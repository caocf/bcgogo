package com.bcgogo.product;

import com.bcgogo.AbstractTest;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.product.dto.ProductLocalInfoDTO;
import com.bcgogo.product.service.IProductService;
import com.bcgogo.search.model.InventorySearchIndex;
import com.bcgogo.search.service.ISearchService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.InventoryDTO;
import com.bcgogo.txn.service.IInventoryService;
import com.bcgogo.txn.service.ITxnService;
import com.bcgogo.txn.service.solr.IProductSolrWriterService;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.junit.Before;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;


public class ProductSearchServiceTest extends AbstractTest {

  @Before
  public void setUpTest() throws Exception {
  }

//  @Test
  public void testBrandTest() throws Exception {
    IProductService productService = ServiceManager.getService(IProductService.class);
    ISearchService searchService = ServiceManager.getService(ISearchService.class);
    IInventoryService inventoryService = ServiceManager.getService(IInventoryService.class);
    Long shopId = createShop();
    String productName = "导航仪";
    ProductDTO productDTO = new ProductDTO();
    productDTO.setShopId(shopId);
    productDTO.setName(productName);
    productDTO.setBrand("佳航");
    productDTO.setSpec("52/458");
    productDTO.setModel("TCO");
    productDTO.setProductVehicleBrand("奔驰");
    productDTO.setProductVehicleModel("S600");
    productDTO.setProductVehicleYear("2012");
    productDTO.setProductVehicleEngine("2.0L");
    productDTO.setProductVehicleStatus(3);
    productDTO.setPrice(300.0);
    productDTO.setPurchasePrice(400D);
    productService.saveNewProduct(productDTO);
    ServiceManager.getService(IProductSolrWriterService.class).createProductSolrIndex(shopId,productDTO.getProductLocalInfoId());
    QueryResponse response = searchService.queryProductByQueryString("product_name:" + productName, 10);
    assertEquals(1, response.getResults().size());

    SolrDocument document = response.getResults().get(0);
    Long productId = Long.parseLong(document.getFirstValue("id") + "");
    ProductDTO pDTO = productService.getProductById(productId,shopId);
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
    inventoryService.addOrUpdateInventorySearchIndexWithList(shopId, list);

    response = searchService.queryProductByQueryString("product_name:" + productName, 10);
    assertEquals(1, response.getResults().size());

    double newPurchasePrice = 450D;
    productLocalInfoDTO.setPurchasePrice(newPurchasePrice);
    productService.updateProductLocalInfo(productLocalInfoDTO);

    double newAmount = 300D;
    long newEditDate = System.currentTimeMillis();
    inventorySearchIndex.setAmount(newAmount);
    inventorySearchIndex.setEditDate(newEditDate);

    InventoryDTO inventoryDTO = new InventoryDTO();
    inventoryDTO.setAmount(newAmount);
    inventoryDTO.setId(productLocalInfoDTO.getId());
    inventoryDTO.setShopId(shopId);
    inventoryDTO.setLastStorageTime(newEditDate);
    ServiceManager.getService(ITxnService.class).saveInventory(inventoryDTO);

    list.clear();
    list.add(inventorySearchIndex);
    inventoryService.addOrUpdateInventorySearchIndexWithList(shopId, list);
    Thread.sleep(2000);

    response = searchService.queryProductByQueryString("product_name:" + productName, 10);
    assertEquals(1, response.getResults().size());
    SolrDocument doc = response.getResults().get(0);
    assertEquals((float) newAmount, doc.getFirstValue("inventory_amount"));
    assertEquals((float) newPurchasePrice, doc.getFirstValue("purchase_price"));
    assertEquals(newEditDate, doc.getFirstValue("storage_time"));

  }

}
