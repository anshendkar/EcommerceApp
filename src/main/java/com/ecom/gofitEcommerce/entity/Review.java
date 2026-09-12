package com.ecom.gofitEcommerce.entity;

import com.ecom.gofitEcommerce.DTO.ReviewDto;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Data
@Table(name="review_tbl")
public class Review {

     @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
     private  Long id;

     private Long rating;

     @Lob
     private String description;

     private String imgUrl;


     @ManyToOne(fetch = FetchType.LAZY , optional = false)
     @JoinColumn(name="user_id" , nullable = false)
     @OnDelete(action = OnDeleteAction.CASCADE)
     private User user;

     @ManyToOne(fetch = FetchType.LAZY , optional = false)
     @JoinColumn(name="product_id" , nullable = false)
     @OnDelete(action = OnDeleteAction.CASCADE)
     private Product product;

     public ReviewDto getDto() {
          ReviewDto dto = new ReviewDto();
          dto.setId(id);
          dto.setRating(rating);
          dto.setDescription(description);
          dto.setImgUrl(imgUrl);
          dto.setProductId(product.getId());
          dto.setUsername(user.getName());
          dto.setUserId(user.getId());
          return dto;
     }


}
