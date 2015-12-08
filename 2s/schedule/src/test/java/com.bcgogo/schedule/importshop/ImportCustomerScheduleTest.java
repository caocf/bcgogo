package com.bcgogo.schedule.importshop;

import com.bcgogo.AbstractTest;
import com.bcgogo.schedule.bean.ImportCustomerSchedule;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.dto.CustomerDTO;
import com.bcgogo.user.dto.CustomerVehicleDTO;
import com.bcgogo.user.dto.SupplierDTO;
import com.bcgogo.user.dto.VehicleDTO;
import com.bcgogo.user.service.IContactService;
import com.bcgogo.user.service.ICustomerService;
import com.bcgogo.user.service.IUserService;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * shop数据临时导入功能单元测试
 * Created by IntelliJ IDEA.
 * User: ZouJianhong
 * Date: 12-4-5
 * Time: 上午11:43
 * To change this template use File | Settings | File Templates.
 */
public class ImportCustomerScheduleTest extends AbstractTest {
  @Before
  public void setUp() throws Exception{
    System.setProperty("solr.solr.home", "../search/src/test/resources/solr");
  }


    private ImportCustomerSchedule importCustomerSchedule;

    /**
     * 客户导入单元测试
     * @throws Exception
     */
    @Test
    public void testImportCustomer() throws Exception {
        IUserService userService = ServiceManager.getService(IUserService.class);
        this.importCustomerSchedule = new ImportCustomerSchedule();
        Long shopId = createShop();
        List<String[]> dataList = new ArrayList<String[]>();
        for (int i = 0; i < 10; i++) {
            String[] data = new String[17];
            data[0] = "苏E21X35";
            data[1] = "邹建宏";
            data[2] = "2029@bcgogo.com";
            data[3] = "15968891272";
            data[5] = "苏州工业园区宏业路";
            data[7] = "545698774";
            data[10] = "xxxxx";
            data[11] = "苏E21X35";
            data[12] = "奥迪";
            data[13] = "A6";
            data[14] = "1999";
            data[15] = "2.0L";
            data[16] = "" + shopId;
            dataList.add(data);
        }
        this.importCustomerSchedule.importCustomer(dataList);
        List<CustomerDTO> customerDTOList = userService.getAllCustomerByName(shopId, "苏E21X35");
        Assert.assertEquals(10, customerDTOList.size());
        CustomerDTO customerDTO = customerDTOList.get(0);
        Assert.assertEquals("苏E21X35", customerDTO.getName());
        Assert.assertEquals("邹建宏", customerDTO.getContact());
        Assert.assertEquals("2029@bcgogo.com", customerDTO.getEmail());
        Assert.assertEquals("545698774", customerDTO.getQq());
        List<CustomerVehicleDTO> customerVehicleDTOList = userService.getVehicleByCustomerId(customerDTO.getId());
        Assert.assertEquals(1, customerVehicleDTOList.size());
        CustomerVehicleDTO customerVehicleDTO = customerVehicleDTOList.get(0);
        VehicleDTO vehicleDTO = userService.getVehicleById(customerVehicleDTO.getVehicleId());
        Assert.assertEquals("苏E21X35", vehicleDTO.getLicenceNo());
        Assert.assertEquals("奥迪", vehicleDTO.getBrand());
    }

    /**
     * 供应商导入单元测试
     * @throws Exception
     */
    @Test
    public void testImportSupplier() throws Exception {
        IUserService userService = ServiceManager.getService(IUserService.class);
        this.importCustomerSchedule = new ImportCustomerSchedule();
        Long shopId = createShop();
        List<String[]> dataList = new ArrayList<String[]>();
        for (int i = 0; i < 10; i++) {
            String[] data = new String[11];
            data[0] = "苏州索伊汽车配件总汇";
            data[1] = "索伊汽车配件";
            data[2] = "13013618988";
            data[3] = "68388135";
            data[5] = "本地";
            data[9] = "北京现代，悦达起亚，江淮瑞风，伊兰特，索纳塔，雅绅特，千里马，赛拉图，普莱特";
            data[10] = "" + shopId;
            dataList.add(data);
        }
        this.importCustomerSchedule.importSupplier(dataList);
        List<SupplierDTO> supplierDTOList = userService.getSupplierByName(shopId, "苏州索伊汽车配件总汇");
        Assert.assertEquals(10, supplierDTOList.size());
        SupplierDTO supplierDTO = supplierDTOList.get(0);
        Assert.assertEquals("苏州索伊汽车配件总汇", supplierDTO.getName());
        Assert.assertEquals("索伊汽车配件", supplierDTO.getAbbr());
        Assert.assertEquals("68388135", supplierDTO.getLandLine());
    }

}
