package org.example.ecommerce.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.ecommerce.dto.request.OrderRequest;
import org.example.ecommerce.dto.response.OrderItemResponse;
import org.example.ecommerce.dto.response.OrderResponse;
import org.example.ecommerce.entity.Order;
import org.example.ecommerce.entity.OrderItem;
import org.example.ecommerce.entity.Product;
import org.example.ecommerce.entity.User;
import org.example.ecommerce.enums.OrderStatus;
import org.example.ecommerce.repository.OrderRepository;
import org.example.ecommerce.repository.ProductRepository;
import org.example.ecommerce.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CartService cartService;

    public List<OrderResponse> getUserOrders(Long userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream().map(this::toOrderResponse).collect(Collectors.toList());
    }

    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll()
                .stream().map(this::toOrderResponse).collect(Collectors.toList());
    }

    @Transactional
    public OrderResponse createOrder(Long userId, OrderRequest request) {

        User user = userRepository.findById(userId).orElseThrow();

        var cartResponse = cartService.getCart(userId);
        if (cartResponse.getItems().isEmpty()) throw new RuntimeException("");

        Order order = new Order();
        order.setUser(user);
        order.setAddress(request.getAddress());
        order.setPhoneNumber(request.getPhoneNumber());
        order.setNotes(request.getNotes());
        order.setStatus(OrderStatus.PENDING);

        BigDecimal totalPrice = BigDecimal.ZERO;

        for (var cartItem : cartResponse.getItems()) {

            Product product = productRepository.findById(cartItem.getProductId()).orElseThrow();
            if (product.getStockQuantity() < cartItem.getQuantity()) throw new RuntimeException("");


            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setProductName(product.getName());
            orderItem.setQuantity(product.getStockQuantity());
            orderItem.setPrice(product.getPrice());
            order.getItems().add(orderItem);

            product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());
            productRepository.save(product);

            totalPrice = totalPrice.add(cartItem.getPrice().multiply(BigDecimal.valueOf(product.getStockQuantity())));
        }

        order.setTotalPrice(totalPrice);
        return toOrderResponse(orderRepository.save(order));

    }

    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, OrderStatus status) {

        Order order = orderRepository.findById(orderId).orElseThrow();

        order.setStatus(status);
        return toOrderResponse(orderRepository.save(order));
    }

    public void cancelOrder(Long userId, Long orderId) {

        Order order = orderRepository.findById(orderId).orElseThrow();

        if (order.getUser().getId().equals(userId)) throw new RuntimeException("");

        if (order.getStatus() == OrderStatus.DELIVERED || order.getStatus() == OrderStatus.SHIPPED)
            throw new RuntimeException("");

        if (order.getStatus() == OrderStatus.CANCELLED) throw new RuntimeException("");

        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
            productRepository.save(product);
        }

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

    }

    private OrderResponse toOrderResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setUserId(order.getUser().getId());
        response.setAddress(order.getAddress());
        response.setPhoneNumber(order.getPhoneNumber());
        response.setNotes(order.getNotes());
        response.setTotalPrice(order.getTotalPrice());
        response.setCreatedAt(order.getCreatedAt());
        response.setUpdatedAt(order.getUpdatedAt());
        response.setItems(order.getItems().stream().map(this::toOrderItemResponse).collect(Collectors.toList()));
        return response;
    }

    private OrderItemResponse toOrderItemResponse(OrderItem orderItem) {
        OrderItemResponse response = new OrderItemResponse();
        response.setId(orderItem.getId());
        response.setProductId(orderItem.getProduct().getId());
        response.setProductName(orderItem.getProductName());
        response.setPrice(orderItem.getPrice());
        response.setQuantity(orderItem.getQuantity());
        response.setTotal(orderItem.getPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity())));
        return response;
    }

}
