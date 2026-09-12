package com.ecom.gofitEcommerce.repository;

import com.ecom.gofitEcommerce.entity.Category;
import com.ecom.gofitEcommerce.entity.Order;
import com.ecom.gofitEcommerce.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Order findByUserIdAndOrderStatus(Long userId, OrderStatus orderStatus);

    List<Order> findAllByOrderStatusIn(List<OrderStatus> orderStatusList);

    List<Order> findAllByUserIdAndOrderStatusIn(Long userId, List<OrderStatus> orderStatuses);

    Optional<Order> findByTrackingId(UUID trackingId);

    List<Order> findByDateBetweenAndOrderStatus(Date startofMonth, Date endOfMonth, OrderStatus orderStatus);


    Long countByOrderStatus(OrderStatus orderStatus);
}
