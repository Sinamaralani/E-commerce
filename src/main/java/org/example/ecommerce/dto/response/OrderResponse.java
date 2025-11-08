package org.example.ecommerce.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderResponse {

    private Long id;
    private Long userId;
    private List<OrderItemResponse> items;
    private BigDecimal totalPrice;
    private String status;
    private String address;
    private Integer phoneNumber;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
