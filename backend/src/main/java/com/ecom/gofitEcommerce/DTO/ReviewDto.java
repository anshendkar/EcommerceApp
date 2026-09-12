package com.ecom.gofitEcommerce.DTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ReviewDto {

    private Long id;

    private Long rating;

    private String description;

    private String imgUrl;

    private Long userId;

    private Long productId;

    private String username;

    @JsonIgnore
    private MultipartFile img;
}
