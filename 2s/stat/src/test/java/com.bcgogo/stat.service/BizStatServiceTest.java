package com.bcgogo.stat.service;

import com.bcgogo.AbstractTest;
import com.bcgogo.search.model.InventorySearchIndex;
import com.bcgogo.search.service.ISearchService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.utils.SearchConstant;
import junit.framework.Assert;
import org.junit.Test;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * User: Xiao Jian
 * Date: 12-1-5
 */

public class BizStatServiceTest extends AbstractTest {

  /* @Test
public void testBizStat() {

IBizStatService bizStatService = ServiceManager.getService(IBizStatService.class);

BizStatDTO  bizStatDTO = new BizStatDTO();
bizStatDTO.setShopId(0L);
bizStatDTO.setStatType(BizStatType.CARREPAIR.toString());
bizStatDTO.setStatYear(2012L);
bizStatDTO.setStatMonth(1L);
bizStatDTO.setStatDay(6L);
bizStatDTO.setStatWeek(5L);
bizStatDTO.setStatSum(5.5d);

bizStatDTO = bizStatService.saveBizStat(bizStatDTO);

BizStatDTO dto = bizStatService.getBizStatById(bizStatDTO.getId());
assertEquals(dto.getShopId(), new Long(0L));
assertEquals(dto.getStatType(), BizStatType.CARREPAIR.toString());
assertEquals(dto.getStatYear(), new Long(2012L));
assertEquals(dto.getStatMonth(), new Long(1));
assertEquals(dto.getStatDay(), new Long(6));
assertEquals(dto.getStatWeek(), new Long(5));
assertEquals(dto.getStatSum(), 5.5d, 0.000001d);


SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

try {
  long startTime = simpleDateFormat.parse("2012-01-06 00:00:00").getTime();
  long endTime = simpleDateFormat.parse("2012-01-06 00:00:00").getTime();
} catch (Exception e) {
}
}    */

  @Test
  public void testGetInventoryTotalAmountByShopId() throws Exception {
    ISearchService searchService = ServiceManager.getService(ISearchService.class);
    Random random = new Random(47);
    DecimalFormat df = new DecimalFormat("#.00");
    Long shopId = 1000L;
    double totalAmount = 0;
    List<InventorySearchIndex> list = new ArrayList<InventorySearchIndex>();
    for (int i = 0; i < 10; i++) {
      char c = (char) ('A' + i);
      InventorySearchIndex inventorySearchIndex = new InventorySearchIndex();
      inventorySearchIndex.setShopId(shopId);
      inventorySearchIndex.setProductId(100000L + i);
      inventorySearchIndex.setEditDate(System.currentTimeMillis());
      inventorySearchIndex.setProductName("商品" + c);
      inventorySearchIndex.setProductBrand("品牌" + c);
      inventorySearchIndex.setProductSpec("规格" + c);
      inventorySearchIndex.setProductModel("型号" + c);

      inventorySearchIndex.setBrand("车牌" + c);
      inventorySearchIndex.setModel("车型" + c);
      inventorySearchIndex.setYear("201" + c);
      inventorySearchIndex.setEngine("排量" + c);

      Double dv = Double.parseDouble(df.format(random.nextDouble())) * 100;
      inventorySearchIndex.setAmount(dv);
      inventorySearchIndex.setPurchasePrice(dv);
      inventorySearchIndex.setProductVehicleStatus(SearchConstant.PRODUCT_PRODUCTSTATUS_SPECIAL);
      inventorySearchIndex.setParentProductId(200000L + i);

      totalAmount += inventorySearchIndex.getAmount() * inventorySearchIndex.getPurchasePrice();
      list.add(inventorySearchIndex);
    }
    searchService.addOrUpdateInventorySearchIndexWithList(list);

    double totalAmount2 = ServiceManager.getService(IBizStatService.class).getInventoryTotalAmountByShopId(shopId);
    Assert.assertEquals(totalAmount, totalAmount2);
  }
}
