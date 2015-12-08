package com.bcgogo.txn;

import com.bcgogo.common.Pager;
import com.bcgogo.common.SessionConstants;
import com.bcgogo.common.WebUtil;
import com.bcgogo.enums.ItemTypes;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.search.dto.ItemIndexDTO;
import com.bcgogo.search.model.ItemIndex;
import com.bcgogo.search.service.IItemIndexService;
import com.bcgogo.search.service.ISearchService;
import com.bcgogo.search.service.SearchService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.utils.DateUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: wjl
 * Date: 11-9-30
 * Time: 上午9:37
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/goodsHistory.do")
@SessionAttributes(SessionConstants.DEFAULT_HISTORY_ORDER_TYPE)
public class GoodsHistoryController extends AbstractTxnController {
  private static final Logger LOG = LoggerFactory.getLogger(GoodsHistoryController.class);

  private static final int START_PAGE_NO = 1;
  private static final int RECORDS_PER_PAGE = 5;

  private String getTimeStr(String amount, boolean end) {
    if (StringUtils.isBlank(amount)) return null;
    Calendar cal = Calendar.getInstance();
    SimpleDateFormat dateformat1 = new SimpleDateFormat("yyyy-MM-dd");
    cal.add(Calendar.DATE, Integer.parseInt(amount));
    String time = " 00:00";
    if (end) time = " 23:59";
    return dateformat1.format(cal.getTime()) + time;
  }

  @RequestMapping(params = "method=createGoodsHistory")
  public String createGoodsHistory(ModelMap model, HttpServletRequest request) {
    OrderTypes orderType = OrderTypes.valueOf(request.getParameter("orderType"));
    String supplierName = request.getParameter("supplierName");
    String itemName = request.getParameter("itemName");
    String itemBrand = request.getParameter("brand");
    String itemSpec = request.getParameter("spec");
    String itemModel = request.getParameter("model");
    //第一次进入历史记录时的标志
    String flag = "fresh";
    request.setAttribute("flag", flag);
    ItemIndexDTO itemIndex = new ItemIndexDTO();
    itemIndex.setPageNo(String.valueOf(START_PAGE_NO));
    itemIndex.setCustomerOrSupplierName(supplierName);
    itemIndex.setOrderType(orderType);
    if (OrderTypes.PURCHASE == orderType || OrderTypes.INVENTORY == orderType || OrderTypes.SALE == orderType || OrderTypes.RETURN == orderType) {
      itemIndex.setStartDateStr(DateUtil.getFirtDayTimeOfMonth());
      itemIndex.setEndDateStr(DateUtil.getLastDayOfMonth());
    } else if (OrderTypes.WASH == orderType) {    // TODO: jmc 还会使用goodsHistory页面展示洗车记录吗??
      //shao
      itemIndex.setItemName(itemName);
      itemIndex.setItemBrand(itemBrand);
      itemIndex.setItemSpec(itemSpec);
      itemIndex.setItemModel(itemModel);
      String startOfYear = null;
      String endOfYear = null;
      try {
        startOfYear = DateUtil.getStartOfYear();
        endOfYear = DateUtil.getEndOfYear();
      } catch (ParseException e) {
        LOG.error(e.getMessage(), e);
      }
      itemIndex.setStartDateStr(startOfYear);
      itemIndex.setEndDateStr(endOfYear);
    } else {
      itemIndex.setRepairOrderType(true);
    }
//    addOrderTypesOptions(model, request);
    model.addAttribute(SessionConstants.DEFAULT_HISTORY_ORDER_TYPE, orderType);
    model.addAttribute("command", itemIndex);
    return "/txn/goodsHistory";
  }

  @RequestMapping(params = "method=searchGoodsHistory")
  public String searchGoodsHistory(ModelMap model, HttpServletRequest request, ItemIndexDTO itemIndex) throws Exception {
    Long shopId = WebUtil.getShopId(request);
    String startDateStr = itemIndex.getStartDateStr();
    String endDateStr = itemIndex.getEndDateStr();
    String pageNoStr = itemIndex.getPageNo();
    Integer pageNo = pageNoStr != null && !"".equals(pageNoStr) ? Integer.parseInt(pageNoStr) : START_PAGE_NO;
    List<OrderTypes> orderTypes = itemIndex.getSelectedOrderTypes();
    if (orderTypes == null || orderTypes.isEmpty()) {
      orderTypes = new ArrayList<OrderTypes>();
      OrderTypes defaultOrderType = (OrderTypes) model.get(SessionConstants.DEFAULT_HISTORY_ORDER_TYPE);
      if (defaultOrderType != null) {
        orderTypes.add(defaultOrderType);
    }
    }
    try {
      Long startDateLong = null != startDateStr && !"".equals(startDateStr) ?
          new Long(DateUtil.convertDateStringToDateLong("yyyy-MM-dd HH:mm", startDateStr)) : null;
      Long endDateLong = null != endDateStr && !"".equals(endDateStr) ?
          new Long(DateUtil.convertDateStringToDateLong("yyyy-MM-dd HH:mm", endDateStr)) : null;
      if (startDateLong != null && endDateLong != null && startDateLong > endDateLong) {
        Long tempDataLong = endDateLong;
        endDateLong = startDateLong;
        startDateLong = tempDataLong;
      }

      itemIndex.setShopId(shopId);
      itemIndex.setItemType(ItemTypes.MATERIAL);
      itemIndex.setSelectedOrderTypes(orderTypes);
      List<ItemIndex> itemIndexList = searchService.searchItemIndex(itemIndex, startDateLong, endDateLong, (pageNo - 1) * RECORDS_PER_PAGE, RECORDS_PER_PAGE);
      if ((itemIndexList == null || itemIndexList.size() <= 0) && pageNo != START_PAGE_NO) {
        itemIndexList = searchService.searchItemIndex(itemIndex, startDateLong, endDateLong, (pageNo - 2) * RECORDS_PER_PAGE, RECORDS_PER_PAGE);
        itemIndex.setPageNo((pageNo - 1) + "");
      }
      List<ItemIndexDTO> itemIndexListResult = null;
      if (itemIndexList != null) {
        itemIndexListResult = new ArrayList<ItemIndexDTO>();
        for (ItemIndex item : itemIndexList) {
          if (item.getOrderId() == null) {
            continue;
          }
          ItemIndexDTO itemIndexDTO = item.toDTO();
          itemIndexListResult.add(itemIndexDTO);
          }
          }

      int totalRows = searchService.countGoodsHistory(itemIndex, startDateLong, endDateLong);
      Pager pager = new Pager(totalRows, pageNo, RECORDS_PER_PAGE);

//      addOrderTypesOptions(model, request);
      model.addAttribute("pager", pager);
      model.addAttribute("command", itemIndex);
      model.addAttribute("itemIndexList", itemIndexListResult);
    } catch (Exception e) {
      LOG.debug("/goodsHistory.do");
      LOG.debug("method=searchGoodsHistory");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.debug(itemIndex.toString());
      WebUtil.reThrow(LOG, e);
    }
    return "/txn/goodsHistory";
  }

  @RequestMapping(params = "method=createCarHistory")
  public String createCarHistory(ModelMap model, HttpServletRequest request) throws UnsupportedEncodingException {
    request.setCharacterEncoding("UTF-8");
    ItemIndexDTO itemIndex = new ItemIndexDTO();
    String orderType = request.getParameter("orderType");
    OrderTypes orderTypeEnum = OrderTypes.valueOf(orderType);
    String licenceNo = request.getParameter("licenceNo");
    String searchFlag = request.getParameter("searchflag");

    itemIndex.setStartDateStr(getTimeStr(searchFlag, false));
    itemIndex.setEndDateStr(getTimeStr(searchFlag, true));
    itemIndex.setPageNo(String.valueOf(START_PAGE_NO));
    itemIndex.setVehicle(licenceNo);
    itemIndex.setOrderType(orderTypeEnum);
    model.addAttribute("command", itemIndex);
    model.addAttribute("issubmit", request.getParameter("issubmit"));
    return "/txn/carHistory";
  }

  /**
   * 当天新增客户【车辆】历史记录 弹出页面初始化
   *
   * @param model
   * @param request
   * @return
   * @throws UnsupportedEncodingException
   * @author zhangchuanlong
   */
  @RequestMapping(params = "method=createCarHistoryByTodayNewVehicle")
  public String createCarHistoryByTodayNewVehicle(ModelMap model, HttpServletRequest request) throws UnsupportedEncodingException {
//    request.setCharacterEncoding("UTF-8");
    ItemIndexDTO itemIndex = new ItemIndexDTO();
    String licenceNo = request.getParameter("licenceNo");
    String searchFlag = request.getParameter("searchflag");
    itemIndex.setStartDateStr(getTimeStr(searchFlag, false));
    itemIndex.setEndDateStr(getTimeStr(searchFlag, true));
    itemIndex.setPageNo("1");
    itemIndex.setVehicle(licenceNo);
    model.addAttribute("command", itemIndex);
    model.addAttribute("issubmit", request.getParameter("issubmit"));
    return "/txn/carHistoryByTodayCustomer";
  }
  //注释 存在问题 从2.0直接拷贝过来
  @RequestMapping(params = "method=searchCarHistory")
  public String searchCarHistory(ModelMap model, HttpServletRequest request, ItemIndexDTO itemIndex) throws Exception {
    IItemIndexService itemIndexService = ServiceManager.getService(IItemIndexService.class);
    ISearchService searchService = ServiceManager.getService(SearchService.class);
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    String licenceNo = request.getParameter("licenceNo");
    String pageNoStr = itemIndex.getPageNo();
    Integer pageNo = pageNoStr != null && !"".equals(pageNoStr) ? Integer.parseInt(pageNoStr) : 1;
    itemIndex.setVehicle(licenceNo);
    try {
      Long startDateLong = DateUtil.convertDateStringToDateLong("yyyy-MM-dd HH:mm", itemIndex.getStartDateStr());
      Long endDateLong = DateUtil.convertDateStringToDateLong("yyyy-MM-dd HH:mm", itemIndex.getEndDateStr());
      int totalRows = searchService.countRepairOrderHistory(shopId, itemIndex.getVehicle(), itemIndex.getServices(), itemIndex.getItemName(), startDateLong, endDateLong);
      List<ItemIndexDTO> itemIndexList = itemIndexService.getRepairOrderHistory(
          shopId, itemIndex.getVehicle(), itemIndex.getServices(), itemIndex.getItemName(), startDateLong, endDateLong, (pageNo - 1) * 5, 5);
      if (itemIndexList != null && itemIndexList.size() > 0) {
        for (ItemIndexDTO item : itemIndexList) {
          if (OrderTypes.REPAIR == item.getOrderType()) {
            item.setServices(itemIndexService.getItemInfo(item.getOrderId(), ItemTypes.SERVICE));
            item.setItemName(itemIndexService.getItemInfo(item.getOrderId(), ItemTypes.MATERIAL));
          }
          if (OrderTypes.WASH_BEAUTY == item.getOrderType()) {
            item.setServices(itemIndexService.getItemInfo(item.getOrderId(), ItemTypes.WASH));
            item.setItemName(null);
          }
          if (OrderTypes.RECHARGE == item.getOrderType()) {
            item.setServices(itemIndexService.getItemInfo(item.getOrderId(), ItemTypes.RECHARGE));
            //  item.setPaymentTimeStr(item.getPaymentTime() == null ? null : item.getPaymentTime());
            item.setOrderStatusStr(item.getOrderStatus() == null ? "" : item.getOrderStatus().getName());
            item.setOrderTypeStr(item.getOrderType() == null ? "" : item.getOrderType().getName());
            item.setItemName(null);
          }
          if (OrderTypes.WASH_MEMBER == item.getOrderType()) {
            item.setOrderTypeStr(item.getOrderType().getName());
            item.setServices(item.getItemName());
            item.setItemName(null);
            item.setOrderTotalAmount(null);
          }
          if (OrderTypes.WASH_BEAUTY == item.getOrderType() || OrderTypes.RECHARGE == item.getOrderType()) {
            item.setItemPrice(item.getItemPrice());
          } else if (OrderTypes.WASH_MEMBER == item.getOrderType()) {
            item.setItemPrice(null);
            item.setOrderTypeStr(item.getItemName());
          } else {
            if (item.getItemPrice() != null && item.getItemCount() != null) {
              item.setItemPrice(item.getItemPrice() * item.getItemCount());
            } else {
              item.setItemPrice(item.getItemPrice());
            }
          }
            String createDate = DateUtil.convertDateLongToDateString("yyyy-MM-dd", item.getOrderTimeCreated());
            item.setOrderTimeCreatedStr(createDate);
          item.setOrderStatusStr(item.getOrderStatus() == null ? "" : item.getOrderStatus().getName());
            item.setOrderTypeStr(item.getOrderType() == null ? "" : item.getOrderType().getName());
            boolean arrearsIsNull = item.getArrears() == null || item.getArrears() == 0;
            item.setPaymentTimeStr(arrearsIsNull ? "" : DateUtil.convertDateLongToDateString("yyyy-MM-dd", item.getPaymentTime()));
            item.setArrears(arrearsIsNull ? null : item.getArrears());
          }
      }

      List<ItemIndexDTO> itemIndexDTOs = new ArrayList<ItemIndexDTO>();
      Map map = new HashMap();
      if (null != itemIndexList && 0 != itemIndexList.size()) {
        for (ItemIndexDTO itemIndexDTO : itemIndexList) {
          map.put(itemIndexDTO.getOrderId(), itemIndexDTO);
          }
      }
      Iterator iterator = map.keySet().iterator();

      while (iterator.hasNext()) {
        itemIndexDTOs.add((ItemIndexDTO) map.get(iterator.next()));
      }

      if (null == itemIndexDTOs || 0 == itemIndexDTOs.size()) {
          itemIndexDTOs = null;
      }
      Pager pager = new Pager(totalRows, pageNo, 5);
      model.addAttribute("pager", pager);
      model.addAttribute("command", itemIndex);
      model.addAttribute("itemIndexList", itemIndexDTOs);
    } catch (Exception e) {
      LOG.debug("/goodsHistory.do");
      LOG.debug("method=searchCarHistory");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.debug(itemIndex.toString());
      LOG.error(e.getMessage(), e);
    }
    return "/txn/carHistory";
  }

  /**
   *  查询当天新增客户【车辆】历史记录
   *
   * @param model
   * @param request
   * @param itemIndex
   * @return
   * @author zhangchuanlong
   */
  @RequestMapping(params = "method=searchCarHistoryByNewVehicle")
  public String searchCarHistoryByNewVehicle(ModelMap model, HttpServletRequest request, ItemIndexDTO itemIndex) {
    IItemIndexService itemIndexService = ServiceManager.getService(IItemIndexService.class);
    IUserService userService=ServiceManager.getService(IUserService.class);
    ISearchService searchService = ServiceManager.getService(SearchService.class);
    Long shopId = (Long) request.getSession().getAttribute("shopId");
     String licenceNo = request.getParameter("licenceNo");
    itemIndex.setVehicle(licenceNo);
    String pageNoStr = itemIndex.getPageNo();
    Integer pageNo = pageNoStr != null && !"".equals(pageNoStr) ? Integer.parseInt(pageNoStr) : 1;
    try {
      Long fromDateTime = DateUtil.convertDateStringToDateLong("yyyy-MM-dd HH:mm", itemIndex.getStartDateStr());
      Long endDateTime = DateUtil.convertDateStringToDateLong("yyyy-MM-dd HH:mm", itemIndex.getEndDateStr());
      /*总记录数*/
      int totalRows = userService.countRepairOrderHistoryByNewVehicle(shopId, itemIndex.getVehicle(), itemIndex.getServices(), itemIndex.getItemName(), fromDateTime, endDateTime);
      Pager pager = new Pager(totalRows, pageNo, RECORDS_PER_PAGE);
     /*记录列表*/
      Long startTime = System.currentTimeMillis();       //开始查询时间
      List<ItemIndexDTO> itemIndexDTOList = userService.getRepairOrderHistoryByNewVehicle(
          shopId, itemIndex.getVehicle(), itemIndex.getServices(), itemIndex.getItemName(), fromDateTime, endDateTime, pager);
      LOG.info("查询当日新增车辆历史记录共用了" + (System.currentTimeMillis() - startTime) + "毫秒");
      if (itemIndexDTOList != null && itemIndexDTOList.size() > 0) {
        for (ItemIndexDTO item : itemIndexDTOList) {
          /**
           *         维修美容
           *         洗车
           *         材料销售
           *         会员洗车
           *         洗车充值
           */
          /*如果是维修单*/
          if (OrderTypes.REPAIR == item.getOrderType()) {
            /*将施工项目添加到item当中*/
            item.setServices(itemIndexService.getItemInfo(item.getOrderId(), ItemTypes.SERVICE));
            /*将材料品名内容添加到item当中*/
            item.setItemName(itemIndexService.getItemInfo(item.getOrderId(), ItemTypes.MATERIAL));
          }
          /*如果是洗车单*/
          if (OrderTypes.WASH_BEAUTY == item.getOrderType()) {
            /*将洗车服务内容添加到item当中*/
            item.setServices(itemIndexService.getItemInfo(item.getOrderId(), ItemTypes.WASH));
            /*材料品名设置null*/
            item.setItemName(null);
          }
          /*如果是洗车充值单*/
          if (OrderTypes.RECHARGE == item.getOrderType()) {
            /*将“洗车充值”添加到item中*/
            item.setServices(itemIndexService.getItemInfo(item.getOrderId(), ItemTypes.RECHARGE));
           /*设置状态*/
            item.setOrderStatusStr(item.getOrderStatus() == null ? "" : item.getOrderStatus().getName());
            item.setOrderTypeStr(item.getOrderType().getName());
            item.setItemName(null);
          }
          /*如果是会员洗车*/
          if (OrderTypes.WASH_MEMBER == item.getOrderType()) {
            /*将"洗车“添加到item中*/
            item.setOrderTypeStr(item.getOrderType().getName());
            item.setServices(item.getItemName());
            item.setItemName(null);
            item.setOrderTotalAmount(null);
          }
          /*如果是非会员洗车或洗车充值*/
          if (OrderTypes.WASH_BEAUTY == item.getOrderType() || OrderTypes.RECHARGE == item.getOrderType()) {
            item.setItemPrice(item.getItemPrice());
         /*如果是会员洗车*/
          } else if (OrderTypes.WASH_MEMBER == item.getOrderType()) {
            item.setItemPrice(null);
            item.setOrderTypeStr(item.getItemName());
          } else {     /*既不是会员洗车也不是非会员洗车*/
            if (item.getItemPrice() != null && item.getItemCount() != null) {
              item.setItemPrice(item.getItemPrice() * item.getItemCount());
            } else {
              item.setItemPrice(item.getItemPrice());
            }
          }
          /*设置单据类型*/
          item.setOrderTypeStr(item.getOrderType() == null ? "" : item.getOrderType().getName());
          /*设置单据状态*/
          item.setOrderStatusStr(item.getOrderStatus() == null ? "" : item.getOrderStatus().getName());
          boolean arrearsIsNull = item.getArrears() == null || item.getArrears() == 0;
          item.setPaymentTimeStr(arrearsIsNull ? "" : DateUtil.convertDateLongToDateString("yyyy-MM-dd", item.getPaymentTime()));
          item.setArrears(arrearsIsNull ? null : item.getArrears());
        }
      }
      List<ItemIndexDTO> itemIndexDTOs = new ArrayList<ItemIndexDTO>();
      Map map = new HashMap();
      if (null != itemIndexDTOList && 0 != itemIndexDTOList.size()) {
        for (ItemIndexDTO itemIndexDTO : itemIndexDTOList) {
          map.put(itemIndexDTO.getOrderId(), itemIndexDTO);
        }
      }
      Iterator iterator = map.keySet().iterator();
      while (iterator.hasNext()) {
        itemIndexDTOs.add((ItemIndexDTO) map.get(iterator.next()));
      }
      if (null == itemIndexDTOs || 0 == itemIndexDTOs.size()) {
        itemIndexDTOs = null;
      }
      model.addAttribute("pager", pager);
      model.addAttribute("command", itemIndex);
      model.addAttribute("itemIndexList", itemIndexDTOs);
    } catch (Exception e) {
      LOG.debug("/goodsHistory.do");
      LOG.debug("method=searchCarHistoryByNewVehicle");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.debug(itemIndex.toString());
      LOG.error(e.getMessage(), e);
    }
    return "/txn/carHistoryByTodayCustomer";
  }
}
