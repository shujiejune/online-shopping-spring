package org.example.shopify.Controller;

import org.example.shopify.Config.JwtProvider;
import org.example.shopify.DTO.LoginRequestDTO;
import org.example.shopify.DTO.LoginResponseDTO;
import org.example.shopify.DTO.RegistrationRequestDTO;
import org.example.shopify.Domain.User;
import org.example.shopify.Service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api")
public class UserController {
    private final UserService userService;
    private final JwtProvider jwtProvider;

    public UserController(UserService userService, JwtProvider jwtProvider) {
        this.userService = userService;
        this.jwtProvider = jwtProvider;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody RegistrationRequestDTO registrationDto) {
        userService.register(registrationDto);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginDto) {
        // 1. Authenticate user
        User user = userService.login(loginDto.getUsername(), loginDto.getPassword());

        // 2. Generate Token
        String token = jwtProvider.createToken(user.getUsername(), user.getRole());

        // 3. Build the Response DTO
        LoginResponseDTO response = new LoginResponseDTO();
        response.setToken(token);
        response.setUsername(user.getUsername());
        // Map int role to String (0 -> ADMIN, 1 -> USER)
        response.setRole(user.getRole() == 0 ? "ADMIN" : "USER");

        return ResponseEntity.ok(response);
    }
}
