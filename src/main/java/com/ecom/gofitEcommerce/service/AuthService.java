package com.ecom.gofitEcommerce.service;

import com.ecom.gofitEcommerce.DTO.AuthRequest;
import com.ecom.gofitEcommerce.DTO.AuthResponse;
import com.ecom.gofitEcommerce.DTO.SignupRequest;
import com.ecom.gofitEcommerce.DTO.UserDto;
import com.ecom.gofitEcommerce.entity.Order;
import com.ecom.gofitEcommerce.entity.User;
import com.ecom.gofitEcommerce.enums.OrderStatus;
import com.ecom.gofitEcommerce.enums.UserRole;
import com.ecom.gofitEcommerce.repository.OrderRepository;
import com.ecom.gofitEcommerce.repository.UserRepository;
import com.ecom.gofitEcommerce.utils.JwtUtil;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AuthService {

    private  AuthenticationManager authenticationManager;
    private  JwtUtil jwtUtil;
    private  UserDetailService userDetailService;
    private  PasswordEncoder passwordEncoder;
    private UserRepository userRepository;

    private OrderRepository orderRepository;


    public String verify(AuthRequest request) {
        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        if (authenticate.isAuthenticated()) {
            return jwtUtil.generateToken(request);
        }

        throw new BadCredentialsException("Invalid credentials");
    }

    public UserDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return mapToDto(user);
    }

    public UserDto createUser(SignupRequest request) {
        User user = new User();
        user.setEmail(request.getEmail());
        user.setName(request.getName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(UserRole.CUSTOMER);

        User createdUser = userRepository.save(user);
        Order order = new Order();
        order.setAmount(0L);
        order.setTotalAmount(0L);
        order.setDiscount(0L);
        order.setUser(createdUser);
        order.setOrderStatus(OrderStatus.Pending);
        orderRepository.save(order);
        return mapToDto(createdUser);
    }

    private UserDto mapToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setUserRole(user.getRole());
        return dto;
    }

    public boolean hasUserWithEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    @PostConstruct
    public void createAdminAccount() {
        Optional<User> admin = userRepository.findFirstByRole(UserRole.ADMIN);
        if (admin.isEmpty()) {
            User user = new User();
            user.setEmail("admin@test.com");
            user.setName("admin12");
            user.setRole(UserRole.ADMIN);
            user.setPassword(passwordEncoder.encode("admin12"));
            userRepository.save(user);
        }
    }
}

