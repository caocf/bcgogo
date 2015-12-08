package com.bcgogo.pojo.constants;


import com.bcgogo.pojo.response.ApiResponse;

import java.text.MessageFormat;

/**
 * User: ZhangJuntao
 * Date: 13-8-8
 * Time: 下午1:56
 */
public enum MessageCode {

  FAILED(-3, "操作失败！", Status.FAIL),
  SUCCESS(0, "操作成功！", Status.SUCCESS),

  //detail code message  status
  INIT_SUCCESS(1, "初始化成功！", Status.SUCCESS),
  INIT_EXCEPTION(-1, "初始化异常！", Status.FAIL),
  PERMISSION_DENY(-2, "无权限！", Status.FAIL),

  //register
  REGISTER_SUCCESS(100, "注册成功！", Status.SUCCESS),
  REGISTER_FAIL(-100, "{0}，注册失败！", Status.FAIL),
  REGISTER_EXCEPTION(-101, "注册异常！", Status.FAIL),

  //login
  LOGIN_SUCCESS(200, "登录成功！", Status.SUCCESS),
  LOGIN_FAIL(-200, "{0}，登录失败！", Status.FAIL),
  LOGIN_EXCEPTION(-201, "登录异常！", Status.FAIL),
  LOGIN_TIME_OUT(-202, "登录过期", Status.FAIL),
  LOGIN_PERMISSION_DENY(-203, "需要登录", Status.FAIL),
  LOGIN_IMEI_EMPTY(-204, "登录imei号不能为空", Status.FAIL),
  LOGIN_USER_NO_EMPTY(-205, "用户账户不能为空", Status.FAIL),
  LOGIN_USER_NOT_EXIST(-206, "用户账户不存在", Status.FAIL),

  //retrieve password
  RETRIEVE_PASSWORD_SUCCESS(300, "找回密码成功！", Status.SUCCESS),
  RETRIEVE_PASSWORD_FAIL(-300, "{0}，找回密码失败！", Status.FAIL),
  RETRIEVE_PASSWORD_EXCEPTION(-301, "找回密码异常！", Status.FAIL),

  //upgrade testing
  UPGRADE_TESTING_SUCCESS(400, "升级检测成功！", Status.SUCCESS),
  UPGRADE_TESTING_FAIL(-400, "{0}，升级检测失败！", Status.FAIL),
  UPGRADE_TESTING_EXCEPTION(-401, "升级检测异常！", Status.FAIL),

  //user passowrd update
  USER_PASSWORD_UPDATE_SUCCESS(500, "修改成功！", Status.SUCCESS),
  USER_PASSWORD_UPDATE_FAIL(-500, "{0}，修改失败！", Status.FAIL),
  USER_PASSWORD_UPDATE_EXCEPTION(-501, "修改异常！", Status.FAIL),

  //save app vehicle
  SAVE_APP_VEHICLE_SUCCESS(1000, "保存车辆信息成功！", Status.SUCCESS),
  SAVE_APP_VEHICLE_FAIL(-1000, "{0}，保存车辆信息失败！", Status.FAIL),
  SAVE_APP_VEHICLE_EXCEPTION(-1001, "保存车辆信息异常！", Status.FAIL),

  //obtain app vehicle
  OBTAIN_APP_VEHICLE_SUCCESS(1100, "获取车辆列表信息成功！", Status.SUCCESS),
  OBTAIN_APP_VEHICLE_FAIL(-1100, "{0}，获取车辆列表信息失败！", Status.FAIL),
  OBTAIN_APP_VEHICLE_EXCEPTION(-1101, "获取车辆列表信息异常！", Status.FAIL),


  //set app vehicle default
  APP_VEHICLE_SET_DEFAULT_SUCCESS(1110, "车辆设置成功！", Status.SUCCESS),
  APP_VEHICLE_SET_DEFAULT_FAIL(-1110, "{0}，车辆设置失败！", Status.FAIL),
  APP_VEHICLE_SET_DEFAULT_EXCEPTION(-1111, "车辆设置异常！", Status.FAIL),

  //single app vehicle
  SINGLE_APP_VEHICLE_SUCCESS(1200, "获取车辆信息成功！", Status.SUCCESS),
  SINGLE_APP_VEHICLE_FAIL(-1200, "{0}，获取车辆信息失败！", Status.FAIL),
  SINGLE_APP_VEHICLE_EXCEPTION(-1201, "获取车辆信息异常！", Status.FAIL),

  //delete single app vehicle
  DELETE_SINGLE_APP_VEHICLE_SUCCESS(1300, "删除车辆成功！", Status.SUCCESS),
  DELETE_SINGLE_APP_VEHICLE_FAIL(-1300, "{0}，删除车辆失败！", Status.FAIL),
  DELETE_SINGLE_APP_VEHICLE_EXCEPTION(-1301, "删除车辆异常！", Status.FAIL),

  //brand model/ keyword vehicle
  APP_VEHICLE_BRAND_MODEL_KEYWORD_SUCCESS(1400, "获取车辆品牌成功！", Status.SUCCESS),
  APP_VEHICLE_BRAND_MODEL_KEYWORD_FAIL(-1400, "{0}，获取车辆信息失败！", Status.FAIL),
  APP_VEHICLE_BRAND_MODEL_KEYWORD_EXCEPTION(-1401, "获取车辆信息异常！", Status.FAIL),

  //app vehicle fault
  APP_VEHICLE_FAULT_SUCCESS(1500, "发送车辆故障信息成功！", Status.SUCCESS),
  APP_VEHICLE_FAULT_FAIL(-1500, "{0}，发送车辆故障信息失败！", Status.FAIL),
  APP_VEHICLE_FAULT_EXCEPTION(-1501, "发送车辆故障信息异常！", Status.FAIL),

  //fault dictionary
  UPDATE_FAULT_DICTIONARY_SUCCESS(1600, "故障字典信息更新成功！", Status.SUCCESS),
  UPDATE_FAULT_DICTIONARY_FAIL(-1600, "{0}，故障字典信息更新失败！", Status.FAIL),
  UPDATE_FAULT_DICTIONARY_EXCEPTION(-1701, "故障字典信息更新异常！", Status.FAIL),

  //obtain shop area
  OBTAIN_AREA_SUCCESS(1800, "获取地区列信息成功！", Status.SUCCESS),
  OBTAIN_AREA_FAIL(-1800, "{0}，获取地区列信息失败！", Status.FAIL),
  OBTAIN_AREA_EXCEPTION(-1801, "获取地区列信息异常！", Status.FAIL),

  //obtain shop suggestion
  OBTAIN_SHOP_SUGGESTION_SUCCESS(1900, "获取店铺建议信息成功！", Status.SUCCESS),
  OBTAIN_SHOP_SUGGESTION_FAIL(-1900, "{0}，获取店铺建议信息失败！", Status.FAIL),
  OBTAIN_SHOP_SUGGESTION_EXCEPTION(-1901, "获取店铺建议信息异常！", Status.FAIL),

  //obtain shop list
  OBTAIN_SHOP_LIST_SUCCESS(2000, "查询推荐店铺成功！", Status.SUCCESS),
  OBTAIN_SHOP_LIST_FAIL(-2000, "{0}，查询推荐店铺失败！", Status.FAIL),
  OBTAIN_SHOP_LIST_EXCEPTION(-2001, "查询推荐店铺异常！", Status.FAIL),

  //obtain shop detail
  OBTAIN_SHOP_DETAIL_SUCCESS(2100, "查询店铺详情成功！", Status.SUCCESS),
  OBTAIN_SHOP_DETAIL_FAIL(-2100, "{0}，查询店铺详情失败！", Status.FAIL),
  OBTAIN_SHOP_DETAIL_EXCEPTION(-2101, "查询店铺详情异常！", Status.FAIL),

  //obtain shop detail
  OBTAIN_MESSAGE_SUCCESS(2200, "获取消息成功！", Status.SUCCESS),
  OBTAIN_MESSAGE_FAIL(-2200, "{0}，获取消息失败！", Status.FAIL),
  OBTAIN_MESSAGE_EXCEPTION(-2201, "获取消息异常！", Status.FAIL),

  //obd binding
  OBD_BINDING_SUCCESS(2300, "OBD绑定成功！", Status.SUCCESS),
  OBD_BINDING_FAIL(-2300, "{0}，OBD绑定失败！", Status.FAIL),
  OBD_BINDING_EXCEPTION(-2301, "OBD绑定异常！", Status.FAIL),


  //feedback
  FEEDBACK_SUCCESS(3100, "提交成功！", Status.SUCCESS),
  FEEDBACK_FAIL(-3100, "{0}，提交失败！", Status.FAIL),
  FEEDBACK_EXCEPTION(-3101, "提交异常！", Status.FAIL),

  //logout
  LOGOUT_SUCCESS(3200, "用户注销成功！", Status.SUCCESS),
  LOGOUT_FAIL(-3200, "{0}，用户注销失败！", Status.FAIL),
  LOGOUT_EXCEPTION(-3201, "用户注销异常！", Status.FAIL),

  //vehicle maintain update
  VEHICLE_MAINTAIN_UPDATE_SUCCESS(3300, "用户修改保养信息成功！", Status.SUCCESS),
  VEHICLE_MAINTAIN_UPDATE_FAIL(-3300, "{0}，用户修改保养信息失败！", Status.FAIL),
  VEHICLE_MAINTAIN_UPDATE_EXCEPTION(-3301, "用户修改保养信息异常！", Status.FAIL),

  //user information update
  USER_INFO_UPDATE_SUCCESS(3400, "用户修改个人资料成功！", Status.SUCCESS),
  USER_INFO_UPDATE_FAIL(-3400, "{0}，用户修改个人资料失败！", Status.FAIL),
  USER_INFO_UPDATE_EXCEPTION(-3401, "用户修改个人资料异常！", Status.FAIL),

  //get user information
  USER_INFO_GET_SUCCESS(3500, "用户资料获取成功！", Status.SUCCESS),
  USER_INFO_GET_FAIL(-3500, "{0}，用户资料获取失败！", Status.FAIL),
  USER_INFO_GET_EXCEPTION(-3501, "用户资料获取异常！", Status.FAIL),

  //user comment shop order user information
  USER_COMMENT_ORDER_SUCCESS(3600, "用户评价店铺成功！", Status.SUCCESS),
  USER_COMMENT_ORDER_FAIL(-3600, "{0}，用户评价店铺失败！", Status.FAIL),
  USER_COMMENT_ORDER_EXCEPTION(-3601, "用户评价店铺异常！", Status.FAIL),

  //user cancel service
  USER_CANCEL_SERVICE_SUCCESS(3700, "用户取消服务成功！", Status.SUCCESS),
  USER_CANCEL_SERVICE_FAIL(-3700, "{0}，用户取消服务失败！", Status.FAIL),
  USER_CANCEL_SERVICE_EXCEPTION(-3701, "用户取消服务异常！", Status.FAIL),


  //user get order info
  ORDER_INFO_GET_SUCCESS(3800, "单据信息获取成功！", Status.SUCCESS),
  ORDER_INFO_GET_FAIL(-3800, "{0}，单据信息获取失败！", Status.FAIL),
  ORDER_INFO_GET_EXCEPTION(-3801, "单据信息获取异常！", Status.FAIL),

  //user get order history
  ORDER_HISTORY_GET_SUCCESS(3900, "单据历史信息获取成功！", Status.SUCCESS),
  ORDER_HISTORY_GET_FAIL(-3900, "{0}，单据历史信息获取失败！", Status.FAIL),
  ORDER_HISTORY_GET_EXCEPTION(-3901, "单据历史信息获取异常！", Status.FAIL),

  //user appoint service
  USER_APPOINT_SERVICE_SUCCESS(4000, "用户预约服务成功！", Status.SUCCESS),
  USER_APPOINT_SERVICE_FAIL(-4000, "{0}，用户预约服务失败！", Status.FAIL),
  USER_APPOINT_SERVICE_EXCEPTION(-4001, "用户预约服务异常！", Status.FAIL),

  //user save vehicle condition
  VEHICLE_CONDITION_SAVE_SUCCESS(4100, "发送车况信息成功！", Status.SUCCESS),
  VEHICLE_CONDITION_SAVE_FAIL(-4100, "{0}，发送车况信息失败！", Status.FAIL),
  VEHICLE_CONDITION_SAVE_EXCEPTION(-4101, "发送车况信息异常！", Status.FAIL),

  //obtain service category
  OBTAIN_SERVICE_CATEGORY_SUCCESS(4200, "获取服务范围信息成功！", Status.SUCCESS),
  OBTAIN_SERVICE_CATEGORY_FAIL(-4200, "{0}，获取服务范围信息失败！", Status.FAIL),
  OBTAIN_SERVICE_CATEGORY_EXCEPTION(-4201, "获取服务范围列信息异常！", Status.FAIL),

  //account list
  ACCOUNT_LIST_SUCCESS(4300, "查询账单列表成功！", Status.SUCCESS),
  ACCOUNT_LIST_FAIL(-4300, "{0}，查询账单列表失败！", Status.FAIL),
  ACCOUNT_LIST_EXCEPTION(-4301, "查询账单列表异常！", Status.FAIL),

  //account detail
  ACCOUNT_DETAIL_SUCCESS(4400, "查询账单详情成功！", Status.SUCCESS),
  ACCOUNT_DETAIL_FAIL(-4400, "{0}，查询账单详情失败！", Status.FAIL),
  ACCOUNT_DETAIL_EXCEPTION(-4401, "查询账单详情异常！", Status.FAIL),

  //transform detail
  ACCOUNT_TRANSFORM_INQUIRY_SUCCESS(4450, "账单转换询价单成功！", Status.SUCCESS),
  ACCOUNT_TRANSFORM_INQUIRY_FAIL(-4450, "{0}，账单转换询价单失败！", Status.FAIL),
  ACCOUNT_TRANSFORM_INQUIRY_EXCEPTION(-4451, "账单转换询价单异常！", Status.FAIL),

  //account delete
  ACCOUNT_DELETE_SUCCESS(4500, "账单删除成功！", Status.SUCCESS),
  ACCOUNT_DELETE_FAIL(-4500, "{0}，账单删除失败！", Status.FAIL),
  ACCOUNT_DELETE_EXCEPTION(-4501, "账单删除异常！", Status.FAIL),

  //account save
  ACCOUNT_SAVE_SUCCESS(4600, "账单保存成功！", Status.SUCCESS),
  ACCOUNT_SAVE_FAIL(-4600, "{0}，账单保存失败！", Status.FAIL),
  ACCOUNT_SAVE_EXCEPTION(-4601, "账单保存异常！", Status.FAIL),

  // enquiry save
  ENQUIRY_SAVE_SUCCESS(4700, "询价单保存成功！", Status.SUCCESS),
  ENQUIRY_SAVE_FAIL(-4700, "{0}，询价单保存失败！", Status.FAIL),
  ENQUIRY_SAVE_EXCEPTION(-4701, "询价单保存异常！", Status.FAIL),
  //enquiry update
  ENQUIRY_UPDATE_SUCCESS(4800, "询价单更新成功！", Status.SUCCESS),
  ENQUIRY_UPDATE_FAIL(-4800, "{0}，询价单更新失败！", Status.FAIL),
  ENQUIRY_UPDATE_EXCEPTION(-4801, "询价单更新异常！", Status.FAIL),
  //enquiry send
  ENQUIRY_SEND_SUCCESS(4900, "询价单发送成功！", Status.SUCCESS),
  ENQUIRY_SEND_FAIL(-4900, "{0}，询价单发送失败！", Status.FAIL),
  ENQUIRY_SEND_EXCEPTION(-4901, "询价单发送异常！", Status.FAIL),
  //enquiry delete
  ENQUIRY_DELETE_SUCCESS(5000, "询价单删除成功！", Status.SUCCESS),
  ENQUIRY_DELETE_FAIL(-5000, "{0}，询价单删除失败！", Status.FAIL),
  ENQUIRY_DELETE_EXCEPTION(-5001, "询价单删除异常！", Status.FAIL),
  //enquiry detail
  ENQUIRY_DETAIL_SUCCESS(5100, "查询询价单详情成功！", Status.SUCCESS),
  ENQUIRY_DETAIL_FAIL(-5100, "{0}，查询询价单详情失败！", Status.FAIL),
  ENQUIRY_DETAIL_EXCEPTION(-5101, "查询询价单详情异常！", Status.FAIL),
  //enquiry list
  ENQUIRY_LIST_SUCCESS(5200, "查询询价单列表成功！", Status.SUCCESS),
  ENQUIRY_LIST_FAIL(-5200, "{0}，查询询价单列表失败！", Status.FAIL),
  ENQUIRY_LIST_EXCEPTION(-5201, "查询询价单列表异常！", Status.FAIL),
  //enquiry forward
  ENQUIRY_FORWARD_SUCCESS(5300, "获取询价单转发信息成功！", Status.SUCCESS),
  ENQUIRY_FORWARD_FAIL(-5300, "{0}，获取询价单转发信息失败！", Status.FAIL),
  ENQUIRY_FORWARD_EXCEPTION(-5301, "获取询价单转发信息异常！", Status.FAIL),
  //upYun param
  GET_UPYUN_PARAM_SUCCESS(5400, "获取upYun信息成功！", Status.SUCCESS),
  GET_UPYUN_PARAM_FAIL(-5400, "{0}，获取upYun信息失败！", Status.FAIL),
  GET_UPYUN_PARAM_EXCEPTION(-5401, "获取upYun信息异常！", Status.FAIL),

  //juhe 条件查询接口
  JUHE_VIOLATE_CONDITION_REGULATIONS_SEARCH_SUCCESS(5500, "聚合查询成功！", Status.SUCCESS),
  JUHE_VIOLATE_CONDITION_REGULATIONS_SEARCH_FAIL(-5500, "{0}，聚合查询失败！", Status.FAIL),
  JUHE_VIOLATE_CONDITION_REGULATIONS_SEARCH_EXCEPTION(-5501, "聚合查询异常！", Status.FAIL),

  //车辆故障信息操作接口
  APP_VEHICLE_FAULT_CODE_OPERATE_SUCCESS(5600, "车辆故障信息操作成功！", Status.SUCCESS),
  APP_VEHICLE_FAULT_CODE_OPERATE_FAIL(-5600, "{0}，车辆故障信息操作失败！", Status.FAIL),
  APP_VEHICLE_FAULT_CODE_OPERATE_EXCEPTION(-5601, "车辆故障信息操作异常！", Status.FAIL),

  //车辆故障信息操作接口
  APP_VEHICLE_FAULT_CODE_LIST_SUCCESS(5700, "车辆故障信息展示成功！", Status.SUCCESS),
  APP_VEHICLE_FAULT_CODE_LIST_FAIL(-5700, "{0}，车辆故障信息展示失败！", Status.FAIL),
  APP_VEHICLE_FAULT_CODE_LIST_EXCEPTION(-5701, "车辆故障信息展示异常！", Status.FAIL),


  USER_INFO_SUCCESS(5800, "获取用户信息成功！", Status.SUCCESS),
  USER_INFO_FAIL(-5800, "{0}，获取用户信息失败！", Status.FAIL),
  USER_INFO_EXCEPTION(-5801, "获取用户信息异常！", Status.FAIL),

  VEHICLE_INFO_SUGGESTION_SUCCESS(5900, "获取车辆信息成功！", Status.SUCCESS),
  VEHICLE_INFO_SUGGESTION_FAIL(-5900, "{0}，获取车辆信息失败！", Status.FAIL),
  VEHICLE_INFO_SUGGESTION_EXCEPTION(-5901, "获取车辆信息异常！", Status.FAIL),

  SHOP_BINDING_SUCCESS(6300, "店铺绑定成功！", Status.SUCCESS),
  SHOP_BINDING_FAIL(-6300, "{0}，店铺绑定失败！", Status.FAIL),
  SHOP_BINDING_EXCEPTION(-6301, "店铺绑定异常！", Status.FAIL),

  OBD_MESSAGE_SUCCESS(6400, "OBD信息接收成功！", Status.SUCCESS),
  OBD_MESSAGE_FAIL(-6400, "{0}，OBD信息接收失败！", Status.FAIL),
  OBD_MESSAGE_EXCEPTION(-6401, "OBD信息接收异常！", Status.FAIL),

  DRIVE_LOG_SAVED_SUCCESS(6500, "行车日志接收成功！", Status.SUCCESS),
  DRIVE_LOG_SAVED_FAIL(-6500, "{0}，行车日志接收失败！", Status.FAIL),
  DRIVE_LOG_SAVED_EXCEPTION(-6501, "行车日志接收异常！", Status.FAIL),

  DRIVE_LOG_CONTENTS_SUCCESS(6600, "获取行车日志列表成功！", Status.SUCCESS),
  DRIVE_LOG_CONTENTS_FAIL(-6600, "{0}，获取行车日志列表失败！", Status.FAIL),
  DRIVE_LOG_CONTENTS_EXCEPTION(-6601, "获取行车日志列表异常！", Status.FAIL),

  DRIVE_LOG_DETAIL_SUCCESS(6700, "获取行车日志成功！", Status.SUCCESS),
  DRIVE_LOG_DETAIL_FAIL(-6700, "{0}，获取行车日志失败！", Status.FAIL),
  DRIVE_LOG_DETAIL_EXCEPTION(-6701, "获取行车日志异常！", Status.FAIL),
  DRIVE_LOG_DELETE_EXCEPTION(-6702, "获取行车日志异常！", Status.FAIL),
  DRIVE_LOG_NOT_EXIST(-6703, "行车日志不存在或已经删除！", Status.FAIL),

  UPDATE_APP_USER_CONFIG_SUCCESS(6800, "用户配置更新成功！", Status.SUCCESS),
  UPDATE_APP_USER_CONFIG_FAIL(-6800, "{0}，用户配置更新失败！", Status.FAIL),
  UPDATE_APP_USER_CONFIG_EXCEPTION(-6801, "用户配置更新异常！", Status.FAIL),

  SHOP_COMMENT_RECORD_LIST_SUCCESS(6900, "获取店铺评价成功！", Status.SUCCESS),
  SHOP_COMMENT_RECORD_LIST_FAIL(-6900, "{0}，获取店铺评价失败！", Status.FAIL),
  SHOP_COMMENT_RECORD_LIST_EXCEPTION(-6901, "获取店铺评价异常！", Status.FAIL),


  //IMEI VALIDATE
  IMEI_VALIDATE_SUCCESS(7000, "IMEI校验成功！", Status.SUCCESS),
  IMEI_VALIDATE_FAIL(-7000, "{0}，IMEI校验失败！", Status.FAIL),
  IMEI_VALIDATE_EXCEPTION(-7001, "IMEI校验异常！", Status.FAIL),

  //save oil price
  SAVE_OIL_PRICE_SUCCESS(7100, "保存油价成功！", Status.SUCCESS),
  SAVE_OIL_PRICE_FAIL(-7100, "{0}，保存油价失败！", Status.FAIL),
  SAVE_OIL_PRICE_EXCEPTION(-7101, "保存油价异常！", Status.FAIL),

  VEHICLE_VIOLATE_REGULATION_QUERY_SUCCESS(7200, "违章查询成功。", Status.SUCCESS),
  VEHICLE_VIOLATE_REGULATION_QUERY_FAIL(-7200, "{0}，违章查询失败。", Status.FAIL),
  VEHICLE_VIOLATE_REGULATION_QUERY_EXCEPTION(-7201, "违章查询异常。", Status.FAIL),
  VEHICLE_VIOLATE_CITY_IS_EMPTY(-7211, "违章城市为空，请确认已经编辑成功。", Status.FAIL),
  VEHICLE_VIOLATE_VIN_IS_EMPTY(-7212, "车架号为空，请确认已经编辑成功。", Status.FAIL),
  VEHICLE_VIOLATE_ENGINE_NO_IS_EMPTY(-7213, "发动机号为空，请确认已经编辑成功。", Status.FAIL),

  //user get shop advert
  SHOP_ADVERT_GET_SUCCESS(7200, "店铺公告获取成功！", Status.SUCCESS),
  SHOP_ADVERT_GET_FAIL(-7200, "{0}，店铺公告获取失败！", Status.FAIL),
  SHOP_ADVERT_GET_EXCEPTION(-7201, "店铺公告获取异常！", Status.FAIL),

  //drive stat
  DRIVE_STAT_LIST_SUCCESS(7300, "获取行程统计成功！", Status.SUCCESS),
  DRIVE_STAT_LIST_FAIL(-7300, "{0}，获取行程统计失败！", Status.FAIL),
  DRIVE_STAT_LIST_EXCEPTION(-7301, "获取行程统计异常！", Status.FAIL),

  //bcgogo app获取短信内容
  BCGOGO_MSG_CONTENT_SUCCESS(7400, "获取短信内容成功！", Status.SUCCESS),
  BCGOGO_MSG_CONTENT_FAIL(-7400, "{0}，获取短信内容失败！", Status.FAIL),
  BCGOGO_MSG_CONTENT_EXCEPTION(-7401, "获取短信内容异常！", Status.FAIL),

  //bcgogo app发送短信
  BCGOGO_SEND_MSG_SUCCESS(7500, "发送短信成功！", Status.SUCCESS),
  BCGOGO_SEND_MSG_FAIL(-7500, "{0}，发送短信失败！", Status.FAIL),
  BCGOGO_SEND_MSG_EXCEPTION(-7501, "发送短信异常！", Status.FAIL),

  //bcgogo app 标记已处理
  BCGOGO_REMIND_HANDLE_SUCCESS(7600, "处理成功！", Status.SUCCESS),
  BCGOGO_REMIND_HANDLE_FAIL(-7600, "{0}，处理失败！", Status.FAIL),
  BCGOGO_REMIND_HANDLE_EXCEPTION(-7601, "处理异常！", Status.FAIL),

  //bcgogo app 接受预约单
  BCGOGO_ACCEPT_APPOINT_SUCCESS(7700, "接受预约单成功！", Status.SUCCESS),
  BCGOGO_ACCEPT_APPOINT_FAIL(-7700, "{0}，接受预约单失败！", Status.FAIL),
  BCGOGO_ACCEPT_APPOINT_EXCEPTION(-7701, "接受预约单异常！", Status.FAIL),

  //bcgogo app 更改服务时间并接受预约单
  BCGOGO_CHANGE_APPOINT_TIME_SUCCESS(7800, "更改预约单服务时间成功！", Status.SUCCESS),
  BCGOGO_CHANGE_APPOINT_TIME_FAIL(-7800, "{0}，更改预约单服务时间失败！", Status.FAIL),
  BCGOGO_CHANGE_APPOINT_TIME_EXCEPTION(-7801, "更改预约单服务时间异常！", Status.FAIL),

  //bcgogo app 获取故障列表
  BCGOGO_VEHICLE_FAULT_LIST_SUCCESS(7900, "获取故障列表成功！", Status.SUCCESS),
  BCGOGO_VEHICLE_FAULT_LIST_FAIL(-7900, "{0}，获取故障列表失败！", Status.FAIL),
  BCGOGO_VEHICLE_FAULT_LIST_EXCEPTION(-7901, "获取故障列表异常！", Status.FAIL),

  //bcgogo app 获取保养列表
  BCGOGO_CUSTOMER_REMIND_LIST_SUCCESS(8000, "获取保养列表成功！", Status.SUCCESS),
  BCGOGO_CUSTOMER_REMIND_LIST_FAIL(-8000, "{0}，获取保养列表失败！", Status.FAIL),
  BCGOGO_CUSTOMER_REMIND_LIST_EXCEPTION(-8001, "获取保养列表异常！", Status.FAIL),

  //bcgogo app 获取预约列表
  BCGOGO_APPOINT_ORDER_LIST_SUCCESS(8100, "获取预约列表成功！", Status.SUCCESS),
  BCGOGO_APPOINT_ORDER_LIST_FAIL(-8100, "{0}，获取预约列表失败！", Status.FAIL),
  BCGOGO_APPOINT_ORDER_LIST_EXCEPTION(-8101, "获取预约列表异常！", Status.FAIL),

  //碰撞视频上传
  VIDEO_UPLOAD_FAIL(8200, "视频上传失败！", Status.FAIL),
  VIDEO_UPLOAD_PROGRESS_QUERY_FAIL(8201, "视频上传失败！", Status.FAIL),
  VIDEO_SIZE_EMPTY(8202, "上传视频文件为空！", Status.FAIL),
  IMPACT_UUID_EMPTY(8203, "碰撞识别码不能为空！", Status.FAIL),
  ILLEGAL_MULTIPART_CONTENT(8204, "非标准的文件上传请求！", Status.FAIL),
  IMPACT_INIT_DATA_NOT_EXIST(8205, "初始化视频记录不存在！", Status.FAIL),
  IMPACT_DATA_SAVE_SUCCESS(8206, "碰撞数据保存成功！", Status.SUCCESS),
  VIDEO_UPLOAD_PARAM_EMPTY(8207, "视频上传参数不能为空！", Status.FAIL),
  IMPACT_DATA_NOT_EXIST(8208, "碰撞数据不存在！", Status.FAIL),
  IMPACT_UUID_EXISTED(8209, "碰撞uuid识别码已存在！", Status.FAIL),
  FILE_CRC_IS_EMPTY(8210, "文件CRC校验码不应为空！", Status.FAIL),
  FILE_CRC_CHECK_SUM_EXCEPTION(8210, "文件CRC校验异常！", Status.FAIL),

  //一键救援信息获取
  RESCUE_DETAIL_GET_FAIL(8300, "一键救援信息获取失败！", Status.FAIL),
  RESCUE_DETAIL_GET_SUCCESS(8301, "一键救援信息获取成功！", Status.SUCCESS),
  RESCUE_APP_USER_NO_EMPTY(8302, "appUserNo不能为空！", Status.FAIL),

  //后视镜上报数据
  VEHICLE_DRIVE_SUCCESS(8400, "上报车辆点火熄火信息成功！", Status.SUCCESS),
  VEHICLE_DRIVE_UUID_NO_EMPTY(8401, "上报车辆点火熄火uuid为空！", Status.FAIL),
  VEHICLE_DATA_SUCCESS(8402, "上报车况信息成功！", Status.SUCCESS),
  VEHICLE_DATA_UUID_NO_EMPTY(8403, "上报车况信息uuid为空！", Status.FAIL),

  //故障码相关
  FAULT_CODE_FALL(8500, "故障码编号faultCode不能为空！", Status.FAIL) ,

  //微信相关MsgCode
  CREATE_TEMP_QR_CODE_FALL(9000, "创建二维码异常，请稍后再试！", Status.FAIL),
  WX_OPENID_EMPTY_FALL(9001, "微信用户OPENID不能为空！", Status.FAIL),
  ;

  private enum Status {
    SUCCESS, FAIL
  }

  private int code;
  private String value;
  private Status status;

  private MessageCode(int code, String value, Status status) {
    this.code = code;
    this.value = value;
    this.status = status;
  }

  public static ApiResponse toApiResponse(MessageCode code) {
    ApiResponse apiResponse = new ApiResponse();
    apiResponse.setStatus(code.getStatus());
    apiResponse.setErrorCode(code.getCode());
    apiResponse.setMessage(code.getValue());
    return apiResponse;
  }

  public static ApiResponse toApiResponse(MessageCode code, String reason) {
    ApiResponse apiResponse = new ApiResponse();
    apiResponse.setStatus(code.getStatus());
    apiResponse.setErrorCode(code.getCode());
    apiResponse.setMessage(MessageFormat.format(code.getValue(), reason));
    return apiResponse;
  }

  public static ApiResponse toApiResponse(MessageCode code, String reason, boolean containCodeValue) {
    ApiResponse apiResponse = new ApiResponse();
    apiResponse.setStatus(code.getStatus());
    apiResponse.setErrorCode(code.getCode());
    if (containCodeValue)
      apiResponse.setMessage(MessageFormat.format(code.getValue(), reason));
    else
      apiResponse.setMessage(reason);
    return apiResponse;
  }

//  public static ApiResponse toApiResponse(MessageCode code, ValidateMsg msg) {
//    ApiResponse apiResponse = new ApiResponse();
//    apiResponse.setStatus(code.getStatus());
//    apiResponse.setErrorCode(code.getCode());
//    apiResponse.setMessage(MessageFormat.format(code.getValue(), msg.getValue()));
//    return apiResponse;
//  }

  public String getStatus() {
    return status.name();
  }

  public int getCode() {
    return code;
  }

  public void setCode(int code) {
    this.code = code;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

}
