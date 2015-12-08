package com.bcgogo.txn;

import com.bcgogo.AbstractTest;
import com.bcgogo.CommonTestService;
import com.bcgogo.common.PopMessage;
import com.bcgogo.common.Result;
import com.bcgogo.enums.*;
import com.bcgogo.stat.dto.SupplierRecordDTO;
import com.bcgogo.txn.dto.*;
import com.bcgogo.txn.dto.StatementAccount.OrderDebtType;
import com.bcgogo.txn.service.ISupplierPayableService;
import com.bcgogo.txn.service.SupplierPayableService;
import com.bcgogo.user.dto.SupplierDTO;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.JsonUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.ui.ModelMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: zhangchuanlong
 * Date: 12-8-27
 * Time: 上午10:04
 * <p/>
 * 应付款结算controller单元测试
 */
public class SupplierPayableControllerTest extends AbstractTest {

  @Before
  public void setUp() throws Exception {
    supplierPayableController = new SupplierPayableController();
    request = new MockHttpServletRequest();
    response = new MockHttpServletResponse();
    supplierPayableService = com.bcgogo.service.ServiceManager.getService(ISupplierPayableService.class);
  }

  /**
   * 保存定金单元测试
   *
   * @throws Exception
   * @author zhangchuanlong
   */
  @Test
  public void testAddDeposit() throws Exception {
    String depositJson = "{\"cash\":\"10\",\"bankCardAmount\":\"10\",\"checkAmount\":\"10\",\"checkNo\":\"1011\",\"actuallyPaid\":\"30\",\"supplierId\":\"10000010001100011\"}";
    request.setParameter("depositDTO", depositJson);
    Long shopId = createShop();
    request.getSession().setAttribute("shopId", shopId);
    ModelMap model = new ModelMap();
    supplierPayableController.setSupplierPayableService((SupplierPayableService) supplierPayableService);
    Result result = supplierPayableController.addDeposit(model, request, response);
    Assert.assertTrue(result.isSuccess());
    DepositDTO depositDTO = (DepositDTO) model.get("depositDTO");
    Assert.assertEquals(10.0, depositDTO.getCash());
    Assert.assertEquals(10.0, depositDTO.getBankCardAmount());
    Assert.assertEquals(10.0, depositDTO.getCheckAmount());
    Assert.assertEquals("1011", depositDTO.getCheckNo());
    Assert.assertEquals(30.0, depositDTO.getActuallyPaid());
    Assert.assertEquals((Object) 10000010001100011l, depositDTO.getSupplierId());
  }

  private void addDeposit(Long shopId, Long supplierId){
    String depositJson = "{\"cash\":\"100\",\"bankCardAmount\":\"100\",\"checkAmount\":\"100\",\"checkNo\":\"1011\",\"actuallyPaid\":\"300\",\"supplierId\":\""+supplierId+"\"}";
    request.setParameter("depositDTO", depositJson);
    request.getSession().setAttribute("shopId", shopId);
    ModelMap model = new ModelMap();
    supplierPayableController.setSupplierPayableService((SupplierPayableService) supplierPayableService);
    Result result = supplierPayableController.addDeposit(model, request, response);
  }

  /**
   * 分页查询应付款单元测试
   *
   * @throws Exception
   */
  @Test
  public void testSearchPayable() throws Exception {
    Long shopId = createShop();
    SupplierDTO supplierDTO = addSupplier();
    Long supplierId = supplierDTO.getId();
    request.getSession().setAttribute("shopId", shopId);
    ModelMap model = new ModelMap();
    request.setParameter("supplierId", supplierId.toString());
    request.setParameter("orderType", "desc");
    request.setParameter("orderName", "pay_time");
    request.setParameter("fromTime", "");
    request.setParameter("toTime", "");
    request.setParameter("startPageNo", "1");
    /*无数据*/
    supplierPayableController.setSupplierPayableService((SupplierPayableService) supplierPayableService);
    supplierPayableController.searchPayable(model, request, response);
    Object totalCount = model.get("totalCount");
    List<PayableDTO> payableDTOs = (List<PayableDTO>) model.get("payableDTOs");
    Assert.assertEquals("0", totalCount.toString());
    Assert.assertEquals(0, payableDTOs.size());
    /*总数据不超过页大小*/
    createPayables(shopId, 9, supplierDTO.getId());
    supplierPayableController.setSupplierPayableService((SupplierPayableService) supplierPayableService);
    supplierPayableController.searchPayable(model, request, response);
    totalCount = model.get("totalCount");
    payableDTOs = (List<PayableDTO>) model.get("payableDTOs");
    Assert.assertEquals("9", totalCount.toString());
    Assert.assertEquals(5, payableDTOs.size());
    /*输入条件不满足*/
    request.setParameter("supplierId", "11111111111112");
    supplierPayableController.setSupplierPayableService((SupplierPayableService) supplierPayableService);
    supplierPayableController.searchPayable(model, request, response);
    totalCount = model.get("totalCount");
    payableDTOs = (List<PayableDTO>) model.get("payableDTOs");
    Assert.assertEquals("0", totalCount.toString());
    Assert.assertEquals(0, payableDTOs.size());
    /*有数据有查询*/
    request.setParameter("supplierId", supplierId.toString());
    request.setParameter("orderType", "desc");
    request.setParameter("orderName", "pay_time");
    request.setParameter("fromTime", DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DEFAULT, DateUtil.getStartTimeOfToday()));
    request.setParameter("toTime", DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DEFAULT, DateUtil.getEndTimeOfToday()));
    request.setParameter("startPageNo", "1");
    supplierPayableController.setSupplierPayableService((SupplierPayableService) supplierPayableService);
    supplierPayableController.searchPayable(model, request, response);
    totalCount = model.get("totalCount");
    payableDTOs = (List<PayableDTO>) model.get("payableDTOs");
    Assert.assertEquals("9", totalCount.toString());
    Assert.assertEquals(5, payableDTOs.size());

  }

  /**
   * 付款给供应商单元测试
   *
   * @throws Exception
   */
  @Test
  public void testPayToSupplier() throws Exception {
    Long shopId = createShop();
    request.getSession().setAttribute("shopId", shopId);
    SupplierDTO supplierDTO = addSupplier();
    Long supplierId = supplierDTO.getId();
    List<PayableDTO> payableDTOs = createPayables(shopId, 5, supplierId);
    for (PayableDTO p : payableDTOs) {
      p.setIdStr(p.getId().toString());
    }
    ModelMap model = new ModelMap();
    String lstPayAbles = JsonUtil.listToJson(payableDTOs);
    PayableHistoryDTO payableHistoryDTO = createPayableHistoryDTO(shopId, supplierId);
    String payableHistoryDTOStr = JsonUtil.objectToJson(payableHistoryDTO);
    request.setParameter("lstPayAbles", lstPayAbles);
    request.setParameter("payableHistoryDTO", payableHistoryDTOStr);
    addDeposit(shopId, supplierId);

    List<PayableDTO> payableDTOList = new Gson().fromJson(lstPayAbles, new TypeToken<List<PayableDTO>>() {
    }.getType());
    supplierPayableController.setSupplierPayableService((SupplierPayableService) supplierPayableService);
    supplierPayableController.payToSupplier(model, request, response);
    PopMessage popMessage = (PopMessage) model.get("result");
    Assert.assertEquals("success", popMessage.getMessage());
    SupplierRecordDTO supplierRecordDTO = supplierRecordService.getSupplierRecordDTOBySupplierId(shopId, supplierId);
    Assert.assertNotNull(supplierRecordDTO);
    Assert.assertEquals(100d, supplierPayableService.getSumPayableBySupplierId(supplierId, shopId, OrderDebtType.SUPPLIER_DEBT_PAYABLE).get(0), 0.001);
    Assert.assertEquals(100d, supplierRecordDTO.getCreditAmount(), 0.001);
  }

  /**
   * 根据供应商ID获得总付款记录数
   *
   * @throws Exception
   */
  @Test
  public void testGetTotalCountOfPayable() throws Exception {
    Long shopId = createShop();
    SupplierDTO supplierDTO = addSupplier();
    Long supplierId = supplierDTO.getId();
    request.getSession().setAttribute("shopId", shopId);
    request.setParameter("supplierId", supplierId.toString());
    ModelMap model = new ModelMap();
    createPayables(shopId, 9, supplierDTO.getId());      //创建9个应付款
    supplierPayableController.setSupplierPayableService((SupplierPayableService) supplierPayableService);
    supplierPayableController.getTotalCountOfPayable(model, request, response);
    PopMessage popMessage = (PopMessage) model.get("result");
    Object totalCount = model.get("totalCount");
    Assert.assertEquals(9, popMessage.getMessage());
    Assert.assertEquals("9", totalCount.toString());
  }

  /**
   * 付款历史记录查询
   *
   * @throws Exception
   */
  @Test
  public void testGetPayableHistoryRecord() throws Exception {
    Long shopId = createShop();
    request.getSession().setAttribute("shopId", shopId);
    request.setParameter("startTime", "");
    request.setParameter("endTime", "");
    request.setParameter("supplierId", "11111111111111");
    request.setParameter("orderByName", "");
    request.setParameter("orderByType", "");
    request.setParameter("startPageNo", "1");
    ModelMap model = new ModelMap();
    /*无数据*/
    supplierPayableController.setSupplierPayableService((SupplierPayableService) supplierPayableService);
    supplierPayableController.getPayableHistoryRecord(model, request, response);
    List<PayableHistoryRecordDTO> payableHistoryRecordDTOs = (List<PayableHistoryRecordDTO>) model.get("payableHistoryRecordDTOs");
    Assert.assertEquals(0, payableHistoryRecordDTOs.size());
    /*总数据不超过页大小*/
    createPayableHistoryRecord(shopId, 5);
    supplierPayableController.setSupplierPayableService((SupplierPayableService) supplierPayableService);
    supplierPayableController.getPayableHistoryRecord(model, request, response);
    payableHistoryRecordDTOs = (List<PayableHistoryRecordDTO>) model.get("payableHistoryRecordDTOs");
    Assert.assertEquals(5, payableHistoryRecordDTOs.size());
    /*输入条件不满足*/
    request.setParameter("startTime", DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DEFAULT, DateUtil.getStartTimeOfYesterday()));
    request.setParameter("endTime", DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DEFAULT, DateUtil.getEndTimeOfYesterday()));
    request.setParameter("supplierId", "11111111111112");
    request.setParameter("orderByName", "");
    request.setParameter("orderByType", "");
    request.setParameter("startPageNo", "1");
    supplierPayableController.setSupplierPayableService((SupplierPayableService) supplierPayableService);
    supplierPayableController.getPayableHistoryRecord(model, request, response);
    payableHistoryRecordDTOs = (List<PayableHistoryRecordDTO>) model.get("payableHistoryRecordDTOs");
    Assert.assertEquals(0, payableHistoryRecordDTOs.size());
    /*有数据有查询*/
    request.setParameter("startTime", DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DEFAULT, DateUtil.getStartTimeOfToday()));
    request.setParameter("endTime", DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DEFAULT, DateUtil.getEndTimeOfToday()));
    request.setParameter("supplierId", "11111111111111");
    request.setParameter("orderByName", "");
    request.setParameter("orderByType", "");
    request.setParameter("startPageNo", "1");
    supplierPayableController.setSupplierPayableService((SupplierPayableService) supplierPayableService);
    supplierPayableController.getPayableHistoryRecord(model, request, response);
    payableHistoryRecordDTOs = (List<PayableHistoryRecordDTO>) model.get("payableHistoryRecordDTOs");
    Assert.assertEquals(5, payableHistoryRecordDTOs.size());

  }

  /**
   * 获得供应商总应付款额
   *
   * @throws Exception
   */
  @Test
  public void testGetCreditAmountBySupplierId() throws Exception {
    Long shopId = createShop();
    SupplierDTO supplierDTO = addSupplier();
    Long supplierId = supplierDTO.getId();
    request.getSession().setAttribute("shopId", shopId);
    request.setParameter("supplierId", supplierId.toString());
    ModelMap model = new ModelMap();
    createPayables(shopId, 10, supplierDTO.getId());
    supplierPayableController.setSupplierPayableService((SupplierPayableService) supplierPayableService);
    supplierPayableController.getCreditAmountBySupplierId(model, request, response);
    PopMessage popMessage = (PopMessage) model.get("result");
    Assert.assertEquals("800.0", popMessage.getMessage().toString());
  }

  /**
   * 根据供应商ID获得供应商总定金
   *
   * @throws Exception
   */
  @Test
  public void testGetSumDepositBySupplierId() throws Exception {
    Long shopId = createShop();
    request.getSession().setAttribute("shopId", shopId);
    String depositJson = "{\"cash\":\"10\",\"bankCardAmount\":\"10\",\"checkAmount\":\"10\",\"checkNo\":\"1011\",\"actuallyPaid\":\"30\",\"supplierId\":\"10000010001100011\"}";
    request.setParameter("depositDTO", depositJson);
    ModelMap model = new ModelMap();
    supplierPayableController.setSupplierPayableService((SupplierPayableService) supplierPayableService);
    /*保存定金*/
    Result result = supplierPayableController.addDeposit(model, request, response);
    Assert.assertTrue(result.isSuccess());
    DepositDTO depositDTO = (DepositDTO) model.get("depositDTO");
    Assert.assertEquals(10.0, depositDTO.getCash());
    Assert.assertEquals(10.0, depositDTO.getBankCardAmount());
    Assert.assertEquals(10.0, depositDTO.getCheckAmount());
    Assert.assertEquals("1011", depositDTO.getCheckNo());
    Assert.assertEquals(30.0, depositDTO.getActuallyPaid());
    Assert.assertEquals((Object) 10000010001100011l, depositDTO.getSupplierId());
    request.setParameter("supplierId", depositDTO.getSupplierId().toString());
    /*获得定金*/
    supplierPayableController.getSumDepositBySupplierId(model, request, response);
    PopMessage popMessage = (PopMessage) model.get("result");
    Assert.assertEquals(30.0, popMessage.getMessage());
  }

  /**
   * 判断单据是否有已付款
   *
   * @throws Exception
   */
  @Test
  public void testCheckPaidInventory() throws Exception {
    Long shopId = createShop();
    SupplierDTO supplierDTO = addSupplier();
    Long supplierId = supplierDTO.getId();
    request.getSession().setAttribute("shopId", shopId);
    createPayables(shopId, 9, supplierDTO.getId());
    ModelMap model = new ModelMap();
    request.setParameter("supplierId", supplierId.toString());
    request.setParameter("purchaseInventoryId", "100000000002");
    supplierPayableController.setSupplierPayableService((SupplierPayableService) supplierPayableService);
    supplierPayableController.checkPaidInventory(model, request, response);
    PopMessage popMessage = (PopMessage) model.get("result");
    Assert.assertEquals("error", popMessage.getMessage());
  }

  /**
   * 对老入库单应付款表初始化
   *
   * @throws Exception
   */
  @Test
  public void initPurchaseInventoryPayable() throws Exception {
    ModelMap model = new ModelMap();
    Map orderModel = new HashMap();
    Map productModel = new HashMap();
    Map itemModel = new HashMap();
    CommonTestService commonTestService = new CommonTestService();
    Long shopId = createShop();
    request.getSession().setAttribute("shopId", shopId);
    PurchaseInventoryDTO purchaseInventoryDTO = new PurchaseInventoryDTO();
    purchaseInventoryDTO.setSupplier("pTest1");
    purchaseInventoryDTO.setContact("HHH");
    purchaseInventoryDTO.setMobile("13012343943");
    purchaseInventoryDTO.setVestDateStr("2012-03-20 12:20");
    purchaseInventoryDTO.setTotal(11D);
    PurchaseInventoryItemDTO purchaseInventoryItemDTO = new PurchaseInventoryItemDTO();
    purchaseInventoryItemDTO.setAmount(25d);
    purchaseInventoryItemDTO.setPurchasePrice(1d);
    purchaseInventoryItemDTO.setRecommendedPrice(5D);
    String productName = "QQQ";
    purchaseInventoryItemDTO.setProductName(productName);
    purchaseInventoryItemDTO.setSpec("123");
    purchaseInventoryItemDTO.setTotal(11d);
    purchaseInventoryItemDTO.setBrand("");
    purchaseInventoryItemDTO.setModel("");
    purchaseInventoryItemDTO.setVehicleBrand("");
    purchaseInventoryItemDTO.setVehicleEngine("");
    purchaseInventoryItemDTO.setVehicleModel("");
    purchaseInventoryItemDTO.setVehicleYear("");
    purchaseInventoryItemDTO.setLowerLimit(5D);
    purchaseInventoryItemDTO.setUpperLimit(10D);
    purchaseInventoryItemDTO.setUnit("");

    orderModel.put("mobile", "13012343943");
    orderModel.put("contact", "HHH");
    orderModel.put("total", 11D);
    orderModel.put("supplierName", "pTest1");
    orderModel.put("status", OrderStatus.PURCHASE_INVENTORY_DONE);
    itemModel.put("amount", 25d);
    itemModel.put("price", 1d);
    itemModel.put("total", 11d);
    itemModel.put("vehicleBrand", "");
    itemModel.put("vehicleModel", "");
    itemModel.put("unit", "");
    productModel.put("isTestOrder", "true");
    commonTestService.setTestProductModel(productModel, productName, "", "", "123", "", "", "", "");
    commonTestService.setTestProductModel(productModel, 25d, 1d, 5d, "", "", null, 5D, 10D);
    PurchaseInventoryItemDTO[] itemDTOs = new PurchaseInventoryItemDTO[1];
    itemDTOs[0] = purchaseInventoryItemDTO;
    purchaseInventoryDTO.setItemDTOs(itemDTOs);

    //step1 入库一件新商品
    goodsStorageController.savePurchaseInventory(model, purchaseInventoryDTO, request,response);
    unitTestSleepSecond();
	  goodsStorageController.getPurchaseInventory(model, request, purchaseInventoryDTO.getId().toString());
    PurchaseInventoryDTO returnPurchaseInventoryDTO = (PurchaseInventoryDTO)model.get("purchaseInventoryDTO");
    PayableDTO returnPayableDTO = supplierPayableService.getInventoryPayable(returnPurchaseInventoryDTO.getShopId(), returnPurchaseInventoryDTO.getId(), returnPurchaseInventoryDTO.getSupplierId());
    supplierPayableController.setSupplierPayableService((SupplierPayableService) supplierPayableService);
    supplierPayableController.initPurchaseInventoryPayable(model);
    PayableDTO payableDTO = (PayableDTO) model.get("payableDTO");
    Assert.assertEquals("QQQ;", payableDTO.getMaterialName());

  }


  /**
   * 创建应付款
   *
   *
   * @param shopId
   * @param num
   * @return
   */
  public List<PayableDTO> createPayables(Long shopId, int num, Long supplierId) {
    List<PayableDTO> payableDTOs = new ArrayList<PayableDTO>();
    for (int i = 0; i < num; i++) {
      PayableDTO payableDTO = new PayableDTO();
      payableDTO.setDeduction(20d);
      payableDTO.setAmount(120d);
      payableDTO.setPaidAmount(20d);
      payableDTO.setCreditAmount(80d);
      payableDTO.setMaterialName("材料" + i);
      payableDTO.setShopId(shopId);
      payableDTO.setSupplierId(supplierId);
      payableDTO.setPurchaseInventoryId(100000000000l + i);
      payableDTO.setStatus(PayStatus.USE);
      payableDTO.setPayTime(System.currentTimeMillis());
      payableDTO.setStrikeAmount(0d);
      payableDTO.setOrderType(OrderTypes.INVENTORY);
      payableDTO.setOrderDebtType(null);
      //这里set0只是为了保证单元测试正确
      payableDTO.setStatementAccount(0D);
      payableDTO.setStatementAccountOrderId(0L);
      payableDTO.setOrderDebtType(OrderDebtType.SUPPLIER_DEBT_PAYABLE);
      payableDTO = supplierPayableService.savePayable(payableDTO);
      payableDTO.setTotalCreditAmount(0D);
      payableDTO.setLastPayer("结算人");
      payableDTO.setLastPayerId(111L);

      payableDTOs.add(payableDTO);


    }
    return payableDTOs;
  }

  /**
   * 创建付款历史
   *
   * @param shopId
   * @return
   * @author zhangchuanlong
   */
  public PayableHistoryDTO createPayableHistoryDTO(Long shopId, Long supplierId) {
    PayableHistoryDTO payableHistoryDTO = new PayableHistoryDTO();
    payableHistoryDTO.setShopId(shopId);
    payableHistoryDTO.setActuallyPaid(280d);         //实付
    payableHistoryDTO.setBankCardAmount(100d);        //银行卡
    payableHistoryDTO.setCash(100d);                           //现金
    payableHistoryDTO.setCheckNo("11111111111");         //支票号码
    payableHistoryDTO.setCheckAmount(50d);                  //支票
    payableHistoryDTO.setCreditAmount(100d);                   //挂账
    payableHistoryDTO.setDeduction(20d);                             //扣款
    payableHistoryDTO.setDepositAmount(30d);                    //定金
    payableHistoryDTO.setSupplierId(supplierId);
    payableHistoryDTO.setStrikeAmount(0d);
    return payableHistoryDTO;
  }

  /**
   * 创建客户付款记录
   *
   * @param shopId
   * @param count
   * @return
   */
  public List<PayableHistoryRecordDTO> createPayableHistoryRecord(Long shopId, int count) {
    List<PayableHistoryRecordDTO> payableHistoryRecordDTOs = new ArrayList<PayableHistoryRecordDTO>();
    for (int i = 0; i < count; i++) {
      PayableHistoryRecordDTO payableHistoryRecordDTO = new PayableHistoryRecordDTO();
      payableHistoryRecordDTO.setStatus(PayStatus.USE);
      payableHistoryRecordDTO.setActuallyPaid(80d);
      payableHistoryRecordDTO.setAmount(100d);
      payableHistoryRecordDTO.setBankCardAmount(20d);
      payableHistoryRecordDTO.setCash(20d);
      payableHistoryRecordDTO.setCheckAmount(20d);
      payableHistoryRecordDTO.setCheckNo("1111111111111");
      payableHistoryRecordDTO.setCreditAmount(20d);
      payableHistoryRecordDTO.setDeduction(20d);
      payableHistoryRecordDTO.setPaidTime(System.currentTimeMillis());
      payableHistoryRecordDTO.setMaterialName("材料" + i);
      payableHistoryRecordDTO.setDepositAmount(20d);
      payableHistoryRecordDTO.setShopId(shopId);
      payableHistoryRecordDTO.setSupplierId(11111111111111l);
      payableHistoryRecordDTO.setPaymentType(PaymentTypes.INVENTORY);
      payableHistoryRecordDTO.setDayType(DayType.OTHER_DAY);
      payableHistoryRecordDTO = supplierPayableService.saveOrUpdatePayHistoryRecord(payableHistoryRecordDTO);
      payableHistoryRecordDTOs.add(payableHistoryRecordDTO);
    }
    return payableHistoryRecordDTOs;
  }
}
