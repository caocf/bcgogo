package com.bcgogo.stat;

import com.bcgogo.common.WebUtil;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.product.dto.ProductLocalInfoDTO;
import com.bcgogo.product.service.IProductService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.stat.dto.CostStatConditionDTO;
import com.bcgogo.stat.dto.PurchaseInventoryStatDTO;
import com.bcgogo.stat.dto.SalesStatCondition;
import com.bcgogo.stat.dto.SupplierTranStatDTO;
import com.bcgogo.txn.dto.PriceFluctuationStatDTO;
import com.bcgogo.txn.dto.PurchaseReturnMonthStatDTO;
import com.bcgogo.txn.service.IPurchaseCostStatService;
import com.bcgogo.utils.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * 成本统计controller
 * User: Jimuchen
 * Date: 12-10-27
 * Time: 上午2:03
 * To change this template use File | Settings | File Templates.
 */

@Controller
@RequestMapping("/costStat.do")
public class CostStatController {
  private static final Logger LOG = LoggerFactory.getLogger(CostStatController.class);

  private static final int TOP_LIMIT = 10;

  @RequestMapping(params = "method=getCostStat")
  public String getCostStat(Model model, HttpServletRequest request, CostStatConditionDTO conditionDTO){
    Long shopId = WebUtil.getShopId(request);
    if(shopId==null){
      return "/";
    }
    int year = DateUtil.getCurrentYear();
    int month = DateUtil.getCurrentMonth();
    boolean allYear = false;
    if(conditionDTO == null){
      conditionDTO = new CostStatConditionDTO();
    }
    if(conditionDTO.getAllYear() == null){
      conditionDTO.setAllYear(false);
    }else{
      allYear = conditionDTO.getAllYear();
    }
    String[] queryFields = {CostStatConditionDTO.FIELD_PRODUCT_NAME, CostStatConditionDTO.FIELD_BRAND};
    if(conditionDTO.getYear() != null){
      year = conditionDTO.getYear();
    }else{
      conditionDTO.setYear(year);
    }
    if(conditionDTO.getMonth() != null){
      month = conditionDTO.getMonth();
    }else{
      conditionDTO.setMonth(month);
    }
    if(conditionDTO.getQueryFields() != null){
      queryFields = conditionDTO.getQueryFields();
    }
    if(!ArrayUtils.contains(queryFields, CostStatConditionDTO.FIELD_PRODUCT_NAME)){
      ArrayUtils.add(queryFields, CostStatConditionDTO.FIELD_PRODUCT_NAME);
    }
    conditionDTO.setQueryFields(queryFields);
    try{
      //根据查询条件查询商品组合
      IPurchaseCostStatService purchaseCostStatService = ServiceManager.getService(IPurchaseCostStatService.class);
      List<PurchaseInventoryStatDTO> result = purchaseCostStatService.queryTopPurchaseInventoryMonthStat(shopId, year, month, allYear, queryFields, TOP_LIMIT);
      double total = purchaseCostStatService.queryPurchaseInventoryTotal(shopId, year, month, allYear);

      PurchaseInventoryStatDTO other = new PurchaseInventoryStatDTO();
      double topTotal = 0;
      for(PurchaseInventoryStatDTO statDTO : result){
        topTotal += statDTO.getTotal();
      }
      other.setTotal(total-topTotal);
      other.setProductName("其他");

      model.addAttribute("purchaseInventoryStatDTOs", result);
      model.addAttribute("other", other);
      model.addAttribute("total", String.format("%.1f", total));
      return "/stat/costStatistics";
    } catch (Exception e) {
      LOG.error("costStatController getCostStat shopId:" + shopId);
      LOG.error(e.getMessage(),e);
    }
    return "/stat/costStatistics";
  }

  @RequestMapping(params = "method=getSupplierStat")
  public String getSupplierStat(Model model, HttpServletRequest request, CostStatConditionDTO costStatConditionDTO){
    IPurchaseCostStatService purchaseCostStatService = ServiceManager.getService(IPurchaseCostStatService.class);
    Long shopId = WebUtil.getShopId(request);
    if(shopId == null){
      return "/";
    }
    int year = DateUtil.getCurrentYear();
    int month = DateUtil.getCurrentMonth();
    boolean allYear = false;
    if(costStatConditionDTO == null){
      costStatConditionDTO = new CostStatConditionDTO();
    }
    if(costStatConditionDTO.getAllYear() == null){
      costStatConditionDTO.setAllYear(false);
    }else{
      allYear = costStatConditionDTO.getAllYear();
    }
    if(costStatConditionDTO.getYear() != null){
      year = costStatConditionDTO.getYear();
    }else{
      costStatConditionDTO.setYear(year);
    }
    if(costStatConditionDTO.getMonth() != null){
      month = costStatConditionDTO.getMonth();
    }else{
      costStatConditionDTO.setMonth(month);
    }
    try{
      List<SupplierTranStatDTO> supplierTranStatDTOs = purchaseCostStatService.queryTopSupplierTranMonthStat(shopId, year, month, allYear, TOP_LIMIT);
      double total = purchaseCostStatService.querySupplierTranTotal(shopId, year, month, allYear);
      SupplierTranStatDTO other = new SupplierTranStatDTO();
      double topTotal = 0;
      for(SupplierTranStatDTO statDTO : supplierTranStatDTOs){
        topTotal += statDTO.getTotal();
      }
      other.setTotal(total-topTotal);
      other.setSupplierName("其他");

      model.addAttribute("supplierTranStatDTOs", supplierTranStatDTOs);
      model.addAttribute("total", String.format("%.1f", total));
      model.addAttribute("other", other);

      return "/stat/supplierStatistics";
    }catch(Exception e){
      LOG.error("costStatController.getSupplierStat shopId:" + shopId);
      LOG.error(e.getMessage(),e);
    }
    return "/stat/supplierStatistics";
  }

  @RequestMapping(params = "method=getPriceStat")
  public String getPriceStat(Model model, HttpServletRequest request){
    IPurchaseCostStatService purchaseCostStatService = ServiceManager.getService(IPurchaseCostStatService.class);
    IProductService productService = ServiceManager.getService(IProductService.class);
    Long shopId = WebUtil.getShopId(request);
    int year = DateUtil.getCurrentYear();
    int month = DateUtil.getCurrentMonth();
    String dateRange = (year-1)+"年"+month+"月 - "+year+"年"+month+"月";

    //价格波动TOP10结果集
    List<PriceFluctuationStatDTO> result = new ArrayList<PriceFluctuationStatDTO>();
    //商品属性结果集
    List<Map<String,String>> otherList = new ArrayList<Map<String, String>>();
    //默认排第一的商品显示折线图
    List<List<Object>> resultList = new ArrayList<List<Object>>();

    try{
      //先从PriceFluctuationStat表查出最近12个月的数据（数据不全，需要通过product_local_info的ID来补全信息）
      result = purchaseCostStatService.queryTopPurchaseInventoryLastTwelveMonthStat(shopId,TOP_LIMIT);
      //通过返回结果获得product_local_info的id，间接获取product表的ID，补全商品信息
      for(int i=0;i<result.size();i++){
        ProductLocalInfoDTO productLocalInfoDTO = productService.getProductLocalInfoById(result.get(i).getProductId(),shopId);
        if(productLocalInfoDTO==null){
          productLocalInfoDTO = new ProductLocalInfoDTO();
        }
        Long productId = productLocalInfoDTO.getProductId();
        ProductDTO productDTO;
        if(productId==null){
          productDTO = new ProductDTO();
        }else{
          productDTO = productService.getProductById(productId,shopId);
        }
        //品牌产地、规格型号、车辆品牌车型、商品编号
        Map<String,String> map = new HashMap<String, String>();
        map.put("commodityCode",StringUtil.isNotEmpty(productDTO.getCommodityCode()) ? productDTO.getCommodityCode() : "--");
        map.put("name",StringUtil.isNotEmpty(productDTO.getName()) ? productDTO.getName() : "--");
        map.put("brand",StringUtil.isNotEmpty(productDTO.getBrand()) ? productDTO.getBrand() : "--");
        map.put("model",StringUtil.isNotEmpty(productDTO.getModel()) ? productDTO.getModel() : "--");
        map.put("spec",StringUtil.isNotEmpty(productDTO.getSpec()) ? productDTO.getSpec() : "--");
        map.put("vehicleBrand",StringUtil.isNotEmpty(productDTO.getVehicleBrand()) ? productDTO.getVehicleBrand() : "--");
        map.put("vehicleModel",StringUtil.isNotEmpty(productDTO.getVehicleModel()) ? productDTO.getVehicleModel() : "--");
        otherList.add(map);
      }
    }catch (Exception e){
      LOG.error(e.getMessage(),e);
    }


    //获取去年的今天的零点时刻毫秒数，例如今天是2012-11-07，时间范围则是2011-11-07 00:00:00 - 2012-11-07 00:00:00，共366个时间点
    Calendar c = Calendar.getInstance();
    c.set(Calendar.HOUR_OF_DAY, 0);
    c.set(Calendar.MINUTE, 0);
    c.set(Calendar.SECOND, 0);
    c.set(Calendar.MILLISECOND,0);
    c.add(Calendar.HOUR,6);
    Long endTime = c.getTimeInMillis();
    c.add(Calendar.YEAR, -1);
    Long startTime = c.getTimeInMillis();

    List<List<Object>> dataList = new ArrayList<List<Object>>();
    if(result.size()>0){
      dataList = purchaseCostStatService.getPriceFluctuationLineChartData(shopId,result.get(0).getProductId(),startTime,endTime);
    }

    //补足整年数据，无数据的那天补零
    long days = (endTime-startTime)/(24*3600*1000);
    for(int i=0;i<days;i++){
      List<Object> resultItemList = new ArrayList<Object>();
      Long itemTime = startTime + 86400000l * i;
      Double itemValue = 0d;
      for (int j=0;j<dataList.size();j++){
        if(itemTime == Long.parseLong((String)dataList.get(j).get(0))){
          itemValue = (Double)dataList.get(j).get(1);
          break;
        }
      }
      resultItemList.add(itemTime+86400000l);
      resultItemList.add(itemValue);
      resultList.add(resultItemList);
    }
    model.addAttribute("priceFluctuationStatDTOList", result);
    model.addAttribute("productInfoList", otherList);
    model.addAttribute("dateRange",dateRange);
    model.addAttribute("chartData",JsonUtil.objectToJson(resultList));

    return "/stat/priceStatistics";
  }

  /**
   * 采购分析->退货统计
   * @param model
   * @param request
   * @param salesStatCondition
   * @return
   */
  @RequestMapping(params = "method=getReturnStat")
  public String getReturnStat(Model model, HttpServletRequest request,SalesStatCondition salesStatCondition) {
    Long shopId = null;
    try {
      IPurchaseCostStatService purchaseCostStatService = ServiceManager.getService(IPurchaseCostStatService.class);

      shopId = WebUtil.getShopId(request);
      if (shopId == null) {
        return "/";
      }
      int year = DateUtil.getCurrentYear();
      int month = DateUtil.getCurrentMonth();
      if (salesStatCondition == null) {
        salesStatCondition = new SalesStatCondition();
      }
      if (salesStatCondition.getAllYear() == null || !salesStatCondition.getAllYear().booleanValue()) {
        salesStatCondition.setAllYear(false);
      } else {
        salesStatCondition.setAllYear(true);
      }
      if (salesStatCondition.getYear() != null) {
        year = salesStatCondition.getYear();
      } else {
        salesStatCondition.setYear(year);
      }
      if (salesStatCondition.getMonth() != null) {
        month = salesStatCondition.getMonth();
      } else {
        salesStatCondition.setMonth(month);
      }
      if (StringUtil.isEmpty(salesStatCondition.getProductOrSupplier())) {
        salesStatCondition.setProductOrSupplier(StatConstant.QUERY_BY_PRODUCT);
      }


      double total = 0;  //退货总金额
      double totalAmount = 0; //退货总数量
      int totalProduct = 0;//退货商品的种类
      int totalSupplier = 0;//退货供应商的种类
      int totalSize = 0;//返回前台的查询个数
      List<String> stringList = purchaseCostStatService.countTotalReturnByCondition(shopId, salesStatCondition);
      if (CollectionUtils.isNotEmpty(stringList) && stringList.size() == StatConstant.RETURN_SIZE) {
        totalAmount = NumberUtil.toReserve(Double.valueOf(stringList.get(0)), 1);
        total = NumberUtil.toReserve(Double.valueOf(stringList.get(1)), NumberUtil.MONEY_PRECISION);
        totalProduct = Integer.valueOf(stringList.get(3));
        totalSupplier = Integer.valueOf(stringList.get(2));
      }


      if (StatConstant.QUERY_BY_PRODUCT.equals(salesStatCondition.getProductOrSupplier())) {
        totalSize = totalProduct;
      } else {
        totalSize = totalSupplier;
      }


      model.addAttribute("total", total);
      model.addAttribute("result", total);
      model.addAttribute("totalAmount", totalAmount);
      if (totalAmount < 0) {
        return "/stat/returnStatistics";
      }

      List<PurchaseReturnMonthStatDTO> purchaseReturnMonthStatDTOList = purchaseCostStatService.queryPurchaseReturnByCondition(shopId, salesStatCondition);
      if (CollectionUtils.isEmpty(purchaseReturnMonthStatDTOList)) {
        return "/stat/returnStatistics";
      }
      purchaseCostStatService.getReturnInfo(model, purchaseReturnMonthStatDTOList, salesStatCondition, total, totalAmount,totalSize);
      model.addAttribute("salesStatCondition",salesStatCondition);
    } catch (Exception e) {
      LOG.error("costStatController getReturnStat shopId:" + shopId);
      LOG.error(e.getMessage(),e);
    }
    return "/stat/returnStatistics";
  }

//  //获取价格波动折线图的数据
//  @RequestMapping(params="method=getPriceFluctuationLineChartData")
//  public void getPriceFluctuationLineChartData(HttpServletRequest request,HttpServletResponse response, Long productId){
//    IPurchaseCostStatService purchaseCostStatService = ServiceManager.getService(IPurchaseCostStatService.class);
//    Map<String,Object> result = new HashMap<String,Object>();
//    List<Long> timePointList = new ArrayList<Long>();
//    //获取去年的今天的零点时刻毫秒数，例如今天是2012-11-07，时间范围则是2011-11-07 00:00:00 - 2012-11-07 00:00:00，共366个时间点
//    Calendar c = Calendar.getInstance();
//    c.set(Calendar.HOUR_OF_DAY, 0);
//    c.set(Calendar.MINUTE, 0);
//    c.set(Calendar.SECOND, 0);
//    c.set(Calendar.MILLISECOND,0);
//    Long today = c.getTimeInMillis();
//    c.add(Calendar.YEAR, -1);
//    Long timePoint = c.getTimeInMillis();
//    long days = (today-timePoint)/(24*3600*1000);
//    for(int i=0;i<days+1;i++){
//      timePoint = timePoint + 24*3600*1000;
//      timePointList.add(timePoint);
//    }
//    Long shopId = WebUtil.getShopId(request);
//    result = purchaseCostStatService.getPriceFluctuationLineChartData(shopId,productId,timePointList);
//    try {
//      PrintWriter writer = response.getWriter();
//      writer.write(JsonUtil.objectToJson(result));
//      writer.close();
//    } catch (IOException e) {
//      e.printStackTrace();
//    }
//  }

  //获取价格波动折线图的数据
  @RequestMapping(params="method=getPriceFluctuationLineChartData")
  public void getPriceFluctuationLineChartData(HttpServletRequest request,HttpServletResponse response, Long productId){
    Long shopId = WebUtil.getShopId(request);
    IPurchaseCostStatService purchaseCostStatService = ServiceManager.getService(IPurchaseCostStatService.class);
    List<List<Object>> resultList = new ArrayList<List<Object>>();

    //获取去年的今天的零点时刻毫秒数，例如今天是2012-11-07，时间范围则是2011-11-07 00:00:00 - 2012-11-07 00:00:00，共366个时间点
    Calendar c = Calendar.getInstance();
    c.set(Calendar.HOUR_OF_DAY, 0);
    c.set(Calendar.MINUTE, 0);
    c.set(Calendar.SECOND, 0);
    c.set(Calendar.MILLISECOND,0);
    c.add(Calendar.HOUR,6);
    Long endTime = c.getTimeInMillis();
    c.add(Calendar.YEAR, -1);
    Long startTime = c.getTimeInMillis();

    List<List<Object>> dataList = purchaseCostStatService.getPriceFluctuationLineChartData(shopId,productId,startTime,endTime);

    //补足整年数据，无数据的那天补零
    long days = (endTime-startTime)/(24*3600*1000);
    for(int i=0;i<days;i++){
      List<Object> resultItemList = new ArrayList<Object>();
      Long itemTime = startTime + 86400000l * i;
      Double itemValue = 0d;
      for (int j=0;j<dataList.size();j++){
        if(itemTime == Long.parseLong((String)dataList.get(j).get(0))){
          itemValue = (Double)dataList.get(j).get(1);
          break;
        }
      }
      resultItemList.add(itemTime+86400000l);
      resultItemList.add(itemValue);
      resultList.add(resultItemList);
    }
    try {
      PrintWriter writer = response.getWriter();
      writer.write(JsonUtil.objectToJson(resultList));
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
