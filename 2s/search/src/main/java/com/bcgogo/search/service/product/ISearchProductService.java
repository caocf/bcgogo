package com.bcgogo.search.service.product;

import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.search.dto.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: caiweili
 * Date: 8/1/12
 * Time: 2:17 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ISearchProductService {
  /**
   * 库存查询（不知道Field的情况下6字段全部查询）
   *
   * @param searchConditionDTO
   * @return
   * @throws Exception
   */
  public ProductSearchResultListDTO queryProductWithUnknownField(SearchConditionDTO searchConditionDTO) throws Exception;

  @Deprecated
  public List<String> queryProductSuggestionWithSimpleList(SearchConditionDTO searchConditionDTO) throws Exception;

  /**
   * 下拉建议(查询memcache 再查询solr）
   *
   * @param searchConditionDTO
   * @return
   * @throws Exception
   */
  @Deprecated
  public List<SearchSuggestionDTO> getProductSuggestion(SearchConditionDTO searchConditionDTO) throws Exception;

  /**
   * 标准库存查询
   *
   * @param searchConditionDTO
   * @return
   * @throws Exception
   */
  public ProductSearchResultListDTO queryProductWithStdQuery(SearchConditionDTO searchConditionDTO) throws Exception;


  /**
   * @param searchConditionDTO
   * @return
   * @throws Exception
   */
  public ProductSearchSuggestionListDTO queryProductSuggestionWithDetails(SearchConditionDTO searchConditionDTO) throws Exception;


	/**
	 * 根据productLocalInfoId 从solr中查找一组产品信息
	 * @param shopId
	 * @param productLocalInfoId
	 * @return
	 * @throws Exception
	 */
	List<ProductDTO> queryProductByLocalInfoIds(Long shopId,Long ...productLocalInfoId) throws Exception;
  /**
   * 根据ProductId 搜solr 构造成productDTO,Solr search DEMO  仅用于测试
   */
  public ProductDTO getProductDTOFromSolrById(Long shopId,Long productId) throws Exception;

  /**
   * 批发商搜索客户购买过的商品的库存
   * @param searchConditionDTO
   * @return
   * @throws Exception
   */
  ProductSearchResultGroupListDTO queryCustomerShopInventory(SearchConditionDTO searchConditionDTO) throws Exception;

  /**
   *
   * 分页针对该customer shop products
   * @param searchConditionDTO   customer shop id is unique
   * @return
   * @throws Exception
   */
  ProductSearchResultListDTO queryCustomerInventoryByCustomerShopId(SearchConditionDTO searchConditionDTO) throws Exception;

  /**
   * 专用
   * @param searchConditionDTO
   * @return
   * @throws Exception
   */
  ProductSearchResultListDTO queryAccessoryRecommend(boolean isMatchNull,SearchConditionDTO searchConditionDTO) throws Exception;

}
