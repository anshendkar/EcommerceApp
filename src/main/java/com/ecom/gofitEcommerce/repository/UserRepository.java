package com.ecom.gofitEcommerce.repository;

import com.ecom.gofitEcommerce.entity.User;
import com.ecom.gofitEcommerce.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User , Long> {

    Optional<User> findByEmail(String email);

    //User findByRole(UserRole userRole);

    List<User> findByRole(UserRole role);
    Optional<User> findFirstByRole(UserRole role);
}
