package com.bcgogo.txn.service.pushMessage;

import com.bcgogo.config.dto.CustomerRelatedShopDTO;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.dto.SupplierRelatedShopDTO;
import com.bcgogo.config.service.IApplyService;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.service.IShopService;
import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.constant.pushMessage.AppointConstant;
import com.bcgogo.constant.pushMessage.ClientConstant;
import com.bcgogo.constant.pushMessage.PushMessageContentTemplate;
import com.bcgogo.constant.pushMessage.PushMessageParamsKeyConstant;
import com.bcgogo.enums.notification.OperatorType;
import com.bcgogo.enums.txn.pushMessage.*;
import com.bcgogo.notification.velocity.AppointVelocityContext;
import com.bcgogo.notification.velocity.PushMessageVelocityContext;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.pushMessage.PushMessageDTO;
import com.bcgogo.txn.dto.pushMessage.PushMessageReceiverDTO;
import com.bcgogo.txn.dto.pushMessage.PushMessageSourceDTO;
import com.bcgogo.txn.model.TxnDaoManager;
import com.bcgogo.txn.model.TxnWriter;
import com.bcgogo.txn.model.pushMessage.PushMessage;
import com.bcgogo.txn.service.messageCenter.AbstractMessageService;
import com.bcgogo.user.service.ICustomerService;
import com.bcgogo.user.service.ISupplierService;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.JsonUtil;
import com.bcgogo.utils.ShopConstant;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.VelocityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * User: ZhangJuntao
 * Date: 13-6-20
 * Time: 上午10:39
 */
@Component
public class ApplyPushMessageService extends AbstractMessageService implements IApplyPushMessageService {
  private static final Logger LOG = LoggerFactory.getLogger(ApplyPushMessageService.class);
  @Autowired
  private TxnDaoManager txnDaoManager;

  @Override
  public void createApplyRelatedPushMessage(Long originShopId, Long invitedShopId, Long sourceId,Long invitedTime, PushMessageSourceType sourceType)throws Exception {
    IPushMessageService pushMessageService = ServiceManager.getService(IPushMessageService.class);
    IShopService shopService = ServiceManager.getService(IShopService.class);
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    Map<Long, ShopDTO> shopDTOMap = shopService.getShopByShopIds(originShopId, invitedShopId);
    ShopDTO originShop = shopDTOMap.get(originShopId);
    ShopDTO invitedShop = shopDTOMap.get(invitedShopId);
    if (originShop == null || invitedShop == null) {
      LOG.error("get shop by ids[{},{}] is null.", originShop, invitedShop);
      return;
    }
    originShop.setAreaName(configService.getShopAreaInfoByShopDTO(originShop));
    invitedShop.setAreaName(configService.getShopAreaInfoByShopDTO(invitedShop));

    PushMessageDTO pushMessageDTO = new PushMessageDTO();
    pushMessageDTO.setShopId(originShop.getId());
    pushMessageDTO.setCreatorType(OperatorType.SHOP);
    pushMessageDTO.setCreatorId(pushMessageDTO.getShopId());
    pushMessageDTO.setRelatedObjectId(sourceId);

    pushMessageDTO.setCreateTime(invitedTime);
    pushMessageDTO.setEndDate(ConfigUtils.getPushMessageLifeCycle(pushMessageDTO.getCreateTime()));

    pushMessageDTO.setLevel(PushMessageLevel.HIGH);

    if (sourceType == PushMessageSourceType.APPLY_CUSTOMER) {
      pushMessageDTO.setType(PushMessageType.APPLY_CUSTOMER);

      PushMessageVelocityContext pushMessageVelocityContext = new PushMessageVelocityContext();
      pushMessageVelocityContext.setShopDTO(originShop);
      VelocityContext context = new VelocityContext();
      context.put(VELOCITY_PARAMETER, pushMessageVelocityContext);
      String content = generateMsgUsingVelocity(context, PushMessageContentTemplate.APPLY_CUSTOMER_CONTENT, "APPLY_CUSTOMER_CONTENT");
      String contentText = generateMsgUsingVelocity(context, PushMessageContentTemplate.APPLY_CUSTOMER_CONTENT_TEXT, "APPLY_CUSTOMER_CONTENT_TEXT");
      Map<String,String> paramsMap = new HashMap<String, String>();
      paramsMap.put(PushMessageParamsKeyConstant.ShopId,originShop.getId().toString());
      pushMessageDTO.setParams(JsonUtil.mapToJson(paramsMap));

      pushMessageDTO.setContentText(contentText);
      pushMessageDTO.setContent(content);
    } else {
      pushMessageDTO.setType(PushMessageType.APPLY_SUPPLIER);

      PushMessageVelocityContext pushMessageVelocityContext = new PushMessageVelocityContext();
      pushMessageVelocityContext.setShopDTO(originShop);
      VelocityContext context = new VelocityContext();
      context.put(VELOCITY_PARAMETER, pushMessageVelocityContext);
      String content = generateMsgUsingVelocity(context, PushMessageContentTemplate.APPLY_SUPPLIER_CONTENT, "APPLY_SUPPLIER_CONTENT");
      String contentText = generateMsgUsingVelocity(context, PushMessageContentTemplate.APPLY_SUPPLIER_CONTENT_TEXT, "APPLY_SUPPLIER_CONTENT_TEXT");

      Map<String,String> paramsMap = new HashMap<String, String>();
      paramsMap.put(PushMessageParamsKeyConstant.ShopId,originShop.getId().toString());
      pushMessageDTO.setParams(JsonUtil.mapToJson(paramsMap));

      pushMessageDTO.setContentText(contentText);
      pushMessageDTO.setContent(content);
    }

    String content = ClientConstant.RELEVANCE_RELATED_MSG;
    pushMessageDTO.setPromptContent(content.replace("{shopName}", originShop.getName()));
    PushMessageReceiverDTO pushMessageReceiverDTO;

    pushMessageReceiverDTO = new PushMessageReceiverDTO();
    pushMessageReceiverDTO.setShopId(invitedShop.getId());
    pushMessageReceiverDTO.setReceiverId(invitedShop.getId());
    pushMessageReceiverDTO.setReceiverType(OperatorType.SHOP);
    pushMessageReceiverDTO.setShowStatus(PushMessageShowStatus.ACTIVE);
    pushMessageReceiverDTO.setShopKind(invitedShop.getShopKind());
    pushMessageDTO.setCurrentPushMessageReceiverDTO(pushMessageReceiverDTO);

    PushMessageSourceDTO pushMessageSourceDTO = new PushMessageSourceDTO();
    pushMessageSourceDTO.setCreateTime(System.currentTimeMillis());
    pushMessageSourceDTO.setSourceId(sourceId);
    pushMessageSourceDTO.setShopId(originShop.getId());
    pushMessageSourceDTO.setType(sourceType);
    pushMessageDTO.setPushMessageSourceDTO(pushMessageSourceDTO);

    pushMessageService.createPushMessage(pushMessageDTO,true);
  }

  @Override
  public void createSingleMobileAutoMatchApplyRelatedMessage(ShopDTO shopDTO, Long shopVersionId) throws Exception {
    //查找匹配客户店面
    IApplyService applyService = ServiceManager.getService(IApplyService.class);
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    Long shopId = shopDTO.getId();
    //关联匹配客户 shopDTO 下面的客户拿出来去匹配
    if (ConfigUtils.isWholesalerVersion(shopVersionId)) {
      PushMessageType pushMessageType = PushMessageType.MATCHING_RECOMMEND_CUSTOMER;
      List<CustomerRelatedShopDTO> customerRelatedShopDTOList = applyService.getRelatedShopByCustomerMobile(shopId, shopDTO.getShopKind());
      if (CollectionUtil.isEmpty(customerRelatedShopDTOList)) return;
      for (CustomerRelatedShopDTO dto : customerRelatedShopDTOList) {
        String promptContent = ClientConstant.SINGLE_MATCHING_RECOMMEND_CUSTOMER_RELATED_MSG;
        promptContent = promptContent.replace("{shopName}", dto.getRelatedShopName()).replace("{customerName}", dto.getCustomerName());
        //生成匹配消息
        ShopDTO relatedShopDTO = configService.getShopById(dto.getRelatedShopId());
        PushMessageVelocityContext pushMessageVelocityContext = new PushMessageVelocityContext();
        pushMessageVelocityContext.setShopDTO(relatedShopDTO);
        pushMessageVelocityContext.setCustomerName(dto.getCustomerName());

        VelocityContext context = new VelocityContext();
        context.put(VELOCITY_PARAMETER, pushMessageVelocityContext);
        String content = generateMsgUsingVelocity(context, PushMessageContentTemplate.SINGLE_MATCHING_RECOMMEND_CUSTOMER_RELATED_CONTENT, "SINGLE_MATCHING_RECOMMEND_CUSTOMER_RELATED_CONTENT");
        String contentText = generateMsgUsingVelocity(context, PushMessageContentTemplate.SINGLE_MATCHING_RECOMMEND_CUSTOMER_RELATED_CONTENT_TEXT, "SINGLE_MATCHING_RECOMMEND_CUSTOMER_RELATED_CONTENT_TEXT");

        IPushMessageService pushMessageService = ServiceManager.getService(IPushMessageService.class);
        PushMessageDTO pushMessageDTO = new PushMessageDTO();
        pushMessageDTO.setShopId(ShopConstant.BC_SHOP_ID);
        pushMessageDTO.setCreatorType(OperatorType.SHOP);
        pushMessageDTO.setCreatorId(pushMessageDTO.getShopId());
        pushMessageDTO.setCreateTime(System.currentTimeMillis());
        pushMessageDTO.setEndDate(ConfigUtils.getPushMessageLifeCycle(pushMessageDTO.getCreateTime()));

        Map<String,String> paramsMap = new HashMap<String, String>();
        paramsMap.put(PushMessageParamsKeyConstant.ShopId,dto.getRelatedShopId().toString());
        paramsMap.put(PushMessageParamsKeyConstant.CustomerId,dto.getCustomerId().toString());
        paramsMap.put(PushMessageParamsKeyConstant.AppParams, dto.getRelatedShopId() + ";" + dto.getCustomerId());
        pushMessageDTO.setParams(JsonUtil.mapToJson(paramsMap));

        pushMessageDTO.setTitle(ClientConstant.RELEVANCE_TITLE);
        pushMessageDTO.setLevel(PushMessageLevel.NORMAL);
        pushMessageDTO.setType(pushMessageType);
        pushMessageDTO.setPromptContent(promptContent);

        pushMessageDTO.setContent(content);
        pushMessageDTO.setContentText(contentText);

        PushMessageReceiverDTO pushMessageReceiverDTO = new PushMessageReceiverDTO();
        pushMessageReceiverDTO.setShopId(shopDTO.getId());
        pushMessageReceiverDTO.setReceiverId(shopDTO.getId());
        pushMessageReceiverDTO.setReceiverType(OperatorType.SHOP);
        pushMessageReceiverDTO.setShopKind(shopDTO.getShopKind());
        pushMessageReceiverDTO.setShowStatus(PushMessageShowStatus.ACTIVE);
        pushMessageDTO.setCurrentPushMessageReceiverDTO(pushMessageReceiverDTO);
        PushMessageSourceDTO pushMessageSourceDTO = new PushMessageSourceDTO();
        pushMessageSourceDTO.setSourceId(dto.getRelatedShopId());
        pushMessageSourceDTO.setCreateTime(System.currentTimeMillis());
        pushMessageSourceDTO.setShopId(dto.getRelatedShopId());
        pushMessageSourceDTO.setType(PushMessageSourceType.MATCHING_RECOMMEND_CUSTOMER_SHOP);
        pushMessageDTO.setPushMessageSourceDTO(pushMessageSourceDTO);

        pushMessageService.createPushMessage(pushMessageDTO,false);
      }
    }

    //匹配供应商 shopDTO 下面的供应商拿出来去匹配
    if (true) {
      PushMessageType pushMessageType = PushMessageType.MATCHING_RECOMMEND_SUPPLIER;

      List<SupplierRelatedShopDTO> supplierRelatedShopDTOList = applyService.getRelatedShopBySupplierMobile(shopId, shopDTO.getShopKind());
      if (CollectionUtil.isEmpty(supplierRelatedShopDTOList)) return;
      for (SupplierRelatedShopDTO dto : supplierRelatedShopDTOList) {
        String promptContent = ClientConstant.SINGLE_MATCHING_RECOMMEND_SUPPLIER_RELATED_MSG;
        promptContent = promptContent.replace("{shopName}", dto.getRelatedShopName()).replace("{supplierName}", dto.getSupplierName());
        //生成匹配消息
        ShopDTO relatedShopDTO = configService.getShopById(dto.getRelatedShopId());
        PushMessageVelocityContext pushMessageVelocityContext = new PushMessageVelocityContext();
        pushMessageVelocityContext.setShopDTO(relatedShopDTO);
        pushMessageVelocityContext.setSupplierName(dto.getSupplierName());

        VelocityContext context = new VelocityContext();
        context.put(VELOCITY_PARAMETER, pushMessageVelocityContext);
        String content = generateMsgUsingVelocity(context, PushMessageContentTemplate.SINGLE_MATCHING_RECOMMEND_SUPPLIER_RELATED_CONTENT, "SINGLE_MATCHING_RECOMMEND_SUPPLIER_RELATED_CONTENT");
        String contentText = generateMsgUsingVelocity(context, PushMessageContentTemplate.SINGLE_MATCHING_RECOMMEND_SUPPLIER_RELATED_CONTENT_TEXT, "SINGLE_MATCHING_RECOMMEND_SUPPLIER_RELATED_CONTENT_TEXT");

        IPushMessageService pushMessageService = ServiceManager.getService(IPushMessageService.class);
        PushMessageDTO pushMessageDTO = new PushMessageDTO();
        pushMessageDTO.setShopId(ShopConstant.BC_SHOP_ID);
        pushMessageDTO.setCreatorType(OperatorType.SYS);
        pushMessageDTO.setCreatorId(pushMessageDTO.getShopId());
        pushMessageDTO.setCreateTime(System.currentTimeMillis());
        pushMessageDTO.setEndDate(ConfigUtils.getPushMessageLifeCycle(pushMessageDTO.getCreateTime()));

        Map<String,String> paramsMap = new HashMap<String, String>();
        paramsMap.put(PushMessageParamsKeyConstant.ShopId,dto.getRelatedShopId().toString());
        paramsMap.put(PushMessageParamsKeyConstant.SupplierId,dto.getSupplierId().toString());
        paramsMap.put(PushMessageParamsKeyConstant.AppParams,dto.getRelatedShopId() + ";" + dto.getSupplierId());
        pushMessageDTO.setParams(JsonUtil.mapToJson(paramsMap));

        pushMessageDTO.setTitle(ClientConstant.RELEVANCE_TITLE);
        pushMessageDTO.setLevel(PushMessageLevel.NORMAL);
        pushMessageDTO.setType(pushMessageType);
        pushMessageDTO.setPromptContent(promptContent);
        pushMessageDTO.setContent(content);
        pushMessageDTO.setContentText(contentText);

        PushMessageReceiverDTO pushMessageReceiverDTO = new PushMessageReceiverDTO();
        pushMessageReceiverDTO.setShopId(shopDTO.getId());
        pushMessageReceiverDTO.setReceiverId(shopDTO.getId());
        pushMessageReceiverDTO.setReceiverType(OperatorType.SHOP);
        pushMessageReceiverDTO.setShopKind(shopDTO.getShopKind());
        pushMessageReceiverDTO.setShowStatus(PushMessageShowStatus.ACTIVE);
        pushMessageDTO.setCurrentPushMessageReceiverDTO(pushMessageReceiverDTO);

        PushMessageSourceDTO pushMessageSourceDTO = new PushMessageSourceDTO();
        pushMessageSourceDTO.setSourceId(dto.getRelatedShopId());
        pushMessageSourceDTO.setCreateTime(System.currentTimeMillis());
        pushMessageSourceDTO.setShopId(dto.getRelatedShopId());
        pushMessageSourceDTO.setType(PushMessageSourceType.MATCHING_RECOMMEND_SUPPLIER_SHOP);
        pushMessageDTO.setPushMessageSourceDTO(pushMessageSourceDTO);

        pushMessageService.createPushMessage(pushMessageDTO,false);
      }
    }
  }

  @Override
  public Set<Long> getApplyPushMessageShopIds(Long pushMessageId) throws Exception{
    TxnWriter writer = txnDaoManager.getWriter();
    PushMessage pushMessage = writer.getById(PushMessage.class, pushMessageId);
    Set<Long> shopIdSet = new HashSet<Long>();
    Map<String,String> paramsMap = JsonUtil.jsonToStringMap(pushMessage.getParams());
    String shopIds = paramsMap.get(PushMessageParamsKeyConstant.ShopIds);
    if(StringUtils.isNotBlank(shopIds)){
      shopIdSet = new HashSet<Long>(Arrays.asList(StringUtil.parseLongArray(shopIds.split(","))));
    }
    return shopIdSet;
  }

  @Override
  public void addCancelRecommendAssociatedCount(Long pushMessageId) throws Exception{
    ICustomerService customerService = ServiceManager.getService(ICustomerService.class);
    ISupplierService supplierService = ServiceManager.getService(ISupplierService.class);
//    IConfigService configService = ServiceManager.getService(IConfigService.class);
    TxnWriter writer = txnDaoManager.getWriter();
    PushMessage pushMessage = writer.getById(PushMessage.class, pushMessageId);
    if (pushMessage == null) return;
    if (pushMessage.getType() != PushMessageType.MATCHING_RECOMMEND_CUSTOMER
        && pushMessage.getType() != PushMessageType.MATCHING_RECOMMEND_SUPPLIER) {
       return;
    }
    Map<String,String> paramsMap = JsonUtil.jsonToStringMap(pushMessage.getParams());
    if (pushMessage.getType() == PushMessageType.MATCHING_RECOMMEND_CUSTOMER) {
      String customerIds = paramsMap.get(PushMessageParamsKeyConstant.CustomerIds);
      if(StringUtils.isNotBlank(customerIds)){
        customerService.addCancelRecommendAssociatedCount(new HashSet<Long>(Arrays.asList(StringUtil.parseLongArray(customerIds.split(",")))));
      }
    } else {
      String supplierIds = paramsMap.get(PushMessageParamsKeyConstant.SupplierIds);
      if(StringUtils.isNotBlank(supplierIds)){
        supplierService.addCancelRecommendAssociatedCount(new HashSet<Long>(Arrays.asList(StringUtil.parseLongArray(supplierIds.split(",")))));
      }
    }
  }

  @Override
  public void cancelApplyRecommendAssociated(Long pushMessageId) throws Exception{
    TxnWriter writer = txnDaoManager.getWriter();
    PushMessage pushMessage = writer.getById(PushMessage.class, pushMessageId);
    if (pushMessage == null) return;
    if (pushMessage.getType() != PushMessageType.MATCHING_RECOMMEND_CUSTOMER
        && pushMessage.getType() != PushMessageType.MATCHING_RECOMMEND_SUPPLIER) {
      return;
    }
    ICustomerService customerService = ServiceManager.getService(ICustomerService.class);
    ISupplierService supplierService = ServiceManager.getService(ISupplierService.class);
    Map<String,String> paramsMap = JsonUtil.jsonToStringMap(pushMessage.getParams());
    if (pushMessage.getType() == PushMessageType.MATCHING_RECOMMEND_CUSTOMER) {
      String customerIds = paramsMap.get(PushMessageParamsKeyConstant.CustomerIds);
      if(StringUtils.isNotBlank(customerIds)){
        customerService.cancelApplyRecommendAssociated(new HashSet<Long>(Arrays.asList(StringUtil.parseLongArray(customerIds.split(",")))));
      }
    } else {
      String supplierIds = paramsMap.get(PushMessageParamsKeyConstant.SupplierIds);
      if(StringUtils.isNotBlank(supplierIds)){
        supplierService.cancelApplyRecommendAssociated(new HashSet<Long>(Arrays.asList(StringUtil.parseLongArray(supplierIds.split(",")))));
      }
    }
  }
}
