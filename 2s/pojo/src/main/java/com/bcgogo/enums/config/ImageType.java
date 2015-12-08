package com.bcgogo.enums.config;

import org.apache.commons.lang.ArrayUtils;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 13-7-30
 * Time: 上午10:57
 * To change this template use File | Settings | File Templates.
 * 主图的imageSequence 为0
 * 其他都是从1开始
 */
public enum ImageType {
  PRODUCT_MAIN_IMAGE("商品主图"),//主图的imageSequence 为0
  PRODUCT_AUXILIARY_IMAGE("商品辅图"),
  PRODUCT_DESCRIPTION_IMAGE("商品详情描述图"),
  SHOP_BUSINESS_LICENSE_IMAGE("店铺营业执照"),
  SHOP_MAIN_IMAGE("店铺风采图"),//主图的imageSequence 为0
  SHOP_RQ_IMAGE("店铺二维码图"),
  SHOP_AUXILIARY_IMAGE("店铺风采图"),
  CUSTOMER_IDENTIFICATION_IMAGE("客户证件照"),
  ENQUIRY_ORDER_MAIN_IMAGE("询价单主图"),
  ENQUIRY_ORDER_AUXILIARY_IMAGE("询价单辅图"),
  APP_USER_BILL_MAIN_IMAGE("用户账单主图"),
  APP_USER_BILL_AUXILIARY_IMAGE("用户账单辅图"),
  SHOP_ADVERT_IMAGE("店铺宣传描述图"),
  RECOMMEND_TREE("店铺推广类目图"),
  WX_ZD_IMAGE("微信账单插图"),
  WX_ARTICLE_IMAGE("微信公告模板"),
  WX_AUDIT_IMAGE("微信待审核图"),
  SHOP_WX_MSG_IMAGE("店铺自定义微信消息图"),




  //
  ;

  private String value;

  private ImageType(String value) {
    this.value = value;
  }

  public String getValue() {
    return this.value;
  }

  private static Map<ImageScene, List<ImageType>> map = new HashMap<ImageScene, List<ImageType>>();
  static{
    for(ImageScene imageScene : ImageScene.values()){
      List<ImageType> imageTypeList = new ArrayList<ImageType>();
      switch (imageScene) {
        case PRODUCT_RECOMMEND_LIST_IMAGE_SMALL: {
          imageTypeList.add(PRODUCT_MAIN_IMAGE);
          break;
        }
        case PRODUCT_LIST_IMAGE_SMALL: {
          imageTypeList.add(PRODUCT_MAIN_IMAGE);
          break;
        }
        case OTHER_PRODUCT_IMAGE: {
          imageTypeList.add(PRODUCT_MAIN_IMAGE);
          break;
        }
        case PRODUCT_INFO_IMAGE_BIG: {
          imageTypeList.add(PRODUCT_MAIN_IMAGE);
          imageTypeList.add(PRODUCT_AUXILIARY_IMAGE);
          break;
        }
        case PRODUCT_INFO_IMAGE_SMALL: {
          imageTypeList.add(PRODUCT_MAIN_IMAGE);
          imageTypeList.add(PRODUCT_AUXILIARY_IMAGE);
          break;
        }
        case PRODUCT_INFO_DESCRIPTION_IMAGE: {
          imageTypeList.add(PRODUCT_DESCRIPTION_IMAGE);
          break;
        }
        case SHOP_ADVERT_INFO_DESCRIPTION_IMAGE: {
          imageTypeList.add(SHOP_ADVERT_IMAGE);
          break;
        }
        case SHOP_ADVERT_INFO_DESCRIPTION_SMALL_IMAGE: {
          imageTypeList.add(SHOP_ADVERT_IMAGE);
          break;
        }
        case SHOP_BUSINESS_LICENSE_IMAGE: {
          imageTypeList.add(SHOP_BUSINESS_LICENSE_IMAGE);
          break;
        }
        case SHOP_IMAGE: {
          imageTypeList.add(SHOP_AUXILIARY_IMAGE);
          imageTypeList.add(SHOP_MAIN_IMAGE);
          break;
        }
        case SHOP_IMAGE_BIG: {
          imageTypeList.add(SHOP_AUXILIARY_IMAGE);
          imageTypeList.add(SHOP_MAIN_IMAGE);
          break;
        }
        case SHOP_IMAGE_SMALL: {
          imageTypeList.add(SHOP_AUXILIARY_IMAGE);
          imageTypeList.add(SHOP_MAIN_IMAGE);
          break;
        }
        case SHOP_MANAGE_UPLOAD_IMAGE: {
          imageTypeList.add(SHOP_AUXILIARY_IMAGE);
          imageTypeList.add(SHOP_MAIN_IMAGE);
          break;
        }
        case SHOP_RQ_IMAGE: {
          imageTypeList.add(SHOP_RQ_IMAGE);
          break;
        }
        case CUSTOMER_IDENTIFICATION_IMAGE: {
          imageTypeList.add(CUSTOMER_IDENTIFICATION_IMAGE);
          break;
        }
        case CUSTOMER_IDENTIFICATION_IMAGE_UPLOAD: {
          imageTypeList.add(CUSTOMER_IDENTIFICATION_IMAGE);
          break;
        }
        case SHOP_IMAGE_APP_320X480_SMALL:
        case SHOP_IMAGE_APP_480X800_SMALL:
        case SHOP_IMAGE_APP_320X480_BIG:
        case SHOP_IMAGE_APP_640X960_SMALL:
        case SHOP_IMAGE_APP_640X1136_SMALL:
        case SHOP_IMAGE_APP_720X1280_SMALL:
        case SHOP_IMAGE_APP_480X800_BIG:
        case SHOP_IMAGE_APP_720X1280_BIG:
        case SHOP_IMAGE_APP_640X960_BIG:
        case SHOP_IMAGE_APP_640X1136_BIG:
        case SHOP_IMAGE_APP_320X480_FULL_SCREEN:
        case SHOP_IMAGE_APP_480X800_FULL_SCREEN:
        case SHOP_IMAGE_APP_720X1280_FULL_SCREEN:
        case SHOP_IMAGE_APP_640X960_FULL_SCREEN:
        case SHOP_IMAGE_APP_640X1136_FULL_SCREEN:
        {
          imageTypeList.add(SHOP_MAIN_IMAGE);
          break;
        }
        case APP_USER_BILL_IMAGE_APP_320X480_SMALL:
        case APP_USER_BILL_IMAGE_APP_480X800_SMALL:
        case APP_USER_BILL_IMAGE_APP_640X960_SMALL:
        case APP_USER_BILL_IMAGE_APP_640X1136_SMALL:
        case APP_USER_BILL_IMAGE_APP_720X1280_SMALL:
        case APP_USER_BILL_IMAGE_APP_320X480_FULL_SCREEN:
        case APP_USER_BILL_IMAGE_APP_480X800_FULL_SCREEN:
        case APP_USER_BILL_IMAGE_APP_720X1280_FULL_SCREEN:
        case APP_USER_BILL_IMAGE_APP_640X960_FULL_SCREEN:
        case APP_USER_BILL_IMAGE_APP_640X1136_FULL_SCREEN: {
          imageTypeList.add(APP_USER_BILL_MAIN_IMAGE);
          imageTypeList.add(APP_USER_BILL_AUXILIARY_IMAGE);
          break;
        }
        case RECOMMEND_SHOP_IMAGE:{
          imageTypeList.add(RECOMMEND_TREE);
          break;
        }
      }
      map.put(imageScene,imageTypeList);
    }
  }

  public static Set<ImageType> getImageTypeListByImageScene(ImageScene... imageScenes) {
    if(ArrayUtils.isEmpty(imageScenes)) return null;
    Set<ImageType> imageTypeSet = new HashSet<ImageType>();
    for(ImageScene imageScene:imageScenes){
      imageTypeSet.addAll(map.get(imageScene));
    }
    return imageTypeSet;
  }

  public static Set<ImageType> getAllProductImageTypeSet() {
    Set<ImageType> imageTypeSet = new HashSet<ImageType>();
    imageTypeSet.add(ImageType.PRODUCT_MAIN_IMAGE);
    imageTypeSet.add(ImageType.PRODUCT_AUXILIARY_IMAGE);
    imageTypeSet.add(ImageType.PRODUCT_DESCRIPTION_IMAGE);
    return imageTypeSet;
  }

  public static Set<ImageType> getEnquiryImageTypeSet() {
    Set<ImageType> imageTypeSet = new HashSet<ImageType>();
    imageTypeSet.add(ImageType.ENQUIRY_ORDER_MAIN_IMAGE);
    imageTypeSet.add(ImageType.ENQUIRY_ORDER_AUXILIARY_IMAGE);
    return imageTypeSet;
  }



}
