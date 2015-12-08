package com.bcgogo.admin.user;


import com.bcgogo.util.WebUtil;
import com.bcgogo.enums.user.DepartmentResponsibility;
import com.bcgogo.enums.user.Status;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.dto.DepartmentDTO;
import com.bcgogo.user.dto.Node;
import com.bcgogo.user.dto.OccupationDTO;
import com.bcgogo.user.dto.UserDTO;
import com.bcgogo.user.dto.permission.UserSearchCondition;
import com.bcgogo.user.permission.UserSearchResult;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.user.service.permission.IUserCacheService;
import com.bcgogo.utils.EncryptionUtil;
import com.bcgogo.utils.MemberConstant;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * Created by IntelliJ IDEA.
 * User: zhangjuntao
 * Date: 2012-11-10
 * Time: 15:35
 */
@Controller
@RequestMapping("/user.do")
public class UserController {
  private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

  public UserController() {
  }

  @RequestMapping(params = "method=getDepartmentsAndOccupations")
  @ResponseBody
  public Object getDepartmentsAndOccupations(HttpServletRequest request, HttpServletResponse response) {
    IUserCacheService userCacheService = ServiceManager.getService(IUserCacheService.class);
    Node node = null;
    try {
      node = userCacheService.getDepartmentsAndOccupations(WebUtil.getShopId(request));
    } catch (Exception e) {
      LOG.debug("/admin/user.do");
      LOG.debug("method=getDepartmentsAndOccupations");
      LOG.error(e.getMessage(), e);
    }
    return node;
  }

  @RequestMapping(params = "method=getUserSuggestionByName")
  @ResponseBody
  public Object getUserSuggestionByName(HttpServletRequest request, String name, String operateScene) {
    IUserCacheService userCacheService = ServiceManager.getService(IUserCacheService.class);
    UserSearchResult userSearchResult = null;
    try {
      Set<Long> departmentIds = null;
      if ("departmentFilter".equals(operateScene)) {
        departmentIds = userCacheService.getHeadOfficeByUserId(WebUtil.getShopId(request), WebUtil.getUserId(request));
      }
      userSearchResult = userCacheService.getUserSuggestionByName(departmentIds, name, WebUtil.getShopId(request));
    } catch (Exception e) {
      LOG.debug("/admin/user.do");
      LOG.debug("method=getUserSuggestionByName");
      LOG.error(e.getMessage(), e);
    }
    return userSearchResult;
  }


  @ResponseBody
  @RequestMapping(params = "method=getActiveUserByName")
  public Object getActiveUserByName(HttpServletRequest request, HttpServletResponse response, String name) {
    IUserCacheService userCacheService = ServiceManager.getService(IUserCacheService.class);
    if (name == null) return "";
    UserDTO dto = userCacheService.getUser(name, WebUtil.getShopId(request));
    if (dto != null) return dto.getId();
    return "";
  }


  @RequestMapping(params = "method=getUsersByCondition")
  @ResponseBody
  public Object getUsersByCondition(HttpServletRequest request, HttpServletResponse response, UserSearchCondition condition) {
    IUserCacheService userCacheService = ServiceManager.getService(IUserCacheService.class);
    UserSearchResult userSearchResult = null;
    try {
      condition.setShopId(WebUtil.getShopId(request));
      userSearchResult = userCacheService.getUsersByCondition(condition);
    } catch (Exception e) {
      LOG.debug("/admin/user.do");
      LOG.debug("method=getUsersByCondition");
      LOG.error(e.getMessage(), e);
    }
    return userSearchResult;
  }

  @RequestMapping(params = "method=getUsersByDepartmentId")
  @ResponseBody
  public Object getUsersByDepartmentId(HttpServletRequest request, HttpServletResponse response, UserSearchCondition userSearchCondition) {
    IUserCacheService userCacheService = ServiceManager.getService(IUserCacheService.class);
    UserSearchResult userSearchResult = null;
    try {
      Long shopId = WebUtil.getShopId(request);
      if (shopId == null || userSearchCondition.getDepartmentId() == null)
        throw new Exception("shopId or departmentId is null!");
      userSearchCondition.setHasPager(false);
      userSearchCondition.setShopId(shopId);
      userSearchResult = userCacheService.getUsersByDepartmentId(userSearchCondition);
      userSearchResult.setSuccess(true);
    } catch (Exception e) {
      userSearchResult = new UserSearchResult();
      userSearchResult.setSuccess(false);
      LOG.debug("/admin/user.do");
      LOG.debug("method=getUsersByDepartmentId");
      LOG.error(e.getMessage(), e);
    }
    return userSearchResult;
  }

  //CRM 设置负责人
  @RequestMapping(params = "method=updateUserDepartmentResponsibility")
  @ResponseBody
  public Object updateUserDepartmentResponsibility(HttpServletRequest request, Long userId, DepartmentResponsibility departmentResponsibility) {
    IUserCacheService userCacheService = ServiceManager.getService(IUserCacheService.class);
    Map<String, Object> result = new HashMap<String, Object>();
    try {
      Long shopId = WebUtil.getShopId(request);
      if (shopId == null) throw new Exception("shopId is null!");
      userCacheService.updateUserDepartmentResponsibility(shopId, userId, departmentResponsibility);
      result.put("success", true);
      result.put("message", "操作成功!");
    } catch (Exception e) {
      LOG.debug("/admin/user.do");
      LOG.debug("method=updateDepartment");
      LOG.error(e.getMessage(), e);
      result.put("success", false);
      result.put("message", "操作失败!");
    }
    return result;
  }

  //CRM 更新部门
  @RequestMapping(params = "method=updateDepartment")
  @ResponseBody
  public Object updateDepartment(HttpServletRequest request, Node node) {
    IUserCacheService userCacheService = ServiceManager.getService(IUserCacheService.class);
    Map<String, Object> result = new HashMap<String, Object>();
    try {
      Long shopId = WebUtil.getShopId(request);
      if (shopId == null) throw new Exception("shopId is null!");
      DepartmentDTO departmentDTO = node.toDepartmentDTO();
      if (userCacheService.checkDepartment(departmentDTO)) {
        result.put("duplicate", true);
        return result;
      }
      departmentDTO.setShopId(shopId);
      departmentDTO.setStatus(Status.active);
      departmentDTO = userCacheService.updateDepartment(departmentDTO);
      result.put("success", true);
      result.put("node", departmentDTO);
    } catch (Exception e) {
      LOG.debug("/admin/user.do");
      LOG.debug("method=updateDepartment");
      LOG.error(e.getMessage(), e);
      result.put("success", false);
      result.put("message", "操作失败!");
    }
    return result;
  }

  @RequestMapping(params = "method=resetUserPassword")
  @ResponseBody
  public Object resetUserPassword(HttpServletRequest request, String userId) {
    IUserCacheService userCacheService = ServiceManager.getService(IUserCacheService.class);
    Map<String, Object> result = new HashMap<String, Object>();
    try {
      Long shopId = WebUtil.getShopId(request);
      if (StringUtils.isBlank(userId) || shopId == null) throw new Exception("userId or shopId is empty!");
      UserDTO userDTO = userCacheService.getUser(shopId, Long.valueOf(userId));
      String passwordWithEncrypt = EncryptionUtil.encryptPassword("111111", shopId);
      userDTO.setPassword(passwordWithEncrypt);
      userCacheService.setUser(userDTO);
      result.put("success", true);
      result.put("password", passwordWithEncrypt);
    } catch (Exception e) {
      LOG.debug("/admin/user.do");
      LOG.debug("method=resetUserPassword");
      LOG.error(e.getMessage(), e);
      result.put("success", false);
      result.put("message", "操作失败!");
    }
    return result;
  }

  @RequestMapping(params = "method=updateOccupation")
  @ResponseBody
  public Object updateOccupation(HttpServletRequest request, Node node) {
    IUserCacheService userCacheService = ServiceManager.getService(IUserCacheService.class);
    Map<String, Object> result = new HashMap<String, Object>();
    try {
      Long shopId = WebUtil.getShopId(request);
      if (shopId == null) throw new Exception("shopId is null!");
      OccupationDTO occupationDTO = node.toOccupationDTO();
      if (userCacheService.checkOccupation(occupationDTO)) {
        result.put("duplicate", true);
        return result;
      }
      occupationDTO.setShopId(shopId);
      occupationDTO.setStatus(Status.active);
      occupationDTO = userCacheService.updateOccupation(occupationDTO);
      result.put("success", true);
      result.put("node", occupationDTO);
    } catch (Exception e) {
      LOG.debug("/admin/user.do");
      LOG.debug("method=updateOccupation");
      LOG.error(e.getMessage(), e);
      result.put("success", false);
      result.put("message", "操作失败!");
    }
    return result;
  }

  @RequestMapping(params = "method=checkDepartmentBeforeDelete")
  @ResponseBody
  public Object checkDepartmentBeforeDelete(HttpServletRequest request, String departmentId) {
    IUserCacheService userCacheService = ServiceManager.getService(IUserCacheService.class);
    Map<String, Object> result = new HashMap<String, Object>();
    try {
      Long shopId = WebUtil.getShopId(request);
      if (shopId == null) throw new Exception("shopId is null!");
      if (StringUtils.isBlank(departmentId)) throw new Exception("departmentId is empty");
      result.put("success", true);
      result.put("hasBeUsed", userCacheService.checkDepartmentBeforeDelete(Long.valueOf(departmentId), shopId));
    } catch (Exception e) {
      LOG.debug("/admin/user.do");
      LOG.debug("method=checkDepartmentBeforeDelete");
      LOG.error(e.getMessage(), e);
      result.put("success", false);
      result.put("message", "操作失败!");
    }
    return result;
  }

  @RequestMapping(params = "method=checkOccupationBeforeDelete")
  @ResponseBody
  public Object checkOccupationBeforeDelete(HttpServletRequest request, String occupationId) {
    IUserCacheService userCacheService = ServiceManager.getService(IUserCacheService.class);
    Map<String, Object> result = new HashMap<String, Object>();
    try {
      Long shopId = WebUtil.getShopId(request);
      if (shopId == null) throw new Exception("shopId is null!");
      if (StringUtils.isBlank(occupationId)) throw new Exception("occupationId is empty");
      result.put("success", true);
      result.put("hasBeUsed", userCacheService.checkOccupationBeforeDelete(Long.valueOf(occupationId), shopId));
    } catch (Exception e) {
      LOG.debug("/admin/user.do");
      LOG.debug("method=checkDepartmentBeforeDelete");
      LOG.error(e.getMessage(), e);
      result.put("success", false);
      result.put("message", "操作失败!");
    }
    return result;
  }

  @RequestMapping(params = "method=deleteDepartment")
  @ResponseBody
  public Object deleteDepartment(HttpServletRequest request, String departmentId) {
    IUserCacheService userCacheService = ServiceManager.getService(IUserCacheService.class);
    Map<String, Object> result = new HashMap<String, Object>();
    try {
      Long shopId = WebUtil.getShopId(request);
      if (shopId == null) throw new Exception("shopId is null!");
      if (StringUtils.isBlank(departmentId)) throw new Exception("departmentId is empty");
      result.put("success", true);
      result.put("message", userCacheService.deleteDepartment(Long.valueOf(departmentId), shopId));
    } catch (Exception e) {
      LOG.debug("/admin/user.do");
      LOG.debug("method=checkDepartmentBeforeDelete");
      LOG.error(e.getMessage(), e);
      result.put("success", false);
      result.put("message", "操作失败!");
    }
    return result;
  }

  @RequestMapping(params = "method=deleteOccupation")
  @ResponseBody
  public Object deleteOccupation(HttpServletRequest request, String occupationId) {
    IUserCacheService userCacheService = ServiceManager.getService(IUserCacheService.class);
    Map<String, Object> result = new HashMap<String, Object>();
    try {
      Long shopId = WebUtil.getShopId(request);
      if (shopId == null) throw new Exception("shopId is null!");
      if (StringUtils.isBlank(occupationId)) throw new Exception("occupationId is empty");
      result.put("success", true);
      result.put("message", userCacheService.deleteOccupation(Long.valueOf(occupationId), shopId));
    } catch (Exception e) {
      LOG.debug("/admin/user.do");
      LOG.debug("method=deleteOccupation");
      LOG.error(e.getMessage(), e);
      result.put("success", false);
      result.put("message", "操作失败!");
    }
    return result;
  }

  @RequestMapping(params = "method=saveOrUpdateUser")
  @ResponseBody
  public Object saveOrUpdateUser(HttpServletRequest request, UserDTO userDTO) {
    IUserCacheService userCacheService = ServiceManager.getService(IUserCacheService.class);
    Map<String, Object> result = new HashMap<String, Object>();
    try {
      Long shopId = WebUtil.getShopId(request);
      if (shopId == null) throw new Exception("shopId is null!");
      userDTO.setShopId(shopId);
      if (userDTO.getId() != null) {
        userCacheService.updateUser(userDTO);
      } else {
        userDTO.setPassword(EncryptionUtil.encryptPassword(userDTO.getPassword(), userDTO.getShopId()));
        userDTO = userCacheService.createUser(userDTO);
      }
      result.put("success", true);
      result.put("message", "操作成功!");
    } catch (Exception e) {
      LOG.debug("/admin/user.do");
      LOG.debug("method=saveOrUpdateUser");
      LOG.debug(userDTO.toString());
      LOG.error(e.getMessage(), e);
      result.put("success", false);
      result.put("message", "操作失败!");
    }
    return result;
  }

  @RequestMapping(params = "method=checkUserNo")
  @ResponseBody
  public Object checkUserNo(HttpServletRequest request, HttpServletResponse response) {
    boolean isDuplicate = true;
    try {
      Long shopId = WebUtil.getShopId(request);
      if (shopId == null) throw new Exception("shopId is null!");
      String userNo = request.getParameter("userNo");
      IUserCacheService userCacheService = ServiceManager.getService(IUserCacheService.class);
      isDuplicate = userCacheService.checkUserNo(userNo);
    } catch (Exception e) {
      LOG.debug("/user.do");
      LOG.debug("method=checkUserNo");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
    }
    return isDuplicate;
  }

  @RequestMapping(params = "method=deleteUser")
  @ResponseBody
  public Object deleteUser(HttpServletRequest request, HttpServletResponse response, String userIds) {
    Map<String, Object> result = new HashMap<String, Object>();
    result.put("success", true);
    result.put("message", "操作成功!");
    try {
      Long shopId = WebUtil.getShopId(request);
      if (shopId == null) throw new Exception("shopId is null!");
      if (StringUtils.isBlank(userIds)) return false;
      String[] idStrArray = userIds.split(",");
      Long[] ids = new Long[idStrArray.length];
      for (int i = 0, max = idStrArray.length; i < max; i++) {
        ids[i] = Long.valueOf(idStrArray[i]);
      }
      IUserCacheService userCacheService = ServiceManager.getService(IUserCacheService.class);
      userCacheService.deleteUserByLogic(shopId, ids);
    } catch (Exception e) {
      result.put("success", false);
      result.put("message", "操作失败!");
      LOG.debug("/user.do");
      LOG.debug("method=deleteUser");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
    }
    return result;
  }


  @RequestMapping(params = "method=updateUsersStatus")
  @ResponseBody
  public Object updateUsersStatus(HttpServletRequest request, String ids, String status) {
    Map<String, Object> result = new HashMap<String, Object>();
    result.put("success", true);
    result.put("message", "操作成功!");
    try {
      Long shopId = WebUtil.getShopId(request);
      if (shopId == null) throw new Exception("shopId is null!");
      if (StringUtils.isBlank(ids) || StringUtils.isBlank(status)) {
        LOG.warn("user id[{}] or status[{}]  is empty.", ids, status);
        return null;
      }
      String[] idsArray = ids.split(",");
      if (ArrayUtils.isEmpty(idsArray)) {
        LOG.warn("user id is empty.");
        return null;
      }
      Long[] idsLong = new Long[idsArray.length];
      for (int i = 0, max = idsArray.length; i < max; i++) {
        idsLong[i] = Long.valueOf(idsArray[i]);
      }
      ServiceManager.getService(IUserCacheService.class).updateUsersStatusByIds(Status.valueOf(status), shopId, idsLong);
    } catch (Exception e) {
      result.put("success", false);
      result.put("message", "操作失败!");
      LOG.debug("/user.do");
      LOG.debug("method=updateUsersStatus");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
    }
    return result;
  }


  @RequestMapping(params = "method=changeMemberPassword")
  public void changeMemberPassword(HttpServletRequest request, HttpServletResponse response,
                                   String memberId, String oldPw, String newPw) {
    IUserService userService = ServiceManager.getService(IUserService.class);
    Long id = Long.parseLong(memberId);
    String changeResult = "";
    try {
      changeResult = userService.changeMemberPassword(id, oldPw, newPw);
    } catch (Exception e) {
      changeResult = MemberConstant.CHANGE_PASSWORD_FAIL;
      LOG.debug("/user.do");
      LOG.debug("method=changeMemberPassword");
      LOG.debug(changeResult);
    }
    try {
      PrintWriter writer = response.getWriter();
      writer.write(changeResult);
      writer.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
