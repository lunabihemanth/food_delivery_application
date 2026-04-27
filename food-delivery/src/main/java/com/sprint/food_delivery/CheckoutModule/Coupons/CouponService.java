package com.sprint.food_delivery.CheckoutModule.Coupons;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sprint.food_delivery.Exception.BadRequestException;
import com.sprint.food_delivery.Exception.ConflictException;
import com.sprint.food_delivery.Exception.ResourceNotFoundException;

@Service
public class CouponService implements ICouponService {

    @Autowired
    private CouponsRepository couponRepository;

    // CREATE
    @Override
    public CouponResponseDTO save(CouponRequestDTO dto) {

        //Validation
        if (dto.getCouponCode() == null || dto.getCouponCode().isBlank()) {
            throw new BadRequestException("Coupon code cannot be empty");
        }

        if (dto.getDiscountAmount() == null || dto.getDiscountAmount() <= 0) {
            throw new BadRequestException("Discount must be greater than 0");
        }

        if (dto.getExpiryDate() == null) {
            throw new BadRequestException("Expiry date is required");
        }

        if (dto.getExpiryDate().isBefore(LocalDate.now())) {
            throw new BadRequestException("Coupon already expired");
        }

        //Business Rule → Unique coupon code
        if (couponRepository.existsByCouponCode(dto.getCouponCode())) {
            throw new ConflictException("Coupon code already exists");
        }


        //DTO to Entity
        Coupons coupon = new Coupons();
        coupon.setCouponCode(dto.getCouponCode());
        coupon.setDiscountAmount(dto.getDiscountAmount());
        coupon.setExpiryDate(dto.getExpiryDate());

        return mapToDTO(couponRepository.save(coupon));
    }

    // GET ALL
    @Override
    public List<CouponResponseDTO> getAll() {
        return couponRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // GET BY CODE
    @Override
    public CouponResponseDTO findByCode(String couponCode) {

        if (couponCode == null || couponCode.isBlank()) {
            throw new BadRequestException("Coupon code cannot be empty");
        }

        Coupons coupon = couponRepository.findByCouponCode(couponCode)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found"));

        // Business Rule → Expiry validation
        if (coupon.getExpiryDate().isBefore(LocalDate.now())) {
            throw new BadRequestException("Coupon is expired");
        }

        return mapToDTO(coupon);
    }

    // GET BY ID
    @Override
    public CouponResponseDTO findById(Integer couponId) {

        if (couponId == null || couponId <= 0) {
            throw new BadRequestException("Invalid coupon id");
        }

        Coupons coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found"));

        return mapToDTO(coupon);
    }

    // UPDATE
    @Override
    public CouponResponseDTO update(Integer couponId, CouponRequestDTO dto) {

        Coupons existing = couponRepository.findById(couponId)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found"));

        // Validation
        if (dto.getCouponCode() == null || dto.getCouponCode().isBlank()) {
            throw new BadRequestException("Coupon code cannot be empty");
        }

        if (dto.getDiscountAmount() == null || dto.getDiscountAmount() <= 0) {
            throw new BadRequestException("Discount must be greater than 0");
        }

        if (dto.getExpiryDate() == null || dto.getExpiryDate().isBefore(LocalDate.now())) {
            throw new BadRequestException("Invalid expiry date");
        }

        // Business Rule → Prevent duplicate code
        if (!existing.getCouponCode().equals(dto.getCouponCode()) &&
                couponRepository.existsByCouponCode(dto.getCouponCode())) {
            throw new ConflictException("Coupon code already exists");
        }

        existing.setCouponCode(dto.getCouponCode());
        existing.setDiscountAmount(dto.getDiscountAmount());
        existing.setExpiryDate(dto.getExpiryDate());

        return mapToDTO(couponRepository.save(existing));
    }

    // DELETE
    @Override
    public String delete(Integer couponId) {

        if (couponId == null || couponId <= 0) {
            throw new BadRequestException("Invalid coupon id");
        }

        if (!couponRepository.existsById(couponId)) {
            throw new ResourceNotFoundException("Coupon not found");
        }

        couponRepository.deleteById(couponId);

        return "Coupon deleted successfully";
    }

    // Helper: Entity ->DTO (After DB)
    private CouponResponseDTO mapToDTO(Coupons coupon) {
        return new CouponResponseDTO(
                coupon.getCouponId(),
                coupon.getCouponCode(),
                coupon.getDiscountAmount(),
                coupon.getExpiryDate()
        );
    }
}