package com.bcgogo.user.service;

import com.bcgogo.common.TreeMenuDTO;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.model.TreeMenu;
import com.bcgogo.user.model.UserDaoManager;
import com.bcgogo.user.model.UserWriter;
import com.bcgogo.user.service.permission.IRoleService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-11-9
 * Time: 下午5:21
 */
@Component
public class ViewerService implements IViewerService {
  private static final Logger LOG = LoggerFactory.getLogger(ViewerService.class);
  @Autowired
  private UserDaoManager userDaoManager;

  @Override
  public List<TreeMenuDTO> getTreeMenuByParentId(Long shopId, Long userId, Long treeId) {
    List<TreeMenuDTO> treeMenuDTOList = new ArrayList<TreeMenuDTO>();
    UserWriter writer = userDaoManager.getWriter();
    List<Long> roleIds = null;
    if (treeId != null) {
      IRoleService roleService = ServiceManager.getService(IRoleService.class);
      roleIds = roleService.getRolesByUserId(shopId, userId);
      if (CollectionUtils.isEmpty(roleIds)) return treeMenuDTOList;
    }
    List<TreeMenu> treeMenuList = writer.getTreeMenuByParentId(treeId, roleIds);
    for (TreeMenu treeMenu : treeMenuList) {
      treeMenuDTOList.add(treeMenu.toDTO());
    }
    return treeMenuDTOList;
  }
}
