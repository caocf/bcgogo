package com.bcgogo.config;

import com.bcgogo.config.dto.AreaDTO;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.model.Area;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: MZDong
 * Date: 11-10-11
 * Time: 下午2:57
 * To change this template use File | Settings | File Templates.
 */

// ToDo: handle exception.
@Controller
@RequestMapping("/backShop.do")
public class AdminConfigController {
  private static final Logger LOG = LoggerFactory.getLogger(AdminConfigController.class);


  private String returnDataToString(List<AreaDTO> areaList) {
    StringBuffer stringBuffer = new StringBuffer();
    for (AreaDTO area : areaList) {
      stringBuffer.append(",new Object({");
      stringBuffer.append("no:" + "\"" + area.getNo() + "\",");
      stringBuffer.append("name:" + "\"" + area.getName() + "\",");
      stringBuffer.append("parentNo:" + "\"" + area.getParentNo() + "\"");
      stringBuffer.append("})");
    }

    return "[" + stringBuffer.toString().substring(1) + "]";
  }

  //读取地址
  @RequestMapping(params = "method=selectarea")
  public void SelectArea(ModelMap model, HttpServletRequest request, HttpServletResponse response) {
    try {
      IConfigService configService = ServiceManager.getService(IConfigService.class);

      List<AreaDTO> areaList = configService.getChildAreaDTOList(NumberUtil.longValue(request.getParameter("parentNo")));
      PrintWriter out = response.getWriter();
      out.write(returnDataToString(areaList));
      out.flush();
      out.close();
    } catch (Exception e) {
      LOG.debug("method=selectarea");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
    }

  }

  //检测手机号码是否重复
  @RequestMapping(params = "method=checkstoremanagermobile")
  public void checkStoreManagerMobile(ModelMap model, HttpServletRequest request, HttpServletResponse response) {
    IConfigService configService = ServiceManager.getService(IConfigService.class);

    if (request.getParameter("storeManagerMobile") == null || request.getParameter("storeManagerMobile").trim().isEmpty())
      return;

    List<ShopDTO> shopDTOList = configService.getShopByStoreManagerMobile(request.getParameter("storeManagerMobile"));

    try {
      PrintWriter out = response.getWriter();

      if (shopDTOList != null && !shopDTOList.isEmpty()) {
        out.write("new Object({stat:\"false\",count:\"" + shopDTOList.size() + "\"})");
      } else {
        out.write("new Object({stat:\"true\"})");
      }

      out.close();
    } catch (Exception e) {
      LOG.debug("method=checkstoremanagermobile");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
    }

  }

  //检测店面名称是否重复
  @RequestMapping(params = "method=checkshopname")
  public void checkShopName(ModelMap model, HttpServletRequest request, HttpServletResponse response) {
    IConfigService configService = ServiceManager.getService(IConfigService.class);

    if (request.getParameter("name") == null || request.getParameter("name").trim().isEmpty())
      return;

    ShopDTO shopDTO = configService.getShopByName(request.getParameter("name"));

    try {
      PrintWriter out = response.getWriter();
      if (shopDTO != null) {
        out.write("\"false\"");
      } else {
        out.write("\"true\"");
      }

      out.close();
    } catch (Exception e) {
      LOG.debug("method=checkshopname");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
    }

  }

  //检测店面名称是否重复
  @RequestMapping(params = "method=getname")
  public void getName(ModelMap model, HttpServletRequest request, HttpServletResponse response) {
    String shopName = ServiceManager.getService(IConfigService.class).getShopById(Long.parseLong(request.getSession().getAttribute("shopId").toString())).getName();
    String userName = ServiceManager.getService(IUserService.class).getUserByUserId(Long.parseLong(request.getSession().getAttribute("userId").toString())).getName();
    try {
      PrintWriter out = response.getWriter();
      out.write("new Object({shopName:\"" + shopName + "\",userName:\"" + userName + "\"})");
      out.close();
    } catch (Exception e) {
      LOG.debug("method=getname");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
    }
  }

  private static String revision = null;
  public static String getBuildVersion(HttpServletRequest request, HttpServletResponse response) {
    ResourceLoader resourceLoader = new DefaultResourceLoader();
    Resource url = resourceLoader.getResource("version.properties");
    if (revision == null) {
      revision = "1";
      try {
        revision = StringUtil.convertinputStreamToString(url.getInputStream()).trim();

      } catch (Exception e) {
        LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
        LOG.error(e.getMessage(), e);
      }
    }
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if (cookie.getName().equalsIgnoreCase("revision")
            && cookie.getValue().equalsIgnoreCase(revision)) {
          LOG.debug("[cookie] get " + "[revision:" + revision + "]");
          return "";
        }
      }
    }
    Cookie cookie = new Cookie("revision", revision);
    LOG.debug("[cookie] add " + "[revision:" + revision + "]");
    cookie.setMaxAge(60 * 60 * 24 * 365);
    response.addCookie(cookie);
    return revision;
  }

  public static String getBuildVersion() {
    ResourceLoader resourceLoader = new DefaultResourceLoader();
    Resource url = resourceLoader.getResource("version.properties");
    if (revision == null) {
      revision = "";
      try {
        revision = StringUtil.convertinputStreamToString(url.getInputStream()).trim();
        if("${buildNumber}".equals(revision)){
          revision = "";
        }else{
          revision = "-min-"+revision;
        }

      } catch (Exception e) {
        LOG.error(e.getMessage(), e);
      }
    }
    return revision;
  }
}
