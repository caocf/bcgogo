package com.bcgogo.txn.service;

import com.bcgogo.AbstractTest;
import com.bcgogo.config.dto.ImportResult;
import com.bcgogo.config.service.excelimport.CheckResult;
import com.bcgogo.config.service.excelimport.ExcelImportConstants;
import com.bcgogo.config.service.excelimport.ImportContext;
import com.bcgogo.constant.ProductConstants;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.product.dto.ProductLocalInfoDTO;
import com.bcgogo.product.service.IProductService;
import com.bcgogo.search.model.InventorySearchIndex;
import com.bcgogo.search.service.ISearchService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.InventoryDTO;
import com.bcgogo.txn.dto.PurchasePriceDTO;
import com.bcgogo.txn.service.importexcel.InventoryImportConstants;
import com.bcgogo.txn.service.importexcel.InventoryImporter;
import com.bcgogo.utils.SearchConstant;
import com.bcgogo.utils.StringUtil;
import junit.framework.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 库存导入功能单元测试.
 * User: ZouJianhong
 * Date: 12-4-24
 * Time: 下午2:23
 * To change this template use File | Settings | File Templates.
 */
public class InventoryImportTest extends AbstractTest{

  /**
   * 构造导入的数据
   * @return
   */
  private List<Map<String, Object>> generateImportData(){
    List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
    Map<String, Object> data = null;
    for(int i = 0; i < 10; i ++){
      data = new HashMap<String, Object>();
      data.put("商品名称","00819寸雨刮");
      data.put("品牌", "品牌1");
      data.put("规格", "规格1");
      data.put("型号", "型号1");
      data.put("车辆品牌", "奥迪");
      data.put("车型", "A6");
      data.put("车辆年代", "2009");
      data.put("库存数量", "10");
      data.put("入库价", "110");
      data.put("销售价", "200");
      dataList.add(data);
    }
    return dataList;
  }

  /**
   * 构造字段映射关系
   * @return
   */
  private Map<String, String> generateFieldMapping(){
    Map<String, String> fieldMapping = new HashMap<String, String>();
    fieldMapping.put(InventoryImportConstants.FieldName.PRODUCT_NAME, "商品名称");
    fieldMapping.put(InventoryImportConstants.FieldName.PRODUCT_BRAND, "品牌");
    fieldMapping.put(InventoryImportConstants.FieldName.PRODUCT_SPEC, "规格");
    fieldMapping.put(InventoryImportConstants.FieldName.PRODUCT_MODEL, "型号");
    fieldMapping.put(InventoryImportConstants.FieldName.VEHICLE_BRAND, "车辆品牌");
    fieldMapping.put(InventoryImportConstants.FieldName.VEHICLE_MODEL, "车型");
    fieldMapping.put(InventoryImportConstants.FieldName.VEHICLE_YEAR, "车辆年代");
    fieldMapping.put(InventoryImportConstants.FieldName.INVENTORY_AMOUNT, "库存数量");
    fieldMapping.put(InventoryImportConstants.FieldName.PURCHASE_PRICE, "入库价");
    fieldMapping.put(InventoryImportConstants.FieldName.SALE_PRICE, "销售价");
    return fieldMapping;
  }

  @Test
  public void testImportInventory() throws Exception {
    InventoryImporter inventoryImporter = ServiceManager.getService(InventoryImporter.class);
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    RFITxnService rfTxnService = ServiceManager.getService(RFITxnService.class);
    IProductService productService = ServiceManager.getService(IProductService.class);
    ISearchService searchService = ServiceManager.getService(ISearchService.class);
    Long shopId = createShop();
    ImportResult importResult = null;
    CheckResult checkResult = null;
    List<Map<String, Object>> dataList = null;

    //场景1：无数据
    ImportContext importContext = new ImportContext();
    importContext.setShopId(shopId);
    checkResult = inventoryImporter.checkData(importContext);
    Assert.assertEquals(false, checkResult.isPass());
    Assert.assertEquals(ExcelImportConstants.CheckResultMessage.EMPTY_DATA_CONTENT, checkResult.getMessage());

    //场景2.有数据，但没有字段映射
    importContext = new ImportContext();
    importContext.setShopId(shopId);
    dataList = generateImportData();
    importContext.setDataList(dataList);
    checkResult = inventoryImporter.checkData(importContext);
    Assert.assertEquals(false, checkResult.isPass());
    Assert.assertEquals(ExcelImportConstants.CheckResultMessage.EMPTY_FIELD_MAPPING, checkResult.getMessage());

    //场景3：有数据，有字段映射
    //验证通用库产品表、本店产品表、库存表、库存价格表
    importContext = new ImportContext();
    importContext.setShopId(shopId);
    dataList = generateImportData();
    importContext.setDataList(dataList);
    importContext.setFieldMapping(generateFieldMapping());
    importResult = inventoryImporter.importData(importContext);
    Assert.assertEquals(true, importResult.isSuccess());
    Assert.assertEquals((Object) 10, importResult.getTotalCount());
    Assert.assertEquals((Object) 10, importResult.getSuccessCount());
    Assert.assertEquals((Object) 0, importResult.getFailCount());
    Assert.assertEquals(StringUtil.EMPTY_STRING, importResult.getMessage());
    List<ProductDTO> productDTOList = productService.getProducts(shopId, 0l, 100);
    Assert.assertNotNull(productDTOList);
    Assert.assertEquals(10, productDTOList.size());
    ProductLocalInfoDTO productLocalInfoDTO = null;
    PurchasePriceDTO purchasePriceDTO = null;
    InventoryDTO inventoryDTO = null;
    for(ProductDTO productDTO : productDTOList){
      Assert.assertNotNull(productDTO);
      Assert.assertEquals(productDTO.getName(), "00819寸雨刮");
      Assert.assertEquals(productDTO.getBrand(), "品牌1");
      Assert.assertEquals(productDTO.getSpec(), "规格1");
      Assert.assertEquals(productDTO.getModel(), "型号1");
      Assert.assertEquals(productDTO.getProductVehicleBrand(), "奥迪");
      Assert.assertEquals(productDTO.getProductVehicleModel(), "A6");
      Assert.assertEquals(productDTO.getProductVehicleYear(), "2009");
      Assert.assertEquals(productDTO.getProductVehicleStatus(), SearchConstant.PRODUCT_PRODUCTSTATUS_ALL);
      Assert.assertEquals(productDTO.getCheckStatus().intValue(), ProductConstants.CheckStatus.CHECK_STATUS_NEW);
      productLocalInfoDTO = productService.getProductLocalInfoByProductId(productDTO.getId(), shopId);
      Assert.assertNotNull(productLocalInfoDTO);
      Assert.assertEquals(110.0, productLocalInfoDTO.getPurchasePrice());
      purchasePriceDTO = rfTxnService.getLatestPurchasePriceByShopIdAndProductId(shopId, productLocalInfoDTO.getId());
      Assert.assertNotNull(purchasePriceDTO);
      Assert.assertEquals(110.0, purchasePriceDTO.getPrice());
      inventoryDTO = txnService.getInventoryByShopIdAndProductId(shopId, productLocalInfoDTO.getId());
      Assert.assertNotNull(inventoryDTO);
      Assert.assertEquals(10.0, inventoryDTO.getAmount());

    }
    //验证search表
    InventorySearchIndex inventorySearchIndex = null;
    List inventorySearchIndexList = searchService.getInventorySearchIndexByShopId(shopId, 0L, 100);
    Assert.assertNotNull(inventorySearchIndexList);
    Assert.assertEquals(10, inventorySearchIndexList.size());
    for(Object obj : inventorySearchIndexList){
      inventorySearchIndex = (InventorySearchIndex) obj;
      Assert.assertNotNull(inventorySearchIndex);
      Assert.assertEquals("00819寸雨刮", inventorySearchIndex.getProductName());
      Assert.assertEquals("品牌1", inventorySearchIndex.getProductBrand());
      Assert.assertEquals("规格1", inventorySearchIndex.getProductSpec());
      Assert.assertEquals("型号1", inventorySearchIndex.getProductModel());
      Assert.assertEquals("奥迪", inventorySearchIndex.getBrand());
      Assert.assertEquals("A6", inventorySearchIndex.getModel());
      Assert.assertEquals("2009", inventorySearchIndex.getYear());
      Assert.assertEquals(SearchConstant.PRODUCT_PRODUCTSTATUS_ALL, inventorySearchIndex.getProductVehicleStatus());
      Assert.assertEquals(110.0, inventorySearchIndex.getPurchasePrice());
      Assert.assertEquals(10.0, inventorySearchIndex.getAmount());
    }

  }

}
