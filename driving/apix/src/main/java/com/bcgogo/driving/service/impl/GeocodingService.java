package com.bcgogo.driving.service.impl;

import com.bcgogo.pojox.config.*;
import com.bcgogo.pojox.enums.GeocoderStatus;
import com.bcgogo.driving.service.IGeocodingService;
import com.bcgogo.pojox.util.ArrayUtil;
import com.bcgogo.pojox.util.CollectionUtil;
import com.bcgogo.pojox.util.JsonUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * 根据关键字或者经纬度信息，获取到相应的百度经纬度或者结构化地理信息
 * User: ZhangJuntao
 * Date: 13-8-2
 * Time: 上午10:54
 */
@Component
public class GeocodingService implements IGeocodingService {
  private static final Logger LOG = LoggerFactory.getLogger(GeocodingService.class);
  private static final String DEFAULT_ENCODE = "UTF-8";
  private static final String OUTPUT = "json";
  //TODO set to db config
  String BAI_DU_GEOCODER_URL = "http://api.map.baidu.com/geocoder";
  String key = "37492c0ee6f924cb5e934fa08c6b1676";

  @Override
  public GeocoderResponse addressToCoordinate(String address, String city) throws IOException {
    if (StringUtils.isBlank(key)) {
      return new GeocoderResponse(GeocoderStatus.INVILID_KEY);
    }
    if (StringUtils.isBlank(address)) {
      return new GeocoderResponse(GeocoderStatus.INVALID_PARAMETERS);
    }
    StringBuilder url = new StringBuilder(BAI_DU_GEOCODER_URL);
    url.append("?output=").append(OUTPUT);
    url.append("&address=").append(URLEncoder.encode(address, DEFAULT_ENCODE));
    if (StringUtils.isNotBlank(city)) {
      url.append("&city=").append(URLEncoder.encode(city, DEFAULT_ENCODE));
    }
//    url.append("&key=").append(key);
    BcgogoHttpResponse bcgogoHttpResponse = new BcgogoHttpRequest().sendGet(url.toString());
    return JsonUtil.fromJson(bcgogoHttpResponse.getContent(), GeocoderResponse.class);
  }

  @Override
  public GeocoderResponse coordinateToAddress(Coordinate location) throws IOException {
//    if (StringUtils.isBlank(key)) {
//      return new GeocoderResponse(GeocoderStatus.INVILID_KEY);
//    }
    if (location == null || StringUtils.isBlank(location.getLat()) || StringUtils.isBlank(location.getLng())) {
      return new GeocoderResponse(GeocoderStatus.INVALID_PARAMETERS);
    }
    StringBuilder url = new StringBuilder(BAI_DU_GEOCODER_URL);
    url.append("?output=").append(OUTPUT);
    url.append("&location=").append(location.getLat()).append(",").append(location.getLng());
//    url.append("&key=").append(key);
    BcgogoHttpResponse bcgogoHttpResponse = new BcgogoHttpRequest().sendGet(url.toString());
    return JsonUtil.fromJson(bcgogoHttpResponse.getContent(), GeocoderResponse.class);
  }

  @Override
  public GeocoderResponse coordinateToAddress(Double lat, Double lng) throws IOException {
    return coordinateToAddress(new Coordinate(String.valueOf(lat), String.valueOf(lng)));
  }


  /**
   * 只返回"街,市"
   *
   * @param lat
   * @param lng
   * @return
   */
  @Override
  public String gpsCoordinateToAddress(String lat, String lng) {
    try {
      return gpsCoordinate2Address(lat, lng);
    } catch (ConnectException e) {
      LOG.error("获取百度地址名连接超时，将重新请求数据：lat：{},lng:{}" + e.getMessage(), lat, lng);
      try {
        return gpsCoordinate2Address(lat, lng);
      } catch (IOException e1) {
        LOG.error("根据GSP坐标获取百度地址名出错：lat：{},lng:{}" + e.getMessage(), lat, lng);
      }
    } catch (SocketTimeoutException e) {
      LOG.error("获取百度地址名读取数据超时，将重新请求数据：lat：{},lng:{}" + e.getMessage(), lat, lng);
      try {
        return gpsCoordinate2Address(lat, lng);
      } catch (IOException e1) {
        LOG.error("根据GSP坐标获取百度地址名出错：lat：{},lng:{}" + e.getMessage(), lat, lng);
      }
    } catch (Exception e) {
      LOG.error("根据GSP坐标获取百度地址名出错：lat：{},lng:{}" + e.getMessage(), lat, lng);
      LOG.error(e.getMessage(), e);
    }
    return null;
  }

  private String gpsCoordinate2Address(String lat, String lng) throws IOException {
    AddressComponent addressComponent = doGpsCoordinateToAddress(lat, lng);
    if (addressComponent == null) {
      return null;
    }
    StringBuilder sb = new StringBuilder();
    if (StringUtils.isNotBlank(addressComponent.getStreet())) {
      sb.append(addressComponent.getStreet());
      sb.append(",");
    }
    if (StringUtils.isNotBlank(addressComponent.getCity())) {
      sb.append(addressComponent.getCity());
    }
    return sb.toString();
  }

  @Override
  public AddressComponent gpsToAddress(String lat, String lng) {
    try {
      return doGpsCoordinateToAddress(lat, lng);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return null;
    }
  }

  /**
   * lat,lng为标准的gps坐标
   * @param lat
   * @param lng
   * @return
   * @throws java.io.IOException
   */
  private AddressComponent doGpsCoordinateToAddress(String lat, String lng) throws IOException {
    if (StringUtils.isBlank(lat) || StringUtils.isBlank(lng)) {
      return null;
    }
    StringBuilder url = new StringBuilder("http://api.map.baidu.com/geocoder/v2/");
    url.append("?ak=").append("LdGlzZuU5cEzMDspXXFGCDSY");
    url.append("&location=").append(lat).append(",").append(lng);
    url.append("&output=json&pois=0");
    url.append("&coordtype=wgs84ll");
    BcgogoHttpResponse bcgogoHttpResponse = new BcgogoHttpRequest().sendGet(url.toString());
    GeocoderResponseV2 geocoderResponse = JsonUtil.fromJson(bcgogoHttpResponse.getContent(), GeocoderResponseV2.class);
    if (geocoderResponse == null || !geocoderResponse.isSuccess()) {
      return null;
    }
    GeocoderResult geocoderResult = geocoderResponse.getResult();
    return geocoderResult.getAddressComponent();
  }


  @Override
  public String gpsCoordinate2FullAddress(String lat, String lng) {
    try {
      AddressComponent addressComponent = doGpsCoordinateToAddress(lat, lng);
      return addressComponent != null ? addressComponent.getAddress() : "";
    } catch (Throwable e) {
      LOG.error("gpsCoordinateToAddressForDriveLog 根据GSP坐标获取百度地址名出错：lat：{},lng:{}" + e.getMessage(), lat, lng);
      LOG.error(e.getMessage(), e);
    }
    return null;
  }

  @Override
  public Coordinate coordinateGspToBaiDu(String lng, String lat) {
    Coordinate coordinate = new Coordinate();
    coordinate.setLat(lat);
    coordinate.setLng(lng);
    return CollectionUtil.getFirst(coordinateGspToBaiDu(coordinate));
  }

  @Override
  public List<Coordinate> coordinateGspToBaiDu(Coordinate... coordinates) {

    List<Coordinate> coordinateList = new ArrayList<Coordinate>();
    try {
      //http://api.map.baidu.com/geoconv/v1/?coords=114.21892734521,29.575429778924;114.21892734521,29.575429778924&from=1&to=5&ak=你的密钥
      if (ArrayUtil.isEmpty(coordinates)) {
        return coordinateList;
      }
      int maxConvertSize = 80;

      int size = coordinates.length % maxConvertSize > 0 ? ((coordinates.length / maxConvertSize) + 1) : (coordinates.length / maxConvertSize);

      for (int i = 0; i < size; i++) {
        StringBuilder url = new StringBuilder("http://api.map.baidu.com/geoconv/v1/?coords=");
        for (int index = i * maxConvertSize; index < (i + 1) * maxConvertSize; index++) {
          if (index > coordinates.length - 1) {
            break;
          }

          Coordinate coordinate = coordinates[index];
          url.append(coordinate.getLng()).append(",").append(coordinate.getLat());
          url.append(";");
        }
        url = new StringBuilder(url.substring(0, url.length() - 1));
        url.append("&output=json&from=1&to=5");
        url.append("&ak=").append("LdGlzZuU5cEzMDspXXFGCDSY");
        BcgogoHttpResponse bcgogoHttpResponse = new BcgogoHttpRequest().sendGet(url.toString());
        CoordinateConvertResponse convertResponse = JsonUtil.fromJson(bcgogoHttpResponse.getContent(), CoordinateConvertResponse.class);
        if (convertResponse != null && convertResponse.isSuccess() && CollectionUtil.isNotEmpty(convertResponse.getResult())) {
          coordinateList.addAll(convertResponse.getResult());
        }
      }

    } catch (Throwable e) {
      LOG.error("coordinateGspToBaiDu 根据GSP坐标获取百度地址：coordinates：{} " + coordinates.toString());
      LOG.error(e.getMessage(), e);
    }

    if (CollectionUtil.isNotEmpty(coordinateList)) {
      for (Coordinate coordinate : coordinateList) {
        coordinate.setLng(coordinate.getX());
        coordinate.setLat(coordinate.getY());
      }
    }

    return coordinateList;
  }

}
