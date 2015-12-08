package com.bcgogo.admin.config.shop;

import com.bcgogo.enums.txn.finance.ChargeType;
import com.bcgogo.txn.service.solr.ICustomerOrSupplierSolrWriteService;
import com.bcgogo.util.WebUtil;
import com.bcgogo.config.dto.*;
import com.bcgogo.config.model.ShopAuditLog;
import com.bcgogo.config.model.SmsDonationLog;
import com.bcgogo.config.service.*;
import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.enums.*;
import com.bcgogo.enums.shop.AuditStatus;
import com.bcgogo.enums.shop.RegisterType;
import com.bcgogo.enums.shop.ShopStatus;
import com.bcgogo.enums.txn.finance.SmsCategory;
import com.bcgogo.enums.txn.pushMessage.PushMessageSourceType;
import com.bcgogo.enums.user.Status;
import com.bcgogo.enums.user.UserGroupType;
import com.bcgogo.enums.user.UserType;
import com.bcgogo.notification.invitationCode.InvitationCodeGeneratorClient;
import com.bcgogo.notification.service.ISmsService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.stat.dto.SupplierRecordDTO;
import com.bcgogo.txn.dto.finance.ShopSmsRecordDTO;
import com.bcgogo.txn.service.IStoreHouseService;
import com.bcgogo.txn.service.ISupplierRecordService;
import com.bcgogo.txn.service.RFITxnService;
import com.bcgogo.txn.service.finance.IBcgogoReceivableService;
import com.bcgogo.txn.service.finance.ISmsAccountService;
import com.bcgogo.txn.service.pushMessage.IApplyPushMessageService;
import com.bcgogo.txn.service.solr.IShopSolrWriterService;
import com.bcgogo.user.dto.*;
import com.bcgogo.user.dto.permission.UserGroupDTO;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.user.service.permission.IUserCacheService;
import com.bcgogo.user.service.permission.IUserGroupService;
import com.bcgogo.user.service.utils.BcgogoShopLogicResourceUtils;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.EncryptionUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: hans
 * Date: 13-4-7
 * Time: 下午2:23
 * 店面注册
 */
@Component
public class ShopActivateHandler {
  private static final Logger LOG = LoggerFactory.getLogger(ShopManageController.class);

  //数据验证
  public boolean validate(Map<String, Object> result, ShopDTO shopDTO, AuditStatus auditStatus) {
    if (auditStatus == null) {
      LOG.warn("auditStatus is null.");
      result.put("success", false);
      result.put("message", "审核结果为空!");
      return false;
    }
    if (shopDTO == null) {
      LOG.warn("店铺未找到");
      result.put("success", false);
      result.put("message", "店铺未找到!");
      return false;
    }
    if (LOG.isInfoEnabled()) LOG.info("开始执行店铺激活操作.shopDTO:{}", shopDTO.toString());
    if (ShopStatus.isAuditedShopStatus(shopDTO.getShopStatus())) {
      LOG.warn("店铺已经激活");
      result.put("success", false);
      result.put("message", "该店铺已经激活!");
      return false;
    }
    return true;
  }

  //审核日志
  public void createShopAuditLog(long shopId, long userId, String reason, AuditStatus auditStatus) {
    IShopAuditLogService shopAuditLogService = ServiceManager.getService(IShopAuditLogService.class);
    ShopAuditLog log = new ShopAuditLog();
    log.setShopId(shopId);
    log.setAuditorId(userId);
    log.setAuditStatus(auditStatus);
    log.setAuditTime(System.currentTimeMillis());
    log.setReason(reason);
    shopAuditLogService.createShopAuditLog(log);
  }

  //审核
  public Object activate(long shopId, HttpServletRequest request , String reason, AuditStatus auditStatus) {
    long userId = WebUtil.getUserId(request);
    long a = System.currentTimeMillis();
    Map<String, Object> result = new HashMap<String, Object>();
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    ShopDTO shopDTO = configService.getShopById(shopId);
    IShopService shopService = ServiceManager.getService(IShopService.class);
    IUserService userService = ServiceManager.getService(IUserService.class);
    RFITxnService txnService = ServiceManager.getService(RFITxnService.class);
    IApplyService applyService = ServiceManager.getService(IApplyService.class);
    IApplyPushMessageService applyPushMessageService = ServiceManager.getService(IApplyPushMessageService.class);
    if (!this.validate(result, shopDTO, auditStatus)) {
      return result;
    }
    try {
      createShopAuditLog(shopId, userId, reason, auditStatus);
      if (!AuditStatus.isAuditPass(auditStatus)) {
        shopService.updateShopStatus(shopId, ShopStatus.CHECK_PENDING_REJECTED);
        result.put("success", true);
        result.put("message", "审核拒绝操作成功!");
        return result;
      }
      //激活
      shopDTO = shopService.activateShop(shopDTO.getId(), Long.parseLong(request.getParameter("shopVersionId")),ChargeType.valueOf(request.getParameter("chargeType")));
      //创建用户 权限
      createUser(shopDTO);
      //创建一条软件待支付记录
     
      ServiceManager.getService(IBcgogoReceivableService.class).createSoftwareReceivable(shopDTO.getId(),userId,WebUtil.getUserName(request),shopDTO.getBuyChannels());
      //短信赠送处理逻辑
      smsHandsel(shopDTO, userId);
      // add By WLF 激活成功之后，在批发商客店铺中新增一个客户 todo 待整理
      // 先从临时关系表中找到新店与老客户的关系，如果没有记录就新增客户，有就不再新增
      if (RegisterType.SELF_REGISTER.equals(shopDTO.getRegisterType())) {
        //个人直接注册，无邀请码
      } else if (RegisterType.SUPPLIER_REGISTER.equals(shopDTO.getRegisterType())) {
        if (shopDTO.getWholesalerShopId() != null) {
          Long customerId = configService.getTempCustomerIdByShopId(shopDTO.getId());
          if (customerId == null) {
            //新建客户记录
            CustomerRecordDTO customerRecordDTO = new CustomerRecordDTO();
            customerRecordDTO.setShopId(shopDTO.getWholesalerShopId());
            customerRecordDTO.setName(shopDTO.getName());
            customerRecordDTO.setContact(shopDTO.getStoreManager());
            customerRecordDTO.setPhone(shopDTO.getLandline());
            customerRecordDTO.setMobile(shopDTO.getStoreManagerMobile());
            customerRecordDTO.setAddress(shopDTO.getAddress());
            customerRecordDTO.setEmail(shopDTO.getEmail());
            customerRecordDTO.setQq(shopDTO.getQq());
            customerRecordDTO.setShortName(shopDTO.getShortname());
            customerRecordDTO.setMemo(shopDTO.getMemo());
            //新建客户
            CustomerDTO newCustomerDTO = new CustomerDTO();
            newCustomerDTO.fromCustomerRecordDTO(customerRecordDTO, false, false);
            newCustomerDTO.setCustomerShopId(shopDTO.getId());
            newCustomerDTO.setRelationType(RelationTypes.REGISTER_RELATED);
            newCustomerDTO = userService.createCustomer(newCustomerDTO);
            customerId = newCustomerDTO.getId();
            customerRecordDTO.setCustomerId(customerId);
            customerRecordDTO.setCustomerShopId(shopDTO.getId());
            userService.createCustomerRecord(customerRecordDTO);
            //新建的客户建立索引
            ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexCustomerByCustomerId(customerId);
          } else {
            // customer表和customer_record表中添加customer_shop_id
            CustomerDTO customerDTO = userService.getCustomerById(customerId);
            customerDTO.setCustomerShopId(shopDTO.getId());
            customerDTO.setRelationType(RelationTypes.REGISTER_RELATED);
            userService.updateCustomer(customerDTO);
            List<CustomerRecordDTO> customerRecordDTOList = userService.getCustomerRecordByCustomerId(customerId);
            if (CollectionUtils.isNotEmpty(customerRecordDTOList)) {
              for (CustomerRecordDTO recordDTO : customerRecordDTOList) {
                recordDTO.setCustomerShopId(shopDTO.getId());
                userService.updateCustomerRecord(recordDTO);
              }
            }
            // 删除客户与新店的临时关联
            configService.deleteShopCustomerRelation(customerId);
            //新建的客户建立索引
            ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexCustomerByCustomerId(customerId);
          }
        }
        // 默认把批发商作为新开店铺的第一个供应商，用批发商管理员信息，非店主信息
        if (shopDTO.getWholesalerShopId() != null) {
          SupplierDTO newSupplierDTO = new SupplierDTO();
          newSupplierDTO.setShopId(shopDTO.getId());
          newSupplierDTO.setSupplierShopId(shopDTO.getWholesalerShopId());
          newSupplierDTO.setRelationType(RelationTypes.BE_REGISTERED);
          ShopDTO wholesalerShopDTO = configService.getShopById(shopDTO.getWholesalerShopId());
          newSupplierDTO.fromSupplierShopDTO(wholesalerShopDTO);
          newSupplierDTO = userService.createSupplier(newSupplierDTO);
          //生成SupplierRecord
          ISupplierRecordService supplierService = ServiceManager.getService(ISupplierRecordService.class);
          SupplierRecordDTO supplierRecordDTO = supplierService.createSupplierRecordUsingSupplierDTO(newSupplierDTO);
          supplierService.saveOrUpdateSupplierRecord(supplierRecordDTO);

          //新建的供应商建立索引
          ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexSupplierBySupplierId(newSupplierDTO.getId());

          // 建立批发商与自建店铺之间的关联
          WholesalerShopRelationDTO wholesalerShopRelationDTO = new WholesalerShopRelationDTO();
          wholesalerShopRelationDTO.setShopId(shopDTO.getId());
          wholesalerShopRelationDTO.setWholesalerShopId(shopDTO.getWholesalerShopId());
          wholesalerShopRelationDTO.setStatus(ShopRelationStatus.ENABLED);
          configService.createWholesalerShopRelation(wholesalerShopRelationDTO);
        }

      } else {
        RegisterInfoDTO registerInfoDTO = configService.getRegisterInfoDTOByRegisterShopId(shopDTO.getId());
        ShopDTO inviterShopDTO = null;
        if (registerInfoDTO != null) {
          inviterShopDTO = configService.getShopById(registerInfoDTO.getInviterShopId());
        }
        //客户邀请供应商   等供应商注册的时候在本店已经有关联了，则以新注册的店向本店发送一个关联请求
        if (RegisterType.CUSTOMER_INVITE.equals(shopDTO.getRegisterType())) {
          if (inviterShopDTO != null) {
            SupplierDTO supplierDTO = CollectionUtil.getFirst(userService.getSupplierById(inviterShopDTO.getId(), registerInfoDTO.getInviteeId()));
            if (supplierDTO != null && !CustomerStatus.DISABLED.equals(supplierDTO.getStatus()) && supplierDTO.getSupplierShopId() != null) {
              ShopRelationInviteDTO shopRelationInviteDTO = applyService.saveApplySupplierRelation(inviterShopDTO.getId(), null, shopDTO.getId());
              applyPushMessageService.createApplyRelatedPushMessage(inviterShopDTO.getId(), shopRelationInviteDTO.getInvitedShopId(), shopRelationInviteDTO.getId(),shopRelationInviteDTO.getInviteTime(), PushMessageSourceType.APPLY_CUSTOMER);
            } else {
              WholesalerShopRelationDTO wholesalerShopRelationDTO = new WholesalerShopRelationDTO();
              wholesalerShopRelationDTO.setShopId(inviterShopDTO.getId());
              wholesalerShopRelationDTO.setWholesalerShopId(shopDTO.getId());
              wholesalerShopRelationDTO.setStatus(ShopRelationStatus.ENABLED);
              configService.createWholesalerShopRelation(wholesalerShopRelationDTO);
              CustomerDTO customerDTO = userService.createRelationCustomer(shopDTO, inviterShopDTO, RelationTypes.BE_INVITED_RELATED);
              ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexCustomerByCustomerId(customerDTO.getId());
              if (supplierDTO != null && !CustomerStatus.DISABLED.equals(supplierDTO.getStatus())) {
                supplierDTO.setSupplierShopId(shopDTO.getId());
                supplierDTO.setRelationType(RelationTypes.INVITE_RELATED);
                userService.updateSupplier(supplierDTO);
                ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexSupplierBySupplierId(supplierDTO.getId());
              } else {
                txnService.createRelationSupplier(inviterShopDTO, shopDTO, RelationTypes.INVITE_RELATED);
              }
            }
          }
          //供应商邀请客户
        } else if (RegisterType.SUPPLIER_INVITE.equals(shopDTO.getRegisterType())) {
          if (inviterShopDTO != null) {
            CustomerDTO customerDTO = userService.getCustomerDTOByCustomerId(registerInfoDTO.getInviteeId(), inviterShopDTO.getId());
            if (customerDTO != null && !CustomerStatus.DISABLED.equals(customerDTO.getStatus()) && customerDTO.getCustomerShopId() != null) {
              ShopRelationInviteDTO shopRelationInviteDTO = applyService.saveApplyCustomerRelation(inviterShopDTO.getId(), null, shopDTO.getId());
              applyPushMessageService.createApplyRelatedPushMessage(inviterShopDTO.getId(), shopRelationInviteDTO.getInvitedShopId(), shopRelationInviteDTO.getId(),shopRelationInviteDTO.getInviteTime(), PushMessageSourceType.APPLY_SUPPLIER);
            } else {
              WholesalerShopRelationDTO wholesalerShopRelationDTO = new WholesalerShopRelationDTO();
              wholesalerShopRelationDTO.setShopId(shopDTO.getId());
              wholesalerShopRelationDTO.setWholesalerShopId(inviterShopDTO.getId());
              wholesalerShopRelationDTO.setStatus(ShopRelationStatus.ENABLED);
              configService.createWholesalerShopRelation(wholesalerShopRelationDTO);
              txnService.createRelationSupplier(shopDTO, inviterShopDTO, RelationTypes.BE_INVITED_RELATED);
              if (customerDTO != null && !CustomerStatus.DISABLED.equals(customerDTO.getStatus())) {
                customerDTO.setCustomerShopId(shopDTO.getId());
                customerDTO.setRelationType(RelationTypes.INVITE_RELATED);
                userService.updateCustomer(customerDTO);
                ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexCustomerByCustomerId(customerDTO.getId());
              } else {
                customerDTO = userService.createRelationCustomer(inviterShopDTO, shopDTO, RelationTypes.INVITE_RELATED);
                ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexCustomerByCustomerId(customerDTO.getId());
              }
            }
          }
        } else if (RegisterType.SYSTEM_INVITE_CUSTOMER.equals(shopDTO.getRegisterType())) {
          if (inviterShopDTO != null) {
            ShopRelationInviteDTO shopRelationInviteDTO= applyService.saveApplyCustomerRelation(inviterShopDTO.getId(), null, shopDTO.getId());
            applyPushMessageService.createApplyRelatedPushMessage(inviterShopDTO.getId(), shopRelationInviteDTO.getInvitedShopId(), shopRelationInviteDTO.getId(),shopRelationInviteDTO.getInviteTime(), PushMessageSourceType.APPLY_SUPPLIER);
          }
        } else if (RegisterType.SYSTEM_INVITE_SUPPLIER.equals(shopDTO.getRegisterType())) {
          if (inviterShopDTO != null) {
            ShopRelationInviteDTO shopRelationInviteDTO= applyService.saveApplySupplierRelation(inviterShopDTO.getId(), null, shopDTO.getId());
            applyPushMessageService.createApplyRelatedPushMessage(inviterShopDTO.getId(), shopRelationInviteDTO.getInvitedShopId(), shopRelationInviteDTO.getId(),shopRelationInviteDTO.getInviteTime(), PushMessageSourceType.APPLY_CUSTOMER);
          }
        }
      }
      //初始化刷卡机扫描枪开关
      UserSwitchDTO userSwitchDTO;
      if (!ConfigUtils.isFourSShopVersion(shopDTO.getShopVersionId())) {
        if (ConfigUtils.isWholesalerVersion(shopDTO.getShopVersionId())) {
          userSwitchDTO = userService.getUserSwitchByShopIdAndScene(shopId, "SCANNING_BARCODE");
          if (userSwitchDTO == null) {
            userService.saveUserSwitch(shopId, "SCANNING_BARCODE", MessageSwitchStatus.ON.toString());
          }
        } else {
          userSwitchDTO = userService.getUserSwitchByShopIdAndScene(shopId, "SCANNING_BARCODE");
          if (userSwitchDTO == null) {
            userService.saveUserSwitch(shopId, "SCANNING_BARCODE", MessageSwitchStatus.OFF.toString());
          }
          userSwitchDTO = userService.getUserSwitchByShopIdAndScene(shopId, "SCANNING_CARD");
          if (userSwitchDTO == null) {
            userService.saveUserSwitch(shopId, "SCANNING_CARD", MessageSwitchStatus.ON.toString());
          }

        }
      }
      //初始化店面的二维码图片
      ServiceManager.getService(IShopService.class).saveOrUpdateShopRQImage(shopDTO);
      LOG.debug("5===========" + (System.currentTimeMillis() - a));
//      txnService.updateServiceSingle(shopDTO.getId(), null, "洗车", "洗车", 0D, 0D); //todo 慢 test by zhangjuntao
      ServiceManager.getService(IShopSolrWriterService.class).reCreateShopIdSolrIndex(shopDTO.getId());
      initDefaultStoreHouse(shopDTO);
      LOG.debug("6===========" + (System.currentTimeMillis() - a));
    } catch (Exception e) {
      LOG.debug("/beshop.do");
      LOG.debug("method=activateshop");
      LOG.error(e.getMessage(), e);
      result.put("success", false);
      result.put("message", "店铺激活异常！");
      return result;
    }
    result.put("success", true);
    result.put("message", "审核通过，请耐心等待短信！");
    return result;
  }

  //todo 慢
  private void createUser(ShopDTO shopDTO) throws Exception {
    IUserCacheService userCacheService = ServiceManager.getService(IUserCacheService.class);
    IUserGroupService userGroupService = ServiceManager.getService(IUserGroupService.class);
    IUserService userService = ServiceManager.getService(IUserService.class);
    ISmsService smsService = ServiceManager.getService(ISmsService.class);
    UserDTO userDTO = new UserDTO();
    userDTO = userDTO.fromShopDTO(shopDTO);
    String pwd = userService.generatePassword();
    userDTO.setPassword(EncryptionUtil.encryptPassword(pwd, shopDTO.getId()));
    //创建用户登录NO
    List<UserDTO> userDTOList = userService.getUserByMobile(shopDTO.getStoreManagerMobile());
    if (userDTOList != null && userDTOList.size() != 0) {
      //TODO 此处处理单个手机号注册多家店铺的方法存在问题，新生产的用户名还需判断重复，不然重新生成，直到确保唯一
      int i = 0;
      String userNo = shopDTO.getStoreManagerMobile() + (userDTOList.size() + i);
      boolean isExist = userService.getUserByUserInfo(userNo) != null;
      //如果新生成的用户名
      while (isExist) {
        i++;
        userNo = shopDTO.getStoreManagerMobile() + (userDTOList.size() + i);
        isExist = userService.getUserByUserInfo(userNo) != null;
      }
      userDTO.setUserNo(userNo);
    } else {
      userDTO.setUserNo(shopDTO.getStoreManagerMobile());
    }
    UserGroupDTO userGroupDTO = userGroupService.getUniqueUserGroupByName(UserGroupType.SHOP_GROUPNAME_ADMIN.getValue(), null, shopDTO.getShopVersionId());
    if (userGroupDTO == null || userGroupDTO.getId() == null) {
      throw new Exception("user group is null, register fail!");
    }
    userDTO.setUserGroupId(userGroupDTO.getId());
    //短信通知
    if (RegisterType.isRegisterNotByBcgogo(shopDTO.getRegisterType())) {
      smsService.trialRegisterSmsSendToCustomer(shopDTO, userDTO, pwd);
    } else {
      smsService.registerMsgSendToCustomer(shopDTO, userDTO, pwd);
    }
    userDTO.setUserType(UserType.SYSTEM_CREATE);
    userDTO.setStatusEnum(Status.active);
    userCacheService.createUserAndStaff(userDTO);
  }

  private void smsHandsel(ShopDTO shopDTO, long userId) throws Exception {
    final double smsDonationValue = 10d;
    final Long smsDonationNumber = 100L;

    String invitationCode = ServiceManager.getService(IConfigService.class).getRegisterInfoByRegisterShopId(shopDTO.getId());
    if (StringUtils.isNotBlank(invitationCode)) {
      //改变邀请码状态
      ServiceManager.getService(InvitationCodeGeneratorClient.class).updateInvitationCodeToUsed(invitationCode);
    }
    if (shopDTO.getRegisterType() != RegisterType.SELF_REGISTER) {
      // 充值100
      ShopBalanceDTO dto = new ShopBalanceDTO();
      dto.setSmsBalance(smsDonationValue);
      dto.setRechargeTotal(smsDonationValue);
      dto.setShopId(shopDTO.getId());
      ServiceManager.getService(IShopBalanceService.class).createSmsBalance(dto);

      //客户注册赠送 统计
      ShopSmsRecordDTO shopSmsRecordDTO = new ShopSmsRecordDTO();
      shopSmsRecordDTO.setShopId(shopDTO.getId());
      shopSmsRecordDTO.setBalance(smsDonationValue);
      shopSmsRecordDTO.setNumber(smsDonationNumber);
      shopSmsRecordDTO.setSmsCategory(SmsCategory.REGISTER_HANDSEL);
      shopSmsRecordDTO.setOperatorId(userId);
      ServiceManager.getService(ISmsAccountService.class).createShopSmsHandsel(shopSmsRecordDTO);
      //短信赠送记录
      SmsDonationLog smsDonationLog = new SmsDonationLog();
      smsDonationLog.setShopId(shopDTO.getId());
      smsDonationLog.setDonationType(DonationType.REGISTER_ACTIVATE);
      smsDonationLog.setRegisterType(shopDTO.getRegisterType());
      smsDonationLog.setValue(smsDonationValue);
      smsDonationLog.setDonationTime(System.currentTimeMillis());
      ServiceManager.getService(ISmsDonationLogService.class).createSmsDonationLog(smsDonationLog);
    }
  }

  //初始化仓库
  private void initDefaultStoreHouse(ShopDTO shopDTO) throws Exception {
    if (BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(shopDTO.getShopVersionId())) {
      IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
      LOG.info("Shop[{}，{}]的ShopVersion[{}] 初始化仓库！", new Object[]{shopDTO.getId(), shopDTO.getName(), shopDTO.getShopVersionId()});
      storeHouseService.initDefaultStoreHouse(shopDTO.getId());
    }
  }
}
