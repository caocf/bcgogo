package com.bcgogo.txn.service.productThrough;

import com.bcgogo.enums.OrderStatus;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.txn.dto.*;
import com.bcgogo.txn.model.Inventory;
import com.bcgogo.txn.model.TxnWriter;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * 库存减少的单据打通逻辑专用service
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 13-5-2
 * Time: 上午11:51
 * To change this template use File | Settings | File Templates.
 */
public interface IProductOutStorageService {


  /**
   * 销售退货单作废库存打通
   * @param salesReturnDTO
   */
  public void productThroughForSalesReturnRepeal(SalesReturnDTO salesReturnDTO);
    /**
   * 库存增加的单据 商品-库存打通逻辑
   *
   * @param bcgogoOrderDto
   * @param orderType
   * @param orderStatus
   */
  public void productThroughByOrder(BcgogoOrderDto bcgogoOrderDto, OrderTypes orderType, OrderStatus orderStatus);

  /**
   * 库存减少的单据 商品-库存打通逻辑
   *
   * @param bcgogoOrderDto
   * @param orderType
   * @param orderStatus
   * @param txnWriter
   */
  public void productThroughByOrder(BcgogoOrderDto bcgogoOrderDto, OrderTypes orderType, OrderStatus orderStatus, TxnWriter txnWriter,Map<Long,Inventory> inventoryMap);

  /**
   * 库存减少的单据
   *
   * @param bcgogoOrderDto
   * @param orderType
   * @param orderStatus
   * @return
   */
  public List<InStorageRecordDTO> getInStorageRecordByOrder(BcgogoOrderDto bcgogoOrderDto, OrderTypes orderType, OrderStatus orderStatus);


  /**
   * 保存supplier_inventory 根据库存增加记录
   *
   * @param outStorageRelationDTOList
   * @return
   */
  public Map<Long, List<SupplierInventoryDTO>> getSupplierInventoryList(List<OutStorageRelationDTO> outStorageRelationDTOList);

  /**
   * 保存商品减少单据 打通记录
   * @param outStorageRelationDTOList
   * @param writer
   */
  public void saveOutStorageRelation(List<OutStorageRelationDTO> outStorageRelationDTOList,TxnWriter writer);


  /**
   * 商品出入库打通 入库单作废专用处理
   * @param bcgogoOrderDto
   */
  public Map<OrderTypes,HashSet<Long>> productThroughByInventoryRepeal(BcgogoOrderDto bcgogoOrderDto);

   /**
   * 入库单作废时判断该供应商的库存是否充足
   * @param purchaseInventoryDTO
   * @return
   */
  public boolean validateBeforeInventoryRepeal(PurchaseInventoryDTO purchaseInventoryDTO);

}
