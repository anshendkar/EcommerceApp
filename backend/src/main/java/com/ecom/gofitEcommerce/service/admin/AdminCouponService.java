package com.ecom.gofitEcommerce.service.admin;

import com.ecom.gofitEcommerce.entity.Coupon;
import com.ecom.gofitEcommerce.exceptions.ValidationException;
import com.ecom.gofitEcommerce.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminCouponService {

    private final CouponRepository couponRepository;


    //method to create a coupon

    public Coupon createCoupon(Coupon coupon){
        if(couponRepository.existsByCode(coupon.getCode())){
            throw new ValidationException("Coupon code already exists..");
        }
        return couponRepository.save(coupon);
    }

    public List<Coupon> getAllCoupons(){
        return couponRepository.findAll();
    }
}
