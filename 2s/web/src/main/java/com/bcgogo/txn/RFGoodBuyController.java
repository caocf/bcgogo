  package com.bcgogo.txn;

import com.bcgogo.common.BcgogoOrderFormBean;
import com.bcgogo.common.Pair;
import com.bcgogo.common.Result;
import com.bcgogo.common.WebUtil;
import com.bcgogo.config.cache.AreaCacheManager;
import com.bcgogo.config.cache.BcgogoConcurrentController;
import com.bcgogo.config.dto.OperationLogDTO;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.service.IOperationLogService;
import com.bcgogo.config.service.image.IImageService;
import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.enums.*;
import com.bcgogo.enums.config.ImageScene;
import com.bcgogo.enums.txn.pushMessage.PushMessageSourceType;
import com.bcgogo.product.cache.ProductUnitCache;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.product.dto.ProductLocalInfoDTO;
import com.bcgogo.product.service.IProductService;
import com.bcgogo.product.service.IPromotionsService;
import com.bcgogo.search.service.IItemIndexService;
import com.bcgogo.search.service.ISearchService;
import com.bcgogo.search.service.ItemIndexService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.stat.dto.SupplierRecordDTO;
import com.bcgogo.txn.bcgogoListener.orderEvent.PurchaseOrderSavedEvent;
import com.bcgogo.txn.bcgogoListener.publisher.BcgogoEventPublisher;
import com.bcgogo.txn.dto.*;
import com.bcgogo.txn.dto.StatementAccount.OrderDebtType;
import com.bcgogo.txn.dto.supplierComment.SupplierCommentRecordDTO;
import com.bcgogo.txn.service.*;
import com.bcgogo.txn.service.pushMessage.IOrderPushMessageService;
import com.bcgogo.txn.service.pushMessage.IPushMessageService;
import com.bcgogo.txn.service.pushMessage.ITradePushMessageService;
import com.bcgogo.txn.service.solr.ICustomerOrSupplierSolrWriteService;
import com.bcgogo.txn.service.solr.IOrderSolrWriterService;
import com.bcgogo.txn.service.supplierComment.ISupplierCommentService;
import com.bcgogo.user.dto.SupplierDTO;
import com.bcgogo.user.service.ISupplierService;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.utils.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

@Controller
@RequestMapping("/RFbuy.do")
public class RFGoodBuyController {
  private static final Logger LOGGER = LoggerFactory.getLogger(RFGoodBuyController.class);
  private static final String PAGE_GOODSBUY = "/txn/goodsBuy";
  private static final String PAGE_GOODSBUY_ONLINE = "/autoaccessoryonline/goodsBuyOnline";
  private static final String PAGE_MODIFY_GOODSBUY_ONLINE = "/autoaccessoryonline/modifyGoodsBuyOnline";
  private static final String PAGE_MODIFY_SINGLE_GOODSBUY_ONLINE = "/autoaccessoryonline/modifySingleGoodsBuyOnline";
  private static final String PAGE_GOODSBUY_FINISH = "/txn/goodsBuyFinish";
  private static final String PAGE_GOODSBUYPRINT = "/txn/print/goodsBuyPrint";
  private static final String MODEL_PURCHASEORDERDTO = "purchaseOrderDTO";
  private static final String PARAM_SUPPLIERID = "supplierId";
  private static final String PARAM_PRODUCTIDS = "productIds";
	private static final String REDIRECT_SHOW = "redirect:RFbuy.do?method=show";
  private static final String REDIRECT_MODIFY = "redirect:RFbuy.do?method=modifyPurchaseOrderOnline";

	private RFITxnService rfiTxnService;
	private IUserService userService;
	private IGoodBuyService goodBuyService;
	private IDraftOrderService draftOrderService;
	private IProductService productService;
	private ISearchService searchService;
	private ITxnService txnService;
	private IOperationLogService operationLogService;
	private IGoodSaleService goodSaleService;
	private IOrderPushMessageService orderPushMessageService;

	public RFITxnService getRfiTxnService() {
		if(rfiTxnService == null){
			rfiTxnService = ServiceManager.getService(RFITxnService.class);
		}
		return rfiTxnService;
	}

  public IOrderPushMessageService getOrderPushMessageService() {
    return orderPushMessageService == null ? orderPushMessageService = ServiceManager.getService(IOrderPushMessageService.class) : orderPushMessageService;
  }

	public void setRfiTxnService(RFITxnService rfiTxnService) {
		this.rfiTxnService = rfiTxnService;
	}

	public IUserService getUserService() {
		if(userService == null){
			userService = ServiceManager.getService(IUserService.class);
		}
		return userService;
	}

	public IGoodBuyService getGoodBuyService() {
		if(goodBuyService == null ){
			goodBuyService = ServiceManager.getService(IGoodBuyService.class);
		}
		return goodBuyService;
	}

	public IDraftOrderService getDraftOrderService() {
		if(draftOrderService == null){
			draftOrderService = ServiceManager.getService(IDraftOrderService.class);
		}
		return draftOrderService;
	}

	public IProductService getProductService() {
		if(productService == null){
			productService = ServiceManager.getService(IProductService.class);
		}
		return productService;
	}

	public ISearchService getSearchService() {
		if(searchService == null){
			searchService = ServiceManager.getService(ISearchService.class);
		}
		return searchService;
	}

	public ITxnService getTxnService() {
		return txnService == null ? ServiceManager.getService(ITxnService.class) : txnService;
	}

	public IOperationLogService getOperationLogService() {
		return operationLogService == null?ServiceManager.getService(IOperationLogService.class):operationLogService;
	}

	public IGoodSaleService getGoodSaleService(){
		return goodSaleService == null?ServiceManager.getService(IGoodSaleService.class) :goodSaleService;
	}

	@RequestMapping(params = "method=create")
  public String create(HttpServletRequest request, ModelMap modelMap) throws Exception {
    Long shopId = null;
    Long userId = null;
    String userName = null;
    try {
      ISupplierPayableService supplierPayableService = ServiceManager.getService(ISupplierPayableService.class);
      shopId=WebUtil.getShopId(request);
      userId =WebUtil.getUserId(request);
      userName = WebUtil.getUserName(request);
      IUserService userService = ServiceManager.getService(IUserService.class);
      String supplierId = request.getParameter(PARAM_SUPPLIERID);
      if (StringUtils.isBlank(supplierId)) {
        if (request.getParameter("supplierName") != null && !"".equals(request.getParameter("supplierName"))) {
          SupplierDTO supplierDTO = new SupplierDTO();
          supplierDTO.setName(request.getParameter("supplierName").trim());
          supplierDTO.setContact(request.getParameter("supplierName").trim());
          supplierDTO.setShopId((Long) request.getSession().getAttribute("shopId"));
          supplierDTO = userService.createSupplier(supplierDTO);
          ServiceManager.getService(ISupplierRecordService.class).createSupplierRecordUsingSupplierDTO(supplierDTO);
          ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexSupplierBySupplierId(supplierDTO.getId());
          supplierId = supplierDTO.getId().toString();
        }
      }

      String productIds = request.getParameter(PARAM_PRODUCTIDS);
      PurchaseOrderDTO purchaseOrderDTO = getRfiTxnService().createPurchaseOrder(shopId, userId, userName, supplierId, productIds);

      purchaseOrderDTO.setBillProducer((String)request.getSession().getAttribute("userName"));
      purchaseOrderDTO.setBillProducerId((Long)request.getSession().getAttribute("userId"));

//      if(StringUtils.isBlank(purchaseOrderDTO.getReceiptNo())) {
//        ITxnService txnService = ServiceManager.getService(ITxnService.class);
//        purchaseOrderDTO.setReceiptNo(txnService.getReceiptNo(WebUtil.getShopId(request), OrderTypes.PURCHASE, null));
//      }
      purchaseOrderDTO.setVestDateStr(DateUtil.dateLongToStr(purchaseOrderDTO.getVestDate(),DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN));

      if(StringUtils.isNotBlank(supplierId)) {
        List<Double> doubleList = supplierPayableService.getSumPayableBySupplierId(Long.valueOf(supplierId), shopId, OrderDebtType.SUPPLIER_DEBT_PAYABLE);//应付款总额 实付总额
        SupplierDTO supplierDTO = userService.getSupplierById(Long.valueOf(supplierId));
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


        SupplierRecordDTO supplierRecordDTO = supplierPayableService.getSupplierRecordDTOBySupplierId(shopId, Long.valueOf(supplierId));
        if (null != supplierRecordDTO && null != supplierRecordDTO.getDebt()) {
          supplierRecordDTO.setDebt(NumberUtil.round(supplierRecordDTO.getDebt(), NumberUtil.MONEY_PRECISION));
        }
        request.setAttribute("supplierRecordDTO", supplierRecordDTO);
      }
      purchaseOrderDTO.initDefaultItemUnit(ProductUnitCache.getProductUnitMap());
      modelMap.addAttribute(MODEL_PURCHASEORDERDTO, purchaseOrderDTO);
      Map settlementTypeList = TxnConstant.getSettlementTypeMap(request.getLocale());
      modelMap.addAttribute("settlementTypeList", settlementTypeList);
      Map invoiceCategoryList = TxnConstant.getInvoiceCatagoryMap(request.getLocale());
      modelMap.addAttribute("invoiceCategoryList", invoiceCategoryList);
      modelMap.addAttribute("wholesalerVersion", ConfigUtils.isWholesalerVersion(WebUtil.getShopVersionId(request)));
    } catch (Exception e) {
      LOGGER.debug("/RFbuy.do");
      LOGGER.debug("method=create");
      LOGGER.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      WebUtil.reThrow(LOGGER, e);
    }
    return PAGE_GOODSBUY;
  }

	@RequestMapping(params = "method=ajaxValidatorPurchaseOrderDTOSave")
	@ResponseBody
	public Object ajaxValidatorPurchaseOrderDTOSave(HttpServletRequest request, PurchaseOrderDTO purchaseOrderDTO) {
		try {
			Long shopId = WebUtil.getShopId(request);
			if (purchaseOrderDTO == null) {
				return  new Result(ValidatorConstant.ORDER_IS_NULL_MSG, false);
			}
			purchaseOrderDTO.setShopId(shopId);
      return getRfiTxnService().getDeletedProductValidatorResult(purchaseOrderDTO);
		} catch (Exception e) {
			LOGGER.error("ajaxValidatorPurchaseOrderDTOSave 验证出错bcgogoOrderDto:{}" + e.getMessage(), purchaseOrderDTO, e);
			return  new Result(ValidatorConstant.REQUEST_ERROR_MSG, false);
		}
	}

  @RequestMapping(params = "method=save")
  public String save(HttpServletRequest request, ModelMap modelMap, PurchaseOrderDTO purchaseOrderDTO) throws Exception {
    if(checkPurchaseOrderDTOEmpty(purchaseOrderDTO)){
      LOGGER.warn("开始执行采购时，数据异常，采购信息：purchaseOrderDTO 为空！");
      return create(request, modelMap);
    }
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    try {
	    LOGGER.debug("开始执行采购："+purchaseOrderDTO.toString());
	    //预处理订单信息
	    purchaseOrderDTO.parseOrder();
	    purchaseOrderDTO.setShopId(WebUtil.getShopId(request));
	    purchaseOrderDTO.setUserId(WebUtil.getUserId(request));
	      //采购单 单据号
      if(StringUtils.isBlank(purchaseOrderDTO.getReceiptNo())){
        purchaseOrderDTO.setReceiptNo(txnService.getReceiptNo(purchaseOrderDTO.getShopId(),OrderTypes.PURCHASE,null));
      }
		  purchaseOrderDTO.setStatus(OrderStatus.PURCHASE_ORDER_WAITING);


	    // 处理供应商逻辑
	    SupplierDTO supplierDTO = getGoodBuyService().handleSupplierForPurchase(purchaseOrderDTO);
	    //   处理商品逻辑
	    getGoodBuyService().handleProductForPurchaseOrder(purchaseOrderDTO);

	    //采购逻辑
	    getRfiTxnService().saveOrUpdatePurchaseOrder(purchaseOrderDTO);
      ServiceManager.getService(IProductHistoryService.class).saveProductHistoryForOrder(WebUtil.getShopId(request),purchaseOrderDTO);

	    LOGGER.debug("savingpurchaseOrderDTO {} done",purchaseOrderDTO);
	     //后续逻辑
	    getGoodBuyService().updateSupplierAfterPurchaseOrder(supplierDTO, purchaseOrderDTO);

	      //结算后 草稿单作废
      if(!StringUtil.isEmpty(purchaseOrderDTO.getDraftOrderIdStr()))
        getDraftOrderService().deleteDraftOrder(purchaseOrderDTO.getShopId(),NumberUtil.longValue(purchaseOrderDTO.getDraftOrderIdStr()));


       //author:zhangjuntao 常用产品保存
      purchaseOrderDTO.setCurrentUsedProductDTOList();
      purchaseOrderDTO.setCurrentUsedVehicleDTOList();
      BcgogoEventPublisher bcgogoEventPublisher = new BcgogoEventPublisher();
      PurchaseOrderSavedEvent purchaseOrderSavedEvent = new PurchaseOrderSavedEvent(purchaseOrderDTO);
      String isRunThread = request.getParameter("isRunThread");
      if (!"noRun".equals(isRunThread)) {
        bcgogoEventPublisher.publisherPurchaseOrderSaved(purchaseOrderSavedEvent);
      }
      request.setAttribute("UNIT_TEST", purchaseOrderSavedEvent); //单元测试
      purchaseOrderSavedEvent.setMainFlag(true);
    } catch (Exception e) {
      LOGGER.debug("/RFbuy.do");
      LOGGER.debug("method=save");
      LOGGER.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOGGER.debug(purchaseOrderDTO.toString());
	    WebUtil.reThrow(LOGGER, e);
    }finally {
	    BcgogoConcurrentController.release(ConcurrentScene.PURCHASE, purchaseOrderDTO.getId());
    }
	  modelMap.addAttribute("id", purchaseOrderDTO.getId());
	  return REDIRECT_SHOW;
  }

  private boolean checkPurchaseOrderDTOEmpty(PurchaseOrderDTO purchaseOrderDTO) {
    boolean isEmpty = true;
		if(purchaseOrderDTO == null){
			return isEmpty;
		}
    // 如果归属时间为空
    if (StringUtils.isBlank(purchaseOrderDTO.getVestDateStr())) {
      LOGGER.warn("good buy vest can't be null");
      purchaseOrderDTO.setVestDateStr(DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN, System.currentTimeMillis()));
    }
		if(ArrayUtils.isEmpty(purchaseOrderDTO.getItemDTOs())){
			return  isEmpty;
		}
		for(PurchaseOrderItemDTO itemDTO:purchaseOrderDTO.getItemDTOs()){
			if(itemDTO == null){
				continue;
			}
			if(StringUtils.isNotBlank(itemDTO.getProductName())){
				isEmpty = false;
				break;
			}
		}
		return isEmpty;
  }


  private void getPurchaseOrder(ModelMap modelMap, String id,Long shopId) throws Exception {
    try {
      PurchaseOrderDTO purchaseOrderDTO = getRfiTxnService().getPurchaseOrderDTOById(Long.parseLong(id), shopId);
      //页面显示采购单
	    if(purchaseOrderDTO == null){
		    purchaseOrderDTO = new PurchaseOrderDTO();
		    LOGGER.error("用id 为{},shopId 为{} getPurchaseOrder 的时候出错!！",id,shopId);  //todo BCSHOP-3092
	    }
      modelMap.addAttribute(MODEL_PURCHASEORDERDTO, purchaseOrderDTO);
	    SalesOrderDTO salesOrderDTO = getTxnService().getSalesOrderByPurchaseOrderId(purchaseOrderDTO.getId(),purchaseOrderDTO.getSupplierShopId());
	    if(salesOrderDTO!=null){
		    salesOrderDTO.getIsShortage();
		    purchaseOrderDTO.setSaleOrderReceiptNo(salesOrderDTO.getReceiptNo());
		    modelMap.addAttribute("salesOrderDTO",salesOrderDTO);
	    }
      LOGGER.debug("purchase order 显示 {} ", purchaseOrderDTO);
    } catch (Exception e) {
      LOGGER.debug("/RFbuy.do");
      LOGGER.debug("id:" + id);
      WebUtil.reThrow(LOGGER, e);
    }
  }

  @RequestMapping(params = "method=show")
  public String show(ModelMap modelMap, @RequestParam("id") String id,HttpServletRequest request) throws Exception {
    Long shopId = WebUtil.getShopId(request);
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    if (shopId == null) throw new Exception("shopId can't be null.");
    getPurchaseOrder(modelMap, id, shopId);

    PurchaseOrderDTO purchaseOrderDTO = (PurchaseOrderDTO) modelMap.get(MODEL_PURCHASEORDERDTO);
    if(purchaseOrderDTO!=null) {
      ISupplierPayableService supplierPayableService = ServiceManager.getService(ISupplierPayableService.class);
      List<Double> doubleList = supplierPayableService.getSumPayableBySupplierId(purchaseOrderDTO.getSupplierId(), purchaseOrderDTO.getShopId(), OrderDebtType.SUPPLIER_DEBT_PAYABLE);//应付款总额 实付总额

      //应付款总额
      Double totalPayable = doubleList.get(0);
      request.setAttribute("totalPayable", NumberUtil.round(totalPayable, NumberUtil.MONEY_PRECISION));

      SupplierRecordDTO supplierRecordDTO = supplierPayableService.getSupplierRecordDTOBySupplierId(purchaseOrderDTO.getShopId(), purchaseOrderDTO.getSupplierId());
      if (null != supplierRecordDTO && null != supplierRecordDTO.getDebt()) {
        supplierRecordDTO.setDebt(NumberUtil.round(supplierRecordDTO.getDebt(), NumberUtil.MONEY_PRECISION));
      }
      SupplierDTO supplierDTO = getUserService().getSupplierById(purchaseOrderDTO.getSupplierId());
      request.setAttribute("supplierRecordDTO", supplierRecordDTO);
      modelMap.addAttribute("supplierDTO", supplierDTO);
      if (purchaseOrderDTO.isWholesalerPurchase()) {
        generateOtherPurchaseOrderInfo(purchaseOrderDTO);
        ShopDTO shopDTO = configService.getShopById(supplierDTO.getSupplierShopId());
        modelMap.addAttribute("supplierShop", shopDTO);
        PurchaseInventoryDTO purchaseInventoryDTO = getTxnService().getPurchaseInventoryIdByPurchaseOrderId(purchaseOrderDTO.getShopId(), purchaseOrderDTO.getId());
        if (purchaseInventoryDTO != null) {
          purchaseOrderDTO.setPurchaseInventoryId(purchaseInventoryDTO.getId());
          //供应商点评
          ISupplierCommentService supplierCommentService = ServiceManager.getService(ISupplierCommentService.class);
          SupplierCommentRecordDTO commentRecordDTO = supplierCommentService.getCommentRecordByOrderId(purchaseOrderDTO.getId(), purchaseOrderDTO.getShopId());
          if (commentRecordDTO == null) {
            purchaseOrderDTO.setAddContent(false);
          } else {
            purchaseOrderDTO.setCommentStatusStr(commentRecordDTO.getCommentStatus().toString());
            purchaseOrderDTO.setSupplierCommentRecordIdStr(commentRecordDTO.getId().toString());
            purchaseOrderDTO.setQualityScore(commentRecordDTO.getQualityScore());
            purchaseOrderDTO.setSpeedScore(commentRecordDTO.getSpeedScore());
            purchaseOrderDTO.setAttitudeScore(commentRecordDTO.getAttitudeScore());
            purchaseOrderDTO.setPerformanceScore(commentRecordDTO.getPerformanceScore());
            purchaseOrderDTO.setSupplierCommentContent(commentRecordDTO.getFirstCommentContent());
            if (StringUtil.isNotEmpty(commentRecordDTO.getAddCommentContent())) {
              purchaseOrderDTO.setAddContent(false);
              purchaseOrderDTO.setAddCommentContent(commentRecordDTO.getAddCommentContent());
            } else {
              purchaseOrderDTO.setAddContent(true);
            }
          }
        }
        if (OrderStatus.SELLER_PENDING.equals(purchaseOrderDTO.getStatus())) {
          //拿最新的supplier  信息
          List<SupplierDTO> supplierDTOs = getUserService().getSupplierById(shopId, purchaseOrderDTO.getSupplierId());
          if (CollectionUtils.isNotEmpty(supplierDTOs)) {
            purchaseOrderDTO.setSupplierDTO(supplierDTOs.get(0));
          }
          //组装区域信息
          purchaseOrderDTO.setAreaInfo(AreaCacheManager.getAreaInfo(purchaseOrderDTO.getProvince(), purchaseOrderDTO.getCity(), purchaseOrderDTO.getRegion()));
          IImageService imageService = ServiceManager.getService(IImageService.class);
          List<ImageScene> imageSceneList= new ArrayList<ImageScene>();
          imageSceneList.add(ImageScene.PRODUCT_LIST_IMAGE_SMALL);
          imageService.addImageInfoHistoryToBcgogoItemDTO(imageSceneList,true,purchaseOrderDTO);
          return PAGE_MODIFY_SINGLE_GOODSBUY_ONLINE;
        }
      }
    }
    return PAGE_GOODSBUY_FINISH;
  }

  private void generateOtherPurchaseOrderInfo(PurchaseOrderDTO purchaseOrderDTO) throws Exception {
    List<PromotionsDTO> promotionsDTOs = null;
    IPromotionsService promotionsService = ServiceManager.getService(IPromotionsService.class);
    for (PurchaseOrderItemDTO purchaseOrderItemDTO : purchaseOrderDTO.getItemDTOs()) {
      if(purchaseOrderItemDTO.getQuotedPreBuyOrderItemId()!=null){
        purchaseOrderDTO.setFromQuotedPreBuyOrder(true);
        purchaseOrderItemDTO.setCustomPriceFlag(true);
      }else{
        if (StringUtils.isNotBlank(purchaseOrderItemDTO.getPromotionsId())) {
          String[] promotionsIdsStr = purchaseOrderItemDTO.getPromotionsId().split(",");
          Long [] promotionsIds= new Long[promotionsIdsStr.length];
          for(int i = 0; i<promotionsIdsStr.length; i++){
            promotionsIds[i] = Long.parseLong(promotionsIdsStr[i]);
          }
          promotionsDTOs = promotionsService.getPromotionsDTODetailById(purchaseOrderDTO.getSupplierShopId(),promotionsIds);
          purchaseOrderItemDTO.setPromotionsDTOs(promotionsDTOs);
          purchaseOrderItemDTO.setPromotionsInfoJson(JsonUtil.listToJson(promotionsDTOs));
        }else {
          if (OrderStatus.SELLER_PENDING.equals(purchaseOrderDTO.getStatus())) {
            //取最新的
            promotionsDTOs = promotionsService.getPromotionsDTODetailByProductLocalInfoId(purchaseOrderDTO.getSupplierShopId(),purchaseOrderItemDTO.getSupplierProductId());
            promotionsDTOs=null;
            Set<Long> promotionsIds=new HashSet<Long>();
            if(CollectionUtil.isNotEmpty(promotionsDTOs)){
              for(PromotionsDTO promotionsDTO:promotionsDTOs){
                if(promotionsDTO==null) continue;
                promotionsIds.add(promotionsDTO.getId());
              }
              purchaseOrderItemDTO.setPromotionsIds(promotionsIds);
              purchaseOrderItemDTO.setPromotionsDTOs(promotionsDTOs);
              purchaseOrderItemDTO.setPromotionsInfoJson(JsonUtil.listToJson(promotionsDTOs));
            }
          }
        }
      }

    }
  }

  @RequestMapping(params = "method=print")
  public void print(ModelMap modelMap, @RequestParam("id") String id,HttpServletRequest request,HttpServletResponse response) throws Exception {
    getPurchaseOrder(modelMap, id, WebUtil.getShopId(request));
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    IPrintService printService = ServiceManager.getService(IPrintService.class);
    try{
      ShopDTO shopDTO = configService.getShopById(WebUtil.getShopId(request));
      PrintTemplateDTO printTemplateDTO = printService.getSinglePrintTemplateDTOByShopIdAndType(WebUtil.getShopId(request), OrderTypes.PURCHASE);
      PurchaseOrderDTO purchaseOrderDTO = (PurchaseOrderDTO) modelMap.get(MODEL_PURCHASEORDERDTO);
      if(purchaseOrderDTO!=null){
        if(StringUtil.isEmpty(purchaseOrderDTO.getMobile())&&StringUtil.isNotEmpty(purchaseOrderDTO.getLandline())){
          purchaseOrderDTO.setMobile(purchaseOrderDTO.getLandline());
        }
      }
      purchaseOrderDTO.setShopLandLine(shopDTO.getLandline());
      PrintWriter out = response.getWriter();
      response.setContentType("text/html");
      response.setCharacterEncoding("UTF-8");
      if(null != printTemplateDTO){
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
        String myTemplateName = "goodsBuyPrint" + String.valueOf(WebUtil.getShopId(request));
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
        context.put("purchaseOrderDTO", purchaseOrderDTO);
        context.put("storeManagerMobile", null == shopDTO.getStoreManagerMobile() ? "" : shopDTO.getStoreManagerMobile());
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
    catch(Exception e)
    {
      LOGGER.debug("/RFbuy.do");
      LOGGER.debug("id:" + id);
      WebUtil.reThrow(LOGGER, e);
    }

  }

  @RequestMapping(params = "method=purchaseOrderRepeal")
  public String purchaseOrderRepeal(ModelMap model, Long id, HttpServletRequest request) throws Exception {
    RFITxnService rfiTxnService = ServiceManager.getService(RFITxnService.class);
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    IItemIndexService itemIndexService = ServiceManager.getService(ItemIndexService.class);
    ISearchService searchService = ServiceManager.getService(ISearchService.class);
    Long shopId = WebUtil.getShopId(request);
    Long userId = WebUtil.getUserId(request);

	  SalesOrderDTO salesOrderDTO = null;
	  PurchaseOrderDTO purchaseOrderDTO = null;
	  try {
      if (shopId == null) throw new Exception("shop id is null!");
      if (userId == null) throw new Exception("user id is null!");
      if (id == null) throw new Exception("purchaseOrder Id is null!");

      purchaseOrderDTO = rfiTxnService.getPurchaseOrderDTOById(id, shopId);
      if ( purchaseOrderDTO != null && purchaseOrderDTO.getSupplierShopId() != null) {
        salesOrderDTO = txnService.getSalesOrderByPurchaseOrderId(purchaseOrderDTO.getId(), purchaseOrderDTO.getSupplierShopId());
      }
      if (purchaseOrderDTO != null && purchaseOrderDTO.getId() != null) {
        BcgogoConcurrentController.lock(ConcurrentScene.PURCHASE, purchaseOrderDTO.getId());
      }
      if (salesOrderDTO != null && salesOrderDTO.getId() != null) {
        BcgogoConcurrentController.lock(ConcurrentScene.SALE, salesOrderDTO.getId());
      }

      if (OrderStatus.PURCHASE_ORDER_WAITING.equals(purchaseOrderDTO.getStatus())
          || OrderStatus.SELLER_PENDING.equals(purchaseOrderDTO.getStatus())
          || OrderStatus.PURCHASE_SELLER_STOP.equals(purchaseOrderDTO.getStatus())
          || OrderStatus.SELLER_REFUSED.equals(purchaseOrderDTO.getStatus())) {

        txnService.deleteInventoryRemindEventByShopIdAndPurchaseOrderId(purchaseOrderDTO.getShopId(), purchaseOrderDTO.getId());

        itemIndexService.updateItemIndexPurchaseOrderStatus(purchaseOrderDTO.getShopId(), OrderTypes.PURCHASE,
            purchaseOrderDTO.getId(), OrderStatus.PURCHASE_ORDER_REPEAL);
        rfiTxnService.updateReceivable(purchaseOrderDTO.getShopId(), purchaseOrderDTO.getId(), OrderTypes.PURCHASE, ReceivableStatus.REPEAL);
        txnService.updatePurchaseOrderStatus(purchaseOrderDTO.getShopId(), purchaseOrderDTO.getId(), OrderStatus.PURCHASE_ORDER_REPEAL, WebUtil.getUserId(request),null);
        IPromotionsService promotionsService = ServiceManager.getService(IPromotionsService.class);
        promotionsService.updatePromotionOrderRecordStatus(purchaseOrderDTO.getShopId(), purchaseOrderDTO.getId(), purchaseOrderDTO.getStatus());
        searchService.updateOrderIndex(purchaseOrderDTO.getShopId(), purchaseOrderDTO.getId(),
            OrderTypes.PURCHASE, OrderStatus.PURCHASE_ORDER_REPEAL);
        rfiTxnService.saveRepealOrderByOrderIdAndOrderType(shopId, purchaseOrderDTO.getId(), OrderTypes.PURCHASE);
        //reindex order solr
        ServiceManager.getService(IOrderSolrWriterService.class).reCreateOrderSolrIndex(ServiceManager.getService(IConfigService.class).getShopById(shopId), OrderTypes.PURCHASE, purchaseOrderDTO.getId());
        //保存采购单作废的操作日志
        ServiceManager.getService(ITxnService.class).saveOperationLogTxnService(new OperationLogDTO(purchaseOrderDTO.getShopId(), WebUtil.getUserId(request), purchaseOrderDTO.getId(), ObjectTypes.PURCHASE_ORDER, OperationTypes.INVALID));

        //add by WLF 删除采购待入库提醒
        getTxnService().cancelRemindEventByOrderId(RemindEventType.TXN, purchaseOrderDTO.getId());
        //更新缓存
        getTxnService().updateRemindCountInMemcacheByTypeAndShopId(RemindEventType.TXN, purchaseOrderDTO.getShopId());

        //add by WLF 更新缓存中待办采购单的数量
        List<Long> supplierIdList = ServiceManager.getService(IUserService.class).getRelatedSupplierIdListByShopId(shopId);
        getTxnService().updateTodoOrderCountInMemcacheByTypeAndShopId(RemindEventType.TODO_PURCHASE_ORDER, shopId, supplierIdList);

        //取消推送消息
        ServiceManager.getService(IPushMessageService.class).disabledPushMessageReceiverBySourceId(null, purchaseOrderDTO.getId(),null, PushMessageSourceType.SALE_NEW);

        if (salesOrderDTO != null && OrderStatus.PENDING.equals(salesOrderDTO.getStatus())) {
          getGoodBuyService().repealPurchaseSaleOrderDTO(salesOrderDTO);
          //保存关联销售单作废的操作日志
          ServiceManager.getService(ITxnService.class).saveOperationLogTxnService(new OperationLogDTO(salesOrderDTO.getShopId(), WebUtil.getUserId(request), salesOrderDTO.getId(), ObjectTypes.SALE_ORDER, OperationTypes.INVALID));
          itemIndexService.updateItemIndexPurchaseOrderStatus(salesOrderDTO.getShopId(), OrderTypes.SALE, salesOrderDTO.getId(), salesOrderDTO.getStatus());
          searchService.updateOrderIndex(salesOrderDTO.getShopId(), salesOrderDTO.getId(), OrderTypes.SALE, salesOrderDTO.getStatus());
          ServiceManager.getService(IOrderSolrWriterService.class).reCreateOrderSolrIndex(ServiceManager.getService(IConfigService.class).getShopById(salesOrderDTO.getShopId()), OrderTypes.SALE, salesOrderDTO.getId());
        }
        return "redirect:/RFbuy.do?method=show&id=" + id;
      }else{
        model.addAttribute("errorMsg","当前单据状态为"+purchaseOrderDTO.getStatus().getName()+",不能作废！");
        show(model,id.toString(),request);
        return PAGE_GOODSBUY_FINISH;
      }
    } catch (Exception e) {
		  LOGGER.error("采购单作废异常" + e.getMessage(), e);
		  return "redirect:/RFbuy.do?method=show&id=" + id;
	  } finally {
		  if (purchaseOrderDTO != null && purchaseOrderDTO.getId() != null) {
			  BcgogoConcurrentController.release(ConcurrentScene.PURCHASE, purchaseOrderDTO.getId());
		  }
		  if (salesOrderDTO != null && salesOrderDTO.getId() != null) {
			  BcgogoConcurrentController.release(ConcurrentScene.SALE, salesOrderDTO.getId());
		  }
	  }
  }

  @RequestMapping(params = "method=validateCopy")
  @ResponseBody
  public Result validateCopy(HttpServletRequest request, Long id){
    Long shopId = null;
    try {
      shopId = WebUtil.getShopId(request);
      if (shopId == null || id == null) {
        LOGGER.error("storage.do?method=validateCopy, shopId:{}, id:{}", shopId, id);
        return new Result("验证失败", "验证失败，请重试！", false);
      }
      //在线采购没有复制  所以不需要校验了
      return getGoodBuyService().validateCopy(id, shopId);
    } catch (Exception e) {
      LOGGER.error("storage.do?method=validateCopy, shopId:{}, id:{}", shopId, id);
      LOGGER.error(e.getMessage(), e);
      return new Result("验证失败", "验证失败，请重试！", false);
    }
  }

  /**
   * 采购单复制功能
   *
   * @param model
   * @param request
   * @param id
   * @return
   */
  @RequestMapping(params = "method=copyPurchaseOrder")
  public String copyPurchaseOrder(ModelMap model, HttpServletRequest request, Long id) throws Exception {
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    String userName = (String)request.getSession().getAttribute("userName");
    Long userId = (Long)request.getSession().getAttribute("userId");
    try {
      if (shopId == null || id == null) {
        LOGGER.error("RFbuy.do?method=copyPurchaseOrder,shopId = " + shopId + "purchaseOrderId=" + id + "\n");
        return create(request, model);
      }
      ITxnService txnService = ServiceManager.getService(ITxnService.class);
      PurchaseOrderDTO purchaseOrderDTO = txnService.getPurchaseOrder(id, shopId);
      if (purchaseOrderDTO == null) {
        LOGGER.error("RFbuy.do?method=copyPurchaseOrder, PurchaseOrderDTO not found, shopId = " + shopId + "purchaseOrderId=" + id + "\n");
        return create(request, model);
      }
      RFITxnService rftxnService = ServiceManager.getService(RFITxnService.class);

      if(purchaseOrderDTO.getSupplierId()!=null) {
        SupplierDTO supplierDTO = ServiceManager.getService(IUserService.class).getSupplierById(purchaseOrderDTO.getSupplierId());
        purchaseOrderDTO.setSupplierDTO(supplierDTO);
      }

      PurchaseOrderDTO  newPurchaseOrderDTO = rftxnService.copyPurchaseOrder(shopId, purchaseOrderDTO);
      newPurchaseOrderDTO.set(shopId, userId, userName);
      newPurchaseOrderDTO.setReceiptNo(null);
      newPurchaseOrderDTO.setBillProducer(userName);
      newPurchaseOrderDTO.setBillProducerId(userId);
      newPurchaseOrderDTO.setDraftOrderIdStr(null);

      model.addAttribute(MODEL_PURCHASEORDERDTO, purchaseOrderDTO);
        Map settlementTypeList = TxnConstant.getSettlementTypeMap(request.getLocale());
        model.addAttribute("settlementTypeList", settlementTypeList);
        Map invoiceCategoryList = TxnConstant.getInvoiceCatagoryMap(request.getLocale());
        model.addAttribute("invoiceCategoryList", invoiceCategoryList);
      return PAGE_GOODSBUY;

    } catch (Exception e) {
      LOGGER.error("RFbuy.do?method=copyPurchaseOrder, shopId = " + shopId + "purchaseOrderId=" + id + "\n" + e.getMessage());
      LOGGER.error(e.getMessage(), e);
      return create(request, model);
    }

  }

  @RequestMapping(params = "method=updatePurchaseOrderOnline")
  @ResponseBody
  public Object updatePurchaseOrderOnline(HttpServletRequest request,PurchaseOrderDTO purchaseOrderDTO) throws Exception {
    Long shopId = WebUtil.getShopId(request);
    Long userId = WebUtil.getUserId(request);
    String userName = WebUtil.getUserName(request);
    try {
      if (shopId == null) throw new Exception("shop id is null!");
      if (userId == null) throw new Exception("user id is null!");
      if (purchaseOrderDTO.getSupplierShopId() == null) throw new Exception("supplier shop id is null!");

      //预处理订单信息
      purchaseOrderDTO.parseOrder();
      purchaseOrderDTO.setShopId(shopId);
      purchaseOrderDTO.setUserId(userId);
      purchaseOrderDTO.setEditor(userName);
      purchaseOrderDTO.setEditorId(userId);
      purchaseOrderDTO.setEditDate(System.currentTimeMillis());

      removeNullSupplierProductRow(purchaseOrderDTO);
      if(ArrayUtils.isEmpty(purchaseOrderDTO.getItemDTOs())){
        return new Result("订单不能为空,请确认后再次提交订单!", false);
      }
      Result result = getGoodBuyService().verifyPurchaseModify(purchaseOrderDTO);
      if (result != null && !result.isSuccess()) {
        return result;
      }
      StringBuffer msg = new StringBuffer();
      result = new Result();
      List<String> data = new ArrayList<String>();
      validatorPurchaseOrderOnlineProduct(msg,data, result, purchaseOrderDTO);
      if(!result.isSuccess()){
        result.setMsg(msg.append(ValidatorConstant.WHOLESALER_PRODUCT_NOT_EXIST).toString());
        result.setData(data);
        result.setOperation(Result.Operation.UPDATE_PRODUCT_SALES_STATUS.toString());
        return result;
      }

      if (!BcgogoConcurrentController.lock(ConcurrentScene.PURCHASE, purchaseOrderDTO.getId())) {
        return new Result("当前单据正在被操作，请稍候再试", false);
      }
      //采购逻辑
      getRfiTxnService().saveOrUpdatePurchaseOrderOnlineNotCreateSalesOrderDTO(purchaseOrderDTO);
      ServiceManager.getService(IProductHistoryService.class).saveProductHistoryForOrder(purchaseOrderDTO.getSupplierShopId(),purchaseOrderDTO);
      //后续逻辑 供应商逻辑
      SupplierDTO supplierDTO = getUserService().getSupplierById(purchaseOrderDTO.getSupplierId());
      getGoodBuyService().updateSupplierAfterPurchaseOrder(supplierDTO, purchaseOrderDTO);

      BcgogoEventPublisher bcgogoEventPublisher = new BcgogoEventPublisher();
      PurchaseOrderSavedEvent purchaseOrderSavedEvent = new PurchaseOrderSavedEvent(purchaseOrderDTO);
      bcgogoEventPublisher.publisherPurchaseOrderSaved(purchaseOrderSavedEvent);

    } catch (Exception e) {
      LOGGER.error("/RFbuy.do");
      LOGGER.error("method=updatePurchaseOrderOnline");
      LOGGER.error("shopId:" + shopId + ",userId:" + userId);
      LOGGER.error(purchaseOrderDTO.toString());
      WebUtil.reThrow(LOGGER, e);
    }finally {
      BcgogoConcurrentController.release(ConcurrentScene.PURCHASE, purchaseOrderDTO.getId());
    }
    return new Result();
  }

  //TODO PAY ZhangJuntao
  @RequestMapping(params = "method=savePurchaseOrderOnline")
  public String savePurchaseOrderOnline(HttpServletRequest request, ModelMap modelMap, BcgogoOrderFormBean orderFormBean,String returnLinkType) throws Exception {
    Long shopId = WebUtil.getShopId(request);
    Long userId = WebUtil.getUserId(request);
    String userName = WebUtil.getUserName(request);
    ISupplierService supplierService = ServiceManager.getService(ISupplierService.class);
    try {
      if (shopId == null) throw new Exception("shop id is null!");
      if (userId == null) throw new Exception("user id is null!");
      List<Long> purchaseOrderIdList = new ArrayList<Long>();
      for(PurchaseOrderDTO purchaseOrderDTO : orderFormBean.getPurchaseOrderDTOs()){
        if (purchaseOrderDTO.getSupplierShopId() != null){
          boolean isNewOrder = purchaseOrderDTO.getId() == null;
          //预处理订单信息
          purchaseOrderDTO.parseOrder();
          purchaseOrderDTO.setShopId(shopId);
          purchaseOrderDTO.setUserId(userId);
          purchaseOrderDTO.setUserName(userName);
          purchaseOrderDTO.setEditDate(System.currentTimeMillis());
          purchaseOrderDTO.setEditor(userName);
          purchaseOrderDTO.setEditorId(userId);
          purchaseOrderDTO.setBillProducer(userName);
          purchaseOrderDTO.setBillProducerId(userId);
          //采购单 单据号
          if(StringUtils.isBlank(purchaseOrderDTO.getReceiptNo())){
            purchaseOrderDTO.setReceiptNo(getTxnService().getReceiptNo(purchaseOrderDTO.getShopId(), OrderTypes.PURCHASE, null));
          }
          purchaseOrderDTO.setStatus(OrderStatus.SELLER_PENDING);

          //没有供应商的通过供应商店铺生成供应商
          getGoodBuyService().createOnlineSupplier(purchaseOrderDTO);

          //采购逻辑
//          SalesOrderDTO salesOrderDTO = new SalesOrderDTO();
//          getRfiTxnService().saveOrUpdatePurchaseOrderOnline(purchaseOrderDTO, salesOrderDTO);
          getRfiTxnService().saveOrUpdatePurchaseOrderOnlineNotCreateSalesOrderDTO(purchaseOrderDTO);
          //生成推送消息
          if (isNewOrder) {
            IPreBuyOrderService preBuyOrderService = ServiceManager.getService(IPreBuyOrderService.class);
            ITradePushMessageService tradePushMessageService = ServiceManager.getService(ITradePushMessageService.class);
            QuotedPreBuyOrderItemDTO currentQuotedPreBuyOrderItemDTO = null;
            List<QuotedPreBuyOrderItemDTO> quotedPreBuyOrderItemDTOList =null;
            getOrderPushMessageService().createOrderPushMessageMessage(purchaseOrderDTO.getShopId(), purchaseOrderDTO.getSupplierShopId(), purchaseOrderDTO.getId(), PushMessageSourceType.SALE_NEW);

            Set<Long> pushShopIdSet = new HashSet<Long>();
            for(PurchaseOrderItemDTO purchaseOrderItemDTO:purchaseOrderDTO.getItemDTOs()){
              if(purchaseOrderItemDTO.getQuotedPreBuyOrderItemId()!=null){
                currentQuotedPreBuyOrderItemDTO = preBuyOrderService.getQuotedPreBuyOrderItemDTOById(purchaseOrderItemDTO.getQuotedPreBuyOrderItemId());
                if(currentQuotedPreBuyOrderItemDTO!=null){
                  pushShopIdSet.add(currentQuotedPreBuyOrderItemDTO.getShopId());
                  quotedPreBuyOrderItemDTOList = preBuyOrderService.getQuotedPreBuyOrderItemDTOsByPreBuyOrderId(null,currentQuotedPreBuyOrderItemDTO.getPreBuyOrderId());
                  if(CollectionUtils.isNotEmpty(quotedPreBuyOrderItemDTOList)){
                    for(QuotedPreBuyOrderItemDTO quotedPreBuyOrderItemDTO:quotedPreBuyOrderItemDTOList){
                      if(!pushShopIdSet.contains(quotedPreBuyOrderItemDTO.getShopId())){
                        tradePushMessageService.createQuotedOrderIgnoredPushMessage(purchaseOrderDTO.getShopId(),quotedPreBuyOrderItemDTO.getShopId(),quotedPreBuyOrderItemDTO.getId());
                        pushShopIdSet.add(quotedPreBuyOrderItemDTO.getShopId());
                      }
                    }
                  }
                  break;
                }
              }
            }
          }
          //采购成功会删购物车  更新购物车缓存
          ServiceManager.getService(IShoppingCartService.class).updateLoginUserShoppingCartInMemCache(shopId,userId);
          purchaseOrderIdList.add(purchaseOrderDTO.getId());

          //在线采购用 对方shopId   记录商品历史信息
          ServiceManager.getService(IProductHistoryService.class).saveProductHistoryForOrder(purchaseOrderDTO.getSupplierShopId(),purchaseOrderDTO);
          //后续逻辑 供应商逻辑
          SupplierDTO supplierDTO = getUserService().getSupplierById(purchaseOrderDTO.getSupplierId());
          getGoodBuyService().updateSupplierAfterPurchaseOrder(supplierDTO, purchaseOrderDTO);

          BcgogoEventPublisher bcgogoEventPublisher = new BcgogoEventPublisher();
          PurchaseOrderSavedEvent purchaseOrderSavedEvent = new PurchaseOrderSavedEvent(purchaseOrderDTO);
          bcgogoEventPublisher.publisherPurchaseOrderSaved(purchaseOrderSavedEvent);

//          BcgogoEventPublisher bcgogoSaleOrderEventPublisher = new BcgogoEventPublisher();
//          SaleOrderSavedEvent saleOrderSavedEvent = new SaleOrderSavedEvent(salesOrderDTO);
//          bcgogoSaleOrderEventPublisher.publisherSaleOrderSaved(saleOrderSavedEvent);

        }
      }
      modelMap.addAttribute("orderSuccess", true);
      modelMap.addAttribute("purchaseOrderIds",StringUtil.arrayToStr(",",purchaseOrderIdList.toArray(new Long[purchaseOrderIdList.size()])));
      modelMap.addAttribute("returnLinkType",returnLinkType);
    } catch (Exception e) {
      LOGGER.debug("/RFbuy.do");
      LOGGER.debug("method=savePurchaseOrderOnline");
      LOGGER.debug("shopId:" + shopId + ",userId:" + userId);
      WebUtil.reThrow(LOGGER, e);
    }
    return REDIRECT_MODIFY;
  }
  @RequestMapping(params = "method=modifyPurchaseOrderOnline")
  public String modifyPurchaseOrderOnline(HttpServletRequest request, ModelMap modelMap,String returnLinkType,Long... purchaseOrderIds) throws Exception {
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    Long shopId= WebUtil.getShopId(request);
    Long userId= WebUtil.getUserId(request);
    try {
      if (shopId == null) throw new Exception("shop id is null!");
      if (userId == null) throw new Exception("user id is null!");
      if(!ArrayUtils.isEmpty(purchaseOrderIds)){
        List<PurchaseOrderDTO> purchaseOrderDTOList = new ArrayList<PurchaseOrderDTO>();
        for(Long purchaseOrderId : purchaseOrderIds) {
          PurchaseOrderDTO purchaseOrderDTO = getRfiTxnService().getPurchaseOrderDTOById(purchaseOrderId, shopId);
          if(OrderStatus.SELLER_PENDING.equals(purchaseOrderDTO.getStatus())){
            SalesOrderDTO salesOrderDTO = getTxnService().getSalesOrderByPurchaseOrderId(purchaseOrderDTO.getId(), purchaseOrderDTO.getSupplierShopId());
            if (salesOrderDTO != null) {
              purchaseOrderDTO.setSaleOrderReceiptNo(salesOrderDTO.getReceiptNo());
            }
            generateOtherPurchaseOrderInfo(purchaseOrderDTO);
            //拿最新的supplier  信息
            List<SupplierDTO> supplierDTOs = getUserService().getSupplierById(shopId, purchaseOrderDTO.getSupplierId());
            if (CollectionUtils.isNotEmpty(supplierDTOs)) {
              purchaseOrderDTO.setSupplierDTO(supplierDTOs.get(0));
              ShopDTO supplierShopDTO = configService.getShopById(supplierDTOs.get(0).getSupplierShopId());
              purchaseOrderDTO.setQqArray(supplierShopDTO.getQqArray());

            }

            //组装区域信息
            purchaseOrderDTO.setAreaInfo(AreaCacheManager.getAreaInfo(purchaseOrderDTO.getProvince(), purchaseOrderDTO.getCity(), purchaseOrderDTO.getRegion()));
            purchaseOrderDTOList.add(purchaseOrderDTO);
          }
        }
        IImageService imageService = ServiceManager.getService(IImageService.class);
        List<ImageScene> imageSceneList= new ArrayList<ImageScene>();
        imageSceneList.add(ImageScene.PRODUCT_LIST_IMAGE_SMALL);
        imageService.addImageInfoHistoryToBcgogoItemDTO(imageSceneList,true,purchaseOrderDTOList.toArray(new PurchaseOrderDTO[purchaseOrderDTOList.size()]));
        modelMap.addAttribute("purchaseOrderDTOList",purchaseOrderDTOList);
        modelMap.addAttribute("returnLinkType",returnLinkType);
      }
    } catch (Exception e) {
      LOGGER.debug("/RFbuy.do");
      LOGGER.debug("method=modifyPurchaseOrderOnline");
      LOGGER.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      WebUtil.reThrow(LOGGER, e);
    }
    return PAGE_MODIFY_GOODSBUY_ONLINE;
  }


  @RequestMapping(params = "method=createPurchaseOrderOnlineByShoppingCart")
  public String createPurchaseOrderOnlineByShoppingCart(HttpServletRequest request, ModelMap modelMap,Long... shoppingCartItemIds) throws Exception {
    Long shopId= WebUtil.getShopId(request);
    Long userId= WebUtil.getUserId(request);
    String userName = WebUtil.getUserName(request);
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    try {
      if (shopId == null) throw new Exception("shop id is null!");
      if (userId == null) throw new Exception("user id is null!");
      if(!ArrayUtils.isEmpty(shoppingCartItemIds)){
        IShoppingCartService shoppingCartService = ServiceManager.getService(IShoppingCartService.class);
        List<ShoppingCartItemDTO> shoppingCartItemDTOList = shoppingCartService.getShoppingCartItemDTOById(shopId,userId,shoppingCartItemIds);
        Map<Long,Map<Long,Pair<Double,Long>>> paramMap = new HashMap<Long, Map<Long,Pair<Double,Long>>>();
        Map<Long,Pair<Double,Long>> productPairMap = null;

        List<PurchaseOrderDTO> purchaseOrderDTOList = new ArrayList<PurchaseOrderDTO>();
        for(ShoppingCartItemDTO shoppingCartItemDTO : shoppingCartItemDTOList){
          productPairMap = paramMap.get(shoppingCartItemDTO.getSupplierShopId());
          if(productPairMap==null){
            productPairMap = new HashMap<Long, Pair<Double,Long>>();
          }
          productPairMap.put(shoppingCartItemDTO.getProductLocalInfoId(),new Pair<Double, Long>(shoppingCartItemDTO.getAmount(),shoppingCartItemDTO.getId()));
          paramMap.put(shoppingCartItemDTO.getSupplierShopId(),productPairMap);
        }
        ISupplierService supplierService = ServiceManager.getService(ISupplierService.class);
        Map<Long,SupplierDTO> supplierDTOMap = supplierService.getSupplierBySupplierShopId(shopId,paramMap.keySet().toArray(new Long[paramMap.keySet().size()]));

        for(Map.Entry<Long,Map<Long,Pair<Double,Long>>> entry : paramMap.entrySet()){
          SupplierDTO supplierDTO =  supplierDTOMap.get(entry.getKey());
             //非关联采购的时候组装好供应商信息
          if(supplierDTO == null){
            supplierDTO = new SupplierDTO();
            ShopDTO supplierShopDTO = configService.getShopById(entry.getKey());
            supplierDTO.fromSupplierShopDTO(supplierShopDTO);
          }
          PurchaseOrderDTO purchaseOrderDTO = getRfiTxnService().createPurchaseOrderOnline(shopId, userId, userName,supplierDTO, entry.getValue());
          if(StringUtils.isBlank(purchaseOrderDTO.getReceiptNo())) {
            purchaseOrderDTO.setReceiptNo(getTxnService().getReceiptNo(shopId, OrderTypes.PURCHASE, null));
          }
          purchaseOrderDTOList.add(purchaseOrderDTO);
        }
        BcgogoOrderFormBean orderFormBean = new BcgogoOrderFormBean();
        IImageService imageService = ServiceManager.getService(IImageService.class);
        List<ImageScene> imageSceneList= new ArrayList<ImageScene>();
        imageSceneList.add(ImageScene.PRODUCT_LIST_IMAGE_SMALL);
        imageService.addImageInfoToBcgogoItemDTO(imageSceneList,true,purchaseOrderDTOList.toArray(new PurchaseOrderDTO[purchaseOrderDTOList.size()]));
        orderFormBean.setPurchaseOrderDTOs(purchaseOrderDTOList.toArray(new PurchaseOrderDTO[purchaseOrderDTOList.size()]));
        modelMap.addAttribute("orderFormBean",orderFormBean);
      }
    } catch (Exception e) {
      LOGGER.debug("/RFbuy.do");
      LOGGER.debug("method=createPurchaseOrderOnlineByShoppingCart");
      LOGGER.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      WebUtil.reThrow(LOGGER, e);
    }
    //返回配件报价
    modelMap.addAttribute("returnLinkType","ShoppingCart");
    return PAGE_GOODSBUY_ONLINE;
  }

  @RequestMapping(params = "method=createPurchaseOrderOnlineByProduct")
  public String createPurchaseOrderOnlineByProduct(HttpServletRequest request, ModelMap modelMap,String... paramString) throws Exception {
    Long shopId= WebUtil.getShopId(request);
    Long userId= WebUtil.getUserId(request);
    String userName = WebUtil.getUserName(request);
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    try {
      if (shopId == null) throw new Exception("shop id is null!");
      if (userId == null) throw new Exception("user id is null!");
      if(!ArrayUtils.isEmpty(paramString)){
        Map<Long,Map<Long,Pair<Double,Long>>> paramMap = new HashMap<Long, Map<Long,Pair<Double,Long>>>();
        Map<Long,Pair<Double,Long>> productPairMap = null;
        List<PurchaseOrderDTO> purchaseOrderDTOList = new ArrayList<PurchaseOrderDTO>();
        for(String param : paramString){
          String[] params = param.split("_");
          if(NumberUtil.isLongNumber(params[0]) && NumberUtil.isLongNumber(params[1])){
            productPairMap = paramMap.get(NumberUtil.longValue(params[0]));
            if(productPairMap==null){
              productPairMap = new HashMap<Long, Pair<Double,Long>>();
            }
            productPairMap.put(NumberUtil.longValue(params[1]),new Pair<Double, Long>(NumberUtil.doubleValue(params[2],0),null));
            paramMap.put(NumberUtil.longValue(params[0]),productPairMap);
          }
        }
        ISupplierService supplierService = ServiceManager.getService(ISupplierService.class);
        Map<Long,SupplierDTO> supplierDTOMap = supplierService.getSupplierBySupplierShopId(shopId,paramMap.keySet().toArray(new Long[paramMap.keySet().size()]));
        for(Map.Entry<Long,Map<Long,Pair<Double,Long>>> entry : paramMap.entrySet()){
          SupplierDTO supplierDTO =   supplierDTOMap.get(entry.getKey());
          ShopDTO supplierShopDTO = null;
          //非关联采购的时候组装好供应商信息
          if(supplierDTO == null){
            supplierDTO = new SupplierDTO();
            supplierShopDTO = configService.getShopById(entry.getKey());
            supplierDTO.fromSupplierShopDTO(supplierShopDTO);
          }else{
            supplierShopDTO = configService.getShopById(supplierDTO.getSupplierShopId());
          }

          PurchaseOrderDTO purchaseOrderDTO = getRfiTxnService().createPurchaseOrderOnline(shopId, userId, userName,supplierDTO, entry.getValue());
          purchaseOrderDTO.setQqArray(supplierShopDTO.getQqArray());
          if(StringUtils.isBlank(purchaseOrderDTO.getReceiptNo())) {
            purchaseOrderDTO.setReceiptNo(getTxnService().getReceiptNo(shopId, OrderTypes.PURCHASE, null));
          }
          purchaseOrderDTOList.add(purchaseOrderDTO);
        }
        BcgogoOrderFormBean orderFormBean = new BcgogoOrderFormBean();
        IImageService imageService = ServiceManager.getService(IImageService.class);
        List<ImageScene> imageSceneList= new ArrayList<ImageScene>();
        imageSceneList.add(ImageScene.PRODUCT_LIST_IMAGE_SMALL);
        imageService.addImageInfoToBcgogoItemDTO(imageSceneList,true,purchaseOrderDTOList.toArray(new PurchaseOrderDTO[purchaseOrderDTOList.size()]));

        orderFormBean.setPurchaseOrderDTOs(purchaseOrderDTOList.toArray(new PurchaseOrderDTO[purchaseOrderDTOList.size()]));
        modelMap.addAttribute("orderFormBean",orderFormBean);
      }
    } catch (Exception e) {
      LOGGER.debug("/RFbuy.do");
      LOGGER.debug("method=createPurchaseOrderOnlineByProduct");
      LOGGER.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      WebUtil.reThrow(LOGGER, e);
    }
    //返回配件报价
    modelMap.addAttribute("returnLinkType","CommodityQuotations");
    return PAGE_GOODSBUY_ONLINE;
  }

  @RequestMapping(params = "method=ajaxValidatorPurchaseOrderOnline")
  @ResponseBody
  public Object ajaxValidatorPurchaseOrderOnline(HttpServletRequest request,BcgogoOrderFormBean orderFormBean) {
    try {
      Long shopId = WebUtil.getShopId(request);
      if (orderFormBean==null || ArrayUtils.isEmpty(orderFormBean.getPurchaseOrderDTOs())) {
        return new Result("没有采购单可以提交!", false);
      }
      List<PurchaseOrderDTO> purchaseOrderDTOList = new ArrayList<PurchaseOrderDTO>();
      for(PurchaseOrderDTO purchaseOrderDTO : orderFormBean.getPurchaseOrderDTOs()){
        if(purchaseOrderDTO==null || purchaseOrderDTO.getSupplierShopId()!=null){
          purchaseOrderDTOList.add(purchaseOrderDTO);
        }
      }
      if (CollectionUtils.isEmpty(purchaseOrderDTOList)) {
        return new Result("没有采购单可以提交!", false);
      }
      ISupplierService supplierService = ServiceManager.getService(ISupplierService.class);
      Set<Long> supplierIdSet = new HashSet<Long>();

      for(PurchaseOrderDTO purchaseOrderDTO : purchaseOrderDTOList){
        removeNullSupplierProductRow(purchaseOrderDTO);
        if(ArrayUtils.isEmpty(purchaseOrderDTO.getItemDTOs())){
          return new Result("订单不能为空,请确认后再次提交订单!", false);
        }
        if(purchaseOrderDTO.getSupplierId() != null) {
          supplierIdSet.add(purchaseOrderDTO.getSupplierId());
        }
      }
//      Map<Long,SupplierDTO> supplierDTOMap = supplierService.getSupplierByIdSet(shopId,supplierIdSet);
      //检查供应商是否被删除 检查供应商关联关系
      StringBuffer msg = new StringBuffer();
      Result result = new Result();
//      List<SupplierDTO> supplierData = new ArrayList<SupplierDTO>();
//      for(SupplierDTO supplierDTO : supplierDTOMap.values()){
//        if(supplierDTO.getSupplierShopId()==null){
//          msg.append(supplierDTO.getName()+"<br>");
//          supplierData.add(supplierDTO);
//          result.setSuccess(false);
//        }
//      }
//      if(!result.isSuccess()){
//        msg.append("不是您的关联供应商,请删除后重新提交!");
//        result.setMsg(msg.toString());
//        result.setData(supplierData);
//        result.setOperation(Result.Operation.UPDATE_SUPPLIER_RELATION.toString());
//        return result;
//      }

        List<String> data = new ArrayList<String>();
        msg = new StringBuffer();
        for(PurchaseOrderDTO purchaseOrderDTO : purchaseOrderDTOList){
          validatorPurchaseOrderOnlineProduct(msg,data, result, purchaseOrderDTO);
        }
        if(!result.isSuccess()){
          result.setMsg(msg.append(ValidatorConstant.WHOLESALER_PRODUCT_NOT_EXIST).toString());
          result.setData(data);
          result.setOperation(Result.Operation.UPDATE_PRODUCT_SALES_STATUS.toString());
          return result;
        }
      return new Result();
    } catch (Exception e) {
      LOGGER.error("ajaxValidatorPurchaseOrderOnline 验证出错bcgogoOrderDto:{}" + e.getMessage(), orderFormBean.getPurchaseOrderDTOs(), e);
      return  new Result(ValidatorConstant.REQUEST_ERROR_MSG, false);
    }
  }

  private void validatorPurchaseOrderOnlineProduct(StringBuffer msg,List<String> data, Result result, PurchaseOrderDTO purchaseOrderDTO) throws Exception {
    List<ProductDTO> notExistProductDTOs = new ArrayList<ProductDTO>();

    for (PurchaseOrderItemDTO purchaseOrderItemDTO : purchaseOrderDTO.getItemDTOs()) {
      if(purchaseOrderItemDTO.getSupplierProductId()!=null){
        ProductLocalInfoDTO productLocalInfoDTO = getProductService().getProductLocalInfoById(purchaseOrderItemDTO.getSupplierProductId(), purchaseOrderDTO.getSupplierShopId());
        if(productLocalInfoDTO==null || !ProductStatus.InSales.equals(productLocalInfoDTO.getSalesStatus())){
          ProductDTO productDTO = new ProductDTO();
          productDTO.set(purchaseOrderDTO.getSupplierShopId(), purchaseOrderItemDTO);
          notExistProductDTOs.add(productDTO);
        }
      }else{
        ProductDTO productDTO = new ProductDTO();
        productDTO.set(purchaseOrderDTO.getSupplierShopId(), purchaseOrderItemDTO);
        ProductDTO dbProductDTO = CollectionUtil.uniqueResult(getProductService().getProductDTOsBy7P(purchaseOrderDTO.getSupplierShopId(), productDTO));
        if(dbProductDTO!=null){
          ProductLocalInfoDTO productLocalInfoDTO = getProductService().getProductLocalInfoByProductId(dbProductDTO.getId(), purchaseOrderDTO.getSupplierShopId());
          if(productLocalInfoDTO==null || !ProductStatus.InSales.equals(productLocalInfoDTO.getSalesStatus())){
            notExistProductDTOs.add(productDTO);
          }
        }else{
          notExistProductDTOs.add(productDTO);
        }
      }

    }
    if (CollectionUtils.isNotEmpty(notExistProductDTOs)) {
      for (ProductDTO productDTO : notExistProductDTOs) {
        msg.append(productDTO.getProductMsg());
        data.add(productDTO.getProductLocalInfoId().toString());
      }
      result.setSuccess(false);
    }
  }

  private void removeNullSupplierProductRow(PurchaseOrderDTO purchaseOrderDTO) {
    if (purchaseOrderDTO.getItemDTOs() != null) {
      PurchaseOrderItemDTO[] purchaseOrderItemDTOs = purchaseOrderDTO.getItemDTOs();
      List<PurchaseOrderItemDTO> purchaseOrderItemDTOList = new ArrayList<PurchaseOrderItemDTO>();
      for (int i = 0; i < purchaseOrderItemDTOs.length; i++) {
        if (purchaseOrderItemDTOs[i].getSupplierProductId()!=null) {
          purchaseOrderItemDTOList.add(purchaseOrderItemDTOs[i]);
        }
      }
      if (CollectionUtils.isNotEmpty(purchaseOrderItemDTOList)) {
        purchaseOrderDTO.setItemDTOs(purchaseOrderItemDTOList.toArray(new PurchaseOrderItemDTO[purchaseOrderItemDTOList.size()]));
      }else{
        purchaseOrderDTO.setItemDTOs(null);
      }
    }
  }


  /**
   * 只能根据 同一个店铺的多个报价Item 直接下单  不走促销
   * @param request
   * @param modelMap
   * @param quotedPreBuyOrderItemIds
   * @return
   * @throws Exception
   */
  @RequestMapping(params = "method=createPurchaseOrderOnlineByQuotedPreBuyOrder")
  public String createPurchaseOrderOnlineByQuotedPreBuyOrder(HttpServletRequest request, ModelMap modelMap,Long... quotedPreBuyOrderItemIds) throws Exception {
    Long shopId= WebUtil.getShopId(request);
    Long userId= WebUtil.getUserId(request);
    String userName = WebUtil.getUserName(request);
    try {
      if (shopId == null) throw new Exception("shop id is null!");
      if (userId == null) throw new Exception("user id is null!");
      if(!ArrayUtils.isEmpty(quotedPreBuyOrderItemIds)){
        IPreBuyOrderService preBuyOrderService = ServiceManager.getService(IPreBuyOrderService.class);
        IConfigService configService = ServiceManager.getService(IConfigService.class);
        List<QuotedPreBuyOrderItemDTO> quotedPreBuyOrderItemDTOList = preBuyOrderService.getQuotedPreBuyOrderItemDTOsByItemId(quotedPreBuyOrderItemIds);

        if(CollectionUtils.isNotEmpty(quotedPreBuyOrderItemDTOList)){
          Set<Long> productIdSet = new HashSet<Long>();
          HashSet<Long> supplierShopIdSet = new HashSet<Long>();
          for(QuotedPreBuyOrderItemDTO quotedPreBuyOrderItemDTO : quotedPreBuyOrderItemDTOList){
            productIdSet.add(quotedPreBuyOrderItemDTO.getProductId());
            supplierShopIdSet.add(quotedPreBuyOrderItemDTO.getShopId());
          }
          if(supplierShopIdSet.size()!=1){
            throw  new Exception("shop Id  is not same!");
          }
          ISupplierService supplierService = ServiceManager.getService(ISupplierService.class);
          Map<Long,SupplierDTO> supplierDTOMap = supplierService.getSupplierBySupplierShopId(shopId,supplierShopIdSet.toArray(new Long[supplierShopIdSet.size()]));

          PurchaseOrderDTO purchaseOrderDTO = new PurchaseOrderDTO();
          purchaseOrderDTO.set(shopId, userId, userName);
          Long supplierShopId = NumberUtil.longValue(CollectionUtils.get(supplierShopIdSet,0));
          SupplierDTO supplierDTO =  supplierDTOMap.get(supplierShopId);
          //未生成关联供应商的时候需要new 一个supplierDTO
          if(supplierDTO == null && supplierShopId!=null){
            ShopDTO supplierShopDTO = configService.getShopById(supplierShopId);
            if(supplierShopDTO != null){
              supplierDTO = new SupplierDTO();
              supplierDTO.fromSupplierShopDTO(supplierShopDTO);
            }
          }
          purchaseOrderDTO.setSupplierDTO(supplierDTO);
          purchaseOrderDTO.setAreaInfo(AreaCacheManager.getAreaInfo(purchaseOrderDTO.getProvince(),purchaseOrderDTO.getCity(),purchaseOrderDTO.getRegion()));
          purchaseOrderDTO.setFromQuotedPreBuyOrder(true);
          List<PurchaseOrderItemDTO> purchaseOrderItemDTOList = new ArrayList<PurchaseOrderItemDTO>();
          double orderTotal = 0d;
          Map<Long,ProductDTO> productDTOMap = getProductService().getProductDTOMapByProductLocalInfoIds(purchaseOrderDTO.getSupplierShopId(),productIdSet);
          PurchaseOrderItemDTO purchaseOrderItemDTO= null;
          for(QuotedPreBuyOrderItemDTO quotedPreBuyOrderItemDTO : quotedPreBuyOrderItemDTOList){
            ProductDTO productDTO = productDTOMap.get(quotedPreBuyOrderItemDTO.getProductId());
            if (productDTO != null) {
              purchaseOrderItemDTO = new PurchaseOrderItemDTO();
              PreBuyOrderItemDTO preBuyOrderItemDTO = preBuyOrderService.getPreBuyOrderItemDTOById(quotedPreBuyOrderItemDTO.getPreBuyOrderItemId());
              purchaseOrderItemDTO.setAmount(preBuyOrderItemDTO.getAmount());
              purchaseOrderItemDTO.setQuotedPreBuyOrderItemId(quotedPreBuyOrderItemDTO.getId());
              purchaseOrderItemDTO.setWholesalerProductDTO(productDTO);
              purchaseOrderItemDTO.setUnit(quotedPreBuyOrderItemDTO.getUnit());
              purchaseOrderItemDTO.setInventoryAmount(productDTO.getInSalesAmount());//批发商的上架量
              purchaseOrderItemDTO.setPrice(quotedPreBuyOrderItemDTO.getPrice());//报价
              purchaseOrderItemDTO.setQuotedPrice(quotedPreBuyOrderItemDTO.getPrice());

              purchaseOrderItemDTO.setTotal(NumberUtil.round(purchaseOrderItemDTO.getAmount()*purchaseOrderItemDTO.getPrice(), NumberUtil.MONEY_PRECISION));
              orderTotal+=purchaseOrderItemDTO.getTotal();
              purchaseOrderItemDTOList.add(purchaseOrderItemDTO);
            }
          }
          purchaseOrderDTO.setItemDTOs(purchaseOrderItemDTOList.toArray(new PurchaseOrderItemDTO[purchaseOrderItemDTOList.size()]));
          purchaseOrderDTO.setTotal(NumberUtil.round(orderTotal,NumberUtil.MONEY_PRECISION));

          getGoodBuyService().setLocalInfoWithProductMapping(purchaseOrderDTO);

          if(StringUtils.isBlank(purchaseOrderDTO.getReceiptNo())) {
            purchaseOrderDTO.setReceiptNo(getTxnService().getReceiptNo(shopId, OrderTypes.PURCHASE, null));
          }
          BcgogoOrderFormBean orderFormBean = new BcgogoOrderFormBean();
          List<PurchaseOrderDTO> purchaseOrderDTOList = new ArrayList<PurchaseOrderDTO>();
          purchaseOrderDTOList.add(purchaseOrderDTO);
          IImageService imageService = ServiceManager.getService(IImageService.class);
          List<ImageScene> imageSceneList= new ArrayList<ImageScene>();
          imageSceneList.add(ImageScene.PRODUCT_LIST_IMAGE_SMALL);
          imageService.addImageInfoToBcgogoItemDTO(imageSceneList,true,purchaseOrderDTOList.toArray(new PurchaseOrderDTO[purchaseOrderDTOList.size()]));

          orderFormBean.setPurchaseOrderDTOs(purchaseOrderDTOList.toArray(new PurchaseOrderDTO[purchaseOrderDTOList.size()]));
          modelMap.addAttribute("orderFormBean",orderFormBean);
        }
      }
    } catch (Exception e) {
      LOGGER.debug("/RFbuy.do");
      LOGGER.debug("method=createPurchaseOrderOnlineByQuotedPreBuyOrder");
      LOGGER.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      WebUtil.reThrow(LOGGER, e);
    }
    //返回配件报价
    modelMap.addAttribute("returnLinkType","MyBuying");
    return PAGE_GOODSBUY_ONLINE;
  }

}
