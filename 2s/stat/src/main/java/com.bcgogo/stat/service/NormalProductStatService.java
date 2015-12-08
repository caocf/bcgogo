package com.bcgogo.stat.service;

import com.bcgogo.common.Pager;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.enums.Product.NormalProductStatType;
import com.bcgogo.enums.shop.ShopKind;
import com.bcgogo.product.ProductCategory.NormalProductStatSearchResult;
import com.bcgogo.product.dto.NormalProductDTO;
import com.bcgogo.product.model.ProductLocalInfo;
import com.bcgogo.product.service.IProductService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.InventoryDTO;
import com.bcgogo.txn.dto.NormalProductInventoryStatDTO;
import com.bcgogo.txn.dto.PurchaseInventoryItemDTO;
import com.bcgogo.txn.model.NormalProductInventoryStat;
import com.bcgogo.txn.service.ITxnService;
import com.bcgogo.user.dto.permission.ShopVersionDTO;
import com.bcgogo.user.service.permission.IShopVersionService;
import com.bcgogo.utils.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 后台CRM采购分析统计专用
 * Created by IntelliJ IDEA.
 * User: liuWei
 * Date: 13-2-7
 * Time: 下午5:03
 * To change this template use File | Settings | File Templates.
 */
@Component
public class NormalProductStatService implements INormalProductStatService {

  /**
   * 根据标准产品id和默认的shopId -1 表示全部店铺的统计值 统计该标准产品采购数据
   *
   * @param normalProductId
   * @param shopId
   * @return
   * @throws Exception
   */
  public Map<NormalProductStatType, NormalProductInventoryStatDTO> countStatDateByNormalProductId(Long normalProductId, Long shopId) throws Exception {
    IProductService productService = ServiceManager.getService(IProductService.class);
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    IConfigService configService = ServiceManager.getService(IConfigService.class);

    List<ProductLocalInfo> productLocalInfoList = productService.getProductLocalInfoByNormalProductId(normalProductId);
    if (CollectionUtils.isEmpty(productLocalInfoList)) {
      return null;
    }

    Set<Long> shopIdSet = new HashSet<Long>();

    for (ProductLocalInfo productLocalInfo : productLocalInfoList) {
      shopIdSet.add(productLocalInfo.getShopId());
    }
    if(CollectionUtils.isEmpty(shopIdSet)){
      return null;
    }

    Map<Long,ShopDTO> shopDTOMap = configService.getShopByShopId(shopIdSet.toArray(new Long[shopIdSet.size()]));
    if(MapUtils.isEmpty(shopDTOMap)){
      return null;
    }


    NormalProductInventoryStatDTO weekStatDTO = this.getNormalStatDTO(shopId, normalProductId, NormalProductStatType.WEEK);
    NormalProductInventoryStatDTO monthStatDTO = this.getNormalStatDTO(shopId, normalProductId, NormalProductStatType.MONTH);
    NormalProductInventoryStatDTO threeMonthStatDTO = this.getNormalStatDTO(shopId, normalProductId, NormalProductStatType.THREE_MONTH);
    NormalProductInventoryStatDTO halfYearStatDTO = this.getNormalStatDTO(shopId, normalProductId, NormalProductStatType.HALF_YEAR);
    NormalProductInventoryStatDTO yearStatDTO = this.getNormalStatDTO(shopId, normalProductId, NormalProductStatType.YEAR);

    Map<NormalProductStatType, NormalProductInventoryStatDTO> totalMap = new HashMap<NormalProductStatType, NormalProductInventoryStatDTO>();


    Map<Long,List<PurchaseInventoryItemDTO>> shopMap = new HashMap<Long,List<PurchaseInventoryItemDTO>>();
    for(ProductLocalInfo productLocalInfo : productLocalInfoList) {


      ShopDTO shopDTO = shopDTOMap.get(productLocalInfo.getShopId());
      if (shopDTO == null || shopDTO.getShopKind() != ShopKind.OFFICIAL) {
        continue;
      }

      List<PurchaseInventoryItemDTO> purchaseInventoryItemDTOList = txnService.getPurchaseInventoryItemByProductIdVestDate(productLocalInfo.getShopId(), productLocalInfo.getId(), DateUtil.getLastYearTime(DateUtil.getYesterday()));
      if (CollectionUtils.isEmpty(purchaseInventoryItemDTOList)) {
        continue;
      }
      if (shopMap.containsKey(productLocalInfo.getShopId())) {
        List<PurchaseInventoryItemDTO> list = shopMap.get(productLocalInfo.getShopId());
        list.addAll(purchaseInventoryItemDTOList);
        shopMap.remove(productLocalInfo.getShopId());
        shopMap.put(productLocalInfo.getShopId(), list);
      } else {
        shopMap.put(productLocalInfo.getShopId(), purchaseInventoryItemDTOList);
      }
    }
    Iterator keys = shopMap.keySet().iterator();

		while(keys.hasNext()) {
      Long productLocalShopId = (Long) keys.next();
      List<PurchaseInventoryItemDTO> purchaseInventoryItemDTOs = shopMap.get(productLocalShopId);

      Map<NormalProductStatType, NormalProductInventoryStatDTO> normalProductInventoryStatDTOMap = this.getNormalProductInventoryStatByTime(productLocalShopId, normalProductId, purchaseInventoryItemDTOs);
      if (MapUtils.isEmpty(normalProductInventoryStatDTOMap)) {
        continue;
      }
      txnService.saveNormalProductStatList(normalProductInventoryStatDTOMap.values());

      weekStatDTO = this.addStatDate(weekStatDTO, normalProductInventoryStatDTOMap.get(weekStatDTO.getNormalProductStatType()));
      monthStatDTO = this.addStatDate(monthStatDTO, normalProductInventoryStatDTOMap.get(monthStatDTO.getNormalProductStatType()));
      threeMonthStatDTO = this.addStatDate(threeMonthStatDTO, normalProductInventoryStatDTOMap.get(threeMonthStatDTO.getNormalProductStatType()));
      halfYearStatDTO = this.addStatDate(halfYearStatDTO, normalProductInventoryStatDTOMap.get(halfYearStatDTO.getNormalProductStatType()));
      yearStatDTO = this.addStatDate(yearStatDTO, normalProductInventoryStatDTOMap.get(yearStatDTO.getNormalProductStatType()));
    }

    if (weekStatDTO.getAmount() > 0) {
      totalMap.put(NormalProductStatType.WEEK, weekStatDTO);
    }
    if (monthStatDTO.getAmount() > 0) {
      totalMap.put(NormalProductStatType.MONTH, monthStatDTO);
    }
    if (threeMonthStatDTO.getAmount() > 0) {
      totalMap.put(NormalProductStatType.THREE_MONTH, threeMonthStatDTO);
    }
    if (halfYearStatDTO.getAmount() > 0) {
      totalMap.put(NormalProductStatType.HALF_YEAR, halfYearStatDTO);
    }
    if (yearStatDTO.getAmount() > 0) {
      totalMap.put(NormalProductStatType.YEAR, yearStatDTO);
    }

    txnService.saveNormalProductStatList(totalMap.values());
    return totalMap;
  }

  /**
   * 根据具体的采购项进行标准产品的统计
   *
   * @param shopId
   * @param normalProductId
   * @param purchaseInventoryItemDTOList
   * @return
   */
  @Override
  public Map<NormalProductStatType, NormalProductInventoryStatDTO> getNormalProductInventoryStatByTime(Long shopId, Long normalProductId, List<PurchaseInventoryItemDTO> purchaseInventoryItemDTOList) {
    ITxnService txnService = ServiceManager.getService(ITxnService.class);

    if (CollectionUtils.isEmpty(purchaseInventoryItemDTOList)) {
      return null;
    }
    Map<NormalProductStatType, NormalProductInventoryStatDTO> normalProductInventoryStatDTOMap = new HashMap<NormalProductStatType, NormalProductInventoryStatDTO>();

    NormalProductInventoryStatDTO weekStatDTO = this.getNormalStatDTO(shopId, normalProductId, NormalProductStatType.WEEK);
    NormalProductInventoryStatDTO monthStatDTO = this.getNormalStatDTO(shopId, normalProductId, NormalProductStatType.MONTH);
    NormalProductInventoryStatDTO threeMonthStatDTO = this.getNormalStatDTO(shopId, normalProductId, NormalProductStatType.THREE_MONTH);
    NormalProductInventoryStatDTO halfYearStatDTO = this.getNormalStatDTO(shopId, normalProductId, NormalProductStatType.HALF_YEAR);
    NormalProductInventoryStatDTO yearStatDTO = this.getNormalStatDTO(shopId, normalProductId, NormalProductStatType.YEAR);

    Map<Long,Map<NormalProductStatType,NormalProductInventoryStatDTO>> productStatMap = new HashMap<Long, Map<NormalProductStatType, NormalProductInventoryStatDTO>>();


    for (PurchaseInventoryItemDTO purchaseInventoryItemDTO : purchaseInventoryItemDTOList) {

      Map<NormalProductStatType,NormalProductInventoryStatDTO> productStatTypeMap = productStatMap.get(purchaseInventoryItemDTO.getProductId());

      if(MapUtils.isEmpty(productStatTypeMap)) {
        productStatTypeMap = new HashMap<NormalProductStatType, NormalProductInventoryStatDTO>();
      }


      //最近一周的统计
      if (purchaseInventoryItemDTO.getVestDate() >= DateUtil.getLastWeekTime(DateUtil.getYesterday())) {

        weekStatDTO.calculateFromPurchaseInventory(purchaseInventoryItemDTO,false);

        NormalProductInventoryStatDTO thisProductWeekStatDTO = productStatTypeMap.get(NormalProductStatType.WEEK);
        if (thisProductWeekStatDTO == null) {
          thisProductWeekStatDTO = this.getNormalStatDTO(shopId, normalProductId, NormalProductStatType.WEEK);
        }
        thisProductWeekStatDTO.calculateFromPurchaseInventory(purchaseInventoryItemDTO,true);
        productStatTypeMap.put(NormalProductStatType.WEEK, thisProductWeekStatDTO);

      }

      //最近一个月的统计
      if (purchaseInventoryItemDTO.getVestDate() >= DateUtil.getLastMonthTime(DateUtil.getYesterday())) {
        monthStatDTO.calculateFromPurchaseInventory(purchaseInventoryItemDTO,false);
        NormalProductInventoryStatDTO thisProductMonthStatDTO = productStatTypeMap.get(NormalProductStatType.MONTH);
        if (thisProductMonthStatDTO == null) {
          thisProductMonthStatDTO = this.getNormalStatDTO(shopId, normalProductId, NormalProductStatType.MONTH);
        }
        thisProductMonthStatDTO.calculateFromPurchaseInventory(purchaseInventoryItemDTO,true);
        productStatTypeMap.put(NormalProductStatType.MONTH, thisProductMonthStatDTO);

      }

      //最近三个月的统计
      if (purchaseInventoryItemDTO.getVestDate() >= DateUtil.getLastThreeMonthTime(DateUtil.getYesterday())) {

        threeMonthStatDTO.calculateFromPurchaseInventory(purchaseInventoryItemDTO,false);
        NormalProductInventoryStatDTO thisProductThreeMonthStatDTO = productStatTypeMap.get(NormalProductStatType.THREE_MONTH);
        if (thisProductThreeMonthStatDTO == null) {
          thisProductThreeMonthStatDTO = this.getNormalStatDTO(shopId, normalProductId, NormalProductStatType.THREE_MONTH);
        }
        thisProductThreeMonthStatDTO.calculateFromPurchaseInventory(purchaseInventoryItemDTO,true);
        productStatTypeMap.put(NormalProductStatType.THREE_MONTH, thisProductThreeMonthStatDTO);

      }
      //最近半年的统计
      if (purchaseInventoryItemDTO.getVestDate() >= DateUtil.getLastHalfYearTime(DateUtil.getYesterday())) {

        halfYearStatDTO.calculateFromPurchaseInventory(purchaseInventoryItemDTO,false);
        NormalProductInventoryStatDTO thisProductHalfYearStatDTO = productStatTypeMap.get(NormalProductStatType.HALF_YEAR);
        if (thisProductHalfYearStatDTO == null) {
          thisProductHalfYearStatDTO = this.getNormalStatDTO(shopId, normalProductId, NormalProductStatType.HALF_YEAR);
        }
        thisProductHalfYearStatDTO.calculateFromPurchaseInventory(purchaseInventoryItemDTO,true);
        productStatTypeMap.put(NormalProductStatType.HALF_YEAR, thisProductHalfYearStatDTO);

      }

      //最近一年的统计
      if (purchaseInventoryItemDTO.getVestDate() >= DateUtil.getLastYearTime(DateUtil.getYesterday())) {

        yearStatDTO.calculateFromPurchaseInventory(purchaseInventoryItemDTO,false);
        NormalProductInventoryStatDTO thisProductYearStatDTO = productStatTypeMap.get(NormalProductStatType.YEAR);
        if (thisProductYearStatDTO == null) {
          thisProductYearStatDTO = this.getNormalStatDTO(shopId, normalProductId, NormalProductStatType.YEAR);
        }
        thisProductYearStatDTO.calculateFromPurchaseInventory(purchaseInventoryItemDTO,true);
        productStatTypeMap.put(NormalProductStatType.YEAR, thisProductYearStatDTO);

      }

      productStatMap.put(purchaseInventoryItemDTO.getProductId(),productStatTypeMap);
    }

    for (Map map : productStatMap.values()) {
      txnService.saveNormalProductStatList(map.values());
    }


    if (weekStatDTO.getAmount() > 0) {
      normalProductInventoryStatDTOMap.put(NormalProductStatType.WEEK, weekStatDTO);
    }
    if (monthStatDTO.getAmount() > 0) {
      normalProductInventoryStatDTOMap.put(NormalProductStatType.MONTH, monthStatDTO);
    }
    if (threeMonthStatDTO.getAmount() > 0) {
      normalProductInventoryStatDTOMap.put(NormalProductStatType.THREE_MONTH, threeMonthStatDTO);
    }
    if (halfYearStatDTO.getAmount() > 0) {
      normalProductInventoryStatDTOMap.put(NormalProductStatType.HALF_YEAR, halfYearStatDTO);
    }
    if (yearStatDTO.getAmount() > 0) {
      normalProductInventoryStatDTOMap.put(NormalProductStatType.YEAR, yearStatDTO);
    }
    return normalProductInventoryStatDTOMap;
  }

  /**
   * 生成统计封装类
   *
   * @param shopId
   * @param normalProductId
   * @param normalProductStatType
   * @return
   */
  public NormalProductInventoryStatDTO getNormalStatDTO(Long shopId, Long normalProductId, NormalProductStatType normalProductStatType) {

    NormalProductInventoryStatDTO normalProductInventoryStatDTO = new NormalProductInventoryStatDTO();
    normalProductInventoryStatDTO.setShopId(shopId);
    normalProductInventoryStatDTO.setNormalProductId(normalProductId);
    normalProductInventoryStatDTO.setNormalProductStatType(normalProductStatType);
    normalProductInventoryStatDTO.setBottomPriceSet(false);
    return normalProductInventoryStatDTO;

  }


  /**
   * 两个统计数据进行相加
   *
   * @param normalProductInventoryStatDTO
   * @param addStatDTO
   * @return
   */
  public NormalProductInventoryStatDTO addStatDate(NormalProductInventoryStatDTO normalProductInventoryStatDTO, NormalProductInventoryStatDTO addStatDTO) {
    if (addStatDTO == null) {
      return normalProductInventoryStatDTO;
    }

    if (!normalProductInventoryStatDTO.isBottomPriceSet()) {
      normalProductInventoryStatDTO.setBottomPrice(addStatDTO.getBottomPrice());
      normalProductInventoryStatDTO.setBottomPriceSet(true);
    }

    normalProductInventoryStatDTO.setAmount(normalProductInventoryStatDTO.getAmount() + addStatDTO.getAmount());
    normalProductInventoryStatDTO.setTotal(normalProductInventoryStatDTO.getTotal() + addStatDTO.getTotal());
    normalProductInventoryStatDTO.setTopPrice(normalProductInventoryStatDTO.getTopPrice() > addStatDTO.getTopPrice() ?
        normalProductInventoryStatDTO.getTopPrice() : addStatDTO.getTopPrice());
    normalProductInventoryStatDTO.setBottomPrice(normalProductInventoryStatDTO.getBottomPrice() < addStatDTO.getBottomPrice() ?
        normalProductInventoryStatDTO.getBottomPrice() : addStatDTO.getBottomPrice());
    normalProductInventoryStatDTO.setTimes(normalProductInventoryStatDTO.getTimes() + addStatDTO.getTimes());
    return normalProductInventoryStatDTO;
  }

  /**
   * 根据标准产品组 获得采购统计数据
   * @param shopIds
   * @param normalProductIds
   * @param normalProductStatType
   * @param pager
   * @param normalProductDTOMap
   * @return
   */
  public NormalProductStatSearchResult getStatDateByCondition(Long[] shopIds, Long[] normalProductIds, NormalProductStatType normalProductStatType, Pager pager,Map<Long,NormalProductDTO> normalProductDTOMap) {

    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    NormalProductStatSearchResult normalProductStatSearchResult = new NormalProductStatSearchResult();
    List<Long> totalNormalProductIds = txnService.countStatDateByNormalProductIds(shopIds, normalProductIds, normalProductStatType);
    if (CollectionUtils.isEmpty(totalNormalProductIds)) {
      return normalProductStatSearchResult;
    }

    normalProductStatSearchResult.setTotalRows(totalNormalProductIds.size());
    pager.setTotalRows(totalNormalProductIds.size());

    List<Long> normalProductIdList = new ArrayList<Long>();
    for (int i = 0; i < totalNormalProductIds.size(); i++) {
      if (i >= pager.getRowStart() && i < pager.getRowStart() + pager.getPageSize()) {
        normalProductIdList.add(totalNormalProductIds.get(i));
      }
    }
    if (CollectionUtils.isEmpty(normalProductIdList)) {
      return normalProductStatSearchResult;
    }

    List<NormalProductInventoryStat> normalProductInventoryStatList = txnService.getStatDateByNormalProductIds(shopIds, normalProductIdList.toArray(new Long[normalProductIdList.size()]), normalProductStatType);
    if (CollectionUtils.isEmpty(normalProductInventoryStatList)) {
      return normalProductStatSearchResult;
    }
    Collection<NormalProductInventoryStat> statList = normalProductInventoryStatList;

    Map<Long, NormalProductInventoryStat> inventoryStatMap = new HashMap<Long, NormalProductInventoryStat>();
    if (!(shopIds.length == 1 && shopIds[0] == StatConstant.EMPTY_SHOP_ID)) {
      for (NormalProductInventoryStat normalProductInventoryStat : normalProductInventoryStatList) {
        NormalProductInventoryStat inventoryStat = inventoryStatMap.get(normalProductInventoryStat.getNormalProductId());
        if (inventoryStat == null) {
          inventoryStatMap.put(normalProductInventoryStat.getNormalProductId(), normalProductInventoryStat);
        } else {
          inventoryStat.setAmount(inventoryStat.getAmount() + normalProductInventoryStat.getAmount());
          inventoryStat.setTotal(inventoryStat.getTotal() + normalProductInventoryStat.getTotal());
          inventoryStat.setTimes(inventoryStat.getTimes() + normalProductInventoryStat.getTimes());
          inventoryStat.setTopPrice(inventoryStat.getTopPrice() > normalProductInventoryStat.getTopPrice() ? inventoryStat.getTopPrice() : normalProductInventoryStat.getTopPrice());
          inventoryStat.setBottomPrice(inventoryStat.getBottomPrice() < normalProductInventoryStat.getBottomPrice() ? inventoryStat.getBottomPrice() : normalProductInventoryStat.getBottomPrice());
          inventoryStatMap.put(inventoryStat.getNormalProductId(), inventoryStat);
        }
      }
      statList = inventoryStatMap.values();
    }

    List<NormalProductInventoryStatDTO> normalProductInventoryStatDTOList = new ArrayList<NormalProductInventoryStatDTO>();
    for (NormalProductInventoryStat normalProductInventoryStat : statList) {
      NormalProductInventoryStatDTO normalProductInventoryStatDTO = this.getProductAndShopInfo(normalProductInventoryStat, normalProductDTOMap.get(normalProductInventoryStat.getNormalProductId()),
          null, null);
      if (normalProductInventoryStatDTO != null) {
        normalProductInventoryStatDTOList.add(normalProductInventoryStatDTO);
      }
    }
    normalProductStatSearchResult.setResults(normalProductInventoryStatDTOList);
    return normalProductStatSearchResult;
  }

  /**
   * 获得每个统计记录的 标准产品信息和店铺信息
   * @param normalProductInventoryStat
   * @return
   */
  public NormalProductInventoryStatDTO getProductAndShopInfo(NormalProductInventoryStat normalProductInventoryStat,NormalProductDTO normalProductDTO,ShopDTO shopDTO,ShopVersionDTO shopVersionDTO) {
    if (normalProductInventoryStat == null) {
      return null;
    }
    NormalProductInventoryStatDTO normalProductInventoryStatDTO = normalProductInventoryStat.toDTO();
    if (normalProductDTO != null) {
      normalProductInventoryStatDTO.setCommodityCode(StringUtil.isEmpty(normalProductDTO.getCommodityCode()) ? "--" : normalProductDTO.getCommodityCode());
      normalProductInventoryStatDTO.setNameAndBrand(StringUtil.isEmpty(normalProductDTO.getProductName()) ? "--" : normalProductDTO.getProductName() + "/" + (StringUtil.isEmpty(normalProductDTO.getBrand()) ? "--" : normalProductDTO.getBrand()));
      normalProductInventoryStatDTO.setSpecAndModel(StringUtil.isEmpty(normalProductDTO.getSpec()) ? "--" : normalProductDTO.getSpec() + "/" + (StringUtil.isEmpty(normalProductDTO.getModel()) ? "--" : normalProductDTO.getModel()));
      normalProductInventoryStatDTO.setProductVehicleBrand(StringUtil.isEmpty(normalProductDTO.getVehicleModel()) ? "--" : normalProductDTO.getVehicleModel() + "/" + (StringUtil.isEmpty(normalProductDTO.getVehicleBrand()) ? "--" : normalProductDTO.getVehicleBrand()));
      normalProductInventoryStatDTO.setUnit(StringUtil.isEmpty(normalProductDTO.getUnit()) ? "--" : normalProductDTO.getUnit());
    }

    if (normalProductInventoryStatDTO.getShopId() != StatConstant.EMPTY_SHOP_ID) {
      if (shopDTO != null) {
        normalProductInventoryStatDTO.setShopName(shopDTO.getName());
      }
      if (shopVersionDTO != null) {
        normalProductInventoryStatDTO.setShopVersion(shopVersionDTO.getValue());
      }
    }

    normalProductInventoryStatDTO.setPriceStr(normalProductInventoryStatDTO.getTopPrice() + "/" + normalProductInventoryStatDTO.getBottomPrice());
    return normalProductInventoryStatDTO;
  }


  /**
   * 根据标准产品组 获得采购统计数据
   *
   * @param shopIds
   * @param normalProductId
   * @param normalProductStatType
   * @param pager
   * @return
   */
  public NormalProductStatSearchResult getStatDetailByCondition(Long[] shopIds, Long normalProductId, NormalProductStatType normalProductStatType, Pager pager) throws Exception {

    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    IProductService productService = ServiceManager.getService(IProductService.class);
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    IShopVersionService shopVersionService = ServiceManager.getService(IShopVersionService.class);

    NormalProductStatSearchResult normalProductStatSearchResult = new NormalProductStatSearchResult();
    int totalRows = txnService.countStatDetailByNormalProductIds(shopIds, normalProductId, normalProductStatType);
    if (totalRows <= 0) {
      return normalProductStatSearchResult;
    }

    normalProductStatSearchResult.setTotalRows(totalRows);
    int currentPage = pager.getRowStart() / pager.getPageSize();
    pager = new Pager(totalRows, currentPage + 1, pager.getPageSize());


    List<NormalProductInventoryStat> normalProductInventoryStatList = txnService.getStatDetailByNormalProductIds(shopIds, normalProductId, normalProductStatType, pager);
    if (CollectionUtils.isEmpty(normalProductInventoryStatList)) {
      return normalProductStatSearchResult;
    }

    Set<Long> shopIdSet = new HashSet<Long>();
    Set<Long> productLocalIdSet = new HashSet<Long>();
    for (NormalProductInventoryStat normalProductInventoryStat : normalProductInventoryStatList) {
      shopIdSet.add(normalProductInventoryStat.getShopId());
      productLocalIdSet.add(normalProductInventoryStat.getProductLocalInfoId());
    }
    Map<Long, ShopDTO> shopDTOMap = configService.getShopByShopId(shopIdSet.toArray(new Long[shopIdSet.size()]));

    Map<Long, ShopVersionDTO> shopVersionDTOMap = shopVersionService.getAllShopVersionMap();


    List<NormalProductInventoryStatDTO> normalProductInventoryStatDTOList = new ArrayList<NormalProductInventoryStatDTO>();
    for (NormalProductInventoryStat normalProductInventoryStat : normalProductInventoryStatList) {
      ShopDTO shopDTO = shopDTOMap.get(normalProductInventoryStat.getShopId());
      NormalProductInventoryStatDTO normalProductInventoryStatDTO = this.getProductAndShopInfo(normalProductInventoryStat, null, shopDTO,
          shopVersionDTOMap.get(shopDTO.getShopVersionId()));
      String areaInfo = configService.getShopAreaInfoByShopDTO(shopDTO);
      normalProductInventoryStatDTO.setAreaInfo(areaInfo);

      InventoryDTO inventoryDTO = txnService.getInventoryByShopIdAndProductId(normalProductInventoryStat.getShopId(),normalProductInventoryStat.getProductLocalInfoId());
      if(inventoryDTO != null){
        normalProductInventoryStatDTO.setInventoryAmount(inventoryDTO.getAmount());
        normalProductInventoryStatDTO.setLastInventoryDate(DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_ALL,inventoryDTO.getLastStorageTime()));

        normalProductInventoryStatDTO.setUnit(inventoryDTO.getUnit());
      }

      if (normalProductInventoryStatDTO != null) {
        normalProductInventoryStatDTOList.add(normalProductInventoryStatDTO);
      }
    }
    normalProductStatSearchResult.setResults(normalProductInventoryStatDTOList);
    return normalProductStatSearchResult;
  }
}



