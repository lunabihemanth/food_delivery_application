import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-coupons',
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './coupons.html',
  styleUrl: './coupons.css'
})
export class Coupons implements OnInit {

  coupons: any[] = [];
  copiedCode = '';

  constructor(private router: Router) {}

  ngOnInit() {
    // Dummy data — will replace with API later
    this.coupons = [
      { couponId: 1, couponCode: 'SAVE20', discountAmount: 20, expiryDate: '2026-06-30', description: '20% off on all orders' },
      { couponId: 2, couponCode: 'FLAT50', discountAmount: 50, expiryDate: '2026-08-31', description: 'Flat ₹50 off on orders above ₹200' },
      { couponId: 3, couponCode: 'FREESHIP', discountAmount: 5, expiryDate: '2026-12-31', description: 'Free delivery on your order' },
      { couponId: 4, couponCode: 'HALLOWEEN', discountAmount: 30, expiryDate: '2026-10-31', description: '30% off special offer' },
      { couponId: 5, couponCode: 'NEWUSER', discountAmount: 15, expiryDate: '2024-12-31', description: '15% off for new users' },
    ];
  }

  isExpired(date: string): boolean {
    return new Date(date) < new Date();
  }

  copyCode(code: string) {
    navigator.clipboard.writeText(code);
    this.copiedCode = code;
    setTimeout(() => {
      this.copiedCode = '';
    }, 2000);
  }

  useInCart(code: string) {
    localStorage.setItem('appliedCoupon', code);
    this.router.navigate(['/customer/cart']);
  }

  validCoupons() {
    return this.coupons.filter(c => !this.isExpired(c.expiryDate));
  }

  expiredCoupons() {
    return this.coupons.filter(c => this.isExpired(c.expiryDate));
  }
}