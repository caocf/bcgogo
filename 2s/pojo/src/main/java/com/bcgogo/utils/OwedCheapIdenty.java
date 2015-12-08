package com.bcgogo.utils;

/**
 * Created by IntelliJ IDEA.
 * User: caiweili
 * Date: 1/31/12
 * Time: 1:37 PM
 * To change this template use File | Settings | File Templates.
 */
public enum OwedCheapIdenty {
  /**
 * 欠款优惠标识
 */
  none,
  //无欠款无优惠
  unowedUncheap,
  //无欠款有优惠
  unowedCheap,
  //有欠款无优惠
  owedUncheap,
  //有欠款有优惠
  owedCheap
}
