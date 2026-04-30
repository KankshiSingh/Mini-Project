import { Component, signal, computed, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { ToastService } from '../../services/toast.service';
import { CartService } from '../../services/cart.service';

// ─── Types ────────────────────────────────────────────────────
export interface Book {
  id?: string;
  _id?: string;
  title: string;
  author: string;
  genre: string;
  price: number;
  originalPrice: number;
  rating: number;
  reviews: number;
  condition: 'Like New' | 'Good' | 'Fair' | string;
  language: string;
  pages: number;
  year: number;
  description: string;
  coverColor: string;
  coverAccent: string;
  emoji: string;
  imageUrl?: string;
  tags: string[];
  bestseller?: boolean;
  wishlist?: boolean;
  cartQty?: number;
}

const GENRES = ['All', 'Fiction', 'Self-Help', 'Finance', 'History', 'Science', 'Biography', 'Thriller', 'Fantasy', 'Romance', 'Productivity'];
const CONDITIONS = ['All', 'Like New', 'Good', 'Fair'];
const SORT_OPTIONS = [
  { value: 'popular', label: 'Most Popular' },
  { value: 'rating', label: 'Highest Rated' },
  { value: 'price-asc', label: 'Price: Low → High' },
  { value: 'price-desc', label: 'Price: High → Low' },
  { value: 'newest', label: 'Newest First' },
];

import { BookService } from '../../services/book.service';

@Component({
  selector: 'app-view-books',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './view-books.component.html',
  styleUrls: ['./view-books.component.css'],
})
export class ViewBooksComponent implements OnInit {
  Math = Math;
  private cartService = inject(CartService);
  private bookService = inject(BookService);
  private toast = inject(ToastService);

  // ── State ────────────────────────────────────────────────
  books = signal<any[]>([]);
  cart = this.cartService.buyCart;
  genres = GENRES;
  conditions = CONDITIONS;
  sortOptions = SORT_OPTIONS;

  searchQuery = signal('');
  selectedGenre = signal('All');
  selectedCond = signal('All');
  minPrice = signal(0);
  maxPrice = signal(1000);
  minRating = signal(0);
  sortBy = signal('popular');
  viewMode = signal<'grid' | 'list'>('grid');
  showCartPanel = signal(false);
  quickViewBook = signal<Book | null>(null);
  currentPage = signal(1);
  pageSize = 20;
  filtersOpen = signal(false);

  // ── Derived ──────────────────────────────────────────────
  filtered = computed(() => {
    const q = this.searchQuery().toLowerCase().trim();
    const g = this.selectedGenre();
    const c = this.selectedCond();
    const minP = this.minPrice();
    const maxP = this.maxPrice();
    const minR = this.minRating();
    const sort = this.sortBy();

    let list = this.books().filter(b => {
      if (q && !b.title.toLowerCase().includes(q) &&
        !b.author.toLowerCase().includes(q) &&
        !(b.tags && b.tags.some((t: string) => t.toLowerCase().includes(q)))) return false;
      if (g !== 'All' && b.genre !== g) return false;
      if (c !== 'All' && b.condition !== c) return false;
      if (b.price < minP || b.price > maxP) return false;
      if (b.rating < minR) return false;
      return true;
    });

    list = [...list].sort((a, b) => {
      switch (sort) {
        case 'popular': return b.reviews - a.reviews;
        case 'rating': return b.rating - a.rating;
        case 'price-asc': return a.price - b.price;
        case 'price-desc': return b.price - a.price;
        case 'newest': return b.year - a.year;
        default: return 0;
      }
    });

    return list;
  });

  paged = computed(() => {
    const start = (this.currentPage() - 1) * this.pageSize;
    return this.filtered().slice(start, start + this.pageSize);
  });

  totalPages = computed(() => Math.ceil(this.filtered().length / this.pageSize));

  cartCount = this.cartService.buyCount;
  cartTotal = this.cartService.buyTotal;

  genreCounts = computed(() => {
    const map: Record<string, number> = {};
    this.books().forEach(b => { map[b.genre] = (map[b.genre] ?? 0) + 1; });
    return map;
  });

  formatPrice(n: number) { return n.toLocaleString('en-IN'); }
  formatNum(n: number) { return n.toLocaleString('en-IN'); }
  getGenreCount(g: string) { return this.genreCounts()[g] ?? 0; }

  ngOnInit() {
    this.bookService.getAvailableBooks().subscribe(data => {
      // Map to ensure UI doesn't crash on missing properties
      const mapped = data.map(b => ({
        ...b,
        id: b.id || b._id,
        genre: b.category || b.genre || 'Other',
        rating: b.rating || (Math.random() * 2 + 3).toFixed(1), // Random 3.0 to 5.0
        reviews: b.reviews || Math.floor(Math.random() * 1000),
        originalPrice: b.originalPrice || (b.price * 1.5),
        tags: b.tags || [],
        coverColor: b.coverColor || '#ccc',
        imageUrl: b.imageUrl,
        emoji: b.emoji || '📖',
        condition: b.condition || 'Good',
        year: b.year || 2024
      }));
      this.books.set(mapped);
    });
  }

  // ── Helpers ──────────────────────────────────────────────
  discount(b: Book) { return Math.round((1 - b.price / b.originalPrice) * 100); }
  stars(r: number) { return Array.from({ length: 5 }, (_, i) => i < Math.floor(r)); }
  halfStar(r: number) { return r % 1 >= 0.5; }

  condClass(c: string) {
    return c === 'Like New' ? 'badge-success' : c === 'Good' ? 'badge-primary' : 'badge-warning';
  }

  // ── Filtering ────────────────────────────────────────────
  setGenre(g: string) { this.selectedGenre.set(g); this.currentPage.set(1); }
  clearFilters() {
    this.searchQuery.set(''); this.selectedGenre.set('All');
    this.selectedCond.set('All'); this.minPrice.set(0);
    this.maxPrice.set(500); this.minRating.set(0);
    this.sortBy.set('popular'); this.currentPage.set(1);
  }

  // ── Pagination ───────────────────────────────────────────
  pages() {
    const total = this.totalPages();
    const cur = this.currentPage();
    const pages: (number | '...')[] = [];
    if (total <= 7) { for (let i = 1; i <= total; i++) pages.push(i); }
    else {
      pages.push(1);
      if (cur > 3) pages.push('...');
      for (let i = Math.max(2, cur - 1); i <= Math.min(total - 1, cur + 1); i++) pages.push(i);
      if (cur < total - 2) pages.push('...');
      pages.push(total);
    }
    return pages;
  }
  goPage(p: number | '...') {
    if (typeof p === 'number') { this.currentPage.set(p); window.scrollTo({ top: 0, behavior: 'smooth' }); }
  }

  // ── Cart ─────────────────────────────────────────────────
  addToCart(b: Book, showPanel = false) {
    this.cartService.addToBuyCart(b);
    this.toast.success('Added to cart!', `"${b.title}" added successfully.`);
    if (showPanel) this.showCartPanel.set(true);
    // mark on book
    this.books.update(list => list.map(bk => bk.id === b.id ? { ...bk, cartQty: (bk.cartQty ?? 0) + 1 } : bk));
  }

  removeFromCart(id: number) {
    const book = this.books().find(b => b.id === id);
    if (book) this.cartService.removeBuyItem(book.title);
    this.books.update(list => list.map(b => b.id === id ? { ...b, cartQty: 0 } : b));
  }

  changeQty(id: number, d: number) {
    const book = this.books().find(b => b.id === id);
    if (!book) return;

    if (d > 0) this.cartService.increaseQty(book.title);
    else this.cartService.decreaseQty(book.title);
  }

  inCart(id: number) { return this.cart().some(i => i.book.id === id); }

  // ── Wishlist ─────────────────────────────────────────────
  toggleWishlist(b: Book) {
    this.books.update(list => list.map(bk =>
      bk.id === b.id ? { ...bk, wishlist: !bk.wishlist } : bk
    ));
    const msg = b.wishlist ? 'Removed from wishlist' : 'Added to wishlist ️';
    this.toast.info(msg);
  }

  // ── Quick View ───────────────────────────────────────────
  openQuickView(b: Book) { this.quickViewBook.set(b); }
  closeQuickView() { this.quickViewBook.set(null); }
}