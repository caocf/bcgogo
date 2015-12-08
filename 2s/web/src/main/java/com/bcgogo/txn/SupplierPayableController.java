package com.bcgogo.txn;

import com.bcgogo.common.*;
import com.bcgogo.config.dto.OperationLogDTO;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.service.IOperationLogService;
import com.bcgogo.enums.*;
import com.bcgogo.exception.PageException;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.*;
import com.bcgogo.txn.dto.StatementAccount.OrderDebtType;
import com.bcgogo.txn.service.IPrintService;
import com.bcgogo.txn.service.IRunningStatService;
import com.bcgogo.txn.service.ISupplierPayableService;
import com.bcgogo.txn.service.ITxnService;
import com.bcgogo.txn.service.SupplierPayableService;
import com.bcgogo.txn.service.solr.ICustomerOrSupplierSolrWriteService;
import com.bcgogo.txn.service.solr.OrderSolrWriterService;
import com.bcgogo.user.dto.SupplierDTO;
import com.bcgogo.user.service.ISupplierService;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.utils.JsonUtil;
import com.bcgogo.utils.MoneyUtil;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.ValidatorConstant;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: zhangchuanlong
 * Date: 12-8-17
 * Time: 下午1:12
 * <p/>
 * 供应商应付款Controller
 */

@Scope("prototype")
@Controller
@RequestMapping("/payable.do")
public class SupplierPayableController {
  private static final Logger LOG = LoggerFactory.getLogger(SupplierPayableController.class);
  @Autowired
  private ISupplierPayableService supplierPayableService;

  public static final String SUCCESS="success";
  public static final String ERROR="error";
  public static final String CHECK_NO = "支票号";

  public void setSupplierPayableService(SupplierPayableService supplierPayableService) {
    this.supplierPayableService = supplierPayableService;
  }

  /**
   * 添加定金
   *
   * @param model
   * @param request
   * @author zhangchuanlong
   */
  @RequestMapping(params = "method=addDeposit")
  @ResponseBody
  public Result addDeposit(ModelMap model, HttpServletRequest request, HttpServletResponse response) {
    IRunningStatService runningStatService = ServiceManager.getService(IRunningStatService.class);
    ISupplierPayableService supplierPayableService = ServiceManager.getService(ISupplierPayableService.class);
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    String deposit = request.getParameter("depositDTO");
    String print = request.getParameter("print");
    Result result = new Result(true);

    try {
      // TODO zhuj 下面这些方法都应该放在service层 同一个事务里面
      DepositDTO depositDTO = new Gson().fromJson(deposit, DepositDTO.class);
      depositDTO.setShopId(shopId);
      depositDTO.setOperator(WebUtil.getUserName(request));
      result = supplierPayableService.supplierDepositAdd(depositDTO);// add by zhuj
      depositDTO.setPayTime(System.currentTimeMillis());
      runningStatService.runningStatFromDepositDTO(depositDTO,false);
      supplierPayableService.savePayableHistoryRecordFromDepositDTO(depositDTO);
      ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexSupplierBySupplierId(depositDTO.getSupplierId());
      if(StringUtils.isNotBlank(print) && print.equals("true")){
        result.setOperation("print");
      }
      model.addAttribute("depositDTO", depositDTO);   // unit test
      return result;
    } catch (Exception e) {
      LOG.debug("/payable.do");
      LOG.debug("method=addDeposit");
      LOG.error(e.getMessage(), e);
      result = new Result(false);
      result.setData(0D);
      return result;
    }
  }

  /**
   * 查询供应商预付款取用记录
   *
   * @param request
   * @param modelMap
   * @return
   */
  @RequestMapping(params = "method=queryDepositOrdersShopIdAndSupplierId", method = RequestMethod.POST)
  @ResponseBody
  public PagingListResult querySupplierDepositOrders(HttpServletRequest request, ModelMap modelMap, Integer startPageNo) {
    PagingListResult<DepositOrderDTO> resultList = new PagingListResult<DepositOrderDTO>();
    Long shopId = WebUtil.getShopId(request);
    String supplierId = request.getParameter("supplierId");
    try {
      resultList.setSuccess(true);
      int start = startPageNo == null ? 1 : startPageNo;
      int pageSize = 10;
      int totalCount = 0;
      Pager pager = new Pager(totalCount, start, pageSize);
      resultList.setPager(pager);
      if (StringUtils.isBlank(supplierId)) {
        LOG.error("[querySupplierDepositOrders],supplierId is blank.");
        return resultList;
      }
      if (StringUtils.isBlank(request.getParameter("inOutFlag"))) {
        return resultList;
      }
      Long inOutFlag = Long.parseLong(request.getParameter("inOutFlag")); // 0全部 1入 2出
      // 排序字段
      String sortName = request.getParameter("sortName");
      String sortFlag = request.getParameter("sortFlag");
      SortObj sortObj = new SortObj();
      if (StringUtils.isNotBlank(sortName) && StringUtils.isNotBlank(sortFlag)) {
        sortObj.setSortName(sortName);
        sortObj.setSortFlag(sortFlag);
      }

      ISupplierPayableService supplierPayableService = ServiceManager.getService(ISupplierPayableService.class);
      totalCount = supplierPayableService.countDepositOrdersByShopIdSupplierId(shopId, Long.parseLong(supplierId), inOutFlag);
      pager = new Pager(totalCount, start, pageSize);
      resultList.setPager(pager);
      List<DepositOrderDTO> depositOrderDTOs = supplierPayableService.queryDepositOrdersByShopIdSupplierId(shopId, Long.parseLong(supplierId), inOutFlag, sortObj, pager);
      if (!org.springframework.util.CollectionUtils.isEmpty(depositOrderDTOs)) {
        resultList.setResults(depositOrderDTOs);
        resultList.setSuccess(true);
        return resultList;
      }
      return resultList;
    } catch (Exception e) {
      LOG.error("[ajaxDepositOrdersQuery]:shopId is {},customerId is {},", new Object[]{shopId, supplierId});
    }
    return resultList;
  }

  /**
   * 分页查询应付款
   *
   * @param model
   * @param request
   * @param response
   * @throws ParseException
   * @throws PageException
   * @author zhangchuanlong
   */
  @RequestMapping(params = "method=searchPayable")
  public void searchPayable(ModelMap model, HttpServletRequest request, HttpServletResponse response) throws ParseException, PageException {
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    Long supplierId = null;
    try {
      if (!StringUtil.isEmpty(request.getParameter("supplierId"))) {
        supplierId = Long.valueOf(request.getParameter("supplierId"));
      }
      String orderByType = request.getParameter("orderType");
      String orderByName = request.getParameter("orderName");
      String fromTime = request.getParameter("fromTime");
      String toTime = request.getParameter("toTime");
      int pageNo = Integer.parseInt(request.getParameter("startPageNo"));
      int pageSize = 5;
      int totalCount = supplierPayableService.searchPayable(shopId, supplierId, fromTime, toTime);
      Pager pager = new Pager(totalCount, pageNo, pageSize);
      List<PayableDTO> payableDTOs = supplierPayableService.searchPayable(shopId, supplierId, fromTime, toTime, orderByType, orderByName, pager);
      String jsonStr = "";
      jsonStr = JsonUtil.listToJson(payableDTOs);
      jsonStr = jsonStr.substring(0, jsonStr.length() - 1);
      if (!"[".equals(jsonStr.trim())) {
        jsonStr = jsonStr + "," + pager.toJson().substring(1, pager.toJson().length());
      } else {
        jsonStr = pager.toJson();
      }
      model.addAttribute("totalCount", totalCount);  //用于单元测试
      model.addAttribute("payableDTOs", payableDTOs);//用于单元测试
      PrintWriter writer = response.getWriter();
      writer.write(jsonStr);
      writer.close();
    } catch (Exception e) {
      LOG.debug("/payable.do");
      LOG.debug("method=searchPayable");
      LOG.error(e.getMessage(), e);
    }
  }

  /**
   * 付款给供应商
   *
   * @param model
   * @param request
   * @param response
   * @author zhangchuanlong
   */
  @RequestMapping(params = "method=payToSupplier")
  public void payToSupplier(ModelMap model, HttpServletRequest request, HttpServletResponse response) {
    String checkResult = "";
    try {

      try {
        Long shopId = (Long) request.getSession().getAttribute("shopId");
        Long userId = WebUtil.getUserId(request);
        String username = WebUtil.getUserName(request);
        String lstPayAbles = request.getParameter("lstPayAbles");

        //应付款ID
        List<PayableDTO> payableDTOList = new Gson().fromJson(lstPayAbles, new TypeToken<List<PayableDTO>>() {
        }.getType());
        //应付款历史
        String payDTO = request.getParameter("payableHistoryDTO");
        PayableHistoryDTO payableHistoryDTO = new Gson().fromJson(payDTO, PayableHistoryDTO.class);
        payableHistoryDTO.setShopId(shopId);
        if(NumberUtil.doubleVal(payableHistoryDTO.getCheckAmount()) <= 0){
          payableHistoryDTO.setCheckNo("");
        }else if(!StringUtil.isEmpty(payableHistoryDTO.getCheckNo()) && CHECK_NO.equals(payableHistoryDTO.getCheckNo())){
          payableHistoryDTO.setCheckNo("");
        }
        payableHistoryDTO.setPayer(username);
        payableHistoryDTO.setPayerId(userId);
        payableHistoryDTO.setPayTime(System.currentTimeMillis());

        checkResult = supplierPayableService.checkSupplierAccount(payableDTOList, payableHistoryDTO);
        if (StringUtil.isEmpty(checkResult)) {
          checkResult = SUCCESS;
          //添加付款历史
          payableHistoryDTO = supplierPayableService.saveOrUpdatePayableHistory(payableHistoryDTO);
          /*付款给供应商进行结算  返回实际的付款记录*/
          List<PayableDTO> payableDTOs =  supplierPayableService.payedToSupplier(payableDTOList, payableHistoryDTO, PaymentTypes.INVENTORY_DEBT);
          SupplierDTO supplierDTO = ServiceManager.getService(IUserService.class).getSupplierById(payableHistoryDTO.getSupplierId());
//          if(supplierDTO.getCustomerId() != null) {
//              CustomerRecordDTO customerRecordDTO = ServiceManager.getService(IUserService.class).getCustomerRecordByCustomerId(supplierDTO.getCustomerId()).get(0);
//              customerRecordDTO.setTotalPayable(NumberUtil.doubleVal(customerRecordDTO.getTotalPayable()) - payableHistoryDTO.getActuallyPaid());
//              ServiceManager.getService(IUserService.class).updateCustomerRecord(customerRecordDTO);
//              ServiceManager.getService(ISupplierSolrWriteService.class).reindexCustomerByCustomerId(customerRecordDTO.getCustomerId());
//          }
          //solr reindex
            if(supplierDTO.getCustomerId() != null) {
                ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexCustomerByCustomerId(supplierDTO.getCustomerId());
            }
          if (CollectionUtils.isNotEmpty(payableDTOs)) {
            for (PayableDTO payableDTO : payableDTOs) {
              //ad by WLF 保存应付款结算的日志
              ServiceManager.getService(ITxnService.class).saveOperationLogTxnService(
                new OperationLogDTO(shopId, (Long)request.getSession().getAttribute("userId"), payableDTO.getPurchaseInventoryId(), ObjectTypes.INVENTORY_ORDER, OperationTypes.SETTLE));
              ServiceManager.getService(OrderSolrWriterService.class).reCreateOrderSolrIndex(ServiceManager.getService(IConfigService.class).getShopById(shopId), OrderTypes.INVENTORY, payableDTO.getPurchaseInventoryId());
            }
          }
          ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexSupplierBySupplierId(payableHistoryDTO.getSupplierId());
        }
      } catch (Exception e) {
        LOG.debug("/payable.do");
        LOG.debug("method=payToSupplier");
        LOG.error(e.getMessage(), e);
        checkResult = "结算失败";
      }

      PopMessage popMessage = new PopMessage();
      popMessage.setMessage(checkResult);
      model.addAttribute("result", popMessage);
      checkResult = JsonUtil.objectToJson(popMessage);
      PrintWriter writer = response.getWriter();
      writer.write(checkResult);
      writer.close();
    } catch (Exception e) {
      LOG.debug("/payable.do");
      LOG.debug("method=payToSupplier");
      LOG.error(e.getMessage(), e);
    }
  }

  /**
   * 根据供应商ID获得总付款记录数
   *
   * @param request
   * @param response
   * @author zhangchuanlong
   */
  @RequestMapping(params = "method=getTotalCountOfPayable")
  public void getTotalCountOfPayable(ModelMap model, HttpServletRequest request, HttpServletResponse response) {
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    String supplierId = request.getParameter("supplierId");
    if (StringUtils.isBlank(supplierId)) return;
    try {
      int totalCount = supplierPayableService.getTotalCountOfPayable(shopId, Long.valueOf(supplierId));
      PopMessage popMessage = new PopMessage();
      popMessage.setMessage(totalCount);
      model.addAttribute("result", popMessage);
      model.addAttribute("totalCount", totalCount);
      String jsonStr = JsonUtil.objectToJson(popMessage);
      PrintWriter writer = response.getWriter();
      writer.write(jsonStr);
      writer.close();

    } catch (Exception e) {
      LOG.debug("/payable.do");
      LOG.debug("method=getTotalCountOfPayable");
      LOG.error(e.getMessage(), e);
    }
  }

   @RequestMapping(params = "method=toPayableSettlement")
  public String toPayableSettlement(ModelMap model, Long supplierId) {
    try {
//      int totalCount = supplierPayableService.getTotalCountOfPayable(WebUtil.getShopId(request), Long.valueOf(request.getParameter("supplierId")));
//      PopMessage popMessage = new PopMessage();
//      popMessage.setMessage(totalCount);
//      model.addAttribute("result", popMessage);
//      model.addAttribute("totalCount", totalCount);
//      String jsonStr = JsonUtil.objectToJson(popMessage);
//      PrintWriter writer = response.getWriter();
//      writer.write(jsonStr);
//      writer.close();
      model.addAttribute("supplierId",supplierId);
      return "/customer/payableSettlement";

    } catch (Exception e) {
      LOG.debug("/payable.do");
      LOG.debug("method=getTotalCountOfPayable");
      LOG.error(e.getMessage(), e);
      return null;
    }
  }


  /**
   * 付款历史记录查询
   *
   * @param model
   * @param request
   * @param response
   * @author zhangchuanlong
   */
  @RequestMapping(params = "method=payHistoryRecords")
  public void getPayableHistoryRecord(ModelMap model, HttpServletRequest request, HttpServletResponse response) throws PageException, ParseException {
    Long shopId = (Long) request.getSession().getAttribute("shopId");    //店面ID
    String startTime = request.getParameter("startTime");//开始时间
    String endTime = request.getParameter("endTime"); //结束时间
    String supplierId = request.getParameter("supplierId");      //供应商ID
    String orderByName = request.getParameter("orderByName");        //排序字段
    String orderByType = request.getParameter("orderByType");       //排序方式
    if (StringUtils.isBlank(supplierId)) return;
    try {
      int totalCount = supplierPayableService.getTotalCountOfPayableHistoryRecord(shopId, supplierId, startTime, endTime);
      int pageNo = Integer.parseInt(request.getParameter("startPageNo"));
      int pageSize = 5;
      Pager pager = new Pager(totalCount, pageNo, pageSize);
      List<PayableHistoryRecordDTO> payableHistoryRecordDTOs = supplierPayableService.getPayableHistoryRecord(shopId, supplierId, startTime, endTime, orderByName, orderByType, pager);
      if(CollectionUtils.isNotEmpty(payableHistoryRecordDTOs)) {
        for (PayableHistoryRecordDTO payableHistoryRecordDTO : payableHistoryRecordDTOs) {
          payableHistoryRecordDTO.setMaterialName(payableHistoryRecordDTO.getMaterialName() == null ? "" : payableHistoryRecordDTO.getMaterialName().replaceAll("\\...", ""));
        }
      }
      String jsonStr = "";
      jsonStr = JsonUtil.listToJson(payableHistoryRecordDTOs);
      jsonStr = jsonStr.substring(0, jsonStr.length() - 1);
      if (!"[".equals(jsonStr.trim())) {
        jsonStr = jsonStr + "," + pager.toJson().substring(1, pager.toJson().length());
      } else {
        jsonStr = pager.toJson();
      }
      model.addAttribute("payableHistoryRecordDTOs", payableHistoryRecordDTOs);           //用于单元测试

      PrintWriter writer = response.getWriter();
      writer.write(jsonStr);
      writer.close();
    } catch (Exception e) {
      LOG.debug("/payable.do");
      LOG.debug("method=payHistoryRecords");
      LOG.error(e.getMessage(), e);
    }
  }

  /**
   * 根据供应商ID 获得应付总额
   *
   * @param model
   * @param request
   * @param response
   */
  @RequestMapping(params = "method=getCreditAmountBySupplierId")
  public void getCreditAmountBySupplierId(ModelMap model, HttpServletRequest request, HttpServletResponse response) {
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    String supplierId = request.getParameter("supplierId");
    if (StringUtils.isEmpty(supplierId)) return;
    try {
      Double sumPayable = supplierPayableService.getSumPayableBySupplierId(Long.parseLong(supplierId), shopId, OrderDebtType.SUPPLIER_DEBT_PAYABLE).get(0);
      PopMessage popMessage = new PopMessage();
      popMessage.setMessage(sumPayable);
      String jsonStr = JsonUtil.objectToJson(popMessage);
      PrintWriter writer = response.getWriter();
      writer.write(jsonStr);
      writer.close();
      model.addAttribute("result", popMessage);             //用于单元测试
    } catch (Exception e) {
      LOG.debug("/payable.do");
      LOG.debug("method=getTotalCountOfPayable");
      LOG.error(e.getMessage(), e);
    }
  }

  /**
   * 根据供应商ID获得供应商总定金额
   *
   * @param model
   * @param request
   * @param response
   */
  @RequestMapping(params = "method=getSumDepositBySupplierId")
  public void getSumDepositBySupplierId(ModelMap model, HttpServletRequest request, HttpServletResponse response) {
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    String supplierId = request.getParameter("supplierId");
    if (StringUtils.isEmpty(supplierId)) return;
    try {
      Double sumPayable = supplierPayableService.getSumDepositBySupplierId(Long.parseLong(supplierId), shopId);
      PopMessage popMessage = new PopMessage();
      popMessage.setMessage(sumPayable);
      String jsonStr = JsonUtil.objectToJson(popMessage);
      PrintWriter writer = response.getWriter();
      writer.write(jsonStr);
      writer.close();
      model.addAttribute("result", popMessage);
    } catch (Exception e) {
      LOG.debug("/payable.do");
      LOG.debug("method=getSumDepositBySupplierId");
      LOG.error(e.getMessage(), e);
    }
  }

  /**
   *
   * 入库作废时判断单据是否有冲账 有冲账不能作废
   */
  @RequestMapping(params = "method=checkPaidInventory")
  public void checkPaidInventory(ModelMap model, HttpServletRequest request, HttpServletResponse response) {
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    String supplierId = request.getParameter("supplierId");
    String inventoryId = request.getParameter("purchaseInventoryId");
    String message = ERROR;
    if (supplierId == null || "".equals(supplierId) || inventoryId == null || "".endsWith(inventoryId))
      return;
    try {
      PayableDTO payableDTO = supplierPayableService.getInventoryPayable(shopId, Long.parseLong(inventoryId), Long.parseLong(supplierId));
      //入库作废时判断单据是否有冲账 有冲账不能作废
      if(NumberUtil.doubleVal(payableDTO.getStrikeAmount()) > 0){
        message = SUCCESS;
      }
      PopMessage popMessage = new PopMessage();
      popMessage.setMessage(message);
      model.addAttribute("result", popMessage);
      String jsonStr = JsonUtil.objectToJson(popMessage);
      PrintWriter writer = response.getWriter();
      writer.write(jsonStr);
      writer.close();

    } catch (Exception e) {
      LOG.debug("/payable.do");
      LOG.debug("method=checkPaidInventory");
      LOG.error(e.getMessage(), e);
    }
  }

  /**
   * 已废弃 不要继续使用 liuWei
   * 对老入库单据初始化
   *
   */
  @Deprecated
  @RequestMapping(params = "method=initPurchaseInventoryPayable")
  public void initPurchaseInventoryPayable(ModelMap model) throws Exception {
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    ISupplierPayableService supplierPayableService = ServiceManager.getService(ISupplierPayableService.class);
    StringBuffer errorMessage = new StringBuffer();
    //获取所有单据
    List<PurchaseInventoryDTO> purchaseInventoryDTOs = txnService.getAllPurchaseInventory();
    //对每个单据进行保存
    for (PurchaseInventoryDTO p : purchaseInventoryDTOs) {
      if (p == null) {
        LOG.error("入库单据不存在！");
        continue;
      }
      String materialName = "";
      PayableDTO payableDTO = supplierPayableService.getInventoryPayable(p.getShopId(), p.getId(), p.getSupplierId());
      if (payableDTO == null) {
        PayableDTO newPayableDTO = new PayableDTO();
        newPayableDTO.setAmount(p.getTotal());
        newPayableDTO.setShopId(p.getShopId());
        newPayableDTO.setSupplierId(p.getSupplierId());
        newPayableDTO.setPaidAmount(p.getTotal());
        newPayableDTO.setPurchaseInventoryId(p.getId());
        newPayableDTO.setPayTime(p.getCreationDate());
        newPayableDTO.setCash(p.getTotal());
        newPayableDTO.setBankCard(0D);
        newPayableDTO.setCheque(0D);
        newPayableDTO.setDeposit(0D);
        if (p.getItemDTOs() != null) {
          for (PurchaseInventoryItemDTO purchaseInventoryItemDTO : p.getItemDTOs()) {
            if (p == null) {
              LOG.error("入库单内没有商品！");
              continue;
            }
            materialName = materialName + purchaseInventoryItemDTO.getProductName() + ";";
          }
        }
        if (p.getStatus() == OrderStatus.PURCHASE_INVENTORY_REPEAL) {
          newPayableDTO.setStatus(PayStatus.REPEAL);
        } else {
          newPayableDTO.setStatus(PayStatus.USE);
        }
        newPayableDTO.setMaterialName(materialName);
        newPayableDTO.setPayTime(p.getCreationDate());
        supplierPayableService.savePayable(newPayableDTO);
        payableDTO = supplierPayableService.getInventoryPayable(p.getShopId(), p.getId(), p.getSupplierId());
        model.addAttribute("payableDTO", newPayableDTO);
      } else {
        model.addAttribute("payableDTO", payableDTO);
      }


      if (payableDTO == null) {
        LOG.error("shop_id:" + p.getShopId() + "purchaseInventoryId:" + p.getId() + "初始化实付记录失败");
        continue;
      }

      double cash = 0.0;  //现金
      double bankCard = 0.0; //银行卡
      double cheque = 0.0; //支票
      double deposit = 0.0; //定金

      List<PayableHistoryRecordDTO> payableHistoryDTOList = supplierPayableService.getPayableHistoryRecord(p.getShopId(), p.getSupplierId(), p.getId(),null);
      //没有记录
      if (CollectionUtils.isEmpty(payableHistoryDTOList)) {

        PayableHistoryRecordDTO payableHistoryRecordDTO = new PayableHistoryRecordDTO();
        payableHistoryRecordDTO.setShopId(p.getShopId());
        payableHistoryRecordDTO.setDeduction(0D);
        payableHistoryRecordDTO.setCreditAmount(0D);
        payableHistoryRecordDTO.setCash(p.getTotal());
        payableHistoryRecordDTO.setBankCardAmount(0D);
        payableHistoryRecordDTO.setCheckAmount(0D);
        payableHistoryRecordDTO.setDepositAmount(0D);
        payableHistoryRecordDTO.setActuallyPaid(p.getTotal());
        payableHistoryRecordDTO.setAmount(p.getTotal());
        payableHistoryRecordDTO.setPurchaseInventoryId(p.getId());
        payableHistoryRecordDTO.setSupplierId(p.getSupplierId());
        payableHistoryRecordDTO.setPayableId(payableDTO.getId());
        payableHistoryRecordDTO.setPaymentType(PaymentTypes.INVENTORY);
        payableHistoryRecordDTO.setPaidAmount(p.getTotal());
        payableHistoryRecordDTO.setPaidTime(p.getCreationDate());
        payableHistoryRecordDTO.setStatus(PayStatus.USE);

        payableHistoryRecordDTO.setMaterialName(payableDTO.getMaterialName());
        supplierPayableService.savePayHistoryRecord(payableHistoryRecordDTO);


        if (OrderStatus.PURCHASE_INVENTORY_REPEAL == p.getStatus()) {

          Long repealDate = null;
          List<RepealOrderDTO> repealOrderDTOList = txnService.getRepealOrderByShopIdAndOrderId(p.getShopId(), p.getId());
          if (CollectionUtils.isEmpty(repealOrderDTOList)) {
            errorMessage.append("shopId:" + p.getShopId() + ",orderId:" + p.getId() + "单据已作废,在repealOrder表中无记录");
            repealDate = p.getLastModified();
          } else {
            repealDate = repealOrderDTOList.get(0).getRepealDate();
          }

          payableHistoryRecordDTO = new PayableHistoryRecordDTO();
          payableHistoryRecordDTO.setShopId(p.getShopId());
          payableHistoryRecordDTO.setDeduction(0D);
          payableHistoryRecordDTO.setCreditAmount(0D);
          payableHistoryRecordDTO.setCash(0 - p.getTotal());
          payableHistoryRecordDTO.setBankCardAmount(0D);
          payableHistoryRecordDTO.setCheckAmount(0D);
          payableHistoryRecordDTO.setDepositAmount(0D);
          payableHistoryRecordDTO.setActuallyPaid(0 - p.getTotal());
          payableHistoryRecordDTO.setAmount(0 - p.getTotal());
          payableHistoryRecordDTO.setPurchaseInventoryId(p.getId());
          payableHistoryRecordDTO.setSupplierId(p.getSupplierId());
          payableHistoryRecordDTO.setPayableId(payableDTO.getId());
          payableHistoryRecordDTO.setPaymentType(PaymentTypes.INVENTORY_REPEAL);
          payableHistoryRecordDTO.setPaidAmount(0 - p.getTotal());
          payableHistoryRecordDTO.setStatus(PayStatus.REPEAL);

          payableHistoryRecordDTO.setPaidTime(repealDate);
          payableHistoryRecordDTO.setMaterialName(payableDTO.getMaterialName());
          supplierPayableService.savePayHistoryRecord(payableHistoryRecordDTO);
        }

      } else {
        for (PayableHistoryRecordDTO payableHistoryRecordDTO : payableHistoryDTOList) {
          cash += payableHistoryRecordDTO.getCash();
          bankCard += payableHistoryRecordDTO.getBankCardAmount();
          cheque += payableHistoryRecordDTO.getCheckAmount();
          deposit += payableHistoryRecordDTO.getDepositAmount();
        }

        if (OrderStatus.PURCHASE_INVENTORY_REPEAL == p.getStatus()) {

          Long repealDate = null;
          List<RepealOrderDTO> repealOrderDTOList = txnService.getRepealOrderByShopIdAndOrderId(p.getShopId(), p.getId());
          if (CollectionUtils.isEmpty(repealOrderDTOList)) {
            errorMessage.append("shopId:" + p.getShopId() + ",orderId:" + p.getId() + "单据已作废,在repealOrder表中无记录");
            repealDate = p.getLastModified();
          } else {
            repealDate = repealOrderDTOList.get(0).getRepealDate();
          }

          PayableHistoryRecordDTO payableHistoryRecordDTO = new PayableHistoryRecordDTO();
          payableHistoryRecordDTO.setShopId(p.getShopId());
          payableHistoryRecordDTO.setDeduction(0D);
          payableHistoryRecordDTO.setCreditAmount(0D);
          payableHistoryRecordDTO.setCash(0 - p.getTotal());
          payableHistoryRecordDTO.setBankCardAmount(0D);
          payableHistoryRecordDTO.setCheckAmount(0D);
          payableHistoryRecordDTO.setDepositAmount(0D);
          payableHistoryRecordDTO.setActuallyPaid(0 - p.getTotal());
          payableHistoryRecordDTO.setAmount(0 - p.getTotal());
          payableHistoryRecordDTO.setPurchaseInventoryId(p.getId());
          payableHistoryRecordDTO.setSupplierId(p.getSupplierId());
          payableHistoryRecordDTO.setPayableId(payableDTO.getId());
          payableHistoryRecordDTO.setPaymentType(PaymentTypes.INVENTORY_REPEAL);
          payableHistoryRecordDTO.setPaidAmount(0 - p.getTotal());
          if (OrderStatus.PURCHASE_INVENTORY_REPEAL == p.getStatus()) {
            payableHistoryRecordDTO.setStatus(PayStatus.REPEAL);
          } else {
            payableHistoryRecordDTO.setStatus(PayStatus.USE);
          }
          payableHistoryRecordDTO.setPaidTime(repealDate);
          payableHistoryRecordDTO.setMaterialName(payableDTO.getMaterialName());
          supplierPayableService.savePayHistoryRecord(payableHistoryRecordDTO);
        }
      }
      payableDTO.setCash(cash);
      payableDTO.setCheque(cheque);
      payableDTO.setBankCard(bankCard);
      payableDTO.setPaidAmount(payableDTO.getAmount() - payableDTO.getCreditAmount() - payableDTO.getDeduction());
      payableDTO.setDeposit(deposit);
      supplierPayableService.updatePayable(payableDTO);
    }
  }

  /* 应付款结算打印  */
  @RequestMapping(params = "method=printPayable")
  public void printPayable(HttpServletRequest request,HttpServletResponse response) throws Exception {
      ITxnService txnService = ServiceManager.getService(ITxnService.class);
      ISupplierService supplierService = ServiceManager.getService(ISupplierService.class);
      IConfigService configService = ServiceManager.getService(IConfigService.class);

      Long shopId = (Long) request.getSession().getAttribute("shopId");
      ShopDTO shopDTO = configService.getShopById(shopId);

      Long supplierId = NumberUtil.longValue(request.getParameter("supplierId"), 0L);
      String totalAmount = request.getParameter("totalAmount");
      String payedAmount = request.getParameter("payedAmount");
      String deduction = request.getParameter("deduction");
      String creditAmount = request.getParameter("creditAmount");
      String cash = request.getParameter("cash");
      String bankCardAmount = request.getParameter("bankCardAmount");
      String checkAmount = request.getParameter("checkAmount");
      String depositAmount = request.getParameter("depositAmount");
      String[] payableIdsStrArray = request.getParameter("payableId").split(",");
      List<Long> payableIds = new ArrayList<Long>();
      List<PayableDTO> payableDTOList = new ArrayList<PayableDTO>();
      SupplierDTO supplierDTO = supplierService.getSupplierById(supplierId,shopId);

      for (int i = 0; i < payableIdsStrArray.length; i++) {
        if(payableIdsStrArray[i]!=null && !"".equals(payableIdsStrArray[i])){
          payableIds.add(Long.valueOf(payableIdsStrArray[i]));
        }
      }
      payableDTOList = supplierPayableService.getPayable(payableIds);
      //日期字符串阶段，去掉时间部分
      if(payableDTOList!=null){
        for(int i=0;i<payableDTOList.size();i++){
          payableDTOList.get(i).setPayTimeStr(payableDTOList.get(i).getPayTimeStr().substring(0,10));
        }
      }

      Date now = new Date();
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
      String szDatetime1 = sdf.format(now);
      IPrintService printService = ServiceManager.getService(IPrintService.class);
      try{
          PrintTemplateDTO printTemplateDTO = printService.getSinglePrintTemplateDTOByShopIdAndType(WebUtil.getShopId(request), OrderTypes.PAYABLE);
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
              String myTemplateName = "payable"+String.valueOf(WebUtil.getShopId(request));
              String myTemplate = str;
              //模板资源存放 资源库 中
              repo.putStringResource(myTemplateName, myTemplate);
              //取得velocity的模版
              Template t = ve.getTemplate(myTemplateName,"UTF-8");
              //取得velocity的上下文context
              VelocityContext context = new VelocityContext();
              //把数据填入上下文
              context.put("dateStr", szDatetime1);  //结算日期
              context.put("supplierDTO", supplierDTO);  //供应商信息
              context.put("payableDTOList",payableDTOList); //应付列表
              context.put("payedAmount", payedAmount);  //实付总额
              context.put("totalAmount", totalAmount);  //应付总额
              context.put("deduction",deduction);       //扣款
              context.put("creditAmount",creditAmount);//挂账
              //详细结算
              context.put("cash",cash);//现金
              context.put("bankCardAmount",bankCardAmount);//银行卡
              context.put("checkAmount",checkAmount);//支票
              context.put("depositAmount",depositAmount);//定金
              context.put("totalAmountStr", MoneyUtil.toBigType(totalAmount));  //总额繁体
              context.put("payedAmountStr", MoneyUtil.toBigType(payedAmount));  //实付繁体
              context.put("shopDTO", shopDTO);  //公司名称
              //输出流
              StringWriter writer = new StringWriter();
              //转换输出
              t.merge(context, writer);
              out.print(writer);
              writer.close();
          }else{
              out.print("<html><head><title></title></head><body>没有可用的模板</body><html>");
          }
          out.close();
      }catch(Exception e){
          LOG.debug("/payable.do");
          LOG.debug("id:" + supplierId);
          WebUtil.reThrow(LOG, e);
      }
  }

  /* 定金结算打印 */
  @RequestMapping(params = "method=printDeposit")
  public void printDeposit(HttpServletRequest request,HttpServletResponse response) throws Exception {
      ISupplierService supplierService = ServiceManager.getService(ISupplierService.class);
      IConfigService configService = ServiceManager.getService(IConfigService.class);

      Long shopId = (Long) request.getSession().getAttribute("shopId");
      ShopDTO shopDTO = configService.getShopById(shopId);

      Long supplierId = NumberUtil.longValue(request.getParameter("supplierId"), 0L);
      String cashDeposit = request.getParameter("cashDeposit");
      String bankCardAmountDeposit = request.getParameter("bankCardAmountDeposit");
      String checkAmountDeposit = request.getParameter("checkAmountDeposit");
      String checkNoDeposit = request.getParameter("checkNoDeposit");
      String actuallyPaidDeposit = request.getParameter("actuallyPaidDeposit");

      SupplierDTO supplierDTO = supplierService.getSupplierById(supplierId,shopId);
      Date now = new Date();
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
      String szDatetime1 = sdf.format(now);
      IPrintService printService = ServiceManager.getService(IPrintService.class);
      try{
          PrintTemplateDTO printTemplateDTO = printService.getSinglePrintTemplateDTOByShopIdAndType(WebUtil.getShopId(request), OrderTypes.PRE_PAY);
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
              String myTemplateName = "prePay"+String.valueOf(WebUtil.getShopId(request));
              String myTemplate = str;
              //模板资源存放 资源库 中
              repo.putStringResource(myTemplateName, myTemplate);
              //取得velocity的模版
              Template t = ve.getTemplate(myTemplateName,"UTF-8");
              //取得velocity的上下文context
              VelocityContext context = new VelocityContext();
              //把数据填入上下文
              context.put("dateStr", szDatetime1);                            //结算日期
              context.put("supplierDTO", supplierDTO);                       //供应商信息
              context.put("cashDeposit",cashDeposit);                        //现金
              context.put("bankCardAmountDeposit", bankCardAmountDeposit);  //银行卡
              context.put("checkAmountDeposit", checkAmountDeposit);        //支票
              context.put("actuallyPaidDeposit",actuallyPaidDeposit);       //实付
              context.put("actuallyPaidDepositStr",MoneyUtil.toBigType(actuallyPaidDeposit));       //实付大写
              context.put("shopDTO", shopDTO);                                //公司名称
              //输出流
              StringWriter writer = new StringWriter();
              //转换输出
              t.merge(context, writer);
              out.print(writer);
              writer.close();
          }else{
              out.print("<html><head><title></title></head><body>没有可用的模板</body><html>");
          }
          out.close();
      }catch(Exception e){
          LOG.debug("/payable.do");
          LOG.debug("id:" + supplierId);
          WebUtil.reThrow(LOG, e);
      }
  }


   /**
   *
   * 入库单结算前校验
   */
  @RequestMapping(params = "method=checkInventoryBeforeSettled")
  @ResponseBody
  public Object checkInventoryBeforeSettled(ModelMap model, HttpServletRequest request, HttpServletResponse response) {
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    String supplierId = request.getParameter("supplierId");
    String inventoryId = request.getParameter("purchaseInventoryId");
    String message = ERROR;
    if (supplierId == null || "".equals(supplierId) || inventoryId == null || "".endsWith(inventoryId))
     return new Result(ValidatorConstant.ORDER_IS_NULL_MSG, false);
    try {
      message = SUCCESS;
       return new Result(ValidatorConstant.ORDER_IS_NULL_MSG, true);

    } catch (Exception e) {
      LOG.debug("/payable.do");
      LOG.debug("method=checkInventoryBeforeSettled");
      LOG.error(e.getMessage(), e);
    }
     return new Result(ValidatorConstant.ORDER_IS_NULL_MSG, false);
  }



}