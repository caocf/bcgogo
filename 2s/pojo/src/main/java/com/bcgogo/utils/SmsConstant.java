package com.bcgogo.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: caiweili
 * Date: 3/4/12
 * Time: 1:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class SmsConstant {
  //  public static final Integer SMS_STATUS_SUCCESS = 0; //成功
//  public static final Integer SMS_STATUS_ACT_NOT_EXIST = -1; //帐号不存在
//  public static final Integer SMS_STATUS_BC_INSF = -2; //账户余额不足
//  public static final Integer SMS_STATUS_BANNED = -3; //帐号已被禁用
//  public static final Integer SMS_STATUS_IP = -4; //ip鉴权失败
//  public static final Integer SMS_STATUS_PARA = -8; //缺少请求参数或参数不正确
//  public static final Integer SMS_STATUS_IL = -9; //内容不合法
//  public static final Integer SMS_STATUS_EXCEED_LIMIT = -10; //账户当日发送短信量已经超过允许的每日最大发送量

  //Bcgogo status
  public static final String SMS_STATUS_WAITING = "waiting";
  public static final String SMS_STATUS_SUCCESS = "success";
  public static final String SMS_STATUS_FAIL = "fail";
  public static final String SMS_STATUS_TIMEOUT = "timeout";
  public static final String SMS_STATUS_DELAY = "delay";
  public static final String SMS_STATUS_DELETE = "delete"; //全体客户保存定时钟成功

  public static final String SMS_EXECUTE_TYPE_ALL = "all";   //标明全体顾客
  public static final String SMS_EXECUTE_TYPE_NORMAL = "normal";   //标明全体顾客

  //shop status
  public static final String SMS_STATUS_LOW_BALANCE = "店铺余额不足"; // shop balance is too low
  public static final String SMS_STATUS_INCOMPLETE = "店铺数据不完整"; // 数据不完整
  public static final String SMS_STATUS_ACT_NOT_EXIST = "店铺短信帐号不存在"; //帐号不存在
//  public static final String SMS_STATUS_ERROR = -1010; // error


  public static final Integer SMS_TYPE_CHANGE_PWD = 1000;
  public static final Integer SMS_TYPE_NEW_USER = 1001;
  public static final Integer SMS_TYPE_DISCOUNT_ALERT = 1002;
  public static final Integer SMS_TYPE_DEBT_ALERT = 1003;
  public static final Integer SMS_TYPE_WASH_CARD_ALERT = 1004;
  public static final Integer SMS_TYPE_VERIFICATION = 1005;
  public static final Integer SMS_TYPE_REPAIR_FINISH = 1005;
  public static final Integer SMS_TYPE_MANUAL = 1006;
  public static final Integer SMS_TYPE_BUY_CARD = 1007;
  public static final Integer SMS_TYPE_CHANGE_USER_NO= 1008;

  public static final long SMS_FAILED_DELAY = 15 * 60 * 1000;

  public static final int SMS_RETRY_TIMES = 3;
  public static final int SMS_DELETE_TIMES = 4;

  public static final int SMS_UNIT_LENGTH = 67;
  public static final float SMS_UNIT_PRICE = 0.1f;

  public static final long MAX_SEND_TIMES = 5;
  public static final double DEFAULT_SMS_DEBT = 3;

  public static final int SMS_SEND_MOBILES_MAX_LENGHT = 450; //发送短信的手机号码不得超过500

  public static final int SMS_SEND_MOBILES_MAX_NUMBER = 100; //发送短信的手机号码不得超过500
  public static final int SMS_SEND_MOBILES_MAX_LENGTH = 1200; //发送短信的手机号码不得超过488


  //三通
  public static class Sms3TongConstant {
    public static final String name = "3TONG";
    public static final String tricomUrl = "http://3tong.cn:8080/ema_new/http/SendSms";
    //原
//    public static final String account = "838095";
//    //    public static final String normail_pass = "bcgogo66733331";
//    public static final String password = "afa9f3904d5f775bb82a513ae32dc9c0";
    //现
    public static final String account = "dh6763";
    //    public static final String normail_pass = "6763.com";
    public static final String password = "04ac74ac5bd65f04526820746c28dd7b";

    public static final int SMS_RESPONSE_ERROR = 0;
    public static final int mobileNumber = 100;
    public static final Map<Integer, String> smsCodeMapping = new HashMap();

    public static String getSmsSenderByName(Integer code) {
      return smsCodeMapping.get(code);
    }

    static {
      smsCodeMapping.put(-1, "三通：帐号不存在，请检查用户名或者密码是否正确");
      smsCodeMapping.put(-2, "三通：账户余额不足");
      smsCodeMapping.put(-3, "三通：帐号已被禁用");
      smsCodeMapping.put(-4, "三通：ip鉴权失败（需要ip校验的场合）");
      smsCodeMapping.put(-8, "三通：缺少请求参数或参数不正确（请检查用户名，密码，下发号码，下发内容是否为空，或者下发号码数量是否大于100个）");
      smsCodeMapping.put(-9, "三通：内容不合法（含有非法内容，请检查下发内容）");
      smsCodeMapping.put(-10, "三通：账户当日发送短信量已经超过允许的每日最大发送量（账户被限制每日发送短信数量的情况有用）");

    }
  }

  //亿美
  public static class SmsYiMeiConstant {

    public static final String name = "YiMei";
    public static final int mobileNumber = 200;
    public static final String SEPARATOR = ",";
    public static final String DEFAULT_SENDER_NAME = "苏州统购";
    public static final int SMS_STATUS_SUCCESS = 0;
    public static final int SMS_RESPONSE_ERROR = -1;
    public static final Map<Integer, String> smsCodeMapping = new HashMap<Integer, String>();

    public static String getSmsResponseCodeMap(Integer code) {
      return smsCodeMapping.get(code);
    }

    static {
      smsCodeMapping.put(-1, "新密码长度不能大于6");
      smsCodeMapping.put(0, "操作成功");
      smsCodeMapping.put(9, "操作频繁");
      smsCodeMapping.put(10, "客户端注册失败");
      smsCodeMapping.put(11, "企业信息注册失败");
      smsCodeMapping.put(17, "发送信息失败（未激活序列号或序列号和KEY值不对，或账户没有余额等）");
      smsCodeMapping.put(18, "发送定时信息失败");
      smsCodeMapping.put(22, "注销失败");
      smsCodeMapping.put(101, "客户端网络故障");
      smsCodeMapping.put(303, "客户端网络超时或网络故障");
      smsCodeMapping.put(305, "服务器端返回错误，错误的返回值（返回值不是数字字符串）");
      smsCodeMapping.put(307, "目标电话号码不符合规则，电话号码必须是以0、1开头");
      smsCodeMapping.put(308, "新密码不是数字，必须是数字");
      smsCodeMapping.put(997, "平台返回找不到超时的短信，该信息是否成功无法确定");
      smsCodeMapping.put(998, "由于客户端网络问题导致信息发送超时，该信息是否成功下发无法确定");
      smsCodeMapping.put(999, "三通：缺少请求参数或参数不正确（请检查用户名，密码，下发号码，下发内容是否为空，或者下发号码数量是否大于100个）");
      smsCodeMapping.put(911005, "客户端注册失败(请检查序列号、密码、key值是否配置正确)");
      smsCodeMapping.put(911003, "该序列号已经使用其它key值注册过了。（若无法找回key值请联系销售注销，然后重新注册使用。）");
      smsCodeMapping.put(-1, "系统异常");
      smsCodeMapping.put(-101, "命令不被支持");
      smsCodeMapping.put(-102, "用户信息删除失败");
      smsCodeMapping.put(-103, "用户信息更新失败");
      smsCodeMapping.put(-104, "指令超出请求限制");
      smsCodeMapping.put(-111, "企业注册失败");
      smsCodeMapping.put(-117, "发送短信失败");
      smsCodeMapping.put(-118, "获取MO失败");
      smsCodeMapping.put(-119, "获取Report失败");
      smsCodeMapping.put(-120, "更新密码失败");
      smsCodeMapping.put(-122, "用户注销失败");
      smsCodeMapping.put(-110, "用户激活失败");
      smsCodeMapping.put(-123, "查询单价失败");
      smsCodeMapping.put(-124, "查询余额失败");
      smsCodeMapping.put(-125, "设置MO转发失败");
      smsCodeMapping.put(-127, "计费失败零余额");
      smsCodeMapping.put(-128, "计费失败余额不足");
      smsCodeMapping.put(-1100, "序列号错误,序列号不存在内存中,或尝试攻击的用户");
      smsCodeMapping.put(-1102, "序列号正确,Password错误");
      smsCodeMapping.put(-1103, "序列号正确,Key错误");
      smsCodeMapping.put(-1104, "序列号路由错误");
      smsCodeMapping.put(-1105, "序列号状态异常 未用1");
      smsCodeMapping.put(-1106, "序列号状态异常 已用2 兼容原有系统为0");
      smsCodeMapping.put(-1107, "序列号状态异常 停用3");
      smsCodeMapping.put(-1108, "序列号状态异常 停止5");
      smsCodeMapping.put(-113, "充值失败");
      smsCodeMapping.put(-1131, "充值卡无效");
      smsCodeMapping.put(-1132, "充值卡密码无效");
      smsCodeMapping.put(-1133, "充值卡绑定异常");
      smsCodeMapping.put(-1134, "充值卡状态异常");
      smsCodeMapping.put(-1135, "充值卡金额无效");
      smsCodeMapping.put(-190, "数据库异常");
      smsCodeMapping.put(-1901, "数据库插入异常");
      smsCodeMapping.put(-1902, "数据库更新异常");
      smsCodeMapping.put(-1903, "数据库删除异常");
      smsCodeMapping.put(-9000, "数据格式错误,数据超出数据库允许范围 -9000;");
      smsCodeMapping.put(-9001, "序列号格式错误  -900");
      smsCodeMapping.put(-9002, "密码格式错误  -9002");
      smsCodeMapping.put(-9003, "唯一码格式错误  -9003");
      smsCodeMapping.put(-9004, "设置转发格式错误  -9004");
      smsCodeMapping.put(-9005, "公司地址格式错误  -9005");
      smsCodeMapping.put(-9006, "企业中文名格式错误  -9006");
      smsCodeMapping.put(-9007, "企业中文名简称格式错误-9007");
      smsCodeMapping.put(-9008, "邮件地址格式错误  -9008");
      smsCodeMapping.put(-9009, "企业英文名格式错误  -9009");
      smsCodeMapping.put(-9010, "企业英文名简称格式错误  -9010");
      smsCodeMapping.put(-9011, "传真格式错误  -901");
      smsCodeMapping.put(-9012, "联系人格式错误  -9012");
      smsCodeMapping.put(-9013, "联系电话  -9013");
      smsCodeMapping.put(-9014, "邮编格式错误  -9014");
      smsCodeMapping.put(-9015, "新密码格式错误  -9015");
      smsCodeMapping.put(-9016, "发送短信包大小超出范围  -9016");
      smsCodeMapping.put(-9017, "发送短信内容格式错误  -9017");
      smsCodeMapping.put(-9018, "发送短信扩展号格式错误  -9018");
      smsCodeMapping.put(-9019, "发送短信优先级格式错误  -9019");
      smsCodeMapping.put(-9020, "发送短信手机号格式错误  -9020");
      smsCodeMapping.put(-9021, "发送短信定时时间格式错误  -902");
      smsCodeMapping.put(-9022, "发送短信唯一序列值错误  -9022");
      smsCodeMapping.put(-9023, "充值卡号格式错误  -9023");
      smsCodeMapping.put(-9024, "充值密码格式错误  -9024");
    }
  }

  //众方
  public static class SmsSweConstant {
    public static final String name = "SWE";
    public static final String tricomUrl = "http://www.smswe.com:50000/sms/services/sms/";
    public static final String key = "richarapi";
    public static final String secret = "560e4c1e9d609840497ef45e38ec9f70f35427";
    public static final String username = "richar_ji";
    public static final String password = "hfps860621_";
    public static final long tokenTime = 1;  //获取token间隔时间
    public static final int SMS_STATUS_SUCCESS = 1;
    public static final int mobileNumber = 10;
    public static final Map<Integer, String> smsCodeMapping = new HashMap();

    public static String getSmsSenderByName(Integer code) {
      return smsCodeMapping.get(code);
    }

    static {
      smsCodeMapping.put(0, "众方:请求结果:失败");
      smsCodeMapping.put(1, "众方:请求结果:成功");
      smsCodeMapping.put(2, "众方:请求失败,非有效帐户");
      smsCodeMapping.put(3, "众方:认证失败");
      smsCodeMapping.put(4, "众方:SMS充值请求失败,非有效定单");
      smsCodeMapping.put(11, "众方:请求参数认证成功");
      smsCodeMapping.put(12, "众方:请求参数认证:未定义的接口方法");
      smsCodeMapping.put(13, "众方:授权认证:无效的授权码");
      smsCodeMapping.put(14, "众方:权限认证:未开通操作权限");
      smsCodeMapping.put(16, "众方:请求参数:IP地址访问受限");
      smsCodeMapping.put(17, "众方:请求参数:禁用的接口方法");
      smsCodeMapping.put(40, "众方:数据库操作失败");
      smsCodeMapping.put(41, "众方:数据库查询失败");
      smsCodeMapping.put(42, "众方:数据库写入失败");
      smsCodeMapping.put(43, "众方:数据不合法");
      smsCodeMapping.put(50, "众方:参数正确");
      smsCodeMapping.put(53, "众方:参数值类型不匹配");
      smsCodeMapping.put(54, "众方:参数类型必须是整数");
      smsCodeMapping.put(55, "众方:参数类型必须是email格式");
      smsCodeMapping.put(56, "众方:参数类型必须是日期格式(2010-01-01)");
      smsCodeMapping.put(57, "众方:参数类型必须是电话号码(固话或手机号码)");
      smsCodeMapping.put(58, "众方:参数类型必须是传真号码格式(区号+电话号码+分机号码)");
      smsCodeMapping.put(59, "众方:参数类型枚举值错误");
      smsCodeMapping.put(60, "众方:参数类型必须是域名");
      smsCodeMapping.put(61, "众方:发送开始时间格式不正确");
      smsCodeMapping.put(62, "众方:参数类型:超出发送开始时间设置限制(必须是7日内)");
      smsCodeMapping.put(63, "众方:参数类型必须是日期时间格式(2010-01-01 00:00:00)");
      smsCodeMapping.put(64, "众方:参数类型必须是手机号码格式");
      smsCodeMapping.put(65, "众方:参数值不正确");
      smsCodeMapping.put(66, "众方:用户名必须是数字,字母或下划线");
      smsCodeMapping.put(67, "众方:密码必须是数字或字母");
      smsCodeMapping.put(68, "众方:参数超过限定长度");
      smsCodeMapping.put(101, "众方:重复的用户名");
      smsCodeMapping.put(102, "众方:重复的邮箱地址");
      smsCodeMapping.put(103, "众方:恶意注册限制");
      smsCodeMapping.put(104, "众方:旧密码不正确");
      smsCodeMapping.put(105, "众方:选号:已经选过号码");
      smsCodeMapping.put(106, "众方:选号:号码已经被使用");
      smsCodeMapping.put(107, "众方:选号:不存在的接收号码");
      smsCodeMapping.put(108, "众方:激活用户:无效的激活码");
      smsCodeMapping.put(109, "众方:激活用户:已经激活过了");
      smsCodeMapping.put(110, "众方:注册用户邀请码:无效的注册邀请码");
      smsCodeMapping.put(111, "众方:注册用户邀请码:非代理商用户,不能使用邀请码注册");
      smsCodeMapping.put(112, "众方:注册用户:非有效域名");
      smsCodeMapping.put(113, "众方:注册用户:该域名当日注册数超过限制数目");
      smsCodeMapping.put(114, "众方:登陆:用户名或密码错误");
      smsCodeMapping.put(115, "众方:用户状态:用户已经被冻结");
      smsCodeMapping.put(116, "众方:用户状态:用户已经被删除");
      smsCodeMapping.put(117, "众方:用户状态:用户未激活");
      smsCodeMapping.put(118, "众方:用户状态:无效用户");
      smsCodeMapping.put(119, "众方:没有操作权限");
      smsCodeMapping.put(120, "众方:非指定域名的所属用户或下级用户");
      smsCodeMapping.put(121, "众方:ip地址已经被限制注册操作");
      smsCodeMapping.put(122, "众方:短信注册码已经产生,请稍候接收");
      smsCodeMapping.put(123, "众方:该手机号码已经使用");
      smsCodeMapping.put(124, "众方:注册验证码不正确");
      smsCodeMapping.put(125, "众方:该用户名未注册");
      smsCodeMapping.put(126, "众方:忘记密码:用户名和短信手机号码不匹配");
      smsCodeMapping.put(127, "众方:非域名所属用户");
      smsCodeMapping.put(151, "众方:DB的字段域无效:无效的用户名");
      smsCodeMapping.put(152, "众方:DB的字段域不存在");
      smsCodeMapping.put(153, "众方:DB的字段域冲突");
      smsCodeMapping.put(154, "众方:DB的业务逻辑冲突");
      smsCodeMapping.put(155, "众方:DB的字段域无效:非上下级用户关系");
      smsCodeMapping.put(156, "众方:DB的字段域无效:转入账户和转出账户不能相同");
      smsCodeMapping.put(157, "众方:DB的字段域无效::转出账户余额不足");
      smsCodeMapping.put(158, "众方:DB的字段域无效:转出余额必须大于0");
      smsCodeMapping.put(159, "众方:域名已经存在");
      smsCodeMapping.put(160, "众方:域名管理用户名或密码错误");
      smsCodeMapping.put(161, "众方:非代理商用户不能设置为域名所属用户");
      smsCodeMapping.put(162, "众方:资金转移:账户密码不正确");
      smsCodeMapping.put(163, "众方:DB的字段域无效:必须开通代理商");
      smsCodeMapping.put(164, "众方:DB的字段域无效:无效的token");
      smsCodeMapping.put(700, "众方:发送成功");
      smsCodeMapping.put(701, "众方:发送失败");
      smsCodeMapping.put(702, "众方:短信号码本文件不存在");
      smsCodeMapping.put(703, "众方:无效号码");
      smsCodeMapping.put(704, "众方:账户余额不足");
      smsCodeMapping.put(705, "众方:账户余额低于或等于4元，群发传真不能超过10份");
      smsCodeMapping.put(706, "众方:单发短信一次最多发送10个号码");
      smsCodeMapping.put(707, "众方:群发短信一次最多发送10万个号码");
      smsCodeMapping.put(708, "众方:用户资费数据错误");
      smsCodeMapping.put(709, "众方:短信内容包含敏感关键字");
      smsCodeMapping.put(710, "众方:只能取消状态为待发送或发送中的任务");
      smsCodeMapping.put(711, "众方:只能暂停状态为待发送或发送中的任务");
      smsCodeMapping.put(712, "众方:只能恢复状态为已经发送, 发送结果为余额不足或暂停的任务");
      smsCodeMapping.put(713, "众方:只能重发状态为已经发送, 发送结果为成功的任务");
      smsCodeMapping.put(714, "众方:任务不存在");
      smsCodeMapping.put(631, "众方:发送邮件失败");
      smsCodeMapping.put(1001, "众方:连接SMS服务器失败");
    }
  }

  //内容
  public class MsgTemplateContentConstant {
    public static final String newpassword = "{newpassword}";
    public static final String orderNumber = "{orderNumber}";
    public static final String bcgogoOrderReceiptNo = "{bcgogoOrderReceiptNo}";
    public static final String vercode = "{vercode}";
    public static final String regUrl = "{regUrl}";
    public static final String invitationCode = "{invitationCode}";
    public static final String shortName = "{shortName}";
    public static final String storeManager = "{storeManager}";
    public static final String name = "{name}";
    public static final String userNo = "{userNo}";
    public static final String shopName = "{shopName}";
    public static final String shopMobile = "{shopMobile}";
    public static final String shopLandline = "{shopLandline}";  //    SHOP 电话
    public static final String licenceNo = "{licenceNo}";        //车牌
    public static final String appointName = "{appointName}";
    public static final String customer = "{customer}";
    public static final String userName = "{userName}";
    public static final String password = "{password}";
    public static final String carOwnerName = "{carOwnerName}";
    public static final String money = "{money}";
    public static final String year = "{year}";
    public static final String faultCode = "{faultCode}";
    public static final String day = "{day}";
    public static final String mouth = "{mouth}";
    public static final String contact = "{contact}";
    public static final String mobile = "{mobile}";
    public static final String time = "{time}";
    public static final String phone = "{phone}";
    public static final String memoTime = "{memoTime}";                         //   备忘时间
    public static final String repaymentDate = "{repaymentDate}";               //预计还款日
    public static final String productNameAndCounts = "{productNameAndCounts}";
    public static final String discount = "{discount}";                           //折扣
    public static final String actualCollection = "{actualCollection}";         //实收款
    public static final String debt = "{debt}";                                    //欠款
    public static final String receivable = "{receivable}";                    //应收款
    public static final String services = "{services}";                    //服务项目
    public static final String pauseMark = "、";                    //服务项目
    public static final String ge = "个、";                    //服务项目
    public static final String faultCodeContent = "故障提醒";
  }

  public class VelocityMsgTemplateConstant {
    // 会员消费短信相关
    public static final String cardOwnerName = "cardOwnerName";
    public static final String consumeDate = "consumeDate";
    public static final String hasConsumeItems = "hasConsumeItems";
    public static final String consumeItems = "consumeItems";
    public static final String consumeAmount = "consumeAmount";
    public static final String hasRemainItems = "hasRemainItems";
    public static final String remainItems = "remainItems";
    public static final String remainAmount = "remainAmount";
    public static final String limitless = "无限";
    public static final String shopName = "shopName";
    public static final String shopMobile = "shopMobile";
    public static final String memberNo = "memberNo";
    public static final String password = "password";
    public static final String passwordChanged = "passwordChanged";
    public static final String consumePrice = "consumePrice";
  }

  //短信模板类型
  public class MsgTemplateTypeConstant {
    public static final String sendFinishMsg1 = "sendFinishMsg1";
    public static final String sendFinishMsg2 = "sendFinishMsg2";

    public static final String sendAllocatedAccountMsg1 = "sendAllocatedAccountMsg1";
    public static final String verificationCode = "verificationCode";
    public static final String invitationCodeToSupplier = "invitationCodeToSupplier";
    public static final String invitationCodeToCustomer = "invitationCodeToCustomer";
    public static final String resetPassword = "resetPassword";
    public static final String appResetPassword = "appResetPassword";
    public static final String changePassword = "changePassword";
    public static final String changeUserNo= "changeUserNo";
    public static final String changeMemberPassword = "changeMemberPassword";

    public static final String sendDebtMsg1 = "sendDebtMsg1";
    public static final String sendDebtMsg2 = "sendDebtMsg2";
    public static final String sendDebtMsg3 = "sendDebtMsg3";
    public static final String sendDebtMsg4 = "sendDebtMsg4";
    public static final String sendDebtMsg5 = "sendDebtMsg5";

    public static final String sendDiscountMsg1 = "sendDiscountMsg1";
    public static final String sendDiscountMsg2 = "sendDiscountMsg2";
    public static final String sendDiscountMsg3 = "sendDiscountMsg3";
    public static final String sendDiscountMsg4 = "sendDiscountMsg4";

    public static final String customerRemindGuarantee = "customerRemindGuarantee";
    public static final String customerRemindValidateCar = "customerRemindValidateCar";
    public static final String customerRemindBirthday = "customerRemindBirthday";
    public static final String customerRemindDebt = "customerRemindDebt";
    public static final String customerRemindKeepInGoodRepair = "customerRemindKeepInGoodRepair";
    public static final String registrationReminderForBackgroundAuditStaff = "registrationReminderForBackgroundAuditStaff";
    public static final String registerMsgSendToCustomer = "registerMsgSendToCustomer";
    public static final String trialRegisterSmsSendToCustomer = "trialRegisterSmsSendToCustomer";
    public static final String bcgogoOrderSms = "bcgogoOrderSms";
    public static final String appointService = "appointService";
    public static final String memberService = "memberService";
    public static final String maintainMileage = "maintainMileage"; //保养里程提醒

    public static final String memberConsumeMsg = "memberConsumeMsg";
    public static final String memberBuyCardMsg = "memberBuyCardMsg"; //会员购卡短信
    public static final String salesAccepted = "salesAccepted";  //销售单接受短信
    public static final String salesRefuse = "salesRefuse";      //销售单拒绝短信
    public static final String stockingCancel = "stockingCancel";//备货中作废短信
    public static final String shippedCancel = "shippedCancel";    //已发货作废短信
    public static final String returnsAccepted = "returnsAccepted";//退货单接受短信
    public static final String returnsRefuse = "returnsRefuse";     //退货单接受短信
    public static final String memberRenewCardMsg = "memberRenewCardMsg";   //会员续卡短信
    public static final String settledRemindMsg="settledRemindMsg"; //结算提醒
    public static final String faultInfoCodeMsg = "faultInfoCodeMsg";   //故障提醒
  }

  public class CustomerRemindType {
    public static final int guarantee = 0;            //保险
    public static final int validateCar = 1;          //验车
    public static final int birthday = 2;             //  生日
    public static final int debt = 3;                   // 欠款
    public static final int keepInGoodRepair = 4;         // 保养
    public static final int appointService = 5;         // 自定义的预约服务
    public static final int memberService = 6;        //会员服务
    public static final int maintainMileage = 7;        //保养里程
    public static final int settledRemindMsg=8;        //结算提醒
  }

  //联逾
  public static class SmsLianYuConstant {

    public static final String name = "LianYu";
    public static final int mobileNumber = 100;
    public static final String SEPARATOR = ",";
    public static final String DEFAULT_SENDER_NAME = "苏州统购";
    public static final int SMS_STATUS_SUCCESS = 0;
    public static final int SMS_RESPONSE_ERROR = -1;
    public static final Map<Integer, String> smsCodeMapping = new HashMap<Integer, String>();

    public static final String smsSend = "http://115.29.44.189:8080/sms/smsInterface.do";   //短信发送接口
    public static final String balanceInquery = "http://115.29.44.189:8080/sms/smsBalance.do"; //余额查询接口
    public static final String detectionStopWords = "http://115.29.44.189:8080/sms/checkFilterWord.do";  //检测屏蔽词接口
    public static final String returnStatus = "http://115.29.44.189:8080/sms/smsReport.do";   //获取状态报告接口
    public static final String smsReply = "http://115.29.44.189:8080/sms/smsReply.do";   //短信回复接口

    public static final String userName = "13646203695";
    public static final String password = "123456";

    public static String getSmsResponseCodeMap(Integer code) {
      return smsCodeMapping.get(code);
    }

    static {
      smsCodeMapping.put(0, "操作成功");
      smsCodeMapping.put(1, "用户名不能为空");
      smsCodeMapping.put(2, "密码不能为空");
      smsCodeMapping.put(3, "短信内容不能为空");
      smsCodeMapping.put(4, "手机号码不能为空");
      smsCodeMapping.put(5, "用户名错误");
      smsCodeMapping.put(6, "密码错误");
      smsCodeMapping.put(7, "用户账号已被锁定");
      smsCodeMapping.put(8, "包含屏蔽词");
      smsCodeMapping.put(9, "用户账号余额不足，请充值后发送");
      smsCodeMapping.put(10, "检测内容不能为空");
      smsCodeMapping.put(11, "用户未开放http接口访问");
      smsCodeMapping.put(12, "用户访问接口ip错误");
    }
  }


}