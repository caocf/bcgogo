package com.bcgogo.schedule.index.stat;

import com.bcgogo.config.dto.ShopBusinessScopeDTO;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.dto.ShopServiceCategoryDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.service.IServiceCategoryService;
import com.bcgogo.enums.ExeStatus;
import com.bcgogo.enums.config.VehicleSelectBrandModel;
import com.bcgogo.product.StandardBrandModelCache.StandardBrandModelCache;
import com.bcgogo.product.service.IProductService;
import com.bcgogo.product.standardVehicleBrandModel.ShopVehicleBrandModelDTO;
import com.bcgogo.schedule.BcgogoQuartzJobBean;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.service.solr.ICustomerOrSupplierSolrWriteService;
import com.bcgogo.user.dto.CustomerDTO;
import com.bcgogo.user.dto.RelatedShopUpdateLogDTO;
import com.bcgogo.user.dto.RelatedShopUpdateTaskDTO;
import com.bcgogo.user.dto.SupplierDTO;
import com.bcgogo.user.model.*;
import com.bcgogo.user.model.task.RelatedShopUpdateLog;
import com.bcgogo.user.service.IRelatedShopUpdateService;
import com.bcgogo.user.service.ISupplierService;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.utils.CollectionUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-8-8
 * Time: 下午3:08
 */
@Component
public class RelatedShopUpdateTaskSchedule extends BcgogoQuartzJobBean {
  private static final Logger LOG = LoggerFactory.getLogger(RelatedShopUpdateTaskSchedule.class);

  private static boolean lock = false;

  private static synchronized boolean isLock() {
    if (lock) {
      return true;
    }
    lock = true;
    return false;
  }

  @Override
  protected void executeJob(JobExecutionContext jobExecutionContext) throws JobExecutionException {
    if (isLock()) {
      return;
    }
    IRelatedShopUpdateService relatedShopUpdateService = null;
    RelatedShopUpdateTaskDTO relatedShopUpdateTaskDTO = null;
    try {
      relatedShopUpdateService = ServiceManager.getService(IRelatedShopUpdateService.class);
      do {
        relatedShopUpdateTaskDTO = relatedShopUpdateService.getFirstRelatedShopUpdateTaskDTO(ExeStatus.READY);
        if(relatedShopUpdateTaskDTO == null){
          break;
        }
        relatedShopUpdateService.updateRelatedShopUpdateTaskStatus(relatedShopUpdateTaskDTO,ExeStatus.START);
        handleRelatedShopUpdateTask(relatedShopUpdateTaskDTO);
        relatedShopUpdateService.updateRelatedShopUpdateTaskStatus(relatedShopUpdateTaskDTO,ExeStatus.FINISHED);
      } while (relatedShopUpdateTaskDTO != null);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      if(relatedShopUpdateService != null){
        relatedShopUpdateService.updateRelatedShopUpdateTaskStatus(relatedShopUpdateTaskDTO,ExeStatus.EXCEPTION);
      }
    } finally {
      lock = false;
    }
  }

  private void handleRelatedShopUpdateTask(RelatedShopUpdateTaskDTO relatedShopUpdateTaskDTO)throws Exception{
    if (relatedShopUpdateTaskDTO == null || relatedShopUpdateTaskDTO.getId() == null || relatedShopUpdateTaskDTO.getShopId() == null) {
      LOG.error("需要同步的店铺和供应商信息的时候店铺信息不存在！");
      return;
    }
    Long shopId = relatedShopUpdateTaskDTO.getShopId();
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    IUserService userService = ServiceManager.getService(IUserService.class);
    ISupplierService supplierService = ServiceManager.getService(ISupplierService.class);
    ShopDTO shopDTO = configService.getShopById(shopId);
    if (shopDTO == null) {
      LOG.error("需要同步的店铺和供应商信息的时候店铺信息不存在！");
      return;
    }
    Set<Long> shopIds = new HashSet<Long>();
    shopIds.add(shopId);
    List<ShopBusinessScopeDTO> shopBusinessScopeDTOs = configService.getShopBusinessScopeByShopId(shopIds);
    Set<Long> shopProductCategoryIds = new HashSet<Long>();
    if(CollectionUtils.isNotEmpty(shopBusinessScopeDTOs)){
      for(ShopBusinessScopeDTO shopBusinessScopeDTO : shopBusinessScopeDTOs){
        if(shopBusinessScopeDTO != null && shopBusinessScopeDTO.getProductCategoryId()!=null){
          shopProductCategoryIds.add(shopBusinessScopeDTO.getProductCategoryId());
        }
      }
    }
    String shopVehicleModelIdStr = getShopVehicleModelIdStr(shopDTO);
    String shopServiceCategoryRelationIdStr = getShopServiceCategoryRelationIdStr(shopDTO.getId());

    //同步客户资料
    Set<Long> relatedSupplierIds = new HashSet<Long>();
    Set<Long> updatedCustomerIds = new HashSet<Long>();  //已经更新了的customerId
    Set<Long> updatedSupplierIds = new HashSet<Long>();
    UserWriter writer = ServiceManager.getService(UserDaoManager.class).getWriter();
    Object status = writer.begin();
    try {

      List<RelatedShopUpdateLogDTO> relatedShopUpdateLogDTOs = new ArrayList<RelatedShopUpdateLogDTO>();

      List<Customer> customers = writer.getCustomerByCustomerShopId(shopId);
       if (CollectionUtils.isNotEmpty(customers)) {
        for (Customer customer : customers) {
          customer.fromRelatedShop(shopDTO, relatedShopUpdateLogDTOs);
          writer.update(customer);
          CustomerRecord customerRecord = writer.getCustomerRecord(customer.getShopId(), customer.getId());
          customerRecord.fromRelatedShop(shopDTO, relatedShopUpdateLogDTOs);
          writer.update(customerRecord);
          updatedCustomerIds.add(customer.getId());
          if (customer.getSupplierId() != null) {
            relatedSupplierIds.add(customer.getSupplierId());
          }
          updatedCustomerBusinessScope(writer, customer, shopProductCategoryIds);
          CustomerDTO customerDTO = customer.toDTO();
          if(VehicleSelectBrandModel.PART_MODEL.equals(shopDTO.getShopSelectBrandModel()) && StringUtils.isNotBlank(shopVehicleModelIdStr)){
            customerDTO.setVehicleModelIdStr(shopVehicleModelIdStr);
            userService.saveCustomerVehicleBrandModelRelation(writer,customerDTO.getShopId(),customerDTO, StandardBrandModelCache.getShopVehicleBrandModelDTOMap());
          }
          //保存服务范围
          if(StringUtils.isNotBlank(shopServiceCategoryRelationIdStr)){
            customerDTO.setServiceCategoryRelationIdStr(shopServiceCategoryRelationIdStr);
            userService.saveCustomerServiceCategoryRelation(customerDTO.getShopId(),customerDTO);
          }
        }
        //同步客户关联供应商的资料
        if (CollectionUtils.isNotEmpty(relatedSupplierIds)) {
          List<Supplier> suppliers = writer.getSupplierByIds(relatedSupplierIds);
          for (Supplier supplier : suppliers) {
            supplier.fromRelatedShop(shopDTO, relatedShopUpdateLogDTOs);
            updatedSupplierIds.add(supplier.getId());
            writer.update(supplier);
            updatedSupplierBusinessScope(writer,supplier,shopProductCategoryIds);
            if(VehicleSelectBrandModel.PART_MODEL.equals(shopDTO.getShopSelectBrandModel()) && StringUtils.isNotBlank(shopVehicleModelIdStr)){
              SupplierDTO supplierDTO = supplier.toDTO();
              supplierDTO.setVehicleModelIdStr(shopVehicleModelIdStr);
              supplierService.saveSupplierVehicleBrandModelRelation(writer, supplierDTO.getShopId(), supplierDTO, StandardBrandModelCache.getShopVehicleBrandModelDTOMap());
            }
          }
        }
      }
      //同步供应商的资料
      List<Supplier> suppliers = writer.getSupplierBySupplierShopId(shopId);
      Set<Long> relatedCustomerIds = new HashSet<Long>();
      if (CollectionUtils.isNotEmpty(suppliers)) {
        for (Supplier supplier : suppliers) {
          if (!relatedSupplierIds.contains(supplier.getId())) {
            supplier.fromRelatedShop(shopDTO, relatedShopUpdateLogDTOs);
            writer.update(supplier);
            updatedSupplierBusinessScope(writer,supplier,shopProductCategoryIds);
            if(VehicleSelectBrandModel.PART_MODEL.equals(shopDTO.getShopSelectBrandModel()) && StringUtils.isNotBlank(shopVehicleModelIdStr)){
              SupplierDTO supplierDTO = supplier.toDTO();
              supplierDTO.setVehicleModelIdStr(shopVehicleModelIdStr);
              supplierService.saveSupplierVehicleBrandModelRelation(writer,supplierDTO.getShopId(),supplierDTO, StandardBrandModelCache.getShopVehicleBrandModelDTOMap());
            }
            updatedSupplierIds.add(supplier.getId());
            if (supplier.getCustomerId() != null && !updatedCustomerIds.contains(supplier.getCustomerId())) {
              relatedCustomerIds.add(supplier.getCustomerId());
            }
          }
        }
        //同步供应商关联客户的资料
        if (CollectionUtils.isNotEmpty(relatedCustomerIds)) {
          List<Customer> relatedCustomer = writer.getCustomerByIds(relatedCustomerIds.toArray(new Long[relatedCustomerIds.size()]));
          for (Customer customer : relatedCustomer) {
            customer.fromRelatedShop(shopDTO, relatedShopUpdateLogDTOs);
            writer.update(customer);
            CustomerRecord customerRecord = writer.getCustomerRecord(customer.getShopId(), customer.getId());
            customerRecord.fromRelatedShop(shopDTO, relatedShopUpdateLogDTOs);
            writer.update(customerRecord);
            updatedCustomerIds.add(customer.getId());
            updatedCustomerBusinessScope(writer, customer, shopProductCategoryIds);

            CustomerDTO customerDTO = customer.toDTO();
            if(VehicleSelectBrandModel.PART_MODEL.equals(shopDTO.getShopSelectBrandModel()) && StringUtils.isNotBlank(shopVehicleModelIdStr)){
              customerDTO.setVehicleModelIdStr(shopVehicleModelIdStr);
              userService.saveCustomerVehicleBrandModelRelation(writer,customerDTO.getShopId(),customerDTO, StandardBrandModelCache.getShopVehicleBrandModelDTOMap());
            }

            //保存服务范围
            if(StringUtils.isNotBlank(shopServiceCategoryRelationIdStr)){
              customerDTO.setServiceCategoryRelationIdStr(shopServiceCategoryRelationIdStr);
              userService.saveCustomerServiceCategoryRelation(customerDTO.getShopId(),customerDTO);
            }
          }
        }
      }
      if (CollectionUtils.isNotEmpty(relatedShopUpdateLogDTOs)) {
        for (RelatedShopUpdateLogDTO relatedShopUpdateLogDTO : relatedShopUpdateLogDTOs) {
          if (relatedShopUpdateLogDTO != null) {
            relatedShopUpdateLogDTO.setTaskId(relatedShopUpdateTaskDTO.getId());
            relatedShopUpdateLogDTO.setFinishTime(System.currentTimeMillis());
            RelatedShopUpdateLog relatedShopUpdateLog = new RelatedShopUpdateLog();
            relatedShopUpdateLog.fromDTO(relatedShopUpdateLogDTO);
            writer.save(relatedShopUpdateLog);
            relatedShopUpdateLogDTO.setId(relatedShopUpdateLog.getId());
          }
        }
      }

      writer.commit(status);
      ICustomerOrSupplierSolrWriteService supplierSolrWriteService = ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class);
      if (CollectionUtils.isNotEmpty(updatedCustomerIds)) {
        for (Long customerId : updatedCustomerIds) {
          supplierSolrWriteService.reindexCustomerByCustomerId(customerId);
        }
      }
      if (CollectionUtils.isNotEmpty(updatedSupplierIds)) {
        for (Long supplierId : updatedSupplierIds) {
          supplierSolrWriteService.reindexSupplierBySupplierId(supplierId);
        }
      }
    } finally {
      writer.rollback(status);
    }

  }

  private String getShopVehicleModelIdStr(ShopDTO shopDTO) {
    IProductService productService = ServiceManager.getService(IProductService.class);
    //保存主营车型
    List<ShopVehicleBrandModelDTO> shopVehicleBrandModelDTOList =  productService.getShopVehicleBrandModelByShopId(shopDTO.getId());
    if(VehicleSelectBrandModel.PART_MODEL.equals(shopDTO.getShopSelectBrandModel()) && CollectionUtil.isNotEmpty(shopVehicleBrandModelDTOList)){
      StringBuilder vehicleModelIdStr = new StringBuilder();
      for(ShopVehicleBrandModelDTO shopVehicleBrandModelDTO : shopVehicleBrandModelDTOList){
        vehicleModelIdStr.append(shopVehicleBrandModelDTO.getModelId()).append(",");
      }
      return vehicleModelIdStr.substring(0,vehicleModelIdStr.length()-1);
    }
    return null;
  }
  private String getShopServiceCategoryRelationIdStr(Long shopId) {
    //保存服务范围
    IServiceCategoryService serviceCategoryService = ServiceManager.getService(IServiceCategoryService.class);
    List<ShopServiceCategoryDTO> shopServiceCategoryDTOList = serviceCategoryService.getShopServiceCategoryDTOByShopId(shopId);
    if(CollectionUtil.isNotEmpty(shopServiceCategoryDTOList)){
      StringBuilder serviceCategoryRelationIdStr = new StringBuilder();
      for(ShopServiceCategoryDTO shopServiceCategoryDTO : shopServiceCategoryDTOList){
        serviceCategoryRelationIdStr.append(shopServiceCategoryDTO.getServiceCategoryId()).append(",");
      }
      return serviceCategoryRelationIdStr.substring(0,serviceCategoryRelationIdStr.length()-1);
    }
    return null;
  }

  //同步客户的经营范围
  private void updatedCustomerBusinessScope(UserWriter writer, Customer customer, Set<Long> shopProductCategoryIds) {
    if(customer == null){return;}
    if(shopProductCategoryIds == null){shopProductCategoryIds = new HashSet<Long>();}
    List<BusinessScope> businessScopes = writer.getCustomerSupplierBusinessScope(customer.getShopId(),customer.getId(),null);
    Map<Long,BusinessScope> businessScopeMap = new HashMap<Long, BusinessScope>();
    if(CollectionUtils.isNotEmpty(businessScopes)){
      for(BusinessScope businessScope:businessScopes){
        businessScopeMap.put(businessScope.getProductCategoryId(),businessScope);
      }
    }
    for(BusinessScope businessScope : businessScopes){
      if(!shopProductCategoryIds.contains(businessScope.getProductCategoryId())){
        writer.delete(businessScope);
      }
    }
    for (Long shopProductCategoryId : shopProductCategoryIds) {
       if(businessScopeMap.get(shopProductCategoryId) == null){
         BusinessScope businessScope = new BusinessScope();
         businessScope.setShopId(customer.getShopId());
         businessScope.setCustomerId(customer.getId());
         businessScope.setProductCategoryId(shopProductCategoryId);
         writer.save(businessScope);
         businessScopeMap.put(shopProductCategoryId,businessScope);
       }
    }
  }

  //同步供应商的经营范围
  private void updatedSupplierBusinessScope(UserWriter writer, Supplier supplier, Set<Long> shopProductCategoryIds) {
    if(supplier == null){return;}
    if(shopProductCategoryIds == null){shopProductCategoryIds = new HashSet<Long>();}
    List<BusinessScope> businessScopes = writer.getCustomerSupplierBusinessScope(supplier.getShopId(), null, supplier.getId());
    Map<Long,BusinessScope> businessScopeMap = new HashMap<Long, BusinessScope>();
    if(CollectionUtils.isNotEmpty(businessScopes)){
      for(BusinessScope businessScope:businessScopes){
        businessScopeMap.put(businessScope.getProductCategoryId(),businessScope);
      }
    }
    for(BusinessScope businessScope : businessScopes){
      if(!shopProductCategoryIds.contains(businessScope.getProductCategoryId())){
        writer.delete(businessScope);
      }
    }
    for (Long shopProductCategoryId : shopProductCategoryIds) {
       if(businessScopeMap.get(shopProductCategoryId) == null){
         BusinessScope businessScope = new BusinessScope();
         businessScope.setShopId(supplier.getShopId());
         businessScope.setSupplierId(supplier.getId());
         businessScope.setProductCategoryId(shopProductCategoryId);
         writer.save(businessScope);
         businessScopeMap.put(shopProductCategoryId,businessScope);
       }
    }
  }

}
