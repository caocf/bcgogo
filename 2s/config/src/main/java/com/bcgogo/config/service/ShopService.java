package com.bcgogo.config.service;

import com.bcgogo.baidu.model.geocoder.GeocoderResponse;
import com.bcgogo.baidu.service.IGeocodingService;
import com.bcgogo.common.AllListResult;
import com.bcgogo.common.ListResult;
import com.bcgogo.common.Result;
import com.bcgogo.config.ShopSearchCondition;
import com.bcgogo.config.ShopSearchResult;
import com.bcgogo.config.cache.AreaCacheManager;
import com.bcgogo.config.dto.*;
import com.bcgogo.config.dto.image.DataImageRelationDTO;
import com.bcgogo.config.dto.image.ImageInfoDTO;
import com.bcgogo.config.model.*;
import com.bcgogo.config.service.image.ImageService;
import com.bcgogo.config.upyun.UpYun;
import com.bcgogo.config.upyun.UpYunManager;
import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.enums.ExeStatus;
import com.bcgogo.enums.app.ServiceScope;
import com.bcgogo.enums.config.DataType;
import com.bcgogo.enums.config.ImageScene;
import com.bcgogo.enums.config.ImageType;
import com.bcgogo.enums.shop.*;
import com.bcgogo.enums.txn.finance.ChargeType;
import com.bcgogo.enums.txn.finance.PaymentStatus;
import com.bcgogo.enums.user.RoleType;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.exception.BcgogoExceptionType;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.dto.ContactDTO;
import com.bcgogo.utils.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 13-1-1
 * Time: 下午3:53
 */
@Component
public class ShopService implements IShopService {
  private static final Logger LOG = LoggerFactory.getLogger(ShopService.class);

  @Autowired
  private ConfigDaoManager configDaoManager;

  @Override
  public Map<Long, ShopDTO> getShopByShopIds(Long... shopIds) {
    ConfigWriter writer = configDaoManager.getWriter();
    Map<Long, ShopDTO> map = new HashMap<Long, ShopDTO>();
    if (ArrayUtil.isEmpty(shopIds)) return map;
    List<Shop> shopList = writer.getShopByShopId(shopIds);
    for (Shop shop : shopList) {
      map.put(shop.getId(), shop.toDTO());
    }
    return map;
  }

  @Override
  public ShopDTO createShop(ShopDTO shopDTO) throws BcgogoException, IOException {
    Long oldNo = null, newNo = null;
    if (shopDTO == null) throw new BcgogoException(BcgogoExceptionType.NullException);
    ConfigWriter writer = configDaoManager.getWriter();
    Object status = writer.begin();
    try {
      //shop name
      String shopName = shopDTO.getName();
      if (shopName == null)
        throw new BcgogoException(BcgogoExceptionType.InvalidShopName);
      shopName = shopName.trim();
      if (!shopName.matches(Shop.VALID_Shop_NAME_REGEX))
        throw new BcgogoException(BcgogoExceptionType.InvalidShopName);
      shopDTO.setName(shopName);
      Shop shop = new Shop(shopDTO);
      writer.save(shop);
      newNo = shop.getAreaId();
      //地理坐标
//      updateShopGeocode(shop, true, writer);

      shopDTO.setId(shop.getId());
      saveOrUpdateShopContacts(shopDTO, writer);

      //保存经营范围
      this.saveOrUpdateShopBusinessScope(writer, shop.getId(), shopDTO.getProductCategoryIds());

      //保存更新人
      this.maintainShopLog(writer, shop.getId(), shopDTO.getFollowId(), shopDTO.getShopStatus());
      writer.commit(status);
      return shopDTO;
    } finally {
      writer.rollback(status);
    }
  }

  //保存联系人信息
  private void saveOrUpdateShopContacts(ShopDTO shopDTO, ConfigWriter writer) {
    if (!ArrayUtils.isEmpty(shopDTO.getContacts())) {
      ContactDTO[] contactDTOs = shopDTO.getContacts();
      for (ContactDTO contactDTO : contactDTOs) {
        if (contactDTO == null) continue;
        contactDTO.setShopId(shopDTO.getId());
        contactDTO.setDisabled(1); // 默认有效
        //update
        if (contactDTO.getId() != null) {
          ShopContact shopContact = writer.getById(ShopContact.class, contactDTO.getId());
          if (StringUtils.isNotBlank(contactDTO.getName()) || StringUtils.isNotBlank(contactDTO.getMobile())) {
            shopContact.fromDTO(contactDTO);
            writer.update(shopContact);
          } else {
            shopContact.setDisabled(0);
            writer.update(shopContact);
          }
        }
        //save
        else {
          if (StringUtils.isNotBlank(contactDTO.getName()) || StringUtils.isNotBlank(contactDTO.getMobile())) {
            writer.saveOrUpdate(new ShopContact().fromDTO(contactDTO));
          }
        }
      }
    }
  }

  @Override
  public ShopDTO updateShop(ShopDTO shopDTO) throws BcgogoException, IOException {
    if (shopDTO == null) throw new BcgogoException(BcgogoExceptionType.NullException);
    ConfigWriter writer = configDaoManager.getWriter();
    Object status = writer.begin();
    try {
      Long id = shopDTO.getId();
      if (id == null) throw new BcgogoException(BcgogoExceptionType.ShopNotFound);
      Shop shop = writer.getById(Shop.class, id);
      if (shop == null) throw new BcgogoException(BcgogoExceptionType.ShopNotFound);
      String oldAddress = shop.getAddress();
      //shop name
      String shopName = shopDTO.getName();
      if (shopName == null)
        throw new BcgogoException(BcgogoExceptionType.InvalidShopName);
      shopName = shopName.trim();
      if (!shopName.matches(Shop.VALID_Shop_NAME_REGEX))
        throw new BcgogoException(BcgogoExceptionType.InvalidShopName);
      shopDTO.setLocateStatus(shop.getLocateStatus());
      shopDTO.setCoordinateLat(shop.getCoordinateLat());
      shopDTO.setCoordinateLon(shop.getCoordinateLon());
      shopDTO.setName(shopName);
      shopDTO.setTrialStartTime(shop.getTrialStartTime());
      shopDTO.setTrialEndTime(shop.getTrialEndTime());
      shopDTO.setUsingEndTime(shop.getUsingEndTime());
      shopDTO.setBargainStatus(shop.getBargainStatus());
      shopDTO.setBargainPrice(shop.getBargainPrice());
      shopDTO.setPaymentStatus(shop.getPaymentStatus());
      shopDTO.setAgent(shop.getAgent());
      shop.fromDTO(shopDTO);
      shop.setAreaId(shopDTO);
      writer.saveOrUpdate(shop);
//      updateShopGeocode(shop,!oldAddress.equals(shopDTO.getAddress()) , writer);
      saveOrUpdateShopContacts(shopDTO, writer);

      //保存经营范围
      this.saveOrUpdateShopBusinessScope(writer, shop.getId(), shopDTO.getProductCategoryIds());

      //保存更新人
      this.maintainShopLog(writer, shop.getId(), shopDTO.getFollowId(), shop.getShopStatus());

      writer.commit(status);
      return shopDTO;
    } finally {
      writer.rollback(status);
    }

  }

  @Override
  public boolean updateShopGeocode(Long shopId) {
    ConfigWriter writer = configDaoManager.getWriter();
    Object status = writer.begin();
    try {
      Shop shop = writer.getById(Shop.class, shopId);
      updateShopGeocode(shop,true,writer);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
    return true;
  }

  //更新地理坐标
  private void updateShopGeocode(Shop shop, boolean isAddressChanged, ConfigWriter writer) {
//    IGeocodingService geocodingService = ServiceManager.getService(IGeocodingService.class);
//    if (StringUtils.isBlank(shop.getAddress())) return;
//    String city = null;
//    if (shop.getCity() != null) {
//      AreaDTO areaDTO = AreaCacheManager.getAreaDTOByNo(shop.getCity());
//      city = (areaDTO != null && StringUtil.isNotEmpty(areaDTO.getName())) ? areaDTO.getName() : null;
//    }
//    GeocoderResponse geocoderResponse = geocodingService.addressToCoordinate(shop.getAddress(), city);
//    if (geocoderResponse.isSuccess()) {
//      Coordinate coordinate = geocoderResponse.getResult().getLocation();
//      shop.setCoordinateLat(coordinate.getLat());
//      shop.setCoordinateLon(coordinate.getLng());
//      writer.saveOrUpdate(shop);
//    }
    if(isAddressChanged){
      shop.setLocateStatus(LocateStatus.IN_ACTIVE);
      writer.saveOrUpdate(shop);
    }
  }

  @Override
  public ShopDTO registerShop(ShopDTO shopDTO) throws Exception {
    ConfigWriter writer = configDaoManager.getWriter();
    Object status = writer.begin();
    try {
      Shop shop = new Shop(shopDTO);
      writer.save(shop);

//      updateShopGeocode(shop, true, writer);
      shopDTO.setId(shop.getId());
      if (shopDTO.getInvitationCodeDTO() != null) {
        RegisterInfo registerInfo = new RegisterInfo();
        registerInfo.from(shopDTO);
        writer.save(registerInfo);
      }
      if (!ArrayUtil.isEmpty(shopDTO.getShopPhoto())) {
        Attachment shopAttachment = new Attachment();
        shopAttachment.setShopPhoto(shopDTO);
        writer.save(shopAttachment);
        shop.setShopPhotoId(shopAttachment.getId());
      }
      if (!ArrayUtil.isEmpty(shopDTO.getBusinessLicense())) {
        Attachment shopAttachment = new Attachment();
        shopAttachment.setShopBusinessLicensePhoto(shopDTO);
        writer.save(shopAttachment);
        shop.setBusinessLicenseId(shopAttachment.getId());
      }

      // add by zhuj 店铺联系人列表
      boolean hasContact = false;
      if (!ArrayUtils.isEmpty(shopDTO.getContacts())) {
        ContactDTO[] contactDTOs = shopDTO.getContacts();
        for (int i = 0; i < contactDTOs.length; i++) {
          if (contactDTOs[i] != null && contactDTOs[i].isValidContact()) { // 数组可以存储null
            hasContact = true;
            contactDTOs[i].setShopId(shopDTO.getId());
            contactDTOs[i].setDisabled(1); // 默认有效
            ShopContact shopContact = new ShopContact();
            shopContact.fromDTO(contactDTOs[i]);
            writer.save(shopContact);
            contactDTOs[i].setIsShopOwner(shopContact.getShopOwner());
            contactDTOs[i].setId(shopContact.getId());
          }
        }
      }
      if (shop.getShopPhotoId() != null || shop.getBusinessLicenseId() != null || hasContact) {
        writer.update(shop);
      }
      //保存更新人
      this.maintainShopLog(writer, shop.getId(), shopDTO.getFollowId(), shop.getShopStatus());

      writer.commit(status);
      shopDTO.setId(shop.getId());
      shopDTO.setShopPhotoId(shop.getShopPhotoId());
      shopDTO.setBusinessLicenseId(shop.getBusinessLicenseId());
      return shopDTO;
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public List<ShopDTO> getRecommendedShop() {
    ConfigWriter writer = configDaoManager.getWriter();
    List<ShopDTO> shopDTOList = new ArrayList<ShopDTO>();
    List<Shop> shopList = writer.getRecommendedShop();
    for (Shop shop : shopList) {
      shopDTOList.add(shop.toDTO());
    }
    return shopDTOList;
  }

  @Override
  public ShopSearchResult getShopByShopCondition(ShopSearchCondition condition) {
    ShopSearchResult result = new ShopSearchResult();
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    ConfigWriter writer = configDaoManager.getWriter();
    List<Area> areaList = null;
    Map<Long, Shop> shopMap = new HashMap<Long, Shop>();
//    if (StringUtils.isNotBlank(condition.getSalesman())) {
//      condition.setShopIds(writer.getShopRelationInviteInvitedShopIdsByOriginShopObscureName(condition.getSalesman()));
//    }
    List<ShopDTO> shops = writer.searchShopByCondition(condition);
//    List<Long> shopIds = new ArrayList<Long>();
//    if (StringUtils.isNotBlank(condition.getSalesman())) {
//      for (ShopDTO s : shops) {
//        if(RegisterType.isRegisterByShopSuggestion(s.getRegisterType()))shopIds.add(s.getId());
//      }
//      if (CollectionUtils.isNotEmpty(shopIds)) {
//        shopMap = writer.getShopRelationInviteOriginShopByInvitedShopIds(shopIds.toArray(new Long[shopIds.size()]));
//      }
//    }
//    Shop shopRelationInviteOriginShop;
    for (ShopDTO dto : shops) {
//      if (RegisterType.isRegisterByShopSuggestion(dto.getRegisterType())) {
//        shopRelationInviteOriginShop = shopMap.get(dto.getId());
//        if (shopRelationInviteOriginShop != null) {
//          dto.setShopRelationInviteOriginShopName(shopRelationInviteOriginShop.getName());
//          dto.setShopRelationInviteOriginShopId(shopRelationInviteOriginShop.getId());
//        }
//      }
      //区域
      if (dto.getAreaId() != null) {
        areaList = configService.getArea(dto.getAreaId());
        if (CollectionUtils.isNotEmpty(areaList)) {
          String areaName = "";
          int i = 0;
          for (Area a : areaList) {
            if (i == 0) {
              dto.setProvince(a.getNo());
            } else if (i == 1) {
              dto.setCity(a.getNo());
            } else {
              dto.setRegion(a.getNo());
            }
            i++;
            areaName += a.getName();
          }
          dto.setAreaName(areaName);
        }
      }
      // 经营范围
      dto.fromBusinessScopes();
      // 特色
      dto.fromFeatures();
      // 相关业务
      dto.fromRelatedBusinesses();
      // 经营方式
      dto.fromOperationModes();
      result.getResults().add(dto);
    }

    result.setTotals(writer.countShopByCondition(condition));
    return result;
  }

  public ShopDTO getShopAreaInfo(Long shopId) {
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    ShopDTO shopDTO = configService.getShopById(shopId);
    if (shopDTO == null) return null;
    List<Area> areaList = configService.getArea(shopDTO.getAreaId());
    if (com.bcgogo.utils.CollectionUtil.isNotEmpty(areaList)) {
      int count = 0;
      for (Area area : areaList) {
        switch (count) {
          case 0:
            shopDTO.setProvince(area.getNo());
          case 1:
            shopDTO.setCity(area.getNo());
          case 2:
            shopDTO.setRegion(area.getNo());
        }
        count++;
      }
    }
    return shopDTO;
  }

  public boolean checkShopName(String shopName, Long id) {
    ConfigWriter writer = configDaoManager.getWriter();
    List<Shop> shopList = writer.getShopByName(shopName);
    if (id == null) {
      return CollectionUtils.isNotEmpty(shopList);
    } else {
      return !shopList.isEmpty() && !shopList.get(0).getId().equals(id);
    }
  }

  @Override
  public void updateShopStatus(Long shopId, ShopStatus shopStatus) {
    ConfigWriter writer = configDaoManager.getWriter();
    Object status = writer.begin();
    try {
      Shop s = writer.getById(Shop.class, shopId);
      s.setShopStatus(shopStatus);
      writer.update(s);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public void updateShopState(Long shopId, ShopState shopState) {
    ConfigWriter writer = configDaoManager.getWriter();
    Object status = writer.begin();
    try {
      Shop s = writer.getById(Shop.class, shopId);
      s.setShopState(shopState);
      writer.update(s);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public void updateShopPaymentStatus(Long shopId, PaymentStatus paymentStatus, Long usingEndTime) {
    ConfigWriter writer = configDaoManager.getWriter();
    Object status = writer.begin();
    try {
      Shop s = writer.getById(Shop.class, shopId);
      if (PaymentStatus.FULL_PAYMENT == paymentStatus) {
        s.setUsingEndTime(null);
        s.setShopState(ShopState.ACTIVE);
        createShopOperationTask(writer, ShopOperateTaskScene.ENABLE_REGISTERED_PAID_SHOP, s.getId());
      } else {
        s.setUsingEndTime(usingEndTime);
        if (s.getUsingEndTime() != null && s.getUsingEndTime() < System.currentTimeMillis()) {
          s.setShopState(ShopState.ARREARS);
          createShopOperationTask(writer, ShopOperateTaskScene.DISABLE_REGISTERED_PAID_SHOP, s.getId());
        } else {
          s.setShopState(ShopState.ACTIVE);
          createShopOperationTask(writer, ShopOperateTaskScene.ENABLE_REGISTERED_PAID_SHOP, s.getId());
        }
      }
      s.setPaymentStatus(paymentStatus);
      s.setShopStatus(ShopStatus.REGISTERED_PAID);

      writer.update(s);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public void updateShopStatus(Long shopId, ShopStatus shopStatus, PaymentStatus paymentStatus) {
    ConfigWriter writer = configDaoManager.getWriter();
    Object status = writer.begin();
    try {
      Shop s = writer.getById(Shop.class, shopId);
      s.setPaymentStatus(paymentStatus);
      s.setShopStatus(shopStatus);
      writer.update(s);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public void createShopOperateHistory(ShopOperateHistoryDTO dto) throws BcgogoException {
    if (dto.getOperateShopId() == null) throw new BcgogoException("shop id is null");
    if (dto.getOperateType() == null) throw new BcgogoException("operate type is null");
    ConfigWriter writer = configDaoManager.getWriter();
    Object status = writer.begin();
    try {
      Shop s = writer.getById(Shop.class, dto.getOperateShopId());
      if (ShopOperateType.DISABLE_REGISTERED_PAID_SHOP == dto.getOperateType()) {
        s.setShopState(ShopState.IN_ACTIVE);
        createShopOperationTask(writer, ShopOperateTaskScene.DISABLE_REGISTERED_PAID_SHOP, s.getId());
      } else if (ShopOperateType.ENABLE_REGISTERED_PAID_SHOP == dto.getOperateType()) {
        s.setShopState(ShopState.ACTIVE);
        createShopOperationTask(writer, ShopOperateTaskScene.ENABLE_REGISTERED_PAID_SHOP, s.getId());
      } else if (ShopOperateType.UPDATE_REGISTERED_TRIAL_SHOP == dto.getOperateType()) {
        s.setShopStatus(ShopStatus.REGISTERED_PAID);
        //如果是缴费的店铺 自动充值短信
        IShopBalanceService shopBalanceService = ServiceManager.getService(IShopBalanceService.class);
        shopBalanceService.createSmsBalanceForRegister(s.getId(), writer);
      } else if (ShopOperateType.CONTINUE_TO_TRY == dto.getOperateType()) {
        if (dto.getTrialEndTime() == null) throw new BcgogoException("trialTime is null");
        s.setTrialEndTime(dto.getTrialEndTime());
        s.setShopState(ShopState.ACTIVE);
        createShopOperationTask(writer, ShopOperateTaskScene.ENABLE_REGISTERED_PAID_SHOP, s.getId());
      }
      writer.update(s);
      ShopOperateHistory history = new ShopOperateHistory();
      history.fromDTO(dto);
      writer.save(history);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public void checkTrialEndTimeShop() {
    ConfigWriter writer = configDaoManager.getWriter();
    List<Shop> shopList = writer.getShop(ShopStatus.REGISTERED_TRIAL, System.currentTimeMillis());
    Object status = writer.begin();
    try {
      for (Shop shop : shopList) {
        shop.setShopState(ShopState.OVERDUE);
        writer.update(shop);
        createShopOperationTask(writer, ShopOperateTaskScene.DISABLE_REGISTERED_PAID_SHOP, shop.getId());
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public ShopDTO checkTrialEndTimeShop(long shopId) {
    ConfigWriter writer = configDaoManager.getWriter();
    Shop shop = writer.getById(Shop.class, shopId);
    if (shop == null) {
      return null;
    }
    if (ShopStatus.REGISTERED_TRIAL == shop.getShopStatus() && shop.getTrialEndTime() != null && (shop.getTrialEndTime() - System.currentTimeMillis() < 0)) {
      Object status = writer.begin();
      try {
        shop.setShopState(ShopState.OVERDUE);
        writer.update(shop);
        createShopOperationTask(writer, ShopOperateTaskScene.DISABLE_REGISTERED_PAID_SHOP, shopId);
        writer.commit(status);
      } finally {
        writer.rollback(status);
      }
    }
    return shop.toDTO();
  }

  @Override
  public ShopOperateHistoryDTO getLatestShopOperateHistory(Long operateShopId, ShopOperateType type) throws BcgogoException {
    if (operateShopId == null) throw new BcgogoException("shop id is null");
    if (type == null) throw new BcgogoException("ShopOperateType is null");
    ConfigWriter writer = configDaoManager.getWriter();
    List<ShopOperateHistory> historyList = writer.getLatestShopOperateHistory(operateShopId, type);
    if (CollectionUtils.isNotEmpty(historyList)) return historyList.get(0).toDTO();
    return null;
  }

  @Override
  public AllListResult<ShopOperateHistoryDTO> getLatestShopOperateHistoryList(Long operateShopId) throws BcgogoException {
    if (operateShopId == null) throw new BcgogoException("shop id is null");
    ConfigWriter writer = configDaoManager.getWriter();
    AllListResult<ShopOperateHistoryDTO> allListResult = new AllListResult<ShopOperateHistoryDTO>();
    List<ShopOperateHistoryDTO> result = new ArrayList<ShopOperateHistoryDTO>();
    List<ShopOperateHistory> historyList = writer.getLatestShopOperateHistory(operateShopId, null);
    for (ShopOperateHistory history : historyList) {
      result.add(history.toDTO());
    }
    allListResult.setSuccess(true);
    allListResult.setResults(result);
    allListResult.setTotalRows(allListResult.getResults().size());
    return allListResult;
  }

  @Override
  public void createShopOperationTask(ConfigWriter writer, ShopOperateTaskScene scene, Long... shopIds) {
    if (scene == null || ArrayUtils.isEmpty(shopIds)) {
      return;
    }
    for (Long shopId : shopIds) {
      ShopOperationTask shopOperationTask = new ShopOperationTask(shopId, scene, ExeStatus.READY);
      writer.save(shopOperationTask);
    }
  }

  /**
   * 保存更新 营业范围 需要开事物
   *
   * @param productCategoryIdSet 商品分类 id
   */
  private void saveOrUpdateShopBusinessScope(ConfigWriter writer, Long shopId, Set<Long> productCategoryIdSet) {
    Set<Long> shopIdSet = new HashSet<Long>();
    shopIdSet.add(shopId);
    List<ShopBusinessScope> shopBusinessScopes = writer.getShopBusinessScopeByShopId(shopIdSet);
    if (CollectionUtils.isEmpty(productCategoryIdSet) || shopId == null) {
      return;
    }
    for (ShopBusinessScope scope : shopBusinessScopes) {
      if (productCategoryIdSet.contains(scope.getId())) {
        productCategoryIdSet.remove(scope.getId());
      } else {
        writer.delete(scope);
      }
    }
    if (CollectionUtils.isEmpty(productCategoryIdSet)) {
      return;
    }
    for (Long id : productCategoryIdSet) {
      writer.save(new ShopBusinessScope(shopId, id));
    }
  }

  /**
   * 保存更新 营业范围 需要开事物
   * 跟进人变更 log
   *
   * @param writer ConfigWriter
   * @param shopId 跟进店铺
   * @param userId 跟进人
   */
  private void maintainShopLog(ConfigWriter writer, Long shopId, Long userId, ShopStatus shopStatus) {
    if (userId == null) return;
    List<MaintainShopLog> maintainShopLogs = writer.getMaintainShopLog(shopId);
    if (CollectionUtils.isEmpty(maintainShopLogs)
        || (CollectionUtils.isNotEmpty(maintainShopLogs) && !maintainShopLogs.get(0).getUserId().equals(userId))) {
      writer.save(new MaintainShopLog(shopId, userId, shopStatus));
    }
  }

  @Override
  public ShopOperationTaskDTO getFirstReadyShopOperationTaskDTO() {
    ShopOperationTask shopOperationTask = configDaoManager.getWriter().getFirstReadyShopOperationTask();
    if (shopOperationTask != null) {
      return shopOperationTask.toDTO();
    } else {
      return null;
    }
  }

  @Override
  public void updateShopOperationTaskDTO(ShopOperationTaskDTO taskDTO) {
    if (taskDTO == null || taskDTO.getId() == null) {
      return;
    }
    ConfigWriter writer = configDaoManager.getWriter();
    Object status = writer.begin();
    try {
      ShopOperationTask shopOperationTask = writer.getById(ShopOperationTask.class, taskDTO.getId());
      if (shopOperationTask != null) {
        shopOperationTask.fromDTO(taskDTO);
        writer.update(shopOperationTask);
        writer.commit(status);
      }
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public ListResult<ShopDTO> getShopSuggestionByName(String shopName, ShopStatus... shopStatuses) throws BcgogoException {
    ConfigWriter writer = configDaoManager.getWriter();
    List<ShopDTO> shopDTOs = writer.getShopByObscureName(shopName, shopStatuses);
    return new AllListResult<ShopDTO>(shopDTOs, true, shopDTOs.size());
  }

  @Override
  public ShopDTO getActiveUsingShopByName(String shopName) {
    ConfigWriter writer = configDaoManager.getWriter();
    List<Shop> shopList = writer.getActiveUsingShopByName(shopName);
    if (shopList.isEmpty()) return null;
    return shopList.get(0).toDTO();
  }

  @Override
  public List<Long> getShopIdByShopCondition(ShopSearchCondition shopSearchCondition) {
    ConfigWriter writer = configDaoManager.getWriter();
    return writer.getShopIdByShopCondition(shopSearchCondition);
  }

  @Override
  public void maintainShopLog(long shopId, long userId, String userName) {
    ConfigWriter writer = configDaoManager.getWriter();
    Object status = writer.begin();
    try {
      Shop shop = writer.getById(Shop.class, shopId);
      if (shop == null) {
        return;
      }
      shop.setFollowId(userId);
      shop.setFollowName(userName);
      writer.update(shop);
      maintainShopLog(writer, shopId, userId, shop.getShopStatus());
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  /**
   * 如果店铺使用截止时间小于当前系统时间 把店铺状态改为欠费状态
   */
  @Override
  public void checkTrialDebtShop() {
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    List<Shop> shopList = configService.getShop();

    ConfigWriter writer = configDaoManager.getWriter();
    Object status = writer.begin();
    try {
      for (Shop shop : shopList) {
        if (shop.getShopStatus() != ShopStatus.REGISTERED_PAID) {
          continue;
        }
        if ((shop.getUsingEndTime() == null || shop.getUsingEndTime() > System.currentTimeMillis())) {
          continue;
        }
        shop.setShopState(ShopState.ARREARS);
        writer.update(shop);
        createShopOperationTask(writer, ShopOperateTaskScene.ARREARS_REGISTERED_PAID_SHOP, shop.getId());
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public boolean initShopServiceScope() {
    ConfigWriter writer = configDaoManager.getWriter();
    List<Shop> shopList = writer.getInitShopServiceScope();
    Object status = writer.begin();
    try {
      IServiceCategoryService serviceCategoryService = ServiceManager.getService(IServiceCategoryService.class);
      List<Long> wash = serviceCategoryService.getServiceCategoryChildrenIdsByParentServiceScope(ServiceScope.WASH);
      List<Long> overhaulAndMaintenance = ServiceManager.getService(IServiceCategoryService.class)
          .getServiceCategoryChildrenIdsByParentServiceScope(ServiceScope.OVERHAUL_AND_MAINTENANCE);
      List<Long> decorationBeauty = serviceCategoryService.getServiceCategoryChildrenIdsByParentServiceScope(ServiceScope.DECORATION_BEAUTY);
      List<Long> painting = serviceCategoryService.getServiceCategoryChildrenIdsByParentServiceScope(ServiceScope.PAINTING);
      List<Long> insurance = serviceCategoryService.getServiceCategoryChildrenIdsByParentServiceScope(ServiceScope.INSURANCE);
      Set<Long> ids = new HashSet<Long>();
      for (Shop shop : shopList) {
        ids.clear();
        if (shop == null || shop.getShopVersionId() == null) continue;
        if(shop.getShopVersionId().longValue() == 10000010017431643L){
          continue;
        }
        switch (RoleType.shopVersionBaseRoleMapping(shop.getShopVersionId())) {
          case WASH_SHOP_BASE:
            ids.addAll(wash);
            ids.addAll(decorationBeauty);
            ids.addAll(insurance);
            break;
          case INTEGRATED_SHOP_BASE:
          case REPAIR_SHOP_BASE:
          case ADVANCED_SHOP_BASE:
            ids.addAll(wash);
            ids.addAll(overhaulAndMaintenance);
            ids.addAll(decorationBeauty);
            ids.addAll(painting);
            ids.addAll(insurance);
            break;
          default:
            break;
        }
        for (Long id : ids) {
          writer.save(new ShopServiceCategory(id, shop.getId()));
        }
      }
      writer.commit(status);
      return true;
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public Result shopRegisteredStatistics(ShopStatus shopStatus) throws BcgogoException {
    Result result = new Result(true);
    if (shopStatus == null) {
      result.setSuccess(false);
      return result;
    }
    ConfigWriter writer = configDaoManager.getWriter();
    Long inActiveNumber = writer.countShopByShopStatusAndShopStatues(new ShopState[]{ShopState.IN_ACTIVE, ShopState.ARREARS}, shopStatus);
    Long total = writer.countShopByShopStatusAndShopStatues(null, shopStatus);
    if (shopStatus == ShopStatus.REGISTERED_TRIAL) {
      result.setData("共有试用客户 " + total + " 个,其中（禁用账户 " + inActiveNumber + " 个）");
    } else if (shopStatus == ShopStatus.REGISTERED_PAID) {
      result.setData("共有正式客户" + total + "个,其中（禁用账户 " + inActiveNumber + " 个）");
    }
    return result;
  }

  @Override
  public ShopDTO activateShop(Long shopId, long shopVersionId, ChargeType chargeType) throws BcgogoException {
    ConfigWriter writer = configDaoManager.getWriter();
    Object status = writer.begin();
    try {
      if (shopId == null) throw new BcgogoException("shop id is null");
      Shop shop = writer.getById(Shop.class, shopId);
      shop.setShopStatus(ShopStatus.REGISTERED_TRIAL);
      shop.setPaymentStatus(PaymentStatus.NON_PAYMENT);
      shop.setTrialStartTime(System.currentTimeMillis());
      if(ChargeType.ONE_TIME.equals(chargeType)) {
        shop.setTrialEndTime(getProbationaryPeriod());
      } else if(ChargeType.YEARLY.equals(chargeType)) {
        shop.setTrialEndTime(getProbationaryPeriodYearly());
      }
      shop.setRegistrationDate(System.currentTimeMillis());
      shop.setBargainStatus(BargainStatus.NO_BARGAIN);
      shop.setShopState(ShopState.ACTIVE);
      shop.setShopVersionId(shopVersionId);
      shop.setChargeType(chargeType);
      writer.save(shop);
      writer.commit(status);
      return shop.toDTO();
    } finally {
      writer.rollback(status);
    }
  }

  //使用截止日期
  private Long getProbationaryPeriod() {
    String config = ServiceManager.getService(IConfigService.class).getConfig("SHOP_PROBATIONARY_PERIOD", ShopConstant.BC_SHOP_ID);
    Integer day = 30;
    if (StringUtil.isEmpty(config)) {
      LOG.warn("SHOP_PROBATIONARY_PERIOD config is empty!");
    } else {
      if (RegexUtils.isDigital(config)) {
        day = NumberUtil.intValue(config);
      } else {
        LOG.warn("SHOP_PROBATIONARY_PERIOD config is illegal!");
      }
    }
    return System.currentTimeMillis() + (1000 * 60 * 60 * 24L * day);
  }

  //按年收费，使用截止日期
  private Long getProbationaryPeriodYearly() {
    String config = ServiceManager.getService(IConfigService.class).getConfig("SHOP_PROBATIONARY_PERIOD_YEARLY", ShopConstant.BC_SHOP_ID);
    Integer day = 365;
    if (StringUtil.isEmpty(config)) {
      LOG.warn("SHOP_PROBATIONARY_PERIOD_YEARLY config is empty!");
    } else {
      if (RegexUtils.isDigital(config)) {
        day = NumberUtil.intValue(config);
      } else {
        LOG.warn("SHOP_PROBATIONARY_PERIOD_YEARLY config is illegal!");
      }
    }
    return System.currentTimeMillis() + (1000 * 60 * 60 * 24L * day);
  }

  @Override
  public List<Long> getAllShopIds() {
    ConfigWriter writer = configDaoManager.getWriter();
    return writer.getAllShopIds();
  }

  @Override
  public List<Long> getActiveShopIds() {
    ConfigWriter writer = configDaoManager.getWriter();
    return writer.getActiveShopIds();
  }

  @Override
  public List<ContactDTO> getShopContactsByShopId(Long shopId) {
    ConfigWriter writer = configDaoManager.getWriter();
    List<ContactDTO> contactDTOList = new ArrayList<ContactDTO>();
    List<ShopContact> shopContacts = writer.getShopContactsByShopId(shopId);
    for (ShopContact entity : shopContacts) {
      contactDTOList.add(entity.toDTO());
    }
    return contactDTOList;
  }

  @Override
  public List<Long> getBusinessScopeIdsByShopId(Long shopId) {
    return configDaoManager.getWriter().getBusinessScopeIdsByShopId(shopId);
  }

  public Shop getShopById(Long shopId) {
    return configDaoManager.getWriter().getById(Shop.class, shopId);
  }

  public ShopDTO getShopDTOById(Long shopId) {
    Shop shop = configDaoManager.getWriter().getById(Shop.class, shopId);
    if (shop == null) {
      return null;
    }
    return shop.toDTO();
  }

  public Result saveShopInfo(Result result, Long shopId, ShopDTO dto) {
    ConfigWriter writer = configDaoManager.getWriter();
    Object status = writer.begin();
    try {
      Shop shop = getShopById(shopId);
      if (shop == null || ShopState.DELETED.equals(shop.getShopState())) {
        return result.LogErrorMsg("店铺信息不存在，更新失败。");
      }
      if (dto.getCity() == null) {
        return result.LogErrorMsg("请选择城市。");
      }
      shop.setUrl(dto.getUrl());
      String oldAddress = shop.getAddress();
      shop.setAddress(dto.getAddress());
      shop.setLicencePlate(dto.getLicencePlate());
      shop.setOperationMode(dto.getOperationMode());
      shop.setProvince(dto.getProvince());
      shop.setCity(dto.getCity());
      shop.setLandline(dto.getLandline());
      shop.setRegion(dto.getRegion());
      shop.setAreaId(dto);
      shop.setMemo(dto.getMemo());
      shop.setAccidentMobile(dto.getAccidentMobile());
      writer.update(shop);
//      updateShopGeocode(shop,!oldAddress.equals(dto.getAddress()) , writer);
      if (!ConfigUtils.isWholesalerVersion(shop.getShopVersionId())) {
        handleShopServiceCategory(dto);
      }
      writer.commit(status);
      return result;
    } finally {
      writer.rollback(status);
    }


  }


  public Result saveShopContacts(Result result, Long shopId, ShopDTO dto) {
    ConfigWriter writer = configDaoManager.getWriter();
    List<ShopContact> shopContacts = writer.getShopContactsByShopId(shopId);
    Object status = writer.begin();
    try {
      Shop shop = getShopById(shopId);
      if (shop == null || ShopState.DELETED.equals(shop.getShopState())) {
        return result.LogErrorMsg("店铺信息不存在，更新失败。");
      }
      if (CollectionUtil.isNotEmpty(shopContacts)) {
        for (ShopContact contact : shopContacts) {
          contact.setDisabled(0);
          writer.update(contact);
        }
      }
      ContactDTO[] contactDTOs = dto.getContacts();
      if (!ArrayUtils.isEmpty(contactDTOs)) {
        for (int i = 0; i < contactDTOs.length; i++) {
          if (contactDTOs[i] != null && contactDTOs[i].isValidContact()) {
            contactDTOs[i].setShopId(shopId);
            contactDTOs[i].setDisabled(1); // 默认有效
            ShopContact shopContact = new ShopContact();
            shopContact.fromDTO(contactDTOs[i]);
            writer.save(shopContact);
          }
        }
      }
      writer.commit(status);
      return result;
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public void saveOrUpdateShopRQImage(ShopDTO shop) {
    ImageService imageService = ServiceManager.getService(ImageService.class);
    // 设置缩略图的参数
    Map<String, String> params = new HashMap<String, String>();
    // 若在 upyun 后台配置过缩略图版本号，则可以设置缩略图的版本名称
    // 注意：只有存在缩略图版本名称，才会按照配置参数制作缩略图，否则无效
    params.put(UpYun.PARAMS.KEY_X_GMKERL_THUMBNAIL.getValue(), ImageScene.IMAGE_AUTO.getImageVersion());
    try {
      byte[] imageBytes = RQUtil.getRQImageByte(shop.getId().toString() + "," + shop.getName(), 110);
      String shopPhotoPath = UpYunManager.getInstance().generateUploadImagePath(shop.getId(), shop.getId() + "-rq.png");
      if (UpYunManager.getInstance().writeFile(shopPhotoPath, imageBytes, true, params)) {
        DataImageRelationDTO dataImageRelationDTO = new DataImageRelationDTO(shop.getId(), shop.getId(), DataType.SHOP, ImageType.SHOP_RQ_IMAGE, 0);
        dataImageRelationDTO.setImageInfoDTO(new ImageInfoDTO(shop.getId(), shopPhotoPath));
        Set<ImageType> imageTypes = new HashSet<ImageType>();
        imageTypes.add(ImageType.SHOP_RQ_IMAGE);
        imageService.saveOrUpdateDataImageDTOs(shop.getId(), imageTypes, DataType.SHOP, shop.getId(), dataImageRelationDTO);
      } else {
        throw new Exception("店铺二维码图片上传失败!shopId:" + shop.getId());
      }
//      System.out.print(ConfigUtils.getUpYunDomainUrl() + shopPhotoPath);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }

  }

  private void handleShopServiceCategory(ShopDTO shopDTO) {
     if(StringUtil.isEmpty(shopDTO.getServiceCategoryIdStr())) {
         return;
     }
     IServiceCategoryService serviceCategoryService = ServiceManager.getService(IServiceCategoryService.class);
     serviceCategoryService.updateShopServiceCategory(shopDTO.getId(),shopDTO.getServiceCategoryIdStr());
  }

  @Deprecated
  @Override
  public void updateShopCoordinate(Long id) throws IOException {
    ConfigDaoManager configDaoManager = ServiceManager.getService(ConfigDaoManager.class);
    ConfigWriter writer = configDaoManager.getWriter();
    IGeocodingService geocodingService = ServiceManager.getService(IGeocodingService.class);
    AreaDTO areaDTO;
    String city;
    Object status = writer.begin();
    try {
      Shop shop = writer.getById(Shop.class, id);
      areaDTO = AreaCacheManager.getAreaDTOByNo(shop.getCity());
      city = (areaDTO != null && StringUtil.isNotEmpty(areaDTO.getName())) ? areaDTO.getName() : null;
      GeocoderResponse geocoderResponse = geocodingService.addressToCoordinate(shop.getAddress(), city);
      if (geocoderResponse == null || !geocoderResponse.isSuccess()) {
        areaDTO = AreaCacheManager.getAreaDTOByNo(shop.getRegion());
        if (areaDTO != null)
          geocoderResponse = geocodingService.addressToCoordinate(areaDTO.getName(), city);
        else {
          areaDTO = AreaCacheManager.getAreaDTOByNo(shop.getCity());
          if (areaDTO != null)
            geocoderResponse = geocodingService.addressToCoordinate(areaDTO.getName(), city);
        }
      }
      if (geocoderResponse != null && geocoderResponse.isSuccess()) {
        shop.setCoordinateLon(geocoderResponse.getResult().getLocation().getLng());
        shop.setCoordinateLat(geocoderResponse.getResult().getLocation().getLat());
        writer.update(shop);
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

//  /**
//   * 获得最后消费店铺 与 推荐店铺
//   * 根据地区过滤 服务范围过滤
//   *
//   * @param condition ShopSolrSearchConditionDTO
//   */
//  @Deprecated
//  @Override
//  public List<AppShopDTO> getAppUserLastExpenseAndRecommendShop(ShopSolrSearchConditionDTO condition) {
//    List<AppShopDTO> appShopDTOList = new ArrayList<AppShopDTO>();
//    if (CollectionUtil.isEmpty(condition.getShopIds())) return appShopDTOList;
//    ConfigWriter writer = configDaoManager.getWriter();
//    Area area = writer.getAreaByCityCode(condition.getCityCode());
//    if (area == null) return appShopDTOList;
//    List<Shop> shopList = new ArrayList<Shop>();
//    for (Long id : condition.getShopIds()) {
//      shopList.add(writer.getById(Shop.class, id));
//    }
//    Iterator<Shop> iterator = shopList.iterator();
//    AppShopDTO appShopDTO;
//    while (iterator.hasNext()) {
//      Shop shop = iterator.next();
//      if (!shop.getCity().equals(area.getId())) {
//        iterator.remove();
//        continue;
//      }
//      if (StringUtil.isNotEmpty(condition.getName())) {
//        if (!shop.getName().contains(condition.getName())) {
//          iterator.remove();
//          continue;
//        }
//      }
//      appShopDTO = shop.toAppShopDTO();
//      if (StringUtil.isNotEmpty(shop.getCoordinateLon()) && StringUtil.isNotEmpty(shop.getCoordinateLat())) {
//        appShopDTO.setDistance(CoordinateUtils.getTwoCoordinatesDistance(condition.getLocationLat(), condition.getLocationLon(),
//            Double.valueOf(shop.getCoordinateLat()), Double.valueOf(shop.getCoordinateLon())));
//      }
//      appShopDTO.setCityCode(condition.getCityCode());
//      appShopDTOList.add(appShopDTO);
//    }
//    return appShopDTOList;
//  }

  @Override
  public Result updateShopAddressCoordinate(Long id, String coordinateLon, String coordinateLat, Long province,
                                            Long city, Long region, String address) {
    Result result = new Result("坐标更新失败！", false);
    if (id == null) {
      LOG.error("shop id is null");
      return result;
    }
    if (coordinateLon == null || coordinateLat == null) {
      LOG.error("coordinateLon or coordinateLat  is null");
      return result;
    }
    ConfigWriter writer = configDaoManager.getWriter();
    Object status = writer.begin();
    try {
      Shop shop = writer.getById(Shop.class,id);
      shop.setCoordinateLat(coordinateLat);
      shop.setCoordinateLon(coordinateLon);
      shop.setLocateStatus(LocateStatus.ACTIVE);
      //更新坐标暂时不更新地址
//      shop.setCity(city);
//      shop.setProvince(province);
//      shop.setRegion(region);
//      shop.setAddress(address);
//      if (shop.getRegion() != null) {
//        shop.setAreaId(shop.getRegion());
//      } else if (shop.getCity() != null) {
//        shop.setAreaId(shop.getCity());
//      } else if (shop.getProvince() != null) {
//        shop.setAreaId(shop.getProvince());
//      }
      writer.update(shop);
      writer.commit(status);
      result.setSuccess(true);
      result.setMsg("坐标更新成功！");
    } finally {
      writer.rollback(status);
    }
    return result;
  }

  public void verifyShop(ShopDTO shopDTO) throws BadCredentialsException {
    if (shopDTO == null) {
      throw new BadCredentialsException("shopNull");
    }

    if (ShopState.DELETED == shopDTO.getShopState()) {
      throw new BadCredentialsException("deleteShop");
    }
    if (ShopState.OVERDUE == shopDTO.getShopState() && ShopStatus.REGISTERED_TRIAL == shopDTO.getShopStatus()) {
      throw new BadCredentialsException("shopOverdue");
    }
    if (ShopState.ARREARS == shopDTO.getShopState()) {
      throw new BadCredentialsException("arrears");
    }

    if (ShopState.IN_ACTIVE == shopDTO.getShopState() && ShopStatus.REGISTERED_PAID == shopDTO.getShopStatus()) {
//      IShopService shopService = ServiceManager.getService(IShopService.class);
//      String message = "";
//      try {
//        ShopOperateHistoryDTO shopOperateHistoryDTO = shopService.getLatestShopOperateHistory(shopDTO.getId(), ShopOperateType.DISABLE_REGISTERED_PAID_SHOP);
//        if (shopOperateHistoryDTO != null) message = shopOperateHistoryDTO.getReason();
//        else message="无";
//      } catch (BcgogoException e) {
//        LOG.warn(e.getMessage(), e);
//      }
      throw new BadCredentialsException("adminForbid");
    }
    /*if (ShopState.ACTIVE != shopDTO.getShopState() || ShopStatus.REGISTERED_PAID != shopDTO.getShopStatus()) {
      LOG.error("shop id:{} status:{} and state:{} is error! ", new Object[]{shopDTO.getId(), shopDTO.getShopStatus(), shopDTO.getShopState()});
      throw new BadCredentialsException("无效店铺，请重新输入或咨询客服.");
    }*/
  }


  public List<ShopsDTO> getShopsByGPS ( double lat , double lon){
    ConfigDaoManager configDaoManager = ServiceManager.getService(ConfigDaoManager.class);
    ConfigWriter writer = configDaoManager.getWriter();

    //先计算查询点的经纬度范围
    double r = 6371;//地球半径千米
    double dis = 0.5;//0.5千米距离
    double dlng =  2*Math.asin(Math.sin(dis/(2*r))/Math.cos(lat*Math.PI/180));
    dlng = dlng*180/Math.PI;//角度转为弧度
    double dlat = dis/r;
    dlat = dlat*180/Math.PI;
    double minlat =lat - dlat;
    double maxlat = lat + dlat;
    double minlon = lon - dlng;
    double maxlon = lon + dlng;
    return writer.getShopsByGPS(minlat , maxlat ,minlon , maxlon);
  }

  public List<TrafficPackageDTO> getTrafficPackage(File f) {
    List<TrafficPackageDTO> list = new ArrayList<TrafficPackageDTO>();
    try {

      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();
      Document doc = builder.parse(f);
      NodeList nl = doc.getElementsByTagName("value");
      for (int i = 0; i < nl.getLength(); i++) {
        TrafficPackageDTO trafficPackageDTO = new TrafficPackageDTO();
        trafficPackageDTO.setProductId(Long.valueOf(doc.getElementsByTagName("productId").item(i).getFirstChild().getNodeValue()));
        trafficPackageDTO.setName(doc.getElementsByTagName("name").item(i).getFirstChild().getNodeValue());
        trafficPackageDTO.setIntroduce(doc.getElementsByTagName("introduce").item(i).getFirstChild().getNodeValue());
        trafficPackageDTO.setRemainingNum(Integer.valueOf(doc.getElementsByTagName("totalNum").item(i).getFirstChild().getNodeValue()));
        trafficPackageDTO.setPrice(Double.valueOf(doc.getElementsByTagName("price").item(i).getFirstChild().getNodeValue()));
        trafficPackageDTO.setPictureUrl(doc.getElementsByTagName("pictureUrl").item(i).getFirstChild().getNodeValue());
        trafficPackageDTO.setDisCount(Double.valueOf(doc.getElementsByTagName("discount").item(i).getFirstChild().getNodeValue()));
        list.add(trafficPackageDTO);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    LOG.info(list.toString());
    return list;
  }


}
