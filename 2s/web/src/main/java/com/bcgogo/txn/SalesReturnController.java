package com.bcgogo.txn;

import com.bcgogo.common.Result;
import com.bcgogo.common.WebUtil;
import com.bcgogo.config.cache.BcgogoConcurrentController;
import com.bcgogo.config.dto.OperationLogDTO;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.service.IOperationLogService;
import com.bcgogo.enums.*;
import com.bcgogo.enums.txn.pushMessage.PushMessageSourceType;
import com.bcgogo.notification.service.SmsService;
import com.bcgogo.product.cache.ProductUnitCache;
import com.bcgogo.product.service.IProductService;
import com.bcgogo.search.service.CurrentUsed.IProductCurrentUsedService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.bcgogoListener.orderEvent.SalesReturnSavedEvent;
import com.bcgogo.txn.bcgogoListener.publisher.BcgogoEventPublisher;
import com.bcgogo.txn.dto.*;
import com.bcgogo.txn.dto.StatementAccount.OrderDebtType;
import com.bcgogo.txn.service.*;
import com.bcgogo.txn.service.productThrough.IProductInStorageService;
import com.bcgogo.txn.service.productThrough.IProductOutStorageService;
import com.bcgogo.txn.service.pushMessage.IPushMessageService;
import com.bcgogo.txn.service.solr.ICustomerOrSupplierSolrWriteService;
import com.bcgogo.user.dto.CustomerDTO;
import com.bcgogo.user.dto.CustomerRecordDTO;
import com.bcgogo.user.dto.MemberDTO;
import com.bcgogo.user.dto.SupplierDTO;
import com.bcgogo.user.model.Customer;
import com.bcgogo.user.model.SalesMan;
import com.bcgogo.user.service.ICustomerService;
import com.bcgogo.user.service.IMembersService;
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
@RequestMapping("/salesReturn.do")
public class SalesReturnController {
  private static final Logger LOG = LoggerFactory.getLogger(SalesReturnController.class);
  private static final String REDIRECT_SHOW = "redirect:salesReturn.do?method=showSalesReturnOrderBySalesReturnOrderId";
  @Autowired
  private ISaleReturnOrderService saleReturnOrderService;
  @Autowired
  private SmsService smsService;

	private RFITxnService rfiTxnService;

	public RFITxnService getRfiTxnService() {
		return rfiTxnService == null ? ServiceManager.getService(RFITxnService.class) : rfiTxnService;
	}

	public void setSaleReturnOrderService(ISaleReturnOrderService saleReturnOrderService) {
    this.saleReturnOrderService = saleReturnOrderService;
  }

  public void setSmsService(SmsService smsService) {
    this.smsService = smsService;
  }

  @RequestMapping(params = "method=acceptSalesReturnOrder")
  public String acceptSalesReturnOrder(ModelMap model, HttpServletRequest request, Long id, String memo) {
    ISaleReturnOrderService saleReturnOrderService = ServiceManager.getService(ISaleReturnOrderService.class);
    try {
      Long shopId = WebUtil.getShopId(request);
      Long userId = WebUtil.getUserId(request);
      String userName = WebUtil.getUserName(request);
      model.addAttribute("salesReturnOrderId", id);
      if (shopId == null || userId == null) {
        return REDIRECT_SHOW;
      }
      SalesReturnDTO salesReturnDTO = saleReturnOrderService.getSalesReturnDTOById(shopId, id);
      if (salesReturnDTO == null) {
        request.setAttribute("errorMsg", "系统出错!");
        return REDIRECT_SHOW;
      }
      if (!OrderStatus.PENDING.equals(salesReturnDTO.getStatus())) {
        LOG.error("单据状态[{}]不是PENDING，不能进行accept操作", salesReturnDTO.getStatus());
        request.setAttribute("errorMsg", "当前单据状态是" + salesReturnDTO.getStatus().getName() + "不能接受单据！");
        return REDIRECT_SHOW;
      }
      String key = "salesReturnOrder" + StringUtil.truncValue(salesReturnDTO.getId().toString()) + "purchaseReturnOrder" + StringUtil.truncValue(salesReturnDTO.getPurchaseReturnOrderId().toString()) + StringUtil.truncValue(shopId.toString());
      try {
        if (!BcgogoConcurrentController.lock(ConcurrentScene.HANDLE_PAYABLE, key)) {
          request.setAttribute("errorMsg", "当前单据正在被操作，请稍候再试！");
          return REDIRECT_SHOW;
        }
        salesReturnDTO.setUserId(userId);
        salesReturnDTO.setEditorId(userId);
        salesReturnDTO.setEditor(userName);
        salesReturnDTO.setEditDate(System.currentTimeMillis());
        salesReturnDTO.setReviewerId(userId);
        salesReturnDTO.setReviewer(userName);
        salesReturnDTO.setReviewDate(System.currentTimeMillis());
        salesReturnDTO.setMemo(memo);
        salesReturnDTO = saleReturnOrderService.acceptSalesReturnDTO(salesReturnDTO);
      } finally {
        BcgogoConcurrentController.release(ConcurrentScene.HANDLE_PAYABLE, key);
      }

      //add by WLF 更新缓存中待办销售退货单的数量
      List<Long> customerIdList = ServiceManager.getService(ICustomerService.class).getRelatedCustomerIdListByShopId(salesReturnDTO.getShopId());
      ServiceManager.getService(ITxnService.class).updateTodoOrderCountInMemcacheByTypeAndShopId(RemindEventType.TODO_SALE_RETURN_ORDER, salesReturnDTO.getShopId(), customerIdList);

      PurchaseReturnDTO purchaseReturnDTO = salesReturnDTO.getPurchaseReturnDTO();
      if (purchaseReturnDTO != null) {
        Customer customer = ServiceManager.getService(IUserService.class).getCustomerByCustomerId(salesReturnDTO.getCustomerId(), salesReturnDTO.getShopId());
        smsService.returnsAcceptedSMS(purchaseReturnDTO.getShopId(), salesReturnDTO.getShopId(), purchaseReturnDTO.getReceiptNo(), customer.getMobile());
        ServiceManager.getService(IPushMessageService.class).disabledPushMessageReceiverBySourceId(null, purchaseReturnDTO.getId(),null,PushMessageSourceType.SALE_RETURN_NEW);
      }
      //线程做orderindex
      BcgogoEventPublisher bcgogoEventPublisher = new BcgogoEventPublisher();
      SalesReturnSavedEvent salesReturnSavedEvent = new SalesReturnSavedEvent(salesReturnDTO);
      bcgogoEventPublisher.publisherSalesReturnSaved(salesReturnSavedEvent);
      request.setAttribute("UNIT_TEST", salesReturnSavedEvent); //单元测试

    } catch (Exception e) {
      LOG.debug("/salesReturn.do");
      LOG.debug("method=acceptSalesReturnOrder");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.debug(e.getMessage(), e);
      request.setAttribute("errorMsg", "系统出错！");
      return REDIRECT_SHOW;
    }
    return REDIRECT_SHOW;
  }

  @RequestMapping(params = "method=refuseSalesReturnOrder")
  public String refuseSalesReturnOrder(ModelMap model, HttpServletRequest request, Long id, String memo, String refuseReason) {
    ISaleReturnOrderService saleReturnOrderService = ServiceManager.getService(ISaleReturnOrderService.class);
    try {
      Long shopId = WebUtil.getShopId(request);
      Long userId = WebUtil.getUserId(request);
      String userName = WebUtil.getUserName(request);
      Long shopVersionId=WebUtil.getShopVersionId(request);
      model.addAttribute("salesReturnOrderId", id);
      if (shopId == null || userId == null || id == null) {
        return REDIRECT_SHOW;
      }
      SalesReturnDTO salesReturnDTO = saleReturnOrderService.getSalesReturnDTOById(shopId, id);
      if (salesReturnDTO == null) {
        request.setAttribute("errorMsg", "系统出错！");
        return REDIRECT_SHOW;
      }
      if (!OrderStatus.PENDING.equals(salesReturnDTO.getStatus())) {
        LOG.error("单据状态[{}]不是PENDING，不能进行refuse操作", salesReturnDTO.getStatus());
        request.setAttribute("errorMsg", "当前单据状态是" + salesReturnDTO.getStatus().getName() + "不能拒绝单据！");
        return REDIRECT_SHOW;
      }
      String key = "salesReturnOrder" + StringUtil.truncValue(salesReturnDTO.getId().toString()) + "purchaseReturnOrder" + StringUtil.truncValue(salesReturnDTO.getPurchaseReturnOrderId().toString()) + StringUtil.truncValue(shopId.toString());
      try {
        if (!BcgogoConcurrentController.lock(ConcurrentScene.HANDLE_PAYABLE, key)) {
          request.setAttribute("errorMsg", "当前单据正在被操作，请稍候再试！");
          return REDIRECT_SHOW;
        }
        salesReturnDTO.setUserId(userId);
        salesReturnDTO.setEditorId(userId);
        salesReturnDTO.setEditor(userName);
        salesReturnDTO.setEditDate(System.currentTimeMillis());
        salesReturnDTO.setReviewerId(userId);
        salesReturnDTO.setShopVersionId(shopVersionId);
        salesReturnDTO.setReviewer(userName);
        salesReturnDTO.setReviewDate(System.currentTimeMillis());
        salesReturnDTO.setMemo(memo);
        salesReturnDTO.setRefuseReason(refuseReason);
        salesReturnDTO = saleReturnOrderService.refuseSalesReturnDTO(salesReturnDTO);

      } finally {
        BcgogoConcurrentController.release(ConcurrentScene.HANDLE_PAYABLE, key);
      }

      //add by WLF 更新缓存中待办销售退货单的数量
      List<Long> customerIdList = ServiceManager.getService(ICustomerService.class).getRelatedCustomerIdListByShopId(salesReturnDTO.getShopId());
      ServiceManager.getService(ITxnService.class).updateTodoOrderCountInMemcacheByTypeAndShopId(RemindEventType.TODO_SALE_RETURN_ORDER, salesReturnDTO.getShopId(), customerIdList);

      PurchaseReturnDTO purchaseReturnDTO = salesReturnDTO.getPurchaseReturnDTO();
      if (purchaseReturnDTO != null) {
        Customer customer = ServiceManager.getService(IUserService.class).getCustomerByCustomerId(salesReturnDTO.getCustomerId(), salesReturnDTO.getShopId());
        smsService.returnsRefuseSMS(purchaseReturnDTO.getShopId(), salesReturnDTO.getShopId(), purchaseReturnDTO.getReceiptNo(), customer.getMobile());
        ServiceManager.getService(IPushMessageService.class).disabledPushMessageReceiverBySourceId(null, purchaseReturnDTO.getId(),null, PushMessageSourceType.SALE_RETURN_NEW);
      }

      //线程做orderindex
      BcgogoEventPublisher bcgogoEventPublisher = new BcgogoEventPublisher();
      SalesReturnSavedEvent salesReturnSavedEvent = new SalesReturnSavedEvent(salesReturnDTO);
      bcgogoEventPublisher.publisherSalesReturnSaved(salesReturnSavedEvent);
      request.setAttribute("UNIT_TEST", salesReturnSavedEvent); //单元测试

    } catch (Exception e) {
      LOG.debug("/salesReturn.do");
      LOG.debug("method=refuseSalesReturnOrder");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.debug(e.getMessage(), e);
      request.setAttribute("errorMsg", "系统出错");
      return REDIRECT_SHOW;
    }
    return REDIRECT_SHOW;
  }

  /**
   * 根据销售退货单ID显示退货记录
   *
   * @param model
   * @param request
   * @return
   * @throws Exception
   */
  @RequestMapping(params = "method=showSalesReturnOrderBySalesReturnOrderId")
  public String showSalesReturnOrderBySalesReturnOrderId(ModelMap model, HttpServletRequest request, String salesReturnOrderId) throws Exception {
    try {
      if (StringUtils.isBlank(salesReturnOrderId))
        throw new Exception("showSalesReturnOrderBySalesReturnOrderId id is empty!");
      ITxnService txnService = ServiceManager.getService(ITxnService.class);
      Long shopId = WebUtil.getShopId(request);
      Long userId = WebUtil.getUserId(request);
      String userName = WebUtil.getUserName(request);
      IUserService userService = ServiceManager.getService(IUserService.class);

      SalesReturnDTO salesReturnDTO = saleReturnOrderService.getSalesReturnDTOById(shopId, Long.valueOf(salesReturnOrderId));

      // 填充退货明细中的具体信息
      salesReturnDTO = saleReturnOrderService.fillSalesReturnItemDTOsDetailInfo(salesReturnDTO);

      ReceivableDTO receivableDTO = txnService.getReceivableByShopIdAndOrderTypeAndOrderId(shopId, OrderTypes.SALE_RETURN, salesReturnDTO.getId());
      if (receivableDTO != null) {
        salesReturnDTO.setSettledAmount(Math.abs(NumberUtil.round(receivableDTO.getSettledAmount(), NumberUtil.MONEY_PRECISION)));
        salesReturnDTO.setCashAmount(Math.abs(NumberUtil.round(receivableDTO.getCash(), NumberUtil.MONEY_PRECISION)));
        salesReturnDTO.setBankAmount(Math.abs(NumberUtil.round(receivableDTO.getBankCard(), NumberUtil.MONEY_PRECISION)));
        salesReturnDTO.setBankCheckAmount(Math.abs(NumberUtil.round(receivableDTO.getCheque(), NumberUtil.MONEY_PRECISION)));
        // add by zhuj
        salesReturnDTO.setCustomerDeposit(Math.abs(NumberUtil.round(receivableDTO.getDeposit(),NumberUtil.MONEY_PRECISION)));
        
        salesReturnDTO.setDiscountAmount(Math.abs(NumberUtil.round(receivableDTO.getDiscount(), NumberUtil.MONEY_PRECISION)));
        salesReturnDTO.setStrikeAmount(Math.abs(NumberUtil.round(receivableDTO.getStrike(), NumberUtil.MONEY_PRECISION)));
        salesReturnDTO.setAccountDebtAmount(Math.abs(NumberUtil.round(receivableDTO.getDebt(), NumberUtil.MONEY_PRECISION)));
        salesReturnDTO.setStatementAmount(Math.abs(NumberUtil.round(receivableDTO.getStatementAmount(),NumberUtil.MONEY_PRECISION)));
        if (!ArrayUtils.isEmpty(receivableDTO.getRecordDTOs())) {
          List<String> checkNos = new ArrayList<String>();
          for (ReceptionRecordDTO receptionRecordDTO : receivableDTO.getRecordDTOs()) {
            if (StringUtils.isNotBlank(receptionRecordDTO.getChequeNo())) {
              checkNos.add(receptionRecordDTO.getChequeNo());
            }
          }
          if (CollectionUtils.isNotEmpty(checkNos)) {
            salesReturnDTO.setBankCheckNo(StringUtil.arrayToStr(",", checkNos));
          }
        }
      }

      if(OrderStatus.WAITING_STORAGE.equals(salesReturnDTO.getStatus())){
        //如果不是结算状态 默认设置退货人为当前用户
        if(StringUtil.isEmpty(salesReturnDTO.getSalesReturner())){
          salesReturnDTO.setSalesReturner(userName);
          SalesMan salesMan = userService.getSalesManByName(shopId, userName);
          salesReturnDTO.setSalesReturnerId(salesMan == null ? null:salesMan.getId());
        }
        if(BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(WebUtil.getShopVersionId(request))){
          IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
          List<StoreHouseDTO> storeHouseDTOList = storeHouseService.getAllStoreHousesByShopId(shopId);
          model.addAttribute("storeHouseDTOList", storeHouseDTOList);//select 选项
          if(CollectionUtils.isNotEmpty(storeHouseDTOList) && salesReturnDTO.getStorehouseId()==null){
            if(storeHouseDTOList.size()==1){
              salesReturnDTO.setStorehouseId(storeHouseDTOList.get(0).getId());
            }
          }
        }
      }

      model.addAttribute("salesReturnDTO", salesReturnDTO);
      if(null != salesReturnDTO.getCustomerId()){
        CustomerRecordDTO customerRecordDTO = userService.getCustomerRecordDTOByCustomerIdAndShopId(shopId,salesReturnDTO.getCustomerId());
        CustomerDTO customerDTO = userService.getCustomerDTOByCustomerId(salesReturnDTO.getCustomerId(), shopId);
        model.addAttribute("customerRecordDTO",customerRecordDTO);
        model.addAttribute("customerDTO", customerDTO);
      }
      //结算信息
      List<ReceptionRecordDTO> receptionRecordDTOs = txnService.getSettledRecord(shopId, OrderTypes.SALE_RETURN, Long.valueOf(salesReturnOrderId));
      model.addAttribute("receptionRecordDTOs",receptionRecordDTOs);
      model.addAttribute("receiveNo",ServiceManager.getService(ITxnService.class).getStatementAccountOrderNo(shopId, salesReturnDTO.getStatementAccountOrderId()));
      //客户的应收应付
      ServiceManager.getService(ITxnService.class).getPayableAndReceivableToModel(model, shopId, salesReturnDTO.getCustomerId());
    } catch (Exception e) {
      LOG.error("/salesReturn.do");
      LOG.error("method=showSalesReturnOrderBySalesReturnOrderId");
      LOG.error("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
    }
    return "/txn/salesReturnFinish";

  }

  @RequestMapping(params = "method=accountDetail")
  public String accountDetail(ModelMap model, HttpServletRequest request, String customerId) {
    IUserService userService = ServiceManager.getService(IUserService.class);
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    Long shopId = null;
    Long userId = null;
    try {
      shopId = WebUtil.getShopId(request);
      userId = WebUtil.getUserId(request);

      if (StringUtil.isNotEmpty(request.getParameter("settleType")) && "normal".equals((request.getParameter("settleType")))) {
        model.addAttribute("settleType", "normal");
        if (StringUtil.isNotEmpty(customerId) && NumberUtil.isNumber(customerId)) {
          List<CustomerDTO> customerDTOs = userService.getShopCustomerById(shopId, Long.parseLong(customerId));
          CustomerDTO customer = CollectionUtil.getFirst(customerDTOs);
          if (customer == null) {
            LOG.error("/salesReturn.do?method=accountDetail 出错, customer不存在. customerID:{}", customerId);
            return "/txn/accountDetailSalesReturn";
          }
          List<String> stringList = txnService.getDebtFromReceivableByCustomerId(shopId, Long.parseLong(customerId), OrderDebtType.CUSTOMER_DEBT_RECEIVABLE, ReceivableStatus.FINISH);
          model.addAttribute("customerName", customer.getName());
          model.addAttribute("receivableTotal", CollectionUtils.isEmpty(stringList) ? 0 : NumberUtil.doubleVal(Double.valueOf(stringList.get(0))));
        // add by zhuj
        ICustomerDepositService depositService = ServiceManager.getService(ICustomerDepositService.class);
        CustomerDepositDTO customerDepositDTO = depositService.queryCustomerDepositByShopIdAndCustomerId(shopId, Long.parseLong(customerId));
        if (customerDepositDTO != null) {
          model.addAttribute("customerDepositAvailable", customerDepositDTO.getActuallyPaid());
        }
        // add end
        }

        SalesReturnDTO salesReturnDTO = new SalesReturnDTO();
        String totalStr = request.getParameter("total");
        if (NumberUtil.isNumber(totalStr)) {
          salesReturnDTO.setTotal(Double.valueOf(totalStr));
        }
        model.addAttribute("salesReturnDTO", salesReturnDTO);

      } else {
        String salesReturnIdStr = request.getParameter("salesReturnId");
        if (StringUtils.isBlank(customerId) || !NumberUtil.isNumber(customerId)) {
          LOG.error("/salesReturn.do?method=accountDetail 出错, customerId为空或非数字. customerID:{}", customerId);
          return "/txn/accountDetailSalesReturn";
        }
        List<CustomerDTO> customerDTOs = userService.getShopCustomerById(shopId, Long.parseLong(customerId));
        CustomerDTO customer = CollectionUtil.getFirst(customerDTOs);
        if (customer == null) {
          LOG.error("/salesReturn.do?method=accountDetail 出错, customer不存在. customerID:{}", customerId);
          return "/txn/accountDetailSalesReturn";
        }
        List<String> stringList = txnService.getDebtFromReceivableByCustomerId(shopId, Long.parseLong(customerId), OrderDebtType.CUSTOMER_DEBT_RECEIVABLE, ReceivableStatus.FINISH);
        model.addAttribute("customerName", customer.getName());
        model.addAttribute("receivableTotal", CollectionUtils.isEmpty(stringList) ? 0 : NumberUtil.doubleVal(Double.valueOf(stringList.get(0))));
        if (StringUtils.isNotEmpty(salesReturnIdStr) && NumberUtil.isNumber(salesReturnIdStr)) {
          SalesReturnDTO salesReturnDTO = saleReturnOrderService.getSalesReturnDTOById(shopId, Long.parseLong(salesReturnIdStr));
          if (salesReturnDTO != null) {
            model.addAttribute("salesReturnDTO", salesReturnDTO);
          }
        }
      }
    } catch (Exception e) {
      LOG.error("/salesReturn.do?method=accountDetail 出错, shopId:{}, userId:{}", shopId, userId);
      LOG.error(e.getMessage(), e);
    }

    return "/txn/accountDetailSalesReturn";
  }

  @RequestMapping(params = "method=settleForWholesaler")
  @ResponseBody
  public Result settleForWholesaler(ModelMap model, HttpServletRequest request,SalesReturnDTO settleSalesReturnDTO) {
    Long shopId = null;
    Long userId = null;
    ISaleReturnOrderService saleReturnOrderService = ServiceManager.getService(ISaleReturnOrderService.class);
    IProductCurrentUsedService productCurrentUsedService = ServiceManager.getService(IProductCurrentUsedService.class);
    IUserService userService = ServiceManager.getService(IUserService.class);
    try {
      shopId = WebUtil.getShopId(request);
      userId = WebUtil.getUserId(request);
      Long shopVersionId = WebUtil.getShopVersionId(request);
      if (shopId == null || userId == null || shopVersionId==null) {
        return new Result("数据出错，请重试。", false);
      }
      SalesReturnDTO salesReturnDTO = saleReturnOrderService.getSalesReturnDTOById(shopId, settleSalesReturnDTO.getId());
      if (salesReturnDTO == null) {
        return new Result("找不到此退货单。", false);
      }
      salesReturnDTO.setUserId(userId);
      salesReturnDTO.setUserName(WebUtil.getUserName(request));
      salesReturnDTO.setShopVersionId(shopVersionId);
      salesReturnDTO.setShopId(shopId);
      salesReturnDTO.setSettledAmount(settleSalesReturnDTO.getSettledAmount());
      salesReturnDTO.setTotalCostPrice(settleSalesReturnDTO.getTotalCostPrice());
      salesReturnDTO.setCashAmount(settleSalesReturnDTO.getCashAmount());
      salesReturnDTO.setBankCheckAmount(settleSalesReturnDTO.getBankCheckAmount());
      salesReturnDTO.setBankCheckNo(settleSalesReturnDTO.getBankCheckNo());
      salesReturnDTO.setBankAmount(settleSalesReturnDTO.getBankAmount());
      salesReturnDTO.setCustomerDeposit(settleSalesReturnDTO.getCustomerDeposit()); // add by zhuj
      salesReturnDTO.setStrikeAmount(settleSalesReturnDTO.getStrikeAmount());
      salesReturnDTO.setDiscountAmount(settleSalesReturnDTO.getDiscountAmount());
      salesReturnDTO.setSalesReturnerId(settleSalesReturnDTO.getSalesReturnerId());
      salesReturnDTO.setSalesReturner(settleSalesReturnDTO.getSalesReturner());
      salesReturnDTO.setStorehouseId(settleSalesReturnDTO.getStorehouseId());
      salesReturnDTO.setAccountDebtAmount(settleSalesReturnDTO.getAccountDebtAmount());
      SalesReturnItemDTO[] settleItemDTOs = settleSalesReturnDTO.getItemDTOs();
      if (!ArrayUtils.isEmpty(settleItemDTOs)) {
        Map<Long, SalesReturnItemDTO> settleItemsMap = new HashMap<Long, SalesReturnItemDTO>();
        for (SalesReturnItemDTO itemDTO : settleItemDTOs) {
          settleItemsMap.put(itemDTO.getId(), itemDTO);
        }
        for (SalesReturnItemDTO itemDTO : salesReturnDTO.getItemDTOs()) {
          SalesReturnItemDTO settleItemDTO = settleItemsMap.get(itemDTO.getId());
          itemDTO.setAmount(settleItemDTO.getStorageAmount());
          itemDTO.setPrice(NumberUtil.round(itemDTO.getTotal() / itemDTO.getAmount(), NumberUtil.MONEY_PRECISION));
          itemDTO.setUnit(settleItemDTO.getWholesalerUnit());
        }
      }
      if(BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(WebUtil.getShopVersionId(request))){
        if (salesReturnDTO.getStorehouseId() != null) {
          IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
          StoreHouseDTO storeHouseDTO = storeHouseService.getStoreHouseDTOById(shopId,salesReturnDTO.getStorehouseId());
          if(storeHouseDTO==null || DeletedType.TRUE.equals(storeHouseDTO.getDeleted())){
            return new Result(ValidatorConstant.STOREHOUSE_DELETED_MSG, false);
          }
        }else{
          return new Result(ValidatorConstant.STOREHOUSE_NULL_MSG, false);
        }
      }

      Map<Long, Long> supplierNewProductIdOldProductIdMap = new HashMap<Long, Long>();
      try {
        supplierNewProductIdOldProductIdMap = saleReturnOrderService.handleProductForSalesReturnOrder(salesReturnDTO);
      } catch (Exception e) {
        LOG.error(e.getMessage(), e);
        return new Result("结算失败，处理商品映射关系时失败。", false);
      }
      ServiceManager.getService(ITxnService.class).updateProductUnit(salesReturnDTO);
      Result result;
      String key = "salesReturnOrder" + StringUtil.truncValue(salesReturnDTO.getId().toString()) + "purchaseReturnOrder" + StringUtil.truncValue(salesReturnDTO.getPurchaseReturnOrderId().toString()) + StringUtil.truncValue(shopId.toString());
      try {
        if (!BcgogoConcurrentController.lock(ConcurrentScene.HANDLE_PAYABLE, key)) {
          return new Result("等待超时，结算失败！", false);
        }
        // 单据状态改变，结算, 生成相关Log记录
        result = saleReturnOrderService.settleSalesReturn(salesReturnDTO, userId, supplierNewProductIdOldProductIdMap);

        //add by WLF 更新缓存中待办销售退货单的数量
        List<Long> customerIdList = ServiceManager.getService(ICustomerService.class).getRelatedCustomerIdListByShopId(shopId);
        ServiceManager.getService(ITxnService.class).updateTodoOrderCountInMemcacheByTypeAndShopId(RemindEventType.TODO_SALE_RETURN_ORDER, salesReturnDTO.getShopId(), customerIdList);
      } finally {
        BcgogoConcurrentController.release(ConcurrentScene.HANDLE_PAYABLE, key);
      }

      //memcache中产品信息更新
      productCurrentUsedService.saveRecentChangedProductInMemory(salesReturnDTO);

      ServiceManager.getService(IProductHistoryService.class).saveProductHistoryForOrder(shopId,salesReturnDTO);

      if (result.isSuccess()) {
        List<CustomerRecordDTO> customerRecordDTOs = userService.getCustomerRecordByCustomerId(salesReturnDTO.getCustomerId());
        if (CollectionUtils.isNotEmpty(customerRecordDTOs)) {
          CustomerRecordDTO customerRecordDTO = customerRecordDTOs.get(0);
          customerRecordDTO.setTotalReturnAmount(customerRecordDTO.getTotalReturnAmount() + salesReturnDTO.getSettledAmount() + NumberUtil.doubleVal(salesReturnDTO.getAccountDebtAmount()));
          customerRecordDTO.setTotalReceivable(customerRecordDTO.getTotalReceivable() - salesReturnDTO.getStrikeAmount());
          customerRecordDTO.setRepayDate(null);
          if(null != salesReturnDTO.getAccountDebtAmount() && salesReturnDTO.getAccountDebtAmount() >0)
          {
            customerRecordDTO.setTotalPayable((null==customerRecordDTO.getTotalPayable()?0D:customerRecordDTO.getTotalPayable())
                +salesReturnDTO.getAccountDebtAmount());
          }
          userService.updateCustomerRecord(customerRecordDTO);
        }
        Map<Long,OutStorageRelationDTO[]> relationDTOMap=null;
        if(BcgogoShopLogicResourceUtils.isThroughSelectSupplier(WebUtil.getShopVersionId(request))){
          if(!ArrayUtil.isEmpty(settleSalesReturnDTO.getItemDTOs())){
            relationDTOMap=new HashMap<Long,OutStorageRelationDTO[]>();
            for(BcgogoOrderItemDto itemDto:settleSalesReturnDTO.getItemDTOs()){
              if(itemDto.getProductId()==null||itemDto==null){
                LOG.error("error info...");
                continue;
              }
              relationDTOMap.put(itemDto.getProductId(),itemDto.getOutStorageRelationDTOs());
            }
          }
          if(!ArrayUtil.isEmpty(salesReturnDTO.getItemDTOs())){
            for(BcgogoOrderItemDto itemDto:salesReturnDTO.getItemDTOs()){
              itemDto.setOutStorageRelationDTOs(relationDTOMap.get(itemDto.getProductId()));
            }
          }
        }
        //商品出入库打通
        salesReturnDTO.setSelectSupplier(BcgogoShopLogicResourceUtils.isThroughSelectSupplier(WebUtil.getShopVersionId(request)));
        ServiceManager.getService(IProductInStorageService.class).productThroughByOrder(salesReturnDTO,OrderTypes.SALE_RETURN,OrderStatus.SETTLED);

        //更新统计数据
        BcgogoEventPublisher bcgogoEventPublisher = new BcgogoEventPublisher();
        SalesReturnSavedEvent purchaseReturnSavedEvent = new SalesReturnSavedEvent(salesReturnDTO);
        bcgogoEventPublisher.publisherSalesReturnSaved(purchaseReturnSavedEvent);
      }
      return result;
    } catch (Exception e) {
      LOG.error("/salesReturn.do?method=settle 出错, shopId:{}, userId:{}", shopId, userId);
      LOG.error(e.getMessage(), e);
      return new Result("结算失败。", false);
    }
  }

  @RequestMapping(params = "method=printSalesReturnOrder")
  public void printSalesReturnOrder(HttpServletRequest request, HttpServletResponse response) {
    try {
      String salesReturnOrderId = request.getParameter("salesReturnOrderId");
      if (StringUtils.isBlank(salesReturnOrderId))
        throw new Exception("showSalesReturnOrderBySalesReturnOrderId id is empty!");
      IUserService userService = ServiceManager.getService(IUserService.class);
      ITxnService txnService = ServiceManager.getService(ITxnService.class);
      Long shopId = WebUtil.getShopId(request);
      SalesReturnDTO salesReturnDTO = saleReturnOrderService.getSalesReturnDTOById(shopId, Long.valueOf(salesReturnOrderId));
      if (null == salesReturnDTO)
        throw new Exception("showSalesReturnOrderBySalesReturnOrderId salesReturnDTO is empty!");
      //设置客户信息
      List<CustomerDTO> customerDTOList = userService.getShopCustomerById(shopId, salesReturnDTO.getCustomerId());
      if (CollectionUtils.isNotEmpty(customerDTOList)) {
        salesReturnDTO.setCustomerDTO(customerDTOList.get(0));
      }
      // 填充退货明细中的具体信息
      salesReturnDTO = saleReturnOrderService.fillSalesReturnItemDTOsDetailInfo(salesReturnDTO);

      ReceivableDTO receivableDTO = txnService.getReceivableByShopIdAndOrderTypeAndOrderId(shopId, OrderTypes.SALE_RETURN, salesReturnDTO.getId());
      if (receivableDTO != null) {
        salesReturnDTO.setSettledAmount(Math.abs(NumberUtil.round(receivableDTO.getSettledAmount(), NumberUtil.MONEY_PRECISION)));
        salesReturnDTO.setCashAmount(Math.abs(NumberUtil.round(receivableDTO.getCash(), NumberUtil.MONEY_PRECISION)));
        salesReturnDTO.setBankAmount(Math.abs(NumberUtil.round(receivableDTO.getBankCard(), NumberUtil.MONEY_PRECISION)));
        salesReturnDTO.setBankCheckAmount(Math.abs(NumberUtil.round(receivableDTO.getCheque(), NumberUtil.MONEY_PRECISION)));
        salesReturnDTO.setDiscountAmount(Math.abs(NumberUtil.round(receivableDTO.getDiscount(), NumberUtil.MONEY_PRECISION)));
        salesReturnDTO.setStrikeAmount(Math.abs(NumberUtil.round(receivableDTO.getStrike(), NumberUtil.MONEY_PRECISION)));

        if (!ArrayUtils.isEmpty(receivableDTO.getRecordDTOs())) {
          List<String> checkNos = new ArrayList<String>();
          for (ReceptionRecordDTO receptionRecordDTO : receivableDTO.getRecordDTOs()) {
            if (StringUtils.isNotBlank(receptionRecordDTO.getChequeNo())) {
              checkNos.add(receptionRecordDTO.getChequeNo());
            }
          }
          salesReturnDTO.setBankCheckNo(StringUtil.arrayToStr(",", checkNos));
        }
      }

      request.setAttribute("salesReturnDTO", salesReturnDTO);
      toPrint(request, response);
    } catch (Exception e) {
      LOG.error("/salesReturn.do");
      LOG.error("method=showSalesReturnOrderBySalesReturnOrderId");
      LOG.error("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
    }
  }

  private void toPrint(HttpServletRequest request, HttpServletResponse response) {
    Long shopId = WebUtil.getShopId(request);
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    IPrintService printService = ServiceManager.getService(IPrintService.class);
    try {
      PrintTemplateDTO printTemplateDTO = printService.getSinglePrintTemplateDTOByShopIdAndType(shopId, OrderTypes.SALE_RETURN);
      SalesReturnDTO salesReturnDTO = (SalesReturnDTO) request.getAttribute("salesReturnDTO");
      ShopDTO shopDTO = configService.getShopById(shopId);
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
        String myTemplateName = "salesReturnPrint" + String.valueOf(WebUtil.getShopId(request));
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
        context.put("salesReturnDTO", salesReturnDTO);
        context.put("shopDTO", shopDTO);
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
      LOG.error(e.getMessage(), e);
    }
  }

  @RequestMapping(params = "method=settleForNormal")
  @ResponseBody
  public Result settleForNormal(ModelMap model, HttpServletRequest request, SalesReturnDTO salesReturnDTO) {
    IProductService productService = ServiceManager.getService(IProductService.class);
    Long shopId = null;
    Long userId = null;

    try {

      shopId = WebUtil.getShopId(request);
      userId = WebUtil.getUserId(request);
      Long shopVersionId = WebUtil.getShopVersionId(request);
      String username = WebUtil.getUserName(request);
      if (shopVersionId == null || shopId == null || userId == null || salesReturnDTO == null) {
        LOG.error("shopId:" + shopId + "salesReturnDTO:" + salesReturnDTO == null ? "" : salesReturnDTO.toString());
        return new Result("数据出错，请重试。", false);
      }
      salesReturnDTO.setShopId(shopId);
      salesReturnDTO.setUserId(userId);
      salesReturnDTO.setShopVersionId(shopVersionId);
      salesReturnDTO.setUserName(username);
      salesReturnDTO.setCustomer(salesReturnDTO.getCustomerStr());

      //结算时间
      try {
        if(StringUtil.isNotEmpty(salesReturnDTO.getVestDateStr())){
          Long vestDate = DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN,salesReturnDTO.getVestDateStr());
          if(DateUtil.isSameDay(System.currentTimeMillis(),vestDate)){
            salesReturnDTO.setVestDate(System.currentTimeMillis());
          }else{
            salesReturnDTO.setVestDate(vestDate);
          }
        }
      } catch (Exception e) {
         salesReturnDTO.setVestDate(System.currentTimeMillis());
      }

      //更新客户信息
      ICustomerService customerService = ServiceManager.getService(ICustomerService.class);
      ITxnService txnService = ServiceManager.getService(ITxnService.class);
      if(salesReturnDTO.getCustomerId() == null  && CustomerConstant.DEFAULT_CUSTOMER_NAME.equals(salesReturnDTO.getCustomer())) {
        // modified by zhuj
        CustomerDTO customerDTO =customerService.isCustomerExist(shopId,CustomerConstant.DEFAULT_CUSTOMER_NAME,null,null);
        if (customerDTO!=null && customerDTO.getId() != null ) {
          salesReturnDTO.setCustomerId(customerDTO.getId());
          salesReturnDTO.setCustomerDTO(customerDTO);
          MemberDTO memberDTO = ServiceManager.getService(IMembersService.class).getMemberByCustomerId(shopId,customerDTO.getId());
          if(memberDTO != null){
            salesReturnDTO.setMemberNo(memberDTO.getMemberNo());
            salesReturnDTO.setMemberStatus(memberDTO.getStatus());
            salesReturnDTO.setMemberType(memberDTO.getType());
          }
        }else if(salesReturnDTO.getStrikeAmount() > 0){
          return  new Result("客户不存在，不能冲账",false);
        }
      }

      if(salesReturnDTO.getStrikeAmount() > 0 && salesReturnDTO.getCustomerId() != null) {
        List<String> stringList = txnService.getDebtFromReceivableByCustomerId(shopId, salesReturnDTO.getCustomerId(), OrderDebtType.CUSTOMER_DEBT_RECEIVABLE, ReceivableStatus.FINISH);
        if (NumberUtil.doubleVal(Double.valueOf(stringList.get(0))) < salesReturnDTO.getStrikeAmount()) {
          return new Result("客户欠款不足,不能冲账", false);
        }
      }

      if(BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(WebUtil.getShopVersionId(request))){
        if (salesReturnDTO.getStorehouseId() != null) {
          IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
          StoreHouseDTO storeHouseDTO = storeHouseService.getStoreHouseDTOById(shopId,salesReturnDTO.getStorehouseId());
          if(storeHouseDTO==null || DeletedType.TRUE.equals(storeHouseDTO.getDeleted())){
            return new Result(ValidatorConstant.STOREHOUSE_DELETED_MSG, false);
          }
        }else{
          return new Result(ValidatorConstant.STOREHOUSE_NULL_MSG, false);
        }
      }

      productService.updateProductForSalesReturn(salesReturnDTO);
      //更新已经被删除的商品，状态的值置空
	    getRfiTxnService().updateDeleteProductsByOrderDTO(salesReturnDTO);
      ServiceManager.getService(ITxnService.class).updateProductUnit(salesReturnDTO);
      Result checkResult = this.validateSalesReturnBeforeSettle(request, salesReturnDTO);
      if (!checkResult.isSuccess()) {
        return checkResult;
      }

      ISaleReturnOrderService saleReturnOrderService = ServiceManager.getService(ISaleReturnOrderService.class);

      Result updateResult = customerService.saveOrUpdateCustomerInfo(salesReturnDTO);
        Long supplierId = null;
        if(salesReturnDTO.getCustomerId() != null) {
          CustomerDTO customerDTO = ServiceManager.getService(IUserService.class).getCustomerById(salesReturnDTO.getCustomerId());
          CustomerRecordDTO customerRecordDTO = ServiceManager.getService(IUserService.class).getCustomerRecordByCustomerId(customerDTO.getId()).get(0);
          customerRecordDTO.fromCustomerDTO(customerDTO);
            if(customerDTO.getSupplierId() != null) {
                SupplierDTO supplierDTO = ServiceManager.getService(IUserService.class).getSupplierById(customerDTO.getSupplierId());
                if (null != customerRecordDTO) {
                    supplierDTO.fromCustomerRecordDTO(customerRecordDTO);
                    ServiceManager.getService(IUserService.class).updateSupplier(supplierDTO);
                    supplierId = supplierDTO.getId();

                }

            }
        }

      if(updateResult == null || !updateResult.isSuccess()){
        return  updateResult;
      }

      Result result = saleReturnOrderService.settleSalesReturnForNormal(salesReturnDTO);

      ServiceManager.getService(IProductHistoryService.class).saveProductHistoryForOrder(shopId,salesReturnDTO);

      //删除草稿
      if (StringUtils.isNotBlank(salesReturnDTO.getDraftOrderIdStr())) {
        ServiceManager.getService(DraftOrderService.class).deleteDraftOrder(salesReturnDTO.getShopId(), Long.valueOf(salesReturnDTO.getDraftOrderIdStr()));
      }

      if (result.isSuccess()) {
        //更新统计数据
        BcgogoEventPublisher bcgogoEventPublisher = new BcgogoEventPublisher();
        SalesReturnSavedEvent purchaseReturnSavedEvent = new SalesReturnSavedEvent(salesReturnDTO);
        bcgogoEventPublisher.publisherSalesReturnSaved(purchaseReturnSavedEvent);
      }
        if(supplierId != null) {
            ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexSupplierBySupplierId(supplierId);
        }

      return result;
    } catch (Exception e) {
      LOG.error("shopId:" + shopId + "salesReturnDTO:" + salesReturnDTO.toString());
      LOG.error(e.getMessage(), e);
      return new Result("数据出错，请重试。", false);
    }
  }

  @RequestMapping(params = "method=createSalesReturn")
  public String createSalesReturn(ModelMap model, HttpServletRequest request) {
    Long shopId = null;
    SalesReturnDTO salesReturnDTO = new SalesReturnDTO();
    try {
      IUserService userService = ServiceManager.getService(IUserService.class);

      ISaleReturnOrderService saleReturnOrderService = ServiceManager.getService(ISaleReturnOrderService.class);
      shopId = WebUtil.getShopId(request);
      if (shopId == null) {
        return "/";
      }
      salesReturnDTO.setEditor((String) request.getSession().getAttribute("userName"));
      salesReturnDTO.setEditorId((Long) request.getSession().getAttribute("userId"));
      salesReturnDTO.setShopId(shopId);
      salesReturnDTO.setSalesReturner((String) request.getSession().getAttribute("userName"));
      SalesMan salesMan = userService.getSalesManByName(shopId, salesReturnDTO.getSalesReturner());
      salesReturnDTO.setSalesReturnerId(salesMan == null ? null : salesMan.getId());

      salesReturnDTO.setEditDate(System.currentTimeMillis());
      salesReturnDTO.setVestDate(System.currentTimeMillis());
      if (!StringUtil.isEmpty(request.getParameter("receiptNo"))) {
        salesReturnDTO.setReceiptNo(request.getParameter("receiptNo"));
      }
      String orderIdStr = request.getParameter("orderId");
      String customerId = request.getParameter("customerId");
      String customerName = request.getParameter("customerName");
      String productIdStr = request.getParameter("productIds");
      if (orderIdStr == null) {
        if (StringUtil.isNotEmpty(customerId) || StringUtil.isNotEmpty(customerName)) {
          salesReturnDTO = saleReturnOrderService.getCustomerInfoByCustomerInfo(model,salesReturnDTO, customerId, customerName);
        } else if (StringUtil.isNotEmpty(productIdStr)) {
          salesReturnDTO = saleReturnOrderService.getCustomerInfoByProductIds(salesReturnDTO, productIdStr);
        }
       salesReturnDTO.setReadOnly(Boolean.FALSE);
      } else if (NumberUtil.isNumber(orderIdStr)) {
        if (request.getParameter("orderType").equals("sale")) {
          salesReturnDTO.setOriginOrderId(Long.valueOf(orderIdStr));
          salesReturnDTO.setOriginOrderType(OrderTypes.SALE);
          salesReturnDTO = saleReturnOrderService.createSalesReturnByOrderId(model,salesReturnDTO);
        } else if (request.getParameter("orderType").equals("repair")) {
          salesReturnDTO.setOriginOrderId(Long.valueOf(orderIdStr));
          salesReturnDTO.setOriginOrderType(OrderTypes.REPAIR);
          salesReturnDTO = saleReturnOrderService.createSalesReturnByOrderId(model,salesReturnDTO);
        }
        salesReturnDTO.setReadOnly(Boolean.TRUE);
      }
      if(BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(WebUtil.getShopVersionId(request))){
        IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
        List<StoreHouseDTO> storeHouseDTOList = storeHouseService.getAllStoreHousesByShopId(shopId);
        model.addAttribute("storeHouseDTOList", storeHouseDTOList);//select 选项
        if(CollectionUtils.isNotEmpty(storeHouseDTOList) && salesReturnDTO.getStorehouseId()==null){
          if(storeHouseDTOList.size()==1){
            salesReturnDTO.setStorehouseId(storeHouseDTOList.get(0).getId());
          }
        }
      }
    } catch (Exception e) {
      LOG.error("shopId:" + shopId + "salesReturnDTO:" + salesReturnDTO.toString());
      LOG.error(e.getMessage(), e);
    }
    salesReturnDTO.setCustomerStr(salesReturnDTO.getCustomer());
    salesReturnDTO.setVestDateStr(DateUtil.dateLongToStr(salesReturnDTO.getVestDate(),DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN));

    salesReturnDTO.initDefaultItemUnit(ProductUnitCache.getProductUnitMap());
    model.addAttribute("salesReturnDTO", salesReturnDTO);


    return "/txn/salesReturn";
  }


  @RequestMapping(params = "method=validateSalesReturnBeforeSettle")
  @ResponseBody
  public Result validateSalesReturnBeforeSettle(HttpServletRequest request, SalesReturnDTO salesReturnDTO) {
    Long shopId = null;
    Long userId = null;
    try {
      ISaleReturnOrderService saleReturnOrderService = ServiceManager.getService(ISaleReturnOrderService.class);
      shopId = WebUtil.getShopId(request);
      userId = WebUtil.getUserId(request);
      if (shopId == null || userId == null || salesReturnDTO == null) {
        LOG.error("shopId:" + shopId + "salesReturnDTO:" + salesReturnDTO == null ? "" : salesReturnDTO.toString());
        return new Result("数据出错，请重试。", false);
      }
      salesReturnDTO.setShopId(shopId);
      salesReturnDTO.setUserId(userId);
      salesReturnDTO.setCustomer(salesReturnDTO.getCustomerStr());
      if(BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(WebUtil.getShopVersionId(request))){
        if (salesReturnDTO.getStorehouseId() != null) {
          IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
          StoreHouseDTO storeHouseDTO = storeHouseService.getStoreHouseDTOById(shopId,salesReturnDTO.getStorehouseId());
          if(storeHouseDTO==null || DeletedType.TRUE.equals(storeHouseDTO.getDeleted())){
            return new Result(ValidatorConstant.STOREHOUSE_DELETED_MSG, false);
          }
        }else{
          return new Result(ValidatorConstant.STOREHOUSE_NULL_MSG, false);
        }
      }
      Result result = saleReturnOrderService.validateSalesReturnBeforeSettle(salesReturnDTO);
      return result;

    } catch (Exception e) {
      LOG.error("shopId:" + shopId + "salesReturnDTO:" + salesReturnDTO.toString());
      LOG.error(e.getMessage(), e);
      return new Result("结算失败。", false);
    }
  }

  @RequestMapping(params = "method=repeal")
  public String repeal(ModelMap model, HttpServletRequest request, Long orderId, Long toStorehouseId){
    IUserService userService = ServiceManager.getService(IUserService.class);
    ICustomerService customerService = ServiceManager.getService(ICustomerService.class);

    Long shopId = null;
    Long userId = null;
    try {
      shopId = WebUtil.getShopId(request);
      userId = WebUtil.getUserId(request);
      String userName = WebUtil.getUserName(request);
      Long shopVersionId = WebUtil.getShopVersionId(request);
      model.addAttribute("salesReturnOrderId", orderId);
      if (shopId == null || userId == null || orderId == null) {
        return REDIRECT_SHOW;
      }
      SalesReturnDTO salesReturnDTO = saleReturnOrderService.getSalesReturnDTOById(shopId, orderId);
      if(salesReturnDTO == null){
        LOG.error("shopId:{}, salesReturnOrderID:{} 为空！", shopId, orderId);
        return REDIRECT_SHOW;
      }
      if(salesReturnDTO.getStatus()!=OrderStatus.SETTLED){
        LOG.debug("单据状态不是已结算，无法作废！");
        return REDIRECT_SHOW;
      }
      Result result = validateRepeal(model, request, orderId);
      if(!result.isSuccess()){
        return REDIRECT_SHOW;
      }
      salesReturnDTO.setShopVersionId(shopVersionId);
      salesReturnDTO.setEditDate(System.currentTimeMillis());
      salesReturnDTO.setEditor(userName);
      salesReturnDTO.setEditorId(userId);
      salesReturnDTO.setUserId(userId);
      salesReturnDTO.setUserName(userName);

      saleReturnOrderService.repealOrderInTxn(salesReturnDTO, toStorehouseId);

      //销售退货单作废库存打通
      ServiceManager.getService(IProductOutStorageService.class).productThroughForSalesReturnRepeal(salesReturnDTO);


      //repeal_order
      getRfiTxnService().saveRepealOrderByOrderIdAndOrderType(shopId, salesReturnDTO.getId(), OrderTypes.SALE_RETURN);
      ServiceManager.getService(ITxnService.class).saveOperationLogTxnService(new OperationLogDTO(salesReturnDTO.getShopId(), salesReturnDTO.getUserId(), salesReturnDTO.getId(), ObjectTypes.SALE_RETURN_ORDER, OperationTypes.INVALID));
      //更新客户总退货量, 更新客户应收款
      saleReturnOrderService.updateCustomerInfoByRepeal(salesReturnDTO);

      //线程做orderindex
      BcgogoEventPublisher bcgogoEventPublisher = new BcgogoEventPublisher();
      SalesReturnSavedEvent salesReturnSavedEvent = new SalesReturnSavedEvent(salesReturnDTO);
      bcgogoEventPublisher.publisherSalesReturnSaved(salesReturnSavedEvent);
      request.setAttribute("UNIT_TEST", salesReturnSavedEvent); //单元测试
    }catch(Exception e){
      LOG.debug("shopID:{}, userId:{}, id:{}", new Object[]{shopId, userId, orderId});
      LOG.error("salesReturn.do?method=repeal", e);
    }
    model.addAttribute("salesReturnOrderId", orderId);
    return REDIRECT_SHOW;
  }

  @RequestMapping(params = "method=validateRepeal")
  @ResponseBody
  public Result validateRepeal(ModelMap model, HttpServletRequest request, Long orderId){
    Result result = new Result(false);
    Long shopId = WebUtil.getShopId(request);
    if(orderId == null || shopId == null){
      result.setMsg("验证失败，请重试。");
      return result;
    }
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    ICustomerDepositService customerDepositService = ServiceManager.getService(ICustomerDepositService.class);
    Long shopVersionId = WebUtil.getShopVersionId(request);
    try{
      SalesReturnDTO salesReturnDTO = saleReturnOrderService.getSalesReturnDTOById(shopId, orderId);
      Long customerId = salesReturnDTO.getCustomerId();
      if(salesReturnDTO == null){
        result.setMsg("单据不存在！");
        return result;
      }
      if(salesReturnDTO.getStatus() != OrderStatus.SETTLED){
        result.setMsg("单据状态并非'已结算'，无法作废！");
        return result;
      }
      ReceivableDTO receivableDTO = txnService.getReceivableByShopIdOrderId(shopId, orderId);
      if(receivableDTO == null){
        result.setMsg("单据结算信息不存在，无法作废！");
        return result;
      }
      //销售退货不支持退到会员卡
//      if(receivableDTO.getMemberBalancePay()){

      if(Math.abs(receivableDTO.getDeposit())>0){
        CustomerDepositDTO customerDepositDTO = customerDepositService.queryCustomerDepositByShopIdAndCustomerId(shopId, customerId);
        if(customerDepositDTO== null || customerDepositDTO.getActuallyPaid() < Math.abs(receivableDTO.getDeposit())){
          result.setMsg("客户预收款余额不足，无法作废！");
          return result;
        }
      }
      Result enoughInventoryResult = null;
      if(BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(shopVersionId)){
        enoughInventoryResult = saleReturnOrderService.validateStorehouseInventoryForRepeal(salesReturnDTO);
      }else{
        enoughInventoryResult = saleReturnOrderService.validateEnoughInventoryForRepeal(salesReturnDTO);
      }
      if(!enoughInventoryResult.isSuccess()){
        return enoughInventoryResult;
      }
      return new Result(true);
    }catch(Exception e){
      result.setMsg("验证出现异常，无法作废！");
      return result;
    }
  }

}
