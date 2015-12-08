package com.bcgogo.txn;

import com.bcgogo.AbstractTest;
import com.bcgogo.CommonTestService;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.search.dto.ItemIndexDTO;
import com.bcgogo.search.model.SearchDaoManager;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.PurchaseInventoryDTO;
import com.bcgogo.txn.dto.PurchaseInventoryItemDTO;
import com.bcgogo.txn.dto.PurchaseReturnDTO;
import com.bcgogo.txn.dto.PurchaseReturnItemDTO;
import com.bcgogo.txn.model.PurchaseReturnItem;
import com.bcgogo.txn.service.IInventoryService;
import com.bcgogo.txn.service.PurchaseReturnService;
import com.bcgogo.user.dto.SupplierDTO;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.ui.ModelMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: QiuXinyu
 * Date: 12-7-16
 * Time: 上午11:53
 * To change this template use File | Settings | File Templates.
 */
public class GoodsReturnControllerTest extends AbstractTest {

  private CommonTestService commonTestService;

  @Before
  public void setUp() throws Exception {
    goodsReturnController = new GoodsReturnController();
    txnController = new TxnController();
    goodsStorageController = new GoodStorageController();
    buyController = new RFGoodBuyController();
    saleController = new GoodSaleController();
    request = new MockHttpServletRequest();
    response = new MockHttpServletResponse();
    searchDaoManager = ServiceManager.getService(SearchDaoManager.class);
    commonTestService = new CommonTestService();
    purchaseReturnService = ServiceManager.getService(PurchaseReturnService.class);
    goodsReturnController.setPurchaseReturnService(purchaseReturnService);
    inventoryService = ServiceManager.getService(IInventoryService.class);
    initTxnControllers(goodsStorageController);
  }

   /*
  //1，入库11.2个，入库价12.5，设定销售价20.1，下限2.5，上限3.5               11.2*12.5-10.0*1
  //2.供应商查找可退货数量
  //3.品名查找可退货数量
  //4,选中生成退货单 退货数量2个
  //5,修改退货价格10，退货量1个，确认退货
  @Test
  public void testGoodsReturn() throws Exception {
    Long shopId = createShop();
    Map supplierModel = new HashMap();
    Map productModel = new HashMap();
    Map itemModel = new HashMap();
    ItemIndexDTO itemIndexDTO = new ItemIndexDTO();
    request.getSession().setAttribute("shopId", shopId);
    SupplierDTO supplierDTO = commonTestService.createSupplier(supplierModel);
    ProductDTO productDTO = commonTestService.createProduct(productModel);
    ModelMap model = new ModelMap();

    PurchaseInventoryItemDTO purchaseInventoryItemDTO = commonTestService.createPurchaseInventoryItemDTO(itemModel
        , productModel, productDTO, 12.5, 11.2, "个", 2.5, 10.5, 20.1);
    PurchaseInventoryDTO purchaseInventoryDTO = commonTestService.createPurchaseInventoryDTO(supplierDTO, purchaseInventoryItemDTO);
    ////1，入库11.2个，入库价12.5，设定销售价20.1，下限2.5，上限3.5
    goodsStorageController.save(model, purchaseInventoryDTO, request);
    Long productId = ((PurchaseInventoryDTO) model.get("purchaseInventoryDTO")).getItemDTOs()[0].getProductId();
    productDTO.setId(productId);
    Long supplierId = ((PurchaseInventoryDTO) model.get("purchaseInventoryDTO")).getSupplierId();
    supplierDTO.setId(supplierId);


    //2.供应商查找可退货数量
    model = new ModelMap();
    itemIndexDTO.setCustomerOrSupplierName(supplierDTO.getName());
    //goodsReturnController.createProductSearch(model, request, itemIndexDTO);
    ArrayList<PurchaseReturnItemDTO> returnItemDTOList = (ArrayList<PurchaseReturnItemDTO>) model.get("purchaseReturnItemDTOs");
    productModel.put("productId", productId);
    supplierModel.put("supplierId", supplierId);
    itemModel.put("returnAbleAmount", 11.2D);
    commonTestService.testGetReturnItemDTO(productModel, returnItemDTOList.get(0));
    commonTestService.testGetReturnItemDTO(supplierModel, returnItemDTOList.get(0));
    commonTestService.testGetReturnItemDTO(itemModel, returnItemDTOList.get(0));
    Assert.assertEquals(1, returnItemDTOList.size());
    Assert.assertEquals(1, returnItemDTOList.get(0).getItemIndexDTOs().length);


    //3.品名查找可退货数量
    model = new ModelMap();
    itemIndexDTO = new ItemIndexDTO();
    itemIndexDTO.setItemName(productDTO.getName());
//    goodsReturnController.createProductSearch(model, request, itemIndexDTO);
    returnItemDTOList = (ArrayList<PurchaseReturnItemDTO>) model.get("purchaseReturnItemDTOs");
    commonTestService.testGetReturnItemDTO(productModel, returnItemDTOList.get(0));
    commonTestService.testGetReturnItemDTO(supplierModel, returnItemDTOList.get(0));
    commonTestService.testGetReturnItemDTO(itemModel, returnItemDTOList.get(0));
    Assert.assertEquals(1, returnItemDTOList.size());
    Assert.assertEquals(1, returnItemDTOList.get(0).getItemIndexDTOs().length);


    //4,选中生成退货单
    itemIndexDTO = new ItemIndexDTO();
    PurchaseReturnItemDTO checkedItem = returnItemDTOList.get(0);
    checkedItem.setCheckId(checkedItem.getSupplierId() + "_" + checkedItem.getProductId());
    checkedItem.setAmount(2.0);
    PurchaseReturnItemDTO[] itemDTOs = new PurchaseReturnItemDTO[1];
    itemDTOs[0] = checkedItem;
    itemIndexDTO.setItemDTOs(itemDTOs);
    goodsReturnController.createReturnStorage(model, request, itemIndexDTO);
    PurchaseReturnDTO returnDTO = (PurchaseReturnDTO) model.get("purchaseReturnDTO");
    commonTestService.testCreatePurchaseReturnDTO(supplierModel, returnDTO);
    Assert.assertEquals(1, returnDTO.getItemDTOs().length);
    itemModel.put("itemAmount", 2.0);
    itemModel.put("itemTotal", 2.0 * 12.5);
    commonTestService.testCreateReturenItemDTO(productModel, returnDTO.getItemDTOs()[0]);
    commonTestService.testCreateReturenItemDTO(itemModel, returnDTO.getItemDTOs()[0]);


    //5,修改退货价格，确认退货
    model = new ModelMap();
    returnDTO.getItemDTOs()[0].setAmount(1.0);
    returnDTO.getItemDTOs()[0].setPrice(10.0);
    returnDTO.getItemDTOs()[0].setTotal(10.0);
    returnDTO.setTotal(10.0);
    goodsReturnController.saveReturnStorage(model, request, returnDTO);
    returnDTO = (PurchaseReturnDTO) model.get("purchaseReturnDTO");
    productModel.put("amount", 10.2);
    commonTestService.testProductId(productModel, productId, shopId);
//      commonTestService.TestPurchaseReturnId(model,)
    supplierModel.put("totalInventoryAmount", 11.2 * 12.5 - 10.0 * 1);
    supplierModel.put("lastOrderId", returnDTO.getId());
    supplierModel.put("lastOrderProducts", productDTO.getName());
    supplierModel.put("lastOrderType", OrderTypes.RETURN);
    commonTestService.testSupplierId(supplierModel, supplierId, shopId);







    //6.根据库存查询生成退货单
    request.setParameter("productIds",productDTO.getProductLocalInfoId().toString());
    goodsReturnController.createReturnStorageByProductId( model, request,  response) ;
    returnDTO = (PurchaseReturnDTO) model.get("purchaseReturnDTO");
    Assert.assertNotNull(returnDTO);
    Assert.assertEquals(1,returnDTO.getItemDTOs().length);

    //7.根据供应商生成退货单
    request.setParameter("supplierId",supplierDTO.getId().toString());
    goodsReturnController.createReturnStorageBySupplierId(model, request,  response) ;
    returnDTO = (PurchaseReturnDTO) model.get("purchaseReturnDTO");
    Assert.assertNotNull(returnDTO);
    Assert.assertEquals(1,returnDTO.getItemDTOs().length);
    commonTestService.testCreatePurchaseReturnDTO(supplierModel, returnDTO);

  }
     */
   //1.保存退货记录
   //2.根据库存查询生成退货单
   //3..根据供应商生成退货单
   @Test
  public void testGoodsReturn() throws Exception {
      Long shopId = createShop();
    Map supplierModel = new HashMap();
    Map productModel = new HashMap();
    Map itemModel = new HashMap();
    ItemIndexDTO itemIndexDTO = new ItemIndexDTO();
    request.getSession().setAttribute("shopId", shopId);
    SupplierDTO supplierDTO = commonTestService.createSupplier(supplierModel);
    ProductDTO productDTO = commonTestService.createProduct(productModel);
    ModelMap model = new ModelMap();

    PurchaseInventoryItemDTO purchaseInventoryItemDTO = commonTestService.createPurchaseInventoryItemDTO(itemModel
        , productModel, productDTO, 12.5, 11.2, "个", 2.5, 10.5, 20.1);
    PurchaseInventoryDTO purchaseInventoryDTO = commonTestService.createPurchaseInventoryDTO(supplierDTO, purchaseInventoryItemDTO);
    ////1，入库11.2个，入库价12.5，设定销售价20.1，下限2.5，上限3.5
    goodsStorageController.savePurchaseInventory(model, purchaseInventoryDTO, request,response);
	  goodsStorageController.getPurchaseInventory(model, request, purchaseInventoryDTO.getId().toString());
    Long productId = ((PurchaseInventoryDTO) model.get("purchaseInventoryDTO")).getItemDTOs()[0].getProductId();
    productDTO.setId(productId);
    Long supplierId = ((PurchaseInventoryDTO) model.get("purchaseInventoryDTO")).getSupplierId();
    supplierDTO.setId(supplierId);


   //1.保存退货记录
    PurchaseReturnDTO returnDTO = new  PurchaseReturnDTO();
     returnDTO.setReturnPayableType("cash");
    returnDTO.setSupplierDTO(supplierDTO);
    returnDTO.setShopId(shopId);

    PurchaseReturnItemDTO  purchaseReturnItemDTO = new PurchaseReturnItemDTO();
    productDTO.setProductLocalInfoId(productDTO.getId());
    purchaseReturnItemDTO.setProduct(productDTO);
    purchaseReturnItemDTO.setInventoryAmount(5d);
    purchaseReturnItemDTO.setPrice(36d);
    purchaseReturnItemDTO.setAmount(2d);
    purchaseReturnItemDTO.setTotal(72d);
    returnDTO.setTotal(72d);
    returnDTO.setSettledAmount(72d);
    PurchaseReturnItemDTO[] a = {purchaseReturnItemDTO};
    returnDTO.setItemDTOs(a);

    goodsReturnController.saveReturnStorage(model,request ,returnDTO);
    returnDTO = (PurchaseReturnDTO)model.get("purchaseReturnDTO");
    returnDTO = txnWriter.getPurchaseReturnById(shopId ,returnDTO.getId()) ;

    Assert.assertNotNull(returnDTO);
   //  commonTestService.testCreatePurchaseReturnDTO(supplierModel, returnDTO);
     Assert.assertEquals(72, purchaseReturnItemDTO.getTotal(), 0.0001);
      Assert.assertEquals(supplierId,returnDTO.getSupplierId());
     List purchaseReturnItemList = txnWriter.getPurchaseReturnItemsByProdctId(returnDTO.getId(),productId);
      Assert.assertEquals(1,purchaseReturnItemList.size());

     PurchaseReturnItem purchaseReturnItem = (PurchaseReturnItem) purchaseReturnItemList.get(0);
     Assert.assertEquals(36,purchaseReturnItem.getPrice(),0.0001);
     Assert.assertEquals(productId,purchaseReturnItem.getProductId());
    Assert.assertEquals(72,purchaseReturnItem.getTotal(),0.0001);
     Assert.assertEquals(2,purchaseReturnItem.getAmount(),0.0001);

     goodsReturnController.createReturnStorage(model, request);



   //2.根据库存查询生成退货单
    request.setParameter("productIds",purchaseInventoryItemDTO.getProductIdStr());
    goodsReturnController.createReturnStorageByProductId( model, request,  response) ;
    returnDTO = (PurchaseReturnDTO) model.get("purchaseReturnDTO");
    Assert.assertNotNull(returnDTO);
    Assert.assertEquals(1, returnDTO.getItemDTOs().length);

   //3.根据供应商生成退货单
    request.setParameter("supplierId",supplierDTO.getId().toString());
    goodsReturnController.createReturnStorageBySupplierId(model, request,  response) ;
    returnDTO = (PurchaseReturnDTO) model.get("purchaseReturnDTO");
    Assert.assertNotNull(returnDTO);
    Assert.assertEquals(1,returnDTO.getItemDTOs().length);
    commonTestService.testCreatePurchaseReturnDTO(supplierModel, returnDTO);

  }

  @Test
  public void testRepealPurchaseReturn() throws Exception
  {
//         Long shopId = createShop();
//    Map supplierModel = new HashMap();
//    Map productModel = new HashMap();
//    Map itemModel = new HashMap();
//    request.getSession().setAttribute("shopId", shopId);
//    SupplierDTO supplierDTO = commonTestService.createSupplier(supplierModel);
//    ProductDTO productDTO = commonTestService.createProduct(productModel);
//    ModelMap model = new ModelMap();
//
//    PurchaseInventoryItemDTO purchaseInventoryItemDTO = commonTestService.createPurchaseInventoryItemDTO(itemModel
//        , productModel, productDTO, 12.5, 11, "个", 2.5, 10.5, 20.1);
//    PurchaseInventoryDTO purchaseInventoryDTO = commonTestService.createPurchaseInventoryDTO(supplierDTO, purchaseInventoryItemDTO);
//    ////1，入库11个，入库价12.5，设定销售价20.1，下限2.5，上限3.5
//    goodsStorageController.save(model, purchaseInventoryDTO, request);
//	  goodsStorageController.getPurchaseInventory(model, request, purchaseInventoryDTO.getId().toString());
//    Long productId = ((PurchaseInventoryDTO) model.get("purchaseInventoryDTO")).getItemDTOs()[0].getProductId();
//    productDTO.setId(productId);
//    Long supplierId = ((PurchaseInventoryDTO) model.get("purchaseInventoryDTO")).getSupplierId();
//    supplierDTO.setId(supplierId);
//
//    PurchaseReturnDTO returnDTO = new  PurchaseReturnDTO();
//    returnDTO.setReturnPayableType("deposit");
//    returnDTO.setSupplierDTO(supplierDTO);
//    returnDTO.setShopId(shopId);
//    returnDTO.setDepositAmount(72d);
//    PurchaseReturnItemDTO  purchaseReturnItemDTO = new PurchaseReturnItemDTO();
//    productDTO.setProductLocalInfoId(productDTO.getId());
//    purchaseReturnItemDTO.setProduct(productDTO);
//    purchaseReturnItemDTO.setInventoryAmount(11d);
//    purchaseReturnItemDTO.setPrice(36d);
//    purchaseReturnItemDTO.setAmount(2d);
//    purchaseReturnItemDTO.setTotal(72d);
//    returnDTO.setTotal(72d);
//
//    PurchaseReturnItemDTO[] a = {purchaseReturnItemDTO};
//    returnDTO.setItemDTOs(a);
//
//    goodsReturnController.saveReturnStorage(model,request ,returnDTO);
//
//    Long purchaseReturnId = returnDTO.getId();
//
//    goodsReturnController.repealReturnStorage(model,request,purchaseReturnId);
//
//    Inventory inventory = txnWriter.getById(Inventory.class, purchaseReturnItemDTO.getProductId());
//    Double inventoryAmount = inventory.getAmount();
//    Assert.assertEquals(11,inventoryAmount,0.0001);

//    Deposit deposit = txnWriter.getDepositBySupplierId(returnDTO.getShopId(), returnDTO.getSupplierId());
//    Double actualPay = deposit.getActuallyPaid();
//    Double cash = deposit.getCash();
//    Assert.assertEquals(0,actualPay,0.0001);
//    Assert.assertEquals(0,cash,0.0001);
//
//    PurchaseReturn purchaseReturn = txnWriter.getById(PurchaseReturn.class, returnDTO.getId());
//    Assert.assertSame(purchaseReturn.getStatus(), OrderStatus.REPEAL);
//
//    InventorySearchIndex inventorySearchIndex =searchDaoManager.getWriter().getInventorySearchIndexByProductLocalInfoId(returnDTO.getShopId(), purchaseReturnItemDTO.getProductId());
//    Assert.assertEquals(11,inventorySearchIndex.getAmount(),0.0001);
  }


   @Test
  public void testRepealPurchaseReturnOfStrike() throws Exception{
//         Long shopId = createShop();
//    Map supplierModel = new HashMap();
//    Map productModel = new HashMap();
//    Map itemModel = new HashMap();
//    request.getSession().setAttribute("shopId", shopId);
//    SupplierDTO supplierDTO = commonTestService.createSupplier(supplierModel);
//    ProductDTO productDTO = commonTestService.createProduct(productModel);
//    ModelMap model = new ModelMap();
//
//    PurchaseInventoryItemDTO purchaseInventoryItemDTO = commonTestService.createPurchaseInventoryItemDTO(itemModel
//        , productModel, productDTO, 12.5, 11, "个", 2.5, 10.5, 20.1);
//    PurchaseInventoryDTO purchaseInventoryDTO = commonTestService.createPurchaseInventoryDTO(supplierDTO, purchaseInventoryItemDTO);
//    ////1，入库11个，入库价12.5，设定销售价20.1，下限2.5，上限3.5
//
//    purchaseInventoryDTO.setCreditAmount(purchaseInventoryDTO.getTotal());
//    goodsStorageController.save(model, purchaseInventoryDTO, request);
//	  goodsStorageController.getPurchaseInventory(model, request, purchaseInventoryDTO.getId().toString());
//    Long productId = ((PurchaseInventoryDTO) model.get("purchaseInventoryDTO")).getItemDTOs()[0].getProductId();
//    productDTO.setId(productId);
//    Long supplierId = ((PurchaseInventoryDTO) model.get("purchaseInventoryDTO")).getSupplierId();
//    supplierDTO.setId(supplierId);
//
//    List<PayableDTO> payableDTOList = supplierPayableService.searchPayable(shopId,supplierId);
//    double creditAmountOld = payableDTOList.get(0).getCreditAmount();
//    supplierDTO = userService.getSupplierById(supplierId);
//    SupplierRecord supplierRecord = txnWriter.getSupplierRecord(shopId, supplierId);
//    double creditAmountOldS = supplierRecord.getCreditAmount();
//    double inventoryAmountOld = supplierDTO.getTotalInventoryAmount();
//    //冲账作废
//    PurchaseReturnDTO returnDTO = new  PurchaseReturnDTO();
//     returnDTO.setReturnPayableType("strike");
//    returnDTO.setSupplierDTO(supplierDTO);
//    returnDTO.setShopId(shopId);
//    PurchaseReturnItemDTO  purchaseReturnItemDTO = new PurchaseReturnItemDTO();
//    productDTO.setProductLocalInfoId(productDTO.getId());
//    purchaseReturnItemDTO.setProduct(productDTO);
//    purchaseReturnItemDTO.setInventoryAmount(11d);
//    purchaseReturnItemDTO.setPrice(36d);
//    purchaseReturnItemDTO.setAmount(2d);
//    purchaseReturnItemDTO.setTotal(72d);
//
//    returnDTO.setTotal(72d);
//    returnDTO.setStrikeAmount(72d);
//
//    PurchaseReturnItemDTO[] a = {purchaseReturnItemDTO};
//    returnDTO.setItemDTOs(a);
//
//    goodsReturnController.saveReturnStorage(model,request ,returnDTO);
//
//    supplierDTO = userService.getSupplierById(supplierId);
//    double inventoryAmountValue = supplierDTO.getTotalInventoryAmount();
//
//    Long purchaseReturnId = returnDTO.getId();
//
//    goodsReturnController.repealReturnStorage(model,request,purchaseReturnId);
//
//    Inventory inventory = txnWriter.getById(Inventory.class, purchaseReturnItemDTO.getProductId());
//    Double inventoryAmount = inventory.getAmount();
//    Assert.assertEquals(11,inventoryAmount,0.0001);
//
//    PurchaseReturn purchaseReturn = txnWriter.getById(PurchaseReturn.class, returnDTO.getId());
//    Assert.assertSame(purchaseReturn.getStatus(), OrderStatus.REPEAL);
//
//    InventorySearchIndex inventorySearchIndex =searchDaoManager.getWriter().getInventorySearchIndexByProductLocalInfoId(returnDTO.getShopId(), purchaseReturnItemDTO.getProductId());
//    Assert.assertEquals(11,inventorySearchIndex.getAmount(),0.0001);
//
//    List<PayableDTO> payableDTOListNew = supplierPayableService.searchPayable(shopId,supplierId);
//    double creditAmountNew = payableDTOListNew.get(0).getCreditAmount();
//    Assert.assertEquals(creditAmountOld,creditAmountNew,0.0001);
//
//    supplierDTO = userService.getSupplierById(supplierId);
//    double inventoryAmountNew = supplierDTO.getTotalInventoryAmount();
//    Assert.assertEquals(inventoryAmountOld,inventoryAmountNew,0.0001);
//
//    supplierRecord = txnWriter.getSupplierRecord(shopId, supplierId);
//    double creditAmountNewS = supplierRecord.getCreditAmount();
//    Assert.assertEquals(creditAmountNewS,creditAmountOldS,0.0001);
  }


}
