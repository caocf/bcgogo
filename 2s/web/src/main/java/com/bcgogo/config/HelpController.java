package com.bcgogo.config;

import com.bcgogo.common.StringUtil;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.sys.dto.help.HelpConfigDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * 处理 help 公用请求的 接口
 * @author nan.dong zhen.pan
 */
@Controller
@RequestMapping("/help.do")
public class HelpController {
  private static final Logger LOG = LoggerFactory.getLogger(HelpController.class);

  @RequestMapping(params = "method=toHelper")
  public String toHelp(ModelMap modelMap,String title,String param) {
    modelMap.addAttribute("page_param",param);
    if (StringUtil.isEmpty(title)) {
      return "helper/importDataHelper";
    } else {
      return "helper/" + title;
    }
  }

  /**
   * return Help config, you can get Help Domain from config
   * @return
   */
  @ResponseBody
  @RequestMapping(params = "method=getHelpResourceConfig")
  public Object getHelpResourceConfig() {
    try {
      IConfigService configSrv = ServiceManager.getService(IConfigService.class);
      HelpConfigDTO config = new HelpConfigDTO();

      config.setDomain(configSrv.getConfig("help_resources_domain", -1l));
      return config;
    }catch (Exception e) {
      LOG.error("获取帮助视频信息异常！");
      LOG.error(e.getMessage(), e);
      /*
       * {
       *   error:{message:"xxxxxxxxxx"}
       * }
       */
      Map<String, Map> result = new HashMap<String, Map>();
      Map<String, String> error = new HashMap<String, String>();
      error.put("message", e.getMessage());
      result.put("error", error);
      return result;
    }finally {
      LOG.info("end method=getHelpResourceConfig");
    }
  }

}
