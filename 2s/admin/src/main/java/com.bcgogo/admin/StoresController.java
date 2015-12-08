package com.bcgogo.admin;

import com.bcgogo.product.service.IProductSolrService;
import com.bcgogo.service.ServiceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * Created by IntelliJ IDEA.
 * User: WWW
 * Date: 12-1-29
 * Time: 上午9:53
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/stores.do")
public class StoresController {
  private static final Logger LOG = LoggerFactory.getLogger(StoresController.class);

  @RequestMapping(params = "method=getStoresInfo")
  public String getStoresInfo() {
    return "/pages/storesManage";
  }

  @RequestMapping(params = "method=reindexProductForSolr")
  public void reindexProductForSolr(ModelMap model, HttpServletRequest request, HttpServletResponse response) {
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    String jsonStr = "";
    try {
      IProductSolrService productSolrService = ServiceManager.getService(IProductSolrService.class);
      productSolrService.reindexProductForSolr(shopId);
      jsonStr = "{\"isSuccess\":\"Y\"}";
    } catch (Exception e) {
      jsonStr = "{\"isSuccess\":\"N\"}";
    }
    PrintWriter writer = null;
    try {
      writer = response.getWriter();
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
    writer.write(jsonStr);
    writer.close();
  }
}
