package com.bcgogo.admin.config.shop;

import com.bcgogo.admin.user.permission.ShopUpgradeHandler;
import com.bcgogo.common.AllListResult;
import com.bcgogo.common.ListResult;
import com.bcgogo.common.Result;
import com.bcgogo.config.model.ShopAgentProduct;
import com.bcgogo.enums.DeletedType;
import com.bcgogo.enums.config.VehicleSelectBrandModel;
import com.bcgogo.enums.user.Status;
import com.bcgogo.product.StandardBrandModelCache.StandardBrandModelCache;
import com.bcgogo.user.dto.permission.ShopVersionDTO;
import com.bcgogo.user.service.permission.IShopVersionService;
import com.bcgogo.util.WebUtil;
import com.bcgogo.config.ShopSearchCondition;
import com.bcgogo.config.ShopSearchResult;
import com.bcgogo.config.ShopSearchScene;
import com.bcgogo.config.dto.AreaDTO;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.dto.ShopOperateHistoryDTO;
import com.bcgogo.config.dto.image.DataImageDetailDTO;
import com.bcgogo.config.dto.image.DataImageRelationDTO;
import com.bcgogo.config.dto.image.ImageInfoDTO;
import com.bcgogo.config.model.ShopAgentProduct;
import com.bcgogo.config.service.*;
import com.bcgogo.config.service.image.IImageService;
import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.config.util.ImageUtils;
import com.bcgogo.enums.DeletedType;
import com.bcgogo.enums.config.DataType;
import com.bcgogo.enums.config.ImageScene;
import com.bcgogo.enums.config.ImageType;
import com.bcgogo.enums.config.VehicleSelectBrandModel;
import com.bcgogo.enums.shop.*;
import com.bcgogo.enums.txn.finance.BuyChannels;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.product.service.IProductCategoryService;
import com.bcgogo.product.service.IProductService;
import com.bcgogo.product.standardVehicleBrandModel.ShopVehicleBrandModelDTO;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.service.ITxnService;
import com.bcgogo.txn.service.solr.IShopSolrWriterService;
import com.bcgogo.user.dto.ContactDTO;
import com.bcgogo.user.dto.Node;
import com.bcgogo.user.dto.UserDTO;
import com.bcgogo.user.service.IRelatedShopUpdateService;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.user.service.permission.IUserCacheService;
import com.bcgogo.util.WebUtil;
import com.bcgogo.utils.ArrayUtil;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-7-30
 * Time: 下午2:30
 * shop controller (shop shopVersion)
 */
@Controller
@RequestMapping("/shopManage.do")
public class ShopManageController {
  private static final Logger LOG = LoggerFactory.getLogger(ShopManageController.class);
  private IConfigService configService = null;
  private IShopService shopService = null;
  private IUserCacheService userCacheService = null;
  @Autowired
  private IUserService userService = null;
  @Autowired
  private ShopActivateHandler activateHandler = null;
  @Autowired
  private ShopUpgradeHandler shopUpgradeHandler = null;

  public IConfigService getConfigService() {
    if (configService == null) {
      configService = ServiceManager.getService(IConfigService.class);
    }
    return configService;
  }

  public IUserCacheService getUserCacheService() {
    if (userCacheService == null) {
      userCacheService = ServiceManager.getService(IUserCacheService.class);
    }
    return userCacheService;
  }

  public IShopService getShopService() {
    if (shopService == null) {
      shopService = ServiceManager.getService(IShopService.class);
    }
    return shopService;
  }

  //获得客户线索
  @RequestMapping(params = "method=getShopByShopCondition")
  @ResponseBody
  public Object getShopByShopCondition(HttpServletRequest request, HttpServletResponse response, ShopSearchCondition condition) {
    ShopSearchResult result;
    try {
      if (ShopSearchScene.CLUE == condition.getScene()) {
        //获得数据权限 (线索客户)
        condition.setUserIds(getUserCacheService().getSubordinateIdsByUserId(WebUtil.getShopId(request), WebUtil.getUserId(request)));
        if (ArrayUtils.isEmpty(condition.getShopStatuses())) {
          condition.setShopStatuses(new String[]{ShopStatus.NO_INTENTION.name(), ShopStatus.LATENT.name(), ShopStatus.INTENTION.name()});
        }
      } else if (ShopSearchScene.CHECK_PENDING == condition.getScene()) {
        condition.setSortFiled("submit_application_date");
      } else if (ShopSearchScene.REGISTERED_TRAIL == condition.getScene()) {
        condition.setSortFiled("trial_end_time");
      } else if (ShopSearchScene.REGISTERED == condition.getScene()) {
        condition.setSortFiled("registration_date");
      }
      List<Long> ids;
      if (StringUtils.isNotBlank(condition.getRegion())) {
        condition.setAreaId(new Long[]{Long.valueOf(condition.getRegion())});
      } else if (StringUtils.isNotBlank(condition.getCity())) {
        ids = getConfigService().getAreaLeafsByParentId(NumberUtil.longValue(condition.getCity()));
        if (CollectionUtils.isNotEmpty(ids)) condition.setAreaId(ids.toArray(new Long[ids.size()]));
      } else if (StringUtils.isNotBlank(condition.getProvince())) {
        ids = getConfigService().getAreaLeafsByParentId(NumberUtil.longValue(condition.getProvince()));
        if (CollectionUtils.isNotEmpty(ids)) condition.setAreaId(ids.toArray(new Long[ids.size()]));
      }
      result = getShopService().getShopByShopCondition(condition);
    } catch (Exception e) {
      result = new ShopSearchResult();
      result.setSuccess(false);
      LOG.debug("/admin/shopManage.do");
      LOG.debug("method=getShopByShopCondition");
      LOG.error(e.getMessage(), e);
    }
    return result;
  }

  @ResponseBody
  @RequestMapping(params = "method=updateShopAddressCoordinate")
  public Object updateShopAddressCoordinate(HttpServletRequest request, HttpServletResponse response,
                                            Long id, String coordinateLon, String coordinateLat,
                                            Long province, Long city, Long region, String address) {
    try {
      Result result = ServiceManager.getService(IShopService.class)
          .updateShopAddressCoordinate(id, coordinateLon, coordinateLat, province, city, region, address);
      if (result.isSuccess())
        ServiceManager.getService(IShopSolrWriterService.class).reCreateShopIdSolrIndex(id);
      return result;
    } catch (Exception e) {
      LOG.debug("/admin/shopManage.do");
      LOG.debug("method=updateShopAddressCoordinate");
      LOG.error(e.getMessage(), e);
    }
    return new Result(false);
  }

  @ResponseBody
  @RequestMapping(params = "method=shopRegisteredStatistics")
  public Object shopRegisteredStatistics(HttpServletRequest request, HttpServletResponse response, ShopStatus shopStatus) {
    try {
      return getShopService().shopRegisteredStatistics(shopStatus);
    } catch (BcgogoException e) {
      LOG.debug("/admin/shopManage.do");
      LOG.debug("method=shopRegisteredStatistics");
      LOG.error(e.getMessage(), e);
    }
    return new Result(false);
  }

  @ResponseBody
  @RequestMapping(params = "method=getShopDetail")
  public Object getShopContactsByShopId(HttpServletRequest request, HttpServletResponse response, Long shopId) {
    try {
      List<ContactDTO> contactDTOList = getShopService().getShopContactsByShopId(shopId);
      List<Long> businessScopeIds = getShopService().getBusinessScopeIdsByShopId(shopId);
      Object[] objects = new Object[19];
      objects[0] = contactDTOList;
      objects[1] = businessScopeIds;
      objects[2] = ServiceManager.getService(IProductCategoryService.class).getBusinessScopeByShopId(shopId);
      objects[3] = ServiceManager.getService(IProductService.class).getShopRegisterProductList(shopId);
      IImageService imageService = ServiceManager.getService(IImageService.class);
      List<ImageScene> imageSceneList = new ArrayList<ImageScene>();
      imageSceneList.add(ImageScene.SHOP_IMAGE_SMALL);
      imageSceneList.add(ImageScene.SHOP_IMAGE_BIG);
      imageSceneList.add(ImageScene.SHOP_IMAGE);
      imageSceneList.add(ImageScene.SHOP_BUSINESS_LICENSE_IMAGE);
      Set<Long> shopIdSet = new HashSet<Long>();
      shopIdSet.add(shopId);
      Map<Long, Map<ImageScene, List<DataImageDetailDTO>>> imageMap = imageService.getDataImageDetailDTO(shopIdSet, imageSceneList, DataType.SHOP, shopId);
      objects[4] = ImageUtils.generateCommonImageDetail(imageSceneList, imageMap.get(shopId), true);
      //审核记录
      objects[5] = userService.getShopAuditLogDTOListByShopIdAndStatus(shopId, AuditStatus.DISAGREE);
      //服务范围
      String serviceCategoryStr = "";
      Map<Long, String> shopServiceCategoryIdNameMap = ServiceManager.getService(IServiceCategoryService.class).getShopServiceCategoryIdNameMap(shopId);
      if (shopServiceCategoryIdNameMap != null) {
        for (Long serviceCategoryId : shopServiceCategoryIdNameMap.keySet()) {
          serviceCategoryStr += shopServiceCategoryIdNameMap.get(serviceCategoryId) + ",";
        }
        if(StringUtils.isNotBlank(serviceCategoryStr))
          serviceCategoryStr = serviceCategoryStr.substring(0,serviceCategoryStr.length() - 1);
      }
      if (StringUtil.isNotEmpty(serviceCategoryStr)) {
        serviceCategoryStr = serviceCategoryStr.substring(0, serviceCategoryStr.length() - 1);
      }
      objects[6] = serviceCategoryStr;
      //主营车型
      objects[7] = ServiceManager.getService(IProductService.class).getVehicleBrandModelByShopId(shopId);

      objects[8] = ServiceManager.getService(IServiceCategoryService.class).getCheckedServiceCategory(shopId);
      //是否选择了全部车型
      ShopDTO shopDTO = ServiceManager.getService(IConfigService.class).getShopById(shopId);
      if (VehicleSelectBrandModel.ALL_MODEL.equals(shopDTO.getShopSelectBrandModel())) {
        objects[9] = "ALL_MODEL";
      } else if (VehicleSelectBrandModel.PART_MODEL.equals(shopDTO.getShopSelectBrandModel())) {
        objects[9] = "PART_MODEL";
      } else {
        objects[9] = null;
      }
      //是否是汽配版本
      objects[10] = ConfigUtils.isWholesalerVersion(shopDTO.getShopVersionId());
      //代理产品
      List<ShopAgentProduct> shopAgentProductList = ServiceManager.getService(IAgentProductService.class).getShopAgentProduct(shopId);
      if (CollectionUtils.isNotEmpty(shopAgentProductList)) {
        objects[11] = true;
      } else {
        objects[11] = false;
      }
      //主营车型Ids
      List<Long> shopVehicleModelIds = new ArrayList<Long>();
      List<ShopVehicleBrandModelDTO> shopVehicleBrandModelDTOList = ServiceManager.getService(IProductService.class).getShopVehicleBrandModelByShopId(shopId);
      if (CollectionUtils.isNotEmpty(shopVehicleBrandModelDTOList)) {
        for (ShopVehicleBrandModelDTO shopVehicleBrandModelDTO : shopVehicleBrandModelDTOList) {
          shopVehicleModelIds.add(shopVehicleBrandModelDTO.getModelId());
        }
      }
      objects[12] = shopVehicleModelIds;
      objects[13] = shopDTO.getChargeType();
      objects[14] = userService.getShopAuditLogDTOListByShopIdAndStatus(shopId, AuditStatus.AGREE);

      IAreaService areaService = ServiceManager.getService(IAreaService.class);
      objects[15] = areaService.getShopAdAreaDTOsByShopId(shopId);
      objects[16] = areaService.getShopAdAreaScopeByShopId(shopId);

      IRecommendShopService recommendShopService = ServiceManager.getService(IRecommendShopService.class);
      objects[17] = recommendShopService.getShopRecommendDTOs(shopId);
      objects[18] = areaService.getShopRecommendScopeByShopId(shopId);


      return new Result(true, objects);
    } catch (Exception e) {
      LOG.debug("/admin/shopManage.do");
      LOG.debug("method=getShopDetail");
      LOG.error(e.getMessage(), e);
    }
    return new Result(false);
  }


  @ResponseBody
  @RequestMapping(params = "method=getShopSuggestionByName")
  public Object getShopSuggestionByName(HttpServletRequest request, HttpServletResponse response, String shopName, ShopStatus... shopStatuses) {
    ListResult<ShopDTO> result;
    try {
      result = getShopService().getShopSuggestionByName(shopName, shopStatuses);
    } catch (BcgogoException e) {
      result = new AllListResult<ShopDTO>();
      LOG.debug("/admin/shopManage.do");
      LOG.debug("method=getShopSuggestionByName");
      LOG.error(e.getMessage(), e);
    }
    return result;
  }

  @ResponseBody
  @RequestMapping(params = "method=getActiveUsingShopByName")
  public Object getActiveUsingShopByName(HttpServletRequest request, HttpServletResponse response, String shopName) {
    if (shopName == null) return "";
    ShopDTO dto = getShopService().getActiveUsingShopByName(shopName);
    if (dto != null) return dto;
    return "";
  }

  @ResponseBody
  @RequestMapping(params = "method=getLatestShopOperateHistoryList")
  public Object getLatestShopOperateHistoryList(HttpServletRequest request, HttpServletResponse response, Long shopId) {
    ListResult<ShopOperateHistoryDTO> result;
    if (shopId == null) return new AllListResult<ShopOperateHistoryDTO>();
    try {
      result = getShopService().getLatestShopOperateHistoryList(shopId);
      Set<Long> userIds = new HashSet<Long>();
      for (ShopOperateHistoryDTO dto : result.getResults()) {
        userIds.add(dto.getOperateUserId());
      }
      Map<Long, UserDTO> userDTOMap = getUserCacheService().getUserMap(userIds);
      UserDTO userDTO;
      for (ShopOperateHistoryDTO dto : result.getResults()) {
        userDTO = userDTOMap.get(dto.getOperateUserId());
        if (userDTO != null) dto.setOperateUserName(userDTO.getName());
      }
    } catch (BcgogoException e) {
      result = new AllListResult<ShopOperateHistoryDTO>();
      LOG.debug("/admin/shopManage.do");
      LOG.debug("method=getLatestShopOperateHistoryList");
      LOG.error(e.getMessage(), e);
    }
    return result;
  }

  @ResponseBody
  @RequestMapping(params = "method=selectArea")
  public Object selectArea(HttpServletRequest request, HttpServletResponse response, String parentNo) {
    Map<String, Object> result = new HashMap<String, Object>();
    if (StringUtils.isBlank(parentNo)) return result;
    result.put("success", true);
    List<AreaDTO> areaDTOList = getConfigService().getChildAreaDTOList(NumberUtil.longValue(parentNo));
    result.put("results", areaDTOList);
    return result;
  }

  //查询地区
  @RequestMapping(params = "method=searchLicenseNo")
  @ResponseBody
  public Map<String, Object> searchLicenseNo(HttpServletRequest request, String localArea) {
    IProductService productService = ServiceManager.getService(IProductService.class);
    Map<String, Object> result = new HashMap<String, Object>();
    try {
      //根据地区找到车牌
      String localCarNo = productService.getCarNoByAreaNo(Long.valueOf(localArea));
      if (StringUtils.isNotBlank(localCarNo)) {
        result.put("plateCarNo", localCarNo);
        result.put("success", true);
      }
    } catch (Exception e) {
      result.put("success", false);
      LOG.debug("/shopManager.do");
      LOG.debug("method=searchLicenseNo");
      LOG.debug("shopId:" + WebUtil.getShopId(request) + ",userId:" + WebUtil.getUserId(request));
      LOG.debug("localArea:" + localArea);
      LOG.error(e.getMessage(), e);
    }
    return result;
  }

  @RequestMapping(params = "method=updateSaleManShop")
  @ResponseBody
  public Object updateSaleManShop(HttpServletRequest request, Long userId, Long id) {
    Map<String, Object> result = new HashMap<String, Object>();
    try {
      UserDTO userDTO = ServiceManager.getService(IUserService.class).getUserByUserId(userId);
      if (userDTO == null) {
        result.put("success", false);
        result.put("message", "操作失败!");
        return result;
      }
      getShopService().maintainShopLog(id, userId, userDTO.getUserName());
      result.put("success", true);
      result.put("message", "操作成功!");
    } catch (Exception e) {
      LOG.debug("/admin/shopManage.do");
      LOG.debug("method=updateSaleManShop");
      LOG.error(e.getMessage(), e);
      result.put("success", false);
      result.put("message", "操作失败!");
    }
    return result;
  }

  @RequestMapping(params = "method=updateShopStatus")
  @ResponseBody
  public Object updateShopStatus(HttpServletRequest request, Long shopId, ShopState state) {
    Map<String, Object> result = new HashMap<String, Object>();
    try {
      getShopService().updateShopState(shopId, state);
      result.put("success", true);
      result.put("message", "操作成功!");
    } catch (Exception e) {
      LOG.debug("/admin/shopManage.do");
      LOG.debug("method=updateShopStatus");
      LOG.error(e.getMessage(), e);
      result.put("success", false);
      result.put("message", "操作失败!");
    }
    return result;
  }

  @RequestMapping(params = "method=createShopOperateHistory")
  @ResponseBody
  public Object createShopOperateHistory(HttpServletRequest request, ShopOperateHistoryDTO dto) {
    Map<String, Object> result = new HashMap<String, Object>();
    try {
      dto.setOperateTime(System.currentTimeMillis());
      if (dto.getTrialEndTime() != null) {
        dto.setTrialEndTime(NumberUtil.longValue(dto.getTrialEndTime()) + 24 * 60 * 60 * 1000 - 1);
      }
      dto.setOperateUserId(WebUtil.getUserId(request));
      shopUpgradeHandler.upgrade(dto);
      result.put("success", true);
      result.put("message", "操作成功!");
    } catch (Exception e) {
      LOG.debug("/admin/shopManage.do");
      LOG.debug("method=updateShopStatus");
      LOG.error(e.getMessage(), e);
      result.put("success", false);
      result.put("message", "操作失败!");
    }
    return result;
  }

  @RequestMapping(params = "method=rollOutClue")
  @ResponseBody
  public Object rollOutClue(HttpServletRequest request, Long id, Long userId) {
    Map<String, Object> result = new HashMap<String, Object>();
    try {
      UserDTO userDTO = ServiceManager.getService(IUserService.class).getUserByUserId(userId);
      if (userDTO == null) {
        result.put("success", false);
        result.put("message", "操作失败!");
        return result;
      }
      getShopService().maintainShopLog(id, userId, userDTO.getName());
      result.put("success", true);
      result.put("message", "操作成功!");
    } catch (Exception e) {
      LOG.debug("/admin/shopManage.do");
      LOG.debug("method=rollOutClue");
      LOG.error(e.getMessage(), e);
      result.put("success", false);
      result.put("message", "操作失败!");
    }
    return result;
  }

  @RequestMapping(params = "method=saveShop")
  @ResponseBody
  public Object saveShop(HttpServletRequest request, ShopDTO dto) {
    IRelatedShopUpdateService relatedShopUpdateService = ServiceManager.getService(IRelatedShopUpdateService.class);
    Map<String, Object> result = new HashMap<String, Object>();
    try {
      //数据处理过程
      if (!shopDataHandler(dto, request, result)) {
        return result;
      }
      dto.setLegalRep(dto.getOwner());
      if (dto.getId() == null) {
        dto = ServiceManager.getService(IShopService.class).createShop(dto);
        if (!ConfigUtils.isWholesalerVersion(dto.getShopVersionId())) {
          //服务范围
          ServiceManager.getService(IServiceCategoryService.class).saveShopServiceCategory(dto.getId(), ArrayUtil.toLongArr(dto.getServiceCategoryIds()));
          //代理产品
          ServiceManager.getService(IAgentProductService.class).saveShopAgentProduct(dto.getId(), ArrayUtil.toLongArr(dto.getAgentProductIds()));
        }
        //主营车型
        if (CollectionUtils.isNotEmpty(dto.getVehicleModelIds())) {
          IProductService productService = ServiceManager.getService(IProductService.class);
          List<ShopVehicleBrandModelDTO> shopVehicleBrandModelDTOList = productService.generateVehicleBrandModelDTOByModelIds(dto.getVehicleModelIds());
          if (CollectionUtils.isNotEmpty(shopVehicleBrandModelDTOList)) {
            ShopVehicleBrandModelDTO[] shopVehicleBrandModelDTOs = new ShopVehicleBrandModelDTO[shopVehicleBrandModelDTOList.size()];
            shopVehicleBrandModelDTOList.toArray(shopVehicleBrandModelDTOs);
            dto.setShopVehicleBrandModelDTOs(shopVehicleBrandModelDTOs);
            productService.saveShopVehicleBrandModel(dto);
          }
        }
        //广告范围
        ServiceManager.getService(IAreaService.class).saveOrUpdateShopAdArea(dto.getId(), dto.getShopAdAreaIds());
        //广告类目
        ServiceManager.getService(IAreaService.class).saveOrUpdateShopRecommend(dto.getId(),dto.getRecommendIds());
      } else {
        //服务范围
        String serviceCategoryStr = "";
        if (CollectionUtils.isNotEmpty(dto.getServiceCategoryIds())) {
          serviceCategoryStr = dto.getServiceCategoryIds().toString();
          serviceCategoryStr = serviceCategoryStr.substring(1, serviceCategoryStr.length() - 1);
        }

        ServiceManager.getService(IServiceCategoryService.class).updateShopServiceCategory(dto.getId(), serviceCategoryStr);
        if (dto.getSelectAllBrandModel() != null && dto.getSelectAllBrandModel()) {
          dto.setVehicleModelIds(null);
        }
        ServiceManager.getService(IProductService.class).updateShopVehicleBrandModel(dto.getId(), dto.getVehicleModelIds());
        //代理产品
        if (dto.getAgentProductIds() != null && CollectionUtils.isNotEmpty(dto.getAgentProductIds())) {
          if (CollectionUtils.isNotEmpty(ServiceManager.getService(IAgentProductService.class).getShopAgentProduct(dto.getId()))) {
            ServiceManager.getService(IAgentProductService.class).updateShopAgentProductStatus(dto.getId(), DeletedType.FALSE);
          } else {
            ServiceManager.getService(IAgentProductService.class).saveShopAgentProduct(dto.getId(), ArrayUtil.toLongArr(dto.getAgentProductIds()));
          }

        } else {
          ServiceManager.getService(IAgentProductService.class).updateShopAgentProductStatus(dto.getId(), DeletedType.TRUE);
        }

        //广告范围
        ServiceManager.getService(IAreaService.class).saveOrUpdateShopAdArea(dto.getId(),dto.getShopAdAreaIds());
        //广告类目
        ServiceManager.getService(IAreaService.class).saveOrUpdateShopRecommend(dto.getId(), dto.getRecommendIds());

        ShopDTO dbShopDTO = getConfigService().getShopById(dto.getId());
        List<Long> shopBusinessScopeProductCategoryIds = getConfigService().getShopBusinessScopeProductCategoryIdListByShopId(dto.getId());
        if (CollectionUtils.isNotEmpty(shopBusinessScopeProductCategoryIds) && dbShopDTO != null) {
          dbShopDTO.setProductCategoryIds(new HashSet<Long>(shopBusinessScopeProductCategoryIds));
        }
        if (!dbShopDTO.getName().equals(dto.getName())) {
          ServiceManager.getService(IShopService.class).saveOrUpdateShopRQImage(dto);
        }
        boolean isNeedToCreateTask = relatedShopUpdateService.isNeedToCreateTask(dbShopDTO, dto);
        dto = ServiceManager.getService(IShopService.class).updateShop(dto);
        ServiceManager.getService(IShopSolrWriterService.class).reCreateShopIdSolrIndex(dto.getId());
        if (isNeedToCreateTask) {
          relatedShopUpdateService.createRelatedShopUpdateTask(dto.getId());
        }
      }
      if (ArrayUtil.isNotEmpty(dto.getProductDTOs()) && (ShopOperateScene.SUBMIT_CLIENT_APPLICATION) == dto.getScene()) {
        dto.prepareForSaveProduct();
        ServiceManager.getService(ITxnService.class).batchSaveProductWithReindex(dto.getId(), null, dto.getProductDTOs());
        ServiceManager.getService(IProductService.class).saveShopRegisterProduct(dto.getProductDTOs());
        getConfigService().saveOrUpdateUnitSort(dto.getId(), dto.getProductDTOs());
      }
      if (dto.getImageCenterDTO() != null) {
        IImageService imageService = ServiceManager.getService(IImageService.class);
        List<DataImageRelationDTO> dataImageRelationDTOList = new ArrayList<DataImageRelationDTO>();
        if (StringUtils.isNotBlank(dto.getImageCenterDTO().getShopBusinessLicenseImagePath())) {
          DataImageRelationDTO dataImageRelationDTO = new DataImageRelationDTO(dto.getId(), dto.getId(), DataType.SHOP, ImageType.SHOP_BUSINESS_LICENSE_IMAGE, 1);
          dataImageRelationDTO.setImageInfoDTO(new ImageInfoDTO(dto.getId(), dto.getImageCenterDTO().getShopBusinessLicenseImagePath()));
          dataImageRelationDTOList.add(dataImageRelationDTO);
        }
        if (CollectionUtils.isNotEmpty(dto.getImageCenterDTO().getShopImagePaths())) {
          int count = 0;
          for (String imagePath : dto.getImageCenterDTO().getShopImagePaths()) {
            if (StringUtils.isNotBlank(imagePath)) {
              DataImageRelationDTO dataImageRelationDTO = new DataImageRelationDTO(dto.getId(), dto.getId(), DataType.SHOP, count == 0 ? ImageType.SHOP_MAIN_IMAGE : ImageType.SHOP_AUXILIARY_IMAGE, count);
              dataImageRelationDTO.setImageInfoDTO(new ImageInfoDTO(dto.getId(), imagePath));
              dataImageRelationDTOList.add(dataImageRelationDTO);
              count++;
            }
          }
        }
        Set<ImageType> imageTypeSet = new HashSet<ImageType>();
        imageTypeSet.add(ImageType.SHOP_AUXILIARY_IMAGE);
        imageTypeSet.add(ImageType.SHOP_MAIN_IMAGE);
        imageTypeSet.add(ImageType.SHOP_BUSINESS_LICENSE_IMAGE);

        imageService.saveOrUpdateDataImageDTOs(dto.getId(), imageTypeSet, DataType.SHOP, dto.getId(), dataImageRelationDTOList.toArray(new DataImageRelationDTO[dataImageRelationDTOList.size()]));
      }

      result.put("success", true);
      result.put("shop", dto);
    } catch (Exception e) {
      LOG.debug("/admin/shopManage.do");
      LOG.debug("method=saveShop");
      LOG.error(e.getMessage(), e);
      result.put("success", false);
      result.put("message", "操作失败!");
    }
    return result;
  }

  @RequestMapping(params = "method=resetPassword")
  @ResponseBody
  public Object resetPassword(HttpServletRequest request, ShopDTO dto) {
    try {
      return ServiceManager.getService(IUserService.class)
          .resetSystemCreatedPassword(dto.getId());
    } catch (Exception e) {
      LOG.debug("/admin/resetPassword.do");
      LOG.debug("method=shopManage");
      LOG.error(e.getMessage(), e);
      return new Result(e.getMessage(), false);
    }
  }

  @RequestMapping(params = "method=changeUserNo")
  @ResponseBody
  public Object changeUserNo(HttpServletRequest request, ShopDTO dto) {
    try {
      return ServiceManager.getService(IUserService.class)
          .changeSystemCreatedUserNo(dto.getId(), dto.getManagerUserNo());
    } catch (Exception e) {
      LOG.debug("/admin/changeUserNo.do");
      LOG.debug("method=shopManage");
      LOG.error(e.getMessage(), e);
      return new Result(e.getMessage(), false);
    }
  }

  @ResponseBody
  @RequestMapping(params = "method=activateShop")
  public Object activateShop(HttpServletRequest request, Long shopId, String reason, AuditStatus auditStatus) {
    return activateHandler.activate(shopId, request, reason, auditStatus);
  }

  //处理shop数据
  private boolean shopDataHandler(ShopDTO dto, HttpServletRequest request, Map<String, Object> result) throws Exception {
    if (StringUtils.isBlank(dto.getName())) {
      result.put("message", "店铺名称不能为空！");
      result.put("success", false);
      return false;
    }
    if (getShopService().checkShopName(dto.getName(), dto.getId())) {
      result.put("duplicate", true);
      result.put("success", false);
      return false;
    }
    //增加待审核
    if (dto.getScene() == ShopOperateScene.SUBMIT_CLIENT_APPLICATION) {
      dto.setSubmitApplicationDate(System.currentTimeMillis());
      if (dto.getShopStatus() == null) dto.setShopStatus(ShopStatus.CHECK_PENDING);
    }

    addLicencePlate(dto);
    //注册代理与跟进人
    addAgentAndFollow(dto, request);
    //店面特色
    dto.toFeatures();
    //相关业务
    dto.toRelatedBusinesses();
    //经营方式
    dto.toOperationModes();
    //判断区域
    if (dto.getRegion() != null) {
      dto.setAreaId(dto.getRegion());
    } else if (dto.getCity() != null) {
      dto.setAreaId(dto.getCity());
    } else if (dto.getProvince() != null) {
      dto.setAreaId(dto.getProvince());
    }
    if (dto.getRegisterType() == null) dto.setRegisterType(RegisterType.SALESMAN_REGISTER);
    if (dto.getBuyChannels() == null) dto.setBuyChannels(BuyChannels.BACKGROUND_ENTRY);
    if (dto.getShopState() == null) dto.setShopState(ShopState.ACTIVE);
    if (dto.getShopKind() == null) dto.setShopKind(ShopKind.OFFICIAL);
    // 关联客户上限
    if (ConfigUtils.isWholesalerVersion(dto.getShopVersionId())) {
      dto.setShopLevel(ShopLevel.PRIMARY_WHOLESALER);
    }
    //软件售价
    ShopVersionDTO shopVersionDTO = ServiceManager.getService(IShopVersionService.class).getShopVersionById(dto.getShopVersionId());
    if(shopVersionDTO!=null && shopVersionDTO.getSoftPrice()!=null && dto.getSoftPrice() == null){
      dto.setSoftPrice(shopVersionDTO.getSoftPrice().doubleValue());
    }
    //广告相关设置
    if(dto.getAdPricePerMonth()!= null && dto.getAdPricePerMonth()>0 && StringUtils.isNotBlank(dto.getAdStartDateStr())
        && StringUtils.isNotBlank(dto.getAdEndDateStr())){
      dto.setAdStartDate(DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_DAY,dto.getAdStartDateStr()));
      dto.setAdEndDate(DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_DAY,dto.getAdEndDateStr()));
      if(ProductAdType.ALL.equals(dto.getProductAdType())){
        dto.setShopAdAreaIds(null);
      }
      if(!(System.currentTimeMillis()>= dto.getAdStartDate() && System.currentTimeMillis() <= dto.getAdEndDate())){
        dto.setProductAdType(ProductAdType.DISABLED);
      }
    }else {
      dto.setAdPricePerMonth(null);
      dto.setAdStartDate(null);
      dto.setAdEndDate(null);
      dto.setProductAdType(ProductAdType.DISABLED);
      dto.setShopAdAreaIds(null);
    }
    return true;
  }

  private void addLicencePlate(ShopDTO dto) {
    //意向客户才建立销售与店面的关系
    if (dto.getScene() == ShopOperateScene.ADD_INTENTION_CLIENT) {
      if (dto.getCity() != null) {
        dto.setLicencePlate(ServiceManager.getService(IProductService.class).getCarNoByAreaNo(dto.getCity()));
      }
    }
  }

  private void addAgentAndFollow(ShopDTO dto, HttpServletRequest request) {
    //跟进人 与 注册人
    if (dto.getId() == null && dto.getScene() != ShopOperateScene.ADD_INTENTION_CLIENT) {
      UserDTO follow = getUserCacheService().getUser(dto.getFollowName(), WebUtil.getShopId(request));
      dto.setFollowId(follow.getId());
      dto.setFollowName(follow.getName());
      UserDTO agent = ServiceManager.getService(IUserService.class).getUserByUserId(WebUtil.getUserId(request));
      if (agent != null) {
        dto.setAgentId(agent.getUserNo());
        dto.setAgentMobile(agent.getMobile());
        dto.setAgent(agent.getName());
      }
    }
    //线索注册
    else if ((dto.getId() != null && dto.getScene() == ShopOperateScene.SUBMIT_CLIENT_APPLICATION)) {
      UserDTO follow = getUserCacheService().getUser(dto.getFollowName(), WebUtil.getShopId(request));
      if (follow != null) {
        dto.setFollowId(follow.getId());
        dto.setFollowName(follow.getName());
      }
      UserDTO agent = ServiceManager.getService(IUserService.class).getUserByUserId(WebUtil.getUserId(request));
      if (agent != null) {
        dto.setAgentId(agent.getUserNo());
        dto.setAgentMobile(agent.getMobile());
        dto.setAgent(agent.getName());
      }
    }
    //非线索客户
    else if (dto.getScene() != ShopOperateScene.ADD_INTENTION_CLIENT) {
      UserDTO follow = getUserCacheService().getUser(dto.getFollowName(), WebUtil.getShopId(request));
      dto.setFollowId(follow.getId());
      dto.setFollowName(follow.getName());
    }
    //线索客户
    else if (dto.getId() == null && dto.getScene() == ShopOperateScene.ADD_INTENTION_CLIENT) {
      UserDTO agent = ServiceManager.getService(IUserService.class).getUserByUserId(WebUtil.getUserId(request));
      dto.setFollowId(agent.getId());
      dto.setFollowName(agent.getName());
    }
  }

  @ResponseBody
  @RequestMapping(params = "method=getServiceCatrgory")
  public Object getServiceCatrgory(HttpServletRequest request, Long shopId) {
    Result result = new Result();
    List<Node> nodes = ServiceManager.getService(IServiceCategoryService.class).getCheckedServiceCategory(shopId);
    if (CollectionUtils.isEmpty(nodes)) {
      result.setSuccess(false);
      return result;
    }
    result.setSuccess(true);
    result.setData(nodes);
    return result;
  }
}
