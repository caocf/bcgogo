//package com.bcgogo.user;
//
//import com.bcgogo.AbstractTest;
//import com.bcgogo.customer.CustomerController;
//import com.bcgogo.customer.MemberController;
//import com.bcgogo.enums.MemberStatus;
//import com.bcgogo.enums.TimesStatus;
//import com.bcgogo.search.dto.ItemIndexDTO;
//import com.bcgogo.search.dto.OrderIndexDTO;
//import com.bcgogo.search.service.SearchService;
//import com.bcgogo.service.ServiceManager;
//import com.bcgogo.txn.dto.*;
//import com.bcgogo.txn.model.Service;
//import com.bcgogo.txn.service.ITxnService;
//import com.bcgogo.txn.service.RFITxnService;
//import com.bcgogo.txn.service.TxnService;
//import com.bcgogo.user.dto.*;
//import com.bcgogo.user.service.IMembersService;
//import com.bcgogo.utils.DateUtil;
//import junit.framework.Assert;
//import org.apache.commons.collections.CollectionUtils;
//import org.junit.Before;
//import org.junit.Test;
//import org.springframework.mock.web.MockHttpServletRequest;
//import org.springframework.mock.web.MockHttpServletResponse;
//import org.springframework.ui.ModelMap;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
///**
// * Created by IntelliJ IDEA.
// * 会员方面的单元测试
// * User: cfl
// * Date: 12-7-20
// * Time: 下午4:30
// * To change this template use File | Settings | File Templates.
// */
//public class MemberControllerTest extends AbstractTest {
//  @Before
//  public void setUp() throws Exception {
//    memberController = new MemberController();
//    customerController = new CustomerController();
//    request = new MockHttpServletRequest();
//    response = new MockHttpServletResponse();
//    modelMap = new ModelMap();
//  }
//
//  /**
//   * 测试进入设卡第一步页面
//   */
//  @Test
//  public void testToCardFirst() {
//    String url = memberController.toCardFirst(request, response);
//    Assert.assertEquals("/customer/cardFirst", url);
//  }
//
//  /**
//   * 测试卡名是否相同
//   */
//  @Test
//  public void testCheckCardNameByName() {
//    memberController.checkCardNameByName(request, response, "会员");
//    Assert.assertEquals("会员", (String) request.getAttribute("name"));
//  }
//
//  /**
//   * 测试进入设卡第二步页面
//   */
//  @Test
//  public void testToCardSecond() {
//    MemberCardDTO memberCardDTO = new MemberCardDTO();
//    memberCardDTO.setType("为你幼稚");
//    String url = memberController.toCardSecond(modelMap, request, response, memberCardDTO);
//    Assert.assertEquals("为你幼稚", ((MemberCardDTO) modelMap.get("memberCardDTO")).getType());
//    Assert.assertEquals("/customer/cardSecond", url);
//  }
//
//  /**
//   * 测试进入设卡第三步页面
//   */
//  @Test
//  public void testToCardThird() throws Exception {
//    MemberCardDTO memberCardDTO = new MemberCardDTO();
//
//    memberCardDTO.setId(123456L);
//
//    String url = memberController.toCardThird(modelMap, request, response, memberCardDTO);
//
//    MemberCardDTO newMemberCardDTO = (MemberCardDTO) modelMap.get("memberCardDTO");
//
//    Assert.assertEquals("/customer/cardThird", url);
//    Assert.assertEquals(memberCardDTO.getId(), newMemberCardDTO.getId());
//    Assert.assertEquals(0.0, newMemberCardDTO.getWorth());
//  }
//
//  /**
//   * 测试保存卡信息
//   */
//  @Test
//  public void testSaveCardSet() {
//    MemberCardDTO memberCardDTO = new MemberCardDTO();
//    memberCardDTO.setName("会员一号准备");
//    memberCardDTO.setShopId(111111L);
//    request.getSession().setAttribute("shopId", memberCardDTO.getShopId());
//    List<MemberCardServiceDTO> memberCardServiceDTOs = new ArrayList<MemberCardServiceDTO>();
//
//    MemberCardServiceDTO memberCardServiceDTO = new MemberCardServiceDTO();
//    memberCardServiceDTO.setServiceId(Long.valueOf(1));
//    memberCardServiceDTO.setServiceName("会员1");
//    memberCardServiceDTO.setTerm(1);
//    memberCardServiceDTO.setTimes(1);
//    memberCardServiceDTOs.add(memberCardServiceDTO);
//
//    memberCardServiceDTO = new MemberCardServiceDTO();
//    memberCardServiceDTO.setServiceId(Long.valueOf(2));
//    memberCardServiceDTO.setServiceName("会员2");
//    memberCardServiceDTO.setTerm(2);
//    memberCardServiceDTO.setTimes(2);
//    memberCardServiceDTOs.add(memberCardServiceDTO);
//    memberCardDTO.setMemberCardServiceDTOs(memberCardServiceDTOs);
//
//    String url = memberController.saveCardSet(request, response, memberCardDTO);
//
//    MemberCardDTO newMemberCardDTO = (MemberCardDTO) request.getAttribute("memberCardDTO");
//
//    Assert.assertEquals("/customer/cardSetComplete", url);
//    Assert.assertEquals(true, null == memberCardDTO.getId() ? false : true);
//    Assert.assertEquals(memberCardDTO.getName(), newMemberCardDTO.getName());
//    Assert.assertEquals(memberCardDTO.getShopId(), newMemberCardDTO.getShopId());
//
//    RFITxnService rfiTxnService = ServiceManager.getService(RFITxnService.class);
//
//    Service service1 = rfiTxnService.getRFServiceByServiceNameAndShopId(memberCardDTO.getShopId(), "会员1");
//    Service service2 = rfiTxnService.getRFServiceByServiceNameAndShopId(memberCardDTO.getShopId(), "会员2");
//    for (MemberCardServiceDTO newMemberCardServiceDTO : newMemberCardDTO.getMemberCardServiceDTOs()) {
//      Assert.assertNotNull(newMemberCardServiceDTO.getId());
//      if (service1.getId().equals(newMemberCardServiceDTO.getServiceId())) {
//        Assert.assertEquals("会员1", newMemberCardServiceDTO.getServiceName());
//      } else {
//        Assert.assertEquals(service2.getId(), newMemberCardServiceDTO.getServiceId());
//        Assert.assertEquals("会员2", newMemberCardServiceDTO.getServiceName());
//      }
//    }
//  }
//
//  /**
//   * 整理要显示在买卡界面上的数据
//   *
//   * @throws Exception
//   */
//  @Test
//  public void testBuyCard() throws Exception {
//    Long shopId = createShop();
//    MemberCardDTO memberCardDTO = new MemberCardDTO();
//    memberCardDTO.setName("会员一号准备");
//    memberCardDTO.setShopId(shopId);
//    memberCardDTO.setWorth(Double.valueOf(500));
//    memberCardDTO.setPrice(Double.valueOf(400));
//    request.getSession().setAttribute("shopId", memberCardDTO.getShopId());
//    CustomerRecordDTO customerRecordDTO = new CustomerRecordDTO();
//    customerRecordDTO.setName("项目一号准备");
//    customerRecordDTO.setInvoiceCategory("1111");
//    customerRecordDTO.setSettlementType("1111");
//    customerController.addCustomer(request, response,modelMap, customerRecordDTO);
//    Long customerId = (Long) request.getSession().getAttribute("customerId");
//    List<MemberCardServiceDTO> memberCardServiceDTOs = new ArrayList<MemberCardServiceDTO>();
//    MemberCardServiceDTO memberCardServiceDTO = new MemberCardServiceDTO();
//    memberCardServiceDTO.setServiceName("项目1");
//    memberCardServiceDTO.setTerm(1);
//    memberCardServiceDTO.setTimes(1);
//    memberCardServiceDTOs.add(memberCardServiceDTO);
//    memberCardServiceDTO = new MemberCardServiceDTO();
//    memberCardServiceDTO.setServiceName("项目2");
//    memberCardServiceDTO.setTerm(2);
//    memberCardServiceDTO.setTimes(2);
//    memberCardServiceDTOs.add(memberCardServiceDTO);
//
//    memberCardDTO.setMemberCardServiceDTOs(memberCardServiceDTOs);
//
//    memberController.saveCardSet(request, response, memberCardDTO);
//
//    Long cardId = ((MemberCardDTO) request.getAttribute("memberCardDTO")).getId();
//
//    String url = memberController.buyCard(modelMap, request, response, customerId, cardId);
//    Assert.assertEquals("/customer/buyCard", url);
//    Assert.assertEquals(shopId, memberCardDTO.getShopId());
//    MemberCardOrderDTO memberCardOrderDTO = (MemberCardOrderDTO) modelMap.get("memberCardOrderDTO");
//    MemberCardDTO newMemberCardDTO = (MemberCardDTO) modelMap.get("memberCardDTO");
//    Assert.assertEquals(customerId, memberCardOrderDTO.getCustomerId());
//    Assert.assertEquals(Double.valueOf(400), newMemberCardDTO.getPrice());
//    Assert.assertEquals(Double.valueOf(500), newMemberCardDTO.getWorth());
//
//    RFITxnService rfiTxnService = ServiceManager.getService(RFITxnService.class);
//
//    Service service1 = rfiTxnService.getRFServiceByServiceNameAndShopId(newMemberCardDTO.getShopId(), "项目1");
//    Service service2 = rfiTxnService.getRFServiceByServiceNameAndShopId(newMemberCardDTO.getShopId(), "项目2");
//    for (MemberCardOrderServiceDTO memberCardOrderServiceDTO : memberCardOrderDTO.getMemberCardOrderServiceDTOs()) {
//      Assert.assertEquals(Integer.valueOf(0), memberCardOrderServiceDTO.getOldTimes());
//      if (service1.getId().equals(memberCardOrderServiceDTO.getServiceId())) {
//        Assert.assertEquals(Integer.valueOf(1), memberCardOrderServiceDTO.getCardTimes());
//        Assert.assertEquals(DateUtil.getDeadline(null, 1), memberCardOrderServiceDTO.getDeadline());
//      } else {
//        Assert.assertEquals(service2.getId(), memberCardOrderServiceDTO.getServiceId());
//        Assert.assertEquals(Integer.valueOf(2), memberCardOrderServiceDTO.getCardTimes());
//        Assert.assertEquals(DateUtil.getDeadline(null, 2), memberCardOrderServiceDTO.getDeadline());
//      }
//    }
//  }
//
//  /**
//   * 合并会员上的服务和套餐卡上的服务一并显示到购卡界面
//   *
//   * @throws Exception
//   */
//  @Test
//  public void testCombineOldServiceAndNewService() throws Exception {
//    MemberDTO memberDTO = new MemberDTO();
//    List<MemberServiceDTO> memberServiceDTOs = new ArrayList<MemberServiceDTO>();
//    MemberServiceDTO memberServiceDTO = new MemberServiceDTO();
//    memberServiceDTO.setDeadline(DateUtil.convertDateStringToDateLong(DateUtil.YEAR_MONTH_DATE, "2012-8-12"));
//    memberServiceDTO.setServiceId(1L);
//    memberServiceDTO.setServiceName("项目1号");
//    memberServiceDTO.setStatus(MemberStatus.ENABLED);
//    memberServiceDTO.setTimes(20);
//    memberServiceDTOs.add(memberServiceDTO);
//    memberServiceDTO = new MemberServiceDTO();
//    memberServiceDTO.setDeadline(DateUtil.convertDateStringToDateLong(DateUtil.YEAR_MONTH_DATE, "2012-09-12"));
//    memberServiceDTO.setServiceId(2L);
//    memberServiceDTO.setServiceName("项目2号");
//    memberServiceDTO.setStatus(MemberStatus.ENABLED);
//    memberServiceDTO.setTimes(15);
//    memberServiceDTOs.add(memberServiceDTO);
//    memberServiceDTO = new MemberServiceDTO();
//    memberServiceDTO.setDeadline(DateUtil.convertDateStringToDateLong(DateUtil.YEAR_MONTH_DATE, "2012-09-12"));
//    memberServiceDTO.setServiceId(3L);
//    memberServiceDTO.setServiceName("项目3号");
//    memberServiceDTO.setStatus(MemberStatus.ENABLED);
//    memberServiceDTO.setTimes(-1);
//    memberServiceDTOs.add(memberServiceDTO);
//    memberServiceDTO = new MemberServiceDTO();
//    memberServiceDTO.setDeadline(DateUtil.convertDateStringToDateLong(DateUtil.YEAR_MONTH_DATE, "2012-09-12"));
//    memberServiceDTO.setServiceId(4L);
//    memberServiceDTO.setServiceName("项目4号");
//    memberServiceDTO.setStatus(MemberStatus.ENABLED);
//    memberServiceDTO.setTimes(15);
//    memberServiceDTOs.add(memberServiceDTO);
//    memberServiceDTO = new MemberServiceDTO();
//    memberServiceDTO.setDeadline(DateUtil.convertDateStringToDateLong(DateUtil.YEAR_MONTH_DATE, "2012-09-12"));
//    memberServiceDTO.setServiceId(5L);
//    memberServiceDTO.setServiceName("项目5号");
//    memberServiceDTO.setStatus(MemberStatus.ENABLED);
//    memberServiceDTO.setTimes(15);
//    memberServiceDTOs.add(memberServiceDTO);
//    memberDTO.setMemberServiceDTOs(memberServiceDTOs);
//
//    Map<Long, MemberServiceDTO> memberServiceDTOMap = MemberServiceDTO.listToMap(memberDTO.getMemberServiceDTOs());
//
//    MemberCardDTO memberCardDTO = new MemberCardDTO();
//    List<MemberCardServiceDTO> memberCardServiceDTOs = new ArrayList<MemberCardServiceDTO>();
//    MemberCardServiceDTO memberCardServiceDTO = new MemberCardServiceDTO();
//    memberCardServiceDTO.setServiceId(1L);
//    memberCardServiceDTO.setServiceName("项目1号");
//    memberCardServiceDTO.setTerm(2);
//    memberCardServiceDTO.setTimes(10);
//    memberCardServiceDTOs.add(memberCardServiceDTO);
//    memberCardServiceDTO = new MemberCardServiceDTO();
//    memberCardServiceDTO.setServiceId(2L);
//    memberCardServiceDTO.setServiceName("项目2号");
//    memberCardServiceDTO.setTerm(5);
//    memberCardServiceDTO.setTimes(18);
//    memberCardServiceDTOs.add(memberCardServiceDTO);
//    memberCardServiceDTO = new MemberCardServiceDTO();
//    memberCardServiceDTO.setServiceId(3L);
//    memberCardServiceDTO.setServiceName("项目3号");
//    memberCardServiceDTO.setTerm(5);
//    memberCardServiceDTO.setTimes(18);
//    memberCardServiceDTOs.add(memberCardServiceDTO);
//    memberCardServiceDTO = new MemberCardServiceDTO();
//    memberCardServiceDTO.setServiceId(4L);
//    memberCardServiceDTO.setServiceName("项目4号");
//    memberCardServiceDTO.setTerm(-1);
//    memberCardServiceDTO.setTimes(18);
//    memberCardServiceDTOs.add(memberCardServiceDTO);
//    memberCardServiceDTO = new MemberCardServiceDTO();
//    memberCardServiceDTO.setServiceId(5L);
//    memberCardServiceDTO.setServiceName("项目5号");
//    memberCardServiceDTO.setTerm(5);
//    memberCardServiceDTO.setTimes(-1);
//    memberCardServiceDTOs.add(memberCardServiceDTO);
//    memberCardDTO.setMemberCardServiceDTOs(memberCardServiceDTOs);
//
//    List<MemberCardOrderServiceDTO> memberCardOrderServiceDTOs = memberController.combineOldServiceAndNewService(memberDTO, memberCardDTO);
//    Assert.assertEquals(Long.valueOf(1), memberCardOrderServiceDTOs.get(0).getServiceId());
//    Assert.assertEquals(new Integer(30), memberCardOrderServiceDTOs.get(0).getBalanceTimes());
//    Assert.assertEquals(DateUtil.getDeadline(memberServiceDTOMap.get(1L).getDeadline(), 2), memberCardOrderServiceDTOs.get(0).getDeadline());
//    Assert.assertEquals(Long.valueOf(2), memberCardOrderServiceDTOs.get(1).getServiceId());
//    Assert.assertEquals(new Integer(33), memberCardOrderServiceDTOs.get(1).getBalanceTimes());
//    Assert.assertEquals(DateUtil.getDeadline(memberServiceDTOMap.get(2L).getDeadline(), 5), memberCardOrderServiceDTOs.get(1).getDeadline());
//    Assert.assertEquals(new Integer(18), memberCardOrderServiceDTOs.get(2).getBalanceTimes());
//    Assert.assertEquals(DateUtil.getDeadline(memberServiceDTOMap.get(3L).getDeadline(), 5), memberCardOrderServiceDTOs.get(2).getDeadline());
//    Assert.assertEquals(new Integer(33), memberCardOrderServiceDTOs.get(3).getBalanceTimes());
//    Assert.assertEquals(Long.valueOf(-1), memberCardOrderServiceDTOs.get(3).getDeadline());
//    Assert.assertEquals(new Integer(-1), memberCardOrderServiceDTOs.get(4).getBalanceTimes());
//    Assert.assertEquals(DateUtil.getDeadline(memberServiceDTOMap.get(5L).getDeadline(), 5), memberCardOrderServiceDTOs.get(4).getDeadline());
//  }
//
//  @Test
//  public void testCheckMemberNo() {
//    request.getSession().setAttribute("shopId", 12345L);
//    memberController.checkMemberNo(modelMap, request, response, "项目");
//    Assert.assertEquals("项目", modelMap.get("memberNo"));
//  }
//
//  @Test
//  public void testGetSaleMans() throws Exception {
//    request.getSession().setAttribute("shopId", 12345L);
//    memberController.getSaleMans(modelMap, request, response, null);
//    Assert.assertEquals("[]", (String) modelMap.get("jsonStr"));
//  }
//
//  /**
//   * 新会员购卡
//   *
//   * @throws Exception
//   */
//  @Test
//  public void testSaveMemberCardOrderByNewMember() throws Exception {
//    Long shopId = createShop();
//    request.getSession().setAttribute("shopId", shopId);
//    CustomerRecordDTO customerRecordDTO = new CustomerRecordDTO();
//    customerRecordDTO.setName("伟哥一号准备");
//    customerRecordDTO.setInvoiceCategory("1111");
//    customerRecordDTO.setSettlementType("1111");
//    customerController.addCustomer(request,response, modelMap, customerRecordDTO);
//    Long customerId = (Long) request.getSession().getAttribute("customerId");
//
//    MemberCardDTO memberCardDTO = createMemberCard(shopId, "会员卡一号准备");
//
//    List<MemberCardOrderServiceDTO> memberCardOrderServiceDTOs = memberController.combineOldServiceAndNewService(null, memberCardDTO);
//
//    for (MemberCardOrderServiceDTO memberCardOrderServiceDTO : memberCardOrderServiceDTOs) {
//      if (-1 == memberCardOrderServiceDTO.getBalanceTimes()) {
//        memberCardOrderServiceDTO.setTimesStatus(Integer.valueOf(TimesStatus.UNLIMITED.getStatus()).intValue());
//      }
//      if (-1 == memberCardOrderServiceDTO.getDeadline()) {
//        memberCardOrderServiceDTO.setDeadlineStatus(1);
//      } else {
//        memberCardOrderServiceDTO.setDeadlineStr(DateUtil.convertDateLongToDateString(DateUtil.YEAR_MONTH_DATE,
//            memberCardOrderServiceDTO.getDeadline()));
//      }
//    }
//
//    MemberCardOrderDTO memberCardOrderDTO = new MemberCardOrderDTO();
//    memberCardOrderDTO.setMemberCardOrderServiceDTOs(memberCardOrderServiceDTOs);
//    ReceivableDTO receivableDTO = new ReceivableDTO();
//    ReceptionRecordDTO[] receptionRecordDTOs = new ReceptionRecordDTO[1];
//    ReceptionRecordDTO receptionRecordDTO = new ReceptionRecordDTO();
//    receptionRecordDTOs[0] = receptionRecordDTO;
//    receivableDTO.setTotal(2000);
//    receivableDTO.setDiscount(200);
//    receivableDTO.setDebt(200);
//    receivableDTO.setCash(800d);
//    receivableDTO.setBankCard(300d);
//    receivableDTO.setCheque(500d);
//    receptionRecordDTOs[0].setChequeNo("555555");
//    receivableDTO.setRecordDTOs(receptionRecordDTOs);
//
//    List<MemberCardOrderItemDTO> memberCardOrderItemDTOs = new ArrayList<MemberCardOrderItemDTO>();
//    MemberCardOrderItemDTO memberCardOrderItemDTO = new MemberCardOrderItemDTO();
//    memberCardOrderItemDTO.setPrice(2000.0);
//    memberCardOrderItemDTO.setAmount(1800.0);
//    memberCardOrderItemDTO.setWorth(2500.0);
//    memberCardOrderItemDTO.setCardId(memberCardDTO.getId());
//    memberCardOrderItemDTOs.add(memberCardOrderItemDTO);
//    memberCardOrderDTO.setMemberCardOrderItemDTOs(memberCardOrderItemDTOs);
//    MemberDTO memberDTO = new MemberDTO();
//    memberDTO.setMemberNo("123456");
//    memberDTO.setPassword("000000");
//    memberDTO.setBalance(2500.0);
//    memberCardOrderDTO.setReceivableDTO(receivableDTO);
//    memberCardOrderDTO.setCustomerId(customerId);
//    memberCardOrderDTO.setExecutorId(0L);
//    memberCardOrderDTO.setShopId(shopId);
//    memberCardOrderDTO.setCustomerName(customerRecordDTO.getName());
//    memberCardOrderDTO.setRepayTime("2012-09-20");
//    memberCardOrderDTO.setTotal(2000.0);
//    memberCardOrderDTO.setMobile("12345678987");
//    memberCardOrderDTO.setMemberDTO(memberDTO);
//
//    memberController.saveMemberCardOrder(modelMap, request, response, memberCardOrderDTO);
//
//    String jsonStr = (String) modelMap.get("jsonStr");
//    boolean searchTransSuccess = ((Boolean) modelMap.get("searchTransSuccess")).booleanValue();
//    MemberCardOrderDTO newMemberCardOrderDTO = (MemberCardOrderDTO) modelMap.get("memberCardOrderDTO");
//    String expectedJsonStr = "{\"resu\":\"success\",\"orderId\":\"" + newMemberCardOrderDTO.getId() + "\",\"memberNo\":\"123456\"}";
//    Assert.assertEquals(true, searchTransSuccess);
//    Assert.assertEquals(expectedJsonStr, jsonStr);
//    Assert.assertEquals(customerId, newMemberCardOrderDTO.getCustomerId());
//    Assert.assertEquals(true, null == newMemberCardOrderDTO.getId() ? false : true);
//
//    IMembersService membersService = ServiceManager.getService(IMembersService.class);
//    MemberDTO newMemberDTO = membersService.getMemberByCustomerId(newMemberCardOrderDTO.getShopId(), customerId);
//    Assert.assertEquals(memberDTO.getMemberNo(), newMemberDTO.getMemberNo());
//    Assert.assertEquals(Double.valueOf(2500), newMemberDTO.getBalance());
//    for (MemberServiceDTO memberServiceDTO : newMemberDTO.getMemberServiceDTOs()) {
//      ServiceDTO serviceDTO = ServiceManager.getService(ITxnService.class).getServiceById(memberServiceDTO.getServiceId());
//      if ("换机油".equals(serviceDTO.getName())) {
//        Assert.assertEquals(new Integer(8), memberServiceDTO.getTimes());
//        Assert.assertEquals(DateUtil.getDeadline(System.currentTimeMillis(), 5), memberServiceDTO.getDeadline());
//      } else {
//        Assert.assertEquals(new Integer(-1), memberServiceDTO.getTimes());
//        Assert.assertEquals(DateUtil.getDeadline(System.currentTimeMillis(), 2), memberServiceDTO.getDeadline());
//      }
//    }
//  }
//
//  /**
//   * 老会员续卡， 退卡
//   *
//   * @throws Exception
//   */
//  @Test
//  public void testSaveMemberCardOrderByOldMemberAndReturnCard() throws Exception {
//    Long shopId = createShop();
//    request.getSession().setAttribute("shopId", shopId);
//
//    //创建客户
//    CustomerRecordDTO customerRecordDTO = new CustomerRecordDTO();
//    customerRecordDTO.setName("伟哥一号准备");
//    customerRecordDTO.setInvoiceCategory("1111");
//    customerRecordDTO.setSettlementType("1111");
//    customerController.addCustomer(request,response, modelMap, customerRecordDTO);
//    Long customerId = (Long) request.getSession().getAttribute("customerId");
//
//    //创建会员
//    MemberDTO memberDTO = createMember(shopId, customerId, "会员一号准备", "123456");
//
//    Map<Long, MemberServiceDTO> memberServiceDTOMap = MemberServiceDTO.listToMap(memberDTO.getMemberServiceDTOs());
//
//    membersService = ServiceManager.getService(IMembersService.class);
//    //创建套餐卡
//    MemberCardDTO memberCardDTO = new MemberCardDTO();
//    memberCardDTO.setPrice(2000.0);
//    memberCardDTO.setWorth(2500.0);
//    memberCardDTO.setPercentageAmount(10.0);
//    memberCardDTO.setName("会员卡一号准备");
//    memberCardDTO.setShopId(shopId);
//    List<MemberCardServiceDTO> memberCardServiceDTOs = new ArrayList<MemberCardServiceDTO>();
//    MemberCardServiceDTO memberCardServiceDTO = null;
//    //创建和会员卡上服务种类相同的服务
//    for (MemberServiceDTO memberServiceDTO : memberDTO.getMemberServiceDTOs()) {
//      if ("施工项目1".equals(memberServiceDTO.getServiceName())) {
//        memberCardServiceDTO = new MemberCardServiceDTO();
//        memberCardServiceDTO.setServiceId(memberServiceDTO.getServiceId());
//        memberCardServiceDTO.setServiceName("施工项目1");
//        memberCardServiceDTO.setTerm(5);
//        memberCardServiceDTO.setTimes(-1);
//        memberCardServiceDTOs.add(memberCardServiceDTO);
//      } else {
//        memberCardServiceDTO = new MemberCardServiceDTO();
//        memberCardServiceDTO.setServiceId(memberServiceDTO.getServiceId());
//        memberCardServiceDTO.setServiceName("施工项目2");
//        memberCardServiceDTO.setTerm(2);
//        memberCardServiceDTO.setTimes(8);
//        memberCardServiceDTOs.add(memberCardServiceDTO);
//      }
//    }
//
//    Object status = txnWriter.begin();
//    Service service = new Service();
//    service.setName("施工项目3");
//    service.setShopId(shopId);
//    txnWriter.save(service);
//    txnWriter.commit(status);
//
//    //创建会员卡上没有的服务
//    memberCardServiceDTO = new MemberCardServiceDTO();
//    memberCardServiceDTO.setServiceId(service.getId());
//    memberCardServiceDTO.setServiceName(service.getName());
//    memberCardServiceDTO.setTerm(2);
//    memberCardServiceDTO.setTimes(9);
//    memberCardServiceDTOs.add(memberCardServiceDTO);
//    Map<Long, MemberCardServiceDTO> memberCardServiceDTOMap = MemberCardServiceDTO.listToMap(memberCardServiceDTOs);
//    memberCardDTO.setMemberCardServiceDTOs(memberCardServiceDTOs);
//
//    membersService.saveOrUpdateMemberCard(memberCardDTO);
//
//    //合并会员卡上的服务和套餐上的服务
//    List<MemberCardOrderServiceDTO> memberCardOrderServiceDTOs = memberController.combineOldServiceAndNewService(memberDTO, memberCardDTO);
//
//    //根据次数和期限给次数和期限的状态赋值，这两个都是从页面传过来的，这里模拟一下
//    for (MemberCardOrderServiceDTO memberCardOrderServiceDTO : memberCardOrderServiceDTOs) {
//      if (-1 == memberCardOrderServiceDTO.getBalanceTimes()) {
//        //1表示无限次的状态
//        memberCardOrderServiceDTO.setTimesStatus(Integer.valueOf(TimesStatus.UNLIMITED.getStatus()).intValue());
//      }
//      if (-1 == memberCardOrderServiceDTO.getDeadline()) {
//        //1表示无限期的状态
//        memberCardOrderServiceDTO.setDeadlineStatus(1);
//      } else {
//        memberCardOrderServiceDTO.setDeadlineStr(DateUtil.convertDateLongToDateString(DateUtil.YEAR_MONTH_DATE,
//            memberCardOrderServiceDTO.getDeadline()));
//      }
//    }
//
//    //整理从页面传过来的数据，封装到memberCardOrderDTO中
//    MemberCardOrderDTO memberCardOrderDTO = new MemberCardOrderDTO();
//    memberCardOrderDTO.setMemberCardOrderServiceDTOs(memberCardOrderServiceDTOs);
//    ReceivableDTO receivableDTO = new ReceivableDTO();
//    ReceptionRecordDTO[] receptionRecordDTOs = new ReceptionRecordDTO[1];
//    ReceptionRecordDTO receptionRecordDTO = new ReceptionRecordDTO();
//    receptionRecordDTOs[0] = receptionRecordDTO;
//    receivableDTO.setTotal(2000);
//    receivableDTO.setDiscount(200);
//    receivableDTO.setDebt(200);
//    receivableDTO.setCash(800d);
//    receivableDTO.setBankCard(300d);
//    receivableDTO.setCheque(500d);
//    receptionRecordDTOs[0].setChequeNo("555555");
//    receivableDTO.setRecordDTOs(receptionRecordDTOs);
//
//    List<MemberCardOrderItemDTO> memberCardOrderItemDTOs = new ArrayList<MemberCardOrderItemDTO>();
//    MemberCardOrderItemDTO memberCardOrderItemDTO = new MemberCardOrderItemDTO();
//    memberCardOrderItemDTO.setPrice(2000.0);
//    memberCardOrderItemDTO.setAmount(1800.0);
//    memberCardOrderItemDTO.setWorth(2500.0);
//    memberCardOrderItemDTO.setSalesId(2222L);
//    memberCardOrderItemDTO.setSalesMan("卖卡人1");
//    memberCardOrderItemDTO.setCardId(memberCardDTO.getId());
//    memberCardOrderItemDTOs.add(memberCardOrderItemDTO);
//    memberCardOrderDTO.setMemberCardOrderItemDTOs(memberCardOrderItemDTOs);
//
//    memberDTO.setBalance(2500.0 + (null == memberDTO.getBalance() ? 0.0 : memberDTO.getBalance().doubleValue()));
//    memberCardOrderDTO.setReceivableDTO(receivableDTO);
//    memberCardOrderDTO.setCustomerId(customerId);
//    memberCardOrderDTO.setExecutorId(0L);
//    memberCardOrderDTO.setShopId(shopId);
//    memberCardOrderDTO.setCustomerName(customerRecordDTO.getName());
//    memberCardOrderDTO.setRepayTime("2012-09-20");
//    memberCardOrderDTO.setTotal(2000.0);
//    memberCardOrderDTO.setMobile("12345678987");
//    memberCardOrderDTO.setMemberDTO(memberDTO);
//
//    //购卡controller
//    memberController.saveMemberCardOrder(modelMap, request, response, memberCardOrderDTO);
//
//    String jsonStr = (String) modelMap.get("jsonStr");
//    boolean searchTransSuccess = ((Boolean) modelMap.get("searchTransSuccess")).booleanValue();
//    MemberCardOrderDTO newMemberCardOrderDTO = (MemberCardOrderDTO) modelMap.get("memberCardOrderDTO");
//    Long memberCardOrderId = newMemberCardOrderDTO.getId();
//
//    String expectedJsonStr = "{\"resu\":\"success\",\"orderId\":\"" + newMemberCardOrderDTO.getId() + "\",\"memberNo\":\"会员一号准备\"}";
//
//    Assert.assertEquals(true, searchTransSuccess);
//    Assert.assertEquals(expectedJsonStr, jsonStr);
//    Assert.assertEquals(customerId, newMemberCardOrderDTO.getCustomerId());
//    Assert.assertEquals(true, null == newMemberCardOrderDTO.getId() ? false : true);
//    Assert.assertEquals(2222L, newMemberCardOrderDTO.getMemberCardOrderItemDTOs().get(0).getSalesId().longValue());
//
//    IMembersService membersService = ServiceManager.getService(IMembersService.class);
//    MemberDTO newMemberDTO = membersService.getMemberByCustomerId(newMemberCardOrderDTO.getShopId(), customerId);
//    Assert.assertEquals(memberDTO.getMemberNo(), newMemberDTO.getMemberNo());
//    Assert.assertEquals(memberDTO.getBalance(), newMemberDTO.getBalance());
//
//    txnService = ServiceManager.getService(TxnService.class);
//    ReceivableDTO newReceivableDTO = txnService.getReceivableByShopIdAndOrderTypeAndOrderId(shopId, null, newMemberCardOrderDTO.getId());
//
//    Assert.assertEquals(2000.0, newReceivableDTO.getTotal());
//    Assert.assertEquals(200.0, newReceivableDTO.getDiscount());
//    Assert.assertEquals(200.0, newReceivableDTO.getDebt());
//    Assert.assertEquals(800.0, newReceivableDTO.getCash());
//    Assert.assertEquals(300.0, newReceivableDTO.getBankCard());
//    Assert.assertEquals(500.0, newReceivableDTO.getCheque());
//
//    for (MemberServiceDTO memberServiceDTO : newMemberDTO.getMemberServiceDTOs()) {
//      MemberCardServiceDTO memberCardServiceDTOTest = memberCardServiceDTOMap.get(memberServiceDTO.getServiceId());
//      if ("施工项目1".equals(memberCardServiceDTOTest.getServiceName())) {
//        Assert.assertEquals(new Integer(-1), memberServiceDTO.getTimes());
//        if (null != memberServiceDTOMap.get(memberServiceDTO.getServiceId())) {
//          Assert.assertEquals(DateUtil.getDeadline(memberServiceDTOMap.get(memberServiceDTO.getServiceId()).getDeadline(), 5), memberServiceDTO.getDeadline());
//        } else {
//          Assert.assertEquals(DateUtil.getDeadline(System.currentTimeMillis(), 5), memberServiceDTO.getDeadline());
//        }
//      } else if ("施工项目2".equals(memberCardServiceDTOTest.getServiceName())) {
//        Assert.assertEquals(new Integer(8), memberServiceDTO.getTimes());
//        if (null != memberServiceDTOMap.get(memberServiceDTO.getServiceId())) {
//          Assert.assertEquals(DateUtil.getDeadline(memberServiceDTOMap.get(memberServiceDTO.getServiceId()).getDeadline(), 2), memberServiceDTO.getDeadline());
//        } else {
//          Assert.assertEquals(DateUtil.getDeadline(System.currentTimeMillis(), 2), memberServiceDTO.getDeadline());
//        }
//      } else {
//        Assert.assertEquals(new Integer(9), memberServiceDTO.getTimes());
//        if (null != memberServiceDTOMap.get(memberServiceDTO.getServiceId())) {
//          Assert.assertEquals(DateUtil.getDeadline(memberServiceDTOMap.get(memberServiceDTO.getServiceId()).getDeadline(), 2), memberServiceDTO.getDeadline());
//        } else {
//          Assert.assertEquals(DateUtil.getDeadline(System.currentTimeMillis(), 2), memberServiceDTO.getDeadline());
//        }
//      }
//    }
//
//
//    // 生成退卡单页面
//    modelMap = new ModelMap();
//    memberController.returnCard(modelMap, request, customerId,null);
//    memberDTO = (MemberDTO) modelMap.get("memberDTO");
//    Assert.assertEquals(3, memberDTO.getMemberServiceDTOs().size());
//    Assert.assertEquals(4500.0d, memberDTO.getBalance());
//
//    MemberCardReturnDTO memberCardReturnDTO = (MemberCardReturnDTO)modelMap.get("memberCardReturnDTO");
//    List<MemberCardReturnServiceDTO> returnServiceDTOs = memberCardReturnDTO.getMemberCardReturnServiceDTOs();
//    Assert.assertTrue(CollectionUtils.isNotEmpty(returnServiceDTOs));
//    Assert.assertEquals(3, returnServiceDTOs.size());
//    for(MemberCardReturnServiceDTO serviceDTO : returnServiceDTOs){
//      if("施工项目1".equals(serviceDTO.getServiceName())){
//        Assert.assertEquals(new Integer(-1), serviceDTO.getRemainTimes());
//      }else if("施工项目2".equals(serviceDTO.getServiceName())){
//        Assert.assertEquals(new Integer(8), serviceDTO.getRemainTimes());
//      }else if("施工项目3".equals(serviceDTO.getServiceName())){
//        Assert.assertEquals(new Integer(9), serviceDTO.getRemainTimes());
//      }
//    }
//    Assert.assertEquals(1, memberCardReturnDTO.getMemberCardReturnItemDTOs().size());
//    Assert.assertEquals(2222L, memberCardReturnDTO.getMemberCardReturnItemDTOs().get(0).getSalesId().longValue());
//    Assert.assertEquals(2000.0d, memberCardReturnDTO.getTotal());
//    Assert.assertEquals(2500d, memberCardReturnDTO.getLastRecharge());
//    Assert.assertEquals(4500d, memberCardReturnDTO.getMemberBalance());
//
//    //保存退卡
//    CustomerDTO customerDTO = (CustomerDTO)modelMap.get("customerDTO");
//
//    memberCardReturnDTO.setCustomerId(customerDTO.getId());
//    MemberDTO memberDTOInPage = new MemberDTO();
//    memberDTOInPage.setId(memberDTO.getId());
//    memberCardReturnDTO.setMemberDTO(memberDTOInPage);
//
//    receptionRecordDTO = new ReceptionRecordDTO();
//    receptionRecordDTO.setOrderTotal(memberCardReturnDTO.getTotal());
//    receptionRecordDTO.setCash(memberCardReturnDTO.getTotal());
//    receptionRecordDTO.setShopId(shopId);
//    memberCardReturnDTO.setReceptionRecordDTO(receptionRecordDTO);
//
//    modelMap = new ModelMap();
//    request = new MockHttpServletRequest();
//    request.getSession().setAttribute("shopId", shopId);
//    memberController.saveReturnCard(modelMap, request, memberCardReturnDTO);
//    memberCardReturnDTO = (MemberCardReturnDTO)modelMap.get("memberCardReturnDTO");
//    MemberDTO memberDTO1 = membersService.getMemberByCustomerId(shopId, customerId);
//    //服务项次数置0
//    for(MemberServiceDTO memberServiceDTO:memberDTO1.getMemberServiceDTOs()){
//      Assert.assertEquals(new Integer(0), memberServiceDTO.getTimes());
//    }
//    //会员储值余额置0
//    Assert.assertEquals(0d, memberDTO1.getBalance());
//
//    memberCardReturnDTO = txnService.getMemberCardReturnDTOById(shopId, memberCardReturnDTO.getId());
//    Assert.assertEquals(3, memberCardReturnDTO.getMemberCardReturnServiceDTOs().size());
//    Assert.assertEquals(1, memberCardReturnDTO.getMemberCardReturnItemDTOs().size());
//    Assert.assertEquals(memberCardOrderId, memberCardReturnDTO.getLastMemberCardOrderId());
//    Assert.assertEquals(2000.0d, memberCardReturnDTO.getLastBuyTotal());
//    Assert.assertEquals(4500d, memberCardReturnDTO.getMemberBalance());
//    Assert.assertEquals(2222L, memberCardReturnDTO.getMemberCardReturnItemDTOs().get(0).getSalesId().longValue());
//
//    searchService = ServiceManager.getService(SearchService.class);
//    List<OrderIndexDTO> orderIndexes = searchService.getOrderIndexDTOByOrderId(shopId, memberCardReturnDTO.getId());
//    Assert.assertEquals(1, orderIndexes.size());
//    List<ItemIndexDTO> itemIndexes = searchService.getItemIndexDTOListByOrderId(shopId, memberCardReturnDTO.getId());
//    Assert.assertEquals(4, itemIndexes.size());
//
//  }
//
//}
