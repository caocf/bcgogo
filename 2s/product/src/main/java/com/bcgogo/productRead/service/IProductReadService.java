package com.bcgogo.productRead.service;

import com.bcgogo.common.Pair;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.product.model.ProductCategory;
import com.bcgogo.product.standardVehicleBrandModel.ShopVehicleBrandModelDTO;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 13-6-17
 * Time: 下午1:17
 * To change this template use File | Settings | File Templates.
 */
public interface IProductReadService {

  List<ProductDTO> getProductDTOList(Long shopId, int start, int rows);

  List<ProductDTO> getProductDTOListByProductLocalInfoIds(Long shopId, Set<Long> productLocalInfoIds);


  /**
   * 获取店铺注册的商品 商品信息拿的是最新的商品信息
   * @param shopId
   * @return
   */
  public List<ProductDTO> getShopRegisterProductList(Long shopId);
  /**
   * 根据id获取商品分类（经营范围）
   * @param productCategoryIds
   * @return
   */
  public List<ProductCategory> getCategoryListByIds(List<Long> productCategoryIds);

  public Map<String,String> joinShopVehicleBrandModelStr(Long shopId);

}
