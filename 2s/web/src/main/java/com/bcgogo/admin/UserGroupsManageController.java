package com.bcgogo.admin;

import com.bcgogo.common.Pager;
import com.bcgogo.common.WebUtil;
import com.bcgogo.enums.user.Status;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.dto.permission.UserGroupSearchCondition;
import com.bcgogo.user.permission.UserGroupSearchResult;
import com.bcgogo.user.service.permission.IUserGroupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
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
 * Date: 12-12-15
 * Time: 下午9:32
 * 用户组管理
 */
@Controller
@RequestMapping("/userGroupsManage.do")
public class UserGroupsManageController {
  private static final Logger LOG = LoggerFactory.getLogger(UserGroupsManageController.class);

  @RequestMapping(params = "method=showUserGroupsManage")
  public String showUserGroupsManage(HttpServletRequest request, HttpServletResponse response) throws IOException {
    return "/admin/permissionManager/userGroupsManage";
  }

  //userGroup Json
  @RequestMapping(params = "method=getUserGroupsByCondition")
  @ResponseBody
  public Object getUserGroupsByCondition(HttpServletRequest request, HttpServletResponse response, UserGroupSearchCondition condition, Integer maxRows, Integer startPageNo) {
    List<Object> result = new ArrayList<Object>();
    try {
      Long shopId = WebUtil.getShopId(request);
      Long shopVersionId = WebUtil.getShopVersion(request).getId();
      condition.setStatus(Status.active);
      condition.setLimit(maxRows);
      condition.setStart((startPageNo - 1) * maxRows);
      UserGroupSearchResult searchResult = ServiceManager.getService(IUserGroupService.class).getUserGroupsByCondition(shopId, shopVersionId, condition);
      result.add(searchResult.getResults());
      result.add(searchResult.getCountSystemDefault());
      result.add(searchResult.getCountCustom());
      result.add(new Pager(Integer.valueOf(Long.valueOf(searchResult.getTotalRows()).toString()), startPageNo, maxRows));
    } catch (Exception e) {
      LOG.debug("/admin/user.do");
      LOG.debug("method=getDepartmentsAndOccupations");
      LOG.error(e.getMessage(), e);
    }
    return result;
  }

  @RequestMapping(params = "method=deleteUserGroup")
  @ResponseBody
  public Object deleteUserGroup(HttpServletRequest request, Long userGroupId, Long shopVersionId) {
    Map<String, Object> result = new HashMap<String, Object>();
    try {
      IUserGroupService userGroupService=ServiceManager.getService(IUserGroupService.class);
      Long shopId = WebUtil.getShopId(request);
      if (shopId == null) throw new Exception("shopId is null!");
      if (userGroupId == null) throw new Exception("userGroupId is null!");
      if(userGroupService.checkBeforeDeleteUserGroup(userGroupId)){
        result.put("success", false);
        result.put("message", "该职位被使用!");
      } else{
        userGroupService.deleteUserGroupLogical(userGroupId);
        result.put("success", true);
        result.put("message", "操作成功!");
      }

    } catch (Exception e) {
      LOG.debug("/admin/userGroup.do");
      LOG.debug("method=deleteUserGroup");
      LOG.error(e.getMessage(), e);
      result.put("success", false);
      result.put("message", "操作失败!");
    }
    return result;
  }


}
