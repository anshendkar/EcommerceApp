package com.ecom.gofitEcommerce.DTO;

import com.ecom.gofitEcommerce.entity.User;
import com.ecom.gofitEcommerce.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

@Data
public class UserDto {

    private Long id;

    private String email ;

    private String name;

    private UserRole userRole;
}
