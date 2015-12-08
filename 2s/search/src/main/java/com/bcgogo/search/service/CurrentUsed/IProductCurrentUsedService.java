package com.bcgogo.search.service.CurrentUsed;

import com.bcgogo.common.Pair;
import com.bcgogo.search.dto.CurrentUsedProductDTO;
import com.bcgogo.search.dto.SearchConditionDTO;
import com.bcgogo.search.dto.SearchMemoryConditionDTO;
import com.bcgogo.search.dto.SearchSuggestionDTO;
import com.bcgogo.search.model.CurrentUsedProduct;
import com.bcgogo.txn.dto.BcgogoOrderDto;

import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-5-23
 * Time: 下午1:57
 */
public interface IProductCurrentUsedService {
  //从内存或数据库中取常用商品
  public List<CurrentUsedProduct> getCurrentUsedProductsFromMemory(SearchMemoryConditionDTO searchMemoryConditionDTO);

  public void currentUsedProductSaved(BcgogoOrderDto bcgogoOrderDto);

  public void currentUsedProductSaved(Long shopId, List<CurrentUsedProductDTO> currentUsedProductDTOList);

  //更新数据库常用商品
  public void currentUsedProductDBSaved(Long shopId, List<CurrentUsedProductDTO> currentUsedProductDTOList);

  //更新内存常用商品
  public void currentUsedProductMemorySaved(Long shopId, List<CurrentUsedProductDTO> currentUsedProductDTOList);

  @Deprecated
  public void saveRecentChangedProductInMemory(BcgogoOrderDto bcgogoOrderDto) throws Exception;

  @Deprecated
  public Map<Long, Pair<Long, Boolean>> getRecentChangedProductFromMemory(Long shopId);

  @Deprecated
	public Map<Long, Long> getRecentDeletedProductFromMemory(Long shopId);

	/**
	 * 根据传过来的newRecentChangedProductMap 再去solr里检查是否有该数据，标识数据为新增true，还是修改false
	 * @param shopId
	 * @param newRecentChangedProductMap
	 * @throws Exception
	 */
  @Deprecated
  public void saveRecentChangedProductInMemory(Long shopId, Map<Long, Pair<Long, Boolean>> newRecentChangedProductMap) throws Exception;

  @Deprecated
	public void saveRecentDeletedProductInMemory(Long shopId, Map<Long, Long> newRecentDeletedProductMap) throws Exception;

  List<SearchSuggestionDTO> getProductSuggestionFromMemory(SearchMemoryConditionDTO searchMemoryConditionDTO) throws Exception;
}
