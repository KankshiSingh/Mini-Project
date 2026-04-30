import { Component, HostListener, signal, inject, computed } from '@angular/core';
import { RouterLink, RouterLinkActive, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';
import { CartService } from '../../services/cart.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive],
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent {
  authService = inject(AuthService);
  cartService = inject(CartService);
  
  scrolled = signal(false);
  menuOpen = signal(false);
  searchOpen = signal(false);

  // Derived signals from services
  isLoggedIn = this.authService.isLoggedIn;
  currentUser = this.authService.currentUser;
  cartCount = this.cartService.buyCount;

  navLinks = [
    { label: 'Browse',  route: '/view-books', icon: '' },
    { label: 'Add Book',route: '/add-book',   icon: '️' },
    { label: 'Dashboard',route: '/dashboard', icon: '' },
    { label: 'Reviews', route: '/book-reviews', icon: '⭐' },
  ];

  @HostListener('window:scroll')
  onScroll() { this.scrolled.set(window.scrollY > 12); }

  toggleMenu()  { this.menuOpen.update(v => !v); }
  toggleSearch(){ this.searchOpen.update(v => !v); }
  closeMenu()   { this.menuOpen.set(false); }

  logout() {
    this.authService.logout();
  }
}
