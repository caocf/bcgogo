package com.bcgogo.txn;

import com.bcgogo.AbstractTest;
import com.bcgogo.common.Pair;
import com.bcgogo.config.util.MemCacheAdapter;
import com.bcgogo.product.dto.PingyinInfo;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.search.client.SolrClientHelper;
import com.bcgogo.search.dto.SearchConditionDTO;
import com.bcgogo.search.dto.SearchMemoryConditionDTO;
import com.bcgogo.search.model.CurrentUsedProduct;
import com.bcgogo.search.model.CurrentUsedVehicle;
import com.bcgogo.search.model.SearchDaoManager;
import com.bcgogo.search.model.SearchWriter;
import com.bcgogo.search.service.CurrentUsed.ProductCurrentUsedService;
import com.bcgogo.search.service.ISearchService;
import com.bcgogo.search.service.product.ISearchProductService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.bcgogoListener.orderEvent.OrderSavedEvent;
import com.bcgogo.txn.dto.PurchaseInventoryDTO;
import com.bcgogo.txn.dto.PurchaseInventoryItemDTO;
import com.bcgogo.txn.service.solr.ISolrMergeService;
import com.bcgogo.utils.PinyinUtil;
import com.bcgogo.utils.SearchConstant;
import junit.framework.Assert;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.ui.ModelMap;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-5-30
 * Time: 上午9:56
 * To change this template use File | Settings | File Templates.
 */
//author：zhangjuntao 下拉框单元测试
public class MemcacheTest extends AbstractTest {
  public static final Logger LOG = LoggerFactory.getLogger(MemcacheTest.class);

  @Before
  public void setUp() throws Exception {
    txnController = new TxnController();
    goodsStorageController = new GoodStorageController();
    buyController = new RFGoodBuyController();
    request = new MockHttpServletRequest();
    response = new MockHttpServletResponse();
    searchDaoManager = ServiceManager.getService(SearchDaoManager.class);
    initTxnControllers(goodsStorageController);
  }

  //入库单
  private PurchaseInventoryDTO initInventory(Long shopId) {
    ModelMap model = new ModelMap();
    PurchaseInventoryDTO purchaseInventoryDTO = new PurchaseInventoryDTO();
    purchaseInventoryDTO.setShopId(shopId);
    purchaseInventoryDTO.setSupplier("Supplier1");
    purchaseInventoryDTO.setMobile("12345678901");
    purchaseInventoryDTO.setVestDateStr("2012-03-20 12:20");
    purchaseInventoryDTO.setTotal(11D);    //单据总额
    PurchaseInventoryItemDTO[] itemDTOs = new PurchaseInventoryItemDTO[3];

    PurchaseInventoryItemDTO piiDTO1 = new PurchaseInventoryItemDTO();
    piiDTO1.setProductName("商品1");
    piiDTO1.setBrand("品牌1");
    piiDTO1.setVehicleBrand("车辆品牌1");
    piiDTO1.setAmount(25d);
    piiDTO1.setPurchasePrice(1d);
    piiDTO1.setRecommendedPrice(5D);
    piiDTO1.setTotal(11d);
    piiDTO1.setUnit("箱");
    itemDTOs[0] = piiDTO1;

    PurchaseInventoryItemDTO piiDTO2 = new PurchaseInventoryItemDTO();
    piiDTO2.setProductName("轮胎");
    piiDTO2.setBrand("品牌2");
    piiDTO2.setVehicleBrand("车辆品牌2");
    piiDTO2.setAmount(25d);
    piiDTO2.setPurchasePrice(1d);
    piiDTO2.setRecommendedPrice(5D);
    piiDTO2.setTotal(11d);
    piiDTO2.setUnit("箱");
    itemDTOs[1] = piiDTO2;

    PurchaseInventoryItemDTO piiDTO3 = new PurchaseInventoryItemDTO();
    piiDTO3.setProductName("轮胎");
    piiDTO3.setBrand("品牌3");
    piiDTO3.setVehicleBrand("车辆品牌3");
    piiDTO3.setAmount(25d);
    piiDTO3.setPurchasePrice(1d);
    piiDTO3.setRecommendedPrice(5D);
    piiDTO3.setTotal(11d);
    piiDTO3.setUnit("箱");
    itemDTOs[2] = piiDTO3;
    purchaseInventoryDTO.setItemDTOs(itemDTOs);
    goodsStorageController.savePurchaseInventory(model, purchaseInventoryDTO, request,response);
    OrderSavedEvent orderSavedEvent = (OrderSavedEvent) request.getAttribute("UNIT_TEST");
    while (orderSavedEvent.mockFlag()) {
      try {
        Thread.sleep(1000);
      } catch (Exception e) {
        LOG.error(e.getMessage(), e);
      }
    }
    return purchaseInventoryDTO;
  }

  //入库单 下拉列表
  @Test
  public void testCreatePurchaseInventory() throws Exception {
    MemCacheAdapter.delete("testdsf");
    ProductCurrentUsedService pcus = new ProductCurrentUsedService();
    flushAllMemCache();
    Long shopId = createShop();
    request.getSession().setAttribute("shopId", shopId);
    initInventory(shopId);
     SearchConditionDTO searchConditionDTO = new SearchConditionDTO();
      searchConditionDTO.setShopId(shopId);
      searchConditionDTO.setSearchField(SearchConstant.PRODUCT_BRAND);
      //memcache中没有值 产品品牌
      List<CurrentUsedProduct> currentUsedProductList = (List<CurrentUsedProduct>) MemCacheAdapter.get(pcus.getMemCacheKey(new SearchMemoryConditionDTO(searchConditionDTO)));
      Assert.assertEquals(3, currentUsedProductList.size());
      //数据库中有值
      SearchWriter writer = searchDaoManager.getWriter();
      currentUsedProductList = writer.getCurrentUsedProduct(new SearchMemoryConditionDTO(searchConditionDTO));
      Assert.assertEquals(3, currentUsedProductList.size());

      searchConditionDTO.setSearchField(SearchConstant.PRODUCT_NAME);
      //memcache中没有值   品名
      currentUsedProductList = (List<CurrentUsedProduct>) MemCacheAdapter.get(pcus.getMemCacheKey(new SearchMemoryConditionDTO(searchConditionDTO)));
      Assert.assertEquals(2, currentUsedProductList.size());
      //数据库中有值
      currentUsedProductList = writer.getCurrentUsedProduct(new SearchMemoryConditionDTO(searchConditionDTO));
      Assert.assertEquals(2, currentUsedProductList.size());

      searchConditionDTO.setSearchField("vehicle_" + SearchConstant.VEHICLE_BRAND);   //todo 临时
      //memcache中没有值   车辆品牌
      List<CurrentUsedVehicle> currentUsedVehicleList = (List<CurrentUsedVehicle>) MemCacheAdapter.get(pcus.getMemCacheKey(new SearchMemoryConditionDTO(searchConditionDTO)));
      Assert.assertEquals(3, currentUsedVehicleList.size());
      //数据库中有值
      currentUsedVehicleList = writer.getCurrentUsedVehicle(searchConditionDTO);
      Assert.assertEquals(3, currentUsedVehicleList.size());
  }

  //入库单 下拉列表
  @Deprecated
  public void testRecentChangedProduct() throws Exception {
    Long shopId = createShop();
    request.getSession().setAttribute("shopId", shopId);
    initInventory(shopId);
    OrderSavedEvent orderSavedEvent = (OrderSavedEvent) request.getAttribute("UNIT_TEST");
    Map<Long, Pair<Long, Boolean>> recentChangedProductMap = productCurrentUsedService.getRecentChangedProductFromMemory(shopId);
    Assert.assertEquals(3, recentChangedProductMap.size());
    for (Map.Entry<Long, Pair<Long, Boolean>> entry : recentChangedProductMap.entrySet()) {
      Assert.assertEquals(false, entry.getValue().getValue().booleanValue());
      System.out.println(new Date(entry.getValue().getKey()).toString());
      System.out.println(new Date(System.currentTimeMillis()).toString());
      System.out.println("--------" + (System.currentTimeMillis() - entry.getValue().getKey()));
      Assert.assertEquals(true, System.currentTimeMillis() - entry.getValue().getKey() <= 10 * 1000);
    }
    try {
      Thread.sleep(15 * 1000);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }

    for (Map.Entry<Long, Pair<Long, Boolean>> entry : recentChangedProductMap.entrySet()) {
      Assert.assertEquals(false, entry.getValue().getValue().booleanValue());
      Assert.assertEquals(false, System.currentTimeMillis() - entry.getValue().getKey() <= 10 * 1000);
    }
    initInventory2(shopId);

    recentChangedProductMap = productCurrentUsedService.getRecentChangedProductFromMemory(shopId);
    Assert.assertEquals(2, recentChangedProductMap.size());
    for (Map.Entry<Long, Pair<Long, Boolean>> entry : recentChangedProductMap.entrySet()) {
      Assert.assertEquals(false, entry.getValue().getValue().booleanValue());
      Assert.assertEquals(true, System.currentTimeMillis() - entry.getValue().getKey() <= 10 * 1000);
    }
  }


  //入库单
  private PurchaseInventoryDTO initInventory2(Long shopId) {
    ModelMap model = new ModelMap();
    PurchaseInventoryDTO purchaseInventoryDTO = new PurchaseInventoryDTO();
    purchaseInventoryDTO.setShopId(shopId);
    purchaseInventoryDTO.setSupplier("Supplier1");
    purchaseInventoryDTO.setMobile("12345678901");
    purchaseInventoryDTO.setTotal(11D);    //单据总额
    PurchaseInventoryItemDTO[] itemDTOs = new PurchaseInventoryItemDTO[2];

    PurchaseInventoryItemDTO piiDTO1 = new PurchaseInventoryItemDTO();
    piiDTO1.setProductName("商品1");
    piiDTO1.setBrand("品牌1");
    piiDTO1.setVehicleBrand("车辆品牌1");
    piiDTO1.setAmount(25d);
    piiDTO1.setPurchasePrice(1d);
    piiDTO1.setRecommendedPrice(5D);
    piiDTO1.setTotal(11d);
    piiDTO1.setUnit("箱");
    itemDTOs[0] = piiDTO1;

    PurchaseInventoryItemDTO piiDTO2 = new PurchaseInventoryItemDTO();
    piiDTO2.setProductName("商品2");
    piiDTO2.setBrand("品牌2");
    piiDTO2.setVehicleBrand("车辆品牌2");
    piiDTO2.setAmount(25d);
    piiDTO2.setPurchasePrice(1d);
    piiDTO2.setRecommendedPrice(5D);
    piiDTO2.setTotal(11d);
    piiDTO2.setUnit("箱");
    itemDTOs[1] = piiDTO2;

    purchaseInventoryDTO.setItemDTOs(itemDTOs);
    goodsStorageController.savePurchaseInventory(model, purchaseInventoryDTO, request,response);
    OrderSavedEvent orderSavedEvent = (OrderSavedEvent) request.getAttribute("UNIT_TEST");
    while (orderSavedEvent.mockFlag()) {
      try {
        Thread.sleep(1000);
      } catch (Exception e) {
        LOG.error(e.getMessage(), e);
        ;
      }
    }
    return purchaseInventoryDTO;
  }

 @Deprecated
  public void testMergeCacheProductDTO() throws Exception {
    ISearchProductService searchProductService = ServiceManager.getService(ISearchProductService.class);

    Long shopId = createShop();
    request.getSession().setAttribute("shopId", shopId);
    PurchaseInventoryDTO purchaseInventoryDTO = initInventory(shopId);
    PurchaseInventoryItemDTO[] itemDTOs = purchaseInventoryDTO.getItemDTOs();
    Map<Long, String> productNameMap = new HashMap<Long, String>();
    for (int i = 0; i < itemDTOs.length; i++) {
      productNameMap.put(itemDTOs[i].getProductId(), itemDTOs[i].getProductName());
    }

    Map<Long, Pair<Long, Boolean>> recentChangedProductMap = productCurrentUsedService.getRecentChangedProductFromMemory(shopId);
    Assert.assertEquals(3, recentChangedProductMap.size());
    Long[] productLocalInfoIds = new Long[recentChangedProductMap.size()];
    int i = 0;
    for (Map.Entry<Long, Pair<Long, Boolean>> entry : recentChangedProductMap.entrySet()) {
      productLocalInfoIds[i] = entry.getKey();
      i++;
    }
    updateInventory(purchaseInventoryDTO);
    initSolrProductDTO(productLocalInfoIds, shopId);

    SearchConditionDTO searchConditionDTO = new SearchConditionDTO();
    searchConditionDTO.setSearchWord("轮");
    searchConditionDTO.setShopId(shopId);
    searchConditionDTO.setStart(0);
    searchConditionDTO.setRows(10);
    List<ProductDTO> res = searchProductService.queryProductWithUnknownField(searchConditionDTO).getProducts();
	  ServiceManager.getService(ISolrMergeService.class).mergeCacheProductDTO(shopId,res);
    Assert.assertEquals(6, res.size());
    for (Map.Entry<Long, String> entry : productNameMap.entrySet()) {
      System.out.println("-----xxxxx------" + entry.getKey() + entry.getValue());
    }
    for (ProductDTO productDTO : res) {
      System.out.println("-----------ProductLocalInfoId：" + productDTO.getProductLocalInfoId());
      System.out.println("-----------productNameMap：" + productNameMap.get(productDTO.getProductLocalInfoId()));
      System.out.println("-----------productDTO:" + productDTO.getName());


      if (productDTO.getProductLocalInfoId().longValue() == 11l) {
        Assert.assertEquals("轮胎", productDTO.getName());
      } else if (productDTO.getProductLocalInfoId().longValue() == 21l) {
        Assert.assertEquals("轮毂", productDTO.getName());
      } else if (productDTO.getProductLocalInfoId().longValue() == 31l) {
        Assert.assertEquals("元宝梁", productDTO.getName());
      } else {
        Assert.assertEquals(productNameMap.get(productDTO.getProductLocalInfoId()), productDTO.getName());
      }
    }
  }

  private void initSolrProductDTO(Long[] productLocalInfoIds, Long shopId) throws Exception {
    ISearchService searchService = ServiceManager.getService(ISearchService.class);
    Collection<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();

    SolrInputDocument doc = new SolrInputDocument();
    doc.addField("id", "1", 1);
    doc.addField("product_name", "轮胎");
    PingyinInfo pingyinInfo = PinyinUtil.getPingyinInfo("轮胎");
    doc.addField("product_name_fl", pingyinInfo.firstLetters);
    doc.addField("product_name_py", pingyinInfo.pingyin);
    doc.addField("product_brand", "正新");
    doc.addField("product_id", productLocalInfoIds[0]);

    doc.addField("shop_id", shopId);
    docs.add(doc);

    doc = new SolrInputDocument();
    doc.addField("id", "3", 1);
    doc.addField("product_name", "轮胎");
    pingyinInfo = PinyinUtil.getPingyinInfo("轮胎");
    doc.addField("product_name_fl", pingyinInfo.firstLetters);
    doc.addField("product_name_py", pingyinInfo.pingyin);
    doc.addField("product_brand", "米其林");
    doc.addField("shop_id", shopId);
    doc.addField("purchase_price", 2000);
    doc.addField("product_vehicle_brand", "宝马");
    doc.addField("product_id", productLocalInfoIds[1]);
    docs.add(doc);

    doc = new SolrInputDocument();
    doc.addField("id", "4", 1);
    doc.addField("product_name", "轮胎");
    pingyinInfo = PinyinUtil.getPingyinInfo("轮胎");
    doc.addField("product_name_fl", pingyinInfo.firstLetters);
    doc.addField("product_name_py", pingyinInfo.pingyin);
    doc.addField("product_brand", "米其林");
    doc.addField("shop_id", shopId);
    doc.addField("product_vehicle_brand", "别克");
    doc.addField("purchase_price", 1000);
    doc.addField("product_id", 11l);
    docs.add(doc);

    doc = new SolrInputDocument();
    doc.addField("id", "5", 1);
    doc.addField("product_name", "轮毂");
    pingyinInfo = PinyinUtil.getPingyinInfo("轮毂");
    doc.addField("product_name_fl", pingyinInfo.firstLetters);
    doc.addField("product_name_py", pingyinInfo.pingyin);
    doc.addField("product_brand", "米其林");
    doc.addField("shop_id", shopId);
    doc.addField("purchase_price", 2500);
    doc.addField("product_vehicle_brand", "宝马");
    doc.addField("product_id", 21l);
    docs.add(doc);

    doc = new SolrInputDocument();
    doc.addField("id", "2", 1);
    doc.addField("product_name", "元宝梁");
    pingyinInfo = PinyinUtil.getPingyinInfo("元宝梁");
    doc.addField("product_name_fl", pingyinInfo.firstLetters);
    doc.addField("product_name_py", pingyinInfo.pingyin);
    doc.addField("product_brand", "正");
    doc.addField("product_id", 31l);
    doc.addField("shop_id", shopId);

    docs.add(doc);

    SolrClientHelper.getProductSolrClient().addDocs(docs);
  }


  //入库单
  private PurchaseInventoryDTO updateInventory(PurchaseInventoryDTO purchaseInventoryDTO) {
    ModelMap model = new ModelMap();
    PurchaseInventoryItemDTO[] itemDTOs = purchaseInventoryDTO.getItemDTOs();
    PurchaseInventoryItemDTO[] newItemDTOs = new PurchaseInventoryItemDTO[2];
    PurchaseInventoryItemDTO piiDTO1 = itemDTOs[0];
    piiDTO1.setProductName("商品1");
    piiDTO1.setBrand("品牌1");
    piiDTO1.setVehicleBrand("车辆品牌1");
    piiDTO1.setAmount(25d);
    piiDTO1.setPurchasePrice(1d);
    piiDTO1.setRecommendedPrice(5D);
    piiDTO1.setTotal(11d);
    piiDTO1.setUnit("箱");
    newItemDTOs[0] = piiDTO1;

    PurchaseInventoryItemDTO piiDTO2 = itemDTOs[1];
    piiDTO2.setProductName("轮胎");
    piiDTO2.setBrand("品牌2");
    piiDTO2.setVehicleBrand("车辆品牌2");
    piiDTO2.setAmount(25d);
    piiDTO2.setPurchasePrice(1d);
    piiDTO2.setRecommendedPrice(5D);
    piiDTO2.setTotal(11d);
    piiDTO2.setUnit("箱");
    newItemDTOs[1] = piiDTO2;

    purchaseInventoryDTO.setItemDTOs(newItemDTOs);
    goodsStorageController.savePurchaseInventory(model, purchaseInventoryDTO, request,response);
    OrderSavedEvent orderSavedEvent = (OrderSavedEvent) request.getAttribute("UNIT_TEST");
    while (orderSavedEvent.mockFlag()) {
      try {
        Thread.sleep(1000);
      } catch (Exception e) {
        LOG.error(e.getMessage(), e);
        ;
      }
    }
    return purchaseInventoryDTO;
  }
}
