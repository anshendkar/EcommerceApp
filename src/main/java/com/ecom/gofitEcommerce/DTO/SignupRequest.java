package com.ecom.gofitEcommerce.DTO;

import com.ecom.gofitEcommerce.enums.UserRole;
import lombok.Data;

@Data
public class SignupRequest {

    private String name;

    private String email;

    private String password;

}
