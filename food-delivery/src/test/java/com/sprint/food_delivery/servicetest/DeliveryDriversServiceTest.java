package com.sprint.food_delivery.servicetest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.sprint.food_delivery.DeliveryModule.DeliveryDrivers.DeliveryDriversRequestDTO;
import com.sprint.food_delivery.DeliveryModule.DeliveryDrivers.DeliveryDriversResponseDTO;
import com.sprint.food_delivery.DeliveryModule.DeliveryDrivers.IDeliveryDriversService;
import com.sprint.food_delivery.Exception.BadRequestException;
import com.sprint.food_delivery.Exception.ConflictException;
import com.sprint.food_delivery.Exception.ResourceNotFoundException;

@SpringBootTest
@Transactional
public class DeliveryDriversServiceTest {

    @Autowired
    private IDeliveryDriversService driverService;

    // 🔧 Helper
    private DeliveryDriversRequestDTO createDTO(String phone) {
        DeliveryDriversRequestDTO dto = new DeliveryDriversRequestDTO();
        dto.setDriverName("Driver");
        dto.setDriverPhone(phone);
        dto.setDriverVehicle("Bike");
        return dto;
    }

    // ✅ 1. Save Driver - Valid
    @Test
    void testSaveDriverSuccess() {
        DeliveryDriversRequestDTO dto =
                createDTO("9" + System.currentTimeMillis());

        DeliveryDriversResponseDTO res = driverService.save(dto);

        assertNotNull(res);
        assertEquals(dto.getDriverName(), res.getDriverName());
    }

    // ❌ 2. Save Driver - Empty Name
    @Test
    void testSaveDriverEmptyName() {
        DeliveryDriversRequestDTO dto = createDTO("1111111111");
        dto.setDriverName("");

        assertThrows(BadRequestException.class, () -> driverService.save(dto));
    }

    // ❌ 3. Save Driver - Null Name
    @Test
    void testSaveDriverNullName() {
        DeliveryDriversRequestDTO dto = createDTO("1111111112");
        dto.setDriverName(null);

        assertThrows(BadRequestException.class, () -> driverService.save(dto));
    }

    // ❌ 4. Save Driver - Empty Phone
    @Test
    void testSaveDriverEmptyPhone() {
        DeliveryDriversRequestDTO dto = createDTO("");
        assertThrows(BadRequestException.class, () -> driverService.save(dto));
    }

    // ❌ 5. Save Driver - Null Phone
    @Test
    void testSaveDriverNullPhone() {
        DeliveryDriversRequestDTO dto = createDTO(null);
        assertThrows(BadRequestException.class, () -> driverService.save(dto));
    }

    // ❌ 6. Save Driver - Empty Vehicle
    @Test
    void testSaveDriverEmptyVehicle() {
        DeliveryDriversRequestDTO dto = createDTO("1111111113");
        dto.setDriverVehicle("");

        assertThrows(BadRequestException.class, () -> driverService.save(dto));
    }

    // ❌ 7. Save Driver - Duplicate Phone
    @Test
    void testSaveDriverDuplicatePhone() {
        String phone = "9" + System.currentTimeMillis();

        driverService.save(createDTO(phone));

        assertThrows(ConflictException.class,
                () -> driverService.save(createDTO(phone)));
    }

    // ✅ 8. Get All Drivers
    @Test
    void testGetAllDrivers() {
        List<DeliveryDriversResponseDTO> list = driverService.getAll();
        assertNotNull(list);
    }

    // ✅ 9. Find By ID - Valid
    @Test
    void testFindByIdSuccess() {
        DeliveryDriversResponseDTO saved =
                driverService.save(createDTO("9" + System.currentTimeMillis()));

        DeliveryDriversResponseDTO found =
                driverService.findById(saved.getDriverId());

        assertEquals(saved.getDriverId(), found.getDriverId());
    }

    // ❌ 10. Find By ID - Not Found
    @Test
    void testFindByIdNotFound() {
        assertThrows(ResourceNotFoundException.class,
                () -> driverService.findById(9999));
    }

    // ✅ 11. Update Driver - Valid
    @Test
    void testUpdateDriverSuccess() {
        DeliveryDriversResponseDTO saved =
                driverService.save(createDTO("9" + System.currentTimeMillis()));

        DeliveryDriversRequestDTO update = new DeliveryDriversRequestDTO();
        update.setDriverName("Updated");
        update.setDriverPhone(saved.getDriverPhone());
        update.setDriverVehicle("Car");

        DeliveryDriversResponseDTO updated =
                driverService.update(saved.getDriverId(), update);

        assertEquals("Updated", updated.getDriverName());
    }

    // ❌ 12. Update Driver - Not Found
    @Test
    void testUpdateDriverNotFound() {
        DeliveryDriversRequestDTO dto = createDTO("1234567890");

        assertThrows(ResourceNotFoundException.class,
                () -> driverService.update(9999, dto));
    }

    // ❌ 13. Update Driver - Duplicate Phone
    @Test
    void testUpdateDriverDuplicatePhone() {
        String phone1 = "9" + System.currentTimeMillis();
        String phone2 = "8" + System.currentTimeMillis();

        DeliveryDriversResponseDTO d1 = driverService.save(createDTO(phone1));
        driverService.save(createDTO(phone2));

        DeliveryDriversRequestDTO update = new DeliveryDriversRequestDTO();
        update.setDriverName("Test");
        update.setDriverPhone(phone2); // duplicate
        update.setDriverVehicle("Bike");

        assertThrows(ConflictException.class,
                () -> driverService.update(d1.getDriverId(), update));
    }

    // ✅ 14. Delete Driver - Valid
    @Test
    void testDeleteDriverSuccess() {
        DeliveryDriversResponseDTO saved =
                driverService.save(createDTO("9" + System.currentTimeMillis()));

        String result = driverService.delete(saved.getDriverId());

        assertTrue(result.contains("deleted"));
    }

    // ❌ 15. Delete Driver - Not Found
    @Test
    void testDeleteDriverNotFound() {
        assertThrows(ResourceNotFoundException.class,
                () -> driverService.delete(9999));
    }
}