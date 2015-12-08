package com.bcgogo.sms;

import com.bcgogo.config.service.IConfigService;
import com.bcgogo.enums.notification.SmsType;
import com.bcgogo.enums.sms.SmsSendScene;
import com.bcgogo.notification.dto.InvitationCodeSendDTO;
import com.bcgogo.notification.dto.SmsDTO;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.service.IInvitationCodeSmsService;
import com.bcgogo.util.WebUtil;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.ShopConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 13-1-21
 * Time: 下午1:36
 */
@Controller
@RequestMapping("/promotionalSms.do")
public class PromotionalSmsController {
  public static final Logger LOG = LoggerFactory.getLogger(PromotionalSmsController.class);

  //bcgogo 邀请码促销短信
  @RequestMapping(params = "method=sentInvitationCodeSms")
  public Object sentInvitationCodeSms(HttpServletRequest request, String sendTime) {
    IInvitationCodeSmsService invitationCodeSmsService = ServiceManager.getService(IInvitationCodeSmsService.class);
    Map<String, Object> result = new HashMap<String, Object>();
    try {
      InvitationCodeSendDTO invitationCodeSendDTO = new InvitationCodeSendDTO();
      invitationCodeSendDTO.setSendTime(DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN,sendTime));
      invitationCodeSendDTO.setEliminateShopIdList(eliminateShopIds());
       SmsDTO smsDTO=new SmsDTO();
      smsDTO.setShopId(WebUtil.getShopId(request));
      smsDTO.setUserId(WebUtil.getUserId(request));
      smsDTO.setSmsType(SmsType.SMS_SENT);
      smsDTO.setEditDate(System.currentTimeMillis());
      smsDTO.setSendTime(System.currentTimeMillis());
      smsDTO.setSmsFlag(true);
      smsDTO.setSmsSendScene(SmsSendScene.SEND_INVITATION_CODE_SMS);
      invitationCodeSmsService.sendInvitationSmsForCustomersAndSuppliers(smsDTO,invitationCodeSendDTO);
      result.put("success", true);
    } catch (Exception e) {
      result.put("success", false);
      LOG.debug("/promotionalSms.do");
      LOG.debug("method=sentInvitationCodeSms");
      LOG.error(e.getMessage(), e);
    }
    result.put("success", true);
    return result;
  }


  private List<Long> eliminateShopIds() {
    String config = ServiceManager.getService(IConfigService.class).getConfig("ELIMINATE_SHOP_ID_LIST", ShopConstant.BC_SHOP_ID);
    return NumberUtil.parseLongValues(config);
  }
}
