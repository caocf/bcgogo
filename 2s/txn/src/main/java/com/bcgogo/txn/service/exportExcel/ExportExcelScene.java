package com.bcgogo.txn.service.exportExcel;

/**
 * Created with IntelliJ IDEA.
 * User: king
 * Date: 13-7-31
 * Time: 下午2:40
 * To change this template use File | Settings | File Templates.
 */
public enum  ExportExcelScene {
   INVENTORY("导出商品"),
   CUSTOMER("导出会员"),
   ORDER("导出单据"),
   CUSTOMER_TRANSACTION("导出客户交易"),
   REPAIR_BUSINESS_STAT("车辆施工营业统计"),
   SALES_BUSINESS_STAT("商品销售营业统计"),
   BUSINESS_STAFF_STAT("员工业绩统计"),
   WASH_BUSINESS_STAT("洗车营业统计"),
   REPAIR_ASSISTANT_STAT("员工业绩统计-车辆施工"),
   WASH_ASSISTANT_STAT("员工业绩统计-洗车美容"),
   SALES_ASSISTANT_STAT("员工业绩统计-商品销售"),
   MEMBER_ASSISTANT_STAT("员工业绩统计-会员卡销售"),
   BUSINESS_ACCOUNT_ASSISTANT_STAT("员工业绩统计-营业外收入"),
   CUSTOMER_REMIND("客户提醒服务"),
   SHOP_FAULT_INFO("事故故障提醒"),
   VEHICLE_LIST("客户车辆导出");


   String scene;

    public String getScene() {
        return scene;
    }

   ExportExcelScene(String scene) {
       this.scene = scene;
   }
}
