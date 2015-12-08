package com.bcgogo.enums.config;

/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 13-7-26
 * Time: 下午5:18
 *
 * version.1    AUTO
 * version.2    60X60
 * version.3    35X35
 * version.4    200X200
 * version.5    720XAUTO
 * version.6    80X80
 * version.7    285X180
 * version.8    360X240
 * version.9    600X400
 * version.10   70X50
 * version.11   100X80
 * version.12   120X100
 * version.13   110X110
 *
 * version.14   63X63
 * version.15   94X94
 * version.16   109X109
 * version.17   125X125
 * version.18   141X141
 * version.19   164X164
 * version.20   245X245
 * version.21   218X218
 * version.22   310X200
 * version.23   480X480
 * version.24   720X720
 * version.25   640X640
 * version.26   320X320
 *
 * version.13 version.22  version.12 version.7  //不能加水印
 */
public enum ImageScene {
  IMAGE_AUTO("","AUTO","显示原图"),
  PRODUCT_50X55_IMAGE_SMALL("version.2","50X55","50X55商品小图"),
  PRODUCT_LIST_IMAGE_SMALL("version.2","60X60","商品列表小图"),
  OTHER_PRODUCT_IMAGE("version.4","200X200","商品列表大图"),
  PRODUCT_INFO_IMAGE_SMALL("version.3","35X35","商品信息小图"),
  PRODUCT_INFO_IMAGE_BIG("version.4","200X200","商品信息大图"),
  BCGOGO_PRODUCT_INFO_IMAGE_SMALL("version.3","35X35","BCGOGO商品信息小图"),
  BCGOGO_PRODUCT_INFO_IMAGE_BIG("version.4","200X200","BCGOGO商品信息大图"),
  BCGOGO_PRODUCT_LIST_IMAGE("version.13","110X110","BCGOGO商品列表图"),
  BCGOGO_PRODUCT_LIST_IMAGE_SMALL("version.6","80X80","BCGOGO商品列表小图"),
  PRODUCT_INFO_DESCRIPTION_IMAGE("version.5","720XAUTO","商品信息详细描述图片"),
  PRODUCT_RECOMMEND_LIST_IMAGE_SMALL("version.6","80X80","商品推荐列表小图"),
  PRE_BUY_PRODUCT_INFO_IMAGE_SMALL("version.6","80X80","商品信息小图"),

  SHOP_BUSINESS_LICENSE_IMAGE("version.7","285X180","店铺资料营业执照"),
  SHOP_IMAGE("version.8","360X240","店铺风采图"),
  SHOP_IMAGE_60X60_SMALL("version.2","60X60","店铺小图"),
  SHOP_IMAGE_BIG("version.9","600X400","店铺风采图大图"),
  SHOP_IMAGE_SMALL("version.10","70X50","店铺风采图小图"),
  SHOP_REGISTER("version.11","100X80","所有注册页面上传店面照片"),
  SHOP_MANAGE_UPLOAD_IMAGE("version.12","120X100","本店资料维护页面上传店面照片"),
  SHOP_RQ_IMAGE("version.13","110X110","店铺二维码照片"),
  //app 手机端使用版本
  SHOP_IMAGE_APP_320X480_SMALL("version.14","63X63","APP320X480店铺小图"),
  SHOP_IMAGE_APP_480X800_SMALL("version.15","94X94","APP480X800店铺小图"),
  SHOP_IMAGE_APP_320X480_BIG("version.16","109X109","APP320X480店铺大图"),
  SHOP_IMAGE_APP_640X960_SMALL("version.17","125X125","APP640X960店铺小图"),
  SHOP_IMAGE_APP_640X1136_SMALL("version.17","125X125","APP640X1136店铺小图"),
  SHOP_IMAGE_APP_720X1280_SMALL("version.18","141X141","APP720X1280店铺小图"),
  SHOP_IMAGE_APP_480X800_BIG("version.19","164X164","APP480X800店铺大图"),
  SHOP_IMAGE_APP_720X1280_BIG("version.20","245X245","APP720X1280店铺大图"),
  SHOP_IMAGE_APP_640X960_BIG("version.21","218X218","APP640X960店铺大图"),
  SHOP_IMAGE_APP_640X1136_BIG("version.21","218X218","APP640X1136店铺大图"),
  SHOP_IMAGE_APP_480X800_FULL_SCREEN("version.23", "480X480", "APP480X800店铺满屏图"),
  SHOP_IMAGE_APP_720X1280_FULL_SCREEN("version.24", "720X720", "APP720X1280店铺满屏图"),
  SHOP_IMAGE_APP_640X960_FULL_SCREEN("version.25", "640X640", "APP640X960店铺满屏图"),
  SHOP_IMAGE_APP_640X1136_FULL_SCREEN("version.25", "640X640", "APP640X1136店铺满屏图"),
  SHOP_IMAGE_APP_320X480_FULL_SCREEN("version.26", "320X320", "APP320X480店铺满屏图"),

  CUSTOMER_IDENTIFICATION_IMAGE_UPLOAD("version.12","120X100","上传客户证件照片显示图"),
  CUSTOMER_IDENTIFICATION_IMAGE("version.22","310X200","上传客户证件照片显示图"),


  APP_USER_BILL_IMAGE_APP_320X480_SMALL("version.14","63X63","APP320X480账单小图"),
  APP_USER_BILL_IMAGE_APP_480X800_SMALL("version.15","94X94","APP480X800账单小图"),
  APP_USER_BILL_IMAGE_APP_640X960_SMALL("version.17","125X125","APP640X960账单小图"),
  APP_USER_BILL_IMAGE_APP_640X1136_SMALL("version.17","125X125","APP640X1136账单小图"),
  APP_USER_BILL_IMAGE_APP_720X1280_SMALL("version.18","141X141","APP720X1280账单小图"),

  APP_USER_BILL_IMAGE_APP_480X800_FULL_SCREEN("version.23", "480X480", "APP480X800账单满屏图"),
  APP_USER_BILL_IMAGE_APP_720X1280_FULL_SCREEN("version.24", "720X720", "APP720X1280账单满屏图"),
  APP_USER_BILL_IMAGE_APP_640X960_FULL_SCREEN("version.25", "640X640", "APP640X960账单满屏图"),
  APP_USER_BILL_IMAGE_APP_640X1136_FULL_SCREEN("version.25", "640X640", "APP640X1136账单满屏图"),
  APP_USER_BILL_IMAGE_APP_320X480_FULL_SCREEN("version.26", "320X320", "APP320X480账单满屏图"),
  SHOP_ENQUIRY_APP_SMALL("version.19","164X164","店铺查看询价单小图"),
  SHOP_ENQUIRY_APP_FULL("version.5","720XAUTO","店铺查看询价单大图"),

  SHOP_ADVERT_INFO_DESCRIPTION_IMAGE("version.5","720XAUTO","店铺宣传详细描述图片"),
  SHOP_ADVERT_INFO_DESCRIPTION_SMALL_IMAGE("version.23", "480X480", "店铺宣传详细描述小图"),
  RECOMMEND_SHOP_IMAGE("", "AUTO","显示原图"),


  //
  ;

  private String imageVersion;
  private String imageSize;
  private String description;

  private ImageScene(String imageVersion,String imageSize,String description) {
    this.imageVersion = imageVersion;
    this.imageSize = imageSize;
    this.description = description;
  }

  public String getImageVersion() {
    return this.imageVersion;
  }

  public String getDescription() {
    return this.description;
  }

  public String getImageSize() {
    return imageSize;
  }

}
