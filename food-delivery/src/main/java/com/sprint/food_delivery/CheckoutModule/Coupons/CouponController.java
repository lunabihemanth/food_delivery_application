package com.sprint.food_delivery.CheckoutModule.Coupons;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController   //makes this class a REST API controller
@RequestMapping("/coupons")  //All API's in this class start with /coupons
public class CouponController {

    @Autowired
    private ICouponService couponService;

    // helper method for response structure standardization
    private Map<String, Object> buildResponse(int status, String message, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", status);
        response.put("message", message);
        response.put("data", data);
        response.put("timestamp", LocalDateTime.now());
        return response;
    }

    //  Create the Coupon
    @PostMapping
    public ResponseEntity<Map<String, Object>> createCoupon(
            @Valid @RequestBody CouponRequestDTO dto) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(buildResponse(201, "Coupon created successfully",
                        couponService.save(dto)));
    }

    //  Get all the Coupons
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllCoupons() {

        return ResponseEntity.ok(
                buildResponse(200, "Coupons fetched successfully",
                        couponService.getAll())
        );
    }

    //  Validate the Coupon
    @GetMapping("/{couponCode}")
    public ResponseEntity<Map<String, Object>> validateCoupon(
            @PathVariable String couponCode) {

        return ResponseEntity.ok(
                buildResponse(200, "Coupon validated successfully",
                        couponService.findByCode(couponCode))
        );
    }

    // Update the Coupon
    @PutMapping("/{couponId}")   // inside a @RequestMapping("/coupons") class
        public ResponseEntity<?> updateCoupon(@PathVariable Integer couponId,
                                        @RequestBody CouponRequestDTO dto) {
        CouponResponseDTO updated = couponService.update(couponId, dto);
        return ResponseEntity.ok(buildResponse(200, "Coupon updated", updated));
        }

    // Delete Coupon
    @DeleteMapping("/{couponId}")
    public ResponseEntity<Map<String, Object>> deleteCoupon(
            @PathVariable Integer couponId) {

        couponService.delete(couponId);

        return ResponseEntity.ok(
                buildResponse(200, "Coupon deleted successfully", null)
        );
    }


    
}