//package com.bcgogo.txn;
//
//import com.bcgogo.AbstractTest;
//import com.bcgogo.enums.DebtStatus;
//import com.bcgogo.exception.BcgogoException;
//import com.bcgogo.product.dto.ProductDTO;
//import com.bcgogo.search.dto.RecOrPayIndexDTO;
//import com.bcgogo.txn.dto.DebtDTO;
//import com.bcgogo.txn.dto.SalesOrderDTO;
//import com.bcgogo.user.dto.CustomerDTO;
//import com.bcgogo.utils.DateUtil;
//import junit.framework.Assert;
//import org.junit.Before;
//import org.junit.Test;
//import org.springframework.mock.web.MockHttpServletRequest;
//import org.springframework.mock.web.MockHttpServletResponse;
//import org.springframework.ui.ModelMap;
//
//import java.text.SimpleDateFormat;
//import java.util.Date;
//
///**
// * Created by IntelliJ IDEA.
// * User: caiweili
// * Date: 2/19/12
// * Time: 4:47 PM
// * To change this template use File | Settings | File Templates.
// */
//public class PayDebtTest  extends AbstractTest {
//
//  @Before
//  public void setUp() throws Exception {
//    goodsStorageController = new GoodStorageController();
//    buyController = new RFGoodBuyController();
//    saleController = new GoodSaleController();
//    txnController=new TxnController();
//    arrearsController=new ArrearsController();
//    request = new MockHttpServletRequest();
//    response = new MockHttpServletResponse();
//    initTxnControllers(goodsStorageController);
//  }
//
//  @Test
//  public void testPayDebt() throws Exception{
//    Long shopId = createShop();
//    ProductDTO productDTO = new ProductDTO();
//    productDTO.setName("MyProduct"+ System.currentTimeMillis());
//    productDTO.setSpec("1232");
//    productDTO.setProductVehicleBrand("多款");
//    addInventory(shopId, productDTO, 100, 10);
//
//    SalesOrderDTO salesOrderDTO = createSalesOrderDTO(productDTO,30,100,1000);
//    ModelMap model = new ModelMap();
//    saleController.saveSale(model, salesOrderDTO, request,  "2012-03-01");
//
//    salesOrderDTO = createSalesOrderDTO(productDTO,40,100,1000);
//    model = new ModelMap();
//    saleController.saveSale(model, salesOrderDTO, request,  "2012-03-05");
//    SalesOrderDTO returnedSalesOrderDTO = (SalesOrderDTO) model.get("salesOrderDTO");
//
//    model = new ModelMap();
//    RecOrPayIndexDTO  recOrPayIndexDTO=null;
//    arrearsController.getReceivables(model,request, recOrPayIndexDTO);
//
//    String customerId = returnedSalesOrderDTO.getCustomerId().toString();
//    double totalAmount = 0;
//    double payedAmount = 3500;
//    double owedAmount= 1000;
//    String receivableOrderIdsString ="";
//    String orderTotalsString="";
//    String orderOwedsString="";
//    String orderPayedsString="";
//    String debtIdsString="";
//    String huankuanTime="2012-03-19";
//
//    DebtDTO[] debtDTOs = (DebtDTO[])model.get("DebtDTOs");
//
//    Assert.assertEquals(2, debtDTOs.length);
//
//    for (int i = 0; i < debtDTOs.length; i++) {
//      DebtDTO debtDTO = debtDTOs[i];
//      totalAmount += debtDTO.getDebt();
//      if (i == 0) {
//        receivableOrderIdsString += debtDTO.getRecievableId();
//        orderOwedsString += String.valueOf(debtDTO.getDebt());
//        orderPayedsString += String.valueOf(debtDTO.getSettledAmount());
//        debtIdsString += debtDTO.getId();
//        orderTotalsString +=debtDTO.getTotalAmount();
//      } else {
//        receivableOrderIdsString += ",";
//        receivableOrderIdsString += debtDTO.getRecievableId();
//        orderOwedsString += ",";
//        orderOwedsString += String.valueOf(debtDTO.getDebt());
//        orderPayedsString += ",";
//        orderPayedsString += String.valueOf(debtDTO.getSettledAmount());
//        debtIdsString += ",";
//        debtIdsString += debtDTO.getId();
//        orderTotalsString +=",";
//        orderTotalsString +=debtDTO.getTotalAmount();
//      }
//    }
//    request.setParameter("cashAmount","3500");
//    request.setParameter("discount","500");
//    txnController.payAll(model, request, customerId,
//        totalAmount,
//        payedAmount,
//        owedAmount,
//        "",
//        "",
//        receivableOrderIdsString,
//        "",
//        orderTotalsString,
//        orderOwedsString,
//        orderPayedsString,
//        returnedSalesOrderDTO.getCustomer(),
//        returnedSalesOrderDTO.getMobile(),
//        debtIdsString,
//        huankuanTime);
//
//    model = new ModelMap();
////    txnController.getReceivable(model,request,recOrPayIndexDTO);
//    debtDTOs = (DebtDTO[])model.get("DebtDTOs");
//
//    Assert.assertEquals(1, debtDTOs.length);
//
//  }
//
//  /**
//   * 欠款结算按时间区间查询
//   * @author zhangchuanlong
//   * @throws Exception
//   */
//  @Test
//  public void testDetailsArrears() throws Exception {
//    Long shopId = createShop();
//    CustomerDTO customerDTO = userService.createCustomer(createCustomer(shopId));
//    ModelMap model = new ModelMap();
//    request.getSession().setAttribute("shopId", shopId);
//    request.setParameter("startDate", "");
//    request.setParameter("endDate", "");
//    request.setParameter("pageNo","1") ;
////    数据库无数据！
//    String url = txnController.receivableSettle(model, request, customerDTO.getId());
//    Assert.assertEquals(null, model.get("DebtDTOs"));
//    Assert.assertEquals(customerDTO.getId(), model.get("customerId"));
//    Assert.assertEquals("", model.get("startDate"));
//    Assert.assertEquals("", model.get("endDate"));
//    Assert.assertEquals("/txn/detailsArrears", url);
////    总数据不超过页大小！
//    request.setParameter("startDate", DateUtil.format(DateUtil.YEAR_MONTH_DATE, new Date()) + " 00:00:00");
//    request.setParameter("endDate", DateUtil.format(DateUtil.YEAR_MONTH_DATE, new Date()) + " 23:59:59");
//    createDebt(shopId, customerDTO.getId(), 3);
//    url = txnController.receivableSettle(model, request, customerDTO.getId());
//    Assert.assertEquals("/txn/detailsArrears", url);
//     Assert.assertEquals(3,((DebtDTO[]) model.get("DebtDTOs")).length);
//    Assert.assertEquals(customerDTO.getId(), model.get("customerId"));
//    Assert.assertEquals(DateUtil.format(DateUtil.YEAR_MONTH_DATE, new Date()) + " 00:00:00", model.get("startDate"));
//    Assert.assertEquals(DateUtil.format(DateUtil.YEAR_MONTH_DATE, new Date()) + " 23:59:59", model.get("endDate"));
//    model.addAttribute("DebtDTOs",null) ;
//    //输入时间不满足条件
//    Calendar cal = Calendar.getInstance();
//    cal.add(Calendar.DATE, -1);
//    Date d = cal.getTime();
//    request.setParameter("startDate", DateUtil.format(DateUtil.YEAR_MONTH_DATE, d) + " 00:00:00");
//    request.setParameter("endDate", DateUtil.format(DateUtil.YEAR_MONTH_DATE, d) + " 23:59:59");
//    createDebt(shopId, customerDTO.getId(), 100);
//    url = txnController.receivableSettle(model, request, customerDTO.getId());
//    Assert.assertEquals("/txn/detailsArrears", url);
//    Assert.assertEquals(null, model.get("DebtDTOs"));
//    Assert.assertEquals(customerDTO.getId(), model.get("customerId"));
//    Assert.assertEquals(DateUtil.format(DateUtil.YEAR_MONTH_DATE, d) + " 00:00:00", model.get("startDate"));
//    Assert.assertEquals(DateUtil.format(DateUtil.YEAR_MONTH_DATE, d) + " 23:59:59", model.get("endDate"));
//      model.addAttribute("DebtDTOs",null) ;
//    //正常输入   、
//    request.setParameter("startDate", DateUtil.format(DateUtil.YEAR_MONTH_DATE, new Date()) + " 00:00:00");
//    request.setParameter("endDate", DateUtil.format(DateUtil.YEAR_MONTH_DATE, new Date()) + " 23:59:59");
//    createDebt(shopId, customerDTO.getId(), 100);
//     url = txnController.receivableSettle(model, request, customerDTO.getId());
//    Assert.assertNotNull(model.get("DebtDTOs"));
//      Assert.assertEquals(5,((DebtDTO[]) model.get("DebtDTOs")).length);
//    Assert.assertEquals("/txn/detailsArrears", url);
//    Assert.assertEquals(customerDTO.getId(), model.get("customerId"));
//    Assert.assertEquals(DateUtil.format(DateUtil.YEAR_MONTH_DATE, new Date()) + " 00:00:00", model.get("startDate"));
//    Assert.assertEquals(DateUtil.format(DateUtil.YEAR_MONTH_DATE, new Date()) + " 23:59:59", model.get("endDate"));
//
//  }
//
//
//  /**
//   * 创建欠款单
//   *
//   * @param shopId
//   * @param customerId
//   * @param count
//   * @author zhangchuanlong
//   * @throws BcgogoException
//   */
//  private void createDebt(Long shopId, Long customerId,int count) throws BcgogoException {
//    for(int i=0;i<count;i++){
//      DebtDTO debtDTO=new DebtDTO();
//      debtDTO.setDebt(40);
//      debtDTO.setContent("油漆");
//      debtDTO.setCustomerId(customerId);
//      debtDTO.setDate("2012-12-12");
//      debtDTO.setMaterial("轮胎");
//      debtDTO.setStatus(DebtStatus.ARREARS);
//      debtDTO.setShopId(shopId);
//      debtDTO.setTotalAmount(100);
//      debtDTO.setService("洗车");
//      debtDTO.setSettledAmount(60);
//      txnService.saveDebtDTO(debtDTO);
//      }
//  }
//
//  /**
//   * 创建customer
//   *
//   * @param shopId
//   * @author zhangchuanlong
//   * @return
//   * @throws Exception
//   */
//    public CustomerDTO createCustomer(Long shopId) throws Exception {
//    CustomerDTO customerDTO = new CustomerDTO();
//     customerDTO.setShopId(shopId);
//    customerDTO.setName("miss");
//    customerDTO.setMobile("15151771582");
//    customerDTO.setContact("tang");
//    customerDTO.setBirthday(DateUtil.convertDateStringToDateLong("MM-dd", new SimpleDateFormat("MM-dd").format(new Date())));
//    return customerDTO;
//  }
//}
