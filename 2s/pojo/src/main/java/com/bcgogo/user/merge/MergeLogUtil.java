package com.bcgogo.user.merge;

import com.bcgogo.enums.CustomerStatus;
import com.bcgogo.enums.MemberStatus;
import com.bcgogo.enums.VehicleStatus;
import com.bcgogo.utils.UserConstant;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 12-11-14
 * Time: 上午7:40
 * To change this template use File | Settings | File Templates.
 */
public class MergeLogUtil {

  public static MergeChangeLogDTO log_receivable_customerId(Long shopId,Long userId,Long recordId,Long oldValue,Long newValue){
    MergeChangeLogDTO mergeChangeLog=new MergeChangeLogDTO();
    mergeChangeLog.setShopId(shopId);
    mergeChangeLog.setUserId(userId);
    mergeChangeLog.setRecordId(recordId);
    mergeChangeLog.setModifiedTable("receivable");
    mergeChangeLog.setField("customer_id");
    mergeChangeLog.setOldValue(String.valueOf(oldValue));
    mergeChangeLog.setNewValue(String.valueOf(newValue));
    mergeChangeLog.setDescription(MergeChangeLogDTO.ModifyType.MODIFY_CUSTOMER_ID);
    return mergeChangeLog;
  }

  public static MergeChangeLogDTO log_debt_customerId(Long shopId,Long userId,Long recordId,Long oldValue,Long newValue){
    MergeChangeLogDTO mergeChangeLog=new MergeChangeLogDTO();
    mergeChangeLog.setShopId(shopId);
    mergeChangeLog.setUserId(userId);
    mergeChangeLog.setRecordId(recordId);
    mergeChangeLog.setModifiedTable("debt");
    mergeChangeLog.setField("customer_id");
    mergeChangeLog.setOldValue(String.valueOf(oldValue));
    mergeChangeLog.setNewValue(String.valueOf(newValue));
    mergeChangeLog.setDescription(MergeChangeLogDTO.ModifyType.MODIFY_CUSTOMER_ID);
    return mergeChangeLog;
  }

   public static MergeChangeLogDTO log_remind_event_customerId(Long shopId,Long userId,Long recordId,Long oldValue,Long newValue){
    MergeChangeLogDTO mergeChangeLog=new MergeChangeLogDTO();
    mergeChangeLog.setShopId(shopId);
    mergeChangeLog.setUserId(userId);
    mergeChangeLog.setRecordId(recordId);
    mergeChangeLog.setModifiedTable("remind_event");
    mergeChangeLog.setField("customer_id");
    mergeChangeLog.setOldValue(String.valueOf(oldValue));
    mergeChangeLog.setNewValue(String.valueOf(newValue));
    mergeChangeLog.setDescription(MergeChangeLogDTO.ModifyType.MODIFY_CUSTOMER_ID);
    return mergeChangeLog;
  }

  public static MergeChangeLogDTO log_disable_customerVehicle(Long shopId,Long userId,Long recordId){
    MergeChangeLogDTO mergeChangeLog=new MergeChangeLogDTO();
    mergeChangeLog.setShopId(shopId);
    mergeChangeLog.setUserId(userId);
    mergeChangeLog.setRecordId(recordId);
    mergeChangeLog.setModifiedTable("customer_vehicle");
    mergeChangeLog.setField("status");
    mergeChangeLog.setOldValue(VehicleStatus.ENABLED.toString());
    mergeChangeLog.setNewValue(VehicleStatus.DISABLED.toString());
    mergeChangeLog.setDescription(MergeChangeLogDTO.ModifyType.DELETE_RECORD);
    return mergeChangeLog;
  }


  public static MergeChangeLogDTO log_disable_customerRecord(Long shopId,Long userId,Long recordId){
    MergeChangeLogDTO mergeChangeLog=new MergeChangeLogDTO();
    mergeChangeLog.setShopId(shopId);
    mergeChangeLog.setUserId(userId);
    mergeChangeLog.setRecordId(recordId);
    mergeChangeLog.setModifiedTable("customer_record");
    mergeChangeLog.setField("status");
    mergeChangeLog.setOldValue(CustomerStatus.ENABLED.toString());
    mergeChangeLog.setNewValue(CustomerStatus.DISABLED.toString());
    mergeChangeLog.setDescription(MergeChangeLogDTO.ModifyType.DELETE_RECORD);
    return mergeChangeLog;
  }

  public static MergeChangeLogDTO log_disable_Member(Long shopId,Long userId,Long recordId){
    MergeChangeLogDTO mergeChangeLog=new MergeChangeLogDTO();
    mergeChangeLog.setShopId(shopId);
    mergeChangeLog.setUserId(userId);
    mergeChangeLog.setRecordId(recordId);
    mergeChangeLog.setModifiedTable("Member");
    mergeChangeLog.setField("status");
    mergeChangeLog.setOldValue(MemberStatus.ENABLED.toString());
    mergeChangeLog.setNewValue(MemberStatus.DISABLED.toString());
    mergeChangeLog.setDescription(MergeChangeLogDTO.ModifyType.DELETE_RECORD);
    return mergeChangeLog;
  }

  public static MergeChangeLogDTO log_repairOrder_customerId(Long shopId,Long userId,Long recordId,Long oldValue,Long newValue){
    MergeChangeLogDTO mergeChangeLog=new MergeChangeLogDTO();
    mergeChangeLog.setShopId(shopId);
    mergeChangeLog.setUserId(userId);
    mergeChangeLog.setRecordId(recordId);
    mergeChangeLog.setModifiedTable("repair_order");
    mergeChangeLog.setField("customer_id");
    mergeChangeLog.setOldValue(String.valueOf(oldValue));
    mergeChangeLog.setNewValue(String.valueOf(newValue));
    mergeChangeLog.setDescription(MergeChangeLogDTO.ModifyType.MODIFY_CUSTOMER_ID);
    return mergeChangeLog;
  }

  public static MergeChangeLogDTO log_statementAccountOrder(Long shopId,Long userId,Long recordId,Long oldValue,Long newValue,String csType){
    MergeChangeLogDTO mergeChangeLog=new MergeChangeLogDTO();
    mergeChangeLog.setShopId(shopId);
    mergeChangeLog.setUserId(userId);
    mergeChangeLog.setRecordId(recordId);
    mergeChangeLog.setModifiedTable("statement_account_order");
    mergeChangeLog.setOldValue(String.valueOf(oldValue));
    mergeChangeLog.setNewValue(String.valueOf(newValue));
    if(UserConstant.CSType.CUSTOMER.equals(csType)){
      mergeChangeLog.setField("customer_id");
      mergeChangeLog.setDescription(MergeChangeLogDTO.ModifyType.MODIFY_CUSTOMER_ID);
    }else {
      mergeChangeLog.setField("supplier_id");
      mergeChangeLog.setDescription(MergeChangeLogDTO.ModifyType.MODIFY_SUPPLIER_ID);
    }
    return mergeChangeLog;
  }

   public static MergeChangeLogDTO log_borrowOrder(Long shopId,Long userId,Long recordId,Long oldValue,Long newValue,String csType){
    MergeChangeLogDTO mergeChangeLog=new MergeChangeLogDTO();
    mergeChangeLog.setShopId(shopId);
    mergeChangeLog.setUserId(userId);
    mergeChangeLog.setRecordId(recordId);
    mergeChangeLog.setModifiedTable("borrow_order");
    mergeChangeLog.setField("borrower_id");
    mergeChangeLog.setOldValue(String.valueOf(oldValue));
    mergeChangeLog.setNewValue(String.valueOf(newValue));
    if(UserConstant.CSType.CUSTOMER.equals(csType)){
      mergeChangeLog.setDescription(MergeChangeLogDTO.ModifyType.MODIFY_CUSTOMER_ID);
    }else {
      mergeChangeLog.setDescription(MergeChangeLogDTO.ModifyType.MODIFY_SUPPLIER_ID);
    }
    return mergeChangeLog;
  }

   public static MergeChangeLogDTO log_supplierInventory_customerId(Long shopId,Long userId,Long recordId,Long oldValue,Long newValue){
    MergeChangeLogDTO mergeChangeLog=new MergeChangeLogDTO();
    mergeChangeLog.setShopId(shopId);
    mergeChangeLog.setUserId(userId);
    mergeChangeLog.setRecordId(recordId);
    mergeChangeLog.setModifiedTable("supplier_inventory");
    mergeChangeLog.setField("supplier_id");
    mergeChangeLog.setOldValue(String.valueOf(oldValue));
    mergeChangeLog.setNewValue(String.valueOf(newValue));
    mergeChangeLog.setDescription(MergeChangeLogDTO.ModifyType.MODIFY_SUPPLIER_ID);
    return mergeChangeLog;
  }

  public static MergeChangeLogDTO log_salesOrder_customerId(Long shopId,Long userId,Long recordId,Long oldValue,Long newValue){
    MergeChangeLogDTO mergeChangeLog=new MergeChangeLogDTO();
    mergeChangeLog.setShopId(shopId);
    mergeChangeLog.setUserId(userId);
    mergeChangeLog.setRecordId(recordId);
    mergeChangeLog.setModifiedTable("repair_order");
    mergeChangeLog.setField("customer_id");
    mergeChangeLog.setOldValue(String.valueOf(oldValue));
    mergeChangeLog.setNewValue(String.valueOf(newValue));
    mergeChangeLog.setDescription(MergeChangeLogDTO.ModifyType.MODIFY_CUSTOMER_ID);
    return mergeChangeLog;
  }

  public static MergeChangeLogDTO log_salesReturn_customerId(Long shopId,Long userId,Long recordId,Long oldValue,Long newValue){
    MergeChangeLogDTO mergeChangeLog=new MergeChangeLogDTO();
    mergeChangeLog.setShopId(shopId);
    mergeChangeLog.setUserId(userId);
    mergeChangeLog.setRecordId(recordId);
    mergeChangeLog.setModifiedTable("sales_return");
    mergeChangeLog.setField("customer_id");
    mergeChangeLog.setOldValue(String.valueOf(oldValue));
    mergeChangeLog.setNewValue(String.valueOf(newValue));
    mergeChangeLog.setDescription(MergeChangeLogDTO.ModifyType.MODIFY_CUSTOMER_ID);
    return mergeChangeLog;
  }

  public static MergeChangeLogDTO log_washBeautyOrder_customerId(Long shopId,Long userId,Long recordId,Long oldValue,Long newValue){
    MergeChangeLogDTO mergeChangeLog=new MergeChangeLogDTO();
    mergeChangeLog.setShopId(shopId);
    mergeChangeLog.setUserId(userId);
    mergeChangeLog.setRecordId(recordId);
    mergeChangeLog.setModifiedTable("wash_beauty_order");
    mergeChangeLog.setField("customer_id");
    mergeChangeLog.setOldValue(String.valueOf(oldValue));
    mergeChangeLog.setNewValue(String.valueOf(newValue));
    mergeChangeLog.setDescription(MergeChangeLogDTO.ModifyType.MODIFY_CUSTOMER_ID);
    return mergeChangeLog;
  }

  public static MergeChangeLogDTO log_orderIndex_customerId(Long shopId,Long userId,Long recordId,Long oldValue,Long newValue){
    MergeChangeLogDTO mergeChangeLog=new MergeChangeLogDTO();
    mergeChangeLog.setShopId(shopId);
    mergeChangeLog.setUserId(userId);
    mergeChangeLog.setRecordId(recordId);
    mergeChangeLog.setModifiedTable("order_index");
    mergeChangeLog.setField("customer_or_supplier_id");
    mergeChangeLog.setOldValue(String.valueOf(oldValue));
    mergeChangeLog.setNewValue(String.valueOf(newValue));
    mergeChangeLog.setDescription(MergeChangeLogDTO.ModifyType.MODIFY_CUSTOMER_ID);
    return mergeChangeLog;
  }

  public static MergeChangeLogDTO log_orderItem_customerId(Long shopId,Long userId,Long recordId,Long oldValue,Long newValue){
    MergeChangeLogDTO mergeChangeLog=new MergeChangeLogDTO();
    mergeChangeLog.setShopId(shopId);
    mergeChangeLog.setUserId(userId);
    mergeChangeLog.setRecordId(recordId);
    mergeChangeLog.setModifiedTable("order_item");
    mergeChangeLog.setField("customer_id");
    mergeChangeLog.setOldValue(String.valueOf(oldValue));
    mergeChangeLog.setNewValue(String.valueOf(newValue));
    mergeChangeLog.setDescription(MergeChangeLogDTO.ModifyType.MODIFY_CUSTOMER_ID);
    return mergeChangeLog;
  }

  public static MergeChangeLogDTO log_payable_supplierId(Long shopId,Long userId,Long recordId,Long oldValue,Long newValue){
    MergeChangeLogDTO mergeChangeLog=new MergeChangeLogDTO();
    mergeChangeLog.setShopId(shopId);
    mergeChangeLog.setUserId(userId);
    mergeChangeLog.setRecordId(recordId);
    mergeChangeLog.setModifiedTable("payable");
    mergeChangeLog.setField("supplier_id");
    mergeChangeLog.setOldValue(String.valueOf(oldValue));
    mergeChangeLog.setNewValue(String.valueOf(newValue));
    mergeChangeLog.setDescription(MergeChangeLogDTO.ModifyType.MODIFY_SUPPLIER_ID);
    return mergeChangeLog;
  }

  public static MergeChangeLogDTO log_customer_deposit_cash(Long shopId, Long userId, Long recordId, Double oldValue, Double newValue){
    MergeChangeLogDTO mergeChangeLog=new MergeChangeLogDTO();
    mergeChangeLog.setShopId(shopId);
    mergeChangeLog.setUserId(userId);
    mergeChangeLog.setRecordId(recordId);
    mergeChangeLog.setModifiedTable("customer_deposit");
    mergeChangeLog.setField("cash");
    mergeChangeLog.setOldValue(String.valueOf(oldValue));
    mergeChangeLog.setNewValue(String.valueOf(newValue));
    mergeChangeLog.setDescription(MergeChangeLogDTO.ModifyType.MODIFY_CUSTOMER_DEPOSIT_AMOUNT);
    return mergeChangeLog;
  }

  public static MergeChangeLogDTO log_customer_deposit_bankCard(Long shopId, Long userId, Long recordId, Double oldValue, Double newValue){
    MergeChangeLogDTO mergeChangeLog=new MergeChangeLogDTO();
    mergeChangeLog.setShopId(shopId);
    mergeChangeLog.setUserId(userId);
    mergeChangeLog.setRecordId(recordId);
    mergeChangeLog.setModifiedTable("customer_deposit");
    mergeChangeLog.setField("bank_card_amount");
    mergeChangeLog.setOldValue(String.valueOf(oldValue));
    mergeChangeLog.setNewValue(String.valueOf(newValue));
    mergeChangeLog.setDescription(MergeChangeLogDTO.ModifyType.MODIFY_CUSTOMER_DEPOSIT_AMOUNT);
    return mergeChangeLog;
  }

  public static MergeChangeLogDTO log_customer_deposit_checkAmount(Long shopId, Long userId, Long recordId, Double oldValue, Double newValue){
    MergeChangeLogDTO mergeChangeLog=new MergeChangeLogDTO();
    mergeChangeLog.setShopId(shopId);
    mergeChangeLog.setUserId(userId);
    mergeChangeLog.setRecordId(recordId);
    mergeChangeLog.setModifiedTable("customer_deposit");
    mergeChangeLog.setField("check_amount");
    mergeChangeLog.setOldValue(String.valueOf(oldValue));
    mergeChangeLog.setNewValue(String.valueOf(newValue));
    mergeChangeLog.setDescription(MergeChangeLogDTO.ModifyType.MODIFY_CUSTOMER_DEPOSIT_AMOUNT);
    return mergeChangeLog;
  }

  public static MergeChangeLogDTO log_customer_deposit_actuallyPaid(Long shopId, Long userId, Long recordId, Double oldValue, Double newValue){
    MergeChangeLogDTO mergeChangeLog=new MergeChangeLogDTO();
    mergeChangeLog.setShopId(shopId);
    mergeChangeLog.setUserId(userId);
    mergeChangeLog.setRecordId(recordId);
    mergeChangeLog.setModifiedTable("customer_deposit");
    mergeChangeLog.setField("actually_paid");
    mergeChangeLog.setOldValue(String.valueOf(oldValue));
    mergeChangeLog.setNewValue(String.valueOf(newValue));
    mergeChangeLog.setDescription(MergeChangeLogDTO.ModifyType.MODIFY_CUSTOMER_DEPOSIT_AMOUNT);
    return mergeChangeLog;
  }

  public static MergeChangeLogDTO log_deposit_cash(Long shopId, Long userId, Long recordId, Double oldValue, Double newValue){
    MergeChangeLogDTO mergeChangeLog=new MergeChangeLogDTO();
    mergeChangeLog.setShopId(shopId);
    mergeChangeLog.setUserId(userId);
    mergeChangeLog.setRecordId(recordId);
    mergeChangeLog.setModifiedTable("deposit");
    mergeChangeLog.setField("cash");
    mergeChangeLog.setOldValue(String.valueOf(oldValue));
    mergeChangeLog.setNewValue(String.valueOf(newValue));
    mergeChangeLog.setDescription(MergeChangeLogDTO.ModifyType.MODIFY_DEPOSIT_AMOUNT);
    return mergeChangeLog;
  }

  public static MergeChangeLogDTO log_deposit_bankCard(Long shopId, Long userId, Long recordId, Double oldValue, Double newValue){
    MergeChangeLogDTO mergeChangeLog=new MergeChangeLogDTO();
    mergeChangeLog.setShopId(shopId);
    mergeChangeLog.setUserId(userId);
    mergeChangeLog.setRecordId(recordId);
    mergeChangeLog.setModifiedTable("deposit");
    mergeChangeLog.setField("bank_card_amount");
    mergeChangeLog.setOldValue(String.valueOf(oldValue));
    mergeChangeLog.setNewValue(String.valueOf(newValue));
    mergeChangeLog.setDescription(MergeChangeLogDTO.ModifyType.MODIFY_DEPOSIT_AMOUNT);
    return mergeChangeLog;
  }

  public static MergeChangeLogDTO log_deposit_checkAmount(Long shopId, Long userId, Long recordId, Double oldValue, Double newValue){
    MergeChangeLogDTO mergeChangeLog=new MergeChangeLogDTO();
    mergeChangeLog.setShopId(shopId);
    mergeChangeLog.setUserId(userId);
    mergeChangeLog.setRecordId(recordId);
    mergeChangeLog.setModifiedTable("deposit");
    mergeChangeLog.setField("check_amount");
    mergeChangeLog.setOldValue(String.valueOf(oldValue));
    mergeChangeLog.setNewValue(String.valueOf(newValue));
    mergeChangeLog.setDescription(MergeChangeLogDTO.ModifyType.MODIFY_DEPOSIT_AMOUNT);
    return mergeChangeLog;
  }

  public static MergeChangeLogDTO log_deposit_actuallyPaid(Long shopId, Long userId, Long recordId, Double oldValue, Double newValue){
    MergeChangeLogDTO mergeChangeLog=new MergeChangeLogDTO();
    mergeChangeLog.setShopId(shopId);
    mergeChangeLog.setUserId(userId);
    mergeChangeLog.setRecordId(recordId);
    mergeChangeLog.setModifiedTable("deposit");
    mergeChangeLog.setField("actually_paid");
    mergeChangeLog.setOldValue(String.valueOf(oldValue));
    mergeChangeLog.setNewValue(String.valueOf(newValue));
    mergeChangeLog.setDescription(MergeChangeLogDTO.ModifyType.MODIFY_DEPOSIT_AMOUNT);
    return mergeChangeLog;
  }


  public static MergeChangeLogDTO log_payableHistoryRecord_supplierId(Long shopId,Long userId,Long recordId,Long oldValue,Long newValue){
    MergeChangeLogDTO mergeChangeLog=new MergeChangeLogDTO();
    mergeChangeLog.setShopId(shopId);
    mergeChangeLog.setUserId(userId);
    mergeChangeLog.setRecordId(recordId);
    mergeChangeLog.setModifiedTable("payable_history_record");
    mergeChangeLog.setField("supplier_id");
    mergeChangeLog.setOldValue(String.valueOf(oldValue));
    mergeChangeLog.setNewValue(String.valueOf(newValue));
    mergeChangeLog.setDescription(MergeChangeLogDTO.ModifyType.MODIFY_SUPPLIER_ID);
    return mergeChangeLog;
  }

  public static MergeChangeLogDTO log_payableHistory_supplierId(Long shopId,Long userId,Long recordId,Long oldValue,Long newValue){
    MergeChangeLogDTO mergeChangeLog=new MergeChangeLogDTO();
    mergeChangeLog.setShopId(shopId);
    mergeChangeLog.setUserId(userId);
    mergeChangeLog.setRecordId(recordId);
    mergeChangeLog.setModifiedTable("payable_history");
    mergeChangeLog.setField("supplier_id");
    mergeChangeLog.setOldValue(String.valueOf(oldValue));
    mergeChangeLog.setNewValue(String.valueOf(newValue));
    mergeChangeLog.setDescription(MergeChangeLogDTO.ModifyType.MODIFY_SUPPLIER_ID);
    return mergeChangeLog;
  }

  public static MergeChangeLogDTO log_purchaseOrder_supplierId(Long shopId,Long userId,Long recordId,Long oldValue,Long newValue){
    MergeChangeLogDTO mergeChangeLog=new MergeChangeLogDTO();
    mergeChangeLog.setShopId(shopId);
    mergeChangeLog.setUserId(userId);
    mergeChangeLog.setRecordId(recordId);
    mergeChangeLog.setModifiedTable("purchase_order");
    mergeChangeLog.setField("supplier_id");
    mergeChangeLog.setOldValue(String.valueOf(oldValue));
    mergeChangeLog.setNewValue(String.valueOf(newValue));
    mergeChangeLog.setDescription(MergeChangeLogDTO.ModifyType.MODIFY_SUPPLIER_ID);
    return mergeChangeLog;
  }

  public static MergeChangeLogDTO log_purchaseInventory_supplierId(Long shopId,Long userId,Long recordId,Long oldValue,Long newValue){
    MergeChangeLogDTO mergeChangeLog=new MergeChangeLogDTO();
    mergeChangeLog.setShopId(shopId);
    mergeChangeLog.setUserId(userId);
    mergeChangeLog.setRecordId(recordId);
    mergeChangeLog.setModifiedTable("purchase_inventory");
    mergeChangeLog.setField("supplier_id");
    mergeChangeLog.setOldValue(String.valueOf(oldValue));
    mergeChangeLog.setNewValue(String.valueOf(newValue));
    mergeChangeLog.setDescription(MergeChangeLogDTO.ModifyType.MODIFY_SUPPLIER_ID);
    return mergeChangeLog;
  }

  public static MergeChangeLogDTO log_purchaseReturn_supplierId(Long shopId,Long userId,Long recordId,Long oldValue,Long newValue){
    MergeChangeLogDTO mergeChangeLog=new MergeChangeLogDTO();
    mergeChangeLog.setShopId(shopId);
    mergeChangeLog.setUserId(userId);
    mergeChangeLog.setRecordId(recordId);
    mergeChangeLog.setModifiedTable("purchase_return");
    mergeChangeLog.setField("supplier_id");
    mergeChangeLog.setOldValue(String.valueOf(oldValue));
    mergeChangeLog.setNewValue(String.valueOf(newValue));
    mergeChangeLog.setDescription(MergeChangeLogDTO.ModifyType.MODIFY_SUPPLIER_ID);
    return mergeChangeLog;
  }

    public static MergeChangeLogDTO log_supplierReturnPayable_supplierId(Long shopId,Long userId,Long recordId,Long oldValue,Long newValue){
    MergeChangeLogDTO mergeChangeLog=new MergeChangeLogDTO();
    mergeChangeLog.setShopId(shopId);
    mergeChangeLog.setUserId(userId);
    mergeChangeLog.setRecordId(recordId);
    mergeChangeLog.setModifiedTable("supplier_return_payable");
    mergeChangeLog.setField("supplier_id");
    mergeChangeLog.setOldValue(String.valueOf(oldValue));
    mergeChangeLog.setNewValue(String.valueOf(newValue));
    mergeChangeLog.setDescription(MergeChangeLogDTO.ModifyType.MODIFY_SUPPLIER_ID);
    return mergeChangeLog;
  }

   public static MergeChangeLogDTO log_purchaseReturnMonthStat_supplierId(Long shopId,Long userId,Long recordId,Long oldValue,Long newValue){
    MergeChangeLogDTO mergeChangeLog=new MergeChangeLogDTO();
    mergeChangeLog.setShopId(shopId);
    mergeChangeLog.setUserId(userId);
    mergeChangeLog.setRecordId(recordId);
    mergeChangeLog.setModifiedTable("purchase_return_month_stat");
    mergeChangeLog.setField("supplier_id");
    mergeChangeLog.setOldValue(String.valueOf(oldValue));
    mergeChangeLog.setNewValue(String.valueOf(newValue));
    mergeChangeLog.setDescription(MergeChangeLogDTO.ModifyType.MODIFY_SUPPLIER_ID);
    return mergeChangeLog;
  }

  public static MergeChangeLogDTO log_supplierTranMonthStat_supplierId(Long shopId,Long userId,Long recordId,Long oldValue,Long newValue){
    MergeChangeLogDTO mergeChangeLog=new MergeChangeLogDTO();
    mergeChangeLog.setShopId(shopId);
    mergeChangeLog.setUserId(userId);
    mergeChangeLog.setRecordId(recordId);
    mergeChangeLog.setModifiedTable("supplier_tran_month_stat");
    mergeChangeLog.setField("supplier_id");
    mergeChangeLog.setOldValue(String.valueOf(oldValue));
    mergeChangeLog.setNewValue(String.valueOf(newValue));
    mergeChangeLog.setDescription(MergeChangeLogDTO.ModifyType.MODIFY_SUPPLIER_ID);
    return mergeChangeLog;
  }

  public static MergeChangeLogDTO log_inStorageRecord_supplierId(Long shopId,Long userId,Long recordId,Long oldValue,Long newValue){
    MergeChangeLogDTO mergeChangeLog=new MergeChangeLogDTO();
    mergeChangeLog.setShopId(shopId);
    mergeChangeLog.setUserId(userId);
    mergeChangeLog.setRecordId(recordId);
    mergeChangeLog.setModifiedTable("in_storage_record");
    mergeChangeLog.setField("supplier_id");
    mergeChangeLog.setOldValue(String.valueOf(oldValue));
    mergeChangeLog.setNewValue(String.valueOf(newValue));
    mergeChangeLog.setDescription(MergeChangeLogDTO.ModifyType.MODIFY_SUPPLIER_ID);
    return mergeChangeLog;
  }

  public static MergeChangeLogDTO log_outStorageRelation_supplierId(Long shopId,Long userId,Long recordId,Long oldValue,Long newValue){
    MergeChangeLogDTO mergeChangeLog=new MergeChangeLogDTO();
    mergeChangeLog.setShopId(shopId);
    mergeChangeLog.setUserId(userId);
    mergeChangeLog.setRecordId(recordId);
    mergeChangeLog.setModifiedTable("out_storage_relation");
    mergeChangeLog.setField("related_supplier_id");
    mergeChangeLog.setOldValue(String.valueOf(oldValue));
    mergeChangeLog.setNewValue(String.valueOf(newValue));
    mergeChangeLog.setDescription(MergeChangeLogDTO.ModifyType.MODIFY_SUPPLIER_ID);
    return mergeChangeLog;
  }

  public static MergeChangeLogDTO log_deposit_order_customerId(Long shopId,Long userId,Long recordId,Long oldValue,Long newValue){
    MergeChangeLogDTO mergeChangeLog=new MergeChangeLogDTO();
    mergeChangeLog.setShopId(shopId);
    mergeChangeLog.setUserId(userId);
    mergeChangeLog.setRecordId(recordId);
    mergeChangeLog.setModifiedTable("deposit_order");
    mergeChangeLog.setField("customer_id");
    mergeChangeLog.setOldValue(String.valueOf(oldValue));
    mergeChangeLog.setNewValue(String.valueOf(newValue));
    mergeChangeLog.setDescription(MergeChangeLogDTO.ModifyType.MODIFY_CUSTOMER_ID);
    return mergeChangeLog;
  }

  public static MergeChangeLogDTO log_deposit_order_supplierId(Long shopId,Long userId,Long recordId,Long oldValue,Long newValue){
    MergeChangeLogDTO mergeChangeLog=new MergeChangeLogDTO();
    mergeChangeLog.setShopId(shopId);
    mergeChangeLog.setUserId(userId);
    mergeChangeLog.setRecordId(recordId);
    mergeChangeLog.setModifiedTable("deposit_order");
    mergeChangeLog.setField("customer_id");
    mergeChangeLog.setOldValue(String.valueOf(oldValue));
    mergeChangeLog.setNewValue(String.valueOf(newValue));
    mergeChangeLog.setDescription(MergeChangeLogDTO.ModifyType.MODIFY_SUPPLIER_ID);
    return mergeChangeLog;
  }

  public static MergeChangeLogDTO log_appointOrder_customerId(Long shopId,Long userId,Long recordId,Long oldValue,Long newValue){
    MergeChangeLogDTO mergeChangeLog=new MergeChangeLogDTO();
    mergeChangeLog.setShopId(shopId);
    mergeChangeLog.setUserId(userId);
    mergeChangeLog.setRecordId(recordId);
    mergeChangeLog.setModifiedTable("appoint_order");
    mergeChangeLog.setField("customer_id");
    mergeChangeLog.setOldValue(String.valueOf(oldValue));
    mergeChangeLog.setNewValue(String.valueOf(newValue));
    mergeChangeLog.setDescription(MergeChangeLogDTO.ModifyType.MODIFY_CUSTOMER_ID);
    return mergeChangeLog;
  }
}
