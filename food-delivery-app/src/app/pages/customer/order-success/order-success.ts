import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-order-success',
  imports: [CommonModule, RouterLink],
  templateUrl: './order-success.html',
  styleUrl: './order-success.css'
})
export class OrderSuccess implements OnInit {

  orderId: any;
  orderSteps = ['Placed', 'Preparing', 'On the way', 'Delivered'];

  constructor(private router: Router) {}

  ngOnInit() {
    this.orderId = localStorage.getItem('lastOrderId') || '1001';
  }

  goToOrders() {
    this.router.navigate(['/customer/orders']);
  }

  goToHome() {
    this.router.navigate(['/customer/home']);
  }
}

