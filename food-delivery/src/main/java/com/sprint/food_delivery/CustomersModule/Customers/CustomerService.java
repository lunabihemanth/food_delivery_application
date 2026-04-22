package com.sprint.food_delivery.CustomersModule.Customers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sprint.food_delivery.Exception.BadRequestException;
import com.sprint.food_delivery.Exception.ConflictException;
import com.sprint.food_delivery.Exception.ResourceNotFoundException;

@Service
public class CustomerService implements ICustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    // CREATE
    @Override
    public CustomerResponseDTO save(CustomerRequestDTO dto) {

        // Validation
        if (dto.getCustomerName() == null || dto.getCustomerName().isBlank()) {
            throw new BadRequestException("Customer name cannot be empty");
        }

        if (dto.getCustomerEmail() == null || dto.getCustomerEmail().isBlank()) {
            throw new BadRequestException("Email cannot be empty");
        }

        if (dto.getCustomerPhone() == null || dto.getCustomerPhone().isBlank()) {
            throw new BadRequestException("Phone cannot be empty");
        }

        //Email must be unique
        if (customerRepository.existsByCustomerEmail(dto.getCustomerEmail())) {
            throw new ConflictException("Email already exists");
        }

        Customers customer = new Customers();
        customer.setCustomerName(dto.getCustomerName());
        customer.setCustomerEmail(dto.getCustomerEmail());
        customer.setCustomerPhone(dto.getCustomerPhone());

        return mapToDTO(customerRepository.save(customer));
    }

    // GET ALL
    @Override
    public List<CustomerResponseDTO> getAll() {
        return customerRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // GET BY ID
    @Override
    public CustomerResponseDTO findById(Integer id) {

        Customers customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        return mapToDTO(customer);
    }

    // UPDATE
    @Override
    public CustomerResponseDTO update(Integer id, CustomerRequestDTO dto) {

        Customers existing = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        // prevent duplicate email
        if (!existing.getCustomerEmail().equals(dto.getCustomerEmail()) &&
                customerRepository.existsByCustomerEmail(dto.getCustomerEmail())) {
            throw new ConflictException("Email already exists");
        }

        existing.setCustomerName(dto.getCustomerName());
        existing.setCustomerEmail(dto.getCustomerEmail());
        existing.setCustomerPhone(dto.getCustomerPhone());

        return mapToDTO(customerRepository.save(existing));
    }

    // DELETE
    @Override
    public String delete(Integer id) {

        if (!customerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Customer not found");
        }

        customerRepository.deleteById(id);
        return null;
    }

    // MAPPER
    private CustomerResponseDTO mapToDTO(Customers c) {
        return new CustomerResponseDTO(
                c.getCustomerId(),
                c.getCustomerName(),
                c.getCustomerEmail(),
                c.getCustomerPhone()
        );
    }
}