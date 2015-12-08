package com.bcgogo.utils;

/**
 * Created by IntelliJ IDEA.
 * User: Lucien
 * Date: 12-4-6
 * Time: 上午11:30
 * To change this template use File | Settings | File Templates.
 */
public class StatConstant {
  //common use
  public static final int DEFAULT_NEGATIVE = -1;//常用负数

  public static final int QUERY_SIZE = 10;//默认查询top 10

  public static final int RESULT_SIZE = 3;//查询结果的大小
  public static final String QUERY_BY_AMOUNT = "按数量统计";
  public static final String QUERY_BY_MONEY = "按金额统计";
  public static final String ALL_MONTH = "所有月份";
  public static final String ONE_MONTH = "一个月周期";
  public static final String THREE_MONTH = "三个月周期";
  public static final String SIX_MONTH = "半年周期";
  public static final String ONE_YEAR = "一年周期";
  public static final String QUERY_BY_PRODUCT = "按商品统计";
  public static final String QUERY_BY_SUPPLIER = "按供应商统计";
  public static final String PRODUCT = "商品";
  public static final String SUPPLIER = "供应商";
  public static final int RETURN_SIZE = 4;//查询结果的大小

  //会员消费统计
  public static final String MEMBER_STATISTICS = "memberConsumeStatistics";
  public static final int TWO_QUERY_SIZE = 2;//返回三个查询结果
  public static final String NOT_CONTAIN_REPEAL = "NO";//查询结果不包含作废

  public static long EMPTY_SHOP_ID = -1L;//后台CRM->店铺财务统计->采购统计使用

  //销售退货统计
  public static final String SALES_RETURN_STATISTICS = "salesReturnStatistics";

  //入库退货统计
  public static final String INVENTORY_RETURN_STATISTICS = "inventoryReturnStatistics";

  //客户交易统计
  public static final String CUSTOMER_STATISTICS = "customerStat";


  //供应商交易统计
  public static final String SUPPLIER_STATISTICS = "supplierStat";

  //分类交易统计
  public static final String CATEGORY_STATISTICS = "categoryStat";

}
