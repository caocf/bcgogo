package com.bcgogo.txn;

import com.bcgogo.AbstractTest;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.search.dto.ItemIndexDTO;
import com.bcgogo.txn.dto.RepairOrderDTO;
import com.bcgogo.txn.dto.RepairOrderItemDTO;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.SearchConstant;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.ui.ModelMap;

import java.text.DecimalFormat;
import java.util.Random;

/**
 * Created by IntelliJ IDEA.
 * User: zhangchuanlong
 * Date: 12-7-16
 * Time: 上午9:13
 *  商品历史记录
 */
public class GoodsHistoryControllerTest extends AbstractTest{
    @Before
  public void setUp() throws Exception {
    goodsHistoryController = new GoodsHistoryController();
    repairController = new RepairController();
      initTxnControllers(repairController);
    request = new MockHttpServletRequest();
    response = new MockHttpServletResponse();
  }

  /**
   *   查询当天新增客户【车辆】历史记录
   *
   * @author zhangchuanlong
   * @throws Exception
   */
   @Test
  public void testSearchCarHistoryByNewVehicle() throws Exception {
     /*做一施工销售单*/
     Random random = new Random(47);
     String licenceNo = "苏E11559";
//     String btnType = "save";
     Long shopId = createShop();
     DecimalFormat df = new DecimalFormat("#.00");
     ModelMap model = new ModelMap();
     Double totalAmount = 0.0D;
     String timeStr = "2012-07-16 10:13";
     String hkTime = "2012-07-26";
     StringBuffer sb = new StringBuffer();
     char c = 'A';
     int itemNum = 100;
     /*施工销售单明细*/
     RepairOrderItemDTO[] repairOrderItemDTOs = new RepairOrderItemDTO[itemNum];
     for (int i = 0; i < itemNum; i++) {
       double amount = new Double(df.format(random.nextDouble() * 100));
       double price = new Double(df.format(random.nextDouble() * 100));
       repairOrderItemDTOs[i] = new RepairOrderItemDTO();
       repairOrderItemDTOs[i].setAmount(amount);
       repairOrderItemDTOs[i].setPrice(price);
       repairOrderItemDTOs[i].setTotal(amount * price);
       repairOrderItemDTOs[i].setProductName("产品" + ((char) (c + i)));
       repairOrderItemDTOs[i].setSpec("规格" + ((char) (c + i)));
       repairOrderItemDTOs[i].setInventoryAmount(0.0D);
       repairOrderItemDTOs[i].setProductType(SearchConstant.PRODUCT_PRODUCTSTATUS_SPECIAL);
       totalAmount = totalAmount + repairOrderItemDTOs[i].getTotal();
     }
     for (int i = 0; i < itemNum; i++) {
       sb.append(repairOrderItemDTOs[itemNum - 1 - i].getProductName()).append(",");
     }
   /*施工销售单*/
     RepairOrderDTO repairOrderDTO = new RepairOrderDTO();
     repairOrderDTO.setVechicle(licenceNo);
     repairOrderDTO.setLicenceNo(licenceNo);
     repairOrderDTO.setCustomerName("张先生");
     repairOrderDTO.setStartMileage(200D);
     repairOrderDTO.setFuelNumber("3");
     repairOrderDTO.setStartDate(DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN, timeStr));
     repairOrderDTO.setEndDateStr(timeStr);
     repairOrderDTO.setTotal(totalAmount);
     repairOrderDTO.setServiceType(OrderTypes.REPAIR);
     repairOrderDTO.setBrand("奇瑞");
     repairOrderDTO.setModel("旗云3");
     repairOrderDTO.setYear("2010");
     repairOrderDTO.setEngine("2.0L");
     repairOrderDTO.setSettledAmount(totalAmount / 2);
     repairOrderDTO.setMaintainTimeStr("2012-07-17");
     repairOrderDTO.setInsureTimeStr("2012-07-17");
     repairOrderDTO.setExamineTimeStr("2012-07-17");
     repairOrderDTO.setMobile("13512012567");
     repairOrderDTO.setItemDTOs(repairOrderItemDTOs);
     Double pm = totalAmount - repairOrderDTO.getSettledAmount();
     repairOrderDTO.setDebt(pm);
     repairOrderDTO.setHuankuanTime(hkTime);

     request.setParameter("vehicleNumber", licenceNo);
     request.getSession().setAttribute("shopId", shopId);
     //保存施工维修单
//     txnController.saveRepairOrder(model, repairOrderDTO, request, btnType);
     repairController.dispatchRepairOrder(model, repairOrderDTO, request);
     unitTestSleepSecond();
     ItemIndexDTO itemIndexDTO = new ItemIndexDTO();
     itemIndexDTO.setVehicle(licenceNo);
     itemIndexDTO.setServices("维修美容");
     itemIndexDTO.setItemName("产品");
     itemIndexDTO.setPageNo("5");
     itemIndexDTO.setStartDateStr(DateUtil.dateLongToStr(DateUtil.getStartTimeOfToday()));
     itemIndexDTO.setEndDateStr(DateUtil.dateLongToStr(DateUtil.getEndTimeOfToday()));
     String url = goodsHistoryController.searchCarHistoryByNewVehicle(model, request, itemIndexDTO);

     Assert.assertEquals("/txn/carHistoryByTodayCustomer", url);
  }

}
