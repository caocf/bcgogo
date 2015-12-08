package com.bcgogo.txn.service.productThrough;

import com.bcgogo.common.Pager;
import com.bcgogo.common.Result;
import com.bcgogo.config.model.Shop;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.enums.*;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.product.dto.ProductLocalInfoDTO;
import com.bcgogo.product.service.IProductService;
import com.bcgogo.search.dto.ItemIndexDTO;
import com.bcgogo.search.service.IItemIndexService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.*;
import com.bcgogo.txn.model.*;
import com.bcgogo.txn.service.IInventoryService;
import com.bcgogo.txn.service.ITxnService;
import com.bcgogo.txn.service.web.IGoodsStorageService;
import com.bcgogo.user.dto.SupplierDTO;
import com.bcgogo.user.service.ISupplierService;
import com.bcgogo.user.service.SupplierService;
import com.bcgogo.user.service.utils.BcgogoShopLogicResourceUtils;
import com.bcgogo.utils.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 商品出入库打通专用接口实现类
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 13-4-12
 * Time: 上午9:34
 * To change this template use File | Settings | File Templates.
 */
@Component
public class ProductThroughService implements IProductThroughService {
  private static final Logger LOG = LoggerFactory.getLogger(ProductThroughService.class);
  @Autowired
  private TxnDaoManager txnDaoManager;

  private IItemIndexService itemIndexService;
  private IProductService productService;
  private ITxnService txnService;
  private IProductThroughOrderService productThroughOrderService;
  private IProductInStorageService productInStorageService;
  private IProductOutStorageService productOutStorageService;

  public IProductThroughOrderService getProductThroughOrderService() {
    return productThroughOrderService == null ? ServiceManager.getService(ProductThroughOrderService.class) : productThroughOrderService;
  }

  public IProductInStorageService getProductInStorageService() {
    return productInStorageService == null ? ServiceManager.getService(IProductInStorageService.class) : productInStorageService;
  }

  public IProductOutStorageService getProductOutStorageService() {
    return productOutStorageService == null ? ServiceManager.getService(IProductOutStorageService.class) : productOutStorageService;
  }

  public ITxnService getTxnService() {
    return txnService == null ? ServiceManager.getService(ITxnService.class) : txnService;
  }

  public IProductService getProductService() {
    return productService == null ? ServiceManager.getService(IProductService.class) : productService;
  }


  public IItemIndexService getItemIndexService() {
    return itemIndexService == null ? ServiceManager.getService(IItemIndexService.class) : itemIndexService;
  }

  @Override
  public void initSupplierInventory() throws Exception {
    LOG.info("初始化出入库数据开始...");
    IGoodsStorageService goodsStorageService = ServiceManager.getService(IGoodsStorageService.class);
    IInventoryService inventoryService = ServiceManager.getService(IInventoryService.class);

    List<Shop> shopList = ServiceManager.getService(IConfigService.class).getShop();
    int defaultPageSize = 1000;
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      for (Shop shop : shopList) {
        LOG.info("ShopID:{}初始化开始", shop.getId());
        //该店铺特殊处理
        if(shop.getId() == 10000010018221723L) {
          continue;
        }

        long startId = 0L;
        do {
          //1.supplier 和product关系
          List<SupplierInventoryDTO> supplierInventoryDTOs = new ArrayList<SupplierInventoryDTO>();
          Long endId = goodsStorageService.getNextProductId(shop.getId(), startId, defaultPageSize);
          if (endId == null) {
            break;
          }
          if (startId != endId) {
            supplierInventoryDTOs = goodsStorageService.getInitSupplierInventory(shop.getId(), shop.getShopVersionId(), startId, endId);
            saveInitSupplierInventory(writer, supplierInventoryDTOs);
            writer.commit(status);
            status = writer.begin();
            startId = endId;
          } else {
            break;
          }
        } while (true);
        //2.库存归类
        int productAmount = inventoryService.countProductInventory(shop.getId());
        int currentPage = 1;
        Pager pager = new Pager(productAmount, currentPage, defaultPageSize);

        do {
          List<SupplierInventoryDTO> supplierInventoryDTOs = getInitSupplierInventory(shop.getId(), shop.getShopVersionId(), pager);
          saveInitSupplierInventory(writer, supplierInventoryDTOs);
          writer.commit(status);
          status = writer.begin();
          if (pager.hasNextPage() && pager.getCurrentPage() < pager.getTotalPage()) {
            currentPage++;
            pager = new Pager(productAmount, currentPage, defaultPageSize);
          } else {
            break;
          }
        } while (true);
        LOG.info("ShopID:{}初始化结束", shop.getId());
      }
      writer.commit(status);
      LOG.info("出入库初始化结束");
    } finally {
      writer.rollback(status);
    }

  }

  //只用于初始化
  private List<SupplierInventoryDTO> getInitSupplierInventory(Long shopId, Long shopVersionId, Pager pager) {
    List<SupplierInventoryDTO> supplierInventoryDTOs = new ArrayList<SupplierInventoryDTO>();
    if (BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(shopVersionId)) {
      supplierInventoryDTOs = txnDaoManager.getWriter().getInitHaveStoreHouseSupplierInventoryAmount(shopId, pager);
    } else {
      supplierInventoryDTOs = txnDaoManager.getWriter().getInitNoStoreHouseSupplierInventoryAmount(shopId, pager);
    }
    return supplierInventoryDTOs;
  }

     //只用于初始化
  private void saveInitSupplierInventory(TxnWriter writer, Collection<SupplierInventoryDTO> supplierInventoryDTOs) throws Exception {
    if (CollectionUtils.isEmpty(supplierInventoryDTOs)) {
      return;
    }
    for (SupplierInventoryDTO supplierInventoryDTO : supplierInventoryDTOs) {
      SupplierInventory supplierInventory = new SupplierInventory();
      supplierInventory.fromDTO(supplierInventoryDTO);
      writer.save(supplierInventory);
    }
  }

  @Override
  public void saveOrUpdateSupplierInventory(TxnWriter writer, Collection<SupplierInventoryDTO> supplierInventoryDTOs) throws Exception {
    IProductService productService = ServiceManager.getService(IProductService.class);
    ISupplierService supplierService = ServiceManager.getService(ISupplierService.class);

    Long shopId = null;
    if (CollectionUtils.isEmpty(supplierInventoryDTOs)) {
    return;
  }
    //undefinedSupplierInventoryDTOs-->未指定供应商信息(不存在供应商Id的）
    //supplierInventoryDTO组装分类
    List<SupplierInventoryDTO> undefinedSupplierInventoryDTOs = new ArrayList<SupplierInventoryDTO>();
    Map<Long, List<SupplierInventoryDTO>> supplierInventoryDTOMap = new HashMap<Long, List<SupplierInventoryDTO>>();
    Map<Long, SupplierDTO> supplierDTOMap = new HashMap<Long, SupplierDTO>();
    Set<Long> allProductIds = new HashSet<Long>();
    Map<Long, ProductLocalInfoDTO> productLocalInfoDTOMap = new HashMap<Long, ProductLocalInfoDTO>();
    for (SupplierInventoryDTO supplierInventoryDTO : supplierInventoryDTOs) {
      if (supplierInventoryDTO.getSupplierId() == null) {
        undefinedSupplierInventoryDTOs.add(supplierInventoryDTO);
      } else {
        List<SupplierInventoryDTO> definedSupplierInventoryDTOs = supplierInventoryDTOMap.get(supplierInventoryDTO.getSupplierId());
        if (CollectionUtils.isNotEmpty(definedSupplierInventoryDTOs)) {
          definedSupplierInventoryDTOs.add(supplierInventoryDTO);
        } else {
          definedSupplierInventoryDTOs = new ArrayList<SupplierInventoryDTO>();
          definedSupplierInventoryDTOs.add(supplierInventoryDTO);
          supplierInventoryDTOMap.put(supplierInventoryDTO.getSupplierId(), definedSupplierInventoryDTOs);
        }
      }
      if (supplierInventoryDTO.getProductId() != null) {
        allProductIds.add(supplierInventoryDTO.getProductId());
      }
      if (supplierInventoryDTO.getShopId() != null && shopId == null) {
        shopId = supplierInventoryDTO.getShopId();
      }
    }

    productLocalInfoDTOMap = productService.getProductLocalInfoMap(shopId, allProductIds.toArray(new Long[allProductIds.size()]));
    //处理有id的供应商
    if (!supplierInventoryDTOMap.isEmpty()) {
      Set<Long> supplierIds = supplierInventoryDTOMap.keySet();
      supplierDTOMap = supplierService.getSupplierByIdSet(shopId, supplierIds);
      for (Long supplierId : supplierIds) {
        Set<Long> productIds = new HashSet<Long>();
        SupplierDTO supplierDTO = supplierDTOMap.get(supplierId);
        Long storehouseId = null;
        List<SupplierInventoryDTO> definedSupplierInventoryDTOs = supplierInventoryDTOMap.get(supplierId);
        if (CollectionUtils.isEmpty(definedSupplierInventoryDTOs)) {
          continue;
        }
        for (SupplierInventoryDTO supplierInventoryDTO : definedSupplierInventoryDTOs) {
          if (supplierInventoryDTO.getProductId() != null) {
            productIds.add(supplierInventoryDTO.getProductId());
          }
          if (supplierInventoryDTO.getShopId() != null && shopId == null) {
            shopId = supplierInventoryDTO.getShopId();
          }
          if (supplierInventoryDTO.getStorehouseId() != null && storehouseId == null) {
            storehouseId = supplierInventoryDTO.getStorehouseId();
          }
        }
        Map<Long, SupplierInventory> dbSupplierInventoryMap = getSupplierInventoryMap(shopId, supplierId, storehouseId, productIds);
        saveOrUpdateSupplierInventory(writer, definedSupplierInventoryDTOs, supplierDTO, dbSupplierInventoryMap, productLocalInfoDTOMap);
      }
    }

    //处理没有id的供应商库存
    if (CollectionUtils.isNotEmpty(undefinedSupplierInventoryDTOs)) {
      for (SupplierInventoryDTO undefinedSupplierInventoryDTO : undefinedSupplierInventoryDTOs) {
        if (undefinedSupplierInventoryDTO.getProductId() == null) {
          continue;
        }
        if (undefinedSupplierInventoryDTO.getSupplierType() == null) {
          undefinedSupplierInventoryDTO.setSupplierType(OutStorageSupplierType.UNDEFINED_SUPPLIER);
        }
        undefinedSupplierInventoryDTO.setSupplierName(undefinedSupplierInventoryDTO.getSupplierType().getName());
        Map<Long, SupplierInventory> dbSupplierInventoryMap = new HashMap<Long, SupplierInventory>();
        SupplierInventory supplierInventory = writer.getSupplierInventoryBySupplierType(shopId, undefinedSupplierInventoryDTO.getProductId(),
            undefinedSupplierInventoryDTO.getStorehouseId(), undefinedSupplierInventoryDTO.getSupplierType());
        if (supplierInventory != null) {
          dbSupplierInventoryMap.put(supplierInventory.getProductId(), supplierInventory);
        }
        List<SupplierInventoryDTO> toHandelSupplierInventoryDTO = new ArrayList<SupplierInventoryDTO>();
        toHandelSupplierInventoryDTO.add(undefinedSupplierInventoryDTO);
        saveOrUpdateSupplierInventory(writer, toHandelSupplierInventoryDTO, null, dbSupplierInventoryMap, productLocalInfoDTOMap);
      }
    }
  }

  private void saveOrUpdateSupplierInventory(TxnWriter writer, List<SupplierInventoryDTO> toUpdateSupplierInventoryDTOs, SupplierDTO supplierDTO,
                                             Map<Long, SupplierInventory> dbSupplierInventoryMap, Map<Long, ProductLocalInfoDTO> productLocalInfoDTOMap) {
    IInventoryService inventoryService = ServiceManager.getService(IInventoryService.class);
    for (SupplierInventoryDTO supplierInventoryDTO : toUpdateSupplierInventoryDTOs) {
      SupplierInventory dbSupplierInventory = dbSupplierInventoryMap.get(supplierInventoryDTO.getProductId());
      if (supplierDTO != null) {
        supplierInventoryDTO.setSupplierName(supplierDTO.getName());
        supplierInventoryDTO.setSupplierContact(supplierDTO.getContact());
        supplierInventoryDTO.setSupplierMobile(supplierDTO.getMobile());
      }
      if (dbSupplierInventory == null) {
        dbSupplierInventory = new SupplierInventory();
        if (supplierInventoryDTO.getChangeAmount() != null && supplierInventoryDTO.getChangeAmount() > 0) {
          supplierInventoryDTO.setRemainAmount(supplierInventoryDTO.getChangeAmount());
        } else if (supplierInventoryDTO.getRemainAmount() == null || supplierInventoryDTO.getRemainAmount() < -0.0001) {
          supplierInventoryDTO.setRemainAmount(0d);
        }

        if (supplierInventoryDTO.getTotalInStorageChangeAmount() != null && supplierInventoryDTO.getTotalInStorageChangeAmount() > 0) {
          supplierInventoryDTO.setTotalInStorageAmount(supplierInventoryDTO.getTotalInStorageChangeAmount());
        } else if (supplierInventoryDTO.getTotalInStorageAmount() == null || supplierInventoryDTO.getTotalInStorageAmount() < -0.0001) {
          supplierInventoryDTO.setTotalInStorageAmount(0d);
        }
        if (NumberUtil.doubleVal(supplierInventoryDTO.getLastStoragePrice()) > 0.0001) {
          supplierInventoryDTO.setMaxStoragePrice(supplierInventoryDTO.getLastStoragePrice());
          supplierInventoryDTO.setMinStoragePrice(supplierInventoryDTO.getLastStoragePrice());
          if(supplierInventoryDTO.getAverageStoragePrice() == null){
            supplierInventoryDTO.setAverageStoragePrice(supplierInventoryDTO.getLastStoragePrice());
          }
        }
        if(!YesNo.YES.equals(supplierInventoryDTO.getDisabled())){
          supplierInventoryDTO.setDisabled(YesNo.NO);
        }
        dbSupplierInventory.fromDTO(supplierInventoryDTO);
        if(dbSupplierInventory.getLastPurchaseInventoryOrderId() == null){
          dbSupplierInventory.setLastStorageAmount(null);
          dbSupplierInventory.setLastStorageTime(null);
          dbSupplierInventory.setLastStoragePrice(null);
        }
        writer.save(dbSupplierInventory);
        supplierInventoryDTO.setId(dbSupplierInventory.getId());
        dbSupplierInventoryMap.put(dbSupplierInventory.getProductId(), dbSupplierInventory);
      } else {
        double preMaxStoragePrice = NumberUtil.doubleVal(dbSupplierInventory.getMaxStoragePrice());
        double preMinStoragePrice = NumberUtil.doubleVal(dbSupplierInventory.getMinStoragePrice());
        double preAverageStoragePrice = NumberUtil.doubleVal(dbSupplierInventory.getAverageStoragePrice());
        double preTotalInStorageAmount = NumberUtil.doubleVal(dbSupplierInventory.getTotalInStorageAmount());
        double preRemainStorageAmount = NumberUtil.doubleVal(dbSupplierInventory.getRemainAmount());
        double preLastStorageAmount = NumberUtil.doubleVal(dbSupplierInventory.getLastStorageAmount());

        double newLastPurchasePrice = NumberUtil.doubleVal(supplierInventoryDTO.getLastStoragePrice());
        double newChangeAmount = NumberUtil.doubleVal(supplierInventoryDTO.getChangeAmount());
        double newTotalInStorageChangeAmount = NumberUtil.doubleVal(supplierInventoryDTO.getTotalInStorageChangeAmount());
        double newLastStorageAmount = NumberUtil.doubleVal(supplierInventoryDTO.getLastStorageAmount());
        double newAverageStoragePrice = NumberUtil.doubleVal(supplierInventoryDTO.getAverageStoragePrice());
        double newMaxPurchasePrice = NumberUtil.doubleVal(supplierInventoryDTO.getMaxStoragePrice());
        double newMinPurchasePrice = NumberUtil.doubleVal(supplierInventoryDTO.getMinStoragePrice());
        String sellUnit = dbSupplierInventory.getUnit();

        ProductLocalInfoDTO productLocalInfoDTO = productLocalInfoDTOMap.get(dbSupplierInventory.getProductId());

        if (UnitUtil.isStorageUnit(dbSupplierInventory.getUnit(), productLocalInfoDTO)) {
          preMaxStoragePrice = preMaxStoragePrice / productLocalInfoDTO.getRate();
          preAverageStoragePrice = preAverageStoragePrice / productLocalInfoDTO.getRate();
          preMinStoragePrice = preMinStoragePrice / productLocalInfoDTO.getRate();
          preTotalInStorageAmount = preTotalInStorageAmount * productLocalInfoDTO.getRate();
          preRemainStorageAmount = preRemainStorageAmount * productLocalInfoDTO.getRate();
          preLastStorageAmount = preLastStorageAmount * productLocalInfoDTO.getRate();
          sellUnit = productLocalInfoDTO.getSellUnit();
        }
        if (UnitUtil.isStorageUnit(supplierInventoryDTO.getUnit(), productLocalInfoDTO)) {
          newLastPurchasePrice = newLastPurchasePrice / productLocalInfoDTO.getRate();
          newChangeAmount = newChangeAmount * productLocalInfoDTO.getRate();
          newTotalInStorageChangeAmount = newTotalInStorageChangeAmount * productLocalInfoDTO.getRate();
          newLastStorageAmount = newLastStorageAmount * productLocalInfoDTO.getRate();
          newAverageStoragePrice = newAverageStoragePrice / productLocalInfoDTO.getRate();
          newMaxPurchasePrice = newMaxPurchasePrice / productLocalInfoDTO.getRate();
          newMinPurchasePrice = newMinPurchasePrice / productLocalInfoDTO.getRate();
        }

        dbSupplierInventory.setUnit(sellUnit);
        dbSupplierInventory.setMinStoragePrice(preMinStoragePrice);
        dbSupplierInventory.setMaxStoragePrice(preMaxStoragePrice);
        dbSupplierInventory.setTotalInStorageAmount(preTotalInStorageAmount);
        dbSupplierInventory.setRemainAmount(preRemainStorageAmount);
        dbSupplierInventory.setSupplierName(supplierInventoryDTO.getSupplierName());
        dbSupplierInventory.setSupplierContact(supplierInventoryDTO.getSupplierContact());
        dbSupplierInventory.setSupplierMobile(supplierInventoryDTO.getSupplierMobile());



        //设定剩余数量
        if (preRemainStorageAmount + newChangeAmount > -0.0001) {
          dbSupplierInventory.setRemainAmount(preRemainStorageAmount + newChangeAmount);
        } else {
          dbSupplierInventory.setRemainAmount(0d);
        }
        //设定最新平均价  1,入库单导致的平均价变化，2，单位变化带来的平均价变化
        if (Math.abs(newChangeAmount) > 0.0001 && Math.abs(newLastStorageAmount)>0.0001 ) {
          newAverageStoragePrice = inventoryService.calculateInventoryAveragePrice(preAverageStoragePrice, preRemainStorageAmount, newLastPurchasePrice, newChangeAmount);
          dbSupplierInventory.setAverageStoragePrice(newAverageStoragePrice);
        }
        //设定最高价最低价
        if (OrderTypes.INVENTORY.equals(supplierInventoryDTO.getOrderType()) && newLastPurchasePrice > 0.001) {
          dbSupplierInventory.setMaxStoragePrice(newLastPurchasePrice > preMaxStoragePrice ? newLastPurchasePrice : preMaxStoragePrice);
        }
        if (OrderTypes.INVENTORY.equals(supplierInventoryDTO.getOrderType()) && newLastPurchasePrice > 0.001) {
          if (preMinStoragePrice < 0.0001) {
            dbSupplierInventory.setMinStoragePrice(newLastPurchasePrice);
          } else {
            dbSupplierInventory.setMinStoragePrice(newLastPurchasePrice < preMinStoragePrice ? newLastPurchasePrice : preMinStoragePrice);
          }
        }

        //设置累计入库量
        if (preTotalInStorageAmount + newTotalInStorageChangeAmount > -0.0001) {
          dbSupplierInventory.setTotalInStorageAmount(preTotalInStorageAmount + newTotalInStorageChangeAmount);
        }

        //记录最后入库信息
        if (supplierInventoryDTO.getLastStorageTime() != null && supplierInventoryDTO.getLastPurchaseInventoryOrderId() != null
            && (dbSupplierInventory.getLastStorageTime() == null || supplierInventoryDTO.getLastStorageTime() >= dbSupplierInventory.getLastStorageTime())) {
          dbSupplierInventory.setLastStorageTime(supplierInventoryDTO.getLastStorageTime());
          //一张入库单同一商品多条入库记录的时候需要合并
          if (dbSupplierInventory.getLastPurchaseInventoryOrderId() != null
              && dbSupplierInventory.getLastPurchaseInventoryOrderId() == supplierInventoryDTO.getLastPurchaseInventoryOrderId()) {
            dbSupplierInventory.setLastStorageAmount(preLastStorageAmount + newLastStorageAmount);
            if (newLastPurchasePrice > 0.0001) {
              dbSupplierInventory.setLastStoragePrice(newLastPurchasePrice);
            }
          } else {
            dbSupplierInventory.setLastStorageAmount(newLastStorageAmount);
            dbSupplierInventory.setLastStoragePrice(newLastPurchasePrice);
          }
          dbSupplierInventory.setLastPurchaseInventoryOrderId(supplierInventoryDTO.getLastPurchaseInventoryOrderId());
        }

        //修改disabled状态
        if(YesNo.YES.equals(supplierInventoryDTO.getDisabled())){
          dbSupplierInventory.setDisabled(YesNo.YES);
        }else{
          dbSupplierInventory.setDisabled(YesNo.NO);
        }
        writer.update(dbSupplierInventory);
      }
    }
  }

  @Override
  public void saveOrUpdateSupplierInventory(Collection<SupplierInventoryDTO> supplierInventoryDTOs) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
      Object status = writer.begin();
      try {
        saveOrUpdateSupplierInventory(writer,supplierInventoryDTOs);
        writer.commit(status);
      } finally {
        writer.rollback(status);
      }
  }

  @Override
  public void saveOrUpdateSupplierInventoryByModify(TxnWriter writer, Collection<SupplierInventoryDTO> supplierInventoryDTOs) throws Exception {
    if (CollectionUtil.isEmpty(supplierInventoryDTOs)) {
      return;
    }
    Set<Long> supplierInventoryId = new HashSet<Long>();
    for (SupplierInventoryDTO supplierInventoryDTO : supplierInventoryDTOs) {
      if (supplierInventoryDTO.getId() != null) {
        supplierInventoryId.add(supplierInventoryDTO.getId());
      }
    }
    Map<Long, SupplierInventory> supplierInventoryMap = getSupplierInventoryMapByIds(supplierInventoryId);
    for (SupplierInventoryDTO supplierInventoryDTO : supplierInventoryDTOs) {
      SupplierInventory supplierInventory = null;
      if(supplierInventoryDTO.getDisabled() == null){
        supplierInventoryDTO.setDisabled(YesNo.NO);
      }
      if (supplierInventoryDTO.getId() != null) {
        supplierInventory = supplierInventoryMap.get(supplierInventoryDTO.getId());
      }
      if (supplierInventory == null) {
        supplierInventory = new SupplierInventory();
      }
      supplierInventory.fromDTO(supplierInventoryDTO);
      writer.saveOrUpdate(supplierInventory);
    }
  }

  @Override
  public Map<Long, SupplierInventory> getSupplierInventoryMapByIds(Set<Long> supplierInventoryIds) {
    Map<Long, SupplierInventory> supplierInventoryMap = new HashMap<Long, SupplierInventory>();
    if (CollectionUtils.isEmpty(supplierInventoryIds)) {
      return supplierInventoryMap;
    }
    List<SupplierInventory> supplierInventories = txnDaoManager.getWriter().getSupplierInventoriesByIds(supplierInventoryIds);
    if (CollectionUtils.isNotEmpty(supplierInventories)) {
      for (SupplierInventory supplierInventory : supplierInventories) {
        supplierInventoryMap.put(supplierInventory.getId(), supplierInventory);
      }
    }
    return supplierInventoryMap;
  }

  @Override
  public Map<Long, SupplierInventory> getSupplierInventoryMapByPurchaseInventoryId(Long shopId, Long purchaseInventoryId) {
    Map<Long, SupplierInventory> supplierInventoryMap = new HashMap<Long, SupplierInventory>();
    if (shopId == null|| purchaseInventoryId == null) {
      return supplierInventoryMap;
    }
    List<SupplierInventory> supplierInventories = txnDaoManager.getWriter().getSupplierInventoriesByPurchaseInventoryId(shopId, purchaseInventoryId);
    if (CollectionUtils.isNotEmpty(supplierInventories)) {
      for (SupplierInventory supplierInventory : supplierInventories) {
        supplierInventoryMap.put(supplierInventory.getProductId(), supplierInventory);
      }
    }
    return supplierInventoryMap;
  }

  @Override
  public void saveOrUpdateSupplierInventoryByModify(Collection<SupplierInventoryDTO> supplierInventoryDTOs) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
      Object status = writer.begin();
      try {
        saveOrUpdateSupplierInventoryByModify(writer, supplierInventoryDTOs);
        writer.commit(status);
      } finally {
        writer.rollback(status);
      }
  }

  @Override
  public Map<Long, SupplierInventory> getSupplierInventoryMap(Long shopId, Long supplierId, Long storehouseId, Set<Long> productIds) {
    Map<Long, SupplierInventory> supplierInventoryMap = new HashMap<Long, SupplierInventory>();
    if (shopId == null|| CollectionUtils.isEmpty(productIds)) {
      return supplierInventoryMap;
    }
    List<SupplierInventory> supplierInventories = getSupplierInventoryList(shopId, supplierId, storehouseId, productIds);
    if (CollectionUtils.isNotEmpty(supplierInventories)) {
      for (SupplierInventory supplierInventory : supplierInventories) {
        supplierInventoryMap.put(supplierInventory.getProductId(), supplierInventory);
      }
    }
    return supplierInventoryMap;
  }

  @Override
  public Map<Long, SupplierInventoryDTO> getSupplierInventoryDTOMap(Long shopId, Long supplierId, Long storehouseId, Set<Long> productIds) {
    Map<Long, SupplierInventoryDTO> supplierInventoryDTOMap = new HashMap<Long, SupplierInventoryDTO>();
    if (shopId == null|| CollectionUtils.isEmpty(productIds)) {
      return supplierInventoryDTOMap;
    }
    List<SupplierInventory> supplierInventories = getSupplierInventoryList(shopId, supplierId, storehouseId, productIds);
    if (CollectionUtils.isNotEmpty(supplierInventories)) {
      for (SupplierInventory supplierInventory : supplierInventories) {
        supplierInventoryDTOMap.put(supplierInventory.getProductId(), supplierInventory.toDTO());
      }
    }
    return supplierInventoryDTOMap;
  }

  @Override
  public List<SupplierInventory> getSupplierInventoryList(Long shopId, Long supplierId, Long storehouseId, Set<Long> productIds) {
    if (shopId == null|| CollectionUtils.isEmpty(productIds)) {
      return null;
    }
    return txnDaoManager.getWriter().getSupplierInventoryList(shopId, supplierId, storehouseId, productIds);
  }

  @Override
  public List<SupplierDTO> getCommonSupplierByProductIds(Long shopId, Long[] productIds) {
    TxnWriter writer = txnDaoManager.getWriter();
    List<SupplierInventoryDTO> supplierInventoryDTOs = writer.getLastProductSupplierByProductIds(shopId, new HashSet<Long>(Arrays.asList(productIds)));
    List<SupplierDTO> supplierDTOList = null;
    if (CollectionUtils.isNotEmpty(supplierInventoryDTOs)) {
      supplierDTOList = new ArrayList<SupplierDTO>();
      ISupplierService supplierService = ServiceManager.getService(SupplierService.class);
      for (SupplierInventoryDTO supplierInventoryDTO : supplierInventoryDTOs) {
        if (supplierInventoryDTO.getSupplierId() != null) {
          SupplierDTO supplierDTO = supplierService.getSupplierById(supplierInventoryDTO.getSupplierId(),shopId);
          if (supplierDTO != null && !CustomerStatus.DISABLED.equals(supplierDTO.getStatus())) ;
          supplierDTOList.add(supplierDTO);
        }
      }
    }
    return supplierDTOList;
  }

  @Override
  public Map<Long, List<SupplierInventoryDTO>> getSupplierInventoryMap(Long shopId, Set<Long> productIdSet) {
    Map<Long, List<SupplierInventoryDTO>> supplierInventoryMap = new HashMap<Long, List<SupplierInventoryDTO>>();
    if (shopId == null || CollectionUtil.isEmpty(productIdSet)) {
      return supplierInventoryMap;
    }
    List<SupplierInventory> supplierInventories = txnDaoManager.getWriter().getSupplierInventoriesByProductIds(shopId, productIdSet);
    if (CollectionUtil.isNotEmpty(supplierInventories)) {
      for (SupplierInventory supplierInventory : supplierInventories) {
        if (supplierInventory.getProductId() != null) {
          List<SupplierInventoryDTO> supplierInventoryDTOs = supplierInventoryMap.get(supplierInventory.getProductId());
          if (supplierInventoryDTOs==null) {
            supplierInventoryDTOs = new ArrayList<SupplierInventoryDTO>();
            supplierInventory.setStorehouseId(null);
            supplierInventoryDTOs.add(supplierInventory.toDTO());
            supplierInventoryMap.put(supplierInventory.getProductId(), supplierInventoryDTOs);
          } else {
            boolean hasSameSupplierProduct = false;
            for (SupplierInventoryDTO supplierInventoryDTO : supplierInventoryDTOs) {
              //存在的供应商；未指定供应商、商品导入供应商；不同仓库数据合并
              if (supplierInventoryDTO.getSupplierId() != null && supplierInventoryDTO.getSupplierId().equals(supplierInventory.getSupplierId())
                  || supplierInventoryDTO.getSupplierId() == null && supplierInventory.getSupplierId() == null) {
                if (supplierInventoryDTO.getSupplierId() == null && supplierInventory.getSupplierId() == null) {
                  supplierInventoryDTO.setSupplierType(OutStorageSupplierType.UNDEFINED_SUPPLIER);
                }
                hasSameSupplierProduct = true;
                buildSupplierInventoryDTO(supplierInventoryDTO,supplierInventory);
                break;
              }
            }
            if (!hasSameSupplierProduct) {
              supplierInventory.setStorehouseId(null);
              supplierInventoryDTOs.add(supplierInventory.toDTO());
            }
          }
        }

      }
    }
    return supplierInventoryMap;
  }

  @Override
  public Map<Long, List<SupplierInventoryDTO>> getSimpleSupplierInventoryMap(Long shopId, Set<Long> productIdSet) {
    Map<Long, List<SupplierInventoryDTO>> supplierInventoryMap = new HashMap<Long, List<SupplierInventoryDTO>>();
    if (shopId == null || CollectionUtil.isEmpty(productIdSet)) {
      return supplierInventoryMap;
    }
    List<SupplierInventory> supplierInventories = txnDaoManager.getWriter().getSupplierInventoriesByProductIds(shopId, productIdSet);
    if (CollectionUtil.isNotEmpty(supplierInventories)) {
      for (SupplierInventory supplierInventory : supplierInventories) {
        if (supplierInventory.getProductId() != null) {
          List<SupplierInventoryDTO> supplierInventoryDTOs = supplierInventoryMap.get(supplierInventory.getProductId());
          if (supplierInventoryDTOs==null) {
            supplierInventoryDTOs = new ArrayList<SupplierInventoryDTO>();
            supplierInventoryDTOs.add(supplierInventory.toDTO());
            supplierInventoryMap.put(supplierInventory.getProductId(), supplierInventoryDTOs);
          } else {
            boolean hasSameSupplierProduct = false;
            for (SupplierInventoryDTO supplierInventoryDTO : supplierInventoryDTOs) {
              //存在的供应商；未指定供应商、商品导入供应商；不同仓库数据合并
              if (NumberUtil.longValue(supplierInventoryDTO.getSupplierId()) == NumberUtil.longValue(supplierInventory.getSupplierId())) {
                hasSameSupplierProduct = true;
                //最后入库信息取最新的
                if (NumberUtil.longValue(supplierInventoryDTO.getLastStorageTime())<NumberUtil.longValue(supplierInventory.getLastStorageTime())) {
                  supplierInventoryDTO.setLastStorageTime(supplierInventory.getLastStorageTime());
                  supplierInventoryDTO.setSupplierName(supplierInventory.getSupplierName());
                  supplierInventoryDTO.setSupplierContact(supplierInventory.getSupplierContact());
                  supplierInventoryDTO.setSupplierMobile(supplierInventory.getSupplierMobile());
                }
                break;
              }
            }
            if (!hasSameSupplierProduct) {
              supplierInventoryDTOs.add(supplierInventory.toDTO());
              supplierInventoryMap.put(supplierInventory.getProductId(), supplierInventoryDTOs);
            }
          }
        }

      }
    }
    return supplierInventoryMap;
  }

  public List<SupplierInventory> getSupplierAllInventory(Long shopId, Long supplierId) {
    if (shopId == null || supplierId == null) {
      return null;
    }
    return txnDaoManager.getWriter().getSupplierAllInventory(shopId, supplierId);
  }


  /**
   * 根据产品id和供应商id获得该产品所有入库itemDTO
   * 若供应商id为空 则表示所有供应商
   *
   * @param shopId
   * @param productIdMap
   * @return
   */
  public Map<Long, List<ItemIndexDTO>> getInventoryItemIndexDTOByProductId(Long shopId, Map<Long, Set<Long>> productIdMap) {
    Map<Long, List<ItemIndexDTO>> productMap = new HashMap<Long, List<ItemIndexDTO>>();

    if (MapUtils.isEmpty(productIdMap)) {
      return productMap;
    }
    Set<Long> productIdSet = productIdMap.keySet();
    if (CollectionUtil.isEmpty(productIdSet)) {
      return productMap;
    }

    Set<Long> supplierIdSet = null;

    List<ItemIndexDTO> itemIndexDTOList = getItemIndexService().getInventoryItemIndexDTOByProductIds(shopId, productIdSet);

    if (CollectionUtil.isEmpty(itemIndexDTOList)) {
      return productMap;
    }

    for (ItemIndexDTO itemIndexDTO : itemIndexDTOList) {
      if (itemIndexDTO == null || itemIndexDTO.getProductId() == null || itemIndexDTO.getCustomerId() == null) {
        continue;
      }

      supplierIdSet = productIdMap.get(itemIndexDTO.getProductId());

      if (CollectionUtils.isNotEmpty(supplierIdSet) && !supplierIdSet.contains(itemIndexDTO.getCustomerId())) {
        continue;
      }

      List<ItemIndexDTO> indexDTOList = null;

      if (productMap.containsKey(itemIndexDTO.getProductId())) {
        indexDTOList = productMap.get(itemIndexDTO.getProductId());
      } else {
        indexDTOList = new ArrayList<ItemIndexDTO>();
      }
      indexDTOList.add(itemIndexDTO);
      productMap.put(itemIndexDTO.getProductId(), indexDTOList);
    }
    return productMap;

  }

  /**
   * 获得每个单据的商品id和该商品选择的供应商supplierSet 并获得  outStorageRelationDTO.OutStorageType类型
   *
   * @param bcgogoOrderDto
   * @return
   */
  public Map<Long, Set<Long>> getProductIdMapByOrder(BcgogoOrderDto bcgogoOrderDto, OutStorageRelationDTO outStorageRelationDTO) {

    Long productId = null;
    boolean selectSupplier = bcgogoOrderDto.isSelectSupplier(); //是否是选择供应商版本 如果不是 supplierSet为空
    Set<Long> supplierSet = new HashSet<Long>(); //每个商品选择的供应商
    OutStorageRelationDTO[] outStorageRelationDTOs = null;//每个商品的出库记录
    Map<Long, Set<Long>> productIdMap = new HashMap<Long, Set<Long>>(); //商品id和选择的供应商supplierSet

    for (BcgogoOrderItemDto bcgogoOrderItemDto : bcgogoOrderDto.getItemDTOs()) {
      if (bcgogoOrderItemDto.getProductId() == null) {
        continue;
      }
      productId = bcgogoOrderItemDto.getProductId();

      if (!selectSupplier) {
        outStorageRelationDTO.setOutStorageType(OutStorageType.SYSTEM_ASSIGN);
        productIdMap.put(productId, supplierSet);
        continue;
      }

      outStorageRelationDTO.setOutStorageType(OutStorageType.SELF_ASSIGN);
      outStorageRelationDTOs = bcgogoOrderItemDto.getOutStorageRelationDTOs();

      if (ArrayUtils.isEmpty(outStorageRelationDTOs)) {
        LOG.error("orderItem.outStorageRelationDTOs is empty:itemId:" + bcgogoOrderItemDto.getId());
        productIdMap.put(productId, supplierSet);
        continue;
      }

      for (OutStorageRelationDTO relationDTO : outStorageRelationDTOs) {

        if (!NumberUtil.isNumber(relationDTO.getOutStorageUnit())) {
          LOG.error("orderItem.outStorageRelationDTOs.relatedSupplierIdStr is error:relatedSupplierIdStr:" + relationDTO.getOutStorageUnit());
          productIdMap.put(productId, supplierSet);
          continue;
        }
        relationDTO.setRelatedSupplierId(Long.valueOf(relationDTO.getOutStorageUnit()));
        supplierSet.add(relationDTO.getRelatedSupplierId());
      }
      productIdMap.put(productId, supplierSet);
    }
    return productIdMap;
  }


  /**
   * 商品出入库打通-根据单据信息更新商品剩余库存，并保存商品出入库关系记录
   * 接受单据：销售单结算
   * @param bcgogoOrderDto
   * @return
   */
  public Result productThroughByOrder(BcgogoOrderDto bcgogoOrderDto, OrderTypes orderType, TxnWriter txnWriter) {
    Result result = new Result();
    result.setSuccess(false);

    try {
      if (bcgogoOrderDto == null || ArrayUtil.isEmpty(bcgogoOrderDto.getItemDTOs())) {
        result.setSuccess(true);
        return result;
      }

      OutStorageRelationDTO outStorageRelationDTO = new OutStorageRelationDTO();
      outStorageRelationDTO.setShopId(bcgogoOrderDto.getShopId());
      outStorageRelationDTO.setOutStorageOrderId(bcgogoOrderDto.getId());
      outStorageRelationDTO.setOutStorageOrderType(orderType);

      //获得每个商品id和该商品所选则供应商
      Map<Long, Set<Long>> productIdMap = this.getProductIdMapByOrder(bcgogoOrderDto, outStorageRelationDTO);

      Set<Long> productIdSet = productIdMap.keySet();
      Map<Long, ProductLocalInfoDTO> productLocalInfoDTOMap = getProductService().getProductLocalInfoMap(bcgogoOrderDto.getShopId(), productIdSet.toArray(new Long[productIdSet.size()]));


      Map<Long, List<ItemIndexDTO>> productMap = this.getInventoryItemIndexDTOByProductId(bcgogoOrderDto.getShopId(), productIdMap);

      List<ItemIndexDTO> itemIndexDTOList = null;
      Long productId = null;
      Double amount = 0D;
      boolean selectSupplier = bcgogoOrderDto.isSelectSupplier();//是否选择供应商
      for (BcgogoOrderItemDto orderItemDto : bcgogoOrderDto.getItemDTOs()) {
        productId = orderItemDto.getProductId();
        itemIndexDTOList = productMap.get(productId);
        amount = NumberUtil.doubleVal(orderItemDto.getAmount());

        outStorageRelationDTO.setOutStorageItemId(orderItemDto.getId());
        outStorageRelationDTO.setProductId(productId);
        outStorageRelationDTO.setOutStorageItemAmount(amount);
        outStorageRelationDTO.setOutStorageUnit(orderItemDto.getUnit());
        outStorageRelationDTO.setRelationTime(System.currentTimeMillis());

        if (CollectionUtil.isEmpty(itemIndexDTOList)) {
          outStorageRelationDTO.setOutStorageType(OutStorageType.SYSTEM_ASSIGN);
          handleNoProductInventoryAddRecord(outStorageRelationDTO, txnWriter);
        } else {

          if (selectSupplier) {
            outStorageRelationDTO.setOutStorageType(OutStorageType.SELF_ASSIGN);
            handleSelectSupplier(outStorageRelationDTO, productLocalInfoDTOMap.get(productId), orderItemDto, itemIndexDTOList, txnWriter);
          } else {
            outStorageRelationDTO.setOutStorageType(OutStorageType.SYSTEM_ASSIGN);
            handleProductInventoryAddRecord(outStorageRelationDTO, productLocalInfoDTOMap.get(productId), amount, itemIndexDTOList, txnWriter);
          }

        }
      }
    } catch (Exception e) {
      LOG.error("ProductThroughService.handleProductRemainByOrder,orderType:" + orderType + ",bcgogoOrderDto" + JsonUtil.objectToJson(bcgogoOrderDto));
    }

    return result;
  }


  public void handleSelectSupplier(OutStorageRelationDTO outStorageRelationDTO, ProductLocalInfoDTO productLocalInfoDTO, BcgogoOrderItemDto bcgogoOrderItemDto, List<ItemIndexDTO> itemIndexDTOList, TxnWriter txnWriter) {

    OutStorageRelationDTO[] outStorageRelationDTOs = bcgogoOrderItemDto.getOutStorageRelationDTOs();
    if (ArrayUtil.isEmpty(outStorageRelationDTOs)) {
      return;
    }

    Map<Long, List<ItemIndexDTO>> supplierMap = new HashMap<Long, List<ItemIndexDTO>>();
    List<ItemIndexDTO> indexDTOList = null;
    for (ItemIndexDTO itemIndexDTO : itemIndexDTOList) {
      if (supplierMap.containsKey(itemIndexDTO.getCustomerId())) {
        indexDTOList = supplierMap.get(itemIndexDTO.getCustomerId());
      } else {
        indexDTOList = new ArrayList<ItemIndexDTO>();
      }
      indexDTOList.add(itemIndexDTO);
      supplierMap.put(itemIndexDTO.getCustomerId(), indexDTOList);
    }

    Long relatedSupplierId = null;
    double useRelatedAmount = 0;
    for (OutStorageRelationDTO relationDTO : outStorageRelationDTOs) {
      relatedSupplierId = relationDTO.getRelatedSupplierId();
      useRelatedAmount = NumberUtil.doubleVal(relationDTO.getUseRelatedAmount());
      indexDTOList = supplierMap.get(relatedSupplierId);


      if (CollectionUtil.isEmpty(indexDTOList)) {
        handleNoProductInventoryAddRecord(outStorageRelationDTO, txnWriter);
      } else {
        handleProductInventoryAddRecord(outStorageRelationDTO, productLocalInfoDTO, useRelatedAmount, indexDTOList, txnWriter);
      }
    }
  }


  /**
   * 当单据商品没有库存增加记录时。调用该方法
   *
   * @param outStorageRelationDTO
   */
  public void handleNoProductInventoryAddRecord(OutStorageRelationDTO outStorageRelationDTO, TxnWriter txnWriter) {
    try {
      OutStorageRelation outStorageRelation = new OutStorageRelation();
      outStorageRelation.fromDTO(outStorageRelationDTO);
      outStorageRelation.setUseRelatedAmount(NumberUtil.round(outStorageRelationDTO.getOutStorageItemAmount(),1));
      outStorageRelation.setRelatedOrderType(null);
      outStorageRelation.setRelatedOrderId(null);
      outStorageRelation.setRelatedItemId(null);
      outStorageRelation.setRelatedSupplierId(null);
      outStorageRelation.setOutStorageType(OutStorageType.SELF_ASSIGN);
      outStorageRelation.setSupplierType(OutStorageSupplierType.UNDEFINED_SUPPLIER);
      txnWriter.save(outStorageRelation);
    } catch (Exception e) {
      LOG.error("ProductThroughService.handleNoProductInventoryAddRecord" + ",outStorageRelationDTO:" + outStorageRelationDTO.toString());
      LOG.error(e.getMessage(), e);

    }

  }

  public List<SupplierInventory> getSupplierInventory(SupplierInventoryDTO condition){
    if(condition.getShopId()==null) return null;
    TxnWriter writer=txnDaoManager.getWriter();
    return writer.getSupplierInventory(condition);
  }

  @Override
  public List<SupplierInventoryDTO> getSupplierInventoryDTOsWithOtherStorehouse(Long shopId, Long productId, Long storehouseId) throws Exception {
    List<SupplierInventoryDTO> supplierInventoryDTOs = new ArrayList<SupplierInventoryDTO>();
    if (shopId == null || productId == null) {
      return supplierInventoryDTOs;
    }
    Set<Long> productIds = new HashSet<Long>();
    productIds.add(productId);
    Map<Long, List<SupplierInventoryDTO>> supplierInventoryDTOMap = getSupplierInventoryDTOsWithOtherStorehouseMap(shopId, productIds, storehouseId);
    supplierInventoryDTOs = supplierInventoryDTOMap.get(productId);
    if (supplierInventoryDTOs == null) {
      supplierInventoryDTOs = new ArrayList<SupplierInventoryDTO>();
    }
    return supplierInventoryDTOs;
  }

  @Override
  public Map<Long, List<SupplierInventoryDTO>> getSupplierInventoryDTOsWithOtherStorehouseMap(Long shopId, Set<Long> productIds, Long storehouseId) {
    IInventoryService inventoryService = ServiceManager.getService(IInventoryService.class);
    Map<Long, List<SupplierInventoryDTO>> supplierInventoryDTOMap = new HashMap<Long, List<SupplierInventoryDTO>>();
    if (shopId == null || CollectionUtils.isEmpty(productIds)) {
      return supplierInventoryDTOMap;
    }
    List<SupplierInventory> supplierInventories = txnDaoManager.getWriter().getSupplierInventoriesByProductIds(shopId, productIds);
    //外面的key是productId，里面的key是supplierId
    Map<Long, Map<Long, SupplierInventoryDTO>> allDefinedSupplierInventoryMap = new HashMap<Long, Map<Long, SupplierInventoryDTO>>();
    //外面的key是productId，里面的key是supplierType
    Map<Long, Map<OutStorageSupplierType, SupplierInventoryDTO>> allUndefinedSupplierInventoryMap = new HashMap<Long, Map<OutStorageSupplierType, SupplierInventoryDTO>>();

    if (CollectionUtils.isNotEmpty(supplierInventories)) {
      //添加相同仓库的供应商信息
      for (SupplierInventory supplierInventory : supplierInventories) {
        if (storehouseId != null) {
          if (storehouseId.equals(supplierInventory.getStorehouseId())) {
            SupplierInventoryDTO supplierInventoryDTO = supplierInventory.toDTO();
            List<SupplierInventoryDTO> supplierInventoryDTOList = supplierInventoryDTOMap.get(supplierInventory.getProductId());
            if (supplierInventoryDTOList == null) {
              supplierInventoryDTOList = new ArrayList<SupplierInventoryDTO>();
            }
            supplierInventoryDTOList.add(supplierInventoryDTO);
            supplierInventoryDTOMap.put(supplierInventory.getProductId(), supplierInventoryDTOList);
            if (supplierInventory.getSupplierId() != null) {
              Map<Long, SupplierInventoryDTO> definedSupplierInventoryMap = allDefinedSupplierInventoryMap.get(supplierInventory.getProductId());
              if (definedSupplierInventoryMap == null) {
                definedSupplierInventoryMap = new HashMap<Long, SupplierInventoryDTO>();
              }
              definedSupplierInventoryMap.put(supplierInventory.getSupplierId(), supplierInventoryDTO);
              allDefinedSupplierInventoryMap.put(supplierInventory.getProductId(), definedSupplierInventoryMap);
            } else {
              Map<OutStorageSupplierType, SupplierInventoryDTO> undefinedSupplierInventoryMap = allUndefinedSupplierInventoryMap.get(supplierInventory.getProductId());
              if (undefinedSupplierInventoryMap == null) {
                undefinedSupplierInventoryMap = new HashMap<OutStorageSupplierType, SupplierInventoryDTO>();
              }
              undefinedSupplierInventoryMap.put(supplierInventory.getSupplierType(), supplierInventoryDTO);
              allUndefinedSupplierInventoryMap.put(supplierInventory.getProductId(), undefinedSupplierInventoryMap);
            }
          }
        }
      }
      //添加不同仓库的供应商信息
      for (SupplierInventory supplierInventory : supplierInventories) {
        Map<Long, SupplierInventoryDTO> definedSupplierInventoryMap = allDefinedSupplierInventoryMap.get(supplierInventory.getProductId());
        Map<OutStorageSupplierType, SupplierInventoryDTO> undefinedSupplierInventoryMap = allUndefinedSupplierInventoryMap.get(supplierInventory.getProductId());
        List<SupplierInventoryDTO> supplierInventoryDTOs = supplierInventoryDTOMap.get(supplierInventory.getProductId());
        if (supplierInventoryDTOs == null) {
          supplierInventoryDTOs = new ArrayList<SupplierInventoryDTO>();
        }
        if (definedSupplierInventoryMap == null) {
          definedSupplierInventoryMap = new HashMap<Long, SupplierInventoryDTO>();
        }
        if (undefinedSupplierInventoryMap == null) {
          undefinedSupplierInventoryMap = new HashMap<OutStorageSupplierType, SupplierInventoryDTO>();
        }
        if (supplierInventory.getSupplierId() != null) {
          if (definedSupplierInventoryMap.get(supplierInventory.getSupplierId()) == null) {
            SupplierInventoryDTO supplierInventoryDTO = supplierInventory.toDTO();
            if (storehouseId != null) {
              supplierInventoryDTO.setRemainAmount(0d);
            }
            supplierInventoryDTOs.add(supplierInventoryDTO);
            supplierInventoryDTOMap.put(supplierInventory.getProductId(), supplierInventoryDTOs);
            definedSupplierInventoryMap.put(supplierInventory.getSupplierId(), supplierInventoryDTO);
            allDefinedSupplierInventoryMap.put(supplierInventory.getProductId(), definedSupplierInventoryMap);
          }
        } else {
          if (undefinedSupplierInventoryMap.get(supplierInventory.getSupplierType()) == null) {
            SupplierInventoryDTO supplierInventoryDTO = supplierInventory.toDTO();
            if (storehouseId != null) {
              supplierInventoryDTO.setRemainAmount(0d);
            }
            supplierInventoryDTOs.add(supplierInventoryDTO);
            supplierInventoryDTOMap.put(supplierInventory.getProductId(), supplierInventoryDTOs);
            undefinedSupplierInventoryMap.put(supplierInventory.getSupplierType(), supplierInventoryDTO);
            allUndefinedSupplierInventoryMap.put(supplierInventory.getProductId(), undefinedSupplierInventoryMap);
          }
        }
      }
    }
    Set<Long> noSupplierInventoryProductIds = new HashSet<Long>();
    for (Long productId : productIds) {
      Map<Long, SupplierInventoryDTO> definedSupplierInventoryMap = allDefinedSupplierInventoryMap.get(productId);
      Map<OutStorageSupplierType, SupplierInventoryDTO> undefinedSupplierInventoryMap = allUndefinedSupplierInventoryMap.get(productId);
      if (MapUtils.isEmpty(definedSupplierInventoryMap) && MapUtils.isEmpty(undefinedSupplierInventoryMap)) {
        noSupplierInventoryProductIds.add(productId);
      }
    }
    if (CollectionUtil.isNotEmpty(noSupplierInventoryProductIds)) {
      Map<Long, InventoryDTO> inventoryDTOMap = inventoryService.getInventoryDTOMap(shopId, noSupplierInventoryProductIds);
      for (Long productId : noSupplierInventoryProductIds) {
        List<SupplierInventoryDTO> supplierInventoryDTOs = supplierInventoryDTOMap.get(productId);
        if (supplierInventoryDTOs == null) {
          supplierInventoryDTOs = new ArrayList<SupplierInventoryDTO>();
        }
        if (CollectionUtil.isEmpty(supplierInventoryDTOs)) {
          SupplierInventoryDTO supplierInventoryDTO = new SupplierInventoryDTO();
          supplierInventoryDTO.setShopId(shopId);
          supplierInventoryDTO.setStorehouseId(storehouseId);
          supplierInventoryDTO.setProductId(productId);
          supplierInventoryDTO.setSupplierType(OutStorageSupplierType.UNDEFINED_SUPPLIER);
          supplierInventoryDTO.setSupplierName(OutStorageSupplierType.UNDEFINED_SUPPLIER.getName());
          InventoryDTO inventoryDTO = inventoryDTOMap.get(productId);
          if (inventoryDTO != null) {
            supplierInventoryDTO.setUnit(inventoryDTO.getUnit());
            supplierInventoryDTO.setAverageStoragePrice(inventoryDTO.getInventoryAveragePrice());
          }
          if (supplierInventoryDTOs == null) {
            supplierInventoryDTOs = new ArrayList<SupplierInventoryDTO>();
          }
          supplierInventoryDTOs.add(supplierInventoryDTO);
          supplierInventoryDTOMap.put(productId, supplierInventoryDTOs);
        }
      }
    }
    return supplierInventoryDTOMap;
  }

  @Override
  public Map<Long, List<SupplierInventoryDTO>> getSupplierInventoryDTOByStorehouse(SupplierInventoryDTO condition) {
    if (condition.getShopId() == null || ArrayUtil.isEmpty(condition.getProductIds())) {
      return null;
    }
    TxnWriter writer = txnDaoManager.getWriter();
    List<SupplierInventory> supplierInventoryList = new ArrayList<SupplierInventory>();
    if (OrderTypes.INVENTORY_CHECK.equals(condition.getOrderType())) {
      Long storehouseId = condition.getStorehouseId();
      condition.setStorehouseId(null);
      supplierInventoryList = writer.getSupplierInventory(condition);
      condition.setStorehouseId(storehouseId);
    } else {
      supplierInventoryList = writer.getSupplierInventory(condition);
    }

    Map<Long, List<SupplierInventoryDTO>> supplierInventoryMap = new HashMap<Long, List<SupplierInventoryDTO>>();
    if (CollectionUtil.isNotEmpty(supplierInventoryList)) {
      for (SupplierInventory supplierInventory : supplierInventoryList) {
        List<SupplierInventoryDTO> supplierInventories = supplierInventoryMap.get(supplierInventory.getProductId());
        if (CollectionUtil.isEmpty(supplierInventories)) {
          supplierInventories = new ArrayList<SupplierInventoryDTO>();
          supplierInventories.add(supplierInventory.toDTO());
          supplierInventoryMap.put(supplierInventory.getProductId(), supplierInventories);
        } else {
          supplierInventoryMap.get(supplierInventory.getProductId()).add(supplierInventory.toDTO());
        }
      }
    }
    return supplierInventoryMap;
  }

  /**
   * 根据productId,unit amount 更新item的剩余库存
   *
   * @param outStorageRelationDTO
   * @param txnWriter
   */
  public void handleProductInventoryAddRecord(OutStorageRelationDTO outStorageRelationDTO, ProductLocalInfoDTO productLocalInfoDTO, Double amount, List<ItemIndexDTO> itemIndexDTOList, TxnWriter txnWriter) {

    for (ItemIndexDTO itemIndexDTO : itemIndexDTOList) {
      outStorageRelationDTO.setRelatedSupplierId(itemIndexDTO.getCustomerId());
      outStorageRelationDTO.setRelatedItemId(itemIndexDTO.getItemId());
      outStorageRelationDTO.setRelatedOrderId(itemIndexDTO.getOrderId());
      outStorageRelationDTO.setRelatedOrderType(itemIndexDTO.getOrderType());

      if (amount > 0) {
        updateRemainAmountOfItem(amount, productLocalInfoDTO, outStorageRelationDTO, txnWriter);
      }
    }

    if (amount > 0) {
      handleNoProductInventoryAddRecord(outStorageRelationDTO, txnWriter);
    }
  }


  public Double updateRemainAmountOfItem(double amount,ProductLocalInfoDTO productLocalInfoDTO, OutStorageRelationDTO outStorageRelationDTO, TxnWriter txnWriter) {
    OrderTypes relatedOrderType = outStorageRelationDTO.getRelatedOrderType();

    double remainAmount = 0;//每个item剩余的库存
    String relatedItemUnit = null;


    PurchaseInventoryItem purchaseInventoryItem = null;
    InnerReturnItem innerReturnItem = null;
    InventoryCheckItem inventoryCheckItem = null;

    if (relatedOrderType == OrderTypes.INVENTORY) {
      purchaseInventoryItem = txnWriter.getById(PurchaseInventoryItem.class, outStorageRelationDTO.getRelatedItemId());
      relatedItemUnit = purchaseInventoryItem.getUnit();

    } else if (relatedOrderType == OrderTypes.INNER_RETURN) {
      innerReturnItem = txnWriter.getById(InnerReturnItem.class, outStorageRelationDTO.getRelatedItemId());
      relatedItemUnit = innerReturnItem.getUnit();
    } else if (relatedOrderType == OrderTypes.INVENTORY_CHECK) {
      inventoryCheckItem = txnWriter.getById(InventoryCheckItem.class, outStorageRelationDTO.getRelatedItemId());
      relatedItemUnit = inventoryCheckItem.getUnit();
    } else {
      return amount;
    }


    if (remainAmount <= 0) {
      return amount;
    }

    boolean multiply = false; //原来单据的数量是否做了乘法
    boolean division = false; //原来单据的数量是否做了除法

    if (UnitUtil.isStorageUnit(relatedItemUnit, productLocalInfoDTO)) {
      if (!UnitUtil.isStorageUnit(outStorageRelationDTO.getOutStorageUnit(), productLocalInfoDTO)) {
        amount = amount / productLocalInfoDTO.getRate();
        division = true;
      }
    } else {
      if (UnitUtil.isStorageUnit(outStorageRelationDTO.getOutStorageUnit(), productLocalInfoDTO)) {
        amount = amount * productLocalInfoDTO.getRate();
        multiply = true;
      }
    }

    double useRelatedAmount = 0;

    if (remainAmount < amount) {
      useRelatedAmount = remainAmount;
      remainAmount = 0;
      amount = amount - remainAmount;
    } else {
      useRelatedAmount = amount;
      remainAmount = remainAmount - amount;
      amount = 0;
    }

    OutStorageRelation outStorageRelation = new OutStorageRelation();
    outStorageRelation.fromDTO(outStorageRelationDTO);

    outStorageRelation.setUseRelatedAmount(NumberUtil.round(useRelatedAmount,1));
    outStorageRelation.setSupplierType(OutStorageSupplierType.NATIVE_SUPPLIER);
    txnWriter.save(outStorageRelation);

    if (relatedOrderType == OrderTypes.INVENTORY) {
      txnWriter.update(purchaseInventoryItem);

    } else if (relatedOrderType == OrderTypes.INNER_RETURN) {
      txnWriter.update(purchaseInventoryItem);
    } else if (relatedOrderType == OrderTypes.INVENTORY_CHECK) {
      txnWriter.update(purchaseInventoryItem);
    }

    if (multiply) {
      amount = amount / productLocalInfoDTO.getRate();
    } else if (division) {
      amount = amount / productLocalInfoDTO.getRate();
    }
    return amount;
  }



  /**
   * 销售退货单库存单打通
   *
   * @param bcgogoOrderDto
   */
  public Map<Long, BcgogoOrderItemDto> salesReturnProductThrough(BcgogoOrderDto bcgogoOrderDto, OrderTypes orderType) throws Exception {

    Map<Long, BcgogoOrderItemDto> bcgogoOrderItemDtoMap = new HashMap<Long, BcgogoOrderItemDto>();
    BcgogoOrderItemDto[] bcgogoOrderItemDtos = null;
    BcgogoOrderDto orderDto = null;


    switch (orderType) {
      case SALE_RETURN:
        SalesReturnDTO salesReturnDTO = (SalesReturnDTO) bcgogoOrderDto;
        Long originOrderId = salesReturnDTO.getOriginOrderId();
        OrderTypes originOrderType = salesReturnDTO.getOriginOrderType();

        if (originOrderType == OrderTypes.SALE) {
          orderDto = getTxnService().getSalesOrder(originOrderId);
        } else if (originOrderType == OrderTypes.REPAIR) {
          orderDto = getTxnService().getRepairOrder(originOrderId);
        }
        break;
      case SALE:
        orderDto = getTxnService().getSalesOrder(bcgogoOrderDto.getId());
        break;
      default:
    }

    if (orderDto != null && !ArrayUtil.isEmpty(orderDto.getItemDTOs())) {
      for (BcgogoOrderItemDto bcgogoOrderItemDto : orderDto.getItemDTOs()) {
        bcgogoOrderItemDtoMap.put(bcgogoOrderItemDto.getProductId(), bcgogoOrderItemDto);
      }
    }
    return bcgogoOrderItemDtoMap;
  }


  /**
   * 根据shopId,单据id和单据类型 itemId productId获得商品出入库关系表
   *
   * @param shopId
   * @param outStorageOrderId
   * @param outStorageOrderType
   * @param outStorageItemId
   * @param productId
   * @return
   */
  public List<OutStorageRelationDTO> getOutStorageRelation(Long shopId, Long outStorageOrderId, OrderTypes outStorageOrderType, Long outStorageItemId, Long productId) {
    TxnWriter writer = txnDaoManager.getWriter();
    List<OutStorageRelationDTO> outStorageRelationDTOList = new ArrayList<OutStorageRelationDTO>();

    List<OutStorageRelation> outStorageRelationList = writer.getOutStorageRelation(shopId, outStorageOrderId, outStorageOrderType, outStorageItemId, productId);
    if (CollectionUtil.isEmpty(outStorageRelationList)) {
      return outStorageRelationDTOList;
    }
    for (OutStorageRelation outStorageRelation : outStorageRelationList) {
      outStorageRelationDTOList.add(outStorageRelation.toDTO());
    }
    return outStorageRelationDTOList;
  }

 /**
   * 根据shopId,单据id 获得商品出入库关系表   key为orderItemId
   *
   * @param shopId
   * @param outStorageOrderId
   * @return itemId OutList
   */
  @Override
  public Map<Long,List<OutStorageRelationDTO>> getOutStorageRelationMap(Long shopId, Long... outStorageOrderId) {
    TxnWriter writer = txnDaoManager.getWriter();
    Map<Long,List<OutStorageRelationDTO>> outStorageRelationDTOMap = new HashMap<Long, List<OutStorageRelationDTO>>();
    Map<Long,Map<String,OutStorageRelationDTO>> outStorageRelationDTOTempMapMap = new HashMap<Long, Map<String,OutStorageRelationDTO>>();
    Map<String,OutStorageRelationDTO> outStorageRelationDTOTempMap = null;
    List<OutStorageRelation> outStorageRelationList = writer.getOutStorageRelationByOrderIds(shopId, outStorageOrderId);

    if(CollectionUtils.isNotEmpty(outStorageRelationList)){
      for (OutStorageRelation outStorageRelation : outStorageRelationList) {
        outStorageRelationDTOTempMap = outStorageRelationDTOTempMapMap.get(outStorageRelation.getOutStorageItemId());
        if(outStorageRelationDTOTempMap == null){
          outStorageRelationDTOTempMap = new HashMap<String, OutStorageRelationDTO>();
        }
        String key = ObjectUtil.generateKey(outStorageRelation.getSupplierType(),outStorageRelation.getRelatedSupplierId());
        if(!outStorageRelationDTOTempMap.containsKey(key)){
          outStorageRelationDTOTempMap.put(key,outStorageRelation.toDTO());
        }
        outStorageRelationDTOTempMapMap.put(outStorageRelation.getOutStorageItemId(),outStorageRelationDTOTempMap);
      }

      for (Map.Entry<Long,Map<String,OutStorageRelationDTO>> entry : outStorageRelationDTOTempMapMap.entrySet()) {
        outStorageRelationDTOMap.put(entry.getKey(),new ArrayList<OutStorageRelationDTO>(entry.getValue().values()));
      }
    }

    return outStorageRelationDTOMap;
  }


  @Override
  public Map<Long, List<InStorageRecordDTO>> getInStorageRecordDTOMapByOrderIds(Long shopId, Long... orderId) {
    TxnWriter writer = txnDaoManager.getWriter();
    Map<Long,List<InStorageRecordDTO>> inStorageRelationDTOMap = new HashMap<Long, List<InStorageRecordDTO>>();
    Map<Long,Map<String,InStorageRecordDTO>> inStorageRelationDTOTempMapMap = new HashMap<Long, Map<String,InStorageRecordDTO>>();
    Map<String,InStorageRecordDTO> inStorageRelationDTOTempMap = null;
    List<InStorageRecord> inStorageRecordList = writer.getInStorageRecordByOrderIds(shopId, orderId);
    if(CollectionUtils.isNotEmpty(inStorageRecordList)){
      for (InStorageRecord inStorageRecord : inStorageRecordList) {
        inStorageRelationDTOTempMap = inStorageRelationDTOTempMapMap.get(inStorageRecord.getInStorageItemId());
        if(inStorageRelationDTOTempMap == null){
          inStorageRelationDTOTempMap = new HashMap<String, InStorageRecordDTO>();
        }
        String key = ObjectUtil.generateKey(inStorageRecord.getSupplierType(),inStorageRecord.getSupplierId());
        if(!inStorageRelationDTOTempMap.containsKey(key)){
          inStorageRelationDTOTempMap.put(key,inStorageRecord.toDTO());
        }
        inStorageRelationDTOTempMapMap.put(inStorageRecord.getInStorageItemId(),inStorageRelationDTOTempMap);
      }
      for (Map.Entry<Long,Map<String,InStorageRecordDTO>> entry : inStorageRelationDTOTempMapMap.entrySet()) {
        inStorageRelationDTOMap.put(entry.getKey(),new ArrayList<InStorageRecordDTO>(entry.getValue().values()));
      }
    }

    return inStorageRelationDTOMap;
  }

  public Map<Long,List<OutStorageRelation>> getOutStorageRelationProductMap(Long shopId, Long outStorageOrderId) {
    TxnWriter writer = txnDaoManager.getWriter();
    Map<Long,List<OutStorageRelation>> outStorageRelationDTOMap = new HashMap<Long, List<OutStorageRelation>>();
    Map<Long,Map<String,OutStorageRelation>> outStorageRelationDTOTempMapMap = new HashMap<Long, Map<String,OutStorageRelation>>();
    Map<String,OutStorageRelation> outStorageRelationDTOTempMap = null;
    List<OutStorageRelation> outStorageRelationList = writer.getOutStorageRelationByOrderIds(shopId, outStorageOrderId);
    if(CollectionUtils.isNotEmpty(outStorageRelationList)){
      for (OutStorageRelation outStorageRelation : outStorageRelationList) {
        outStorageRelationDTOTempMap = outStorageRelationDTOTempMapMap.get(outStorageRelation.getOutStorageItemId());
        if(outStorageRelationDTOTempMap == null){
          outStorageRelationDTOTempMap = new HashMap<String, OutStorageRelation>();
        }
        String key = ObjectUtil.generateKey(outStorageRelation.getSupplierType(),outStorageRelation.getRelatedSupplierId());
        if(!outStorageRelationDTOTempMap.containsKey(key)){
          outStorageRelationDTOTempMap.put(key,outStorageRelation);
        }
        outStorageRelationDTOTempMapMap.put(outStorageRelation.getProductId(),outStorageRelationDTOTempMap);
      }

      for (Map.Entry<Long,Map<String,OutStorageRelation>> entry : outStorageRelationDTOTempMapMap.entrySet()) {
        outStorageRelationDTOMap.put(entry.getKey(),new ArrayList<OutStorageRelation>(entry.getValue().values()));
      }
    }

    return outStorageRelationDTOMap;
  }


  /**
   * @param bcgogoOrderItemDto
   * @param outStorageRelationDTOs
   */
  public List<SupplierInventoryDTO> getSupplierInventoryList(BcgogoOrderItemDto bcgogoOrderItemDto, OutStorageRelationDTO[] outStorageRelationDTOs, SupplierInventoryDTO supplierInventoryDTO) {
    List<SupplierInventoryDTO> supplierInventoryDTOList = new ArrayList<SupplierInventoryDTO>();


    SupplierInventory supplierInventory = new SupplierInventory();
    supplierInventory.fromDTO(supplierInventoryDTO);
    SupplierInventoryDTO inventoryDTO = supplierInventory.toDTO();


    if (bcgogoOrderItemDto != null) {
      inventoryDTO.setUnit(bcgogoOrderItemDto.getUnit());
      inventoryDTO.setChangeAmount(bcgogoOrderItemDto.getAmount());
      inventoryDTO.setSupplierType(OutStorageSupplierType.UNDEFINED_SUPPLIER);
    }

    if (ArrayUtil.isEmpty(outStorageRelationDTOs)) {
      supplierInventoryDTOList.add(inventoryDTO);
    } else {
      for (OutStorageRelationDTO outStorageRelationDTO : outStorageRelationDTOs) {
        supplierInventory = new SupplierInventory();
        supplierInventory.fromDTO(supplierInventoryDTO);
        inventoryDTO = new SupplierInventoryDTO();
        inventoryDTO = supplierInventory.toDTO();
        inventoryDTO.setSupplierId(outStorageRelationDTO.getRelatedSupplierId());
        inventoryDTO.setSupplierType(outStorageRelationDTO.getSupplierType());
        supplierInventoryDTOList.add(inventoryDTO);
      }
    }
    return supplierInventoryDTOList;
  }




  public List<SupplierInventoryDTO> supplierInventoryOfSale(SalesOrderDTO salesOrderDTO) {


    List<SupplierInventoryDTO> supplierInventoryDTOList = new ArrayList<SupplierInventoryDTO>();

    SalesOrderItemDTO[] salesOrderItemDTOs = salesOrderDTO.getItemDTOs();
    OutStorageRelationDTO[] outStorageRelationDTOs = null;
    boolean selectSupplier = salesOrderDTO.isSelectSupplier();
    Long storeHouseId = salesOrderDTO.getStorehouseId();
    Set<Long> productIds = new HashSet<Long>();

    SupplierInventoryDTO supplierInventoryDTO = new SupplierInventoryDTO();
    supplierInventoryDTO.setShopId(salesOrderDTO.getShopId());
    supplierInventoryDTO.setStorehouseId(storeHouseId);

    if (selectSupplier) {
      for (SalesOrderItemDTO salesOrderItemDTO : salesOrderItemDTOs) {
        outStorageRelationDTOs = salesOrderItemDTO.getOutStorageRelationDTOs();
        supplierInventoryDTOList.addAll(getSupplierInventoryList(salesOrderItemDTO, outStorageRelationDTOs, supplierInventoryDTO));
      }
      return supplierInventoryDTOList;
    }

    for (SalesOrderItemDTO salesOrderItemDTO : salesOrderItemDTOs) {
      productIds.add(salesOrderItemDTO.getProductId());
    }

    List<SupplierInventory> supplierInventoryList = this.getSupplierInventoryList(salesOrderDTO.getShopId(), null, storeHouseId, productIds);
    if (CollectionUtil.isEmpty(supplierInventoryDTOList)) {
      for (SalesOrderItemDTO salesOrderItemDTO : salesOrderItemDTOs) {

        OutStorageRelationDTO relationDTO = new OutStorageRelationDTO();
        relationDTO.setSupplierType(OutStorageSupplierType.UNDEFINED_SUPPLIER);
        relationDTO.setUseRelatedAmount(salesOrderItemDTO.getAmount());

        outStorageRelationDTOs = new OutStorageRelationDTO[1];
        outStorageRelationDTOs[0] = relationDTO;

        supplierInventoryDTOList.addAll(getSupplierInventoryList(salesOrderItemDTO, outStorageRelationDTOs, supplierInventoryDTO));
      }
      return supplierInventoryDTOList;
    }

    Map<Long, List<SupplierInventory>> map = new HashMap<Long, List<SupplierInventory>>();

    for (SupplierInventory supplierInventory : supplierInventoryList) {
      if (NumberUtil.doubleVal(supplierInventory.getRemainAmount()) <= 0) {
        continue;
      }

      List<SupplierInventory> inventoryList = map.get(supplierInventory.getProductId());

      if (inventoryList == null) {
        inventoryList = new ArrayList<SupplierInventory>();
      }
      inventoryList.add(supplierInventory);

      map.put(supplierInventory.getProductId(), inventoryList);
    }

    Map<Long, ProductLocalInfoDTO> productLocalInfoDTOMap = getProductService().getProductLocalInfoMap(salesOrderDTO.getShopId(), productIds.toArray(new Long[productIds.size()]));


    for (SalesOrderItemDTO salesOrderItemDTO : salesOrderItemDTOs) {
      Double amount = NumberUtil.doubleVal(salesOrderItemDTO.getAmount());
      String unit = salesOrderItemDTO.getUnit();
      ProductLocalInfoDTO productLocalInfoDTO = productLocalInfoDTOMap.get(salesOrderItemDTO.getProductId());

      if (UnitUtil.isStorageUnit(unit, productLocalInfoDTOMap.get(salesOrderItemDTO.getProductId()))) {
        amount = amount * productLocalInfoDTO.getRate();
      }

      outStorageRelationDTOs = getOutStorageRelationDTO(salesOrderItemDTO, amount, map.get(salesOrderItemDTO.getProductId()));
      salesOrderItemDTO.setOutStorageRelationDTOs(outStorageRelationDTOs);
      supplierInventoryDTOList.addAll(getSupplierInventoryList(salesOrderItemDTO, outStorageRelationDTOs, supplierInventoryDTO));
    }
    return supplierInventoryDTOList;
  }

  public OutStorageRelationDTO[] getOutStorageRelationDTO(BcgogoOrderItemDto bcgogoOrderItemDto,Double amount,List<SupplierInventory> supplierInventoryList) {

    if (CollectionUtil.isEmpty(supplierInventoryList)) {
      return null;
    }

    List<OutStorageRelationDTO> outStorageRelationDTOList = new ArrayList<OutStorageRelationDTO>();
    for (SupplierInventory supplierInventory : supplierInventoryList) {
      if (amount > 0) {
        OutStorageRelationDTO outStorageRelationDTO = new OutStorageRelationDTO();
        outStorageRelationDTO.setRelatedSupplierId(supplierInventory.getSupplierId());
        outStorageRelationDTO.setSupplierType(supplierInventory.getSupplierType());
        if (NumberUtil.doubleVal(supplierInventory.getRemainAmount()) > amount) {
          outStorageRelationDTO.setUseRelatedAmount(amount);
          amount = 0D;
        } else {
          outStorageRelationDTO.setUseRelatedAmount(NumberUtil.doubleVal(supplierInventory.getRemainAmount()));
          amount = amount - NumberUtil.doubleVal(supplierInventory.getRemainAmount());
        }
        outStorageRelationDTOList.add(outStorageRelationDTO);
      }
    }
    return outStorageRelationDTOList.toArray(new OutStorageRelationDTO[outStorageRelationDTOList.size()]);
  }


  /**
   * 根据shopId,单据id和单据类型 itemId productId获得商品出入库关系表
   *
   * @param shopId
   * @param relatedOrderId
   * @param relatedOrderType
   * @param relatedItemId
   * @param productId
   * @return
   */
  public List<OutStorageRelationDTO>  getOutStorageRelationByRelated(Long shopId,Long relatedOrderId ,OrderTypes relatedOrderType,Long relatedItemId ,Long productId) {
    TxnWriter writer = txnDaoManager.getWriter();
    List<OutStorageRelationDTO> outStorageRelationDTOList = new ArrayList<OutStorageRelationDTO>();

    List<OutStorageRelation> outStorageRelationList = writer.getOutStorageRelationByRelated(shopId, relatedOrderId, relatedOrderType, relatedItemId, productId);
    if (CollectionUtil.isEmpty(outStorageRelationList)) {
      return outStorageRelationDTOList;
    }
    for (OutStorageRelation outStorageRelation : outStorageRelationList) {
      outStorageRelationDTOList.add(outStorageRelation.toDTO());
    }
    return outStorageRelationDTOList;
  }


  @Override
  public int countSupplierInventory(Long shopId, Long productId) {
    if(shopId == null || productId == null){
      return 0;
    }
    return txnDaoManager.getWriter().countSupplierInventory(shopId,productId);
  }

  @Override
  public List<SupplierInventoryDTO> getSupplierInventoryByPaging(Long shopId, Long productId, Pager pager) {
    List<SupplierInventoryDTO> supplierInventoryDTOs = new ArrayList<SupplierInventoryDTO>();
    if (shopId == null || productId == null) {
      return supplierInventoryDTOs;
    }
    List<Long> supplierIds = txnDaoManager.getWriter().getSupplierIdsByProductId(shopId, productId, pager);
    if (CollectionUtil.isNotEmpty(supplierIds)) {
      supplierInventoryDTOs = new ArrayList<SupplierInventoryDTO>(getSupplierInventoryMapBySupplierIds(shopId, productId, new HashSet<Long>(supplierIds)).values());
      sortSupplierInventoryDTOsByLastPurchaseTime(supplierInventoryDTOs);
    }
    return supplierInventoryDTOs;
  }

  @Override
  public Map<String, SupplierInventoryDTO> getSupplierInventoryMapBySupplierIds(Long shopId, Long productId, Set<Long> supplierIds) {
    Map<String, SupplierInventoryDTO> supplierInventoryDTOMap = new HashMap<String, SupplierInventoryDTO>();
    if (shopId == null || productId == null || CollectionUtil.isEmpty(supplierIds)) {
      return supplierInventoryDTOMap;
    }
    List<SupplierInventory> supplierInventories = txnDaoManager.getWriter().getSupplierInventoryByProductAndSupplierIds(shopId, productId, supplierIds);
    if (CollectionUtil.isNotEmpty(supplierInventories)) {
      for (SupplierInventory supplierInventory : supplierInventories) {
        if (supplierInventory.getSupplierId() != null) {
          SupplierInventoryDTO supplierInventoryDTO = supplierInventoryDTOMap.get(supplierInventory.getSupplierId().toString());
          if (supplierInventoryDTO == null) {
            supplierInventoryDTO = supplierInventory.toDTO();
          } else {
            supplierInventoryDTO = buildSupplierInventoryDTO(supplierInventoryDTO, supplierInventory);
          }
          supplierInventoryDTOMap.put(supplierInventory.getSupplierId().toString(), supplierInventoryDTO);
        } else {
          if (supplierInventory.getSupplierType() != null) {
            SupplierInventoryDTO supplierInventoryDTO = supplierInventoryDTOMap.get(supplierInventory.getSupplierType().getName());
            if (supplierInventoryDTO == null) {
              supplierInventoryDTO = supplierInventory.toDTO();
            } else {
              supplierInventoryDTO = buildSupplierInventoryDTO(supplierInventoryDTO, supplierInventory);
            }
            supplierInventoryDTOMap.put(supplierInventory.getSupplierType().name(), supplierInventoryDTO);
          }
        }
      }
    }
    return supplierInventoryDTOMap;
  }

  private SupplierInventoryDTO buildSupplierInventoryDTO(SupplierInventoryDTO supplierInventoryDTO, SupplierInventory supplierInventory) {
    if (supplierInventoryDTO == null || supplierInventory == null) {
      return null;
    }
    //库存总量累加,剩余库存量累加
    double totalInStorageAmount = NumberUtil.round(NumberUtil.doubleVal(supplierInventoryDTO.getTotalInStorageAmount())
        + NumberUtil.doubleVal(supplierInventory.getTotalInStorageAmount()), 2);
    supplierInventoryDTO.setTotalInStorageAmount(totalInStorageAmount);
    double totalRemainAmount = NumberUtil.round(NumberUtil.doubleVal(supplierInventoryDTO.getRemainAmount())
        + NumberUtil.doubleVal(supplierInventory.getRemainAmount()), 2);
    //平均价重新计算
    if (totalRemainAmount > 0) {
      double averagePrice = NumberUtil.round((NumberUtil.doubleVal(supplierInventoryDTO.getRemainAmount())
          * NumberUtil.doubleVal(supplierInventoryDTO.getAverageStoragePrice())
          + NumberUtil.doubleVal(supplierInventory.getRemainAmount()) * NumberUtil.doubleVal(supplierInventory.getAverageStoragePrice()))
          / totalRemainAmount, 2);
      supplierInventoryDTO.setAverageStoragePrice(averagePrice);
    }
    supplierInventoryDTO.setRemainAmount(totalRemainAmount);
    //最高价，最低价重新计算
    if (NumberUtil.doubleVal(supplierInventory.getMaxStoragePrice()) > NumberUtil.doubleVal(supplierInventoryDTO.getMaxStoragePrice())) {
      supplierInventoryDTO.setMaxStoragePrice(supplierInventory.getMaxStoragePrice());
    }
    if ( NumberUtil.doubleVal(supplierInventory.getMinStoragePrice())>0.0001 &&
        (NumberUtil.doubleVal(supplierInventory.getMinStoragePrice()) < NumberUtil.doubleVal(supplierInventoryDTO.getMinStoragePrice())
            || supplierInventoryDTO.getMinStoragePrice() == null)) {
      supplierInventoryDTO.setMinStoragePrice(supplierInventory.getMinStoragePrice());
    }

    //最后入库信息取最新的
    if (supplierInventoryDTO.getLastStorageTime() == null ||
        (supplierInventoryDTO.getLastStorageTime() != null && supplierInventory.getLastStorageTime() != null
            && supplierInventoryDTO.getLastStorageTime() < supplierInventory.getLastStorageTime())) {
      supplierInventoryDTO.setLastStorageTime(supplierInventory.getLastStorageTime());
      supplierInventoryDTO.setLastStorageAmount(supplierInventory.getLastStorageAmount());
      supplierInventoryDTO.setLastStoragePrice(supplierInventory.getLastStoragePrice());
      supplierInventoryDTO.setSupplierName(supplierInventory.getSupplierName());
      supplierInventoryDTO.setSupplierContact(supplierInventory.getSupplierContact());
      supplierInventoryDTO.setSupplierMobile(supplierInventory.getSupplierMobile());
      supplierInventoryDTO.setLastPurchaseInventoryOrderId(supplierInventory.getLastPurchaseInventoryOrderId());
    }
    return supplierInventoryDTO;
  }


  @Override
  public List<SupplierInventoryDTO> getSupplierInventoryDTOsByProductIdAndStorehouseId(Long shopId, Long storehouseId, Long... productIds) {
    List<SupplierInventoryDTO> supplierInventoryDTOs = new ArrayList<SupplierInventoryDTO>();
    IProductService productService = ServiceManager.getService(IProductService.class);
    if (shopId == null || ArrayUtils.isEmpty(productIds) || storehouseId == null) {
      return supplierInventoryDTOs;
    }
    List<SupplierInventory> supplierInventories = txnDaoManager.getWriter().getSupplierInventoryByStorehouseIdAndProductIds(
        shopId, storehouseId, productIds);
    // 没有Id的供应商的产品库存（多种类型的未指定只存一条）
    Map<Long, SupplierInventoryDTO> undefinedSupplierMap = new HashMap<Long, SupplierInventoryDTO>();
    //有Id的供应商的产品库存（多种类型的未指定只存一条）
    Map<Long, SupplierInventoryDTO> resultSupplierMap = new HashMap<Long, SupplierInventoryDTO>();
    if (CollectionUtil.isNotEmpty(supplierInventories)) {
      for (SupplierInventory supplierInventory : supplierInventories) {
        if (supplierInventory.getProductId() == null) {
          continue;
        }
        SupplierInventoryDTO supplierInventoryDTO =  supplierInventory.toDTO();
        if (supplierInventory.getSupplierId() == null) {
          undefinedSupplierMap.put(supplierInventory.getProductId(), supplierInventoryDTO);
        }else{
          resultSupplierMap.put(supplierInventory.getProductId(), supplierInventoryDTO);
        }
        supplierInventoryDTOs.add(supplierInventoryDTO);
      }
    }
    Map<Long,ProductDTO> productDTOMap =  productService.getProductDTOMapByProductLocalInfoIds(shopId, new HashSet<Long>(Arrays.asList(productIds)));
    for (Long productId : productIds) {
      if (undefinedSupplierMap.get(productId) == null && resultSupplierMap.get(productId) == null) {
        SupplierInventoryDTO supplierInventoryDTO = new SupplierInventoryDTO();
        supplierInventoryDTO.setProductId(productId);
        supplierInventoryDTO.setSupplierName(OutStorageSupplierType.UNDEFINED_SUPPLIER.getName());
        supplierInventoryDTO.setSupplierType(OutStorageSupplierType.UNDEFINED_SUPPLIER);
        ProductDTO productDTO =  productDTOMap.get(productId);
        if(productDTO != null){
          supplierInventoryDTO.setUnit(productDTO.getSellUnit());
        }
        supplierInventoryDTO.setRemainAmount(0D);
        undefinedSupplierMap.put(productId,supplierInventoryDTO);
        resultSupplierMap.put(productId,supplierInventoryDTO);
        supplierInventoryDTOs.add(supplierInventoryDTO);
      }
    }
    return supplierInventoryDTOs;
  }

  @Override
  public List<SupplierInventoryDTO> getSupplierInventoryDTOsByProductId(Long shopId, Long productId) {
    List<SupplierInventoryDTO> supplierInventoryDTOs = new ArrayList<SupplierInventoryDTO>();
       if (shopId == null  || productId == null) {
         return supplierInventoryDTOs;
       }
    Set<Long> productIds = new HashSet<Long>();
    productIds.add(productId);
       List<SupplierInventory> supplierInventories = txnDaoManager.getWriter().getSupplierInventoriesByProductIds(shopId,productIds);
       if (CollectionUtil.isNotEmpty(supplierInventories)) {
         for (SupplierInventory supplierInventory : supplierInventories) {
           if (supplierInventory.getProductId() == null) {
             continue;
           }
           SupplierInventoryDTO supplierInventoryDTO =  supplierInventory.toDTO();
           supplierInventoryDTOs.add(supplierInventoryDTO);
         }
       }
       return supplierInventoryDTOs;
  }


  /**
   *
   * @param shopId
   * @param storehouseId 仓库列表
   * @param productIdSet 产品id列表
   * @param supplierIdSet  供应商列表
   * @param outStorageSupplierTypes 供应商类型
   * @param containSupplierIdEmpty 是否有供应商id为空的
   * @return
   */
  public Map<Long, List<InStorageRecordDTO>> getInStorageRecordMap(Long shopId,Long storehouseId, Set<Long> productIdSet, Set<Long> supplierIdSet,Set<OutStorageSupplierType> outStorageSupplierTypes,boolean containSupplierIdEmpty) {
    Map<Long, List<InStorageRecordDTO>> productMap = new HashMap<Long, List<InStorageRecordDTO>>();
    TxnWriter writer = txnDaoManager.getWriter();

    if (CollectionUtil.isEmpty(productIdSet)) {
      return productMap;
    }

    List<InStorageRecord> inStorageRecordList = writer.getInStorageRecordMap(shopId, storehouseId, productIdSet, supplierIdSet, outStorageSupplierTypes, containSupplierIdEmpty);

    if (CollectionUtil.isEmpty(inStorageRecordList)) {
      return productMap;
    }
    List<InStorageRecordDTO> inStorageRecordDTOList = null;
    for (InStorageRecord inStorageRecord : inStorageRecordList) {
      if(inStorageRecord.getSupplierId() == null && !containSupplierIdEmpty){
        continue;
      }else if (inStorageRecord.getSupplierId() != null && CollectionUtils.isNotEmpty(supplierIdSet) && !supplierIdSet.contains(inStorageRecord.getSupplierId())) {
        continue;
      }
      if (productMap.containsKey(inStorageRecord.getProductId())) {
        inStorageRecordDTOList = productMap.get(inStorageRecord.getProductId());
      } else {
        inStorageRecordDTOList = new ArrayList<InStorageRecordDTO>();
      }
      inStorageRecordDTOList.add(inStorageRecord.toDTO());
      productMap.put(inStorageRecord.getProductId(), inStorageRecordDTOList);
    }
    return productMap;
  }

  @Override
  public void sortSupplierInventoryDTOsByLastPurchaseTime(List<SupplierInventoryDTO> supplierInventoryDTOs) {
    if (CollectionUtil.isEmpty(supplierInventoryDTOs)) {
      return;
    }
    ComparatorSupplierInventoryDTO comparator = new ComparatorSupplierInventoryDTO();
    Collections.sort(supplierInventoryDTOs, comparator);
  }

  private class ComparatorSupplierInventoryDTO implements Comparator {

    public int compare(Object arg0, Object arg1) {
      SupplierInventoryDTO supplierInventoryDTO0 = (SupplierInventoryDTO) arg0;
      SupplierInventoryDTO supplierInventoryDTO1 = (SupplierInventoryDTO) arg1;

      long lastPurchaseTime0 = NumberUtil.longValue(supplierInventoryDTO0.getLastStorageTime());
      long lastPurchaseTime1 = NumberUtil.longValue(supplierInventoryDTO1.getLastStorageTime());
      if (lastPurchaseTime0 > lastPurchaseTime1) {
        return -1;
      } else if (lastPurchaseTime0 < lastPurchaseTime1) {
        return 1;
      } else {
        return 0;
      }
    }
  }

  @Override
  public void updateSupplierInventoryStatusBySupplierId(Long shopId,Long supplierId, YesNo isDisabled)throws Exception{
    if(shopId == null || supplierId == null || isDisabled == null){
      return;
    }
    Set<Long> supplierIds = new HashSet<Long>();
    supplierIds.add(supplierId);
    List<SupplierInventoryDTO> supplierInventoryDTOs = txnDaoManager.getWriter().getSupplierInventoryDTOBySupplierIds(shopId,supplierIds);
    if(CollectionUtils.isNotEmpty(supplierInventoryDTOs)){
      for(SupplierInventoryDTO supplierInventoryDTO :supplierInventoryDTOs){
        supplierInventoryDTO.setDisabled(isDisabled);
      }
      saveOrUpdateSupplierInventoryByModify(supplierInventoryDTOs);
    }
  }

  @Override
  public void updateSupplierInventoryStatusByProductId(Long shopId, Long productId, YesNo isDisabled) throws Exception{
    if(shopId == null || productId == null || isDisabled == null){
        return;
      }
     List<SupplierInventoryDTO> supplierInventoryDTOs = getSupplierInventoryDTOsByProductId(shopId,productId);
    if(CollectionUtils.isNotEmpty(supplierInventoryDTOs)){
       for(SupplierInventoryDTO supplierInventoryDTO :supplierInventoryDTOs){
         supplierInventoryDTO.setDisabled(isDisabled);
       }
       saveOrUpdateSupplierInventoryByModify(supplierInventoryDTOs);
     }
  }

  //产品供应商打通数据初始化
  public void initProductThrough(Long shopId) throws Exception {

    TxnWriter txnWriter = txnDaoManager.getWriter();

    List<PurchaseInventory> purchaseInventoryList = txnWriter.getPurchaseInventoryByShopId(shopId, OrderStatus.PURCHASE_INVENTORY_DONE);

    Map<Long,List<PurchaseInventoryDTO>> map = new HashMap<Long,List<PurchaseInventoryDTO>>();

    if (CollectionUtils.isNotEmpty(purchaseInventoryList)) {
      for (PurchaseInventory purchaseInventory : purchaseInventoryList) {
        Long purchaseInventoryId = purchaseInventory.getId();

        PurchaseInventoryDTO purchaseInventoryDTO = ServiceManager.getService(IGoodsStorageService.class).getPurchaseInventory(purchaseInventoryId, shopId);

        List<SupplierInventoryDTO> supplierInventoryDTOs = new ArrayList<SupplierInventoryDTO>();

        for (PurchaseInventoryItemDTO purchaseInventoryItemDTO : purchaseInventoryDTO.getItemDTOs()) {

          List<PurchaseInventoryDTO> purchaseInventoryDTOList = null;
          if (map.containsKey(purchaseInventoryItemDTO.getProductId())) {
            purchaseInventoryDTOList = map.get(purchaseInventoryItemDTO.getProductId());
          } else {
            purchaseInventoryDTOList = new ArrayList<PurchaseInventoryDTO>();
          }
          purchaseInventoryDTOList.add(purchaseInventoryDTO);

          map.put(purchaseInventoryItemDTO.getProductId(),purchaseInventoryDTOList);

          ProductLocalInfoDTO productLocalInfoDTO = getProductService().getProductLocalInfoById(purchaseInventoryItemDTO.getProductId(), shopId);

          SupplierInventoryDTO supplierInventoryDTO = new SupplierInventoryDTO(purchaseInventoryDTO, purchaseInventoryItemDTO);
          supplierInventoryDTO.addStorageInventoryChange(productLocalInfoDTO.getSellUnit(), purchaseInventoryItemDTO.getAmount(), NumberUtil.doubleVal(purchaseInventoryItemDTO.getPrice()));
          supplierInventoryDTOs.add(supplierInventoryDTO);
        }


        Object status = txnWriter.begin();
        try {

          ServiceManager.getService(IProductInStorageService.class).productThroughByOrder(purchaseInventoryDTO, OrderTypes.INVENTORY, purchaseInventoryDTO.getStatus(),txnWriter);
          this.saveOrUpdateSupplierInventory(txnWriter, supplierInventoryDTOs);
          txnWriter.commit(status);
        } finally {
          txnWriter.rollback(status);
        }

      }
    }


    List<SalesOrder> salesOrderList = txnWriter.getSalesOrderByShopId(shopId, OrderStatus.SALE_DONE);
    if (CollectionUtils.isNotEmpty(salesOrderList)) {
      for (SalesOrder salesOrder : salesOrderList) {
        SalesOrderDTO salesOrderDTO = getTxnService().getSalesOrder(salesOrder.getId(), shopId);
        salesOrderDTO.setSelectSupplier(true);
        Object status = txnWriter.begin();
        try {

          for (SalesOrderItemDTO salesOrderItemDTO : salesOrderDTO.getItemDTOs()) {
            List<OutStorageRelationDTO> outStorageRelationDTOList = new ArrayList<OutStorageRelationDTO>();
            OutStorageRelationDTO outStorageRelationDTO = new OutStorageRelationDTO();
            outStorageRelationDTO.setSupplierType(OutStorageSupplierType.NATIVE_SUPPLIER);

            List<PurchaseInventoryDTO> purchaseInventoryDTOList = map.get(salesOrderItemDTO.getProductId());
            if (CollectionUtils.isNotEmpty(purchaseInventoryDTOList)) {
              PurchaseInventoryDTO purchaseInventoryDTO = CollectionUtil.getFirst(purchaseInventoryDTOList);
              outStorageRelationDTO.setUseRelatedAmount(salesOrderItemDTO.getAmount());
              outStorageRelationDTO.setRelatedSupplierId(purchaseInventoryDTO.getSupplierId());
              outStorageRelationDTO.setRelatedSupplierName(purchaseInventoryDTO.getSupplier());
            } else {
              throw new Exception("purchaseInventoryDTO is null");
            }
            outStorageRelationDTOList.add(outStorageRelationDTO);
            salesOrderItemDTO.setOutStorageRelationDTOs(outStorageRelationDTOList.toArray(new OutStorageRelationDTO[outStorageRelationDTOList.size()]));
          }


          ServiceManager.getService(IProductOutStorageService.class).productThroughByOrder(salesOrderDTO, OrderTypes.SALE, salesOrderDTO.getStatus(), txnWriter,null);
          txnWriter.commit(status);
        } finally {
          txnWriter.rollback(status);
        }
      }
    }

  }

 @Override
  public void  generatePurchaseReturnOutStorageRelation(BcgogoOrderDto orderDto,Long supplierId){
    if(supplierId==null) return;
    Set<Long> productIds=new HashSet<Long>();
    if(!ArrayUtil.isEmpty(orderDto.getItemDTOs())){
      for(BcgogoOrderItemDto itemDto:orderDto.getItemDTOs()){
        productIds.add(itemDto.getProductId());
      }
      if(productIds.isEmpty()) return;
      SupplierInventoryDTO condition=new SupplierInventoryDTO();
      condition.setShopId(orderDto.getShopId());
      condition.setProductIds(productIds.toArray(new Long[productIds.size()]));
      condition.setSupplierId(supplierId);
      List<SupplierInventory> inventories=getSupplierInventory(condition);
      if(CollectionUtil.isEmpty(inventories)) return;
      Map<Long,OutStorageRelationDTO> relationDTOMap=new HashMap<Long, OutStorageRelationDTO>();
      for(SupplierInventory inventory:inventories){
        OutStorageRelationDTO relationDTO=new OutStorageRelationDTO();
        relationDTO.setRelatedSupplierId(supplierId);
        relationDTO.setRelatedSupplierName(inventory.getSupplierName());
        relationDTO.setRelatedSupplierInventory(inventory.getRemainAmount());
        relationDTO.setRelatedSupplierAveragePrice(inventory.getAverageStoragePrice());
        relationDTO.setSupplierType(inventory.getSupplierType());
        relationDTO.setProductId(inventory.getProductId());
        relationDTOMap.put(inventory.getProductId(),relationDTO);
      }
      for(BcgogoOrderItemDto itemDto:orderDto.getItemDTOs()){
        OutStorageRelationDTO outStorageRelationDTO=relationDTOMap.get(itemDto.getProductId());
        if(outStorageRelationDTO!=null){
          outStorageRelationDTO.setUseRelatedAmount(itemDto.getAmount());
          itemDto.setOutStorageRelationDTOs(new OutStorageRelationDTO[]{outStorageRelationDTO});
        }
      }
    }
  }


  public void productThroughByOrderForUpdateOnlineReturn(PurchaseReturnDTO purchaseReturnDTO,PurchaseReturnDTO originPurchaseReturnDTO,TxnWriter writer){
    if(purchaseReturnDTO==null||originPurchaseReturnDTO==null){
      return;
    }
    getProductInStorageService().productThroughByOrder(originPurchaseReturnDTO,OrderTypes.RETURN,OrderStatus.REPEAL,writer);
    getProductOutStorageService().productThroughByOrder(purchaseReturnDTO,OrderTypes.RETURN,OrderStatus.SELLER_PENDING,writer,null);
  }


}
