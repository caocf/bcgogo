package com.bcgogo.user.service;

import com.bcgogo.AbstractTest;
import com.bcgogo.config.dto.ImportResult;
import com.bcgogo.config.service.excelimport.CheckResult;
import com.bcgogo.config.service.excelimport.ExcelImportConstants;
import com.bcgogo.config.service.excelimport.ImportContext;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.stat.dto.SupplierRecordDTO;
import com.bcgogo.user.dto.SupplierDTO;
import com.bcgogo.user.service.excelimport.supplier.SupplierImportConstants;
import com.bcgogo.user.service.excelimport.supplier.SupplierImporter;
import com.bcgogo.utils.StringUtil;
import junit.framework.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 供应商导入功能单元测试
 * Created by IntelliJ IDEA.
 * User: ZouJianhong
 * Date: 12-4-19
 * Time: 上午7:50
 * To change this template use File | Settings | File Templates.
 */
public class SupplierImportTest extends AbstractTest {

  /**
   * 构造导入的数据
   * @return
   */
  private List<Map<String, Object>> generateImportData(){
    List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
    Map<String, Object> data = null;
    for(int i = 0; i < 10; i ++){
      data = new HashMap<String, Object>();
      data.put("供应商名","测试供应商");
      data.put("手机", "15678903456");
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
    fieldMapping.put(SupplierImportConstants.FieldName.NAME, "供应商名");
    fieldMapping.put(SupplierImportConstants.FieldName.MOBILE, "手机");
    return fieldMapping;
  }

  @Test
  public void testImportSupplier() throws Exception {
    IUserService userService = ServiceManager.getService(IUserService.class);
    ImportResult importResult = null;
    CheckResult checkResult = null;
    List<Map<String, Object>> dataList = null;
    Long shopId = createShop();

    //场景1：无数据
    ImportContext importContext = new ImportContext();
    importContext.setShopId(shopId);
    checkResult = supplierImporter.checkData(importContext);
    Assert.assertEquals(false, checkResult.isPass());
    Assert.assertEquals(ExcelImportConstants.CheckResultMessage.EMPTY_DATA_CONTENT, checkResult.getMessage());

    //2.有数据，但没有字段映射
    importContext = new ImportContext();
    importContext.setShopId(shopId);
    dataList = generateImportData();
    importContext.setDataList(dataList);
    checkResult = supplierImporter.checkData(importContext);
    Assert.assertEquals(false, checkResult.isPass());
    Assert.assertEquals(ExcelImportConstants.CheckResultMessage.EMPTY_FIELD_MAPPING, checkResult.getMessage());

    //场景3：有数据，有字段映射
    importContext = new ImportContext();
    importContext.setShopId(shopId);
    dataList = generateImportData();
    importContext.setDataList(dataList);
    importContext.setFieldMapping(generateFieldMapping());
    Map<String,SupplierRecordDTO> map = new HashMap<String, SupplierRecordDTO>();
    importResult = supplierImporter.importData(map,importContext);
    Assert.assertEquals(true, importResult.isSuccess());
    Assert.assertEquals((Object) 10, importResult.getTotalCount());
    Assert.assertEquals((Object) 10, importResult.getSuccessCount());
    Assert.assertEquals((Object) 0, importResult.getFailCount());
    Assert.assertEquals(StringUtil.EMPTY_STRING, importResult.getMessage());
    List<SupplierDTO> supplierDTOList = userService.getSupplierByName(shopId, "测试供应商");
    Assert.assertEquals(10, supplierDTOList.size());
    for(SupplierDTO supplierDTO : supplierDTOList){
      Assert.assertEquals("测试供应商", supplierDTO.getName());
      Assert.assertEquals("15678903456", supplierDTO.getMobile());
    }

  }

  @Autowired
  private SupplierImporter supplierImporter = ServiceManager.getService(SupplierImporter.class);

}
