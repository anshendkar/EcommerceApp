package com.ecom.gofitEcommerce.DTO;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AuthRequest {

    @Email(message= "Email is not in correct format")
    @NotEmpty(message ="Email is mandatory")
    @NotEmpty(message ="Email is mandatory")
    @Column(unique = true)
    private String email;

    @NotEmpty(message = "Password is mandatory")
    @NotBlank(message = "Password is mandatory")
    @Size(min = 6 , message = "Password should be minimum 6 chracters long")
    private String password;
}
