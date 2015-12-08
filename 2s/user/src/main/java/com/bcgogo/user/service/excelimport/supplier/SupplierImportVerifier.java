package com.bcgogo.user.service.excelimport.supplier;

import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.service.excelimport.ExcelImportException;
import com.bcgogo.config.service.excelimport.ImportVerifier;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.dto.ValidateImportDataDTO;
import com.bcgogo.user.service.excelimport.customer.CustomerImportConstants;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 供应商导入字段校验类
 * Created by IntelliJ IDEA.
 * User: ZouJianhong
 * Date: 12-4-17
 * Time: 下午3:22
 * To change this template use File | Settings | File Templates.
 */
@Component
public class SupplierImportVerifier implements ImportVerifier {

  @Override
  public String verify(Map<String, Object> data, Map<String, String> fieldMapping,ValidateImportDataDTO validateImportDataDTO) throws ExcelImportException {
    String verifyName = verifyName(data.get(fieldMapping.get(SupplierImportConstants.FieldName.NAME)));
    if (!StringUtil.isEmpty(verifyName)) {
      return verifyName;
    }
    String verifyAbbr = verifyAbbr(data.get(fieldMapping.get(SupplierImportConstants.FieldName.ABBR)));
    if (!StringUtil.isEmpty(verifyAbbr)) {
      return verifyAbbr;
    }
    String verifyCity = verifyCity(data,data.get(fieldMapping.get(SupplierImportConstants.FieldName.CITY)),validateImportDataDTO);
    if (!StringUtil.isEmpty(verifyCity)) {
      return verifyCity;
    }
    String verifyAddress = verifyAddress(data.get(fieldMapping.get(SupplierImportConstants.FieldName.ADDRESS)));
    if (!StringUtil.isEmpty(verifyAddress)) {
      return verifyAddress;
    }
    String verifyContact = verifyContact(data.get(fieldMapping.get(SupplierImportConstants.FieldName.CONTACT)));
    if (!StringUtil.isEmpty(verifyContact)) {
      return verifyContact;
    }
    String verifyMobile = verifyMobile(data.get(fieldMapping.get(SupplierImportConstants.FieldName.MOBILE)),validateImportDataDTO);
    if (!StringUtil.isEmpty(verifyMobile)) {
      return verifyMobile;
    }
    String verifyLandline = verifyLandline(data.get(fieldMapping.get(SupplierImportConstants.FieldName.LANDLINE)),validateImportDataDTO);
    if (!StringUtil.isEmpty(verifyLandline)) {
      return verifyLandline;
    }
    String verifyFax = verifyFax(data.get(fieldMapping.get(SupplierImportConstants.FieldName.FAX)));
    if (!StringUtil.isEmpty(verifyFax)) {
      return verifyFax;
    }
    String verifyQQ = verifyQQ(data.get(fieldMapping.get(SupplierImportConstants.FieldName.QQ)));
    if (!StringUtil.isEmpty(verifyQQ)) {
      return verifyQQ;
    }
    String verifyEmail = verifyEmail(data.get(fieldMapping.get(SupplierImportConstants.FieldName.EMAIL)));
    if (!StringUtil.isEmpty(verifyEmail)) {
      return verifyEmail;
    }
    String verifyBank = verifyBank(data.get(fieldMapping.get(SupplierImportConstants.FieldName.BANK)));
    if (!StringUtil.isEmpty(verifyBank)) {
      return verifyBank;
    }
    String verifyBankAccountName = verifyBankAccountName(data.get(fieldMapping.get(SupplierImportConstants.FieldName.BANK_ACCOUNT_NAME)));
    if (!StringUtil.isEmpty(verifyBankAccountName)) {
      return verifyBankAccountName;
    }
    String verifyAccount = verifyAccount(data.get(fieldMapping.get(SupplierImportConstants.FieldName.ACCOUNT)));
    if (!StringUtil.isEmpty(verifyAccount)) {
      return verifyAccount;
    }
    String verifyInvoiceCategory = verifyInvoiceCategory(data.get(fieldMapping.get(SupplierImportConstants.FieldName.INVOICE_CATEGORY)));
    if (!StringUtil.isEmpty(verifyInvoiceCategory)) {
      return verifyInvoiceCategory;
    }
    String verifyBusinessScope = verifyBusinessScope(data.get(fieldMapping.get(SupplierImportConstants.FieldName.BUSINESS_SCOPE)));
    if (!StringUtil.isEmpty(verifyBusinessScope)) {
      return verifyBusinessScope;
    }
    return null;
  }

  public String verifyName(Object value) {
    if (value == null) {
      return SupplierImportConstants.CheckResultMessage.EMPTY_NAME;
    }
    String name = String.valueOf(value);
    if (StringUtil.isEmpty(name)) {
      return SupplierImportConstants.CheckResultMessage.EMPTY_NAME;
    }
    if (name.length() > SupplierImportConstants.FieldLength.FIELD_LENGTH_NAME) {
      return SupplierImportConstants.CheckResultMessage.NAME_TOO_LONG;
    }
    return null;
  }

  public String verifyAbbr(Object value) {
    if (value == null) {
      return null;
    }
    String abbr = String.valueOf(value);
    if (StringUtil.isEmpty(abbr)) {
      return null;
    }
    if (abbr.length() > SupplierImportConstants.FieldLength.FIELD_LENGTH_ABBR) {
      return SupplierImportConstants.CheckResultMessage.ABBR_TOO_LONG;
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
    if (city.length() > SupplierImportConstants.FieldLength.FIELD_LENGTH_CITY) {
      return SupplierImportConstants.CheckResultMessage.CITY_TOO_LONG;
    }
    String provinceAndCityNo = ServiceManager.getService(IConfigService.class).getProvinceAndCityNoByCityName(city,validateImportDataDTO);
    if(StringUtils.isEmpty(provinceAndCityNo)) {
      return SupplierImportConstants.CheckResultMessage.CITY_NAME_ERROR;
    }
    data.put(SupplierImportConstants.PROVINCE_AND_CITY_NO,provinceAndCityNo);
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
    if (address.length() > SupplierImportConstants.FieldLength.FIELD_LENGTH_ADDRESS) {
      return SupplierImportConstants.CheckResultMessage.ADDRESS_TOO_LONG;
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
    if (contact.length() > SupplierImportConstants.FieldLength.FIELD_LENGTH_CONTACT) {
      return SupplierImportConstants.CheckResultMessage.CONTACT_TOO_LONG;
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
    if (mobile.length() > SupplierImportConstants.FieldLength.FIELD_LENGTH_MOBILE) {
      return SupplierImportConstants.CheckResultMessage.MOBILE_TOO_LONG;
    }

    String[] mobiles = mobile.split("/");
    for(String str : mobiles)
    {
      if(null != validateImportDataDTO && null != validateImportDataDTO.getMobileSupplierMap()
          && null != validateImportDataDTO.getMobileSupplierMap().get(str))
      {
        return "\""+str+"\""+SupplierImportConstants.CheckResultMessage.MOBILE_EXIST_IN_TABLE;
      }
    }

    return null;
  }

  public String verifyLandline(Object value,ValidateImportDataDTO validateImportDataDTO) {
    if (value == null) {
      return null;
    }
    String landline = String.valueOf(value);
    if (StringUtil.isEmpty(landline)) {
      return null;
    }
    if (landline.length() > SupplierImportConstants.FieldLength.FIELD_LENGTH_LANDLINE) {
      return SupplierImportConstants.CheckResultMessage.LANDLINE_TOO_LONG;
    }
    String[] landlines = landline.split("/");
    for (String landLine : landlines) {
      if (null != validateImportDataDTO && MapUtils.isNotEmpty(validateImportDataDTO.getLandLineSupplierMap())
          && null != validateImportDataDTO.getLandLineSupplierMap().get(landLine)) {
        return "\"" + landLine + "\"" + SupplierImportConstants.CheckResultMessage.LAND_LINE_EXIST_IN_TABLE;
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
    if (fax.length() > SupplierImportConstants.FieldLength.FIELD_LENGTH_FAX) {
      return SupplierImportConstants.CheckResultMessage.FAX_TOO_LONG;
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
    if (qq.length() > SupplierImportConstants.FieldLength.FIELD_LENGTH_QQ) {
      return SupplierImportConstants.CheckResultMessage.QQ_TOO_LONG;
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
    if (email.length() > SupplierImportConstants.FieldLength.FIELD_LENGTH_EMAIL) {
      return SupplierImportConstants.CheckResultMessage.EMAIL_TOO_LONG;
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
    if (bank.length() > SupplierImportConstants.FieldLength.FIELD_LENGTH_BANK) {
      return SupplierImportConstants.CheckResultMessage.BANK_TOO_LONG;
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
    if (bankAccountName.length() > SupplierImportConstants.FieldLength.FIELD_LENGTH_BANK_ACCOUNT_NAME) {
      return SupplierImportConstants.CheckResultMessage.BANK_ACCOUNT_NAME_TOO_LONG;
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
    if (account.length() > SupplierImportConstants.FieldLength.FIELD_LENGTH_ACCOUNT) {
      return SupplierImportConstants.CheckResultMessage.ACCOUNT_TOO_LONG;
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
    if (invoiceCategory.length() > SupplierImportConstants.FieldLength.FIELD_LENGTH_INVOICE_CATEGORY) {
      return SupplierImportConstants.CheckResultMessage.INVOICE_CATEGORY_TOO_LONG;
    }
    return null;
  }

  public String verifyBusinessScope(Object value) {
    if (value == null) {
      return null;
    }
    String businessScope = String.valueOf(value);
    if (StringUtil.isEmpty(businessScope)) {
      return null;
    }
    if (businessScope.length() > SupplierImportConstants.FieldLength.FIELD_LENGTH_BUSINESS_SCOPE) {
      return SupplierImportConstants.CheckResultMessage.BUSINESS_SCOPE_TOO_LONG;
    }
    return null;
  }
}
