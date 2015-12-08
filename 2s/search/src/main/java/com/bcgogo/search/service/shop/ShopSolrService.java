package com.bcgogo.search.service.shop;

import com.bcgogo.config.cache.ServiceCategoryCache;
import com.bcgogo.config.dto.ServiceCategoryDTO;
import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.search.client.SolrClientHelper;
import com.bcgogo.search.dto.ShopSearchResultListDTO;
import com.bcgogo.search.dto.ShopSolrDTO;
import com.bcgogo.search.dto.ShopSolrSearchConditionDTO;
import com.bcgogo.search.util.SolrQueryUtils;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.RegexUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * User: ZhangJuntao
 * Date: 13-8-6
 * Time: 下午5:53
 */
@Component
public class ShopSolrService implements IShopSolrService {
  private static final Logger LOG = LoggerFactory.getLogger(ShopSolrService.class);

  @Override
  public ShopSearchResultListDTO queryShopSuggestion(ShopSolrSearchConditionDTO condition) throws Exception {
    SolrQuery query = new SolrQuery();
    StringBuilder qString = new StringBuilder();
    boolean started = false;
    //query
    started = generateKeywordsQuery(condition.getKeyword(), qString, started);
    if (condition.getDataKind() != null)
      started = generateStringRelatedQuery("shop_kind", condition.getDataKind().toString(), started, qString);
    if (!ArrayUtils.isEmpty(condition.getShopTypes()))
      started = generateStringArrayRelatedQuery("shop_type", condition.getShopTypes(), started, qString);
    if (StringUtils.isNotBlank(condition.getServiceScopeIds())) {
      started = generateStringArrayRelatedQuery("service_scope_ids", condition.getServiceScopeIds().split(","), started, qString);
    }
    if (condition.getAreaId() != null) {
      generateIntegerRelatedQuery("city_no", condition.getAreaId(), started, qString);
    } else {
      generateIntegerRelatedQuery("city_code", condition.getCityCode(), started, qString);
    }
    query.setQuery(qString.toString());
    query.setRows(condition.getLimit());
    ShopSearchResultListDTO result = new ShopSearchResultListDTO();
    QueryResponse response = SolrClientHelper.geShopSolrClient().query(query);
    SolrDocumentList docs = response.getResults();
    result.setNumFound(docs.getNumFound());
    ShopSolrDTO solrDTO;
    for (SolrDocument doc : docs) {
      solrDTO = new ShopSolrDTO(doc);
      result.getShopSolrDTOList().add(solrDTO);
    }
    return result;
  }

  @Override
  public ShopSearchResultListDTO queryShop(ShopSolrSearchConditionDTO condition) throws Exception {
    ShopSearchResultListDTO sResult = querySpecialShop(condition);
    if (condition.isSpecialQuery()) {
      int sSize = sResult.getShopSolrDTOList().size();
      if (condition.getStart() == 0) {
        //(非洗车 && special is null )||超过10条
        if ((/*!condition.isWashCarServiceShopQuery() &&*/ sResult.getNumFound() != 0) || sSize >= condition.getPageSize()) {
          setServiceScopeName(sResult);
          return sResult;
        }
        condition.setLimit(condition.getLimit() - sSize);
      } else {
        condition.setStart(condition.getStart() - sSize);
      }
    }
    //query normal shop
    ShopSearchResultListDTO result = queryNormalShop(condition);
    //merge result
    if (condition.isSpecialQuery()) {
      if (condition.getStart() == 0) result.getShopSolrDTOList().addAll(0, sResult.getShopSolrDTOList());
      result.setNumFound(sResult.getNumFound() + result.getNumFound());
    }
    setServiceScopeName(result);
    return result;
  }

  private ShopSearchResultListDTO querySpecialShop(ShopSolrSearchConditionDTO condition) throws Exception {
    //query recommend shop
    ShopSearchResultListDTO recommendResult = queryRecommendShop(condition);
    //query other special shop
    if (CollectionUtil.isNotEmpty(condition.getRecommendShopIds()))
      condition.addExcludeShopIds(new HashSet<Long>(condition.getRecommendShopIds()));
    ShopSearchResultListDTO specialResult = queryOtherSpecialShop(condition);

    specialResult.getShopSolrDTOList().addAll(0, recommendResult.getShopSolrDTOList());
    specialResult.setNumFound(specialResult.getNumFound() + recommendResult.getNumFound());
    return specialResult;
  }

  private ShopSearchResultListDTO queryNormalShop(ShopSolrSearchConditionDTO condition) throws Exception {
    SolrQuery query = new SolrQuery();
    StringBuilder qString = new StringBuilder();
    boolean started = false;
    //query
    started = generateKeywordsQuery(condition.getKeyword(), qString, started);
    if (condition.getAreaId() != null) {
      started = generateIntegerRelatedQuery("city_no", condition.getAreaId(), started, qString);
    } else {
      started = generateIntegerRelatedQuery("city_code", condition.getCityCode(), started, qString);
    }
    if (!ArrayUtils.isEmpty(condition.getShopTypes()))
      started = generateStringArrayRelatedQuery("shop_type", condition.getShopTypes(), started, qString);
    if (condition.getDataKind() != null)
      started = generateStringRelatedQuery("shop_kind", condition.getDataKind().toString(), started, qString);
    if (StringUtils.isNotBlank(condition.getServiceScopeIds())) {
      started = generateStringArrayRelatedQuery("service_scope_ids", condition.getServiceScopeIds().split(","), started, qString);
    }
    if (condition.isSpecialQuery())
      generateInListRelatedQuery("-id", condition.getSpecialAndExcludeShopIds(), started, qString);
    condition.setLocationDistance(null);
    query.setQuery(qString.toString());
    query.setStart(condition.getStart());
    query.setRows(condition.getLimit());
    generateGeofiltFilterQuery(condition, query);
    query.set("sort", condition.getSort());
    ShopSearchResultListDTO result = new ShopSearchResultListDTO();
    QueryResponse response = SolrClientHelper.geShopSolrClient().query(query);
    SolrDocumentList docs = response.getResults();
    result.setNumFound(docs.getNumFound());
    ShopSolrDTO solrDTO;
    for (SolrDocument doc : docs) {
      solrDTO = new ShopSolrDTO(doc);
      result.getShopSolrDTOList().add(solrDTO);
    }
    return result;
  }

  private ShopSearchResultListDTO queryRecommendShop(ShopSolrSearchConditionDTO condition) throws Exception {
    SolrQuery query = new SolrQuery();
    StringBuilder qString = new StringBuilder();
    ShopSearchResultListDTO result = new ShopSearchResultListDTO();
    if (!condition.isRecommendShopQuery()) {
      return result;
    }
    boolean notStarted = false;
    if (condition.getDataKind() != null)
      notStarted = generateStringRelatedQuery("shop_kind", condition.getDataKind().toString(), notStarted, qString);
    notStarted = generateInListRelatedQuery("-id", condition.getExcludeShopIds(), notStarted, qString);
    if (!ArrayUtils.isEmpty(condition.getShopTypes()))
      notStarted = generateStringArrayRelatedQuery("shop_type", condition.getShopTypes(), notStarted, qString);
    generateInListRelatedQuery("id", condition.getRecommendShopIds(), notStarted, qString);
    query.setQuery(qString.toString());
    query.set("sort", condition.getSort());
    condition.setLocationDistance(null);
    generateGeofiltFilterQuery(condition, query);
    QueryResponse response = SolrClientHelper.geShopSolrClient().query(query);
    SolrDocumentList docs = response.getResults();
    result.setNumFound(docs.getNumFound());
    ShopSolrDTO solrDTO;
    for (SolrDocument doc : docs) {
      solrDTO = new ShopSolrDTO(doc);
      result.getShopSolrDTOList().add(solrDTO);
    }
    return result;
  }

  public ShopSearchResultListDTO queryOtherSpecialShop(ShopSolrSearchConditionDTO condition) throws Exception {
    SolrQuery query = new SolrQuery();
    StringBuilder qString = new StringBuilder();
    ShopSearchResultListDTO result = new ShopSearchResultListDTO();
    if (!condition.isWashCarServiceShopQuery() && !condition.isLastExpenseShopQuery()) {
      return result;
    }
    boolean notStarted = false;
    if (StringUtils.isNotBlank(condition.getServiceScopeIds()))
      notStarted = generateStringArrayRelatedQuery("service_scope_ids", condition.getServiceScopeIds().split(","), notStarted, qString);
    if (condition.getDataKind() != null)
      notStarted = generateStringRelatedQuery("shop_kind", condition.getDataKind().toString(), notStarted, qString);
    notStarted = generateInListRelatedQuery("-id", condition.getExcludeShopIds(), notStarted, qString);
    if (!ArrayUtils.isEmpty(condition.getShopTypes()))
      notStarted = generateStringArrayRelatedQuery("shop_type", condition.getShopTypes(), notStarted, qString);
    generateOtherSpecialShopQuery(condition, qString, notStarted);
    query.setQuery(qString.toString());
    condition.setLocationDistance(ConfigUtils.getSearchShopLocationDistance());
    generateGeofiltFilterQuery(condition, query);
    QueryResponse response = SolrClientHelper.geShopSolrClient().query(query);
    SolrDocumentList docs = response.getResults();
    result.setNumFound(docs.getNumFound());
    ShopSolrDTO solrDTO;
    for (SolrDocument doc : docs) {
      solrDTO = new ShopSolrDTO(doc);
      result.getShopSolrDTOList().add(solrDTO);
    }
    return result;
  }

  private boolean generateOtherSpecialShopQuery(ShopSolrSearchConditionDTO condition, StringBuilder qString, boolean notStarted) {
    if (condition.getAreaId() != null) {
      generateIntegerRelatedQuery("city_no", condition.getAreaId(), true, qString);
    } else {
      generateIntegerRelatedQuery("city_code", condition.getCityCode(), true, qString);
    }
    if (notStarted) {
      qString.append(" AND ");
    }
    qString.append("(");
    boolean flag = false;
    if (condition.isLastExpenseShopQuery()) {
      qString.append("( id:").append(condition.getLastExpenseShopId());
      qString.append(")");
      notStarted = true;
      flag = true;
    }
    if (condition.isWashCarServiceShopQuery()) {
      int i = 0;
      if (flag) {
        qString.append(" OR ");
      }
      qString.append("((");
      for (Long value : condition.getMemberCardShopIds()) {
        if (value == null) continue;
        if (i != 0) {
          qString.append(" OR ");
        }
        qString.append(" (id:").append(value).append(")^2 ");
        i++;
      }
      qString.append(")");
      qString.append(")^2 ");
      notStarted = true;
    }
    qString.append(")");
    return notStarted;
  }

  private void setServiceScopeName(ShopSearchResultListDTO result) {
    //服务范围
    for (ShopSolrDTO shopSolrDTO : result.getShopSolrDTOList()) {
      if (ArrayUtils.isEmpty(shopSolrDTO.getServiceScopeIds())) continue;
      String[] services = new String[shopSolrDTO.getServiceScopeIds().length];
      ServiceCategoryDTO categoryDTO;
      int i = 0;
      for (String str : shopSolrDTO.getServiceScopeIds()) {
        try {
          categoryDTO = ServiceCategoryCache.getServiceCategoryDTOById(Long.valueOf(str));
          if (categoryDTO != null) services[i++] = categoryDTO.getName();
        } catch (Exception e) {
          LOG.error(e.getMessage(), e);
        }
      }
      shopSolrDTO.setServiceScopes(services);
    }
  }

  private <T> boolean generateInListRelatedQuery(String field, Collection<T> values, boolean started, StringBuilder qString) {
    if (CollectionUtil.isEmpty(values)) return started;
    int i = 0;
    qString.append(" AND ").append(field).append(":(");
    for (T value : values) {
      if (i++ != 0) qString.append(" ");
      qString.append(value);

    }
    started = true;
    qString.append(")");
    return started;
  }

  //AND (field:A OR field:B OR field:C)
  private boolean generateStringArrayRelatedQuery(String field, String[] values, boolean started, StringBuilder qString) {
    if (ArrayUtils.isEmpty(values)) return started;
    int i = 0;
    boolean flag = false; //排除 values{"",""}
    for (String value : values) {
      if (StringUtils.isBlank(value)) continue;
      flag = true;
      if (i > 0) {
        qString.append(" OR ");
      } else if (i == 0 && started) {
        qString.append(" AND (");
      } else {
        qString.append(" ( ");
      }
      value = SolrQueryUtils.escape(value);
      if (!StringUtils.isBlank(value)) {
        qString.append(field).append(":").append("(").append(value).append(")");
        started = true;
        i++;
      }
    }
    if (flag)
      qString.append(")");
    return started;
  }

  private boolean generateStringRelatedQuery(String field, String value, boolean started, StringBuilder qString) {
    if (!StringUtils.isBlank(value)) {
      if (started) {
        qString.append(" AND ");
      }
      qString.append(field).append(":").append("(").append(SolrQueryUtils.escape(value)).append(")");
      started = true;
    }
    return started;
  }

  private boolean generateIntegerRelatedQuery(String field, Integer value, boolean started, StringBuilder qString) {
    if (value != null) {
      if (started) {
        qString.append(" AND ");
      }
      qString.append(field).append(":").append("(").append(value).append(")");
      started = true;
    }
    return started;
  }

  private boolean generateKeywordsQuery(String keyword, StringBuilder qString, boolean started) {
    if (StringUtils.isNotBlank(keyword)) {
      keyword = SolrQueryUtils.escape(keyword);
      if (started) qString.append(" AND ");
      qString
          .append(" (")
          .append("name:").append("(*").append(keyword).append("*)^10");
      if (RegexUtils.isAlpha(keyword)) {
        qString.append(" OR ").append("name_fl:").append("(*").append(keyword).append("*) ");
        qString.append(" OR ").append("name_py:").append("(*").append(keyword).append("*)^3 ");
      }
      qString.append(" OR ").append("address:").append("(*").append(keyword).append("*)^5 ")
          .append(") ")
      ;
      started = true;
    }
    return started;
  }

  //geofilt
  private void generateGeofiltFilterQuery(ShopSolrSearchConditionDTO condition, SolrQuery query) {
    if (condition.getLocationLat() != null && condition.getLocationLon() != null) {
      if (condition.getLocationDistance() != null) {
        query.set("d", String.valueOf(condition.getLocationDistance()));
        query.add("fq", "{!geofilt}");
      }
      query.set("pt", condition.getLocationLat().toString() + "," + condition.getLocationLon().toString());
      query.set("sfield", "shop_location_lat_lon");
      query.set("fl", "distance:geodist(),*");
    }
  }

}
