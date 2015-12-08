package com.bcgogo.txn.service.recommend;

import com.bcgogo.common.Pager;
import com.bcgogo.config.dto.ApplyShopSearchCondition;
import com.bcgogo.config.dto.ShopBusinessScopeDTO;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.model.Shop;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.enums.DeletedType;
import com.bcgogo.enums.PromotionsEnum;
import com.bcgogo.enums.shop.ShopKind;
import com.bcgogo.product.ProductCategory.ProductCategoryDTO;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.product.model.ProductCategory;
import com.bcgogo.product.productCategoryCache.ProductCategoryCache;
import com.bcgogo.product.productManage.ProductSearchCondition;
import com.bcgogo.product.service.IProductService;
import com.bcgogo.product.service.IPromotionsService;
import com.bcgogo.search.util.SolrUtil;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.stat.dto.PurchaseInventoryStatDTO;
import com.bcgogo.txn.dto.PreBuyOrderDTO;
import com.bcgogo.txn.dto.PreBuyOrderItemDTO;
import com.bcgogo.txn.dto.PromotionsDTO;
import com.bcgogo.txn.dto.SalesStatDTO;
import com.bcgogo.txn.dto.recommend.ProductRecommendDTO;
import com.bcgogo.txn.dto.recommend.SalesInventoryWeekStatDTO;
import com.bcgogo.txn.dto.recommend.ShopProductMatchRecordDTO;
import com.bcgogo.txn.dto.recommend.ShopProductMatchResultDTO;
import com.bcgogo.txn.model.*;
import com.bcgogo.txn.model.recommend.*;
import com.bcgogo.txnRead.model.TxnReadReader;
import com.bcgogo.user.dto.BusinessScopeDTO;
import com.bcgogo.user.dto.CustomerDTO;
import com.bcgogo.user.dto.SupplierDTO;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.utils.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 精准推荐
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 13-6-19
 * Time: 上午10:41
 * To change this template use File | Settings | File Templates.
 */
@Component
public class PreciseRecommendService implements IPreciseRecommendService {

  private static final Logger LOG = LoggerFactory.getLogger(PreciseRecommendService.class);

  private static final long DEFAULT_SHOP_ID = -1l;
  private static final int TOP_SALE_NUM = 10; //每个店默认查询销量前十的商品

  @Autowired
  private TxnDaoManager txnDaoManager;

  /**
   * 1.统计所有汽修版的店铺 上周销售量和入库量总和 并按品名和品牌去重
   * 2.遍历所有汽修店铺 找到销量前十的商品、注册填写的商品、经营范围的商品 用品名 品牌去重
   * 3.用品名 品牌去匹配所有汽修版总和的统计值 如果匹配 作为展示结果保存数据
   * 4.2013年7月6日发布后改为按月统计
   */
  public void salesInventoryMonthStat() {
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    List<Shop> shopList = configService.getShop();
    Map<String, SalesInventoryWeekStatDTO> testShopMap = new HashMap<String, SalesInventoryWeekStatDTO>();
    Map<String, SalesInventoryWeekStatDTO> normalShopMap = new HashMap<String, SalesInventoryWeekStatDTO>();

    for (Shop shop : shopList) {

      Map<String, SalesInventoryWeekStatDTO> salesInventoryWeekStatMap = this.getSalesInventoryWeekStatByShopId(shop.getId(), shop.getShopKind());
      if (MapUtils.isEmpty(salesInventoryWeekStatMap)) {
        continue;
      }
      Map<String, SalesInventoryWeekStatDTO> returnMap = new HashMap<String, SalesInventoryWeekStatDTO>();
      this.saveSalesInventoryWeekStat(salesInventoryWeekStatMap, returnMap);


      Set<String> keySet = returnMap.keySet();
      String key = "";
      for (Iterator iterator = keySet.iterator(); iterator.hasNext(); ) {
        key = (String) iterator.next();
        SalesInventoryWeekStatDTO weekStatDTO = returnMap.get(key);

        if (shop.getShopKind() == ShopKind.OFFICIAL) {
          normalShopMap.put(key, weekStatDTO);
        } else if (shop.getShopKind() == ShopKind.TEST) {
          testShopMap.put(key, weekStatDTO);
        }
      }
    }


    for (Shop shop : shopList) {

      try {
        if (!ConfigUtils.isWholesalerVersion(shop.getShopVersionId())) {
          continue;
        }
        Map<String, ProductDTO> productDTOMap = this.getInterestedProductByShopId(shop.getId());
        if (MapUtils.isEmpty(productDTOMap)) {
          continue;
        }

        Map<String,ShopProductMatchResultDTO> stringListMap = new HashMap<String, ShopProductMatchResultDTO>();
        List<ShopProductMatchRecordDTO> recordDTOList = new ArrayList<ShopProductMatchRecordDTO>();

        if (shop.getShopKind() == ShopKind.OFFICIAL) {

          for (ProductDTO productDTO : productDTOMap.values()) {
            for (SalesInventoryWeekStatDTO salesInventoryWeekStatDTO : normalShopMap.values()) {
              if (SolrUtil.getImitateSolrMatchScore((salesInventoryWeekStatDTO.getProductName() + " " + StringUtils.defaultIfEmpty(salesInventoryWeekStatDTO.getProductBrand(), "")).trim(),
                  (productDTO.getName() + " " + StringUtils.defaultIfEmpty(productDTO.getBrand(), "")).trim(), SolrUtil.MATCH_RULE_PARTICIPLE_SINGLE) > 0) {

                String key = productDTO.getName();
                if (!StringUtils.isEmpty(productDTO.getBrand())) {
                  key += productDTO.getBrand();
                }
                key = key.trim();
                ShopProductMatchRecordDTO shopProductMatchRecordDTO = new ShopProductMatchRecordDTO(productDTO, salesInventoryWeekStatDTO);
                recordDTOList.add(shopProductMatchRecordDTO);
                ShopProductMatchResultDTO shopProductMatchResult = stringListMap.get(key);
                if (shopProductMatchResult == null) {
                  shopProductMatchResult = new ShopProductMatchResultDTO();

                  shopProductMatchResult.setProductName(productDTO.getName());
                  shopProductMatchResult.setProductBrand(productDTO.getBrand());
                  shopProductMatchResult.setSalesAmount(salesInventoryWeekStatDTO.getSalesAmount());
                  shopProductMatchResult.setSalesUnit(salesInventoryWeekStatDTO.getSalesUnit());
                  shopProductMatchResult.setInventoryAmount(salesInventoryWeekStatDTO.getInventoryAmount());
                  shopProductMatchResult.setInventoryUnit(salesInventoryWeekStatDTO.getInventoryUnit());
                  shopProductMatchResult.setStatTime(salesInventoryWeekStatDTO.getStatTime());
                  shopProductMatchResult.setShopKind(salesInventoryWeekStatDTO.getShopKind());
                  shopProductMatchResult.setWeekOfYear(salesInventoryWeekStatDTO.getWeekOfYear());
                  shopProductMatchResult.setStatYear(salesInventoryWeekStatDTO.getStatYear());
                  shopProductMatchResult.setStatMonth(salesInventoryWeekStatDTO.getStatMonth());
                  shopProductMatchResult.setStatDay(salesInventoryWeekStatDTO.getStatDay());
                  stringListMap.put(key,shopProductMatchResult);
                }else{
                  shopProductMatchResult.setSalesAmount(shopProductMatchResult.getSalesAmount() + salesInventoryWeekStatDTO.getSalesAmount());
                  shopProductMatchResult.setInventoryAmount(shopProductMatchResult.getInventoryAmount() + salesInventoryWeekStatDTO.getInventoryAmount());
                  stringListMap.put(key,shopProductMatchResult);
                }

              }
            }
          }

        } else if (shop.getShopKind() == ShopKind.TEST) {
          for (ProductDTO productDTO : productDTOMap.values()) {
            for (SalesInventoryWeekStatDTO salesInventoryWeekStatDTO : testShopMap.values()) {

              if (SolrUtil.getImitateSolrMatchScore((salesInventoryWeekStatDTO.getProductName() + " " + StringUtils.defaultIfEmpty(salesInventoryWeekStatDTO.getProductBrand(), "")).trim(),
                  (productDTO.getName() + " " + StringUtils.defaultIfEmpty(productDTO.getBrand(), "")).trim(), SolrUtil.MATCH_RULE_PARTICIPLE_SINGLE) > 0) {
                String key = productDTO.getName();
                if (!StringUtils.isEmpty(productDTO.getBrand())) {
                  key += productDTO.getBrand();
                }
                key = key.trim();

                ShopProductMatchRecordDTO shopProductMatchRecordDTO = new ShopProductMatchRecordDTO(productDTO, salesInventoryWeekStatDTO);
                recordDTOList.add(shopProductMatchRecordDTO);
                ShopProductMatchResultDTO shopProductMatchResult = stringListMap.get(key);
                if (shopProductMatchResult == null) {
                  shopProductMatchResult = new ShopProductMatchResultDTO();

                  shopProductMatchResult.setProductName(productDTO.getName());
                  shopProductMatchResult.setProductBrand(productDTO.getBrand());
                  shopProductMatchResult.setSalesAmount(salesInventoryWeekStatDTO.getSalesAmount());
                  shopProductMatchResult.setSalesUnit(salesInventoryWeekStatDTO.getSalesUnit());
                  shopProductMatchResult.setInventoryAmount(salesInventoryWeekStatDTO.getInventoryAmount());
                  shopProductMatchResult.setInventoryUnit(salesInventoryWeekStatDTO.getInventoryUnit());
                  shopProductMatchResult.setStatTime(salesInventoryWeekStatDTO.getStatTime());
                  shopProductMatchResult.setShopKind(salesInventoryWeekStatDTO.getShopKind());
                  shopProductMatchResult.setWeekOfYear(salesInventoryWeekStatDTO.getWeekOfYear());
                  shopProductMatchResult.setStatYear(salesInventoryWeekStatDTO.getStatYear());
                  shopProductMatchResult.setStatMonth(salesInventoryWeekStatDTO.getStatMonth());
                  shopProductMatchResult.setStatDay(salesInventoryWeekStatDTO.getStatDay());
                  stringListMap.put(key, shopProductMatchResult);
                } else {
                  shopProductMatchResult.setSalesAmount(shopProductMatchResult.getSalesAmount() + salesInventoryWeekStatDTO.getSalesAmount());
                  shopProductMatchResult.setInventoryAmount(shopProductMatchResult.getInventoryAmount() + salesInventoryWeekStatDTO.getInventoryAmount());
                  stringListMap.put(key, shopProductMatchResult);
                }
              }
            }
          }
        }

        this.saveShopProductMatchRecordDTO(recordDTOList);
        this.saveShopMatchRecordResultFromMap(stringListMap, shop.getId());
      } catch (Exception e) {
        LOG.error(e.getMessage(), e);
      }

    }


  }


  /**
   * 获取本店感兴趣的商品 销量前十的商品、注册填写的商品、经营范围的商品 用品名 品牌去重
   *
   * @param shopId
   * @return
   */
  public Map<String, ProductDTO> getInterestedProductByShopId(Long shopId) {
    Map<String, ProductDTO> productDTOMap = new HashMap<String, ProductDTO>();

    IConfigService configService = ServiceManager.getService(IConfigService.class);
    IProductService productService = ServiceManager.getService(IProductService.class);

    Set<Long> shopIdSet = new HashSet<Long>();
    shopIdSet.add(shopId);
    List<ShopBusinessScopeDTO> shopBusinessScopeDTOList = configService.getShopBusinessScopeByShopId(shopIdSet);

    if (CollectionUtils.isNotEmpty(shopBusinessScopeDTOList)) {
      List<Long> productCategoryIdList = new ArrayList<Long>();
      for (ShopBusinessScopeDTO shopBusinessScopeDTO : shopBusinessScopeDTOList) {
        productCategoryIdList.add(shopBusinessScopeDTO.getProductCategoryId());
      }
      List<ProductCategory> productCategoryList = productService.getCategoryListByIds(productCategoryIdList);

      if (CollectionUtils.isNotEmpty(productCategoryList)) {
        for (ProductCategory productCategory : productCategoryList) {
          ProductDTO productDTO = productDTOMap.get(productCategory.getName());
          if (productDTO == null) {
            productDTO = new ProductDTO();
            productDTO.setShopId(shopId);
            productDTO.setName(productCategory.getName());
            productDTO.setNormalProductId(productCategory.getId());
          }
          productDTOMap.put(productCategory.getName(), productDTO);
        }
      }
    }

    List<ProductDTO> topSaleProductList = this.getLastMonthTopTenSalesByShopId(shopId);
    List<ProductDTO> registerProductList = productService.getShopRegisterProductList(shopId);

    topSaleProductList.addAll(registerProductList);

    String key = "";
    for (ProductDTO productDTO : topSaleProductList) {
      key = productDTO.getName();
      if (productDTOMap.containsKey(key)) {
        productDTOMap.remove(key);
      }
      if (StringUtil.isNotEmpty(productDTO.getBrand())) {
        key += productDTO.getBrand();
      }

      ProductDTO dto = productDTOMap.get(key);
      if (dto == null) {
        dto = new ProductDTO();
        dto.setShopId(shopId);
        dto.setName(productDTO.getName());
        dto.setBrand(productDTO.getBrand());
        dto.setId(productDTO.getId());
        dto.setProductLocalInfoId(productDTO.getProductLocalInfoId());
      }
      productDTOMap.put(key, dto);
    }
    return productDTOMap;
  }

  /**
   * 保存店铺匹配记录
   *
   * @param recordDTOList
   */
  public void saveShopProductMatchRecordDTO(List<ShopProductMatchRecordDTO> recordDTOList) {
    if (CollectionUtils.isEmpty(recordDTOList)) {
      return;
    }
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      for (ShopProductMatchRecordDTO shopProductMatchRecordDTO : recordDTOList) {
        ShopProductMatchRecord shopProductMatchRecord = new ShopProductMatchRecord();
        shopProductMatchRecord = shopProductMatchRecord.fromDTO(shopProductMatchRecordDTO);
        writer.save(shopProductMatchRecord);
      }
      writer.flush();
      writer.commit(status);
    } finally {
      writer.rollback(status);

    }

  }

  /**
   * 保存汽配版商品匹配结果
   *
   * @param salesInventoryWeekStatMap
   */
  public void saveShopMatchRecordResult(Map<Long, SalesInventoryWeekStatDTO> salesInventoryWeekStatMap, Long shopId) {
    if (CollectionUtils.isEmpty(salesInventoryWeekStatMap.values())) {
      return;
    }
    TxnWriter writer = txnDaoManager.getWriter();

    Object status = writer.begin();
    try {


      for (SalesInventoryWeekStatDTO weekStatDTO : salesInventoryWeekStatMap.values()) {
        ShopProductMatchResult shopProductMatchResult = new ShopProductMatchResult();
        shopProductMatchResult = shopProductMatchResult.fromDTO(weekStatDTO);
        shopProductMatchResult.setShopId(shopId);
        writer.save(shopProductMatchResult);
      }
      writer.flush();
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  /**
   * 保存汽配版商品匹配结果
   *
   * @param stringListMap
   */
  public void saveShopMatchRecordResultFromMap(Map<String,ShopProductMatchResultDTO> stringListMap, Long shopId) {
    if (MapUtils.isEmpty(stringListMap) || CollectionUtils.isEmpty(stringListMap.values())) {
      return;
    }
    TxnWriter writer = txnDaoManager.getWriter();

    Object status = writer.begin();
    try {
      for (ShopProductMatchResultDTO shopProductMatchResultDTO : stringListMap.values()) {
        ShopProductMatchResult shopProductMatchResult = new ShopProductMatchResult();
        shopProductMatchResult = shopProductMatchResult.fromDTO(shopProductMatchResultDTO);
        shopProductMatchResult.setShopId(shopId);
        writer.save(shopProductMatchResult);
      }
      writer.flush();
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  /**
   * 保存上周销量、入库量统计
   *
   * @param salesInventoryWeekStatMap
   */
  public void saveSalesInventoryWeekStat(Map<String, SalesInventoryWeekStatDTO> salesInventoryWeekStatMap, Map<String, SalesInventoryWeekStatDTO> returnMap) {
    if (CollectionUtils.isEmpty(salesInventoryWeekStatMap.values())) {
      return;
    }
    TxnWriter writer = txnDaoManager.getWriter();

    Object status = writer.begin();
    try {
      String key = "";
      for (SalesInventoryWeekStatDTO weekStatDTO : salesInventoryWeekStatMap.values()) {
        key = weekStatDTO.getKey();
        SalesInventoryWeekStat salesInventoryWeekStat = this.getSalesInventoryWeekStatByCondition(weekStatDTO.getStatYear(), weekStatDTO.getStatMonth(), weekStatDTO.getStatDay(),
            weekStatDTO.getShopKind(), weekStatDTO.getProductName(), weekStatDTO.getProductBrand());
        if (salesInventoryWeekStat == null) {
          salesInventoryWeekStat = new SalesInventoryWeekStat();
          salesInventoryWeekStat = salesInventoryWeekStat.fromDTO(weekStatDTO);
          writer.save(salesInventoryWeekStat);
          weekStatDTO = salesInventoryWeekStat.toDTO();

        } else {
          salesInventoryWeekStat.setSalesAmount(salesInventoryWeekStat.getSalesAmount() + weekStatDTO.getSalesAmount());
          salesInventoryWeekStat.setInventoryAmount(salesInventoryWeekStat.getInventoryAmount() + weekStatDTO.getInventoryAmount());
          writer.update(salesInventoryWeekStat);
          weekStatDTO = salesInventoryWeekStat.toDTO();
        }
        returnMap.put(key, weekStatDTO);
      }
      writer.flush();
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  /**
   * 周数 店铺类型 商品名称 商品品牌 查询上周销量统计
   *
   * @param statYear   year
   * @param statMonth   month
   * @param statDay   day
   * @param shopKind     店铺类型
   * @param productName  商品名称
   * @param productBrand 商品品牌
   * @return
   */
  public SalesInventoryWeekStat getSalesInventoryWeekStatByCondition(int statYear,int statMonth,int statDay, ShopKind shopKind, String productName, String productBrand) {

    TxnWriter writer = txnDaoManager.getWriter();
    List<SalesInventoryWeekStat> salesInventoryWeekStatList = writer.getSalesInventoryWeekStatByCondition( statYear,statMonth,statDay,shopKind, productName, productBrand);
    if (CollectionUtils.isEmpty(salesInventoryWeekStatList)) {
      return null;
    }

    if (salesInventoryWeekStatList.size() > 1) {
      LOG.error("salesInventoryStat is error:statYear:" + statYear +",statMonth:" + statMonth +",statDay:" + statDay + "," + shopKind + "," + productName + "," + productBrand);
    }
    return salesInventoryWeekStatList.get(0);

  }

  /**
   * 根据店铺id获取上周商品的销售量和入库量
   *
   * @param shopId
   * @return
   */
  public Map<String, SalesInventoryWeekStatDTO> getSalesInventoryWeekStatByShopId(Long shopId, ShopKind shopKind) {
    Map<Long, SalesInventoryWeekStatDTO> salesInventoryWeekStatDTOMap = new HashMap<Long, SalesInventoryWeekStatDTO>();
    Map<String, SalesInventoryWeekStatDTO> weekStatDTOMap = new HashMap<String, SalesInventoryWeekStatDTO>();
    Set<Long> productIdSet = new HashSet<Long>();

    TxnWriter writer = txnDaoManager.getWriter();

    Long startTime = DateUtil.getLastMonthTime(Calendar.getInstance());
    startTime = DateUtil.getStartTimeOfTimeDay(startTime);
    Long endTime = System.currentTimeMillis();
    int weekOfYear = DateUtil.getWeekOfYear(System.currentTimeMillis());
    Long statTime = DateUtil.get6clock(System.currentTimeMillis());
    int statMonth = DateUtil.getCurrentMonth();
    int statDay = DateUtil.getCurrentDay();
    int statYear = DateUtil.getCurrentYear();

    List<SalesStatDTO> salesStatDTOList = writer.getLastWeekSalesByShopId(shopId, startTime, endTime);

    List<SalesStatDTO> statDTOs = new ArrayList<SalesStatDTO>();

    if (CollectionUtils.isNotEmpty(salesStatDTOList)) {
      for (SalesStatDTO salesStatDTO : salesStatDTOList) {
        SalesStat stat = writer.getLatestSalesStatBeforeTime(shopId, salesStatDTO.getProductId(), endTime);
        if (stat == null) {
          continue;
        }
        SalesStatDTO statDTO = stat.toDTO();
        SalesStat preStat = writer.getLatestSalesStatBeforeTime(shopId, salesStatDTO.getProductId(), startTime);
        if (preStat != null) {
          statDTO.setAmount(statDTO.getAmount() - preStat.getAmount());
        }
        statDTOs.add(statDTO);
      }
    }

    List<SalesStatDTO> salesStatChangeList = writer.getLastWeekSalesChangeByShopId(shopId, startTime, endTime);
    statDTOs.addAll(salesStatChangeList);

    List<PurchaseInventoryStatDTO> purchaseInventoryStatDTOList = writer.getLastWeekInventoryByShopId(shopId, startTime, endTime);

    List<PurchaseInventoryStatDTO> purchaseInventoryStatDTOs = new ArrayList<PurchaseInventoryStatDTO>();

    if(CollectionUtils.isNotEmpty(purchaseInventoryStatDTOList)){
      for(PurchaseInventoryStatDTO purchaseInventoryStatDTO : purchaseInventoryStatDTOList){
        PurchaseInventoryStat inventoryStat = writer.getLatestPurchaseInventoryStatBeforeTime(shopId, purchaseInventoryStatDTO.getProductId(),endTime);

        if(inventoryStat == null){
          continue;
        }
        PurchaseInventoryStatDTO inventoryStatDTO = inventoryStat.toDTO();
        PurchaseInventoryStat prePurchaseInventoryStat = writer.getLatestPurchaseInventoryStatBeforeTime(shopId, purchaseInventoryStatDTO.getProductId(), startTime);
        if(prePurchaseInventoryStat != null){
          inventoryStatDTO.setAmount(inventoryStatDTO.getAmount() - prePurchaseInventoryStat.getAmount());
        }
        purchaseInventoryStatDTOs.add(inventoryStatDTO);
      }
    }

    List<PurchaseInventoryStatDTO> purchaseInventoryChangeDTOList = writer.getLastWeekInventoryChangeByShopId(shopId, startTime, endTime);
    purchaseInventoryStatDTOs.addAll(purchaseInventoryChangeDTOList);

    if (CollectionUtils.isNotEmpty(statDTOs)) {
      for (SalesStatDTO salesStatDTO : statDTOs) {

        productIdSet.add(salesStatDTO.getProductId());

        SalesInventoryWeekStatDTO salesInventoryWeekStatDTO = salesInventoryWeekStatDTOMap.get(salesStatDTO.getProductId());
        if (salesInventoryWeekStatDTO == null) {
          salesInventoryWeekStatDTO = new SalesInventoryWeekStatDTO();
          salesInventoryWeekStatDTO.setShopKind(shopKind);
          salesInventoryWeekStatDTO.setStatTime(statTime);
          salesInventoryWeekStatDTO.setProductLocalInfoId(salesStatDTO.getProductId());
          salesInventoryWeekStatDTO.setWeekOfYear(weekOfYear);
          salesInventoryWeekStatDTO.setStatYear(statYear);
          salesInventoryWeekStatDTO.setStatMonth(statMonth);
          salesInventoryWeekStatDTO.setStatDay(statDay);
        }
        salesInventoryWeekStatDTO.setSalesAmount(salesInventoryWeekStatDTO.getSalesAmount() + salesStatDTO.getAmount());
        salesInventoryWeekStatDTO.setSalesAmount(salesInventoryWeekStatDTO.getSalesAmount() < 0 ? 0 : salesInventoryWeekStatDTO.getSalesAmount());
        salesInventoryWeekStatDTOMap.put(salesStatDTO.getProductId(), salesInventoryWeekStatDTO);
      }
    }

    if (CollectionUtils.isNotEmpty(purchaseInventoryStatDTOs)) {
      for (PurchaseInventoryStatDTO purchaseInventoryStatDTO : purchaseInventoryStatDTOs) {
        productIdSet.add(purchaseInventoryStatDTO.getProductId());

        SalesInventoryWeekStatDTO salesInventoryWeekStatDTO = salesInventoryWeekStatDTOMap.get(purchaseInventoryStatDTO.getProductId());
        if (salesInventoryWeekStatDTO == null) {
          salesInventoryWeekStatDTO = new SalesInventoryWeekStatDTO();
          salesInventoryWeekStatDTO.setShopKind(shopKind);
          salesInventoryWeekStatDTO.setStatTime(statTime);
          salesInventoryWeekStatDTO.setProductLocalInfoId(purchaseInventoryStatDTO.getProductId());
          salesInventoryWeekStatDTO.setWeekOfYear(weekOfYear);
          salesInventoryWeekStatDTO.setStatYear(statYear);
          salesInventoryWeekStatDTO.setStatMonth(statMonth);
          salesInventoryWeekStatDTO.setStatDay(statDay);
        }
        salesInventoryWeekStatDTO.setInventoryAmount(salesInventoryWeekStatDTO.getInventoryAmount() + purchaseInventoryStatDTO.getAmount());
        salesInventoryWeekStatDTO.setInventoryAmount(salesInventoryWeekStatDTO.getInventoryAmount() < 0 ? 0 : salesInventoryWeekStatDTO.getInventoryAmount());
        salesInventoryWeekStatDTOMap.put(purchaseInventoryStatDTO.getProductId(), salesInventoryWeekStatDTO);
      }
    }


    if (CollectionUtils.isEmpty(productIdSet)) {
      return weekStatDTOMap;
    }
    IProductService productService = ServiceManager.getService(IProductService.class);
    Map<Long, ProductDTO> productDTOMap = productService.getProductDTOMapByProductLocalInfoIds(shopId, productIdSet);
    if (MapUtils.isEmpty(productDTOMap)) {
      return weekStatDTOMap;
    }


    for (SalesInventoryWeekStatDTO salesInventoryWeekStatDTO : salesInventoryWeekStatDTOMap.values()) {
      ProductDTO productDTO = productDTOMap.get(salesInventoryWeekStatDTO.getProductLocalInfoId());
      if (productDTO == null) {
        continue;
      }
      if (StringUtil.isEmpty(productDTO.getName())) {
        continue;
      }
      StringBuffer stringBuffer = new StringBuffer();
      stringBuffer.append(productDTO.getName());
      if (StringUtil.isNotEmpty(productDTO.getBrand())) {
        stringBuffer.append(productDTO.getBrand());
      }
      String key = stringBuffer.toString();
      SalesInventoryWeekStatDTO weekStatDTO = weekStatDTOMap.get(key);
      if (weekStatDTO == null) {
        weekStatDTO = new SalesInventoryWeekStatDTO();
        weekStatDTO.setKey(key);
        weekStatDTO.setShopKind(shopKind);
        weekStatDTO.setStatTime(statTime);
        weekStatDTO.setWeekOfYear(weekOfYear);
        weekStatDTO.setStatYear(statYear);
        weekStatDTO.setStatMonth(statMonth);
        weekStatDTO.setStatDay(statDay);
        weekStatDTO.setProductName(productDTO.getName());
        weekStatDTO.setProductBrand(StringUtil.isEmpty(productDTO.getBrand()) ? null : productDTO.getBrand());
        weekStatDTO.setSalesAmount(salesInventoryWeekStatDTO.getSalesAmount());
        weekStatDTO.setInventoryAmount(salesInventoryWeekStatDTO.getInventoryAmount());
      } else {
        weekStatDTO.setSalesAmount(weekStatDTO.getSalesAmount() + salesInventoryWeekStatDTO.getSalesAmount());
        weekStatDTO.setInventoryAmount(weekStatDTO.getInventoryAmount() + salesInventoryWeekStatDTO.getInventoryAmount());
      }
      weekStatDTOMap.put(key, weekStatDTO);
    }
    return weekStatDTOMap;
  }


  /**
   * 获取上一月某个店铺销量前十的商品
   *
   * @param shopId
   * @return
   */
  public List<ProductDTO> getLastMonthTopTenSalesByShopId(Long shopId) {

    List<ProductDTO> productDTOList = new ArrayList<ProductDTO>();
    Set<Long> productIdSet = new HashSet<Long>();
    TxnWriter writer = txnDaoManager.getWriter();

    Long startTime = DateUtil.getLastMonthTime(Calendar.getInstance());
    startTime = DateUtil.getStartTimeOfTimeDay(startTime);
    Long endTime = System.currentTimeMillis();
    List<SalesStatDTO> salesStatDTOList = writer.getLastWeekSalesByShopId(shopId, startTime, endTime);

    List<SalesStatDTO> statDTOs = new ArrayList<SalesStatDTO>();

    if (CollectionUtils.isNotEmpty(salesStatDTOList)) {
      for (SalesStatDTO salesStatDTO : salesStatDTOList) {
        SalesStat stat = writer.getLatestSalesStatBeforeTime(shopId, salesStatDTO.getProductId(), endTime);
        if (stat == null) {
          continue;
        }
        SalesStatDTO statDTO = stat.toDTO();
        SalesStat preStat = writer.getLatestSalesStatBeforeTime(shopId, salesStatDTO.getProductId(), startTime);
        if (preStat != null) {
          statDTO.setAmount(statDTO.getAmount() - preStat.getAmount());
        }
        statDTOs.add(statDTO);
      }
    }

    List<SalesStatDTO> salesStatChangeList = writer.getLastWeekSalesChangeByShopId(shopId, startTime, endTime);
    statDTOs.addAll(salesStatChangeList);

    if (CollectionUtil.isEmpty(statDTOs)) {
      return productDTOList;
    }

    Map<Long, SalesStatDTO> salesStatDTOMap = new HashMap<Long, SalesStatDTO>();

    for (SalesStatDTO salesStatDTO : statDTOs) {

      SalesStatDTO statDTO = salesStatDTOMap.get(salesStatDTO.getProductId());
      if (statDTO == null) {
        salesStatDTOMap.put(salesStatDTO.getProductId(), salesStatDTO);
      } else {
        statDTO.setAmount(salesStatDTO.getAmount() + statDTO.getAmount());
        salesStatDTOMap.put(salesStatDTO.getProductId(), statDTO);
      }
    }

    List<SalesStatDTO> resultList = new ArrayList<SalesStatDTO>();
    for (SalesStatDTO salesStatDTO : salesStatDTOMap.values()) {
      if(salesStatDTO.getAmount()<= 0){
        continue;
      }
      resultList.add(salesStatDTO);
    }

    Collections.sort(resultList);
    for (int index = 0; index < resultList.size(); index++) {
      if (index < TOP_SALE_NUM) {
        productIdSet.add(resultList.get(index).getProductId());
      }
    }

    IProductService productService = ServiceManager.getService(IProductService.class);
    Map<Long, ProductDTO> productDTOMap = productService.getProductDTOMapByProductLocalInfoIds(shopId, productIdSet);
    if (CollectionUtil.isEmpty(productDTOMap.values())) {
      return productDTOList;
    }
    for (ProductDTO productDTO : productDTOMap.values()) {
      productDTOList.add(productDTO);
    }

    return productDTOList;

  }

  /**
   * 根据shopId获取第二大类的经营范围的合计
   *
   * @param shopIdSet
   * @return
   */
  public Map<Long, String> getSecondCategoryByShopId(Set<Long> shopIdSet) {
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    Map<Long, String> shopBusinessScopeMap = new HashMap<Long, String>();

    Map<Long, Set<Long>> shopBusinessScopeSet = new HashMap<Long, Set<Long>>();

    if (CollectionUtils.isEmpty(shopIdSet)) {
      return shopBusinessScopeMap;
    }

    List<ShopBusinessScopeDTO> shopBusinessScopeDTOList = configService.getShopBusinessScopeByShopId(shopIdSet);
    if (CollectionUtils.isEmpty(shopBusinessScopeDTOList)) {
      return shopBusinessScopeMap;
    }

    Map<Long, ProductCategoryDTO> productCategoryDTOMap = ProductCategoryCache.getProductCategoryDTOMap();

    if (MapUtils.isEmpty(productCategoryDTOMap)) {
      return shopBusinessScopeMap;
    }

    Set<Long> businessScopeSet = null;
    ProductCategoryDTO productCategoryDTO = null;
    for (ShopBusinessScopeDTO shopBusinessScopeDTO : shopBusinessScopeDTOList) {

      productCategoryDTO = productCategoryDTOMap.get(shopBusinessScopeDTO.getProductCategoryId());

      if (productCategoryDTO == null) {
        continue;
      }

      businessScopeSet = shopBusinessScopeSet.get(shopBusinessScopeDTO.getShopId());
      if (businessScopeSet == null) {
        businessScopeSet = new HashSet<Long>();
      }
      businessScopeSet.add(productCategoryDTO.getParentId());
      shopBusinessScopeSet.put(shopBusinessScopeDTO.getShopId(), businessScopeSet);
    }

    for (Long shopId : shopBusinessScopeSet.keySet()) {
      StringBuffer stringBuffer = new StringBuffer();
      Set<Long> set = shopBusinessScopeSet.get(shopId);
      if (CollectionUtils.isEmpty(set)) {
        continue;
      }
      for (Long id : set) {
        productCategoryDTO = productCategoryDTOMap.get(id);
        if (productCategoryDTO == null) {
          continue;
        }
        stringBuffer.append(productCategoryDTO.getName()).append(",");
      }
      shopBusinessScopeMap.put(shopId, StringUtil.getShortStringByNum(stringBuffer.toString(), 0, stringBuffer.toString().length() - 1));
    }
    return shopBusinessScopeMap;
  }


  /**
   * 推荐客户 推荐供应商 获取经营范围
   *
   * @param applyShopSearchConditionList
   * @return
   */
  public List<ApplyShopSearchCondition> getShopBusinessScopeForApply(List<ApplyShopSearchCondition> applyShopSearchConditionList) {
    if (CollectionUtils.isEmpty(applyShopSearchConditionList)) {
      return applyShopSearchConditionList;
    }
    Set<Long> shopIdSet = new HashSet<Long>();
    for (ApplyShopSearchCondition condition : applyShopSearchConditionList) {
      shopIdSet.add(condition.getShopId());
    }
    Map<Long, String> stringMap = this.getSecondCategoryByShopId(shopIdSet);
    if (MapUtils.isEmpty(stringMap)) {
      return applyShopSearchConditionList;
    }
    for (ApplyShopSearchCondition condition : applyShopSearchConditionList) {
      String businessScope = stringMap.get(condition.getShopId());
      if (StringUtil.isNotEmpty(businessScope)) {
        condition.setBusinessScope(businessScope);
      }
    }

    return applyShopSearchConditionList;
  }


  /**
   * 获取本店推荐客户或者供应商
   * @param shopId
   * @param pager
   * @return
   */
  public List<ShopDTO> getRecommendShopByShopId(Long shopId,Pager pager) {
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    List<ShopDTO> shopDTOList = new ArrayList<ShopDTO>();
    TxnWriter txnWriter = txnDaoManager.getWriter();
    List<ShopRecommend> shopRecommendList = txnWriter.getRecommendShopByShopId(shopId,pager);
    if (CollectionUtil.isEmpty(shopRecommendList)) {
      return shopDTOList;
    }

    Set<Long> shopIdSet = new HashSet<Long>();
    for (ShopRecommend shopRecommend : shopRecommendList) {
      if (shopRecommend.getRecommendShopId() == null) {
        continue;
      }
      shopIdSet.add(shopRecommend.getRecommendShopId());
    }
    shopDTOList = configService.getShopByIds(shopIdSet.toArray(new Long[shopIdSet.size()]));

    if (CollectionUtil.isEmpty(shopDTOList)) {
      return shopDTOList;
    }

    Map<Long,ShopDTO> shopDTOMap = new HashMap<Long, ShopDTO>();
    for(ShopDTO shopDTO : shopDTOList){
      shopDTOMap.put(shopDTO.getId(),shopDTO);
    }

    shopDTOList = new ArrayList<ShopDTO>();
    Map<Long, String> businessScopeMap = this.getSecondCategoryByShopId(shopIdSet);
    String businessScope = "";
    for (ShopRecommend shopRecommend : shopRecommendList) {
      ShopDTO shopDTO = shopDTOMap.get(shopRecommend.getRecommendShopId());
      if(shopDTO == null){
        continue;
      }
      shopDTO.setAreaName(configService.getShopAreaInfoByShopDTO(shopDTO));
      shopDTO.resetBusinessScope();
      businessScope = businessScopeMap.get(shopDTO.getId());
      if (StringUtil.isNotEmpty(businessScope)) {
        shopDTO.setBusinessScopeStr(businessScope);
      }
      shopDTOList.add(shopDTO);
    }
    return shopDTOList;
  }

  /**
   *汽配版获取推荐商品
   * @param shopId
   * @param deletedType
   * @param pager
   * @return
   */
  public List<PreBuyOrderItemDTO> getWholesalerProductRecommendByPager(Long shopId,DeletedType deletedType,Pager pager) {
    List<PreBuyOrderItemDTO> preBuyOrderItemDTOList = new ArrayList<PreBuyOrderItemDTO>();
    try {
      if (shopId == null || deletedType == null) {
        return null;
      }
      IConfigService configService = ServiceManager.getService(IConfigService.class);
      ShopDTO localShopDTO = configService.getShopById(shopId);
      TxnWriter writer = txnDaoManager.getWriter();
      List<PreBuyOrderItemRecommend> preBuyOrderItemRecommends = writer.getWholesalerProductRecommendByPager(shopId, deletedType, pager);
      if (CollectionUtils.isEmpty(preBuyOrderItemRecommends)) {
        return null;
      }

      Set<Long> preBuyOrderItemIdSet = new HashSet<Long>();
      Set<Long> preBuyOrderIdSet = new HashSet<Long>();
      Set<Long> shopIdSet = new HashSet<Long>();

      for (PreBuyOrderItemRecommend preBuyOrderItemRecommend : preBuyOrderItemRecommends) {
        if (preBuyOrderItemRecommend.getPreBuyOrderItemId() == null || preBuyOrderItemRecommend.getPreBuyOrderId() == null) {
          continue;
        }
        preBuyOrderItemIdSet.add(preBuyOrderItemRecommend.getPreBuyOrderItemId());
        preBuyOrderIdSet.add(preBuyOrderItemRecommend.getPreBuyOrderId());
        shopIdSet.add(preBuyOrderItemRecommend.getPreBuyOrderShopId());
      }
      if (CollectionUtils.isEmpty(preBuyOrderIdSet) || CollectionUtils.isEmpty(preBuyOrderItemIdSet) || CollectionUtils.isEmpty(shopIdSet)) {
        return null;
      }

      List<PreBuyOrderItem> preBuyOrderItemList = writer.getPreBuyOrderItemsByIdSet(preBuyOrderItemIdSet);
      if (CollectionUtils.isEmpty(preBuyOrderItemList)) {
        return null;
      }

      Map<Long,PreBuyOrderItem> preBuyOrderItemMap = new HashMap<Long, PreBuyOrderItem>();
      for(PreBuyOrderItem preBuyOrderItem : preBuyOrderItemList){
        preBuyOrderItemMap.put(preBuyOrderItem.getId(),preBuyOrderItem);
      }

      Map<Long, ShopDTO> shopDTOMap = configService.getShopByShopId(shopIdSet.toArray(new Long[shopIdSet.size()]));

      List<PreBuyOrder> preBuyOrderList = writer.getPreBuyOrdersByShopIdAndOrderIds(null, preBuyOrderIdSet.toArray(new Long[preBuyOrderIdSet.size()]));
      if (CollectionUtils.isEmpty(preBuyOrderList)) {
        return null;
      }
      Map<Long, PreBuyOrderDTO> preBuyOrderDTOMap = new HashMap<Long, PreBuyOrderDTO>();
      for (PreBuyOrder preBuyOrder : preBuyOrderList) {
        if (preBuyOrder.getDeleted() == DeletedType.TRUE) {
          continue;
        }
        preBuyOrderDTOMap.put(preBuyOrder.getId(), preBuyOrder.toDTO());
      }
      PreBuyOrderDTO preBuyOrderDTO = null;
      Long preBuyOrderId = null;
      ShopDTO shopDTO = null;
      for (PreBuyOrderItemRecommend preBuyOrderItemRecommend : preBuyOrderItemRecommends) {
        if (preBuyOrderItemRecommend.getPreBuyOrderItemId() == null || preBuyOrderItemRecommend.getPreBuyOrderId() == null) {
          continue;
        }
        PreBuyOrderItem preBuyOrderItem = preBuyOrderItemMap.get(preBuyOrderItemRecommend.getPreBuyOrderItemId());
        if(preBuyOrderItem == null){
          continue;
        }
        preBuyOrderId = preBuyOrderItem.getPreBuyOrderId();
        preBuyOrderDTO = preBuyOrderDTOMap.get(preBuyOrderId);
        if (preBuyOrderDTO == null || preBuyOrderDTO.getEndDate() == null || preBuyOrderDTO.getEndDate() < DateUtil.getTheDayTime()) {
          continue;
        }
        shopDTO = shopDTOMap.get(preBuyOrderDTO.getShopId());
        if (shopDTO == null) {
          continue;
        }

        PreBuyOrderItemDTO preBuyOrderItemDTO = preBuyOrderItem.toDTO();

        if (NumberUtil.isEqual(shopDTO.getCity(), localShopDTO.getCity())) {
          preBuyOrderItemDTO.setLocalCity(true);
        } else {
          preBuyOrderItemDTO.setLocalCity(false);
        }
        preBuyOrderItemDTO.setShopId(preBuyOrderItemRecommend.getPreBuyOrderShopId());
        preBuyOrderItemDTO.setShopIdStr(shopDTO.getIdStr());
        preBuyOrderItemDTO.setShopName(shopDTO.getName());
        preBuyOrderItemDTO.setShopAreaInfo(configService.getShopAreaInfoByShopDTO(shopDTO));
        preBuyOrderItemDTO.setVestDateStr(preBuyOrderDTO.getVestDateStr());
        preBuyOrderItemDTO.setEndDateStr(preBuyOrderDTO.getEndDateStr());
        preBuyOrderItemDTO.setBusinessChanceType(preBuyOrderDTO.getBusinessChanceType());
        preBuyOrderItemDTO.setBusinessChanceTypeStr(preBuyOrderDTO.getBusinessChanceType().getName());
        preBuyOrderItemDTO.setEndDateCount(preBuyOrderDTO.getEndDateCount());
        preBuyOrderItemDTO.setPreBuyOrderIdStr(preBuyOrderDTO.getId().toString());
        preBuyOrderItemDTOList.add(preBuyOrderItemDTO);
      }
      return preBuyOrderItemDTOList;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
    return null;
  }


  /**
   * 填充商品和店铺信息 供求中心首页使用
   * @param productLocalInfoIdSet
   * @param shopIdSet
   * @return
   */
  public List<ProductDTO> fillProductShopInfo(Set<Long> productLocalInfoIdSet,Set<Long> shopIdSet) {
    List<ProductDTO> productDTOList = new ArrayList<ProductDTO>();
    if (CollectionUtils.isEmpty(productLocalInfoIdSet) || CollectionUtils.isEmpty(shopIdSet)) {
      return productDTOList;
    }

    IConfigService configService = ServiceManager.getService(IConfigService.class);
    IProductService productService = ServiceManager.getService(IProductService.class);
    List<ShopDTO> shopDTOList = configService.getShopByIds(shopIdSet.toArray(new Long[shopIdSet.size()]));
    if (CollectionUtils.isEmpty(shopDTOList)) {
      return productDTOList;
    }
    Map<Long, ShopDTO> shopDTOMap = new HashMap<Long, ShopDTO>();

    for (ShopDTO shopDTO : shopDTOList) {
      shopDTO.setAreaName(configService.getShopAreaInfoByShopDTO(shopDTO));
      shopDTOMap.put(shopDTO.getId(), shopDTO);
    }
    Map<Long, ProductDTO> productDTOMap = productService.getProductDTOMapByProductLocalInfoIds(productLocalInfoIdSet);
    if (MapUtils.isEmpty(productDTOMap)) {
      return productDTOList;
    }

    ShopDTO shopDTO = null;
    for (ProductDTO productDTO : productDTOMap.values()) {
      shopDTO = shopDTOMap.get(productDTO.getShopId());
      if (shopDTO != null) {
        productDTO.setShopName(shopDTO.getName());
        productDTO.setShopAreaInfo(shopDTO.getAreaName());
      }

      productDTOList.add(productDTO);
    }
    return productDTOList;
  }


    /**
   * 汽修版获取推荐商品数量
   * @param shopId
   * @param deletedType
   * @return
   */
  public int countProductRecommendByShopId(Long shopId,DeletedType deletedType){
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.countProductRecommendByShopId(shopId,deletedType);
  }

  /**
   *汽配版获取推荐商品数量
   * @param shopId
   * @param deletedType
   * @return
   */
  public int countWholesalerProductRecommendByShopId(Long shopId,DeletedType deletedType){
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.countWholesalerProductRecommendByShopId(shopId, deletedType);
  }

  /**
   * 供求中心首页获取上周销量信息(汽配版)
   * @param shopId
   * @param weekOfYear
   * @return
   */
  public int countLastWeekSalesInventoryStatByShopId(Long shopId,int weekOfYear){
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.countLastWeekSalesInventoryStatByShopId(shopId, weekOfYear);
  }

  /**
   * 供求中心首页获取上月销量信息(汽配版)
   * @param shopId
   * @param statMonth
   * @return
   */
  public List<ShopProductMatchResultDTO> getLastMonthSalesInventoryStatByShopId(Long shopId,int statYear,int statMonth,int statDay,Pager pager) {
    TxnWriter writer = txnDaoManager.getWriter();
    List<ShopProductMatchResult> shopProductMatchResultList = writer.getLastMonthSalesInventoryStatByShopId(shopId, statYear,statMonth,statDay, pager);
    if (CollectionUtils.isEmpty(shopProductMatchResultList)) {
      return null;
    }
    List<ShopProductMatchResultDTO> shopProductMatchResultDTOList = new ArrayList<ShopProductMatchResultDTO>();
    for (ShopProductMatchResult shopProductMatchResult : shopProductMatchResultList) {
      shopProductMatchResultDTOList.add(shopProductMatchResult.toDTO());
    }
    return shopProductMatchResultDTOList;
  }


  /**
   * 获取客户或者供应商的二级分类（关联供应商专用）
   * @param customerDTO
   * @param supplierDTO
   * @return
   */

  public void getCustomerSupplierBusinessScope(CustomerDTO customerDTO,SupplierDTO supplierDTO) {

    try {
      IConfigService configService = ServiceManager.getService(IConfigService.class);

      Map<Long, ProductCategoryDTO> productCategoryDTOMap = ProductCategoryCache.getProductCategoryDTOMap();
      List<Long> productCategoryIds = null;
      if (customerDTO != null) {
        if (CollectionUtils.isEmpty(customerDTO.getThirdCategoryIds())) {
          productCategoryIds = configService.getShopBusinessScopeProductCategoryIdListByShopId(customerDTO.getCustomerShopId());
        }else{
          productCategoryIds = customerDTO.getThirdCategoryIds();
        }
      }


      StringBuffer businessScope = new StringBuffer();
      Set<String> stringSet = new HashSet<String>();
      ProductCategoryDTO productCategoryDTO = null;
      if (CollectionUtil.isNotEmpty(productCategoryIds)) {
        for (Long categoryId : productCategoryIds) {
          productCategoryDTO = productCategoryDTOMap.get(categoryId);
          if (productCategoryDTO == null) {
            continue;
          }
          productCategoryDTO = productCategoryDTOMap.get(productCategoryDTO.getParentId());
          if (productCategoryDTO == null) {
            continue;
          }
          stringSet.add(productCategoryDTO.getName());
        }
      }

      if (CollectionUtils.isNotEmpty(stringSet)) {
        for (String str : stringSet) {
          businessScope.append(str).append(",");
        }
      }

      if (customerDTO != null) {
        if (StringUtil.isNotEmpty(businessScope.toString())) {
          customerDTO.setBusinessScopeStr(StringUtil.getShortStringByNum(businessScope.toString(), 0, businessScope.length() - 1));
        } else {
          customerDTO.setBusinessScopeStr(null);
        }
      }

      stringSet = new HashSet<String>();
      businessScope = new StringBuffer();
      List<Long> categoryIds = null;
      if (supplierDTO != null) {
        if (CollectionUtils.isEmpty(supplierDTO.getThirdCategoryIds())) {
          categoryIds = configService.getShopBusinessScopeProductCategoryIdListByShopId(supplierDTO.getSupplierShopId());
        }else{
          categoryIds = supplierDTO.getThirdCategoryIds();
        }
      }

      if (CollectionUtil.isNotEmpty(categoryIds)) {
        for (Long categoryId : categoryIds) {
          productCategoryDTO = productCategoryDTOMap.get(categoryId);
          if (productCategoryDTO == null) {
            continue;
          }
          productCategoryDTO = productCategoryDTOMap.get(productCategoryDTO.getParentId());
          if (productCategoryDTO == null) {
            continue;
          }
          stringSet.add(productCategoryDTO.getName());
        }
      }

      if (CollectionUtils.isNotEmpty(stringSet)) {
        for (String str : stringSet) {
          businessScope.append(str).append(",");
        }
      }

      if (supplierDTO != null) {
        if (StringUtil.isNotEmpty(businessScope.toString())) {
          supplierDTO.setBusinessScope(StringUtil.getShortStringByNum(businessScope.toString(), 0, businessScope.length() - 1));
        } else {
          supplierDTO.setBusinessScope(null);
        }
      }
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
  }

  /**
   * 获取客户或者供应商的二级分类（新增客户或者供应商专用）
   * @param customerDTO
   * @param supplierDTO
   * @return
   */
  public void getCustomerSupplierBusinessScopeForAdd(CustomerDTO customerDTO,SupplierDTO supplierDTO) {

    try {
      IUserService userService = ServiceManager.getService(IUserService.class);

      Long shopId = customerDTO == null ? supplierDTO.getShopId() : customerDTO.getShopId();
      Long customerId = customerDTO == null ? null : customerDTO.getId();
      Long supplierId = supplierDTO == null ? null : supplierDTO.getId();
      if (shopId == null || (customerId == null && supplierId == null)) {
        return;
      }

      Map<Long, ProductCategoryDTO> productCategoryDTOMap = ProductCategoryCache.getProductCategoryDTOMap();
      List<Long> productCategoryIds = new ArrayList<Long>();
      if (customerDTO != null) {
        if (CollectionUtils.isEmpty(customerDTO.getThirdCategoryIds())) {
          List<BusinessScopeDTO> businessScopeDTOList = userService.getCustomerSupplierBusinessScope(shopId, customerId, supplierId);
          if (CollectionUtils.isNotEmpty(businessScopeDTOList)) {
            for (BusinessScopeDTO businessScopeDTO : businessScopeDTOList) {
              productCategoryIds.add(businessScopeDTO.getProductCategoryId());
            }
          }
        } else {
          productCategoryIds = customerDTO.getThirdCategoryIds();
        }
      }


      StringBuffer businessScope = new StringBuffer();
      Set<String> stringSet = new HashSet<String>();
      ProductCategoryDTO productCategoryDTO = null;
      if (CollectionUtil.isNotEmpty(productCategoryIds)) {
        for (Long categoryId : productCategoryIds) {
          productCategoryDTO = productCategoryDTOMap.get(categoryId);
          if (productCategoryDTO == null) {
            continue;
          }
          productCategoryDTO = productCategoryDTOMap.get(productCategoryDTO.getParentId());
          if (productCategoryDTO == null) {
            continue;
          }
          stringSet.add(productCategoryDTO.getName());
        }
      }

      if (CollectionUtils.isNotEmpty(stringSet)) {
        for (String str : stringSet) {
          businessScope.append(str).append(",");
        }
      }

      if (customerDTO != null) {
        if (StringUtil.isNotEmpty(businessScope.toString())) {
          customerDTO.setBusinessScopeStr(StringUtil.getShortStringByNum(businessScope.toString(), 0, businessScope.length() - 1));
        } else {
          customerDTO.setBusinessScopeStr(null);
        }
      }

      stringSet = new HashSet<String>();
      businessScope = new StringBuffer();
      List<Long> categoryIds = new ArrayList<Long>();
      if (supplierDTO != null) {
        if (CollectionUtils.isEmpty(supplierDTO.getThirdCategoryIds())) {
          List<BusinessScopeDTO> businessScopeDTOList = userService.getCustomerSupplierBusinessScope(shopId, customerId, supplierId);
          if (CollectionUtils.isNotEmpty(businessScopeDTOList)) {
            for (BusinessScopeDTO businessScopeDTO : businessScopeDTOList) {
              categoryIds.add(businessScopeDTO.getProductCategoryId());
            }
          }
        } else {
          categoryIds = supplierDTO.getThirdCategoryIds();
        }
      }

      if (CollectionUtil.isNotEmpty(categoryIds)) {
        for (Long categoryId : categoryIds) {
          productCategoryDTO = productCategoryDTOMap.get(categoryId);
          if (productCategoryDTO == null) {
            continue;
          }
          productCategoryDTO = productCategoryDTOMap.get(productCategoryDTO.getParentId());
          if (productCategoryDTO == null) {
            continue;
          }
          stringSet.add(productCategoryDTO.getName());
        }
      }

      if (CollectionUtils.isNotEmpty(stringSet)) {
        for (String str : stringSet) {
          businessScope.append(str).append(",");
        }
      }

      if (supplierDTO != null) {
        if (StringUtil.isNotEmpty(businessScope.toString())) {
          supplierDTO.setBusinessScope(StringUtil.getShortStringByNum(businessScope.toString(), 0, businessScope.length() - 1));
        } else {
          supplierDTO.setBusinessScope(null);
        }
      }
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
  }

  /**
   * 批量设置客户或者供应商的经营范围
   * @param customerDTOList
   * @param supplierDTOList
   */
  public void setCustomerSupplierBusinessScope(List<CustomerDTO> customerDTOList,List<SupplierDTO> supplierDTOList) {
    if (CollectionUtils.isEmpty(customerDTOList) && CollectionUtils.isEmpty(supplierDTOList)) {
      return;
    }

    IUserService userService = ServiceManager.getService(IUserService.class);

    Long shopId = null;

    Set<Long> customerIdSet = new HashSet<Long>();
    Set<Long> supplierIdSet = new HashSet<Long>();

    Map<Long, CustomerDTO> customerDTOMap = new HashMap<Long, CustomerDTO>();
    Map<Long, SupplierDTO> supplierDTOMap = new HashMap<Long, SupplierDTO>();


    if (CollectionUtils.isNotEmpty(customerDTOList)) {
      for (CustomerDTO customerDTO : customerDTOList) {
        customerIdSet.add(customerDTO.getId());
        shopId = customerDTO.getShopId();
        customerDTOMap.put(customerDTO.getId(), customerDTO);
      }
      List<BusinessScopeDTO> businessScopeDTOList = userService.getCustomerSupplierBusinessScope(shopId, customerIdSet, null);
      if (CollectionUtils.isNotEmpty(businessScopeDTOList)) {
        for (BusinessScopeDTO businessScopeDTO : businessScopeDTOList) {
          CustomerDTO customerDTO = customerDTOMap.get(businessScopeDTO.getCustomerId());
          if (customerDTO == null) {
            continue;
          }
          customerDTO.getThirdCategoryIds().add(businessScopeDTO.getProductCategoryId());

          if(StringUtil.isEmpty(customerDTO.getThirdCategoryIdStr())) {
            customerDTO.setThirdCategoryIdStr(businessScopeDTO.getProductCategoryId() + ",");
          }else{
            customerDTO.setThirdCategoryIdStr(customerDTO.getThirdCategoryIdStr() + "," + businessScopeDTO.getProductCategoryId());
          }

        }
      }
    }

    if (CollectionUtils.isNotEmpty(supplierDTOList)) {
      for (SupplierDTO supplierDTO : supplierDTOList) {
        supplierIdSet.add(supplierDTO.getId());
        shopId = supplierDTO.getShopId();
        supplierDTOMap.put(supplierDTO.getId(), supplierDTO);
      }
      List<BusinessScopeDTO> businessScopeDTOList = userService.getCustomerSupplierBusinessScope(shopId, null, supplierIdSet);
      if (CollectionUtils.isNotEmpty(businessScopeDTOList)) {
        for (BusinessScopeDTO businessScopeDTO : businessScopeDTOList) {
          SupplierDTO supplierDTO = supplierDTOMap.get(businessScopeDTO.getSupplierId());
          if (supplierDTO == null) {
            continue;
          }
          supplierDTO.getThirdCategoryIds().add(businessScopeDTO.getProductCategoryId());

          if (StringUtil.isEmpty(supplierDTO.getThirdCategoryIdStr())) {
            supplierDTO.setThirdCategoryIdStr(businessScopeDTO.getProductCategoryId() + ",");
          } else {
            supplierDTO.setThirdCategoryIdStr(supplierDTO.getThirdCategoryIdStr() + "," + businessScopeDTO.getProductCategoryId());
          }
        }
      }
    }
  }


  @Override
  public List<ProductDTO> getRecommendProductDetailDTOs(ProductSearchCondition condition) throws Exception {
    Long shopId=condition.getShopId();
    if(shopId==null) throw new Exception("参数异常。");

    IConfigService configService = ServiceManager.getService(IConfigService.class);

    ShopDTO localShopDTO = configService.getShopById(shopId);
    TxnWriter writer = txnDaoManager.getWriter();
    List<ProductRecommend> productRecommendList = writer.getRecommendProduct(condition);
    List<ProductDTO> productDTOList = new ArrayList<ProductDTO>();
    if (CollectionUtils.isNotEmpty(productRecommendList)) {
      Set<Long> productLocalInfoIdSet = new HashSet<Long>();
      Set<Long> shopIdSet = new HashSet<Long>();

      for (ProductRecommend productRecommend : productRecommendList) {
        productLocalInfoIdSet.add(productRecommend.getMatchedProductLocalInfoId());
        shopIdSet.add(productRecommend.getMatchedProductShopId());
      }
      IProductService productService = ServiceManager.getService(IProductService.class);
      Map<Long, ProductDTO> productDTOMap = productService.getProductDTOMapByProductLocalInfoIds(new HashSet<Long>(productLocalInfoIdSet));
      Map<Long, ShopDTO> shopDTOMap = configService.getShopByShopId(shopIdSet.toArray(new Long[shopIdSet.size()]));

      ProductDTO productDTO = null;
      ShopDTO shopDTO = null;
      for (ProductRecommend productRecommend : productRecommendList) {
        productDTO = productDTOMap.get(productRecommend.getMatchedProductLocalInfoId());
        if (productDTO != null) {
          shopDTO = shopDTOMap.get(productRecommend.getMatchedProductShopId());
          if(shopDTO == null){
            continue;
          }
          if(NumberUtil.isEqual(shopDTO.getCity(), localShopDTO.getCity())){
            productDTO.setLocalCity(true);
          }else{
            productDTO.setLocalCity(false);
          }
          productDTO.setShopInfo(shopDTO);
          productDTO.setShopAreaInfo(configService.getShopAreaInfoByShopDTO(shopDTO));
          productDTO.setShopIdStr(productDTO.getShopId().toString());
          productDTO.setInSalesPrice(NumberUtil.toReserve(productDTO.getInSalesPrice(), NumberUtil.PRECISION));
          productDTOList.add(productDTO);
        } else {
          LOG.error("没有找到Product[{}]信息!", productRecommend.getMatchedProductLocalInfoId());
        }
      }
      ServiceManager.getService(IPromotionsService.class).addUsingPromotionToProductDTO(productDTOList.toArray(new ProductDTO[productDTOList.size()]));
    }
    return productDTOList;
  }

  /**
   * 获取指定商品的指定时间 的销量
   */
  @Override
  public List<ProductDTO> getSalesAmountByShopIdProductIdTime(Long shopId,Long startTime, Long endTime,Long... productId) {
    List<ProductDTO> productDTOList = new ArrayList<ProductDTO>();
    Map<Long,Double> productSaleStatMap = this.getSalesAmountMapByShopIdProductIdTime(shopId,startTime,endTime,productId);
    IProductService productService = ServiceManager.getService(IProductService.class);
    Map<Long, ProductDTO> productDTOMap = productService.getProductDTOMapByProductLocalInfoIds(shopId, productSaleStatMap.keySet());
    if (CollectionUtil.isEmpty(productDTOMap.values())) {
      return productDTOList;
    }
    for (ProductDTO productDTO : productDTOMap.values()) {
      productDTO.setSalesAmount(productSaleStatMap.get(productDTO.getProductLocalInfoId()));
      productDTOList.add(productDTO);
    }
    return productDTOList;
  }

  /**
   * 最近30天销量
   * @param shopId
   * @param productId
   * @return
   */
  @Override
  public Map<Long,Double> getSalesAmountMapByShopIdProductIdTime(Long shopId,Long startTime, Long endTime,Long... productId) {
    Map<Long,Double> productSaleStatMap = new HashMap<Long, Double>();
    TxnWriter writer = txnDaoManager.getWriter();

    List<SalesStatDTO> salesStatDTOList = writer.getLastWeekSalesByShopId(shopId, startTime, endTime,productId);

    List<SalesStatDTO> statDTOs = new ArrayList<SalesStatDTO>();

    if (CollectionUtils.isNotEmpty(salesStatDTOList)) {
      for (SalesStatDTO salesStatDTO : salesStatDTOList) {
        SalesStat stat = writer.getLatestSalesStatBeforeTime(shopId, salesStatDTO.getProductId(), endTime);
        if (stat == null) {
          continue;
        }
        SalesStatDTO statDTO = stat.toDTO();
        SalesStat preStat = writer.getLatestSalesStatBeforeTime(shopId, salesStatDTO.getProductId(), startTime);
        if (preStat != null) {
          statDTO.setAmount(statDTO.getAmount() - preStat.getAmount());
        }
        statDTOs.add(statDTO);
      }
    }

    List<SalesStatDTO> salesStatChangeList = writer.getLastWeekSalesChangeByShopId(shopId, startTime, endTime,productId);
    statDTOs.addAll(salesStatChangeList);

    if (CollectionUtil.isEmpty(statDTOs)) {
      return productSaleStatMap;
    }

    for (SalesStatDTO salesStatDTO : statDTOs) {
      productSaleStatMap.put(salesStatDTO.getProductId(),NumberUtil.doubleVal(productSaleStatMap.get(salesStatDTO.getProductId()))+NumberUtil.doubleVal(salesStatDTO.getAmount()));
    }
    return productSaleStatMap;
  }

}
