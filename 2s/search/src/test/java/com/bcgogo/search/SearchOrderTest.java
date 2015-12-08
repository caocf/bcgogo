package com.bcgogo.search;

import com.bcgogo.enums.ConsumeType;
import com.bcgogo.enums.ItemTypes;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.enums.shop.ShopKind;
import com.bcgogo.search.client.SolrClientHelper;
import com.bcgogo.search.dto.ItemIndexDTO;
import com.bcgogo.search.dto.OrderSearchConditionDTO;
import com.bcgogo.search.dto.OrderSearchResultListDTO;
import com.bcgogo.search.service.ISearchService;
import com.bcgogo.search.service.order.ISearchOrderService;
import com.bcgogo.service.ServiceManager;
import junit.framework.Assert;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by IntelliJ IDEA.
 * User: caiweili
 * Date: 8/3/12
 * Time: 1:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class SearchOrderTest extends AbstractTest {
  @Before
  public void setUpTest() throws Exception {
  }


  @Test
  public void searchOrderTest() throws Exception {

    ISearchOrderService searchOrdertService = ServiceManager.getService(ISearchOrderService.class);

    SolrClientHelper.getProductSolrClient().deleteAll();

    Collection<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
    SolrInputDocument doc = new SolrInputDocument();
    doc.addField("id", "1", 1);
    doc.addField("product_name", "轮胎");
    doc.addField("product_brand", "正新");
    doc.addField("product_spec", "1234");
    doc.addField("product_spec", "1235");
    doc.addField("shop_id", 100);
    doc.addField("shop_kind",ShopKind.TEST);
    doc.addField("order_total_amount", 30);
    doc.addField("product_name", "机油");
    doc.addField("product_brand", "桥牌");
    doc.addField("service_worker", "李小龙");
    doc.addField("service_worker", "李大龙");
    doc.addField("created_time", 1341100800000l);//7/1/2012
    doc.addField("order_type", OrderTypes.REPAIR);
    docs.add(doc);

    doc = new SolrInputDocument();
    doc.addField("id", "2", 1);
    doc.addField("product_name", "轮胎");
    doc.addField("product_brand", "米其林");
    doc.addField("product_spec", "1234");
    doc.addField("product_spec", "1235");
    doc.addField("shop_id", 100);
    doc.addField("shop_kind",ShopKind.TEST);
    doc.addField("order_total_amount", 300);
    doc.addField("product_name", "机油");
    doc.addField("product_brand", "卡牌");
    doc.addField("service_worker", "李龙");
    doc.addField("service_worker", "李大龙");
    doc.addField("created_time", 1343779200000l);//8/1/2012
    doc.addField("order_type", OrderTypes.REPAIR);
    docs.add(doc);

    doc = new SolrInputDocument();
    doc.addField("id", "3", 1);
    doc.addField("product_name", "活化石");
    doc.addField("product_brand", "大众");
    doc.addField("product_spec", "124");
    doc.addField("product_spec", "135");
    doc.addField("shop_id", 100);
    doc.addField("shop_kind",ShopKind.TEST);
    doc.addField("order_total_amount", 3000);
    doc.addField("product_name", "机油");
    doc.addField("product_brand", "卡牌");
    doc.addField("service_worker", "李中龙");
    doc.addField("service_worker", "李大龙");
    doc.addField("created_time", 1343779200000l);//8/1/2012
    doc.addField("order_type", OrderTypes.REPAIR);
    docs.add(doc);

    SolrClientHelper.getOrderSolrClient().addDocs(docs);

    OrderSearchConditionDTO conditionDTO = new OrderSearchConditionDTO();
    conditionDTO.setSearchStrategy(new String[]{OrderSearchConditionDTO.SEARCHSTRATEGY_STATS_FACET});
    conditionDTO.setStatsFields(new String[]{"order_total_amount"});
    conditionDTO.setFacetFields(new String[]{"order_type"});
    conditionDTO.setOrderType(new String[]{OrderTypes.REPAIR.toString()});
    conditionDTO.setShopKind(ShopKind.TEST);
    conditionDTO.setProductName("机油");
    conditionDTO.setStartTime(1342310400000l);// 7/15/2012
    conditionDTO.setShopId(100l);
    conditionDTO.setRowStart(0);
    conditionDTO.setPageRows(10);
    OrderSearchResultListDTO rsp = searchOrdertService.queryOrders(conditionDTO);
    rsp.getOrders();
    assertEquals(2, rsp.getOrders().size());

    String[] workers = new String[1];
    workers[0] = "李中龙";
    conditionDTO.setServiceWorker(workers);
    rsp = searchOrdertService.queryOrders(conditionDTO);
    rsp.getOrders();
    assertEquals(1, rsp.getOrders().size());
    assertEquals("李中龙", rsp.getOrders().get(0).getServiceWorker()[0]);

    conditionDTO.setServiceWorker(null);
    conditionDTO.setAmountUpper(2000d);
    rsp = searchOrdertService.queryOrders(conditionDTO);
    rsp.getOrders();
    assertEquals(1, rsp.getOrders().size());
    Assert.assertEquals(300, (Double) rsp.getOrders().get(0).getAmount(), 0.001);

    conditionDTO.setAmountUpper(null);
    conditionDTO.setSort("order_total_amount desc");
    rsp = searchOrdertService.queryOrders(conditionDTO);
    rsp.getOrders();
    assertEquals(2, rsp.getOrders().size());
    Assert.assertEquals(3000, (Double) rsp.getOrders().get(0).getAmount(), 0.001);

  }


  @Test
  public void searchOrderTestWithGroup() throws Exception {

    ISearchOrderService searchOrderService = ServiceManager.getService(ISearchOrderService.class);

    ISearchService searchService = ServiceManager.getService(ISearchService.class);
    SolrClientHelper.getProductSolrClient().deleteAll();

    Collection<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
    SolrInputDocument doc = new SolrInputDocument();
    doc.addField("id", "1", 1);
    doc.addField("order_type", "INVENTORY");
    doc.addField("product_name", "轮胎");
    doc.addField("product_brand", "正新");
    doc.addField("product_spec", "1234");
    doc.addField("product_spec", "1235");
    doc.addField("shop_id", 100);
    doc.addField("shop_kind", ShopKind.TEST);
    doc.addField("order_total_amount", 30);
    doc.addField("product_name", "机油");
    doc.addField("product_brand", "桥牌");
    doc.addField("product", "机油 桥牌");
    doc.addField("product", "轮胎 正新");
    doc.addField("service_worker", "李小龙");
    doc.addField("service_worker", "李大龙");
    doc.addField("created_time", 1341100800000l);//7/1/2012
    docs.add(doc);

    doc = new SolrInputDocument();
    doc.addField("id", "2", 1);
    doc.addField("order_type", "INVENTORY");
    doc.addField("product_name", "轮胎");
    doc.addField("product_brand", "米其林");
    doc.addField("product_spec", "1234");
    doc.addField("product_spec", "1235");
    doc.addField("shop_id", 100);
    doc.addField("shop_kind", ShopKind.TEST);
    doc.addField("order_total_amount", 300);
    doc.addField("product_name", "机油");
    doc.addField("product_brand", "卡牌");
    doc.addField("product", "机油 卡牌");
    doc.addField("product", "轮胎 米其林");

    doc.addField("service_worker", "李龙");
    doc.addField("service_worker", "李大龙");
    doc.addField("created_time", 1343779200000l);//8/1/2012
    docs.add(doc);

    doc = new SolrInputDocument();
    doc.addField("id", "3", 1);

    doc.addField("order_type", "PURCHASE");
    doc.addField("product_name", "活化石");
    doc.addField("product_brand", "大众");
    doc.addField("product_spec", "124");
    doc.addField("product_spec", "135");
    doc.addField("shop_id", 100);
    doc.addField("shop_kind", ShopKind.TEST);
    doc.addField("order_total_amount", 3000);
    doc.addField("product_name", "机油");
    doc.addField("product_brand", "卡牌");
    doc.addField("product", "机油 卡牌");
    doc.addField("product", "活化石 大众");
    doc.addField("service_worker", "李中龙");
    doc.addField("service_worker", "李大龙");
    doc.addField("created_time", 1343779200000l);//8/1/2012
    docs.add(doc);

    SolrClientHelper.getOrderSolrClient().addDocs(docs);

    OrderSearchConditionDTO conditionDTO = new OrderSearchConditionDTO();
    conditionDTO.setOrderType(new String[]{"INVENTORY","PURCHASE"});
    conditionDTO.setSearchStrategy(new String[]{OrderSearchConditionDTO.SEARCHSTRATEGY_STATS_FACET});
    conditionDTO.setStatsFields(new String[]{"order_total_amount"});
    conditionDTO.setFacetFields(new String[]{"order_type"});
    conditionDTO.setProductName("机油");
    conditionDTO.setShopId(100l);
    conditionDTO.setShopKind(ShopKind.TEST);
    conditionDTO.setRowStart(0);
    conditionDTO.setPageRows(2);
    conditionDTO.setSort("created_time desc");
    OrderSearchResultListDTO resultListDTO = searchOrderService.queryOrders(conditionDTO);
    //倒序  2条
    assertEquals(2, resultListDTO.getOrders().size());

    //统计一共3条
    assertEquals(2, resultListDTO.getTotalCounts().get("ORDER_TOTAL_AMOUNT_ORDER_TYPE_INVENTORY").longValue());
    assertEquals(330, resultListDTO.getTotalAmounts().get("ORDER_TOTAL_AMOUNT_ORDER_TYPE_INVENTORY"),0.01);

    assertEquals(1, resultListDTO.getTotalCounts().get("ORDER_TOTAL_AMOUNT_ORDER_TYPE_PURCHASE").longValue());
    assertEquals(3000, resultListDTO.getTotalAmounts().get("ORDER_TOTAL_AMOUNT_ORDER_TYPE_PURCHASE"),0.01);


    String[] orderType = {"INVENTORY"};
    conditionDTO.setOrderType(orderType);
    conditionDTO.setSearchStrategy(new String[]{OrderSearchConditionDTO.SEARCHSTRATEGY_STATS_FACET,OrderSearchConditionDTO.SEARCHSTRATEGY_CURRENT_PAGE_STATS});
    conditionDTO.setStatsFields(new String[]{"order_total_amount"});
    conditionDTO.setFacetFields(new String[]{"order_type"});
    conditionDTO.setPageStatsFields(new String[]{"order_total_amount"});
    conditionDTO.setRowStart(0);
    conditionDTO.setPageRows(3);
    conditionDTO.setShopKind(ShopKind.TEST);
    //
    resultListDTO = searchOrderService.queryOrders(conditionDTO);
    assertEquals(2, resultListDTO.getOrders().size());
    assertEquals(330, resultListDTO.getCurrentPageTotalAmounts().get("order_total_amount"),0.01);

    //统计一共2条
    assertEquals(2, resultListDTO.getTotalCounts().get("ORDER_TOTAL_AMOUNT_ORDER_TYPE_INVENTORY").longValue());
    assertEquals(330, resultListDTO.getTotalAmounts().get("ORDER_TOTAL_AMOUNT_ORDER_TYPE_INVENTORY"),0.01);

    assertEquals(null, resultListDTO.getTotalCounts().get("ORDER_TOTAL_AMOUNT_ORDER_TYPE_PURCHASE"));
    assertEquals(null, resultListDTO.getTotalAmounts().get("ORDER_TOTAL_AMOUNT_ORDER_TYPE_PURCHASE"));

    conditionDTO = new OrderSearchConditionDTO();
    conditionDTO.setOrderType(new String[]{"INVENTORY","PURCHASE"});
    conditionDTO.setSearchStrategy(new String[]{OrderSearchConditionDTO.SEARCHSTRATEGY_STATS_FACET,OrderSearchConditionDTO.SEARCHSTRATEGY_CURRENT_PAGE_STATS});
    conditionDTO.setStatsFields(new String[]{"order_total_amount"});
    conditionDTO.setFacetFields(new String[]{"order_type"});
    conditionDTO.setPageStatsFields(new String[]{"order_total_amount"});
    conditionDTO.setShopId(100l);
    conditionDTO.setRowStart(0);
    conditionDTO.setPageRows(2);
    conditionDTO.setShopKind(ShopKind.TEST);
    conditionDTO.setSort("created_time desc");
    //
    resultListDTO = searchOrderService.queryOrders(conditionDTO);
    assertEquals(2, resultListDTO.getOrders().size());
    assertEquals(3300, resultListDTO.getCurrentPageTotalAmounts().get("order_total_amount"),0.01);

    //统计一共3条
    assertEquals(2, resultListDTO.getTotalCounts().get("ORDER_TOTAL_AMOUNT_ORDER_TYPE_INVENTORY").longValue());
    assertEquals(330, resultListDTO.getTotalAmounts().get("ORDER_TOTAL_AMOUNT_ORDER_TYPE_INVENTORY"),0.01);

    assertEquals(1, resultListDTO.getTotalCounts().get("ORDER_TOTAL_AMOUNT_ORDER_TYPE_PURCHASE").longValue());
    assertEquals(3000, resultListDTO.getTotalAmounts().get("ORDER_TOTAL_AMOUNT_ORDER_TYPE_PURCHASE"),0.01);




    List<ItemIndexDTO> itemIndexDTOs = new ArrayList<ItemIndexDTO>();
    ItemIndexDTO itemIndexDTO = new ItemIndexDTO();
    itemIndexDTO.setItemId(123456l);
    itemIndexDTO.setItemName("机油");
    itemIndexDTO.setItemBrand("壳牌");
    itemIndexDTO.setItemModel("4L");
    itemIndexDTO.setVehicleBrand("别克");
    itemIndexDTO.setVehicleModel("君威");
    itemIndexDTO.setItemPrice(500.0);
    itemIndexDTO.setItemCount(1.0);
    itemIndexDTO.setItemType(ItemTypes.MATERIAL);
    itemIndexDTOs.add(itemIndexDTO);

    itemIndexDTO = new ItemIndexDTO();
    itemIndexDTO.setItemId(123456212l);
    itemIndexDTO.setItemName("打蜡");
    itemIndexDTO.setItemType(ItemTypes.SERVICE);
    itemIndexDTOs.add(itemIndexDTO);
    
    itemIndexDTO = new ItemIndexDTO();
    itemIndexDTO.setItemId(12345414146l);
    itemIndexDTO.setItemName("轮胎");
    itemIndexDTO.setItemBrand("马牌");
    itemIndexDTO.setItemModel("225/17");
    itemIndexDTO.setVehicleBrand("别克");
    itemIndexDTO.setVehicleModel("君威");
    itemIndexDTO.setItemPrice(700.0);
    itemIndexDTO.setItemCount(4.0);
    itemIndexDTO.setItemType(ItemTypes.MATERIAL);
    itemIndexDTOs.add(itemIndexDTO);

    itemIndexDTO = new ItemIndexDTO();
    itemIndexDTO.setItemId(12345612464l);
    itemIndexDTO.setItemName("换轮胎");
    itemIndexDTO.setItemType(ItemTypes.SERVICE);
    itemIndexDTO.setConsumeType(ConsumeType.TIMES);
    itemIndexDTOs.add(itemIndexDTO);

    doc = new SolrInputDocument();
    doc.addField("id", "3", 1);

    doc.addField("order_type", "REPAIR");
    doc.addField("customer_or_supplier_name", "小秋");
    doc.addField("product_name", "机油");
    doc.addField("product_brand", "壳牌");
    doc.addField("product_spec", "124");
    doc.addField("product_spec", "135");
    doc.addField("shop_id", 100);
    doc.addField("order_total_amount", 3000);
    doc.addField("product_name", "机油");
    doc.addField("product_brand", "卡牌");
    doc.addField("product", "机油 卡牌");
    doc.addField("product", "活化石 大众");
    doc.addField("service_worker", "李中龙");
    doc.addField("service_worker", "李大龙");
    doc.addField("created_time", 1343779200000l);//8/1/2012
    for(ItemIndexDTO aItemIndexDTO: itemIndexDTOs){
      doc.addField("item_detail", aItemIndexDTO.generateItemDetail());//8/1/2012
    }
    docs = new ArrayList<SolrInputDocument>();
    docs.add(doc);

    SolrClientHelper.getOrderSolrClient().addDocs(docs);

    conditionDTO = new OrderSearchConditionDTO();
    conditionDTO.setShopId(100l);
    conditionDTO.setCustomerOrSupplierName("小秋");
    conditionDTO.setOrderType(new String[]{OrderTypes.REPAIR.toString()});
    conditionDTO.setRowStart(0);
    conditionDTO.setPageRows(2);
    resultListDTO = searchOrderService.queryOrders(conditionDTO);
    
    assertEquals(1, resultListDTO.getOrders().size());
    assertEquals(4, resultListDTO.getOrders().get(0).getItemIndexDTOs().size());
  }
}
