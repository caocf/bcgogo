package com.bcgogo.admin.finance;

import com.bcgogo.common.Result;
import com.bcgogo.util.WebUtil;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.finance.AccountSearchCondition;
import com.bcgogo.txn.dto.finance.BcgogoHardwareReceivableDetailDTO;
import com.bcgogo.txn.dto.finance.BcgogoSmsRecordDTO;
import com.bcgogo.txn.dto.finance.SmsRecordSearchCondition;
import com.bcgogo.txn.service.finance.IBcgogoAccountService;
import com.bcgogo.txn.service.finance.IBcgogoReceivableService;
import com.bcgogo.txn.service.finance.ISmsAccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;


/**
 * User: zhangjuntao
 * Date: 13-3-19
 * Time: 10:34 PM
 */
@Controller
@RequestMapping("/bcgogoSmsAccount.do")
public class BcgogoSmsAccountController {
  private static final Logger LOG = LoggerFactory.getLogger(BcgogoSmsAccountController.class);
  @Autowired
  private ISmsAccountService smsAccountService = null;

  @RequestMapping(params = "method=searchBcgogoSmsRecordResult")
  @ResponseBody
  public Object searchHardwareSoftwareAccountResult(HttpServletRequest request, HttpServletResponse response, SmsRecordSearchCondition condition) {
    Result result = null;
    try {
      result = smsAccountService.searchBcgogoSmsRecordResult(condition);
    } catch (Exception e) {
      result = new Result(false);
      LOG.debug("/bcgogoSmsAccount.do");
      LOG.debug("method=searchBcgogoSmsRecordResult");
      LOG.error(e.getMessage(), e);
    }
    return result;
  }

  @RequestMapping(params = "method=getBcgogoSmsTotalAccount")
  @ResponseBody
  public Object searchHardwareSoftwareAccountResult(HttpServletRequest request, HttpServletResponse response) {
    Object result = null;
    try {
      result = smsAccountService.getBcgogoSmsTotalAccount();
    } catch (Exception e) {
      result = new Result(false);
      LOG.debug("/bcgogoSmsAccount.do");
      LOG.debug("method=getBcgogoSmsTotalAccount");
      LOG.error(e.getMessage(), e);
    }
    return result;
  }

  @RequestMapping(params = "method=createBcgogoRecharge")
  @ResponseBody
  public Object createBcgogoRecharge(HttpServletRequest request, HttpServletResponse response, BcgogoSmsRecordDTO dto) {
    Result result = new Result(true);
    try {
      dto.setOperatorId(WebUtil.getUserId(request));
      smsAccountService.createBcgogoRecharge(dto);
    } catch (Exception e) {
      LOG.debug("/bcgogoSmsAccount.do");
      LOG.debug("method=createBcgogoRecharge");
      LOG.error(e.getMessage(), e);
      result.setSuccess(false);
      result.setMsg("操作失败!");
    }
    return result;
  }

}
