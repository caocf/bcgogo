package com.bcgogo.txn;

import com.bcgogo.common.Result;
import com.bcgogo.common.WebUtil;
import com.bcgogo.config.cache.BcgogoConcurrentController;
import com.bcgogo.config.dto.OperationLogDTO;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.service.IOperationLogService;
import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.enums.*;
import com.bcgogo.enums.txn.pushMessage.PushMessageSourceType;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.product.cache.ProductUnitCache;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.product.dto.ProductLocalInfoDTO;
import com.bcgogo.product.service.IProductService;
import com.bcgogo.product.service.IPromotionsService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.bcgogoListener.orderEvent.SaleOrderSavedEvent;
import com.bcgogo.txn.bcgogoListener.publisher.BcgogoEventPublisher;
import com.bcgogo.txn.dto.*;
import com.bcgogo.txn.dto.StatementAccount.OrderDebtType;
import com.bcgogo.txn.model.SalesOrderItem;
import com.bcgogo.txn.model.SupplierInventory;
import com.bcgogo.txn.service.*;
import com.bcgogo.txn.service.productThrough.IProductOutStorageService;
import com.bcgogo.txn.service.productThrough.IProductThroughService;
import com.bcgogo.txn.service.pushMessage.IOrderPushMessageService;
import com.bcgogo.txn.service.pushMessage.IPushMessageService;
import com.bcgogo.txn.service.solr.IOrderSolrWriterService;
import com.bcgogo.user.dto.*;
import com.bcgogo.user.model.Customer;
import com.bcgogo.user.model.Member;
import com.bcgogo.user.service.ICustomerService;
import com.bcgogo.user.service.IMembersService;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.user.service.utils.BcgogoShopLogicResourceUtils;
import com.bcgogo.utils.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

import static java.lang.String.valueOf;

@Controller
@RequestMapping("/sale.do")
public class GoodSaleController extends AbstractTxnController {
  private static final Logger LOG = LoggerFactory.getLogger(GoodSaleController.class);
  private static final String REDIRECT_SHOW = "redirect:sale.do?method=getSalesOrder";
  private static final String ONLINE_SALES_ORDER = "redirect:sale.do?method=toOnlineSalesOrder";
  private static final String SHOW = "/txn/goodsSaleFinish";
  private static final String SHOW_PENDING_PURCHASE = "/autoaccessoryonline/ordercenter/onlinePendingPurchaseOrder";
  private static final String REDIRECT_CREATE = "redirect:sale.do?method=getProducts";
  private static final String REDIRECT_ONLINE_LIST = "redirect:orderCenter.do?method=getTodoOrders&type=TODO_SALE_ORDERS";

  private IOrderSolrWriterService orderSolrWriterService;

  public IOrderSolrWriterService getOrderSolrWriterService() {
    if (orderSolrWriterService == null) {
      orderSolrWriterService = ServiceManager.getService(IOrderSolrWriterService.class);
    }
    return orderSolrWriterService;
  }

  /**
   * 根据销售单准备打印
   *
   * @param model
   * @return
   */
  @RequestMapping(params = "method=getSalesOrderToPrint")
  public void getSalesOrderToPrint(ModelMap model, HttpServletRequest request, HttpServletResponse response,
                                   @RequestParam("salesOrderId") String salesOrderId, String templateId) throws Exception {
    getSalesOrderInfo(model, request, salesOrderId);
    ICustomerService customerService = ServiceManager.getService(ICustomerService.class);
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
    productService = ServiceManager.getService(IProductService.class);
    IPrintService printService = ServiceManager.getService(IPrintService.class);
    try {
      ShopDTO shopDTO = configService.getShopById(WebUtil.getShopId(request));
      PrintTemplateDTO printTemplateDTO = null;
      if (StringUtils.isBlank(templateId) || !NumberUtil.isNumber(templateId)) {
        printTemplateDTO = printService.getSinglePrintTemplateDTOByShopIdAndType(WebUtil.getShopId(request), OrderTypes.SALE);
      } else {
        printTemplateDTO = printService.getPrintTemplateDTOFullById(Long.parseLong(templateId));
      }
      SalesOrderDTO salesOrderDTO = (SalesOrderDTO) model.get("salesOrderDTO");
      salesOrderDTO.setShopLandLine(shopDTO.getLandline());
      CustomerDTO customerDTO = customerService.getCustomerById(salesOrderDTO.getCustomerId());
      if (customerDTO != null) {
        if (StringUtil.isEmpty(customerDTO.getMobile()) || StringUtil.isNotEmpty(customerDTO.getLandLine())) {
          salesOrderDTO.setMobile(customerDTO.getLandLine());
        }
        salesOrderDTO.setFax(customerDTO.getFax());

        MemberDTO memberDTO = getMembersService().getMemberByCustomerId(shopDTO.getId(), salesOrderDTO.getCustomerId());
        if (memberDTO != null) {
          salesOrderDTO.setMemberNo(memberDTO.getMemberNo());
          salesOrderDTO.setMemberBalance(memberDTO.getBalance());
        }
      }
      PrintWriter out = response.getWriter();
      response.setContentType("text/html");
      response.setCharacterEncoding("UTF-8");
      CustomerRecordDTO customerRecordDTO = null;
      if (null != salesOrderDTO.getCustomerId()) {
        customerRecordDTO = customerService.getCustomerRecordDTOByCustomerId(salesOrderDTO.getShopId(), salesOrderDTO.getCustomerId());
      }
      StoreHouseInventoryDTO storeHouseInventoryDTO = null;
      ProductDTO productDTO = null;
      String storageBin = null;
      boolean isHaveStoreHouseResource = BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(WebUtil.getShopVersionId(request));
      Double amountTotal = 0d;
      if (!ArrayUtil.isEmpty(salesOrderDTO.getItemDTOs())) {
        for (SalesOrderItemDTO itemDTO : salesOrderDTO.getItemDTOs()) {
          amountTotal += NumberUtil.round(itemDTO.getAmount(), 2);
          if (isHaveStoreHouseResource) {
            storeHouseInventoryDTO = storeHouseService.getStoreHouseInventoryDTO(salesOrderDTO.getStorehouseId(), itemDTO.getProductId());
            if (storeHouseInventoryDTO == null) continue;
            storageBin = storeHouseInventoryDTO.getStorageBin() == null ? "" : storeHouseInventoryDTO.getStorageBin();
            itemDTO.setStorageBin(storageBin);
          } else {
            productDTO = productService.getProductByProductLocalInfoId(itemDTO.getProductId(), itemDTO.getShopId());
            if (productDTO != null) {
              storageBin = productDTO.getStorageBin() == null ? "" : productDTO.getStorageBin();
              itemDTO.setStorageBin(storageBin);
            }
          }
        }
      }
      salesOrderDTO.setAmountTotal(amountTotal);
      if (null != printTemplateDTO) {
        byte bytes[] = printTemplateDTO.getTemplateHtml();
        String str = new String(bytes, "UTF-8");
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
        String myTemplateName = "goodsalePrint" + valueOf(WebUtil.getShopId(request));
        String myTemplate = str;
        //模板资源存放 资源库 中
        repo.putStringResource(myTemplateName, myTemplate);
        //从资源库中加载模板
        Template template = ve.getTemplate(myTemplateName);
        //取得velocity的模版
        Template t = ve.getTemplate(myTemplateName, "UTF-8");
        //取得velocity的上下文context
        VelocityContext context = new VelocityContext();
        String shortReceiptNo = null;
        if (StringUtils.isNotBlank(salesOrderDTO.getReceiptNo())) {
          shortReceiptNo = salesOrderDTO.getReceiptNo().substring(2);
        }
        salesOrderDTO.setUserName(WebUtil.getUserName(request));
        //把数据填入上下文
        context.put("salesOrderDTO", salesOrderDTO);
        context.put("storeManagerMobile", null == shopDTO.getStoreManagerMobile() ? "" : shopDTO.getStoreManagerMobile());
        context.put("customerRecordDTO", customerRecordDTO);
        context.put("shortReceiptNo", shortReceiptNo);
        if (model.get("totalReceivable") != null) {
          String totalReceivable = (String) model.get("totalReceivable");
          context.put("totalReceivable", NumberUtil.doubleValue(totalReceivable, 0d));
        } else {
          context.put("totalReceivable", 0d);
        }
        if (customerDTO != null) {
          context.put("member", membersService.getMemberByCustomerId(salesOrderDTO.getShopId(), customerDTO.getId()));
        }
        context.put("isDebug", System.getProperty("is.developer.debug"));
        //输出流
        StringWriter writer = new StringWriter();
        //转换输出
        t.merge(context, writer);
        out.print(writer);
        writer.close();
      } else {
        out.print("<html><head><title></title></head><body>没有可用的模板</body><html>");
      }
      out.close();
    } catch (Exception e) {
      LOG.debug("/sale.do");
      LOG.debug("id:" + salesOrderId);
      WebUtil.reThrow(LOG, e);
    }
  }

  @RequestMapping(params = "method=getPendingPurchaseOrderToPrint")
  public void getPendingPurchaseOrderToPrint(ModelMap model, HttpServletRequest request, HttpServletResponse response,
                                             @RequestParam("purchaseOrderId") String purchaseOrderId, String templateId) throws Exception {
    ICustomerService customerService = ServiceManager.getService(ICustomerService.class);
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
    productService = ServiceManager.getService(IProductService.class);
    IPrintService printService = ServiceManager.getService(IPrintService.class);
    try {
      Long shopId = WebUtil.getShopId(request);
      PurchaseOrderDTO purchaseOrderDTO = null;
      if (StringUtils.isNotBlank(purchaseOrderId) && StringUtils.isNumeric(purchaseOrderId)) {
        purchaseOrderDTO = getRfiTxnService().getPurchaseOrderDTOByIdAndSupplierShopId(Long.parseLong(purchaseOrderId), shopId);
      }
      if (purchaseOrderDTO != null) {
        ShopDTO shopDTO = configService.getShopById(WebUtil.getShopId(request));
        PrintTemplateDTO printTemplateDTO = null;
        if (StringUtils.isBlank(templateId) || !NumberUtil.isNumber(templateId)) {
          printTemplateDTO = printService.getSinglePrintTemplateDTOByShopIdAndType(WebUtil.getShopId(request), OrderTypes.PENDING_PURCHASE_ORDER);
        } else {
          printTemplateDTO = printService.getPrintTemplateDTOFullById(Long.parseLong(templateId));
        }
        SalesOrderDTO salesOrderDTO = getGoodSaleService().generateSaleOrderDTOFromPurchase(purchaseOrderDTO);
        salesOrderDTO.setReceiptNo(purchaseOrderDTO.getReceiptNo());
        salesOrderDTO.setVestDate(purchaseOrderDTO.getVestDate());
        salesOrderDTO.setShopLandLine(shopDTO.getLandline());
        salesOrderDTO.setRefuseMsg(purchaseOrderDTO.getRefuseMsg());
        salesOrderDTO.setShopAddress(shopDTO.getAddress());
        salesOrderDTO.setShopName(shopDTO.getName());
        //只是用于打印时显示的字符窜
        if (OrderStatus.SELLER_PENDING.equals(purchaseOrderDTO.getStatus())) {
          salesOrderDTO.setStatus(OrderStatus.PENDING);
        } else if (OrderStatus.PURCHASE_ORDER_REPEAL.equals(purchaseOrderDTO.getStatus())) {
          salesOrderDTO.setStatus(OrderStatus.STOP);
        } else if (OrderStatus.SELLER_REFUSED.equals(purchaseOrderDTO.getStatus())) {
          salesOrderDTO.setStatus(OrderStatus.REFUSED);
        }

        CustomerDTO customerDTO = getUserService().getCustomerByCustomerShopIdAndShopId(shopId, purchaseOrderDTO.getShopId());
        if (customerDTO == null) {
          customerDTO = new CustomerDTO();
          ShopDTO customerShopDTO = getConfigService().getShopById(purchaseOrderDTO.getShopId());
          customerDTO.fromCustomerShopDTO(customerShopDTO);
        }

        if (customerDTO != null && StringUtil.isEmpty(customerDTO.getMobile()) || StringUtil.isNotEmpty(customerDTO.getLandLine())) {
          salesOrderDTO.setMobile(customerDTO.getLandLine());
        }
        salesOrderDTO.setFax(customerDTO.getFax());
        salesOrderDTO.setCustomerDTO(customerDTO);
        PrintWriter out = response.getWriter();
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        CustomerRecordDTO customerRecordDTO = null;
        if (null != customerDTO.getId()) {
          customerRecordDTO = customerService.getCustomerRecordDTOByCustomerId(shopId, customerDTO.getId());
        }
        if (customerRecordDTO == null) {
          customerRecordDTO = new CustomerRecordDTO();
          customerRecordDTO.fromCustomerDTO(customerDTO);
        }
        StoreHouseInventoryDTO storeHouseInventoryDTO = null;
        ProductDTO productDTO = null;
        String storageBin = null;
        boolean isHaveStoreHouseResource = BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(WebUtil.getShopVersionId(request));
        Double amountTotal = 0d;
        if (!ArrayUtil.isEmpty(salesOrderDTO.getItemDTOs())) {
          for (SalesOrderItemDTO itemDTO : salesOrderDTO.getItemDTOs()) {
            amountTotal += NumberUtil.round(itemDTO.getAmount(), 2);
            if (isHaveStoreHouseResource) {
              storeHouseInventoryDTO = storeHouseService.getStoreHouseInventoryDTO(salesOrderDTO.getStorehouseId(), itemDTO.getProductId());
              if (storeHouseInventoryDTO == null) continue;
              storageBin = storeHouseInventoryDTO.getStorageBin() == null ? "" : storeHouseInventoryDTO.getStorageBin();
              itemDTO.setStorageBin(storageBin);
            } else {
              productDTO = productService.getProductByProductLocalInfoId(itemDTO.getProductId(), shopId);
              if (productDTO != null) {
                storageBin = productDTO.getStorageBin() == null ? "" : productDTO.getStorageBin();
                itemDTO.setStorageBin(storageBin);
              }
            }
          }
        }
        salesOrderDTO.setAmountTotal(amountTotal);
        if (null != printTemplateDTO) {
          byte bytes[] = printTemplateDTO.getTemplateHtml();
          String str = new String(bytes, "UTF-8");
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
          String myTemplateName = "goodsalePrint" + valueOf(WebUtil.getShopId(request));
          String myTemplate = str;
          //模板资源存放 资源库 中
          repo.putStringResource(myTemplateName, myTemplate);
          //从资源库中加载模板
          Template template = ve.getTemplate(myTemplateName);
          //取得velocity的模版
          Template t = ve.getTemplate(myTemplateName, "UTF-8");
          //取得velocity的上下文context
          VelocityContext context = new VelocityContext();
          String shortReceiptNo = null;
          if (StringUtils.isNotBlank(salesOrderDTO.getReceiptNo())) {
            shortReceiptNo = salesOrderDTO.getReceiptNo().substring(2);
          }
          salesOrderDTO.setUserName(WebUtil.getUserName(request));
          //把数据填入上下文
          context.put("salesOrderDTO", salesOrderDTO);
          context.put("storeManagerMobile", null == shopDTO.getStoreManagerMobile() ? "" : shopDTO.getStoreManagerMobile());
          context.put("customerRecordDTO", customerRecordDTO);
          context.put("shortReceiptNo", shortReceiptNo);
          context.put("isDebug", System.getProperty("is.developer.debug"));
          //输出流
          StringWriter writer = new StringWriter();
          //转换输出
          t.merge(context, writer);
          out.print(writer);
          writer.close();
        } else {
          out.print("<html><head><title></title></head><body>没有可用的模板</body><html>");
        }
        out.close();
      }
    } catch (Exception e) {
      LOG.debug("/sale.do");
      LOG.debug("purchaseOrderId:" + purchaseOrderId);
      WebUtil.reThrow(LOG, e);
    }
  }


  @RequestMapping(params = "method=toSalesOrder")
  public String toSalesOrder(ModelMap model, HttpServletRequest request, String salesOrderId) {
    model.addAttribute("salesOrderId", salesOrderId);
    //  if(ConfigUtils.isWholesalerVersion(WebUtil.getShopVersionId(request))){
    SalesOrderDTO salesOrderDTO = goodSaleService.getSimpleSalesOrderById(Long.parseLong(salesOrderId));
    if (salesOrderDTO != null && salesOrderDTO.getPurchaseOrderId() != null) {
      return ONLINE_SALES_ORDER;
    } else {
      return REDIRECT_SHOW;
    }
  }

  /**
   * 根据销售单Id找销售单
   *
   * @param model
   * @return
   */
  @RequestMapping(params = "method=getSalesOrder")
  public String getSalesOrder(ModelMap model, HttpServletRequest request, @RequestParam("salesOrderId") String salesOrderId) {
    LOG.info("查看销售单开始! salesOrderId:{}",salesOrderId);
    StopWatchUtil sw = new StopWatchUtil("getSalesOrder", "start");
    if (salesOrderId == null || salesOrderId.equals("null") || salesOrderId.equals("")) {
      LOG.info("销售单跳转异常，销售单id为[{}]", salesOrderId);
      return "/txn/goodsSale";
    }
    try {
      Long shopId = WebUtil.getShopId(request);
      getSalesOrderInfo(model, request, salesOrderId);
      SalesOrderDTO salesOrderDTO = (SalesOrderDTO) model.get("salesOrderDTO");
      sw.stopAndStart("step_a");
      if (BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(WebUtil.getShopVersionId(request))) {
        IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
        List<StoreHouseDTO> storeHouseDTOList = storeHouseService.getAllStoreHousesByShopId(WebUtil.getShopId(request));
        model.addAttribute("storeHouseDTOList", storeHouseDTOList);//select 选项
      }
      //结算记录
      List<ReceptionRecordDTO> receptionRecordDTOs = ServiceManager.getService(ITxnService.class).getSettledRecord(WebUtil.getShopId(request), OrderTypes.SALE, NumberUtil.toLong(salesOrderId));
      model.addAttribute("receptionRecordDTOs", receptionRecordDTOs);
      if (salesOrderDTO != null) {
        CustomerDTO customerDTO = getUserService().getCustomerDTOByCustomerId(salesOrderDTO.getCustomerId(), shopId);
        model.addAttribute("customerDTO", customerDTO);
      }
      request.setAttribute("wholesalerVersion", ConfigUtils.isWholesalerVersion(WebUtil.getShopVersionId(request)));
    } catch (Exception e) {
      LOG.debug("/sale.do?method=getSalesOrder");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.debug("salesOrderId:" + salesOrderId);
      LOG.error(e.getMessage(), e);
    }
    if (salesOrderId == null || salesOrderId.equals("null") || salesOrderId.equals("")) {
      LOG.info("销售单跳转异常，销售单id为[{}]", salesOrderId);
      return "/txn/goodsSale";
    }
    sw.stopAndPrintLog();
    return "/txn/goodsSaleFinish";
  }

  /**
   * 进入在线销售页面
   *
   * @param model
   * @return
   */
  @RequestMapping(params = "method=toOnlineSalesOrder")
  public String toOnlineSalesOrder(ModelMap model, HttpServletRequest request, String salesOrderId) {
    LOG.debug("查看在线销售单开始!");
    long begin = System.currentTimeMillis();
    long current = begin;
    if (salesOrderId == null || salesOrderId.equals("null") || salesOrderId.equals("")) {
      LOG.info("销售单跳转异常，销售单id为[{}]", salesOrderId);
      return "/txn/goodsSale";
    }
    try {
      Long shopId = WebUtil.getShopId(request);
      getSalesOrderInfo(model, request, salesOrderId);
      if (BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(WebUtil.getShopVersionId(request))) {
        IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
        List<StoreHouseDTO> storeHouseDTOList = storeHouseService.getAllStoreHousesByShopId(WebUtil.getShopId(request));
        model.addAttribute("storeHouseDTOList", storeHouseDTOList);//select 选项
      }
      SalesOrderDTO salesOrderDTO = (SalesOrderDTO) model.get("salesOrderDTO");
      CustomerDTO customerDTO = userService.getCustomerDTOByCustomerId(salesOrderDTO.getCustomerId(), shopId);
      model.addAttribute("customerDTO", customerDTO);
      //结算记录
      List<ReceptionRecordDTO> receptionRecordDTOs = ServiceManager.getService(ITxnService.class).getSettledRecord(WebUtil.getShopId(request), OrderTypes.SALE, NumberUtil.toLong(salesOrderId));
      model.addAttribute("receptionRecordDTOs", receptionRecordDTOs);
    } catch (Exception e) {
      LOG.debug("/sale.do?method=toOnlineSalesOrder,salesOrderId:" + salesOrderId + "shopId:" + request.getSession().getAttribute("shopId") +
        ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
    }
    LOG.debug("查看销售单--阶段1。执行时间: {} ms", System.currentTimeMillis() - current);
    LOG.debug("查看销售单。总执行时间: {} ms", System.currentTimeMillis() - begin);
    return "/autoaccessoryonline/ordercenter/onlineSalesOrder";
  }

  private void getSalesOrderInfo(ModelMap model, HttpServletRequest request, String salesOrderId) {
    long current = System.currentTimeMillis();
//    ITxnService txnService = ServiceManager.getService(ITxnService.class);
//    IProductService productService = ServiceManager.getService(IProductService.class);
//    IUserService userService = ServiceManager.getService(IUserService.class);
//    IConfigService configService = ServiceManager.getService(IConfigService.class);
//    RFITxnService rfiTxnService = ServiceManager.getService(RFITxnService.class);
    IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
    IPromotionsService promotionsService = ServiceManager.getService(IPromotionsService.class);
    try {
      Long shopId = (Long) request.getSession().getAttribute("shopId");
      SalesOrderDTO salesOrderDTO = txnService.getSalesOrder(NumberUtils.toLong(salesOrderId), shopId);
      if (salesOrderDTO != null && !ArrayUtil.isEmpty(salesOrderDTO.getItemDTOs())) {
        for (SalesOrderItemDTO itemDTO : salesOrderDTO.getItemDTOs()) {
          itemDTO.setPurchaseAmount(itemDTO.getAmount());
        }
      }
      CustomerDTO customerDTO = null;
      PurchaseOrderDTO purchaseOrderDTO = null;
      if (salesOrderDTO != null) {
        salesOrderDTO.setEditDateStr(DateUtil.convertDateLongToDateString("yyyy-MM-dd", salesOrderDTO.getEditDate()));          // 制单日期
        salesOrderDTO.setVestDateStr(DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN, salesOrderDTO.getVestDate()));          // 归属日期
      }
      ShopDTO shopDTO = null;
      SalesOrderItemDTO[] salesOrderItemDTOs = null;
      //得到客户信息
      if (null != salesOrderDTO) {
        salesOrderDTO.setUserId(WebUtil.getUserId(request));
        customerDTO = userService.getCustomerById(salesOrderDTO.getCustomerId());
        if (customerDTO != null) {
          List<CustomerRecordDTO> customerRecordDTOs = userService.getCustomerRecordByCustomerId(customerDTO.getId());
          if (null != customerRecordDTOs && customerRecordDTOs.size() > 0) {
            model.addAttribute("customerRecordDTO", customerRecordDTOs.get(0));
            if (customerRecordDTOs.get(0).getTotalReceivable() > 0)
              salesOrderDTO.setDebt(customerRecordDTOs.get(0).getTotalReceivable());
          }
        }

        shopDTO = configService.getShopById(new Long(shopId));
        if (null != shopDTO) {
          salesOrderDTO.setShopName(shopDTO.getName());
          salesOrderDTO.setShopAddress(shopDTO.getAddress());
          salesOrderDTO.setShopLandLine(shopDTO.getLandline());
        }

        salesOrderItemDTOs = salesOrderDTO.getItemDTOs();
        //客户的应收应付
        ServiceManager.getService(ITxnService.class).getPayableAndReceivableToModel(model, shopId, salesOrderDTO.getCustomerId());

      } else {
        salesOrderDTO = new SalesOrderDTO();
      }

      String print = (String) request.getParameter("print");

      if (StringUtils.isNotBlank(print) && "true".equals(print)) {
        salesOrderDTO.setPrint(print);
      } else {
        salesOrderDTO.setPrint("false");
      }
      LOG.debug("查看销售单--阶段2-1。执行时间: {} ms", System.currentTimeMillis() - current);
      current = System.currentTimeMillis();

      model.addAttribute("salesOrderDTO", salesOrderDTO);
      if (null == shopDTO) {
        model.addAttribute("storeManagerMobile", "");
      } else {
        model.addAttribute("storeManagerMobile", null == shopDTO.getStoreManagerMobile() ? "" : shopDTO.getStoreManagerMobile());
      }
      //ToDo: do we need this?
      Double itemTotal = 0D;
      Set<Long> productIds = salesOrderDTO.getProductIdSet();
      Map<Long, InventoryDTO> inventoryDTOMap = getInventoryService().getInventoryDTOMap(shopId, productIds);
      Map<Long, ProductDTO> productDTOMap = getProductService().getProductDTOMapByProductLocalInfoIds(shopId, productIds);
      Map<Long, ProductHistoryDTO> productHistoryDTOMap = ServiceManager.getService(IProductHistoryService.class)
        .getProductHistoryDTOMapByProductHistoryIds(salesOrderDTO.getProductHistoryIds());
      Map<Long, StoreHouseInventoryDTO> storeHouseInventoryDTOMap = new HashMap<Long, StoreHouseInventoryDTO>();
      if (BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(WebUtil.getShopVersionId(request))) {
        storeHouseInventoryDTOMap = storeHouseService.getStoreHouseInventoryDTOMapByStorehouseAndProductIds(shopId,
          salesOrderDTO.getStorehouseId(), productIds.toArray(new Long[productIds.size()]));
      }
      if (null != salesOrderItemDTOs && salesOrderItemDTOs.length > 0) {
        Double amountTotal = 0d;
        for (SalesOrderItemDTO salesOrderItemDTO : salesOrderItemDTOs) {
          InventoryDTO inventoryDTO = inventoryDTOMap.get(salesOrderItemDTO.getProductId());
          ProductDTO productDTO = productDTOMap.get(salesOrderItemDTO.getProductId());

          //如果采购单位是库存大单位
          if (UnitUtil.isStorageUnit(salesOrderItemDTO.getUnit(), productDTO)) {
            salesOrderItemDTO.setInventoryAmount(inventoryDTO.getAmount() / productDTO.getRate());
            salesOrderItemDTO.setPurchasePrice(productDTO.getPurchasePrice() == null ? 0d : productDTO.getPurchasePrice() * productDTO.getRate());
          } else {
            salesOrderItemDTO.setInventoryAmount(inventoryDTO.getAmount());
            salesOrderItemDTO.setPurchasePrice(productDTO.getPurchasePrice() == null ? 0 : productDTO.getPurchasePrice());
          }
          itemTotal += NumberUtil.doubleVal(salesOrderItemDTO.getTotal());

          ProductHistoryDTO productHistoryDTO = productHistoryDTOMap.get(salesOrderItemDTO.getProductHistoryId());
          if (productHistoryDTO != null) {
            salesOrderItemDTO.setProductHistoryDTO(productHistoryDTO);
            if (OrderUtil.salesOrderInProgress.contains(salesOrderDTO.getStatus())) {
              salesOrderItemDTO.setProductUnitRateInfo(productDTO);
            }
          } else {
            salesOrderItemDTO.setProductDTOWithOutUnit(productDTO);
          }
          Double shortageAmount = NumberUtil.subtraction(salesOrderItemDTO.getAmount(), salesOrderItemDTO.getReserved(), inventoryDTO.getAmount());
          if (shortageAmount > 0) {
            salesOrderItemDTO.setShortage(shortageAmount);
          }
          Set<Long> ids = salesOrderItemDTO.getPromotionsIds();
          if (CollectionUtil.isNotEmpty(ids)) {
            Long[] promotionsIds = ids.toArray(new Long[ids.size()]);
            salesOrderItemDTO.setPromotionsDTOs(promotionsService.getPromotionsDTODetailById(shopId, promotionsIds));
          }
          amountTotal += salesOrderItemDTO.getAmount();
        }
        salesOrderDTO.setAmountTotal(NumberUtil.round(amountTotal, 2));
      }
      LOG.debug("查看销售单--阶段2-2。执行时间: {} ms", System.currentTimeMillis() - current);
      current = System.currentTimeMillis();

      salesOrderDTO.setItemTotal(NumberUtil.round(itemTotal, NumberUtil.MONEY_PRECISION));
      //得到收款单信息
      IMembersService membersService = ServiceManager.getService(IMembersService.class);
      ReceivableDTO receivableDTO = txnService.getReceivableByShopIdAndOrderTypeAndOrderId(shopId, OrderTypes.REPAIR,
        NumberUtils.toLong(salesOrderId));
      if (null != receivableDTO) {
        salesOrderDTO.setReceivableId(receivableDTO.getId());
        salesOrderDTO.setSettledAmount(receivableDTO.getSettledAmount());
        salesOrderDTO.setSettledAmountHid(receivableDTO.getSettledAmount());
        salesOrderDTO.setDebt(receivableDTO.getDebt());
        salesOrderDTO.setCashAmount(receivableDTO.getCash());
        salesOrderDTO.setBankAmount(receivableDTO.getBankCard());
        salesOrderDTO.setMemberAmount(receivableDTO.getMemberBalancePay());
        salesOrderDTO.setBankCheckAmount(receivableDTO.getCheque());
        salesOrderDTO.setOrderDiscount(receivableDTO.getDiscount());
        salesOrderDTO.setStrikeAmount(NumberUtil.doubleVal(receivableDTO.getStrike()));
        salesOrderDTO.setPayee(receivableDTO.getLastPayee());
        salesOrderDTO.setStatementAmount(NumberUtil.doubleVal(receivableDTO.getStatementAmount()));
        salesOrderDTO.setHuankuanTime(receivableDTO.getRemindTime() == null ? null : DateUtil.convertDateLongToDateString(DateUtil.YEAR_MONTH_DATE, receivableDTO.getRemindTime()));
        if (null != receivableDTO.getMemberDiscountRatio()) {
          salesOrderDTO.setMemberDiscountRatio(NumberUtil.round(receivableDTO.getMemberDiscountRatio() * 10, 1));
        }
        salesOrderDTO.setAfterMemberDiscountTotal(receivableDTO.getAfterMemberDiscountTotal());
        ReceptionRecordDTO[] receptionRecordDTOs = receivableDTO.getRecordDTOs();
        if (receptionRecordDTOs != null && receptionRecordDTOs.length > 0) {
          salesOrderDTO.setBankCheckNo("");
          for (ReceptionRecordDTO receptionRecordDTO : receptionRecordDTOs) {
            if (StringUtil.isNotEmpty(receptionRecordDTO.getChequeNo())) {
              salesOrderDTO.setBankCheckNo(salesOrderDTO.getBankCheckNo() + " " + receptionRecordDTO.getChequeNo());
            }
          }
        }
        if (receivableDTO.getMemberId() != null) {
          Member member = membersService.getMemberById(receivableDTO.getMemberId());
          if (member != null) {
            salesOrderDTO.setAccountMemberNo(member.getMemberNo());
          }
        }
      }

      LOG.debug("查看销售单--阶段2-3。执行时间: {} ms", System.currentTimeMillis() - current);
      current = System.currentTimeMillis();
      if (salesOrderDTO != null && salesOrderDTO.getPurchaseOrderId() != null
        && customerDTO != null && customerDTO.getCustomerShopId() != null) {
        purchaseOrderDTO = getTxnService().getPurchaseOrder(salesOrderDTO.getPurchaseOrderId(), customerDTO.getCustomerShopId());
        if (purchaseOrderDTO != null && purchaseOrderDTO.getDeliveryDate() != null) {
          purchaseOrderDTO.setDeliveryDateStr(DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DAY, purchaseOrderDTO.getDeliveryDate()));
          salesOrderDTO.setPromotionsInfoJson(purchaseOrderDTO.getPromotionsInfoJson());
        }
        model.addAttribute("purchaseOrderDTO", purchaseOrderDTO);
      }
      if (salesOrderDTO.getExpressId() != null) {
        ExpressDTO expressDTO = rfiTxnService.getExpressDTOById(salesOrderDTO.getExpressId());
        salesOrderDTO.setExpressDTO(expressDTO);
      }
      if (OrderStatus.PENDING.equals(salesOrderDTO.getStatus())) {
        salesOrderDTO.setGoodsSaler(WebUtil.getUserName(request));
        salesOrderDTO.setVestDate(DateUtil.getTheDayTime());
        salesOrderDTO.setVestDateStr(DateUtil.convertDateLongToString(System.currentTimeMillis(), DateUtil.DATE_STRING_FORMAT_DEFAULT));
      }
      if (BcgogoShopLogicResourceUtils.isThroughSelectSupplier(WebUtil.getShopVersionId(request)) &&
        OrderStatus.STOCKING.equals(salesOrderDTO.getStatus()) && !ArrayUtil.isEmpty(salesOrderDTO.getItemDTOs())) {
        Map<Long, List<OutStorageRelationDTO>> relationMap = ServiceManager.getService(IProductThroughService.class).getOutStorageRelationMap(shopId, salesOrderDTO.getId());
        if (relationMap != null && !relationMap.isEmpty()) {
          for (SalesOrderItemDTO itemDTO : salesOrderDTO.getItemDTOs()) {
            List<OutStorageRelationDTO> relationDTOs = relationMap.get(itemDTO.getId());
            Double amount = 0d;
            if (CollectionUtil.isNotEmpty(relationDTOs)) {
              for (OutStorageRelationDTO relationDTO : relationDTOs) {
                amount += NumberUtil.doubleVal(relationDTO.getSupplierRelatedAmount());
                itemDTO.setAmount(relationDTO.getOutStorageItemAmount());
              }
            }
          }
        }
      }

      Double otherIncomeTotal = 0D;
      if (CollectionUtils.isNotEmpty(salesOrderDTO.getOtherIncomeItemDTOList())) {
        for (SalesOrderOtherIncomeItemDTO itemDTO : salesOrderDTO.getOtherIncomeItemDTOList()) {
          if (StringUtils.isBlank(itemDTO.getName())) {
            continue;
          }

          otherIncomeTotal += (null == itemDTO.getPrice()) ? 0D : itemDTO.getPrice();
        }
      }
      salesOrderDTO.setOtherIncomeTotal(NumberUtil.round(otherIncomeTotal, NumberUtil.MONEY_PRECISION));
      model.addAttribute("salesOrderDTO", salesOrderDTO);
      model.addAttribute("afterMemberDeduction", NumberUtil.round(NumberUtil.round(salesOrderDTO.getAfterMemberDiscountTotal()) - salesOrderDTO.getSettledAmount() - salesOrderDTO.getDebt()));
      model.addAttribute("receiveNo", ServiceManager.getService(ITxnService.class).getStatementAccountOrderNo(shopId, salesOrderDTO.getStatementAccountOrderId()));
      LOG.debug("查看销售单--阶段2-4。执行时间: {} ms", System.currentTimeMillis() - current);
      current = System.currentTimeMillis();
    } catch (Exception e) {
      LOG.debug("/sale.do");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.debug("salesOrderId:" + salesOrderId);
      LOG.error(e.getMessage(), e);
    }
  }


  /**
   * 供应商进入待处理的销售单详细页面，也就是对方的采购单
   *
   * @param model
   * @return
   */
  @RequestMapping(params = "method=toOnlinePendingPurchaseOrder")
  private String toOnlinePendingPurchaseOrder(ModelMap model, HttpServletRequest request, String purchaseOrderId) {
    Long shopId = WebUtil.getShopId(request);
    try {
      Long orderId = null;
      if (StringUtils.isBlank(purchaseOrderId) || !StringUtils.isNumeric(purchaseOrderId)) {
        LOG.error("采购跳转异常，销售单id为[{}]", purchaseOrderId);
      } else {
        orderId = Long.parseLong(purchaseOrderId);
      }
      PurchaseOrderDTO purchaseOrderDTO = getRfiTxnService().getPurchaseOrderDTOByIdAndSupplierShopId(orderId, shopId);
      SalesOrderDTO salesOrderDTO = null;
      CustomerDTO customerDTO = null;
      CustomerRecordDTO customerRecordDTO = null;
      //采购单为待处理状态时
      if (purchaseOrderDTO != null && (OrderStatus.SELLER_PENDING.equals(purchaseOrderDTO.getStatus()) || OrderStatus.SELLER_REFUSED.equals(purchaseOrderDTO.getStatus()) || OrderStatus.PURCHASE_ORDER_REPEAL.equals(purchaseOrderDTO.getStatus()))) {
        salesOrderDTO = getGoodSaleService().generateSaleOrderDTOFromPurchase(purchaseOrderDTO);
//        salesOrderDTO.setPurchaseOrderDTO(purchaseOrderDTO);
        customerDTO = getUserService().getCustomerByCustomerShopIdAndShopId(shopId, purchaseOrderDTO.getShopId());
        if (customerDTO == null) {
          customerDTO = new CustomerDTO();
          ShopDTO customerShopDTO = getConfigService().getShopById(purchaseOrderDTO.getShopId());
          customerDTO.fromCustomerShopDTO(customerShopDTO);
        } else {
          List<CustomerRecordDTO> customerRecordDTOs = getUserService().getCustomerRecordByCustomerId(customerDTO.getId());
          if (null != customerRecordDTOs && customerRecordDTOs.size() > 0) {
            customerRecordDTO = customerRecordDTOs.get(0);
          }
        }
        OrderStatus salesStatus = convertPurchaseStatusToSaleStatus(purchaseOrderDTO.getStatus());
        salesOrderDTO.setStatus(salesStatus);
        //采购单已经处理时
      } else if (purchaseOrderDTO != null && !OrderStatus.SELLER_PENDING.equals(purchaseOrderDTO.getStatus())) {
        salesOrderDTO = getGoodSaleService().getSimpleSalesOrderByPurchaseOrderId(purchaseOrderDTO.getId());
        if (salesOrderDTO != null && salesOrderDTO.getId() != null) {
          model.addAttribute("salesOrderId", salesOrderDTO.getId());
          return ONLINE_SALES_ORDER;
        }
      }
      if (salesOrderDTO == null) {
        salesOrderDTO = new SalesOrderDTO();
      }
      if (purchaseOrderDTO == null) {
        purchaseOrderDTO = new PurchaseOrderDTO();
      }
      if (customerDTO == null) {
        customerDTO = new CustomerDTO();
      }
      if (customerRecordDTO == null) {
        customerRecordDTO = new CustomerRecordDTO();
      }
      if (OrderStatus.PURCHASE_ORDER_REPEAL.equals(salesOrderDTO.getStatus())) {
        salesOrderDTO.setStatus(OrderStatus.STOP);
      }
      model.addAttribute("salesOrderDTO", salesOrderDTO);
      model.addAttribute("purchaseOrderDTO", purchaseOrderDTO);
      model.addAttribute("customerDTO", customerDTO);
      model.addAttribute("customerRecordDTO", customerRecordDTO);


      if (BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(WebUtil.getShopVersionId(request))) {
        IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
        List<StoreHouseDTO> storeHouseDTOList = storeHouseService.getAllStoreHousesByShopId(WebUtil.getShopId(request));
        model.addAttribute("storeHouseDTOList", storeHouseDTOList);//select 选项
      }

    } catch (Exception e) {
      LOG.error("/sale.do?method=toOnlinePendingPurchaseOrder,purchaseOrderId:{}" + e.getMessage(), purchaseOrderId, e);
    }
    return SHOW_PENDING_PURCHASE;
  }

  private OrderStatus convertPurchaseStatusToSaleStatus(OrderStatus status) {
    switch (status) {
      case SELLER_PENDING:
        return OrderStatus.PENDING;
      case SELLER_STOCK:
        return OrderStatus.STOCKING;
      case SELLER_DISPATCH:
        return OrderStatus.DISPATCH;
      case SELLER_REFUSED:
        return OrderStatus.REFUSED;
      default:
        return status;
    }
  }

  @RequestMapping(params = "method=getProducts")
  public String getProducts(ModelMap model, HttpServletRequest request, HttpServletResponse response) throws Exception {
    RFITxnService rfiTxnService = ServiceManager.getService(RFITxnService.class);
    IUserService userService = ServiceManager.getService(IUserService.class);
    String productIds1 = request.getParameter("productIds");
    String[] productIds = null;
    Set<Long> productIdSet = new HashSet<Long>();
    if (productIds1 != null) {
      productIds = productIds1.split(",");
    }
    StringBuilder stringBuilder = new StringBuilder();
    if (productIds != null && productIds.length > 0) {
      for (int arrayLength = 0; arrayLength < productIds.length; arrayLength++) {
        if (NumberUtil.isNumber(productIds[arrayLength])) {
          stringBuilder.append(productIds[arrayLength]).append(",");
        }
      }
      productIds = stringBuilder.toString().split(",");
    }

    String customerId = request.getParameter("customerId");
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    SalesOrderDTO salesOrderDTO = null;
    if (!model.containsKey("salesOrderDTO")) {
      salesOrderDTO = new SalesOrderDTO();
      long curTime = System.currentTimeMillis();
      String time = DateUtil.convertDateLongToDateString("yyyy-MM-dd", curTime);
      salesOrderDTO.setEditDateStr(time);
      salesOrderDTO.setEditDate(curTime);
      salesOrderDTO.setVestDateStr(time);
      salesOrderDTO.setVestDate(curTime);
      salesOrderDTO.setShopId((Long) request.getSession().getAttribute("shopId"));
      salesOrderDTO.setEditor((String) request.getSession().getAttribute("userName"));
      salesOrderDTO.setEditorId((Long) request.getSession().getAttribute("userId"));
      salesOrderDTO.setGoodsSaler((String) request.getSession().getAttribute("userName"));
      salesOrderDTO.setGoodsSalerId((Long) request.getSession().getAttribute("userId"));
    } else {
      salesOrderDTO = (SalesOrderDTO) model.get("salesOrderDTO");
    }

    if ("noId".equals(request.getParameter("cancle"))) {
      salesOrderDTO.setReceiptNo(request.getParameter("receiptNo"));
    }

    try {
      IProductService productService = ServiceManager.getService(IProductService.class);
      if (null != productIds) {
        for (int i = 0; i < productIds.length; i++) {
          if (com.bcgogo.utils.NumberUtil.isNumber(productIds[i]) && StringUtils.isNotEmpty(productIds[i])) {
            productIdSet.add(Long.parseLong(productIds[i]));
          }
        }
      }
      Map<Long, ProductDTO> productDTOMap = productService.getProductDTOMapByProductLocalInfoIds(shopId, productIdSet);
      Map<Long, InventoryDTO> inventoryDTOMap = getInventoryService().getInventoryDTOMap(shopId, productIdSet);

      Map<Long, CategoryDTO> categoryDTOMap = new HashMap<Long, CategoryDTO>();
      Set<Long> businessCategoryIds = new HashSet<Long>();
      if (MapUtils.isNotEmpty(productDTOMap)) {
        for (ProductDTO productDTO : productDTOMap.values()) {
          if (productDTO != null && productDTO.getBusinessCategoryId() != null) {
            businessCategoryIds.add(productDTO.getBusinessCategoryId());
          }
        }
      }
      categoryDTOMap = rfiTxnService.getCategoryDTOMapById(shopId, businessCategoryIds);
      if (null != productIds) {
        SalesOrderItemDTO[] itemDTOs = new SalesOrderItemDTO[productIds.length];
        salesOrderDTO.setItemDTOs(itemDTOs);
        double salesOrderTotal = 0d;
        for (int i = 0; i < productIds.length; i++) {
          if (!com.bcgogo.utils.NumberUtil.isNumber(productIds[i])) {
            continue;
          }
          ProductDTO productDTO = productDTOMap.get(new Long(productIds[i]));
          if (itemDTOs[i] == null) {
            itemDTOs[i] = new SalesOrderItemDTO();
          }
          Long[] productLocalInfoId = new Long[1];
          productLocalInfoId[0] = new Long(productIds[i]);
          InventoryDTO inventoryDTO = inventoryDTOMap.get(productLocalInfoId[0]);
          SalesOrderItemDTO salesOrderItemDTO = itemDTOs[i];

          if (productDTO != null && productDTO.getId() != null) {

            salesOrderItemDTO.setVehicleBrand(productDTO.getProductVehicleBrand());
            salesOrderItemDTO.setVehicleModel(productDTO.getProductVehicleModel());
            salesOrderItemDTO.setVehicleYear(productDTO.getProductVehicleYear());
            salesOrderItemDTO.setVehicleEngine(productDTO.getProductVehicleEngine());
            salesOrderItemDTO.setBrand(productDTO.getBrand());
            salesOrderItemDTO.setModel(productDTO.getModel());
            salesOrderItemDTO.setSpec(productDTO.getSpec());
            salesOrderItemDTO.setProductVehicleStatus(valueOf(productDTO.getProductVehicleStatus()));

            salesOrderItemDTO.setProductName(productDTO.getName());
            itemDTOs[i].setBrand(productDTO.getBrand());
            itemDTOs[i].setModel(productDTO.getModel());
            itemDTOs[i].setSpec(productDTO.getSpec());
            itemDTOs[i].setProductName(productDTO.getName());
            itemDTOs[i].setProductId(new Long(productIds[i]));
            itemDTOs[i].setStorageBin(productDTO.getStorageBin());
            itemDTOs[i].setCommodityCode(productDTO.getCommodityCode());
            if (null != productDTO.getBusinessCategoryId()) {
              CategoryDTO categoryDTO = categoryDTOMap.get(productDTO.getBusinessCategoryId());
              if (null != categoryDTO) {
                salesOrderItemDTO.setBusinessCategoryId(categoryDTO.getId());
                salesOrderItemDTO.setBusinessCategoryName(categoryDTO.getCategoryName());
              }
            }


            if (inventoryDTO != null) {    //设定销售价带入商品销售单
              double recommendedPrice = NumberUtil.round(inventoryDTO.getSalesPrice(), 2);
              itemDTOs[i].setPrice(recommendedPrice);
              salesOrderItemDTO.setPrice(recommendedPrice);
              double amount = NumberUtil.round(inventoryDTO.getAmount(), 2);
              itemDTOs[i].setInventoryAmount(amount);
              double pp = NumberUtil.round(inventoryDTO.getLatestInventoryPrice(), 2);
              itemDTOs[i].setPurchasePrice(pp);
              itemDTOs[i].setAmount(RfTxnConstant.ORDER_DEFAULT_AMOUNT);
              double itemTotal = NumberUtil.round(itemDTOs[i].getAmount() * itemDTOs[i].getPrice(), 2);
              itemDTOs[i].setTotal(itemTotal);
              salesOrderTotal += itemDTOs[i].getTotal();
            }
            itemDTOs[i].setUnitAndRate(productDTO);
          }
          salesOrderTotal = NumberUtil.round(salesOrderTotal, NumberUtil.MONEY_PRECISION);
          salesOrderDTO.setTotal(salesOrderTotal);
          salesOrderDTO.setSettledAmount(salesOrderTotal);
        }
      }
      if (customerId == null || "null".equals(customerId)) {
        if (request.getParameter("customerName") != null && !"".equals(request.getParameter("customerName"))) {
          String customerName = request.getParameter("customerName");
          CustomerDTO customerDTO = new CustomerDTO();
          customerDTO.setName(customerName);
          customerDTO.setShopId(shopId);
          customerDTO = userService.createCustomer(customerDTO);
          customerId = customerDTO.getId().toString();
        }
      }
      //得到客户和车辆信息
      if (StringUtils.isNotEmpty(customerId)) {
        CustomerDTO customerDTO = userService.getCustomerById(Long.valueOf(customerId));
        if (customerDTO != null) {
          salesOrderDTO.setCustomerDTO(customerDTO);
          List<CustomerRecordDTO> customerRecordDTOs = userService.getCustomerRecordByCustomerId(customerDTO.getId());
          if (null != customerRecordDTOs && customerRecordDTOs.size() > 0) {
            model.addAttribute("customerRecordDTO", customerRecordDTOs.get(0));
          }
          model.addAttribute("isAdd", false);
        }
        //得到车辆
        List<CustomerVehicleDTO> customerVehicleDTOs = userService.getVehicleByCustomerId(customerDTO.getId());
        if (customerVehicleDTOs != null && customerVehicleDTOs.size() > 0) {
          VehicleDTO vehicleDTO = userService.getVehicleById(customerVehicleDTOs.get(0).getVehicleId());
          //salesOrderDTO.setLicenceNo(vehicleDTO.getLicenceNo());
          salesOrderDTO.setBrand(vehicleDTO.getBrand());
          salesOrderDTO.setModel(vehicleDTO.getModel());
          salesOrderDTO.setYear(vehicleDTO.getYear());
          salesOrderDTO.setEngine(vehicleDTO.getEngine());
        }
        //应收应付款
        ServiceManager.getService(ITxnService.class).getPayableAndReceivableToModel(model, shopId, Long.valueOf(customerId));
      }
      if (BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(WebUtil.getShopVersionId(request))) {
        IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
        List<StoreHouseDTO> storeHouseDTOList = storeHouseService.getAllStoreHousesByShopId(shopId);
        model.addAttribute("storeHouseDTOList", storeHouseDTOList);//select 选项
        if (CollectionUtils.isNotEmpty(storeHouseDTOList) && salesOrderDTO.getStorehouseId() == null) {
          if (storeHouseDTOList.size() == 1) {
            salesOrderDTO.setStorehouseId(storeHouseDTOList.get(0).getId());
          }
        }
        InventoryService inventoryService = ServiceManager.getService(InventoryService.class);
        //更新库存 根据仓库
        inventoryService.updateItemDTOInventoryAmountByStorehouse(shopId, salesOrderDTO.getStorehouseId(), salesOrderDTO);
      }
      salesOrderDTO.setVestDateStr(DateUtil.dateLongToStr(salesOrderDTO.getVestDate(), DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN));
      salesOrderDTO.initDefaultItemUnit(ProductUnitCache.getProductUnitMap());
      model.addAttribute("salesOrderDTO", salesOrderDTO);

      request.setAttribute("wholesalerVersion", ConfigUtils.isWholesalerVersion(WebUtil.getShopVersionId(request)));
    } catch (Exception e) {
      LOG.debug("/sale.do");
      LOG.debug("method=getProducts");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
    }

    return "/txn/goodsSale";
  }

  /**
   * 线下销售单结算
   *
   * @param model
   * @param salesOrderDTO
   * @param request
   * @param response
   * @param huankuanTime
   * @return
   * @throws Exception
   */
  @RequestMapping(params = "method=saveSale")
  public String saveSale(ModelMap model, SalesOrderDTO salesOrderDTO, HttpServletRequest request, HttpServletResponse response,
                         String huankuanTime) throws Exception {
    Long shopId = WebUtil.getShopId(request);
    LOG.info("保存销售单开始!,shopId:{}",shopId);
    StopWatchUtil sw = new StopWatchUtil("saveSale", "start");
    if (checkSalesOrderDTOEmpty(salesOrderDTO)) {
      LOG.warn("开始执行销售时，数据异常，销售信息：salesOrderDTO 为空！");
      return getProducts(model, request, response);
    }
    String contact = request.getParameter("contact");
    Long userId = WebUtil.getUserId(request);
    salesOrderDTO.setUserId(userId);
    String username = WebUtil.getUserName(request);
    salesOrderDTO.setUserName(username);
    salesOrderDTO.setShopName(WebUtil.getShopName(request));
    salesOrderDTO.setShopVersionId(WebUtil.getShopVersionId(request));
    IMemberCheckerService memberCheckerService = ServiceManager.getService(IMemberCheckerService.class);
    salesOrderDTO.setShopId(shopId);

    salesOrderDTO.setStatus(OrderStatus.SALE_DONE);
    salesOrderDTO.setInventoryLimitDTO(new InventoryLimitDTO());
    salesOrderDTO.getInventoryLimitDTO().setShopId(salesOrderDTO.getShopId());

    if (salesOrderDTO.getSettledAmount() == null) {
      salesOrderDTO.setSettledAmount(0.0);
    }
    LOG.debug("salesOrder:{}", salesOrderDTO);
    try {
      sw.stopAndStart("step1");
      //组装销售单的静态信息
      prepareSalesOrder(salesOrderDTO, request, huankuanTime);
      //单据号
      if (StringUtils.isBlank(salesOrderDTO.getReceiptNo())) {
        salesOrderDTO.setReceiptNo(txnService.getReceiptNo(salesOrderDTO.getShopId(), OrderTypes.SALE, null));
      }
      //处理营业分类
      getGoodSaleService().batchSaveCategory(salesOrderDTO);
      //处理其他分类
      getTxnService().batchSaveOrUpdateOtherIncomeKind(salesOrderDTO.getShopId(), salesOrderDTO.getOtherIncomeNames());
      sw.stopAndStart("step_w");
      //销售单 所填车辆若为新车型，则新增，并将ID保存到此销售单
      getGoodSaleService().populateSalesOrderDTO(salesOrderDTO);
      //更新客户信息 客户供应商打通信息
      customerService.updateVehicleAndCustomer(salesOrderDTO, shopId, contact, salesOrderDTO.getPaymentTime());
      sw.stopAndStart("step_b");
      //更新product 信息
      getGoodSaleService().saveOrUpdateProductForSaleOrder(salesOrderDTO);
      sw.stopAndStart("step_p");
      //保存销售单
      salesOrderDTO = getGoodSaleService().createOrUpdateSalesOrder(salesOrderDTO, huankuanTime);

      sw.stopAndStart("step_u");
      //ad by WLF 保存销售单的结算日志
      ServiceManager.getService(ITxnService.class).saveOperationLogTxnService(
        new OperationLogDTO(shopId, WebUtil.getUserId(request), salesOrderDTO.getId(), ObjectTypes.SALE_ORDER, OperationTypes.CREATE));

      //结算后 草稿单作废
      if (StringUtil.isNotEmpty(salesOrderDTO.getDraftOrderIdStr())) {
        ServiceManager.getService(IDraftOrderService.class).deleteDraftOrder(salesOrderDTO.getShopId(), NumberUtil.longValue(salesOrderDTO.getDraftOrderIdStr()));
      }
      sw.stopAndStart("step_u");
      //营业统计 常用产品
      BcgogoEventPublisher bcgogoEventPublisher = new BcgogoEventPublisher();
      SaleOrderSavedEvent saleOrderSavedEvent = new SaleOrderSavedEvent(salesOrderDTO);
      String isRunThread = request.getParameter("isRunThread");
      if (!"noRun".equals(isRunThread)) {
        bcgogoEventPublisher.publisherSaleOrderSaved(saleOrderSavedEvent);
      }
      saleOrderSavedEvent.setMainFlag(true);
      request.setAttribute("UNIT_TEST", saleOrderSavedEvent);

      //会员结算信息
      memberCheckerService.updateMemberInfo(salesOrderDTO);
      //发送微信账单到车主
      ServiceManager.getService(WXTxnService.class).sendConsumeMsg(salesOrderDTO);

//      //短信逻辑
//      String time = salesOrderDTO.getVestDateStr();
//      ShopDTO shopDTO = configService.getShopById(shopId);
//      //如果总计大于实收和欠款和的话，就代表打折了，要发送折扣短信给店老板
//      if (NumberUtil.doubleVal(salesOrderDTO.getTotal()) > NumberUtil.doubleVal(salesOrderDTO.getSettledAmount())
//          + NumberUtil.doubleVal(salesOrderDTO.getDebt())) {
//        getSmsService().sendSalesOrderCustomerCheapMsgToBoss(salesOrderDTO, shopId, shopDTO, time);
//      }
//      //如果有欠款就要发送欠款备忘给店老板
//      if (NumberUtil.doubleVal(salesOrderDTO.getDebt()) > 0) {
//        getSmsService().sendSalesOrderCustomerDebtMsgToBoss(salesOrderDTO, shopId, shopDTO, time, salesOrderDTO.getPaymentTime());
//      }
      sw.stopAndStart("step_r");
      //更新memcacheLimitInfo
      getInventoryService().updateMemocacheLimitByInventoryLimitDTO(shopId, salesOrderDTO.getInventoryLimitDTO());
      //每新增一张单据，就要将同一个客户里面的欠款提醒的状态改为未提醒
      ServiceManager.getService(ITxnService.class).updateRemindEventStatus(salesOrderDTO.getShopId(), salesOrderDTO.getCustomerId(), "customer");
      sw.stopAndPrintLog();
    } catch (Exception e) {
      LOG.debug("/sale.do");
      LOG.debug("method=saveSale");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.debug("contact:" + contact + ",huankuanTime:" + huankuanTime);
      LOG.debug(salesOrderDTO.toString());
      LOG.error(e.getMessage(), e);
    }
    if ("true".equals(salesOrderDTO.getPrint())) {
      request.setAttribute("print", "true");
    }
    model.addAttribute("salesOrderId", salesOrderDTO.getId());

    return REDIRECT_SHOW + "&print=" + salesOrderDTO.getPrint();
  }

  //组装销售单的静态信息
  private void prepareSalesOrder(SalesOrderDTO salesOrderDTO, HttpServletRequest request, String huankuanTime) throws Exception {
    salesOrderDTO.setUserId(WebUtil.getUserId(request));
    String username = WebUtil.getUserName(request);
    salesOrderDTO.setUserName(username);
    salesOrderDTO.setShopVersionId(WebUtil.getShopVersionId(request));
    salesOrderDTO.setShopId(WebUtil.getShopId(request));
    salesOrderDTO.setStatus(OrderStatus.SALE_DONE);
    salesOrderDTO.setInventoryLimitDTO(new InventoryLimitDTO());
    salesOrderDTO.getInventoryLimitDTO().setShopId(salesOrderDTO.getShopId());
    if (salesOrderDTO.getSettledAmount() == null) {
      salesOrderDTO.setSettledAmount(0.0);
    }
    //      解决错误的字符的问题   spring:form 的问题
    if (salesOrderDTO.getMobile() != null) {
      if (salesOrderDTO.getMobile().length() == 0)
        salesOrderDTO.setMobile(null);
      else if (salesOrderDTO.getMobile().length() == 1 && 0 == (int) salesOrderDTO.getMobile().charAt(0))
        salesOrderDTO.setMobile(null);
    }

    if (null == salesOrderDTO.getAfterMemberDiscountTotal()) {
      salesOrderDTO.setAfterMemberDiscountTotal(salesOrderDTO.getTotal());
    }
    salesOrderDTO.setEditDate(DateUtil.convertDateStringToDateLong("yyyy-MM-dd", salesOrderDTO.getEditDateStr()));       //制单时间
    salesOrderDTO.setVestDate(DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN, salesOrderDTO.getVestDateStr()));       //归属时间
    if (salesOrderDTO.getVestDate() == null) {
      salesOrderDTO.setVestDate(System.currentTimeMillis());
    }
    //设置还款时间 ,还款时间要大于归属时间
    if (salesOrderDTO.getDebt() > 0.001) {
      if (StringUtils.isNotEmpty(huankuanTime) && !"null".equals(huankuanTime)) {
        Long payTime = DateUtil.convertDateStringToDateLong("yyyy-MM-dd", huankuanTime);
        if (payTime > salesOrderDTO.getVestDate()) {
          salesOrderDTO.setPaymentTime(payTime);
        }
      } else {
        salesOrderDTO.setPaymentTime(0L);
      }
    } else {
      salesOrderDTO.setPaymentTime(null);
    }
    //材料栏去空行
    boolean isIgnoreVerifierInventoryResource = BcgogoShopLogicResourceUtils.isIgnoreVerifierInventoryResource(WebUtil.getShopVersionId(request));
    List<SalesOrderItemDTO> salesOrderItemDTOs = new ArrayList<SalesOrderItemDTO>();
    for (SalesOrderItemDTO salesOrderItemDTO : salesOrderDTO.getItemDTOs()) {
      if (null == salesOrderItemDTO || StringUtils.isBlank(salesOrderItemDTO.getProductName())) {
        continue;
      }
      if (!isIgnoreVerifierInventoryResource && salesOrderItemDTO.getProductId() == null) {
        continue;
      }
      if (salesOrderItemDTO.getBusinessCategoryName() != null) {
        salesOrderItemDTO.setBusinessCategoryName(salesOrderItemDTO.getBusinessCategoryName().trim());
      }
      salesOrderItemDTOs.add(salesOrderItemDTO);
    }
    salesOrderDTO.setItemDTOs(salesOrderItemDTOs.toArray(new SalesOrderItemDTO[salesOrderItemDTOs.size()]));

    //其他费用去空行
    Double otherIncomeTotal = 0D;
    List<SalesOrderOtherIncomeItemDTO> otherIncomeItemDTOList = new ArrayList<SalesOrderOtherIncomeItemDTO>();
    if (null != salesOrderDTO && CollectionUtils.isNotEmpty(salesOrderDTO.getOtherIncomeItemDTOList())) {
      for (SalesOrderOtherIncomeItemDTO orderOtherIncomeItemDTO : salesOrderDTO.getOtherIncomeItemDTOList()) {
        if (StringUtils.isBlank(orderOtherIncomeItemDTO.getName())) {
          continue;
        }
        otherIncomeTotal += NumberUtil.doubleVal(orderOtherIncomeItemDTO.getPrice());
        otherIncomeItemDTOList.add(orderOtherIncomeItemDTO);
      }
    }
    salesOrderDTO.setOtherIncomeTotal(NumberUtil.toReserve(otherIncomeTotal, NumberUtil.MONEY_PRECISION));
    salesOrderDTO.setOtherIncomeItemDTOList(otherIncomeItemDTOList);

  }

  private boolean checkSalesOrderDTOEmpty(SalesOrderDTO salesOrderDTO) {
    boolean isEmpty = true;
    if (salesOrderDTO == null) {
      return isEmpty;
    }
    // 如果归属时间为空
    if (StringUtils.isBlank(salesOrderDTO.getVestDateStr())) {    // 如果归属时间为空
      LOG.warn("good sale vest date can't be null");
      salesOrderDTO.setVestDateStr(DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN, System.currentTimeMillis()));
    }
    if (ArrayUtils.isEmpty(salesOrderDTO.getItemDTOs())) {
      return isEmpty;
    }
    for (SalesOrderItemDTO itemDTO : salesOrderDTO.getItemDTOs()) {
      if (itemDTO == null) {
        continue;
      }
      if (StringUtils.isNotBlank(itemDTO.getProductName())) {
        isEmpty = false;
        break;
      }
    }
    return isEmpty;
  }

  /**
   * 根据客户Id查找客户的所有欠款记录，包括销售单和维修单
   *
   * @param customerId
   * @param
   * @throws BcgogoException
   */
  @RequestMapping(params = "method=getTotalDebts")
  public void getTotalDebts(HttpServletRequest request, HttpServletResponse response, String customerId) throws BcgogoException {
    IUserService userService = ServiceManager.getService(IUserService.class);
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    try {
      List<CustomerRecordDTO> customerRecords = userService.getCustomerRecordByCustomerId(new Long(customerId));
      double total = 0;
      double totalAmount = 0;
      double totalReturnDebt = 0;
      if (customerRecords.size() > 0) {
        total = customerRecords.get(0).getTotalReceivable();
        totalAmount = customerRecords.get(0).getTotalAmount();
        totalReturnDebt = NumberUtil.numberValue(customerRecords.get(0).getTotalPayable(), 0D);
      }
      //应付款总额
      Double totalPayable = ServiceManager.getService(ISupplierPayableService.class).getSumReceivableByCustomerId(Long.valueOf(customerId), shopId, OrderDebtType.CUSTOMER_DEBT_PAYABLE);
      CustomerDTO customerDTO1 = ServiceManager.getService(IUserService.class).getCustomerById(Long.valueOf(customerId));
      if (customerDTO1.getSupplierId() != null) {
        List<Double> payables = ServiceManager.getService(ISupplierPayableService.class).getSumPayableBySupplierId(customerDTO1.getSupplierId(), shopId, OrderDebtType.SUPPLIER_DEBT_PAYABLE);
        Double supplierPayable = 0.0;
        if (payables != null) {
          supplierPayable = payables.get(0);
        }
        totalReturnDebt = -NumberUtil.doubleVal(totalPayable) + NumberUtil.doubleVal(supplierPayable);

      } else {
        totalReturnDebt = -NumberUtil.doubleVal(totalPayable);
      }
      //应收款总额
      Double receivable = ServiceManager.getService(ISupplierPayableService.class).getSumReceivableByCustomerId(Long.valueOf(customerId), shopId, OrderDebtType.CUSTOMER_DEBT_RECEIVABLE);

      if (customerDTO1.getSupplierId() != null) {
        List<Double> returnList = ServiceManager.getService(ISupplierPayableService.class).getSumPayableBySupplierId(Long.valueOf(customerDTO1.getSupplierId()), shopId, OrderDebtType.SUPPLIER_DEBT_RECEIVABLE);
        if (returnList != null) {
          total = NumberUtil.round(0 - returnList.get(0), 2) + NumberUtil.round(receivable, 2);
        } else {
          total = NumberUtil.round(receivable, 2);
        }

      } else {
        total = NumberUtil.round(receivable, 2);
      }
      PrintWriter writer = response.getWriter();
      writer.write("{\"totalDebt\":\"" + total + "\",\"totalAmount\":\"" + totalAmount + "\",\"totalReturnDebt\":\"" + totalReturnDebt + "\"}");
      writer.close();
    } catch (Exception e) {
      LOG.debug("/sale.do");
      LOG.debug("method=getTotalDebts");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.debug("customerId:" + customerId);
      LOG.error(e.getMessage(), e);
    }
  }

  @RequestMapping(params = "method=searchvehicle")
  public void searchvehicle(ModelMap model, HttpServletRequest request, HttpServletResponse response, Long shopId, String licenceNo) {
    IUserService userService = ServiceManager.getService(IUserService.class);
    String jsonStr = "";
    StringBuffer sb = new StringBuffer("");
    try {
      List<VehicleDTO> vehicles = userService.getVehicleByLicenceNo(shopId, licenceNo);
      if (null != vehicles && vehicles.size() > 0) {
        sb.append("{\"brand\":\"" + vehicles.get(0).getBrand() + "\",");
        sb.append("\"model\":\"" + vehicles.get(0).getModel() + "\",");
        sb.append("\"year\":\"" + vehicles.get(0).getYear() + "\",");
        sb.append("\"engine\":\"" + vehicles.get(0).getEngine() + "\",");

        //如果存在这个车辆，查询客户
        List<CustomerDTO> customerDTOs = userService.getCustomerByLicenceNo(shopId, vehicles.get(0).getLicenceNo());
        sb.append("\"customer\":\"" + customerDTOs.get(0).getName() + "\",");
        sb.append("\"customerIdStr\":\"" + customerDTOs.get(0).getIdStr() + "\",");
        sb.append("\"mobile\":\"" + customerDTOs.get(0).getMobile() + "\"}");
      }
      jsonStr = sb.toString();
      PrintWriter writer = response.getWriter();
      writer.write(jsonStr);
      writer.close();
    } catch (Exception e) {
      LOG.debug("/sale.do");
      LOG.debug("method=searchvehicle");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.debug("licenceNo:" + licenceNo);
      LOG.error(e.getMessage(), e);
    }
  }

  //商品销售页面查询条件改变
  @RequestMapping(params = "method=searchCustomerByName")
  public void searchCustomerByName(ModelMap model, HttpServletRequest request, HttpServletResponse response, Long shopId, String customerName) {
    IUserService userService = ServiceManager.getService(IUserService.class);
    Long shopid = (Long) request.getSession().getAttribute("shopId");
    String jsonStr = "";
    StringBuffer sb = new StringBuffer("");
    try {
      if (shopid == null || "".equals(customerName)) {
        return;
      }
      List<CustomerDTO> customers = userService.getCustomerByName(shopid, customerName);
      if (null != customers && customers.size() > 0) {
        sb.append("{\"success\":\"true\",\"infos\":[");
        for (int i = 0; i < customers.size(); i++) {
          sb.append("{\"customer\":\"" + customers.get(i).getName() + "\",");
          sb.append("\"customerIdStr\":\"" + customers.get(i).getIdStr() + "\",");
          if (!"".equals(customers.get(i).getMobile().trim())) {
            //提示信息拼手机
//            modify by miao.liu 修复 数据为空时，页面显示为 null 字符的问题
//            sb.append("\"mobile\":\"" + customers.get(i).getMobile() + "\",");
//            sb.append("\"info\":\"" + customers.get(i).getName() + "  " + customers.get(i).getMobile() + "\",");

            sb.append("\"mobile\":\"" + (StringUtils.isBlank(customers.get(i).getMobile()) ? "" : customers.get(i).getMobile()) + "\",");
            sb.append("\"info\":\"" + customers.get(i).getName() + "  " + (StringUtils.isBlank(customers.get(i).getMobile()) ? "" : customers.get(i).getMobile()) + "\",");
          } else {
            //提示信息拼电话
//            sb.append("\"mobile\":\"" + customers.get(i).getLandLine() + "\",");
//            sb.append("\"info\":\"" + customers.get(i).getName() + "  " + customers.get(i).getLandLine() + "\",");

            sb.append("\"mobile\":\"" + (StringUtils.isBlank(customers.get(i).getLandLine()) ? "" : customers.get(i).getLandLine()) + "\",");
            sb.append("\"info\":\"" + customers.get(i).getName() + "  " + (StringUtils.isBlank(customers.get(i).getLandLine()) ? "" : customers.get(i).getLandLine()) + "\",");
          }
          String contactTemp = customers.get(i).getContact() == null ? "" : customers.get(i).getContact();
          if (i != customers.size() - 1) {
            sb.append("\"contact\":\"" + contactTemp + "\"},");
          } else {
            sb.append("\"contact\":\"" + contactTemp + "\"}]");
          }
        }
        sb.append("}");
      }
      jsonStr = sb.toString();
      PrintWriter writer = response.getWriter();
      writer.write(jsonStr);
      writer.close();
    } catch (Exception e) {
      LOG.debug("/sale.do");
      LOG.debug("method=searchCustomerByName");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.debug("customerName:" + customerName);
      LOG.error(e.getMessage(), e);
    }
  }

  //
  //商品销售页面查询条件改变
  @RequestMapping(params = "method=searchCustomerById")
  @ResponseBody
  public Object searchCustomerById(ModelMap model, HttpServletRequest request, HttpServletResponse response, Long customerId) {
    IUserService userService = ServiceManager.getService(IUserService.class);
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    Map result = new HashMap();
    try {
      if (shopId == null) {
        return null;
      }
      List<CustomerDTO> customers = userService.getShopCustomerById(shopId, customerId);
      if (null != customers && customers.size() > 0) {
        result.put("success", true);
        result.put("infos", customers);
        return result;
      }
      return null;
    } catch (Exception e) {
      LOG.debug("/sale.do");
      LOG.debug("method=searchCustomerById");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.debug("customerId:" + customerId);
      LOG.error(e.getMessage(), e);
      result.put("success", false);
      return result;
    }
  }

  @RequestMapping(params = "method=saleOrderRepeal")
  public String saleOrderRepeal(ModelMap model, HttpServletRequest request, Long salesOrderId, String repealMsg, Long toStorehouseId) {
    StopWatchUtil sw = new StopWatchUtil("saleOrderRepeal", "start");
    RFITxnService rfiTxnService = ServiceManager.getService(RFITxnService.class);
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    SalesOrderDTO salesOrderDTO = null;
    try {
      Long shopId = WebUtil.getShopId(request);
      Long shopVersionId = WebUtil.getShopVersionId(request);
      String userName = WebUtil.getUserName(request);
      PurchaseOrderDTO purchaseOrderDTO = new PurchaseOrderDTO();
      Result result = new Result();
      if (salesOrderId == null) {
        throw new Exception("salesOrderId is null");
      }
      //去数据库校验salesOrderId对应的状态，如果是已经作废，则跳转显示界面
      if (salesOrderId != null) {
        sw.stopAndStart("getSalesOrder");
        this.getSalesOrderInfo(model, request, valueOf(salesOrderId));  //todo 需要写在service中
        salesOrderDTO = (SalesOrderDTO) model.get("salesOrderDTO");
        if (OrderStatus.SALE_REPEAL == salesOrderDTO.getStatus() || salesOrderDTO.getStatementAccountOrderId() != null) {
          LOG.info("销售单作废异常：重复提交需要作废的salesOrderDTO:{}，页面跳转是现实界面", salesOrderDTO);
          return this.getSalesOrder(model, request, salesOrderDTO.getId().toString());
        }
      }
      if (salesOrderDTO == null) {
        throw new Exception("salesOrderDTO is null");
      }
      sw.stopAndStart("strikeReceivable");
      salesOrderDTO.setRepealMsg(repealMsg);
      ReceivableDTO strikeReceivableDTO = txnService.getReceivableByShopIdAndOrderTypeAndOrderId(shopId, OrderTypes.SALE, salesOrderDTO.getId());
      if (strikeReceivableDTO != null && strikeReceivableDTO.getStrike() != null && strikeReceivableDTO.getStrike() > 0) {
        WebUtil.addSimpleJsMsg(model, new Result("作废失败", "该单据已被冲帐结算，无法作废。", false));
        return this.getSalesOrder(model, request, salesOrderDTO.getId().toString());
      }
      sw.stopAndStart("customerDTO");

      CustomerDTO customerDTO = getUserService().getCustomerDTOByCustomerId(salesOrderDTO.getCustomerId(), salesOrderDTO.getShopId());
      if (salesOrderDTO.getPurchaseOrderId() != null && customerDTO.getCustomerShopId() != null) {
        purchaseOrderDTO = getTxnService().getPurchaseOrder(salesOrderDTO.getPurchaseOrderId(), customerDTO.getCustomerShopId());
      }
      result = checkRepealSaleOrder(salesOrderDTO, purchaseOrderDTO);
      if (!result.isSuccess()) {
        model.addAttribute("result", result);
        return getSalesOrder(model, request, salesOrderDTO.getId().toString());
      }
      if (shopId != null) {
        salesOrderDTO.setShopId(shopId);
      }
      salesOrderDTO.setEditDate(System.currentTimeMillis());
      salesOrderDTO.setEditor(WebUtil.getUserName(request));
      salesOrderDTO.setEditorId(WebUtil.getUserId(request));
      salesOrderDTO.setShopVersionId(WebUtil.getShopVersionId(request));
      salesOrderDTO.setShopVersionId(shopVersionId);
      //更新已经被删除的商品，状态的值置空
      rfiTxnService.updateDeleteProductsByOrderDTO(salesOrderDTO);

      if (salesOrderDTO != null && (OrderStatus.SALE_DONE.equals(salesOrderDTO.getStatus())
        || OrderStatus.SALE_DEBT_DONE.equals(salesOrderDTO.getStatus()))) {
        //出入库打通
        if (BcgogoShopLogicResourceUtils.isThroughSelectSupplier(WebUtil.getShopVersionId(request))) {
          salesOrderDTO.setSelectSupplier(true);
        } else {
          salesOrderDTO.setSelectSupplier(false);
        }
        sw.stopAndStart("repeal");
        getGoodSaleService().repealSalesOrder(shopId, toStorehouseId, salesOrderDTO);

        //更新customerRecord记录
        sw.stopAndStart("updateCustomerRe");
        CustomerRecordDTO customerRecordDTO = rfiTxnService.updateCustomerRecordByShopIdAndOrderId(shopId, salesOrderDTO.getId(), salesOrderDTO.getCustomerId(), OrderTypes.SALE);
        model.addAttribute("customerRecordDTO", customerRecordDTO);

        //add by WLF 更新缓存中待办销售单的数量
        sw.stopAndStart("customerTodoSaleCache");
        List<Long> customerIdList = ServiceManager.getService(ICustomerService.class).getRelatedCustomerIdListByShopId(shopId);
        getTxnService().updateTodoOrderCountInMemcacheByTypeAndShopId(RemindEventType.TODO_SALE_ORDER, shopId, customerIdList);

        sw.stopAndStart("thread");
        //更新orderindex order solr 状态。
        if (salesOrderDTO.getId() != null && salesOrderDTO.getId() > 0) {
          BcgogoEventPublisher bcgogoEventPublisher = new BcgogoEventPublisher();
          SaleOrderSavedEvent saleOrderSavedEvent = new SaleOrderSavedEvent(salesOrderDTO);
          bcgogoEventPublisher.publisherSaleOrderSaved(saleOrderSavedEvent);
          saleOrderSavedEvent.setMainFlag(true);
          request.setAttribute("UNIT_TEST", saleOrderSavedEvent);
        } else {
          LOG.error("/sale.do");
          LOG.error("method=saleOrderRepeal");
          LOG.error("销售单作废时 销售单id为null，salesOrderDTO：{}", salesOrderDTO);
        }

        model.put("salesOrderDTO", salesOrderDTO);
      } else if (salesOrderDTO != null && (OrderStatus.STOCKING.equals(salesOrderDTO.getStatus())
        || OrderStatus.DISPATCH.equals(salesOrderDTO.getStatus()))) {
        salesOrderDTO.setOrderTypes(OrderTypes.SALE);
        getGoodSaleService().stopSaleOrder(toStorehouseId, salesOrderDTO, purchaseOrderDTO);
        //取消 推送消息
        //新订单和备货中,发货中
        ServiceManager.getService(IPushMessageService.class).disabledPushMessageReceiverBySourceId(null, purchaseOrderDTO.getId(), null,
          PushMessageSourceType.SALE_NEW, PushMessageSourceType.PURCHASE_SELLER_STOCK, PushMessageSourceType.PURCHASE_SELLER_DISPATCH);
      }
      sw.stopAndPrintLog();
    } catch (Exception e) {
      LOG.error("销售单作废出错需要作废的salesOrderDTO：{}" + e.getMessage(), salesOrderDTO, e);
    }
    model.addAttribute("salesOrderId", salesOrderDTO.getId());
    return REDIRECT_SHOW;
  }

  @RequestMapping(params = "method=validateCopy")
  @ResponseBody
  public Result validateCopy(ModelMap model, HttpServletRequest request, Long salesOrderId) {
    RFITxnService rfiTxnService = ServiceManager.getService(RFITxnService.class);
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    IUserService userService = ServiceManager.getService(IUserService.class);
    Long shopId = null;
    Long userId = null;
    try {
      shopId = WebUtil.getShopId(request);
      userId = WebUtil.getUserId(request);
      if (shopId == null || salesOrderId == null) {
        LOG.error("sales.do?method=validateCopy, shopId:{}, salesOrderId:{}", shopId, salesOrderId);
        return new Result("验证失败", "验证失败，请重试！", false);
      }
      return getGoodSaleService().validateCopy(salesOrderId, shopId);
    } catch (Exception e) {
      LOG.error("sales.do?method=validateCopy. shopId:{}, userId:{}", shopId, userId);
      LOG.error(e.getMessage(), e);
      return new Result("验证失败", "验证失败，请重试！", false);
    }
  }

  //复制销售单
  @RequestMapping(params = "method=copyGoodSale")
  public String copyGoodSale(ModelMap model, HttpServletRequest request, Long salesOrderId) {
    RFITxnService rfiTxnService = ServiceManager.getService(RFITxnService.class);
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    ICustomerService customerService = ServiceManager.getService(ICustomerService.class);
    IProductThroughService productThroughService = ServiceManager.getService(IProductThroughService.class);
    try {
      Long shopId = WebUtil.getShopId(request);
      SalesOrderDTO salesOrderDTO = null;
      if (salesOrderId != null) {
        getSalesOrderInfo(model, request, salesOrderId.toString());
      }
      salesOrderDTO = (SalesOrderDTO) model.get("salesOrderDTO");
      if (BcgogoShopLogicResourceUtils.isThroughSelectSupplier(WebUtil.getShopVersionId(request))) {
        Map<Long, List<OutStorageRelationDTO>> outStorageRelationMap = productThroughService.getOutStorageRelationMap(shopId, salesOrderId);
        SupplierInventoryDTO condition = null;
        List<SupplierInventory> supplierInventoryList = null;
        for (BcgogoOrderItemDto itemDto : salesOrderDTO.getItemDTOs()) {
          condition = new SupplierInventoryDTO();
          condition.setShopId(shopId);
          condition.setStorehouseId(salesOrderDTO.getStorehouseId());
          condition.setProductIds(new Long[]{itemDto.getProductId()});
          supplierInventoryList = productThroughService.getSupplierInventory(condition);
          List<OutStorageRelationDTO> relationDTOs = new ArrayList<OutStorageRelationDTO>();
          List<OutStorageRelationDTO> outStorageRelationDTOs = outStorageRelationMap.get(itemDto.getId());
          if (CollectionUtil.isNotEmpty(supplierInventoryList)) {
            for (SupplierInventory inventory : supplierInventoryList) {
              OutStorageRelationDTO relationDTO = new OutStorageRelationDTO();
              relationDTO.setRelatedSupplierId(inventory.getSupplierId());
              relationDTO.setRelatedSupplierName(inventory.getSupplierName());
              relationDTO.setRelatedSupplierInventory(inventory.getRemainAmount());
              relationDTO.setSupplierType(inventory.getSupplierType());
              if (CollectionUtil.isNotEmpty(outStorageRelationDTOs)) {
                for (OutStorageRelationDTO outStorageRelationDTO : outStorageRelationDTOs) {
                  Double useAmount = outStorageRelationDTO.getSupplierRelatedAmount();
                  if (useAmount == null) {
                    useAmount = 0d;
                  }
                  String key = ObjectUtil.generateKey(outStorageRelationDTO.getRelatedSupplierId(), outStorageRelationDTO.getSupplierType());
                  if (key.equals(ObjectUtil.generateKey(inventory.getSupplierId(), inventory.getSupplierType())) && useAmount > 0) {
                    relationDTO.setUseRelatedAmount(useAmount);
                  }
                }
              }
              relationDTOs.add(relationDTO);
            }
          }
          itemDto.setOutStorageRelationDTOs(relationDTOs.toArray(new OutStorageRelationDTO[relationDTOs.size()]));
        }
      }
      salesOrderDTO = rfiTxnService.copyGoodSale(shopId, salesOrderDTO);
      CustomerDTO customerDTO = salesOrderDTO.generateCustomerDTO();
      boolean customerSame = customerService.compareCustomerSameWithHistory(customerDTO, shopId);
      if (!customerSame) {
        salesOrderDTO.clearCustomerInfo();
        model.remove("customerRecordDTO");
      } else {
        model.addAttribute("isAdd", false);
      }
      salesOrderDTO.setReceiptNo(null);
      salesOrderDTO.setDraftOrderIdStr(null);
      salesOrderDTO.setPurchaseOrderId(null);
      salesOrderDTO.setPurchaseOrderDTO(null);
      salesOrderDTO.setPurchaseMemo(null);
      salesOrderDTO.setPurchaseVestDate(null);
      salesOrderDTO.setStatus(null);

      if (BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(WebUtil.getShopVersionId(request))) {
        IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
        List<StoreHouseDTO> storeHouseDTOList = storeHouseService.getAllStoreHousesByShopId(shopId);
        model.addAttribute("storeHouseDTOList", storeHouseDTOList);//select 选项
        if (salesOrderDTO.getStorehouseId() != null) {
          StoreHouseDTO storeHouseDTO = storeHouseService.getStoreHouseDTOById(shopId, salesOrderDTO.getStorehouseId());
          if (storeHouseDTO == null || DeletedType.TRUE.equals(storeHouseDTO.getDeleted())) {
            salesOrderDTO.setStorehouseId(null);
            salesOrderDTO.setStorehouseName(null);
          }
        }
        if (CollectionUtils.isNotEmpty(storeHouseDTOList) && salesOrderDTO.getStorehouseId() == null) {
          if (storeHouseDTOList.size() == 1) {
            salesOrderDTO.setStorehouseId(storeHouseDTOList.get(0).getId());
          }
        }
        InventoryService inventoryService = ServiceManager.getService(InventoryService.class);
        //更新库存 根据仓库
        inventoryService.updateItemDTOInventoryAmountByStorehouse(shopId, salesOrderDTO.getStorehouseId(), salesOrderDTO);
      }
      request.setAttribute("wholesalerVersion", ConfigUtils.isWholesalerVersion(WebUtil.getShopVersionId(request)));
      model.addAttribute("salesOrderDTO", salesOrderDTO);
    } catch (Exception e) {
      LOG.debug("/sale.do \n method=copyGoodSale \n shopId:" + request.getSession().getAttribute("shopId") +
        ",userId:" + request.getSession().getAttribute("userId") + "  \n customerId:");
      LOG.error(e.getMessage(), e);
    }
    return "/txn/goodsSale";
  }

  @RequestMapping(params = "method=accountDetail")
  public String accountDetail(HttpServletRequest request) throws Exception {
    String customerIdStr = request.getParameter("customerId");
    Long shopId = WebUtil.getShopId(request);
    IMembersService membersService = ServiceManager.getService(IMembersService.class);
    if (StringUtils.isNotBlank(customerIdStr)) {
      MemberDTO memberDTO = membersService.getMemberByCustomerId(shopId, Long.valueOf(customerIdStr));
      request.setAttribute("memberBalance", null == memberDTO ? null : memberDTO.getBalance());
      request.setAttribute("memberNo", null == memberDTO ? null : memberDTO.getMemberNo());
      Double memberDiscount = null;
      if (null != memberDTO && null != memberDTO.getMemberDiscount()) {
        memberDiscount = memberDTO.getMemberDiscount();
        memberDiscount = NumberUtil.round(memberDiscount * 10, 1);
      }
      request.setAttribute("memberDiscount", memberDiscount);
      // add by zhuj 查询用户可用预收款
      ICustomerDepositService customerDepositService = ServiceManager.getService(ICustomerDepositService.class);
      CustomerDepositDTO customerDepositDTO = customerDepositService.queryCustomerDepositByShopIdAndCustomerId(shopId, Long.valueOf(customerIdStr));
      if (customerDepositDTO != null) {
        request.setAttribute("depositAvailable", customerDepositDTO.getActuallyPaid());
      }
    }
    return "/txn/salesAccountDetail1";
  }

  /**
   * 根据前台ajax提交的dto进行验证 返回校验结果
   *
   * @param request
   * @param salesOrderId 销售单id
   */
  @RequestMapping(params = "method=validateDispatchSaleOrder")
  @ResponseBody
  public Result validateDispatchSaleOrder(HttpServletRequest request, Long salesOrderId) {
    try {
      Long shopId = (Long) request.getSession().getAttribute("shopId");
      Long shopVersionId = WebUtil.getShopVersionId(request);
      SalesOrderDTO salesOrderDTO = getTxnService().getSalesOrder(salesOrderId, shopId);
      salesOrderDTO.setShopVersionId(shopVersionId);
      if (!ArrayUtils.isEmpty(salesOrderDTO.getItemDTOs())) {
        for (SalesOrderItemDTO salesOrderItemDTO : salesOrderDTO.getItemDTOs()) {
          ProductDTO productDTO = getProductService().getProductByProductLocalInfoId(salesOrderItemDTO.getProductId(), salesOrderDTO.getShopId());
          ProductHistoryDTO productHistoryDTO = ServiceManager.getService(IProductHistoryService.class).getProductHistoryById(salesOrderItemDTO.getProductHistoryId(), shopId);
          if (productHistoryDTO != null) {
            salesOrderItemDTO.setProductHistoryDTO(productHistoryDTO);
            if (OrderUtil.salesOrderInProgress.contains(salesOrderDTO.getStatus())) {
              salesOrderItemDTO.setProductUnitRateInfo(productDTO);
            }
          } else {
            salesOrderItemDTO.setProductDTOWithOutUnit(productDTO);
          }
        }
      }
      return checkDispatchSaleOrder(salesOrderDTO);
    } catch (Exception e) {
      LOG.error("/sale.do");
      LOG.error("method=validateDispatchSaleOrder");
      LOG.error("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
      return new Result(MemberConstant.SUBMIT_EXCEPTION, false);
    }
  }

  /**
   * 根据前台ajax提交的dto进行验证 返回校验结果
   *
   * @param request
   * @param salesOrderDTO 销售单
   */
  @RequestMapping(params = "method=validateSalesOrder")
  @ResponseBody
  public Result validateSalesOrder(HttpServletRequest request, SalesOrderDTO salesOrderDTO) {
    StopWatchUtil sw = new StopWatchUtil("validateSalesOrder", "start");
    RFITxnService rfiTxnService = ServiceManager.getService(RFITxnService.class);
    IUserService userService = ServiceManager.getService(IUserService.class);
    try {
      Long shopId = (Long) request.getSession().getAttribute("shopId");
      Long shopVersionId = WebUtil.getShopVersionId(request);
      // add by zhuj 校验是否为新增用户
      //comment by qxy 不需要后台校验，前台已经搞定
//      String isAdd = request.getParameter("isAdd");
//      if (StringUtils.isBlank(isAdd)) {
//        List<CustomerDTO> customerDTOList = userService.getCustomerByName(shopId, salesOrderDTO.getCustomer());
//        if (!CollectionUtils.isEmpty(customerDTOList)){
//          return new Result(ValidatorConstant.CUSTOMER_NAME_DUPLICATE, false);
//        }
//      }
      if (null != salesOrderDTO.getCustomerId()) {
        Customer customer = userService.getCustomerByCustomerId(salesOrderDTO.getCustomerId(), shopId);
        if (null != customer && CustomerStatus.DISABLED.equals(customer.getStatus())) {
          return new Result(CustomerConstant.CUSTOMER_DISABLED_NO_SETTLE, false);
        }
      }

      List<Long> productIdList = new ArrayList<Long>();
      if (!BcgogoShopLogicResourceUtils.isIgnoreVerifierInventoryResource(shopVersionId)) {
        //去掉空行
        productIdList = removeNullProductRow(salesOrderDTO);
      }

      if (ArrayUtil.isEmpty(salesOrderDTO.getItemDTOs())) {
        return new Result(ValidatorConstant.ORDER_NULL_MSG, false);
      } else if (!ArrayUtil.isEmpty(salesOrderDTO.getItemDTOs()) && salesOrderDTO.getItemDTOs().length > productIdList.size()) {
        if (!BcgogoShopLogicResourceUtils.isIgnoreVerifierInventoryResource(shopVersionId)) {
          return new Result(ValidatorConstant.ORDER_NEW_PRODUCT_ERROR, false);
        }
      } else {
        if (!BcgogoShopLogicResourceUtils.isIgnoreVerifierInventoryResource(shopVersionId)) {
          IInventoryService inventoryService = ServiceManager.getService(IInventoryService.class);
          if (BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(shopVersionId)) {
            //校验产品总库存
            //通过仓库校验库存
            if (salesOrderDTO.getStorehouseId() != null) {
              IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
              StoreHouseDTO storeHouseDTO = storeHouseService.getStoreHouseDTOById(shopId, salesOrderDTO.getStorehouseId());
              if (storeHouseDTO == null || DeletedType.TRUE.equals(storeHouseDTO.getDeleted())) {
                return new Result(ValidatorConstant.STOREHOUSE_DELETED_MSG, false);
              }
              Map<String, String> data = new HashMap<String, String>();
              if (!inventoryService.checkBatchProductInventoryByStoreHouse(shopId, salesOrderDTO.getStorehouseId(), salesOrderDTO.getItemDTOs(), data, productIdList)) {
                //校验产品是否可以调拨
                if (inventoryService.checkBatchProductInventoryInOtherStorehouse(shopId, salesOrderDTO, productIdList)) {
                  return new Result(ValidatorConstant.PRODUCT_STOREHOUSE_INVENTORY_LACK, false, Result.Operation.UPDATE_PRODUCT_INVENTORY.getValue() + "+" + Result.Operation.CONFIRM_ALLOCATE_RECORD.getValue(), data);
                } else {
                  return new Result(ValidatorConstant.PRODUCT_INVENTORY_LACK, false, Result.Operation.UPDATE_PRODUCT_INVENTORY.getValue(), data);
                }
              }
            } else {
              return new Result(ValidatorConstant.STOREHOUSE_NULL_MSG, false);
            }
          } else {
            //校验产品库存
            Map<String, String> data = new HashMap<String, String>();
            if (!inventoryService.checkBatchProductInventory(shopId, salesOrderDTO, data, productIdList)) {
              return new Result(ValidatorConstant.PRODUCT_INVENTORY_LACK, false, Result.Operation.UPDATE_PRODUCT_INVENTORY.getValue(), data);
            }
          }
        }
      }
      String validateType = request.getParameter("validateType");
      //校验会员信息
      if ("accountDetail".equals(validateType)) {
        IMemberCheckerService memberCheckerService = ServiceManager.getService(IMemberCheckerService.class);
        String msg = memberCheckerService.checkSalesOrderMemberInfo(shopId, salesOrderDTO);
        if (!MemberConstant.MEMBER_VALIDATE_SUCCESS.equals(msg)) {
          return new Result(msg, false);
        }
      }
      //小型店铺校验库存商品是否被删除
      if (!BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(shopVersionId)) {
        if (salesOrderDTO.getShopId() == null) {
          salesOrderDTO.setShopId(shopId);
        }
        return rfiTxnService.getDeletedProductValidatorResult(salesOrderDTO);
      }
      sw.stopAndPrintLog();
    } catch (Exception e) {
      LOG.error("/sale.do");
      LOG.error("method=validateSalesOrder");
      LOG.error("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
      return new Result(MemberConstant.SUBMIT_EXCEPTION, false);
    }
    return new Result();
  }

  private List<Long> removeNullProductRow(SalesOrderDTO salesOrderDTO) {
    List<Long> productIdList = new ArrayList<Long>();
    if (salesOrderDTO.getItemDTOs() != null) {
      SalesOrderItemDTO[] salesOrderItemDTOs = salesOrderDTO.getItemDTOs();
      List<SalesOrderItemDTO> salesOrderItemDTOList = new ArrayList<SalesOrderItemDTO>();
      for (int i = 0; i < salesOrderItemDTOs.length; i++) {
        if (salesOrderItemDTOs[i].getProductId() != null && StringUtils.isNotBlank(salesOrderItemDTOs[i].getProductName())) {
          salesOrderItemDTOList.add(salesOrderItemDTOs[i]);
          productIdList.add(salesOrderItemDTOs[i].getProductId());
        }
      }
      if (CollectionUtils.isNotEmpty(salesOrderItemDTOList)) {
        salesOrderDTO.setItemDTOs(salesOrderItemDTOList.toArray(new SalesOrderItemDTO[salesOrderItemDTOList.size()]));
      } else {
        salesOrderDTO.setItemDTOs(null);
      }
    }
    return productIdList;
  }

  @RequestMapping(params = "method=acceptSaleOrder")
  public String acceptSaleOrder(ModelMap model, HttpServletRequest request, SalesOrderDTO salesOrderDTO) {
    IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
    Long salesOrderId = salesOrderDTO.getId();
    String goodsSaler = salesOrderDTO.getGoodsSaler();
    Long goodsSalerId = salesOrderDTO.getGoodsSalerId();
    String acceptMemo = salesOrderDTO.getAcceptMemo();
    Long storehouseId = salesOrderDTO.getStorehouseId();
    String preDispatchDateStr = salesOrderDTO.getPreDispatchDateStr();

    Result result = new Result();
    Long purchaseOrderId = null;
    Long shopId = WebUtil.getShopId(request);
    try {
      if (goodsSaler == null) {//此处一定要用 == 请不要改成Stringutil工具类
        goodsSaler = WebUtil.getUserName(request);
      }
      Map<Long, OutStorageRelationDTO[]> relationDTOMap = null;
      if (BcgogoShopLogicResourceUtils.isThroughSelectSupplier(WebUtil.getShopVersionId(request))) {
        if (!ArrayUtil.isEmpty(salesOrderDTO.getItemDTOs())) {
          relationDTOMap = new HashMap<Long, OutStorageRelationDTO[]>();
          for (BcgogoOrderItemDto itemDto : salesOrderDTO.getItemDTOs()) {
            if (itemDto.getProductId() == null || itemDto == null) {
              LOG.error("error info...");
              continue;
            }
            relationDTOMap.put(itemDto.getProductId(), itemDto.getOutStorageRelationDTOs());
          }
        }
      }
      CustomerDTO customerDTO = null;
      PurchaseOrderDTO purchaseOrderDTO = null;
      if (salesOrderId != null) {
        getSalesOrderInfo(model, request, salesOrderId.toString());
      }
      salesOrderDTO = (SalesOrderDTO) model.get("salesOrderDTO");
      if (salesOrderDTO != null) {
        salesOrderDTO.setGoodsSaler(goodsSaler);
        salesOrderDTO.setGoodsSalerId(goodsSalerId);
        salesOrderDTO.setVestDate(System.currentTimeMillis());
        salesOrderDTO.setUserId(WebUtil.getUserId(request));
        salesOrderDTO.setPreDispatchDateFromPage(preDispatchDateStr);  //todo
        salesOrderDTO.setMemo(acceptMemo);
        if (salesOrderDTO.getCustomerId() != null) {
          customerDTO = getUserService().getCustomerById(salesOrderDTO.getCustomerId());
        }
        if (salesOrderDTO.getPurchaseOrderId() != null) {
          purchaseOrderId = salesOrderDTO.getPurchaseOrderId();
          purchaseOrderDTO = getTxnService().getPurchaseOrder(salesOrderDTO.getPurchaseOrderId(), customerDTO.getCustomerShopId());
        }
      }
      result = checkAcceptSaleOrder(salesOrderDTO, customerDTO, purchaseOrderDTO);
      if (!result.isSuccess()) {
        model.addAttribute("result", result);
        return SHOW;
      }
      // 加锁失败
      if (!(BcgogoConcurrentController.lock(ConcurrentScene.SALE, salesOrderId.toString())
        && BcgogoConcurrentController.lock(ConcurrentScene.PURCHASE, purchaseOrderId.toString()))) {
        result.setMsg("当前单据正在被操作，请稍候再试");
        model.addAttribute("result", result);
        return SHOW;
      }
      salesOrderDTO.setShopVersionId(WebUtil.getShopVersionId(request));
      if (BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(salesOrderDTO.getShopVersionId())) {
        StoreHouseDTO storeHouseDTO = storeHouseService.getStoreHouseDTOById(salesOrderDTO.getShopId(), storehouseId);
        salesOrderDTO.setStorehouseId(storehouseId);
        salesOrderDTO.setStorehouseName(storeHouseDTO == null ? null : storeHouseDTO.getName());
      }
      if (BcgogoShopLogicResourceUtils.isThroughSelectSupplier(WebUtil.getShopVersionId(request))) {
        salesOrderDTO.setSelectSupplier(true);
      }
      getGoodSaleService().acceptSaleOrder(salesOrderDTO, purchaseOrderDTO, relationDTOMap);

      //生成推送消息
      ServiceManager.getService(IOrderPushMessageService.class)
        .createOrderPushMessageMessage(purchaseOrderDTO.getSupplierShopId(), purchaseOrderDTO.getShopId(), purchaseOrderDTO.getId(), PushMessageSourceType.PURCHASE_SELLER_STOCK);

      //发送接受短信
      getSmsService().salesAcceptedSMS(purchaseOrderDTO.getShopId(), salesOrderDTO.getShopId(), purchaseOrderDTO.getReceiptNo(), salesOrderDTO.getMobile());
      getSearchService().updateSearchOrderStatus(salesOrderDTO, purchaseOrderDTO);
      getOrderSolrWriterService().reCreateOrderSolrIndex(ServiceManager.getService(IConfigService.class).getShopById(purchaseOrderDTO.getShopId()), OrderTypes.PURCHASE, purchaseOrderDTO.getId());
      getOrderSolrWriterService().reCreateOrderSolrIndex(ServiceManager.getService(IConfigService.class).getShopById(salesOrderDTO.getShopId()), OrderTypes.SALE, salesOrderDTO.getId());
      model.addAttribute("salesOrderId", salesOrderDTO.getId());
      return ONLINE_SALES_ORDER;
    } catch (Exception e) {
      LOG.error("acceptSaleOrder 出错" + e.getMessage(), e);
      return null;
    } finally {
      if (salesOrderId != null) {
        BcgogoConcurrentController.release(ConcurrentScene.SALE, salesOrderId.toString());
      }
      if (purchaseOrderId != null) {
        BcgogoConcurrentController.release(ConcurrentScene.PURCHASE, purchaseOrderId.toString());
      }
    }
  }


  @RequestMapping(params = "method=acceptPendingPurchaseOrder")
  public String acceptPendingPurchaseOrder(ModelMap model, HttpServletRequest request, SalesOrderDTO salesOrderDTO) {
    IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
    IGoodBuyService goodBuyService = ServiceManager.getService(IGoodBuyService.class);
    Long salesOrderId = salesOrderDTO.getId();
    String goodsSaler = salesOrderDTO.getGoodsSaler();
    Long goodsSalerId = salesOrderDTO.getGoodsSalerId();
    String acceptMemo = salesOrderDTO.getAcceptMemo();
    Long storehouseId = salesOrderDTO.getStorehouseId();
    String preDispatchDateStr = salesOrderDTO.getPreDispatchDateStr();
    Result result = new Result();
    Long purchaseOrderId = salesOrderDTO.getPurchaseOrderId();
    Long shopId = WebUtil.getShopId(request);
    try {
      if (goodsSaler == null) {//此处一定要用 == 请不要改成Stringutil工具类
        goodsSaler = WebUtil.getUserName(request);
      }
      PurchaseOrderDTO purchaseOrderDTO = getRfiTxnService().getPurchaseOrderDTOByIdAndSupplierShopId(purchaseOrderId, shopId);
      SalesOrderDTO salesOrderDTOFromDB = getGoodSaleService().getSimpleSalesOrderByPurchaseOrderId(purchaseOrderId);
      result = checkAcceptPendingPurchaseOrder(salesOrderDTO, purchaseOrderDTO);
      if (!result.isSuccess()) {
        model.addAttribute("result", result);
        if (salesOrderDTOFromDB != null && salesOrderDTOFromDB.getId() != null) {
          return toOnlineSalesOrder(model, request, salesOrderDTOFromDB.getIdStr());
        }
        return toOnlinePendingPurchaseOrder(model, request, String.valueOf(purchaseOrderId));
      }

      // 加锁失败
      if (!BcgogoConcurrentController.lock(ConcurrentScene.PURCHASE, purchaseOrderId.toString())) {
        result.setMsg("当前单据正在被操作，请稍候再试");
        model.addAttribute("result", result);
        return SHOW_PENDING_PURCHASE;
      }

      SalesOrderDTO newSalesOrderDTO = getGoodSaleService().createOnlineSalesOrderDTO(purchaseOrderDTO);
      Map<Long, OutStorageRelationDTO[]> relationDTOMap = null;
      if (BcgogoShopLogicResourceUtils.isThroughSelectSupplier(WebUtil.getShopVersionId(request))) {
        if (!ArrayUtil.isEmpty(salesOrderDTO.getItemDTOs())) {
          relationDTOMap = new HashMap<Long, OutStorageRelationDTO[]>();
          for (BcgogoOrderItemDto itemDto : salesOrderDTO.getItemDTOs()) {
            if (itemDto.getProductId() == null || itemDto == null) {
              LOG.error("error info...");
              continue;
            }
            relationDTOMap.put(itemDto.getProductId(), itemDto.getOutStorageRelationDTOs());
          }
        }
      }
      if (newSalesOrderDTO != null) {
        newSalesOrderDTO.setGoodsSaler(goodsSaler);
        newSalesOrderDTO.setGoodsSalerId(goodsSalerId);
        newSalesOrderDTO.setVestDate(System.currentTimeMillis());
        newSalesOrderDTO.setUserId(WebUtil.getUserId(request));
        newSalesOrderDTO.setPreDispatchDateFromPage(preDispatchDateStr);
        newSalesOrderDTO.setMemo(acceptMemo);
      }

      newSalesOrderDTO.setShopVersionId(WebUtil.getShopVersionId(request));
      if (BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(newSalesOrderDTO.getShopVersionId())) {
        StoreHouseDTO storeHouseDTO = storeHouseService.getStoreHouseDTOById(newSalesOrderDTO.getShopId(), storehouseId);
        newSalesOrderDTO.setStorehouseId(storehouseId);
        newSalesOrderDTO.setStorehouseName(storeHouseDTO == null ? null : storeHouseDTO.getName());
      }
      if (BcgogoShopLogicResourceUtils.isThroughSelectSupplier(WebUtil.getShopVersionId(request))) {
        newSalesOrderDTO.setSelectSupplier(true);
      }
      getGoodSaleService().acceptSaleOrder(newSalesOrderDTO, purchaseOrderDTO, relationDTOMap);

      //生成推送消息
      ServiceManager.getService(IOrderPushMessageService.class)
        .createOrderPushMessageMessage(purchaseOrderDTO.getSupplierShopId(), purchaseOrderDTO.getShopId(), purchaseOrderDTO.getId(), PushMessageSourceType.PURCHASE_SELLER_STOCK);

      //发送接受短信
      getSmsService().salesAcceptedSMS(purchaseOrderDTO.getShopId(), newSalesOrderDTO.getShopId(), purchaseOrderDTO.getReceiptNo(), newSalesOrderDTO.getMobile());
      getSearchService().updateSearchOrderStatus(newSalesOrderDTO, purchaseOrderDTO);
      getOrderSolrWriterService().reCreateOrderSolrIndex(ServiceManager.getService(IConfigService.class).getShopById(purchaseOrderDTO.getShopId()), OrderTypes.PURCHASE, purchaseOrderDTO.getId());
//      getOrderSolrWriterService().reCreateOrderSolrIndex(ServiceManager.getService(IConfigService.class).getShopById(newSalesOrderDTO.getShopId()), OrderTypes.SALE, newSalesOrderDTO.getId());
      BcgogoEventPublisher bcgogoSaleOrderEventPublisher = new BcgogoEventPublisher();
      SaleOrderSavedEvent saleOrderSavedEvent = new SaleOrderSavedEvent(newSalesOrderDTO);
      bcgogoSaleOrderEventPublisher.publisherSaleOrderSaved(saleOrderSavedEvent);
      saleOrderSavedEvent.setMainFlag(true);
      request.setAttribute("UNIT_TEST", saleOrderSavedEvent);

      model.addAttribute("salesOrderId", newSalesOrderDTO.getId());
      return ONLINE_SALES_ORDER;
    } catch (Exception e) {
      LOG.error("acceptSaleOrder 出错" + e.getMessage(), e);
      return null;
    } finally {
      if (salesOrderId != null) {
        BcgogoConcurrentController.release(ConcurrentScene.SALE, salesOrderId.toString());
      }
      if (purchaseOrderId != null) {
        BcgogoConcurrentController.release(ConcurrentScene.PURCHASE, purchaseOrderId.toString());
      }
    }
  }


  @RequestMapping(params = "method=dispatchSaleOrder")
  public String dispatchSaleOrder(ModelMap model, HttpServletRequest request, SalesOrderDTO salesOrderDTO) {
    Result result = new Result();
    Long purchaseOrderId = null;
    String company = salesOrderDTO.getCompany();
    String waybills = salesOrderDTO.getWaybills();
    String dispatchMemo = salesOrderDTO.getDispatchMemo();
    Long salesOrderId = salesOrderDTO.getId();
    try {
      CustomerDTO customerDTO = null;
      PurchaseOrderDTO purchaseOrderDTO = null;
      Map<Long, OutStorageRelationDTO[]> relationDTOMap = null;
      if (BcgogoShopLogicResourceUtils.isThroughSelectSupplier(WebUtil.getShopVersionId(request))) {
        if (!ArrayUtil.isEmpty(salesOrderDTO.getItemDTOs())) {
          relationDTOMap = new HashMap<Long, OutStorageRelationDTO[]>();
          for (BcgogoOrderItemDto itemDto : salesOrderDTO.getItemDTOs()) {
            if (itemDto.getProductId() == null || itemDto == null) {
              LOG.error("error info...");
              continue;
            }
            relationDTOMap.put(itemDto.getProductId(), itemDto.getOutStorageRelationDTOs());
          }
        }
      }
      if (salesOrderId != null) {
        getSalesOrderInfo(model, request, salesOrderId.toString());
      }
      salesOrderDTO = (SalesOrderDTO) model.get("salesOrderDTO");
      if (salesOrderDTO != null) {
        salesOrderDTO.setUserId(WebUtil.getUserId(request));
        salesOrderDTO.setCompany(company);
        salesOrderDTO.setWaybills(waybills);
        salesOrderDTO.setDispatchMemo(dispatchMemo);
        salesOrderDTO.setShopVersionId(WebUtil.getShopVersionId(request));
      }
      if (salesOrderDTO != null && salesOrderDTO.getCustomerId() != null) {
        customerDTO = getUserService().getCustomerById(salesOrderDTO.getCustomerId());
      }
      if (salesOrderDTO != null && salesOrderDTO.getPurchaseOrderId() != null) {
        purchaseOrderId = salesOrderDTO.getPurchaseOrderId();
        purchaseOrderDTO = getTxnService().getPurchaseOrder(salesOrderDTO.getPurchaseOrderId(), customerDTO.getCustomerShopId());
      }
      result = checkDispatchSaleOrder(salesOrderDTO);
      if (!result.isSuccess()) {
        model.addAttribute("result", result);
        if (null == salesOrderDTO) {
          salesOrderDTO = new SalesOrderDTO();
        }
        model.addAttribute("salesOrderDTO", salesOrderDTO);
        return SHOW;
      }
      // 加锁失败
      if (!(BcgogoConcurrentController.lock(ConcurrentScene.SALE, salesOrderId.toString())
        && BcgogoConcurrentController.lock(ConcurrentScene.PURCHASE, purchaseOrderId.toString()))) {
        result.setMsg("当前单据正在被操作，请稍候再试");
        model.addAttribute("result", result);
        if (null == salesOrderDTO) {
          salesOrderDTO = new SalesOrderDTO();
        }
        model.addAttribute("salesOrderDTO", salesOrderDTO);
        return SHOW;
      }
      //生成推送消息
      //需要在更新状态之前
      ServiceManager.getService(IOrderPushMessageService.class)
        .createOrderPushMessageMessage(purchaseOrderDTO.getSupplierShopId(), purchaseOrderDTO.getShopId(), purchaseOrderDTO.getId(), PushMessageSourceType.PURCHASE_SELLER_DISPATCH);

      getGoodSaleService().dispatchSaleOrder(salesOrderDTO, purchaseOrderDTO, relationDTOMap);

      getSearchService().updateSearchOrderStatus(salesOrderDTO, purchaseOrderDTO);
      getOrderSolrWriterService().reCreateOrderSolrIndex(ServiceManager.getService(IConfigService.class).getShopById(purchaseOrderDTO.getShopId()), OrderTypes.PURCHASE, purchaseOrderDTO.getId());
      getOrderSolrWriterService().reCreateOrderSolrIndex(ServiceManager.getService(IConfigService.class).getShopById(salesOrderDTO.getShopId()), OrderTypes.SALE, salesOrderDTO.getId());
      model.addAttribute("salesOrderId", salesOrderDTO.getId());
      return ONLINE_SALES_ORDER;
    } catch (Exception e) {
      LOG.error("dispatchSaleOrder 出错" + e.getMessage(), e);
      return null;
    } finally {
      if (salesOrderId != null) {
        BcgogoConcurrentController.release(ConcurrentScene.SALE, salesOrderId.toString());
      }
      if (purchaseOrderId != null) {
        BcgogoConcurrentController.release(ConcurrentScene.PURCHASE, purchaseOrderId.toString());
      }

    }
  }

  @RequestMapping(params = "method=refuseSaleOrder")
  public String refuseSaleOrder(ModelMap model, HttpServletRequest request, SalesOrderDTO salesOrderDTO) {
    Long shopId = null;
    Result result = new Result();
    Long purchaseOrderId = null;
    Long salesOrderId = null;
    String refuseMsg = null;
    String saleMemo = null;
    try {
      salesOrderId = salesOrderDTO.getId();
      refuseMsg = salesOrderDTO.getRefuseMsg();
      saleMemo = salesOrderDTO.getSaleMemo();
      shopId = WebUtil.getShopId(request);
      CustomerDTO customerDTO = null;
      PurchaseOrderDTO purchaseOrderDTO = null;
      if (salesOrderId != null) {
        getSalesOrderInfo(model, request, salesOrderId.toString());
      }
      salesOrderDTO = (SalesOrderDTO) model.get("salesOrderDTO");
      if (salesOrderDTO != null && salesOrderDTO.getCustomerId() != null) {
        customerDTO = getUserService().getCustomerById(salesOrderDTO.getCustomerId());
      }
      if (salesOrderDTO != null && salesOrderDTO.getPurchaseOrderId() != null) {
        salesOrderDTO.setUserId(WebUtil.getUserId(request));
        salesOrderDTO.setRefuseMsg(refuseMsg);
        salesOrderDTO.setMemo(saleMemo);
        purchaseOrderId = salesOrderDTO.getPurchaseOrderId();
        purchaseOrderDTO = getTxnService().getPurchaseOrder(salesOrderDTO.getPurchaseOrderId(), customerDTO.getCustomerShopId());
      }
      result = checkRefuseSaleOrder(salesOrderDTO, customerDTO, purchaseOrderDTO);
      if (!result.isSuccess()) {
        model.addAttribute("result", result);
        return SHOW;
      }
      // 加锁失败
      if (!(BcgogoConcurrentController.lock(ConcurrentScene.SALE, salesOrderId.toString())
        && BcgogoConcurrentController.lock(ConcurrentScene.PURCHASE, purchaseOrderId.toString()))) {
        result.setMsg("当前单据正在被操作，请稍候再试");
        model.addAttribute("result", result);
        return SHOW;
      }

      getGoodSaleService().refuseSaleOrder(salesOrderDTO, purchaseOrderDTO);

      //生成推送消息
      ServiceManager.getService(IOrderPushMessageService.class)
        .createOrderPushMessageMessage(purchaseOrderDTO.getSupplierShopId(), purchaseOrderDTO.getShopId(), purchaseOrderDTO.getId(), PushMessageSourceType.PURCHASE_SELLER_REFUSED);

      //发送拒绝短信
      getSmsService().salesRefuseSMS(purchaseOrderDTO.getShopId(), salesOrderDTO.getShopId(), purchaseOrderDTO.getReceiptNo(), salesOrderDTO.getMobile());
      getSearchService().updateSearchOrderStatus(salesOrderDTO, purchaseOrderDTO);
      getOrderSolrWriterService().reCreateOrderSolrIndex(ServiceManager.getService(IConfigService.class).getShopById(purchaseOrderDTO.getShopId()), OrderTypes.PURCHASE, purchaseOrderDTO.getId());
      getOrderSolrWriterService().reCreateOrderSolrIndex(ServiceManager.getService(IConfigService.class).getShopById(salesOrderDTO.getShopId()), OrderTypes.SALE, salesOrderDTO.getId());

      model.addAttribute("salesOrderId", salesOrderDTO.getId());
      return ONLINE_SALES_ORDER;
    } catch (Exception e) {
      LOG.error("refuseSaleOrder 出错" + e.getMessage(), e);
    } finally {
      if (salesOrderId != null) {
        try {
          List<SalesOrderItem> salesOrderItemList = getTxnService().getSaleOrderItemListByOrderId(salesOrderId);
          Long[] productIds = new Long[salesOrderItemList.size()];
          for (int i = 0; i < salesOrderItemList.size(); i++) {
            productIds[i] = salesOrderItemList.get(i).getProductId();
          }
        } catch (Exception e) {
          LOG.error("initLackInfo 出错" + e.getMessage(), e);
        }
        BcgogoConcurrentController.release(ConcurrentScene.SALE, salesOrderId.toString());
      }
      if (purchaseOrderId != null) {
        BcgogoConcurrentController.release(ConcurrentScene.PURCHASE, purchaseOrderId.toString());
      }

    }
    return ONLINE_SALES_ORDER;
  }

  /**
   * 拒绝待办采购单
   *
   * @param model
   * @param request
   * @param salesOrderDTO
   * @return
   */
  @RequestMapping(params = "method=refusePendingPurchaseOrder")
  public String refusePendingPurchaseOrder(ModelMap model, HttpServletRequest request, SalesOrderDTO salesOrderDTO) {
    Long shopId = null;
    Result result = new Result();
    Long userId = WebUtil.getUserId(request);
    salesOrderDTO.setUserId(userId);
    Long purchaseOrderId = null;
    try {
      shopId = WebUtil.getShopId(request);
      purchaseOrderId = salesOrderDTO.getPurchaseOrderId();
      CustomerDTO customerDTO = null;
      PurchaseOrderDTO purchaseOrderDTO = null;
      SalesOrderDTO salesOrderDTOFromDB = getGoodSaleService().getSimpleSalesOrderByPurchaseOrderId(purchaseOrderId);
      if (salesOrderDTO != null && salesOrderDTO.getPurchaseOrderId() != null) {
        purchaseOrderDTO = getRfiTxnService().getPurchaseOrderDTOByIdAndSupplierShopId(purchaseOrderId, shopId);
      }

      result = checkRefusePendingPurchase(salesOrderDTO, purchaseOrderDTO);
      if (!result.isSuccess()) {
        model.addAttribute("result", result);
        if (salesOrderDTOFromDB != null && salesOrderDTOFromDB.getId() != null) {
          return toOnlineSalesOrder(model, request, salesOrderDTOFromDB.getIdStr());
        }
        return SHOW;
      }
      // 加锁失败
      if (!BcgogoConcurrentController.lock(ConcurrentScene.PURCHASE, purchaseOrderId.toString())) {
        result.setMsg("当前单据正在被操作，请稍候再试");
        model.addAttribute("result", result);
        return SHOW;
      }
      customerDTO = userService.getCustomerByCustomerShopIdAndShopId(shopId, purchaseOrderDTO.getShopId());
      if (customerDTO == null) {
        customerDTO = new CustomerDTO();
        ShopDTO customerShopDTO = configService.getShopById(purchaseOrderDTO.getShopId());
        customerDTO.fromCustomerShopDTO(customerShopDTO);
      }
      getGoodSaleService().refusePendingPurchaseOrder(salesOrderDTO, purchaseOrderDTO);

      //生成推送消息
      ServiceManager.getService(IOrderPushMessageService.class).createOrderPushMessageMessage(purchaseOrderDTO.getSupplierShopId(),
        purchaseOrderDTO.getShopId(), purchaseOrderDTO.getId(), PushMessageSourceType.PURCHASE_SELLER_REFUSED);

      //发送拒绝短信
      getSmsService().salesRefuseSMS(purchaseOrderDTO.getShopId(), shopId, purchaseOrderDTO.getReceiptNo(), customerDTO.getMobile());
      getSearchService().updateSearchOrderStatus(null, purchaseOrderDTO);
      getOrderSolrWriterService().reCreateOrderSolrIndex(ServiceManager.getService(IConfigService.class).getShopById(purchaseOrderDTO.getShopId()),
        OrderTypes.PURCHASE, purchaseOrderDTO.getId());

//      model.addAttribute("salesOrderId", salesOrderDTO.getId());
      return REDIRECT_ONLINE_LIST;
    } catch (Exception e) {
      LOG.error("refuseSaleOrder 出错" + e.getMessage(), e);
    } finally {
      if (purchaseOrderId != null) {
        BcgogoConcurrentController.release(ConcurrentScene.PURCHASE, purchaseOrderId.toString());
      }
    }
    return ONLINE_SALES_ORDER;
  }


  //todo 还需要补充
  private Result checkAcceptSaleOrder(SalesOrderDTO salesOrderDTO, CustomerDTO customerDTO, PurchaseOrderDTO purchaseOrderDTO) {
    Result result = new Result();
    if (salesOrderDTO == null) {
      result.setMsg("需要接受的订单不存在!");
      LOG.error(result.getMsg());
      result.setSuccess(false);
      result.setOperation(Result.Operation.ALERT.name());
      return result;
    }
    if (salesOrderDTO.getPurchaseOrderId() == null) {
      result.setMsg("当前订单没有关联的采购单无法接受!");
      LOG.error(result.getMsg());
      result.setSuccess(false);
      result.setOperation(Result.Operation.ALERT.name());
      return result;
    }
    if (!OrderStatus.PENDING.equals(salesOrderDTO.getStatus())) {
      result.setMsg("当前销售单的状态是:" + salesOrderDTO.getStatus().getName() + "，无法被接受！");
      LOG.error(result.getMsg());
      result.setSuccess(false);
      result.setOperation(Result.Operation.ALERT.name());
      return result;
    }
    result.setSuccess(true);
    return result;
  }

  private Result checkAcceptPendingPurchaseOrder(SalesOrderDTO salesOrderDTO, PurchaseOrderDTO purchaseOrderDTO) {
    Result result = new Result();
    if (purchaseOrderDTO == null) {
      result.setMsg("需要接受的采购订单订单不存在!");
      LOG.error(result.getMsg());
      result.setSuccess(false);
      result.setOperation(Result.Operation.ALERT.name());
      return result;
    }

    if (!OrderStatus.SELLER_PENDING.equals(purchaseOrderDTO.getStatus())) {
      result.setMsg("当前采购订单已经被处理，无法被接受！");
      LOG.error(result.getMsg());
      result.setSuccess(false);
      result.setOperation(Result.Operation.ALERT.name());
      return result;
    }
    if (salesOrderDTO != null && salesOrderDTO.getId() != null) {
      result.setMsg("当前采购订单已经被处理，无法被接受！");
      LOG.error(result.getMsg());
      result.setSuccess(false);
      result.setOperation(Result.Operation.ALERT.name());
      return result;
    }
    result.setSuccess(true);
    return result;
  }


  private Result checkRefuseSaleOrder(SalesOrderDTO salesOrderDTO, CustomerDTO customerDTO, PurchaseOrderDTO purchaseOrderDTO) {
    Result result = new Result();
    if (salesOrderDTO == null) {
      result.setMsg("被拒绝的订单不存在!");
      LOG.error(result.getMsg());
      result.setSuccess(false);
      return result;
    }
    if (salesOrderDTO.getPurchaseOrderId() == null) {
      result.setMsg("当前订单没有关联的采购单无法拒绝!");
      LOG.error(result.getMsg());
      result.setSuccess(false);
      return result;
    }
    if (!OrderStatus.PENDING.equals(salesOrderDTO.getStatus())) {
      result.setMsg("当前销售单的状态是：" + salesOrderDTO.getStatus().getName() + "，无法被拒绝！");
      LOG.error(result.getMsg());
      result.setSuccess(false);
      return result;
    }
    result.setSuccess(true);
    return result;
  }

  private Result checkRefusePendingPurchase(SalesOrderDTO salesOrderDTO, PurchaseOrderDTO purchaseOrderDTO) {
    Result result = new Result();
    if (salesOrderDTO == null || purchaseOrderDTO == null) {
      result.setMsg("被拒绝的订单不存在!");
      LOG.error(result.getMsg());
      result.setSuccess(false);
      return result;
    }
    if (salesOrderDTO.getId() != null || !OrderStatus.SELLER_PENDING.equals(purchaseOrderDTO.getStatus())) {
      result.setMsg("当前订单已经被处理，无法拒绝!");
      LOG.error(result.getMsg());
      result.setSuccess(false);
      return result;
    }
    result.setSuccess(true);
    return result;
  }

  private Result checkRepealSaleOrder(SalesOrderDTO salesOrderDTO, PurchaseOrderDTO purchaseOrderDTO) {
    Result result = new Result();
    if (salesOrderDTO == null) {
      result.setMsg("被拒绝的订单不存在!");
      result.setSuccess(false);
      return result;
    }

    //卖家已入库 销售单备货中和已发货 不可以作废  其他情况可以
    if (salesOrderDTO.getPurchaseOrderId() != null && purchaseOrderDTO != null) {
      if (OrderStatus.PURCHASE_ORDER_DONE.equals(purchaseOrderDTO.getStatus())
        && (OrderStatus.STOCKING.equals(salesOrderDTO.getStatus()) || OrderStatus.DISPATCH.equals(salesOrderDTO.getStatus()))) {
        result.setMsg("买家已入库，不能终止销售！");
        result.setOperation(Result.Operation.ALERT.name());
        result.setSuccess(false);
        return result;
      }
    }
    result.setSuccess(true);
    return result;
  }

  //todo 还需要补充
  private Result checkDispatchSaleOrder(SalesOrderDTO salesOrderDTO) throws Exception {
    Result result = new Result();
    if (salesOrderDTO == null) {
      result.setMsg("需要接受的订单不存在!");
      LOG.error(result.getMsg());
      result.setSuccess(false);
      return result;
    }
    if (salesOrderDTO.getPurchaseOrderId() == null) {
      result.setMsg("当前订单没有关联的采购单无法接受!");
      LOG.error(result.getMsg());
      result.setSuccess(false);
      return result;
    }
    if (!OrderStatus.STOCKING.equals(salesOrderDTO.getStatus())) {
      result.setMsg("当前销售单的状态是" + salesOrderDTO.getStatus().getName() + "，无法被接受！");
      LOG.error(result.getMsg());
      result.setSuccess(false);
      return result;
    }
    //校验是否库存是否大于缺料数 是否可以发货
    Set<Long> productIds = new HashSet<Long>();
    StringBuffer productMsg = new StringBuffer();
    boolean isLack = false;
    if (salesOrderDTO != null && !ArrayUtils.isEmpty(salesOrderDTO.getItemDTOs())) {
      for (SalesOrderItemDTO salesOrderItemDTO : salesOrderDTO.getItemDTOs()) {
        productIds.add(salesOrderItemDTO.getProductId());
      }
      if (BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(salesOrderDTO.getShopVersionId())) {
        isLack = checkInventoryLackByStorehouse(salesOrderDTO, productIds, productMsg, isLack);
        List<Long> productIdList = new ArrayList<Long>();
        productIdList.addAll(productIds);
        if (isLack) {//如果缺料  校验是否可以调拨
          if (getInventoryService().checkBatchProductInventoryInOtherStorehouse(salesOrderDTO.getShopId(), salesOrderDTO, productIdList)) {
            return new Result(ValidatorConstant.PRODUCT_STOREHOUSE_INVENTORY_LACK, false, Result.Operation.CONFIRM_ALLOCATE_RECORD.getValue(), null);
          }
        }
      } else {
        isLack = checkInventoryLack(salesOrderDTO, productIds, productMsg, isLack);
      }
    }
    if (isLack) {
      productMsg.append(ValidatorConstant.NOT_ENOUGH_INVENTORY_TO_DISPATCH);
      result.setSuccess(false);
      result.setMsg(productMsg.toString());
      result.setOperation(Result.Operation.ALERT_SALE_LACK.name());
      return result;
    }
    result.setSuccess(true);
    return result;
  }

  private boolean checkInventoryLackByStorehouse(SalesOrderDTO salesOrderDTO, Set<Long> productIds, StringBuffer productMsg, boolean lack) throws Exception {
    Set<Long> lackProductSet = new HashSet<Long>();
    Map<Long, ProductLocalInfoDTO> productLocalInfoDTOMap = new HashMap<Long, ProductLocalInfoDTO>();
    Map<Long, StoreHouseInventoryDTO> storeHouseInventoryDTOMap = new HashMap<Long, StoreHouseInventoryDTO>();
    IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
    if (CollectionUtils.isNotEmpty(productIds)) {
      storeHouseInventoryDTOMap = storeHouseService.getStoreHouseInventoryDTOMapByStorehouseAndProductIds(salesOrderDTO.getShopId(), salesOrderDTO.getStorehouseId(), productIds.toArray(new Long[productIds.size()]));
      productLocalInfoDTOMap = getProductService().getProductLocalInfoMap(salesOrderDTO.getShopId(), productIds.toArray(new Long[productIds.size()]));
    }
    for (SalesOrderItemDTO salesOrderItemDTO : salesOrderDTO.getItemDTOs()) {
      if (salesOrderItemDTO.getProductId() == null) {
        continue;
      }
      ProductLocalInfoDTO productLocalInfoDTO = productLocalInfoDTOMap.get(salesOrderItemDTO.getProductId());
      StoreHouseInventoryDTO storeHouseInventoryDTO = storeHouseInventoryDTOMap.get(salesOrderItemDTO.getProductId());
      double lackAmount = NumberUtil.doubleVal(salesOrderItemDTO.getAmount()) - NumberUtil.doubleVal(salesOrderItemDTO.getReserved());
      if (lackAmount > 0.0001) {
        if (UnitUtil.isStorageUnit(salesOrderItemDTO.getUnit(), productLocalInfoDTO)) {
          lackAmount = lackAmount * productLocalInfoDTO.getRate();
        }
        if (storeHouseInventoryDTO == null || storeHouseInventoryDTO.getAmount() < lackAmount) {
          lack = true;
          lackProductSet.add(salesOrderItemDTO.getProductId());
          if (StringUtils.isEmpty(productMsg.toString())) {
            productMsg.append(salesOrderItemDTO.getProductName());
          } else {
            productMsg.append(",").append(salesOrderItemDTO.getProductName());
          }
        }
      }
    }
    productIds.clear();
    productIds.addAll(lackProductSet);
    return lack;
  }

  private boolean checkInventoryLack(SalesOrderDTO salesOrderDTO, Set<Long> productIds, StringBuffer productMsg, boolean lack) {
    Map<Long, InventoryDTO> inventoryDTOMap = new HashMap<Long, InventoryDTO>();
    Map<Long, ProductLocalInfoDTO> productLocalInfoDTOMap = new HashMap<Long, ProductLocalInfoDTO>();
    if (CollectionUtils.isNotEmpty(productIds)) {
      inventoryDTOMap = getInventoryService().getInventoryDTOMap(salesOrderDTO.getShopId(), productIds);
      productLocalInfoDTOMap = getProductService().getProductLocalInfoMap(salesOrderDTO.getShopId(), productIds.toArray(new Long[productIds.size()]));
    }
    for (SalesOrderItemDTO salesOrderItemDTO : salesOrderDTO.getItemDTOs()) {
      if (salesOrderItemDTO.getProductId() == null) {
        continue;
      }
      ProductLocalInfoDTO productLocalInfoDTO = productLocalInfoDTOMap.get(salesOrderItemDTO.getProductId());
      InventoryDTO inventoryDTO = inventoryDTOMap.get(salesOrderItemDTO.getProductId());
      double lackAmount = NumberUtil.doubleVal(salesOrderItemDTO.getAmount()) - NumberUtil.doubleVal(salesOrderItemDTO.getReserved());
      if (lackAmount > 0.0001) {
        if (UnitUtil.isStorageUnit(salesOrderItemDTO.getUnit(), productLocalInfoDTO)) {
          lackAmount = lackAmount * productLocalInfoDTO.getRate();
        }
        if (inventoryDTO == null || inventoryDTO.getAmount() < lackAmount) {
          lack = true;
          if (StringUtils.isEmpty(productMsg.toString())) {
            productMsg.append(salesOrderItemDTO.getProductName());
          } else {
            productMsg.append(",").append(salesOrderItemDTO.getProductName());
          }

        }
      }
    }
    return lack;
  }

  /**
   * 在线销售单结算
   * 1.校验单据状态 库存状态 单据加锁
   * 2.执行结算逻辑 客户统计 更新单据状态
   * 3.更新统计信息
   *
   * @param model
   * @param request
   * @param accountInfoDTO
   * @return
   */
  @RequestMapping(params = "method=saleOrderSettle")
  @ResponseBody
  public Object saleOrderSettle(ModelMap model, HttpServletRequest request, AccountInfoDTO accountInfoDTO) {
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    if (shopId == null) {
      return "/";
    }
    if (accountInfoDTO == null || accountInfoDTO.getOrderId() == null) {
      return new Result(ValidatorConstant.ORDER_IS_NULL_MSG, false);
    }
    try {
      Long userId = WebUtil.getUserId(request);
      String username = WebUtil.getUserName(request);
      ITxnService txnService = ServiceManager.getService(ITxnService.class);
      IGoodSaleService goodSaleService = ServiceManager.getService(IGoodSaleService.class);
      ICustomerService customerService = ServiceManager.getService(ICustomerService.class);
      Long orderId = accountInfoDTO.getOrderId();
      SalesOrderDTO salesOrderDTO = txnService.getSalesOrder(orderId);
      if (salesOrderDTO == null || salesOrderDTO.getId() == null) {
        return new Result(ValidatorConstant.ORDER_IS_NULL_MSG, false);
      }
      // 加锁失败
      if (!(BcgogoConcurrentController.lock(ConcurrentScene.SALE, salesOrderDTO.getId().toString()))) {
        return new Result(ValidatorConstant.ORDER_IS_NULL_MSG, false);
      }
      salesOrderDTO.setUserId(userId);
      salesOrderDTO.setUserName(username);

      String checkResult = goodSaleService.checkSalesOrderBeforeSettle(shopId, salesOrderDTO, null);

      //判断单据是否符合结算逻辑
      if (!StringUtils.isEmpty(checkResult)) {
        model.addAttribute("resultMsg", "failure");
      }

      //生成receivable reception_record 更改单据的状态
      goodSaleService.saveReceivableDebtReceptionRecord(salesOrderDTO, accountInfoDTO);
      getSalesOrder(model, request, String.valueOf(orderId));
      salesOrderDTO = (SalesOrderDTO) model.get("salesOrderDTO");

      //更新客户信息
      customerService.updateVehicleAndCustomer(salesOrderDTO, shopId, salesOrderDTO.getContact(), null);

      //保存单据状态改变日志
      IOrderStatusChangeLogService orderStatusChangeLogService = ServiceManager.getService(IOrderStatusChangeLogService.class);
      orderStatusChangeLogService.saveOrderStatusChangeLog(new OrderStatusChangeLogDTO(salesOrderDTO.getShopId(), salesOrderDTO.getUserId(), salesOrderDTO.getStatus(), OrderStatus.DISPATCH, salesOrderDTO.getId(), OrderTypes.SALE));

      //保存操作日志
      IOperationLogService operationLogService = ServiceManager.getService(IOperationLogService.class);
      ServiceManager.getService(ITxnService.class).saveOperationLogTxnService(new OperationLogDTO(salesOrderDTO.getShopId(), salesOrderDTO.getUserId(), salesOrderDTO.getId(), ObjectTypes.SALE_ORDER, OperationTypes.SETTLE));

      //add by WLF 更新缓存中待办销售单的数量
      List<Long> customerIdList = ServiceManager.getService(ICustomerService.class).getRelatedCustomerIdListByShopId(shopId);
      getTxnService().updateTodoOrderCountInMemcacheByTypeAndShopId(RemindEventType.TODO_SALE_ORDER, shopId, customerIdList);


      //在线销售出入库打通
      ServiceManager.getService(IProductOutStorageService.class).productThroughByOrder(salesOrderDTO, OrderTypes.SALE, salesOrderDTO.getStatus());

      BcgogoEventPublisher bcgogoEventPublisher = new BcgogoEventPublisher();
      salesOrderDTO.setCurrentUsedProductDTOList();
      salesOrderDTO.setCurrentUsedVehicleDTOList();
      SaleOrderSavedEvent saleOrderSavedEvent = new SaleOrderSavedEvent(salesOrderDTO);
      String isRunThread = request.getParameter("isRunThread");
      if (!"noRun".equals(isRunThread)) {
        bcgogoEventPublisher.publisherSaleOrderSaved(saleOrderSavedEvent);
      }
      saleOrderSavedEvent.setMainFlag(true);
      request.setAttribute("UNIT_TEST", saleOrderSavedEvent);
      //对客户的商品进行reindex
      goodSaleService.reindexCustomerProductSolr(salesOrderDTO);

    } catch (Exception e) {
      LOG.error("GoodSaleController.saleOrderSettle 销售单结算失败");
      LOG.error(e.getMessage(), e);
      return new Result(ValidatorConstant.ORDER_SETTLED_FAILURE, true);


    } finally {
      BcgogoConcurrentController.release(ConcurrentScene.SALE, accountInfoDTO.getOrderId().toString());
    }
    return new Result(ValidatorConstant.ORDER_SETTLED_SUCCESS, true);
  }

  /**
   * 点击结算/欠款结算 跳转到结算窗口
   *
   * @param model
   * @param request
   * @return
   * @throws Exception
   */
  @RequestMapping(params = "method=supplierSalesAccount")
  public String supplierSalesAccount(ModelMap model, HttpServletRequest request, String status) throws Exception {
    try {
      Long shopId = WebUtil.getShopId(request);
      if (shopId == null) {
        return "/";
      }
      String orderIdStr = request.getParameter("orderId");
      if (StringUtils.isEmpty(orderIdStr)) {
        return REDIRECT_CREATE;
      }
      ITxnService txnService = ServiceManager.getService(ITxnService.class);

      getSalesOrder(model, request, String.valueOf(orderIdStr));
      SalesOrderDTO salesOrderDTO = (SalesOrderDTO) model.get("salesOrderDTO");
      if (salesOrderDTO != null && salesOrderDTO.getShopId().equals(shopId)) {
        model.addAttribute("salesOrderDTO", salesOrderDTO);
      } else {
        return REDIRECT_CREATE;
      }
      // 欠款结算
      if (StringUtils.isNotBlank(status) && status.equals(OrderStatus.SALE_DONE.toString()) && (null != salesOrderDTO.getDebt() && salesOrderDTO.getDebt() > 0)) {
        ReceivableDTO receivableDTO = txnService.getReceivableDTOByShopIdAndOrderId(shopId, salesOrderDTO.getId());
        DebtDTO debtDTO = txnService.getDebtByShopIdAndCustomerIdAndOrderId(shopId, salesOrderDTO.getCustomerId(), salesOrderDTO.getId());
        if (receivableDTO == null || debtDTO == null) {
          LOG.error("销售单欠款结算准备数据时出错。receivable 或 debt 不存在。shopID:{}, saleOrderID:{}", shopId, orderIdStr);
        }
        model.addAttribute("orderTotal", salesOrderDTO.getTotal());

        model.addAttribute("payedAmount", receivableDTO == null ? 0 : receivableDTO.getSettledAmount());
        model.addAttribute("receivableId", receivableDTO == null ? "" : receivableDTO.getId());
        salesOrderDTO.setTotal(receivableDTO == null ? salesOrderDTO.getTotal() : receivableDTO.getDebt());
        model.addAttribute("debtId", debtDTO == null ? "" : debtDTO.getId());
      }
      return "/txn/orderAccount/supplierSalesAccount";
    } catch (Exception e) {
      LOG.error("GoodSaleController.java method=supplierSalesAccount");
      LOG.error(e.getMessage(), e);
    }
    return REDIRECT_CREATE;
  }

  /**
   * 单据结算前进行校验是否符合结算
   *
   * @param request
   * @param orderId
   * @return
   */
  @RequestMapping(params = "method=validateSupplierSalesOrder")
  @ResponseBody
  public Object validateSupplierSalesOrder(HttpServletRequest request, Long orderId, String status) {
    try {

      Long shopId = WebUtil.getShopId(request);
      if (shopId == null) {
        return "/";
      }

      ITxnService txnService = ServiceManager.getService(ITxnService.class);
      IGoodSaleService goodSaleService = ServiceManager.getService(IGoodSaleService.class);
      if (orderId == null) {
        return new Result(ValidatorConstant.ORDER_IS_NULL_MSG, false);
      }

      SalesOrderDTO salesOrderDTO = txnService.getSalesOrder(orderId);
      if (salesOrderDTO == null || !salesOrderDTO.getShopId().equals(shopId)) {
        return new Result(ValidatorConstant.ORDER_IS_NULL_MSG, false);
      }

      String checkResult = goodSaleService.checkSalesOrderBeforeSettle(shopId, salesOrderDTO, status);
      if (StringUtils.isEmpty(checkResult)) {
        return new Result(ValidatorConstant.ORDER_STATUS_CORRECT, true);
      } else {
        return new Result(checkResult, false);
      }
    } catch (Exception e) {
      LOG.error("validateSupplierSalesOrder 验证出错OrderId:{}" + e.getMessage(), orderId, e);
      return new Result(ValidatorConstant.REQUEST_ERROR_MSG, false);
    }
  }


}
