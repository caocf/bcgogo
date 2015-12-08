package com.bcgogo.config.service.image;

import com.bcgogo.api.*;
import com.bcgogo.api.response.ApiPageListResponse;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.dto.image.*;
import com.bcgogo.config.model.*;
import com.bcgogo.config.upyun.UpYunManager;
import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.config.util.ImageUtils;
import com.bcgogo.enums.common.ObjectStatus;
import com.bcgogo.enums.config.DataType;
import com.bcgogo.enums.config.ImageScene;
import com.bcgogo.enums.config.ImageType;
import com.bcgogo.product.BcgogoProductDTO;
import com.bcgogo.product.BcgogoProductPropertyDTO;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.search.dto.OrderItemSearchResultDTO;
import com.bcgogo.search.dto.OrderSearchResultListDTO;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.*;
import com.bcgogo.txn.dto.enquiry.ShopEnquiryDTO;
import com.bcgogo.user.dto.CustomerDTO;
import com.bcgogo.utils.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 13-7-29
 * Time: 上午11:00
 * To change this template use File | Settings | File Templates.
 */
@Component
public class ImageService implements IImageService {
  private static final Logger LOG = LoggerFactory.getLogger(ImageService.class);

  @Autowired
  private ConfigDaoManager configDaoManager;

  @Override
  public void saveOrUpdateDataImageDTOs(Long shopId,Set<ImageType> imageTypeSet,DataType dataType,Long dataId,DataImageRelationDTO... dataImageRelationDTOs) throws Exception {
    if(dataId==null) return;
    ConfigWriter writer = configDaoManager.getWriter();
    Object status = writer.begin();
    try {
      List<DataImageRelationDTO> dataImageRelationDTOList = new ArrayList<DataImageRelationDTO>(Arrays.asList(dataImageRelationDTOs));
      dataImageRelationDTOList.removeAll(Collections.singleton(null));
      Set<Long> shopIdSet = new HashSet<Long>();
      shopIdSet.add(shopId);
      if (CollectionUtils.isNotEmpty(dataImageRelationDTOList)) {
        for (DataImageRelationDTO dataImageRelationDTO : dataImageRelationDTOList) {
          if (!dataType.equals(dataImageRelationDTO.getDataType())) {
            throw new Exception("批量保存DataImageRelationDTO时候不能出现 dataType不同的DataImageRelationDTO！！！！");
          }
          if (!dataId.equals(dataImageRelationDTO.getDataId())) {
            throw new Exception("批量保存DataImageRelationDTO时候不能出现 dataId不同的DataImageRelationDTO！！！！");
          }
        }
      }
      //找到需要更新和删除的db 数据
      List<Object[]> objectsList = writer.getDataImageRelation(shopIdSet, null, imageTypeSet, dataType, dataId);
      if (CollectionUtils.isNotEmpty(objectsList)) {
        if (CollectionUtils.isNotEmpty(dataImageRelationDTOList)) {
          for (DataImageRelationDTO dataImageRelationDTO : dataImageRelationDTOs) {
            //找出path一样的，更新sequence，或者新增
            if (dataImageRelationDTO.getId() == null && dataImageRelationDTO.getImageInfoDTO() != null) {
              boolean isExist = false;
              Iterator<Object[]> iterator = objectsList.iterator();
              while (iterator.hasNext()) {
                Object[] objects = iterator.next();
                if (!ArrayUtils.isEmpty(objects) && objects.length == 2 && objects[0] != null && objects[1] != null) {
                  DataImageRelation dataImageRelation = (DataImageRelation) objects[0];
                  ImageInfo imageInfo = (ImageInfo) objects[1];
                  if (imageInfo.getPath().equals(dataImageRelationDTO.getImageInfoDTO().getPath())) {//更新ImageSequence
                    dataImageRelation.setImageSequence(dataImageRelationDTO.getImageSequence());
                    dataImageRelation.setImageType(dataImageRelationDTO.getImageType());
                    writer.update(dataImageRelation);
                    dataImageRelationDTO.setId(dataImageRelation.getId());
                    dataImageRelationDTO.setImageId(imageInfo.getId());
                    iterator.remove();
                    isExist = true;
                    break;
                  }
                }
              }
              if (!isExist) {
                ImageInfo imageInfo = new ImageInfo();
                imageInfo.fromDTO(dataImageRelationDTO.getImageInfoDTO());
                writer.save(imageInfo);
                dataImageRelationDTO.setImageId(imageInfo.getId());
                DataImageRelation dataImageRelation = new DataImageRelation();
                dataImageRelation.fromDTO(dataImageRelationDTO);
                writer.save(dataImageRelation);
                dataImageRelationDTO.setId(dataImageRelation.getId());
                dataImageRelationDTO.setImageId(imageInfo.getId());
              }
            }
          }
        }
        //删除废弃的
        if (CollectionUtils.isNotEmpty(objectsList)) {
          for (Object[] objects : objectsList) {
            if (!ArrayUtils.isEmpty(objects) && objects.length == 2 && objects[0] != null && objects[1] != null) {
              DataImageRelation dataImageRelation = (DataImageRelation) objects[0];
              ImageInfo imageInfo = (ImageInfo) objects[1];
              dataImageRelation.setStatus(ObjectStatus.DISABLED);
              imageInfo.setStatus(ObjectStatus.DISABLED);
              writer.update(dataImageRelation);
              writer.update(imageInfo);
            }
          }
        }
      } else {
        if (CollectionUtils.isNotEmpty(dataImageRelationDTOList)) {
          for (DataImageRelationDTO dataImageRelationDTO : dataImageRelationDTOs) {
            if (dataImageRelationDTO.getId() == null && dataImageRelationDTO.getImageInfoDTO() != null) {
              ImageInfo imageInfo = new ImageInfo();
              imageInfo.fromDTO(dataImageRelationDTO.getImageInfoDTO());
              writer.save(imageInfo);
              dataImageRelationDTO.setImageId(imageInfo.getId());
              DataImageRelation dataImageRelation = new DataImageRelation();
              dataImageRelation.fromDTO(dataImageRelationDTO);
              writer.save(dataImageRelation);
              dataImageRelationDTO.setId(dataImageRelation.getId());
              dataImageRelationDTO.setImageId(imageInfo.getId());
            }
          }
        }
      }
      writer.commit(status);

    } finally {
      writer.rollback(status);
    }
  }

  public void saveOrUpdateAppUserImages(String appUserNo, Set<ImageType> imageTypeSet, DataType dataType, Long dataId,
                                        List<AppUserImageDTO> appUserImageDTOs) throws Exception {
    ConfigWriter writer = configDaoManager.getWriter();
    Object status = writer.begin();
    try {
      appUserImageDTOs.removeAll(Collections.singleton(null));
      //找到需要更新和删除的db 数据
      Set<String> appUserNos = new HashSet<String>();
      appUserNos.add(appUserNo);
      List<Object[]> objectsList = writer.getAppUserDataImageRelation(appUserNos, imageTypeSet, dataType, dataId);
      if (CollectionUtils.isNotEmpty(objectsList)) {
        if (CollectionUtils.isNotEmpty(appUserImageDTOs)) {
          for (AppUserImageDTO appUserImageDTO : appUserImageDTOs) {
            //找出path一样的，更新sequence，或者新增
            if (StringUtils.isNotEmpty(appUserImageDTO.getImagePath())) {
              boolean isExist = false;
              Iterator<Object[]> iterator = objectsList.iterator();
              while (iterator.hasNext()) {
                Object[] objects = iterator.next();
                if (!ArrayUtils.isEmpty(objects) && objects.length == 2 && objects[0] != null && objects[1] != null) {
                  DataImageRelation dataImageRelation = (DataImageRelation) objects[0];
                  ImageInfo imageInfo = (ImageInfo) objects[1];
                  if (imageInfo.getPath().equals(appUserImageDTO.getImagePath())) {//更新ImageSequence
                    dataImageRelation.setImageSequence(appUserImageDTO.getSequence());
                    dataImageRelation.setImageType(appUserImageDTO.getImageType());
                    writer.update(dataImageRelation);
                    appUserImageDTO.setImageId(dataImageRelation.getId());
                    iterator.remove();
                    isExist = true;
                    break;
                  }
                }
              }
              if (!isExist) {
                ImageInfo imageInfo = new ImageInfo();
                imageInfo.fromAppUserImageDTO(appUserNo, appUserImageDTO);
                writer.save(imageInfo);
                DataImageRelation dataImageRelation = new DataImageRelation();
                dataImageRelation.fromAppUserImageDTO(appUserNo, dataId, appUserImageDTO, dataType);
                dataImageRelation.setImageId(imageInfo.getId());
                writer.save(dataImageRelation);
                appUserImageDTO.setImageId(dataImageRelation.getId());
              }
            }
          }
        }
        //删除废弃的
        if (CollectionUtils.isNotEmpty(objectsList)) {
          for (Object[] objects : objectsList) {
            if (!ArrayUtils.isEmpty(objects) && objects.length == 2 && objects[0] != null && objects[1] != null) {
              DataImageRelation dataImageRelation = (DataImageRelation) objects[0];
              ImageInfo imageInfo = (ImageInfo) objects[1];
              dataImageRelation.setStatus(ObjectStatus.DISABLED);
              imageInfo.setStatus(ObjectStatus.DISABLED);
              writer.update(dataImageRelation);
              writer.update(imageInfo);
            }
          }
        }
      } else {
        if (CollectionUtils.isNotEmpty(appUserImageDTOs)) {
          for (AppUserImageDTO appUserImageDTO : appUserImageDTOs) {
            if (appUserImageDTO.getImageId() == null && StringUtils.isNotEmpty(appUserImageDTO.getImagePath())) {
              ImageInfo imageInfo = new ImageInfo();
              imageInfo.fromAppUserImageDTO(appUserNo, appUserImageDTO);
              writer.save(imageInfo);
              DataImageRelation dataImageRelation = new DataImageRelation();
              dataImageRelation.fromAppUserImageDTO(appUserNo, dataId, appUserImageDTO, dataType);
              dataImageRelation.setImageId(imageInfo.getId());
              writer.save(dataImageRelation);
              appUserImageDTO.setImageId(dataImageRelation.getId());
            }
          }
        }
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }


  @Override
  public void handleEnquiryImages(EnquiryDTO enquiryDTO,List<ImageScene> imageSceneList) throws Exception {
    List<AppUserImageDTO> appUserImageDTOs = new ArrayList<AppUserImageDTO>();
    if (enquiryDTO != null && enquiryDTO.haveEnquiryImage() && enquiryDTO.getId() != null) {
      int sequence = 0;
      for (AppUserImageDTO enquiryImage : enquiryDTO.getEnquiryImages()) {
        if (enquiryImage != null && enquiryImage.validate()) {
          enquiryImage.setSequence(sequence);
          enquiryImage.setImageType(sequence == 0 ? ImageType.ENQUIRY_ORDER_MAIN_IMAGE : ImageType.ENQUIRY_ORDER_AUXILIARY_IMAGE);
          appUserImageDTOs.add(enquiryImage);
          sequence++;
        }
      }
      if (CollectionUtils.isNotEmpty(appUserImageDTOs)) {
        Set<ImageType> imageTypes = new HashSet<ImageType>();
        imageTypes.add(ImageType.ENQUIRY_ORDER_MAIN_IMAGE);
        imageTypes.add(ImageType.ENQUIRY_ORDER_AUXILIARY_IMAGE);
        saveOrUpdateAppUserImages(enquiryDTO.getAppUserNo(), imageTypes, DataType.APP_ENQUIRY, enquiryDTO.getId(), appUserImageDTOs);
      }
      if (CollectionUtils.isNotEmpty(imageSceneList) && CollectionUtils.isNotEmpty(appUserImageDTOs)) {
        for (AppUserImageDTO appUserImageDTO : appUserImageDTOs) {
          setAppUserImageDTOURL(imageSceneList, appUserImageDTO);
        }
      }
      enquiryDTO.setEnquiryImages(appUserImageDTOs.toArray(new AppUserImageDTO[appUserImageDTOs.size()]));
    }
  }

   //用户上传的图片
  private void setAppUserImageDTOURL(List<ImageScene> imageSceneList, AppUserImageDTO appUserImageDTO) {
    if (CollectionUtils.isNotEmpty(imageSceneList) && appUserImageDTO != null && appUserImageDTO.validate()) {
      for (ImageScene imageScene : imageSceneList) {
        switch (imageScene) {
          //手机端 小图  5种尺寸
          case SHOP_IMAGE_APP_480X800_SMALL:
          case SHOP_IMAGE_APP_720X1280_SMALL:
          case SHOP_IMAGE_APP_640X1136_SMALL:
          case SHOP_IMAGE_APP_640X960_SMALL:
          case SHOP_IMAGE_APP_320X480_SMALL: {
            appUserImageDTO.setSmallImageUrl(ImageUtils.generateUpYunImagePath(appUserImageDTO.getImagePath(), imageScene.getImageVersion()));
            break;
          }

          //手机端 大图 5种尺寸
          case SHOP_IMAGE_APP_480X800_FULL_SCREEN:
          case SHOP_IMAGE_APP_720X1280_FULL_SCREEN:
          case SHOP_IMAGE_APP_640X960_FULL_SCREEN:
          case SHOP_IMAGE_APP_640X1136_FULL_SCREEN:
          case SHOP_IMAGE_APP_320X480_FULL_SCREEN: {
            appUserImageDTO.setBigImageUrl(ImageUtils.generateUpYunImagePath(appUserImageDTO.getImagePath(), imageScene.getImageVersion()));
            break;
          }
          case SHOP_ENQUIRY_APP_SMALL:
            appUserImageDTO.setSmallImageUrl(ImageUtils.generateUpYunImagePath(appUserImageDTO.getImagePath(), imageScene.getImageVersion()));
            break;
          case SHOP_ENQUIRY_APP_FULL:
            appUserImageDTO.setBigImageUrl(ImageUtils.generateUpYunImagePath(appUserImageDTO.getImagePath(), imageScene.getImageVersion()));
            break;
          default:
        }
      }
    }
  }

  //默认图片
  private AppUserImageDTO generateAppUserDefaultImage(List<ImageScene> imageScenes) {
    AppUserImageDTO appUserImageDTO = new AppUserImageDTO();
    for (ImageScene imageScene : imageScenes) {
      switch (imageScene) {
        //手机端 小图  5种尺寸
        case SHOP_IMAGE_APP_480X800_SMALL:
        case SHOP_IMAGE_APP_720X1280_SMALL:
        case SHOP_IMAGE_APP_640X1136_SMALL:
        case SHOP_IMAGE_APP_640X960_SMALL:
        case SHOP_IMAGE_APP_320X480_SMALL: {
          appUserImageDTO.setSmallImageUrl(ImageUtils.generateNotFindImageUrl() + ConfigUtils.getUpYunSeparator() + imageScene.getImageVersion());
          break;
        }

        //手机端 大图 5种尺寸
        case SHOP_IMAGE_APP_480X800_FULL_SCREEN:
        case SHOP_IMAGE_APP_720X1280_FULL_SCREEN:
        case SHOP_IMAGE_APP_640X960_FULL_SCREEN:
        case SHOP_IMAGE_APP_640X1136_FULL_SCREEN:
        case SHOP_IMAGE_APP_320X480_FULL_SCREEN: {
          appUserImageDTO.setBigImageUrl(ImageUtils.generateNotFindImageUrl() + ConfigUtils.getUpYunSeparator() + imageScene.getImageVersion());
          break;
        }
        default:
      }
    }
    return appUserImageDTO;
  }

  @Override
  public void deleteDataImageRelationDTOById(Long shopId,Long dataImageRelationId) {
    ConfigWriter writer = configDaoManager.getWriter();
    Object status = writer.begin();
    try {
      DataImageRelation dataImageRelation = writer.getById(DataImageRelation.class,dataImageRelationId);
      if(dataImageRelation!=null&&dataImageRelation.getShopId().equals(shopId)){
        ImageInfo imageInfo = writer.getById(ImageInfo.class,dataImageRelation.getImageId());
        dataImageRelation.setStatus(ObjectStatus.DISABLED);
        imageInfo.setStatus(ObjectStatus.DISABLED);
        writer.saveOrUpdate(dataImageRelation);
        writer.saveOrUpdate(imageInfo);
        writer.commit(status);
      }
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public void saveImageErrorLogDTO(ImageErrorLogDTO imageErrorLogDTO) {
    ConfigWriter writer = configDaoManager.getWriter();
    Object status = writer.begin();
    try {
      ImageErrorLog imageErrorLog = new ImageErrorLog();
      imageErrorLog.fromDTO(imageErrorLogDTO);
      writer.save(imageErrorLog);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public boolean isExistDataImageRelation(Long shopId,ImageType imageType,DataType dataType, Long dataId,int imageSequence){
    ConfigWriter writer = configDaoManager.getWriter();
    Object[] objects = writer.getDataImageRelation(shopId,imageType,dataType,dataId,imageSequence);
    if(ArrayUtils.isEmpty(objects)){
      return false;
    }
    return true;
  }

  @Override
  public Map<Long, Map<ImageScene, List<DataImageDetailDTO>>> getDataImageDetailDTO(Set<Long> shopIdSet, List<ImageScene> imageSceneList, DataType dataType, Long... dataId) {
    return getDataImageDetailDTO(shopIdSet, null, imageSceneList, dataType, dataId);
  }

  private Map<Long, Map<ImageScene, List<DataImageDetailDTO>>> getDataImageDetailDTO(Set<Long> shopIdSet, Set<String> appUserNoSet, List<ImageScene> imageSceneList, DataType dataType, Long[] dataId) {
    Map<Long, Map<ImageScene, List<DataImageDetailDTO>>> result = new HashMap<Long, Map<ImageScene, List<DataImageDetailDTO>>>();
    if ((CollectionUtils.isEmpty(shopIdSet) && CollectionUtils.isEmpty(appUserNoSet)) || CollectionUtils.isEmpty(imageSceneList) || ArrayUtils.isEmpty(dataId))
      return result;
    Set<ImageType> imageTypeSet = ImageType.getImageTypeListByImageScene(imageSceneList.toArray(new ImageScene[imageSceneList.size()]));
    if (CollectionUtils.isEmpty(imageTypeSet)) return result;

    ConfigWriter writer = configDaoManager.getWriter();

    List<Object[]> objectsList = writer.getDataImageRelation(shopIdSet, appUserNoSet, imageTypeSet, dataType, dataId);
    if (CollectionUtils.isNotEmpty(objectsList)) {
      List<DataImageDetailDTO> dataImageDetailDTOList = null;
      DataImageRelation dataImageRelation = null;
      ImageInfo imageInfo = null;
      for (Object[] objects : objectsList) {
        if (!ArrayUtils.isEmpty(objects) && objects.length == 2 && objects[0] != null && objects[1] != null) {
          for (ImageScene imageScene : imageSceneList) {
            dataImageRelation = (DataImageRelation) objects[0];
            imageInfo = (ImageInfo) objects[1];
            if (ImageType.getImageTypeListByImageScene(imageScene).contains(dataImageRelation.getImageType())) {
              Map<ImageScene, List<DataImageDetailDTO>> sceneMap = result.get(dataImageRelation.getDataId());
              if (sceneMap == null) {
                sceneMap = new HashMap<ImageScene, List<DataImageDetailDTO>>();
                dataImageDetailDTOList = new ArrayList<DataImageDetailDTO>();
              } else {
                dataImageDetailDTOList = sceneMap.get(imageScene);
                if (dataImageDetailDTOList == null) {
                  dataImageDetailDTOList = new ArrayList<DataImageDetailDTO>();
                }
              }
              dataImageDetailDTOList.add(new DataImageDetailDTO(dataImageRelation.toDTO(), ImageUtils.generateUpYunImagePath(imageInfo.getPath(), imageScene.getImageVersion()), imageInfo.getPath()));
              sceneMap.put(imageScene, dataImageDetailDTOList);
              result.put(dataImageRelation.getDataId(), sceneMap);
            }
          }
        }
      }
    }
    return result;
  }

  @Override
  public List<ImageInfoDTO> getImageInfoDTO(Long shopId, Set<ImageType> imageTypeSet, DataType dataType, Long dataId) {
    List<ImageInfoDTO> result = new ArrayList<ImageInfoDTO>();
    if(shopId==null || CollectionUtils.isEmpty(imageTypeSet) || dataId==null) return result;

    ConfigWriter writer = configDaoManager.getWriter();

    List<ImageInfo> imageInfoList = writer.getDataImageInfoList(shopId, imageTypeSet, dataType, dataId);
    if(CollectionUtils.isNotEmpty(imageInfoList)){
      for(ImageInfo imageInfo:imageInfoList){
        result.add(imageInfo.toDTO());
      }
    }
    return result;
  }

  @Override
  public void copyProductHistoryImageDTOs(ProductHistoryDTO... productHistoryDTOs){
    if(ArrayUtils.isEmpty(productHistoryDTOs)) return;
    ConfigWriter writer = configDaoManager.getWriter();
    Set<Long> productShopIdSet = new HashSet<Long>();
    Map<Long,ProductHistoryDTO> productHistoryDTOMap = new HashMap<Long, ProductHistoryDTO>();
    for(ProductHistoryDTO productHistoryDTO:productHistoryDTOs){
      productShopIdSet.add(productHistoryDTO.getShopId());
      productHistoryDTOMap.put(productHistoryDTO.getProductLocalInfoId(),productHistoryDTO);
    }
    List<Object[]> objectsList = writer.getDataImageRelation(productShopIdSet, null, ImageType.getAllProductImageTypeSet(), DataType.PRODUCT, productHistoryDTOMap.keySet().toArray(new Long[productHistoryDTOMap.keySet().size()]));
    if(CollectionUtils.isNotEmpty(objectsList)){
      Object status = writer.begin();
      try {
        DataImageRelation pDataImageRelation = null,phDataImageRelation = null;
        ImageInfo pImageInfo = null,phImageInfo = null;
        for(Object[] objects:objectsList){
          if(!ArrayUtils.isEmpty(objects) && objects.length==2 && objects[0]!=null && objects[1]!=null){
            pDataImageRelation = (DataImageRelation)objects[0];
            pImageInfo = (ImageInfo)objects[1];
            DataImageRelationDTO dataImageRelationDTO = pDataImageRelation.toDTO();
            dataImageRelationDTO.setDataId(productHistoryDTOMap.get(dataImageRelationDTO.getDataId()).getId());//dataId  修改为 product_history_id
            dataImageRelationDTO.setId(null);
            dataImageRelationDTO.setImageId(null);
            dataImageRelationDTO.setDataType(DataType.PRODUCT_HISTORY);
            phDataImageRelation = new DataImageRelation();
            phDataImageRelation.fromDTO(dataImageRelationDTO);
            ImageInfoDTO imageInfoDTO = pImageInfo.toDTO();
            imageInfoDTO.setId(null);
            imageInfoDTO.setCreatedTime(System.currentTimeMillis());
            phImageInfo = new ImageInfo();
            phImageInfo.fromDTO(imageInfoDTO);

            writer.save(phImageInfo);
            phDataImageRelation.setImageId(phImageInfo.getId());
            writer.save(phDataImageRelation);
          }
        }
        writer.commit(status);
      } finally {
        writer.rollback(status);
      }
    }
  }

  @Override
  public void addImageInfoHistoryToBcgogoItemDTO(List<ImageScene> imageSceneList,boolean isDefaultImage, BcgogoOrderDto... bcgogoOrderDtos){
    List<BcgogoOrderDto> bcgogoOrderDtoList = new ArrayList<BcgogoOrderDto>(Arrays.asList(bcgogoOrderDtos));
    bcgogoOrderDtoList.removeAll(Collections.singleton(null));

    if(CollectionUtils.isEmpty(bcgogoOrderDtoList) || CollectionUtils.isEmpty(imageSceneList)) return;
    Set<Long> dataShopIdSet = new HashSet<Long>();
    Set<Long> productHistoryIdSet = new HashSet<Long>();
    for(BcgogoOrderDto bcgogoOrderDto:bcgogoOrderDtoList){
      for(BcgogoOrderItemDto itemDto:bcgogoOrderDto.getItemDTOs()){
        if (bcgogoOrderDto instanceof PurchaseOrderDTO && ((PurchaseOrderDTO) bcgogoOrderDto).isWholesalerPurchase()) {
          dataShopIdSet.add(((PurchaseOrderDTO) bcgogoOrderDto).getSupplierShopId());
        }else{
          dataShopIdSet.add(bcgogoOrderDto.getShopId());
        }
        productHistoryIdSet.add(itemDto.getProductHistoryId());
      }
    }

    Map<Long,Map<ImageScene,List<DataImageDetailDTO>>> imageMap = getDataImageDetailDTO(dataShopIdSet, imageSceneList, DataType.PRODUCT_HISTORY, productHistoryIdSet.toArray(new Long[productHistoryIdSet.size()]));
    for(BcgogoOrderDto bcgogoOrderDto:bcgogoOrderDtoList){
      for(BcgogoOrderItemDto itemDto:bcgogoOrderDto.getItemDTOs()){
        itemDto.setImageCenterDTO(ImageUtils.generateCommonImageDetail(imageSceneList, imageMap.get(itemDto.getProductHistoryId()), isDefaultImage));
      }
    }
  }

  @Override
  public void addImageInfoToBcgogoItemDTO(List<ImageScene> imageSceneList,boolean isDefaultImage, BcgogoOrderDto... bcgogoOrderDtos){
    List<BcgogoOrderDto> bcgogoOrderDtoList = new ArrayList<BcgogoOrderDto>(Arrays.asList(bcgogoOrderDtos));
    bcgogoOrderDtoList.removeAll(Collections.singleton(null));

    if(CollectionUtils.isEmpty(bcgogoOrderDtoList) || CollectionUtils.isEmpty(imageSceneList)) return;
    Set<Long> dataShopIdSet = new HashSet<Long>();
    Set<Long> productIdSet = new HashSet<Long>();
    for(BcgogoOrderDto bcgogoOrderDto:bcgogoOrderDtoList){
      for(BcgogoOrderItemDto itemDto:bcgogoOrderDto.getItemDTOs()){
        if (bcgogoOrderDto instanceof PurchaseOrderDTO && ((PurchaseOrderDTO) bcgogoOrderDto).isWholesalerPurchase()) {
          dataShopIdSet.add(((PurchaseOrderDTO) bcgogoOrderDto).getSupplierShopId());
          productIdSet.add(((PurchaseOrderItemDTO) itemDto).getSupplierProductId());
        }else{
          dataShopIdSet.add(bcgogoOrderDto.getShopId());
          productIdSet.add(itemDto.getProductId());
        }

      }
    }

    Map<Long,Map<ImageScene,List<DataImageDetailDTO>>> imageMap = getDataImageDetailDTO(dataShopIdSet, imageSceneList, DataType.PRODUCT, productIdSet.toArray(new Long[productIdSet.size()]));
    for(BcgogoOrderDto bcgogoOrderDto:bcgogoOrderDtoList){
      for(BcgogoOrderItemDto itemDto:bcgogoOrderDto.getItemDTOs()){
        if (bcgogoOrderDto instanceof PurchaseOrderDTO && ((PurchaseOrderDTO) bcgogoOrderDto).isWholesalerPurchase()) {
          itemDto.setImageCenterDTO(ImageUtils.generateCommonImageDetail(imageSceneList, imageMap.get(((PurchaseOrderItemDTO) itemDto).getSupplierProductId()), isDefaultImage));
        }else{
          itemDto.setImageCenterDTO(ImageUtils.generateCommonImageDetail(imageSceneList, imageMap.get(itemDto.getProductId()), isDefaultImage));
        }
      }
    }
  }
  @Override
  public void addImageInfoToProductDTO(List<ImageScene> imageSceneList,boolean isDefaultImage,ProductDTO... productDTOs){
    if(ArrayUtil.isEmpty(productDTOs)){
      return;
    }
    List<ProductDTO> productDTOList = new ArrayList<ProductDTO>(Arrays.asList(productDTOs));
    productDTOList.removeAll(Collections.singleton(null));

    if(CollectionUtils.isEmpty(productDTOList) || CollectionUtils.isEmpty(imageSceneList)) return;
    Set<Long> dataShopIdSet = new HashSet<Long>();
    Set<Long> dataIdSet = new HashSet<Long>();
    for(ProductDTO productDTO:productDTOList){
      dataShopIdSet.add(productDTO.getShopId());
      dataIdSet.add(productDTO.getProductLocalInfoId());
    }
    Map<Long,Map<ImageScene,List<DataImageDetailDTO>>> imageMap = getDataImageDetailDTO(dataShopIdSet, imageSceneList, DataType.PRODUCT, dataIdSet.toArray(new Long[dataIdSet.size()]));
    for(ProductDTO productDTO:productDTOList){
      productDTO.setImageCenterDTO(ImageUtils.generateCommonImageDetail(imageSceneList, imageMap.get(productDTO.getProductLocalInfoId()),isDefaultImage));
    }
  }
  @Override
  public void addImageInfoToProductHistoryDTO(List<ImageScene> imageSceneList,boolean isDefaultImage,ProductHistoryDTO... productHistoryDTOs){
    List<ProductHistoryDTO> productHistoryDTOList = new ArrayList<ProductHistoryDTO>(Arrays.asList(productHistoryDTOs));
    productHistoryDTOList.removeAll(Collections.singleton(null));

    if(CollectionUtils.isEmpty(productHistoryDTOList) || CollectionUtils.isEmpty(imageSceneList)) return;
    Set<Long> dataShopIdSet = new HashSet<Long>();
    Set<Long> dataIdSet = new HashSet<Long>();
    for(ProductHistoryDTO productHistoryDTO:productHistoryDTOList){
      dataShopIdSet.add(productHistoryDTO.getShopId());
      dataIdSet.add(productHistoryDTO.getId());
    }
    Map<Long,Map<ImageScene,List<DataImageDetailDTO>>> imageMap = getDataImageDetailDTO(dataShopIdSet, imageSceneList, DataType.PRODUCT_HISTORY, dataIdSet.toArray(new Long[dataIdSet.size()]));
    for(ProductHistoryDTO productHistoryDTO:productHistoryDTOList){
      productHistoryDTO.setImageCenterDTO(ImageUtils.generateCommonImageDetail(imageSceneList, imageMap.get(productHistoryDTO.getId()),isDefaultImage));
    }
  }
  @Override
  public void addImageToShoppingCartItemDTO(List<ShoppingCartItemDTO> shoppingCartItemDTOs, List<ImageScene> imageSceneList,boolean isDefaultImage) {
    Set<Long> dataShopIdSet = new HashSet<Long>();
    Set<Long> dataIdSet = new HashSet<Long>();
    shoppingCartItemDTOs.removeAll(Collections.singleton(null));
    if(CollectionUtils.isNotEmpty(shoppingCartItemDTOs)){

      for(ShoppingCartItemDTO shoppingCartItemDTO:shoppingCartItemDTOs){
        dataShopIdSet.add(shoppingCartItemDTO.getSupplierShopId());
        dataIdSet.add(shoppingCartItemDTO.getProductLocalInfoId());
      }
      Map<Long,Map<ImageScene,List<DataImageDetailDTO>>> imageMap = getDataImageDetailDTO(dataShopIdSet, imageSceneList, DataType.PRODUCT, dataIdSet.toArray(new Long[dataIdSet.size()]));
      for(ShoppingCartItemDTO shoppingCartItemDTO:shoppingCartItemDTOs){
        shoppingCartItemDTO.setImageCenterDTO(ImageUtils.generateCommonImageDetail(imageSceneList, imageMap.get(shoppingCartItemDTO.getProductLocalInfoId()), isDefaultImage));
      }
    }
  }


  @Override
  public void addImageToPreBuyOrderFromProduct(PreBuyOrderDTO... preBuyOrderDTOs){
    if(ArrayUtil.isEmpty(preBuyOrderDTOs)) return;
    Set<Long> shopIdSet = new HashSet<Long>();
    List<Long> dataIds=new ArrayList<Long>();
    for (PreBuyOrderDTO preBuyOrderDTO : preBuyOrderDTOs) {
      if (preBuyOrderDTO != null && preBuyOrderDTO.getShopId() != null) {
        shopIdSet.add(preBuyOrderDTO.getShopId());
        if (ArrayUtil.isNotEmpty(preBuyOrderDTO.getItemDTOs())) {
          for (PreBuyOrderItemDTO itemDTO : preBuyOrderDTO.getItemDTOs()) {
            dataIds.add(itemDTO.getProductId());
          }
        }
      }
    }
    List<ImageScene> imageSceneList=new ArrayList<ImageScene>();
    imageSceneList.add(ImageScene.PRODUCT_LIST_IMAGE_SMALL);
    Map<Long,Map<ImageScene,List<DataImageDetailDTO>>> imageMap = getDataImageDetailDTO(shopIdSet, imageSceneList, DataType.PRODUCT, dataIds.toArray(new Long[dataIds.size()]));
    for(PreBuyOrderDTO orderDTO:preBuyOrderDTOs){
      if(orderDTO != null && ArrayUtil.isNotEmpty(orderDTO.getItemDTOs())){
        for(PreBuyOrderItemDTO itemDTO:orderDTO.getItemDTOs()){
         ImageCenterDTO imageCenterDTO=ImageUtils.generateCommonImageDetail(imageSceneList, imageMap.get(itemDTO.getProductId()), false);
          if(imageCenterDTO==null||imageCenterDTO.getProductListSmallImageDetailDTO()==null){
            continue;
          }
          List<String> productInfoImagePaths=new ArrayList<String>();
          productInfoImagePaths.add(imageCenterDTO.getProductListSmallImageDetailDTO().getImagePath());
          imageCenterDTO.setProductInfoImagePaths(productInfoImagePaths);
          itemDTO.setImageCenterDTO(imageCenterDTO);
        }
      }
    }
  }

  @Override
  public void addImageToPreBuyOrderItemSearchResultDTO(List<OrderItemSearchResultDTO> searchResultListDTOs, List<ImageScene> imageSceneList,boolean isDefaultImage) {
    if(CollectionUtil.isEmpty(searchResultListDTOs)){
      return;
    }
    Set<Long> dataShopIdSet = new HashSet<Long>();
    Set<Long> dataIdSet = new HashSet<Long>();
    searchResultListDTOs.removeAll(Collections.singleton(null));
    if(CollectionUtils.isNotEmpty(searchResultListDTOs)){
      for(OrderItemSearchResultDTO searchResultListDTO:searchResultListDTOs){
        dataShopIdSet.add(searchResultListDTO.getShopId());
        dataIdSet.add(searchResultListDTO.getItemId());
      }
      Map<Long,Map<ImageScene,List<DataImageDetailDTO>>> imageMap = getDataImageDetailDTO(dataShopIdSet, imageSceneList, DataType.ORDER, dataIdSet.toArray(new Long[dataIdSet.size()]));
      for(OrderItemSearchResultDTO searchResultListDTO:searchResultListDTOs){
        searchResultListDTO.setImageCenterDTO(ImageUtils.generateCommonImageDetail(imageSceneList, imageMap.get(searchResultListDTO.getItemId()), isDefaultImage));
      }
    }
  }

   @Override
   public void addImageToPreBuyOrderItemDTO(List<PreBuyOrderItemDTO> preBuyOrderItemDTOs, List<ImageScene> imageSceneList,boolean isDefaultImage) {
     if(CollectionUtil.isEmpty(preBuyOrderItemDTOs)){
       return;
     }
     Set<Long> dataShopIdSet = new HashSet<Long>();
     Set<Long> dataIdSet = new HashSet<Long>();
     preBuyOrderItemDTOs.removeAll(Collections.singleton(null));
     if(CollectionUtils.isNotEmpty(preBuyOrderItemDTOs)){
       for(PreBuyOrderItemDTO itemDTO:preBuyOrderItemDTOs){
         dataShopIdSet.add(itemDTO.getShopId());
         dataIdSet.add(itemDTO.getId());
       }
       Map<Long,Map<ImageScene,List<DataImageDetailDTO>>> imageMap = getDataImageDetailDTO(dataShopIdSet, imageSceneList, DataType.ORDER, dataIdSet.toArray(new Long[dataIdSet.size()]));
       for(PreBuyOrderItemDTO itemDTO:preBuyOrderItemDTOs){
         itemDTO.setImageCenterDTO(ImageUtils.generateCommonImageDetail(imageSceneList, imageMap.get(itemDTO.getId()), isDefaultImage));
       }
     }
   }

  @Override
  public void addImageToShopDTO(List<ImageScene> imageSceneList,boolean isDefaultImage,ShopDTO... shopDTOs) {
    List<ShopDTO> shopDTOList = new ArrayList<ShopDTO>(Arrays.asList(shopDTOs));
    shopDTOList.removeAll(Collections.singleton(null));
    if(CollectionUtils.isEmpty(shopDTOList) || CollectionUtils.isEmpty(imageSceneList)) return;

    Set<Long> dataShopIdSet = new HashSet<Long>();
    Set<Long> dataIdSet = new HashSet<Long>();
    for(ShopDTO shopDTO:shopDTOList){
      dataShopIdSet.add(shopDTO.getId());
      dataIdSet.add(shopDTO.getId());
    }
    Map<Long,Map<ImageScene,List<DataImageDetailDTO>>> imageMap = getDataImageDetailDTO(dataShopIdSet, imageSceneList, DataType.SHOP, dataIdSet.toArray(new Long[dataIdSet.size()]));
    for(ShopDTO shopDTO:shopDTOList){
      shopDTO.setImageCenterDTO(ImageUtils.generateCommonImageDetail(imageSceneList, imageMap.get(shopDTO.getId()),isDefaultImage));
    }
  }

  @Override
  public void addImageToCustomerDTO(List<ImageScene> imageSceneList, boolean isDefaultImage, CustomerDTO... customerDTOs) {
    List<CustomerDTO> customerDTOList = new ArrayList<CustomerDTO>(Arrays.asList(customerDTOs));
    customerDTOList.removeAll(Collections.singleton(null));
    if(CollectionUtils.isEmpty(customerDTOList) || CollectionUtils.isEmpty(imageSceneList)) return;

    Set<Long> dataShopIdSet = new HashSet<Long>();
    Set<Long> dataIdSet = new HashSet<Long>();
    for(CustomerDTO customerDTO:customerDTOList){
      dataShopIdSet.add(customerDTO.getShopId());
      dataIdSet.add(customerDTO.getId());
    }
    Map<Long,Map<ImageScene,List<DataImageDetailDTO>>> imageMap = this.getDataImageDetailDTO(dataShopIdSet, imageSceneList, DataType.CUSTOMER, dataIdSet.toArray(new Long[dataIdSet.size()]));
    for(CustomerDTO customerDTO:customerDTOList){
      customerDTO.setImageCenterDTO(ImageUtils.generateCommonImageDetail(imageSceneList, imageMap.get(customerDTO.getId()),isDefaultImage));
    }
  }
  @Override
  public void addImageToBcgogoProductDTO(List<ImageScene> imageSceneList, BcgogoProductDTO... bcgogoProductDTOs) {
    List<BcgogoProductDTO> bcgogoProductDTOList = new ArrayList<BcgogoProductDTO>(Arrays.asList(bcgogoProductDTOs));
    bcgogoProductDTOList.removeAll(Collections.singleton(null));
    if (CollectionUtils.isEmpty(bcgogoProductDTOList) || CollectionUtils.isEmpty(imageSceneList)) return;

    List<BcgogoProductPropertyDTO> bcgogoProductPropertyDTOList = null;
    for (BcgogoProductDTO bcgogoProductDTO : bcgogoProductDTOList) {
      bcgogoProductPropertyDTOList = bcgogoProductDTO.getPropertyDTOList();
      if(CollectionUtils.isNotEmpty(bcgogoProductPropertyDTOList)){
        if(StringUtils.isBlank(bcgogoProductDTO.getImagePath())){
          bcgogoProductDTO.setImagePath(bcgogoProductPropertyDTOList.get(0).getImagePath());
        }
        for(BcgogoProductPropertyDTO bcgogoProductPropertyDTO : bcgogoProductPropertyDTOList){
         bcgogoProductPropertyDTO.setImageCenterDTO(generateBcgogoProductImageCenterDTO(imageSceneList, bcgogoProductPropertyDTO.getImagePath()));
        }
      }
      bcgogoProductDTO.setImageCenterDTO(generateBcgogoProductImageCenterDTO(imageSceneList,bcgogoProductDTO.getImagePath()));
    }
  }

  private ImageCenterDTO generateBcgogoProductImageCenterDTO(List<ImageScene> imageSceneList, String imagePath) {
    ImageCenterDTO imageCenterDTO = new ImageCenterDTO();
    for(ImageScene imageScene : imageSceneList){
      String imageURL = StringUtils.isNotBlank(imagePath)? ImageUtils.generateUpYunImagePath(imagePath, imageScene.getImageVersion()):ImageUtils.generateNotFindImageUrl(imageScene.getImageVersion());
      switch (imageScene) {
        case BCGOGO_PRODUCT_INFO_IMAGE_BIG: {
          imageCenterDTO.setBcgogoProductInfoBigImageURL(imageURL);
          break;
        }
        case BCGOGO_PRODUCT_INFO_IMAGE_SMALL: {
          imageCenterDTO.setBcgogoProductInfoSmallImageURL(imageURL);
          break;
        }
        case BCGOGO_PRODUCT_LIST_IMAGE: {
          imageCenterDTO.setBcgogoProductListImageURL(imageURL);
          break;
        }
        case BCGOGO_PRODUCT_LIST_IMAGE_SMALL: {
          imageCenterDTO.setBcgogoProductListSmallImageURL(imageURL);
          break;
        }
      }
    }
    return imageCenterDTO;
  }

  @Override
  public void addShopImageAppShopDTO(List<ImageScene> imageSceneList, boolean isDefaultImage, List<AppShopDTO> appShopDTOList) {
    if(CollectionUtils.isEmpty(imageSceneList))return;
    Set<Long> dataShopIdSet = new HashSet<Long>();
    Set<Long> dataIdSet = new HashSet<Long>();
    appShopDTOList.removeAll(Collections.singleton(null));
    if (CollectionUtils.isNotEmpty(appShopDTOList)) {
      for (AppShopDTO dto : appShopDTOList) {
        dataShopIdSet.add(dto.getId());
        dataIdSet.add(dto.getId());
      }
      Map<Long, Map<ImageScene, List<DataImageDetailDTO>>> imageMap = this.getDataImageDetailDTO(dataShopIdSet, imageSceneList, DataType.SHOP, dataIdSet.toArray(new Long[dataIdSet.size()]));
      for (AppShopDTO dto : appShopDTOList) {
        ImageCenterDTO centerDTO = ImageUtils.generateCommonImageDetail(imageSceneList, imageMap.get(dto.getId()), isDefaultImage);
        if (centerDTO != null && centerDTO.getShopBigMainImageDetailDTO() != null)
          dto.setBigImageUrl(centerDTO.getShopBigMainImageDetailDTO().getImageURL());
        if (centerDTO != null && centerDTO.getShopSmallMainImageDetailDTO() != null)
          dto.setSmallImageUrl(centerDTO.getShopSmallMainImageDetailDTO().getImageURL());
      }
    }
  }

  @Override
  public void addAppUserBillImages(List<ImageScene> imageSceneList, boolean isDefaultImage, List<AppUserBillDTO> appUserBillDTOs) {
    if (CollectionUtils.isEmpty(imageSceneList) || CollectionUtils.isEmpty(appUserBillDTOs)) return;
    appUserBillDTOs.removeAll(Collections.singleton(null));
    if (CollectionUtils.isEmpty(appUserBillDTOs)) return;

    Set<String> appUserNoSet = new HashSet<String>();
    Set<Long> dataIdSet = new HashSet<Long>();
    if (CollectionUtils.isNotEmpty(appUserBillDTOs)) {
      for (AppUserBillDTO dto : appUserBillDTOs) {
        appUserNoSet.add(dto.getAppUserNo());
        dataIdSet.add(dto.getId());
      }
      Map<Long, Map<ImageScene, List<DataImageDetailDTO>>> imageMap = this.getDataImageDetailDTO(null,appUserNoSet, imageSceneList, DataType.APP_USER_BILL, dataIdSet.toArray(new Long[dataIdSet.size()]));
      for (AppUserBillDTO dto : appUserBillDTOs) {
        ImageCenterDTO centerDTO = ImageUtils.generateCommonImageDetail(imageSceneList, imageMap.get(dto.getId()), isDefaultImage);
        Map<Long, AppUserImageDTO> imageDTOMap = new HashMap<Long, AppUserImageDTO>();
        AppUserImageDTO appUserImageDTO;
        for (DataImageDetailDTO detailDTO : centerDTO.getAppUserBillSmallImageDetailDTOs()) {
          appUserImageDTO = detailDTO.toAppUserImageDTO();
          appUserImageDTO.setSmallImageUrl(detailDTO.getImageURL());
          imageDTOMap.put(detailDTO.getImageId(), appUserImageDTO);
        }
        for (DataImageDetailDTO detailDTO : centerDTO.getAppUserBillBigImageDetailDTOs()) {
          appUserImageDTO = imageDTOMap.get(detailDTO.getImageId());
          if (appUserImageDTO != null) {
            appUserImageDTO.setBigImageUrl(detailDTO.getImageURL());
          }
        }
        dto.setImageList(new ArrayList<AppUserImageDTO>(imageDTOMap.values()));
      }
    }
  }

  @Override
   public void addShopImageAppOrderDTO(ImageScene imageScene, boolean isDefaultImage, ApiOrderHistoryResponse apiOrderHistoryResponse) {
    if (imageScene == null) return;
    Set<Long> dataShopIdSet = new HashSet<Long>();
    Set<Long> dataIdSet = new HashSet<Long>();

    if (apiOrderHistoryResponse != null) {
      for (AppOrderDTO dto : apiOrderHistoryResponse.getUnFinishedServiceList()) {
        dataShopIdSet.add(dto.getShopId());
        dataIdSet.add(dto.getShopId());
      }

      for (AppOrderDTO dto : apiOrderHistoryResponse.getFinishedServiceList()) {
        dataShopIdSet.add(dto.getShopId());
        dataIdSet.add(dto.getShopId());
      }
      List<ImageScene> imageSceneList = new ArrayList<ImageScene>();
      imageSceneList.add(imageScene);
      Map<Long, Map<ImageScene, List<DataImageDetailDTO>>> imageMap = ServiceManager.getService(IImageService.class)
          .getDataImageDetailDTO(dataShopIdSet, imageSceneList, DataType.SHOP, dataIdSet.toArray(new Long[dataIdSet.size()]));
      for (AppOrderDTO dto : apiOrderHistoryResponse.getUnFinishedServiceList()) {
        ImageCenterDTO centerDTO = ImageUtils.generateCommonImageDetail(imageSceneList, imageMap.get(dto.getShopId()), isDefaultImage);
        if (centerDTO != null && centerDTO.getShopSmallMainImageDetailDTO() != null) {
          dto.setShopImageUrl(centerDTO.getShopSmallMainImageDetailDTO().getImageURL());
        }
      }

      for (AppOrderDTO dto : apiOrderHistoryResponse.getFinishedServiceList()) {
        ImageCenterDTO centerDTO = ImageUtils.generateCommonImageDetail(imageSceneList, imageMap.get(dto.getShopId()), isDefaultImage);
        if (centerDTO != null && centerDTO.getShopSmallMainImageDetailDTO() != null) {
          dto.setShopImageUrl(centerDTO.getShopSmallMainImageDetailDTO().getImageURL());
        }
      }
    }
  }

  @Override
  public void addShopImageAppOrderDTO(ImageScene imageScene, boolean isDefaultImage, ApiPageListResponse<AppOrderDTO> apiPageListResponse) {
    if (imageScene == null) return;
     Set<Long> dataShopIdSet = new HashSet<Long>();
     Set<Long> dataIdSet = new HashSet<Long>();

     if (apiPageListResponse != null && CollectionUtils.isNotEmpty(apiPageListResponse.getResults())) {
       for (AppOrderDTO dto : apiPageListResponse.getResults()) {
         dataShopIdSet.add(dto.getShopId());
         dataIdSet.add(dto.getShopId());
       }

       List<ImageScene> imageSceneList = new ArrayList<ImageScene>();
       imageSceneList.add(imageScene);
       Map<Long, Map<ImageScene, List<DataImageDetailDTO>>> imageMap = ServiceManager.getService(IImageService.class)
           .getDataImageDetailDTO(dataShopIdSet, imageSceneList, DataType.SHOP, dataIdSet.toArray(new Long[dataIdSet.size()]));
       for (AppOrderDTO dto : apiPageListResponse.getResults()) {
         ImageCenterDTO centerDTO = ImageUtils.generateCommonImageDetail(imageSceneList, imageMap.get(dto.getShopId()), isDefaultImage);
         if (centerDTO != null && centerDTO.getShopSmallMainImageDetailDTO() != null) {
           dto.setShopImageUrl(centerDTO.getShopSmallMainImageDetailDTO().getImageURL());
         }
       }
     }
  }

  @Deprecated
  @Override
  public void initErrorImagePath () throws Exception{
    ConfigWriter writer = configDaoManager.getWriter();
    Set<ImageType> imageTypes = new HashSet<ImageType>();
    imageTypes.add(ImageType.SHOP_MAIN_IMAGE);
    imageTypes.add(ImageType.SHOP_AUXILIARY_IMAGE);
    imageTypes.add(ImageType.SHOP_BUSINESS_LICENSE_IMAGE);
    List<ImageInfo> imageInfoList = writer.getAllDataImageInfoList(imageTypes);
    if(CollectionUtils.isNotEmpty(imageInfoList)) {
      for (ImageInfo imageInfo : imageInfoList) {
        String oldPath = imageInfo.getPath();
        if (StringUtils.isNotBlank(oldPath)) {
          LOG.warn("oldPath:" + oldPath);
          String[] strs = oldPath.substring(1).split("/");
          if (!"2013".equals(strs[0])) {
            File file = new File(System.getProperty("java.io.tmpdir") + "/" + strs[4]);
            if (UpYunManager.getInstance().readFile(oldPath, file)) {
              String newPath = "/" + strs[1] + "/" + strs[2] + "/" + strs[3] + "/" + strs[0] + "/" + strs[4];
              newPath = newPath.replaceAll("\\.\\.", "\\.");
              LOG.warn("newPath:" + newPath);
              if (UpYunManager.getInstance().writeFile(newPath, file, true)) {
                Object status = writer.begin();
                try {
                  imageInfo.setPath(newPath);
                  writer.update(imageInfo);
                  writer.commit(status);
                } finally {
                  writer.rollback(status);
                }
              } else {
                LOG.error("upload image to new path:" + newPath + " error!");
              }
            }else{
              LOG.error("NotFindImageInYunByDBImagePath:"+oldPath);
            }
          }
        }
      }
    }
  }

  @Deprecated
  @Override
  public void getNotFindImageInYunByDBImagePath () throws Exception{
    ConfigWriter writer = configDaoManager.getWriter();
    List<ImageInfo> imageInfoList = writer.getAllDataImageInfoList(new HashSet<ImageType>(Arrays.asList(ImageType.values())));
    if(CollectionUtils.isNotEmpty(imageInfoList)){
      for(ImageInfo imageInfo : imageInfoList){
        String path = imageInfo.getPath();
        if(StringUtils.isNotBlank(path)){
          LOG.debug("path:" + path);
          String[] strs = path.substring(1).split("/");
          File file = new File(System.getProperty("java.io.tmpdir")+"/"+strs[4]);
          if(!UpYunManager.getInstance().readFile(path, file)){
            LOG.error("NotFindImageInYunByDBImagePath:"+path);
          }
        }
      }
    }
  }

  @Override
  public void addAppEnquiryImage(List<EnquiryDTO> enquiryDTOs, List<ImageScene> imageScenes, boolean isDefaultImage) {
    if (CollectionUtils.isNotEmpty(enquiryDTOs)) {
      Set<Long> dataIds = new HashSet<Long>();
      Set<String> appUserNoSet = new HashSet<String>();
      for (EnquiryDTO enquiryDTO : enquiryDTOs) {
        if (enquiryDTO != null && enquiryDTO.getId() != null) {
          dataIds.add(enquiryDTO.getId());
        }
        if (enquiryDTO != null && StringUtils.isNotBlank(enquiryDTO.getAppUserNo())) {
          appUserNoSet.add(enquiryDTO.getAppUserNo());
        }
      }
      ConfigWriter writer = configDaoManager.getWriter();
      List<Object[]> dataImageRelationImageInfoList = writer.getAppUserDataImageRelation(appUserNoSet, ImageType.getEnquiryImageTypeSet(),
          DataType.APP_ENQUIRY, dataIds.toArray(new Long[dataIds.size()]));
      Map<Long, List<AppUserImageDTO>> appUserImageMap = new HashMap<Long, List<AppUserImageDTO>>();
      if (CollectionUtils.isNotEmpty(dataImageRelationImageInfoList)) {
        for (Object[] objects : dataImageRelationImageInfoList) {
          if (!ArrayUtils.isEmpty(objects) && objects.length == 2 && objects[0] != null && objects[1] != null) {
            DataImageRelation dataImageRelation = (DataImageRelation) objects[0];
            ImageInfo imageInfo = (ImageInfo) objects[1];
            if (dataImageRelation != null && dataImageRelation.getDataId() != null) {
              List<AppUserImageDTO> appUserImageDTOs = appUserImageMap.get(dataImageRelation.getDataId());
              if (appUserImageDTOs == null) {
                appUserImageDTOs = new ArrayList<AppUserImageDTO>();
              }
              AppUserImageDTO appUserImageDTO = new AppUserImageDTO();
              appUserImageDTO.setImageInfo(dataImageRelation.toDTO(), imageInfo.toDTO());
              setAppUserImageDTOURL(imageScenes, appUserImageDTO);
              appUserImageDTOs.add(appUserImageDTO);
              appUserImageMap.put(dataImageRelation.getDataId(), appUserImageDTOs);
            }
          }
        }
        for (EnquiryDTO enquiryDTO : enquiryDTOs) {
          if (enquiryDTO != null && enquiryDTO.getId() != null) {
            List<AppUserImageDTO> appUserImageDTOs = appUserImageMap.get(enquiryDTO.getId());
            if (CollectionUtils.isEmpty(appUserImageDTOs) && isDefaultImage) {
              if (appUserImageDTOs == null) {
                appUserImageDTOs = new ArrayList<AppUserImageDTO>();
              }
              AppUserImageDTO defaultAppUserImageDTO = generateAppUserDefaultImage(imageScenes);
              appUserImageDTOs.add(defaultAppUserImageDTO);
            }
            if (appUserImageDTOs != null) {
              enquiryDTO.setEnquiryImages(appUserImageDTOs.toArray(new AppUserImageDTO[appUserImageDTOs.size()]));
            }
          }
        }
      }
    }
  }

  @Override
  public void addShopEnquiryImage(ShopEnquiryDTO shopEnquiryDTO, List<ImageScene> shopImageScenes, boolean isDefaultImage) {
    if (shopEnquiryDTO != null && shopEnquiryDTO.getId() != null) {
      Set<Long> dataIds = new HashSet<Long>();
      Set<String> appUserNoSet = new HashSet<String>();
      dataIds.add(shopEnquiryDTO.getId());
      appUserNoSet.add(shopEnquiryDTO.getAppUserNo());
      ConfigWriter writer = configDaoManager.getWriter();
      List<Object[]> dataImageRelationImageInfoList = writer.getAppUserDataImageRelation(appUserNoSet, ImageType.getEnquiryImageTypeSet(),
          DataType.APP_ENQUIRY, dataIds.toArray(new Long[dataIds.size()]));
      Map<Long, List<AppUserImageDTO>> appUserImageMap = new HashMap<Long, List<AppUserImageDTO>>();
      if (CollectionUtils.isNotEmpty(dataImageRelationImageInfoList)) {
        for (Object[] objects : dataImageRelationImageInfoList) {
          if (!ArrayUtils.isEmpty(objects) && objects.length == 2 && objects[0] != null && objects[1] != null) {
            DataImageRelation dataImageRelation = (DataImageRelation) objects[0];
            ImageInfo imageInfo = (ImageInfo) objects[1];
            if (dataImageRelation != null && dataImageRelation.getDataId() != null) {
              List<AppUserImageDTO> appUserImageDTOs = appUserImageMap.get(dataImageRelation.getDataId());
              if (appUserImageDTOs == null) {
                appUserImageDTOs = new ArrayList<AppUserImageDTO>();
              }
              AppUserImageDTO appUserImageDTO = new AppUserImageDTO();
              appUserImageDTO.setImageInfo(dataImageRelation.toDTO(), imageInfo.toDTO());
              setAppUserImageDTOURL(shopImageScenes, appUserImageDTO);
              appUserImageDTOs.add(appUserImageDTO);
              appUserImageMap.put(dataImageRelation.getDataId(), appUserImageDTOs);
            }
          }
        }
        List<AppUserImageDTO> appUserImageDTOs = appUserImageMap.get(shopEnquiryDTO.getId());
        if (CollectionUtils.isEmpty(appUserImageDTOs) && isDefaultImage) {
          if (appUserImageDTOs == null) {
            appUserImageDTOs = new ArrayList<AppUserImageDTO>();
          }
          AppUserImageDTO defaultAppUserImageDTO = generateAppUserDefaultImage(shopImageScenes);
          appUserImageDTOs.add(defaultAppUserImageDTO);
        }
        if (appUserImageDTOs != null) {
          shopEnquiryDTO.setEnquiryImages(appUserImageDTOs.toArray(new AppUserImageDTO[appUserImageDTOs.size()]));
        }
      }
    }
  }

  @Override
  public List<AdvertDTO> addImageInfoToAdvertDTO(List<ImageScene> imageSceneList, boolean isDefaultImage, AdvertDTO... advertDTOs) {
    if (ArrayUtil.isEmpty(advertDTOs)) {
      return null;
    }
    List<AdvertDTO> advertDTOList = new ArrayList<AdvertDTO>(Arrays.asList(advertDTOs));
    advertDTOList.removeAll(Collections.singleton(null));

    if (CollectionUtils.isEmpty(advertDTOList) || CollectionUtils.isEmpty(imageSceneList)) return null;
    Set<Long> dataShopIdSet = new HashSet<Long>();
    Set<Long> dataIdSet = new HashSet<Long>();
    for (AdvertDTO advertDTO : advertDTOList) {
      dataShopIdSet.add(advertDTO.getShopId());
      dataIdSet.add(advertDTO.getId());
    }
    Map<Long, Map<ImageScene, List<DataImageDetailDTO>>> imageMap = getDataImageDetailDTO(dataShopIdSet, imageSceneList, DataType.SHOP_ADVERT, dataIdSet.toArray(new Long[dataIdSet.size()]));
    for (AdvertDTO advertDTO : advertDTOList) {
      ImageCenterDTO imageCenterDTO = ImageUtils.generateCommonImageDetail(imageSceneList, imageMap.get(advertDTO.getId()), isDefaultImage);
      if (imageCenterDTO != null) {
        DataImageDetailDTO dataImageDetailDTO = imageCenterDTO.getShopAdvertSmallImage();
        if (dataImageDetailDTO != null) {
          advertDTO.setImageUrl(imageCenterDTO.getShopAdvertSmallImage().getImageURL());
        }

        List<DataImageDetailDTO> shopAdvertImageDetailDTOs = imageCenterDTO.getShopAdvertImageDetailDTOs();
        String description = advertDTO.getDescription();
        if (CollectionUtils.isNotEmpty(shopAdvertImageDetailDTOs) && StringUtils.isNotBlank(description)) {
          for (int i = 0; i < shopAdvertImageDetailDTOs.size(); i++) {
            description = description.replaceAll(ImageUtils.ImageSrcPlaceHolder + i, shopAdvertImageDetailDTOs.get(i).getImageURL());
          }
          advertDTO.setDescription(description);
        }
      }
    }

    return advertDTOList;
  }

  @Override
  public Map<Long, DataImageDetailDTO> getRecommendTreeImgMapByDataIds(Set<Long> recommendIds) {
    Map<Long, DataImageDetailDTO> dataImageRelationDTOMap = new HashMap<Long, DataImageDetailDTO>();
    if(CollectionUtils.isNotEmpty(recommendIds)){
      Set<Long> shopIds =  new HashSet<Long>();
      shopIds.add(ConfigConstant.CONFIG_SHOP_ID);
      List<ImageScene> imageSceneList = new ArrayList<ImageScene>();
      imageSceneList.add(ImageScene.RECOMMEND_SHOP_IMAGE);
      Map<Long, Map<ImageScene, List<DataImageDetailDTO>>> dataMap = getDataImageDetailDTO(shopIds,null,imageSceneList,DataType.RECOMMEND_TREE,recommendIds.toArray(new Long[recommendIds.size()]));
      for(Long recommendId : recommendIds){
        Map<ImageScene, List<DataImageDetailDTO>> imageDataImageDetailMap = dataMap.get(recommendId);
        if(MapUtils.isNotEmpty(imageDataImageDetailMap)){
          List<DataImageDetailDTO> dataImageDetailDTOs = imageDataImageDetailMap.get(ImageScene.RECOMMEND_SHOP_IMAGE);
          if(CollectionUtils.isNotEmpty(dataImageDetailDTOs)){
            for(DataImageDetailDTO dataImageDetailDTO : dataImageDetailDTOs){
              if(dataImageDetailDTO != null && dataImageDetailDTO.getDataId() != null){
                dataImageRelationDTOMap.put(dataImageDetailDTO.getDataId(),dataImageDetailDTO);
              }
            }
          }
        }
      }
    }
    return dataImageRelationDTOMap;
  }
}
