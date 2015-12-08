package com.bcgogo.txn;

import com.bcgogo.AbstractTest;
import com.bcgogo.enums.ProductStatus;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.product.model.Kind;
import com.bcgogo.search.dto.InventorySearchIndexDTO;
import com.bcgogo.search.model.InventorySearchIndex;
import com.bcgogo.search.service.product.ISearchProductService;
import com.bcgogo.service.ServiceManager;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.ui.ModelMap;

import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;

/**
 * Created by IntelliJ IDEA.
 * User: caiweili
 * Date: 2/18/12
 * Time: 10:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class StockSearchTest extends AbstractTest {
  @Before
  public void setUp() throws Exception {
    request = new MockHttpServletRequest();
    response = new MockHttpServletResponse();
    stockSearchController = new StockSearchController();
    goodsStorageController = new GoodStorageController();
    initTxnControllers(goodsStorageController);
  }

  @Test   //商品修改单元测试
  public void testAjaxToUpdateProduct() throws Exception {
    Long shopId = createShop();


    ProductDTO productDTO = new ProductDTO();
    productDTO.setShopId(shopId);
    productDTO.setName("轮胎A");
    productDTO.setSpec("55/214");
    productDTO.setBrand("米其林");
    productDTO.setModel("型号A");
    productDTO.setProductVehicleBrand("多款");
    addInventory(shopId, productDTO, 100, 10);

    String newProductName = "轮胎B";
    String newProductBrand = "倍耐力";
    String newProductSpec = "100/200";
    String newProductModel = "型号B";
    String newBrand = "奔驰";
    String newModel = "S600";

    ProductDTO newProductDTO = new ProductDTO();
    newProductDTO.setShopId(shopId);
    newProductDTO.setName(newProductName);
    newProductDTO.setSpec(newProductSpec);
    newProductDTO.setBrand(newProductBrand);
    newProductDTO.setModel(newProductModel);
    newProductDTO.setVehicleBrand(newBrand);
    newProductDTO.setVehicleModel(newModel);
    newProductDTO.setProductLocalInfoId(productDTO.getProductLocalInfoId());
    newProductDTO.setRecommendedPrice(250D);
    newProductDTO.setUpperLimit(50D);
    newProductDTO.setLowerLimit(40D);

    stockSearchController.ajaxToUpdateProduct(request,newProductDTO);

    InventorySearchIndex inventorySearchIndex = searchWriter.getById(InventorySearchIndex.class, productDTO.getProductLocalInfoId());
    Long[] ids = productService.getVehicleIds(newBrand, newModel, null, null);
    List<ProductDTO> productDTOs = searchService.queryProducts(newProductName, "product_name",
        "", newProductBrand, newProductSpec, newProductModel,
        newBrand, newModel, null, null,
        ids[0], ids[1], ids[2], ids[3],
        shopId, false, null, 0, 5);

    ProductDTO productDTO2 = productDTOs.get(0);
    assertEquals(newProductName, inventorySearchIndex.getProductName());
    assertEquals(newProductName, productDTO2.getName());
    assertEquals(newProductBrand, inventorySearchIndex.getProductBrand());
    assertEquals(newProductBrand, productDTO2.getBrand());
    assertEquals(newProductModel, inventorySearchIndex.getProductModel());
    assertEquals(newProductModel, productDTO2.getModel());
    assertEquals(newProductSpec, inventorySearchIndex.getProductSpec());
    assertEquals(newProductSpec, productDTO2.getSpec());


  }

	/**
	 * 入库一件商品，删除（删除失败）
	 * 将此商品销售掉
	 * @throws Exception
	 */
	@Test
	public void ajaxToDeleteProductByProductLocalInfoIdTest()throws Exception{
		Long shopId = createShop();
    ProductDTO productDTO = new ProductDTO();
    productDTO.setShopId(shopId);
		productDTO.setCommodityCode("LTA-55/214A");
    productDTO.setName("轮胎A");
    productDTO.setSpec("55/214");
    productDTO.setBrand("米其林");
    productDTO.setModel("型号A");
    productDTO.setProductVehicleBrand("宝马");
		productDTO.setProductVehicleModel("5系");
    Long purchaseInventoryId = addInventory(shopId, productDTO, 100, 10,"个");
		ModelMap modelMap = new ModelMap();
		Map<String,Object> map = (Map<String,Object>)stockSearchController.ajaxToDeleteProductByProductLocalInfoId(modelMap,request,response,productDTO.getProductLocalInfoId());
		assertEquals("error",map.get("result"));
		assertEquals("该商品库存大于0，不能删除！",map.get("resultMsg"));
		goodsStorageController.cancelPurchaseInventory(modelMap,request,purchaseInventoryId);
		modelMap = new ModelMap();
		map.clear();
		map =(Map<String,Object>)stockSearchController.ajaxToDeleteProductByProductLocalInfoId(modelMap,request,response,productDTO.getProductLocalInfoId());
	 	assertEquals("success",map.get("result"));
		assertEquals("删除成功！",map.get("resultMsg"));

		ProductDTO productDTOReturn = productService.getProductByProductLocalInfoId(productDTO.getProductLocalInfoId(),shopId);
		assertEquals(ProductStatus.DISABLED,productDTOReturn.getStatus());
		InventorySearchIndexDTO inventorySearchIndexDTO = searchService.getInventorySearchIndexById(shopId,productDTO.getProductLocalInfoId());
		assertEquals(ProductStatus.DISABLED,inventorySearchIndexDTO.getStatus());
		ProductDTO productDTOSolr = ServiceManager.getService(ISearchProductService.class).getProductDTOFromSolrById(shopId, productDTOReturn.getId());
		assertEquals(ProductStatus.DISABLED,productDTOSolr.getStatus());
	}

  @Test //保存商品分类
  public void testAjaxSaveProductKind() throws Exception {
    Long shopId = createShop();
    request.getSession().setAttribute("shopId",shopId);
    Long kindId = productService.getProductKindId(shopId,"油漆");
    Kind k = productWriter.getById(Kind.class, kindId);
    assertEquals("油漆", k.getName());
  }

  @Test //保存商品分类
  public void testSaveOrUpdateProductKind() throws Exception {
    Long shopId = createShop();
    request.getSession().setAttribute("shopId",shopId);
    Long oldKindId = productService.getProductKindId(shopId,"车灯");
    Kind k = productWriter.getById(Kind.class, oldKindId);
    String newKindName = "特种车灯";
    Long newKindId = productService.getKindIdByName(shopId, newKindName);
    productService.updateKind(oldKindId,newKindName);
    Long KindId = productService.getProductKindId(shopId,newKindName);

    assertEquals(null, newKindId);
    assertEquals(oldKindId, KindId);
  }
}

