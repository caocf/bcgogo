package com.bcgogo.user.service;

import com.bcgogo.AbstractTest;
import com.bcgogo.enums.SystemType;
import com.bcgogo.enums.user.ResourceType;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.dto.permission.ResourceDTO;
import com.bcgogo.user.service.permission.IMenuService;
import com.bcgogo.user.service.permission.IResourceService;
import org.junit.Before;
import org.junit.Test;

/**
 * User: ZhangJuntao
 * Date: 13-4-26
 * Time: 下午1:06
 */
public class MenuServiceTest extends AbstractTest {

  @Before
  public void setUpTest() throws Exception {

  }

  @Test
  public void buildMenuTest() {
    IResourceService resourceService = ServiceManager.getService(IResourceService.class);
    ResourceDTO resourceDTO = new ResourceDTO();
    resourceDTO.setName("txn");
    resourceDTO.setValue("Txn");
    resourceDTO.setMemo("进销存");
    resourceDTO.setSystemType(SystemType.SHOP);
    resourceDTO.setType(ResourceType.menu);
    resourceService.setResource(resourceDTO);

    resourceDTO.setName("txn");
    resourceDTO.setValue("Txn");
    resourceDTO.setMemo("进销存");
    resourceDTO.setSystemType(SystemType.SHOP);
    resourceDTO.setType(ResourceType.menu);
    resourceService.setResource(resourceDTO);


    IMenuService menuService = ServiceManager.getService(IMenuService.class);
//    menuService.buildMenu();
  }
}
