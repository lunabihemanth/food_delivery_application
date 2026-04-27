package com.sprint.food_delivery.servicetest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.sprint.food_delivery.CheckoutModule.Coupons.CouponRequestDTO;
import com.sprint.food_delivery.CheckoutModule.Coupons.ICouponService;
import com.sprint.food_delivery.CheckoutModule.OrdersCoupons.IOrdersCouponsService;
import com.sprint.food_delivery.CheckoutModule.OrdersCoupons.OrdersCouponsRequestDTO;
import com.sprint.food_delivery.CheckoutModule.OrdersCoupons.OrdersCouponsResponseDTO;
import com.sprint.food_delivery.CustomersModule.Customers.CustomerRequestDTO;
import com.sprint.food_delivery.CustomersModule.Customers.ICustomerService;
import com.sprint.food_delivery.Exception.ConflictException;
import com.sprint.food_delivery.Exception.ResourceNotFoundException;
import com.sprint.food_delivery.OrderModule.Orders.IOrdersService;
import com.sprint.food_delivery.OrderModule.Orders.OrdersRequestDTO;
import com.sprint.food_delivery.RestaurantsModule.Restaurants.IRestaurantsService;
import com.sprint.food_delivery.RestaurantsModule.Restaurants.RestaurantsRequestDTO;

@SpringBootTest
@Transactional
public class OrdersCouponsServiceTest {

    @Autowired private IOrdersCouponsService service;
    @Autowired private IOrdersService orderService;
    @Autowired private ICouponService couponService;
    @Autowired private ICustomerService customerService;
    @Autowired private IRestaurantsService Restaurantservice;

    // HELPERS

    private Integer createCustomer() {
        CustomerRequestDTO dto = new CustomerRequestDTO();
        dto.setCustomerName("User_" + System.nanoTime());
        dto.setCustomerEmail("user" + System.nanoTime() + "@mail.com");
        dto.setCustomerPhone("9" + System.nanoTime());
        return customerService.save(dto).getCustomerId();
    }

    private Integer createRestaurant() {
        RestaurantsRequestDTO dto = new RestaurantsRequestDTO();
        dto.setRestaurantName("Rest_" + System.nanoTime());
        dto.setRestaurantAddress("Mumbai");
        dto.setRestaurantPhone("9" + System.nanoTime());
        return Restaurantservice.save(dto).getRestaurantId();
    }

    private Integer createOrder() {
        Integer cid = createCustomer();
        Integer rid = createRestaurant();

        OrdersRequestDTO dto = new OrdersRequestDTO();
        dto.setCustomerId(cid);
        dto.setRestaurantId(rid);

        return orderService.save(dto).getOrderId();
    }

    private Integer createCoupon(boolean expired) {
        CouponRequestDTO dto = new CouponRequestDTO();

        dto.setCouponId((int)(System.nanoTime() % 1000000)); // 🔥 unique ID
        dto.setCouponCode("CODE_" + System.nanoTime());
        dto.setDiscountAmount(100.0);

        if (expired) {
            dto.setExpiryDate(LocalDate.now().minusDays(2)); // ❌ expired
        } else {
            dto.setExpiryDate(LocalDate.now().plusDays(5)); // ✅ valid
        }

        return couponService.save(dto).getCouponId();
    }

    private OrdersCouponsRequestDTO createDTO(Integer oid, Integer cid) {
        OrdersCouponsRequestDTO dto = new OrdersCouponsRequestDTO();
        dto.setOrderId(oid);
        dto.setCouponId(cid);
        return dto;
    }

    // ✅ 1. Apply Coupon Success
    @Test
    void testApplyCouponSuccess() {
        Integer oid = createOrder();
        Integer cid = createCoupon(false);

        OrdersCouponsResponseDTO res =
                service.applyCoupon(createDTO(oid, cid));

        assertNotNull(res);
        assertEquals(oid, res.getOrderId());
    }

    // ❌ 3. Duplicate Coupon
    @Test
    void testDuplicateCoupon() {
        Integer oid = createOrder();
        Integer cid = createCoupon(false);

        service.applyCoupon(createDTO(oid, cid));

        assertThrows(ConflictException.class,
                () -> service.applyCoupon(createDTO(oid, cid)));
    }

    // ❌ 4. Order Not Found
    @Test
    void testOrderNotFound() {
        Integer cid = createCoupon(false);

        assertThrows(ResourceNotFoundException.class,
                () -> service.applyCoupon(createDTO(999999, cid)));
    }

    // ❌ 5. Coupon Not Found
    @Test
    void testCouponNotFound() {
        Integer oid = createOrder();

        assertThrows(ResourceNotFoundException.class,
                () -> service.applyCoupon(createDTO(oid, 999999)));
    }

    // ✅ 6. Get Coupons By Order
    @Test
    void testGetCouponsByOrder() {
        Integer oid = createOrder();
        Integer cid = createCoupon(false);

        service.applyCoupon(createDTO(oid, cid));

        List<OrdersCouponsResponseDTO> list =
                service.getCouponsByOrderId(oid);

        assertFalse(list.isEmpty());
    }

    // ❌ 7. Get Coupons - Order Not Found
    @Test
    void testGetCouponsOrderNotFound() {
        assertThrows(ResourceNotFoundException.class,
                () -> service.getCouponsByOrderId(999999));
    }

    // ✅ 8. Remove Coupon
    @Test
    void testRemoveCoupon() {
        Integer oid = createOrder();
        Integer cid = createCoupon(false);

        service.applyCoupon(createDTO(oid, cid));

        String res = service.removeCoupon(oid, cid);

        assertTrue(res.contains("removed"));
    }

    // ❌ 9. Remove Coupon Not Applied
    @Test
    void testRemoveCouponNotApplied() {
        assertThrows(ResourceNotFoundException.class,
                () -> service.removeCoupon(999999, 999999));
    }
}
