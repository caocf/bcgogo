package com.bcgogo.txn;

import com.bcgogo.common.Result;
import com.bcgogo.common.WebUtil;
import com.bcgogo.config.cache.BcgogoConcurrentController;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.enums.*;
import com.bcgogo.enums.txn.pushMessage.PushMessageSourceType;
import com.bcgogo.product.cache.ProductUnitCache;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.stat.dto.SupplierRecordDTO;
import com.bcgogo.txn.bcgogoListener.orderEvent.PurchaseReturnSavedEvent;
import com.bcgogo.txn.bcgogoListener.publisher.BcgogoEventPublisher;
import com.bcgogo.txn.dto.*;
import com.bcgogo.txn.dto.StatementAccount.OrderDebtType;
import com.bcgogo.txn.service.*;
import com.bcgogo.txn.service.productThrough.IProductOutStorageService;
import com.bcgogo.txn.service.productThrough.IProductThroughService;
import com.bcgogo.txn.service.pushMessage.IPushMessageService;
import com.bcgogo.user.dto.CustomerDTO;
import com.bcgogo.user.dto.CustomerRecordDTO;
import com.bcgogo.user.dto.SupplierDTO;
import com.bcgogo.user.service.ISupplierService;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.user.service.utils.BcgogoShopLogicResourceUtils;
import com.bcgogo.utils.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.StringResourceLoader;
import org.apache.velocity.runtime.resource.util.StringResourceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: zyj
 * Date: 12-2-15
 * Time: 下午3:00
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/goodsReturn.do")
public class GoodsReturnController {
  private static final Logger LOG = LoggerFactory.getLogger(GoodsReturnController.class);
  private static final String MODEL_SETTLEMENTTYPE_LIST = "settlementTypeList";
  private static final String MODEL_INVOICECATEGORY_LIST = "invoiceCategoryList";
  private static final String REDIRECT_SHOW = "redirect:goodsReturn.do?method=showReturnStorageByPurchaseReturnId";
  private static final String REDIRECT_MODIFY = "redirect:onlineReturn.do?method=modifyReturnStorage";

  @Autowired
  private IPurchaseReturnService purchaseReturnService;

  public void setPurchaseReturnService(PurchaseReturnService purchaseReturnService) {
    this.purchaseReturnService = purchaseReturnService;
  }

  /**
   * 生成退货单
   *
   * @param model
   * @param request
   * @return
   */
  @RequestMapping(params = "method=createReturnStorage")
  public String createReturnStorage(ModelMap model, HttpServletRequest request) {
    Long shopId = null;
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    try {
      shopId = WebUtil.getShopId(request);
      PurchaseReturnDTO purchaseReturnDTO = purchaseReturnService.createPurchaseReturnDTO(shopId, null);
      purchaseReturnDTO.setEditor((String) request.getSession().getAttribute("userName"));
      purchaseReturnDTO.setEditorId((Long) request.getSession().getAttribute("userId"));
      purchaseReturnDTO.setShopId(shopId);

      if ("noId".equals(request.getParameter("cancle"))) {
        purchaseReturnDTO.setReceiptNo(request.getParameter("receiptNo"));
      }

      PurchaseReturnItemDTO purchaseReturnItemDTO = new PurchaseReturnItemDTO();
      PurchaseReturnItemDTO[] itemDTOs = {purchaseReturnItemDTO};
      purchaseReturnDTO.setItemDTOs(itemDTOs);

      if(BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(WebUtil.getShopVersionId(request))){
        IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
        List<StoreHouseDTO> storeHouseDTOList = storeHouseService.getAllStoreHousesByShopId(shopId);
        model.addAttribute("storeHouseDTOList", storeHouseDTOList);//select 选项
        if(CollectionUtils.isNotEmpty(storeHouseDTOList) && purchaseReturnDTO.getStorehouseId()==null){
          if(storeHouseDTOList.size()==1){
            purchaseReturnDTO.setStorehouseId(storeHouseDTOList.get(0).getId());
          }
        }
        InventoryService inventoryService = ServiceManager.getService(InventoryService.class);
        //更新库存 根据仓库
        inventoryService.updateItemDTOInventoryAmountByStorehouse(shopId,purchaseReturnDTO.getStorehouseId(), purchaseReturnDTO);
      }
      purchaseReturnDTO.setVestDateStr(DateUtil.dateLongToStr(purchaseReturnDTO.getVestDate(),DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN));
      model.addAttribute("purchaseReturnDTO", purchaseReturnDTO);
      this.setSupplierDropDownInfo(model, request);
      model.addAttribute("wholesalerVersion", ConfigUtils.isWholesalerVersion(WebUtil.getShopVersionId(request)));
    } catch (Exception e) {
      LOG.error("goodsReturn.do,method=createReturnStorage error,shopId :{}", shopId);
      LOG.error(e.getMessage(), e);
    }
    return "/txn/returnsStorage";
  }



  @RequestMapping(params = "method=saveReturnStorage")
  public String saveReturnStorage(ModelMap model, HttpServletRequest request, PurchaseReturnDTO purchaseReturnDTO) {
    RFITxnService rfiTxnService = ServiceManager.getService(RFITxnService.class);
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    ISupplierPayableService supplierPayableService = ServiceManager.getService(ISupplierPayableService.class);
    try {
      Long shopId = WebUtil.getShopId(request);
      Long userId = WebUtil.getUserId(request);
      Long shopVersionId = WebUtil.getShopVersionId(request);
      String username = WebUtil.getUserName(request);

      if (shopId == null || userId==null) {
        return "/txn/returnsStorage";
      }
      if(purchaseReturnDTO.getSupplierId()==null && purchaseReturnDTO.getSupplierShopId()!=null){
        purchaseReturnDTO.setSupplierShopId(null);
      }
      purchaseReturnDTO.setUserId(userId);
      purchaseReturnDTO.setUserName(username);

      //去掉空行
      if (purchaseReturnDTO.getItemDTOs() != null) {
        PurchaseReturnItemDTO[] purchaseReturnItemDTOs = purchaseReturnDTO.getItemDTOs();
        List<PurchaseReturnItemDTO> purchaseReturnItemLists = new ArrayList<PurchaseReturnItemDTO>();
        for (int i = 0; i < purchaseReturnItemDTOs.length; i++) {
          if (purchaseReturnItemDTOs[i].getProductId() != null && StringUtils.isNotBlank(purchaseReturnItemDTOs[i].getProductName())) {
            purchaseReturnItemLists.add(purchaseReturnItemDTOs[i]);
          }
        }
        if (CollectionUtils.isNotEmpty(purchaseReturnItemLists)) {
          purchaseReturnDTO.setItemDTOs(purchaseReturnItemLists.toArray(new PurchaseReturnItemDTO[purchaseReturnItemLists.size()]));
        }
      }
      purchaseReturnDTO.setShopId(shopId);
      purchaseReturnDTO.setUserId(userId);
      purchaseReturnDTO.setInventoryLimitDTO(new InventoryLimitDTO());
      purchaseReturnDTO.getInventoryLimitDTO().setShopId(shopId);
      //退款时间
      purchaseReturnDTO.setEditDate(System.currentTimeMillis());
      purchaseReturnDTO.setEditDateStr(DateUtil.convertDateLongToDateString("yyyy-MM-dd", purchaseReturnDTO.getEditDate()));
      //归属时间
      purchaseReturnDTO.setVestDate(DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN, purchaseReturnDTO.getVestDateStr()));
      if (StringUtils.isBlank(purchaseReturnDTO.getReceiptNo())) {
        purchaseReturnDTO.setReceiptNo(txnService.getReceiptNo(shopId, OrderTypes.RETURN, null));
      }
      //todo 如果itemDTO 中的商品新增了单位，更新产品单位
      //更新单据中之前没有单位的产品数据
      txnService.updateProductUnit(purchaseReturnDTO);

      purchaseReturnDTO = rfiTxnService.savePurchaseReturn(shopId,shopVersionId,purchaseReturnDTO);

      //更新库存告警信息
      ServiceManager.getService(IInventoryService.class).updateMemocacheLimitByInventoryLimitDTO(shopId, purchaseReturnDTO.getInventoryLimitDTO());

      //todo:供应商退款
      purchaseReturnDTO.setCash(purchaseReturnDTO.getCash() == null ? 0d : purchaseReturnDTO.getCash());
      purchaseReturnDTO.setDepositAmount(purchaseReturnDTO.getDepositAmount() == null ? 0d : purchaseReturnDTO.getDepositAmount());
      purchaseReturnDTO.setStrikeAmount(purchaseReturnDTO.getStrikeAmount() == null ? 0d : purchaseReturnDTO.getStrikeAmount());
      if ("支票号".equals(purchaseReturnDTO.getBankCheckNo())) {
        purchaseReturnDTO.setBankCheckNo("");
      }
      supplierPayableService.returnPayable(purchaseReturnDTO);
      model.addAttribute("purchaseReturnDTO", purchaseReturnDTO);
      this.setSupplierDropDownInfo(model, request);

      ServiceManager.getService(IProductHistoryService.class).saveProductHistoryForOrder(shopId,purchaseReturnDTO);

      if (purchaseReturnDTO.getSupplierId() != null) {
        SupplierDTO supplierDTO = ServiceManager.getService(IUserService.class).getSupplierById(purchaseReturnDTO.getSupplierId());
        if (supplierDTO.getCustomerId() != null) {
          //同时更新客户的信息
          CustomerDTO customerDTO = ServiceManager.getService(IUserService.class).getCustomerById(supplierDTO.getCustomerId());
          customerDTO.fromSupplierDTO(supplierDTO);
          ServiceManager.getService(IUserService.class).updateCustomer(customerDTO);
          CustomerRecordDTO customerRecordDTO = ServiceManager.getService(IUserService.class).getCustomerRecordByCustomerId(supplierDTO.getCustomerId()).get(0);
          customerRecordDTO.fromCustomerDTO(customerDTO);
          ServiceManager.getService(IUserService.class).updateCustomerRecord(customerRecordDTO);
        }
      }
        //每新增一张单据，就要将同一个供应商里面的欠款提醒的状态改为未提醒
        if (purchaseReturnDTO.getSupplierId() != null) {
            SupplierDTO supplierDTO = ServiceManager.getService(IUserService.class).getSupplierById(purchaseReturnDTO.getSupplierId());
            if (supplierDTO.getCustomerId() != null) {
                ServiceManager.getService(ITxnService.class).updateRemindEventStatus(purchaseReturnDTO.getShopId(),supplierDTO.getCustomerId(),"customer");
            } else {
                ServiceManager.getService(ITxnService.class).updateRemindEventStatus(purchaseReturnDTO.getShopId(),purchaseReturnDTO.getSupplierId(),"supplier");
            }
        }

      //线程做orderindex
      BcgogoEventPublisher bcgogoEventPublisher = new BcgogoEventPublisher();
      PurchaseReturnSavedEvent purchaseReturnSavedEvent = new PurchaseReturnSavedEvent(purchaseReturnDTO);
      bcgogoEventPublisher.publisherPurchaseReturnSaved(purchaseReturnSavedEvent);
      request.setAttribute("UNIT_TEST", purchaseReturnSavedEvent); //单元测试
      model.addAttribute("purchaseReturnId", purchaseReturnDTO.getId());
    } catch (Exception e) {
      LOG.debug("/goodsReturn.do");
      LOG.debug("method=saveReturnStorage");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.debug(e.getMessage(), e);
      request.setAttribute("errorMsg", "系统出错！");
      return "/txn/returnsStorage";
    }
    return REDIRECT_SHOW+"&print="+purchaseReturnDTO.getPrint();
  }

  private void updateOtherPayable(HttpServletRequest request, PurchaseReturnDTO purchaseReturnDTO, ISupplierPayableService supplierPayableService) throws ParseException {
    double strike = purchaseReturnDTO.getStrikeAmount().doubleValue();
    List<PayableDTO> payableDTOList = supplierPayableService.searchPayable(WebUtil.getShopId(request), purchaseReturnDTO.getSupplierId());

    Double sumPayable = supplierPayableService.getSumPayableBySupplierId(purchaseReturnDTO.getSupplierId(), WebUtil.getShopId(request),OrderDebtType.SUPPLIER_DEBT_PAYABLE).get(0);

    PayableHistoryDTO payableHistoryDTO = new PayableHistoryDTO();

    payableHistoryDTO.setShopId(WebUtil.getShopId(request));
    payableHistoryDTO.setSupplierId(purchaseReturnDTO.getSupplierId());
    payableHistoryDTO.setDeduction(0D);
    payableHistoryDTO.setCreditAmount(sumPayable - strike);
    payableHistoryDTO.setCash(0D);
    payableHistoryDTO.setBankCardAmount(0D);
    payableHistoryDTO.setCheckAmount(0D);
    payableHistoryDTO.setActuallyPaid(strike);
    payableHistoryDTO.setDepositAmount(0D);
    payableHistoryDTO.setStrikeAmount(strike);

    //添加付款历史
    payableHistoryDTO = supplierPayableService.saveOrUpdatePayableHistory(payableHistoryDTO);
    /*付款给供应商进行结算*/
    payableHistoryDTO.setPurchaseReturnId(purchaseReturnDTO.getId());

    for (PayableDTO payableDTO : payableDTOList) {

      supplierPayableService.paidByStrikeAmount(payableHistoryDTO, payableDTO, PaymentTypes.INVENTORY_DEBT);
      if (payableHistoryDTO.getDeductionAndActuallyPaid() == 0) break;//实付与扣款之和为0停止付款
      if (payableDTO.getCreditAmount() == 0) continue;//应付款为0，跳到下一张单据
    }
  }


  @RequestMapping(params = "method=printReturnStorageOrder")
  public void printReturnStorageOrder(ModelMap model, HttpServletRequest request, HttpServletResponse response, String purchaseReturnId) throws Exception {
    Long shopId = WebUtil.getShopId(request);
    purchaseReturnService.getGoodsReturnInfo(model, shopId, purchaseReturnId);
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    IPrintService printService = ServiceManager.getService(IPrintService.class);
    try {
      PrintTemplateDTO printTemplateDTO = printService.getSinglePrintTemplateDTOByShopIdAndType(WebUtil.getShopId(request), OrderTypes.RETURN);
      PurchaseReturnDTO purchaseReturnDTO = (PurchaseReturnDTO) model.get("purchaseReturnDTO");
      String storeManagerMobile = (String) model.get("storeManagerMobile");
      PrintWriter out = response.getWriter();
      response.setContentType("text/html");
      response.setCharacterEncoding("UTF-8");
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
        String myTemplateName = "purchaseReturnPrint" + String.valueOf(WebUtil.getShopId(request));
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
        context.put("purchaseReturnDTO", purchaseReturnDTO);
        context.put("storeManagerMobile", storeManagerMobile);
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
      LOG.debug("id:" + purchaseReturnId);
      WebUtil.reThrow(LOG, e);
    }
  }


  @RequestMapping(params = "method=getSupplierDetailInfo")
  public void getSupplierDetailInfo(ModelMap model, HttpServletRequest request, HttpServletResponse response) {
    try {

      String supplierIdStr = request.getParameter("supplierId");
      Long shopId = WebUtil.getShopId(request);
      SupplierDTO supplierDTO = null;
      if (supplierIdStr != null && !"".equals(supplierIdStr)) {
        Long supplierId = Long.valueOf(supplierIdStr);
        ISupplierService supplierService = ServiceManager.getService(ISupplierService.class);
        supplierDTO = supplierService.getSupplierById(supplierId,shopId);
      }

      String jsonStr = null;
      jsonStr = JsonUtil.objectToJson(supplierDTO);
      PrintWriter writer = response.getWriter();
      writer.write(jsonStr);
      writer.close();
    } catch (Exception e) {
      LOG.debug("/goodsReturn.do");
      LOG.debug("method=getSupplierDetailInfo");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
    }

  }

  @RequestMapping(params = "method=getReturnStorageByPurchaseReturnNo")
  @ResponseBody
  public Object getReturnStorageByPurchaseReturnNo(ModelMap model, HttpServletRequest request, HttpServletResponse response) {
    try {
      Long shopId = WebUtil.getShopId(request);
      String no = request.getParameter("no");
      PurchaseReturnDTO purchaseReturnDTO = null;
      if (no != null && !"".equals(no)) {
        RFITxnService rfiTxnService = ServiceManager.getService(RFITxnService.class);
        purchaseReturnDTO = rfiTxnService.getPurchaseReturnDTOByPurchaseReturnNo(shopId, no);

      }
      return purchaseReturnDTO;
    } catch (Exception e) {
      LOG.debug("/goodsReturn.do");
      LOG.debug("method=getReturnStorageByPurchaseReturnNo");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
    }
    return null;

  }

  @RequestMapping(params = "method=createReturnStorageByProductId")
  public String createReturnStorageByProductId(ModelMap model, HttpServletRequest request, HttpServletResponse response) {
    Long shopId = null;
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    ISupplierPayableService supplierPayableService = ServiceManager.getService(ISupplierPayableService.class);
    try {

      shopId = WebUtil.getShopId(request);
      String productIds = request.getParameter("productIds");
      String supplierId = request.getParameter("supplierId");

      //将产品ID字符串解析成产品ID数组
      Long[] productIdArray = null;
      if (productIds != null) {
        String[] productIdStrArray = productIds.split(",");
        productIdArray = new Long[productIdStrArray.length];
        for (int i = 0; i < productIdStrArray.length; i++) {
          productIdArray[i] = Long.valueOf(productIdStrArray[i]);
        }
      }

      PurchaseReturnDTO purchaseReturnDTO = purchaseReturnService.createPurchaseReturnDTOByProductIds(shopId, productIdArray);
      purchaseReturnDTO.setEditor((String) request.getSession().getAttribute("userName"));
      purchaseReturnDTO.setEditorId((Long) request.getSession().getAttribute("userId"));
      purchaseReturnDTO.setShopId(shopId);

      SupplierDTO supplierDTO = null;
      //获取所有产品共同的供应商
      if (productIdArray != null) {
        IProductThroughService productThroughService = ServiceManager.getService(IProductThroughService.class);
        List<SupplierDTO> supplierDTOList = productThroughService.getCommonSupplierByProductIds(shopId,productIdArray);
        if (CollectionUtils.isNotEmpty(supplierDTOList)) {
          supplierDTO =  supplierDTOList.get(0);
        }
      }

      //如果有 SupplierId 传过来填充该供应商信息
      if (supplierId != null && !"".equals(supplierId)) {
        IUserService userService = ServiceManager.getService(IUserService.class);
        List<SupplierDTO> supplierDTOs = userService.getSupplierById(Long.valueOf(shopId), Long.valueOf(supplierId));
        if (CollectionUtils.isNotEmpty(supplierDTOs)) {
          supplierDTO =  supplierDTOs.get(0);
        }
      }
      if(supplierDTO!=null){
        purchaseReturnDTO.setSupplierDTO(supplierDTO);
        if (supplierDTO.getId() != null) {
          //应付款总额 实付总额
          List<Double> doubleList = supplierPayableService.getSumPayableBySupplierId(supplierDTO.getId(), shopId, OrderDebtType.SUPPLIER_DEBT_PAYABLE);
          //应付款总额
          Double totalPayable = CollectionUtil.getFirst(doubleList);
          model.addAttribute("totalPayable", String.valueOf(NumberUtil.round(totalPayable, NumberUtil.MONEY_PRECISION)));
          SupplierRecordDTO supplierRecordDTO = supplierPayableService.getSupplierRecordDTOBySupplierId(shopId, supplierDTO.getId());
          if (null != supplierRecordDTO && null != supplierRecordDTO.getDebt()) {
            supplierRecordDTO.setDebt(NumberUtil.round(supplierRecordDTO.getDebt(), NumberUtil.MONEY_PRECISION));
          }
          model.addAttribute("supplierRecordDTO", supplierRecordDTO);
        }
      }

      if (purchaseReturnDTO.getItemDTOs() == null) {
        PurchaseReturnItemDTO purchaseReturnItemDTO = new PurchaseReturnItemDTO();
        PurchaseReturnItemDTO[] itemDTOs = {purchaseReturnItemDTO};
        purchaseReturnDTO.setItemDTOs(itemDTOs);
      }

      if(BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(WebUtil.getShopVersionId(request))){
        IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
        List<StoreHouseDTO> storeHouseDTOList = storeHouseService.getAllStoreHousesByShopId(shopId);
        model.addAttribute("storeHouseDTOList", storeHouseDTOList);//select 选项
        if(CollectionUtils.isNotEmpty(storeHouseDTOList) && purchaseReturnDTO.getStorehouseId()==null){
          if(storeHouseDTOList.size()==1){
            purchaseReturnDTO.setStorehouseId(storeHouseDTOList.get(0).getId());
          }
        }
        InventoryService inventoryService = ServiceManager.getService(InventoryService.class);
        //更新库存 根据仓库
        inventoryService.updateItemDTOInventoryAmountByStorehouse(shopId,purchaseReturnDTO.getStorehouseId(), purchaseReturnDTO);
      }
      Long purchaseOrderId=NumberUtil.longValue(request.getParameter("purchaseOrderId"));
      if("true".equals(request.getParameter("isToSalesReturn"))&&purchaseOrderId!=null){
        purchaseReturnDTO.setOriginOrderId(purchaseOrderId);
        purchaseReturnDTO.setReadOnly(true);
        Map<Long,PurchaseInventoryItemDTO> itemMap=new HashMap<Long,PurchaseInventoryItemDTO>() ;
        PurchaseInventoryDTO inventoryDTO = txnService.getPurchaseInventoryById(purchaseOrderId, shopId);
        if (inventoryDTO != null) {
          purchaseReturnDTO.setOriginReceiptNo(inventoryDTO.getReceiptNo());
          purchaseReturnDTO.setContactId(inventoryDTO.getContactId());
          purchaseReturnDTO.setSupplierDTO(supplierDTO);
        }
        List<PurchaseInventoryItemDTO> itemDTOs=txnService.getPurchaseInventoryItemByOrderIds(purchaseOrderId);
        if(CollectionUtil.isNotEmpty(itemDTOs)){
          for(PurchaseInventoryItemDTO itemDTO:itemDTOs){
            if(itemDTO==null||itemDTO.getProductId()==null){
              LOG.error("error info....");
              continue;
            }
            itemMap.put(itemDTO.getProductId(), itemDTO);
          }
        }
        if(!ArrayUtil.isEmpty(purchaseReturnDTO.getItemDTOs())){
          for(PurchaseReturnItemDTO itemDTO:purchaseReturnDTO.getItemDTOs()){
            if(itemDTO==null||itemDTO.getProductId()==null){
              LOG.error("error info....");
              continue;
            }
            PurchaseInventoryItemDTO pItemDTO=itemMap.get(itemDTO.getProductId());
            if(pItemDTO!=null){
              double amount = pItemDTO.getAmount();
              if(UnitUtil.isStorageUnit(pItemDTO.getUnit(), itemDTO)){
                amount *= itemDTO.getRate();
              }
              itemDTO.setIamount(amount);
              itemDTO.setIprice(pItemDTO.getPrice());
            }
          }
        }
      }
      purchaseReturnDTO.initDefaultItemUnit(ProductUnitCache.getProductUnitMap());
      model.addAttribute("purchaseReturnDTO", purchaseReturnDTO);
      this.setSupplierDropDownInfo(model, request);
      return "/txn/returnsStorage";
    } catch (Exception e) {
      LOG.error("goodsReturn.do,method=createReturnStorageByProductId error,shopId :{}", shopId);
      LOG.error(e.getMessage(), e);
      return "";
    }
  }

  @RequestMapping(params = "method=createReturnStorageBySupplierId")
  public String createReturnStorageBySupplierId(ModelMap model, HttpServletRequest request, HttpServletResponse response) {
    Long shopId = null;
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    IUserService userService = ServiceManager.getService(IUserService.class);
    ISupplierPayableService supplierPayableService = ServiceManager.getService(ISupplierPayableService.class);
    try {

      shopId = WebUtil.getShopId(request);
      String supplierId = request.getParameter("supplierId");
      PurchaseReturnDTO purchaseReturnDTO = new PurchaseReturnDTO();
      if (NumberUtil.isNumber(supplierId)) {
        purchaseReturnDTO = purchaseReturnService.createPurchaseReturnDTOBySupplierId(shopId, Long.valueOf(supplierId));
        SupplierDTO supplierDTO = userService.getSupplierById(Long.valueOf(supplierId));
        List<Double> doubleList = supplierPayableService.getSumPayableBySupplierId(Long.valueOf(supplierId),shopId, OrderDebtType.SUPPLIER_DEBT_PAYABLE);//应付款总额 实付总额

        //应付款总额
        Double totalPayable = doubleList.get(0);
          if(supplierDTO.getCustomerId() != null) {
              Double payable = supplierPayableService.getSumReceivableByCustomerId(supplierDTO.getCustomerId(), shopId, OrderDebtType.CUSTOMER_DEBT_PAYABLE);
              request.setAttribute("totalPayable", String.valueOf(NumberUtil.round(totalPayable, NumberUtil.MONEY_PRECISION)- NumberUtil.doubleVal(payable)));
          } else {
              request.setAttribute("totalPayable", String.valueOf(NumberUtil.round(totalPayable, NumberUtil.MONEY_PRECISION)));
          }
          //应收款
          List<Double> returnList = supplierPayableService.getSumPayableBySupplierId(Long.valueOf(supplierId), shopId,OrderDebtType.SUPPLIER_DEBT_RECEIVABLE);
          if(supplierDTO.getCustomerId() != null) {
              Double receivable = supplierPayableService.getSumReceivableByCustomerId(supplierDTO.getCustomerId(), shopId, OrderDebtType.CUSTOMER_DEBT_RECEIVABLE);
              request.setAttribute("totalReceivable", String.valueOf(NumberUtil.round(0-returnList.get(0), 2) +NumberUtil.round(receivable,2)));

          }  else {
              request.setAttribute("totalReceivable", String.valueOf(NumberUtil.round(0-returnList.get(0), 2)));
          }
        SupplierRecordDTO supplierRecordDTO = supplierPayableService.getSupplierRecordDTOBySupplierId(shopId,Long.valueOf(supplierId));
        if(null != supplierRecordDTO && null != supplierRecordDTO.getDebt()) {
          supplierRecordDTO.setDebt(NumberUtil.round(supplierRecordDTO.getDebt(), NumberUtil.MONEY_PRECISION));
        }
        request.setAttribute("supplierRecordDTO",supplierRecordDTO);
      }
      purchaseReturnDTO.setEditor((String) request.getSession().getAttribute("userName"));
      purchaseReturnDTO.setEditorId((Long) request.getSession().getAttribute("userId"));
      purchaseReturnDTO.setShopId(shopId);

      //用于退货明细显示一条记录
      if (purchaseReturnDTO.getItemDTOs() == null) {
        PurchaseReturnItemDTO purchaseReturnItemDTO = new PurchaseReturnItemDTO();
        PurchaseReturnItemDTO[] itemDTOs = {purchaseReturnItemDTO};
        purchaseReturnDTO.setItemDTOs(itemDTOs);
      }
      this.setSupplierDropDownInfo(model, request);
      purchaseReturnDTO.setReceiptNo(null);
      model.addAttribute("purchaseReturnDTO", purchaseReturnDTO);
      List<StoreHouseDTO> storeHouseDTOList = null;
      if(BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(WebUtil.getShopVersionId(request))){
        IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
        storeHouseDTOList = storeHouseService.getAllStoreHousesByShopId(shopId);
        model.addAttribute("storeHouseDTOList", storeHouseDTOList);//select 选项
      }
    } catch (Exception e) {
      LOG.error("goodsReturn.do,method=createReturnStorageBySupplierId error,shopId :{}", shopId);
      LOG.error(e.getMessage(), e);
    }
    return "/txn/returnsStorage";

  }

  private void setSupplierDropDownInfo(ModelMap model, HttpServletRequest request) {
    Map settlementTypeList = TxnConstant.getSettlementTypeMap(request.getLocale());
    model.addAttribute(MODEL_SETTLEMENTTYPE_LIST, settlementTypeList);
    Map invoiceCategoryList = TxnConstant.getInvoiceCatagoryMap(request.getLocale());
    model.addAttribute(MODEL_INVOICECATEGORY_LIST, invoiceCategoryList);

  }



  /**
   * 根据退货单ID显示退货记录
   *
   * @param model
   * @param request
   * @return
   * @throws Exception
   */
  @RequestMapping(params = "method=showReturnStorageByPurchaseReturnId")
  public String showReturnStorageByPurchaseReturnId(ModelMap model, HttpServletRequest request, String purchaseReturnId) throws Exception {
    try {
      ITxnService txnService = ServiceManager.getService(ITxnService.class);
      if (StringUtils.isBlank(purchaseReturnId))
        throw new Exception("showReturnStorageByPurchaseReturnId purchaseReturnId is empty!");
      RFITxnService rfiTxnService = ServiceManager.getService(RFITxnService.class);
      IUserService userService = ServiceManager.getService(IUserService.class);
      ISupplierPayableService supplierPayableService = ServiceManager.getService(ISupplierPayableService.class);

      Long shopId = WebUtil.getShopId(request);

      PurchaseReturnDTO purchaseReturnDTO = rfiTxnService.getPurchaseReturnDTOById(Long.valueOf(purchaseReturnId));
      if (purchaseReturnDTO != null) {
        if(OrderStatus.SELLER_PENDING.equals(purchaseReturnDTO.getStatus())){
          model.addAttribute("purchaseReturnId", purchaseReturnDTO.getId());
          return REDIRECT_MODIFY;
        }

        //设置退货是用现金还是定金
        PayableDTO payableDTO = supplierPayableService.getPayableDTOByOrderId(shopId, Long.valueOf(purchaseReturnId));

//        SupplierReturnPayableDTO supplierReturnPayableDTO = supplierPayableService.getSupplierReturnPayableByPurchaseReturnId(shopId, Long.valueOf(purchaseReturnId));
        if (payableDTO != null) {
          if(payableDTO.getCheque()!=0)
          {
            PayableHistoryRecordDTO payableHistoryRecordDTO = supplierPayableService.getPayHistoryRecord(purchaseReturnDTO.getId(),purchaseReturnDTO.getId(),shopId);
            if(null != payableHistoryRecordDTO)
            {
              purchaseReturnDTO.setBankCheckNo(payableHistoryRecordDTO.getCheckNo());
            }
          }
          purchaseReturnDTO.setCash(-payableDTO.getCash());
          purchaseReturnDTO.setStrikeAmount(-payableDTO.getStrikeAmount());
          purchaseReturnDTO.setDepositAmount(-payableDTO.getDeposit());
          purchaseReturnDTO.setPayee(payableDTO.getLastPayer());
          purchaseReturnDTO.setAccountDebtAmount(-payableDTO.getCreditAmount());
          purchaseReturnDTO.setAccountDiscount(-payableDTO.getDeduction());
          purchaseReturnDTO.setSettledAmount(-payableDTO.getPaidAmount());
          purchaseReturnDTO.setBankAmount(-payableDTO.getBankCard());
          purchaseReturnDTO.setBankCheckAmount(-payableDTO.getCheque());
          purchaseReturnDTO.setStatementAmount(- NumberUtil.doubleVal(payableDTO.getStatementAccount()));
        }

        // 填充退货明细中的具体信息
        purchaseReturnService.fillPurchaseReturnItemDTOsDetailInfo(purchaseReturnDTO);
        purchaseReturnDTO.setPrint(request.getParameter("print"));
        model.addAttribute("purchaseReturnDTO", purchaseReturnDTO);
        this.setSupplierDropDownInfo(model, request);
      } else {
        model.addAttribute("purchaseReturnDTO", purchaseReturnDTO);
        this.setSupplierDropDownInfo(model, request);
      }

      if(null != purchaseReturnDTO) {
        List<Double> doubleList = supplierPayableService.getSumPayableBySupplierId(purchaseReturnDTO.getSupplierId(), purchaseReturnDTO.getShopId(), OrderDebtType.SUPPLIER_DEBT_PAYABLE);//应付款总额 实付总额
        SupplierDTO supplierDTO = userService.getSupplierById(purchaseReturnDTO.getSupplierId());
        //应付款总额
        Double totalPayable = doubleList.get(0);
          if(supplierDTO.getCustomerId() != null) {
              Double payable = supplierPayableService.getSumReceivableByCustomerId(supplierDTO.getCustomerId(), shopId, OrderDebtType.CUSTOMER_DEBT_PAYABLE);
              request.setAttribute("totalPayable", String.valueOf(NumberUtil.round(totalPayable, NumberUtil.MONEY_PRECISION)- NumberUtil.doubleVal(payable)));
          } else {
              request.setAttribute("totalPayable", String.valueOf(NumberUtil.round(totalPayable, NumberUtil.MONEY_PRECISION)));
          }
          //应收款
          List<Double> returnList = supplierPayableService.getSumPayableBySupplierId(purchaseReturnDTO.getSupplierId(), shopId,OrderDebtType.SUPPLIER_DEBT_RECEIVABLE);
          if(supplierDTO.getCustomerId() != null) {
              Double receivable = supplierPayableService.getSumReceivableByCustomerId(supplierDTO.getCustomerId(), shopId, OrderDebtType.CUSTOMER_DEBT_RECEIVABLE);
              request.setAttribute("totalReceivable", String.valueOf(NumberUtil.round(0-returnList.get(0), 2) +NumberUtil.round(receivable,2)));

          }  else {
              request.setAttribute("totalReceivable", String.valueOf(NumberUtil.round(0-returnList.get(0), 2)));
          }


        SupplierRecordDTO supplierRecordDTO = supplierPayableService.getSupplierRecordDTOBySupplierId(purchaseReturnDTO.getShopId(), purchaseReturnDTO.getSupplierId());
        if (null != supplierRecordDTO && null != supplierRecordDTO.getDebt()) {
          supplierRecordDTO.setDebt(NumberUtil.round(supplierRecordDTO.getDebt(), NumberUtil.MONEY_PRECISION));
        }
        request.setAttribute("supplierRecordDTO", supplierRecordDTO);
        model.addAttribute("supplierDTO", supplierDTO);
        ShopDTO supplierShop = ServiceManager.getService(IConfigService.class).getShopById(supplierDTO.getSupplierShopId());
        model.addAttribute("supplierShop", supplierShop);
      }

      if (null != purchaseReturnDTO) {
        /**
         * 单据结算记录
         */
        List<PayableHistoryRecordDTO> payableHistoryRecordDTOs = supplierPayableService.getSettledRecord(shopId, OrderTypes.RETURN, Long.valueOf(purchaseReturnId));
        model.addAttribute("payableHistoryRecordDTOs", payableHistoryRecordDTOs);
        model.addAttribute("receiveNo", ServiceManager.getService(ITxnService.class).getStatementAccountOrderNo(shopId, purchaseReturnDTO.getStatementAccountOrderId()));
      }


    } catch (Exception e) {
      LOG.error("/goodsReturn.do");
      LOG.error("method=showReturnStorageByPurchaseReturnId");
      LOG.error("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
    }
    return "/txn/returnsStorageFinish";

  }

  @RequestMapping(params = "method=returnAccountDetail")
  public String returnAccountDetail(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
    Long shopId = WebUtil.getShopId(request);
    String supplierIdStr = request.getParameter("supplierId");
    String purchaseReturnId = request.getParameter("purchaseReturnId");
    if (StringUtils.isNotBlank(supplierIdStr)) {
      ISupplierPayableService supplierPayableService = ServiceManager.getService(ISupplierPayableService.class);
      Double totalPayable = supplierPayableService.getSumPayableBySupplierId(Long.valueOf(supplierIdStr), shopId,OrderDebtType.SUPPLIER_DEBT_PAYABLE).get(0);
      request.setAttribute("totalPayable", totalPayable);
      Double sumPayable = supplierPayableService.getSumDepositBySupplierId(Long.valueOf(supplierIdStr), shopId);
      sumPayable = NumberUtil.toReserve(sumPayable, NumberUtil.MONEY_PRECISION);
      request.setAttribute("sumPayable", sumPayable);
    }else{
      //  否则为新供应商
      request.setAttribute("sumPayable", 0.00);
    }
    if(StringUtils.isNotBlank(purchaseReturnId)){
      RFITxnService rfiTxnService = ServiceManager.getService(RFITxnService.class);
      try{
        PurchaseReturnDTO purchaseReturnDTO = rfiTxnService.getPurchaseReturnDTOById(shopId, Long.valueOf(purchaseReturnId));
        if(purchaseReturnDTO == null){
          LOG.error("点击退货单结算时出错，单据找不到。 GoodsReturnController.returnAccountDetail. shopID:{}, purchaseReturnID:{}", shopId, purchaseReturnId);
        }else{
          if(purchaseReturnDTO.getSupplierShopId() !=null){
            model.addAttribute("isRelationOrder", true);
          }
          model.addAttribute("purchaseReturnId", purchaseReturnId);
          model.addAttribute("status", purchaseReturnDTO.getStatus());
          model.addAttribute("total", NumberUtil.round(purchaseReturnDTO.getTotal(), NumberUtil.MONEY_PRECISION));
        }
      }catch(Exception e){
        LOG.error("点击退货单结算时出错， GoodsReturnController.returnAccountDetail. shopID:{}, purchaseReturnID:{}", shopId, purchaseReturnId);
        LOG.error(e.getMessage(), e);
      }
    }
    return "/txn/returnAccountDetail";
  }

  @RequestMapping(params = "method=getTotalPayable")
  @ResponseBody
  public Map getTotalPayable(HttpServletRequest request, HttpServletResponse response) {
    Long shopId = WebUtil.getShopId(request);
    String supplierIdStr = request.getParameter("supplierId");
    Map<String, Double> map = new HashMap<String, Double>();
    map.put("resu", null);
    if (StringUtils.isNotBlank(supplierIdStr)) {
      ISupplierPayableService supplierPayableService = ServiceManager.getService(ISupplierPayableService.class);
      Double totalPayable = supplierPayableService.getSumPayableBySupplierId(Long.valueOf(supplierIdStr), shopId, OrderDebtType.SUPPLIER_DEBT_PAYABLE).get(0);

      totalPayable = NumberUtil.toReserve(totalPayable, NumberUtil.MONEY_PRECISION);

      map.put("resu", totalPayable);

    }

    return map;
  }

  @RequestMapping(params = "method=validateCopy")
  @ResponseBody
  public Result validateCopy(ModelMap model, HttpServletRequest request, Long purchaseReturnId){
    RFITxnService rfiTxnService = ServiceManager.getService(RFITxnService.class);
    Long shopId = null;
    Long userId = null;
    try {
      shopId = WebUtil.getShopId(request);
      userId = WebUtil.getUserId(request);
      if (shopId == null || purchaseReturnId == null) {
        LOG.error("goodsReturn.do?method=validateCopy, shopId:{}, purchaseReturnId:{}", shopId, purchaseReturnId);
        return new Result("验证失败", "验证失败，请重试！", false);
      }
      return rfiTxnService.validatePurchaseReturnCopy(purchaseReturnId, shopId);
    }catch(Exception e){
      LOG.error("goodsReturn.do?method=validateCopy. shopId:{}, userId:{}, purchaseReturnId:{}", new Object[]{shopId, userId, purchaseReturnId});
      LOG.error(e.getMessage(), e);
      return new Result("验证失败", "验证失败，请重试！", false);
    }
  }

  /**
   * 退货单复制功能
   *
   * @param model
   * @param request
   * @param purchaseReturnId
   * @return
   */
  @RequestMapping(params = "method=copyReturnStorage")
  public String copyReturnStorage(ModelMap model, HttpServletRequest request, Long purchaseReturnId) throws Exception {
    Long shopId = null;

    try {
      shopId = WebUtil.getShopId(request);
      if (shopId == null || purchaseReturnId == null) {
        LOG.error("goodsReturn.do?method=copyReturnStorage, shopId = " + shopId + "returnStorageId=" + purchaseReturnId + "\n");
        return createReturnStorage(model, request);
      }
      PurchaseReturnDTO purchaseReturnDTO = purchaseReturnService.getPurchaseReturnById(purchaseReturnId, shopId);
      if (purchaseReturnDTO == null) {
        LOG.error("goodsReturn.do?method=copyReturnStorage, PurchaseReturnDTO not found, shopId = " + shopId +
            "returnStorageId=" + purchaseReturnId + "\n");
        return createReturnStorage(model, request);
      }

      ITxnService txnService = ServiceManager.getService(ITxnService.class);
      PurchaseReturnDTO newPurchaseReturnDTO = purchaseReturnService.copyPurchaseReturnDTO(purchaseReturnDTO);
      //加上供应商信息
      if(newPurchaseReturnDTO.getSupplierId()!=null){
        SupplierDTO supplierDTO = ServiceManager.getService(IUserService.class).getSupplierById(newPurchaseReturnDTO.getSupplierId());
        newPurchaseReturnDTO.setSupplierDTO(supplierDTO);
      }
      newPurchaseReturnDTO.setReceiptNo(txnService.getReceiptNo(shopId, OrderTypes.RETURN, null));
      newPurchaseReturnDTO.setDraftOrderIdStr(null);
      newPurchaseReturnDTO.setEditor((String) request.getSession().getAttribute("userName"));
      newPurchaseReturnDTO.setEditorId((Long) request.getSession().getAttribute("userId"));
      newPurchaseReturnDTO.setShopId(shopId);
      newPurchaseReturnDTO.setStatus(null);
      this.setSupplierDropDownInfo(model, request);
      if (BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(WebUtil.getShopVersionId(request))) {
        IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
        List<StoreHouseDTO> storeHouseDTOList = storeHouseService.getAllStoreHousesByShopId(shopId);
        model.addAttribute("storeHouseDTOList", storeHouseDTOList);//select 选项
        if(newPurchaseReturnDTO.getStorehouseId()!=null){
          StoreHouseDTO storeHouseDTO = storeHouseService.getStoreHouseDTOById(shopId,newPurchaseReturnDTO.getStorehouseId());
          if(storeHouseDTO==null || DeletedType.TRUE.equals(storeHouseDTO.getDeleted())){
            newPurchaseReturnDTO.setStorehouseId(null);
            newPurchaseReturnDTO.setStorehouseName(null);
          }
        }
        if (CollectionUtils.isNotEmpty(storeHouseDTOList) && newPurchaseReturnDTO.getStorehouseId() == null) {
          if(storeHouseDTOList.size()==1){
            newPurchaseReturnDTO.setStorehouseId(storeHouseDTOList.get(0).getId());
          }
        }
        //更新库存 根据仓库
        InventoryService inventoryService = ServiceManager.getService(InventoryService.class);
        inventoryService.updateItemDTOInventoryAmountByStorehouse(shopId,newPurchaseReturnDTO.getStorehouseId(), newPurchaseReturnDTO);
      }
        ISupplierPayableService supplierPayableService = ServiceManager.getService(ISupplierPayableService.class);
        SupplierDTO supplierDTO = ServiceManager.getService(IUserService.class).getSupplierById(purchaseReturnDTO.getSupplierId());
        //应付款
        List<Double> doubleList = supplierPayableService.getSumPayableBySupplierId(purchaseReturnDTO.getSupplierId(),shopId, OrderDebtType.SUPPLIER_DEBT_PAYABLE);//应付款总额 实付总额
        Double totalPayable = doubleList.get(0);
        if(supplierDTO.getCustomerId() != null) {
            Double payable = supplierPayableService.getSumReceivableByCustomerId(supplierDTO.getCustomerId(), shopId, OrderDebtType.CUSTOMER_DEBT_PAYABLE);
            request.setAttribute("totalPayable", String.valueOf(NumberUtil.round(totalPayable, NumberUtil.MONEY_PRECISION)- NumberUtil.doubleVal(payable)));
        } else {
            request.setAttribute("totalPayable", String.valueOf(NumberUtil.round(totalPayable, NumberUtil.MONEY_PRECISION)));
        }
        //应收款
        List<Double> returnList = supplierPayableService.getSumPayableBySupplierId(purchaseReturnDTO.getSupplierId(), shopId,OrderDebtType.SUPPLIER_DEBT_RECEIVABLE);
        if(supplierDTO.getCustomerId() != null) {
            Double receivable = supplierPayableService.getSumReceivableByCustomerId(supplierDTO.getCustomerId(), shopId, OrderDebtType.CUSTOMER_DEBT_RECEIVABLE);
            request.setAttribute("totalReceivable", String.valueOf(NumberUtil.round(0-returnList.get(0), 2) +NumberUtil.round(receivable,2)));

        }  else {
            request.setAttribute("totalReceivable", String.valueOf(NumberUtil.round(0-returnList.get(0), 2)));
        }
      model.addAttribute("purchaseReturnDTO", newPurchaseReturnDTO);
      Map settlementTypeList = TxnConstant.getSettlementTypeMap(request.getLocale());
      model.addAttribute(MODEL_SETTLEMENTTYPE_LIST, settlementTypeList);
      Map invoiceCategoryList = TxnConstant.getInvoiceCatagoryMap(request.getLocale());
      model.addAttribute(MODEL_INVOICECATEGORY_LIST, invoiceCategoryList);
      return "/txn/returnsStorage";
    } catch (Exception e) {
      LOG.error("goodsReturn.do?method=copyReturnStorage,shopId = " + shopId + "returnStorageId=" + purchaseReturnId + "\n");
      LOG.error(e.getMessage(), e);
      return createReturnStorage(model, request);
    }

  }


  /**
   * 此方法为在线作废!! 普通作废  method=repeal
   * 结算后不能作废  只有在被拒绝后 有做   只需要改状态 库存  在拒绝的时候已经还回去了
   * @param model
   * @param request
   * @param purchaseReturnId
   * @return
   * @throws Exception
   */
  @RequestMapping(params = "method=repealReturnStorage")
  String repealReturnStorage(ModelMap model, HttpServletRequest request, Long purchaseReturnId,Long toStorehouseId) throws Exception {
    RFITxnService rfiTxnService = ServiceManager.getService(RFITxnService.class);
    IInventoryService inventoryService = ServiceManager.getService(IInventoryService.class);
    Long shopId = null;
    Long shopVersionId = null;
    PurchaseReturnDTO purchaseReturnDTO = null;
    try {
      shopVersionId = WebUtil.getShopVersionId(request);
      shopId = WebUtil.getShopId(request);
      if (shopId == null || purchaseReturnId == null) {
        LOG.error("goodsReturn.do?method=repealReturnStorage, shopId = " + shopId + "returnStorageId=" + purchaseReturnId + "\n");
        return createReturnStorage(model, request);
      }

      purchaseReturnDTO = rfiTxnService.getPurchaseReturnDTOById(Long.valueOf(purchaseReturnId));

      if (purchaseReturnDTO == null) {
        LOG.error("goodsReturn.do?method=repealReturnStorage, PurchaseReturnDTO not found, shopId = " + shopId +
            "returnStorageId=" + purchaseReturnId + "\n");
        return createReturnStorage(model, request);
      }
      model.addAttribute("purchaseReturnId", purchaseReturnDTO.getId());
      if (purchaseReturnDTO.getStatus() == OrderStatus.REPEAL) {
        LOG.error("goodsReturn.do?method=repealReturnStorage, PurchaseReturnDTO has been repealed, shopId = " + shopId +
            "returnStorageId=" + purchaseReturnId + "\n");
        return REDIRECT_SHOW;
      }
      if (!(purchaseReturnDTO.getStatus() == OrderStatus.SELLER_REFUSED || purchaseReturnDTO.getStatus() == OrderStatus.SELLER_PENDING)) {
        LOG.error("goodsReturn.do?method=repealReturnStorage, 状态["+purchaseReturnDTO.getStatus()+"]不是SELLER_REFUSED、PENDING，不能作废, shopId = " + shopId +
            "returnStorageId=" + purchaseReturnId + "\n");
        request.setAttribute("errorMsg", "当前单据状态是" + purchaseReturnDTO.getStatus().getName() + "不能作废单据！");
        return REDIRECT_SHOW;
      }
      // 填充退货明细中的具体信息
      purchaseReturnService.fillPurchaseReturnItemDTOsDetailInfo(purchaseReturnDTO);

      purchaseReturnDTO.setShopVersionId(shopVersionId);
      purchaseReturnDTO.setEditDate(System.currentTimeMillis());
      purchaseReturnDTO.setEditor(WebUtil.getUserName(request));
      purchaseReturnDTO.setEditorId(WebUtil.getUserId(request));
      purchaseReturnDTO.setUserId(WebUtil.getUserId(request));
      purchaseReturnDTO.setUserName(WebUtil.getUserName(request));
      ISaleReturnOrderService saleReturnOrderService = ServiceManager.getService(ISaleReturnOrderService.class);
      SalesReturnDTO salesReturnDTO = saleReturnOrderService.getSalesReturnDTOByPurchaseReturnOrderId(purchaseReturnDTO.getId());
      String key = "salesReturnOrder" + StringUtil.truncValue(salesReturnDTO.getId().toString())+"purchaseReturnOrder"+StringUtil.truncValue(salesReturnDTO.getPurchaseReturnOrderId().toString())+ StringUtil.truncValue(shopId.toString());
      try {
        if (!BcgogoConcurrentController.lock(ConcurrentScene.HANDLE_PAYABLE, key)) {
          request.setAttribute("errorMsg", "当前单据正在被操作，请稍候再试！");
          return REDIRECT_SHOW;
        }
        purchaseReturnService.repealPurchaseReturnInTxn(purchaseReturnDTO, toStorehouseId);
      } finally {
        BcgogoConcurrentController.release(ConcurrentScene.HANDLE_PAYABLE, key);
      }

      //更新orderindex order supplier solr 状态。
      BcgogoEventPublisher bcgogoEventPublisher = new BcgogoEventPublisher();
      PurchaseReturnSavedEvent purchaseReturnSavedEvent = new PurchaseReturnSavedEvent(purchaseReturnDTO);
      bcgogoEventPublisher.publisherPurchaseReturnSaved(purchaseReturnSavedEvent);
      //取消 推送消息
      ServiceManager.getService(IPushMessageService.class).disabledPushMessageReceiverBySourceId(null, purchaseReturnDTO.getId(),null,PushMessageSourceType.SALE_RETURN_NEW);
    } catch (Exception e) {
      LOG.error("goodsReturn.do?method=repealReturnStorage,shopId = " + shopId + "returnStorageId=" + purchaseReturnId + "\n");
      LOG.error(e.getMessage(), e);
      model.addAttribute("purchaseReturnDTO", purchaseReturnDTO);
      model.addAttribute("errorMsg", "系统出错！");
      return REDIRECT_SHOW;
    }
    return REDIRECT_SHOW;
  }

  @RequestMapping(params = "method=settleForWholesaler")
  @ResponseBody
  public Result settleSupplierPurchaseReturn(ModelMap model, HttpServletRequest request, Long purchaseReturnId, PurchaseReturnDTO settlePurchaseReturnDTO){
    RFITxnService rfiTxnService = ServiceManager.getService(RFITxnService.class);
    ISupplierPayableService supplierPayableService = ServiceManager.getService(ISupplierPayableService.class);
    IUserService userService = ServiceManager.getService(IUserService.class);
    Long shopId = null;
    Long userId = null;
    try{
      shopId = WebUtil.getShopId(request);
      userId = WebUtil.getUserId(request);
      if (shopId == null || purchaseReturnId == null || userId == null) {
        LOG.error("GoodsReturnController.settleSupplierPurchaseReturn, shopId:{}, purchaseReturnId:{}, userId:{}", new Object[]{shopId, purchaseReturnId, userId});
        return new Result("数据异常，结算失败。", false);
      }
      PurchaseReturnDTO purchaseReturnDTO = rfiTxnService.getPurchaseReturnDTOById(purchaseReturnId);
      if (purchaseReturnDTO == null) {
        LOG.error("GoodsReturnController.settleSupplierPurchaseReturn, 找不到退货单. shopId:{}, purchaseReturnId:{}", shopId, purchaseReturnId);
        return new Result("无此退货单，结算失败。", false);
      }
      purchaseReturnDTO.setUserId(userId);
      purchaseReturnDTO.setUserName(WebUtil.getUserName(request));

      List<SupplierDTO> supplierDTOList = userService.getSupplierById(shopId, purchaseReturnDTO.getSupplierId());
      if (CollectionUtils.isNotEmpty(supplierDTOList)) {
        purchaseReturnDTO.setSupplierDTO(supplierDTOList.get(0));
      }
      purchaseReturnService.fillPurchaseReturnItemDTOsDetailInfo(purchaseReturnDTO);

      if (purchaseReturnDTO.getStatus() != OrderStatus.SELLER_ACCEPTED) {
        LOG.error("GoodsReturnController.settleSupplierPurchaseReturn, 单据状态无法结算. shopId:{}, purchaseReturnDTO:{}", shopId, purchaseReturnDTO);
        return new Result("单据状态不符，无法结算。", false);
      }

      String settleKey = "purchaseReturnOrder" + StringUtil.truncValue(purchaseReturnDTO.getId().toString()) + StringUtil.truncValue(shopId.toString());
      try {
        if (!BcgogoConcurrentController.lock(ConcurrentScene.HANDLE_PAYABLE, settleKey)) {
          return new Result("等待超时，结算失败！", false);
        }
        //更新已经被删除的商品，状态的值置空
        rfiTxnService.updateDeleteProductsByOrderDTO(purchaseReturnDTO);

        purchaseReturnDTO.setInventoryLimitDTO(new InventoryLimitDTO());

        // 结算， 同save方法中逻辑。
        purchaseReturnDTO.setCash(settlePurchaseReturnDTO.getCash()==null?0d:settlePurchaseReturnDTO.getCash());
        purchaseReturnDTO.setDepositAmount(settlePurchaseReturnDTO.getDepositAmount()==null?0d:settlePurchaseReturnDTO.getDepositAmount());
        purchaseReturnDTO.setStrikeAmount(settlePurchaseReturnDTO.getStrikeAmount()==null?0d:settlePurchaseReturnDTO.getStrikeAmount());
        purchaseReturnDTO.setAccountDebtAmount(settlePurchaseReturnDTO.getAccountDebtAmount()==null?0d:settlePurchaseReturnDTO.getAccountDebtAmount());
        purchaseReturnDTO.setAccountDiscount(settlePurchaseReturnDTO.getAccountDiscount()==null?0d:settlePurchaseReturnDTO.getAccountDiscount());
        purchaseReturnDTO.setSettledAmount(settlePurchaseReturnDTO.getSettledAmount()==null?0d:settlePurchaseReturnDTO.getSettledAmount());
        purchaseReturnDTO.setBankAmount(NumberUtil.numberValue(settlePurchaseReturnDTO.getBankAmount(),0D));
        purchaseReturnDTO.setBankCheckAmount(NumberUtil.numberValue(settlePurchaseReturnDTO.getBankCheckAmount(),0D));
        if (purchaseReturnDTO.getBankCheckAmount() == 0) {
          settlePurchaseReturnDTO.setBankCheckNo("");
        }
        if ("支票号".equals(settlePurchaseReturnDTO.getBankCheckNo())) {
          settlePurchaseReturnDTO.setBankCheckNo("");
        }
        purchaseReturnDTO.setBankCheckNo(settlePurchaseReturnDTO.getBankCheckNo());
        supplierPayableService.returnPayable(purchaseReturnDTO);    //包括流水统计

        //payableupdate记录，改变应付款 supplierRecord
        if (null != purchaseReturnDTO.getStrikeAmount() && purchaseReturnDTO.getStrikeAmount().doubleValue() > 0.001) {
          String key = "updatePayable" + StringUtil.truncValue(purchaseReturnDTO.getSupplierId().toString()) + StringUtil.truncValue(WebUtil.getShopId(request).toString());
          try {
            if (!BcgogoConcurrentController.lock(ConcurrentScene.HANDLE_PAYABLE, key)) {
              return new Result("结算失败", false);
            }
            updateOtherPayable(request, purchaseReturnDTO, supplierPayableService);
          } finally {
            BcgogoConcurrentController.release(ConcurrentScene.HANDLE_PAYABLE, key);
          }
        }

        rfiTxnService.settlePurchaseReturn(shopId, userId, purchaseReturnDTO);
      }finally{
        BcgogoConcurrentController.release(ConcurrentScene.HANDLE_PAYABLE, settleKey);
      }

      ServiceManager.getService(IProductOutStorageService.class).productThroughByOrder(purchaseReturnDTO, OrderTypes.RETURN, purchaseReturnDTO.getStatus());


      BcgogoEventPublisher bcgogoEventPublisher = new BcgogoEventPublisher();
      PurchaseReturnSavedEvent purchaseReturnSavedEvent = new PurchaseReturnSavedEvent(purchaseReturnDTO);
      bcgogoEventPublisher.publisherPurchaseReturnSaved(purchaseReturnSavedEvent);
      request.setAttribute("UNIT_TEST", purchaseReturnSavedEvent); //单元测试

      return new Result("结算成功！", true);
    }catch(Exception e){
      LOG.error("GoodsReturnController.settleSupplierPurchaseReturn, shopId:{}, purchaseReturnId:{}", shopId, purchaseReturnId);
      LOG.error(e.getMessage(), e);
      return new Result("结算失败！", false);
    }
  }

  /**
   * 普通入库退货单作废(非在线)
   */
  @RequestMapping(params = "method=repeal")
  public String repeal(ModelMap model, HttpServletRequest request, Long purchaseReturnId, Long toStorehouseId) throws Exception {
    RFITxnService rfiTxnService = ServiceManager.getService(RFITxnService.class);
    IInventoryService inventoryService = ServiceManager.getService(IInventoryService.class);
    ISupplierService supplierService = ServiceManager.getService(ISupplierService.class);
    ISupplierPayableService supplierPayableService = ServiceManager.getService(ISupplierPayableService.class);
    IUserService userService = ServiceManager.getService(IUserService.class);
    Long shopId = null;
    Long shopVersionId = null;
    PurchaseReturnDTO purchaseReturnDTO = null;
    try {
      Result result = validateRepeal(model, request, purchaseReturnId);
      if(!result.isSuccess()){
        model.addAttribute("purchaseReturnId", purchaseReturnId);
        return REDIRECT_SHOW;
      }
      shopVersionId = WebUtil.getShopVersionId(request);
      shopId = WebUtil.getShopId(request);
      if (shopId == null || purchaseReturnId == null) {
        LOG.error("goodsReturn.do?method=repeal, shopId = " + shopId + "returnStorageId=" + purchaseReturnId + "\n");
        return createReturnStorage(model, request);
      }

      purchaseReturnDTO = rfiTxnService.getPurchaseReturnDTOById(Long.valueOf(purchaseReturnId));
      SupplierDTO supplierDTO = supplierService.getSupplierById(purchaseReturnDTO.getSupplierId(),purchaseReturnDTO.getShopId());

      if (purchaseReturnDTO == null) {
        LOG.error("goodsReturn.do?method=repeal, PurchaseReturnDTO not found, shopId = " + shopId +
            "returnStorageId=" + purchaseReturnId + "\n");
        return createReturnStorage(model, request);
      }
      model.addAttribute("purchaseReturnId", purchaseReturnDTO.getId());
      if (purchaseReturnDTO.getStatus() == OrderStatus.REPEAL) {
        LOG.error("goodsReturn.do?method=repeal, PurchaseReturnDTO has been repealed, shopId = " + shopId +
            "returnStorageId=" + purchaseReturnId + "\n");
        return REDIRECT_SHOW;
      }

      purchaseReturnDTO.setShopVersionId(shopVersionId);
      purchaseReturnDTO.setEditDate(System.currentTimeMillis());
      purchaseReturnDTO.setEditor(WebUtil.getUserName(request));
      purchaseReturnDTO.setEditorId(WebUtil.getUserId(request));
      purchaseReturnDTO.setUserId(WebUtil.getUserId(request));
      purchaseReturnDTO.setUserName(WebUtil.getUserName(request));

      rfiTxnService.updateDeleteProductsByOrderDTO(purchaseReturnDTO);
      if(supplierDTO != null && supplierDTO.getStatus() == CustomerStatus.DISABLED){
        supplierDTO.setStatus(null);
        userService.updateSupplier(supplierDTO);
      }

      purchaseReturnService.repealPurchaseReturnInTxn(purchaseReturnDTO, toStorehouseId);

      //repeal_order
      rfiTxnService.saveRepealOrderByOrderIdAndOrderType(shopId, purchaseReturnDTO.getId(), OrderTypes.RETURN);
      //payable相关记录作废
      supplierPayableService.returnPayableRepeal(purchaseReturnDTO);

      //更新供应商总退货量, 更新供应商应付款 supplierRecord
      ServiceManager.getService(ISupplierRecordService.class).updateSupplierRecordDebt(shopId, supplierDTO);

      //更新orderindex order supplier solr 状态。
      BcgogoEventPublisher bcgogoEventPublisher = new BcgogoEventPublisher();
      PurchaseReturnSavedEvent purchaseReturnSavedEvent = new PurchaseReturnSavedEvent(purchaseReturnDTO);
      bcgogoEventPublisher.publisherPurchaseReturnSaved(purchaseReturnSavedEvent);
    } catch (Exception e) {
      LOG.error("goodsReturn.do?method=repealReturnStorage,shopId = " + shopId + "returnStorageId=" + purchaseReturnId + "\n");
      LOG.error(e.getMessage(), e);
      model.addAttribute("purchaseReturnDTO", purchaseReturnDTO);
      model.addAttribute("errorMsg", "系统出错！");
      return REDIRECT_SHOW;
    }
    return REDIRECT_SHOW;
  }

  @RequestMapping(params = "method=validateRepeal")
  @ResponseBody
  public Result validateRepeal(ModelMap modelMap, HttpServletRequest request, Long purchaseReturnId){
    Result result = new Result(false);
    ISupplierPayableService supplierPayableService = ServiceManager.getService(ISupplierPayableService.class);
    RFITxnService rfiTxnService = ServiceManager.getService(RFITxnService.class);
    Long shopId = WebUtil.getShopId(request);
    try{
      PurchaseReturnDTO purchaseReturnDTO  = rfiTxnService.getPurchaseReturnDTOById(shopId, purchaseReturnId);
      if(purchaseReturnDTO == null){
        result.setMsg("单据不存在!");
        return result;
      }
      PayableDTO payableDTO = ServiceManager.getService(ITxnService.class).getPayableDTOByOrderId(shopId, purchaseReturnId);
      if(payableDTO == null){
        result.setMsg("未找到结算信息,无法作废!");
        return result;
      }
      if(purchaseReturnDTO.getStatementAccountOrderId()!=null){
        result.setMsg("单据已做过对账,无法作废!");
        return result;
      }
      if(payableDTO.getDeposit()!= null && payableDTO.getDeposit()!=0){
        DepositDTO depositDTO = supplierPayableService.getDepositBySupplierId(shopId, purchaseReturnDTO.getSupplierId());
        if(depositDTO == null){
          result.setMsg("此供应商无预付款,无法作废!");
          return result;
        }
        if(Math.abs(payableDTO.getDeposit()) - depositDTO.getActuallyPaid() > 0){
          result.setMsg("供应商预付款不足,无法作废!");
          return result;
        }
      }
      SupplierDTO supplierDTO = ServiceManager.getService(ISupplierService.class).getSupplierById(purchaseReturnDTO.getSupplierId(),shopId);
      if(supplierDTO == null){
        return new Result("供应商不存在, 无法作废!",false);
      }
      if(supplierDTO.getStatus()== CustomerStatus.DISABLED){
        return new Result(true, Result.Operation.CONFIRM_RESTORE_SUPPLIER);
      }
      return new Result(true);
    }catch (Exception e){
      LOG.error("goodsReturn.do?method=validateRepeal error.", e);
      return new Result("校验失败!", false);
    }
  }
}
