package com.sprint.food_delivery.servicetest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.sprint.food_delivery.CustomersModule.Customers.CustomerRequestDTO;
import com.sprint.food_delivery.CustomersModule.Customers.CustomerResponseDTO;
import com.sprint.food_delivery.CustomersModule.Customers.ICustomerService;
import com.sprint.food_delivery.Exception.BadRequestException;
import com.sprint.food_delivery.Exception.ConflictException;
import com.sprint.food_delivery.Exception.ResourceNotFoundException;

@SpringBootTest
@Transactional
public class CustomerServiceTest {

    @Autowired
    private ICustomerService customerService;

    // ✅ 1. Save Customer - Valid
    @Test
    void testSaveCustomerSuccess() {
        CustomerRequestDTO dto = new CustomerRequestDTO();
        dto.setCustomerName("John Doe");
        dto.setCustomerEmail("john" + System.currentTimeMillis() + "@mail.com");
        dto.setCustomerPhone("9999999999");

        CustomerResponseDTO response = customerService.save(dto);

        assertNotNull(response);
        assertEquals(dto.getCustomerName(), response.getCustomerName());
    }

    // ❌ 2. Save Customer - Empty Name
    @Test
    void testSaveCustomerEmptyName() {
        CustomerRequestDTO dto = new CustomerRequestDTO();
        dto.setCustomerName("");
        dto.setCustomerEmail("test@mail.com");
        dto.setCustomerPhone("9999999999");

        assertThrows(BadRequestException.class, () -> customerService.save(dto));
    }

    // ❌ 3. Save Customer - Empty Email
    @Test
    void testSaveCustomerEmptyEmail() {
        CustomerRequestDTO dto = new CustomerRequestDTO();
        dto.setCustomerName("Test");
        dto.setCustomerEmail("");
        dto.setCustomerPhone("9999999999");

        assertThrows(BadRequestException.class, () -> customerService.save(dto));
    }

    // ❌ 4. Save Customer - Empty Phone
    @Test
    void testSaveCustomerEmptyPhone() {
        CustomerRequestDTO dto = new CustomerRequestDTO();
        dto.setCustomerName("Test");
        dto.setCustomerEmail("test@mail.com");
        dto.setCustomerPhone("");

        assertThrows(BadRequestException.class, () -> customerService.save(dto));
    }

    // ❌ 5. Save Customer - Null Name
    @Test
    void testSaveCustomerNullName() {
        CustomerRequestDTO dto = new CustomerRequestDTO();
        dto.setCustomerName(null);
        dto.setCustomerEmail("test@mail.com");
        dto.setCustomerPhone("9999999999");

        assertThrows(BadRequestException.class, () -> customerService.save(dto));
    }

    // ❌ 6. Save Customer - Null Email
    @Test
    void testSaveCustomerNullEmail() {
        CustomerRequestDTO dto = new CustomerRequestDTO();
        dto.setCustomerName("Test");
        dto.setCustomerEmail(null);
        dto.setCustomerPhone("9999999999");

        assertThrows(BadRequestException.class, () -> customerService.save(dto));
    }

    // ❌ 7. Save Customer - Null Phone
    @Test
    void testSaveCustomerNullPhone() {
        CustomerRequestDTO dto = new CustomerRequestDTO();
        dto.setCustomerName("Test");
        dto.setCustomerEmail("test@mail.com");
        dto.setCustomerPhone(null);

        assertThrows(BadRequestException.class, () -> customerService.save(dto));
    }

    // ❌ 8. Save Customer - Duplicate Email
    @Test
    void testSaveCustomerDuplicateEmail() {
        String email = "dup" + System.currentTimeMillis() + "@mail.com";

        CustomerRequestDTO dto1 = new CustomerRequestDTO();
        dto1.setCustomerName("User1");
        dto1.setCustomerEmail(email);
        dto1.setCustomerPhone("1111111111");

        customerService.save(dto1);

        CustomerRequestDTO dto2 = new CustomerRequestDTO();
        dto2.setCustomerName("User2");
        dto2.setCustomerEmail(email);
        dto2.setCustomerPhone("2222222222");

        assertThrows(ConflictException.class, () -> customerService.save(dto2));
    }

    // ✅ 9. Get All Customers
    @Test
    void testGetAllCustomers() {
        List<CustomerResponseDTO> list = customerService.getAll();
        assertNotNull(list);
    }

    // ✅ 10. Find By ID - Valid
    @Test
    void testFindByIdSuccess() {
        CustomerRequestDTO dto = new CustomerRequestDTO();
        dto.setCustomerName("FindUser");
        dto.setCustomerEmail("find" + System.currentTimeMillis() + "@mail.com");
        dto.setCustomerPhone("9999999999");

        CustomerResponseDTO saved = customerService.save(dto);

        CustomerResponseDTO found = customerService.findById(saved.getCustomerId());

        assertEquals(saved.getCustomerId(), found.getCustomerId());
    }

    // ❌ 11. Find By ID - Not Found
    @Test
    void testFindByIdNotFound() {
        assertThrows(ResourceNotFoundException.class, () -> customerService.findById(99999));
    }

    // ✅ 12. Update Customer - Valid
    @Test
    void testUpdateCustomerSuccess() {
        CustomerRequestDTO dto = new CustomerRequestDTO();
        dto.setCustomerName("Before");
        dto.setCustomerEmail("update" + System.currentTimeMillis() + "@mail.com");
        dto.setCustomerPhone("9999999999");

        CustomerResponseDTO saved = customerService.save(dto);

        CustomerRequestDTO update = new CustomerRequestDTO();
        update.setCustomerName("After");
        update.setCustomerEmail(saved.getCustomerEmail());
        update.setCustomerPhone("8888888888");

        CustomerResponseDTO updated = customerService.update(saved.getCustomerId(), update);

        assertEquals("After", updated.getCustomerName());
    }

    // ❌ 13. Update Customer - Not Found
    @Test
    void testUpdateCustomerNotFound() {
        CustomerRequestDTO dto = new CustomerRequestDTO();
        dto.setCustomerName("Test");
        dto.setCustomerEmail("test@mail.com");
        dto.setCustomerPhone("9999999999");

        assertThrows(ResourceNotFoundException.class, () -> customerService.update(9999, dto));
    }

    // ❌ 14. Update Customer - Duplicate Email
    @Test
    void testUpdateCustomerDuplicateEmail() {
        String email1 = "u1" + System.currentTimeMillis() + "@mail.com";
        String email2 = "u2" + System.currentTimeMillis() + "@mail.com";

        CustomerRequestDTO dto1 = new CustomerRequestDTO();
        dto1.setCustomerName("User1");
        dto1.setCustomerEmail(email1);
        dto1.setCustomerPhone("1111111111");

        CustomerRequestDTO dto2 = new CustomerRequestDTO();
        dto2.setCustomerName("User2");
        dto2.setCustomerEmail(email2);
        dto2.setCustomerPhone("2222222222");

        CustomerResponseDTO c1 = customerService.save(dto1);
        customerService.save(dto2);

        CustomerRequestDTO update = new CustomerRequestDTO();
        update.setCustomerName("Updated");
        update.setCustomerEmail(email2);
        update.setCustomerPhone("9999999999");

        assertThrows(ConflictException.class, () ->
                customerService.update(c1.getCustomerId(), update));
    }

    // ✅ 15. Delete Customer - Valid
    @Test
    void testDeleteCustomerSuccess() {
        CustomerRequestDTO dto = new CustomerRequestDTO();
        dto.setCustomerName("DeleteMe");
        dto.setCustomerEmail("delete" + System.currentTimeMillis() + "@mail.com");
        dto.setCustomerPhone("9999999999");

        CustomerResponseDTO saved = customerService.save(dto);

        String result = customerService.delete(saved.getCustomerId());

        assertNull(result);
    }

    // ❌ 16. Delete Customer - Not Found
    @Test
    void testDeleteCustomerNotFound() {
        assertThrows(ResourceNotFoundException.class, () -> customerService.delete(9999));
    }
}