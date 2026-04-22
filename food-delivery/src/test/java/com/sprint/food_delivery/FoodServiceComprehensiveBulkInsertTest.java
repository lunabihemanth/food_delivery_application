/* package com.sprint.food_delivery;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import com.sprint.food_delivery.CheckoutModule.Coupons.CouponService;
import com.sprint.food_delivery.CheckoutModule.OrdersCoupons.OrdersCouponsService;
import com.sprint.food_delivery.CheckoutModule.Ratings.RatingsService;
import com.sprint.food_delivery.CustomersModule.Customers.CustomerRequestDTO;
import com.sprint.food_delivery.CustomersModule.Customers.CustomerService;
import com.sprint.food_delivery.CustomersModule.DeliveryAddress.DeliveryAddressService;
import com.sprint.food_delivery.OrderModule.OrderItems.OrderItemsService;
import com.sprint.food_delivery.OrderModule.Orders.OrdersService;
import com.sprint.food_delivery.RestaurantsModule.MenuItems.MenuItemsService;
import com.sprint.food_delivery.RestaurantsModule.Restaurants.RestaurantsService;

@SpringBootTest
@Transactional
@Rollback(false)
public class FoodServiceComprehensiveBulkInsertTest {

    @Autowired private CustomerService customerService;
    @Autowired private RestaurantsService restaurantService;
    @Autowired private MenuItemsService menuItemService;
    @Autowired private DeliveryAddressService driverService;
    @Autowired private OrdersService orderService;
    @Autowired private OrderItemsService orderItemService;
    @Autowired private DeliveryAddressService deliveryAddressService;
    @Autowired private CouponService couponService;
    @Autowired private OrdersCouponsService ordersCouponService;
    @Autowired private RatingsService ratingService;

    @Test
    public void insertComprehensiveDataInAllTables() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("FOOD SERVICE DATABASE - COMPREHENSIVE BULK INSERT TEST");
        System.out.println("Inserting 15 records in each table");
        System.out.println("=".repeat(80) + "\n");

        try {
            insertCustomers();
            // insertRestaurants();
            // insertMenuItems();
            // insertDeliveryDrivers();
            // insertOrders();
            // insertOrderItems();
            // insertDeliveryAddresses();
            // insertCoupons();
            // insertOrdersCoupons();
            // insertRatings();

            System.out.println("\n" + "=".repeat(80));
            System.out.println("✓ ALL DATA INSERTED SUCCESSFULLY!");
            System.out.println("Total Records: 150 (15 records × 10 tables)");
            System.out.println("=".repeat(80) + "\n");
        } catch (Exception e) {
            System.err.println("\n✗ ERROR DURING DATA INSERTION: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ========================================================================
    // 1. INSERT CUSTOMERS (15 records)
    // ========================================================================
    private void insertCustomers() {
        System.out.println("\n[1/10] Inserting CUSTOMERS (15 records)...");
        List<CustomerRequestDTO> customers = new ArrayList<>();

        customers.add(createCustomer(1, "John Doe", "john.doe@email.com", "555-0101"));
        customers.add(createCustomer(2, "Jane Smith", "jane.smith@email.com", "555-0102"));
        customers.add(createCustomer(3, "Robert Johnson", "robert.j@email.com", "555-0103"));
        customers.add(createCustomer(4, "Emily Williams", "emily.w@email.com", "555-0104"));
        customers.add(createCustomer(5, "Michael Brown", "michael.b@email.com", "555-0105"));
        customers.add(createCustomer(6, "Sarah Davis", "sarah.d@email.com", "555-0106"));
        customers.add(createCustomer(7, "David Miller", "david.m@email.com", "555-0107"));
        customers.add(createCustomer(8, "Lisa Anderson", "lisa.a@email.com", "555-0108"));
        customers.add(createCustomer(9, "Christopher Taylor", "chris.t@email.com", "555-0109"));
        customers.add(createCustomer(10, "Mary Thomas", "mary.t@email.com", "555-0110"));
        customers.add(createCustomer(11, "James Jackson", "james.j@email.com", "555-0111"));
        customers.add(createCustomer(12, "Patricia White", "patricia.w@email.com", "555-0112"));
        customers.add(createCustomer(13, "Charles Harris", "charles.h@email.com", "555-0113"));
        customers.add(createCustomer(14, "Jennifer Martin", "jennifer.m@email.com", "555-0114"));
        customers.add(createCustomer(15, "Richard Thompson", "richard.t@email.com", "555-0115"));

        // insertWithErrorHandling(customers, customerService::createCustomer, "Customer");
    }

    private CustomerRequestDTO createCustomer(int id, String name, String email, String phone) {
        CustomerRequestDTO dto = new CustomerRequestDTO();
        dto.setCustomerName(name);
        dto.setCustomerEmail(email);
        dto.setCustomerPhone(phone);
        return dto;
    }

    // // ========================================================================
    // // 2. INSERT RESTAURANTS (15 records)
    // // ========================================================================
    // private void insertRestaurants() {
    //     System.out.println("\n[2/10] Inserting RESTAURANTS (15 records)...");
    //     List<RestaurantsRequestDTO> restaurants = new ArrayList<>();

    //     restaurants.add(createRestaurants(1, "Pizza Palace", "123 Main St, New York, NY 10001", "555-1001"));
    //     restaurants.add(createRestaurants(2, "Burger Barn", "456 Oak Ave, Los Angeles, CA 90001", "555-1002"));
    //     restaurants.add(createRestaurants(3, "Sushi Spot", "789 Pine Rd, Chicago, IL 60601", "555-1003"));
    //     restaurants.add(createRestaurants(4, "Taco Fiesta", "321 Elm St, Houston, TX 77001", "555-1004"));
    //     restaurants.add(createRestaurants(5, "Pasta House", "654 Maple Dr, Phoenix, AZ 85001", "555-1005"));
    //     restaurants.add(createRestaurants(6, "Dragon's Wok", "987 Cedar Ln, Philadelphia, PA 19101", "555-1006"));
    //     restaurants.add(createRestaurants(7, "The Grill House", "159 Birch St, San Antonio, TX 78201", "555-1007"));
    //     restaurants.add(createRestaurants(8, "Curry Kitchen", "246 Ash Ave, San Diego, CA 92101", "555-1008"));
    //     restaurants.add(createRestaurants(9, "BBQ Smokehouse", "357 Spruce St, Dallas, TX 75201", "555-1009"));
    //     restaurants.add(createRestaurants(10, "Seafood Delight", "468 Willow Rd, San Jose, CA 95101", "555-1010"));
    //     restaurants.add(createRestaurants(11, "Mediterranean Kitchen", "579 Walnut St, Austin, TX 78701", "555-1011"));
    //     restaurants.add(createRestaurants(12, "Steakhouse Prime", "680 Chestnut Ave, Jacksonville, FL 32099", "555-1012"));
    //     restaurants.add(createRestaurants(13, "Thai Temple", "791 Hazel Dr, Fort Worth, TX 76102", "555-1013"));
    //     restaurants.add(createRestaurants(14, "Vietnamese Pho House", "802 Poplar St, Columbus, OH 43085", "555-1014"));
    //     restaurants.add(createRestaurants(15, "Korean BBQ King", "913 Magnolia Ave, Charlotte, NC 28202", "555-1015"));

    //     //insertWithErrorHandling(restaurants, restaurantService::createRestaurant, "Restaurant");
    // }

    // private RestaurantsRequestDTO createRestaurants(int id, String name, String address, String phone) {
    //     RestaurantsRequestDTO dto = new RestaurantsRequestDTO();
    //     dto.setRestaurantName(name);
    //     dto.setRestaurantAddress(address);
    //     dto.setRestaurantPhone(phone);
    //     return dto;
    // }

    // // ========================================================================
    // // 3. INSERT MENU ITEMS (15 records)
    // // ========================================================================
    // private void insertMenuItems() {
    //     System.out.println("\n[3/10] Inserting MENU ITEMS (15 records)...");
    //     List<MenuItemsRequestDTO> menuItems = new ArrayList<>();

    //     menuItems.add(createMenuItems(1, "Margherita Pizza", "Classic pizza with tomato and mozzarella", 12.99, 1));
    //     menuItems.add(createMenuItems(2, "Pepperoni Pizza", "Pizza topped with pepperoni slices", 14.99, 1));
    //     menuItems.add(createMenuItems(3, "Classic Burger", "Beef burger with lettuce and tomato", 10.99, 2));
    //     menuItems.add(createMenuItems(4, "Cheeseburger", "Burger with melted cheese", 11.99, 2));
    //     menuItems.add(createMenuItems(5, "California Roll", "Imitation crab, cucumber, avocado", 9.99, 3));
    //     menuItems.add(createMenuItems(6, "Spicy Tuna Roll", "Tuna with spicy mayo", 11.99, 3));
    //     menuItems.add(createMenuItems(7, "Beef Tacos", "Three soft or hard shell tacos", 8.99, 4));
    //     menuItems.add(createMenuItems(8, "Chicken Tacos", "Three chicken tacos", 8.99, 4));
    //     menuItems.add(createMenuItems(9, "Spaghetti Carbonara", "Classic Italian pasta", 12.99, 5));
    //     menuItems.add(createMenuItems(10, "Fettuccine Alfredo", "Creamy Alfredo sauce", 11.99, 5));
    //     menuItems.add(createMenuItems(11, "Kung Pao Chicken", "Chicken with peanuts and spices", 11.99, 6));
    //     menuItems.add(createMenuItems(12, "Lo Mein", "Noodles with vegetables and protein", 10.99, 6));
    //     menuItems.add(createMenuItems(13, "Grilled Steak", "Premium beef steak", 22.99, 7));
    //     menuItems.add(createMenuItems(14, "Butter Chicken", "Tender chicken in creamy sauce", 13.99, 8));
    //     menuItems.add(createMenuItems(15, "Beef Ribs", "Slow-cooked BBQ ribs", 18.99, 9));

    //     //insertWithErrorHandling(menuItems, menuItemService::createMenuItem, "MenuItem");
    // }

    // private MenuItemsRequestDTO createMenuItems(int id, String name, String description, double price, int restaurantId) {
    //     MenuItemsRequestDTO dto = new MenuItemsRequestDTO();
    //     dto.setItemId(id);
    //     dto.setItemName(name);
    //     dto.setItemDescription(description);
    //     dto.setItemPrice(price);
    //     dto.setRestaurantId(restaurantId);
    //     return dto;
    // }

    // // ========================================================================
    // // 4. INSERT DELIVERY DRIVERS (15 records)
    // // ========================================================================
    // private void insertDeliveryDrivers() {
    //     System.out.println("\n[4/10] Inserting DELIVERY DRIVERS (15 records)...");
    //     List<DeliveryDriversRequestDTO> drivers = new ArrayList<>();

    //     drivers.add(createDriver(1, "James Wilson", "555-2001", "Honda Civic"));
    //     drivers.add(createDriver(2, "Kevin Moore", "555-2002", "Toyota Camry"));
    //     drivers.add(createDriver(3, "Maria Garcia", "555-2003", "Ford Focus"));
    //     drivers.add(createDriver(4, "Ahmed Hassan", "555-2004", "Nissan Altima"));
    //     drivers.add(createDriver(5, "Jennifer Lee", "555-2005", "Volkswagen Jetta"));
    //     drivers.add(createDriver(6, "Carlos Rodriguez", "555-2006", "Hyundai Elantra"));
    //     drivers.add(createDriver(7, "Nina Patel", "555-2007", "Honda Accord"));
    //     drivers.add(createDriver(8, "Marcus Johnson", "555-2008", "Ford Fusion"));
    //     drivers.add(createDriver(9, "Lisa Brown", "555-2009", "Toyota Prius"));
    //     drivers.add(createDriver(10, "David Chen", "555-2010", "Honda CR-V"));
    //     drivers.add(createDriver(11, "Sarah Martinez", "555-2011", "Mazda 3"));
    //     drivers.add(createDriver(12, "Tom Wilson", "555-2012", "Subaru Impreza"));
    //     drivers.add(createDriver(13, "Jessica Anderson", "555-2013", "Toyota Corolla"));
    //     drivers.add(createDriver(14, "Michael Davis", "555-2014", "Ford Escape"));
    //     drivers.add(createDriver(15, "Amanda Taylor", "555-2015", "Honda Civic Hybrid"));

    //     //insertWithErrorHandling(drivers, driverService::createDriver, "DeliveryDriver");
    // }

    // private DeliveryDriversRequestDTO createDriver(int id, String name, String phone, String vehicle) {
    //     DeliveryDriversRequestDTO dto = new DeliveryDriversRequestDTO();
    //     dto.setDriverName(name);
    //     dto.setDriverPhone(phone);
    //     dto.setDriverVehicle(vehicle);
    //     return dto;
    // }

    // // ========================================================================
    // // 5. INSERT ORDERS (15 records)
    // // ========================================================================
    // private <OrderRequestDTO> void insertOrders() {
    //     System.out.println("\n[5/10] Inserting ORDERS (15 records)...");
    //     List<OrderRequestDTO> orders = new ArrayList<>();

    //     orders.add(createOrder(1, LocalDateTime.of(2024, 1, 15, 12, 30), 1, 1, 1, "Delivered"));
    //     orders.add(createOrder(2, LocalDateTime.of(2024, 1, 15, 13, 45), 2, 2, 2, "Delivered"));
    //     orders.add(createOrder(3, LocalDateTime.of(2024, 1, 16, 18, 20), 3, 3, 3, "Delivered"));
    //     orders.add(createOrder(4, LocalDateTime.of(2024, 1, 16, 19, 00), 4, 4, 4, "Delivered"));
    //     orders.add(createOrder(5, LocalDateTime.of(2024, 1, 17, 12, 00), 5, 5, 5, "Delivered"));
    //     orders.add(createOrder(6, LocalDateTime.of(2024, 1, 17, 13, 30), 6, 6, 6, "Delivered"));
    //     orders.add(createOrder(7, LocalDateTime.of(2024, 1, 18, 19, 15), 7, 7, 7, "Delivered"));
    //     orders.add(createOrder(8, LocalDateTime.of(2024, 1, 18, 20, 00), 8, 8, 8, "Pending"));
    //     orders.add(createOrder(9, LocalDateTime.of(2024, 1, 19, 12, 45), 9, 9, 9, "Processing"));
    //     orders.add(createOrder(10, LocalDateTime.of(2024, 1, 19, 13, 30), 10, 10, 10, "Delivered"));
    //     orders.add(createOrder(11, LocalDateTime.of(2024, 1, 20, 14, 15), 11, 11, 11, "Delivered"));
    //     orders.add(createOrder(12, LocalDateTime.of(2024, 1, 20, 15, 45), 12, 12, 12, "Processing"));
    //     orders.add(createOrder(13, LocalDateTime.of(2024, 1, 21, 11, 30), 13, 13, 13, "Delivered"));
    //     orders.add(createOrder(14, LocalDateTime.of(2024, 1, 21, 16, 20), 14, 14, 14, "Pending"));
    //     orders.add(createOrder(15, LocalDateTime.of(2024, 1, 22, 17, 45), 15, 15, 15, "Delivered"));

    //     //insertWithErrorHandling(orders, orderService::createOrder, "Order");
    // }

    // private OrdersRequestDTO createOrder(int id, LocalDateTime orderDate, int customerId, int restaurantId, int driverId, String status) {
    //     OrdersRequestDTO dto = new OrdersRequestDTO();
    //     dto.setOrderDate(orderDate);
    //     dto.setCustomerId(customerId);
    //     dto.setRestaurantId(restaurantId);
    //     dto.setDeliveryDriverId(driverId);
    //     dto.setOrderStatus(status);
    //     return dto;
    // }

    // // ========================================================================
    // // 6. INSERT ORDER ITEMS (15 records)
    // // ========================================================================
    // private void insertOrderItems() {
    //     System.out.println("\n[6/10] Inserting ORDER ITEMS (15 records)...");
    //     List<OrderItemRequestDTO> orderItems = new ArrayList<>();

    //     orderItems.add(createOrderItem(1, 1, 1, 2));
    //     orderItems.add(createOrderItem(2, 1, 2, 1));
    //     orderItems.add(createOrderItem(3, 2, 3, 1));
    //     orderItems.add(createOrderItem(4, 2, 4, 2));
    //     orderItems.add(createOrderItem(5, 3, 5, 3));
    //     orderItems.add(createOrderItem(6, 3, 6, 1));
    //     orderItems.add(createOrderItem(7, 4, 7, 2));
    //     orderItems.add(createOrderItem(8, 4, 8, 1));
    //     orderItems.add(createOrderItem(9, 5, 9, 1));
    //     orderItems.add(createOrderItem(10, 5, 10, 1));
    //     orderItems.add(createOrderItem(11, 6, 11, 2));
    //     orderItems.add(createOrderItem(12, 7, 12, 1));
    //     orderItems.add(createOrderItem(13, 8, 13, 1));
    //     orderItems.add(createOrderItem(14, 9, 14, 2));
    //     orderItems.add(createOrderItem(15, 10, 15, 1));

    //     insertWithErrorHandling(orderItems, orderItemService::createOrderItem, "OrderItem");
    // }

    // private OrderItemRequestDTO createOrderItem(int id, int orderId, int itemId, int quantity) {
    //     OrderItemRequestDTO dto = new OrderItemRequestDTO();
    //     dto.setOrderItemId(id);
    //     dto.setOrderId(orderId);
    //     dto.setItemId(itemId);
    //     dto.setQuantity(quantity);
    //     return dto;
    // }

    // // ========================================================================
    // // 7. INSERT DELIVERY ADDRESSES (15 records)
    // // ========================================================================
    // private void insertDeliveryAddresses() {
    //     System.out.println("\n[7/10] Inserting DELIVERY ADDRESSES (15 records)...");
    //     List<DeliveryAddressRequestDTO> addresses = new ArrayList<>();

    //     addresses.add(createDeliveryAddress(1, 1, "123 Park Ave", "Apt 4B", "New York", "NY", "10001"));
    //     addresses.add(createDeliveryAddress(2, 2, "456 Beach Blvd", "", "Los Angeles", "CA", "90001"));
    //     addresses.add(createDeliveryAddress(3, 3, "789 Lake Shore Dr", "Suite 200", "Chicago", "IL", "60601"));
    //     addresses.add(createDeliveryAddress(4, 4, "321 Downtown Ave", "", "Houston", "TX", "77001"));
    //     addresses.add(createDeliveryAddress(5, 5, "654 Desert Rd", "Apt 101", "Phoenix", "AZ", "85001"));
    //     addresses.add(createDeliveryAddress(6, 6, "987 Historical St", "", "Philadelphia", "PA", "19101"));
    //     addresses.add(createDeliveryAddress(7, 7, "159 River Rd", "Unit 5", "San Antonio", "TX", "78201"));
    //     addresses.add(createDeliveryAddress(8, 8, "246 Garden Path", "", "San Diego", "CA", "92101"));
    //     addresses.add(createDeliveryAddress(9, 9, "135 Broadway", "Suite 301", "Dallas", "TX", "75201"));
    //     addresses.add(createDeliveryAddress(10, 10, "357 Sunset Blvd", "", "San Jose", "CA", "95101"));
    //     addresses.add(createDeliveryAddress(11, 11, "468 Mountain View", "Apt 22", "Austin", "TX", "78701"));
    //     addresses.add(createDeliveryAddress(12, 12, "579 Ocean View", "", "Jacksonville", "FL", "32099"));
    //     addresses.add(createDeliveryAddress(13, 13, "680 Prairie Ave", "Suite 400", "Fort Worth", "TX", "76102"));
    //     addresses.add(createDeliveryAddress(14, 14, "791 Valley Dr", "", "Columbus", "OH", "43085"));
    //     addresses.add(createDeliveryAddress(15, 15, "802 Forest Lane", "Unit 10", "Charlotte", "NC", "28202"));

    //     insertWithErrorHandling(addresses, deliveryAddressService::createAddress, "DeliveryAddress");
    // }

    // private DeliveryAddressRequestDTO createDeliveryAddress(int id, int customerId, String line1, String line2, String city, String state, String zip) {
    //     DeliveryAddressRequestDTO dto = new DeliveryAddressRequestDTO();
    //     dto.setAddressId(id);
    //     dto.setCustomerId(customerId);
    //     dto.setAddressLine1(line1);
    //     dto.setAddressLine2(line2);
    //     dto.setCity(city);
    //     dto.setState(state);
    //     dto.setPostalCode(zip);
    //     return dto;
    // }

    // // ========================================================================
    // // 8. INSERT COUPONS (15 records)
    // // ========================================================================
    // private void insertCoupons() {
    //     System.out.println("\n[8/10] Inserting COUPONS (15 records)...");
    //     List<CouponRequestDTO> coupons = new ArrayList<>();

    //     coupons.add(createCoupon(1, "SAVE10", 10.00, LocalDate.of(2024, 12, 31)));
    //     coupons.add(createCoupon(2, "SAVE15", 15.00, LocalDate.of(2024, 12, 31)));
    //     coupons.add(createCoupon(3, "PIZZA20", 20.00, LocalDate.of(2024, 11, 30)));
    //     coupons.add(createCoupon(4, "BURGER5", 5.00, LocalDate.of(2024, 10, 31)));
    //     coupons.add(createCoupon(5, "SUSHI25", 25.00, LocalDate.of(2024, 12, 31)));
    //     coupons.add(createCoupon(6, "WELCOME30", 30.00, LocalDate.of(2024, 9, 30)));
    //     coupons.add(createCoupon(7, "PASTA12", 12.00, LocalDate.of(2024, 11, 15)));
    //     coupons.add(createCoupon(8, "DELIVERY5", 5.00, LocalDate.of(2024, 12, 31)));
    //     coupons.add(createCoupon(9, "SUMMER50", 50.00, LocalDate.of(2024, 8, 31)));
    //     coupons.add(createCoupon(10, "WINTER20", 20.00, LocalDate.of(2024, 1, 31)));
    //     coupons.add(createCoupon(11, "SPRING15", 15.00, LocalDate.of(2024, 5, 31)));
    //     coupons.add(createCoupon(12, "FALL25", 25.00, LocalDate.of(2024, 10, 31)));
    //     coupons.add(createCoupon(13, "NEWYEAR35", 35.00, LocalDate.of(2024, 1, 31)));
    //     coupons.add(createCoupon(14, "FRIDAY13", 13.00, LocalDate.of(2024, 6, 30)));
    //     coupons.add(createCoupon(15, "LUCKY7", 7.00, LocalDate.of(2024, 7, 31)));

    //     insertWithErrorHandling(coupons, couponService::createCoupon, "Coupon");
    // }

    // private CouponRequestDTO createCoupon(int id, String code, double discount, LocalDate expiryDate) {
    //     CouponRequestDTO dto = new CouponRequestDTO();
    //     dto.setCouponId(id);
    //     dto.setCouponCode(code);
    //     dto.setDiscountAmount(discount);
    //     dto.setExpiryDate(expiryDate);
    //     return dto;
    // }

    // // ========================================================================
    // // 9. INSERT ORDER-COUPONS MAPPING (15 records)
    // // ========================================================================
    // private void insertOrdersCoupons() {
    //     System.out.println("\n[9/10] Inserting ORDER-COUPON MAPPINGS (15 records)...");
    //     List<OrdersCouponRequestDTO> ordersCoupons = new ArrayList<>();

    //     ordersCoupons.add(createOrdersCoupon(1, 1));
    //     ordersCoupons.add(createOrdersCoupon(2, 2));
    //     ordersCoupons.add(createOrdersCoupon(3, 3));
    //     ordersCoupons.add(createOrdersCoupon(4, 4));
    //     ordersCoupons.add(createOrdersCoupon(5, 5));
    //     ordersCoupons.add(createOrdersCoupon(6, 6));
    //     ordersCoupons.add(createOrdersCoupon(7, 7));
    //     ordersCoupons.add(createOrdersCoupon(8, 8));
    //     ordersCoupons.add(createOrdersCoupon(9, 9));
    //     ordersCoupons.add(createOrdersCoupon(10, 10));
    //     ordersCoupons.add(createOrdersCoupon(11, 11));
    //     ordersCoupons.add(createOrdersCoupon(12, 12));
    //     ordersCoupons.add(createOrdersCoupon(13, 13));
    //     ordersCoupons.add(createOrdersCoupon(14, 14));
    //     ordersCoupons.add(createOrdersCoupon(15, 15));

    //     insertWithErrorHandling(ordersCoupons, ordersCouponService::createOrdersCoupon, "OrdersCoupon");
    // }

    // private OrdersCouponRequestDTO createOrdersCoupon(int orderId, int couponId) {
    //     OrdersCouponRequestDTO dto = new OrdersCouponRequestDTO();
    //     dto.setOrderId(orderId);
    //     dto.setCouponId(couponId);
    //     return dto;
    // }

    // // ========================================================================
    // // 10. INSERT RATINGS (15 records)
    // // ========================================================================
    // private void insertRatings() {
    //     System.out.println("\n[10/10] Inserting RATINGS & REVIEWS (15 records)...");
    //     List<RatingRequestDTO> ratings = new ArrayList<>();

    //     ratings.add(createRating(1, 1, 1, 5, "Excellent pizza! Delivered on time. Highly recommend!"));
    //     ratings.add(createRating(2, 2, 2, 4, "Great burgers, very tasty. Slightly slow delivery."));
    //     ratings.add(createRating(3, 3, 3, 5, "Sushi was fresh and delicious! Best in town."));
    //     ratings.add(createRating(4, 4, 4, 4, "Good tacos, could use more salsa. Nice service."));
    //     ratings.add(createRating(5, 5, 5, 5, "Best pasta I've had in a while! Will order again."));
    //     ratings.add(createRating(6, 6, 6, 4, "Good quality, slightly slow delivery. Food was hot."));
    //     ratings.add(createRating(7, 7, 7, 5, "Amazing food and quick delivery! 5 stars!"));
    //     ratings.add(createRating(8, 8, 8, 3, "Food was okay, slightly old when arrived. Average."));
    //     ratings.add(createRating(9, 9, 9, 5, "Fantastic flavors, highly recommend to friends!"));
    //     ratings.add(createRating(10, 10, 10, 4, "Good food, decent delivery time. Satisfied."));
    //     ratings.add(createRating(11, 11, 11, 5, "Outstanding experience! Perfect service."));
    //     ratings.add(createRating(12, 12, 12, 4, "Tasty dishes, minor issues with delivery."));
    //     ratings.add(createRating(13, 13, 13, 5, "Authentic cuisine, excellent quality!"));
    //     ratings.add(createRating(14, 14, 14, 3, "Decent food, could be better. Average experience."));
    //     ratings.add(createRating(15, 15, 15, 5, "Perfect delivery, amazing taste. Highly satisfied!"));

    //     insertWithErrorHandling(ratings, ratingService::createRating, "Rating");
    // }

    // private RatingRequestDTO createRating(int id, int orderId, int restaurantId, int rating, String review) {
    //     RatingRequestDTO dto = new RatingRequestDTO();
    //     dto.setRatingId(id);
    //     dto.setOrderId(orderId);
    //     dto.setRestaurantId(restaurantId);
    //     dto.setRating(rating);
    //     dto.setReview(review);
    //     return dto;
    // }

    // // ========================================================================
    // // GENERIC ERROR HANDLING METHOD
    // // ========================================================================
    // private <T> void insertWithErrorHandling(List<T> items, ServiceInsertAction<T> action, String entityType) {
    //     int successCount = 0;
    //     int failureCount = 0;

    //     for (T item : items) {
    //         try {
    //             action.insert(item);
    //             successCount++;
    //         } catch (Exception e) {
    //             failureCount++;
    //             System.err.println("  ✗ Error inserting " + entityType + ": " + e.getMessage());
    //         }
    //     }

    //     System.out.println("  ✓ " + entityType + " - Inserted: " + successCount + " | Failed: " + failureCount);
    // }

    // @FunctionalInterface
    // interface ServiceInsertAction<T> {
    //     void insert(T item) throws Exception;
    // }
}
    */