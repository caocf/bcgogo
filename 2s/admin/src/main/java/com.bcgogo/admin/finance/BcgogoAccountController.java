package com.bcgogo.admin.finance;

import com.bcgogo.common.Result;
import com.bcgogo.util.WebUtil;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.finance.AccountSearchCondition;
import com.bcgogo.txn.dto.finance.BcgogoHardwareReceivableDetailDTO;
import com.bcgogo.txn.dto.finance.BcgogoReceivableDTO;
import com.bcgogo.txn.dto.finance.BcgogoReceivableSearchCondition;
import com.bcgogo.txn.service.finance.IBcgogoAccountService;
import com.bcgogo.txn.service.finance.IBcgogoReceivableService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@RequestMapping("/bcgogoAccount.do")
public class BcgogoAccountController {
  private static final Logger LOG = LoggerFactory.getLogger(BcgogoAccountController.class);

  @RequestMapping(params = "method=searchHardwareSoftwareAccountResult")
  @ResponseBody
  public Object searchHardwareSoftwareAccountResult(HttpServletRequest request, HttpServletResponse response, AccountSearchCondition condition) {
    IBcgogoAccountService accountService = ServiceManager.getService(IBcgogoAccountService.class);
    Result result = null;
    try {
      result = accountService.searchHardwareSoftwareAccountResult(condition);
    } catch (Exception e) {
      result = new Result(false);
      LOG.debug("/bcgogoAccount.do");
      LOG.debug("method=searchHardwareSoftwareAccountResult");
      LOG.error(e.getMessage(), e);
    }
    return result;
  }

  @RequestMapping(params = "method=countHardwareSoftwareAccount")
  @ResponseBody
  public Object searchHardwareSoftwareDetails(HttpServletRequest request, HttpServletResponse response,AccountSearchCondition condition) {
    IBcgogoAccountService accountService = ServiceManager.getService(IBcgogoAccountService.class);
    Map<String, Object> result;
    try {
      result = accountService.countHardwareSoftwareAccount(condition);
    } catch (Exception e) {
      result = new HashMap<String, Object>();
      result.put("success", false);
      LOG.debug("/bcgogoAccount.do");
      LOG.debug("method=searchHardwareSoftwareAccountResult");
      LOG.error(e.getMessage(), e);
    }
    return result;
  }

}
