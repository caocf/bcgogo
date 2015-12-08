package com.bcgogo.txn.service.importexcel.order;

import com.bcgogo.config.service.excelimport.ExcelImportException;
import com.bcgogo.config.service.excelimport.ImportVerifier;
import com.bcgogo.user.dto.ValidateImportDataDTO;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.StringUtil;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 单据导入字段校验类
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 12-10-28
 * Time: 下午3:01
 * To change this template use File | Settings | File Templates.
 */
@Component
public class OrderImportVerifier implements ImportVerifier {

  @Override
  public String verify(Map<String, Object> data, Map<String, String> fieldMapping,ValidateImportDataDTO validateImportDataDTO) throws ExcelImportException {
    String serviceContent=String.valueOf(data.get(fieldMapping.get(OrderImportConstants.FieldName.SERVICE_CONTENT)));
    String verifyReceipt = verifyReceipt(data.get(fieldMapping.get(OrderImportConstants.FieldName.RECEIPT)));
    String fileName=String.valueOf(data.get("fileName"));
    if (!StringUtil.isEmpty(verifyReceipt)) {
      return verifyReceipt;
    }
    String verifyProductCode = verifyProductCode(data.get(fieldMapping.get(OrderImportConstants.FieldName.PRODUCT_CODE)));
    if (!StringUtil.isEmpty(verifyProductCode)) {
      return verifyProductCode;
    }

    String productName = verifyProductName(data.get(fieldMapping.get(OrderImportConstants.FieldName.PRODUCT_NAME)),serviceContent,fileName);
    if (StringUtil.isNotEmpty(productName)) {
      return productName;   //说明有错误信息
    }
    String verifyBrand = verifyBrand(data.get(fieldMapping.get(OrderImportConstants.FieldName.BRAND)));
    if (!StringUtil.isEmpty(verifyBrand)) {
      return verifyBrand;
    }

    String verifyModel = verifyModel(data.get(fieldMapping.get(OrderImportConstants.FieldName.MODEL)));
    if (!StringUtil.isEmpty(verifyModel)) {
      return verifyModel;
    }
    String verifySpec = verifySpec(data.get(fieldMapping.get(OrderImportConstants.FieldName.SPEC)));
    if (!StringUtil.isEmpty(verifySpec)) {
      return verifySpec;
    }

    String verifyVehicleBrand = verifyVehicleBrand(data.get(fieldMapping.get(OrderImportConstants.FieldName.VEHICLE_BRAND)));
    if (!StringUtil.isEmpty(verifyVehicleBrand)) {
      return verifyVehicleBrand;
    }
    String verifyVehicleModel = verifyVehicleModel(data.get(fieldMapping.get(OrderImportConstants.FieldName.VEHICLE_MODEL)));
    if (!StringUtil.isEmpty(verifyVehicleModel)) {
      return verifyVehicleModel;
    }
    String verifyPrice = verifyPrice(data.get(fieldMapping.get(OrderImportConstants.FieldName.PRICE)));
    if (!StringUtil.isEmpty(verifyPrice)) {
      return verifyPrice;
    }
    String verifyAmount = verifyAmount(data.get(fieldMapping.get(OrderImportConstants.FieldName.AMOUNT)));
    if (!StringUtil.isEmpty(verifyAmount)) {
      return verifyAmount;
    }
    String verifyUnit = verifyUnit(data.get(fieldMapping.get(OrderImportConstants.FieldName.UNIT)));
    if (!StringUtil.isEmpty(verifyUnit)) {
      return verifyUnit;
    }
    String verifyVestDate = verifyVestDate(data.get(fieldMapping.get(OrderImportConstants.FieldName.VEST_DATE)));
    if (!StringUtil.isEmpty(verifyVestDate)) {
      return verifyVestDate;
    }
    String verifyOrderStatus = verifyOrderStatus(data.get(fieldMapping.get(OrderImportConstants.FieldName.ORDER_STATUS)));
    if (!StringUtil.isEmpty(verifyOrderStatus)) {
      return verifyOrderStatus;
    }

    String verifyVehicle = verifyVehicle(data.get(fieldMapping.get(OrderImportConstants.FieldName.VEHICLE)));
    if (!StringUtil.isEmpty(verifyVehicle)) {
      return verifyVehicle;
    }

    String verifyCustomerOrSupplierName = verifyCustomerOrSupplierName(data.get(fieldMapping.get(OrderImportConstants.FieldName.CUSTOMER_SUPPLIER_NAME)));
    if (!StringUtil.isEmpty(verifyCustomerOrSupplierName)) {
      return verifyCustomerOrSupplierName;
    }
    String verifyContact = verifyContact(data.get(fieldMapping.get(OrderImportConstants.FieldName.CONTACT)));
    if (!StringUtil.isEmpty(verifyContact)) {
      return verifyContact;
    }
    String verifyMobile = verifyMobile(data.get(fieldMapping.get(OrderImportConstants.FieldName.MOBILE)));
    if (!StringUtil.isEmpty(verifyMobile)) {
      return verifyMobile;
    }
    String verifyMemberType = verifyMemberType(data.get(fieldMapping.get(OrderImportConstants.FieldName.MEMBER_TYPE)));
    if (!StringUtil.isEmpty(verifyMemberType)) {
      return verifyMemberType;
    }
    String verifyMemberCardNo = verifyMemberCardNo(data.get(fieldMapping.get(OrderImportConstants.FieldName.MEMBER_CARD_NO)));
    if (!StringUtil.isEmpty(verifyMemberCardNo)) {
      return verifyMemberCardNo;
    }
    String verifyPayPerProject = verifyPayPerProject(data.get(fieldMapping.get(OrderImportConstants.FieldName.PAY_PER_PROJECT)));
    if (!StringUtil.isEmpty(verifyPayPerProject)) {
      return verifyPayPerProject;
    }

    String verifySalesMan = verifySalesMan(data.get(fieldMapping.get(OrderImportConstants.FieldName.SALES_MAN)));
    if (!StringUtil.isEmpty(verifySalesMan)) {
      return verifySalesMan;
    }

    String verifyServiceTotal = verifyServiceTotal(data.get(fieldMapping.get(OrderImportConstants.FieldName.SERVICE_TOTAL)));
    if (!StringUtil.isEmpty(verifyServiceTotal)) {
      return verifyServiceTotal;
    }
    String verifyServiceWorker = verifyServiceWorker(data.get(fieldMapping.get(OrderImportConstants.FieldName.SERVICE_WORKER)));
    if (!StringUtil.isEmpty(verifyServiceWorker)) {
      return verifyServiceWorker;
    }
    String verifyServiceContent = verifyServiceContent(data.get(fieldMapping.get(OrderImportConstants.FieldName.SERVICE_CONTENT)));
    if (!StringUtil.isEmpty(verifyServiceContent)) {
      return verifyServiceContent;
    }
    String verifyInTime = verifyInTime(data.get(fieldMapping.get(OrderImportConstants.FieldName.IN_TIME)));
    if (!StringUtil.isEmpty(verifyInTime)) {
      return verifyInTime;
    }
    String verifyOutTime = verifyOutTime(data.get(fieldMapping.get(OrderImportConstants.FieldName.OUT_TIME)));
    if (!StringUtil.isEmpty(verifyOutTime)) {
      return verifyOutTime;
    }
    String verifyPayWay = verifyPayWay(data.get(fieldMapping.get(OrderImportConstants.FieldName.PAY_WAY)));
    if (!StringUtil.isEmpty(verifyPayWay)) {
      return verifyPayWay;
    }
    String verifyTotal = verifyTotal(data.get(fieldMapping.get(OrderImportConstants.FieldName.TOTAL)));
    if (!StringUtil.isEmpty(verifyTotal)) {
      return verifyTotal;
    }
    String verifyActuallyPaid = verifyActuallyPaid(data.get(fieldMapping.get(OrderImportConstants.FieldName.ACTUALLY_PAID)));
    if (!StringUtil.isEmpty(verifyActuallyPaid)) {
      return verifyActuallyPaid;
    }

    String verifyDebt = verifyDebt(data.get(fieldMapping.get(OrderImportConstants.FieldName.DEBT)));
    if (!StringUtil.isEmpty(verifyDebt)) {
      return verifyDebt;
    }

    String verifyMemo = verifyMemo(data.get(fieldMapping.get(OrderImportConstants.FieldName.MEMO)));
    if (!StringUtil.isEmpty(verifyMemo)) {
      return verifyMemo;
    }
    return null;
  }

  public String verifyReceipt(Object value) {
    if (value == null) {
      return OrderImportConstants.CheckResultMessage.EMPTY_RECEIPT_NAME;
    }
    String receipt = String.valueOf(value);
    if (StringUtil.isEmpty(receipt)) {
      return OrderImportConstants.CheckResultMessage.EMPTY_RECEIPT_NAME;
    }
    if(!NumberUtil.isNumber(receipt)){
         return OrderImportConstants.CheckResultMessage.ERROR_RECEIPT_FORMAT;
    }
    if (receipt.length() > OrderImportConstants.FieldLength.FIELD_LENGTH_RECEIPT_NAME) {
      return OrderImportConstants.CheckResultMessage.RECEIPT_TOO_LONG;
    }
    return null;
  }

  public String verifyProductCode(Object value) {
    if (value == null) {
      return null;
    }
    String receipt = String.valueOf(value);
    if (StringUtil.isEmpty(receipt)) {
      return null;
    }
    if (receipt.length() > OrderImportConstants.FieldLength.FIELD_LENGTH_PRODUCT_CODE) {
      return OrderImportConstants.CheckResultMessage.PRODUCT_CODE_TOO_LONG;
    }
    return null;
  }
  public String verifyProductName(Object value,String serviceContent,String fileName) {
    if (value == null&&StringUtil.isEmpty(serviceContent)&&!fileName.equals("洗车美容")) {
      return OrderImportConstants.CheckResultMessage.EMPTY_PRODUCT_NAME;
    }
    String name = String.valueOf(value);
    if (StringUtil.isEmpty(name)) {
      return OrderImportConstants.CheckResultMessage.EMPTY_PRODUCT_NAME;
    }
    if (name.length() > OrderImportConstants.FieldLength.FIELD_LENGTH_PRODUCT_NAME) {
      return OrderImportConstants.CheckResultMessage.PRODUCT_NAME_TOO_LONG;
    }
    return null;
  }

  public String verifyBrand(Object value) {
  if (value == null) {
      return null;
    }
    String brand = String.valueOf(value);
    if (StringUtil.isEmpty(brand)) {
      return null;
    }
    if (brand.length() > OrderImportConstants.FieldLength.FIELD_LENGTH_BRAND) {
      return OrderImportConstants.CheckResultMessage.BRAND_TOO_LONG;
    }
    return null;
  }


  public String verifySpec(Object value) {
    if (value == null) {
      return null;
    }
    String spec = String.valueOf(value);
    if (StringUtil.isEmpty(spec)) {
      return null;
    }
    if (spec.length() > OrderImportConstants.FieldLength.FIELD_LENGTH_SPEC) {
      return OrderImportConstants.CheckResultMessage.SPEC_TOO_LONG;
    }
    return null;
  }

  public String verifyModel(Object value) {
    if (value == null) {
      return null;
    }
    String model = String.valueOf(value);
    if (StringUtil.isEmpty(model)) {
      return null;
    }
    if (model.length() > OrderImportConstants.FieldLength.FIELD_LENGTH_MODEL) {
      return OrderImportConstants.CheckResultMessage.MODEL_TOO_LONG;
    }
    return null;
  }

  public String verifyVehicleBrand(Object value) {
    if (value == null) {
      return null;
    }
    String model = String.valueOf(value);
    if (StringUtil.isEmpty(model)) {
      return null;
    }
    if (model.length() > OrderImportConstants.FieldLength.FIELD_LENGTH_VEHICLE_BRAND) {
      return OrderImportConstants.CheckResultMessage.VEHICLE_BRAND_TOO_LONG;
    }
    return null;
  }

  public String verifyVehicleModel(Object value) {
    if (value == null) {
      return null;
    }
    String model = String.valueOf(value);
    if (StringUtil.isEmpty(model)) {
      return null;
    }
    if (model.length() > OrderImportConstants.FieldLength.FIELD_LENGTH_VEHICLE_MODEL) {
      return OrderImportConstants.CheckResultMessage.VEHICLE_MODEL_TOO_LONG;
    }
    return null;
  }

  public String verifyPrice(Object value) {
   if (value == null) {
      return null;
    }
    String inventoryAmount = String.valueOf(value);
    if (StringUtil.isEmpty(inventoryAmount)) {
      return null;
    }
    if(!NumberUtil.isNumber(inventoryAmount)){
      return OrderImportConstants.CheckResultMessage.PRICE_NOT_NUMBER;
    }
    return null;
  }
  public String verifyAmount(Object value) {
    if (value == null) {
      return null;
    }
    String inventoryAmount = String.valueOf(value);
    if (StringUtil.isEmpty(inventoryAmount)) {
      return null;
    }
    if(!NumberUtil.isNumber(inventoryAmount)){
      return OrderImportConstants.CheckResultMessage.AMOUNT_NOT_NUMBER;
    }
    return null;
  }
  public String verifyUnit(Object value) {
    if (value == null) {
      return null;
    }
    String contact = String.valueOf(value);
    if (StringUtil.isEmpty(contact)) {
      return null;
    }
    if (contact.length() > OrderImportConstants.FieldLength.FIELD_LENGTH_UNIT) {
      return OrderImportConstants.CheckResultMessage.UNIT_TOO_LONG;
    }
    return null;
  }

  public String verifyOrderStatus(Object value) {
    if (value == null) {
      return null;
    }
    String orderStatus = String.valueOf(value);
    if (StringUtil.isEmpty(orderStatus)) {
      return null;
    }
    if (orderStatus.length() > OrderImportConstants.FieldLength.FIELD_LENGTH_ORDER_STATUS) {
      return OrderImportConstants.CheckResultMessage.ORDER_STATUS_TOO_LONG;
    }
    return null;
  }



  public String verifyVestDate(Object value) {
    if (value == null) {
      return null;
    }
    String vestDate = String.valueOf(value);
    if (StringUtil.isEmpty(vestDate)) {
      return null;
    }
    if (vestDate.length() > OrderImportConstants.FieldLength.FIELD_LENGTH_STANDARD_DATE_FORMAT) {
      return OrderImportConstants.CheckResultMessage.VEST_DATE_TOO_LONG;
    }
    if(!DateUtil.isStandardDateFormat(vestDate)){
       return OrderImportConstants.CheckResultMessage.ERROR_DATE_FOMART;
    }
    return null;
  }

  public String verifyVehicle(Object value) {
    if (value == null) {
      return null;
    }
    String vehicle = String.valueOf(value);
    if (StringUtil.isEmpty(vehicle)) {
      return null;
    }
    if (vehicle.length() > OrderImportConstants.FieldLength.FIELD_LENGTH_VEHICLE) {
      return OrderImportConstants.CheckResultMessage.VEHICLE_TOO_LONG;
    }
    return null;
  }

  public String verifyCustomerOrSupplierName(Object value) {
    if (value == null) {
      return null;
    }
    String supplierName = String.valueOf(value);
    if (StringUtil.isEmpty(supplierName)) {
      return null;
    }
    if (supplierName.length() > OrderImportConstants.FieldLength.FIELD_LENGTH_SUPPLIER_NAME) {
      return OrderImportConstants.CheckResultMessage.SUPPLIER_NAME_TOO_LONG;
    }
    return null;
  }
  public String verifyContact(Object value) {
    if (value == null) {
      return null;
    }
    String customerName = String.valueOf(value);
    if (StringUtil.isEmpty(customerName)) {
      return null;
    }
    if (customerName.length() > OrderImportConstants.FieldLength.FIELD_LENGTH_CONTACT) {
      return OrderImportConstants.CheckResultMessage.CONTACT_TOO_LONG;
    }
    return null;
  }
  public String verifyMobile(Object value) {
    if (value == null) {
      return null;
    }
    String customerName = String.valueOf(value);
    if (StringUtil.isEmpty(customerName)) {
      return null;
    }
    if (customerName.length() > OrderImportConstants.FieldLength.FIELD_LENGTH_MOBILE) {
      return OrderImportConstants.CheckResultMessage.MOBILE_TOO_LONG;
    }
    return null;
  }

  public String verifyMemberType(Object value) {
    if (value == null) {
      return null;
    }
    String customerName = String.valueOf(value);
    if (StringUtil.isEmpty(customerName)) {
      return null;
    }
    if (customerName.length() > OrderImportConstants.FieldLength.FIELD_LENGTH_MEMBER_TYPE) {
      return OrderImportConstants.CheckResultMessage.MEMBER_TYPE_TOO_LONG;
    }
    return null;
  }

  public String verifyMemberCardNo(Object value) {
    if (value == null) {
      return null;
    }
    String customerName = String.valueOf(value);
    if (StringUtil.isEmpty(customerName)) {
      return null;
    }
    if (customerName.length() > OrderImportConstants.FieldLength.FIELD_LENGTH_MEMBER_CARD_NO) {
      return OrderImportConstants.CheckResultMessage.MEMBER_CARD_NO_TOO_LONG;
    }
    return null;
  }

  public String verifyPayPerProject(Object value) {
    if (value == null) {
      return null;
    }
    String customerName = String.valueOf(value);
    if (StringUtil.isEmpty(customerName)) {
      return null;
    }
    if (customerName.length() > OrderImportConstants.FieldLength.FIELD_LENGTH_PAY_PER_PROJECT) {
      return OrderImportConstants.CheckResultMessage.PAY_PER_PROJECT_TOO_LONG;
    }
    return null;
  }

  public String verifySalesMan(Object value) {
    if (value == null) {
      return null;
    }
    String customerName = String.valueOf(value);
    if (StringUtil.isEmpty(customerName)) {
      return null;
    }
    if (customerName.length() > OrderImportConstants.FieldLength.FIELD_LENGTH_SALES_MAN) {
      return OrderImportConstants.CheckResultMessage.SALES_MAN_TOO_LONG;
    }
    return null;
  }
  public String verifyServiceTotal(Object value) {
  if (value == null) {
      return null;
    }
    String total = String.valueOf(value);
    if (StringUtil.isEmpty(total)) {
      return null;
    }
    if(!NumberUtil.isNumber(total)){
      return OrderImportConstants.CheckResultMessage.SERVICE_TOTAL_NOT_NUMBER;
    }
    return null;
  }

  public String verifyServiceWorker(Object value) {
   if (value == null) {
      return null;
    }
    String customerName = String.valueOf(value);
    if (StringUtil.isEmpty(customerName)) {
      return null;
    }
    if (customerName.length() > OrderImportConstants.FieldLength.FIELD_LENGTH_SALES_MAN) {
      return OrderImportConstants.CheckResultMessage.SERVICE_WORKER_TOO_LONG;
    }
    return null;
  }
  public String verifyServiceContent(Object value) {
    if (value == null) {
      return null;
    }
    String customerName = String.valueOf(value);
    if (StringUtil.isEmpty(customerName)) {
      return null;
    }
    if (customerName.length() > OrderImportConstants.FieldLength.FIELD_LENGTH_SERVICE_CONTENT) {
      return OrderImportConstants.CheckResultMessage.SERVICE_CONTENT_TOO_LONG;
    }
    return null;
  }

  public String verifyInTime(Object value) {
    if (value == null) {
      return null;
    }
    String inTime = String.valueOf(value);
    if (StringUtil.isEmpty(inTime)) {
      return null;
    }
    if (inTime.length() > OrderImportConstants.FieldLength.FIELD_LENGTH_STANDARD_DATE_FORMAT) {
      return OrderImportConstants.CheckResultMessage.IN_TIME_TOO_LONG;
    }
    if(!DateUtil.isStandardDateFormat(inTime)){
      return OrderImportConstants.CheckResultMessage.ERROR_DATE_FOMART;
    }
    return null;
  }

  public String verifyOutTime(Object value) {
    if (value == null) {
      return null;
    }
    String outTime = String.valueOf(value);
    if (StringUtil.isEmpty(outTime)) {
      return null;
    }
    if (outTime.length() > OrderImportConstants.FieldLength.FIELD_LENGTH_STANDARD_DATE_FORMAT) {
      return OrderImportConstants.CheckResultMessage.OUT_TIME_TOO_LONG;
    }
    if(!DateUtil.isStandardDateFormat(outTime)){
      return OrderImportConstants.CheckResultMessage.ERROR_DATE_FOMART;
    }
    return null;
  }

  public String verifyPayWay(Object value) {
  if (value == null) {
      return null;
    }
    String customerName = String.valueOf(value);
    if (StringUtil.isEmpty(customerName)) {
      return null;
    }
    if (customerName.length() > OrderImportConstants.FieldLength.FIELD_LENGTH_SALES_MAN) {
      return OrderImportConstants.CheckResultMessage.PAY_WAY_TOO_LONG;
    }
    return null;
  }

  public String verifyTotal(Object value) {
    if (value == null) {
      return null;
    }
    String total = String.valueOf(value);
    if (StringUtil.isEmpty(total)) {
      return null;
    }
    if(!NumberUtil.isNumber(total)){
      return OrderImportConstants.CheckResultMessage.TOTAL_NOT_NUMBER;
    }
    return null;
  }

  public String verifyActuallyPaid(Object value) {
   if (value == null) {
      return null;
    }
    String total = String.valueOf(value);
    if (StringUtil.isEmpty(total)) {
      return null;
    }
    if(!NumberUtil.isNumber(total)){
      return OrderImportConstants.CheckResultMessage.ACTUALLY_PAID_NOT_NUMBER;
    }
    return null;
  }


  public String verifyDebt(Object value) {
    if (value == null) {
      return null;
    }
    String debt = String.valueOf(value);
    if (StringUtil.isEmpty(debt)) {
      return null;
    }
    if(!NumberUtil.isNumber(debt)){
      return OrderImportConstants.CheckResultMessage.DEBT_NOT_NUMBER;
    }
    return null;
  }



  public String verifyMemo(Object value) {
    if (value == null) {
      return null;
    }
    String memo = String.valueOf(value);
    if (StringUtil.isEmpty(memo)) {
      return null;
    }
    if (memo.length() > OrderImportConstants.FieldLength.FIELD_LENGTH_MEMO) {
      return OrderImportConstants.CheckResultMessage.MEMO_TOO_LONG;
    }
    return null;
  }



  /**
   * 验证时间的正确性，月日是否符合实际
   * @param str
   * @param str
   * @return
   */
  public String checkDateStrFormat(String str)
  {
    int year = Integer.parseInt(str.split("-")[0]) ;
    int month = Integer.parseInt(str.split("-")[1]);
    int day = Integer.parseInt(str.split("-")[2]);

    String remind="";

    if(month<0 || month >12)
    {
      remind = "月份要在1到12之间！";
      return remind;
    }

    switch (month)
    {
      case 1 : if(day>31) remind="1月份号数不能大于31！";break;
      case 2 : if(year%4 == 0 && day>29) remind="闰年2月份号数不能大于29！";
      else if(year%4 !=0 && day>28) remind="平年2月份号数不能大于29！";break;
      case 3 : if(day>31) remind="3月份号数不能大于31！";break;
      case 4 : if(day>30) remind="4月份号数不能大于30！";break;
      case 5 : if(day>31) remind="5月份号数不能大于31！";break;
      case 6 : if(day>30) remind="6月份号数不能大于30！";break;
      case 7 : if(day>31) remind="7月份号数不能大于31！";break;
      case 8 : if(day>31) remind="8月份号数不能大于31！";break;
      case 9 : if(day>30) remind="9月份号数不能大于30！";break;
      case 10 : if(day>31) remind="10月份号数不能大于31！";break;
      case 11 : if(day>30) remind="11月份号数不能大于30！";break;
      case 12 : if(day>31) remind="12月份号数不能大于31！";
    }

    return remind;
  }


}
