package com.bcgogo.config.service.image;

import com.bcgogo.api.*;
import com.bcgogo.api.response.ApiPageListResponse;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.dto.image.DataImageDetailDTO;
import com.bcgogo.config.dto.image.DataImageRelationDTO;
import com.bcgogo.config.dto.image.ImageErrorLogDTO;
import com.bcgogo.config.dto.image.ImageInfoDTO;
import com.bcgogo.enums.config.DataType;
import com.bcgogo.enums.config.ImageScene;
import com.bcgogo.enums.config.ImageType;
import com.bcgogo.product.BcgogoProductDTO;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.search.dto.OrderItemSearchResultDTO;
import com.bcgogo.txn.dto.*;
import com.bcgogo.txn.dto.enquiry.ShopEnquiryDTO;
import com.bcgogo.user.dto.CustomerDTO;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 13-7-29
 * Time: 上午10:59
 * To change this template use File | Settings | File Templates.
 */
public interface IImageService {
  /**
   * 批量保存DataImageRelationDTO  注意：dataType  dataId 必须都一样 也就是说 这些DataImageRelationDTO必须是同一个对象的图片关联关系
   * 保存或者更新dataImageRelationDTOs，然后会删除指定的 data对象的ImageType的无用数据
   * @param shopId
   * @param imageTypeSet
   * @param dataType
   * @param dataId
   * @param dataImageRelationDTOs
   */
  void saveOrUpdateDataImageDTOs(Long shopId,Set<ImageType> imageTypeSet,DataType dataType,Long dataId,DataImageRelationDTO... dataImageRelationDTOs) throws Exception;

  public void saveOrUpdateAppUserImages(String appUserNo, Set<ImageType> imageTypeSet, DataType dataType, Long dataId, List<AppUserImageDTO> appUserImageDTOs) throws Exception;

  /**
   * 处理询价单的图片
   *
   * @param enquiryDTO
   * @param imageSceneList
   * @throws Exception
   */
  void handleEnquiryImages(EnquiryDTO enquiryDTO, List<ImageScene> imageSceneList) throws Exception;

  /**
   * dataImageRelation ,imageInfo 同时删掉
   * @param shopId
   * @param dataImageRelationId
   */
  void deleteDataImageRelationDTOById(Long shopId,Long dataImageRelationId);

  void saveImageErrorLogDTO(ImageErrorLogDTO imageErrorLogDTO);

  Map<Long,Map<ImageScene,List<DataImageDetailDTO>>> getDataImageDetailDTO(Set<Long> shopIdSet,List<ImageScene> imageSceneList,DataType dataType, Long... dataId);

  List<ImageInfoDTO> getImageInfoDTO(Long shopId,Set<ImageType> imageTypeSet, DataType dataType, Long dataId);

  void copyProductHistoryImageDTOs(ProductHistoryDTO... productHistoryDTOs);

  void addImageInfoToBcgogoItemDTO(List<ImageScene> imageSceneList,boolean isDefaultImage, BcgogoOrderDto... bcgogoOrderDtos);
  void addImageInfoHistoryToBcgogoItemDTO(List<ImageScene> imageSceneList,boolean isDefaultImage, BcgogoOrderDto... bcgogoOrderDtos);

  void addImageInfoToProductDTO(List<ImageScene> imageSceneList,boolean isDefaultImage,ProductDTO... productDTOs);
  void addImageInfoToProductHistoryDTO(List<ImageScene> imageSceneList,boolean isDefaultImage,ProductHistoryDTO... productHistoryDTOs);
  void addImageToShoppingCartItemDTO(List<ShoppingCartItemDTO> shoppingCartItemDTOs, List<ImageScene> imageSceneList,boolean isDefaultImage);

  void addImageToShopDTO(List<ImageScene> imageSceneList, boolean isDefaultImage, ShopDTO... shopDTOs);

  void addImageToPreBuyOrderItemDTO(List<PreBuyOrderItemDTO> preBuyOrderItemDTOs, List<ImageScene> imageSceneList,boolean isDefaultImage);

  void addImageToPreBuyOrderFromProduct(PreBuyOrderDTO... preBuyOrderDTOs);

  void addImageToPreBuyOrderItemSearchResultDTO(List<OrderItemSearchResultDTO> searchResultListDTOs, List<ImageScene> imageSceneList,boolean isDefaultImage);

  void addImageToCustomerDTO(List<ImageScene> imageSceneList, boolean isDefaultImage, CustomerDTO... customerDTOs);

  void addShopImageAppShopDTO(List<ImageScene> imageSceneList, boolean isDefaultImage, List<AppShopDTO> appShopDTOList);

  void addAppUserBillImages(List<ImageScene> imageSceneList, boolean isDefaultImage, List<AppUserBillDTO> appUserBillDTOs);

  boolean isExistDataImageRelation(Long shopId, ImageType imageType, DataType dataType, Long dataId, int imageSequence);

  public void addShopImageAppOrderDTO(ImageScene imageScene, boolean isDefaultImage, ApiOrderHistoryResponse apiOrderHistoryResponse);

  public void addShopImageAppOrderDTO(ImageScene imageScene, boolean isDefaultImage, ApiPageListResponse<AppOrderDTO> apiOrderHistoryResponse);

  @Deprecated
  void initErrorImagePath() throws Exception;

  @Deprecated
  void getNotFindImageInYunByDBImagePath() throws Exception;

  void addImageToBcgogoProductDTO(List<ImageScene> imageSceneList, BcgogoProductDTO... bcgogoProductDTOs);

  //组装app端询价单图片展示
  void addAppEnquiryImage(List<EnquiryDTO> enquiryDTOs, List<ImageScene> shopImageScenes,boolean isDefaultImage);

  void addShopEnquiryImage(ShopEnquiryDTO shopEnquiryDTO,List<ImageScene> shopImageScenes,boolean isDefaultImage);

  public List<AdvertDTO> addImageInfoToAdvertDTO(List<ImageScene> imageSceneList, boolean isDefaultImage, AdvertDTO... advertDTOs);

  Map<Long,DataImageDetailDTO> getRecommendTreeImgMapByDataIds(Set<Long> recommendIds);
}
