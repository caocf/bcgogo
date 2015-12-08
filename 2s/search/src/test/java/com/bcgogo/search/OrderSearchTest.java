package com.bcgogo.search;

import org.junit.Before;
import org.junit.Test;

/**
 * Created by IntelliJ IDEA.
 * User: wjl
 * Date: 12-4-10
 * Time: 上午11:34
 * To change this template use File | Settings | File Templates.
 */
public class OrderSearchTest extends AbstractTest {
  @Before
  public void setUpTest() throws Exception {
  }

  @Test
  public void testAddOrderIndexToSolr() throws Exception {
//    ISearchService searchService = ServiceManager.getService(ISearchService.class);
//    IOrderIndexService orderIndexService = ServiceManager.getService(IOrderIndexService.class);
//    searchService.deleteByQuery("*:*", "order");
//    long startTime = System.currentTimeMillis();
//    OrderIndexDTO orderIndex = new OrderIndexDTO();
//    Long shopId = 10000010001790080L;
//
//    orderIndex.setOrderId(10000L);
//    orderIndex.setShopId(shopId);
//    orderIndex.setOrderType("4");
//    orderIndex.setOrderStatus("2");
//    orderIndex.setOrderContent("施工内容A,商品A,商品B");
//    orderIndex.setOrderTotalAmount(1002D);
//    orderIndex.setCustomerOrSupplierId(10003L);
//    orderIndex.setCustomerOrSupplierName("张先生");
//    orderIndex.setServiceWorker("张三,李四,王武");
//    orderIndex.setArrears(500D);
//    orderIndex.setPaymentTime(System.currentTimeMillis());
//    orderIndex.setVehicle("苏E55489");
//    orderIndex.setContactNum("13514565896");
//    orderIndex.setCreationDate(startTime);
//
//    List<OrderIndexDTO> list = new ArrayList<OrderIndexDTO>();
//    list.add(orderIndex);
//    orderIndexService.addOrderIndexToSolr(list);
//    long endTime = System.currentTimeMillis();
//    QueryResponse response = orderIndexService.queryOrderByServiceWorker(10000010001790080L, "张三",startTime,endTime, 0, 10);
//    SolrDocumentList documentList = response.getResults();
//    Assert.assertEquals(1, documentList.size());
//    System.out.println("成功");
//    QueryResponse response = orderIndexService.queryOrderByServiceWorker(10000010001790080L, "k1", 0, 1345231896390L, 0, 100);
//    System.out.println("solr保存成功" + response.toString());
  }
}
