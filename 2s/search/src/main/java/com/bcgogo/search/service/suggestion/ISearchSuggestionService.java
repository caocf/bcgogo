package com.bcgogo.search.service.suggestion;

import com.bcgogo.enums.Product.ProductCategoryType;
import com.bcgogo.product.ProductCategory.ProductCategoryDTO;
import com.bcgogo.search.dto.SearchSuggestionDTO;
import com.bcgogo.txn.dto.CarDTO;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: xzhu
 * Date: 12-10-19
 * Time: 上午11:51
 * To change this template use File | Settings | File Templates.
 */
public interface ISearchSuggestionService {
  /**
   * 下拉建议  施工项目
   *
   * @param name
   * @param shopId
   * @return
   * @throws Exception
   */
  public List<SearchSuggestionDTO> getRepairServiceSuggestion(Long shopId, String name) throws Exception;

  /**
   * 下拉建议  经营范围
   *
   * @param searchWord
   * @return
   * @throws Exception
   */
  List<SearchSuggestionDTO> getProductCategorySuggestion(Long shopId,String searchWord,ProductCategoryType productCategoryType,Long parentId) throws Exception;

  List<ProductCategoryDTO> getProductCategoryDetailList(Long shopId,String searchWord, ProductCategoryType... productCategoryTypes) throws Exception;
}
