package com.bcgogo.user.service;

import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.user.dto.CouponDTO;
import com.bcgogo.user.model.Coupon;
import com.bcgogo.user.model.UserDaoManager;
import com.bcgogo.user.model.UserWriter;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA
 * User: ztyu
 * Date: 2015/11/11
 * Time: 20:38.
 */
@Service
@Transactional
public class CouponService implements ICouponService {

  private final static Logger LOG = LoggerFactory.getLogger(ConsumingService.class);

  @Autowired
  private UserDaoManager userDaoManager;

  @Override
  public CouponDTO getCoupon(String appUserNo) {
    UserWriter userWriter = userDaoManager.getWriter();
    Coupon coupon = userWriter.getCoupon(appUserNo);
    CouponDTO couponDTO = new CouponDTO();
    couponDTO.setId(coupon.getId());
    couponDTO.setAppUserNo(coupon.getAppUserNo());
    couponDTO.setAcouponId(coupon.getId());
    couponDTO.setBalance(coupon.getBalance());
    couponDTO.setCreatedTime(coupon.getCreatedTime());
    couponDTO.setExpireTime(coupon.getExpireTime());
    return couponDTO;
  }

  @Override
  public void saveOrUpdateCoupon(CouponDTO couponDTO) {
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    Coupon coupon = null;
    try {
      if (couponDTO.getId() != null) {
        coupon = writer.getById(Coupon.class, couponDTO.getId());
      } else {
        coupon = new Coupon();
      }
      coupon.fromDTO(couponDTO);
      writer.saveOrUpdate(coupon);
      writer.commit(status);
      couponDTO.setId(coupon.getId());
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public Long getRecommendPhone(String appUserNo) {
    UserWriter userWriter = userDaoManager.getWriter();
    return userWriter.getRecommendPhone(appUserNo);
  }

  @Override
  public void saveRecommendPhone(String appUserNo, long phone ,double coupon) {
    UserWriter userWriter = userDaoManager.getWriter();
    userWriter.saveRecommendPhone(appUserNo , phone ,coupon);

  }

  @Override
  public Long getIsShared(String appUserNo) {
    UserWriter userWriter = userDaoManager.getWriter();
    return userWriter.getIsShared(appUserNo);
  }

  @Override
  public void saveIsShared(String appUserNo, int isShared ,double coupon) {
    UserWriter userWriter = userDaoManager.getWriter();
    userWriter.saveIsShared(appUserNo , isShared ,coupon);
  }

  @Override
  public void saveCoupon(String appUserNo, double coupon) {
    UserWriter userWriter = userDaoManager.getWriter();
    userWriter.saveCoupon(appUserNo ,coupon);
  }

  @Override
  public List<CouponDTO> getCouponDTOsByImei(String imei){
    UserWriter userWriter = userDaoManager.getWriter();
    List<Coupon> couponList=userWriter.getCouponsByImei(imei);
    if(CollectionUtils.isEmpty(couponList)){
      return new ArrayList<CouponDTO>();
    }
    List<CouponDTO> couponDTOList=new ArrayList<CouponDTO>();
    for(Coupon coupon:couponList){
      couponDTOList.add(coupon.toDTO());
    }
    return couponDTOList;
  }

  @Override
  public List<CouponDTO> getCouponsByAppUserNo(String appUserNo){
    UserWriter userWriter = userDaoManager.getWriter();
    List<Coupon> couponList=userWriter.getCouponsByAppUserNo(appUserNo);
    if(CollectionUtils.isEmpty(couponList)){
      return new ArrayList<CouponDTO>();
    }
    List<CouponDTO> couponDTOList=new ArrayList<CouponDTO>();
    for(Coupon coupon:couponList){
      couponDTOList.add(coupon.toDTO());
    }
    return couponDTOList;
  }

  /**
   * OBD更换,代金券进行更新
   * @param appUserNo
   * @param obdImei
   */
  @Override
  public void updateCouponForGsmOBDBind(String appUserNo,String obdImei){
    UserWriter userWriter=userDaoManager.getWriter();
    if (StringUtil.isNotEmpty(appUserNo)&&StringUtil.isNotEmpty(obdImei)){
      CouponDTO oldCouponDTO=CollectionUtil.getFirst(getCouponsByAppUserNo(appUserNo));
      CouponDTO newCouponDTO = CollectionUtil.getFirst(getCouponDTOsByImei(obdImei));
      try{
        //老代金券的appUserNo置空
        if(oldCouponDTO!=null){
          oldCouponDTO.setAppUserNo(null);
          saveOrUpdateCoupon(oldCouponDTO);
        }
        //新代金券
        //如果OBD的imei对应的代金券不存在(即新的OBD)则创建新的代金券
        if (newCouponDTO==null){
          newCouponDTO=new CouponDTO();
          newCouponDTO.setCreatedTime(System.currentTimeMillis());
          newCouponDTO.setBalance(ConfigUtils.getCouponDefaultAmount());
          newCouponDTO.setImei(obdImei);
          newCouponDTO.setAppUserNo(appUserNo);
          saveOrUpdateCoupon(newCouponDTO);
        }
        //如果OBD的imei对应的代金券存在(即旧的OBD)则将旧OBD对应的代金券appUserNo更新
        else{
          newCouponDTO.setAppUserNo(appUserNo);
          saveOrUpdateCoupon(newCouponDTO);
        }
      }catch(Exception e){
        LOG.error("OBD更换,更新代金券出错 appUserNo:"+appUserNo+" , imei:"+obdImei);
        LOG.error(e.getMessage(), e);
      }
    }

  }
}
