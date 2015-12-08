package com.bcgogo.user.service.utils;

import com.bcgogo.config.service.IConfigService;
import com.bcgogo.constant.LogicResource;
import com.bcgogo.enums.user.ResourceType;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.service.permission.IPrivilegeService;
import com.bcgogo.utils.ShopConstant;
import org.apache.commons.lang.StringUtils;

/**
 * Created by IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 12-9-10
 * Time: 下午7:02
 * To change this template use File | Settings | File Templates.
 */
public  class BcgogoShopLogicResourceUtils {
	//只有小型店铺不需要校验库存
	public static boolean isIgnoreVerifierInventoryResource(Long shopVersionId) {
    IPrivilegeService privilegeService = ServiceManager.getService(IPrivilegeService.class);
    return privilegeService.verifierShopVersionResource(shopVersionId, ResourceType.logic, LogicResource.WEB_VERSION_IGNORE_VERIFIER_INVENTORY);
  }

  public static boolean isHaveStoreHouseResource(Long shopVersionId) {
    IPrivilegeService privilegeService = ServiceManager.getService(IPrivilegeService.class);
    return privilegeService.verifierShopVersionResource(shopVersionId, ResourceType.logic, LogicResource.WEB_VERSION_STOREHOUSE);
  }

  public static boolean isMemberStoredValue(Long shopVersionId) {
    IPrivilegeService privilegeService = ServiceManager.getService(IPrivilegeService.class);
    return privilegeService.verifierShopVersionResource(shopVersionId, ResourceType.logic, LogicResource.WEB_VERSION_MEMBER_STORED_VALUE);
  }

  public static boolean isVehicleConstruction(Long shopVersionId) {
    IPrivilegeService privilegeService = ServiceManager.getService(IPrivilegeService.class);
    return privilegeService.verifierShopVersionResource(shopVersionId, ResourceType.logic, LogicResource.WEB_VERSION_VEHICLE_CONSTRUCTION);
  }

  /**
   * cfl 加的
   * @param shopVersionId
   * @return
   */
  @Deprecated
  public static boolean isWholesalers(Long shopVersionId) {
    IPrivilegeService privilegeService = ServiceManager.getService(IPrivilegeService.class);
    return privilegeService.verifierShopVersionResource(shopVersionId, ResourceType.logic, LogicResource.WEB_VERSION_WHOLESALERS);
  }

  public static boolean isDisableUnrelatedSupplierCommodityQuotations(Long shopVersionId) {
    IPrivilegeService privilegeService = ServiceManager.getService(IPrivilegeService.class);
    return privilegeService.verifierShopVersionResource(shopVersionId, ResourceType.logic, LogicResource.WEB_VERSION_DISABLE_UNRELATED_SUPPLIER_COMMODITYQUOTATIONS);
  }

  public static boolean isNotActiveRecommendSupplier(Long shopVersionId) {
    IPrivilegeService privilegeService = ServiceManager.getService(IPrivilegeService.class);
    return !privilegeService.verifierShopVersionResource(shopVersionId, ResourceType.logic, LogicResource.WEB_VERSION_ACTIVE_RECOMMEND_SUPPLIER);
  }

  public static boolean isThroughSelectSupplier(Long shopVersionId) {
    IPrivilegeService privilegeService = ServiceManager.getService(IPrivilegeService.class);
    return privilegeService.verifierShopVersionResource(shopVersionId, ResourceType.logic, LogicResource.WEB_VERSION_PRODUCT_THROUGH_SELECT_SUPPLIER);
  }

}
