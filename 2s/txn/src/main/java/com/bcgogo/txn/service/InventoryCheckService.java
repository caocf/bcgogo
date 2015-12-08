package com.bcgogo.txn.service;

import com.bcgogo.common.Pair;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.product.service.IProductService;
import com.bcgogo.search.model.InventorySearchIndex;
import com.bcgogo.search.service.CurrentUsed.ProductCurrentUsedService;
import com.bcgogo.search.service.ISearchService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.*;
import com.bcgogo.txn.model.*;
import com.bcgogo.txn.service.productThrough.IProductInStorageService;
import com.bcgogo.txn.service.productThrough.IProductOutStorageService;
import com.bcgogo.txn.service.solr.IProductSolrWriterService;
import com.bcgogo.user.service.utils.BcgogoShopLogicResourceUtils;
import com.bcgogo.utils.ArrayUtil;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 12-10-21
 * Time: 下午3:39
 * To change this template use File | Settings | File Templates.
 */
@Component
public class InventoryCheckService implements IInventoryCheckService {

  private static final Logger LOG = LoggerFactory.getLogger(InventoryCheckService.class);
  @Autowired
  private TxnDaoManager txnDaoManager;

  private IProductInStorageService productInStorageService;
  private IProductOutStorageService productOutStorageService;

  public IProductInStorageService getProductInStorageService() {
    return productInStorageService == null ? ServiceManager.getService(IProductInStorageService.class) : productInStorageService;
  }

  public IProductOutStorageService getProductOutStorageService() {
    return productOutStorageService == null ? ServiceManager.getService(IProductOutStorageService.class) : productOutStorageService;
  }

  @Override
  public InventoryCheckDTO saveInventoryCheckOrder(InventoryCheckDTO inventoryCheckOrderDTO) throws Exception{
    if (inventoryCheckOrderDTO != null) {
      ProductCurrentUsedService productCurrentUsedService = ServiceManager.getService(ProductCurrentUsedService.class);
      IInventoryService inventoryService = ServiceManager.getService(IInventoryService.class);
      ISearchService searchService =  ServiceManager.getService(ISearchService.class);
      IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
      IProductHistoryService productHistoryService = ServiceManager.getService(IProductHistoryService.class);
      TxnWriter txnWriter = txnDaoManager.getWriter();
      Object status = txnWriter.begin();
      try {
        InventoryCheck inventoryCheck = new InventoryCheck();
        if(BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(inventoryCheckOrderDTO.getShopVersionId())){
          StoreHouseDTO storeHouseDTO = storeHouseService.getStoreHouseDTOById(inventoryCheckOrderDTO.getShopId(),inventoryCheckOrderDTO.getStorehouseId());
          inventoryCheckOrderDTO.setStorehouseName(storeHouseDTO == null ? null : storeHouseDTO.getName());
        }
        inventoryCheck.fromDTO(inventoryCheckOrderDTO);
        txnWriter.save(inventoryCheck);

        inventoryCheckOrderDTO.setId(inventoryCheck.getId());
        inventoryCheckOrderDTO.setIdStr(inventoryCheck.getId().toString());

        InventoryCheckItemDTO[] inventoryCheckItemDTOs = inventoryCheckOrderDTO.getItemDTOs();
        Map<Long, Pair<Long, Boolean>> recentChangedProductMap = new HashMap<Long, Pair<Long, Boolean>>();
        List<InventorySearchIndex> inventorySearchIndexes = new ArrayList<InventorySearchIndex>();
        Set<Long> updateProductIds = new HashSet<Long>();
        InventoryLimitDTO inventoryLimitDTO = new InventoryLimitDTO();
        if (!ArrayUtils.isEmpty(inventoryCheckItemDTOs)) {
          Set<Long> productIds = new HashSet<Long>();
          for (InventoryCheckItemDTO inventoryCheckItemDTO : inventoryCheckItemDTOs) {
            productIds.add(inventoryCheckItemDTO.getProductId());
          }
          Map<Long, ProductHistoryDTO> productHistoryDTOMap = productHistoryService.getOrSaveProductHistoryByLocalInfoId(inventoryCheck.getShopId(), productIds.toArray(new Long[productIds.size()]));
          for (InventoryCheckItemDTO inventoryCheckItemDTO : inventoryCheckItemDTOs) {
            inventoryCheckItemDTO.setInventoryCheckId(inventoryCheck.getId());
            ProductHistoryDTO productHistoryDTO = productHistoryDTOMap.get(inventoryCheckItemDTO.getProductId());
            inventoryCheckItemDTO.setProductHistoryId(productHistoryDTO==null?null:productHistoryDTO.getId());
            InventoryCheckItem inventoryCheckItem = new InventoryCheckItem();
            inventoryCheckItem.fromDTO(inventoryCheckItemDTO);
            txnWriter.save(inventoryCheckItem);
            inventoryCheckItemDTO.setId(inventoryCheckItem.getId());
            inventoryCheckItemDTO.setIdStr(inventoryCheckItem.getId().toString());
            Long productId = inventoryCheckItemDTO.getProductId();
            Long shopId = inventoryCheckOrderDTO.getShopId();
            Inventory inventory = null;
            if (productId != null && shopId != null) {
              inventory = txnWriter.getInventoryByIdAndshopId(productId, shopId);
            }
            InventorySearchIndex inventorySearchIndex = searchService.getInventorySearchIndexByProductId(inventoryCheckItemDTO.getProductId());
            if (inventory != null) {
              updateProductIds.add(inventory.getId());
              double actualInventoryAmount = inventoryCheckItemDTO.getActualInventoryAmount() == null ? 0d : inventoryCheckItemDTO.getActualInventoryAmount().doubleValue();
              double currentInventoryAmount = inventory.getAmount();
              if(BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(inventoryCheckOrderDTO.getShopVersionId())){
                StoreHouseInventoryDTO storeHouseInventoryDTO = storeHouseService.getStoreHouseInventoryDTO(inventoryCheckOrderDTO.getStorehouseId(),inventory.getId());
                currentInventoryAmount = storeHouseInventoryDTO==null?0d:storeHouseInventoryDTO.getAmount();
              }
              //todo by qxy
//              double currentInventoryAveragePrice = inventory.getInventoryAveragePrice() == null ? 0d : inventoryCheckItemDTO.getInventoryAveragePrice().doubleValue();
                if (actualInventoryAmount != currentInventoryAmount ) {
                updateProductIds.add(inventory.getId());
                inventoryService.caculateBeforeLimit(inventory.toDTO(), inventoryLimitDTO);
                if(BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(inventoryCheckOrderDTO.getShopVersionId())){
                  StoreHouseInventoryDTO storeHouseInventoryDTO = new StoreHouseInventoryDTO();
                  storeHouseInventoryDTO.setAmount(actualInventoryAmount);
                  storeHouseInventoryDTO.setProductLocalInfoId(inventory.getId());
                  storeHouseInventoryDTO.setStorehouseId(inventoryCheckOrderDTO.getStorehouseId());
                  storeHouseService.saveOrUpdateStoreHouseInventoryDTO(txnWriter,storeHouseInventoryDTO);
                  Double inventoryAmount = storeHouseService.sumStoreHouseAllInventoryAmountByProductLocalInfoId(shopId,inventory.getId());
                  inventory.setAmount(inventoryAmount);
                }else{
                  inventory.setAmount(actualInventoryAmount);
                }
                inventoryService.caculateAfterLimit(inventory.toDTO(), inventoryLimitDTO);
                //更新数据库库存
                txnWriter.update(inventory);
                if (inventorySearchIndex != null) {
                  inventorySearchIndex.setAmount(inventoryCheckItemDTO.getActualInventoryAmount());
                  inventorySearchIndexes.add(inventorySearchIndex);
                }
                //构建商品更新缓存ID
                recentChangedProductMap.put(inventoryCheckItemDTO.getProductId(), new Pair(System.currentTimeMillis(), false));
              }
            }
          }

        }

        getProductInStorageService().productThroughByInventoryCheck(inventoryCheckOrderDTO, txnWriter);

        txnWriter.commit(status);

        if (updateProductIds != null && !updateProductIds.isEmpty()) {
          //reindex   solr
          ServiceManager.getService(IProductSolrWriterService.class).createProductSolrIndex(inventoryCheckOrderDTO.getShopId(), updateProductIds.toArray(new Long[updateProductIds.size()]));
        }
        //InventorySearchIndex
        searchService.updateInventorySearchIndexAmountWithList(inventorySearchIndexes);
        //构建缓存
        productCurrentUsedService.saveRecentChangedProductInMemory(inventoryCheckOrderDTO.getShopId(), recentChangedProductMap);
        inventoryService.updateMemocacheLimitByInventoryLimitDTO(inventoryCheckOrderDTO.getShopId(), inventoryLimitDTO);
      } finally {
        txnWriter.rollback(status);
      }
    }
    return inventoryCheckOrderDTO;
  }


  public InventoryCheckDTO saveInventoryCheckOrderWithoutUpdateInventoryInfo(InventoryCheckDTO inventoryCheckOrderDTO) {
    if (inventoryCheckOrderDTO != null) {
      IProductHistoryService productHistoryService = ServiceManager.getService(IProductHistoryService.class);
      TxnWriter txnWriter = txnDaoManager.getWriter();
      Object status = txnWriter.begin();
      try {
        InventoryCheck inventoryCheck = new InventoryCheck();
        inventoryCheck.fromDTO(inventoryCheckOrderDTO);
        txnWriter.save(inventoryCheck);
        inventoryCheckOrderDTO.setId(inventoryCheck.getId());
        inventoryCheckOrderDTO.setIdStr(inventoryCheck.getId().toString());
        InventoryCheckItemDTO[] inventoryCheckItemDTOs = inventoryCheckOrderDTO.getItemDTOs();
        if (!ArrayUtil.isEmpty(inventoryCheckItemDTOs)) {
          Set<Long> productIds = new HashSet<Long>();
          for (InventoryCheckItemDTO inventoryCheckItemDTO : inventoryCheckItemDTOs) {
            productIds.add(inventoryCheckItemDTO.getProductId());
          }
          Map<Long, ProductHistoryDTO> productHistoryDTOMap = productHistoryService.getOrSaveProductHistoryByLocalInfoId(inventoryCheck.getShopId(), productIds.toArray(new Long[productIds.size()]));

          for (InventoryCheckItemDTO inventoryCheckItemDTO : inventoryCheckItemDTOs) {
            inventoryCheckItemDTO.setInventoryCheckId(inventoryCheck.getId());
            ProductHistoryDTO productHistoryDTO = productHistoryDTOMap.get(inventoryCheckItemDTO.getProductId());
            inventoryCheckItemDTO.setProductHistoryId(productHistoryDTO==null?null:productHistoryDTO.getId());
            InventoryCheckItem inventoryCheckItem = new InventoryCheckItem();
            inventoryCheckItem.fromDTO(inventoryCheckItemDTO);
            txnWriter.save(inventoryCheckItem);
            inventoryCheckItemDTO.setId(inventoryCheckItem.getId());
            inventoryCheckItemDTO.setIdStr(inventoryCheckItem.getId().toString());

          }
          getProductInStorageService().productThroughByInventoryCheck(inventoryCheckOrderDTO, txnWriter);
        }
        txnWriter.commit(status);
      } finally {
        txnWriter.rollback(status);
      }
    }
    return inventoryCheckOrderDTO;

  }

  public InventoryCheckDTO getInventoryCheckById(Long shopId,Long inventoryCheckId) throws Exception {
    IProductService productService=ServiceManager.getService(IProductService.class);
    ITxnService txnService=ServiceManager.getService(ITxnService.class);
    TxnWriter txnWriter = txnDaoManager.getWriter();
    InventoryCheck inventoryCheck=txnWriter.getInventoryCheckById(shopId,inventoryCheckId);
    if(inventoryCheck==null) return null;
    List<InventoryCheckItemDTO> itemDTOs=new ArrayList<InventoryCheckItemDTO>();
    InventoryCheckDTO inventoryCheckDTO=inventoryCheck.toDTO();
    InventoryCheckItemDTO itemDTO=null;
    ProductDTO productDTO=null;
    for(InventoryCheckItem item:txnWriter.getInventoryCheckItem(inventoryCheckId)){
      itemDTO=item.toDTO();
      itemDTOs.add(itemDTO);
      productDTO = productService.getProductByProductLocalInfoId(itemDTO.getProductId(),shopId);
      if(productDTO==null){
        LOG.error("商品信息异常！");
        continue;
      }
      itemDTO.setProductName(productDTO.getName());
      itemDTO.setBrand(productDTO.getBrand());
      itemDTO.setSpec(productDTO.getSpec());
      itemDTO.setModel(productDTO.getModel());
      itemDTO.setVehicleBrand(productDTO.getVehicleBrand());
      itemDTO.setVehicleModel(productDTO.getVehicleModel());
      itemDTO.setCommodityCode(productDTO.getCommodityCode());
      itemDTO.setInventoryAmountUnit(NumberUtil.doubleVal(itemDTO.getInventoryAmount()) + (StringUtil.isEmpty(itemDTO.getUnit())?"":itemDTO.getUnit()));
    }

    inventoryCheckDTO.setItemDTOs(itemDTOs.toArray(new InventoryCheckItemDTO[itemDTOs.size()]));
    return inventoryCheckDTO;
  }

}
