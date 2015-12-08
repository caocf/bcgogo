package com.bcgogo.txnRead.service;

import com.bcgogo.enums.txn.preBuyOrder.BusinessChanceType;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.product.service.IProductService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.PreBuyOrderItemDTO;
import com.bcgogo.txn.dto.SalesStatDTO;
import com.bcgogo.txn.model.PreBuyOrderItem;
import com.bcgogo.txn.model.SalesStat;
import com.bcgogo.txnRead.model.TxnReadDaoManager;
import com.bcgogo.txnRead.model.TxnReadReader;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.DateUtil;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 13-6-17
 * Time: 下午1:18
 * To change this template use File | Settings | File Templates.
 */
@Service
public class RecommendReadService implements IRecommendReadService {
  private static final Logger LOG = LoggerFactory.getLogger(RecommendReadService.class);
  private static final int TOP_SALE_NUM = 10; //每个店默认查询销量前十的商品

  @Autowired
  private TxnReadDaoManager txnReadDaoManager;

  @Override
  public List<PreBuyOrderItemDTO> getValidPreBuyOrderItemDTOByShopId(Long shopId,BusinessChanceType... businessChanceType) throws Exception {
    TxnReadReader txnReader = txnReadDaoManager.getReader();
    List<PreBuyOrderItemDTO> preBuyOrderItemDTOList = new ArrayList<PreBuyOrderItemDTO>();
    List<PreBuyOrderItem> preBuyOrderItemList = txnReader.getValidPreBuyOrderItemByShopId(shopId,businessChanceType);
    if(CollectionUtils.isNotEmpty(preBuyOrderItemList)){
      for(PreBuyOrderItem preBuyOrderItem: preBuyOrderItemList){
        preBuyOrderItemDTOList.add(preBuyOrderItem.toDTO());
      }
    }
    return preBuyOrderItemDTOList;
  }

  /**
   * 获取上一月某个店铺销量前十的商品
   *
   * @param shopId
   * @return
   */
  public List<ProductDTO> getLastMonthTopTenSalesByShopId(Long shopId) {

    List<ProductDTO> productDTOList = new ArrayList<ProductDTO>();
    Map<Long,Double> productSaleStatMap = new HashMap<Long, Double>();
    TxnReadReader txnReader = txnReadDaoManager.getReader();

    Long startTime = DateUtil.getLastMonthTime(Calendar.getInstance());
    startTime = DateUtil.getStartTimeOfTimeDay(startTime);
    Long endTime = System.currentTimeMillis();
    List<SalesStatDTO> salesStatDTOList = txnReader.getLastWeekSalesByShopId(shopId, startTime, endTime);

     List<SalesStatDTO> statDTOs = new ArrayList<SalesStatDTO>();

    if (CollectionUtils.isNotEmpty(salesStatDTOList)) {
      for (SalesStatDTO salesStatDTO : salesStatDTOList) {
        SalesStat stat = txnReader.getLatestSalesStatBeforeTime(shopId, salesStatDTO.getProductId(), endTime);
        if (stat == null) {
          continue;
        }
        SalesStatDTO statDTO = stat.toDTO();
        SalesStat preStat = txnReader.getLatestSalesStatBeforeTime(shopId, salesStatDTO.getProductId(), startTime);
        if (preStat != null) {
          statDTO.setAmount(statDTO.getAmount() - preStat.getAmount());
        }
        statDTOs.add(statDTO);
      }
    }

    List<SalesStatDTO> salesStatChangeList = txnReader.getLastWeekSalesChangeByShopId(shopId, startTime, endTime);
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
        productSaleStatMap.put(resultList.get(index).getProductId(),resultList.get(index).getAmount());
      }
    }

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
}
