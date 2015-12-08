package com.bcgogo.admin.product;

import com.bcgogo.common.Pager;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.util.WebUtil;
import com.bcgogo.product.ProductCategory.NormalProductStatSearchResult;
import com.bcgogo.product.dto.NormalProductDTO;
import com.bcgogo.product.productManage.ProductSearchCondition;
import com.bcgogo.product.service.IProductService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.stat.service.INormalProductStatService;
import com.bcgogo.utils.ArrayUtil;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.StatConstant;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * CRM->店铺财务统计->采购分析专用controller
 * Created by IntelliJ IDEA.
 * User: liuWei
 * Date: 12-12-29
 * Time: 下午3:59
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/normalProductStat.do")
public class NormalProductStatController {

  private static final Logger LOG = LoggerFactory.getLogger(NormalProductStatController.class);


  /**
   * 根据前台查询条件 获得标准商品店铺采购统计
   * @param request
   * @param response
   * @param productSearchCondition
   * @return
   */
  @RequestMapping(params = "method=getNormalProductStatByCondition")
  @ResponseBody
  public Object getNormalProductStatByCondition(HttpServletRequest request, HttpServletResponse response, ProductSearchCondition productSearchCondition) {

    try {
      Long shopId = WebUtil.getShopId(request);
      if (shopId == null) throw new Exception("shopId is null!");
      if(productSearchCondition == null || productSearchCondition.getNormalProductStatType() == null){
        return null;
      }

      List<Long> shopIdList = null;
      if(StringUtil.isNotEmpty(productSearchCondition.getShopVersion()) || productSearchCondition.getProvinceId() != null || productSearchCondition.getCityId() != null ||
          productSearchCondition.getRegionId() != null) {
        IConfigService configService = ServiceManager.getService(IConfigService.class);


        List<Long> shopVersionIds = new ArrayList<Long>();

        if ("REPAIR_VERSION".equals(productSearchCondition.getShopVersion())) {
          shopVersionIds.add(10000010017531654L);
          shopVersionIds.add(10000010017531655L);
          shopVersionIds.add(10000010039823882L);
        } else if ("WHOLESALER_VERSION".equals(productSearchCondition.getShopVersion())) {
          shopVersionIds.add(10000010017531657L);
          shopVersionIds.add(10000010037193619L);
          shopVersionIds.add(10000010037193620L);
          shopVersionIds.add(10000010017531653L);
        }

        shopIdList = configService.getShopByShopVersionAndArea(shopVersionIds.toArray(new Long[shopVersionIds.size()]), productSearchCondition.getProvinceId(), productSearchCondition.getCityId(),
            productSearchCondition.getRegionId());
        if (CollectionUtils.isEmpty(shopIdList)) {
          return null;
        }
      }else{
        shopIdList = new ArrayList<Long>();
        shopIdList.add(StatConstant.EMPTY_SHOP_ID);
      }


      productSearchCondition.setShopId(shopId);
      IProductService productService = ServiceManager.getService(IProductService.class);
      INormalProductStatService normalProductStatService = ServiceManager.getService(INormalProductStatService.class);

      Map<Long,NormalProductDTO> normalProductDTOMap = productService.getSimpleNormalProductDTO(productSearchCondition);

      if (MapUtils.isEmpty(normalProductDTOMap)) {
        return null;
      }
      Long[] normalProductIds = normalProductDTOMap.keySet().toArray(new Long[normalProductDTOMap.keySet().size()]);

      Pager pager = new Pager();
      pager.setRowStart(productSearchCondition.getStart());
      pager.setPageSize(productSearchCondition.getLimit());

      NormalProductStatSearchResult normalProductStatSearchResult = normalProductStatService.getStatDateByCondition(shopIdList.toArray(new Long[shopIdList.size()]) , normalProductIds, productSearchCondition.getNormalProductStatType(),pager,normalProductDTOMap);

      return normalProductStatSearchResult;
    } catch (Exception e) {
      LOG.error("normalProductStat.do method=getNormalProductStatByCondition");
      LOG.error(productSearchCondition.toString());
      LOG.error(e.getMessage(),e);
    }
    return null;
  }

  /**
   * 根据前台查询条件获得某个具体的标准产品各个店铺的采购统计
   * @param request
   * @param response
   * @param productSearchCondition
   * @return
   */
  @RequestMapping(params = "method=getNormalProductStatDetailByNormalProductId")
  @ResponseBody
  public Object getNormalProductStatDetailByNormalProductId(HttpServletRequest request, HttpServletResponse response,  ProductSearchCondition productSearchCondition) {

    try {
      Long shopId = WebUtil.getShopId(request);
      if (shopId == null) throw new Exception("shopId is null!");
      productSearchCondition.setShopId(shopId);
      if (productSearchCondition == null || productSearchCondition.getNormalProductStatType() == null || !NumberUtil.isNumber(productSearchCondition.getNormalProductIdStr())) {
        return null;
      }
      INormalProductStatService normalProductStatService = ServiceManager.getService(INormalProductStatService.class);
      Pager pager = new Pager();
      pager.setRowStart(productSearchCondition.getStart());
      pager.setPageSize(productSearchCondition.getLimit());

      List<Long> shopIdList = null;
      if (StringUtil.isNotEmpty(productSearchCondition.getShopVersion()) || productSearchCondition.getProvinceId() != null || productSearchCondition.getCityId() != null ||
          productSearchCondition.getRegionId() != null) {
        IConfigService configService = ServiceManager.getService(IConfigService.class);

        List<Long> shopVersionIds = new ArrayList<Long>();

        if ("REPAIR_VERSION".equals(productSearchCondition.getShopVersion())) {
          shopVersionIds.add(10000010017531654L);
          shopVersionIds.add(10000010017531655L);
          shopVersionIds.add(10000010039823882L);
        } else if ("WHOLESALER_VERSION".equals(productSearchCondition.getShopVersion())) {
          shopVersionIds.add(10000010017531657L);
          shopVersionIds.add(10000010037193619L);
          shopVersionIds.add(10000010037193620L);
          shopVersionIds.add(10000010017531653L);
        }

        shopIdList = configService.getShopByShopVersionAndArea(shopVersionIds.toArray(new Long[shopVersionIds.size()]), productSearchCondition.getProvinceId(), productSearchCondition.getCityId(),
            productSearchCondition.getRegionId());
        if (CollectionUtils.isEmpty(shopIdList)) {
          return null;
        }
      } else {
        shopIdList = new ArrayList<Long>();
      }

      NormalProductStatSearchResult normalProductStatSearchResult = normalProductStatService.getStatDetailByCondition(shopIdList.toArray(new Long[shopIdList.size()]), Long.valueOf(productSearchCondition.getNormalProductIdStr()), productSearchCondition.getNormalProductStatType(), pager);
      return normalProductStatSearchResult;
    } catch (Exception e) {
      LOG.error("normalProductStat.do method=getNormalProductStatByCondition");
      LOG.error("normalProductIdStr" + productSearchCondition.getNormalProductIdStr());
      LOG.error(e.getMessage(), e);
    }
    return null;
  }

}
