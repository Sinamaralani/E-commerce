package org.example.ecommerce.service;

import lombok.RequiredArgsConstructor;
import org.example.ecommerce.dto.request.LoginRequest;
import org.example.ecommerce.dto.request.RegisterRequest;
import org.example.ecommerce.dto.response.AuthResponse;
import org.example.ecommerce.entity.Cart;
import org.example.ecommerce.entity.User;
import org.example.ecommerce.enums.Role;
import org.example.ecommerce.exception.BadRequestException;
import org.example.ecommerce.repository.CartRepository;
import org.example.ecommerce.repository.UserRepository;
import org.example.ecommerce.security.JwtUtils;
import org.example.ecommerce.security.UserDetailsImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {

        if (userRepository.findByUsername(request.getUsername()).isPresent())
            throw new BadRequestException("Username is already taken");

        if (userRepository.findByEmail(request.getEmail()).isPresent())
            throw new BadRequestException("Email is already taken");

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhoneNumber(request.getPhoneNumber());
        user.setAddress(request.getAddress());
        user.setRole(Role.USER);
        User savedUser = userRepository.save(user);

        Cart cart = new Cart();
        cart.setUser(savedUser);
        cartRepository.save(cart);

        return helper(request.getUsername(), request.getPassword());
    }

    public AuthResponse login(LoginRequest request) {

        if (userRepository.findByUsername(request.getUsername()).isEmpty())
            throw new UsernameNotFoundException("Username not found");

        return helper(request.getUsername(), request.getPassword());
    }

    private AuthResponse helper(String username, String password) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwtToken = jwtUtils.generateJwtToken(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        return new AuthResponse(
                userDetails.getId(),
                jwtToken,
                userDetails.getUsername(),
                userDetails.getAuthorities().iterator().next().getAuthority()
        );
    }

}
