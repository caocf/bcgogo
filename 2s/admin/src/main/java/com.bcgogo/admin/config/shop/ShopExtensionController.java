package com.bcgogo.admin.config.shop;

import com.bcgogo.common.Result;
import com.bcgogo.txn.service.solr.IShopSolrWriterService;
import com.bcgogo.util.WebUtil;
import com.bcgogo.config.dto.ShopExtensionLogDTO;
import com.bcgogo.config.service.IShopExtensionService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.dto.UserDTO;
import com.bcgogo.user.service.permission.IUserCacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * User: ZhangJuntao
 * Date: 13-3-31
 * Time: 下午3:33
 */
@Controller
@RequestMapping("/shopExtension.do")
public class ShopExtensionController {
  private static final Logger LOG = LoggerFactory.getLogger(ShopExtensionController.class);

  @RequestMapping(params = "method=getShopExtensionLogs")
  @ResponseBody
  public Object getShopExtensionLogs(HttpServletRequest request, HttpServletResponse response, long shopId) {
    IShopExtensionService extensionService = ServiceManager.getService(IShopExtensionService.class);
    Result result = new Result(true);
    try {
      List<ShopExtensionLogDTO> dtoList = extensionService.getShopExtensionLogs(shopId);
      Set<Long> userIds = new HashSet<Long>();
      for (ShopExtensionLogDTO dto : dtoList) {
        userIds.add(dto.getOperatorId());
      }
      UserDTO userDTO;
      Map<Long, UserDTO> userDTOMap = ServiceManager.getService(IUserCacheService.class).getUserMap(userIds);
      for (ShopExtensionLogDTO dto : dtoList) {
        userDTO = userDTOMap.get(dto.getOperatorId());
        if (userDTO != null) dto.setOperatorName(userDTO.getName());
      }
      result.setData(dtoList);
      result.setTotal(dtoList.size());
    } catch (Exception e) {
      result.setSuccess(false);
      LOG.debug("/shopExtension.do");
      LOG.debug("method=getShopExtensionLogs");
      LOG.error(e.getMessage(), e);
    }
    return result;
  }

  @RequestMapping(params = "method=hasShopExtensionLogs")
  @ResponseBody
  public Object hasShopExtensionLogs(HttpServletRequest request, HttpServletResponse response, long shopId) {
    IShopExtensionService extensionService = ServiceManager.getService(IShopExtensionService.class);
    try {
      return extensionService.hasShopExtensionLogs(shopId);
    } catch (Exception e) {
      LOG.debug("/shopExtension.do");
      LOG.debug("method=getShopExtensionLogs");
      LOG.error(e.getMessage(), e);
    }
    return false;
  }

  @RequestMapping(params = "method=createShopExtensionLog")
  @ResponseBody
  public Object createShopExtensionLog(HttpServletRequest request, HttpServletResponse response,
                                       long shopId, int extensionDays, String reason) {
    Result result = new Result(true);
    try {
      IShopExtensionService extensionService = ServiceManager.getService(IShopExtensionService.class);
      extensionService.createShopExtensionLog(shopId, extensionDays, WebUtil.getUserId(request), reason);
      try {
        ServiceManager.getService(IShopSolrWriterService.class).reCreateShopIdSolrIndex(shopId);
      } catch (Exception e) {
        LOG.error(e.getMessage(), e);
      }
      result.setMsg("延期操作成功！");
    } catch (Exception e) {
      LOG.debug("/shopExtension.do");
      LOG.debug("method=createShopExtensionLog");
      LOG.error(e.getMessage(), e);
      result.setSuccess(false);
      result.setMsg("操作失败!");
    }
    return result;
  }


}
