package com.bcgogo.customer;

import com.bcgogo.api.AppUserDTO;
import com.bcgogo.common.Pager;
import com.bcgogo.common.PagingListResult;
import com.bcgogo.common.Result;
import com.bcgogo.common.WebUtil;
import com.bcgogo.config.dto.RecentlyUsedDataDTO;
import com.bcgogo.config.dto.ShopBalanceDTO;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.service.IRecentlyUsedDataService;
import com.bcgogo.config.service.IShopBalanceService;
import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.enums.MessageScene;
import com.bcgogo.enums.PlansRemindStatus;
import com.bcgogo.enums.RemindEventType;
import com.bcgogo.enums.SolrIdPrefix;
import com.bcgogo.enums.config.RecentlyUsedDataType;
import com.bcgogo.enums.notification.ContactGroupType;
import com.bcgogo.enums.notification.SmsChannel;
import com.bcgogo.enums.notification.SmsType;
import com.bcgogo.enums.sms.SenderType;
import com.bcgogo.enums.sms.SmsSendScene;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.notification.SmsHelper;
import com.bcgogo.notification.dto.*;
import com.bcgogo.notification.model.SmsJob;
import com.bcgogo.notification.service.INotificationService;
import com.bcgogo.notification.service.ISmsService;
import com.bcgogo.notification.smsSend.SmsUtil;
import com.bcgogo.remind.dto.RemindEventDTO;
import com.bcgogo.remind.dto.ShopPlanDTO;
import com.bcgogo.search.dto.CustomerSupplierSearchConditionDTO;
import com.bcgogo.search.dto.CustomerSupplierSearchResultDTO;
import com.bcgogo.search.dto.CustomerSupplierSearchResultListDTO;
import com.bcgogo.search.service.user.ISearchCustomerSupplierService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.service.ITxnService;
import com.bcgogo.txn.service.pushMessage.ICustomPushMessageService;
import com.bcgogo.txn.service.sms.ISendSmsService;
import com.bcgogo.txn.service.solr.ICustomerOrSupplierSolrWriteService;
import com.bcgogo.user.dto.*;
import com.bcgogo.user.model.Contact;
import com.bcgogo.user.model.ShopPlan;
import com.bcgogo.user.service.*;
import com.bcgogo.user.service.app.IAppUserCustomerMatchService;
import com.bcgogo.utils.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.*;

@Controller
@RequestMapping("/sms.do")
public class SmsController {

  public static final Logger LOG = LoggerFactory.getLogger(SmsController.class);
  public static final int pageSize = 5;     //页码显示条数

  //收件箱
  @RequestMapping(params = "method=smsinbox")
  public String smsInbox(HttpServletRequest request) throws Exception {
    INotificationService notificationService = ServiceManager.getService(INotificationService.class);
    IUserService userService = ServiceManager.getService(IUserService.class);
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    //已接受短信数目
    int inBoxSmsNumber = 0;

    if (shopId == null) {
      request.setAttribute("inBoxSmsNumber", inBoxSmsNumber);
      return "/sms/smsinbox";
    }

    inBoxSmsNumber = notificationService.countShopInBox(shopId);
    Pager pager = new Pager(inBoxSmsNumber, NumberUtil.intValue(request.getParameter("pageNo"), 1), pageSize);

    List<InBoxDTO> inBoxDTOList = null;
    try {
      inBoxDTOList = notificationService.getShopInBoxs(shopId, pager.getCurrentPage() - 1, pageSize);    //分页查询
      if (inBoxDTOList != null) {
        for (InBoxDTO inBoxDTO : inBoxDTOList) {
          String mobile = inBoxDTO.getSendMobile();
          List<CustomerDTO> customerDTOList = userService.getCompleteCustomerByMobile(shopId, mobile);
          if (customerDTOList != null && customerDTOList.size() > 0) {
            inBoxDTO.setName(customerDTOList.get(0).getName());
          }
          List<VehicleDTO> vehicleDTOList = userService.getCompleteVehicleByMobile(shopId, mobile);
          if (vehicleDTOList != null && vehicleDTOList.size() > 0) {
            inBoxDTO.setLicenceNo(vehicleDTOList.get(0).getLicenceNo());
          }
        }
        request.setAttribute("pager", pager);
//        request.setAttribute("pageNo", pageNo);
//        request.setAttribute("pageCount", pageCount);
        request.setAttribute("inBoxDTOList", inBoxDTOList);
      }
    } catch (Exception e) {
      LOG.debug("/sms.do");
      LOG.debug("method=smsinbox");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
    }
    return "/sms/smsinbox";
  }

  @RequestMapping(params = "method=querySms")
  @ResponseBody
  public Object querySms(HttpServletRequest request,SmsSearchCondition condition){
    StopWatchUtil sw = new StopWatchUtil("querySms", "start");
    INotificationService notificationService = ServiceManager.getService(INotificationService.class);
    IUserService userService = ServiceManager.getService(IUserService.class);
    Long shopId = WebUtil.getShopId(request);
    if(shopId==null||condition.getSmsType()==null){
      return null;
    }
    try {
      condition.setShopId(shopId);
      condition.setKeyWord(StringUtil.toTrim(condition.getKeyWord()));
      Pager pager = new Pager(notificationService.countSms(condition), NumberUtil.intValue(condition.getStartPageNo(),1),condition.getPageSize());
      condition.setPager(pager);
      List<SmsDTO> smsDTOList=null;
      sw.stopAndStart("query main");
      smsDTOList=notificationService.querySms(condition);
      sw.stopAndStart("process");
      if(CollectionUtil.isNotEmpty(smsDTOList)){
        for(SmsDTO smsDTO:smsDTOList){
          if(smsDTO==null||smsDTO.getUserId()==null){
            continue;
          }
          UserDTO userDTO=userService.getUserByUserId(smsDTO.getUserId());
          smsDTO.setUserName(userDTO!=null?userDTO.getName():"");
          if(StringUtil.isNotEmpty(smsDTO.getContactGroupIds())){
            Long[] contactGroupIds=ArrayUtil.toLongArr(smsDTO.getContactGroupIds().split(","));
            smsDTO.setContactGroupDTOs(userService.getContactGroupByIds(contactGroupIds));
          }
          ServiceManager.getService(ISendSmsService.class).generateSmsContactBySpecialId(shopId, smsDTO);
        }
      }
      Map resultMap=new HashMap();
      resultMap.put("pager",pager);
      resultMap.put("results", SmsHelper.toSmsListFromSmsDTO(smsDTOList));
      //统计短信条数
      sw.stopAndStart("count");
      condition.setStartTime(DateUtil.getStartTimeOfToday());
      condition.setEndTime(DateUtil.getEndTimeOfToday());
      resultMap.put("today_total_num",notificationService.countSms(condition));
      condition.setStartTime(DateUtil.getStartTimeOfYesterday());
      condition.setEndTime(DateUtil.getEndTimeOfYesterday());
      resultMap.put("yesterday_total_num",notificationService.countSms(condition));
      condition.setStartTime(DateUtil.getLastWeekStartTime());
      condition.setEndTime(DateUtil.getLastWeekEndTime());
      resultMap.put("last_week_total_num",notificationService.countSms(condition));
      condition.setStartTime(null);
      condition.setEndTime(DateUtil.getStartTimeOfYesterday());
      resultMap.put("other_total_num",notificationService.countSms(condition));
      sw.stopAndPrintLog();
      return resultMap;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return null;
    }
  }

  @RequestMapping(params = "method=getTheAssignSms")
  @ResponseBody
  public Object getTheAssignSms(HttpServletRequest request,Integer rowStart,SmsType smsType){
    INotificationService notificationService = ServiceManager.getService(INotificationService.class);
    IUserService userService = ServiceManager.getService(IUserService.class);
    Long shopId = WebUtil.getShopId(request);
    Result result=new Result();
    try {
      if(shopId==null||smsType==null){
        return result.LogErrorMsg("参数异常。");
      }
      SmsSearchCondition condition=new SmsSearchCondition();
      condition.setShopId(shopId);
      condition.setSmsType(smsType);
      int smsTotalNum=notificationService.countSms(condition);
      if(smsTotalNum<=0) return result.LogErrorMsg("没有短信数据。");
      rowStart=NumberUtil.intValue(rowStart);
      rowStart=rowStart<1?1:rowStart;
      rowStart=rowStart>smsTotalNum?smsTotalNum:rowStart;
      Pager pager=new Pager();
      pager.setRowStart(rowStart-1);
      pager.setPageSize(1);
      condition.setPager(pager);
      SmsDTO smsDTO=CollectionUtil.getFirst(notificationService.getSmsDTOList(condition));
      if(smsDTO==null) return result.LogErrorMsg("短信不存在");
      UserDTO userDTO=userService.getUserByUserId(smsDTO.getUserId());
      smsDTO.setUserName(userDTO!=null?userDTO.getUserName():"");
      if(StringUtil.isNotEmpty(smsDTO.getContactGroupIds())){
        Long[] contactGroupIds=ArrayUtil.toLongArr(smsDTO.getContactGroupIds().split(","));
        smsDTO.setContactGroupDTOs(userService.getContactGroupByIds(contactGroupIds));
      }
      ServiceManager.getService(ISendSmsService.class).generateSmsContactBySpecialId(shopId, smsDTO);
      smsDTO.setSmsTotalNum(smsTotalNum);
      smsDTO.setRowStart(rowStart);
      return smsDTO;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return result.LogErrorMsg("网络异常。");
    }
  }


  /**
   * 已发送
   * @return
   */
  @RequestMapping(params = "method=smssent")
  public String smsSent(){
    return "redirect:sms.do?method=toSmsList&smsType=SMS_SENT";
  }

  /**
   * 待发送msg
   */
  @RequestMapping(params = "method=smssend")
  public String smsSend() throws Exception {
    return "redirect:sms.do?method=toSmsList&smsType=SMS_SEND";
  }

  @RequestMapping(params = "method=toSmsDraft")
  public String toSmsDraft() throws Exception {
    return "redirect:sms.do?method=toSmsList&smsType=SMS_DRAFT";
  }

  @RequestMapping(params = "method=toSmsList")
  public String toSmsList(ModelMap modelMap,String smsType) throws Exception {
    modelMap.put("smsType",smsType);
    return "/sms/smsList";
  }

  @RequestMapping(params = "method=toSmsTemplateList")
  public String toSmsTemplateList(ModelMap modelMap,HttpServletRequest request) throws Exception {
    try{
      IConfigService configService= ServiceManager.getService(IConfigService.class);
      modelMap.put("shopName",configService.getShopById(WebUtil.getShopId(request)).getName());
    } catch (Exception e){
      LOG.error(e.getMessage(),e);
    }
    return "/sms/smsTemplateList";
  }

  @RequestMapping(params = "method=toAddressList")
  public String toAddressList(HttpServletRequest request,ModelMap modelMap) throws Exception {
    IUserService userService=ServiceManager.getService(IUserService.class);
    try{
      List<ContactGroupDTO> groupDTOs=SmsHelper.filterContactGroupByVersion(ConfigUtils.isWholesalerVersion(WebUtil.getShopVersionId(request)),userService.getContactGroup());
      if(CollectionUtil.isNotEmpty(groupDTOs)){
        ISearchCustomerSupplierService searchCustomerSupplierService=ServiceManager.getService(ISearchCustomerSupplierService.class);
        CustomerSupplierSearchConditionDTO searchConditionDTO=new CustomerSupplierSearchConditionDTO();
        searchConditionDTO.setShopId(WebUtil.getShopId(request));
        searchConditionDTO.setSearchStrategies(new CustomerSupplierSearchConditionDTO.SearchStrategy[]{CustomerSupplierSearchConditionDTO.SearchStrategy.mobileNotEmpty});
//        int allContactNum=0;
        for(ContactGroupDTO groupDTO:groupDTOs){
          searchConditionDTO.setContactGroupType(groupDTO.getContactGroupType());
          CustomerSupplierSearchResultListDTO resultListDTO=searchCustomerSupplierService.queryContact(searchConditionDTO);
          groupDTO.setTotalNum(NumberUtil.intValue(resultListDTO.getNumFound()));
          if(ContactGroupType.OTHERS.equals(groupDTO.getContactGroupType())){
            modelMap.put("otherContactNum",NumberUtil.intValue(resultListDTO.getNumFound()));
          }
//          allContactNum+=NumberUtil.intValue(resultListDTO.getNumFound());
        }
        searchConditionDTO.setContactGroupType(null);
        modelMap.put("allContactNum",NumberUtil.intValue(searchCustomerSupplierService.queryContact(searchConditionDTO).getNumFound()));
      }
      modelMap.put("contactGroupDTOs",groupDTOs);
    }catch (Exception e){
      LOG.error(e.getMessage(),e);
    }
    return "/sms/addressList";
  }

  @RequestMapping(params = "method=getSmsTemplateList")
  @ResponseBody
  public Object getSmsTemplateList(HttpServletRequest request,int startPageNo,Integer pageSize,String keyWord){
    INotificationService notificationService=ServiceManager.getService(INotificationService.class);
    Long shopId=WebUtil.getShopId(request);
    try{
      keyWord=StringUtil.toTrim(keyWord);
      pageSize=pageSize==null?SmsHelper.SMS_TEMPLATE_LIST_NUM:pageSize;
      startPageNo=startPageNo==0?1:startPageNo;
      PagingListResult<MessageTemplateDTO> result=new PagingListResult<MessageTemplateDTO>();
      Pager pager = new Pager(notificationService.countShopMsgTemplate(shopId,keyWord),startPageNo,pageSize);
      result.setPager(pager);
      result.setResults(notificationService.getShopMsgTemplate(shopId,keyWord,pager));
      return result;
    }catch (Exception e){
      LOG.error(e.getMessage(),e);
      return null;
    }
  }

  @RequestMapping(params = "method=saveSmsTemplate")
  @ResponseBody
  public Object saveSmsTemplate(HttpServletRequest request,Long templateId,String name,String content){
    INotificationService notificationService=ServiceManager.getService(INotificationService.class);
    Result result=new Result();
    try{
      if(StringUtil.isEmpty(name)) return result.LogErrorMsg("模板名不能为空。");
      if(StringUtil.isEmpty(content)) return result.LogErrorMsg("模板内容不能为空。");
      Long shopId=WebUtil.getShopId(request);
      if(templateId==null){
        List<MessageTemplateDTO> templateDTOs=notificationService.getShopMsgTemplateDTOByName(shopId,name);
        if(CollectionUtil.isNotEmpty(templateDTOs)){
          return result.LogErrorMsg("模板名不能重复，请重新输入。");
        }
      }
      MessageTemplateDTO templateDTO=new MessageTemplateDTO();
      templateDTO.setShopId(WebUtil.getShopId(request));
      templateDTO.setId(templateId);
      templateDTO.setName(name);
      templateDTO.setContent(content);
      templateDTO.setType(MessageScene.SHOP_SMS_TEMPLATE.toString());
      notificationService.saveShopMsgTemplate(templateDTO);
      return result;
    }catch (Exception e){
      LOG.error(e.getMessage(),e);
      return result.LogErrorMsg("网络异常");
    }
  }

  @RequestMapping(params = "method=saveSmsContact")
  @ResponseBody
  public Object saveSmsContact(HttpServletRequest request,String name,String mobile,String customerSupplierFlag){
    IUserService userService=ServiceManager.getService(IUserService.class);
    IContactService contactService=ServiceManager.getService(IContactService.class);
    Result result=new Result();
    Long shopId=WebUtil.getShopId(request);
    try{
      if(StringUtil.isEmpty(mobile)) return result.LogErrorMsg("手机号不能为空。");
      if(StringUtil.isEmpty(name)) return result.LogErrorMsg("联系人名不能为空。");
      //校验新增联系人
      if("CUSTOMER".equals(customerSupplierFlag)||"SUPPLIER".equals(customerSupplierFlag)){
        List<Contact> contactList= userService.getCustomerSupplierContactByMobile(shopId,mobile);
        if(CollectionUtil.isNotEmpty(contactList)){
          for(Contact contact:contactList){
            if(contact==null) continue;
            if(mobile.equals(contact.getMobile())){
              return result.LogErrorMsg("手机号已存在，不能加为客户或供应商。");
            }
          }
        }
      }
      ContactDTO contactDTO=new ContactDTO();
      contactDTO.setShopId(shopId);
      contactDTO.setName(StringUtil.toTrim(name));
      contactDTO.setMobile(mobile);
      contactDTO.setMainContact(1);
      contactDTO.setDisabled(1);
      Long  contactId=userService.saveOrUpdateContact(contactDTO);
      // saveCustomerSupplier里面也会将联系人字段存到solr
      if(StringUtils.isEmpty(customerSupplierFlag)) {
        ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexOtherContactSolrIndex(shopId, contactId);
      }
      saveCustomerSupplier(contactDTO,customerSupplierFlag);
      result.setData(contactDTO);
      return result;
    }catch (Exception e){
      LOG.error(e.getMessage(),e);
      return result.LogErrorMsg("网络异常");
    }
  }

  private void saveCustomerSupplier(ContactDTO contactDTO,String customerSupplierFlag) throws BcgogoException {
    IContactService contactService=ServiceManager.getService(IContactService.class);
    IUserService userService=ServiceManager.getService(IUserService.class);
    Long contactId=contactDTO.getId();
    Long shopId=contactDTO.getShopId();
    if("CUSTOMER".equals(customerSupplierFlag)){
      CustomerDTO customerDTO=SmsHelper.genCustomerDTOFromContact(contactDTO);
      userService.createCustomer(customerDTO);
//      contactDTO=CollectionUtil.getFirst(contactService.getContactsByIds(shopId,contactId));
      contactDTO.setCustomerId(customerDTO.getId());
      userService.saveOrUpdateContact(contactDTO); //add customerId
      ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexCustomerByCustomerId(customerDTO.getId());
      contactDTO.setSpecialIdStr(SolrIdPrefix.CUSTOMER+"_"+contactId);
    }else if("SUPPLIER".equals(customerSupplierFlag)){
      SupplierDTO supplierDTO=SmsHelper.genSupplierDTOFromContact(contactDTO);
      userService.createSupplier(supplierDTO);
      contactDTO.setSupplierId(supplierDTO.getId());
      contactDTO.setSupplierId(supplierDTO.getId());
      userService.saveOrUpdateContact(contactDTO); //add customerId
      ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexSupplierBySupplierId(supplierDTO.getId());
      contactDTO.setSpecialIdStr(SolrIdPrefix.SUPPLIER+"_"+contactId);
    }else{
      contactDTO.setSpecialIdStr(SolrIdPrefix.OTHER+"_"+contactId);
    }
  }

  @RequestMapping(params = "method=updateSmsContact")
  @ResponseBody
  public Object updateSmsContact(HttpServletRequest request,Long contactId,String name,String mobile,SolrIdPrefix customerSupplierFlag){
    Result result=new Result();
    try{
      if(contactId==null) return result.LogErrorMsg("联系人信息异常。");
      if(StringUtil.isEmpty(mobile)) return result.LogErrorMsg("手机号不能为空。");
      if(StringUtil.isEmpty(name)) return result.LogErrorMsg("联系人名不能为空。");
      IUserService userService=ServiceManager.getService(IUserService.class);
      Long shopId=WebUtil.getShopId(request);
      ContactDTO contactDTO=userService.getContactDTOById(shopId,contactId);
      if(contactDTO==null) return result.LogErrorMsg("联系人不存在。");
      if(SolrIdPrefix.CUSTOMER.equals(customerSupplierFlag)||SolrIdPrefix.SUPPLIER.equals(customerSupplierFlag)){
        List<Contact> contactList=userService.getCustomerSupplierContactByMobile(shopId,mobile);
        if(CollectionUtil.isNotEmpty(contactList)){
          for(Contact contact:contactList){
            if(contact==null||contactId.equals(contact.getId())){
              continue;
            }
            if(mobile.equals(contact.getMobile())){
            return  result.LogErrorMsg("手机号已存在，更新失败。");
            }
          }
        }
      }
      contactDTO.setName(StringUtil.toTrim(name));
      contactDTO.setMobile(mobile);
      userService.saveOrUpdateContact(contactDTO);
      ICustomerOrSupplierSolrWriteService solrWriteService=ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class);
      solrWriteService.reindexOtherContactSolrIndex(shopId, contactId);
      if(SolrIdPrefix.CUSTOMER.equals(customerSupplierFlag)||SolrIdPrefix.SUPPLIER.equals(customerSupplierFlag)){
        saveCustomerSupplier(contactDTO,customerSupplierFlag.toString());
        solrWriteService.deleteContactSolrIndexById(SolrIdPrefix.OTHER+"_"+contactId);
        contactDTO.setSpecialIdStr(customerSupplierFlag+"_"+contactId);
      }else{
        contactDTO.setSpecialIdStr(SolrIdPrefix.OTHER+"_"+contactId);
      }
      result.setData(contactDTO);
      return result;
    }catch (Exception e){
      LOG.error(e.getMessage(),e);
      return result.LogErrorMsg("网络异常");
    }
  }

  @RequestMapping(params = "method=deleteSmsTemplate")
  @ResponseBody
  public Object deleteSmsTemplate(HttpServletRequest request,Long[] templateIds){
    INotificationService notificationService=ServiceManager.getService(INotificationService.class);
    Result result=new Result();
    try{
      if(ArrayUtil.isEmpty(templateIds)) return result.LogErrorMsg("参数异常。");
      notificationService.deleteSmsTemplate(templateIds);
      return result;
    }catch (Exception e){
      LOG.error(e.getMessage(),e);
      return result.LogErrorMsg("网络异常");
    }
  }





  @RequestMapping(params = "method=saveSmsDraft")
  @ResponseBody
  public Result saveSmsDraft(HttpServletRequest request,SmsDTO smsDTO){
    Result result=new Result();
    try{
      Long shopId=WebUtil.getShopId(request);
      INotificationService notificationService = ServiceManager.getService(INotificationService.class);
      smsDTO.setId (smsDTO.getSmsDraftId()!=null?smsDTO.getSmsDraftId():null);
      smsDTO.setShopId(shopId);
      smsDTO.setUserId(WebUtil.getUserId(request));
      ServiceManager.getService(ISendSmsService.class).prepareForSaveSms(shopId, smsDTO);
      smsDTO.setSmsType(SmsType.SMS_DRAFT);
      smsDTO.setSendTime(StringUtil.isEmpty(smsDTO.getSendTimeStr())?System.currentTimeMillis():DateUtil.convertDateStringToDateLong(DateUtil.STANDARD,smsDTO.getSendTimeStr()));

      Long smsId=notificationService.saveOrUpdateSms(smsDTO);
      notificationService.saveOrUpdateSmsIndex(ServiceManager.getService(ISendSmsService.class).prepareForSaveSmsIndex(shopId, smsId));
      result.setDataStr(StringUtil.valueOf(smsId));
      return result;
    }catch (Exception e){
      LOG.error(e.getMessage(),e);
      return null;
    }
  }

  @RequestMapping(params = "method=deleteSms")
  @ResponseBody
  public Result saveSmsDraft(HttpServletRequest request,Long[] smsIds){
    Result result=new Result();
    try{
      if(ArrayUtil.isEmpty(smsIds)) return result.LogErrorMsg("参数异常。");
      Long shopId=WebUtil.getShopId(request);
      INotificationService notificationService = ServiceManager.getService(INotificationService.class);
      notificationService.deleteSms(shopId, smsIds);
      return result;
    }catch (Exception e){
      LOG.error(e.getMessage(),e);
      return result.LogErrorMsg("网络异常。");
    }
  }

  @RequestMapping(params = "method=modifySendTime")
  @ResponseBody
  public Result modifySendTime(HttpServletRequest request,Long smsId,String sendTimeStr){
    Result result=new Result();
    try{
      if(StringUtil.isEmpty(sendTimeStr)){
        return result.LogErrorMsg("发送时间应不能为空，请重新选择！");
      }
      Long sendTime=DateUtil.convertDateStringToDateLong(DateUtil.STANDARD, sendTimeStr);
      if(sendTime<System.currentTimeMillis()){
        return result.LogErrorMsg("发送时间应大于当前时间，请重新选择！");
      }
      INotificationService notificationService=ServiceManager.getService(INotificationService.class);
      SmsDTO smsDTO=notificationService.getSmsDTOById(WebUtil.getShopId(request),smsId);
      if(smsDTO==null){
        return result.LogErrorMsg("短信不存在。");
      }
      smsDTO.setSendTime(sendTime);
      smsDTO.setEditDate(System.currentTimeMillis());
      notificationService.saveOrUpdateSms(smsDTO);
      result.setData(DateUtil.convertDateLongToDateString(DateUtil.STANDARD_CN, sendTime));
      return result;
    }catch (Exception e){
      LOG.error(e.getMessage(),e);
      return result.LogErrorMsg("网络异常。");
    }
  }

  @RequestMapping(params = "method=cancelSendSms")
  @ResponseBody
  public Result cancelSendSms(HttpServletRequest request,Long[] smsIds){
    Result result=new Result();
    try{
      if(ArrayUtil.isEmpty(smsIds)) return result.LogErrorMsg("参数异常。");
      Long shopId=WebUtil.getShopId(request);
      INotificationService notificationService = ServiceManager.getService(INotificationService.class);
      List<SmsDTO> smsDTOList= notificationService.getSmsDTOByIds(shopId, smsIds);
      if(CollectionUtil.isNotEmpty(smsDTOList)){
        Long smsId=null;
        for(SmsDTO smsDTO:smsDTOList){
          smsDTO.setSmsType(SmsType.SMS_DRAFT);
          smsDTO.setEditDate(System.currentTimeMillis());
          smsDTO.setSendTime(smsDTO.getEditDate());
          smsId=notificationService.saveOrUpdateSms(smsDTO);
          notificationService.saveOrUpdateSmsIndex(ServiceManager.getService(ISendSmsService.class).prepareForSaveSmsIndex(shopId, smsId));
        }
      }
      return result;
    }catch (Exception e){
      LOG.error(e.getMessage(),e);
      return result.LogErrorMsg("网络异常。");
    }
  }

  //立即发送
  @RequestMapping(params = "method=doSendSms")
  @ResponseBody
  public Result doSendSms(HttpServletRequest request,Long[] smsIds){
    Result result=new Result();
    try{
      if(ArrayUtil.isEmpty(smsIds)) return result.LogErrorMsg("参数异常。");
      Long shopId=WebUtil.getShopId(request);
      //更新sms
      INotificationService notificationService = ServiceManager.getService(INotificationService.class);
      List<SmsDTO> smsDTOList= notificationService.getSmsDTOByIds(shopId, smsIds);
      Long smsId=null;
      if(CollectionUtil.isNotEmpty(smsDTOList)){
        for(SmsDTO smsDTO:smsDTOList){
          smsDTO.setSmsType(SmsType.SMS_SENT);
          smsDTO.setEditDate(System.currentTimeMillis());
          smsDTO.setSendTime(System.currentTimeMillis());
          smsId=notificationService.saveOrUpdateSms(smsDTO);
          result.setDataStr(String.valueOf(smsId));
          notificationService.saveOrUpdateSmsIndex(ServiceManager.getService(ISendSmsService.class).prepareForSaveSmsIndex(shopId, smsId));
        }
      }
      //更新job
      List<SmsJob> smsJobs=notificationService.getSmsJobsBySmsId(shopId, smsIds);
      if(CollectionUtil.isNotEmpty(smsJobs)){
        for(SmsJob smsJob:smsJobs){
          smsJob.setStartTime(System.currentTimeMillis());
        }
        notificationService.updateSmsJob(smsJobs);
      }
      return result;
    }catch (Exception e){
      LOG.error(e.getMessage(),e);
      return result.LogErrorMsg("网络异常。");
    }
  }

  //再次发送
  @RequestMapping(params = "method=reSendSms")
  @ResponseBody
  public Result reSendSms(HttpServletRequest request,Long smsId){
    Result result=new Result();
    try{
      if(smsId==null) return result.LogErrorMsg("参数异常。");
      Long shopId=WebUtil.getShopId(request);
      //更新sms
      INotificationService notificationService = ServiceManager.getService(INotificationService.class);
      SmsDTO smsDTO= notificationService.getSmsDTOById(shopId, smsId);
      if(smsDTO==null) result.LogErrorMsg("短信信息异常。");
      smsDTO.setId(null);
      smsDTO.setSendTimeStr(null);
      smsDTO.setSmsFlag(true);
      smsDTO.setEditDate(System.currentTimeMillis());
      if(StringUtil.isNotEmpty(smsDTO.getContactGroupIds())){
        Long[] contactGroupIds=ArrayUtil.toLongArr(smsDTO.getContactGroupIds().split(","));
        smsDTO.setContactGroupDTOs(ServiceManager.getService(IUserService.class).getContactGroupByIds(contactGroupIds));
      }
      ServiceManager.getService(ISendSmsService.class).generateSmsContactBySpecialId(shopId, smsDTO);
      sendSms(request,smsDTO,true);
      return result;
    }catch (Exception e){
      LOG.error(e.getMessage(),e);
      return result.LogErrorMsg("网络异常。");
    }
  }



  @RequestMapping(params = "method=sendSms")
  @ResponseBody
  public Result sendSms(HttpServletRequest request,SmsDTO smsDTO,boolean reSendFlag){
    Result result=new Result();
    Long shopId=WebUtil.getShopId(request);
    try{
      StopWatchUtil sw = new StopWatchUtil("sendSms", "preapre");
      sw.stopAndStart("save sms");
      ISendSmsService smsService=ServiceManager.getService(ISendSmsService.class);
      smsDTO.setTemplateFlag("true".equals(request.getParameter("templateFlag")));
      smsDTO.setShopId(WebUtil.getShopId(request));
      smsDTO.setUserId(WebUtil.getUserId(request));
      smsService.sendSms(result,smsDTO);
      //删除短信草稿
      if(smsDTO.getSmsDraftId()!=null){
        ServiceManager.getService(INotificationService.class).deleteSms(shopId, smsDTO.getSmsDraftId());
      }
      //保存最近联系人
      ITxnService txnService = ServiceManager.getService(ITxnService.class);
      IShopPlanService shopPlanService = ServiceManager.getService(IShopPlanService.class);
      if(StringUtils.isNotBlank(smsDTO.getContactIds())){
        Set<Long> contactIdSet = new HashSet<Long>();
        String[] specialContactIdStrs = smsDTO.getContactIds().split(",");
        for(String specialContactIdStr:specialContactIdStrs){
          contactIdSet.add(NumberUtil.longValue(specialContactIdStr.substring(specialContactIdStr.indexOf("_")+1)));
          contactIdSet.removeAll(Collections.singleton(null));
        }
        if(CollectionUtils.isNotEmpty(contactIdSet)){
          ServiceManager.getService(IRecentlyUsedDataService.class).saveOrUpdateRecentlyUsedData(shopId,WebUtil.getUserId(request),RecentlyUsedDataType.USED_SMS_CONTACT,contactIdSet.toArray(new Long[contactIdSet.size()]));
        }
      }

      sw.stopAndStart("remind related");
      //修改提醒表的状态为已提醒
      String remindEventIdStr = request.getParameter("remindEventId"); //todo 下面代码有BUG啊，只有欠款提醒发了短信会更新掉remindEvent，其他的咋办呢？！
      if(!StringUtil.isEmpty(remindEventIdStr)){
        Long remindEventId = NumberUtil.longValue(remindEventIdStr);
        if(remindEventId!=null){
          RemindEventDTO remindEventDTO = txnService.getRemindEventById(remindEventId);
          if(remindEventDTO != null) {
            if (RemindEventType.CUSTOMER_SERVICE.equals(RemindEventType.valueOf(remindEventDTO.getEventType()))) {
              remindEventDTO.setRemindStatus(UserConstant.Status.REMINDED);
              txnService.updateRemindEvent(remindEventDTO);
            } else {
              if (remindEventDTO.getCustomerId() != null) {
                List<RemindEventDTO> remindEventDTOs = txnService.getRemindEventListByCustomerIdAndType(RemindEventType.valueOf(remindEventDTO.getEventType()), remindEventDTO.getCustomerId());
                if (CollectionUtils.isNotEmpty(remindEventDTOs)) {
                  for (RemindEventDTO remindEventDTO1 : remindEventDTOs) {
                    remindEventDTO1.setRemindStatus(UserConstant.Status.REMINDED);
                    txnService.updateRemindEvent(remindEventDTO1);
                  }
                }
              }
              if (remindEventDTO.getSupplierId() != null) {
                List<RemindEventDTO> remindEventDTOs = txnService.getRemindEventListBySupplierIdAndType(RemindEventType.valueOf(remindEventDTO.getEventType()), remindEventDTO.getSupplierId());
                if (CollectionUtils.isNotEmpty(remindEventDTOs)) {
                  for (RemindEventDTO remindEventDTO1 : remindEventDTOs) {
                    remindEventDTO1.setRemindStatus(UserConstant.Status.REMINDED);
                    txnService.updateRemindEvent(remindEventDTO1);
                  }
                }
              }
            }
          }
        }
      }
      sw.stopAndStart("shop plan related");
      String shopPlanIdStr = request.getParameter("shopPlanId");
      if (StringUtils.isNotBlank(shopPlanIdStr)) {
        try {
          shopPlanService.updateStatus(shopId, Long.valueOf(shopPlanIdStr), PlansRemindStatus.reminded);
        } catch (Exception e) {
          LOG.error("未能更新本店计划提醒");
          LOG.error("shopId{}", shopId);
          LOG.error("shopPlanId{}", shopPlanIdStr);
          LOG.error(e.getMessage(), e);
        }
      }
      sw.stopAndPrintLog();
      return result;
    }catch (Exception e){
      LOG.error(e.getMessage(),e);
      return null;
    }
  }

  @RequestMapping(params = "method=setsms")
  public String setSms(HttpServletRequest request) throws Exception {
    return "/sms/smswrite";
  }

  @RequestMapping(params = "method=smswrite")
  public String smsWrite(HttpServletRequest request,ModelMap modelMap,String contactIds,CustomerRemindSms customerRemindSms) {
    Long shopId=WebUtil.getShopId(request);
    Long userId=WebUtil.getUserId(request);
    String from = request.getParameter("from");
    try {
      //客户欠款提醒ID，客户服务提醒ID，会员服务提醒ID，传入短信编辑页面，发送或预设时间后更改提醒状态
      String remindEventIdStr = request.getParameter("remindEventId");
      modelMap.put("remindEventId", remindEventIdStr);

      customerRemindSms.setShopId(shopId);
      IContactService contactService = ServiceManager.getService(IContactService.class);
      ISmsService smsService = ServiceManager.getService(ISmsService.class);
      IUserService userService = ServiceManager.getService(IUserService.class);
      List<UserDTO> userDTOs = userService.getShopUser(shopId);
      if (CollectionUtils.isNotEmpty(userDTOs)) {
        UserDTO userDTO = userDTOs.get(0);
        if (userDTO != null) {
          customerRemindSms.setUserName(userDTO.getName());
        }
      }
      modelMap.put("smsSendScene", SmsSendScene.getSmsSendSceneByRemindType(customerRemindSms.getType()));
      // 根据 type 获得模板
      customerRemindSms = smsService.sendCustomerServiceRemindMessage(customerRemindSms);
      if (customerRemindSms != null) {
        modelMap.put("templateFlag", customerRemindSms.isTemplateFlag());
        modelMap.put("sendMobile", customerRemindSms.getTitle());
        modelMap.put("smsContent", customerRemindSms.getContent());
      }


      if("sendPromotionsMsg".equals(from)){
        if(StringUtil.isNotEmpty(request.getParameter("customerIds"))){
          String [] customerIds=request.getParameter("customerIds").split(",");
          List<CustomerDTO> customerDTOs=ServiceManager.getService(CustomerService.class).getShopRelatedCustomer(WebUtil.getShopId(request),ArrayUtil.toLongArr(customerIds));
          List<CustomerDTO> customerDTOList=new ArrayList<CustomerDTO>();
          if(CollectionUtil.isNotEmpty(customerDTOs)){
            for(CustomerDTO customerDTO:customerDTOs){
              if(StringUtils.isNotEmpty(customerDTO.getMobile())){
                customerDTOList.add(customerDTO);
              }
            }
          }
          modelMap.put("customerDTOList",customerDTOList);
        }
        modelMap.put("fromPage","sendPromotionsMsg");
        modelMap.put("pMsg",request.getParameter("pContent"));
        modelMap.put("from_sendPromotionsMsg",true);
      }

      IRecentlyUsedDataService recentlyUsedDataService = ServiceManager.getService(IRecentlyUsedDataService.class);
      int maxSize = ConfigUtils.getRecentlyUsedSmsContactNum();
      List<RecentlyUsedDataDTO> recentlyUsedDataDTOList = recentlyUsedDataService.getRecentlyUsedDataDTOList(shopId,userId, RecentlyUsedDataType.USED_SMS_CONTACT,maxSize);
      if(CollectionUtils.isNotEmpty(recentlyUsedDataDTOList)){
        List<Long> recentlyUsedDataIdList = new ArrayList<Long>();
        for(RecentlyUsedDataDTO recentlyUsedDataDTO:recentlyUsedDataDTOList){
          recentlyUsedDataIdList.add(recentlyUsedDataDTO.getDataId());
        }
        Map<Long,ContactDTO> contactDTOMap = userService.getContactDTOMapByIdFormContactVehicle(shopId,recentlyUsedDataIdList.toArray(new Long[recentlyUsedDataIdList.size()]));
        List<ContactDTO> recentlyUsedContactDTOList = new ArrayList<ContactDTO>();
        ContactDTO contactDTO = null;
        Set<Long> customerIdSet = new HashSet<Long>();
        Set<Long> supplierIdSet = new HashSet<Long>();
        for(RecentlyUsedDataDTO recentlyUsedDataDTO:recentlyUsedDataDTOList){
          contactDTO = contactDTOMap.get(recentlyUsedDataDTO.getDataId());
          if(contactDTO!=null){
            recentlyUsedContactDTOList.add(contactDTO);
            if(contactDTO.getSupplierId()!=null) supplierIdSet.add(contactDTO.getSupplierId());
            if(contactDTO.getCustomerId()!=null) customerIdSet.add(contactDTO.getCustomerId());
          }
        }
        fillCustomerOrSupplierNameToContact(shopId, recentlyUsedContactDTOList, customerIdSet, supplierIdSet);
        modelMap.put("recentlyUsedContactDTOs", SmsHelper.preProcessContact(recentlyUsedContactDTOList));
      }
      ISearchCustomerSupplierService searchCustomerSupplierService = ServiceManager.getService(ISearchCustomerSupplierService.class);
      CustomerSupplierSearchConditionDTO customerSupplierSearchConditionDTO = new CustomerSupplierSearchConditionDTO();
      customerSupplierSearchConditionDTO.setSearchStrategies(new CustomerSupplierSearchConditionDTO.SearchStrategy[]{CustomerSupplierSearchConditionDTO.SearchStrategy.mobileNotEmpty});
      customerSupplierSearchConditionDTO.setShopId(shopId);
      customerSupplierSearchConditionDTO.setRows(Integer.MAX_VALUE);
      customerSupplierSearchConditionDTO.setStart(0);
      CustomerSupplierSearchResultListDTO customerSupplierSearchResultListDTO = searchCustomerSupplierService.queryContact(customerSupplierSearchConditionDTO);
      modelMap.put("allContactDTOs",SmsHelper.preProcessContact(customerSupplierSearchResultListDTO.getContactDTOList()));
      modelMap.put("contactGroupDTOs", userService.getContactGroup());
      modelMap.put("smsId",request.getParameter("smsId"));
      String mobile = request.getParameter("mobile");
      Long customerId = NumberUtil.longValue(request.getParameter("customerId"));
      if(StringUtils.isBlank(contactIds) && customerId!=null && StringUtils.isNotBlank(mobile)){
        ContactDTO contactDTO = contactService.getContactDTO(shopId,customerId,null,mobile);;
        if(contactDTO!=null) contactIds = contactDTO.getId().toString();
      }
      Long supplierId = NumberUtil.longValue(request.getParameter("supplierId"));
      if(StringUtils.isBlank(contactIds) && supplierId!=null && StringUtils.isNotBlank(mobile)){
        ContactDTO contactDTO = contactService.getContactDTO(shopId,null,supplierId,mobile);
        if(contactDTO!=null) contactIds = contactDTO.getId().toString();
      }

      modelMap.put("contactIds",contactIds);
      modelMap.put("shopPlanId",request.getParameter("shopPlanId"));
      modelMap.put("templateId",request.getParameter("templateId"));
      modelMap.put("mobile",mobile);
      modelMap.put("contactName",request.getParameter("contactName"));
      modelMap.put("excludeContact", request.getParameter("excludeContact"));
    } catch (Exception e) {
      LOG.debug("/sms.do?method=smswrite");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(),e);
    }
    return "/sms/smswrite";
  }

  private void fillCustomerOrSupplierNameToContact(Long shopId, List<ContactDTO> contactDTOList, Set<Long> customerIdSet, Set<Long> supplierIdSet) {
    Map<Long, CustomerDTO> customerDTOMap = new HashMap<Long, CustomerDTO>();
    if(CollectionUtils.isNotEmpty(customerIdSet)){
      customerDTOMap = ServiceManager.getService(ICustomerService.class).getCustomerByIdSet(shopId,customerIdSet);
    }
    Map<Long, SupplierDTO> supplierDTOMap = new HashMap<Long, SupplierDTO>();
    if(CollectionUtils.isNotEmpty(supplierIdSet)){
      supplierDTOMap = ServiceManager.getService(ISupplierService.class).getSupplierByIdSet(shopId,supplierIdSet);
    }
    for(ContactDTO contactDTO:contactDTOList) {
      String customerOrSupplierName = null;
      if(StringUtils.isBlank(customerOrSupplierName) && contactDTO.getCustomerId()!=null){
        CustomerDTO customerDTO = customerDTOMap.get(contactDTO.getCustomerId());
        customerOrSupplierName = customerDTO==null?null:customerDTO.getName();
      }
      if(StringUtils.isBlank(customerOrSupplierName) && contactDTO.getSupplierId()!=null){
        SupplierDTO supplierDTO = supplierDTOMap.get(contactDTO.getSupplierId());
        customerOrSupplierName = supplierDTO==null?null:supplierDTO.getName();
      }
      contactDTO.setCustomerOrSupplierName(customerOrSupplierName);
    }
  }

  @RequestMapping(params = "method=toSmsWriteSelection")
  public String toSmsWriteSelection(HttpServletRequest request,ModelMap modelMap) throws Exception {
    Long smsId=NumberUtil.longValue(request.getParameter("smsId"));
    String contactIds=StringUtil.valueOf(request.getParameter("contactIds"));
    Long templateId=NumberUtil.longValue(request.getParameter("templateId"));
    String smsContent = request.getParameter("smsContent");
    String mobile = request.getParameter("mobile");
    String contactName = request.getParameter("contactName");
    Long shopPlanId = NumberUtil.longValue(request.getParameter("shopPlanId"));
    String excludeContact = request.getParameter("excludeContact");
    modelMap.put("isWholesalerVersion",ConfigUtils.isWholesalerVersion(WebUtil.getShopVersionId(request)));
    SmsDTO smsDTO=new SmsDTO();
    try{
      Long shopId=WebUtil.getShopId(request);
      INotificationService notificationService=ServiceManager.getService(INotificationService.class);
      IUserService userService = ServiceManager.getService(IUserService.class);
      IContactService contactService = ServiceManager.getService(IContactService.class);
      if(smsId!=null){   //从草稿箱列表或短信详细
        smsDTO=notificationService.getSmsDTOById(shopId,smsId);
        if(smsDTO==null) return "/sms/smsDetail";
        smsDTO.setSmsDraftId(smsId);
        if(StringUtils.isBlank(excludeContact) || !excludeContact.equals("true")){
          if(StringUtil.isNotEmpty(smsDTO.getContactGroupIds())){
            Long[] contactGroupIds=ArrayUtil.toLongArr(smsDTO.getContactGroupIds().split(","));
            smsDTO.setContactGroupDTOs(userService.getContactGroupByIds(contactGroupIds));
          }
          ServiceManager.getService(ISendSmsService.class).generateSmsContactBySpecialId(shopId, smsDTO);
        }
      }else {
        if (StringUtil.isNotEmpty(contactIds)) {//从联系人列表跳转
          smsDTO.setContactIds(contactIds);
          ServiceManager.getService(ISendSmsService.class).generateSmsContactBySpecialId(shopId, smsDTO);
        }else if(StringUtils.isNotBlank(mobile)){
          ContactDTO contactDTO=contactService.getDefaultContactDTO(shopId, mobile);
          if(contactDTO!=null){
            List<ContactDTO> contactDTOList =new ArrayList<ContactDTO>();
            String customerOrSupplierName = null;
            if(StringUtils.isBlank(customerOrSupplierName) && contactDTO.getCustomerId()!=null){
              CustomerDTO customerDTO = ServiceManager.getService(ICustomerService.class).getCustomerById(contactDTO.getCustomerId(),shopId);
              customerOrSupplierName = customerDTO==null?null:customerDTO.getName();
            }
            if(StringUtils.isBlank(customerOrSupplierName) && contactDTO.getSupplierId()!=null){
              SupplierDTO supplierDTO = ServiceManager.getService(ISupplierService.class).getSupplierById(contactDTO.getSupplierId(),shopId);
              customerOrSupplierName = supplierDTO==null?null:supplierDTO.getName();
            }
            contactDTO.setCustomerOrSupplierName(customerOrSupplierName);
            contactDTOList.add(contactDTO);
            smsDTO.setContactDTOs(contactDTOList);
          }else{
            contactDTO = new ContactDTO(contactName,mobile);
            List<ContactDTO> contactDTOList =new ArrayList<ContactDTO>();
            contactDTOList.add(contactDTO);
            smsDTO.setContactDTOs(contactDTOList);
          }
        }else if(shopPlanId!=null) {
          ShopPlanDTO shopPlanDTO = ServiceManager.getService(IShopPlanService.class).getPlanDTO(shopId, shopPlanId);
          if (shopPlanDTO != null && StringUtils.isNotBlank(shopPlanDTO.getUserInfo())) {
            List<Map<String,String>> userInfoList = (List)JsonUtil.jsonToObject(shopPlanDTO.getUserInfo(),List.class);
            if(CollectionUtils.isNotEmpty(userInfoList)){
              Set<Long> contactGroupIdSet = new HashSet<Long>();
              Map<Long,String> customerOrSupplierIdMobileMap = new HashMap<Long, String>();
              List<ContactGroupDTO> contactGroupDTOList =  userService.getContactGroup();
              Map<ContactGroupType,Long> contactGroupTypeLongMap = new HashMap<ContactGroupType, Long>();
              for(ContactGroupDTO contactGroupDTO : contactGroupDTOList){
                contactGroupTypeLongMap.put(contactGroupDTO.getContactGroupType(),contactGroupDTO.getId());
              }
              List<ContactDTO> contactDTOList = new ArrayList<ContactDTO>();
              for(Map<String,String> infoMap : userInfoList){
                String pMobile = infoMap.get("mobile");
                if (TxnConstant.ALL_PERSON.equals(pMobile) || TxnConstant.ALL_PHONE_CONTACTS.equals(pMobile)) {
                  contactGroupIdSet.add(contactGroupTypeLongMap.get(ContactGroupType.CUSTOMER));
                  contactGroupIdSet.add(contactGroupTypeLongMap.get(ContactGroupType.SUPPLIER));
                } else if (TxnConstant.ALL_CUSTOMER.equals(pMobile)) {
                  contactGroupIdSet.add(contactGroupTypeLongMap.get(ContactGroupType.CUSTOMER));
                } else if (TxnConstant.ALL_SUPPLIER.equals(pMobile)) {
                  contactGroupIdSet.add(contactGroupTypeLongMap.get(ContactGroupType.SUPPLIER));
                } else if (TxnConstant.ALL_MEMBER.endsWith(pMobile)) {
                  contactGroupIdSet.add(contactGroupTypeLongMap.get(ContactGroupType.MEMBER));
                } else {
                  Long customerOrSupplierId = NumberUtil.longValue(infoMap.get("userId"));
                  if(RegexUtils.isMobile(pMobile)){
                    if(customerOrSupplierId!=null){
                      customerOrSupplierIdMobileMap.put(customerOrSupplierId, pMobile);
                    }else{
                      ContactDTO contactDTO = CollectionUtil.getFirst(contactService.getContactByCusOrSupOrShopIdOrName(null,null,shopId,null,pMobile));
                      if(contactDTO!=null){
                        contactDTOList.add(contactDTO);
                      }else{
                        contactDTOList.add(new ContactDTO(infoMap.get("name"),pMobile));
                      }
                    }
                  }
                }
              }
              if(MapUtils.isNotEmpty(customerOrSupplierIdMobileMap)){
                Map<Long, List<ContactDTO>> contactListMap =  contactService.getContactsByCustomerOrSupplierIds(new ArrayList<Long>(customerOrSupplierIdMobileMap.keySet()),"customer");
                contactListMap.putAll(contactService.getContactsByCustomerOrSupplierIds(new ArrayList<Long>(customerOrSupplierIdMobileMap.keySet()),"supplier"));
                List<ContactDTO> contactDTOs = null;
                for(Map.Entry<Long,String> entry:customerOrSupplierIdMobileMap.entrySet()){
                  contactDTOs = contactListMap.get(entry.getKey());
                  if(CollectionUtils.isNotEmpty(contactDTOs)){
                    for(ContactDTO contactDTO:contactDTOs){
                      if(entry.getValue().equals(contactDTO.getMobile())){
                        contactDTOList.add(contactDTO);
                        break;
                      }
                    }
                  }
                }
              }
              if(CollectionUtils.isNotEmpty(contactGroupIdSet)){
                smsDTO.setContactGroupDTOs(ServiceManager.getService(IUserService.class).getContactGroupByIds(contactGroupIdSet.toArray(new Long[contactGroupIdSet.size()])));
              }
              smsDTO.setContactDTOs(contactDTOList);
            }
          }
        }
        if(CollectionUtils.isNotEmpty(smsDTO.getContactDTOs())){
          Set<Long> customerIdSet = new HashSet<Long>();
          Set<Long> supplierIdSet = new HashSet<Long>();
          for(ContactDTO contactDTO:smsDTO.getContactDTOs()) {
            if (contactDTO.getSupplierId() != null) supplierIdSet.add(contactDTO.getSupplierId());
            if (contactDTO.getCustomerId() != null) customerIdSet.add(contactDTO.getCustomerId());
          }
          fillCustomerOrSupplierNameToContact(shopId, smsDTO.getContactDTOs(), customerIdSet, supplierIdSet);
          smsDTO.setContactDTOs(SmsHelper.preProcessContact(smsDTO.getContactDTOs()));
        }

        if(templateId!=null){
          MessageTemplateDTO templateDTO = notificationService.getShopMsgTemplateDTOById(shopId, templateId);
          smsDTO.setContent(templateDTO != null ? templateDTO.getContent() : null);
        }
        if (StringUtils.isNotBlank(smsContent)) {
          smsDTO.setContent(smsContent);
        }
      }

      ShopDTO shopDTO=ServiceManager.getService(IConfigService.class).getShopById(WebUtil.getShopId(request));
      if(shopDTO!=null){
        modelMap.put("shopName",StringUtils.isNotBlank(shopDTO.getShortname())?shopDTO.getShortname():shopDTO.getName());
      }
      modelMap.put("templateFlag",StringUtil.toTrim(request.getParameter("templateFlag")));
    }catch (Exception e){
      LOG.error(e.getMessage(),e);
    }
    modelMap.put("smsDTOJson",JsonUtil.objectToJson(smsDTO));
    return "/sms/smsWriteSelection";
  }

  @RequestMapping(params = "method=toSmsDetail")
  public String toSmsDetail(ModelMap modelMap,HttpServletRequest request,Long smsId) throws Exception {
    INotificationService notificationService= ServiceManager.getService(INotificationService.class);
    IUserService userService=ServiceManager.getService(IUserService.class);
    Long shopId=WebUtil.getShopId(request);
    SmsDTO smsDTO=notificationService.getSmsDTOById(shopId,smsId);
    if(smsDTO==null) return "/sms/smsDetail";
    if(StringUtil.isNotEmpty(smsDTO.getContactGroupIds())){
      Long[] contactGroupIds=ArrayUtil.toLongArr(smsDTO.getContactGroupIds().split(","));
      smsDTO.setContactGroupDTOs(userService.getContactGroupByIds(contactGroupIds));
    }
    ServiceManager.getService(ISendSmsService.class).generateSmsContactBySpecialId(shopId, smsDTO);
    smsDTO.setSendTimeStr(DateUtil.convertDateLongToDateString(DateUtil.STANDARD_CN, smsDTO.getSendTime()));
    UserDTO userDTO=userService.getUserByUserId(smsDTO.getUserId());
    smsDTO.setUserName(userDTO!=null?userDTO.getUserName():"");
    modelMap.put("smsDTO", smsDTO);
    SmsSearchCondition condition=new SmsSearchCondition();
    condition.setShopId(shopId);
    condition.setSmsType(smsDTO.getSmsType());
    modelMap.put("smsType",smsDTO.getSmsType());
    modelMap.put("rowStart",request.getParameter("rowStart"));
    modelMap.put("viewFlag",request.getParameter("viewFlag"));
    return "/sms/smsDetail";
  }

  @RequestMapping(params = "method=getDefaultContactDTO")
  @ResponseBody
  public Object getDefaultContactDTO(HttpServletRequest request,String mobile){
    try{
      IContactService contactService=ServiceManager.getService(IContactService.class);
      ContactDTO contactDTO=contactService.getDefaultContactDTO(WebUtil.getShopId(request),mobile);
      return contactDTO==null?new ContactDTO():contactDTO;
    }catch (Exception e){
      LOG.error(e.getMessage(),e);
      return new ContactDTO();
    }
  }

  @RequestMapping(params = "method=toSmsSendFinish")
  public String toSmsSendFinish(ModelMap modelMap,HttpServletRequest request,Long smsId){
    try{
      Long shopId = WebUtil.getShopId(request);
      if(smsId == null) throw new Exception("smsId is null");
      modelMap.put("smsId",smsId);
      SmsDTO smsDTO=ServiceManager.getService(INotificationService.class).getSmsDTOById(shopId,smsId);
      if(smsDTO!=null){
        modelMap.put("smsType",smsDTO.getSmsType());
        //判断是否过滤了重复的手机
        boolean hasMobileDumplicated = ServiceManager.getService(IUserService.class).hasMobileDumplicated(shopId, smsDTO);
        List<String> mobiles = ServiceManager.getService(IUserService.class).filterGetMobilesFromSmsDTO(shopId, smsDTO);
        String mobilesArray = CollectionUtil.collectionToCommaString(mobiles);
        String mobileArrayStr = SmsUtil.filterMobiles(mobilesArray);
        modelMap.put("hasFiltered",hasMobileDumplicated);
        modelMap.put("countSmsSent",NumberUtil.intValue(smsDTO.getCountSmsSent()));
        modelMap.put("countAppSent",NumberUtil.intValue(smsDTO.getCountAppSent()));
        modelMap.put("smsFlag",smsDTO.getSmsFlag());
        modelMap.put("appFlag",smsDTO.getAppFlag());
        modelMap.put("countMobile",mobileArrayStr.split(",").length);
      }
    }catch (Exception e){
      LOG.error(e.getMessage(),e);
    }
    return "/sms/smsSendFinish";
  }



  @RequestMapping(params = "method=getSmsStat")
  @ResponseBody
  public Object getSmsStat(HttpServletRequest request){
    INotificationService notificationService=ServiceManager.getService(INotificationService.class);
    SmsSearchCondition condition=new SmsSearchCondition();
    condition.setShopId(WebUtil.getShopId(request));
    Map<String,Object> smsStat=new HashMap<String, Object>();
    condition.setSmsType(SmsType.SMS_DRAFT);
    smsStat.put("sms_draft_num",notificationService.countSms(condition));
    condition.setSmsType(SmsType.SMS_SEND);
    smsStat.put("sms_send_num", notificationService.countSms(condition));
    condition.setSmsType(SmsType.SMS_SENT);
    smsStat.put("sms_sent_num", notificationService.countSms(condition));
    ShopBalanceDTO shopBalanceDTO = ServiceManager.getService(IShopBalanceService.class).getSmsBalanceByShopId(condition.getShopId());
    smsStat.put("sms_balance",shopBalanceDTO!=null?shopBalanceDTO.getSmsBalance():0);
    return  smsStat;
  }


  //发送提醒短信
  @RequestMapping(params = "method=sendRemindMsg")
  public String sendRemindMsg(HttpServletRequest request) throws Exception {
    String licenceNo = request.getParameter("licenceNo");
    String name = request.getParameter("name");
    String mobile = request.getParameter("mobile");
    String date = request.getParameter("date");
    String type = request.getParameter("type");

    licenceNo = getZwString(licenceNo);
    name = getZwString(name);
    type = getZwString(type);

    String smsContent = "";

    int remindType = 0;
    if (type.trim().equals("保险")) {
      smsContent = "保险";
    }

    request.setAttribute("mobile", mobile);
    request.setAttribute("date", date);
    request.setAttribute("smsContent", smsContent);

    return "/sms/smswrite";
  }

  //查看短信余额
  @RequestMapping(params = "method=checkSmsBalance")
  public void checkSmsBalance(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    PrintWriter out = response.getWriter();
    if (shopId != null) {
      IShopBalanceService shopBalanceService = ServiceManager.getService(IShopBalanceService.class);
      ShopBalanceDTO shopBalanceDTO = shopBalanceService.getSmsBalanceByShopId(shopId);
      Double smsBalance = 0d;
      if (shopBalanceDTO != null && shopBalanceDTO.getSmsBalance() != null) {
        smsBalance = shopBalanceDTO.getSmsBalance();
      }
      out.write(String.valueOf(smsBalance));
    } else {
      out.write(" ");
    }
    out.flush();
    out.close();
  }

  private static String getZwString(String str) {
    if (str == null) {
      return null;
    }
    try {
      str = new String(str.getBytes("ISO-8859-1"));
    } catch (Exception e) {
      LOG.debug("/sms.do");
      LOG.debug("str:" + str);
      LOG.error(e.getMessage(), e);
    }
    return str;
  }

  @RequestMapping(params = "method=getMobiles")
  @ResponseBody
  public Map getMobiles(HttpServletRequest request, HttpServletResponse response) {
    ISearchCustomerSupplierService searchService = ServiceManager.getService(ISearchCustomerSupplierService.class);
    String mobiles = request.getParameter("mobiles");
    Map<String, String> mobileMap = new HashMap<String, String>();
    mobileMap.put("mobiles", "");
    Map<String, String> map = new HashMap<String, String>();
    try {
      CustomerSupplierSearchConditionDTO searchConditionDTO = new CustomerSupplierSearchConditionDTO();
      searchConditionDTO.setShopId(WebUtil.getShopId(request));
      CustomerSupplierSearchResultListDTO searchResultListDTO = null;
      if (StringUtils.isBlank(mobiles)) {
        return mobileMap;
      }
      if (StringUtils.isNotBlank(mobiles) || mobiles.contains(TxnConstant.ALL_PERSON) || mobiles.contains(TxnConstant.ALL_CUSTOMER) ||
              mobiles.contains(TxnConstant.ALL_SUPPLIER) || mobiles.contains(TxnConstant.ALL_MEMBER) || mobiles.contains(TxnConstant.ALL_PHONE_CONTACTS)) {
        String[] mobileArr = mobiles.split(",");

        for (int i = 0; i < mobileArr.length; i++) {
          String mobile = "";
          if (TxnConstant.ALL_PERSON.equals(mobileArr[i]) || TxnConstant.ALL_PHONE_CONTACTS.equals(mobileArr[i])) {
            //solr中获取有手机的全体用户遍历获取手机号，正确的放入map中
            searchResultListDTO = searchService.queryCustomerMobiles(searchConditionDTO);
            putMobileFromCustomerOrSupplierOrMember(searchResultListDTO, map);
          } else if (TxnConstant.ALL_CUSTOMER.equals(mobileArr[i])) {
            searchConditionDTO.setCustomerOrSupplier("customer");
            //solr中获取有手机的全体k客户遍历获取手机号，正确的放入map中
            searchResultListDTO = searchService.queryCustomerMobiles(searchConditionDTO);
            putMobileFromCustomerOrSupplierOrMember(searchResultListDTO, map);
          } else if (TxnConstant.ALL_SUPPLIER.equals(mobileArr[i])) {
            searchConditionDTO.setCustomerOrSupplier("supplier");
            //solr中获取有手机的全体供应商遍历获取手机号，正确的放入map中
            searchResultListDTO = searchService.queryCustomerMobiles(searchConditionDTO);
            putMobileFromCustomerOrSupplierOrMember(searchResultListDTO, map);
          } else if (TxnConstant.ALL_MEMBER.endsWith(mobileArr[i])) {
            List<String> memberCardTypes = ServiceManager.getService(IMembersService.class).getMemberCardTypeByShopId(searchConditionDTO.getShopId());
            searchConditionDTO.setMemberType(CollectionUtil.collectionToCommaString(memberCardTypes));
            //solr中获取有手机的全体会员遍历获取手机号，正确的放入map中
            searchResultListDTO = searchService.queryCustomerMobiles(searchConditionDTO);
            putMobileFromCustomerOrSupplierOrMember(searchResultListDTO, map);
          } else {
            String newMobile = mobileArr[i].trim();
            if (StringUtils.isNotBlank(newMobile) && newMobile.length() == 11 && newMobile.substring(0, 1).equals("1") && isNumeric(newMobile)) {
              map.put(newMobile, newMobile);
            }
          }
        }
        Set sets = map.keySet();
        mobileMap.put("mobiles", sets.toString().substring(1, sets.toString().length() - 1));
      }
    } catch (Exception e) {
      LOG.error("sms.do?method=getMobiles 出错");
      LOG.error(e.getMessage(), e);
    } finally {
      return mobileMap;
    }
  }

  public void putMobileFromCustomerOrSupplierOrMember(CustomerSupplierSearchResultListDTO searchResultListDTO, Map<String, String> map) {
    if (null == searchResultListDTO || CollectionUtils.isEmpty(searchResultListDTO.getCustomerSuppliers())) {
      return;
    }

    for (CustomerSupplierSearchResultDTO searchResultDTO : searchResultListDTO.getCustomerSuppliers()) {
      if(CollectionUtils.isNotEmpty(searchResultDTO.getContactDTOList())){
        for(ContactDTO contactDTO : searchResultDTO.getContactDTOList()){
          String mobile = contactDTO.getMobile().trim();
          if (StringUtils.isNotBlank(mobile) && mobile.length() == 11 && mobile.substring(0, 1).equals("1") && isNumeric(mobile)) {
            map.put(mobile, mobile);
          }
        }
      }
    }
  }

  public boolean isNumeric(String str) {
    if (str.matches("\\d*")) {
      return true;
    } else {
      return false;
    }
  }

  @RequestMapping(params = "method=getMobileMsgContent")
  @ResponseBody
  public Object getMobileMsgContent(HttpServletRequest request,CustomerRemindSms customerRemindSms) {
    Long shopId=WebUtil.getShopId(request);
    try {
      if(shopId==null) throw new Exception("shopId is null!");
      customerRemindSms.setShopId(shopId);
      customerRemindSms = ServiceManager.getService(ISmsService.class).sendCustomerServiceRemindMessage(customerRemindSms);
      return customerRemindSms;
    } catch (Exception e) {
      LOG.debug("/sms.do?method=getMobileMsgContent");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(),e);
    }
    return null;
  }
}
