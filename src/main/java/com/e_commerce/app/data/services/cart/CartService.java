package com.e_commerce.app.data.services.cart;

import com.e_commerce.app.config.exceptions.BadRequestException;
import com.e_commerce.app.data.dto.cart.CartItemRequest;
import com.e_commerce.app.data.entities.auth.UserEntity;
import com.e_commerce.app.data.entities.cart.CartEntity;
import com.e_commerce.app.data.entities.cart.CartItemEntity;
import com.e_commerce.app.data.entities.product.ProductEntity;
import com.e_commerce.app.data.repositories.auth.UserRepository;
import com.e_commerce.app.data.repositories.cart.CartItemRepository;
import com.e_commerce.app.data.repositories.cart.CartRepository;
import com.e_commerce.app.data.repositories.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Transactional
    public CartEntity getOrCreateCart(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return cartRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    CartEntity cart = CartEntity.builder().user(user).build();
                    return cartRepository.save(cart);
                });
    }

    @Transactional
    public CartEntity addItem(String email, CartItemRequest request) {
        CartEntity cart = getOrCreateCart(email);
        ProductEntity product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (product.getStockQuantity() < request.getQuantity()) {
            throw new BadRequestException("Insufficient stock");
        }

        // Update quantity if item already exists in cart
        cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId())
                .ifPresentOrElse(
                        existing -> existing.setQuantity(
                                existing.getQuantity() + request.getQuantity()),
                        () -> {
                            CartItemEntity newItem = CartItemEntity.builder()
                                    .cart(cart)
                                    .product(product)
                                    .quantity(request.getQuantity())
                                    .build();
                            cart.getItems().add(newItem);
                        }
                );

        return cartRepository.save(cart);
    }

    @Transactional
    public CartEntity updateItem(String email, Long itemId, Integer quantity) {
        CartEntity cart = getOrCreateCart(email);
        CartItemEntity item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new BadRequestException("Item does not belong to your cart");
        }

        if (quantity <= 0) {
            cart.getItems().remove(item);
            cartItemRepository.delete(item);
        } else {
            item.setQuantity(quantity);
        }

        return cartRepository.save(cart);
    }

    @Transactional
    public void clearCart(CartEntity cart) {
        cartItemRepository.deleteByCartId(cart.getId());
        cart.getItems().clear();
    }
}