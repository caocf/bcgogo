package com.bcgogo.product.cache;

import com.bcgogo.product.dto.ProductUnitDTO;
import com.bcgogo.product.service.IProductService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商品 单位Cache
 */
public class ProductUnitCache {

  private static final Logger LOG = LoggerFactory.getLogger(ProductUnitCache.class);


  private static Map<String, String> productUnitMap = new HashMap<String, String>();

  public static String getUnitByProductName(String productName) {
    if (MapUtils.isEmpty(productUnitMap)) {
      IProductService productService = ServiceManager.getService(IProductService.class);
      List<ProductUnitDTO> productUnitDTOList = productService.getAllProductUnitDTOList();
      if (CollectionUtils.isEmpty(productUnitDTOList)) {
        LOG.error("getAllProductUnitDTOList productUnitDTOList is empty");
      } else {
        for (ProductUnitDTO productUnitDTO : productUnitDTOList) {
          productUnitMap.put(productUnitDTO.getProductName(), productUnitDTO.getUnit());
        }
      }
    }
    return productUnitMap.get(StringUtil.formateStr(productName));
  }
  public static Map<String, String> getProductUnitMap() {
    if (MapUtils.isEmpty(productUnitMap)) {
      IProductService productService = ServiceManager.getService(IProductService.class);
      List<ProductUnitDTO> productUnitDTOList = productService.getAllProductUnitDTOList();
      if (CollectionUtils.isEmpty(productUnitDTOList)) {
        LOG.error("getAllProductUnitDTOList productUnitDTOList is empty");
      } else {
        for (ProductUnitDTO productUnitDTO : productUnitDTOList) {
          productUnitMap.put(productUnitDTO.getProductName(), productUnitDTO.getUnit());
        }
      }
    }
    return new HashMap<String, String>(productUnitMap);
  }
  public static void refreshProductUnitCache() {
    IProductService productService = ServiceManager.getService(IProductService.class);
    List<ProductUnitDTO> productUnitDTOList = productService.getAllProductUnitDTOList();
    if (CollectionUtils.isEmpty(productUnitDTOList)) {
      LOG.error("getAllProductUnitDTOList productUnitDTOList is empty");
    } else {
      productUnitMap.clear();
      for (ProductUnitDTO productUnitDTO : productUnitDTOList) {
        productUnitMap.put(productUnitDTO.getProductName(), productUnitDTO.getUnit());
      }
    }
  }
}
