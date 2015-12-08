package com.bcgogo.stat;

import com.bcgogo.common.Pager;
import com.bcgogo.common.WebUtil;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.enums.CategoryType;
import com.bcgogo.enums.ItemTypes;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.product.service.IProductService;
import com.bcgogo.search.client.SolrClientHelper;
import com.bcgogo.search.dto.*;
import com.bcgogo.search.service.order.ISearchOrderService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.BusinessStatDTO;
import com.bcgogo.txn.dto.CategoryDTO;
import com.bcgogo.txn.dto.PrintTemplateDTO;
import com.bcgogo.txn.dto.ServiceDTO;
import com.bcgogo.txn.service.IPrintService;
import com.bcgogo.txn.service.ITxnService;
import com.bcgogo.txn.service.RFITxnService;
import com.bcgogo.user.dto.CustomerDTO;
import com.bcgogo.user.model.Customer;
import com.bcgogo.user.model.Supplier;
import com.bcgogo.user.service.ICustomerService;
import com.bcgogo.user.service.utils.BcgogoShopLogicResourceUtils;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.StatConstant;
import com.bcgogo.utils.TxnConstant;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.StringResourceLoader;
import org.apache.velocity.runtime.resource.util.StringResourceRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.ParseException;
import java.util.*;

/**
 * 分项统计专用controller
 * Created by IntelliJ IDEA.
 * User: xzhu
 */
@Controller
@RequestMapping("/itemStat.do")
public class ItemStatController {
  private static final Log LOG = LogFactory.getLog(BizStatController.class);

  private static final int DEFAULT_MAX_ROWS = 15;
  private static final int DEFAULT_START_PAGE = 1;

  /**
   * @param model model
   * @return 用于向页面跳转 什么事情都不做 分项统计
   */
  @RequestMapping(params = "method=getItemStat")
  public String getItemStat(ModelMap model, HttpServletRequest request) {
    String statType = null;
    try {
      statType = request.getParameter("type");
      if (StringUtils.isEmpty(statType)) {
        return "/";
      }
      String startTimeStr = DateUtil.getFirtDayOfMonth();
      String endTimeStr = DateUtil.convertDateLongToString(System.currentTimeMillis(), DateUtil.DATE_STRING_FORMAT_DAY);
      model.addAttribute("startTimeStr", startTimeStr);
      model.addAttribute("endTimeStr", endTimeStr);
    } catch (Exception e) {
      LOG.debug("/itemStat.do");
      LOG.debug("method=getItemStat");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
    }
    if (StatConstant.CUSTOMER_STATISTICS.equals(statType)) {
      request.setAttribute("wholesalerVersion", ConfigUtils.isWholesalerVersion(WebUtil.getShopVersionId(request)));
      return "stat/businessAnalysis/customerStatistics";
    } else if (StatConstant.SUPPLIER_STATISTICS.equals(statType)) {
      return "stat/businessAnalysis/supplierStatistics";
    } else if (StatConstant.CATEGORY_STATISTICS.equals(statType)) {
      request.setAttribute("wholesalerVersion", ConfigUtils.isWholesalerVersion(WebUtil.getShopVersionId(request)));
      return "stat/businessAnalysis/categoryStatistics";
    }
    return "/";
  }

  /**
   * 根据条件查询order
   *
   * @param request
   * @param orderSearchConditionDTO
   * @return
   */
  @ResponseBody
  @RequestMapping(params = "method=getItemStatData")
  public Object getItemStatData(HttpServletRequest request, OrderSearchConditionDTO orderSearchConditionDTO, Integer startPageNo, Integer maxRows) {
    OrderSearchResultListDTO orderSearchResultListDTO = null;
    try {
      ISearchOrderService searchOrderService = ServiceManager.getService(ISearchOrderService.class);
      //数据校验
      Long shopId = (Long) request.getSession().getAttribute("shopId");
      if (orderSearchConditionDTO == null) throw new Exception("OrderSearchConditionDTO can't be null.");
      if (StringUtils.isBlank(orderSearchConditionDTO.getStatType())) throw new Exception("OrderSearchConditionDTO StatType can't be null.");
      if (shopId == null) throw new Exception("shopId can't be null.");
      startPageNo = (startPageNo == null) ? DEFAULT_START_PAGE : startPageNo;
      maxRows = (maxRows == null) ? DEFAULT_MAX_ROWS : maxRows;
      //校验时间
      orderSearchConditionDTO.setShopId(shopId);
      orderSearchConditionDTO.setRowStart((startPageNo - 1) * maxRows);
      orderSearchConditionDTO.setPageRows(maxRows);
      orderSearchConditionDTO.validateBeforeQuery();
      orderSearchConditionDTO.verificationQueryTime();

      List<Object> result = new ArrayList<Object>();
      Pager pager = null;
      if ("businessStatistics".equals(orderSearchConditionDTO.getStatType())) {
        generateCustomerConditions(orderSearchConditionDTO, shopId);
        orderSearchConditionDTO.setSearchStrategy(new String[]{OrderSearchConditionDTO.SEARCHSTRATEGY_STATS, OrderSearchConditionDTO.SEARCHSTRATEGY_CURRENT_PAGE_STATS});
        orderSearchConditionDTO.setOrderType(new String[]{"WASH_BEAUTY", "SALE", "REPAIR"});
        orderSearchConditionDTO.setOrderStatus(new String[]{"WASH_SETTLED", "SALE_DONE", "SALE_DEBT_DONE", "REPAIR_SETTLED"});
        orderSearchConditionDTO.setStatsFields(new String[]{"item_total", "item_total_cost_price", "item_count"});
        orderSearchConditionDTO.setPageStatsFields(new String[]{"item_total", "item_total_cost_price", "item_count"});
        if (StringUtils.isNotBlank(orderSearchConditionDTO.getBusinessCategory())) {
          RFITxnService txnService = ServiceManager.getService(RFITxnService.class);
          CategoryDTO categoryDTO = txnService.getCategoryDTOByName(shopId, orderSearchConditionDTO.getBusinessCategory(), CategoryType.BUSINESS_CLASSIFICATION);
          orderSearchConditionDTO.setBusinessCategoryId(categoryDTO == null ? "-1" : categoryDTO.getId().toString());
        }
        orderSearchConditionDTO.setSort("order_created_time desc,order_receipt_no desc");
         Long shopAreaId = orderSearchConditionDTO.getRegionNo()!=null?orderSearchConditionDTO.getRegionNo():(orderSearchConditionDTO.getCityNo()!=null?orderSearchConditionDTO.getCityNo():orderSearchConditionDTO.getProvinceNo());
        if(shopAreaId!=null){
          JoinSearchConditionDTO joinSearchConditionDTO = new JoinSearchConditionDTO();
          joinSearchConditionDTO.setShopId(shopId);
          joinSearchConditionDTO.setFromColumn("id");
          joinSearchConditionDTO.setToColumn("customer_or_supplier_id");
          joinSearchConditionDTO.setFromIndex(SolrClientHelper.BcgogoSolrCore.CUSTOMER_SUPPLIER_CORE.getValue());
          joinSearchConditionDTO.setCustomerOrSupplier(new String[]{"customer"});
          joinSearchConditionDTO.setAreaId(shopAreaId);
          orderSearchConditionDTO.setJoinSearchConditionDTO(joinSearchConditionDTO);
        }
        orderSearchResultListDTO = searchOrderService.queryOrderItems(orderSearchConditionDTO);
        //添加客户状态
        signCustomerStatusInOrderItem(orderSearchResultListDTO, shopId);
        pager = new Pager(Integer.valueOf(String.valueOf(orderSearchResultListDTO.getItemNumFound())), startPageNo, maxRows);

      } else if ("productStatistics".equals(orderSearchConditionDTO.getStatType())) {
        generateCustomerConditions(orderSearchConditionDTO, shopId);
        orderSearchConditionDTO.setSearchStrategy(new String[]{OrderSearchConditionDTO.SEARCHSTRATEGY_STATS, OrderSearchConditionDTO.SEARCHSTRATEGY_CURRENT_PAGE_STATS});
        orderSearchConditionDTO.setOrderType(new String[]{"SALE", "REPAIR"});    // 单据类型
        orderSearchConditionDTO.setOrderStatus(new String[]{"SALE_DONE", "SALE_DEBT_DONE", "REPAIR_SETTLED"});
        orderSearchConditionDTO.setStatsFields(new String[]{"item_total", "item_total_cost_price", "item_count"});
        orderSearchConditionDTO.setPageStatsFields(new String[]{"item_total", "item_total_cost_price", "item_count","item_price"});
        orderSearchConditionDTO.setItemTypes(new String[]{ItemTypes.MATERIAL.toString()});
        if (StringUtils.isNotBlank(orderSearchConditionDTO.getProductKind())) {
          IProductService productService = ServiceManager.getService(IProductService.class);
          Long kindId = productService.getKindIdByName(shopId, orderSearchConditionDTO.getProductKind());
          List<String> productIdSet = new ArrayList<String>();
          productIdSet.add("-1");
          if (kindId != null) {
            List<ProductDTO> productDTOList = productService.getProductDTOsByProductKindId(shopId, kindId);
            if (CollectionUtils.isNotEmpty(productDTOList)) {
              productIdSet.clear();
              for (ProductDTO productDTO : productDTOList) {
                productIdSet.add(productDTO.getProductLocalInfoIdStr());
              }
            }
          }
          orderSearchConditionDTO.setProductIds(productIdSet.toArray(new String[productIdSet.size()]));
        }
        orderSearchConditionDTO.setSort("order_created_time desc,order_receipt_no desc");

        Long shopAreaId = orderSearchConditionDTO.getRegionNo()!=null?orderSearchConditionDTO.getRegionNo():(orderSearchConditionDTO.getCityNo()!=null?orderSearchConditionDTO.getCityNo():orderSearchConditionDTO.getProvinceNo());
        if(shopAreaId!=null){
          JoinSearchConditionDTO joinSearchConditionDTO = new JoinSearchConditionDTO();
          joinSearchConditionDTO.setShopId(shopId);
          joinSearchConditionDTO.setFromColumn("id");
          joinSearchConditionDTO.setToColumn("customer_or_supplier_id");
          joinSearchConditionDTO.setFromIndex(SolrClientHelper.BcgogoSolrCore.CUSTOMER_SUPPLIER_CORE.getValue());
          joinSearchConditionDTO.setCustomerOrSupplier(new String[]{"customer"});
          joinSearchConditionDTO.setAreaId(shopAreaId);
          orderSearchConditionDTO.setJoinSearchConditionDTO(joinSearchConditionDTO);
        }

        orderSearchResultListDTO = searchOrderService.queryOrderItemsByExactCondition(orderSearchConditionDTO);
        //添加客户状态
        signCustomerStatusInOrderItem(orderSearchResultListDTO, shopId);
        pager = new Pager(Integer.valueOf(String.valueOf(orderSearchResultListDTO.getItemNumFound())), startPageNo, maxRows);
      } else if ("customerStatistics".equals(orderSearchConditionDTO.getStatType())) {
        generateCustomerConditions(orderSearchConditionDTO, shopId);
        orderSearchConditionDTO.setSearchStrategy(new String[]{OrderSearchConditionDTO.SEARCHSTRATEGY_STATS, OrderSearchConditionDTO.SEARCHSTRATEGY_CURRENT_PAGE_STATS});
        orderSearchConditionDTO.setOrderType(new String[]{"WASH_BEAUTY", "SALE", "REPAIR"});    // 单据类型
        orderSearchConditionDTO.setOrderStatus(new String[]{"WASH_SETTLED", "SALE_DONE", "SALE_DEBT_DONE", "REPAIR_SETTLED"});
        orderSearchConditionDTO.setStatsFields(new String[]{"order_total_amount", "order_settled_amount", "order_debt_amount", "discount","total_cost_price","gross_profit"});
        orderSearchConditionDTO.setPageStatsFields(new String[]{"order_total_amount", "order_settled_amount", "order_debt_amount", "total_cost_price", "discount", "gross_profit"});

        orderSearchConditionDTO.setSort("created_time desc");

        orderSearchResultListDTO = searchOrderService.queryOrders(orderSearchConditionDTO);
        signCustomerStatusInOrder(orderSearchResultListDTO, shopId);

        pager = new Pager(Integer.valueOf(String.valueOf(orderSearchResultListDTO.getNumFound())), startPageNo, maxRows);
      } else if ("supplierStatistics".equals(orderSearchConditionDTO.getStatType())) {
        generateSupplierConditions(orderSearchConditionDTO, shopId);
        orderSearchConditionDTO.setSearchStrategy(new String[]{OrderSearchConditionDTO.SEARCHSTRATEGY_STATS, OrderSearchConditionDTO.SEARCHSTRATEGY_CURRENT_PAGE_STATS});
        orderSearchConditionDTO.setOrderType(new String[]{"INVENTORY"});
        orderSearchConditionDTO.setOrderStatus(new String[]{"PURCHASE_INVENTORY_DONE", "SETTLED"});
        orderSearchConditionDTO.setStatsFields(new String[]{"order_total_amount", "order_settled_amount", "order_debt_amount", "discount"});
        orderSearchConditionDTO.setPageStatsFields(new String[]{"order_total_amount", "order_settled_amount", "order_debt_amount", "total_cost_price", "discount", "gross_profit"});

        orderSearchConditionDTO.setSort("created_time desc");

        orderSearchResultListDTO = searchOrderService.queryOrders(orderSearchConditionDTO);

        pager = new Pager(Integer.valueOf(String.valueOf(orderSearchResultListDTO.getNumFound())), startPageNo, maxRows);
      }else if("serviceAndConstructionStatistics".equals(orderSearchConditionDTO.getStatType())) {
        generateCustomerConditions(orderSearchConditionDTO, shopId);
        orderSearchConditionDTO.setSearchStrategy(new String[]{OrderSearchConditionDTO.SEARCHSTRATEGY_STATS, OrderSearchConditionDTO.SEARCHSTRATEGY_CURRENT_PAGE_STATS});
        orderSearchConditionDTO.setOrderType(new String[]{"REPAIR"});    // 单据类型
        orderSearchConditionDTO.setOrderStatus(new String[]{"REPAIR_SETTLED"});
        orderSearchConditionDTO.setStatsFields(new String[]{"item_total"});
        orderSearchConditionDTO.setPageStatsFields(new String[]{"item_total", "item_count", "item_price"});
        orderSearchConditionDTO.setItemTypes(new String[]{ItemTypes.SERVICE.toString()});
        String serviceAndConstruction = request.getParameter("serviceAndConstruction");
        if (StringUtils.isNotEmpty(serviceAndConstruction)) {
          List<ServiceDTO> serviceDTOList = ServiceManager.getService(ITxnService.class).getServiceByServiceNameAndShopId(shopId, serviceAndConstruction);
          if(serviceDTOList!=null && CollectionUtils.isNotEmpty(serviceDTOList)) {
            String[] serviceIds = new String[serviceDTOList.size()];
            int i = 0;
            for (ServiceDTO serviceDTO : serviceDTOList) {
              serviceIds[i++] = serviceDTO.getId().toString();
            }
            orderSearchConditionDTO.setServiceIds(serviceIds);
          }
        }
        orderSearchConditionDTO.setSort("order_created_time desc,order_receipt_no desc");
        orderSearchResultListDTO = searchOrderService.queryOrderItemsByExactCondition(orderSearchConditionDTO);
        //添加客户状态
        signCustomerStatusInOrderItem(orderSearchResultListDTO, shopId);
        pager = new Pager(Integer.valueOf(String.valueOf(orderSearchResultListDTO.getItemNumFound())), startPageNo, maxRows);
      }
      result.add(orderSearchResultListDTO);
      result.add(pager);
      return result;
    } catch (Exception e) {
      LOG.debug("/itemStat.do");
      LOG.debug("method=getItemStatData");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
    }
    return null;
  }

  private void generateSupplierConditions(OrderSearchConditionDTO orderSearchConditionDTO, Long shopId) {
    orderSearchConditionDTO.setSupplierId(null);
    ICustomerService customerService = ServiceManager.getService(ICustomerService.class);
    if (StringUtils.isNotBlank(orderSearchConditionDTO.getSupplierId())) {
      orderSearchConditionDTO.setCustomerOrSupplierIds(new String[]{orderSearchConditionDTO.getSupplierId()});
      orderSearchConditionDTO.setCustomerOrSupplierName(null);
    } else if (StringUtils.isNotBlank(orderSearchConditionDTO.getSupplierName()) || StringUtils.isNotBlank(orderSearchConditionDTO.getMobile())) {
      List<Supplier> supplierList = customerService.getAllSupplierByNameAndMobile(shopId, orderSearchConditionDTO.getSupplierName(), orderSearchConditionDTO.getMobile());
      Set<String> supplierIdSet = new HashSet<String>();
      if (CollectionUtils.isNotEmpty(supplierList)) {
        for (Supplier supplier : supplierList) {
          supplierIdSet.add(supplier.getId().toString());
        }
      } else {
        supplierIdSet.add("-1");
      }
      orderSearchConditionDTO.setCustomerOrSupplierIds(supplierIdSet.toArray(new String[supplierIdSet.size()]));
      orderSearchConditionDTO.setCustomerOrSupplierName(null);
    }
  }

  private void generateCustomerConditions(OrderSearchConditionDTO orderSearchConditionDTO, Long shopId) {
    orderSearchConditionDTO.setCustomerId(null);
    ICustomerService customerService = ServiceManager.getService(ICustomerService.class);
    if (StringUtils.isNotBlank(orderSearchConditionDTO.getCustomerId())) {
      orderSearchConditionDTO.setCustomerOrSupplierIds(new String[]{orderSearchConditionDTO.getCustomerId()});
      orderSearchConditionDTO.setCustomerOrSupplierName(null);
    } else if (StringUtils.isNotBlank(orderSearchConditionDTO.getCustomerName()) || StringUtils.isNotBlank(orderSearchConditionDTO.getMobile())) {
      List<Customer> customerDTOList = null;
      customerDTOList = customerService.getAllCustomerByNameAndMobile(shopId,orderSearchConditionDTO.getCustomerName(),orderSearchConditionDTO.getMobile());
      Set<String> customerIdSet = new HashSet<String>();
      if (CollectionUtils.isNotEmpty(customerDTOList)) {
        for (Customer customerDTO : customerDTOList) {
          customerIdSet.add(customerDTO.getId().toString());
        }
      } else {
        customerIdSet.add("-1");
      }
      orderSearchConditionDTO.setCustomerOrSupplierIds(customerIdSet.toArray(new String[customerIdSet.size()]));
      orderSearchConditionDTO.setCustomerOrSupplierName(null);
    }
  }

  private void signCustomerStatusInOrder(OrderSearchResultListDTO orderSearchResultListDTO, Long shopId) {
    //添加客户状态
    if (CollectionUtils.isNotEmpty(orderSearchResultListDTO.getOrders())) {
      Set<Long> customerIdSet = new HashSet<Long>();
      for (OrderSearchResultDTO order : orderSearchResultListDTO.getOrders()) {
        if ((!OrderTypes.SALE.toString().equals(order.getOrderType()) &&
            !OrderTypes.REPAIR.toString().equals(order.getOrderType()) &&
            !OrderTypes.WASH_BEAUTY.toString().equals(order.getOrderType()) &&
            !OrderTypes.MEMBER_BUY_CARD.toString().equals(order.getOrderType())) || order.getCustomerOrSupplierId() == null) {
          continue;
        }
        customerIdSet.add(order.getCustomerOrSupplierId());
      }
      Map<Long, CustomerDTO> customerDTOMap = ServiceManager.getService(ICustomerService.class).getCustomerByIdSet(shopId, customerIdSet);
      if (MapUtils.isNotEmpty(customerDTOMap)) {
        CustomerDTO customerDTO = null;
        for (OrderSearchResultDTO order : orderSearchResultListDTO.getOrders()) {
          if ((!OrderTypes.SALE.toString().equals(order.getOrderType()) &&
              !OrderTypes.REPAIR.toString().equals(order.getOrderType()) &&
              !OrderTypes.WASH_BEAUTY.toString().equals(order.getOrderType()) &&
              !OrderTypes.MEMBER_BUY_CARD.toString().equals(order.getOrderType())) || order.getCustomerOrSupplierId() == null) {
            continue;
          }
          customerDTO = customerDTOMap.get(order.getCustomerOrSupplierId());
          if (customerDTO != null) {
            order.setCustomerStatus(customerDTO.getStatus());
          }
        }
      }
    }
  }

  private void signCustomerStatusInOrderItem(OrderSearchResultListDTO orderSearchResultListDTO, Long shopId) {
    Set<Long> customerIdSet = new HashSet<Long>();
    if (CollectionUtils.isNotEmpty(orderSearchResultListDTO.getOrderItems())) {
      for (OrderItemSearchResultDTO orderItem : orderSearchResultListDTO.getOrderItems()) {
        if ((!OrderTypes.SALE.toString().equals(orderItem.getOrderType()) &&
            !OrderTypes.REPAIR.toString().equals(orderItem.getOrderType()) &&
            !OrderTypes.WASH_BEAUTY.toString().equals(orderItem.getOrderType()) &&
            !OrderTypes.MEMBER_BUY_CARD.toString().equals(orderItem.getOrderType())) || orderItem.getCustomerOrSupplierId() == null) {
          continue;
        }
        customerIdSet.add(orderItem.getCustomerOrSupplierId());
      }
      Map<Long, CustomerDTO> customerDTOMap = ServiceManager.getService(ICustomerService.class).getCustomerByIdSet(shopId, customerIdSet);
      if (MapUtils.isNotEmpty(customerDTOMap)) {
        CustomerDTO customerDTO = null;
        for (OrderItemSearchResultDTO orderItem : orderSearchResultListDTO.getOrderItems()) {
          if ((!OrderTypes.SALE.toString().equals(orderItem.getOrderType()) &&
              !OrderTypes.REPAIR.toString().equals(orderItem.getOrderType()) &&
              !OrderTypes.WASH_BEAUTY.toString().equals(orderItem.getOrderType()) &&
              !OrderTypes.MEMBER_BUY_CARD.toString().equals(orderItem.getOrderType())) || orderItem.getCustomerOrSupplierId() == null) {
            continue;
          }
          customerDTO = customerDTOMap.get(orderItem.getCustomerOrSupplierId());
          if (customerDTO != null) {
            orderItem.setCustomerStatus(customerDTO.getStatus());
          }
        }
      }
    }
  }


  /**
   * 获取柱状图数据
   *
   * @param request
   * @return
   */
  @RequestMapping(params = "method=getChartData")
  @ResponseBody
  public Object getChartData(HttpServletRequest request, String chartType, String statType) {
    try {
      Map<String, Object> result = new HashMap<String, Object>();
      Long shopId = (Long) request.getSession().getAttribute("shopId");
      if (shopId == null) throw new Exception("shopId can't be null.");
      ITxnService txnService = ServiceManager.getService(ITxnService.class);
      Map<Integer, Double> sortedMap = new TreeMap<Integer, Double>();

      if ("day".equals(chartType)) {
        Calendar cal = Calendar.getInstance();
        Long endTime = cal.getTimeInMillis();
        cal.add(Calendar.MONTH, -1);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        String yearMonthDayStart = DateUtil.convertDateLongToDateString("yyyy-MM-dd", cal.getTimeInMillis());//多拿一天
        String prevkey = DateUtil.convertDateLongToDateString("yyyyMMdd", cal.getTimeInMillis());
        String yearMonthDayEnd = DateUtil.convertDateLongToDateString("yyyy-MM-dd", endTime);

        cal.set(Calendar.MINUTE, 1);
        Long startTime = cal.getTimeInMillis();
        sortedMap.put(Integer.parseInt(DateUtil.convertDateLongToDateString("yyyyMMdd", cal.getTimeInMillis())), 0d);
        while (Integer.parseInt(DateUtil.convertDateLongToDateString("yyyyMMdd", cal.getTimeInMillis())) < Integer.parseInt(DateUtil.convertDateLongToDateString("yyyyMMdd", endTime))) {
          cal.add(Calendar.DAY_OF_MONTH, 1);
          sortedMap.put(Integer.parseInt(DateUtil.convertDateLongToDateString("yyyyMMdd", cal.getTimeInMillis())), 0d);
        }
        result.put("categoryName", DateUtil.convertDateLongToDateString("yyyy年MM月dd日", startTime) + "-" + DateUtil.convertDateLongToDateString("yyyy年MM月dd日", endTime));
//        if ("businessStatistics".equals(statType)) {
//        }else if ("productStatistics".equals(statType)){
//
//        }else if ("supplierStatistics".equals(statType)){
//
//        } else {
        Map<String, BusinessStatDTO> statDTOMap = txnService.getBusinessStatMapByYearMonthDay(shopId, yearMonthDayStart, yearMonthDayEnd);
        tewtwet(chartType, result, sortedMap, prevkey, statDTOMap);
//        }
      } else {
        Long endTime = DateUtil.getLastDayDateTimeOfMonth();
        Calendar cal = Calendar.getInstance();//多拿一个月
        cal.add(Calendar.YEAR, -1);
        List<String> yearMonthList = new ArrayList<String>();
        cal.set(Calendar.DATE, 1);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        yearMonthList.add(DateUtil.convertDateLongToDateString("yyyy-MM", cal.getTimeInMillis()));
        String prevkey = DateUtil.convertDateLongToDateString("yyyyMM", cal.getTimeInMillis());
        cal.add(Calendar.MONTH, 1);
        yearMonthList.add(DateUtil.convertDateLongToDateString("yyyy-MM", cal.getTimeInMillis()));
        Long startTime = cal.getTimeInMillis();
        sortedMap.put(Integer.parseInt(DateUtil.convertDateLongToDateString("yyyyMM", cal.getTimeInMillis())), 0d);
        while (Integer.parseInt(DateUtil.convertDateLongToDateString("yyyyMM", cal.getTimeInMillis())) < Integer.parseInt(DateUtil.convertDateLongToDateString("yyyyMM", endTime))) {
          cal.add(Calendar.MONTH, 1);
          sortedMap.put(Integer.parseInt(DateUtil.convertDateLongToDateString("yyyyMM", cal.getTimeInMillis())), 0d);
          yearMonthList.add(DateUtil.convertDateLongToDateString("yyyy-MM", cal.getTimeInMillis()));
        }
        result.put("categoryName", DateUtil.convertDateLongToDateString("yyyy年MM月", startTime) + "-" + DateUtil.convertDateLongToDateString("yyyy年MM月", endTime));

//        if ("businessStatistics".equals(statType)) {
//        }else if ("productStatistics".equals(statType)){
//
//        }else if ("supplierStatistics".equals(statType)){
//
//        } else {
        Map<String, BusinessStatDTO> statDTOMap = txnService.getBusinessStatMapByYearMonth(shopId, yearMonthList.toArray(new String[yearMonthList.size()]));
        tewtwet(chartType, result, sortedMap, prevkey, statDTOMap);
//        }
      }

      return result;
    } catch (Exception e) {
      LOG.debug("/itemStat.do");
      LOG.debug("method=getChartData");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
    }
    return null;
  }

  private void tewtwet(String chartType, Map<String, Object> result, Map<Integer, Double> sortedMap, String prevkey, Map<String, BusinessStatDTO> statDTOMap) throws ParseException {
    BusinessStatDTO prevBusinessStatDTO = new BusinessStatDTO();
    if (statDTOMap.get(prevkey) != null) {
      prevBusinessStatDTO = statDTOMap.get(prevkey);
    }

    List<Map<String, Double>> data = new ArrayList<Map<String, Double>>();
    List<String> categories = new ArrayList<String>();
    Map<String, String> categoriesMap = new HashMap<String, String>();
    Map<String, Double> map = null;
    String category = null;
    BusinessStatDTO statDTO = null;
    for (Integer key : sortedMap.keySet()) {
      map = new HashMap<String, Double>();
      statDTO = statDTOMap.get(key.toString());
      if (statDTO != null) {
        map.put("y", NumberUtil.round(statDTO.getStatSum() - prevBusinessStatDTO.getStatSum(), 1));
        prevBusinessStatDTO = statDTO;
      } else {
        map.put("y", 0d);
      }

      data.add(map);
      category = Integer.valueOf(key.toString().substring(key.toString().length() - 2)).toString();
      categories.add(category);
      if ("day".equals(chartType)) {
        categoriesMap.put(category, DateUtil.convertDateLongToDateString("yyyy年MM月dd日", DateUtil.convertDateStringToDateLong("yyyyMMdd", key.toString())));
      } else {
        categoriesMap.put(category, DateUtil.convertDateLongToDateString("yyyy年MM月", DateUtil.convertDateStringToDateLong("yyyyMM", key.toString())));
      }
    }
    result.put("categoriesMap", categoriesMap);
    result.put("categories", categories);
    result.put("data", data);
  }

  @RequestMapping(params = "method=getItemStatDataToPrint")
  public void getItemStatDataToPrint(HttpServletRequest request, HttpServletResponse response, OrderSearchConditionDTO orderSearchConditionDTO, int currentPage, int maxRows) throws Exception {
    Object object = getItemStatData(request, orderSearchConditionDTO, currentPage, maxRows);
    if (null == object) {
      return;
    }
    List<Object> result = (List<Object>) object;

    OrderTypes orderType = null;

    if ("customerStatistics".equals(orderSearchConditionDTO.getStatType())) {
      orderType = OrderTypes.CUSTOMER_BUSINESS_STATISTICS;
    } else if ("supplierStatistics".equals(orderSearchConditionDTO.getStatType())) {
      orderType = OrderTypes.SUPPLIER_BUSINESS_STATISTICS;
    } else if ("productStatistics".equals(orderSearchConditionDTO.getStatType())) {
      orderType = OrderTypes.PRODUCT_CATEGORY_SALES_STATISTICS;
    } else if ("businessStatistics".equals(orderSearchConditionDTO.getStatType())) {
      orderType = OrderTypes.BUSINESS_CATEGORY_SALES_STATISTICS;
    }else if("serviceAndConstructionStatistics".equals(orderSearchConditionDTO.getStatType())){
      orderType = OrderTypes.SERVICE_SALES_STATISTICS;
    }

    if (null == orderType || CollectionUtils.isEmpty(result)) {
      return;
    }

    if (result.size() == 1) {
      return;
    }

    toPrint(request, response, result, orderType, orderSearchConditionDTO);
  }

  public void toPrint(HttpServletRequest request, HttpServletResponse response, List<Object> result, OrderTypes orderType, OrderSearchConditionDTO orderSearchConditionDTO) throws Exception {
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    IPrintService printService = ServiceManager.getService(IPrintService.class);
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    PrintWriter out = response.getWriter();
    response.setContentType("text/html");
    response.setCharacterEncoding("UTF-8");

    OrderSearchResultListDTO orderSearchResultListDTO = (OrderSearchResultListDTO) result.get(0);
    Pager pager = (Pager) result.get(1);

    try {
      ShopDTO shopDTO = configService.getShopById(WebUtil.getShopId(request));
      PrintTemplateDTO printTemplateDTO = printService.getSinglePrintTemplateDTOByShopIdAndType(WebUtil.getShopId(request), orderType);

      if (null != printTemplateDTO) {
        byte bytes[] = printTemplateDTO.getTemplateHtml();
        String str = new String(bytes, "UTF-8");

        //获取VelocityEngine
        VelocityEngine ve = createVelocityEngine();
        //创建资源库
        StringResourceRepository repo = StringResourceLoader.getRepository();

        String myTemplateName = "breakdown" + orderType.toString() + String.valueOf(WebUtil.getShopId(request));

        String myTemplate = str;

        //模板资源存放 资源库 中

        repo.putStringResource(myTemplateName, myTemplate);

        //从资源库中加载模板

        Template template = ve.getTemplate(myTemplateName);

        //取得velocity的模版
        Template t = ve.getTemplate(myTemplateName, "UTF-8");
        //取得velocity的上下文context
        VelocityContext context = new VelocityContext();

        //把数据填入上下文
        context.put("pager", pager);


        context.put("orders", orderSearchResultListDTO.getOrders());
        context.put("orderItems", orderSearchResultListDTO.getOrderItems());
        context.put("orderSearchConditionDTO", orderSearchConditionDTO);
        context.put("currentPageTotalAmounts", orderSearchResultListDTO.getCurrentPageTotalAmounts());
        context.put("totalAmounts", orderSearchResultListDTO.getTotalAmounts());

        if ("supplierStatistics".equals(orderSearchConditionDTO.getStatType()))
        {
          Double currentTotal = orderSearchResultListDTO.getCurrentPageTotalAmounts().get("order_total_amount");
          Double settledTotal = orderSearchResultListDTO.getCurrentPageTotalAmounts().get("order_settled_amount");
          Double debtTotal = orderSearchResultListDTO.getCurrentPageTotalAmounts().get("order_debt_amount");
          Double discountTotal = orderSearchResultListDTO.getCurrentPageTotalAmounts().get("discount");

          Double total = orderSearchResultListDTO.getTotalAmounts().get("ORDER_SETTLED_AMOUNT")+orderSearchResultListDTO.getTotalAmounts().get("ORDER_DEBT_AMOUNT");
          Double totalSettledAmount = orderSearchResultListDTO.getTotalAmounts().get("ORDER_SETTLED_AMOUNT");
          Double totalDebt = orderSearchResultListDTO.getTotalAmounts().get("ORDER_DEBT_AMOUNT");
          Double totalDiscount = orderSearchResultListDTO.getTotalAmounts().get("DISCOUNT");
          context.put("total", NumberUtil.round(total,NumberUtil.MONEY_PRECISION));
          context.put("totalSettledAmount", NumberUtil.round(totalSettledAmount,NumberUtil.MONEY_PRECISION));
          context.put("totalDebt", NumberUtil.round(totalDebt,NumberUtil.MONEY_PRECISION));
          context.put("totalDiscount", NumberUtil.round(totalDiscount,NumberUtil.MONEY_PRECISION));
          context.put("currentTotal", NumberUtil.round(currentTotal,NumberUtil.MONEY_PRECISION));
          context.put("settledTotal", NumberUtil.round(settledTotal,NumberUtil.MONEY_PRECISION));
          context.put("debtTotal", NumberUtil.round(debtTotal,NumberUtil.MONEY_PRECISION));
          context.put("discountTotal", NumberUtil.round(discountTotal,NumberUtil.MONEY_PRECISION));
        }else if("serviceAndConstructionStatistics".equals(orderSearchConditionDTO.getStatType())){
          List<OrderItemSearchResultDTO> list = orderSearchResultListDTO.getOrderItems();
          Double itemCountTotal = 0.0;
          for (OrderItemSearchResultDTO dto : list) {
            if (dto.getItemCount() != null) {
              itemCountTotal += dto.getItemCount();
            }
          }
          context.put("itemCountTotal", itemCountTotal);
        }

        //输出流
        StringWriter writer = new StringWriter();

        //转换输出
        t.merge(context, writer);
        out.print(writer);
        writer.close();
      } else {
        out.print(TxnConstant.NO_PRINT_TEMPLATE);
      }
    } catch (Exception e) {
      LOG.debug("toPrint");
      LOG.debug(e.getMessage(), e);
    } finally {
      out.flush();
      out.close();
    }
  }

  private VelocityEngine createVelocityEngine() throws Exception {
    //初始化并取得Velocity引擎
    VelocityEngine ve = new VelocityEngine();
    ve.setProperty(VelocityEngine.RESOURCE_LOADER, "string");
    ve.setProperty("string.resource.loader.class", "org.apache.velocity.runtime.resource.loader.StringResourceLoader");
    ve.setProperty("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.SimpleLog4JLogSystem");
    ve.setProperty("runtime.log.logsystem.log4j.category", "velocity");
    ve.setProperty("runtime.log.logsystem.log4j.logger", "velocity");
    ve.init();
    return ve;
  }
}
