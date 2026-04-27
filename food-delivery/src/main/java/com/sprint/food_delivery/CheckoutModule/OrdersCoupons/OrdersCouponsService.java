package com.sprint.food_delivery.CheckoutModule.OrdersCoupons;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sprint.food_delivery.CheckoutModule.Coupons.Coupons;
import com.sprint.food_delivery.CheckoutModule.Coupons.CouponsRepository;
import com.sprint.food_delivery.Exception.BadRequestException;
import com.sprint.food_delivery.Exception.ConflictException;
import com.sprint.food_delivery.Exception.ResourceNotFoundException;
import com.sprint.food_delivery.OrderModule.Orders.Orders;
import com.sprint.food_delivery.OrderModule.Orders.OrdersRepository;

@Service
public class OrdersCouponsService implements IOrdersCouponsService {

    @Autowired
    private OrdersCouponsRepository ordersCouponsRepository;

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private CouponsRepository couponsRepository;

    // Apply coupon
    @Override
    public OrdersCouponsResponseDTO applyCoupon(OrdersCouponsRequestDTO dto) {

        // Validate Order exists
        Orders order = ordersRepository.findById(dto.getOrderId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Order not found with id: " + dto.getOrderId()));

        // Validate Coupon exists
        Coupons coupon = couponsRepository.findById(dto.getCouponId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Coupon not found with id: " + dto.getCouponId()));

        // Expiry check
        if (coupon.getExpiryDate().isBefore(LocalDate.now())) {
            throw new BadRequestException("Coupon is expired");
        }

        //prevent duplicate coupon on same order
        OrdersCouponsId id = new OrdersCouponsId(dto.getOrderId(), dto.getCouponId());

        if (ordersCouponsRepository.existsById(id)) {
            throw new ConflictException("Coupon already applied to this order");
        }

        OrdersCoupons entity = new OrdersCoupons();
        entity.setId(id);
        entity.setOrder(order);
        entity.setCoupon(coupon);


        return map(ordersCouponsRepository.save(entity));
    }

    //  GET ALL COUPONS FOR ORDER
    @Override
    public List<OrdersCouponsResponseDTO> getCouponsByOrderId(Integer orderId) {

        // Validate order exists
        if (!ordersRepository.existsById(orderId)) {
            throw new ResourceNotFoundException("Order not found with id: " + orderId);
        }

        return ordersCouponsRepository.findByOrder_OrderId(orderId)
                .stream()
                .map(this::map)
                .collect(Collectors.toList());
    }

    // REMOVE COUPON
    @Override
    public String removeCoupon(Integer orderId, Integer couponId) {

        OrdersCouponsId id = new OrdersCouponsId(orderId, couponId);

        if (!ordersCouponsRepository.existsById(id)) {
            throw new ResourceNotFoundException("Coupon not applied to this order");
        }

        ordersCouponsRepository.deleteById(id);

        return "Coupon removed successfully from order: " + orderId;
    }

    // Helper: Entity -> DTO
    private OrdersCouponsResponseDTO map(OrdersCoupons oc) {
        return new OrdersCouponsResponseDTO(
                oc.getOrder().getOrderId(),
                oc.getCoupon().getCouponId()
        );
    }
}