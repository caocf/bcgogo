package com.bcgogo.search.service.vehicle;

import com.bcgogo.enums.OrderTypes;
import com.bcgogo.enums.ServiceStatus;
import com.bcgogo.enums.VehicleStatus;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.search.client.SolrClientHelper;
import com.bcgogo.search.dto.JoinSearchConditionDTO;
import com.bcgogo.search.dto.SearchConditionDTO;
import com.bcgogo.search.dto.VehicleSearchConditionDTO;
import com.bcgogo.search.dto.VehicleSearchResultDTO;
import com.bcgogo.search.model.CurrentUsedVehicle;
import com.bcgogo.search.service.CurrentUsed.IVehicleCurrentUsedService;
import com.bcgogo.search.util.SolrQueryUtils;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.CarDTO;
import com.bcgogo.user.dto.VehicleDTO;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.SearchConstant;
import com.bcgogo.utils.ServiceUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FieldStatsInfo;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.TermsResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.CommonParams;
import org.apache.solr.common.params.StatsParams;
import org.apache.solr.common.params.TermsParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class SearchVehicleService implements ISearchVehicleService {
  private static final Logger LOG = LoggerFactory.getLogger(ISearchVehicleService.class);
  private static final String KEY_INVERVAL="_";

  @Override
  public List<CarDTO> queryVehicleLicenseNoSuggestion(Long shopId,String searchWord) throws Exception {
    if (shopId == null) throw new BcgogoException("shopId nullPointException!");
    StringBuilder qString = new StringBuilder();

    if (StringUtils.isNotBlank(searchWord)) {
      searchWord = SolrQueryUtils.escape(searchWord);
      qString.append("licence_no:").append("(\"").append(searchWord).append("\"").append(")^100 ");
      qString.append(" OR ").append("licence_no_start:").append("(").append(searchWord).append(")^50 ");
      qString.append(" OR ").append("licence_no_end:").append("(").append(searchWord).append(")^40 ");
      qString.append(" OR ").append("licence_no_contained:").append("(").append(searchWord).append(")^30 ");
      qString.append(" OR ").append("licence_no_fl_start:").append("(").append(searchWord).append(")^20 ");
      qString.append(" OR ").append("licence_no_fl_end:").append("(").append(searchWord).append(")^10 ");
      qString.append(" OR ").append("licence_no_fl_contained:").append("(").append(searchWord).append(") ");
    }
    SolrQuery query = new SolrQuery();
    if (qString.length() == 0) {
      qString.append("*:*");
    }
    query.setQuery(qString.toString());
    StringBuilder fQueryString = new StringBuilder();
    fQueryString.append("(shop_id:").append(shopId.toString()).append(" AND vehicle_status:").append(VehicleStatus.ENABLED.toString()).append(" AND doc_type:").append(SolrClientHelper.BcgogoSolrDocumentType.VEHICLE_DOC_TYPE.getValue()).append(")");
    query.setFilterQueries(fQueryString.toString());
    query.setParam("fl", "id,licence_no,score");
    query.setStart(0);
    query.setRows(50);
    QueryResponse rsp = SolrClientHelper.getVehicleSolrClient().query(query);
    SolrDocumentList documents = rsp.getResults();
    List<CarDTO> result = new ArrayList<CarDTO>();
    CarDTO carDTO = null;
    for (SolrDocument document : documents) {
      String id = (String) document.getFirstValue("id");
      String licenseNo = (String) document.getFirstValue("licence_no");
      carDTO = new CarDTO();
      carDTO.setId(id);
      carDTO.setCarno(licenseNo);
      carDTO.setLicenceNo(licenseNo);
      result.add(carDTO);
    }
    return result;
  }

  @Override
  public List<String> getVehicleSuggestionList(SearchConditionDTO searchConditionDTO) throws Exception {
    if (searchConditionDTO.searchFieldEquals(SearchConstant.VEHICLE_BRAND)) {
      String searchField = searchConditionDTO.getSearchField();//todo zhangjuntao 临时解决 因为getCurrentUsedVehiclesFromMemory中修改了SearchField
      IVehicleCurrentUsedService currentUsedService = ServiceManager.getService(IVehicleCurrentUsedService.class);
      //得到 下拉框前5个常用Vehicle memcache
      List<String> vehicleBrandList = new ArrayList<String>();
      List<CurrentUsedVehicle> currentUsedVehicleList = currentUsedService.getCurrentUsedVehiclesFromMemory(searchConditionDTO);
      if (CollectionUtils.isNotEmpty(currentUsedVehicleList)) {
        for (CurrentUsedVehicle CurrentUsedVehicle : currentUsedVehicleList) {
          vehicleBrandList.add(CurrentUsedVehicle.getBrand());
        }
      }

      searchConditionDTO.setSearchField(searchField);
      //从solr中取
      List<String> searchList = this.getVehicleSuggestionListByKeywords(searchConditionDTO);
      searchList = ServiceUtil.classifyList(searchList);
      if (CollectionUtils.isNotEmpty(searchList)) {
        vehicleBrandList.addAll(searchList);
      }
      return vehicleBrandList;
    } else {
      return this.getVehicleSuggestionListByKeywords(searchConditionDTO);
    }

  }

  @Override
  public List<String> getVehicleSuggestionListByKeywords(SearchConditionDTO searchConditionDTO) throws Exception {
    if (StringUtils.isBlank(searchConditionDTO.getSearchWord())) {
      return getVehicleSuggestionListByKeywords(searchConditionDTO, true);
    }    List<String> terms = queryTermsForVehicle(searchConditionDTO.getSearchWord(), searchConditionDTO.getSearchField());
    if ((terms == null || terms.size() <= 0) && ("brand".equals(searchConditionDTO.getSearchField()) || "model".equals(searchConditionDTO.getSearchField()))) {
      String pyField = "brand_first_letter";
      if ("model".equals(searchConditionDTO.getSearchField())) pyField = "model_first_letter";
      terms = queryTermsForVehicle(searchConditionDTO.getSearchWord().toLowerCase(), pyField);
    }
    if (terms.size() == 0) {
      return getVehicleSuggestionListByKeywords(searchConditionDTO, true);
    }

    StringBuilder stringBuilder = new StringBuilder();
    for (String term : terms) {
      stringBuilder.append(term).append(" ");
    }
    searchConditionDTO.setSearchWord(stringBuilder.toString());
    return getVehicleSuggestionListByKeywords(searchConditionDTO, false);
  }

  private List<String> getVehicleSuggestionListByKeywords(SearchConditionDTO searchConditionDTO, boolean useAnd)
      throws Exception {
    LOG.debug("query {}", searchConditionDTO.getSearchWord());
    String searchField = searchConditionDTO.getSearchField();
    SolrQuery query = new SolrQuery();
    if (!StringUtils.isBlank(searchConditionDTO.getSearchWord())) {
      StringBuffer QueryString = new StringBuffer();
      QueryString.append(searchField).append(":(").append(escape(searchConditionDTO.getSearchWord())).append(")");
      if ("brand".equals(searchField))
        QueryString.append(" OR ").append("brand_first_letter:(").append(escape(searchConditionDTO.getSearchWord())).append(")");
      else if ("model".equals(searchField))
        QueryString.append(" OR ").append("model_first_letter:(").append(escape(searchConditionDTO.getSearchWord())).append(")");
      query.setQuery(QueryString.toString());
    } else {
      query.setQuery("*:*");
    }
    StringBuffer fQueryString = new StringBuffer();

    boolean filter = false;
    if (StringUtils.isNotBlank(searchConditionDTO.getVehicleBrand())) {
      LOG.debug("brand = {}", searchConditionDTO.getVehicleBrand());
      fQueryString.append("brand_exact:(\"").append(SolrQueryUtils.escape(searchConditionDTO.getVehicleBrand())).append("\")");
      filter = true;
    }
    if (StringUtils.isNotBlank(searchConditionDTO.getVehicleModel())) {
      LOG.debug("model = {}", searchConditionDTO.getVehicleModel());
      if (filter) {
        fQueryString.append(" AND ");
      }
      fQueryString.append("model_exact:(\"").append(SolrQueryUtils.escape(searchConditionDTO.getVehicleModel())).append("\")");
      filter = true;
    }

    if (StringUtils.isNotBlank(searchConditionDTO.getVehicleEngine())) {
      LOG.debug("engine = {}", searchConditionDTO.getVehicleEngine());
      if (filter) {
        fQueryString.append(" AND ");
      }
      fQueryString.append("engine_exact:(\"").append(SolrQueryUtils.escape(searchConditionDTO.getVehicleEngine())).append("\")");
      filter = true;
    }

    if (StringUtils.isNotBlank(searchConditionDTO.getVehicleYear())) {
      LOG.debug("year = {}", searchConditionDTO.getVehicleYear());
      if (filter) {
        fQueryString.append(" AND ");
      }
      fQueryString.append("year_exact:(\"").append(SolrQueryUtils.escape(searchConditionDTO.getVehicleYear())).append("\")");
      filter = true;
    }

    if (filter) {
      String fqString = fQueryString.toString();
      query.addFilterQuery(fqString);
    }
    query.addFilterQuery("doc_type:" + SolrClientHelper.BcgogoSolrDocumentType.VEHICLE_PROPERTY_SUGGESTION_DOC_TYPE.getValue());
    String fieldExact = searchField + "_exact";
    query.addFacetField(fieldExact).setFacetMinCount(1);
    int facetList = 10;
    if ("brand".equals(searchField)||"model".equals(searchField)) {
      facetList = SolrQueryUtils.getSelectOptionNumber();
    }
    query.setFacetLimit(facetList);
    if (useAnd) query.setParam("q.op", "AND");
    QueryResponse rsp = SolrClientHelper.getVehicleSolrClient().query(query);
    FacetField facetField = rsp.getFacetField(fieldExact);
    List<FacetField.Count> counts = facetField.getValues();
    List<String> results = new ArrayList<String>();
    if (counts != null) {
      for (FacetField.Count count : counts) {
        String name = count.getName();
        results.add(name);
        LOG.trace("result name: {}", name);
      }
    }
    if (results.size() == 0) {
      LOG.debug("did not find anything: {}");
      List<String> terms = queryTermsForVehicle(searchConditionDTO.getSearchWord(), searchField);
      StringBuilder stringBuilder = new StringBuilder();
      for (String term : terms) {
        stringBuilder.append(term).append(" ");
      }
      if (terms.size() > 0 && !stringBuilder.toString().trim().equals(searchConditionDTO.getSearchWord())) {
        searchConditionDTO.setSearchWord(stringBuilder.toString().trim());
        return getVehicleSuggestionListByKeywords(searchConditionDTO);
      }
    }
    LOG.debug("query {} done", searchConditionDTO.getSearchWord());
    return results;
  }

  private List<String> queryTermsForVehicle(String q, String field) throws Exception {

    SolrQuery query = new SolrQuery();
    query.setParam(CommonParams.QT, "/terms");
    query.setParam(TermsParams.TERMS, true);
    query.setParam(TermsParams.TERMS_LIMIT, "10");
    query.setParam(TermsParams.TERMS_FIELD, field);
    query.setParam(TermsParams.TERMS_PREFIX_STR, escape(q));
    query.addFilterQuery("doc_type:"+SolrClientHelper.BcgogoSolrDocumentType.VEHICLE_PROPERTY_SUGGESTION_DOC_TYPE.getValue());
    QueryResponse rsp = SolrClientHelper.getVehicleSolrClient().query(query);
    TermsResponse termsResponse = rsp.getTermsResponse();
    List<TermsResponse.Term> terms = termsResponse.getTerms(field);
    List<String> results = new ArrayList<String>();
    for (TermsResponse.Term term : terms) {
      String result = term.getTerm();
      results.add(result);
    }
    return results;
  }

  /**
   * 不去除空格
   */
  private String escape(String queryStr) {
    if (queryStr == null) return queryStr;
    if(queryStr.length()>=50) queryStr = queryStr.substring(0,50);
    return QueryParser.escape(queryStr);
  }




  @Override
  public VehicleSearchResultDTO queryVehicle(VehicleSearchConditionDTO vehicleSearchConditionDTO) throws Exception {
    SolrQuery query = new SolrQuery();
    if (vehicleSearchConditionDTO == null) throw new Exception("VehicleSearchConditionDTO is null.");
    if (vehicleSearchConditionDTO.getShopId() == null) throw new Exception("VehicleSearchConditionDTO shopId is null.");
    VehicleSearchResultDTO searchResultListDTO = new VehicleSearchResultDTO();
    StringBuilder qString = new StringBuilder();
    boolean started = false;


    started = generateStringNgramContinuousQuery("licence_no", vehicleSearchConditionDTO.getLicenceNo(), started, qString);
    started = generateStringNgramContinuousQuery("engine_no", vehicleSearchConditionDTO.getEngineNo(), started, qString);
    started = generateStringNgramContinuousQuery("chassis_number", vehicleSearchConditionDTO.getChassisNumber(), started, qString);
    started = generateStringNgramContinuousQuery("vehicle_brand", vehicleSearchConditionDTO.getVehicleBrand(), started, qString);
    started = generateStringNgramContinuousQuery("vehicle_model", vehicleSearchConditionDTO.getVehicleModel(), started, qString);
    started = generateStringNgramContinuousQuery("vehicle_color", vehicleSearchConditionDTO.getVehicleColor(), started, qString);

    if(vehicleSearchConditionDTO.getMaintainIntervalsMileage()!=null){
      started = generateRangeQuery("maintain_intervals_mileage",String.valueOf(vehicleSearchConditionDTO.getMaintainIntervalsMileage()*-1), vehicleSearchConditionDTO.getMaintainIntervalsMileage().toString(), started, qString,true);
    }

    if(vehicleSearchConditionDTO.getMaintainIntervalsDay()!=null){
      started = generateRangeQuery("maintain_time", DateUtil.getInnerDayTime(vehicleSearchConditionDTO.getMaintainIntervalsDay()*-1).toString(),String.valueOf(DateUtil.getInnerDayTime(vehicleSearchConditionDTO.getMaintainIntervalsDay())+DateUtil.DAY_MILLION_SECONDS-1), started, qString,true);
    }

    started = generateRangeQuery("vehicle_last_consume_time", vehicleSearchConditionDTO.getVehicleLastConsumeTimeStart() == null ? "" : vehicleSearchConditionDTO.getVehicleLastConsumeTimeStart().toString(),
        vehicleSearchConditionDTO.getVehicleLastConsumeTimeEnd() == null ? "" : vehicleSearchConditionDTO.getVehicleLastConsumeTimeEnd().toString(), started, qString,true);

    if (qString.length() == 0) {
      qString.append("*:*");
    }
    if (StringUtils.isNotBlank(vehicleSearchConditionDTO.getSort())) {
      query.setParam("sort", vehicleSearchConditionDTO.getSort());
    }
    query.setQuery(qString.toString());
//    query.setParam("debugQuery", "true");
    StringBuilder fQueryString = new StringBuilder();
    fQueryString.append("(shop_id:").append(vehicleSearchConditionDTO.getShopId()).append(" AND vehicle_status:").append(VehicleStatus.ENABLED.toString()).append(" AND doc_type:").append(SolrClientHelper.BcgogoSolrDocumentType.VEHICLE_DOC_TYPE.getValue()).append(")");
    if (ArrayUtils.contains(vehicleSearchConditionDTO.getSearchStrategies(), VehicleSearchConditionDTO.SearchStrategy.SEARCH_STRATEGY_HAS_MOBILE)) {
      fQueryString.append(" AND is_mobile_vehicle:1");
    }
    if (ArrayUtils.contains(vehicleSearchConditionDTO.getSearchStrategies(), VehicleSearchConditionDTO.SearchStrategy.SEARCH_STRATEGY_OBD)) {
      fQueryString.append(" AND obd_id:{0 TO *}");
    }
    query.addFilterQuery(fQueryString.toString());

    if(vehicleSearchConditionDTO.getJoinSearchConditionDTO()!=null){
      generateFilterJoinCustomerOrSupplierQuery(vehicleSearchConditionDTO.getJoinSearchConditionDTO(), query);
    }
    query.setParam("fl", "*,score");
    query.setStart(vehicleSearchConditionDTO.getStart());
    query.setRows(vehicleSearchConditionDTO.getRows());
    if (StringUtils.isNotBlank(vehicleSearchConditionDTO.getSort())) {
      query.setParam("sort", vehicleSearchConditionDTO.getSort());
    }
    if (vehicleSearchConditionDTO.getSearchStrategies()!=null
        && Arrays.asList(vehicleSearchConditionDTO.getSearchStrategies()).contains(VehicleSearchConditionDTO.SearchStrategy.SEARCH_STRATEGY_STATS_FACET)
        && !ArrayUtils.isEmpty(vehicleSearchConditionDTO.getStatsFields())
        && !ArrayUtils.isEmpty(vehicleSearchConditionDTO.getFacetFields())) {
      query.setFacet(true);
      query.setParam(StatsParams.STATS, "true");
      query.setParam(StatsParams.STATS_FIELD, vehicleSearchConditionDTO.getStatsFields());
      query.setParam(StatsParams.STATS_FACET, vehicleSearchConditionDTO.getFacetFields());
    } else if (vehicleSearchConditionDTO.getSearchStrategies()!=null
        && Arrays.asList(vehicleSearchConditionDTO.getSearchStrategies()).contains(VehicleSearchConditionDTO.SearchStrategy.SEARCH_STRATEGY_STATS)
        && !ArrayUtils.isEmpty(vehicleSearchConditionDTO.getStatsFields())) {
      query.setParam(StatsParams.STATS, "true");
      query.setParam(StatsParams.STATS_FIELD, vehicleSearchConditionDTO.getStatsFields());
    }
    QueryResponse response = SolrClientHelper.getVehicleSolrClient().query(query);


    if (vehicleSearchConditionDTO.getSearchStrategies()!=null
        && Arrays.asList(vehicleSearchConditionDTO.getSearchStrategies()).contains(VehicleSearchConditionDTO.SearchStrategy.SEARCH_STRATEGY_STATS_FACET)
        && !ArrayUtils.isEmpty(vehicleSearchConditionDTO.getStatsFields())
        && !ArrayUtils.isEmpty(vehicleSearchConditionDTO.getFacetFields())) {
      Map<String, FieldStatsInfo> statsInfoMap = response.getFieldStatsInfo();
      if (statsInfoMap!=null && !statsInfoMap.isEmpty()) {
        Map<String, Long> statCounts = new HashMap<String, Long>();
        Map<String, Long> statNotNullCounts = new HashMap<String, Long>();
        Map<String, Double> statSums = new HashMap<String, Double>();
        StringBuilder key = null;//statsField_facetField_facetName
        FieldStatsInfo statsInfo = null;
        Map<String, List<FieldStatsInfo>> statsInfoFacetMap = null;
        for (String statsField : vehicleSearchConditionDTO.getStatsFields()) {
          statsInfo = statsInfoMap.get(statsField);
          if (statsInfo != null && !statsInfo.getFacets().isEmpty()) {
            statsInfoFacetMap = statsInfo.getFacets();
            for (String facetField : vehicleSearchConditionDTO.getFacetFields()) {
              List<FieldStatsInfo> statsInfoList = statsInfoFacetMap.get(facetField);
              for (FieldStatsInfo stats : statsInfoList) {
                String facetName = stats.getName();
                double sum = NumberUtil.round((Double) stats.getSum(), 2);
                key = new StringBuilder(statsField).append(KEY_INVERVAL).append(facetField).append(KEY_INVERVAL).append(facetName);
                statSums.put(key.toString().toUpperCase(), sum);
                long count = NumberUtil.longValue(stats.getCount())+NumberUtil.longValue(stats.getMissing());
                statCounts.put(key.toString().toUpperCase(), count);
                statNotNullCounts.put(key.toString().toUpperCase(), NumberUtil.longValue(stats.getCount()));
              }
            }
          }
        }
        searchResultListDTO.setStatNotNullCounts(statNotNullCounts);
        searchResultListDTO.setStatCounts(statCounts);
        searchResultListDTO.setStatAmounts(statSums);
      }
    } else if (vehicleSearchConditionDTO.getSearchStrategies()!=null
        && Arrays.asList(vehicleSearchConditionDTO.getSearchStrategies()).contains(VehicleSearchConditionDTO.SearchStrategy.SEARCH_STRATEGY_STATS)
        && !ArrayUtils.isEmpty(vehicleSearchConditionDTO.getStatsFields())) {
      Map<String, FieldStatsInfo> statsInfoMap = response.getFieldStatsInfo();
      if (statsInfoMap!=null && !statsInfoMap.isEmpty()) {
        Map<String, Long> statCounts = new HashMap<String, Long>();
        Map<String, Long> statNotNullCounts = new HashMap<String, Long>();
        Map<String, Double> statSums = new HashMap<String, Double>();
        for (String statsField : vehicleSearchConditionDTO.getStatsFields()) {
          FieldStatsInfo statsInfo = response.getFieldStatsInfo().get(statsField);
          if (statsInfo != null) {
            statNotNullCounts.put(statsField.toUpperCase(),NumberUtil.longValue(statsInfo.getCount()));
            statCounts.put(statsField.toUpperCase(),NumberUtil.longValue(statsInfo.getCount())+NumberUtil.longValue(statsInfo.getMissing()));
            statSums.put(statsField.toUpperCase(), NumberUtil.round((Double)statsInfo.getSum(), 2));
          }
        }
        searchResultListDTO.setStatNotNullCounts(statNotNullCounts);
        searchResultListDTO.setStatCounts(statCounts);
        searchResultListDTO.setStatAmounts(statSums);
      }
    }


    SolrDocumentList docs = response.getResults();
    searchResultListDTO.setNumFound(docs.getNumFound());
    VehicleDTO vehicleDTO = null;
    if (CollectionUtils.isNotEmpty(docs)) {
      for (SolrDocument document : docs) {
        vehicleDTO = new VehicleDTO();
        Long id = NumberUtil.longValue(document.getFirstValue("id"));
        String licenceNo = (String) document.getFirstValue("licence_no");
        String vehicleContact = (String) document.getFirstValue("vehicle_contact");
        String vehicleMobile = (String) document.getFirstValue("vehicle_mobile");
        String vehicleModel = (String) document.getFirstValue("vehicle_model");
        String vehicleBrand = (String) document.getFirstValue("vehicle_brand");
        String vehicleColor = (String) document.getFirstValue("vehicle_color");
        Double obdMileage = (Double) document.getFirstValue("obd_mileage");
        Double maintainMileage = (Double) document.getFirstValue("maintain_mileage");
        Double maintainIntervalsMileage = (Double) document.getFirstValue("maintain_intervals_mileage");
        Long maintainTime = (Long) document.getFirstValue("maintain_time");
        Long insureTime = (Long) document.getFirstValue("insure_time");

        Integer vehicleTotalConsumeCount = (Integer) document.getFirstValue("vehicle_total_consume_count");
        Double vehicleTotalConsumeAmount = (Double) document.getFirstValue("vehicle_total_consume_amount");
        Long vehicleLastConsumeTime = (Long) document.getFirstValue("vehicle_last_consume_time");

        String lastConsumeOrderType = (String) document.getFirstValue("last_consume_order_type");
        String gsmObdImei = (String) document.getFirstValue("gsm_obd_imei");
        String gsmObdImeiMobile = (String) document.getFirstValue("gsm_obd_imei_mobile");
        Long lastConsumeOrderId = (Long) document.getFirstValue("last_consume_order_id");

        Long obdId = (Long) document.getFirstValue("obd_id");

        Long customerId = NumberUtil.longValue(document.getFirstValue("customer_id"));

        vehicleDTO.setId(id);
        vehicleDTO.setLicenceNo(licenceNo);
        vehicleDTO.setContact(vehicleContact);
        vehicleDTO.setMobile(vehicleMobile);
        vehicleDTO.setModel(vehicleModel);
        vehicleDTO.setBrand(vehicleBrand);
        vehicleDTO.setColor(vehicleColor);
        if(maintainMileage!=null){
          vehicleDTO.setMaintainMileage(maintainMileage.longValue());
        }
        vehicleDTO.setMaintainTime(maintainTime);
        vehicleDTO.setInsureTime(insureTime);
        vehicleDTO.setMaintainIntervalsMileage(maintainIntervalsMileage);
        vehicleDTO.setObdMileage(obdMileage);

        vehicleDTO.setObdId(obdId);
        vehicleDTO.setGsmObdImei(gsmObdImei);
        vehicleDTO.setGsmObdImeiMoblie(gsmObdImeiMobile);

        vehicleDTO.setCustomerId(customerId);
        vehicleDTO.setVehicleTotalConsumeAmount(vehicleTotalConsumeAmount);
        vehicleDTO.setVehicleTotalConsumeCount(vehicleTotalConsumeCount);
        vehicleDTO.setVehicleLastConsumeTime(vehicleLastConsumeTime);
        vehicleDTO.setLastConsumeOrderId(lastConsumeOrderId);
        vehicleDTO.setLastConsumeOrderType(StringUtils.isNotBlank(lastConsumeOrderType) ? OrderTypes.valueOf(lastConsumeOrderType):null);
        searchResultListDTO.getVehicleDTOList().add(vehicleDTO);
      }
    }
    return searchResultListDTO;
  }
  //join
  private void generateFilterJoinCustomerOrSupplierQuery(JoinSearchConditionDTO joinSearchConditionDTO,SolrQuery query) {
    if(joinSearchConditionDTO==null) return;
    StringBuilder joinQueryString = new StringBuilder();
    boolean started = false;
    if(SolrClientHelper.BcgogoSolrCore.CUSTOMER_SUPPLIER_CORE.getValue().equals(joinSearchConditionDTO.getFromIndex())){
      if(joinSearchConditionDTO.getShopId()!=null){
        joinQueryString.append("shop_id:").append(joinSearchConditionDTO.getShopId());
        started = true;
      }
      String customerOrSupplierInfo = SolrQueryUtils.escape(joinSearchConditionDTO.getCustomerOrSupplierInfo());
      if(StringUtils.isNotBlank(joinSearchConditionDTO.getCustomerOrSupplierInfo())){
        if (started) {
          joinQueryString.append(" AND ");
        }
        joinQueryString.append(" (name_ngram").append(":").append(customerOrSupplierInfo);
        joinQueryString.append(" OR contact_ngram").append(":").append(customerOrSupplierInfo);
        joinQueryString.append(" OR mobile_ngram").append(":").append(customerOrSupplierInfo);
        joinQueryString.append(" OR member_no_ngram").append(":").append(customerOrSupplierInfo).append(")");
        started = true;
      }
    }
    StringBuilder qString = new StringBuilder();
    if(StringUtils.isNotBlank(joinQueryString.toString())){
      qString.append("{!join from=").append(joinSearchConditionDTO.getFromColumn()).append(" to=").append(joinSearchConditionDTO.getToColumn()).append(" fromIndex=").append(joinSearchConditionDTO.getFromIndex()).append("}");
      qString.append("(").append(joinQueryString).append(")");
      query.addFilterQuery(qString.toString());
    }
  }

  private boolean generateStringNgramContinuousQuery(String field, String value, boolean started, StringBuilder qString) {
    if (!StringUtils.isBlank(value)) {
      String escapedValue = SolrQueryUtils.escape(value);
      if (started) {
        qString.append(" AND ");
      }
      qString.append("(");
      qString.append(field).append(":").append("(\"").append(escapedValue).append("\")^10 ");
      qString.append(" OR ").append(field).append("_ngram_continuous:").append("(").append(escapedValue).append(")");
      qString.append(")");
      started = true;
    }
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

  //默认闭区间 通过region判断区间开闭
  private boolean generateRangeQuery(String field, String start, String end, boolean started,StringBuilder qString,boolean isClose) {
    if (StringUtils.isBlank(start) && StringUtils.isBlank(end)) return started;
    if (started) {
      qString.append(" AND ");
    }
    if (StringUtils.isNotBlank(start) && StringUtils.isNotBlank(end)) {
      qString.append(field).append(":").append(isClose ? "[" : "{").append(start).append(" TO ").append(end).append(isClose ? "]" : "}");
    } else if (StringUtils.isNotBlank(start)) {
      qString.append(field).append(":").append(isClose ? "[" : "{").append(start).append(" TO *").append(isClose ? "]" : "}");
    } else {
      qString.append(field).append(":").append(isClose ? "[" : "{").append("* TO ").append(end).append(isClose ? "]" : "}");
    }
    started = true;
    return started;
  }


  /**
   * 发动机号 车架号建议下拉
   *
   * @param shopId
   * @param searchWord
   * @return
   * @throws Exception
   */
  public List<Map> getVehicleEngineNoClassNoSuggestion(Long shopId, String searchWord, String searchField) throws Exception {
    if (shopId == null || StringUtils.isEmpty(searchField) || !("engine_no".equals(searchField) || "chassis_number".equals(searchField) ||
        "gsm_obd_imei".equals(searchField)  || "gsm_obd_imei_mobile".equals(searchField)|| "licence_no".equals(searchField))) {
      throw new BcgogoException("shopId searchField nullPointException!");
    }
    StringBuilder qString = new StringBuilder();

    if (StringUtils.isNotBlank(searchWord)) {

      searchWord = SolrQueryUtils.escape(searchWord);
      if ("engine_no".equals(searchField)) {
        qString.append("engine_no:").append("(\"").append(searchWord).append("\"").append(")^100 ");
        qString.append(" OR ").append("engine_no_start:").append("(").append(searchWord).append(")^50 ");
        qString.append(" OR ").append("engine_no_end:").append("(").append(searchWord).append(")^40 ");
        qString.append(" OR ").append("engine_no_contained:").append("(").append(searchWord).append(")^30 ");
      } else if ("chassis_number".equals(searchField)) {
        qString.append("chassis_number:").append("(\"").append(searchWord).append("\"").append(")^100 ");
        qString.append(" OR ").append("chassis_number_start:").append("(").append(searchWord).append(")^50 ");
        qString.append(" OR ").append("chassis_number_end:").append("(").append(searchWord).append(")^40 ");
        qString.append(" OR ").append("chassis_number_contained:").append("(").append(searchWord).append(")^30 ");
      }else if ("gsm_obd_imei".equals(searchField)) {
        qString.append("gsm_obd_imei:").append("(\"").append(searchWord).append("\"").append(")^100 ");
        qString.append(" OR ").append("gsm_obd_imei_start:").append("(").append(searchWord).append(")^50 ");
        qString.append(" OR ").append("gsm_obd_imei_end:").append("(").append(searchWord).append(")^40 ");
        qString.append(" OR ").append("gsm_obd_imei_contained:").append("(").append(searchWord).append(")^30 ");
      }else if ("gsm_obd_imei_mobile".equals(searchField)) {
        qString.append("gsm_obd_imei_mobile:").append("(\"").append(searchWord).append("\"").append(")^100 ");
        qString.append(" OR ").append("gsm_obd_imei_mobile_start:").append("(").append(searchWord).append(")^50 ");
        qString.append(" OR ").append("gsm_obd_imei_mobile_end:").append("(").append(searchWord).append(")^40 ");
        qString.append(" OR ").append("gsm_obd_imei_mobile_contained:").append("(").append(searchWord).append(")^30 ");
      }else if ("licence_no".equals(searchField)) {
        qString.append("licence_no:").append("(\"").append(searchWord).append("\"").append(")^100 ");
        qString.append(" OR ").append("licence_no_start:").append("(").append(searchWord).append(")^50 ");
        qString.append(" OR ").append("licence_no_end:").append("(").append(searchWord).append(")^40 ");
        qString.append(" OR ").append("licence_no_contained:").append("(").append(searchWord).append(")^30 ");
        qString.append(" OR ").append("licence_no_fl_start:").append("(").append(searchWord).append(")^20 ");
        qString.append(" OR ").append("licence_no_fl_end:").append("(").append(searchWord).append(")^10 ");
        qString.append(" OR ").append("licence_no_fl_contained:").append("(").append(searchWord).append(") ");
      }
    }
    SolrQuery query = new SolrQuery();
    if (qString.length() == 0) {
      qString.append("*:*");
    }
    query.setQuery(qString.toString());
    StringBuilder fQueryString = new StringBuilder();
    fQueryString.append("(shop_id:").append(shopId.toString()).append(" AND vehicle_status:").append(VehicleStatus.ENABLED.toString()).append(" AND doc_type:").append(SolrClientHelper.BcgogoSolrDocumentType.VEHICLE_DOC_TYPE.getValue());
    if ("engine_no".equals(searchField)) {
      fQueryString.append(" AND engine_no:[\"\" TO *]");
    } else if ("chassis_number".equals(searchField)) {
      fQueryString.append(" AND chassis_number:[\"\" TO *]");
    } else if ("gsm_obd_imei".equals(searchField)) {
      fQueryString.append(" AND gsm_obd_imei:[\"\" TO *]");
    }else if ("gsm_obd_imei_mobile".equals(searchField)) {
      fQueryString.append(" AND gsm_obd_imei_mobile:[\"\" TO *]");
    }else if ("licence_no".equals(searchField)) {
      fQueryString.append(" AND licence_no:[\"\" TO *]");
    }
    fQueryString.append(" AND obd_id:[0 TO *]");

    fQueryString.append(")");


    query.setFilterQueries(fQueryString.toString());
    query.setParam("fl", "id,engine_no,chassis_number,gsm_obd_imei,gsm_obd_imei_mobile,licence_no,score");

    query.setStart(0);
    query.setRows(50);
    QueryResponse rsp = SolrClientHelper.getVehicleSolrClient().query(query);
    SolrDocumentList documents = rsp.getResults();

    List<Map> dropDownList = new ArrayList<Map>();

    Map<String, Object> dropDownItem = null;
    Map<String, String> propertyMap = null;

    for (SolrDocument document : documents) {
      dropDownItem = new HashMap<String, Object>();
      propertyMap = new HashMap<String, String>();
      propertyMap.put("id", (String) document.getFirstValue("id"));
      dropDownItem.put("label",  (String) document.getFirstValue(searchField));
      dropDownItem.put("details", propertyMap);
      dropDownItem.put("type", "option");  //目前只使用   option  （category）暂时不用
      dropDownList.add(dropDownItem);

    }
    return dropDownList;
  }


  @Override
  public Map<String,String> queryVehicleNoForVehiclePosition(VehicleSearchConditionDTO vehicleSearchConditionDTO) throws Exception {
    SolrQuery query = new SolrQuery();
    if (vehicleSearchConditionDTO == null) throw new Exception("VehicleSearchConditionDTO is null.");
    if (vehicleSearchConditionDTO.getShopId() == null) throw new Exception("VehicleSearchConditionDTO shopId is null.");
    StringBuilder qString = new StringBuilder();
    boolean started = false;


    started = generateStringNgramContinuousQuery("licence_no", vehicleSearchConditionDTO.getLicenceNo(), started, qString);
    started = generateStringNgramContinuousQuery("engine_no", vehicleSearchConditionDTO.getEngineNo(), started, qString);
    started = generateStringNgramContinuousQuery("chassis_number", vehicleSearchConditionDTO.getChassisNumber(), started, qString);
    started = generateStringNgramContinuousQuery("gsm_obd_imei", vehicleSearchConditionDTO.getGsmObdImei(), started, qString);
    started = generateStringNgramContinuousQuery("gsm_obd_imei_mobile", vehicleSearchConditionDTO.getGsmObdImeiMoblie(), started, qString);


    if (qString.length() == 0) {
      qString.append("*:*");
    }
    query.setQuery(qString.toString());
    StringBuilder fQueryString = new StringBuilder();
    fQueryString.append("(shop_id:").append(vehicleSearchConditionDTO.getShopId()).append(" AND vehicle_status:").append(VehicleStatus.ENABLED.toString()).append(" AND doc_type:").append(SolrClientHelper.BcgogoSolrDocumentType.VEHICLE_DOC_TYPE.getValue()).append(")");
    if (ArrayUtils.contains(vehicleSearchConditionDTO.getSearchStrategies(), VehicleSearchConditionDTO.SearchStrategy.SEARCH_STRATEGY_HAS_MOBILE)) {
      fQueryString.append(" AND is_mobile_vehicle:1");
    }
    if (ArrayUtils.contains(vehicleSearchConditionDTO.getSearchStrategies(), VehicleSearchConditionDTO.SearchStrategy.SEARCH_STRATEGY_OBD)) {
      fQueryString.append(" AND obd_id:{0 TO *}");
    }
    query.addFilterQuery(fQueryString.toString());

    if (vehicleSearchConditionDTO.getJoinSearchConditionDTO() != null) {
      generateFilterJoinCustomerOrSupplierQuery(vehicleSearchConditionDTO.getJoinSearchConditionDTO(), query);
    }
    query.setParam("fl", "*,score");
    if (StringUtils.isNotBlank(vehicleSearchConditionDTO.getSort())) {
      query.setParam("sort", vehicleSearchConditionDTO.getSort());
    }

    QueryResponse response = SolrClientHelper.getVehicleSolrClient().query(query);

    SolrDocumentList docs = response.getResults();
    Map<String,String> imeiMap = new HashMap<String, String>();

    if (CollectionUtils.isNotEmpty(docs)) {
      for (SolrDocument document : docs) {
        String imei = (String) document.getFirstValue("gsm_obd_imei");
        String vehicleNo = (String) document.getFirstValue("licence_no");
        if (StringUtils.isEmpty(imei) || StringUtils.isEmpty(vehicleNo)) {
          continue;
        }

        imeiMap.put(imei,vehicleNo);

      }
    }
    return imeiMap;
  }
}