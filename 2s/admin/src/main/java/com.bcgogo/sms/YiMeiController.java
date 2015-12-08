package com.bcgogo.sms;

import com.bcgogo.enums.notification.SmsChannel;
import com.bcgogo.notification.client.ClientFactory;
import com.bcgogo.notification.client.yimei.YimeiSmsParam;
import com.bcgogo.service.ServiceManager;
import org.apache.commons.lang.StringUtils;
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
 * User: ZhangJuntao
 * Date: 12-9-8
 * Time: 下午5:25
 */
@Controller
@RequestMapping("/yiMei.do")
public class YiMeiController {
  private static final Logger LOG = LoggerFactory.getLogger(YiMeiController.class);

  @RequestMapping(params = "method=options")
  @ResponseBody
  public Object options(HttpServletRequest request, HttpServletResponse response, String command) {
    Map<String, String> map = new HashMap<String, String>();
    map.put("info", "操作失败");
    YimeiSmsParam param = new YimeiSmsParam();
    try {
      if (StringUtils.isNotBlank(command)) {
        LOG.debug("执行亿美命令:" + command);
        if ("balance_industry".equals(command)) {
          param.setSmsChannel(SmsChannel.INDUSTRY);
          map.put("info", String.valueOf("余额：" + ClientFactory.getYimeiHttpSmsClient().queryBalance(param)));
        } else if ("balance_marketing".equals(command)) {
          param.setSmsChannel(SmsChannel.MARKETING);
          map.put("info", String.valueOf("余额：" + ClientFactory.getYimeiHttpSmsClient().queryBalance(param)));
        } else if ("register_industry".equals(command)) {
          param.setSmsChannel(SmsChannel.INDUSTRY);
          ClientFactory.getYimeiHttpSmsClient().register(param);
          map.put("info", "操作成功");
        } else if ("register_marketing".equals(command)) {
          param.setSmsChannel(SmsChannel.MARKETING);
          ClientFactory.getYimeiHttpSmsClient().register(param);
          map.put("info", "操作成功");
        } else if ("logout_industry".equals(command)) {
          param.setSmsChannel(SmsChannel.INDUSTRY);
          ClientFactory.getYimeiHttpSmsClient().logout(param);
          map.put("info", "操作成功");
        } else if ("logout_marketing".equals(command)) {
          param.setSmsChannel(SmsChannel.MARKETING);
          ClientFactory.getYimeiHttpSmsClient().logout(param);
          map.put("info", "操作成功");
        }
      }
    } catch (Exception e) {
      LOG.debug("/yiMei.do");
      LOG.debug("method=options");
      LOG.debug("command:" + command);
      LOG.error(e.getMessage(), e);
    }
    return map;
  }
}
