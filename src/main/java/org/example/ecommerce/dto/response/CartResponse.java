package org.example.ecommerce.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CartResponse {

    private Long id;
    private List<CartItemResponse> items;
    private BigDecimal totalPrice;
    private Integer totalItems;
}
