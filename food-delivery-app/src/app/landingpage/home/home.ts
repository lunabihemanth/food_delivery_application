import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-home',
  standalone: true,
  templateUrl: './home.html',
  styleUrls: ['./home.css']
})
export class HomeComponent {

  constructor(private router: Router) {}

  teamMembers = [
    { name: 'Annie Rufina C', role: 'Customer' },
    { name: 'Jeevitha E', role: 'Delivery' },
    { name: 'Kisol Shamilisha', role: 'Restaurant' },
    { name: 'Thenmozhi S', role: 'Customer' },
    { name: 'Hemanth Karthick', role: 'Delivery' }
  ];

  goToLogin() {
    this.router.navigate(['/login']);
  }
}