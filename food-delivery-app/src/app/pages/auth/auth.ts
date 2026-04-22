import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-auth',
  imports: [CommonModule, FormsModule],
  templateUrl: './auth.html',
  styleUrl: './auth.css'
})
export class Auth implements OnInit {

  isLogin = true;
  role = 'customer';
  errorMsg = '';

  form = {
    name: '',
    email: '',
    phone: '',
    password: ''
  };

  constructor(private route: ActivatedRoute, private router: Router) {}

  ngOnInit() {
    this.role = this.route.snapshot.paramMap.get('role') || 'customer';
    localStorage.setItem('role', this.role);
  }

  getRoleEmoji() {
    if (this.role === 'customer') return '👤';
    if (this.role === 'restaurant') return '🍽️';
    return '🚗';
  }

  submit() {
    if (!this.form.email || !this.form.password) {
      this.errorMsg = 'Please fill all required fields';
      return;
    }
    if (!this.isLogin && !this.form.name) {
      this.errorMsg = 'Please enter your name';
      return;
    }
    this.errorMsg = '';

    const user = {
      id: 1,
      name: this.isLogin ? this.form.email.split('@')[0] : this.form.name,
      email: this.form.email,
      phone: this.form.phone,
      role: this.role
    };

    localStorage.setItem('user', JSON.stringify(user));
    localStorage.setItem('userId', '1');

    if (this.role === 'customer') {
      this.router.navigate(['/customer/home']);
    } else if (this.role === 'restaurant') {
      this.router.navigate(['/restaurant/dashboard']);
    } else {
      this.router.navigate(['/driver/dashboard']);
    }
  }
}
