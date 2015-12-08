package com.bcgogo.utils;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 12-6-28
 * Time: 下午1:45
 * To change this template use File | Settings | File Templates.
 */
public class SolrConstant {
  public static final String SELECT_OPTION_NUMBER = "SelectOptionNumber";//单据商品下拉框显示个数
  
  public static final Long BASIC_SHOP_ID = 1L;
  /**
   * 用于SOLR reindex或者清楚数据的场景判断
   */
  public static final String ALL = "all"; //所有店面和基础数据
  public static final String DESIGNATED_STORE = "designated_store"; //指定店面
  public static final String DESIGNATED_DATA = "designated_data";  //指定数据

  /**
   * 导入数据的类型常量
   */
  public static final int UPLOAD_VEHICLE = 0;     //导入车辆信息
  public static final int UPLOAD_PRODUCT = 1;       //导入产品信息
  public static final int UPLOAD_PRODUCT_VEHICLE = 2;     //导入产品与车型的关联信息
  public static final int UPLOAD_LICENSE_PREFIX = 3;     //导入车牌号前缀
  public static final int UPLOAD_REGION = 4;            //导入地区信息
}
