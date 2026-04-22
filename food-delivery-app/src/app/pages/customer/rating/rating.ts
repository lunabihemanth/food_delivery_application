import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-rating',
  imports: [CommonModule, FormsModule],
  templateUrl: './rating.html',
  styleUrl: './rating.css'
})
export class Rating implements OnInit {

  orderId: any;
  rating = 0;
  review = '';
  stars = [1, 2, 3, 4, 5];
  hoverStar = 0;

  constructor(private route: ActivatedRoute, private router: Router) {}

  ngOnInit() {
    this.orderId = this.route.snapshot.paramMap.get('orderId');
  }

  getRatingLabel() {
    const labels: any = {
      0: 'Tap to rate',
      1: '😞 Poor',
      2: '😐 Fair',
      3: '🙂 Good',
      4: '😊 Great',
      5: '🤩 Excellent!'
    };
    return labels[this.rating];
  }

  submitRating() {
    if (this.rating === 0) {
      alert('Please select a rating');
      return;
    }
    // Will connect to API later
    alert('Thank you for your feedback!');
    this.router.navigate(['/customer/orders']);
  }

  goBack() {
    this.router.navigate(['/customer/orders']);
  }
}

