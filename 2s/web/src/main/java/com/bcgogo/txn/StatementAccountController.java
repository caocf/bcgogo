package com.bcgogo.txn;

import com.bcgogo.common.Pager;
import com.bcgogo.common.Result;
import com.bcgogo.common.StringUtil;
import com.bcgogo.common.WebUtil;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.enums.*;
import com.bcgogo.notification.dto.MessageSwitchDTO;
import com.bcgogo.notification.service.INotificationService;
import com.bcgogo.notification.service.ISmsService;
import com.bcgogo.search.dto.OrderSearchConditionDTO;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.stat.dto.SupplierRecordDTO;
import com.bcgogo.txn.bcgogoListener.orderEvent.StatementAccountEvent;
import com.bcgogo.txn.bcgogoListener.publisher.BcgogoEventPublisher;
import com.bcgogo.txn.dto.CustomerDepositDTO;
import com.bcgogo.txn.dto.PrintTemplateDTO;
import com.bcgogo.txn.dto.RunningStatDTO;
import com.bcgogo.txn.dto.StatementAccount.OrderDebtType;
import com.bcgogo.txn.dto.StatementAccount.StatementAccountConstant;
import com.bcgogo.txn.dto.StatementAccount.StatementAccountResultDTO;
import com.bcgogo.txn.dto.StatementAccountOrderDTO;
import com.bcgogo.txn.service.*;
import com.bcgogo.txn.service.statementAccount.IStatementAccountService;
import com.bcgogo.txn.service.supplierComment.ISupplierCommentService;
import com.bcgogo.user.dto.CustomerDTO;
import com.bcgogo.user.dto.CustomerRecordDTO;
import com.bcgogo.user.dto.MemberDTO;
import com.bcgogo.user.dto.SupplierDTO;
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
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import com.bcgogo.user.service.ICustomerService;

import static java.lang.String.valueOf;

/**
 * 对账单专用controller
 * Created by IntelliJ IDEA.
 * User: liuWei
 * Date: 13-1-8
 * Time: 下午3:09
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/statementAccount.do")
public class StatementAccountController {
  private static final Logger LOG = LoggerFactory.getLogger(StatementAccountController.class);


  /**
   * 跳转到对账单查询页面
   *
   * @param request
   * @return
   */
  @RequestMapping(params = "method=redirectSearchCustomerBill")
  public String redirectSearchCustomerBill(HttpServletRequest request, ModelMap model) {
    String customerOrSupplierIdStr = request.getParameter("customerOrSupplierIdStr");
    String type = request.getParameter(StatementAccountConstant.TYPE);
    Long shopId = WebUtil.getShopId(request);
    try {
      if (StringUtils.isNotEmpty(customerOrSupplierIdStr) && StringUtils.isNotEmpty(type)) {
        IUserService userService = ServiceManager.getService(IUserService.class);
        if (type.equals(StatementAccountConstant.CUSTOMER_TYPE)) {
          model.addAttribute("order", customerOrSupplierIdStr);
          model.addAttribute("orderType", OrderTypes.CUSTOMER_STATEMENT_ACCOUNT.toString());
          model.addAttribute("orderTypeStr", OrderTypes.CUSTOMER_STATEMENT_ACCOUNT.getName());
          CustomerDTO customerDTO = userService.getCustomerDTOByCustomerId(Long.valueOf(customerOrSupplierIdStr), shopId);
          if (customerDTO != null) {
            model.addAttribute("customerOrSupplierName", customerDTO.getName());
            model.addAttribute("mobile", customerDTO.getMobile());
          }
        } else if (type.equals(StatementAccountConstant.SUPPLIER_TYPE)) {
          model.addAttribute("orderType", OrderTypes.SUPPLIER_STATEMENT_ACCOUNT.toString());
          model.addAttribute("orderTypeStr", OrderTypes.SUPPLIER_STATEMENT_ACCOUNT.getName());
          ISupplierCommentService supplierCommentService = ServiceManager.getService(ISupplierCommentService.class);
          Long supplierShopId = supplierCommentService.getSupplierShopIdBySupplierId(Long.valueOf(customerOrSupplierIdStr));
          if (supplierShopId != null) {
            model.addAttribute("supplierShopId", supplierShopId);
          }
          SupplierDTO supplierDTO = userService.getSupplierById(Long.valueOf(customerOrSupplierIdStr));
          if (supplierDTO != null) {
            model.addAttribute("customerOrSupplierName", supplierDTO.getName());
            model.addAttribute("mobile", supplierDTO.getMobile());
          }
        }
        model.addAttribute("customerOrSupplierId", customerOrSupplierIdStr);
      }
      return "customer/customerBill";
    } catch (Exception e) {
      LOG.error("statementAccount.do method=redirectCustomerBill,customerOrSupplierIdStr" + "customerOrSupplierIdStr");
      LOG.error(e.getMessage(), e);
      return "/";
    }
  }

  /**
   * 跳转到生成对账单页面
   *
   * @param request
   * @return
   */
  @RequestMapping(params = "method=redirectCreateCustomerBill")
  public String redirectCreateCustomerBill(HttpServletRequest request, ModelMap model) {
    Long shopId = WebUtil.getShopId(request);
    if (shopId == null) {
      return "/";
    }
    String customerOrSupplierIdStr = request.getParameter("customerOrSupplierIdStr");
    try {
      if (StringUtil.isEmpty(customerOrSupplierIdStr) || !NumberUtil.isNumber(customerOrSupplierIdStr)) {
        LOG.error("statementAccount.do method=redirectCreateCustomerBill" + " customerId is null");
        return "/";
      }

      String orderTypeStr = request.getParameter("orderType");
      if (StringUtils.isEmpty(orderTypeStr)) {
        LOG.error("statementAccount.do method=redirectCreateCustomerBill" + " orderTypeStr is null");
        return "/";
      } else if ("clientInfo".equals(orderTypeStr)) {//汽配版客户详情页面orderType 传入的是clientInfo 特殊处理
        orderTypeStr = OrderTypes.CUSTOMER_STATEMENT_ACCOUNT.toString();
      }
      IUserService userService = ServiceManager.getService(IUserService.class);
      OrderTypes orderType = null;
      if (OrderTypes.CUSTOMER_STATEMENT_ACCOUNT.toString().equals(orderTypeStr)) {
        CustomerDTO customerDTO = userService.getCustomerDTOByCustomerId(Long.valueOf(customerOrSupplierIdStr), shopId);
        if (customerDTO == null) {
          throw new Exception("customer is null :customerId:" + customerOrSupplierIdStr);
        }
        if(customerDTO.getSupplierId() != null) {
            model.addAttribute("identity",StatementAccountConstant.IDENTITY);
        }
        model.addAttribute("mobile",customerDTO.getMobile());

        orderType = OrderTypes.CUSTOMER_STATEMENT_ACCOUNT;
      } else if (OrderTypes.SUPPLIER_STATEMENT_ACCOUNT.toString().equals(orderTypeStr)) {
        orderType = OrderTypes.SUPPLIER_STATEMENT_ACCOUNT;
        SupplierDTO supplierDTO = userService.getSupplierById(Long.valueOf(customerOrSupplierIdStr));
        if (supplierDTO == null) {
          throw new Exception("supplierDTO is null :supplierId:" + customerOrSupplierIdStr);
        }

        if (supplierDTO.getCustomerId() != null) {
          model.addAttribute("identity", StatementAccountConstant.IDENTITY);
        }

        model.addAttribute("mobile", supplierDTO.getMobile());

        ISupplierCommentService supplierCommentService = ServiceManager.getService(ISupplierCommentService.class);
        Long supplierShopId = supplierCommentService.getSupplierShopIdBySupplierId(Long.valueOf(customerOrSupplierIdStr));
        if (supplierShopId != null) {
          model.addAttribute("supplierShopId", supplierShopId);
        }

      } else {
        LOG.error("statementAccount.do method=redirectCreateCustomerBill" + " orderTypes is error:" + orderTypeStr);
        return "/";
      }
      model.addAttribute("orderType", orderType.toString());

      ITxnService txnService = ServiceManager.getService(ITxnService.class);
      String endTimeStr = DateUtil.dateLongToStr(System.currentTimeMillis(), DateUtil.DATE_STRING_FORMAT_DAY);
      String startTimeStr = StatementAccountConstant.DEFAULT_START_DATE;
      model.addAttribute("startTimeStr", startTimeStr);
      model.addAttribute("endTimeStr", endTimeStr);
      model.addAttribute("customerOrSupplierId", customerOrSupplierIdStr);
      StatementAccountOrderDTO statementAccountOrderDTO = new StatementAccountOrderDTO();
      model.addAttribute("statementAccountOrderDTO", statementAccountOrderDTO);
      String receiptNo = txnService.getReceiptNo(shopId, orderType, null);
      if (StringUtil.isEmpty(receiptNo)) {
        LOG.error("statementAccount.do method=redirectCreateCustomerBill receiptNo is empty:customerOrSupplierIdStr" + customerOrSupplierIdStr + ",shopId:" + shopId);
        return "/";
      }
      statementAccountOrderDTO.setReceiptNo(receiptNo);
      model.addAttribute("receiptNo", receiptNo);
      return "customer/createCustomerBill";
    } catch (Exception e) {
      LOG.error("statementAccount.do method=redirectCreateCustomerBill,shopId" + shopId + " customerOrSupplierIdStr:" + customerOrSupplierIdStr);
      LOG.error(e.getMessage(), e);
      return "/";
    }
  }


  /**
   * 根据前台查询条件查询对账单记录
   *
   * @param request
   * @param orderSearchConditionDTO
   * @param startPageNo
   * @param maxRows
   * @return
   */
  @ResponseBody
  @RequestMapping(params = "method=searchStatementAccountOrder")
  public Object searchStatementAccountOrder(HttpServletRequest request, OrderSearchConditionDTO orderSearchConditionDTO, int startPageNo, int maxRows) {
    List<Object> objectList = new ArrayList<Object>();

    try {
      Long shopId = WebUtil.getShopId(request);
      if (shopId == null) {
        return "/";
      }
      if (orderSearchConditionDTO == null) {
        LOG.error("statementAccount.do getStatementAccountOrder:orderSearchConditionDTO is null,shopId:" + shopId);
        return "/";
      }

      String customerOrSupplierIdSArrayStr = request.getParameter("customerOrSupplierIdSArray");
      if (StringUtils.isNotEmpty(customerOrSupplierIdSArrayStr)) {
        orderSearchConditionDTO.setCustomerOrSupplierIds(customerOrSupplierIdSArrayStr.split(","));
      }else if (StringUtils.isNotEmpty(orderSearchConditionDTO.getCustomerOrSupplierId()) && orderSearchConditionDTO.getCustomerOrSupplierIds() == null) {
        orderSearchConditionDTO.setCustomerOrSupplierIds(new String[]{orderSearchConditionDTO.getCustomerOrSupplierId()});
      }

      if (ConfigUtils.isWholesalerVersion(WebUtil.getShopVersionId(request)) && orderSearchConditionDTO.getCustomerOrSupplierIds() != null) {
        String[] customerOrSupplierIds = ServiceManager.getService(ICustomerService.class).getCustomerOrSupplierId(orderSearchConditionDTO.getCustomerOrSupplierIds());
        orderSearchConditionDTO.setCustomerOrSupplierIds(customerOrSupplierIds);
      }
      if(StringUtils.isNotEmpty(orderSearchConditionDTO.getReceiptNo())){
        orderSearchConditionDTO.setReceiptNo(orderSearchConditionDTO.getReceiptNo().toUpperCase());
      }
      StatementAccountResultDTO statementAccountResultDTO = new StatementAccountResultDTO();//返回结果封装类

      orderSearchConditionDTO.setShopId(shopId);

      if (StringUtil.isEmpty(orderSearchConditionDTO.getEndTimeStr())) {
        orderSearchConditionDTO.setEndTimeStr(DateUtil.dateLongToStr(System.currentTimeMillis(), DateUtil.DATE_STRING_FORMAT_DAY));
      }
      if (orderSearchConditionDTO.getStartTime() == null) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -10);
        orderSearchConditionDTO.setStartTime(calendar.getTimeInMillis());
      }

      orderSearchConditionDTO.verificationQueryTime();
      if (StringUtils.isNotEmpty(orderSearchConditionDTO.getReceiptNo())) {
        orderSearchConditionDTO.setReceiptNo(orderSearchConditionDTO.getReceiptNo().trim());
        if (StatementAccountConstant.RECEIPT_NO.equals(orderSearchConditionDTO.getReceiptNo())) {
          orderSearchConditionDTO.setReceiptNo(null);
        }
      }
      if (StringUtils.isNotEmpty(orderSearchConditionDTO.getOperator())) {
        orderSearchConditionDTO.setOperator(orderSearchConditionDTO.getOperator().trim());
        if (StatementAccountConstant.OPERATOR.equals(orderSearchConditionDTO.getOperator())) {
          orderSearchConditionDTO.setOperator(null);
        }
      }

      IStatementAccountService statementAccountService = ServiceManager.getService(IStatementAccountService.class);
      int size = statementAccountService.countStatementAccountOrderList(orderSearchConditionDTO);
      if (size <= 0) {
        objectList.add(statementAccountResultDTO);
        objectList.add(new Pager(0, startPageNo, maxRows));
        return objectList;
      }
      Pager pager = new Pager(size, startPageNo, maxRows);
      List<StatementAccountOrderDTO> statementAccountOrderDTOList = statementAccountService.getStatementAccountOrderList(orderSearchConditionDTO, pager);
      if (CollectionUtils.isEmpty(statementAccountOrderDTOList)) {
        throw new Exception("statementAccount.do getStatementAccountOrder: query result is error," + orderSearchConditionDTO.toString());
      }
      statementAccountResultDTO.setReceivableList(statementAccountOrderDTOList);
      StatementAccountOrderDTO statementAccountOrderDTO = statementAccountOrderDTOList.get(0);
      OrderDebtType orderDebtType = statementAccountOrderDTO.getOrderDebtType();
      if (orderDebtType == OrderDebtType.CUSTOMER_DEBT_PAYABLE || orderDebtType == OrderDebtType.SUPPLIER_DEBT_PAYABLE) {
        statementAccountResultDTO.setLastStateAccount(StatementAccountConstant.PAY_STR + statementAccountOrderDTO.getDebt());
      } else if (orderDebtType == OrderDebtType.CUSTOMER_DEBT_RECEIVABLE || orderDebtType == OrderDebtType.SUPPLIER_DEBT_RECEIVABLE) {
        statementAccountResultDTO.setLastStateAccount(StatementAccountConstant.RECEIVABLE_STR + statementAccountOrderDTO.getDebt());
      }
      objectList.add(statementAccountResultDTO);
      objectList.add(pager);
      return objectList;
    } catch (Exception e) {
      LOG.error("statementAccount.do getStatementAccountOrder:" + orderSearchConditionDTO.toString());
      LOG.error(e.getMessage(), e);
    }
    return null;
  }


  /**
   * 查询客户或者供应商欠款记录 跳转到对账单页面
   *
   * @param request
   * @param response
   * @return
   */
  @ResponseBody
  @RequestMapping(params = "method=getCurrentStatementAccountOrder")
  public Object getCurrentStatementAccountOrder(HttpServletRequest request, HttpServletResponse response, OrderSearchConditionDTO orderSearchConditionDTO) {

    try {
      Long shopId = WebUtil.getShopId(request);
      if (shopId == null || orderSearchConditionDTO == null || orderSearchConditionDTO.getCustomerOrSupplierId() == null || ArrayUtils.isEmpty(orderSearchConditionDTO.getOrderType())) {
        return null;
      }
      orderSearchConditionDTO.setShopId(shopId);
      if (orderSearchConditionDTO.getStartTimeStr() == null) {
        orderSearchConditionDTO.setStartTimeStr(StatementAccountConstant.DEFAULT_START_DATE);
      }
      if (orderSearchConditionDTO.getEndTimeStr() == null) {
        orderSearchConditionDTO.setEndTimeStr(DateUtil.dateLongToStr(System.currentTimeMillis(), DateUtil.DATE_STRING_FORMAT_DAY));
      }
      orderSearchConditionDTO.verificationQueryTime();

      IStatementAccountService statementAccountService = ServiceManager.getService(IStatementAccountService.class);
      List<Object> objectList = statementAccountService.getCurrentStatementAccountOrder(orderSearchConditionDTO);
      if (CollectionUtils.isEmpty(objectList)) {
        objectList = new ArrayList<Object>();
        StatementAccountResultDTO statementAccountResultDTO = new StatementAccountResultDTO();
        statementAccountResultDTO.setResultSize(0);
        objectList.add(statementAccountResultDTO);
        objectList.add(JsonUtil.EMPTY_JSON_STRING);
        return objectList;
      }
      return objectList;
    } catch (Exception e) {
      LOG.error("statementAccount.do getStatementAccountOrder:" + orderSearchConditionDTO.toString());
      LOG.error(e.getMessage(), e);
    }
    return null;
  }


  /**
   * 根据欠款类型跳转到结算页面
   *
   * @param request
   * @return
   * @throws Exception
   */
  @RequestMapping(params = "method=statementOrderAccount")
  public String statementOrderAccount(ModelMap model,HttpServletRequest request) throws Exception {

    String orderDebtType = request.getParameter("orderDebtType");
    if (StringUtil.isEmpty(orderDebtType)) {
      return "/";
    }
    String customerIdStr = request.getParameter("customerId");
    if (StringUtil.isEmpty(customerIdStr) || !NumberUtil.isNumber(customerIdStr)) {
      return "/";
    }

    request.setAttribute("depositDisplay", true);
    request.setAttribute("memberBalance", null);
    request.setAttribute("memberNo", null);

    Long shopId = WebUtil.getShopId(request);
    if (orderDebtType.equals(OrderDebtType.CUSTOMER_DEBT_RECEIVABLE.toString()) && BcgogoShopLogicResourceUtils.isMemberStoredValue(WebUtil.getShopVersionId(request))) {
      request.setAttribute(StatementAccountConstant.ROW_SPAN, StatementAccountConstant.MEMBER_ROW_SPAN);
    } else if (orderDebtType.equals(OrderDebtType.SUPPLIER_DEBT_PAYABLE.toString())) {
      request.setAttribute(StatementAccountConstant.ROW_SPAN, StatementAccountConstant.NORMAL_ROW_SPAN);
    } else {
      request.setAttribute(StatementAccountConstant.ROW_SPAN, StatementAccountConstant.NORMAL_ROW_SPAN);
    }

    if (orderDebtType.equals(OrderDebtType.SUPPLIER_DEBT_PAYABLE.toString())) {
      /*//如果该入库单有采购单，而且该供应商是批发商版本中的供应商 不能使用定金进行结算
      IUserService userService = ServiceManager.getService(IUserService.class);
      SupplierDTO supplierDTO = userService.getSupplierById(Long.valueOf(customerIdStr));
      if (supplierDTO != null && NumberUtil.longValue(supplierDTO.getSupplierShopId()) > 0) { // shopId不等于null 即为批发商版本供应商
        request.setAttribute("depositDisplay", true);
      } else {*/
        request.setAttribute("depositDisplay", false);
        ISupplierPayableService supplierPayableService = ServiceManager.getService(ISupplierPayableService.class);
        Double sumPayable = supplierPayableService.getSumDepositBySupplierId(Long.valueOf(customerIdStr), shopId);
        sumPayable = NumberUtil.toReserve(sumPayable, NumberUtil.MONEY_PRECISION);
        if (sumPayable > 0) {
          request.setAttribute("depositDisplay", true);
          request.setAttribute("sumPayable", sumPayable);
          request.setAttribute(StatementAccountConstant.ROW_SPAN, Integer.parseInt(request.getAttribute(StatementAccountConstant.ROW_SPAN).toString()) + 1);
        }
     // }
    } else if (orderDebtType.equals(OrderDebtType.CUSTOMER_DEBT_RECEIVABLE.toString())) {

      IMembersService membersService = ServiceManager.getService(IMembersService.class);
      MemberDTO memberDTO = membersService.getMemberByCustomerId(shopId, Long.valueOf(customerIdStr));
      request.setAttribute("memberBalance", null == memberDTO ? null : memberDTO.getBalance());
      request.setAttribute("memberNo", null == memberDTO ? null : memberDTO.getMemberNo());

      // 汽修版用户可以用预收款
      if (ConfigUtils.isWholesalerVersion(WebUtil.getShopVersionId(request))) {
        request.setAttribute("depositDisplay", false);
        // add by zhuj
        ICustomerDepositService depositService = ServiceManager.getService(ICustomerDepositService.class);
        CustomerDepositDTO depositDTO = depositService.queryCustomerDepositByShopIdAndCustomerId(shopId, Long.valueOf(customerIdStr));
        if (depositDTO != null && depositDTO.getActuallyPaid() != null) {
          Double sumPayable = NumberUtil.toReserve(depositDTO.getActuallyPaid(), NumberUtil.MONEY_PRECISION);
          if (sumPayable > 0) {
            request.setAttribute("depositDisplay", true);
            request.setAttribute("sumPayable", sumPayable);
            request.setAttribute(StatementAccountConstant.ROW_SPAN, Integer.parseInt(request.getAttribute(StatementAccountConstant.ROW_SPAN).toString()) + 1);
          }
        }
      }
    }
    model.addAttribute("orderDebtType",orderDebtType.toString());
    //短信控制开关
    MessageSwitchDTO messageSwitchDTO = ServiceManager.getService(INotificationService.class).getMessageSwitchDTOByShopIdAndScene(shopId, MessageScene.MEMBER_CONSUME_SMS_SWITCH);
    if(messageSwitchDTO == null || (messageSwitchDTO != null && MessageSwitchStatus.ON.equals(messageSwitchDTO.getStatus()))) {
      model.addAttribute("smsSwitch",true);
    } else {
      model.addAttribute("smsSwitch",false);
    }
    return "/orderAccount/statementOrderAccount";
  }

  @ResponseBody
  @RequestMapping(params = "method=settleStatementAccountOrder")
  public Object settleStatementAccountOrder(HttpServletRequest request, StatementAccountOrderDTO statementAccountOrderDTO) {
    Long shopId = WebUtil.getShopId(request);
    if (shopId == null) {
      return "/";
    }
    statementAccountOrderDTO.setShopId(shopId);
    statementAccountOrderDTO.setSalesManId((Long) (request.getSession().getAttribute("userId")));
    statementAccountOrderDTO.setSalesMan((String) request.getSession().getAttribute("userName"));

    IStatementAccountService statementAccountService = ServiceManager.getService(IStatementAccountService.class);
    Result result = null;
    Result validateResult = null;
    try {
      validateResult = statementAccountService.validateStatementAccountBeforeSettle(statementAccountOrderDTO);
      if (!validateResult.isSuccess()) {
        return validateResult;
      }
    } catch (Exception e) {
      LOG.error("statementAccount.do method=settleStatementAccountOrder");
      LOG.error(e.getMessage(), e);
      result = new Result();
      result.setSuccess(false);
      result.setMsg(StatementAccountConstant.STATEMENT_ACCOUNT_ORDER_ERROR);
      return result;
    }
    try {
      CustomerDTO customerDTO = null;
      SupplierDTO supplierDTO = null;
      IUserService userService = ServiceManager.getService(IUserService.class);
      ITxnService txnService = ServiceManager.getService(ITxnService.class);
      Long customerOrSupplierId = statementAccountOrderDTO.getCustomerOrSupplierId();
      if (statementAccountOrderDTO.getOrderType() == OrderTypes.CUSTOMER_STATEMENT_ACCOUNT) {
        customerDTO = userService.getCustomerDTOByCustomerId(customerOrSupplierId, shopId);
        if (customerDTO == null) {
          throw new Exception("customer is null :customerId:" + customerOrSupplierId);
        }
        SupplierDTO relatedSupplierDTO = null;

        if(customerDTO.getSupplierId()!=null){
          relatedSupplierDTO = userService.getSupplierById(customerDTO.getSupplierId());
          customerDTO.setPermanentDualRole(true);
          relatedSupplierDTO.setPermanentDualRole(true);
        }
        if(!StringUtil.isEmpty(statementAccountOrderDTO.getMobile()) && NumberUtil.isNumber(statementAccountOrderDTO.getMobile())){
          customerDTO.updateMobile(statementAccountOrderDTO.getMobile());

          if(customerDTO.getSupplierId()!=null){
            relatedSupplierDTO.updateMobile(statementAccountOrderDTO.getMobile());
          }
        }
        if(relatedSupplierDTO!=null){
          userService.updateSupplier(relatedSupplierDTO);
        }
        userService.updateCustomer(customerDTO);

        statementAccountOrderDTO.setCustomerOrSupplier(customerDTO.getName());
        statementAccountOrderDTO.setContact(customerDTO.getContact());
        statementAccountOrderDTO.setMobile(customerDTO.getMainContactMobile());
        statementAccountOrderDTO.setAddress(customerDTO.getAddress());
      } else if (statementAccountOrderDTO.getOrderType() == OrderTypes.SUPPLIER_STATEMENT_ACCOUNT) {
        supplierDTO = userService.getSupplierById(customerOrSupplierId);
        if (supplierDTO == null) {
          throw new Exception("supplierDTO is null :supplierId:" + customerOrSupplierId);
        }

        CustomerDTO relatedCustomerDTO = null;

        if(supplierDTO.getCustomerId()!=null){
          relatedCustomerDTO = userService.getCustomerById(supplierDTO.getCustomerId());
          supplierDTO.setPermanentDualRole(true);
          relatedCustomerDTO.setPermanentDualRole(true);
        }

        if(!StringUtil.isEmpty(statementAccountOrderDTO.getMobile()) && NumberUtil.isNumber(statementAccountOrderDTO.getMobile())){
          supplierDTO.updateMobile(statementAccountOrderDTO.getMobile());
          if(supplierDTO.getCustomerId()!=null){
            relatedCustomerDTO.updateMobile(statementAccountOrderDTO.getMobile());
          }
        }
        userService.updateSupplier(supplierDTO);
        if(relatedCustomerDTO!=null){
          userService.updateCustomer(relatedCustomerDTO);
        }

        statementAccountOrderDTO.setCustomerOrSupplier(supplierDTO.getName());
        statementAccountOrderDTO.setContact(supplierDTO.getContact());
        statementAccountOrderDTO.setAddress(supplierDTO.getAddress());
        statementAccountOrderDTO.setMobile(supplierDTO.getMainContactMobile());
      }

      result = statementAccountService.settleStatementAccountOrder(statementAccountOrderDTO);
      if(!result.isSuccess()){
        return result;
      }

      IRunningStatService runningStatService = ServiceManager.getService(IRunningStatService.class);
      RunningStatDTO runningStatDTO = statementAccountOrderDTO.toRunningStatDTO();
      runningStatService.runningStat(runningStatDTO,false);


      if (NumberUtil.doubleVal(statementAccountOrderDTO.getMemberAmount()) > 0 && statementAccountOrderDTO.getMemberDTO() != null) {
        IMembersService membersService = ServiceManager.getService(IMembersService.class);
        membersService.updateMember(statementAccountOrderDTO.getMemberDTO());
        if (statementAccountOrderDTO.isSendMemberSms()) {
          VelocityContext context = new VelocityContext();    //操作到此变量的都是为了提供会员结算短信内容。
          context.put(SmsConstant.VelocityMsgTemplateConstant.consumeAmount, NumberUtil.toReserve(statementAccountOrderDTO.getMemberAmount(),NumberUtil.MONEY_PRECISION));
          context.put(SmsConstant.VelocityMsgTemplateConstant.remainAmount, NumberUtil.toReserve(statementAccountOrderDTO.getMemberDTO().getBalance(),NumberUtil.MONEY_PRECISION));
          CustomerDTO cardOwner = userService.getCustomerWithMemberByMemberNoShopId(statementAccountOrderDTO.getAccountMemberNo(), shopId);
          if (cardOwner != null && StringUtils.isNotEmpty(cardOwner.getMobile())) {
            ISmsService smsService = ServiceManager.getService(ISmsService.class);
            IConfigService configService = ServiceManager.getService(IConfigService.class);
            ShopDTO shopDTO = configService.getShopById(Long.valueOf(shopId));
            smsService.sendMemberMsgToCardOwner(cardOwner, shopDTO, context);
          }
        }
      }
      // comment by zhuj
      /*if (NumberUtil.doubleVal(statementAccountOrderDTO.getDepositAmount()) > 0) {
        ISupplierPayableService supplierPayableService = ServiceManager.getService(ISupplierPayableService.class);
        supplierPayableService.paidByDepositFromDeposit(statementAccountOrderDTO.getShopId(), statementAccountOrderDTO.getCustomerOrSupplierId(), NumberUtil.doubleVal(statementAccountOrderDTO.getDepositAmount()));
      }*/

      if (statementAccountOrderDTO.getOrderType() == OrderTypes.CUSTOMER_STATEMENT_ACCOUNT) {
        CustomerRecordDTO customerRecordDTO = userService.getShopCustomerRecordByCustomerId(shopId, statementAccountOrderDTO.getCustomerOrSupplierId()).toDTO();
        Double totalPayable = ServiceManager.getService(ISupplierPayableService.class).getSumReceivableByCustomerId(statementAccountOrderDTO.getCustomerOrSupplierId(), shopId, OrderDebtType.CUSTOMER_DEBT_PAYABLE);
        customerRecordDTO.setTotalPayable(Math.abs(NumberUtil.doubleVal(totalPayable)));
        Double receivable = ServiceManager.getService(ISupplierPayableService.class).getSumReceivableByCustomerId(statementAccountOrderDTO.getCustomerOrSupplierId(), shopId, OrderDebtType.CUSTOMER_DEBT_RECEIVABLE);
        customerRecordDTO.setTotalReceivable(NumberUtil.toReserve(receivable,NumberUtil.MONEY_PRECISION));
        customerRecordDTO.setRepayDate(statementAccountOrderDTO.getPaymentTime());
        if (NumberUtil.doubleVal(statementAccountOrderDTO.getMemberAmount()) > 0) {
          customerRecordDTO.setMemberConsumeTimes(NumberUtil.longValue(customerRecordDTO.getMemberConsumeTimes()) + 1);
          customerRecordDTO.setMemberConsumeTotal(NumberUtil.doubleVal(customerRecordDTO.getMemberConsumeTotal()) + NumberUtil.doubleVal(statementAccountOrderDTO.getMemberAmount()));
        }
        userService.updateCustomerRecord(customerRecordDTO);
        if(customerDTO!=null && customerDTO.getSupplierId()!=null){
          ISupplierRecordService supplierRecordService = ServiceManager.getService(ISupplierRecordService.class);
          SupplierRecordDTO supplierRecordDTO = supplierRecordService.getSupplierRecordDTOBySupplierId(shopId, customerDTO.getSupplierId());
          List<Double> payableList= ServiceManager.getService(ISupplierPayableService.class).getSumPayableBySupplierId(customerDTO.getSupplierId(),shopId, OrderDebtType.SUPPLIER_DEBT_PAYABLE);
          List<Double> returnList = ServiceManager.getService(ISupplierPayableService.class).getSumPayableBySupplierId(customerDTO.getSupplierId(), shopId,OrderDebtType.SUPPLIER_DEBT_RECEIVABLE);
          supplierRecordDTO.setCreditAmount(Math.abs(NumberUtil.doubleVal(payableList.get(0))));
          supplierRecordDTO.setDebt(Math.abs(returnList.get(0)));
          supplierRecordService.saveOrUpdateSupplierRecord(supplierRecordDTO);
        }

      } else if (statementAccountOrderDTO.getOrderType() == OrderTypes.SUPPLIER_STATEMENT_ACCOUNT) {
        ISupplierRecordService supplierRecordService = ServiceManager.getService(ISupplierRecordService.class);
        SupplierRecordDTO supplierRecordDTO = supplierRecordService.getSupplierRecordDTOBySupplierId(shopId, statementAccountOrderDTO.getCustomerOrSupplierId());
        List<Double> payableList= ServiceManager.getService(ISupplierPayableService.class).getSumPayableBySupplierId(statementAccountOrderDTO.getCustomerOrSupplierId(),shopId, OrderDebtType.SUPPLIER_DEBT_PAYABLE);
        List<Double> returnList = ServiceManager.getService(ISupplierPayableService.class).getSumPayableBySupplierId(Long.valueOf(statementAccountOrderDTO.getCustomerOrSupplierId()), shopId,OrderDebtType.SUPPLIER_DEBT_RECEIVABLE);
        supplierRecordDTO.setCreditAmount(Math.abs(NumberUtil.doubleVal(payableList.get(0))));
        supplierRecordDTO.setDebt(Math.abs(returnList.get(0)));
        supplierRecordService.saveOrUpdateSupplierRecord(supplierRecordDTO);
        if(supplierDTO!=null && supplierDTO.getCustomerId()!=null){
          CustomerRecordDTO customerRecordDTO = userService.getShopCustomerRecordByCustomerId(shopId, supplierDTO.getCustomerId()).toDTO();
          Double totalPayable = ServiceManager.getService(ISupplierPayableService.class).getSumReceivableByCustomerId(supplierDTO.getCustomerId(), shopId, OrderDebtType.CUSTOMER_DEBT_PAYABLE);
          customerRecordDTO.setTotalPayable(Math.abs(NumberUtil.doubleVal(totalPayable)));
          Double receivable = ServiceManager.getService(ISupplierPayableService.class).getSumReceivableByCustomerId(supplierDTO.getCustomerId(), shopId, OrderDebtType.CUSTOMER_DEBT_RECEIVABLE);
          customerRecordDTO.setTotalReceivable(NumberUtil.toReserve(receivable,NumberUtil.MONEY_PRECISION));
          customerRecordDTO.setRepayDate(statementAccountOrderDTO.getPaymentTime());
          if (NumberUtil.doubleVal(statementAccountOrderDTO.getMemberAmount()) > 0) {
            customerRecordDTO.setMemberConsumeTimes(NumberUtil.longValue(customerRecordDTO.getMemberConsumeTimes()) + 1);
            customerRecordDTO.setMemberConsumeTotal(NumberUtil.doubleVal(customerRecordDTO.getMemberConsumeTotal()) + NumberUtil.doubleVal(statementAccountOrderDTO.getMemberAmount()));
          }
          userService.updateCustomerRecord(customerRecordDTO);
        }
      }
      //by qxy move to thread
//      List<StatementAccountOrderDTO> statementAccountOrderDTOList = statementAccountOrderDTO.getOrderDTOList();
//      for (StatementAccountOrderDTO orderDTO : statementAccountOrderDTOList) {
//        Long orderId = orderDTO.getOrderId();
//        OrderTypes orderTypes = orderDTO.getOrderType();
//        ServiceManager.getService(OrderSolrWriterService.class).reCreateOrderSolrIndex(ServiceManager.getService(IConfigService.class).getShopById(shopId), orderTypes, orderId);
//        if (statementAccountOrderDTO.getOrderType() == OrderTypes.CUSTOMER_STATEMENT_ACCOUNT) {
//          ServiceManager.getService(IItemIndexService.class).updateItemIndexArrearsAndPaymentTime(orderId, 0D, statementAccountOrderDTO.getPaymentTime());
//        } else if (orderDTO.getOrderType() == OrderTypes.INVENTORY) {
//          ServiceManager.getService(IOperationLogService.class).saveOperationLog(
//              new OperationLogDTO(statementAccountOrderDTO.getShopId(), statementAccountOrderDTO.getSalesManId(), orderDTO.getOrderId(), ObjectTypes.INVENTORY_ORDER, OperationTypes.SETTLE));
//        }
//      }
      result.setOperation(statementAccountOrderDTO.getId().toString());

      //by qxy move to thread
//      ServiceManager.getService(IOrderSolrWriterService.class).reCreateOrderSolrIndex(ServiceManager.getService(IConfigService.class).getShopById(shopId),statementAccountOrderDTO.getOrderType(),statementAccountOrderDTO.getId());

      //在线程里对单据，客户，做reindex
      BcgogoEventPublisher bcgogoEventPublisher = new BcgogoEventPublisher();
      StatementAccountEvent statementAccountEvent = new StatementAccountEvent(statementAccountOrderDTO);
      bcgogoEventPublisher.publisherStatementAccount(statementAccountEvent);
      statementAccountEvent.setMainFlag(true);

    } catch (Exception e) {
      LOG.error("statementAccount.do method=settleStatementAccountOrder");
      LOG.error(e.getMessage(), e);
    }
    return result;
  }

  /**
   * ajax查询结算人信息
   *
   * @param model
   * @param request
   * @param operator
   * @param customerOrSupplierIdStr
   */
  @ResponseBody
  @RequestMapping(params = "method=getOperatorByCustomerOrSupplierId")
  public Object getOperatorByCustomerOrSupplierId(ModelMap model, HttpServletRequest request, String operator, String customerOrSupplierIdStr) {
    Long shopId = WebUtil.getShopId(request);
    List<StatementAccountOrderDTO> statementAccountOrderDTOList = new ArrayList<StatementAccountOrderDTO>();
    try {
      if (shopId == null) {
        return "/";
      }
      Long customerOrSupplierId = null;
      if (StringUtils.isNotEmpty(customerOrSupplierIdStr)) {
        customerOrSupplierId = Long.valueOf(customerOrSupplierIdStr);
      }
      IStatementAccountService statementAccountService = ServiceManager.getService(IStatementAccountService.class);
      statementAccountOrderDTOList = statementAccountService.getOperatorByCustomerOrSupplierId(shopId, customerOrSupplierId, operator);
      if (CollectionUtil.isNotEmpty(statementAccountOrderDTOList)) {
        return statementAccountOrderDTOList;
      }
    } catch (Exception e) {
      LOG.error("statement.do getOperatorByCustomerOrSupplierId :shopId" + shopId);
      LOG.error(e.getMessage(), e);
    }
    return new ArrayList<StatementAccountOrderDTO>();
  }

  @RequestMapping(params = "method=showStatementAccountOrderById")
  public String showStatementAccountOrderById(ModelMap model, HttpServletRequest request, @RequestParam("statementOrderId") String statementOrderIdStr) {
    Long shopId = WebUtil.getShopId(request);
    OrderTypes orderTypes = null;
    String customerOrSupplierIdStr = request.getParameter("customerOrSupplierIdStr");
    String orderTypeStr = request.getParameter("orderTypeStr");
    try {
      if (shopId == null) {
        return "/";
      }


      if (StringUtil.isEmpty(statementOrderIdStr) || !NumberUtil.isNumber(statementOrderIdStr)) {
        LOG.error("statement.do?method=showStatementAccountOrderById statementAccountOrderDTO is error " + statementOrderIdStr);
        return "/";
      }
      IStatementAccountService statementAccountService = ServiceManager.getService(IStatementAccountService.class);
      StatementAccountOrderDTO statementAccountOrderDTO = statementAccountService.getStatementOrderInfo(Long.valueOf(statementOrderIdStr));
      if (statementAccountOrderDTO == null) {
        LOG.error("statement.do?method=showStatementAccountOrderById statementAccountOrderDTO is null " + statementOrderIdStr);
        return "/";
      }
      customerOrSupplierIdStr = statementAccountOrderDTO.getCustomerOrSupplierId().toString();
      orderTypes = statementAccountOrderDTO.getOrderType();

      String orderType = statementAccountOrderDTO.getOrderType().toString();
      model.addAttribute("orderType", orderType);
      statementAccountOrderDTO.setPrint(request.getParameter("print"));
      model.addAttribute("statementAccountOrderDTO", statementAccountOrderDTO);

      //如果是供应商对账单 判断是否是关联供应商
      if(orderTypes == OrderTypes.SUPPLIER_STATEMENT_ACCOUNT) {
        ISupplierCommentService supplierCommentService = ServiceManager.getService(ISupplierCommentService.class);
        Long supplierShopId = supplierCommentService.getSupplierShopIdBySupplierId(Long.valueOf(customerOrSupplierIdStr));
        if (supplierShopId != null) {
          model.addAttribute("supplierShopId", supplierShopId);
        }
      }

    } catch (Exception e) {
      LOG.error("statement.do method=showStatementAccountOrderById,shopId" + shopId + ",customerOrSupplierIdStr" + customerOrSupplierIdStr + ",orderType" + orderTypeStr);
      LOG.error(e.getMessage(), e);

      if (orderTypes == OrderTypes.CUSTOMER_STATEMENT_ACCOUNT) {
        return "redirect:statementAccount.do?method=redirectSearchCustomerBill&type=" + StatementAccountConstant.CUSTOMER_TYPE + "&customerOrSupplierIdStr=" + Long.valueOf(customerOrSupplierIdStr);
      } else {
        return "redirect:statementAccount.do?method=redirectSearchCustomerBill&type=" + StatementAccountConstant.SUPPLIER_TYPE + "&customerOrSupplierIdStr=" + Long.valueOf(customerOrSupplierIdStr);

      }
    }
    return "/orderAccount/statementOrderFinish";
  }

  @RequestMapping(params = "method=print")
  public void print(HttpServletRequest request,HttpServletResponse response) throws Exception
  {
    String idStr = request.getParameter("id");
    if(StringUtils.isBlank(idStr))
    {
      return;
    }

    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    IPrintService printService = ServiceManager.getService(IPrintService.class);
    PrintWriter out = response.getWriter();
    try{

      IStatementAccountService statementAccountService = ServiceManager.getService(IStatementAccountService.class);
      StatementAccountOrderDTO statementAccountOrderDTO = statementAccountService.getStatementOrderInfo(Long.valueOf(idStr));
      PrintTemplateDTO printTemplateDTO = printService.getSinglePrintTemplateDTOByShopIdAndType(WebUtil.getShopId(request), OrderTypes.CUSTOMER_SUPPLIER_STATEMENT_ACCOUNT);


      response.setContentType("text/html");
      response.setCharacterEncoding("UTF-8");

      if(null != printTemplateDTO) {
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

        String myTemplateName = OrderTypes.CUSTOMER_SUPPLIER_STATEMENT_ACCOUNT.toString()+ valueOf(WebUtil.getShopId(request));

        String myTemplate =  str;

        //模板资源存放 资源库 中

        repo.putStringResource(myTemplateName, myTemplate);

        //从资源库中加载模板

        Template template = ve.getTemplate(myTemplateName);

        //取得velocity的模版
        Template t = ve.getTemplate(myTemplateName,"UTF-8");
        //取得velocity的上下文context
        VelocityContext context = new VelocityContext();

        //把数据填入上下文
        context.put("orderDTO", statementAccountOrderDTO);

        //输出流
        StringWriter writer = new StringWriter();

        //转换输出
        t.merge(context, writer);
        out.print(writer);
        writer.close();
      } else {
        out.print("<html><head><title></title></head><body>没有可用的模板</body><html>");
      }

    } catch (Exception e) {
      LOG.debug("statementAccount.do?method=print");
      LOG.error(e.getMessage(),e);
    }finally {
      out.close();
    }
  }
}
