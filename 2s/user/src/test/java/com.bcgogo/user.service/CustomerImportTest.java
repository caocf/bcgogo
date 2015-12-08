package com.bcgogo.user.service;

import com.bcgogo.AbstractTest;
import com.bcgogo.config.dto.ImportResult;
import com.bcgogo.config.service.excelimport.CheckResult;
import com.bcgogo.config.service.excelimport.ExcelImportConstants;
import com.bcgogo.config.service.excelimport.ImportContext;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.dto.CustomerDTO;
import com.bcgogo.user.dto.CustomerRecordDTO;
import com.bcgogo.user.dto.CustomerVehicleDTO;
import com.bcgogo.user.dto.VehicleDTO;
import com.bcgogo.user.service.excelimport.customer.CustomerImportConstants;
import com.bcgogo.user.service.excelimport.customer.CustomerImporter;
import com.bcgogo.utils.StringUtil;
import junit.framework.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 客户导入功能单元测试
 * Created by IntelliJ IDEA.
 * User: ZouJianhong
 * Date: 12-4-16
 * Time: 下午4:02
 * To change this template use File | Settings | File Templates.
 */
public class CustomerImportTest extends AbstractTest {

  /**
   * 构造导入的数据
   * @return
   */
  private List<Map<String, Object>> generateImportData(){
    List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
    Map<String, Object> data = null;
    for(int i = 0; i < 10; i ++){
      data = new HashMap<String, Object>();
      data.put("客户名","测试客户");
      data.put("手机", "15678903456");
      data.put("车牌号", "苏E23455");
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
    fieldMapping.put(CustomerImportConstants.FieldName.NAME, "客户名");
    fieldMapping.put(CustomerImportConstants.FieldName.MOBILE, "手机");
    fieldMapping.put(CustomerImportConstants.FieldName.VEHICLE_LICENCE_NO, "车牌号");
    return fieldMapping;
  }

  @Test
  public void testImportCustomer() throws Exception {
    IUserService userService = ServiceManager.getService(IUserService.class);
    ImportResult importResult = null;
    CheckResult checkResult = null;
    List<Map<String, Object>> dataList = null;
    Long shopId = createShop();

    //场景1：无数据
    ImportContext importContext = new ImportContext();
    importContext.setShopId(shopId);
    checkResult = customerImporter.checkData(importContext);
    Assert.assertEquals(false, checkResult.isPass());
    Assert.assertEquals(ExcelImportConstants.CheckResultMessage.EMPTY_DATA_CONTENT, checkResult.getMessage());

    //2.有数据，但没有字段映射
    importContext = new ImportContext();
    importContext.setShopId(shopId);
    dataList = generateImportData();
    importContext.setDataList(dataList);
    checkResult = customerImporter.checkData(importContext);
    Assert.assertEquals(false, checkResult.isPass());
    Assert.assertEquals(ExcelImportConstants.CheckResultMessage.EMPTY_FIELD_MAPPING, checkResult.getMessage());


    //场景3：有数据，有字段映射
    importContext = new ImportContext();
    importContext.setShopId(shopId);
    dataList = generateImportData();
    importContext.setDataList(dataList);
    importContext.setFieldMapping(generateFieldMapping());
    importResult = customerImporter.importData(importContext);
    Assert.assertEquals(true, importResult.isSuccess());
    Assert.assertEquals((Object) 10, importResult.getTotalCount());
    Assert.assertEquals((Object) 10, importResult.getSuccessCount());
    Assert.assertEquals((Object) 0, importResult.getFailCount());
    Assert.assertEquals(StringUtil.EMPTY_STRING, importResult.getMessage());
    List<CustomerDTO> customerDTOList = userService.getCustomerByName(shopId, "测试客户");
    Assert.assertEquals(10, customerDTOList.size());
    CustomerRecordDTO customerRecordDTO = null;
    CustomerVehicleDTO customerVehicleDTO = null;
    VehicleDTO vehicleDTO = null;
    for(CustomerDTO customerDTO : customerDTOList){
      Assert.assertEquals("测试客户", customerDTO.getName());
      Assert.assertEquals("15678903456", customerDTO.getMobile());
      customerRecordDTO = userService.getCustomerRecordByCustomerId(customerDTO.getId()).get(0);
      Assert.assertNotNull(customerRecordDTO);
      Assert.assertEquals("测试客户", customerRecordDTO.getName());
      Assert.assertEquals("15678903456", customerRecordDTO.getMobile());
      customerVehicleDTO = userService.getVehicleByCustomerId(customerDTO.getId()).get(0);
      Assert.assertNotNull(customerVehicleDTO);
      vehicleDTO = userService.getVehicleById(customerVehicleDTO.getVehicleId());
      Assert.assertNotNull(vehicleDTO);
      Assert.assertEquals("苏E23455", vehicleDTO.getLicenceNo());
    }

  }

  @Autowired
  private CustomerImporter customerImporter = ServiceManager.getService(CustomerImporter.class);

}
