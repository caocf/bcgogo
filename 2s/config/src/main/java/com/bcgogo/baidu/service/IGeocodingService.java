package com.bcgogo.baidu.service;

import com.bcgogo.baidu.model.AddressComponent;
import com.bcgogo.baidu.model.geocoder.CoordinateConvertResponse;
import com.bcgogo.user.Coordinate;
import com.bcgogo.baidu.model.geocoder.GeocoderResponse;

import java.io.IOException;
import java.util.List;

/**
 * 根据关键字或者经纬度信息，获取到相应的百度经纬度或者结构化地理信息
 * User: ZhangJuntao
 * Date: 13-8-2
 * Time: 上午10:54
 */
public interface IGeocodingService {

  /**
   * @param address
   * @param city
   */
  GeocoderResponse addressToCoordinate(String address, String city) throws IOException;

  /**
   * @param location lat: 纬度：数值，lng: 经度：数值
   * @return GeocoderResponse
   */
  GeocoderResponse coordinateToAddress(Coordinate location) throws IOException;

  GeocoderResponse coordinateToAddress(Double lat, Double lng) throws IOException;

  //只返回"街,市"
  String gpsCoordinateToAddress(String lat, String lng);

  String gpsCoordinate2FullAddress(String lat, String lng);

  AddressComponent gpsToAddress(String lat, String lng);

  List<Coordinate> coordinateGspToBaiDu(Coordinate... coordinates);

  Coordinate coordinateGspToBaiDu(String lon, String lat);


}
