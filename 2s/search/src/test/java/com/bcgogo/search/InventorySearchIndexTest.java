package com.bcgogo.search;

import com.bcgogo.search.dto.InventorySearchIndexDTO;
import com.bcgogo.search.model.InventorySearchIndex;
import com.bcgogo.search.service.ISearchService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.utils.SearchConstant;
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
public class InventorySearchIndexTest extends AbstractTest {
  @Before
  public void setUpTest() throws Exception {
  }

  @Test
  public void testAddOrUpdateInventorySearchIndexWithList() {
    InventorySearchIndex itemIndex = new InventorySearchIndex();
    itemIndex.setShopId(100000L);
    itemIndex.setProductId(20001L);
    itemIndex.setEditDate(System.currentTimeMillis());
    itemIndex.setAmount(20D);
    itemIndex.setProductName("轮胎");
    itemIndex.setProductBrand("米其林");
    itemIndex.setProductSpec("200/452");
    itemIndex.setProductModel("速度(H)");
    itemIndex.setBrand("奔驰");
    itemIndex.setModel("S600");
    itemIndex.setYear("2012");
    itemIndex.setEngine("6.0L");

    InventorySearchIndex itemIndex2 = new InventorySearchIndex();
    itemIndex2.setShopId(100000L);
    itemIndex2.setProductId(30001L);
    itemIndex2.setEditDate(System.currentTimeMillis());
    itemIndex2.setAmount(40D);
    itemIndex2.setProductName("轮胎");
    itemIndex2.setProductBrand("倍耐力");
    itemIndex2.setProductSpec("200/452");
    itemIndex2.setProductModel("速度(H)");
    itemIndex2.setBrand("奔驰");
    itemIndex2.setModel("S600");
    itemIndex2.setYear("2012");
    itemIndex2.setEngine("6.0L");

    List<InventorySearchIndex> inventorySearchIndexList = new ArrayList<InventorySearchIndex>();
    inventorySearchIndexList.add(itemIndex);
    inventorySearchIndexList.add(itemIndex2);

    ISearchService searchService = ServiceManager.getService(ISearchService.class);
    searchService.addOrUpdateInventorySearchIndexWithList(inventorySearchIndexList);

    Long count = searchService.searchInventorySearchIndexCount(itemIndex.getShopId(), null, null, null
        , null, null, null, null, null, null);

    List<InventorySearchIndexDTO> items = searchService.searchInventorySearchIndex(
        itemIndex.getShopId(),
        "轮胎",
        "米其林",
        null,
        null,
        itemIndex.getBrand(),
        itemIndex.getModel(),
        null,
        null,
        null,
        null,
        null
    );

    assertEquals(2, count.intValue());
    assertEquals(1, items.size());
  }

  @Test
  public void testSaveOrUpdateInventorySearchIndexByUpdateInfo() {
    InventorySearchIndex itemIndex = new InventorySearchIndex();
    itemIndex.setShopId(100000L);
    itemIndex.setProductId(20001L);
    itemIndex.setEditDate(System.currentTimeMillis());
    itemIndex.setAmount(20D);
    itemIndex.setProductName("轮胎");
    itemIndex.setProductBrand("米其林");
    itemIndex.setProductSpec("200/452");
    itemIndex.setProductModel("速度(H)");
    itemIndex.setBrand("奔驰");
    itemIndex.setModel("S600");
    itemIndex.setYear("2012");
    itemIndex.setEngine("6.0L");
    itemIndex.setProductVehicleStatus(SearchConstant.PRODUCT_PRODUCTSTATUS_SPECIAL);

    List<InventorySearchIndex> inventorySearchIndexList = new ArrayList<InventorySearchIndex>();
    inventorySearchIndexList.add(itemIndex);

    ISearchService searchService = ServiceManager.getService(ISearchService.class);
    searchService.addOrUpdateInventorySearchIndexWithList(inventorySearchIndexList);

    searchService.saveOrUpdateInventorySearchIndexByUpdateInfo(itemIndex.getShopId(), itemIndex.getProductId(),
        itemIndex.getProductVehicleStatus(), "多款", null, null, null);
  }
}
