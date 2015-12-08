package com.bcgogo.product;

import com.bcgogo.AbstractTest;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.product.dto.ProductLocalInfoDTO;
import com.bcgogo.product.service.IProductService;
import com.bcgogo.product.service.IProductSolrService;
import com.bcgogo.search.model.InventorySearchIndex;
import com.bcgogo.search.service.ISearchService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.InventoryDTO;
import com.bcgogo.txn.service.ITxnService;
import com.bcgogo.txn.service.solr.IProductSolrWriterService;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;


public class ProductTest extends AbstractTest {

  @Before
  public void setUpTest() throws Exception {
  }

  @Test
  public void testBrandTest() throws Exception {
    ProductDTO productDTO = new ProductDTO();
    productDTO.setBrand("依维柯");
    productDTO.setKindId(33L);
    productDTO.setMemo("备注");
    productDTO.setMfr("奔驰");
    productDTO.setMfrEn("benz");
    productDTO.setModel("型号");
    productDTO.setName("名称");
    productDTO.setNameEn("name");
    productDTO.setOrigin("江苏");
    productDTO.setOriginNo(77);
    productDTO.setShopId(88L);
    productDTO.setSpec("规格");
    productDTO.setState(99L);
    productDTO.setUnit("计量");

    IProductService productService = ServiceManager.getService(IProductService.class);
    ProductDTO fDTO = productService.createProduct(productDTO);

    ProductDTO otherBrandDTO = productService.getProductById(fDTO.getId(), productDTO.getShopId());

    assertEquals("依维柯", otherBrandDTO.getBrand());
    assertEquals("33", otherBrandDTO.getKindId() + "");
    assertEquals("备注", otherBrandDTO.getMemo());
    assertEquals("奔驰", otherBrandDTO.getMfr());
    assertEquals("benz", otherBrandDTO.getMfrEn());
    assertEquals("型号", otherBrandDTO.getModel());

    assertEquals("名称", otherBrandDTO.getName());
    assertEquals("name", otherBrandDTO.getNameEn());
    assertEquals("江苏", otherBrandDTO.getOrigin());
    assertEquals("77", otherBrandDTO.getOriginNo() + "");
    assertEquals("88", otherBrandDTO.getShopId() + "");
    assertEquals("规格", otherBrandDTO.getSpec());
    assertEquals("99", otherBrandDTO.getState() + "");
    assertEquals("计量", otherBrandDTO.getUnit());

  }

  @Test
  public void testReIndexForSolr() throws Exception {
    IProductSolrService productSolrService = ServiceManager.getService(IProductSolrService.class);
    productSolrService.reindexProductForSolr(1l);
  }

  @Test
  public void testReindexProductInventory() throws Exception {
    IProductService productService = ServiceManager.getService(IProductService.class);
    IProductSolrService productSolrService = ServiceManager.getService(IProductSolrService.class);
    IProductSolrWriterService productSolrWriterService = ServiceManager.getService(IProductSolrWriterService.class);

    ISearchService searchService = ServiceManager.getService(ISearchService.class);
    Long shopId = createShop();
    String productName = "导航仪";
    ProductDTO productDTO = new ProductDTO();
    productDTO.setShopId(shopId);
    productDTO.setName(productName);
    productDTO.setBrand("佳航");
    productDTO.setSpec("52/458");
    productDTO.setModel("TCO");
    productDTO.setProductVehicleBrand("梅赛德斯奔驰");
    productDTO.setProductVehicleModel("S600");
    productDTO.setProductVehicleYear("2012");
    productDTO.setProductVehicleEngine("2.0L");
    productDTO.setProductVehicleStatus(3);
    productDTO.setPrice(300.0);
    productDTO.setPurchasePrice(400D);
    productService.saveNewProduct(productDTO);
    productSolrWriterService.createProductSolrIndex(shopId,productDTO.getProductLocalInfoId());
    QueryResponse response = searchService.queryProductByQueryString("product_name:" + productName, 10);
    assertEquals(1, response.getResults().size());

    SolrDocument document = response.getResults().get(0);
    Long productId = Long.parseLong(document.getFirstValue("id") + "");
    ProductDTO pDTO = productService.getProductById(productId, shopId);
    ProductLocalInfoDTO productLocalInfoDTO = productService.getProductLocalInfoByProductId(productId, shopId);
    assertEquals("导航仪", pDTO.getName());

    InventorySearchIndex inventorySearchIndex = new InventorySearchIndex();
    inventorySearchIndex.setShopId(shopId);
    inventorySearchIndex.setProductId(productLocalInfoDTO.getId());
    inventorySearchIndex.setEditDate(System.currentTimeMillis());
    inventorySearchIndex.setProductName(productDTO.getName());
    inventorySearchIndex.setProductBrand(productDTO.getBrand());
    inventorySearchIndex.setProductSpec(productDTO.getSpec());
    inventorySearchIndex.setProductModel(productDTO.getModel());
    inventorySearchIndex.setBrand(productDTO.getProductVehicleBrand());
    inventorySearchIndex.setModel(productDTO.getProductVehicleModel());
    inventorySearchIndex.setYear(productDTO.getProductVehicleYear());
    inventorySearchIndex.setEngine(productDTO.getProductVehicleEngine());
    inventorySearchIndex.setProductVehicleStatus(productDTO.getProductVehicleStatus());
    inventorySearchIndex.setAmount(40D);
    inventorySearchIndex.setParentProductId(productId);
    inventorySearchIndex.setPurchasePrice(productDTO.getPurchasePrice());

    List<InventorySearchIndex> list = new ArrayList<InventorySearchIndex>();
    list.add(inventorySearchIndex);
    searchService.addOrUpdateInventorySearchIndexWithList(list);
    productSolrWriterService.reCreateProductSolrIndex(shopId, 2000);

    response = searchService.queryProductByQueryString("product_name:" + productName, 10);
    assertEquals(1, response.getResults().size());

    double newAmount = 300D;
    double newPurchasePrice = 450D;
    productLocalInfoDTO.setPurchasePrice(newPurchasePrice);
    productService.updateProductLocalInfo(productLocalInfoDTO);

    long newEditDate = System.currentTimeMillis();
    inventorySearchIndex.setAmount(newAmount);
    inventorySearchIndex.setEditDate(newEditDate);
    inventorySearchIndex.setPurchasePrice(newPurchasePrice);

    InventoryDTO inventoryDTO = new InventoryDTO();
    inventoryDTO.setAmount(newAmount);
    inventoryDTO.setId(productLocalInfoDTO.getId());
    inventoryDTO.setShopId(shopId);
    inventoryDTO.setLastStorageTime(newEditDate);
    ServiceManager.getService(ITxnService.class).saveInventory(inventoryDTO);

    list.clear();
    list.add(inventorySearchIndex);
    searchService.addOrUpdateInventorySearchIndexWithList(list);

    productSolrWriterService.createProductSolrIndex(shopId, new Long[]{inventorySearchIndex.getProductId()});
    response = searchService.queryProductByQueryString("product_name:" + productName, 10);
    assertEquals(1, response.getResults().size());
    SolrDocument doc = response.getResults().get(0);
    assertEquals((double) newAmount, doc.getFirstValue("inventory_amount"));
    assertEquals((double) newPurchasePrice, doc.getFirstValue("purchase_price"));
    assertEquals(newEditDate, doc.getFirstValue("storage_time"));


  }

}
