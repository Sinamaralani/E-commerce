package org.example.ecommerce.controller;

import lombok.RequiredArgsConstructor;
import org.example.ecommerce.dto.request.AddToCartRequest;
import org.example.ecommerce.entity.Cart;
import org.example.ecommerce.security.UserDetailsImpl;
import org.example.ecommerce.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<Cart> getCart(UserDetailsImpl userDetails) {
        return ResponseEntity.ok(cartService.getCart(userDetails.getId()));
    }

    @PostMapping("/add")
    public ResponseEntity<Cart> addToCart(UserDetailsImpl userDetails, @RequestBody AddToCartRequest request) {
        return ResponseEntity.ok(cartService.addToCart(userDetails.getId(), request));
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<Cart> updateCartItem(
            UserDetailsImpl userDetails,
            @PathVariable Long itemId,
            @RequestParam Integer quantity) {
        return ResponseEntity.ok(cartService.updateCartItem(userDetails.getId(), itemId, quantity));
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<Cart> deleteCartItem(UserDetailsImpl userDetails, @PathVariable Long itemId) {
        return ResponseEntity.ok(cartService.deleteCartItem(userDetails.getId(), itemId));
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearCart(UserDetailsImpl userDetails) {
        cartService.clearCart(userDetails.getId());
        return ResponseEntity.ok().build();
    }
}
