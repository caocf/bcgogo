package com.bcgogo.admin.user.permission;

import com.bcgogo.util.WebUtil;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.enums.user.Status;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.dto.Node;
import com.bcgogo.user.dto.permission.ShopRoleDTO;
import com.bcgogo.user.dto.permission.ShopVersionDTO;
import com.bcgogo.user.service.permission.IModuleService;
import com.bcgogo.user.service.permission.IShopVersionService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-7-30
 * Time: 下午2:30
 * shop controller (shop shopVersion)
 */
@Controller
@RequestMapping("/shopVersion.do")
public class ShopVersionController {
  private static final Logger LOG = LoggerFactory.getLogger(ShopVersionController.class);
  private IConfigService configService = null;
  private IShopVersionService shopVersionService = null;

  public IConfigService getConfigService() {
    if (configService == null) {
      configService = ServiceManager.getService(IConfigService.class);
    }
    return configService;
  }


  public IShopVersionService getShopVersionService() {
    if (shopVersionService == null) {
      shopVersionService = ServiceManager.getService(IShopVersionService.class);
    }
    return shopVersionService;
  }

  @RequestMapping(params = "method=getAllShopVersion")
  @ResponseBody
  public Object getAllShopVersion(HttpServletRequest request, HttpServletResponse response) {
    Map<String, List<ShopVersionDTO>> result = new HashMap<String, List<ShopVersionDTO>>();
    try {
      Long shopId = WebUtil.getShopId(request);
      if (shopId == null) throw new Exception("shopId is null!");
      result.put("results", getShopVersionService().getAllShopVersion());
    } catch (Exception e) {
      LOG.debug("/admin/shopVersion.do");
      LOG.debug("method=getAllShopVersion");
      LOG.error(e.getMessage(), e);
    }
    return result;
  }

  @RequestMapping(params = "method=getCommonShopVersion")
  @ResponseBody
  public Object getCommonShopVersion(HttpServletRequest request, HttpServletResponse response) {
    Map<String, List<ShopVersionDTO>> result = new HashMap<String, List<ShopVersionDTO>>();
    try {
      Long shopId = WebUtil.getShopId(request);
      if (shopId == null) throw new Exception("shopId is null!");
      result.put("results", getShopVersionService().getCommonShopVersion());
    } catch (Exception e) {
      LOG.debug("/admin/shopVersion.do");
      LOG.debug("method=getAllShopVersion");
      LOG.error(e.getMessage(), e);
    }
    return result;
  }


  @RequestMapping(params = "method=saveOrUpdateShopVersion")
  @ResponseBody
  public Object saveOrUpdateShopVersion(HttpServletRequest request, ShopVersionDTO dto) {
    Map<String, Object> result = new HashMap<String, Object>();
    try {
      getShopVersionService().saveOrUpdateShopVersion(dto);
      result.put("success", true);
      result.put("message", "操作成功!");
    } catch (Exception e) {
      LOG.debug("/admin/shopVersion.do");
      LOG.debug("method=updateModule");
      LOG.error(e.getMessage(), e);
      result.put("success", false);
      result.put("message", "操作失败!");
    }
    return result;
  }

  @RequestMapping(params = "method=deleteShopVersion")
  @ResponseBody
  public Object deleteShopVersion(HttpServletRequest request, Long shopVersionId) {
    Map<String, Object> result = new HashMap<String, Object>();
    try {
      Long shopId = WebUtil.getShopId(request);
      if (shopId == null) throw new Exception("shopId is null!");
      if (shopVersionId == null) return result;
      result.put("success", true);
      result.put("message", getShopVersionService().deleteShopVersion(shopVersionId, shopId));
    } catch (Exception e) {
      LOG.debug("/admin/resource.do");
      LOG.debug("method=deleteResource");
      LOG.error(e.getMessage(), e);
      result.put("success", false);
      result.put("message", "操作失败!");
    }
    return result;
  }

  //module role 组成的tree（包含了对应的shopType的所包含的roles）
  @RequestMapping(params = "method=getTreeModuleRolesForShopVersion")
  @ResponseBody
  public Object getTreeModules(HttpServletRequest request, Long shopVersionId) {
    Node node = null;
    try {
      Long shopId = WebUtil.getShopId(request);
      if (shopId == null) throw new Exception("shopId is null!");
      if (shopVersionId == null) return new Node();
      node = ServiceManager.getService(IModuleService.class).getTreeModuleRolesForShopVersion(shopId, shopVersionId);
    } catch (Exception e) {
      LOG.debug("/admin/user.do");
      LOG.debug("method=getDepartmentsAndOccupations");
      LOG.error(e.getMessage(), e);
    }
    return node;
  }

  //shop type 下的 某个 用户组 的角色tree
  @RequestMapping(params = "method=getTreeModuleRolesForUserGroup")
  @ResponseBody
  public Object getTreeModuleRolesForUserGroup(HttpServletRequest request, Long shopVersionId, Long userGroupId) {
    Node node = null;
    try {
      Long shopId = WebUtil.getShopId(request);
      if (shopId == null) throw new Exception("shopId is null!");
      if (shopVersionId == null || userGroupId == null) return new Node();
      node = ServiceManager.getService(IModuleService.class).getTreeModuleRolesForUserGroup(shopId, shopVersionId, userGroupId);
    } catch (Exception e) {
      LOG.debug("/admin/user.do");
      LOG.debug("method=getDepartmentsAndOccupations");
      LOG.error(e.getMessage(), e);
    }
    return node;
  }

  @RequestMapping(params = "method=saveRolesConfigForShopVersion")
  @ResponseBody
  public Object saveRolesConfigForShopType(HttpServletRequest request, HttpServletResponse response, Long versionId) {
    Map<String, Object> result = new HashMap<String, Object>();
    try {
      List<ShopRoleDTO> shopRoleDTOList = new ArrayList<ShopRoleDTO>();
      Long shopId = WebUtil.getShopId(request);
      if (shopId == null) throw new Exception("shopId is null!");
      String ids=request.getParameter("roleIds");
      if (StringUtils.isNotBlank(ids)) {
        String[] roleIds = ids.split(",");
        ShopRoleDTO shopRoleDTO = null;
        for (int i = 0, max = roleIds.length; i < max; i++) {
          shopRoleDTO = new ShopRoleDTO();
          shopRoleDTO.setRoleId(Long.valueOf(roleIds[i]));
          shopRoleDTO.setShopVersionId(versionId);
          shopRoleDTO.setStatus(Status.active);
          shopRoleDTO.setStatus(Status.inActive);
          shopRoleDTOList.add(shopRoleDTO);
        }
      }
      result.put("success", getShopVersionService().saveRolesConfigForShopVersion(shopRoleDTOList, versionId));
    } catch (Exception e) {
      result.put("success", false);
      LOG.debug("/admin/shopVersion.do");
      LOG.debug("method=saveRolesConfigForShopVersion");
      LOG.error(e.getMessage(), e);
    }
    return result;
  }


}
