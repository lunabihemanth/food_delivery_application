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
import com.sprint.food_delivery.CustomersModule.Customers.CustomerResponseDTO;
import com.sprint.food_delivery.CustomersModule.Customers.ICustomerService;
import com.sprint.food_delivery.CustomersModule.DeliveryAddress.DeliveryAddressRequestDTO;
import com.sprint.food_delivery.CustomersModule.DeliveryAddress.DeliveryAddressResponseDTO;
import com.sprint.food_delivery.CustomersModule.DeliveryAddress.IDeliveryAddressService;
import com.sprint.food_delivery.Exception.BadRequestException;
import com.sprint.food_delivery.Exception.ConflictException;
import com.sprint.food_delivery.Exception.CustomerNotFoundException;
import com.sprint.food_delivery.Exception.DeliveryAddressNotFoundException;


@SpringBootTest
@Transactional
public class DeliveryAddressServiceTest {

    @Autowired
    private IDeliveryAddressService addressService;

    @Autowired
    private ICustomerService customerService;

    // 🔧 Helper to create customer
    private Integer createCustomer() {
        CustomerRequestDTO dto = new CustomerRequestDTO();
        dto.setCustomerName("Test User");
        dto.setCustomerEmail("user" + System.currentTimeMillis() + "@mail.com");
        dto.setCustomerPhone("9999999999");

        CustomerResponseDTO res = customerService.save(dto);
        return res.getCustomerId();
    }

    // ✅ 1. Save Address - Valid
    @Test
    void testSaveAddressSuccess() {
        Integer customerId = createCustomer();

        DeliveryAddressRequestDTO dto = new DeliveryAddressRequestDTO();
        dto.setAddressLine1("Street 1");
        dto.setAddressLine2("Apt 101");
        dto.setCity("Mumbai");
        dto.setState("MH");
        dto.setPostalCode("400001");
        dto.setCustomerId(customerId);

        DeliveryAddressResponseDTO res = addressService.save(dto);

        assertNotNull(res.getAddressId());
    }

    // ❌ 2. Save Address - Customer Not Found
    @Test
    void testSaveAddressCustomerNotFound() {
        DeliveryAddressRequestDTO dto = new DeliveryAddressRequestDTO();
        dto.setAddressLine1("Street");
        dto.setCity("Mumbai");
        dto.setState("MH");
        dto.setPostalCode("400001");
        dto.setCustomerId(9999);

        assertThrows(CustomerNotFoundException.class, () -> addressService.save(dto));
    }

    // ❌ 3. Save Address - Duplicate Address
    @Test
    void testSaveAddressDuplicate() {
        Integer customerId = createCustomer();

        DeliveryAddressRequestDTO dto = new DeliveryAddressRequestDTO();
        dto.setAddressLine1("Same Street");
        dto.setCity("Mumbai");
        dto.setState("MH");
        dto.setPostalCode("400001");
        dto.setCustomerId(customerId);

        addressService.save(dto);

        assertThrows(ConflictException.class, () -> addressService.save(dto));
    }

    // ❌ 4. Save Address - Empty AddressLine1
    @Test
    void testSaveAddressEmptyLine1() {
        Integer customerId = createCustomer();

        DeliveryAddressRequestDTO dto = new DeliveryAddressRequestDTO();
        dto.setAddressLine1("");
        dto.setCity("Mumbai");
        dto.setState("MH");
        dto.setPostalCode("400001");
        dto.setCustomerId(customerId);

        assertThrows(BadRequestException.class, () -> addressService.save(dto));
    }

    // ❌ 5. Save Address - Null City
    @Test
    void testSaveAddressNullCity() {
        Integer customerId = createCustomer();

        DeliveryAddressRequestDTO dto = new DeliveryAddressRequestDTO();
        dto.setAddressLine1("Street");
        dto.setCity(null);
        dto.setState("MH");
        dto.setPostalCode("400001");
        dto.setCustomerId(customerId);

        assertThrows(BadRequestException.class, () -> addressService.save(dto));
    }

    // ❌ 6. Save Address - Empty State
    @Test
    void testSaveAddressEmptyState() {
        Integer customerId = createCustomer();

        DeliveryAddressRequestDTO dto = new DeliveryAddressRequestDTO();
        dto.setAddressLine1("Street");
        dto.setCity("Mumbai");
        dto.setState("");
        dto.setPostalCode("400001");
        dto.setCustomerId(customerId);

        assertThrows(BadRequestException.class, () -> addressService.save(dto));
    }

    // ❌ 7. Save Address - Empty Postal Code
    @Test
    void testSaveAddressEmptyPostal() {
        Integer customerId = createCustomer();

        DeliveryAddressRequestDTO dto = new DeliveryAddressRequestDTO();
        dto.setAddressLine1("Street");
        dto.setCity("Mumbai");
        dto.setState("MH");
        dto.setPostalCode("");
        dto.setCustomerId(customerId);

        assertThrows(BadRequestException.class, () -> addressService.save(dto));
    }

    // ✅ 8. Get All Addresses
    @Test
    void testGetAllAddresses() {
        List<DeliveryAddressResponseDTO> list = addressService.getAll();
        assertNotNull(list);
    }

    // ✅ 9. Find By ID - Valid
    @Test
    void testFindByIdSuccess() {
        Integer customerId = createCustomer();

        DeliveryAddressRequestDTO dto = new DeliveryAddressRequestDTO();
        dto.setAddressLine1("Street");
        dto.setCity("Mumbai");
        dto.setState("MH");
        dto.setPostalCode("400001");
        dto.setCustomerId(customerId);

        DeliveryAddressResponseDTO saved = addressService.save(dto);

        DeliveryAddressResponseDTO found = addressService.findById(saved.getAddressId());

        assertEquals(saved.getAddressId(), found.getAddressId());
    }

    // ❌ 10. Find By ID - Not Found
    @Test
    void testFindByIdNotFound() {
        assertThrows(DeliveryAddressNotFoundException.class,
                () -> addressService.findById(9999));
    }

    // ✅ 11. Get By Customer ID - Valid
    @Test
    void testGetByCustomerIdSuccess() {
        Integer customerId = createCustomer();

        DeliveryAddressRequestDTO dto = new DeliveryAddressRequestDTO();
        dto.setAddressLine1("Street");
        dto.setCity("Mumbai");
        dto.setState("MH");
        dto.setPostalCode("400001");
        dto.setCustomerId(customerId);

        addressService.save(dto);

        List<DeliveryAddressResponseDTO> list =
                addressService.getByCustomerId(customerId);

        assertFalse(list.isEmpty());
    }

    // ❌ 12. Get By Customer ID - Not Found
    @Test
    void testGetByCustomerIdNotFound() {
        assertThrows(CustomerNotFoundException.class,
                () -> addressService.getByCustomerId(9999));
    }

    // ✅ 13. Update Address - Valid
    @Test
    void testUpdateAddressSuccess() {
        Integer customerId = createCustomer();

        DeliveryAddressRequestDTO dto = new DeliveryAddressRequestDTO();
        dto.setAddressLine1("Old");
        dto.setCity("Mumbai");
        dto.setState("MH");
        dto.setPostalCode("400001");
        dto.setCustomerId(customerId);

        DeliveryAddressResponseDTO saved = addressService.save(dto);

        DeliveryAddressRequestDTO update = new DeliveryAddressRequestDTO();
        update.setAddressLine1("New");
        update.setCity("Mumbai");
        update.setState("MH");
        update.setPostalCode("400002");
        update.setCustomerId(customerId);

        DeliveryAddressResponseDTO updated =
                addressService.update(saved.getAddressId(), update);

        assertEquals("New", updated.getAddressLine1());
    }

    // ❌ 14. Update Address - Not Found
    @Test
    void testUpdateAddressNotFound() {
        DeliveryAddressRequestDTO dto = new DeliveryAddressRequestDTO();
        dto.setAddressLine1("Test");
        dto.setCity("Mumbai");
        dto.setState("MH");
        dto.setPostalCode("400001");
        dto.setCustomerId(createCustomer());

        assertThrows(DeliveryAddressNotFoundException.class,
                () -> addressService.update(9999, dto));
    }

    // ❌ 15. Update Address - Customer Not Found
    @Test
    void testUpdateAddressCustomerNotFound() {
        Integer customerId = createCustomer();

        DeliveryAddressRequestDTO dto = new DeliveryAddressRequestDTO();
        dto.setAddressLine1("Street");
        dto.setCity("Mumbai");
        dto.setState("MH");
        dto.setPostalCode("400001");
        dto.setCustomerId(customerId);

        DeliveryAddressResponseDTO saved = addressService.save(dto);

        dto.setCustomerId(9999);

        assertThrows(CustomerNotFoundException.class,
                () -> addressService.update(saved.getAddressId(), dto));
    }

    // ✅ 16. Delete Address - Valid
    @Test
    void testDeleteAddressSuccess() {
        Integer customerId = createCustomer();

        DeliveryAddressRequestDTO dto = new DeliveryAddressRequestDTO();
        dto.setAddressLine1("Delete");
        dto.setCity("Mumbai");
        dto.setState("MH");
        dto.setPostalCode("400001");
        dto.setCustomerId(customerId);

        DeliveryAddressResponseDTO saved = addressService.save(dto);

        String result = addressService.delete(saved.getAddressId());

        assertTrue(result.contains("deleted"));
    }

    // ❌ 17. Delete Address - Not Found
    @Test
    void testDeleteAddressNotFound() {
        assertThrows(DeliveryAddressNotFoundException.class,
                () -> addressService.delete(9999));
    }
}