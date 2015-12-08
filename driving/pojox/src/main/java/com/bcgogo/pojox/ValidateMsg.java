package com.bcgogo.pojox;

/**
 * User: ZhangJuntao
 * Date: 13-8-31
 * Time: 下午3:48
 */
public enum ValidateMsg {
  //password
  PASSWORD_EMPTY("请输入密码"),
  PASSWORD_TOO_LONG("密码最多20个字"),
  APP_USER_LOGIN_ERROR("密码错误，请重新输入"),
  PASSWORD_RESET_SUCCESS("用户密码更新成功"),
  PASSWORD_RESET_NUMBER_TOO_MUCH("抱歉，1天最多只能申请3次"),
  PASSWORD_IS_RESTING("您已经申请密码找回，请稍候再试"),
  //image version
  APP_USER_LOGIN_IMAGE_VERSION("手机硬件分辨率必填"),
  APP_PLATFORM_EMPTY("请获取用户手机系统平台类型"),
  //app version
  APP_VERSION_EMPTY("APP版本号必填"),
  //app user no
  APP_USER_NOT_EXIST("用户名不存在，请重新输入"),
  APP_USER_NO_EMPTY("请输入用户名"),
  APP_USER_NOT_FORBID("用户被禁用，请联系店铺管理员"),

  APP_USER_NO_TOO_SHORT("用户名最少5个字"),
  APP_USER_NO_TOO_LONG("用户名最多50个字"),
  APP_USER_NO_HAS_BEEN_USED("该用户名已被占用，请重新输入"),
  APP_USER_NO_ILLEGAL("用户名不合法，请重新输入"),
  APP_USER_MOBILE_HAS_BEEN_USED("该手机号已被占用，请重新输入"),
  //mobile
  MOBILE_EMPTY("请输入手机号"),
  MOBILE_ILLEGAL("手机号格式错误，请重新输入"),
  MOBILE_NO_EXISTED("该手机号对应的用户不存在"),

  //shop
  SHOP_NOT_EXIST("店铺不存在"),
  SHOP_ID_IS_NULL("店铺Id为空"),
  //obd
  OBD_SN_EMPTY("obd硬件唯一标识号不能为空"),
  OBD_VEHICLE_VIN_EMPTY("车辆Vin码不能为空"),
  //vehicle
  VEHICLE_EMPTY("请输入车辆信息"),
  VEHICLE_VIN_EMPTY("请输入车辆唯一编号"),
  VEHICLE_NO_EMPTY("请输入车牌号"),
  VEHICLE_NO_ILLEGAL("车牌号格式错误，请重新输入"),
  APP_VEHICLE_NOT_EXIST("找不到车辆信息"),
  APP_VEHICLE_ID_EMPTY("请输入车辆数据库id"),
  APP_VEHICLE_MODEL_NOT_EMPTY("请输入车型"),
  APP_VEHICLE_BRAND_NOT_EMPTY("请输入车辆品牌"),
  APP_VEHICLE_NO_EXIST("该车牌号已经被使用"),
  APP_VEHICLE_VIN_EXIST("该车辆唯一编号已经被使用"),
  //当前里程
  CURRENT_MILEAGE_LIMIT("当前里程最多20个字"),
  NEXT_CURRENT_MILEAGE_LIMIT("下次保养里程最多20个字"),

  VERSION_UPGRADE("检测到新版本，立即下载，是/否？"),
  OBD_VERSION_UPGRADE("检测到新版本"),
  MIRROR_VERSION_UPGRADE("未检测到新版本"),

  EMPTY_SHOP("抱歉！无此店铺"),
  APP_ENQUIRY_EMPTY("请上传询价图片或填写询价内容"),


  APP_ENQUIRY_SAVE_TARGET_SHOP_STATUS_ILLEGAL("询价单保存，选择发送店铺非法操作，请重新填写"),
  APP_ENQUIRY_TARGET_SHOP_NO_NAME("询价单发送店铺名未填写，请重新填写"),
  APP_ENQUIRY_UPDATE_NOT_EXIST("需要修改的询价单不存在，请重新填写"),
  APP_ENQUIRY_UPDATE_DISABLED("当前询价单已经删除，无法修改，请重新填写"),
  APP_ENQUIRY_UPDATE_SENT("当前询价单已经发送，无法修改，请重新填写"),
  APP_ENQUIRY_SEND_NOT_EXIST("需要发送的询价单不存在，请重新填写"),
  APP_ENQUIRY_SEND_DISABLED("当前询价单已经删除，无法发送，请重新填写"),

  APP_ENQUIRY_SEND_NO_TARGET_SHOP("您还未选择发送店铺，请选择"),

  APP_ENQUIRY_DETAIL_NOT_EXIST("询价单不存在"),
  APP_USER_LOGIN_INFO_EMPTY("登录信息为空"),

  JUHE_VIOLATE_CONDITION_REGULATIONS_SEARCH_CITY_CODE_EMPTY("聚合违章查询CITY_CODE为空"),
  JUHE_VIOLATE_CONDITION_REGULATIONS_SEARCH_VEHICLE_NO_EMPTY("聚合违章查询车牌号为空"),
  ENGINE_EMPTY("请先完善发动机号"),
  CLASS_EMPTY("请先完善车架号"),
  REGIST_EMPTY("请先完善登记证书"),
  APP_SENT_VEHICLE_FAULT_TO_OFTEN("发送车况数据太频繁，请稍候再试！"),
  DO_NOT_SUPPORT_THE_CITY("暂不支持该城市"),
//  VEHICLE_VIOLATE_REGULATION_CITY_EMPTY("未输入城市"),
//  VEHICLE_VIOLATE_REGULATION_VEHICLE_NO_EMPTY("请输入车牌号"),

  APP_VEHICLE_FAULT_CODE_EMPTY("需要标记的故障信息不存在，请刷新后重新操作！"),
  APP_VEHICLE_FAULT_CODE_OPERATION_ILLEGAL("故障信息标记状态不正确，请刷新后重新操作！"),
  APP_VEHICLE_FAULT_CODE_SEARCH_ILLEGAL("故障信息查询状态不正确！"),

  DRIVE_LOG_EMPTY("行车日志为空，无法保存！"),
  DRIVE_LOG_APP_ID_EMPTY("行车日志文件标识错误，无法保存！"),
  APP_USER_NOT_FOUND("用户不存在"),
  APP_USER_CONFIG_NOT_FOUND("需要修改的配置不存在"),
  APP_USER_CONFIG_NAME_ILLEGALITY("需要修改的配置名称不正确"),
  APP_USER_CONFIG_VALUE_ILLEGALITY("需要修改的配置值不正确"),

  //imie
  IMEI_EMPTY("请输入IMEI号码"),
  IMEI_TOO_LONG("IMEI最多20个字"),

  //注册类型
  APP_USER_TYPE_EMPTY("注册类型为空"),

  //IMEI
  IMEI_SN_HAS_BEEN_USED("该IMEI号码已被占用，请重新输入"),
  IMEI_SN_NO_EXIST("该IMEI号码所对应车辆不存在，请重新输入"),
  VEHICLE_IMEI_SN_MORE_THAN_ONE("该IMEI号码对应车辆多于一个，请重新输入"),
  IMEI_OBD_NOT_EXIST("该IMEI号码对应的OBD还未登记，请重新输入"),
//  IMEI_OBD_SOLD("该IMEI号码对应的OBD已经销售，请重新输入"),

  //
  ;

  private String value;

  private ValidateMsg(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
