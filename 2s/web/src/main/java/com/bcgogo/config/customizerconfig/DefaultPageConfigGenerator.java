package com.bcgogo.config.customizerconfig;

import com.bcgogo.common.Result;
import com.bcgogo.config.CustomizerConfigInfo;
import com.bcgogo.config.CustomizerConfigResult;
import com.bcgogo.config.dto.PageCustomizerConfigDTO;
import com.bcgogo.config.service.customizerconfig.IPageCustomizerConfigService;
import com.bcgogo.config.service.customizerconfig.PageCustomizerConfigOrderContentParser;
import com.bcgogo.config.service.customizerconfig.PageCustomizerConfigProductContentParser;
import com.bcgogo.constant.LogicResource;
import com.bcgogo.enums.config.PageCustomizerConfigScene;
import com.bcgogo.enums.config.PageCustomizerConfigShopId;
import com.bcgogo.enums.config.PageCustomizerConfigStatus;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.dto.permission.ResourceDTO;
import com.bcgogo.user.service.permission.IResourceService;
import com.bcgogo.utils.StringUtil;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * User: ZhangJuntao
 * Date: 13-5-26
 * Time: 下午5:22
 */
@Component
public class DefaultPageConfigGenerator {

  public Result createDefaultPageConfig(HttpServletRequest request) throws Exception {
    Long shopId = PageCustomizerConfigShopId.DEFAULT.getValue();
    IPageCustomizerConfigService customizerConfigService = ServiceManager.getService(IPageCustomizerConfigService.class);
    customizerConfigService.deletePageCustomizerConfigByShopId(shopId);
    createDefaultOrderConfigPage(request, shopId);
    createDefaultProductConfigPage(request, shopId);
    return new Result("success", true);
  }

  private void createDefaultProductConfigPage(HttpServletRequest request, long shopId) throws Exception {
    IPageCustomizerConfigService customizerConfigService = ServiceManager.getService(IPageCustomizerConfigService.class);
    CustomizerConfigResult result = new CustomizerConfigResult("product", "商品", 1);
    PageCustomizerConfigDTO<CustomizerConfigResult> configDTO = new PageCustomizerConfigDTO<CustomizerConfigResult>();
    configDTO.setContentDto(result);
    configDTO.setStatus(PageCustomizerConfigStatus.ACTIVE);
    configDTO.setScene(PageCustomizerConfigScene.PRODUCT);
    configDTO.setShopId(shopId);
    result.setChecked(true);
    result.setNecessary(true);
    result.getConfigInfoList().add(createInfoList("product_info", "商品信息", 1, "", 35, true, true));
    result.getConfigInfoList().add(createInfoList("inventory", " 库存量", 2, "WEB.TXN.INVENTORY_MANAGE.STOCK_SEARCH.INVENTORY", 8));
    result.getConfigInfoList().add(createInfoList("average_price", "均价", 3, "WEB.TXN.INVENTORY_MANAGE.STOCK_SEARCH.AVERAGE_PRICE", 7));
    result.getConfigInfoList().add(createInfoList("new_storage_price", "最新价", 4, "WEB.TXN.INVENTORY_MANAGE.STOCK_SEARCH.NEW_STORAGE_PRICE", 7));
    result.getConfigInfoList().add(createInfoList("storage_bin", "货位", 5, "WEB.TXN.INVENTORY_MANAGE.STOCK_SEARCH.STORAGE_BIN", 8));
    result.getConfigInfoList().add(createInfoList("sale_price", "零售价", 6, "WEB.TXN.INVENTORY_MANAGE.STOCK_SEARCH.SALE_PRICE", 8));
    result.getConfigInfoList().add(createInfoList("trade_price", "批发价", 7, "WEB.TXN.INVENTORY_MANAGE.STOCK_SEARCH.TRADE_PRICE", 8));
    result.getConfigInfoList().add(createInfoList("product_classify", "商品分类", 8, "WEB.TXN.INVENTORY_MANAGE.STOCK_SEARCH.PRODUCT_CLASSIFY", 10));
    result.getConfigInfoList().add(createInfoList("alarm_settings", "上/下限", 9, "", 10));

    customizerConfigService.updatePageCustomizerConfig(shopId, configDTO, new PageCustomizerConfigProductContentParser());

  }

  private void createDefaultOrderConfigPage(HttpServletRequest request, long shopId) throws Exception {
    IPageCustomizerConfigService customizerConfigService = ServiceManager.getService(IPageCustomizerConfigService.class);
    CustomizerConfigResult result;
    PageCustomizerConfigDTO<List<CustomizerConfigResult>> configDTO = new PageCustomizerConfigDTO<List<CustomizerConfigResult>>();
    List<CustomizerConfigResult> customizerConfigResults = new ArrayList<CustomizerConfigResult>();

    configDTO.setStatus(PageCustomizerConfigStatus.ACTIVE);
    configDTO.setScene(PageCustomizerConfigScene.ORDER);
    configDTO.setShopId(shopId);
    configDTO.setContentDto(customizerConfigResults);

    result = new CustomizerConfigResult("order_type_condition", "单据条件", 1);
    result.getConfigInfoList().add(createInfoList("purchase_order", "采购单", 1, "WEB.TXN.PURCHASE_MANAGE.PURCHASE"));
    result.getConfigInfoList().add(createInfoList("storage_order", "入库单", 2, "WEB.TXN.PURCHASE_MANAGE.STORAGE"));
    result.getConfigInfoList().add(createInfoList("sale_order", "销售单", 3, "WEB.TXN.SALE_MANAGE.SALE"));
    result.getConfigInfoList().add(createInfoList("vehicle_construction_order", "施工单", 4, "WEB.VEHICLE_CONSTRUCTION.CONSTRUCT.BASE"));
    result.getConfigInfoList().add(createInfoList("wash_beauty_order", "洗车美容单", 5, "WEB.VEHICLE_CONSTRUCTION.WASH_BEAUTY.BASE"));
    result.getConfigInfoList().add(createInfoList("purchase_return_order", "入库退货单", 6, "WEB.TXN.PURCHASE_MANAGE.RETURN"));
    result.getConfigInfoList().add(createInfoList("sale_return_order", "销售退货单", 7, "WEB.TXN.SALE_MANAGE.RETURN"));
    result.getConfigInfoList().add(createInfoList("buy_card_order", "会员购卡续卡", 8, "WEB.CUSTOMER_MANAGER.MEMBERSHIP_PACKAGE_MANAGER"));
    result.getConfigInfoList().add(createInfoList("return_card", "会员退卡", 9, "WEB.CUSTOMER_MANAGER.MEMBERSHIP_PACKAGE_MANAGER"));
    result.getConfigInfoList().add(createInfoList("order_number", "单据号", 10, ""));
    configDTO.getContentDto().add(result);

    result = new CustomizerConfigResult("order_product_condition", "商品条件", 2);
    result.getConfigInfoList().add(createInfoList("product_info", "品名/品牌/规格/型号/适用车辆", 1, ""));
    result.getConfigInfoList().add(createInfoList("commodity_code", "商品编号", 2, ""));
    configDTO.getContentDto().add(result);

    result = new CustomizerConfigResult("order_customer_supplier_condition", "客户/供应商条件", 3);
    result.getConfigInfoList().add(createInfoList("name", "客户/供应商", 1, ""));
    result.getConfigInfoList().add(createInfoList("contact", "联系人", 2, ""));
    result.getConfigInfoList().add(createInfoList("mobile", "手机", 3, ""));
    result.getConfigInfoList().add(createInfoList("area", "所属区域", 4, ""));
    result.getConfigInfoList().add(createInfoList("member_type", "会员类型", 5, LogicResource.WEB_VERSION_MEMBER_STORED_VALUE));
    result.getConfigInfoList().add(createInfoList("member_card_no", "会员卡号", 6, LogicResource.WEB_VERSION_MEMBER_STORED_VALUE));
    result.getConfigInfoList().add(createInfoList("payPerProject", "计次收费项目", 7, LogicResource.WEB_VERSION_MEMBER_STORED_VALUE));
    configDTO.getContentDto().add(result);

    result = new CustomizerConfigResult("order_operator_condition", "操作人条件", 4);
    result.getConfigInfoList().add(createInfoList("service_worker", "施工人", 1, LogicResource.WEB_VERSION_VEHICLE_CONSTRUCTION));
    result.getConfigInfoList().add(createInfoList("salesman", "销售人", 2, ""));
    result.getConfigInfoList().add(createInfoList("operator", "操作人", 3, ""));
    configDTO.getContentDto().add(result);

    result = new CustomizerConfigResult("order_vehicle_condition", "车辆信息", 5);
    result.getConfigInfoList().add(createInfoList("license_number", "车牌号", 1, LogicResource.WEB_VERSION_VEHICLE_CONSTRUCTION));
    result.getConfigInfoList().add(createInfoList("brand", "车辆品牌", 2, ""));
    result.getConfigInfoList().add(createInfoList("model", "车型", 3, ""));
    result.getConfigInfoList().add(createInfoList("color", "车身颜色", 4, ""));
    configDTO.getContentDto().add(result);

    result = new CustomizerConfigResult("order_pay_method_condition", "结算条件", 6);
    result.getConfigInfoList().add(createInfoList("cash", "现金", 1, ""));
    result.getConfigInfoList().add(createInfoList("bankCard", "银行卡", 2, ""));
    result.getConfigInfoList().add(createInfoList("cheque", "支票", 3, ""));
    result.getConfigInfoList().add(createInfoList("deposit", "预付款", 4, ""));
    result.getConfigInfoList().add(createInfoList("customer_deposit", "预收款", 5, LogicResource.WEB_VERSION_CUSTOMER_DEPOSIT_USE));
    result.getConfigInfoList().add(createInfoList("member_balance_pay", "会员消费", 6, LogicResource.WEB_VERSION_MEMBER_STORED_VALUE));
    result.getConfigInfoList().add(createInfoList("not_paid", "欠款", 7, ""));
    result.getConfigInfoList().add(createInfoList("statement_account", "对账", 8, ""));
    result.getConfigInfoList().add(createInfoList("coupon", "消费劵", 9, LogicResource.WEB_VERSION_VEHICLE_CONSTRUCTION));
    result.getConfigInfoList().add(createInfoList("expense_amount", "消费金额", 10, ""));
    configDTO.getContentDto().add(result);


    result = new CustomizerConfigResult("order_other_condition", "其他条件", 7);
//    result.getConfigInfoList().add(createInfoList("service_worker", "所属区域", 1, ""));
    result.getConfigInfoList().add(createInfoList("order_status_repeal", "作废条件", 2, ""));
    result.getConfigInfoList().add(createInfoList("order_time", "日期条件", 3, ""));
    configDTO.getContentDto().add(result);

    customizerConfigService.updatePageCustomizerConfig(shopId, configDTO, new PageCustomizerConfigOrderContentParser());
  }

  private CustomizerConfigInfo createInfoList(String name, String value, Integer sort, String resourceName) throws Exception {
    if (StringUtil.isNotEmpty(resourceName)) {
      IResourceService resourceService = ServiceManager.getService(IResourceService.class);
      ResourceDTO resourceDTO = resourceService.getResource(resourceName);
      if (resourceDTO == null) throw new Exception("初始化失败,resource value " + resourceName + " is null");
      return new CustomizerConfigInfo(name, value, sort, resourceDTO.getName());
    }
    return new CustomizerConfigInfo(name, value, sort, "");
  }

  private CustomizerConfigInfo createInfoList(String name, String value, Integer sort, String resourceName, Integer weight) throws Exception {
    CustomizerConfigInfo info = createInfoList(name, value, sort, resourceName);
    info.setWeight(weight);
    return info;
  }

  private CustomizerConfigInfo createInfoList(String name, String value, Integer sort, String resourceName, Integer weight, boolean checked, boolean necessary) throws Exception {
    CustomizerConfigInfo info = createInfoList(name, value, sort, resourceName, weight);
    info.setChecked(checked);
    info.setNecessary(necessary);
    return info;
  }
}
