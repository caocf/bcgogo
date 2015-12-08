package com.bcgogo.config.customizerconfig;

import com.bcgogo.common.Result;
import com.bcgogo.common.WebUtil;
import com.bcgogo.config.CustomizerConfigInfo;
import com.bcgogo.config.CustomizerConfigResult;
import com.bcgogo.config.PageCustomizerOrderConfigRequest;
import com.bcgogo.config.PageCustomizerProductConfigRequest;
import com.bcgogo.config.dto.PageCustomizerConfigDTO;
import com.bcgogo.config.service.IShopConfigService;
import com.bcgogo.config.service.customizerconfig.IPageCustomizerConfigService;
import com.bcgogo.config.service.customizerconfig.ParserFactory;
import com.bcgogo.constant.LogicResource;
import com.bcgogo.enums.config.PageCustomizerConfigScene;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.service.permission.IPrivilegeService;
import com.bcgogo.user.verifier.PrivilegeRequestProxy;
import com.bcgogo.utils.CollectionUtil;
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
import java.util.Iterator;
import java.util.List;

/**
 * User: ZhangJuntao
 * Date: 13-5-24
 * Time: 下午4:40
 */
@Controller
@RequestMapping("/pageCustomizerConfig.do")
public class PageCustomizerConfigController {
  private static final Logger LOG = LoggerFactory.getLogger(PageCustomizerConfigController.class);
  private static final String CUSTOMIZER_CONFIG_PAGE = "/admin/customConfig/pageCustomizerConfig";

  @Autowired
  private IPageCustomizerConfigService service;


  @RequestMapping(params = "method=show")
  public String shoe(ModelMap model) {
    return CUSTOMIZER_CONFIG_PAGE;
  }

  @RequestMapping(params = "method=getProductPageConfig")
  @ResponseBody
  public Object getProductPageConfig(HttpServletRequest request, HttpServletResponse response) {
    try {
      IPrivilegeService privilegeService = ServiceManager.getService(IPrivilegeService.class);
      IShopConfigService shopConfigService = ServiceManager.getService(IShopConfigService.class);
      long shopVersionId = WebUtil.getShopVersionId(request),
          userGroupId = WebUtil.getUserGroupId(request),
          shopId = WebUtil.getShopId(request);
      CustomizerConfigInfo info;
      PageCustomizerConfigDTO<CustomizerConfigResult> result = service.getPageCustomizerConfig(WebUtil.getShopId(request), PageCustomizerConfigScene.PRODUCT);
      Iterator<CustomizerConfigInfo> iterator = result.getContentDto().getConfigInfoList().iterator();
      while (iterator.hasNext()) {
        info = iterator.next();
        if (StringUtils.isNotBlank(info.getResourceName()) &&
            !privilegeService.verifierResourceByName(shopVersionId, userGroupId, info.getResourceName())) {
          iterator.remove();
          continue;
        }
        if ("storage_bin".equals(info.getName()) && (!shopConfigService.isStorageBinSwitchOn(shopId)
            || PrivilegeRequestProxy.verifierShopVersionResourceProxy(request, LogicResource.WEB_VERSION_STOREHOUSE))) {
          iterator.remove();
          continue;
        }
        if ("trade_price".equals(info.getName())) {
          if (!shopConfigService.isTradePriceSwitchOn(shopId, shopVersionId)) {
            iterator.remove();
          }
        }
      }
      return new Result(true, result);
    } catch (Exception e) {
      LOG.debug("/pageCustomizerConfig.do");
      LOG.debug("method=getProductPageConfig");
      LOG.debug("shopId:" + WebUtil.getShopId(request));
      LOG.error(e.getMessage(), e);
      return new Result(e.getMessage(), false);
    }
  }

  @RequestMapping(params = "method=getOrderPageConfig")
  @ResponseBody
  public Object getOrderPageConfig(HttpServletRequest request, HttpServletResponse response) {
    try {

      IPrivilegeService privilegeService = ServiceManager.getService(IPrivilegeService.class);
      long shopVersionId = WebUtil.getShopVersionId(request),
          userGroupId = WebUtil.getUserGroupId(request);
      CustomizerConfigInfo info;
      CustomizerConfigResult configResult;
      PageCustomizerConfigDTO<List<CustomizerConfigResult>> result = service.getPageCustomizerConfig(WebUtil.getShopId(request), PageCustomizerConfigScene.ORDER);
      Iterator<CustomizerConfigResult> resultIterator = result.getContentDto().iterator();
      Iterator<CustomizerConfigInfo> infoIterator = null;
      while (resultIterator.hasNext()) {
        configResult = resultIterator.next();
        if (CollectionUtil.isNotEmpty(configResult.getConfigInfoList())) {
          infoIterator = configResult.getConfigInfoList().iterator();
          while (infoIterator.hasNext()) {
            info = infoIterator.next();
            if (StringUtils.isNotBlank(info.getResourceName()) &&
                !privilegeService.verifierResourceByName(shopVersionId, userGroupId, info.getResourceName())) {
              infoIterator.remove();
            }
          }
        }
        if (CollectionUtil.isEmpty(configResult.getConfigInfoList())) {
          resultIterator.remove();
        }
      }
      return new Result(true, result);
    } catch (Exception e) {
      LOG.debug("/pageCustomizerConfig.do");
      LOG.debug("method=getOrderPageConfig");
      LOG.debug("shopId:" + WebUtil.getShopId(request));
      LOG.error(e.getMessage(), e);
      return new Result(e.getMessage(), false);
    }
  }

  @RequestMapping(params = "method=updateProductPageConfig")
  @ResponseBody
  public Object updateProductPageConfig(HttpServletRequest request, HttpServletResponse response, PageCustomizerProductConfigRequest configRequest) {
    try {
      PageCustomizerConfigDTO<CustomizerConfigResult> configDTO = new PageCustomizerConfigDTO<CustomizerConfigResult>();
      configDTO.fromPageCustomizerOrderConfigDTO(configRequest);
      configDTO.setContentDto(configRequest.getContentDto());
      service.updatePageCustomizerConfig(WebUtil.getShopId(request), configDTO, ParserFactory.<CustomizerConfigResult>getParserByName(PageCustomizerConfigScene.PRODUCT));
      return new Result("保存成功！", true);
    } catch (Exception e) {
      LOG.debug("/pageCustomizerConfig.do");
      LOG.debug("method=updateProductPageConfig");
      LOG.debug("shopId:" + WebUtil.getShopId(request));
      LOG.error(e.getMessage(), e);
      return new Result(e.getMessage(), false);
    }
  }

  @RequestMapping(params = "method=updateOrderPageConfig")
  @ResponseBody
  public Object updateOrderPageConfig(HttpServletRequest request, HttpServletResponse response, PageCustomizerOrderConfigRequest configRequest) {
    try {
      PageCustomizerConfigDTO<List<CustomizerConfigResult>> configDTO = new PageCustomizerConfigDTO<List<CustomizerConfigResult>>();
      configDTO.fromPageCustomizerOrderConfigDTO(configRequest);
      configDTO.setContentDto(configRequest.getContentDto());
      service.updatePageCustomizerConfig(WebUtil.getShopId(request), configDTO, ParserFactory.<List<CustomizerConfigResult>>getParserByName(PageCustomizerConfigScene.ORDER));
      return new Result("保存成功！", true);
    } catch (Exception e) {
      LOG.debug("/pageCustomizerConfig.do");
      LOG.debug("method=updateOrderPageConfig");
      LOG.debug("shopId:" + WebUtil.getShopId(request));
      LOG.error(e.getMessage(), e);
      return new Result(e.getMessage(), false);
    }
  }

  @RequestMapping(params = "method=restorePageConfig")
  @ResponseBody
  public Object restorePageConfig(HttpServletRequest request, HttpServletResponse response, PageCustomizerConfigScene scene) {
    try {
      service.restorePageConfig(WebUtil.getShopId(request), scene);
      return new Result("还原默认设置成功！", true);
    } catch (Exception e) {
      LOG.debug("/pageCustomizerConfig.do");
      LOG.debug("method=restorePageConfig");
      LOG.debug("shopId:" + WebUtil.getShopId(request));
      LOG.error(e.getMessage(), e);
      return new Result(e.getMessage(), false);
    }
  }

}
