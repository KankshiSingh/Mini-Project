import { Injectable, signal, computed, inject, effect } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { AuthService } from './auth.service';

export interface CartItem {
  book: any;
  qty: number;
}

@Injectable({
  providedIn: 'root'
})
export class CartService {
  private http = inject(HttpClient);
  private auth = inject(AuthService);
  private apiUrl = 'http://localhost:8080/api/cart';

  // Signals for global reactivity
  buyCart = signal<any[]>([]);
  sellCart = signal<any[]>([]); // Keep it local for frontend drafts (if needed)

  // Computed values
  buyCount = computed(() => this.buyCart().reduce((acc, item) => acc + item.qty, 0));
  buyTotal = computed(() => this.buyCart().reduce((acc, item) => acc + (item.book.price * item.qty), 0));

  constructor() {
    effect(() => {
      const user = this.auth.currentUser();
      if (user && (user.id || user._id)) {
        this.fetchCart();
      } else {
        this.buyCart.set([]);
      }
    });
  }

  fetchCart() {
    const userId = this.auth.currentUser()?.id;
    if (!userId) return;
    this.http.get<any>(`${this.apiUrl}/${userId}`).subscribe(cart => {
      if (cart && cart.buyItems) {
        // Transform the backend CartItem to the frontend's expected {book: {...}, qty: N} shape
        const mappedItems = cart.buyItems.map((item: any) => ({
          book: {
            id: item.bookId,
            title: item.title,
            author: item.author,
            price: item.price,
            type: item.type,
            category: item.category,
            condition: item.condition
          },
          qty: item.quantity,
          bookId: item.bookId
        }));
        this.buyCart.set(mappedItems);
      }
    });
  }

  addToBuyCart(book: any) {
    const userId = this.auth.currentUser()?.id;
    if (!userId) {
      alert("Please login first to add items to cart.");
      return;
    }
    
    this.http.post(`${this.apiUrl}/${userId}/add`, { bookId: book.id || book._id, quantity: 1 }).subscribe({
      next: () => this.fetchCart(),
      error: () => alert("Failed to add to cart.")
    });
  }

  increaseQty(bookId: string) {
    const userId = this.auth.currentUser()?.id;
    if (!userId) return;
    const item = this.buyCart().find(i => i.bookId === bookId);
    if (!item) return;

    this.http.put(`${this.apiUrl}/${userId}/update`, { bookId, quantity: item.qty + 1 }).subscribe(() => this.fetchCart());
  }

  decreaseQty(bookId: string) {
    const userId = this.auth.currentUser()?.id;
    if (!userId) return;
    const item = this.buyCart().find(i => i.bookId === bookId);
    if (!item) return;

    if (item.qty > 1) {
      this.http.put(`${this.apiUrl}/${userId}/update`, { bookId, quantity: item.qty - 1 }).subscribe(() => this.fetchCart());
    } else {
      this.removeBuyItem(bookId);
    }
  }

  removeBuyItem(bookId: string) {
    const userId = this.auth.currentUser()?.id;
    if (!userId) return;
    this.http.delete(`${this.apiUrl}/${userId}/remove/${bookId}`).subscribe(() => this.fetchCart());
  }

  // Local sell cart operations (if still used)
  addToSellCart(book: any) {
    this.sellCart.update(items => {
      const existing = items.find(b => b.title === book.title);
      if (existing) {
        return items.map(b => b.title === book.title ? { ...b, quantity: b.quantity + 1 } : b);
      }
      return [...items, { ...book, quantity: 1 }];
    });
  }
  removeSellItem(bookTitle: string) {
    this.sellCart.update(items => items.filter(b => b.title !== bookTitle));
  }
}