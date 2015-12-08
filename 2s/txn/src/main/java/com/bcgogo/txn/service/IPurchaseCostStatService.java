package com.bcgogo.txn.service;

import com.bcgogo.enums.OrderStatus;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.stat.dto.PurchaseInventoryStatDTO;
import com.bcgogo.stat.dto.SalesStatCondition;
import com.bcgogo.stat.dto.SupplierTranStatDTO;
import com.bcgogo.txn.dto.*;
import com.bcgogo.txn.model.PurchaseInventoryMonthStat;
import com.bcgogo.txn.model.PurchaseInventoryStat;
import com.bcgogo.txn.model.PurchaseInventoryStatChange;
import org.springframework.ui.Model;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Jimuchen
 * Date: 12-10-24
 * Time: 下午5:56
 */
public interface IPurchaseCostStatService {
  void purchaseCostStat(PurchaseInventoryDTO purchaseInventoryDTO, boolean isRepeal);

  void purchaseReturnStat(PurchaseReturnDTO purchaseReturnDTO,boolean isRepeal);

  void salesStat(BcgogoOrderDto salesOrderDTO,OrderStatus orderStatus);

  PurchaseInventoryStatDTO queryCostStat(Long shopId, int year, int month, boolean allYear, PurchaseInventoryStatDTO purchaseInventoryStatDTO);

  SupplierTranStatDTO querySupplierTranStat(Long shopId, int year, int month, boolean allYear, Long id);

  List<SupplierTranStatDTO> queryTopSupplierTranMonthStat(Long shopId, int year, int month, boolean allYear, int topLimit);

  double querySupplierTranTotal(Long shopId, int year, int month, boolean allYear);

  List<PurchaseInventoryStatDTO> queryTopPurchaseInventoryMonthStat(Long shopId, int year, int month, boolean allYear, String[] queryFields, int topLimit);

  double queryPurchaseInventoryTotal(Long shopId, int year, int month, boolean allYear);

  List<PurchaseInventoryMonthStat> getPurchaseInventoryMonthStatByProperties(Long shopId, String name, String brand, String vehicleBrand, String vehicleModel);

  List<PurchaseInventoryMonthStat> purchaseCostStatForProductInRange(Long shopId, Long productId, long begin, long end) throws Exception;

  void batchSaveOrUpdateInventoryCostMonthStat(List<PurchaseInventoryMonthStat> oldStats);

  List<ProductDTO> querySalesStatByCondition(Long shopId,SalesStatCondition salesStatCondition);

  List<String> countSalesStatByCondition(Long shopId,SalesStatCondition salesStatCondition);

  List<String> countBadSalesStatByCondition(Long shopId,SalesStatCondition salesStatCondition);

  List<ProductDTO> queryBadSalesStatByCondition(Long shopId,SalesStatCondition salesStatCondition);

  List<PriceFluctuationStatDTO> queryTopPurchaseInventoryLastTwelveMonthStat(Long shopId, int limit);

  public void saveOrUpdatePurchaseInventoryStatChange(Long shopId, Long productId, Double addAmount, Long vestDate);

  public List<Object[]> queryAllProductPriceFluctuation(Long startTime, Long endTime);

  public void savePriceFluctuationStat(List<Object[]> list);

//  Map<String,Object> getPriceFluctuationLineChartData(Long shopId, Long productId, List<Long> timePointList);
  List<List<Object>> getPriceFluctuationLineChartData(Long shopId, Long productId, Long startTime, Long endTime);

  PurchaseInventoryMonthStat purchaseReturnCostStatForProductInRange(Long shopId, Long productId, long begin, long end) throws Exception;

  public List<String> countTotalReturnByCondition(Long shopId,SalesStatCondition salesStatCondition);

  public List<PurchaseReturnMonthStatDTO> queryPurchaseReturnByCondition(Long shopId, SalesStatCondition salesStatCondition);

  public void getReturnInfo(Model model,List<PurchaseReturnMonthStatDTO> purchaseReturnMonthStatDTOList,SalesStatCondition salesStatCondition,double total,double totalAmount,int totalSize);

  public List<ProductDTO> getProductInfo(List<ProductDTO> productDTOList,Long shopId);

  PurchaseInventoryMonthStat getPurchaseInventoryMonthStatByPropertiesYearMonth(Long shopId, String name, String brand, String vehicleBrand, String vehicleModel, int statYear, int statMonth);

  public PurchaseInventoryStat getCostStat(Long shopId, Long productId, long fromTime, long endTime);

  public PurchaseInventoryStatChange getCostStatChange(Long shopId, Long productId, long fromTime, long endTime);

}
