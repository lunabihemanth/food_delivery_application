// import { NgModule } from '@angular/core';
// import { RouterModule, Routes } from '@angular/router';
// import { Welcome } from './pages/welcome/welcome';
// import { RoleSelect } from './pages/role-select/role-select';
// import { Auth } from './pages/auth/auth';
// import { Home } from './pages/customer/home/home';
// import { Menu } from './pages/customer/menu/menu';
// import { Cart } from './pages/customer/cart/cart';
// import { Checkout } from './pages/customer/checkout/checkout';
// import { OrderSuccess } from './pages/customer/order-success/order-success';
// import { MyOrders } from './pages/customer/my-orders/my-orders';
// import { Rating } from './pages/customer/rating/rating';
// import { Coupons } from './pages/customer/coupons/coupons';
// import { Profile } from './pages/customer/profile/profile';
// import { Dashboard as RestaurantDashboard } from './pages/driver/dashboard/dashboard';
// import { MenuManagement } from './pages/restaurant/menu-management/menu-management';
// import { Orders as RestaurantOrders } from './pages/restaurant/orders/orders';
// import { Ratings } from './pages/restaurant/ratings/ratings';
// import { Profile as RestaurantProfile } from './pages/customer/profile/profile';
// import { Dashboard as DriverDashboard } from './pages/driver/dashboard/dashboard';
// import { Deliveries } from './pages/driver/deliveries/deliveries';
// import { Profile as DriverProfile } from './pages/customer/profile/profile';

// export const routes: Routes = [
//   { path: '', redirectTo: 'welcome', pathMatch: 'full' },
//   { path: 'welcome', component: Welcome },
//   { path: 'role-select', component: RoleSelect},
//   { path: 'login/:role', component: Auth },

//   // Customer routes
//   { path: 'customer/home', component: Home },
//   { path: 'customer/menu/:id', component: Menu},
//   { path: 'customer/cart', component: Cart },
//   { path: 'customer/checkout', component: Checkout },
//   { path: 'customer/order-success', component: OrderSuccess },
//   { path: 'customer/orders', component: MyOrders },
//   { path: 'customer/rating/:orderId', component: Rating },
//   { path: 'customer/coupons', component: Coupons},
//   { path: 'customer/profile', component: Profile},

//   // Restaurant routes
//   { path: 'restaurant/dashboard', component: RestaurantDashboard },
//   { path: 'restaurant/menu', component: MenuManagement },
//   { path: 'restaurant/orders', component: RestaurantOrders },
//   { path: 'restaurant/ratings', component: Ratings },
//   { path: 'restaurant/profile', component: RestaurantProfile },

//   // Driver routes
//   { path: 'driver/dashboard', component: DriverDashboard },
//   { path: 'driver/deliveries', component: Deliveries },
//   { path: 'driver/profile', component: DriverProfile },

//   { path: '**', redirectTo: 'welcome' }
// ];

// @NgModule({
//   imports: [RouterModule.forRoot(routes)],
//   exports: [RouterModule]
// })
// export class AppRoutingModule { }

// import { Routes } from '@angular/router';

// import { HomeComponent } from './landingpage/home/home';
// import { LoginComponent } from './landingpage/login/login';
// import { Profile } from './hemanth/profile/profile';

// // (Optional – add later when needed)
// // import { CustomerHome } from './customers/home/home';
// // import { DeliveryHome } from './delivery/home/home';
// // import { RestaurantHome } from './restaurants/home/home';

// export const routes: Routes = [
//   // Default route
//   { path: '', redirectTo: 'home', pathMatch: 'full' },

//   // Landing pages
//   { path: 'home', component: HomeComponent },
//   { path: 'login', component: LoginComponent },
//   { path: 'profile', component: Profile },
//   // Future routes (add later)
//   // { path: 'customers', component: CustomerHome },
//   // { path: 'delivery', component: DeliveryHome },
//   // { path: 'restaurants', component: RestaurantHome },

//   // Fallback (optional)
//   { path: '**', redirectTo: 'home' }
// ];
import { Routes } from '@angular/router';

// Landing
import { HomeComponent } from './landingpage/home/home';
import { LoginComponent } from './landingpage/login/login';



// Your modules (adjust paths if needed)
import { Home as CustomerHome } from './pages/customer/home/home';
import { Dashboard as DeliveryHome } from './pages/driver/dashboard/dashboard';
import { Dashboard as RestaurantHome } from './pages/restaurant/dashboard/dashboard';
import { Hemanth } from './hemanth/hemanth';
import { Thenmozhi } from './thenmozhi/thenmozhi';
import { Kisol } from './kisol/kisol';
import { Jeevitha } from './jeevitha/jeevitha';
import { Annie } from './annie/annie';

export const routes: Routes = [
  { path: '', redirectTo: 'home', pathMatch: 'full' },

  // Landing
  { path: 'home', component: HomeComponent },
  { path: 'login', component: LoginComponent },

  { path: 'annie', component: Annie },
  { path: 'jeevitha', component: Jeevitha },
  { path: 'kisol', component: Kisol },
  { path: 'thenmozhi', component: Thenmozhi },
  { path: 'hemanth', component: Hemanth },

];