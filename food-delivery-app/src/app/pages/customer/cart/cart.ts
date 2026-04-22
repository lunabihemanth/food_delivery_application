import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-cart',
  imports: [CommonModule, FormsModule],
  templateUrl: './cart.html',
  styleUrl: './cart.css'
})
export class Cart implements OnInit {

  cart: any[] = [];
  couponCode = '';
  couponMsg = '';
  couponApplied = false;
  discount = 0;

  constructor(private router: Router) {}

  ngOnInit() {
    this.cart = JSON.parse(localStorage.getItem('cart') || '[]');
  }

  totalItems() {
    return this.cart.reduce((s, c) => s + c.quantity, 0);
  }

  subtotal() {
    return this.cart.reduce((s, c) => s + (c.price * c.quantity), 0);
  }

  finalTotal() {
    return this.subtotal() - this.discount;
  }

  increase(item: any) {
    item.quantity++;
    this.saveCart();
  }

  decrease(item: any) {
    item.quantity--;
    if (item.quantity === 0) {
      this.cart = this.cart.filter(c => c.itemId !== item.itemId);
    }
    this.saveCart();
  }

  removeItem(item: any) {
    this.cart = this.cart.filter(c => c.itemId !== item.itemId);
    this.saveCart();
  }

  clearCart() {
    if (confirm('Clear all items from cart?')) {
      this.cart = [];
      this.discount = 0;
      this.couponApplied = false;
      this.couponMsg = '';
      this.saveCart();
    }
  }

  saveCart() {
    localStorage.setItem('cart', JSON.stringify(this.cart));
  }

  applyCoupon() {
    if (!this.couponCode) {
      this.couponMsg = 'Please enter a coupon code';
      return;
    }
    // Dummy coupon check — will connect to API later
    if (this.couponCode.toUpperCase() === 'SAVE20') {
      this.discount = Math.round(this.subtotal() * 0.2);
      this.couponApplied = true;
      this.couponMsg = '20% discount applied! You save ₹' + this.discount;
    } else if (this.couponCode.toUpperCase() === 'FLAT50') {
      this.discount = 50;
      this.couponApplied = true;
      this.couponMsg = 'Flat ₹50 discount applied!';
    } else {
      this.couponApplied = false;
      this.couponMsg = 'Invalid coupon code';
      this.discount = 0;
    }
  }

  removeCoupon() {
    this.couponCode = '';
    this.couponMsg = '';
    this.couponApplied = false;
    this.discount = 0;
  }

  checkout() {
    this.router.navigate(['/customer/checkout']);
  }

  goBack() {
    this.router.navigate(['/customer/home']);
  }
}
