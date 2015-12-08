package com.bcgogo.admin.finance;

import com.bcgogo.common.Result;
import com.bcgogo.config.dto.OperationLogDTO;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.service.IOperationLogService;
import com.bcgogo.enums.ObjectTypes;
import com.bcgogo.enums.sms.StatType;
import com.bcgogo.enums.txn.finance.SmsCategory;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.SmsRechargeDTO;
import com.bcgogo.txn.dto.finance.*;
import com.bcgogo.txn.model.PreferentialPolicy;
import com.bcgogo.txn.service.ISmsRechargeService;
import com.bcgogo.txn.service.finance.IBcgogoAccountService;
import com.bcgogo.txn.service.finance.IBcgogoReceivableService;
import com.bcgogo.txn.service.finance.ISmsAccountService;
import com.bcgogo.user.dto.UserDTO;
import com.bcgogo.user.service.permission.IUserCacheService;
import com.bcgogo.util.WebUtil;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * User: zhangjuntao
 * Date: 13-3-19
 * Time: 10:34 PM
 */
@Controller
@RequestMapping("/shopSmsAccount.do")
public class ShopSmsAccountController {
  private static final Logger LOG = LoggerFactory.getLogger(ShopSmsAccountController.class);

  @Autowired
  private ISmsAccountService smsAccountService = null;

  @RequestMapping(params = "method=searchShopSmsAccountResult")
  @ResponseBody
  public Object searchHardwareSoftwareAccountResult(HttpServletRequest request, HttpServletResponse response, SmsRecordSearchCondition condition) {
    Result result = null;
    try {
      result = smsAccountService.searchShopSmsAccountResult(condition);
    } catch (Exception e) {
      result = new Result(false);
      LOG.debug("/shopSmsAccount.do");
      LOG.debug("method=searchShopSmsAccountResult");
      LOG.error(e.getMessage(), e);
    }
    return result;
  }

  @RequestMapping(params = "method=shopSmsAccountStatistics")
  @ResponseBody
  public Object shopSmsAccountStatistics(HttpServletRequest request, HttpServletResponse response,SmsRecordSearchCondition condition) {
    Result result = new Result(true);
    try {
      result = smsAccountService.shopSmsAccountStatistics(condition);
    } catch (Exception e) {
      LOG.debug("/shopSmsAccount.do");
      LOG.debug("method=shopSmsAccountStatistics");
      LOG.error(e.getMessage(), e);
      result.setSuccess(false);
      result.setMsg("操作失败!");
    }
    return result;
  }


  @RequestMapping(params = "method=searchShopSmsRecordResult")
  @ResponseBody
  public Object searchShopSmsRecordResult(HttpServletRequest request, HttpServletResponse response, SmsRecordSearchCondition condition) {
    Result result = null;
    try {
      condition.setStatType(StatType.DAY);
      result = smsAccountService.searchShopSmsRecordResult(condition);
    } catch (Exception e) {
      result = new Result(false);
      LOG.debug("/shopSmsAccount.do");
      LOG.debug("method=searchShopSmsAccountResult");
      LOG.error(e.getMessage(), e);
    }
    return result;
  }


  @RequestMapping(params = "method=shopSmsRecordStatistics")
  @ResponseBody
  public Object shopSmsRecordStatistics(HttpServletRequest request, HttpServletResponse response,SmsRecordSearchCondition condition) {
    Result result = new Result(true);
    try {
      condition.setStatType(StatType.DAY);
      result = smsAccountService.shopSmsRecordStatistics(condition);
    } catch (Exception e) {
      LOG.debug("/shopSmsAccount.do");
      LOG.debug("method=shopSmsRecordStatistics");
      LOG.error(e.getMessage(), e);
      result.setSuccess(false);
      result.setMsg("操作失败!");
    }
    return result;
  }


  @RequestMapping(params = "method=createShopSmsRefund")
  @ResponseBody
  public Object createBcgogoRecharge(HttpServletRequest request, HttpServletResponse response, ShopSmsRecordDTO dto) {
    Result result = new Result(true);
    try {
      dto.setOperatorId(WebUtil.getUserId(request));
      dto.setSmsCategory(SmsCategory.REFUND);
      result = smsAccountService.createShopSmsRefund(dto);
    } catch (Exception e) {
      LOG.debug("/shopSmsAccount.do");
      LOG.debug("method=createShopSmsRefund");
      LOG.error(e.getMessage(), e);
      result.setSuccess(false);
      result.setMsg("操作失败!");
    }
    return result;
  }

  @RequestMapping(params = "method=searchSmsRechargeResult")
  @ResponseBody
  public Object searchSmsRechargeResult(HttpServletRequest request, HttpServletResponse response, SmsRechargeSearchCondition smsRechargeSearchCondition) {
    Result result = null;
    ISmsRechargeService smsRechargeService = ServiceManager.getService(ISmsRechargeService.class);
    try {
      result = smsRechargeService.searchSmsRechargeResult(smsRechargeSearchCondition);
    } catch (Exception e) {
      result = new Result(false);
      LOG.debug("/shopSmsAccount.do");
      LOG.debug("method=searchSmsRechargeResult");
      LOG.error(e.getMessage(), e);
    }
    return result;
  }

  @RequestMapping(params = "method=statSmsRechargeByPaymentWay")
  @ResponseBody
  public Object statSmsRechargeByPaymentWay(HttpServletRequest request, HttpServletResponse response, SmsRechargeSearchCondition smsRechargeSearchCondition) {
    Result result = null;
    ISmsRechargeService smsRechargeService = ServiceManager.getService(ISmsRechargeService.class);
    try {
      result = smsRechargeService.statSmsRechargeByPaymentWay(smsRechargeSearchCondition);
    } catch (Exception e) {
      result = new Result(false);
      LOG.debug("/shopSmsAccount.do");
      LOG.debug("method=searchSmsRechargeResult");
      LOG.error(e.getMessage(), e);
    }
    return result;
  }

  @RequestMapping(params = "method=getSmsPreferentialPolicy")
  @ResponseBody
  public Object getSmsPreferentialPolicy(HttpServletRequest request, HttpServletResponse response) {
    Result result = null;
    ISmsRechargeService smsRechargeService = ServiceManager.getService(ISmsRechargeService.class);
    try {
      result = smsRechargeService.getSmsPreferentialPolicy();
    } catch (Exception e) {
      result = new Result(false);
      LOG.debug("/shopSmsAccount.do");
      LOG.debug("method=searchSmsRechargeResult");
      LOG.error(e.getMessage(), e);
    }
    return result;
  }

  @RequestMapping(params = "method=savePreferentialSetting")
  @ResponseBody
  public Object savePreferentialSetting(HttpServletRequest request, HttpServletResponse response) {
    Result result = new Result(true);
    ISmsRechargeService smsRechargeService = ServiceManager.getService(ISmsRechargeService.class);
    try {
      String ids = request.getParameter("ids");
      String rechargeAmounts = request.getParameter("rechargeAmounts");
      String presentAmounts = request.getParameter("presentAmounts");
      smsRechargeService.savePreferentialSetting(ids,rechargeAmounts,presentAmounts);
    } catch (Exception e) {
      LOG.debug("/shopSmsAccount.do");
      LOG.debug("method=savePreferentialSetting");
      LOG.error(e.getMessage(), e);
      result.setSuccess(false);
      result.setMsg("操作失败!");
    }
    return result;
  }

  @RequestMapping(params = "method=recharge")
  @ResponseBody
  public Object recharge(HttpServletRequest request, HttpServletResponse response) {
    Result result = new Result(true);
    try {
       String shopId = request.getParameter("shopId");
       String rechargeAmount = request.getParameter("rechargeAmount");
       String payeeId = request.getParameter("payeeId");
       if(StringUtil.isEmpty(shopId) || StringUtil.isEmpty(rechargeAmount) || StringUtil.isEmpty(payeeId)) {
         result.setSuccess(false);
         result.setMsg("shopId or rechargeAmount or payeeId is null");
         return result;
       }
       Long userId = (Long) request.getSession().getAttribute("userId");
      result = smsAccountService.recharge(userId, NumberUtil.longValue(shopId), NumberUtil.longValue(payeeId), NumberUtil.doubleVal(rechargeAmount));
    } catch (Exception e) {
      LOG.debug("/shopSmsAccount.do");
      LOG.debug("method=recharge");
      LOG.error(e.getMessage(), e);
      result.setSuccess(false);
      result.setMsg("操作失败!");
    }
    return result;
  }

  @RequestMapping(params = "method=getPresentAmountByRechargeAmount")
  @ResponseBody
  public Object getPresentAmountByRechargeAmount(HttpServletRequest request, HttpServletResponse response) {
    Result result = new Result(true);
    String rechargeAmount = request.getParameter("rechargeAmount");
    if(NumberUtil.isNumber(rechargeAmount)) {
      Double presentAmount = smsAccountService.getPresentAmountByRechargeAmount(NumberUtil.doubleVal(rechargeAmount));
      result.setData(presentAmount);
    } else {
      result.setSuccess(false);
      result.setMsg("充值金额格式不正确");
    }
    return result;
  }

  @RequestMapping(params = "method=getSmsRechargeById")
  @ResponseBody
  public Object getSmsRechargeById(HttpServletRequest request, HttpServletResponse response) {
    ISmsRechargeService smsRechargeService = ServiceManager.getService(ISmsRechargeService.class);
    Result result = new Result(true);
    String smsRechargeId = request.getParameter("smsRechargeId");
    if(StringUtil.isEmpty(smsRechargeId)) {
      result.setSuccess(false);
      result.setMsg("smsRechargeId is null");
      return request;
    }
    SmsRechargeDTO smsRechargeDTO = smsRechargeService.getSmsRechargeById(NumberUtil.longValue(smsRechargeId));
    if(smsRechargeDTO != null && smsRechargeDTO.getShopId() != null) {
      ShopDTO shopDTO = ServiceManager.getService(IConfigService.class).getShopById(smsRechargeDTO.getShopId());
      if(shopDTO != null) {
        smsRechargeDTO.setShopName(shopDTO.getName());
      }
    }
    if(smsRechargeDTO.getUserId() != null) {
      UserDTO userDTO = ServiceManager.getService(IUserCacheService.class).getUserById(smsRechargeDTO.getUserId());
      if(userDTO != null) {
        smsRechargeDTO.setSubmitor(userDTO.getName());
      }
    }
    List<OperationLogDTO> operationLogDTOList = ServiceManager.getService(IOperationLogService.class).getOprationLogByObjectId(ObjectTypes.BCGOGO_SMS_RECHARGE_RECEIVABLE_ORDER, smsRechargeDTO.getId());
    if(CollectionUtil.isNotEmpty(operationLogDTOList)) {
      OperationLogDTO operationLogDTO = operationLogDTOList.get(0);
      if(operationLogDTO.getUserId() != null) {
        UserDTO userDTO = ServiceManager.getService(IUserCacheService.class).getUserById(operationLogDTO.getUserId());
        if(userDTO != null) {
          operationLogDTO.setUserName(userDTO.getName());
        }
      }
      smsRechargeDTO.setOperationLogDTO(operationLogDTO);
    }
    result.setData(smsRechargeDTO);
    return result;
  }

}
