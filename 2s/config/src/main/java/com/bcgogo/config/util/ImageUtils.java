package com.bcgogo.config.util;

import com.bcgogo.config.dto.image.DataImageDetailDTO;
import com.bcgogo.config.dto.image.ImageCenterDTO;
import com.bcgogo.enums.config.ImageScene;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 13-7-30
 * Time: 上午10:26
 * To change this template use File | Settings | File Templates.
 */
public class ImageUtils {
  public final static String ImageSrcPlaceHolder = "_\\$src\\$_";

  public static String generateNotFindImageUrl() {
    return ConfigUtils.getUpYunDomainUrl()+ConfigUtils.getUpYunNotFindImagePath();
  }
  public static String generateNotFindImageUrl(String imageVersion) {
    if(StringUtils.isBlank(imageVersion)){
      return  ConfigUtils.getUpYunDomainUrl()+ConfigUtils.getUpYunNotFindImagePath();
    }
    return ConfigUtils.getUpYunDomainUrl()+ConfigUtils.getUpYunNotFindImagePath()+ ConfigUtils.getUpYunSeparator()+imageVersion;
  }
  public static String generateUpYunImagePath(String imagePath,String imageVersion){
    if(StringUtils.isBlank(imageVersion)){
      return  ConfigUtils.getUpYunDomainUrl()+imagePath;
    }
    return  ConfigUtils.getUpYunDomainUrl()+imagePath+ ConfigUtils.getUpYunSeparator()+imageVersion;
  }

  public static ImageCenterDTO generateCommonImageDetail(List<ImageScene> imageSceneList,Map<ImageScene,List< DataImageDetailDTO >> imageSceneListMap,boolean isDefaultImage){
    if(CollectionUtils.isEmpty(imageSceneList)) return null;
    ImageCenterDTO imageCenterDTO = new ImageCenterDTO();
    List<DataImageDetailDTO> dataImageDetailDTOList = null;
    for(ImageScene imageScene:imageSceneList){
      if(MapUtils.isEmpty(imageSceneListMap)){
        dataImageDetailDTOList= new ArrayList<DataImageDetailDTO>();
      }else{
        dataImageDetailDTOList = imageSceneListMap.get(imageScene);
        if(dataImageDetailDTOList==null)
          dataImageDetailDTOList= new ArrayList<DataImageDetailDTO>();
      }
      int listSize = dataImageDetailDTOList.size();
      switch (imageScene) {
        case PRODUCT_LIST_IMAGE_SMALL: {
          if(CollectionUtils.isNotEmpty(dataImageDetailDTOList)){
            imageCenterDTO.setProductListSmallImageDetailDTO(dataImageDetailDTOList.get(0));
          }else if(isDefaultImage){
            imageCenterDTO.setProductListSmallImageDetailDTO(new DataImageDetailDTO(0,generateNotFindImageUrl()+ConfigUtils.getUpYunSeparator()+imageScene.getImageVersion()));
          }
          break;
        }
        case OTHER_PRODUCT_IMAGE: {
          if(CollectionUtils.isNotEmpty(dataImageDetailDTOList)){
            imageCenterDTO.setOtherProductListImageDetailDTO(dataImageDetailDTOList.get(0));
          }else if(isDefaultImage){
            imageCenterDTO.setOtherProductListImageDetailDTO(new DataImageDetailDTO(0,generateNotFindImageUrl()+ConfigUtils.getUpYunSeparator()+imageScene.getImageVersion()));
          }
          break;
        }
        case PRODUCT_INFO_IMAGE_BIG: {
          if(listSize<5 && isDefaultImage){
            for(int i=0;i<5-listSize;i++){
              dataImageDetailDTOList.add(new DataImageDetailDTO(listSize+1,generateNotFindImageUrl()+ConfigUtils.getUpYunSeparator()+imageScene.getImageVersion()));
            }
          }
          imageCenterDTO.setProductInfoBigImageDetailDTOs(dataImageDetailDTOList);
          break;
        }
        case PRODUCT_INFO_IMAGE_SMALL: {
          if(listSize<5 && isDefaultImage){
            for(int i=0;i<5-listSize;i++){
              dataImageDetailDTOList.add(new DataImageDetailDTO(listSize+1,generateNotFindImageUrl()+ConfigUtils.getUpYunSeparator()+imageScene.getImageVersion()));
            }
          }
          imageCenterDTO.setProductInfoSmallImageDetailDTOs(dataImageDetailDTOList);
          break;
        }
        case PRODUCT_INFO_DESCRIPTION_IMAGE: {
          if(listSize<5 && isDefaultImage){
            for(int i=0;i<5-listSize;i++){
              dataImageDetailDTOList.add(new DataImageDetailDTO(listSize+1,generateNotFindImageUrl()+ConfigUtils.getUpYunSeparator()+imageScene.getImageVersion()));
            }
          }
          imageCenterDTO.setProductInfoDescriptionImageDetailDTOs(dataImageDetailDTOList);
          break;
        }
        case PRODUCT_RECOMMEND_LIST_IMAGE_SMALL: {
          if(CollectionUtils.isNotEmpty(dataImageDetailDTOList)){
            imageCenterDTO.setRecommendProductListImageDetailDTO(dataImageDetailDTOList.get(0));
          }else if(isDefaultImage){
            imageCenterDTO.setRecommendProductListImageDetailDTO(new DataImageDetailDTO(0,generateNotFindImageUrl()+ConfigUtils.getUpYunSeparator()+imageScene.getImageVersion()));
          }
          break;
        }
        case SHOP_ADVERT_INFO_DESCRIPTION_IMAGE: {
          if (listSize < 5 && isDefaultImage) {
            for (int i = 0; i < 5 - listSize; i++) {
              dataImageDetailDTOList.add(new DataImageDetailDTO(listSize + 1, generateNotFindImageUrl() + ConfigUtils.getUpYunSeparator() + imageScene.getImageVersion()));
            }
          }
          imageCenterDTO.setShopAdvertImageDetailDTOs(dataImageDetailDTOList);
          break;
        }
        case SHOP_ADVERT_INFO_DESCRIPTION_SMALL_IMAGE: {
          if (CollectionUtils.isNotEmpty(dataImageDetailDTOList)) {
            imageCenterDTO.setShopAdvertSmallImage(dataImageDetailDTOList.get(0));
          }
          break;
        }
        case SHOP_BUSINESS_LICENSE_IMAGE: {
          if(CollectionUtils.isNotEmpty(dataImageDetailDTOList)){
            imageCenterDTO.setShopBusinessLicenseImageDetailDTO(dataImageDetailDTOList.get(0));
          }else if(isDefaultImage){
            imageCenterDTO.setShopBusinessLicenseImageDetailDTO(new DataImageDetailDTO(0,generateNotFindImageUrl()+ConfigUtils.getUpYunSeparator()+imageScene.getImageVersion()));
          }
          break;
        }
        case SHOP_RQ_IMAGE: {
          if(CollectionUtils.isNotEmpty(dataImageDetailDTOList)){
            imageCenterDTO.setShopRQImageDetailDTO(dataImageDetailDTOList.get(0));
          }else if(isDefaultImage){
            imageCenterDTO.setShopRQImageDetailDTO(new DataImageDetailDTO(0,generateNotFindImageUrl()+ConfigUtils.getUpYunSeparator()+imageScene.getImageVersion()));
          }
          break;
        }
        case SHOP_IMAGE_BIG: {
          if(listSize<4 && isDefaultImage){
            for(int i=0;i<4-listSize;i++){
              dataImageDetailDTOList.add(new DataImageDetailDTO(listSize+1,generateNotFindImageUrl()+ConfigUtils.getUpYunSeparator()+imageScene.getImageVersion()));
            }
          }
          imageCenterDTO.setShopBigImageDetailDTOs(dataImageDetailDTOList);
          break;
        }
        case SHOP_IMAGE_SMALL: {
          if(listSize<4 && isDefaultImage){
            for(int i=0;i<4-listSize;i++){
              dataImageDetailDTOList.add(new DataImageDetailDTO(listSize+1,generateNotFindImageUrl()+ConfigUtils.getUpYunSeparator()+imageScene.getImageVersion()));
            }
          }
          imageCenterDTO.setShopSmallImageDetailDTOs(dataImageDetailDTOList);
          break;
        }
        case SHOP_IMAGE: {
          if(listSize<4 && isDefaultImage){
            for(int i=0;i<4-listSize;i++){
              dataImageDetailDTOList.add(new DataImageDetailDTO(listSize+1,generateNotFindImageUrl()+ConfigUtils.getUpYunSeparator()+imageScene.getImageVersion()));
            }
          }
          imageCenterDTO.setShopImageDetailDTOs(dataImageDetailDTOList);
          break;
        }
        case SHOP_IMAGE_60X60_SMALL: {
          if(CollectionUtils.isNotEmpty(dataImageDetailDTOList)){
            imageCenterDTO.setShopSmallMainImageDetailDTO(dataImageDetailDTOList.get(0));
          }else if(isDefaultImage){
            imageCenterDTO.setShopSmallMainImageDetailDTO(new DataImageDetailDTO(0,generateNotFindImageUrl()+ConfigUtils.getUpYunSeparator()+imageScene.getImageVersion()));
          }
          break;
        }
        case SHOP_MANAGE_UPLOAD_IMAGE: {
          if(listSize<4 && isDefaultImage){
            for(int i=0;i<4-listSize;i++){
              dataImageDetailDTOList.add(new DataImageDetailDTO(listSize+1,generateNotFindImageUrl()+ConfigUtils.getUpYunSeparator()+imageScene.getImageVersion()));
            }
          }
          imageCenterDTO.setShopUploadImageDetailDTOs(dataImageDetailDTOList);
          break;
        }
        case CUSTOMER_IDENTIFICATION_IMAGE_UPLOAD: {
          if(listSize<6 && isDefaultImage){
            for(int i=0;i<6-listSize;i++){
              dataImageDetailDTOList.add(new DataImageDetailDTO(listSize+1,generateNotFindImageUrl()+ConfigUtils.getUpYunSeparator()+imageScene.getImageVersion()));
            }
          }
          imageCenterDTO.setCustomerUploadIdentificationImageDetailDTOs(dataImageDetailDTOList);
          break;
        }
        case CUSTOMER_IDENTIFICATION_IMAGE: {
          if(listSize<6 && isDefaultImage){
            for(int i=0;i<6-listSize;i++){
              dataImageDetailDTOList.add(new DataImageDetailDTO(listSize+1,generateNotFindImageUrl()+ConfigUtils.getUpYunSeparator()+imageScene.getImageVersion()));
            }
          }
          imageCenterDTO.setCustomerIdentificationImageDetailDTOs(dataImageDetailDTOList);
          break;
        }
        //手机端 小图  5种尺寸
        case SHOP_IMAGE_APP_480X800_SMALL:
        case SHOP_IMAGE_APP_720X1280_SMALL:
        case SHOP_IMAGE_APP_640X1136_SMALL:
        case SHOP_IMAGE_APP_640X960_SMALL:
        case SHOP_IMAGE_APP_320X480_SMALL: {
          if(CollectionUtils.isNotEmpty(dataImageDetailDTOList)){
            imageCenterDTO.setShopSmallMainImageDetailDTO(dataImageDetailDTOList.get(0));
          }else if(isDefaultImage){
            imageCenterDTO.setShopSmallMainImageDetailDTO(new DataImageDetailDTO(0,generateNotFindImageUrl()+ConfigUtils.getUpYunSeparator()+imageScene.getImageVersion()));
          }
          break;
        }

        //手机端 大图 5种尺寸
        case SHOP_IMAGE_APP_480X800_FULL_SCREEN:
        case SHOP_IMAGE_APP_720X1280_FULL_SCREEN:
        case SHOP_IMAGE_APP_640X960_FULL_SCREEN:
        case SHOP_IMAGE_APP_640X1136_FULL_SCREEN:
        case SHOP_IMAGE_APP_320X480_FULL_SCREEN: {
          if(CollectionUtils.isNotEmpty(dataImageDetailDTOList)){
            imageCenterDTO.setShopBigMainImageDetailDTO(dataImageDetailDTOList.get(0));
          }else if(isDefaultImage){
            imageCenterDTO.setShopBigMainImageDetailDTO(new DataImageDetailDTO(0,generateNotFindImageUrl()+ConfigUtils.getUpYunSeparator()+imageScene.getImageVersion()));
          }
          break;
        }

        //手机端 小图  5种尺寸
        case APP_USER_BILL_IMAGE_APP_480X800_SMALL:
        case APP_USER_BILL_IMAGE_APP_720X1280_SMALL:
        case APP_USER_BILL_IMAGE_APP_640X1136_SMALL:
        case APP_USER_BILL_IMAGE_APP_640X960_SMALL:
        case APP_USER_BILL_IMAGE_APP_320X480_SMALL: {
          if (CollectionUtils.isNotEmpty(dataImageDetailDTOList)) {
            imageCenterDTO.getAppUserBillSmallImageDetailDTOs().addAll(dataImageDetailDTOList);
          } else if (isDefaultImage) {
            imageCenterDTO.getAppUserBillSmallImageDetailDTOs().add(new DataImageDetailDTO(0, generateNotFindImageUrl() + ConfigUtils.getUpYunSeparator() + imageScene.getImageVersion()));
          }
          break;
        }

        //手机端 大图 5种尺寸
        case APP_USER_BILL_IMAGE_APP_480X800_FULL_SCREEN:
        case APP_USER_BILL_IMAGE_APP_720X1280_FULL_SCREEN:
        case APP_USER_BILL_IMAGE_APP_640X960_FULL_SCREEN:
        case APP_USER_BILL_IMAGE_APP_640X1136_FULL_SCREEN:
        case APP_USER_BILL_IMAGE_APP_320X480_FULL_SCREEN: {
          if (CollectionUtils.isNotEmpty(dataImageDetailDTOList)) {
            imageCenterDTO.getAppUserBillBigImageDetailDTOs().addAll(dataImageDetailDTOList);
          } else if (isDefaultImage) {
            imageCenterDTO.getAppUserBillBigImageDetailDTOs().add(new DataImageDetailDTO(0, generateNotFindImageUrl() + ConfigUtils.getUpYunSeparator() + imageScene.getImageVersion()));
          }
          break;
        }
      }
    }
    return imageCenterDTO;
  }

  public static boolean isImg(MultipartFile file) {
    Set<String> imgFileName = new HashSet<String>();
    imgFileName.add("image/png");
    imgFileName.add("image/jpg");
    imgFileName.add("image/jpeg");
    if(file!=null && imgFileName.contains(file.getContentType().toLowerCase())){
      return true;
    }
    return false;
  }


}
