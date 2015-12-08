package com.bcgogo.product;

import com.bcgogo.AbstractTest;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.product.service.IProductService;
import com.bcgogo.search.client.SolrClientHelper;
import com.bcgogo.search.service.ISearchService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.GoodStorageController;
import com.bcgogo.txn.RepairController;
import com.bcgogo.txn.TxnController;
import com.bcgogo.txn.dto.RepairOrderDTO;
import com.bcgogo.user.dto.CustomerDTO;
import com.bcgogo.user.dto.VehicleDTO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.ui.ModelMap;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: caiweili
 * Date: 2/28/12
 * Time: 10:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class ProductSolrSearchTest extends AbstractTest {
  @Before
  public void setUp() throws Exception {
    txnController = new TxnController();
    goodsStorageController = new GoodStorageController();
    repairController = new RepairController();
    initTxnControllers(goodsStorageController, txnController,repairController);
    request = new MockHttpServletRequest();
    response = new MockHttpServletResponse();
  }

  @Test
  public void testProductSearch() throws Exception {
    ISearchService searchService = ServiceManager.getService(ISearchService.class);
    SolrClientHelper.getProductSolrClient().deleteAll();
    Long shopId = createShop();
    ProductDTO productDTO = createProductDTO("后保险杠:1", null, null, "11", "全部", null, null, null);
    addInventory(shopId, productDTO, 10, 10);
    productDTO = createProductDTO("前保险杠", null, null, "15", "全部", null, null, null);
    addInventory(shopId, productDTO, 10, 10);
    productDTO = createProductDTO("前刹车片", null, null, "16", "全部", null, null, null);
    addInventory(shopId, productDTO, 10, 10);
    List<ProductDTO> result = searchService.queryProducts("保险", "product_name", null, null, null, null,
        null, null, null, null, null, null, null, null, shopId, false,null, 0, 10);
    Assert.assertEquals(2, result.size());

    result = searchService.queryProducts("保险", "product_name", null, null, "15", null,
        null, null, null, null, null, null, null, null, shopId, false,null, 0, 10);
    Assert.assertEquals(1, result.size());

    result = searchService.queryProducts("前刹车片", "product_name", null, null, "16", null,
        null, null, null, null, null, null, null, null, shopId, false,null, 0, 10);
    Assert.assertEquals(1, result.size());

    result = searchService.queryProducts("前", "product_name", null, null, null, null,
        null, null, null, null, null, null, null, null, shopId, false,null, 0, 10);
    Assert.assertEquals(2, result.size());

    List<String> suggests = searchService.queryProductSuggestionList("保险", "product_name", null, null, null, null,
        null, null, null, null, null, null, null, null, shopId, false, 0, 10);
    Assert.assertEquals(2, suggests.size());

    suggests = searchService.queryProductSuggestionList("前", "product_name", null, null, null, null,
        null, null, null, null, null, null, null, null, shopId, false, 0, 10);
    Assert.assertEquals(2, suggests.size());

    suggests = searchService.queryProductSuggestionList("q", "product_name", null, null, null, null,
        null, null, null, null, null, null, null, null, shopId, false, 0, 10);
    Assert.assertEquals(2, suggests.size());

    suggests = searchService.queryProductSuggestionList("qc", "product_name", null, null, null, null,
        null, null, null, null, null, null, null, null, shopId, false, 0, 10);
    Assert.assertEquals(1, suggests.size());

    suggests = searchService.queryProductSuggestionList("前", "product_name", null, null, null, null, "全部",
        null, null, null, null, null, null, null, shopId, false, 0, 10);
    Assert.assertEquals(2, suggests.size());

    IProductService productService = ServiceManager.getService(IProductService.class);
    Long[] ids = productService.getVehicleIds("丰田", null, null, null);

//    suggests = searchService.queryProductSuggestionList("前", "product_name", null, null, null, null, "丰田",
//        null, null, null,
//        ids[0], ids[1], ids[2], ids[3],
//        shopId, false, 0, 10);
//    Assert.assertEquals(2, suggests.size());
//
//    suggests = searchService.queryProductSuggestionList("前", "product_name", null, null, null, null, "多款",
//        null, null, null, null, null, null, null, shopId, false, 0, 10);
//    Assert.assertEquals(2, suggests.size());
//
//    suggests = searchService.queryProductSuggestionList("h", "product_name", null, null, null, null, "全部",
//        null, null, null, null, null, null, null, shopId, false, 0, 10);
//    Assert.assertEquals(1, suggests.size());


  }

  @Test
  public void testProductSearchWithSpecialParts() throws Exception {
    ISearchService searchService = ServiceManager.getService(ISearchService.class);
    SolrClientHelper.getProductSolrClient().deleteAll();
    Long shopId = createShop();
    ProductDTO productDTO = createProductDTO("后保险杠", null, null, "11", "丰田", null, null, null);

    addInventory(shopId, productDTO, 10, 10);
    productDTO = createProductDTO("前保险杠", null, null, "15", "丰田", null, null, null);
    addInventory(shopId, productDTO, 10, 10);
    productDTO = createProductDTO("前刹车片", null, null, "16", "丰田", null, null, null);
    addInventory(shopId, productDTO, 10, 10);
    IProductService productService = ServiceManager.getService(IProductService.class);
    Long[] ids = productService.getVehicleIds("丰田", null, null, null);

    List<ProductDTO> result = searchService.queryProducts("保险", "product_name", null, null, null, null,
        "丰田", null, null, null, ids[0], ids[1], ids[2], ids[3],
        shopId, false,null, 0, 10);
    Assert.assertEquals(2, result.size());

    result = searchService.queryProducts("保险", "product_name", null, null, "15", null,
        "丰田", null, null, null,
        ids[0], ids[1], ids[2], ids[3],
        shopId, false,null, 0, 10);
    Assert.assertEquals(1, result.size());

    result = searchService.queryProducts("前刹车片", "product_name", null, null, "16", null,
        "丰田", null, null, null,
        ids[0], ids[1], ids[2], ids[3],
        shopId, false,null, 0, 10);
    Assert.assertEquals(1, result.size());

    result = searchService.queryProducts("前刹车片", "product_name", null, null, "16", null,
        "全部", null, null, null, null, null, null, null, shopId, false,null, 0, 10);
    Assert.assertEquals(0, result.size());
  }


  @Test
  public void testProductSearchWithSpecialPartsWithModel() throws Exception {
    ISearchService searchService = ServiceManager.getService(ISearchService.class);
    SolrClientHelper.getProductSolrClient().deleteAll();
    Long shopId = createShop();
    ProductDTO productDTO = createProductDTO("后保险杠", null, null, "11", "丰田", "CAMRY", null, null);
    addInventory(shopId, productDTO, 10, 10);
    productDTO = createProductDTO("前保险杠", null, null, "15", "丰田", "CAMRY", null, null);
    addInventory(shopId, productDTO, 10, 10);
    productDTO = createProductDTO("前刹车片", null, null, "16", "丰田", "CAMRY", null, null);
    addInventory(shopId, productDTO, 10, 10);
    IProductService productService = ServiceManager.getService(IProductService.class);
    Long[] ids = productService.getVehicleIds("丰田", "CAMRY", null, null);

    List<ProductDTO> result = searchService.queryProducts("保险", "product_name", null, null, null, null,
        "丰田", null, null, null, ids[0], null, ids[2], ids[3],
        shopId, false,null, 0, 10);
    Assert.assertEquals(2, result.size());

    result = searchService.queryProducts("保险", "product_name", null, null, null, null,
        "丰田", "CAMRY", null, null, ids[0], ids[1], ids[2], ids[3],
        shopId, false,null, 0, 10);
    Assert.assertEquals(2, result.size());

    result = searchService.queryProducts("保险", "product_name", null, null, "15", null,
        "丰田", null, null, null,
        ids[0], null, ids[2], ids[3],
        shopId, false,null, 0, 10);
    Assert.assertEquals(1, result.size());

    result = searchService.queryProducts("前刹车片", "product_name", null, null, "16", null,
        "丰田", "CAMRY", null, null,
        ids[0], ids[1], ids[2], ids[3],
        shopId, false,null, 0, 10);
    Assert.assertEquals(1, result.size());

    result = searchService.queryProducts("前刹车片", "product_name", null, null, "16", null,
        "全部", null, null, null, null, null, null, null, shopId, false,null, 0, 10);
    Assert.assertEquals(0, result.size());
  }

  @Test
  public void testProductSearchWithSpecialPartsWithModelYear() throws Exception {
    ISearchService searchService = ServiceManager.getService(ISearchService.class);
    SolrClientHelper.getProductSolrClient().deleteAll();
    Long shopId = createShop();
    ProductDTO productDTO = createProductDTO("后保险杠", null, null, "11", "丰田", "CAMRY", "2010", null);
    addInventory(shopId, productDTO, 10, 10);
    productDTO = createProductDTO("前保险杠", null, null, "15", "丰田", "CAMRY", "2010", null);
    addInventory(shopId, productDTO, 10, 10);
    productDTO = createProductDTO("前刹车片", null, null, "16", "丰田", "CAMRY", "2010", null);
    addInventory(shopId, productDTO, 10, 10);
    IProductService productService = ServiceManager.getService(IProductService.class);
    Long[] ids = productService.getVehicleIds("丰田", "CAMRY", "2010", null);

    List<ProductDTO> result = searchService.queryProducts("保险", "product_name", null, null, null, null,
        "丰田", null, null, null, ids[0], null, null, null,
        shopId, false,null, 0, 10);
    Assert.assertEquals(2, result.size());

    result = searchService.queryProducts("保险", "product_name", null, null, null, null,
        "丰田", "CAMRY", "2010", null, ids[0], ids[1], ids[2], null,
        shopId, false,null, 0, 10);
    Assert.assertEquals(2, result.size());

    result = searchService.queryProducts("保险", "product_name", null, null, "15", null,
        "丰田", null, null, null,
        ids[0], null, null, null,
        shopId, false,null, 0, 10);
    Assert.assertEquals(1, result.size());

    result = searchService.queryProducts("前刹车片", "product_name", null, null, "16", null,
        "丰田", "CAMRY", null, null,
        ids[0], ids[1], null, null,
        shopId, false,null, 0, 10);
    Assert.assertEquals(1, result.size());

    result = searchService.queryProducts("前刹车片", "product_name", null, null, "16", null,
        "全部", null, null, null, null, null, null, null, shopId, false,null, 0, 10);
    Assert.assertEquals(0, result.size());
  }

  @Test
  public void testProductSearchWithSpecialPartsWithModelYearEngine() throws Exception {
    ISearchService searchService = ServiceManager.getService(ISearchService.class);
    SolrClientHelper.getProductSolrClient().deleteAll();
    Long shopId = createShop();
    ProductDTO productDTO = createProductDTO("后保险杠", null, null, "11", "丰田", "CAMRY", "2010", "2L");
    addInventory(shopId, productDTO, 10, 10);
    productDTO = createProductDTO("前保险杠", null, null, "15", "丰田", "CAMRY", "2010", "2L");
    addInventory(shopId, productDTO, 10, 10);
    productDTO = createProductDTO("前刹车片", null, null, "16", "丰田", "CAMRY", "2010", "2L");
    addInventory(shopId, productDTO, 10, 10);
    IProductService productService = ServiceManager.getService(IProductService.class);
    Long[] ids = productService.getVehicleIds("丰田", "CAMRY", "2010", "2L");

    List<ProductDTO> result = searchService.queryProducts("保险", "product_name", null, null, null, null,
        "丰田", null, null, null, ids[0], null, null, null,
        shopId, false,null, 0, 10);
    Assert.assertEquals(2, result.size());

    result = searchService.queryProducts("保险", "product_name", null, null, null, null,
        "丰田", "CAMRY", "2010", "2L", ids[0], ids[1], ids[2], ids[3],
        shopId, false,null, 0, 10);
    Assert.assertEquals(2, result.size());

    result = searchService.queryProducts("保险", "product_name", null, null, "15", null,
        "丰田", null, null, null,
        ids[0], null, null, null,
        shopId, false,null, 0, 10);
    Assert.assertEquals(1, result.size());

    result = searchService.queryProducts("前刹车片", "product_name", null, null, "16", null,
        "丰田", "CAMRY", null, null,
        ids[0], ids[1], null, null,
        shopId, false,null, 0, 10);
    Assert.assertEquals(1, result.size());

    result = searchService.queryProducts("前刹车片", "product_name", null, null, "16", null,
        "全部", null, null, null, null, null, null, null, shopId, false,null, 0, 10);
    Assert.assertEquals(0, result.size());
  }


  @Test
  public void testProductSearchForRepairOrder() throws Exception {
    ISearchService searchService = ServiceManager.getService(ISearchService.class);
    SolrClientHelper.getProductSolrClient().deleteAll();
    Long shopId = createShop();
    request.getSession().setAttribute("shopId", shopId);

    VehicleDTO vehicleDTO = generateVehicleDTO("苏E" + String.valueOf(System.currentTimeMillis()).substring(3, 8),
        "丰田", "凯美瑞", "2010", null);
    CustomerDTO customerDTO = generateCustomerDTO("C100", "13211111119");

    ProductDTO productDTO = createProductDTO("后保险杠", null, null, "11", "", null, null, null);
    RepairOrderDTO repairOrderDTO = generateRepairOrderDTO(productDTO, vehicleDTO, customerDTO, request);
    ModelMap model = new ModelMap();
//    String url = txnController.saveRepairOrder(model, repairOrderDTO, request, "save");
    String url = repairController.dispatchRepairOrder(model, repairOrderDTO, request);
    unitTestSleepSecond();
    Assert.assertTrue(url.startsWith(getRepairOrderRedirectUrl(repairOrderDTO)));

    productDTO = createProductDTO("前保险杠", null, null, "15", "", null, null, null);
    repairOrderDTO = generateRepairOrderDTO(productDTO, vehicleDTO, customerDTO, request);
    model = new ModelMap();
//    url = txnController.saveRepairOrder(model, repairOrderDTO, request, "save");
    url = repairController.dispatchRepairOrder(model, repairOrderDTO, request);
    unitTestSleepSecond();
    Assert.assertTrue(url.startsWith(getRepairOrderRedirectUrl(repairOrderDTO)));

    productDTO = createProductDTO("前刹车片", null, null, "16", "", null, null, null);
    repairOrderDTO = generateRepairOrderDTO(productDTO, vehicleDTO, customerDTO, request);
    model = new ModelMap();
//    url = txnController.saveRepairOrder(model, repairOrderDTO, request, "save");
    url = repairController.dispatchRepairOrder(model, repairOrderDTO, request);
    unitTestSleepSecond();
    Assert.assertTrue(url.startsWith(getRepairOrderRedirectUrl(repairOrderDTO)));

    List<ProductDTO> result = searchService.queryProducts("保险", "product_name", null, null, null, null,
        null, null, null, null, null, null, null, null, shopId, false,null, 0, 10);
    Assert.assertEquals(2, result.size());

    result = searchService.queryProducts("保险", "product_name", null, null, "15", null,
        null, null, null, null, null, null, null, null, shopId, false, null,0, 10);
    Assert.assertEquals(1, result.size());

    result = searchService.queryProducts("前刹车片", "product_name", null, null, "16", null,
        null, null, null, null, null, null, null, null, shopId, false,null, 0, 10);
    Assert.assertEquals(1, result.size());

    result = searchService.queryProducts("前", "product_name", null, null, null, null,
        null, null, null, null, null, null, null, null, shopId, false, null,0, 10);
    Assert.assertEquals(2, result.size());

    List<String> suggests = searchService.queryProductSuggestionList("保险", "product_name", null, null, null, null,
        null, null, null, null, null, null, null, null, shopId, false, 0, 10);
    Assert.assertEquals(2, suggests.size());

    suggests = searchService.queryProductSuggestionList("前", "product_name", null, null, null, null,
        null, null, null, null, null, null, null, null, shopId, false, 0, 10);
    Assert.assertEquals(2, suggests.size());

    suggests = searchService.queryProductSuggestionList("q", "product_name", null, null, null, null,
        null, null, null, null, null, null, null, null, shopId, false, 0, 10);
    Assert.assertEquals(2, suggests.size());

    suggests = searchService.queryProductSuggestionList("qc", "product_name", null, null, null, null,
        null, null, null, null, null, null, null, null, shopId, false, 0, 10);
    Assert.assertEquals(1, suggests.size());

    suggests = searchService.queryProductSuggestionList("前", "product_name", null, null, null, null, "",
        null, null, null, null, null, null, null, shopId, false, 0, 10);
    Assert.assertEquals(2, suggests.size());

    IProductService productService = ServiceManager.getService(IProductService.class);
    Long[] ids = productService.getVehicleIds("丰田", null, null, null);

//    suggests = searchService.queryProductSuggestionList("前", "product_name", null, null, null, null, "丰田",
//        null, null, null,
//        ids[0], ids[1], ids[2], ids[3],
//        shopId, false, 0, 10);
//    Assert.assertEquals(2, suggests.size());

//    suggests = searchService.queryProductSuggestionList("前", "product_name", null, null, null, null, "",
//        null, null, null, null, null, null, null, shopId, false, 0, 10);
//    Assert.assertEquals(2, suggests.size());
//
//    suggests = searchService.queryProductSuggestionList("h", "product_name", null, null, null, null, "全部",
//        null, null, null, null, null, null, null, shopId, false, 0, 10);
//    Assert.assertEquals(1, suggests.size());
  }

  @Test
  public void testProductSearchWithSpecialPartsForRepairOrder() throws Exception {
    ISearchService searchService = ServiceManager.getService(ISearchService.class);
    SolrClientHelper.getProductSolrClient().deleteAll();
    Long shopId = createShop();
    request.getSession().setAttribute("shopId", shopId);

    VehicleDTO vehicleDTO = generateVehicleDTO("苏E" + String.valueOf(System.currentTimeMillis()).substring(3, 8),
        "丰田", "凯美瑞", "2010", null);
    CustomerDTO customerDTO = generateCustomerDTO("C100", "13211111119");

    ProductDTO productDTO = createProductDTO("后保险杠", null, null, "11", "丰田", null, null, null);
    RepairOrderDTO repairOrderDTO = generateRepairOrderDTO(productDTO, vehicleDTO, customerDTO, request);
    ModelMap model = new ModelMap();
//    String url = txnController.saveRepairOrder(model, repairOrderDTO, request, "save");
    String url = repairController.dispatchRepairOrder(model, repairOrderDTO, request);
    unitTestSleepSecond();
    Assert.assertTrue(url.startsWith(getRepairOrderRedirectUrl(repairOrderDTO)));

    productDTO = createProductDTO("前保险杠", null, null, "15", "丰田", null, null, null);
    repairOrderDTO = generateRepairOrderDTO(productDTO, vehicleDTO, customerDTO, request);
    model = new ModelMap();
//    url = txnController.saveRepairOrder(model, repairOrderDTO, request, "save");
    url = repairController.dispatchRepairOrder(model, repairOrderDTO, request);
    unitTestSleepSecond();
    Assert.assertTrue(url.startsWith(getRepairOrderRedirectUrl(repairOrderDTO)));

    productDTO = createProductDTO("前刹车片", null, null, "16", "丰田", null, null, null);
    repairOrderDTO = generateRepairOrderDTO(productDTO, vehicleDTO, customerDTO, request);
    model = new ModelMap();
//    url = txnController.saveRepairOrder(model, repairOrderDTO, request, "save");
    url = repairController.dispatchRepairOrder(model, repairOrderDTO, request);
    unitTestSleepSecond();
    Assert.assertTrue(url.startsWith(getRepairOrderRedirectUrl(repairOrderDTO)));

    IProductService productService = ServiceManager.getService(IProductService.class);
    Long[] ids = productService.getVehicleIds("丰田", null, null, null);

    List<ProductDTO> result = searchService.queryProducts("保险", "product_name", null, null, null, null,
        "丰田", null, null, null, ids[0], ids[1], ids[2], ids[3],
        shopId, false,null, 0, 10);
    Assert.assertEquals(2, result.size());

    result = searchService.queryProducts("保险", "product_name", null, null, "15", null,
        "丰田", null, null, null,
        ids[0], ids[1], ids[2], ids[3],
        shopId, false,null, 0, 10);
    Assert.assertEquals(1, result.size());

    result = searchService.queryProducts("前刹车片", "product_name", null, null, "16", null,
        "丰田", null, null, null,
        ids[0], ids[1], ids[2], ids[3],
        shopId, false,null, 0, 10);
    Assert.assertEquals(1, result.size());

//    result = searchService.queryProducts("前刹车片", "product_name", null, null, "16", null,
//        "全部", null, null, null, null, null, null, null, shopId, false,null, 0, 10);
//    Assert.assertEquals(0, result.size());
  }

  @Test
  public void testProductSearchForRepairOrderWithMultiPurposeParts() throws Exception {
    ISearchService searchService = ServiceManager.getService(ISearchService.class);
    SolrClientHelper.getProductSolrClient().deleteAll();
    Long shopId = createShop();
    request.getSession().setAttribute("shopId", shopId);

    VehicleDTO vehicleDTO = generateVehicleDTO("苏E" + String.valueOf(System.currentTimeMillis()).substring(3, 8),
        "丰田", "凯美瑞", "2010", null);
    CustomerDTO customerDTO = generateCustomerDTO("C100", "13211111119");

    ProductDTO productDTO = createProductDTO("后保险杠", null, null, "11", "多款", null, null, null);
    RepairOrderDTO repairOrderDTO = generateRepairOrderDTO(productDTO, vehicleDTO, customerDTO, request);
    ModelMap model = new ModelMap();
//    String url = txnController.saveRepairOrder(model, repairOrderDTO, request, "save");
    String url = repairController.dispatchRepairOrder(model, repairOrderDTO, request);
    unitTestSleepSecond();
    Assert.assertTrue(url.startsWith(getRepairOrderRedirectUrl(repairOrderDTO)));

    productDTO = createProductDTO("前保险杠", null, null, "15", "多款", null, null, null);
    repairOrderDTO = generateRepairOrderDTO(productDTO, vehicleDTO, customerDTO, request);
    model = new ModelMap();
//    url = txnController.saveRepairOrder(model, repairOrderDTO, request, "save");
    url = repairController.dispatchRepairOrder(model, repairOrderDTO, request);
    unitTestSleepSecond();
    Assert.assertTrue(url.startsWith(getRepairOrderRedirectUrl(repairOrderDTO)));
    //车型多款 全部 单元测试
//    productDTO = createProductDTO("前刹车片", null, null, "16", "多款", null, null, null);
//    repairOrderDTO = generateRepairOrderDTO(productDTO, vehicleDTO, customerDTO, request);
//    model = new ModelMap();
//    url = txnController.saveRepairOrder(model, repairOrderDTO, request, "save");
//    Assert.assertTrue(url.startsWith(getRepairOrderRedirectUrl(repairOrderDTO)));
//
//    IProductService productService = ServiceManager.getService(IProductService.class);
//    Long[] ids = productService.getVehicleIds("丰田", null, null, null);
//
//    List<ProductDTO> result = searchService.queryProducts("保险", "product_name", null, null, null, null,
//        "丰田", null, null, null,
//        ids[0], ids[1], ids[2], ids[3],
//        shopId, false,null, 0, 10);
//    Assert.assertEquals(2, result.size());
//
//    result = searchService.queryProducts("保险", "product_name", null, null, null, null,
//        "多款", null, null, null,
//        null, null, null, null,
//        shopId, false,null, 0, 10);
//    Assert.assertEquals(2, result.size());
//
//    result = searchService.queryProducts("保险", "product_name", null, null, "15", null,
//        "丰田", null, null, null,
//        ids[0], ids[1], ids[2], ids[3],
//        shopId, false,null, 0, 10);
//    Assert.assertEquals(1, result.size());
//
//    result = searchService.queryProducts("前刹车片", "product_name", null, null, "16", null,
//        "丰田", null, null, null,
//        ids[0], ids[1], ids[2], ids[3],
//        shopId, false, null,0, 10);
//    Assert.assertEquals(1, result.size());
//
//    result = searchService.queryProducts("前刹车片", "product_name", null, null, "16", null,
//        "全部", null, null, null,
//        null, null, null, null,
//        shopId, false,null, 0, 10);
//    Assert.assertEquals(0, result.size());
  }

  @Test
  public void testEscapeSpecialCharForSolr() throws Exception {
    ISearchService searchService = ServiceManager.getService(ISearchService.class);
    SolrClientHelper.getProductSolrClient().deleteAll();
    Long shopId = createShop();
    ProductDTO productDTO = createProductDTO("轮胎:(新款)", null, null, "速度(H)", "丰田", "CAMRY", "2010", "2L");
    addInventory(shopId, productDTO, 10, 10);
    productDTO = createProductDTO("万向节", null, "2001:(//R36)", null, "丰田", "CAMRY", "2010", "2L");
    addInventory(shopId, productDTO, 10, 10);
    productDTO = createProductDTO("导航仪", "诺亚", "200\\R32", "100::001", "丰田", "CAMRY", "2010", "2L");
    addInventory(shopId, productDTO, 10, 10);
    IProductService productService = ServiceManager.getService(IProductService.class);
    Long[] ids = productService.getVehicleIds("丰田", "CAMRY", "2010", "2L");

    List<ProductDTO> result = searchService.queryProducts("轮胎:(新款)", "product_name", null, null, null, null,
        "丰田", null, null, null, ids[0], null, null, null,
        shopId, false, null,0, 10);
    Assert.assertEquals(1, result.size());

    List<String> result2 = searchService.queryProductSuggestionList("轮胎:(新款)", "product_name", null, null, null, null,
        "丰田", null, null, null, ids[0], null, null, null,
        shopId, false, 0, 10);
    Assert.assertEquals(1, result2.size());

    result = searchService.queryProducts("2001:(//R36)", "product_model", "万向节", "", "", null,
        "丰田", "CAMRY", "2010", "2L", ids[0], ids[1], ids[2], ids[3],
        shopId, false,null, 0, 10);
    Assert.assertEquals(1, result.size());

    result2 = searchService.queryProductSuggestionList("2001:(//R36)", "product_model", "万向节", "", "", null,
        "丰田", "CAMRY", "2010", "2L", ids[0], ids[1], ids[2], ids[3],
        shopId, false, 0, 10);
    Assert.assertEquals(1, result2.size());

    result = searchService.queryProducts("100::001", "product_spec", "导航仪", "诺亚", null, null,
        "丰田", "CAMRY", "2010", "2L", ids[0], ids[1], ids[2], ids[3],
        shopId, false, null,0, 10);
    Assert.assertEquals(1, result.size());

    result2 = searchService.queryProductSuggestionList("100::001", "product_spec", "导航仪", "诺亚", null, null,
        "丰田", "CAMRY", "2010", "2L", ids[0], ids[1], ids[2], ids[3],
        shopId, false, 0, 10);
    Assert.assertEquals(1, result2.size());
  }

  @Test
  public void testQueryProducts() throws Exception {
    ISearchService searchService = ServiceManager.getService(ISearchService.class);
    SolrClientHelper.getProductSolrClient().deleteAll();
    Long shopId = createShop();
    ProductDTO productDTO = createProductDTO("轮胎{米其林}", null, null, null, null, null, null, null);
    addInventory(shopId, productDTO, 10, 10);
    productDTO = createProductDTO("导航仪", null, null, null, null, null, null, null);
    addInventory(shopId, productDTO, 10, 10);

    List<ProductDTO> result = searchService.queryProducts("", "product_name", "导航仪", null, null, null,
        null, null, null, null, null, null, null, null, shopId, false,null, 0, 10);
    Assert.assertEquals("导航仪", result.get(0).getName());

    result = searchService.queryProducts("轮胎", "product_name", null, null, null, null,
        null, null, null, null, null, null, null, null, shopId, false,null, 0, 10);
    Assert.assertEquals("轮胎{米其林}", result.get(0).getName());   //模糊搜索可以找到特殊字符的商品

    result = searchService.queryProducts("", "product_name", "轮胎{米其林}", null, null, null,
        null, null, null, null, null, null, null, null, shopId, false,null, 0, 10);
    Assert.assertEquals(1, result.size());
    Assert.assertEquals("轮胎{米其林}", result.get(0).getName());
  }

}
