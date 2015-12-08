package com.bcgogo.print;

import com.bcgogo.common.Result;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.PrintTemplateDTO;
import com.bcgogo.txn.dto.ShopPrintTemplateDTO;
import com.bcgogo.txn.service.IPrintService;
import com.bcgogo.txn.service.PrintService;
import com.bcgogo.utils.JsonUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: cfl
 * Date: 12-6-12
 * Time: 上午10:52
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/print.do")
public class PrintController {
  private static final Logger LOG = LoggerFactory.getLogger(PrintController.class);

  @RequestMapping(params = "method=toLeadPage")
  public String toLeadPage(HttpServletRequest request, ModelMap model) {
    prepareModelData(model);
    model.addAttribute("shopId", PrintService.defaultTemplateShopId);
    model.addAttribute("shopName", "默认模板");
    return "/print/leadPrintPage";
  }

  private void prepareModelData(ModelMap model){
    Map<OrderTypes, String> printTemplateMap = prepareTemplateTypeMap();
    model.addAttribute("printTemplateMap", printTemplateMap);
    PrintTemplateDTO print = new PrintTemplateDTO();
    PrintTemplateDTO printTemplateDTO = new PrintTemplateDTO();
    model.addAttribute("print", print);
    model.addAttribute("printTemplateDTO", printTemplateDTO);
  }

  @RequestMapping(params = "method=chooseOldTemplate")
  public String chooseOldTemplate(HttpServletRequest request, HttpServletResponse response) {
    Map<OrderTypes, String> printTemplateMap = prepareTemplateTypeMap();
    request.setAttribute("printTemplateMap", printTemplateMap);
    PrintTemplateDTO printTemplateDTO = new PrintTemplateDTO();
    request.setAttribute("print", printTemplateDTO);
    return "/print/chooseOldTemplate";
  }

  private Map<OrderTypes, String> prepareTemplateTypeMap(){
    Map<OrderTypes, String> printTemplateMap = new LinkedHashMap<OrderTypes, String>();
    printTemplateMap.put(OrderTypes.PURCHASE, OrderTypes.PURCHASE.getName());
    printTemplateMap.put(OrderTypes.INVENTORY, OrderTypes.INVENTORY.getName());
    printTemplateMap.put(OrderTypes.SALE, OrderTypes.SALE.getName());
    printTemplateMap.put(OrderTypes.REPAIR, OrderTypes.REPAIR.getName());
    printTemplateMap.put(OrderTypes.REPAIR_SECONDARY, OrderTypes.REPAIR_SECONDARY.getName());
    printTemplateMap.put(OrderTypes.RETURN, OrderTypes.RETURN.getName());
    printTemplateMap.put(OrderTypes.DEBT, OrderTypes.DEBT.getName());
    printTemplateMap.put(OrderTypes.PAYABLE, OrderTypes.PAYABLE.getName());
    printTemplateMap.put(OrderTypes.DEPOSIT, OrderTypes.DEPOSIT.getName());
    printTemplateMap.put(OrderTypes.BIZSTAT, OrderTypes.BIZSTAT.getName());
    printTemplateMap.put(OrderTypes.WASH_TICKET,OrderTypes.WASH_TICKET.getName());
    printTemplateMap.put(OrderTypes.WASH_AUTO_TICKET,OrderTypes.WASH_AUTO_TICKET.getName());
    printTemplateMap.put(OrderTypes.WASH_BEAUTY,OrderTypes.WASH_BEAUTY.getName());
    printTemplateMap.put(OrderTypes.MEMBER_BUY_CARD,OrderTypes.MEMBER_BUY_CARD.getName());
    printTemplateMap.put(OrderTypes.MEMBER_RETURN_CARD,OrderTypes.MEMBER_RETURN_CARD.getName());
    printTemplateMap.put(OrderTypes.CUSTOMER_BUSINESS_STATISTICS,OrderTypes.CUSTOMER_BUSINESS_STATISTICS.getName());
    printTemplateMap.put(OrderTypes.SUPPLIER_BUSINESS_STATISTICS,OrderTypes.SUPPLIER_BUSINESS_STATISTICS.getName());
    printTemplateMap.put(OrderTypes.PRODUCT_CATEGORY_SALES_STATISTICS,OrderTypes.PRODUCT_CATEGORY_SALES_STATISTICS.getName());
    printTemplateMap.put(OrderTypes.BUSINESS_CATEGORY_SALES_STATISTICS,OrderTypes.BUSINESS_CATEGORY_SALES_STATISTICS.getName());
    printTemplateMap.put(OrderTypes.SERVICE_SALES_STATISTICS,OrderTypes.SERVICE_SALES_STATISTICS.getName());
    printTemplateMap.put(OrderTypes.SALE_RETURN,OrderTypes.SALE_RETURN.getName());
    printTemplateMap.put(OrderTypes.INVENTORY_PRINT,OrderTypes.INVENTORY_PRINT.getName());
    printTemplateMap.put(OrderTypes.BUSINESS_ACCOUNT,OrderTypes.BUSINESS_ACCOUNT.getName());
    printTemplateMap.put(OrderTypes.PAYABLE_STATISTICAL,OrderTypes.PAYABLE_STATISTICAL.getName());
    printTemplateMap.put(OrderTypes.RECEIVABLE_STATISTICAL,OrderTypes.RECEIVABLE_STATISTICAL.getName());
    printTemplateMap.put(OrderTypes.BIZSTAT_REPAIR_DETAIL,OrderTypes.BIZSTAT_REPAIR_DETAIL.getName());
    printTemplateMap.put(OrderTypes.BIZSTAT_SALES_DETAIL,OrderTypes.BIZSTAT_SALES_DETAIL.getName());
    printTemplateMap.put(OrderTypes.BIZSTAT_WASH_DETAIL,OrderTypes.BIZSTAT_WASH_DETAIL.getName());
    printTemplateMap.put(OrderTypes.BUSINESS_MEMBER_CONSUME,OrderTypes.BUSINESS_MEMBER_CONSUME.getName());
    printTemplateMap.put(OrderTypes.BUSINESS_MEMBER_RETURN,OrderTypes.BUSINESS_MEMBER_RETURN.getName());
    printTemplateMap.put(OrderTypes.BUSINESS_MEMBER_CARD_ORDER,OrderTypes.BUSINESS_MEMBER_CARD_ORDER.getName());
    printTemplateMap.put(OrderTypes.RUNNING_DAY_INCOME,OrderTypes.RUNNING_DAY_INCOME.getName());
    printTemplateMap.put(OrderTypes.RUNNING_MONTH_INCOME,OrderTypes.RUNNING_MONTH_INCOME.getName());
    printTemplateMap.put(OrderTypes.RUNNING_YEAR_INCOME,OrderTypes.RUNNING_YEAR_INCOME.getName());
    printTemplateMap.put(OrderTypes.RUNNING_DAY_EXPEND,OrderTypes.RUNNING_DAY_EXPEND.getName());
    printTemplateMap.put(OrderTypes.RUNNING_MONTH_EXPEND,OrderTypes.RUNNING_MONTH_EXPEND.getName());
    printTemplateMap.put(OrderTypes.RUNNING_YEAR_EXPEND,OrderTypes.RUNNING_YEAR_EXPEND.getName());
    printTemplateMap.put(OrderTypes.REPAIR_PICKING,OrderTypes.REPAIR_PICKING.getName());
    printTemplateMap.put(OrderTypes.INNER_PICKING,OrderTypes.INNER_PICKING.getName());
    printTemplateMap.put(OrderTypes.INNER_RETURN,OrderTypes.INNER_RETURN.getName());
    printTemplateMap.put(OrderTypes.INSURANCE,OrderTypes.INSURANCE.getName());
    printTemplateMap.put(OrderTypes.ALLOCATE_RECORD,OrderTypes.ALLOCATE_RECORD.getName());
    printTemplateMap.put(OrderTypes.QUALIFIED_CREDENTIAL,OrderTypes.QUALIFIED_CREDENTIAL.getName());
    printTemplateMap.put(OrderTypes.CUSTOMER_SUPPLIER_STATEMENT_ACCOUNT,OrderTypes.CUSTOMER_SUPPLIER_STATEMENT_ACCOUNT.getName());
    printTemplateMap.put(OrderTypes.INVENTORY_CHECK,OrderTypes.INVENTORY_CHECK.getName());
    printTemplateMap.put(OrderTypes.BORROW_ORDER,OrderTypes.BORROW_ORDER.getName());
    printTemplateMap.put(OrderTypes.SALE_RETURN_BUSINESS_STATISTICS,OrderTypes.SALE_RETURN_BUSINESS_STATISTICS.getName());
    printTemplateMap.put(OrderTypes.INVENTORY_RETURN_BUSINESS_STATISTICS,OrderTypes.INVENTORY_RETURN_BUSINESS_STATISTICS.getName());
    printTemplateMap.put(OrderTypes.PRE_PAY, OrderTypes.PRE_PAY.getName());
    printTemplateMap.put(OrderTypes.PRE_RECEIVE, OrderTypes.PRE_RECEIVE.getName());
    printTemplateMap.put(OrderTypes.PRE_PAY_STATISTICS, OrderTypes.PRE_PAY_STATISTICS.getName());
    printTemplateMap.put(OrderTypes.PRE_RECEIVE_STATISTICS, OrderTypes.PRE_RECEIVE_STATISTICS.getName());
    printTemplateMap.put(OrderTypes.ASSISTENT_STAT, OrderTypes.ASSISTENT_STAT.getName());
    printTemplateMap.put(OrderTypes.ASSISTENT_MEMBER_CARD_STAT, OrderTypes.ASSISTENT_MEMBER_CARD_STAT.getName());
    printTemplateMap.put(OrderTypes.ASSISTENT_SERVICE_STAT, OrderTypes.ASSISTENT_SERVICE_STAT.getName());
    printTemplateMap.put(OrderTypes.ASSISTENT_WASH_STAT, OrderTypes.ASSISTENT_WASH_STAT.getName());
    printTemplateMap.put(OrderTypes.ASSISTENT_PRODUCT_STAT, OrderTypes.ASSISTENT_PRODUCT_STAT.getName());
    printTemplateMap.put(OrderTypes.ASSISTANT_BUSINESS_ACCOUNT_STAT, OrderTypes.ASSISTANT_BUSINESS_ACCOUNT_STAT.getName());
    printTemplateMap.put(OrderTypes.PENDING_PURCHASE_ORDER, OrderTypes.PENDING_PURCHASE_ORDER.getName());
    printTemplateMap.put(OrderTypes.APPOINT_ORDER, OrderTypes.APPOINT_ORDER.getName());
    printTemplateMap.put(OrderTypes.INSURANCE_PREVIEW,OrderTypes.INSURANCE_PREVIEW.getName());
    printTemplateMap.put(OrderTypes.BCGOGO_HARDWARE_RECEIVABLE_ORDER, OrderTypes.BCGOGO_HARDWARE_RECEIVABLE_ORDER.getName());
    printTemplateMap.put(OrderTypes.ENQUIRY_ORDER, OrderTypes.ENQUIRY_ORDER.getName());
    return printTemplateMap;
  }

  @RequestMapping(params = "method=getTemplateNameByName")
  public void getTemplateNameByName(HttpServletRequest request, HttpServletResponse response, ModelMap model, String name, Long shopId) {
    IPrintService printService = ServiceManager.getService(IPrintService.class);
    try {
      PrintWriter out = response.getWriter();
      int count = printService.countPrintTemplateDTOByName(shopId, name);

      if (count>0) {
        out.write("[{resu:\"error\"}]");
      } else {
        out.write("[{resu:\"success\"}]");
      }
      out.close();

    } catch (Exception e) {
      LOG.debug("method=getTemplateNameByName");
      LOG.error(e.getMessage(), e);
    }
  }

  @RequestMapping(params = "method=getTemplateNameByType")
  public void getTemplateNameByType(HttpServletRequest request, HttpServletResponse response, String type) {
    IPrintService printService = ServiceManager.getService(IPrintService.class);
    List<PrintTemplateDTO> printTemplateDTOList = null;
    String jsonStr = "";
    try {
      PrintWriter out = response.getWriter();
      printTemplateDTOList = printService.getPrintTemplateDTOByType(OrderTypes.valueOf(type));
      if (null != printTemplateDTOList) {
        jsonStr = JsonUtil.listToJson(printTemplateDTOList);
      } else {
        jsonStr = "[]";
      }
      out.write(jsonStr);
      out.close();
    } catch (Exception e) {
      LOG.debug("method=getTemplateNameByType");
      LOG.error(e.getMessage(), e);
    }
  }

  @RequestMapping(params = "method=ShopRelevanceTemplate")
  public void ShopRelevanceTemplate(HttpServletRequest request, HttpServletResponse response, ModelMap model) throws Exception {
    StringBuffer sb = new StringBuffer("");
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    IPrintService printService = ServiceManager.getService(IPrintService.class);
    ShopDTO shopDTO = null;
    ShopPrintTemplateDTO shopPrintTemplateDTO = null;
    String shopName = request.getParameter("shopName");
    String orderType = request.getParameter("orderType");
    String shopId = request.getParameter("shopId");
    Long templateId = Long.valueOf(request.getParameter("templateId"));
    String displayName = request.getParameter("displayName");
    PrintWriter out = response.getWriter();
    try {
      shopPrintTemplateDTO = new ShopPrintTemplateDTO();
      shopPrintTemplateDTO.setOrderType(OrderTypes.valueOf(orderType));
      shopPrintTemplateDTO.setTemplateId(templateId);
      shopPrintTemplateDTO.setDisplayName(displayName);
      if (null == shopName || "".equals(shopName)) {
        shopPrintTemplateDTO.setShopId(PrintService.defaultTemplateShopId);
        printService.createOrUpdateShopPrintTemplateByEmptyShopName(shopPrintTemplateDTO);
      } else {
        if (StringUtils.isNotBlank(shopId)) {
          shopDTO = configService.getShopById(Long.valueOf(shopId));
        } else {
          shopDTO = configService.getShopByName(shopName);
        }
        if (null == shopDTO) {
          sb.append("此店面没注册");
        } else {
          shopPrintTemplateDTO.setShopId(shopDTO.getId());
          printService.createOrUpdateShopPrintTemplate(shopPrintTemplateDTO);
        }
      }

    } catch (Exception e) {
      LOG.debug("method=ShopRelevanceTemplate");
      sb.append("建立失败");
      LOG.error(e.getMessage(), e);
    }

    if ("".equals(sb.toString())) {
      sb.append("建立成功");
    }
    String jsonStr = sb.toString();
    response.setCharacterEncoding("UTF-8");
    out.write(jsonStr);
    out.close();
  }


  @RequestMapping(params = "method=savePrintTemplate")
  public void savePrintTemplate(HttpServletRequest request, HttpServletResponse response, ModelMap model) throws Exception {
    PrintWriter out = response.getWriter();
    response.setCharacterEncoding("UTF-8");
    StringBuffer sb = new StringBuffer("");
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    IPrintService printService = ServiceManager.getService(IPrintService.class);
    ShopDTO shopDTO = null;
    PrintTemplateDTO printTemplateDTO = new PrintTemplateDTO();
    String shopName = request.getParameter("uploadShopName");
    String orderType = request.getParameter("orderType");
    String shopId = request.getParameter("uploadShopId");
    String templateName = request.getParameter("templateName");
    String displayName = request.getParameter("displayName");
    try {
      printTemplateDTO.setName(templateName);
      printTemplateDTO.setDisplayName(displayName);
      printTemplateDTO.setOrderType(OrderTypes.valueOf(orderType));
      MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
      MultipartFile  multipartFile = multipartRequest.getFile("printFile");
      InputStream is = multipartFile.getInputStream();
      byte[] templateHtml = configService.InputStreamToByte(is);
      printTemplateDTO.setTemplateHtml(templateHtml);
      if(shopId.equals(PrintService.defaultTemplateShopId.toString())){
        printService.createOrUpdateDefaultPrintTemplate(printTemplateDTO);
      }else{
        if(StringUtils.isNotBlank(shopId)){
          shopDTO = configService.getShopById(Long.valueOf(shopId));
        }else{
          shopDTO = configService.getShopByName(shopName);
        }
        if(null == shopDTO){
          sb.append("notRegister");
        }else{
          printService.createPrintTemplate(shopDTO.getId(), printTemplateDTO);
        }
      }
    } catch (Exception e) {
      LOG.debug("method=savePrintTemplate");
      LOG.error(e.getMessage(), e);
      sb.append("error");
    }

    if ("".equals(sb.toString())) {
      sb.append("success");
    }
    String jsonStr = sb.toString();
    Map<String,String> map = new HashMap<String, String>();
    map.put("resu",jsonStr);
    jsonStr = JsonUtil.mapToJson(map);
    response.setCharacterEncoding("UTF-8");
    out.write(jsonStr);
    out.close();
  }

  @RequestMapping(params = "method=getShopNameByName")
  public void getShopNameByName(HttpServletRequest request, HttpServletResponse response, String name) {
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    try {
      String jsonStr = "[]";
      StringBuffer sb = new StringBuffer("[");
      if (StringUtils.isNotBlank(name)) {
        List<ShopDTO> shopDTOList = configService.getShopByObscureName(name);
        if (shopDTOList != null && shopDTOList.size() > 0) {
          for (ShopDTO shopDTO : shopDTOList) {
            sb.append("{\"name\":\"" + shopDTO.getName() + "\",");
            sb.append("\"id\":\"" + shopDTO.getId().toString() + "\",");
            if (null == shopDTO.getMobile() || "".equals(shopDTO.getMobile())) {
              sb.append("\"mobile\":\"\"},");
            } else {
              sb.append("\"mobile\":\"" + shopDTO.getMobile() + "\"},");
            }
          }
          sb.replace(sb.length() - 1, sb.length(), "]");
          jsonStr = sb.toString();
        }
      }

      PrintWriter writer = response.getWriter();
      writer.write(jsonStr);
      writer.close();
    } catch (Exception e) {
      LOG.debug("/print.do");
      LOG.debug("method=getShopNameByName");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId"));
      LOG.debug("name:" + name);
      LOG.error(e.getMessage(), e);
    }
  }

  @RequestMapping(params = "method=searchPrintTemplateByShopAndType")
  public String searchPrintTemplateByShopAndType(ModelMap model, HttpServletRequest request, ShopPrintTemplateDTO printTemplateDTO){
    IPrintService printService = ServiceManager.getService(IPrintService.class);
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    try{
      List<PrintTemplateDTO> printTemplateDTOs = printService.getAllPrintTemplateDTOByShopIdAndType(printTemplateDTO.getShopId(), printTemplateDTO.getOrderType());
      model.addAttribute("printTemplates", printTemplateDTOs);

      if(printTemplateDTO.getShopId().equals(PrintService.defaultTemplateShopId)){
        model.addAttribute("shopName", "默认模板");
        model.addAttribute("shopId", PrintService.defaultTemplateShopId);
      }else{
        ShopDTO shopDTO = configService.getShopById(printTemplateDTO.getShopId());
        model.addAttribute("shopName", shopDTO.getName());
        model.addAttribute("shopId", shopDTO.getId());
      }
    }catch(Exception e){
      LOG.error("searchPrintTemplateByShopAndType error: shopID:{}, orderType:{}", printTemplateDTO.getShopId(), printTemplateDTO.getOrderType());
      LOG.error(e.getMessage(), e);
    }
    prepareModelData(model);
    model.addAttribute("printTemplateDTO", printTemplateDTO);

    return "/print/leadPrintPage";
  }

  @RequestMapping(params = "method=deleteTemplateByShopPrintTemplateId")
  @ResponseBody
  public Result deleteTemplateByShopPrintTemplateId(ModelMap model, HttpServletRequest request, Long id){
    if(id==null){
      return new Result("ID为空！", false);
    }
    try{
      IPrintService printService = ServiceManager.getService(IPrintService.class);
      printService.deleteTemplateByShopPrintTemplateId(id);
      return new Result(true);
    }catch(Exception e){
      LOG.warn("print.do?method=deleteTemplateByShopPrintTemplateId 出错");
      LOG.error(e.getMessage(), e);
      return new Result("出错！", false);
    }
  }

  @RequestMapping(params = "method=updateDisplayNameByShopPrintTemplateId")
  @ResponseBody
  public Result updateDisplayNameByShopPrintTemplateId(Long id, String displayName){
    if(id == null || StringUtils.isBlank(displayName)){
      return new Result("ID为空或输入的显示名为空！", false);
    }
    try{
      IPrintService printService = ServiceManager.getService(IPrintService.class);
      printService.updateDisplayNameByShopPrintTemplateId(id, displayName);
      return new Result(true);
    }catch(Exception e){
      LOG.warn("print.do?method=updateDisplayNameByShopPrintTemplateId 出错");
      LOG.error(e.getMessage(), e);
      return new Result(e.getMessage(), false);
    }
  }

}
