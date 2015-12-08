package com.bcgogo.enums.shop;

/**
 * Created by XinyuQiu on 14-7-22.
 * 店铺广告状态，在有效期内状态 为ALL 或者 PART，超过有效期就是DISABLED
 */

public enum ProductAdType {
  ALL,//所有地区
  PART,//部分地区,
  DISABLED//失效
}
