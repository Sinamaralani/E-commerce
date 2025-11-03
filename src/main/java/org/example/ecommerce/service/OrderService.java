package org.example.ecommerce.service;

import lombok.RequiredArgsConstructor;
import org.example.ecommerce.repository.OrderItemRepository;
import org.example.ecommerce.repository.OrderRepository;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

}
