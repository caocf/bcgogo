package com.bcgogo.txn;

import com.bcgogo.api.AppUserDTO;
import com.bcgogo.api.EnquiryShopResponseDTO;
import com.bcgogo.common.Pager;
import com.bcgogo.common.PagingListResult;
import com.bcgogo.common.Result;
import com.bcgogo.common.WebUtil;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.enums.app.EnquiryShopResponseStatus;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.PrintTemplateDTO;
import com.bcgogo.txn.dto.enquiry.EnquirySearchConditionDTO;
import com.bcgogo.txn.dto.enquiry.ShopEnquiryDTO;
import com.bcgogo.txn.service.IPrintService;
import com.bcgogo.txn.service.app.IEnquiryService;
import com.bcgogo.user.service.app.IAppUserService;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.NumberUtil;
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
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-11-12
 * Time: 下午2:37
 */
@Controller
@RequestMapping("/enquiry.do")
public class ShopEnquiryController {
  private static final Logger LOG = LoggerFactory.getLogger(ShopEnquiryController.class);
  private static final String SHOW_SEARCH_PAGE = "/txn/enquiryOrder/enquiryOrderList";
  private static final String REDIRECT_SHOW = "redirect:appoint.do?method=showAppointOrderDetail";
  private static final String ENQUIRY_DETAIL = "/txn/enquiryOrder/enquiryOrderDetail";

  @RequestMapping(params = "method=showEnquiryOrderList")
  public String showEnquiryOrderList(ModelMap model, HttpServletRequest request, HttpServletResponse response) {
    Long shopId = WebUtil.getShopId(request);
    IEnquiryService enquiryService = ServiceManager.getService(IEnquiryService.class);
    try {
      //今日未报价数量，这里处理的时候用了大于等于今天凌晨时间，如果有数据是明后天的（脏数据）也归到今天
      EnquirySearchConditionDTO searchConditionDTO = new EnquirySearchConditionDTO();
      searchConditionDTO.setShopId(shopId);
      searchConditionDTO.setEnquiryTimeStart(DateUtil.getStartTimeOfToday());
      searchConditionDTO.setResponseStatuses(new EnquiryShopResponseStatus[]{EnquiryShopResponseStatus.UN_RESPONSE});
      int today_un_response_count = enquiryService.countShopEnquiryDTOs(searchConditionDTO);
      //往日未报价数量，这里处理时间用的小于等于昨夜 23：59：59.999
      searchConditionDTO.setEnquiryTimeStart(null);
      searchConditionDTO.setEnquiryTimeEnd(DateUtil.getEndTimeOfYesterday());
      searchConditionDTO.setResponseStatuses(new EnquiryShopResponseStatus[]{EnquiryShopResponseStatus.UN_RESPONSE});
      int before_today_un_response_count = enquiryService.countShopEnquiryDTOs(searchConditionDTO);
      //今日已报价数量，这里处理的时候用了大于等于今天凌晨时间，如果有数据是明后天的（脏数据）也归到今天
      searchConditionDTO.setEnquiryTimeEnd(null);
      searchConditionDTO.setEnquiryTimeStart(DateUtil.getStartTimeOfToday());
      searchConditionDTO.setResponseStatuses(new EnquiryShopResponseStatus[]{EnquiryShopResponseStatus.RESPONSE});
      int today_response_count = enquiryService.countShopEnquiryDTOs(searchConditionDTO);
      //往日为报价数量，这里处理时间用的小于等于昨夜  23：59：59.999
      searchConditionDTO.setEnquiryTimeStart(null);
      searchConditionDTO.setEnquiryTimeEnd(DateUtil.getEndTimeOfYesterday());
      searchConditionDTO.setResponseStatuses(new EnquiryShopResponseStatus[]{EnquiryShopResponseStatus.RESPONSE});
      int before_today_response_count = enquiryService.countShopEnquiryDTOs(searchConditionDTO);
      model.addAttribute("TODAY_UN_RESPONSE_COUNT", today_un_response_count);
      model.addAttribute("BEFORE_TODAY_UN_RESPONSE_COUNT", before_today_un_response_count);
      model.addAttribute("TODAY_RESPONSE_COUNT", today_response_count);
      model.addAttribute("BEFORE_TODAY_RESPONSE_COUNT", before_today_response_count);
    } catch (Exception e) {
      LOG.error("enquiry.do?method=showEnquiryOrderList,shopId:{}" + e.getMessage(), shopId, e);
    }
    return SHOW_SEARCH_PAGE;
  }

  @RequestMapping(params = "method=searchShopEnquiryList")
  @ResponseBody
  public Object searchShopEnquiryList(ModelMap model, HttpServletRequest request, HttpServletResponse response,
                                      EnquirySearchConditionDTO searchCondition) {
    Long shopId = WebUtil.getShopId(request);
    PagingListResult<ShopEnquiryDTO> result = new PagingListResult<ShopEnquiryDTO>();
    IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);
    IEnquiryService enquiryService = ServiceManager.getService(IEnquiryService.class);
    try {
      searchCondition.setShopId(WebUtil.getShopId(request));

      searchCondition.setAppUserNosFromAppUserCustomers(appUserService.getAppUserCustomersByCustomerIds(searchCondition.getCustomerIds()));
      int totalRows = enquiryService.countShopEnquiryDTOs(searchCondition);
      List<ShopEnquiryDTO> shopEnquiryDTOs = enquiryService.searchShopEnquiryDTOs(searchCondition);
      Pager pager = new Pager(totalRows, searchCondition.getStartPageNo(), searchCondition.getMaxRows());
      result.setPager(pager);
      result.setData(shopEnquiryDTOs);
    } catch (Exception e) {
      LOG.error("enquiry.do?method=searchShopEnquiryList,shopId:{}" + e.getMessage(), shopId, e);
    }
    return result;
  }

  @RequestMapping(params = "method=showEnquiryDetail")
  public String showEnquiryDetail(ModelMap model, HttpServletRequest request, HttpServletResponse response) {
    Long shopId = WebUtil.getShopId(request);
    String enquiryIdStr = request.getParameter("enquiryId");
    IEnquiryService enquiryService = ServiceManager.getService(IEnquiryService.class);
    try {
      if (StringUtils.isEmpty(enquiryIdStr)) {
        throw new Exception("appointOrderId can not be null!");
      }
      Long appointOrderId = NumberUtil.longValue(enquiryIdStr);
      ShopEnquiryDTO shopEnquiryDTO = enquiryService.getShopEnquiryDTODetail(appointOrderId, shopId);
      model.addAttribute("shopEnquiryDTO", shopEnquiryDTO);
    } catch (Exception e) {
      LOG.error("enquiry.do?method=showEnquiryDetail" + e.getMessage(), e);
    }
    return ENQUIRY_DETAIL;
  }

  @RequestMapping(params = "method=shopEnquiryResponse")
  @ResponseBody
  public Object shopEnquiryResponse(ModelMap model, HttpServletRequest request, HttpServletResponse response,
                                    EnquiryShopResponseDTO enquiryShopResponseDTO) {
    Long shopId = WebUtil.getShopId(request);
    Result result = new Result();
    IEnquiryService enquiryService = ServiceManager.getService(IEnquiryService.class);
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    try {
      ShopEnquiryDTO shopEnquiryDTO = enquiryService.getSimpleShopEnquiryDTO(enquiryShopResponseDTO.getEnquiryId(), shopId);
      result = enquiryService.validateAddResponse(shopEnquiryDTO, enquiryShopResponseDTO);
      if (result != null && result.isSuccess()) {
        ShopDTO shopDTO = configService.getShopById(shopId);
        enquiryShopResponseDTO.setShopId(shopId);
        enquiryShopResponseDTO.setShopName(shopDTO.getName());
        enquiryShopResponseDTO.setResponseTime(System.currentTimeMillis());
        enquiryShopResponseDTO.setStatus(EnquiryShopResponseStatus.RESPONSE);
        enquiryService.handelAddEnquiryShopResponse(shopEnquiryDTO, enquiryShopResponseDTO);
        result.setData(enquiryShopResponseDTO);
      }
    } catch (Exception e) {
      LOG.error("enquiry.do?method=shopEnquiryResponse,shopId:{}" + e.getMessage(), shopId, e);
    }
    return result;
  }

  @RequestMapping(params = "method=printEnquiryOrderDetail")
  public void printEnquiryOrderDetail(HttpServletRequest request, HttpServletResponse response, Long enquiryId) {
    Long shopId = WebUtil.getShopId(request);
    PrintWriter out = null;
    try {
      if (enquiryId == null) {
        LOG.error("enquiryId can not be null");
        return;
      }
      ShopEnquiryDTO shopEnquiryDTO = ServiceManager.getService(IEnquiryService.class)
          .getShopEnquiryDTODetail(enquiryId, shopId);
      IPrintService printService = ServiceManager.getService(IPrintService.class);
      response.setContentType("text/html");
      response.setCharacterEncoding("UTF-8");
      out = response.getWriter();
      if (shopEnquiryDTO == null) {
        LOG.error("shopEnquiryDTO is null");
        return;
      }
      AppUserDTO appUserDTO = ServiceManager.getService(IAppUserService.class).getAppUserByUserNo(shopEnquiryDTO.getAppUserNo(),null);
      if (appUserDTO == null) {
        LOG.error("appUserDTO is null");
        return;
      }
      PrintTemplateDTO printTemplateDTO = printService.getSinglePrintTemplateDTOByShopIdAndType(WebUtil.getShopId(request), OrderTypes.ENQUIRY_ORDER);
      if (printTemplateDTO != null) {
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
        String templateName = "enquiryOrderPrint" + String.valueOf(shopId);
        //模板资源存放 资源库 中
        repo.putStringResource(templateName, new String(printTemplateDTO.getTemplateHtml(), "UTF-8"));
        //取得velocity的模版
        Template t = ve.getTemplate(templateName, "UTF-8");
        //取得velocity的上下文context
        VelocityContext context = new VelocityContext();
        context.put("isDebug", System.getProperty("is.developer.debug"));
        context.put("shopEnquiryDTO", shopEnquiryDTO);
        context.put("appUserDTO", appUserDTO);
        context.put("today", DateUtil.convertDateLongToString(System.currentTimeMillis(), DateUtil.DATE_STRING_FORMAT_DEFAULT_TO_AFTERNOON));
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
      LOG.error(e.getMessage(), e);
    } finally {
      if (out != null) {
        out.close();
      }
    }
  }

}
