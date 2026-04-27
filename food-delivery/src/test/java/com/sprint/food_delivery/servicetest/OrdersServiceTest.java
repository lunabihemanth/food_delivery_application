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

import com.sprint.food_delivery.CustomersModule.Customers.CustomerRequestDTO;
import com.sprint.food_delivery.CustomersModule.Customers.ICustomerService;
import com.sprint.food_delivery.DeliveryModule.DeliveryDrivers.DeliveryDriversRequestDTO;
import com.sprint.food_delivery.DeliveryModule.DeliveryDrivers.IDeliveryDriversService;
import com.sprint.food_delivery.Exception.BadRequestException;
import com.sprint.food_delivery.Exception.ResourceNotFoundException;
import com.sprint.food_delivery.OrderModule.Orders.IOrdersService;
import com.sprint.food_delivery.OrderModule.Orders.OrdersRequestDTO;
import com.sprint.food_delivery.OrderModule.Orders.OrdersResponseDTO;
import com.sprint.food_delivery.RestaurantsModule.Restaurants.IRestaurantsService;
import com.sprint.food_delivery.RestaurantsModule.Restaurants.RestaurantsRequestDTO;

@SpringBootTest
@Transactional
public class OrdersServiceTest {

    @Autowired
    private IOrdersService orderService;

    @Autowired
    private ICustomerService customerService;

    @Autowired
    private IRestaurantsService Restaurantservice;

    @Autowired
    private IDeliveryDriversService driverService;

    // Helpers
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

    private Integer createDriver() {
        DeliveryDriversRequestDTO dto = new DeliveryDriversRequestDTO();
        dto.setDriverName("Driver_" + System.nanoTime());
        dto.setDriverPhone("8" + System.nanoTime());
        dto.setDriverVehicle("Bike");

        return driverService.save(dto).getDriverId();
    }

    private OrdersRequestDTO createDTO(Integer cid, Integer rid) {
        OrdersRequestDTO dto = new OrdersRequestDTO();
        dto.setCustomerId(cid);
        dto.setRestaurantId(rid);
        return dto;
    }

    // ✅ 1. Save Order - Valid (No Driver)
    @Test
    void testSaveOrderSuccess() {
        Integer cid = createCustomer();
        Integer rid = createRestaurant();

        OrdersResponseDTO res = orderService.save(createDTO(cid, rid));

        assertNotNull(res);
        assertEquals("PENDING", res.getOrderStatus()); // 🔥 default check
    }

    // ✅ 2. Save Order - With Driver
    @Test
    void testSaveOrderWithDriver() {
        Integer cid = createCustomer();
        Integer rid = createRestaurant();
        Integer did = createDriver();

        OrdersRequestDTO dto = createDTO(cid, rid);
        dto.setDeliveryDriverId(did);

        OrdersResponseDTO res = orderService.save(dto);

        assertEquals(did, res.getDeliveryDriverId());
    }

    // ❌ 3. Save - Customer Not Found
    @Test
    void testSaveOrderCustomerNotFound() {
        Integer rid = createRestaurant();

        OrdersRequestDTO dto = createDTO(9999, rid);

        assertThrows(ResourceNotFoundException.class,
                () -> orderService.save(dto));
    }

    // ❌ 4. Save - Restaurant Not Found
    @Test
    void testSaveOrderRestaurantNotFound() {
        Integer cid = createCustomer();

        OrdersRequestDTO dto = createDTO(cid, 9999);

        assertThrows(ResourceNotFoundException.class,
                () -> orderService.save(dto));
    }

    // ❌ 5. Save - Driver Not Found
    @Test
    void testSaveOrderDriverNotFound() {
        Integer cid = createCustomer();
        Integer rid = createRestaurant();

        OrdersRequestDTO dto = createDTO(cid, rid);
        dto.setDeliveryDriverId(9999);

        assertThrows(ResourceNotFoundException.class,
                () -> orderService.save(dto));
    }

    // ✅ 6. Get All Orders
    @Test
    void testGetAllOrders() {
        List<OrdersResponseDTO> list = orderService.getAll();
        assertNotNull(list);
    }

    // ✅ 7. Find By ID - Valid
    @Test
    void testFindByIdSuccess() {
        Integer cid = createCustomer();
        Integer rid = createRestaurant();

        OrdersResponseDTO saved = orderService.save(createDTO(cid, rid));

        OrdersResponseDTO found = orderService.findById(saved.getOrderId());

        assertEquals(saved.getOrderId(), found.getOrderId());
    }

    // ❌ 8. Find By ID - Not Found
    @Test
    void testFindByIdNotFound() {
        assertThrows(ResourceNotFoundException.class,
                () -> orderService.findById(9999));
    }

    // ✅ 9. Get By Customer ID
    @Test
    void testGetByCustomerIdSuccess() {
        Integer cid = createCustomer();
        Integer rid = createRestaurant();

        orderService.save(createDTO(cid, rid));

        List<OrdersResponseDTO> list = orderService.getByCustomerId(cid);

        assertFalse(list.isEmpty());
    }

    // ❌ 10. Get By Customer ID - Not Found
    @Test
    void testGetByCustomerIdNotFound() {
        assertThrows(ResourceNotFoundException.class,
                () -> orderService.getByCustomerId(9999));
    }

    // ✅ 11. Valid Status Flow
    @Test
    void testValidStatusTransition() {
        Integer cid = createCustomer();
        Integer rid = createRestaurant();

        OrdersResponseDTO saved = orderService.save(createDTO(cid, rid));

        OrdersRequestDTO dto = new OrdersRequestDTO();
        dto.setOrderStatus("CONFIRMED");

        OrdersResponseDTO updated =
                orderService.update(saved.getOrderId(), dto);

        assertEquals("CONFIRMED", updated.getOrderStatus());
    }

    // ❌ 12. Invalid Status Flow
    @Test
    void testInvalidStatusTransition() {
        Integer cid = createCustomer();
        Integer rid = createRestaurant();

        OrdersResponseDTO saved = orderService.save(createDTO(cid, rid));

        OrdersRequestDTO dto = new OrdersRequestDTO();
        dto.setOrderStatus("DELIVERED"); // ❌ skip flow

        assertThrows(BadRequestException.class,
                () -> orderService.update(saved.getOrderId(), dto));
    }

    // ❌ 13. Update Order - Not Found
    @Test
    void testUpdateOrderNotFound() {
        OrdersRequestDTO dto = new OrdersRequestDTO();
        dto.setOrderStatus("CONFIRMED");

        assertThrows(ResourceNotFoundException.class,
                () -> orderService.update(9999, dto));
    }

    // ❌ 14. Delete Delivered Order
    @Test
    void testDeleteDeliveredOrder() {
        Integer cid = createCustomer();
        Integer rid = createRestaurant();

        OrdersResponseDTO saved = orderService.save(createDTO(cid, rid));

        // Move to DELIVERED (valid path)
        OrdersRequestDTO dto = new OrdersRequestDTO();
        dto.setOrderStatus("CONFIRMED");
        orderService.update(saved.getOrderId(), dto);

        dto.setOrderStatus("OUT_FOR_DELIVERY");
        orderService.update(saved.getOrderId(), dto);

        dto.setOrderStatus("DELIVERED");
        orderService.update(saved.getOrderId(), dto);

        // ❌ Now delete should fail
        assertThrows(BadRequestException.class,
                () -> orderService.delete(saved.getOrderId()));
    }

    // ✅ 15. Delete Order - Valid
    @Test
    void testDeleteOrderSuccess() {
        Integer cid = createCustomer();
        Integer rid = createRestaurant();

        OrdersResponseDTO saved = orderService.save(createDTO(cid, rid));

        String result = orderService.delete(saved.getOrderId());

        assertTrue(result.contains("deleted"));
    }
}