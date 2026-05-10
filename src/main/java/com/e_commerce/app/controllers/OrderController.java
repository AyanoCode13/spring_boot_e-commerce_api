package com.e_commerce.app.controllers;

import com.e_commerce.app.data.dto.order.OrderRequest;
import com.e_commerce.app.data.dto.order.OrderResponse;
import com.e_commerce.app.data.enums.OrderStatus;
import com.e_commerce.app.data.mappers.EntityMapper;
import com.e_commerce.app.data.services.order.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final EntityMapper mapper;

    @Operation(summary = "Place a new order from current cart",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @PostMapping
    public ResponseEntity<OrderResponse> placeOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody OrderRequest request) {
        return ResponseEntity.ok(
                mapper.toOrderResponse(
                        orderService.placeOrder(userDetails.getUsername(), request)));
    }

    @Operation(summary = "Get my orders",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @GetMapping
    public ResponseEntity<Page<OrderResponse>> getMyOrders(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(
                orderService.getUserOrders(userDetails.getUsername(), page, size)
                        .map(mapper::toOrderResponse));
    }

    @Operation(summary = "Get order by ID",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getById(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        return ResponseEntity.ok(
                mapper.toOrderResponse(
                        orderService.getById(id, userDetails.getUsername())));
    }

    @Operation(summary = "Update order status (Admin only)",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<OrderResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam OrderStatus status) {
        return ResponseEntity.ok(
                mapper.toOrderResponse(orderService.updateStatus(id, status)));
    }
}