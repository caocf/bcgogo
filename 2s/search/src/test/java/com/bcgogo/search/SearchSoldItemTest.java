package com.bcgogo.search;

import com.bcgogo.enums.ItemTypes;
import com.bcgogo.enums.OrderStatus;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.search.dto.ItemIndexDTO;
import com.bcgogo.search.model.ItemIndex;
import com.bcgogo.search.service.ISearchService;
import com.bcgogo.service.ServiceManager;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;


/**
 * Created by IntelliJ IDEA.
 * User: caiweili
 * Date: 1/2/12
 * Time: 5:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class SearchSoldItemTest extends AbstractTest {
  @Before
  public void setUpTest() throws Exception {
  }

  @Test
  public void testSearchSoldItems() {
    ItemIndex itemIndex = new ItemIndex();
    itemIndex.setCustomerId(100000l);
    itemIndex.setItemId(1000001l);
    itemIndex.setItemName("tire");
    itemIndex.setItemTypeEnum(ItemTypes.MATERIAL);
    itemIndex.setOrderId(121l);
    itemIndex.setOrderStatusEnum(OrderStatus.REPAIR_DONE);
    itemIndex.setOrderTimeCreated(System.currentTimeMillis());
    itemIndex.setOrderTypeEnum(OrderTypes.REPAIR);
    itemIndex.setShopId(1000l);
    itemIndex.setVehicle("Toyota");
    ISearchService searchService = ServiceManager.getService(ISearchService.class);
    searchService.addOrUpdateItemIndex(itemIndex);
    List<OrderTypes> itemOrderType = new ArrayList<OrderTypes>();
    itemOrderType.add(itemIndex.getOrderTypeEnum());

    ItemIndexDTO dto = new ItemIndexDTO();
    dto.setShopId(itemIndex.getShopId());
    dto.setOrderId(itemIndex.getOrderId());
    dto.setSelectedOrderTypes(itemOrderType);
    dto.setItemId(itemIndex.getItemId());
    dto.setItemType(itemIndex.getItemTypeEnum());
    List<ItemIndex> items = searchService.searchItemIndex(dto,null,null,null,null);
    assertEquals(1, items.size());
  }

  @Test
  public void testAddOrUpdateItemIndex() {
    ISearchService searchService = ServiceManager.getService(ISearchService.class);

    ItemIndex itemIndex = new ItemIndex();
    itemIndex.setCustomerId(10001L);
    itemIndex.setItemId(5001L);
    itemIndex.setItemName("tire");
    itemIndex.setItemTypeEnum(ItemTypes.MATERIAL);
    itemIndex.setOrderId(25001L);
    itemIndex.setOrderStatusEnum(OrderStatus.REPAIR_DONE);
    itemIndex.setOrderTimeCreated(System.currentTimeMillis());
    itemIndex.setOrderTypeEnum(OrderTypes.REPAIR);
    itemIndex.setShopId(1000l);
    itemIndex.setVehicle("Toyota");

    searchService.addOrUpdateItemIndex(itemIndex);

    ItemIndex itemIndex2 = new ItemIndex();
    itemIndex2.setCustomerId(20001L);
    itemIndex2.setItemId(5001L);
    itemIndex2.setItemName("fire");
    itemIndex2.setItemTypeEnum(ItemTypes.MATERIAL);
    itemIndex2.setOrderId(25001L);
    itemIndex2.setOrderStatusEnum(OrderStatus.REPAIR_DONE);
    itemIndex2.setOrderTimeCreated(System.currentTimeMillis());
    itemIndex2.setOrderTypeEnum(OrderTypes.REPAIR);
    itemIndex2.setShopId(1000l);
    itemIndex2.setVehicle("BENZ");

    searchService.addOrUpdateItemIndex(itemIndex2);


    List<OrderTypes> itemOrderType = new ArrayList<OrderTypes>();
    itemOrderType.add(itemIndex.getOrderTypeEnum());

    ItemIndexDTO dto = new ItemIndexDTO();
    dto.setShopId(itemIndex.getShopId());
    dto.setOrderId(itemIndex.getOrderId());
    dto.setSelectedOrderTypes(itemOrderType);
    dto.setItemId(itemIndex.getItemId());
    dto.setItemType(itemIndex.getItemTypeEnum());
    List<ItemIndex> items = searchService.searchItemIndex(dto,null,null,null,null);
    assertEquals(1, items.size());
  }
}
