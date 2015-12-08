package com.bcgogo.admin;

import com.bcgogo.util.WebUtil;
import com.bcgogo.config.CRMOperationLogCondition;
import com.bcgogo.config.CRMOperationLogResult;
import com.bcgogo.config.service.ICRMOperationLogService;
import com.bcgogo.service.ServiceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-11-29
 * Time: 下午8:11
 * 日志 controller
 */
@Controller
@RequestMapping("/CRMOperationLog.do")
public class CRMOperationLogController {
  private static final Logger LOG = LoggerFactory.getLogger(CRMOperationLogController.class);

  @RequestMapping(params = "method=getCRMOperationLogsByCondition")
  @ResponseBody
  public Object getCRMOperationLogsByCondition(HttpServletRequest request, CRMOperationLogCondition condition) {
    ICRMOperationLogService logService = ServiceManager.getService(ICRMOperationLogService.class);
    CRMOperationLogResult logResult = new CRMOperationLogResult();
    try {
      Long shopId = WebUtil.getShopId(request);
      condition.setShopId(shopId);
      if (shopId == null) throw new Exception("shopId is null!");
      logResult = logService.getLogsByCondition(condition);
    } catch (Exception e) {
      LOG.debug("/admin/log.do");
      LOG.debug("method=getCRMOperationLogsByCondition");
      LOG.error(e.getMessage(), e);
    }
    return logResult;
  }
}
