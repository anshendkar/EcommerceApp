package com.ecom.gofitEcommerce.service.customer.cart;

import com.ecom.gofitEcommerce.DTO.*;
import com.ecom.gofitEcommerce.entity.*;
import com.ecom.gofitEcommerce.enums.OrderStatus;
import com.ecom.gofitEcommerce.exceptions.ValidationException;
import com.ecom.gofitEcommerce.repository.*;
import com.ecom.gofitEcommerce.utils.Mapper;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
@Service
@AllArgsConstructor
@Transactional
public class CartService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CartItemsRepository cartItemsRepository;
    private final ProductRepository productRepository;
    private final CouponRepository couponRepository;

    // =========================================================
    // ADD PRODUCT TO CART
    // =========================================================
    public ResponseEntity<?> addProductToCart(AddProductInCartDto dto) {

        Order activeOrder =
                orderRepository.findByUserIdAndOrderStatus(dto.getUserId(), OrderStatus.Pending);

        if (activeOrder == null) {
            User user = userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            activeOrder = new Order();
            activeOrder.setUser(user);
            activeOrder.setAmount(0L);
            activeOrder.setDiscount(0L);
            activeOrder.setTotalAmount(0L);
            activeOrder.setOrderStatus(OrderStatus.Pending);
            activeOrder.setDate(new Date());
            activeOrder.setTrackingId(UUID.randomUUID());

            activeOrder = orderRepository.save(activeOrder);
        }

        Optional<CartItems> existing =
                cartItemsRepository.findByProductIdAndOrderIdAndUserId(
                        dto.getProductId(),
                        activeOrder.getId(),
                        dto.getUserId());

        if (existing.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Item already in cart.");
        }

        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        CartItems cart = new CartItems();
        cart.setProduct(product);
        cart.setPrice(product.getPrice());
        cart.setQuantity(1L);
        cart.setUser(activeOrder.getUser());
        cart.setOrder(activeOrder);

        CartItems saved = cartItemsRepository.save(cart);

        // ⭐ FIXED (recalculate instead of manual math)
        updateActiveOrderAmounts(activeOrder);
        orderRepository.save(activeOrder);

        CartItemResponseDto response = new CartItemResponseDto();
        response.setId(saved.getId());
        response.setProductId(product.getId());
        response.setProductName(product.getName());
        response.setPrice(saved.getPrice());
        response.setQuantity(saved.getQuantity());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // =========================================================
    // GET CART
    // =========================================================
    public OrderDto getCartByUserId(Long userId) {

        Order activeOrder =
                orderRepository.findByUserIdAndOrderStatus(userId, OrderStatus.Pending);

        if (activeOrder == null) return new OrderDto();

        List<CartItemsDto> items = activeOrder.getCartItems()
                .stream()
                .map(Mapper::getCartDto)
                .collect(Collectors.toList());

        OrderDto dto = new OrderDto();
        dto.setId(activeOrder.getId());
        dto.setAmount(activeOrder.getAmount());
        dto.setTotalAmount(activeOrder.getTotalAmount());
        dto.setDiscount(activeOrder.getDiscount());
        dto.setOrderStatus(activeOrder.getOrderStatus());
        dto.setCartItems(items);

        if (activeOrder.getCoupon() != null)
            dto.setCouponName(activeOrder.getCoupon().getName());

        return dto;
    }

    // =========================================================
    // APPLY COUPON (UNCHANGED)
    // =========================================================
    public OrderDto applyCoupon(Long userId, String code) {

        Order activeOrder =
                orderRepository.findByUserIdAndOrderStatus(userId, OrderStatus.Pending);

        Coupon coupon = couponRepository.findByCode(code)
                .orElseThrow(() -> new ValidationException("Coupon not Found."));

        if (couponExpired(coupon))
            throw new ValidationException("Coupon has expired");

        activeOrder.setCoupon(coupon);

        // ⭐ recompute totals
        updateActiveOrderAmounts(activeOrder);
        orderRepository.save(activeOrder);

        return activeOrder.getOrderDto();
    }

    private boolean couponExpired(Coupon coupon) {
        return coupon.getExpirationDate() != null
                && new Date().after(coupon.getExpirationDate());
    }

    // =========================================================
    // INCREASE QUANTITY
    // =========================================================
    public OrderDto increaseProductquantity(AddProductInCartDto dto) {

        Order activeOrder =
                orderRepository.findByUserIdAndOrderStatus(dto.getUserId(), OrderStatus.Pending);

        CartItems cartItems =
                cartItemsRepository.findByProductIdAndOrderIdAndUserId(
                                dto.getProductId(),
                                activeOrder.getId(),
                                dto.getUserId())
                        .orElseThrow();

        cartItems.setQuantity(cartItems.getQuantity() + 1);

        cartItemsRepository.save(cartItems);

        updateActiveOrderAmounts(activeOrder);
        orderRepository.save(activeOrder);

        return activeOrder.getOrderDto();
    }

    // =========================================================
    // DECREASE QUANTITY
    // =========================================================
    public OrderDto decreaseProductQuantity(AddProductInCartDto dto) {

        Order activeOrder =
                orderRepository.findByUserIdAndOrderStatus(dto.getUserId(), OrderStatus.Pending);

        CartItems cartItems =
                cartItemsRepository.findByProductIdAndOrderIdAndUserId(
                                dto.getProductId(),
                                activeOrder.getId(),
                                dto.getUserId())
                        .orElseThrow();

        cartItems.setQuantity(cartItems.getQuantity() - 1);

        if (cartItems.getQuantity() <= 0) {
            cartItemsRepository.delete(cartItems);
        } else {
            cartItemsRepository.save(cartItems);
        }

        updateActiveOrderAmounts(activeOrder);
        orderRepository.save(activeOrder);

        return activeOrder.getOrderDto();
    }

    // =========================================================
    // REMOVE PRODUCT FROM CART ⭐ FIXED
    // =========================================================
    public boolean removeProductFromCart(Long userId, Long productId) {

        Order activeOrder =
                orderRepository.findByUserIdAndOrderStatus(userId, OrderStatus.Pending);

        Optional<CartItems> optionalCart =
                cartItemsRepository.findByProductIdAndOrderIdAndUserId(
                        productId,
                        activeOrder.getId(),
                        userId);

        optionalCart.ifPresent(cartItemsRepository::delete);

        updateActiveOrderAmounts(activeOrder);
        orderRepository.save(activeOrder);

        return true;
    }

    // =========================================================
    // CENTRAL ORDER CALCULATION ⭐ IMPORTANT
    // =========================================================
    private void updateActiveOrderAmounts(Order activeOrder) {

        long totalAmount = activeOrder.getCartItems()
                .stream()
                .mapToLong(item -> item.getPrice() * item.getQuantity())
                .sum();

        activeOrder.setTotalAmount(totalAmount);

        if (activeOrder.getCoupon() != null) {

            double discount =
                    (activeOrder.getCoupon().getDiscount() / 100.0) * totalAmount;

            activeOrder.setDiscount((long) discount);
            activeOrder.setAmount((long) (totalAmount - discount));

        } else {
            activeOrder.setDiscount(0L);
            activeOrder.setAmount(totalAmount);
        }
    }

    public OrderDto searchProductBytrackingId(UUID trackingId) {
        Optional<Order> optionalOrder = orderRepository.findByTrackingId(trackingId);
        if (optionalOrder.isPresent()) {
            return optionalOrder.get().getOrderDto();
        }
        return null;
    }

    @Transactional public OrderDto placeOrder(PlacedOrderDto placedOrderDto) {
        Order activeOrder = orderRepository.findByUserIdAndOrderStatus( placedOrderDto.getUserId(), OrderStatus.Pending);
        Optional<User> optionalUser = userRepository.findById(placedOrderDto.getUserId());
        if (optionalUser.isPresent() && activeOrder != null) { // ✅ Finalize current order
            activeOrder.setOrderDescription(placedOrderDto.getOrderDescription());
            activeOrder.setAddress(placedOrderDto.getAddress());
            activeOrder.setDate(new Date());
            activeOrder.setOrderStatus(OrderStatus.Placed); // ✅ Mark as placed
            activeOrder.setTrackingId(UUID.randomUUID());
            orderRepository.save(activeOrder); // ✅ Create new pending order
            Order newPendingOrder = new Order();
            newPendingOrder.setAmount(0L);
            newPendingOrder.setTotalAmount(0L);
            newPendingOrder.setDiscount(0L);
            newPendingOrder.setUser(optionalUser.get());
            newPendingOrder.setOrderStatus(OrderStatus.Pending);
            newPendingOrder.setDate(new Date());
            newPendingOrder.setTrackingId(UUID.randomUUID());
            orderRepository.save(newPendingOrder); // <-- ✅ This line was missing
            return activeOrder.getOrderDto(); }
        return null;
    }

    @Transactional public List<OrderDto> getMyPlacedOrders(Long userId) {
        return orderRepository .findAllByUserIdAndOrderStatusIn(userId,
                List.of( OrderStatus.Placed, OrderStatus.Shipped, OrderStatus.Delivered ))
                .stream() .map(Order::getOrderDto) .collect(Collectors.toList());
    }
}