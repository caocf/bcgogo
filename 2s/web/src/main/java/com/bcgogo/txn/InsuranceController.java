package com.bcgogo.txn;

import com.bcgogo.common.Pager;
import com.bcgogo.common.PagingListResult;
import com.bcgogo.common.Result;
import com.bcgogo.common.WebUtil;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.enums.InsuranceValidateScene;
import com.bcgogo.enums.OrderStatus;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.AppointOrderDTO;
import com.bcgogo.txn.dto.InsuranceOrderDTO;
import com.bcgogo.txn.dto.PrintTemplateDTO;
import com.bcgogo.txn.dto.RepairOrderDTO;
import com.bcgogo.txn.model.InsuranceOrder;
import com.bcgogo.txn.service.IPrintService;
import com.bcgogo.txn.service.ITxnService;
import com.bcgogo.user.dto.InsuranceCompanyDTO;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.TxnConstant;
import org.apache.commons.collections.CollectionUtils;
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
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-1-7
 * Time: 下午1:34
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/insurance.do")
public class InsuranceController extends AbstractTxnController {
  private static final Logger LOG = LoggerFactory.getLogger(InsuranceController.class);
  private static final String INSURANCE_INFO = "/txn/insuranceInfo";
  private static final String REPAIR_ORDER_INFO = "/txn/invoicing";
  private static final String INSURANCE_LIST = "/txn/insuranceList";

  @RequestMapping(params = "method=createInsuranceOrder")
  public String createInsuranceOrder(ModelMap model, HttpServletRequest request, HttpServletResponse response) {
    try {
      InsuranceOrderDTO insuranceOrderDTO = new InsuranceOrderDTO();
      insuranceOrderDTO.setStatus(OrderStatus.UNSETTLED);
      String dealingType=request.getParameter("dealingType");
      Long shopId = WebUtil.getShopId(request);
      Long repairOrderId = NumberUtil.toLong(request.getParameter("repairOrderId"));
      Long repairDraftOrderId = NumberUtil.toLong(request.getParameter("repairDraftOrderId"));
      Long insuranceOrderId2 = NumberUtil.toLong(request.getParameter("insuranceOrderId"));

      if (repairOrderId != null) {
        Long insuranceOrderId = insuranceService.getInsuranceOrderIdByRepairOrderId(shopId, repairOrderId);
        if (insuranceOrderId != null) {
            model.addAttribute("dealingType",dealingType);
          return "redirect:insurance.do?method=showInsuranceOrder&insuranceOrderId=" + insuranceOrderId;
        }
        insuranceOrderDTO = insuranceService.createInsuranceOrderDTOByRepairOrderId(shopId,WebUtil.getShopVersionId(request), repairOrderId);
      } else if(repairDraftOrderId != null) {
        InsuranceOrderDTO insuranceOrder = insuranceService.getInsuranceOrderByRepairDraftOrderId(shopId, repairDraftOrderId);
        if(insuranceOrder != null) {
            model.addAttribute("dealingType",dealingType);
          return "redirect:insurance.do?method=showInsuranceOrder&insuranceOrderId=" + insuranceOrder.getId();
        }
      } else if(insuranceOrderId2 != null) {
          model.addAttribute("dealingType",dealingType);
        return "redirect:insurance.do?method=showInsuranceOrder&insuranceOrderId=" + insuranceOrderId2;
      }
      insuranceOrderDTO.setShopId(shopId);
      insuranceOrderDTO.setEditor(WebUtil.getUserName(request));
      insuranceOrderDTO.setEditorId(WebUtil.getUserId(request));


      model.addAttribute("insuranceOrderDTO", insuranceOrderDTO);
      List<InsuranceCompanyDTO> insuranceCompanyDTOs = userService.getAllInsuranceCompanyDTOs();
      if(CollectionUtils.isNotEmpty(insuranceCompanyDTOs)) {
        insuranceOrderDTO.setInsuranceCompanyDTO(insuranceCompanyDTOs.get(0));
      }
      model.addAttribute("insuranceCompanyDTOs", insuranceCompanyDTOs);
        if(insuranceOrderDTO.getStatusStr()!=null){
            if(insuranceOrderDTO.getStatusStr().equals("待结算")){
                return INSURANCE_INFO;
            }else{
                model.addAttribute("dealingType",insuranceOrderDTO.getStatusStr());
                return "/txn/insuranceDealing";
            }
        }else{
            return INSURANCE_INFO;
        }


    } catch (Exception e) {
      LOG.error("method=createInsuranceOrder;" + e.getMessage(), e);
      return INSURANCE_INFO;
    }
  }

  @RequestMapping(params = "method=showInsuranceOrderList")
  public String showInsuranceOrderList(ModelMap model, HttpServletRequest request, HttpServletResponse response,
                                       InsuranceOrderDTO searchCondition) throws Exception {
    try {
        Long shopId = WebUtil.getShopId(request);
        if (searchCondition == null) {
            searchCondition = new InsuranceOrderDTO();
        }
        searchCondition.setShopId(shopId);
      List<InsuranceCompanyDTO> insuranceCompanyDTOs = userService.getAllInsuranceCompanyDTOs();
      Double totalClaims = insuranceService.sumInsuranceOrderClaims(searchCondition);
      Integer totalAmount = insuranceService.sumInsuranceOrderDTOs(shopId);
      model.addAttribute("totalClaims", String.valueOf(NumberUtil.round(totalClaims, 2)));
      model.addAttribute("insuranceCompanyDTOs", insuranceCompanyDTOs);
      model.addAttribute("searchCondition",  new InsuranceOrderDTO());
      model.addAttribute("totalAmount", totalAmount);
      return INSURANCE_LIST;
    } catch (Exception e) {
      LOG.error("method=showInsuranceOrderList;searchCondition{}" + e.getMessage(), searchCondition, e);
      return INSURANCE_LIST;
    }
  }


    @RequestMapping(params = "method=searchInsuranceOrderData")
    @ResponseBody
    public Object searchInsuranceOrderData(ModelMap model, HttpServletRequest request, HttpServletResponse response,
                                                 InsuranceOrderDTO searchCondition){

        PagingListResult<InsuranceOrderDTO> result = new PagingListResult<InsuranceOrderDTO>();
        try {
            Long shopId = WebUtil.getShopId(request);
            if (searchCondition == null) {
                searchCondition = new InsuranceOrderDTO();
            }
            String sortStr=request.getParameter("sortStr");
            if(sortStr!=null){
                searchCondition.setSortStatus(sortStr.trim());
            }
            searchCondition.setShopId(shopId);
            Double totalClaims = insuranceService.sumInsuranceOrderClaims(searchCondition);
            List<InsuranceOrderDTO> insuranceOrderDTOs = insuranceService.getInsuranceOrderDTOs(searchCondition);
            int totalRows = insuranceService.countInsuranceOrderDTOs(searchCondition);
            Pager pager = new Pager(totalRows, searchCondition.getStartPageNo(), searchCondition.getPageSize());
            result.setPager(pager);
            result.setResults(insuranceOrderDTOs);
            result.setData(totalClaims);



        } catch (Exception e) {
            LOG.error("method=showInsuranceOrderList;searchCondition{}" + e.getMessage(), searchCondition, e);

        }

        return result;



    }



  @RequestMapping(params = "method=showInsuranceOrder")
  public String showInsuranceOrder(ModelMap model, HttpServletRequest request, HttpServletResponse response, Long insuranceOrderId) {
    try {
      Long shopId = WebUtil.getShopId(request);
      if(shopId == null || insuranceOrderId == null){
        return createInsuranceOrder(model,request,response);
      }

      String dealingType=request.getParameter("dealingType");
      if(dealingType!=null){
          model.addAttribute("dealingType",dealingType);
      }


      InsuranceOrderDTO insuranceOrderDTO = insuranceService.getInsuranceOrderDTOById(insuranceOrderId,shopId);
      if(insuranceOrderDTO != null){
        model.addAttribute("insuranceOrderDTO",insuranceOrderDTO);
        List<InsuranceCompanyDTO> insuranceCompanyDTOs = userService.getAllInsuranceCompanyDTOs();
        model.addAttribute("insuranceCompanyDTOs", insuranceCompanyDTOs);

        if(insuranceOrderDTO.getStatusStr().equals("待结算")){
            return INSURANCE_INFO;
        }else{
            model.addAttribute("dealingType",insuranceOrderDTO.getStatusStr());
            return "/txn/insuranceDealing";
        }

      }else {
        return createInsuranceOrder(model,request,response);
      }
    } catch (Exception e) {
      LOG.error("method=showInsuranceOrder;insuranceOrderId:{}" + e.getMessage(), insuranceOrderId, e);
      return createInsuranceOrder(model, request, response);
    }
  }

  @RequestMapping(params = "method=printInsuranceOrder")
  public void printInsuranceOrder(ModelMap model, HttpServletRequest request, HttpServletResponse response, Long insuranceOrderId) {

    Long shopId = WebUtil.getShopId(request);
    if(shopId == null || insuranceOrderId == null){
      return;
    }
    InsuranceOrderDTO insuranceOrderDTO = insuranceService.getInsuranceOrderDTOById(insuranceOrderId,shopId);
    if(null == insuranceOrderDTO)
    {
      return;
    }

    IConfigService configService = ServiceManager.getService(IConfigService.class);
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    IPrintService printService = ServiceManager.getService(IPrintService.class);
    ShopDTO shopDTO = configService.getShopById(shopId);
    try{
      PrintTemplateDTO printTemplateDTO = printService.getSinglePrintTemplateDTOByShopIdAndType(WebUtil.getShopId(request), OrderTypes.INSURANCE);

      PrintWriter out = response.getWriter();
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

        String myTemplateName = "INSURANCE"+ String.valueOf(WebUtil.getShopId(request));

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
        context.put("insuranceOrderDTO", insuranceOrderDTO);
        context.put("shopDTO",shopDTO);

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
      LOG.debug("method=printInnerReturn");
      LOG.error(e.getMessage(),e);
    }
  }

    @RequestMapping(params = "method=printInsuranceOrderPreview")
    public void printInsuranceOrderPreview(ModelMap model, HttpServletRequest request, HttpServletResponse response, Long insuranceOrderId) {

        Long shopId = WebUtil.getShopId(request);

        if(shopId == null || insuranceOrderId == null){
            return;
        }
        InsuranceOrderDTO insuranceOrderDTO = insuranceService.getInsuranceOrderDTOById(insuranceOrderId,shopId);
        if(null == insuranceOrderDTO)
        {
            return;
        }

        IConfigService configService = ServiceManager.getService(IConfigService.class);
        ITxnService txnService = ServiceManager.getService(ITxnService.class);
        IPrintService printService = ServiceManager.getService(IPrintService.class);
        ShopDTO shopDTO = configService.getShopById(shopId);
        try{
            PrintTemplateDTO printTemplateDTO = printService.getSinglePrintTemplateDTOByShopIdAndType(WebUtil.getShopId(request), OrderTypes.INSURANCE_PREVIEW);

            PrintWriter out = response.getWriter();
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

                String myTemplateName = "INSURANCE_PREVIEW"+ String.valueOf(WebUtil.getShopId(request));

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
                context.put("insuranceOrderDTO", insuranceOrderDTO);
                context.put("shopDTO",shopDTO);
                context.put("currentTime", DateUtil.dateToStr(new Date()));


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
            LOG.debug("method=printInnerReturn");
            LOG.error(e.getMessage(),e);
        }
    }

  @RequestMapping(params = "method=saveInsuranceOrder")
  public String saveInsuranceOrder(ModelMap model, HttpServletRequest request, HttpServletResponse response,
                                   InsuranceOrderDTO insuranceOrderDTO) {
    try {
      if(insuranceOrderDTO == null){
        insuranceOrderDTO = new InsuranceOrderDTO();
      }
      String dealingType=request.getParameter("dealingType");
      String dealingTypeStr="";
      if(dealingType.equals("unsettled")){
          insuranceOrderDTO.setStatus(OrderStatus.UNSETTLED);
          dealingTypeStr="待结算";
      }
      if(dealingType.equals("settled")){
          insuranceOrderDTO.setStatus(OrderStatus.SETTLED);
          dealingTypeStr="已结算";
      }
      Long shopId = WebUtil.getShopId(request);
      insuranceOrderDTO.setShopId(shopId);
      insuranceOrderDTO.setEditor(WebUtil.getUserName(request));
      insuranceOrderDTO.setEditorId(WebUtil.getUserId(request));
      insuranceOrderDTO.initDate();

     Result result = insuranceService.validateSaveInsurance(insuranceOrderDTO, InsuranceValidateScene.CHECK_ALL.toString());
      if(result!=null && !result.isSuccess()){
        model.addAttribute("result",result);
        return INSURANCE_INFO;
      }
      //处理客户，车辆信息
      insuranceService.saveOrUpdateCustomerVehicle(insuranceOrderDTO);
      //保存更新单据
      insuranceService.saveOrUpdateInsuranceOrder(insuranceOrderDTO);
      List<InsuranceCompanyDTO> insuranceCompanyDTOs = userService.getAllInsuranceCompanyDTOs();
      model.addAttribute("insuranceCompanyDTOs", insuranceCompanyDTOs);
      model.addAttribute("result",new Result("保存成功",true));
      model.addAttribute("dealingType",dealingTypeStr);
        if(dealingType.equals("unsettled")){
            return INSURANCE_INFO;
        }else{
            return "/txn/insuranceDealing";
        }

    } catch (Exception e) {
      LOG.error("method=saveInsuranceOrder;" + e.getMessage(), e);
      return createInsuranceOrder(model,request,response);
    }
  }

    @RequestMapping(params = "method=nullifyOrSettledInsuranceOrder")
    public String nullifyOrSettledInsuranceOrder(ModelMap model, HttpServletRequest request, HttpServletResponse response,
                                                 InsuranceOrderDTO insuranceOrderDTO) {
        try{
            String dealingType=request.getParameter("dealingType");
            String dealingTypeStr="";
            if(insuranceOrderDTO == null){
                insuranceOrderDTO = new InsuranceOrderDTO();
            }
            if(dealingType.equals("nullify")){
                insuranceOrderDTO.setStatus(OrderStatus.REPEAL);
                dealingTypeStr="已作废";
            }
            if(dealingType.equals("settled")){
                insuranceOrderDTO.setStatus(OrderStatus.SETTLED);
                dealingTypeStr="已结算";
            }
            Long shopId = WebUtil.getShopId(request);
            insuranceOrderDTO.setShopId(shopId);
            insuranceOrderDTO.setEditor(WebUtil.getUserName(request));
            insuranceOrderDTO.setEditorId(WebUtil.getUserId(request));
            insuranceOrderDTO.initDate();
            insuranceService.saveOrUpdateInsuranceOrder(insuranceOrderDTO);

            model.addAttribute("insuranceOrderDTO",insuranceOrderDTO);
            List<InsuranceCompanyDTO> insuranceCompanyDTOs = userService.getAllInsuranceCompanyDTOs();
            model.addAttribute("insuranceCompanyDTOs", insuranceCompanyDTOs);
            model.addAttribute("dealingType",dealingTypeStr);
            return "/txn/insuranceDealing";
        }catch (Exception e){
            LOG.error("method=nullifyOrSettledInsuranceOrder;" + e.getMessage(), e);
            return "/txn/insuranceDealing";
        }

    }


  @RequestMapping(params = "method=createRepairOrderByInsurance")
  public String createRepairOrderByInsurance(ModelMap model, HttpServletRequest request, HttpServletResponse response,
                                             InsuranceOrderDTO insuranceOrderDTO) {
    try {
      if (insuranceOrderDTO == null) {
        insuranceOrderDTO = new InsuranceOrderDTO();
      }
        if(insuranceOrderDTO.getStatus()==null){
            insuranceOrderDTO.setStatus(OrderStatus.UNSETTLED);
        }
      Long shopId = WebUtil.getShopId(request);
      insuranceOrderDTO.setShopId(shopId);
      insuranceOrderDTO.setEditor(WebUtil.getUserName(request));
      insuranceOrderDTO.setEditorId(WebUtil.getUserId(request));
      insuranceOrderDTO.initDate();


      Result result = insuranceService.validateSaveInsurance(insuranceOrderDTO, InsuranceValidateScene.CHECK_ALL.toString());
      if (result != null && !result.isSuccess()) {
        model.addAttribute("result", result);
        return INSURANCE_INFO;
      }
      //处理客户，车辆信息
      insuranceService.saveOrUpdateCustomerVehicle(insuranceOrderDTO);
      //保存更新单据
      insuranceService.saveOrUpdateInsuranceOrder(insuranceOrderDTO);
      //生成施工单
      RepairOrderDTO repairOrderDTO = insuranceService.createRepairOrderDTO(insuranceOrderDTO);

      if(repairOrderDTO != null){
        repairOrderDTO.setEndDateStr(null);
      }

      repairService.initRepairOrderModel(repairOrderDTO, model);
      //主要内容
      Map serviceTypeList = OrderTypes.getServicesLocaleMap(request.getLocale());
      model.addAttribute("serviceTypeList", serviceTypeList);
      //剩余油量
      Map fuelNumberList = TxnConstant.getFuelNumberMap(request.getLocale());
      model.addAttribute("fuelNumberList", fuelNumberList);
      return REPAIR_ORDER_INFO;

    } catch (Exception e) {
      LOG.error("method=createRepairOrderByInsurance;" + e.getMessage(), e);
      return INSURANCE_INFO;
    }
  }


  @RequestMapping(params = "method=validateSaveInsurance")
  @ResponseBody
  public Object validateSaveInsurance(ModelMap model, HttpServletRequest request, HttpServletResponse response,
                                      InsuranceOrderDTO insuranceOrderDTO) {
    try {
      String validateScene = request.getParameter("validateScene");
      insuranceOrderDTO.setShopId(WebUtil.getShopId(request));
      Result result = insuranceService.validateSaveInsurance(insuranceOrderDTO, validateScene);
      if(result ==null){
        result  = new Result();
      }
      return result;
    } catch (Exception e) {
      LOG.error("method=validateSaveInsurance;" + e.getMessage(), e);
      return new Result("网络异常", false, Result.Operation.ALERT.getValue(), null);
    }
  }
}
