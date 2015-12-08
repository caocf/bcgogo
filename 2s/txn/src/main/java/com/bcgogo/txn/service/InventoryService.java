package com.bcgogo.txn.service;

import com.bcgogo.common.Result;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.util.MemCacheAdapter;
import com.bcgogo.constant.MemcachePrefix;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.enums.PayStatus;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.product.dto.ProductLocalInfoDTO;
import com.bcgogo.product.service.IProductService;
import com.bcgogo.product.service.IProductSolrService;
import com.bcgogo.search.dto.InventorySearchIndexDTO;
import com.bcgogo.search.model.InventorySearchIndex;
import com.bcgogo.search.service.ISearchService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.bcgogoListener.threadPool.OrderThreadPool;
import com.bcgogo.txn.dto.*;
import com.bcgogo.txn.dto.StatementAccount.OrderDebtType;
import com.bcgogo.txn.model.*;
import com.bcgogo.txn.service.solr.IProductSolrWriterService;
import com.bcgogo.utils.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: QiuXinyu
 * Date: 12-6-18
 * Time: 上午10:22
 * To change this template use File | Settings | File Templates.
 */

@Component
public class InventoryService implements IInventoryService {

  private static final Logger LOG = LoggerFactory.getLogger(InventoryService.class);
  public static final String SURPAY="surPay";
  @Autowired
  private TxnDaoManager txnDaoManager;

	private IGoodSaleService goodSaleService;
	private IProductService productService;
	private ISearchService searchService;
  private IProductSolrService productSolrService;

	public IGoodSaleService getGoodSaleService() {
		return goodSaleService == null ? ServiceManager.getService(IGoodSaleService.class) : goodSaleService;
	}

	public IProductService getProductService() {
		return productService == null ?ServiceManager.getService(IProductService.class) : productService;
	}

	public ISearchService getSearchService() {
		return searchService == null ?ServiceManager.getService(ISearchService.class) : searchService;
	}

	public IProductSolrService getProductSolrService() {
		return productSolrService == null ?ServiceManager.getService(IProductSolrService.class) : productSolrService;
	}

  @Override
  public MemcacheInventorySumDTO getInventorySum(Long shopId) throws Exception {
    if (shopId == null) {
      return null;
    }
    ISearchService searchService = ServiceManager.getService(ISearchService.class);
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    String delayStr = configService.getConfig("InventoryCountDelay", -1L);
    Long delayTime;
    if (StringUtils.isBlank(delayStr) || !NumberUtils.isNumber(delayStr)) {
      delayTime = TxnConstant.MEMCACHE_INVENTORY_COUNT_ACTIVETIME;
    } else {
      delayTime = new Long(delayStr) * 1000L;
    }
    Long currentTime = System.currentTimeMillis();
    String key = MemcachePrefix.inventorySum.getValue() + shopId.toString();
    MemcacheInventorySumDTO memcacheInventorySumDTO = (MemcacheInventorySumDTO) MemCacheAdapter.get(key);
    if (memcacheInventorySumDTO != null &&
        (currentTime - memcacheInventorySumDTO.getStorageTime() < delayTime)) {
      return memcacheInventorySumDTO;
    } else {
      memcacheInventorySumDTO = searchService.countInventoryInfoByShopId(shopId);
      memcacheInventorySumDTO.setStorageTime(currentTime);
      if (LOG.isDebugEnabled()) {
      }
      MemCacheAdapter.set(key, memcacheInventorySumDTO);
      return memcacheInventorySumDTO;
    }
  }

  @Override
  public MemcacheInventorySumDTO getSearchProductNameInventoryCount(Long shopId, String productName, MemcacheInventorySumDTO memcacheInventorySumDTO) throws Exception {
    ISearchService searchService = ServiceManager.getService(ISearchService.class);
    Integer start = 0;
    Integer rows = RfTxnConstant.SOLR_INVENTORY_SUM_MAXROWS;
    int docSize, loopIndex = 0;

    if (memcacheInventorySumDTO == null) {
      memcacheInventorySumDTO = new MemcacheInventorySumDTO();
      memcacheInventorySumDTO.setInventoryCount(0);
      memcacheInventorySumDTO.setInventorySum(0d);
      memcacheInventorySumDTO.setInventoryProductAmount(0d);
      memcacheInventorySumDTO.setShopId(shopId);
    } else {
      if (memcacheInventorySumDTO.getInventoryCount() == null) {
        memcacheInventorySumDTO.setInventoryCount(0);
      }
      if (memcacheInventorySumDTO.getInventorySum() == null) {
        memcacheInventorySumDTO.setInventorySum(0d);
      }
      if (memcacheInventorySumDTO.getInventoryProductAmount() == null) {
        memcacheInventorySumDTO.setInventoryProductAmount(0d);
      }
    }
    if (StringUtils.isBlank(productName)) {
      return getInventorySum(shopId);
    } else {
      do {
        QueryResponse response = searchService.queryProductByKeywords(shopId, productName, start, rows);
        SolrDocumentList docs = response.getResults();
        docSize = docs.size();
        double inventorySum = 0;
        double totalAmount = 0d;
        for (int i = 0; i < docSize; i++) {
          double purchasePrice = docs.get(i).getFieldValue("purchase_price") == null ?
              0d :  NumberUtil.doubleValue(docs.get(i).getFieldValue("purchase_price").toString(),0d);
          double amount = docs.get(i).getFieldValue("inventory_amount") == null ?
              0d : NumberUtil.doubleValue(docs.get(i).getFieldValue("inventory_amount").toString(),0d);
          inventorySum += purchasePrice * amount;
          totalAmount += amount;
        }
        loopIndex++;
        start = rows * loopIndex;
        memcacheInventorySumDTO.setInventoryCount(memcacheInventorySumDTO.getInventoryCount() + docSize);
        memcacheInventorySumDTO.setInventorySum(memcacheInventorySumDTO.getInventorySum() + inventorySum);
        memcacheInventorySumDTO.setInventoryProductAmount(memcacheInventorySumDTO.getInventoryProductAmount() + totalAmount);
      } while (rows.equals(docSize));
      return memcacheInventorySumDTO;
    }
  }

  @Override
  public void saveOrUpdateInventoryLimit(PurchaseInventoryDTO purchaseInventoryDTO) throws Exception {
    IProductService productService = ServiceManager.getService(IProductService.class);
    TxnWriter txnWriter = txnDaoManager.getWriter();
    Object status = txnWriter.begin();
    try {
      if (purchaseInventoryDTO == null || purchaseInventoryDTO.getItemDTOs() == null) {
        return;
      }
      for (PurchaseInventoryItemDTO purchaseInventoryItemDTO : purchaseInventoryDTO.getItemDTOs()) {
        if (purchaseInventoryItemDTO.getProductId() == null) {
          continue;
        }
        Inventory inventory = txnWriter.getById(Inventory.class, purchaseInventoryItemDTO.getProductId());
        ProductLocalInfoDTO productLocalInfoDTO = productService.getProductLocalInfoById(purchaseInventoryItemDTO.getProductId(), purchaseInventoryDTO.getShopId());
        if (UnitUtil.isStorageUnit(purchaseInventoryItemDTO.getUnit(), productLocalInfoDTO)) {   //大单位
          inventory.setLowerLimit(purchaseInventoryItemDTO.getLowerLimit() == null ? null : purchaseInventoryItemDTO.getLowerLimit() * productLocalInfoDTO.getRate());
          inventory.setUpperLimit(purchaseInventoryItemDTO.getUpperLimit() == null ? null : purchaseInventoryItemDTO.getUpperLimit() * productLocalInfoDTO.getRate());
        } else {
          inventory.setLowerLimit(purchaseInventoryItemDTO.getLowerLimit());
          inventory.setUpperLimit(purchaseInventoryItemDTO.getUpperLimit());
        }
        txnWriter.saveOrUpdate(inventory);
        caculateAfterLimit(inventory.toDTO(), purchaseInventoryDTO.getInventoryLimitDTO());
      }
      txnWriter.commit(status);
    } finally {
      txnWriter.rollback(status);
    }

  }

  @Override
  public void caculateBeforeLimit(InventoryDTO inventoryDTO, InventoryLimitDTO inventoryLimitDTO) {
    if (inventoryDTO == null) {
      return;
    }
    if (inventoryLimitDTO == null) {
      inventoryLimitDTO = new InventoryLimitDTO();
    }

    if (inventoryDTO.getLowerLimit() != null && !(new Double(0)).equals(inventoryDTO.getLowerLimit())) {
      if (inventoryDTO.getAmount() < inventoryDTO.getLowerLimit() - 0.0001) {
        inventoryLimitDTO.setPreviousLowerLimitAmount(inventoryLimitDTO.getPreviousLowerLimitAmount() == null ?
            1 : (inventoryLimitDTO.getPreviousLowerLimitAmount() + 1));
      }
    }
    if (inventoryDTO.getUpperLimit() != null && !(new Double(0).equals(inventoryDTO.getUpperLimit()))) {
      if (inventoryDTO.getAmount() > inventoryDTO.getUpperLimit() + 0.0001) {
        inventoryLimitDTO.setPreviousUpperLimitAmount(inventoryLimitDTO.getPreviousUpperLimitAmount() == null ?
            1 : (inventoryLimitDTO.getPreviousUpperLimitAmount() + 1));
      }
    }
  }

  @Override
  public void caculateAfterLimit(InventoryDTO inventoryDTO, InventoryLimitDTO inventoryLimitDTO) {
    if (inventoryDTO == null) {
      return;
    }
    if (inventoryLimitDTO == null) {
      inventoryLimitDTO = new InventoryLimitDTO();
    }

    if (inventoryDTO.getLowerLimit() != null && !(new Double(0)).equals(inventoryDTO.getLowerLimit())) {
      if (inventoryDTO.getAmount() < inventoryDTO.getLowerLimit() - 0.0001) {
        inventoryLimitDTO.setAfterLowerLimitAmount(inventoryLimitDTO.getAfterLowerLimitAmount() == null ?
            1 : (inventoryLimitDTO.getAfterLowerLimitAmount() + 1));
      }
    }
    if (inventoryDTO.getUpperLimit() != null && !(new Double(0).equals(inventoryDTO.getUpperLimit()))) {
      if (inventoryDTO.getAmount() > inventoryDTO.getUpperLimit() + 0.0001) {
        inventoryLimitDTO.setAfterUpperLimitAmount(inventoryLimitDTO.getAfterUpperLimitAmount() == null ?
            1 : (inventoryLimitDTO.getAfterUpperLimitAmount() + 1));
      }
    }
  }

  @Override
  public void caculateBeforeLimit(InventorySearchIndexDTO inventoryDTO, InventoryLimitDTO inventoryLimitDTO) {
    if (inventoryDTO == null) {
      return;
    }
    if (inventoryLimitDTO == null) {
      inventoryLimitDTO = new InventoryLimitDTO();
    }

    if (inventoryDTO.getLowerLimit() != null && !(new Double(0)).equals(inventoryDTO.getLowerLimit())) {
      if (inventoryDTO.getAmount() < inventoryDTO.getLowerLimit() - 0.0001) {
        inventoryLimitDTO.setPreviousLowerLimitAmount(inventoryLimitDTO.getPreviousLowerLimitAmount() == null ?
            1 : (inventoryLimitDTO.getPreviousLowerLimitAmount() + 1));
      }
    }
    if (inventoryDTO.getUpperLimit() != null && !(new Double(0).equals(inventoryDTO.getUpperLimit()))) {
      if (inventoryDTO.getAmount() > inventoryDTO.getUpperLimit() + 0.0001) {
        inventoryLimitDTO.setPreviousUpperLimitAmount(inventoryLimitDTO.getPreviousUpperLimitAmount() == null ?
            1 : (inventoryLimitDTO.getPreviousUpperLimitAmount() + 1));
      }
    }
  }

  @Override
  public void caculateAfterLimit(InventorySearchIndexDTO inventoryDTO, InventoryLimitDTO inventoryLimitDTO) {
    if (inventoryDTO == null) {
      return;
    }
    if (inventoryLimitDTO == null) {
      inventoryLimitDTO = new InventoryLimitDTO();
    }

    if (inventoryDTO.getLowerLimit() != null && !(new Double(0)).equals(inventoryDTO.getLowerLimit())) {
      if (inventoryDTO.getAmount() < inventoryDTO.getLowerLimit() - 0.0001) {
        inventoryLimitDTO.setAfterLowerLimitAmount(inventoryLimitDTO.getAfterLowerLimitAmount() == null ?
            1 : (inventoryLimitDTO.getAfterLowerLimitAmount() + 1));
      }
    }
    if (inventoryDTO.getUpperLimit() != null && !(new Double(0).equals(inventoryDTO.getUpperLimit()))) {
      if (inventoryDTO.getAmount() > inventoryDTO.getUpperLimit() + 0.0001) {
        inventoryLimitDTO.setAfterUpperLimitAmount(inventoryLimitDTO.getAfterUpperLimitAmount() == null ?
            1 : (inventoryLimitDTO.getAfterUpperLimitAmount() + 1));
      }
    }
  }

  @Override
  public void getLimitAndAchievementForProductDTOs(List<ProductDTO> productDTOs,Long shopId) throws Exception {
    TxnWriter txnWriter = txnDaoManager.getWriter();
    if (CollectionUtils.isEmpty(productDTOs)) {
      return;
    }
	  Long [] productIds = new Long[productDTOs.size()];
	  for (int i = 0, len = productDTOs.size(); i < len; i++) {
		  productIds[i] = productDTOs.get(i).getProductLocalInfoId();
	  }
	  List<Inventory> inventories = txnWriter.getInventoryByIds(shopId, productIds);
	  Map<Long, Inventory> inventoryMap = new HashMap<Long, Inventory>(productDTOs.size() * 2, 0.75f);
	  for(Inventory inventory :inventories){
		  inventoryMap.put(inventory.getId(),inventory);
	  }
	  for (ProductDTO productDTO : productDTOs) {
		  if (productDTO == null || productDTO.getProductLocalInfoId() == null) {
			  continue;
		  }
		  Inventory inventory = inventoryMap.get(productDTO.getProductLocalInfoId());
		  if (inventory != null) {
			  productDTO.setLowerLimit(inventory.getLowerLimit());
			  productDTO.setUpperLimit(inventory.getUpperLimit());
        productDTO.setSalesTotalAchievementAmount(inventory.getSalesTotalAchievementAmount());
        productDTO.setSalesTotalAchievementType(inventory.getSalesTotalAchievementType());
        productDTO.setSalesProfitAchievementAmount(inventory.getSalesProfitAchievementAmount());
        productDTO.setSalesProfitAchievementType(inventory.getSalesProfitAchievementType());

		  }
	  }
  }

  @Override
  public void updateInventoryLimit(InventoryLimitDTO inventoryLimitDTO) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      if (inventoryLimitDTO == null || inventoryLimitDTO.getProductDTOs() == null || inventoryLimitDTO.getShopId() == null) {
        return;
      }
      Set<Long> productLocalInfoIdSet = new HashSet<Long>();
      Map<Long, ProductDTO> productDTOMap = new HashMap<Long, ProductDTO>();
      for (ProductDTO productDTO : inventoryLimitDTO.getProductDTOs()) {
        if (productDTO == null || productDTO.getProductLocalInfoId() == null) {
          continue;
        }
        productLocalInfoIdSet.add(productDTO.getProductLocalInfoId());
        productDTOMap.put(productDTO.getProductLocalInfoId(), productDTO);
      }
      if (productLocalInfoIdSet != null && !productLocalInfoIdSet.isEmpty() && productDTOMap != null) {
        List<Inventory> inventories = writer.getInventoryByIds(inventoryLimitDTO.getShopId(), productLocalInfoIdSet.toArray(new Long[productLocalInfoIdSet.size()]));
        if (CollectionUtils.isNotEmpty(inventories)) {
          for (Inventory inventory : inventories) {
            if (inventory == null) {
              continue;
            }
            caculateBeforeLimit(inventory.toDTO(), inventoryLimitDTO);
            ProductDTO productDTO = productDTOMap.get(inventory.getId());
            inventory.setLowerLimit(productDTO.getLowerLimit());
            inventory.setUpperLimit(productDTO.getUpperLimit());
            writer.update(inventory);
            caculateAfterLimit(inventory.toDTO(), inventoryLimitDTO);
          }
        }
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public void updateSingelInventoryLimit(Long productId, Double lowerLimitVal, Double upperLimitVal,
                                         Long shopId, InventoryLimitDTO inventoryLimitDTO) throws Exception {
    if (shopId == null || productId == null) {
      return;
    }
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {

      Inventory inventory = writer.getInventoryByIdAndshopId(productId, shopId);
      if (inventory != null && lowerLimitVal != null && upperLimitVal != null) {
        if (lowerLimitVal.equals(inventory.getLowerLimit()) && upperLimitVal.equals(inventory.getUpperLimit())) {
          return;
        }
        caculateBeforeLimit(inventory.toDTO(), inventoryLimitDTO);
        inventory.setLowerLimit(lowerLimitVal);
        inventory.setUpperLimit(upperLimitVal);
        writer.update(inventory);
        writer.commit(status);
        caculateAfterLimit(inventory.toDTO(), inventoryLimitDTO);
      }
    } finally {
      writer.rollback(status);
    }
  }

  @Override
	public InventoryDTO updateInventoryInfo(Long shopId,InventoryDTO inventoryDTO,InventoryLimitDTO inventoryLimitDTO) throws Exception {
    if (shopId == null) throw new BcgogoException("shopId is null");
    if (inventoryDTO == null) throw new BcgogoException("inventoryDTO is null");
    if (inventoryDTO.getId() == null) throw new BcgogoException("inventory id is null");
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      Inventory inventory = writer.getInventoryByIdAndshopId(inventoryDTO.getId(), shopId);
      if(inventory==null) return null;
        caculateBeforeLimit(inventory.toDTO(), inventoryLimitDTO);
      if (inventoryDTO.getLowerLimit() != null && inventoryDTO.getUpperLimit() != null) {
        inventory.setLowerLimit(inventoryDTO.getLowerLimit());
        inventory.setUpperLimit(inventoryDTO.getUpperLimit());
      }
      if (inventoryDTO.getSalesPrice() != null) {
        inventory.setSalesPrice(inventoryDTO.getSalesPrice());
      }

      if (StringUtils.isNotBlank(inventoryDTO.getUnit())) {
        inventory.setUnit(inventoryDTO.getUnit());
      }
      if (inventoryDTO.getStoreHouseInventoryDTO() != null) {
        IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
        storeHouseService.saveOrUpdateStoreHouseInventoryDTO(writer,inventoryDTO.getStoreHouseInventoryDTO());
        Double inventoryTotalAmount = storeHouseService.sumStoreHouseAllInventoryAmountByProductLocalInfoId(shopId, inventory.getId());
        inventory.setAmount(inventoryTotalAmount);
      } else if (inventoryDTO.getAmount() != null) {
        inventory.setAmount(inventoryDTO.getAmount());
      }
      if (inventoryDTO.getInventoryAveragePrice() != null) {
        inventory.setInventoryAveragePrice(inventoryDTO.getInventoryAveragePrice());
      }
      writer.update(inventory);
      writer.commit(status);
      caculateAfterLimit(inventory.toDTO(), inventoryLimitDTO);
      return inventory.toDTO();
    } catch(Exception e){
      LOG.error("InventoryService.updateInventoryInfo报错. inventoryID:{}, shopId:{}", inventoryDTO.getId(), shopId);
      LOG.error(e.getMessage(), e);
      return null;
    } finally {
      writer.rollback(status);
    }
  }

	@Override
  public MemcacheLimitDTO getMemcacheLimitDTO(Long shopId) throws Exception {
    if (shopId == null) {
      LOG.error("shop id is null,can't get mencacheLimitDTO");
      return null;
    }
    String key = MemcachePrefix.inventoryLimit.getValue() + shopId.toString();
    MemcacheLimitDTO memcacheLimitDTO = (MemcacheLimitDTO) MemCacheAdapter.get(key);
    if (memcacheLimitDTO == null) {
      memcacheLimitDTO = saveMemcacheLimitDTOFromDB(shopId);
    }
    return memcacheLimitDTO;
  }

  @Override
  public MemcacheLimitDTO saveMemcacheLimitDTOFromDB(Long shopId) throws Exception {
    if (shopId == null) {
      LOG.error("shop id is null,can't get mencacheLimitDTO");
      return null;
    }
	  ISearchService searchService = ServiceManager.getService(ISearchService.class);
    String key = MemcachePrefix.inventoryLimit.getValue() + shopId.toString();
    MemcacheLimitDTO memcacheLimitDTO;
    memcacheLimitDTO = new MemcacheLimitDTO();
    memcacheLimitDTO.setShopId(shopId);
    Long startTime = System.currentTimeMillis();
    Integer lowerLimit = searchService.countInventoryLowerLimitAmount(shopId);
    Integer upperLimit = searchService.countInventoryUpperLimitAmount(shopId);
    Long endTime = System.currentTimeMillis();
    LOG.debug("shopId = {} 执行了一次全局count InventoryLimitAmout 共耗时 :{} ms", shopId, (endTime - startTime));
    memcacheLimitDTO.setCurrentLowerLimitAmount(lowerLimit);
    memcacheLimitDTO.setCurrentUpperLimitAmount(upperLimit);
    boolean flag = MemCacheAdapter.set(key, memcacheLimitDTO);
    if (!flag) {
      LOG.error("memcache add inventoryLimitInfo error:shopId = " + shopId
          + "  memcacheLimitDTO =" + memcacheLimitDTO.toString() + " please check memcache status");
    }
    return memcacheLimitDTO;
  }


  @Override
  public MemcacheLimitDTO updateMemocacheLimitByInventoryLimitDTO(Long shopId, InventoryLimitDTO inventoryLimitDTO) throws Exception {
    if (shopId == null) {
      LOG.error("shop id is null,can't get mencacheLimitDTO");
      return null;
    }
    String key = MemcachePrefix.inventoryLimit.getValue() + shopId.toString();
    MemcacheLimitDTO memcacheLimitDTO = (MemcacheLimitDTO) MemCacheAdapter.get(key);
    if (checkMemcacheLimit(memcacheLimitDTO, inventoryLimitDTO)) {
      memcacheLimitDTO.setCurrentLowerLimitAmount(inventoryLimitDTO.getCurrentLowerLimitAmount());
      memcacheLimitDTO.setCurrentUpperLimitAmount(inventoryLimitDTO.getCurrentUpperLimitAmount());
      boolean flag = MemCacheAdapter.set(key, memcacheLimitDTO);
      if (!flag) {
        LOG.error("memcache add inventoryLimitInfo error:shopId = " + shopId
            + "  memcacheLimitDTO =" + memcacheLimitDTO.toString() + " please check memcache status");
      }
    } else {        //check memocacheLimitDTO 或者 inventoryLimitDTO数据异常，从DB中更新memcache
      memcacheLimitDTO = saveMemcacheLimitDTOFromDB(shopId);
    }
    return memcacheLimitDTO;
  }

  /**
   * 检查memcacheLimitDTO 和inventoryLimitDTO 容器中数据关系 来判断是否用inventoryLimitDTO 中数据更新memcacheLimit
   *
   * @param memcacheLimitDTO
   * @param inventoryLimitDTO
   * @return true 表示可以更新，false 表示数据异常
   */
  private boolean checkMemcacheLimit(MemcacheLimitDTO memcacheLimitDTO, InventoryLimitDTO inventoryLimitDTO) {
    boolean flag = true;
    if (memcacheLimitDTO == null || inventoryLimitDTO == null) {
      flag = false;
      return flag;
    }
    int currentLowerLimitAmount = memcacheLimitDTO.getCurrentLowerLimitAmount() == null ? 0 : memcacheLimitDTO.getCurrentLowerLimitAmount();
    int currentUpperLimitAmount = memcacheLimitDTO.getCurrentUpperLimitAmount() == null ? 0 : memcacheLimitDTO.getCurrentUpperLimitAmount();
    int inventoryPreviouLowerAmount = inventoryLimitDTO.getPreviousLowerLimitAmount() == null ? 0 : inventoryLimitDTO.getPreviousLowerLimitAmount();
    int inventoryPreviouUpperAmount = inventoryLimitDTO.getPreviousUpperLimitAmount() == null ? 0 : inventoryLimitDTO.getPreviousUpperLimitAmount();
    //事件处理前的limit数量大于当前memcacheLimitDTO 中数据时，memcacheLimit 中数据异常，需要更新
    if (inventoryPreviouLowerAmount > currentLowerLimitAmount || inventoryPreviouUpperAmount > currentUpperLimitAmount) {
      flag = false;
      return flag;
    }
    int lowerAmountChange = (inventoryLimitDTO.getAfterLowerLimitAmount() == null ? 0 : inventoryLimitDTO.getAfterLowerLimitAmount())
        - (inventoryLimitDTO.getPreviousLowerLimitAmount() == null ? 0 : inventoryLimitDTO.getPreviousLowerLimitAmount());
    int upperAmountChange = (inventoryLimitDTO.getAfterUpperLimitAmount() == null ? 0 : inventoryLimitDTO.getAfterUpperLimitAmount())
        - (inventoryLimitDTO.getPreviousUpperLimitAmount() == null ? 0 : inventoryLimitDTO.getPreviousUpperLimitAmount());
    //事件处理之后limitchange数量大于当前memcacheLimitDTO 中数据时，memcacheLimit 中数据异常，需要更新
    if (lowerAmountChange + currentLowerLimitAmount < 0 || upperAmountChange + currentUpperLimitAmount < 0) {
      flag = false;
      return flag;
    }
    inventoryLimitDTO.setLowerLimitChangeAmount(lowerAmountChange);
    inventoryLimitDTO.setUpperLimitChangeAmount(upperAmountChange);
    inventoryLimitDTO.setCurrentLowerLimitAmount(currentLowerLimitAmount + lowerAmountChange);
    inventoryLimitDTO.setCurrentUpperLimitAmount(currentUpperLimitAmount + upperAmountChange);
    return flag;
  }

  /**
   * 单据入库后保存应付款表
   *
   * @param purchaseInventoryDTO
   * @return
   */
  @Override
  public PayableDTO savePayableFromPurchaseInventoryDTO(PurchaseInventoryDTO purchaseInventoryDTO) {
    SupplierPayableService supplierPayableService = ServiceManager.getService(SupplierPayableService.class);
    if (purchaseInventoryDTO == null) return null;
    PurchaseInventoryItemDTO[] purchaseInventoryItemDTOs = purchaseInventoryDTO.getItemDTOs();
    if (purchaseInventoryItemDTOs == null || purchaseInventoryItemDTOs.length == 0) return null;
    StringBuffer materialName = new StringBuffer();
    for (PurchaseInventoryItemDTO p : purchaseInventoryItemDTOs) {
      materialName.append(p.getProductName()).append(";");
    }
    PayableDTO payableDTO = new PayableDTO();
    payableDTO.setAmount(NumberUtil.numberValue(purchaseInventoryDTO.getTotal(), 0d));
    payableDTO.setMaterialName(materialName.toString());
    payableDTO.setPayTime(purchaseInventoryDTO.getVestDate());   //使用单据归属时间
    payableDTO.setPurchaseInventoryId(purchaseInventoryDTO.getId());
    payableDTO.setShopId(purchaseInventoryDTO.getShopId());
    payableDTO.setSupplierId(purchaseInventoryDTO.getSupplierId());
    payableDTO.setReceiptNo(purchaseInventoryDTO.getReceiptNo());
    //payableDTO.setCreditAmount(NumberUtil.numberValue(purchaseInventoryDTO.getTotal(),0D));     //先将应付款设为设置为总额，然后再执行付款操作
//    if (SURPAY.equals(purchaseInventoryDTO.getPaidtype())) {         //如果是付款详细页面进行付款
      /* 扣款*/
      payableDTO.setDeduction(NumberUtil.numberValue(purchaseInventoryDTO.getDeduction(), 0D));
      /*欠款挂账*/
      payableDTO.setCreditAmount(NumberUtil.numberValue(purchaseInventoryDTO.getCreditAmount(), 0D));
      /*已付*/
      payableDTO.setPaidAmount(NumberUtil.numberValue(purchaseInventoryDTO.getActuallyPaid(), 0D));
      /*现金*/
      payableDTO.setCash(NumberUtil.numberValue(purchaseInventoryDTO.getCash(), 0D));

//    } else {    //如果是入库单页面进行付款
//      /* 入库单页面扣款*/
//      payableDTO.setDeduction(NumberUtil.numberValue(purchaseInventoryDTO.getStroageSupplierDeduction(), 0d));
//      /*入库单页面欠款挂账*/
//      payableDTO.setCreditAmount(NumberUtil.numberValue(purchaseInventoryDTO.getStroageCreditAmount(), 0D));
//      /*实付*/
//      payableDTO.setPaidAmount(NumberUtil.numberValue(purchaseInventoryDTO.getStroageActuallyPaid(), 0D));
//
//      /*现金*/
//      payableDTO.setCash(NumberUtil.numberValue(purchaseInventoryDTO.getStroageActuallyPaid(), 0D));
//    }

    /*银行卡*/
    payableDTO.setBankCard(NumberUtil.numberValue(purchaseInventoryDTO.getBankCardAmount(), 0D));
    /*支票*/
    payableDTO.setCheque(NumberUtil.numberValue(purchaseInventoryDTO.getCheckAmount(), 0d));
    /*定金*/
    payableDTO.setDeposit(NumberUtil.numberValue(purchaseInventoryDTO.getDepositAmount(), 0D));

    if(null == payableDTO.getStrikeAmount())
    {
      payableDTO.setStrikeAmount(0D);
    }
    payableDTO.setLastPayer(purchaseInventoryDTO.getUserName());
    payableDTO.setLastPayerId(purchaseInventoryDTO.getUserId());

    payableDTO.setStatus(PayStatus.USE);
    payableDTO.setOrderType(OrderTypes.INVENTORY);
    payableDTO.setOrderDebtType(OrderDebtType.SUPPLIER_DEBT_PAYABLE);
    payableDTO.setOrderType(OrderTypes.INVENTORY);
    /*保存应付款表*/
    payableDTO = supplierPayableService.savePayable(payableDTO);
    return payableDTO;
  }

  @Override
  public Map<Long, PayableDTO> getPayableDTOByPurchaseInventoryId(Long shopId,Long... purchaseInventoryId) {
    TxnWriter writer = txnDaoManager.getWriter();
    Map<Long, PayableDTO> payableDTOMap = new HashMap<Long, PayableDTO>();
    if (purchaseInventoryId != null && purchaseInventoryId.length > 0) {
      List<Payable> payableList = writer.getPayableByPurchaseInventoryId(shopId, purchaseInventoryId);
      if (CollectionUtils.isNotEmpty(payableList)) {
        for (Payable payable : payableList) {
          payableDTOMap.put(payable.getPurchaseInventoryId(), payable.toDTO());
        }
      }
    }
    return payableDTOMap;
  }

   /**
   * 计算库存平均价  最新入库平均价=（系统中的库存平均价*系统中的库存量+当前入库价*当前入库量）（/ 系统中的库存量+当前入库量），
   * @param formerInventoryAveragePrice 系统中的库存平均价
   * @param formerInventoryAmount  系统中的库存量
   * @param currentInventoryPrice  当前入库价
   * @param currentInventoryAmount  当前入库量
   * @return
   */
  public double calculateInventoryAveragePrice(double formerInventoryAveragePrice,double formerInventoryAmount,double currentInventoryPrice,double currentInventoryAmount)
  {
    BigDecimal formerInventoryAveragePrice_BigBigDecimal = new BigDecimal(formerInventoryAveragePrice);
    BigDecimal formerInventoryAmount_BigBigDecimal = new BigDecimal(formerInventoryAmount);
    BigDecimal currentInventoryPrice_BigBigDecimal = new BigDecimal(currentInventoryPrice);
    BigDecimal currentInventoryAmount_BigBigDecimal = new BigDecimal(currentInventoryAmount);

    BigDecimal inventoryTotal = formerInventoryAveragePrice_BigBigDecimal.multiply(formerInventoryAmount_BigBigDecimal);
    BigDecimal currentInventoryTotal = currentInventoryPrice_BigBigDecimal.multiply(currentInventoryAmount_BigBigDecimal);

    BigDecimal total = inventoryTotal.add(currentInventoryTotal);
    BigDecimal totalAmount = formerInventoryAmount_BigBigDecimal.add(currentInventoryAmount_BigBigDecimal);
    if (totalAmount.doubleValue() - 0d < 0.0001) {
      return formerInventoryAveragePrice;
    }
    BigDecimal latestInventoryPrice = total.divide(totalAmount,2,BigDecimal.ROUND_HALF_UP);
    return  latestInventoryPrice.doubleValue();

  }

	@Override
	public boolean isInventoryEmpty(Long shopId, Long productId)throws Exception{
		TxnWriter writer = txnDaoManager.getWriter();
		Inventory inventory = writer.getInventoryByIdAndshopId(productId,shopId);
		if(inventory == null || inventory.getAmount()<0.001){
			return true;
		}else {
			return false;
		}
	}

	@Override
	public Map<Long, InventoryDTO> getInventoryDTOMap(Long shopId, Set<Long> productIds) {
		if (shopId == null || CollectionUtils.isEmpty(productIds)) {
			return new HashMap<Long, InventoryDTO>();
		}
		TxnWriter writer = txnDaoManager.getWriter();

		List<Inventory> inventories = writer.getInventoryByIds(shopId, productIds.toArray(new Long[productIds.size()]));
		Map<Long, InventoryDTO> inventoryMap = new HashMap<Long, InventoryDTO>((int) (productIds.size() / 0.75f) + 1, 0.75f);
		for (Inventory inventory : inventories) {
			inventoryMap.put(inventory.getId(), inventory.toDTO());
		}
		return inventoryMap;
	}
	@Override
	public Map<Long, InventoryDTO> getInventoryDTOMapByProductIds(Long... productIds) {
		if (ArrayUtils.isEmpty(productIds)) {
			return new HashMap<Long, InventoryDTO>();
		}
		TxnWriter writer = txnDaoManager.getWriter();

		List<Inventory> inventories = writer.getInventoryByProductIds(productIds);
		Map<Long, InventoryDTO> inventoryMap = new HashMap<Long, InventoryDTO>((int) (productIds.length / 0.75f) + 1, 0.75f);
		for (Inventory inventory : inventories) {
			inventoryMap.put(inventory.getId(), inventory.toDTO());
		}
		return inventoryMap;
	}

	@Override
	public Map<Long, Inventory> getInventoryMap(Long shopId, Set<Long> productIds) {
		if (shopId == null || CollectionUtils.isEmpty(productIds)) {
			return new HashMap<Long, Inventory>();
		}
		TxnWriter writer = txnDaoManager.getWriter();

		List<Inventory> inventories = writer.getInventoryByIds(shopId, productIds.toArray(new Long[productIds.size()]));
		Map<Long, Inventory> inventoryMap = new HashMap<Long, Inventory>((int) (productIds.size() / 0.75f) + 1, 0.75f);
		for (Inventory inventory : inventories) {
			inventoryMap.put(inventory.getId(), inventory);
		}
		return inventoryMap;
	}

  @Override
  public InventoryDTO getInventoryDTOByProductId(Long productId){
    TxnWriter writer = txnDaoManager.getWriter();
    Inventory inventory = writer.getById(Inventory.class,productId);
    if(inventory!=null){
      return inventory.toDTO();
    }else{
      return null;
    }
  }

	@Override
	public boolean updateSaleOrderLack(Long shopId,Long firstSalesOrderId,Long... productIds) throws Exception {
		boolean isUpdate = false;
		if(shopId == null || ArrayUtils.isEmpty(productIds)){
			return isUpdate;
		}
		Map<Long,List<SalesOrderItem>> salesOrderItemMap =  getGoodSaleService().getLackSalesOrderItemByProductIds(shopId,firstSalesOrderId,productIds);
		if (salesOrderItemMap == null || salesOrderItemMap.isEmpty()) {
			return isUpdate;
		}
		Set<Long> toUpdateProductIds = salesOrderItemMap.keySet();
		InventoryLimitDTO inventoryLimitDTO = new InventoryLimitDTO();
		Map<Long,Inventory> inventoryMap = getInventoryMap(shopId,toUpdateProductIds);
			if (inventoryMap == null || inventoryMap.isEmpty()) {
			return isUpdate;
		}
		Map<Long,InventorySearchIndex> inventorySearchIndexMap = getSearchService().getInventorySearchIndexMapByProductIds(shopId, toUpdateProductIds.toArray(new Long[toUpdateProductIds.size()]));

		TxnWriter writer = txnDaoManager.getWriter();
		Object status = writer.begin();
		try {
			for (Long productId : toUpdateProductIds) {
				Inventory inventory = inventoryMap.get(productId);
				List<SalesOrderItem> salesOrderItems = salesOrderItemMap.get(productId);
				if (CollectionUtils.isNotEmpty(salesOrderItems)) {
					caculateBeforeLimit(inventory.toDTO(), inventoryLimitDTO);
					for (SalesOrderItem salesOrderItem : salesOrderItems) {
						double lackAmount = salesOrderItem.getAmount() - NumberUtil.doubleVal(salesOrderItem.getReserved());
						if (inventory.getAmount() + 0.0001 > lackAmount) {
							salesOrderItem.setReserved(salesOrderItem.getAmount());
							writer.update(salesOrderItem);

							isUpdate = true;
							inventory.setAmount(inventory.getAmount() - lackAmount);
						} else if (inventory.getAmount() > 0) {
							double toReserved = inventory.getAmount();
							salesOrderItem.setReserved(salesOrderItem.getReserved() + toReserved);
							writer.update(salesOrderItem);
							inventory.setAmount(0);
						}
					}
					writer.update(inventory);
					caculateAfterLimit(inventory.toDTO(), inventoryLimitDTO);
					InventorySearchIndex inventorySearchIndex = inventorySearchIndexMap.get(productId);
					if(inventorySearchIndex != null) {
						inventorySearchIndex.setAmount(inventory.getAmount());
            getSearchService().updateInventorySearchIndex(inventorySearchIndex.toDTO());
					}
				}
			}
			writer.commit(status);
			updateMemocacheLimitByInventoryLimitDTO(shopId, inventoryLimitDTO);
			getSearchService().addOrUpdateInventorySearchIndexWithList(new ArrayList<InventorySearchIndex>(inventorySearchIndexMap.values()));
      ServiceManager.getService(IProductSolrWriterService.class).createProductSolrIndex(shopId, productIds);
		} finally {
			writer.rollback(status);
		}
		return isUpdate;
	}

  @Override
  public boolean updateSaleOrderLackBySaleOrderId(TxnWriter writer,SalesOrderDTO salesOrderDTO,List<InventorySearchIndex> inventorySearchIndexes) throws Exception {
    boolean isUpdate = false;
    if (salesOrderDTO == null) {
      return isUpdate;
    }
    Map<Long, List<SalesOrderItem>> salesOrderItemMap = getGoodSaleService().getSalesOrderItemsByOrderId(salesOrderDTO.getId());
    if (salesOrderItemMap == null || salesOrderItemMap.isEmpty()) {
      return isUpdate;
    }
    Set<Long> toUpdateProductIds = salesOrderItemMap.keySet();
    InventoryLimitDTO inventoryLimitDTO = new InventoryLimitDTO();
    Map<Long, Inventory> inventoryMap = getInventoryMap(salesOrderDTO.getShopId(), toUpdateProductIds);
    if (inventoryMap == null || inventoryMap.isEmpty()) {
      return isUpdate;
    }
    Map<Long, InventorySearchIndex> inventorySearchIndexMap = getSearchService().getInventorySearchIndexMapByProductIds(salesOrderDTO.getShopId(), toUpdateProductIds.toArray(new Long[toUpdateProductIds.size()]));
    List<SalesOrderItemDTO> salesOrderItemDTOList = new ArrayList<SalesOrderItemDTO>();
    for (Long productId : toUpdateProductIds) {
      Inventory inventory = inventoryMap.get(productId);
      List<SalesOrderItem> salesOrderItems = salesOrderItemMap.get(productId);
      if (CollectionUtils.isNotEmpty(salesOrderItems)) {
        caculateBeforeLimit(inventory.toDTO(), inventoryLimitDTO);
        for (SalesOrderItem salesOrderItem : salesOrderItems) {
          double lackAmount = salesOrderItem.getAmount() - NumberUtil.doubleVal(salesOrderItem.getReserved());
          if (inventory.getAmount() + 0.0001 > lackAmount) {
            salesOrderItem.setReserved(salesOrderItem.getAmount());
            writer.update(salesOrderItem);

            isUpdate = true;
            inventory.setAmount(inventory.getAmount() - lackAmount);
          } else if (inventory.getAmount() > 0) {
            double toReserved = inventory.getAmount();
            salesOrderItem.setReserved(salesOrderItem.getReserved() + toReserved);
            writer.update(salesOrderItem);
            inventory.setAmount(0);
          }
          salesOrderItemDTOList.add(salesOrderItem.toDTO());
        }
        writer.update(inventory);
        salesOrderDTO.setItemDTOs(salesOrderItemDTOList.toArray(new SalesOrderItemDTO[salesOrderItemDTOList.size()]));
        caculateAfterLimit(inventory.toDTO(), inventoryLimitDTO);
        InventorySearchIndex inventorySearchIndex = inventorySearchIndexMap.get(productId);
        if (inventorySearchIndex != null) {
          inventorySearchIndex.setAmount(inventory.getAmount());
        }
      }
    }
    updateMemocacheLimitByInventoryLimitDTO(salesOrderDTO.getShopId(), inventoryLimitDTO);
    inventorySearchIndexes.addAll(inventorySearchIndexMap.values());
    return isUpdate;
  }
  @Override
  public boolean updateSaleOrderLackBySaleOrderIdAndStorehouse(TxnWriter writer,SalesOrderDTO salesOrderDTO,List<InventorySearchIndex> inventorySearchIndexes) throws Exception {
    boolean isUpdate = false;
    if (salesOrderDTO == null) {
      return isUpdate;
    }
    Map<Long, List<SalesOrderItem>> salesOrderItemMap = getGoodSaleService().getSalesOrderItemsByOrderId(salesOrderDTO.getId());
    if (salesOrderItemMap == null || salesOrderItemMap.isEmpty()) {
      return isUpdate;
    }
    Set<Long> toUpdateProductIds = salesOrderItemMap.keySet();
    InventoryLimitDTO inventoryLimitDTO = new InventoryLimitDTO();
    Map<Long, Inventory> inventoryMap = getInventoryMap(salesOrderDTO.getShopId(), toUpdateProductIds);
    IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
    Map<Long,StoreHouseInventoryDTO> storeHouseInventoryDTOMap = storeHouseService.getStoreHouseInventoryDTOMapByStorehouseAndProductIds(salesOrderDTO.getShopId(), salesOrderDTO.getStorehouseId(), toUpdateProductIds.toArray(new Long[toUpdateProductIds.size()]));

    if (inventoryMap == null || inventoryMap.isEmpty()) {
      return isUpdate;
    }
    Map<Long, InventorySearchIndex> inventorySearchIndexMap = getSearchService().getInventorySearchIndexMapByProductIds(salesOrderDTO.getShopId(), toUpdateProductIds.toArray(new Long[toUpdateProductIds.size()]));
    List<SalesOrderItemDTO> salesOrderItemDTOList = new ArrayList<SalesOrderItemDTO>();

    for (Long productId : toUpdateProductIds) {
      Inventory inventory = inventoryMap.get(productId);
      StoreHouseInventoryDTO storeHouseInventoryDTO = storeHouseInventoryDTOMap.get(productId);
      if(storeHouseInventoryDTO==null){
        storeHouseInventoryDTO = new StoreHouseInventoryDTO(salesOrderDTO.getStorehouseId(),productId,0d);
      }
      List<SalesOrderItem> salesOrderItems = salesOrderItemMap.get(productId);
      if (CollectionUtils.isNotEmpty(salesOrderItems)) {
        caculateBeforeLimit(inventory.toDTO(), inventoryLimitDTO);
        for (SalesOrderItem salesOrderItem : salesOrderItems) {
          double lackAmount = salesOrderItem.getAmount() - NumberUtil.doubleVal(salesOrderItem.getReserved());
          if (storeHouseInventoryDTO.getAmount() + 0.0001 > lackAmount) {
            salesOrderItem.setReserved(salesOrderItem.getAmount());
            writer.update(salesOrderItem);

            isUpdate = true;
            storeHouseInventoryDTO.setChangeAmount(lackAmount*-1);
            inventory.setAmount(inventory.getAmount() - lackAmount);
            storeHouseService.saveOrUpdateStoreHouseInventoryDTO(writer,storeHouseInventoryDTO);
          } else if (storeHouseInventoryDTO.getAmount() > 0) {
            double toReserved = storeHouseInventoryDTO.getAmount();
            salesOrderItem.setReserved(salesOrderItem.getReserved() + toReserved);
            writer.update(salesOrderItem);
            inventory.setAmount(inventory.getAmount()-toReserved);
            storeHouseInventoryDTO.setAmount(0d);
            storeHouseService.saveOrUpdateStoreHouseInventoryDTO(writer,storeHouseInventoryDTO);
          }
          salesOrderItemDTOList.add(salesOrderItem.toDTO());
        }
        writer.update(inventory);
        salesOrderDTO.setItemDTOs(salesOrderItemDTOList.toArray(new SalesOrderItemDTO[salesOrderItemDTOList.size()]));
        caculateAfterLimit(inventory.toDTO(), inventoryLimitDTO);
        InventorySearchIndex inventorySearchIndex = inventorySearchIndexMap.get(productId);
        if (inventorySearchIndex != null) {
          inventorySearchIndex.setAmount(inventory.getAmount());
        }
      }
    }
    updateMemocacheLimitByInventoryLimitDTO(salesOrderDTO.getShopId(), inventoryLimitDTO);
    inventorySearchIndexes.addAll(inventorySearchIndexMap.values());
    return isUpdate;
  }

  @Override
  public boolean updateSaleOrderLackByStoreHouse(Long shopId,Long storehouseId,Long firstSalesOrderId, Long... productIds) throws Exception {
    boolean isUpdate = false;
    if(shopId == null || ArrayUtils.isEmpty(productIds)){
      return isUpdate;
    }
    Map<Long,List<SalesOrderItem>> salesOrderItemMap =  getGoodSaleService().getLackSalesOrderItemByProductIdsAndStorehouse(shopId,firstSalesOrderId,storehouseId,productIds);
    if (salesOrderItemMap == null || salesOrderItemMap.isEmpty()) {
      return isUpdate;
    }
    Set<Long> toUpdateProductIds = salesOrderItemMap.keySet();
    InventoryLimitDTO inventoryLimitDTO = new InventoryLimitDTO();
    Map<Long,Inventory> inventoryMap = getInventoryMap(shopId,toUpdateProductIds);
    IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
    Map<Long,StoreHouseInventoryDTO> storeHouseInventoryDTOMap = storeHouseService.getStoreHouseInventoryDTOMapByStorehouseAndProductIds(shopId, storehouseId, productIds);
    if (MapUtils.isEmpty(inventoryMap)) {
      return isUpdate;
    }
    Map<Long,InventorySearchIndex> inventorySearchIndexMap = getSearchService().getInventorySearchIndexMapByProductIds(shopId, toUpdateProductIds.toArray(new Long[toUpdateProductIds.size()]));

    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      Double oldStoreHouseInventoryAmount = 0d;
      for (Long productId : toUpdateProductIds) {
        Inventory inventory = inventoryMap.get(productId);
        StoreHouseInventoryDTO storeHouseInventoryDTO = storeHouseInventoryDTOMap.get(productId);
        if(storeHouseInventoryDTO==null){
          continue;
        }
        oldStoreHouseInventoryAmount = storeHouseInventoryDTO.getAmount();
        List<SalesOrderItem> salesOrderItems = salesOrderItemMap.get(productId);
        if (CollectionUtils.isNotEmpty(salesOrderItems)) {
          caculateBeforeLimit(inventory.toDTO(), inventoryLimitDTO);
          for (SalesOrderItem salesOrderItem : salesOrderItems) {
            double lackAmount = salesOrderItem.getAmount() - NumberUtil.doubleVal(salesOrderItem.getReserved());
            if (storeHouseInventoryDTO.getAmount() + 0.0001 > lackAmount) {
              salesOrderItem.setReserved(salesOrderItem.getAmount());
              writer.update(salesOrderItem);
              isUpdate = true;
              storeHouseInventoryDTO.setAmount(storeHouseInventoryDTO.getAmount() - lackAmount);
            } else if (storeHouseInventoryDTO.getAmount() > 0) {
              double toReserved = storeHouseInventoryDTO.getAmount();
              salesOrderItem.setReserved(salesOrderItem.getReserved() + toReserved);
              writer.update(salesOrderItem);
              storeHouseInventoryDTO.setAmount(0D);
            }
          }
          storeHouseService.saveOrUpdateStoreHouseInventoryDTO(writer,storeHouseInventoryDTO);
          inventory.setAmount(inventory.getAmount()-oldStoreHouseInventoryAmount+storeHouseInventoryDTO.getAmount());
          writer.update(inventory);
          caculateAfterLimit(inventory.toDTO(), inventoryLimitDTO);
          InventorySearchIndex inventorySearchIndex = inventorySearchIndexMap.get(productId);
          if(inventorySearchIndex != null) {
            inventorySearchIndex.setAmount(inventory.getAmount());
            getSearchService().updateInventorySearchIndex(inventorySearchIndex.toDTO());
          }
        }
      }
      writer.commit(status);
      updateMemocacheLimitByInventoryLimitDTO(shopId, inventoryLimitDTO);
      getSearchService().addOrUpdateInventorySearchIndexWithList(new ArrayList<InventorySearchIndex>(inventorySearchIndexMap.values()));
      ServiceManager.getService(IProductSolrWriterService.class).createProductSolrIndex(shopId,productIds);
    } finally {
      writer.rollback(status);
    }
    return isUpdate;
  }

  @Override
  public List<InventoryDTO> getInventoryDTOsByShopId(Long shopId,int start,int rows) {
    TxnWriter writer = txnDaoManager.getWriter();
    List<Inventory> inventoryList = writer.getInventoryDTOsByShopId(shopId, start, rows);
    if(CollectionUtils.isNotEmpty(inventoryList)){
      List<InventoryDTO> inventoryDTOList = new ArrayList<InventoryDTO>();
      for(Inventory inventory : inventoryList){
        inventoryDTOList.add(inventory.toDTO());
      }
      return inventoryDTOList;
    }
    return null;
  }

  @Override
  public boolean checkBatchProductInventory(Long shopId,BcgogoOrderDto bcgogoOrderDto,Map<String, String> data, List<Long> productIdList) throws Exception {
    boolean success = true;
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    List<InventoryDTO> inventoryDTOList = txnService.getInventoryByShopIdAndProductIds(shopId, productIdList.toArray(new Long[productIdList.size()]));
    Set<Long> productIdSet = new HashSet<Long>();
    productIdSet.addAll(productIdList);
    Map<Long,ProductDTO> productDTOMap = getProductService().getProductDTOMapByProductLocalInfoIds(shopId,productIdSet);
    Map<Long, Double> inventoryAmoutMap = new HashMap<Long, Double>();
    if (CollectionUtils.isNotEmpty(inventoryDTOList)) {
      for (InventoryDTO inventoryDTO : inventoryDTOList) {
        inventoryAmoutMap.put(inventoryDTO.getId(), inventoryDTO.getAmount());
      }
    }
    Double inventoryAmout = null,reserved = null;
    ProductDTO productDTO = null;
    for (BcgogoOrderItemDto itemDTO : bcgogoOrderDto.getItemDTOs()) {
      if (itemDTO.getProductId() == null) continue;
      productDTO = productDTOMap.get(itemDTO.getProductId());
      if (productDTO == null) continue;
      inventoryAmout = inventoryAmoutMap.get(itemDTO.getProductId());
      inventoryAmout = (inventoryAmout == null ? 0d : inventoryAmout);
      reserved = itemDTO.getReserved() == null ? 0d : itemDTO.getReserved();
      if (UnitUtil.isStorageUnit(itemDTO.getUnit(), productDTO)) {
        if ((itemDTO.getAmount() - reserved) * productDTO.getRate() > inventoryAmout) {
          success = false;
        }
      } else {
        if (itemDTO.getAmount() - reserved > inventoryAmout) {
          success = false;
        }
      }
      data.put(itemDTO.getProductId().toString(), inventoryAmout.toString());
    }
    return success;
  }

  @Override
  public boolean checkBatchPurchaseInventoryInventory(Long shopId, BcgogoOrderDto bcgogoOrderDto, Map<String, String> data, List<Long> productIdList) throws Exception {
    boolean success = true;
    IInventoryService inventoryService = ServiceManager.getService(IInventoryService.class);
    Set<Long> productIdSet = new HashSet<Long>();
    productIdSet.addAll(productIdList);
    Map<Long, InventoryDTO> inventoryDTOMap = inventoryService.getInventoryDTOMap(shopId, productIdSet);
    Map<Long, ProductDTO> productDTOMap = getProductService().getProductDTOMapByProductLocalInfoIds(shopId, productIdSet);
    Map<Long, Double> inventoryAmountMap = new HashMap<Long, Double>();
    if (MapUtils.isNotEmpty(inventoryDTOMap)) {
      for (InventoryDTO inventoryDTO : inventoryDTOMap.values()) {
        inventoryAmountMap.put(inventoryDTO.getId(), inventoryDTO.getAmount());
        data.put(inventoryDTO.getId().toString(), ((Double) NumberUtil.doubleVal(inventoryDTO.getAmount())).toString());
      }
    }

    ProductDTO productDTO = null;
    for (BcgogoOrderItemDto itemDTO : bcgogoOrderDto.getItemDTOs()) {
      double inventoryAmount = 0d, reserved = 0d, itemAmount = 0;
      if (itemDTO.getProductId() == null) continue;
      productDTO = productDTOMap.get(itemDTO.getProductId());
      inventoryAmount = NumberUtil.doubleVal(inventoryAmountMap.get(itemDTO.getProductId()));
      reserved = NumberUtil.doubleVal(itemDTO.getReserved());
      itemAmount = NumberUtil.doubleVal(itemDTO.getAmount());

      if (UnitUtil.isStorageUnit(itemDTO.getUnit(), productDTO)) {
        itemAmount = itemAmount * productDTO.getRate();
        reserved = reserved * productDTO.getRate();
      }
      if (itemAmount - reserved > inventoryAmount) {
        success = false;
      }
      inventoryAmount = inventoryAmount - itemAmount;
      inventoryAmountMap.put(itemDTO.getProductId(), inventoryAmount);
    }
    return success;
  }



  @Override
  public boolean checkBatchProductInventoryInOtherStorehouse(Long shopId,BcgogoOrderDto bcgogoOrderDto,List<Long> productIdList) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    boolean success = false;
    double count = writer.sumStoreHouseInventoryInOtherStorehouseByProductIds(shopId,bcgogoOrderDto.getStorehouseId(),productIdList.toArray(new Long[productIdList.size()]));
    if(count>0){
      success= true;
    }
    return success;
  }

  @Override
  public boolean checkBatchProductInventoryByStoreHouse(Long shopId,Long storehouseId,BcgogoOrderItemDto[] bcgogoOrderItemDtos,Map<String, String> data, List<Long> productIdList) throws Exception {
    boolean success = true;
    IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
    Set<Long> productIdSet = new HashSet<Long>();
    productIdSet.addAll(productIdList);
    Map<Long,ProductDTO> productDTOMap = getProductService().getProductDTOMapByProductLocalInfoIds(shopId,productIdSet);

    Map<Long,StoreHouseInventoryDTO> storeHouseInventoryDTOMap = storeHouseService.getStoreHouseInventoryDTOMapByStorehouseAndProductIds(shopId, storehouseId, productIdSet.toArray(new Long[productIdSet.size()]));
    Double inventoryAmout = null,reserved = null;
    ProductDTO productDTO = null;StoreHouseInventoryDTO storeHouseInventoryDTO = null;
    productIdList.clear();
    for (BcgogoOrderItemDto itemDTO : bcgogoOrderItemDtos) {
      if (itemDTO.getProductId() == null) continue;
      productDTO = productDTOMap.get(itemDTO.getProductId());
      if (productDTO == null) continue;
      storeHouseInventoryDTO = storeHouseInventoryDTOMap.get(itemDTO.getProductId());
      inventoryAmout = (storeHouseInventoryDTO == null ? 0d : storeHouseInventoryDTO.getAmount());
      reserved = itemDTO.getReserved() == null ? 0d : itemDTO.getReserved();
      if (UnitUtil.isStorageUnit(itemDTO.getUnit(), productDTO)) {
        inventoryAmout = inventoryAmout/productDTO.getRate();
      }
      if (itemDTO.getAmount() - reserved > inventoryAmout) {
        productIdList.add(itemDTO.getProductId());
        success = false;
      }
      data.put(itemDTO.getProductId().toString(), NumberUtil.numberValue(inventoryAmout,2).toString());
    }
    return success;
  }

    @Override
    public boolean RFCheckBatchProductInventoryByStoreHouse(Long shopId,Long storehouseId,Long originalStorehouseId,BcgogoOrderItemDto[] bcgogoOrderItemDtos,Map<String, String> data, List<Long> productIdList) throws Exception {
        boolean success = true;
        IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
        Set<Long> productIdSet = new HashSet<Long>();
        productIdSet.addAll(productIdList);
        Map<Long,ProductDTO> productDTOMap = getProductService().getProductDTOMapByProductLocalInfoIds(shopId,productIdSet);

        Map<Long,StoreHouseInventoryDTO> storeHouseInventoryDTOMap = storeHouseService.getStoreHouseInventoryDTOMapByStorehouseAndProductIds(shopId, storehouseId, productIdSet.toArray(new Long[productIdSet.size()]));
        Double inventoryAmout = null,reserved = null;
        ProductDTO productDTO = null;StoreHouseInventoryDTO storeHouseInventoryDTO = null;
        productIdList.clear();
        for (BcgogoOrderItemDto itemDTO : bcgogoOrderItemDtos) {
            if (itemDTO.getProductId() == null) continue;
            productDTO = productDTOMap.get(itemDTO.getProductId());
            if (productDTO == null) continue;
            storeHouseInventoryDTO = storeHouseInventoryDTOMap.get(itemDTO.getProductId());
            inventoryAmout = (storeHouseInventoryDTO == null ? 0d : storeHouseInventoryDTO.getAmount());
            reserved = itemDTO.getReserved() == null ? 0d : itemDTO.getReserved();
            if (UnitUtil.isStorageUnit(itemDTO.getUnit(), productDTO)) {
                inventoryAmout = inventoryAmout/productDTO.getRate();
            }
            if(originalStorehouseId == null || storehouseId.equals(originalStorehouseId)) {
                if (itemDTO.getAmount() - reserved > inventoryAmout) {
                    productIdList.add(itemDTO.getProductId());
                    success = false;
                }
            } else {
                if (itemDTO.getAmount() > inventoryAmout) {
                    productIdList.add(itemDTO.getProductId());
                    success = false;
                }
            }

            data.put(itemDTO.getProductId().toString(), NumberUtil.numberValue(inventoryAmout,2).toString());
        }
        return success;
    }

  @Override
  public void synInventoryWithStoreHouse(Long shopId, Long productLocalInfoId) throws Exception {
    IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
    Double inventoryAmount = storeHouseService.sumStoreHouseAllInventoryAmountByProductLocalInfoId(shopId,productLocalInfoId);
    TxnWriter txnWriter = txnDaoManager.getWriter();
    Inventory inventory = txnWriter.getInventoryByIdAndshopId(productLocalInfoId, shopId);
    if(inventory==null){
      inventory = new Inventory();
      inventory.setShopId(shopId);
    }
    inventory.setAmount(inventoryAmount);
    txnWriter.saveOrUpdate(inventory);
  }

  @Override
  public List<InventoryDTO> getInventoryDTOById(Long shopId,Long ...productLocalInfoIds){
    TxnWriter writer = txnDaoManager.getWriter();
    List<InventoryDTO> inventoryDTOList=new ArrayList<InventoryDTO>();
    List<Inventory> inventoryList=writer.getInventoryByIds(shopId,productLocalInfoIds);
    if(CollectionUtil.isNotEmpty(inventoryList)){
      for(Inventory inventory:inventoryList){
        if(inventory==null) continue;
        inventoryDTOList.add(inventory.toDTO());
      }
    }
    return inventoryDTOList;
  }

  @Override
  public void updateItemDTOInventoryAmountByStorehouse(Long shopId,Long storehouseId, BcgogoOrderDto bcgogoOrderDto) throws Exception {
    IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
    if(!ArrayUtil.isEmpty(bcgogoOrderDto.getItemDTOs())){
      if(storehouseId!=null){
        List<Long> productIdList = bcgogoOrderDto.getProductIdList();
        Map<Long,StoreHouseInventoryDTO> storeHouseInventoryDTOMap = storeHouseService.getStoreHouseInventoryDTOMapByStorehouseAndProductIds(shopId, storehouseId, productIdList.toArray(new Long[productIdList.size()]));
        StoreHouseInventoryDTO storeHouseInventoryDTO = null;
        for(BcgogoOrderItemDto itemDTO : bcgogoOrderDto.getItemDTOs()){
          if(itemDTO.getProductId()==null) continue;
          productIdList.add(itemDTO.getProductId());
          storeHouseInventoryDTO = storeHouseInventoryDTOMap.get(itemDTO.getProductId());
          if(storeHouseInventoryDTO!=null){
            if (UnitUtil.isStorageUnit(itemDTO.getUnit(), itemDTO)) {      //入库单位是库存大单位
              itemDTO.setInventoryAmount((storeHouseInventoryDTO.getAmount()) / itemDTO.getRate());
            } else {
              itemDTO.setInventoryAmount(storeHouseInventoryDTO.getAmount());
            }
            itemDTO.setStorageBin(storeHouseInventoryDTO.getStorageBin());
          }else {
            itemDTO.setInventoryAmount(0d);
            itemDTO.setStorageBin(null);
          }
        }
      }else{
        //不用更新库存   用原来的总库存 但是 货位放空
        for(BcgogoOrderItemDto itemDTO : bcgogoOrderDto.getItemDTOs()){
          if(itemDTO.getProductId()==null) continue;
            itemDTO.setStorageBin(null);
        }
      }
    }
  }

  @Override
  public void updateDraftItemDTOInventoryAmountByStorehouse(Long shopId,Long storehouseId, BcgogoOrderDto bcgogoOrderDto) throws Exception {
    IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
    if(!ArrayUtil.isEmpty(bcgogoOrderDto.getItemDTOs())){
      if(storehouseId!=null){
        List<Long> productIdList = bcgogoOrderDto.getProductIdList();
        Map<Long,StoreHouseInventoryDTO> storeHouseInventoryDTOMap = storeHouseService.getStoreHouseInventoryDTOMapByStorehouseAndProductIds(shopId, storehouseId, productIdList.toArray(new Long[productIdList.size()]));
        StoreHouseInventoryDTO storeHouseInventoryDTO = null;
        for(BcgogoOrderItemDto itemDTO : bcgogoOrderDto.getItemDTOs()){
          if(itemDTO.getProductId()==null) continue;
          productIdList.add(itemDTO.getProductId());
          storeHouseInventoryDTO = storeHouseInventoryDTOMap.get(itemDTO.getProductId());
          if(storeHouseInventoryDTO!=null){
            if (UnitUtil.isStorageUnit(itemDTO.getUnit(), itemDTO)) {      //入库单位是库存大单位
              itemDTO.setInventoryAmount((storeHouseInventoryDTO.getAmount()) / itemDTO.getRate());
            } else {
              itemDTO.setInventoryAmount(storeHouseInventoryDTO.getAmount());
            }
          }else {
            itemDTO.setInventoryAmount(0d);
          }
        }
      }
    }
  }

  @Override
  public void updateInventorySearchIndexByProductId(Long shopId, Long productLocalInfoId, Double recommendedPrice) throws Exception {
    ServiceManager.getService(ISearchService.class).updateInventorySearchIndexByUpdateInfo(productLocalInfoId, recommendedPrice);
//    ServiceManager.getService(IProductSolrService.class).reindexProductInventory(shopId, new Long[]{productLocalInfoId}, false);
    ServiceManager.getService(IProductSolrWriterService.class).createProductSolrIndex(shopId, new Long[]{productLocalInfoId});
  }

  @Override
  public Result updateInventory(Result result,InventoryDTO inventoryDTO){
    if(inventoryDTO==null){
      return result.LogErrorMsg("参数异常。");
    }
    Long id=inventoryDTO.getId();
    Long shopId=inventoryDTO.getShopId();
    if(inventoryDTO.getId()==null||inventoryDTO.getShopId()==null){
      return result.LogErrorMsg("参数异常。");
    }
    TxnWriter writer = txnDaoManager.getWriter();
    Object status=writer.begin();
    try{
      Inventory inventory=CollectionUtil.getFirst(writer.getInventoryByIds(shopId,id));
      if(inventory==null){
        return result.LogErrorMsg("保存异常。");
      }
      if(inventoryDTO.getSalesPrice()!=null)
        inventory.setSalesPrice(inventoryDTO.getSalesPrice());
      if(inventoryDTO.getUnit()!=null)
        inventory.setUnit(inventory.getUnit());
      writer.update(inventory);
      return result;
    }finally {
      writer.rollback(status);
    }
  }

  @Override
  public void addOrUpdateInventorySearchIndexWithList(final Long shopId, final List<InventorySearchIndex> itemList) throws Exception {
    final Long[] invIds = new Long[itemList.size()];
    int i = 0;
    for (InventorySearchIndex inv : itemList) {
      invIds[i++] = inv.getProductId();
    }
    if(CollectionUtils.isNotEmpty(itemList)){
      OrderThreadPool.getInstance().execute(new Runnable() {
        @Override
        public void run() {
          //modify bu xzhu
          try {
            ServiceManager.getService(ISearchService.class).batchAddOrUpdateInventorySearchIndexWithList(shopId,itemList);
            ServiceManager.getService(IProductSolrWriterService.class).createProductSolrIndex(shopId, invIds);
          } catch (Exception e) {
            LOG.error(e.getMessage(), e);
          }
        }
      });
    }
  }

  @Override
  public void updateInventorySearchIndexAmountWithList(Long shopId, List<InventorySearchIndex> itemList) throws Exception {
    ServiceManager.getService(ISearchService.class).updateInventorySearchIndexAmountWithList(itemList);
    Long[] invIds = new Long[itemList.size()];
    int i = 0;
    for (InventorySearchIndex inv : itemList) {
      invIds[i++] = inv.getProductId();
    }
    ServiceManager.getService(IProductSolrWriterService.class).createProductSolrIndex(shopId, invIds);
  }

  @Override
  public boolean checkProductTradePriceAndInventoryAveragePriceByProductLocalInfoId(Long shopId, Long... productLocalInfoIds) throws Exception {
    Set<Long> productLocalInfoIdSet = new HashSet<Long>();
    CollectionUtils.addAll(productLocalInfoIdSet,productLocalInfoIds);
    Map<Long,ProductDTO> productDTOMap = getProductService().getProductDTOMapByProductLocalInfoIds(shopId,productLocalInfoIdSet);
    Map<Long, InventoryDTO> inventoryDTOMap = this.getInventoryDTOMap(shopId,productLocalInfoIdSet);
    InventoryDTO inventoryDTO = null;
    for(ProductDTO productDTO : productDTOMap.values()){
      inventoryDTO = inventoryDTOMap.get(productDTO.getProductLocalInfoId());
      if(inventoryDTO!=null){
        if(NumberUtil.doubleVal(productDTO.getTradePrice())<= NumberUtil.doubleVal(inventoryDTO.getInventoryAveragePrice())){
          return false;
        }
      }
    }
    return true;
  }

  @Override
  public int countProductInventory(Long shopId) {
    return txnDaoManager.getWriter().countProductInventory(shopId);
  }


}
