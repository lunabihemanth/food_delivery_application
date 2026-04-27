package com.sprint.food_delivery.CheckoutModule.OrdersCoupons;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
public class OrdersCouponsController {

    @Autowired
    private IOrdersCouponsService ordersCouponsService;

    //Response format for all API's
    private Map<String, Object> buildResponse(int status, String message, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", status);
        response.put("message", message);
        response.put("data", data);
        response.put("timestamp", LocalDateTime.now());
        return response;
    }

    //  APPLY COUPON
    @PostMapping("/{orderId}/coupons/{couponId}")
    public ResponseEntity<Map<String, Object>> applyCoupon(
            @PathVariable Integer orderId,
            @PathVariable Integer couponId) {

        OrdersCouponsRequestDTO dto = new OrdersCouponsRequestDTO();
        dto.setOrderId(orderId);
        dto.setCouponId(couponId);

        OrdersCouponsResponseDTO response =
                ordersCouponsService.applyCoupon(dto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(buildResponse(201, "Coupon applied successfully", response));
    }

    //  GET ALL COUPONS FOR AN ORDER
    @GetMapping("/{orderId}/coupons")
    public ResponseEntity<Map<String, Object>> getCouponsByOrder(
            @PathVariable Integer orderId) {

        List<OrdersCouponsResponseDTO> list =
                ordersCouponsService.getCouponsByOrderId(orderId);

        return ResponseEntity.ok(
                buildResponse(200, "Coupons fetched successfully", list)
        );
    }

    //  REMOVE COUPON
    @DeleteMapping("/{orderId}/coupons/{couponId}")
    public ResponseEntity<Map<String, Object>> removeCoupon(
            @PathVariable Integer orderId,
            @PathVariable Integer couponId) {

        String message = ordersCouponsService.removeCoupon(orderId, couponId);

        return ResponseEntity.ok(
                buildResponse(200, message, null)
        );
    }
}