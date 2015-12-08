package com.bcgogo.api.controller;

import com.bcgogo.api.ApiResponse;
import com.bcgogo.user.service.utils.SessionUtil;
import com.bcgogo.enums.app.MessageCode;
import com.bcgogo.enums.shop.ShopType;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.search.dto.ShopSearchResultListDTO;
import com.bcgogo.search.dto.ShopSolrSearchConditionDTO;
import com.bcgogo.search.service.shop.IShopSolrService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * User: ZhangJuntao
 * Date: 13-12-12
 * Time: 下午1:46
 */
@Deprecated
public abstract class DeprecatedShopController {
  private static final Logger LOG = LoggerFactory.getLogger(DeprecatedShopController.class);

  /**
   * @deprecated in app 2.0
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
  @RequestMapping(value = "/shop/searchList/coordinate/{coordinate}/serviceScopeIds/{serviceScopeIds}/sortType/{sortType}/areaId/{areaId}/cityCode/{cityCode}/shopType/{shopType}/keywords/{keywords}/pageNo/{pageNo}/pageSize/{pageSize}/userNo/{userNo}",
      method = RequestMethod.GET)
  public ApiResponse obtainShopList(HttpServletRequest request, HttpServletResponse response,
                                    @PathVariable("coordinate") String coordinate,
                                    @PathVariable("cityCode") String cityCode, @PathVariable("sortType") String sortType,
                                    @PathVariable("keywords") String keywords,
                                    @PathVariable("pageNo") Integer pageNo, @PathVariable("pageSize") Integer pageSize,
                                    @PathVariable("shopType") String shopType,
                                    @PathVariable("userNo") String userNo,
                                    @PathVariable("serviceScopeIds") String serviceScopeIds,
                                    @PathVariable("areaId") String areaId) throws Exception {
    try {
      ShopSolrSearchConditionDTO condition = getShopSolrSearchConditionDTO(request, response, coordinate, areaId, cityCode, sortType, keywords, pageNo, pageSize, shopType, serviceScopeIds, null, "false");
      return obtainShopList(request, response, condition);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return MessageCode.toApiResponse(MessageCode.OBTAIN_SHOP_LIST_EXCEPTION);
    }
  }

  /**
   * @deprecated in app 2.1
   */
  @ResponseBody
  @RequestMapping(value = "/shop/searchList/{coordinateType}/{coordinate}/serviceScopeIds/{serviceScopeIds}/sortType/{sortType}/areaId/{areaId}/cityCode/{cityCode}/shopType/{shopType}/keywords/{keywords}/pageNo/{pageNo}/pageSize/{pageSize}",
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
                                    @PathVariable("coordinateType") String coordinateType) throws Exception {
    try {
      ShopSolrSearchConditionDTO condition = getShopSolrSearchConditionDTO(request, response, coordinate, areaId, cityCode, sortType, keywords, pageNo, pageSize, shopType, serviceScopeIds, coordinateType, "false");
      return obtainShopList(request, response, condition);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return MessageCode.toApiResponse(MessageCode.OBTAIN_SHOP_LIST_EXCEPTION);
    }
  }

  /**
   * @deprecated in app 2.1
   */
  @ResponseBody
  @RequestMapping(value = "/shop/suggestions/keywords/{keywords}/cityCode/{cityCode}/areaId/{areaId}",
      method = RequestMethod.GET)
  public ApiResponse obtainShopSuggestion(HttpServletRequest request, HttpServletResponse response,
                                          @PathVariable("keywords") String keywords,
                                          @PathVariable("cityCode") String cityCode,
                                          @PathVariable("areaId") String areaId
  ) throws Exception {
    try {
      ApiResponse apiResponse;
      if (cityCode == null && areaId == null) {
        return MessageCode.toApiResponse(MessageCode.OBTAIN_SHOP_SUGGESTION_FAIL, "城市编号为空");
      } else {
        apiResponse = MessageCode.toApiResponse(MessageCode.OBTAIN_SHOP_SUGGESTION_SUCCESS);
        ShopSolrSearchConditionDTO condition = new ShopSolrSearchConditionDTO();
        condition.setKeyword(StringUtil.isEmptyAppGetParameter(keywords) ? null : keywords);
        condition.setCityCode((StringUtil.isEmptyAppGetParameter(cityCode) ? null : Integer.valueOf(cityCode)));
        condition.setAreaId(StringUtil.isEmptyAppGetParameter(areaId) ? null : Integer.valueOf(areaId));
        condition.setShopTypes(new String[]{ShopType.SHOP_AUTO_REPAIR.name(), ShopType.SHOP_4S.name()});
        condition.setDataKind(SessionUtil.getAppUserDataKind(request, response));
        ShopSearchResultListDTO listDTO = ServiceManager.getService(IShopSolrService.class).queryShopSuggestion(condition);
        return listDTO.toApiShopSuggestionResponse(apiResponse);
      }
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return MessageCode.toApiResponse(MessageCode.OBTAIN_SHOP_SUGGESTION_EXCEPTION);
    }
  }

  //  @Deprecated
//  private void addAppUserLastExpenseAndRecommendShop(ShopSolrSearchConditionDTO condition, AppUserDTO appUserDTO, ApiShopListResponse response) {
//    if (condition.getPageNo() == 1) {
//      condition.setShopIds(appUserDTO);
//      List<AppShopDTO> appShopDTOs = ServiceManager.getService(IShopService.class).getAppUserLastExpenseAndRecommendShop(condition);
//      //评价
//      Map<Long, CommentStatDTO> commentStatDTOMap = ServiceManager.getService(ISupplierCommentService.class)
//          .getCommentStatByShopIds(condition.getShopIds());
//      for (AppShopDTO dto : appShopDTOs) {
//        dto.from(commentStatDTOMap.get(dto.getId()));
//      }
//      if (CollectionUtil.isNotEmpty(appShopDTOs)) {
//        Iterator<AppShopDTO> iterator = response.getShopList().iterator();
//        while (iterator.hasNext()) {
//          AppShopDTO appShopDTO = iterator.next();
//          for (AppShopDTO dto : appShopDTOs) {
//            if (appShopDTO.getId().equals(dto.getId())) {
//              iterator.remove();
//              break;
//            }
//          }
//        }
//        response.getShopList().addAll(0, appShopDTOs);
//      }
//    }
//  }

  abstract protected ShopSolrSearchConditionDTO getShopSolrSearchConditionDTO
      (HttpServletRequest request, HttpServletResponse response,
       String coordinate, String areaId, String cityCode, String sortType, String keywords,
       Integer pageNo, Integer pageSize, String shopType, String serviceScopeIds, String coordinateType, String isMore) throws BcgogoException, IOException, ServletException;

  abstract protected ApiResponse obtainShopList(HttpServletRequest request, HttpServletResponse response, ShopSolrSearchConditionDTO condition) throws Exception;

}