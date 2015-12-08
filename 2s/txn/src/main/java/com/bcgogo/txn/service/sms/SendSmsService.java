package com.bcgogo.txn.service.sms;

import com.bcgogo.api.AppUserDTO;
import com.bcgogo.common.Result;
import com.bcgogo.config.dto.ShopBalanceDTO;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.service.IShopBalanceService;
import com.bcgogo.enums.SolrIdPrefix;
import com.bcgogo.enums.notification.ContactGroupType;
import com.bcgogo.enums.notification.SmsChannel;
import com.bcgogo.enums.notification.SmsType;
import com.bcgogo.enums.sms.SenderType;
import com.bcgogo.enums.sms.SmsSendScene;
import com.bcgogo.notification.SmsHelper;
import com.bcgogo.notification.dto.ContactGroupDTO;
import com.bcgogo.notification.dto.SmsDTO;
import com.bcgogo.notification.dto.SmsIndexDTO;
import com.bcgogo.notification.dto.SmsJobDTO;
import com.bcgogo.notification.model.SmsJob;
import com.bcgogo.notification.service.INotificationService;
import com.bcgogo.notification.smsSend.SmsUtil;
import com.bcgogo.search.dto.CustomerSupplierSearchConditionDTO;
import com.bcgogo.search.dto.CustomerSupplierSearchResultListDTO;
import com.bcgogo.search.service.user.ISearchCustomerSupplierService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.service.solr.ICustomerOrSupplierSolrWriteService;
import com.bcgogo.user.dto.ContactDTO;
import com.bcgogo.user.dto.CustomerDTO;
import com.bcgogo.user.model.Contact;
import com.bcgogo.user.model.Customer;
import com.bcgogo.user.service.IContactService;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.user.service.app.IAppUserCustomerMatchService;
import com.bcgogo.user.service.app.IAppUserService;
import com.bcgogo.utils.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 14-2-11
 * Time: 下午5:09
 * To change this template use File | Settings | File Templates.
 */
@Component
public class SendSmsService implements ISendSmsService{
  private static final Logger LOG = LoggerFactory.getLogger(SendSmsService.class);

  private Result validateSendSms(Result result,Long shopId,SmsDTO smsDTO) throws Exception {
    smsDTO.setAppFlag(smsDTO.getAppFlag()==null?false:smsDTO.getAppFlag());
    smsDTO.setSmsFlag(smsDTO.getSmsFlag()==null?false:smsDTO.getSmsFlag());
    if(!smsDTO.getAppFlag()&&!smsDTO.getSmsFlag()){
      return result.LogErrorMsg("请选择发送短信或者APP消息");
    }
    if(StringUtil.isEmpty(smsDTO.getContent())){
      return result.LogErrorMsg("请填写短信内容。");
    }
    //发送app消息
    if(smsDTO.getAppFlag()){
      if(!Boolean.TRUE.equals(smsDTO.getSmsFlag())&& CollectionUtil.isEmpty(smsDTO.getAppUserNos())){
        return result.LogErrorMsg("APP客户不存在，发送APP消息失败。");
      }
    }
    if(smsDTO.getSmsFlag()){
       List<String> mobiles=ServiceManager.getService(IUserService.class).filterGetMobilesFromSmsDTO(shopId,smsDTO);
       if(!Boolean.TRUE.equals(smsDTO.getAppFlag())&& CollectionUtil.isEmpty(mobiles)){
        return result.LogErrorMsg("客户手机号为空，发送短信失败。");
      }
    }
    if(smsDTO.getSmsFlag()){
      IShopBalanceService shopBalanceService = ServiceManager.getService(IShopBalanceService.class);
      //判断余额
      ShopBalanceDTO shopBalanceDTO = shopBalanceService.getSmsBalanceByShopId(shopId);
      if (shopBalanceDTO != null) {
        double balance = shopBalanceDTO.getSmsBalance();   //单位 元
        //余额小于5元 提醒
        if (balance < 5) {
          return result.LogErrorMsg("您的短信余额不足5元,请及时充值!");
        }
        //欠款提醒
        if (balance < 0) {
          return result.LogErrorMsg("您的短信欠款已达"+(0 - balance)+"元,请及时充值!");
        }
      }
    }
    return result;
  }

  @Override
  public void prepareForSaveSms(Long shopId,SmsDTO smsDTO) throws Exception {
    if(CollectionUtil.isNotEmpty(smsDTO.getContactGroupDTOs())){
      List<ContactGroupDTO> groupDTOs=smsDTO.getContactGroupDTOs();
      StringBuilder sb=new StringBuilder();
      for(ContactGroupDTO groupDTO:groupDTOs){
        if(groupDTO==null) continue;
        if(ContactGroupType.APP_CUSTOMER.equals(groupDTO.getContactGroupType())){
          groupDTO.setAppCustomerFlag(true);
        }
        sb.append(groupDTO.getId());
        sb.append(",");
      }
      smsDTO.setContactGroupIds(sb.toString().substring(0,sb.length()-1));
    }
    //处理联系人
    if(CollectionUtil.isNotEmpty(smsDTO.getContactDTOs())){
      IUserService userService=ServiceManager.getService(IUserService.class);
      List<ContactDTO> contactDTOs=smsDTO.getContactDTOs();
      StringBuilder sb=new StringBuilder();
      List<Long> contactIdList = new ArrayList<Long>();
      for(ContactDTO contactDTO:contactDTOs){
        if(contactDTO==null) continue;
        if((StringUtil.isEmpty(contactDTO.getSpecialIdStr())&&StringUtil.isNotEmpty(contactDTO.getMobile()))){   //手动添加的联系人
          contactDTO.setShopId(shopId);
          contactDTO.setDisabled(ContactConstant.ENABLED);
          Long contactId = userService.saveOrUpdateContact(contactDTO);
          contactDTO.setSpecialIdStr(SolrIdPrefix.OTHER.toString()+"_"+contactId);
          contactIdList.add(contactId);
        }
        sb.append(contactDTO.getSpecialIdStr());
        sb.append(",");
      }
      smsDTO.setContactIds(sb.toString().substring(0,sb.length()-1));
      if(CollectionUtils.isNotEmpty(contactIdList)){
        ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexOtherContactSolrIndex(shopId,contactIdList.toArray(new Long[contactIdList.size()]));
      }
    }
    smsDTO.setSmsSendScene(smsDTO.getSmsSendScene()==null? SmsSendScene.MANUALLY:smsDTO.getSmsSendScene());
    //generate appUserNos
    List<String> appUserNos=smsDTO.getAppUserNos();
    if(CollectionUtil.isEmpty(appUserNos)) {
      appUserNos=new ArrayList<String>();
      smsDTO.setAppUserNos(appUserNos);
    }
    List<String> appUserNoFromContact=getAppUserNoFromSmsDTO(shopId,smsDTO);
    if(CollectionUtil.isNotEmpty(appUserNoFromContact)){
      appUserNos.addAll(appUserNoFromContact);
    }
  }

  @Override
  public SmsIndexDTO prepareForSaveSmsIndex(Long shopId,Long smsId){
    INotificationService notificationService=ServiceManager.getService(INotificationService.class);
    SmsDTO smsDTO=notificationService.getSmsDTOById(shopId, smsId);
    SmsIndexDTO smsIndexDTO=notificationService.getSmsIndexDTOBySmsId(shopId, smsDTO.getId());
    smsIndexDTO=smsIndexDTO==null?new SmsIndexDTO():smsIndexDTO;
    if(StringUtil.isNotEmpty(smsDTO.getContactGroupIds())){
      Long[] contactGroupIds=ArrayUtil.toLongArr(smsDTO.getContactGroupIds().split(","));
      smsDTO.setContactGroupDTOs(ServiceManager.getService(IUserService.class).getContactGroupByIds(contactGroupIds));
    }
    generateSmsContactBySpecialId(shopId, smsDTO);
    if(CollectionUtil.isNotEmpty(smsDTO.getContactDTOs())){
      IUserService userService=ServiceManager.getService(IUserService.class);
      List<Long> customerIds=new ArrayList<Long>();
      List<Long> supplierIds=new ArrayList<Long>();
      for(ContactDTO contactDTO:smsDTO.getContactDTOs()){
        if(contactDTO.getCustomerId()!=null){
          customerIds.add(contactDTO.getCustomerId());
        }
        if(contactDTO.getSupplierId()!=null){
          supplierIds.add(contactDTO.getSupplierId());
        }
      }
      smsDTO.setCustomerDTOs(userService.getCustomerDTOByIds(shopId,ArrayUtil.toLongArr(customerIds)));
      smsDTO.setSupplierDTOs(userService.getSupplierDTOByIds(shopId, ArrayUtil.toLongArr(supplierIds)));
    }
    smsIndexDTO.fromSmsDTO(smsDTO);
    return smsIndexDTO;
  }

  @Override
  public void generateSmsContactBySpecialId(Long shopId,SmsDTO smsDTO){
    if(StringUtil.isEmpty(smsDTO.getContactIds())) return;
    String [] specialIds=smsDTO.getContactIds().split(",");
    if(ArrayUtil.isEmpty(specialIds)) return;
    IUserService userService=ServiceManager.getService(IUserService.class);
    List<Long> contactIds=new ArrayList<Long>();
    String[] ids=null;
    for(String specialId:specialIds){
      if(specialId.contains("_")){
        ids=specialId.split("_");
        if(ArrayUtil.isEmpty(ids)||ids.length!=2) continue;
        contactIds.add(NumberUtil.toLong(ids[1]));
      }else{
        contactIds.add(NumberUtil.toLong(specialId));
      }
    }
    List<ContactDTO> contactDTOList = userService.getContactDTOByIdFormContactVehicle(shopId,contactIds.toArray(new Long[contactIds.size()]));

    Long customerId = null;
    if (CollectionUtils.isNotEmpty(contactDTOList)) {
      for (ContactDTO contactDTO : contactDTOList) {
        if (contactDTO.getCustomerId() != null) {
          customerId = contactDTO.getCustomerId();
          break;
        }
      }
    }
    if (customerId != null) {
      IUserService customerService = ServiceManager.getService(IUserService.class);
      Customer customer = customerService.getCustomerByCustomerId(customerId, shopId);

      if(customer != null){
        List<CustomerDTO> customerDTOList = new ArrayList<CustomerDTO>();
        customerDTOList.add(customer.toDTO());
        smsDTO.setCustomerDTOs(customerDTOList);
      }
    }

    smsDTO.setContactDTOs(contactDTOList);

  }

  private List<Long> generateAppCustomerIds(Long shopId,SmsDTO smsDTO) throws Exception {
    IContactService contactService=ServiceManager.getService(IContactService.class);
    List<ContactDTO> contactDTOs=null;
    List<Long> contactIds=new ArrayList<Long>();
    if(SmsHelper.hasAppCustomerGroup(smsDTO)){ //app组所有客户
      CustomerSupplierSearchConditionDTO searchConditionDTO=new CustomerSupplierSearchConditionDTO();
      searchConditionDTO.setShopId(shopId);
      searchConditionDTO.setStart(0);
      searchConditionDTO.setRows(Integer.MAX_VALUE);
      searchConditionDTO.setContactGroupType(ContactGroupType.APP_CUSTOMER);
      CustomerSupplierSearchResultListDTO resultListDTO=ServiceManager.getService(ISearchCustomerSupplierService.class).queryContact(searchConditionDTO);
      contactDTOs=resultListDTO.getContactDTOList();
    }else{
      contactDTOs=smsDTO.getContactDTOs();
    }
    contactIds=SmsHelper.genContactIdFromSmsDTO(contactDTOs);
    Set<Long> customerIdSet=contactService.getAppCustomerIdFromContact(shopId,contactIds);
    if(CollectionUtil.isEmpty(customerIdSet)) return null;
    return new ArrayList<Long>(customerIdSet);
  }

   private  List<String> getAppUserNoFromSmsDTO(Long shopId,SmsDTO smsDTO) throws Exception {
    List<ContactDTO> contactDTOs=null;
    if(SmsHelper.hasAppCustomerGroup(smsDTO)){ //app组所有客户
      CustomerSupplierSearchConditionDTO searchConditionDTO=new CustomerSupplierSearchConditionDTO();
      searchConditionDTO.setShopId(shopId);
      searchConditionDTO.setStart(0);
      searchConditionDTO.setRows(Integer.MAX_VALUE);
      searchConditionDTO.setContactGroupType(ContactGroupType.APP_CUSTOMER);
      CustomerSupplierSearchResultListDTO resultListDTO=ServiceManager.getService(ISearchCustomerSupplierService.class).queryContact(searchConditionDTO);
      contactDTOs=resultListDTO.getContactDTOList();
    }else{
      contactDTOs=smsDTO.getContactDTOs();
    }
    List<Long> contactIds=SmsHelper.genContactIdFromSmsDTO(contactDTOs);
    return CollectionUtil.isNotEmpty(contactIds)?ServiceManager.getService(IAppUserService.class).getAppUserNoByVehicleIdOrContactId(contactIds.toArray(new Long[contactIds.size()])):null;
  }

  @Override
  public Result sendSms(Long shopId,Long userId,String content,Boolean appFlag,Boolean smsFlag,Boolean templateFlag,ContactDTO ... contactDTOs) throws Exception {
    if(shopId==null||userId==null||ArrayUtil.isEmpty(contactDTOs)) throw new Exception("illegal parameter!");
    if(!Boolean.TRUE.equals(appFlag)&&!Boolean.TRUE.equals(smsFlag)) throw new Exception("set the send flag!");
    List<ContactDTO> smsContactDTOs=new ArrayList<ContactDTO>();
    for(ContactDTO contactDTO:contactDTOs){
      if(contactDTO.getId()==null&&StringUtil.isEmpty(contactDTO.getMobile())){
        continue;
      }
      contactDTO.setSpecialIdStr(StringUtil.valueOf(contactDTO.getId()));
      smsContactDTOs.add(contactDTO);
    }
    Result result=new Result();
    SmsDTO smsDTO=new SmsDTO();
    smsDTO.setShopId(shopId);
    smsDTO.setUserId(userId);
    smsDTO.setContent(content);
    smsDTO.setAppFlag(appFlag);
    smsDTO.setSmsFlag(smsFlag);
    smsDTO.setTemplateFlag(templateFlag);
    smsDTO.setContactDTOs(smsContactDTOs);
    sendSms(result, smsDTO);
    return result;
  }

  @Override
  public Result sendSms(Long shopId,Long userId,String content,Boolean appFlag,Boolean smsFlag,Boolean templateFlag,String appUserNo,String mobile) throws Exception{
    if(shopId==null||userId==null||StringUtil.isEmpty(appUserNo)) throw new Exception("illegal parameter!");
    if(!Boolean.TRUE.equals(appFlag)&&!Boolean.TRUE.equals(smsFlag)) throw new Exception("set the send flag!");
    List<ContactDTO> smsContactDTOs=new ArrayList<ContactDTO>();
    IContactService contactService=ServiceManager.getService(IContactService.class);
    ContactDTO contactDTO=contactService.getContactDTO(shopId,null,null,mobile);
    if(contactDTO==null){
      contactDTO=new ContactDTO();
      contactDTO.setShopId(shopId);
      contactDTO.setMobile(mobile);
      contactDTO.setMainContact(1);
      contactDTO.setDisabled(1);
      contactService.saveContact(contactDTO);
    }
    smsContactDTOs.add(contactDTO);
    Result result=new Result();
    SmsDTO smsDTO=new SmsDTO();
    smsDTO.setShopId(shopId);
    smsDTO.setUserId(userId);
    smsDTO.setContent(content);
    smsDTO.setAppFlag(appFlag);
    smsDTO.setSmsFlag(smsFlag);
    smsDTO.setTemplateFlag(templateFlag);
    smsDTO.setContactDTOs(smsContactDTOs);
    List<String> appUserNos=new ArrayList<String>();
    appUserNos.add(appUserNo);
    smsDTO.setAppUserNos(appUserNos);
    sendSms(result, smsDTO);
    return result;
  }


  @Override
  public Result sendSms(Result result,SmsDTO smsDTO) throws Exception {
    if(smsDTO==null||result==null) throw new Exception("illegal parameter!");
    Long shopId=smsDTO.getShopId();
    if(shopId==null||smsDTO.getUserId()==null) throw new Exception("shopId can't be null!");
    StopWatchUtil sw = new StopWatchUtil("sendSms", "preapre");
    INotificationService notificationService = ServiceManager.getService(INotificationService.class);
    prepareForSaveSms(shopId,smsDTO);
    result= validateSendSms(result,shopId,smsDTO);
    if(!result.isSuccess()){
      return result;
    }
    sw.stopAndStart("save sms");
    //保存SMS
    if(StringUtil.isNotEmpty(smsDTO.getSendTimeStr())){
      smsDTO.setSendTime(DateUtil.convertDateStringToDateLong(DateUtil.STANDARD, smsDTO.getSendTimeStr()));
      smsDTO.setSmsType(SmsType.SMS_SEND);
    }else{
      smsDTO.setSendTime(System.currentTimeMillis());
      smsDTO.setSmsType(SmsType.SMS_SENT);
    }
    Long smsId=notificationService.saveOrUpdateSms(smsDTO);
    notificationService.saveOrUpdateSmsIndex(prepareForSaveSmsIndex(shopId, smsId));
    result.setDataStr(StringUtil.valueOf(smsId));
    sw.stopAndStart("filter mobile");
    //创建SmsJob

    SmsJobDTO smsJobDTO = new SmsJobDTO();
    if(Boolean.TRUE.equals(smsDTO.getTemplateFlag())){
      smsJobDTO.setSmsChannel(SmsChannel.INDUSTRY);
    } else {
      smsJobDTO.setSmsChannel(SmsChannel.MARKETING);
    }
    smsJobDTO.setShopId(shopId);
    smsJobDTO.setType(SmsConstant.SMS_TYPE_MANUAL);
    smsJobDTO.setSender(SenderType.Shop);
    smsJobDTO.setSmsId(smsId);
    ShopDTO shopDTO = ServiceManager.getService(IConfigService.class).getShopById(shopId);
    if(null != shopDTO){
      smsJobDTO.setShopName(StringUtils.isBlank(shopDTO.getShortname())?shopDTO.getName():shopDTO.getShortname());
    }
    sw.stopAndStart("gen sms job");

    //生成短信的job
    List<String> mobiles=ServiceManager.getService(IUserService.class).filterGetMobilesFromSmsDTO(shopId,smsDTO);
    if(smsDTO.getSmsFlag()&&CollectionUtil.isNotEmpty(mobiles)){
      String mobilesArray = CollectionUtil.collectionToCommaString(mobiles);
      String mobileArrayStr = SmsUtil.filterMobiles(mobilesArray);
      smsDTO.setCountSmsSent(SmsUtil.calculateSmsDTONum(smsDTO.getContent(),(shopDTO.getShortname()==null?shopDTO.getName() : shopDTO.getShortname()))*mobileArrayStr.split(",").length);
      notificationService.saveOrUpdateSms(smsDTO);
      smsJobDTO.setContent(smsDTO.getContent());
      smsJobDTO.setReceiveMobile(mobilesArray);
      smsJobDTO.setStartTime(smsDTO.getSendTime());
      notificationService.sendSmsAsync(smsJobDTO);
    }
    sw.stopAndStart("gen app job");
    //生成APP消息的job
    if(smsDTO.getAppFlag()){
      List<String> appUserNoList=smsDTO.getAppUserNos();
      if(CollectionUtil.isNotEmpty(appUserNoList)){
        smsDTO.setCountAppSent(appUserNoList.size());
        notificationService.saveOrUpdateSms(smsDTO);
        List<SmsJob> smsJobs=new ArrayList<SmsJob>();
        for(String appUserNo:appUserNoList){
          SmsJobDTO smsJobDTOClone=smsJobDTO.clone();
          smsJobDTOClone.setContent(smsDTO.getContent() + "【" + (StringUtils.isBlank(shopDTO.getShortname())?shopDTO.getName():shopDTO.getShortname()) + "】");
          smsJobDTOClone.setAppUserNo(appUserNo);
          smsJobDTOClone.setStartTime(smsDTO.getSendTime());
          smsJobDTOClone.setSmsSendScene(SmsSendScene.APP_MESSAGE);
          SmsJob job = new SmsJob();
          job.fromDTO(smsJobDTOClone);
          smsJobs.add(job);
        }
        notificationService.batchSaveSmsJob(smsJobs);
      }
    }
    sw.stopAndStart("save recent contacts");
    return result;
  }

//  @Override
//  public Result sendSms(Long shopId, Long userId, String content, Boolean appFlag, Boolean smsFlag, Boolean templateFlag, String appUserNo, String mobile) throws Exception{
//    if(shopId==null||userId==null||StringUtil.isEmpty(appUserNo)) throw new Exception("illegal parameter!");
//    if(!Boolean.TRUE.equals(appFlag)&&!Boolean.TRUE.equals(smsFlag)) throw new Exception("set the send flag!");
//    List<ContactDTO> smsContactDTOs=new ArrayList<ContactDTO>();
//    IContactService contactService=ServiceManager.getService(IContactService.class);
//    ContactDTO contactDTO=contactService.getContactDTO(shopId,null,null,mobile);
//    if(contactDTO==null){
//      contactDTO=new ContactDTO();
//      contactDTO.setShopId(shopId);
//      contactDTO.setMobile(mobile);
//      contactDTO.setMainContact(1);
//      contactDTO.setDisabled(1);
//      contactService.saveContact(contactDTO);
//    }
//    smsContactDTOs.add(contactDTO);
//    Result result=new Result();
//    SmsDTO smsDTO=new SmsDTO();
//    smsDTO.setShopId(shopId);
//    smsDTO.setUserId(userId);
//    smsDTO.setContent(content);
//    smsDTO.setAppFlag(appFlag);
//    smsDTO.setSmsFlag(smsFlag);
//    smsDTO.setTemplateFlag(templateFlag);
//    smsDTO.setContactDTOs(smsContactDTOs);
//    smsDTO.setAppUserNo(appUserNo);
//    sendSms(result, smsDTO);
//    return result;
//  }
  

}
