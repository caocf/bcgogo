package com.bcgogo.autoaccessoryonline;

import com.bcgogo.common.*;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.service.image.IImageService;
import com.bcgogo.enums.OrderStatus;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.enums.RemindEventType;
import com.bcgogo.enums.TodoOrderType;
import com.bcgogo.enums.config.ImageScene;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.product.service.IProductService;
import com.bcgogo.product.service.IPromotionsService;
import com.bcgogo.search.dto.*;
import com.bcgogo.search.service.order.ISearchOrderService;
import com.bcgogo.search.service.user.ISearchCustomerSupplierService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.*;
import com.bcgogo.txn.dto.remind.OrderCenterDTO;
import com.bcgogo.txn.dto.supplierComment.SupplierCommentRecordDTO;
import com.bcgogo.txn.model.SalesOrderItem;
import com.bcgogo.txn.service.*;
import com.bcgogo.txn.service.remind.IOrderCenterService;
import com.bcgogo.txn.service.supplierComment.ISupplierCommentService;
import com.bcgogo.user.dto.CustomerDTO;
import com.bcgogo.user.dto.SupplierDTO;
import com.bcgogo.user.service.ICustomerService;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.user.service.utils.BcgogoShopLogicResourceUtils;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.TxnConstant;
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
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: xzhu
 * Date: 12-10-22
 * Time: 下午5:33
 * 订单中心
 */
@Controller
@RequestMapping("/orderCenter.do")
public class OrderCenterController {
  private static final Logger LOG = LoggerFactory.getLogger(OrderCenterController.class);

  //clean by ZhangJuntao
  @RequestMapping(params = "method=showOrderCenter")
  public String showOrderCenter(HttpServletRequest request,HttpServletResponse response)throws Exception{
    Long shopId = WebUtil.getShopId(request);
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    IOrderCenterService orderCenterService = ServiceManager.getService(IOrderCenterService.class);
    OrderCenterDTO orderCenterDTO = orderCenterService.getOrderCenterStatistics(shopId);
    request.setAttribute("sale_today_new",orderCenterDTO.getSaleTodayNew());
    request.setAttribute("sale_early_new",orderCenterDTO.getSaleEarlyNew());
    request.setAttribute("sale_new",orderCenterDTO.getSaleNew());
    request.setAttribute("sale_stocking",orderCenterDTO.getSaleStocking());
    request.setAttribute("sale_dispatch",orderCenterDTO.getSaleDispatch());
    request.setAttribute("sale_sale_debt_done",orderCenterDTO.getSaleSaleDebtDone());
    request.setAttribute("sale_in_progress",orderCenterDTO.getSaleInProgress());
    request.setAttribute("sale_return_today_new",orderCenterDTO.getSaleReturnTodayNew());
    request.setAttribute("sale_return_early_new",orderCenterDTO.getSaleReturnEarlyNew());
    request.setAttribute("sale_return_new",orderCenterDTO.getSaleReturnNew());
    request.setAttribute("sale_return_in_progress",orderCenterDTO.getSaleReturnInProgress());
    request.setAttribute("purchase_today_new",orderCenterDTO.getPurchaseTodayNew());
    request.setAttribute("purchase_early_new",orderCenterDTO.getPurchaseEarlyNew());
    request.setAttribute("purchase_new",orderCenterDTO.getPurchaseNew());
    request.setAttribute("purchase_seller_stock",orderCenterDTO.getPurchaseSellerStock());
    request.setAttribute("purchase_seller_dispatch",orderCenterDTO.getPurchaseSellerDispatch());
    request.setAttribute("purchase_seller_refused",orderCenterDTO.getPurchaseSellerRefused());
    request.setAttribute("purchase_seller_stop",orderCenterDTO.getPurchaseSellerStop());
    request.setAttribute("purchase_in_progress",orderCenterDTO.getPurchaseInProgress());
    request.setAttribute("purchase_today_done",orderCenterDTO.getPurchaseTodayDone());
    request.setAttribute("purchase_early_done",orderCenterDTO.getPurchaseEarlyDone());
    request.setAttribute("purchase_done",orderCenterDTO.getPurchaseDone());
    request.setAttribute("purchase_return_today_new",orderCenterDTO.getPurchaseReturnTodayNew());
    request.setAttribute("purchase_return_early_new",orderCenterDTO.getPurchaseReturnEarlyNew());
    request.setAttribute("purchase_return_new",orderCenterDTO.getPurchaseReturnNew());
    request.setAttribute("purchase_return_seller_accept",orderCenterDTO.getPurchaseReturnSellerAccept());
    request.setAttribute("purchase_return_seller_refused",orderCenterDTO.getPurchaseReturnSellerRefused());
    request.setAttribute("purchase_return_in_progress",orderCenterDTO.getPurchaseReturnInProgress());
    //返回前，更新缓存中的数值
    List<Long> customerIdList = ServiceManager.getService(ICustomerService.class).getRelatedCustomerIdListByShopId(shopId);
    List<Long> supplierIdList = ServiceManager.getService(ICustomerService.class).getRelatedCustomerIdListByShopId(shopId);
    txnService.updateTodoOrderCountInMemcacheByTypeAndShopId(RemindEventType.TODO_SALE_ORDER, shopId, customerIdList);
    txnService.updateTodoOrderCountInMemcacheByTypeAndShopId(RemindEventType.TODO_SALE_RETURN_ORDER, shopId, customerIdList);
    txnService.updateTodoOrderCountInMemcacheByTypeAndShopId(RemindEventType.TODO_PURCHASE_ORDER, shopId, supplierIdList);
    txnService.updateTodoOrderCountInMemcacheByTypeAndShopId(RemindEventType.TODO_PURCHASE_RETURN_ORDER, shopId, supplierIdList);
         //用户引导
      CookieUtil.rebuildCookiesForUserGuide(request, response, true, new String[]{"PRODUCT_ONLINE_GUIDE_BEGIN", "PRODUCT_ONLINE_GUIDE_TXN"});
    return "autoaccessoryonline/ordercenter/orderCenterIndex";
  }


  //与消息有关系 参数修改请斟酌 zhangjuntao
  //待办单据初始页面
  @RequestMapping(params = "method=getTodoOrders")
  public String getTodoOrders(HttpServletRequest request, ModelMap model) throws Exception {
    //首先判断页面指向的参数
    String todoOrderType = request.getParameter("type");
    if(todoOrderType==null){
      throw new Exception("todoOrderType is null");
    }else{
      //待办销售单
      if(TodoOrderType.TODO_SALE_ORDERS.toString().equals(todoOrderType)){
//        return getTodoSaleOrders(request,model);
      return toMySaleOrdersPage(request, model);
      }
      //待办销售退货单
      else if(TodoOrderType.TODO_SALE_RETURN_ORDERS.toString().equals(todoOrderType)){
        return getTodoSaleReturnOrders(request,model);
      }
      //待办采购单
      else if(TodoOrderType.TODO_PURCHASE_ORDERS.toString().equals(todoOrderType)){

//      return getTodoPurchaseOrders(request,model);

        return toMyPurchaseOrdersPage(request, model);
      }
      //待办入库退货单
      else{
        return getTodoPurchaseReturnOrders(request,model);
      }
    }
  }

  private String getTodoSaleOrders(HttpServletRequest request, ModelMap model) throws Exception{
    IPromotionsService promotionsService = ServiceManager.getService(IPromotionsService.class);
    ICustomerService customerService = ServiceManager.getService(ICustomerService.class);
    IUserService userService = ServiceManager.getService(IUserService.class);
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    IProductService productService = ServiceManager.getService(IProductService.class);
    RFITxnService rfiTxnService = ServiceManager.getService(RFITxnService.class);
    Long shopId = WebUtil.getShopId(request);
    if(BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(WebUtil.getShopVersionId(request))){
      IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
      List<StoreHouseDTO> storeHouseDTOList = storeHouseService.getAllStoreHousesByShopId(shopId);
      model.addAttribute("storeHouseDTOList", storeHouseDTOList);//select 选项
    }
    String startTimeStr = request.getParameter("startTimeStr");
    String endTimeStr = request.getParameter("endTimeStr");
    String customerName = request.getParameter("customerName");
    String receiptNo = request.getParameter("receiptNo");
    String orderStatus = request.getParameter("orderStatus");
    // 默认状态 allTodo
    if(StringUtil.isEmpty(orderStatus)){
      orderStatus = "allTodo";
    }
    model.addAttribute("startTimeStr",StringUtil.isEmpty(startTimeStr)?"":startTimeStr);
    model.addAttribute("endTimeStr",StringUtil.isEmpty(endTimeStr)?"":endTimeStr);
    model.addAttribute("customerName",StringUtil.isEmpty(customerName)?"":customerName);
    model.addAttribute("receiptNo",StringUtil.isEmpty(receiptNo)?"":receiptNo);
    model.addAttribute("orderStatus",orderStatus);

    int pageSize = 5;
    int currentPage = NumberUtil.intValue(request.getParameter("pageNo"), 1);
    Pager pager = new Pager(0, currentPage, pageSize);
    //日期转换Long
    Long startTime = null;
    Long endTime = null;
    try{
      if(!StringUtil.isEmpty(startTimeStr)){
        //开始时间按该天0点算，左闭右开
        startTime = DateUtil.convertDateStringToDateLong("yyyy-MM-dd",startTimeStr);
      }
      if(!StringUtil.isEmpty(endTimeStr)){
        //结束时间按第二天的0点算，左闭右开
        endTime = DateUtil.convertDateStringToDateLong("yyyy-MM-dd",endTimeStr);
        endTime = endTime + 24*3600*1000 - 1;
      }

      //根据客户名称模糊匹配出所有客户的idList
      List<Long> fuzzyCustomerIdList = null;
      if(!StringUtil.isEmpty(customerName)){
        fuzzyCustomerIdList = userService.getCustomerIdsByNameWithFuzzyQuery(shopId,customerName);
      }
      //查出本店全部关联客户的List
      List<Long> relatedCustomerIdList = customerService.getRelatedCustomerIdListByShopId(shopId);
      //封装最终的customerIdList
      List<Long> customerIdList = null;
      //填了客户名，以模糊匹配结果为准
      if(!StringUtil.isEmpty(customerName)){
        //模糊匹配有结果
        if(CollectionUtils.isNotEmpty(fuzzyCustomerIdList)){
          //存在关联客户，需要取交集
          if(CollectionUtils.isNotEmpty(relatedCustomerIdList)){
            customerIdList = new ArrayList<Long>();
            for(int i=0;i<fuzzyCustomerIdList.size();i++){
              if(relatedCustomerIdList.contains(fuzzyCustomerIdList.get(i))){
                customerIdList.add(fuzzyCustomerIdList.get(i));
              }
            }
          }else{
            //不存在关联客户
            customerIdList = null;
          }
        }else{
          //模糊匹配无结果
          fuzzyCustomerIdList = new ArrayList<Long>();
          fuzzyCustomerIdList.add(-1l);
          customerIdList = fuzzyCustomerIdList;
        }
      }else{
        //没填客户名，以关联结果为准
        customerIdList = relatedCustomerIdList;
      }

      //条件查询全部单据的数量
      Long totalCount = txnService.getTodoSalesOrderCount(shopId,startTime,endTime,customerIdList,receiptNo,orderStatus);

      //组装分页对象
      pager = new Pager(totalCount.intValue(), NumberUtil.intValue(request.getParameter("pageNo"), 1), pager.getPageSize());
      request.setAttribute("pager", pager);
      request.setAttribute("pageNo", pager.getCurrentPage());
      request.setAttribute("pageCount", pager.getTotalPage());

      //销售单List
      List<SalesOrderDTO> orderDTOList = txnService.getTodoSalesOrderDTOListByCondition(shopId,startTime,endTime,customerIdList,receiptNo,orderStatus,pager);

      //物流信息
      Map<Long,ExpressDTO> expressDTOMap = new HashMap<Long,ExpressDTO>();

      //采购单状态
      Map<Long,OrderStatus> purchaseOrderStatusMap = new HashMap<Long, OrderStatus>();

      //六属性
      List<Map<Long,ProductDTO>> productDTOMapList = new ArrayList<Map<Long,ProductDTO>>();

      //根据单据ID获取item，并组装到DTO
      if(totalCount>0){
        for(int i=0;i<orderDTOList.size();i++){

          //放置欠款信息
          ReceivableDTO receivableDTO = txnService.getReceivableDTOByShopIdAndOrderId(shopId,orderDTOList.get(i).getId());
          if(null != receivableDTO)
          {
            orderDTOList.get(i).setDebt(receivableDTO.getDebt());
          }
          //组装item
          List<SalesOrderItem> itemList = txnService.getSaleOrderItemListByOrderId(orderDTOList.get(i).getId());
          SalesOrderItemDTO[] itemDTOs = new SalesOrderItemDTO[itemList.size()];
          //组装六属性
          Map<Long,ProductDTO> productDTOMap = new HashMap<Long,ProductDTO>();
          for(int j=0;j<itemList.size();j++){
            itemDTOs[j] = itemList.get(j).toDTO();
            if(itemDTOs[j].getPromotionsIds()!=null){
              Set<Long> ids=itemDTOs[j].getPromotionsIds();
              Long []promotionsIds=ids.toArray(new Long[ids.size()]);
              itemDTOs[j].setPromotionsDTOs(promotionsService.getPromotionsDTODetailById(shopId, promotionsIds));
            }
            //在待处理和备货中状态时，计算缺料
            if(orderDTOList.get(i).getStatus().equals(OrderStatus.STOCKING)){
              //组装缺料信息，先与库存量比对，如果够则不缺料；如果不够，再与单据的预留量比对，算出具体要补多少
              InventoryDTO inventoryDTO = txnService.getInventoryByShopIdAndProductId(shopId, itemDTOs[j].getProductId());
              double shortageAmount=0d;
              if(inventoryDTO!=null)
                shortageAmount=NumberUtil.subtraction(itemList.get(j).getAmount(),itemList.get(j).getReserved(),inventoryDTO.getAmount());
              if(shortageAmount>0){
                orderDTOList.get(i).setShortage(true);
                itemDTOs[j].setShortage(shortageAmount);
              }
            }
            //组装商品信息
            ProductDTO productDTO = productService.getProductByProductLocalInfoId(itemDTOs[j].getProductId(),shopId);
            //获取六属性
            productDTOMap.put(itemDTOs[j].getProductId(),productDTO);
          }
          orderDTOList.get(i).setItemDTOs(itemDTOs);
          //组装客户姓名
          CustomerDTO customerDTO = userService.getCustomerById(orderDTOList.get(i).getCustomerId());
          orderDTOList.get(i).setCustomer(customerDTO.getName());
          productDTOMapList.add(productDTOMap);
          //组装采购单的预期交货时间，需要采购单ID和客户shopId，除了已结算和已作废
          if(orderDTOList.get(i).getPurchaseOrderId()!=null){
            PurchaseOrderDTO purchaseOrderDTO = txnService.getPurchaseOrder(orderDTOList.get(i).getPurchaseOrderId(),customerDTO.getCustomerShopId());
            if(purchaseOrderDTO!=null){
              orderDTOList.get(i).setPurchaseVestDate(DateUtil.convertDateLongToDateString("yyyy-MM-dd",purchaseOrderDTO.getDeliveryDate()));
              orderDTOList.get(i).setPromotionsInfoJson(purchaseOrderDTO.getPromotionsInfoJson());
            }
          }
          //组装物流信息，已发货状态显示
          if(orderDTOList.get(i).getStatus().equals(OrderStatus.DISPATCH)){
            Long expressId = orderDTOList.get(i).getExpressId();
            if(expressId!=null){
              ExpressDTO expressDTO = rfiTxnService.getExpressDTOById(expressId);
              if(expressDTO!=null){
                expressDTOMap.put(orderDTOList.get(i).getId(),expressDTO);
              }
            }
          }
          //备货中和已发货状态下，需要获取客户采购单的状态，如果是已入库，则不能作废
          if(orderDTOList.get(i).getStatus().equals(OrderStatus.STOCKING) || orderDTOList.get(i).getStatus().equals(OrderStatus.DISPATCH)){
            PurchaseOrderDTO purchaseOrderDTO = txnService.getPurchaseOrder(orderDTOList.get(i).getPurchaseOrderId(),customerDTO.getCustomerShopId());
            if(purchaseOrderDTO!=null){
              purchaseOrderStatusMap.put(orderDTOList.get(i).getId(),purchaseOrderDTO.getStatus());
            }
          }
        }
      }

      //待办销售单的状态
      List<OrderStatus> statusList = new ArrayList<OrderStatus>();
      statusList.add(OrderStatus.PENDING);
      statusList.add(OrderStatus.STOCKING);
      statusList.add(OrderStatus.DISPATCH);
      statusList.add(OrderStatus.REFUSED);
      statusList.add(OrderStatus.STOP);
      statusList.add(OrderStatus.SELLER_STOP);
      statusList.add(OrderStatus.SALE_REPEAL);
      statusList.add(OrderStatus.SALE_DEBT_DONE);
      statusList.add(OrderStatus.SALE_DONE);

      model.addAttribute("statusList",statusList);
      model.addAttribute("orderDTOList",orderDTOList);
      model.addAttribute("productDTOMapList",productDTOMapList);
      model.addAttribute("expressDTOMap",expressDTOMap);
      model.addAttribute("purchaseOrderStatusMap",purchaseOrderStatusMap);

      //今天的时间段
      Long timePoint = null;
      try{
        timePoint = DateUtil.getToday(DateUtil.YEAR_MONTH_DATE, new Date());
      }catch (Exception e){
        LOG.error(e.getMessage(),e);
      }
      //今日新增
      Long sale_today_new = txnService.getTodoSalesOrderCount(shopId,timePoint,null,relatedCustomerIdList,null,OrderStatus.PENDING.toString());
      //往日新增
      Long sale_early_new = txnService.getTodoSalesOrderCount(shopId,null,timePoint,relatedCustomerIdList,null,OrderStatus.PENDING.toString());
      //新增总数
      Long sale_new = sale_today_new + sale_early_new;
      //备货中
      Long sale_stocking = txnService.getTodoSalesOrderCount(shopId,null,null,relatedCustomerIdList,null,OrderStatus.STOCKING.toString());
      //已发货
      Long sale_dispatch = txnService.getTodoSalesOrderCount(shopId,null,null,relatedCustomerIdList,null,OrderStatus.DISPATCH.toString());
      //欠款结算
      Long sale_sale_debt_done = txnService.getTodoSalesOrderCount(shopId,null,null,relatedCustomerIdList,null,OrderStatus.SALE_DEBT_DONE.toString());
      //处理中（各个待处理状态的总和）
      Long sale_in_progress = sale_stocking + sale_dispatch + sale_sale_debt_done;
      request.setAttribute("sale_today_new",sale_today_new);
      request.setAttribute("sale_early_new",sale_early_new);
      request.setAttribute("sale_new",sale_new);
      request.setAttribute("sale_stocking",sale_stocking);
      request.setAttribute("sale_dispatch",sale_dispatch);
      request.setAttribute("sale_sale_debt_done",sale_sale_debt_done);
      request.setAttribute("sale_in_progress",sale_in_progress);

      //返回前，更新缓存中的数值
      txnService.updateTodoOrderCountInMemcacheByTypeAndShopId(RemindEventType.TODO_SALE_ORDER, shopId, relatedCustomerIdList);
    }catch (Exception e){
      LOG.error(e.getMessage(),e);
    }

    return "autoaccessoryonline/ordercenter/todoSaleOrders";
  }

  private String getTodoSaleReturnOrders(HttpServletRequest request, ModelMap model) throws Exception {
    ICustomerService customerService = ServiceManager.getService(ICustomerService.class);
    IUserService userService = ServiceManager.getService(IUserService.class);
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    IProductService productService = ServiceManager.getService(IProductService.class);
    RFITxnService rfiTxnService = ServiceManager.getService(RFITxnService.class);

    Long shopId = WebUtil.getShopId(request);
    String startTimeStr = request.getParameter("startTimeStr");
    String endTimeStr = request.getParameter("endTimeStr");
    String customerName = request.getParameter("customerName");
    String receiptNo = request.getParameter("receiptNo");
    String orderStatus = request.getParameter("orderStatus");
    // 默认状态 allTodo
    if(StringUtil.isEmpty(orderStatus)){
      orderStatus = "allTodo";
    }
    model.addAttribute("startTimeStr",StringUtil.isEmpty(startTimeStr)?"":startTimeStr);
    model.addAttribute("endTimeStr",StringUtil.isEmpty(endTimeStr)?"":endTimeStr);
    model.addAttribute("customerName",StringUtil.isEmpty(customerName)?"":customerName);
    model.addAttribute("receiptNo",StringUtil.isEmpty(receiptNo)?"":receiptNo);
    model.addAttribute("orderStatus",orderStatus);

    int pageSize = 5;
    int currentPage = NumberUtil.intValue(request.getParameter("pageNo"), 1);
    Pager pager = new Pager(0, currentPage, pageSize);
    //日期转换Long
    Long startTime = null;
    Long endTime = null;
    try{
      if(!StringUtil.isEmpty(startTimeStr)){
        //开始时间按该天0点算，左闭右开
        startTime = DateUtil.convertDateStringToDateLong("yyyy-MM-dd",startTimeStr);
      }
      if(!StringUtil.isEmpty(endTimeStr)){
        //结束时间按第二天的0点算，左闭右开
        endTime = DateUtil.convertDateStringToDateLong("yyyy-MM-dd",endTimeStr);
        endTime = endTime + 24*3600*1000 - 1;
      }

      //根据客户名称模糊匹配出所有客户的idList
      List<Long> fuzzyCustomerIdList = null;
      if(!StringUtil.isEmpty(customerName)){
        fuzzyCustomerIdList = userService.getCustomerIdsByNameWithFuzzyQuery(shopId,customerName);
      }
      //查出本店全部关联客户的List
      List<Long> relatedCustomerIdList = customerService.getRelatedCustomerIdListByShopId(shopId);
      //封装最终的customerIdList
      List<Long> customerIdList = null;
      //填了客户名，以模糊匹配结果为准
      if(!StringUtil.isEmpty(customerName)){
        //模糊匹配有结果
        if(CollectionUtils.isNotEmpty(fuzzyCustomerIdList)){
          //存在关联客户，需要取交集
          if(CollectionUtils.isNotEmpty(relatedCustomerIdList)){
            customerIdList = new ArrayList<Long>();
            for(int i=0;i<fuzzyCustomerIdList.size();i++){
              if(relatedCustomerIdList.contains(fuzzyCustomerIdList.get(i))){
                customerIdList.add(fuzzyCustomerIdList.get(i));
              }
            }
          }else{
            //不存在关联客户
            customerIdList = null;
          }
        }else{
          //模糊匹配无结果
          fuzzyCustomerIdList = new ArrayList<Long>();
          fuzzyCustomerIdList.add(-1l);
          customerIdList = fuzzyCustomerIdList;
        }
      }else{
        //没填客户名，以关联结果为准
        customerIdList = relatedCustomerIdList;
      }

      //条件查询全部单据的数量
      Long totalCount = txnService.getTodoSalesReturnOrderCount(shopId,startTime,endTime,customerIdList,receiptNo,orderStatus);

      //组装分页对象
      pager = new Pager(totalCount.intValue(), NumberUtil.intValue(request.getParameter("pageNo"), 1), pager.getPageSize());
      request.setAttribute("pager", pager);
      request.setAttribute("pageNo", pager.getCurrentPage());
      request.setAttribute("pageCount", pager.getTotalPage());

      //销售退货单List
      List<SalesReturnDTO> orderDTOList = txnService.getTodoSalesReturnOrderDTOListByCondition(shopId,startTime,endTime,customerIdList,receiptNo,orderStatus,pager);

      //六属性
      List<Map<Long,ProductDTO>> productDTOMapList = new ArrayList<Map<Long,ProductDTO>>();

      //根据单据ID获取item，并组装到DTO
      if(totalCount>0){
        for(int i=0;i<orderDTOList.size();i++){
          //组装item
          List<SalesReturnItemDTO> itemList = txnService.getSalesReturnItemDTOs(shopId, orderDTOList.get(i).getId());
          SalesReturnItemDTO[] itemDTOs = new SalesReturnItemDTO[itemList.size()];
          //组装六属性
          Map<Long,ProductDTO> productDTOMap = new HashMap<Long,ProductDTO>();
          if(!OrderStatus.SETTLED.equals(orderDTOList.get(i).getStatus()) && orderDTOList.get(i).getPurchaseReturnOrderId()!=null){
            Map<Long,PurchaseReturnItemDTO> purchaseReturnItemDTOMap = new HashMap<Long,PurchaseReturnItemDTO>();
            IPurchaseReturnService purchaseReturnService = ServiceManager.getService(IPurchaseReturnService.class);
            PurchaseReturnDTO purchaseReturnDTO = rfiTxnService.getPurchaseReturnDTOById(orderDTOList.get(i).getPurchaseReturnOrderId());
            purchaseReturnDTO = purchaseReturnService.fillPurchaseReturnItemDTOsDetailInfo(purchaseReturnDTO);
            if (purchaseReturnDTO != null && !ArrayUtils.isEmpty(purchaseReturnDTO.getItemDTOs())) {
              for (PurchaseReturnItemDTO purchaseReturnItemDTO : purchaseReturnDTO.getItemDTOs()) {
                purchaseReturnItemDTOMap.put(purchaseReturnItemDTO.getId(), purchaseReturnItemDTO);
              }
              for (int j = 0; j < itemList.size(); j++) {
                itemDTOs[j] = itemList.get(j);
                //组装商品信息
                PurchaseReturnItemDTO purchaseReturnItemDTO = purchaseReturnItemDTOMap.get(itemDTOs[j].getCustomerOrderItemId());
                //获取六属性
                if (purchaseReturnItemDTO != null) {
                  ProductDTO productDTO = new ProductDTO();
                  productDTO.setName(purchaseReturnItemDTO.getProductName());
                  productDTO.setBrand(purchaseReturnItemDTO.getBrand());
                  productDTO.setModel(purchaseReturnItemDTO.getModel());
                  productDTO.setSpec(purchaseReturnItemDTO.getSpec());
                  productDTO.setVehicleBrand(purchaseReturnItemDTO.getVehicleBrand());
                  productDTO.setVehicleModel(purchaseReturnItemDTO.getVehicleModel());
                  productDTO.setCommodityCode(purchaseReturnItemDTO.getCommodityCode());
                  productDTOMap.put(itemDTOs[j].getProductId(), productDTO);
                }
              }
            } else {
              for (int j = 0; j < itemList.size(); j++) {
                itemDTOs[j] = itemList.get(j);
                //组装商品信息
                ProductDTO productDTO = productService.getProductByProductLocalInfoId(itemDTOs[j].getProductId(), shopId);
                //获取六属性
                productDTOMap.put(itemDTOs[j].getProductId(), productDTO);
              }
            }
          }else{
            for(int j=0;j<itemList.size();j++){
              itemDTOs[j] = itemList.get(j);
              //组装商品信息
              ProductDTO productDTO = productService.getProductByProductLocalInfoId(itemDTOs[j].getProductId(),shopId);
              //获取六属性
              productDTOMap.put(itemDTOs[j].getProductId(),productDTO);
            }
          }

          orderDTOList.get(i).setItemDTOs(itemDTOs);
          //组装客户姓名
          orderDTOList.get(i).setCustomer(userService.getCustomerById(orderDTOList.get(i).getCustomerId()).getName());
          productDTOMapList.add(productDTOMap);
        }
      }

      //待办销售退货单的状态
      List<OrderStatus> statusList = new ArrayList<OrderStatus>();
      statusList.add(OrderStatus.PENDING);
      statusList.add(OrderStatus.WAITING_STORAGE);
      statusList.add(OrderStatus.REFUSED);
      statusList.add(OrderStatus.SETTLED);
      statusList.add(OrderStatus.STOP);

      model.addAttribute("statusList",statusList);
      model.addAttribute("orderDTOList",orderDTOList);
      model.addAttribute("productDTOMapList",productDTOMapList);

      //今天的时间段
      Long timePoint = null;
      try{
        timePoint = DateUtil.getToday(DateUtil.YEAR_MONTH_DATE, new Date());
      }catch (Exception e){
        LOG.error(e.getMessage(),e);
      }
      //今日新增
      Long sale_return_today_new = txnService.getTodoSalesReturnOrderCount(shopId,timePoint,null,relatedCustomerIdList,null,OrderStatus.PENDING.toString());
      //往日新增
      Long sale_return_early_new = txnService.getTodoSalesReturnOrderCount(shopId,null,timePoint,relatedCustomerIdList,null,OrderStatus.PENDING.toString());
      //新增总数
      Long sale_return_new = sale_return_today_new + sale_return_early_new;
      //处理中（待入库）
      Long sale_return_in_progress = txnService.getTodoSalesReturnOrderCount(shopId,null,null,relatedCustomerIdList,null,OrderStatus.WAITING_STORAGE.toString());
      request.setAttribute("sale_return_today_new",sale_return_today_new);
      request.setAttribute("sale_return_early_new",sale_return_early_new);
      request.setAttribute("sale_return_new",sale_return_new);
      request.setAttribute("sale_return_in_progress",sale_return_in_progress);

      //返回前，更新缓存中的数值
      txnService.updateTodoOrderCountInMemcacheByTypeAndShopId(RemindEventType.TODO_SALE_RETURN_ORDER, shopId, relatedCustomerIdList);
    }catch (Exception e){
      LOG.error(e.getMessage(),e);
    }

    return "autoaccessoryonline/ordercenter/todoSaleReturnOrders";
  }

  // old page old controller
  private String getTodoPurchaseOrders(HttpServletRequest request, ModelMap model) throws Exception {
    IUserService userService = ServiceManager.getService(IUserService.class);
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    IPromotionsService promotionsService = ServiceManager.getService(IPromotionsService.class);
    IProductService productService = ServiceManager.getService(IProductService.class);
    RFITxnService rfiTxnService = ServiceManager.getService(RFITxnService.class);

    Long shopId = WebUtil.getShopId(request);
    String startTimeStr = request.getParameter("startTimeStr");
    String endTimeStr = request.getParameter("endTimeStr");
    String supplierName = request.getParameter("supplierName");
    String receiptNo = request.getParameter("receiptNo");
    String orderStatus = request.getParameter("orderStatus");
    String timeField = request.getParameter("timeField");

    if(StringUtils.isNotBlank(timeField) && timeField.trim().equalsIgnoreCase("inventoryVestDate")){
      timeField = TxnConstant.PURCHASE_ORDER_FIELD_INVENTORY_VEST_DATE;
    }else{
      timeField = TxnConstant.PURCHASE_ORDER_FIELD_CREATED;
    }
    // 默认状态 allTodo
    if(StringUtil.isEmpty(orderStatus)){
      orderStatus = "allTodo";
    }
    model.addAttribute("startTimeStr",StringUtil.isEmpty(startTimeStr)?"":startTimeStr);
    model.addAttribute("endTimeStr",StringUtil.isEmpty(endTimeStr)?"":endTimeStr);
    model.addAttribute("supplierName",StringUtil.isEmpty(supplierName)?"":supplierName);
    model.addAttribute("receiptNo",StringUtil.isEmpty(receiptNo)?"":receiptNo);
    model.addAttribute("orderStatus",orderStatus);

    int pageSize = 5;
    int currentPage = NumberUtil.intValue(request.getParameter("pageNo"), 1);
    Pager pager = new Pager(0, currentPage, pageSize);
    //日期转换Long
    Long startTime = null;
    Long endTime = null;
    try{
      if(!StringUtil.isEmpty(startTimeStr)){
        //开始时间按该天0点算，左闭右开
        startTime = DateUtil.convertDateStringToDateLong("yyyy-MM-dd",startTimeStr);
      }
      if(!StringUtil.isEmpty(endTimeStr)){
        //结束时间按第二天的0点算，左闭右开
        endTime = DateUtil.convertDateStringToDateLong("yyyy-MM-dd",endTimeStr);
        endTime = endTime + 24*3600*1000 - 1;
      }

      //根据供应商名称模糊匹配出所有供应商的idList
      List<Long> fuzzySupplierIdList = null;
      if(!StringUtil.isEmpty(supplierName)){
        fuzzySupplierIdList = userService.getSupplierIdsByNameWithFuzzyQuery(shopId,supplierName);
      }
      //查出本店全部关联供应商的List
      List<Long> relatedSupplierIdList = userService.getRelatedSupplierIdListByShopId(shopId);
      //封装最终的supplierIdList
      List<Long> supplierIdList = null;
      //填了供应商，以模糊匹配结果为准
      if(!StringUtil.isEmpty(supplierName)){
        //模糊匹配有结果
        if(CollectionUtils.isNotEmpty(fuzzySupplierIdList)){
          //存在关联供应商，需要取交集
          if(CollectionUtils.isNotEmpty(relatedSupplierIdList)){
            supplierIdList = new ArrayList<Long>();
            for(int i=0;i<fuzzySupplierIdList.size();i++){
              if(relatedSupplierIdList.contains(fuzzySupplierIdList.get(i))){
                supplierIdList.add(fuzzySupplierIdList.get(i));
              }
            }
          }else{
            //不存在关联供应商
            supplierIdList = null;
          }
        }else{
          //模糊匹配无结果
          fuzzySupplierIdList = new ArrayList<Long>();
          fuzzySupplierIdList.add(-1l);
          supplierIdList = fuzzySupplierIdList;
        }
      }else{
        //没填供应商，以关联结果为准
        supplierIdList = relatedSupplierIdList;
      }

      //条件查询全部单据的数量
      Long totalCount = txnService.getTodoPurchaseOrderCount(shopId,startTime,endTime,supplierIdList,receiptNo,orderStatus, timeField);

      //组装分页对象
      pager = new Pager(totalCount.intValue(), NumberUtil.intValue(request.getParameter("pageNo"), 1), pager.getPageSize());
      request.setAttribute("pager", pager);
      request.setAttribute("pageNo", pager.getCurrentPage());
      request.setAttribute("pageCount", pager.getTotalPage());

      //采购单List
      List<PurchaseOrderDTO> orderDTOList = txnService.getTodoPurchaseOrderDTOListByCondition(shopId,startTime,endTime,supplierIdList,receiptNo,orderStatus,pager, timeField);

      //入库单ID
      Map<Long,PurchaseInventoryDTO> purchaseInventoryOrderMap = new HashMap<Long,PurchaseInventoryDTO>();

      //根据单据ID获取item，并组装到DTO
      if(CollectionUtils.isNotEmpty(orderDTOList)){
        for(PurchaseOrderDTO purchaseOrderDTO : orderDTOList ){
          //组装卖方的缺料信息，不具体到item
          SalesOrderDTO salesOrderDTO = txnService.getSalesOrderByPurchaseOrderId(purchaseOrderDTO.getId(),purchaseOrderDTO.getSupplierShopId());
          if(salesOrderDTO!=null){
            SalesOrderItemDTO[] salesOrderItemDTOs = salesOrderDTO.getItemDTOs();
            if(salesOrderItemDTOs!=null && salesOrderItemDTOs.length>0){
              for(int k=0;k<salesOrderItemDTOs.length;k++){
                if(salesOrderItemDTOs[k].getReserved()<salesOrderItemDTOs[k].getAmount()){
                  purchaseOrderDTO.setShortage(true);
                  break;
                }
              }
            }
          }
          for (PurchaseOrderItemDTO purchaseOrderItemDTO : purchaseOrderDTO.getItemDTOs()) {
            Set<Long> ids = purchaseOrderItemDTO.getPromotionsIds();
            if (CollectionUtil.isNotEmpty(ids)) {
              Long[] promotionsIds = ids.toArray(new Long[ids.size()]);
              purchaseOrderItemDTO.setPromotionsDTOs(promotionsService.getPromotionsDTODetailById(purchaseOrderDTO.getSupplierShopId(), promotionsIds));
            }
          }

          //组装已入库的单据号
          if(purchaseOrderDTO.getStatus().equals(OrderStatus.PURCHASE_ORDER_DONE)){
            PurchaseInventoryDTO purchaseInventoryDTO = txnService.getPurchaseInventoryIdByPurchaseOrderId(shopId,purchaseOrderDTO.getId());
            purchaseInventoryOrderMap.put(purchaseOrderDTO.getId(),purchaseInventoryDTO);
          }
        }
      }

      //待办采购单的状态
      List<OrderStatus> statusList = new ArrayList<OrderStatus>();
      statusList.add(OrderStatus.SELLER_PENDING);
      statusList.add(OrderStatus.SELLER_STOCK);
      statusList.add(OrderStatus.SELLER_DISPATCH);
      statusList.add(OrderStatus.SELLER_REFUSED);
      statusList.add(OrderStatus.PURCHASE_SELLER_STOP);
      statusList.add(OrderStatus.PURCHASE_ORDER_DONE);
      statusList.add(OrderStatus.PURCHASE_ORDER_REPEAL);

      model.addAttribute("statusList",statusList);
      model.addAttribute("orderDTOList",orderDTOList);
      model.addAttribute("purchaseInventoryOrderMap",purchaseInventoryOrderMap);

      //今天的时间段
      Long timePoint = null;
      try{
        timePoint = DateUtil.getToday(DateUtil.YEAR_MONTH_DATE, new Date());
      }catch (Exception e){
        LOG.error(e.getMessage(),e);
      }
      //今日新增
      Long purchase_today_new = txnService.getTodoPurchaseOrderCount(shopId,timePoint,null,relatedSupplierIdList,null,OrderStatus.SELLER_PENDING.toString(), TxnConstant.PURCHASE_ORDER_FIELD_CREATED);
      //往日新增
      Long purchase_early_new = txnService.getTodoPurchaseOrderCount(shopId,null,timePoint,relatedSupplierIdList,null,OrderStatus.SELLER_PENDING.toString(), TxnConstant.PURCHASE_ORDER_FIELD_CREATED);
      //新增总数
      Long purchase_new = purchase_today_new + purchase_early_new;
      //卖家备货中
      Long purchase_seller_stock = txnService.getTodoPurchaseOrderCount(shopId,null,null,relatedSupplierIdList,null,OrderStatus.SELLER_STOCK.toString(), TxnConstant.PURCHASE_ORDER_FIELD_CREATED);
      //卖家发货中
      Long purchase_seller_dispatch = txnService.getTodoPurchaseOrderCount(shopId,null,null,relatedSupplierIdList,null,OrderStatus.SELLER_DISPATCH.toString(), TxnConstant.PURCHASE_ORDER_FIELD_CREATED);
      //卖家已拒绝
      Long purchase_seller_refused = txnService.getTodoPurchaseOrderCount(shopId,null,null,relatedSupplierIdList,null,OrderStatus.SELLER_REFUSED.toString(), TxnConstant.PURCHASE_ORDER_FIELD_CREATED);
      //卖家终止交易
      Long purchase_seller_stop = txnService.getTodoPurchaseOrderCount(shopId,null,null,relatedSupplierIdList,null,OrderStatus.PURCHASE_SELLER_STOP.toString(), TxnConstant.PURCHASE_ORDER_FIELD_CREATED);
      //处理中（各个待处理状态的总和）
      Long purchase_in_progress = purchase_seller_stock + purchase_seller_dispatch + purchase_seller_refused + purchase_seller_stop;
      //今日入库
      Long purchase_today_done = txnService.getTodoPurchaseOrderCount(shopId,timePoint,null,relatedSupplierIdList,null,OrderStatus.PURCHASE_ORDER_DONE.toString(), TxnConstant.PURCHASE_ORDER_FIELD_INVENTORY_VEST_DATE);
      //往日入库
      Long purchase_early_done = txnService.getTodoPurchaseOrderCount(shopId,null,timePoint,relatedSupplierIdList,null,OrderStatus.PURCHASE_ORDER_DONE.toString(), TxnConstant.PURCHASE_ORDER_FIELD_INVENTORY_VEST_DATE);
      //入库总数
      Long purchase_done = purchase_today_done + purchase_early_done;
      request.setAttribute("purchase_today_new",purchase_today_new);
      request.setAttribute("purchase_early_new",purchase_early_new);
      request.setAttribute("purchase_new",purchase_new);
      request.setAttribute("purchase_seller_stock",purchase_seller_stock);
      request.setAttribute("purchase_seller_dispatch",purchase_seller_dispatch);
      request.setAttribute("purchase_seller_refused",purchase_seller_refused);
      request.setAttribute("purchase_seller_stop",purchase_seller_stop);
      request.setAttribute("purchase_in_progress",purchase_in_progress);
      request.setAttribute("purchase_today_done",purchase_today_done);
      request.setAttribute("purchase_early_done",purchase_early_done);
      request.setAttribute("purchase_done",purchase_done);

      //返回前，更新缓存中的数值
      txnService.updateTodoOrderCountInMemcacheByTypeAndShopId(RemindEventType.TODO_PURCHASE_ORDER, shopId, relatedSupplierIdList);
    }catch (Exception e){
      LOG.error(e.getMessage(),e);
    }

    return "autoaccessoryonline/ordercenter/todoPurchaseOrders";
  }

  private String toMyPurchaseOrdersPage(HttpServletRequest request, ModelMap model){
    OrderSearchConditionDTO orderSearchConditionDTO = new OrderSearchConditionDTO();
    String orderStatus = request.getParameter("orderStatus");
    String endTimeStr = "", startTimeStr = "",inventoryVestStartDateStr = "",inventoryVestEndDateStr = "";
    if ("inventoryVestDate".equals(request.getParameter("timeField"))) {
      inventoryVestEndDateStr = request.getParameter("endTimeStr");
      inventoryVestStartDateStr = request.getParameter("startTimeStr");
    } else {
      endTimeStr = request.getParameter("endTimeStr");
      startTimeStr = request.getParameter("startTimeStr");
    }
    if("inProgress".equals(orderStatus)){
      orderStatus = "SELLER_STOCK,SELLER_DISPATCH,PURCHASE_SELLER_STOP,SELLER_REFUSED";
    }

    model.addAttribute("orderStatus",orderStatus);
    model.addAttribute("endTimeStr",endTimeStr);
    model.addAttribute("startTimeStr",startTimeStr);
    model.addAttribute("inventoryVestStartDateStr",inventoryVestStartDateStr);
    model.addAttribute("inventoryVestEndDateStr",inventoryVestEndDateStr);
//    if(order)
////    orderCenter.do?method=getTodoOrders&type=TODO_PURCHASE_ORDERS&orderStatus=SELLER_PENDING
//    orderCenter.do?method=getTodoOrders&type=TODO_PURCHASE_ORDERS&orderStatus=SELLER_PENDING&startTimeStr=2013-08-28
//    orderCenter.do?method=getTodoOrders&type=TODO_PURCHASE_ORDERS&orderStatus=SELLER_PENDING&endTimeStr=2013-08-27
//    orderCenter.do?method=getTodoOrders&type=TODO_PURCHASE_ORDERS&orderStatus=inProgress
//    orderCenter.do?method=getTodoOrders&type=TODO_PURCHASE_ORDERS&orderStatus=SELLER_STOCK
//    orderCenter.do?method=getTodoOrders&type=TODO_PURCHASE_ORDERS&orderStatus=SELLER_DISPATCH
//    orderCenter.do?method=getTodoOrders&type=TODO_PURCHASE_ORDERS&orderStatus=PURCHASE_SELLER_STOP
//    orderCenter.do?method=getTodoOrders&type=TODO_PURCHASE_ORDERS&orderStatus=SELLER_REFUSED
//    orderCenter.do?method=getTodoOrders&type=TODO_PURCHASE_ORDERS&orderStatus=PURCHASE_ORDER_DONE
//    orderCenter.do?method=getTodoOrders&type=TODO_PURCHASE_ORDERS&orderStatus=PURCHASE_ORDER_DONE&startTimeStr=2013-08-28&timeField=inventoryVestDate
//    orderCenter.do?method=getTodoOrders&type=TODO_PURCHASE_ORDERS&orderStatus=PURCHASE_ORDER_DONE&endTimeStr=2013-08-27&timeField=inventoryVestDate

    return "autoaccessoryonline/ordercenter/my_purchase_orders";
  }

  private String toMySaleOrdersPage(HttpServletRequest request, ModelMap model) throws Exception{
    String currTab = request.getParameter("currTab");
    String orderStatus = request.getParameter("orderStatus");
    String endTimeStr = request.getParameter("endTimeStr");
    String startTimeStr = request.getParameter("startTimeStr");
    String todayDateStr = DateUtil.getTodayStr(DateUtil.DATE_STRING_FORMAT_DAY);
    String yesterdayDateStr = "";
    try {
      yesterdayDateStr = DateUtil.format(DateUtil.DATE_STRING_FORMAT_DAY, DateUtil.getYesterday().getTime());
    } catch (Exception e) {
        LOG.error(e.getMessage(),e);
    }

    model.addAttribute("todayDateStr",todayDateStr);
    model.addAttribute("yesterdayDateStr",yesterdayDateStr);
    model.addAttribute("orderStatus",orderStatus);
    model.addAttribute("currTab",currTab);
    model.addAttribute("endTimeStr",endTimeStr);
    model.addAttribute("startTimeStr",startTimeStr);
    if(BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(WebUtil.getShopVersionId(request))){
      IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
      List<StoreHouseDTO> storeHouseDTOList = storeHouseService.getAllStoreHousesByShopId(WebUtil.getShopId(request));
      model.addAttribute("storeHouseDTOList", storeHouseDTOList);//select 选项
    }
    return "autoaccessoryonline/ordercenter/my_sale_orders";
  }

  @RequestMapping(params = "method=getOnlinePurchaseOrders")
  @ResponseBody
  public Object getOnlinePurchaseOrders(HttpServletRequest request, ModelMap model, HttpServletResponse response, OrderSearchConditionDTO orderSearchConditionDTO) {
    IUserService userService = ServiceManager.getService(IUserService.class);
    ISearchOrderService searchOrderService = ServiceManager.getService(ISearchOrderService.class);
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    RFITxnService rfiTxnService = ServiceManager.getService(RFITxnService.class);
    IGoodBuyService goodBuyService = ServiceManager.getService(IGoodBuyService.class);
    IPromotionsService promotionsService = ServiceManager.getService(IPromotionsService.class);
    ISupplierCommentService supplierCommentService = ServiceManager.getService(ISupplierCommentService.class);
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    PagingListResult<PurchaseOrderDTO> result = new PagingListResult<PurchaseOrderDTO>();
    try {
      //数据校验
      Long shopId = WebUtil.getShopId(request);
      if (orderSearchConditionDTO == null) throw new Exception("OrderSearchConditionDTO can't be null.");
      if (shopId == null) throw new Exception("shopId can't be null.");
      //校验时间
      orderSearchConditionDTO.verificationQueryTime();
      orderSearchConditionDTO.setShopId(shopId);
      orderSearchConditionDTO.setSearchStrategy(new String[]{OrderSearchConditionDTO.SEARCHSTRATEGY_ONLINE_ORDERS});


      orderSearchConditionDTO.setOrderType(new String[]{OrderTypes.PURCHASE.toString()});
      if (StringUtils.isBlank(orderSearchConditionDTO.getSort())) {
        orderSearchConditionDTO.setSort("created_time desc");
      }
      orderSearchConditionDTO.setPageRows(5);
      orderSearchConditionDTO.setRowStart((orderSearchConditionDTO.getStartPageNo() - 1) * orderSearchConditionDTO.getMaxRows());
      OrderSearchResultListDTO orderSearchResultListDTO = searchOrderService.queryOrders(orderSearchConditionDTO);

      Set<Long> supplierIds = new HashSet<Long>();
      List<PurchaseOrderDTO> purchaseOrderDTOs = new ArrayList<PurchaseOrderDTO>();
      Map<String,PurchaseInventoryDTO> purchaseInventoryOrderMap = new HashMap<String, PurchaseInventoryDTO>();
      Map<String,SupplierDTO> supplierDTOMap = new HashMap<String, SupplierDTO>();
      Map<String,ShopDTO> shopDTOMap = new HashMap<String, ShopDTO>();

      if (CollectionUtils.isNotEmpty(orderSearchResultListDTO.getOrders())) {
        for (OrderSearchResultDTO order : orderSearchResultListDTO.getOrders()) {
          if (OrderTypes.PURCHASE.toString().equals(order.getOrderType())) {
            PurchaseOrderDTO purchaseOrderDTO = rfiTxnService.getPurchaseOrderDTOById(order.getOrderId(), shopId);
            if (purchaseOrderDTO != null) {
              //组装促销信息
              for (PurchaseOrderItemDTO purchaseOrderItemDTO : purchaseOrderDTO.getItemDTOs()) {
                Set<Long> ids = purchaseOrderItemDTO.getPromotionsIds();
                if (CollectionUtil.isNotEmpty(ids)) {
                  Long[] promotionsIds = ids.toArray(new Long[ids.size()]);
                  purchaseOrderItemDTO.setPromotionsDTOs(promotionsService.getPromotionsDTODetailById(purchaseOrderDTO.getSupplierShopId(), promotionsIds));
                }
              }


              //组装卖方的缺料信息，不具体到item
              SalesOrderDTO salesOrderDTO = txnService.getSalesOrderByPurchaseOrderId(purchaseOrderDTO.getId(), purchaseOrderDTO.getSupplierShopId());
              if (salesOrderDTO != null) {
                SalesOrderItemDTO[] salesOrderItemDTOs = salesOrderDTO.getItemDTOs();
                Set<Long> supplierProductIds = salesOrderDTO.getProductIdSet();
                Long storeHouseId = salesOrderDTO.getStorehouseId();

                if (salesOrderItemDTOs != null && salesOrderItemDTOs.length > 0) {
                  for (int k = 0; k < salesOrderItemDTOs.length; k++) {
                    if (salesOrderItemDTOs[k].getReserved() < salesOrderItemDTOs[k].getAmount()) {
                      purchaseOrderDTO.setShortage(true);
                      break;
                    }
                  }
                }
              }


              //组装已入库的单据号
              if (purchaseOrderDTO.getStatus().equals(OrderStatus.PURCHASE_ORDER_DONE)) {
                PurchaseInventoryDTO purchaseInventoryDTO = txnService.getPurchaseInventoryIdByPurchaseOrderId(shopId, purchaseOrderDTO.getId());
                if (purchaseInventoryDTO != null && purchaseInventoryDTO.getId() != null) {
                  purchaseOrderDTO.setPurchaseInventoryId(purchaseInventoryDTO.getId());
                  purchaseOrderDTO.setPurchaseInventoryReceiptNo(purchaseInventoryDTO.getReceiptNo());
                  purchaseInventoryOrderMap.put(purchaseOrderDTO.getId().toString(), purchaseInventoryDTO);
                }
              }

              //组装供应商信息
              SupplierDTO supplierDTO = CollectionUtil.getFirst(userService.getSupplierById(shopId, purchaseOrderDTO.getSupplierId()));
              if (supplierDTO != null && supplierDTO.getId() != null) {
                supplierDTOMap.put(supplierDTO.getId().toString(), supplierDTO);
              }

              if(shopDTOMap.get(purchaseOrderDTO.getSupplierShopId().toString()) == null){
                ShopDTO shopDTO = configService.getShopById(supplierDTO.getSupplierShopId());
                if(shopDTO != null){
                  shopDTO.filterHasMobileContact();
                  shopDTOMap.put(shopDTO.getId().toString(), shopDTO);
                }
              }

              //供应商点评
              SupplierCommentRecordDTO commentRecordDTO = supplierCommentService.getCommentRecordByOrderId(purchaseOrderDTO.getId(), purchaseOrderDTO.getShopId());
              purchaseOrderDTO.setCommentRecordDTO(commentRecordDTO);

              purchaseOrderDTOs.add(purchaseOrderDTO);
            }
          }
        }
      }
      IImageService imageService = ServiceManager.getService(IImageService.class);
      List<ImageScene> imageSceneList = new ArrayList<ImageScene>();
      imageSceneList.add(ImageScene.PRODUCT_LIST_IMAGE_SMALL);
      imageService.addImageInfoHistoryToBcgogoItemDTO(imageSceneList,true,purchaseOrderDTOs.toArray(new PurchaseOrderDTO[purchaseOrderDTOs.size()]));

      Map<String,Object> resultData = new HashMap<String, Object>();
      resultData.put("purchaseInventoryOrders",purchaseInventoryOrderMap);
      resultData.put("supplierDTOs",supplierDTOMap);
      resultData.put("shopDTOs", shopDTOMap);
//      SupplierDTO supplierDTO = null;
//      if (null != orderSearchResultListDTO && null != orderSearchResultListDTO.getOrders()) {
//        for (OrderSearchResultDTO order : orderSearchResultListDTO.getOrders()) {
//          if (null == order.getCustomerOrSupplierId()) {
//            continue;
//          }
//          if (!OrderTypes.SALE.toString().equals(order.getOrderType()) &&
//              !OrderTypes.REPAIR.toString().equals(order.getOrderType()) &&
//              !OrderTypes.WASH_BEAUTY.toString().equals(order.getOrderType()) &&
//              !OrderTypes.MEMBER_BUY_CARD.toString().equals(order.getOrderType()) &&
//              !OrderTypes.MEMBER_RETURN_CARD.toString().endsWith(order.getOrderType())) {
//            supplierDTO = userService.getSupplierById(order.getCustomerOrSupplierId());
//            if (null != supplierDTO) {
//              order.setCustomerStatus(supplierDTO.getStatus());
//            }
//          } else {
//            Customer customer = userService.getCustomerByCustomerId(order.getCustomerOrSupplierId(), WebUtil.getShopId(request));
//            if (null != customer) {
//              order.setCustomerStatus(customer.getStatus());
//            }
//          }
//        }
//      }
      Pager pager = new Pager(Integer.valueOf(orderSearchResultListDTO.getNumFound() + ""), orderSearchConditionDTO.getStartPageNo(), orderSearchConditionDTO.getMaxRows());
      result.setPager(pager);
      result.setResults(purchaseOrderDTOs);
      result.setData(resultData);
      return result;
    } catch (Exception e) {
      LOG.error("orderCenter.do?method=getOnlinePurchaseOrders;userId = " + WebUtil.getUserId(request) + e.getMessage(), e);
      result.setSuccess(false);
      return result;
    }
  }

  @RequestMapping(params = "method=getOnlineSaleOrders")
  @ResponseBody
  public Object getOnlineSaleOrders(HttpServletRequest request, ModelMap model, HttpServletResponse response, OrderSearchConditionDTO orderSearchConditionDTO) {
    IUserService userService = ServiceManager.getService(IUserService.class);
    ISearchOrderService searchOrderService = ServiceManager.getService(ISearchOrderService.class);
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    IPromotionsService promotionsService = ServiceManager.getService(IPromotionsService.class);
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    PagingListResult<SalesOrderDTO> result = new PagingListResult<SalesOrderDTO>();
    try {
      //数据校验
      Long shopId = WebUtil.getShopId(request);
      if (orderSearchConditionDTO == null) throw new Exception("OrderSearchConditionDTO can't be null.");
      if (shopId == null) throw new Exception("shopId can't be null.");
      //校验时间
      orderSearchConditionDTO.verificationQueryTime();
      orderSearchConditionDTO.setShopId(shopId);
      orderSearchConditionDTO.setSearchStrategy(new String[]{OrderSearchConditionDTO.SEARCHSTRATEGY_ONLINE_ORDERS});
      orderSearchConditionDTO.setOrderType(new String[]{OrderTypes.SALE.toString()});
      if (StringUtils.isBlank(orderSearchConditionDTO.getSort())) {
        orderSearchConditionDTO.setSort("created_time desc");
      }
      orderSearchConditionDTO.setPageRows(5);
      orderSearchConditionDTO.setRowStart((orderSearchConditionDTO.getStartPageNo() - 1) * orderSearchConditionDTO.getPageRows());
      if(!ArrayUtils.isEmpty(orderSearchConditionDTO.getOrderStatus()) && orderSearchConditionDTO.getOrderStatus().length == 1){
        if(orderSearchConditionDTO.getOrderStatus()[0].equals("SALE_DEBT_DONE")){
          orderSearchConditionDTO.setOrderStatus(new String[]{"SALE_DONE"});
          orderSearchConditionDTO.setNotPaid(true);
        }else if(orderSearchConditionDTO.getOrderStatus()[0].equals("SALE_DONE")){
          orderSearchConditionDTO.setNotPaid(false);
        }
      }
      OrderSearchResultListDTO orderSearchResultListDTO = searchOrderService.queryOrders(orderSearchConditionDTO);

      Set<Long> customerIds = new HashSet<Long>();
      Set<Long> customerProductIds = new HashSet<Long>();
      List<SalesOrderDTO> salesOrderDTOs = new ArrayList<SalesOrderDTO>();
      Map<String,CustomerDTO> customerDTOMap = new HashMap<String, CustomerDTO>();
      Map<String, ShopDTO> shopDTOMap = new HashMap<String, ShopDTO>();

      if (CollectionUtils.isNotEmpty(orderSearchResultListDTO.getOrders())) {
        for (OrderSearchResultDTO order : orderSearchResultListDTO.getOrders()) {
          if (OrderTypes.SALE.toString().equals(order.getOrderType())) {
            Map<Long,InventoryDTO> inventoryDTOMap = null;
            SalesOrderDTO salesOrderDTO = txnService.getSalesOrder(order.getOrderId(), shopId);
            if (salesOrderDTO != null) {
              if(salesOrderDTO.getPurchaseOrderId() != null) {
                //得到总的促销信息
                PurchaseOrderDTO purchaseOrderDTO = txnService.getPurchaseOrderById(salesOrderDTO.getPurchaseOrderId());
                if(purchaseOrderDTO != null) {
                  salesOrderDTO.setPromotionsInfoDTO(purchaseOrderDTO.getPromotionsInfoDTO());
                }
              }
              Set<Long> productIds =  salesOrderDTO.getProductIdSet();
              inventoryDTOMap = ServiceManager.getService(IInventoryService.class).getInventoryDTOMap(shopId, productIds);
              Map<Long,ProductDTO> productDTOMap = ServiceManager.getService(IProductService.class).getProductDTOMapByProductLocalInfoIds(shopId,productIds);
              Map<Long,ProductHistoryDTO> productHistoryDTOMap = ServiceManager.getService(IProductHistoryService.class)
                  .getProductHistoryDTOMapByProductHistoryIds(salesOrderDTO.getProductHistoryIds());
              for (SalesOrderItemDTO salesOrderItemDTO : salesOrderDTO.getItemDTOs()) {
                //组装销售产品信息
                ProductDTO productDTO = productDTOMap.get(salesOrderItemDTO.getProductId());
                ProductHistoryDTO productHistoryDTO = productHistoryDTOMap.get(salesOrderItemDTO.getProductHistoryId());
                if (productHistoryDTO != null) {
                  salesOrderItemDTO.setProductHistoryDTO(productHistoryDTO);

                } else {
                  salesOrderItemDTO.setProductDTOWithOutUnit(productDTO);
                }
                //组装促销信息
                Set<Long> ids = salesOrderItemDTO.getPromotionsIds();
                if (CollectionUtil.isNotEmpty(ids)) {
                  Long[] promotionsIds = ids.toArray(new Long[ids.size()]);
                  salesOrderItemDTO.setPromotionsDTOs(promotionsService.getPromotionsDTODetailById(shopId, promotionsIds));
                }
                //组装缺料信息，具体到item
                if(inventoryDTOMap != null && inventoryDTOMap.size() > 0) {
                  InventoryDTO inventoryDTO = inventoryDTOMap.get(salesOrderItemDTO.getProductId());
                  Double shortageAmount= 0.0;
                  if(inventoryDTO != null) {
                    shortageAmount = NumberUtil.subtraction(salesOrderItemDTO.getAmount(),salesOrderItemDTO.getReserved(),inventoryDTO.getAmount());
                  }
                  if(shortageAmount>0){
                    salesOrderDTO.setShortage(true);
                    salesOrderItemDTO.setShortage(shortageAmount);
                  }
                }
              }

              //组装客户信息
              CustomerDTO customerDTO = userService.getCustomerById(salesOrderDTO.getCustomerId());
              if (customerDTO != null && customerDTO.getId() != null) {
                customerDTOMap.put(customerDTO.getId().toString(), customerDTO);
                if(customerDTO.getCustomerShopId() != null && shopDTOMap.get(customerDTO.getCustomerShopId().toString()) == null){
                  ShopDTO shopDTO = configService.getShopById(customerDTO.getCustomerShopId());
                  if(shopDTO != null){
                    shopDTO.filterHasMobileContact();
                    shopDTOMap.put(shopDTO.getId().toString(), shopDTO);
                  }
                }
              }
              if(order.getNotPaid() != null && order.getNotPaid().equals(true) && salesOrderDTO.getStatus() == OrderStatus.SALE_DONE){
                salesOrderDTO.setStatus(OrderStatus.SALE_DEBT_DONE);
              }
              if(salesOrderDTO.getPurchaseOrderId() != null) {
                PurchaseOrderDTO purchaseOrderDTO = txnService.getPurchaseOrderById(salesOrderDTO.getPurchaseOrderId());
                if(purchaseOrderDTO != null) {
                  salesOrderDTO.setPurchaseVestDate(purchaseOrderDTO.getDeliveryDateStr());
                }
              }
              salesOrderDTOs.add(salesOrderDTO);
            }
          }
        }
      }
      IImageService imageService = ServiceManager.getService(IImageService.class);
      List<ImageScene> imageSceneList = new ArrayList<ImageScene>();
      imageSceneList.add(ImageScene.PRODUCT_LIST_IMAGE_SMALL);
      imageService.addImageInfoHistoryToBcgogoItemDTO(imageSceneList,true,salesOrderDTOs.toArray(new SalesOrderDTO[salesOrderDTOs.size()]));

      Map<String,Object> resultData = new HashMap<String, Object>();
      resultData.put("customerDTOs",customerDTOMap);
      resultData.put("shopDTOs", shopDTOMap);
      Pager pager = new Pager(Integer.valueOf(orderSearchResultListDTO.getNumFound() + ""), orderSearchConditionDTO.getStartPageNo(), orderSearchConditionDTO.getPageRows());
      result.setPager(pager);
      result.setResults(salesOrderDTOs);
      result.setData(resultData);
      return result;
    } catch (Exception e) {
      LOG.error("orderCenter.do?method=getOnlineSaleOrders;userId = " + WebUtil.getUserId(request) + e.getMessage(), e);
      result.setSuccess(false);
      return result;
    }
  }

  @RequestMapping(params = "method=getOnlineSaleNewOrders")
  @ResponseBody
  public Object getOnlineSaleNewOrders(HttpServletRequest request, ModelMap model, HttpServletResponse response, OrderSearchConditionDTO orderSearchConditionDTO) {
    IUserService userService = ServiceManager.getService(IUserService.class);
    ISearchOrderService searchOrderService = ServiceManager.getService(ISearchOrderService.class);
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    RFITxnService rfiTxnService = ServiceManager.getService(RFITxnService.class);
    IPromotionsService promotionsService = ServiceManager.getService(IPromotionsService.class);
    ISupplierCommentService supplierCommentService = ServiceManager.getService(ISupplierCommentService.class);
    ISearchCustomerSupplierService searchCustomerService = ServiceManager.getService(ISearchCustomerSupplierService.class);
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    PagingListResult<PurchaseOrderDTO> result = new PagingListResult<PurchaseOrderDTO>();
    CustomerSupplierSearchResultListDTO searchResultListDTO = null;
    List<Long> customerShopIds = new ArrayList<Long>();
    try {
      //数据校验
      Long shopId = WebUtil.getShopId(request);
      if (orderSearchConditionDTO == null) throw new Exception("OrderSearchConditionDTO can't be null.");
      if (shopId == null) throw new Exception("shopId can't be null.");
      //二次查询，查询对应客户的customer_shop_id
      if(ArrayUtils.isEmpty(orderSearchConditionDTO.getCustomerShopIds()) && com.bcgogo.utils.StringUtil.isNotEmpty(orderSearchConditionDTO.getCustomerOrSupplierInfo())) {
        CustomerSupplierSearchConditionDTO customerSupplierSearchConditionDTO = new CustomerSupplierSearchConditionDTO();
        customerSupplierSearchConditionDTO.setShopId(shopId);
        customerSupplierSearchConditionDTO.setSearchWord(orderSearchConditionDTO.getCustomerOrSupplierInfo());
        searchResultListDTO = searchCustomerService.queryCustomerWithUnknownField(customerSupplierSearchConditionDTO);
        if(searchResultListDTO != null && CollectionUtil.isNotEmpty(searchResultListDTO.getCustomerSuppliers())) {
          for(CustomerSupplierSearchResultDTO customerSupplierSearchResultDTO : searchResultListDTO.getCustomerSuppliers()) {
            if(StringUtils.isNotEmpty(customerSupplierSearchResultDTO.getCustomerOrSupplierShopId())) {
              customerShopIds.add(NumberUtil.toLong(customerSupplierSearchResultDTO.getCustomerOrSupplierShopId()));
            }
          }
        }
        if(CollectionUtil.isNotEmpty(customerShopIds)) {
          Long[] customerShopIdArray = new Long[customerShopIds.size()];
          customerShopIds.toArray(customerShopIdArray);
          orderSearchConditionDTO.setCustomerShopIds(customerShopIdArray);
        }
      } else {
        orderSearchConditionDTO.setExactSearch(true);
      }
      if(com.bcgogo.utils.StringUtil.isNotEmpty(orderSearchConditionDTO.getCustomerOrSupplierInfo())) {
        orderSearchConditionDTO.setShopName(orderSearchConditionDTO.getCustomerOrSupplierInfo());
      }

      ShopDTO shopDTO = ServiceManager.getService(IConfigService.class).getShopById(shopId);
      //校验时间
      orderSearchConditionDTO.verificationQueryTime();
      orderSearchConditionDTO.setSearchStrategy(new String[]{OrderSearchConditionDTO.SEARCHSTRATEGY_ONLINE_ORDERS, OrderSearchConditionDTO.SEARCHSTRATEGY_NO_SHOP_RESTRICT});
      orderSearchConditionDTO.setShopKind(shopDTO.getShopKind());
      orderSearchConditionDTO.setCustomerOrSupplierShopIds(new String[]{shopId.toString()});
      orderSearchConditionDTO.setOrderType(new String[]{OrderTypes.PURCHASE.toString()});
      if (StringUtils.isBlank(orderSearchConditionDTO.getSort())) {
        orderSearchConditionDTO.setSort("created_time desc");
      }
      orderSearchConditionDTO.setPageRows(5);
      orderSearchConditionDTO.setRowStart((orderSearchConditionDTO.getStartPageNo() - 1) * orderSearchConditionDTO.getPageRows());
      OrderSearchResultListDTO orderSearchResultListDTO = searchOrderService.queryOrders(orderSearchConditionDTO);
      List<PurchaseOrderDTO> purchaseOrderDTOs = new ArrayList<PurchaseOrderDTO>();
      Map<String,ShopDTO> shopDTOMap = new HashMap<String, ShopDTO>();
      if (CollectionUtils.isNotEmpty(orderSearchResultListDTO.getOrders())) {
        for (OrderSearchResultDTO order : orderSearchResultListDTO.getOrders()) {
          if (OrderTypes.PURCHASE.toString().equals(order.getOrderType())) {
            PurchaseOrderDTO purchaseOrderDTO = rfiTxnService.getPurchaseOrderDTOById(order.getOrderId(), order.getShopId());
            if (purchaseOrderDTO != null) {
              for (PurchaseOrderItemDTO purchaseOrderItemDTO : purchaseOrderDTO.getItemDTOs()) {
                //组装促销信息
                Set<Long> ids = purchaseOrderItemDTO.getPromotionsIds();
                if (CollectionUtil.isNotEmpty(ids)) {
                  Long[] promotionsIds = ids.toArray(new Long[ids.size()]);
                  purchaseOrderItemDTO.setPromotionsDTOs(promotionsService.getPromotionsDTODetailById(purchaseOrderDTO.getSupplierShopId(), promotionsIds));
                }
              }
              purchaseOrderDTO.setShopIdStr(purchaseOrderDTO.getShopId().toString());
              purchaseOrderDTOs.add(purchaseOrderDTO);
                if(shopDTOMap.get(purchaseOrderDTO.getShopId().toString()) == null){
                  ShopDTO customerShop = configService.getShopById(purchaseOrderDTO.getShopId());
                  if(customerShop != null){
                    customerShop.filterHasMobileContact();
                    shopDTOMap.put(customerShop.getId().toString(), customerShop);
                  }
                }


            }
          }
        }
      }
      IImageService imageService = ServiceManager.getService(IImageService.class);
      List<ImageScene> imageSceneList = new ArrayList<ImageScene>();
      imageSceneList.add(ImageScene.PRODUCT_LIST_IMAGE_SMALL);
      imageService.addImageInfoHistoryToBcgogoItemDTO(imageSceneList,true,purchaseOrderDTOs.toArray(new PurchaseOrderDTO[purchaseOrderDTOs.size()]));

      Map<String,Object> resultData = new HashMap<String, Object>();
      resultData.put("shopDTOs", shopDTOMap);
      Pager pager = new Pager(Integer.valueOf(orderSearchResultListDTO.getNumFound() + ""), orderSearchConditionDTO.getStartPageNo(), orderSearchConditionDTO.getPageRows());
      result.setPager(pager);
      result.setResults(purchaseOrderDTOs);
      result.setData(resultData);
      return result;
    } catch (Exception e) {
      LOG.error("orderCenter.do?method=getOnlineSaleNewOrders;userId = " + WebUtil.getUserId(request) + e.getMessage(), e);
      result.setSuccess(false);
      return result;
    }
  }

  /**
   * 异步获取代办采购单统计情况
   * @param request
   * @param model
   * @param response
   * @return
   */
  @RequestMapping(params = "method=getOnlinePurchaseOrderCount")
  @ResponseBody
  public Object getOnlinePurchaseOrderCount(HttpServletRequest request, ModelMap model,HttpServletResponse response){
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    IUserService userService = ServiceManager.getService(IUserService.class);
    Long shopId = WebUtil.getShopId(request);
     try{
        //查出本店全部关联供应商的List
//      List<Long> relatedSupplierIdList = userService.getRelatedSupplierIdListByShopId(shopId);
       List<Long> relatedSupplierIdList = null; //此处查询全部供应商的统计数据包括已删除的，所以不能用上面的方法。
      //今天的时间段
       Long  timePoint = DateUtil.getToday(DateUtil.YEAR_MONTH_DATE, new Date());
      //今日新增
      Long purchase_today_new = txnService.getTodoPurchaseOrderCount(shopId,timePoint,null,relatedSupplierIdList,null,OrderStatus.SELLER_PENDING.toString(), TxnConstant.PURCHASE_ORDER_FIELD_CREATED);
      //往日新增
      Long purchase_early_new = txnService.getTodoPurchaseOrderCount(shopId,null,timePoint,relatedSupplierIdList,null,OrderStatus.SELLER_PENDING.toString(), TxnConstant.PURCHASE_ORDER_FIELD_CREATED);
      //新增总数
      Long purchase_new = purchase_today_new + purchase_early_new;
      //卖家备货中
      Long purchase_seller_stock = txnService.getTodoPurchaseOrderCount(shopId,null,null,relatedSupplierIdList,null,OrderStatus.SELLER_STOCK.toString(), TxnConstant.PURCHASE_ORDER_FIELD_CREATED);
      //卖家发货中
      Long purchase_seller_dispatch = txnService.getTodoPurchaseOrderCount(shopId,null,null,relatedSupplierIdList,null,OrderStatus.SELLER_DISPATCH.toString(), TxnConstant.PURCHASE_ORDER_FIELD_CREATED);
      //卖家已拒绝
      Long purchase_seller_refused = txnService.getTodoPurchaseOrderCount(shopId,null,null,relatedSupplierIdList,null,OrderStatus.SELLER_REFUSED.toString(), TxnConstant.PURCHASE_ORDER_FIELD_CREATED);
      //卖家终止交易
      Long purchase_seller_stop = txnService.getTodoPurchaseOrderCount(shopId,null,null,relatedSupplierIdList,null,OrderStatus.PURCHASE_SELLER_STOP.toString(), TxnConstant.PURCHASE_ORDER_FIELD_CREATED);
      //处理中（各个待处理状态的总和）
      Long purchase_in_progress = purchase_seller_stock + purchase_seller_dispatch + purchase_seller_refused + purchase_seller_stop;
      //今日入库
      Long purchase_today_done = txnService.getTodoPurchaseOrderCount(shopId,timePoint,null,relatedSupplierIdList,null,OrderStatus.PURCHASE_ORDER_DONE.toString(), TxnConstant.PURCHASE_ORDER_FIELD_INVENTORY_VEST_DATE);
      //往日入库
      Long purchase_early_done = txnService.getTodoPurchaseOrderCount(shopId,null,timePoint,relatedSupplierIdList,null,OrderStatus.PURCHASE_ORDER_DONE.toString(), TxnConstant.PURCHASE_ORDER_FIELD_INVENTORY_VEST_DATE);
      //入库总数
      Long purchase_done = purchase_today_done + purchase_early_done;
       Map<String,Long> countMap = new HashMap<String, Long>();
       countMap.put("purchase_today_new",purchase_today_new);
       countMap.put("purchase_early_new",purchase_early_new);
       countMap.put("purchase_new",purchase_new);
       countMap.put("purchase_seller_stock",purchase_seller_stock);
       countMap.put("purchase_seller_dispatch",purchase_seller_dispatch);
       countMap.put("purchase_seller_refused",purchase_seller_refused);
       countMap.put("purchase_seller_stop",purchase_seller_stop);
       countMap.put("purchase_in_progress",purchase_in_progress);
       countMap.put("purchase_today_done",purchase_today_done);
       countMap.put("purchase_early_done",purchase_early_done);
       countMap.put("purchase_done",purchase_done);

      //返回前，更新缓存中的数值 之前是这么做的，这个没必要更新吧？  modify by qxy
//      txnService.updateTodoOrderCountInMemcacheByTypeAndShopId(RemindEventType.TODO_PURCHASE_ORDER, shopId, relatedSupplierIdList);
      return new Result("",true,countMap);
     }catch (Exception e){
      LOG.error(e.getMessage(),e);
       return new Result(false);
    }

  }

  /**
   * 异步获取代办销售单统计情况
   * @param request
   * @param model
   * @param response
   * @return
   */
  @RequestMapping(params = "method=getOnlineSaleOrderCount")
  @ResponseBody
  public Object getOnlineSaleOrderCount(HttpServletRequest request, ModelMap model,HttpServletResponse response) {
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    ICustomerService customerService = ServiceManager.getService(ICustomerService.class);
    Long shopId = WebUtil.getShopId(request);
    //今天的时间段
    Long timePoint = null;
    try{
      timePoint = DateUtil.getToday(DateUtil.YEAR_MONTH_DATE, new Date());
      //查出本店全部关联客户的List
      //List<Long> relatedCustomerIdList = customerService.getRelatedCustomerIdListByShopId(shopId);
      List<Long> relatedCustomerIdList = null;    //为了与solr查询结果一致
      //今日新增
      Long sale_today_new = txnService.getSalesNewOrderCountBySupplierShopId(shopId,timePoint,null,OrderStatus.SELLER_PENDING.toString(),TxnConstant.PURCHASE_ORDER_FIELD_CREATED);
      //往日新增
      Long sale_early_new = txnService.getSalesNewOrderCountBySupplierShopId(shopId,null,timePoint,OrderStatus.SELLER_PENDING.toString(),TxnConstant.PURCHASE_ORDER_FIELD_CREATED);
      //新增总数
      Long sale_new = sale_today_new + sale_early_new;
      //备货中
      Long sale_stocking = txnService.getTodoSalesOrderCount(shopId,null,null,relatedCustomerIdList,null,OrderStatus.STOCKING.toString());
      //已发货
      Long sale_dispatch = txnService.getTodoSalesOrderCount(shopId,null,null,relatedCustomerIdList,null,OrderStatus.DISPATCH.toString());
      //欠款结算
      Long sale_sale_debt_done = txnService.getTodoSalesOrderCount(shopId,null,null,relatedCustomerIdList,null,OrderStatus.SALE_DEBT_DONE.toString());
      //处理中（各个待处理状态的总和）
      Long sale_in_progress = sale_stocking + sale_dispatch + sale_sale_debt_done;
      Map<String,Long> countMap = new HashMap<String, Long>();
      countMap.put("sale_today_new",sale_today_new);
      countMap.put("sale_early_new",sale_early_new);
      countMap.put("sale_new",sale_new);
      countMap.put("sale_stocking",sale_stocking);
      countMap.put("sale_dispatch",sale_dispatch);
      countMap.put("sale_sale_debt_done",sale_sale_debt_done);
      countMap.put("sale_in_progress",sale_in_progress);
      return new Result("",true,countMap);
    }catch (Exception e){
      LOG.error(e.getMessage(),e);
      return new Result(false);
    }

  }

  private String getTodoPurchaseReturnOrders(HttpServletRequest request, ModelMap model) throws Exception {
    IProductService productService = ServiceManager.getService(IProductService.class);
    IUserService userService = ServiceManager.getService(IUserService.class);
    ITxnService txnService = ServiceManager.getService(ITxnService.class);

    Long shopId = WebUtil.getShopId(request);
    if(BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(WebUtil.getShopVersionId(request))){
      IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
      List<StoreHouseDTO> storeHouseDTOList = storeHouseService.getAllStoreHousesByShopId(shopId);
      model.addAttribute("storeHouseDTOList", storeHouseDTOList);//select 选项
    }
    String startTimeStr = request.getParameter("startTimeStr");
    String endTimeStr = request.getParameter("endTimeStr");
    String supplierName = request.getParameter("supplierName");
    String receiptNo = request.getParameter("receiptNo");
    String orderStatus = request.getParameter("orderStatus");
    // 默认状态 allTodo
    if(StringUtil.isEmpty(orderStatus)){
      orderStatus = "allTodo";
    }
    model.addAttribute("startTimeStr",StringUtil.isEmpty(startTimeStr)?"":startTimeStr);
    model.addAttribute("endTimeStr",StringUtil.isEmpty(endTimeStr)?"":endTimeStr);
    model.addAttribute("supplierName",StringUtil.isEmpty(supplierName)?"":supplierName);
    model.addAttribute("receiptNo",StringUtil.isEmpty(receiptNo)?"":receiptNo);
    model.addAttribute("orderStatus",orderStatus);

    int pageSize = 5;
    int currentPage = NumberUtil.intValue(request.getParameter("pageNo"), 1);
    Pager pager = new Pager(0, currentPage, pageSize);
    //日期转换Long
    Long startTime = null;
    Long endTime = null;
    try{
      if(!StringUtil.isEmpty(startTimeStr)){
        //开始时间按该天0点算，左闭右开
        startTime = DateUtil.convertDateStringToDateLong("yyyy-MM-dd",startTimeStr);
      }
      if(!StringUtil.isEmpty(endTimeStr)){
        //结束时间按第二天的0点算，左闭右开
        endTime = DateUtil.convertDateStringToDateLong("yyyy-MM-dd",endTimeStr);
        endTime = endTime + 24*3600*1000 - 1;
      }

      //根据供应商名称模糊匹配出所有供应商的idList
      List<Long> fuzzySupplierIdList = null;
      if(!StringUtil.isEmpty(supplierName)){
        fuzzySupplierIdList = userService.getSupplierIdsByNameWithFuzzyQuery(shopId,supplierName);
      }
      //查出本店全部关联供应商的List
      List<Long> relatedSupplierIdList = userService.getRelatedSupplierIdListByShopId(shopId);
      //封装最终的supplierIdList
      List<Long> supplierIdList = null;
      //填了供应商，以模糊匹配结果为准
      if(!StringUtil.isEmpty(supplierName)){
        //模糊匹配有结果
        if(CollectionUtils.isNotEmpty(fuzzySupplierIdList)){
          //存在关联供应商，需要取交集
          if(CollectionUtils.isNotEmpty(relatedSupplierIdList)){
            supplierIdList = new ArrayList<Long>();
            for(int i=0;i<fuzzySupplierIdList.size();i++){
              if(relatedSupplierIdList.contains(fuzzySupplierIdList.get(i))){
                supplierIdList.add(fuzzySupplierIdList.get(i));
              }
            }
          }else{
            //不存在关联供应商
            supplierIdList = null;
          }
        }else{
          //模糊匹配无结果
          fuzzySupplierIdList = new ArrayList<Long>();
          fuzzySupplierIdList.add(-1l);
          supplierIdList = fuzzySupplierIdList;
        }
      }else{
        //没填供应商，以关联结果为准
        supplierIdList = relatedSupplierIdList;
      }

      //条件查询全部单据的数量
      Long totalCount = txnService.getTodoPurchaseReturnOrderCount(shopId,startTime,endTime,supplierIdList,receiptNo,orderStatus);

      //组装分页对象
      pager = new Pager(totalCount.intValue(), NumberUtil.intValue(request.getParameter("pageNo"), 1), pager.getPageSize());
      request.setAttribute("pager", pager);
      request.setAttribute("pageNo", pager.getCurrentPage());
      request.setAttribute("pageCount", pager.getTotalPage());

      //采购退货单List
      List<PurchaseReturnDTO> orderDTOList = txnService.getTodoPurchaseReturnOrderDTOListByCondition(shopId,startTime,endTime,supplierIdList,receiptNo,orderStatus,pager);

      //六属性
      List<Map<Long,ProductDTO>> productDTOMapList = new ArrayList<Map<Long,ProductDTO>>();

      //根据单据ID获取item，并组装到DTO
      if(totalCount>0){
        for(int i=0;i<orderDTOList.size();i++){
          //组装item
          List<PurchaseReturnItemDTO> itemList = txnService.getPurchaseReturnItemDTOs(orderDTOList.get(i).getId());
          PurchaseReturnItemDTO[] itemDTOs = new PurchaseReturnItemDTO[itemList.size()];
          //组装六属性
          Map<Long,ProductDTO> productDTOMap = new HashMap<Long,ProductDTO>();
          for(int j=0;j<itemList.size();j++){
            itemDTOs[j] = itemList.get(j);
            //组装商品信息
            ProductDTO productDTO = productService.getProductByProductLocalInfoId(itemDTOs[j].getProductId(),shopId);
            //获取六属性
            productDTOMap.put(itemDTOs[j].getProductId(),productDTO);
          }
          orderDTOList.get(i).setItemDTOs(itemDTOs);
          //组装供应商名称
          orderDTOList.get(i).setSupplier(userService.getSupplierById(orderDTOList.get(i).getSupplierId()).getName());
          productDTOMapList.add(productDTOMap);
        }
      }

      //待办入库退货单的状态
      List<OrderStatus> statusList = new ArrayList<OrderStatus>();
      statusList.add(OrderStatus.SELLER_PENDING);
      statusList.add(OrderStatus.SELLER_ACCEPTED);
      statusList.add(OrderStatus.SELLER_REFUSED);
      statusList.add(OrderStatus.SETTLED);


      model.addAttribute("statusList",statusList);
      model.addAttribute("orderDTOList",orderDTOList);
      model.addAttribute("productDTOMapList",productDTOMapList);

      //今天的时间段
      Long timePoint = null;
      try{
        timePoint = DateUtil.getToday(DateUtil.YEAR_MONTH_DATE, new Date());
      }catch (Exception e){
        LOG.error(e.getMessage(),e);
      }
      //今日新增
      Long purchase_return_today_new = txnService.getTodoPurchaseReturnOrderCount(shopId,timePoint,null,relatedSupplierIdList,null,OrderStatus.SELLER_PENDING.toString());
      //往日新增
      Long purchase_return_early_new = txnService.getTodoPurchaseReturnOrderCount(shopId,null,timePoint,relatedSupplierIdList,null,OrderStatus.SELLER_PENDING.toString());
      //新增总数
      Long purchase_return_new = purchase_return_today_new + purchase_return_early_new;
      //卖家已接受
      Long purchase_return_seller_accept = txnService.getTodoPurchaseReturnOrderCount(shopId,null,null,relatedSupplierIdList,null,OrderStatus.SELLER_ACCEPTED.toString());
      //卖家已拒绝
      Long purchase_return_seller_refused = txnService.getTodoPurchaseReturnOrderCount(shopId,null,null,relatedSupplierIdList,null,OrderStatus.SELLER_REFUSED.toString());
      //处理中（各个待处理状态的总和）
      Long purchase_return_in_progress = purchase_return_seller_accept + purchase_return_seller_refused;
      request.setAttribute("purchase_return_today_new",purchase_return_today_new);
      request.setAttribute("purchase_return_early_new",purchase_return_early_new);
      request.setAttribute("purchase_return_new",purchase_return_new);
      request.setAttribute("purchase_return_seller_accept",purchase_return_seller_accept);
      request.setAttribute("purchase_return_seller_refused",purchase_return_seller_refused);
      request.setAttribute("purchase_return_in_progress",purchase_return_in_progress);

      //返回前，更新缓存中的数值
      txnService.updateTodoOrderCountInMemcacheByTypeAndShopId(RemindEventType.TODO_PURCHASE_ORDER, shopId, relatedSupplierIdList);
    }catch (Exception e){
      LOG.error(e.getMessage(),e);
    }
    return "autoaccessoryonline/ordercenter/todoPurchaseReturnOrders";
  }



}
