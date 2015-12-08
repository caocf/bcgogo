package com.bcgogo.user.service;

import com.bcgogo.enums.PlansRemindStatus;
import com.bcgogo.enums.sms.SenderType;
import com.bcgogo.notification.dto.SmsJobDTO;
import com.bcgogo.notification.service.INotificationService;
import com.bcgogo.notification.smsSend.SmsException;
import com.bcgogo.remind.dto.ShopPlanDTO;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.model.ShopPlan;
import com.bcgogo.user.model.UserDaoManager;
import com.bcgogo.user.model.UserWriter;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.UserConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: zyj
 * Date: 12-4-16
 * Time: 下午5:20
 * To change this template use File | Settings | File Templates.
 */
@Component
public class ShopPlanService implements IShopPlanService {
  private static final Logger LOG = LoggerFactory.getLogger(UserService.class);
  @Autowired
  private UserDaoManager userDaoManager;

  public void sendPlanSms(ShopPlanDTO shopPlanDTO) throws SmsException {
    UserWriter writer = userDaoManager.getWriter();
    ICustomerService customerService = ServiceManager.getService(ICustomerService.class);
    SmsJobDTO smsJobDTO = shopPlanDTO.toSmsJobDTO();
    List<String> mobiles;
    if (UserConstant.CustomerType.ALL_CUSTOMER.equals(shopPlanDTO.getCustomerType())) {
      mobiles = customerService.getCustomersPhonesByShopId(shopPlanDTO.getShopId());
    } else {
      String[] customerIdsArray = shopPlanDTO.getCustomerIds().split(",");
      List<Long> customerIdsList = new ArrayList<Long>();
      for (String id : customerIdsArray) {
        customerIdsList.add(Long.valueOf(id));
      }
      mobiles = writer.getCustomersMobilesByCustomerIds(shopPlanDTO.getShopId(), customerIdsList);
    }
    if (CollectionUtil.isNotEmpty(mobiles)) {
      StringBuilder builder = new StringBuilder();
      for (String m : mobiles) {
        builder.append(m).append(",");
      }
      smsJobDTO.setReceiveMobile(builder.toString());
      smsJobDTO.setExecuteType(UserConstant.CustomerType.NORM_CUSTOMER);
      smsJobDTO.setStartTime(System.currentTimeMillis());
      smsJobDTO.setSender(SenderType.Shop);
      INotificationService notificationService = ServiceManager.getService(INotificationService.class);
      notificationService.sendSmsAsync(smsJobDTO);
    }
//    customerService.saveSmsJob(shopPlanDTO);
    //更新 ShopPlanDTO 的状态
    Object status = writer.begin();
    try {
      ShopPlan shopPlan = writer.getById(ShopPlan.class, shopPlanDTO.getId());
      shopPlan.setStatus(PlansRemindStatus.reminded);
      writer.update(shopPlan);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  public List<ShopPlanDTO> getPlans(Long shopId,  int pageNo,int pageSize,Long date,String type) {
    UserWriter writer = userDaoManager.getWriter();
    List<ShopPlan> shopPlanList = writer.getPlans(shopId, pageNo, pageSize,date,type);
    List<ShopPlanDTO> shopPlanDTOList = new ArrayList<ShopPlanDTO>();
    for (ShopPlan shopPlan : shopPlanList) {
      ShopPlanDTO shopPlanDTO = shopPlan.toDTO();
//      if (UserConstant.CustomerType.ALL_CUSTOMER.equals(shopPlanDTO.getCustomerType())) {
//        int customerNumber = (int) ServiceManager.getService(IUserService.class).countShopCustomerRecord(shopId);
//        shopPlanDTO.setCustomerNames(shopPlan.getCustomerNames() + "(" + customerNumber + "位)");
//      }
      shopPlanDTOList.add(shopPlanDTO);
    }
    return shopPlanDTOList;
  }

  public int countPlans(Long shopId, List<PlansRemindStatus> status, Long remindTime,String type) {
    UserWriter writer = userDaoManager.getWriter();
    return writer.countPlans(shopId, status, remindTime,type);
  }

  public void savePlans(List<ShopPlanDTO> shopPlanDTOList) {
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      for (ShopPlanDTO shopPlanDTO : shopPlanDTOList) {
        ShopPlan shopPlan = new ShopPlan();
        shopPlan.fromDTO(shopPlanDTO);
        writer.save(shopPlan);
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  public void dropPlan(Long shopId, Long id) {
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    ShopPlan shopPlan = writer.getById(ShopPlan.class, id);
    try {
      if (shopPlan != null) {
        shopPlan.setStatus(PlansRemindStatus.canceled);
        writer.update(shopPlan);
        writer.commit(status);
      }
    } catch (Exception e) {
      LOG.info(e.getMessage());
    } finally {
      writer.rollback(status);
    }
  }

  public int countPlansByStatus(Long shopId,PlansRemindStatus status)
  {
    UserWriter writer = userDaoManager.getWriter();
    return writer.countPlansByStatus(shopId, status);
  }

  public int countActivityPlansExpired(Long shopId,Long now)
  {
    UserWriter writer = userDaoManager.getWriter();
    return writer.countActivityPlansExpired(shopId,now);
  }

  public ShopPlanDTO savePlan(ShopPlanDTO shopPlanDTO)
  {
    if(null == shopPlanDTO)
    {
      return null;
    }
    UserWriter writer = userDaoManager.getWriter();

    ShopPlan shopPlan = new ShopPlan(shopPlanDTO);

    Object status = writer.begin();

    try{
      writer.save(shopPlan);
      writer.commit(status);
      shopPlanDTO.setId(shopPlan.getId());
      return shopPlanDTO;
    }finally {
      writer.rollback(status);
    }
  }

  public ShopPlanDTO updatePlan(ShopPlanDTO shopPlanDTO)
  {
    if(null == shopPlanDTO || null == shopPlanDTO.getId())
    {
      return null;
    }

    UserWriter writer = userDaoManager.getWriter();

    ShopPlan shopPlan = new ShopPlan(shopPlanDTO);

    Object status = writer.begin();

    try{
      shopPlan = writer.getPlan(shopPlanDTO.getShopId(),shopPlanDTO.getId());
      shopPlan.setContact(shopPlanDTO.getContact());
      shopPlan.setContent(shopPlanDTO.getContent());
      shopPlan.setCustomerIds(shopPlanDTO.getCustomerIds());
      shopPlan.setCustomerNames(shopPlanDTO.getCustomerNames());
      shopPlan.setCustomerType(shopPlanDTO.getCustomerType());
      shopPlan.setRemindTime(shopPlanDTO.getRemindTime());
      shopPlan.setRemindType(shopPlanDTO.getRemindType());
      shopPlan.setStatus(PlansRemindStatus.activity);
      shopPlan.setUserInfo(shopPlanDTO.getUserInfo());
      writer.update(shopPlan);
      writer.commit(status);
      shopPlanDTO.setId(shopPlan.getId());
      return shopPlanDTO;
    }finally {
      writer.rollback(status);
    }
  }

  public ShopPlanDTO getPlanDTO(Long shopId, Long planId) {
    if (null == planId) {
      return null;
    }

    UserWriter writer = userDaoManager.getWriter();
    ShopPlan plan = writer.getPlan(shopId, planId);
    if(plan!=null) return plan.toDTO();
    return null;
  }

  public ShopPlan updateStatus(Long shopId,Long id,PlansRemindStatus status)
  {
    if(null == id)
    {
      return null;
    }

    UserWriter writer = userDaoManager.getWriter();

    Object object = writer.begin();
    ShopPlan shopPlan = null;
    try{
      shopPlan = writer.getPlan(shopId,id);
      if(null != shopPlan)
      {
        shopPlan.setStatus(status);
        writer.update(shopPlan);
      }
      writer.commit(object);
    }finally {
      writer.rollback(object);
    }
    return shopPlan;
  }

  @Override
  public int countPlans()
  {
    UserWriter writer = userDaoManager.getWriter();

    return writer.countPlans();
  }

  public List<ShopPlan> getHundredShopPlans()
  {
    UserWriter writer = userDaoManager.getWriter();

    return writer.getHundredShopPlans();
  }

}
