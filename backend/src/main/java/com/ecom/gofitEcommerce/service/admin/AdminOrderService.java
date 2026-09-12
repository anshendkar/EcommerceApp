package com.ecom.gofitEcommerce.service.admin;

import com.ecom.gofitEcommerce.DTO.AnalyticsResponse;
import com.ecom.gofitEcommerce.DTO.OrderDto;
import com.ecom.gofitEcommerce.entity.Order;
import com.ecom.gofitEcommerce.enums.OrderStatus;
import com.ecom.gofitEcommerce.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminOrderService {

    private  final OrderRepository orderRepository;

    @Transactional
    public List<OrderDto> getAllPlacedOrders(){

        List<Order> orderList = orderRepository.findAllByOrderStatusIn(List.of(OrderStatus.Pending , OrderStatus.Placed
        , OrderStatus.Shipped , OrderStatus.Delivered));

        return orderList.stream().map(Order::getOrderDto).collect(Collectors.toList());
    }

    @Transactional
    public OrderDto changeOrderStatus(Long orderId , String status){
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if(optionalOrder.isPresent()){
            Order order = optionalOrder.get();
            if(Objects.equals(status , "Shipped")){
                order.setOrderStatus(OrderStatus.Shipped);
            }else if(Objects.equals(status , "Delivered")){
                order.setOrderStatus(OrderStatus.Delivered);
            }
            return orderRepository.save(order).getOrderDto();
        }
        return null;
    }

    @Transactional
    public AnalyticsResponse calculateAnalytics(){

        LocalDate currentDate = LocalDate.now();
        LocalDate previousMonthDate = currentDate.minusMonths(1);
        Long currentMonthOrders = getTotalOrdersForCurrentMonth(currentDate.getMonthValue() , currentDate.getYear());

        Long previousMonthOrders = getTotalOrdersForCurrentMonth(previousMonthDate.getMonthValue() , previousMonthDate.getYear());
        Long currentMonthEarnings = getTotalEarningsForMonth(currentDate.getMonthValue() , currentDate.getYear());
        Long previousMonthEarnings = getTotalEarningsForMonth(previousMonthDate.getMonthValue(), previousMonthDate.getYear());

        Long placed = orderRepository.countByOrderStatus(OrderStatus.Placed);

        Long delivered = orderRepository.countByOrderStatus(OrderStatus.Delivered);

        Long  shipped = orderRepository.countByOrderStatus(OrderStatus.Shipped);

        return new AnalyticsResponse(placed , shipped , delivered , currentMonthOrders , previousMonthOrders ,currentMonthEarnings , previousMonthEarnings);
    }

    @Transactional
    private Long getTotalEarningsForMonth(int monthValue, int year) {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR , year);
        calendar.set(Calendar.MONTH , monthValue-1);
        calendar.set(Calendar.DAY_OF_MONTH,1);
        calendar.set(Calendar.HOUR_OF_DAY , 0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);

        Date startofMonth = calendar.getTime();

        calendar.set(Calendar.DAY_OF_MONTH , calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY , 23);
        calendar.set(Calendar.MINUTE , 59);
        calendar.set(Calendar.SECOND , 59);

        Date endOfMonth = calendar.getTime();

        List<Order> orders = orderRepository.findByDateBetweenAndOrderStatus(startofMonth , endOfMonth , OrderStatus.Delivered);

        Long sum = 0L;
        for(Order order : orders){
            sum += order.getAmount();
        }
        return sum;


    }

    @Transactional
    private Long getTotalOrdersForCurrentMonth(int month, int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR , year);
        calendar.set(Calendar.MONTH , month-1);
        calendar.set(Calendar.DAY_OF_MONTH,1);
        calendar.set(Calendar.HOUR_OF_DAY , 0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);

        Date startofMonth = calendar.getTime();

        calendar.set(Calendar.DAY_OF_MONTH , calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY , 23);
        calendar.set(Calendar.MINUTE , 59);
        calendar.set(Calendar.SECOND , 59);

        Date endOfMonth = calendar.getTime();

        List<Order> orders = orderRepository.findByDateBetweenAndOrderStatus(startofMonth , endOfMonth , OrderStatus.Delivered);

        return (long) orders.size();

    }
}
