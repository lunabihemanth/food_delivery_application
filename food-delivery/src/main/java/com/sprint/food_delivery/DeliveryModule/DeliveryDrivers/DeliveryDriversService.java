package com.sprint.food_delivery.DeliveryModule.DeliveryDrivers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sprint.food_delivery.Exception.BadRequestException;
import com.sprint.food_delivery.Exception.ConflictException;
import com.sprint.food_delivery.Exception.ResourceNotFoundException;



@Service
public class DeliveryDriversService implements IDeliveryDriversService {

    @Autowired
    private DeliveryDriversRepository repository;

    // CREATE
    @Override
    public DeliveryDriversResponseDTO save(DeliveryDriversRequestDTO dto) {

        validate(dto);

        // phone must be unique
        if (repository.existsByDriverPhone(dto.getDriverPhone())) {
            throw new ConflictException("Driver with this phone already exists");
        }

        DeliveryDrivers d = new DeliveryDrivers();
        mapToEntity(d, dto);

        return map(repository.save(d));
    }

    // GET ALL
    @Override
    public List<DeliveryDriversResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(this::map)
                .collect(Collectors.toList());
    }

    // GET BY ID
    @Override
    public DeliveryDriversResponseDTO findById(Integer id) {

        DeliveryDrivers driver = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with id: " + id));

        return map(driver);
    }

    // UPDATE
    @Override
    public DeliveryDriversResponseDTO update(Integer id, DeliveryDriversRequestDTO dto) {

        validate(dto);

        DeliveryDrivers existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with id: " + id));

        // prevent duplicate phone
        if (!existing.getDriverPhone().equals(dto.getDriverPhone()) &&
                repository.existsByDriverPhone(dto.getDriverPhone())) {
            throw new ConflictException("Driver phone already exists");
        }

        mapToEntity(existing, dto);

        return map(repository.save(existing));
    }

    // DELETE
    @Override
    public String delete(Integer id) {

        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Driver not found with id: " + id);
        }

        repository.deleteById(id);
        return "Driver deleted successfully";
    }

    // VALIDATION
    private void validate(DeliveryDriversRequestDTO dto) {

        if (dto.getDriverName() == null || dto.getDriverName().isBlank()) {
            throw new BadRequestException("Driver name cannot be empty");
        }

        if (dto.getDriverPhone() == null || dto.getDriverPhone().isBlank()) {
            throw new BadRequestException("Driver phone cannot be empty");
        }

        if (dto.getDriverVehicle() == null || dto.getDriverVehicle().isBlank()) {
            throw new BadRequestException("Driver vehicle cannot be empty");
        }
    }

    // ENTITY MAPPER
    private void mapToEntity(DeliveryDrivers d, DeliveryDriversRequestDTO dto) {
        d.setDriverName(dto.getDriverName());
        d.setDriverPhone(dto.getDriverPhone());
        d.setDriverVehicle(dto.getDriverVehicle());
    }

    // RESPONSE MAPPER
    private DeliveryDriversResponseDTO map(DeliveryDrivers d) {
        return new DeliveryDriversResponseDTO(
                d.getDriverId(),
                d.getDriverName(),
                d.getDriverPhone(),
                d.getDriverVehicle()
        );
    }
}