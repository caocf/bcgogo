package com.bcgogo.sms;

import com.bcgogo.common.Result;
import com.bcgogo.common.WebUtil;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.enums.notification.InvitationCodeStatus;
import com.bcgogo.enums.notification.InvitationCodeType;
import com.bcgogo.enums.notification.SmsType;
import com.bcgogo.enums.sms.SenderType;
import com.bcgogo.enums.sms.SmsSendScene;
import com.bcgogo.notification.dto.InvitationCodeDTO;
import com.bcgogo.notification.dto.InvitationCodeSendDTO;
import com.bcgogo.notification.dto.SmsDTO;
import com.bcgogo.notification.invitationCode.InvitationCodeGeneratorClient;
import com.bcgogo.user.service.IInvitationCodeSmsService;
import com.bcgogo.service.ServiceManager;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Created with IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 13-1-18
 * Time: 下午10:08
 */
@Controller
@RequestMapping("/invitationCodeSms.do")
public class InvitationCodeSmsController {
  public static final Logger LOG = LoggerFactory.getLogger(InvitationCodeSmsController.class);

  @Autowired
  private InvitationCodeGeneratorClient invitationCodeGeneratorClient;

  public void setInvitationCodeGeneratorClient(InvitationCodeGeneratorClient invitationCodeGeneratorClient) {
    this.invitationCodeGeneratorClient = invitationCodeGeneratorClient;
  }

  @ResponseBody
  @RequestMapping(params = "method=checkCustomerOrSupplierWithoutSendInvitationCodeSms")
  public Object checkCustomerOrSupplierWithoutSendInvitationCodeSms(HttpServletRequest request, String customerOrSupplier) {
    Result result = new Result(true);
    try {
      IInvitationCodeSmsService invitationCodeSmsService = ServiceManager.getService(IInvitationCodeSmsService.class);
      result.setTotal(Integer.valueOf(invitationCodeSmsService.checkCustomerOrSupplierWithoutSendInvitationCodeSms(customerOrSupplier, WebUtil.getShopId(request)).toString()));
    } catch (Exception e) {
      LOG.debug("/invitationCodeSms.do");
      LOG.debug("method=checkCustomerOrSupplierWithoutSendInvitationCodeSms");
      LOG.error(e.getMessage(), e);
    }
    return result;
  }

  //群发
  @RequestMapping(params = "method=sentInvitationCodePromotionalSms")
  @ResponseBody
  public Object sentInvitationCodePromotionalSms(HttpServletRequest request, String customerOrSupplier) {
    IInvitationCodeSmsService invitationCodeSmsService = ServiceManager.getService(IInvitationCodeSmsService.class);
    try {
      if (StringUtils.isBlank(customerOrSupplier)) throw new Exception("customerOrSupplier is null!");
      InvitationCodeSendDTO invitationCodeSendDTO = new InvitationCodeSendDTO();
      invitationCodeSendDTO.setSender(SenderType.Shop);
      invitationCodeSendDTO.setInvitationCodeType(InvitationCodeType.SHOP);
      invitationCodeSendDTO.setShopId(WebUtil.getShopId(request));
      invitationCodeSendDTO.setSendTime(System.currentTimeMillis());
      invitationCodeSendDTO.setEliminateMobileList(ServiceManager.getService(IConfigService.class).getSendInvitationCodeActiveShopMobile());
      SmsDTO smsDTO=new SmsDTO();
      smsDTO.setShopId(WebUtil.getShopId(request));
      smsDTO.setUserId(WebUtil.getUserId(request));
      smsDTO.setSmsType(SmsType.SMS_SENT);
      smsDTO.setEditDate(System.currentTimeMillis());
      smsDTO.setSendTime(System.currentTimeMillis());
      smsDTO.setSmsFlag(true);
      if ("CUSTOMER".equals(customerOrSupplier)) {
        invitationCodeSendDTO.setSmsSendScene(SmsSendScene.INVITE_CUSTOMER);
        smsDTO.setSmsSendScene(SmsSendScene.INVITE_CUSTOMER);
        invitationCodeSmsService.sendInvitationCodeSmsForCustomers(smsDTO,invitationCodeSendDTO);
      } else {
        invitationCodeSendDTO.setSmsSendScene(SmsSendScene.INVITE_SUPPLIER);
        smsDTO.setSmsSendScene(SmsSendScene.INVITE_SUPPLIER);
        invitationCodeSmsService.sendInvitationCodeSmsForSuppliers(smsDTO,invitationCodeSendDTO);
      }
    } catch (Exception e) {
      LOG.debug("/invitationCodeSms.do");
      LOG.debug("method=sentInvitationCodePromotionalSms");
      LOG.error(e.getMessage(), e);
      return new Result(false);
    }
    return new Result();
  }

  //单个推荐
  @RequestMapping(params = "method=sentInvitationCodeSms")
  @ResponseBody
  public Object sentInvitationCodeSms(HttpServletRequest request, String customerOrSupplier, Long id) {
    Long shopId = WebUtil.getShopId(request);
    try {
      if (id == null) throw new Exception("id is null");
      if (StringUtils.isBlank(customerOrSupplier)) throw new Exception("customerOrSupplier is null");
      IInvitationCodeSmsService invitationCodeSmsService = ServiceManager.getService(IInvitationCodeSmsService.class);
      if ("CUSTOMER".equals(customerOrSupplier)) {
        return invitationCodeSmsService.sendCustomerInvitationCodeSms(shopId, id, SenderType.Shop, SmsSendScene.INVITE_CUSTOMER);
      } else {
        return invitationCodeSmsService.sendSupplierInvitationCodeSms(shopId, id, SenderType.Shop, SmsSendScene.INVITE_SUPPLIER);
      }
    } catch (Exception e) {
      LOG.error("method=sentInvitationCodeSms,shopId :{}" + e.getMessage(), shopId, e);
      return new Result("网络异常", false);
    }
  }

  @RequestMapping(params = "method=getSystemInvite")
  @ResponseBody
  public Object getSystemInvite(HttpServletRequest request, String code,String mobile) {
    Long shopId = WebUtil.getShopId(request);
    Result result = new Result(true);
    try {
      InvitationCodeDTO dto = invitationCodeGeneratorClient.findInvitationCodeByCode(code);
      if (dto == null || dto.getStatus() != InvitationCodeStatus.OVERDUE) {
        result.setSuccess(false);
        return result;
      }
      invitationCodeGeneratorClient.updateInvitationCodeToUsed(code);
      IInvitationCodeSmsService invitationCodeSmsService = ServiceManager.getService(IInvitationCodeSmsService.class);
      dto.setMobile(mobile);
      invitationCodeSmsService.reSendInvitationCodeSms(dto);
      return result;
    } catch (Exception e) {
      result.setSuccess(false);
      LOG.error("method=sentInvitationCodeSms,shopId :{}" + e.getMessage(), shopId, e);
      return new Result("网络异常", false);
    }
  }

}
