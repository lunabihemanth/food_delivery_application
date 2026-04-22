package com.sprint.food_delivery.CustomersModule.DeliveryAddress;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sprint.food_delivery.CustomersModule.Customers.CustomerRepository;
import com.sprint.food_delivery.CustomersModule.Customers.Customers;
import com.sprint.food_delivery.Exception.BadRequestException;
import com.sprint.food_delivery.Exception.ConflictException;
import com.sprint.food_delivery.Exception.CustomerNotFoundException;
import com.sprint.food_delivery.Exception.DeliveryAddressNotFoundException;

@Service
public class DeliveryAddressService implements IDeliveryAddressService {

    @Autowired
    private DeliveryAddressRepository deliveryAddressRepository;

    @Autowired
    private CustomerRepository customerRepository;

    // CREATE
    @Override
    public DeliveryAddressResponseDTO save(DeliveryAddressRequestDTO dto) {

        // Validation
        validate(dto);

        Customers customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new CustomerNotFoundException(dto.getCustomerId()));

        // prevent duplicate address for same customer
        boolean exists = deliveryAddressRepository
                .findByCustomer_CustomerId(dto.getCustomerId())
                .stream()
                .anyMatch(a ->
                        a.getAddressLine1().equalsIgnoreCase(dto.getAddressLine1()) &&
                        a.getCity().equalsIgnoreCase(dto.getCity()) &&
                        a.getPostalCode().equals(dto.getPostalCode())
                );

        if (exists) {
            throw new ConflictException("Address already exists for this customer");
        }

        DeliveryAddress address = new DeliveryAddress();
        mapToEntity(address, dto, customer);

        return mapToResponseDTO(deliveryAddressRepository.save(address));
    }

    // GET ALL
    @Override
    public List<DeliveryAddressResponseDTO> getAll() {
        return deliveryAddressRepository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    // GET BY ID
    @Override
    public DeliveryAddressResponseDTO findById(Integer id) {

        DeliveryAddress address = deliveryAddressRepository.findById(id)
                .orElseThrow(() -> new DeliveryAddressNotFoundException(id));

        return mapToResponseDTO(address);
    }

    // GET BY CUSTOMER ID
    @Override
    public List<DeliveryAddressResponseDTO> getByCustomerId(Integer customerId) {

        if (!customerRepository.existsById(customerId)) {
            throw new CustomerNotFoundException(customerId);
        }

        return deliveryAddressRepository.findByCustomer_CustomerId(customerId)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    // UPDATE
    @Override
    public DeliveryAddressResponseDTO update(Integer id, DeliveryAddressRequestDTO dto) {

        validate(dto);

        DeliveryAddress existing = deliveryAddressRepository.findById(id)
                .orElseThrow(() -> new DeliveryAddressNotFoundException(id));

        Customers customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new CustomerNotFoundException(dto.getCustomerId()));

        mapToEntity(existing, dto, customer);

        return mapToResponseDTO(deliveryAddressRepository.save(existing));
    }

    // DELETE
    @Override
    public String delete(Integer id) {

        if (!deliveryAddressRepository.existsById(id)) {
            throw new DeliveryAddressNotFoundException(id);
        }

        deliveryAddressRepository.deleteById(id);

        return "Address deleted successfully with id: " + id;
    }

    // VALIDATION METHOD
    private void validate(DeliveryAddressRequestDTO dto) {

        if (dto.getAddressLine1() == null || dto.getAddressLine1().isBlank()) {
            throw new BadRequestException("Address Line1 cannot be empty");
        }

        if (dto.getCity() == null || dto.getCity().isBlank()) {
            throw new BadRequestException("City cannot be empty");
        }

        if (dto.getState() == null || dto.getState().isBlank()) {
            throw new BadRequestException("State cannot be empty");
        }

        if (dto.getPostalCode() == null || dto.getPostalCode().isBlank()) {
            throw new BadRequestException("Postal code cannot be empty");
        }
    }

    // ENTITY MAPPER
    private void mapToEntity(DeliveryAddress address,
                            DeliveryAddressRequestDTO dto,
                            Customers customer) {

        address.setAddressLine1(dto.getAddressLine1());
        address.setAddressLine2(dto.getAddressLine2());
        address.setCity(dto.getCity());
        address.setState(dto.getState());
        address.setPostalCode(dto.getPostalCode());
        address.setCustomer(customer);
    }

    // RESPONSE MAPPER
    private DeliveryAddressResponseDTO mapToResponseDTO(DeliveryAddress address) {
        return new DeliveryAddressResponseDTO(
                address.getAddressId(),
                address.getAddressLine1(),
                address.getAddressLine2(),
                address.getCity(),
                address.getState(),
                address.getPostalCode(),
                address.getCustomer().getCustomerId()
        );
    }
}