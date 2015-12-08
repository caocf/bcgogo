package com.bcgogo.customer;

import com.bcgogo.common.Pager;
import com.bcgogo.common.RFCommon;
import com.bcgogo.common.StringUtil;
import com.bcgogo.common.WebUtil;
import com.bcgogo.config.dto.OperationLogDTO;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.service.IOperationLogService;
import com.bcgogo.enums.*;
import com.bcgogo.enums.user.SalesManStatus;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.notification.dto.MessageSwitchDTO;
import com.bcgogo.notification.service.INotificationService;
import com.bcgogo.notification.service.ISmsService;
import com.bcgogo.search.dto.*;
import com.bcgogo.search.service.IOrderIndexService;
import com.bcgogo.search.service.ISearchService;
import com.bcgogo.search.service.order.ISearchOrderService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.stat.StatUtil;
import com.bcgogo.txn.bcgogoListener.orderEvent.MemberCardOrderSavedEvent;
import com.bcgogo.txn.bcgogoListener.orderEvent.MemberCardReturnSavedEvent;
import com.bcgogo.txn.bcgogoListener.publisher.BcgogoEventPublisher;
import com.bcgogo.txn.dto.*;
import com.bcgogo.txn.model.Service;
import com.bcgogo.txn.service.*;
import com.bcgogo.txn.service.sms.ISendSmsService;
import com.bcgogo.user.dto.*;
import com.bcgogo.user.model.*;
import com.bcgogo.user.service.ICustomerService;
import com.bcgogo.user.service.IMembersService;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.utils.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
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
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: cfl
 * Date: 12-7-2
 * Time: 下午4:19
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/member.do")
public class MemberController {

  public static final Logger LOG = LoggerFactory.getLogger(MemberController.class);

  public static final int PAGE_SIZE = 15;//页面显示条数
  public static final int DEFAULT_PAGE_NO = 1;  //默认查询页码 1

  @Autowired
  private UserDaoManager userDaoManager;

  @RequestMapping(params = "method=selectFirstPage")
  public String selectFirstPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
    IMembersService membersService = ServiceManager.getService(IMembersService.class);
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    List<MemberCardDTO> memberCardDTOs = membersService.getMemberCardDTOByShopIdAndServiceStatus(shopId);
    if (CollectionUtils.isNotEmpty(memberCardDTOs)) {
      request.setAttribute("memberCardDTOs", getMemberCardDTOByShopIdAndServiceStatus(shopId));
      return "/customer/cardSetComplete";
    }
    return toCardFirst(request, response);
  }

  @RequestMapping(params = "method=toCardFirst")
  public String toCardFirst(HttpServletRequest request, HttpServletResponse response) {

    MemberCardDTO memberCardDTO = new MemberCardDTO();

    request.setAttribute("memberCardDTO", memberCardDTO);

    return "/customer/cardFirst";
  }

  /**
   * 会员卡名称不能重复
   *
   * @param request
   * @param response
   * @param name
   */
  @RequestMapping(params = "method=checkCardNameByName")
  public void checkCardNameByName(HttpServletRequest request, HttpServletResponse response, String name) {
    IMembersService membersService = ServiceManager.getService(IMembersService.class);
    try {

      Long shopId = (Long) request.getSession().getAttribute("shopId");

      PrintWriter out = response.getWriter();

      String jsonStr = "";

      MemberCardDTO memberCardDTO = null;
      if (StringUtils.isNotBlank(name)) {
        memberCardDTO = membersService.getMemberCardDTOByCardName(shopId, name);
      }

      if (null != memberCardDTO && MemberStatus.ENABLED == memberCardDTO.getStatus()) {
        jsonStr = "error";
      }
      else {
        jsonStr = "success";
      }

      Map<String, String> jsonMap = new HashMap<String, String>();

      jsonMap.put("resu", jsonStr);

      out.write(JsonUtil.mapToJson(jsonMap));

      out.flush();
      out.close();
      //单元测试用
      request.setAttribute("name", name);
    } catch (Exception e) {
      LOG.debug("method=checkNameByName");
      LOG.error(e.getMessage(), e);
    }
  }

  @RequestMapping(params = "method=toCardSecond")
  public String toCardSecond(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response, MemberCardDTO memberCardDTO) {
    String name = memberCardDTO.getName();
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    IMembersService membersService = ServiceManager.getService(IMembersService.class);
    try {
      MemberCardDTO newMemberCardDTO = membersService.getMemberCardDTOByCardName(shopId, name);
      if (null != newMemberCardDTO && MemberStatus.ENABLED == newMemberCardDTO.getStatus()) {
        newMemberCardDTO.setType(memberCardDTO.getType());
        memberCardDTO = newMemberCardDTO;
        memberCardDTO.setPrice(com.bcgogo.utils.NumberUtil.round(memberCardDTO.getPrice(), NumberUtil.MONEY_PRECISION));
        memberCardDTO.setWorth(com.bcgogo.utils.NumberUtil.round(memberCardDTO.getWorth(), NumberUtil.MONEY_PRECISION));
        memberCardDTO.setPercentageAmount(com.bcgogo.utils.NumberUtil.round(memberCardDTO.getPercentageAmount(), NumberUtil.MONEY_PRECISION));
      }
    } catch (Exception ex) {
      LOG.debug("method=toCardSecond");
      ex.printStackTrace();
      LOG.error(ex.getMessage(), ex);
    }
    modelMap.addAttribute("memberCardDTO", memberCardDTO);
    return "/customer/cardSecond";
  }

  @RequestMapping(params = "method=toCardThird")
  public String toCardThird(ModelMap model, HttpServletRequest request, HttpServletResponse response, MemberCardDTO memberCardDTO) throws Exception {
    IMembersService membersService = ServiceManager.getService(IMembersService.class);
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    //当前途这三种价格框不填的话默认为0
    if (null == memberCardDTO.getWorth()) {
      memberCardDTO.setWorth(0.0);
    }
    if (null == memberCardDTO.getPrice()) {
      memberCardDTO.setPrice(0.0);
    }
    if (null == memberCardDTO.getPercentageAmount()) {
      memberCardDTO.setPercentageAmount(0.0);
    }

    //获取此会员卡信息（状态为endabled的服务）
    MemberCardDTO newMemberCardDTO = null;
    if (null != memberCardDTO && StringUtils.isNotBlank(memberCardDTO.getName())) {
      newMemberCardDTO = membersService.getMemberCardDTOAndServiceByShopIdAndNameAndStatus(shopId,
                                                                                           memberCardDTO.getName());
    }

    if (newMemberCardDTO != null && MemberStatus.ENABLED == newMemberCardDTO.getStatus()) {
      //获取服务名称
      if (CollectionUtils.isNotEmpty(newMemberCardDTO.getMemberCardServiceDTOs())) {
        for (MemberCardServiceDTO memberCardServiceDTO : newMemberCardDTO.getMemberCardServiceDTOs()) {
          ServiceDTO serviceDTO = txnService.getServiceById(memberCardServiceDTO.getServiceId());
          memberCardServiceDTO.setServiceName(serviceDTO.getName());
        }
      }

      newMemberCardDTO.setType(memberCardDTO.getType());
      newMemberCardDTO.setPrice(memberCardDTO.getPrice());
      newMemberCardDTO.setWorth(memberCardDTO.getWorth());
      newMemberCardDTO.setPercentageAmount(memberCardDTO.getPercentageAmount());
      memberCardDTO = newMemberCardDTO;
    }

    model.addAttribute("memberCardDTO", memberCardDTO);
    return "/customer/cardThird";
  }

  @RequestMapping(params = "method=saveCardSet")
  public String saveCardSet(HttpServletRequest request, HttpServletResponse response, MemberCardDTO memberCardDTO) {
    IMembersService membersService = ServiceManager.getService(IMembersService.class);
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    memberCardDTO.setShopId(shopId);
    try {
      //处理前台输入memberCardService没有Id
      if(CollectionUtils.isNotEmpty(memberCardDTO.getMemberCardServiceDTOs()))
      {
        for (MemberCardServiceDTO memberCardServiceDTO : memberCardDTO.getMemberCardServiceDTOs()) {
          if (StringUtil.isEmpty(memberCardServiceDTO.getServiceName())) {
            continue;
          }
          ServiceDTO serviceDTO = txnService.saveOrUpdateService(shopId, memberCardServiceDTO.getServiceName());
          memberCardServiceDTO.setServiceId(serviceDTO.getId());
        }
      }

      //保存卡信息
      memberCardDTO = membersService.saveOrUpdateMemberCard(memberCardDTO);
      request.setAttribute("memberCardDTO", memberCardDTO);
      request.setAttribute("memberCardDTOs", getMemberCardDTOByShopIdAndServiceStatus(shopId));
    } catch (Exception ex) {
      LOG.debug("method=saveCardSet");
      LOG.debug("shopId", shopId);
      LOG.debug("memberCardDTO", memberCardDTO);
      ex.printStackTrace();
      LOG.error(ex.getMessage(), ex);
    }
    return "/customer/cardSetComplete";
  }

  /**
   * 卡列表中点删除修改会员卡状态为DISABLED
   *
   * @param request
   * @param response
   * @param id
   * @return
   * @throws Exception
   */
  @RequestMapping(params = "method=disableMemberCard")
  public String disableMemberCard(HttpServletRequest request, HttpServletResponse response, Long id) throws Exception {
    Long shopId = (Long) request.getSession().getAttribute("shopId");

    IMembersService membersService = ServiceManager.getService(IMembersService.class);
    try {
      if (null != id) {
        membersService.disabledMemberCardDTOById(shopId, id);
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }

    request.setAttribute("memberCardDTOs", getMemberCardDTOByShopIdAndServiceStatus(shopId));
    return "/customer/cardSetComplete";
  }

  /**
   * 获取卡列表和信息
   *
   * @param shopId
   * @return
   * @throws Exception
   */
  public List<MemberCardDTO> getMemberCardDTOByShopIdAndServiceStatus(Long shopId) throws Exception {
    IMembersService membersService = ServiceManager.getService(IMembersService.class);
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    List<String> names = new ArrayList<String>();

    List<MemberCardDTO> memberCardDTOs = membersService.getMemberCardDTOByShopIdAndServiceStatus(shopId);

    //卡排列顺序
    if (null != memberCardDTOs && memberCardDTOs.size() > 0) {
      for (MemberCardDTO memberCardDTO2 : memberCardDTOs) {
        if ("洗车卡".equals(memberCardDTO2.getName())) {
          memberCardDTO2.setSort(4);
        }
        if ("银卡".equals(memberCardDTO2.getName())) {
          memberCardDTO2.setSort(3);
        }
        if ("金卡".equals(memberCardDTO2.getName())) {
          memberCardDTO2.setSort(2);
        }
        if ("VIP卡".equals(memberCardDTO2.getName())) {
          memberCardDTO2.setSort(1);
        }
        if (null != memberCardDTO2.getMemberCardServiceDTOs() && memberCardDTO2.getMemberCardServiceDTOs().size() > 0) {
          for (MemberCardServiceDTO memberCardServiceDTO : memberCardDTO2.getMemberCardServiceDTOs()) {
            ServiceDTO serviceDTO = txnService.getServiceById(memberCardServiceDTO.getServiceId());
            if (null != serviceDTO && StringUtils.isNotBlank(serviceDTO.getName())) {
              memberCardServiceDTO.setServiceName(serviceDTO.getName());
            }
          }
        }
      }
      Collections.sort(memberCardDTOs);
    }

    return memberCardDTOs;
  }

  @RequestMapping(params = "method=selectCardList")
  public String selectCardList(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) throws Exception {
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    modelMap.addAttribute("memberCardDTOs", getMemberCardDTOByShopIdAndServiceStatus(shopId));
    return "/customer/selectCard";
  }

  @RequestMapping(params = "method=buyCard")
  public String buyCard(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response, Long customerId, Long cardId) {
    IMembersService membersService = ServiceManager.getService(IMembersService.class);
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    ICustomerService customerService = ServiceManager.getService(ICustomerService.class);
    INotificationService notificationService = ServiceManager.getService(INotificationService.class);
    MemberDTO memberDTO = null;
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    MemberCardDTO memberCardDTO = null;
    MemberOrderType memberOrderType = MemberOrderType.NEW;   //购卡还是续卡

    List<MemberCardOrderServiceDTO> memberCardOrderServiceDTOs = null;
    try {
      CustomerDTO customerDTO = customerService.getCustomerById(customerId);
      //获取会员信息
      if (null != customerId) {
        memberDTO = membersService.getMemberByCustomerId(shopId, customerId);
        if (null != memberDTO && memberDTO.getStatus() != MemberStatus.DISABLED) {
          memberOrderType = MemberOrderType.RENEW;
          memberDTO.setBalance(com.bcgogo.utils.NumberUtil.round(memberDTO.getBalance(), NumberUtil.MONEY_PRECISION));
          if (null != memberDTO.getMemberDiscount()) {
            memberDTO.setMemberDiscount(NumberUtil.round(memberDTO.getMemberDiscount() * 10, 1));
          }
        }
      }
      //购买的卡信息
      memberCardDTO = membersService.getMemberCardDTOByCardIdAndServiceStatus(shopId, cardId);
      if (null != memberCardDTO) {
        memberCardDTO.setPrice(com.bcgogo.utils.NumberUtil.round(memberCardDTO.getPrice(), NumberUtil.MONEY_PRECISION));
        memberCardDTO.setWorth(com.bcgogo.utils.NumberUtil.round(memberCardDTO.getWorth(), NumberUtil.MONEY_PRECISION));
      }
      if (null != memberCardDTO && CollectionUtils.isNotEmpty(memberCardDTO.getMemberCardServiceDTOs())) {
        for (MemberCardServiceDTO memberCardServiceDTO : memberCardDTO.getMemberCardServiceDTOs()) {
          ServiceDTO serviceDTO = txnService.getServiceById(memberCardServiceDTO.getServiceId());
          if (null != serviceDTO && StringUtils.isNotBlank(serviceDTO.getName())) {
            memberCardServiceDTO.setServiceName(serviceDTO.getName());
          }
        }
      }

      //把会员原来的可用的服务和现在卡上的服务合并起来
      memberCardOrderServiceDTOs = this.combineOldServiceAndNewService(memberDTO, memberCardDTO);
      if (null != memberCardOrderServiceDTOs) {
        for (MemberCardOrderServiceDTO memberCardOrderServiceDTO : memberCardOrderServiceDTOs) {
          ServiceDTO serviceDTO = txnService.getServiceById(memberCardOrderServiceDTO.getServiceId());
          if (null != serviceDTO && null != serviceDTO.getName()) {
            memberCardOrderServiceDTO.setServiceName(serviceDTO.getName());
          }
        }
      }

      MemberCardOrderDTO memberCardOrderDTO = new MemberCardOrderDTO();
      memberCardOrderDTO.setMemberCardOrderServiceDTOs(memberCardOrderServiceDTOs);
      memberCardOrderDTO.setMemberDTO(memberDTO);
      memberCardOrderDTO.setCustomerId(customerId);
      memberCardOrderDTO.setShopId(shopId);
      memberCardOrderDTO.setMemberOrderType(memberOrderType);
      if (null == memberCardDTO) {
        memberCardOrderDTO.setMemberCardName("洗车卡");
      } else {
        memberCardOrderDTO.setMemberCardName(memberCardDTO.getName());
      }

      if (null != memberDTO && null != memberCardDTO) {
        if (StringUtils.isNotBlank(memberDTO.getType())) {
          memberCardOrderDTO.setMemberType(memberDTO.getType());
        } else if (StringUtils.isNotBlank(memberCardDTO.getName())) {
          memberCardOrderDTO.setMemberType(memberCardDTO.getName());
        } else {
          memberCardOrderDTO.setMemberType("洗车卡");
        }
      } else if (null != memberDTO && null == memberCardDTO) {
        if (StringUtils.isNotBlank(memberDTO.getType())) {
          memberCardOrderDTO.setMemberType(memberDTO.getType());
        } else {
          memberCardOrderDTO.setMemberType("洗车卡");
        }
      } else if (null == memberDTO && null != memberCardDTO) {
        if (StringUtils.isNotBlank(memberCardDTO.getName())) {
          memberCardOrderDTO.setMemberType(memberCardDTO.getName());
        } else {
          memberCardOrderDTO.setMemberType("洗车卡");
        }
      } else if (null == memberDTO && null == memberCardDTO) {
        memberCardOrderDTO.setMemberType("洗车卡");
      }
      if (memberDTO != null && memberDTO.getId() != null) {
        MemberStatus memberStatus = membersService.getMemberStatusByMemberDTO(memberDTO);
        request.setAttribute("memberStatus", memberStatus);
      }
      //短信控制开关
      MessageSwitchDTO messageSwitchDTO = notificationService.getMessageSwitchDTOByShopIdAndScene(shopId, MessageScene.MEMBER_CONSUME_SMS_SWITCH);
      if(messageSwitchDTO == null || (messageSwitchDTO != null && MessageSwitchStatus.ON.equals(messageSwitchDTO.getStatus()))) {
          modelMap.addAttribute("smsSwitch",true);
      } else {
        modelMap.addAttribute("smsSwitch",false);
      }
      modelMap.addAttribute("user", request.getSession().getAttribute("userName"));
      modelMap.addAttribute("userId", request.getSession().getAttribute("userId"));
      modelMap.addAttribute("customerDTO", customerDTO);
      modelMap.addAttribute("customerId", customerId);
      modelMap.addAttribute("memberDTO", memberDTO);
      modelMap.addAttribute("memberCardDTO", memberCardDTO);
      modelMap.addAttribute("memberCardOrderDTO", memberCardOrderDTO);
      String dateStr = DateUtil.getTodayStr(DateUtil.YEAR_MONTH_DATE);
      modelMap.addAttribute("dateStr", dateStr);
    } catch (Exception e) {
      LOG.debug("customerId", customerId);
      LOG.error(e.getMessage(), e);
    }

    return "/customer/buyCard";
  }

  /**
   * 超级管理->员工营业统计->员工管理 获得员工信息
   *
   * @param model
   * @param request
   * @return
   * @throws BcgogoException
   */
  @Deprecated //by zhangjuntao
  @RequestMapping(params = "method=salesManData")
  public String salesManData(ModelMap model, HttpServletRequest request) throws BcgogoException {

    IUserService userService = ServiceManager.getService(IUserService.class);
    Long shopId = (Long) request.getSession().getAttribute("shopId");

    int totalNumber = userService.countSalesManByShopIdAndStatus(shopId, null);
    int totalInService = userService.countSalesManByShopIdAndStatus(shopId, SalesManStatus.INSERVICE);
    request.setAttribute("totalInService", totalInService);

    Pager pager = new Pager(totalNumber, NumberUtil.intValue(request.getParameter("pageNo"), DEFAULT_PAGE_NO),
                            PAGE_SIZE);
    request.setAttribute("pager", pager);
    List<SalesManDTO> salesManDTOList;
    salesManDTOList = userService.getSalesManDTOListByShopId(shopId, null, pager);

    request.removeAttribute("salesManDTOList");
    request.setAttribute("salesManDTOList", salesManDTOList);
    return "/customer/salesManData";
  }

  /**
   * 根据 request中的shop_id 和员工id获取该员工信息 如果id不存在 默认是新员工
   *
   * @param request
   * @return
   * @throws BcgogoException
   */
  @Deprecated //by zhangjuntao
  @RequestMapping(params = "method=getSaleManInfoById")
  public String getSaleManInfoById(HttpServletRequest request,Long salesManId) throws BcgogoException {
    Long shopId = WebUtil.getShopId(request);
    IUserService userService = ServiceManager.getService(IUserService.class);
    SalesManDTO salesManDTO = null;
    if (salesManId!=null) {
      salesManDTO = userService.getSalesManDTOById(salesManId);
    }
    else {
      salesManDTO = new SalesManDTO();
      salesManDTO.setStatus(SalesManStatus.INSERVICE);
      salesManDTO.setSex(Sex.MALE.toString());
      salesManDTO.setShopId(shopId);
    }
    request.setAttribute("salesManDTO", salesManDTO);
    return "/customer/salesManInfo";
  }



  /**
   * 更新员工信息
   *
   * @param model
   * @param request
   * @param response
   * @param salesManDTO
   * @throws Exception
   */
  @Deprecated
  @RequestMapping(params = "method=updateSalesManInfo")
  public void updateSalesManInfo(ModelMap model, HttpServletRequest request, HttpServletResponse response, SalesManDTO
      salesManDTO) throws Exception {
    IUserService userService = ServiceManager.getService(IUserService.class);

    if (salesManDTO == null) {
      LOG.error("/member.do");
      LOG.error("method= updateSalesManInfo");
      LOG.error(salesManDTO.toString());
      LOG.error("更新员工信息失败,员工信息为空");
      return;
    }
    else {
      try {
        userService.saveOrUpdateSalesMan(salesManDTO);
      } catch (Exception e) {
        LOG.error("/member.do");
        LOG.error("method= updateSalesManInfo");
        LOG.error(salesManDTO.toString());
        LOG.error("更新员工信息失败");
        LOG.error(e.getMessage(), e);
      }
    }
  }

  /**
   * 更改施工人
   */
  @RequestMapping(params = "method=updateWorkerName")
  @ResponseBody
  public Map updateWorkerName(HttpServletRequest request, HttpServletResponse response) throws Exception {
    String workerName = request.getParameter("workerName");
    String workerIdStr = request.getParameter("workerId");
    Long workerId = null;
    Map map = new HashMap();

    if (StringUtils.isNotBlank(workerIdStr)) {
      workerId = Long.valueOf(workerIdStr);
    }

    if (StringUtils.isBlank(workerName) || null == workerId) {
      map.put("resu", "error");
      map.put("msg", "no workerName or no workerId");
      return map;
    }

    Long shopId = WebUtil.getShopId(request);

    IUserService userService = ServiceManager.getService(IUserService.class);
    SalesManDTO workerDTO = userService.getSalesManDTOById(workerId);

    SalesMan salesMan = userService.getSalesManByName(shopId, workerName);

    if(salesMan!=null){

      if(!NumberUtil.isEqual(salesMan.getId(),workerId)){
        map.put("resu", "error");
        map.put("msg", "Exists the same worker name");
      }
      else{
        workerDTO.setName(workerName);
        userService.saveOrUpdateSalesMan(workerDTO);
        map.put("resu", "success");
      }
    }
    else{
        workerDTO.setName(workerName);
        userService.saveOrUpdateSalesMan(workerDTO);
        map.put("resu", "success");
    }
    return map;

  }

  // /**
  //  * 删除施工人
  //  */
  // @RequestMapping(params = "method=deleteWorker")
  // @ResponseBody
  // public Map deleteWorker(HttpServletRequest request, HttpServletResponse response) {

  //   IUserService userService = ServiceManager.getService(IUserService.class);
  //   String workerIdStr = request.getParameter("workerId");
  //   Map map = new HashMap();

  //   if(StringUtils.isBlank(workerIdStr))
  //   {
  //     return map;
  //   }

  //   Long workerId = Long.valueOf(workerIdStr);

  //   try{
  //     userService.
  //     map.put("resu","success");
  //   }
  //   catch (Exception e)
  //   {
  //     map.put("resu","error");
  //     LOG.error(e.getMessage(),e);
  //     LOG.error("method=deleteWorker  workerId="+workerId);
  //   }

  //   return map;
  // }

  public List<MemberCardOrderServiceDTO> combineOldServiceAndNewService(MemberDTO memberDTO, MemberCardDTO memberCardDTO) throws Exception {
    List<MemberCardOrderServiceDTO> memberCardOrderServiceDTOs = new ArrayList<MemberCardOrderServiceDTO>();
    List<MemberServiceDTO> memberServiceDTOs = null;
    List<MemberCardServiceDTO> memberCardServiceDTOs = null;
    if (null != memberDTO && CollectionUtils.isNotEmpty(memberDTO.getMemberServiceDTOs())) {
      memberServiceDTOs = memberDTO.getMemberServiceDTOs();
    }
    if (null != memberCardDTO && CollectionUtils.isNotEmpty(memberCardDTO.getMemberCardServiceDTOs())) {
      memberCardServiceDTOs = memberCardDTO.getMemberCardServiceDTOs();
    }
    Map<Long, MemberServiceDTO> memberServiceDTOMap = MemberServiceDTO.listToMap(memberServiceDTOs);
    Map<Long, MemberCardServiceDTO> memberCardServiceDTOMap = MemberCardServiceDTO.listToMap(memberCardServiceDTOs);
    MemberCardOrderServiceDTO memberCardOrderServiceDTO = null;
    if (CollectionUtils.isNotEmpty(memberServiceDTOs)) {
      for (MemberServiceDTO memberServiceDTO : memberServiceDTOs) {
        memberCardOrderServiceDTO = new MemberCardOrderServiceDTO();
        memberCardOrderServiceDTO.setOldDeadlineStr(DateUtil.convertDateLongToDateString(DateUtil.YEAR_MONTH_DATE, memberServiceDTO.getDeadline()));
        if (null != memberCardServiceDTOMap.get(memberServiceDTO.getServiceId())) {
          MemberCardServiceDTO memberCardServiceDTO = memberCardServiceDTOMap.get(memberServiceDTO.getServiceId());
          if (com.bcgogo.utils.NumberUtil.isEqualNegativeOne(memberCardServiceDTO.getTerm())) {
            memberCardOrderServiceDTO.setAddTerm(0);
          } else {
            memberCardOrderServiceDTO.setAddTerm(memberCardServiceDTO.getTerm());
          }
          memberCardOrderServiceDTO.setVehicles(memberServiceDTO.getVehicles());
          memberCardOrderServiceDTO.setOldTimes(memberServiceDTO.getTimes());
          memberCardOrderServiceDTO.setCardTimes(memberCardServiceDTO.getTimes());
          memberCardOrderServiceDTO.setCardTimesStatus(TimesStatus.UNLIMITED.getStatus());
          if (!com.bcgogo.utils.NumberUtil.isEqualNegativeOne(memberServiceDTO.getTimes()) && !com.bcgogo.utils.NumberUtil.isEqualNegativeOne(memberCardServiceDTO.getTimes())) {
            memberCardOrderServiceDTO.setBalanceTimes(memberServiceDTO.getTimes() + memberCardServiceDTO.getTimes());
          } else {
            memberCardOrderServiceDTO.setBalanceTimes(memberCardServiceDTO.getTimes());
          }
          memberCardOrderServiceDTO.setIncreasedTimes(memberCardServiceDTO.getTimes());
          memberCardOrderServiceDTO.setServiceId(memberCardServiceDTO.getServiceId());
          memberCardOrderServiceDTO.setDeadline( DateUtil.getDeadline(System.currentTimeMillis(), memberCardServiceDTO.getTerm()));
          if (!Long.valueOf("-1").equals(memberServiceDTO.getDeadline()) && !com.bcgogo.utils.NumberUtil.isEqualNegativeOne(memberCardServiceDTO.getTerm())) {
            memberCardOrderServiceDTO.setDeadline(DateUtil.getDeadline(memberServiceDTO.getDeadline(), memberCardServiceDTO.getTerm()));
          }
          memberCardOrderServiceDTO.setCardTimesStatus(TimesStatus.UNLIMITED.getStatus());
          if (Long.valueOf("-1").equals(memberCardOrderServiceDTO.getDeadline())) {
            memberCardOrderServiceDTO.setDeadlineStr(DeadlineLimitType.UNLIMITED.getType());
          } else {
            String deadlineStr = DateUtil.convertDateLongToDateString(DateUtil.YEAR_MONTH_DATE,memberCardOrderServiceDTO.getDeadline());
            memberCardOrderServiceDTO.setDeadlineStr(deadlineStr);
          }
          memberCardOrderServiceDTOs.add(memberCardOrderServiceDTO);
        }
        //memberServiceDTO中有此服务，而memberCardServiceDTO中无此服务
        else {
          memberCardOrderServiceDTO.setAddTerm(0);
          memberCardOrderServiceDTO.setOldTimes(memberServiceDTO.getTimes());
          memberCardOrderServiceDTO.setBalanceTimes(memberServiceDTO.getTimes());
          memberCardOrderServiceDTO.setIncreasedTimes(0);
          memberCardOrderServiceDTO.setCardTimesStatus(TimesStatus.LIMITED.getStatus());
          memberCardOrderServiceDTO.setVehicles(memberServiceDTO.getVehicles());
          memberCardOrderServiceDTO.setServiceId(memberServiceDTO.getServiceId());
          memberCardOrderServiceDTO.setDeadline(memberServiceDTO.getDeadline());
          memberCardOrderServiceDTO.setDeadlineStr(memberServiceDTO.getDeadlineStr());
          memberCardOrderServiceDTOs.add(memberCardOrderServiceDTO);
        }
      }
    }

    //遍历memberCardServiceDTOs，因为上文中已把服务相同的合并了，所以这里不做处理
    if (CollectionUtils.isNotEmpty(memberCardServiceDTOs)) {
      for (MemberCardServiceDTO memberCardServiceDTO : memberCardServiceDTOs) {
        memberCardOrderServiceDTO = new MemberCardOrderServiceDTO();
        if (null == memberServiceDTOMap.get(memberCardServiceDTO.getServiceId())) {
          memberCardOrderServiceDTO.setOldDeadlineStr(DateUtil.convertDateLongToDateString(DateUtil.YEAR_MONTH_DATE, System.currentTimeMillis()));
          memberCardOrderServiceDTO.setCardTimesStatus(TimesStatus.UNLIMITED.getStatus());
          if (com.bcgogo.utils.NumberUtil.isEqualNegativeOne(memberCardServiceDTO.getTerm())) {
            memberCardOrderServiceDTO.setAddTerm(0);
          }else {
            memberCardOrderServiceDTO.setAddTerm(memberCardServiceDTO.getTerm());
          }
          memberCardOrderServiceDTO.setOldTimes(0);
          memberCardOrderServiceDTO.setCardTimes(memberCardServiceDTO.getTimes());
          memberCardOrderServiceDTO.setBalanceTimes(memberCardServiceDTO.getTimes());
          memberCardOrderServiceDTO.setIncreasedTimes(memberCardServiceDTO.getTimes());
          memberCardOrderServiceDTO.setServiceId(memberCardServiceDTO.getServiceId());
          memberCardOrderServiceDTO.setDeadline(DateUtil.getDeadline(System.currentTimeMillis(), memberCardServiceDTO.getTerm()));
          if (Long.valueOf("-1").equals(memberCardOrderServiceDTO.getDeadline())) {
            memberCardOrderServiceDTO.setDeadlineStr(DeadlineLimitType.UNLIMITED.getType());
          }else {
            String deadlineStr = DateUtil.convertDateLongToDateString(DateUtil.YEAR_MONTH_DATE,memberCardOrderServiceDTO.getDeadline());
            memberCardOrderServiceDTO.setDeadlineStr(deadlineStr);
          }
          memberCardOrderServiceDTOs.add(memberCardOrderServiceDTO);
        }
      }
    }
    return CollectionUtils.isNotEmpty(memberCardOrderServiceDTOs) ? memberCardOrderServiceDTOs : null;
  }

  /**
   * 保存会员购卡信息
   *
   * @param modelMap
   * @param request
   * @param response
   * @param memberCardOrderDTO
   */
  @RequestMapping(params = "method=saveMemberCardOrder")
  public void saveMemberCardOrder(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response, MemberCardOrderDTO memberCardOrderDTO) throws Exception {
    IMembersService membersService = ServiceManager.getService(IMembersService.class);
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    IOrderIndexService orderIndexService = ServiceManager.getService(IOrderIndexService.class);
    IServiceHistoryService serviceHistoryService = ServiceManager.getService(IServiceHistoryService.class);
    PrintWriter out = response.getWriter();
    StringBuffer sb = new StringBuffer("");
    //txn事务保存成功标志
    boolean txnTransSuccess = false;
    //user事务保存成功标志
    boolean userTransSuccess = false;
    //search事务保存成功标志
    boolean searchTransSuccess = false;
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    try {
      Long vestDate = System.currentTimeMillis();
      String vestDateStr = DateUtil.dateLongToStr(vestDate, DateUtil.YEAR_MONTH_DATE);

      if (null != memberCardOrderDTO) {

        if (null == memberCardOrderDTO.getReceivableDTO() || null == memberCardOrderDTO.getMemberDTO()
            || null == memberCardOrderDTO.getMemberDTO().getMemberNo() || null == memberCardOrderDTO.getCustomerId()) {
          return;
        }
        //

        memberCardOrderDTO.getReceivableDTO().setLastPayeeId(WebUtil.getUserId(request));
        memberCardOrderDTO.getReceivableDTO().setLastPayee(WebUtil.getUserName(request));
        memberCardOrderDTO.setVestDate(vestDate);
        memberCardOrderDTO.setVestDateStr(vestDateStr);
        memberCardOrderDTO.setEditDate(vestDate);
        memberCardOrderDTO.setEditDateStr(vestDateStr);
        memberCardOrderDTO.setShopId(shopId);
        memberCardOrderDTO.setTotal(memberCardOrderDTO.getReceivableDTO().getTotal());

        memberCardOrderDTO.setWorth(memberCardOrderDTO.getMemberCardOrderItemDTOs().get(0).getWorth());
        memberCardOrderDTO.setMemberBalance(memberCardOrderDTO.getMemberDTO().getBalance());

        if (CollectionUtils.isNotEmpty(memberCardOrderDTO.getMemberCardOrderServiceDTOs())) {
          for (MemberCardOrderServiceDTO memberCardOrderServiceDTO : memberCardOrderDTO.getMemberCardOrderServiceDTOs()) {
            if (StringUtils.isNotBlank(memberCardOrderServiceDTO.getServiceName())) {
              memberCardOrderServiceDTO.setServiceId(txnService.saveOrUpdateServiceForWashBeauty(shopId,
                                                                                                 memberCardOrderServiceDTO.getServiceName()).getId());
            }

            if (null != memberCardOrderServiceDTO.getServiceId()) {
              txnService.changeServiceTimeType(shopId, memberCardOrderServiceDTO.getServiceId(), ServiceTimeType.YES);
            }
          }
        }

        if (null != memberCardOrderDTO.getCustomerId()) {
          MemberDTO memberDTO = membersService.getMemberByCustomerId(shopId, memberCardOrderDTO.getCustomerId());
          if (null != memberDTO) {
            memberCardOrderDTO.setOldMemberNo(memberDTO.getMemberNo());
            memberCardOrderDTO.getMemberDTO().setId(memberDTO.getId());
            if (null != memberDTO.getMemberDiscount()) {
              memberCardOrderDTO.setOldMemberDiscount(NumberUtil.round(memberDTO.getMemberDiscount(),NumberUtil.MONEY_PRECISION));
            } else {
              memberCardOrderDTO.setOldMemberDiscount(1D);
            }
          }
        }

        if (null != memberCardOrderDTO.getMemberDiscount()) {
          memberCardOrderDTO.setMemberDiscount(NumberUtil.round(memberCardOrderDTO.getMemberDiscount() / 10,NumberUtil.MONEY_PRECISION));
        }
        else {
          memberCardOrderDTO.setMemberDiscount(1D);
        }
        //保存membercardorder,membercardorderitem,membercardorderservice,receivable,receptionrecord,debt表
        memberCardOrderDTO = txnService.saveMemberCardOrder(memberCardOrderDTO);
        //txnTransSuccess保存成功
        txnTransSuccess = true;
        //保存更新会员信息member和memberservice,customer,customerrecord
        membersService.saveOrUpdateMember(memberCardOrderDTO);

        //add by WLF 生成会员卡服务到期提醒
        List<MemberService> memberServices = membersService.getMemberServicesByMemberId(memberCardOrderDTO.getMemberDTO().getId());
        if (CollectionUtils.isNotEmpty(memberServices)) {
          for (MemberService memberService : memberServices) {
            txnService.saveRemindEvent(memberService, shopId, memberCardOrderDTO.getCustomerId(), memberCardOrderDTO.getCustomerName(), memberCardOrderDTO.getMobile());
          }
        }
        //add by WLF 提醒数量，更新到缓存
        txnService.updateRemindCountInMemcacheByTypeAndShopId(RemindEventType.CUSTOMER_SERVICE, shopId);

        //add by WLF 保存会员购卡续卡的日志
        ServiceManager.getService(ITxnService.class).saveOperationLogTxnService(
            new OperationLogDTO(shopId, (Long)request.getSession().getAttribute("userId"), memberCardOrderDTO.getId(), ObjectTypes.MEMBER_CARD_BUY_ORDER, OperationTypes.CREATE));

        //userTransSuccess保存成功
        userTransSuccess = true;
        //保存orderindex和itemindex。和membercardorderservice也要保存到itemindex中
        orderIndexService.saveOrderIndexAndItemIndexOfMemberCardOrder(memberCardOrderDTO);
        //searchTransSuccess保存成功
        searchTransSuccess = true;
        //发送会员购卡短信
        ISmsService smsSercice = ServiceManager.getService(ISmsService.class);
        ShopDTO shopDTO = ServiceManager.getService(IConfigService.class).getShopById(shopId);
        VelocityContext context = new VelocityContext();
        String smsContent="";
        if(memberCardOrderDTO.getMobile() != null && "true".equals(request.getParameter("sendMsg"))) {
          if(memberCardOrderDTO.getMemberOrderType() == MemberOrderType.NEW){
            smsContent=smsSercice.sendMemberCardBuyMsg(memberCardOrderDTO, shopDTO, context);
          }else if(memberCardOrderDTO.getMemberOrderType() == MemberOrderType.RENEW){
            smsContent=smsSercice.sendMemberCardRenewMsg(memberCardOrderDTO, shopDTO, context);
          }
          ContactDTO contactDTO=new ContactDTO();
          contactDTO.setMobile(memberCardOrderDTO.getMobile());
          ServiceManager.getService(ISendSmsService.class).sendSms(shopId,WebUtil.getUserId(request),smsContent,false,true,true,contactDTO);
        }
        //会员购卡续卡营业统计  and order reindex
        BcgogoEventPublisher bcgogoEventPublisher = new BcgogoEventPublisher();
        MemberCardOrderSavedEvent memberCardOrderSavedEvent = new MemberCardOrderSavedEvent(memberCardOrderDTO);
        bcgogoEventPublisher.publisherMemberCardOrderSaved(memberCardOrderSavedEvent);
      }

    } catch (Exception e) {
      LOG.debug("method=saveMemberCardOrder");
      LOG.debug("memberCardOrderDTO{}", memberCardOrderDTO);
      if (!txnTransSuccess) {
        LOG.debug("txn数据库保存失败");
      }
      if (!userTransSuccess) {
        LOG.debug("user数据库保存失败");
      }
      if (!searchTransSuccess) {
        LOG.debug("search数据库保存失败");
      }
      LOG.error(e.getMessage(), e);
    } finally {
      if (userTransSuccess) {
        sb.append("success");
      }
      else {
        sb.append("error");
      }
      String jsonStr = sb.toString();
      Map<String, String> jsonMap = new HashMap();
      jsonMap.put("resu", jsonStr);
      jsonMap.put("orderId", null == memberCardOrderDTO.getId() ? "" : memberCardOrderDTO.getId().toString());
      jsonMap.put("memberNo", memberCardOrderDTO.getMemberDTO().getMemberNo());
      response.setCharacterEncoding("UTF-8");

      jsonStr = JsonUtil.mapToJson(jsonMap);

      out.write(jsonStr);
      out.flush();
      out.close();
      modelMap.addAttribute("memberCardOrderDTO", memberCardOrderDTO);
      modelMap.addAttribute("searchTransSuccess", searchTransSuccess);
      modelMap.addAttribute("jsonStr", jsonStr);
    }
  }

  /**
   * 获取打印购卡小票数据
   *
   * @param modelMap
   * @param request
   * @param response
   * @param orderId
   * @throws Exception
   */
  @RequestMapping(params = "method=printMemberOrder")
  public void printMemberOrder(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response, Long orderId) throws Exception {
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    IMembersService membersService = ServiceManager.getService(IMembersService.class);
    ICustomerService customerService = ServiceManager.getService(ICustomerService.class);
    IUserService userService = ServiceManager.getService(IUserService.class);
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    IServiceHistoryService serviceHistoryService = ServiceManager.getService(IServiceHistoryService.class);
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    MemberCardOrderDTO memberCardOrderDTO = null;
    try {
      memberCardOrderDTO = txnService.getMemberCardOrderDTOById(shopId, orderId);

      if (null == memberCardOrderDTO) {
        return;
      }
      ShopDTO shopDTO = configService.getShopById(shopId);
      CustomerDTO customerDTO = customerService.getCustomerById(memberCardOrderDTO.getCustomerId());
      MemberDTO memberDTO = membersService.getMemberByCustomerId(shopId, customerDTO.getId());
      MemberCardDTO memberCardDTO = membersService.getMemberCardDTOByCardIdAndServiceStatus(shopId, memberCardOrderDTO
          .getMemberCardOrderItemDTOs().get(0).getCardId());
      SalesManDTO salesManDTO = membersService.getSaleManDTOById(shopId, memberCardOrderDTO.getMemberCardOrderItemDTOs()
          .get(0).getSalesId());
      UserDTO userDTO = null;
      if (null != memberCardOrderDTO.getExecutorId()) {
        userDTO = userService.getUserByUserId(memberCardOrderDTO.getExecutorId());
      }
      ReceivableDTO receivableDTO = txnService.getReceivableByShopIdAndOrderTypeAndOrderId(shopId, null,
                                                                                           memberCardOrderDTO.getId());

      List<MemberCardOrderServiceDTO> memberCardOrderServiceDTOs = null;

      if (null != memberDTO && CollectionUtils.isNotEmpty(memberDTO.getMemberServiceDTOs())) {
        memberCardOrderServiceDTOs = new ArrayList<MemberCardOrderServiceDTO>();
        Map<Long, MemberCardOrderServiceDTO> memberCardOrderServiceDTOMap = MemberCardOrderServiceDTO.listToMap(
            memberCardOrderDTO.getMemberCardOrderServiceDTOs());
        for (MemberServiceDTO memberServiceDTO : memberDTO.getMemberServiceDTOs()) {
          ServiceDTO serviceDTO = txnService.getServiceById(memberServiceDTO.getServiceId());
          MemberCardOrderServiceDTO memberCardOrderServiceDTO = new MemberCardOrderServiceDTO();
          memberCardOrderServiceDTO.setServiceName(serviceDTO.getName());
          memberCardOrderServiceDTO.setServiceId(memberServiceDTO.getServiceId());
          memberCardOrderServiceDTO.setBalanceTimes(memberServiceDTO.getTimes());
          memberCardOrderServiceDTO.setVehicles(memberServiceDTO.getVehicles());
          memberCardOrderServiceDTO.setDeadlineStr(memberServiceDTO.getDeadlineStr());
          memberCardOrderServiceDTO.setOldTimes(memberServiceDTO.getTimes());

          if (null != memberCardOrderServiceDTOMap.get(memberServiceDTO.getServiceId())) {
            memberCardOrderServiceDTO.setOldTimes(
                memberCardOrderServiceDTOMap.get(memberServiceDTO.getServiceId()).getOldTimes());
          }
          memberCardOrderServiceDTOs.add(memberCardOrderServiceDTO);
        }
      }

      memberCardOrderDTO.setCustomerName(customerDTO.getName());
      memberCardOrderDTO.setMemberCardName(null == memberCardDTO ? "洗车卡" : memberCardDTO.getName());
      memberCardOrderDTO.setMemberDTO(memberDTO);
      memberCardOrderDTO.setVestDateStr(DateUtil.convertDateLongToDateString(DateUtil.YEAR_MONTH_DATE,
          memberCardOrderDTO.getVestDate()));
      memberCardOrderDTO.setReceivableDTO(receivableDTO);
      memberCardOrderDTO.getMemberCardOrderItemDTOs().get(0).setSalesMan(
          null == salesManDTO ? "" : salesManDTO.getName());

      request.setAttribute("shopDTO", shopDTO);
      request.setAttribute("MemberCardOrderDTO", memberCardOrderDTO);
      request.setAttribute("userDTO", userDTO);
      request.setAttribute("memberCardOrderServiceDTOs", memberCardOrderServiceDTOs);

      toPrintSmallCard(request, response);
    } catch (Exception e) {
      LOG.debug("method=printMemberOrder");
      LOG.debug("orderId", orderId);
      LOG.debug("MemberCardOrder", memberCardOrderDTO);
      LOG.debug(e.getMessage(), e);
    }
  }

  /**
   * 小票打印
   */
  public void toPrintSmallCard(HttpServletRequest request, HttpServletResponse response) throws Exception {
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    PrintWriter out = response.getWriter();
    response.setContentType("text/html");
    response.setCharacterEncoding("UTF-8");
    MemberCardOrderDTO memberCardOrderDTO = (MemberCardOrderDTO) request.getAttribute("MemberCardOrderDTO");
    UserDTO userDTO = (UserDTO) request.getAttribute("userDTO");
    ShopDTO shopDTO = (ShopDTO) request.getAttribute("shopDTO");
    List<MemberCardOrderServiceDTO> memberCardOrderServiceDTOs = (List<MemberCardOrderServiceDTO>) request.getAttribute(
        "memberCardOrderServiceDTOs");
    IPrintService printService = ServiceManager.getService(IPrintService.class);

    try {
      PrintTemplateDTO printTemplateDTO = printService.getSinglePrintTemplateDTOByShopIdAndType(RFCommon.getShopId(request),
          OrderTypes.MEMBER_BUY_CARD);

      if (null != printTemplateDTO) {
        byte bytes[] = printTemplateDTO.getTemplateHtml();
        String str = new String(bytes, "UTF-8");

        //获取VelocityEngine
        VelocityEngine ve = createVelocityEngine();
        //创建资源库
        StringResourceRepository repo = StringResourceLoader.getRepository();

        String myTemplateName = "smallCardPrint" + String.valueOf(RFCommon.getShopId(request));

        String myTemplate = str;

        //模板资源存放 资源库 中

        repo.putStringResource(myTemplateName, myTemplate);

        //从资源库中加载模板

        Template template = ve.getTemplate(myTemplateName);

        //取得velocity的模版
        Template t = ve.getTemplate(myTemplateName, "UTF-8");
        //取得velocity的上下文context
        VelocityContext context = new VelocityContext();

        double balanceMoney = com.bcgogo.utils.NumberUtil.round(memberCardOrderDTO.getMemberDTO().getBalance(), NumberUtil.MONEY_PRECISION);
        double worth = com.bcgogo.utils.NumberUtil.round(
            memberCardOrderDTO.getMemberCardOrderItemDTOs().get(0).getWorth(), NumberUtil.MONEY_PRECISION);
        double oldMoney = com.bcgogo.utils.NumberUtil.round(balanceMoney - worth, NumberUtil.MONEY_PRECISION);
        //把数据填入上下文
        context.put("worth", worth);
        context.put("balanceMoney", balanceMoney);
        context.put("oldMoney", oldMoney);
        context.put("executor", null == userDTO ? "" : userDTO.getName());
        context.put("memberCardOrderDTO", memberCardOrderDTO);
        context.put("shopName", shopDTO.getName());
        context.put("memberCardOrderServiceDTOs", memberCardOrderServiceDTOs);
        //输出流
        StringWriter writer = new StringWriter();

        //转换输出
        t.merge(context, writer);
        out.print(writer);
        writer.close();
      }
      else {
        out.print(TxnConstant.NO_PRINT_TEMPLATE);
      }
    } catch (Exception e) {
      LOG.debug("toPrintSmallCard");
      LOG.debug(e.getMessage(), e);
    } finally {
      out.flush();
      out.close();
    }
  }

  @RequestMapping(params = "method=checkMemberNo")
  public void checkMemberNo(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response, String memberNo) {
    IMembersService membersService = ServiceManager.getService(IMembersService.class);
    try {

      Long shopId = (Long) request.getSession().getAttribute("shopId");

      PrintWriter out = response.getWriter();

      String jsonStr = "";

      Member member = null;
      if (StringUtils.isNotBlank(memberNo)) {
        member = membersService.getMemberByShopIdAndMemberNo(shopId, memberNo);
      }

      if (null != member) {
        jsonStr = "error";
      }
      else {
        jsonStr = "success";
      }

      Map<String, String> jsonMap = new HashMap<String, String>();

      jsonMap.put("resu", jsonStr);

      jsonStr = JsonUtil.mapToJson(jsonMap);

      out.write(jsonStr);
      out.flush();
      out.close();

      modelMap.addAttribute("memberNo", memberNo);
    } catch (Exception e) {
      LOG.debug("method=checkMemberNo");
      LOG.error(e.getMessage(), e);
    }
  }

  @RequestMapping(params = "method=getSaleMans")
  public void getSaleMans(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response, String name) throws Exception {
    IMembersService membersService = ServiceManager.getService(IMembersService.class);
    IUserService userService = ServiceManager.getService(IUserService.class);
    String jsonStr = "";
    Long shopId = (Long) request.getSession().getAttribute("shopId");

    name ="";//暂时不以名字搜索
    List<SalesManDTO> salesManDTOs = membersService.searchSaleManByShopIdAndKeyword(shopId, name);

    jsonStr = JsonUtil.listToJson(salesManDTOs);

    PrintWriter writer = response.getWriter();
    writer.write(jsonStr);
    writer.close();

    modelMap.addAttribute("jsonStr", jsonStr);
  }

  /**
   * ajax获取一个服务信息（memberservice和membercardservice合并后的）
   *
   * @param modelMap
   * @param request
   * @param response
   * @param memberId
   * @param cardId
   * @param serviceId
   * @throws Exception
   */
  @RequestMapping(params = "method=getServiceByCardServiceCombineMemberService")
  public void getServicesByCardServiceCombineMemberService(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response, Long memberId, Long cardId, Long serviceId, String serviceName) throws Exception {
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    RFITxnService rfiTxnService = ServiceManager.getService(RFITxnService.class);
    IMembersService membersService = ServiceManager.getService(IMembersService.class);
    String jsonStr = "";
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    ServiceDTO serviceDTO = null;
    MemberService memberService = null;

    if (null == serviceId && StringUtils.isBlank(serviceName)) {
      return;
    }

    if (null == serviceId) {
      try {
        Service service = rfiTxnService.getRFServiceByServiceNameAndShopId(shopId, serviceName);

        if (null != service) {
          if (ServiceStatus.DISABLED != service.getStatus()) {
            serviceId = service.getId();
          }

          serviceName = service.getName();
        }
      } catch (Exception e) {
        LOG.error("saveOrUpdateService");
        LOG.error("shopId", shopId);
        LOG.error("shopId", serviceName);
        LOG.error(e.getMessage(), e);
      }
    }

    if (null != memberId) {
      memberService = membersService.getMemberServiceByMemberIdAndServiceIdAndStatus(memberId, serviceId);
    }
    MemberCardService memberCardService = membersService.getMemberCardServiceByMemberCardIdAndServiceIdAndStatus(cardId,
                                                                                                                 serviceId);

    MemberCardOrderServiceDTO memberCardOrderServiceDTO = new MemberCardOrderServiceDTO();

    if (null != memberService && null != memberCardService) {
      if (memberService.getDeadline() < System.currentTimeMillis()) {
        memberCardOrderServiceDTO.setOldDeadlineStr(
            DateUtil.convertDateLongToDateString(DateUtil.YEAR_MONTH_DATE, System.currentTimeMillis()));
      }
      else {
        memberCardOrderServiceDTO.setOldDeadlineStr(
            DateUtil.convertDateLongToDateString(DateUtil.YEAR_MONTH_DATE, memberService.getDeadline()));
      }
      memberCardOrderServiceDTO.setServiceName(serviceName);
      memberCardOrderServiceDTO.setOldTimes(memberService.getTimes());
      memberCardOrderServiceDTO.setCardTimesStatus(TimesStatus.UNLIMITED.getStatus());
      memberCardOrderServiceDTO.setCardTimes(memberCardService.getTimes());

      if (!Integer.valueOf(-1).equals(memberService.getTimes()) && !Integer.valueOf(-1).equals(
          memberCardService.getTimes())) {
        memberCardOrderServiceDTO.setBalanceTimes(memberService.getTimes() + memberCardService.getTimes());
      }
      else {
        memberCardOrderServiceDTO.setBalanceTimes(memberCardService.getTimes());
      }

      memberCardOrderServiceDTO.setIncreasedTimes(memberCardService.getTimes());
      memberCardOrderServiceDTO.setServiceId(memberCardService.getServiceId());
      memberCardOrderServiceDTO.setVehicles(memberService.getVehicles());
      memberCardOrderServiceDTO.setDeadline(
          DateUtil.getDeadline(System.currentTimeMillis(), memberCardService.getTerm()));

      if (!Long.valueOf("-1").equals(memberService.getDeadline()) && !com.bcgogo.utils.NumberUtil.isEqualNegativeOne(
          memberCardService.getTerm())) {
        memberCardOrderServiceDTO.setDeadline(
            DateUtil.getDeadline(memberService.getDeadline(), memberCardService.getTerm()));
      }
      if (Long.valueOf("-1").equals(memberCardOrderServiceDTO.getDeadline())) {
        memberCardOrderServiceDTO.setDeadlineStr(DeadlineLimitType.UNLIMITED.getType());
      }
      else {
        String deadlineStr = DateUtil.convertDateLongToDateString(DateUtil.YEAR_MONTH_DATE,
                                                                  memberCardOrderServiceDTO.getDeadline());
        memberCardOrderServiceDTO.setDeadlineStr(deadlineStr);
      }
    }
    if (null != memberService && null == memberCardService) {
      if (memberService.getDeadline() < System.currentTimeMillis()) {
        memberCardOrderServiceDTO.setOldDeadlineStr(
            DateUtil.convertDateLongToDateString(DateUtil.YEAR_MONTH_DATE, System.currentTimeMillis()));
      }
      else {
        memberCardOrderServiceDTO.setOldDeadlineStr(
            DateUtil.convertDateLongToDateString(DateUtil.YEAR_MONTH_DATE, memberService.getDeadline()));
      }

      memberCardOrderServiceDTO.setServiceName(serviceName);
      memberCardOrderServiceDTO.setOldTimes(memberService.getTimes());
      memberCardOrderServiceDTO.setBalanceTimes(memberService.getTimes());
      memberCardOrderServiceDTO.setIncreasedTimes(0);
      memberCardOrderServiceDTO.setCardTimes(0);
      memberCardOrderServiceDTO.setCardTimesStatus(TimesStatus.LIMITED.getStatus());
      memberCardOrderServiceDTO.setVehicles(memberService.getVehicles());
      memberCardOrderServiceDTO.setServiceId(memberService.getServiceId());
      memberCardOrderServiceDTO.setDeadline(memberService.getDeadline());
      if (-1 == memberService.getDeadline()) {
        memberCardOrderServiceDTO.setDeadlineStr(DeadlineLimitType.UNLIMITED.getType());
      }
      else {
        String dateStr = DateUtil.dateLongToStr(memberService.getDeadline(), DateUtil.YEAR_MONTH_DATE);
        memberCardOrderServiceDTO.setDeadlineStr(dateStr);
      }

    }
    if (null == memberService && null != memberCardService) {
      memberCardOrderServiceDTO.setOldDeadlineStr(
          DateUtil.convertDateLongToDateString(DateUtil.YEAR_MONTH_DATE, System.currentTimeMillis()));

      memberCardOrderServiceDTO.setServiceName(serviceName);
      memberCardOrderServiceDTO.setOldTimes(0);
      memberCardOrderServiceDTO.setIncreasedTimes(memberCardService.getTimes());
      memberCardOrderServiceDTO.setBalanceTimes(memberCardService.getTimes());
      memberCardOrderServiceDTO.setCardTimes(memberCardService.getTimes());
      memberCardOrderServiceDTO.setCardTimesStatus(TimesStatus.UNLIMITED.getStatus());
      Long date = System.currentTimeMillis();
      String dateStr = DateUtil.dateLongToStr(date, DateUtil.YEAR_MONTH_DATE);
      date = DateUtil.convertDateStringToDateLong(DateUtil.YEAR_MONTH_DATE, dateStr);

      memberCardOrderServiceDTO.setDeadlineStr(dateStr);
      memberCardOrderServiceDTO.setDeadline(
          DateUtil.getDeadline(System.currentTimeMillis(), memberCardService.getTerm()));
      memberCardOrderServiceDTO.setTerm(0);

      if (Long.valueOf("-1").equals(memberCardOrderServiceDTO.getDeadline())) {
        memberCardOrderServiceDTO.setDeadlineStr(DeadlineLimitType.UNLIMITED.getType());
      }
      else {
        String deadlineStr = DateUtil.convertDateLongToDateString(DateUtil.YEAR_MONTH_DATE,
                                                                  memberCardOrderServiceDTO.getDeadline());
        memberCardOrderServiceDTO.setDeadlineStr(deadlineStr);
      }
    }
    if (null == memberService && null == memberCardService) {
      serviceDTO = txnService.getServiceById(serviceId);

      if (null != serviceDTO) {
        serviceName = serviceDTO.getName();
      }
      memberCardOrderServiceDTO.setOldDeadlineStr(
          DateUtil.convertDateLongToDateString(DateUtil.YEAR_MONTH_DATE, System.currentTimeMillis()));
      memberCardOrderServiceDTO.setServiceName(serviceName);
      memberCardOrderServiceDTO.setOldTimes(0);
      memberCardOrderServiceDTO.setIncreasedTimes(0);
      memberCardOrderServiceDTO.setBalanceTimes(0);
      memberCardOrderServiceDTO.setCardTimes(0);

      Long date = System.currentTimeMillis();

      date = DateUtil.getDeadline(date, 12);

      String dateStr = DateUtil.convertDateLongToDateString(DateUtil.YEAR_MONTH_DATE, date);

      memberCardOrderServiceDTO.setDeadlineStr(dateStr);
      memberCardOrderServiceDTO.setDeadline(date);
      memberCardOrderServiceDTO.setTerm(0);
    }

    jsonStr = JsonUtil.objectToJson(memberCardOrderServiceDTO);

    PrintWriter writer = response.getWriter();
    writer.write(jsonStr);
    writer.close();
  }

  @RequestMapping(params = "method=getMemberByCustomerId")
  @ResponseBody
  public Object getMemberByCustomerId(HttpServletRequest request,Long customerId) throws Exception {
    if(customerId==null){
      return new MemberDTO();
    }
    try{
      return ServiceManager.getService(IMembersService.class).getMemberByCustomerId(WebUtil.getShopId(request),customerId);
    }catch (Exception e){
      LOG.error(e.getMessage(),e);
      return null;
    }
  }

  @RequestMapping(params = "method=getVehiclesAndMobile")
  public void getVehiclesAndMobile(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response, Long customerId) throws Exception {
    IUserService userService = ServiceManager.getService(IUserService.class);
    ICustomerService customerService = ServiceManager.getService(ICustomerService.class);
    List<MemberDTO> memberDTOs = new ArrayList<MemberDTO>();
    String jsonStr = "";
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    CustomerDTO customerDTO = customerService.getCustomerById(customerId);
    if (null != customerDTO && StringUtils.isNotBlank(customerDTO.getMobile())) {
      MemberDTO memberDTO = new MemberDTO();
      memberDTO.setMemberNo(customerDTO.getMobile());
      memberDTOs.add(memberDTO);
    }
    List<CarDTO> carDTOs = userService.getVehiclesByCustomerId(shopId, customerId);
    for (CarDTO carDTO : carDTOs) {
      MemberDTO memberDTO = new MemberDTO();
      memberDTO.setMemberNo(carDTO.getLicenceNo());
      memberDTOs.add(memberDTO);
    }

    jsonStr = JsonUtil.listToJson(memberDTOs);
    PrintWriter writer = response.getWriter();
    writer.write(jsonStr);
    writer.close();
  }

  @RequestMapping(params = "method=getExecutor")
  public void getExecutor(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) throws Exception {
    IUserService userService = ServiceManager.getService(IUserService.class);
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    String jsonStr = "";
    List<UserDTO> userDTOs = userService.getShopUser(shopId);
    jsonStr = JsonUtil.listToJson(userDTOs);
    PrintWriter writer = response.getWriter();
    writer.write(jsonStr);
    writer.close();
  }

  @RequestMapping(params = "method=getMemberInfo")
  public void getMemberInfo(HttpServletRequest request, HttpServletResponse response, Long customerId) throws Exception {
    IMembersService membersService = ServiceManager.getService(IMembersService.class);
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    MemberDTO memberDTO = null;
    PrintWriter out = response.getWriter();
    String jsonStr = "";
    try {
      if (null != customerId) {
        memberDTO = membersService.getMemberByCustomerId(shopId, customerId);
        if (null != memberDTO) {
          memberDTO.setStatus(membersService.getMemberStatusByMemberDTO(memberDTO));
          memberDTO.setStatusStr(memberDTO.getStatus().getStatus());
          if (CollectionUtils.isNotEmpty(memberDTO.getMemberServiceDTOs())) {
            for (MemberServiceDTO memberServiceDTO : memberDTO.getMemberServiceDTOs()) {
              Service service = ServiceManager.getService(RFITxnService.class).getServiceById(memberServiceDTO.getServiceId());
              if (service != null) {
                memberServiceDTO.setServiceName(service.getName());
              }
            }
          }
        }
      }

      jsonStr = JsonUtil.objectToJson(memberDTO);
      out.write(jsonStr);
    } catch (Exception e) {
      LOG.debug("method=getMemberInfo");
      LOG.debug("customerId", customerId);
      LOG.debug(e.getMessage(), e);
    } finally {
      out.flush();
      out.close();
    }
  }

  private VelocityEngine createVelocityEngine() throws Exception {
    //初始化并取得Velocity引擎
    VelocityEngine ve = new VelocityEngine();
    ve.setProperty(VelocityEngine.RESOURCE_LOADER, "string");
    ve.setProperty("string.resource.loader.class", "org.apache.velocity.runtime.resource.loader.StringResourceLoader");
    ve.setProperty("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.SimpleLog4JLogSystem");
    ve.setProperty("runtime.log.logsystem.log4j.category", "velocity");
    ve.setProperty("runtime.log.logsystem.log4j.logger", "velocity");
    ve.init();
    return ve;
  }

  @RequestMapping(params = "method=checkNoMemberCard")
  public void checkNoMemberCard(HttpServletRequest request, HttpServletResponse response) throws Exception {
    IMembersService membersService = ServiceManager.getService(IMembersService.class);

    Long shopId = (Long) request.getSession().getAttribute("shopId");

    PrintWriter out = response.getWriter();

    Map<String, String> map = new HashMap<String, String>();

    try {
      int total = membersService.countMemberCardByShopId(shopId);

      if (total > 0) {
        map.put("resu", "success");
      }
      else {
        map.put("resu", "error");
      }

      out.write(JsonUtil.mapToJson(map));

    } catch (Exception e) {
      LOG.error("method=checkNoMemberCard");
      LOG.error("shopId", shopId);
      LOG.error(e.getMessage(), e);
    } finally {
      out.close();
    }
  }

  @RequestMapping(params = "method=checkMobileDifferentCustomer")
  public void checkMobileDifferentCustomer(HttpServletRequest request, HttpServletResponse response, Long customerId, String mobile) throws Exception {
    IUserService userService = ServiceManager.getService(IUserService.class);
    PrintWriter out = response.getWriter();
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    String jsonStr = "";
    try {
      List<CustomerDTO> customerDTOList = userService.getCustomerByMobile(shopId, mobile);

      if (CollectionUtils.isEmpty(customerDTOList)) {
        jsonStr = "noCustomer";
      }
      else {
        if (customerDTOList.size() == 1 && !customerDTOList.get(0).getId().equals(customerId)) {
          jsonStr = "hasCustomer";
        }
        else if (customerDTOList.size() == 1 && customerDTOList.get(0).getId().equals(customerId)) {
          jsonStr = "noCustomer";
        }
        else if (customerDTOList.size() > 1) {
          jsonStr = "hasCustomerGtOne";
        }
      }

      Map<String, String> map = new HashMap<String, String>();
      map.put("resu", jsonStr);
      out.write(JsonUtil.mapToJson(map));
    } catch (Exception e) {
      LOG.error("method=checkMobileDifferentCustomer");
      LOG.error("customerID", customerId);
      LOG.error("mobile", mobile);
      LOG.error(e.getMessage(), e);
    } finally {
      out.flush();
      out.close();
    }
  }

  @RequestMapping(params = "method=ajaxGetCustomerWithMember")
  @ResponseBody
  public CustomerDTO ajaxGetMemberInfo(@RequestParam String memberNo, HttpServletRequest request) throws Exception {
    Long shopId = WebUtil.getShopId(request);
    IUserService userService = ServiceManager.getService(IUserService.class);
    CustomerDTO customerDTO = userService.getCustomerWithMemberByMemberNoShopId(memberNo, shopId);
    if (null == customerDTO) {
      customerDTO = new CustomerDTO();
    }
    return customerDTO;
  }

  /**
   * 根据会员号密码  消费金额判断会员余额是否充足
   *
   * @param modelMap
   * @param request
   * @param response
   * @param memberNo
   * @param memberPasswordStr
   * @param memberAmountStr
   */
  @RequestMapping(params = "method=checkMemberBalance")
  public void checkMemberBalance(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response, String memberNo, String memberPasswordStr, String memberAmountStr) {

    IMemberCheckerService memberCheckerService = ServiceManager.getService(IMemberCheckerService.class);
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    String jsonStr = "";
    try {
      jsonStr = memberCheckerService.checkMemberBalance(shopId, memberNo, memberPasswordStr, memberAmountStr);
    } catch (Exception e) {
      LOG.error("/member.do");
      LOG.error("method=checkMemberBalance");
      LOG.error(
          "shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute(
              "userId"));
      LOG.error(e.getMessage(), e);

      jsonStr = MemberConstant.AJAX_SUBMIT_FAILURE;
    }
    try {
      Map<String, String> jsonMap = new HashMap();
      jsonMap.put(MemberConstant.CHECK_RESULT, jsonStr);
      jsonStr = JsonUtil.mapToJson(jsonMap);

      PrintWriter writer = response.getWriter();
      writer.write(jsonStr);
      writer.close();
    } catch (Exception e) {
      LOG.error("/member.do");
      LOG.error("method=checkMemberBalance");
      LOG.error(
          "shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute(
              "userId"));
      LOG.error(e.getMessage(), e);
    }
  }

  /**
   * 删除客户判断此客户会员号上时候有余额
   *
   * @param request
   * @param response
   * @throws Exception
   */
  @RequestMapping(params = "method=checkMemberBalanceExist")
  public void checkMemberBalanceExist(HttpServletRequest request, HttpServletResponse response) throws Exception {
    PrintWriter out = response.getWriter();
    IMembersService membersService = ServiceManager.getService(IMembersService.class);
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    String jsonStr = "";
    String customerIdStr = request.getParameter("customerId");
    if (StringUtils.isBlank(customerIdStr)) {
      return;
    }
    try {
      MemberDTO memberDTO = membersService.getMemberByCustomerId(shopId, Long.valueOf(customerIdStr));
      if (null != memberDTO && null != memberDTO.getBalance() && memberDTO.getBalance().doubleValue() > 0) {
        jsonStr = "error";
      }
      if(memberDTO!=null&&!MemberStatus.DISABLED.equals(memberDTO.getStatus())&&!jsonStr.equals("error")){
        List<MemberService> memberServices = membersService.getMemberServicesByMemberId(memberDTO.getId());
        if(CollectionUtils.isNotEmpty(memberServices)){
          for(MemberService memberService:memberServices){
            if(memberService==null){
              continue;
            }
            Long deadLine=memberService.getDeadline();
            Integer times=memberService.getTimes();
            if(times!=null){
              if(times==-1||times>0&&(deadLine!=null&&(deadLine==-1||deadLine>System.currentTimeMillis()))){
                jsonStr = "error";
                break;
              }
            }
          }
        }
      }
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    } finally {
      Map<String, String> map = new HashMap<String, String>();
      map.put("resu", jsonStr);
      out.write(JsonUtil.mapToJson(map));
      out.flush();
      out.close();
    }
  }

  @RequestMapping(params = "method=returnCard")
  public String returnCard(ModelMap model, HttpServletRequest request, Long customerId,String pageLinkedFrom) {
    IMembersService membersService = ServiceManager.getService(IMembersService.class);
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    ICustomerService customerService = ServiceManager.getService(ICustomerService.class);
    MemberDTO memberDTO = null;
    Long shopId = WebUtil.getShopId(request);
    MemberCardDTO memberCardDTO = null;
    if (customerId == null) {
      return "/customer/returnCard";
    }
    try {
      CustomerDTO customerDTO = customerService.getCustomerById(customerId);
      memberDTO = membersService.getMemberByCustomerId(shopId, customerId);
      if (memberDTO != null) {
        memberDTO.setBalance(NumberUtil.round(memberDTO.getBalance(), NumberUtil.MONEY_PRECISION));
      }

      MemberCardOrderDTO memberCardOrderDTO = txnService.getLatestMemberCardOrder(shopId, customerId);
      MemberCardReturnDTO memberCardReturnDTO = new MemberCardReturnDTO();
      memberCardReturnDTO.setShopId(shopId);
      memberCardReturnDTO.setMemberBalance(memberDTO.getBalance());
      memberCardReturnDTO.setTotal(0D);
      if (memberCardOrderDTO != null) {
        List<MemberCardOrderServiceDTO> memberCardOrderServiceDTOs = txnService.getMemberCardOrderServiceDTOByOrderId(
            shopId, memberCardOrderDTO.getId());
        Map<Long, MemberCardOrderServiceDTO> memberCardOrderServiceDTOMap = new HashMap<Long, MemberCardOrderServiceDTO>();
        if (CollectionUtils.isNotEmpty(memberCardOrderServiceDTOs)) {
          for (MemberCardOrderServiceDTO memberCardOrderServiceDTO : memberCardOrderServiceDTOs) {
            memberCardOrderServiceDTOMap.put(memberCardOrderServiceDTO.getServiceId(), memberCardOrderServiceDTO);
          }
        }

        memberCardReturnDTO.setLastBuyTotal(memberCardOrderDTO.getTotal());
        memberCardReturnDTO.setLastBuyDate(memberCardOrderDTO.getVestDate());
        memberCardReturnDTO.setLastRecharge(memberCardOrderDTO.getWorth());
        memberCardReturnDTO.setLastMemberCardOrderId(memberCardOrderDTO.getId());
        memberCardReturnDTO.setTotal(memberCardOrderDTO.getTotal());    //退卡金额默认等于上次购卡金额

        List<MemberCardOrderItemDTO> memberCardOrderItemDTOs = txnService.getMemberCardOrderItemDTOByOrderId(shopId,
            memberCardOrderDTO.getId());
        List<MemberCardReturnItemDTO> memberCardReturnItemDTOs = new ArrayList<MemberCardReturnItemDTO>();
        MemberCardReturnItemDTO memberCardReturnItemDTO = new MemberCardReturnItemDTO();
        if (CollectionUtils.isNotEmpty(memberCardOrderItemDTOs)) {
          MemberCardOrderItemDTO memberCardOrderItemDTO = memberCardOrderItemDTOs.get(0);
          memberCardReturnItemDTO.setLastRecharge(memberCardOrderDTO.getWorth());
          memberCardReturnItemDTO.setMemberBalance(memberDTO.getBalance());
          memberCardReturnItemDTO.setSalesId(memberCardOrderItemDTO.getSalesId());
          if (memberCardOrderItemDTO.getSalesId() != null) {
            SalesManDTO salesManDTO = ServiceManager.getService(IUserService.class).getSalesManDTOById(
                memberCardOrderItemDTO.getSalesId());
            memberCardReturnDTO.setSalesMan(salesManDTO == null ? "" : salesManDTO.getName());
          }
        }
        memberCardReturnItemDTOs.add(memberCardReturnItemDTO);
        memberCardReturnDTO.setMemberCardReturnItemDTOs(memberCardReturnItemDTOs);

        List<MemberCardReturnServiceDTO> memberCardReturnServiceDTOs = new ArrayList<MemberCardReturnServiceDTO>();
        if (CollectionUtils.isNotEmpty(memberDTO.getMemberServiceDTOs())) {
          for (MemberServiceDTO memberServiceDTO : memberDTO.getMemberServiceDTOs()) {
            MemberCardReturnServiceDTO memberCardReturnServiceDTO = new MemberCardReturnServiceDTO();
            memberCardReturnServiceDTO.setShopId(shopId);
            memberCardReturnServiceDTO.setServiceId(memberServiceDTO.getServiceId());
            ServiceDTO serviceDTO = txnService.getServiceById(memberServiceDTO.getServiceId());
            if (null != serviceDTO && null != serviceDTO.getName()) {
              memberCardReturnServiceDTO.setServiceName(serviceDTO.getName());
            }
            memberCardReturnServiceDTO.setRemainTimes(memberServiceDTO.getTimes());
            MemberCardOrderServiceDTO memberCardOrderServiceDTO = memberCardOrderServiceDTOMap.get(
                memberServiceDTO.getServiceId());
            if (memberCardOrderServiceDTO != null) {
              memberCardReturnServiceDTO.setLastBuyTimes(memberCardOrderServiceDTO.getIncreasedTimes());
              memberCardReturnServiceDTO.setLastBuyTimesLimitType(memberCardOrderServiceDTO.getBalanceTimesLimitType());
            }
            else {
              memberCardReturnServiceDTO.setLastBuyTimes(0);
            }

            memberCardReturnServiceDTOs.add(memberCardReturnServiceDTO);
          }
        }
        memberCardReturnDTO.setMemberCardReturnServiceDTOs(memberCardReturnServiceDTOs);
      }else{
        List<MemberCardReturnItemDTO> memberCardReturnItemDTOs = new ArrayList<MemberCardReturnItemDTO>();
        MemberCardReturnItemDTO memberCardReturnItemDTO = new MemberCardReturnItemDTO();

        memberCardReturnItemDTO.setMemberBalance(memberDTO.getBalance());

        memberCardReturnItemDTOs.add(memberCardReturnItemDTO);
        memberCardReturnDTO.setMemberCardReturnItemDTOs(memberCardReturnItemDTOs);

        List<MemberCardReturnServiceDTO> memberCardReturnServiceDTOs = new ArrayList<MemberCardReturnServiceDTO>();
        if (CollectionUtils.isNotEmpty(memberDTO.getMemberServiceDTOs())) {
          for (MemberServiceDTO memberServiceDTO : memberDTO.getMemberServiceDTOs()) {
            MemberCardReturnServiceDTO memberCardReturnServiceDTO = new MemberCardReturnServiceDTO();
            memberCardReturnServiceDTO.setShopId(shopId);
            memberCardReturnServiceDTO.setServiceId(memberServiceDTO.getServiceId());
            ServiceDTO serviceDTO = txnService.getServiceById(memberServiceDTO.getServiceId());
            if (null != serviceDTO && null != serviceDTO.getName()) {
              memberCardReturnServiceDTO.setServiceName(serviceDTO.getName());
            }
            memberCardReturnServiceDTO.setRemainTimes(memberServiceDTO.getTimes());

            memberCardReturnServiceDTO.setLastBuyTimes(0);

            memberCardReturnServiceDTOs.add(memberCardReturnServiceDTO);
          }
        }
        memberCardReturnDTO.setMemberCardReturnServiceDTOs(memberCardReturnServiceDTOs);
      }


      model.addAttribute("pageLinkedFrom",pageLinkedFrom);
      model.addAttribute("memberDTO", memberDTO);
      model.addAttribute("customerDTO", customerDTO);
      model.addAttribute("memberCardReturnDTO", memberCardReturnDTO);

    } catch (Exception e) {
      LOG.error("会员卡退卡创建时出错. customerID: {}", customerId);
      LOG.error(e.getMessage(), e);
    }
    return "/customer/returnCard";
  }

  @RequestMapping(params = "method=saveReturnCard")
  @ResponseBody
  public Map saveReturnCard(ModelMap model, HttpServletRequest request, MemberCardReturnDTO memberCardReturnDTO) {
    if (memberCardReturnDTO == null || memberCardReturnDTO.getCustomerId() == null || memberCardReturnDTO.getReceptionRecordDTO() == null) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("result", "fail");
      return map;
    }
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    IOrderIndexService orderIndexService = ServiceManager.getService(IOrderIndexService.class);
    IMembersService membersService = ServiceManager.getService(IMembersService.class);
    ICustomerService customerService = ServiceManager.getService(ICustomerService.class);
    StringBuffer sb = new StringBuffer("");
    //txn事务保存成功标志
    boolean txnTransSuccess = false;
    //user事务保存成功标志
    boolean userTransSuccess = false;
    //search事务保存成功标志
    boolean searchTransSuccess = false;
    Long shopId = WebUtil.getShopId(request);
    try {
      Long returnDate = System.currentTimeMillis();
      String returnDateStr = DateUtil.dateLongToStr(returnDate, DateUtil.YEAR_MONTH_DATE);
      memberCardReturnDTO.setReturnDate(returnDate);
      memberCardReturnDTO.setReturnDateStr(returnDateStr);
      memberCardReturnDTO.setShopId(shopId);
      memberCardReturnDTO.setTotal(memberCardReturnDTO.getReceptionRecordDTO().getAmount());
      CustomerDTO customerDTO = customerService.getCustomerById(memberCardReturnDTO.getCustomerId());
      memberCardReturnDTO.setCustomerDTO(customerDTO);
      memberCardReturnDTO.setUserId(WebUtil.getUserId(request));
      memberCardReturnDTO.setUserName(WebUtil.getUserName(request));

      //保存membercardorder,membercardorderitem,membercardorderservice,receivable,receptionrecord,debt表
      memberCardReturnDTO = txnService.saveMemberCardReturn(memberCardReturnDTO);
      model.addAttribute("memberCardReturnDTO", memberCardReturnDTO);
      //txnTransSuccess保存成功
      txnTransSuccess = true;

      //ad by WLF 保存会员退卡的日志
      OperationLogDTO operationLogDTO = new OperationLogDTO();
      operationLogDTO.setShopId(shopId);
      operationLogDTO.setUserId((Long)request.getSession().getAttribute("userId"));
      operationLogDTO.setObjectId(memberCardReturnDTO.getId());
      operationLogDTO.setObjectType(ObjectTypes.MEMBER_CARD_RETURN_ORDER);
      operationLogDTO.setOperationType(OperationTypes.CREATE);
      ServiceManager.getService(ITxnService.class).saveOperationLogTxnService(operationLogDTO);

      membersService.invalidMember(shopId, memberCardReturnDTO.getMemberDTO().getId());
      //userTransSuccess保存成功
      userTransSuccess = true;

      orderIndexService.saveOrderIndexAndItemIndexOfMemberCardReturn(memberCardReturnDTO);
      //searchTransSuccess保存成功
      searchTransSuccess = true;

      //会员购卡续卡营业统计  reindex
      BcgogoEventPublisher bcgogoEventPublisher = new BcgogoEventPublisher();
      MemberCardReturnSavedEvent memberCardReturnSavedEvent = new MemberCardReturnSavedEvent(memberCardReturnDTO);
      bcgogoEventPublisher.publisherMemberCardReturnSaved(memberCardReturnSavedEvent);
    } catch (Exception e) {
      LOG.debug("method=saveReturnCard");
      LOG.debug("memberCardReturnDTO{}", memberCardReturnDTO);
      if (!txnTransSuccess) {
        LOG.debug("txn数据库保存失败");
      }
      if (!userTransSuccess) {
        LOG.debug("user数据库保存失败");
      }
      if (!searchTransSuccess) {
        LOG.debug("search数据库保存失败");
      }
      LOG.error(e.getMessage(), e);
    } finally {
      if (userTransSuccess) {
        sb.append("success");
      }
      else {
        sb.append("error");
      }
      String jsonStr = sb.toString();
      Map<String, String> jsonMap = new HashMap();
      jsonMap.put("resu", jsonStr);
      jsonMap.put("orderId", null == memberCardReturnDTO.getId() ? "" : memberCardReturnDTO.getId().toString());
      return jsonMap;
    }
  }

  @RequestMapping(params = "method=printMemberReturn")
  public void printMemberReturn(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response, Long orderId) throws Exception {
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    IUserService userService = ServiceManager.getService(IUserService.class);
    IPrintService printService = ServiceManager.getService(IPrintService.class);
    PrintWriter out = response.getWriter();
    response.setContentType("text/html");
    response.setCharacterEncoding("UTF-8");

    Long shopId = WebUtil.getShopId(request);
    MemberCardReturnDTO memberCardReturnDTO = txnService.getMemberCardReturnDTOById(shopId, orderId);
    List<ReceptionRecordDTO> receptionRecordDTOs = txnService.getReceptionRecordByOrderId(shopId, orderId, null);
    if (CollectionUtils.isNotEmpty(receptionRecordDTOs)) {
      memberCardReturnDTO.setReceptionRecordDTO(receptionRecordDTOs.get(0));
    }
    ShopDTO shopDTO = ServiceManager.getService(IConfigService.class).getShopById(shopId);
    UserDTO userDTO = userService.getUserByUserId(memberCardReturnDTO.getExecutorId());
    Customer customer = userService.getCustomerByCustomerId(memberCardReturnDTO.getCustomerId(), shopId);
    memberCardReturnDTO.setCustomerName(customer == null ? "" : customer.getName());

    if (CollectionUtils.isNotEmpty(memberCardReturnDTO.getMemberCardReturnItemDTOs()) &&
        memberCardReturnDTO.getMemberCardReturnItemDTOs().get(0).getSalesId() != null) {
      SalesManDTO salesManDTO = userService.getSalesManDTOById(
          memberCardReturnDTO.getMemberCardReturnItemDTOs().get(0).getSalesId());
      memberCardReturnDTO.setSalesMan(salesManDTO == null ? "" : salesManDTO.getName());
    }

    try {
      PrintTemplateDTO printTemplateDTO = printService.getSinglePrintTemplateDTOByShopIdAndType(RFCommon.getShopId(request),
          OrderTypes.MEMBER_RETURN_CARD);

      if (null != printTemplateDTO) {
        byte bytes[] = printTemplateDTO.getTemplateHtml();
        String myTemplate = new String(bytes, "UTF-8");

        //获取VelocityEngine
        VelocityEngine ve = createVelocityEngine();
        //创建资源库
        StringResourceRepository repo = StringResourceLoader.getRepository();

        String myTemplateName = "memberCardReturnTemplate" + String.valueOf(RFCommon.getShopId(request));

        //模板资源存放 资源库 中
        repo.putStringResource(myTemplateName, myTemplate);

        //取得velocity的模版
        Template t = ve.getTemplate(myTemplateName, "UTF-8");
        //取得velocity的上下文context
        VelocityContext context = new VelocityContext();

        //把数据填入上下文
        context.put("executor", userDTO == null ? "" : userDTO.getName());
        context.put("memberCardReturnDTO", memberCardReturnDTO);
        context.put("shopName", shopDTO.getName());
        //输出流
        StringWriter writer = new StringWriter();

        //转换输出
        t.merge(context, writer);
        out.print(writer);
        writer.close();
      }
      else {
        out.print(TxnConstant.NO_PRINT_TEMPLATE);
      }
    } catch (Exception e) {
      LOG.debug("printMemberReturn出错。");
      LOG.debug(e.getMessage(), e);
    } finally {
      out.flush();
      out.close();
    }
  }


  @RequestMapping(params = "method=memberStat")
  public String memberStat(ModelMap model, HttpServletRequest request) throws Exception {
    try {
      String startTimeStr = DateUtil.getFirtDayOfMonth();
      String endTimeStr = DateUtil.convertDateLongToString(System.currentTimeMillis(), DateUtil.DATE_STRING_FORMAT_DAY);
      model.addAttribute("startTimeStr", startTimeStr);
      model.addAttribute("endTimeStr", endTimeStr);
    } catch (Exception e) {
      LOG.error("/member.do method=memberStat");
      LOG.error(
          "shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute(
              "userId"));
      LOG.error(e.getMessage(), e);
    }
    return "stat/memberStatistics";
  }

  @ResponseBody
  @RequestMapping(params = "method=getMemberStatData")
  public Object getMemberStatData(HttpServletRequest request, OrderSearchConditionDTO orderSearchConditionDTO, int startPageNo, int maxRows) {

    try {
      Long shopId = (Long) request.getSession().getAttribute("shopId");
      if (NumberUtil.longValue(shopId) < 0) {
        return "/";
      }
      orderSearchConditionDTO.verificationQueryTime();
      orderSearchConditionDTO.setShopId(shopId);

      return null;
    } catch (Exception e) {
      LOG.error("memberController.java method=getMemberStatData");
      LOG.error(
          "shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute(
              "userId"));
      LOG.error(e.getMessage(), e);
    }
    return null;
  }

  @ResponseBody
  @RequestMapping(params = "method=getMemberCardOrder")
  public Object getMemberCardOrder(HttpServletRequest request, OrderSearchConditionDTO orderSearchConditionDTO, int startPageNo, int maxRows) {

    try {
      ITxnService txnService = ServiceManager.getService(ITxnService.class);
      IBusinessStatService businessStatService = ServiceManager.getService(IBusinessStatService.class);
      IMembersService membersService = ServiceManager.getService(IMembersService.class);
      List<Object> returnList = new ArrayList<Object>();
      Long shopId = (Long) request.getSession().getAttribute("shopId");
      if (NumberUtil.longValue(shopId) < 0) {
        return "/";
      }
      MemberStatResultDTO memberStatResultDTO = new MemberStatResultDTO();
      orderSearchConditionDTO.setShopId(shopId);
      Long startTime = orderSearchConditionDTO.getStartTime();
      Long endTime = orderSearchConditionDTO.getEndTime();
      if (startTime == null || endTime == null) {
        startTime = DateUtil.getFirstDayDateTimeOfMonth();
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.setTimeInMillis(startTime);
        calendar.add(Calendar.MONTH, 1);
        endTime = calendar.getTimeInMillis();
      }
      orderSearchConditionDTO.setMemberNo(null);
      if (StringUtil.isEmpty(orderSearchConditionDTO.getCustomerName()) && StringUtil.isEmpty(
          orderSearchConditionDTO.getVehicle())
          && StringUtil.isEmpty(orderSearchConditionDTO.getAccountMemberNo())) {
        orderSearchConditionDTO.setCustomerOrSupplierIds(null);
      }
      else {
        membersService.setCustomerIdByVehicleMemberName(orderSearchConditionDTO);
        if (ArrayUtils.isEmpty(orderSearchConditionDTO.getCustomerOrSupplierIds())) {
          returnList.add(memberStatResultDTO);
          returnList.add(new Pager(0, startPageNo, maxRows));
          return returnList;
        }
      }
      int size = 0;
      List<String> stringList = new ArrayList<String>();
      try {
        stringList = txnService.getMemberOrderCountAndSum(shopId, startTime, endTime, orderSearchConditionDTO);
      } catch (Exception e) {
        LOG.debug("获取会员列表出错 shopId:" + request.getSession().getAttribute(
            "shopId") + ",userId:" + request.getSession().getAttribute("userId"));
        LOG.error(e.getMessage(), e);
      }

      if (stringList != null && stringList.size() == StatConstant.RESULT_SIZE) {
        size = (NumberUtil.intValue(stringList.get(0)));
        memberStatResultDTO.setResultTotal(NumberUtil.doubleValue(stringList.get(1), 0));
        memberStatResultDTO.setResultSettledAmount(NumberUtil.doubleValue(stringList.get(2), 0));
      }
      //分页
      Pager pager = statUtil.getPager(size, startPageNo, maxRows);
      //根据开始时间、结束时间、排序类型 分页组建 获得单据列表
      if (size > 0) {
        memberStatResultDTO = businessStatService.getMemberCardOrderDTOList(shopId, startTime, endTime,
                                                                            " order by created desc", pager,
                                                                            memberStatResultDTO,
                                                                            orderSearchConditionDTO);
      }
      returnList.add(memberStatResultDTO);

      if(StringUtils.isBlank(orderSearchConditionDTO.getStartTimeStr()))
      {
        pager.setStartDateStr(DateUtil.convertDateLongToDateString(DateUtil.YEAR_MONTH_DATE,startTime));
      }
      else
      {
        pager.setStartDateStr(orderSearchConditionDTO.getStartTimeStr());
      }
      if(StringUtils.isBlank(orderSearchConditionDTO.getEndTimeStr()))
      {
        pager.setEndDateStr(DateUtil.convertDateLongToDateString(DateUtil.YEAR_MONTH_DATE,endTime-1L));
      }
      else
      {
        pager.setEndDateStr(orderSearchConditionDTO.getEndTimeStr());
      }
      returnList.add(pager);
      return returnList;
    } catch (Exception e) {
      LOG.error("member.do getMemberCardOrder shopId:" + request.getSession().getAttribute("shopId"));
     LOG.error(e.getMessage(), e);
    }
    return null;
  }

  @ResponseBody
  @RequestMapping(params = "method=getMemberReturnOrder")
  public Object getMemberReturnOrder(HttpServletRequest request, OrderSearchConditionDTO orderSearchConditionDTO, int startPageNo, int maxRows) {
    try {
      ITxnService txnService = ServiceManager.getService(ITxnService.class);
      IBusinessStatService businessStatService = ServiceManager.getService(IBusinessStatService.class);
      IMembersService membersService = ServiceManager.getService(IMembersService.class);
      List<Object> returnList = new ArrayList<Object>();
      Long shopId = (Long) request.getSession().getAttribute("shopId");
      if (NumberUtil.longValue(shopId) < 0) {
        return "/";
      }

      //返回结果
      MemberStatResultDTO memberStatResultDTO = new MemberStatResultDTO();

      orderSearchConditionDTO.setShopId(shopId);
      Long startTime = orderSearchConditionDTO.getStartTime();
      Long endTime = orderSearchConditionDTO.getEndTime();
      if (startTime == null || endTime == null) {
        startTime = DateUtil.getFirstDayDateTimeOfMonth();
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.setTimeInMillis(startTime);
        calendar.add(Calendar.MONTH, 1);
        endTime = calendar.getTimeInMillis();
      }
      int size = 0;
      orderSearchConditionDTO.setMemberNo(null);
      if (com.bcgogo.utils.StringUtil.isEmpty(
          orderSearchConditionDTO.getCustomerName()) && com.bcgogo.utils.StringUtil.isEmpty(
          orderSearchConditionDTO.getVehicle())
          && com.bcgogo.utils.StringUtil.isEmpty(orderSearchConditionDTO.getAccountMemberNo())) {
        orderSearchConditionDTO.setCustomerOrSupplierIds(null);
      }
      else {
        membersService.setCustomerIdByVehicleMemberName(orderSearchConditionDTO);
        if (ArrayUtils.isEmpty(orderSearchConditionDTO.getCustomerOrSupplierIds())) {
          returnList.add(memberStatResultDTO);
          returnList.add(new Pager(0, startPageNo, maxRows));
          return returnList;
        }
      }

      List<String> stringList = new ArrayList<String>();
      try {
        stringList = txnService.getMemberReturnOrderCountAndSum(shopId, startTime, endTime, orderSearchConditionDTO);
      } catch (Exception e) {
        LOG.debug("获取会员退卡列表出错 shopId:" + request.getSession().getAttribute(
            "shopId") + ",userId:" + request.getSession().getAttribute("userId"));
        LOG.error(e.getMessage(), e);
      }

      if (stringList != null && stringList.size() == StatConstant.TWO_QUERY_SIZE) {
        size = (NumberUtil.intValue(stringList.get(0)));
        memberStatResultDTO.setResultTotal(NumberUtil.doubleValue(stringList.get(1), 0));
      }
      //分页
      Pager pager = statUtil.getPager(size, startPageNo, maxRows);
      //根据开始时间、结束时间、排序类型 分页组建 获得单据列表
      if (size > 0) {
        memberStatResultDTO = businessStatService.getMemberCardReturnDTOList(shopId, startTime, endTime,
                                                                             " order by created desc", pager,
                                                                             memberStatResultDTO,
                                                                             orderSearchConditionDTO);
      }
      returnList.add(memberStatResultDTO);

      if(StringUtils.isBlank(orderSearchConditionDTO.getStartTimeStr()))
      {
        pager.setStartDateStr(DateUtil.convertDateLongToDateString(DateUtil.YEAR_MONTH_DATE,startTime));
      }
      else
      {
        pager.setStartDateStr(orderSearchConditionDTO.getStartTimeStr());
      }
      if(StringUtils.isBlank(orderSearchConditionDTO.getEndTimeStr()))
      {
        pager.setEndDateStr(DateUtil.convertDateLongToDateString(DateUtil.YEAR_MONTH_DATE,endTime-1L));
      }
      else
      {
        pager.setEndDateStr(orderSearchConditionDTO.getEndTimeStr());
      }
      returnList.add(pager);
      return returnList;
    } catch (Exception e) {
      LOG.error("member.do getMemberReturnOrder shopId:" + (Long) request.getSession().getAttribute("shopId"));
     LOG.error(e.getMessage(), e);
    }
    return null;
  }


  @ResponseBody
  @RequestMapping(params = "method=getMemberConsume")
  public Object getMemberConsume(HttpServletRequest request, OrderSearchConditionDTO orderSearchConditionDTO, int startPageNo, int maxRows) {
    try {
      Long shopId = (Long) request.getSession().getAttribute("shopId");
      if (NumberUtil.longValue(shopId) < 0) {
        return "/";
      }

      //日期转换Long
      Long startTime = null;
      Long endTime = null;
      if(!StringUtil.isEmpty(orderSearchConditionDTO.getStartTimeStr())){
        //开始时间按该天0点算，左闭右开
        startTime = DateUtil.convertDateStringToDateLong("yyyy-MM-dd",orderSearchConditionDTO.getStartTimeStr());
      }
      if(!StringUtil.isEmpty(orderSearchConditionDTO.getStartTimeStr())){
        //结束时间按第二天的0点算，左闭右开
        endTime = DateUtil.convertDateStringToDateLong("yyyy-MM-dd",orderSearchConditionDTO.getStartTimeStr());
        endTime = endTime + 24*3600*1000 - 1;
      }

//      Long startTime = orderSearchConditionDTO.getStartTime();
//      Long endTime = orderSearchConditionDTO.getEndTime();
      if (startTime == null && endTime == null) {
        startTime = DateUtil.getFirstDayDateTimeOfMonth();
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.setTimeInMillis(startTime);
        calendar.add(Calendar.MONTH, 1);
        endTime = calendar.getTimeInMillis();
        orderSearchConditionDTO.setStartTime(startTime);
        orderSearchConditionDTO.setEndTime(endTime);
      }
      if (!StringUtil.isEmpty(orderSearchConditionDTO.getCustomerName())) {
        orderSearchConditionDTO.setCustomerName(orderSearchConditionDTO.getCustomerName().trim());
      }
      if (!StringUtil.isEmpty(orderSearchConditionDTO.getVehicle())) {
        orderSearchConditionDTO.setVehicle(orderSearchConditionDTO.getVehicle().trim());
      }
      if (!StringUtil.isEmpty(orderSearchConditionDTO.getAccountMemberNo())) {
        orderSearchConditionDTO.setAccountMemberNo(orderSearchConditionDTO.getAccountMemberNo().trim());
      }
      orderSearchConditionDTO.setMemberNo(null);
      ISearchOrderService searchOrderService = ServiceManager.getService(ISearchOrderService.class);
      IUserService userService = ServiceManager.getService(IUserService.class);
      List<Object> result = new ArrayList<Object>();
      if (StringUtils.isNotBlank(orderSearchConditionDTO.getCustomerName())) {
        List<CustomerDTO> customerDTOList = userService.getAllCustomerByName(shopId,orderSearchConditionDTO.getCustomerName());
        Set<String> customerIdSet = new HashSet<String>();
        if (CollectionUtils.isNotEmpty(customerDTOList)) {
          for (CustomerDTO customerDTO : customerDTOList) {
            customerIdSet.add(customerDTO.getId().toString());
          }
        }
        else {
          customerIdSet.add(String.valueOf(StatConstant.DEFAULT_NEGATIVE));
        }
        orderSearchConditionDTO.setCustomerOrSupplierIds(customerIdSet.toArray(new String[customerIdSet.size()]));
        orderSearchConditionDTO.setCustomerOrSupplierName(null);
      }
      orderSearchConditionDTO.setStatType(StatConstant.MEMBER_STATISTICS);
      orderSearchConditionDTO.setShopId(shopId);
      orderSearchConditionDTO.setOrderStatusRepeal(StatConstant.NOT_CONTAIN_REPEAL);
      orderSearchConditionDTO.setSort("created_time desc");
      orderSearchConditionDTO.setRowStart((startPageNo - 1) * maxRows);
      orderSearchConditionDTO.setPageRows(maxRows);

      orderSearchConditionDTO.setSearchStrategy(new String[]{OrderSearchConditionDTO.SEARCHSTRATEGY_STATS, OrderSearchConditionDTO.SEARCHSTRATEGY_CURRENT_PAGE_STATS});
      orderSearchConditionDTO.setOrderType(new String[]{"WASH_BEAUTY", "SALE", "REPAIR", "MEMBER_BUY_CARD","CUSTOMER_STATEMENT_ACCOUNT"});
      orderSearchConditionDTO.setStatsFields(new String[]{"member_balance_pay"});
      orderSearchConditionDTO.setPayMethod(new String[]{"MEMBER_BALANCE_PAY"});
      orderSearchConditionDTO.setPageStatsFields(new String[]{"member_balance_pay"});

      OrderSearchResultListDTO orderSearchResultListDTO = searchOrderService.queryOrders(orderSearchConditionDTO);
      if (CollectionUtils.isNotEmpty(orderSearchResultListDTO.getOrders())) {
        Set<Long> customerIdSet = new HashSet<Long>();
        for (OrderSearchResultDTO order : orderSearchResultListDTO.getOrders()) {
          if ((!OrderTypes.SALE.toString().equals(order.getOrderType()) &&
              !OrderTypes.REPAIR.toString().equals(order.getOrderType()) &&
              !OrderTypes.MEMBER_BUY_CARD.toString().equals(order.getOrderType()) &&
              !OrderTypes.CUSTOMER_STATEMENT_ACCOUNT.toString().equals(order.getOrderType()) &&
              !OrderTypes.WASH_BEAUTY.toString().equals(order.getOrderType()))) {
            continue;
          }
          customerIdSet.add(order.getCustomerOrSupplierId());
        }
        Map<Long, CustomerDTO> customerDTOMap = ServiceManager.getService(ICustomerService.class).getCustomerByIdSet(
            shopId, customerIdSet);
        if (MapUtils.isNotEmpty(customerDTOMap)) {
          CustomerDTO customerDTO = null;
          for (OrderSearchResultDTO order : orderSearchResultListDTO.getOrders()) {
            if ((!OrderTypes.SALE.toString().equals(order.getOrderType()) &&
                !OrderTypes.REPAIR.toString().equals(order.getOrderType()) &&
                !OrderTypes.MEMBER_BUY_CARD.toString().equals(order.getOrderType()) &&
                !OrderTypes.WASH_BEAUTY.toString().equals(order.getOrderType())&&
                !OrderTypes.CUSTOMER_STATEMENT_ACCOUNT.toString().equals(order.getOrderType()))) {
              continue;
            }
            customerDTO = customerDTOMap.get(order.getCustomerOrSupplierId());
            if (customerDTO != null) {
              order.setCustomerStatus(customerDTO.getStatus());
            }
            order.setOrderContent("");
            if (NumberUtil.doubleVal(order.getMemberBalancePay()) > 0) {
              order.setConsumeType("金额;");
              order.setOrderContent(order.getMemberBalancePay().toString() + "元;");
            }
            if (OrderTypes.REPAIR.toString().equals(order.getOrderType()) || OrderTypes.WASH_BEAUTY.toString().equals(order.getOrderType())) {
              List<ItemIndexDTO> itemIndexDTOList = order.getItemIndexDTOs();
              if (CollectionUtils.isNotEmpty(itemIndexDTOList)) {
                for (ItemIndexDTO itemIndexDTO : itemIndexDTOList) {
                  if (ConsumeType.TIMES == itemIndexDTO.getConsumeType()) {
                    order.setOrderContent(order.getOrderContent() + itemIndexDTO.getServices() + "1次;");
                    order.setConsumeType((StringUtil.isEmpty(order.getConsumeType()) ? "" : order.getConsumeType()) + "计次划卡");
                  }
                }
              }
            }
            //拼接商品材料名称
            if (OrderTypes.REPAIR.toString().equals(order.getOrderType()) || OrderTypes.SALE.toString().equals(order.getOrderType())) {
              String productNames = "";
              Long orderId = order.getOrderId();
              List<ItemIndexDTO> itemIndexDTOList = ServiceManager.getService(ISearchService.class).getItemIndexDTOListByOrderId(shopId,orderId);
              if(CollectionUtils.isNotEmpty(itemIndexDTOList)){
                for(ItemIndexDTO itemIndexDTO : itemIndexDTOList){
                  if(itemIndexDTO.getProductId()!=null){
                    productNames = productNames + "," + itemIndexDTO.getItemName();
                  }
                }
                if(productNames.length()>0){
                  productNames = productNames.substring(1);
                }
                order.setProductNames(productNames);
              }
            }
          }
        }
      }
      Pager pager = new Pager(Integer.valueOf(String.valueOf(orderSearchResultListDTO.getNumFound())), startPageNo, maxRows);
      result.add(orderSearchResultListDTO);
      if (StringUtils.isBlank(orderSearchConditionDTO.getStartTimeStr())) {
        pager.setStartDateStr(DateUtil.convertDateLongToDateString(DateUtil.YEAR_MONTH_DATE, startTime));
      } else {
        pager.setStartDateStr(orderSearchConditionDTO.getStartTimeStr());
      }
      if (StringUtils.isBlank(orderSearchConditionDTO.getEndTimeStr())) {
        pager.setEndDateStr(DateUtil.convertDateLongToDateString(DateUtil.YEAR_MONTH_DATE, endTime - 1L));
      } else {
        pager.setEndDateStr(orderSearchConditionDTO.getEndTimeStr());
      }
      result.add(pager);
      return result;
    } catch (Exception e) {
      LOG.error("member.do.getMemberConsume shopId:" + request.getSession().getAttribute("shopId"));
      LOG.error(e.getMessage(), e);
    }
    return null;
  }

  @ResponseBody
  @RequestMapping(params = "method=getSingleMemberConsume")
  public Object getSingleMemberConsume (HttpServletRequest request) {
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    IMembersService membersService = ServiceManager.getService(IMembersService.class);
    RFITxnService rfiTxnService = ServiceManager.getService(RFITxnService.class);
    IUserService userService = ServiceManager.getService(IUserService.class);
    try {
      Long shopId = WebUtil.getShopId(request);
      Long customerId = null;
      String customerIdStr = request.getParameter("customerId");
      if(!StringUtil.isEmpty(customerIdStr)){
        customerId = Long.parseLong(customerIdStr);
      }

      String startTimeStr = request.getParameter("startTimeStr");
      String endTimeStr = request.getParameter("endTimeStr");
      //日期转换Long
      Long startTime = null;
      Long endTime = null;

      if(!StringUtil.isEmpty(startTimeStr)){
        //开始时间按该天0点算，左闭右开
        startTime = DateUtil.convertDateStringToDateLong("yyyy-MM-dd",startTimeStr);
      }
      if(!StringUtil.isEmpty(endTimeStr)){
        //结束时间按第二天的0点算，左闭右开
        endTime = DateUtil.convertDateStringToDateLong("yyyy-MM-dd",endTimeStr);
        endTime = endTime + 24*3600*1000 - 1;
      }

      //根据customerId获取memberId
      MemberDTO memberDTO = membersService.getMemberByCustomerId(shopId, customerId);

      List<OrderSearchResultDTO> orders = new ArrayList<OrderSearchResultDTO>();
      List<ReceivableDTO> receivableDTOList = txnService.getMemberCardConsumeByMemberId(memberDTO.getId());

      if(!CollectionUtil.isEmpty(receivableDTOList)){
        for(ReceivableDTO receivableDTO : receivableDTOList){
          OrderSearchResultDTO orderSearchResultDTO = new OrderSearchResultDTO();
          orderSearchResultDTO.setMemberType(memberDTO.getType());
          orderSearchResultDTO.setMemberNo(memberDTO.getMemberNo());
          OrderTypes orderType = receivableDTO.getOrderType();
          if(orderType.equals(OrderTypes.REPAIR)){
            RepairOrderDTO repairOrderDTO = rfiTxnService.getRepairOrderDTOById(receivableDTO.getOrderId(), receivableDTO.getShopId());
            Long vehicleId = repairOrderDTO.getVechicleId();
            VehicleDTO vehicleDTO = userService.getVehicleById(vehicleId);
            if(vehicleDTO!=null){
              orderSearchResultDTO.setVehicle(vehicleDTO.getLicenceNo());
            }
            orderSearchResultDTO.setOrderId(receivableDTO.getOrderId());
            orderSearchResultDTO.setOrderType(orderType.toString());
            if(repairOrderDTO != null){
              orderSearchResultDTO.setVestDate(repairOrderDTO.getVestDate());
              orderSearchResultDTO.setCreatedTime(repairOrderDTO.getCreationDate());
              orderSearchResultDTO.setReceiptNo(repairOrderDTO.getReceiptNo());
            }
            orderSearchResultDTO.setAmount(receivableDTO.getMemberBalancePay());
            orderSearchResultDTO.setOrderContent(""+receivableDTO.getMemberBalancePay()+"元");
            orderSearchResultDTO.setConsumeType("储值消费");
            orders.add(orderSearchResultDTO);
          }else if(orderType.equals(OrderTypes.WASH_BEAUTY)){
            WashBeautyOrderDTO washBeautyOrderDTO = txnService.getWashBeautyOrderDTOById(shopId, receivableDTO.getOrderId());
            orderSearchResultDTO.setOrderId(receivableDTO.getOrderId());
            orderSearchResultDTO.setOrderType(orderType.toString());
            if(washBeautyOrderDTO!=null){
              orderSearchResultDTO.setVestDate(washBeautyOrderDTO.getVestDate());
              orderSearchResultDTO.setCreatedTime(washBeautyOrderDTO.getCreationDate());
              orderSearchResultDTO.setReceiptNo(washBeautyOrderDTO.getReceiptNo());
              orderSearchResultDTO.setVehicle(washBeautyOrderDTO.getVechicle());
            }
            orderSearchResultDTO.setAmount(receivableDTO.getMemberBalancePay());
            //洗车单item拼接消费金额及划卡次数
            List<WashBeautyOrderItemDTO> washBeautyOrderItemDTOList = txnService.getWashBeautyOrderItemDTOByOrderId(shopId, receivableDTO.getOrderId());
            if(!CollectionUtil.isEmpty(washBeautyOrderItemDTOList)){
              String orderContent = "";
              String consumeType = "";
              String timesContent = "";
              String moneyContent = "";
              boolean timesFlag = false;
              boolean moneyFlag = false;
              for(WashBeautyOrderItemDTO washBeautyOrderItemDTO : washBeautyOrderItemDTOList){
                //储值消费
                if(washBeautyOrderItemDTO.getPayType().equals(ConsumeType.MONEY)){
                  moneyFlag = true;
                }else if(washBeautyOrderItemDTO.getPayType().equals(ConsumeType.TIMES)){
                  timesFlag = true;
                  ServiceDTO serviceDTO = txnService.getServiceById(washBeautyOrderItemDTO.getServiceId());
                  timesContent = timesContent + serviceDTO.getName() + "1次， ";
                }
              }
              if(moneyFlag==true){
                moneyContent = receivableDTO.getMemberBalancePay() + "元， ";
                consumeType = consumeType + "储值消费， ";
              }
              if(timesFlag==true){
                consumeType = consumeType + "计次划卡， ";
              }
              orderContent = moneyContent + timesContent;
              //多余逗号截断
              orderContent = orderContent.substring(0,orderContent.length()-2);
              consumeType = consumeType.substring(0,consumeType.length()-2);
              orderSearchResultDTO.setOrderContent(orderContent);
              orderSearchResultDTO.setConsumeType(consumeType);
            }
            orders.add(orderSearchResultDTO);
          }else if(orderType.equals(OrderTypes.SALE)){
            SalesOrderDTO salesOrderDTO = txnService.getSalesOrder(receivableDTO.getOrderId());
            orderSearchResultDTO.setOrderId(receivableDTO.getOrderId());
            orderSearchResultDTO.setOrderType(orderType.toString());
            orderSearchResultDTO.setVestDate(salesOrderDTO.getVestDate());
            orderSearchResultDTO.setCreatedTime(salesOrderDTO.getCreationDate());
            orderSearchResultDTO.setReceiptNo(salesOrderDTO.getReceiptNo());
            orderSearchResultDTO.setAmount(receivableDTO.getMemberBalancePay());
            orderSearchResultDTO.setOrderContent(""+receivableDTO.getMemberBalancePay()+"元");
            orderSearchResultDTO.setConsumeType("储值消费");
            orders.add(orderSearchResultDTO);
          }
        }
      }

      //按时间倒序排列
      Collections.sort(orders, new OrderSearchResultComparator());
      //根据页面时间条件截断
      List<OrderSearchResultDTO> orderDTOList =orders;
      if(startTime!=null){
        List<OrderSearchResultDTO> tempList = new ArrayList<OrderSearchResultDTO>();
        for(OrderSearchResultDTO order : orderDTOList){
          if(order.getCreatedTime() >= startTime){
            tempList.add(order);
          }
        }
        orderDTOList = tempList;
      }
      if(endTime!=null){
        List<OrderSearchResultDTO> tempList = new ArrayList<OrderSearchResultDTO>();
        for(OrderSearchResultDTO order : orderDTOList){
          if(order.getCreatedTime() < endTime){
            tempList.add(order);
          }
        }
        orderDTOList = tempList;
      }

      int totalCount = orderDTOList.size();
      int pageNo = NumberUtil.intValue(request.getParameter("startPageNo"), 1);
      int pageSize = 5;
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

      List<Object> result = new ArrayList<Object>();
      result.add(orderDTOList);
      result.add(pager);
      return result;
    } catch (Exception e) {
      LOG.error("member.do?getSingleMemberConsume shopId:" + request.getSession().getAttribute("shopId"));
      LOG.error(e.getMessage(), e);
    }
    return null;
  }

  @RequestMapping(params = "method=getMemberDiscount")
  @ResponseBody
  public Map getMemberDiscount(HttpServletRequest request, HttpServletResponse response) {
    Map map = new HashMap();
    IMembersService membersService = ServiceManager.getService(IMembersService.class);
    IUserService userService = ServiceManager.getService(IUserService.class);
    String memberNo = request.getParameter("memberNo");
    String password = request.getParameter("password");
    Long shopId = WebUtil.getShopId(request);
    Member member = membersService.getMemberByShopIdAndMemberNo(shopId, memberNo);
    if (null == member) {
      map.put("resu", "error");
      map.put("msg", "noMember");
    }
    else {

      Customer customer = userService.getCustomerByCustomerId(member.getCustomerId(), shopId);
      if (null == customer) {
        map.put("resu", "error");
        map.put("msg", "noCustomer");
        LOG.error("会员关联的客户不存在，customerId=" + member.getCustomerId().toString());
      }
      else {
        if (CustomerStatus.DISABLED.equals(customer.getStatus())) {
          map.put("resu", "error");
          map.put("msg", "customerDelete");
        }
        else {
          Double memberDiscount = member.getMemberDiscount();
          if (null != memberDiscount) {
            memberDiscount = NumberUtil.round(memberDiscount * 10, 1);
          }
          map.put("resu", "success");
          map.put("memberDiscount", memberDiscount);
        }
      }


    }

    return map;
  }

  @RequestMapping(params = "method=getMemberDiscountAndValidatePassword")
  @ResponseBody
  public Map getMemberDiscountAndValidatePassword(HttpServletRequest request, HttpServletResponse response) {
    Map map = new HashMap();
    IMembersService membersService = ServiceManager.getService(IMembersService.class);
    IUserService userService = ServiceManager.getService(IUserService.class);
    String memberNo = request.getParameter("memberNo");
    String password = request.getParameter("password");
    Long shopId = WebUtil.getShopId(request);
    Member member = membersService.getMemberByShopIdAndMemberNo(shopId, memberNo);
    if (null == member) {
      map.put("resu", "error");
      map.put("msg", "noMember");
    }
    else {

      if (StringUtils.isNotBlank(member.getPassword())) {
        if (StringUtils.isBlank(password) || (StringUtils.isNotBlank(password) && !member.getPassword().equals(
            EncryptionUtil.encryptPassword(password, shopId)))) {
          map.put("resu", "error");
          map.put("msg", "passwordError");
          return map;
        }
      }

      Customer customer = userService.getCustomerByCustomerId(member.getCustomerId(), shopId);
      if (null == customer) {
        map.put("resu", "error");
        map.put("msg", "noCustomer");
        LOG.error("会员关联的客户不存在，customerId=" + member.getCustomerId().toString());
      }
      else {
        if (CustomerStatus.DISABLED.equals(customer.getStatus())) {
          map.put("resu", "error");
          map.put("msg", "customerDelete");
        }
        else {
          Double memberDiscount = member.getMemberDiscount();
          if (null != memberDiscount) {
            memberDiscount = NumberUtil.round(memberDiscount * 10, 1);
          }
          map.put("resu", "success");
          map.put("memberDiscount", memberDiscount);
        }
      }


    }

    return map;
  }

  @RequestMapping(params = "method=getBusinessMemberInfoToPrint")
  public void getBusinessMemberInfoToPrint(HttpServletRequest request,HttpServletResponse response)
  {
    String dataObj = request.getParameter("dataObj");
    String startDateStr = request.getParameter("startDateStr");
    String endDateStr = request.getParameter("endDateStr");
    String orderType = request.getParameter("orderType");

    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    IPrintService printService = ServiceManager.getService(IPrintService.class);

    MemberStatResultDTO memberStatResultDTO= null;
    OrderSearchResultListDTO orderSearchResultListDTO = null;

    try
    {
      Gson gson = new Gson();
      PrintTemplateDTO printTemplateDTO = null;
      String myTemplateName = "";
      if("memberCardOrder".equals(orderType))
      {
        memberStatResultDTO =  gson.fromJson(dataObj,new TypeToken<MemberStatResultDTO>(){}.getType());
        printTemplateDTO = printService.getSinglePrintTemplateDTOByShopIdAndType(WebUtil.getShopId(request), OrderTypes.BUSINESS_MEMBER_CARD_ORDER);
        myTemplateName = "businessMemberCardOrder"+ String.valueOf(WebUtil.getShopId(request));
      }
      if("memberConsume".equals(orderType))
      {
        orderSearchResultListDTO =  gson.fromJson(dataObj,new TypeToken<OrderSearchResultListDTO>(){}.getType());
        printTemplateDTO = printService.getSinglePrintTemplateDTOByShopIdAndType(WebUtil.getShopId(request), OrderTypes.BUSINESS_MEMBER_CONSUME);
        myTemplateName = "businessMemberConsume"+ String.valueOf(WebUtil.getShopId(request));
      }
      if("memberReturn".equals(orderType))
      {
        memberStatResultDTO =  gson.fromJson(dataObj,new TypeToken<MemberStatResultDTO>(){}.getType());
        printTemplateDTO = printService.getSinglePrintTemplateDTOByShopIdAndType(WebUtil.getShopId(request), OrderTypes.BUSINESS_MEMBER_RETURN);
        myTemplateName = "businessMemberReturn"+ String.valueOf(WebUtil.getShopId(request));
      }
      PrintWriter out = response.getWriter();
      response.setContentType("text/html");
      response.setCharacterEncoding("UTF-8");

      if(null != printTemplateDTO)
      {
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
        context.put("startDateStr",startDateStr);
        context.put("endDateStr",endDateStr);
        context.put("memberStatResultDTO",memberStatResultDTO);
        context.put("orderSearchResultListDTO",orderSearchResultListDTO);
        //输出流
        StringWriter writer = new StringWriter();

        //转换输出
        t.merge(context, writer);
        out.print(writer);
        writer.close();
      }
      else
      {
        out.print("<html><head><title></title></head><body>没有可用的模板</body><html>");
      }

      out.close();

    }
    catch(Exception e)
    {
      LOG.error("method=getBusinessMemberInfoToPrint");
      LOG.error(e.getMessage(),e);
    }
  }

  @RequestMapping(params = "method=getMemberType")
  @ResponseBody
  public Map getMemberType(HttpServletRequest request,HttpServletResponse response)
  {
    IMembersService membersService = ServiceManager.getService(IMembersService.class);
    Long shopId = WebUtil.getShopId(request);
    Map map = new HashMap();
    map.put("data",membersService.getEnableMemberCardDTOByShopId(shopId));
    return map;
  }

  @Autowired
  private StatUtil statUtil;

  @RequestMapping(params = "method=dropMemberServiceRemind")
  public void dropMemberServiceRemind(HttpServletRequest request, HttpServletResponse response){
    Long shopId = WebUtil.getShopId(request);
    String idStr = request.getParameter("idStr");
    if(StringUtil.isEmpty(idStr)){
      return;
    }
    Long id = Long.parseLong(idStr);
    IMembersService membersService = ServiceManager.getService(IMembersService.class);
    try{
      MemberService ms = membersService.getMemberServiceById(id);
      ms.setRemindStatus(UserConstant.Status.CANCELED);
      membersService.updateMemberService(ms);
      PrintWriter writer = response.getWriter();
      writer.write("succ");
      writer.close();
    }catch (Exception e){
      LOG.error("method=dropMemberServiceRemind");
      LOG.error("修改会员服务提醒状态出错！");
      LOG.error(e.getMessage(),e);
    }
  }

  /**
   * 根据会员号获取相似会员号列表
   * @param request
   * @param response
   * @return
   */
  @RequestMapping(params = "method=getEnabledMemberLikeMemberNo")
  @ResponseBody
  public List getEnabledMemberLikeMemberNo(HttpServletRequest request, HttpServletResponse response) {
    String memberNo = request.getParameter("memberNo");
    if (StringUtil.isEmpty(memberNo)) {
      return new ArrayList<MemberDTO>();
    }
    IMembersService membersService = ServiceManager.getService(IMembersService.class);
    Long shopId = WebUtil.getShopId(request);
    return membersService.getEnabledMemberLikeMemberNo(shopId, memberNo);
  }

}



