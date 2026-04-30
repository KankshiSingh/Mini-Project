import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { RouterModule } from '@angular/router';
import { FormsModule, ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { BookService } from '../../services/book.service';
import { ToastService } from '../../services/toast.service';

export interface Review {
  id: string;
  bookId: string | number;
  author: string;
  avatar: string;
  rating: number;
  title: string;
  body: string;
  date: Date;
  helpful: number;
  verified: boolean;
}

// Seed reviews for demo purposes
const SEED_REVIEWS: Review[] = [
  { id:'r1', bookId:'1', author:'Priya Sharma', avatar:'PS', rating:5, title:'Changed my perspective completely', body:'This book is a masterpiece. The writing style is beautiful and the story stays with you long after you finish.', date: new Date('2025-03-12'), helpful:24, verified:true },
  { id:'r2', bookId:'1', author:'Arjun Mehta', avatar:'AM', rating:4, title:'A must-read classic', body:'Beautifully written with deep philosophical insights. A little slow in the middle but the ending is worth it.', date: new Date('2025-02-28'), helpful:17, verified:true },
  { id:'r3', bookId:'2', author:'Kavya Reddy', avatar:'KR', rating:5, title:'Life-changing habits framework', body:'The most practical self-improvement book I\'ve read. The 1% rule is something I now apply every single day.', date: new Date('2025-03-20'), helpful:41, verified:true },
  { id:'r4', bookId:'3', author:'Rohan Gupta', avatar:'RG', rating:5, title:'Best finance book for Indians', body:'Explains money concepts in a way that\'s relatable to the Indian context. Every young professional should read this.', date: new Date('2025-01-15'), helpful:33, verified:false },
];

const GENRE_COLORS: Record<string, { bg: string; accent: string; emoji: string }> = {
  Fiction:    { bg:'#7c3aed', accent:'#ddd6fe', emoji:'📖' },
  'Self-Help':{ bg:'#065f46', accent:'#a7f3d0', emoji:'⚡' },
  Finance:    { bg:'#b45309', accent:'#fde68a', emoji:'💰' },
  History:    { bg:'#1e3a5f', accent:'#93c5fd', emoji:'🏛️' },
  Science:    { bg:'#0f172a', accent:'#818cf8', emoji:'🔬' },
  Biography:  { bg:'#374151', accent:'#d1d5db', emoji:'🎭' },
  Thriller:   { bg:'#1f2937', accent:'#9ca3af', emoji:'🔪' },
  Fantasy:    { bg:'#7c2d12', accent:'#fed7aa', emoji:'🐉' },
  Romance:    { bg:'#be185d', accent:'#fbcfe8', emoji:'💌' },
  Productivity:{ bg:'#0f766e', accent:'#99f6e4', emoji:'🎯' },
};

const SORT_OPTIONS = [
  { value: 'recent',   label: 'Most Recent' },
  { value: 'rating',   label: 'Highest Rated' },
  { value: 'reviews',  label: 'Most Reviewed' },
  { value: 'az',       label: 'A → Z' },
];

@Component({
  selector: 'app-book-reviews',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, ReactiveFormsModule],
  templateUrl: './book-reviews.component.html',
  styleUrls: ['./book-reviews.component.css'],
})
export class BookReviewsComponent implements OnInit {
  private bookService = inject(BookService);
  private http        = inject(HttpClient);
  private toast       = inject(ToastService);
  private fb          = inject(FormBuilder);

  // ── Data ─────────────────────────────────────────────────
  books    = signal<any[]>([]);
  reviews  = signal<Review[]>([]);
  loading  = signal(true);
  
  // Pagination
  currentPage  = signal(1);
  itemsPerPage = 12;
  Math = Math;

  // ── UI state ─────────────────────────────────────────────
  searchQuery    = signal('');
  selectedGenre  = signal('All');
  sortBy         = signal('recent');
  activeBookId   = signal<string | number | null>(null);
  showWriteForm  = signal(false);
  hoverRating    = signal(0);
  submitting     = signal(false);

  sortOptions = SORT_OPTIONS;
  genres      = signal<string[]>(['All']);

  // ── Review form ──────────────────────────────────────────
  reviewForm = this.fb.group({
    bookId:    ['', Validators.required],
    author:    ['', [Validators.required, Validators.minLength(2)]],
    rating:    [0,  [Validators.required, Validators.min(1), Validators.max(5)]],
    title:     ['', [Validators.required, Validators.minLength(3)]],
    body:      ['', [Validators.required, Validators.minLength(20), Validators.maxLength(1000)]],
  });

  get rf() { return this.reviewForm.controls; }

  // ── Computed ─────────────────────────────────────────────
  filteredBooks = computed(() => {
    const q  = this.searchQuery().toLowerCase().trim();
    const g  = this.selectedGenre();
    let list = this.books();

    if (q) list = list.filter(b =>
      b.title?.toLowerCase().includes(q) || b.author?.toLowerCase().includes(q)
    );
    if (g !== 'All') list = list.filter(b => (b.category || b.genre) === g);

    const sort = this.sortBy();
    return [...list].sort((a, b) => {
      switch(sort) {
        case 'rating':  return (b.averageRating || 0) - (a.averageRating || 0);
        case 'reviews': return (b.reviewCount   || 0) - (a.reviewCount   || 0);
        case 'az':      return (a.title || '').localeCompare(b.title || '');
        default:        return (b.id || 0)             - (a.id || 0);
      }
    });
  });
  
  totalPages = computed(() => Math.ceil(this.filteredBooks().length / this.itemsPerPage));
  
  pagedBooks = computed(() => {
    const start = (this.currentPage() - 1) * this.itemsPerPage;
    return this.filteredBooks().slice(start, start + this.itemsPerPage);
  });
  
  pages = computed(() => {
    const total = this.totalPages();
    return Array.from({ length: total }, (_, i) => i + 1);
  });

  bookReviews = computed(() => {
    const id = this.activeBookId();
    return id ? this.reviews().filter(r => String(r.bookId) === String(id)) : [];
  });

  activeBook = computed(() =>
    this.books().find(b => String(b.id) === String(this.activeBookId())) ?? null
  );

  globalStats = computed(() => {
    const all = this.reviews();
    if (!all.length) return { avg: 0, total: 0, dist: [0,0,0,0,0] };
    const avg  = all.reduce((s, r) => s + r.rating, 0) / all.length;
    const dist = [5,4,3,2,1].map(n => all.filter(r => r.rating === n).length);
    return { avg: +avg.toFixed(1), total: all.length, dist };
  });

  // ── Lifecycle ────────────────────────────────────────────
  ngOnInit() {
    this.bookService.getAvailableBooks().subscribe({
      next: (res: any[]) => {
        // Enrich with cover colour & emoji from local map
        this.books.set(res.map(b => {
          const genre  = b.category || b.genre || 'Fiction';
          const colors = GENRE_COLORS[genre] ?? GENRE_COLORS['Fiction'];
          return {
            ...b,
            coverColor: b.coverColor || colors.bg,
            coverAccent: colors.accent,
            emoji: b.emoji || colors.emoji,
            averageRating: b.averageRating || 0,
            reviewCount:   b.reviewCount   || 0,
          };
        }));
        // Extract genres
        const gs = [...new Set(res.map((b:any) => b.category || b.genre).filter(Boolean))];
        this.genres.set(['All', ...gs]);
        
        // Also fetch all reviews for global stats and previews
        this.fetchReviews();
        
        this.loading.set(false);
      },
      error: () => {
        // Fallback demo books so the page still works
        this.books.set(this.fallbackBooks());
        this.reviews.set(SEED_REVIEWS);
        this.genres.set(['All','Fiction','Self-Help','Finance','History','Science']);
        this.loading.set(false);
      },
    });
  }

  fetchReviews() {
    this.http.get<Review[]>('http://localhost:8080/api/reviews/all').subscribe({
      next: (res) => this.reviews.set(res),
      error: (err) => console.error('Failed to fetch reviews', err)
    });
  }

  // ── Helpers ──────────────────────────────────────────────
  goPage(p: number) {
    if (typeof p === 'number') {
      this.currentPage.set(p);
      window.scrollTo({ top: 0, behavior: 'smooth' });
    }
  }

  coverStyle(book: any) {
    return `linear-gradient(135deg, ${book.coverColor} 0%, ${book.coverColor}cc 100%)`;
  }

  stars(rating: number)  { return Array.from({ length:5 }, (_,i) => i < Math.floor(rating)); }
  halfStar(r: number)    { return r % 1 >= 0.5; }
  discount(b: any)       { return b.originalPrice ? Math.round((1 - b.price/b.originalPrice)*100) : 0; }
  ratingLabel(n: number) { return ['','Poor','Fair','Good','Great','Excellent'][n] ?? ''; }
  pct(count: number)     { return this.globalStats().total ? Math.round(count/this.globalStats().total*100) : 0; }

  openReviews(book: any) {
    this.activeBookId.set(book.id);
    this.showWriteForm.set(false);
    this.reviewForm.patchValue({ bookId: String(book.id) });
    setTimeout(() =>
      document.getElementById('review-detail')?.scrollIntoView({ behavior:'smooth', block:'start' }), 80
    );
  }

  closeDetail() { this.activeBookId.set(null); this.showWriteForm.set(false); }

  openWriteForm() {
    if (!this.activeBookId()) {
      this.toast.info('Select a book first', 'Click on any book card to choose which book to review.');
      return;
    }
    this.showWriteForm.set(true);
    setTimeout(() =>
      document.getElementById('write-review')?.scrollIntoView({ behavior:'smooth', block:'start' }), 80
    );
  }

  setRating(n: number)    { this.reviewForm.patchValue({ rating: n }); this.hoverRating.set(0); }
  setHover(n: number)     { this.hoverRating.set(n); }
  clearHover()            { this.hoverRating.set(0); }
  displayRating()         { return this.hoverRating() || this.rf['rating'].value || 0; }

  markHelpful(review: Review) {
    this.reviews.update(list =>
      list.map(r => r.id === review.id ? { ...r, helpful: r.helpful + 1 } : r)
    );
    this.toast.success('Marked as helpful!');
  }

  async submitReview() {
    if (this.reviewForm.invalid) { this.reviewForm.markAllAsTouched(); return; }
    this.submitting.set(true);
    await new Promise(r => setTimeout(r, 1200));

    const v = this.reviewForm.value;
    const initials = (v.author ?? '?').split(' ').map((w:string) => w[0]).join('').toUpperCase().slice(0,2);

    const newReview: Review = {
      id:       `r-${Date.now()}`,
      bookId:   v.bookId!,
      author:   v.author!,
      avatar:   initials,
      rating:   v.rating!,
      title:    v.title!,
      body:     v.body!,
      date:     new Date(),
      helpful:  0,
      verified: false,
    };

    this.reviews.update(list => [newReview, ...list]);

    // Update book averageRating & reviewCount
    this.books.update(list => list.map(b => {
      if (String(b.id) !== String(v.bookId)) return b;
      const bookRevs = [...this.reviews()].filter(r => String(r.bookId) === String(b.id));
      const avg = bookRevs.reduce((s, r) => s + r.rating, 0) / bookRevs.length;
      return { ...b, averageRating: +avg.toFixed(1), reviewCount: bookRevs.length };
    }));

    this.submitting.set(false);
    this.showWriteForm.set(false);
    this.reviewForm.reset({ bookId: String(v.bookId), rating: 0 });
    this.toast.success('Review posted! 🎉', 'Thank you for sharing your thoughts.');
  }

  cancelReview() {
    this.showWriteForm.set(false);
    this.reviewForm.patchValue({ title:'', body:'', rating:0, author:'' });
  }

  trackById = (_: number, b: any) => b.id;

  private fallbackBooks() {
    return [
      { id:'1', title:'The Alchemist',    author:'Paulo Coelho',   category:'Fiction',   price:120, originalPrice:499, emoji:'⚗️', coverColor:'#7c3aed', coverAccent:'#ddd6fe', averageRating:4.8, reviewCount:28 },
      { id:'2', title:'Atomic Habits',    author:'James Clear',    category:'Self-Help', price:180, originalPrice:699, emoji:'⚡', coverColor:'#065f46', coverAccent:'#a7f3d0', averageRating:4.9, reviewCount:51 },
      { id:'3', title:'Rich Dad Poor Dad',author:'R. Kiyosaki',    category:'Finance',   price:95,  originalPrice:399, emoji:'💰', coverColor:'#b45309', coverAccent:'#fde68a', averageRating:4.7, reviewCount:32 },
      { id:'4', title:'Sapiens',          author:'Y.N. Harari',    category:'History',   price:220, originalPrice:799, emoji:'🦴', coverColor:'#1e3a5f', coverAccent:'#93c5fd', averageRating:4.8, reviewCount:49 },
      { id:'5', title:'Deep Work',        author:'Cal Newport',    category:'Productivity', price:140, originalPrice:549, emoji:'🎯', coverColor:'#0f766e', coverAccent:'#99f6e4', averageRating:4.6, reviewCount:19 },
      { id:'6', title:'1984',             author:'George Orwell',  category:'Fiction',   price:80,  originalPrice:299, emoji:'👁️', coverColor:'#1f2937', coverAccent:'#9ca3af', averageRating:4.9, reviewCount:61 },
    ];
  }
}
