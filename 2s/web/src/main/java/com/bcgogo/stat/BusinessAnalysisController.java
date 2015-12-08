package com.bcgogo.stat;

import com.bcgogo.common.WebUtil;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.search.dto.OrderSearchConditionDTO;
import com.bcgogo.search.dto.OrderSearchResultListDTO;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.PrintTemplateDTO;
import com.bcgogo.txn.service.IPrintService;
import com.bcgogo.txn.service.ISaleReturnOrderService;
import com.bcgogo.txn.service.ITxnService;
import com.bcgogo.utils.StatConstant;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections.CollectionUtils;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 13-1-30
 * Time: 下午4:59
 * To change this template use File | Settings | File Templates.
 */

@Controller
@RequestMapping("/businessAnalysis.do")
public class BusinessAnalysisController {
  private static final Log LOG = LogFactory.getLog(BusinessAnalysisController.class);

  /**
   * 跳转到销售退货统计页面
   *
   * @param request
   * @param model
   * @return
   */
  @RequestMapping(params = "method=redirectSalesReturnStat")
  public String redirectSalesReturnStat(HttpServletRequest request, ModelMap model) {
    return "stat/businessAnalysis/salesReturnStat";
  }

  /**
   * 跳转到入库退货统计页面
   *
   * @param request
   * @param model
   * @return
   */
  @RequestMapping(params = "method=redirectInventoryReturnStat")
  public String redirectInventoryReturnStat(HttpServletRequest request, ModelMap model) {
    return "stat/businessAnalysis/inventoryReturnStatistics";
  }

  /**
   * 获取销售退货统计数据
   *
   * @param request
   * @param modelMap
   * @param orderSearchConditionDTO
   * @param startPageNo
   * @param maxRows
   * @return
   */
  @RequestMapping(params = "method=getSalesReturnList")
  @ResponseBody
  public Object getSalesReturnList(HttpServletRequest request, ModelMap modelMap, OrderSearchConditionDTO orderSearchConditionDTO, int startPageNo, int maxRows) {
    Long shopId = null;
    try {

      shopId = WebUtil.getShopId(request);
      if (orderSearchConditionDTO == null) {
        return new ArrayList<Object>();
      }
      orderSearchConditionDTO.setShopId(shopId);
      orderSearchConditionDTO.setStatType(StatConstant.SALES_RETURN_STATISTICS);
      orderSearchConditionDTO.validateBeforeQuery();
      orderSearchConditionDTO.setSearchStrategy(new String[]{OrderSearchConditionDTO.SEARCHSTRATEGY_STATS, OrderSearchConditionDTO.SEARCHSTRATEGY_CURRENT_PAGE_STATS});
      orderSearchConditionDTO.setOrderType(new String[]{"SALE_RETURN"});
      orderSearchConditionDTO.setOrderStatus(new String[]{"SETTLED"});
      orderSearchConditionDTO.setStatsFields(new String[]{"order_total_amount", "order_settled_amount", "order_debt_amount", "discount"});
      orderSearchConditionDTO.setPageStatsFields(new String[]{"order_total_amount", "order_settled_amount", "order_debt_amount", "discount"});

      ISaleReturnOrderService saleReturnOrderService = ServiceManager.getService(ISaleReturnOrderService.class);
      List list = saleReturnOrderService.getReturnStatByCondition(orderSearchConditionDTO, startPageNo, maxRows);
      if (CollectionUtils.isEmpty(list)) {
        return new ArrayList<Object>();
      }
      return list;
    } catch (Exception e) {
      LOG.error("salesReturn.getSalesReturnList,shopId:" + shopId + orderSearchConditionDTO.toString());
      LOG.error(e.getMessage(), e);

    }
    return new ArrayList<Object>();
  }

  /**
   * 获取入库退货统计数据
   *
   * @param request
   * @param modelMap
   * @param orderSearchConditionDTO
   * @param startPageNo
   * @param maxRows
   * @return
   */
  @RequestMapping(params = "method=getInventoryReturnList")
  @ResponseBody
  public Object getInventoryReturnList(HttpServletRequest request, ModelMap modelMap, OrderSearchConditionDTO orderSearchConditionDTO, int startPageNo, int maxRows) {
    Long shopId = null;
    try {
      shopId = WebUtil.getShopId(request);
      if (orderSearchConditionDTO == null) {
        return new ArrayList<Object>();
      }
      orderSearchConditionDTO.setShopId(shopId);
      orderSearchConditionDTO.setStatType(StatConstant.INVENTORY_RETURN_STATISTICS);
      orderSearchConditionDTO.validateBeforeQuery();

      orderSearchConditionDTO.setSearchStrategy(new String[]{OrderSearchConditionDTO.SEARCHSTRATEGY_STATS, OrderSearchConditionDTO.SEARCHSTRATEGY_CURRENT_PAGE_STATS});
      orderSearchConditionDTO.setOrderType(new String[]{"RETURN"});
      orderSearchConditionDTO.setOrderStatus(new String[]{"SETTLED"});
      orderSearchConditionDTO.setStatsFields(new String[]{"order_total_amount", "order_settled_amount", "order_debt_amount", "discount"});
      orderSearchConditionDTO.setPageStatsFields(new String[]{"order_total_amount", "order_settled_amount", "order_debt_amount", "discount"});


      ISaleReturnOrderService saleReturnOrderService = ServiceManager.getService(ISaleReturnOrderService.class);
      List list = saleReturnOrderService.getReturnStatByCondition(orderSearchConditionDTO, startPageNo, maxRows);
      if (CollectionUtils.isEmpty(list)) {
        return new ArrayList<Object>();
      }
      return list;
    } catch (Exception e) {
      LOG.error("salesReturn.getInventoryReturnList,shopId:" + shopId + orderSearchConditionDTO.toString());
      LOG.error(e.getMessage(), e);
    }
    return new ArrayList<Object>();
  }

  @RequestMapping(params = "method=getDataToPrint")
  public void getDataToPrint(HttpServletRequest request,HttpServletResponse response) throws Exception
  {
    String dataList = request.getParameter("dataList");

    if(org.apache.commons.lang.StringUtils.isBlank(dataList))
    {
      return;
    }

    String name = request.getParameter("name");
    String mobile = request.getParameter("mobile");
    String startTimeStr = request.getParameter("startTimeStr");
    String endTimeStr = request.getParameter("endTimeStr");

    Gson gson = new Gson();
    OrderSearchResultListDTO orderSearchResultListDTO = null;

    boolean errorflag = false;
    try
    {
      orderSearchResultListDTO  =  gson.fromJson(dataList,new TypeToken<OrderSearchResultListDTO>(){}.getType());
    }catch (Exception e)
    {
      LOG.error("Json串转换成OrderSearchResultListDTO对象出错");
      LOG.error(e.getMessage(),e);
      errorflag = true;
    }

    if(errorflag)
    {
      return;
    }

    if(null == orderSearchResultListDTO || CollectionUtils.isEmpty(orderSearchResultListDTO.getOrders()))
    {
      return;
    }

    PrintWriter out = response.getWriter();
    response.setContentType("text/html");
    response.setCharacterEncoding("UTF-8");
    try{
      ITxnService txnService = ServiceManager.getService(ITxnService.class);
      IPrintService printService = ServiceManager.getService(IPrintService.class);
      PrintTemplateDTO printTemplateDTO = null;

      if("SALE_RETURN".equals(orderSearchResultListDTO.getOrders().get(0).getOrderType()))
      {
        printTemplateDTO = printService.getSinglePrintTemplateDTOByShopIdAndType(WebUtil.getShopId(request), OrderTypes.SALE_RETURN_BUSINESS_STATISTICS);
      }
      else
      {
        printTemplateDTO = printService.getSinglePrintTemplateDTOByShopIdAndType(WebUtil.getShopId(request), OrderTypes.INVENTORY_RETURN_BUSINESS_STATISTICS);
      }

      if(null != printTemplateDTO)
      {
        byte bytes[]=printTemplateDTO.getTemplateHtml();
        String str = new String(bytes,"UTF-8");

        //初始化并取得Velocity引擎
        VelocityEngine ve = new VelocityEngine();
        ve.setProperty(VelocityEngine.RESOURCE_LOADER, "string");
        ve.setProperty("string.resource.loader.class", "org.apache.velocity.runtime.resource.loader.StringResourceLoader");
        ve.setProperty("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.SimpleLog4JLogSystem");
        ve.setProperty("runtime.log.logsystem.log4j.category", "velocity");
        ve.setProperty("runtime.log.logsystem.log4j.logger", "velocity");
        ve.init();
        //创建资源库

        StringResourceRepository repo = StringResourceLoader.getRepository();

        String myTemplate =  str;
        String myTemplateName = "";
        if("SALE_RETURN".equals(orderSearchResultListDTO.getOrders().get(0).getOrderType()))
        {
          myTemplateName = "SALE_RETURN_BUSINESS_STATISTICS"+ String.valueOf(WebUtil.getShopId(request));
        }
        else
        {
          myTemplateName = "INVENTORY_RETURN_BUSINESS_STATISTICS"+ String.valueOf(WebUtil.getShopId(request));
        }

        //模板资源存放 资源库 中

        repo.putStringResource(myTemplateName, myTemplate);

        //从资源库中加载模板

        Template template = ve.getTemplate(myTemplateName);

        //取得velocity的模版
        Template t = ve.getTemplate(myTemplateName,"UTF-8");
        //取得velocity的上下文context
        VelocityContext context = new VelocityContext();

        //把数据填入上下文
        context.put("startTimeStr",startTimeStr);
        context.put("mobile",mobile);
        context.put("endTimeStr",endTimeStr);
        context.put("name",name);
        context.put("orders",orderSearchResultListDTO.getOrders());
        context.put("orderTotal",orderSearchResultListDTO.getTotalAmounts().get("ORDER_TOTAL_AMOUNT"));
        context.put("settledTotal",orderSearchResultListDTO.getTotalAmounts().get("ORDER_SETTLED_AMOUNT"));
        context.put("debtTotal",orderSearchResultListDTO.getTotalAmounts().get("ORDER_DEBT_AMOUNT"));
        context.put("discountTotal",orderSearchResultListDTO.getTotalAmounts().get("DISCOUNT"));
        context.put("pageTotal",orderSearchResultListDTO.getCurrentPageTotalAmounts().get("order_total_amount"));
        context.put("pageSettled",orderSearchResultListDTO.getCurrentPageTotalAmounts().get("order_settled_amount"));
        context.put("pageDebt",orderSearchResultListDTO.getCurrentPageTotalAmounts().get("order_debt_amount"));
        context.put("pageDiscount",orderSearchResultListDTO.getCurrentPageTotalAmounts().get("discount"));

        //输出流
        StringWriter writer = new StringWriter();

        //转换输出
        t.merge(context, writer);
        out.print(writer);
        writer.close();
      }
      else
      {
        out.print("<html><head><title></title></head><body>没有可用的模板</body><html>");
      }

    }
    catch(Exception e)
    {
      LOG.error("method=getDataToPrint");
      LOG.error(e.getMessage(),e);
    }
    finally {
      out.close();
    }
  }
}
