package com.bcgogo.txn.service.ShopRelation;

import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.dto.ShopServiceCategoryDTO;
import com.bcgogo.config.model.ConfigWriter;
import com.bcgogo.config.model.WholesalerShopRelation;
import com.bcgogo.config.service.IApplyService;
import com.bcgogo.config.service.IServiceCategoryService;
import com.bcgogo.enums.RelationTypes;
import com.bcgogo.enums.ShopRelationStatus;
import com.bcgogo.enums.config.VehicleSelectBrandModel;
import com.bcgogo.product.StandardBrandModelCache.StandardBrandModelCache;
import com.bcgogo.product.service.IProductService;
import com.bcgogo.product.standardVehicleBrandModel.ShopVehicleBrandModelDTO;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.service.RFITxnService;
import com.bcgogo.txn.service.recommend.IPreciseRecommendService;
import com.bcgogo.user.dto.CustomerDTO;
import com.bcgogo.user.dto.SupplierDTO;
import com.bcgogo.user.service.ICustomerService;
import com.bcgogo.user.service.ISupplierService;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.utils.CollectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-8-26
 * Time: 下午7:10
 */
@Component
public class ShopRelationService implements IShopRelationService{
  private static final Logger LOG = LoggerFactory.getLogger(ShopRelationService.class);

  @Override
  public SupplierDTO collectSupplierShop(ShopDTO customerShopDTO, ShopDTO supplierShopDTO) throws Exception {
    if (customerShopDTO == null || supplierShopDTO == null || customerShopDTO.getId() == null || supplierShopDTO.getId() == null) {
      LOG.error("collectSupplierShop shopDTO is null ,customerShopDTO:{},supplierShopDTO:{}", customerShopDTO, supplierShopDTO);
      return null;
    }
    IApplyService applyService = ServiceManager.getService(IApplyService.class);
    RFITxnService rfiTxnService = ServiceManager.getService(RFITxnService.class);
    IUserService userService = ServiceManager.getService(IUserService.class);
    IPreciseRecommendService preciseRecommendService = ServiceManager.getService(IPreciseRecommendService.class);
    ISupplierService supplierService = ServiceManager.getService(ISupplierService.class);
    IProductService productService = ServiceManager.getService(IProductService.class);
    applyService.collectSupplierShopUpdateRelation(customerShopDTO.getId(), supplierShopDTO.getId());
    SupplierDTO supplierDTO = rfiTxnService.createRelationSupplier(customerShopDTO, supplierShopDTO, RelationTypes.APPLY_RELATED);

    //保存客户和供应商的经营范围
    userService.createCustomerSupplierBusinessScope(null, supplierDTO);
    preciseRecommendService.getCustomerSupplierBusinessScope(null, supplierDTO);
    userService.updateCustomerSupplierBusinessScope(null, supplierDTO);
    //保存主营车型
    List<ShopVehicleBrandModelDTO> shopVehicleBrandModelDTOList =  productService.getShopVehicleBrandModelByShopId(supplierShopDTO.getId());
    if(VehicleSelectBrandModel.PART_MODEL.equals(supplierShopDTO.getShopSelectBrandModel()) && CollectionUtil.isNotEmpty(shopVehicleBrandModelDTOList)){
      StringBuilder vehicleModelIdStr = new StringBuilder();
      for(ShopVehicleBrandModelDTO shopVehicleBrandModelDTO : shopVehicleBrandModelDTOList){
        vehicleModelIdStr.append(shopVehicleBrandModelDTO.getModelId()).append(",");
      }
      supplierDTO.setVehicleModelIdStr(vehicleModelIdStr.substring(0,vehicleModelIdStr.length()-1));
      supplierService.saveSupplierVehicleBrandModelRelation(customerShopDTO.getId(),supplierDTO, StandardBrandModelCache.getShopVehicleBrandModelDTOMap());
    }
    return supplierDTO;
  }



  @Override
  public CustomerDTO collectCustomerShop(ShopDTO supplierShopDTO, ShopDTO customerShopDTO) throws Exception {
    if (customerShopDTO == null || supplierShopDTO == null || customerShopDTO.getId() == null || supplierShopDTO.getId() == null) {
      LOG.error("collectSupplierShop shopDTO is null ,customerShopDTO:{},supplierShopDTO:{}", customerShopDTO, supplierShopDTO);
      return null;
    }
    IApplyService applyService = ServiceManager.getService(IApplyService.class);
    IUserService userService = ServiceManager.getService(IUserService.class);
    IPreciseRecommendService preciseRecommendService = ServiceManager.getService(IPreciseRecommendService.class);
    IProductService productService = ServiceManager.getService(IProductService.class);

    applyService.collectCustomerShopUpdateRelation(customerShopDTO.getId(), supplierShopDTO.getId());
    CustomerDTO customerDTO = userService.createRelationCustomer(supplierShopDTO, customerShopDTO, RelationTypes.RECOMMEND_RELATED);

    //保存客户和供应商的经营范围
    userService.createCustomerSupplierBusinessScope(customerDTO, null);
    preciseRecommendService.getCustomerSupplierBusinessScope(customerDTO, null);
    userService.updateCustomerSupplierBusinessScope(customerDTO, null);

    //保存主营车型
    List<ShopVehicleBrandModelDTO> shopVehicleBrandModelDTOList =  productService.getShopVehicleBrandModelByShopId(customerShopDTO.getId());
    if(VehicleSelectBrandModel.PART_MODEL.equals(customerShopDTO.getShopSelectBrandModel()) && CollectionUtil.isNotEmpty(shopVehicleBrandModelDTOList)){
      StringBuilder vehicleModelIdStr = new StringBuilder();
      for(ShopVehicleBrandModelDTO shopVehicleBrandModelDTO : shopVehicleBrandModelDTOList){
        vehicleModelIdStr.append(shopVehicleBrandModelDTO.getModelId()).append(",");
      }
      customerDTO.setVehicleModelIdStr(vehicleModelIdStr.substring(0,vehicleModelIdStr.length()-1));
      userService.saveCustomerVehicleBrandModelRelation(supplierShopDTO.getId(), customerDTO, StandardBrandModelCache.getShopVehicleBrandModelDTOMap());
    }

    //保存服务范围
    IServiceCategoryService serviceCategoryService = ServiceManager.getService(IServiceCategoryService.class);
    List<ShopServiceCategoryDTO> shopServiceCategoryDTOList = serviceCategoryService.getShopServiceCategoryDTOByShopId(customerShopDTO.getId());
    if(CollectionUtil.isNotEmpty(shopServiceCategoryDTOList)){
      StringBuilder serviceCategoryRelationIdStr = new StringBuilder();
      for(ShopServiceCategoryDTO shopServiceCategoryDTO : shopServiceCategoryDTOList){
        serviceCategoryRelationIdStr.append(shopServiceCategoryDTO.getServiceCategoryId()).append(",");
      }
      customerDTO.setServiceCategoryRelationIdStr(serviceCategoryRelationIdStr.substring(0,serviceCategoryRelationIdStr.length()-1));
      userService.saveCustomerServiceCategoryRelation(supplierShopDTO.getId(),customerDTO);
    }

    return customerDTO;
  }
}
