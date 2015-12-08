package com.bcgogo.web;

import com.bcgogo.api.DriveLogDTO;
import com.bcgogo.baidu.model.AddressComponent;
import com.bcgogo.baidu.service.IGeocodingService;
import com.bcgogo.common.Pager;
import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.Coordinate;
import com.bcgogo.user.service.app.IDriveLogService;
import com.bcgogo.utils.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * https 访问 http服务器
 * Created by Hans on 13-12-16.
 */
@Controller
@RequestMapping("/api/proxy/*")
public class ApiController {

  @RequestMapping(value = "/baidu/map/shop")
  public void shopBaiduMap(HttpServletResponse response, HttpServletRequest request) throws Exception {
    String http = ConfigUtils.getHttpServerURL();
    String coordinateLat = request.getParameter("coordinateLat");
    String coordinateLon = request.getParameter("coordinateLon");
    if (StringUtil.isEmpty(coordinateLat) || StringUtil.isEmpty(coordinateLon))
      response.sendRedirect(redirect(http, "/400.html"));
    response.sendRedirect(redirect(http, "/shopBaiduMap.html?coordinateLat=" + coordinateLat + "&coordinateLon=" + coordinateLon));
  }

  @RequestMapping(value = "/baidu/map/shop/register")
  public void shopRegisterBaiduMap(HttpServletResponse response, HttpServletRequest request) throws Exception {
    String http = ConfigUtils.getHttpServerURL();
    String coordinateLat = request.getParameter("coordinateLat");
    String coordinateLon = request.getParameter("coordinateLon");
    String addressDetail = request.getParameter("addressDetail");
    String city = request.getParameter("city");
    String region = request.getParameter("region");
    coordinateLat = StringUtil.isEmpty(coordinateLat) ? null : coordinateLat;
    coordinateLon = StringUtil.isEmpty(coordinateLon) ? null : coordinateLon;
    addressDetail = StringUtil.isEmpty(addressDetail) ? null : addressDetail;
    city = StringUtil.isEmpty(city) ? null : city;
    region = StringUtil.isEmpty(region) ? null : region;
    String url = generateURL(coordinateLat, coordinateLon, addressDetail, city, region, request.getParameter("origin"));
    response.sendRedirect(redirect(http, url));
  }

  private String generateURL(String coordinateLat, String coordinateLon,
                             String addressDetail, String city, String region, String origin) throws IOException {
    if ((StringUtil.isEmpty(coordinateLat) || StringUtil.isEmpty(coordinateLon)) && (StringUtil.isEmpty(addressDetail) && StringUtil.isEmpty(city)))
      return "/400.html";
    String url = "/webShopRegisterBaiduMap.html?origin=" + origin;
    if (StringUtil.isNotEmpty(coordinateLat)) {
      url += "&coordinateLat=" + coordinateLat;
    }
    if (StringUtil.isNotEmpty(coordinateLon)) {
      url += "&coordinateLon=" + coordinateLon;
    }
    if (StringUtil.isNotEmpty(addressDetail)) {
      url += "&addressDetail=" + URLEncoder.encode(addressDetail, "UTF-8");
    }
    if (StringUtil.isNotEmpty(city)) {
      url += "&city=" + URLEncoder.encode(city, "UTF-8");
    }
    if (StringUtil.isNotEmpty(region)) {
      url += "&region=" + URLEncoder.encode(region, "UTF-8");
    }
    return url;
  }

  @RequestMapping(value = "/baidu/map/appoint")
  public void appointBaiduMap(HttpServletResponse response, HttpServletRequest request) throws Exception {
    String http = ConfigUtils.getHttpServerURL();
    String coordinateLat = request.getParameter("coordinateLat");
    String coordinateLon = request.getParameter("coordinateLon");
    String size = request.getParameter("size");
    if (StringUtil.isEmpty(coordinateLat) || StringUtil.isEmpty(coordinateLon)) {
      response.sendRedirect(redirect(http, "/400.html"));
    }
    if ("big".equals(size)) {
      response.sendRedirect(redirect(http, "/appointOrderBigMap.html?coordinateLat=" + coordinateLat +
          "&coordinateLon=" + coordinateLon));
    } else {
      response.sendRedirect(redirect(http, "/appointOrderMap.html?coordinateLat=" + coordinateLat + "&coordinateLon=" + coordinateLon));
    }

  }
    @RequestMapping(value = "/baidu/map/common")
    public void commonBaiduMap(HttpServletResponse response, HttpServletRequest request) throws Exception {
        String http = ConfigUtils.getHttpServerURL();
        String coordinateLat = request.getParameter("coordinateLat");
        String coordinateLon = request.getParameter("coordinateLon");
        if (StringUtil.isEmpty(coordinateLat) || StringUtil.isEmpty(coordinateLon)) {
            response.sendRedirect(redirect(http, "/400.html"));
        }
        response.sendRedirect(redirect(http, "/commonBaiduMap.html?coordinateLat=" + coordinateLat +"&coordinateLon=" + coordinateLon));
    }

  private String redirect(String http, String page) {
    return "http://" + http + page;
  }

  @RequestMapping(value = "/baidu/map/vehicleDriveLog")
  public void vehicleDriveLog(HttpServletResponse response, HttpServletRequest request) throws Exception {
    String http = ConfigUtils.getHttpServerURL();
    String data = request.getParameter("data");
    if (StringUtil.isEmpty(data) || !NumberUtil.isLongNumber(data)) {

      String coordinate = request.getParameter("coordinate");
      String city = request.getParameter("city");
      response.sendRedirect(redirect(http, "/driveLog.html?"+ "shopCoordinate=" + coordinate + "&city=" + URLEncoder.encode(city, "UTF-8")));

//      response.sendRedirect(redirect("localhost:8080/web/customer/vehicle", "/vehicleDriveLog1.jsp?" + "shopCoordinate=" + coordinate + "&city=" + URLEncoder.encode(city, "UTF-8")));
    } else {

      IDriveLogService driveLogService = ServiceManager.getService(IDriveLogService.class);
      IGeocodingService geocodingService = ServiceManager.getService(IGeocodingService.class);

      DriveLogDTO driveLogDTO = driveLogService.getDriveLogDTOById(Long.valueOf(data));
      driveLogDTO.generateCoordinate();
      driveLogDTO.calculateAverageSpeed();
      if (ArrayUtil.isNotEmpty(driveLogDTO.getCoordinates())) {

        AddressComponent addressComponent = geocodingService.gpsToAddress(driveLogDTO.getStartLat(), driveLogDTO.getStartLon());
        if (addressComponent != null) {
          driveLogDTO.setStartPlace(addressComponent.getStreetInfo());
          driveLogDTO.setDetailStartPlace(addressComponent.getStreetNumberInfo());
        }

        addressComponent = geocodingService.gpsToAddress(driveLogDTO.getEndLat(), driveLogDTO.getEndLon());
        if (addressComponent != null) {
          driveLogDTO.calculateAverageSpeed();
          driveLogDTO.setEndPlace(addressComponent.getStreetInfo());
          driveLogDTO.setDetailEndPlace(addressComponent.getStreetNumberInfo());
        }
        List<Coordinate> coordinateList = geocodingService.coordinateGspToBaiDu(driveLogDTO.getCoordinates());
        if (CollectionUtil.isNotEmpty(coordinateList)) {
          driveLogDTO.setBaiDuCoordinate(coordinateList);
        }
        driveLogDTO.setCoordinates(null);
        driveLogDTO.setPlaceNoteDTO(null);
        driveLogDTO.setPlaceNotes(null);

//        String str = driveLogDTO.getParamString();
//        response.sendRedirect(redirect("localhost:8080/web/customer/vehicle", "/vehicleDriveLog1.jsp?" + driveLogDTO.getParamString()));

        if (StringUtil.isEmpty(driveLogDTO.getStartPlace()) || StringUtil.isEmpty(driveLogDTO.getEndPlace()) ||
            CollectionUtil.isEmpty(driveLogDTO.getBaiDuCoordinate())) {
          String coordinate = request.getParameter("coordinate");
          String city = request.getParameter("city");
          response.sendRedirect(redirect(http, "/driveLog.html?" + "shopCoordinate=" + coordinate + "&city=" + URLEncoder.encode(city, "UTF-8")));
        }else{
          response.sendRedirect(redirect(http, "/driveLog.html?" + driveLogDTO.generateParamString()));
        }
      }else{
        String coordinate = request.getParameter("coordinate");
        String city = request.getParameter("city");
        response.sendRedirect(redirect(http, "/driveLog.html?" + "shopCoordinate=" + coordinate + "&city=" + URLEncoder.encode(city, "UTF-8")));
      }
    }


  }

  @RequestMapping(value = "/baidu/map/vehiclePosition")
  public void vehiclePosition(HttpServletResponse response, HttpServletRequest request) throws Exception {
    String http = ConfigUtils.getHttpServerURL();
    String data = request.getParameter("data");
    if (StringUtil.isEmpty(data)) {
//      response.sendRedirect(redirect(http, "/400.html"));

      String coordinate = request.getParameter("coordinate");
      String city = request.getParameter("city");


      response.sendRedirect(redirect(http, "/vehiclePosition.html?"  + "shopCoordinate=" + coordinate + "&city=" + URLEncoder.encode(city, "UTF-8")));

//      response.sendRedirect(redirect("localhost:8080/web/customer/vehicle", "/vehiclePostion1.jsp?" + "shopCoordinate=" + coordinate + "&city=" + URLEncoder.encode(city, "UTF-8")));

    } else if(ArrayUtil.isNotEmpty(data.split(",,,"))) {
      String[] array = data.split(",,,");
      StringBuilder sb = new StringBuilder();


      for (int i = 0; i < array.length; i++) {
        String string = array[i];
        String[] strings = string.split("__");
        if (strings.length == 5) {
          strings[3] = URLEncoder.encode(strings[3], "UTF-8");
          strings[4] = URLEncoder.encode(strings[4], "UTF-8");
          for (int index = 0; index < strings.length; index++) {
            String str = strings[index];
            sb.append(str);
            if (index != strings.length - 1) {
              sb.append("__");
            }
          }

          if (i != array.length - 1) {
            sb.append(",,,");
          }
        }
      }

//      response.sendRedirect(redirect("localhost:8080/web/customer/vehicle", "/vehiclePostion1.jsp?" +sb.toString()));

      response.sendRedirect(redirect(http, "/vehiclePosition.html?" + sb.toString()));

    } else {
      response.sendRedirect(redirect(http, "/400.html"));
    }
  }

}
