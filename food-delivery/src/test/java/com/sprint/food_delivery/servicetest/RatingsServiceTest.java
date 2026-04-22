package com.sprint.food_delivery.servicetest;

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

import com.sprint.food_delivery.CheckoutModule.Ratings.IRatingsService;
import com.sprint.food_delivery.CheckoutModule.Ratings.RatingsRequestDTO;
import com.sprint.food_delivery.CheckoutModule.Ratings.RatingsResponseDTO;
import com.sprint.food_delivery.CustomersModule.Customers.CustomerRequestDTO;
import com.sprint.food_delivery.CustomersModule.Customers.ICustomerService;
import com.sprint.food_delivery.Exception.BadRequestException;
import com.sprint.food_delivery.Exception.ConflictException;
import com.sprint.food_delivery.Exception.ResourceNotFoundException;
import com.sprint.food_delivery.OrderModule.Orders.IOrdersService;
import com.sprint.food_delivery.OrderModule.Orders.OrdersRequestDTO;
import com.sprint.food_delivery.OrderModule.Orders.OrdersResponseDTO;
import com.sprint.food_delivery.RestaurantsModule.Restaurants.IRestaurantsService;
import com.sprint.food_delivery.RestaurantsModule.Restaurants.RestaurantsRequestDTO;

@SpringBootTest
@Transactional
public class RatingsServiceTest {

    @Autowired private IRatingsService ratingService;
    @Autowired private IOrdersService orderService;
    @Autowired private ICustomerService customerService;
    @Autowired private IRestaurantsService Restaurantservice;

    // 🔧 HELPERS

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

    // 🔥 IMPORTANT → create DELIVERED order
    private Integer createDeliveredOrder(Integer cid, Integer rid) {
        OrdersRequestDTO dto = new OrdersRequestDTO();
        dto.setCustomerId(cid);
        dto.setRestaurantId(rid);

        OrdersResponseDTO order = orderService.save(dto);

        // move status → PENDING → CONFIRMED → OUT_FOR_DELIVERY → DELIVERED
        OrdersRequestDTO update = new OrdersRequestDTO();

        update.setOrderStatus("CONFIRMED");
        orderService.update(order.getOrderId(), update);

        update.setOrderStatus("OUT_FOR_DELIVERY");
        orderService.update(order.getOrderId(), update);

        update.setOrderStatus("DELIVERED");
        orderService.update(order.getOrderId(), update);

        return order.getOrderId();
    }

    private RatingsRequestDTO createDTO(Integer oid, Integer rid) {
        RatingsRequestDTO dto = new RatingsRequestDTO();
        dto.setOrderId(oid);
        dto.setRestaurantId(rid);
        dto.setRating(4);
        dto.setReview("Good");
        return dto;
    }

    // ✅ 1. Save Rating Success
    @Test
    void testSaveRatingSuccess() {
        Integer cid = createCustomer();
        Integer rid = createRestaurant();
        Integer oid = createDeliveredOrder(cid, rid);

        RatingsResponseDTO res =
                ratingService.save(createDTO(oid, rid));

        assertNotNull(res);
        assertEquals(4, res.getRating());
    }

    // ❌ 2. Order Not Delivered
    @Test
    void testRatingBeforeDelivery() {
        Integer cid = createCustomer();
        Integer rid = createRestaurant();

        OrdersRequestDTO dto = new OrdersRequestDTO();
        dto.setCustomerId(cid);
        dto.setRestaurantId(rid);

        Integer oid = orderService.save(dto).getOrderId();

        assertThrows(BadRequestException.class,
                () -> ratingService.save(createDTO(oid, rid)));
    }

    // ❌ 3. Different Restaurant
    @Test
    void testOrderRestaurantMismatch() {
        Integer cid = createCustomer();
        Integer rid1 = createRestaurant();
        Integer rid2 = createRestaurant();

        Integer oid = createDeliveredOrder(cid, rid1);

        assertThrows(BadRequestException.class,
                () -> ratingService.save(createDTO(oid, rid2)));
    }

    // ❌ 4. Duplicate Rating
    @Test
    void testDuplicateRating() {
        Integer cid = createCustomer();
        Integer rid = createRestaurant();
        Integer oid = createDeliveredOrder(cid, rid);

        ratingService.save(createDTO(oid, rid));

        assertThrows(ConflictException.class,
                () -> ratingService.save(createDTO(oid, rid)));
    }

    // ❌ 5. Invalid Rating Value
    @Test
    void testInvalidRatingValue() {
        Integer cid = createCustomer();
        Integer rid = createRestaurant();
        Integer oid = createDeliveredOrder(cid, rid);

        RatingsRequestDTO dto = createDTO(oid, rid);
        dto.setRating(6); // invalid

        assertThrows(BadRequestException.class,
                () -> ratingService.save(dto));
    }

    // ❌ 6. Order Not Found
    @Test
    void testOrderNotFound() {
        Integer rid = createRestaurant();

        assertThrows(ResourceNotFoundException.class,
                () -> ratingService.save(createDTO(9999, rid)));
    }

    // ❌ 7. Restaurant Not Found
    @Test
    void testRestaurantNotFound() {
        Integer cid = createCustomer();
        Integer rid = createRestaurant();
        Integer oid = createDeliveredOrder(cid, rid);

        assertThrows(ResourceNotFoundException.class,
                () -> ratingService.save(createDTO(oid, 9999)));
    }

    // ✅ 8. Get All Ratings
    @Test
    void testGetAllRatings() {
        Integer cid = createCustomer();
        Integer rid = createRestaurant();
        Integer oid = createDeliveredOrder(cid, rid);

        ratingService.save(createDTO(oid, rid));

        List<RatingsResponseDTO> list = ratingService.getAll();

        assertFalse(list.isEmpty());
    }

    // ✅ 9. Find By ID
    @Test
    void testFindById() {
        Integer cid = createCustomer();
        Integer rid = createRestaurant();
        Integer oid = createDeliveredOrder(cid, rid);

        RatingsResponseDTO saved =
                ratingService.save(createDTO(oid, rid));

        RatingsResponseDTO found =
                ratingService.findById(saved.getRatingId());

        assertEquals(saved.getRatingId(), found.getRatingId());
    }

    // ❌ 10. Find By ID Not Found
    @Test
    void testFindByIdNotFound() {
        assertThrows(ResourceNotFoundException.class,
                () -> ratingService.findById(9999));
    }

    // ✅ 11. Get By Restaurant
    @Test
    void testGetByRestaurant() {
        Integer cid = createCustomer();
        Integer rid = createRestaurant();
        Integer oid = createDeliveredOrder(cid, rid);

        ratingService.save(createDTO(oid, rid));

        List<RatingsResponseDTO> list =
                ratingService.getByRestaurantId(rid);

        assertFalse(list.isEmpty());
    }

    // ❌ 12. Restaurant Not Found (Get)
    @Test
    void testGetByRestaurantNotFound() {
        assertThrows(ResourceNotFoundException.class,
                () -> ratingService.getByRestaurantId(9999));
    }

    // ✅ 13. Update Rating
    @Test
    void testUpdateRating() {
        Integer cid = createCustomer();
        Integer rid = createRestaurant();
        Integer oid = createDeliveredOrder(cid, rid);

        RatingsResponseDTO saved =
                ratingService.save(createDTO(oid, rid));

        RatingsRequestDTO update = createDTO(oid, rid);
        update.setRating(5);

        RatingsResponseDTO updated =
                ratingService.update(saved.getRatingId(), update);

        assertEquals(5, updated.getRating());
    }

    // ❌ 14. Update Invalid Rating
    @Test
    void testUpdateInvalidRating() {
        Integer cid = createCustomer();
        Integer rid = createRestaurant();
        Integer oid = createDeliveredOrder(cid, rid);

        RatingsResponseDTO saved =
                ratingService.save(createDTO(oid, rid));

        RatingsRequestDTO update = createDTO(oid, rid);
        update.setRating(10);

        assertThrows(BadRequestException.class,
                () -> ratingService.update(saved.getRatingId(), update));
    }

    // ✅ 15. Delete Rating
    @Test
    void testDeleteRating() {
        Integer cid = createCustomer();
        Integer rid = createRestaurant();
        Integer oid = createDeliveredOrder(cid, rid);

        RatingsResponseDTO saved =
                ratingService.save(createDTO(oid, rid));

        String res = ratingService.delete(saved.getRatingId());

        assertTrue(res.contains("deleted"));
    }
}