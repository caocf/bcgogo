package com.bcgogo.txn.service.importexcel;

import com.bcgogo.config.service.excelimport.ExcelImportException;
import com.bcgogo.constant.ProductConstants;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.product.dto.ProductLocalInfoDTO;
import com.bcgogo.search.dto.InventorySearchIndexDTO;
import com.bcgogo.txn.dto.InventoryDTO;
import com.bcgogo.txn.dto.InventoryInfoDTO;
import com.bcgogo.txn.dto.PurchasePriceDTO;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.PinyinUtil;
import com.bcgogo.utils.SearchConstant;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 库存导入时库存相关信息封装类生成器
 * Created by IntelliJ IDEA.
 * User: ZouJianhong
 * Date: 12-4-19
 * Time: 下午3:21
 * To change this template use File | Settings | File Templates.
 */
@Component
public class InventoryInfoDTOGenerator {

  public InventoryInfoDTO generate(Map<String, Object> data, Map<String, String> fieldMapping, Long shopId) throws ExcelImportException{
    if (shopId == null) {
      return null;
    }
    InventoryInfoDTO inventoryInfoDTO = new InventoryInfoDTO();
    ProductDTO productDTO = new ProductDTO();
    if (data.get(fieldMapping.get(InventoryImportConstants.FieldName.PRODUCT_NAME)) != null) {
      String productName = String.valueOf(data.get(fieldMapping.get(InventoryImportConstants.FieldName.PRODUCT_NAME)));
      productDTO.setName(productName);
      productDTO.setFirstLetter(PinyinUtil.getFirstLetter(productName));
      productDTO.setFirstLetterCombination(PinyinUtil.converterToFirstSpell(productName));
    }
    if (data.get(fieldMapping.get(InventoryImportConstants.FieldName.PRODUCT_BRAND)) != null) {
      productDTO.setBrand(String.valueOf(data.get(fieldMapping.get(InventoryImportConstants.FieldName.PRODUCT_BRAND))));
    }
    if (data.get(fieldMapping.get(InventoryImportConstants.FieldName.PRODUCT_SPEC)) != null) {
      productDTO.setSpec(String.valueOf(data.get(fieldMapping.get(InventoryImportConstants.FieldName.PRODUCT_SPEC))));
    }
    if (data.get(fieldMapping.get(InventoryImportConstants.FieldName.PRODUCT_MODEL)) != null) {
      productDTO.setModel(String.valueOf(data.get(fieldMapping.get(InventoryImportConstants.FieldName.PRODUCT_MODEL))));
    }
//    if (!StringUtil.isEmpty(String.valueOf(data.get(fieldMapping.get(InventoryImportConstants.FieldName.VEHICLE_BRAND))))) {
    if (data.get(fieldMapping.get(InventoryImportConstants.FieldName.VEHICLE_BRAND)) != null) {
      productDTO.setVehicleBrand(String.valueOf(data.get(fieldMapping.get(InventoryImportConstants.FieldName.VEHICLE_BRAND))));
      productDTO.setProductVehicleBrand(String.valueOf(data.get(fieldMapping.get(InventoryImportConstants.FieldName.VEHICLE_BRAND))));
    }
//	  else{
//      productDTO.setProductVehicleBrand(InventoryImportConstants.DefaultValue.DEFAULT_VALUE_PRODUCT_VEHHICLE_BRAND);
//    }
    if (data.get(fieldMapping.get(InventoryImportConstants.FieldName.VEHICLE_MODEL)) != null) {
      productDTO.setVehicleModel(String.valueOf(data.get(fieldMapping.get(InventoryImportConstants.FieldName.VEHICLE_MODEL))));
      productDTO.setProductVehicleModel(String.valueOf(data.get(fieldMapping.get(InventoryImportConstants.FieldName.VEHICLE_MODEL))));
    }
    if (data.get(fieldMapping.get(InventoryImportConstants.FieldName.VEHICLE_ENGINE)) != null) {
      productDTO.setVehicleEngine(String.valueOf(data.get(fieldMapping.get(InventoryImportConstants.FieldName.VEHICLE_ENGINE))));
      productDTO.setProductVehicleEngine(String.valueOf(data.get(fieldMapping.get(InventoryImportConstants.FieldName.VEHICLE_ENGINE))));
    }
    if (data.get(fieldMapping.get(InventoryImportConstants.FieldName.VEHICLE_YEAR)) != null) {
      String vehileYear = StringUtil.doubleStringToIntegerString(String.valueOf(data.get(fieldMapping.get(InventoryImportConstants.FieldName.VEHICLE_YEAR))));
      productDTO.setVehicleYear(vehileYear);
      productDTO.setProductVehicleYear(vehileYear);
    }

    if(data.get(fieldMapping.get(InventoryImportConstants.FieldName.COMMODITY_CODE)) != null)
    {
      String commodity = String.valueOf(data.get(fieldMapping.get(InventoryImportConstants.FieldName.COMMODITY_CODE)));
      productDTO.setCommodityCode(commodity);
    }

    String unit = null;
    if(data.get(fieldMapping.get(InventoryImportConstants.FieldName.UNIT)) != null)
    {
      unit = String.valueOf(data.get(fieldMapping.get(InventoryImportConstants.FieldName.UNIT)));
    }

    //默认为通用
    productDTO.setProductVehicleStatus(SearchConstant.PRODUCT_PRODUCTSTATUS_ALL);
    //默认为新产品
    productDTO.setCheckStatus(ProductConstants.CheckStatus.CHECK_STATUS_NEW);
    productDTO.setShopId(shopId);
    inventoryInfoDTO.setProductDTO(productDTO);

    ProductLocalInfoDTO productLocalInfoDTO = new ProductLocalInfoDTO();
    if (NumberUtil.isNumber(String.valueOf(data.get(fieldMapping.get(InventoryImportConstants.FieldName.PURCHASE_PRICE))))) {
      productLocalInfoDTO.setPurchasePrice(NumberUtil.doubleValue(String.valueOf(data.get(fieldMapping.get(InventoryImportConstants.FieldName.PURCHASE_PRICE))), 0L));
    }else{
      productLocalInfoDTO.setPurchasePrice(0.0);
    }

    if (NumberUtil.isNumber(String.valueOf(data.get(fieldMapping.get(InventoryImportConstants.FieldName.TRADE_PRICE))))) {
      productLocalInfoDTO.setTradePrice(NumberUtil.doubleValue(String.valueOf(data.get(fieldMapping.get(InventoryImportConstants.FieldName.TRADE_PRICE))), 0L));
    }else{
      productLocalInfoDTO.setTradePrice(0.0);
    }

    String storageBin = null;
    if (data.get(fieldMapping.get(InventoryImportConstants.FieldName.STORAGE_BIN)) != null) {
      storageBin = String.valueOf(data.get(fieldMapping.get(InventoryImportConstants.FieldName.STORAGE_BIN)));
    }
    String storehouse = null;
    if (data.get(fieldMapping.get(InventoryImportConstants.FieldName.STORE_HOUSE)) != null) {
      storehouse = String.valueOf(data.get(fieldMapping.get(InventoryImportConstants.FieldName.STORE_HOUSE)));
    }
    inventoryInfoDTO.setStoreHouse(storehouse);

    productLocalInfoDTO.setStorageBin(storageBin);
    productLocalInfoDTO.setShopId(shopId);
    productLocalInfoDTO.setSellUnit(unit);
    productLocalInfoDTO.setStorageUnit(unit);
    /*设定销售价*/
      if (NumberUtils.isNumber(String.valueOf(data.get(fieldMapping.get(InventoryImportConstants.FieldName.SALE_PRICE))))) {
      productLocalInfoDTO.setPrice(Double.valueOf(data.get(fieldMapping.get(InventoryImportConstants.FieldName.SALE_PRICE)).toString()));
    }
    inventoryInfoDTO.setProductLocalInfoDTO(productLocalInfoDTO);

    InventoryDTO inventoryDTO = new InventoryDTO();
    inventoryDTO.setShopId(shopId);
    if (NumberUtil.isNumber(String.valueOf(data.get(fieldMapping.get(InventoryImportConstants.FieldName.INVENTORY_AMOUNT))))) {
      inventoryDTO.setAmount(NumberUtil.doubleValue(String.valueOf(data.get(fieldMapping.get(InventoryImportConstants.FieldName.INVENTORY_AMOUNT))), 0L));
    }else{
      inventoryDTO.setAmount(0D);
    }
    inventoryDTO.setInventoryAveragePrice(productLocalInfoDTO.getPurchasePrice());
    inventoryDTO.setSalesPrice(productLocalInfoDTO.getPrice());
    inventoryDTO.setUnit(unit);
    inventoryDTO.setLatestInventoryPrice(productLocalInfoDTO.getPurchasePrice());
    inventoryDTO.setLastStorageTime(System.currentTimeMillis());
    inventoryInfoDTO.setInventoryDTO(inventoryDTO);

    PurchasePriceDTO purchasePriceDTO = new PurchasePriceDTO();
    purchasePriceDTO.setShopId(shopId);
    purchasePriceDTO.setPrice(productLocalInfoDTO.getPurchasePrice());
    purchasePriceDTO.setDate(System.currentTimeMillis());
    inventoryInfoDTO.setPurchasePriceDTO(purchasePriceDTO);

    InventorySearchIndexDTO inventorySearchIndexDTO = new InventorySearchIndexDTO();
    inventorySearchIndexDTO.setShopId(shopId);
    inventorySearchIndexDTO.setProductName(productDTO.getName());
    inventorySearchIndexDTO.setProductBrand(productDTO.getBrand());
    inventorySearchIndexDTO.setProductModel(productDTO.getModel());
    inventorySearchIndexDTO.setProductSpec(productDTO.getSpec());
    inventorySearchIndexDTO.setBrand(productDTO.getVehicleBrand());
    inventorySearchIndexDTO.setUnit(unit);
    inventorySearchIndexDTO.setModel(productDTO.getVehicleModel());
    inventorySearchIndexDTO.setYear(productDTO.getVehicleYear());
    inventorySearchIndexDTO.setEngine(productDTO.getVehicleEngine());
    inventorySearchIndexDTO.setProductVehicleStatus(productDTO.getProductVehicleStatus());
    inventorySearchIndexDTO.setEditDate(System.currentTimeMillis());
    inventorySearchIndexDTO.setAmount(inventoryDTO.getAmount());
    inventorySearchIndexDTO.setPurchasePrice(productLocalInfoDTO.getPurchasePrice());
    inventorySearchIndexDTO.setPrice(productLocalInfoDTO.getPrice());
    inventorySearchIndexDTO.setCommodityCode(productDTO.getCommodityCode());
    inventorySearchIndexDTO.setRecommendedPrice(productLocalInfoDTO.getPrice());                //设定销售价
    inventorySearchIndexDTO.setInventoryAveragePrice(productLocalInfoDTO.getPurchasePrice());
    inventorySearchIndexDTO.setTradePrice(productLocalInfoDTO.getTradePrice());
    inventorySearchIndexDTO.setStorageBin(productLocalInfoDTO.getStorageBin());
    inventoryInfoDTO.setInventorySearchIndexDTO(inventorySearchIndexDTO);


    return inventoryInfoDTO;
  }
}
