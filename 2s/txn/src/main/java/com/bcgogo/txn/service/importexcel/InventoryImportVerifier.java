package com.bcgogo.txn.service.importexcel;

import com.bcgogo.config.service.excelimport.ExcelImportException;
import com.bcgogo.config.service.excelimport.ImportVerifier;
import com.bcgogo.user.dto.ValidateImportDataDTO;
import com.bcgogo.user.service.utils.BcgogoShopLogicResourceUtils;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.RegexUtils;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.lang.Validate;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 库存导入数据校验类
 * Created by IntelliJ IDEA.
 * User: ZouJianhong
 * Date: 12-4-19
 * Time: 下午1:41
 * To change this template use File | Settings | File Templates.
 */
@Component
public class InventoryImportVerifier implements ImportVerifier {

  @Override
  public String verify(Map<String, Object> data, Map<String, String> fieldMapping,ValidateImportDataDTO validateImportDataDTO) throws ExcelImportException {
    String verifyProductName = verifyProductName(data.get(fieldMapping.get(InventoryImportConstants.FieldName.PRODUCT_NAME)));
    if (!StringUtil.isEmpty(verifyProductName)) {
      return verifyProductName;
    }
    String verifyProductBrand = verifyProductBrand(data.get(fieldMapping.get(InventoryImportConstants.FieldName.PRODUCT_BRAND)));
    if (!StringUtil.isEmpty(verifyProductBrand)) {
      return verifyProductBrand;
    }
    String verifyProductModel = verifyProductModel(data.get(fieldMapping.get(InventoryImportConstants.FieldName.PRODUCT_MODEL)));
    if (!StringUtil.isEmpty(verifyProductModel)) {
      return verifyProductModel;
    }
    String verifyProductSpec = verifyProductSpec(data.get(fieldMapping.get(InventoryImportConstants.FieldName.PRODUCT_SPEC)));
    if (!StringUtil.isEmpty(verifyProductSpec)) {
      return verifyProductSpec;
    }
    String verifyVehicleBrand = verifyVehicleBrand(data.get(fieldMapping.get(InventoryImportConstants.FieldName.VEHICLE_BRAND)));
    if (!StringUtil.isEmpty(verifyVehicleBrand)) {
      return verifyVehicleBrand;
    }
    String verifyVehicleModel = verifyVehicleModel(data.get(fieldMapping.get(InventoryImportConstants.FieldName.VEHICLE_MODEL)));
    if (!StringUtil.isEmpty(verifyVehicleModel)) {
      return verifyVehicleModel;
    }
    String verifyVehicleYear = verifyVehicleYear(data.get(fieldMapping.get(InventoryImportConstants.FieldName.VEHICLE_YEAR)));
    if (!StringUtil.isEmpty(verifyVehicleYear)) {
      return verifyVehicleYear;
    }
    String verifyVehicleEngine = verifyVehicleEngine(data.get(fieldMapping.get(InventoryImportConstants.FieldName.VEHICLE_ENGINE)));
    if (!StringUtil.isEmpty(verifyVehicleEngine)) {
      return verifyVehicleEngine;
    }
    String verifyInventoryAmount = verifyInventoryAmount(data.get(fieldMapping.get(InventoryImportConstants.FieldName.INVENTORY_AMOUNT)));
    if (!StringUtil.isEmpty(verifyInventoryAmount)) {
      return verifyInventoryAmount;
    }
    String verifyPurchasePrice = verifyPurchasePrice(data.get(fieldMapping.get(InventoryImportConstants.FieldName.PURCHASE_PRICE)));
    if (!StringUtil.isEmpty(verifyPurchasePrice)) {
      return verifyPurchasePrice;
    }
    String verifySalePrice = verifySalePrice(data.get(fieldMapping.get(InventoryImportConstants.FieldName.SALE_PRICE)));
    if (!StringUtil.isEmpty(verifySalePrice)) {
      return verifySalePrice;
    }

    String verifyCommodityCode = verifyCommodityCode(data.get(fieldMapping.get(InventoryImportConstants.FieldName.COMMODITY_CODE)),validateImportDataDTO);
    if (!StringUtil.isEmpty(verifyCommodityCode)) {
      return verifyCommodityCode;
    }

    String verifyUnit = verifyUnit(data.get(fieldMapping.get(InventoryImportConstants.FieldName.UNIT)));
    if (!StringUtil.isEmpty(verifyUnit)) {
      return verifyUnit;
    }

    String verifyStorageBin = verifyStorageBin(data.get(fieldMapping.get(InventoryImportConstants.FieldName.STORAGE_BIN)));
    if (!StringUtil.isEmpty(verifyStorageBin)) {
      return verifyUnit;
    }

    String verifyTradePrice = verifyTradePrice(data.get(fieldMapping.get(InventoryImportConstants.FieldName.TRADE_PRICE)));
    if (!StringUtil.isEmpty(verifyTradePrice)) {
      return verifyUnit;
    }

    String verifyStoreHouse = verifyStoreHouse(data.get(fieldMapping.get(InventoryImportConstants.FieldName.STORE_HOUSE)));
    if (!StringUtil.isEmpty(verifyStoreHouse)) {
      return verifyStoreHouse;
    }

    return null;
  }

  public String verifyProductName(Object value) {
    if (value == null) {
      return InventoryImportConstants.CheckResultMessage.EMPTY_PRODUCT_NAME;
    }
    String productName = String.valueOf(value);
    if (StringUtil.isEmpty(productName)) {
      return InventoryImportConstants.CheckResultMessage.EMPTY_PRODUCT_NAME;
    }
    if (productName.length() > InventoryImportConstants.FieldLength.FIELD_LENGTH_PRODUCT_NAME) {
      return InventoryImportConstants.CheckResultMessage.PRODUCT_NAME_TOO_LONG;
    }
    return null;
  }

  public String verifyProductBrand(Object value) {
    if (value == null) {
      return null;
    }
    String productBrand = String.valueOf(value);
    if (StringUtil.isEmpty(productBrand)) {
      return null;
    }
    if (productBrand.length() > InventoryImportConstants.FieldLength.FIELD_LENGTH_PRODUCT_BRAND) {
      return InventoryImportConstants.CheckResultMessage.PRODUCT_BRAND_TOO_LONG;
    }
    return null;
  }

  public String verifyProductSpec(Object value) {
    if (value == null) {
      return null;
    }
    String productSpec = String.valueOf(value);
    if (StringUtil.isEmpty(productSpec)) {
      return null;
    }
    if (productSpec.length() > InventoryImportConstants.FieldLength.FIELD_LENGTH_PRODUCT_SPEC) {
      return InventoryImportConstants.CheckResultMessage.PRODUCT_SPEC_TOO_LONG;
    }
    return null;
  }


  public String verifyProductModel(Object value) {
    if (value == null) {
      return null;
    }
    String productModel = String.valueOf(value);
    if (StringUtil.isEmpty(productModel)) {
      return null;
    }
    if (productModel.length() > InventoryImportConstants.FieldLength.FIELD_LENGTH_PRODUCT_MODEL) {
      return InventoryImportConstants.CheckResultMessage.PRODUCT_MODEL_TOO_LONG;
    }
    return null;
  }

  public String verifyVehicleBrand(Object value) {
    if (value == null) {
      return null;
    }
    String vehicleBrand = String.valueOf(value);
    if (StringUtil.isEmpty(vehicleBrand)) {
      return null;
    }
    if (vehicleBrand.length() > InventoryImportConstants.FieldLength.FIELD_LENGTH_VEHICLE_BRAND) {
      return InventoryImportConstants.CheckResultMessage.VEHICLE_BRAND_TOO_LONG;
    }
    return null;
  }

  public String verifyVehicleModel(Object value) {
    if (value == null) {
      return null;
    }
    String vehicleModel = String.valueOf(value);
    if (StringUtil.isEmpty(vehicleModel)) {
      return null;
    }
    if (vehicleModel.length() > InventoryImportConstants.FieldLength.FIELD_LENGTH_VEHICLE_MODEL) {
      return InventoryImportConstants.CheckResultMessage.VEHICLE_MODEL_TOO_LONG;
    }
    return null;
  }

  public String verifyVehicleYear(Object value) {
    if (value == null) {
      return null;
    }
    String vehicleYear = String.valueOf(value);
    if (StringUtil.isEmpty(vehicleYear)) {
      return null;
    }
    if (vehicleYear.length() > InventoryImportConstants.FieldLength.FIELD_LENGTH_VEHICLE_YEAR) {
      return InventoryImportConstants.CheckResultMessage.VEHICLE_YEAR_TOO_LONG;
    }
    return null;
  }

  public String verifyVehicleEngine(Object value) {
    if (value == null) {
      return null;
    }
    String vehicleEngine = String.valueOf(value);
    if (StringUtil.isEmpty(vehicleEngine)) {
      return null;
    }
    if (vehicleEngine.length() > InventoryImportConstants.FieldLength.FIELD_LENGTH_VEHICLE_ENGINE) {
      return InventoryImportConstants.CheckResultMessage.VEHICLE_ENGINE_TOO_LONG;
    }
    return null;
  }

  public String verifyInventoryAmount(Object value) {
    if (value == null) {
      return null;
    }
    String inventoryAmount = String.valueOf(value);
    if (StringUtil.isEmpty(inventoryAmount)) {
      return null;
    }
    if(!NumberUtil.isNumber(inventoryAmount)){
      return InventoryImportConstants.CheckResultMessage.INVENTORY_AMOUNT_NOT_NUMBER;
    }
    return null;
  }

  public String verifyPurchasePrice(Object value) {
    if (value == null) {
      return null;
    }
    String purchasePrice = String.valueOf(value);
    if (StringUtil.isEmpty(purchasePrice)) {
      return null;
    }
    if(!NumberUtil.isNumber(purchasePrice)){
      return InventoryImportConstants.CheckResultMessage.PURCHASE_PRICE_NOT_NUMBER;
    }
    if(!NumberUtil.isPositiveNumber(purchasePrice))
    {
    return InventoryImportConstants.CheckResultMessage.PURCHASE_PRICE_NOT_NUMBER;
    }
    return null;
  }

  public String verifySalePrice(Object value) {
    if (value == null) {
      return null;
    }
    String salePrice = String.valueOf(value);
    if (StringUtil.isEmpty(salePrice)) {
      return null;
    }
    if(!NumberUtil.isNumber(salePrice)){
      return InventoryImportConstants.CheckResultMessage.SALE_PRICE_NOT_NUMBER;
    }
    return null;
  }

  public String verifyCommodityCode(Object value,ValidateImportDataDTO validateImportDataDTO) {
    if (value == null) {
      return null;
    }
    String commodityCode = String.valueOf(value);
    if (StringUtil.isEmpty(commodityCode)) {
      return null;
    }
    if(commodityCode.length()>InventoryImportConstants.FieldLength.FIELD_LENGTH_COMMODITY_CODE){
      return InventoryImportConstants.CheckResultMessage.COMMODITY_CODE_TOO_LONG;
    }

//    if(RegexUtils.hasSpecialCharacters(commodityCode))
//    {
//      return InventoryImportConstants.CheckResultMessage.COMMODITY_CODE_SPECIAL_CHARACTERS;
//    }

    if(null != validateImportDataDTO && null != validateImportDataDTO.getComProductMap()
        && null != validateImportDataDTO.getComProductMap().get(commodityCode))
    {
      return "\""+commodityCode+"\""+InventoryImportConstants.CheckResultMessage.COMMODITY_CODE_EXIST_IN_TABLE;
    }

    return null;
  }

  public String verifyUnit(Object value) {
    if (value == null) {
      return null;
    }
    String unit = String.valueOf(value);
    if (StringUtil.isEmpty(unit)) {
      return null;
    }
    if(unit.length()>InventoryImportConstants.FieldLength.FIELD_LENGTH_UNIT){
      return InventoryImportConstants.CheckResultMessage.UNIT_TOO_LONG;
    }

    return null;
  }

  public String verifyStorageBin(Object value) {
    if (value == null) {
      return null;
    }
    String storageBin = String.valueOf(value);
    if (StringUtil.isEmpty(storageBin)) {
      return null;
    }
    if(storageBin.length()>InventoryImportConstants.FieldLength.FIELD_LENGTH_STORAGE_BIN){
      return InventoryImportConstants.CheckResultMessage.STORAGE_BIN_TOO_LONG;
    }

    return null;
  }

  public String verifyTradePrice(Object value) {
    if (value == null) {
      return null;
    }
    String tradePrice = String.valueOf(value);
    if (StringUtil.isEmpty(tradePrice)) {
      return null;
    }
    if(!NumberUtil.isNumber(tradePrice)){
      return InventoryImportConstants.CheckResultMessage.TRADE_PRICE_NOT_NUMBER;
    }
    return null;
  }

  public String verifyStoreHouse(Object value) {
    if (value == null) {
      return null;
    }
    String storeHouse = String.valueOf(value);
    if (StringUtil.isEmpty(storeHouse)) {
      return null;
    }
    if(storeHouse.length()>InventoryImportConstants.FieldLength.FIELD_LENGTH_STORE_HOUSE){
      return InventoryImportConstants.CheckResultMessage.STORE_HOUSE_TOO_LONG;
    }
    return null;
  }
}
