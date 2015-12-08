package com.bcgogo.utils;

/**
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 11-12-28
 * Time: 下午5:19
 * To change this template use File | Settings | File Templates.
 */

public class MemberConstant {

  //会员卡验证
  public static final String  PRODUCT_NULL = "材料为空,请重新输入";
  public static final String  PRODUCT_INVENTORY_LACK = "材料库存不足,请重新输入";
  public static final String  REPAIR_ORDER_EXIST = "此车牌号存在未结算单据";
  public static final String  REPAIR_ORDER_NULL = "施工内容为空,请重新输入";
  public static final String  REPAIR_ORDER_REPEAL = "此单据已经被作废";
  public static final String  REPAIR_ORDER_SETTLED = "此单据已经结算过,不能重复结算";
  public static final String  REPAIR_ORDER_SETTLED_SAVE = "此单据已经结算过,不能再次改单";
  public static final String  REPAIR_ORDER_SETTLED_FINISH = "此单据已经结算过,不能重复完工";
  public static final String  WASH_BEAUTY_ORDER_NULL = "单据内容为空,请重新输入";
  public static final String  MEMBER_NO_NEED = "由于您输入了储值金额或者有计次划卡项目,请输入会员卡号";
  public static final String  MEMBER_NOT_EXIST = "无此会员号码,请重新输入";
	public static final String  MEMBER_INVALID = "此会员已经失效,请输入其他会员号";
  public static final String  PASSWORD_NO_CORRECT = "密码不正确,请重新输入密码";
  public static final String  MEMBER_BALANCE_NOT_ENOUGH = "该会员卡储值金额不足,请充值";
  public static final String  REPAIR_ORDER_DTO_NULL = "请填写施工单内容";
	public static final String  MEMBER_NO_CONTAIN_SERVICE = "该会员无此项计次划卡施工项目或已过期";
  public static final String  MEMBER_VALIDATE_SUCCESS = "success";
  public static final String  SUBMIT_EXCEPTION = "提交失败,请联系客服人员";
  public static final String  SHOP_NO_CONTAIN_SERVICE = "无此施工项目，不能进行计次划卡";
  public static final String  AJAX_SUBMIT_FAILURE = "前台校验失败";
  public static final String  TWO_OR_MORE_MEMBER_BY_ONE_MEMBER_NO = "该会员号下有多张会员";
  public static final String  MEMBER_SERVICE_OUT_DEADLINE = "该计次划卡项目已过有效期";
  public static final String  VEHICLE_NO_IS_LIMIT = "该计次划卡项目是限制车牌项目,该车牌无法消费";
  public static final String  MEMBER_SERVICE_OUT_COUNT = "该计次划卡项目次数已用完";
  public static final String  MEMBER_PASSWORD_ERROR = "CONFIRM_PASSWORD_FAIL";
  public static final String  CHANGE_PASSWORD_SUCCESS = "CHANGE_PASSWORD_SUCCESS";
  public static final String  CHANGE_PASSWORD_FAIL = "CHANGE_PASSWORD_FAIL";
  public static final String  MEMBER_CONFIG_SWITCH_NAME = "MEMBERSWITCH";
  public static final String  CUSTOMER_CONTAIN_SERVICE = "该客户不是会员，不能进行计次划卡消费";
  public static final String  MEMBER_CUSTOMER_NOT_EXIST = "此会员卡的客户已经删除，不能用此会员卡结算";

  public static final String CHECK_RESULT = "resu";//保存校验结果 用于json

  public static final String DEPOSIT_NOT_ENOUGH = "该供应商定金余额不足";
  public static final String CUSTOMER_DEPOSIT_NOT_ENOUGH = "该客户预售款金额不足";
}

