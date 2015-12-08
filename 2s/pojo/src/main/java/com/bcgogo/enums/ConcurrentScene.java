package com.bcgogo.enums;

/**
 * Created by IntelliJ IDEA.
 * User: Jimuchen
 * Date: 12-8-15
 * Time: 下午5:52
 */
public enum ConcurrentScene {
  MEMBER("member_"),
  CURRENT_USED("CURRENT_USED"),
  SALE("sale_"),
  PURCHASE("purchase_"),
  INVENTORY("inventory_"),
  REPAIR("repair_"),
  WASH_BEAUTY("wash_beauty_"),
  PURCHASE_RETURN("purchase_return_"),
  SALE_RETURN("sale_return_"),
  CUSTOMER("customer_"),
  SUPPLIER("supplier_"),
  REPAIR_ORDER_TEMPLATE("repair_order_template_"),
  REPAIR_PICKING("repair_picking_"),
  INNER_PICKING("inner_picking_"),
  HANDLE_PAYABLE("handle_payable_"),
  INVENTORY_CHECK("inventory_check_"),
  BORROW_ORDER("borrow_order_"),
  UPDATE_UNIT_SORT("update_unit_sort_"),
  ALLOCATE_RECORD("allocate_record"),
  CUSTOMER_STATEMENT_ACCOUNT("customer_statement_account"),
  SUPPLIER_STATEMENT_ACCOUNT("supplier_statement_account"),
  INVITE("invite_"),
  INNER_RETURN("inner_return_"),
  SHOP_CONFIG_CACHE("shop_config_cache"),
  CANCEL_SHOP_RELATION("can_shop_relation_"),
  RMI_REINDEX("rmi_reindex"),
  APPOINT_ORDER("appoint_order_"),
  PUSH_MESSAGE_RECEIVER("push_message_receiver_"),
  CHINA_PAY("china_pay_"),
  WEB_DELETE_CUSTOMER("web_delete_customer_"),
  WEB_REPAIR_ORDER_REPEAL("web_repair_order_repeal_"),
  BCGOGO_HARDWARE_RECEIVABLE_ORDER("bcgogo_hardware_receivable_order_"),
  BCGOGO_SOFTWARE_RECEIVABLE_ORDER("bcgogo_software_receivable_order_"),
  BCGOGO_SMS_RECHARGE_RECEIVABLE_ORDER("bcgogo_sms_recharge_receivable_order_"),
  ENQUIRY("enquiry_"),
  WX_GET_ACCESS_TOKEN("wx_get_access_token_"),
  WX_GET_JS_API_TICKET("wx_get_js_api_ticket_"),
  WX_BATCH_CREATE_LIMIT_QR_CODE("batchCreatelimitQRCode"),
  WX_EVENT_SUBSCRIBE("wx_event_subscribe_"),
  WX_EVENT_MENU_CLICK("wx_event_menu_click_"),
  WX_EVENT_UN_SUBSCRIBE("wx_event_un_subscribe_"),
  WX_EVENT_TEMPLATE_SEND_STATUS_REPORT("wx_event_template_send_status_report_"),
  WX_EVENT_MASS_MSG_SEND_STATUS_REPORT("wx_event_mass_msg_send_status_report_"),
  WX_EVENT_SCAN("wx_event_scan_"),
  WX_RESP_HANDLE_SUBSCRIBE("wx_resp_handle_subscribe_"),
  WX_HANDLE_V_REGULATION("wx_resp_handle_v_regulation_"),
  WX_RESP_HANDLE_UN_SUBSCRIBE("wx_resp_handle_un_subscribe_"),
  APP_ORDER_COMMENT("app_order_comment_"),
  APP_SAVE_VEHICLE_FAULT_INFO("app_save_vehicle_fault_info_"),
  CAMERA_VARN_DATA("camera_varn_data_"),
  GSM_APP_FIND_PWD("gsm_app_find_pwd_");

  ;

  private ConcurrentScene(String name){
    this.name = name;
  }
  private String name;

  public String getName() {
    return name;
  }
}
