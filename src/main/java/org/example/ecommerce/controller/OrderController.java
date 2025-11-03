package org.example.ecommerce.controller;

import lombok.RequiredArgsConstructor;
import org.example.ecommerce.service.OrderService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class OrderController {

    private final OrderService orderService;
}
