package com.bcgogo.txn.service;

import com.bcgogo.enums.ConsumeType;
import com.bcgogo.enums.RepairOrderTemplateStatus;
import com.bcgogo.enums.ServiceStatus;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.product.dto.ProductLocalInfoDTO;
import com.bcgogo.product.dto.ProductVehicleDTO;
import com.bcgogo.product.service.ProductService;
import com.bcgogo.search.model.InventorySearchIndex;
import com.bcgogo.search.service.SearchService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.*;
import com.bcgogo.txn.model.*;
import com.bcgogo.txn.service.solr.IOrderSolrWriterService;
import com.bcgogo.user.service.utils.BcgogoShopLogicResourceUtils;
import com.bcgogo.utils.SearchConstant;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Li jinlong
 * Date: 12-10-12
 * Time: 下午5:32
 * To change this template use File | Settings | File Templates.
 */

@Component
public class RepairOrderTemplateService implements IRepairOrderTemplateService {
  private static final Logger LOG = LoggerFactory.getLogger(RepairOrderTemplateService.class);
  @Autowired
  private TxnDaoManager txnDaoManager;

  @Override
  public RepairOrderTemplateDTO saveOrUpdateRepairOrderTemplate(RepairOrderTemplateDTO repairOrderTemplateDTO) throws Exception {
    TxnWriter txnWriter = txnDaoManager.getWriter();
    if (repairOrderTemplateDTO != null) {
      String repairOrderTemplateName = repairOrderTemplateDTO.getTemplateName();
      Long shopId = repairOrderTemplateDTO.getShopId();
      RepairOrderTemplate repairOrderTemplate = txnWriter.getRepairOrderTemplateByTemplateNameAndStatus(shopId, repairOrderTemplateName, RepairOrderTemplateStatus.ENABLED);

      Object status = txnWriter.begin();

      try {
        if (repairOrderTemplate == null) {
          repairOrderTemplate = new RepairOrderTemplate();
          repairOrderTemplate.fromDTO(repairOrderTemplateDTO);
          repairOrderTemplate.setUsageCounter(0);
          txnWriter.save(repairOrderTemplate);

          repairOrderTemplateDTO.setId(repairOrderTemplate.getId());
          this.saveOrUpdateRepairOrderTemplateService(txnWriter, repairOrderTemplateDTO, true);
          this.saveOrUpdateRepairOrderTemplateItem(txnWriter, repairOrderTemplateDTO, true);
          this.saveOrUpdateRepairOrderTemplateOtherIncomeItem(txnWriter,repairOrderTemplateDTO,true);
        } else {
          repairOrderTemplateDTO.setId(repairOrderTemplate.getId());
          repairOrderTemplate.setTemplateName(repairOrderTemplateDTO.getTemplateName());
          this.saveOrUpdateRepairOrderTemplateService(txnWriter, repairOrderTemplateDTO, false);
          this.saveOrUpdateRepairOrderTemplateItem(txnWriter, repairOrderTemplateDTO, false);
          this.saveOrUpdateRepairOrderTemplateOtherIncomeItem(txnWriter,repairOrderTemplateDTO,false);
        }

        txnWriter.commit(status);

      } finally {
        txnWriter.rollback(status);
      }

    }

    return repairOrderTemplateDTO;
  }

  /**
   * 保存或者更新施工内容模板
   *
   * @param txnWriter
   * @param repairOrderTemplateDTO
   * @param isSave
   */
  public void saveOrUpdateRepairOrderTemplateService(TxnWriter txnWriter, RepairOrderTemplateDTO repairOrderTemplateDTO, boolean isSave) {

    List<RepairOrderTemplateServiceDTO> templateServiceDTOList = repairOrderTemplateDTO.getRepairOrderTemplateServiceDTOs();
    if (templateServiceDTOList == null)
      return;
    RFITxnService rfiTxnService = ServiceManager.getService(RFITxnService.class);
    //保存施工单施工内容模板
    Set<Long> set = new HashSet<Long>();
    if (isSave) {
      for (RepairOrderTemplateServiceDTO templateServiceDTO : templateServiceDTOList) {

        RepairOrderServiceDTO serviceDTO = templateServiceDTO.getRepairOrderServiceDTO();

        if (StringUtils.isNotBlank(serviceDTO.getService())) {
          if (StringUtils.isNotBlank(serviceDTO.getBusinessCategoryName())) {
            serviceDTO.setBusinessCategoryName(serviceDTO.getBusinessCategoryName().trim());
            serviceDTO.setBusinessCategoryId(rfiTxnService.saveCategory(txnWriter, repairOrderTemplateDTO.getShopId(), serviceDTO.getBusinessCategoryName()).getId());
          }
        }
        // 判断施工内容是否是新增，如果新增则加入service表中 新加的施工服务类型只能是现金
        Service service = rfiTxnService.getRFServiceByServiceNameAndShopId(repairOrderTemplateDTO.getShopId(), serviceDTO.getService());
        if (null == service) {
          service = new Service();
          service.setShopId(repairOrderTemplateDTO.getShopId());
          service.setName(serviceDTO.getService());
          service.setPrice(serviceDTO.getTotal());
          service.setStatus(ServiceStatus.ENABLED);
          service.setMemo("");
          txnWriter.save(service);
          serviceDTO.setServiceId(service.getId());
          rfiTxnService.saveOrUpdateCategoryItemRelation(txnWriter, service.getShopId(), serviceDTO.getBusinessCategoryId(), serviceDTO.getServiceId());
          set.add(service.getId());
        } else {
          serviceDTO.setServiceId(service.getId());
          if (ServiceStatus.DISABLED.equals(service.getStatus())) {
            service.setStatus(ServiceStatus.ENABLED);
            txnWriter.update(service);
            set.add(service.getId());
          }
          rfiTxnService.saveOrUpdateCategoryItemRelation(txnWriter, service.getShopId(), serviceDTO.getBusinessCategoryId(), serviceDTO.getServiceId());
        }

        templateServiceDTO.setPrice(serviceDTO.getTotal());
        templateServiceDTO.setRepairOrderTemplateId(repairOrderTemplateDTO.getId());
        templateServiceDTO.setServiceId(serviceDTO.getServiceId());
        templateServiceDTO.setServiceName(serviceDTO.getService());
        templateServiceDTO.setBusinessCategoryId(serviceDTO.getBusinessCategoryId());
        templateServiceDTO.setBusinessCategoryName(serviceDTO.getBusinessCategoryName());
        //保存施工内容模板
        com.bcgogo.txn.model.RepairOrderTemplateService templateService = new com.bcgogo.txn.model.RepairOrderTemplateService();
        templateService.fromDTO(templateServiceDTO);
        txnWriter.save(templateService);

        templateServiceDTO.setId(templateService.getId());
        serviceDTO.setTemplateServiceId(templateService.getId());

      }
    } else{  //更新施工单施工内容模板

      //施工内容为删除情况
      List<com.bcgogo.txn.model.RepairOrderTemplateService> templateServiceList = txnWriter.getRepairOrderTemplateServicesByRepairOrderTemplateId(repairOrderTemplateDTO.getId());
      for (com.bcgogo.txn.model.RepairOrderTemplateService templateService : templateServiceList) {
        Long templateServiceId = templateService.getId();
        boolean isExist = false;
        for (RepairOrderTemplateServiceDTO templateServiceDTO : templateServiceDTOList) {
          RepairOrderServiceDTO serviceDTO = templateServiceDTO.getRepairOrderServiceDTO();
          if (serviceDTO.getTemplateServiceId() != null) {
            if (serviceDTO.getTemplateServiceId().equals(templateServiceId)) {
              isExist = true;
            }
          }
        }
        if (isExist == false) {
          txnWriter.delete(com.bcgogo.txn.model.RepairOrderTemplateService.class, templateServiceId);
        }

      }

      for (RepairOrderTemplateServiceDTO templateServiceDTO : templateServiceDTOList) {
        RepairOrderServiceDTO serviceDTO = templateServiceDTO.getRepairOrderServiceDTO();
        if (StringUtils.isNotBlank(serviceDTO.getService())) {
          if (StringUtils.isNotBlank(serviceDTO.getBusinessCategoryName())) {
            serviceDTO.setBusinessCategoryName(serviceDTO.getBusinessCategoryName().trim());
            serviceDTO.setBusinessCategoryId(rfiTxnService.saveCategory(txnWriter, repairOrderTemplateDTO.getShopId(), serviceDTO.getBusinessCategoryName()).getId());
          }
        }
        // 判断施工内容是否是新增，如果新增则加入service表中 新加的施工服务类型只能是现金
        Service service = rfiTxnService.getRFServiceByServiceNameAndShopId(repairOrderTemplateDTO.getShopId(), serviceDTO.getService());
        if (null == service) {
          service = new Service();
          service.setShopId(repairOrderTemplateDTO.getShopId());
          service.setName(serviceDTO.getService());
          service.setPrice(serviceDTO.getTotal());
          service.setStatus(ServiceStatus.ENABLED);
          service.setMemo("");
          txnWriter.save(service);
          serviceDTO.setServiceId(service.getId());
          rfiTxnService.saveOrUpdateCategoryItemRelation(txnWriter, service.getShopId(), serviceDTO.getBusinessCategoryId(), serviceDTO.getServiceId());
          set.add(service.getId());
        } else {
          serviceDTO.setServiceId(service.getId());
          if (ServiceStatus.DISABLED.equals(service.getStatus())) {
            service.setStatus(ServiceStatus.ENABLED);
            txnWriter.update(service);
            set.add(service.getId());
          }
          rfiTxnService.saveOrUpdateCategoryItemRelation(txnWriter, service.getShopId(), serviceDTO.getBusinessCategoryId(), serviceDTO.getServiceId());

        }

        //施工内容为新增情况
        if (StringUtils.isBlank(serviceDTO.getTemplateServiceIdStr())) {
          templateServiceDTO.setPrice(serviceDTO.getPrice());
          templateServiceDTO.setRepairOrderTemplateId(repairOrderTemplateDTO.getId());
          templateServiceDTO.setServiceId(serviceDTO.getServiceId());
          templateServiceDTO.setServiceName(serviceDTO.getService());
          templateServiceDTO.setBusinessCategoryId(serviceDTO.getBusinessCategoryId());
          templateServiceDTO.setBusinessCategoryName(serviceDTO.getBusinessCategoryName());
          //保存施工内容模板
          com.bcgogo.txn.model.RepairOrderTemplateService templateService = new com.bcgogo.txn.model.RepairOrderTemplateService();
          templateService.fromDTO(templateServiceDTO);
          txnWriter.save(templateService);

          templateServiceDTO.setId(templateService.getId());
          serviceDTO.setTemplateServiceId(templateService.getId());

        } else{ //施工内容为修改情况
          com.bcgogo.txn.model.RepairOrderTemplateService templateService = txnWriter.getById(com.bcgogo.txn.model.RepairOrderTemplateService.class, Long.valueOf(serviceDTO.getTemplateServiceIdStr()));
          templateService.setServiceId(serviceDTO.getServiceId());
          templateService.setServiceName(serviceDTO.getService());
          templateService.setPrice(serviceDTO.getTotal());
          templateService.setBusinessCategoryId(serviceDTO.getBusinessCategoryId());
          templateService.setBusinessCategoryName(serviceDTO.getBusinessCategoryName());
          txnWriter.update(templateService);

          templateServiceDTO.setPrice(serviceDTO.getPrice());
          templateServiceDTO.setRepairOrderTemplateId(repairOrderTemplateDTO.getId());
          templateServiceDTO.setServiceId(serviceDTO.getServiceId());
          templateServiceDTO.setServiceName(serviceDTO.getService());
          templateServiceDTO.setId(templateService.getId());
          templateServiceDTO.setBusinessCategoryName(templateService.getBusinessCategoryName());
          templateServiceDTO.setBusinessCategoryId(templateService.getBusinessCategoryId());
        }

      }
    }
    try {
      if (CollectionUtils.isNotEmpty(set)) {
        ServiceManager.getService(IOrderSolrWriterService.class).createRepairServiceSolrIndex(repairOrderTemplateDTO.getShopId(), set);
      }
    } catch (Exception e) {
      LOG.error("shopId:{}", repairOrderTemplateDTO.getShopId());
      LOG.error("serviceId:{}", StringUtil.arrayToStr(",", set.toArray(new Long[set.size()])));
      LOG.error("createRepairServiceSolrIndex 失败！", e);
    }
  }


  /**
   * 保存或者更新施工材料内容模板
   *
   * @param txnWriter
   * @param repairOrderTemplateDTO
   * @param isSave
   */
  public void saveOrUpdateRepairOrderTemplateItem(TxnWriter txnWriter, RepairOrderTemplateDTO repairOrderTemplateDTO, boolean isSave) throws Exception {
    RFITxnService rfiTxnService = ServiceManager.getService(RFITxnService.class);
    TxnService txnService = ServiceManager.getService(TxnService.class);
    IRepairService repairService = ServiceManager.getService(IRepairService.class);
    ProductService productService = ServiceManager.getService(ProductService.class);
    RepairOrderDTO repairOrderDTO = repairOrderTemplateDTO.getRepairOrderDTO();
    List<RepairOrderTemplateItemDTO> itemDTOList = repairOrderTemplateDTO.getRepairOrderTemplateItemDTOs();
    List inventorySearchIndexList = new ArrayList() ;
    if (itemDTOList == null)
      return;
    for (RepairOrderTemplateItemDTO templateItemDTO : itemDTOList) {
      RepairOrderItemDTO itemDTO = templateItemDTO.getRepairOrderItemDTO();
      if(StringUtils.isNotBlank(itemDTO.getProductName()))
      {
        if(StringUtils.isNotBlank(itemDTO.getBusinessCategoryName()))
        {
          itemDTO.setBusinessCategoryName(itemDTO.getBusinessCategoryName().trim());
          itemDTO.setBusinessCategoryId(rfiTxnService.saveCategory(txnWriter,repairOrderDTO.getShopId(),itemDTO.getBusinessCategoryName()).getId());
        }
      }

      Long productId = null;
      Long productLocalInfoId = null;
      ProductLocalInfoDTO productLocalInfoDTO = new ProductLocalInfoDTO();
      if (itemDTO.getProductId() == null) {
        ProductDTO productDTO = new ProductDTO();
        boolean isNewProduct = repairService.addNewProduct(repairOrderDTO, itemDTO, productDTO);
        productLocalInfoId = productDTO.getProductLocalInfoId();
        productId = productDTO.getId();
        Inventory inventory = null;
        if(productLocalInfoId!=null){
          inventory = txnWriter.getById(Inventory.class,productLocalInfoId);
        }
        if (null != productLocalInfoId && inventory == null) {
          inventory = txnService.saveInventoryAfterSaveNewProduct(productDTO.getShopId(), productLocalInfoId, 0, txnWriter, productDTO.getSellUnit());
        }

        if (null != productLocalInfoId && !isNewProduct) {
          productService.updateCommodityCodeByProductLocalInfoId(repairOrderDTO.getShopId(), productLocalInfoId, itemDTO.getCommodityCode());
        }
        itemDTO.setProductId(productLocalInfoId);
        productLocalInfoDTO = productService.getProductLocalInfoById(itemDTO.getProductId(), repairOrderTemplateDTO.getShopId());

        InventorySearchIndex inventorySearchIndex = txnService.createInventorySearchIndex(inventory, productId);
        inventorySearchIndexList.add(inventorySearchIndex);
      } else {
        productLocalInfoId = itemDTO.getProductId();
        repairService.addVehicleToProduct(repairOrderDTO, itemDTO);        //多款车型的商品做了一次维修单之后新增一款关联车型
        productLocalInfoDTO = productService.getProductLocalInfoById(itemDTO.getProductId(), repairOrderDTO.getShopId());
        if (productLocalInfoDTO != null) {
          productId = productLocalInfoDTO.getProductId();
          productService.updateProductLocalInfoCategory(repairOrderDTO.getShopId(),productId,itemDTO.getBusinessCategoryId());
          productService.updateCommodityCodeByProductLocalInfoId(repairOrderDTO.getShopId(), productLocalInfoId, itemDTO.getCommodityCode());
        } else {
          LOG.error("shopId:{} 商品不存在,productId:{}", new Object[]{repairOrderTemplateDTO.getShopId(), productLocalInfoId});
        }
        if (itemDTO.getProductType() != null && itemDTO.getProductType() == SearchConstant.PRODUCT_PRODUCTSTATUS_MULTIPLE) {
          ProductVehicleDTO productVehicleDTO = new ProductVehicleDTO();
          productVehicleDTO.setShopId(repairOrderDTO.getShopId());
          productVehicleDTO.setBrandId(repairOrderDTO.getBrandId());
          productVehicleDTO.setModelId(repairOrderDTO.getModelId());
          productVehicleDTO.setYearId(repairOrderDTO.getYearId());
          productVehicleDTO.setEngineId(repairOrderDTO.getEngineId());
          productVehicleDTO.setProductId(productId);
          productService.createProductVehicle(productVehicleDTO);
        }
      }

      // 保存材料单内容
      if (isSave) {

        RepairOrderTemplateItem templateItem = new RepairOrderTemplateItem();
        templateItem.setProductId(productLocalInfoId);
        templateItem.setAmount(itemDTO.getAmount());
        templateItem.setPrice(itemDTO.getPrice());
        templateItem.setUnit(itemDTO.getUnit());
        templateItem.setRepairOrderTemplateId(repairOrderTemplateDTO.getId());
        templateItem.setBusinessCategoryId(itemDTO.getBusinessCategoryId());
        templateItem.setBusinessCategoryName(itemDTO.getBusinessCategoryName());
        txnWriter.save(templateItem);

        templateItemDTO.setId(templateItem.getId());
        itemDTO.setTemplateItemId(templateItem.getId());

      } else{ //更新材料单内容

        //施工内容为新增情况
        if (StringUtils.isBlank(itemDTO.getTemplateItemIdStr())) {
          RepairOrderTemplateItem templateItem = new RepairOrderTemplateItem();
          templateItem.setProductId(productLocalInfoId);
          templateItem.setAmount(itemDTO.getAmount());
          templateItem.setPrice(itemDTO.getPrice());
          templateItem.setUnit(itemDTO.getUnit());
          templateItem.setRepairOrderTemplateId(repairOrderTemplateDTO.getId());
          templateItem.setBusinessCategoryId(itemDTO.getBusinessCategoryId());
          templateItem.setBusinessCategoryName(itemDTO.getBusinessCategoryName());
          txnWriter.save(templateItem);

          templateItemDTO.setId(templateItem.getId());
          itemDTO.setTemplateItemId(templateItem.getId());

        } else { //施工内容为修改情况
          com.bcgogo.txn.model.RepairOrderTemplateItem templateItem = txnWriter.getById(com.bcgogo.txn.model.RepairOrderTemplateItem.class, itemDTO.getTemplateItemId());
          templateItem.setProductId(productLocalInfoId);
          templateItem.setAmount(itemDTO.getAmount());
          templateItem.setPrice(itemDTO.getPrice());
          templateItem.setUnit(itemDTO.getUnit());
          templateItem.setBusinessCategoryId(itemDTO.getBusinessCategoryId());
          templateItem.setBusinessCategoryName(itemDTO.getBusinessCategoryName());
          txnWriter.update(templateItem);

         templateItemDTO.setId(templateItem.getId());
        }
      }

    }
     ServiceManager.getService(IInventoryService.class)
            .addOrUpdateInventorySearchIndexWithList(repairOrderTemplateDTO.getShopId(), inventorySearchIndexList);
    //删除
    if (isSave == false) {
      List<RepairOrderTemplateItem> templateItemList = txnWriter.getRepairOrderTemplateItemsByRepairOrderTemplateId(repairOrderTemplateDTO.getId());
      for (RepairOrderTemplateItem templateItem : templateItemList) {
        Long templateItemId = templateItem.getId();
        boolean isExist = false;
        for (RepairOrderTemplateItemDTO templateItemDTO : itemDTOList) {
          RepairOrderItemDTO itemDTO = templateItemDTO.getRepairOrderItemDTO();
          if (itemDTO.getTemplateItemId() != null) {
            if (templateItemId.equals(itemDTO.getTemplateItemId())) {
              isExist = true;
            }
          }

        }
        if (isExist == false) {
          txnWriter.delete(RepairOrderTemplateItem.class, templateItemId);
        }
      }

    }


  }

  public RepairOrderTemplateDTO deleteRepairOrderTemplateById(Long repairOrderTemplateId) {
    TxnWriter txnWriter = txnDaoManager.getWriter();
    Object status = txnWriter.begin();
    RepairOrderTemplateDTO repairOrderTemplateDTO = null;
    try {
      RepairOrderTemplate repairOrderTemplate = txnWriter.getById(RepairOrderTemplate.class, repairOrderTemplateId);
      if (repairOrderTemplate != null) {
        repairOrderTemplate.setStatus(RepairOrderTemplateStatus.DISABLED);
        txnWriter.update(repairOrderTemplate);
        repairOrderTemplateDTO = repairOrderTemplate.toDTO();
      }

      txnWriter.commit(status);
    } finally {
      txnWriter.rollback(status);
    }
    return repairOrderTemplateDTO;
  }

  public RepairOrderTemplateDTO renameRepairOrderTemplateById(Long shopId, Long repairOrderTemplateId, String newRepairOrderTemplateName) {
    TxnWriter txnWriter = txnDaoManager.getWriter();
    Object status = txnWriter.begin();
    RepairOrderTemplateDTO repairOrderTemplateDTO = null;
    try {

      //如果存在同名的模板，先将老的模板设为无用状态
      RepairOrderTemplate oldRepairOrderTemplate = txnWriter.getRepairOrderTemplateByTemplateNameAndStatus(shopId, newRepairOrderTemplateName, RepairOrderTemplateStatus.ENABLED);
      if (oldRepairOrderTemplate != null) {
        oldRepairOrderTemplate.setStatus(RepairOrderTemplateStatus.DISABLED);
        txnWriter.update(oldRepairOrderTemplate);
      }


      RepairOrderTemplate repairOrderTemplate = txnWriter.getById(RepairOrderTemplate.class, repairOrderTemplateId);
      if (repairOrderTemplate != null) {
        repairOrderTemplate.setTemplateName(newRepairOrderTemplateName);
        repairOrderTemplate.setStatus(RepairOrderTemplateStatus.ENABLED);
        txnWriter.update(repairOrderTemplate);
        repairOrderTemplateDTO = repairOrderTemplate.toDTO();
      }

      txnWriter.commit(status);

    } finally {
      txnWriter.rollback(status);
    }
    return repairOrderTemplateDTO;
  }

  public List<RepairOrderTemplateDTO> getAllRepairOrderTemplate(Long shopId) {
    TxnWriter txnWriter = txnDaoManager.getWriter();
    List<RepairOrderTemplate> repairOrderTemplateList = txnWriter.getRepairOrderTemplateByShopId(shopId, RepairOrderTemplateStatus.ENABLED);
    List<RepairOrderTemplateDTO> repairOrderTemplateDTOList = new ArrayList<RepairOrderTemplateDTO>();
    if (CollectionUtils.isNotEmpty(repairOrderTemplateList)) {
      for (RepairOrderTemplate repairOrderTemplate : repairOrderTemplateList) {
        if (repairOrderTemplate != null) {
          RepairOrderTemplateDTO repairOrderTemplateDTO = repairOrderTemplate.toDTO();
          repairOrderTemplateDTOList.add(repairOrderTemplateDTO);
        }
      }
    }
    return repairOrderTemplateDTOList;
  }

  @Override
  public RepairOrderTemplateDTO getRepairOrderTemplateByTemplateName(Long shopId,Long shopVersionId,Long storehouseId, String repairOrderTemplateName) throws Exception {
    RepairOrderTemplateDTO repairOrderTemplateDTO = null;
    TxnWriter txnWriter = txnDaoManager.getWriter();
    RFITxnService rfiTxnService = ServiceManager.getService(RFITxnService.class);
    RepairOrderTemplate repairOrderTemplate = txnWriter.getRepairOrderTemplateByTemplateNameAndStatus(shopId, repairOrderTemplateName, RepairOrderTemplateStatus.ENABLED);
    if (repairOrderTemplate != null) {
      repairOrderTemplateDTO = repairOrderTemplate.toDTO();

      List<com.bcgogo.txn.model.RepairOrderTemplateService> templateServiceList = txnWriter.getRepairOrderTemplateServicesByRepairOrderTemplateId(repairOrderTemplate.getId());
      if (CollectionUtils.isNotEmpty(templateServiceList)) {
        List<RepairOrderTemplateServiceDTO> templateServiceDTOList = new ArrayList<RepairOrderTemplateServiceDTO>();
        for (com.bcgogo.txn.model.RepairOrderTemplateService templateService : templateServiceList) {
          RepairOrderTemplateServiceDTO templateServiceDTO = templateService.toDTO();
          RepairOrderServiceDTO serviceDTO = new RepairOrderServiceDTO();

          serviceDTO.setTemplateServiceId(templateServiceDTO.getId());
          serviceDTO.setTemplateServiceIdStr(templateServiceDTO.getIdStr());
          serviceDTO.setConsumeType(ConsumeType.MONEY);
          serviceDTO.setCostPrice(templateServiceDTO.getPrice() == null ? 0d : templateServiceDTO.getPrice());
          serviceDTO.setPrice(templateServiceDTO.getPrice() == null ? 0d : templateServiceDTO.getPrice());
          serviceDTO.setTotal(templateServiceDTO.getPrice() == null ? 0d : templateServiceDTO.getPrice());
          serviceDTO.setService(templateServiceDTO.getServiceName() == null ? "" : templateServiceDTO.getServiceName());
          serviceDTO.setServiceId(templateServiceDTO.getServiceId());
          serviceDTO.setServiceIdStr(templateServiceDTO.getServiceId().toString());
          serviceDTO.setShopId(shopId);
          serviceDTO.setIdStr("");
          serviceDTO.setWorkers("");
          serviceDTO.setWorkerIds("");
          serviceDTO.setMemo("");
          serviceDTO.setBusinessCategoryId(templateServiceDTO.getBusinessCategoryId());
          serviceDTO.setBusinessCategoryName(templateServiceDTO.getBusinessCategoryName());

          if(null != serviceDTO.getServiceId()) {
            CategoryDTO categoryDTO = rfiTxnService.getCateGoryByServiceId(shopId, serviceDTO.getServiceId());
            if (null == categoryDTO) {
              serviceDTO.setBusinessCategoryId(null);
              serviceDTO.setBusinessCategoryName(null);
            } else {
              serviceDTO.setBusinessCategoryId(categoryDTO.getId());
              serviceDTO.setBusinessCategoryName(categoryDTO.getCategoryName());
            }

            Service service = txnWriter.getById(Service.class, serviceDTO.getServiceId());
            serviceDTO.setStandardHours(service.getStandardHours());
            serviceDTO.setStandardUnitPrice(service.getStandardUnitPrice());
            serviceDTO.setActualHours(service.getStandardHours());
            serviceDTO.setTotal(service.getPrice());
          }
          templateServiceDTO.setRepairOrderServiceDTO(serviceDTO);
          templateServiceDTOList.add(templateServiceDTO);
        }

        repairOrderTemplateDTO.setRepairOrderTemplateServiceDTOs(templateServiceDTOList);
      }

      ProductService productService = ServiceManager.getService(ProductService.class);
      List<RepairOrderTemplateItem> templateItemList = txnWriter.getRepairOrderTemplateItemsByRepairOrderTemplateId(repairOrderTemplate.getId());
      Map<Long,StoreHouseInventoryDTO> storeHouseInventoryDTOMap = new HashMap<Long, StoreHouseInventoryDTO>();
      Map<Long,InventoryDTO> inventoryDTOMap = new HashMap<Long, InventoryDTO>();
      Map<Long,ProductDTO> productDTOMap = new HashMap<Long, ProductDTO>();
      if (CollectionUtils.isNotEmpty(templateItemList)) {
        Set<Long> productIds = new HashSet<Long>();
        for (RepairOrderTemplateItem repairOrderTemplateItem : templateItemList) {
          if (repairOrderTemplateItem.getProductId() != null) {
            productIds.add(repairOrderTemplateItem.getProductId());
          }
        }
        if (BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(shopVersionId) && storehouseId != null) {
          storeHouseInventoryDTOMap = ServiceManager.getService(IStoreHouseService.class).getStoreHouseInventoryDTOMapByStorehouseAndProductIds(shopId, storehouseId, productIds.toArray(new Long[productIds.size()]));
        }
        inventoryDTOMap = ServiceManager.getService(IInventoryService.class).getInventoryDTOMap(shopId, productIds);
        productDTOMap = productService.getProductDTOMapByProductLocalInfoIds(shopId,productIds);
      }
      if (CollectionUtils.isNotEmpty(templateItemList)) {
        List<RepairOrderTemplateItemDTO> templateItemDTOList = new ArrayList<RepairOrderTemplateItemDTO>();
        for (RepairOrderTemplateItem templateItem : templateItemList) {
          RepairOrderTemplateItemDTO templateItemDTO = templateItem.toDTO();
          RepairOrderItemDTO itemDTO = new RepairOrderItemDTO();
          Long productId = templateItemDTO.getProductId();
          itemDTO.setTemplateItemId(templateItemDTO.getId());
          itemDTO.setTemplateItemIdStr(templateItemDTO.getIdStr());
          itemDTO.setId(null);
          itemDTO.setIdStr("");
          itemDTO.setBusinessCategoryId(templateItemDTO.getBusinessCategoryId());
          itemDTO.setBusinessCategoryName(templateItemDTO.getBusinessCategoryName());

          if (productId != null) {
            InventoryDTO inventoryDTO = inventoryDTOMap.get(productId);
            StoreHouseInventoryDTO storeHouseInventoryDTO = storeHouseInventoryDTOMap.get(productId);
            ProductDTO productDTO = productDTOMap.get(productId);
            if (productDTO != null) {
              itemDTO.setProductType(productDTO.getProductVehicleStatus());
              itemDTO.setProductId(productDTO.getProductLocalInfoId());
              itemDTO.setProductName(productDTO.getName());
              itemDTO.setBrand(productDTO.getBrand());
              itemDTO.setSpec(productDTO.getSpec());
              itemDTO.setModel(productDTO.getModel());
              itemDTO.setCommodityCode(productDTO.getCommodityCode() == null ? "" : productDTO.getCommodityCode());
              itemDTO.setAmount(templateItemDTO.getAmount() == null ? 0d : templateItemDTO.getAmount());
              itemDTO.setStorageUnit(productDTO.getStorageUnit());
              itemDTO.setSellUnit(productDTO.getSellUnit());
              itemDTO.setRate(productDTO.getRate() == null ? 0L : productDTO.getRate());
              itemDTO.setTradePrice(productDTO.getTradePrice());
              itemDTO.setStorageBin(productDTO.getStorageBin() == null ? "" : productDTO.getStorageBin());
              if(inventoryDTO != null){
                itemDTO.setInventoryAmount(inventoryDTO.getAmount() == null ? 0d : inventoryDTO.getAmount());
              }
              if(storeHouseInventoryDTO != null){
                itemDTO.setInventoryAmount(storeHouseInventoryDTO.getAmount() == null ? 0d : storeHouseInventoryDTO.getAmount());
                itemDTO.setStorageBin(storeHouseInventoryDTO.getStorageBin() == null ? "":storeHouseInventoryDTO.getStorageBin());
              }

              itemDTO.setPrice(templateItemDTO.getPrice() == null ? 0d : templateItemDTO.getPrice());
              itemDTO.setUnit(templateItemDTO.getUnit());
              itemDTO.setTotal(itemDTO.getAmount() * itemDTO.getPrice());
              itemDTO.setReserved(0d);
            }

            if (null == productDTO || null == productDTO.getBusinessCategoryId()) {
              itemDTO.setBusinessCategoryId(null);
              itemDTO.setBusinessCategoryName(null);
            } else {
              Category category = rfiTxnService.getEnabledCategoryById(shopId, productDTO.getBusinessCategoryId());
              if (null == category) {
                itemDTO.setBusinessCategoryId(null);
                itemDTO.setBusinessCategoryName(null);
              } else {
                itemDTO.setBusinessCategoryId(category.getId());
                itemDTO.setBusinessCategoryName(category.getCategoryName());
              }
            }
          }

          templateItemDTO.setRepairOrderItemDTO(itemDTO);
          templateItemDTOList.add(templateItemDTO);
        }

        repairOrderTemplateDTO.setRepairOrderTemplateItemDTOs(templateItemDTOList);
      }


      List<RepairOrderTemplateOtherIncomeItem> incomeItemList = txnWriter.getRepairOrderTemplateOtherIncomeItem(shopId,repairOrderTemplateDTO.getId());
      List<RepairOrderOtherIncomeItemDTO> incomeItemDTOList = null;

      if (CollectionUtils.isNotEmpty(incomeItemList)) {
        for (RepairOrderTemplateOtherIncomeItem item : incomeItemList) {
          if (StringUtils.isBlank(item.getName())) {
            continue;
          }

          if (null == incomeItemDTOList) {
            incomeItemDTOList = new ArrayList<RepairOrderOtherIncomeItemDTO>();
          }

          RepairOrderOtherIncomeItemDTO itemDTO = new RepairOrderOtherIncomeItemDTO();

          itemDTO.setTemplateId(item.getId());
          itemDTO.setMemo(item.getMemo());
          itemDTO.setName(item.getName());
          itemDTO.setPrice(item.getPrice());
          itemDTO.setOtherIncomeCostPrice(item.getCostPrice());
          itemDTO.setOtherIncomeCalculateWay(item.getOtherIncomeCalculateWay());
          itemDTO.setCalculateCostPrice(item.getCalculateCostPrice() == null ? null : item.getCalculateCostPrice().name());
          itemDTO.setOtherIncomeRate(item.getOtherIncomePriceRate());

          incomeItemDTOList.add(itemDTO);
        }
      }
      RepairOrderDTO repairOrderDTO = new RepairOrderDTO();
      repairOrderDTO.setOtherIncomeItemDTOList(incomeItemDTOList);
      repairOrderTemplateDTO.setRepairOrderDTO(repairOrderDTO);
    }

    return repairOrderTemplateDTO;
  }

  @Override
  public RepairOrderTemplateDTO getSimpleRepairOrderTemplateByTemplateName(Long shopId, Long shopVersionId, Long storehouseId, String repairOrderTemplateName) throws Exception {
    TxnWriter txnWriter = txnDaoManager.getWriter();
    RepairOrderTemplate repairOrderTemplate = txnWriter.getRepairOrderTemplateByTemplateNameAndStatus(shopId, repairOrderTemplateName, RepairOrderTemplateStatus.ENABLED);
    if (repairOrderTemplate != null) {
      return repairOrderTemplate.toDTO();
    }
    return null;
  }


  public List<RepairOrderTemplateDTO> getTop5RepairOrderTemplateOrderByUsageCounter(Long shopId) {
    TxnWriter txnWriter = txnDaoManager.getWriter();
    List<RepairOrderTemplate> repairOrderTemplateList = txnWriter.getTop5RepairOrderTemplateByShopId(shopId);
    List<RepairOrderTemplateDTO> repairOrderTemplateDTOList = new ArrayList<RepairOrderTemplateDTO>();
    if (CollectionUtils.isNotEmpty(repairOrderTemplateList)) {
      for (RepairOrderTemplate repairOrderTemplate : repairOrderTemplateList) {
        if (repairOrderTemplate != null) {
          RepairOrderTemplateDTO repairOrderTemplateDTO = repairOrderTemplate.toDTO();
          repairOrderTemplateDTOList.add(repairOrderTemplateDTO);
        }
      }
    }
    return repairOrderTemplateDTOList;
  }


  public RepairOrderTemplateDTO updateRepairOrderTemplateUsageCounter(Long repairOrderTemplateId) {
    TxnWriter txnWriter = txnDaoManager.getWriter();
    Object status = txnWriter.begin();
    RepairOrderTemplateDTO repairOrderTemplateDTO = null;
    try {
      RepairOrderTemplate repairOrderTemplate = txnWriter.getById(RepairOrderTemplate.class, repairOrderTemplateId);
      if (repairOrderTemplate != null) {
        Integer oldUsageCounter = repairOrderTemplate.getUsageCounter();
        if (oldUsageCounter == null) {
          oldUsageCounter = 0;
        }
        repairOrderTemplate.setUsageCounter(++oldUsageCounter);
        txnWriter.update(repairOrderTemplate);
        repairOrderTemplateDTO = repairOrderTemplate.toDTO();
      }

      txnWriter.commit(status);
    } finally {
      txnWriter.rollback(status);
    }
    return repairOrderTemplateDTO;
  }


  private void saveOrUpdateRepairOrderTemplateOtherIncomeItem(TxnWriter txnWriter, RepairOrderTemplateDTO repairOrderTemplateDTO,boolean isSave) {
    Long shopId = repairOrderTemplateDTO.getShopId();

    if (isSave) {
      if (CollectionUtils.isEmpty(repairOrderTemplateDTO.getRepairOrderTemplateOtherIncomeItemDTOList())) {
        return;
      }

      for (RepairOrderTemplateOtherIncomeItemDTO itemDTO : repairOrderTemplateDTO.getRepairOrderTemplateOtherIncomeItemDTOList()) {
        RepairOrderTemplateOtherIncomeItem item = new RepairOrderTemplateOtherIncomeItem();
        itemDTO.setShopId(shopId);
        itemDTO.setRepairOrderTemplateId(repairOrderTemplateDTO.getId());
        item.fromDTO(itemDTO);
        txnWriter.save(item);
      }
    } else {
      List<RepairOrderTemplateOtherIncomeItem> templateOtherIncomeItemList = txnWriter.getRepairOrderTemplateOtherIncomeItem(shopId, repairOrderTemplateDTO.getId());

      Map<Long, RepairOrderTemplateOtherIncomeItemDTO> templateOtherIncomeItemDTOMap = RepairOrderTemplateOtherIncomeItemDTO.listToMap(repairOrderTemplateDTO.getRepairOrderTemplateOtherIncomeItemDTOList());

      if (CollectionUtils.isNotEmpty(templateOtherIncomeItemList)) {
        for (RepairOrderTemplateOtherIncomeItem item : templateOtherIncomeItemList) {
          RepairOrderTemplateOtherIncomeItemDTO itemDTO = templateOtherIncomeItemDTOMap.get(item.getId());
          if (itemDTO!=null) {
            itemDTO.setShopId(shopId);
            itemDTO.setRepairOrderTemplateId(repairOrderTemplateDTO.getId());
            item.fromDTO(itemDTO);
            txnWriter.update(item);
          } else {
            txnWriter.delete(RepairOrderTemplateOtherIncomeItem.class, item.getId());
          }
        }
      }

      if (CollectionUtils.isNotEmpty(repairOrderTemplateDTO.getRepairOrderTemplateOtherIncomeItemDTOList())) {
        for (RepairOrderTemplateOtherIncomeItemDTO itemDTO : repairOrderTemplateDTO.getRepairOrderTemplateOtherIncomeItemDTOList()) {
          if (null == itemDTO.getId()) {
            itemDTO.setShopId(shopId);
            itemDTO.setRepairOrderTemplateId(repairOrderTemplateDTO.getId());
            RepairOrderTemplateOtherIncomeItem item = new RepairOrderTemplateOtherIncomeItem();
            item.fromDTO(itemDTO);
            txnWriter.save(item);
          }
        }
      }
    }
  }

}

