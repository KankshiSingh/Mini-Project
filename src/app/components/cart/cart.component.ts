import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CartService } from '../../services/cart.service';
import { ToastService } from '../../services/toast.service';

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './cart.component.html',
  styleUrls: ['./cart.component.css']
})
export class CartComponent {
  cartService = inject(CartService);

  toast = inject(ToastService);

  // Expose signals to template
  buyCart = this.cartService.buyCart;
  sellCart = this.cartService.sellCart;
  buyTotal = this.cartService.buyTotal;

  increaseQty(item: any) {
    this.cartService.increaseQty(item.bookId);
  }

  decreaseQty(item: any) {
    this.cartService.decreaseQty(item.bookId);
  }

  removeBuy(item: any) {
    this.cartService.removeBuyItem(item.bookId);
  }

  removeSell(book: any) {
    this.cartService.removeSellItem(book.title);
  }

  getSellTotal() {
    return this.sellCart().reduce((total, item) => total + item.price * item.quantity, 0);
  }

  checkout() {
    const userId = this.cartService['auth'].currentUser()?.id;
    if (!userId) {
      this.toast.error("Please login to checkout.");
      return;
    }
    
    // We can assume OrderService is injected or we inject it here.
    // However since the backend OrderController maps from `{ userId }` we can just send an API request
    this.cartService['http'].post(`http://localhost:8080/api/orders/${userId}`, {}).subscribe({
      next: (res) => {
        this.toast.success("Order packed successfully!");
        this.cartService.fetchCart(); // This will clear cart in UI since backend clears cart after order
      },
      error: (err) => {
        this.toast.error("Failed to create order.");
      }
    });
  }
}
