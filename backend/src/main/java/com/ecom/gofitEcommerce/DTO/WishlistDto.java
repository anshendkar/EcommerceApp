package com.ecom.gofitEcommerce.DTO;

import lombok.Data;

@Data
public class WishlistDto {

    private Long userId;

    private Long productId;

    private Long id;

    private String productName;

    private String productDescription;

    private String imgUrl;

    private Long price;

}
