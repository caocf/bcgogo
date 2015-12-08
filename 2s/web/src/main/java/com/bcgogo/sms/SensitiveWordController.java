package com.bcgogo.sms;

import com.bcgogo.common.PopMessage;
import com.bcgogo.common.Result;
import com.bcgogo.notification.service.SensitiveWordsService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.utils.JsonUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * User: ZhangJuntao
 * Date: 13-6-3
 * Time: 上午8:53
 */
@Controller
@RequestMapping("/sensitiveWord.do")
public class SensitiveWordController {
  public static final Logger LOG = LoggerFactory.getLogger(SensitiveWordController.class);

  /**
   * 发送并验证敏感词
   */
  @ResponseBody
  @RequestMapping(params = "method=validateSensitiveWord")
  public Object validateSensitiveWord(ModelMap model, HttpServletResponse response, Long id, String receiveMobile, String content) {
    try {
      Result result = new Result();
      String sensitiveWord = ServiceManager.getService(SensitiveWordsService.class).validateSensitiveWord(content);
      if (StringUtils.isNotBlank(sensitiveWord)) {
        result.setMsg(false, sensitiveWord);
      } else {
        result.setMsg(true, "success");
      }
      return result;
    } catch (Exception e) {
      LOG.debug("/sms.do");
      LOG.debug("method=sendSmsJob");
      LOG.error(e.getMessage(), e);
      return new Result(e.getMessage(), false);
    }
  }

}
