package com.bcgogo.constant;

/**
 * 产品相关属性值常量
 * Created by IntelliJ IDEA.
 * User: ZouJianhong
 * Date: 12-4-21
 * Time: 下午3:41
 * To change this template use File | Settings | File Templates.
 */
public class ProductConstants {

  public class CheckStatus {
    /* 新产品 */
    public static final int CHECK_STATUS_NEW = 0;
    /* 审核通过 */
    public static final int CHECK_STATUS_PASS = 0;
    /* 审核未通过 */
    public static final int CHECK_STATUS_NOT_PASS = 0;

  }

  /*product solr build supplier 信息的长度*/
  public static final int PRODUCT_SUPPLIER_LIMIT = 3;

  public static final String  NO_PRODUCT_NAME = "品名不能为空，请输入品名!";
  public static final String  NO_PRODUCT_INFO = "商品信息不能为空，请重新输入!";
  public static final String  EXIST_PRODUCT_COMMODITY_CODE = "当前商品编码已经使用，请重新输入!";
  public static final String  EXIST_PRODUCT = "当前商品已经存在，请重新输入!";
  public static final String  NO_STOREHOUSE = "当前仓库不存在，请重新选择仓库!";

  public static final Double IN_SALES_AMOUNT_AVAILABLE=9999999999D; //上架量显示有货
  public static final Double IN_SALES_AMOUNT_UN_AVAILABLE=-9999999999D; //上架量显示无货


}
