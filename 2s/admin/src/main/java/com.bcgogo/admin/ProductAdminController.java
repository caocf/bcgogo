package com.bcgogo.admin;

import com.bcgogo.product.service.IProductService;
import com.bcgogo.service.ServiceManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * User: Xiao Jian
 * Date: 12-2-3
 */

@Controller
@RequestMapping("/productadmin.do")
public class ProductAdminController {
  private static final Log LOG = LogFactory.getLog(ProductAdminController.class);

  @RequestMapping(params = "method=productlist")
  public String shopList(HttpServletRequest request) {
    if (request.getSession() == null || request.getSession().getAttribute("shopId") == null) return "/";
    Long shopId = Long.parseLong(request.getSession().getAttribute("shopId").toString());
    if (shopId != 0) return "/";

    int pageNo = 1;
    int pageSize = 10;
    try {
      pageNo = Integer.parseInt(request.getSession().getAttribute("pageNo").toString());
      request.setAttribute("pageNo", pageNo);
      pageSize = Integer.parseInt(request.getSession().getAttribute("pageSize").toString());
      request.setAttribute("pageSize", pageSize);
    } catch (Exception e) {
    }

    IProductService productService = ServiceManager.getService(IProductService.class);
    request.setAttribute("productAdminDTOList", productService.getProductAdminList(pageNo, pageSize));

    return "product/productlist";
  }

}
