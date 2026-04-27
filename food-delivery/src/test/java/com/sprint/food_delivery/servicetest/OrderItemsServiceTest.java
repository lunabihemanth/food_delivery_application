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
import com.sprint.food_delivery.Exception.BadRequestException;
import com.sprint.food_delivery.Exception.ResourceNotFoundException;
import com.sprint.food_delivery.OrderModule.OrderItems.IOrderItemsService;
import com.sprint.food_delivery.OrderModule.OrderItems.OrderItemsRequestDTO;
import com.sprint.food_delivery.OrderModule.OrderItems.OrderItemsResponseDTO;
import com.sprint.food_delivery.OrderModule.Orders.IOrdersService;
import com.sprint.food_delivery.OrderModule.Orders.OrdersRequestDTO;
import com.sprint.food_delivery.RestaurantsModule.MenuItems.IMenuItemsService;
import com.sprint.food_delivery.RestaurantsModule.MenuItems.MenuItemsRequestDTO;
import com.sprint.food_delivery.RestaurantsModule.Restaurants.IRestaurantsService;
import com.sprint.food_delivery.RestaurantsModule.Restaurants.RestaurantsRequestDTO;

@SpringBootTest
@Transactional
public class OrderItemsServiceTest {

    @Autowired
    private IOrderItemsService orderItemsService;

    @Autowired
    private IOrdersService orderService;

    @Autowired
    private IMenuItemsService menuService;

    @Autowired
    private IRestaurantsService Restaurantservice;

    @Autowired
    private ICustomerService customerService;

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

    private Integer createMenuItem(Integer rid) {
        MenuItemsRequestDTO dto = new MenuItemsRequestDTO();
        dto.setItemName("Pizza");
        dto.setItemDescription("Cheese");
        dto.setItemPrice(200.0);
        dto.setRestaurantId(rid);
        return menuService.save(dto).getItemId();
    }

    private Integer createOrder(Integer cid, Integer rid) {
        OrdersRequestDTO dto = new OrdersRequestDTO();
        dto.setCustomerId(cid);
        dto.setRestaurantId(rid);
        return orderService.save(dto).getOrderId();
    }

    private OrderItemsRequestDTO createDTO(Integer orderId, Integer itemId) {
        OrderItemsRequestDTO dto = new OrderItemsRequestDTO();
        dto.setOrderId(orderId);
        dto.setItemId(itemId);
        dto.setQuantity(2);
        return dto;
    }

    // ✅ 1. Save - Valid
    @Test
    void testSaveOrderItemSuccess() {
        Integer rid = createRestaurant();
        Integer cid = createCustomer();
        Integer orderId = createOrder(cid, rid);
        Integer itemId = createMenuItem(rid);

        OrderItemsResponseDTO res =
                orderItemsService.save(createDTO(orderId, itemId));

        assertNotNull(res);
        assertEquals(2, res.getQuantity());
    }

    // ❌ 2. Save - Quantity Null
    @Test
    void testSaveQuantityNull() {
        Integer rid = createRestaurant();
        Integer cid = createCustomer();
        Integer orderId = createOrder(cid, rid);
        Integer itemId = createMenuItem(rid);

        OrderItemsRequestDTO dto = createDTO(orderId, itemId);
        dto.setQuantity(null);

        assertThrows(BadRequestException.class,
                () -> orderItemsService.save(dto));
    }

    // ❌ 3. Save - Quantity Zero
    @Test
    void testSaveQuantityZero() {
        Integer rid = createRestaurant();
        Integer cid = createCustomer();
        Integer orderId = createOrder(cid, rid);
        Integer itemId = createMenuItem(rid);

        OrderItemsRequestDTO dto = createDTO(orderId, itemId);
        dto.setQuantity(0);

        assertThrows(BadRequestException.class,
                () -> orderItemsService.save(dto));
    }

    // ❌ 4. Save - Order Not Found
    @Test
    void testSaveOrderNotFound() {
        Integer rid = createRestaurant();
        Integer itemId = createMenuItem(rid);

        OrderItemsRequestDTO dto = createDTO(9999, itemId);

        assertThrows(ResourceNotFoundException.class,
                () -> orderItemsService.save(dto));
    }

    // ❌ 5. Save - Menu Item Not Found
    @Test
    void testSaveMenuItemNotFound() {
        Integer rid = createRestaurant();
        Integer cid = createCustomer();
        Integer orderId = createOrder(cid, rid);

        OrderItemsRequestDTO dto = createDTO(orderId, 9999);

        assertThrows(ResourceNotFoundException.class,
                () -> orderItemsService.save(dto));
    }

    // ❌ 6. Save - Different Restaurant
    @Test
    void testSaveDifferentRestaurant() {
        Integer rid1 = createRestaurant();
        Integer rid2 = createRestaurant();

        Integer cid = createCustomer();
        Integer orderId = createOrder(cid, rid1);
        Integer itemId = createMenuItem(rid2); // ❌ different restaurant

        OrderItemsRequestDTO dto = createDTO(orderId, itemId);

        assertThrows(BadRequestException.class,
                () -> orderItemsService.save(dto));
    }

    // ✅ 7. Get All
    @Test
    void testGetAll() {
        List<OrderItemsResponseDTO> list = orderItemsService.getAll();
        assertNotNull(list);
    }

    // ✅ 8. Find By ID - Valid
    @Test
    void testFindByIdSuccess() {
        Integer rid = createRestaurant();
        Integer cid = createCustomer();
        Integer orderId = createOrder(cid, rid);
        Integer itemId = createMenuItem(rid);

        OrderItemsResponseDTO saved =
                orderItemsService.save(createDTO(orderId, itemId));

        OrderItemsResponseDTO found =
                orderItemsService.findById(saved.getOrderItemId());

        assertEquals(saved.getOrderItemId(), found.getOrderItemId());
    }

    // ❌ 9. Find By ID - Not Found
    @Test
    void testFindByIdNotFound() {
        assertThrows(ResourceNotFoundException.class,
                () -> orderItemsService.findById(9999));
    }

    // ✅ 10. Get By Order ID
    @Test
    void testGetByOrderIdSuccess() {
        Integer rid = createRestaurant();
        Integer cid = createCustomer();
        Integer orderId = createOrder(cid, rid);
        Integer itemId = createMenuItem(rid);

        orderItemsService.save(createDTO(orderId, itemId));

        List<OrderItemsResponseDTO> list =
                orderItemsService.getByOrderId(orderId);

        assertFalse(list.isEmpty());
    }

    // ❌ 11. Get By Order ID - Not Found
    @Test
    void testGetByOrderIdNotFound() {
        assertThrows(ResourceNotFoundException.class,
                () -> orderItemsService.getByOrderId(9999));
    }

    // ✅ 12. Update - Valid
    @Test
    void testUpdateSuccess() {
        Integer rid = createRestaurant();
        Integer cid = createCustomer();
        Integer orderId = createOrder(cid, rid);
        Integer itemId = createMenuItem(rid);

        OrderItemsResponseDTO saved =
                orderItemsService.save(createDTO(orderId, itemId));

        OrderItemsRequestDTO update = createDTO(orderId, itemId);
        update.setQuantity(5);

        OrderItemsResponseDTO updated =
                orderItemsService.update(saved.getOrderItemId(), update);

        assertEquals(5, updated.getQuantity());
    }

    // ❌ 13. Update - Not Found
    @Test
    void testUpdateNotFound() {
        Integer rid = createRestaurant();
        Integer cid = createCustomer();
        Integer itemId = createMenuItem(rid);

        OrderItemsRequestDTO dto = createDTO(9999, itemId);

        assertThrows(ResourceNotFoundException.class,
                () -> orderItemsService.update(9999, dto));
    }

    // ❌ 14. Update - Different Restaurant
    @Test
    void testUpdateDifferentRestaurant() {
        Integer rid1 = createRestaurant();
        Integer rid2 = createRestaurant();

        Integer cid = createCustomer();
        Integer orderId = createOrder(cid, rid1);
        Integer itemId1 = createMenuItem(rid1);
        Integer itemId2 = createMenuItem(rid2);

        OrderItemsResponseDTO saved =
                orderItemsService.save(createDTO(orderId, itemId1));

        OrderItemsRequestDTO update = createDTO(orderId, itemId2);

        assertThrows(BadRequestException.class,
                () -> orderItemsService.update(saved.getOrderItemId(), update));
    }

    // ✅ 15. Delete - Valid
    @Test
    void testDeleteSuccess() {
        Integer rid = createRestaurant();
        Integer cid = createCustomer();
        Integer orderId = createOrder(cid, rid);
        Integer itemId = createMenuItem(rid);

        OrderItemsResponseDTO saved =
                orderItemsService.save(createDTO(orderId, itemId));

        String result = orderItemsService.delete(saved.getOrderItemId());

        assertTrue(result.contains("deleted"));
    }

    // ❌ 16. Delete - Not Found
    @Test
    void testDeleteNotFound() {
        assertThrows(ResourceNotFoundException.class,
                () -> orderItemsService.delete(9999));
    }
}