package com.bcgogo.product.StandardBrandModelCache;

import com.bcgogo.common.Pair;
import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.product.service.IStandardBrandModelService;
import com.bcgogo.product.standardVehicleBrandModel.ShopVehicleBrandModelDTO;
import com.bcgogo.search.util.SolrQueryUtils;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.utils.CollectionUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 标准车辆品牌车型cache
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 13-7-24
 * Time: 下午12:05
 * To change this template use File | Settings | File Templates.
 */
public class StandardBrandModelCache {
  private static IStandardBrandModelService standardBrandModelService;
  private static final Logger LOG = LoggerFactory.getLogger(StandardBrandModelCache.class);
  public static List<ShopVehicleBrandModelDTO> vehicleBrandModelDTOList = new ArrayList<ShopVehicleBrandModelDTO>();
  public static Map<Long, ShopVehicleBrandModelDTO> vehicleBrandModelDTOMap = new HashMap<Long, ShopVehicleBrandModelDTO>();

  public static IStandardBrandModelService getStandardBrandModelService() {
    return standardBrandModelService == null ? ServiceManager.getService(IStandardBrandModelService.class) : standardBrandModelService;
  }

  public static ShopVehicleBrandModelDTO getShopVehicleBrandModelDTOByModelId(Long modelId) {
    return getShopVehicleBrandModelDTOMap().get(modelId);
  }

  public static List<String> getVehicleBrandSuggestion(String searchWord, String modelName) {
    List<ShopVehicleBrandModelDTO> vehicleBrandModelDTOList = getShopVehicleBrandModelDTOList();
    List<String> result = new ArrayList<String>();
    if (CollectionUtils.isNotEmpty(vehicleBrandModelDTOList)) {
      for (ShopVehicleBrandModelDTO shopVehicleBrandModelDTO : vehicleBrandModelDTOList) {
        if (StringUtils.isNotBlank(searchWord) && StringUtils.isNotBlank(modelName)) {
          if (shopVehicleBrandModelDTO.getModelName().equals(modelName)
              &&(shopVehicleBrandModelDTO.getBrandName().toLowerCase().contains(searchWord.toLowerCase())
              || shopVehicleBrandModelDTO.getBrandFl().contains(searchWord.toLowerCase())
              || shopVehicleBrandModelDTO.getBrandPy().contains(searchWord.toLowerCase()))) {
            if (!result.contains(shopVehicleBrandModelDTO.getBrandName())) {
              result.add(shopVehicleBrandModelDTO.getBrandName());
            }
          }
        } else if (StringUtils.isNotBlank(searchWord) && StringUtils.isBlank(modelName)) {
          if (shopVehicleBrandModelDTO.getBrandName().toLowerCase().contains(searchWord.toLowerCase())
              || shopVehicleBrandModelDTO.getBrandFl().contains(searchWord.toLowerCase())
              || shopVehicleBrandModelDTO.getBrandPy().contains(searchWord.toLowerCase())) {
            if (!result.contains(shopVehicleBrandModelDTO.getBrandName())) {
              result.add(shopVehicleBrandModelDTO.getBrandName());
            }
          }
        } else if (StringUtils.isBlank(searchWord) && StringUtils.isNotBlank(modelName)) {
          if (shopVehicleBrandModelDTO.getModelName().equals(modelName)) {
            if (!result.contains(shopVehicleBrandModelDTO.getBrandName())) {
              result.add(shopVehicleBrandModelDTO.getBrandName());
            }
          }
        } else if (StringUtils.isBlank(searchWord) && StringUtils.isBlank(modelName)) {
          if (!result.contains(shopVehicleBrandModelDTO.getBrandName())) {
            result.add(shopVehicleBrandModelDTO.getBrandName());
          }
        }
        if (result.size() >= SolrQueryUtils.getSelectOptionNumber()) {
          return result;
        }
      }
    }
    return result;
  }

  public static List<String> getVehicleModelSuggestion(String searchWord, String brandName) {
    List<ShopVehicleBrandModelDTO> vehicleBrandModelDTOList = getShopVehicleBrandModelDTOList();
    List<String> result = new ArrayList<String>();
    if (CollectionUtils.isNotEmpty(vehicleBrandModelDTOList)) {
      for (ShopVehicleBrandModelDTO shopVehicleBrandModelDTO : vehicleBrandModelDTOList) {
        if (StringUtils.isNotBlank(searchWord) && StringUtils.isNotBlank(brandName)) {
          if (shopVehicleBrandModelDTO.getBrandName().equals(brandName)
              && (shopVehicleBrandModelDTO.getModelName().toLowerCase().contains(searchWord.toLowerCase())
              || shopVehicleBrandModelDTO.getModelFl().contains(searchWord.toLowerCase())
              || shopVehicleBrandModelDTO.getModelPy().contains(searchWord.toLowerCase()))) {
            if (!result.contains(shopVehicleBrandModelDTO.getModelName())) {
              result.add(shopVehicleBrandModelDTO.getModelName());
            }
          }
        } else if (StringUtils.isNotBlank(searchWord) && StringUtils.isBlank(brandName)) {
          if (shopVehicleBrandModelDTO.getModelName().toLowerCase().contains(searchWord.toLowerCase())
              || shopVehicleBrandModelDTO.getModelFl().contains(searchWord.toLowerCase())
              || shopVehicleBrandModelDTO.getModelPy().contains(searchWord.toLowerCase())) {
            if (!result.contains(shopVehicleBrandModelDTO.getModelName())) {
              result.add(shopVehicleBrandModelDTO.getModelName());
            }
          }
        } else if (StringUtils.isBlank(searchWord) && StringUtils.isNotBlank(brandName)) {
          if (shopVehicleBrandModelDTO.getBrandName().equals(brandName)) {
            if (!result.contains(shopVehicleBrandModelDTO.getModelName())) {
              result.add(shopVehicleBrandModelDTO.getModelName());
            }
          }
        } else if (StringUtils.isBlank(searchWord) && StringUtils.isBlank(brandName)) {
          if (!result.contains(shopVehicleBrandModelDTO.getModelName())) {
            result.add(shopVehicleBrandModelDTO.getModelName());
          }
        }
        if (result.size() >= SolrQueryUtils.getSelectOptionNumber()) {
          return result;
        }
      }
    }
    return result;
  }

  public static List<ShopVehicleBrandModelDTO> getShopVehicleBrandModelDTOList() {
    if (CollectionUtils.isEmpty(vehicleBrandModelDTOList)) {
      vehicleBrandModelDTOList = getStandardBrandModelService().getAllStandardVehicleBrandModel();
      if (CollectionUtils.isEmpty(vehicleBrandModelDTOList)) {
        LOG.warn("服务分类(VehicleBrandModel)数据为空。");
      }
    }
    return vehicleBrandModelDTOList;
  }

  public static Map<Long, ShopVehicleBrandModelDTO> getShopVehicleBrandModelDTOMap() {
    if(MapUtils.isEmpty(vehicleBrandModelDTOMap)){
      List<ShopVehicleBrandModelDTO> vehicleBrandModelDTOs = getShopVehicleBrandModelDTOList();
      if (CollectionUtil.isNotEmpty(vehicleBrandModelDTOs)) {
        for (ShopVehicleBrandModelDTO vehicleBrandModelDTO : vehicleBrandModelDTOs) {
          vehicleBrandModelDTOMap.put(vehicleBrandModelDTO.getModelId(), vehicleBrandModelDTO);
        }
      }
    }

    return vehicleBrandModelDTOMap;
  }
}
