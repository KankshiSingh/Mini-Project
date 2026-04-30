import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { BookService } from '../../services/book.service';
import { AuthService } from '../../services/auth.service';
import { ToastService } from '../../services/toast.service';
import { CartService } from '../../services/cart.service';

@Component({
  selector: 'app-add-book',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './add-book.component.html',
  styleUrls: ['./add-book.component.css']
})
export class AddBookComponent {
  constructor(private bookService: BookService, private auth: AuthService, private toast: ToastService, private cartService: CartService) { }

  bookTitle = '';
  author = '';
  condition = '';
  price = 0;
  type = '';

  selectType(t: string) {
    this.type = t;

    if (t === 'donate') {
      this.price = 0;
    }
  }

  estimatePrice() {
    if (this.type === 'sell') {
      if (this.condition === 'new') {
        this.price = 400;
      }
      else if (this.condition === 'good') {
        this.price = 250;
      }
      else if (this.condition === 'old') {
        this.price = 120;
      }
    }
  }

  submitBook() {
    const user = this.auth.currentUser();
    if (!user) {
      alert("Please login to add a book.");
      return;
    }

    const book = {
      title: this.bookTitle,
      author: this.author,
      condition: this.condition,
      price: this.price,
      type: this.type.toUpperCase(),
      userId: user.id || user._id,
      ownerName: user.name,
      ownerEmail: user.email
    };

    this.bookService.addBook(book).subscribe({
      next: (res) => {
        this.toast.success("Book successfully added/listed!");
        
        // Add to local sell cart to show it in the cart section
        this.cartService.addToSellCart(res);

        // Reset form
        this.bookTitle = '';
        this.author = '';
        this.condition = '';
        this.price = 0;
        this.type = '';
      },
      error: (err) => {
        this.toast.error("Failed to list book. " + (err.error?.message || ''));
      }
    });

  }
}