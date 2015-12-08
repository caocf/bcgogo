package com.bcgogo.admin.user.permission;

import com.bcgogo.common.AllListResult;
import com.bcgogo.common.Result;
import com.bcgogo.util.WebUtil;
import com.bcgogo.enums.SystemType;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.dto.permission.MenuDTO;
import com.bcgogo.user.dto.permission.ResourceDTO;
import com.bcgogo.user.dto.permission.ResourceSearchCondition;
import com.bcgogo.user.service.permission.IPrivilegeService;
import com.bcgogo.user.service.permission.IResourceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-7-30
 * Time: 下午2:30
 * 资源 controller
 */
@Controller
@RequestMapping("/resource.do")
public class ResourceController {
  private static final Logger LOG = LoggerFactory.getLogger(ResourceController.class);
  private IResourceService resourceService = null;

  public IResourceService getResourceService() {
    if (resourceService == null) {
      resourceService = ServiceManager.getService(IResourceService.class);
    }
    return resourceService;
  }

  @RequestMapping(params = "method=copyRoleResource")
  @ResponseBody
  public Object copyRoleResource(HttpServletRequest request, Long desRoleId, Long origRoleId) {
    Map<String, Object> result = new HashMap<String, Object>();
    try {
      if (desRoleId == null || origRoleId == null)
        throw new Exception("desRoleId:" + desRoleId + ",origRoleId:" + origRoleId);
      getResourceService().copyRoleResource(desRoleId, origRoleId);
      result.put("success", true);
      result.put("message", "操作成功!");
    } catch (Exception e) {
      LOG.debug("/admin/resource.do");
      LOG.debug("method=updateModule");
      LOG.error(e.getMessage(), e);
      result.put("success", false);
      result.put("message", "操作失败!");
    }
    return result;
  }

  @RequestMapping(params = "method=saveRoleResource")
  @ResponseBody
  public Object saveRoleResource(HttpServletRequest request, Long moduleId, Long roleId, Long resourceId, SystemType systemType) {
    Map<String, Object> result = new HashMap<String, Object>();
    try {
      Long shopId = WebUtil.getShopId(request);
      if (shopId == null) throw new Exception("shopId is null!");
      if (resourceId == null || (roleId == null && moduleId == null))
        throw new Exception("moduleId:" + moduleId + ",roleId:" + roleId + ",resourceId:" + resourceId + " is null!");
      if (roleId != null) {
        getResourceService().setRoleResource(roleId, resourceId);
      } else {
        getResourceService().saveResourceToModule(resourceId, moduleId, systemType);
      }
      result.put("success", true);
      result.put("message", "操作成功!");
    } catch (Exception e) {
      LOG.debug("/admin/permission.do");
      LOG.debug("method=updateModule");
      LOG.error(e.getMessage(), e);
      result.put("success", false);
      result.put("message", "操作失败!");
    }
    return result;
  }

  @RequestMapping(params = "method=deleteResource")
  @ResponseBody
  public Object deleteResource(HttpServletRequest request, Long resourceId) {
    Map<String, Object> result = new HashMap<String, Object>();
    try {
      Long shopId = WebUtil.getShopId(request);
      if (shopId == null) throw new Exception("shopId is null!");
      if (resourceId == null) throw new Exception("resourceId is null");
      result.put("success", true);
      result.put("message", getResourceService().deleteResource(resourceId, shopId));
    } catch (Exception e) {
      LOG.debug("/admin/resource.do");
      LOG.debug("method=deleteResource");
      LOG.error(e.getMessage(), e);
      result.put("success", false);
      result.put("message", "操作失败!");
    }
    return result;
  }

  @RequestMapping(params = "method=getResourcesByCondition")
  @ResponseBody
  public Object getResourcesByCondition(HttpServletRequest request, ResourceSearchCondition condition) {
    AllListResult<MenuDTO> result =null;
    try {
      result = getResourceService().getResourcesByCondition(condition);
    } catch (Exception e) {
      result = new AllListResult<MenuDTO>(null, false, 0);
      LOG.debug("/admin/resource.do");
      LOG.debug("method=getResourcesByCondition");
      LOG.error(e.getMessage(), e);
    }
    return result;
  }
//
//  @RequestMapping(params = "method=saveOrUpdateResource")
//  @ResponseBody
//  public Object saveOrUpdateResource(HttpServletRequest request, ResourceDTO resourceDTO) {
//    Map<String, Object> result = new HashMap<String, Object>();
//    try {
////      getResourceService().setMenu(menuDTO);
//      resourceDTO.setValue(resourceDTO.getValue().trim());
//      getResourceService().setResource(resourceDTO);
//      result.put("success", true);
//      result.put("message", "操作成功!");
//    } catch (Exception e) {
//      LOG.debug("/admin/resource.do");
//      LOG.debug("method=saveOrUpdateResource");
//      LOG.error(e.getMessage(), e);
//      result.put("success", false);
//      result.put("message", "操作失败!");
//    }
//    return result;
//  }

  @RequestMapping(params = "method=saveOrUpdateResource")
  @ResponseBody
  public Object saveOrUpdateResource(HttpServletRequest request, ResourceDTO resourceDTO) {
    Result result;
    try {
      resourceDTO.setValue(resourceDTO.getValue().trim());
      result = getResourceService().setResource(resourceDTO);
      return result;
    } catch (Exception e) {
      LOG.debug("/admin/resource.do");
      LOG.debug("method=saveOrUpdateResource");
      LOG.error(e.getMessage(), e);
      return new Result(e.getMessage(), false);
    }
  }

  @RequestMapping(params = "method=checkResourceBeforeDelete")
  @ResponseBody
  public Object checkResourceBeforeDelete(HttpServletRequest request, Long resourceId) {
    Map<String, Object> result = new HashMap<String, Object>();
    try {
      Long shopId = WebUtil.getShopId(request);
      if (shopId == null) throw new Exception("shopId is null!");
      if (resourceId == null) throw new Exception("roleId is empty");
      result.put("success", true);
      result.put("hasBeUsed", getResourceService().checkResourceBeforeDelete(resourceId, shopId));
    } catch (Exception e) {
      LOG.debug("/admin/permission.do");
      LOG.debug("method=checkRoleBeforeDelete");
      LOG.error(e.getMessage(), e);
      result.put("success", false);
      result.put("message", "操作失败!");
    }
    return result;
  }

  @RequestMapping(params = "method=getResources")
  @ResponseBody
  public Object getResources(HttpServletRequest request) {
    IPrivilegeService privilegeService = ServiceManager.getService(IPrivilegeService.class);
    List<ResourceDTO> resourceDTOList;
    try {
      resourceDTOList = privilegeService.getResourceDTOList(null, WebUtil.getUserGroupId(request));
    } catch (Exception e) {
      LOG.debug("/admin/resource.do");
      LOG.debug("method=getResources");
      LOG.error(e.getMessage(), e);
      resourceDTOList = new ArrayList<ResourceDTO>();
    }
    return resourceDTOList;
  }

  @RequestMapping(params = "method=getResourcesByRoleId")
  @ResponseBody
  public Object getResourcesByRoleId(HttpServletRequest request, Long roleId) {
    Map<String, List<ResourceDTO>> map = new HashMap<String, List<ResourceDTO>>();
    try {
      Long shopId = WebUtil.getShopId(request);
      if (shopId == null || roleId == null)
        throw new Exception("shopId or roleId is null!");
      List<ResourceDTO> resourceDTOList = ServiceManager.getService(IResourceService.class).getResourceByRoleId(roleId);
      map.put("results", resourceDTOList);
    } catch (Exception e) {
      LOG.debug("/admin/resource.do");
      LOG.debug("method=getResourcesByRole");
      LOG.error(e.getMessage(), e);
    }
    return map;
  }




}
