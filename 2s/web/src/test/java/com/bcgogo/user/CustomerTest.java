package com.bcgogo.user;

import com.bcgogo.AbstractTest;
import com.bcgogo.customer.CustomerController;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.TxnController;
import com.bcgogo.user.dto.*;
import com.bcgogo.user.model.Customer;
import com.bcgogo.user.model.CustomerVehicle;
import com.bcgogo.user.model.UserWriter;
import com.bcgogo.user.service.IUserService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.ui.ModelMap;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: zyj
 * Date: 12-3-13
 * Time: 下午2:54
 * To change this template use File | Settings | File Templates.
 */
public class CustomerTest extends AbstractTest {
  @Before
  public void setUp() throws Exception {
    this.request = new MockHttpServletRequest();
    this.txnController = new TxnController();
    this.customerController = new CustomerController();
    this.modelMap = new ModelMap();
  }

  @Test
  public void testClientInfo() throws Exception {
    ModelMap model = new ModelMap();
    Long shopId = createShop();
    request.getSession().setAttribute("shopId", shopId);
    CustomerRecordDTO customerRecordDTO = new CustomerRecordDTO();
    CustomerDTO customer = new CustomerDTO();
    CustomerVehicleDTO customerVehicleDTO = new CustomerVehicleDTO();
    VehicleDTO vehicleDTO = new VehicleDTO();
    createCustomerDTO(customer, shopId);
    createCustomerRecordDTO(customerRecordDTO, shopId);
    createVehicleDTO(vehicleDTO, shopId);
    IUserService userService = ServiceManager.getService(IUserService.class);
    userService.createCustomerRecord(customerRecordDTO);
    CustomerDTO customerDTO = userService.createCustomer(customer);
    VehicleDTO vehicleDTO1 = userService.createVehicle(vehicleDTO);
    userService.addVehicleToCustomer(vehicleDTO1.getId(), customerDTO.getId());
    txnController.clientInfo(model, request, "MRS.SHAO", "15151774444","phone", "2343545", "","");
    CustomerRecordDTO customerRecordDTO2 = (CustomerRecordDTO) model.get("customerRecordDTO");
    Assert.assertEquals("MRS.SHAO", customerRecordDTO2.getName());
    Assert.assertEquals("", customerRecordDTO2.getContact());
    Assert.assertEquals("15151774444", customerRecordDTO2.getMobile());
    txnController.clientInfo(model, request, "邵磊", "15151774443","", "", "小邵","");
    CustomerRecordDTO customerRecordDTO3 = (CustomerRecordDTO) model.get("customerRecordDTO");
    Assert.assertEquals("邵磊", customerRecordDTO3.getName());
    Assert.assertEquals("15151774443", customerRecordDTO3.getMobile());
  }
//  @Test
//  public void testPrintDebtArrears()throws Exception{
//    ModelMap model=new ModelMap();
//    ITxnService txnService = ServiceManager.getService(ITxnService.class);
//    IUserService userService = ServiceManager.getService(IUserService.class);
//    Long shopId=createShop();
//
//    CustomerDTO customer = new CustomerDTO();
//    createCustomerDTO(customer, shopId);
//    userService.createCustomer(customer);
//    ProductDTO productDTO=createProductDTO("轮胎", "米其林", "TR", "", "TR", "TR", "2008", "0L");
//    SalesOrderDTO salesOrderDTO=createSalesOrderDTO(productDTO, 10, 100, 5);
//    SalesOrderDTO salesOrderDTO1=txnService.createOrUpdateSalesOrder(salesOrderDTO, "2012-3-20");
//    request.getSession().setAttribute("shopId",shopId);
//     String orderId=","+salesOrderDTO1.getId();
//    request.setParameter("customerId", "2343545");
//    request.setParameter("totalAmount", "10");
//    request.setParameter("payedAmount","5");
//    request.setParameter("orderId",orderId);
//    txnController.printDebtArrears(model, request);
//
//
//
//
//  }


  public CustomerRecordDTO createCustomerRecordDTO(CustomerRecordDTO customerRecordDTO, Long shopId) {
    customerRecordDTO.setShopId(shopId);
    customerRecordDTO.setName("MRS.SHAO");
    customerRecordDTO.setCustomerId(2343545L);
    customerRecordDTO.setArea("苏州");
    customerRecordDTO.setBankAccountName("test");
    customerRecordDTO.setShortName("test");
    customerRecordDTO.setCustomerKind("s");
    customerRecordDTO.setMobile("15151774444");
    customerRecordDTO.setAddress("shanghai");
    customerRecordDTO.setContact("MR.SHAO");
    customerRecordDTO.setPhone("0512-0808");
    customerRecordDTO.setFax("test");
    customerRecordDTO.setBank("test");
    customerRecordDTO.setQq("test");
    customerRecordDTO.setAccount("test");
    customerRecordDTO.setMemberNumber("test");
    customerRecordDTO.setEmail("test");
    return customerRecordDTO;
  }

  public CustomerDTO createCustomerDTO(CustomerDTO customer, Long shopId) {
    customer.setName("MRS.SHAO");
    customer.setShopId(shopId);
    customer.setId(2343545L);
    customer.setArea("苏州");
    customer.setBankAccountName("test");
    customer.setShortName("test");
    customer.setCustomerKind("s");
    customer.setMobile("15151774444");
    customer.setAddress("shanghai");
    customer.setContact("MR.SHAO");
    customer.setFax("test");
    customer.setBank("test");
    customer.setQq("test");
    customer.setAccount("test");
    customer.setEmail("test");
    ContactDTO[] contactDTOs = new ContactDTO[3];
    ContactDTO contactDTO = new ContactDTO();
    contactDTO.setIsMainContact(1);
    contactDTO.setMobile(customer.getMobile());
    contactDTO.setDisabled(1);
    contactDTO.setName(customer.getContact());
    contactDTO.setEmail(customer.getEmail());
    contactDTO.setQq(customer.getQq());
    contactDTO.setLevel(0);
    contactDTO.setCustomerId(customer.getId());
    contactDTOs[0] = contactDTO;
    customer.setContacts(contactDTOs);
    return customer;
  }

  public VehicleDTO createVehicleDTO(VehicleDTO vehicleDTO, Long shopId) {
    vehicleDTO.setShopId(shopId);
    vehicleDTO.setLicenceNo("苏A00098");
    vehicleDTO.setBrand("奥迪");
    vehicleDTO.setModel("A6");
    vehicleDTO.setYear("2012");
    return vehicleDTO;
  }

  @Test
  public void testUpdateCustomerMobile() throws Exception {
    ModelMap model = new ModelMap();
    Long shopId = createShop();
    CustomerDTO customer = new CustomerDTO();
    customer = createCustomerDTO(customer, shopId);
    customer.setId(123456L);
    customer = userService.createCustomer(customer);
    request.getSession().setAttribute("shopId", shopId);
    request.setParameter("customerId", customer.getId().toString());
    request.setParameter("mobile", "15851654173");
    customerController.updateMobile(request, response);

    CustomerDTO customerNew = customerService.getCustomerById(customer.getId());
    Assert.assertEquals("15851654173", customerNew.getMobile());

    CustomerRecordDTO customerRecordDTO = new CustomerRecordDTO();
    customerRecordDTO = createCustomerRecordDTO(customerRecordDTO, shopId);
    customerRecordDTO.setCustomerId(customer.getId());
    customerRecordDTO = userService.createCustomerRecord(customerRecordDTO);
    request.getSession().setAttribute("shopId", shopId);
    request.setParameter("customerId", customer.getId().toString());
    request.setParameter("mobile", "15851654173");
    customerController.updateMobile(request, response);
    CustomerDTO customerDTO = userService.getCustomerDTOByCustomerId(customer.getId(), shopId);
    Assert.assertEquals("15851654173", customerDTO.getMobile());
  }

  @Test
  public void testUpdateSupplierMobile() throws Exception {
    ModelMap model = new ModelMap();
    Long shopId = createShop();
    SupplierDTO supplierDTO = new SupplierDTO();
    supplierDTO = createSupplierDTO(supplierDTO, shopId);
    supplierDTO = userService.createSupplier(supplierDTO);
    request.getSession().setAttribute("shopId", shopId);
    request.setParameter("supplierId", supplierDTO.getId().toString());
    request.setParameter("mobile", "15851654173");
    customerController.updateMobile(request, response);
    SupplierDTO supplierDTONew = userService.getSupplierById(supplierDTO.getId());
    Assert.assertEquals("15851654173", supplierDTONew.getMobile());
  }

    public SupplierDTO createSupplierDTO(SupplierDTO supplierDTO, Long shopId) {
    supplierDTO.setName("zhangjuntao");
    supplierDTO.setShopId(shopId);
    supplierDTO.setMobile("15862936076");
    supplierDTO.setAddress("苏州");
    ContactDTO[] contactDTOs = new ContactDTO[3];
    ContactDTO contactDTO = new ContactDTO();
    contactDTO.setMobile(supplierDTO.getMobile());
    contactDTO.setIsMainContact(1);
    contactDTO.setDisabled(1);
    contactDTOs[0] = contactDTO;
    supplierDTO.setContacts(contactDTOs);
    return supplierDTO;
  }

  /**
   * 客户管理-客户资料
   * @author zhangchuanlong
   *
   * @throws Exception
   */
  @Test
  public void testcustomerData() throws Exception{
    ModelMap model = new ModelMap();
    Long shopId = createShop();
    request.getSession().setAttribute("shopId", shopId);
    //创建100个客户和100条记录
    createCustomerAndCustomerRecordDTO(shopId);
    request.getSession().setAttribute("shopId", shopId);
    request.setParameter("timeType", "today");
    request.setParameter("orderByName", "name");
    request.setParameter("orderByType", "asc");
    /*设置第5页 */
    request.setParameter("pageNo", "5");
    /*新增客户数*/
    Assert.assertEquals(100, userService.countShopCustomerRecord(shopId));
    /*本月新增客户记录数*/
    Assert.assertEquals("100", customerService.getCountOfCustomerByMonth(shopId).toString());
    /*当天新增客户记录数*/
    Assert.assertEquals("100", customerService.getCountOfCustomerByToay(shopId).toString());
    /*昨天新增客户记录数*/
    Assert.assertEquals("0", customerService.getCountOfCustomerByYesterDay(shopId).toString());
    String url = customerController.customerData(model, request,response);
    Assert.assertEquals("/customer/myCustomer", url);
  }

  /**
   * 创建100个客户,客户记录 用于测试
   *
   * @param shopId
   * @throws BcgogoException
   */
  private void createCustomerAndCustomerRecordDTO( Long shopId) throws BcgogoException {
    for (int i = 0; i < 100; i++) {
      CustomerRecordDTO customerRecordDTO = new CustomerRecordDTO();
      CustomerDTO customerDTO = new CustomerDTO();
      customerDTO.setName("MRS.SHAO" + i);
      customerDTO.setShopId(shopId);
      customerDTO.setArea("苏州" + i);
      customerDTO.setBankAccountName("test" + i);
      customerDTO.setShortName("test" + i);
      customerDTO.setCustomerKind("s" + i);
      customerDTO.setMobile("15151774444" + i);
      customerDTO.setAddress("shanghai" + i);
      customerDTO.setContact("MR.SHAO" + i);
      customerDTO.setFax("test" + i);
      customerDTO.setBank("test" + i);
      customerDTO.setQq("test" + i);
      customerDTO.setAccount("test" + i);
      customerDTO.setEmail("test" + i);

      customerRecordDTO.setShopId(shopId);
      customerRecordDTO.setName(customerDTO.getName());
      customerRecordDTO.setCustomerId(customerDTO.getId());
      customerRecordDTO.setArea(customerDTO.getArea());
      customerRecordDTO.setBankAccountName(customerDTO.getBankAccountName());
      customerRecordDTO.setShortName(customerDTO.getShortName());
      customerRecordDTO.setCustomerKind(customerDTO.getCustomerKind());
      customerRecordDTO.setMobile(customerDTO.getMobile());
      customerRecordDTO.setAddress(customerDTO.getAddress());
      customerRecordDTO.setContact(customerDTO.getContact());
      customerRecordDTO.setPhone(customerDTO.getMobile());
      customerRecordDTO.setFax(customerDTO.getFax());
      customerRecordDTO.setBank(customerDTO.getBank());
      customerRecordDTO.setQq(customerDTO.getQq());
      customerRecordDTO.setAccount(customerDTO.getAccount());
      customerRecordDTO.setMemberNumber(customerDTO.getMemberNumber());
      customerRecordDTO.setEmail(customerDTO.getEmail());
      userService.createCustomerRecord(customerRecordDTO);
      userService.createCustomer(customerDTO);
    }
  }

  @Test
  public void testDeleteCustomer()  throws Exception
  {
    Long shopId = createShop();
    CustomerRecordDTO customerRecordDTO = new CustomerRecordDTO();
    CustomerDTO customerDTO = new CustomerDTO();
    customerDTO.setName("cfl");
    customerDTO.setShopId(shopId);
    customerDTO.setArea("苏州");
    customerDTO.setBankAccountName("test");
    customerDTO.setShortName("test");
    customerDTO.setCustomerKind("s");
    customerDTO.setMobile("15151774444");
    customerDTO.setAddress("shanghai");
    customerDTO.setContact("MR.SHAO");
    customerDTO.setFax("test");
    customerDTO.setBank("test");
    customerDTO.setQq("test");
    customerDTO.setAccount("test");
    customerDTO.setEmail("test");
    customerDTO.setBirthday(222L);
    customerDTO.setLandLine("44444444");
    ContactDTO[] contactDTOs = new ContactDTO[3];
    ContactDTO contactDTO = new ContactDTO();
    contactDTO.setCustomerId(customerDTO.getId());
    contactDTO.setMobile("15151774444");
    contactDTO.setIsMainContact(1);
    contactDTO.setShopId(shopId);
    contactDTO.setLevel(0);
    contactDTOs[0] = contactDTO;
    customerDTO.setContacts(contactDTOs);
    customerDTO = userService.createCustomer(customerDTO);

    customerRecordDTO.setShopId(shopId);
    customerRecordDTO.setName(customerDTO.getName());
    customerRecordDTO.setCustomerId(customerDTO.getId());
    customerRecordDTO.setArea(customerDTO.getArea());
    customerRecordDTO.setBankAccountName(customerDTO.getBankAccountName());
    customerRecordDTO.setShortName(customerDTO.getShortName());
    customerRecordDTO.setCustomerKind(customerDTO.getCustomerKind());
    customerRecordDTO.setMobile(customerDTO.getMobile());
    customerRecordDTO.setAddress(customerDTO.getAddress());
    customerRecordDTO.setContact(customerDTO.getContact());
    customerRecordDTO.setPhone(customerDTO.getMobile());
    customerRecordDTO.setFax(customerDTO.getFax());
    customerRecordDTO.setBank(customerDTO.getBank());
    customerRecordDTO.setQq(customerDTO.getQq());
    customerRecordDTO.setAccount(customerDTO.getAccount());
    customerRecordDTO.setMemberNumber(customerDTO.getMemberNumber());
    customerRecordDTO.setEmail(customerDTO.getEmail());
    customerRecordDTO = userService.createCustomerRecord(customerRecordDTO);


    VehicleDTO vehicleDTO = new VehicleDTO();
    vehicleDTO.setLicenceNo("苏U66666");
    vehicleDTO.setShopId(shopId);

    vehicleDTO = userService.createVehicle(vehicleDTO);

    CustomerVehicle customerVehicle = new CustomerVehicle();
    customerVehicle.setCustomerId(customerDTO.getId());
    customerVehicle.setVehicleId(vehicleDTO.getId());

    UserWriter writer = userDaoManager.getWriter();

    Object status = writer.begin();

    try{
      writer.save(customerVehicle);
      writer.commit(status);
    }finally {
      writer.rollback(status);
    }
    Customer customer = userService.getCustomerByCustomerId(customerDTO.getId(),shopId);

    Assert.assertNotNull(customer);
    List<CustomerDTO> customerDTOList = userService.getCustomerByBirth(customerDTO.getBirthday());
    Assert.assertEquals(1,customerDTOList.size());

    customerDTOList = userService.getCustomerByLicenceNo(shopId,vehicleDTO.getLicenceNo());

    Assert.assertEquals(1,customerDTOList.size());

    customerDTOList = userService.getCustomerByMobile(shopId,customerDTO.getMobile());
    Assert.assertEquals(1,customerDTOList.size());

    customerDTOList = userService.getCustomerByName(shopId,customerDTO.getName());

    Assert.assertEquals(1,customerDTOList.size());

    customerDTOList = userService.getCustomerByTelephone(shopId,customerDTO.getLandLine());
    Assert.assertEquals(1,customerDTOList.size());

    List<CustomerRecordDTO> customerRecordDTOList = userService.getCustomerRecordByName(customerDTO.getName());
    Assert.assertEquals(1,customerRecordDTOList.size());

    List<VehicleDTO> vehicleDTOList = userService.getVehicleByLicenceNo(shopId,vehicleDTO.getLicenceNo());
    Assert.assertEquals(1,vehicleDTOList.size());

    vehicleDTOList = userService.getVehicleByMobile(shopId,customerDTO.getMobile());
    Assert.assertEquals(1,vehicleDTOList.size());

    request.setParameter("customerId",customerDTO.getId().toString());
    customerController.deleteCustomer(modelMap,request,response);

    customer = userService.getCustomerByCustomerId(customerDTO.getId(),shopId);

    Assert.assertNotNull(customer);
    customerDTOList = userService.getCustomerByBirth(customerDTO.getBirthday());
    Assert.assertEquals(0,customerDTOList.size());

    customerDTOList = userService.getCustomerByLicenceNo(shopId,vehicleDTO.getLicenceNo());

    Assert.assertEquals(0,customerDTOList.size());

    customerDTOList = userService.getCustomerByMobile(shopId,customerDTO.getMobile());
    Assert.assertEquals(0,customerDTOList.size());

    customerDTOList = userService.getCustomerByName(shopId,customerDTO.getName());

    Assert.assertNull(customerDTOList);

    customerDTOList = userService.getCustomerByTelephone(shopId,customerDTO.getLandLine());
    Assert.assertEquals(0,customerDTOList.size());

    customerRecordDTOList = userService.getCustomerRecordByName(customerDTO.getName());
    Assert.assertEquals(0,customerRecordDTOList.size());

    vehicleDTOList = userService.getVehicleByLicenceNo(shopId,vehicleDTO.getLicenceNo());
    Assert.assertEquals(0,vehicleDTOList.size());

    vehicleDTOList = userService.getVehicleByMobile(shopId,customerDTO.getMobile());
    Assert.assertEquals(0,vehicleDTOList.size());
  }

}
