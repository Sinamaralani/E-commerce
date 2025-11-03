package org.example.ecommerce.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.example.ecommerce.dto.request.AddToCartRequest;
import org.example.ecommerce.dto.response.CartItemResponse;
import org.example.ecommerce.dto.response.CartResponse;
import org.example.ecommerce.entity.Cart;
import org.example.ecommerce.entity.CartItem;
import org.example.ecommerce.entity.Product;
import org.example.ecommerce.entity.User;
import org.example.ecommerce.repository.CartItemRepository;
import org.example.ecommerce.repository.CartRepository;
import org.example.ecommerce.repository.ProductRepository;
import org.example.ecommerce.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@AllArgsConstructor
@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public CartResponse getCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId).orElseThrow();
        return toCartResponse(cart);
    }

    @Transactional
    public CartResponse addToCart(Long userId, AddToCartRequest request) {

        User user = userRepository.findById(userId).orElseThrow();

        Product product = productRepository.findById(request.getProductId()).orElseThrow();

        if (!product.getIsAvailable()) {
            throw new RuntimeException("Product is not available");
        }

        if (product.getStockQuantity() < request.getQuantity()) {
            throw new RuntimeException("Stock quantity less than request quantity");
        }

        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return cartRepository.save(newCart);
                });

        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId()).orElse(null);

        if (cartItem != null) {
            int newQuantity = cartItem.getQuantity() + request.getQuantity();
            if (newQuantity < request.getQuantity()) {
                throw new RuntimeException("Quantity less than request quantity");
            }
            cartItem.setQuantity(newQuantity);
        } else {
            cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(request.getQuantity());
            cartItem.setPrice(product.getPrice());
            cart.getCartItems().add(cartItem);
        }

        cartItemRepository.save(cartItem);
        cart.calculateTotalPrice();
        cartRepository.save(cart);

        return toCartResponse(cart);

    }

    @Transactional
    public CartResponse updateCartItem(Long userId, Long itemId, Integer quantity) {

        Cart cart = cartRepository.findByUserId(userId).orElseThrow();

        CartItem cartItem = cartItemRepository.findById(itemId).orElseThrow();

        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new RuntimeException("Cart item already exists");
        }

        if (quantity <= 0) throw new RuntimeException("Quantity less than request quantity");

        if (cartItem.getQuantity() < quantity) throw new RuntimeException("Quantity less than request quantity");

        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);
        cart.calculateTotalPrice();
        cartRepository.save(cart);

        return toCartResponse(cart);

    }

    @Transactional
    public CartResponse deleteCartItem(Long userId, Long itemId) {

        Cart cart = cartRepository.findByUserId(userId).orElseThrow();

        CartItem cartItem = cartItemRepository.findById(itemId).orElseThrow();

        if (!cartItem.getCart().getId().equals(cart.getId()))
            throw new RuntimeException("Cart item is not for this user");

        cart.getCartItems().remove(cartItem);
        cartItemRepository.delete(cartItem);
        cart.calculateTotalPrice();
        cartRepository.save(cart);

        return toCartResponse(cart);
    }

    public void clearCart(Long userId) {

        Cart cart = cartRepository.findByUserId(userId).orElseThrow();

        cart.getCartItems().clear();
        cart.setTotalPrice(BigDecimal.ZERO);
        cartRepository.save(cart);
    }

    private CartResponse toCartResponse(Cart cart) {
        CartResponse cartResponse = new CartResponse();
        cartResponse.setId(cart.getId());
        cartResponse.setItems(cart.getCartItems().stream().map(this::toCartItemResponse).toList());
        cartResponse.setTotalPrice(cart.getTotalPrice());
        cartResponse.setTotalItems(cart.getCartItems().size());
        return cartResponse;
    }

    private CartItemResponse toCartItemResponse(CartItem cartItem) {
        CartItemResponse cartItemResponse = new CartItemResponse();
        cartItemResponse.setId(cartItemResponse.getId());
        cartItemResponse.setProductId(cartItemResponse.getProductId());
        cartItemResponse.setProductName(cartItem.getProduct().getName());
        cartItemResponse.setPrice(cartItem.getPrice());
        cartItemResponse.setQuantity(cartItem.getQuantity());
        cartItemResponse.setTotalPrice(cartItem.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
        return cartItemResponse;
    }

}
