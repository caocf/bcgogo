package com.bcgogo.sms;

import com.bcgogo.common.Result;
import com.bcgogo.config.model.SmsDonationLog;
import com.bcgogo.config.service.IShopBalanceService;
import com.bcgogo.config.service.ISmsDonationLogService;
import com.bcgogo.enums.DonationType;
import com.bcgogo.enums.txn.finance.SmsCategory;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.finance.ShopSmsRecordDTO;
import com.bcgogo.txn.service.finance.ISmsAccountService;
import com.bcgogo.util.WebUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Created with IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 13-1-21
 * Time: 下午1:36
 * 短信赠送
 */
@Controller
@RequestMapping("/smsDonate.do")
public class SmsDonateController {
  public static final Logger LOG = LoggerFactory.getLogger(SmsDonateController.class);

  //增加赠送
  @RequestMapping(params = "method=addSmsDonate")
  @ResponseBody
  public Object addSmsDonate(HttpServletRequest request, Long shopId, Double smsDonationValue) {
    try {
      ShopSmsRecordDTO shopSmsRecordDTO = new ShopSmsRecordDTO();
      shopSmsRecordDTO.setSmsCategory(SmsCategory.RECOMMEND_HANDSEL);
      shopSmsRecordDTO.setNumber(Math.round(smsDonationValue * 10));
      shopSmsRecordDTO.setBalance(smsDonationValue);
      shopSmsRecordDTO.setShopId(shopId);
      shopSmsRecordDTO.setOperatorId(WebUtil.getUserId(request));
      ServiceManager.getService(ISmsAccountService.class).createShopSmsHandsel(shopSmsRecordDTO);
      ServiceManager.getService(IShopBalanceService.class).addSmsBalance(shopId, smsDonationValue);
      //短信赠送记录
      SmsDonationLog smsDonationLog = new SmsDonationLog();
      smsDonationLog.setShopId(shopId);
      smsDonationLog.setDonationType(DonationType.SMS_BACKGROUND_DONATION);
      smsDonationLog.setValue(smsDonationValue);
      smsDonationLog.setDonationTime(System.currentTimeMillis());
      ServiceManager.getService(ISmsDonationLogService.class).createSmsDonationLog(smsDonationLog);
      return new Result("赠送短信成功！", true);
    } catch (Exception e) {
      LOG.debug("/smsDonate.do");
      LOG.debug("method=addSmsDonate");
      LOG.error(e.getMessage(), e);
      return new Result(e.getMessage(), true);
    }
  }


}
