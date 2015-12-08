package com.bcgogo.enums;

/**
 * Created by IntelliJ IDEA.
 * User: Jimuchen
 * Date: 12-6-13
 * Time: 上午11:53
 * To change this template use File | Settings | File Templates.
 */
public enum UnitStatus {
  BLANK,                  //库存单位，销售单位均为空时
  USE_STORAGE_UNIT,     //同时存在库存单位，销售单位，且使用库存大单位状态
  USE_SELL_UNIT,        //同时存在库存单位，销售单位，且使用销售小单位
  ERROR;                 //用户使用单位异常
}
