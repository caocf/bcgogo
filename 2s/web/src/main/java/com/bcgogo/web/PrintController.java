package com.bcgogo.web;

import com.bcgogo.common.Pager;
import com.bcgogo.common.PrintHelper;
import com.bcgogo.common.StringUtil;
import com.bcgogo.common.WebUtil;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.enums.ConsumeType;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.enums.assistantStat.AchievementStatType;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.AbstractTxnController;
import com.bcgogo.txn.dto.*;
import com.bcgogo.txn.dto.assistantStat.AssistantAchievementStatDTO;
import com.bcgogo.txn.dto.assistantStat.AssistantStatSearchDTO;
import com.bcgogo.txn.service.IPrintService;
import com.bcgogo.txn.service.ITxnService;
import com.bcgogo.txn.service.stat.assistantStat.IAssistantStatService;
import com.bcgogo.user.dto.MemberDTO;
import com.bcgogo.user.dto.MemberServiceDTO;
import com.bcgogo.user.service.IMembersService;
import com.bcgogo.utils.ArrayUtil;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.ShopConstant;
import com.bcgogo.utils.TxnConstant;
import org.apache.commons.collections.CollectionUtils;
import org.apache.velocity.VelocityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Jimuchen
 * Date: 13-5-6
 * Time: 下午5:50
 */
@Controller
@RequestMapping("/print.do")
public class PrintController extends AbstractTxnController {
  private static final Logger LOGGER = LoggerFactory.getLogger(PrintController.class);
  @Autowired
  IAssistantStatService assistantStatService;
  @Autowired
  IPrintService printService;

  @RequestMapping(params = "method=getTemplates")
  @ResponseBody
  public List<PrintTemplateDTO> getPrintTemplates(HttpServletRequest request, String orderType) {
    IPrintService printService = ServiceManager.getService(IPrintService.class);
    Long shopId = WebUtil.getShopId(request);
    OrderTypes orderTypes = OrderTypes.valueOf(orderType);
    try {
      List<PrintTemplateDTO> printTemplateDTOs = printService.getAllPrintTemplateDTOByShopIdAndType(shopId, orderTypes);
      return printTemplateDTOs;
    } catch (Exception e) {
      LOGGER.error(e.getMessage(), e);
      return null;
    }
  }

  @RequestMapping(params = "method=getYunPrintClientVersion")
  @ResponseBody
  public String getYunPrintClientVersion(HttpServletRequest request) {
    return ConfigUtils.getYunPrintClientVersion();
  }

  /**
   * 员工业绩统计
   *
   * @param request
   * @param response
   * @param assistantStatSearchDTO
   */
  @RequestMapping(params = "method=printAssistantStat")
  @ResponseBody
  public void printAssistantStat(HttpServletRequest request, HttpServletResponse response, AssistantStatSearchDTO assistantStatSearchDTO) {
    if (assistantStatSearchDTO == null || StringUtil.isEmpty(assistantStatSearchDTO.getAchievementStatTypeStr())) {
      return;
    }

    Long shopId = WebUtil.getShopId(request);
    if (shopId == null) {
      return;
    }
    try {
      if (AchievementStatType.ASSISTANT.getName().equals(assistantStatSearchDTO.getAchievementStatTypeStr())) {
        assistantStatSearchDTO.setAchievementStatType(AchievementStatType.ASSISTANT);
      } else if (AchievementStatType.DEPARTMENT.getName().equals(assistantStatSearchDTO.getAchievementStatTypeStr())) {
        assistantStatSearchDTO.setAchievementStatType(AchievementStatType.DEPARTMENT);
      }
      assistantStatSearchDTO.setTime();
      List<Long> resultList = assistantStatService.countAssistantStatByCondition(assistantStatSearchDTO);
      int totalNum = CollectionUtils.isEmpty(resultList) ? 0 : resultList.size();
      Pager pager = new Pager(totalNum, assistantStatSearchDTO.getStartPageNo(), assistantStatSearchDTO.getMaxRows());

      Set<Long> ids = new HashSet<Long>();
      for (int index = 0; index < assistantStatSearchDTO.getMaxRows(); index++) {
        if (index + pager.getRowStart() < resultList.size()) {
          ids.add(resultList.get(index + pager.getRowStart()));
        }
      }
      List<AssistantAchievementStatDTO> assistantAchievementStatDTOList = assistantStatService.getAssistantStatByIds(assistantStatSearchDTO, ids);
      assistantStatSearchDTO.setAssistantAchievementStatDTOList(assistantAchievementStatDTOList);

      PrintTemplateDTO printTemplateDTO = printService.getSinglePrintTemplateDTOByShopIdAndType(WebUtil.getShopId(request), OrderTypes.ASSISTENT_STAT);
      String myTemplateName = "BORROW_ORDER" + String.valueOf(WebUtil.getShopId(request));
      VelocityContext context = new VelocityContext();
      context.put("assistantStatSearchDTO", assistantStatSearchDTO);
      PrintHelper.generatePrintPage(response, printTemplateDTO.getTemplateHtml(), myTemplateName, context);
    } catch (Exception e) {
      LOGGER.error(e.getMessage(), e);
      return;
    }
  }

  /**
   * 摄像头客户端访问
   *
   * @param shopId
   * @param orderId
   * @throws Exception
   */
  @RequestMapping(params = "method=printWashBeautyOrder")
  public void printWashBeautyOrder(HttpServletRequest request, HttpServletResponse response, Long shopId, Long orderId) throws Exception {
    WashBeautyOrderDTO washBeautyOrderDTO = null;
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    IMembersService membersService = ServiceManager.getService(IMembersService.class);
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    ShopDTO shopDTO = configService.getShopById(shopId);
    String dataStr = DateUtil.convertDateLongToDateString(TxnConstant.FORMAT_STANDARD_YEAR_MONTH_DATE_HOUR_MINUTE,
      System.currentTimeMillis());
    washBeautyOrderDTO = txnService.getWashBeautyOrderDTOById(shopId, orderId);
    washBeautyOrderDTO.setVestDateStr(DateUtil.convertDateLongToDateString(DateUtil.YEAR_MONTH_DATE, washBeautyOrderDTO.getVestDate()));
    //      washBeautyOrderDTO.setExecutor((String)request.getSession().getAttribute("userName"));
    ReceivableDTO receivableDTO = txnService.getReceivableByShopIdAndOrderTypeAndOrderId(shopId, null, washBeautyOrderDTO.getId());
    washBeautyOrderDTO.setReceivableDTO(receivableDTO);
    Long memberId = receivableDTO.getMemberId();
    MemberDTO memberDTO = null;
    Map<Long, MemberServiceDTO> memberServiceDTOMap = new HashMap<Long, MemberServiceDTO>();
    VelocityContext context = new VelocityContext();
    if (null != memberId) {
      memberDTO = membersService.getMemberDTOById(shopId, memberId);

      context.put("memberNo", memberDTO.getMemberNo());
      context.put("memberBalance", memberDTO.getBalance());
      if (CollectionUtils.isNotEmpty(memberDTO.getMemberServiceDTOs())) {
        memberServiceDTOMap = MemberServiceDTO.listToMap(memberDTO.getMemberServiceDTOs());
      }
    } else {
      memberDTO = membersService.getMemberByCustomerId(shopId, washBeautyOrderDTO.getCustomerId());
      if (null != memberDTO) {
        context.put("memberNo", memberDTO.getMemberNo());
        context.put("memberBalance", memberDTO.getBalance());
        if (CollectionUtils.isNotEmpty(memberDTO.getMemberServiceDTOs())) {
          memberServiceDTOMap = MemberServiceDTO.listToMap(memberDTO.getMemberServiceDTOs());
        }
      }
    }
    WashBeautyOrderItemDTO[] washBeautyOrderItemDTOs = washBeautyOrderDTO.getWashBeautyOrderItemDTOs();
    if (null == washBeautyOrderItemDTOs || washBeautyOrderItemDTOs.length == 0) {
      return;
    }
    int consumeTimes = 0;
    for (WashBeautyOrderItemDTO washBeautyOrderItemDTO : washBeautyOrderItemDTOs) {
      ServiceDTO serviceDTO = txnService.getServiceById(washBeautyOrderItemDTO.getServiceId());
      washBeautyOrderItemDTO.setServiceName(serviceDTO.getName());
      MemberServiceDTO memberServiceDTO = memberServiceDTOMap.get(washBeautyOrderItemDTO.getServiceId());
      washBeautyOrderItemDTO.setMemberServiceTime(null == memberServiceDTO ? null : memberServiceDTO.getTimes());
      if (ConsumeType.TIMES == washBeautyOrderItemDTO.getPayType()) {
        consumeTimes++;
      }
    }
    context.put("nowStr", DateUtil.convertDateLongToDateString(DateUtil.DEFAULT, System.currentTimeMillis()));
    context.put("shopDTO", shopDTO);
    context.put("dataStr", dataStr);
    context.put("orderDTO", washBeautyOrderDTO);
    context.put("consumeTimes", consumeTimes);
    String myTemplateName = "WASH_AUTO_TICKET" + String.valueOf(shopId);
    PrintTemplateDTO printTemplateDTO = printService.getSinglePrintTemplateDTOByShopIdAndType(shopId, OrderTypes.WASH_AUTO_TICKET);
    PrintHelper.generatePrintPage(response, printTemplateDTO.getTemplateHtml(), myTemplateName, context);
  }

}
