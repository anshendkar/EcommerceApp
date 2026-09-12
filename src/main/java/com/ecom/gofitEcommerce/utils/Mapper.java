package com.ecom.gofitEcommerce.utils;

import com.ecom.gofitEcommerce.DTO.CartItemsDto;
import com.ecom.gofitEcommerce.DTO.OrderDto;
import com.ecom.gofitEcommerce.DTO.ProductDto;
import com.ecom.gofitEcommerce.entity.CartItems;
import com.ecom.gofitEcommerce.entity.Order;
import com.ecom.gofitEcommerce.entity.Product;

import java.util.Base64;

public class Mapper {
    public static ProductDto toDto(Product product) {
        return ProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .description(product.getDescription())
                .imgUrl(product.getImgUrl())
                .categoryId(product.getCategory().getId())
                .categoryName(product.getCategory().getName())
                .build();
    }

    public static CartItemsDto getCartDto( CartItems  cartItems){
        CartItemsDto cartItemsDto = new CartItemsDto();
        cartItemsDto.setId(cartItems.getId());
        cartItemsDto.setPrice(cartItems.getPrice());
        cartItemsDto.setProductId(cartItems.getProduct().getId());
        cartItemsDto.setQuantity(cartItems.getQuantity());
        cartItemsDto.setUserId(cartItems.getUser().getId());
        cartItemsDto.setProductName(cartItems.getProduct().getName());
        cartItemsDto.setImgUrl(cartItems.getProduct().getImgUrl());

        return cartItemsDto;
    }
}
