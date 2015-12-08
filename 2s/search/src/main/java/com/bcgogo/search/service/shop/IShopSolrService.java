package com.bcgogo.search.service.shop;

import com.bcgogo.search.dto.ShopSearchResultListDTO;
import com.bcgogo.search.dto.ShopSolrSearchConditionDTO;

/**
 * User: ZhangJuntao
 * Date: 13-8-6
 * Time: 下午5:52
 */
public interface IShopSolrService {

  ShopSearchResultListDTO queryShopSuggestion(ShopSolrSearchConditionDTO condition) throws Exception;

  ShopSearchResultListDTO queryShop(ShopSolrSearchConditionDTO condition) throws Exception;

}
