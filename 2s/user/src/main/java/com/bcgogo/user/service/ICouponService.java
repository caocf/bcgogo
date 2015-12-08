package com.bcgogo.user.service;

import com.bcgogo.user.dto.CouponDTO;

import java.util.List;

/**
 * Created by IntelliJ IDEA
 * User: ztyu
 * Date: 2015/11/11
 * Time: 20:37.
 */
public interface ICouponService {

  CouponDTO getCoupon(String appUserNo);

  void saveOrUpdateCoupon(CouponDTO couponDTO);

  Long getRecommendPhone(String appUserNo);

  void saveRecommendPhone(String appUserNo , long phone ,double coupon);

  Long getIsShared(String appUserNo);

  void saveIsShared(String appUserNo , int isShared , double coupon);

  void saveCoupon(String appUserNo , double coupon);

  List<CouponDTO> getCouponDTOsByImei(String imei);

  List<CouponDTO> getCouponsByAppUserNo(String appUserNo);

  void updateCouponForGsmOBDBind(String appUserNo,String obdImei);
}
