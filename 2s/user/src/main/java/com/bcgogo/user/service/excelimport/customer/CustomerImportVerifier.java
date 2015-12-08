package com.bcgogo.user.service.excelimport.customer;

import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.service.excelimport.ExcelImportException;
import com.bcgogo.config.service.excelimport.ImportVerifier;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.dto.ValidateImportDataDTO;
import com.bcgogo.utils.RegexUtils;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * 客户导入字段校验类
 * Created by IntelliJ IDEA.
 * User: ZouJianhong
 * Date: 12-4-7
 * Time: 下午3:01
 * To change this template use File | Settings | File Templates.
 */
@Component
public class CustomerImportVerifier implements ImportVerifier {

  @Override
  public String verify(Map<String, Object> data, Map<String, String> fieldMapping,ValidateImportDataDTO validateImportDataDTO) throws ExcelImportException {
    String verifyName = verifyName(data.get(fieldMapping.get(CustomerImportConstants.FieldName.NAME)));
    if (!StringUtil.isEmpty(verifyName)) {
      return verifyName;
    }
    String verifyShortName = verifyShortName(data.get(fieldMapping.get(CustomerImportConstants.FieldName.SHORT_NAME)));
    if (!StringUtil.isEmpty(verifyShortName)) {
      return verifyShortName;
    }
    String verifyCity = verifyCity(data,data.get(fieldMapping.get(CustomerImportConstants.FieldName.CITY)),validateImportDataDTO);
    if (!StringUtil.isEmpty(verifyCity)) {
      return verifyCity;
    }
    String verifyAddress = verifyAddress(data.get(fieldMapping.get(CustomerImportConstants.FieldName.ADDRESS)));
    if (!StringUtil.isEmpty(verifyAddress)) {
      return verifyAddress;
    }
    String verifyContact = verifyContact(data.get(fieldMapping.get(CustomerImportConstants.FieldName.CONTACT)));
    if (!StringUtil.isEmpty(verifyContact)) {
      return verifyContact;
    }
    String verifyMobile = verifyMobile(data.get(fieldMapping.get(CustomerImportConstants.FieldName.MOBILE)),validateImportDataDTO);
    if (!StringUtil.isEmpty(verifyMobile)) {
      return verifyMobile;
    }
    String verifyLandline = verifyLandline(data.get(fieldMapping.get(CustomerImportConstants.FieldName.LANDLINE)),validateImportDataDTO);
    if (!StringUtil.isEmpty(verifyLandline)) {
      return verifyLandline;
    }
    String verifyFax = verifyFax(data.get(fieldMapping.get(CustomerImportConstants.FieldName.FAX)));
    if (!StringUtil.isEmpty(verifyFax)) {
      return verifyFax;
    }
    String verifyArea = verifyArea(data.get(fieldMapping.get(CustomerImportConstants.FieldName.AREA)));
    if (!StringUtil.isEmpty(verifyArea)) {
      return verifyArea;
    }
    String verifyBirthday = verifyBirthday(data.get(fieldMapping.get(CustomerImportConstants.FieldName.BIRTHDAY)));
    if (!StringUtil.isEmpty(verifyBirthday)) {
      return verifyBirthday;
    }
    String verifyQQ = verifyQQ(data.get(fieldMapping.get(CustomerImportConstants.FieldName.QQ)));
    if (!StringUtil.isEmpty(verifyQQ)) {
      return verifyQQ;
    }
    String verifyEmail = verifyEmail(data.get(fieldMapping.get(CustomerImportConstants.FieldName.EMAIL)));
    if (!StringUtil.isEmpty(verifyEmail)) {
      return verifyEmail;
    }
    String verifyBank = verifyBank(data.get(fieldMapping.get(CustomerImportConstants.FieldName.BANK)));
    if (!StringUtil.isEmpty(verifyBank)) {
      return verifyBank;
    }
    String verifyBankAccountName = verifyBankAccountName(data.get(fieldMapping.get(CustomerImportConstants.FieldName.BANK_ACCOUNT_NAME)));
    if (!StringUtil.isEmpty(verifyBankAccountName)) {
      return verifyBankAccountName;
    }
    String verifyAccount = verifyAccount(data.get(fieldMapping.get(CustomerImportConstants.FieldName.ACCOUNT)));
    if (!StringUtil.isEmpty(verifyAccount)) {
      return verifyAccount;
    }
    String verifyInvoiceCategory = verifyInvoiceCategory(data.get(fieldMapping.get(CustomerImportConstants.FieldName.INVOICE_CATEGORY)));
    if (!StringUtil.isEmpty(verifyInvoiceCategory)) {
      return verifyInvoiceCategory;
    }
    String verifySettlementType = verifySettlementType(data.get(fieldMapping.get(CustomerImportConstants.FieldName.SETTLEMENT_TYPE)));
    if (!StringUtil.isEmpty(verifySettlementType)) {
      return verifySettlementType;
    }
    String verifyCustomerKind = verifyCustomerKind(data.get(fieldMapping.get(CustomerImportConstants.FieldName.CUSTOMER_KIND)));
    if (!StringUtil.isEmpty(verifyCustomerKind)) {
      return verifyCustomerKind;
    }

    String verifyLicenceNo = verifyLicenceNo(data.get(fieldMapping.get(CustomerImportConstants.FieldName.VEHICLE_LICENCE_NO)),validateImportDataDTO);
    if (!StringUtil.isEmpty(verifyLicenceNo)) {
      return verifyLicenceNo;
    }

    String verifyBrand = verifyBrand(data.get(fieldMapping.get(CustomerImportConstants.FieldName.VEHICLE_BRAND)));
    if (!StringUtil.isEmpty(verifyBrand)) {
      return verifyBrand;
    }

    String verifyModel = verifyModel(data.get(fieldMapping.get(CustomerImportConstants.FieldName.VEHICLE_MODEL)));
    if (!StringUtil.isEmpty(verifyModel)) {
      return verifyModel;
    }

    String verifyYear = verifyYear(data.get(fieldMapping.get(CustomerImportConstants.FieldName.VEHICLE_YEAR)));
    if (!StringUtil.isEmpty(verifyYear)) {
      return verifyYear;
    }

    String verifyEngine = verifyEngine(data.get(fieldMapping.get(CustomerImportConstants.FieldName.VEHICLE_ENGINE)));
    if (!StringUtil.isEmpty(verifyEngine)) {
      return verifyEngine;
    }

    String verifyEngineNO = verifyEngineNo(data.get(fieldMapping.get(CustomerImportConstants.FieldName.VEHICLE_ENGINE_NO)));
    if (!StringUtil.isEmpty(verifyEngineNO)) {
      return verifyEngineNO;
    }

    String verifyChassisNumber = verifyChassisNumber(data.get(fieldMapping.get(CustomerImportConstants.FieldName.VEHICLE_CHASSIS_NUMBER)));
    if (!StringUtil.isEmpty(verifyChassisNumber)) {
      return verifyChassisNumber;
    }

    String verifyMemberNo = verifyMemberNo(data.get(fieldMapping.get(CustomerImportConstants.FieldName.MEMBER_NO)),validateImportDataDTO);
    if (!StringUtil.isEmpty(verifyMemberNo)) {
      return verifyMemberNo;
    }

    if(null != data.get(fieldMapping.get(CustomerImportConstants.FieldName.MEMBER_NO)))
    {
      String verifyType = verifyType(data.get(fieldMapping.get(CustomerImportConstants.FieldName.TYPE)));
      if (!StringUtil.isEmpty(verifyType)) {
        return verifyType;
      }

      String verifyBalance = verifyBalance(data.get(fieldMapping.get(CustomerImportConstants.FieldName.BALANCE)));
      if (!StringUtil.isEmpty(verifyBalance)) {
        return verifyBalance;
      }

      String verifyAccumulatePoints = verifyAccumulatePoints(data.get(fieldMapping.get(CustomerImportConstants.FieldName.ACCUMULATE_POINTS)));
      if(!StringUtil.isEmpty(verifyAccumulatePoints))
      {
        return verifyAccumulatePoints;
      }
      String verifyMemberDiscount = verifyMemberDiscount(data.get(fieldMapping.get(CustomerImportConstants.FieldName.MEMBER_DISCOUNT)));
      if(!StringUtil.isEmpty(verifyMemberDiscount))
      {
        return verifyMemberDiscount;
      }
//      String verifyServiceDiscount = verifyServiceDiscount(data.get(fieldMapping.get(CustomerImportConstants.FieldName.SERVICE_DISCOUNT)));
//      if(!StringUtil.isEmpty(verifyServiceDiscount))
//      {
//        return verifyServiceDiscount;
//      }

//      String verifyMaterialDiscount = verifyMaterialDiscount(data.get(fieldMapping.get(CustomerImportConstants.FieldName.MATERIAL_DISCOUNT)));
//      if(!StringUtil.isEmpty(verifyMaterialDiscount))
//      {
//        return verifyMaterialDiscount;
//      }

      String verifyJoinDate = verifyJoinDate(data.get(fieldMapping.get(CustomerImportConstants.FieldName.JOIN_DATE)));
      if(!StringUtil.isEmpty(verifyJoinDate))
      {
        return verifyJoinDate;
      }

      String verifyDeadline = verifyDeadline(data.get(fieldMapping.get(CustomerImportConstants.FieldName.DEADLINE)));
      if(!StringUtil.isEmpty(verifyDeadline))
      {
        return verifyDeadline;
      }
    }

    return null;
  }

  public String verifyName(Object value) {
    if (value == null) {
      return CustomerImportConstants.CheckResultMessage.EMPTY_NAME;
    }
    String name = String.valueOf(value);
    if (StringUtil.isEmpty(name)) {
      return CustomerImportConstants.CheckResultMessage.EMPTY_NAME;
    }
    if (name.length() > CustomerImportConstants.FieldLength.FIELD_LENGTH_NAME) {
      return CustomerImportConstants.CheckResultMessage.NAME_TOO_LONG;
    }
    return null;
  }

  public String verifyShortName(Object value) {
    if (value == null) {
      return null;
    }
    String shortName = String.valueOf(value);
    if (StringUtil.isEmpty(shortName)) {
      return null;
    }
    if (shortName.length() > CustomerImportConstants.FieldLength.FIELD_LENGTH_SHORT_NAME) {
      return CustomerImportConstants.CheckResultMessage.SHORT_NAME_TOO_LONG;
    }
    return null;
  }

  public String verifyCity(Map<String, Object> data,Object value,ValidateImportDataDTO validateImportDataDTO) {
    if (value == null) {
      return null;
    }
    String city = String.valueOf(value);
    city = city.trim();
    if (StringUtil.isEmpty(city)) {
      return null;
    }
    if (city.length() > CustomerImportConstants.FieldLength.FIELD_LENGTH_CITY) {
      return CustomerImportConstants.CheckResultMessage.CITY_TOO_LONG;
    }
    String provinceAndCityNo = ServiceManager.getService(IConfigService.class).getProvinceAndCityNoByCityName(city,validateImportDataDTO);
    if(StringUtils.isEmpty(provinceAndCityNo)) {
      return CustomerImportConstants.CheckResultMessage.CITY_NAME_ERROR;
    }
    data.put(CustomerImportConstants.PROVINCE_AND_CITY_NO,provinceAndCityNo);
    return null;
  }

  public String verifyAddress(Object value) {
    if (value == null) {
      return null;
    }
    String address = String.valueOf(value);
    if (StringUtil.isEmpty(address)) {
      return null;
    }
    if (address.length() > CustomerImportConstants.FieldLength.FIELD_LENGTH_ADDRESS) {
      return CustomerImportConstants.CheckResultMessage.ADDRESS_TOO_LONG;
    }
    return null;
  }


  public String verifyContact(Object value) {
    if (value == null) {
      return null;
    }
    String contact = String.valueOf(value);
    if (StringUtil.isEmpty(contact)) {
      return null;
    }
    if (contact.length() > CustomerImportConstants.FieldLength.FIELD_LENGTH_CONTACT) {
      return CustomerImportConstants.CheckResultMessage.CONTACT_TOO_LONG;
    }
    return null;
  }

  public String verifyMobile(Object value,ValidateImportDataDTO validateImportDataDTO) {
    if (value == null) {
      return null;
    }
    String mobile = String.valueOf(value);
    if (StringUtil.isEmpty(mobile)) {
      return null;
    }
    if (mobile.length() > CustomerImportConstants.FieldLength.FIELD_LENGTH_MOBILE) {
      return CustomerImportConstants.CheckResultMessage.MOBILE_TOO_LONG;
    }

    String[] mobiles = mobile.split("/");
    for(String str : mobiles)
    {
      if(null != validateImportDataDTO && null != validateImportDataDTO.getMobileCustomerMap()
          && null != validateImportDataDTO.getMobileCustomerMap().get(str))
      {
        return "\""+str+"\""+CustomerImportConstants.CheckResultMessage.MOBILE_EXIST_IN_TABLE;
      }
    }

    return null;
  }

  public String verifyLandline(Object value, ValidateImportDataDTO validateImportDataDTO) {
    if (value == null) {
      return null;
    }
    String landline = String.valueOf(value);
    if (StringUtil.isEmpty(landline)) {
      return null;
    }
    if (landline.length() > CustomerImportConstants.FieldLength.FIELD_LENGTH_LANDLINE) {
      return CustomerImportConstants.CheckResultMessage.LANDLINE_TOO_LONG;
    }
    String[] landlines = landline.split("/");
    for (String landLine : landlines) {
      if (null != validateImportDataDTO && MapUtils.isNotEmpty(validateImportDataDTO.getLandLineCustomerMap())
          && null != validateImportDataDTO.getLandLineCustomerMap().get(landLine)) {
        return "\"" + landLine + "\"" + CustomerImportConstants.CheckResultMessage.LAND_LINE_EXIST_IN_TABLE;
      }
    }
    return null;
  }

  public String verifyFax(Object value) {
    if (value == null) {
      return null;
    }
    String fax = String.valueOf(value);
    if (StringUtil.isEmpty(fax)) {
      return null;
    }
    if (fax.length() > CustomerImportConstants.FieldLength.FIELD_LENGTH_FAX) {
      return CustomerImportConstants.CheckResultMessage.FAX_TOO_LONG;
    }
    return null;
  }

  public String verifyArea(Object value) {
    if (value == null) {
      return null;
    }
    String area = String.valueOf(value);
    if (StringUtil.isEmpty(area)) {
      return null;
    }
    if (area.length() > CustomerImportConstants.FieldLength.FIELD_LENGTH_AREA) {
      return CustomerImportConstants.CheckResultMessage.AREA_TOO_LONG;
    }
    return null;
  }

  public String verifyBirthday(Object value) {
    if (value == null) {
      return null;
    }
    String birthday = String.valueOf(value);
    if (StringUtil.isEmpty(birthday)) {
      return null;
    }
    if (birthday.length() > CustomerImportConstants.FieldLength.FIELD_LENGTH_BIRTHDAY) {
      return CustomerImportConstants.CheckResultMessage.BIRTHDAY_TOO_LONG;
    }
    return null;
  }

  public String verifyQQ(Object value) {
    if (value == null) {
      return null;
    }
    String qq = String.valueOf(value);
    if (StringUtil.isEmpty(qq)) {
      return null;
    }
    if (qq.length() > CustomerImportConstants.FieldLength.FIELD_LENGTH_QQ) {
      return CustomerImportConstants.CheckResultMessage.QQ_TOO_LONG;
    }
    return null;
  }

  public String verifyEmail(Object value) {
    if (value == null) {
      return null;
    }
    String email = String.valueOf(value);
    if (StringUtil.isEmpty(email)) {
      return null;
    }
    if (email.length() > CustomerImportConstants.FieldLength.FIELD_LENGTH_EMAIL) {
      return CustomerImportConstants.CheckResultMessage.EMAIL_TOO_LONG;
    }
    return null;
  }

  public String verifyBank(Object value) {
    if (value == null) {
      return null;
    }
    String bank = String.valueOf(value);
    if (StringUtil.isEmpty(bank)) {
      return null;
    }
    if (bank.length() > CustomerImportConstants.FieldLength.FIELD_LENGTH_BANK) {
      return CustomerImportConstants.CheckResultMessage.BANK_TOO_LONG;
    }
    return null;
  }

  public String verifyBankAccountName(Object value) {
    if (value == null) {
      return null;
    }
    String bankAccountName = String.valueOf(value);
    if (StringUtil.isEmpty(bankAccountName)) {
      return null;
    }
    if (bankAccountName.length() > CustomerImportConstants.FieldLength.FIELD_LENGTH_BANK_ACCOUNT_NAME) {
      return CustomerImportConstants.CheckResultMessage.BANK_ACCOUNT_NAME_TOO_LONG;
    }
    return null;
  }

  public String verifyAccount(Object value) {
    if (value == null) {
      return null;
    }
    String account = String.valueOf(value);
    if (StringUtil.isEmpty(account)) {
      return null;
    }
    if (account.length() > CustomerImportConstants.FieldLength.FIELD_LENGTH_ACCOUNT) {
      return CustomerImportConstants.CheckResultMessage.ACCOUNT_TOO_LONG;
    }
    return null;
  }

  public String verifyInvoiceCategory(Object value) {
    if (value == null) {
      return null;
    }
    String invoiceCategory = String.valueOf(value);
    if (StringUtil.isEmpty(invoiceCategory)) {
      return null;
    }
    if (invoiceCategory.length() > CustomerImportConstants.FieldLength.FIELD_LENGTH_INVOICE_CATEGORY) {
      return CustomerImportConstants.CheckResultMessage.INVOICE_CATEGORY_TOO_LONG;
    }
    return null;
  }

  public String verifySettlementType(Object value) {
    if (value == null) {
      return null;
    }
    String settlementType = String.valueOf(value);
    if (StringUtil.isEmpty(settlementType)) {
      return null;
    }
    if (settlementType.length() > CustomerImportConstants.FieldLength.FIELD_LENGTH_SETTLEMENT_TYPE) {
      return CustomerImportConstants.CheckResultMessage.SETTLEMENT_TYPE_TOO_LONG;
    }
    return null;
  }

  public String verifyCustomerKind(Object value) {
    if (value == null) {
      return null;
    }
    String customerKind = String.valueOf(value);
    if (StringUtil.isEmpty(customerKind)) {
      return null;
    }
    if (customerKind.length() > CustomerImportConstants.FieldLength.FIELD_LENGTH_CUSTOMER_KIND) {
      return CustomerImportConstants.CheckResultMessage.CUSTOMER_KIND_TOO_LONG;
    }
    return null;
  }

  public String verifyLicenceNo(Object value,ValidateImportDataDTO validateImportDataDTO) {
    if (value == null) {
      return null;
    }
    String licenceNo = String.valueOf(value);
    if (StringUtil.isEmpty(licenceNo)) {
      return null;
    }
    if (licenceNo.length() > CustomerImportConstants.FieldLength.FIELD_LENGTH_VEHICLE_LINCENCE_NO) {
      return CustomerImportConstants.CheckResultMessage.VEHICLE_LICENCE_NO_TOO_LONG;
    }

    String[] licenceNos = licenceNo.split(",");

    for(String str : licenceNos)
    {
      if(null != validateImportDataDTO && null != validateImportDataDTO.getVehicleDTOMap()
          && null != validateImportDataDTO.getVehicleDTOMap().get(str))
      {
        return "\""+str+"\""+CustomerImportConstants.CheckResultMessage.VEHICLE_EXIST_IN_TABLE;
      }
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
    if (brand.length() > CustomerImportConstants.FieldLength.FIELD_LENGTH_VEHICLE_BRAND) {
      return CustomerImportConstants.CheckResultMessage.VEHICLE_BRAND_TOO_LONG;
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
    if (model.length() > CustomerImportConstants.FieldLength.FIELD_LENGTH_VEHICLE_MODEL) {
      return CustomerImportConstants.CheckResultMessage.VEHICLE_MODEL_TOO_LONG;
    }
    return null;
  }

  public String verifyYear(Object value) {
    if (value == null) {
      return null;
    }
    String year = String.valueOf(value);
    if (StringUtil.isEmpty(year)) {
      return null;
    }
    if (year.length() > CustomerImportConstants.FieldLength.FIELD_LENGTH_VEHICLE_YEAR) {
      return CustomerImportConstants.CheckResultMessage.VEHICLE_YEAR_TOO_LONG;
    }
    return null;
  }

  public String verifyEngine(Object value) {
    if (value == null) {
      return null;
    }
    String engine = String.valueOf(value);
    if (StringUtil.isEmpty(engine)) {
      return null;
    }
    if (engine.length() > CustomerImportConstants.FieldLength.FIELD_LENGTH_VEHICLE_ENGINE) {
      return CustomerImportConstants.CheckResultMessage.VEHICLE_ENGINE_TOO_LONG;
    }
    return null;
  }

  public String verifyEngineNo(Object value) {
    if (value == null) {
      return null;
    }
    String engineNo = String.valueOf(value);
    if (StringUtil.isEmpty(engineNo)) {
      return null;
    }
    if (engineNo.length() > CustomerImportConstants.FieldLength.FIELD_LENGTH_VEHICLE_ENGINE_NO) {
      return CustomerImportConstants.CheckResultMessage.VEHICLE_ENGINE_NO_TOO_LONG;
    }
    return null;
  }

  public String verifyChassisNumber(Object value) {
    if (value == null) {
      return null;
    }
    String chassisNumber = String.valueOf(value).replace(" ","");
    if (StringUtil.isEmpty(chassisNumber)) {
      return null;
    }
    if (chassisNumber.length() > CustomerImportConstants.FieldLength.FIELD_LENGTH_VEHICLE_VIN) {
      return CustomerImportConstants.CheckResultMessage.VEHICLE_CHASSIS_NUMBER_TOO_LONG;
    }

    if(!RegexUtils.isChassisNumber(chassisNumber))
    {
      return CustomerImportConstants.CheckResultMessage.VEHICLE_CHASSIS_NUMBER_ENGLISH_AND_NUMBER;
    }

    return null;
  }

  public String verifyMemberNo(Object value,ValidateImportDataDTO validateImportDataDTO)
  {
    if(null == value)
    {
      return null;
    }

    String memberNo = String.valueOf(value);

    if(StringUtils.isBlank(memberNo))
    {
      return null;
    }

    String scienceNumReg = "^((-?\\d+.?\\d*)[Ee]{1}(-?\\d+))$";
    Pattern pattern = Pattern.compile(scienceNumReg);
    if(pattern.matcher(memberNo).matches()){
      return CustomerImportConstants.CheckResultMessage.MEMBER_NO_SCIENCE_PATTERN;
    }

    if(memberNo.length() > CustomerImportConstants.FieldLength.FIELD_LENGTH_MEMBER_NO)
    {
      return CustomerImportConstants.CheckResultMessage.MEMBER_NO_TOO_LONG;
    }

    if(null != validateImportDataDTO && null != validateImportDataDTO.getMemberDTOMap()
        && null != validateImportDataDTO.getMemberDTOMap().get(memberNo))
    {
      return "\""+memberNo+"\""+CustomerImportConstants.CheckResultMessage.MEMBER_EXIST_IN_TABLE;
    }

    return null;
  }

  public String verifyType(Object value)
  {
    if(null == value)
    {
      return null;
    }

    String type = String.valueOf(value);

    if(StringUtils.isBlank(type))
    {
      return null;
    }

    if(type.length() > CustomerImportConstants.FieldLength.FIELD_LENGTH_MEMBER_TYPE)
    {
      return CustomerImportConstants.CheckResultMessage.MEMBER_TYPE_TOO_LONG;
    }
    return null;
  }

  public String verifyBalance(Object value)
  {
    if (null == value)
    {
      return null;
    }
    String balance = String.valueOf(value);

    if(StringUtils.isBlank(balance))
    {
      return null;
    }

    if(!NumberUtils.isNumber(balance))
    {
      return CustomerImportConstants.FieldName.BALANCE_DESC+CustomerImportConstants.CheckResultMessage.NOT_NUMBER;
    }

    return null;
  }

  public String verifyAccumulatePoints(Object value)
  {
    if(null == value)
    {
      return null;
    }

    String accumulatePoints = String.valueOf(value);

    if(StringUtils.isBlank(accumulatePoints))
    {
      return null;
    }

    if(!isInteger(accumulatePoints))
    {
      return CustomerImportConstants.FieldName.ACCUMULATE_POINTS_DESC+CustomerImportConstants.CheckResultMessage.NOT_INTEGER;
    }
    return null;
  }

//  public String verifyServiceDiscount(Object value)
//  {
//    if(null == value)
//    {
//      return null;
//    }
//
//    String serviceDiscount = String.valueOf(value);
//
//    if(StringUtils.isBlank(serviceDiscount))
//    {
//      return null;
//    }
//
//    if(!NumberUtils.isNumber(serviceDiscount))
//    {
//      return CustomerImportConstants.FieldName.SERVICE_DISCOUNT_DESC+CustomerImportConstants.CheckResultMessage.NOT_NUMBER;
//    }
//
//    return null;
//  }
//
//  public String verifyMaterialDiscount(Object value)
//  {
//    if(null == value)
//    {
//      return null;
//    }
//
//    String materialDiscount = String.valueOf(value);
//
//    if(StringUtils.isBlank(materialDiscount))
//    {
//      return null;
//    }
//
//    if(!NumberUtils.isNumber(materialDiscount))
//    {
//      return CustomerImportConstants.FieldName.MATERIAL_DISCOUNT_DESC+CustomerImportConstants.CheckResultMessage.NOT_NUMBER;
//    }
//
//    return null;
//  }

  public String verifyMemberDiscount(Object value)
  {
    if(null == value)
    {
      return null;
    }

    String memberDiscount = String.valueOf(value);

    if(StringUtils.isBlank(memberDiscount))
    {
      return null;
    }

    if(!NumberUtils.isNumber(memberDiscount))
    {
      return CustomerImportConstants.FieldName.MEMBER_DISCOUNT_DESC+CustomerImportConstants.CheckResultMessage.NOT_NUMBER;
    }

    if(Double.valueOf(memberDiscount).intValue()<1 || Double.valueOf(memberDiscount).intValue()>10)
    {
      return CustomerImportConstants.FieldName.MEMBER_DISCOUNT_DESC+CustomerImportConstants.CheckResultMessage.ONE_TO_TEN;
    }

    return null;
  }

  public String verifyJoinDate(Object value)
  {
    if(null == value)
    {
      return null;
    }

    String joinDate = String.valueOf(value);

    if(StringUtils.isBlank(joinDate))
    {
      return null;
    }

    String datePattern1 = "\\d{4}-\\d{2}-\\d{2}";
    Pattern pattern = Pattern.compile(datePattern1);

    if(!pattern.matcher(joinDate).matches())
    {
      return CustomerImportConstants.FieldName.JOIN_DATE_DESC+CustomerImportConstants.CheckResultMessage.FORMAT_NOT_EXPECT;
    }

    String remind = checkDateStrFormat(joinDate);

    if(StringUtils.isNotBlank(remind))
    {
      return CustomerImportConstants.FieldName.JOIN_DATE_DESC+remind;
    }

    return null;
  }

  public String verifyDeadline(Object value)
  {
    if(null == value)
    {
      return null;
    }

    String deadline = String.valueOf(value);

    if(StringUtils.isBlank(deadline))
    {
      return null;
    }

    String datePattern1 = "\\d{4}-\\d{2}-\\d{2}";
    Pattern pattern = Pattern.compile(datePattern1);

    if(!pattern.matcher(deadline).matches())
    {
      return CustomerImportConstants.FieldName.DEADLINE_DESC+CustomerImportConstants.CheckResultMessage.FORMAT_NOT_EXPECT;
    }

    String remind = checkDateStrFormat(deadline);

    if(StringUtils.isNotBlank(remind))
    {
      return CustomerImportConstants.FieldName.DEADLINE_DESC+remind;
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

  /**
   * 从excel传到后台的时候整数会变成double型
   * @return
   */
  public boolean isInteger(String str)
  {
    if(!NumberUtils.isNumber(str))
    {
      return false;
    }

    if(-1 == str.indexOf("\\."))
    {
      return true;
    }

    str = str.split("\\.")[1];

    if(Integer.valueOf(str).intValue() == 0)
    {
      return true;
    }
    else
    {
      return false;
    }
  }
}
