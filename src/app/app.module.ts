import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { RouterModule } from '@angular/router';

import { AppComponent } from './app.component';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { AddBookComponent } from './components/add-book/add-book.component';
import { ViewBooksComponent } from './components/view-books/view-books.component';

@NgModule({
  imports: [
    BrowserModule,
    AppComponent,
    LoginComponent,
    RegisterComponent,
    DashboardComponent,
    AddBookComponent,
    ViewBooksComponent,
    RouterModule.forRoot([
      { path: '', component: LoginComponent },
      { path: 'register', component: RegisterComponent },
      { path: 'dashboard', component: DashboardComponent },
      { path: 'add-book', component: AddBookComponent },
      { path: 'view-books', component: ViewBooksComponent }
    ])
  ],
  bootstrap: [AppComponent]
})
export class AppModule {}