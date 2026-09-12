package com.ecom.gofitEcommerce.DTO;

import com.ecom.gofitEcommerce.entity.Category;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto implements Serializable {
    private Long id;
    private String name;
    private Long price;
    private String description;
    private String imgUrl;
    private Long categoryId;
    private String categoryName;

    private Long quantity;

    @JsonIgnore
    private MultipartFile img; // upload only
}
