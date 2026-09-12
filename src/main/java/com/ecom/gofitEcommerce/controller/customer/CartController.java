package com.ecom.gofitEcommerce.controller.customer;

import com.ecom.gofitEcommerce.DTO.OrderDto;
import com.ecom.gofitEcommerce.DTO.AddProductInCartDto;
import com.ecom.gofitEcommerce.DTO.PlacedOrderDto;
import com.ecom.gofitEcommerce.exceptions.ValidationException;
import com.ecom.gofitEcommerce.service.customer.cart.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.filter.OrderedFilter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
public class CartController {

    private  final CartService cartService;


    @PostMapping("/cart")
    public ResponseEntity<?> addProductToCart(@RequestBody AddProductInCartDto addProductInCartDto){
        return cartService.addProductToCart(addProductInCartDto);
    }

    @GetMapping("/cart/{userId}")
    public ResponseEntity<?> getCartByUserId(@PathVariable Long userId) {
        OrderDto orderDto = cartService.getCartByUserId(userId);
        return ResponseEntity.status(HttpStatus.OK).body(orderDto);
    }

//    @GetMapping("/cart")
//    public ResponseEntity<?> getCartDefaultUser() {
//        Long defaultUserId = 1L;
//        OrderDto orderDto = cartService.getCartByUserId(defaultUserId);
//        return ResponseEntity.ok(orderDto);
//    }

    @GetMapping("/coupon/{userId}/{code}")
    public ResponseEntity<?> applyCoupon(@PathVariable Long userId , @PathVariable String code){
        try{
            OrderDto orderDto = cartService.applyCoupon(userId , code);
            return ResponseEntity.ok(orderDto);
        } catch(ValidationException ex){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }
    @PostMapping("/addition")
    public ResponseEntity<OrderDto> increaseProductquantity(@RequestBody AddProductInCartDto addProductInCartDto){
        return ResponseEntity.status(HttpStatus.CREATED).body(cartService.increaseProductquantity(addProductInCartDto));
    }

    @PostMapping("/decrease")
    public ResponseEntity<OrderDto> decreaseProductQuantity(@RequestBody AddProductInCartDto addProductInCartDto) {
        return ResponseEntity.status(HttpStatus.OK).body(cartService.decreaseProductQuantity(addProductInCartDto));
    }

    @PostMapping("/placeOrder")
    public ResponseEntity<OrderDto> decreaseProductQuantity(@RequestBody PlacedOrderDto placedOrderDto) {
        return ResponseEntity.status(HttpStatus.OK).body(cartService.placeOrder(placedOrderDto));
    }

    @GetMapping("/my-placed-orders/{userId}")
    public ResponseEntity<List<OrderDto>> getMyPlacedOrders(@PathVariable Long userId) {
        List<OrderDto> orders = cartService.getMyPlacedOrders(userId);
        return ResponseEntity.ok(orders);
    }

    @DeleteMapping("/cart/{userId}/{productId}")
    public ResponseEntity<String> removeProductFromCart(@PathVariable Long userId,
                                                        @PathVariable Long productId) {
        boolean removed = cartService.removeProductFromCart(userId, productId);
        if (removed) {
            return ResponseEntity.ok("Product removed from cart");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found in cart");
        }
    }
}
