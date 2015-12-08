package com.bcgogo.socketReceiver.constant;

/**
 * Created by XinyuQiu on 14-10-27.
 */
public class ZHYSConstant {
  public static final String AUT_TYPE_AUT = "AUT";
  public static final String AUT_TYPE_ACCON = "ACCON";//state 为11
  public static final int AUT_TYPE_ACCON_STATE = 11;//state 为11
  public static final String AUT_TYPE_ACCOFF = "ACCOFF";//state 为12
  public static final int AUT_TYPE_ACCOFF_STATE = 12;//state 为12

  // DTU 数据#分割位置
  public static final int IDX_DTU_IMEI = 0;
  public static final int IDX_DTU_DAY = 8;
  public static final int IDX_DTU_TIME = 9;
  public static final int IDX_DTU_STATE = 1;
  public static final int IDX_DTU_INFO = 7;
  public static final int IDX_AUT_TYPE = 2;
  public static final int IDX_AUT_INFO = 6;
  public static final int IDX_AUT_GROUP = 3;
  public static final int IDX_AUT_CELLPOS = 4;

  //DTU<> 内数据下标
  public static final int IDX_DTU_SPWR = 0;
  public static final int IDX_DTU_ECT = 1;
  public static final int IDX_DTU_RPM = 2;
  public static final int IDX_DTU_MAX_R = 3;
  public static final int IDX_DTU_VSS = 4;
  public static final int IDX_DTU_MAX_S = 5;
  public static final int IDX_DTU_BAD_H = 6;
  public static final int IDX_DTU_BAD_L = 7;
  public static final int IDX_DTU_RUN_TIME = 8;
  public static final int IDX_DTU_MIL_DIST = 9;
  public static final int IDX_DTU_FUEL_LVL = 10;
  public static final int IDX_DTU_VPWR = 11;
  public static final int IDX_DTU_CAC_ITRFE = 12;//ml
  public static final int IDX_DTU_CAC_AFE = 13;
  public static final int IDX_DTU_CAC_TFE = 14;
  public static final int IDX_DTU_CAC_TRFE = 15;
  public static final int IDX_DTU_AD_MIL = 16;

  public static final int IDX_DTU_TR_MIL = 17;
  public static final int IDX_DTU_DE_MIL = 18;
  public static final int IDX_DTU_H_ST = 19;

  public static final int IDX_DTU_AD_FEH = 20;
  public static final int IDX_DTU_DRI_T = 21;
  public static final int IDX_DTU_IDLE_T = 22;
  public static final int IDX_DTU_RDTC = 23;
  public static final int IDX_DTU_RPDTC = 24;

  //坐标信息
  public static final int IDX_AUT_LON = 0;
  public static final int IDX_AUT_LONDIR = 1;
  public static final int IDX_AUT_LAT = 2;
  public static final int IDX_AUT_LATDIR =3;
  public static final int IDX_AUT_VELOCITY = 4;
  public static final int IDX_AUT_HEADING = 5;
}
