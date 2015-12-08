package com.bcgogo.search.dto;

import com.bcgogo.api.ApiResponse;
import com.bcgogo.api.response.ApiShopListResponse;
import com.bcgogo.api.response.ApiShopSuggestionResponse;
import com.bcgogo.common.Pager;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.enums.app.ValidateMsg;
import com.bcgogo.exception.PageException;
import com.bcgogo.utils.CollectionUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * User: ZhangJuntao
 * Date: 13-8-16
 * Time: 下午3:15
 */
public class ShopSearchResultListDTO {
  private List<ShopSolrDTO> shopSolrDTOList = new ArrayList<ShopSolrDTO>();
  private List<ShopDTO> shopDTOs;
  private long numFound;

  public List<Long> getShopIdsFromSolrSearchResult() {
    List<Long> shopIds = new ArrayList<Long>();
    for (ShopSolrDTO dto : shopSolrDTOList) {
      if (dto.getId() != null) shopIds.add(dto.getId());
    }
    return shopIds;
  }

  public void mergeDBAndSolrResult(List<ShopDTO> shopDTOs) {
    if (CollectionUtil.isEmpty(shopDTOs)) {
      return;
    }
    for (ShopDTO dto : shopDTOs) {

    }
    this.setShopDTOs(shopDTOs);
  }


  public List<ShopSolrDTO> getShopSolrDTOList() {
    return shopSolrDTOList;
  }

  public void setShopSolrDTOList(List<ShopSolrDTO> shopSolrDTOList) {
    this.shopSolrDTOList = shopSolrDTOList;
  }

  public long getNumFound() {
    return numFound;
  }

  public void setNumFound(long numFound) {
    this.numFound = numFound;
  }

  public List<ShopDTO> getShopDTOs() {
    return shopDTOs;
  }

  public void setShopDTOs(List<ShopDTO> shopDTOs) {
    this.shopDTOs = shopDTOs;
  }

  public ApiResponse toApiShopSuggestionResponse(ApiResponse apiResponse) {
    ApiShopSuggestionResponse response = new ApiShopSuggestionResponse(apiResponse);
    if (CollectionUtil.isEmpty(getShopSolrDTOList())) {
      return response;
    }
    for (ShopSolrDTO dto : getShopSolrDTOList()) {
      response.getShopSuggestionList().add(dto.toAppShopSuggestion());
    }
    return response;
  }

  public ApiShopListResponse toApiShopListResponse(ShopSolrSearchConditionDTO condition, ApiResponse apiResponse) throws PageException {
    ApiShopListResponse response = new ApiShopListResponse(apiResponse);
    if (CollectionUtil.isEmpty(getShopSolrDTOList())) {
      response.setMessage(ValidateMsg.EMPTY_SHOP.getValue());
      return response;
    }
    for (ShopSolrDTO dto : getShopSolrDTOList()) {
      response.getShopList().add(dto.toAppShopDTO());
    }
    response.setPager(new Pager((int) getNumFound(), condition.getPageNo(), condition.getPageSize()));
    return response;
  }

  public List<Long> getShopIds() {
    List<Long> shopIds = new ArrayList<Long>();
    if (CollectionUtil.isEmpty(getShopSolrDTOList())) {
      for (ShopSolrDTO dto : getShopSolrDTOList()) {
        shopIds.add(dto.getId());
      }
    }
    return shopIds;
  }
}
