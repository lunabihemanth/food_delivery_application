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

import com.sprint.food_delivery.Exception.BadRequestException;
import com.sprint.food_delivery.Exception.ResourceNotFoundException;
import com.sprint.food_delivery.RestaurantsModule.MenuItems.IMenuItemsService;
import com.sprint.food_delivery.RestaurantsModule.MenuItems.MenuItemsRequestDTO;
import com.sprint.food_delivery.RestaurantsModule.MenuItems.MenuItemsResponseDTO;
import com.sprint.food_delivery.RestaurantsModule.Restaurants.IRestaurantsService;
import com.sprint.food_delivery.RestaurantsModule.Restaurants.RestaurantResponseDTO;
import com.sprint.food_delivery.RestaurantsModule.Restaurants.RestaurantsRequestDTO;

@SpringBootTest
@Transactional
public class MenuItemsServiceTest {

    @Autowired
    private IMenuItemsService menuService;

    @Autowired
    private IRestaurantsService Restaurantservice;

    // 🔧 Helper → Always creates UNIQUE restaurant
    private Integer createRestaurant() {
        RestaurantsRequestDTO dto = new RestaurantsRequestDTO();
        dto.setRestaurantName("Rest_" + System.nanoTime()); // 🔥 no duplicate ever
        dto.setRestaurantAddress("Mumbai");
        dto.setRestaurantPhone("9" + System.nanoTime());

        RestaurantResponseDTO res = Restaurantservice.save(dto);

        assertNotNull(res);
        return res.getRestaurantId();
    }

    // 🔧 Helper → Valid DTO
    private MenuItemsRequestDTO createDTO(Integer restaurantId) {
        MenuItemsRequestDTO dto = new MenuItemsRequestDTO();
        dto.setItemName("Pizza");
        dto.setItemDescription("Cheese Pizza");
        dto.setItemPrice(250.0);
        dto.setRestaurantId(restaurantId);
        return dto;
    }

    // ✅ 1. Save - Valid
    @Test
    void testSaveMenuItemsuccess() {
        Integer rid = createRestaurant();

        MenuItemsResponseDTO res = menuService.save(createDTO(rid));

        assertNotNull(res);
        assertEquals("Pizza", res.getItemName());
    }

    // ❌ 2. Save - Null Name
    @Test
    void testSaveMenuItemNullName() {
        Integer rid = createRestaurant();

        MenuItemsRequestDTO dto = createDTO(rid);
        dto.setItemName(null);

        assertThrows(BadRequestException.class, () -> menuService.save(dto));
    }

    // ❌ 3. Save - Empty Name
    @Test
    void testSaveMenuItemEmptyName() {
        Integer rid = createRestaurant();

        MenuItemsRequestDTO dto = createDTO(rid);
        dto.setItemName("");

        assertThrows(BadRequestException.class, () -> menuService.save(dto));
    }

    // ❌ 4. Save - Price Zero
    @Test
    void testSaveMenuItemZeroPrice() {
        Integer rid = createRestaurant();

        MenuItemsRequestDTO dto = createDTO(rid);
        dto.setItemPrice(0.0);

        assertThrows(BadRequestException.class, () -> menuService.save(dto));
    }

    // ❌ 5. Save - Negative Price
    @Test
    void testSaveMenuItemNegativePrice() {
        Integer rid = createRestaurant();

        MenuItemsRequestDTO dto = createDTO(rid);
        dto.setItemPrice(-50.0);

        assertThrows(BadRequestException.class, () -> menuService.save(dto));
    }

    // ❌ 6. Save - Restaurant Not Found
    @Test
    void testSaveMenuItemRestaurantNotFound() {
        MenuItemsRequestDTO dto = createDTO(99999);

        assertThrows(ResourceNotFoundException.class,
                () -> menuService.save(dto));
    }

    // ✅ 7. Get All
    @Test
    void testGetAllMenuItems() {
        List<MenuItemsResponseDTO> list = menuService.getAll();
        assertNotNull(list);
    }

    // ✅ 8. Find By ID - Valid
    @Test
    void testFindByIdSuccess() {
        Integer rid = createRestaurant();
        MenuItemsResponseDTO saved = menuService.save(createDTO(rid));

        MenuItemsResponseDTO found = menuService.findById(saved.getItemId());

        assertEquals(saved.getItemId(), found.getItemId());
    }

    // ❌ 9. Find By ID - Not Found
    @Test
    void testFindByIdNotFound() {
        assertThrows(ResourceNotFoundException.class,
                () -> menuService.findById(99999));
    }

    // ✅ 10. Get By Restaurant - Valid
    @Test
    void testGetByRestaurantIdSuccess() {
        Integer rid = createRestaurant();
        menuService.save(createDTO(rid));

        List<MenuItemsResponseDTO> list =
                menuService.getByRestaurantId(rid);

        assertFalse(list.isEmpty());
    }

    // ❌ 11. Get By Restaurant - Not Found
    @Test
    void testGetByRestaurantIdNotFound() {
        assertThrows(ResourceNotFoundException.class,
                () -> menuService.getByRestaurantId(99999));
    }

    // ✅ 12. Update - Valid
    @Test
    void testUpdateMenuItemsuccess() {
        Integer rid = createRestaurant();
        MenuItemsResponseDTO saved = menuService.save(createDTO(rid));

        MenuItemsRequestDTO update = new MenuItemsRequestDTO();
        update.setItemName("Burger");
        update.setItemDescription("Veg Burger");
        update.setItemPrice(150.0);
        update.setRestaurantId(rid);

        MenuItemsResponseDTO updated =
                menuService.update(saved.getItemId(), update);

        assertEquals("Burger", updated.getItemName());
    }

    // ❌ 13. Update - Menu Item Not Found (IMPORTANT ORDER CASE)
    @Test
    void testUpdateMenuItemNotFound() {
        Integer rid = createRestaurant();
        MenuItemsRequestDTO dto = createDTO(rid);

        assertThrows(ResourceNotFoundException.class,
                () -> menuService.update(99999, dto)); // item checked FIRST
    }

    // ❌ 14. Update - Restaurant Not Found
    @Test
    void testUpdateMenuItemRestaurantNotFound() {
        Integer rid = createRestaurant();
        MenuItemsResponseDTO saved = menuService.save(createDTO(rid));

        MenuItemsRequestDTO update = createDTO(99999);

        assertThrows(ResourceNotFoundException.class,
                () -> menuService.update(saved.getItemId(), update));
    }

    // ❌ 15. Update - Invalid Price
    @Test
    void testUpdateMenuItemInvalidPrice() {
        Integer rid = createRestaurant();
        MenuItemsResponseDTO saved = menuService.save(createDTO(rid));

        MenuItemsRequestDTO update = createDTO(rid);
        update.setItemPrice(0.0);

        assertThrows(BadRequestException.class,
                () -> menuService.update(saved.getItemId(), update));
    }

    // ✅ 16. Delete - Valid
    @Test
    void testDeleteMenuItemsuccess() {
        Integer rid = createRestaurant();
        MenuItemsResponseDTO saved = menuService.save(createDTO(rid));

        String result = menuService.delete(saved.getItemId());

        assertTrue(result.contains("deleted"));
    }

    // ❌ 17. Delete - Not Found
    @Test
    void testDeleteMenuItemNotFound() {
        assertThrows(ResourceNotFoundException.class,
                () -> menuService.delete(99999));
    }
}