package com.bcgogo.admin.config.shop;

import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.product.service.IProductCategoryService;
import com.bcgogo.product.service.IProductService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.service.ITxnService;
import com.bcgogo.user.dto.Node;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.NumberUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.Set;


/**
 * 经营范围相关controller
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 13-6-21
 * Time: 上午10:07
 */
@Controller
@RequestMapping("/businessScope.do")
public class BusinessScopeController {
  private static final Logger LOG = LoggerFactory.getLogger(BusinessScopeController.class);


  /**
   * 获得该店铺的经营范围
   *
   * @param shopId 店铺id
   * @return CheckNode
   */
  @RequestMapping(params = "method=getBusinessScopeByShopId")
  @ResponseBody
  public Object getBusinessScopeByShopId(HttpServletRequest request, Long shopId) {
    Node node = null;
    try {
      node = ServiceManager.getService(IProductCategoryService.class).getBusinessScopeByShopId(shopId);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      node = new Node();
    }
    return node;
  }

  /**
   * 获得选中的经营范围
   *
   * @return CheckNode
   */
  @RequestMapping(params = "method=getCheckedBusinessScope")
  @ResponseBody
  public Object getCheckedBusinessScope(HttpServletRequest request, Long shopId) {
    Node node = null;
    try {
      Set<Long> ids = new HashSet<Long>(NumberUtil.parseLongValues(request.getParameter("ids")));
      if (CollectionUtil.isEmpty(ids)) return this.getBusinessScopeByShopId(request, shopId);
      node = ServiceManager.getService(IProductCategoryService.class).getCheckedBusinessScope(shopId, ids);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      node = new Node();
    }
    return node;
  }

  @RequestMapping(params = "method=getShopProductList")
  @ResponseBody
  public Object getShopProductList(HttpServletRequest request, ShopDTO dto) {
    Node node = null;
    try {
      dto.prepareForSaveProduct();
      ServiceManager.getService(ITxnService.class) .batchSaveProductWithReindex(dto.getId(), null, dto.getProductDTOs());
      ServiceManager.getService(IProductService.class) .saveShopRegisterProduct(dto.getProductDTOs());
      ServiceManager.getService(IConfigService.class).saveOrUpdateUnitSort(dto.getId(),dto.getProductDTOs());
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      node = new Node();
    }
    return node;
  }


  /**
   * 获得该店铺的主营车型
   *
   * @param shopId 店铺id
   * @return CheckNode
   */
  @RequestMapping(params = "method=getShopVehicleBrandModelByShopId")
  @ResponseBody
  public Object getShopVehicleBrandModelByShopId(HttpServletRequest request, Long shopId) {
    Node node = null;
    try {
      node = ServiceManager.getService(IProductService.class).getVehicleBrandModelByShopId(shopId);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      node = new Node();
    }
    return node;
  }

  /**
   * 获得选中的主营车型
   *
   * @return CheckNode
   */
  @RequestMapping(params = "method=getCheckedVehicleBrandModel")
  @ResponseBody
  public Object getCheckedVehicleBrandModel(HttpServletRequest request, Long shopId) {
    Node node = null;
    try {
      Set<Long> ids = new HashSet<Long>(NumberUtil.parseLongValues(request.getParameter("ids")));
      if (CollectionUtil.isEmpty(ids)) return this.getShopVehicleBrandModelByShopId(request, shopId);
      node = ServiceManager.getService(IProductService.class).getCheckedVehicleBrandModel(ids);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      node = new Node();
    }
    return node;
  }
}


