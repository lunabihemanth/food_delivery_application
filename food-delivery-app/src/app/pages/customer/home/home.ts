import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-home',
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './home.html',
  styleUrl: './home.css'
})
export class Home implements OnInit {

  userName = '';
  search = '';
  restaurants: any[] = [];

  constructor(private router: Router) {}

  ngOnInit() {
    const user = JSON.parse(localStorage.getItem('user') || '{}');
    this.userName = user.name || 'User';

    // Dummy data — will replace with API later
    this.restaurants = [
      { restaurantId: 1, restaurantName: 'Tasty Bites', restaurantAddress: '123 Main St', restaurantPhone: '9876543210', rating: 4.5 },
      { restaurantId: 2, restaurantName: 'Sizzling Grill', restaurantAddress: '456 Elm St', restaurantPhone: '9876543211', rating: 4.2 },
      { restaurantId: 3, restaurantName: 'Spice Avenue', restaurantAddress: '789 Oak St', restaurantPhone: '9876543212', rating: 4.8 },
      { restaurantId: 4, restaurantName: 'Pizza Palace', restaurantAddress: '890 Oak St', restaurantPhone: '9876543213', rating: 4.1 },
      { restaurantId: 5, restaurantName: 'Burger Barn', restaurantAddress: '123 Cedar St', restaurantPhone: '9876543214', rating: 4.6 },
      { restaurantId: 6, restaurantName: 'Chinese Garden', restaurantAddress: '456 Elm St', restaurantPhone: '9876543215', rating: 4.3 },
    ];
  }

  filteredRestaurants() {
    return this.restaurants.filter(r =>
      r.restaurantName.toLowerCase().includes(this.search.toLowerCase())
    );
  }

  goToMenu(restaurant: any) {
    localStorage.setItem('selectedRestaurant', JSON.stringify(restaurant));
    this.router.navigate(['/customer/menu', restaurant.restaurantId]);
  }

  getInitial(name: string) {
    return name?.charAt(0).toUpperCase() || 'R';
  }
}
