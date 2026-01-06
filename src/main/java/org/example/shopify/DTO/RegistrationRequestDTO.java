package org.example.shopify.DTO;

import lombok.Data;

@Data
public class RegistrationRequestDTO {
    private String username;
    private String email;
    private String password;
}