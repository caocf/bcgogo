package com.bcgogo.txn.service.productThrough;

import com.bcgogo.enums.OrderStatus;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.txn.dto.*;
import com.bcgogo.txn.model.SupplierInventory;
import com.bcgogo.txn.model.TxnWriter;

import java.util.List;
import java.util.Map;

/**
 * 库存增加专用service
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 13-5-2
 * Time: 上午11:51
 * To change this template use File | Settings | File Templates.
 */
public interface IProductInStorageService {


  /**
   * 库存增加的单据 商品-库存打通逻辑
   *
   * @param bcgogoOrderDto
   * @param orderType
   * @param orderStatus
   * @param txnWriter
   */
  public void productThroughByOrder(BcgogoOrderDto bcgogoOrderDto, OrderTypes orderType, OrderStatus orderStatus, TxnWriter txnWriter);

  /**
   * 库存增加的单据 商品-库存打通逻辑
   *
   * @param bcgogoOrderDto
   * @param orderType
   * @param orderStatus
   */
  public void productThroughByOrder(BcgogoOrderDto bcgogoOrderDto, OrderTypes orderType, OrderStatus orderStatus);

  /**
   * 库存增加单据 保存商品入库记录 入库单 销售退货单 销售单、施工单作废 盘点单 内部退料单 维修退料单 仓库调拨单 借调单归还
   *
   * @param bcgogoOrderDto
   * @param orderType
   * @param orderStatus
   * @return
   */
  public List<InStorageRecordDTO> getInStorageRecordByOrder(BcgogoOrderDto bcgogoOrderDto, OrderTypes orderType, OrderStatus orderStatus);

  /**
   * 保存库存增加记录
   *
   * @param inStorageRecordDTOList
   */
  public void saveOrUpdateInStorageRecordDTO(List<InStorageRecordDTO> inStorageRecordDTOList,TxnWriter txnWriter);

  /**
   * 保存supplier_inventory 根据库存增加记录
   *
   * @param inStorageRecordDTOList
   * @return
   */
  public Map<Long, List<SupplierInventoryDTO>> getSupplierInventoryList(List<InStorageRecordDTO> inStorageRecordDTOList);

  /**
   * 库存盘点单打通逻辑
   * @param inventoryCheckDTO
   */
  public void productThroughByInventoryCheck(InventoryCheckDTO inventoryCheckDTO,TxnWriter txnWriter);

  /**
   * 对商品库存增加的单据进行排序
   * @param inStorageRecordDTOList
   */
  public List<InStorageRecordDTO> sortInStorageRecordList(List<InStorageRecordDTO> inStorageRecordDTOList);

  /**
   * 仓库调拨单处理流程
   * @param allocateRecordDTO
   * @param txnWriter
   */
  public void productThroughByAllocateRecord(AllocateRecordDTO allocateRecordDTO,TxnWriter txnWriter);

  /**
   * 非选择供应商的版本 把每个商品库存增加记录的价格 设置成平均价
   * @param inStorageRecordDTOList
   */
  public void setPriceByInStorageRecordList(List<InStorageRecordDTO> inStorageRecordDTOList);

  /**
   * 对供应商库存进行排序
   * @param supplierInventoryList
   */
  public List<SupplierInventory> sortSupplierInventoryList(List<SupplierInventory> supplierInventoryList);

  /**
   * 维修领料中退料操作库存增加出入库打通方法
   * @param repairPickingDTO
   * @param repairPickingItemDTOs
   * @param writer
   */
  public void handelRepairPickingInStorageService(RepairPickingDTO repairPickingDTO,List<RepairPickingItemDTO> repairPickingItemDTOs,TxnWriter writer);



}
