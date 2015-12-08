package com.bcgogo.user.service;

import com.bcgogo.enums.PlansRemindStatus;
import com.bcgogo.notification.smsSend.SmsException;
import com.bcgogo.remind.dto.ShopPlanDTO;
import com.bcgogo.user.model.ShopPlan;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: sl
 * Date: 12-4-16
 * Time: 下午5:18
 * To change this template use File | Settings | File Templates.
 */
public interface IShopPlanService {
  public void sendPlanSms(ShopPlanDTO shopPlanDTO) throws SmsException;

  public List<ShopPlanDTO> getPlans(Long shopId,int pageNo, int pageSize,Long now,String type);

  public int countPlans(Long shopId, List<PlansRemindStatus> status, Long remindTime,String type);

  public void savePlans(List<ShopPlanDTO> shopPlanDTOList);

  public void dropPlan(Long shopId, Long id);

  public int countPlansByStatus(Long shopId,PlansRemindStatus status);

  public int countActivityPlansExpired(Long shopId,Long now);

  public ShopPlanDTO savePlan(ShopPlanDTO shopPlanDTO);

  public ShopPlanDTO updatePlan(ShopPlanDTO shopPlanDTO);

  public ShopPlanDTO getPlanDTO(Long shopId,Long planId);

  public ShopPlan updateStatus(Long shopId,Long id,PlansRemindStatus status);

  public int countPlans();

  public List<ShopPlan> getHundredShopPlans();
}
