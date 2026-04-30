import { bootstrapApplication } from '@angular/platform-browser';
import { provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { AppComponent } from './app/app.component';
import { LoginComponent } from './app/components/login/login.component';
import { RegisterComponent } from './app/components/register/register.component';
import { DashboardComponent } from './app/components/dashboard/dashboard.component';
import { AddBookComponent } from './app/components/add-book/add-book.component';
import { ViewBooksComponent } from './app/components/view-books/view-books.component';
import { ProfileComponent } from './app/components/profile/profile.component';
import { CartComponent } from './app/components/cart/cart.component';
import { BookReviewsComponent } from './app/components/book-reviews/book-reviews.component';
import { ReviewDetailsComponent } from './app/components/review-details/review-details.component';
bootstrapApplication(AppComponent, {
  providers: [
    provideHttpClient(),
    provideRouter([
      { path: '', component: LoginComponent },
      { path: 'login', component: LoginComponent },
      { path: 'register', component: RegisterComponent },
      { path: 'dashboard', component: DashboardComponent },
      { path: 'add-book', component: AddBookComponent },
      { path: 'view-books', component: ViewBooksComponent },
      { path: 'profile', component: ProfileComponent },
      { path: 'cart', component: CartComponent },
      { path: 'book-reviews', component: BookReviewsComponent },
      { path: 'book-reviews/:bookId', component: ReviewDetailsComponent }
    ])
  ]
}).catch(err => console.error(err));
