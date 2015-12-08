package com.bcgogo.enums.app;

/**
 * app_user_customer_update_task
 * Created with IntelliJ IDEA.
 * User: lw
 * Date: 13-10-12
 * Time: 上午9:27
 * To change this template use File | Settings | File Templates.
 */
public enum TaskType {
  APP_REGISTER,      //APP用户注册时候的task
  APP_APPOINTMENT, //app预约
  APP_UPDATE_INFORMATION,//app更改个人资料
  APP_UPDATE_VEHICLE,//app修改车辆信息
  APP_DELETE_VEHICLE,//app删除车辆

  CUSTOMER_MERGE,//客户合并

  CUSTOMER_REPAIR_ORDER,//客户施工单
  CUSTOMER_SALES_ORDER,//客户销售单
  CUSTOMER_SALES_RETURN,//客户销售退货
  CUSTOMER_WASH_BEAUTY, //客户洗车美容
  CUSTOMER_WEB_APPOINTMENT,//客户web端预约
  CUSTOMER_BORROW_ORDER,//借调单

  RELATED_SHOP_UPDATE, //店铺修改信息，相关联的客户或者供应商信息同步


  WEB_UPDATE_APPOINT_ORDER,//预约单改单

  WEB_ADD_NEW_CUSTOMER,//web新增客户信息
  WEB_UPDATE_CUSTOMER,//web修改客户信息
  WEB_UPDATE_MOBILE,//web修改手机号

  WEB_IMPORT_CUSTOMER;//web导入客户数据


}
