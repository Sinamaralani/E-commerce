package org.example.ecommerce.repository;

import org.example.ecommerce.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
    Optional<CartItem> findByCartIdAndProductId(Long cartId, Long productId);
    Optional<CartItem> findById(Long id);
}
