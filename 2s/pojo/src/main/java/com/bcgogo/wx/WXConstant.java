package com.bcgogo.wx;

import com.bcgogo.common.CommonUtil;

import java.security.PublicKey;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 14-8-28
 * Time: 上午11:58
 * To change this template use File | Settings | File Templates.
 */
public class WXConstant {


  //消息单价
  public static Double MSG_PRICE = 0.03D;
  // 缺省赠送金额
  public static Double DEFAULT_WX_GIFT_TOTAL = 3D;
  //推广统购微信公共号二维码场景
  public static final String BC_WX_EXTEND_SCENE = "qrscene_1";
  //苏州统购publicNo
  public static final String YI_FA_PUBLIC_ID = "gh_e1f30832359c";
  //统购车业publicNo
  public static final String OFFICIAL_PUBLIC_ID = "gh_8517c1b80e05";
  //威尼尤至publicNo
  public static final String WEI_NI_YOU_ZHI_PUBLIC_ID = "gh_bd8827987f5e";
  //利宜行publicNo
  public static final String LI_YI_XING_PUBLIC_ID = "gh_90e5bd7565e1";
  //获取access_token过期时间
  public static Long GET_ACCESS_TRYING_EXPIRY = 200L;
  //
  public static Long RESP_HANDLE_EXPIRY = 1000L;

  //令牌
  public static final String token = "1b51f05aac9a79170110df3f4b3510cc";
  //接口返回值
  public static final String SUCCESS = "0";


  //永久二维码   最大场景值为100000（目前参数只支持1--100000）
  public static final int QR_LIMIT_SCENE_MAX_SIZE = 50;

  public static final int QR_CODE_SCENE_RESERVED = 10;
  //临时二维码最大有效时间(7天)，以秒为单位。
  public static final Long TEMP_QR_CODE_MAX_EXPIRE_SECONDS = 604800L;

  //微信帐号app_id
  public static String MIRROR_APP_ID;
  // 微信帐号SECRET
  public static String MIRROR_SECRET;

   static  {
    if (MIRROR_APP_ID == null) {
      if (CommonUtil.isDevMode()) {
        MIRROR_APP_ID = "wx5b48b13bde7e9e4d";
        MIRROR_SECRET = "d85f66bc3ccb0bac62bbc2bee4f65696";
      } else { //利宜行
        MIRROR_APP_ID = "wx1cedd36d6b94a448";
        MIRROR_SECRET = "f0ee1ab57ea5f1d5c892e9057ed1f9bc";
      }
    }


  }


  /**
   * *********************************微信图库文件夹名****************************************************************
   */

  public static final String IMAGE_FILE_LIB_BILL = "bill";

  public static final String IMAGE_FILE_LIB_MEMBER = "member";

  public static final String IMAGE_FILE_LIB_VEHICLE = "vehicle";

  public static final String PREFIX_WX_LIB_IMAGE_NUM = "WX_LIB_IMAGE_NUM_";

//  public static final int WX_IMAGE_LIB_BILL_NUM=15;
//
//  public static final int WX_IMAGE_LIB_MEMBER_NUM=15;
//
//  public static final int WX_IMAGE_LIB_VEHICLE_NUM=15;

  //发送策略 demo-演示，official-正式发送
//  public static final String SEND_STRATEGY_DEMO="demo";
//  public static final String SEND_STRATEGY_OFFICIAL="official";

  //生产域名
  public static final String OFFICIAL_DOMAIN = "http://reg.bcgogo.com";
  //测试域名
  public static final String TEST_DOMAIN = "http://wx.bcgogo.cn:8035";
  //开发环境域名
  public static final String DEVELOP_DOMAIN = "http://wx.bcgogo.cn:9006";

  public static final String TO_ORDER_DETAIL_URL = "/web/wxTxn.do?method=2w&_i={orderId}&o={orderType}&v={vehicleNo}";

  public static final String TO_MEMBER_CARD_DETAIL_URL = "/web/wxTxn.do?method=mCard&m={MEMBER_ID}&o={OPEN_ID}";

  public static final String TO_VEHICLE_BIND_URL = "/web/weChat.do?method=2Bind&_i={OPENID}";

  public static final String TO_VEHICLE_EDIT_URL = "/web/weChat.do?method=vEdit&_i={U_V_ID}";

  public static final String TO_VEHICLE_VIOLATE_REGULATION_URL = "/web/weChat.do?method=vReg&_i={USER_VEHICLE_ID}";
  //素材详细
  public static final String TO_ARTICLE_DETAIL_URL = "/web/weChat.do?method=aDetail&_i={id}";
  //碰撞视频
  public static final String TO_MIRROR_VIDEO_URL = "/web/mirror/2Video/{OPEN_ID}";

  public static final String TO_MIRROR_PV_MSG_URL = "/web/mirror/to_talk/{TYPE}/{OPEN_ID}/{APP_USER_NO}";

  //图库里图的数量
  public static final int USER_BIND_VEHICLE_MAX_SIZE = 10;

  /**
   * *************************** menu type **********************************************************
   */

  public static String MENU_TYPE_CLICK = "click";

  public static String MENU_TYPE_VIEW = "view";

  public static String MENU_TYPE_TEXT = "_M_KEY_TEXT_";

  /**
   * ********************************************** 菜单等操作自动回复内容 response content ********************************
   */

  public static final String HTML_WELCOME_WORD_DEFAULT = "欢迎关注【众星汽车修理厂】 {/玫瑰}{/玫瑰}<br>本店无线名：my-wifi<br>无线密码：1234567890<br>" +
    "&nbsp;&nbsp;绑定您的车牌号，并输入车架号，发动机号（方便您随时查违章）。<br>" +
    "&nbsp;&nbsp;后续可以查询在我店消费明细，也可以点评我店的服务。<br>" +
    "&nbsp;&nbsp;欢迎转发，转发有礼，欢迎光临。<br>";
  //scene:通过场景关注成功
  public static final String CONTENT_SCENE_SUBSCRIBE = "欢迎关注【{NAME}】 /玫瑰，您将收到本店的实时账单和优惠信息。\n绑定您的车牌号，立即查收汽车相关消费账单，点击<a href='{B_URL}'>立即绑定</a>。";
  //直接关注统购车业
  public static final String CONTENT_SUBSCRIBE = "欢迎关注统购车业 /玫瑰/玫瑰/玫瑰，绑定您的车牌号，即可查收汽车相关消费账单。\n点击<a href='{B_URL}'>立即绑定</a>。";
  //关注利宜行
  public static final String CONTENT_SUBSCRIBE_LIYIXING = "欢迎关注利宜行 /玫瑰/玫瑰/玫瑰。";

  public static final String CONTENT_ACCIDENT_SPECIALIST = "您已成为事故专员 /玫瑰/玫瑰/玫瑰。";
  //提示绑定车牌
  public static final String CONTENT_VEHICLE_BIND = "您还未绑定车辆，点击<a href='{B_URL}'>立即绑定</a>。";

  public static final String CONTENT_VEHICLE_BIND_S1 = "点击<a href='{B_URL}'>立即绑定</a>。";

  public static final String CONTENT_DEFAULT_REPLY = "感谢您的留言 /微笑";

  /**
   * ************************ 消息模版对应key *********************************************************
   */

  public static String TEMPLATE_KEY_APPOINT_REMIND_PREFIX = "template_key_appoint_remind_";
  //预约提醒服务
  public static String TEMPLATE_TITLE_VEHICLE_VIOLATE_REGULATION = "违章提醒";

  public static String TEMPLATE_TITLE_VEHICLE_RESCUE = "车辆请求救援提醒";

  public static String TEMPLATE_TITLE_VEHICLE_NOTIFY = "待处理通知";

  public static String TEMPLATE_TITLE_NEW_VEHICLE_VIOLATE_REGULATION = "新违章通知";

  public static String TEMPLATE_TITLE_APPOINT_REMIND = "预约到期提醒";

  public static String TEMPLATE_TITLE_MIRROR_APPOINT_REMIND = "服务预约提醒";

  public static String TEMPLATE_TITLE_MEMBER_CONSUME = "会员消费通知";

  public static String TEMPLATE_TITLE_CONSUME_REMIND = "消费提醒";

  public static String TEMPLATE_TITLE_IMPACT_REMIND = "车辆碰撞提醒";

  public static String TEMPLATE_TITLE_VEHICLE_FAULT_REMIND = "车辆异常提醒";

  public static String TEMPLATE_TITLE_VEHICLE_FAULT_REMIND_FIRST = "尊敬的{VEHICLE_NO}车主，您的车辆产生异常的震动：";
  //绑定车牌号
  public static String TEMPLATE_KEY_VEHICLE_BIND = "_template_key_HOrTWarb36GDqdUbfkyy26maBYLfPfl-ZIKpeh0bUq8";

  /**
   * *************************************************** memcached KEY ***************************************************************************
   */

  public static final String USER_LAST_ACTION_PREFIX = "_user_last_action_";

  public static final String WX_IMAGE_LIB_PREFIX = "_wx_image_lib_";
  //the secret key
  public static final String WX_ACCOUNT_SECRET_KEY = "_wx_account_secret_key_";
  //memcached  access_token_key
  public static final String KEY_PREFIX_ACCESS_TOKEN = "wx_access_token_key_";

  public static final String KEY_PREFIX_JS_API_TICKET = "wx_Js_api_ticket_key_";
  //the account key
  public static final String KEY_PREFIX_ACCOUNT = "mem_account_key_";
  //公共号需要把请求转发出去的mem key
//  public static final String KEY_PREFIX_DISPATCH_NO="dispatch_public_no_key_";
  //转发请求公共号的前缀
  public static final String DISPATCH_KEY_PREFIX = "dispatch_key_";

  /**
   * ************************* MENU EVENT KEY ******************************************
   */
  //车辆绑定
  public static final String MENU_EVENT_KEY_BIND = "_M_KEY_VEHICLE_BIND";
  //历史账单
  public static final String MENU_EVENT_BILL_HISTORY = "_M_KEY_BILL_HISTORY";
  //会员卡
  public static final String MENU_EVENT_MEMBER_CARD = "_M_KEY_MEMBER_CARD";
  //我的车辆
  public static final String MENU_EVENT_VEHICLE_LIST = "_M_KEY_VEHICLE_LIST";
  //违章查询
  public static final String MENU_EVENT_VEHICLE_VIOLATE = "_M_KEY_VEHICLE_VIOLATE";

  /**
   * ************************* memcached data expirationTime ******************************************
   */
  //事件接口处理完成时间。可设大点 防止接口被多次调用
  public static final Long M_EXPIRE_RESP_HANDLE_START = 60 * 1000l;  // 5min

  public static final Long M_EXPIRE_RESP_HANDLE_FINISH = 60 * 1000l;  // 30s
  //缓存上次操作记录 5分钟
  public static final Long M_EXPIRE_USER_LAST_ACTION = 5 * 60 * 1000l;
  //ACCESS_TOKEN缓存时间 30分钟
  public static final Long M_EXPIRE_ACCESS_TOKEN = 30 * 60 * 1000l;
  //ACCOUNT缓存时间 24小时
  public static final Long M_EXPIRE_ACCOUNT = 24 * 60 * 60 * 1000l;
  //wx_image_lib缓存时间 24小时
  public static final Long M_EXPIRE_WX_IMAGE_LIB = 24 * 60 * 60 * 1000l;

  /**
   * ************************* interface url list ***********************************************
   */
  //获取access_token地址
  public static final String URL_ACCESS_TOKEN = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid={APPID}&secret={SECRET}";
  //获取js_api_ticket地址
  public static final String URL_JS_API_TICKET = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token={ACCESS_TOKEN}&type=jsapi";
  //微信长链接转短链接接口
  public static final String URL_GET_SHORT = "https://api.weixin.qq.com/cgi-bin/shorturl?access_token={ACCESS_TOKEN}";
  //创建菜单
  public static final String URL_CREATE_MENU = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token={ACCESS_TOKEN}";
  //删除菜单
  public static final String URL_DELETE_MENU = "https://api.weixin.qq.com/cgi-bin/menu/delete?access_token={ACCESS_TOKEN}";
  //创建二维码ticket
  public static final String URL_CREATE_QR_CODE = "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token={ACCESS_TOKEN}";
  //通过ticket换取二维码
  public static final String URL_SHOW_QR_CODE = "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket={TICKET}";
  //发送模版消息
  public static final String URL_SEND_TEMPLATE_MSG = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token={ACCESS_TOKEN}";
  //发送客服消息
  public static final String URL_SEND_CUSTOM_MSG = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token={ACCESS_TOKEN}";
  //群发消息
  public static final String URL_SEND_MASS_MSG = "https://api.weixin.qq.com/cgi-bin/message/mass/send?access_token={ACCESS_TOKEN}";
  //分组群发消息
  public static final String URL_SEND_GROUP_MASS_MSG = "https://api.weixin.qq.com/cgi-bin/message/mass/sendall?access_token={ACCESS_TOKEN}";
  //获取微信粉丝列表
  public static final String URL_GET_WX_USERS = "https://api.weixin.qq.com/cgi-bin/user/get?access_token={ACCESS_TOKEN}";
  //获取用户基本信息
  public static final String URL_GET_WX_USER_INFO = "https://api.weixin.qq.com/cgi-bin/user/info?access_token={ACCESS_TOKEN}&openid={OPENID}&lang=zh_CN";
  //微信创建分组
  public static final String URL_CREATE_WX_GROUP = "https://api.weixin.qq.com/cgi-bin/groups/create?access_token={ACCESS_TOKEN}";

  public static final String URL_UPDATE_WX_GROUP_USER = "https://api.weixin.qq.com/cgi-bin/groups/members/update?access_token={ACCESS_TOKEN}";
  //上传多媒体文件
  public static final String URL_MEDIA_UPLOAD = "http://file.api.weixin.qq.com/cgi-bin/media/upload?access_token={ACCESS_TOKEN}&type={TYPE}";
  //上传图文消息素材
  public static final String URL_UPLOAD_NEWS = "https://api.weixin.qq.com/cgi-bin/media/uploadnews?access_token={ACCESS_TOKEN}";
  //下载多媒体文件
  public static final String URL_DOWNLOAD_NEWS = "http://file.api.weixin.qq.com/cgi-bin/media/get?access_token={ACCESS_TOKEN}&media_id={MEDIA_ID}";
  //获取oauth_access_token地址
  public static final String URL_OAUTH_ACCESS_TOKEN = "https://api.weixin.qq.com/sns/oauth2/access_token?appid={APP_ID}&secret={SECRET}&code={CODE}&grant_type=authorization_code";


  /**
   * ************************* command ***********************************************
   */
  //管理员操作
  public static final String CMD_OPT_LIST_ADMIN = "cmd_list_admin";

  public static final String CMD_OPT_ADMIN_UPDATE_MENU = "update menu";
  public static final String CMD_OPT_ADMIN_ANALYSE = "analyse and stat";
  public static final Map<String, Map<String, String>> commandMap = new HashMap<String, Map<String, String>>();

  static {
    //admin的命令列表
    Map<String, String> cmdList_admin = new LinkedHashMap<String, String>();
    cmdList_admin.put("1", CMD_OPT_ADMIN_ANALYSE);
    cmdList_admin.put("2", CMD_OPT_ADMIN_UPDATE_MENU);
    commandMap.put(CMD_OPT_LIST_ADMIN, cmdList_admin);
  }

  //  public static final String CMD_KEY_WORD_BILL="账单";
//  public static final String CMD_KEY_VEHICLE_BIND="绑定";
  public static final String CMD_KEY_ADMIN = "@admin";

  public static final String CMD_KEY_ADMIN_PASS = "I am admin,open the door!";  //todo decode

}