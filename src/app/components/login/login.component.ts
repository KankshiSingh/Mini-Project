import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {

  email = '';
  password = '';
  emailError = signal('');
  passwordError = signal('');

  constructor(
    private router: Router,
    private auth: AuthService
  ) { }

  validate() {
    let isValid = true;
    if (!this.email) {
      this.emailError.set('Email is required');
      isValid = false;
    } else if (!/\S+@\S+\.\S+/.test(this.email)) {
      this.emailError.set('Invalid email format');
      isValid = false;
    } else {
      this.emailError.set('');
    }

    if (!this.password) {
      this.passwordError.set('Password is required');
      isValid = false;
    } else {
      this.passwordError.set('');
    }
    return isValid;
  }

  login() {
    if (!this.validate()) return;
    this.auth.login({ email: this.email, password: this.password }).subscribe({
      next: (res) => {
        if (res && res.success) {
          this.router.navigate(['/dashboard']);
        } else {
          this.passwordError.set("Invalid Credentials");
        }
      },
      error: (err) => {
        console.error("Backend Error:", err);
        this.passwordError.set("Error during login. Make sure the backend server is running.");
      }
    });
  }
}
