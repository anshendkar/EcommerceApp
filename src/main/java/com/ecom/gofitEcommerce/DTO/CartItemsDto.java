package com.ecom.gofitEcommerce.DTO;

import lombok.Data;

@Data
public class CartItemsDto {

    private  Long id;

    private  Long price;

    private Long quantity;

    private Long productId;

    private Long orderId;

    private String productName;

    private String imgUrl;

    private Long userId;
    private String couponName;
}
