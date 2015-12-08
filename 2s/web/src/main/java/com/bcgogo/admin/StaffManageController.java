package com.bcgogo.admin;

import com.bcgogo.common.PagingDetailResult;
import com.bcgogo.common.WebUtil;
import com.bcgogo.enums.Sex;
import com.bcgogo.enums.user.SalesManStatus;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.assistantStat.AssistantAchievementHistoryDTO;
import com.bcgogo.txn.service.IMemberCheckerService;
import com.bcgogo.txn.service.stat.assistantStat.IAssistantStatService;
import com.bcgogo.user.dto.DepartmentDTO;
import com.bcgogo.user.dto.SalesManDTO;
import com.bcgogo.user.dto.permission.StaffSearchCondition;
import com.bcgogo.user.dto.permission.UserGroupDTO;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.user.service.permission.IStaffService;
import com.bcgogo.user.service.permission.IUserCacheService;
import com.bcgogo.user.service.permission.IUserGroupService;
import com.bcgogo.utils.SalesManConstant;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-12-18
 * Time: 上午11:42
 * 员工
 */
@Controller
@RequestMapping("/staffManage.do")
public class StaffManageController {
  private static final Logger LOG = LoggerFactory.getLogger(StaffManageController.class);

  @RequestMapping(params = "method=showStaffManagePage")
  public String showPermissionConfig(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String userGroupName= request.getParameter("userGroupName");
    String userGroupId= request.getParameter("userGroupId");
    request.setAttribute("configUserGroupName",userGroupName);
    request.setAttribute("configUserGroupId",userGroupId);
    return "/admin/staffManage/staffList";
  }

  //员工查询
  @RequestMapping(params = "method=getStaffByCondition")
  @ResponseBody
  public Object getStaffByCondition(HttpServletRequest request, StaffSearchCondition condition, Integer maxRows, Integer startPageNo) {
    PagingDetailResult<SalesManDTO,Long> result;
    IStaffService staffService = ServiceManager.getService(IStaffService.class);
    try {
      condition.setLimit(maxRows);
      condition.setStart((startPageNo - 1) * maxRows);
      condition.setStartPageNo(startPageNo);
      condition.setShopId( WebUtil.getShopId(request));
      result = staffService.getStaffByCondition(condition);
    } catch (Exception e) {
      result = new PagingDetailResult<SalesManDTO,Long>();
      result.setSuccess(false);
      LOG.debug("/staffManage.do");
      LOG.debug("method=getStaffByCondition");
      LOG.error(e.getMessage(), e);
    }
    return result;
  }

  //根据 request中的shop_id 和员工id获取该员工信息 如果id不存在 默认是新员工
  @RequestMapping(params = "method=getSaleManInfoById")
  public String getSaleManInfoById(HttpServletRequest request, Long salesManId) throws BcgogoException {
    Long shopId = WebUtil.getShopId(request);
    IUserService userService = ServiceManager.getService(IUserService.class);
    SalesManDTO salesManDTO = null;
    if (salesManId != null) {
      salesManDTO = userService.getSalesManDTOById(salesManId);
    } else {
      salesManDTO = new SalesManDTO();
      salesManDTO.setStatus(SalesManStatus.INSERVICE);
      salesManDTO.setSex(Sex.MALE.toString());
      salesManDTO.setShopId(shopId);
    }
    request.setAttribute("salesManDTO", salesManDTO);
    return "/admin/staffManage/addStaff";
  }

  //通过ID获得员工信息
  @RequestMapping(params = "method=getSalesManInfo")
  @ResponseBody
  public Object getSalesManInfo(HttpServletRequest request, Long salesManId) throws BcgogoException {

    Long shopId = WebUtil.getShopId(request);
    if(salesManId == null){
      return null;
    }
    IUserService userService = ServiceManager.getService(IUserService.class);
    SalesManDTO salesManDTO = null;
    salesManDTO = userService.getSalesManDTOById(salesManId);

    IAssistantStatService statService = ServiceManager.getService(IAssistantStatService.class);
    AssistantAchievementHistoryDTO historyDTO =  statService.getLastedAssistantAchievementHistory(shopId,salesManId,System.currentTimeMillis());
    if(historyDTO != null && salesManDTO != null){
      salesManDTO = salesManDTO.fromDTO(historyDTO);
    }
    return salesManDTO;
  }

  //检查员工工号是否重复
  @RequestMapping(params = "method=checkSalesManCode")
  @ResponseBody
  public Object checkSalesManCode(HttpServletRequest request, HttpServletResponse response, String salesManCode, Long salesManId) {
    boolean isDuplicate = true;
    try {
      Long shopId = WebUtil.getShopId(request);
      IStaffService staffService = ServiceManager.getService(IStaffService.class);
      if (shopId == null) throw new Exception("shopId is null!");
      if (StringUtils.isBlank(salesManCode)) return isDuplicate;
      isDuplicate = staffService.checkSalesManCode(salesManCode, shopId, salesManId);
    } catch (Exception e) {
      LOG.debug("/user.do");
      LOG.debug("method=checkUserNo");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
    }
    return isDuplicate;
  }


  //保存 更新 员工
  @RequestMapping(params = "method=saveOrUpdateSalesManInfo")
  public void saveOrUpdateSalesManInfo(HttpServletRequest request, SalesManDTO salesManDTO) {
    IUserService userService = ServiceManager.getService(IUserService.class);
    try {
      userService.saveOrUpdateSalesMan(salesManDTO);
    } catch (Exception e) {
      LOG.debug("/staffManage.do");
      LOG.debug("method= saveOrUpdateSalesManInfo");
      LOG.debug(salesManDTO.toString());
      LOG.debug("更新员工信息失败");
      LOG.error(e.getMessage(), e);
    }
  }

  //校验员工信息 校验 员工编号 和姓名 入职日期
  @RequestMapping(params = "method=checkAndSaveSalesManInfo")
  @ResponseBody
  public Object checkAndSaveSalesManInfo(HttpServletRequest request, SalesManDTO salesManDTO) {
    IUserService userService = ServiceManager.getService(IUserService.class);
    IMemberCheckerService memberCheckerService = ServiceManager.getService(IMemberCheckerService.class);
    Map<String, Object> result = new HashMap<String, Object>();
    Long shopId = WebUtil.getShopId(request);
    String checkResultStr = memberCheckerService.checkSalesManInfo(salesManDTO, shopId);
    if (checkResultStr.equals(SalesManConstant.SALES_MAN_INFO_VALIDATE_SUCCESS)) {
      try {
        userService.saveOrUpdateSalesMan(salesManDTO);
        IAssistantStatService assistantStatService = ServiceManager.getService(IAssistantStatService.class);
        List<SalesManDTO> salesManDTOList = new ArrayList<SalesManDTO>();
        salesManDTOList.add(salesManDTO);
        assistantStatService.updateSalesManAchievement(salesManDTO.getShopId(), salesManDTOList, WebUtil.getUserId(request));
      } catch (Exception e) {
        LOG.debug("/staffManage.do method= checkAndSaveSalesManInfo");
        LOG.debug(salesManDTO.toString());
        LOG.error(e.getMessage(), e);
      }
    }
    result.put("info",checkResultStr);
    return result;
  }

  //下拉框修改保存员工信息
  @RequestMapping(params = "method=saveSalesManInfo")
  @ResponseBody
  public Object saveSalesManInfo(HttpServletRequest request, SalesManDTO salesManDTO) {
    IUserService userService = ServiceManager.getService(IUserService.class);
    ModelMap map = new ModelMap();
    try {
      salesManDTO.setShopId(WebUtil.getShopId(request));
      if (salesManDTO.getStatusValue() != null) {
        salesManDTO.setStatus(SalesManStatus.valueOf(salesManDTO.getStatusValue()));
      }
      if (userService.saveOrUpdateSalesMan(salesManDTO) != null) {
        map.put("result", "success");

        IAssistantStatService assistantStatService = ServiceManager.getService(IAssistantStatService.class);
        List<SalesManDTO> salesManDTOList = new ArrayList<SalesManDTO>();
        salesManDTOList.add(salesManDTO);
        assistantStatService.updateSalesManAchievement(salesManDTO.getShopId(), salesManDTOList, WebUtil.getUserId(request));

      } else {
        map.put("result", "failed");
      }

    } catch (Exception e) {
      LOG.debug("/staffManage.do method= checkAndSaveSalesManInfo");
      LOG.debug(salesManDTO.toString());
      LOG.error(e.getMessage(), e);
    }
    return map;
  }

  //部门下拉建议
  @RequestMapping(params = "method=getDepartmentDropList")
  @ResponseBody
  private Map getDepartmentDropList(HttpServletRequest request, String uuid, String keyWord) throws Exception {
    Long shopId = WebUtil.getShopId(request);
    List<DepartmentDTO> departmentDTOList = ServiceManager.getService(IUserCacheService.class).getDepartmentByName(shopId, keyWord);
    Map map = new HashMap();
    map.put("uuid", uuid);
    map.put("data", departmentDTOList);
    return map;
  }

  //用户组下拉建议
  //默认是模糊匹配
  @RequestMapping(params = "method=getUserGroupDropList")
  @ResponseBody
  private Map getUserGroupDropList(HttpServletRequest request, String uuid, String keyWord, Boolean isFuzzyMatching) throws Exception {
    Long shopId = WebUtil.getShopId(request);
    Long shopVersionId = WebUtil.getShopVersion(request).getId();
    List<UserGroupDTO> departmentDTOList = ServiceManager.getService(IUserGroupService.class).getUserGroupByName(keyWord, (isFuzzyMatching == null || isFuzzyMatching), shopId, shopVersionId);
    Map map = new HashMap();
    map.put("uuid", uuid);
    map.put("data", departmentDTOList);
    return map;
  }

  //更新部门
  @RequestMapping(params = "method=updateDepartment")
  @ResponseBody
  public Object updateDepartment(HttpServletRequest request, String departmentName, Long departmentId) {
    IUserCacheService userCacheService = ServiceManager.getService(IUserCacheService.class);
    Map<String, Object> result = new HashMap<String, Object>();
    try {
      Long shopId = WebUtil.getShopId(request);
      if (StringUtils.isBlank(departmentName)) {
        result.put("success", false);
        return result;
      }
      if (shopId == null) throw new Exception("shopId is null!");
      if (userCacheService.checkDepartmentNameByShopId(departmentName, shopId)) {
        result.put("duplicate", true);
        return result;
      }
      userCacheService.updateDepartmentName(departmentName, departmentId);
      result.put("success", true);
    } catch (Exception e) {
      LOG.debug("/staffManage.do");
      LOG.debug("method=updateDepartment");
      LOG.error(e.getMessage(), e);
      result.put("success", false);
      result.put("message", "操作失败!");
    }
    return result;
  }

  @RequestMapping(params = "method=deleteDepartment")
  @ResponseBody
  public Object deleteDepartment(HttpServletRequest request, Long departmentId) {
    IUserCacheService userCacheService = ServiceManager.getService(IUserCacheService.class);
    Map<String, Object> result = new HashMap<String, Object>();
    try {
      Long shopId = WebUtil.getShopId(request);
      if (shopId == null) throw new Exception("shopId is null!");
      if (departmentId == null) throw new Exception("departmentId is empty");
      if (userCacheService.checkDepartmentForDelete(departmentId, shopId)) {
        result.put("hasBeUsed", true);
        return result;
      }
      result.put("success", true);
      result.put("message", userCacheService.deleteDepartment(departmentId, shopId));
    } catch (Exception e) {
      LOG.debug("/staffManage.do");
      LOG.debug("method=deleteDepartment");
      LOG.error(e.getMessage(), e);
      result.put("success", false);
      result.put("message", "操作失败!");
    }
    return result;
  }

  @RequestMapping(params = "method=deleteStaff")
  @ResponseBody
  public Object deleteDepartment(HttpServletRequest request, Long salesManId, Long userId) {
    IStaffService staffService = ServiceManager.getService(IStaffService.class);
    Map<String, Object> result = new HashMap<String, Object>();
    try {
      Long shopId = WebUtil.getShopId(request);
      if (shopId == null) throw new Exception("shopId is null!");
      if (salesManId == null) throw new Exception("salesManId is empty");
      result.put("success", staffService.deleteStaff(salesManId, userId));
    } catch (Exception e) {
      LOG.debug("/staffManage.do");
      LOG.debug("method=deleteStaff");
      LOG.error(e.getMessage(), e);
      result.put("success", false);
    }
    return result;
  }


}
