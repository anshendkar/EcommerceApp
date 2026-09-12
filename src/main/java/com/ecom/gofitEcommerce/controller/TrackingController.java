package com.ecom.gofitEcommerce.controller;

import com.ecom.gofitEcommerce.DTO.OrderDto;
import com.ecom.gofitEcommerce.service.customer.cart.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/")
public class TrackingController {

    private final CartService cartService;

    @GetMapping("track/{trackingId}")
    public ResponseEntity<OrderDto> getOrderByTrackingId(@PathVariable UUID trackingId) {
      //  UUID uuid = UUID.fromString(trackingId);
            OrderDto orderDto = cartService.searchProductBytrackingId(trackingId);
            if (orderDto != null) {
                return ResponseEntity.ok(orderDto);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
    }
}
