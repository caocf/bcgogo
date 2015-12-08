package com.bcgogo.utils;

/**
 * User: ZhangJuntao
 * Date: 13-9-10
 * Time: 上午10:37
 */
public class CoordinateUtils {
  public static final double DEGREES_TO_RADIANS = Math.PI / 180.0;
  //地球半径
  public static final double EARTH_MEAN_RADIUS_KM = 6371.009;
  //地球直径
  private static final double EARTH_MEAN_DIAMETER = EARTH_MEAN_RADIUS_KM * 2;

  /**
   * 距离半径计算方式
   *
   * @param latCenterRad 中心点经纬度
   * @param lonCenterRad
   * @param latVals      目标经纬度
   * @param lonVals
   * @return 两坐标的距离 单位千米
   */
  public static double getTwoCoordinatesDistance(double latCenterRad, double lonCenterRad, double latVals, double lonVals) {
    //计算经纬度
    double latRad = latVals * DEGREES_TO_RADIANS;
    double lonRad = lonVals * DEGREES_TO_RADIANS;

    //计算经纬度的差
    double diffX = latCenterRad * DEGREES_TO_RADIANS - latRad;
    double diffY = lonCenterRad * DEGREES_TO_RADIANS - lonRad;
    //计算正弦和余弦
    double hsinX = Math.sin(diffX * 0.5);
    double hsinY = Math.sin(diffY * 0.5);
    double latCenterRad_cos = Math.cos(latCenterRad * DEGREES_TO_RADIANS);
    double h = hsinX * hsinX + (latCenterRad_cos * Math.cos(latRad) * hsinY * hsinY);

    return (EARTH_MEAN_DIAMETER * Math.atan2(Math.sqrt(h), Math.sqrt(1 - h)));
  }

  public static void main(String[] args) {
    Double val = CoordinateUtils.getTwoCoordinatesDistance(120.001766, 31.7778, 119.985184, 31.785124);
    System.out.println(val);
//      assertEquals(1.88, val, 0.1);
//    val=SolrUtil.getTwoCoordinatesDistance(120.630878,31.332306, 118.848937,32.071766);
//    assertEquals(213.2, val, 0.1);
  }
}
