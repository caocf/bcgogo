package com.bcgogo.txn;

import com.bcgogo.AbstractTest;
import com.bcgogo.common.Pager;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.search.dto.InventorySearchIndexDTO;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.InventoryLimitDTO;
import com.bcgogo.txn.dto.MemcacheInventorySumDTO;
import com.bcgogo.txn.dto.MemcacheLimitDTO;
import com.bcgogo.txn.service.RFTxnService;
import com.bcgogo.utils.RfTxnConstant;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.ui.ModelMap;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 12-6-11
 * Time: 上午9:05
 * To change this template use File | Settings | File Templates.
 */
public class InventoryServiceTest extends AbstractTest {

  @Before
  public void setUp() throws Exception {
    txnController = new TxnController();
    request = new MockHttpServletRequest();
    response = new MockHttpServletResponse();
    rfiTxnService = ServiceManager.getService(RFTxnService.class);
    goodsStorageController = new GoodStorageController();
    initTxnControllers(goodsStorageController);
  }

  /**
   * 1,设置单个商品的上下限，
   * 2，设置多个商品的上下限
   * 3,查询商品上下限的具体条目
   * @throws Exception
   */
  @Test
  public void setInventoryLimitTest() throws Exception {
    Long shopId = createShop();
    ModelMap model = new ModelMap();
    String productName = "ProductName_Test_";
    request.getSession().setAttribute("shopId", shopId);
    flushAllMemCache();
    ProductDTO[] productDTOs = new ProductDTO[20];
    for (int i = 0; i < 20; i++) {
      productDTOs[i] = new ProductDTO();
      productDTOs[i] = createProductDTO(productName + i, null, null, null, null, null, null, null);
      addInventory(shopId, productDTOs[i], i * 1.0, i * 10.0 + 1, "个");
    }
    //设置单个商品的上下限

    txnController.updateSingleLimit(request, response, productDTOs[0].getProductLocalInfoId(), "2", "5");
    MemcacheLimitDTO memcacheLimitDTO = iInventoryService.getMemcacheLimitDTO(shopId);
    Assert.assertEquals(new Integer(1), memcacheLimitDTO.getCurrentLowerLimitAmount());
    Assert.assertEquals(new Integer(0), memcacheLimitDTO.getCurrentUpperLimitAmount());

    //设置多个商品的上下限
    InventoryLimitDTO inventoryLimitDTO = new InventoryLimitDTO();
    inventoryLimitDTO.setShopId(shopId);
    ProductDTO[] productDTOLimits = new ProductDTO[10];
    for (int i = 0; i < 10; i++) {
      productDTOLimits[i] = productDTOs[i];
      productDTOLimits[i].setLowerLimit(2D);
      productDTOLimits[i].setUpperLimit(5D);
      inventoryLimitDTO.setProductDTOs(productDTOLimits);
    }
    txnController.updateLimit(model, request, response, inventoryLimitDTO);
    memcacheLimitDTO = iInventoryService.getMemcacheLimitDTO(shopId);
    Assert.assertEquals(new Integer(2), memcacheLimitDTO.getCurrentLowerLimitAmount());
    Assert.assertEquals(new Integer(4), memcacheLimitDTO.getCurrentUpperLimitAmount());

    //查询设定上下限的商品明细
    Pager pager = new Pager(1);
    pager.setPageSize(10);
    String sort = RfTxnConstant.sortCommandMap_DB.get("nameAsc");
    String searchConditionStr = RfTxnConstant.sortCommandMap_DB.get("lowerLimit");
    List<InventorySearchIndexDTO> inventorySearchIndexDTOs = searchService.getInventorySearchIndexDTOLimit(shopId, pager, searchConditionStr, null);
    Assert.assertEquals(2, inventorySearchIndexDTOs.size());
    inventorySearchIndexDTOs = searchService.getInventorySearchIndexDTOLimit(shopId, pager, searchConditionStr, sort);
    Assert.assertEquals(2, inventorySearchIndexDTOs.size());
    Assert.assertEquals(productName + 0, inventorySearchIndexDTOs.get(0).getProductName());
    Assert.assertEquals(productName + 1, inventorySearchIndexDTOs.get(1).getProductName());
    searchConditionStr = RfTxnConstant.sortCommandMap_DB.get("upperLimit");
    inventorySearchIndexDTOs = searchService.getInventorySearchIndexDTOLimit(shopId, pager, searchConditionStr, null);
    Assert.assertEquals(4, inventorySearchIndexDTOs.size());
    inventorySearchIndexDTOs = searchService.getInventorySearchIndexDTOLimit(shopId, pager, searchConditionStr, sort);
    Assert.assertEquals(4, inventorySearchIndexDTOs.size());
    Assert.assertEquals(productName + 6, inventorySearchIndexDTOs.get(0).getProductName());
    Assert.assertEquals(productName + 7, inventorySearchIndexDTOs.get(1).getProductName());
    Assert.assertEquals(productName + 8, inventorySearchIndexDTOs.get(2).getProductName());
    Assert.assertEquals(productName + 9, inventorySearchIndexDTOs.get(3).getProductName());

  }

   /**
	 * 1,入库10个商品
	 * 2，库存信息统计
   * @throws Exception
   */
  @Test
  public void inventorySearchTest() throws Exception {
    Long shopId = createShop();
    ModelMap model = new ModelMap();
    String productName = "ProductName_Test_";
    request.getSession().setAttribute("shopId", shopId);
    flushAllMemCache();
    productController.forwardInsertProductData(model,request,"all");
    int testAmount = 10;
    ProductDTO[] productDTOs = new ProductDTO[testAmount];
    double totalSum = 0d;
		double totalAmount = 0d;
    for (int i = 0; i < testAmount; i++) {
      productDTOs[i] = new ProductDTO();
      productDTOs[i] = createProductDTO(productName + i, null, null, null, null, null, null, null);
      productDTOs[i].setShopId(shopId);
      addInventory(shopId, productDTOs[i], i * 1.0, i * 10.0, "个");
      totalSum += i*10.0*i;
	    totalAmount += i;
    }
    MemcacheInventorySumDTO memcacheInventorySumDTO = new MemcacheInventorySumDTO();
    memcacheInventorySumDTO = iInventoryService.getInventorySum(shopId);
    Assert.assertEquals(testAmount,memcacheInventorySumDTO.getInventoryCount(),0.001);
    Assert.assertEquals(totalSum,memcacheInventorySumDTO.getInventorySum(),0.001);
		Assert.assertEquals(totalAmount,memcacheInventorySumDTO.getInventoryProductAmount(),0.0001);
    memcacheInventorySumDTO = new MemcacheInventorySumDTO();
    searchService.countSolrSearchService(productDTOs[0], memcacheInventorySumDTO);
    Assert.assertEquals(testAmount,memcacheInventorySumDTO.getInventoryCount(),0.001);
    Assert.assertEquals(totalSum,memcacheInventorySumDTO.getInventorySum(),0.001);
    Assert.assertEquals(totalAmount,memcacheInventorySumDTO.getInventoryProductAmount(),0.001);
    memcacheInventorySumDTO = new MemcacheInventorySumDTO();
    memcacheInventorySumDTO = iInventoryService.getSearchProductNameInventoryCount(shopId, productName,memcacheInventorySumDTO);
    Assert.assertEquals(testAmount,memcacheInventorySumDTO.getInventoryCount(),0.001);
    Assert.assertEquals(totalSum,memcacheInventorySumDTO.getInventorySum(),0.001);
    Assert.assertEquals(totalAmount,memcacheInventorySumDTO.getInventoryProductAmount(),0.001);
    memcacheInventorySumDTO = new MemcacheInventorySumDTO();
    memcacheInventorySumDTO = iInventoryService.getSearchProductNameInventoryCount(shopId, "1", memcacheInventorySumDTO);
    Assert.assertEquals(1, memcacheInventorySumDTO.getInventoryCount(), 0.001);
    Assert.assertEquals(10, memcacheInventorySumDTO.getInventorySum(), 0.001);
    Assert.assertEquals(1, memcacheInventorySumDTO.getInventoryProductAmount(), 0.001);
    memcacheInventorySumDTO = new MemcacheInventorySumDTO();
    iInventoryService.getSearchProductNameInventoryCount(shopId,"2",memcacheInventorySumDTO);
    Assert.assertEquals(1, memcacheInventorySumDTO.getInventoryCount(), 0.001);
    Assert.assertEquals(2*20, memcacheInventorySumDTO.getInventorySum(), 0.001);
    Assert.assertEquals(2, memcacheInventorySumDTO.getInventoryProductAmount(), 0.001);
  }


}
