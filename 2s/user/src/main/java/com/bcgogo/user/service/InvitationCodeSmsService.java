package com.bcgogo.user.service;

import com.bcgogo.common.Result;
import com.bcgogo.config.model.Shop;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.constant.LogicResource;
import com.bcgogo.enums.notification.InvitationCodeType;
import com.bcgogo.enums.notification.OperatorType;
import com.bcgogo.enums.shop.ShopKind;
import com.bcgogo.enums.sms.SenderType;
import com.bcgogo.enums.sms.SmsSendScene;
import com.bcgogo.enums.user.ResourceType;
import com.bcgogo.notification.dto.InvitationCodeDTO;
import com.bcgogo.notification.dto.InvitationCodeSendDTO;
import com.bcgogo.notification.dto.SmsDTO;
import com.bcgogo.notification.dto.SmsIndexDTO;
import com.bcgogo.notification.invitationCode.InvitationCodeGeneratorClient;
import com.bcgogo.notification.model.SmsSendingTimes;
import com.bcgogo.notification.service.INotificationService;
import com.bcgogo.notification.service.ISmsSendingTimesService;
import com.bcgogo.notification.service.ISmsService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.dto.ContactDTO;
import com.bcgogo.user.dto.CustomerDTO;
import com.bcgogo.user.dto.SupplierDTO;
import com.bcgogo.user.model.UserDaoManager;
import com.bcgogo.user.model.UserWriter;
import com.bcgogo.user.service.permission.IPrivilegeService;
import com.bcgogo.utils.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 13-1-21
 * Time: 上午10:28
 * 发送邀请码短信
 */
@Component
public class InvitationCodeSmsService implements IInvitationCodeSmsService {
  private static final Logger LOG = LoggerFactory.getLogger(InvitationCodeSmsService.class);
  private final static int PAGE_SIZE = 1000;

  @Autowired
  protected UserDaoManager userDaoManager;

  protected InvitationCodeGeneratorClient invitationCodeGeneratorClient;

  protected ISmsService smsService;

  protected ICustomerService customerService;

  protected ISupplierService supplierService;

  protected IUserService userService;

  public InvitationCodeGeneratorClient getInvitationCodeGeneratorClient() {
    if (invitationCodeGeneratorClient == null)
      invitationCodeGeneratorClient = ServiceManager.getService(InvitationCodeGeneratorClient.class);
    return invitationCodeGeneratorClient;
  }

  public ISmsService getSmsService() {
    if (smsService == null)
      smsService = ServiceManager.getService(ISmsService.class);
    return smsService;
  }

  public ICustomerService getCustomerService() {
    if (customerService == null) {
      customerService = ServiceManager.getService(ICustomerService.class);
    }
    return customerService;
  }

  public ISupplierService getSupplierService() {
    if (supplierService == null) {
      supplierService = ServiceManager.getService(ISupplierService.class);
    }
    return supplierService;
  }

  public IUserService getUserService() {
    if (userService == null) {
      userService = ServiceManager.getService(IUserService.class);
    }
    return userService;
  }

  private boolean isEliminateShop(InvitationCodeSendDTO sendDTO, Long shopId) {
    for (Long eliminateShopId : sendDTO.getEliminateShopIdList()) {
      if (NumberUtil.isEqual(shopId, eliminateShopId)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public void sendInvitationSmsForCustomersAndSuppliers(SmsDTO smsDTO,InvitationCodeSendDTO invitationCodeSendDTO) throws Exception {
    invitationCodeSendDTO.setSender(SenderType.bcgogo);
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    invitationCodeSendDTO.setNeedCode(false);
    List<Shop> shopList = configService.getSendInvitationCodeActiveShop();
    List<String> shopMobileList = configService.getSendInvitationCodeActiveShopMobile();
    IPrivilegeService privilegeService = ServiceManager.getService(IPrivilegeService.class);
    for (Shop shop : shopList) {
      LOG.info("shop[id:{}] sendInvitationCodeSms......", shop.getId());
      if (ShopKind.TEST == shop.getShopKind()) continue;
      if (isEliminateShop(invitationCodeSendDTO, shop.getId())) continue;
      invitationCodeSendDTO.setShopId(shop.getId());
      invitationCodeSendDTO.setInvitationCodeSendTimes(5);
      invitationCodeSendDTO.setEliminateMobileList(shopMobileList);
      invitationCodeSendDTO.setSmsSendScene(SmsSendScene.INVITE_SUPPLIER);
      sendInvitationCodeSmsForSupplier(smsDTO,invitationCodeSendDTO);
      if (privilegeService.verifierShopVersionResource(shop.getShopVersionId(), ResourceType.logic, LogicResource.WEB_VERSION_SEND_INVITATION_CODE_TO_CUSTOMER)) {
        invitationCodeSendDTO.setSmsSendScene(SmsSendScene.INVITE_CUSTOMER);
        sendInvitationCodeSmsForCustomer(smsDTO,invitationCodeSendDTO);
      }
    }
  }

  @Override
  public void sendInvitationCodeSmsForCustomers(SmsDTO smsDTO,InvitationCodeSendDTO invitationCodeSendDTO) throws Exception {
    if (invitationCodeSendDTO.getShopId() == null) {
      throw new Exception("shopId is null!");
    }
    if (invitationCodeSendDTO.getSender() == null) {
      throw new Exception("senderType is null!");
    }
    if (invitationCodeSendDTO.getInvitationCodeType() == null) {
      throw new Exception("invitationCodeType is null!");
    }
    sendInvitationCodeSmsForCustomer(smsDTO,invitationCodeSendDTO);
  }

  @Override
  public void sendInvitationCodeSmsForSuppliers(SmsDTO smsDTO,InvitationCodeSendDTO invitationCodeSendDTO) throws Exception {
    if (invitationCodeSendDTO.getShopId() == null) {
      throw new Exception("shopId is null!");
    }
    if (invitationCodeSendDTO.getSender() == null) {
      throw new Exception("senderType is null!");
    }
    if (invitationCodeSendDTO.getInvitationCodeType() == null) {
      throw new Exception("invitationCodeType is null!");
    }
    sendInvitationCodeSmsForSupplier(smsDTO,invitationCodeSendDTO);
  }

  private void sendInvitationCodeSmsForCustomer(SmsDTO smsDTO,InvitationCodeSendDTO invitationCodeSendDTO) throws Exception {
    List<Long> ids = new ArrayList<Long>();
    Set<String> mobiles = new HashSet<String>();
    List<InvitationCodeDTO> invitationCodeDTOList;
    List<InvitationCodeSendDTO> invitationCodeSendDTOList;
    InvitationCodeSendDTO sendDTO;
    Map<Long, InvitationCodeDTO> invitationCodeDTOMap = new HashMap<Long, InvitationCodeDTO>();
    InvitationCodeDTO invitationCodeDTO;
    long startId = 0;
    List<CustomerDTO> customerDTOList;
    CustomerDTO customerDTO;
    SmsSendingTimes smsSendingTimes;
    ISmsSendingTimesService smsSendingTimesService = ServiceManager.getService(ISmsSendingTimesService.class);
    boolean flag;
    while (true) {
      customerDTOList = getCustomerService().getCustomersByShopIdSendInvitationCode(invitationCodeSendDTO.getShopId(),
        startId, invitationCodeSendDTO.getPageSize(), invitationCodeSendDTO.getCreateTime());
      if (CollectionUtils.isEmpty(customerDTOList)) break;
      startId = customerDTOList.get(customerDTOList.size() - 1).getId();
      invitationCodeSendDTOList = new ArrayList<InvitationCodeSendDTO>();
      ids.clear();
      mobiles.clear();
      Iterator iterator = customerDTOList.iterator();
      while (iterator.hasNext()) {
        if (iterator == null) {
          continue;
        }
        customerDTO = (CustomerDTO) iterator.next();
        String mobile = customerDTO.getMainContactMobile();
        if (!RegexUtils.isMobile(mobile)) {
          iterator.remove();
          continue;
        }
        flag = false;
        //该手机号已经被注册过
        if (eliminateMobile(invitationCodeSendDTO.getEliminateMobileList(), customerDTO.getContactMobiles())) {
          iterator.remove();
          continue;
        }
        if (flag) {
          continue;
        }
        //客户名称过滤
        if (invitationCodeSmsFilterByCustomerName(customerDTO.getName())) {
          iterator.remove();
          continue;
        }
        mobiles.add(mobile.trim());
      }
      if (CollectionUtils.isEmpty(customerDTOList)) continue;
      Map<String, SmsSendingTimes> map = smsSendingTimesService.getSmsSendingTimesByMobiles(mobiles);
      iterator = customerDTOList.iterator();
      while (iterator.hasNext()) {
        customerDTO = (CustomerDTO) iterator.next();
        if (invitationCodeSendDTO.getInvitationCodeSendTimes() != -1) {
          smsSendingTimes = map.get(customerDTO.getMainContactMobile());
          if (smsSendingTimes != null && (smsSendingTimes.getInvitationCodeSendingTimes() - invitationCodeSendDTO.getInvitationCodeSendTimes()) >= 0) {
            iterator.remove();
            continue;
          }
        }
        ids.add(customerDTO.getId());
      }
      if (CollectionUtil.isEmpty(ids)) continue;

      if (invitationCodeSendDTO.isNeedCode()) {
        invitationCodeDTOList = getInvitationCodeGeneratorClient().createInvitationCodes(invitationCodeSendDTO.getInvitationCodeType(), OperatorType.SHOP,
          invitationCodeSendDTO.getShopId(), OperatorType.CUSTOMER, ids, null, invitationCodeSendDTO.isCheckingDuplicated());
        invitationCodeDTOMap = toMap(invitationCodeDTOList);
      }
      iterator = customerDTOList.iterator();
      while (iterator.hasNext()) {
        customerDTO = (CustomerDTO) iterator.next();
        invitationCodeDTO = invitationCodeDTOMap.get(customerDTO.getId());
        sendDTO = new InvitationCodeSendDTO();
        sendDTO.setSender(invitationCodeSendDTO.getSender());
        sendDTO.setSmsSendScene(invitationCodeSendDTO.getSmsSendScene());
        sendDTO.setShopId(invitationCodeSendDTO.getShopId());
        sendDTO.setMobile(customerDTO.getMobile());
        if(ArrayUtil.isNotEmpty(customerDTO.getContacts())){
          sendDTO.setContactDTOs(Arrays.asList(customerDTO.getContacts()));
        }
        sendDTO.setCustomerSmsInvitationCodeTemplate();
        sendDTO.setSendTime(invitationCodeSendDTO.getSendTime());
        customerDTO.setInvitationCodeSendDate(System.currentTimeMillis());
        customerDTO.setInvitationCodeSendTimes(customerDTO.getInvitationCodeSendTimes() == null ? 1 : customerDTO.getInvitationCodeSendTimes() + 1);
        if (invitationCodeSendDTO.isNeedCode()) {
          if (invitationCodeDTO == null || !RegexUtils.isMobile(customerDTO.getMobile())) {
            iterator.remove();
            continue;
          }
          sendDTO.setCode(invitationCodeDTO.getCode());
        }
        invitationCodeSendDTOList.add(sendDTO);
      }
      //生成sms
      if(smsDTO!=null){
        List<ContactDTO> contactDTOs=new ArrayList<ContactDTO>();
        for(InvitationCodeSendDTO codeSendDTO:invitationCodeSendDTOList){
          if(CollectionUtil.isEmpty(codeSendDTO.getContactDTOs())) continue;
          contactDTOs.addAll(codeSendDTO.getContactDTOs());
        }
        smsDTO.setContactDTOs(contactDTOs);
        ServiceManager.getService(IContactService.class).saveSmsInfo(smsDTO);
      }
      getSmsService().sendInvitationCodeByShop(smsDTO,SmsSendScene.INVITE_CUSTOMER,invitationCodeSendDTOList);
      getCustomerService().batchUpdateCustomer(customerDTOList);
    }
  }

  //过滤掉非汉字字符  过滤车牌号
  private boolean invitationCodeSmsFilterByCustomerName(String name) {
    return (StringUtils.isNotBlank(name) && (name.contains("**客户**")) || RegexUtils.isVehicleNo(name));
  }

  private boolean eliminateMobile(List<String> eliminateMobiles, List<String> mobiles) {
    for (String s : mobiles) {
      if (eliminateMobiles.contains(s)) {
        return true;
      }
    }
    return false;
  }

  private void sendInvitationCodeSmsForSupplier(SmsDTO smsDTO,InvitationCodeSendDTO invitationCodeSendDTO) throws Exception {
    List<Long> ids = new ArrayList<Long>();
    Set<String> mobiles = new HashSet<String>();
    List<InvitationCodeDTO> invitationCodeDTOList;
    List<InvitationCodeSendDTO> invitationCodeSendDTOList;
    InvitationCodeSendDTO sendDTO;
    Map<Long, InvitationCodeDTO> invitationCodeDTOMap = new HashMap<Long, InvitationCodeDTO>();
    InvitationCodeDTO invitationCodeDTO;
    long startId = 0;
    List<SupplierDTO> supplierDTOList;
    SupplierDTO supplierDTO;
    SmsSendingTimes smsSendingTimes;
    ISmsSendingTimesService smsSendingTimesService = ServiceManager.getService(ISmsSendingTimesService.class);
    if (invitationCodeSendDTO.getPageSize() == null) {
      invitationCodeSendDTO.setPageSize(PAGE_SIZE);
    }
    while (true) {
      supplierDTOList = getSupplierService().getSuppliersByShopIdSendInvitationCode(invitationCodeSendDTO.getShopId(),
        startId, invitationCodeSendDTO.getPageSize(), invitationCodeSendDTO.getCreateTime());
      if (CollectionUtils.isEmpty(supplierDTOList)) break;
      Iterator iterator = supplierDTOList.iterator();
      startId = supplierDTOList.get(supplierDTOList.size() - 1).getId();
      invitationCodeSendDTOList = new ArrayList<InvitationCodeSendDTO>();
      boolean flag;
      while (iterator.hasNext()) {
        supplierDTO = (SupplierDTO) iterator.next();
        String mobile = supplierDTO.getMainContactMobile();
        if (!RegexUtils.isMobile(supplierDTO.getMobile())) {
          iterator.remove();
          continue;
        }
        flag = false;
        //该手机号已经被注册过
        if (eliminateMobile(invitationCodeSendDTO.getEliminateMobileList(), supplierDTO.getContactMobiles())) {
          iterator.remove();
          continue;
        }
        if (flag)
          continue;
        mobiles.add(mobile.trim());
      }
      if (CollectionUtils.isEmpty(supplierDTOList)) continue;
      Map<String, SmsSendingTimes> map = smsSendingTimesService.getSmsSendingTimesByMobiles(mobiles);
      iterator = supplierDTOList.iterator();
      while (iterator.hasNext()) {
        supplierDTO = (SupplierDTO) iterator.next();
        if (invitationCodeSendDTO.getInvitationCodeSendTimes() != -1) {
          smsSendingTimes = map.get(supplierDTO.getMainContactMobile());
          if (smsSendingTimes != null && (smsSendingTimes.getInvitationCodeSendingTimes() - invitationCodeSendDTO.getInvitationCodeSendTimes() >= 0)) {
            iterator.remove();
            continue;
          }
        }
        ids.add(supplierDTO.getId());
      }
      if (CollectionUtils.isEmpty(ids)) continue;

      if (invitationCodeSendDTO.isNeedCode()) {
        invitationCodeDTOList = getInvitationCodeGeneratorClient().createInvitationCodes(invitationCodeSendDTO.getInvitationCodeType(), OperatorType.SHOP,
          invitationCodeSendDTO.getShopId(), OperatorType.SUPPLIER, ids, null, invitationCodeSendDTO.isCheckingDuplicated());
        invitationCodeDTOMap = toMap(invitationCodeDTOList);
      }
      iterator = supplierDTOList.iterator();
      while (iterator.hasNext()) {
        supplierDTO = (SupplierDTO) iterator.next();
        sendDTO = new InvitationCodeSendDTO();
        sendDTO.setSender(invitationCodeSendDTO.getSender());
        sendDTO.setSmsSendScene(invitationCodeSendDTO.getSmsSendScene());
        sendDTO.setShopId(invitationCodeSendDTO.getShopId());
        sendDTO.setSupplierSmsInvitationCodeTemplate();
        sendDTO.setSendTime(invitationCodeSendDTO.getSendTime());
        sendDTO.setMobile(supplierDTO.getMobile());
        if(ArrayUtil.isNotEmpty(supplierDTO.getContacts())){
          sendDTO.setContactDTOs(Arrays.asList(supplierDTO.getContacts()));
        }
        supplierDTO.setInvitationCodeSendDate(System.currentTimeMillis());
        supplierDTO.setInvitationCodeSendTimes(supplierDTO.getInvitationCodeSendTimes() == null ? 1 : supplierDTO.getInvitationCodeSendTimes() + 1);
        if (invitationCodeSendDTO.isNeedCode()) {
          invitationCodeDTO = invitationCodeDTOMap.get(supplierDTO.getId());
          if (invitationCodeDTO == null) {
            iterator.remove();
            continue;
          }
          sendDTO.setCode(invitationCodeDTO.getCode());
        }
        invitationCodeSendDTOList.add(sendDTO);
      }
      if(smsDTO!=null){
        //生成sms
        List<ContactDTO> contactDTOs=new ArrayList<ContactDTO>();
        for(InvitationCodeSendDTO codeSendDTO:invitationCodeSendDTOList){
          if(CollectionUtil.isEmpty(codeSendDTO.getContactDTOs())) continue;
          contactDTOs.addAll(codeSendDTO.getContactDTOs());
        }
        smsDTO.setContactDTOs(contactDTOs);
        ServiceManager.getService(IContactService.class).saveSmsInfo(smsDTO);
      }
      getSmsService().sendInvitationCodeByShop(smsDTO,SmsSendScene.INVITE_SUPPLIER,invitationCodeSendDTOList);
      getSupplierService().batchUpdateSupplier(supplierDTOList);
    }
  }



  private Map<Long, InvitationCodeDTO> toMap(List<InvitationCodeDTO> invitationCodeDTOList) {
    Map<Long, InvitationCodeDTO> map = new HashMap<Long, InvitationCodeDTO>();
    if (CollectionUtil.isEmpty(invitationCodeDTOList)) return map;
    for (InvitationCodeDTO dto : invitationCodeDTOList) {
      map.put(dto.getInviteeId(), dto);
    }
    return map;
  }


  @Override
  public Result sendCustomerInvitationCodeSms(Long shopId, Long customerId, SenderType senderType, SmsSendScene smsSendScene) throws Exception {
    CustomerDTO customerDTO = getCustomerService().getCustomerById(customerId);
    if (customerDTO == null) {
      LOG.warn("customer get by id[{}] is null!", customerId);
      return new Result("发送失败", "客户Id为空！", false);
    }
    String mainContactMobile = customerDTO.getMainContactMobile();
    List<String> mobiles = customerDTO.getContactMobiles();
    if (!RegexUtils.isMobile(mainContactMobile)) {
      LOG.warn("mobile:[{}] is illegal!", customerDTO.getContacts());
      return new Result("发送失败", "手机号码不合法！", false);
    }
    List<String> shopMobileList = ServiceManager.getService(IConfigService.class).getSendInvitationCodeActiveShopMobile();
    for (String m : mobiles) {
      if (shopMobileList.contains(m)) {
        LOG.warn("mobile:[{}] had registered!", m);
        return new Result("发送失败", "该手机号码已被注册使用一发软件！", false);
      }
    }
    InvitationCodeSendDTO sendDTO = new InvitationCodeSendDTO();
    sendDTO.setCode(getInvitationCodeGeneratorClient().createInvitationCode(InvitationCodeType.SHOP, OperatorType.SHOP, shopId, OperatorType.CUSTOMER, customerId, null));
    sendDTO.setShopId(shopId);
    sendDTO.setMobile(customerDTO.getMobile());
    sendDTO.setSender(senderType);
    sendDTO.setCustomerSmsInvitationCodeTemplate();
    sendDTO.setSmsSendScene(smsSendScene);
    if (getSmsService().sendInvitationCode(sendDTO)) {
      //修改状态
      customerDTO.setInvitationCodeSendDate(System.currentTimeMillis());
      customerDTO.setInvitationCodeSendTimes(customerDTO.getInvitationCodeSendTimes() == null ? 1 : customerDTO.getInvitationCodeSendTimes() + 1);
      getUserService().updateCustomer(customerDTO);
      return new Result(true);
    }
    return new Result("发送失败", "短信发送失败！", false);
  }

  @Override
  public boolean reSendInvitationCodeSms(InvitationCodeDTO dto) throws Exception {
    InvitationCodeSendDTO sendDTO = new InvitationCodeSendDTO();
    sendDTO.setCode(getInvitationCodeGeneratorClient().createInvitationCode(InvitationCodeType.SYSTEM, OperatorType.SHOP, dto.getInviterId(), dto.getInviteeType(), dto.getInviteeId(), null));
    sendDTO.setShopId(dto.getInviterId());
    sendDTO.setMobile(dto.getMobile());
    if (dto.getInviteeType() == OperatorType.CUSTOMER) {
      sendDTO.setCustomerSmsInvitationCodeTemplate();
    } else {
      sendDTO.setSupplierSmsInvitationCodeTemplate();
    }
    sendDTO.setSender(SenderType.bcgogo);
    getSmsService().sendInvitationCode(sendDTO);
    return true;
  }

  @Override
  public Result sendSupplierInvitationCodeSms(Long shopId, Long supplierId, SenderType senderType, SmsSendScene smsSendScene) throws Exception {
    SupplierDTO supplierDTO = getSupplierService().getSupplierById(supplierId, shopId);
    if (supplierDTO == null) {
      LOG.warn("supplier get by id[{}] is null!", supplierId);
      return new Result("发送失败", "供应商Id为空！", false);
    }
    String mainContactMobile = supplierDTO.getMainContactMobile();
    List<String> mobiles = supplierDTO.getContactMobiles();
    if (!RegexUtils.isMobile(mainContactMobile)) {
      LOG.warn("mobile:[{}] is illegal!", supplierDTO.getContacts());
      return new Result("发送失败", "手机号码不合法！", false);
    }
    List<String> shopMobileList = ServiceManager.getService(IConfigService.class).getSendInvitationCodeActiveShopMobile();
    for (String m : mobiles) {
      if (shopMobileList.contains(m)) {
        LOG.warn("mobile:[{}] had registered!", m);
        return new Result("发送失败", "该手机号码已被注册使用一发软件！", false);
      }
    }
    InvitationCodeSendDTO sendDTO = new InvitationCodeSendDTO();
    sendDTO.setCode(getInvitationCodeGeneratorClient().createInvitationCode(InvitationCodeType.SHOP, OperatorType.SHOP, shopId, OperatorType.SUPPLIER, supplierId, null));
    sendDTO.setShopId(shopId);
    sendDTO.setMobile(supplierDTO.getMobile());
    sendDTO.setSender(SenderType.Shop);
    sendDTO.setSupplierSmsInvitationCodeTemplate();
    if (getSmsService().sendInvitationCode(sendDTO)) {
      //修改状态
      supplierDTO.setInvitationCodeSendDate(System.currentTimeMillis());
      supplierDTO.setInvitationCodeSendTimes(supplierDTO.getInvitationCodeSendTimes() == null ? 1 : supplierDTO.getInvitationCodeSendTimes() + 1);
      getUserService().updateSupplier(supplierDTO);
      return new Result(true);
    }
    return new Result("发送失败", "短信发送失败！", false);
  }

  @Override
  public Long checkCustomerOrSupplierWithoutSendInvitationCodeSms(String customerOrSupplier, Long shopId) {
    UserWriter writer = userDaoManager.getWriter();
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    List<String> mobiles = configService.getSendInvitationCodeActiveShopMobile();
    if ("CUSTOMER".equals(customerOrSupplier)) {
      return writer.checkCustomerWithoutSendInvitationCodeSms(shopId, mobiles);
    } else {
      return writer.checkSupplierWithoutSendInvitationCodeSms(shopId, mobiles);
    }
  }
}
