package com.bcgogo.txn.service.productThrough;

import com.bcgogo.enums.OrderTypes;
import com.bcgogo.txn.dto.BcgogoOrderDto;
import com.bcgogo.txn.dto.OutStorageRelationDTO;
import com.bcgogo.txn.dto.SupplierInventoryDTO;
import com.bcgogo.txn.model.TxnWriter;

import java.util.List;

/**
 * 商品出入库打通单据处理 专用service
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 13-4-19
 * Time: 下午2:52
 * To change this template use File | Settings | File Templates.
 */
public interface IProductThroughOrderService {

  /**
   * 商品库存减少的单据作废处理流程  只接受 入库单和销售退货单 作废
   * @param bcgogoOrderDto
   * @param orderType
   */
  public List<SupplierInventoryDTO> inventoryAddOrderRepeal(BcgogoOrderDto bcgogoOrderDto, OrderTypes orderType);

  /**
   * 商品库存增加的单据作废处理流程  只接受施工单和销售单 入库退货单  作废
   * @param bcgogoOrderDto
   * @param orderType
   */
  public List<SupplierInventoryDTO> inventoryReduceOrderRepeal(BcgogoOrderDto bcgogoOrderDto, OrderTypes orderType);


   /**
   * 单据作废时 根据出入库关系记录 回滚每个item的剩余库存
   *
   * @param outStorageRelationDTOs
   * @param writer
   * @throws Exception
   */
  public void updateItemRemainAmount(BcgogoOrderDto bcgogoOrderDto,OutStorageRelationDTO[] outStorageRelationDTOs, TxnWriter writer) throws Exception;


  public void updateItemRemainAmountByOrder(BcgogoOrderDto bcgogoOrderDto,OrderTypes orderType) throws Exception;


}
