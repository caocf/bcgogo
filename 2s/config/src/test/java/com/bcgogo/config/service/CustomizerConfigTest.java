package com.bcgogo.config.service;

import com.bcgogo.AbstractTest;
import com.bcgogo.config.CustomizerConfigInfo;
import com.bcgogo.config.CustomizerConfigResult;
import com.bcgogo.config.dto.PageCustomizerConfigDTO;
import com.bcgogo.config.service.customizerconfig.IPageCustomizerConfigService;
import com.bcgogo.config.service.customizerconfig.PageCustomizerConfigOrderContentParser;
import com.bcgogo.enums.config.PageCustomizerConfigScene;
import com.bcgogo.enums.config.PageCustomizerConfigStatus;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.utils.ShopConstant;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * User: ZhangJuntao
 * Date: 13-5-24
 * Time: 下午5:18
 */
public class CustomizerConfigTest extends AbstractTest {

  @Test
  public void updateCustomizerConfigTest() {
    IPageCustomizerConfigService customizerConfigService = ServiceManager.getService(IPageCustomizerConfigService.class);
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    configService.setConfig("CUSTOMIZER_CONFIG", "ON", ShopConstant.BC_SHOP_ID);
    List<CustomizerConfigResult> customizerConfigResults = new ArrayList<CustomizerConfigResult>();
    CustomizerConfigInfo info;
    CustomizerConfigResult result;

    PageCustomizerConfigDTO<List<CustomizerConfigResult>> configDTO = new PageCustomizerConfigDTO<List<CustomizerConfigResult>>();
    configDTO.setStatus(PageCustomizerConfigStatus.ACTIVE);
    configDTO.setScene(PageCustomizerConfigScene.ORDER);
    configDTO.setShopId(1l);
    configDTO.setContentDto(customizerConfigResults);

    result = new CustomizerConfigResult();
    result.setName("order_condition");
    result.setValue("单据条件");
    result.setChecked(true);
    result.setNecessary(true);

    info = new CustomizerConfigInfo();
    info.setChecked(true);
    info.setName("sale_order");
    info.setValue("销售单");
    info.setSort(2);
    result.getConfigInfoList().add(info);

    info = new CustomizerConfigInfo();
    info.setChecked(true);
    info.setName("采购单");
    info.setValue("品牌");
    info.setSort(1);
    result.getConfigInfoList().add(info);
    customizerConfigResults.add(result);


    result = new CustomizerConfigResult();
    result.setName("customer_supplier_condition");
    result.setValue("客户/供应商条件");
    result.setChecked(true);
    result.setNecessary(true);

    info = new CustomizerConfigInfo();
    info.setChecked(true);
    info.setName("contact");
    info.setValue("联系人");
    info.setSort(2);
    result.getConfigInfoList().add(info);

    info = new CustomizerConfigInfo();
    info.setChecked(true);
    info.setName("name");
    info.setValue("客户/供应商");
    info.setSort(1);
    result.getConfigInfoList().add(info);

    info = new CustomizerConfigInfo();
    info.setChecked(true);
    info.setName("mobile");
    info.setValue("手机");
    info.setSort(3);
    result.getConfigInfoList().add(info);

    customizerConfigResults.add(result);

    customizerConfigService.updatePageCustomizerConfig(1l, configDTO, new PageCustomizerConfigOrderContentParser());

    configDTO = customizerConfigService.getPageCustomizerConfig(1l, PageCustomizerConfigScene.ORDER);
    Assert.assertEquals(2, configDTO.getContentDto().size());

    Assert.assertTrue(configDTO.getContentDto().get(1).getConfigInfoList().get(0).getChecked());

    //修改其中一项
    result = configDTO.getContentDto().get(1);
    result.getConfigInfoList().get(0).setChecked(false);

    customizerConfigService.updatePageCustomizerConfig(1l, configDTO, new PageCustomizerConfigOrderContentParser());
    configDTO = customizerConfigService.getPageCustomizerConfig(1l, PageCustomizerConfigScene.ORDER);
    Assert.assertFalse(configDTO.getContentDto().get(1).getConfigInfoList().get(0).getChecked());
  }

}
