package com.bcgogo.txn;

import com.bcgogo.common.Pager;
import com.bcgogo.common.Result;
import com.bcgogo.common.StringUtil;
import com.bcgogo.common.WebUtil;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.enums.DeletedType;
import com.bcgogo.enums.DraftOrderStatus;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.exception.PageException;
import com.bcgogo.product.dto.KindDTO;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.product.dto.ProductLocalInfoDTO;
import com.bcgogo.product.service.IProductService;
import com.bcgogo.search.dto.DraftOrderSearchDTO;
import com.bcgogo.search.dto.InventorySearchIndexDTO;
import com.bcgogo.search.service.ISearchService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.*;
import com.bcgogo.txn.model.Category;
import com.bcgogo.txn.model.SupplierInventory;
import com.bcgogo.txn.service.*;
import com.bcgogo.txn.service.productThrough.IProductThroughService;
import com.bcgogo.user.dto.CustomerDTO;
import com.bcgogo.user.dto.CustomerRecordDTO;
import com.bcgogo.user.dto.CustomerVehicleDTO;
import com.bcgogo.user.dto.VehicleDTO;
import com.bcgogo.user.model.CustomerRecord;
import com.bcgogo.user.service.ICustomerService;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.user.service.utils.BcgogoShopLogicResourceUtils;
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
 * User: ndong
 * Date: 12-9-8
 * Time: 上午3:53
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/draft.do")
public class DraftOrderController {
  private static final Logger LOG = LoggerFactory.getLogger(DraftOrderController.class);
  private static final String PAGE_GOODSBUY = "/txn/goodsBuy";
  private static final String PAGE_GOODSSTORAGE = "/txn/goodsStorage";
  private static final String PAGE_GOODSSALE = "/txn/goodsSale";
  private static final String PAGE_SALES_RETURN = "/txn/salesReturn";
  private static final String PAGE_RETURNSTORAGE = "/txn/returnsStorage";
  private static final String PAGE_REPAIR_BY_ID = "redirect:txn.do?method=getRepairOrder&menu-uid=VEHICLE_CONSTRUCTION_REPAIR";
  private static final String PAGE_REPAIR_BY_VEHICLE = "redirect:txn.do?method=getRepairOrderByVehicleNumber";
  private static final String PAGE_REPAIR = "redirect:txn.do?method=getRepairOrderByDraftOrder";
  private static final String PAGE_DRAFTORDER_BOX = "/remind/draftOrderBox";
  private static final int STARTPAGE_NO = 1;
  private static final String MODEL_CATEGORY_LIST = "categoryList";
  private static final String MODEL_SETTLEMENTTYPE_LIST = "settlementTypeList";
  private static final String MODEL_INVOICECATEGORY_LIST = "invoiceCategoryList";
  @Autowired
  public IDraftOrderService draftOrderService;
  @Autowired
  public ISaleReturnOrderService saleReturnOrderService;

  /**
   * 根据draftOrderSearchDTO对应条件查询所有对应的单据草稿
   *
   * @param request
   * @param draftOrderSearchDTO
   * @return
   * @throws PageException
   */
  @RequestMapping(params = "method=getDraftOrders")
  @ResponseBody
  public Object getDraftOrders(HttpServletRequest request, DraftOrderSearchDTO draftOrderSearchDTO){
    try {
      if (draftOrderSearchDTO == null || StringUtil.isEmpty(draftOrderSearchDTO.getStartPageNo()) || NumberUtil.intValue(draftOrderSearchDTO.getStartPageNo()) < 0) {
        return null;
      }
      draftOrderSearchDTO.convertOrderType(draftOrderSearchDTO.getOrderTypes());
      draftOrderSearchDTO.setShopId(WebUtil.getShopId(request));
      draftOrderSearchDTO.setUserId(WebUtil.getUserId(request));
      Pager pager = new Pager(draftOrderService.countDraftOrders(draftOrderSearchDTO), NumberUtil.intValue(String.valueOf(draftOrderSearchDTO.getStartPageNo()), 1));
      List<Object> result = new ArrayList<Object>();
      Map<String, Object> data = new HashMap<String, Object>();
      draftOrderSearchDTO.setPager(pager);
      data.put("draftOrderData", draftOrderService.getDraftOrders(draftOrderSearchDTO));
      data.put("countOrderTypeList", draftOrderService.countDraftOrderByOrderType(draftOrderSearchDTO));
      result.add(data);
      result.add(pager);
      return result;
    } catch (Exception e) {
      LOG.error("/draft.do?method=getDraftOrders,shopId={},userId ={}", request.getSession().getAttribute("shopId"), request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
      return null;
    }

  }

  /**
   * 转到单据草稿箱页面
   *
   * @param request
   * @return
   */
  @RequestMapping(params = "method=toDraftOrderBox")
  public String toDraftOrderBox(ModelMap modelMap, HttpServletRequest request) throws Exception {
    if (WebUtil.getShopId(request) == null) {
      return "/login";
    }
    DraftOrderSearchDTO draftOrderSearchDTO = new DraftOrderSearchDTO();
    String endTimeStr = DateUtil.convertDateLongToString(System.currentTimeMillis(), DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN);
    draftOrderSearchDTO.setEndTime(endTimeStr);
    modelMap.addAttribute("draftOrderSearchDTO", draftOrderSearchDTO);
    return PAGE_DRAFTORDER_BOX;
  }

  /**
   * 保存采购单草稿
   *
   * @param request
   * @param purchaseOrderDTO
   */
  @RequestMapping(params = "method=savePurchaseOrderDraft")
  @ResponseBody
  public DraftOrderDTO savePurchaseOrderDraft(HttpServletRequest request, PurchaseOrderDTO purchaseOrderDTO) {
    DraftOrderDTO draftOrderDTO = new DraftOrderDTO();
    IProductService productService =ServiceManager.getService(IProductService.class);
    ISearchService searchService = ServiceManager.getService(ISearchService.class);
    try {
      draftOrderDTO.setShopId(WebUtil.getShopId(request));
      draftOrderDTO.setUserId(WebUtil.getUserId(request));
      draftOrderDTO.fromPurchaseOrderDTO(purchaseOrderDTO);
      if(null != draftOrderDTO && !ArrayUtils.isEmpty(draftOrderDTO.getItemDTOs())) {
        for (DraftOrderItemDTO draftOrderItemDTO : draftOrderDTO.getItemDTOs()) {
          if (null == draftOrderItemDTO) {
            continue;
          }

          if (null == draftOrderItemDTO || StringUtils.isBlank(draftOrderItemDTO.getProductName())) {
            continue;
          }

          Long kindId = productService.getProductKindId(draftOrderDTO.getShopId(), draftOrderItemDTO.getProductKind());

          draftOrderItemDTO.setProductKindId(kindId);

          if (null == draftOrderItemDTO.getProductLocalInfoId()) {
            continue;
          } else {
            //再将商品分类更新到Inventory_Search_Index表
            InventorySearchIndexDTO isiDTO = searchService.getInventorySearchIndexById(draftOrderDTO.getShopId(), draftOrderItemDTO.getProductId());
            //最后将商品分类更新到Product表
            ProductDTO productDTO = productService.getProductById(isiDTO.getParentProductId()).toDTO();
            productDTO.setKindId(kindId);
            productService.updateProduct(draftOrderDTO.getShopId(), productDTO);
            if (isiDTO != null) {
              isiDTO.setKindName(draftOrderItemDTO.getProductKind());
              searchService.updateInventorySearchIndex(isiDTO);
            }
          }
        }
      }
      if(draftOrderDTO.getReceiptNo() == null || "".equals(draftOrderDTO.getReceiptNo())) {
        ITxnService txnService = ServiceManager.getService(ITxnService.class);
        draftOrderDTO.setReceiptNo(txnService.getReceiptNo(WebUtil.getShopId(request), OrderTypes.PURCHASE, null));
      }
      return draftOrderService.saveOrUpdateDraftOrder(draftOrderDTO);
    } catch (Exception e) {
      LOG.error("/draft.do?method=savePurchaseOrderDraft,shopId={},userId ={}", request.getSession().getAttribute("shopId"), request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
      return null;
    }
  }

  /**
   * 保存施工单草稿
   *
   * @param request
   * @param repairOrderDTO
   */
  @RequestMapping(params = "method=saveRepairOrderDraft")
  @ResponseBody
  public Object saveRepairOrderDraft(HttpServletRequest request, RepairOrderDTO repairOrderDTO){
    RFITxnService rfiTxnService = ServiceManager.getService(RFITxnService.class);
    IProductService productService =ServiceManager.getService(IProductService.class);
    IRepairService repairService =ServiceManager.getService(IRepairService.class);
    DraftOrderDTO draftOrderDTO=null;
    Map<String, Object> returnMap = new HashMap<String, Object>();
    try {
      Long shopId = WebUtil.getShopId(request);
      Long shopVersionId = WebUtil.getShopVersionId(request);
      repairOrderDTO.setShopId(shopId);
      Result result = repairService.validateRepairOrderOnSaveDraft(repairOrderDTO);
      if(result!=null && !result.isSuccess()){
        returnMap.put("result",result);
        return returnMap;
      }
      if (StringUtils.isNotBlank(repairOrderDTO.getDraftOrderIdStr())) {
        draftOrderDTO = draftOrderService.getOrderByDraftOrderId(shopId,shopVersionId, Long.valueOf(repairOrderDTO.getDraftOrderIdStr()));
      }
      if (repairOrderDTO == null && StringUtils.isNotBlank(repairOrderDTO.getVechicle())) {
        draftOrderDTO = draftOrderService.getDraftOrderByVechicle(shopId,shopVersionId,repairOrderDTO.getVechicle());
      }
      if (draftOrderDTO == null) draftOrderDTO = new DraftOrderDTO();

      draftOrderDTO.setShopId(shopId);
      draftOrderDTO.setUserId(WebUtil.getUserId(request));
      if (StringUtils.isNotBlank(repairOrderDTO.getStartDateStr())) {
        repairOrderDTO.setStartDate(DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN, repairOrderDTO.getStartDateStr()));       //进厂时间
      }
      if (StringUtils.isNotBlank(repairOrderDTO.getEndDateStr())) {
        repairOrderDTO.setEndDate(DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN, repairOrderDTO.getEndDateStr()));       //预计出厂时间
      }

      if(null != repairOrderDTO && !ArrayUtils.isEmpty(repairOrderDTO.getItemDTOs()))
      {
        for(RepairOrderItemDTO repairOrderItemDTO : repairOrderDTO.getItemDTOs())
        {
          if(StringUtils.isNotBlank(repairOrderItemDTO.getProductName()) && StringUtils.isNotBlank(repairOrderItemDTO.getBusinessCategoryName()))
          {
            repairOrderItemDTO.setBusinessCategoryName(repairOrderItemDTO.getBusinessCategoryName().trim());
            repairOrderItemDTO.setBusinessCategoryId(rfiTxnService.saveCategory(shopId,repairOrderItemDTO.getBusinessCategoryName()).getId());
          }
          if(null == repairOrderItemDTO.getProductId())
          {
            continue;
          }

          ProductLocalInfoDTO productLocalInfoDTO = productService.getProductLocalInfoById(repairOrderItemDTO.getProductId(),shopId);

          if(null == productLocalInfoDTO)
          {
            continue;
          }

          productLocalInfoDTO.setBusinessCategoryId(repairOrderItemDTO.getBusinessCategoryId());

          productService.updateProductLocalInfo(productLocalInfoDTO);

        }
      }

      if(null != repairOrderDTO && !ArrayUtils.isEmpty(repairOrderDTO.getServiceDTOs()))
      {
        for(RepairOrderServiceDTO repairOrderServiceDTO : repairOrderDTO.getServiceDTOs())
        {
          if(StringUtils.isNotBlank(repairOrderServiceDTO.getService()) && StringUtils.isNotBlank(repairOrderServiceDTO.getBusinessCategoryName()))
          {
            repairOrderServiceDTO.setBusinessCategoryName(repairOrderServiceDTO.getBusinessCategoryName().trim());
            repairOrderServiceDTO.setBusinessCategoryId(rfiTxnService.saveCategory(shopId,repairOrderServiceDTO.getBusinessCategoryName()).getId());
          }
          if(null == repairOrderServiceDTO.getServiceId())
          {
            continue;
          }

          rfiTxnService.saveOrUpdateCategoryItemRelation(shopId,repairOrderServiceDTO.getBusinessCategoryId(),repairOrderServiceDTO.getServiceId());
        }
      }
      draftOrderDTO.fromRepairOrderDTO(repairOrderDTO);
      if(draftOrderDTO.getReceiptNo() == null || "".equals(draftOrderDTO.getReceiptNo())) {
        ITxnService txnService = ServiceManager.getService(ITxnService.class);
        draftOrderDTO.setReceiptNo(txnService.getReceiptNo(WebUtil.getShopId(request), OrderTypes.REPAIR, null));
      }
      draftOrderDTO= draftOrderService.saveOrUpdateDraftOrder(draftOrderDTO);

      // 施工单模板使用计次
      Long repairOrderTemplateId = repairOrderDTO.getRepairOrderTemplateId();
      if (repairOrderTemplateId != null) {
        IRepairOrderTemplateService repairOrderTemplateService = ServiceManager.getService(RepairOrderTemplateService.class);
        repairOrderTemplateService.updateRepairOrderTemplateUsageCounter(repairOrderTemplateId);
      }
      //修改保险理赔
      ServiceManager.getService(IInsuranceService.class).RFupdateInsuranceOrderById(null, draftOrderDTO.getId(), repairOrderDTO.getInsuranceOrderId(),draftOrderDTO.getReceiptNo());

     //修改预约单信息
      ServiceManager.getService(IAppointOrderService.class).handelAppointOrderAfterSaveRepairDraft(draftOrderDTO);
      returnMap.put("id", draftOrderDTO.getId().toString());
      returnMap.put("receiptNo",draftOrderDTO.getReceiptNo());
      return returnMap;
    } catch (Exception e) {
      LOG.error("/draft.do?method=saveRepairOrderDraft,shopId={},userId ={}", request.getSession().getAttribute("shopId"), request.getSession().getAttribute("userId"));
      LOG.error("draftOrderDTO ={}", draftOrderDTO);
      LOG.error(e.getMessage(), e);
      return returnMap;
    }
  }

  /**
   * 保存入库单草稿
   *
   * @param request
   * @param purchaseInventoryDTO
   */
  @RequestMapping(params = "method=savePurchaseInventoryDraft")
  @ResponseBody
  public DraftOrderDTO savePurchaseInventoryDraft(HttpServletRequest request, PurchaseInventoryDTO purchaseInventoryDTO) {
    DraftOrderDTO draftOrderDTO = new DraftOrderDTO();
    IProductService productService = ServiceManager.getService(IProductService.class);
    ISearchService searchService = ServiceManager.getService(ISearchService.class);
    try {
      draftOrderDTO.setShopId(WebUtil.getShopId(request));
      draftOrderDTO.setUserId(WebUtil.getUserId(request));
      draftOrderDTO.fromPurchaseInventoryDTO(purchaseInventoryDTO);
      if(null != draftOrderDTO && !ArrayUtils.isEmpty(draftOrderDTO.getItemDTOs())) {
        for (DraftOrderItemDTO draftOrderItemDTO : draftOrderDTO.getItemDTOs()) {
          if (null == draftOrderItemDTO) {
            continue;
          }

          if (null == draftOrderItemDTO || StringUtils.isBlank(draftOrderItemDTO.getProductName())) {
            continue;
          }

          Long kindId = productService.getProductKindId(draftOrderDTO.getShopId(), draftOrderItemDTO.getProductKind());

          draftOrderItemDTO.setProductKindId(kindId);

          if (null == draftOrderItemDTO.getProductLocalInfoId()) {
            continue;
          } else {
            //再将商品分类更新到Inventory_Search_Index表
            InventorySearchIndexDTO isiDTO = searchService.getInventorySearchIndexById(draftOrderDTO.getShopId(), draftOrderItemDTO.getProductLocalInfoId());
            //最后将商品分类更新到Product表
            ProductDTO productDTO = productService.getProductById(isiDTO.getParentProductId()).toDTO();
            productDTO.setKindId(draftOrderItemDTO.getProductKindId());
            productService.updateProduct(draftOrderDTO.getShopId(), productDTO);
            if (isiDTO != null) {
              isiDTO.setKindName(draftOrderItemDTO.getProductKind());
              searchService.updateInventorySearchIndex(isiDTO);
            }
          }
        }
      }
      if(draftOrderDTO.getReceiptNo() == null || "".equals(draftOrderDTO.getReceiptNo())) {
        ITxnService txnService = ServiceManager.getService(ITxnService.class);
        draftOrderDTO.setReceiptNo(txnService.getReceiptNo(WebUtil.getShopId(request), OrderTypes.INVENTORY, null));
      }
      return draftOrderService.saveOrUpdateDraftOrder(draftOrderDTO);
    } catch (Exception e) {
      LOG.error("/draft.do?method=savePurchaseInventoryDraft,shopId={},userId ={}", request.getSession().getAttribute("shopId"), request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
      return null;
    }
  }

  /**
   * 保存销售单草稿
   *
   * @param request
   * @param salesOrderDTO
   */
  @RequestMapping(params = "method=saveSalesOrderDraft")
  @ResponseBody
  public DraftOrderDTO saveSalesOrderDraft(HttpServletRequest request,SalesOrderDTO salesOrderDTO){
    IProductService productService = ServiceManager.getService(IProductService.class);
    RFITxnService rfiTxnService = ServiceManager.getService(RFITxnService.class);
    DraftOrderDTO draftOrderDTO=null;
    try {
      draftOrderDTO = new DraftOrderDTO();
      draftOrderDTO.setShopId(WebUtil.getShopId(request));
      draftOrderDTO.setUserId(WebUtil.getUserId(request));

      if(null != salesOrderDTO && !ArrayUtils.isEmpty(salesOrderDTO.getItemDTOs())){
        for(SalesOrderItemDTO salesOrderItemDTO : salesOrderDTO.getItemDTOs()){
          if(StringUtils.isNotBlank(salesOrderItemDTO.getProductName()) && StringUtils.isNotBlank(salesOrderItemDTO.getBusinessCategoryName())) {
            salesOrderItemDTO.setBusinessCategoryName(salesOrderItemDTO.getBusinessCategoryName().trim());
            salesOrderItemDTO.setBusinessCategoryId(rfiTxnService.saveCategory(WebUtil.getShopId(request),salesOrderItemDTO.getBusinessCategoryName()).getId());
          }
          if(null == salesOrderItemDTO.getProductId()){
            continue;
          }
          ProductLocalInfoDTO productLocalInfoDTO = productService.getProductLocalInfoById(salesOrderItemDTO.getProductId(),WebUtil.getShopId(request));
          if(null == productLocalInfoDTO) {
            continue;
          }
          productLocalInfoDTO.setBusinessCategoryId(salesOrderItemDTO.getBusinessCategoryId());
          productService.updateProductLocalInfo(productLocalInfoDTO);
        }
      }
      draftOrderDTO.fromSalesOrderDTO(salesOrderDTO);
      if(StringUtil.isEmpty(draftOrderDTO.getReceiptNo())) {
        ITxnService txnService = ServiceManager.getService(ITxnService.class);
        draftOrderDTO.setReceiptNo(txnService.getReceiptNo(WebUtil.getShopId(request), OrderTypes.SALE, null));
      }
      draftOrderDTO =  draftOrderService.saveOrUpdateDraftOrder(draftOrderDTO);
      return draftOrderDTO;
    } catch (Exception e) {
      LOG.error("/draft.do?method=saveSalesOrderDraft,shopId={},userId ={}", request.getSession().getAttribute("shopId"), request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
      return null;
    }
  }

  /**
   * 保存销售单草稿
   *
   * @param request
   * @param salesReturnDTO
   */
  @RequestMapping(params = "method=saveSalesReturnDraft")
  @ResponseBody
  public DraftOrderDTO saveSalesReturnDraft(HttpServletRequest request,SalesReturnDTO salesReturnDTO){
    DraftOrderDTO draftOrderDTO = new DraftOrderDTO();
    try{
      draftOrderDTO.setShopId(WebUtil.getShopId(request));
      draftOrderDTO.setUserId(WebUtil.getUserId(request));
      draftOrderDTO.fromSalesReturnDTO(salesReturnDTO);
      if(draftOrderDTO.getReceiptNo() == null || "".equals(draftOrderDTO.getReceiptNo())) {
        ITxnService txnService = ServiceManager.getService(ITxnService.class);
        draftOrderDTO.setReceiptNo(txnService.getReceiptNo(WebUtil.getShopId(request), OrderTypes.SALE_RETURN, null));
      }
      return draftOrderService.saveOrUpdateDraftOrder(draftOrderDTO);
    } catch (Exception e) {
      LOG.error("/draft.do?method=saveSalesReturnDraft,shopId={},userId ={}", request.getSession().getAttribute("shopId"), request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
      return null;
    }
  }

  /**
   * 保存退货单草稿
   *
   * @param request
   * @param purchaseReturnDTO
   */
  @RequestMapping(params = "method=saveReturnStorageOrderDraft")
  @ResponseBody
  public DraftOrderDTO saveReturnStorageOrderDraft(HttpServletRequest request, PurchaseReturnDTO purchaseReturnDTO) {
    DraftOrderDTO draftOrderDTO = new DraftOrderDTO();
    try {
      draftOrderDTO.setShopId(WebUtil.getShopId(request));
      draftOrderDTO.setUserId(WebUtil.getUserId(request));
      draftOrderDTO.fromPurchaseReturnDTO(purchaseReturnDTO);
      if(draftOrderDTO.getReceiptNo() == null || "".equals(draftOrderDTO.getReceiptNo())) {
        ITxnService txnService = ServiceManager.getService(ITxnService.class);
        draftOrderDTO.setReceiptNo(txnService.getReceiptNo(WebUtil.getShopId(request), OrderTypes.RETURN, null));
      }
      return draftOrderService.saveOrUpdateDraftOrder(draftOrderDTO);
    } catch (Exception e) {
      LOG.error("/draft.do?method=saveReturnStorageOrderDraft,shopId={},userId ={}", request.getSession().getAttribute("shopId"), request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
      return null;
    }
  }

  /**
   * 删除单据草稿，即设置draftOrder的stutus为DRAFT_REPEAL
   *
   * @param request
   * @param draftOrderSearchDTO
   * @return
   * @throws PageException
   */
  @RequestMapping(params = "method=deleteDraftOrder")
  @ResponseBody
  public Object deleteDraftOrder(HttpServletRequest request, DraftOrderSearchDTO draftOrderSearchDTO) throws PageException {
    try {
      if (StringUtil.isEmpty(draftOrderSearchDTO.getDraftOrderIdStr()) || WebUtil.getShopId(request) == null) {
        return null;
      }
      draftOrderService.deleteDraftOrder(WebUtil.getShopId(request), NumberUtil.longValue(draftOrderSearchDTO.getDraftOrderIdStr()));
      return getDraftOrders(request, draftOrderSearchDTO);
    } catch (Exception e) {
      LOG.error("/draft.do?method=deleteDraftOrder,shopId={},userId ={}", request.getSession().getAttribute("shopId"), request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
      return null;
    }
  }

  /**
   * 修改草稿，跳转到对应单据页面
   *
   * @param modelMap
   * @param request
   * @param draftOrderIdStr
   * @return
   */
  @RequestMapping(params = "method=getOrderByDraftOrderId")
  public String getOrderByDraftOrderId(ModelMap modelMap, HttpServletRequest request, String draftOrderIdStr) {
    if (StringUtil.isEmpty(draftOrderIdStr) && !NumberUtil.isNumber(draftOrderIdStr)) {
      return null;
    }
    try {
      Long shopId = WebUtil.getShopId(request);
      Long shopVersionId = WebUtil.getShopVersionId(request);
      DraftOrderDTO draftOrderDTO = draftOrderService.getOrderByDraftOrderId(shopId,shopVersionId, NumberUtil.longValue(draftOrderIdStr));
      if (draftOrderDTO == null) {
        LOG.error("the draftOrderId is a dity data! and draftOrderId = {}", draftOrderIdStr);
        return null;
      }
      request.setAttribute("wholesalerVersion", ConfigUtils.isWholesalerVersion(WebUtil.getShopVersionId(request)));
      return redirect(modelMap, request, draftOrderDTO);
    } catch (Exception e) {
      LOG.error("draftOrderId{}", draftOrderIdStr);
      LOG.error(e.getMessage(), e);
      return null;
    }
  }

  /**
   * 根据单据orderTypeEnum转向指定页面
   *
   * @param modelMap
   * @param draftOrderDTO
   * @return
   */
  private String redirect(ModelMap modelMap, HttpServletRequest request, DraftOrderDTO draftOrderDTO) throws Exception {
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    RFITxnService rfiTxnService = ServiceManager.getService(RFITxnService.class);
    InventoryService inventoryService = ServiceManager.getService(InventoryService.class);
    IProductThroughService productThroughService=ServiceManager.getService(IProductThroughService.class);
    Long shopId = WebUtil.getShopId(request);
    Long shopVersionId = WebUtil.getShopVersionId(request);
    if(BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(shopVersionId)){
      IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
      List<StoreHouseDTO> storeHouseDTOList = storeHouseService.getAllStoreHousesByShopId(shopId);
      modelMap.addAttribute("storeHouseDTOList", storeHouseDTOList);//select 选项
      if(draftOrderDTO.getStorehouseId()!=null){
        StoreHouseDTO storeHouseDTO = storeHouseService.getStoreHouseDTOById(shopId,draftOrderDTO.getStorehouseId());
        if(DeletedType.TRUE.equals(storeHouseDTO.getDeleted())){
          draftOrderDTO.setStorehouseId(null);
          draftOrderDTO.setStorehouseName(null);
        }
      }
      if(CollectionUtils.isNotEmpty(storeHouseDTOList) && draftOrderDTO.getStorehouseId()==null){
        if(storeHouseDTOList.size()==1){
          draftOrderDTO.setStorehouseId(storeHouseDTOList.get(0).getId());
        }
      }
      //更新库存 根据仓库
      inventoryService.updateDraftItemDTOInventoryAmountByStorehouse(shopId,draftOrderDTO.getStorehouseId(), draftOrderDTO);
    }
    if(BcgogoShopLogicResourceUtils.isThroughSelectSupplier(shopVersionId)){
      SupplierInventoryDTO condition=null;
      for(DraftOrderItemDTO itemDTO:draftOrderDTO.getItemDTOs()){
        Map<String, String> inventoryMap=JsonUtil.jsonToStringMap(itemDTO.getUseAmountJson());
        if(inventoryMap==null||inventoryMap.keySet().isEmpty()){
          continue;
        }
        condition=new SupplierInventoryDTO();
        condition.setShopId(shopId);
        condition.setStorehouseId(draftOrderDTO.getStorehouseId());
        condition.setProductIds(new Long[]{itemDTO.getProductLocalInfoId()});
        List<SupplierInventoryDTO> supplierInventoryDTOList=new ArrayList<SupplierInventoryDTO>();
        if(OrderUtil.inStorageOrders.contains(draftOrderDTO.getOrderTypeEnum())){
          supplierInventoryDTOList=productThroughService.getSupplierInventoryDTOsWithOtherStorehouse(condition.getShopId(), condition.getProductIds()[0], condition.getStorehouseId());
        }else{
          List<SupplierInventory> supplierInventoryList = productThroughService.getSupplierInventory(condition);
          if (CollectionUtil.isNotEmpty(supplierInventoryList)) {
            for (SupplierInventory supplierInventory : supplierInventoryList) {
              supplierInventoryDTOList.add(supplierInventory.toDTO());
            }
          }
        }
        Long csId=draftOrderDTO.getCustomerOrSupplierId();
        List<OutStorageRelationDTO> outStorageRelationDTOs=new ArrayList<OutStorageRelationDTO>();
        if(CollectionUtil.isNotEmpty(supplierInventoryDTOList)){
          for(SupplierInventoryDTO inventoryDTO:supplierInventoryDTOList){
            if(OrderTypes.RETURN.equals(draftOrderDTO.getOrderTypeEnum())&&csId!=null&&!csId.equals(inventoryDTO.getSupplierId())){   //入库退货要过滤掉其他供应商
              continue;
            }
            OutStorageRelationDTO relationDTO=new OutStorageRelationDTO();
            relationDTO.setRelatedSupplierId(inventoryDTO.getSupplierId());
            relationDTO.setRelatedSupplierName(inventoryDTO.getSupplierName());
            relationDTO.setRelatedSupplierInventory(inventoryDTO.getRemainAmount());
            relationDTO.setSupplierType(inventoryDTO.getSupplierType());
            String useAmount=inventoryMap.get(ObjectUtil.generateKey(inventoryDTO.getSupplierId(),inventoryDTO.getSupplierType()));
            if(com.bcgogo.utils.StringUtil.isNotEmpty(useAmount)&&Double.valueOf(useAmount)>0){
              relationDTO.setUseRelatedAmount(Double.valueOf(useAmount));
            }
            outStorageRelationDTOs.add(relationDTO);
          }
          itemDTO.setOutStorageRelationDTOs(outStorageRelationDTOs.toArray(new OutStorageRelationDTO[outStorageRelationDTOs.size()]));
        }
      }
    }
    IProductService productService = ServiceManager.getService(IProductService.class);
    if (draftOrderDTO.getOrderTypeEnum().equals(OrderTypes.PURCHASE)) {
      if(draftOrderDTO.getReceiptNo() == null || "".equals(draftOrderDTO.getReceiptNo())) {
        draftOrderDTO.setReceiptNo(txnService.getReceiptNo(shopId, OrderTypes.PURCHASE, null));
        draftOrderService.saveOrUpdateDraftOrder(draftOrderDTO);
      }
      PurchaseOrderDTO purchaseOrderDTO = draftOrderDTO.toPurchaseOrderDTO();
      if(!"newPage".equals(request.getParameter("flag")) && !"true".equals(request.getParameter("newOpen")) && !"true".equals(request.getParameter("newSettlePage"))) {
        purchaseOrderDTO.setReceiptNo(request.getParameter("receiptNo"));
      }
      if (purchaseOrderDTO!=null && !ArrayUtils.isEmpty(purchaseOrderDTO.getItemDTOs())) {
        for (PurchaseOrderItemDTO purchaseOrderItemDTO : purchaseOrderDTO.getItemDTOs()) {
          if (null != purchaseOrderItemDTO && null != purchaseOrderItemDTO.getProductId()) {
            ProductDTO productDTO = productService.getProductByProductLocalInfoId(purchaseOrderItemDTO.getProductId(), shopId);
            String kindName = null;
            if (null != productDTO && null != productDTO.getKindId()) {
              KindDTO kindDTO = productService.getEnabledKindDTOById(shopId, productDTO.getKindId());
              if (null != kindDTO) {
                kindName = kindDTO.getName();
              }
            }
            purchaseOrderItemDTO.setProductKind(kindName);
          } else if (StringUtils.isNotBlank(purchaseOrderItemDTO.getProductName())) {
            if (null == purchaseOrderItemDTO.getProductKindId()) {
              purchaseOrderItemDTO.setProductKind(null);
              continue;
            }
            String kindName = null;
            KindDTO kindDTO = productService.getEnabledKindDTOById(shopId, purchaseOrderItemDTO.getProductKindId());
            if (null != kindDTO) {
              kindName = kindDTO.getName();
            }
            purchaseOrderItemDTO.setProductKind(kindName);
          }
        }
      }
      purchaseOrderDTO.setBillProducer((String)request.getSession().getAttribute("userName"));
      purchaseOrderDTO.setBillProducerId((Long)request.getSession().getAttribute("userId"));
      setSupplierDropDownInfo(modelMap,request);
      modelMap.addAttribute("purchaseOrderDTO", purchaseOrderDTO);
      return PAGE_GOODSBUY;
    } else if (draftOrderDTO.getOrderTypeEnum().equals(OrderTypes.INVENTORY)) {
      if(draftOrderDTO.getReceiptNo() == null || "".equals(draftOrderDTO.getReceiptNo())) {
        draftOrderDTO.setReceiptNo(txnService.getReceiptNo(shopId, OrderTypes.INVENTORY, null));
        draftOrderService.saveOrUpdateDraftOrder(draftOrderDTO);
      }
      PurchaseInventoryDTO purchaseInventoryDTO = draftOrderDTO.toPurchaseInventoryDTO();
      if(!"newPage".equals(request.getParameter("flag")) && !"true".equals(request.getParameter("newOpen")) && !"true".equals(request.getParameter("newSettlePage"))) {
        purchaseInventoryDTO.setReceiptNo(request.getParameter("receiptNo"));
      }
      if (null != purchaseInventoryDTO && !ArrayUtils.isEmpty(purchaseInventoryDTO.getItemDTOs())) {
        for (PurchaseInventoryItemDTO purchaseInventoryItemDTO : purchaseInventoryDTO.getItemDTOs()) {

          if (null != purchaseInventoryItemDTO && null != purchaseInventoryItemDTO.getProductId()) {
            ProductDTO productDTO = productService.getProductByProductLocalInfoId(purchaseInventoryItemDTO.getProductId(), shopId);

            String kindName = null;

            if (null != productDTO && null != productDTO.getKindId()) {
              KindDTO kindDTO = productService.getEnabledKindDTOById(shopId, productDTO.getKindId());
              if (null != kindDTO) {
                kindName = kindDTO.getName();
              }
            }
            purchaseInventoryItemDTO.setProductKind(kindName);
          } else if (StringUtils.isNotBlank(purchaseInventoryItemDTO.getProductName())) {
            if (null == purchaseInventoryItemDTO.getProductKindId()) {
              purchaseInventoryItemDTO.setProductKind(null);
              continue;
            }
            String kindName = null;
            KindDTO kindDTO = productService.getEnabledKindDTOById(shopId, purchaseInventoryItemDTO.getProductKindId());
            if (null != kindDTO) {
              kindName = kindDTO.getName();
            }
            purchaseInventoryItemDTO.setProductKind(kindName);
          }
        }
      }
      purchaseInventoryDTO.setAcceptor((String)request.getSession().getAttribute("userName"));
      purchaseInventoryDTO.setAcceptorId((Long)request.getSession().getAttribute("userId"));
      setSupplierDropDownInfo(modelMap,request);
      modelMap.addAttribute("purchaseInventoryDTO", purchaseInventoryDTO);
      return PAGE_GOODSSTORAGE;
    } else if (draftOrderDTO.getOrderTypeEnum().equals(OrderTypes.SALE)) {
      if(draftOrderDTO.getReceiptNo() == null || "".equals(draftOrderDTO.getReceiptNo())) {
        draftOrderDTO.setReceiptNo(txnService.getReceiptNo(shopId, OrderTypes.SALE, null));
        draftOrderService.saveOrUpdateDraftOrder(draftOrderDTO);
      }
      SalesOrderDTO salesOrderDTO = draftOrderDTO.toSalesOrderDTO();
      if(!"newPage".equals(request.getParameter("flag")) && !"true".equals(request.getParameter("newOpen")) && !"true".equals(request.getParameter("newSettlePage"))) {
        salesOrderDTO.setReceiptNo(request.getParameter("receiptNo"));
      }
      SalesOrderItemDTO[] salesOrderItemDTOs = salesOrderDTO.getItemDTOs();
      if (null != salesOrderItemDTOs && salesOrderItemDTOs.length > 0) {
        for (SalesOrderItemDTO salesOrderItemDTO : salesOrderItemDTOs) {
          if (StringUtils.isBlank(salesOrderItemDTO.getProductName())) {
            continue;
          }
          if (null != salesOrderItemDTO.getProductId()) {
            ProductLocalInfoDTO productLocalInfoDTO = productService.getProductLocalInfoById(salesOrderItemDTO.getProductId(), shopId);
            if (null == productLocalInfoDTO || null == productLocalInfoDTO.getBusinessCategoryId()) {
              continue;
            }
            Category category = rfiTxnService.getEnabledCategoryById(shopId, productLocalInfoDTO.getBusinessCategoryId());
            if (null == category) {
              salesOrderItemDTO.setBusinessCategoryName(null);
              salesOrderItemDTO.setBusinessCategoryId(null);
              continue;
            }
            salesOrderItemDTO.setBusinessCategoryName(category.getCategoryName());
            salesOrderItemDTO.setBusinessCategoryId(category.getId());
          } else {
            Category category = rfiTxnService.getEnabledCategoryById(shopId, salesOrderItemDTO.getBusinessCategoryId());
            if (null == category) {
              salesOrderItemDTO.setBusinessCategoryName(null);
              salesOrderItemDTO.setBusinessCategoryId(null);
              continue;
            }
            salesOrderItemDTO.setBusinessCategoryName(category.getCategoryName());
            salesOrderItemDTO.setBusinessCategoryId(category.getId());
          }
        }
      }
      salesOrderDTO.setGoodsSaler((String)request.getSession().getAttribute("userName"));
      salesOrderDTO.setGoodsSalerId((Long)request.getSession().getAttribute("userId"));
      modelMap.addAttribute("salesOrderDTO", salesOrderDTO);
      return PAGE_GOODSSALE;
    } else if (draftOrderDTO.getOrderTypeEnum().equals(OrderTypes.RETURN)) {
      if(draftOrderDTO.getReceiptNo() == null || "".equals(draftOrderDTO.getReceiptNo())) {
        draftOrderDTO.setReceiptNo(txnService.getReceiptNo(shopId, OrderTypes.RETURN, null));
        draftOrderService.saveOrUpdateDraftOrder(draftOrderDTO);
      }
      PurchaseReturnDTO purchaseReturnDTO = draftOrderDTO.toPurchaseReturnDTO();
      if(!"newPage".equals(request.getParameter("flag")) && !"true".equals(request.getParameter("newOpen")) && !"true".equals(request.getParameter("newSettlePage"))) {
        purchaseReturnDTO.setReceiptNo(request.getParameter("receiptNo"));
      }
      if(ArrayUtil.isEmpty(purchaseReturnDTO.getItemDTOs())){
        PurchaseReturnItemDTO purchaseReturnItemDTO = new PurchaseReturnItemDTO();
        PurchaseReturnItemDTO[] itemDTOs = {purchaseReturnItemDTO};
        purchaseReturnDTO.setItemDTOs(itemDTOs);
      }
      if (DraftOrderStatus.DRAFT_READ_ONLY.equals(draftOrderDTO.getEditStatus())) {
        purchaseReturnDTO.setReadOnly(true);
      } else if (DraftOrderStatus.DRAFT_READ_WRITE.equals(draftOrderDTO.getEditStatus())) {
        purchaseReturnDTO.setReadOnly(false);
      }
      modelMap.addAttribute("purchaseReturnDTO", purchaseReturnDTO);
      modelMap.addAttribute("categoryList", TxnConstant.getCustomerTypeMap(request.getLocale()));
      modelMap.addAttribute("settlementTypeList", TxnConstant.getSettlementTypeMap(request.getLocale()));
      modelMap.addAttribute("invoiceCategoryList", TxnConstant.getInvoiceCatagoryMap(request.getLocale()));
      return PAGE_RETURNSTORAGE;
    } else if (draftOrderDTO.getOrderTypeEnum().equals(OrderTypes.REPAIR)) {
      if(draftOrderDTO.getReceiptNo() == null || "".equals(draftOrderDTO.getReceiptNo())) {
        draftOrderDTO.setReceiptNo(txnService.getReceiptNo(shopId, OrderTypes.REPAIR, null));
        draftOrderService.saveOrUpdateDraftOrder(draftOrderDTO);
      }
      String receiptNo = draftOrderDTO.getReceiptNo();

      if (draftOrderDTO.getTxnOrderId() != null) {
        modelMap.addAttribute("repairOrderId", draftOrderDTO.getTxnOrderId());
        return PAGE_REPAIR_BY_ID + "&receiptNo=" + receiptNo;
//      } else if (StringUtils.isNotBlank(draftOrderDTO.getVehicle())) {
//        modelMap.addAttribute("vehicleNumber", draftOrderDTO.getVehicle());
//        RepairOrderDTO repairOrderDTO = txnService.getUnbalancedAccountRepairOrderByVehicleNumber(draftOrderDTO.getShopId(), draftOrderDTO.getVehicle().toUpperCase(), null);
//
//        if (null == repairOrderDTO) {
//          if(!"newPage".equals(request.getParameter("flag")) && !"true".equals(request.getParameter("newOpen")) && !"true".equals(request.getParameter("newSettlePage"))) {
//            receiptNo = request.getParameter("receiptNo");
//          }
//        } else {
//          receiptNo = request.getParameter("receiptNo");
//        }
//        return PAGE_REPAIR_BY_VEHICLE + "&cancle=noId&receiptNo=" + receiptNo;
      } else {
        if(!"newPage".equals(request.getParameter("flag")) && !"true".equals(request.getParameter("newOpen")) && !"true".equals(request.getParameter("newSettlePage"))) {
          receiptNo = request.getParameter("receiptNo");
        }
        modelMap.addAttribute("draftOrderId", draftOrderDTO.getId());
        return PAGE_REPAIR + "&receiptNo=" + receiptNo;
      }
    } else if (draftOrderDTO.getOrderTypeEnum().equals(OrderTypes.SALE_RETURN)) {
      if(draftOrderDTO.getReceiptNo() == null || "".equals(draftOrderDTO.getReceiptNo())) {
        draftOrderDTO.setReceiptNo(txnService.getReceiptNo(shopId, OrderTypes.SALE_RETURN, null));
        draftOrderService.saveOrUpdateDraftOrder(draftOrderDTO);
      }
      SalesReturnDTO salesReturnDTO = draftOrderDTO.toSalesReturnDTO();

      if("true".equals(request.getParameter("isFromDraft")) || !"true".equals(request.getParameter("isFromDraft")) && !"newPage".equals(request.getParameter("flag")) && !"true".equals(request.getParameter("newOpen")) && !"true".equals(request.getParameter("newSettlePage"))) {
        salesReturnDTO.setReceiptNo(request.getParameter("receiptNo"));
      }
      SalesReturnItemDTO[] salesReturnItemDTOs = salesReturnDTO.getItemDTOs();
      if (!ArrayUtil.isEmpty(salesReturnItemDTOs)) {
        for (SalesReturnItemDTO salesReturnItemDTO : salesReturnItemDTOs) {
          if (StringUtils.isBlank(salesReturnItemDTO.getProductName())) {
            continue;
          }
          if (null != salesReturnItemDTO.getProductId()) {
            ProductLocalInfoDTO productLocalInfoDTO = productService.getProductLocalInfoById(salesReturnItemDTO.getProductId(), shopId);
            if (null == productLocalInfoDTO || null == productLocalInfoDTO.getBusinessCategoryId()) {
              continue;
            }
            Category category = rfiTxnService.getEnabledCategoryById(shopId, productLocalInfoDTO.getBusinessCategoryId());
            if (null == category) {
              salesReturnItemDTO.setBusinessCategoryName(null);
              salesReturnItemDTO.setBusinessCategoryId(null);
              continue;
            }
            salesReturnItemDTO.setBusinessCategoryName(category.getCategoryName());
            salesReturnItemDTO.setBusinessCategoryId(category.getId());
          } else {
            Category category = rfiTxnService.getEnabledCategoryById(shopId, salesReturnItemDTO.getBusinessCategoryId());
            if (null == category) {
              salesReturnItemDTO.setBusinessCategoryName(null);
              salesReturnItemDTO.setBusinessCategoryId(null);
              continue;
            }
            salesReturnItemDTO.setBusinessCategoryName(category.getCategoryName());
            salesReturnItemDTO.setBusinessCategoryId(category.getId());
          }
        }
      }
      if (DraftOrderStatus.DRAFT_READ_ONLY.equals(draftOrderDTO.getEditStatus())) {
        salesReturnDTO.setReadOnly(true);
      } else if (DraftOrderStatus.DRAFT_READ_WRITE.equals(draftOrderDTO.getEditStatus())) {
        salesReturnDTO.setReadOnly(false);
      }
      modelMap.addAttribute("salesReturnDTO", salesReturnDTO);
      modelMap.addAttribute("draftOrderId", draftOrderDTO.getId());
      return PAGE_SALES_RETURN;
    } else {
      return null;
    }
  }

  @RequestMapping(params = "method=getDraftOrderToPrint")
  public void getDraftOrderToPrint(HttpServletRequest request, HttpServletResponse response) throws Exception {
    String draftOrderId = (String) request.getParameter("id");
    String type = (String) request.getParameter("type");
    if (StringUtils.isBlank(draftOrderId) || StringUtils.isBlank(type)) {
      LOG.debug("method=getDraftOrderToPrint");
      LOG.debug("draftOrderId{}", draftOrderId);
      LOG.debug("type{}", type);
      return;
    }

    Long id = Long.valueOf(draftOrderId);
    Long shopId = WebUtil.getShopId(request);
    Long shopVersionId = WebUtil.getShopVersionId(request);
    DraftOrderDTO draftOrderDTO = draftOrderService.getOrderByDraftOrderId(shopId,shopVersionId,id);

    if (null == draftOrderDTO) {
      LOG.debug("method=getDraftOrderToPrint");
      LOG.debug("draftOrderDTO is null");
      return;
    }

    OrderTypes types = OrderTypes.valueOf(type);
    if (StringUtil.isEmpty(draftOrderDTO.getMobile())
        && !StringUtil.isEmpty(draftOrderDTO.getLandLine())
        && !OrderTypes.REPAIR.equals(types)) {
      draftOrderDTO.setMobile(draftOrderDTO.getLandLine());
    }
    request.setAttribute("draftOrderDTO", draftOrderDTO);

    toPrint(request, response, types);

  }

  public void toPrint(HttpServletRequest request, HttpServletResponse response, OrderTypes types) throws Exception {
    IPrintService printService = ServiceManager.getService(IPrintService.class);
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    IUserService userService = ServiceManager.getService(IUserService.class);
    PrintWriter out = response.getWriter();
    response.setContentType("text/html");
    response.setCharacterEncoding("UTF-8");
    DraftOrderDTO draftOrderDTO = (DraftOrderDTO) request.getAttribute("draftOrderDTO");
    try {
      ShopDTO shopDTO = configService.getShopById(WebUtil.getShopId(request));
      PrintTemplateDTO printTemplateDTO = printService.getSinglePrintTemplateDTOByShopIdAndType(WebUtil.getShopId(request), types);
      draftOrderDTO.setShopAddress(shopDTO.getAddress());
      draftOrderDTO.setShopName(shopDTO.getName());
      draftOrderDTO.setShopLandLine(shopDTO.getLandline());
      String editStr = DateUtil.convertDateLongToDateString(DateUtil.YEAR_MONTH_DATE, System.currentTimeMillis());

      if (null != printTemplateDTO) {
        byte bytes[] = printTemplateDTO.getTemplateHtml();
        String str = new String(bytes, "UTF-8");

        //获取VelocityEngine
        VelocityEngine ve = createVelocityEngine();
        //创建资源库
        StringResourceRepository repo = StringResourceLoader.getRepository();
        String myTemplateName = "draftPrint" + types.toString() + String.valueOf(WebUtil.getShopId(request));
        String myTemplate = str;
        //模板资源存放 资源库 中
        repo.putStringResource(myTemplateName, myTemplate);
        //从资源库中加载模板
        Template template = ve.getTemplate(myTemplateName);
        //取得velocity的模版
        Template t = ve.getTemplate(myTemplateName, "UTF-8");
        //取得velocity的上下文context
        VelocityContext context = new VelocityContext();
        context.put("storeManagerMobile", null == shopDTO.getStoreManagerMobile() ? "" : shopDTO.getStoreManagerMobile());
        if (OrderTypes.PURCHASE == draftOrderDTO.getOrderTypeEnum()) {
          PurchaseOrderDTO purchaseOrderDTO = draftOrderDTO.toPurchaseOrderDTO();
          purchaseOrderDTO.setEditDateStr(editStr);
          context.put("purchaseOrderDTO", purchaseOrderDTO);
        } else if (OrderTypes.INVENTORY == draftOrderDTO.getOrderTypeEnum()) {
          PurchaseInventoryDTO purchaseInventoryDTO = draftOrderDTO.toPurchaseInventoryDTO();
          if(purchaseInventoryDTO.getStorehouseId() != null) {
            StoreHouseDTO storeHouseDTO = ServiceManager.getService(IStoreHouseService.class).getStoreHouseDTOById(draftOrderDTO.getShopId(), purchaseInventoryDTO.getStorehouseId());
            if(storeHouseDTO != null) {
              purchaseInventoryDTO.setStorehouseName(storeHouseDTO.getName());
            }
          }
          purchaseInventoryDTO.setEditDateStr(editStr);
          context.put("purchaseInventoryDTO", purchaseInventoryDTO);
        } else if (OrderTypes.SALE == draftOrderDTO.getOrderTypeEnum()) {
          SalesOrderDTO salesOrderDTO = draftOrderDTO.toSalesOrderDTO();
          salesOrderDTO.setEditDateStr(editStr);
          if (null != salesOrderDTO.getCustomerId()) {
            ICustomerService customerService = ServiceManager.getService(ICustomerService.class);
            CustomerDTO customerDTO = customerService.getCustomerById(salesOrderDTO.getCustomerId());
            salesOrderDTO.setAddress(null == customerDTO ? "" : customerDTO.getAddress());
          }
          CustomerRecord customerRecord=ServiceManager.getService(ICustomerService.class).getUniqueCustomerRecordByCustomerId(WebUtil.getShopId(request),salesOrderDTO.getCustomerId());
          Double[] payableAndReceivable = ServiceManager.getService(ITxnService.class).getPayableAndReceivable(WebUtil.getShopId(request), salesOrderDTO.getCustomerId());
          context.put("totalReceivable", NumberUtil.numberValue(payableAndReceivable[1], 0d));
          salesOrderDTO.setSettledAmount(0d);
          context.put("salesOrderDTO", salesOrderDTO);
        } else if (OrderTypes.RETURN == draftOrderDTO.getOrderTypeEnum()) {
          PurchaseReturnDTO purchaseReturnDTO = draftOrderDTO.toPurchaseReturnDTO();
          purchaseReturnDTO.setEditDateStr(editStr);
          context.put("purchaseReturnDTO", purchaseReturnDTO);
        } else if (OrderTypes.REPAIR == draftOrderDTO.getOrderTypeEnum()) {
          RepairOrderDTO repairOrderDTO = draftOrderDTO.toRepairOrderDTO();
          Map fuelNumberList = TxnConstant.getFuelNumberMap(request.getLocale());
          if(repairOrderDTO.getVechicleId()!=null){
            VehicleDTO vehicleDTO = userService.getVehicleById(repairOrderDTO.getVechicleId());
            repairOrderDTO.setVehicleBuyDate(vehicleDTO==null?null:vehicleDTO.getCarDate());
          }else if(StringUtils.isNotBlank(repairOrderDTO.getVechicle()) && repairOrderDTO.getCustomerId()!=null){
            List<CarDTO> carDTOs = userService.getVehiclesByCustomerId(draftOrderDTO.getShopId(), repairOrderDTO.getCustomerId());
            if(CollectionUtils.isNotEmpty(carDTOs)){
              for(CarDTO carDTO : carDTOs){
                if(carDTO.getLicenceNo().equalsIgnoreCase(repairOrderDTO.getVechicle())){
                  repairOrderDTO.setVehicleBuyDate(carDTO.getCarDate());
                  break;
                }
              }
            }
          }
          CustomerRecordDTO customerRecordDTO = null;
          if (null != repairOrderDTO.getCustomerId()) {
            List<CustomerRecordDTO> customerRecordDTOs = userService.getCustomerRecordByCustomerId(repairOrderDTO.getCustomerId());
            if (null != customerRecordDTOs && customerRecordDTOs.size() > 0) {
              customerRecordDTO = customerRecordDTOs.get(0);
              customerRecordDTO.setRepayDateStr(repairOrderDTO.getHuankuanTime());
            }
            Double[] payableAndReceivable = ServiceManager.getService(ITxnService.class).getPayableAndReceivable(WebUtil.getShopId(request), repairOrderDTO.getCustomerId());
            context.put("totalReceivable", NumberUtil.numberValue(payableAndReceivable[1], 0d));
          }else{
            context.put("totalReceivable", 0d);
          }
          repairOrderDTO.setEditDateStr(editStr);
          repairOrderDTO.setId(Long.valueOf("-1"));
          context.put("repairOrderDTO", repairOrderDTO);
          context.put("storeManagerMobile", shopDTO.getStoreManagerMobile());
          context.put("fuelNumberList", fuelNumberList);
          context.put("customerRecordDTO", customerRecordDTO);
          context.put("userName", (String) request.getSession().getAttribute("userName"));
        }
        //把数据填入上下文

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
      LOG.debug("toPrintSmallCard");
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

  public void setDraftOrderService(IDraftOrderService draftOrderService) {
    this.draftOrderService = draftOrderService;
  }

  private void setSupplierDropDownInfo(ModelMap model, HttpServletRequest request) {
    Map categoryList = TxnConstant.getCustomerTypeMap(request.getLocale());
    model.addAttribute(MODEL_CATEGORY_LIST, categoryList);
    Map settlementTypeList = TxnConstant.getSettlementTypeMap(request.getLocale());
    model.addAttribute(MODEL_SETTLEMENTTYPE_LIST, settlementTypeList);
    Map invoiceCategoryList = TxnConstant.getInvoiceCatagoryMap(request.getLocale());
    model.addAttribute(MODEL_INVOICECATEGORY_LIST, invoiceCategoryList);
  }

  public static void main(String[]args) throws ParseException {
    double time=24*60.0*60;
    System.out.println(time);
    System.out.println(DateUtil.convertDateStringToDateLong(DateUtil.ALL,"2012-3-13 10:22:20"));

  }

  @RequestMapping(params = "method=validateRepairOrderDraftOrder")
  @ResponseBody
  public Object  validateRepairOrderDraftOrder(HttpServletRequest request,String draftOrderIdStr){
    Long shopId = WebUtil.getShopId(request);
    Long shopVersionId = WebUtil.getShopVersionId(request);
    Result result = new Result();
    try {
      if (!StringUtil.isEmpty(draftOrderIdStr) && NumberUtil.isNumber(draftOrderIdStr)) {
        DraftOrderDTO draftOrderDTO = draftOrderService.getOrderByDraftOrderId(shopId,shopVersionId, NumberUtil.longValue(draftOrderIdStr));
      }
      return result;
    } catch (Exception e) {
      LOG.error("/draft.do?method=validateRepairOrderDraftOrder,shopId={},draftOrderIdStr ={}",shopId,draftOrderIdStr );
      LOG.error(e.getMessage(), e);
      return null;
    }
  }

}
