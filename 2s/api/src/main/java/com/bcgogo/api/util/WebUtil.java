package com.bcgogo.api.util;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 2014-12-29
 * Time: 11:18
 */
public class WebUtil {

  public static String getBasePath(HttpServletRequest request) {
     String path = request.getContextPath();
     return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
   }

}
