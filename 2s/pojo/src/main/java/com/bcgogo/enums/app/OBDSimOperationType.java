package com.bcgogo.enums.app;

/**
 * Created by XinyuQiu on 14-6-16.
 */
public enum  OBDSimOperationType {
  SINGLE_GSM_OBD_IMPORT("导入入库OBD单品"),
  SINGLE_MIRROR_OBD_IMPORT("导入入库后视镜单品"),
  SINGLE_GSM_OBD_EDIT("编辑OBD单品信息"),
  SINGLE_MIRROR_OBD_EDIT("编辑后视镜单品信息"),
  SINGLE_GSM_OBD_DELETE("删除OBD/后视镜单品信息"),
  SINGLE_OBD_SIM_IMPORT("导入入库SIM单品"),
  SINGLE_OBD_SIM_EDIT("编辑SIM单品信息"),
  SINGLE_OBD_SIM_DELETE("删除SIM单品信息"),
  GSM_OBD_SIM_PACKAGE("组装成品"),
  COMBINE_GSM_OBD_SIM_IMPORT("导入入库OBD_SIM成品"),
  COMBINE_MIRROR_OBD_SIM_IMPORT("导入入库MIRROR_SIM成品"),
  COMBINE_GSM_OBD_SIM_EDIT("编辑OBD_SIM成品信息"),
  COMBINE_MIRROR_OBD_SIM_EDIT("编辑MIRROR_SIM成品信息"),
  COMBINE_GSM_OBD_SIM_SPLIT("拆分成单品"),
  AGENT_OUT_STORAGE("代理出库"),
  EMPLOYEE_OUT_STORAGE("领出出库"),
  IN_STORAGE("入库"),
  SELL_TO_SHOP("销售至店铺"),
  SELL_TO_CUSTOMER("销售至车主"),
  RETURN_STORAGE("归还成品"),
  INSTALL("安装到车辆"),
  UN_INSTALL("从车辆卸载"),
  ;

  private final String name;

  private OBDSimOperationType(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

}
