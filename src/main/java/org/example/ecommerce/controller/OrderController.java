package org.example.ecommerce.controller;

import lombok.RequiredArgsConstructor;
import org.example.ecommerce.dto.request.OrderRequest;
import org.example.ecommerce.dto.response.OrderResponse;
import org.example.ecommerce.enums.OrderStatus;
import org.example.ecommerce.security.UserDetailsImpl;
import org.example.ecommerce.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody OrderRequest request) {
        return ResponseEntity.ok(orderService.createOrder(userDetails.getId(), request));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAllUserOrders(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(orderService.getUserOrders(userDetails.getId()));
    }

    @GetMapping("/admin/all")
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }


    @PutMapping("/admin/{orderId}/status")
    public ResponseEntity<OrderResponse> updateOrderStaus(
            @PathVariable Long orderId,
            @RequestBody OrderStatus status
    ) {
        return ResponseEntity.ok(orderService.updateOrderStatus(orderId, status));
    }

    @PutMapping("{orderId}/cancel")
    public ResponseEntity<Void> cancelOrder(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long orderId) {
        orderService.cancelOrder(userDetails.getId(), orderId);
        return ResponseEntity.ok().build();
    }
}
