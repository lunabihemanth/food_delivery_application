import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './login.html',
  styleUrls: ['./login.css']
})
export class LoginComponent {

  // Input fields
  username: string = '';
  password: string = '';
  errorMsg: string = '';

  constructor(private router: Router) {}

  // 🔐 Dummy users (for demo)
  users = [
    { username: 'annie', password: '123', role: 'annie' },
    { username: 'jeevitha', password: '123', role: 'jeevitha' },
    { username: 'kisol', password: '123', role: 'kisol' },
    { username: 'thenmozhi', password: '123', role: 'thenmozhi' },
    { username: 'hemanth', password: '123', role: 'hemanth' }
  ];

  // 🔄 Back button
  goBack() {
    this.router.navigate(['/home']);
  }

  // 🚀 Login logic
  onLogin() {

    // Check empty fields
    if (!this.username || !this.password) {
      this.errorMsg = 'Please enter username and password';
      return;
    }

    // Find user
    const user = this.users.find(
      u => u.username === this.username && u.password === this.password
    );

    // Invalid login
    if (!user) {
      this.errorMsg = 'Invalid credentials';
      return;
    }

    // Clear error
    this.errorMsg = '';

    // (Optional) store role
    localStorage.setItem('userRole', user.role);

    // 🔀 Role-based navigation
    if (user.role === 'annie') {
      this.router.navigate(['/annie']);
    } 
    else if (user.role === 'jeevitha') {
      this.router.navigate(['/jeevitha']);
    } 
    else if (user.role === 'kisol') {
      this.router.navigate(['/kisol']);
    } 
    else if (user.role === 'thenmozhi') {
      this.router.navigate(['/thenmozhi']);
    }
    else if (user.role === 'hemanth') {
      this.router.navigate(['/hemanth']);
    }
  }
}