package com.bcgogo.user.service.permission;

import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.enums.user.MenuType;
import com.bcgogo.enums.user.ResourceType;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.dto.permission.MenuDTO;
import com.bcgogo.user.dto.permission.MenuItemResponse;
import com.bcgogo.user.dto.permission.MenuRootResponse;
import com.bcgogo.user.model.UserDaoManager;
import com.bcgogo.user.model.permission.Resource;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * User: ZhangJuntao
 * Date: 13-4-11
 * Time: 下午5:01
 */
@Component
public class MenuService implements IMenuService {
  private static final Logger LOG = LoggerFactory.getLogger(MenuService.class);
  @Autowired
  private UserDaoManager userDaoManager;


  @Override
  public String buildMenu(long shopVersionId, long shopId, long userId, long userGroupId) throws Exception {
    Set<Long> resourceIds = new HashSet<Long>();
    List<Resource> resourceList  = ServiceManager.getService(IResourceService.class).getAllResourcesByUserGroupId(shopVersionId, userGroupId);
    for (Resource r : resourceList) {
      if (ResourceType.menu == r.getType()) {
        resourceIds.add(r.getId());
      }
    }
    Map<String, MenuRootResponse> menuRootResponseMap = new HashMap<String, MenuRootResponse>();
    List<MenuDTO> parents = new ArrayList<MenuDTO>();
    if (CollectionUtil.isNotEmpty(resourceIds)) {
      List<MenuDTO> menuDTOList = ServiceManager.getService(IResourceService.class).getMenuByResourceIds(resourceIds);
//      List<UserSwitch> userSwitches = ServiceManager.getService(IUserService.class).getUserSwitch(shopId);
      boolean isRepairPickingSwitchOn = ServiceManager.getService(IUserService.class).isRepairPickingSwitchOn(shopId);
      Iterator iterator = menuDTOList.iterator();
      MenuDTO menuDTO;
      while (iterator.hasNext()) {
        menuDTO = (MenuDTO) iterator.next();
        //特殊过滤
        if (MenuType.TXN_INVENTORY_MANAGE_REPAIR_PICKING.name().equals(menuDTO.getMenuName()) && !isRepairPickingSwitchOn) {
          iterator.remove();
        }
        //页面配置开关
        if (MenuType.WEB_SYSTEM_SETTINGS_CUSTOM_CONFIG_PAGE_CONFIG.name().equals(menuDTO.getMenuName()) && !ConfigUtils.isCustomizerConfigOpen()) {
          iterator.remove();
        }
        if (menuDTO.getParentId() == null) {
          parents.add(menuDTO);
          iterator.remove();
        }
      }
      List<MenuItemResponse> menuItemResponseList;
      MenuItemResponse response;
      MenuRootResponse rootResponse;
      for (MenuDTO root : parents) {
        root.buildTree(root, menuDTOList);
        //根据logic 做具体处理
//        sort(shopVersionId, root);
        root.reBuildTreeForSort();
        menuItemResponseList = new ArrayList<MenuItemResponse>();
        for (MenuDTO menu : root.getChildren()) {
//          if (menu.getChildren().size() > 1) {
//            String href = "";
//            //特殊root menu 需要连接
//            if (MenuType.AUTO_ACCESSORY_ONLINE_ORDER_CENTER.name().equals(menu.getMenuName())
//                || MenuType.VEHICLE_CONSTRUCTION.name().equals(menu.getMenuName())) {
//              href = root.getHref();
//            }
            response = new MenuItemResponse(menu.getLabel(), menu.getHref(), menu.getMenuName());
            for (MenuDTO m : menu.getChildren()) {
              response.getItem().add(new MenuItemResponse(m.getLabel(), m.getHref(), m.getMenuName()));
            } 
//          } else {
//            response = new MenuItemResponse(menu.getLabel(), menu.getHref(), menu.getMenuName());
//          }
          menuItemResponseList.add(response);
        }
        rootResponse = new MenuRootResponse(root.getLabel(),root.getHref(), root.getMenuName());
        rootResponse.setItem(menuItemResponseList);
        menuRootResponseMap.put(root.getMenuName(), rootResponse);
      }
    }
    return JsonUtil.mapToJson(menuRootResponseMap);
  }

  public void sort(long shopVersionId, MenuDTO root) {
    if (MenuType.AUTO_ACCESSORY_ONLINE.name().equals(root.getMenuName())) {
      IPrivilegeService privilegeService = ServiceManager.getService(IPrivilegeService.class);
      if (privilegeService.verifierShopVersionResource(shopVersionId, ResourceType.logic, "WEB.VERSION.ONLINE_ORDER_CENTER")) {
        root.setLabel("订单中心");
      } else {
        root.setLabel("采购中心");
        if (root.hasChildren()) {
          for (MenuDTO m : root.getChildren()) {
            if (MenuType.AUTO_ACCESSORY_ONLINE_COMMODITYQUOTATIONS.name().equals(m.getMenuName())) {
              m.setSort(2);
            } else if (MenuType.AUTO_ACCESSORY_ONLINE_ORDER_CENTER.name().equals(m.getMenuName())) {
              m.setSort(1);
            } else if (MenuType.AUTO_ACCESSORY_ONLINE_SHOPPINGCART.name().equals(m.getMenuName())) {
              m.setSort(3);
            } else if (MenuType.AUTO_ACCESSORY_ONLINE_RETURN_ONLINE.name().equals(m.getMenuName())) {
              m.setSort(4);
            }
          }
        }
      }
    }
  }

}
