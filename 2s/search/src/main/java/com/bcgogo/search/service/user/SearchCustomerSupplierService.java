package com.bcgogo.search.service.user;

import com.bcgogo.enums.RelationTypes;
import com.bcgogo.enums.notification.ContactGroupType;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.product.dto.PingyinInfo;
import com.bcgogo.search.client.SolrClientHelper;
import com.bcgogo.search.dto.*;
import com.bcgogo.search.util.SolrQueryUtils;
import com.bcgogo.user.dto.ContactDTO;
import com.bcgogo.utils.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.FieldStatsInfo;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.StatsParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.util.*;

/**
 * User: ZhangJuntao
 * Date: 12-8-28
 * Time: 下午11:37
 * supplier 与 customer 搜索service
 */
@Component
public class SearchCustomerSupplierService implements ISearchCustomerSupplierService {
  private static final Logger LOG = LoggerFactory.getLogger(SearchCustomerSupplierService.class);
  @Override
  public CustomerSupplierSearchResultListDTO queryContact(CustomerSupplierSearchConditionDTO searchConditionDTO) throws Exception {
    if (searchConditionDTO == null) throw new Exception("CustomerSupplierSearchConditionDTO is null.");
    if (searchConditionDTO.getShopId() == null) throw new Exception("CustomerSupplierSearchConditionDTO shopId is null.");
    String searchWord = searchConditionDTO.getSearchWord();
    StringBuilder qString = new StringBuilder();
    boolean started = false;
    if (StringUtils.isNotBlank(searchWord)) {
      generateMultiFieldSuggestionQuery(qString, searchWord, searchConditionDTO.getSearchFieldStrategies());
      started = true;
    }
    if (searchConditionDTO.getContactGroupType()!=null) {
      started = generateStringRelatedQuery("contact_group_type", searchConditionDTO.getContactGroupType().toString(), started, qString);
    }
     if (searchConditionDTO.getIds()!=null) {
      started = generateStringRelatedQuery("id", searchConditionDTO.getIds(), started, qString);
    }
    SolrQuery query = new SolrQuery();
    if (qString.length() == 0) {
      qString.append("*:*");
    }
    query.setQuery(qString.toString());
    StringBuilder fQueryString = new StringBuilder();
    fQueryString.append("shop_id:").append(searchConditionDTO.getShopId());
    fQueryString.append(" AND doc_type:").append(SolrClientHelper.BcgogoSolrDocumentType.CONTACT.getValue());
    if (ArrayUtils.contains(searchConditionDTO.getSearchStrategies(),CustomerSupplierSearchConditionDTO.SearchStrategy.mobileNotEmpty)) {
      fQueryString.append(" AND mobile:[\"\" TO *]");
    }
    query.setFilterQueries(fQueryString.toString());

    if (searchConditionDTO.getRows() <= 0)
      throw new BcgogoException("query customer and supplier rows is illegal!");
    query.setStart(searchConditionDTO.getStart());
    query.setRows(searchConditionDTO.getRows());

    query.setParam("fl", "*,score");
    if (StringUtils.isNotBlank(searchConditionDTO.getSort())) {
      query.setParam("sort", searchConditionDTO.getSort());
    }
    QueryResponse rsp = SolrClientHelper.getCustomerSupplierSolrClient().query(query);
    SolrDocumentList docs = rsp.getResults();
    CustomerSupplierSearchResultListDTO searchResultListDTO = new CustomerSupplierSearchResultListDTO();
    searchResultListDTO.setNumFound(docs.getNumFound());
    ContactDTO contactDTO = null;
    if (CollectionUtils.isNotEmpty(docs)) {
      for (SolrDocument doc : docs) {
        String id = (String) doc.getFirstValue("id");
        String name = (String) doc.getFirstValue("name");
        Collection<Object> contacts = doc.getFieldValues("contact");
        Collection<Object> mobiles = doc.getFieldValues("mobile");
        Collection<Object> contactGroupTypes = doc.getFieldValues("contact_group_type");

        contactDTO = new ContactDTO();
        contactDTO.setSpecialIdStr(id);
        contactDTO.setCustomerOrSupplierName(name);
        contactDTO.setName(CollectionUtil.getFirst(contacts)==null?null:CollectionUtil.getFirst(contacts).toString());
        contactDTO.setMobile(CollectionUtil.getFirst(mobiles)==null?null:CollectionUtil.getFirst(mobiles).toString());
        if(CollectionUtils.isNotEmpty(contactGroupTypes)){
          List<ContactGroupType> contactGroupTypeList = new ArrayList<ContactGroupType>();
          for(Object o:contactGroupTypes){
            contactGroupTypeList.add(ContactGroupType.valueOf(o.toString()));
          }
          contactDTO.setContactGroupTypeList(contactGroupTypeList);
        }
        searchResultListDTO.getContactDTOList().add(contactDTO);
      }
    }
    return searchResultListDTO;
  }

  @Override
  public List<SearchSuggestionDTO> queryContactSuggestion(CustomerSupplierSearchConditionDTO searchConditionDTO) throws Exception {
    if (searchConditionDTO == null) throw new Exception("CustomerSupplierSearchConditionDTO is null.");
    if (searchConditionDTO.getShopId() == null) throw new Exception("CustomerSupplierSearchConditionDTO shopId is null.");
    String searchField = searchConditionDTO.getSearchField();
    String searchWord = searchConditionDTO.getSearchWord();
    StringBuilder qString = new StringBuilder();
    boolean started = false;
    if (StringUtils.isNotBlank(searchWord)) {
      generateMultiFieldSuggestionQuery(qString, searchWord, searchConditionDTO.getSearchFieldStrategies());
      started = true;
    }

    SolrQuery query = new SolrQuery();
    if (qString.length() == 0) {
      qString.append("*:*");
    }
    query.setQuery(qString.toString());
    StringBuilder fQueryString = new StringBuilder();
    fQueryString.append("shop_id:").append(searchConditionDTO.getShopId());
    fQueryString.append(" AND doc_type:").append(SolrClientHelper.BcgogoSolrDocumentType.CONTACT.getValue());
    if (ArrayUtils.contains(searchConditionDTO.getSearchStrategies(),CustomerSupplierSearchConditionDTO.SearchStrategy.mobileNotEmpty)) {
      fQueryString.append(" AND mobile:[\"\" TO *]");
    }
    query.setFilterQueries(fQueryString.toString());

    if (searchConditionDTO.getRows() <= 0)
      throw new BcgogoException("query customer and supplier rows is illegal!");
    query.setRows(searchConditionDTO.getRows());

    query.setParam("fl", "*,score");
    if (StringUtils.isNotBlank(searchConditionDTO.getSort())) {
      query.setParam("sort", searchConditionDTO.getSort());
    }
    QueryResponse rsp = SolrClientHelper.getCustomerSupplierSolrClient().query(query);
    List<SearchSuggestionDTO> result = processQueryContactResult(rsp.getResults(),searchWord, searchField);
    return result;
  }
  private List<SearchSuggestionDTO> processQueryContactResult(SolrDocumentList documents,String searchWord, String searchField) {
    List<SearchSuggestionDTO> results = new ArrayList<SearchSuggestionDTO>();
    if (CollectionUtils.isEmpty(documents)) return results;
    if (CustomerSupplierSearchConditionDTO.MULTI_FIELD_TO_SINGLE.equals(searchField)) {
      for (SolrDocument document : documents) {
        String name = (String) document.getFirstValue("name");
        String nameFl = (String) document.getFirstValue("name_fl");
        String namePy = (String) document.getFirstValue("name_py");
        if (StringUtil.isContains(searchWord, name)
            || StringUtil.isPrefixOfWord(searchWord, nameFl)
            || StringUtil.isPrefixOfWord(searchWord, namePy)) {
          SearchSuggestionDTO result = new SearchSuggestionDTO();
          result.addEntry("keyWord", name);
          results.add(result);
        }
        Collection<Object> contacts = document.getFieldValues("contact");
        String contact = CollectionUtil.getFirst(contacts)==null?null:CollectionUtil.getFirst(contacts).toString();
        Collection<Object> contactFls = document.getFieldValues("contact_fl");
        String contactFl = CollectionUtil.getFirst(contactFls)==null?null:CollectionUtil.getFirst(contactFls).toString();
        Collection<Object> contactPys = document.getFieldValues("contact_py");
        String contactPy = CollectionUtil.getFirst(contactPys)==null?null:CollectionUtil.getFirst(contactPys).toString();

        if (StringUtil.isContains(searchWord, contact)
            || StringUtil.isPrefixOfWord(searchWord, contactFl)
            || StringUtil.isPrefixOfWord(searchWord, contactPy)) {
          SearchSuggestionDTO result = new SearchSuggestionDTO();
          result.addEntry("keyWord", contact);
          results.add(result);
        }
        Collection<Object> mobiles = document.getFieldValues("mobile");
        String mobile = CollectionUtil.getFirst(mobiles)==null?null:CollectionUtil.getFirst(mobiles).toString();
        if (StringUtil.isContains(searchWord, mobile)) {
          SearchSuggestionDTO result = new SearchSuggestionDTO();
          result.addEntry("keyWord", mobile);
          results.add(result);
        }
      }
      return results;
    }else{
      for (SolrDocument document : documents) {
        String id = (String) document.getFirstValue("id");
        String name = (String) document.getFirstValue("name");
        Collection<Object> contacts = document.getFieldValues("contact");
        Collection<Object> mobiles = document.getFieldValues("mobile");
        SearchSuggestionDTO result = new SearchSuggestionDTO();
        result.addEntry("id", id);
        result.addEntry("name", name);
        result.addEntry("contact", CollectionUtil.getFirst(contacts)==null?null:CollectionUtil.getFirst(contacts).toString());
        result.addEntry("mobile", CollectionUtil.getFirst(mobiles)==null?null:CollectionUtil.getFirst(mobiles).toString());
        results.add(result);
      }
      return results;
    }
  }

  @Override
  public CustomerSupplierSearchResultListDTO queryCustomerWithUnknownField(CustomerSupplierSearchConditionDTO searchConditionDTO) throws Exception {
    CustomerSupplierSearchResultListDTO searchResultListDTO = new CustomerSupplierSearchResultListDTO();
    SolrQuery query = queryCustomerWithUnknownFieldResponse(searchConditionDTO);

    if(searchConditionDTO.getJoinSearchConditionDTO()!=null){
      generateFilterJoinOrderItemQuery(searchConditionDTO.getJoinSearchConditionDTO(),query);
    }

    if (StringUtils.isNotBlank(searchConditionDTO.getSort())) {
      query.setParam("sort", searchConditionDTO.getSort());
    }
//    query.setParam("debugQuery", "true");
    query.setParam("fl", "*,score");
    query.setStart(searchConditionDTO.getStart());
    query.setRows(searchConditionDTO.getRows());
    query.setFacet(true);
    query.setParam(StatsParams.STATS, "true");
    query.setParam(StatsParams.STATS_FIELD, "total_debt","total_deposit","total_amount","total_return_debt", "total_balance");
    query.setParam(StatsParams.STATS_FACET, "member_type");

    QueryResponse response = SolrClientHelper.getCustomerSupplierSolrClient().query(query);
    FieldStatsInfo statsInfo = response.getFieldStatsInfo().get("total_debt");
    if (statsInfo != null) {
      searchResultListDTO.setTotalDebt(NumberUtil.round((Double)statsInfo.getSum(), 1));
      Map<String, Long> counts = new HashMap<String, Long>();
      Map<String, List<FieldStatsInfo>> statsInfoMap = statsInfo.getFacets();
      List<FieldStatsInfo> statsInfoForMemberType = statsInfoMap.get("member_type");
      for (FieldStatsInfo stats : statsInfoForMemberType) {
        String name = stats.getName();
        long count = stats.getCount();
        counts.put(name, count);
      }
      searchResultListDTO.setCounts(counts);
      searchResultListDTO = getMemberNumFound(searchResultListDTO);
      searchResultListDTO = getHasMobileNumFound(searchResultListDTO, query);
      searchResultListDTO = getHasObdNumFound(searchResultListDTO,searchConditionDTO, query);
      searchResultListDTO = getHasAppNumFound(searchResultListDTO,searchConditionDTO, query);
      searchResultListDTO = getTotalReceivableNumFound(searchResultListDTO,query);
    }

    statsInfo = response.getFieldStatsInfo().get("total_deposit");
    if (statsInfo != null) {
      searchResultListDTO.setTotalDeposit((Double)statsInfo.getSum());
    }

    statsInfo = response.getFieldStatsInfo().get("total_amount");
    if (statsInfo != null) {
      searchResultListDTO.setTotalConsumption((Double) statsInfo.getSum());
    }

    statsInfo = response.getFieldStatsInfo().get("total_return_debt");
    if (statsInfo != null) {
      searchResultListDTO.setTotalReturnDebt((Double) statsInfo.getSum());
    }

    statsInfo = response.getFieldStatsInfo().get("total_balance");
    if (statsInfo != null) {
      searchResultListDTO.setTotalBalance((Double) statsInfo.getSum());
    }

    getTodayNewCustomerNumFound(searchResultListDTO, query);
    getCustomerOrSupplierTypeNumStat(searchResultListDTO,query);

    SolrDocumentList docs = response.getResults();
    searchResultListDTO.setNumFound(docs.getNumFound());
    CustomerSupplierSearchResultDTO searchResultDTO = null;
    if (CollectionUtils.isNotEmpty(docs)) {
      for (SolrDocument doc : docs) {
        searchResultDTO = new CustomerSupplierSearchResultDTO(doc);
        searchResultListDTO.getCustomerSuppliers().add(searchResultDTO);
      }
    }
    return searchResultListDTO;
  }

  public CustomerSupplierSearchResultListDTO querySupplierWithUnknownField(CustomerSupplierSearchConditionDTO searchConditionDTO) throws Exception {
    SolrQuery query = new SolrQuery();
    if (searchConditionDTO == null) throw new Exception("CustomerSupplierSearchConditionDTO is null.");
    CustomerSupplierSearchResultListDTO searchResultListDTO = new CustomerSupplierSearchResultListDTO();
    String searchWord = searchConditionDTO.getSearchWord();
    StringBuilder qString = new StringBuilder();
    boolean started = false;
    if (StringUtils.isNotBlank(searchWord)) {
      boolean isFirst = true;
      for (String sw : searchWord.split(" ")) {
        if(StringUtils.isNotBlank(sw)){
          if (isFirst) {
            qString.append("(");
            isFirst = false;
          } else {
            qString.append(" AND (");
          }
          sw = SolrQueryUtils.escape(sw);
          qString.append(" name").append(":").append("(").append(sw).append(")^100");
          qString.append(" OR name").append(":").append("(").append(sw).append("*").append(")^60");
          qString.append(" OR name_ngram").append(":").append("(").append(sw).append(")^40");

          qString.append(" OR contact").append(":").append("(").append(sw).append(")^20");
          qString.append(" OR contact").append(":").append("(").append(sw).append("*").append(")^15");
          qString.append(" OR contact_ngram").append(":").append("(").append(sw).append(")^10");

          qString.append(" OR mobile").append(":").append("(").append(sw).append(")^20");
          qString.append(" OR mobile").append(":").append("(").append(sw).append("*").append(")^15");
          qString.append(" OR mobile_ngram").append(":").append("(").append(sw).append(")^10");

          qString.append(")");
        }
      }
      started = true;
    }

    started = generateSearchStrategyQuery(searchConditionDTO.getSearchStrategies(), started, qString);
    if (StringUtils.isNotBlank(searchConditionDTO.getIds()))
      started = generateStringArrayRelatedQuery("id", searchConditionDTO.getIds().split(","), started, qString);

    started = generateLongCollectionRelateQuery("customer_or_supplier_shop_id", searchConditionDTO.getRelatedCustomerOrSupplierShopIds(), started, qString);
    started = generateStringRelatedQuery("customer_or_supplier", "supplier", started, qString);
    started = generateStringRelatedQuery("name", searchConditionDTO.getName(), started, qString);
    started = generateStringRelatedQuery("contact", searchConditionDTO.getContact(), started, qString);
    started = generateStringRelatedQuery("mobile", searchConditionDTO.getMobile(), started, qString);
    Long shopAreaId = searchConditionDTO.getRegion()!=null?searchConditionDTO.getRegion():(searchConditionDTO.getCity()!=null?searchConditionDTO.getCity():searchConditionDTO.getProvince());
    started = generateStringRelatedQuery("area_ids", StringUtil.longToString(shopAreaId,""), started, qString);

    started = generateRangeRightRegionQuery("total_trade_amount", searchConditionDTO.getTotalTradeAmountDown() == null ? "" : searchConditionDTO.getTotalTradeAmountDown().toString(),
        searchConditionDTO.getTotalTradeAmountUp() == null ? "" : searchConditionDTO.getTotalTradeAmountUp().toString(), started, qString);
    started = generateRangeQuery("last_inventory_time", searchConditionDTO.getLastInventoryTimeStart() == null ? "" : searchConditionDTO.getLastInventoryTimeStart(),
        searchConditionDTO.getLastInventoryTimeEnd() == null ? "" : searchConditionDTO.getLastInventoryTimeEnd(), started, qString);
    if (searchConditionDTO.isHasDept() != null) {
      if (searchConditionDTO.isHasDept()) {
        if (searchConditionDTO.getTotalDebtUp() != null || searchConditionDTO.getTotalDebtDown() != null) {
        started =  generateRangeRightRegionQuery("total_debt", searchConditionDTO.getTotalDebtDown() == null ? "" : searchConditionDTO.getTotalDebtDown().toString(),
              searchConditionDTO.getTotalDebtUp() == null ? "" : searchConditionDTO.getTotalDebtUp().toString(), started, qString);
        } else {
         started = generateRangeRightRegionQuery("total_debt", "0", "", started, qString);
        }
      } else {
        started =generateStringRelatedQuery("total_debt", "0", started, qString);
      }
    }


    if (searchConditionDTO.getHasDeposit() != null && searchConditionDTO.getHasDeposit()) {
      started = generateRangeRightRegionQuery("total_deposit", "0", "", started, qString);
    }

    if (searchConditionDTO.getTotalReceivableDown() != null || searchConditionDTO.getTotalReceivableUp() != null) {
      started = generateRangeRightRegionQuery("total_return_debt", searchConditionDTO.getTotalReceivableDown() == null ? "" : searchConditionDTO.getTotalReceivableDown().toString(),
          searchConditionDTO.getTotalReceivableUp() == null ? "" : searchConditionDTO.getTotalReceivableUp().toString(), started, qString);
    }

    if (qString.length() == 0) {
      qString.append("*:*");
    }
    if (StringUtils.isNotBlank(searchConditionDTO.getSort())) {
      query.setParam("sort", searchConditionDTO.getSort());
    }
    query.setQuery(qString.toString());
//    query.setParam("debugQuery", "true");
    query.addFilterQuery("shop_id:" + searchConditionDTO.getShopId());
//    if (searchConditionDTO.getRelationType() != null) {
//      String fString = "";
//      if (RelationTypes.UNRELATED.equals(searchConditionDTO.getRelationType())) {
//        fString = "relation_type:" + RelationTypes.UNRELATED.toString();
//      } else {
//        fString = "-relation_type:" + RelationTypes.UNRELATED.toString();
//      }
//      query.addFilterQuery(fString);
//    }
    query.addFilterQuery("-(status:DISABLED) AND -(shop_id:1)");//剔除shop 的索引
/*    query.addFilterQuery("doc_type:"+SolrClientHelper.BcgogoSolrDocumentType.CUSTOMER_SUPPLIER.getValue());*/

    if("identity".equals(searchConditionDTO.getFilterType())) {
      query.addFilterQuery("-dual_identity_id:[0 TO *]");
    }

    if(searchConditionDTO.getJoinSearchConditionDTO()!=null){
      generateFilterJoinOrderItemQuery(searchConditionDTO.getJoinSearchConditionDTO(),query);
    }

    query.setParam("fl", "*,score");
    query.setStart(searchConditionDTO.getStart());
    query.setRows(searchConditionDTO.getRows());

    QueryResponse response;
    FieldStatsInfo statsInfo;
    query.setFacet(true);
    query.setParam("stats", "true");
    query.setParam("stats.field", "total_debt", "total_deposit", "total_trade_amount");
    response = SolrClientHelper.getCustomerSupplierSolrClient().query(query);
    statsInfo = response.getFieldStatsInfo().get("total_debt");
    if (statsInfo != null) {
      searchResultListDTO.setTotalDebt((Double) statsInfo.getSum());
    }
    statsInfo = response.getFieldStatsInfo().get("total_deposit");
    if (statsInfo != null) {
      searchResultListDTO.setTotalDeposit((Double) statsInfo.getSum());
      searchResultListDTO = getHasMobileNumFound(searchResultListDTO, query);
    }
    statsInfo = response.getFieldStatsInfo().get("total_trade_amount");
    if (statsInfo != null) {
      searchResultListDTO.setTotalConsumption((Double) statsInfo.getSum());
    }
    getCustomerOrSupplierTypeNumStat(searchResultListDTO,query);
    SolrDocumentList docs = response.getResults();
    searchResultListDTO.setNumFound(docs.getNumFound());
    if (CollectionUtils.isNotEmpty(docs)) {
      for (SolrDocument doc : docs) {
        CustomerSupplierSearchResultDTO searchResultDTO = new CustomerSupplierSearchResultDTO(doc);
        searchResultListDTO.getCustomerSuppliers().add(searchResultDTO);
      }
    }
    return searchResultListDTO;
  }

  @Override
  public CustomerSupplierSearchResultListDTO queryCustomerSupplierWithUnknownField(CustomerSupplierSearchConditionDTO searchConditionDTO) throws Exception {
    SolrQuery query = new SolrQuery();
    if (searchConditionDTO == null) throw new Exception("CustomerSupplierSearchConditionDTO is null.");
    if (searchConditionDTO.getShopId() == null)
      throw new Exception("CustomerSupplierSearchConditionDTO shopId is null.");
    CustomerSupplierSearchResultListDTO searchResultListDTO = new CustomerSupplierSearchResultListDTO();
    String searchWord = searchConditionDTO.getSearchWord();
    StringBuilder qString = new StringBuilder();
    boolean started = false;
    if (StringUtils.isNotBlank(searchWord)) {
      boolean isFirst = true;
      for (String sw : searchWord.split(" ")) {
        if (isFirst) {
          qString.append("(");
          isFirst = false;
        } else {
          qString.append(" AND (");
        }
        sw = SolrQueryUtils.escape(sw);
        qString.append(" name").append(":").append("(").append(sw).append(")^100");
        qString.append(" OR name").append(":").append("(").append(sw).append("*").append(")^60");
        qString.append(" OR name_ngram").append(":").append("(").append(sw).append(")^40");

        qString.append(" OR contact").append(":").append("(").append(sw).append(")^20");
        qString.append(" OR contact").append(":").append("(").append(sw).append("*").append(")^15");
        qString.append(" OR contact_ngram").append(":").append("(").append(sw).append(")^10");

        qString.append(" OR mobile").append(":").append("(").append(sw).append(")^20");
        qString.append(" OR mobile").append(":").append("(").append(sw).append("*").append(")^15");
        qString.append(" OR mobile_ngram").append(":").append("(").append(sw).append(")^10");

        qString.append(" OR license_no").append(":").append("(").append(sw).append(")^20");
        qString.append(" OR license_no").append(":").append("(").append(sw).append("*").append(")^15");
        qString.append(" OR license_no_ngram").append(":").append("(").append(sw).append(")^10");

        qString.append(" OR member_no").append(":").append("(").append(sw).append(")^20");
        qString.append(" OR member_no").append(":").append("(").append(sw).append("*").append(")^15");
        qString.append(" OR member_no_ngram").append(":").append("(").append(sw).append(")^10");

        qString.append(")");
      }
      started = true;
    }

    if (StringUtils.isNotBlank(searchConditionDTO.getIds()))
      started = generateStringArrayRelatedQuery("id", searchConditionDTO.getIds().split(","), started, qString);

    started = generateStringRelatedQuery("name", searchConditionDTO.getName(), started, qString);
    started = generateStringRelatedQuery("contact", searchConditionDTO.getContact(), started, qString);
    started = generateStringRelatedQuery("mobile", searchConditionDTO.getMobile(), started, qString);

    Long shopAreaId = searchConditionDTO.getRegion()!=null?searchConditionDTO.getRegion():(searchConditionDTO.getCity()!=null?searchConditionDTO.getCity():searchConditionDTO.getProvince());
    started = generateStringRelatedQuery("area_ids", StringUtil.longToString(shopAreaId,""), started, qString);

    //客户 会员 所有逻辑
    if (StringUtils.isNotBlank(searchConditionDTO.getMemberType()))
      started = generateStringArrayRelatedQuery("member_type", searchConditionDTO.getMemberType().split(","), started, qString);
    started = generateRangeRightRegionQuery("total_amount", searchConditionDTO.getTotalAmountDown() == null ? "" : searchConditionDTO.getTotalAmountDown().toString(),
        searchConditionDTO.getTotalAmountUp() == null ? "" : searchConditionDTO.getTotalAmountUp().toString(), started, qString);
    started = generateRangeQuery("last_expense_time", searchConditionDTO.getLastExpenseTimeStart() == null ? "" : searchConditionDTO.getLastExpenseTimeStart(),
        searchConditionDTO.getLastExpenseTimeEnd() == null ? "" : searchConditionDTO.getLastExpenseTimeEnd(), started, qString);
    started = generateRangeQuery("last_inventory_time", searchConditionDTO.getLastInventoryTimeStart() == null ? "" : searchConditionDTO.getLastInventoryTimeStart(),
        searchConditionDTO.getLastInventoryTimeEnd() == null ? "" : searchConditionDTO.getLastInventoryTimeEnd(), started, qString);
    if (searchConditionDTO.isHasDept() != null) {
      if (searchConditionDTO.isHasDept()) {
        generateRangeRightRegionQuery("total_debt", searchConditionDTO.getTotalDebtDown() == null ? "" : searchConditionDTO.getTotalDebtDown().toString(),
            searchConditionDTO.getTotalDebtUp() == null ? "" : searchConditionDTO.getTotalDebtUp().toString(), started, qString);
      } else {
        generateStringRelatedQuery("total_debt", "0", started, qString);
      }
    }
    if (qString.length() == 0) {
      qString.append("*:*");
    }
    if (StringUtils.isNotBlank(searchConditionDTO.getSort())) {
      query.setParam("sort", searchConditionDTO.getSort());
    }
    query.setQuery(qString.toString());
//    query.setParam("debugQuery", "true");
    StringBuilder fQueryString = new StringBuilder();
    fQueryString.append("shop_id:").append(searchConditionDTO.getShopId());
    if (searchConditionDTO.getRelationType() != null) {
      fQueryString.append(" AND ").append("relation_type:" + searchConditionDTO.getRelationType().toString());
    }
    fQueryString.append(" AND ").append("-(status:DISABLED)");
    if (StringUtils.isNotBlank(searchConditionDTO.getCustomerOrSupplier())) {
      fQueryString.append(" AND ").append("customer_or_supplier:").append(searchConditionDTO.getCustomerOrSupplier());
    }
    fQueryString.append(" AND doc_type:").append(SolrClientHelper.BcgogoSolrDocumentType.CUSTOMER_SUPPLIER.getValue());
    query.setFilterQueries(fQueryString.toString());
    query.setParam("fl", "*,score");
    query.setStart(searchConditionDTO.getStart());
    query.setRows(searchConditionDTO.getRows());
    query.setParam("stats", "true");
    QueryResponse response = SolrClientHelper.getCustomerSupplierSolrClient().query(query);
    SolrDocumentList docs = response.getResults();
    searchResultListDTO.setNumFound(docs.getNumFound());
    CustomerSupplierSearchResultDTO searchResultDTO;
    if (CollectionUtils.isNotEmpty(docs)) {
      for (SolrDocument doc : docs) {
        searchResultDTO = new CustomerSupplierSearchResultDTO(doc);
        searchResultListDTO.getCustomerSuppliers().add(searchResultDTO);
      }
    }
    return searchResultListDTO;
  }

  //更具条件查找出所有 Mobiles
  public CustomerSupplierSearchResultListDTO queryCustomerMobiles(CustomerSupplierSearchConditionDTO searchConditionDTO) throws Exception {
    CustomerSupplierSearchResultListDTO searchResultListDTO = new CustomerSupplierSearchResultListDTO();
    SolrQuery query = queryCustomerWithUnknownFieldResponse(searchConditionDTO);
    String fString = "mobile:[\"\" TO *]";
    query.addFilterQuery(fString);
    QueryResponse response = SolrClientHelper.getCustomerSupplierSolrClient().query(query);
    SolrDocumentList docs = response.getResults();
    CustomerSupplierSearchResultDTO searchResultDTO;
    if (CollectionUtils.isNotEmpty(docs)) {
      for (SolrDocument doc : docs) {
        searchResultDTO = new CustomerSupplierSearchResultDTO(doc);
        searchResultListDTO.getCustomerSuppliers().add(searchResultDTO);
      }
    }
    return searchResultListDTO;
  }

  private SolrQuery queryCustomerWithUnknownFieldResponse(CustomerSupplierSearchConditionDTO searchConditionDTO) throws Exception {
    if (searchConditionDTO == null) throw new Exception("CustomerSupplierSearchConditionDTO is null.");
    SolrQuery query = new SolrQuery();
    String searchWord = searchConditionDTO.getSearchWord();
    StringBuilder qString = new StringBuilder();
    boolean started = false;
    if (StringUtils.isNotBlank(searchWord)) {
      boolean isFirst = true;
      for (String sw : searchWord.split(" ")) {
        if (isFirst) {
          qString.append("(");
          isFirst = false;
        } else {
          qString.append(" AND (");
        }
        sw= SolrQueryUtils.escape(sw);
        qString.append(" name").append(":").append("(").append(sw).append(")^100");
        qString.append(" OR name").append(":").append("(").append(sw).append("*").append(")^60");
        qString.append(" OR name_ngram").append(":").append("(").append(sw).append(")^40");

        qString.append(" OR contact").append(":").append("(").append(sw).append(")^20");
        qString.append(" OR contact").append(":").append("(").append(sw).append("*").append(")^15");
        qString.append(" OR contact_ngram").append(":").append("(").append(sw).append(")^10");

        qString.append(" OR mobile").append(":").append("(").append(sw).append(")^20");
        qString.append(" OR mobile").append(":").append("(").append(sw).append("*").append(")^15");
        qString.append(" OR mobile_ngram").append(":").append("(").append(sw).append(")^10");

        qString.append(" OR license_no").append(":").append("(").append(sw).append(")^20");
        qString.append(" OR license_no").append(":").append("(").append(sw).append("*").append(")^15");
        qString.append(" OR license_no_ngram").append(":").append("(").append(sw).append(")^10");

        qString.append(" OR member_no").append(":").append("(").append(sw).append(")^20");
        qString.append(" OR member_no").append(":").append("(").append(sw).append("*").append(")^15");
        qString.append(" OR member_no_ngram").append(":").append("(").append(sw).append(")^10");

        qString.append(")");
      }
      started = true;
    }
    started = generateSearchStrategyQuery(searchConditionDTO.getSearchStrategies(), started, qString);
    started = generateLongCollectionRelateQuery("customer_or_supplier_shop_id", searchConditionDTO.getRelatedCustomerOrSupplierShopIds(), started, qString);

    started = generateStringRelatedQuery("name", searchConditionDTO.getName(), started, qString);
    started = generateStringRelatedQuery("contact", searchConditionDTO.getContact(), started, qString);
    started = generateStringRelatedQuery("mobile", searchConditionDTO.getMobile(), started, qString);

    Long shopAreaId = searchConditionDTO.getRegion()!=null?searchConditionDTO.getRegion():(searchConditionDTO.getCity()!=null?searchConditionDTO.getCity():searchConditionDTO.getProvince());
    started = generateStringRelatedQuery("area_ids", StringUtil.longToString(shopAreaId,""), started, qString);

    //客户 会员 所有逻辑
    if (StringUtils.isNotBlank(searchConditionDTO.getMemberType()))
      started = generateStringArrayRelatedQuery("member_type", searchConditionDTO.getMemberType().split(","), started, qString);
    if (StringUtils.isNotBlank(searchConditionDTO.getIds()))
      started = generateStringArrayRelatedQuery("id", searchConditionDTO.getIds().split(","), started, qString);
    started = generateRangeRightRegionQuery("total_amount", searchConditionDTO.getTotalAmountDown() == null ? "" : searchConditionDTO.getTotalAmountDown().toString(),
        searchConditionDTO.getTotalAmountUp() == null ? "" : searchConditionDTO.getTotalAmountUp().toString(), started, qString);
    started = generateRangeQuery("last_expense_time", searchConditionDTO.getLastExpenseTimeStart() == null ? "" : searchConditionDTO.getLastExpenseTimeStart(),
        searchConditionDTO.getLastExpenseTimeEnd() == null ? "" : searchConditionDTO.getLastExpenseTimeEnd(), started, qString);
    if (searchConditionDTO.isHasDept() != null) {
      if (searchConditionDTO.isHasDept()) {
        if (searchConditionDTO.getTotalDebtUp() != null || searchConditionDTO.getTotalDebtDown() != null) {
          started=generateRangeRightRegionQuery("total_debt", searchConditionDTO.getTotalDebtDown() == null ? "" : searchConditionDTO.getTotalDebtDown().toString(),
              searchConditionDTO.getTotalDebtUp() == null ? "" : searchConditionDTO.getTotalDebtUp().toString(), started, qString);
        } else {
          started=generateRangeRightRegionQuery("total_debt", "0", "", started, qString);
        }
      } else {
        started=generateStringRelatedQuery("total_debt", "0", started, qString);
      }
    }else if (searchConditionDTO.getHasDeposit() != null && searchConditionDTO.getHasDeposit()) {
      started = generateRangeRightRegionQuery("total_deposit", "0", "", started, qString);
    }else if(searchConditionDTO.getHasReturnDebt() != null){
      if(searchConditionDTO.getHasReturnDebt().booleanValue()){
        started=generateRangeRightRegionQuery("total_return_debt", "0", "", started, qString);
      }else{
        started=generateStringRelatedQuery("total_return_debt", "0", started, qString);
      }
    } else if (searchConditionDTO.getHasBalance() != null) {
      if (searchConditionDTO.getHasBalance().booleanValue()) {
        started = generateRangeRightRegionQuery("total_balance", "0", "", started, qString);
      } else {
        started = generateStringRelatedQuery("total_balance", "0", started, qString);
      }
    } else if (searchConditionDTO.getHasTotalConsumption() != null) {
      if (searchConditionDTO.getHasTotalConsumption().booleanValue()) {
        started = generateRangeRightRegionQuery("total_amount", "0", "", started, qString);
      } else {
        started = generateStringRelatedQuery("total_amount", "0", started, qString);
    }
    }

    if (searchConditionDTO.getTotalPayableUp() != null || searchConditionDTO.getTotalPayableDown() != null) {
      started = generateRangeRightRegionQuery("total_return_debt", searchConditionDTO.getTotalPayableDown() == null ? "" : searchConditionDTO.getTotalPayableDown().toString(),
          searchConditionDTO.getTotalPayableUp() == null ? "" : searchConditionDTO.getTotalPayableUp().toString(), started, qString);
    }
    started = generateVehicleQuery(searchConditionDTO, started, qString);

    if (qString.length() == 0) {
      qString.append("*:*");
    }
    query.setQuery(qString.toString());
    StringBuilder fQueryString = new StringBuilder();

    fQueryString.append("shop_id:").append(searchConditionDTO.getShopId());
    if (searchConditionDTO.getRelationType() != null) {
      fQueryString.append(" AND ").append("relation_type:" + searchConditionDTO.getRelationType().toString());
    }
    fQueryString.append(" AND ").append("-(status:DISABLED)");
    if (StringUtils.isNotBlank(searchConditionDTO.getCustomerOrSupplier())) {
      fQueryString.append(" AND ").append("customer_or_supplier:").append(searchConditionDTO.getCustomerOrSupplier());
    }
    fQueryString.append(" AND doc_type:").append(SolrClientHelper.BcgogoSolrDocumentType.CUSTOMER_SUPPLIER.getValue());
    query.setFilterQueries(fQueryString.toString());

    if(StringUtils.isNotBlank(searchConditionDTO.getFilterType())) {
      if("mobileNum".equals(searchConditionDTO.getFilterType())) {
        String fString = "mobile:[\"\" TO *]";
        query.addFilterQuery(fString);
      }
      else if("todayCustomer".equals(searchConditionDTO.getFilterType())) {
        StringBuilder fString =new StringBuilder();
        Long start = DateUtil.convertDateDateShortToDateLong(DateUtil.YEAR_MONTH_DATE, new Date());
        Long end = start + 1000 * 60 * 60 * 24 - 1;
        fString.append("created_time").append(":[").append(start).append(" TO ").append(end).append("]");
        query.addFilterQuery(fString.toString());
      } else if("relatedNum".equals(searchConditionDTO.getFilterType())) {
        String fString="customer_or_supplier_shop_id:[\"\" TO *]";
        query.addFilterQuery(fString);
      } else if("identity".equals(searchConditionDTO.getFilterType())) {
        String fString = "-dual_identity_id:[0 TO *]";
        query.addFilterQuery(fString);
      } else if ("totalOBD".equals(searchConditionDTO.getFilterType())) {
        String fString = "is_obd :true";
        query.addFilterQuery(fString);
      } else if ("totalApp".equals(searchConditionDTO.getFilterType())) {
        String fString = "is_app :true";
        query.addFilterQuery(fString);
      }
    }
    return query;
  }

  private boolean generateVehicleQuery(CustomerSupplierSearchConditionDTO searchConditionDTO,boolean started, StringBuilder qString) {
    String vehicleBrand = SolrQueryUtils.escape(searchConditionDTO.getVehicleBrand());
    String vehicleModel = SolrQueryUtils.escape(searchConditionDTO.getVehicleModel());
    String vehicleColor = SolrQueryUtils.escape(searchConditionDTO.getVehicleColor());
    if (!StringUtil.isAllEmpty(vehicleBrand,vehicleModel,vehicleColor)) {
      if (started) {
        qString.append(" AND ");
      }
      qString.append(" (");
      boolean isFirst = true;
      if (!StringUtils.isBlank(vehicleBrand)){
        qString.append(" vehicle_brand_ngram").append(":").append("(").append(vehicleBrand).append(")");
        isFirst = false;
      }
      if (!StringUtils.isBlank(vehicleModel)){
        if(!isFirst){
          qString.append(" OR ");
          isFirst = false;
        }
        qString.append(" vehicle_model_ngram").append(":").append("(").append(vehicleModel).append(")");

      }
      if (!StringUtils.isBlank(vehicleColor)){
        if(!isFirst){
          qString.append(" OR ");
          isFirst = false;
        }
        qString.append(" vehicle_color_ngram").append(":").append("(").append(vehicleColor).append(")");
      }

      qString.append(" )");
      started = true;
    }
    return started;
  }

  private CustomerSupplierSearchResultListDTO getHasMobileNumFound(CustomerSupplierSearchResultListDTO searchResultListDTO, SolrQuery query) throws Exception {
    String fString = "mobile:[\"\" TO *]";
    query.addFilterQuery(fString);
    QueryResponse response = SolrClientHelper.getCustomerSupplierSolrClient().query(query);
    searchResultListDTO.setHasMobileNumFound(response.getResults().getNumFound());
    query.removeFilterQuery(fString);
    return searchResultListDTO;
  }

  private CustomerSupplierSearchResultListDTO getHasObdNumFound(CustomerSupplierSearchResultListDTO searchResultListDTO,CustomerSupplierSearchConditionDTO searchConditionDTO, SolrQuery query) throws Exception {
    String fString = "is_obd:true";
    query.addFilterQuery(fString);
    query.setStart(0);
    query.setRows(Integer.MAX_VALUE);
    QueryResponse response = SolrClientHelper.getCustomerSupplierSolrClient().query(query);
    searchResultListDTO.setHasObdNumFound(response.getResults().getNumFound());
    SolrDocumentList list = response.getResults();
    for (SolrDocument document : list) {
      Collection<Object> contactIds = document.getFieldValues("contact_id");
      Collection<Object> mobiles = document.getFieldValues("mobile");
      if (CollectionUtils.isNotEmpty(contactIds) && CollectionUtils.isNotEmpty(mobiles)) {
        Long[] contactIdsArr = contactIds.toArray(new Long[contactIds.size()]);
        String[] mobilesArr = mobiles.toArray(new String[mobiles.size()]);
        for (int i = 0; i < contactIdsArr.length; i++) {
          if(!StringUtil.SOLR_PLACEHOLDER_STRING.equals(mobilesArr[i]) && StringUtils.isNotBlank(mobilesArr[i])){
            searchResultListDTO.getOdbContactIdList().add(contactIdsArr[i]);
          }
        }
      }
    }
    query.removeFilterQuery(fString);
    query.setStart(searchConditionDTO.getStart());
    query.setRows(searchConditionDTO.getRows());
    return searchResultListDTO;
    }

  private CustomerSupplierSearchResultListDTO getTotalReceivableNumFound(CustomerSupplierSearchResultListDTO searchResultListDTO, SolrQuery query) throws Exception {
    String fString = " -total_debt:0 and total_debt:[ 0 TO * ]";
    query.addFilterQuery(fString);
    QueryResponse response = SolrClientHelper.getCustomerSupplierSolrClient().query(query);
    searchResultListDTO.setTotalReceivableNumFound(response.getResults().getNumFound());
    query.removeFilterQuery(fString);
    return searchResultListDTO;
  }


  private CustomerSupplierSearchResultListDTO getCustomerOrSupplierTypeNumStat(CustomerSupplierSearchResultListDTO searchResultListDTO, SolrQuery query) throws Exception {
    String fString = "customer_or_supplier_shop_id:[\"\" TO *]";
    query.addFilterQuery(fString);
    QueryResponse response = SolrClientHelper.getCustomerSupplierSolrClient().query(query);
    searchResultListDTO.setRelatedNum(response.getResults().getNumFound());
    query.removeFilterQuery(fString);
    fString="-customer_or_supplier_shop_id:[\"\" TO *]" ;
    query.addFilterQuery(fString);
    response = SolrClientHelper.getCustomerSupplierSolrClient().query(query);
    searchResultListDTO.setUnRelatedNum(response.getResults().getNumFound());
    query.removeFilterQuery(fString);
    return searchResultListDTO;
  }

  private CustomerSupplierSearchResultListDTO getTodayNewCustomerNumFound(CustomerSupplierSearchResultListDTO searchResultListDTO, SolrQuery query) throws Exception {
    StringBuilder fString =new StringBuilder();
    Long start = DateUtil.convertDateDateShortToDateLong(DateUtil.YEAR_MONTH_DATE, new Date());
    Long end = start + 1000 * 60 * 60 * 24 - 1;
    fString.append("created_time").append(":[").append(start).append(" TO ").append(end).append("]");
    query.addFilterQuery(fString.toString());
    QueryResponse response = SolrClientHelper.getCustomerSupplierSolrClient().query(query);
    searchResultListDTO.setTodayNewCustomerNumFound(response.getResults().getNumFound());
    query.removeFilterQuery(fString.toString());
    return searchResultListDTO;
  }

  private CustomerSupplierSearchResultListDTO getMemberNumFound(CustomerSupplierSearchResultListDTO searchResultListDTO) {
    if (MapUtils.isEmpty(searchResultListDTO.getCounts())) return searchResultListDTO;
    Long memberNumFound = 0l;
    for (Map.Entry<String, Long> entry : searchResultListDTO.getCounts().entrySet()) {
      if (!entry.getKey().equals("非会员")) {
        memberNumFound += entry.getValue();
      }
    }
    searchResultListDTO.setMemberNumFound(memberNumFound);
    return searchResultListDTO;
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


  private boolean generateStringRelatedFuzzyQuery(String field, String value, boolean started, StringBuilder qString) {
    if (!StringUtils.isBlank(value)) {
      if (started) {
        qString.append(" AND ");
      }
      qString.append(field).append(":").append("(*").append(SolrQueryUtils.escape(value)).append("*)");
      started = true;
    }
    return started;
  }

  //默认闭区间 通过region判断区间开闭
  private boolean generateRangeQuery(String field, String start, String end, boolean started, StringBuilder qString) {
    if (StringUtils.isBlank(start) && StringUtils.isBlank(end)) return started;
    if (started) {
      qString.append(" AND ");
    }
    if (StringUtils.isNotBlank(start) && StringUtils.isNotBlank(end)) {
      qString.append(field).append(":[").append(start).append(" TO ").append(end).append("]");
    } else if (StringUtils.isNotBlank(start)) {
      qString.append(field).append(":[").append(start).append(" TO *]");
    } else {
      qString.append(field).append(":[* TO ").append(end).append("]");
    }
    started = true;
    return started;
  }

  //左开右闭
  private boolean generateRangeRightRegionQuery(String field, String start, String end, boolean started, StringBuilder qString) {
    if (StringUtils.isBlank(start) && StringUtils.isBlank(end)) return started;
    if (started) {
      qString.append(" AND ");
    }
    if (StringUtils.isNotBlank(start) && StringUtils.isNotBlank(end)) {
      qString.append("(").append(field).append(":{").append(start).append(" TO ").append(end).append("}").append(" OR ").append(field).append(":").append(end).append(")");
    } else if (StringUtils.isNotBlank(start)) {
      qString.append(field).append(":{").append(start).append(" TO *}");
    } else {
      qString.append("(").append(field);
      qString.append(":{* TO ");
      qString.append(end).append("}").append(" OR ").append(field).append(":").append(end).append(")");
    }
    started = true;
    return started;
  }

  @Override
  public List<SearchSuggestionDTO> queryCustomerSupplierSuggestion(CustomerSupplierSearchConditionDTO searchConditionDTO) throws Exception {
    StopWatch sw = new StopWatch("queryCustomerSupplierSuggestion");
    sw.start("query generating");
    if (searchConditionDTO == null) throw new Exception("CustomerSupplierSearchConditionDTO is null.");
    String field = searchConditionDTO.getSearchField();
    String searchWord = searchConditionDTO.getSearchWord();
    StringBuilder qString = new StringBuilder();
    boolean started = false;
    if (StringUtils.isNotBlank(searchWord)) {
      if (field == null || field.equals(CustomerSupplierSearchConditionDTO.INFO)) {
        generateMultiFieldSuggestionQuery(qString, searchWord, searchConditionDTO.getSearchFieldStrategies());
      } else {
        generateSingleFieldSuggestionQuery(qString, searchWord, field);
      }
      started = true;
    }
    generateSearchStrategyQuery(searchConditionDTO.getSearchStrategies(), started, qString);
    generateLongCollectionRelateQuery("customer_or_supplier_shop_id", searchConditionDTO.getRelatedCustomerOrSupplierShopIds(), started, qString);

    SolrQuery query = new SolrQuery();
    if (qString.length() == 0) {
      qString.append("*:*");
    }
    query.setQuery(qString.toString());
//    query.setParam("debugQuery", "true");
    StringBuilder fQueryString = new StringBuilder();
    fQueryString.append("shop_id:").append(searchConditionDTO.getShopId());

    if(searchConditionDTO.getShopKind()!=null){
      fQueryString.append(" AND ").append("shop_kind:").append(searchConditionDTO.getShopKind());
    }
    fQueryString.append(" AND ").append("-(status:DISABLED)");
    if (StringUtils.isNotBlank(searchConditionDTO.getCustomerOrSupplier())) {
      fQueryString.append(" AND ").append("customer_or_supplier:").append(searchConditionDTO.getCustomerOrSupplier());
    }
    if("identity".equals(searchConditionDTO.getFilterType())) {
      fQueryString.append(" AND ").append("-dual_identity_id:[0 TO *]");
    }
    fQueryString.append(" AND doc_type:").append(SolrClientHelper.BcgogoSolrDocumentType.CUSTOMER_SUPPLIER.getValue());
    query.setFilterQueries(fQueryString.toString());

    if (searchConditionDTO.getRows() <= 0)
      throw new BcgogoException("query customer and supplier rows is illegal!");
    query.setRows(searchConditionDTO.getRows());

    query.setParam("fl", "*,score");
    if (StringUtils.isNotBlank(searchConditionDTO.getSort())) {
      query.setParam("sort", searchConditionDTO.getSort());
    }
    if (StringUtils.isNotBlank(field) && !field.equals(CustomerSupplierSearchConditionDTO.INFO)) {
      query.setParam("group", "true");
      query.setParam("group.field", field);
      query.setParam("group.main", "true");
    }
    sw.stop();
    sw.start("query solr time");
    QueryResponse rsp = SolrClientHelper.getCustomerSupplierSolrClient().query(query);
    sw.stop();
    sw.start("query transform time");
    List<SearchSuggestionDTO> result = processQueryResult(rsp.getResults(), searchWord,field, searchConditionDTO);
    sw.stop();
//    LOG.warn(sw.toString());
    return result;
  }

  private void generateSingleFieldSuggestionQuery(StringBuilder qString, String q, String field) {
    q= SolrQueryUtils.escape(q);
    qString.append("(");

    qString.append(field).append(":").append("(").append(q).append(")^10");
    qString.append(" OR ").append(field).append(":").append("(").append(q).append("*").append(")^9");
    qString.append(" OR ").append(field).append("_ngram").append(":").append("(").append(q).append(")^8");
    qString.append(" OR ").append(field).append("_fl:").append("(").append(q).append(")^4");
    qString.append(" OR ").append(field).append("_fl:").append("(").append(q).append("*").append(")^3");
    qString.append(" OR ").append(field).append("_py:").append("(").append(q).append(")^4");
    qString.append(" OR ").append(field).append("_py:").append("(").append(q).append("*").append(")^3");

    qString.append(")");
  }

  private void generateMultiFieldSuggestionQuery(StringBuilder qString, String q,CustomerSupplierSearchConditionDTO.SearchFieldStrategy[] searchFieldStrategies) {
    q = SolrQueryUtils.escape(q);
    qString.append("(");
    qString.append("name").append(":").append("(").append(q).append(")^10");
    qString.append(" OR name").append(":").append("(").append(q).append("*").append(")^9");
    qString.append(" OR name_ngram").append(":").append("(").append(q).append(")^8");
    qString.append(" OR name_fl").append(":").append("(").append(q).append(")^4");
    qString.append(" OR name_fl").append(":").append("(").append(q).append("*").append(")^3");
    qString.append(" OR name_fl_ngram").append(":").append("(").append(q).append(")^2");
    qString.append(" OR name_py").append(":").append("(").append(q).append(")^4");
    qString.append(" OR name_py").append(":").append("(").append(q).append("*").append(")^3");

    qString.append(" OR contact").append(":").append("(").append(q).append(")^10");
    qString.append(" OR contact").append(":").append("(").append(q).append("*").append(")^6");
    qString.append(" OR contact_ngram").append(":").append("(").append(q).append(")^4");
    qString.append(" OR contact_fl").append(":").append("(").append(q).append(")^4");
    qString.append(" OR contact_fl").append(":").append("(").append(q).append("*").append(")^3");
    qString.append(" OR contact_fl_ngram").append(":").append("(").append(q).append(")^2");
    qString.append(" OR contact_py").append(":").append("(").append(q).append(")^4");
    qString.append(" OR contact_py").append(":").append("(").append(q).append("*").append(")^3");

    qString.append(" OR mobile").append(":").append("(").append(q).append(")^20");
    qString.append(" OR mobile").append(":").append("(").append(q).append("*").append(")^15");
    qString.append(" OR mobile_ngram").append(":").append("(").append(q).append(")^10");

    if (ArrayUtils.contains(searchFieldStrategies,CustomerSupplierSearchConditionDTO.SearchFieldStrategy.searchIncludeLicenseNo)) {
      qString.append(" OR license_no").append(":").append("(").append(q).append(")^10");
      qString.append(" OR license_no").append(":").append("(").append(q).append("*").append(")^6");
      qString.append(" OR license_no_ngram").append(":").append("(").append(q).append(")^4");
      qString.append(" OR license_no_fl").append(":").append("(").append(q).append(")^4");
      qString.append(" OR license_no_fl").append(":").append("(").append(q).append("*").append(")^3");
      qString.append(" OR license_no_fl_ngram").append(":").append("(").append(q).append(")^2");
      qString.append(" OR license_no_py").append(":").append("(").append(q).append(")^4");
      qString.append(" OR license_no_py").append(":").append("(").append(q).append("*").append(")^3");
    }
    if (ArrayUtils.contains(searchFieldStrategies,CustomerSupplierSearchConditionDTO.SearchFieldStrategy.searchIncludeMemberNo)) {
      qString.append(" OR member_no").append(":").append("(").append(q).append(")^10");
      qString.append(" OR member_no").append(":").append("(").append(q).append("*").append(")^6");
      qString.append(" OR member_no_ngram").append(":").append("(").append(q).append(")^4");
      qString.append(" OR member_no_fl").append(":").append("(").append(q).append(")^4");
      qString.append(" OR member_no_fl").append(":").append("(").append(q).append("*").append(")^3");
      qString.append(" OR member_no_fl_ngram").append(":").append("(").append(q).append(")^2");
      qString.append(" OR member_no_py").append(":").append("(").append(q).append(")^4");
      qString.append(" OR member_no_py").append(":").append("(").append(q).append("*").append(")^3");
    }
    qString.append(")");
  }

  private List<SearchSuggestionDTO> processQueryResult(SolrDocumentList documents,String q, String field,CustomerSupplierSearchConditionDTO conditionDTO) {
    CustomerSupplierSearchConditionDTO.SearchFieldStrategy[] searchFieldStrategies = conditionDTO.getSearchFieldStrategies();
    List<SearchSuggestionDTO> results = new ArrayList<SearchSuggestionDTO>();
    if (CollectionUtils.isEmpty(documents)) return results;
    if (StringUtils.isNotBlank(field) && !field.equals(CustomerSupplierSearchConditionDTO.INFO)) {
      for (SolrDocument document : documents) {
        String value = (String) document.getFirstValue(field);
        if (StringUtils.isBlank(value)) continue;
        SearchSuggestionDTO result = new SearchSuggestionDTO();
        result.addEntry(field, value);
        results.add(result);
      }
      return results;
    }

    //如果是name 则拼接name+包含的field
    for (SolrDocument document : documents) {
      String customerOrSupplier = (String) document.getFirstValue("customer_or_supplier");
      String customerOrSupplierShopId = (String) document.getFirstValue("customer_or_supplier_shop_id");
      String id = (String) document.getFirstValue("id");
      String name = (String) document.getFirstValue("name");
      String address = (String) document.getFirstValue("address");
      Collection<Object> contacts = document.getFieldValues("contact");
      Collection<Object> contactFls = document.getFieldValues("contact_fl");
      Collection<Object> contactPys = document.getFieldValues("contact_py");
      Collection<Object> mobiles = document.getFieldValues("mobile");
      String licenseNo = (String) document.getFirstValue("license_no");
      List<String> list = (List<String>) document.getFieldValue("license_no");
      if (CollectionUtil.isNotEmpty(list)&&StringUtils.isNotBlank(conditionDTO.getSearchWord())) {
        PingyinInfo tmp = PinyinUtil.getPingyinInfo(conditionDTO.getSearchWord());
        for (String str : list) {
          PingyinInfo pinyin = PinyinUtil.getPingyinInfo(str);
          if (pinyin != null && pinyin.firstLetters != null && pinyin.pingyin != null)
            if (pinyin.firstLetters.contains(tmp.firstLetters)
                || pinyin.pingyin.contains(tmp.pingyin) || str.contains(conditionDTO.getSearchWord())) {
              licenseNo = str;
              break;
            }
        }
      }
      String memberNo = (String) document.getFirstValue("member_no");
      SearchSuggestionDTO result = new SearchSuggestionDTO();
      result.addEntry("customerOrSupplier", customerOrSupplier);
      result.addEntry("id", id.startsWith("shop_")?id.split("_")[1]:id);
      result.addEntry("name", name);

      boolean isMatch =false;
      if (CollectionUtils.isNotEmpty(contacts) && CollectionUtils.isNotEmpty(contactFls) && CollectionUtils.isNotEmpty(contactPys) && CollectionUtils.isNotEmpty(mobiles)) {
        String[] contactArr = contacts.toArray(new String[contacts.size()]);
        String[] contactFlArr = contactFls.toArray(new String[contactFls.size()]);
        String[] contactPyArr = contactPys.toArray(new String[contactPys.size()]);
        String[] mobilesArr = mobiles.toArray(new String[mobiles.size()]);
        for(int i=0;i<contactArr.length;i++){
          if (!StringUtil.SOLR_PLACEHOLDER_STRING.equals(contactArr[i])
              && (StringUtil.isContains(q, contactArr[i])
                  || StringUtil.isPrefixOfWord(q, contactFlArr[i])
                  || StringUtil.isPrefixOfWord(q, contactPyArr[i]))) {
            result.addEntry("contact", contactArr[i]);
            result.addEntry("mobile", StringUtil.SOLR_PLACEHOLDER_STRING.equals(mobilesArr[i])?"":mobilesArr[i]);
            isMatch =true;
            break;
          }
        }
        if(!isMatch){
          for(int i=0;i<mobilesArr.length;i++){
            if (!StringUtil.SOLR_PLACEHOLDER_STRING.equals(mobilesArr[i])
                && StringUtil.isContains(q, mobilesArr[i])) {
              result.addEntry("contact", StringUtil.SOLR_PLACEHOLDER_STRING.equals(contactArr[i])?"":contactArr[i]);
              result.addEntry("mobile", mobilesArr[i]);
              isMatch =true;
              break;
            }
          }
        }
        //如果这条记录不是通过 联系人和手机号匹配到的 就拿 主联系人（主联系人 是第一个）
        if(!isMatch){
          result.addEntry("contact", StringUtil.SOLR_PLACEHOLDER_STRING.equals(contactArr[0])?"":contactArr[0]);
          result.addEntry("mobile", StringUtil.SOLR_PLACEHOLDER_STRING.equals(mobilesArr[0])?"":mobilesArr[0]);
        }
      }

      result.addEntry("address", address);
      if (ArrayUtils.contains(searchFieldStrategies,CustomerSupplierSearchConditionDTO.SearchFieldStrategy.searchIncludeLicenseNo)) {
        result.addEntry("licenseNo", licenseNo);
      }
      if (ArrayUtils.contains(searchFieldStrategies,CustomerSupplierSearchConditionDTO.SearchFieldStrategy.searchIncludeMemberNo)) {
        result.addEntry("memberNo", memberNo);
      }
      result.addEntry("customerOrSupplierShopId", customerOrSupplierShopId);
      results.add(result);
    }
    return results;
  }

  private boolean generateSearchStrategyQuery(CustomerSupplierSearchConditionDTO.SearchStrategy[] searchStrategies, boolean started, StringBuilder qString) {
    if (ArrayUtils.isEmpty(searchStrategies)) return started;
    if (started) {
      qString.append(" AND ");
    }
    //过滤customerOrSupplierShopIdIds不为空
    if (ArrayUtils.contains(searchStrategies,CustomerSupplierSearchConditionDTO.SearchStrategy.customerOrSupplierShopIdNotEmpty)) {
      qString.append("customer_or_supplier_shop_id:[\"\" TO *]");
    } else {
    }
    started = true;
    return started;
  }

  private boolean generateLongCollectionRelateQuery(String field, List<Long> values, boolean started, StringBuilder qString) {
    if (CollectionUtils.isEmpty(values)) return started;
    if (started) {
      qString.append(" AND ");
    }
    qString.append(field).append(":(");
    for (int i = 0, max = values.size(); i < max; i++) {
      qString.append(values.get(i));
      if (i < (max - 1)) qString.append(" OR ");
    }
    qString.append(")");
    started = true;
    return started;
  }

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
      if (!StringUtils.isBlank(value)) {
        qString.append(field).append(":").append("(\"").append(SolrQueryUtils.escape(value)).append("\")");
        started = true;
        i++;
      }
    }
    if (flag)
      qString.append(")");
    return started;
  }

  public boolean generateProductRelatedQuery(JoinSearchConditionDTO conditions, boolean started, StringBuilder qString) {
//    if(StringUtils.isNotBlank(conditions.getProductSearchWord())){//单据查询的  product 混合框功能
//      String value = SolrQueryUtils.escape(conditions.getProductSearchWord());
//      if (started) {
//        qString.append(" AND ");
//      }
//      qString.append("(");
//      qString.append("commodity_code").append(":").append("(").append(value).append("*").append(")^10");
//      qString.append(" OR ").append("commodity_code").append(":").append("(").append(value).append("*").append(")^4");
//      qString.append(" OR ").append("commodity_code").append(":").append("(").append("*").append(value).append("*").append(")");
//      qString.append(" OR ").append("commodity_code").append(":").append("(\"").append(value).append("\")^100");
//
//      qString.append(" OR product_name").append(":").append("(").append(value).append(")^4");
//      qString.append(" OR product_name_ngram").append(":").append("(").append(value).append(")^0.001");
//      qString.append(" OR product_name_ngram_continuous").append(":").append("(").append(value).append(")");
//      qString.append(" OR product_name_exact").append(":").append("(").append(value).append("*").append(")^100");
//
//      qString.append(" OR product_brand").append(":").append("(").append(value).append(")^4");
//      qString.append(" OR product_brand_ngram").append(":").append("(").append(value).append(")^0.001");
//      qString.append(" OR product_brand_ngram_continuous").append(":").append("(").append(value).append(")");
//      qString.append(" OR product_brand_exact").append(":").append("(").append(value).append("*").append(")^100");
//
//      qString.append(" OR product_spec").append(":").append("(").append(value).append(")^4");
//      qString.append(" OR product_spec_ngram").append(":").append("(").append(value).append(")^0.001");
//      qString.append(" OR product_spec_ngram_continuous").append(":").append("(").append(value).append(")");
//      qString.append(" OR product_spec_exact").append(":").append("(").append(value).append("*").append(")^100");
//
//      qString.append(" OR product_model").append(":").append("(").append(value).append(")^4");
//      qString.append(" OR product_model_ngram").append(":").append("(").append(value).append(")^0.001");
//      qString.append(" OR product_model_ngram_continuous").append(":").append("(").append(value).append(")");
//      qString.append(" OR product_model_exact").append(":").append("(").append(value).append("*").append(")^100");
//
//      qString.append(" OR product_vehicle_brand").append(":").append("(").append(value).append(")^4");
//      qString.append(" OR product_vehicle_brand_ngram").append(":").append("(").append(value).append(")^0.001");
//      qString.append(" OR product_vehicle_brand_ngram_continuous").append(":").append("(").append(value).append(")");
//      qString.append(" OR product_vehicle_brand_exact").append(":").append("(").append(value).append("*").append(")^100");
//
//      qString.append(" OR product_vehicle_model").append(":").append("(").append(value).append(")^4");
//      qString.append(" OR product_vehicle_model_ngram").append(":").append("(").append(value).append(")^0.001");
//      qString.append(" OR product_vehicle_model_ngram_continuous").append(":").append("(").append(value).append(")");
//      qString.append(" OR product_vehicle_model_exact").append(":").append("(").append(value).append("*").append(")^100");
//
//      qString.append(")");
//      started = true;
//    }
    started = generateStringRelatedQuery("commodity_code", conditions.getCommodityCode(), started, qString);
    started = generateStringRelatedQuery("product_name_exact", conditions.getProductName(), started, qString);
    started = generateStringRelatedQuery("product_brand_exact", conditions.getProductBrand(), started, qString);
    started = generateStringRelatedQuery("product_spec_exact", conditions.getProductSpec(), started, qString);
    started = generateStringRelatedQuery("product_model_exact", conditions.getProductModel(), started, qString);
    started = generateStringRelatedQuery("product_vehicle_brand_exact", conditions.getProductVehicleBrand(), started, qString);
    started = generateStringRelatedQuery("product_vehicle_model_exact", conditions.getProductVehicleModel(), started, qString);
    return started;
  }

  //
  private boolean generateMultifieldQuery(String field,String value, boolean started, StringBuilder qString) {
    if (!StringUtils.isBlank(value)) {
      value = SolrQueryUtils.escape(value);
      if (started) {
        qString.append(" AND ");
      }
      qString.append("(");
      qString.append(field).append(":").append("(").append(value).append(")^4");
      qString.append(" OR ").append(field).append("_ngram").append(":").append("(").append(value).append(")^0.001");
      qString.append(" OR ").append(field).append("_ngram_continuous").append(":").append("(").append(value).append(")");
      qString.append(" OR ").append(field).append("_exact").append(":").append("(").append(value).append("*").append(")^100");
      qString.append(")");
      started = true;
    }
    return started;
  }

  /**
   * 编号  类型的  前后 匹配  不做分词 拆字 拼音
   * @param qString
   * @param value
   * @param field
   */
  private boolean generateFuzzyRelatedQuery(String field, String value, boolean started, StringBuilder qString) {
    if (!StringUtils.isBlank(value)) {
      value = SolrQueryUtils.escape(value);
      if (started) {
        qString.append(" AND ");
      }
      qString.append("(");
      qString.append(field).append(":").append("(").append(value).append("*").append(")^10");
      qString.append(" OR ").append(field).append(":").append("(").append(value).append("*").append(")^4");
      qString.append(" OR ").append(field).append(":").append("(").append("*").append(value).append("*").append(")");
      qString.append(" OR ").append(field).append(":").append("(\"").append(value).append("\")^100");
      qString.append(")");
      started = true;
    }
    return started;
  }

  //join
  private void generateFilterJoinOrderItemQuery(JoinSearchConditionDTO joinSearchConditionDTO,SolrQuery query) {
    if(joinSearchConditionDTO==null) return;
    StringBuilder joinQueryString = new StringBuilder();
    boolean started = false;
    if(SolrClientHelper.BcgogoSolrCore.ORDER_ITEM_CORE.getValue().equals(joinSearchConditionDTO.getFromIndex())){
      joinQueryString.append("shop_id:").append(joinSearchConditionDTO.getShopId()).append(" AND doc_type:").append(SolrClientHelper.BcgogoSolrDocumentType.ORDER_ITEM_DOC_TYPE.getValue());
      if(joinSearchConditionDTO.getItemTypes()!=null){
        joinQueryString.append(" AND item_type:").append(joinSearchConditionDTO.getItemTypes());
        started = true;
      }

      started = generateStringArrayRelatedQuery("order_type", joinSearchConditionDTO.getOrderTypes(), started, joinQueryString);
      if(!ArrayUtils.isEmpty(joinSearchConditionDTO.getOrderStatus())){
        if (started) {
          joinQueryString.append(" AND ");
        }
        joinQueryString.append("order_status:(");
        for (int i = 0, max = joinSearchConditionDTO.getOrderStatus().length; i < max; i++) {
          joinQueryString.append(joinSearchConditionDTO.getOrderStatus()[i]);
          if (i < (max - 1)) joinQueryString.append(" OR ");
        }
        joinQueryString.append(")");
        started = true;
      }

      generateProductRelatedQuery(joinSearchConditionDTO,started,joinQueryString);
    }
    StringBuilder qString = new StringBuilder();
    if(StringUtils.isNotBlank(joinQueryString.toString())){
      qString.append("{!join from=").append(joinSearchConditionDTO.getFromColumn()).append(" to=").append(joinSearchConditionDTO.getToColumn()).append(" fromIndex=").append(joinSearchConditionDTO.getFromIndex()).append("}");
      qString.append("(").append(joinQueryString).append(")");
      query.addFilterQuery(qString.toString());
    }
  }

  private CustomerSupplierSearchResultListDTO getHasAppNumFound(CustomerSupplierSearchResultListDTO searchResultListDTO,CustomerSupplierSearchConditionDTO searchConditionDTO, SolrQuery query) throws Exception {
    String fString = "is_app:true";
    query.addFilterQuery(fString);
    query.setStart(0);
    query.setRows(Integer.MAX_VALUE);
    QueryResponse response = SolrClientHelper.getCustomerSupplierSolrClient().query(query);
    searchResultListDTO.setHasAppNumFound(response.getResults().getNumFound());
    SolrDocumentList list = response.getResults();
    for (SolrDocument document : list) {
      Collection<Object> mobiles = document.getFieldValues("mobile");
      if (CollectionUtils.isNotEmpty(mobiles)) {
        String[] mobilesArr = mobiles.toArray(new String[mobiles.size()]);
        for (int i = 0; i < mobilesArr.length; i++) {
          if(!StringUtil.SOLR_PLACEHOLDER_STRING.equals(mobilesArr[i]) && StringUtils.isNotBlank(mobilesArr[i])){
            searchResultListDTO.getAppMobileList().add(mobilesArr[i]);
          }
        }
      }
    }
    query.setStart(searchConditionDTO.getStart());
    query.setRows(searchConditionDTO.getRows());
    query.removeFilterQuery(fString);
    return searchResultListDTO;
  }


  /**
   * 通过客户名找到对应的客户id集
   * @param customerName
   * @return
   * @throws Exception
   */
  public List<Long> queryCustomerInfo(String customerName) throws Exception{
    StopWatch sw = new StopWatch();
    sw.start("query generating");
    if (StringUtils.isBlank(customerName)) throw new Exception("customerName is null.");
    StringBuilder qString = new StringBuilder();
    boolean started = false;
    started = generateStringRelatedFuzzyQuery("name", customerName, started, qString);
    SolrQuery query = new SolrQuery();
    if (qString.length() == 0) {
      qString.append("*:*");
    }
    query.setQuery(qString.toString());
    StringBuilder fQueryString = new StringBuilder();
    fQueryString.append("name:*").append(customerName).append("*");
    query.setParam("fl", "*,score");
    QueryResponse rsp = SolrClientHelper.getCustomerSupplierSolrClient().query(query);
    SolrDocumentList documents=rsp.getResults();
    List<Long> customerIdList=new ArrayList<Long>();
    for(SolrDocument document : documents){
      Long id = Long.valueOf((String)document.getFirstValue("id"));
      customerIdList.add(id);
    }
    return customerIdList;
  }

}
