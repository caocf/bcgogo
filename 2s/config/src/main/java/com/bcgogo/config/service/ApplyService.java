package com.bcgogo.config.service;

import com.bcgogo.common.Pager;
import com.bcgogo.common.PagingListResult;
import com.bcgogo.common.Result;
import com.bcgogo.config.dto.*;
import com.bcgogo.config.model.*;
import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.config.util.MemCacheAdapter;
import com.bcgogo.constant.MemcachePrefix;
import com.bcgogo.enums.ObjectTypes;
import com.bcgogo.enums.OperationTypes;
import com.bcgogo.enums.RelationTypes;
import com.bcgogo.enums.ShopRelationStatus;
import com.bcgogo.enums.shop.InviteCountStatus;
import com.bcgogo.enums.shop.InviteStatus;
import com.bcgogo.enums.shop.InviteType;
import com.bcgogo.enums.shop.ShopKind;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.dto.CustomerDTO;
import com.bcgogo.user.dto.SupplierDTO;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.ShopConstant;
import com.bcgogo.utils.ValidatorConstant;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-1-22
 * Time: 下午1:31
 * To change this template use File | Settings | File Templates.
 */
@Component
public class ApplyService implements IApplyService {
  public static final Long EXPIRED_TIME = 2 * 24 * 60 * 60 * 1000l;
  @Autowired
  private ConfigDaoManager configDaoManager;

  private static final Logger LOG = LoggerFactory.getLogger(ApplyService.class);

  private IConfigService configService;
  private IOperationLogService operationLogService;
  private IShopService shopService;

  public IConfigService getConfigService() {
    return configService == null ? ServiceManager.getService(IConfigService.class) : configService;
  }

  public IOperationLogService getOperationLogService() {
    return operationLogService == null ? ServiceManager.getService(IOperationLogService.class) : operationLogService;
  }

  public IShopService getShopService() {
    return shopService == null ? ServiceManager.getService(IShopService.class) : shopService;
  }

  @Override
  public Result validateApplySupplierRelation(Long shopId, Long... supplerShopIds) {
    if (shopId == null && ArrayUtils.isEmpty(supplerShopIds)) {
      return new Result("网络异常，请联系管理员", false);
    }
    for (Long supplierShopId : supplerShopIds) {
      if (!getConfigService().checkLimitCustomerAmount(supplierShopId, 1)) {
        return new Result("对不起，您申请关联的供应商的客户数量已达上限，无法继续添加关联客户，如需继续添加，请联系客服。", false);
      }
    }
    Map<Long, WholesalerShopRelationDTO> wholesalerShopRelationDTOs = getConfigService()
        .getWholesalerShopRelationMapByWholesalerShopId(shopId, RelationTypes.CUSTOMER_RELATE_TO_WHOLESALER_LIST, supplerShopIds);
    if (wholesalerShopRelationDTOs != null && !wholesalerShopRelationDTOs.isEmpty()) {
      if (wholesalerShopRelationDTOs.size() >= supplerShopIds.length) {
        return new Result("当前店铺已经是您的供应商，请勿重复申请", false);
      }
    }
    Map<Long, ShopRelationInviteDTO> shopRelationInviteDTOMap = getShopRelationInviteDTOMapByInvitedShopIds(
        InviteType.CUSTOMER_INVITE, InviteStatus.PENDING, shopId, EXPIRED_TIME, supplerShopIds);
    if (shopRelationInviteDTOMap != null && !shopRelationInviteDTOMap.isEmpty()) {
      if (shopRelationInviteDTOMap.size() >= supplerShopIds.length) {
        return new Result("您已提交关联申请，请勿重复申请", false);
      }
    }
    //校验对方是否邀请过
    Map<Long, ShopRelationInviteDTO> customerShopRelationInviteDTOMap = getShopRelationInviteDTOMapByOriginShopId(
        InviteType.SUPPLIER_INVITE, InviteStatus.PENDING, shopId, EXPIRED_TIME, supplerShopIds);
    if (customerShopRelationInviteDTOMap != null && !customerShopRelationInviteDTOMap.isEmpty()) {
      if (customerShopRelationInviteDTOMap.size() >= supplerShopIds.length) {
        return new Result("对方已经向您提交申请，请勿重复申请", false);
      }
    }
    return new Result();
  }

  @Override
  public Long[] initApplySupplierRelationShopIds(Long shopId, Long... supplerShopIds) {
    if (shopId == null || ArrayUtils.isEmpty(supplerShopIds)) {
      return new Long[0];
    }
    Set<Long> excludeSupplierShopIdSet = new HashSet<Long>();
    //校验是否已经是关联供应商
    Map<Long, WholesalerShopRelationDTO> wholesalerShopRelationDTOs = getConfigService()
        .getWholesalerShopRelationMapByWholesalerShopId(shopId, RelationTypes.CUSTOMER_RELATE_TO_WHOLESALER_LIST, supplerShopIds);
    if (wholesalerShopRelationDTOs != null && !wholesalerShopRelationDTOs.isEmpty()) {
      excludeSupplierShopIdSet.addAll(wholesalerShopRelationDTOs.keySet());
    }
    //校验是否已经提出过申请
    Map<Long, ShopRelationInviteDTO> shopRelationInviteDTOMap = getShopRelationInviteDTOMapByInvitedShopIds(
        InviteType.CUSTOMER_INVITE, InviteStatus.PENDING, shopId, EXPIRED_TIME, supplerShopIds);
    if (shopRelationInviteDTOMap != null && !shopRelationInviteDTOMap.isEmpty()) {
      excludeSupplierShopIdSet.addAll(shopRelationInviteDTOMap.keySet());
    }
    //校验对方是否邀请过
    Map<Long, ShopRelationInviteDTO> customerShopRelationInviteDTOMap = getShopRelationInviteDTOMapByOriginShopId(
        InviteType.SUPPLIER_INVITE, InviteStatus.PENDING, shopId, EXPIRED_TIME, supplerShopIds);
    if (customerShopRelationInviteDTOMap != null && !customerShopRelationInviteDTOMap.isEmpty()) {
      excludeSupplierShopIdSet.addAll(customerShopRelationInviteDTOMap.keySet());
    }

    Set<Long> totalSupplierShopIds = new HashSet<Long>(Arrays.asList(supplerShopIds));
    totalSupplierShopIds.removeAll(excludeSupplierShopIdSet);
    return totalSupplierShopIds.toArray(new Long[totalSupplierShopIds.size()]);
  }

  @Override
  public ShopRelationInviteDTO saveApplySupplierRelation(Long shopId, Long userId, Long supplerShopId) throws Exception {
    if (shopId == null || supplerShopId == null) {
      return null;
    }
    ConfigWriter writer = configDaoManager.getWriter();
    Object status = writer.begin();
    try {
      List<ShopRelationInvite> shopRelationInvites = writer.getShopRelationInvitesByInvitedShopIds(
          InviteType.CUSTOMER_INVITE, InviteStatus.PENDING, shopId, null, supplerShopId);
      ShopRelationInvite shopRelationInvite;
      if (CollectionUtils.isNotEmpty(shopRelationInvites)) {
        shopRelationInvite = shopRelationInvites.get(0);
        shopRelationInvite.setInviteTime(System.currentTimeMillis());
        writer.update(shopRelationInvite);
        writer.commit(status);
        getOperationLogService().saveOperationLog(
            new OperationLogDTO(shopId, userId, shopRelationInvite.getId(), ObjectTypes.APPLY_SUPPLIER, OperationTypes.UPDATE));
      } else {
        shopRelationInvite = new ShopRelationInvite(shopId, userId, supplerShopId);
        shopRelationInvite.setInviteType(InviteType.CUSTOMER_INVITE);
        shopRelationInvite.setStatus(InviteStatus.PENDING);
        writer.save(shopRelationInvite);
        writer.commit(status);
        getOperationLogService().saveOperationLog(
            new OperationLogDTO(shopId, userId, shopRelationInvite.getId(), ObjectTypes.APPLY_SUPPLIER, OperationTypes.CREATE));
      }
      return shopRelationInvite.toDTO();
    } finally {
      writer.rollback(status);
    }

  }

  @Override
  public List<ShopRelationInviteDTO> batchSaveApplySupplierRelation(Long shopId, Long userId, Long... supplerShopIds) throws Exception {
    List<ShopRelationInviteDTO> result = new ArrayList<ShopRelationInviteDTO>();
    ShopRelationInviteDTO dto;
    if (!ArrayUtils.isEmpty(supplerShopIds)) {
      for (Long supplierShopId : supplerShopIds) {
        dto = saveApplySupplierRelation(shopId, userId, supplierShopId);
        if (dto != null) {
          result.add(dto);
        }
      }
    }
    return result;
  }

  @Override
  public Result validateApplyCustomerRelation(Long shopId, Long... customerShopId) {
    if (shopId == null) {
      return new Result("网络异常，请联系管理员", false);
    }
    if (!getConfigService().checkLimitCustomerAmount(shopId, customerShopId.length)) {
      return new Result("对不起，您关联的客户数已达上限，无法继续添加关联客户，如需继续添加，请联系客服。", false);
    }
    List<RelationTypes> relationTypes = new ArrayList<RelationTypes>();
    relationTypes.add(RelationTypes.RELATED);
    Map<Long, WholesalerShopRelationDTO> wholesalerShopRelationDTOs = getConfigService()
        .getWholesalerShopRelationMapByCustomerShopId(shopId,relationTypes, customerShopId);
    if (wholesalerShopRelationDTOs != null && !wholesalerShopRelationDTOs.isEmpty()) {
      if (wholesalerShopRelationDTOs.size() >= customerShopId.length) {
        return new Result("当前店铺已经是您的客户，请勿重复申请", false);
      }
    }
    Map<Long, ShopRelationInviteDTO> shopRelationInviteDTOMap = getShopRelationInviteDTOMapByInvitedShopIds(
        InviteType.SUPPLIER_INVITE, InviteStatus.PENDING, shopId, EXPIRED_TIME, customerShopId);
    if (shopRelationInviteDTOMap != null && !shopRelationInviteDTOMap.isEmpty()) {
      if (shopRelationInviteDTOMap.size() >= customerShopId.length) {
        return new Result("您已提交关联申请，请勿重复申请", false);
      }
    }

    //校验对方是否邀请过自己
    Map<Long, ShopRelationInviteDTO> appliedShopRelationInviteDTOMap = getShopRelationInviteDTOMapByOriginShopId(
        InviteType.CUSTOMER_INVITE, InviteStatus.PENDING, shopId, EXPIRED_TIME, customerShopId);
    if (appliedShopRelationInviteDTOMap != null && !appliedShopRelationInviteDTOMap.isEmpty()) {
      if (appliedShopRelationInviteDTOMap.size() >= customerShopId.length) {
        return new Result("对方已经向您提交申请，请勿重复申请", false);
      }
    }
    return new Result();
  }

  @Override
  public Long[] initApplyCustomerRelationShopIds(Long shopId, Long... customerShopId) {
    if (shopId == null || ArrayUtils.isEmpty(customerShopId)) {
      return new Long[0];
    }
    Set<Long> excludeSupplierShopIdSet = new HashSet<Long>();
    List<RelationTypes> relationTypes = new ArrayList<RelationTypes>();
    relationTypes.add(RelationTypes.RELATED);
    Map<Long, WholesalerShopRelationDTO> wholesalerShopRelationDTOs = getConfigService()
        .getWholesalerShopRelationMapByCustomerShopId(shopId,relationTypes, customerShopId);
    if (wholesalerShopRelationDTOs != null && !wholesalerShopRelationDTOs.isEmpty()) {
      excludeSupplierShopIdSet.addAll(wholesalerShopRelationDTOs.keySet());
    }
    Map<Long, ShopRelationInviteDTO> shopRelationInviteDTOMap = getShopRelationInviteDTOMapByInvitedShopIds(
        InviteType.SUPPLIER_INVITE, InviteStatus.PENDING, shopId, EXPIRED_TIME, customerShopId);
    if (shopRelationInviteDTOMap != null && !shopRelationInviteDTOMap.isEmpty()) {
      excludeSupplierShopIdSet.addAll(wholesalerShopRelationDTOs.keySet());
    }
    //校验对方是否邀请过自己
    Map<Long, ShopRelationInviteDTO> customerShopRelationInviteDTOMap = getShopRelationInviteDTOMapByOriginShopId(
        InviteType.CUSTOMER_INVITE, InviteStatus.PENDING, shopId, EXPIRED_TIME, customerShopId);
    if (customerShopRelationInviteDTOMap != null && !customerShopRelationInviteDTOMap.isEmpty()) {
      excludeSupplierShopIdSet.addAll(customerShopRelationInviteDTOMap.keySet());
    }

    Set<Long> totalSupplierShopIds = new HashSet<Long>(Arrays.asList(customerShopId));
    totalSupplierShopIds.removeAll(excludeSupplierShopIdSet);
    return totalSupplierShopIds.toArray(new Long[totalSupplierShopIds.size()]);
  }

  @Override
  public ShopRelationInviteDTO saveApplyCustomerRelation(Long shopId, Long userId, Long customerShopId) throws Exception {
    if (shopId == null || customerShopId == null) {
      return null;
    }
    ConfigWriter writer = configDaoManager.getWriter();
    Object status = writer.begin();
    try {
      List<ShopRelationInvite> shopRelationInvites = writer.getShopRelationInvitesByInvitedShopIds(
          InviteType.SUPPLIER_INVITE, InviteStatus.PENDING, shopId, null, customerShopId);
      ShopRelationInvite shopRelationInvite;
      if (CollectionUtils.isNotEmpty(shopRelationInvites)) {
        shopRelationInvite = shopRelationInvites.get(0);
        shopRelationInvite.setInviteTime(System.currentTimeMillis());
        writer.update(shopRelationInvite);
        writer.commit(status);
        getOperationLogService().saveOperationLog(
            new OperationLogDTO(shopId, userId, shopRelationInvite.getId(), ObjectTypes.APPLY_CUSTOMER, OperationTypes.UPDATE));
      } else {
        shopRelationInvite = new ShopRelationInvite(shopId, userId, customerShopId);
        shopRelationInvite.setInviteType(InviteType.SUPPLIER_INVITE);
        shopRelationInvite.setStatus(InviteStatus.PENDING);
        writer.save(shopRelationInvite);
        writer.commit(status);
        getOperationLogService().saveOperationLog(new OperationLogDTO(shopId, userId, shopRelationInvite.getId(), ObjectTypes.APPLY_CUSTOMER, OperationTypes.CREATE));
      }
      return shopRelationInvite.toDTO();
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public List<ShopRelationInviteDTO> batchSaveApplyCustomerRelation(Long shopId, Long userId, Long... customerShopIds) throws Exception {
    List<ShopRelationInviteDTO> result = new ArrayList<ShopRelationInviteDTO>();
    ShopRelationInviteDTO dto;
    if (!ArrayUtils.isEmpty(customerShopIds)) {
      for (Long customerShopId : customerShopIds) {
        dto = saveApplyCustomerRelation(shopId, userId, customerShopId);
        if (dto != null) {
          result.add(dto);
        }
      }
    }
    return result;
  }

  @Override
  public List<ApplyShopSearchCondition> searchApplyCustomerShop(ApplyShopSearchCondition searchCondition, String shopVersionIdStr, Pager pager
      , boolean isTesShop) {
    List<ApplyShopSearchCondition> applyShopSearchConditions = new ArrayList<ApplyShopSearchCondition>();
    List<Shop> shops = configDaoManager.getWriter().searchApplyCustomerShops(searchCondition, shopVersionIdStr, isTesShop, pager);
    if (CollectionUtil.isNotEmpty(shops)) {
      Set<Long> customerShopIds = new HashSet<Long>();
      Set<Long> areaNos = new HashSet<Long>();
      for (Shop shop : shops) {
        customerShopIds.add(shop.getId());
        areaNos.add(shop.getProvince());
        areaNos.add(shop.getCity());
        areaNos.add(shop.getRegion());
      }
      Map<Long, AreaDTO> areaMap = getConfigService().getAreaByAreaNo(areaNos);
      Map<Long, ShopRelationInviteDTO> shopRelationInviteDTOMap = getShopRelationInviteDTOMapByInvitedShopIds(
          InviteType.SUPPLIER_INVITE, InviteStatus.PENDING, searchCondition.getShopId(), EXPIRED_TIME, customerShopIds.toArray(new Long[customerShopIds.size()]));
      Map<Long, ShopRelationInviteDTO> oppositesShopRelationInviteDTOMap = getShopRelationInviteDTOMapByOriginShopId(
          InviteType.CUSTOMER_INVITE, InviteStatus.PENDING, searchCondition.getShopId(), EXPIRED_TIME, customerShopIds.toArray(new Long[customerShopIds.size()]));


      for (Shop shop : shops) {
        ShopDTO shopDTO = shop.toDTO();
        shopDTO.setAreaNameByAreaNo(areaMap);
        ApplyShopSearchCondition applyShopSearchCondition = shopDTO.toApplyShopDTO();
        ShopRelationInviteDTO shopRelationInviteDTO = shopRelationInviteDTOMap.get(shop.getId());
        ShopRelationInviteDTO oppositesShopRelationInviteDTO = oppositesShopRelationInviteDTOMap.get(shop.getId());

        if (shopRelationInviteDTO != null) {
          applyShopSearchCondition.setInviteStatus(shopRelationInviteDTO.getStatus());
        } else if (oppositesShopRelationInviteDTO != null) {
          applyShopSearchCondition.setInviteStatus(InviteStatus.OPPOSITES_PENDING);
        }
        applyShopSearchConditions.add(applyShopSearchCondition);
      }
    }
    return applyShopSearchConditions;
  }

  @Override
  public List<ApplyShopSearchCondition> searchApplySupplierShop(ApplyShopSearchCondition searchCondition, String shopVersionIdStr, Pager pager
      , boolean isTesShop) {
    List<ApplyShopSearchCondition> applyShopSearchConditions = new ArrayList<ApplyShopSearchCondition>();
    List<Shop> shops = configDaoManager.getWriter().searchApplySupplierShops(searchCondition, shopVersionIdStr, isTesShop, pager);

    if (CollectionUtil.isNotEmpty(shops)) {
      Set<Long> supplierShopIds = new HashSet<Long>();
      Set<Long> areaNos = new HashSet<Long>();
      for (Shop shop : shops) {
        supplierShopIds.add(shop.getId());
        areaNos.add(shop.getProvince());
        areaNos.add(shop.getCity());
        areaNos.add(shop.getRegion());
      }
      Map<Long, AreaDTO> areaMap = getConfigService().getAreaByAreaNo(areaNos);
      //自己已经申请过的
      Map<Long, ShopRelationInviteDTO> shopRelationInviteDTOMap = getShopRelationInviteDTOMapByInvitedShopIds(
          InviteType.CUSTOMER_INVITE, InviteStatus.PENDING, searchCondition.getShopId(), EXPIRED_TIME, supplierShopIds.toArray(new Long[supplierShopIds.size()]));
      //对方申请过自己的
      Map<Long, ShopRelationInviteDTO> oppositesShopRelationInviteDTOMap = getShopRelationInviteDTOMapByOriginShopId(
          InviteType.SUPPLIER_INVITE, InviteStatus.PENDING, searchCondition.getShopId(), EXPIRED_TIME, supplierShopIds.toArray(new Long[supplierShopIds.size()]));
      ShopRelationInviteDTO shopRelationInviteDTO = null;
      for (Shop shop : shops) {
        ShopDTO shopDTO = shop.toDTO();
        shopDTO.setAreaNameByAreaNo(areaMap);
        ApplyShopSearchCondition applyShopSearchCondition = shopDTO.toApplyShopDTO();
        shopRelationInviteDTO = shopRelationInviteDTOMap.get(shop.getId());
        ShopRelationInviteDTO oppositesShopRelationInviteDTO = oppositesShopRelationInviteDTOMap.get(shop.getId());
        if (shopRelationInviteDTO != null) {
          applyShopSearchCondition.setInviteStatus(shopRelationInviteDTO.getStatus());
        } else if (oppositesShopRelationInviteDTO != null) {
          applyShopSearchCondition.setInviteStatus(InviteStatus.OPPOSITES_PENDING);
        }
        applyShopSearchConditions.add(applyShopSearchCondition);
      }
    }

    return applyShopSearchConditions;
  }

  @Override
  public Integer countApplyCustomerShop(ApplyShopSearchCondition searchCondition, String shopVersionIdStr, boolean isTesShop) {
    return configDaoManager.getWriter().countApplyCustomerShop(searchCondition, shopVersionIdStr, isTesShop);
  }

  @Override
  public Integer countApplySupplierShop(ApplyShopSearchCondition searchCondition, String shopVersionIdStr, boolean isTestShop) {
    return configDaoManager.getWriter().countApplySupplierShop(searchCondition, shopVersionIdStr, isTestShop);
  }
  @Override
  public boolean isTestShop(ShopDTO shopDTO) {
    if (shopDTO != null && shopDTO.getShopKind() != null && shopDTO.getShopKind().equals(ShopKind.TEST)) {
      return true;
    }
    return false;
  }

  @Override
  public Map<Long, ShopRelationInviteDTO> getShopRelationInviteDTOMapByInvitedShopIds(
      InviteType inviteType, InviteStatus status, Long shopId, Long expiredTime, Long... invitedShopIds) {
    Map<Long, ShopRelationInviteDTO> shopRelationInviteDTOMap = new HashMap<Long, ShopRelationInviteDTO>();
    if (shopId == null || ArrayUtils.isEmpty(invitedShopIds)) {
      return shopRelationInviteDTOMap;
    }
    List<ShopRelationInvite> shopRelationInvites = configDaoManager.getWriter().
        getShopRelationInvitesByInvitedShopIds(inviteType, status, shopId, expiredTime, invitedShopIds);
    if (CollectionUtils.isNotEmpty(shopRelationInvites)) {
      for (ShopRelationInvite shopRelationInvite : shopRelationInvites) {
        shopRelationInviteDTOMap.put(shopRelationInvite.getInvitedShopId(), shopRelationInvite.toDTO());
      }
    }
    return shopRelationInviteDTOMap;
  }

  @Override
  public Map<Long, ShopRelationInviteDTO> getShopRelationInviteDTOMapByOriginShopId(
      InviteType inviteType, InviteStatus status, Long invitedShopId, Long expiredTime, Long... originShopIds) {
    Map<Long, ShopRelationInviteDTO> shopRelationInviteDTOMap = new HashMap<Long, ShopRelationInviteDTO>();
    if (invitedShopId == null || ArrayUtils.isEmpty(originShopIds)) {
      return shopRelationInviteDTOMap;
    }
    List<ShopRelationInvite> shopRelationInvites = configDaoManager.getWriter().
        getShopRelationInvitesByOriginShopIds(inviteType, status, invitedShopId, expiredTime, originShopIds);
    if (CollectionUtils.isNotEmpty(shopRelationInvites)) {
      for (ShopRelationInvite shopRelationInvite : shopRelationInvites) {
        shopRelationInviteDTOMap.put(shopRelationInvite.getOriginShopId(), shopRelationInvite.toDTO());
      }
    }
    return shopRelationInviteDTOMap;
  }

  @Override
  public ShopRelationInviteDTO getShopRelationInviteDTOByInvitedShopIdAndId(Long invitedShopId, Long inviteId) {
    if (invitedShopId == null || inviteId == null) {
      return null;
    }
    ShopRelationInvite shopRelationInvite = configDaoManager.getWriter().getShopRelationInviteByInvitedShopIdAndId(invitedShopId, inviteId);
    if (shopRelationInvite != null) {
      return shopRelationInvite.toDTO();
    }
    return null;
  }

  @Override
  public Result validateRefuseApply(ShopDTO shopDTO, ShopRelationInviteDTO shopRelationInviteDTO) {
    Result result = new Result();
    if (shopDTO == null || shopRelationInviteDTO == null) {
      return new Result(ValidatorConstant.REQUEST_ERROR_MSG, false);
    }
    if (!InviteStatus.PENDING.equals(shopRelationInviteDTO.getStatus())) {
      return new Result("当前请求已经被处理，无法拒绝，请刷新页面。", false);
    }
    return result;
  }

  @Override
  public void refuseApply(ShopRelationInviteDTO shopRelationInviteDTO) throws Exception {
    ConfigWriter writer = configDaoManager.getWriter();
    Object status = writer.begin();
    try {
      ShopRelationInvite shopRelationInvite = writer.getShopRelationInviteByInvitedShopIdAndId(
          shopRelationInviteDTO.getInvitedShopId(), shopRelationInviteDTO.getId());
      if (shopRelationInvite != null && InviteStatus.PENDING.equals(shopRelationInvite.getStatus())) {
        shopRelationInviteDTO.setStatus(InviteStatus.REFUSED);
        shopRelationInviteDTO.setOperationTime(System.currentTimeMillis());
        shopRelationInvite.setOperationInfo(shopRelationInviteDTO);
        String refuseMsg = shopRelationInviteDTO.getRefuseMsg();
        if (StringUtils.isBlank(refuseMsg) || refuseMsg.trim().equals("拒绝理由")) {
          refuseMsg = ValidatorConstant.DEFAULT_REFUSE_APPLY_MSG;
          shopRelationInviteDTO.setRefuseMsg(refuseMsg);
        }
        writer.update(shopRelationInvite);
      }
      writer.commit(status);
      getOperationLogService().saveOperationLog(
          new OperationLogDTO(shopRelationInviteDTO.getInvitedShopId(), shopRelationInviteDTO.getOperationManId(),
              shopRelationInvite.getId(), getObjectType(shopRelationInviteDTO.getInviteType()), OperationTypes.REFUSE));
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public Result validateAcceptCustomerApply(ShopDTO shopDTO, ShopRelationInviteDTO shopRelationInviteDTO) {
    if (shopDTO == null || shopRelationInviteDTO == null || shopRelationInviteDTO.getInviteType() == null
        || shopRelationInviteDTO.getOriginShopId() == null || shopRelationInviteDTO.getInvitedShopId() == null) {
      return new Result(ValidatorConstant.REQUEST_ERROR_MSG, false);
    }
    if (!InviteStatus.PENDING.equals(shopRelationInviteDTO.getStatus())) {
      return new Result("当前请求已经被处理，无法接受，请刷新页面。", false);
    }

    if (!getConfigService().checkLimitCustomerAmount(shopDTO.getId(), 1)) {
      return new Result("对不起，您申请关联客户数量已达上限，无法继续添加关联客户，如需继续添加，请联系客服。", false);
    }
    return new Result();
  }

  @Override
  public Result validateAcceptSupplierApply(ShopDTO shopDTO, ShopRelationInviteDTO shopRelationInviteDTO) {
    if (shopDTO == null || shopRelationInviteDTO == null) {
      return new Result(ValidatorConstant.REQUEST_ERROR_MSG, false);
    }
    if (!InviteStatus.PENDING.equals(shopRelationInviteDTO.getStatus())) {
      return new Result("当前请求已经被处理，无法接受，请刷新页面。", false);
    }

    if (!getConfigService().checkLimitCustomerAmount(shopRelationInviteDTO.getOriginShopId(), 1)) {
      return new Result("对不起，对方关联客户数量已达上限，无法继续添加，如需继续，请联系客服。", false);
    }
    return new Result();
  }

  @Override
  public boolean acceptApply(ShopRelationInviteDTO shopRelationInviteDTO) throws Exception {
    ConfigWriter writer = configDaoManager.getWriter();
    Object status = writer.begin();
    boolean isCreatedRelation = false;
    try {
      ShopRelationInvite shopRelationInvite = writer.getShopRelationInviteByInvitedShopIdAndId(
          shopRelationInviteDTO.getInvitedShopId(), shopRelationInviteDTO.getId());
      if (shopRelationInvite != null && InviteStatus.PENDING.equals(shopRelationInvite.getStatus())) {
        shopRelationInviteDTO.setStatus(InviteStatus.ACCEPTED);
        shopRelationInviteDTO.setOperationTime(System.currentTimeMillis());
        shopRelationInvite.setOperationInfo(shopRelationInviteDTO);
        writer.update(shopRelationInvite);
        isCreatedRelation = getConfigService().createWholesalerShopRelationByShopRelation(writer, shopRelationInviteDTO);
      }
      writer.commit(status);
      getOperationLogService().saveOperationLog(
          new OperationLogDTO(shopRelationInviteDTO.getInvitedShopId(), shopRelationInviteDTO.getOperationManId(),
              shopRelationInvite.getId(), getObjectType(shopRelationInviteDTO.getInviteType()), OperationTypes.ACCEPT));
      return isCreatedRelation;
    } finally {
      writer.rollback(status);
    }

  }

  @Override
  public void deleteShopRelationInvites(Long shopId, Long userId, String userName, Long... shopRelationInviteId) throws Exception {
    if (shopId == null || ArrayUtils.isEmpty(shopRelationInviteId)) {
      return;
    }
    ConfigWriter writer = configDaoManager.getWriter();
    Object status = writer.begin();
    Set<ShopRelationInviteDTO> deletedShopRelationDTOs = new HashSet<ShopRelationInviteDTO>();
    try {
      List<ShopRelationInvite> shopRelationInvites = writer.getShopRelationInvitesByInvitedShopIdAndIds(shopId, shopRelationInviteId);
      if (CollectionUtil.isNotEmpty(shopRelationInvites)) {
        for (ShopRelationInvite shopRelationInvite : shopRelationInvites) {
          if (InviteStatus.ACCEPTED.equals(shopRelationInvite.getStatus())
              || InviteStatus.REFUSED.equals(shopRelationInvite.getStatus())) {
            shopRelationInvite.setStatus(InviteStatus.DELETED);
            shopRelationInvite.setOperationMan(userName);
            shopRelationInvite.setOperationManId(userId);
            shopRelationInvite.setOperationTime(System.currentTimeMillis());
            writer.update(shopRelationInvite);
            deletedShopRelationDTOs.add(shopRelationInvite.toDTO());
          }
        }
      }
      writer.commit(status);
      if (CollectionUtil.isNotEmpty(deletedShopRelationDTOs)) {
        for (ShopRelationInviteDTO shopRelationInviteDTO : deletedShopRelationDTOs) {
          getOperationLogService().saveOperationLog(
              new OperationLogDTO(shopRelationInviteDTO.getInvitedShopId(), shopRelationInviteDTO.getOperationManId(),
                  shopRelationInviteDTO.getId(), getObjectType(shopRelationInviteDTO.getInviteType()), OperationTypes.DELETE));
        }
      }
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public PagingListResult<ShopRelationInviteDTO> getShopRelationInvites(Long shopId, InviteType inviteType
      , List<InviteStatus> statuses, Long originShopId, Pager pager) throws Exception {
    ConfigWriter writer = configDaoManager.getWriter();
    int total = writer.countSearchShopRelationInvites(shopId, inviteType, originShopId, statuses);
    pager = new Pager(total, pager.getCurrentPage(), pager.getPageSize());

    IConfigService configService = ServiceManager.getService(IConfigService.class);
    List<ShopRelationInvite> shopRelationInvites = writer.searchShopRelationInvites(shopId, inviteType, originShopId, statuses, pager);
    Set<Long> shopIds = new HashSet<Long>();
    Map<Long, ShopDTO> shopDTOMap = new HashMap<Long, ShopDTO>();
    if (CollectionUtil.isNotEmpty(shopRelationInvites)) {
      for (ShopRelationInvite shopRelationInvite : shopRelationInvites) {
        shopIds.add(shopRelationInvite.getOriginShopId());
      }
      shopDTOMap = getShopService().getShopByShopIds(shopIds.toArray(new Long[shopIds.size()]));
    }
    List<ShopRelationInviteDTO> shopRelationInviteDTOs = new ArrayList<ShopRelationInviteDTO>();
    if (CollectionUtil.isNotEmpty(shopRelationInvites)) {
      for (ShopRelationInvite shopRelationInvite : shopRelationInvites) {
        ShopDTO shopDTO = shopDTOMap.get(shopRelationInvite.getOriginShopId());
        ShopRelationInviteDTO shopRelationInviteDTO = shopRelationInvite.toDTO();
        shopRelationInviteDTO.setOriginShopInfo(shopDTO);
        String areaInfo = configService.getShopAreaInfoByShopDTO(shopDTO);
        if (StringUtils.isNotEmpty(areaInfo)) {
          shopRelationInviteDTO.setOriginAddress(areaInfo);
        }
        shopRelationInviteDTOs.add(shopRelationInviteDTO);
      }
    }
    return new PagingListResult<ShopRelationInviteDTO>(shopRelationInviteDTOs, true, pager);
  }

  @Override
  public List<ShopRelationInviteDTO> getPendingShopRelationInviteDTOs(Long shopId, InviteType inviteType) throws Exception {
    ConfigWriter writer = configDaoManager.getWriter();
    List<ShopRelationInvite> shopRelationInvites = writer.getPendingShopRelationInvites(shopId,inviteType);

    List<ShopRelationInviteDTO> shopRelationInviteDTOs = new ArrayList<ShopRelationInviteDTO>();
    if (CollectionUtil.isNotEmpty(shopRelationInvites)) {
      for (ShopRelationInvite shopRelationInvite : shopRelationInvites) {
        shopRelationInviteDTOs.add(shopRelationInvite.toDTO());
      }
    }
    return shopRelationInviteDTOs;
  }


  private ObjectTypes getObjectType(InviteType inviteType) {
    ObjectTypes objectType = null;
    if (inviteType != null) {
      switch (inviteType) {
        case SUPPLIER_INVITE:
          objectType = ObjectTypes.APPLY_CUSTOMER;
          break;
        case CUSTOMER_INVITE:
          objectType = ObjectTypes.APPLY_SUPPLIER;
          break;
      }
    }
    return objectType;
  }

  @Override
  public boolean cancelShopRelation(Long customerShopId, Long supplierShopId, Long operateShopId,
                                    Long userId, String cancelMsg) throws Exception {
    boolean isCancelSuccess = false;
    if (customerShopId == null || supplierShopId == null) {
      return isCancelSuccess;
    }
    ConfigWriter writer = configDaoManager.getWriter();
    Object status = writer.begin();
    try {
      WholesalerShopRelation wholesalerShopRelation = CollectionUtil.uniqueResult(
          writer.getWholesalerShopRelationByWholesalerShopIds(customerShopId,null, supplierShopId));
      if (wholesalerShopRelation != null && ShopRelationStatus.ENABLED.equals(wholesalerShopRelation.getStatus())) {
        wholesalerShopRelation.setStatus(ShopRelationStatus.DISABLED);
        wholesalerShopRelation.setOperationShopId(operateShopId);
        wholesalerShopRelation.setOperationManId(userId);
        wholesalerShopRelation.setCancelMsg(cancelMsg);
        writer.update(wholesalerShopRelation);
        Shop supplierShop = writer.getById(Shop.class, supplierShopId);
        if (supplierShop != null) {
          int relationCustomerAmount = NumberUtil.intValue(supplierShop.getRelativeCustomerAmount());
          relationCustomerAmount--;
          relationCustomerAmount = relationCustomerAmount > 0 ? relationCustomerAmount : 0;
          supplierShop.setRelativeCustomerAmount(relationCustomerAmount);
          writer.update(supplierShop);
        }
        writer.commit(status);
        isCancelSuccess = true;
        getOperationLogService().saveOperationLog(new OperationLogDTO(operateShopId, userId,
            wholesalerShopRelation.getId(), ObjectTypes.CANCEL_SHOP_RELATION, OperationTypes.DELETE));
      } else {
        isCancelSuccess = false;
      }
      return isCancelSuccess;
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public void customerShopDeleteSupplierUpdateRelation(SupplierDTO supplierDTO, Long userId) throws Exception {
    if (supplierDTO == null || supplierDTO.getShopId() == null || supplierDTO.getSupplierShopId() == null) {
      return;
    }
    ConfigWriter writer = configDaoManager.getWriter();
    Object status = writer.begin();
    try {
      WholesalerShopRelation wholesalerShopRelation = CollectionUtil.uniqueResult(
          writer.getWholesalerShopRelationByWholesalerShopIds(supplierDTO.getShopId(), RelationTypes.CUSTOMER_RELATE_TO_WHOLESALER_LIST,
              supplierDTO.getSupplierShopId()));
      if (wholesalerShopRelation != null && ShopRelationStatus.ENABLED.equals(wholesalerShopRelation.getStatus())) {
        if (RelationTypes.RELATED.equals(wholesalerShopRelation.getRelationType())) {
          wholesalerShopRelation.setRelationType(RelationTypes.SUPPLIER_COLLECTION);
        } else {
          wholesalerShopRelation.setStatus(ShopRelationStatus.DISABLED);
        }
        wholesalerShopRelation.setOperationShopId(supplierDTO.getShopId());
        wholesalerShopRelation.setOperationManId(userId);
        writer.update(wholesalerShopRelation);
        writer.commit(status);
        getOperationLogService().saveOperationLog(new OperationLogDTO(supplierDTO.getShopId(), userId,
            wholesalerShopRelation.getId(), ObjectTypes.CUSTOMER_CANCEL_WHOLESALER_SHOP_RELATION, OperationTypes.DELETE));
      }
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public void wholesalerShopDeleteCustomerUpdateRelation(CustomerDTO customerDTO,Long userId) throws Exception{
    if (customerDTO == null || customerDTO.getShopId() == null || customerDTO.getCustomerShopId() == null) {
      return;
    }
    ConfigWriter writer = configDaoManager.getWriter();
    Object status = writer.begin();
    try {
      WholesalerShopRelation wholesalerShopRelation = CollectionUtil.uniqueResult(
          writer.getWholesalerShopRelationByWholesalerShopIds(customerDTO.getCustomerShopId(), RelationTypes.WHOLESALER_RELATE_TO_CUSTOMER_LIST,
              customerDTO.getShopId()));
      if (wholesalerShopRelation != null && ShopRelationStatus.ENABLED.equals(wholesalerShopRelation.getStatus())) {
        if (RelationTypes.RELATED.equals(wholesalerShopRelation.getRelationType())) {
          wholesalerShopRelation.setRelationType(RelationTypes.CUSTOMER_COLLECTION);
        } else {
          wholesalerShopRelation.setStatus(ShopRelationStatus.DISABLED);
        }
        wholesalerShopRelation.setOperationShopId(customerDTO.getShopId());
        wholesalerShopRelation.setOperationManId(userId);
        writer.update(wholesalerShopRelation);
        writer.commit(status);
        getOperationLogService().saveOperationLog(new OperationLogDTO(customerDTO.getShopId(), userId,
            wholesalerShopRelation.getId(), ObjectTypes.WHOLESALER_CANCEL_CUSTOMER_SHOP_RELATION, OperationTypes.DELETE));
      }
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public ShopDTO getShopAreaDTOInfo(Long shopId) {
    if (shopId == null) {
      return null;
    }
    ShopDTO shopDTO = getConfigService().getShopById(shopId);
    if (shopDTO != null && shopDTO.getAreaId() != null) {
      List<Area> areaList = getConfigService().getArea(shopDTO.getAreaId());
      if (CollectionUtil.isNotEmpty(areaList)) {
        Map<Long, Area> areaMap = new HashMap<Long, Area>();
        for (Area area : areaList) {
          if (area.getNo() == null || area.getParentNo() == null) {
            continue;
          }
          areaMap.put(area.getNo(), area);
        }
        while (!areaMap.isEmpty()) {
          Area area = areaMap.get(shopDTO.getAreaId());
          while (area != null) {
            Area lastArea = area;
            area = areaMap.get(area.getParentNo());
            if (area == null) {
              if (shopDTO.getProvince() == null) {
                shopDTO.setProvince(lastArea.getNo());
              } else if (shopDTO.getCity() == null) {
                shopDTO.setCity(lastArea.getNo());
              } else if (shopDTO.getRegion() == null) {
                shopDTO.setRegion(lastArea.getNo());
              }
              areaMap.remove(lastArea.getNo());
            } else {
              lastArea = area;
            }
          }
        }
      }
    }
    return shopDTO;
  }

  @Override
  public void updateShopRelationInvite(ShopRelationInviteDTO shopRelationInviteDTO) throws Exception {
    if (shopRelationInviteDTO == null || shopRelationInviteDTO.getId() == null) {
      return;
    }
    ConfigWriter writer = configDaoManager.getWriter();
    Object status = writer.begin();
    try {
      ShopRelationInvite shopRelationInvite = writer.getById(ShopRelationInvite.class, shopRelationInviteDTO.getId());
      shopRelationInvite.setCustomerId(shopRelationInviteDTO.getCustomerId());
      shopRelationInvite.setSupplierId(shopRelationInviteDTO.getSupplierId());
      writer.update(shopRelationInvite);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }


  @Override
  public List<ShopDTO> getSupplierOrCustomerShopSuggestion(Long shopId, String searchWord, boolean isTesShop,String customerOrSupplier,String shopRange) {
    List<ShopDTO> shopDTOList = new ArrayList<ShopDTO>();
    String shopVersionIdStr = null;
    if("supplierOnline".equals(customerOrSupplier)){
      //系统推荐给批发商的客户店铺版本ID
      shopVersionIdStr = getConfigService().getConfig("ShopVersionRecommendedToCustomers", ShopConstant.BC_SHOP_ID);
    }else if("customerOnline".equals(customerOrSupplier)){
      //系统推荐给客户的批发商店铺版本ID
      shopVersionIdStr = getConfigService().getConfig("ShopVersionRecommendedToWholesalers", ShopConstant.BC_SHOP_ID);
    }
    if(StringUtils.isNotBlank(shopVersionIdStr)){
      List<Shop> shopList = configDaoManager.getWriter().getSupplierOrCustomerShopSuggestion(shopId, searchWord, shopVersionIdStr, isTesShop,customerOrSupplier,shopRange);
      if (CollectionUtils.isNotEmpty(shopList)) {
        for (Shop shop : shopList) {
          shopDTOList.add(shop.toDTO());
        }
      }
    }

    return shopDTOList;
  }

  //自动匹配
  public List<CustomerRelatedShopDTO> getRelatedShopByCustomerMobile(Long shopId, ShopKind shopKind) {
    ConfigWriter writer = configDaoManager.getWriter();
    List<CustomerRelatedShopDTO> customerRelatedShopDTOList = writer.getRelatedShopByCustomerMobile(shopId, ConfigUtils.getCancelRecommendAssociatedCountLimit(), shopKind);
    if (CollectionUtils.isNotEmpty(customerRelatedShopDTOList)) {
      Set<Long> matchedCustomerShopIds = new HashSet<Long>();
      Set<Long> excludeCustomerShopIds = new HashSet<Long>();
      for (CustomerRelatedShopDTO dto : customerRelatedShopDTOList) {
        matchedCustomerShopIds.add(dto.getRelatedShopId());
      }
      //排除已经申请关联的
      if (CollectionUtil.isNotEmpty(matchedCustomerShopIds)) {
        List<ShopRelationInvite> shopRelationInviteList = writer.getShopRelationInviteInShopIds(matchedCustomerShopIds, shopId,new HashSet<InviteStatus>(){{add(InviteStatus.PENDING);}});
        for (ShopRelationInvite invite : shopRelationInviteList) {
          excludeCustomerShopIds.add(invite.getOriginShopId());
          excludeCustomerShopIds.add(invite.getInvitedShopId());
        }
      }
      //排除已经关联的客户店铺
      Set<Long> customerShopIds = writer.getRelatedCustomerShopIds(shopId, RelationTypes.WHOLESALER_RELATE_TO_CUSTOMER_LIST);
      if (CollectionUtils.isNotEmpty(customerShopIds)) {
        excludeCustomerShopIds.addAll(customerShopIds);
      }
      Iterator<CustomerRelatedShopDTO> iterator = customerRelatedShopDTOList.iterator();
      while (iterator.hasNext()) {
        CustomerRelatedShopDTO customerRelatedShopDTO = iterator.next();
        if (excludeCustomerShopIds.contains(customerRelatedShopDTO.getRelatedShopId())) {
          iterator.remove();
        }
      }
    }

    return customerRelatedShopDTOList;
  }

  @Override
  public List<SupplierRelatedShopDTO> getRelatedShopBySupplierMobile(Long shopId, ShopKind shopKind) {
    ConfigWriter writer = configDaoManager.getWriter();
    List<SupplierRelatedShopDTO> supplierRelatedShopDTOList = writer.getRelatedShopBySupplierMobile(shopId, ConfigUtils.getCancelRecommendAssociatedCountLimit(), shopKind);
    if (CollectionUtils.isNotEmpty(supplierRelatedShopDTOList)) {
      Set<Long> matchedSupplierShopIds = new HashSet<Long>();
      Set<Long> excludeSupplierShopIds = new HashSet<Long>();
      for (SupplierRelatedShopDTO dto : supplierRelatedShopDTOList) {
        matchedSupplierShopIds.add(dto.getRelatedShopId());
      }
      if (CollectionUtil.isNotEmpty(matchedSupplierShopIds)) {
        List<ShopRelationInvite> shopRelationInviteList = writer.getShopRelationInviteInShopIds(matchedSupplierShopIds, shopId,new HashSet<InviteStatus>(){{add(InviteStatus.PENDING);}});
        for (ShopRelationInvite invite : shopRelationInviteList) {
          excludeSupplierShopIds.add(invite.getOriginShopId());
          excludeSupplierShopIds.add(invite.getInvitedShopId());
        }
      }
      Set<Long> wholesalerShopIds = writer.getRelationWholesalerShopIds(shopId, RelationTypes.CUSTOMER_RELATE_TO_WHOLESALER_LIST);
      if (CollectionUtils.isNotEmpty(wholesalerShopIds)) {
        excludeSupplierShopIds.addAll(wholesalerShopIds);
      }
      Iterator<SupplierRelatedShopDTO> iterator = supplierRelatedShopDTOList.iterator();
      while (iterator.hasNext()) {
        SupplierRelatedShopDTO supplierRelatedShopDTO = iterator.next();
        if (excludeSupplierShopIds.contains(supplierRelatedShopDTO.getRelatedShopId())) {
          iterator.remove();
        }
      }
    }
    return supplierRelatedShopDTOList;
  }

  @Override
  public List<Long> deleteOtherSupplierShopRelationInviteByInvitedShopContactMobile(Long customerShopId, Long supplierShopId, List<String> supplierMobiles) throws Exception {
    if(CollectionUtil.isEmpty(supplierMobiles))return new ArrayList<Long>();
    ConfigWriter writer = configDaoManager.getWriter();
    Object status = writer.begin();
    List<Long> result = new ArrayList<Long>();
    try {
      List<Long> shopIdList = writer.getShopIdByContactMobile(customerShopId, supplierShopId, supplierMobiles);
      if (CollectionUtil.isEmpty(shopIdList)) return result;
      List<ShopRelationInvite> shopRelationInviteList = writer.getShopRelationInviteInShopIds(new HashSet<Long>(shopIdList), customerShopId,null);
      for (ShopRelationInvite invite : shopRelationInviteList) {
        if (invite.getStatus() == InviteStatus.PENDING || invite.getStatus() == InviteStatus.OPPOSITES_PENDING) {
          invite.setStatus(InviteStatus.DELETED);
          writer.update(invite);
          result.add(invite.getId());
        }
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
    return result;
  }

  /**
   *
   * @return ShopRelationInvite id
   */
  @Override
  public List<Long> deleteOtherCustomerShopRelationInviteByInvitedShopContactMobile(Long customerShopId, Long supplierShopId, List<String> customerMobiles) throws Exception {
    if(CollectionUtil.isEmpty(customerMobiles))return new ArrayList<Long>();
    ConfigWriter writer = configDaoManager.getWriter();
    Object status = writer.begin();
    List<Long> result = new ArrayList<Long>();
    try {
      List<Long> shopId = writer.getShopIdByContactMobile(customerShopId, supplierShopId, customerMobiles);
      if (CollectionUtil.isEmpty(shopId)) return result;
      List<ShopRelationInvite> shopRelationInviteList = writer.getShopRelationInviteInShopIds(new HashSet<Long>(shopId), supplierShopId,null);
      for (ShopRelationInvite invite : shopRelationInviteList) {
        if (invite.getStatus() == InviteStatus.PENDING || invite.getStatus() == InviteStatus.OPPOSITES_PENDING) {
          invite.setStatus(InviteStatus.DELETED);
          writer.update(invite);
          result.add(invite.getId());
        }
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
    return result;
  }
    //创建或者更新关联关系
  @Override
  public void collectSupplierShopUpdateRelation(Long customerShopId, Long wholesalerShopId) throws Exception {
    if(customerShopId == null || wholesalerShopId == null ){
      LOG.error("collectSupplierShop shopDTO is null ,customerShopId:{},wholesalerShopId:{}",customerShopId,wholesalerShopId);
      return;
    }
    ConfigWriter writer = configDaoManager.getWriter();
    Object status = writer.begin();
    try {
      WholesalerShopRelation wholesalerShopRelation = CollectionUtil.uniqueResult(
          writer.getWholesalerShopRelationByCustomerShopIds(wholesalerShopId, RelationTypes.WHOLESALER_RELATE_TO_CUSTOMER_LIST, customerShopId));
      if (wholesalerShopRelation == null || ShopRelationStatus.DISABLED.equals(wholesalerShopRelation.getStatus())) {
        wholesalerShopRelation = new WholesalerShopRelation(customerShopId, wholesalerShopId, ShopRelationStatus.ENABLED,
            RelationTypes.CUSTOMER_COLLECTION);
        writer.save(wholesalerShopRelation);
      } else if (wholesalerShopRelation != null && RelationTypes.SUPPLIER_COLLECTION.equals(wholesalerShopRelation.getRelationType())) {
        wholesalerShopRelation.setRelationType(RelationTypes.RELATED);
        writer.update(wholesalerShopRelation);
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

     //创建或者更新关联关系 供应商收藏客户关联关系
  @Override
  public void collectCustomerShopUpdateRelation(Long customerShopId, Long wholesalerShopId) throws Exception {
    if (customerShopId == null || wholesalerShopId == null) {
      LOG.error("collectSupplierShop shopDTO is null ,customerShopId:{},wholesalerShopId:{}", customerShopId, wholesalerShopId);
      return;
    }
    ConfigWriter writer = configDaoManager.getWriter();
    Object status = writer.begin();
    try {
      WholesalerShopRelation wholesalerShopRelation = CollectionUtil.uniqueResult(
          writer.getWholesalerShopRelationByCustomerShopIds(wholesalerShopId, RelationTypes.CUSTOMER_RELATE_TO_WHOLESALER_LIST, customerShopId));
      if (wholesalerShopRelation == null || ShopRelationStatus.DISABLED.equals(wholesalerShopRelation.getStatus())) {
        wholesalerShopRelation = new WholesalerShopRelation(customerShopId, wholesalerShopId, ShopRelationStatus.ENABLED,
            RelationTypes.SUPPLIER_COLLECTION);
        writer.save(wholesalerShopRelation);
      } else if (wholesalerShopRelation != null && RelationTypes.CUSTOMER_COLLECTION.equals(wholesalerShopRelation.getRelationType())) {
        wholesalerShopRelation.setRelationType(RelationTypes.RELATED);
        writer.update(wholesalerShopRelation);
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }
}
