package com.bcgogo.api.filter;

import com.bcgogo.api.AppUserLoginInfoDTO;
import com.bcgogo.user.service.utils.SessionUtil;
import com.bcgogo.utils.CookieUtil;
import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.enums.app.MessageCode;
import com.bcgogo.utils.JsonUtil;
import com.bcgogo.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
* Created with IntelliJ IDEA.
* User: zoujianhong
* Date: 13-8-21
* Time: 上午9:34
*/
public class PermissionFilter implements Filter {
  private static final Logger LOG = LoggerFactory.getLogger(PermissionFilter.class);
  private static final String LOGIN_PATH = "/login";
  private static final String LOGOUT_PATH = "/logout";
  private static final String WX = "/wx";
  private static final String VIDEO = "/video";
  private static final String service = "/service";
  private static final String REGISTRATION_PATH = "/user/registration";
  private static final String VEHICLE_INFO_SUGGESTION = "/vehicle/info/suggestion";
  private static final String PASSWORD_PATH = "/user/password";
  private static final String NEW_VERSION_PATH = "/newVersion";
  private static final String VEHICLE_BRAND_MODEL_KEYWORDS = "/vehicle/brandModel/keywords";
  private static final String APP_DOWNLOAD = "/bcgogo/app/download";
  private static final String OBD_MESSAGE = "/obd/OBDMessage";
  private static final String SERVICE_CATEGORY_LIST = "/serviceCategory/list/serviceScope";
  private static final String AREA_LIST = "/area/list/";
  private static final String JUHE_AREA_LIST = "/violateRegulations/juhe/area/list";

  private static final String GUEST = "/guest/";
  private static final String SUFFIX_HTML = ".html";

  private static final String GSM_REGISTRATION_PATH = "/register/gsm/register";
  private static final String GSM_VALIDATE_REGISTRATION_PATH = "/register/gsm/validateRegister";
  private static final String GSM_LOGIN_PATH = "/gsm/login";
  private static final String PLAT_LOGIN_PATH = "/plat/login";
  private static final String MIRROR_LOGIN_PATH = "/mirror/login";
  private static final String GSM_PASSWORD_PATH = "/user/gsm/password";
  private static final String AREA_JUHE_LIST = "/area/juhe/list";
  private static final String BCGOGO_SHOP_OWNER_LOGIN_PATH = "/bcgogoApp/login";
  private static final String WX_NOTIFY_URL = "/user/wxprepayOrder";
  private static final String ALI_NOTIFY_URL = "/user/aliprepayOrder";
  private static final String WX_SHARE_URL = "/user/share";


  private static final List<String> excludePaths = new ArrayList<String>();

  static {
    excludePaths.add(VEHICLE_INFO_SUGGESTION);
    excludePaths.add(LOGIN_PATH);
    excludePaths.add(LOGOUT_PATH);
    excludePaths.add(REGISTRATION_PATH);
    excludePaths.add(PASSWORD_PATH);
    excludePaths.add(NEW_VERSION_PATH);
    excludePaths.add(VEHICLE_BRAND_MODEL_KEYWORDS);
    excludePaths.add(APP_DOWNLOAD);
    excludePaths.add(OBD_MESSAGE);
    excludePaths.add(SERVICE_CATEGORY_LIST);
    excludePaths.add(AREA_LIST);
    excludePaths.add(JUHE_AREA_LIST);
    excludePaths.add(GSM_REGISTRATION_PATH);
    excludePaths.add(GSM_VALIDATE_REGISTRATION_PATH);
    excludePaths.add(GSM_LOGIN_PATH);
    excludePaths.add(PLAT_LOGIN_PATH);
    excludePaths.add(MIRROR_LOGIN_PATH);
    excludePaths.add(GSM_PASSWORD_PATH);
    excludePaths.add(AREA_JUHE_LIST);
    excludePaths.add(BCGOGO_SHOP_OWNER_LOGIN_PATH);
    excludePaths.add(WX);
    excludePaths.add(VIDEO);
    excludePaths.add(service);
    excludePaths.add(WX_NOTIFY_URL);
    excludePaths.add(ALI_NOTIFY_URL);
    excludePaths.add(WX_SHARE_URL);
  }

  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
      throws IOException, ServletException {
    HttpServletRequest request = (HttpServletRequest) servletRequest;
    HttpServletResponse response = (HttpServletResponse) servletResponse;
    String sessionId = CookieUtil.getSessionId(request);
    logRequestParams(request, response, UUID.randomUUID());
    if (ConfigUtils.appNeedPermissionValidate() && !requestExcluded(request)) {
      if (StringUtil.isEmpty(sessionId)) {
        timeout(response);
      } else {
        try {
          AppUserLoginInfoDTO appUserLoginInfoDTO = SessionUtil.getAppUserLoginInfo(response, sessionId);
          if (appUserLoginInfoDTO != null) {
            chain.doFilter(servletRequest, response);
          } else {
            //cat not find login log in AppUserLoginInfo                 000000000000000000000000000000000000000000000000000000000000000000000000000000000
            timeout(response);
          }
        } catch (Exception e) {
          LOG.error(e.getMessage(), e);
        }
      }
    } else {
      chain.doFilter(servletRequest, response);
    }

  }

  private void logRequestParams(HttpServletRequest request, HttpServletResponse response, UUID uuid) {
    try {
      StringBuilder builder = new StringBuilder();
//      Enumeration pNames = request.getParameterNames();
////      System.out.println(request.getParameterMap());
//      while (pNames.hasMoreElements()) {
//        String pName = (String) pNames.nextElement();
//        String pValues[] = request.getParameterValues(pName);
//        StringBuilder result = new StringBuilder(pName);
//        result.append('=');
//        for (int i = 0; i < pValues.length; i++) {
//          if (i > 0)
//            result.append(", ");
//          result.append(pValues[i]);
//        }
//        builder.append(result).append(", ");
//      }
      LOG.info("标识ID: {}. 请求URI: {}", uuid.toString(), request.getRequestURL());
      if (builder.length() != 0)
        LOG.info("标识ID: {}. 请求参数: {}", uuid.toString(), builder.substring(0, builder.length() > 1 ? builder.length() - 2 : builder.length()));
    } catch (Exception e) {
      LOG.warn("Log请求参数时出错.", e);
    }
  }

  private void timeout(HttpServletResponse response) throws IOException {
    try {
      response.setContentType("application/json; charset=UTF-8");
      PrintWriter writer = response.getWriter();
      writer.write(JsonUtil.objectToJson(MessageCode.toApiResponse(MessageCode.LOGIN_TIME_OUT)));
      writer.close();
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
  }


  /**
   * 判断请求是否不需要登录验证
   *
   * @param request HttpServletRequest
   * @return boolean
   */
  private boolean requestExcluded(HttpServletRequest request) {
    String uri = request.getRequestURI();
    for (String excludePath : excludePaths) {
      if (uri.startsWith(request.getContextPath() + excludePath)) {
        return true ;
      }
    }
    return uri.contains(SUFFIX_HTML) || uri.contains(GUEST);
  }

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
  }

  @Override
  public void destroy() {
  }
}
