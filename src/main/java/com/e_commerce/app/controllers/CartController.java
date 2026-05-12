package com.e_commerce.app.controllers;

import com.e_commerce.app.business.dto.cart.CartItemRequest;
import com.e_commerce.app.business.dto.cart.CartResponse;
import com.e_commerce.app.business.dto.order.OrderRequest;
import com.e_commerce.app.business.dto.order.OrderResponse;
import com.e_commerce.app.business.services.cart.CartService;
import com.e_commerce.app.config.security.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @Operation(summary = "Add an item to cart")
    @PostMapping("/")
    public ResponseEntity<Void> addItem(@RequestBody CartItemRequest request){

        return ResponseEntity.ok(null);
    }


}
