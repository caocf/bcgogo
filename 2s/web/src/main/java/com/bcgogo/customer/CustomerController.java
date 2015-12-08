package com.bcgogo.customer;

import com.bcgogo.PageErrorMsg;
import com.bcgogo.api.ObdDTO;
import com.bcgogo.api.ObdSimBindDTO;
import com.bcgogo.api.gsm.GSMRegisterDTO;
import com.bcgogo.common.*;
import com.bcgogo.common.CookieUtil;
import com.bcgogo.config.cache.AreaCacheManager;
import com.bcgogo.config.cache.BcgogoConcurrentController;
import com.bcgogo.config.dto.AreaDTO;
import com.bcgogo.config.dto.image.DataImageDetailDTO;
import com.bcgogo.config.dto.image.DataImageRelationDTO;
import com.bcgogo.config.dto.image.ImageInfoDTO;
import com.bcgogo.config.model.MergeRecord;
import com.bcgogo.config.service.IApplyService;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.service.image.IImageService;
import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.config.util.ImageUtils;
import com.bcgogo.constant.Constant;
import com.bcgogo.enums.*;
import com.bcgogo.enums.app.ObdType;
import com.bcgogo.enums.config.DataType;
import com.bcgogo.enums.config.ImageScene;
import com.bcgogo.enums.config.ImageType;
import com.bcgogo.enums.user.VehicleBrandModelDataType;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.exception.PageException;
import com.bcgogo.generator.SupplierDTOGenerator;
import com.bcgogo.htmlbuilder.SmsCustomerPageBuilder;
import com.bcgogo.notification.dto.CustomerRemindSms;
import com.bcgogo.notification.service.ISmsService;
import com.bcgogo.product.StandardBrandModelCache.StandardBrandModelCache;
import com.bcgogo.product.service.IBaseProductService;
import com.bcgogo.product.service.IProductService;
import com.bcgogo.search.client.SolrClientHelper;
import com.bcgogo.search.dto.*;
import com.bcgogo.search.service.ISearchService;
import com.bcgogo.search.service.user.ISearchCustomerSupplierService;
import com.bcgogo.search.service.vehicle.ISearchVehicleService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.stat.dto.SupplierRecordDTO;
import com.bcgogo.txn.dto.CarDTO;
import com.bcgogo.txn.dto.ConsumingRecordDTO;
import com.bcgogo.txn.dto.CustomerDepositDTO;
import com.bcgogo.txn.dto.ServiceDTO;
import com.bcgogo.txn.dto.StatementAccount.OrderDebtType;
import com.bcgogo.txn.model.Service;
import com.bcgogo.txn.service.*;
import com.bcgogo.txn.service.app.IAppVehicleService;
import com.bcgogo.txn.service.productThrough.IProductThroughService;
import com.bcgogo.txn.service.recommend.IPreciseRecommendService;
import com.bcgogo.txn.service.solr.ICustomerOrSupplierSolrWriteService;
import com.bcgogo.txn.service.vehicleSearch.IVehicleGenerateService;
import com.bcgogo.user.CustomerVehicleResponse;
import com.bcgogo.user.MergeType;
import com.bcgogo.user.dto.*;
import com.bcgogo.user.merge.MergeCustomerSnap;
import com.bcgogo.user.merge.MergeRecordDTO;
import com.bcgogo.user.merge.MergeResult;
import com.bcgogo.user.merge.SearchMergeResult;
import com.bcgogo.user.model.Customer;
import com.bcgogo.user.model.CustomerServiceJob;
import com.bcgogo.user.model.Supplier;
import com.bcgogo.user.service.*;
import com.bcgogo.user.service.app.IAppUserService;
import com.bcgogo.user.service.obd.IObdManagerService;
import com.bcgogo.user.service.solr.IVehicleSolrWriterService;
import com.bcgogo.user.service.utils.BcgogoShopLogicResourceUtils;
import com.bcgogo.utils.*;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.text.ParseException;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: MZDong
 * Date: 11-11-2
 * Time: 下午6:13
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/customer.do")
public class CustomerController {
  @Autowired
  private ISearchService searchService;
  @Autowired
  private IUserService userService;

  private UnitLinkController unitLinkController;

  public static final Logger LOG = LoggerFactory.getLogger(CustomerController.class);   //Log4j 日志
  public static final int pageSize = 10;                                                //页面显示条数
  public static final int PAGE_SIZE = 15;                                                //页面显示条数
  public static final int DEFAULT_PAGE_NO = 1;  //默认查询页码 1
  public static final int DEFAULT_PAGE_SIZE = 5;//默认页面显示条数

  @Autowired
  private SmsCustomerPageBuilder smsCustomerPageBuilder;

  public void setSmsCustomerPageBuilder(SmsCustomerPageBuilder smsCustomerPageBuilder) {
    this.smsCustomerPageBuilder = smsCustomerPageBuilder;
  }

  public UnitLinkController getUnitLinkController() {
    if (unitLinkController == null) {
      unitLinkController = new UnitLinkController();
    }
    return unitLinkController;
  }


  //ToDo: need to cache.
  private void getServiceTypeList(Map serviceTypeList) {
    serviceTypeList.put("1", "保养");
    serviceTypeList.put("2", "维修");
    serviceTypeList.put("3", "美容");
    serviceTypeList.put("4", "保险");
    serviceTypeList.put("5", "销售");
  }

  @RequestMapping(params = "method=addClient")
  public String addClient(HttpServletRequest request) {
    request.setAttribute("fromPage", request.getParameter("fromPage"));

    //获取页面下拉框信息
    Map<String, String> invoiceCatagoryMap = TxnConstant.getInvoiceCatagoryMap(request.getLocale());
    Map<String, String> areaMap = TxnConstant.getAreaMap(request.getLocale());
    Map<String, String> customerTypeMap = TxnConstant.getCustomerTypeMap(request.getLocale());
    Map<String, String> settlementTyoeMap = TxnConstant.getSettlementTypeMap(request.getLocale());
    request.setAttribute("invoiceCatagoryMap", invoiceCatagoryMap);
    request.setAttribute("areaMap", areaMap);
    request.setAttribute("customerTypeMap", customerTypeMap);
    request.setAttribute("settlementTypeMap", settlementTyoeMap);
    CustomerRecordDTO customerRecordDTO = new CustomerRecordDTO();
    customerRecordDTO.setVehicles(new CarDTO[1]);
    customerRecordDTO.setContacts(new ContactDTO[3]); // add by zhuj 联系人列表
    request.setAttribute("customerRecordDTO", customerRecordDTO);

    boolean isWholesalerVersion = ConfigUtils.isWholesalerVersion(WebUtil.getShopVersionId(request));

    request.setAttribute("wholesalerVersion", isWholesalerVersion);
    request.setAttribute("fourSShopVersions", ConfigUtils.isFourSShopVersion(WebUtil.getShopVersionId(request)));

    if (isWholesalerVersion) {
      return "/customer/wholesalerAddCustomer";
//            return "/customer/addNewCustomer1";

    } else {
      return "/customer/addNewCustomer";
    }

  }

  /**
   * 发送短信选择客户时翻页取数据
   *
   * @param request
   * @param response
   * @throws BcgogoException
   */
  @RequestMapping(params = "method=getSmsOnePageCustomer")
  public void getSmsOnePageCustomer(HttpServletRequest request, HttpServletResponse response) throws BcgogoException {
    getOnePageCustomer(request);
    List<CustomerRecordDTO> customerRecordDTOList = (List<CustomerRecordDTO>) request.getAttribute("customerRecordDTOs");
    Pager pager = (Pager) request.getAttribute("pager");
    String jsonStr = "";
    jsonStr = JsonUtil.listToJson(customerRecordDTOList);
    jsonStr = jsonStr.substring(0, jsonStr.length() - 1);
    if (!"[".equals(jsonStr.trim())) {
      jsonStr = jsonStr + "," + pager.toJson().substring(1, pager.toJson().length());
    } else {
      jsonStr = pager.toJson();
    }

    //String htmlContent = this.smsCustomerPageBuilder.build(customerRecordDTOList, pager);
    try {
      PrintWriter writer = response.getWriter();
      writer.write(jsonStr);
      writer.flush();
      writer.close();
    } catch (IOException e) {
      LOG.error("输出流输出json内容时发生异常！");
      LOG.error(e.getMessage(), e);
    }
  }

  /**
   * 获取一页客户数据
   *
   * @param request
   * @throws BcgogoException
   */
  private void getOnePageCustomer(HttpServletRequest request) throws BcgogoException {
    IUserService userService = ServiceManager.getService(IUserService.class);
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    if (shopId == null) {
      return;
    }
    int customerNumber = 0;
    try {
      customerNumber = (int) userService.countShopCustomerRecord(shopId);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
    Pager pager = new Pager(customerNumber, NumberUtil.intValue(request.getParameter("startPageNo"), DEFAULT_PAGE_NO), DEFAULT_PAGE_SIZE);
    List<CustomerRecordDTO> customerRecordDTOList = userService.getSmsCustomerInfoList(shopId, pager.getCurrentPage(), pager.getPageSize());
    if (customerRecordDTOList != null && !customerRecordDTOList.isEmpty()) {
      request.setAttribute("customerRecordDTOs", customerRecordDTOList);
    }
//        request.setAttribute("pageNo", pager.getCurrentPage());
//        request.setAttribute("pageCount", pager.getTotalRows());
    request.setAttribute("pager", pager);
    request.setAttribute("customerSize", customerNumber);
  }

  @RequestMapping(params = "method=addcustomer")
  @ResponseBody
  public Object addCustomer(HttpServletRequest request, HttpServletResponse response, ModelMap model, CustomerRecordDTO customerRecordDTO) throws Exception {
    IUserService userService = ServiceManager.getService(IUserService.class);
    IProductService productService = ServiceManager.getService(IProductService.class);
    IPreciseRecommendService preciseRecommendService = ServiceManager.getService(IPreciseRecommendService.class);
    ICustomerService customerService = ServiceManager.getService(ICustomerService.class);
    Result result = new Result();
    //验证  客户名必须存在
    String customerName = customerRecordDTO.getName();
    if (StringUtils.isBlank(customerName)) {
      return new Result("客户名必须填写", false);
    }
    Long shopId = WebUtil.getShopId(request);
    Long userId = WebUtil.getUserId(request);
    customerRecordDTO.setShopId(shopId);
    result.setMsg("");
    customerService.validateAddCustomer(customerRecordDTO, result);
    if (result != null && !result.isSuccess()) {
      return result;
    }
    CustomerDTO customerDTO = null;
    if (null == customerRecordDTO.getCustomerId()) { //如果用户id不存在，创建用户
      customerDTO = new CustomerDTO();
      try {
        customerDTO.fromCustomerRecordDTO(customerRecordDTO, false, false);
        customerDTO.setRelationType(RelationTypes.UNRELATED);
      } catch (ParseException e) {
        LOG.info("/customer.do");
        LOG.info("method=addcustomer");
        LOG.info("客户生日转换出错");
        LOG.info(customerRecordDTO.toString());
        LOG.info("shopId:{},userId:{}", shopId, userId);
        LOG.error(e.getMessage(), e);
      }

      SupplierDTO supplierDTO = null;
      if (customerRecordDTO.getSupplierId() != null) {
        supplierDTO = ServiceManager.getService(ISupplierService.class).getSupplierById(customerRecordDTO.getSupplierId(), shopId);
        supplierDTO.fromCustomerRecordDTO(customerRecordDTO);
        supplierDTO.setBusinessScope(customerRecordDTO.getBusinessScope());
        supplierDTO.setIdentity("isCustomer");
        userService.updateSupplier(supplierDTO);
        searchService.updateItemIndexSupplier(supplierDTO);
        searchService.updateOrderIndexSupplier(supplierDTO);
        ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexSupplierBySupplierId(supplierDTO.getId());
        customerDTO.setSupplierId(customerRecordDTO.getSupplierId());
      } else if (customerRecordDTO.getIdentity() != null && !"".equals(customerRecordDTO.getIdentity())) {
        supplierDTO = new SupplierDTO();
        supplierDTO.setShopId(shopId);
        supplierDTO.fromCustomerRecordDTO(customerRecordDTO);
        supplierDTO.setIdentity("isCustomer");
        SupplierDTO dto = userService.createSupplier(supplierDTO);
        ServiceManager.getService(ISupplierRecordService.class).createSupplierRecordUsingSupplierDTO(supplierDTO);
        ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexSupplierBySupplierId(dto.getId());
        customerDTO.setSupplierId(dto.getId());
      }
      userService.createCustomer(customerDTO); // 新增用户

      if (ConfigUtils.isWholesalerVersion(WebUtil.getShopVersionId(request))) {
        //更新客户或者供应商的经营范围
        userService.saveOrUpdateCustomerSupplierBusinessScope(customerDTO, supplierDTO, customerRecordDTO.getThirdCategoryIdStr());
        preciseRecommendService.getCustomerSupplierBusinessScopeForAdd(customerDTO, supplierDTO);
        userService.updateCustomerSupplierBusinessScope(customerDTO, supplierDTO);
      }

      // add by zhuj 更新客户供应商的联系人信息
      if (customerRecordDTO.getSupplierId() != null) { // 更新原有的供应商的联系人信息 更新为 客户、供应商 共有的联系人
        ServiceManager.getService(IContactService.class).updateContactsBelongCustomerAndSupplier(customerRecordDTO.getCustomerId(), customerRecordDTO.getSupplierId(), shopId, customerDTO.getContacts());
        ServiceManager.getService(IContactService.class).addContactsBelongCustomerAndSupplier(customerDTO.getId(), customerDTO.getSupplierId(), shopId, customerDTO.getContacts());
      }

      CarDTO[] carDTOs = customerRecordDTO.getVehicles();
      List<VehicleDTO> vehicleDTOList = productService.saveOrUpdateVehicleInfo(shopId, userId, customerDTO.getId(), carDTOs);

      if (ConfigUtils.isFourSShopVersion(WebUtil.getShopVersionId(request))) {
        IAppVehicleService appVehicleService = ServiceManager.getService(IAppVehicleService.class);
        appVehicleService.syncAppVehicle(vehicleDTOList);
        //绑定OBD
        IObdManagerService obdManagerService = ServiceManager.getService(IObdManagerService.class);
        if (!ArrayUtil.isEmpty(carDTOs)) {
          for (CarDTO carDTO : carDTOs) {
            obdManagerService.gsmOBDBind(CollectionUtil.getFirst(vehicleDTOList), carDTO.getGsmObdImei(),
              carDTO.getGsmObdImeiMoblie(), WebUtil.getUserId(request), WebUtil.getShopName(request), WebUtil.getUserName(request));
            ObdDTO obdDTO = obdManagerService.getObdByImei(carDTO.getGsmObdImei());
            if (obdDTO != null && obdDTO.getObdType() != null) {
              //后视镜自动分配帐号
              if (ObdType.MIRROR.equals(obdDTO.getObdType())) {
                GSMRegisterDTO gsmRegisterDTO = new GSMRegisterDTO();
                gsmRegisterDTO.setImei(carDTO.getGsmObdImei());
                ServiceManager.getService(IAppUserService.class).gsmAllocateAppUser(gsmRegisterDTO);
              }
            }
          }
        }

//        CollectionUtil.getFirst(vehicleDTOList).setGsmObdImei(gsmObdImei);
//        CollectionUtil.getFirst(vehicleDTOList).setGsmObdImeiMoblie(gsmObdImeiMoblie);
      }
      customerRecordDTO.setCustomerId(customerDTO.getId());
      request.getSession().setAttribute("customerId", customerDTO.getId());
      customerRecordDTO.setShopId(shopId);
      userService.createCustomerRecord(customerRecordDTO);
      if (customerRecordDTO.getIdentity() != null && !"".equals(customerRecordDTO.getIdentity())) {
        SupplierDTO relatedSupplier = ServiceManager.getService(ISupplierService.class).getSupplierById(customerDTO.getSupplierId(), shopId);
        relatedSupplier.setCustomerId(customerDTO.getId());
        userService.updateSupplier(relatedSupplier);
        // add by zhuj 新增联系人 既是客户又是供应商的 联系人由这边外部控制 这边拥有同时customer 和 supplier的上下文
        ServiceManager.getService(IContactService.class).addContactsBelongCustomerAndSupplier(customerDTO.getId(), customerDTO.getSupplierId(), shopId, customerDTO.getContacts());
        ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexSupplierBySupplierId(relatedSupplier.getId());
      }
    } else {
      customerDTO = userService.getCustomerById(customerRecordDTO.getCustomerId());
      if (null != customerDTO) {
        customerRecordDTO.setCustomerShopId(customerDTO.getCustomerShopId());
        customerRecordDTO.setRelationType(customerDTO.getRelationType());
        customerRecordDTO.setIdentity(customerDTO.getIdentity());
        customerRecordDTO.setSupplierId(customerDTO.getSupplierId());
        try {
          customerDTO.fromCustomerRecordDTO(customerRecordDTO, false, false);
        } catch (ParseException e) {
          LOG.info("/customer.do method=addcustomer");
          LOG.info(customerRecordDTO.toString());
          LOG.info("shopId:{},userId:{}", shopId, userId);
          LOG.error(e.getMessage(), e);
        }
        userService.updateCustomer(customerDTO);
//        userService.saveOrUpdateCarsWithCustomerId(customerDTO.getId(), shopId, customerRecordDTO.getVehicles());
        List<VehicleDTO> vehicleDTOList = productService.saveOrUpdateVehicleInfo(shopId, userId, customerDTO.getId(), customerRecordDTO.getVehicles());
        if (ConfigUtils.isFourSShopVersion(WebUtil.getShopVersionId(request))) {
          IAppVehicleService appVehicleService = ServiceManager.getService(IAppVehicleService.class);
          appVehicleService.syncAppVehicle(vehicleDTOList);
        }
        userService.updateCustomerRecord(customerRecordDTO);    //需要进行事务控制 1.在updateCustomer中进行  2。在此处进行事务控制
        SupplierDTO supplierDTO = null;
        if (customerDTO.getSupplierId() != null) {
          supplierDTO = userService.getSupplierById(customerDTO.getSupplierId());
          supplierDTO.fromCustomerRecordDTO(customerRecordDTO);
          userService.updateSupplier(supplierDTO);
          // add by zhuj 这里需要处理新增的联系人
          List<ContactDTO> contactDTOList = new ArrayList<ContactDTO>();
          ContactDTO[] contactDTOs = customerDTO.getContacts();
          if (!ArrayUtils.isEmpty(contactDTOs)) {
            for (ContactDTO contactDTO : contactDTOs) {
              if (contactDTO != null && contactDTO.isValidContact() && (contactDTO.getId() == null || contactDTO.getId() == 0L)) {
                contactDTOList.add(contactDTO);
              }
            }
          }
          if (!CollectionUtils.isEmpty(contactDTOList)) {
            ServiceManager.getService(IContactService.class).addContactsBelongCustomerAndSupplier(customerDTO.getId(),
              customerDTO.getSupplierId(), shopId, contactDTOList.toArray(new ContactDTO[contactDTOList.size()]));
          }
          // add end
          ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexSupplierBySupplierId(supplierDTO.getId());
        }

        if (ConfigUtils.isWholesalerVersion(WebUtil.getShopVersionId(request))) {
          //更新客户或者供应商的经营范围
          userService.saveOrUpdateCustomerSupplierBusinessScope(customerDTO, supplierDTO, customerRecordDTO.getThirdCategoryIdStr());
          preciseRecommendService.getCustomerSupplierBusinessScopeForAdd(customerDTO, supplierDTO);
          userService.updateCustomerSupplierBusinessScope(customerDTO, supplierDTO);
        }
      }
    }
    if (customerDTO != null) {
      ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexCustomerByCustomerId(customerDTO.getId());
    }
    String pageTip = request.getParameter("pageTip");
    request.setAttribute("pageTip", pageTip);
    result.setData(customerDTO == null ? null : customerDTO.getId().toString());
    return result;
//    return customerData(model, request, response);
  }


  @RequestMapping(params = "method=checkCustomerExist")
  public void checkCustomerExist(HttpServletRequest request, HttpServletResponse response) throws Exception {
    String mobile = request.getParameter("mobile");
    if (mobile != null && !mobile.trim().equals("")) {
      Long shopId = (Long) request.getSession().getAttribute("shopId");
      IUserService userService = ServiceManager.getService(IUserService.class);
      List<CustomerDTO> customerDTOList = userService.getCustomerByMobile(shopId, mobile);
      if (customerDTOList != null && !customerDTOList.isEmpty()) {
        StringBuffer sb = new StringBuffer();
        sb.append("{\"customers\":[");
        for (int m = 0; m < customerDTOList.size() - 1; m++) {
          CustomerDTO customerDTO = customerDTOList.get(m);
          List<CustomerRecordDTO> customerRecordDTOList = userService.getCustomerRecordByCustomerId(customerDTO.getId());
          Long customerRecordId = null;
          if (customerRecordDTOList != null && !customerRecordDTOList.isEmpty()) {
            customerRecordId = customerRecordDTOList.get(0).getId();
          }
          Long birthday = customerDTO.getBirthday();
          String birthStr = DateUtil.convertDateLongToDateString("yyyy-MM-dd", birthday);
          sb.append("{\"idStr\":\"" + customerDTO.getIdStr() + "\",\"customerName\":\"" + customerDTO.getName() + "\",\"mobile\":\"" + customerDTO.getMobile() + "\"," +
            "\"shortName\":\"" + customerDTO.getShortName() + "\",\"address\":\"" + customerDTO.getAddress() + "\",\"contact\":\"" + customerDTO.getContact() + "\"," +
            "\"phone\":\"" + customerDTO.getLandLine() + "\",\"fax\":\"" + customerDTO.getFax() + "\",\"memberNo\":\"" + customerDTO.getMemo() + "\"," +
            "\"area\":\"" + customerDTO.getArea() + "\",\"birth\":\"" + birthStr + "\"," +
            "\"qq\":\"" + customerDTO.getQq() + "\",\"email\":\"" + customerDTO.getEmail() + "\"," +
            "\"bank\":\"" + customerDTO.getBank() + "\",\"bankAccountName\":\"" + customerDTO.getBankAccountName() + "\"," +
            "\"account\":\"" + customerDTO.getAccount() + "\",\"invoiceCategory\":\"" + customerDTO.getInvoiceCategory() + "\"," +
            "\"settlementType\":\"" + customerDTO.getSettlementType() + "\",\"customerKind\":\"" + customerDTO.getCustomerKind() + "\",\"shopId\":\"" + shopId + "\"," +
            "\"customerRecordId\":\"" + (customerRecordId == null ? "" : customerRecordId.toString()) + "\"},");
        }


        CustomerDTO customerDTO = customerDTOList.get(customerDTOList.size() - 1);
        List<CustomerRecordDTO> customerRecordDTOList = userService.getCustomerRecordByCustomerId(customerDTO.getId());
        Long customerRecordId = null;
        if (customerRecordDTOList != null && !customerRecordDTOList.isEmpty()) {
          customerRecordId = customerRecordDTOList.get(0).getId();
        }
        Long birthday = customerDTO.getBirthday();
        String birthStr = DateUtil.convertDateLongToDateString("yyyy-MM-dd", birthday);
        sb.append("{\"idStr\":\"" + customerDTO.getIdStr() + "\",\"customerName\":\"" + customerDTO.getName() + "\",\"mobile\":\"" + customerDTO.getMobile() + "\"," +
          "\"shortName\":\"" + customerDTO.getShortName() + "\",\"address\":\"" + customerDTO.getAddress() + "\",\"contact\":\"" + customerDTO.getContact() + "\"," +
          "\"phone\":\"" + customerDTO.getLandLine() + "\",\"fax\":\"" + customerDTO.getFax() + "\",\"memberNo\":\"" + customerDTO.getMemo() + "\"," +
          "\"area\":\"" + customerDTO.getArea() + "\",\"birth\":\"" + birthStr + "\"," +
          "\"qq\":\"" + customerDTO.getQq() + "\",\"email\":\"" + customerDTO.getEmail() + "\"," +
          "\"bank\":\"" + customerDTO.getBank() + "\",\"bankAccountName\":\"" + customerDTO.getBankAccountName() + "\"," +
          "\"account\":\"" + customerDTO.getAccount() + "\",\"invoiceCategory\":\"" + customerDTO.getInvoiceCategory() + "\"," +
          "\"settlementType\":\"" + customerDTO.getSettlementType() + "\",\"customerKind\":\"" + customerDTO.getCustomerKind() + "\",\"shopId\":\"" + shopId + "\"," +
          "\"customerRecordId\":\"" + (customerRecordId == null ? "" : customerRecordId.toString()) + "\"}]}");

        PrintWriter out = response.getWriter();
        out.write(sb.toString());
        out.close();
      }
    }
  }

  /**
   * 客户管理-客户资料
   *
   * @param model
   * @param request
   * @return
   * @throws BcgogoException
   */
  @RequestMapping(params = "method=customerdata")
  public String customerData(ModelMap model, HttpServletRequest request, HttpServletResponse response) throws BcgogoException, ParseException {
    LOG.debug("查看客户列表开始!");
    long begin = System.currentTimeMillis();
    long current = begin;
    //获得客户卡的类型
    try {
      List<String> memberCardTypes = ServiceManager.getService(IMembersService.class).getMemberCardTypeByShopId(WebUtil.getShopId(request));
      //去掉洗车卡
      Iterator<String> iterator = memberCardTypes.iterator();
      StringBuilder cardNames = new StringBuilder();
      boolean hasWashCard = false;
      while (iterator.hasNext()) {
        String cardName = iterator.next();
        if ("洗车卡".equals(cardName)) {
          hasWashCard = true;
        }
        cardNames.append(cardName).append(",");
      }
      if (!hasWashCard) cardNames.append("洗车卡").append(",");
      if ("true".equals(request.getParameter("resetSearchCondition"))) {
        model.addAttribute("resetSearchCondition", true);
      }
      model.addAttribute("memberCardTypes", memberCardTypes);
      model.addAttribute("customerIds", request.getParameter("customerIds"));
      model.addAttribute("cardNames", StringUtil.subString(cardNames.toString()));
      request.setAttribute("wholesalerVersion", ConfigUtils.isWholesalerVersion(WebUtil.getShopVersionId(request)));
      model.addAttribute("fourSShopVersions", ConfigUtils.isFourSShopVersion(WebUtil.getShopVersionId(request)));

      //用户引导
      CookieUtil.rebuildCookiesForUserGuide(request, response, true, new String[]{"CONTRACT_CUSTOMER_GUIDE_BEGIN", "CONTRACT_CUSTOMER_GUIDE_CUSTOMER_DATA", "CONTRACT_MESSAGE_NOTICE_CHECK_MESSAGE"});

    } catch (Exception e) {
      LOG.error("/customer.customerdata");
      LOG.error("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
    }

    LOG.debug("跳转至客户列表结束! 总时间: {} ms", System.currentTimeMillis() - begin);
    return "/customer/myCustomer";
  }


  @ResponseBody
  @RequestMapping(params = "method=searchCustomerDataAction")
  public Object searchCustomerDataAction(ModelMap modelMap, HttpServletRequest request, CustomerSupplierSearchConditionDTO searchConditionDTO, JoinSearchConditionDTO joinSearchConditionDTO) {
    StopWatch sw = new StopWatch("searchCustomerDataAction");
    sw.start("query solr");
    List<Object> returnList = new ArrayList<Object>();
    Pager pager = null;

    CustomerSupplierSearchResultListDTO searchResultListDTO = null;
    IUserService userService = ServiceManager.getService(IUserService.class);
    Long shopId = WebUtil.getShopId(request);
    try {
      searchConditionDTO.setShopId(shopId);
      searchConditionDTO.setStart((searchConditionDTO.getStartPageNo() - 1) * searchConditionDTO.getMaxRows());
      searchConditionDTO.setRows(searchConditionDTO.getMaxRows());
      ISearchCustomerSupplierService searchService = ServiceManager.getService(ISearchCustomerSupplierService.class);
      //排序
      if (StringUtils.isBlank(searchConditionDTO.getSearchWord()) && StringUtils.isBlank(searchConditionDTO.getSort())) {
        searchConditionDTO.setSort("created_time desc");
      }
      if (!joinSearchConditionDTO.isEmptyOfProductInfo()) {
        joinSearchConditionDTO.setShopId(WebUtil.getShopId(request));
        joinSearchConditionDTO.setFromColumn("customer_or_supplier_id");
        joinSearchConditionDTO.setToColumn("id");
        joinSearchConditionDTO.setFromIndex(SolrClientHelper.BcgogoSolrCore.ORDER_ITEM_CORE.getValue());
        joinSearchConditionDTO.setItemTypes(ItemTypes.MATERIAL);
        joinSearchConditionDTO.setOrderTypes(new String[]{OrderTypes.SALE.toString(), OrderTypes.SALE_RETURN.toString(), OrderTypes.REPAIR.toString()});
        joinSearchConditionDTO.setOrderStatus(new String[]{OrderStatus.SALE_DONE.toString(), OrderStatus.SETTLED.toString(), OrderStatus.REPAIR_SETTLED.toString()});
        searchConditionDTO.setJoinSearchConditionDTO(joinSearchConditionDTO);
      }
      if (searchConditionDTO.getTodayAdd() != null && searchConditionDTO.getTodayAdd().booleanValue()) {
        ServiceManager.getService(IRepairService.class).getCustomerByTodayServiceVehicle(shopId, searchConditionDTO);
      }
      searchResultListDTO = searchService.queryCustomerWithUnknownField(searchConditionDTO);
//      searchResultListDTO.setCustomerRecordDTOs(userService.getShopCustomerRecordDTOBySolrSearchResult(searchResultListDTO.getCustomerSuppliers()));
      //显示处理
      if (CollectionUtils.isEmpty(searchResultListDTO.getCustomerSuppliers())) {
        returnList = new ArrayList<Object>();
        returnList.add(searchResultListDTO);
        pager = new Pager(Integer.valueOf(searchResultListDTO.getNumFound() + ""), searchConditionDTO.getStartPageNo(), searchConditionDTO.getMaxRows());
        returnList.add(pager);
        return returnList;
      }
      sw.stop();
      sw.start("get debt related");
      List<Long> customerIds = new ArrayList<Long>();
      for (CustomerSupplierSearchResultDTO resultDTO : searchResultListDTO.getCustomerSuppliers()) {
        customerIds.add(resultDTO.getId());
      }
      Long[] customerIdArr = customerIds.toArray(new Long[customerIds.size()]);
      Map<Long, Object[]> returnDataMap = ServiceManager.getService(ITxnService.class).getTotalReturnAmountByCustomerIds(shopId, customerIdArr);
      //组装app,OBD 信息
      IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);
      appUserService.generateAppInfo(searchResultListDTO.getCustomerSuppliers());

      sw.stop();
      sw.start("member related");
      for (CustomerSupplierSearchResultDTO resultDTO : searchResultListDTO.getCustomerSuppliers()) {
        //获得该卡拥有的服务
        MemberDTO memberDTO = ServiceManager.getService(IMembersService.class).getMemberByCustomerId(resultDTO.getShopId(), resultDTO.getId());
        resultDTO.setVehicleCount(resultDTO.getVehicleDetailList() == null ? 0 : resultDTO.getVehicleDetailList().size());
        resultDTO.setMemberDTO(memberDTO);
        if (memberDTO != null) {
          if (memberDTO.getMemberServiceDTOs() != null) {
            RFITxnService txnService = ServiceManager.getService(RFITxnService.class);
            for (MemberServiceDTO memberServiceDTO : memberDTO.getMemberServiceDTOs()) {
              Service service = txnService.getServiceById(memberServiceDTO.getServiceId());
              if (service != null) {
                memberServiceDTO.setServiceName(service.getName());
              }
            }
          }
        }
        Object[] returnData = returnDataMap.get(resultDTO.getId());
        if (returnData != null) {
          resultDTO.setTotalReturnAmount(Double.parseDouble(returnData[0].toString()));
          resultDTO.setCountCustomerReturn(Integer.parseInt(returnData[1].toString()));
        }
      }
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }

    try {
      returnList = new ArrayList<Object>();
      int numFount = Integer.valueOf((searchResultListDTO == null ? 0 : searchResultListDTO.getNumFound()) + "");
      if (searchConditionDTO.getTodayAdd() != null && searchConditionDTO.getTodayAdd().booleanValue() && searchConditionDTO.getToDayAddCustomer().first > 0) {
        numFount = searchConditionDTO.getToDayAddCustomer().first;
        List<String> licenceNoList = ServiceManager.getService(IRepairService.class).getTodayServiceVehicleByCustomerId(shopId, searchConditionDTO.getToDayAddCustomer().second);
        searchResultListDTO.setLicenceNoList(licenceNoList);
      }
      pager = new Pager(searchResultListDTO == null ? 0 : numFount, searchConditionDTO.getStartPageNo(), searchConditionDTO.getMaxRows());
      returnList.add(searchResultListDTO);

      returnList.add(pager);

    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
    sw.stop();
    LOG.debug(sw.toString());
    return returnList;
  }

  @RequestMapping(params = "method=sendOBDMsg", method = RequestMethod.GET)
  public String sendOBDMsg(HttpServletRequest request, HttpServletResponse response, String mobile) {
    return getUnitLinkController().smsHistory(request, response, mobile);
  }

  @RequestMapping(params = "method=sendMsgBySearchCondition")
  public String sendMsgBySearchCondition(HttpServletRequest request, ModelMap modelMap, CustomerSupplierSearchConditionDTO searchConditionDTO, JoinSearchConditionDTO joinSearchConditionDTO) {
    ISearchCustomerSupplierService searchService = ServiceManager.getService(ISearchCustomerSupplierService.class);
    CustomerSupplierSearchResultListDTO searchResultListDTO = null;
    StringBuilder contactIds = new StringBuilder();
    SmsController smsController = new SmsController();
    try {
      searchConditionDTO.setShopId(WebUtil.getShopId(request));
      if (!joinSearchConditionDTO.isEmptyOfProductInfo()) {
        joinSearchConditionDTO.setShopId(WebUtil.getShopId(request));
        joinSearchConditionDTO.setFromColumn("customer_or_supplier_id");
        joinSearchConditionDTO.setToColumn("id");
        joinSearchConditionDTO.setFromIndex(SolrClientHelper.BcgogoSolrCore.ORDER_ITEM_CORE.getValue());
        joinSearchConditionDTO.setItemTypes(ItemTypes.MATERIAL);
        joinSearchConditionDTO.setOrderTypes(new String[]{OrderTypes.SALE.toString(), OrderTypes.SALE_RETURN.toString(), OrderTypes.REPAIR.toString()});
        joinSearchConditionDTO.setOrderStatus(new String[]{OrderStatus.SALE_DONE.toString(), OrderStatus.SETTLED.toString(), OrderStatus.REPAIR_SETTLED.toString()});
        searchConditionDTO.setJoinSearchConditionDTO(joinSearchConditionDTO);
      }
      searchResultListDTO = searchService.queryCustomerMobiles(searchConditionDTO);
      for (CustomerSupplierSearchResultDTO customerSearchResultDTO : searchResultListDTO.getCustomerSuppliers()) {
        if (CollectionUtils.isNotEmpty(customerSearchResultDTO.getContactDTOList())) {
          for (ContactDTO contactDTO : customerSearchResultDTO.getContactDTOList()) {
            if (contactDTO.getId() != null && StringUtils.isNotBlank(contactDTO.getMobile())) {
              contactIds.append(contactDTO.getId()).append(",");
            }
          }
        }
      }
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
    return smsController.smsWrite(request, modelMap, contactIds.toString(), new CustomerRemindSms());
  }


  /**
   * 通过memberId获得 memberCard 各个服务
   *
   * @param shopId type
   * @return
   * @returnExample :"更换机油12月内不限次,洗车10月内20次"
   */
  private String getMemberCardServiceByMemberId(Long shopId, String type) throws Exception {
    IMembersService membersService = ServiceManager.getService(IMembersService.class);
    MemberCardDTO memberCardDTO = membersService.getMemberCardDTOByCardName(shopId, type);
    if (memberCardDTO == null) return "";
    List<MemberCardServiceDTO> memberCardServiceDTOs = membersService.getMemberCardServiceDTOByMemberCardIdAndStatus(memberCardDTO.getId());
    StringBuilder cardServices = new StringBuilder();
    boolean start = true;
    if (CollectionUtils.isEmpty(memberCardServiceDTOs)) return "";
    for (MemberCardServiceDTO memberCardServiceDTO : memberCardServiceDTOs) {
      ServiceDTO serviceDTO = ServiceManager.getService(ITxnService.class).getServiceById(memberCardServiceDTO.getServiceId());
      if (null != serviceDTO && StringUtils.isNotBlank(serviceDTO.getName())) {
        memberCardServiceDTO.setServiceName(serviceDTO.getName());
      }
      if (!start) {
        cardServices.append(",");
      }
      start = false;
      if (memberCardServiceDTO.getTimes() == -1 && memberCardServiceDTO.getTerm() != -1) {
        if (StringUtils.isNotBlank(memberCardServiceDTO.getServiceName())) {
          cardServices.append(memberCardServiceDTO.getServiceName());
        }
        cardServices.append(memberCardServiceDTO.getTerm()).append("月内不限次");
      }
      if (memberCardServiceDTO.getTimes() != -1 && memberCardServiceDTO.getTerm() == -1) {
        if (StringUtils.isNotBlank(memberCardServiceDTO.getServiceName())) {
          cardServices.append(memberCardServiceDTO.getServiceName());
        }
        cardServices.append(memberCardServiceDTO.getTimes()).append("次不限期");
      }
      if (memberCardServiceDTO.getTimes() == -1 && memberCardServiceDTO.getTerm() == -1) {
        if (StringUtils.isNotBlank(memberCardServiceDTO.getServiceName())) {
          cardServices.append(memberCardServiceDTO.getServiceName());
        }
        cardServices.append("不限次不限期");
      }
      if (memberCardServiceDTO.getTimes() != -1 && memberCardServiceDTO.getTerm() != -1) {
        if (StringUtils.isNotBlank(memberCardServiceDTO.getServiceName())) {
          cardServices.append(memberCardServiceDTO.getServiceName());
        }
        cardServices.append(memberCardServiceDTO.getTerm()).append("月内").append(memberCardServiceDTO.getTimes()).append("次");
      }
    }
    return cardServices.toString();
  }

  @RequestMapping(params = "method=customerarrears")
  public String customerArrears(HttpServletRequest request) throws BcgogoException {
    if (request.getSession() == null || request.getSession().getAttribute("shopId") == null) {
      return "/";
    }
    Long shopId = Long.parseLong(request.getSession().getAttribute("shopId").toString());
    IUserService userService = ServiceManager.getService(IUserService.class);
    IRunningStatService runningStatService = ServiceManager.getService(IRunningStatService.class);
    //ToDo: total numbers needs to carry to next page.
    //欠款CustomerRecord总数
    int arrearsCustomerNumber = (int) userService.countShopArrearsCustomerRecord(shopId);

    Pager pager = new Pager(arrearsCustomerNumber, NumberUtil.intValue(request.getParameter("pageNo"), DEFAULT_PAGE_NO), PAGE_SIZE);

    //欠款客户
    List<CustomerRecordDTO> arrearsCustomerRecordDTOList = userService.getShopArrearsCustomerRecord(shopId, pager.getCurrentPage() - 1, pager.getPageSize());      //分页查询欠款客户

    //ToDo: need to calculate when save orders.
    //欠款总额
    double totalReceivable = NumberUtil.toReserve(runningStatService.getTotalDebtByShopId(shopId, OrderDebtType.CUSTOMER_DEBT_RECEIVABLE), NumberUtil.MONEY_PRECISION);
    request.setAttribute("arrearsCustomerRecordDTOList", arrearsCustomerRecordDTOList);     //欠款客户
    request.setAttribute("pager", pager);
    request.setAttribute("totalReceivable", NumberUtil.roundToString(totalReceivable, 2));
    return "/customer/customerarrears";
  }

  @RequestMapping(params = "method=searchcustomer")
  public String searchCustomer(HttpServletRequest request, ModelMap model) throws Exception {

    String searchType = request.getParameter("searchType");
    String searchContent = request.getParameter("searchContent");
    searchContent = java.net.URLDecoder.decode(searchContent, "UTF-8");
    IUserService userService = ServiceManager.getService(IUserService.class);
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    model.addAttribute("customerTypeMap", TxnConstant.getCustomerTypeMap(request.getLocale()));
    if (shopId == null) {
      model.addAttribute("searchType", searchType);
      model.addAttribute("searchContent", searchContent);
      model.addAttribute("totalNumber", 0);
      return "/customer/customerdata";
    }
    List<CustomerRecordDTO> customerRecordDTOList = new ArrayList<CustomerRecordDTO>();
    if (searchType != null) {
      if (searchType.trim().equals("name")) {
        customerRecordDTOList = userService.getShopCustomerRecordByName(shopId, searchContent);
      } else if (searchType.trim().equals("licenecNo")) {
        customerRecordDTOList = userService.getShopCustomerRecordByLicenceNo(shopId, searchContent);
      } else if (searchType.trim().equals("mobile")) {
        customerRecordDTOList = userService.getShopCustomerRecordByMobile(shopId, searchContent);
      }
    }
    int totalNumber = customerRecordDTOList.size();
    model.addAttribute("customerRecordDTOList", customerRecordDTOList);
    model.addAttribute("searchType", searchType);
    model.addAttribute("searchContent", searchContent);
    model.addAttribute("totalNumber", totalNumber);
    return "/customer/customerdata";
  }


  @RequestMapping(params = "method=searcharrears")
  public String searchCustomerArrears(HttpServletRequest request) throws Exception {

    String searchType = request.getParameter("searchType");
    String searchContent = request.getParameter("searchContent");
    searchContent = java.net.URLDecoder.decode(searchContent, "UTF-8");
    IUserService userService = ServiceManager.getService(IUserService.class);
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    if (shopId == null) {
      request.setAttribute("searchType", searchType);
      request.setAttribute("searchContent", searchContent);
      request.setAttribute("arrearsCustomerNumber", 0);
      request.setAttribute("totalReceivable", 0);
      return "/customer/customerarrears";
    }

    List<CustomerRecordDTO> arrearsCustomerRecordDTOList = userService.getShopArrearsCustomerRecord(shopId, 0, 10);
    int arrearsCustomerNumber = 0;
    double totalReceivable = 0;
    if (!arrearsCustomerRecordDTOList.isEmpty()) {
      arrearsCustomerNumber = arrearsCustomerRecordDTOList.size();
      request.setAttribute("arrearsCustomerRecordDTOList", arrearsCustomerRecordDTOList);
    }
    request.setAttribute("searchType", searchType);
    request.setAttribute("searchContent", searchContent);
    request.setAttribute("arrearsCustomerNumber", arrearsCustomerNumber);
    request.setAttribute("totalReceivable", totalReceivable);

    return "/customer/customerarrears";
  }

  //查找客户
  @RequestMapping(params = "method=searchCustomerByName")
  @ResponseBody
  public AllListResult<CustomerDTO> searchCustomerByName(HttpServletRequest request, HttpServletResponse response) throws Exception {
    String customerName = request.getParameter("customerName");
    AllListResult<CustomerDTO> result = new AllListResult<CustomerDTO>();
    result.setSuccess(false);
    if (customerName != null && !customerName.trim().equals("")) {
      IUserService userService = ServiceManager.getService(IUserService.class);
      Long shopId = (Long) request.getSession().getAttribute("shopId");
      List<CustomerDTO> customerDTOList = userService.getCustomerByName(shopId, customerName);
      if (customerDTOList != null && !customerDTOList.isEmpty()) {
        result.setSuccess(true);
        List<CustomerDTO> list = new ArrayList<CustomerDTO>();
        for (int m = 0; m < customerDTOList.size(); m++) {
          CustomerDTO customerDTO = customerDTOList.get(m);
          if (customerDTO != null) {
            list.add(customerDTO);
          }
        }
        result.setResults(list);
      }
    }
    return result;
  }

  @RequestMapping(params = "method=getVehicleInfoByVehicleId")
  @ResponseBody
  public VehicleDTO getVehicleInfoByVehicleId(HttpServletRequest request) throws Exception {
    VehicleDTO result = null;
    try {
      String vehicleIdStr = request.getParameter("vehicleId");
      if (StringUtils.isNotEmpty(vehicleIdStr)) {
        Long vehicleId = Long.parseLong(vehicleIdStr);
        result = userService.getVehicleById(vehicleId);
      }
    } catch (Exception e) {
      LOG.debug("/customer.do");
      LOG.debug("method=getVehicleInfoByVehicleId");
      LOG.debug("shopId:" + WebUtil.getShopId(request) + ",userId:" + WebUtil.getUserId(request));
      LOG.error(e.getMessage(), e);
    }
    return result;
  }

  @RequestMapping(params = "method=searchCustomerByVehicleId")
  @ResponseBody
  public CustomerDTO searchCustomerByVehicleId(HttpServletRequest request) throws Exception {
    Long shopId = WebUtil.getShopId(request);
    CustomerDTO result = null;
    try {
      String vehicleIdStr = request.getParameter("vehicleId");
      if (StringUtils.isNotEmpty(vehicleIdStr)) {
        Long vehicleId = Long.parseLong(vehicleIdStr);
        result = userService.getCustomerInfoByVehicleId(shopId, vehicleId);
      }
    } catch (Exception e) {
      LOG.debug("/customer.do");
      LOG.debug("method=searchCustomerByVehicleId");
      LOG.debug("shopId:" + WebUtil.getShopId(request) + ",userId:" + WebUtil.getUserId(request));
      LOG.error(e.getMessage(), e);
    }
    return result;
  }

  @RequestMapping(params = "method=searchCustomerByLicenceNo")
  @ResponseBody
  public CustomerDTO searchCustomerByLicenceNo(HttpServletRequest request, String licenceNo) throws Exception {
    Long shopId = WebUtil.getShopId(request);
    CustomerDTO result = null;
    try {
      if (StringUtils.isNotEmpty(licenceNo)) {
        IVehicleService vehicleService = ServiceManager.getService(IVehicleService.class);
        VehicleDTO vehicleDTO = vehicleService.getVehicleDTOByLicenceNo(shopId, licenceNo);
        if (vehicleDTO != null) {
          result = userService.getCustomerInfoByVehicleId(shopId, vehicleDTO.getId());
        }
      }
    } catch (Exception e) {
      LOG.debug("/customer.do");
      LOG.debug("method=searchCustomerByLicenceNo");
      LOG.debug("shopId:" + WebUtil.getShopId(request) + ",userId:" + WebUtil.getUserId(request));
      LOG.error(e.getMessage(), e);
    }
    return result;
  }

  @RequestMapping(params = "method=getDebtByCustomerId")
  public void getDebtByCustomerId(HttpServletRequest request, HttpServletResponse response) throws Exception {
    String id = request.getParameter("customerId");
    Long customerId = null;
    try {
      customerId = Long.parseLong(id);
    } catch (Exception e) {
      LOG.debug("/customer.do");
      LOG.debug("method=getDebtByCustomerId");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
    }

    String jsonStr = "";

    if (customerId != null) {
      IUserService userService = ServiceManager.getService(IUserService.class);
      List<CustomerRecordDTO> customerRecordDTOList = userService.getCustomerRecordByCustomerId(customerId);
      if (customerRecordDTOList != null && !customerRecordDTOList.isEmpty()) {
        CustomerRecordDTO customerRecordDTO = customerRecordDTOList.get(0);
        if (customerRecordDTO != null) {
          jsonStr = JsonUtil.objectToJson(customerRecordDTO);
        }
      }
    }
    if (StringUtils.isBlank(jsonStr)) {
      jsonStr = "[]";
    }
    PrintWriter out = response.getWriter();
    out.write(jsonStr);
    out.close();
  }

  //获取空白单据代金券列表
  @ResponseBody
  @RequestMapping(params = "method=getCouponList")
  public Object getCouponList(HttpServletRequest request , CarConstructionInvoiceSearchConditionDTO searchConditionDTO) throws PageException {
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    IConsumingService consumingService = ServiceManager.getService(IConsumingService.class);
    LOG.info("----------------"+shopId+"--------------");

    CouponListDTO couponListDTO = new CouponListDTO();
    try {
      List<ConsumingRecordDTO> consumingRecords = consumingService.findConsumingRecordByShopId(shopId, searchConditionDTO.getStartPageNo(), searchConditionDTO.getMaxRows());

      couponListDTO.setConsumingRecordDTOs(consumingRecords);
      int count = consumingService.findConsumingRecordCountByShopId(shopId).intValue();
      Pager pager = new Pager(count, searchConditionDTO.getStartPageNo(), searchConditionDTO.getMaxRows());
      couponListDTO.setPager(pager);
    } catch (Exception e){
      LOG.error(e.getMessage());
    }
    return couponListDTO;
  }

  //获取车辆施工单据列表
  @ResponseBody
  @RequestMapping(params = "method=getCarConstructionInvoiceList")
  public Object getCarConstructionInvoiceList(HttpServletRequest request, CarConstructionInvoiceSearchConditionDTO searchConditionDTO) throws Exception {
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    IRepairService repairService = ServiceManager.getService(IRepairService.class);
    CarConstructionInvoiceSearchResultListDTO searchResultListDTO = null;
    RepairRemindEventTypes repairRemindEventTypes = null;
    OrderStatus[] orderStatus = null;
    if (searchConditionDTO.getOrderStatus() == null || searchConditionDTO.getOrderStatus().equalsIgnoreCase("ALL")) {
      orderStatus = new OrderStatus[]{OrderStatus.REPAIR_DISPATCH, OrderStatus.REPAIR_DONE};
    } else if (searchConditionDTO.getOrderStatus().equalsIgnoreCase("REPAIR_DISPATCH")) {
      orderStatus = new OrderStatus[]{OrderStatus.REPAIR_DISPATCH};
    } else if (searchConditionDTO.getOrderStatus().equalsIgnoreCase("REPAIR_DONE")) {
      orderStatus = new OrderStatus[]{OrderStatus.REPAIR_DONE};
    }
    if (StringUtils.isNotEmpty(searchConditionDTO.getRepairRemindEventTypes())) {
      if (searchConditionDTO.getRepairRemindEventTypes().equalsIgnoreCase("LACK")) {
        repairRemindEventTypes = RepairRemindEventTypes.LACK;
      } else if (searchConditionDTO.getRepairRemindEventTypes().equalsIgnoreCase("INCOMING")) {
        repairRemindEventTypes = RepairRemindEventTypes.INCOMING;
      } else if (searchConditionDTO.getRepairRemindEventTypes().equalsIgnoreCase("WAIT_OUT_STORAGE")) {
        repairRemindEventTypes = RepairRemindEventTypes.WAIT_OUT_STORAGE;
      } else if (searchConditionDTO.getRepairRemindEventTypes().equalsIgnoreCase("OUT_STORAGE")) {
        repairRemindEventTypes = RepairRemindEventTypes.OUT_STORAGE;
      } else if (searchConditionDTO.getRepairRemindEventTypes().equalsIgnoreCase("NORMAL")) {
        repairRemindEventTypes = RepairRemindEventTypes.PENDING;
      }
    }
    try {
      searchResultListDTO = repairService.getRepairOrderByShopId(shopId, orderStatus, repairRemindEventTypes, searchConditionDTO.getStartPageNo(), searchConditionDTO.getMaxRows());
      Pager pager = new Pager(searchResultListDTO.getNumFound(), searchConditionDTO.getStartPageNo(), searchConditionDTO.getMaxRows());
      searchResultListDTO.setPager(pager);
    } catch (Exception e) {
      LOG.debug("/customer.do");
      LOG.debug("method=getCarConstructionInvoiceList");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
    }
    return searchResultListDTO;
  }

  //车辆引导页
  @RequestMapping(params = "method=carindex")
  public String carIndex(HttpServletRequest request) throws Exception {
    IUserService userService = ServiceManager.getService(IUserService.class);
    IRepairService repairService = ServiceManager.getService(IRepairService.class);
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    int serviceTodayTimes = repairService.countRepairOrderByDate(shopId, DateUtil.getStartTimeOfToday(), DateUtil.getEndTimeOfToday());
    int serviceYsterdayTimes = repairService.countRepairOrderByDate(shopId, DateUtil.getStartTimeOfYesterday(), DateUtil.getEndTimeOfYesterday());
    int todayNewUserNumber = userService.countRepairOrderHistoryByNewVehicle(shopId, null, null, null, DateUtil.getStartTimeOfToday(), DateUtil.getEndTimeOfToday());
    request.setAttribute("serviceYsterdayTimes", serviceYsterdayTimes);  //昨天服务次数
    request.setAttribute("serviceTodayTimes", serviceTodayTimes);  //今天服务次数
    request.setAttribute("todayNewUserNumber", todayNewUserNumber);//其中新增
    request.setAttribute("isRepairPickingSwitchOn", userService.isRepairPickingSwitchOn(shopId));
    return "customer/carindex";
  }


  @RequestMapping(params = "method=getbykeyword")    //todo zhangjuntao permission
  public void getInfoByKeyWord(HttpServletRequest request, HttpServletResponse response) {
    if (request.getParameter("keyword") == null) return;
    try {
      IUserService userService = ServiceManager.getService(IUserService.class);

      StringBuffer sb = new StringBuffer();
      sb.append("{");

      String key = request.getParameter("keyword");
      try {
        key = java.net.URLDecoder.decode(key, "UTF-8");
      } catch (Exception e) {
        LOG.debug("/customer.do");
        LOG.debug("method=getbykeyword");
        LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
        LOG.error(e.getMessage(), e);
      }

      int customerSize = 0;
      int supplierSize = 0;
      List<Customer> customerList = null;
      List<Supplier> supplierList = null;
      Long shopId = (Long) request.getSession().getAttribute("shopId");
      //权限
      boolean customermodify = true; //RenderPrivilegeVerifier.verifier(request, "/web/customer/modify");//客户更改
      boolean suppliermodify = true;//RenderPrivilegeVerifier.verifier(request, "/web/supplier/modify");//供应商更改
      if (customermodify) {
        try {
          customerList = userService.getShopCustomerByKey(shopId, key);
          customerSize = CollectionUtils.isEmpty(customerList) ? 0 : customerList.size();
        } catch (Exception e) {
          LOG.debug("/customer.do");
          LOG.debug("method=getbykeyword");
          LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
          LOG.error(e.getMessage(), e);
        }
      }
      if (suppliermodify) {
        try {
          supplierList = userService.getShopSupplierByKey(shopId, key);
          supplierSize = CollectionUtils.isEmpty(supplierList) ? 0 : supplierList.size();
        } catch (Exception e) {
          LOG.debug("/customer.do");
          LOG.debug("method=getbykeyword");
          LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
          LOG.error(e.getMessage(), e);
        }
      }
      Map resultMap = new HashMap();

      if (supplierSize > 0) {
        sb.append("\"supplier\":\"" + supplierSize + "\",");
      }
      if (customerSize > 0) {
        sb.append("\"customer\":\"" + customerSize + "\",");
      }

      if (customerSize == 1 && 1 > supplierSize) {
        sb.append("\"customerIdStr\":\"" + customerList.get(0).getId().toString() + "\",");
      }

      if (supplierSize == 1 && 1 > customerSize) {
        sb.append("\"supplierIdStr\":\"" + supplierList.get(0).getId().toString() + "\",");
      }
      sb.append("\"uvmValue\":\"" + key + "\"");

      sb.append("}");

      PrintWriter out = response.getWriter();
      out.write(sb.toString());
      out.flush();
      out.close();

    } catch (Exception e) {
      LOG.debug("/customer.do");
      LOG.debug("method=getbykeyword");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
    }
  }

  /**
   * 客户管理-供应商资料
   *
   * @param model
   * @param request
   * @return
   * @throws BcgogoException
   */
  @RequestMapping(params = "method=searchSuppiler")
  public String searchSuppiler(ModelMap model, HttpServletRequest request, HttpServletResponse response) throws BcgogoException {
    try {
//      LOG.info("跳转至供应商列表开始!");
//      long begin = System.currentTimeMillis();
//      long current = begin;
//      IUserService userService = ServiceManager.getService(IUserService.class);
//      ISupplierPayableService supplierPayableService = ServiceManager.getService(ISupplierPayableService.class);
//      Long shopId = (Long) request.getSession().getAttribute("shopId");
//      int totalRows = userService.countShopSupplier(shopId);
//      Pager pager = new Pager(totalRows, NumberUtil.intValue(request.getParameter("pageNo"), DEFAULT_PAGE_NO));
//      request.setAttribute("pager", pager);
//      LOG.info("跳转至供应商列表--阶段1。执行时间: {} ms", System.currentTimeMillis() - current);
//      current = System.currentTimeMillis();
//      //分页查询供应商列表
//      List<SupplierDTO> supplierDTOs = userService.getShopSupplier(shopId, pager.getCurrentPage(), pager.getPageSize());
//      List<SupplierDTO> formsupplierDTOs = supplierPayableService.formListSupplierDTOByPayableAndDeposit(supplierDTOs, shopId);
//      LOG.info("跳转至供应商列表--阶段2。执行时间: {} ms", System.currentTimeMillis() - current);
//      current = System.currentTimeMillis();
//      //共有总付款额
//      double totalPayable = supplierPayableService.getTotalPayableByShopId(shopId);
//      //应付定金合计
//      double totalDposit = supplierPayableService.getTotaDepositByShopId(shopId);
//      model.addAttribute("totalPayable", totalPayable);
//      model.addAttribute("totalDposit", totalDposit);
//      model.addAttribute("supplierDTOs", formsupplierDTOs);
//      model.addAttribute("supplierIds", request.getParameter("supplierIds"));
//      CookieUtil.rebuildCookiesForUserGuide(request, response, true, new String[]{"PRODUCT_PRICE_GUIDE_BEGIN","CONTRACT_SUPPLIER_GUIDE_BEGIN", "CONTRACT_SUPPLIER_GUIDE_SUPPLIER_DATA","CONTRACT_MESSAGE_NOTICE_CHECK_MESSAGE"});
//      LOG.info("跳转至供应商列表--阶段3。执行时间: {} ms", System.currentTimeMillis() - current);
//      LOG.info("跳转至供应商列表。总执行时间: {} ms", System.currentTimeMillis() - begin);
      if ("true".equals(request.getParameter("resetSearchCondition"))) {
        model.addAttribute("resetSearchCondition", true);
      }
      request.setAttribute("wholesalerVersion", ConfigUtils.isWholesalerVersion(WebUtil.getShopVersionId(request)));
      return "/customer/mySupplier";
    } catch (Exception e) {
      LOG.debug("/customer.do");
      LOG.debug("method=searchSuppiler");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
    }
    return "/";
  }

  @RequestMapping(params = "method=tippage")
  public String tipPage(HttpServletRequest request) {
    String ucmValue = request.getParameter("uvmValue");
    String newTab = request.getParameter("newTab");
    request.setAttribute("ucmValue", ucmValue);
    request.setAttribute("newTab", newTab);
    //中文
    if (ucmValue != null) {
      try {
        ucmValue = URLDecoder.decode(ucmValue, "UTF-8");
      } catch (Exception e) {
        LOG.debug("/customer.do");
        LOG.debug("method=tippage");
        LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
        LOG.error(e.getMessage(), e);
      }
      request.setAttribute("uvmValue", ucmValue);
    }
    return "/customer/tippage";
  }

  //根据供应商的name找到手机号和做记号
  @RequestMapping(params = "method=getCustomerByName")
  public void getCustomerByName(ModelMap model, HttpServletRequest request, HttpServletResponse response, String CustomerName) {
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    String jsonStr = "[]";
    StringBuffer sb = new StringBuffer("[");
    IUserService userService = ServiceManager.getService(IUserService.class);
    List<CustomerDTO> customerDTOList = userService.getCustomerByName(shopId, CustomerName);
    try {
      if (customerDTOList != null && customerDTOList.size() > 0) {
        sb.append("{\"mobile\":\"" + customerDTOList.get(0).getMobile() + "\",");
        sb.append("\"telephone\":\"" + customerDTOList.get(0).getLandLine() + "\"},");
      }
      sb.replace(sb.length() - 1, sb.length(), "]");
      jsonStr = sb.toString();
      PrintWriter writer = response.getWriter();
      writer.write(jsonStr);
      writer.close();
    } catch (Exception e) {
      LOG.debug("/customer.do");
      LOG.debug("method=getCustomerByName");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.debug("customerName:" + CustomerName);
      LOG.error(e.getMessage(), e);
    }
  }

  @RequestMapping(params = "method=getCustomerByMobile")
  public void getCustomerByMobile(ModelMap model, HttpServletRequest request, HttpServletResponse response, String mobile) {
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    IUserService userService = ServiceManager.getService(IUserService.class);
    List<CustomerDTO> customerDTOs = userService.getCustomerByMobile(shopId, mobile);
    Map jsonMap = null;
    try {
      if (customerDTOs != null && customerDTOs.size() > 0) {
        jsonMap = new HashMap();
        jsonMap.put("mobile", customerDTOs.get(0).getMobile());
        jsonMap.put("customerIdStr", customerDTOs.get(0).getIdStr());
        jsonMap.put("customer", customerDTOs.get(0).getName());
      }
      PrintWriter writer = response.getWriter();
      writer.write(JsonUtil.mapToJson(jsonMap));
      writer.close();
    } catch (Exception e) {
      LOG.debug("/customer.do");
      LOG.debug("method=getCustomerByMobile");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.debug("mobile:" + mobile);
      LOG.error(e.getMessage(), e);
    }
  }

  @RequestMapping(params = "method=getSupplierByMobile")
  public void getSupplierByMobile(ModelMap model, HttpServletRequest request, HttpServletResponse response, String mobile) {
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    IUserService userService = ServiceManager.getService(IUserService.class);
    List<SupplierDTO> supplierDTOs = userService.getSupplierByMobile(shopId, mobile);
    Map jsonMap = null;
    try {
      if (supplierDTOs != null && supplierDTOs.size() > 0) {
        jsonMap = new HashMap();
        jsonMap.put("mobile", supplierDTOs.get(0).getMobile());
        jsonMap.put("supplier", supplierDTOs.get(0).getName());
        jsonMap.put("supplierIdStr", supplierDTOs.get(0).getId().toString());
      }
      PrintWriter writer = response.getWriter();
      writer.write(JsonUtil.mapToJson(jsonMap));
      writer.close();
    } catch (Exception e) {
      LOG.debug("/customer.do");
      LOG.debug("method=getSupplierByMobile");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.debug("mobile:" + mobile);
      LOG.error(e.getMessage(), e);
    }
  }

  @RequestMapping(params = "method=getCustomerByTelephone")
  public void getCustomerByTelephone(ModelMap model, HttpServletRequest request, HttpServletResponse response, String telephone) {
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    IUserService userService = ServiceManager.getService(IUserService.class);
    List<CustomerDTO> customerDTOs = userService.getCustomerByTelephone(shopId, telephone);
    Map jsonMap = null;
    try {
      if (customerDTOs != null && customerDTOs.size() > 0) {
        jsonMap = new HashMap();
        jsonMap.put("telephone", customerDTOs.get(0).getLandLine());
        jsonMap.put("customer", customerDTOs.get(0).getName());
        jsonMap.put("customerIdStr", customerDTOs.get(0).getIdStr());
      }
      PrintWriter writer = response.getWriter();
      writer.write(JsonUtil.mapToJson(jsonMap));
      writer.close();
    } catch (Exception e) {
      LOG.debug("/customer.do");
      LOG.debug("method=getCustomerByTelephone");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.debug("telephone:" + telephone);
      LOG.error(e.getMessage(), e);
    }
  }

  @RequestMapping(params = "method=getSupplierByTelephone")
  public void getSupplierByTelephone(ModelMap model, HttpServletRequest request, HttpServletResponse response, String telephone) {
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    IUserService userService = ServiceManager.getService(IUserService.class);
    List<SupplierDTO> supplierDTOs = userService.getSupplierByTelephone(shopId, telephone);
    Map jsonMap = null;
    try {
      if (supplierDTOs != null && supplierDTOs.size() > 0) {
        jsonMap = new HashMap();
        jsonMap.put("telephone", supplierDTOs.get(0).getLandLine());
        jsonMap.put("supplier", supplierDTOs.get(0).getName());
        jsonMap.put("supplierIdStr", supplierDTOs.get(0).getIdString());
      }
      PrintWriter writer = response.getWriter();
      writer.write(JsonUtil.mapToJson(jsonMap));
      writer.close();
    } catch (Exception e) {
      LOG.debug("/customer.do");
      LOG.debug("method=getSupplierByTelephone");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.debug("telephone:" + telephone);
      LOG.error(e.getMessage(), e);
    }
  }

  @RequestMapping(params = "method=updateMobile")
  public void updateMobile(HttpServletRequest request, HttpServletResponse response) {
    IUserService userService = ServiceManager.getService(IUserService.class);
    ICustomerService customerService = ServiceManager.getService(ICustomerService.class);
    ICustomerOrSupplierSolrWriteService supplierSolrWriteService = ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class);
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    String customerId = request.getParameter("customerId");
    String supplierId = request.getParameter("supplierId");
    String mobile = request.getParameter("mobile");
    String licenceNo = request.getParameter("licenceNo");
    try {
      if (!StringUtils.isBlank(supplierId)) {
        SupplierDTO supplierDTO = userService.getSupplierById(NumberUtil.longValue(supplierId));
        supplierDTO.setMobile(mobile);
        if (!ArrayUtils.isEmpty(supplierDTO.getContacts()) && supplierDTO.hasValidContact()) { // add by zhuj　修改供应商手机号码
          for (ContactDTO contactDTO : supplierDTO.getContacts()) {
            if (contactDTO != null && contactDTO.getIsMainContact() != null && contactDTO.getIsMainContact() == 1) {
              contactDTO.setMobile(mobile);
              break;
            }
          }
        } else {
          ContactDTO contactDTO = new ContactDTO();
          contactDTO.setSupplierId(supplierDTO.getId());
          contactDTO.setShopId(shopId);
          contactDTO.setMobile(mobile);
          contactDTO.setName(supplierDTO.getName());
          contactDTO.setIsMainContact(1);
          contactDTO.setDisabled(1);
          ContactDTO[] contactDTOs = new ContactDTO[]{contactDTO};
          supplierDTO.setContacts(contactDTOs);
        }
        userService.updateSupplier(supplierDTO);
        supplierSolrWriteService.reindexSupplierBySupplierId(supplierDTO.getId());
        return;
      }
      if (StringUtils.isNotBlank(customerId)) {
        if (StringUtils.isNotBlank(licenceNo)) {
          IVehicleService vehicleService = ServiceManager.getService(IVehicleService.class);
          VehicleDTO vehicleDTO = vehicleService.updateVehicleMobile(shopId, licenceNo, mobile);
          if (vehicleDTO != null) {
            ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexCustomerByCustomerId(NumberUtil.longValue(customerId));
            ServiceManager.getService(IVehicleSolrWriterService.class).createVehicleSolrIndex(shopId, vehicleDTO.getId());
          }
        } else {
          CustomerDTO customerDTO = userService.getCustomerDTOByCustomerId(NumberUtil.longValue(customerId), shopId);
          customerDTO.setMobile(mobile);
          if (!ArrayUtils.isEmpty(customerDTO.getContacts()) && customerDTO.hasValidContact()) {
            for (ContactDTO contactDTO : customerDTO.getContacts()) {
              if (contactDTO != null && contactDTO.getIsMainContact() != null && contactDTO.getIsMainContact() == 1) {
                contactDTO.setMobile(mobile);
                break;
              }
            }
          } else {
            ContactDTO contactDTO = new ContactDTO();
            contactDTO.setCustomerId(customerDTO.getId());
            contactDTO.setShopId(shopId);
            contactDTO.setMobile(mobile);
            contactDTO.setName(customerDTO.getName());
            contactDTO.setIsMainContact(1);
            contactDTO.setDisabled(1);
            ContactDTO[] contactDTOs = new ContactDTO[]{contactDTO};
            customerDTO.setContacts(contactDTOs);
          }
          userService.updateCustomer(customerDTO);
          if (customerDTO.getSupplierId() != null) {
            SupplierDTO supplierDTO = userService.getSupplierById(customerDTO.getSupplierId());
            supplierDTO.setMobile(mobile);
            userService.updateSupplier(supplierDTO);
          }

          ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexCustomerByCustomerId(NumberUtil.longValue(customerId));
        }
      }
    } catch (Exception e) {
      LOG.debug("/customer.do");
      LOG.debug("method=updateMobile");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.debug("mobile:" + mobile);
      LOG.error(e.getMessage(), e);
    }
  }

  /**
   * 施工单购卡的时候应为没有customerId，就需要到这来来验证新老客户并添加新客户返回id
   *
   * @param request
   * @param response
   * @param customerName
   * @param mobile
   * @throws Exception
   */
  @RequestMapping(params = "method=checkCustomerExistAndSave")
  public void checkCustomerExistAndSave(HttpServletRequest request, HttpServletResponse response, String customerName,
                                        String mobile, String landLine, String licenceNo, String brand, String model, Long customerId) throws Exception {
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    Long userId = WebUtil.getUserId(request);
    IUserService userService = ServiceManager.getService(IUserService.class);
    IBaseProductService baseProductService = ServiceManager.getService(IBaseProductService.class);
    PrintWriter out = response.getWriter();
    String jsonStr = "";
    List<VehicleDTO> vehicleDTOList = null;
    ICustomerService customerService = ServiceManager.getService(ICustomerService.class);
    Long vehicleId = null;
    try {

      if (StringUtils.isNotBlank(licenceNo)) {
        vehicleDTOList = userService.getVehicleByLicenceNo(shopId, licenceNo);
      }

      if (null != customerId) {

        CustomerDTO customerDTO = userService.getCustomerById(customerId);

        customerDTO.setMobile(mobile);
        customerDTO.setLandLine(landLine);

        userService.updateCustomer(customerDTO);

        List<CustomerRecordDTO> customerRecordDTOList = userService.getCustomerRecordByCustomerId(customerId);

        if (CollectionUtils.isNotEmpty(customerRecordDTOList)) {
          CustomerRecordDTO customerRecordDTO = customerRecordDTOList.get(0);

          customerRecordDTO.setMobile(mobile);

          userService.updateCustomerRecord(customerRecordDTO);
        } else {
          CustomerRecordDTO customerRecordDTO = new CustomerRecordDTO();
          customerRecordDTO.setShopId(shopId);
          customerRecordDTO.setName(customerName);
          customerRecordDTO.setMobile(mobile);
          customerRecordDTO.setCustomerId(customerDTO.getId());
          userService.createCustomerRecord(customerRecordDTO);
        }

        if (null != customerId && CollectionUtils.isEmpty(vehicleDTOList) && StringUtils.isNotBlank(licenceNo)) {
          vehicleDTOList = new ArrayList<VehicleDTO>();
          VehicleDTO vehicleDTO = new VehicleDTO();
          vehicleDTO.setBrand(brand);
          vehicleDTO.setModel(model);
          vehicleDTO.setLicenceNo(licenceNo);
          vehicleDTOList.add(vehicleDTO);
          vehicleDTOList = userService.saveOrUpdateCustomerVehicles(customerId, shopId, userId, vehicleDTOList);
          vehicleId = vehicleDTOList.get(0).getId();
          try {
            baseProductService.saveVehicle(null, null, null, null, brand, model, null, null);
          } catch (Exception e) {
            LOG.error("保存基本车型库信息出错，不影响正常业务处理" + e.getMessage(), e);
          }
        }

        ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexCustomerByCustomerId(customerId);

        if (null != customerId) {
          jsonStr = "{\"msg\":\"saveSuccess\",\"id\":\"" + customerId.toString() + "\"," +
            "\"vehicleId\":\"" + (vehicleId == null ? "" : vehicleId.toString()) + "\"}";
        }
      } else {
        List<CustomerDTO> customerDTOList = userService.getCustomerByMobile(shopId, mobile);
        if (CollectionUtils.isNotEmpty(customerDTOList) && customerDTOList.size() > 1) {
          customerId = customerDTOList.get(0).getId();
          //一个手机号对应多个人与业务不符，给用户提示并且联系客服；
          jsonStr = "{\"msg\":\"existGtOne\"}";
        } else if (CollectionUtils.isNotEmpty(customerDTOList) && customerDTOList.size() == 1) {
          customerId = customerDTOList.get(0).getId();
          if (CollectionUtils.isNotEmpty(vehicleDTOList)) {
            VehicleDTO vehicleDTO = vehicleDTOList.get(0);
            vehicleDTO.setBrand(brand);
            vehicleDTO.setModel(model);
            userService.updateVehicle(vehicleDTO);
            vehicleId = vehicleDTO.getId();
            try {
              baseProductService.saveVehicle(null, null, null, null, brand, model, null, null);
            } catch (Exception e) {
              LOG.error("保存基本车型库信息出错，不影响正常业务处理" + e.getMessage(), e);
            }
          } else if (CollectionUtils.isEmpty(vehicleDTOList) && StringUtils.isNotBlank(licenceNo)) {
            vehicleDTOList = new ArrayList<VehicleDTO>();
            VehicleDTO vehicleDTO = new VehicleDTO();
            vehicleDTO.setBrand(brand);
            vehicleDTO.setModel(model);
            vehicleDTO.setLicenceNo(licenceNo);
            vehicleDTOList.add(vehicleDTO);
            vehicleDTOList = userService.saveOrUpdateCustomerVehicles(customerDTOList.get(0).getId(), shopId, userId, vehicleDTOList);
            vehicleId = vehicleDTOList.get(0).getId();
            try {
              baseProductService.saveVehicle(null, null, null, null, brand, model, null, null);
            } catch (Exception e) {
              LOG.error("保存基本车型库信息出错，不影响正常业务处理" + e.getMessage(), e);
            }
          }
          String vehicleIdStr = (null == vehicleId) ? "" : vehicleId.toString();
          //一个手机好对应一个人，返回customerId;
          jsonStr = "{\"msg\":\"existOne\",\"id\":\"" + customerDTOList.get(0).getId().toString() + "\"," +
            "\"vehicleId\":\"" + vehicleIdStr + "\"}";
        } else {
          //说明是新客户，新增并且返回id
          CustomerDTO customerDTO = new CustomerDTO();
          customerDTO.setName(customerName);
          customerDTO.setMobile(mobile);
          customerDTO.setLandLine(landLine);
          customerDTO.setFirstLetters(PinyinUtil.converterToFirstSpell(customerName));
          customerDTO.setShopId(shopId);
          if (StringUtil.isNotEmpty(mobile)) {
            ContactDTO contactDTO = new ContactDTO();
            contactDTO.setMobile(mobile);
            contactDTO.setShopId(shopId);
            contactDTO.setLevel(ContactConstant.LEVEL_0);
            contactDTO.setDisabled(ContactConstant.ENABLED);
            contactDTO.setIsMainContact(ContactConstant.IS_MAIN_CONTACT);
            contactDTO.setIsShopOwner(ContactConstant.NOT_SHOP_OWNER);
            ContactDTO[] contactDTOs = new ContactDTO[1];
            contactDTOs[0] = contactDTO;
            customerDTO.setContacts(contactDTOs);
          }
          customerDTO = userService.createCustomer(customerDTO);
          CustomerRecordDTO customerRecordDTO = new CustomerRecordDTO();
          customerRecordDTO.setShopId(shopId);
          customerRecordDTO.setName(customerName);
          customerRecordDTO.setMobile(mobile);
          customerRecordDTO.setCustomerId(customerDTO.getId());
          userService.createCustomerRecord(customerRecordDTO);
          customerId = customerDTO.getId();

          ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexCustomerByCustomerId(customerDTO.getId());
          if (null != customerRecordDTO.getCustomerId() && CollectionUtils.isEmpty(vehicleDTOList) && StringUtils.isNotBlank(licenceNo)) {
            vehicleDTOList = new ArrayList<VehicleDTO>();
            VehicleDTO vehicleDTO = new VehicleDTO();
            vehicleDTO.setBrand(brand);
            vehicleDTO.setModel(model);
            vehicleDTO.setLicenceNo(licenceNo);
            vehicleDTOList.add(vehicleDTO);
            vehicleDTOList = userService.saveOrUpdateCustomerVehicles(customerRecordDTO.getCustomerId(), shopId, userId, vehicleDTOList);
            vehicleId = vehicleDTOList.get(0).getId();
            try {
              baseProductService.saveVehicle(null, null, null, null, brand, model, null, null);
            } catch (Exception e) {
              LOG.error("保存基本车型库信息出错，不影响正常业务处理" + e.getMessage(), e);
            }
          }

          if (null != customerDTO.getId()) {
            jsonStr = "{\"msg\":\"saveSuccess\",\"id\":\"" + customerDTO.getId().toString() + "\"," +
              "\"vehicleId\":\"" + (vehicleId == null ? "" : vehicleId.toString()) + "\"}";
          } else {
            jsonStr = "{\"msg\":\"saveError\"}";
          }
        }
      }

    } catch (Exception e) {
      LOG.debug("method=checkCustomerExistAndSave");
      LOG.debug("customerName", customerName);
      LOG.debug("mobile", mobile);
      LOG.error(e.getMessage(), e);
    } finally {
      out.write(jsonStr);
      out.close();
    }
  }

  @RequestMapping(params = "method=getVehicleDTOListByCustomerId")
  @ResponseBody
  public List<VehicleDTO> getVehicleDTOListByCustomerId(HttpServletRequest request, String customerIdStr) {
    List<VehicleDTO> vehicleDTOList = null;
    try {
      if (StringUtils.isNotEmpty(customerIdStr)) {
        Long customerId = Long.parseLong(customerIdStr);
        IVehicleService vehicleService = ServiceManager.getService(IVehicleService.class);
        vehicleDTOList = vehicleService.getVehicleListByCustomerId(customerId);
      }
    } catch (Exception e) {
      LOG.debug("/customer.do");
      LOG.debug("method=getVehicleDTOListByCustomerId");
      LOG.debug("shopId:" + WebUtil.getShopId(request) + ",userId:" + WebUtil.getUserId(request));
      LOG.error(e.getMessage(), e);
    }
    return vehicleDTOList;
  }

  @RequestMapping(params = "method=checkIsExistGsmObdImeiInVehicle")
  @ResponseBody
  public Object checkIsExistGsmObdImeiInVehicle(HttpServletRequest request, Long vehicleId, String gsmObdImei, String gsmObdImeiMoblie) {
    try {
      if (StringUtils.isBlank(gsmObdImei) || StringUtils.isBlank(gsmObdImeiMoblie)) {
        return new Result();
      }
      VehicleDTO vehicleDTO = ServiceManager.getService(IVehicleService.class).getVehicleByGsmObdImei(vehicleId, gsmObdImei);
      if (vehicleDTO != null) {
        return new Result("此车辆的IMEI号重复，请修改！", false);
      }
      ObdSimBindDTO obdSimBindDTO = ServiceManager.getService(IObdManagerService.class).getObdSimBindByShopExact(gsmObdImei, gsmObdImeiMoblie);
      if (obdSimBindDTO == null) {
        return new Result("您输入的OBD不存在，请确认后重新输入!", false);
      }
      return new Result();
    } catch (BcgogoException e) {
      LOG.error(e.getMessage(), e);
      return new Result("您输入的OBD信息异常，请联系客服!", false);
    } catch (Exception e) {
      LOG.debug("/customer.do?method=checkIsExistGsmObdImeiInVehicle");
      LOG.debug("shopId:" + WebUtil.getShopId(request) + ",userId:" + WebUtil.getUserId(request));
      LOG.error(e.getMessage(), e);
    }
    return null;
  }

  //add by weilf 2012-08-15 ajaxLicenceNoIsExisted
  //Ajax 判断车牌是否已经存在，存在则返回客户姓名，不存在则返回空
  @RequestMapping(params = "method=licenceNoIsExisted")
  public void licenceNoIsExisted(HttpServletRequest request,
                                 HttpServletResponse response,
                                 @RequestParam("licenceVal") String licenceVal,
                                 @RequestParam("customerName") String customerName) {
    String jsonStr = "[]";
    StringBuffer sb = new StringBuffer("[");
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    List<CustomerDTO> customerDTOList = null;
    if (shopId == null) {
      return;
    }
    try {
      IUserService userService = ServiceManager.getService(IUserService.class);
      PrintWriter writer = response.getWriter();
      if (!com.bcgogo.common.StringUtil.isEmpty(licenceVal)) {
        customerDTOList = userService.getCustomerByLicenceNo(shopId, licenceVal);
        //无此车牌
        if (customerDTOList == null || customerDTOList.size() == 0) {
          sb.append("{\"customerName\":\"\",\"customerId\":\"\"}]");
          jsonStr = sb.toString();
        } else {
          //此车牌有归属人，则将归属人姓名和id返回给页面
          CustomerDTO customerDTO = customerDTOList.get(0);
          //判断是否为同名客户
          //如果当前页面没有填写客户姓名，或者当前客户姓名与车牌归属人不同命
          sb.append("{\"customerName\":\"" + customerDTO.getName() + "\",\"customerId\":\"" + customerDTO.getId() + "\",\"isObd\":\"" + customerDTO.getIsObd() + "\"}]");
          jsonStr = sb.toString();
        }
      }
      LOG.info(jsonStr);
      writer.write(jsonStr);
      writer.close();
    } catch (Exception e) {
      LOG.debug("/customer.do");
      LOG.debug("method=licenceNoIsExisted");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.debug("licenceVal:" + licenceVal);
      LOG.debug("customerName:" + customerName);
      LOG.error(e.getMessage(), e);
    }
  }

  //add by weilf 2012-08-16
  //Ajax 删除原有客户的车牌号（删除车辆信息及客户车辆的关联信息）
  @RequestMapping(params = "method=deleteCustomerLicenceNo")
  @ResponseBody
  public Object deleteCustomerLicenceNo(HttpServletRequest request, @RequestParam("licenceVal") String licenceVal) {
    //首先根据车牌号，找到车辆ID
    IUserService userService = ServiceManager.getService(IUserService.class);
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    if (shopId == null) {
      return false;
    }
    List<VehicleDTO> vehicleDTOList = userService.getVehicleByLicenceNo(shopId, licenceVal);
    if (vehicleDTOList == null || vehicleDTOList.size() == 0) {
      return true;
    } else {
      for (VehicleDTO vehicleDTO : vehicleDTOList) {
        //根据车辆ID，删除车辆信息
//        userService.deleteCustomerLicenceNo(shopId, vehicleDTO.getId());

        CustomerVehicleDTO customerVehicleDTO = CollectionUtil.getFirst(userService.getCustomerVehicleByVehicleId(vehicleDTO.getId()));
        if (customerVehicleDTO != null) {
          ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexCustomerByCustomerId(customerVehicleDTO.getCustomerId());
        }
      }
      return true;
    }
  }

  //add by weilf 2012-10-12
  //Ajax 根据车辆ID，删除车辆信息及客户车辆的关联信息
  @RequestMapping(params = "method=deleteCustomerVehicleById")
  @ResponseBody
  public Object deleteCustomerVehicleById(HttpServletRequest request, HttpServletResponse response) {
    //首先根据车牌号，找到车辆ID
    IUserService userService = ServiceManager.getService(IUserService.class);
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    String vehicleIdStr = request.getParameter("vehicleId");
    String customerId = request.getParameter("customerId");
    Result result = new Result();
    if (vehicleIdStr == null || customerId == null) {
      return result.LogErrorMsg("数据异常。");
    }
    try {
      Long vehicleId = Long.parseLong(vehicleIdStr);
      VehicleDTO vehicleDTO = ServiceManager.getService(IVehicleService.class).getVehicleDTOById(vehicleId);
      if (vehicleDTO == null) {
        return result.LogErrorMsg("数据异常。");
      }
      if (StringUtil.isNotEmpty(vehicleDTO.getGsmObdImei()) && StringUtil.isNotEmpty(vehicleDTO.getGsmObdImeiMoblie())) {
        return result.LogErrorMsg("车辆已经绑定OBD信息，无法删除。");
      }
      userService.deleteCustomerLicenceNo(shopId, vehicleId);
      ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexCustomerByCustomerId(Long.valueOf(customerId));
      //卸载OBD信息
//      if(StringUtil.isNotEmpty(vehicleDTO.getGsmObdImei())&&StringUtil.isNotEmpty(vehicleDTO.getGsmObdImeiMoblie())){
//        IObdManagerService obdManagerService= ServiceManager.getService(IObdManagerService.class);
//        ObdSimBindDTO obdSimBindDTO=obdManagerService.getObdSimBindByShopExact(vehicleDTO.getGsmObdImei(),vehicleDTO.getGsmObdImeiMoblie());
//        ObdDTO obdDTO=obdManagerService.getObdDTOById(obdSimBindDTO.getObdId());
//        if(obdDTO!=null){
//          obdDTO.setObdStatus(OBDStatus.ON_SELL);
//          obdDTO.setOwnerId(null);
//          obdDTO.setOwnerName(null);
//          obdDTO.setOwnerType(null);
//          obdManagerService.updateOBD(obdDTO);
//        }
//        vehicleDTO=userService.getVehicleById(vehicleId);
//        vehicleDTO.setGsmObdImei(null);
//        vehicleDTO.setGsmObdImeiMoblie(null);
//        userService.updateVehicle(vehicleDTO);
//      }
      return result;
    } catch (Exception e) {
      LOG.debug("/customer.do");
      LOG.debug("method=deleteCustomerVehicleById");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.debug("vehicleId:" + vehicleIdStr);
      LOG.error(e.getMessage(), e);
      return null;
    }
  }

  /**
   *客户资料---添加或更新车辆信息
   * @param request
   * @param response
   * @param customerVehicleResponse
   */
  @RequestMapping(params = "method=ajaxAddOrUpdateCustomerVehicle")
  public void ajaxAddOrUpdateCustomerVehicle(HttpServletRequest request, HttpServletResponse response, CustomerVehicleResponse customerVehicleResponse) {
    try {
      Long shopId = (Long) request.getSession().getAttribute("shopId");
      Long userId = WebUtil.getUserId(request);
      IProductService productService = ServiceManager.getService(IProductService.class);
      IUserService userService = ServiceManager.getService(IUserService.class);
      ICustomerService customerService = ServiceManager.getService(ICustomerService.class);
      IAppVehicleService appVehicleService = ServiceManager.getService(IAppVehicleService.class);
      Map jsonMap = new HashMap();
      if (shopId == null) {
        return;
      }
      String vehicleId = request.getParameter("vehicleId");
      Long customerId = Long.parseLong(request.getParameter("customerId"));
      String licenceNo = request.getParameter("licenceNo");
      String brand = request.getParameter("brand");
      String model = request.getParameter("model");
      String year = request.getParameter("year");
      String engine = request.getParameter("engine");
      String color = request.getParameter("color");
      String vin = request.getParameter("vin");
      String engineNo = request.getParameter("engineNo");
      String dateString = request.getParameter("dateString");
      String startMileage = request.getParameter("startMileage");
      String maintainMileage = request.getParameter("maintainMileage");
      String obdMileage = request.getParameter("obdMileage");
      String by = request.getParameter("by");
      String bx = request.getParameter("bx");
      String yc = request.getParameter("yc");
      String contact = request.getParameter("contact");
      String mobile = request.getParameter("mobile");
      String gsmObdImei = request.getParameter("gsmObdImei");
      String gsmObdImeiMoblie = request.getParameter("gsmObdImeiMoblie");
      String maintainMileagePeriod = request.getParameter("maintainMileagePeriod");

      //后续的方法中会判断是新增还是更新
      CarDTO carDTO = new CarDTO();
      carDTO.setId(vehicleId);
      carDTO.setLicenceNo(licenceNo);
      carDTO.setBrand(brand);
      carDTO.setModel(model);
      carDTO.setYear(year);
      carDTO.setEngine(engine);
      carDTO.setColor(color);
      carDTO.setChassisNumber(vin);
      carDTO.setEngineNo(engineNo);
      carDTO.setContact(contact);
      carDTO.setMobile(mobile);
      carDTO.setColor(color);
      carDTO.setGsmObdImei(gsmObdImei);
      carDTO.setGsmObdImeiMoblie(gsmObdImeiMoblie);
      if (dateString != null && !"".equals(dateString)) {
        carDTO.setCarDate(DateUtil.convertDateStringToDateLong("yyyy-MM-dd", dateString));
      }
      if (StringUtils.isNotBlank(startMileage)) {
        carDTO.setStartMileage(Double.parseDouble(startMileage));
      }
      if (NumberUtil.isNumber(maintainMileagePeriod)) {
        carDTO.setMaintainMileagePeriod(Double.parseDouble(maintainMileagePeriod));
      }
      if (StringUtil.isEmpty(obdMileage)) {
        carDTO.setObdMileage(null);
      } else {
        carDTO.setObdMileage(Double.parseDouble(obdMileage));
      }
      CarDTO[] carArray = new CarDTO[1];
      carArray[0] = carDTO;
      List<VehicleDTO> vehicleDTOList = productService.saveOrUpdateVehicleInfo(shopId, userId, customerId, carArray);
      if (ConfigUtils.isFourSShopVersion(WebUtil.getShopVersionId(request))) {
        appVehicleService.syncAppVehicle(vehicleDTOList);
        //绑定OBD
        ServiceManager.getService(IObdManagerService.class).gsmOBDBind(CollectionUtil.getFirst(vehicleDTOList), gsmObdImei,
          gsmObdImeiMoblie, WebUtil.getUserId(request), WebUtil.getShopName(request), WebUtil.getUserName(request));
        CollectionUtil.getFirst(vehicleDTOList).setGsmObdImei(gsmObdImei);
        CollectionUtil.getFirst(vehicleDTOList).setGsmObdImeiMoblie(gsmObdImeiMoblie);
      }
      vehicleId = String.valueOf(vehicleDTOList.get(0).getId());
      AppointServiceDTO appointServiceDTO = new AppointServiceDTO();
      appointServiceDTO.setMaintainMileagePeriod(customerVehicleResponse.getMaintainMileagePeriod());
      appointServiceDTO.setShopId(shopId);
      appointServiceDTO.setCustomerId(String.valueOf(customerId));
      appointServiceDTO.setVehicleId(String.valueOf(vehicleDTOList.get(0).getId()));
      appointServiceDTO.setMaintainTimeStr(by);
      appointServiceDTO.setInsureTimeStr(bx);
      appointServiceDTO.setExamineTimeStr(yc);
      if (StringUtils.isNotEmpty(maintainMileage) && StringUtils.isNumeric(maintainMileage)) {
        appointServiceDTO.setMaintainMileage(NumberUtil.longValue(maintainMileage));
      }
      if (StringUtils.isNotEmpty(obdMileage) && NumberUtil.isNumber(obdMileage)) {
        appointServiceDTO.setObdMileage(NumberUtil.doubleVal(obdMileage));
      }
      userService.addYuyueToCustomerVehicle(appointServiceDTO);

      //add by WLF 生成提醒
      CustomerDTO customerDTO = userService.getCustomerById(customerId);
      if (StringUtils.isBlank(vehicleId)) {
        vehicleId = appointServiceDTO.getVehicleId();
      }
      VehicleDTO vehicleDTO = userService.getVehicleById(new Long(vehicleId));
      List<AppointServiceDTO> customAppointServiceDTOList = customerVehicleResponse.getAppointServiceDTOs();
      if (CollectionUtils.isNotEmpty(customAppointServiceDTOList)) {
        customerService.saveCustomAppointServiceDTO(shopId, customerId, vehicleDTO.getId(), customAppointServiceDTOList.toArray(new AppointServiceDTO[customAppointServiceDTOList.size()]));
      }


      List<CustomerServiceJobDTO> customerServiceJobDTOList = userService.getCustomerServiceJobByCustomerIdAndVehicleId(shopId, customerId, new Long(vehicleId));
      if (CollectionUtils.isNotEmpty(customerServiceJobDTOList)) {
        for (CustomerServiceJobDTO customerServiceJobDTO : customerServiceJobDTOList) {
          ServiceManager.getService(ITxnService.class).saveRemindEvent(new CustomerServiceJob().fromDTO(customerServiceJobDTO), customerDTO.getName(), customerDTO.getMobile(), vehicleDTO.getLicenceNo());
        }
        //add by WLF 更新缓存
        ServiceManager.getService(ITxnService.class).updateRemindCountInMemcacheByTypeAndShopId(RemindEventType.CUSTOMER_SERVICE, shopId);
      }

      CustomerVehicleDTO customerVehicleDTO = userService.getCustomerVehicleDTOByVehicleIdAndCustomerId(Long.valueOf(vehicleId), customerId);
      List<AppointServiceDTO> appointServiceDTOs = userService.getAppointServiceByCustomerVehicle(shopId, Long.valueOf(vehicleId), customerId);
      ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexCustomerByCustomerId(customerId);
      ServiceManager.getService(IVehicleSolrWriterService.class).createVehicleSolrIndex(shopId, Long.valueOf(vehicleId));
      //获取客户拥有的车辆数
//      int vehicleNum = vechicleService.getVehicleListByCustomerId(customerId).size();
      jsonMap.put("vehicleId", vehicleDTOList.get(vehicleDTOList.size() - 1).getId().toString());
      jsonMap.put("customerVehicleResponse", new CustomerVehicleResponse(customerVehicleDTO, vehicleDTOList.get(0), appointServiceDTOs));
      PrintWriter writer = response.getWriter();
      writer.write(JsonUtil.mapToJson(jsonMap));
      writer.close();
    } catch (Exception e) {
      LOG.debug("/customer.do");
      LOG.debug("method=ajaxAddOrUpdateCustomerVehicle");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
    }
  }

  /**
   * customer Or Suppler 的下拉建议
   *
   * @param request
   * @throws Exception
   */
  @RequestMapping(params = "method=getCustomerOrSupplierSuggestion")
  @ResponseBody
  public Object getCustomerOrSupplierSuggestion(HttpServletRequest request, CustomerSupplierSearchConditionDTO searchConditionDTO) throws Exception {
    StopWatch sw = new StopWatch("getCustomerOrSupplierSuggestion");
    sw.start();
    List<SearchSuggestionDTO> searchSuggestionDTOList = null;
    ISearchCustomerSupplierService searchService = ServiceManager.getService(ISearchCustomerSupplierService.class);
    Long shopId = null;
    try {
      shopId = WebUtil.getShopId(request);
      if (shopId == null) throw new Exception("shop Id is null");
      if (StringUtils.isBlank(searchConditionDTO.getSort())) {
        if (CustomerSupplierSearchConditionDTO.SUPPLIER.equals(searchConditionDTO.getCustomerOrSupplier()) && searchConditionDTO.isEmptyOfSuggestionSupplierInfo())
          searchConditionDTO.setSort("last_inventory_time desc");
        else if (CustomerSupplierSearchConditionDTO.CUSTOMER.equals(searchConditionDTO.getCustomerOrSupplier()) && searchConditionDTO.isEmptyOfSuggestionCustomerInfo())
          searchConditionDTO.setSort("last_expense_time desc");
      }
      searchConditionDTO.setShopId(shopId);
      searchSuggestionDTOList = searchService.queryCustomerSupplierSuggestion(searchConditionDTO);
    } catch (Exception e) {
      LOG.debug(e.getMessage(), e);
    }
    sw.stop();
    LOG.debug(sw.toString());
    LOG.debug("controller total time: {} ms. ", sw.getTotalTimeMillis());
    return searchSuggestionDTOList;
  }

  @RequestMapping(params = "method=deleteCustomer")
  public String deleteCustomer(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) throws Exception {
    String customerIdStr = request.getParameter("customerId");
    String alsoDeleteSupplier = request.getParameter("alsoDeleteSupplier");
    IApplyService applyService = ServiceManager.getService(IApplyService.class);
    if (StringUtils.isBlank(customerIdStr)) {
      return customerData(modelMap, request, response);
    }
    Long shopId = WebUtil.getShopId(request);
    String lockKey = ConcurrentScene.WEB_DELETE_CUSTOMER.getName() + String.valueOf(shopId) + "_" + customerIdStr;
    try {
      if (!BcgogoConcurrentController.lock(ConcurrentScene.WEB_DELETE_CUSTOMER, lockKey)) {
        LOG.warn("deleteCustomer has been handling,customerId is {}", customerIdStr);
        modelMap.put("result", new PageErrorMsg("删除操作正在执行", "请稍后再试"));
        return Constant.PAGE_ERROR;
      }
      Long customerId = Long.valueOf(customerIdStr);
      Long userId = WebUtil.getUserId(request);
      ICustomerService customerService = ServiceManager.getService(ICustomerService.class);
      IUserService userService = ServiceManager.getService(IUserService.class);
      CustomerDTO customerDTO = customerService.getCustomerById(customerId);
      Long relateSupplierId = customerDTO.getSupplierId();
      customerService.deleteCustomer(shopId, customerId);

      if (relateSupplierId == null) {
        ServiceManager.getService(IContactService.class).disabledContactsByIdAndType(customerDTO.getId(), "customer", null); // add by zhuj 非关联客户、供应商联系人删除
      }
      //有店铺关联关系的删除关联关系
      applyService.wholesalerShopDeleteCustomerUpdateRelation(customerDTO, userId);

      if (relateSupplierId != null && StringUtils.isNotBlank(alsoDeleteSupplier) && alsoDeleteSupplier.equals("true") && customerDTO.getPermanentDualRole()) {
        Result result = new Result();
        SupplierDTO supplierDTO = new SupplierDTO();
        supplierDTO.setShopId(shopId);
        supplierDTO.setId(relateSupplierId);
        ServiceManager.getService(ISupplierService.class).deleteSupplier(result, supplierDTO);
        //有关联店铺关系的的删除关联关系
        supplierDTO = userService.getSupplierDTOBySupplierShopIdAndShopId(shopId, relateSupplierId);
        applyService.customerShopDeleteSupplierUpdateRelation(supplierDTO, userId);

        ServiceManager.getService(IContactService.class).deleteRelatedCusSupContacts(customerDTO.getId(), relateSupplierId, "customerAndSupplier", null); // add by zhuj 同时删除
        if (BcgogoShopLogicResourceUtils.isThroughSelectSupplier(WebUtil.getShopVersion(request).getId())) {
          ServiceManager.getService(IProductThroughService.class).updateSupplierInventoryStatusBySupplierId(WebUtil.getShopId(request), supplierDTO.getId(), YesNo.YES);
        }
        ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexSupplierBySupplierId(relateSupplierId);
      } else {
        // add by zhuj 非同时删除客户和供应商
        if (relateSupplierId != null) {
          ServiceManager.getService(IContactService.class).deleteRelatedCusSupContacts(customerDTO.getId(), relateSupplierId, "customer", null); // add by zhuj
          ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexSupplierBySupplierId(relateSupplierId);
        }
      }
      ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexCustomerByCustomerId(customerDTO.getId());
      return "redirect:customer.do?method=customerdata";
    } catch (Exception e) {
      LOG.error("method=deleteCustomer");
      LOG.error(e.getMessage(), e);
      modelMap.put("result", new PageErrorMsg(e.getMessage(), "请稍后再试"));
      return Constant.PAGE_ERROR;
    } finally {
      BcgogoConcurrentController.release(ConcurrentScene.WEB_DELETE_CUSTOMER, lockKey);
    }
  }

  /**
   * 删除客户校验，之前的校验逻辑在前台uncleUser.js 发送各种校验请求，这次增加删除在线店铺逻辑，在这个validateController
   * 中只添加之前未涉及到的校验
   *
   * @param modelMap
   * @param request
   * @param response
   * @param customerId
   * @return
   * @throws Exception
   */
  @RequestMapping(params = "method=validateDeleteCustomer")
  @ResponseBody
  public Object validateDeleteCustomer(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response, Long customerId) throws Exception {
    RFITxnService rfiTxnService = ServiceManager.getService(RFITxnService.class);
    ICustomerDepositService customerDepositService = ServiceManager.getService(ICustomerDepositService.class);
    Result result = new Result();
    CustomerDTO customerDTO = null;
    Long shopId = WebUtil.getShopId(request);
    try {
      customerDTO = userService.getCustomerDTOByCustomerId(customerId, shopId);
      if (customerDTO == null || CustomerStatus.DISABLED.equals(customerDTO.getStatus())) {
        result.setSuccess(false);
        result.setMsg("客户不存在或已被删除！");
        return result;
      }
      //验证删除车辆
      List<CustomerVehicleDTO> customerVehicleDTOs = userService.getVehicleByCustomerId(customerId);
      if (CollectionUtil.isNotEmpty(customerVehicleDTOs)) {
        for (CustomerVehicleDTO customerVehicleDTO : customerVehicleDTOs) {
          if (null == customerVehicleDTO.getVehicleId()) {
            continue;
          }
          VehicleDTO vehicleDTO = userService.getVehicleById(customerVehicleDTO.getVehicleId());
          if (StringUtil.isNotEmpty(vehicleDTO.getGsmObdImei()) && StringUtil.isNotEmpty(vehicleDTO.getGsmObdImeiMoblie())) {
            return result.LogErrorMsg("车辆已经绑定OBD信息，无法删除。");
          }
        }
      }
      //校验未结算的单据
      result = rfiTxnService.validateDeleteCustomerHasUnSettledOrder(shopId, customerDTO);
      if (result != null && result.isSuccess()) {
        return result;
      }
      //该客户有欠款未结算不能删除
      List<CustomerRecordDTO> customerRecordDTOList = userService.getCustomerRecordByCustomerId(customerDTO.getId());
      CustomerRecordDTO customerRecordDTO = CollectionUtil.getFirst(customerRecordDTOList);
      if ((null != customerRecordDTO && customerRecordDTO.getTotalReceivable() > 0)
        || (null != customerRecordDTO && NumberUtil.doubleVal(customerRecordDTO.getTotalPayable()) > 0)) {
        result.setSuccess(false);
        result.setMsg("该客户有欠款未结算不能删除！");
        return result;
      }
      //如果有预收款不能删除
      CustomerDepositDTO customerDepositDTO = customerDepositService.queryCustomerDepositByShopIdAndCustomerId(shopId, customerDTO.getId());
      if (customerDepositDTO != null && NumberUtil.doubleVal(customerDepositDTO.getActuallyPaid()) > 0.0001) {
        result.setSuccess(false);
        result.setMsg("客户存在预收款不能删除！");
        return result;
      }


      return result;
    } catch (Exception e) {
      LOG.error("method=validateDeleteCustomer" + e.getMessage(), e);
      result.setSuccess(false);
      result.setMsg("删除客户出现异常！");
      return result;
    }
  }

  @RequestMapping(params = "method=checkCustomerStatus")
  @ResponseBody
  public Object checkCustomerStatus(HttpServletRequest request, String customerId) throws Exception {
    IUserService userService = ServiceManager.getService(IUserService.class);
    try {
      Customer customer = userService.getCustomerByCustomerId(Long.valueOf(customerId), WebUtil.getShopId(request));
      if (customer == null || CustomerStatus.DISABLED.equals(customer.getStatus())) {
        return new Result(false);
      } else {
        return new Result();
      }
    } catch (Exception e) {
      LOG.error("method=checkCustomerStatus");
      LOG.error(e.getMessage(), e);
    }
    return null;
  }


  @RequestMapping(params = "method=checkArrears")
  public void checkArrears(HttpServletRequest request, HttpServletResponse response) throws Exception {
    PrintWriter out = response.getWriter();
    String jsonStr = "";
    String customerIdStr = request.getParameter("customerId");
    if (StringUtils.isBlank(customerIdStr)) {
      return;
    }

    IUserService userService = ServiceManager.getService(IUserService.class);
    try {

      List<CustomerRecordDTO> customerRecordDTOList = userService.getCustomerRecordByCustomerId(Long.valueOf(customerIdStr));

      CustomerRecordDTO customerRecordDTO = null;

      if (CollectionUtils.isNotEmpty(customerRecordDTOList)) {
        customerRecordDTO = customerRecordDTOList.get(0);
      }

      if ((null != customerRecordDTO && customerRecordDTO.getTotalReceivable() > 0) || (null != customerRecordDTO && NumberUtil.doubleVal(customerRecordDTO.getTotalPayable()) > 0)) {
        jsonStr = "error";
      }

    } catch (Exception e) {
      LOG.error("method=checkArrears");
      LOG.error(e.getMessage(), e);
    } finally {
      Map<String, String> map = new HashMap<String, String>();
      map.put("resu", jsonStr);

      out.write(JsonUtil.mapToJson(map));
      out.flush();
      out.close();
    }
  }

  @RequestMapping(params = "method=deleteCustomerAjax")
  public void deleteCustomerAjax(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) throws Exception {
    PrintWriter out = response.getWriter();
    String customerIdStr = request.getParameter("customerId");
    String jsonStr = "";
    boolean flag = false;
    if (StringUtils.isBlank(customerIdStr)) {
      return;
    }
    try {
      Long customerId = Long.valueOf(customerIdStr);
      Long shopId = WebUtil.getShopId(request);
      ICustomerService customerService = ServiceManager.getService(ICustomerService.class);
      IDraftOrderService draftOrderService = ServiceManager.getService(IDraftOrderService.class);
      IUserService userService = ServiceManager.getService(IUserService.class);
      customerService.deleteCustomer(shopId, customerId);

      ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexCustomerByCustomerId(customerId);

      flag = true;

//      List<OrderTypes> orderTypesList = new ArrayList<OrderTypes>();

//      orderTypesList.add(OrderTypes.SALE);
//      orderTypesList.add(OrderTypes.REPAIR);
//      orderTypesList.add(OrderTypes.WASH_BEAUTY);
//
//      List<DraftOrder> draftOrderList =  draftOrderService.getDraftOrder(shopId,orderTypesList,customerId);
//
//      if(CollectionUtils.isNotEmpty(draftOrderList))
//      {
//        draftOrderService.deleteDraftOrderList(draftOrderList);
//      }

    } catch (Exception e) {
      LOG.error("method=deleteCustomerAjax");
      LOG.error(e.getMessage(), e);
      e.printStackTrace();
    } finally {
      Map<String, String> map = new HashMap<String, String>();

      if (flag) {
        jsonStr = "success";
      } else {
        jsonStr = "error";
      }

      map.put("resu", jsonStr);

      out.write(JsonUtil.mapToJson(map));
      out.flush();
      out.close();
    }
  }

  @RequestMapping(params = "method=mergeCustomerHandler")
  @ResponseBody
  public Object mergeCustomerHandler(ModelMap modelMap, HttpServletRequest request, String parentIdStr, String[] chilIdStrs) {
    IMergeService mergeService = ServiceManager.getService(IMergeService.class);
    if (WebUtil.getShopId(request) == null) {
      return null;
    }
    String key = "mergeCustomerHandler_" + WebUtil.getShopId(request);
    try {
      MergeResult<CustomerDTO, MergeCustomerSnap> mergeResult = new MergeResult<CustomerDTO, MergeCustomerSnap>();
      if (!BcgogoConcurrentController.lock(ConcurrentScene.CUSTOMER, key)) {
        return null;
      }
      mergeResult.setShopId(WebUtil.getShopId(request));
      mergeResult.setUserId(WebUtil.getUserId(request));
      mergeResult.setLocale(request.getLocale());
      mergeResult.setMergeType(MergeType.MERGE_CUSTOMER);
      mergeService.validateMergeCustomer(mergeResult, parentIdStr, chilIdStrs);
      List<Long> childIds = mergeResult.getChildIds();
      if (!mergeResult.isSuccess()) {
        return mergeResult;
      }
      mergeService.mergeCustomerHandler(mergeResult, NumberUtil.longValue(parentIdStr), childIds.toArray(new Long[childIds.size()]));
      // add by WLF 更新缓存
      ServiceManager.getService(ITxnService.class).updateRemindCountInMemcacheByTypeAndShopId(RemindEventType.REPAIR, WebUtil.getShopId(request));
      ServiceManager.getService(ITxnService.class).updateRemindCountInMemcacheByTypeAndShopId(RemindEventType.DEBT, WebUtil.getShopId(request));
      ServiceManager.getService(ITxnService.class).updateRemindCountInMemcacheByTypeAndShopId(RemindEventType.TXN, WebUtil.getShopId(request));
      ServiceManager.getService(ITxnService.class).updateRemindCountInMemcacheByTypeAndShopId(RemindEventType.CUSTOMER_SERVICE, WebUtil.getShopId(request));
      return mergeResult;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return null;
    } finally {
      BcgogoConcurrentController.release(ConcurrentScene.CUSTOMER, key);
    }
  }

  @ResponseBody
  @RequestMapping(params = "method=getMergeCustomerRecords")
  public Object getMergeCustomerRecords(HttpServletRequest request, MergeRecordDTO mergeRecordIndex) {
    IMergeService mergeService = ServiceManager.getService(IMergeService.class);
    try {
      mergeRecordIndex.convertRequestParams();
      mergeRecordIndex.setShopId(WebUtil.getShopId(request));
      mergeRecordIndex.setMergeType(MergeType.MERGE_CUSTOMER);
      List<Object> results = mergeService.getMergeRecords(mergeRecordIndex);
      return results;
    } catch (Exception e) {
      LOG.error("查询客户合并记录异常！");
      LOG.error(e.getMessage(), e);
      return null;
    }
  }

  @RequestMapping(params = "method=toMergeRecord")
  public String toMergeRecord(ModelMap modelMap) {
    modelMap.addAttribute("endTime", DateUtil.convertDateLongToString(System.currentTimeMillis(), DateUtil.DATE_STRING_FORMAT_DEFAULT));
    return "customer/merge/mergeCustomerRecord";
  }

  @ResponseBody
  @RequestMapping(params = "method=getMergedCustomers")
  public Object getMergedCustomers(ModelMap modelMap, HttpServletRequest request, String[] customerIds) {
    ICustomerService customerService = ServiceManager.getService(ICustomerService.class);
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    SearchMergeResult<CustomerDTO> result = new SearchMergeResult<CustomerDTO>();

    try {
      List<Long> customerIdList = new ArrayList<Long>();
      if (ArrayUtil.isEmpty(customerIds)) {
        result.setSuccess(false);
        result.setMsg("客户id有误 ！！！");
        return result;
      }
      for (String customerIdStr : customerIds) {
        customerIdList.add(NumberUtil.longValue(customerIdStr));
      }
      result.setShopId(WebUtil.getShopId(request));
      customerService.getMergedCustomers(result, customerIdList);
      if (!result.isSuccess() || CollectionUtil.isEmpty(result.getResults())) {
        return result;
      }
      if (result.getResults().size() != 2) {
        result.setSuccess(false);
        result.setMsg("选择客户信息异常！");
        return result;
      }
      // add by zhuj 设置customer的预收款信息
      if (!CollectionUtils.isEmpty(result.getResults())) {
        for (CustomerDTO customerDTO : result.getResults()) {
          CustomerDepositDTO customerDepositDTO = ServiceManager.getService(ICustomerDepositService.class).queryCustomerDepositByShopIdAndCustomerId(WebUtil.getShopId(request), customerDTO.getId());
          if (customerDepositDTO != null) {
            customerDTO.setDeposit(customerDepositDTO.getActuallyPaid());
          } else {
            customerDTO.setDeposit(0.00d);
          }
        }
      }

      // add by zhuj 设置所在区域显示
      CustomerDTO parent = result.getResults().get(0);
      Set areaNos = parent.buildAreaNoSet();
      Map<Long, AreaDTO> areaMap = ServiceManager.getService(IConfigService.class).getAreaByAreaNo(areaNos);
      parent.setAreaByAreaNo(areaMap);
      CustomerDTO child = result.getResults().get(1);
      Set childAreaNos = child.buildAreaNoSet();
      Map<Long, AreaDTO> childAreaMap = ServiceManager.getService(IConfigService.class).getAreaByAreaNo(childAreaNos);
      child.setAreaByAreaNo(childAreaMap);

      //默认以关联客户为保留客户
      if (child.getCustomerShopId() != null) {
        CustomerDTO tempCustmer = parent;
        parent = child;
        child = tempCustmer;
      }

      result.setParent(parent);
      result.setChild(child);
      if (parent.getCustomerShopId() != null || child.getCustomerShopId() != null) {
        result.setMergeRelatedFlag(true);
      }
      List<MemberServiceDTO> memberServiceDTOs = null;
      MemberDTO parentMember = parent.getMemberDTO();
      if (parentMember != null) {
        parentMember.setMemberConsumeTotal(txnService.getMemberCardConsumeTotal(WebUtil.getShopId(request), parentMember.getId()));
        memberServiceDTOs = parent.getMemberDTO().getMemberServiceDTOs();
        if (CollectionUtils.isNotEmpty(memberServiceDTOs)) {
          for (MemberServiceDTO memberServiceDTO : memberServiceDTOs) {
            ServiceDTO serviceDTO = txnService.getServiceById(memberServiceDTO.getServiceId());
            if (serviceDTO != null) {
              memberServiceDTO.setServiceName(serviceDTO.getName());
            }
          }
        }
      }
      MemberDTO childMember = child.getMemberDTO();
      if (childMember != null) {
        childMember.setMemberConsumeTotal(txnService.getMemberCardConsumeTotal(WebUtil.getShopId(request), childMember.getId()));
        memberServiceDTOs = child.getMemberDTO().getMemberServiceDTOs();
        if (CollectionUtils.isNotEmpty(memberServiceDTOs)) {
          for (MemberServiceDTO memberServiceDTO : memberServiceDTOs) {
            ServiceDTO serviceDTO = txnService.getServiceById(memberServiceDTO.getServiceId());
            if (serviceDTO != null) {
              memberServiceDTO.setServiceName(serviceDTO.getName());
            }
          }
        }
      }
      result.setMergeRelatedFlag(ConfigUtils.isWholesalerVersion(WebUtil.getShopVersionId(request)));
      return result;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return null;
    }
  }

  @ResponseBody
  @RequestMapping(params = "method=getMergeCustomerSnap")
  public Object getMergeCustomerSnap(ModelMap modelMap, HttpServletRequest request, String parentIdStr, String childIdStr) {
    IMergeService mergeService = ServiceManager.getService(IMergeService.class);
    SearchMergeResult<MergeRecord> result = new SearchMergeResult<MergeRecord>();
    if (StringUtil.isEmpty(parentIdStr) || StringUtil.isEmpty(childIdStr)) {
      result.setSuccess(false);
      result.setMsg("客户信息异常！");
      return result;
    }
    try {
      result.setShopId(WebUtil.getShopId(request));
      mergeService.getMergeSnap(result, NumberUtil.longValue(parentIdStr), NumberUtil.longValue(childIdStr));
      result.setMergeRelatedFlag(ConfigUtils.isWholesalerVersion(WebUtil.getShopVersionId(request)));
      return result;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return null;
    }
  }

  @RequestMapping(params = "method=ajaxGetCustomerInfo")
  @ResponseBody
  public Object ajaxGetCustomerInfo(HttpServletRequest request) {
    Map<String, Object> returnMap = new HashMap<String, Object>();
    IMembersService membersService = ServiceManager.getService(IMembersService.class);
    try {
      Long shopId = WebUtil.getShopId(request);
      String licenceNo = request.getParameter("licenceNo");
      VehicleDTO vehicleDTO = null;
      CustomerDTO customerDTO = null;
      MemberDTO memberDTO = null;
      if (StringUtils.isNotBlank(licenceNo)) {
        vehicleDTO = CollectionUtil.getFirst(userService.getVehicleByLicenceNo(shopId, licenceNo));
        if (vehicleDTO != null) {
          returnMap.put("vehicleDTO", vehicleDTO);
          List<CustomerVehicleDTO> customerVehicleDTOs = userService.getCustomerVehicleByVehicleId(vehicleDTO.getId());
          if (CollectionUtils.isNotEmpty(customerVehicleDTOs)) {
            for (CustomerVehicleDTO customerVehicleDTO : customerVehicleDTOs) {
              if (customerVehicleDTO.getCustomerId() != null) {
                customerDTO = userService.getCustomerDTOByCustomerId(customerVehicleDTO.getCustomerId(), shopId);
                if (customerDTO != null) {
                  returnMap.put("customerDTO", customerDTO);
                  memberDTO = membersService.getMemberDTOByCustomerId(shopId, customerDTO.getId());
                  returnMap.put("memberDTO", memberDTO);
                  break;
                }
              }
            }
          }
        }
      }
      return returnMap;
    } catch (Exception e) {
      LOG.debug("/customer.do?method=ajaxGetCustomerInfo");
      LOG.error(e.getMessage(), e);
      return returnMap;
    }
  }


  /**
   * @param request （parameter：customerId）
   * @return {customerDTO:{},vehicleDTOs:[{},{}],memberDTO:{}}
   */
  @RequestMapping(params = "method=ajaxGetCustomerInfoById")
  @ResponseBody
  public Object ajaxGetCustomerInfoById(HttpServletRequest request) {
    Map<String, Object> returnMap = new HashMap<String, Object>();
    try {
      Long shopId = WebUtil.getShopId(request);
      String customerIdStr = request.getParameter("customerId");
      List<VehicleDTO> vehicleDTOs = null;
      CustomerDTO customerDTO = null;
      MemberDTO memberDTO = null;
      if (StringUtils.isNotBlank(customerIdStr) && StringUtils.isNumeric(customerIdStr)) {
        Long customerId = Long.parseLong(customerIdStr);
        customerDTO = userService.getCustomerDTOByCustomerId(customerId, shopId);
        if (customerDTO != null && customerDTO.getId() != null) {
          vehicleDTOs = ServiceManager.getService(IVehicleService.class).getVehicleListByCustomerId(customerDTO.getId());
          memberDTO = ServiceManager.getService(IMembersService.class).getMemberDTOByCustomerId(shopId, customerDTO.getId());
          if (memberDTO != null && MemberStatus.DISABLED == memberDTO.getStatus()) {
            memberDTO = null;
          }
        }
      }
      returnMap.put("customerDTO", customerDTO);
      returnMap.put("vehicleDTOs", vehicleDTOs);
      returnMap.put("memberDTO", memberDTO);
      return returnMap;
    } catch (Exception e) {
      LOG.debug("/customer.do?method=ajaxGetCustomerInfoById");
      LOG.error(e.getMessage(), e);
      return returnMap;
    }
  }

  /**
   * @param request （parameter：memberNo）
   * @return {customerDTO:{},vehicleDTOs:[{},{}],memberDTO:{}}
   */
  @RequestMapping(params = "method=ajaxGetCustomerInfoByMemberNo")
  @ResponseBody
  public Object ajaxGetCustomerInfoByMemberNo(HttpServletRequest request) {
    Map<String, Object> returnMap = new HashMap<String, Object>();
    try {
      Long shopId = WebUtil.getShopId(request);
      String memberNo = request.getParameter("memberNo");
      List<VehicleDTO> vehicleDTOs = null;
      CustomerDTO customerDTO = null;
      MemberDTO memberDTO = null;
      if (StringUtils.isNotBlank(memberNo)) {
        memberDTO = ServiceManager.getService(IMembersService.class).getEnabledMemberDTOByMemberNo(memberNo, shopId);
        if (memberDTO != null && memberDTO.getCustomerId() != null) {
          customerDTO = userService.getCustomerDTOByCustomerId(memberDTO.getCustomerId(), shopId);
        }
        if (customerDTO != null && customerDTO.getId() != null) {
          vehicleDTOs = ServiceManager.getService(IVehicleService.class).getVehicleListByCustomerId(customerDTO.getId());
        }
      }
      returnMap.put("customerDTO", customerDTO);
      returnMap.put("vehicleDTOs", vehicleDTOs);
      returnMap.put("memberDTO", memberDTO);
      return returnMap;
    } catch (Exception e) {
      LOG.debug("/customer.do?method=ajaxGetCustomerInfoByMemberNo");
      LOG.error(e.getMessage(), e);
      return returnMap;
    }
  }

  /**
   * 通过客户电话带出一个客户信息  （汽修版使用，汽配版存在同手机号多个客户的情况）
   *
   * @param request （parameter：customerMobile）
   * @return {customerDTO:{},vehicleDTOs:[{},{}],memberDTO:{}}
   */
  @RequestMapping(params = "method=ajaxGetCustomerInfoByCustomerMobile")
  @ResponseBody
  public Object ajaxGetCustomerInfoByCustomerMobile(HttpServletRequest request) {
    Map<String, Object> returnMap = new HashMap<String, Object>();
    try {
      Long shopId = WebUtil.getShopId(request);
      String customerMobile = request.getParameter("customerMobile");
      List<VehicleDTO> vehicleDTOs = null;
      CustomerDTO customerDTO = null;
      MemberDTO memberDTO = null;
      if (StringUtils.isNotBlank(customerMobile)) {
        customerDTO = CollectionUtil.getFirst(userService.getCustomerByMobile(shopId, customerMobile));
        if (customerDTO != null && customerDTO.getId() != null) {
          vehicleDTOs = ServiceManager.getService(IVehicleService.class).getVehicleListByCustomerId(customerDTO.getId());
          memberDTO = ServiceManager.getService(IMembersService.class).getMemberDTOByCustomerId(shopId, customerDTO.getId());
          if (memberDTO != null && MemberStatus.DISABLED == memberDTO.getStatus()) {
            memberDTO = null;
          }
        }
      }
      returnMap.put("customerDTO", customerDTO);
      returnMap.put("vehicleDTOs", vehicleDTOs);
      returnMap.put("memberDTO", memberDTO);
      return returnMap;
    } catch (Exception e) {
      LOG.debug("/customer.do?method=ajaxGetCustomerInfoByCustomerMobile");
      LOG.error(e.getMessage(), e);
      return returnMap;
    }
  }

  /**
   * 通过客户座机带出一个客户信息  （汽修版使用，汽配版存在同手机号多个客户的情况）
   *
   * @param request （parameter：customerLandLine）
   * @return {customerDTO:{},vehicleDTOs:[{},{}],memberDTO:{}}
   */
  @RequestMapping(params = "method=ajaxGetCustomerInfoByCustomerLandLine")
  @ResponseBody
  public Object ajaxGetCustomerInfoByCustomerLandLine(HttpServletRequest request) {
    Map<String, Object> returnMap = new HashMap<String, Object>();
    try {
      Long shopId = WebUtil.getShopId(request);
      String customerLandLine = request.getParameter("customerLandLine");
      List<VehicleDTO> vehicleDTOs = null;
      CustomerDTO customerDTO = null;
      MemberDTO memberDTO = null;
      if (StringUtils.isNotBlank(customerLandLine)) {
        customerDTO = CollectionUtil.getFirst(userService.getCustomerByTelephone(shopId, customerLandLine));
        if (customerDTO != null && customerDTO.getId() != null) {
          vehicleDTOs = ServiceManager.getService(IVehicleService.class).getVehicleListByCustomerId(customerDTO.getId());
          memberDTO = ServiceManager.getService(IMembersService.class).getMemberDTOByCustomerId(shopId, customerDTO.getId());
          if (memberDTO != null && MemberStatus.DISABLED == memberDTO.getStatus()) {
            memberDTO = null;
          }
        }
      }
      returnMap.put("customerDTO", customerDTO);
      returnMap.put("vehicleDTOs", vehicleDTOs);
      returnMap.put("memberDTO", memberDTO);
      return returnMap;
    } catch (Exception e) {
      LOG.debug("/customer.do?method=ajaxGetCustomerInfoByCustomerLandLine");
      LOG.error(e.getMessage(), e);
      return returnMap;
    }
  }

  @RequestMapping(params = "method=getSupplierJsonDataByMobile")
  @ResponseBody
  public Object getSupplierJsonDataByMobile(ModelMap model, HttpServletRequest request, HttpServletResponse response, String mobile) {
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    IUserService userService = ServiceManager.getService(IUserService.class);
    SupplierDTO supplierDTO = new SupplierDTO();
    try {
      List<SupplierDTO> supplierDTOs = userService.getSupplierByMobile(shopId, mobile);
      ISupplierPayableService supplierPayableService = ServiceManager.getService(ISupplierPayableService.class);

      if (CollectionUtils.isNotEmpty(supplierDTOs)) {
        supplierDTO = supplierDTOs.get(0);
        List<Double> doubleList = supplierPayableService.getSumPayableBySupplierId(supplierDTO.getId(), shopId, OrderDebtType.SUPPLIER_DEBT_PAYABLE);//应付款总额 实付总额
        //应付款总额
        supplierDTO.setTotalPayable(doubleList.get(0));
        //应收款总额
        SupplierRecordDTO supplierRecordDTO = ServiceManager.getService(ISupplierPayableService.class).getSupplierRecordDTOBySupplierId(shopId, supplierDTO.getId());
        if (null != supplierRecordDTO && null != supplierRecordDTO.getDebt()) {
          supplierDTO.setTotalDebt(NumberUtil.round(NumberUtil.numberValue(supplierRecordDTO.getDebt(), 0D), NumberUtil.MONEY_PRECISION));
        }
      }
    } catch (Exception e) {
      LOG.error("method=getSupplierJsonDataByMobile", e);
      supplierDTO.setName("error");
    }

    return supplierDTO;
  }

  @RequestMapping(params = "method=getSuppliersByMobile")
  @ResponseBody
  public Object getSuppliersByMobile(ModelMap model, HttpServletRequest request, HttpServletResponse response, String mobile) {
    AllListResult<SupplierDTO> result = new AllListResult<SupplierDTO>();
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    IUserService userService = ServiceManager.getService(IUserService.class);
    List<SupplierDTO> supplierDTOs = null;
    try {
      supplierDTOs = userService.getSupplierByMobile2(shopId, mobile);
      ISupplierPayableService supplierPayableService = ServiceManager.getService(ISupplierPayableService.class);

      if (CollectionUtils.isNotEmpty(supplierDTOs)) {
        for (SupplierDTO supplierDTO : supplierDTOs) {
          List<Double> doubleList = supplierPayableService.getSumPayableBySupplierId(supplierDTO.getId(), shopId, OrderDebtType.SUPPLIER_DEBT_PAYABLE);//应付款总额 实付总额
          //应付款总额
          supplierDTO.setTotalPayable(doubleList.get(0));
          //应收款总额
          SupplierRecordDTO supplierRecordDTO = ServiceManager.getService(ISupplierPayableService.class).getSupplierRecordDTOBySupplierId(shopId, supplierDTO.getId());
          if (null != supplierRecordDTO && null != supplierRecordDTO.getDebt()) {
            supplierDTO.setTotalDebt(NumberUtil.round(NumberUtil.numberValue(supplierRecordDTO.getDebt(), 0D), NumberUtil.MONEY_PRECISION));
          }
        }

      }
    } catch (Exception e) {
      LOG.error("method=getSuppliersByMobile", e);
    }
    result.setResults(supplierDTOs);
    return result;
  }

  @RequestMapping(params = "method=getSupplierJsonDataByTelephone")
  @ResponseBody
  public Object getSupplierJsonDataByTelephone(ModelMap model, HttpServletRequest request, HttpServletResponse response, String telephone) {
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    IUserService userService = ServiceManager.getService(IUserService.class);
    List<SupplierDTO> supplierDTOs = userService.getSupplierByTelephone(shopId, telephone);
    ISupplierPayableService supplierPayableService = ServiceManager.getService(ISupplierPayableService.class);
    SupplierDTO supplierDTO = new SupplierDTO();
    if (CollectionUtils.isNotEmpty(supplierDTOs)) {
      supplierDTO = supplierDTOs.get(0);
      List<Double> doubleList = supplierPayableService.getSumPayableBySupplierId(supplierDTO.getId(), shopId, OrderDebtType.SUPPLIER_DEBT_PAYABLE);//应付款总额 实付总额
      //应付款总额
      supplierDTO.setTotalPayable(doubleList.get(0));
      //应收款总额
      SupplierRecordDTO supplierRecordDTO = ServiceManager.getService(ISupplierPayableService.class).getSupplierRecordDTOBySupplierId(shopId, supplierDTO.getId());
      if (null != supplierRecordDTO && null != supplierRecordDTO.getDebt()) {
        supplierDTO.setTotalDebt(NumberUtil.round(NumberUtil.numberValue(supplierRecordDTO.getDebt(), 0D), NumberUtil.MONEY_PRECISION));
      }
    }

    return supplierDTO;
  }

  /**
   * 如果找到相同手机号，返回的result.success = true, result.data为相应实体
   * 如果customer绑定了supplier,则同时在客户与供应商中找相同手机号。返回找到的第一个实体(result.data)
   * 如果supplier绑定了customer,则作相同校验，对未绑定customer的supplier不作校验。
   *
   * @param model
   * @param request
   * @param mobile
   * @param customerId
   * @return
   */
  @RequestMapping(params = "method=getCustomerJsonDataByMobile")
  @ResponseBody
  public Result getCustomerJsonDataByMobile(ModelMap model, HttpServletRequest request, String mobile, Long customerId, Long supplierId) {
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    IUserService userService = ServiceManager.getService(IUserService.class);
    Result result = new Result();
    CustomerDTO customerDTO = null;
    SupplierDTO dbSupplierDTO = null;
    CustomerDTO dbCustomerDTO = null;
    try {
      if (supplierId != null && customerId == null) {
        dbSupplierDTO = userService.getSupplierById(supplierId);
        if (dbSupplierDTO.getCustomerId() == null) {    //非绑定客户的供应商不需校验.
          return new Result(false);
        }
      } else if (supplierId == null && customerId != null) {
        dbCustomerDTO = userService.getCustomerDTOByCustomerId(customerId, shopId);
      }

      List<CustomerDTO> customerDTOs = userService.getCustomerByMobile(shopId, mobile);

      if (CollectionUtils.isNotEmpty(customerDTOs)) {
        customerDTO = customerDTOs.get(0);
        double total = 0;
        double totalAmount = 0;
        double totalReturnDebt = 0;
        List<CustomerRecordDTO> customerRecords = userService.getCustomerRecordByCustomerId(customerDTO.getId());
        if (customerRecords.size() > 0) {
          total = customerRecords.get(0).getTotalReceivable();
          totalAmount = customerRecords.get(0).getTotalAmount();
          totalReturnDebt = NumberUtil.numberValue(customerRecords.get(0).getTotalPayable(), 0D);
        }
        customerDTO.setTotalConsume(totalAmount);
        customerDTO.setTotalReturnDebt(totalReturnDebt);
        customerDTO.setTotalReceivable(total);
        result.setMsg(true, "customer");
        result.setData(customerDTO);
        return result;
      }

      if ((dbCustomerDTO != null && dbCustomerDTO.getSupplierId() != null) || (dbSupplierDTO != null)) {
        List<SupplierDTO> supplierDTOs = userService.getSupplierByMobile(shopId, mobile);
        if (CollectionUtils.isNotEmpty(supplierDTOs)) {
          result.setMsg(true, "supplier");
          result.setData(CollectionUtil.getFirst(supplierDTOs));
          return result;
        }
      }

      result.setSuccess(false);
      return result;
    } catch (Exception e) {
      LOG.error("method=getCustomerJsonDataByMobile:", e);
//      customerDTO.setName("error"); // add by zhuj　暂时用这个字段　TODO ajax返回用同一的result的对象
      return new Result("error", false);
    }
  }

  /**
   * 一组手机号校验，看有没有修改过的手机号被其他客户占用了，
   *
   * @param model
   * @param request
   * @param response
   * @param mobiles
   * @return
   */
  @RequestMapping(params = "method=validateCustomerMobiles")
  @ResponseBody
  public Object validateCustomerMobiles(ModelMap model, HttpServletRequest request, HttpServletResponse response, String[] mobiles, Long customerId) {
    ICustomerService customerService = ServiceManager.getService(ICustomerService.class);
    Long shopId = WebUtil.getShopId(request);
    try {
      return customerService.validateCustomerMobiles(shopId, customerId, mobiles);
    } catch (Exception e) {
      LOG.error("validateCustomerMobiles  " + e.getMessage(), e);
      return new Result("网络异常", false);
    }
  }

  @RequestMapping(params = "method=getCustomersByMobile")
  @ResponseBody
  public Object getCustomersByMobile(ModelMap model, HttpServletRequest request, HttpServletResponse response, String mobile) {
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    IUserService userService = ServiceManager.getService(IUserService.class);
    AllListResult<CustomerDTO> result = new AllListResult<CustomerDTO>();
    try {
      List<CustomerDTO> customerDTOs = userService.getCustomerByMobile2(shopId, mobile);
      if (CollectionUtils.isNotEmpty(customerDTOs)) {
        double total = 0;
        double totalAmount = 0;
        double totalReturnDebt = 0;
        for (CustomerDTO customerDTO : customerDTOs) {
          List<CustomerRecordDTO> customerRecords = userService.getCustomerRecordByCustomerId(customerDTO.getId());

          if (customerRecords.size() > 0) {
            total = customerRecords.get(0).getTotalReceivable();
            totalAmount = customerRecords.get(0).getTotalAmount();
            totalReturnDebt = NumberUtil.numberValue(customerRecords.get(0).getTotalPayable(), 0D);
          }

          customerDTO.setTotalConsume(totalAmount);
          customerDTO.setTotalReturnDebt(totalReturnDebt);
          customerDTO.setTotalReceivable(total);
        }

      }
      result.setResults(customerDTOs);

    } catch (Exception e) {
      LOG.error("method=getCustomersByMobile:", e);
    }

    return result;
  }

  @RequestMapping(params = "method=getCustomerJsonDataByVehicleMobile")
  @ResponseBody
  public Object getCustomerJsonDataByVehicleMobile(ModelMap model, HttpServletRequest request, HttpServletResponse response, String mobile) {
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    IUserService userService = ServiceManager.getService(IUserService.class);
    List<CustomerDTO> customerDTOs = userService.getCustomerByVehicleMobile(shopId, mobile);
    CustomerDTO customerDTO = new CustomerDTO();
    if (CollectionUtils.isNotEmpty(customerDTOs)) {
      customerDTO = customerDTOs.get(0);
    }
    return customerDTO;
  }

  @RequestMapping(params = "method=getCustomerJsonDataByTelephone")
  @ResponseBody
  public Object getCustomerJsonDataByTelephone(ModelMap model, HttpServletRequest request, HttpServletResponse response, String telephone) {
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    IUserService userService = ServiceManager.getService(IUserService.class);
    List<CustomerDTO> customerDTOs = userService.getCustomerByTelephone(shopId, telephone);
    CustomerDTO customerDTO = new CustomerDTO();
    double total = 0;
    double totalAmount = 0;
    double totalReturnDebt = 0;
    if (CollectionUtils.isNotEmpty(customerDTOs)) {
      customerDTO = customerDTOs.get(0);
      List<CustomerRecordDTO> customerRecords = userService.getCustomerRecordByCustomerId(customerDTO.getId());

      if (customerRecords.size() > 0) {
        total = customerRecords.get(0).getTotalReceivable();
        totalAmount = customerRecords.get(0).getTotalAmount();
        totalReturnDebt = NumberUtil.numberValue(customerRecords.get(0).getTotalPayable(), 0D);
      }

    }
    customerDTO.setTotalConsume(totalAmount);
    customerDTO.setTotalReturnDebt(totalReturnDebt);
    customerDTO.setTotalReceivable(total);

    return customerDTO;
  }


  @RequestMapping(params = "method=getCustomerById")
  @ResponseBody
  public Object getCustomerById(HttpServletRequest request, Long customerId) {
    IUserService userService = ServiceManager.getService(IUserService.class);
    Long shopId = WebUtil.getShopId(request);
    if (customerId == null) {
      LOG.info("customerId cannot be null");
      return null;
    }
    CustomerDTO customerDTO = userService.getCustomerDTOByCustomerId(customerId, shopId);
    StringBuilder areaInfo = new StringBuilder();
    if (customerDTO.getProvince() != null) {
      AreaDTO areaDTO = AreaCacheManager.getAreaDTOByNo(customerDTO.getProvince());
      if (areaDTO != null) {
        areaInfo.append(areaDTO.getName());
      }
    }
    if (customerDTO.getCity() != null) {
      AreaDTO areaDTO = AreaCacheManager.getAreaDTOByNo(customerDTO.getCity());
      if (areaDTO != null) {
        areaInfo.append(areaDTO.getName());
      }
    }
    if (customerDTO.getRegion() != null) {
      AreaDTO areaDTO = AreaCacheManager.getAreaDTOByNo(customerDTO.getRegion());
      if (areaDTO != null) {
        areaInfo.append(areaDTO.getName());
      }
    }
    customerDTO.setAreaInfo(areaInfo.toString());
    if (customerDTO != null) {
      List<CustomerDTO> customerDTOList = new ArrayList<CustomerDTO>();
      customerDTOList.add(customerDTO);
      //供应商的经营范围
      IPreciseRecommendService preciseRecommendService = ServiceManager.getService(IPreciseRecommendService.class);
      preciseRecommendService.setCustomerSupplierBusinessScope(customerDTOList, null);

      IVehicleBrandModelRelationService vehicleBrandModelRelationService = ServiceManager.getService(IVehicleBrandModelRelationService.class);
      List<VehicleBrandModelRelationDTO> vehicleBrandModelRelationDTOList = vehicleBrandModelRelationService.getVehicleBrandModelRelationDTOByDataId(shopId, customerDTO.getId(), VehicleBrandModelDataType.CUSTOMER);
      customerDTO.setVehicleBrandModelRelationInfo(vehicleBrandModelRelationDTOList);
    }

    return customerDTO;
  }

  @RequestMapping(params = "method=saveOrUpdateCustomer")
  @ResponseBody
  public Object saveOrUpdateCustomer(HttpServletRequest request, CustomerDTO customerDTO) {
    Long shopId = null;
    try {
      shopId = WebUtil.getShopId(request);
      if (shopId == null) throw new Exception("shop id is null !!");
      IUserService userService = ServiceManager.getService(IUserService.class);
      IContactService contactService = ServiceManager.getService(IContactService.class);
      IPreciseRecommendService preciseRecommendService = ServiceManager.getService(IPreciseRecommendService.class);
      ICustomerService customerService = ServiceManager.getService(ICustomerService.class);

      SupplierDTO supplierDTO = null;
      boolean customerChangeArea = false;    //是否是更新区域

      customerDTO.setBirthday(DateUtil.convertDateStringToDateLong(DateUtil.MONTH_DATE, customerDTO.getBirthdayString()));
      // 填充联系人列表 add by zhuj
      ContactDTO[] contactDTOs = SupplierDTOGenerator.fillContactArray(request);
      if (customerDTO.getSupplierId() != null) {
        customerDTO.setIdentity("isSupplier");
        if (!ArrayUtils.isEmpty(contactDTOs)) {
          for (ContactDTO contactDTO : contactDTOs) {
            if (contactDTO != null && contactDTO.isValidContact()) {
              contactDTO.setSupplierId(customerDTO.getSupplierId());
            }
          }
        }
      }
      customerDTO.setContacts(contactDTOs);
      customerDTO.setShopId(shopId);

      if (customerDTO.getId() != null) {
        CustomerDTO dbCustomerDTO = userService.getCustomerDTOByCustomerId(customerDTO.getId(), shopId);
        if (!StringUtil.compareSame(customerDTO.getProvince(), dbCustomerDTO.getProvince())
          || !StringUtil.compareSame(customerDTO.getCity(), dbCustomerDTO.getCity())
          || !StringUtil.compareSame(customerDTO.getRegion(), dbCustomerDTO.getRegion())) {
          customerChangeArea = true;
        }
        customerDTO.fillUnEditInfo(dbCustomerDTO);

        CustomerRecordDTO customerRecordDTO = userService.getCustomerRecordDTOByCustomerIdAndShopId(shopId, customerDTO.getId());

        if (customerRecordDTO == null) {
          customerRecordDTO = new CustomerRecordDTO();
        }
        customerRecordDTO.fromCustomerDTO(customerDTO);

        customerDTO.setFromManagePage(true);

        customerDTO = userService.updateCustomer(customerDTO);
        userService.updateCustomerRecord(customerRecordDTO);
        if (customerChangeArea) { //当改变客户所属区域时，插入一条schedule任务
          userService.generateCustomerOrderIndexScheduleDTO(shopId, customerDTO);
        }

        //同时更新供应商
        if (customerDTO.getSupplierId() != null) {
          supplierDTO = processSupplierInfo(shopId, customerDTO);

          // add by zhuj 这里需要处理修改的新增的联系人
          String isMergeContact = request.getParameter("mergeContact");
          if (StringUtils.isNotBlank(isMergeContact) && StringUtils.equals(isMergeContact, "noMerge")) {
            // 不需要合并的情况 目前对客户和供应商的联系人都不进行处理 noMerge这个标识 和 modifySupplier.js里面的赋值耦合
          } else {
            contactService.updateContactsBelongCustomerAndSupplier(customerDTO.getId(), customerDTO.getSupplierId(), shopId, customerDTO.getContacts()); // add by zhuj
            contactService.addContactsBelongCustomerAndSupplier(customerDTO.getId(), customerDTO.getSupplierId(), shopId, customerDTO.getContacts());
          }
        }
      } else {//新增客户
        userService.createCustomer(customerDTO);
        CustomerRecordDTO customerRecordDTO = new CustomerRecordDTO();
        customerRecordDTO.fromCustomerDTO(customerDTO);
        customerRecordDTO.setCustomerId(customerDTO.getId());
        userService.createCustomerRecord(customerRecordDTO);
        if (customerDTO.getSupplierId() != null) {
          supplierDTO = processSupplierInfo(shopId, customerDTO);
          // add by zhuj 新增的联系人在这边处理
          contactService.addContactsBelongCustomerAndSupplier(supplierDTO.getCustomerId(), supplierDTO.getId(), shopId, customerDTO.getContacts());
        }
      }

      userService.saveCustomerServiceCategoryRelation(shopId, customerDTO);
      userService.saveCustomerVehicleBrandModelRelation(shopId, customerDTO, StandardBrandModelCache.getShopVehicleBrandModelDTOMap());

      //设置客户供应商的经营范围
      userService.saveOrUpdateCustomerSupplierBusinessScope(customerDTO, supplierDTO, customerDTO.getThirdCategoryIdStr());
      preciseRecommendService.getCustomerSupplierBusinessScopeForAdd(customerDTO, supplierDTO);
      userService.updateCustomerSupplierBusinessScope(customerDTO, supplierDTO);
      //reindex customer in solr  如果 既是客户又是供应商 包含了重新供应商的索引
      ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexCustomerByCustomerId(customerDTO.getId());

      Map<String, String> invoiceCatagoryMap = TxnConstant.getInvoiceCatagoryMap(request.getLocale());
      Map<String, String> customerTypeMap = TxnConstant.getCustomerTypeMap(request.getLocale());
      Map<String, String> settlementTyoeMap = TxnConstant.getSettlementTypeMap(request.getLocale());
      customerDTO.setCustomerKindStr(customerTypeMap.get(customerDTO.getCustomerKind()));
      customerDTO.setSettlementTypeStr(settlementTyoeMap.get(String.valueOf(customerDTO.getSettlementType())));
      customerDTO.setInvoiceCategoryStr(invoiceCatagoryMap.get(String.valueOf(customerDTO.getInvoiceCategory())));

      Boolean wholesalerVersion = ConfigUtils.isWholesalerVersion(WebUtil.getShopVersionId(request));
      if (wholesalerVersion) {
        customerService.addAreaInfoToCustomerDTO(customerDTO);
      }
      customerDTO.fillingContacts();
      customerDTO.compositeLandline();
      return new Result(true, customerDTO);
    } catch (Exception e) {
      LOG.error("/customer.do?method=saveOrUpdateCustomer");
      LOG.error("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
      return new Result(false);
    }
  }

  private SupplierDTO processSupplierInfo(Long shopId, CustomerDTO customerDTO) throws Exception {
    IUserService userService = ServiceManager.getService(IUserService.class);
    ISupplierService supplierService = ServiceManager.getService(ISupplierService.class);
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    SupplierDTO supplierDTO = userService.getSupplierById(customerDTO.getSupplierId());
    boolean supplierChangeArea = false;
    if (!StringUtil.compareSame(customerDTO.getProvince(), supplierDTO.getProvince())
      || !StringUtil.compareSame(customerDTO.getCity(), supplierDTO.getCity())
      || !StringUtil.compareSame(customerDTO.getRegion(), supplierDTO.getRegion())) {
      supplierChangeArea = true;
    }
    supplierDTO.fromCustomerDTO(customerDTO, null);
    supplierDTO.setCustomerId(customerDTO.getId());
    supplierDTO.setIdentity("isCustomer");
    userService.updateSupplier(supplierDTO); //add by zhuj 更新供应商信息
    supplierService.saveSupplierVehicleBrandModelRelation(shopId, supplierDTO, StandardBrandModelCache.getShopVehicleBrandModelDTOMap());
    //建立联系时更新remind_event
    txnService.updateRemindEvent2(shopId, customerDTO.getId(), supplierDTO.getId());

    if (supplierChangeArea) { //当改变供应商所属区域时，插入一条schedule任务
      userService.generateSupplierOrderIndexScheduleDTO(shopId, supplierDTO);
    }
    return supplierDTO;
  }

  @RequestMapping(params = "method=cancelCustomerBindingSupplier")
  @ResponseBody
  public String cancelCustomerBindingSupplier(HttpServletRequest request, Long customerId) {
    String result = "success";
    Long shopId = null;
    try {
      shopId = WebUtil.getShopId(request);
      if (shopId == null) throw new Exception("shop id is null !!");
      if (customerId == null) throw new Exception("customerId is null !!");
      IUserService userService = ServiceManager.getService(IUserService.class);
      CustomerDTO customerDTO = userService.getCustomerDTOByCustomerId(customerId, shopId);
      CustomerRecordDTO customerRecordDTO = userService.getCustomerRecordDTOByCustomerIdAndShopId(shopId, customerId);

      //更新欠款提醒remind_event,将原来既是客户又是供应商的入库退货单的那条记录中的CustomerId清空，supplierId设置值
      if (customerDTO.getSupplierId() != null) {
        ISupplierService supplierService = ServiceManager.getService(ISupplierService.class);
        SupplierDTO supplierDTO = supplierService.getSupplierById(customerDTO.getSupplierId(), shopId);

        ServiceManager.getService(ITxnService.class).updateRemindEvent(customerDTO.getShopId(), customerDTO.getId(), customerDTO.getSupplierId());
        customerDTO.setIdentity(null);
        customerDTO.setSupplierId(null);
        customerDTO.setCancel(true);
        userService.updateCustomer(customerDTO);
        if (customerRecordDTO == null) {
          customerRecordDTO = new CustomerRecordDTO();
        }
        customerRecordDTO.fromCustomerDTO(customerDTO);
        userService.updateCustomerRecord(customerRecordDTO);

        supplierDTO.setIdentity(null);
        supplierDTO.setCustomerId(null);
        supplierDTO.setCancel(true);
        userService.updateSupplier(supplierDTO);
        ServiceManager.getService(IContactService.class).cancelRelatedCusSupContacts(customerDTO.getId(), supplierDTO.getId(), shopId); // add by zhuj 取消关联联系人相关的处理
        ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexCustomerByCustomerId(customerDTO.getId());
        ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexSupplierBySupplierId(supplierDTO.getId());
      }

    } catch (Exception e) {
      LOG.error("/customer.do");
      LOG.error("method=cancelCustomerBindingSupplier");
      LOG.error("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
      result = "fail";
    }

    return result;
  }

  @RequestMapping(params = "method=saveCustomerIdentificationImageRelation")
  @ResponseBody
  public Object saveCustomerIdentificationImageRelation(HttpServletRequest request, Long customerId, String[] customerIdentificationImagePaths) {
    IImageService imageService = ServiceManager.getService(IImageService.class);
    Long shopId = WebUtil.getShopId(request);
    Result result = new Result();
    try {
      if (shopId == null) throw new Exception("shopId is null!");
      if (customerId == null) throw new Exception("customerId is null!");
      List<DataImageRelationDTO> dataImageRelationDTOList = new ArrayList<DataImageRelationDTO>();
      List<DataImageDetailDTO> dataImageDetailDTOList = new ArrayList<DataImageDetailDTO>();
      if (!ArrayUtils.isEmpty(customerIdentificationImagePaths)) {
        int i = 0;
        for (String imagePath : customerIdentificationImagePaths) {
          if (StringUtils.isNotBlank(imagePath)) {
            DataImageRelationDTO dataImageRelationDTO = new DataImageRelationDTO(shopId, customerId, DataType.CUSTOMER, ImageType.CUSTOMER_IDENTIFICATION_IMAGE, i + 1);
            dataImageRelationDTO.setImageInfoDTO(new ImageInfoDTO(shopId, imagePath));
            dataImageRelationDTOList.add(dataImageRelationDTO);
            dataImageDetailDTOList.add(new DataImageDetailDTO(dataImageRelationDTO, ImageUtils.generateUpYunImagePath(imagePath, ImageScene.CUSTOMER_IDENTIFICATION_IMAGE.getImageVersion()), imagePath));
            i++;
          }
        }
      }
      Set<ImageType> imageTypeSet = new HashSet<ImageType>();
      imageTypeSet.add(ImageType.CUSTOMER_IDENTIFICATION_IMAGE);
      imageService.saveOrUpdateDataImageDTOs(shopId, imageTypeSet, DataType.CUSTOMER, customerId, dataImageRelationDTOList.toArray(new DataImageRelationDTO[dataImageRelationDTOList.size()]));
      result.setData(dataImageDetailDTOList);
      return result;
    } catch (Exception e) {
      LOG.error("/customer.do");
      LOG.error("method=saveCustomerIdentificationImageRelation");
      LOG.error("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
      result.setSuccess(false);
      return result;
    }
  }

  @RequestMapping(params = "method=printCustomerIdentificationImage")
  public String printCustomerIdentificationImage(HttpServletRequest request, String imagePath) {
    request.setAttribute("imageURL", ImageUtils.generateUpYunImagePath(imagePath, null));
    return "/common/imagePrint";
  }

  public String getCustomerInfoByCustomerId(CustomerDTO customerDTO) {
    if (customerDTO != null) {
      return "";
    }
    ICustomerService customerService = ServiceManager.getService(ICustomerService.class);

    return customerDTO.getUserId().toString();


  }


  /**
   * 更新车辆预约信息
   *
   * @param request
   * @param response
   * @param customerVehicleResponse
   */
  @RequestMapping(params = "method=ajaxAddOrUpdateVehicleAppoint")
  public void ajaxAddOrUpdateVehicleAppoint(HttpServletRequest request, HttpServletResponse response, CustomerVehicleResponse customerVehicleResponse) {
    try {
      Long shopId = (Long) request.getSession().getAttribute("shopId");
      IUserService userService = ServiceManager.getService(IUserService.class);
      ICustomerService customerService = ServiceManager.getService(ICustomerService.class);
      IVehicleService vehicleService = ServiceManager.getService(IVehicleService.class);
      Map jsonMap = new HashMap();
      if (shopId == null) {
        return;
      }
      String vehicleId = request.getParameter("vehicleId");
      Long customerId = Long.parseLong(request.getParameter("customerId"));

      String maintainMileage = request.getParameter("maintainMileage");
      String by = request.getParameter("by");
      String bx = request.getParameter("bx");
      String yc = request.getParameter("yc");

      AppointServiceDTO appointServiceDTO = new AppointServiceDTO();
      appointServiceDTO.setShopId(shopId);
      appointServiceDTO.setCustomerId(String.valueOf(customerId));
      appointServiceDTO.setVehicleId(vehicleId);
      appointServiceDTO.setMaintainTimeStr(by);
      appointServiceDTO.setInsureTimeStr(bx);
      appointServiceDTO.setExamineTimeStr(yc);
      if (StringUtils.isNotEmpty(maintainMileage) && StringUtils.isNumeric(maintainMileage)) {
        appointServiceDTO.setMaintainMileage(NumberUtil.longValue(maintainMileage));
      }
      CustomerVehicleDTO customerVehicleDTO = userService.addYuyueToCustomerVehicle(appointServiceDTO);


      //add by WLF 生成提醒
      CustomerDTO customerDTO = userService.getCustomerById(customerId);
      if (StringUtils.isBlank(vehicleId)) {
        vehicleId = appointServiceDTO.getVehicleId();
      }
      VehicleDTO vehicleDTO = userService.getVehicleById(new Long(vehicleId));
      List<AppointServiceDTO> customAppointServiceDTOList = customerVehicleResponse.getAppointServiceDTOs();
      if (CollectionUtils.isNotEmpty(customAppointServiceDTOList)) {
        customerService.saveCustomAppointServiceDTO(shopId, customerId, vehicleDTO.getId(), customAppointServiceDTOList.toArray(new AppointServiceDTO[customAppointServiceDTOList.size()]));
      }


      List<CustomerServiceJobDTO> customerServiceJobDTOList = userService.getCustomerServiceJobByCustomerIdAndVehicleId(shopId, customerId, new Long(vehicleId));
      if (CollectionUtils.isNotEmpty(customerServiceJobDTOList)) {
        for (CustomerServiceJobDTO customerServiceJobDTO : customerServiceJobDTOList) {
          //保养里程定时钟处理
          if (customerServiceJobDTO != null && !UserConstant.MAINTAIN_MILEAGE.equals(customerServiceJobDTO.getRemindType())) {
            ServiceManager.getService(ITxnService.class).saveRemindEvent(new CustomerServiceJob().fromDTO(customerServiceJobDTO), customerDTO.getName(), customerDTO.getMobile(), vehicleDTO.getLicenceNo());
          }
        }
        //add by WLF 更新缓存
        ServiceManager.getService(ITxnService.class).updateRemindCountInMemcacheByTypeAndShopId(RemindEventType.CUSTOMER_SERVICE, shopId);
      }
      ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexCustomerByCustomerId(customerId);
      ServiceManager.getService(IVehicleSolrWriterService.class).createVehicleSolrIndex(shopId, vehicleDTO.getId());
      //获取客户拥有的车辆数
      int vehicleNum = vehicleService.getVehicleListByCustomerId(customerId).size();
      jsonMap.put("vehicleId", vehicleId);
      jsonMap.put("vehicleNum", new Long(vehicleNum).toString());

      List<AppointServiceDTO> appointServiceDTOs = new ArrayList<AppointServiceDTO>();
      if (CollectionUtil.isNotEmpty(customAppointServiceDTOList)) {
        for (AppointServiceDTO serviceDTO : customAppointServiceDTOList) {
          if (OperateType.LOGIC_DELETE.toString().equals(serviceDTO.getOperateType())) {
            continue;
          }
          if (StringUtils.isEmpty(serviceDTO.getAppointName()) && StringUtils.isEmpty(serviceDTO.getAppointDate())) {
            continue;
          }
          appointServiceDTOs.add(serviceDTO);
        }
      }
      jsonMap.put("customerVehicleResponse", new CustomerVehicleResponse(customerVehicleDTO, vehicleDTO, appointServiceDTOs));
      PrintWriter writer = response.getWriter();
      writer.write(JsonUtil.mapToJson(jsonMap));
      writer.close();
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
  }


  @RequestMapping(params = "method=getVehicleList")
  @ResponseBody
  public Object getVehicleList(HttpServletRequest request, VehicleSearchConditionDTO vehicleSearchConditionDTO) {
    Long shopId = WebUtil.getShopId(request);
    try {
      if (shopId == null) throw new Exception("shopId is null!");
      vehicleSearchConditionDTO.setShopId(shopId);
      vehicleSearchConditionDTO.setSearchStrategies(new VehicleSearchConditionDTO.SearchStrategy[]{VehicleSearchConditionDTO.SearchStrategy.SEARCH_STRATEGY_STATS});
      vehicleSearchConditionDTO.setStatsFields(new String[]{VehicleSearchConditionDTO.StatsFields.OBD_ID.getName(), VehicleSearchConditionDTO.StatsFields.IS_MOBILE_VEHICLE.getName(), VehicleSearchConditionDTO.StatsFields.VEHICLE_TOTAL_CONSUME_AMOUNT.getName()});
      if (StringUtils.isNotBlank(vehicleSearchConditionDTO.getCustomerInfo())) {
        JoinSearchConditionDTO joinSearchConditionDTO = new JoinSearchConditionDTO();
        joinSearchConditionDTO.setShopId(shopId);
        joinSearchConditionDTO.setFromColumn("id");
        joinSearchConditionDTO.setToColumn("customer_id");
        joinSearchConditionDTO.setFromIndex(SolrClientHelper.BcgogoSolrCore.CUSTOMER_SUPPLIER_CORE.getValue());
        joinSearchConditionDTO.setCustomerOrSupplier(new String[]{"customer"});
        joinSearchConditionDTO.setCustomerOrSupplierInfo(vehicleSearchConditionDTO.getCustomerInfo());
        vehicleSearchConditionDTO.setJoinSearchConditionDTO(joinSearchConditionDTO);
      }

      List<Object> result = new ArrayList<Object>();
      VehicleSearchResultDTO vehicleSearchResultDTO = ServiceManager.getService(ISearchVehicleService.class).queryVehicle(vehicleSearchConditionDTO);
      IVehicleGenerateService vehicleGenerateService = ServiceManager.getService(IVehicleGenerateService.class);
      vehicleGenerateService.generateVehicleSearchResult(shopId, vehicleSearchResultDTO);
      Pager pager = new Pager(vehicleSearchResultDTO == null ? 0 : Integer.valueOf(vehicleSearchResultDTO.getNumFound() + ""), vehicleSearchConditionDTO.getStartPageNo(), vehicleSearchConditionDTO.getMaxRows());
      result.add(vehicleSearchResultDTO);
      result.add(pager);
      return result;
    } catch (Exception e) {
      LOG.error("/customer.do?method=getVehicleList");
      LOG.error("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
      return null;
    }
  }

  @RequestMapping(params = "method=vehicleManageList")
  public String vehicleList(HttpServletRequest request, ModelMap modelMap) {
    Long shopId = WebUtil.getShopId(request);
    try {
      if (shopId == null) throw new Exception("shopId is null!");
      modelMap.addAttribute("vehicleSearchConditionDTO", new VehicleSearchConditionDTO());
      return "/customer/vehicleManageList";
    } catch (Exception e) {
      LOG.error("/customer.do?method=vehicleManageList");
      LOG.error("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
      return "/";
    }
  }

  @RequestMapping(params = "method=sendMsgByVehicleSearchConditionDTO")
  public String sendMsgByVehicleSearchConditionDTO(HttpServletRequest request, ModelMap modelMap, VehicleSearchConditionDTO vehicleSearchConditionDTO) {
    Long shopId = WebUtil.getShopId(request);
    StringBuilder contactIds = new StringBuilder();
    SmsController smsController = new SmsController();
    try {
      if (shopId == null) throw new Exception("shopId is null!");
      vehicleSearchConditionDTO.setShopId(shopId);
      vehicleSearchConditionDTO.setMaxRows(Integer.MAX_VALUE);
      if (StringUtils.isNotBlank(vehicleSearchConditionDTO.getCustomerInfo())) {
        JoinSearchConditionDTO joinSearchConditionDTO = new JoinSearchConditionDTO();
        joinSearchConditionDTO.setShopId(shopId);
        joinSearchConditionDTO.setFromColumn("id");
        joinSearchConditionDTO.setToColumn("customer_id");
        joinSearchConditionDTO.setFromIndex(SolrClientHelper.BcgogoSolrCore.CUSTOMER_SUPPLIER_CORE.getValue());
        joinSearchConditionDTO.setCustomerOrSupplier(new String[]{"customer"});
        joinSearchConditionDTO.setCustomerOrSupplierInfo(vehicleSearchConditionDTO.getCustomerInfo());
        vehicleSearchConditionDTO.setJoinSearchConditionDTO(joinSearchConditionDTO);
      }
      VehicleSearchResultDTO vehicleSearchResultDTO = ServiceManager.getService(ISearchVehicleService.class).queryVehicle(vehicleSearchConditionDTO);
      Set<Long> customerIdSet = new HashSet<Long>();
      for (VehicleDTO vehicleDTO : vehicleSearchResultDTO.getVehicleDTOList()) {
        if (StringUtils.isNotBlank(vehicleDTO.getMobile())) {
          contactIds.append(vehicleDTO.getId()).append(",");
        } else {
          customerIdSet.add(vehicleDTO.getCustomerId());
        }
      }
      Map<Long, List<ContactDTO>> contactDTOMap = ServiceManager.getService(IContactService.class).getContactsByCustomerOrSupplierIds(new ArrayList<Long>(customerIdSet), "customer");
      for (Long customerId : customerIdSet) {
        List<ContactDTO> contactDTOList = contactDTOMap.get(customerId);
        if (CollectionUtils.isNotEmpty(contactDTOList)) {
          for (ContactDTO contactDTO : contactDTOList) {
            if (StringUtils.isNotBlank(contactDTO.getMobile())) {
              contactIds.append(contactDTO.getId()).append(",");
              break;
            }
          }
        }
      }
    } catch (Exception e) {
      LOG.error("/customer.do?method=sendMsgByVehicleSearchConditionDTO");
      LOG.error("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
    }
    return smsController.smsWrite(request, modelMap, contactIds.toString(), new CustomerRemindSms());
  }

  @RequestMapping(params = "method=sendVehicleMsg")
  @ResponseBody
  public Object sendVehicleMsg(HttpServletRequest request, CustomerRemindSms customerRemindSms) {
    Long shopId = WebUtil.getShopId(request);
    Long userId = WebUtil.getUserId(request);
    try {
      customerRemindSms.setShopId(shopId);
      RFITxnService rfiTxnService = ServiceManager.getService(RFITxnService.class);
      customerRemindSms = ServiceManager.getService(ISmsService.class).sendCustomerServiceRemindMessage(customerRemindSms);
      Long remindEventId = NumberUtil.isLongNumber(request.getParameter("remindEventId")) ? Long.valueOf(request.getParameter("remindEventId")) : null;
      Result result = rfiTxnService.bcgogoAppSendMsg(shopId, userId, remindEventId, customerRemindSms);
      return result;
    } catch (Exception e) {
      LOG.debug("/customer.do?method=sendVehicleMsg");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
    }
    return null;
  }
}


