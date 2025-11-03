package org.example.ecommerce.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AuthResponse {
    private Long id;
    private String JwtToken;
    private String username;
    private String role;

}
