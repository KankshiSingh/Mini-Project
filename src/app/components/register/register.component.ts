import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent {

  user = {
    name: '',
    age: '',
    phone: '',
    email: '',
    password: ''
  };

  errors = {
    name: signal(''),
    age: signal(''),
    phone: signal(''),
    email: signal(''),
    password: signal('')
  };

  constructor(
    private router: Router,
    private auth: AuthService
  ) { }

  validate() {
    let isValid = true;

    // Name
    if (!this.user.name.trim()) {
      this.errors.name.set('Name is required');
      isValid = false;
    } else {
      this.errors.name.set('');
    }

    // Age
    if (!this.user.age) {
      this.errors.age.set('Age is required');
      isValid = false;
    } else if (isNaN(Number(this.user.age)) || Number(this.user.age) < 1 || Number(this.user.age) > 120) {
      this.errors.age.set('Enter a valid age (1-120)');
      isValid = false;
    } else {
      this.errors.age.set('');
    }

    // Phone
    if (!this.user.phone) {
      this.errors.phone.set('Phone is required');
      isValid = false;
    } else if (!/^\d{10}$/.test(this.user.phone)) {
      this.errors.phone.set('Phone must be 10 digits');
      isValid = false;
    } else {
      this.errors.phone.set('');
    }

    // Email
    if (!this.user.email) {
      this.errors.email.set('Email is required');
      isValid = false;
    } else if (!/\S+@\S+\.\S+/.test(this.user.email)) {
      this.errors.email.set('Invalid email format');
      isValid = false;
    } else {
      this.errors.email.set('');
    }

    // Password
    if (!this.user.password) {
      this.errors.password.set('Password is required');
      isValid = false;
    } else if (this.user.password.length < 6) {
      this.errors.password.set('Min 6 characters');
      isValid = false;
    } else {
      this.errors.password.set('');
    }

    return isValid;
  }

  register() {
    if (!this.validate()) return;

    this.auth.register(this.user).subscribe({
      next: (res) => {
        if (res && res.success) {
          alert("Registration Successful");
          this.router.navigate(['/dashboard']);
        } else {
          alert("Registration Failed: " + (res.message || 'Unknown error'));
        }
      },
      error: (err) => {
        console.error("Backend Error:", err);
        alert("Registration Failed: Could not connect to backend or backend returned an error.");
      }
    });
  }
}
