package com.bcgogo.search.service.order;

import com.bcgogo.search.dto.*;
import org.apache.solr.client.solrj.response.QueryResponse;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: caiweili
 * Date: 8/2/12
 * Time: 2:49 PM
 */
public interface ISearchOrderService {
  public OrderSearchResultListDTO queryOrders(OrderSearchConditionDTO conditions) throws Exception;


  public QueryResponse queryOrderByServiceWorker(Long shopId, String q, Long startTime, Long endIime, int start, int rows) throws Exception;


  public QueryResponse queryOrderByServiceWorker(Long shopId, long orderId) throws Exception;

  public OrderSearchResultListDTO queryOrderItems(OrderSearchConditionDTO conditions) throws Exception;

  public OrderSearchResultListDTO queryOrderItemsByExactCondition(OrderSearchConditionDTO conditions) throws Exception;

  public ProductThroughSearchResultListDTO queryInOutRecords(ProductThroughSearchDTO throughSearchDTO) throws Exception;

  public List<SearchSuggestionDTO> queryOrderItemSuggestion(OrderSearchConditionDTO conditions) throws Exception;

  OrderSearchResultListDTO queryPreBuyRecommend(OrderSearchConditionDTO conditions) throws Exception;
}
