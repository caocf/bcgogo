package com.bcgogo.product.service;

import com.bcgogo.common.Pager;
import com.bcgogo.product.ProductCategory.ProdCategorySearchCondition;
import com.bcgogo.product.ProductCategory.ProdCategorySearchResult;
import com.bcgogo.product.ProductCategory.ProductCategoryDTO;
import com.bcgogo.product.dto.ProductCategoryRelationDTO;
import com.bcgogo.product.model.ProductCategory;
import com.bcgogo.product.model.ProductCategoryRelation;
import com.bcgogo.user.dto.Node;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: 商品分类管理接口
 * Date: 12-12-18
 * Time: 下午11:54
 */
public interface IProductCategoryService {
  public Node getProductCategory(Long shopId);

  public ProductCategoryDTO saveOrUpdateProductCategoryDTO(ProductCategoryDTO productCategoryDTO);

  public ProdCategorySearchResult getProductCategoryDTOByCondition(ProdCategorySearchCondition prodCategorySearchCondition);

  public List<ProductCategoryDTO> getProductCategoryDTOByShopId(Long shopId);

  public List<ProductCategoryDTO> getProductCategoryDTOByParentId(Long shopId, Long parentId, Pager pager);

  public ProductCategoryDTO getProductCategoryDTOById(Long shopId, Long id);

  List<ProductCategoryDTO> getProductCategoryDTOByNameParentId(Long shopId, String name, Long parentId);

  List<ProductCategoryDTO> getProductCategoryDTOByName(Long shopId, String name);

  public Map<String, Object> validateProductCategoryDTO(ProductCategoryDTO productCategoryDTO, ProductCategoryDTO newProductCategoryDTO);

  public List<ProductCategoryDTO> getSecondCategoryDTOByParentId(Long shopId, Long parentId);

  /**
   * 获得该店铺的经营范围
   *
   * @param shopId 店铺id
   * @return CheckNode
   */
  Node getBusinessScopeByShopId(Long shopId);


  /**
   * 获得选中的经营范围
   *
   * @param shopId 店铺id
   * @return CheckNode
   */
  Node getCheckedBusinessScope(Long shopId, Set<Long> ids);


  List<ProductCategoryDTO> getProductCategoryDTOByIds(Set<Long> ids);


  public List<ProductCategoryDTO> getThirdProductCategoryDTOByName(Long shopId, String name);

  List<ProductCategoryDTO> fillProductCategoryDTOListInfo(List<ProductCategoryDTO> productCategoryDTOList);

  public List<ProductCategoryRelationDTO> productCategoryRelationDTOQuery(Long shopId, Long... productLocalInfoIds);

  /**
   * key productLocalInfoId  value  productCategoryId
   * @param shopId
   * @param productLocalInfoIds
   * @return
   */
  public Map<Long,Long> getProductCategoryRelationMap(Long shopId, Long... productLocalInfoIds);


  ProductCategoryDTO fillProductCategoryDTOInfo(ProductCategoryDTO productCategoryDTO);

  void saveProductCategoryRelation(Long shopId, Long productCategoryId, Long productLocalInfoId);

  List<ProductCategoryDTO> getRecentlyUsedProductCategoryDTOList(Long shopId,Long userId);

  void saveOrUpdateRecentlyUsedProductCategory(Long shopId,Long userId, Long productCategoryId);

  List<Long> getProductCategoryIdsByShopId(Long shopId, int start, int pageSize);
}
