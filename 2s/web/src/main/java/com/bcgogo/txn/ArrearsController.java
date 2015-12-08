package com.bcgogo.txn;

import com.bcgogo.common.Pager;
import com.bcgogo.common.PrintHelper;
import com.bcgogo.common.WebUtil;
import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.enums.MemberStatus;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.exception.PageException;
import com.bcgogo.search.dto.OrderSearchConditionDTO;
import com.bcgogo.search.dto.OrderSearchResultDTO;
import com.bcgogo.search.dto.OrderSearchResultListDTO;
import com.bcgogo.search.dto.RecOrPayIndexDTO;
import com.bcgogo.search.service.order.ISearchOrderService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.stat.dto.PayablePrintDTO;
import com.bcgogo.txn.dto.*;
import com.bcgogo.txn.dto.StatementAccount.StatementAccountConstant;
import com.bcgogo.txn.service.ICustomerDepositService;
import com.bcgogo.txn.service.IPrintService;
import com.bcgogo.txn.service.ITxnService;
import com.bcgogo.user.dto.MemberDTO;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.StringUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.StringResourceLoader;
import org.apache.velocity.runtime.resource.util.StringResourceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 12-10-20
 * Time: 上午7:08
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/arrears.do")
public class ArrearsController extends AbstractTxnController{
  private static final Logger LOG = LoggerFactory.getLogger(ArrearsController.class);
  private static final int ARREARS_DETAIL_SIZE=5;
  private static final int ARREARS_STAT_SIZE=25;

  @RequestMapping(params = "method=toPayableSettlement")
  public String toPayableSettlement(ModelMap model,HttpServletRequest request, Long supplierId) {
    try {
      Long shopId = WebUtil.getShopId(request);
      RecOrPayIndexDTO recOrPayIndexDTO=new RecOrPayIndexDTO();
      model.addAttribute("supplierId", supplierId);
      recOrPayIndexDTO.setCustomerOrSupplierIdStr(String.valueOf(supplierId));
//      recOrPayIndexDTO.setEndDateStr(DateUtil.convertDateLongToString(System.currentTimeMillis(), DateUtil.DATE_STRING_FORMAT_DAY));
      model.addAttribute("recOrPayIndexDTO",recOrPayIndexDTO);
      Double sumDeposit= supplierPayableService.getSumDepositBySupplierId(supplierId,WebUtil.getShopId(request));
      model.addAttribute("depositAvaiable",NumberUtil.round(sumDeposit,NumberUtil.MONEY_PRECISION));
      if(StringUtils.isNotBlank(request.getParameter("orderType")) && "purchaseInventoryOrder".equals(request.getParameter("orderType")))
      {
        Long orderId = Long.valueOf(request.getParameter("orderId"));

        ITxnService txnService = ServiceManager.getService(ITxnService.class);

        PayableDTO payableDTO = txnService.getPayableDTOByOrderId(shopId,orderId);

        model.addAttribute("payableDTO",payableDTO);
        model.addAttribute("orderType",request.getParameter("orderType"));

      }

      return "/customer/payableSettlement";

    } catch (Exception e) {
      LOG.debug("/arrears.do");
      LOG.debug("method=toPayableSettlement");
      LOG.error(e.getMessage(), e);
      return null;
    }
  }

  @RequestMapping(params = "method=toReceivableSettle")
  public  String toReceivableSettle(ModelMap model,HttpServletRequest request,Long customerId) throws Exception {
    RecOrPayIndexDTO recOrPayIndexDTO=new RecOrPayIndexDTO();
    Long shopId = WebUtil.getShopId(request);
    if (customerId != null) {
      MemberDTO memberDTO = membersService.getMemberByCustomerId(shopId, customerId);
      if (memberDTO != null && memberDTO.getStatus().equals(MemberStatus.ENABLED)) {
        if (memberDTO.getMemberDiscount() != null) {
          memberDTO.setMemberDiscount(NumberUtil.round(memberDTO.getMemberDiscount() * 10, 1));
        }
        request.setAttribute("memberBalance", memberDTO.getBalance());
        request.setAttribute("memberDiscount", memberDTO.getMemberDiscount());
        request.setAttribute("memberNo", memberDTO.getMemberNo());
      }
    }
    //用于单据界面单个单据欠款结算
    if(StringUtils.isNotBlank(request.getParameter("orderType")))
    {
      String orderType = request.getParameter("orderType");

      if("goodsSaleOrder".equals(orderType))
      {
        orderType = OrderTypes.SALE.toString();
      }
      else if("washBeauty".equals(orderType))
      {
        orderType = OrderTypes.WASH_BEAUTY.toString();
      }
      else if("repairOrder".equals(orderType))
      {
        orderType = OrderTypes.REPAIR.toString();
      }
      ITxnService txnService = ServiceManager.getService(ITxnService.class);
      model.addAttribute("orderType",orderType);
      Long orderId = Long.valueOf(request.getParameter("orderId"));
      ReceivableDTO receivableDTO = txnService.getReceivableDTOByShopIdAndOrderId(shopId,orderId);
      DebtDTO debtDTO = txnService.getDebtByShopIdOrderId(shopId,orderId);

      model.addAttribute("debtDTO",debtDTO);
      model.addAttribute("receivableDTO",receivableDTO);
    }

    model.addAttribute("customerId", customerId);
    recOrPayIndexDTO.setCustomerOrSupplierIdStr(String.valueOf(customerId));
//    recOrPayIndexDTO.setEndRepayDateStr(DateUtil.convertDateLongToString(System.currentTimeMillis(), DateUtil.DATE_STRING_FORMAT_DAY));
    model.addAttribute("recOrPayIndexDTO",recOrPayIndexDTO);
      //查询用户可用预收款
      ICustomerDepositService customerDepositService = ServiceManager.getService(ICustomerDepositService.class);
      CustomerDepositDTO customerDepositDTO = customerDepositService.queryCustomerDepositByShopIdAndCustomerId(shopId, customerId);
      if (customerDepositDTO != null) {
          request.setAttribute("depositAvailable", customerDepositDTO.getActuallyPaid());
      }
    return "/txn/receivableSettle";
  }




  @RequestMapping(params = "method=toReceivableStat")
  public String toReceivableStat(ModelMap modelMap,HttpServletRequest request){
    RecOrPayIndexDTO recOrPayIndexDTO=new RecOrPayIndexDTO();
    recOrPayIndexDTO.setEndDateStr(DateUtil.convertDateLongToString(System.currentTimeMillis(), DateUtil.DATE_STRING_FORMAT_DAY));
    modelMap.addAttribute("recOrPayIndexDTO",recOrPayIndexDTO);
    modelMap.addAttribute("wholesalerVersion", ConfigUtils.isWholesalerVersion(WebUtil.getShopVersionId(request)));
    return "stat/receivableStat";
  }

  @RequestMapping(params = "method=toPayableStat")
  public String toPayableStat(ModelMap modelMap,HttpServletRequest request){
    RecOrPayIndexDTO recOrPayIndexDTO=new RecOrPayIndexDTO();
    recOrPayIndexDTO.setEndDateStr(DateUtil.convertDateLongToString(System.currentTimeMillis(), DateUtil.DATE_STRING_FORMAT_DAY));
    modelMap.addAttribute("recOrPayIndexDTO",recOrPayIndexDTO);
    modelMap.addAttribute("wholesalerVersion", ConfigUtils.isWholesalerVersion(WebUtil.getShopVersionId(request)));
    return "stat/payableStat";
  }

  @ResponseBody
  @RequestMapping(params = "method=getReceivableStatData")
  public Object getReceivableStatData(ModelMap modelMap, HttpServletRequest request, RecOrPayIndexDTO recOrPayIndexDTO) {
    recOrPayIndexDTO.setOrderByFlag("DESC");
    recOrPayIndexDTO.setOrderByField("payTime");
    recOrPayIndexDTO.setPageSize(ARREARS_STAT_SIZE);
    recOrPayIndexDTO.setOrderTypeArray(new String[]{"WASH_BEAUTY", "SALE", "REPAIR", "MEMBER_BUY_CARD", "RETURN","CUSTOMER_STATEMENT_ACCOUNT","SUPPLIER_STATEMENT_ACCOUNT"});
    return getReceivablesFromSolr(request, recOrPayIndexDTO);
  }

  @ResponseBody
  @RequestMapping(params = "method=getPayableStatData")
  public Object getPayableStatData(ModelMap modelMap, HttpServletRequest request, RecOrPayIndexDTO recOrPayIndexDTO) throws PageException {
    recOrPayIndexDTO.setOrderByFlag("DESC");
    recOrPayIndexDTO.setOrderByField("payTime");
    recOrPayIndexDTO.setPageSize(ARREARS_STAT_SIZE);
    recOrPayIndexDTO.setOrderTypeArray(new String[]{"INVENTORY", "SALE_RETURN","CUSTOMER_STATEMENT_ACCOUNT","SUPPLIER_STATEMENT_ACCOUNT"});
    return getPayablesFromSolr(request,recOrPayIndexDTO);
  }


  /**
   * 查询应付款
   * @param request
   * @param recOrPayIndexDTO
   * @return
   */
  public List<Object> getPayablesFromSolr(HttpServletRequest request, RecOrPayIndexDTO recOrPayIndexDTO) {
    try {
      recOrPayIndexDTO.setShopId(WebUtil.getShopId(request));
      recOrPayIndexDTO.convertHandler();
      OrderSearchConditionDTO orderSearchConditionDTO = recOrPayIndexDTO.toOrderSearchConditionDTO();
      ISearchOrderService searchOrderService = ServiceManager.getService(ISearchOrderService.class);
      orderSearchConditionDTO.verificationQueryTime();
      orderSearchConditionDTO.setDebtType(StatementAccountConstant.SOLR_PAYABLE_DEBT_TYPE);
      if("supplierTotalDebt".equals(recOrPayIndexDTO.getReceiver())) {
          orderSearchConditionDTO.setOrderType(new String[]{OrderTypes.INVENTORY.toString(),OrderTypes.SUPPLIER_STATEMENT_ACCOUNT.toString()});
      } else if("customerTotalDebt".equals(recOrPayIndexDTO.getReceiver())) {
          orderSearchConditionDTO.setOrderType(new String[]{OrderTypes.SALE_RETURN.toString(),OrderTypes.CUSTOMER_STATEMENT_ACCOUNT.toString()});
      }
      OrderSearchResultListDTO orderSearchResultListDTO = searchOrderService.queryOrders(orderSearchConditionDTO);

      Pager pager = null;
      if (orderSearchResultListDTO == null) {
        pager = new Pager(0, NumberUtil.intValue(recOrPayIndexDTO.getStartPageNo(), 1), recOrPayIndexDTO.getPageSize());
      } else {
        pager = new Pager((int) orderSearchResultListDTO.getNumFound(), NumberUtil.intValue(recOrPayIndexDTO.getStartPageNo(), 1), recOrPayIndexDTO.getPageSize());
      }

      double pageTotalDebt = 0;
      if (CollectionUtils.isNotEmpty(orderSearchResultListDTO.getOrders())) {
        for (OrderSearchResultDTO orderSearchResultDTO : orderSearchResultListDTO.getOrders()) {
          pageTotalDebt += NumberUtil.doubleVal(orderSearchResultDTO.getDebt());
          if (StringUtil.isEmpty(orderSearchResultDTO.getOrderType())) {
            continue;
          }
        }
      }
      recOrPayIndexDTO.setPager(pager);
      List<Object> result = new ArrayList<Object>();
      Map<String, Object> data = new HashMap<String, Object>();
      data.put("payables", orderSearchResultListDTO.getOrders());
      data.put("pageArrears", NumberUtil.toReserve(pageTotalDebt,NumberUtil.MONEY_PRECISION));
      orderSearchConditionDTO.setOrderType(recOrPayIndexDTO.getOrderTypeArray());
      Map<String, Double> totalAmounts = searchOrderService.queryOrders(orderSearchConditionDTO).getTotalAmounts();
      data.put("totalArrears", NumberUtil.toReserve(totalAmounts==null?0:totalAmounts.get("ORDER_DEBT_AMOUNT"), NumberUtil.MONEY_PRECISION));
      data.put("totalCostPriceStat",NumberUtil.toReserve(totalAmounts==null?0:totalAmounts.get("TOTAL_COST_PRICE"), NumberUtil.MONEY_PRECISION));
      data.put("recOrPayIndexDTO", recOrPayIndexDTO);
      orderSearchConditionDTO.setOrderType(new String[]{OrderTypes.SALE_RETURN.toString(),OrderTypes.CUSTOMER_STATEMENT_ACCOUNT.toString()});
      totalAmounts  = searchOrderService.queryOrders(orderSearchConditionDTO).getTotalAmounts();
      data.put("customerTotalDebt",NumberUtil.toReserve(totalAmounts == null ? 0 : totalAmounts.get("ORDER_DEBT_AMOUNT"),NumberUtil.MONEY_PRECISION));
      orderSearchConditionDTO.setOrderType(new String[]{OrderTypes.INVENTORY.toString(),OrderTypes.SUPPLIER_STATEMENT_ACCOUNT.toString()});
      totalAmounts  = searchOrderService.queryOrders(orderSearchConditionDTO).getTotalAmounts();
      data.put("supplierTotalDebt",NumberUtil.toReserve(totalAmounts == null ? 0 : totalAmounts.get("ORDER_DEBT_AMOUNT"),NumberUtil.MONEY_PRECISION));
      result.add(data);
      result.add(pager);
      return result;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return null;
    }
  }

  /**
   * 查询应收款
   * @param request
   * @return
   */
  public List<Object> getReceivablesFromSolr(HttpServletRequest request,RecOrPayIndexDTO recOrPayIndexDTO) {
    try {
      IUserService userService = ServiceManager.getService(IUserService.class);
      recOrPayIndexDTO.setShopId(WebUtil.getShopId(request));
      recOrPayIndexDTO.convertHandler();

      OrderSearchConditionDTO orderSearchConditionDTO = recOrPayIndexDTO.toOrderSearchConditionDTO();
      ISearchOrderService searchOrderService = ServiceManager.getService(ISearchOrderService.class);
      orderSearchConditionDTO.verificationQueryTime();
      orderSearchConditionDTO.setDebtType(StatementAccountConstant.SOLR_RECEIVABLE_DEBT_TYPE);
      if("supplierTotalDebt".equals(recOrPayIndexDTO.getReceiver())) {
          orderSearchConditionDTO.setOrderType(new String[]{OrderTypes.SUPPLIER_STATEMENT_ACCOUNT.toString(),OrderTypes.RETURN.toString()});
      } else if("customerTotalDebt".equals(recOrPayIndexDTO.getReceiver())) {
          orderSearchConditionDTO.setOrderType(new String[]{"WASH_BEAUTY", "SALE", "REPAIR", "MEMBER_BUY_CARD","CUSTOMER_STATEMENT_ACCOUNT"});
      }
      OrderSearchResultListDTO orderSearchResultListDTO = searchOrderService.queryOrders(orderSearchConditionDTO);

      Pager pager = null;
      if (orderSearchResultListDTO == null) {
        pager = new Pager(0, NumberUtil.intValue(recOrPayIndexDTO.getStartPageNo(), 1), recOrPayIndexDTO.getPageSize());
      } else {
        pager = new Pager((int) orderSearchResultListDTO.getNumFound(), NumberUtil.intValue(recOrPayIndexDTO.getStartPageNo(), 1), recOrPayIndexDTO.getPageSize());
      }

      double pageTotalDebt = 0;
      if (CollectionUtils.isNotEmpty(orderSearchResultListDTO.getOrders())) {
        for (OrderSearchResultDTO orderSearchResultDTO : orderSearchResultListDTO.getOrders()) {
          pageTotalDebt += NumberUtil.doubleVal(orderSearchResultDTO.getDebt());
          if (StringUtil.isEmpty(orderSearchResultDTO.getOrderType())) {
            continue;
          }
        }
      }

      List result = new ArrayList();
      Map<String, Object> data = new HashMap<String, Object>();
      recOrPayIndexDTO.setPager(pager);
      data.put("receivables", orderSearchResultListDTO.getOrders());
      data.put("pageArrears", NumberUtil.toReserve(pageTotalDebt,NumberUtil.MONEY_PRECISION));
      data.put("recOrPayIndexDTO", recOrPayIndexDTO);
      orderSearchConditionDTO.setOrderType(recOrPayIndexDTO.getOrderTypeArray());
      Map<String, Double> totalAmounts = searchOrderService.queryOrders(orderSearchConditionDTO).getTotalAmounts();
      data.put("totalArrears", NumberUtil.toReserve(totalAmounts==null?0:totalAmounts.get("ORDER_DEBT_AMOUNT"), NumberUtil.MONEY_PRECISION));
      data.put("totalCostPriceStat",NumberUtil.toReserve(totalAmounts==null?0:totalAmounts.get("TOTAL_COST_PRICE"), NumberUtil.MONEY_PRECISION));
      orderSearchConditionDTO.setOrderType(new String[]{OrderTypes.SUPPLIER_STATEMENT_ACCOUNT.toString(),OrderTypes.RETURN.toString()});
      totalAmounts  = searchOrderService.queryOrders(orderSearchConditionDTO).getTotalAmounts();
      data.put("supplierTotalDebt",NumberUtil.toReserve(totalAmounts == null ? 0 : totalAmounts.get("ORDER_DEBT_AMOUNT"),NumberUtil.MONEY_PRECISION));
      orderSearchConditionDTO.setOrderType(new String[]{"WASH_BEAUTY", "SALE", "REPAIR", "MEMBER_BUY_CARD","CUSTOMER_STATEMENT_ACCOUNT"});
      totalAmounts  = searchOrderService.queryOrders(orderSearchConditionDTO).getTotalAmounts();
      data.put("customerTotalDebt",NumberUtil.toReserve(totalAmounts == null ? 0 : totalAmounts.get("ORDER_DEBT_AMOUNT"),NumberUtil.MONEY_PRECISION));
      result.add(data);
      result.add(pager);
      return result;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return null;
    }
  }


  @RequestMapping(params = "method=getPayableToPrint")
  public void getPayableToPrint(HttpServletRequest request,HttpServletResponse response, RecOrPayIndexDTO recOrPayIndexDTO) throws Exception {
    recOrPayIndexDTO.setOrderByFlag("DESC");
    recOrPayIndexDTO.setOrderByField("payTime");
    recOrPayIndexDTO.setPageSize(ARREARS_STAT_SIZE);
    recOrPayIndexDTO.setOrderTypeArray(new String[]{"INVENTORY", "SALE_RETURN","CUSTOMER_STATEMENT_ACCOUNT","SUPPLIER_STATEMENT_ACCOUNT"});
    List<Object> payableResult = getPayablesFromSolr(request,recOrPayIndexDTO);
    if(CollectionUtils.isEmpty(payableResult)){
      return;
    }
    Map<String, Object> data = (Map<String, Object>) payableResult.get(0);
    List<OrderSearchResultDTO> searchResult = (List<OrderSearchResultDTO>) data.get("payables");

    List<PayablePrintDTO> payablePrintDTOs = convertToPayablePrintDTO(searchResult);

    IPrintService printService = ServiceManager.getService(IPrintService.class);
    try{
      String myTemplateName = "payableStatisticalPrint"+ String.valueOf(WebUtil.getShopId(request));
      PrintTemplateDTO printTemplateDTO = printService.getSinglePrintTemplateDTOByShopIdAndType(WebUtil.getShopId(request), OrderTypes.PAYABLE_STATISTICAL);

      VelocityContext context = new VelocityContext();

      //把数据填入上下文
      context.put("startDateStr", recOrPayIndexDTO.getStartDateStr());
      context.put("endDateStr", recOrPayIndexDTO.getEndDateStr());
      context.put("payableDTOList",payablePrintDTOs);
      context.put("totalArrears", data.get("totalArrears"));
      context.put("supplierTotalDebt", data.get("supplierTotalDebt"));
      context.put("customerTotalDebt", data.get("customerTotalDebt"));
      context.put("totalCostPriceStat", data.get("totalCostPriceStat"));
      context.put("pageTotal", data.get("pageArrears"));
      PrintHelper.generatePrintPage(response, printTemplateDTO.getTemplateHtml(), myTemplateName, context);
    } catch(Exception e) {
      LOG.error("method=getPayableToPrint");
      LOG.error(e.getMessage(),e);
    }
  }

  private List<PayablePrintDTO> convertToPayablePrintDTO(List<OrderSearchResultDTO> searchResult) {
    if(CollectionUtils.isEmpty(searchResult)){
      return new ArrayList<PayablePrintDTO>();
    }
    List<PayablePrintDTO> payablePrintDTOs = new ArrayList<PayablePrintDTO>();
    for(OrderSearchResultDTO dto : searchResult){
      PayablePrintDTO printDTO = new PayablePrintDTO();
      printDTO.setReceiptNo(dto.getReceiptNo());
      printDTO.setPayTimeStr(dto.getVestDateStr());
      printDTO.setSupplierName(dto.getCustomerOrSupplierName());
      printDTO.setOrderType(dto.getOrderTypeValue());
      printDTO.setAmount(dto.getAmount());
      printDTO.setPaidAmount(dto.getSettled());
      printDTO.setDeduction(dto.getDiscount());
      printDTO.setCreditAmount(dto.getDebt());
      printDTO.setTotalCostPrice(dto.getTotalCostPrice());
      payablePrintDTOs.add(printDTO);
    }
    return payablePrintDTOs;
  }


  @RequestMapping(params = "method=getReceivableToPrint")
  public void getReceivableToPrint(HttpServletRequest request, HttpServletResponse response, RecOrPayIndexDTO recOrPayIndexDTO) {
    recOrPayIndexDTO.setOrderByFlag("DESC");
    recOrPayIndexDTO.setOrderByField("payTime");
    recOrPayIndexDTO.setPageSize(ARREARS_STAT_SIZE);
    recOrPayIndexDTO.setOrderTypeArray(new String[]{"WASH_BEAUTY", "SALE", "REPAIR", "MEMBER_BUY_CARD", "RETURN", "CUSTOMER_STATEMENT_ACCOUNT", "SUPPLIER_STATEMENT_ACCOUNT"});
    List<Object> result = getReceivablesFromSolr(request, recOrPayIndexDTO);
    if (CollectionUtils.isEmpty(result)) {
      return;
    }
    Map<String, Object> data = (Map<String, Object>) result.get(0);
    List<OrderSearchResultDTO> orderSearchResultDTOs = (List<OrderSearchResultDTO>) data.get("receivables");

    IPrintService printService = ServiceManager.getService(IPrintService.class);

    VelocityContext context = new VelocityContext();
    //把数据填入上下文
    context.put("startDateStr", recOrPayIndexDTO.getStartDateStr());
    context.put("endDateStr", recOrPayIndexDTO.getEndDateStr());
    context.put("debtDTOList", orderSearchResultDTOs);
    context.put("totalArrears", data.get("totalArrears"));
    context.put("supplierTotalDebt", data.get("supplierTotalDebt"));
    context.put("customerTotalDebt", data.get("customerTotalDebt"));
    context.put("totalCostPriceStat", data.get("totalCostPriceStat"));
    context.put("pageTotal", data.get("pageArrears"));

    try {
      PrintTemplateDTO printTemplateDTO = printService.getSinglePrintTemplateDTOByShopIdAndType(WebUtil.getShopId(request), OrderTypes.RECEIVABLE_STATISTICAL);
      String myTemplateName = "payableStatisticalPrint" + String.valueOf(WebUtil.getShopId(request));

      PrintHelper.generatePrintPage(response, printTemplateDTO.getTemplateHtml(), myTemplateName, context);
    } catch (Exception e) {
      LOG.error("method=getReceivableToPrint");
      LOG.error(e.getMessage(), e);
    }
  }

}
