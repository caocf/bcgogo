package com.bcgogo.user.service.excelimport.supplier;

import com.bcgogo.config.service.excelimport.ExcelImportException;
import com.bcgogo.config.service.excelimport.ExcelImportUtil;
import com.bcgogo.enums.RelationTypes;
import com.bcgogo.stat.dto.SupplierRecordDTO;
import com.bcgogo.user.dto.SupplierDTO;
import com.bcgogo.utils.StringUtil;
import org.apache.axis.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 根据从excel中解析出的一行数据构造供应商信息
 * Created by IntelliJ IDEA.
 * User: ZouJianhong
 * Date: 12-4-17
 * Time: 下午3:23
 * To change this template use File | Settings | File Templates.
 */
@Component
public class SupplierImportDTOGenerator {

  private static final Logger LOG = LoggerFactory.getLogger(SupplierImportDTOGenerator.class);

  public SupplierDTO generate(Map<String, Object> data, Map<String, String> fieldMapping, Long shopId) throws ExcelImportException {
    if (shopId == null) {
      return null;
    }
    SupplierDTO supplierDTO = new SupplierDTO();
    supplierDTO.setShopId(shopId);
    supplierDTO.setRelationType(RelationTypes.UNRELATED);
    if (data.get(fieldMapping.get(SupplierImportConstants.FieldName.NAME)) != null) {
      supplierDTO.setName(String.valueOf(data.get(fieldMapping.get(SupplierImportConstants.FieldName.NAME))).trim());
    }
    if (data.get(fieldMapping.get(SupplierImportConstants.FieldName.ABBR)) != null) {
      supplierDTO.setAbbr(String.valueOf(data.get(fieldMapping.get(SupplierImportConstants.FieldName.ABBR))).trim());
    }
    if(data.get(fieldMapping.get(SupplierImportConstants.FieldName.CITY)) != null && data.get(SupplierImportConstants.PROVINCE_AND_CITY_NO) != null) {
      String provinceAndCityNo = String.valueOf(data.get(SupplierImportConstants.PROVINCE_AND_CITY_NO));
      String[] provinceAndCityNoArray = provinceAndCityNo.split(",");
      if(provinceAndCityNoArray.length > 1) {
        supplierDTO.setProvince(Long.valueOf(provinceAndCityNoArray[0]));
        supplierDTO.setCity(Long.valueOf(provinceAndCityNoArray[1]));
      } else {
        supplierDTO.setProvince(Long.valueOf(provinceAndCityNoArray[0]));
      }
    }

    if (data.get(fieldMapping.get(SupplierImportConstants.FieldName.ADDRESS)) != null) {
      supplierDTO.setAddress(String.valueOf(data.get(fieldMapping.get(SupplierImportConstants.FieldName.ADDRESS))).trim());
    }
    if (data.get(fieldMapping.get(SupplierImportConstants.FieldName.CONTACT)) != null) {
      supplierDTO.setContact(String.valueOf(data.get(fieldMapping.get(SupplierImportConstants.FieldName.CONTACT))).trim());
    }
    if (data.get(fieldMapping.get(SupplierImportConstants.FieldName.MOBILE)) != null) {
      supplierDTO.setMobile(ExcelImportUtil.dealWithNumberString(data.get(fieldMapping.get(SupplierImportConstants.FieldName.MOBILE))).trim());
    }
    if (data.get(fieldMapping.get(SupplierImportConstants.FieldName.LANDLINE)) != null) {
      supplierDTO.setLandLine(String.valueOf(data.get(fieldMapping.get(SupplierImportConstants.FieldName.LANDLINE))).trim());
    }
    if (data.get(fieldMapping.get(SupplierImportConstants.FieldName.FAX)) != null) {
      supplierDTO.setFax(String.valueOf(data.get(fieldMapping.get(SupplierImportConstants.FieldName.FAX))).trim());
    }
    if (data.get(fieldMapping.get(SupplierImportConstants.FieldName.QQ)) != null) {
      supplierDTO.setQq(ExcelImportUtil.dealWithNumberString(data.get(fieldMapping.get(SupplierImportConstants.FieldName.QQ))).trim());
    }
    if (data.get(fieldMapping.get(SupplierImportConstants.FieldName.EMAIL)) != null) {
      supplierDTO.setEmail(String.valueOf(data.get(fieldMapping.get(SupplierImportConstants.FieldName.EMAIL))).trim());
    }
    if (data.get(fieldMapping.get(SupplierImportConstants.FieldName.BANK)) != null) {
      supplierDTO.setBank(String.valueOf(data.get(fieldMapping.get(SupplierImportConstants.FieldName.BANK))).trim());
    }
    if (data.get(fieldMapping.get(SupplierImportConstants.FieldName.BANK_ACCOUNT_NAME)) != null) {
      supplierDTO.setAccountName(String.valueOf(data.get(fieldMapping.get(SupplierImportConstants.FieldName.BANK_ACCOUNT_NAME))).trim());
    }
    if (data.get(fieldMapping.get(SupplierImportConstants.FieldName.ACCOUNT)) != null) {
      supplierDTO.setAccount(String.valueOf(data.get(fieldMapping.get(SupplierImportConstants.FieldName.ACCOUNT))).trim());
    }
    if (data.get(fieldMapping.get(SupplierImportConstants.FieldName.BUSINESS_SCOPE)) != null) {
      supplierDTO.setBusinessScope(String.valueOf(data.get(fieldMapping.get(SupplierImportConstants.FieldName.BUSINESS_SCOPE))).trim());
    }

    if (data.get(fieldMapping.get(SupplierImportConstants.FieldName.INVOICE_CATEGORY)) != null) {
      String invoiceCategoryStr = String.valueOf(data.get(fieldMapping.get(SupplierImportConstants.FieldName.INVOICE_CATEGORY))).trim();

      String invoiceCategory = "4";
      if("普通发票".equals(invoiceCategoryStr))
      {
        invoiceCategory = "1";
      }
      else if("增值税发票".equals(invoiceCategoryStr))
      {
        invoiceCategory = "2";
      }
      else if("内部".equals(invoiceCategoryStr))
      {
        invoiceCategory = "3";
      }
      supplierDTO.setInvoiceCategory(invoiceCategory);
    }

    SupplierRecordDTO supplierRecordDTO = new SupplierRecordDTO();
    supplierRecordDTO.setCreditAmount(0D);
    supplierRecordDTO.setShopId(supplierDTO.getShopId());

    supplierDTO.setSupplierRecordDTO(supplierRecordDTO);
    return supplierDTO;
  }
}
