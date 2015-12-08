package com.bcgogo.constant.pushMessage;

import com.bcgogo.enums.txn.pushMessage.PushMessageRedirectUrl;

/**
 * User: ZhangJuntao
 * Date: 13-9-9
 * Time: 上午9:31
 */
public class AppointConstant {
  //店铺发起
  //您已成功预约米其林维修店2013-09-01 10：00洗车服务
  //  public final static String SHOP_ACCEPT_APPOINT_MESSAGE_CONTENT = "您已成功预约 $!{context.shopDTO.name} $!{context.appointTimeStr} $!{context.services}！";

  //您已为车辆【苏E12345】成功预约【2014-01-10 15:20】的【维修】服务，谢谢！【苏州富骏汽车服务有限公司】
  public final static String SHOP_ACCEPT_APPOINT_MESSAGE_TITLE = "服务提醒";
  public final static String SHOP_ACCEPT_APPOINT_MESSAGE_CONTENT = "您已为车辆$!{context.vehicleNo}成功预约$!{context.appointTimeStr}的$!{context.services}服务，谢谢！$!{context.shopDTO.shortname}";

  public final static String SHOP_QUOTE_TO_APP_MESSAGE_TITLE = "服务提醒";
  public final static String SHOP_QUOTE_TO_APP_MESSAGE_CONTENT = "$!{context.shopDTO.name} $!{context.enquiryTimeStr}已为您报价！";

  public final static String SYS_ACCEPT_APPOINT_MESSAGE_TITLE = "服务提醒";
  public final static String SYS_ACCEPT_APPOINT_MESSAGE_CONTENT = "车牌号：$!{context.vehicleNo} $!{context.applyTimeStr}已预约本店 $!{context.services} 服务，30分钟未处理，系统已自动为您接受该服务！";

  //您预约米其林维修店2013-09-01 10：00洗车服务已被拒绝，拒绝理由建议选择其他时间，如有疑问请及时联系店家！
  //  public final static String SHOP_REJECT_APPOINT_MESSAGE_CONTENT = "您预约 $!{context.shopDTO.name} $!{context.appointTimeStr} $!{context.services} 已被拒绝，拒绝理由：$!{context.reason}，如有疑问请及时联系店家！";

  //您为车辆【苏E12345】预约【1月10日 15:20】的【维修】服务已被拒绝，理由为【拒绝理由】，如有疑问请及时联系店家！
  public final static String SHOP_REJECT_APPOINT_MESSAGE_TITLE = "服务提醒";
  public final static String SHOP_REJECT_APPOINT_MESSAGE_CONTENT = "您为车辆$!{context.vehicleNo}预约$!{context.appointTimeStr}的$!{context.services}服务已被拒绝，理由为$!{context.reason}，如有疑问请及时联系店家！";

  //【米其林维修店】已取消您预约【2013-09-01 10：00】【洗车服务】
  public final static String SHOP_CANCEL_APPOINT_MESSAGE_TITLE = "服务提醒";
  //您为车辆【苏E12345】预约【1月10日  15:20】的【维修】服务已取消！【苏州富骏汽车服务有限公司】
  public final static String SHOP_CANCEL_APPOINT_MESSAGE_CONTENT = "您为车辆$!{context.vehicleNo}预约$!{context.appointTimeStr}的$!{context.services}服务已取消！$!{context.shopDTO.shortname}";

  //您预约 米其林维修店2013-09-01 10：00洗车服务已修改为米其林维修店2013-09-02 10：00洗车服务，如有疑问请及时联系店家！
  public final static String SHOP_CHANGE_APPOINT_MESSAGE_TITLE = "服务提醒";
 //您预约车辆【苏E12345】【9月1日 10：00】【洗车服务】已修改为车辆【苏E12345】【9月2日 10：00】【洗车服务】，详询051288889999。【米其林汽修】
//  public final static String SHOP_CHANGE_APPOINT_MESSAGE_CONTENT = "您预约 $!{context.shopDTO.name} $!{context.appointTimeStr} $!{context.services}已修改为 $!{context.newAppointTimeStr} $!{context.newServices}，如有疑问请及时联系店家！";
  public final static String SHOP_CHANGE_APPOINT_MESSAGE_CONTENT = "您预约车辆$!{context.vehicleNo}$!{context.appointTimeStr}$!{context.services}已修改为$!{context.newAppointTimeStr}$!{context.newServices}，详询#if(${context.shopDTO.landline})$!{context.shopDTO.landline}#else$!{context.shopDTO.mobile}#end。$!{context.shopDTO.shortname}";

  //您预约 米其林维修店 2013-09-01 10：00 洗车服务 已结束！
  public final static String SHOP_FINISH_APPOINT_MESSAGE_TITLE = "服务提醒";
  public final static String SHOP_FINISH_APPOINT_MESSAGE_CONTENT = "您预约 $!{context.shopDTO.name} $!{context.appointTimeStr} $!{context.services} 已结束！";

  //app发起
  //车牌号：苏E12345 ，已取消预约2013-09-01 10：00 本店洗车服务 ！
  public final static String APP_CANCEL_APPOINT_MESSAGE_TITLE = "服务提醒";
  public final static String APP_CANCEL_APPOINT_MESSAGE_CONTENT = "车牌号：$!{context.vehicleNo}，已取消预约$!{context.applyTimeStr}本店 $!{context.services}！";
  //车牌号：苏E12345，已向本店提交询价单，赶快去报价吧！
  public final static String APP_SUBMIT_ENQUIRY_MESSAGE_TITLE = "服务提醒";
  public final static String APP_SUBMIT_ENQUIRY_MESSAGE_CONTENT = "车牌号：$!{context.vehicleNo}，已向本店提交询价单，赶快去报价吧！";
  //车牌号：苏E12345 ，已向本店提交2013-09-01 10：00 洗车服务 预约请求！
  public final static String  APP_APPLY_APPOINT_MESSAGE_TITLE = "服务提醒";
  public final static String  APP_APPLY_APPOINT_MESSAGE_CONTENT = "车牌号：$!{context.vehicleNo} ，已向本店提交$!{context.applyTimeStr} $!{context.services} 预约请求！";

  //schedule
  //保养里程
  public final static String APP_VEHICLE_MAINTAIN_MILEAGE_MESSAGE_TITLE = "里程提醒";
  public final static String APP_VEHICLE_MAINTAIN_MILEAGE_MESSAGE_CONTENT = "您的爱车 $!{context.vehicleNo} 已接近保养里程，请及时联系店家保养爱车！";
  public final static String APP_VEHICLE_MAINTAIN_MILEAGE_MESSAGE_2_SHOP_TITLE = "里程提醒";
  public final static String APP_VEHICLE_MAINTAIN_MILEAGE_MESSAGE_2_SHOP_CONTENT =
      "#set($tmp1= $!{context.appVehicle.nextMaintainMileage}-$!{context.appVehicle.currentMileage})" +
          "#set($tmp2= $!{context.appVehicle.currentMileage}-$!{context.appVehicle.nextMaintainMileage})" +
          "$!{context.appUser.name}（$!{context.appUser.mobile}）的爱车（$!{context.appVehicle.vehicleNo}）当前行驶总里程$!{context.appVehicle.currentMileage}公里，" +
          "#if($tmp1<=$!{context.appVehicleMaintainMileageLeftLimit}&&$tmp1>=0)距离保养里程不足$tmp1公里#else已超过保养里程$tmp2公里#end" +
          "。";

  //保养时间
  public final static String APP_VEHICLE_MAINTAIN_TIME_MESSAGE_TITLE = "保养提醒";
  public final static String APP_VEHICLE_MAINTAIN_TIME_MESSAGE_CONTENT = "您的爱车 $!{context.vehicleNo} 已接近保养日期，请及时联系店家保养爱车！";
  public final static String APP_VEHICLE_MAINTAIN_TIME_MESSAGE_2_SHOP_TITLE = "保养提醒";
  public final static String APP_VEHICLE_MAINTAIN_TIME_MESSAGE_2_SHOP_CONTENT =
      "#set($tmp1= $!{context.appVehicle.nextMaintainTime}-$!{context.currentTime})" +
          "$!{context.appUser.name}（$!{context.appUser.mobile}）的爱车（$!{context.appVehicle.vehicleNo}）" +
          "#if($tmp1>=0)距离保养时间（$!{context.appVehicle.nextMaintainTimeStr}）不足$!{context.day}天" +
          "#else已超过保养时间（$!{context.appVehicle.nextMaintainTimeStr}）#if($!{context.day}>0)$!{context.day}天#end#end" +
          "！";

  //保险时间
  public final static String APP_VEHICLE_INSURANCE_TIME_MESSAGE_TITLE = "保险提醒";
  public final static String APP_VEHICLE_INSURANCE_TIME_MESSAGE_CONTENT = "您的爱车 $!{context.vehicleNo} 已接近保险日期，请及时联系店家续保！";
  public final static String APP_VEHICLE_INSURANCE_TIME_MESSAGE_2_SHOP_TITLE = "保险提醒";
  public final static String APP_VEHICLE_INSURANCE_TIME_MESSAGE_2_SHOP_CONTENT =
      "#set($tmp1= $!{context.appVehicle.nextInsuranceTime}-$!{context.currentTime})" +
          "$!{context.appUser.name}（$!{context.appUser.mobile}）的爱车（$!{context.appVehicle.vehicleNo}）" +
          "#if($tmp1>=0)距离保险时间（$!{context.appVehicle.nextInsuranceTimeStr}）不足$!{context.day}天" +
          "#else已超过保险时间（$!{context.appVehicle.nextInsuranceTimeStr}）#if($!{context.day}>0)$!{context.day}天#end#end" +
          "！";

  //验车时间
  public final static String APP_VEHICLE_EXAMINE_TIME_MESSAGE_TITLE = "验车提醒";
  public final static String APP_VEHICLE_EXAMINE_TIME_MESSAGE_CONTENT = "您的爱车 $!{context.vehicleNo} 已接近验车日期，请及时联系店家验车！";
  public final static String APP_VEHICLE_EXAMINE_TIME_MESSAGE_2_SHOP_TITLE = "验车提醒";
  public final static String APP_VEHICLE_EXAMINE_TIME_MESSAGE_2_SHOP_CONTENT =
      "#set($tmp1= $!{context.appVehicle.nextExamineTime}-$!{context.currentTime})" +
          "$!{context.appUser.name}（$!{context.appUser.mobile}）的爱车（$!{context.appVehicle.vehicleNo}）" +
          "#if($tmp1>=0)距离验车时间（$!{context.appVehicle.nextExamineTimeStr}）不足$!{context.day}天" +
          "#else已超过验车时间（$!{context.appVehicle.nextExamineTimeStr}）#if($!{context.day}>0)$!{context.day}天#end#end" +
          "！";

  //过期单据提醒
  public final static String OVERDUE_APPOINT_REMIND_SHOP_MESSAGE_TITLE = "服务提醒";
  public final static String OVERDUE_APPOINT_REMIND_SHOP_MESSAGE_CONTENT  = "$!{context.vehicleNo} 已向本店提交$!{context.appointTimeStr} $!{context.services} 预约服务已经到期，点击查看预约详情友情提示：为了保证客户及时到店服务，请及时联系客户！";
  //快过期 单据提醒shop
  public final static String SOON_EXPIRE_APPOINT_REMIND_SHOP_MESSAGE_TITLE = "服务提醒";
  public final static String SOON_EXPIRE_APPOINT_REMIND_SHOP_MESSAGE_CONTENT  = "$!{context.vehicleNo} 已向本店提交$!{context.appointTimeStr} $!{context.services} 预约服务马上到期，点击查看预约详情友情提示：为了保证客户及时到店服务，请及时联系客户！";

  public final static String OVERDUE_APPOINT_REMIND_APP_MESSAGE_TITLE = "服务提醒";
  public final static String OVERDUE_APPOINT_REMIND_APP_MESSAGE_CONTENT= "您预约 $!{context.shopDTO.name} $!{context.appointTimeStr} $!{context.services}已经到期，请及时到店服务或联系店家改期！";
  //快过期 单据提醒 app
  public final static String SOON_EXPIRE_APPOINT_REMIND_APP_MESSAGE_TITLE = "服务提醒";
  public final static String SOON_EXPIRE_APPOINT_REMIND_APP_MESSAGE_CONTENT= "您预约 $!{context.shopDTO.name} $!{context.appointTimeStr} $!{context.services}马上到期，请及时到店服务！";

  public final static String SHOP_ADVERT_APP_MESSAGE_CONTENT= "店铺$!{context.shopName}于$!{context.advertDateStr}发布新公告,请及时查看!";
  public final static String SHOP_ADVERT_APP_MESSAGE_CONTENT_TITLE = "店铺公告";

  public final static String APP_VEHICLE_VIOLATE_REGULATION_RECORD = "违章记录";

  public final static String APP_TALI = "对话消息";

  public final static String APP_VEHICLE_VIOLATE_REGULATION_RECORD_MESSAGE_CONTENT = "您的爱车有新的违章记录，请及时查看！";






}
