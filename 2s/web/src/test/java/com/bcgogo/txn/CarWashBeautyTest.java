package com.bcgogo.txn;

import com.bcgogo.AbstractTest;
import com.bcgogo.search.service.SearchService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.service.RFTxnService;
import com.bcgogo.txn.service.TxnService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * Created by IntelliJ IDEA.
 * User: zhoudongming
 * Date: 12-7-24
 * Time: 上午9:20
 * To change this template use File | Settings | File Templates.
 */
public class CarWashBeautyTest extends AbstractTest {
  @Before
  public void setUp() throws Exception{
    categoryController = new CategoryController();
    washBeautyController = new WashBeautyController();
    request = new MockHttpServletRequest();
    response = new MockHttpServletResponse();
    rfiTxnService = ServiceManager.getService(RFTxnService.class);
    txnService = ServiceManager.getService(TxnService.class);
    searchService = ServiceManager.getService(SearchService.class);
  }

  @Test(timeout = 300000L)
  public void testCreateWashBeautyOrder() throws Exception{
//    ModelMap model = new ModelMap();
//    Long shopId = createShop();
//    Long worker1 = createSalesMan(shopId, "洗美工人1");
//    Long worker2 = createSalesMan(shopId, "洗美工人2");
//    request.getSession().setAttribute("shopId", shopId);
//    saveServiceAndCategory(shopId);
//    ServiceDTO[] serviceDTOs = rfiTxnService.getServiceByWashBeauty(shopId,null);
//    WashBeautyOrderDTO w = saveMemberInfoDetail(shopId,serviceDTOs[0].getId());
//    washBeautyController.createWashBeautyOrder(model,request,null);
//    WashBeautyOrderDTO washBeautyOrderDTO = (WashBeautyOrderDTO)model.get("washBeautyOrderDTO");
//    serviceDTOs = washBeautyOrderDTO.getServiceDTOs();
//    Assert.assertEquals(1,serviceDTOs.length);
//    ServiceDTO serviceDTO = serviceDTOs[0];
//    Assert.assertEquals("洗车",serviceDTO.getName());
//    Assert.assertEquals(20,serviceDTO.getPrice(),0.001);
//    Assert.assertEquals(5,serviceDTO.getPercentageAmount(),0.001);
//
//    request.setParameter("customerId", String.valueOf(w.getCustomerId()));
//    washBeautyController.getCustomerInfoByName(model,request, null, null, null);
//    washBeautyOrderDTO = (WashBeautyOrderDTO)model.get("washBeautyOrderDTO");
//    serviceDTOs = washBeautyOrderDTO.getServiceDTOs();
//    Assert.assertEquals(1,serviceDTOs.length);
//    serviceDTO = serviceDTOs[0];
//    Assert.assertEquals("洗车",serviceDTO.getName());
//    Assert.assertEquals(20,serviceDTO.getPrice(),0.001);
//    Assert.assertEquals(5,serviceDTO.getPercentageAmount(),0.001);
//    Assert.assertEquals("周东明",washBeautyOrderDTO.getCustomer());
//    Assert.assertEquals(w.getCustomerId(),washBeautyOrderDTO.getCustomerId());
//    Assert.assertEquals("苏E00000",washBeautyOrderDTO.getLicenceNo());
//    Assert.assertEquals("15995496255",washBeautyOrderDTO.getMobile());
//    Assert.assertEquals("奥迪",washBeautyOrderDTO.getBrand());
//    Assert.assertEquals("TT",washBeautyOrderDTO.getModel());
//    MemberDTO memberDTO = washBeautyOrderDTO.getMemberDTO();
//    List<MemberServiceDTO> memberServiceDTOs = memberDTO.getMemberServiceDTOs();
//    Assert.assertEquals(1,memberServiceDTOs.size());
//    MemberServiceDTO memberServiceDTO = memberServiceDTOs.get(0);
//    Assert.assertEquals(2000d,memberDTO.getBalance(),0.001);
//    Assert.assertEquals(w.getCustomerId(),memberDTO.getCustomerId());
//    Assert.assertEquals("苏E00000",memberDTO.getMemberNo());
//    Assert.assertEquals(serviceDTO.getId(),memberServiceDTO.getServiceId());
//    Assert.assertEquals(20,memberServiceDTO.getTimes().intValue());
//    Assert.assertEquals("洗车",memberServiceDTO.getServiceName());
//    Assert.assertEquals(DateUtil.getDeadline(System.currentTimeMillis(),1),memberServiceDTO.getDeadline());
//
//    request.setParameter("licenceNo",w.getLicenceNo());
//    washBeautyController.getCustomerInfoByLicenceNo(model,request, null, null, null);
//    washBeautyOrderDTO = (WashBeautyOrderDTO)model.get("washBeautyOrderDTO");
//    serviceDTOs = washBeautyOrderDTO.getServiceDTOs();
//    Assert.assertEquals(1,serviceDTOs.length);
//    serviceDTO = serviceDTOs[0];
//    Assert.assertEquals("洗车",serviceDTO.getName());
//    Assert.assertEquals(20,serviceDTO.getPrice(),0.001);
//    Assert.assertEquals(5,serviceDTO.getPercentageAmount(),0.001);
//    Assert.assertEquals("周东明",washBeautyOrderDTO.getCustomer());
//    Assert.assertEquals(w.getCustomerId(),washBeautyOrderDTO.getCustomerId());
//    Assert.assertEquals("苏E00000",washBeautyOrderDTO.getLicenceNo());
//    Assert.assertEquals("15995496255",washBeautyOrderDTO.getMobile());
//    Assert.assertEquals("奥迪",washBeautyOrderDTO.getBrand());
//    Assert.assertEquals("TT",washBeautyOrderDTO.getModel());
//    memberDTO = washBeautyOrderDTO.getMemberDTO();
//    memberServiceDTOs = memberDTO.getMemberServiceDTOs();
//    Assert.assertEquals(1,memberServiceDTOs.size());
//    memberServiceDTO = memberServiceDTOs.get(0);
//    Assert.assertEquals(2000d,memberDTO.getBalance(),0.001);
//    Assert.assertEquals(w.getCustomerId(),memberDTO.getCustomerId());
//    Assert.assertEquals("苏E00000",memberDTO.getMemberNo());
//    Assert.assertEquals(serviceDTO.getId(),memberServiceDTO.getServiceId());
//    Assert.assertEquals(20,memberServiceDTO.getTimes().intValue());
//    Assert.assertEquals("洗车",memberServiceDTO.getServiceName());
//    Assert.assertEquals(DateUtil.getDeadline(System.currentTimeMillis(),1),memberServiceDTO.getDeadline());
//
//    categoryController.createNewService(model,request);
//    CategoryServiceSearchDTO categoryServiceSearchDTO = (CategoryServiceSearchDTO)model.get("categoryServiceSearchDTO");
//    categoryServiceSearchDTO.setServiceName("打蜡");
//    categoryServiceSearchDTO.setCategoryName("美容");
//    categoryServiceSearchDTO.setPrice(35d);
//    categoryServiceSearchDTO.setPercentageAmount(10d);
////    categoryController.addNewService(model,request,categoryServiceSearchDTO);
//    categoryServiceSearchDTO = (CategoryServiceSearchDTO)model.get("categoryServiceSearchDTO");
//    ServiceDTO serviceDTO2 = categoryServiceSearchDTO.getServiceDTOs()[1];
//
//    WashBeautyOrderItemDTO[] washBeautyOrderItemDTOs = new WashBeautyOrderItemDTO[2];
//    washBeautyOrderItemDTOs[0] = new WashBeautyOrderItemDTO();
//    washBeautyOrderItemDTOs[0].setPayType(ConsumeType.TIMES);
//    washBeautyOrderItemDTOs[0].setPrice(20d);
//    washBeautyOrderItemDTOs[0].setServiceId(serviceDTO.getId());
//    washBeautyOrderItemDTOs[0].setConsumeTypeStr(ConsumeType.TIMES);
//    washBeautyOrderItemDTOs[0].setShopId(shopId);
//    washBeautyOrderItemDTOs[0].setSurplusTimes("20");
//    washBeautyOrderItemDTOs[0].setSalesMan("洗美工人1,洗美工人3");
//    washBeautyOrderItemDTOs[1] = new WashBeautyOrderItemDTO();
//    washBeautyOrderItemDTOs[1].setPayType(ConsumeType.MONEY);
//    washBeautyOrderItemDTOs[1].setPrice(35d);
//    washBeautyOrderItemDTOs[1].setServiceId(serviceDTO2.getId());
//    washBeautyOrderItemDTOs[1].setConsumeTypeStr(ConsumeType.MONEY);
//    washBeautyOrderItemDTOs[1].setShopId(shopId);
//    washBeautyOrderItemDTOs[1].setSurplusTimes(null);
//    washBeautyOrderItemDTOs[1].setSalesMan("洗美工人1,洗美工人2");
//    washBeautyOrderDTO.setWashBeautyOrderItemDTOs(washBeautyOrderItemDTOs);
//    washBeautyOrderDTO.setTotal(35d);
//    washBeautyOrderDTO.setSettledAmount(35d);
//    washBeautyOrderDTO.setAccountMemberNo("苏E00000");
//    washBeautyOrderDTO.setBankAmount(0d);
//    washBeautyOrderDTO.setBankCheckAmount(0d);
//    washBeautyOrderDTO.setBankCheckNo("");
//    washBeautyOrderDTO.setCashAmount(0d);
//    washBeautyOrderDTO.setOrderDiscount(0);
//    washBeautyOrderDTO.setDebt(0);
//    washBeautyOrderDTO.setMemberAmount(35d);
//    washBeautyController.saveWashBeautyOrder(model,request,washBeautyOrderDTO);
//    washBeautyOrderDTO = (WashBeautyOrderDTO)model.get("washBeautyOrderDTO");
//    memberDTO = washBeautyOrderDTO.getMemberDTO();
//    Assert.assertEquals(1965d,memberDTO.getBalance(),0.001);
//    MemberServiceDTO memberServiceDTO1 = memberDTO.getMemberServiceDTOs().get(0);
//    Assert.assertEquals(19,memberServiceDTO1.getTimes().intValue());
//    ReceivableDTO receivableDTO = txnService.getReceivableByShopIdAndOrderTypeAndOrderId(shopId, OrderTypes.WASH_BEAUTY,washBeautyOrderDTO.getId());
//    Assert.assertEquals(washBeautyOrderDTO.getCashAmount(),receivableDTO.getCash(),0.001);
//    Assert.assertEquals(washBeautyOrderDTO.getTotal(),receivableDTO.getTotal(),0.001);
//    Assert.assertEquals(washBeautyOrderDTO.getBankCheckAmount(),receivableDTO.getCheque(),0.001);
//    Assert.assertEquals(washBeautyOrderDTO.getDebt(),receivableDTO.getDebt(),0.001);
//    Assert.assertEquals(washBeautyOrderDTO.getOrderDiscount(),receivableDTO.getDiscount(),0.001);
//    Assert.assertEquals(washBeautyOrderDTO.getMemberAmount(),receivableDTO.getMemberBalancePay(),0.001);
//    Assert.assertEquals(washBeautyOrderDTO.getSettledAmount(),receivableDTO.getSettledAmount(),0.001);
//    Assert.assertEquals(OrderTypes.WASH_BEAUTY,receivableDTO.getOrderType());
//    Assert.assertEquals(shopId,receivableDTO.getShopId());
//    Assert.assertEquals(washBeautyOrderDTO.getId(),receivableDTO.getOrderId());
//    List<ItemIndexDTO> itemIndexDTOs = searchService.getItemIndexDTOListByOrderId(shopId,washBeautyOrderDTO.getId());
//    Assert.assertEquals(2,itemIndexDTOs.size());
//    ItemIndexDTO itemIndexDTO1 = itemIndexDTOs.get(0);
//    ItemIndexDTO itemIndexDTO2 = itemIndexDTOs.get(1);
//    WashBeautyOrderItemDTO washBeautyOrderItemDTO1 = washBeautyOrderDTO.getWashBeautyOrderItemDTOs()[0];
//    WashBeautyOrderItemDTO washBeautyOrderItemDTO2 = washBeautyOrderDTO.getWashBeautyOrderItemDTOs()[1];
//    Assert.assertEquals(OrderStatus.WASH_SETTLED,itemIndexDTO1.getOrderStatus());
//    Assert.assertEquals(washBeautyOrderDTO.getCustomerId(),itemIndexDTO1.getCustomerId());
//    Assert.assertEquals(washBeautyOrderDTO.getCustomer(),itemIndexDTO1.getCustomerOrSupplierName());
//    Assert.assertEquals(washBeautyOrderItemDTO1.getPrice(),itemIndexDTO1.getItemPrice(),0.001);
//    Assert.assertEquals(0,itemIndexDTO1.getItemCostPrice(),0.001);
//    Assert.assertEquals(washBeautyOrderItemDTO1.getId(),itemIndexDTO1.getItemId());
//    Assert.assertEquals(washBeautyOrderItemDTO1.getServiceId(),itemIndexDTO1.getServiceId());
//    Assert.assertEquals("洗美工人1,洗美工人3", washBeautyOrderItemDTO1.getSalesMan());
//    Assert.assertTrue(washBeautyOrderItemDTO2.getSalesManIds().contains(String.valueOf(worker1)));
//    Assert.assertEquals("洗车",itemIndexDTO1.getItemName());
//    Assert.assertEquals(ItemTypes.WASH,itemIndexDTO1.getItemType());
//    Assert.assertEquals(memberDTO.getId(),itemIndexDTO1.getMemberCardId());
//    Assert.assertEquals(washBeautyOrderDTO.getId(),itemIndexDTO1.getOrderId());
//    Assert.assertEquals(washBeautyOrderDTO.getTotal(),itemIndexDTO1.getOrderTotalAmount(),0.001);
//    Assert.assertEquals(OrderTypes.WASH_BEAUTY,itemIndexDTO1.getOrderType());
//    Assert.assertEquals(shopId,itemIndexDTO1.getShopId());
//    Assert.assertEquals(washBeautyOrderDTO.getLicenceNo(),itemIndexDTO1.getVehicle());
//    Assert.assertEquals(1,itemIndexDTO1.getIncreasedTimes().intValue());
//    Assert.assertEquals(OrderStatus.WASH_SETTLED,itemIndexDTO2.getOrderStatus());
//    Assert.assertEquals(washBeautyOrderDTO.getCustomerId(),itemIndexDTO2.getCustomerId());
//    Assert.assertEquals(washBeautyOrderDTO.getCustomer(),itemIndexDTO2.getCustomerOrSupplierName());
//    Assert.assertEquals(washBeautyOrderItemDTO2.getPrice(),itemIndexDTO2.getItemPrice(),0.001);
//    Assert.assertEquals(0,itemIndexDTO2.getItemCostPrice(),0.001);
//    Assert.assertEquals(washBeautyOrderItemDTO2.getId(),itemIndexDTO2.getItemId());
//    Assert.assertEquals(washBeautyOrderItemDTO2.getServiceId(),itemIndexDTO2.getServiceId());
//    Assert.assertEquals("洗美工人1,洗美工人2", washBeautyOrderItemDTO2.getSalesMan());
//    Assert.assertTrue(washBeautyOrderItemDTO2.getSalesManIds().contains(String.valueOf(worker1)));
//    Assert.assertTrue(washBeautyOrderItemDTO2.getSalesManIds().contains(String.valueOf(worker2)));
//
//    Assert.assertEquals("打蜡",itemIndexDTO2.getItemName());
//    Assert.assertEquals(ItemTypes.WASH,itemIndexDTO2.getItemType());
//    Assert.assertEquals(memberDTO.getId(),itemIndexDTO2.getMemberCardId());
//    Assert.assertEquals(washBeautyOrderDTO.getId(),itemIndexDTO2.getOrderId());
//    Assert.assertEquals(washBeautyOrderDTO.getTotal(),itemIndexDTO2.getOrderTotalAmount(),0.001);
//    Assert.assertEquals(OrderTypes.WASH_BEAUTY,itemIndexDTO2.getOrderType());
//    Assert.assertEquals(shopId,itemIndexDTO2.getShopId());
//    Assert.assertEquals(washBeautyOrderDTO.getLicenceNo(),itemIndexDTO2.getVehicle());
//    Assert.assertEquals(1,itemIndexDTO2.getIncreasedTimes().intValue());
//
//    List<OrderIndex> orderIndexes = searchService.getOrderIndexByOrderId(shopId, washBeautyOrderDTO.getId(), OrderTypes.WASH_BEAUTY, OrderStatus.WASH_SETTLED, w.getCustomerId());
//    Assert.assertEquals(1, orderIndexes.size());
//    Assert.assertEquals("洗美工人1,洗美工人3,洗美工人2", orderIndexes.get(0).getServiceWorker());
//
//	  //作废
//	  model.clear();
//	  washBeautyController.washBeautyOrderRepeal(model, request, washBeautyOrderDTO.getId(), response);
//	  model.clear();
//	  washBeautyController.getWashBeautyOrder(model,request,response,washBeautyOrderDTO.getId().toString());
//	  washBeautyOrderDTO = (WashBeautyOrderDTO)model.get("washBeautyOrderDTO");
//	  Assert.assertEquals(OrderStatus.WASH_REPEAL,washBeautyOrderDTO.getStatus());
//	  List<OrderIndexDTO> orderIndexDTOs = searchService.getOrderIndexDTOByOrderId(shopId,washBeautyOrderDTO.getId());
//	  Assert.assertEquals(1,orderIndexDTOs.size());
//	  Assert.assertEquals(OrderStatus.WASH_REPEAL,orderIndexDTOs.get(0).getOrderStatus());
//	  WashOrderSavedEvent washOrderSavedEvent = (WashOrderSavedEvent)request.getAttribute("UNIT_TEST"); //单元测试
//	  while (washOrderSavedEvent.mockFlag()) {
//		  Thread.sleep(200L);
//	  }
//	  List<OrderIndexDTO> solrOrderIndexDTOs = orderIndexService.getByOrderId(shopId, washBeautyOrderDTO.getId());
//	   Assert.assertEquals(1,solrOrderIndexDTOs.size());
//	  Assert.assertEquals(OrderStatus.WASH_REPEAL,solrOrderIndexDTOs.get(0).getOrderStatus());
//	  //复制
//	  model.clear();
//	  washBeautyController.washBeautyOrderCopy(model,request,washBeautyOrderDTO.getId());
//	  washBeautyOrderDTO = (WashBeautyOrderDTO)model.get("washBeautyOrderDTO");
//	  Assert.assertNull(washBeautyOrderDTO.getId());
//	  Assert.assertNull(washBeautyOrderDTO.getStatus());
//	  Assert.assertEquals(2,washBeautyOrderDTO.getWashBeautyOrderItemDTOs().length);
//	  Assert.assertEquals(35d,washBeautyOrderDTO.getTotal(),0.0001);
  }

  @Test
  public void testConcurrentWash() throws Exception{

  }
}
