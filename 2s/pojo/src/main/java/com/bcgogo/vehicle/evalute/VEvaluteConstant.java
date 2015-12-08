package com.bcgogo.vehicle.evalute;

import java.security.PublicKey;

/**
 * 车价评估相关变量
 * Author: ndong
 * Date: 14-11-5
 * Time: 下午1:45
 */
public class VEvaluteConstant {


  /**************************************car360提供接口查询地址*****************************************************************/

  public static final String TOKEN_CAR360="c9031860f5e217cbcdc3aa11a2b076d0";
  //返回所有的城市列表。
  public static final String URL_GET_ALL_CITY="http://api.che300.com/service/PublicService.php?oper=getAllCity&token={TOKEN}";
   //返回所有的品牌列表。
  public static final String URL_GET_CAR_BRAND_LIST="http://api.che300.com/service/PublicService.php?oper=getCarBrandList&token={TOKEN}";
   //返回指定品牌下面的所有车系列表。。
  public static final String URL_GET_CAR_SERIES_LIST="http://api.che300.com/service/PublicService.php?oper=getCarSeriesList&token={TOKEN}";
    //返回指定车系下面的所有车型
  public static final String URL_GET_CAR_MODEL_LIST="http://api.che300.com/service/PublicService.php?oper=getCarModelList&token={TOKEN}";
  //返回所有的城市列表。
  public static final String URL_GET_USED_CAR_PRICE="http://api.che300.com/service/PublicService.php?oper=getUsedCarPrice&token={TOKEN}";

}
