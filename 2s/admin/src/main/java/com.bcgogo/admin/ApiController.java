package com.bcgogo.admin;

import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.utils.StringUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;

/**
 * https 访问 http服务器
 * Created by Hans on 13-12-16.
 */
@Controller
@RequestMapping("/api/proxy/*")
public class ApiController {
  @RequestMapping(value =
      "/baidu/map/shop/{coordinateLat}/{coordinateLon}/{city}/{region}/{addressDetail}")
  public void shopBaiduMap(@PathVariable("coordinateLat") String coordinateLat,
                           @PathVariable("coordinateLon") String coordinateLon,
                           @PathVariable("addressDetail") String addressDetail,
                           HttpServletResponse response, HttpServletRequest request,
                           @PathVariable("city") String city, @PathVariable("region") String region) throws Exception {
    String http = ConfigUtils.getHttpServerURL();
    coordinateLat = StringUtil.isEmptyAppGetParameter(coordinateLat) ? null : coordinateLat;
    coordinateLon = StringUtil.isEmptyAppGetParameter(coordinateLon) ? null : coordinateLon;
    addressDetail = StringUtil.isEmptyAppGetParameter(addressDetail) ? null : addressDetail;
    city = StringUtil.isEmptyAppGetParameter(city) ? null : city;
    region = StringUtil.isEmptyAppGetParameter(region) ? null : region;
    String url = generateURL(coordinateLat, coordinateLon, addressDetail, city, region,request.getParameter("origin"));
    response.sendRedirect(redirect(http, url));
  }

  private String generateURL(String coordinateLat, String coordinateLon,
                             String addressDetail, String city, String region, String origin) throws IOException {
    if ((StringUtil.isEmpty(coordinateLat) || StringUtil.isEmpty(coordinateLon)) && (StringUtil.isEmpty(addressDetail) && StringUtil.isEmpty(city)))
      return "/400.html";
    String url = "/adminShopBaiduMap.html?origin="+origin;
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

  private String redirect(String http, String page) {
    return "http://" + http + page;
  }


}
