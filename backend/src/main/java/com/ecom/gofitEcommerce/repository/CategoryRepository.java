package com.ecom.gofitEcommerce.repository;

import com.ecom.gofitEcommerce.entity.Category;
import com.ecom.gofitEcommerce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository   extends JpaRepository<Category, Long> {
}
