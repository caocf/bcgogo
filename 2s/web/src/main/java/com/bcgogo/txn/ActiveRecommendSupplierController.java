package com.bcgogo.txn;

import com.bcgogo.common.WebUtil;
import com.bcgogo.enums.Product.RecommendSupplierType;
import com.bcgogo.txn.service.IActiveRecommendSupplierService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * User: ZhangJuntao
 * Date: 13-5-18
 * Time: 上午10:41
 */
@Controller
@RequestMapping("/activeRecommendSupplier.do")
public class ActiveRecommendSupplierController extends AbstractTxnController {
  private static final Logger LOG = LoggerFactory.getLogger(ActiveRecommendSupplierController.class);

  /**
   * 获得主动推荐
   *
   * @param model     ModelMap
   * @param request   HttpServletRequest
   * @param productId long
   */
  @RequestMapping(params = "method=obtainActiveRecommendSupplierByProductId")
  @ResponseBody
  public Object obtainActiveRecommendSupplierByProductId(ModelMap model, HttpServletRequest request, Long productId, RecommendSupplierType type, Double comparePrice,String orderType) {
    try {
      IActiveRecommendSupplierService activeRecommendSupplierService = com.bcgogo.service.ServiceManager.getService(IActiveRecommendSupplierService.class);
      return activeRecommendSupplierService.obtainActiveRecommendSupplierByProductId(productId, WebUtil.getShopVersionId(request), WebUtil.getShopId(request),comparePrice,"REPAIR".equals(orderType));
    } catch (Exception e) {
      LOG.debug("/activeRecommendSupplier.do");
      LOG.debug("method=obtainActiveRecommendSupplierByProductId");
      LOG.error(e.getMessage(), e);
      return null;
    }
  }

}
