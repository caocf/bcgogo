package com.bcgogo.autoaccessoryonline;

import com.bcgogo.common.Pager;
import com.bcgogo.common.Result;
import com.bcgogo.common.WebUtil;
import com.bcgogo.config.cache.BcgogoConcurrentController;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.enums.*;
import com.bcgogo.enums.txn.pushMessage.PushMessageSourceType;
import com.bcgogo.product.dto.ProductMappingDTO;
import com.bcgogo.product.service.IProductService;
import com.bcgogo.search.dto.OrderSearchConditionDTO;
import com.bcgogo.search.dto.OrderSearchResultDTO;
import com.bcgogo.search.dto.OrderSearchResultListDTO;
import com.bcgogo.search.service.order.ISearchOrderService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.bcgogoListener.orderEvent.PurchaseReturnSavedEvent;
import com.bcgogo.txn.bcgogoListener.publisher.BcgogoEventPublisher;
import com.bcgogo.txn.dto.*;
import com.bcgogo.txn.service.*;
import com.bcgogo.txn.service.productThrough.IProductThroughService;
import com.bcgogo.txn.service.pushMessage.IOrderPushMessageService;
import com.bcgogo.txn.service.web.IGoodsStorageService;
import com.bcgogo.user.dto.SupplierDTO;
import com.bcgogo.user.service.ISupplierService;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.user.service.utils.BcgogoShopLogicResourceUtils;
import com.bcgogo.utils.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-3-21
 * Time: 下午2:33
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/onlineReturn.do")
public class OnlineReturnController {
  private static final Logger LOG = LoggerFactory.getLogger(AutoAccessoryOnlineController.class);
  private static final String REDIRECT_SHOW = "redirect:goodsReturn.do?method=showReturnStorageByPurchaseReturnId";
   private static final String REDIRECT_MODIFY = "redirect:onlineReturn.do?method=modifyReturnStorage";


  private ISearchOrderService searchOrderService;
  private IConfigService configService;
  private IGoodsStorageService goodsStorageService;
  private IPurchaseReturnService purchaseReturnService;

  public ISearchOrderService getSearchOrderService() {
    return searchOrderService == null ? ServiceManager.getService(ISearchOrderService.class) : searchOrderService;
  }

  public IConfigService getConfigService() {
    return configService == null ? ServiceManager.getService(IConfigService.class) : configService;
  }

  public IGoodsStorageService getGoodsStorageService() {
    return goodsStorageService  == null ? ServiceManager.getService(IGoodsStorageService.class) : goodsStorageService;
  }

  public IPurchaseReturnService getPurchaseReturnService() {
    return purchaseReturnService == null ? ServiceManager.getService(IPurchaseReturnService.class) : purchaseReturnService;
  }

  @RequestMapping(params = "method=toOnlinePurchaseReturnSelect")
  public String toOnlineReturnSelect(ModelMap modelMap, HttpServletRequest request,OrderSearchConditionDTO searchConditionDTO) {
    Long shopId = null;
    try {
      shopId = WebUtil.getShopId(request);
      if (shopId == null) throw new Exception("shopId can't be null.");
      if(StringUtil.isEmpty(searchConditionDTO.getStartTimeStr()) &&StringUtil.isEmpty(searchConditionDTO.getEndTimeStr())){
        searchConditionDTO.setEndTimeStr(DateUtil.convertDateLongToDateString(DateUtil.DEFAULT,System.currentTimeMillis()));
        searchConditionDTO.setStartTimeStr(DateUtil.convertDateLongToDateString(DateUtil.DEFAULT,DateUtil.getInnerDayTime(-30)));
      }
      modelMap.addAttribute("searchConditionDTO", searchConditionDTO);
      return "/autoaccessoryonline/onlinePurchaseReturnSelectList";
    } catch (Exception e) {
      LOG.debug("/onlineReturn.do?method=toOnlinePurchaseReturnSelect");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
      return null;
    }
  }

  @ResponseBody
  @RequestMapping(params = "method=getOnlinePurchaseList")
  public Object getOnlinePurchaseList(ModelMap modelMap, HttpServletRequest request,OrderSearchConditionDTO orderSearchConditionDTO) {
    Long shopId = null;
     //配合pageAJAX.jsp 使用的数据格式
    List<Object> returnResult = new ArrayList<Object>();
    try {
      OrderSearchResultListDTO orderSearchResultListDTO = null;
        //数据校验
       shopId = WebUtil.getShopId(request);
      if (orderSearchConditionDTO == null){
        orderSearchConditionDTO = new OrderSearchConditionDTO();
      }
      orderSearchConditionDTO.setPageRows(orderSearchConditionDTO.getMaxRows());
      orderSearchConditionDTO.setRowStart((orderSearchConditionDTO.getStartPageNo() - 1) * orderSearchConditionDTO.getMaxRows());
      if (shopId == null) throw new Exception("shopId can't be null.");
      //校验时间
      orderSearchConditionDTO.verificationQueryTime();
      orderSearchConditionDTO.setShopId(shopId);
      ShopDTO shopDTO = ServiceManager.getService(IConfigService.class).getShopById(shopId);
      orderSearchConditionDTO.setShopKind(shopDTO.getShopKind());

      orderSearchConditionDTO.setOrderType(new String[]{"PURCHASE"});
      orderSearchConditionDTO.setOrderStatusRepeal("NO");
      orderSearchConditionDTO.setOrderStatus(new String[]{"PURCHASE_ORDER_DONE"});
      orderSearchConditionDTO.setSort("created_time desc");
      orderSearchConditionDTO.setFacetFields(new String[]{"order_type"});
      Set<Long> supplierShopIds =  getConfigService().getRelationWholesalerShopIds(shopId, RelationTypes.CUSTOMER_RELATE_TO_WHOLESALER_LIST);
      if( CollectionUtil.isNotEmpty(supplierShopIds)){
        orderSearchConditionDTO.setCustomerOrSupplierShopIds(StringUtil.parseStringArray(supplierShopIds));
        orderSearchResultListDTO = getSearchOrderService().queryOrders(orderSearchConditionDTO);
        buildPurchaseOrderInfo(shopId,orderSearchResultListDTO);
        returnResult.add(orderSearchResultListDTO);
        returnResult.add(new Pager(new Long(orderSearchResultListDTO.getNumFound()).intValue(),orderSearchConditionDTO.getStartPageNo(), orderSearchConditionDTO.getMaxRows()));
      } else{
        returnResult.add(new OrderSearchResultListDTO());
        Pager pager = new Pager();
        pager.setTotalRows(0);
        returnResult.add(pager);
      }
      return returnResult;
    } catch (Exception e) {
      LOG.debug("/onlineReturn.do?method=getOnlinePurchaseList");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
      return null;
    }
  }

  private void buildPurchaseOrderInfo(Long shopId,OrderSearchResultListDTO orderSearchResultListDTO)throws Exception{
    IGoodsStorageService goodsStorageService = ServiceManager.getService(IGoodsStorageService.class);
     if(orderSearchResultListDTO == null){
       return;
     }
    List<OrderSearchResultDTO> orders = orderSearchResultListDTO.getOrders();
    Set<Long> purchaseOrderId = new HashSet<Long>();
    if(CollectionUtil.isNotEmpty(orders)){
      for(OrderSearchResultDTO orderSearchResultDTO : orders){
        purchaseOrderId.add(orderSearchResultDTO.getOrderId());
      }
    }
    Map<Long, PurchaseInventoryDTO> purchaseInventoryDTOMap = goodsStorageService.getSimplePurchaseInventoryByPurchaseOrderIds(
        shopId, purchaseOrderId.toArray(new Long[purchaseOrderId.size()]));
    if(CollectionUtil.isNotEmpty(orders)){
      for(OrderSearchResultDTO orderSearchResultDTO : orders){
        PurchaseInventoryDTO purchaseInventoryDTO = purchaseInventoryDTOMap.get(orderSearchResultDTO.getOrderId());
        if(purchaseInventoryDTO != null){
          orderSearchResultDTO.setPurchaseInventoryId(purchaseInventoryDTO.getId());
          orderSearchResultDTO.setPurchaseInventoryReceiptNo(purchaseInventoryDTO.getReceiptNo());
        }
        //此处单据时间格式为yyyy-MM-dd HH:mm ;
        if(orderSearchResultDTO.getCreatedTime() != null){
          orderSearchResultDTO.setCreatedTimeStr(DateUtil.dateLongToStr(orderSearchResultDTO.getCreatedTime(),DateUtil.DATE_STRING_FORMAT_DEFAULT));
        }
      }
    }
  }

  @RequestMapping(params = "method=onlinePurchaseReturnEdit")
   public String onlinePurchaseReturnEdit(ModelMap modelMap, HttpServletRequest request,Long purchaseOrderId) {
     Long shopId = null;
     try {
       shopId = WebUtil.getShopId(request);
       Result result = getPurchaseReturnService().validatePurchaseReturnByPurchaseOrderId(shopId,purchaseOrderId);
       if(result!=null && !result.isSuccess()){
          modelMap.addAttribute("result",result);
          modelMap.addAttribute("purchaseReturnDTO",new PurchaseReturnDTO());
         return "/autoaccessoryonline/onlineReturnEdit";
       }
       //支持一张采购单某几个item退货
       Set<Long> purchaseOrderItemIds = new HashSet<Long>();
       if (StringUtils.isNotBlank(request.getParameter("purchaseOrderItemIds"))) {
         String[] purchaseOrderItemIdArray = request.getParameter("purchaseOrderItemIds").split(",");
         if (!ArrayUtils.isEmpty(purchaseOrderItemIdArray)) {
           for (String itemId : purchaseOrderItemIdArray) {
             if (StringUtils.isNotBlank(itemId) && StringUtils.isNumeric(itemId)) {
               purchaseOrderItemIds.add(Long.parseLong(itemId));
             }
           }
         }
       }
       PurchaseReturnDTO purchaseReturnDTO = getPurchaseReturnService().createOnlinePurchaseReturnByPurchaseOrderId(shopId,purchaseOrderId,purchaseOrderItemIds);

       if(BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(WebUtil.getShopVersionId(request))){
         IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
         List<StoreHouseDTO> storeHouseDTOList = storeHouseService.getAllStoreHousesByShopId(shopId);
         modelMap.addAttribute("storeHouseDTOList", storeHouseDTOList);//select 选项
         if(CollectionUtils.isNotEmpty(storeHouseDTOList) && purchaseReturnDTO.getStorehouseId()==null){
           if(storeHouseDTOList.size()==1){
             purchaseReturnDTO.setStorehouseId(storeHouseDTOList.get(0).getId());
           }
         }
         InventoryService inventoryService = ServiceManager.getService(InventoryService.class);
         //更新库存 根据仓库
         inventoryService.updateItemDTOInventoryAmountByStorehouse(shopId,purchaseReturnDTO.getStorehouseId(), purchaseReturnDTO);
       }
       modelMap.addAttribute("purchaseReturnDTO",purchaseReturnDTO);
       SupplierDTO supplierDTO = ServiceManager.getService(IUserService.class).getSupplierById(purchaseReturnDTO.getSupplierId());
       modelMap.addAttribute("supplierDTO", supplierDTO);
       ShopDTO supplierShopDTO = ServiceManager.getService(IConfigService.class).getShopById(supplierDTO.getSupplierShopId());
       modelMap.addAttribute("supplierShop", supplierShopDTO);
       if (ArrayUtil.isEmpty(purchaseReturnDTO.getItemDTOs())) {
         modelMap.addAttribute("result", new Result("当前采购单无法生成在线退货单，请采用线下退货单,重新填写商品。",false));
       }
       return "/autoaccessoryonline/onlineReturnEdit";
     } catch (Exception e) {
       LOG.debug("/onlineReturn.do?method=onlinePurchaseReturnEdit");
       LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
       LOG.error(e.getMessage(), e);
       return null;
     }
   }

  /**
   * 根据前台ajax提交的dto进行验证 返回校验结果
   *  在线退货单校验
   * @param request
   * @param purchaseReturnDTO 退货单
   */
  @RequestMapping(params = "method=validateReturnOrder")
  @ResponseBody
  public Result validateReturnOrder(HttpServletRequest request, PurchaseReturnDTO purchaseReturnDTO) {
    RFITxnService rfiTxnService = ServiceManager.getService(RFITxnService.class);
    IGoodBuyService goodBuyService = ServiceManager.getService(IGoodBuyService.class);
    ISupplierService supplierService = ServiceManager.getService(ISupplierService.class);
    try {
      Long shopId = (Long) request.getSession().getAttribute("shopId");
      Long shopVersionId = WebUtil.getShopVersionId(request);
      //校验供应商是否为关联供应商
      SupplierDTO supplierDTO = supplierService.getSupplierDTONoContact(purchaseReturnDTO.getSupplierId(),shopId);
      if(supplierDTO == null || supplierDTO.getSupplierShopId() == null){
        return new Result("当前供应商不是关联供应商，无法在线退货！", false);
      }

      //校验采购单状态
       PurchaseOrderDTO purchaseOrderDTO = goodBuyService.getSimplePurchaseOrderDTO(shopId,purchaseReturnDTO.getPurchaseOrderId());
       if(purchaseOrderDTO == null){
         return new Result("关联的在线采购单不存在，无法退货！", false);
       }else if(!OrderStatus.PURCHASE_ORDER_DONE.equals(purchaseOrderDTO.getStatus())){
         return new Result("关联的在线采购单状态不是已入库，无法退货！", false);
       }
      //去掉空行
      List<Long> productIdList = removeNullProductRow(purchaseReturnDTO);

      if (ArrayUtil.isEmpty(purchaseReturnDTO.getItemDTOs())) {
        return new Result(ValidatorConstant.ORDER_NULL_MSG, false);
      } else if (!ArrayUtil.isEmpty(purchaseReturnDTO.getItemDTOs()) && purchaseReturnDTO.getItemDTOs().length>productIdList.size()) {
        return new Result(ValidatorConstant.ORDER_NEW_PRODUCT_ERROR, false);
      } else {
        //如果是批发商  校验所退商品 是不是  跟这个批发商有关系的
        if (purchaseReturnDTO.getSupplierShopId() != null) {
          IProductService productService = ServiceManager.getService(IProductService.class);
          Map<Long, ProductMappingDTO> productMappingDTOMap = productService.getCustomerProductMappingDTOMap(shopId, purchaseReturnDTO.getSupplierShopId(), productIdList.toArray(new Long[productIdList.size()]));
          for (PurchaseReturnItemDTO itemDTO : purchaseReturnDTO.getItemDTOs()) {
            if (!productMappingDTOMap.keySet().contains(itemDTO.getProductId())) {
              return new Result(ValidatorConstant.ORDER_PRODUCT_MATCH_SUPPLIER_ERROR, false);
            }
          }
        }
        IInventoryService inventoryService = ServiceManager.getService(IInventoryService.class);
        if (BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(shopVersionId)) {
          //通过仓库校验库存
          if (purchaseReturnDTO.getStorehouseId() != null) {
            IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
            StoreHouseDTO storeHouseDTO = storeHouseService.getStoreHouseDTOById(shopId,purchaseReturnDTO.getStorehouseId());
            if(storeHouseDTO==null || DeletedType.TRUE.equals(storeHouseDTO.getDeleted())){
              return new Result(ValidatorConstant.STOREHOUSE_DELETED_MSG, false);
            }
            Map<String, String> data = new HashMap<String, String>();
            if (!inventoryService.checkBatchProductInventoryByStoreHouse(shopId, purchaseReturnDTO.getStorehouseId(), purchaseReturnDTO.getItemDTOs(), data, productIdList)) {
              return new Result(ValidatorConstant.PRODUCT_INVENTORY_LACK, false, Result.Operation.UPDATE_PRODUCT_INVENTORY.getValue(), data);
            }
          } else {
            return new Result(ValidatorConstant.STOREHOUSE_NULL_MSG, false);
          }
        }else {
          //校验产品库存
          Map<String, String> data = new HashMap<String, String>();
          if (!inventoryService.checkBatchProductInventory(shopId, purchaseReturnDTO, data, productIdList)) {
            return new Result(ValidatorConstant.PRODUCT_INVENTORY_LACK, false, Result.Operation.UPDATE_PRODUCT_INVENTORY.getValue(), data);
          }
        }
      }
      purchaseReturnDTO.setShopId(shopId);
      return rfiTxnService.getDeletedProductValidatorResult(purchaseReturnDTO);
    } catch (Exception e) {
      LOG.error("/onlineReturn.do");
      LOG.error("method=validateReturnOrder");
      LOG.error("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
      return new Result(MemberConstant.SUBMIT_EXCEPTION, false);
    }
  }


  private List<Long> removeNullProductRow(PurchaseReturnDTO purchaseReturnDTO) {
    List<Long> productIdList = new ArrayList<Long>();
    if (purchaseReturnDTO.getItemDTOs() != null) {
      PurchaseReturnItemDTO[] purchaseReturnItemDTOs = purchaseReturnDTO.getItemDTOs();
      List<PurchaseReturnItemDTO> purchaseReturnItemLists = new ArrayList<PurchaseReturnItemDTO>();
      for (int i = 0; i < purchaseReturnItemDTOs.length; i++) {
        if (purchaseReturnItemDTOs[i].getProductId() != null && StringUtils.isNotBlank(purchaseReturnItemDTOs[i].getProductName())) {
          purchaseReturnItemLists.add(purchaseReturnItemDTOs[i]);
          productIdList.add(purchaseReturnItemDTOs[i].getProductId());
        }
      }
      if (CollectionUtils.isNotEmpty(purchaseReturnItemLists)) {
        purchaseReturnDTO.setItemDTOs(purchaseReturnItemLists.toArray(new PurchaseReturnItemDTO[purchaseReturnItemLists.size()]));
      }
    }
    return productIdList;
  }


  @RequestMapping(params = "method=saveOnlineReturnStorage")
  public String saveReturnStorage(ModelMap model, HttpServletRequest request, PurchaseReturnDTO purchaseReturnDTO) {
    RFITxnService rfiTxnService = ServiceManager.getService(RFITxnService.class);
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    ISupplierPayableService supplierPayableService = ServiceManager.getService(ISupplierPayableService.class);
    try {
      Long shopId = WebUtil.getShopId(request);
      Long userId = WebUtil.getUserId(request);
      Long shopVersionId = WebUtil.getShopVersionId(request);
      String username = WebUtil.getUserName(request);

//      if (shopId == null || userId==null) {
//        return "/txn/returnsStorage";
//      }
//      if(purchaseReturnDTO.getSupplierId()== null && purchaseReturnDTO.getSupplierShopId()!=null){
//        purchaseReturnDTO.setSupplierShopId(null);
//        request.setAttribute("errorMsg", "系统出错！");
//        return "/txn/returnsStorage";
//      }
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
      purchaseReturnDTO.setVestDate(System.currentTimeMillis());
      if (StringUtils.isBlank(purchaseReturnDTO.getReceiptNo())) {
        purchaseReturnDTO.setReceiptNo(txnService.getReceiptNo(shopId, OrderTypes.RETURN, null));
      }
      //todo 如果itemDTO 中的商品新增了单位，更新产品单位
      //更新单据中之前没有单位的产品数据
      txnService.updateProductUnit(purchaseReturnDTO);
      if(BcgogoShopLogicResourceUtils.isThroughSelectSupplier(shopVersionId)){
        ServiceManager.getService(IProductThroughService.class).generatePurchaseReturnOutStorageRelation(purchaseReturnDTO,purchaseReturnDTO.getSupplierId());
      }
      if(purchaseReturnDTO.getId()!=null ){
          ISaleReturnOrderService saleReturnOrderService = ServiceManager.getService(ISaleReturnOrderService.class);
          SalesReturnDTO salesReturnDTO = saleReturnOrderService.getSalesReturnDTOByPurchaseReturnOrderId(purchaseReturnDTO.getId());
          String key = "salesReturnOrder" + StringUtil.truncValue(salesReturnDTO.getId().toString())+"purchaseReturnOrder"+StringUtil.truncValue(salesReturnDTO.getPurchaseReturnOrderId().toString())+ StringUtil.truncValue(shopId.toString());
          try {
            if (!BcgogoConcurrentController.lock(ConcurrentScene.HANDLE_PAYABLE, key)) {
              request.setAttribute("errorMsg", "当前单据正在被操作，请稍候再试！");
              model.addAttribute("purchaseReturnId", purchaseReturnDTO.getId());
              return REDIRECT_MODIFY;
            }
            PurchaseReturnDTO purchaseReturnDTODb = rfiTxnService.getPurchaseReturnDTOById(shopId, purchaseReturnDTO.getId());
            if (OrderStatus.SELLER_PENDING.equals(purchaseReturnDTODb.getStatus())) {
              purchaseReturnDTO = rfiTxnService.updatePurchaseReturn(shopId,shopVersionId, purchaseReturnDTO);
            } else {
              LOG.debug("退货单状态[{}]不是SELLER_PENDING,不能进行修改操作", purchaseReturnDTO.getStatus());
              request.setAttribute("errorMsg", "当前单据状态是" + purchaseReturnDTO.getStatus().getName() + "不能修改单据！");
              model.addAttribute("purchaseReturnId", purchaseReturnDTO.getId());
              return REDIRECT_SHOW;
            }
          } finally {
            BcgogoConcurrentController.release(ConcurrentScene.HANDLE_PAYABLE, key);
          }
      }else{
        purchaseReturnDTO = rfiTxnService.saveOnlinePurchaseReturn(shopId,shopVersionId,purchaseReturnDTO);
        //生成推送消息
        ServiceManager.getService(IOrderPushMessageService.class)
            .createOrderPushMessageMessage(purchaseReturnDTO.getShopId(), purchaseReturnDTO.getSupplierShopId(), purchaseReturnDTO.getId(), PushMessageSourceType.SALE_RETURN_NEW);
      }
      //更新库存告警信息
      ServiceManager.getService(IInventoryService.class).updateMemocacheLimitByInventoryLimitDTO(shopId, purchaseReturnDTO.getInventoryLimitDTO());

//      if(purchaseReturnDTO.getSupplierShopId()==null){//普通退货单
//        //todo:供应商退款
//        purchaseReturnDTO.setCash(purchaseReturnDTO.getCash()==null?0d:purchaseReturnDTO.getCash());
//        purchaseReturnDTO.setDepositAmount(purchaseReturnDTO.getDepositAmount()==null?0d:purchaseReturnDTO.getDepositAmount());
//        purchaseReturnDTO.setStrikeAmount(purchaseReturnDTO.getStrikeAmount()==null?0d:purchaseReturnDTO.getStrikeAmount());
//        if("支票号".equals(purchaseReturnDTO.getBankCheckNo())) {
//          purchaseReturnDTO.setBankCheckNo("");
//        }
//        supplierPayableService.returnPayable(purchaseReturnDTO);
//        model.addAttribute("purchaseReturnDTO", purchaseReturnDTO);
//        this.setSupplierDropDownInfo(model, request);
//
//      }else{
//        LOG.debug("批发商退货,不做结算！");
//      }

      ServiceManager.getService(IProductHistoryService.class).saveProductHistoryForOrder(shopId,purchaseReturnDTO);

      //线程做orderindex
      BcgogoEventPublisher bcgogoEventPublisher = new BcgogoEventPublisher();
      PurchaseReturnSavedEvent purchaseReturnSavedEvent = new PurchaseReturnSavedEvent(purchaseReturnDTO);
      bcgogoEventPublisher.publisherPurchaseReturnSaved(purchaseReturnSavedEvent);
      request.setAttribute("UNIT_TEST", purchaseReturnSavedEvent); //单元测试
      model.addAttribute("purchaseReturnId", purchaseReturnDTO.getId());
    } catch (Exception e) {
      LOG.debug("/onlineReturn.do");
      LOG.debug("method=saveOnlineReturnStorage");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.debug(e.getMessage(), e);
      request.setAttribute("errorMsg", "系统出错！");
      return "/autoaccessoryonline/onlinePurchaseReturnSelectList";
    }
    if(purchaseReturnDTO.getSupplierShopId()!=null && OrderStatus.SELLER_PENDING.equals(purchaseReturnDTO.getStatus())){
      return REDIRECT_MODIFY;
    }else{
      return REDIRECT_SHOW+"&print="+purchaseReturnDTO.getPrint();
    }
  }

  /**
   * 生成退货单
   *
   * @param model
   * @param request
   * @return
   */
  @RequestMapping(params = "method=modifyReturnStorage")
  public String modifyReturnStorage(ModelMap model, HttpServletRequest request,Long purchaseReturnId) {
    Long shopId = null;
    RFITxnService rfiTxnService = ServiceManager.getService(RFITxnService.class);
    try {
      shopId = WebUtil.getShopId(request);
      PurchaseReturnDTO purchaseReturnDTO = rfiTxnService.getPurchaseReturnDTOById(Long.valueOf(purchaseReturnId));
      if(!OrderStatus.SELLER_PENDING.equals(purchaseReturnDTO.getStatus())){
        return REDIRECT_SHOW + "&purchaseReturnId=" + purchaseReturnDTO.getId();
      }
      purchaseReturnDTO.setEditor(WebUtil.getUserName(request));
      purchaseReturnDTO.setEditorId(WebUtil.getUserId(request));
      purchaseReturnDTO.setShopId(shopId);
      if (purchaseReturnDTO != null) {
        //设置供应商信息
        IUserService userService = ServiceManager.getService(IUserService.class);
        List<SupplierDTO> supplierDTOList = userService.getSupplierById(shopId, purchaseReturnDTO.getSupplierId());
        if (CollectionUtils.isNotEmpty(supplierDTOList)) {
          purchaseReturnDTO.setSupplierDTO(supplierDTOList.get(0));
        }
        // 填充退货明细中的具体信息
        getPurchaseReturnService().fillPurchaseReturnItemDTOsDetailInfo(purchaseReturnDTO);
      }
      if(BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(WebUtil.getShopVersionId(request))){
        IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
        List<StoreHouseDTO> storeHouseDTOList = storeHouseService.getAllStoreHousesByShopId(shopId);
        model.addAttribute("storeHouseDTOList", storeHouseDTOList);//select 选项
        InventoryService inventoryService = ServiceManager.getService(InventoryService.class);
        //更新库存 根据仓库
        inventoryService.updateItemDTOInventoryAmountByStorehouse(shopId,purchaseReturnDTO.getStorehouseId(), purchaseReturnDTO);
      }

      model.addAttribute("purchaseReturnDTO", purchaseReturnDTO);
      SupplierDTO supplierDTO = ServiceManager.getService(IUserService.class).getSupplierById(purchaseReturnDTO.getSupplierId());
      model.addAttribute("supplierDTO", supplierDTO);
      ShopDTO supplierShopDTO = ServiceManager.getService(IConfigService.class).getShopById(supplierDTO.getSupplierShopId());
      model.addAttribute("supplierShop", supplierShopDTO);
//      this.setSupplierDropDownInfo(model, request);
    } catch (Exception e) {
      LOG.error("onlineReturn.do?method=modifyReturnStorage error,shopId :{}", shopId);
      LOG.error(e.getMessage(), e);
    }
    return "/autoaccessoryonline/onlineReturnEdit";
  }


}
