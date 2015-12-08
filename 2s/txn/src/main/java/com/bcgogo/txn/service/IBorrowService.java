package com.bcgogo.txn.service;

import com.bcgogo.common.AllListResult;
import com.bcgogo.common.Result;
import com.bcgogo.txn.dto.BorrowOrderDTO;
import com.bcgogo.txn.dto.BorrowOrderItemDTO;
import com.bcgogo.txn.dto.ReturnOrderDTO;
import com.bcgogo.txn.dto.ReturnOrderItemDTO;
import com.bcgogo.txn.model.BorrowOrder;
import com.bcgogo.txn.model.BorrowOrderItem;
import com.bcgogo.user.dto.CustomerOrSupplierDTO;
import com.bcgogo.user.dto.SupplierDTO;

import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 13-3-5
 * Time: 下午12:45
 * To change this template use File | Settings | File Templates.
 */
public interface IBorrowService {

  /**
   * 保存借调单
   * @param borrowOrderDTO
   * @throws Exception
   */
  void saveBorrowOrder(BorrowOrderDTO borrowOrderDTO) throws Exception;

  /**
   * 保存归还单
   * @param result
   * @param returnOrderDTO
   * @return
   * @throws Exception
   */
  Result saveReturnOrder(AllListResult result,ReturnOrderDTO returnOrderDTO) throws Exception;

  /**
   * 根据前台提交的表单，初始化并验证借调单
   * @param result
   * @param borrowOrderDTO
   * @return
   * @throws Exception
   */
  Result initAndVerifySaveBorrowOrder(AllListResult result, BorrowOrderDTO borrowOrderDTO) throws Exception;

  /**
   * 根据前台提交的表单，初始化并验证归还单
   * @param result
   * @param returnOrderDTO
   * @return
   * @throws Exception
   */
  Result verifyAndInitSaveReturnOrder(AllListResult result,ReturnOrderDTO returnOrderDTO) throws Exception;

  List<BorrowOrder> getBorrowOrders(BorrowOrderDTO searchCondition);

  /**
   * 获取各种借调单的统计数据，比如归还，未还。。
   * @param searchCondition
   * @return
   */
  List getBorrowOrderStat(BorrowOrderDTO searchCondition);

  BorrowOrder getBorrowOrderById(Long shopId,Long borrowOrderId);

  List<BorrowOrderItem> getBorrowOrderItemByOrderId(Long shopId,Long borrowOrderId);

  List<BorrowOrderItemDTO> getBorrowOrderItemDTOByOrderId(Long shopId,Long... borrowOrderId);

  List<BorrowOrderItem> getBorrowOrderItemByIds(Long shopId,List<Long> itemIdList);

  /**
   * 获取借调单归还的流水记录
   * @param shopId
   * @param borrowOrderId
   * @return
   * @throws Exception
   */
  List<ReturnOrderDTO> getReturnRunningRecord(Long shopId,Long borrowOrderId) throws Exception;


  List<ReturnOrderDTO> getReturnOrderDTOsByBorrowOrderIds(Long shopId,Long... borrowOrderId) throws Exception;

  List<ReturnOrderItemDTO> getReturnOrderItemDTOsByBorrowOrderIds(Long shopId,Long... borrowOrderId) throws Exception;

  int countBorrowOrders(BorrowOrderDTO searchCondition);

  Map<Long,List<ReturnOrderItemDTO>> getReturnOrderItemsByOrderIds(Long shopId,List<Long> orderIds);

  /**
   * 保存借调单的同时，保存客户，或供应商
   * @param result
   * @param csDTO
   * @return
   * @throws Exception
   */
  SupplierDTO saveOrUpdateSupplierByCsDTO(Result result,CustomerOrSupplierDTO csDTO) throws Exception;

  List getBorrowOrderByBorrower(Long shopId,String borrower);

}
