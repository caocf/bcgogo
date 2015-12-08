package com.bcgogo.txn;

import com.bcgogo.common.*;
import com.bcgogo.config.service.IServiceCategoryService;
import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.enums.DraftOrderStatus;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.enums.app.AppointOrderStatus;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.*;
import com.bcgogo.txn.model.DraftOrder;
import com.bcgogo.txn.model.Service;
import com.bcgogo.txn.service.*;
import com.bcgogo.user.dto.*;
import com.bcgogo.user.service.IMembersService;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.user.service.IVehicleService;
import com.bcgogo.user.service.app.IAppUserService;
import com.bcgogo.user.service.utils.BcgogoShopLogicResourceUtils;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.TxnConstant;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-9-4
 * Time: 下午6:02
 */
@Controller
@RequestMapping("/appoint.do")
public class AppointOrderController {
  private static final Logger LOG = LoggerFactory.getLogger(AppointOrderController.class);
  //private static final String SHOW_EDIT_PAGE = "/txn/appointOrder/appointOrderEdit";
  private static final String SHOW_EDIT_PAGE = "/txn/appointOrder/newAppointOrderEdit";
  private static final String SHOW_SEARCH_PAGE = "/txn/appointOrder/appointOrderList";
  private static final String REDIRECT_SHOW  = "redirect:appoint.do?method=showAppointOrderDetail";
  private static final String APPOINT_ORDER_DETAIL = "/txn/appointOrder/appointOrderDetail";

  @Autowired
  private IAppointOrderService appointOrderService;
  @Autowired
  private IAppUserService appUserService;


  @RequestMapping(params = "method=validateCreateAppointOrder")
  @ResponseBody
  public Object validateCreateAppointOrder(ModelMap model, HttpServletRequest request, HttpServletResponse response) {
    Long shopId = WebUtil.getShopId(request);
    Result result = new Result();
    try {
      result = appointOrderService.validateCreateAppointOrder(shopId);
    } catch (Exception e) {
      LOG.error("appoint.do?method=createAppointOrder,shopId:{}" + e.getMessage(), shopId, e);
    }
    return result;
  }

  @RequestMapping(params = "method=createAppointOrder")
  public String createAppointOrder(ModelMap model, HttpServletRequest request, HttpServletResponse response) {
    Long shopId = WebUtil.getShopId(request);
    try {
      request.setAttribute("customerTypeMap", TxnConstant.getCustomerTypeMap(request.getLocale()));
      AppointOrderDTO appointOrderDTO = new AppointOrderDTO();
      appointOrderDTO.setItemDTOs(new AppointOrderMaterialDTO[]{new AppointOrderMaterialDTO()});
      appointOrderDTO.setServiceDTOs(new AppointOrderServiceDetailDTO[]{new AppointOrderServiceDetailDTO()});
      appointOrderDTO.setAssistantMan(WebUtil.getUserName(request));
      model.addAttribute("appointOrderDTO", appointOrderDTO);
      model.addAttribute("fourSShopVersions", ConfigUtils.isFourSShopVersion(WebUtil.getShopVersionId(request)));
      generateServiceScope(model,request);
    } catch (Exception e) {
      LOG.error("appoint.do?method=createAppointOrder,shopId:{}" + e.getMessage(), shopId, e);
    }
    return SHOW_EDIT_PAGE;
  }

  @RequestMapping(params = "method=createAppointOrderByCustomerInfo")
  public String createAppointOrderByCustomerInfo(ModelMap model, HttpServletRequest request, HttpServletResponse response,
                                                 CustomerDTO customerInfo) {
    Long shopId = WebUtil.getShopId(request);
    try {
      customerInfo.setShopId(shopId);
      AppointOrderDTO appointOrderDTO = appointOrderService.generateAppointOrderByCustomerInfo(customerInfo);
      appointOrderDTO.setAssistantMan(WebUtil.getUserName(request));

      model.addAttribute("appointOrderDTO", appointOrderDTO);
      model.addAttribute("createFromFlag", "customerDetail");
      if (appointOrderDTO.getCustomerId() != null) {
        IVehicleService vehicleService = ServiceManager.getService(IVehicleService.class);
        List<VehicleDTO> vehicleDTOs = vehicleService.getVehicleListByCustomerId(appointOrderDTO.getCustomerId());
        model.addAttribute("vehicleDTOs", vehicleDTOs);
      }
      generateServiceScope(model, request);
      request.setAttribute("customerTypeMap", TxnConstant.getCustomerTypeMap(request.getLocale()));
    } catch (Exception e) {
      LOG.error("appoint.do?method=createAppointOrderByCustomerInfo,shopId:{}" + e.getMessage(), shopId, e);
    }
    return SHOW_EDIT_PAGE;
  }

  //组装预约服务类型，预约方式
  private void generateServiceScope(ModelMap model, HttpServletRequest request) {
    IServiceCategoryService serviceCategoryService = ServiceManager.getService(IServiceCategoryService.class);
    Map<Long, String> serviceScopeMap = serviceCategoryService.getShopServiceCategoryIdNameMap(WebUtil.getShopId(request));
    model.addAttribute("serviceScope", serviceScopeMap);
  }

  @RequestMapping(params = "method=createAppointOrderByShopFaultCodeIds")
  public String createAppointOrderByShopFaultCodeIds(ModelMap model, HttpServletRequest request, String shopFaultInfoIds) {
    Long shopId = WebUtil.getShopId(request);
    try {
      AppointOrderDTO appointOrderDTO = appointOrderService.generateAppointOrderByShopFaultCodeIds(shopId, shopFaultInfoIds);
      appointOrderDTO.setAssistantMan(WebUtil.getUserName(request));

      model.addAttribute("appointOrderDTO", appointOrderDTO);
      generateServiceScope(model, request);
      request.setAttribute("customerTypeMap", TxnConstant.getCustomerTypeMap(request.getLocale()));
    } catch (Exception e) {
      LOG.error("appoint.do?method=createAppointOrderByCustomerInfo,shopId:{}" + e.getMessage(), shopId, e);
    }
    return SHOW_EDIT_PAGE;
  }

  @RequestMapping(params = "method=showAppointOrderDetail")
  public String showAppointOrderDetail(ModelMap model, HttpServletRequest request, HttpServletResponse response) {
      Long shopId = WebUtil.getShopId(request);
      String appointOrderIdStr = request.getParameter("appointOrderId");
      try {
        if(shopId == null) {
          throw new Exception("shopId can not be null");
        }
        if(StringUtils.isEmpty(appointOrderIdStr)) {
          throw new Exception("appointOrderId can not be null!");
        }
        Long appointOrderId = NumberUtil.longValue(appointOrderIdStr);
        AppointOrderDTO appointOrderDTO = appointOrderService.getAppointOrderById(shopId,appointOrderId);

        model.addAttribute("appointOrderDTO",appointOrderDTO);
        generateServiceScope(model,request);
        //编辑页面
        if(appointOrderDTO !=null && AppointOrderStatus.getModifyPreStatus().contains(appointOrderDTO.getStatus())
            &&"EDIT".equals(request.getParameter("operationType"))){
          if (ArrayUtils.isEmpty(appointOrderDTO.getItemDTOs())) {
            appointOrderDTO.setItemDTOs(new AppointOrderMaterialDTO[]{new AppointOrderMaterialDTO()});
          }
          if (ArrayUtils.isEmpty(appointOrderDTO.getServiceDTOs())) {
            appointOrderDTO.setServiceDTOs(new AppointOrderServiceDetailDTO[]{new AppointOrderServiceDetailDTO()});
          }
          request.setAttribute("customerTypeMap", TxnConstant.getCustomerTypeMap(request.getLocale()));
          return SHOW_EDIT_PAGE;
        }

      } catch (Exception e) {
        LOG.error("appoint.do?method=showAppointOrderDetail"+e.getMessage(),e);
      }
      return APPOINT_ORDER_DETAIL;
  }


  /**
   * web 端保预约单
   * @param model
   * @param request
   * @param response
   * @param appointOrderDTO
   * @return
   */
  @RequestMapping(params = "method=saveAppointOrder")
  public String saveAppointOrder(ModelMap model, HttpServletRequest request, HttpServletResponse response,AppointOrderDTO appointOrderDTO) {
     Long shopId = WebUtil.getShopId(request);
     try {
       generateCreateAppointOrderDTO(model, request, appointOrderDTO);
       Result result =  validateSaveAppointOrder(model, request, appointOrderDTO);
       if(result != null && !result.isSuccess()){
         generateServiceScope(model,request);
         request.setAttribute("customerTypeMap", TxnConstant.getCustomerTypeMap(request.getLocale()));
         return SHOW_EDIT_PAGE;
       }
       appointOrderService.handleSaveAppointOrder(appointOrderDTO);
       model.addAttribute("appointOrderId",appointOrderDTO.getId());
       return REDIRECT_SHOW;
     } catch (Exception e) {
       LOG.error("appoint.do?method=saveAppointOrder,shopId:{}" + e.getMessage(), shopId, e);
       model.addAttribute("result",new Result("保存失败！",false));
       generateServiceScope(model,request);
       request.setAttribute("customerTypeMap", TxnConstant.getCustomerTypeMap(request.getLocale()));
       return SHOW_EDIT_PAGE;
     }
   }

  @RequestMapping(params = "method=validateUpdateAppointOrder")
  @ResponseBody
  public Object validateUpdateAppointOrder(ModelMap model, HttpServletRequest request, HttpServletResponse response,AppointOrderDTO appointOrderDTO) {
     Long shopId = WebUtil.getShopId(request);
    Result result = new Result();
     try {
       generateBaseAppointOrderDTO(model, request, appointOrderDTO);
       result = appointOrderService.validateUpdateAppointOrder(appointOrderDTO);
     } catch (Exception e) {
       LOG.error("appoint.do?method=validateUpdateAppointOrder,shopId:{}" + e.getMessage(), shopId, e);
     }
    return result;
   }

  @RequestMapping(params = "method=updateAppointOrder")
  public String updateAppointOrder(ModelMap model, HttpServletRequest request, HttpServletResponse response,AppointOrderDTO appointOrderDTO) {
     Long shopId = WebUtil.getShopId(request);
     try {
       generateBaseAppointOrderDTO(model, request, appointOrderDTO);
       Result result =  appointOrderService.validateUpdateAppointOrder(appointOrderDTO);
       if(result != null && !result.isSuccess()){
         generateServiceScope(model,request);
         request.setAttribute("customerTypeMap", TxnConstant.getCustomerTypeMap(request.getLocale()));
         return SHOW_EDIT_PAGE;
       }
       appointOrderService.handleUpdateAppointOrder(appointOrderDTO);
       model.addAttribute("appointOrderId",appointOrderDTO.getId());
       return REDIRECT_SHOW;
     } catch (Exception e) {
       LOG.error("appoint.do?method=updateAppointOrder,shopId:{}" + e.getMessage(), shopId, e);
     }
    request.setAttribute("customerTypeMap", TxnConstant.getCustomerTypeMap(request.getLocale()));
     return SHOW_EDIT_PAGE;
   }

  //组装信息
  private void generateCreateAppointOrderDTO(ModelMap model, HttpServletRequest request, AppointOrderDTO appointOrderDTO) throws Exception {
    if (appointOrderDTO != null) {
      appointOrderDTO.setShopId(WebUtil.getShopId(request));
      appointOrderDTO.setUserId(WebUtil.getUserId(request));
      if (StringUtils.isNotBlank(appointOrderDTO.getAppointTimeStr())) {
        appointOrderDTO.setAppointTime(DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_DEFAULT, appointOrderDTO.getAppointTimeStr()));
      }
      appointOrderDTO.setCreateTime(System.currentTimeMillis());
    }
  }

  private void generateBaseAppointOrderDTO(ModelMap model, HttpServletRequest request, AppointOrderDTO appointOrderDTO) throws Exception {
    if (appointOrderDTO != null) {
      appointOrderDTO.setShopId(WebUtil.getShopId(request));
      appointOrderDTO.setUserId(WebUtil.getUserId(request));
    }
  }

  private Result validateSaveAppointOrder(ModelMap model, HttpServletRequest request,AppointOrderDTO appointOrderDTO){
    Result result = new Result();

    return result;
  }

  @RequestMapping(params = "method=getAppointOrderToPrint")
  public void getAppointOrderToPrint(HttpServletRequest request,HttpServletResponse response, @RequestParam("appointOrderId") String appointOrderId) {
    Long shopId = WebUtil.getShopId(request);
    PrintWriter out = null;
    try {
    if(shopId == null) {
      throw new Exception("shopId can not be null");
    }
    if(StringUtil.isEmpty(appointOrderId)) {
      throw new Exception("appointOrderId can not be null");
    }
    IPrintService printService = ServiceManager.getService(IPrintService.class);
    response.setContentType("text/html");
    response.setCharacterEncoding("UTF-8");
    out = response.getWriter();

      AppointOrderDTO appointOrderDTO = appointOrderService.getAppointOrderById(shopId,NumberUtil.longValue(appointOrderId));
      // 为空的时候不打印相应模块
      if(ArrayUtils.isEmpty(appointOrderDTO.getItemDTOs())){
        appointOrderDTO.setItemDTOs(null);
      }
      if(ArrayUtils.isEmpty(appointOrderDTO.getServiceDTOs())){
        appointOrderDTO.setServiceDTOs(null);
      }
      if(appointOrderDTO == null) {
        LOG.error("appointOrderDTO is null");
        return;
      }
      PrintTemplateDTO printTemplateDTO = printService.getSinglePrintTemplateDTOByShopIdAndType(WebUtil.getShopId(request), OrderTypes.APPOINT_ORDER);
      if(printTemplateDTO != null) {
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
        String myTemplateName = "appointOrderPrint"+String.valueOf(shopId);
        String myTemplate =  str;
        //模板资源存放 资源库 中
        repo.putStringResource(myTemplateName, myTemplate);
        //从资源库中加载模板
        Template template = ve.getTemplate(myTemplateName);
        //取得velocity的模版
        Template t = ve.getTemplate(myTemplateName,"UTF-8");
        //取得velocity的上下文context
        VelocityContext context = new VelocityContext();
        context.put("isDebug",System.getProperty("is.developer.debug"));
        context.put("appointOrderDTO",appointOrderDTO);
        //输出流
        StringWriter writer = new StringWriter();
        //转换输出
        t.merge(context, writer);
        out.print(writer);
        writer.close();
      } else {
        out.print("<html><head><title></title></head><body><h1>没有可用的模板</h1></body><html>");
      }
    } catch (Exception e) {
        LOG.error(e.getMessage(),e);
    } finally {
        out.close();
    }
  }

  @RequestMapping(params = "method=showAppointOrderList")
  public String showAppointOrderList(ModelMap model, HttpServletRequest request, HttpServletResponse response) {
    Long shopId = WebUtil.getShopId(request);
    IServiceCategoryService serviceCategoryService = ServiceManager.getService(IServiceCategoryService.class);
    try {
      Map<Long, String> serviceScopeMap = serviceCategoryService.getShopServiceCategoryIdNameMap(WebUtil.getShopId(request));
      model.addAttribute("serviceScope", serviceScopeMap);
      String scene = request.getParameter("scene");
      if(StringUtils.isEmpty(scene)){
        scene = "";
      }
      model.addAttribute("scene",scene);
    } catch (Exception e) {
      LOG.error("appoint.do?method=showAppointOrderList,shopId:{}" + e.getMessage(), shopId, e);
    }
    return SHOW_SEARCH_PAGE;
  }

  @RequestMapping(params = "method=searchAppointOrder")
  @ResponseBody
  public Object searchAppointOrder(ModelMap model, HttpServletRequest request, HttpServletResponse response,
                                   AppointOrderSearchCondition searchCondition) {
    Long shopId = WebUtil.getShopId(request);
    PagingListResult<AppointOrderDTO> result = new PagingListResult<AppointOrderDTO>();
    try {
      searchCondition.setShopId(WebUtil.getShopId(request));
      if(searchCondition.isClientNewOrderScene()){
        searchCondition.setAppointOrderStatus(new AppointOrderStatus[]{AppointOrderStatus.PENDING});
      } else if(searchCondition.isClientHandledOrderScene()){
        searchCondition.setAppointOrderStatus(new AppointOrderStatus[]{AppointOrderStatus.ACCEPTED,AppointOrderStatus.TO_DO_REPAIR});
      } else if(searchCondition.isClientOverdueAndSoonOrderOrderScene()){
        searchCondition.setAppointOrderStatus(new AppointOrderStatus[]{AppointOrderStatus.PENDING,AppointOrderStatus.ACCEPTED});
        Long[] intervals = ConfigUtils.getOverdueAppointRemindIntervals();
        searchCondition.setAppointTimeStart(System.currentTimeMillis() + intervals[0]);
        searchCondition.setAppointTimeEnd(System.currentTimeMillis() + intervals[1]);
      }
      searchCondition.setAppUserNosFromAppUserCustomers(appUserService.getAppUserCustomersByCustomerIds(searchCondition.getCustomerIds()));
      int totalRows = appointOrderService.countAppointOrderDTOs(searchCondition);
      List<AppointOrderDTO> appointOrderDTOs = appointOrderService.searchAppointOrderDTOs(searchCondition);
      Pager pager = new Pager(totalRows, searchCondition.getStartPageNo(), searchCondition.getMaxRows());
      result.setPager(pager);
      result.setData(appointOrderDTOs);
    } catch (Exception e) {
      LOG.error("appoint.do?method=searchAppointOrder,shopId:{}" + e.getMessage(), shopId, e);
    }
    return result;
  }

  @RequestMapping(params = "method=validateDraftOrderAndAppointOrderStatus")
  @ResponseBody
  public Object validateDraftOrderAndAppointOrderStatus(HttpServletRequest request,HttpServletResponse response,
                                                        @RequestParam("appointOrderId") String appointOrderId) {
    Long shopId = WebUtil.getShopId(request);
    Result result = new Result();
    IDraftOrderService draftOrderService = ServiceManager.getService(IDraftOrderService.class);
    try {
      if(shopId == null) {
        throw new Exception("shopId can not be null");
      }
      if(StringUtil.isEmpty(appointOrderId)) {
        throw new Exception("appointOrderId can not be null");
      }
      AppointOrderDTO appointOrderDTO = appointOrderService.getAppointOrderById(shopId,NumberUtil.longValue(appointOrderId));
      if(appointOrderDTO == null) {
        result.setMsg(false,"没有找到此预约单");
        return result;
      }
      if(AppointOrderStatus.TO_DO_REPAIR.equals(appointOrderDTO.getStatus())) {
        if(appointOrderDTO.getOrderId() != null) {
          DraftOrder draftOrder = draftOrderService.lazyLoadDraftOrderId(shopId, appointOrderDTO.getOrderId());
          if(draftOrder != null && DraftOrderStatus.DRAFT_REPEAL.equals(draftOrder.getStatus())) {
            result.setMsg(false,"草稿单已被删除，请重新做单");
          }
        }
      }
      result.setData(appointOrderDTO);
      return result;
    } catch (Exception e) {
      LOG.error(e.getMessage(),e);
      result.setMsg(false,e.getMessage());
      return result;
    }

  }

  @RequestMapping(params = "method=validateAcceptAppointOrder")
  @ResponseBody
  public Object validateAcceptAppointOrder(ModelMap model, HttpServletRequest request, HttpServletResponse response, AppointOrderDTO appointOrderDTO) {
    Long shopId = WebUtil.getShopId(request);
    Result result = new Result();
    try {
      generateBaseAppointOrderDTO(model, request, appointOrderDTO);
      result = appointOrderService.validateAcceptAppointOrder(appointOrderDTO);
    } catch (Exception e) {
      LOG.error("appoint.do?method=validateUpdateAppointOrder,shopId:{}" + e.getMessage(), shopId, e);
    }
    return result;
  }

  @RequestMapping(params = "method=acceptAppointOrder")
  public String acceptAppointOrder(ModelMap model, HttpServletRequest request, HttpServletResponse response, AppointOrderDTO appointOrderDTO) {
    Long shopId = WebUtil.getShopId(request);
    try {
      generateBaseAppointOrderDTO(model, request, appointOrderDTO);
      Result result = appointOrderService.validateAcceptAppointOrder(appointOrderDTO);
      if (result != null && !result.isSuccess()) {
        generateServiceScope(model, request);
        return APPOINT_ORDER_DETAIL;
      }
      appointOrderService.handleAcceptAppointOrder(appointOrderDTO);
      model.addAttribute("appointOrderId", appointOrderDTO.getId());
      return REDIRECT_SHOW;
    } catch (Exception e) {
      LOG.error("appoint.do?method=acceptAppointOrder,shopId:{}" + e.getMessage(), shopId, e);
    }
    return SHOW_EDIT_PAGE;
  }

  @RequestMapping(params = "method=validateRefuseAppointOrder")
  @ResponseBody
  public Object validateRefuseAppointOrder(ModelMap model, HttpServletRequest request, HttpServletResponse response, AppointOrderDTO appointOrderDTO) {
    Long shopId = WebUtil.getShopId(request);
    Result result = new Result();
    try {
      generateBaseAppointOrderDTO(model, request, appointOrderDTO);
      result = appointOrderService.validateRefuseAppointOrder(appointOrderDTO);
    } catch (Exception e) {
      LOG.error("appoint.do?method=validateRefuseAppointOrder,shopId:{}" + e.getMessage(), shopId, e);
    }
    return result;
  }

  @RequestMapping(params = "method=refuseAppointOrder")
  public String refuseAppointOrder(ModelMap model, HttpServletRequest request, HttpServletResponse response, AppointOrderDTO appointOrderDTO) {
    Long shopId = WebUtil.getShopId(request);
    try {
      generateBaseAppointOrderDTO(model, request, appointOrderDTO);
      Result result = appointOrderService.validateRefuseAppointOrder(appointOrderDTO);
      if (result != null && !result.isSuccess()) {
        generateServiceScope(model, request);
        return APPOINT_ORDER_DETAIL;
      }
      appointOrderService.handleRefuseAppointOrder(appointOrderDTO);
      model.addAttribute("appointOrderId", appointOrderDTO.getId());
      return REDIRECT_SHOW;
    } catch (Exception e) {
      LOG.error("appoint.do?method=acceptAppointOrder,shopId:{}" + e.getMessage(), shopId, e);
    }
    return SHOW_EDIT_PAGE;
  }


  @RequestMapping(params = "method=validateCancelAppointOrder")
  @ResponseBody
  public Object validateCancelAppointOrder(ModelMap model, HttpServletRequest request, HttpServletResponse response, AppointOrderDTO appointOrderDTO) {
    Long shopId = WebUtil.getShopId(request);
    Result result = new Result();
    try {
      generateBaseAppointOrderDTO(model, request, appointOrderDTO);
      result = appointOrderService.validateCancelAppointOrder(appointOrderDTO);
    } catch (Exception e) {
      LOG.error("appoint.do?method=validateRefuseAppointOrder,shopId:{}" + e.getMessage(), shopId, e);
    }
    return result;
  }

  @RequestMapping(params = "method=cancelAppointOrder")
  public String cancelAppointOrder(ModelMap model, HttpServletRequest request, HttpServletResponse response, AppointOrderDTO appointOrderDTO) {
    Long shopId = WebUtil.getShopId(request);
    try {
      generateBaseAppointOrderDTO(model, request, appointOrderDTO);
      Result result = appointOrderService.validateCancelAppointOrder(appointOrderDTO);
      if (result != null && !result.isSuccess()) {
        generateServiceScope(model, request);
        return APPOINT_ORDER_DETAIL;
      }
      appointOrderService.handleCancelAppointOrder(appointOrderDTO);
      model.addAttribute("appointOrderId", appointOrderDTO.getId());
      return REDIRECT_SHOW;
    } catch (Exception e) {
      LOG.error("appoint.do?method=acceptAppointOrder,shopId:{}" + e.getMessage(), shopId, e);
    }
    return SHOW_EDIT_PAGE;
  }

  /**
   * 预约单生成其他单据时的校验，生成洗车单不需要校验，生成施工单的时候，施工单会有同一车牌号只有一张施工中的约束
   * @param model
   * @param request
   * @param response
   * @param appointOrderDTO
   * @return
   */
  @RequestMapping(params = "method=validateCreateOtherOrder")
  @ResponseBody
  public Object validateCreateOtherOrder(ModelMap model, HttpServletRequest request, HttpServletResponse response, AppointOrderDTO appointOrderDTO) {
    Long shopId = WebUtil.getShopId(request);
    Result result = new Result();
    try {
      generateBaseAppointOrderDTO(model, request, appointOrderDTO);
      result = appointOrderService.validateCreateOtherOrder(appointOrderDTO);
    } catch (Exception e) {
      LOG.error("appoint.do?method=validateCreateOtherOrder,shopId:{}" + e.getMessage(), shopId, e);
    }
    return result;
  }

  /**
   * 1.车牌号存在的时候根据车牌号带出车辆信息
   * 2.车牌号不存在，根据客户id带出客户信息
   * 3.客户Id不存在的时候，根据客户手机号带出客户信息
   * @param model
   * @param request
   * @param response
   * @param appointOrderDTO
   * @return
   */

  @RequestMapping(params = "method=createOtherOrder")
  public String createOtherOrder(ModelMap model, HttpServletRequest request, HttpServletResponse response, AppointOrderDTO appointOrderDTO) {
    Long shopId = WebUtil.getShopId(request);
    Result result = new Result();
    try {
      generateBaseAppointOrderDTO(model, request, appointOrderDTO);
      result = appointOrderService.validateCreateOtherOrder(appointOrderDTO);
      if (result != null && !result.isSuccess()) {
        model.addAttribute("appointOrderId", appointOrderDTO.getId());
        return REDIRECT_SHOW;
      }
      appointOrderDTO = appointOrderService.getAppointOrderById(shopId,appointOrderDTO.getId());
      if(appointOrderService.isCreateWashBeauty(appointOrderDTO)){
       return createWashBeautyDTO(appointOrderDTO, model);
      }else {
        return createRepairOrderDTO(appointOrderDTO, model,request);
      }
    } catch (Exception e) {
      LOG.error("appoint.do?method=createOtherOrder,shopId:{}" + e.getMessage(), shopId, e);
    }
    return "";
  }

  private String createWashBeautyDTO(AppointOrderDTO appointOrderDTO,ModelMap model) throws Exception {
    IUserService userService = ServiceManager.getService(IUserService.class);
    IMembersService membersService = ServiceManager.getService(IMembersService.class);
    RFITxnService rfiTxnService = ServiceManager.getService(RFITxnService.class);
    VehicleDTO vehicleDTO = null;
    CustomerDTO customerDTO = null;
    vehicleDTO = CollectionUtil.getFirst(userService.getVehicleByLicenceNo(appointOrderDTO.getShopId(), appointOrderDTO.getVehicleNo()));
    if (vehicleDTO != null && vehicleDTO.getId() != null) {
      //生成其他单据的时候，如果车辆品牌型都没有，用预约单里的赋值
      if(StringUtils.isEmpty(vehicleDTO.getBrand()) && StringUtils.isEmpty(vehicleDTO.getModel()) ){
        if(StringUtils.isNotBlank(appointOrderDTO.getVehicleBrand())){
          vehicleDTO.setBrand(appointOrderDTO.getVehicleBrand());
          if(StringUtils.isNotBlank(appointOrderDTO.getVehicleModel())){
            vehicleDTO.setModel(appointOrderDTO.getVehicleModel());
          }
        }
      }
      //车主电话没有的话，用预约单上车辆联系人电话
      if(StringUtils.isEmpty(vehicleDTO.getMobile()) && StringUtils.isNotBlank(appointOrderDTO.getVehicleMobile())){
        vehicleDTO.setMobile(appointOrderDTO.getVehicleMobile());
      }

      CustomerVehicleDTO customerVehicleDTO = CollectionUtil.getFirst(userService.getCustomerVehicleByVehicleId(vehicleDTO.getId()));
      if (customerVehicleDTO != null && customerVehicleDTO.getCustomerId() != null) {
        customerDTO = userService.getCustomerDTOByCustomerId(customerVehicleDTO.getCustomerId(), appointOrderDTO.getShopId());
      }
    } else {
      if (appointOrderDTO.getCustomerId() != null) {
        customerDTO = userService.getCustomerDTOByCustomerId(appointOrderDTO.getCustomerId(), appointOrderDTO.getShopId());
      }
      if (customerDTO == null && StringUtils.isNotBlank(appointOrderDTO.getCustomerMobile())) {
        customerDTO = CollectionUtil.getFirst(userService.getCustomerByMobile(appointOrderDTO.getShopId(), appointOrderDTO.getCustomerMobile()));
      }
    }

    if (vehicleDTO == null) {
      vehicleDTO = new VehicleDTO();
    }
    if (vehicleDTO.getId() == null) {
      vehicleDTO = new VehicleDTO();
      vehicleDTO.fromAppointOrderDTO(appointOrderDTO);
    }
    if (customerDTO == null) {
      customerDTO = new CustomerDTO();
    }
    if (customerDTO.getId() == null) {
      customerDTO.fromAppointOrderDTO(appointOrderDTO);
    }

    WashBeautyOrderDTO washBeautyOrderDTO = new WashBeautyOrderDTO();
    washBeautyOrderDTO.setVestDateStr(DateUtil.getNowTimeStr(DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN));
    washBeautyOrderDTO.setAppointOrderId(appointOrderDTO.getId());
    washBeautyOrderDTO.setCustomerDTO(customerDTO);
    washBeautyOrderDTO.setVehicleDTO(vehicleDTO);
    CustomerRecordDTO customerRecordDTO = null;
    if (customerDTO != null && customerDTO.getId() != null) {
      customerRecordDTO = userService.getCustomerRecordDTOByCustomerIdAndShopId(appointOrderDTO.getShopId(), customerDTO.getId());
      MemberDTO memberDTO = membersService.getMemberByCustomerId(appointOrderDTO.getShopId(), customerDTO.getId());
      if (null != memberDTO) {
        memberDTO.setStatus(membersService.getMemberStatusByMemberDTO(memberDTO));
        memberDTO.setStatusStr(memberDTO.getStatus().getStatus());
      }
      washBeautyOrderDTO.setMemberDTO(memberDTO);
    }
    if (customerRecordDTO != null) {
      washBeautyOrderDTO.setTotalReturnDebt(NumberUtil.numberValue(customerRecordDTO.getTotalPayable(), 0D));
    } else {
      washBeautyOrderDTO.setTotalReturnDebt(0D);
    }
    if (washBeautyOrderDTO.getMemberDTO() != null && washBeautyOrderDTO.getMemberDTO().getMemberServiceDTOs() != null) {
      for (MemberServiceDTO memberServiceDTO : washBeautyOrderDTO.getMemberDTO().getMemberServiceDTOs()) {
        Service service = rfiTxnService.getServiceById(memberServiceDTO.getServiceId());
        if (service != null) {
          memberServiceDTO.setServiceName(service.getName());
        }
      }
    }


    washBeautyOrderDTO.setServiceDTOs(rfiTxnService.getServiceByWashBeauty(appointOrderDTO.getShopId(), washBeautyOrderDTO.getMemberDTO()));
    if (washBeautyOrderDTO.getServiceDTOs() == null) {
      ServiceDTO[] serviceDTOs = new ServiceDTO[1];
      serviceDTOs[0] = new ServiceDTO();
      serviceDTOs[0].setName("无服务");
      washBeautyOrderDTO.setServiceDTOs(serviceDTOs);
    }

    appointOrderService.generateCreateWashBeautyOrderItem(washBeautyOrderDTO, appointOrderDTO);
//    WashBeautyOrderItemDTO[] washBeautyOrderItemDTOs = new WashBeautyOrderItemDTO[1];
//    washBeautyOrderItemDTOs[0] = new WashBeautyOrderItemDTO();
//    washBeautyOrderItemDTOs[0].setSurplusTimes(washBeautyOrderDTO.getServiceDTOs()[0].getSurplusTimes());
//    washBeautyOrderItemDTOs[0].setPrice(washBeautyOrderDTO.getServiceDTOs()[0].getPrice());
//    washBeautyOrderDTO.setWashBeautyOrderItemDTOs(washBeautyOrderItemDTOs);
//    washBeautyOrderDTO.setTotal(washBeautyOrderDTO.getServiceDTOs()[0].getPrice());
    washBeautyOrderDTO.setSalesManDTOs(userService.getSalesManList(appointOrderDTO.getShopId()));
    //<--  from washBeautyController .totalDebt(),.totalConsume()
    double totalDebt = 0;
    Double totalConsume = 0d;
    if (customerRecordDTO != null) {
      totalDebt = customerRecordDTO.getTotalReceivable();
      totalConsume = customerRecordDTO.getTotalAmount();
    }
    model.addAttribute("washBeautyOrderDTO", washBeautyOrderDTO);
    model.addAttribute("totalDebt", NumberUtil.round(totalDebt, NumberUtil.MONEY_PRECISION));
    model.addAttribute("totalDebt", NumberUtil.round(totalConsume, NumberUtil.MONEY_PRECISION));
    model.addAttribute("createFromFlag","appointOrder");
    // end -->
    return "/txn/carWash";
  }

  private String createRepairOrderDTO(AppointOrderDTO appointOrderDTO, ModelMap model, HttpServletRequest request) throws Exception {
    IUserService userService = ServiceManager.getService(IUserService.class);
    IMembersService membersService = ServiceManager.getService(IMembersService.class);
    RFITxnService rfiTxnService = ServiceManager.getService(RFITxnService.class);
    VehicleDTO vehicleDTO = null;
    CustomerDTO customerDTO = null;
    vehicleDTO = CollectionUtil.getFirst(userService.getVehicleByLicenceNo(appointOrderDTO.getShopId(), appointOrderDTO.getVehicleNo()));
    if (vehicleDTO != null && vehicleDTO.getId() != null) {

      //生成其他单据的时候，如果车辆品牌型都没有，用预约单里的赋值
      if(StringUtils.isEmpty(vehicleDTO.getBrand()) && StringUtils.isEmpty(vehicleDTO.getModel()) ){
        if(StringUtils.isNotBlank(appointOrderDTO.getVehicleBrand())){
          vehicleDTO.setBrand(appointOrderDTO.getVehicleBrand());
          if(StringUtils.isNotBlank(appointOrderDTO.getVehicleModel())){
            vehicleDTO.setModel(appointOrderDTO.getVehicleModel());
          }
        }
      }
      //车主电话没有的话，用预约单上车辆联系人电话
      if(StringUtils.isEmpty(vehicleDTO.getMobile()) && StringUtils.isNotBlank(appointOrderDTO.getVehicleMobile())){
        vehicleDTO.setMobile(appointOrderDTO.getVehicleMobile());
      }

      CustomerVehicleDTO customerVehicleDTO = CollectionUtil.getFirst(userService.getCustomerVehicleByVehicleId(vehicleDTO.getId()));
      if (customerVehicleDTO != null && customerVehicleDTO.getCustomerId() != null) {
        customerDTO = userService.getCustomerDTOByCustomerId(customerVehicleDTO.getCustomerId(), appointOrderDTO.getShopId());
      }
    } else {
      if (appointOrderDTO.getCustomerId() != null) {
        customerDTO = userService.getCustomerDTOByCustomerId(appointOrderDTO.getCustomerId(), appointOrderDTO.getShopId());
      }
      if (customerDTO == null && StringUtils.isNotBlank(appointOrderDTO.getCustomerMobile())) {
        customerDTO = CollectionUtil.getFirst(userService.getCustomerByMobile(appointOrderDTO.getShopId(), appointOrderDTO.getCustomerMobile()));
      }
    }

    if (vehicleDTO == null) {
      vehicleDTO = new VehicleDTO();
    }
    if (vehicleDTO.getId() == null) {
      vehicleDTO = new VehicleDTO();
      vehicleDTO.fromAppointOrderDTO(appointOrderDTO);
    }
    if (customerDTO == null) {
      customerDTO = new CustomerDTO();
    }
    if (customerDTO.getId() == null) {
      customerDTO.fromAppointOrderDTO(appointOrderDTO);
    }
    RepairOrderDTO repairOrderDTO = new RepairOrderDTO();
    repairOrderDTO.setShopId(WebUtil.getShopId(request));
    repairOrderDTO.setAppointOrderId(appointOrderDTO.getId());
    repairOrderDTO.setCustomerDTO(customerDTO);
    repairOrderDTO.setVehicleDTO(vehicleDTO);
    repairOrderDTO.setStartDateStr(DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN, System.currentTimeMillis()));
    repairOrderDTO.setStartDate(System.currentTimeMillis());
    repairOrderDTO.setServiceType(OrderTypes.REPAIR);
    repairOrderDTO.setShopVersionId(WebUtil.getShopVersionId(request));
    CustomerRecordDTO customerRecordDTO = null;
    if (customerDTO != null && customerDTO.getId() != null) {
      customerRecordDTO = userService.getCustomerRecordDTOByCustomerIdAndShopId(appointOrderDTO.getShopId(), customerDTO.getId());
      MemberDTO memberDTO = membersService.getMemberByCustomerId(appointOrderDTO.getShopId(), customerDTO.getId());
      if (null != memberDTO) {
        repairOrderDTO.setMemberNo(memberDTO.getMemberNo());
        repairOrderDTO.setMemberStatus(membersService.getMemberStatusByMemberDTO(memberDTO).getStatus());
        repairOrderDTO.setMemberType(memberDTO.getType());
        repairOrderDTO.setMemberRemainAmount(memberDTO.getBalance());
      }
    }
    if (customerRecordDTO != null) {
      repairOrderDTO.setTotalReturnDebt(NumberUtil.numberValue(customerRecordDTO.getTotalPayable(), 0D));
      double totalDebt = 0;
      double totalConsume = 0.0;
      double totalReturnDebt = 0.0;
      totalDebt = customerRecordDTO.getTotalReceivable();
      totalConsume = customerRecordDTO.getTotalAmount();
      totalReturnDebt = NumberUtil.numberValue(customerRecordDTO.getTotalPayable(), 0D);
      model.addAttribute("totalDebt", NumberUtil.round(totalDebt, NumberUtil.MONEY_PRECISION));
      model.addAttribute("totalConsume", NumberUtil.round(totalConsume, NumberUtil.MONEY_PRECISION));
      model.addAttribute("totalReturnDebt", NumberUtil.round(totalReturnDebt, NumberUtil.MONEY_PRECISION));
    }

    if (BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(WebUtil.getShopVersionId(request))) {
      IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
      List<StoreHouseDTO> storeHouseDTOList = storeHouseService.getAllStoreHousesByShopId(appointOrderDTO.getShopId());
      model.addAttribute("storeHouseDTOList", storeHouseDTOList);//select 选项
      if (CollectionUtils.isNotEmpty(storeHouseDTOList) && repairOrderDTO.getStorehouseId() == null) {
        if (storeHouseDTOList.size() == 1) {
          repairOrderDTO.setStorehouseId(storeHouseDTOList.get(0).getId());
        }
      }
    }
    appointOrderService.generateCreateRepairOrderItem(repairOrderDTO,appointOrderDTO);

    model.addAttribute("repairOrderDTO", repairOrderDTO);
    //主要内容
    Map serviceTypeList = OrderTypes.getServicesLocaleMap(request.getLocale());
    model.addAttribute("serviceTypeList", serviceTypeList);
    //剩余油量
    Map fuelNumberList = TxnConstant.getFuelNumberMap(request.getLocale());
    model.addAttribute("fuelNumberList", fuelNumberList);
    return "/txn/invoicing";

  }

  @RequestMapping(params = "method=createAppointOrderByCustomerIdAndAppUserNo")
  public String createAppointOrderByCustomerIdAndAppUserNo(ModelMap model, HttpServletRequest request, HttpServletResponse response,
                                                 String customerId,String appUserNo) {
    Long shopId = WebUtil.getShopId(request);
    AppointOrderDTO appointOrderDTO = new AppointOrderDTO();
    try {
      appointOrderDTO = appointOrderService.generateAppointOrderByCustomerId(Long.valueOf(customerId),shopId,appUserNo);
      appointOrderDTO.setAssistantMan(WebUtil.getUserName(request));

      model.addAttribute("appointOrderDTO", appointOrderDTO);
      model.addAttribute("createFromFlag", "customerDetail");
      if (appointOrderDTO.getCustomerId() != null) {
        IVehicleService vehicleService = ServiceManager.getService(IVehicleService.class);
        List<VehicleDTO> vehicleDTOs = vehicleService.getVehicleListByCustomerId(appointOrderDTO.getCustomerId());
        model.addAttribute("vehicleDTOs", vehicleDTOs);
      }
      generateServiceScope(model, request);
      request.setAttribute("customerTypeMap", TxnConstant.getCustomerTypeMap(request.getLocale()));
    } catch (Exception e) {
      LOG.error("appoint.do?method=createAppointOrderByCustomerInfo,shopId:{}" + e.getMessage(), shopId, e);
    }
    return SHOW_EDIT_PAGE;
  }

  @RequestMapping(params = "method=createAppointOrderByAppUserNo")
  public String createAppointOrderByAppUserNo(ModelMap model, HttpServletRequest request, HttpServletResponse response,
                                                           String appUserNo) {
    Long shopId = WebUtil.getShopId(request);
    try {
      AppointOrderDTO appointOrderDTO = appointOrderService.generateAppointOrderByAppUserNo(shopId,appUserNo);
      appointOrderDTO.setAssistantMan(WebUtil.getUserName(request));

      model.addAttribute("appointOrderDTO", appointOrderDTO);
      model.addAttribute("createFromFlag", "customerDetail");
      if (appointOrderDTO.getCustomerId() != null) {
        IVehicleService vehicleService = ServiceManager.getService(IVehicleService.class);
        List<VehicleDTO> vehicleDTOs = vehicleService.getVehicleListByCustomerId(appointOrderDTO.getCustomerId());
        model.addAttribute("vehicleDTOs", vehicleDTOs);
      }
      generateServiceScope(model, request);
      request.setAttribute("customerTypeMap", TxnConstant.getCustomerTypeMap(request.getLocale()));
    } catch (Exception e) {
      LOG.error("appoint.do?method=createAppointOrderByCustomerInfo,shopId:{}" + e.getMessage(), shopId, e);
    }
    return SHOW_EDIT_PAGE;
  }

}