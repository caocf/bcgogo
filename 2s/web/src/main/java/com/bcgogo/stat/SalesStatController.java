package com.bcgogo.stat;

import com.bcgogo.common.WebUtil;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.stat.dto.SalesStatCondition;
import com.bcgogo.txn.service.IPurchaseCostStatService;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.StatConstant;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * 畅销、滞销品统计专用controller
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 12-10-30
 * Time: 下午8:09
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/salesStat.do")
public class SalesStatController {
  private static final Log LOG = LogFactory.getLog(SalesStatController.class);


  /**
   * 畅销品统计
   *
   * @param model
   * @param request
   * @param salesStatCondition 查询条件
   * @return
   */
  @RequestMapping(params = "method=getGoodSaleCost")
  public String getGoodSaleCost(Model model, HttpServletRequest request, SalesStatCondition salesStatCondition) {
    Long shopId = null;
    try {
      IPurchaseCostStatService purchaseCostStatService = ServiceManager.getService(IPurchaseCostStatService.class);

      shopId = WebUtil.getShopId(request);
      if (shopId == null) {
        return "/";
      }
      salesStatCondition = this.getDefaultSalesStatCondition(salesStatCondition);

      double total = 0;  //销售总金额
      double totalAmount = 0; //销售总数量
      double result = 0;//返回结果 用于整除
      int productSize = 0;//这段时期内畅销的商品种类
      List<String> stringList = purchaseCostStatService.countSalesStatByCondition(shopId, salesStatCondition);
      if (CollectionUtils.isNotEmpty(stringList) && stringList.size() == StatConstant.RESULT_SIZE) {
        totalAmount = Double.valueOf(stringList.get(0));
        total = Double.valueOf(stringList.get(1));
        productSize = Integer.valueOf(stringList.get(2));
      }
      if (totalAmount < 0) {
        model.addAttribute("result", result);
        model.addAttribute("total", total);
        model.addAttribute("totalAmount", totalAmount);
        return "/stat/goodSaleStatistics";
      }

      List<ProductDTO> productDTOList = purchaseCostStatService.querySalesStatByCondition(shopId, salesStatCondition);
      if (CollectionUtils.isEmpty(productDTOList)) {
        model.addAttribute("total", total);
        model.addAttribute("totalAmount", totalAmount);
        model.addAttribute("result", result);
        return "/stat/goodSaleStatistics";
      }

      productDTOList = purchaseCostStatService.getProductInfo(productDTOList,shopId);

      ProductDTO other = new ProductDTO();
      double topTotal = 0;
      double topAmount = 0;
      for (ProductDTO productDTO : productDTOList) {
        topTotal += productDTO.getSalesTotal();
        topAmount += productDTO.getSalesAmount();
        if (salesStatCondition.getMoneyOrAmount().equals(StatConstant.QUERY_BY_MONEY)) {
          productDTO.setQueryResult(productDTO.getSalesTotal());
        } else {
          productDTO.setQueryResult(productDTO.getSalesAmount());
        }
      }

      other.setSalesTotal(total - topTotal);
      other.setSalesAmount(totalAmount - topAmount);
      other.setQueryResultStr("其他商品");  ;


      if (salesStatCondition.getMoneyOrAmount().equals(StatConstant.QUERY_BY_MONEY)) {
        model.addAttribute("result", total);
        other.setQueryResult(other.getSalesTotal());
      } else {
        model.addAttribute("result", totalAmount);
        other.setQueryResult(other.getSalesAmount());

      }

      List<ProductDTO> otherList = new ArrayList<ProductDTO>();
      if (productSize > StatConstant.QUERY_SIZE) {
        otherList.add(other);
      }
      model.addAttribute("other", otherList);
      model.addAttribute("itemDTOs", productDTOList);
      model.addAttribute("total", total);
      model.addAttribute("totalAmount", totalAmount);
      return "/stat/goodSaleStatistics";
    } catch (Exception e) {
      LOG.error("salesStatController.java  getGoodSaleCost shopId:" + shopId);
      LOG.error(e.getMessage(), e);
    }

    return "/stat/goodSaleStatistics";
  }


  @RequestMapping(params = "method=getBadSaleCost")
  public String getBadSaleCost(Model model, HttpServletRequest request, SalesStatCondition salesStatCondition) {
    Long shopId = null;
    try {
      IPurchaseCostStatService purchaseCostStatService = ServiceManager.getService(IPurchaseCostStatService.class);
      shopId = WebUtil.getShopId(request);
      if (shopId == null) {
        return "/";
      }
      salesStatCondition = this.getDefaultSalesStatCondition(salesStatCondition);
      double total = 0;  //销售总金额
      double totalAmount = 0; //销售总数量
      double result = 0;//返回到前台
      int productSize = 0;//滞销品的商品种类
      List<String> stringList = purchaseCostStatService.countBadSalesStatByCondition(shopId, salesStatCondition);
      if (CollectionUtils.isNotEmpty(stringList) && stringList.size() == StatConstant.RESULT_SIZE) {
        totalAmount = NumberUtil.toReserve(Double.valueOf(stringList.get(0)), 1);
        total = NumberUtil.toReserve(Double.valueOf(stringList.get(1)), NumberUtil.MONEY_PRECISION);
        productSize = Integer.valueOf(stringList.get(2));
      }

      if (totalAmount < 0) {
        model.addAttribute("result", result);
        model.addAttribute("total", total);
        model.addAttribute("totalAmount", totalAmount);
        return "/stat/badSaleStatistics";
      }

      List<ProductDTO> productDTOList = purchaseCostStatService.queryBadSalesStatByCondition(shopId, salesStatCondition);
      if (CollectionUtils.isEmpty(productDTOList)) {
        model.addAttribute("result", result);
        model.addAttribute("total", total);
        model.addAttribute("totalAmount", totalAmount);
        return "/stat/badSaleStatistics";
      }

      productDTOList = purchaseCostStatService.getProductInfo(productDTOList,shopId);

      ProductDTO other = new ProductDTO();
      double topTotal = 0;
      double topAmount = 0;
      for (ProductDTO productDTO : productDTOList) {
        topTotal += productDTO.getSalesTotal();
        topAmount += productDTO.getSalesAmount();
        if (salesStatCondition.getMoneyOrAmount().equals(StatConstant.QUERY_BY_MONEY)) {
          productDTO.setQueryResult(productDTO.getSalesTotal());
        } else {
          productDTO.setQueryResult(productDTO.getSalesAmount());
        }
      }

      other.setSalesTotal(NumberUtil.toReserve(total - topTotal,NumberUtil.MONEY_PRECISION));
      other.setSalesAmount(NumberUtil.toReserve(totalAmount - topAmount, 1));
      other.setQueryResultStr("其他");

      if (salesStatCondition.getMoneyOrAmount().equals(StatConstant.QUERY_BY_MONEY)) {
        model.addAttribute("result", total);
        other.setQueryResult(other.getSalesTotal());
      } else {
        model.addAttribute("result", totalAmount);
        other.setQueryResult(other.getSalesAmount());
      }

      List<ProductDTO> otherList = new ArrayList<ProductDTO>();
      if (productSize > StatConstant.QUERY_SIZE) {
        otherList.add(other);
      }
      model.addAttribute("other", otherList);
      model.addAttribute("itemDTOs", productDTOList);
      model.addAttribute("total", NumberUtil.toReserve(total, NumberUtil.MONEY_PRECISION));
      model.addAttribute("totalAmount", NumberUtil.toReserve(totalAmount, 1));
      return "/stat/badSaleStatistics";
    } catch (Exception e) {
      LOG.error("salesStatController.java getBadSaleCost shopId:" + shopId);
      LOG.error(e.getMessage(), e);
    }

    return "/stat/badSaleStatistics";
  }

  /**
   * 默认的查询信息
   * @param salesStatCondition
   * @return
   */
  public SalesStatCondition getDefaultSalesStatCondition(SalesStatCondition salesStatCondition) {
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
    if (salesStatCondition.getYear() == null) {
      salesStatCondition.setYear(year);
    }
    if (salesStatCondition.getMonth() == null) {
      salesStatCondition.setMonth(month);
    }
    if (StringUtil.isEmpty(salesStatCondition.getMoneyOrAmount())) {
      salesStatCondition.setMoneyOrAmount(StatConstant.QUERY_BY_MONEY);
    }

    if (StringUtil.isEmpty(salesStatCondition.getQueryPeriodStr())) {
      salesStatCondition.setQueryPeriodStr(StatConstant.ONE_MONTH);
    }
    return salesStatCondition;
  }

}
