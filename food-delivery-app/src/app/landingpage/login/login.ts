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
  username = '';
  password = '';
  errorMsg = '';
  isLoading = false;

  // Dummy user database – each user MUST log in with their own username
  private users = [
    { username: 'annie',     password: 'annie123', route: '/annie' },
    { username: 'hemanth',   password: 'hemanth123', route: '/hemanth' },
    { username: 'thenmozli', password: 'thenmozli123', route: '/thenmozhi' },
    { username: 'kisol',     password: 'kisol123', route: '/kisol' },
    { username: 'jeevitha',  password: 'jeevitha123', route: '/jeevitha' },
    { username: 'admin',     password: 'admin123', route: '/admin' }
  ];

  constructor(private router: Router) {}

  goBack() {
    this.router.navigate(['/home']);
  }

  onLogin() {
    if (!this.username || !this.password) {
      this.errorMsg = 'Please enter username and password';
      return;
    }

    this.isLoading = true;
    this.errorMsg = '';

    // tiny simulated delay
    setTimeout(() => {
      const user = this.users.find(
        u => u.username === this.username.trim().toLowerCase() &&
             u.password === this.password.trim()
      );

      if (!user) {
        this.errorMsg = 'Invalid username or password';
        this.isLoading = false;
        return;
      }

      // Store dummy credentials (not strictly needed but kept for consistency)
      localStorage.setItem('authHeader', btoa(`${user.username}:${user.password}`));
      localStorage.setItem('username', user.username);

      // ✅ Always redirect to the user's own dashboard
      this.router.navigate([user.route]);
    }, 500);
  }
}