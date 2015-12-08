package com.bcgogo.txn.dto.supplierComment;

/**
 * 供应商评价常用常量
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 13-3-15
 * Time: 上午9:24
 * To change this template use File | Settings | File Templates.
 */
public class CommentConstant {

  //客户点评供应商评价前校验

  public static final String SUPPLIER_COMMENT_FAIL = "评分失败,请重试";

  public static final String PURCHASE_ORDER_NULL = "采购单不存在";

  public static final String PURCHASE_INVENTORY_NULL = "采购单不存在";

  public static final String COMMENT_SCORE_NULL = "对不起，请完成所有的评分项目，谢谢";

  public static final String SUPPLIER_COMMENT_DONE = "该入库单对应的采购单已评价";

  public static final String SALE_ORDER_NUll = "该入库单对应的销售单不存在";

  public static final String SUPPLIER_COMMENT_NULL = "该采购单点评记录不存在,追加失败";

  public static final String ADD_COMMENT_CONTENT_EMPTY = "请输入追加内容";

  public static final String ADD_CONTENT_FORMAT = "追加：";

  public static final String ADD_COMMENT_CONTENT_DONE ="您已追加评论,不能再次追加";

  public static final String ADD_CURRENT_DAY = "当天";

  public static final String ADD_DAY ="天后";

  public final static int SPAN_SIZE = 19; //页面展示时宽度样式

  public final static double SCORE_UNIT = 0.5; //分数单位

  public final static String COMMENT_SPACE ="_______";//供应商评价内容和追加内容间隔标志

  public static final String CUSTOMER_BEGIN ="来自";

  public static final String CUSTOMER_END ="的客户";

  public static final String COMMENT_LONG ="评价内容过长,请修改";

  public static final int COMMENT_LENGTH = 500;//评价内容和追加内容的最大字数

  public static final String ADD_COMMENT_CONTENT_FAIL ="追加评论失败,请重试";

  public static final String ORDER_NULL = "单据不存在";

  public static final String COMMENT_DONE = "单据已评价";

  public static final int APP_USER_COMMENT_LENGTH = 100; //手机端用户评价最大字数

  public static final String ORDER_BUSY = "单据正在被操作,请稍后评价";


}
