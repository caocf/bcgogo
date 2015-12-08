package com.bcgogo.filter;

import com.bcgogo.common.StringUtil;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.enums.ShopConfigStatus;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.service.IRequestMonitorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * 系统性能监控过滤器
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 13-2-6
 * Time: 上午11:49
 * To change this template use File | Settings | File Templates.
 */
public class RequestPerformanceFilter implements Filter {

  private static final Logger LOG = LoggerFactory.getLogger(RequestPerformanceFilter.class);
  private static  String SYSTEM_MONITOR = "OFF";
  private static  String REQUEST_SECTION = "";
  private static  Long lastRequestTime = System.currentTimeMillis();

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

    if (servletRequest instanceof HttpServletRequest) {

      IConfigService configService = ServiceManager.getService(IConfigService.class);
      SYSTEM_MONITOR = configService.getConfig("SystemMonitor", -1l);
      REQUEST_SECTION = configService.getConfig("request_section", -1L);

      HttpServletRequest request = (HttpServletRequest) servletRequest;
      long begin = System.currentTimeMillis();
      long lastTime = lastRequestTime;
      filterChain.doFilter(servletRequest, servletResponse);
      long end = System.currentTimeMillis();


      if(!ShopConfigStatus.ON.toString().equals(SYSTEM_MONITOR)){
        return;
      }

      String url =  request.getRequestURI() + "?method=" + servletRequest.getParameter("method");
      IRequestMonitorService requestMonitorService = ServiceManager.getService(IRequestMonitorService.class);
      if (request.getSession(false) != null && request.getSession(false).getAttributeNames().hasMoreElements()) {
        Long shopId = (Long) request.getSession().getAttribute("shopId");
        String userNo = (String) request.getSession().getAttribute("userNo");
        requestMonitorService.saveUrlMonitorStatFromRequest(shopId, userNo, url);

        if (StringUtil.isEmpty(REQUEST_SECTION)) {
          return;
        }

        requestMonitorService.saveOrUpdateRequestMonitor(lastTime, begin, end, url, REQUEST_SECTION);
        lastRequestTime = begin;
      }
    }
  }

  @Override
  public void destroy() {
    //To change body of implemented methods use File | Settings | File Templates.
  }
}
