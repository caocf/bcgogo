package com.bcgogo.txn.service.importexcel.order;

import com.bcgogo.txn.model.ImportedOrderTemp;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.ObjectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 根据从excel中解析出的一行数据构造单据信息
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 12-10-29
 * Time: 下午5:12
 * To change this template use File | Settings | File Templates.
 */
@Component
public class OrderDTOGenerator {

  private static final Logger LOG = LoggerFactory.getLogger(OrderDTOGenerator.class);

  public String getOrderType(String fileName){
    if(fileName.equals("施工单")){
      return "REPAIR";
    }else if(fileName.equals("销售单")){
      return "SALE";
    }else if(fileName.equals("入库单")){
      return "INVENTORY";

    }else if(fileName.equals("洗车美容")){
      return "WASH_BEAUTY";
    }
    return null;
  }

  public String getReceiptPrefix(String orderType){
    if(orderType.equals("REPAIR")){
      return "SG";
    }else if(orderType.equals("SALE")){
      return "XS";
    }else if(orderType.equals("INVENTORY")){
      return "RK";

    }else if(orderType.equals("WASH_BEAUTY")){
      return "XM";
    }
    return null;
  }

  public ImportedOrderTemp generate(Map<String, Object> data, Map<String, String> fieldMapping, Long shopId) throws Exception {
    if (shopId == null) {
      return null;
    }
    ImportedOrderTemp importedOrderDTO = new ImportedOrderTemp();
    importedOrderDTO.setShopId(shopId);
    importedOrderDTO.setOrderTypeStr(getOrderType(data.get("fileName").toString()));
    String receiptPrefix=getReceiptPrefix(importedOrderDTO.getOrderTypeStr());

    if (data.get(fieldMapping.get(OrderImportConstants.FieldName.RECEIPT)) != null) {
      importedOrderDTO.setReceipt(receiptPrefix+String.valueOf(data.get(fieldMapping.get(OrderImportConstants.FieldName.RECEIPT))));
    }
    if (data.get(fieldMapping.get(OrderImportConstants.FieldName.PRODUCT_CODE)) != null) {
      importedOrderDTO.setProductCode(String.valueOf(data.get(fieldMapping.get(OrderImportConstants.FieldName.PRODUCT_CODE))));
    }
    if (data.get(fieldMapping.get(OrderImportConstants.FieldName.PRODUCT_NAME)) != null) {
      importedOrderDTO.setProductName(String.valueOf(data.get(fieldMapping.get(OrderImportConstants.FieldName.PRODUCT_NAME))));
    }
    if (data.get(fieldMapping.get(OrderImportConstants.FieldName.BRAND)) != null) {
      importedOrderDTO.setBrand(String.valueOf(data.get(fieldMapping.get(OrderImportConstants.FieldName.BRAND))));
    }
    if (data.get(fieldMapping.get(OrderImportConstants.FieldName.SPEC)) != null) {
      importedOrderDTO.setSpec(String.valueOf(data.get(fieldMapping.get(OrderImportConstants.FieldName.SPEC))));
    }
    if (data.get(fieldMapping.get(OrderImportConstants.FieldName.MODEL)) != null) {
      importedOrderDTO.setModel(String.valueOf(data.get(fieldMapping.get(OrderImportConstants.FieldName.MODEL))));
    }
    if (data.get(fieldMapping.get(OrderImportConstants.FieldName.VEHICLE_BRAND)) != null) {
      importedOrderDTO.setVehicleBrand(String.valueOf(data.get(fieldMapping.get(OrderImportConstants.FieldName.VEHICLE_BRAND))));
    }
    if (data.get(fieldMapping.get(OrderImportConstants.FieldName.VEHICLE_MODEL)) != null) {
      importedOrderDTO.setVehicleModel(String.valueOf(data.get(fieldMapping.get(OrderImportConstants.FieldName.VEHICLE_MODEL))));
    }
    if (data.get(fieldMapping.get(OrderImportConstants.FieldName.PRICE)) != null) {
      importedOrderDTO.setPrice(NumberUtil.doubleValue(data.get(fieldMapping.get(OrderImportConstants.FieldName.PRICE)).toString(),0));
    }
    if (data.get(fieldMapping.get(OrderImportConstants.FieldName.AMOUNT)) != null) {
      importedOrderDTO.setAmount(NumberUtil.doubleValue(data.get(fieldMapping.get(OrderImportConstants.FieldName.AMOUNT)).toString(),0));
    }
    if (data.get(fieldMapping.get(OrderImportConstants.FieldName.UNIT)) != null) {
      importedOrderDTO.setUnit(String.valueOf(data.get(fieldMapping.get(OrderImportConstants.FieldName.UNIT))));
    }
    if (data.get(fieldMapping.get(OrderImportConstants.FieldName.ORDER_STATUS)) != null ) {
      importedOrderDTO.setOrderStatusStr(String.valueOf(data.get(fieldMapping.get(OrderImportConstants.FieldName.ORDER_STATUS))));
    }
    if (data.get(fieldMapping.get(OrderImportConstants.FieldName.VEST_DATE)) != null) {
      importedOrderDTO.setVestDate(String.valueOf(data.get(fieldMapping.get(OrderImportConstants.FieldName.VEST_DATE))));
    }
    if (data.get(fieldMapping.get(OrderImportConstants.FieldName.VEHICLE)) != null) {
      importedOrderDTO.setVehicle(String.valueOf(data.get(fieldMapping.get(OrderImportConstants.FieldName.VEHICLE))));
    }
    if (data.get(fieldMapping.get(OrderImportConstants.FieldName.CUSTOMER_SUPPLIER_NAME)) != null) {
      importedOrderDTO.setCustomerSupplierName(String.valueOf(data.get(fieldMapping.get(OrderImportConstants.FieldName.CUSTOMER_SUPPLIER_NAME))));
    }
    if (ObjectUtil.isNotEmptyStr(data.get(fieldMapping.get(OrderImportConstants.FieldName.CONTACT))) ) {
      importedOrderDTO.setContact(String.valueOf(data.get(fieldMapping.get(OrderImportConstants.FieldName.CONTACT))));
    }
    if (ObjectUtil.isNotEmptyStr(data.get(fieldMapping.get(OrderImportConstants.FieldName.MOBILE))) ) {
      importedOrderDTO.setMobile(String.valueOf(data.get(fieldMapping.get(OrderImportConstants.FieldName.MOBILE))));
    }
    if (ObjectUtil.isNotEmptyStr(data.get(fieldMapping.get(OrderImportConstants.FieldName.MEMBER_TYPE))) ) {
      importedOrderDTO.setMemberType(String.valueOf(data.get(fieldMapping.get(OrderImportConstants.FieldName.MEMBER_TYPE))));
    }
    if (ObjectUtil.isNotEmptyStr(data.get(fieldMapping.get(OrderImportConstants.FieldName.MEMBER_CARD_NO))) ) {
      importedOrderDTO.setMemberCardNo(String.valueOf(data.get(fieldMapping.get(OrderImportConstants.FieldName.MEMBER_CARD_NO))));
    }
    if (ObjectUtil.isNotEmptyStr(data.get(fieldMapping.get(OrderImportConstants.FieldName.PAY_PER_PROJECT))) ) {
      importedOrderDTO.setPayPerProject(String.valueOf(data.get(fieldMapping.get(OrderImportConstants.FieldName.PAY_PER_PROJECT))));
    }
    if (ObjectUtil.isNotEmptyStr(data.get(fieldMapping.get(OrderImportConstants.FieldName.SALES_MAN))) ) {
      importedOrderDTO.setSalesMan(String.valueOf(data.get(fieldMapping.get(OrderImportConstants.FieldName.SALES_MAN))));
    }
    if (ObjectUtil.isNotEmptyStr(data.get(fieldMapping.get(OrderImportConstants.FieldName.SERVICE_TOTAL))) ) {
      importedOrderDTO.setServiceTotal(NumberUtil.doubleValue(data.get(fieldMapping.get(OrderImportConstants.FieldName.SERVICE_TOTAL)).toString(),0));
    }
    if (ObjectUtil.isNotEmptyStr(data.get(fieldMapping.get(OrderImportConstants.FieldName.SERVICE_WORKER))) ) {
      importedOrderDTO.setServiceWorker(String.valueOf(data.get(fieldMapping.get(OrderImportConstants.FieldName.SERVICE_WORKER))));
    }
    if (ObjectUtil.isNotEmptyStr(data.get(fieldMapping.get(OrderImportConstants.FieldName.SERVICE_CONTENT)))) {
      importedOrderDTO.setServiceContent(String.valueOf(data.get(fieldMapping.get(OrderImportConstants.FieldName.SERVICE_CONTENT))));
      importedOrderDTO.setItemType(ImportedOrderTemp.SERVICE);
    }
    if (data.get(fieldMapping.get(OrderImportConstants.FieldName.IN_TIME)) != null) {
      importedOrderDTO.setInTime(String.valueOf(data.get(fieldMapping.get(OrderImportConstants.FieldName.IN_TIME))));
    }
    if (ObjectUtil.isNotEmptyStr(data.get(fieldMapping.get(OrderImportConstants.FieldName.OUT_TIME))) ) {
      importedOrderDTO.setOutTime(String.valueOf(data.get(fieldMapping.get(OrderImportConstants.FieldName.OUT_TIME))));
    }
    if (ObjectUtil.isNotEmptyStr(data.get(fieldMapping.get(OrderImportConstants.FieldName.PAY_WAY))) ) {
      importedOrderDTO.setPayWay(String.valueOf(data.get(fieldMapping.get(OrderImportConstants.FieldName.PAY_WAY))));
    }
    if (data.get(fieldMapping.get(OrderImportConstants.FieldName.TOTAL)) != null) {
      importedOrderDTO.setTotal(NumberUtil.doubleValue(data.get(fieldMapping.get(OrderImportConstants.FieldName.TOTAL)).toString(),0));
    }
    if (data.get(fieldMapping.get(OrderImportConstants.FieldName.ACTUALLY_PAID)) != null) {
      importedOrderDTO.setActuallyPaid(NumberUtil.doubleValue(data.get(fieldMapping.get(OrderImportConstants.FieldName.ACTUALLY_PAID)).toString(),0));
    }
    if (data.get(fieldMapping.get(OrderImportConstants.FieldName.DEBT)) != null) {
      importedOrderDTO.setDebt(NumberUtil.doubleValue(data.get(fieldMapping.get(OrderImportConstants.FieldName.DEBT)).toString(),0));
    }
    if (data.get(fieldMapping.get(OrderImportConstants.FieldName.MEMO)) != null) {
      importedOrderDTO.setMemo(String.valueOf(data.get(fieldMapping.get(OrderImportConstants.FieldName.MEMO))));
    }
    return importedOrderDTO;
  }

}
