package org.example.ecommerce.dto.response;

public record AuthResponse(
        Long id, String JwtToken, String username, String email
) {
}
