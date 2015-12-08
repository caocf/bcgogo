package com.bcgogo.txn.service;

import com.bcgogo.common.Result;
import com.bcgogo.search.dto.OrderSearchConditionDTO;
import com.bcgogo.txn.dto.PurchaseReturnDTO;
import com.bcgogo.txn.dto.SalesReturnDTO;
import com.bcgogo.txn.dto.SalesReturnItemDTO;
import org.springframework.ui.ModelMap;

import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Qiuxinyu
 * Date: 12-7-16
 * Time: 上午10:29
 * To change this template use File | Settings | File Templates.
 */
public interface ISaleReturnOrderService {

  //根据采购退货单生成销售退货单
  public void createSalesReturnDTOByPurchaseReturnOrderDTO(PurchaseReturnDTO purchaseReturnDTO) throws Exception;

  public SalesReturnDTO getSalesReturnDTOById(Long shopId,Long id) throws Exception;
  public SalesReturnDTO fillSalesReturnItemDTOsDetailInfo(SalesReturnDTO salesReturnDTO) throws Exception;

  public SalesReturnDTO getSalesReturnDTOByPurchaseReturnOrderId(Long purchaseReturnOrderId) throws Exception;

  public SalesReturnDTO getSimpleSalesReturnDTOByPurchaseReturnOrderId(Long shopId,Long purchaseReturnOrderId) throws Exception;

  public SalesReturnDTO acceptSalesReturnDTO(SalesReturnDTO salesReturnDTO) throws Exception;

  public SalesReturnDTO refuseSalesReturnDTO(SalesReturnDTO salesReturnDTO) throws Exception;

  Result settleSalesReturn(SalesReturnDTO salesReturnDTO, Long userId, Map<Long, Long> supplierNewProductIdOldProductIdMap);

  Map<Long, Long> handleProductForSalesReturnOrder(SalesReturnDTO salesReturnDTO) throws Exception;

  public Result validateSalesReturnBeforeSettle(SalesReturnDTO salesReturnDTO) throws Exception;

  public Result settleSalesReturnForNormal(SalesReturnDTO salesReturnDTO) throws Exception;

  public SalesReturnDTO createSalesReturnByOrderId(ModelMap model,SalesReturnDTO salesReturnDTO) throws Exception;

  public SalesReturnDTO getCustomerInfoByCustomerInfo(ModelMap model,SalesReturnDTO salesReturnDTO ,String customerId,String customerName) throws Exception;

  public SalesReturnDTO getCustomerInfoByProductIds(SalesReturnDTO salesReturnDTO ,String productIdStr) throws Exception;


  public List getReturnStatByCondition(OrderSearchConditionDTO orderSearchConditionDTO, int startPageNo, int maxRows);

  void repealOrderInTxn(SalesReturnDTO salesReturnDTO, Long toStorehouseId);

  Result validateEnoughInventoryForRepeal(SalesReturnDTO salesReturnDTO);

  void updateCustomerInfoByRepeal(SalesReturnDTO salesReturnDTO);

  Result validateStorehouseInventoryForRepeal(SalesReturnDTO salesReturnDTO) throws Exception;

  /**
   * 通过id和shopId 获取salesReturnDTO
   *
   * @param id
   * @return
   */
  public SalesReturnItemDTO getSalesReturnOrderItemDTOById(Long id);


  List<SalesReturnDTO> getUnsettledSalesReturnDTOsByCustomerId(Long shopId,Long customerId);
}
