package com.bcgogo.customer;

import com.bcgogo.common.Pager;
import com.bcgogo.common.StringUtil;
import com.bcgogo.common.WebUtil;
import com.bcgogo.config.cache.ServiceCategoryCache;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.service.IServiceCategoryService;
import com.bcgogo.config.service.image.IImageService;
import com.bcgogo.config.upyun.UpYunManager;
import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.enums.*;
import com.bcgogo.enums.config.ImageScene;
import com.bcgogo.enums.config.ServiceCategoryType;
import com.bcgogo.enums.user.ServiceCategoryDataType;
import com.bcgogo.enums.user.VehicleBrandModelDataType;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.exception.PageException;
import com.bcgogo.generator.SupplierDTOGenerator;
import com.bcgogo.notification.dto.CustomerRemindSms;
import com.bcgogo.notification.dto.OutBoxDTO;
import com.bcgogo.notification.service.INotificationService;
import com.bcgogo.notification.service.ISmsService;
import com.bcgogo.product.StandardBrandModelCache.StandardBrandModelCache;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.product.productCategoryCache.ProductCategoryCache;
import com.bcgogo.product.service.IProductService;
import com.bcgogo.search.dto.ItemIndexDTO;
import com.bcgogo.search.dto.OrderDTO;
import com.bcgogo.search.service.ISearchService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.*;
import com.bcgogo.txn.dto.StatementAccount.OrderDebtType;
import com.bcgogo.txn.model.Service;
import com.bcgogo.txn.service.*;
import com.bcgogo.txn.service.recommend.IPreciseRecommendService;
import com.bcgogo.txn.service.solr.ICustomerOrSupplierSolrWriteService;
import com.bcgogo.txn.service.supplierComment.ISupplierCommentService;
import com.bcgogo.user.CustomerVehicleResponse;
import com.bcgogo.user.dto.*;
import com.bcgogo.user.model.CustomerServiceJob;
import com.bcgogo.user.model.task.CusOrSupOrderIndexSchedule;
import com.bcgogo.user.service.*;
import com.bcgogo.utils.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
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
import java.math.BigInteger;
import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: monrove
 * Date: 11-12-14
 * Time: 下午5:38
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/unitlink.do")
public class UnitLinkController {

  public static final Logger LOG = LoggerFactory.getLogger(UnitLinkController.class);
  public static final int pageSize = 5;

  @Autowired
  private SupplierDTOGenerator supplierDTOGenerator;

  @RequestMapping(params = "method=index")
  public String index(HttpServletRequest request) throws Exception {
    String ucmValue = request.getParameter("ucmValue");
    request.setAttribute("ucmValue", ucmValue);
    //中文
    if (ucmValue != null) {
      try {
        ucmValue = URLDecoder.decode(ucmValue, "UTF-8");
      } catch (Exception e) {
        LOG.debug("/unitlink.do");
        LOG.debug("method=index");
        LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
        LOG.error(e.getMessage(), e);
      }
      request.setAttribute("searchKey", ucmValue);
    }
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    if (shopId == null) {
      return "customer/uncleIndex";
    }
    return "customer/uncleIndex";
  }

  /**
   * “单位/联系人/手机号”搜索框进入页面异步取客户信息列表
   *
   * @param request
   * @param response
   * @param startPageNo
   * @param maxRows
   * @param countStr
   * @param ucmValue
   * @throws BcgogoException
   */
  @RequestMapping(params = "method=customerResponse")
  public void customerResponse(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap, Integer startPageNo, Integer maxRows, String countStr, String ucmValue) throws BcgogoException {
    IUserService userService = ServiceManager.getService(IUserService.class);
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    ucmValue = request.getParameter("ucmValue");
    String jsonStr = "";
    //中文
    if (ucmValue != null) {
      try {
        ucmValue = URLDecoder.decode(ucmValue, "UTF-8");
      } catch (Exception e) {
        LOG.debug("/unitlink.do");
        LOG.debug("method=customerResponse");
        LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
        LOG.debug("startPageNo:" + startPageNo + ",maxRows:" + maxRows + ",countStr:" + countStr + ",ucmValue:" + ucmValue);
        LOG.error(e.getMessage(), e);
      }
      request.setAttribute("searchKey", ucmValue);
    }
    //获取客户相关信息
    ICustomerService customerService = ServiceManager.getService(ICustomerService.class);
    int customerSize = customerService.countCustomerRecordByKey(shopId, ucmValue);    //客户总数
    List<CustomerRecordDTO> customerRecordDTOList = null;
    Pager pager = null;
//    if (customerSize <= 0) {
//      return;
//    }
    try {
      if (maxRows == null || maxRows == 0) maxRows = pageSize;
      pager = new Pager(customerSize, NumberUtil.intValue(String.valueOf(startPageNo), 1), maxRows);
    } catch (PageException e) {
      LOG.debug("/unitlink.do");
      LOG.debug("method=customerResponse");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.debug("startPageNo:" + startPageNo + ",maxRows:" + maxRows + ",countStr:" + countStr + ",ucmValue:" + ucmValue);
      LOG.error(e.toString(), e);
    }
    customerRecordDTOList = customerService.getCustomerRecordInfoByKey(shopId, ucmValue, pager.getRowStart(), pager.getPageSize());
    jsonStr = JsonUtil.listToJson(customerRecordDTOList);
    modelMap.addAttribute("jsonStr", jsonStr);
    jsonStr = jsonStr.substring(0, jsonStr.length() - 1);
    if (!"[".equals(jsonStr.trim())) {
      jsonStr = jsonStr + "," + pager.toJson().substring(1, pager.toJson().length());
    } else {
      jsonStr = pager.toJson();
    }
    try {
      PrintWriter writer = response.getWriter();
      writer.write(jsonStr);
      writer.close();
    } catch (Exception e) {
      LOG.debug("/unitlink.do");
      LOG.debug("method=customerResponse");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.debug("startPageNo:" + startPageNo + ",maxRows:" + maxRows + ",countStr:" + countStr + ",ucmValue:" + ucmValue);
      LOG.error(e.getMessage(), e);
    }

  }

  /**
   * 单位/联系人/手机号 异步取供应商信息
   *
   * @param request
   * @param response
   * @param startPageNo
   * @param maxRows
   * @param countStr
   * @param ucmValue
   * @throws Exception
   */
  @RequestMapping(params = "method=supplierResponse")
  public void supplierResponse(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap, Integer startPageNo, Integer maxRows, String countStr, String ucmValue) throws Exception {
    IUserService userService = ServiceManager.getService(IUserService.class);
    ISearchService searchService = ServiceManager.getService(ISearchService.class);
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    ucmValue = request.getParameter("ucmValue");
    String jsonStr = "";
    //中文
    if (ucmValue != null) {
      try {
        ucmValue = java.net.URLDecoder.decode(ucmValue, "UTF-8");
      } catch (Exception e) {
        LOG.debug("/unitlink.do");
        LOG.debug("method=supplierResponse");
        LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
        LOG.debug("startPageNo:" + startPageNo + ",maxRows:" + maxRows + ",countStr:" + countStr + ",ucmValue:" + ucmValue);
        LOG.error(e.getMessage(), e);
      }
      request.setAttribute("searchKey", ucmValue);
    }
    int totalCount = userService.countShopSupplierByKey(shopId, ucmValue);
    Pager pager = new Pager(totalCount, NumberUtil.intValue(String.valueOf(startPageNo), 1), pageSize);
    ISupplierService supplierService = ServiceManager.getService(ISupplierService.class);
    List<SupplierDTO> supplierDTOList = supplierService.getSupplierInfoList(shopId, ucmValue, pager.getCurrentPage(), pager.getPageSize());
    for (SupplierDTO supplierDTO : supplierDTOList) {
      if ((supplierDTO.getLastOrderProducts() == null || "".equals(supplierDTO.getLastOrderProducts())) && null != supplierDTO.getLastOrderId()) {
        supplierDTO.setLastOrderProducts(searchService.getOrderNamesByOrderId(shopId, supplierDTO.getLastOrderId()));
      }
    }
    jsonStr = JsonUtil.listToJson(supplierDTOList);
    modelMap.addAttribute("jsonStr", jsonStr);
    jsonStr = jsonStr.substring(0, jsonStr.length() - 1);
    if (!"[".equals(jsonStr.trim())) {
      jsonStr = jsonStr + "," + pager.toJson().substring(1, pager.toJson().length());
    } else {
      jsonStr = pager.toJson();
    }

    try {
      PrintWriter writer = response.getWriter();
      writer.write(jsonStr);
      writer.close();
    } catch (Exception e) {
      LOG.debug("/unitlink.do");
      LOG.debug("method=supplierResponse");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.debug("startPageNo:" + startPageNo + ",maxRows:" + maxRows + ",countStr:" + countStr + ",ucmValue:" + ucmValue);
      LOG.error(e.getMessage(), e);
    }
  }

  @RequestMapping(params = "method=tippage")
  public String tipPage(HttpServletRequest request) {
    return "/customer/tippage";
  }

  /**
   * 获取客户详细信息（包括关联的车辆和消费记录信息）
   *
   * @param request
   * @return
   * @throws Exception
   */
  @RequestMapping(params = "method=customer")
  public String customer(HttpServletRequest request,Long customerId,String uvmValue) {
    Long shopId=WebUtil.getShopId(request);
    try {
      if (shopId == null) throw new Exception("shop id is null!");

      // //获取区域、客户类型、发票类型、结算方式获取页面下拉框信息
      Map<String, String> invoiceCatagoryMap = TxnConstant.getInvoiceCatagoryMap(request.getLocale());
      Map<String, String> areaMap = TxnConstant.getAreaMap(request.getLocale());
      Map<String, String> customerTypeMap = TxnConstant.getCustomerTypeMap(request.getLocale());
      Map<String, String> settlementTyoeMap = TxnConstant.getSettlementTypeMap(request.getLocale());
      request.setAttribute("invoiceCatagoryMap", invoiceCatagoryMap);
      request.setAttribute("areaMap", areaMap);
      request.setAttribute("customerTypeMap", customerTypeMap);
      request.setAttribute("settlementTypeMap", settlementTyoeMap);
      request.setAttribute("today", DateUtil.getTodayStr(DateUtil.YEAR_MONTH_DATE));


      IUserService userService = ServiceManager.getService(IUserService.class);
      IMembersService membersService = ServiceManager.getService(IMembersService.class);
      RFITxnService txnService = ServiceManager.getService(RFITxnService.class);
      IConfigService configService = ServiceManager.getService(IConfigService.class);
      IImageService imageService = ServiceManager.getService(IImageService.class);
      ICustomerService customerService = ServiceManager.getService(ICustomerService.class);

      Boolean isMemberSwitchOn = WebUtil.checkMemberStatus(request);
      Boolean wholesalerVersion = ConfigUtils.isWholesalerVersion(WebUtil.getShopVersionId(request));
      request.setAttribute("wholesalerVersion", wholesalerVersion);
      request.setAttribute("isMemberSwitchOn",isMemberSwitchOn);
      CustomerDTO customerDTO = null;
      //客户ID为空时表示新客户
      if (customerId==null && StringUtils.isNotBlank(uvmValue)) {
        customerDTO = new CustomerDTO();
        customerDTO.setShopId(shopId);
        customerDTO.setName(uvmValue);
        customerDTO = userService.createCustomer(customerDTO);
        CustomerRecordDTO customerRecordDTO = new CustomerRecordDTO();
        customerRecordDTO.setShopId(shopId);
        customerRecordDTO.setCustomerId(customerDTO.getId());
        customerRecordDTO.setName(customerDTO.getName());
        userService.createCustomerRecord(customerRecordDTO);
        customerId = customerDTO.getId();
      }else{
        customerDTO = userService.getCustomerDTOByCustomerId(customerId, shopId);
        customerDTO.setHasMainContact(true);
      }

      if(customerDTO!=null){
        customerDTO.setCustomerKindStr(customerTypeMap.get(customerDTO.getCustomerKind()));
        customerDTO.setSettlementTypeStr(settlementTyoeMap.get(String.valueOf(customerDTO.getSettlementType())));
        customerDTO.setInvoiceCategoryStr(invoiceCatagoryMap.get(String.valueOf(customerDTO.getInvoiceCategory())));
        //消费信息
        CustomerRecordDTO customerRecordDTO = userService.getCustomerRecordDTOByCustomerIdAndShopId(shopId,customerId);
        customerDTO.setCustomerRecordDTO(customerRecordDTO);

        //应付款总额
        Double totalPayable = ServiceManager.getService(ISupplierPayableService.class).getSumReceivableByCustomerId(customerDTO.getId(), shopId, OrderDebtType.CUSTOMER_DEBT_PAYABLE);
        if (customerDTO.getSupplierId() != null) {
          List<Double> payables = ServiceManager.getService(ISupplierPayableService.class).getSumPayableBySupplierId(customerDTO.getSupplierId(), shopId, OrderDebtType.SUPPLIER_DEBT_PAYABLE);
          Double supplierPayable = 0.0;
          if (payables != null) {
            supplierPayable = payables.get(0);
          }
          totalPayable = NumberUtil.round(-NumberUtil.doubleVal(totalPayable) + NumberUtil.doubleVal(supplierPayable), NumberUtil.MONEY_PRECISION);
        }

        totalPayable  = NumberUtil.round(Math.abs(NumberUtil.doubleVal(totalPayable)), NumberUtil.MONEY_PRECISION);
        customerDTO.setTotalReturnDebt(totalPayable);
        request.setAttribute("totalPayable", totalPayable);

        //应收款总额
        Double receivable = ServiceManager.getService(ISupplierPayableService.class).getSumReceivableByCustomerId(customerDTO.getId(), shopId, OrderDebtType.CUSTOMER_DEBT_RECEIVABLE);
        if (customerDTO.getSupplierId() != null) {
          List<Double> returnList = ServiceManager.getService(ISupplierPayableService.class).getSumPayableBySupplierId(Long.valueOf(customerDTO.getSupplierId()), shopId, OrderDebtType.SUPPLIER_DEBT_RECEIVABLE);
          if (returnList != null) {
            receivable = NumberUtil.round(0 - returnList.get(0), NumberUtil.MONEY_PRECISION) + NumberUtil.round(receivable, NumberUtil.MONEY_PRECISION);
          }
        }
        customerDTO.setTotalReceivable(NumberUtil.round(receivable,NumberUtil.MONEY_PRECISION));
        request.setAttribute("totalReceivable", NumberUtil.round(receivable, NumberUtil.MONEY_PRECISION));


        if (wholesalerVersion) {

          customerService.addAreaInfoToCustomerDTO(customerDTO);

          if (!StringUtil.isEmpty(customerDTO.getAreaInfo()) && (!StringUtil.isEmpty(customerDTO.getAddress()))) {
            if (customerDTO.getAddress().indexOf(customerDTO.getAreaInfo()) != -1) {
              customerDTO.setAddress(customerDTO.getAddress().replaceAll(customerDTO.getAreaInfo(), ""));
            }
          }
          //经营范围
          List<BusinessScopeDTO> businessScopeDTOList = userService.getCustomerSupplierBusinessScope(customerDTO.getShopId(), customerDTO.getId(), null);
          customerDTO.setBusinessScopeInfo(businessScopeDTOList,ProductCategoryCache.getNode());
          //预付款
          ICustomerDepositService customerDepositService = ServiceManager.getService(ICustomerDepositService.class);
          CustomerDepositDTO customerDepositDTO = customerDepositService.queryCustomerDepositByShopIdAndCustomerId(shopId, customerDTO.getId());
          if (customerDepositDTO != null) {
            request.setAttribute("totalCustomerDeposit", customerDepositDTO.getActuallyPaid());
          } else{
            customerDepositDTO = new CustomerDepositDTO();
            customerDepositDTO.setActuallyPaid(0.00);
            customerDepositDTO.setBankCardAmount(0.00);
            customerDepositDTO.setCheckAmount(0.00);
            customerDepositDTO.setCash(0.00);
            customerDepositDTO.setShopId(shopId);
            customerDepositDTO.setCustomerId(customerDTO.getId());
            customerDepositService.saveCustomerDeposit(customerDepositDTO);
            //reindex customer in solr
            ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexCustomerByCustomerId(customerDTO.getId());
            request.setAttribute("totalCustomerDeposit", customerDepositDTO.getActuallyPaid());
          }
          if (customerDTO.getSupplierId() != null && !customerDTO.getIsOnlineShop()) {
            SupplierDTO supplierDTO = CollectionUtil.getFirst(userService.getSupplierById(shopId, customerDTO.getSupplierId()));
            if (supplierDTO != null && supplierDTO.getIsOnlineShop()) {
              customerDTO.setOnlineShop(supplierDTO.getIsOnlineShop());
            }
          }

          IVehicleBrandModelRelationService vehicleBrandModelRelationService =ServiceManager.getService(IVehicleBrandModelRelationService.class);
          List<VehicleBrandModelRelationDTO> vehicleBrandModelRelationDTOList = vehicleBrandModelRelationService.getVehicleBrandModelRelationDTOByDataId(shopId,customerDTO.getId(), VehicleBrandModelDataType.CUSTOMER);
          customerDTO.setVehicleBrandModelRelationInfo(vehicleBrandModelRelationDTOList);

          IServiceCategoryRelationService serviceCategoryRelationService = ServiceManager.getService(IServiceCategoryRelationService.class);
          List<ServiceCategoryRelationDTO> serviceCategoryRelationDTOList = serviceCategoryRelationService.getServiceCategoryDTOByDataId(shopId, customerDTO.getId(), ServiceCategoryDataType.CUSTOMER);
          customerDTO.setServiceCategoryRelationInfo(serviceCategoryRelationDTOList);

          if(!customerDTO.getIsOnlineShop()){
            request.setAttribute("serviceCategoryDTOList", ServiceCategoryCache.getServiceCategoryDTOListByType(ServiceCategoryType.SECOND_CATEGORY));
          }

          //查询店铺与客户的临时关系
          String shopStatus = null;
          Long newShopId = configService.getTempShopIdByCustomerId(customerDTO.getId());
          String customerShopState = configService.getCustomerShopStatus(customerDTO.getId());
          if (newShopId == null) {
            if (customerShopState == null) {
              shopStatus = CustomerShopStatus.NONE_REGISTERED.toString();//临时关系表中无记录，并且shop表中也无记录，定义为“未注册”
            } else {
              shopStatus = CustomerShopStatus.REGISTERED.toString();//临时关系表中无记录，并且shop表中有记录，定义为“已注册”
            }
          } else {
            shopStatus = CustomerShopStatus.APPROVALING.toString();//临时关系表有记录，定义为“待审批”
          }
          request.setAttribute("shopStatus", shopStatus);

        }else{
          //会员信息
          if(isMemberSwitchOn){
            MemberDTO memberDTO = membersService.getMemberByCustomerId(shopId, customerDTO.getId());
            if (memberDTO != null) {
              if (memberDTO.getMemberServiceDTOs() != null) {
                for (MemberServiceDTO memberServiceDTO : memberDTO.getMemberServiceDTOs()) {
                  Service service = txnService.getServiceById(memberServiceDTO.getServiceId());
                  if (service != null) {
                    memberServiceDTO.setServiceName(service.getName());
                  }
                }
              }
              memberDTO.setMemberConsumeTotal(NumberUtil.toReserve(customerRecordDTO.getMemberConsumeTotal(),NumberUtil.MONEY_PRECISION));
              memberDTO.setMemberConsumeTimes(NumberUtil.longValue(customerRecordDTO.getMemberConsumeTimes()));
              request.setAttribute("memberDTO", memberDTO);
              MemberStatus memberStatus = membersService.getMemberStatusByMemberDTO(memberDTO);
              request.setAttribute("memberStatus", memberStatus);
            }
          }

          //证件照
          List<ImageScene> imageSceneList = new ArrayList<ImageScene>();
          imageSceneList.add(ImageScene.CUSTOMER_IDENTIFICATION_IMAGE);
          imageService.addImageToCustomerDTO(imageSceneList,false,customerDTO);

          //本店服务
          IServiceCategoryService serviceCategoryService = ServiceManager.getService(IServiceCategoryService.class);
          Map<Long, String> serviceScopeMap = serviceCategoryService.getShopServiceCategoryIdNameMap(shopId);
          request.setAttribute("serviceScope", serviceScopeMap);

          //客户拥有的车辆信息
          IVehicleService vechicleService = ServiceManager.getService(IVehicleService.class);
          List<CustomerVehicleResponse> customerVehicleResponseList = vechicleService.findVehicleListByCustomerId(customerDTO.getId());
          request.setAttribute("customerVehicleResponseList", customerVehicleResponseList);
          request.setAttribute("upYunFileDTO",UpYunManager.getInstance().generateDefaultUpYunFileDTO(shopId));
        }
      }
      request.setAttribute("customerDTO", customerDTO);
      request.setAttribute("customerId",customerDTO==null?null:customerDTO.getId());

      //客户详细信息对账单
      request.setAttribute("customerOrSupplierId",customerDTO==null?null:customerDTO.getId());
      request.setAttribute("orderType",OrderTypes.CUSTOMER_STATEMENT_ACCOUNT.toString());
      request.setAttribute("orderTypeStr",OrderTypes.CUSTOMER_STATEMENT_ACCOUNT.toString());

      request.setAttribute("fromPage", request.getParameter("fromPage"));
      request.setAttribute("vehicleId", request.getParameter("vehicleId"));
      request.setAttribute("vehicleEdit", request.getParameter("vehicleEdit"));
      request.setAttribute("fourSShopVersions", ConfigUtils.isFourSShopVersion(WebUtil.getShopVersionId(request)));
      if (wholesalerVersion) {
        return "customerDetail/wholesalerCustomerDetail";
      }else{
        return "customerDetail/customerDetail";
      }

    }catch (Exception e){
      LOG.debug("/unitlink.do?method=customer");
      LOG.error("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId")+",customerId:" + customerId);
      LOG.error(e.getMessage(), e);
    }
    return "/";
  }

  /**
   * 客户管理-供应商资料-供应商信息
   *
   * @param request
   * @return
   * @throws BcgogoException
   */
  @RequestMapping(params = "method=supplier")
  public String supplier(HttpServletRequest request,Long supplierId,String uvmValue) {
    Long shopId = null;
    try {
      shopId = WebUtil.getShopId(request);
      if(shopId==null) throw new Exception("shopId is null!!");
      //正常进入供应商详情页面
      IUserService userService = ServiceManager.getService(IUserService.class);
      ISupplierService supplierService = ServiceManager.getService(ISupplierService.class);
      ISupplierPayableService supplierPayableService = ServiceManager.getService(ISupplierPayableService.class);
      //客户类型
      Map<String,String> categoryList = TxnConstant.getCustomerTypeMap(request.getLocale());
      //结算方式
      Map<String,String> settlementTypeList = TxnConstant.getSettlementTypeMap(request.getLocale());
      //发票类型
      Map<String,String> invoiceCategoryList = TxnConstant.getInvoiceCatagoryMap(request.getLocale());
      request.setAttribute("categoryList", categoryList);
      request.setAttribute("settlementTypeList", settlementTypeList);
      request.setAttribute("invoiceCategoryList", invoiceCategoryList);
      request.setAttribute("wholesalerVersion", ConfigUtils.isWholesalerVersion(WebUtil.getShopVersionId(request)));
      //供应商基本信息
      SupplierDTO supplierDTO = null;
      Double totalPayable = 0d,totalDeposit=0d,totalTradeAmount=0d,totalReceivable=0d,totalReturnAmount=0d;
      int consumeTimes = 0;
      if (supplierId != null) {
        supplierDTO = CollectionUtil.uniqueResult(userService.getSupplierById(shopId, supplierId));
        if(supplierDTO==null) throw new Exception("can`t find supplierDTO by supplierId["+supplierId+"]!!");

        List<Double> doubleList = supplierPayableService.getPayableConsumeTimesBySupplierId(supplierId, shopId, OrderDebtType.SUPPLIER_DEBT_PAYABLE);//应付款总额 实付总额
        consumeTimes = doubleList.get(2).intValue();

        //应付款总额
        totalPayable = doubleList.get(0);
        if (supplierDTO.getCustomerId() != null) {
          //即使客户又是供应商的时候
          if (!supplierDTO.getIsOnlineShop()) {
            CustomerDTO customerDTO = userService.getCustomerDTOByCustomerId(supplierDTO.getCustomerId(), shopId);
            if (customerDTO != null && customerDTO.getIsOnlineShop()) {
              supplierDTO.setOnlineShop(true);
            }
          }
          double payable = NumberUtil.doubleVal(supplierPayableService.getSumReceivableByCustomerId(supplierDTO.getCustomerId(), shopId, OrderDebtType.CUSTOMER_DEBT_PAYABLE));
          totalPayable = totalPayable - payable;
        }

        //定金总额
        totalDeposit = supplierPayableService.getSumDepositBySupplierId(supplierId, shopId);
        // add by WLF 累计交易金额
        totalTradeAmount = doubleList.get(0).doubleValue() + doubleList.get(1);

        List<Double> returnList = supplierPayableService.getSumPayableBySupplierId(Long.valueOf(supplierId), shopId, OrderDebtType.SUPPLIER_DEBT_RECEIVABLE);
        totalReceivable = 0 - returnList.get(0);
        if (supplierDTO.getCustomerId() != null) {
          Double receivable = supplierPayableService.getSumReceivableByCustomerId(supplierDTO.getCustomerId(), shopId, OrderDebtType.CUSTOMER_DEBT_RECEIVABLE);
          totalReceivable = totalReceivable + NumberUtil.doubleVal(receivable);
        }
        totalReturnAmount = 0 - (returnList.get(0) + returnList.get(1));

        //供应商点评
        if (supplierDTO.getSupplierShopId() != null) {
          ISupplierCommentService supplierCommentService = ServiceManager.getService(ISupplierCommentService.class);
          List<SupplierDTO> supplierDTOList = new ArrayList<SupplierDTO>();
          supplierDTOList.add(supplierDTO);
          supplierCommentService.setSupplierCommentStat(supplierDTOList, null);
        }
        //供应商的经营范围
        List<BusinessScopeDTO> businessScopeDTOList = userService.getCustomerSupplierBusinessScope(supplierDTO.getShopId(), null, supplierDTO.getId());
        supplierDTO.setBusinessScopeInfo(businessScopeDTOList,ProductCategoryCache.getNode());

        IVehicleBrandModelRelationService vehicleBrandModelRelationService =ServiceManager.getService(IVehicleBrandModelRelationService.class);
        List<VehicleBrandModelRelationDTO> vehicleBrandModelRelationDTOList = vehicleBrandModelRelationService.getVehicleBrandModelRelationDTOByDataId(shopId,supplierDTO.getId(), VehicleBrandModelDataType.SUPPLIER);
        supplierDTO.setVehicleBrandModelRelationInfo(vehicleBrandModelRelationDTOList);

      } else if (StringUtils.isNotBlank(uvmValue)) {
        supplierDTO = new SupplierDTO();
        supplierDTO.setName(uvmValue.trim());
        supplierDTO.setContact(uvmValue.trim()); // TODO zhuj
        supplierDTO.setShopId(shopId);
        userService.createSupplier(supplierDTO);
        ServiceManager.getService(ISupplierRecordService.class).createSupplierRecordUsingSupplierDTO(supplierDTO);
        ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexSupplierBySupplierId(supplierDTO.getId());
      }else{
        throw new Exception("supplierId and uvmValue is null");
      }
      request.setAttribute("totalReturnAmount", String.valueOf(NumberUtil.round(totalReturnAmount, NumberUtil.MONEY_PRECISION)));
      request.setAttribute("totalReceivable", String.valueOf(NumberUtil.round(totalReceivable, NumberUtil.MONEY_PRECISION)));
      request.setAttribute("totalTradeAmount", String.valueOf(NumberUtil.round(totalTradeAmount, NumberUtil.MONEY_PRECISION)));
      request.setAttribute("totalDeposit", String.valueOf(NumberUtil.round(totalDeposit, NumberUtil.MONEY_PRECISION)));
      request.setAttribute("totalPayable", String.valueOf(NumberUtil.round(totalPayable, NumberUtil.MONEY_PRECISION)));
      request.setAttribute("consumeTimes", consumeTimes);

      //供应商详情对账单
      request.setAttribute("customerOrSupplierId",supplierDTO==null?null:supplierDTO.getId());
      request.setAttribute("orderType",OrderTypes.SUPPLIER_STATEMENT_ACCOUNT.toString());
      request.setAttribute("orderTypeStr",OrderTypes.SUPPLIER_STATEMENT_ACCOUNT.toString());

      supplierService.addAreaInfoToSupplierDTO(supplierDTO);
      if(!StringUtil.isEmpty(supplierDTO.getAreaInfo()) && (!StringUtil.isEmpty(supplierDTO.getAddress()))){
        if(supplierDTO.getAddress().indexOf(supplierDTO.getAreaInfo()) != -1){
          supplierDTO.setAddress(supplierDTO.getAddress().replaceAll(supplierDTO.getAreaInfo(),""));
        }
      }

      supplierDTO.setSettlementType(settlementTypeList.get(String.valueOf(supplierDTO.getSettlementTypeId())));
      supplierDTO.setInvoiceCategory(invoiceCategoryList.get(String.valueOf(supplierDTO.getInvoiceCategoryId())));
      request.setAttribute("supplierDTO", supplierDTO);
    } catch (Exception e) {
      LOG.error("/unitlink.do?method=supplier");
      LOG.error("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId")+",supplierId:" + supplierId);
      LOG.error(e.getMessage(), e);
    }
    request.setAttribute("fromPage", request.getParameter("fromPage"));
    return "supplierDetail/supplierDetail";
  }

  /**
   * 新功能勿用  请使用  在customerController  method=saveOrUpdateCustomer
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  @Deprecated
  @RequestMapping(params = "method=updateCustomer")
  @ResponseBody
  public String updateCustomerInfo(HttpServletRequest request,HttpServletResponse response) throws Exception {         //双击编辑框修改
    String result = "success";
    String name = request.getParameter("name");              //单位 -- > 客户名
    String landline = request.getParameter("landline");     //座机
    String addr = request.getParameter("addr");              //地址
    String contact = request.getParameter("contact");       //联系人
    String mobile = request.getParameter("mobile");
    String qq = request.getParameter("qq");                  //qq
    String email = request.getParameter("email");            //邮件
    String code = request.getParameter("code");              //会员号
    String fax = request.getParameter("fax");                //传真
    String area = request.getParameter("area");              //区域
    String birth = request.getParameter("birthdayString"); //生日
    String bank = request.getParameter("bank");              //开户行
    String bankAccountName = request.getParameter("bankAccountName"); //开户名
    String account = request.getParameter("account");       //账号
    String memo = request.getParameter("memo");              //备注
    String invoiceCategory = request.getParameter("invoiceCategory"); //发票类型
    String settlementType = request.getParameter("settlementType");   //结算方式
    String customerKind = request.getParameter("customerKind");       //客户类型
    String shortName = request.getParameter("shortName");   //简称
    String identity = request.getParameter("identity"); //同时是供应商
    String supplierId = request.getParameter("supplierId");
    String thirdCategoryIdStr = request.getParameter("thirdCategoryIdStr");
    Boolean changeArea = false;    //是否是更新区域
    Long province = null;
    Long city = null;
    Long region = null;
    String address = request.getParameter("address");
    if (!"".equals(request.getParameter("province")) && null != request.getParameter("province")) {
      province = Long.parseLong(request.getParameter("province"));
    }
    if (!"".equals(request.getParameter("city")) && null != request.getParameter("city")) {
      city = Long.parseLong(request.getParameter("city"));
    }
    if (!"".equals(request.getParameter("region")) && null != request.getParameter("region")) {
      region = Long.parseLong(request.getParameter("region"));
    }

    boolean isCancel = StringUtils.isNotBlank(identity) && identity.trim().equals("cancelSupplier");

    //客户id
    String id = request.getParameter("customerId");
    Long customerId = null;
    boolean isOnlineShop = false;
    if (id != null && !"null".equals(id) && !"".equals(id)) {
      try {
        customerId = Long.parseLong(id.trim());
      } catch (Exception e) {
        LOG.debug("/unitlink.do");
        LOG.debug("method=updateCustomer");
        LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
        LOG.error(e.getMessage(), e);
      }
    }

    // 填充联系人列表 add by zhuj
    ContactDTO[] contactDTOs = SupplierDTOGenerator.fillContactArray(request);
    if (customerId != null) {
      if(!ArrayUtils.isEmpty(contactDTOs)){
        for (ContactDTO contactDTO: contactDTOs){
          if (contactDTO != null && contactDTO.isValidContact()) {
            contactDTO.setCustomerId(customerId);
          }
        }
      }
    }
    if (StringUtils.isNotBlank(supplierId)) {
      for (ContactDTO contactDTO: contactDTOs){
        if (contactDTO != null && contactDTO.isValidContact()) {
          contactDTO.setSupplierId(Long.parseLong(supplierId));
        }
      }
    }
    if(name == null) {
      name = request.getParameter("unit");
      if(StringUtils.isNotBlank(name)){
        LOG.error("还有客户名称通过unit 传参的error！！！");
      }
    }

    IUserService userService = ServiceManager.getService(IUserService.class);
    IPreciseRecommendService preciseRecommendService = ServiceManager.getService(IPreciseRecommendService.class);
    Long shopId = WebUtil.getShopId(request);
    Long cancelRelationSupplierId = null;   //需要更新供应商关联信息的供应商ID
    CustomerDTO customerDTO = null;
    CustomerRecordDTO customerRecordDTO = null;
    if (customerId != null && !"".equals(customerId) && !"null".equals(customerId)) {
      customerDTO = userService.getCustomerDTOByCustomerId(customerId,shopId);
      customerRecordDTO = userService.getCustomerRecordDTOByCustomerIdAndShopId(shopId,customerId);
    }

    if (customerDTO != null) {
      isOnlineShop = customerDTO.getIsOnlineShop();
      if(customerDTO.getSupplierId() != null && !isOnlineShop){
        SupplierDTO supplierDTO = CollectionUtil.getFirst(userService.getSupplierById(shopId,customerDTO.getSupplierId()));
        if(supplierDTO != null && supplierDTO.getIsOnlineShop()){
          isOnlineShop = supplierDTO.getIsOnlineShop();
        }
      }
      customerDTO.setShopId(shopId);
      if (!isCancel){   // add by zhuj cancel的时候 会导致customerDTO里面的contacts列表都是无用的
        customerDTO.setContacts(contactDTOs);
      }
      //online shop not update this field
      if (!isOnlineShop) {
        if (name != null) {
          customerDTO.setName(name);  //单位--客户名
        }
        if (mobile != null) {
          customerDTO.setMobile(mobile);
        }
        if (addr != null) {
          customerDTO.setAddress(addr);
        }
        if (shortName != null) {
          customerDTO.setShortName(shortName);
        }
        if (province != null) {
          if (customerDTO.getProvince() != province) {
            changeArea = true;
          }
          customerDTO.setProvince(province);
        }
        if (city != null) {
          if (customerDTO.getCity() != city) {
            changeArea = true;
          }
          customerDTO.setCity(city);
        }
        if (customerDTO.getRegion() != region) {
          changeArea = true;
        }
        customerDTO.setRegion(region);

        if (address != null) {
          customerDTO.setAddress(address);
        }
        if(thirdCategoryIdStr != null){
          customerDTO.setThirdCategoryIdStr(thirdCategoryIdStr);
        }
      }
      if (landline != null) {
        customerDTO.setLandLine(landline);
      }
      if (fax != null) {
        customerDTO.setFax(fax);
      }

      if (area != null) {
        customerDTO.setArea(area);
      }
      if (contact != null) {
        customerDTO.setContact(contact);
      }

      if (qq != null) {
        customerDTO.setQq(qq);
      }
      if (email != null) {
        customerDTO.setEmail(email);
      }
      if (code != null) {
        customerDTO.setCode(code);
      }
      if (birth != null) {
        customerDTO.setBirthday(DateUtil.convertDateStringToDateLong(DateUtil.MONTH_DATE, birth));
      }

      if (bank != null) {
        customerDTO.setBank(bank);
      }
      if (bankAccountName != null) {
        customerDTO.setBankAccountName(bankAccountName);
      }
      if (account != null) {
        customerDTO.setAccount(account);
      }
      if (memo != null) {
        customerDTO.setMemo(memo);
      }
      if (invoiceCategory != null) {
        customerDTO.setInvoiceCategory(NumberUtil.longValue(invoiceCategory));
      }
      if (settlementType != null) {
        customerDTO.setSettlementType(NumberUtil.longValue(settlementType));
      }
      if (customerKind != null) {
        customerDTO.setCustomerKind(StringUtils.isBlank(customerKind)?null:customerKind);
      }


      if(StringUtils.isNotBlank(supplierId)) {
          customerDTO.setIdentity("isSupplier");
          customerDTO.setSupplierId(NumberUtil.toLong(supplierId));
      }


       SupplierDTO supplierDTO = null;
      //同时更新供应商
      if (customerDTO.getSupplierId() != null && null != customerDTO.getIdentity() && !"".equals(customerDTO.getIdentity())) {
        supplierDTO = ServiceManager.getService(IUserService.class).getSupplierById(customerDTO.getSupplierId());
        supplierDTO.fromCustomerDTO(customerDTO,null);
        if(StringUtils.isNotBlank(supplierId)) {
            supplierDTO.setIdentity("isCustomer");
            supplierDTO.setCustomerId(customerDTO.getId());
        }
        userService.updateSupplier(supplierDTO); //add by zhuj 更新供应商信息
        //建立联系时更新remind_event
        ServiceManager.getService(ITxnService.class).updateRemindEvent2(shopId, customerDTO.getId(), supplierDTO.getId());

        // add by zhuj 这里需要处理修改的新增的联系人
        String isMergeContact = request.getParameter("mergeContact");
        if (StringUtils.isNotBlank(isMergeContact) && StringUtils.equals(isMergeContact, "noMerge")) {
          // 不需要合并的情况 目前对客户和供应商的联系人都不进行处理 noMerge这个标识 和 modifySupplier.js里面的赋值耦合
        } else {
          List<ContactDTO> contactDTOList = new ArrayList<ContactDTO>();
          ContactDTO[] contactDTOs1 = customerDTO.getContacts();
          if (!ArrayUtils.isEmpty(contactDTOs1)) {
            for (ContactDTO contactDTO : contactDTOs1) {
              if (contactDTO != null && contactDTO.isValidContact()) {
                contactDTOList.add(contactDTO);
              }
            }
          }
          if (!CollectionUtils.isEmpty(contactDTOList)) {
            ContactDTO[] contacts = new ContactDTO[contactDTOList.size()];
            for (int i = 0; i < contactDTOList.size(); i++) {
              contacts[i] = contactDTOList.get(i);
            }
            ServiceManager.getService(IContactService.class).updateContactsBelongCustomerAndSupplier(customerDTO.getId(), customerDTO.getSupplierId(), shopId, contacts); // add by zhuj
            ServiceManager.getService(IContactService.class).addContactsBelongCustomerAndSupplier(customerDTO.getId(), customerDTO.getSupplierId(), shopId, contacts);
          }
        }

        ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexSupplierBySupplierId(supplierDTO.getId());
      }
      // 取消关联
      if (StringUtils.isNotBlank(identity) && identity.trim().equals("cancelSupplier")) { //TODO 取消关联以后 会进行update操作 此时又会去操作联系人
        if (customerDTO.getSupplierId() != null) {
          cancelRelationSupplierId = customerDTO.getSupplierId();
        }
        //更新欠款提醒remind_event,将原来既是客户又是供应商的入库退货单的那条记录中的CustomerId清空，supplierId设置值
        ServiceManager.getService(ITxnService.class).updateRemindEvent(customerDTO.getShopId(),customerDTO.getId(),customerDTO.getSupplierId());
        //ServiceManager.getService(IContactService.class).cancelRelatedCusSupContacts(customerDTO.getId(),cancelRelationSupplierId,shopId); // add by zhuj 取消关联联系人相关的处理
        customerDTO.setIdentity(null);
        customerDTO.setSupplierId(null);
        customerDTO.setCancel(true);
      }

      if (customerRecordDTO == null) {
        try {
          customerDTO.setFromManagePage(true);
          userService.updateCustomer(customerDTO);
        } catch (Exception e) {
          LOG.debug("/unitlink.do");
          LOG.debug("method=updateCustomer");
          LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
          LOG.error(e.getMessage(), e);
          result="failed";
        }
      } else {   //更新CustomerRecordDTO中的相关记录
        customerRecordDTO.fromCustomerDTO(customerDTO);
        if("isSupplier".equals(customerDTO.getIdentity()) && customerDTO.getSupplierId() != null) {
            customerRecordDTO.setIdentity(customerDTO.getIdentity());
            customerRecordDTO.setSupplierId(customerDTO.getSupplierId());
        }
        try {
          customerDTO.setFromManagePage(true);
          userService.updateCustomer(customerDTO);
          userService.updateCustomerRecord(customerRecordDTO);

        } catch (Exception e) {
          LOG.debug("/unitlink.do");
          LOG.debug("method=updateCustomer");
          LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
          LOG.error(e.getMessage(), e);
          result="failed";
        }

      }
      if(StringUtils.isNotBlank(request.getParameter("supplierId"))) {
        if (customerDTO != null) {
          //设置供应商的经营范围
          userService.saveOrUpdateCustomerSupplierBusinessScope(customerDTO, supplierDTO, customerDTO.getThirdCategoryIdStr());
          preciseRecommendService.getCustomerSupplierBusinessScopeForAdd(customerDTO, supplierDTO);
          userService.updateCustomerSupplierBusinessScope(customerDTO, supplierDTO);
        }
        ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexCustomerByCustomerId(customerDTO.getId());
        PrintWriter writer = response.getWriter();
        writer.print("success");
        writer.close();
        return null;
      }
      if (cancelRelationSupplierId != null) {
        ISupplierService supplierService = ServiceManager.getService(ISupplierService.class);
        supplierDTO = supplierService.getSupplierById(cancelRelationSupplierId, shopId);
        supplierDTO.setIdentity(null);
        supplierDTO.setCustomerId(null);
        supplierDTO.setCancel(true);
        userService.updateSupplier(supplierDTO);
        //ServiceManager.getService(IContactService.class).cancelRelatedCusSupContacts(customerId, cancelRelationSupplierId,shopId); // 解除关联 联系人处理逻辑
      }else {
        if (customerDTO != null) {
          //设置供应商的经营范围
          userService.saveOrUpdateCustomerSupplierBusinessScope(customerDTO, supplierDTO, customerDTO.getThirdCategoryIdStr());
          preciseRecommendService.getCustomerSupplierBusinessScopeForAdd(customerDTO, supplierDTO);
          userService.updateCustomerSupplierBusinessScope(customerDTO, supplierDTO);
        }
      }

      // add by zhuj cancel 联系人的处理单独拿出来(否则会出现取消了关联以后，复制出来的联系人，又在进行updateCustomer和updateSupplier的操作中被还原掉)
      if (cancelRelationSupplierId != null){
        ServiceManager.getService(IContactService.class).cancelRelatedCusSupContacts(customerDTO.getId(),cancelRelationSupplierId,shopId); // add by zhuj 取消关联联系人相关的处理
      }

      //当改变客户所属区域时，插入一条schedule任务
      if(changeArea) {
        //判断是否已经存在有ready状态的任务，如果有，则不插入，以此防止用户频繁的更改区域
        List<CusOrSupOrderIndexSchedule> cusOrSupOrderIndexSchedules = userService.getCusOrSupOrderIndexScheduleDTOByCusOrSupId(shopId, customerId, null);
        if(CollectionUtils.isEmpty(cusOrSupOrderIndexSchedules)) {
            CusOrSupOrderIndexScheduleDTO cusOrSupOrderIndexScheduleDTO = new CusOrSupOrderIndexScheduleDTO();
            cusOrSupOrderIndexScheduleDTO.setShopId(shopId);
            cusOrSupOrderIndexScheduleDTO.setCustomerId(customerId);
            cusOrSupOrderIndexScheduleDTO.setCreatedTime(System.currentTimeMillis());
            cusOrSupOrderIndexScheduleDTO.setExeStatus(ExeStatus.READY);
            ServiceManager.getService(IUserService.class).saveCusOrSupOrderIndexSchedule(cusOrSupOrderIndexScheduleDTO);
        }
      }


    } else { //新增客户
      try {
        customerDTO = new CustomerDTO();
        customerDTO.setContacts(contactDTOs); // add by zhuj 更新联系人
        customerDTO.setThirdCategoryIdStr(thirdCategoryIdStr);
        customerRecordDTO = new CustomerRecordDTO();
        if (name != null) {
          customerDTO.setName(name);
          customerRecordDTO.setName(name);
        } else {
          String uvmValue = URLDecoder.decode(request.getParameter("uvmValue"), "UTF-8");
          customerDTO.setName(uvmValue);
          customerRecordDTO.setName(uvmValue);
        }
        if (contact != null) {
          customerDTO.setContact(contact);
            customerRecordDTO.setContact(contact);
        }
        if (mobile != null) {
          customerDTO.setMobile(mobile);
            customerRecordDTO.setMobile(mobile);
        }
        if (landline != null) {
          customerDTO.setLandLine(landline);
          customerRecordDTO.setPhone(landline);
        }
        if (fax != null) {
          customerDTO.setFax(fax);
          customerRecordDTO.setFax(fax);
        }
        if (addr != null) {
          customerDTO.setAddress(addr);
          customerRecordDTO.setAddress(addr);
        }
        if (qq != null) {
          customerDTO.setQq(qq);
          customerRecordDTO.setQq(qq);
        }
        if (email != null) {
          customerDTO.setEmail(email);
          customerRecordDTO.setEmail(email);
        }
        if (code != null) {
          customerDTO.setCode(code);
          customerRecordDTO.setMemberNumber(code);
        }
        if (birth != null) {
          customerDTO.setBirthdayString(birth);
          customerDTO.setBirthday(DateUtil.convertDateStringToDateLong(DateUtil.MONTH_DATE,birth));
          customerRecordDTO.setBirthdayString(birth);
          customerRecordDTO.setBirthday(DateUtil.convertDateStringToDateLong(DateUtil.MONTH_DATE,birth));
        }
        if (province != null) {
          customerDTO.setProvince(province);
          customerRecordDTO.setProvince(province);
        }
        if (city != null) {
          customerDTO.setCity(city);
          customerRecordDTO.setCity(city);
        }
        if (region != null) {
          customerDTO.setRegion(region);
          customerRecordDTO.setRegion(region);
        }
        if(address!=null){
          customerDTO.setAddress(address);
          customerRecordDTO.setAddress(address);
        }
        if (memo != null) {
            customerDTO.setMemo(memo);
            customerRecordDTO.setMemo(memo);
        }
        if (invoiceCategory != null) {
            customerDTO.setInvoiceCategory(NumberUtil.longValue(invoiceCategory));
            customerRecordDTO.setInvoiceCategory(invoiceCategory);
        }
        if (settlementType != null) {
            customerDTO.setSettlementType(NumberUtil.longValue(settlementType));
            customerRecordDTO.setSettlementType(settlementType);
        }
        if (customerKind != null) {
            customerDTO.setCustomerKind(customerKind);
            customerRecordDTO.setCustomerKind(customerKind);
        }
        if (shortName != null) {
            customerDTO.setShortName(shortName);
            customerRecordDTO.setShortName(shortName);
        }
        if (bank != null) {
            customerDTO.setBank(bank);
            customerRecordDTO.setBank(bank);
        }
        if (bankAccountName != null) {
            customerDTO.setBankAccountName(bankAccountName);
            customerRecordDTO.setBankAccountName(bankAccountName);
        }
        if (account != null) {
            customerDTO.setAccount(account);
            customerRecordDTO.setAccount(account);
        }

        customerDTO.setShopId((Long) request.getSession().getAttribute("shopId"));
        customerRecordDTO.setShopId((Long) request.getSession().getAttribute("shopId"));
        if(StringUtils.isNotBlank(supplierId)) {
            customerDTO.setIdentity("isSupplier");
            customerDTO.setSupplierId(NumberUtil.toLong(supplierId));
            customerRecordDTO.setIdentity("isSupplier");
            customerRecordDTO.setSupplierId(NumberUtil.toLong(supplierId));

        }
        customerDTO = userService.createCustomer(customerDTO);

        //add by zhuj　新增完毕以后 设置联系人列表的customerId 以便supplierDTO 更新
        if (customerDTO.hasValidContact()) {
          for (ContactDTO contactDTO : customerDTO.getContacts()) {
            if (contactDTO != null && contactDTO.isValidContact()) {
              contactDTO.setCustomerId(customerDTO.getId());
            }
          }
        }
        customerRecordDTO.setCustomerId(customerDTO.getId());
        userService.createCustomerRecord(customerRecordDTO);
        customerId = customerDTO.getId();
        request.setAttribute("customerId", customerId.toString());
        ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexCustomerByCustomerId(customerDTO.getId());
        SupplierDTO supplierDTO = null;
        if (StringUtils.isNotBlank(supplierId)) {
            //建立联系时更新remind_event
            ServiceManager.getService(ITxnService.class).updateRemindEvent2(shopId, customerDTO.getId(), customerDTO.getSupplierId());
          supplierDTO = ServiceManager.getService(IUserService.class).getSupplierById(NumberUtil.toLong(supplierId));
          supplierDTO.fromCustomerDTO(customerDTO, null);
          if (StringUtils.isNotBlank(supplierId)) {
            supplierDTO.setIdentity("isCustomer");
            supplierDTO.setCustomerId(customerDTO.getId());
          }
          userService.updateSupplier(supplierDTO);
          // add by zhuj 新增的联系人在这边处理
          ServiceManager.getService(IContactService.class).addContactsBelongCustomerAndSupplier(supplierDTO.getCustomerId(), supplierDTO.getId(), shopId, contactDTOs);
          ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexSupplierBySupplierId(supplierDTO.getId());

          if (customerDTO != null) {
            //设置供应商的经营范围
            userService.saveOrUpdateCustomerSupplierBusinessScope(customerDTO, supplierDTO, customerDTO.getThirdCategoryIdStr());
            preciseRecommendService.getCustomerSupplierBusinessScopeForAdd(customerDTO, supplierDTO);
            userService.updateCustomerSupplierBusinessScope(customerDTO, supplierDTO);
          }

          PrintWriter writer = response.getWriter();
          writer.print("success");
          writer.close();
          return null;
        }else {
          if (customerDTO != null) {
            //设置供应商的经营范围
            userService.saveOrUpdateCustomerSupplierBusinessScope(customerDTO, supplierDTO, customerDTO.getThirdCategoryIdStr());
            preciseRecommendService.getCustomerSupplierBusinessScopeForAdd(customerDTO, supplierDTO);
            userService.updateCustomerSupplierBusinessScope(customerDTO, supplierDTO);
          }
        }

      } catch (Exception e) {
        LOG.debug("/unitlink.do" +
            "method=updateCustomer" +
            "\"shopId:\" + request.getSession().getAttribute(\"shopId\") + \",userId:\" + request.getSession().getAttribute(\"userId\")" +
            "updateNewCustomer,CustomerRecord error");
        LOG.error(e.getMessage(), e);
        result="failed";
      }

    }
     //reindex customer in solr
    ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexCustomerByCustomerId(customerDTO.getId());
    if(cancelRelationSupplierId!=null){
      ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexSupplierBySupplierId(cancelRelationSupplierId);
    }
    return result;
  }

  @RequestMapping(params = "method=smsHistory")
  public String smsHistory(HttpServletRequest request,HttpServletResponse response,String mobile) {
    INotificationService notificationService = ServiceManager.getService(INotificationService.class);
    IUserService userService = ServiceManager.getService(IUserService.class);
     int sentSmsNumber = 0;   //已发送短信条数
    try {
      Long shopId = (Long) request.getSession().getAttribute("shopId");
      if (mobile != null && !mobile.trim().equals("")) {
          //计算已发送短信条数
          sentSmsNumber = notificationService.countOutBoxNumberByShopIdAndMobile(shopId, mobile);
          if (sentSmsNumber == 0) {            //没有短信发送历史,直接跳转到短信发送页面
            request.setAttribute("sendMobile", mobile);
            return "sms/smswrite2";
          }
      }
          String pNo = (String) request.getAttribute("pageNo");
          int pageNo = 1;
          if (pNo != null) {
            pageNo = Integer.parseInt(pNo.trim());
          }
          List<OutBoxDTO> outBoxDTOList = null;
          outBoxDTOList = notificationService.getOutBoxByShopAndMobile(shopId, mobile, pageNo - 1, pageSize);   //分页查询
          if (outBoxDTOList != null && !outBoxDTOList.isEmpty()) {
            for (OutBoxDTO outBoxDTO : outBoxDTOList) {
              List<CustomerDTO> customerDTOList = userService.getCustomerByMobile(shopId, mobile);
              if (customerDTOList != null && customerDTOList.size() > 0) {
                outBoxDTO.setName(customerDTOList.get(0).getName());
              }
              List<VehicleDTO> vehicleDTOList = userService.getVehicleByMobile(shopId, mobile);
              if (vehicleDTOList != null && vehicleDTOList.size() > 0) {
                outBoxDTO.setLicenceNo(vehicleDTOList.get(0).getLicenceNo());
              }
            }
          }
          int pageCount = sentSmsNumber % pageSize == 0 ? sentSmsNumber / pageSize : (sentSmsNumber / pageSize + 1);
          request.setAttribute("outBoxDTOList", outBoxDTOList);
          request.setAttribute("sentSmsNumber", sentSmsNumber);
          request.setAttribute("pageNo", pageNo);
          request.setAttribute("pageCount", pageCount);
          request.setAttribute("mobile", mobile);
    } catch (Exception e) {
      LOG.debug("/unitlink.do");
      LOG.debug("method=smsHistory");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
    }
    return "sms/smssent2";
  }

  @RequestMapping(params = "method=smsSend")
  public String smsSend(HttpServletRequest request, CustomerRemindSms customerRemindSms){
      try {
          Long shopId = WebUtil.getShopId(request);
          if (shopId == null || (StringUtils.isBlank(customerRemindSms.getName()) && StringUtils.isBlank(customerRemindSms.getLicenceNo()))) {
              return "sms/smswrite2";
          }

          //客户欠款提醒ID，客户服务提醒ID，会员服务提醒ID，传入短信编辑页面，发送或预设时间后更改提醒状态
          String remindEventIdStr = request.getParameter("remindEventId");
          request.setAttribute("remindEventId", remindEventIdStr);

          customerRemindSms.setShopId(shopId);
          ISmsService smsService = ServiceManager.getService(ISmsService.class);
          IUserService userService = ServiceManager.getService(IUserService.class);
          List<UserDTO> userDTOs = userService.getShopUser(shopId);
          if (CollectionUtils.isNotEmpty(userDTOs)) {
              UserDTO userDTO = userDTOs.get(0);
              if (userDTO != null) {
                  customerRemindSms.setUserName(userDTO.getName());
            }
          }
          // 根据 type 获得模板 todo :type 类型需要修改成枚举
          customerRemindSms = smsService.sendCustomerServiceRemindMessage(customerRemindSms);
          if (customerRemindSms != null) {
            request.setAttribute("templateFlag", customerRemindSms.isTemplateFlag());
            request.setAttribute("sendMobile", customerRemindSms.getTitle());
            request.setAttribute("smsContent", customerRemindSms.getContent());
          }
      } catch (Exception e) {
        LOG.debug("/unitlink.do");
        LOG.debug("method=smsHistory");
        LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
        LOG.error(e.getMessage(), e);
      }
    return "sms/smswrite2";
  }

  /**
   * 请别再使用这个了   请看 supplierController  method=updateSupplier
   * 客户管理-供应商资料-供应商信息-直接编辑
   * 客户管理-供应商资料-供应商信息-更多供应商信息
   *
   * @param request
   * @return
   * @throws Exception
   */
  @Deprecated
  @RequestMapping(params = "method=updateSupplierInfo")
  @ResponseBody
  public String updateSupplierInfo(HttpServletRequest request,HttpServletResponse response) throws Exception {
    String result = "fail";
    IUserService userService = ServiceManager.getService(IUserService.class);
    IPreciseRecommendService preciseRecommendService = ServiceManager.getService(IPreciseRecommendService.class);
    String id = request.getParameter("supplierId");
    long supplierId = NumberUtil.longValue(request.getParameter("supplierId"), 0);
    Long shopId = WebUtil.getShopId(request);
//supplierId不能为空
//    if (supplierId <= 0) {
//      request.setAttribute("updateOpe", "fail");
//      return "customer/uncleSupplier";
//    }
    supplierId = Long.parseLong(id.trim());
//执行编辑更新时，必须找到已存在的供应商数据
    SupplierDTO supplierDTO = userService.getSupplierById(supplierId);
    try {
      if (supplierDTO == null) {
        supplierDTO = userService.createSupplier(this.supplierDTOGenerator.generate(request, supplierDTO));
        ServiceManager.getService(ISupplierRecordService.class).createSupplierRecordUsingSupplierDTO(supplierDTO);
        request.setAttribute("updateOpe", "succ");
      } else {
        CustomerDTO customerDTO = null;
        Boolean isRelatedOnlineShop = (supplierDTO.getSupplierShopId() != null);
        Long customerId = supplierDTO.getCustomerId();
        if(customerId != null){
          customerDTO = userService.getCustomerById(customerId);
          if(customerDTO!=null && customerDTO.getCustomerShopId()!=null){
            isRelatedOnlineShop = true;
          }
        }
        //更新供应商信息对象
        if(isRelatedOnlineShop){
          supplierDTO = this.supplierDTOGenerator.generateOnlineSupplier(request, supplierDTO);
        }else {
          supplierDTO = this.supplierDTOGenerator.generate(request, supplierDTO);
        }
        boolean cancelFlag = customerId != null && StringUtils.isBlank(supplierDTO.getIdentity());
           //同时更新客户信息
        if (customerId != null) {
          customerDTO = userService.getCustomerById(customerId);
          customerDTO.fromSupplierDTO(supplierDTO);
          if (cancelFlag) { //commented by zhuj 取消绑定 判断条件为Identity 为空
            customerDTO = userService.getCustomerById(supplierDTO.getCustomerId());
            customerDTO.setSupplierId(null);
            customerDTO.setIdentity(null);
            customerDTO.setCancel(true);
            userService.updateCustomer(customerDTO);
            supplierDTO.setCustomerId(null);
            supplierDTO.setCancel(true);
            //更新remind_event
            ServiceManager.getService(ITxnService.class).updateRemindEvent(customerDTO.getShopId(), customerDTO.getId(), supplierDTO.getId());
            // add by zhuj 更新联系人信息
            //ServiceManager.getService(IContactService.class).cancelRelatedCusSupContacts(customerDTO.getId(),supplierDTO.getId(),shopId);
          } else {
            ServiceManager.getService(IUserService.class).updateCustomer(customerDTO);
            // add by zhuj 供应客户联系人信息 页面详情跳转过来...
            ServiceManager.getService(IContactService.class).updateContactsBelongCustomerAndSupplier(supplierDTO.getCustomerId(), supplierDTO.getId(), shopId, supplierDTO.getContacts());
            ServiceManager.getService(IContactService.class).addContactsBelongCustomerAndSupplier(supplierDTO.getCustomerId(), supplierDTO.getId(), shopId, supplierDTO.getContacts());
            CustomerRecordDTO customerRecordDTO = null;
            List<CustomerRecordDTO> customerRecordDTOList = ServiceManager.getService(IUserService.class).getCustomerRecordByCustomerId(supplierDTO.getCustomerId());
            if (customerRecordDTOList != null && !customerRecordDTOList.isEmpty()) {
              customerRecordDTO = customerRecordDTOList.get(0);
            }
            if (customerRecordDTO != null) {
              customerRecordDTO.fromCustomerDTO(customerDTO);
              ServiceManager.getService(IUserService.class).updateCustomerRecord(customerRecordDTO);
            }
          }
        }

        CustomerRecordDTO customerRecordDTO = null;
        if (request.getParameter("customerId") != null && !"".equals(request.getParameter("customerId"))) { //客户详情页面 既是客户又是供应商 add by zhuj
          customerDTO = ServiceManager.getService(IUserService.class).getCustomerById(NumberUtil.toLong(request.getParameter("customerId")));
          customerDTO.setIdentity("isSupplier");
          customerDTO.setSupplierId(supplierId);
          customerDTO.fromSupplierDTO(supplierDTO);
          customerDTO.setBirthdayString(request.getParameter("birthdayString"));
          customerDTO.setBirthday(DateUtil.convertDateStringToDateLong(DateUtil.MONTH_DATE, request.getParameter("birthdayString")));
          customerDTO.setCustomerKind(request.getParameter("customerKind"));
          ServiceManager.getService(IUserService.class).updateCustomer(customerDTO);
          customerRecordDTO = ServiceManager.getService(IUserService.class).getCustomerRecordByCustomerId(NumberUtil.toLong(request.getParameter("customerId"))).get(0);
          if (customerRecordDTO != null) {
            customerRecordDTO.setIdentity("isSupplier");
            customerRecordDTO.setSupplierId(supplierId);
            customerRecordDTO.fromCustomerDTO(customerDTO);
            customerRecordDTO.setBirthdayString(request.getParameter("birthdayString"));
            customerRecordDTO.setCustomerKind(request.getParameter("customerKind"));
            ServiceManager.getService(IUserService.class).updateCustomerRecord(customerRecordDTO);
          }

          supplierDTO.setIdentity("isCustomer");
          supplierDTO.setCustomerId(customerDTO.getId());
          //建立联系时更新remind_event
          ServiceManager.getService(ITxnService.class).updateRemindEvent2(shopId, customerDTO.getId(), supplierId);
          String isMergeContact = request.getParameter("mergeContact");
          if (StringUtils.isNotBlank(isMergeContact) && StringUtils.equals(isMergeContact, "noMerge")) {
            // 不需要合并的情况 目前对客户和供应商的联系人都不进行处理 noMerge这个标识 和 modifyClient.js里面的赋值耦合
          } else {
            // add by zhuj 供应客户联系人信息 页面详情跳转过来...
            ServiceManager.getService(IContactService.class).updateContactsBelongCustomerAndSupplier(supplierDTO.getCustomerId(), supplierDTO.getId(), shopId, supplierDTO.getContacts());
            ServiceManager.getService(IContactService.class).addContactsBelongCustomerAndSupplier(supplierDTO.getCustomerId(), supplierDTO.getId(), shopId, supplierDTO.getContacts());
          }


        }
        supplierDTO.setFromManagePage(true);

        //设置供应商的经营范围
        userService.saveOrUpdateCustomerSupplierBusinessScope(customerDTO,supplierDTO,supplierDTO.getThirdCategoryIdStr());
        preciseRecommendService.getCustomerSupplierBusinessScopeForAdd(customerDTO,supplierDTO);
        userService.updateCustomerSupplierBusinessScope(customerDTO,supplierDTO);

        userService.updateSupplier(supplierDTO, null, null, null, null);
        // add by zhuj 最后cancel
        if (cancelFlag) {
          ServiceManager.getService(IContactService.class).cancelRelatedCusSupContacts(customerDTO.getId(), supplierDTO.getId(), shopId);
          ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexCustomerByCustomerId(customerId);
        }

        if (request.getParameter("customerId") != null && !"".equals(request.getParameter("customerId"))) {
          ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexSupplierBySupplierId(supplierDTO.getId());
          ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexCustomerByCustomerId(Long.parseLong(request.getParameter("customerId")));
          PrintWriter writer = response.getWriter();
          writer.write("success");
          writer.close();
          return null;
        }
        if ("".equals(request.getParameter("identity"))) {
          request.setAttribute("updateOpe", "cancelSucc");
        } else {
          request.setAttribute("updateOpe", "succ");
        }

      }
      //reindex supplier in solr
      ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexSupplierBySupplierId(supplierDTO.getId());
      //当改变供应商所属区域时，同时更新供应商的单据
      if(supplierDTO.getChangeArea()) {
          List<CusOrSupOrderIndexSchedule> cusOrSupOrderIndexSchedules = userService.getCusOrSupOrderIndexScheduleDTOByCusOrSupId(shopId,null,supplierId);
          if(CollectionUtils.isEmpty(cusOrSupOrderIndexSchedules)) {
              CusOrSupOrderIndexScheduleDTO cusOrSupOrderIndexScheduleDTO = new CusOrSupOrderIndexScheduleDTO();
              cusOrSupOrderIndexScheduleDTO.setShopId(shopId);
              cusOrSupOrderIndexScheduleDTO.setSupplierId(supplierId);
              cusOrSupOrderIndexScheduleDTO.setCreatedTime(System.currentTimeMillis());
              cusOrSupOrderIndexScheduleDTO.setExeStatus(ExeStatus.READY);
              ServiceManager.getService(IUserService.class).saveCusOrSupOrderIndexSchedule(cusOrSupOrderIndexScheduleDTO);
          }
      }

      result = "success";
    } catch (Exception e) {
      LOG.debug("/unitlink.do");
      LOG.debug("method=updateSupplierInfo");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
      request.setAttribute("updateOpe", "fail");
      result = "fail";
    }
    request.removeAttribute("inventoryTotalMoney");
    request.setAttribute("supplierId", supplierDTO.getId());
    request.setAttribute("reload", "yes");
    return result;
  }

  /**
   * 客户管理-供应商资料-新增供应商
   *
   * @param request
   * @return
   * @throws Exception
   */
  @RequestMapping(params = "method=newSupplier")
  public void newSupplier(HttpServletRequest request, HttpServletResponse response) throws Exception {
    ISupplierService supplierService = ServiceManager.getService(ISupplierService.class);
    IUserService userService = ServiceManager.getService(IUserService.class);
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    ISearchService searchService = ServiceManager.getService(ISearchService.class);
    IPreciseRecommendService preciseRecommendService = ServiceManager.getService(IPreciseRecommendService.class);
    Long shopId = WebUtil.getShopId(request);
    SupplierDTO supplierDTO = this.supplierDTOGenerator.generate(request, null);
    if (supplierDTO.getId() == null) {
      supplierDTO.setRelationType(RelationTypes.UNRELATED);
    }else{
      SupplierDTO dbSupplierDTO = userService.getSupplierById(supplierDTO.getId());
      if(dbSupplierDTO!=null){
        supplierDTO.setIdentity(dbSupplierDTO.getIdentity());
        supplierDTO.setCustomerId(dbSupplierDTO.getCustomerId());
      }
    }

    CustomerDTO businessScopeCustomerDTO = null;

    if (supplierDTO.getCustomerId() != null) {  //已有的供应商关联了客户
      CustomerDTO customerDTO = userService.getCustomerById(supplierDTO.getCustomerId());
      customerDTO.setName(supplierDTO.getName());
      customerDTO.setProvince(supplierDTO.getProvince());
      customerDTO.setCity(supplierDTO.getCity());
      customerDTO.setRegion(supplierDTO.getRegion());
      customerDTO.setAddress(supplierDTO.getAddress());
      customerDTO.setShortName(supplierDTO.getAbbr());
      customerDTO.setContact(supplierDTO.getContact());
      customerDTO.setContacts(supplierDTO.getContacts()); // add by zhuj
      customerDTO.setMobile(supplierDTO.getMobile());
      customerDTO.setEmail(supplierDTO.getEmail());
      customerDTO.setQq(supplierDTO.getQq());
      customerDTO.setLandLine(supplierDTO.getLandLine());
      customerDTO.setLandLineSecond(supplierDTO.getLandLineSecond());
      customerDTO.setLandLineThird(supplierDTO.getLandLineThird());
      customerDTO.compositeLandline();
      customerDTO.setFax(supplierDTO.getFax());
      customerDTO.setBank(supplierDTO.getBank());
      customerDTO.setAccount(supplierDTO.getAccount());
      customerDTO.setBankAccountName(supplierDTO.getAccountName());
      customerDTO.setSettlementType(supplierDTO.getSettlementTypeId());
      customerDTO.setInvoiceCategory(supplierDTO.getInvoiceCategoryId());
      customerDTO.setMemo(supplierDTO.getMemo());
      customerDTO.setIdentity("isSupplier");
      String customerKind = request.getParameter("customerKind");
      if (StringUtils.isNotBlank(customerKind)) {
        customerDTO.setCustomerKind(customerKind);
      }
      if (request.getParameter("birthdayString") != null && !"".equals(request.getParameter("birthdayString"))) {
        customerDTO.setBirthdayString(request.getParameter("birthdayString"));
        if(StringUtils.isNotBlank(customerDTO.getBirthdayString()))  {
            customerDTO.setBirthday(DateUtil.convertDateStringToDateLong(DateUtil.MONTH_DATE,customerDTO.getBirthdayString()));
        }
      }
      userService.updateCustomer(customerDTO);
      ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexCustomerByCustomerId(supplierDTO.getCustomerId());
      supplierDTO.setCustomerId(supplierDTO.getCustomerId());

      businessScopeCustomerDTO = customerDTO;

    } else if (supplierDTO.getIdentity() != null && !"".equals(supplierDTO.getIdentity())) {    //新建关联客户
      CustomerDTO customerDTO = new CustomerDTO();
      customerDTO.setShopId((Long) request.getSession().getAttribute("shopId"));
      customerDTO.setName(supplierDTO.getName());
      customerDTO.setProvince(supplierDTO.getProvince());
      customerDTO.setCity(supplierDTO.getCity());
      customerDTO.setRegion(supplierDTO.getRegion());
      customerDTO.setAddress(supplierDTO.getAddress());
      customerDTO.setShortName(supplierDTO.getAbbr());
      customerDTO.setContact(supplierDTO.getContact());
      customerDTO.setContacts(supplierDTO.getContacts()); // add by zhuj
      customerDTO.setMobile(supplierDTO.getMobile());
      customerDTO.setEmail(supplierDTO.getEmail());
      customerDTO.setQq(supplierDTO.getQq());
      customerDTO.setLandLine(supplierDTO.getLandLine());
      customerDTO.setFax(supplierDTO.getFax());
      customerDTO.setBank(supplierDTO.getBank());
      customerDTO.setAccount(supplierDTO.getAccount());
      customerDTO.setBankAccountName(supplierDTO.getAccountName());
      customerDTO.setSettlementType(supplierDTO.getSettlementTypeId());
      customerDTO.setInvoiceCategory(supplierDTO.getInvoiceCategoryId());
      customerDTO.setIdentity("isSupplier");
      customerDTO.setMemo(supplierDTO.getMemo());
      String customerKind = request.getParameter("customerKind");
      if (StringUtils.isNotBlank(customerKind)) {
        customerDTO.setCustomerKind(customerKind);
      }
      if (request.getParameter("birthdayString") != null && !"".equals(request.getParameter("birthdayString"))) {
        customerDTO.setBirthdayString(request.getParameter("birthdayString"));
          if(StringUtils.isNotBlank(customerDTO.getBirthdayString()))  {
              customerDTO.setBirthday(DateUtil.convertDateStringToDateLong(DateUtil.MONTH_DATE,customerDTO.getBirthdayString()));
          }
      }
      userService.createCustomer(customerDTO);

      CustomerRecordDTO customerRecordDTO = new CustomerRecordDTO();
      customerRecordDTO.fromCustomerDTO(customerDTO);
      userService.createCustomerRecord(customerRecordDTO);
      ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexCustomerByCustomerId(customerDTO.getId());
      supplierDTO.setCustomerId(customerDTO.getId());

      businessScopeCustomerDTO = customerDTO;
    }
    CustomerDTO customerDTO = null;
    CustomerRecordDTO customerRecordDTO = null;
    //客户详细页面, 点击了"也是供应商".
    if (request.getParameter("customerId") != null && !"".equals(request.getParameter("customerId"))) {
      customerDTO = ServiceManager.getService(IUserService.class).getCustomerById(NumberUtil.toLong(request.getParameter("customerId")));
      customerDTO.setIdentity("isSupplier");
      customerDTO.setName(supplierDTO.getName());
      customerDTO.setMobile(supplierDTO.getMobile());
      customerDTO.setLandLine(supplierDTO.getLandLine());
      customerDTO.setLandLineSecond(supplierDTO.getLandLineSecond());
      customerDTO.setLandLineThird(supplierDTO.getLandLineThird());
      customerDTO.compositeLandline();
      customerDTO.setContact(supplierDTO.getContact());
      customerDTO.setContacts(supplierDTO.getContacts()); // add by zhuj
      customerDTO.setFax(supplierDTO.getFax());
      customerDTO.setAddress(supplierDTO.getAddress());
      customerDTO.setQq(supplierDTO.getQq());
      customerDTO.setEmail(supplierDTO.getEmail());
      customerDTO.setBank(supplierDTO.getBank());
      customerDTO.setBankAccountName(supplierDTO.getAccountName());
      customerDTO.setAccount(supplierDTO.getAccount());
      customerDTO.setMemo(supplierDTO.getMemo());
      customerDTO.setInvoiceCategoryStr(supplierDTO.getInvoiceCategory());
      customerDTO.setSettlementTypeStr(supplierDTO.getSettlementType());
      customerDTO.setSettlementType(supplierDTO.getSettlementTypeId());
      customerDTO.setInvoiceCategory(supplierDTO.getInvoiceCategoryId());
      customerDTO.setShortName(supplierDTO.getAbbr());
      customerDTO.setBirthdayString(request.getParameter("birthdayString"));
      customerDTO.setBirthday(DateUtil.convertDateStringToDateLong(DateUtil.MONTH_DATE, customerDTO.getBirthdayString()));
      customerDTO.setCustomerKind(request.getParameter("customerKind"));
      ServiceManager.getService(IUserService.class).updateCustomer(customerDTO);
      customerRecordDTO = ServiceManager.getService(IUserService.class).getCustomerRecordByCustomerId(NumberUtil.toLong(request.getParameter("customerId"))).get(0);
      if (customerRecordDTO != null) {
        customerRecordDTO.setIdentity("isSupplier");
        customerRecordDTO.setName(supplierDTO.getName());
        customerRecordDTO.setMobile(supplierDTO.getMobile());
        customerRecordDTO.setPhone(supplierDTO.getLandLine());
        customerRecordDTO.setPhoneSecond(supplierDTO.getLandLineSecond());
        customerRecordDTO.setPhoneThird(supplierDTO.getLandLineThird());
        customerRecordDTO.setContact(supplierDTO.getContact());
        customerRecordDTO.setContacts(supplierDTO.getContacts()); // add by zhuj
        customerRecordDTO.setFax(supplierDTO.getFax());
        customerRecordDTO.setAddress(supplierDTO.getAddress());
        customerRecordDTO.setQq(supplierDTO.getQq());
        customerRecordDTO.setEmail(supplierDTO.getEmail());
        customerRecordDTO.setBank(supplierDTO.getBank());
        customerRecordDTO.setBankAccountName(supplierDTO.getAccountName());
        customerRecordDTO.setAccount(supplierDTO.getAccount());
        customerRecordDTO.setMemo(supplierDTO.getMemo());
        customerRecordDTO.setInvoiceCategory(supplierDTO.getInvoiceCategory());
        customerRecordDTO.setSettlementType(supplierDTO.getSettlementType());
        customerRecordDTO.setShortName(supplierDTO.getAbbr());
        customerRecordDTO.setBirthdayString(request.getParameter("birthdayString"));
        customerRecordDTO.setBirthday(DateUtil.convertDateStringToDateLong(DateUtil.MONTH_DATE,customerRecordDTO.getBirthdayString()));
        customerRecordDTO.setCustomerKind(request.getParameter("customerKind"));
        ServiceManager.getService(IUserService.class).updateCustomerRecord(customerRecordDTO);
      }

      supplierDTO.setIdentity("isCustomer");
      supplierDTO.setCustomerId(customerDTO.getId());

      businessScopeCustomerDTO = customerDTO;
    }
    userService.createSupplier(supplierDTO);
    supplierService.saveSupplierVehicleBrandModelRelation(shopId,supplierDTO,StandardBrandModelCache.getShopVehicleBrandModelDTOMap());

    //更新客户或者供应商的经营范围
    userService.saveOrUpdateCustomerSupplierBusinessScope(businessScopeCustomerDTO,supplierDTO,supplierDTO.getThirdCategoryIdStr());
    preciseRecommendService.getCustomerSupplierBusinessScopeForAdd(businessScopeCustomerDTO,supplierDTO);
    userService.updateCustomerSupplierBusinessScope(businessScopeCustomerDTO,supplierDTO);
    if (supplierDTO.getIdentity() != null && !"".equals(supplierDTO.getIdentity())) {
      CustomerDTO relatedCustomerDTO = userService.getCustomerById(supplierDTO.getCustomerId());
      relatedCustomerDTO.setSupplierId(supplierDTO.getId());
      relatedCustomerDTO.setSelectBrandModel(supplierDTO.getSelectBrandModel());
      relatedCustomerDTO.setVehicleModelIdStr(supplierDTO.getVehicleModelIdStr());
      userService.updateCustomer(relatedCustomerDTO);
      if (customerRecordDTO!=null && customerRecordDTO.getSupplierId() == null) {
        customerRecordDTO.setSupplierId(supplierDTO.getId());
        userService.updateCustomerRecord(customerRecordDTO);
      }
      userService.saveCustomerVehicleBrandModelRelation(shopId, relatedCustomerDTO, StandardBrandModelCache.getShopVehicleBrandModelDTOMap());
      // add by zhuj 既是供应商又是客户的联系人列表新增
      ServiceManager.getService(IContactService.class).addContactsBelongCustomerAndSupplier(supplierDTO.getCustomerId(), supplierDTO.getId(),WebUtil.getShopId(request), supplierDTO.getContacts());
      ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexCustomerByCustomerId(relatedCustomerDTO.getId());
    }
    ServiceManager.getService(ISupplierRecordService.class).createSupplierRecordUsingSupplierDTO(supplierDTO);
    try {
      if (supplierDTO.isUpdate()) {
        txnService.updatePurchaseInventorySupplier(supplierDTO);
        txnService.updatePurchaseOrderSupplier(supplierDTO);
        searchService.updateItemIndexSupplier(supplierDTO);
        searchService.updateOrderIndexSupplier(supplierDTO);
      }

    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
    ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexSupplierBySupplierId(supplierDTO.getId());
    PrintWriter writer = response.getWriter();
    Map<String, String> map = new HashMap<String, String>();
    map.put("supplierId", supplierDTO.getId().toString());
    writer.write(JsonUtil.mapToJson(map));
    writer.close();
  }

  public void setSupplierDTOGenerator(SupplierDTOGenerator supplierDTOGenerator) {
    this.supplierDTOGenerator = supplierDTOGenerator;
  }

  //更新客户车辆
  @RequestMapping(params = "method=addOrUpdateCustomerVehicle")

  public void addOrUpdateCustomerVehicle(HttpServletRequest request, HttpServletResponse response,
                                         AppointServiceDTO appointServiceDTO) {
    try {
      if(StringUtil.isEmpty(appointServiceDTO.getCustomerId())||StringUtil.isEmpty(appointServiceDTO.getVehicleId())){
        return;
      }
      IUserService userService = ServiceManager.getService(IUserService.class);
      appointServiceDTO.setShopId(WebUtil.getShopId(request));
      userService.addYuyueToCustomerVehicle(appointServiceDTO);
      Long appointServiceId = userService.saveOrUpdateApointServices(appointServiceDTO);

      //add by WLF 生成提醒
      Long customerId = new Long(appointServiceDTO.getCustomerId());
      Long vehicleId = new Long(appointServiceDTO.getVehicleId());
      CustomerDTO customerDTO = userService.getCustomerById(customerId);
      VehicleDTO vehicleDTO = userService.getVehicleById(vehicleId);
      List<CustomerServiceJobDTO> customerServiceJobDTOList = userService.getCustomerServiceJobByCustomerIdAndVehicleId(WebUtil.getShopId(request), customerId, vehicleId);
      if (CollectionUtils.isNotEmpty(customerServiceJobDTOList)) {
        for (CustomerServiceJobDTO customerServiceJobDTO : customerServiceJobDTOList) {
          //保养里程定时钟处理
          if (customerServiceJobDTO != null && !UserConstant.MAINTAIN_MILEAGE.equals(customerServiceJobDTO.getRemindType())) {
            ServiceManager.getService(ITxnService.class).saveRemindEvent(new CustomerServiceJob().fromDTO(customerServiceJobDTO), customerDTO.getName(), customerDTO.getMobile(), vehicleDTO.getLicenceNo());
          }
        }
          //add by WLF 更新缓存
          ServiceManager.getService(ITxnService.class).updateRemindCountInMemcacheByTypeAndShopId(RemindEventType.CUSTOMER_SERVICE,WebUtil.getShopId(request));
      }

      Map<String,String> appointIdMap=new HashMap<String,String>();
      appointIdMap.put("appointServiceId",String.valueOf(appointServiceId));
      PrintWriter writer = response.getWriter();
      writer.write(JsonUtil.mapToJson(appointIdMap));
      writer.close();
    } catch (Exception e) {
      LOG.debug("/unitlink.do");
      LOG.debug("method=addOrUpdateCustomerVehicle");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
    }
  }

  //根据开始、结束时间，查询入库单
  @RequestMapping(params = "method=getPurchaseInventoryHistory")
  @ResponseBody
  public Object getPurchaseInventoryHistory(HttpServletRequest request, HttpServletResponse response){
    try{
      Long shopId = (Long) request.getSession().getAttribute("shopId");
      String id = request.getParameter("supplierId");
      String starttimeStr = request.getParameter("startTime");
      String endtimeStr = request.getParameter("endTime");
      if(starttimeStr==null){
        starttimeStr = "";
      }
      if(endtimeStr==null){
        endtimeStr = "";
      }
      if (id == null) {
        id = (String) request.getAttribute("supplierId");
      }
      if ("0".equals(id)) {
        id = String.valueOf(request.getAttribute("supplierId"));
      }
      Long supplierId = NumberUtil.longValue(id, 0L);

      ISearchService searchService = ServiceManager.getService(ISearchService.class);
      int totalCount = searchService.getPurchaseInventoryHistoryItemIndexSize(shopId, supplierId, starttimeStr, endtimeStr);
      int pageNo = NumberUtil.intValue(request.getParameter("startPageNo"), 1);
      Pager pager = new Pager(totalCount, pageNo, pageSize);
      PurchaseInventoryHistoryDTO purchaseInventoryHistoryDTO = searchService.getPurchaseInventoryHistory(shopId, supplierId, starttimeStr, endtimeStr, pager.getRowStart(), pager.getRowEnd());
      List<ItemIndexDTO> itemIndexDTOList = new ArrayList<ItemIndexDTO>();
      String inventoryTotalMoney = "0";
      if(purchaseInventoryHistoryDTO!=null){
        itemIndexDTOList = purchaseInventoryHistoryDTO.getItemIndexDTOList();
        inventoryTotalMoney = String.valueOf(com.bcgogo.utils.NumberUtil.round(purchaseInventoryHistoryDTO.getInventoryTotalMoney(), NumberUtil.MONEY_PRECISION));
      }
      Map<String,Object> data=new HashMap<String,Object>();
      List<Object> result=new ArrayList<Object>();
      data.put("itemIndexDTOList",itemIndexDTOList);
      data.put("inventoryTotalMoney",inventoryTotalMoney);
      result.add(data);
      result.add(pager);
      return result;
    }catch (Exception e){
      LOG.debug("/unitlink.do");
      LOG.debug("method=getPurchaseInventoryHistory");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
      return null;
    }
  }

  //条件查询客户的消费历史记录
  @RequestMapping(params="method=getCustomerConsumptionHistory")
  @ResponseBody
  public Object getCustomerConsumptionHistory(HttpServletRequest request) throws Exception {
    Long shopId = WebUtil.getShopId(request);
    Long customerId = null;
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    String customerIdStr = request.getParameter("customerId");
    if(!StringUtil.isEmpty(customerIdStr)){
      customerId = Long.parseLong(customerIdStr);
    }

    String startTimeStr = request.getParameter("startTimeStr");
    String endTimeStr = request.getParameter("endTimeStr");
    //日期转换Long
    Long startTime = null;
    Long endTime = null;
    String[] orderTypes = null;
    String orderTypeStr = request.getParameter("orderType");
    if(!StringUtil.isEmpty(orderTypeStr)){
      orderTypeStr = orderTypeStr.substring(1);
      orderTypes = orderTypeStr.split(",");
    }

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

      //默认查出全部时间段、全部单据类型
      List<OrderTypes> orderTypeList = new ArrayList<OrderTypes>();
      boolean flag = false;
      if(!ArrayUtil.isEmpty(orderTypes) && orderTypes.length>0){
        for(int i=0;i<orderTypes.length;i++){
          if(!StringUtil.isEmpty(orderTypes[i])){
            if(orderTypes[i].equals(OrderTypes.REPAIR.toString())){
              orderTypeList.add(OrderTypes.REPAIR);
              orderTypeList.add(OrderTypes.REPAIR_SALE);
              flag = true;
            }
            if(orderTypes[i].equals(OrderTypes.SALE.toString())){
              orderTypeList.add(OrderTypes.SALE);
              flag = true;
            }
            if(orderTypes[i].equals(OrderTypes.SALE_RETURN.toString())){
              orderTypeList.add(OrderTypes.SALE_RETURN);
              flag = true;
            }
            if(orderTypes[i].equals(OrderTypes.WASH_BEAUTY.toString())){
              orderTypeList.add(OrderTypes.WASH);
              orderTypeList.add(OrderTypes.WASH_BEAUTY);
              orderTypeList.add(OrderTypes.WASH_MEMBER);
              flag = true;
            }
            if(orderTypes[i].equals(OrderTypes.MEMBER_BUY_CARD.toString())){
              orderTypeList.add(OrderTypes.RECHARGE);
              orderTypeList.add(OrderTypes.MEMBER_BUY_CARD);
              orderTypeList.add(OrderTypes.MEMBER_RETURN_CARD);
              flag = true;
            }
          }
        }
      }
      if(flag==false){
        orderTypeList.add(OrderTypes.SALE);
        orderTypeList.add(OrderTypes.SALE_RETURN);
        orderTypeList.add(OrderTypes.REPAIR);
        orderTypeList.add(OrderTypes.REPAIR_SALE);
        orderTypeList.add(OrderTypes.WASH);
        orderTypeList.add(OrderTypes.WASH_BEAUTY);
        orderTypeList.add(OrderTypes.WASH_MEMBER);
        orderTypeList.add(OrderTypes.RECHARGE);
        orderTypeList.add(OrderTypes.MEMBER_BUY_CARD);
        orderTypeList.add(OrderTypes.MEMBER_RETURN_CARD);
      }

      ISearchService searchService = ServiceManager.getService(ISearchService.class);
      List<OrderDTO> orderDTOList = searchService.getConsumeOrderHistory(customerId, shopId, startTime, endTime, orderTypeList, null);

      int totalCount = orderDTOList.size();
      int pageNo = NumberUtil.intValue(request.getParameter("startPageNo"), 1);
      Pager pager = new Pager(totalCount, pageNo, pageSize);

      int tmp = 1;
      if (CollectionUtils.isNotEmpty(orderDTOList)) {
        if (pager.getRowEnd() > orderDTOList.size()) {
          orderDTOList.size();
        } else {
          tmp = pager.getRowEnd();
        }
        orderDTOList = orderDTOList.subList(pager.getRowStart(), tmp);
      }
      //转换状态值类型，并且显示还款时间
      if(CollectionUtils.isNotEmpty(orderDTOList)){
        for(OrderDTO orderDTO : orderDTOList){
          if(orderDTO.getStatus()==null) continue;
          if ("已结算".equals(orderDTO.getStatus().getName())) {
            orderDTO.setStatusStr("已结算");
            ReceivableDTO receivableDTO = txnService.getReceivableDTOByShopIdAndOrderId(shopId, orderDTO.getOrderId());
            if (receivableDTO != null) {
              if (receivableDTO.getStatementAccountOrderId() != null) {
                orderDTO.setStatusStr(OrderStatus.STATEMENT_ACCOUNTED.getName());
              } else if (receivableDTO.getDebt() != 0) {
                orderDTO.setArrears(Math.abs(receivableDTO.getDebt()));
                orderDTO.setStatusStr("欠款结算");
              }
            }
          }else{
            orderDTO.setStatusStr(orderDTO.getStatus().getName());
          }
          //施工单的出厂时间
          if(orderDTO.getOrderType().equals(OrderTypes.REPAIR) || orderDTO.getOrderType().equals(OrderTypes.REPAIR_SALE)){
            RepairOrderDTO repairOrderDTO = ServiceManager.getService(ITxnService.class).getRepairOrder(orderDTO.getOrderId());
            orderDTO.setLeaveFactoryTime(repairOrderDTO.getEditDate());
            orderDTO.setLeaveFactoryTimeStr(DateUtil.dateLongToStr(repairOrderDTO.getEndDate(), DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN));
          }
          //去逗号
          String services = orderDTO.getServices();
          if(!StringUtil.isEmpty(services) && ",".equals(services.substring(services.length()-1))){
            services = services.substring(0,services.length()-1);
            orderDTO.setServices(services);
          }
        }
      }

      List<Object> result = new ArrayList<Object>();
      result.add(orderDTOList);
      result.add(pager);
      return result;

    }catch (Exception e){
      LOG.debug("/unitlink.do");
      LOG.debug("method=getCustomerConsumptionHistory");
      LOG.debug("条件查询客户的消费历史记录出错！");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
      return null;
    }
  }

  //查询供应商单据历史记录
  @RequestMapping(params="method=getSupplierOrderHistory")
  @ResponseBody
  public Object getSupplierOrderHistory(HttpServletRequest request) throws Exception{
    Long shopId = WebUtil.getShopId(request);
    Long supplierId = null;
    String supplierIdStr = request.getParameter("supplierId");
    if(!StringUtil.isEmpty(supplierIdStr)){
      supplierId = Long.parseLong(supplierIdStr);
    }

    String startTimeStr = request.getParameter("startTimeStr");
    String endTimeStr = request.getParameter("endTimeStr");
    //日期转换Long
    Long startTime = null;
    Long endTime = null;
    String[] orderTypes = null;
    String orderTypeStr = request.getParameter("orderType");
    if(!StringUtil.isEmpty(orderTypeStr)){
      orderTypeStr = orderTypeStr.substring(1);
      orderTypes = orderTypeStr.split(",");
    }

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

      //默认查出全部时间段、全部单据类型
      List<OrderTypes> orderTypeList = new ArrayList<OrderTypes>();
      boolean flag = false;
      if(!ArrayUtil.isEmpty(orderTypes) && orderTypes.length>0){
        for(int i=0;i<orderTypes.length;i++){
          if(!StringUtil.isEmpty(orderTypes[i])){
            if(orderTypes[i].equals(OrderTypes.PURCHASE.toString())){
              orderTypeList.add(OrderTypes.PURCHASE);
              flag = true;
            }
            if(orderTypes[i].equals(OrderTypes.INVENTORY.toString())){
              orderTypeList.add(OrderTypes.INVENTORY);
              flag = true;
            }
            if(orderTypes[i].equals(OrderTypes.RETURN.toString())){
              orderTypeList.add(OrderTypes.RETURN);
              flag = true;
            }
          }
        }
      }
      if(flag==false){
        orderTypeList.add(OrderTypes.PURCHASE);
        orderTypeList.add(OrderTypes.INVENTORY);
        orderTypeList.add(OrderTypes.RETURN);
      }

      ITxnService txnService = ServiceManager.getService(ITxnService.class);
      IProductService productService = ServiceManager.getService(IProductService.class);

      int totalCount = txnService.getSupplierHistoryOrderList(supplierId, shopId, startTime, endTime, orderTypeList, null).size();
      int pageNo = NumberUtil.intValue(request.getParameter("startPageNo"), 1);
      Pager pager = new Pager(totalCount, pageNo, pageSize);

      List<OrderDTO> orderDTOList = new ArrayList<OrderDTO>();

      //获得全部单据ID和类型 todo 由于OrderIndex中很多记录的vestdate为null
      List<Object[]> objectList = txnService.getSupplierHistoryOrderList(supplierId, shopId, startTime, endTime, orderTypeList, pager);
      //根据单据ID组装item信息
      if(CollectionUtils.isNotEmpty(objectList)){
        for(int i=0;i<objectList.size();i++){
          Long orderId = ((BigInteger)objectList.get(i)[0]).longValue();
          String orderType = (String)objectList.get(i)[1];
          Long vestDate = ((BigInteger)objectList.get(i)[2]).longValue();
          Double total = Double.parseDouble(objectList.get(i)[3]==null?"0":objectList.get(i)[3].toString());
          String receiptNo = (String)objectList.get(i)[4];

          OrderDTO orderDTO = new OrderDTO();
          //采购单
          if(OrderTypes.PURCHASE.toString().equals(orderType)){
            List<PurchaseOrderItemDTO> purchaseOrderItemDTOList = txnService.getPurchaseOrderItemDTOs(orderId);
            //商品总数目
            double productAmount = 0;
            //商品名称
            String material = "";
            if(CollectionUtils.isNotEmpty(purchaseOrderItemDTOList)){
              for(int j=0;j<purchaseOrderItemDTOList.size();j++){
                productAmount = productAmount + purchaseOrderItemDTOList.get(j).getAmount();
                ProductDTO productDTO=productService.getProductByProductLocalInfoId(purchaseOrderItemDTOList.get(j).getProductId(),shopId);
                if(productDTO==null){
                  continue;
                }
                String productName = productDTO.getName();
                material = material + "," + productName;
              }
            }
            if(material.length()>0){
              material = material.substring(1);
            }
            orderDTO.setConsumeDate(vestDate);
            orderDTO.setReceiptNo(receiptNo);
            orderDTO.setOrderType(OrderTypes.PURCHASE);
            orderDTO.setProductAmount(productAmount);
            orderDTO.setTotalMoney(total);
            orderDTO.setMaterial(material);
            orderDTO.setUrl("RFbuy.do?method=show&id="+orderId);
          }
          //入库单
          if(OrderTypes.INVENTORY.toString().equals(orderType)){
            List<PurchaseInventoryItemDTO> purchaseInventoryItemDTOList = txnService.getPurchaseInventoryItemByOrderIds(orderId);
            //商品总数目
            double productAmount = 0;
            //商品名称
            String material = "";
            if(CollectionUtils.isNotEmpty(purchaseInventoryItemDTOList)){
              for(int j=0;j<purchaseInventoryItemDTOList.size();j++){
                productAmount = productAmount + purchaseInventoryItemDTOList.get(j).getAmount();
                ProductDTO productDTO = productService.getProductByProductLocalInfoId(purchaseInventoryItemDTOList.get(j).getProductId(),shopId);
                if(productDTO!=null){
                  material = material + "," + productDTO.getName();
                }
              }
            }
            if(material.length()>0){
              material = material.substring(1);
            }
            orderDTO.setConsumeDate(vestDate);
            orderDTO.setReceiptNo(receiptNo);
            orderDTO.setOrderType(OrderTypes.INVENTORY);
            orderDTO.setProductAmount(productAmount);
            orderDTO.setTotalMoney(total);
            orderDTO.setMaterial(material);
            orderDTO.setUrl("storage.do?method=getPurchaseInventory&purchaseInventoryId="+orderId+"&menu-uid=WEB.TXN.PURCHASE_MANAGE.STORAGE");
          }
          //退货单
          if(OrderTypes.RETURN.toString().equals(orderType)){
            List<PurchaseReturnItemDTO> purchaseReturnItemDTOList = txnService.getPurchaseReturnItemDTOs(orderId);
            //商品总数目
            double productAmount = 0;
            //商品名称
            String material = "";
            if(CollectionUtils.isNotEmpty(purchaseReturnItemDTOList)){
              for(int j=0;j<purchaseReturnItemDTOList.size();j++){
                productAmount = productAmount + purchaseReturnItemDTOList.get(j).getAmount();
                String productName = productService.getProductByProductLocalInfoId(purchaseReturnItemDTOList.get(j).getProductId(),shopId).getName();
                material = material + "," + productName;
              }
            }
            if(material.length()>0){
              material = material.substring(1);
            }
            orderDTO.setConsumeDate(vestDate);
            orderDTO.setReceiptNo(receiptNo);
            orderDTO.setTotalMoney(total);
            orderDTO.setOrderType(OrderTypes.RETURN);
            orderDTO.setProductAmount(productAmount);
            orderDTO.setMaterial(material);
            orderDTO.setUrl("goodsReturn.do?method=showReturnStorageByPurchaseReturnId&purchaseReturnId="+orderId);
          }
          orderDTOList.add(orderDTO);
        }
      }

      String totalMoneyInfo = "";
      String purchaseTotalMoneyInfo = "";
      String inventoryTotalMoneyInfo = "";
      String returnTotalMoneyInfo = "";

      if(orderTypeList.contains(OrderTypes.PURCHASE)){
        Double purchaseTotalMony = txnService.getSupplierTotalMoneyByTimeRangeAndOrderType(shopId,supplierId,startTime,endTime,OrderTypes.PURCHASE.toString());
        if(purchaseTotalMony!=null){
          purchaseTotalMoneyInfo = " 采购金额：" + new DecimalFormat("#,##0.00").format(purchaseTotalMony) + "元， ";
        }
      }
      if(orderTypeList.contains(OrderTypes.INVENTORY)){
        Double inventoryTotalMony = txnService.getSupplierTotalMoneyByTimeRangeAndOrderType(shopId,supplierId,startTime,endTime,OrderTypes.INVENTORY.toString());
        if(inventoryTotalMony!=null){
          inventoryTotalMoneyInfo = " 入库金额：" + new DecimalFormat("#,##0.00").format(inventoryTotalMony) + "元， ";
        }
      }
      if(orderTypeList.contains(OrderTypes.RETURN)){
        Double returnTotalMony = txnService.getSupplierTotalMoneyByTimeRangeAndOrderType(shopId,supplierId,startTime,endTime,OrderTypes.RETURN.toString());
        if(returnTotalMony!=null){
          returnTotalMoneyInfo = " 退货金额：" + new DecimalFormat("#,##0.00").format(returnTotalMony) + "元 ";
        }
      }
      totalMoneyInfo = purchaseTotalMoneyInfo + inventoryTotalMoneyInfo + returnTotalMoneyInfo;
      if(totalMoneyInfo.length()>0 && totalMoneyInfo.substring(totalMoneyInfo.length()-2,totalMoneyInfo.length()).equals("， ")){
        totalMoneyInfo = totalMoneyInfo.substring(0,totalMoneyInfo.length()-2);
      }

      List<Object> result = new ArrayList<Object>();
      List<Object> data = new ArrayList<Object>();
      data.add(orderDTOList);
      data.add(totalMoneyInfo);
      result.add(data);
      result.add(pager);
      return result;

    }catch (Exception e){
      LOG.debug("/unitlink.do");
      LOG.debug("method=getSupplierOrderHistory");
      LOG.debug("条件查询供应商的单据历史记录出错！");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
      return null;
    }
  }
}
