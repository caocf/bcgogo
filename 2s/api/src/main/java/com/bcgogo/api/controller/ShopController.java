package com.bcgogo.api.controller;

import com.bcgogo.api.*;
import com.bcgogo.api.response.*;
import com.bcgogo.config.service.IShopService;
import com.bcgogo.config.service.ShopService;
import com.bcgogo.config.util.MemCacheAdapter;
import com.bcgogo.user.service.utils.SessionUtil;
import com.bcgogo.baidu.model.geocoder.GeocoderResponse;
import com.bcgogo.baidu.service.IGeocodingService;
import com.bcgogo.common.Pager;
import com.bcgogo.common.Result;
import com.bcgogo.config.cache.BcgogoConcurrentController;
import com.bcgogo.config.cache.ServiceCategoryCache;
import com.bcgogo.config.dto.ServiceCategoryDTO;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.dto.ShopServiceCategoryDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.service.IServiceCategoryService;
import com.bcgogo.config.service.image.IImageService;
import com.bcgogo.enums.ConcurrentScene;
import com.bcgogo.enums.DataKind;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.enums.app.*;
import com.bcgogo.enums.config.ImageScene;
import com.bcgogo.enums.shop.ShopType;
import com.bcgogo.enums.txn.AdvertStatus;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.search.dto.ShopSearchResultListDTO;
import com.bcgogo.search.dto.ShopSolrSearchConditionDTO;
import com.bcgogo.search.service.shop.IShopSolrService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.AdvertDTO;
import com.bcgogo.txn.dto.AppointOrderDTO;
import com.bcgogo.txn.dto.pushMessage.appoint.AppAppointParameter;
import com.bcgogo.txn.dto.supplierComment.CommentConstant;
import com.bcgogo.txn.model.app.AppointOrder;
import com.bcgogo.txn.service.IAdvertService;
import com.bcgogo.txn.service.IAppointOrderService;
import com.bcgogo.txn.service.ITxnService;
import com.bcgogo.txn.service.app.IAppOrderService;
import com.bcgogo.txn.service.app.IAppShopCommentService;
import com.bcgogo.txn.service.member.IMemberGenerateService;
import com.bcgogo.txn.service.pushMessage.IAppointPushMessageService;
import com.bcgogo.txn.service.solr.ICustomerOrSupplierSolrWriteService;
import com.bcgogo.txn.service.supplierComment.IAppUserCommentService;
import com.bcgogo.txn.service.supplierComment.ISupplierCommentService;
import com.bcgogo.user.dto.MemberDTO;
import com.bcgogo.user.dto.VehicleDTO;
import com.bcgogo.user.service.ICustomerService;
import com.bcgogo.user.service.IMembersService;
import com.bcgogo.user.service.IVehicleService;
import com.bcgogo.user.service.app.IAppShopService;
import com.bcgogo.user.service.app.IAppUserCustomerMatchService;
import com.bcgogo.user.service.app.IAppUserService;
import com.bcgogo.user.service.app.IAppUserShopBindingService;
import com.bcgogo.user.service.wx.IWXMsgSender;
import com.bcgogo.user.service.wx.IWXUserService;
import com.bcgogo.utils.*;
import com.bcgogo.wx.WXConstant;
import com.bcgogo.wx.user.WXUserDTO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

/**
 * User: ZhangJuntao
 * Date: 13-8-19
 * Time: 下午3:56
 */
@Controller
public class ShopController extends DeprecatedShopController {
  private static final Logger LOG = LoggerFactory.getLogger(ShopController.class);

  /**
   * 根据关键字获取店铺建议列表
   * keywords：店铺名称关键字
   * cityCode：地图数据中的城市编号   *
   */
  @ResponseBody
  @RequestMapping(value = "/shop/suggestions/{keywords}/{cityCode}/{areaId}/{serviceScopeIds}",
    method = RequestMethod.GET)
  public ApiResponse obtainShopSuggestion(HttpServletRequest request, HttpServletResponse response,
                                          @PathVariable("keywords") String keywords,
                                          @PathVariable("cityCode") String cityCode,
                                          @PathVariable("serviceScopeIds") String serviceScopeIds,
                                          @PathVariable("areaId") String areaId
  ) throws Exception {
    return getShopSuggestion(keywords, cityCode, serviceScopeIds, areaId, SessionUtil.getAppUserDataKind(request, response));
  }

  @ResponseBody
  @RequestMapping(value = "/shop/suggestions/guest/{keywords}/{cityCode}/{areaId}/{serviceScopeIds}/{dataKind}",
    method = RequestMethod.GET)
  public ApiResponse guestObtainShopSuggestion(@PathVariable("keywords") String keywords,
                                               @PathVariable("cityCode") String cityCode,
                                               @PathVariable("serviceScopeIds") String serviceScopeIds,
                                               @PathVariable("areaId") String areaId,
                                               @PathVariable("dataKind") String dataKind) throws Exception {
    return getShopSuggestion(keywords, cityCode, serviceScopeIds, areaId, DataKind.valueOf(dataKind));
  }

  private ApiResponse getShopSuggestion(String keywords, String cityCode, String serviceScopeIds, String areaId, DataKind dataKind) {
    try {
      ApiResponse apiResponse;
      ShopSolrSearchConditionDTO condition = new ShopSolrSearchConditionDTO();
      condition.setKeyword(StringUtil.isEmptyAppGetParameter(keywords) ? null : keywords);
      condition.setCityCode((StringUtil.isEmptyAppGetParameter(cityCode) ? null : Integer.valueOf(cityCode)));
      condition.setAreaId(StringUtil.isEmptyAppGetParameter(areaId) ? null : Integer.valueOf(areaId));
      condition.setShopTypes(new String[]{ShopType.SHOP_AUTO_REPAIR.name(), ShopType.SHOP_4S.name()});
      condition.setDataKind(dataKind);
      setServiceScopeIds(serviceScopeIds, condition);
//      if (condition.getCityCode() == null && condition.getAreaId() == null) {
//        return MessageCode.toApiResponse(MessageCode.OBTAIN_SHOP_SUGGESTION_FAIL, "城市编号为空");
//      } else {
//      }
      apiResponse = MessageCode.toApiResponse(MessageCode.OBTAIN_SHOP_SUGGESTION_SUCCESS);
      ShopSearchResultListDTO listDTO = ServiceManager.getService(IShopSolrService.class).queryShopSuggestion(condition);
      return listDTO.toApiShopSuggestionResponse(apiResponse);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return MessageCode.toApiResponse(MessageCode.OBTAIN_SHOP_SUGGESTION_EXCEPTION);
    }
  }

  /**
   * 查询推荐店铺
   * coordinate：地理坐标（经纬度）    *
   * userNo：用户账号
   * shopType：
   * cityCode：百度地图城市编码       *
   * sortType：排序规则
   * shopName：用户填写的店铺名称关键字
   * pageNo：当前分页
   * pageSize：分页大小
   */
  @ResponseBody
  @RequestMapping(value = "/shop/list/guest/{dataKind}/{imageVersion}/{coordinateType}/{coordinate}/{areaId}/{serviceScopeIds}/{sortType}/{shopType}/{keywords}/{pageNo}/{pageSize}",
    method = RequestMethod.GET)
  public ApiResponse guestObtainShopList(@PathVariable("coordinate") String coordinate,
                                         @PathVariable("sortType") String sortType,
                                         @PathVariable("keywords") String keywords,
                                         @PathVariable("pageNo") Integer pageNo,
                                         @PathVariable("areaId") String areaId,
                                         @PathVariable("pageSize") Integer pageSize,
                                         @PathVariable("shopType") String shopType,
                                         @PathVariable("serviceScopeIds") String serviceScopeIds,
                                         @PathVariable("coordinateType") String coordinateType,
                                         @PathVariable("imageVersion") String imageVersion,
                                         @PathVariable("dataKind") String dataKind) throws Exception {
    try {
      ShopSolrSearchConditionDTO condition = new ShopSolrSearchConditionDTO();
      if (StringUtils.isNotBlank(coordinate)) {
        if (StringUtil.isNotEmptyAppGetParameter(coordinate)) {
          String[] coordinates = coordinate.split(",");
          condition.setLocationLon(Double.valueOf(coordinates[0]));
          condition.setLocationLat(Double.valueOf(coordinates[1]));
        }
      }
      CoordinateType type = StringUtil.isEmptyAppGetParameter(coordinateType) ? null : CoordinateType.valueOf(coordinateType);
      if (CoordinateType.LAST == type) {
        LOG.warn("游客手机端没有定位到当前位置的坐标！");
      }
      condition.setAreaId((StringUtil.isEmptyAppGetParameter(areaId) ? null : Integer.valueOf(areaId)));
      condition.sortType2Sort(StringUtil.isEmptyAppGetParameter(sortType) ? null : SortType.valueOf(sortType));
      condition.setKeyword(StringUtil.isEmptyAppGetParameter(keywords) ? null : keywords);
      condition.setStart((pageNo - 1) * pageSize);
      condition.setLimit(pageSize);
      condition.setPageNo(pageNo);
      condition.setShopTypes((shopType.equals("ALL") || StringUtil.isEmptyAppGetParameter(shopType)) ? new String[]{ShopType.SHOP_AUTO_REPAIR.name(), ShopType.SHOP_4S.name()} : new String[]{shopType});
      condition.setPageSize(pageSize);
      condition.setDataKind(DataKind.valueOf(dataKind));
      setServiceScopeIds(serviceScopeIds, condition);
      if (condition.getLocationLat() == null || condition.getLocationLon() == null) {
        return MessageCode.toApiResponse(MessageCode.OBTAIN_SHOP_LIST_FAIL, "未找到你要的店铺");
      }
      if (condition.getAreaId() == null && condition.getCityCode() == null) {
        IGeocodingService geocodingService = ServiceManager.getService(IGeocodingService.class);
        GeocoderResponse r = geocodingService.coordinateToAddress(condition.getLocationLat(), condition.getLocationLon());
        if (r == null || !r.isSuccess()) {
          return MessageCode.toApiResponse(MessageCode.OBTAIN_SHOP_LIST_FAIL, "未找到你要的店铺");
        } else {
          condition.setCityCode(r.getResult().getCityCode());
        }
      }
      condition.setIsMore(true);
      ShopSearchResultListDTO listDTO = ServiceManager.getService(IShopSolrService.class).queryShop(condition);
      ApiShopListResponse apiShopListResponse = listDTO.toApiShopListResponse(condition, MessageCode.toApiResponse(MessageCode.OBTAIN_SHOP_LIST_SUCCESS));
      //获得图片
      ServiceManager.getService(IImageService.class).addShopImageAppShopDTO(SessionUtil.getShopImageScenes(ImageVersion.valueOf(imageVersion)), true, apiShopListResponse.getShopList());
      return apiShopListResponse;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return MessageCode.toApiResponse(MessageCode.OBTAIN_SHOP_LIST_EXCEPTION);
    }
  }

  /**
   * 查询推荐店铺
   * coordinate：地理坐标（经纬度）    *
   * userNo：用户账号
   * shopType：
   * cityCode：百度地图城市编码       *
   * sortType：排序规则
   * shopName：用户填写的店铺名称关键字
   * pageNo：当前分页
   * pageSize：分页大小
   */
  @ResponseBody
  @RequestMapping(value =
    "/shop/list/{coordinateType}/{coordinate}/{serviceScopeIds}/{sortType}/{areaId}/{cityCode}/{shopType}/{keywords}/{isMore}/{pageNo}/{pageSize}",
    method = RequestMethod.GET)
  public ApiResponse obtainShopList(HttpServletRequest request, HttpServletResponse response,
                                    @PathVariable("coordinate") String coordinate,
                                    @PathVariable("areaId") String areaId,
                                    @PathVariable("cityCode") String cityCode,
                                    @PathVariable("sortType") String sortType,
                                    @PathVariable("keywords") String keywords,
                                    @PathVariable("pageNo") Integer pageNo,
                                    @PathVariable("pageSize") Integer pageSize,
                                    @PathVariable("shopType") String shopType,
                                    @PathVariable("serviceScopeIds") String serviceScopeIds,
                                    @PathVariable("isMore") String isMore,
                                    @PathVariable("coordinateType") String coordinateType) throws Exception {
    try {
      ShopSolrSearchConditionDTO condition = getShopSolrSearchConditionDTO(request, response, coordinate, areaId, cityCode, sortType, keywords, pageNo, pageSize, shopType, serviceScopeIds, coordinateType, isMore);
      return obtainShopList(request, response, condition);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return MessageCode.toApiResponse(MessageCode.OBTAIN_SHOP_LIST_EXCEPTION);
    }
  }

  protected ShopSolrSearchConditionDTO getShopSolrSearchConditionDTO
    (HttpServletRequest request, HttpServletResponse response,
     String coordinate, String areaId, String cityCode, String sortType, String keywords,
     Integer pageNo, Integer pageSize, String shopType, String serviceScopeIds, String coordinateType, String isMore) throws BcgogoException, IOException, ServletException {
    ShopSolrSearchConditionDTO condition = new ShopSolrSearchConditionDTO();
    if (StringUtils.isNotBlank(coordinate)) {
      if (StringUtil.isNotEmptyAppGetParameter(coordinate)) {
        String[] coordinates = coordinate.split(",");
        condition.setLocationLon(Double.valueOf(coordinates[0]));
        condition.setLocationLat(Double.valueOf(coordinates[1]));
      }
    }
    String appUserNo = SessionUtil.getAppUserNo(request, response);
    CoordinateType type = StringUtil.isEmptyAppGetParameter(coordinateType) ? null : CoordinateType.valueOf(coordinateType);
    if (CoordinateType.LAST == type) {
      LOG.warn("appUser:{},手机端没有定位到当前位置的坐标！", appUserNo);
    }
    condition.setIsMore(StringUtil.isEmptyAppGetParameter(isMore) ? false : Boolean.valueOf(isMore));
    condition.sortType2Sort(StringUtil.isEmptyAppGetParameter(sortType) ? null : SortType.valueOf(sortType));
    condition.setKeyword(StringUtil.isEmptyAppGetParameter(keywords) ? null : keywords);
    condition.setStart((pageNo - 1) * pageSize);
    condition.setLimit(pageSize);
    condition.setPageNo(pageNo);
    condition.setShopTypes((shopType.equals("ALL") || StringUtil.isEmptyAppGetParameter(shopType)) ? new String[]{ShopType.SHOP_AUTO_REPAIR.name(), ShopType.SHOP_4S.name()} : new String[]{shopType});
    condition.setPageSize(pageSize);
    setServiceScopeIds(serviceScopeIds, condition);
    condition.setAreaId((StringUtil.isEmptyAppGetParameter(areaId) ? null : Integer.valueOf(areaId)));
    condition.setCityCode((StringUtil.isEmptyAppGetParameter(cityCode) ? null : Integer.valueOf(cityCode)));
    condition.setAppUserNo(SessionUtil.getAppUserNo(request, response));
    return condition;
  }

  private void setServiceScopeIds(String serviceScopeIds, ShopSolrSearchConditionDTO condition) {
    String serviceScopeIdsStr = StringUtil.isEmptyAppGetParameter(serviceScopeIds) ? null : serviceScopeIds;
    if (serviceScopeIdsStr != null) {
      IServiceCategoryService serviceCategoryService = ServiceManager.getService(IServiceCategoryService.class);
      Set<Long> ids = new HashSet<Long>(Arrays.asList(NumberUtil.parseLongValueArray(serviceScopeIdsStr)));
      if (ids.size() == 1 && serviceCategoryService.isWashServiceScope(ids.iterator().next())) {
        //洗车策略
        condition.setServiceScopeWashStrategy();
      }
      ids.addAll(serviceCategoryService.getServiceCategoryChildrenIds(ids));
      condition.setServiceScopeIds(StringUtils.join(ids, ","));
    }
  }

  protected ApiResponse obtainShopList(HttpServletRequest request, HttpServletResponse response, ShopSolrSearchConditionDTO condition) throws Exception {
    if (condition.getLocationLat() == null || condition.getLocationLon() == null) {
      return MessageCode.toApiResponse(MessageCode.OBTAIN_SHOP_LIST_FAIL, "未找到你要的店铺");
    }
    if (condition.getAreaId() == null && condition.getCityCode() == null) {
      IGeocodingService geocodingService = ServiceManager.getService(IGeocodingService.class);
      GeocoderResponse r = geocodingService.coordinateToAddress(condition.getLocationLat(), condition.getLocationLon());
      if (r == null || !r.isSuccess()) {
        return MessageCode.toApiResponse(MessageCode.OBTAIN_SHOP_LIST_FAIL, "未找到你要的店铺");
      } else {
        condition.setCityCode(r.getResult().getCityCode());
      }
    }
    AppUserDTO appUserDTO = ServiceManager.getService(IAppUserService.class).getAppUserByUserNo(condition.getAppUserNo(), null);
    if (StringUtil.isEmpty(condition.getKeyword()) && !condition.getIsMore()) {
      condition.setLastExpenseShopId(appUserDTO.getLastExpenseShopId());
      condition.setRecommendShopIds(new ArrayList<Long>(ServiceManager.getService(IAppUserShopBindingService.class)
        .getVehicleIdBindingShopIdMap(condition.getAppUserNo()).values()));
      if (condition.isServiceScopeWashStrategy()) {
        List<Long> shopIds = ServiceManager.getService(IMembersService.class).getMemberCardShopIdsOfAppUser(condition.getAppUserNo());
        if (CollectionUtil.isNotEmpty(shopIds)) condition.setMemberCardShopIds(shopIds);
      }
      condition.setSpecialShopIds();
    }
    condition.setDataKind(SessionUtil.getAppUserDataKind(request, response));
    ShopSearchResultListDTO listDTO = ServiceManager.getService(IShopSolrService.class).queryShop(condition);
    ApiShopListResponse apiShopListResponse = listDTO.toApiShopListResponse(condition, MessageCode.toApiResponse(MessageCode.OBTAIN_SHOP_LIST_SUCCESS));
    //获得图片
    ServiceManager.getService(IImageService.class).addShopImageAppShopDTO(SessionUtil.getShopImageScenes(request, response), true, apiShopListResponse.getShopList());
    return apiShopListResponse;
  }

  @ResponseBody
  @RequestMapping(value =
    "/shop/list/{coordinateType}/{coordinate}/{serviceScopeIds}/{excludeShopIds}/{sortType}/{areaId}/{cityCode}/{shopType}/{keywords}/{pageNo}/{pageSize}",
    method = RequestMethod.GET)
  public ApiResponse obtainShopListExcludeShopIds(HttpServletRequest request, HttpServletResponse response,
                                                  @PathVariable("coordinate") String coordinate,
                                                  @PathVariable("areaId") String areaId,
                                                  @PathVariable("sortType") String sortType,
                                                  @PathVariable("keywords") String keywords,
                                                  @PathVariable("pageNo") Integer pageNo,
                                                  @PathVariable("pageSize") Integer pageSize,
                                                  @PathVariable("shopType") String shopType,
                                                  @PathVariable("serviceScopeIds") String serviceScopeIds,
                                                  @PathVariable("excludeShopIds") String excludeShopIds,
                                                  @PathVariable("cityCode") String cityCode,
                                                  @PathVariable("coordinateType") String coordinateType) throws Exception {
    try {
      ShopSolrSearchConditionDTO condition = getShopSolrSearchConditionDTO
        (request, response, coordinate, areaId, cityCode, sortType, keywords, pageNo, pageSize, shopType, serviceScopeIds, coordinateType, String.valueOf(false));
      addExcludeShopIds(excludeShopIds, condition);
      return obtainShopList(request, response, condition);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return MessageCode.toApiResponse(MessageCode.OBTAIN_SHOP_LIST_EXCEPTION);
    }
  }

  private void addExcludeShopIds(String excludeShopIds, ShopSolrSearchConditionDTO condition) {
    String excludeShopIdsStr = StringUtil.isEmptyAppGetParameter(excludeShopIds) ? null : excludeShopIds;
    if (excludeShopIdsStr != null) {
      Set<Long> ids = new HashSet<Long>(Arrays.asList(NumberUtil.parseLongValueArray(excludeShopIdsStr)));
      condition.getExcludeShopIds().addAll(ids);
    }
  }

  /**
   * 根据店铺ID获取店铺详情
   */
  @ResponseBody
  @RequestMapping(value = "/shop/detail/{shopId}/userNo/{userNo}",
    method = RequestMethod.GET)
  public ApiResponse obtainShopDetail(HttpServletRequest request, HttpServletResponse response,
                                      @PathVariable("shopId") Long shopId, @PathVariable("userNo") String userNo) throws Exception {

    try {
      userNo = SessionUtil.getAppUserNo(request, response);
      AppUserDTO appUserDTO = ServiceManager.getService(IAppUserService.class).getAppUserByUserNo(userNo, null);
      if (appUserDTO == null) {
        return MessageCode.toApiResponse(MessageCode.OBTAIN_SHOP_DETAIL_FAIL, ValidateMsg.APP_USER_NOT_EXIST);
      }
      ApiResponse apiResponse = ServiceManager.getService(IAppShopService.class).getShopDetail(shopId);
      if (apiResponse instanceof ApiShopResponse) {
        //评价
        AppShopDTO appShopDTO = ((ApiShopResponse) apiResponse).getShop();
        appShopDTO.from(ServiceManager.getService(ISupplierCommentService.class).getShopCommentStat(shopId));
        generateShopCommentRecord((ApiShopResponse) apiResponse, appShopDTO);

        //服务范围
        getServiceScope(appShopDTO);
        //会员
        generateMemberService(appUserDTO, appShopDTO);
        //获得图片
        List<AppShopDTO> appShopDTOList = new ArrayList<AppShopDTO>();
        appShopDTOList.add(appShopDTO);
        ServiceManager.getService(IImageService.class).addShopImageAppShopDTO(SessionUtil.getShopImageScenes(request, response), true, appShopDTOList);
      }
      return apiResponse;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return MessageCode.toApiResponse(MessageCode.OBTAIN_SHOP_DETAIL_EXCEPTION);
    }
  }

  private void generateShopCommentRecord(ApiShopResponse apiResponse, AppShopDTO appShopDTO) throws Exception {
    if (apiResponse != null && appShopDTO != null) {
      IAppShopCommentService appShopCommentService = ServiceManager.getService(IAppShopCommentService.class);
      int total = appShopCommentService.countAppShopComments(appShopDTO.getId());
      apiResponse.setCommentCount(total);
      if (total > 0) {
        Pager pager = new Pager(total, 1, 10);
        List<AppShopCommentDTO> appShopCommentDTOs = appShopCommentService.getAppShopCommentDTOs(appShopDTO.getId(), pager);
        apiResponse.setAppShopCommentDTOs(appShopCommentDTOs);
      }
    }
  }

  @ResponseBody
  @RequestMapping(value = "/shop/detail/guest/{imageVersion}/{shopId}",
    method = RequestMethod.GET)
  public ApiResponse obtainShopDetail(@PathVariable("shopId") Long shopId, @PathVariable("imageVersion") String imageVersion) throws Exception {
    try {
      ApiResponse apiResponse = ServiceManager.getService(IAppShopService.class).getShopDetail(shopId);
      if (apiResponse instanceof ApiShopResponse) {
        //评价
        AppShopDTO appShopDTO = ((ApiShopResponse) apiResponse).getShop();
        appShopDTO.from(ServiceManager.getService(ISupplierCommentService.class).getShopCommentStat(shopId));
        generateShopCommentRecord((ApiShopResponse) apiResponse, appShopDTO);
        //服务范围
        getServiceScope(appShopDTO);
        //获得图片
        List<AppShopDTO> appShopDTOList = new ArrayList<AppShopDTO>();
        appShopDTOList.add(appShopDTO);
        ServiceManager.getService(IImageService.class).addShopImageAppShopDTO(SessionUtil.getShopImageScenes(ImageVersion.valueOf(imageVersion)), true, appShopDTOList);
      }
      return apiResponse;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return MessageCode.toApiResponse(MessageCode.OBTAIN_SHOP_DETAIL_EXCEPTION);
    }
  }

  private void getServiceScope(AppShopDTO appShopDTO) {
    List<ShopServiceCategoryDTO> dtoList = ServiceManager.getService(IServiceCategoryService.class).getShopServiceCategoryDTOByShopId(appShopDTO.getId());
    StringBuilder service = new StringBuilder();
    int i = 0;
    for (ShopServiceCategoryDTO category : dtoList) {
      if (i++ != 0) {
        service.append(",");
      }
      service.append(category.getServiceCategoryName());
    }
    appShopDTO.setProductCategoryList(dtoList);
    appShopDTO.setServiceScope(service.toString());
  }

  /**
   * 1.有1个memberDTO的时候。
   * 2.多个member的时候，匹配matchVehicleCustomerIds第一个
   * 3.都不是的时候取第一个
   */
  private void generateMemberService(AppUserDTO appUserDTO, AppShopDTO appShopDTO) throws Exception {
    if (appUserDTO != null && StringUtils.isNotBlank(appUserDTO.getUserNo()) && appShopDTO != null && appShopDTO.getId() != null) {
      IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);
      IMembersService membersService = ServiceManager.getService(IMembersService.class);
      IAppUserCustomerMatchService appUserCustomerMatchService = ServiceManager.getService(IAppUserCustomerMatchService.class);
      String appUserNo = appUserDTO.getUserNo();
      Long shopId = appShopDTO.getId();
      Set<Long> allCustomerIds = new HashSet<Long>();
      MemberDTO memberDTO = null;
      Map<Long, MemberDTO> customerIdMemberDTOMap = new HashMap<Long, MemberDTO>();

      List<AppUserCustomerDTO> appUserCustomerDTOs = appUserService.getAppUserCustomerByAppUserNoAndShopId(appUserNo, shopId);
      if (CollectionUtils.isNotEmpty(appUserCustomerDTOs)) {
        for (AppUserCustomerDTO appUserCustomerDTO : appUserCustomerDTOs) {
          allCustomerIds.add(appUserCustomerDTO.getCustomerId());
        }
      }
      if (CollectionUtils.isNotEmpty(allCustomerIds)) {
        customerIdMemberDTOMap = membersService.getMemberByCustomerIdSet(appShopDTO.getId(), allCustomerIds);
      }
      if (customerIdMemberDTOMap != null && customerIdMemberDTOMap.size() == 1) {
        memberDTO = new ArrayList<MemberDTO>(customerIdMemberDTOMap.values()).get(0);
      }
      if (memberDTO == null && MapUtils.isNotEmpty(customerIdMemberDTOMap) && allCustomerIds.size() > 1 && customerIdMemberDTOMap != null) {
        Set<String> vehicleNos = appUserService.getAppVehicleNosByAppUserNo(appUserNo);
        Set<Long> matchVehicleCustomerIds = new HashSet<Long>();
        if (CollectionUtils.isNotEmpty(vehicleNos)) {
          matchVehicleCustomerIds = appUserCustomerMatchService.filterMatchVehicleCustomerIds(vehicleNos, allCustomerIds, shopId);
        }
        if (CollectionUtils.isNotEmpty(matchVehicleCustomerIds)) {
          for (MemberDTO tempMemberDTO : customerIdMemberDTOMap.values()) {
            if (tempMemberDTO != null && tempMemberDTO.getCustomerId() != null && matchVehicleCustomerIds.contains(tempMemberDTO.getCustomerId())) {
              memberDTO = tempMemberDTO;
              break;
            }
          }
        }
        if (memberDTO == null) {
          memberDTO = new ArrayList<MemberDTO>(customerIdMemberDTOMap.values()).get(0);
        }
      }
      if (memberDTO != null) {
        IMemberGenerateService memberGenerateService = ServiceManager.getService(IMemberGenerateService.class);
        MemberInfoDTO memberInfoDTO = memberGenerateService.generateAppMemberInfoDTO(memberDTO);
        appShopDTO.setMemberInfo(memberInfoDTO);
      }
    }
  }

  /**
   * 手机端用户评价单据
   * <p/>
   * 校验： 1.加锁 这个单据正在评价不能评价 2.这个单据已评价3.单据已作废
   *
   * @param shopOrderCommentDTO
   * @return
   * @throws Exception
   */
  @ResponseBody
  @RequestMapping(value = "/shop/score", method = RequestMethod.PUT)
  public ApiResponse shopOrderComment(HttpServletRequest request, HttpServletResponse response, @RequestBody ShopOrderCommentDTO shopOrderCommentDTO) throws Exception {
    try {
      ApiResponse apiResponse;
      IAppUserCommentService appUserCommentService = ServiceManager.getService(IAppUserCommentService.class);
      String result = shopOrderCommentDTO.validate();
      if (shopOrderCommentDTO.isSuccess(result)) {
        if (!(BcgogoConcurrentController.lock(ConcurrentScene.APP_ORDER_COMMENT, shopOrderCommentDTO.getOrderId()))) {
          apiResponse = new ApiResponse(MessageCode.toApiResponse(MessageCode.USER_COMMENT_ORDER_FAIL, CommentConstant.ORDER_BUSY));
          apiResponse.setDebug(shopOrderCommentDTO.toString());
          return apiResponse;
        }
        Result validateResult = appUserCommentService.validateAndSaveAppUserCommentShop(shopOrderCommentDTO);
        if (validateResult.isSuccess()) {
          apiResponse = new ApiResponse(MessageCode.toApiResponse(MessageCode.USER_COMMENT_ORDER_SUCCESS));
          apiResponse.setDebug(shopOrderCommentDTO.toString());
          return apiResponse;
        } else {
          apiResponse = MessageCode.toApiResponse(MessageCode.USER_COMMENT_ORDER_FAIL, validateResult.getMsg());
          apiResponse.setDebug(shopOrderCommentDTO.toString());
          return apiResponse;
        }
      } else {
        apiResponse = new ApiResponse(MessageCode.toApiResponse(MessageCode.USER_COMMENT_ORDER_FAIL, result));
        apiResponse.setDebug(shopOrderCommentDTO.toString());
      }
      return apiResponse;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return MessageCode.toApiResponse(MessageCode.USER_COMMENT_ORDER_EXCEPTION);
    } finally {
      if (shopOrderCommentDTO != null && shopOrderCommentDTO.getOrderId() != null) {
        BcgogoConcurrentController.release(ConcurrentScene.APP_ORDER_COMMENT, shopOrderCommentDTO.getOrderId());
      }
    }
  }


  /**
   * 手机端用户取消服务
   *
   * @param orderId
   * @return
   * @throws Exception
   */
  @ResponseBody
  @RequestMapping(value = "/service/singleService/orderId/{orderId}/userNo/{userNo}", method = RequestMethod.DELETE)
  public ApiResponse cancelOrder(HttpServletRequest request, HttpServletResponse response, @PathVariable String orderId, @PathVariable String userNo) throws Exception {
    try {
      ApiResponse apiResponse;
      userNo = SessionUtil.getAppUserNo(request, response);
      if (NumberUtil.isLongNumber(orderId) && StringUtils.isNotEmpty(userNo)) {
        IAppOrderService appOrderService = ServiceManager.getService(IAppOrderService.class);
        Result validateResult = appOrderService.appUserCancelOrder(userNo, Long.valueOf(orderId));
        if (validateResult.isSuccess()) {
          apiResponse = new ApiResponse(MessageCode.toApiResponse(MessageCode.USER_CANCEL_SERVICE_SUCCESS));
          apiResponse.setDebug(orderId + ",userNo:" + userNo);

          try {
            IAppointPushMessageService pushMessageService = ServiceManager.getService(IAppointPushMessageService.class);
            IAppointOrderService appointOrderService = ServiceManager.getService(IAppointOrderService.class);
            AppointOrderDTO appointOrderDTO = appointOrderService.getAppointOrderById((Long) validateResult.getData(), Long.valueOf(orderId));
            List<AppAppointParameter> appAppointParameterList = appointOrderService.createAppAppointParameter(appointOrderDTO);
            if (CollectionUtil.isNotEmpty(appAppointParameterList)) {
              for (AppAppointParameter appAppointParameter : appAppointParameterList) {
                pushMessageService.createAppCancelAppointMessage(appAppointParameter);
              }
            }
          } catch (Exception e) {
            LOG.error(e.getMessage(), e);
          }
          return apiResponse;
        } else {
          apiResponse = MessageCode.toApiResponse(MessageCode.USER_CANCEL_SERVICE_FAIL, validateResult.getMsg());
          apiResponse.setDebug(orderId + ",userNo:" + userNo);
          return apiResponse;
        }
      } else {
        apiResponse = new ApiResponse(MessageCode.toApiResponse(MessageCode.USER_CANCEL_SERVICE_FAIL, "单据或账号不存在"));
        apiResponse.setDebug(orderId + ",userNo:" + userNo);
      }
      return apiResponse;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return MessageCode.toApiResponse(MessageCode.USER_CANCEL_SERVICE_EXCEPTION);
    }
  }


  /**
   * 手机端用户获取单据详情
   *
   * @param orderId
   * @param serviceScope 服务类型：洗车、保养、保险、验车、维修
   * @return
   * @throws Exception
   */
  @ResponseBody
  @RequestMapping(value = "/service/historyDetail/orderId/{orderId}/serviceScope/{serviceScope}", method = RequestMethod.GET)
  public ApiResponse getOrderInfo(HttpServletRequest request, HttpServletResponse response, @PathVariable String orderId, @PathVariable String serviceScope) throws Exception {
    try {
      ApiResponse apiResponse;
      if (NumberUtil.isLongNumber(orderId)) {
        ApiOrderResponse apiOrderResponse = new ApiOrderResponse(MessageCode.toApiResponse(MessageCode.ORDER_INFO_GET_SUCCESS));

        IAppOrderService appOrderService = ServiceManager.getService(IAppOrderService.class);
        AppOrderDTO appOrderDTO = appOrderService.getAppOrderByOrderId(Long.valueOf(orderId), serviceScope);
        if (appOrderDTO == null) {
          apiResponse = new ApiOrderResponse(MessageCode.toApiResponse(MessageCode.ORDER_INFO_GET_FAIL, "单据不存在"));
          apiResponse.setDebug(orderId + "serviceScope:" + serviceScope);
          return apiResponse;
        } else {
          apiOrderResponse.setServiceDetail(appOrderDTO);
        }
        apiOrderResponse.setDebug(orderId + "serviceScope:" + serviceScope);
        return apiOrderResponse;
      } else {
        apiResponse = new ApiOrderResponse(MessageCode.toApiResponse(MessageCode.ORDER_INFO_GET_FAIL, "单据不存在"));
        apiResponse.setDebug(orderId + "serviceScope:" + serviceScope);
        return apiResponse;
      }
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return MessageCode.toApiResponse(MessageCode.ORDER_INFO_GET_EXCEPTION);
    }
  }

  /**
   * 手机端用户获取单据服务历史
   *
   * @param userNo
   * @param pageNo
   * @param pageSize
   * @param serviceScopes
   * @return
   * @throws Exception
   */
  @ResponseBody
  @RequestMapping(value = "/service/historyList/serviceScope/{serviceScopes}/status/{status}/userNo/{userNo}/pageNo/{pageNo}/pageSize/{pageSize}", method = RequestMethod.GET)
  public ApiResponse getOrderHistory(HttpServletRequest request, HttpServletResponse response, @PathVariable String userNo,
                                     @PathVariable String pageNo, @PathVariable String pageSize, @PathVariable("serviceScopes") String[] serviceScopes,
                                     @PathVariable("status") String[] status) throws Exception {
    try {
      ApiResponse apiResponse;
      AppUserType appUserType = SessionUtil.getAppUserType(request, response);
      if (NumberUtil.isNumber(pageNo) && NumberUtil.isNumber(pageSize)) {
        IAppOrderService appOrderService = ServiceManager.getService(IAppOrderService.class);
        ApiOrderHistoryResponse apiOrderResponse = appOrderService.getAppOrderHistory
          (SessionUtil.getAppUserNo(request, response), pageNo, pageSize, status, appUserType);
        apiOrderResponse.setDebug("serviceScopes:" + Arrays.toString(serviceScopes) + ",status:" + Arrays.toString(status) + ",userNo:" + userNo + ",pageNo" + pageNo + ",pageSize:" + pageSize);
        return apiOrderResponse;
      } else {
        apiResponse = new ApiOrderResponse(MessageCode.toApiResponse(MessageCode.ORDER_HISTORY_GET_FAIL, "单据不存在"));
        apiResponse.setDebug("serviceScopes:" + Arrays.toString(serviceScopes) + ",status:" + Arrays.toString(status) + ",userNo:" + userNo + ",pageNo" + pageNo + ",pageSize:" + pageSize);
      }
      return apiResponse;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return MessageCode.toApiResponse(MessageCode.ORDER_HISTORY_GET_EXCEPTION);
    }
  }

  /**
   * 手机端用户获取单据服务历史
   *
   * @param pageNo   String
   * @param pageSize String
   * @return ApiResponse
   * @throws Exception
   */
  @ResponseBody
  @RequestMapping(value = "/service/AllHistoryList/pageNo/{pageNo}/pageSize/{pageSize}", method = RequestMethod.GET)
  public ApiResponse getAllOrderHistory(HttpServletRequest request, HttpServletResponse response, @PathVariable String pageNo,
                                        @PathVariable String pageSize) throws Exception {
    try {
      ApiResponse apiResponse;
      String userNo = SessionUtil.getAppUserNo(request, response);
      AppUserType appUserType = SessionUtil.getAppUserType(request, response);
      if (NumberUtil.isNumber(pageNo) && NumberUtil.isNumber(pageSize) && StringUtils.isNotBlank(userNo)) {
        IAppOrderService appOrderService = ServiceManager.getService(IAppOrderService.class);
        apiResponse = appOrderService.getAllAppOrderHistory(userNo, NumberUtil.intValue(pageNo), NumberUtil.intValue(pageSize), appUserType);
        apiResponse.setDebug("userNo:" + userNo + ",pageNo" + pageNo + ",pageSize:" + pageSize);
        return apiResponse;
      } else {
        apiResponse = new ApiPageListResponse(MessageCode.toApiResponse(MessageCode.ORDER_HISTORY_GET_FAIL, "单据不存在"));
        apiResponse.setDebug("userNo:" + userNo + ",pageNo" + pageNo + ",pageSize:" + pageSize);
      }
      return apiResponse;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return MessageCode.toApiResponse(MessageCode.ORDER_HISTORY_GET_EXCEPTION);
    }
  }


  /**
   * 手机端用户预约服务
   *
   * @param modelMap
   * @param appServiceDTO
   * @return
   * @throws Exception
   */
  @ResponseBody
  @RequestMapping(value = "/service/appointment", method = RequestMethod.PUT)
  public ApiResponse appointService(ModelMap modelMap, @RequestBody AppServiceDTO appServiceDTO) throws Exception {
    try {
      ApiResponse apiResponse;
      String result = appServiceDTO.validate(null);
      if (appServiceDTO.isSuccess(result)) {
        IAppOrderService appOrderService = ServiceManager.getService(IAppOrderService.class);
        appServiceDTO.setAppointWay(AppointWay.APP);

        Result saveResult = appOrderService.appUserAppointOrder(appServiceDTO);
        if (saveResult.isSuccess()) {
          apiResponse = new ApiResponse(MessageCode.toApiResponse(MessageCode.USER_APPOINT_SERVICE_SUCCESS));
          apiResponse.setDebug("type:" + appServiceDTO.toString());
          try {
            IAppointPushMessageService pushMessageService = ServiceManager.getService(IAppointPushMessageService.class);
            IAppointOrderService appointOrderService = ServiceManager.getService(IAppointOrderService.class);
            AppointOrder appointOrder = (AppointOrder) saveResult.getData();
            AppointOrderDTO appointOrderDTO = appointOrderService.getAppointOrderById(appointOrder.getShopId(), appointOrder.getId());
            List<AppAppointParameter> appAppointParameterList = appointOrderService.createAppAppointParameter(appointOrderDTO);
            if (CollectionUtil.isNotEmpty(appAppointParameterList)) {
              for (AppAppointParameter appAppointParameter : appAppointParameterList) {
                pushMessageService.createAppApplyAppointMessage(appAppointParameter);
              }
            }
          } catch (Exception e) {
            LOG.error(e.getMessage(), e);
          }

        } else {
          if ("该车辆已经在服务中".equals(saveResult.getMsg())) {
            apiResponse = new ApiResponse(MessageCode.toApiResponse(MessageCode.USER_APPOINT_SERVICE_FAIL_TWO, saveResult.getMsg()));
            apiResponse.setDebug("type:" + appServiceDTO.toString());
          } else {
            apiResponse = new ApiResponse(MessageCode.toApiResponse(MessageCode.USER_APPOINT_SERVICE_FAIL, saveResult.getMsg()));
            apiResponse.setDebug("type:" + appServiceDTO.toString());
          }
        }
        return apiResponse;
      } else {
        apiResponse = new ApiResponse(MessageCode.toApiResponse(MessageCode.USER_APPOINT_SERVICE_FAIL, result));
        apiResponse.setDebug("type:" + appServiceDTO.toString());
      }
      return apiResponse;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return MessageCode.toApiResponse(MessageCode.USER_APPOINT_SERVICE_EXCEPTION);
    }
  }


  /**
   * 微信端用户预约服务
   *
   * @param modelMap
   * @param appServiceDTO
   * @return
   * @throws Exception
   */
  @ResponseBody
  @RequestMapping(value = "/wx/appointment", method = RequestMethod.POST)
  public Result wxAppointService(HttpServletRequest request, ModelMap modelMap, @RequestBody AppServiceDTO appServiceDTO) {
    try {
      Result result = new Result();
      String errMsg = appServiceDTO.validate("wx");
      if (!appServiceDTO.isSuccess(errMsg)) {
        return result.LogErrorMsg(errMsg);
      }
      appServiceDTO.setAppointTime(DateUtil.convertDateStringToDateLong(DateUtil.STANDARD, appServiceDTO.getAppointTimeStr()));
      String receiptNo = ServiceManager.getService(ITxnService.class).getReceiptNo(appServiceDTO.getShopId(), OrderTypes.APPOINT_ORDER, null);
      if (StringUtil.isEmpty(receiptNo)) return result.LogErrorMsg("单据号生成错误");
      appServiceDTO.setReceiptNo(receiptNo);
      appServiceDTO.setAppointWay(AppointWay.WECHAT);
      IAppOrderService appOrderService = ServiceManager.getService(IAppOrderService.class);
      result = appOrderService.saveWXAppointOrder(appServiceDTO);
      if (!result.isSuccess()) return result;
      String openId = appServiceDTO.getOpenId();
      //更新WXUser的mobile
      IWXUserService wxUserService = ServiceManager.getService(IWXUserService.class);
      WXUserDTO userDTO = wxUserService.getWXUserDTOByOpenId(openId);
      userDTO.setMobile(appServiceDTO.getMobile());
      wxUserService.saveOrUpdateWXUser(userDTO);
      //给微信车主推送预约消息
      String publicNo = wxUserService.getWXUserDTOByOpenId(openId).getPublicNo();
      ShopDTO shopDTO = ServiceManager.getService(IConfigService.class).getShopById(appServiceDTO.getShopId());
      String contact = StringUtil.isNotEmpty(shopDTO.getLandline()) ? shopDTO.getLandline() : shopDTO.getMobile();
      String content = "尊敬的" + appServiceDTO.getVehicleNo() + "车主,您的预约已成功提交到" + shopDTO.getName() + "。店铺咨询电话: " + contact;
      ServiceManager.getService(IWXMsgSender.class).sendCustomTextMsg(publicNo, openId, content);
      //给店铺推送预约消息
      IAppointPushMessageService pushMessageService = ServiceManager.getService(IAppointPushMessageService.class);
      IAppointOrderService appointOrderService = ServiceManager.getService(IAppointOrderService.class);
      AppointOrder appointOrder = (AppointOrder) result.getData();
      AppointOrderDTO appointOrderDTO = appointOrderService.getAppointOrderById(appointOrder.getShopId(), appointOrder.getId());
      List<AppAppointParameter> appAppointParameterList = appointOrderService.createAppAppointParameter(appointOrderDTO);
      if (CollectionUtil.isNotEmpty(appAppointParameterList)) {
        for (AppAppointParameter appAppointParameter : appAppointParameterList) {
          pushMessageService.createAppApplyAppointMessage(appAppointParameter);
        }
      }
      return result;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return new Result("预约服务暂不可用。");
    }
  }


  /**
   * 服务范围列表
   * serviceScope：      serviceScope
   */
  @ResponseBody
  @RequestMapping(value = "/serviceCategory/list/serviceScope/{serviceScope}", method = RequestMethod.GET)
  public ApiResponse obtainShopArea(@PathVariable("serviceScope") String serviceScopeStr) throws Exception {
    try {

      ApiServiceCategoryResponse response = new ApiServiceCategoryResponse(MessageCode.toApiResponse(MessageCode.OBTAIN_SERVICE_CATEGORY_SUCCESS));

      List<ServiceCategoryDTO> serviceCategoryDTOs = ServiceCategoryCache.getServiceCategoryDTOByServiceScope(serviceScopeStr);
      response.setServiceCategoryDTOList(serviceCategoryDTOs);
      response.setDebug("serviceScope:" + serviceScopeStr);
      return response;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return MessageCode.toApiResponse(MessageCode.OBTAIN_SERVICE_CATEGORY_FAIL);
    }
  }

  @ResponseBody
  @RequestMapping(value = "/shop/binding", method = RequestMethod.POST)
  public ApiResponse binding(HttpServletRequest request, HttpServletResponse response, ShopBindingDTO shopBindingDTO) throws Exception {
    try {
      shopBindingDTO.setAppUserNo(SessionUtil.getAppUserNo(request, response));
      Result result = ServiceManager.getService(IAppUserShopBindingService.class)
        .binding(shopBindingDTO);
      if (!result.isSuccess()) {
        //创建客户
        return MessageCode.toApiResponse(MessageCode.SHOP_BINDING_FAIL, result.getMsg());
      }
      createCustomerByAppUserNo(shopBindingDTO.getAppUserNo(), shopBindingDTO.getShopId());
      return MessageCode.toApiResponse(MessageCode.SHOP_BINDING_SUCCESS);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return MessageCode.toApiResponse(MessageCode.SHOP_BINDING_EXCEPTION);
    }
  }

  private void createCustomerByAppUserNo(String appUserNo, Long shopId) throws BcgogoException {
    Long customerId = ServiceManager.getService(ICustomerService.class).createOrMatchingCustomerByAppUserNo(appUserNo, shopId);
    if (customerId != null) {
      ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexCustomerByCustomerId(customerId);
    }
  }

  @ResponseBody
  @RequestMapping(value = "/shop/binding/list", method = RequestMethod.GET)
  public ApiResponse bindingList(HttpServletRequest request, HttpServletResponse response) throws Exception {
    try {
      List<ShopBindingInfo> shopBindingInfoList = ServiceManager.getService(IAppUserShopBindingService.class)
        .getBindingShop(SessionUtil.getAppUserNo(request, response));
      ApiResultResponse<List<ShopBindingInfo>> result = new ApiResultResponse<List<ShopBindingInfo>>(MessageCode.toApiResponse(MessageCode.SHOP_BINDING_SUCCESS));
      result.setResult(shopBindingInfoList);
      return result;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return MessageCode.toApiResponse(MessageCode.SHOP_BINDING_EXCEPTION);
    }
  }

  @ResponseBody
  @RequestMapping(value = "/shop/guest/comment/list/{shopId}/{pageNo}/{pageSize}", method = RequestMethod.GET)
  public ApiResponse commentList(HttpServletRequest request, HttpServletResponse response,
                                 @PathVariable("shopId") Long shopId,
                                 @PathVariable("pageNo") int pageNo,
                                 @PathVariable("pageSize") int pageSize) throws Exception {
    try {
      IAppShopCommentService appShopCommentService = ServiceManager.getService(IAppShopCommentService.class);
      return appShopCommentService.getAppShopCommentRecord(shopId, pageNo, pageSize);

    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return MessageCode.toApiResponse(MessageCode.SHOP_COMMENT_RECORD_LIST_EXCEPTION);
    }
  }

  /**
   * 手机端获取店铺宣传详情
   *
   * @param request
   * @param response
   * @param advertId
   * @return
   * @throws Exception
   */
  @ResponseBody
  @RequestMapping(value = "/advert/advertDetail/{advertId}", method = RequestMethod.GET)
  public ApiResponse getOrderInfo(HttpServletRequest request, HttpServletResponse response, @PathVariable String advertId) throws Exception {
    try {
      ApiResponse apiResponse;
      if (NumberUtil.isLongNumber(advertId)) {
        ApiShopAdvertResponse apiShopAdvertResponse = new ApiShopAdvertResponse(MessageCode.toApiResponse(MessageCode.SHOP_ADVERT_GET_SUCCESS));

        IAdvertService appOrderService = ServiceManager.getService(IAdvertService.class);

        AdvertDTO advertDTO = appOrderService.getAdvertById(Long.valueOf(advertId));
        if (advertDTO == null) {
          apiResponse = new ApiShopAdvertResponse(MessageCode.toApiResponse(MessageCode.SHOP_ADVERT_GET_FAIL, "单据不存在"));
          return apiResponse;
        } else {
          apiShopAdvertResponse.getAdvertDTOList().add(advertDTO);
        }
        apiShopAdvertResponse.setDebug("advertId:" + advertId);
        return apiShopAdvertResponse;
      } else {
        apiResponse = new ApiOrderResponse(MessageCode.toApiResponse(MessageCode.SHOP_ADVERT_GET_FAIL, "单据不存在"));
        apiResponse.setDebug("advertId:" + advertId);
        return apiResponse;
      }
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return MessageCode.toApiResponse(MessageCode.SHOP_ADVERT_GET_EXCEPTION);
    }
  }

  /**
   * 手机端分页获取店铺宣传列表
   *
   * @param request
   * @param response
   * @param pageNo
   * @param pageSize
   * @return
   * @throws Exception
   */
  @ResponseBody
  @RequestMapping(value = "/advert/advertList/{pageNo}/{pageSize}", method = RequestMethod.GET)
  public ApiResponse getShopAdvertList(HttpServletRequest request, HttpServletResponse response, @PathVariable String pageNo, @PathVariable String pageSize) throws Exception {
    try {
      ApiShopAdvertResponse apiShopAdvertResponse = new ApiShopAdvertResponse(MessageCode.toApiResponse(MessageCode.SHOP_ADVERT_GET_SUCCESS));

      if (StringUtil.isEmptyAppGetParameter(pageNo) || !NumberUtil.isLongNumber(pageNo)) {
        pageNo = "1";
      }
      if (StringUtil.isEmptyAppGetParameter(pageSize) || !NumberUtil.isLongNumber(pageSize)) {
        pageSize = "5";
      }

      String appUserNo = SessionUtil.getAppUserNo(request, response);
      IVehicleService vehicleService = ServiceManager.getService(IVehicleService.class);
      VehicleDTO vehicleDTO = vehicleService.getVehicleDTOByIMei(appUserNo);


      if (vehicleDTO != null) {
        IAdvertService advertService = ServiceManager.getService(IAdvertService.class);

        AdvertStatus[] advertStatuses = new AdvertStatus[1];
        advertStatuses[0] = AdvertStatus.ACTIVE;
        int count = advertService.countAdvertByDateStatus(vehicleDTO.getShopId(), null, null, advertStatuses);
        Pager pager = new Pager(count, Integer.valueOf(pageNo), Integer.valueOf(pageSize));

        List<AdvertDTO> advertDTOList = null;
        if (count > 0) {
          advertDTOList = advertService.getAdvertByDateStatus(vehicleDTO.getShopId(), null, null, advertStatuses, pager);
        }

        if (CollectionUtil.isNotEmpty(advertDTOList)) {

          IImageService iImageService = ServiceManager.getService(IImageService.class);
          List<ImageScene> imageSceneList = new ArrayList<ImageScene>();
          imageSceneList.add(ImageScene.SHOP_ADVERT_INFO_DESCRIPTION_SMALL_IMAGE);

          AdvertDTO[] advertDTOs = advertDTOList.toArray(new AdvertDTO[advertDTOList.size()]);

          advertDTOList = iImageService.addImageInfoToAdvertDTO(imageSceneList, false, advertDTOs);
        }


        apiShopAdvertResponse.setAdvertDTOList(advertDTOList);
        apiShopAdvertResponse.setPager(pager);
        return apiShopAdvertResponse;
      } else {
        ApiResponse apiResponse = new ApiOrderResponse(MessageCode.toApiResponse(MessageCode.SHOP_ADVERT_GET_FAIL));
        return apiResponse;
      }
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return MessageCode.toApiResponse(MessageCode.SHOP_ADVERT_GET_EXCEPTION);
    }
  }

  /**
   * 上传GPS信息，获取店铺列表
   *
   * @param request
   * @param response
   * @param lat
   * @param lon
   * @return
   */
  @ResponseBody
  @RequestMapping(value = "/shop/surrounding/lat/{lat}/lon/{lon}", method = RequestMethod.GET)
  public ApiShopsDTO getShopsByGPS(HttpServletRequest request, HttpServletResponse response,
                                   @PathVariable double lat, @PathVariable double lon) {
//    String latS = "31.488557" , lonS = "120.584905";
    StopWatchUtil sw = new StopWatchUtil("getShopsByGPS", "start");
    if (lat == 0 || lon == 0) {
      ApiShopsDTO apiShopsDTO = new ApiShopsDTO(MessageCode.toApiResponse(MessageCode.APP_SHOP_GPS_FALL));
      return apiShopsDTO;
    }
    ShopService shopService = ServiceManager.getService(ShopService.class);
    ApiShopsDTO apiShopsDTO = new ApiShopsDTO(MessageCode.toApiResponse(MessageCode.APP_SHOP_LIST_SUCCESS));
    try {
      apiShopsDTO.setData(shopService.getShopsByGPS(lat, lon));
      sw.stopAndPrintLog();
      return apiShopsDTO;
    } catch (Exception e) {
      LOG.error(e.getMessage());
      return new ApiShopsDTO(MessageCode.toApiResponse(MessageCode.APP_SHOP_EXCEPTION));
    }
  }


  /**
   * 获取流量包
   *
   * @param request
   * @param response
   * @return
   */
  @ResponseBody
  @RequestMapping(value = "/product/list", method = RequestMethod.GET)
  public ApiTrafficPackage getTrafficPackage(HttpServletRequest request, HttpServletResponse response ) throws IOException {
    ApiTrafficPackage apiTrafficPackage = new ApiTrafficPackage();
    StopWatchUtil sw = new StopWatchUtil("获取流量包", "Start");
    try {
      File f = new File(PropUtil.getProductPath());
      IShopService shopService = ServiceManager.getService(IShopService.class);
      apiTrafficPackage.setData(shopService.getTrafficPackage(f));
      apiTrafficPackage.setApiResponse(MessageCode.toApiResponse(MessageCode.APP_TRAFFIC_SUCCESS));
    } catch (Exception e) {
      LOG.error(e.getMessage());
      apiTrafficPackage.setApiResponse(MessageCode.toApiResponse(MessageCode.APP_TRAFFIC_FAIL));
    }
    sw.stopAndPrintLog();
    return apiTrafficPackage;
  }

}
