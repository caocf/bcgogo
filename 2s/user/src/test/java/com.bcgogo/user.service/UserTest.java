package com.bcgogo.user.service;

import com.bcgogo.AbstractTest;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.dto.*;
import com.bcgogo.user.model.CustomerVehicle;
import com.bcgogo.utils.DateUtil;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: XJ
 * Date: 9/20/11
 * Time: 4:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class UserTest extends AbstractTest {

  @Before
  public void setUpTest() throws Exception {

  }

  @Test
  public void testGetSmsCustomerInfoList() throws Exception {
    IUserService userService = ServiceManager.getService(IUserService.class);
    Long shopId = this.createShop();
    CustomerDTO customerDTO = this.createCustomerDTO(shopId);
    CustomerRecordDTO customerRecordDTO = this.createCustomerRecordDTO(shopId);
    CustomerCardDTO customerCardDTO = this.createCustomerCardDTO(shopId);
    userService.createCustomer(customerDTO);
    userService.createCustomerRecord(customerRecordDTO);
    userService.createCustomerCard(customerCardDTO);
    List<CustomerRecordDTO> customerRecordDTOList = userService.getSmsCustomerInfoList(shopId, 1, 1);
    if (!customerRecordDTOList.isEmpty()) {
      Assert.assertEquals("苏州统购", customerRecordDTOList.get(0).getName());
      Assert.assertEquals(new Long(123456), customerRecordDTOList.get(0).getCustomerId());
      Assert.assertEquals(new Long(1), customerRecordDTOList.get(0).getWashRemain());
      Assert.assertEquals("2012-01-01", customerRecordDTOList.get(0).getBirthdayStr());
    }
  }

  @Test
  public void testSaveVehicleAppointment() throws Exception {
    IUserService userService = ServiceManager.getService(IUserService.class);
    ICustomerService customerService = ServiceManager.getService(ICustomerService.class);
    Long shopId = createShop();
    CustomerDTO customerDTO = createCustomerDTO(shopId);
    VehicleDTO vehicleDTO = createVehicleDTO(shopId);
    customerDTO.setId(null);
    Long customerId = userService.createCustomer(customerDTO).getId();
    Long vehicleId = userService.createVehicle(vehicleDTO).getId();
    userService.addVehicleToCustomer(vehicleId, customerId);

    Long maintainTime = DateUtil.convertDateStringToDateLong("yyyy-MM-dd", "2012-03-24");
    Long insureTime = DateUtil.convertDateStringToDateLong("yyyy-MM-dd", "2012-04-12");
    Long examineTime = DateUtil.convertDateStringToDateLong("yyyy-MM-dd", "2012-05-15");

    customerService.saveVehicleAppointment(customerId, vehicleId, maintainTime, insureTime, examineTime);
    CustomerVehicle customerVehicle = customerService.getVehicleAppointment(customerId, vehicleId);
    Assert.assertEquals(maintainTime, customerVehicle.getMaintainTime());
    Assert.assertEquals(insureTime, customerVehicle.getInsureTime());
    Assert.assertEquals(examineTime, customerVehicle.getExamineTime());

  }
    
  @Test
  public void addYuyueToCustomerVehicle() throws Exception{
      IUserService userService = ServiceManager.getService(IUserService.class);
      ICustomerService customerService = ServiceManager.getService(ICustomerService.class);
      Long shopId = createShop();
      CustomerDTO customerDTO = new CustomerDTO();
      customerDTO.setShopId(shopId);
      customerDTO.setName("苏州统购");
      customerDTO.setMobile("15151771582");
      customerDTO.setContact("邵磊");
      customerDTO.setBirthday(DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_DAY, "2012-01-01"));
      Long customerId = userService.createCustomer(customerDTO).getId();
      VehicleDTO vehicleDTO = createVehicleDTO(shopId);
      Long vehicleId = userService.createVehicle(vehicleDTO).getId();
      userService.addVehicleToCustomer(vehicleId,customerId);
         AppointServiceDTO appointServiceDTO=new AppointServiceDTO();
      appointServiceDTO.setShopId(shopId);
      appointServiceDTO.setCustomerId(String.valueOf(customerId));
      appointServiceDTO.setVehicleId(String.valueOf(vehicleId));
      appointServiceDTO.setMaintainTimeStr("2012-01-01");
      appointServiceDTO.setInsureTimeStr("2013-01-01");
      appointServiceDTO.setExamineTimeStr("2012-01-04");
      userService.addYuyueToCustomerVehicle(appointServiceDTO);
      CustomerVehicle customerVehicle=customerService.getVehicleAppointment(customerId,vehicleId);
      Assert.assertEquals(DateUtil.convertDateStringToDateLong("yyyy-MM-dd","2012-01-01"),customerVehicle.getMaintainTime());
      Assert.assertEquals(DateUtil.convertDateStringToDateLong("yyyy-MM-dd","2013-01-01"),customerVehicle.getInsureTime());
      Assert.assertEquals(DateUtil.convertDateStringToDateLong("yyyy-MM-dd","2012-01-04"),customerVehicle.getExamineTime());
  }


}
