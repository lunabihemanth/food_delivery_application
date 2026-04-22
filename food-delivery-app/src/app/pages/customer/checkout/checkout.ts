import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-checkout',
  imports: [CommonModule, FormsModule],
  templateUrl: './checkout.html',
  styleUrl: './checkout.css'
})
export class Checkout implements OnInit {

  cart: any[] = [];
  addresses: any[] = [];
  selectedAddress: any = null;
  showAddAddress = false;
  selectedPayment = 'Cash on Delivery';
  paymentMethods = ['Cash on Delivery', 'UPI', 'Credit/Debit Card'];

  newAddress = {
    addressLine1: '',
    city: '',
    state: '',
    postalCode: ''
  };

  constructor(private router: Router) {}

  ngOnInit() {
    this.cart = JSON.parse(localStorage.getItem('cart') || '[]');

    // Dummy addresses
    this.addresses = [
      {
        addressId: 1,
        addressLine1: '123 Elm Street',
        city: 'Chennai',
        state: 'Tamil Nadu',
        postalCode: '600001'
      }
    ];

    if (this.addresses.length > 0) {
      this.selectedAddress = this.addresses[0];
    }
  }

  total() {
    return this.cart.reduce((s, c) => s + (c.price * c.quantity), 0);
  }

  totalItems() {
    return this.cart.reduce((s, c) => s + c.quantity, 0);
  }

  addAddress() {
    if (!this.newAddress.addressLine1 || !this.newAddress.city) {
      alert('Please fill address details');
      return;
    }
    this.addresses.push({
      ...this.newAddress,
      addressId: Date.now()
    });
    this.selectedAddress = this.addresses[this.addresses.length - 1];
    this.showAddAddress = false;
    this.newAddress = { addressLine1: '', city: '', state: '', postalCode: '' };
  }

  placeOrder() {
    if (!this.selectedAddress) {
      alert('Please select a delivery address');
      return;
    }
    // Will connect to API later
    const orderId = Math.floor(Math.random() * 9000) + 1000;
    localStorage.setItem('lastOrderId', orderId.toString());
    localStorage.removeItem('cart');
    this.router.navigate(['/customer/order-success']);
  }

  goBack() {
    this.router.navigate(['/customer/cart']);
  }
}