package com.bcgogo.businessScope;

import com.bcgogo.common.Result;
import com.bcgogo.common.WebUtil;
import com.bcgogo.product.ProductCategory.ProductCategoryDTO;
import com.bcgogo.product.StandardBrandModelCache.StandardBrandModelCache;
import com.bcgogo.product.productCategoryCache.ProductCategoryCache;
import com.bcgogo.product.service.IProductCategoryService;
import com.bcgogo.product.service.IStandardBrandModelService;
import com.bcgogo.product.standardVehicleBrandModel.ShopVehicleBrandModelDTO;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.service.recommend.IPreciseRecommendService;
import com.bcgogo.user.dto.Node;
import com.bcgogo.utils.DateUtil;
import org.slf4j.Logger;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.*;


/**
 * 经营范围相关controller
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 13-6-21
 * Time: 上午10:07
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/businessScope.do")
public class BusinessScopeController {
  private static final Logger LOG = LoggerFactory.getLogger(BusinessScopeController.class);

  @RequestMapping(params = "method=getAllBusinessScope")
  @ResponseBody
  public Object getAllBusinessScope(HttpServletRequest request,String searchWord) {
    Node node = null;
    try {
      if(StringUtils.isNotBlank(searchWord)){
        node = ProductCategoryCache.searchProductCategoryNode(searchWord);
      }else{
        node = ProductCategoryCache.getNode();
      }
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      node = new Node();
    }
    return node;
  }

  @RequestMapping(params = "method=initWeekStat")
  @ResponseBody
  public Object initWeekStat(HttpServletRequest request) {
    Result result = new Result();
    result.setSuccess(false);

    try {
      LOG.info("后台上周每个汽配店铺所关心的配件 汽修版的上周销量、入库量统计");
      LOG.info("开始时间:" + DateUtil.dateLongToStr(System.currentTimeMillis()));
      IPreciseRecommendService service = ServiceManager.getService(IPreciseRecommendService.class);
      service.salesInventoryMonthStat();

    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      result.setSuccess(false);
    } finally {
      LOG.info("后台结束上周销量、入库量统计");
      result.setSuccess(true);
      LOG.info("结束时间:" + DateUtil.dateLongToStr(System.currentTimeMillis()));
    }
    return result;
  }

  /**
   * 根据店铺获取经营范围（第二大类）
   * @param request
   * @return
   */
  @RequestMapping(params = "method=getSecondCategoryByShopId")
  @ResponseBody
  public Object getSecondCategoryByShopId(HttpServletRequest request) {
    Result result = new Result();
    result.setSuccess(false);

    try {
      Long shopId = WebUtil.getShopId(request);
      IPreciseRecommendService service = ServiceManager.getService(IPreciseRecommendService.class);
      Set<Long> shopIdSet = new HashSet<Long>();
      shopIdSet.add(shopId);
      Map<Long, String> businessScopeMap = service.getSecondCategoryByShopId(shopIdSet);
      if (MapUtils.isNotEmpty(businessScopeMap) && StringUtils.isNotEmpty(businessScopeMap.get(shopId))) {
        result.setData(businessScopeMap.get(shopId));
      }
      result.setSuccess(true);
      return result;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      result.setSuccess(false);
      result.setData(null);
    }
    return result;
  }

  /**
   * 经营范围搜索（最大条数30条）
   * @param request
   * @return
   */
  @RequestMapping(params = "method=searchBusinessScope")
  @ResponseBody
  public Object searchBusinessScope(HttpServletRequest request, String name) {
    Map<String, Object> returnMap = new HashMap<String, Object>();
    returnMap.put("uuid", request.getParameter("uuid"));
    try {
      IProductCategoryService productCategoryService = ServiceManager.getService(IProductCategoryService.class);
      List<ProductCategoryDTO> productCategoryDTOList = productCategoryService.getThirdProductCategoryDTOByName(0L, name);
      returnMap.put("data", productCategoryDTOList);
      return returnMap;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
       returnMap.put("data", new ArrayList<ProductCategoryDTO>());
      return returnMap;
    }
  }


  /**
   * 获取所有标准车型、车辆品牌
   * @param request
   * @return
   */
  @RequestMapping(params = "method=getAllStandardVehicleBrandModel")
  @ResponseBody
  public Object getAllStandardVehicleBrandModel(HttpServletRequest request) {
    try {
      return StandardBrandModelCache.getShopVehicleBrandModelDTOList();
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return new ArrayList<ShopVehicleBrandModelDTO>();
    }
  }


}


