package com.bcgogo.search.service.order;

import com.bcgogo.enums.ItemTypes;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.enums.txn.preBuyOrder.QuotedResult;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.search.client.SolrClientHelper;
import com.bcgogo.search.dto.*;
import com.bcgogo.search.service.CurrentUsed.IProductCurrentUsedService;
import com.bcgogo.search.util.SolrQueryUtils;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.utils.ArrayUtil;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.StringUtil;
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

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: caiweili
 * Date: 8/2/12
 * Time: 2:50 PM
 */
@Component
public class SearchOrderService implements ISearchOrderService {
  private static final Logger LOG = LoggerFactory.getLogger(SearchOrderService.class);
  private static final int MAX_QUERY_TIMES = 15;
  private static final String KEY_INVERVAL="_";

  @Override
  public OrderSearchResultListDTO queryOrders(OrderSearchConditionDTO conditions) throws Exception {
    boolean started = false;
    StringBuilder qString = new StringBuilder();
    started = generateProductRelatedQuery(conditions, started, qString,true);
    started = generateMemberNoRelatedQuery(conditions, started, qString);
    //是否包含作废
    if (OrderSearchConditionDTO.ORDER_NOT_CANCEL.equals(conditions.getOrderStatusRepeal())) {
      if (started) qString.append(" AND ");
      qString.append(" -order_status:(PURCHASE_INVENTORY_REPEAL OR PURCHASE_ORDER_REPEAL OR SALE_REPEAL OR REPAIR_REPEAL OR WASH_REPEAL OR REPEAL) ");
      started = true;
    }
    started = generateStringArrayRelatedQuery("order_status", conditions.getOrderStatus(), started, qString);
    started = generateStringArrayRelatedQuery("pay_method", conditions.getPayMethod(), started, qString);
    started = generateStringArrayRelatedQuery("sales_man", conditions.getSalesman(), started, qString);
    started = generateStringArrayRelatedQuery("service_worker", conditions.getServiceWorker(), started, qString);

    started = generateFuzzyRelatedQuery("receipt_no", conditions.getReceiptNo(), started, qString);

//    started = generateFuzzyRelatedQuery("member_no", conditions.getMemberNo(), started, qString);
    started = generateStringRelatedQuery("member_type", conditions.getMemberType(), started, qString);
    started = generateStringRelatedQuery("pay_per_project", conditions.getPayPerProject(), started, qString);   //计次收费项目
    started = generateStringRelatedQuery("coupon_type", conditions.getCouponType(), started, qString);   //消费券
    if (conditions.getProvince()!=null || conditions.getCity()!=null || conditions.getRegion()!=null) {
      Long customerOrSupplierAreaId = conditions.getRegion()!=null?conditions.getRegion():(conditions.getCity()!=null?conditions.getCity():conditions.getProvince());
      started = generateLongRelatedQuery("customer_or_supplier_area_ids",customerOrSupplierAreaId, started, qString);
    }
    //会员消费统计
//    started = generateStringRelatedQuery("account_member_no", conditions.getAccountMemberNo(), started, qString);

    //应收应付统计
    if (StringUtils.isNotEmpty(conditions.getDebtType())) {
      started = generateStringRelatedQuery("debt_type", conditions.getDebtType(), started, qString);
    }

    started = generateFuzzyRelatedQuery("contact_num", conditions.getContactNum(), started, qString);

    started = generateFuzzyRelatedQuery("operator", conditions.getOperator(), started, qString);

    started = generateLongRelatedQuery("operator_id",conditions.getOperatorId(),started, qString);

    started = generateStringRelatedQuery("customer_or_supplier_id", conditions.getCustomerOrSupplierId(), started, qString);

    started = generateStringArrayRelatedQuery("customer_or_supplier_id", conditions.getCustomerOrSupplierIds(), started, qString);

    started = generateFuzzyRelatedQuery("customer_or_supplier_name",  conditions.getCustomerOrSupplierName(), started, qString);
    started = generateFuzzyRelatedQuery("contact", conditions.getContact(), started, qString);
    if(ArrayUtil.isNotEmpty(conditions.getVehicleList())){
      started = generateStringArrayRelatedQuery("vehicle", conditions.getVehicleList(), started, qString);
    }else {
      started = generateFuzzyRelatedQuery("vehicle", conditions.getVehicle(), started, qString);
    }
    started = generateFuzzyRelatedQuery("vehicle_brand", conditions.getvBrand(), started, qString);
    started = generateFuzzyRelatedQuery("vehicle_model", conditions.getvModel(), started, qString);
    started = generateFuzzyRelatedQuery("vehicle_color", conditions.getvColor(), started, qString);
    started = generateMultifieldQuery("service", conditions.getService(), started, qString);

    started = generateLongRelatedQuery("id", conditions.getOrderId(), started, qString);

    started = generateRangeQuery("created_time", conditions.getStartTime() == null ? null : conditions.getStartTime().toString(),
        conditions.getEndTime() == null ? null : conditions.getEndTime().toString(), started, qString,true);

    started = generateRangeQuery("payment_time", conditions.getPaymentTimeStart() == null ? null : conditions.getPaymentTimeStart().toString(),
        conditions.getPaymentTimeEnd() == null ? null : conditions.getPaymentTimeEnd().toString(), started, qString,true);

    started = generateRangeQuery("inventory_vest_time", conditions.getInventoryVestStartDate() == null ? null : conditions.getInventoryVestStartDate().toString(),
        conditions.getInventoryVestEndDate() == null ? null : conditions.getInventoryVestEndDate().toString(), started, qString,true);

    started = generateRangeQuery("order_total_amount", conditions.getAmountLower() == null ? null : conditions.getAmountLower().toString(),
        conditions.getAmountUpper() == null ? null : conditions.getAmountUpper().toString(), started, qString,true);
    started = generateStringArrayRelatedQuery("customer_or_supplier_shop_id",conditions.getCustomerOrSupplierShopIds(), started, qString);

    if(OrderSearchConditionDTO.PreBuyOrderStatus.VALID.equals(conditions.getPreBuyOrderStatus())){
      started = generateRangeQuery("end_time",DateUtil.getTheDayTime().toString(),null, started, qString,true);
    }else if(OrderSearchConditionDTO.PreBuyOrderStatus.EXPIRED.equals(conditions.getPreBuyOrderStatus())){
      started = generateRangeQuery("end_time",null,DateUtil.getTheDayTime().toString(), started, qString,false);
    }

    if (conditions.getProvinceNo()!=null || conditions.getCityNo()!=null || conditions.getRegionNo()!=null) {
      Long shopAreaId = conditions.getRegionNo()!=null?conditions.getRegionNo():(conditions.getCityNo()!=null?conditions.getCityNo():conditions.getProvinceNo());
      started = generateLongRelatedQuery("shop_area_ids",shopAreaId, started, qString);
    }
    if (!ArrayUtils.isEmpty(conditions.getExcludeShopIds())) {
      if (started) {
        qString.append(" AND ");
      }
      qString.append("!shop_id:(");
      for (int i = 0, max = conditions.getExcludeShopIds().length; i < max; i++) {
        qString.append(conditions.getExcludeShopIds()[i]);
        if (i < (max - 1)) qString.append(" OR ");
      }
      qString.append(")");
      started = true;
    }

    started = generateBooleanQuery("unpaid", conditions.getNotPaid(), started, qString);
    if(!ArrayUtils.isEmpty(conditions.getCustomerShopIds()) || StringUtil.isNotEmpty(conditions.getShopName())) {
      started = generateShopNameAndCustomerShopIdQuery(conditions.getCustomerShopIds(), conditions.getShopName(), conditions.getExactSearch(), started, qString);
    } else {
      started = generateFuzzyCustomerOrSupplierInfoQuery(conditions.getCustomerOrSupplierInfo(), started, qString);
    }
    started = generateSearchStrategyQuery(conditions.getSearchStrategy(), started, qString);


    if (qString.length() == 0) {
     qString.append("*:*");
   }
    SolrQuery query = new SolrQuery();
    query.setQuery(qString.toString());

    StringBuilder fQueryString = new StringBuilder();
    if (ArrayUtils.isEmpty(conditions.getOrderType())) throw new BcgogoException("order type nullPointException!");
    generateStringArrayRelatedQuery("order_type", conditions.getOrderType(), false, fQueryString);


    if(!ArrayUtil.contains(conditions.getSearchStrategy(),OrderSearchConditionDTO.SEARCHSTRATEGY_NO_SHOP_RESTRICT)){
         if (conditions.getShopId() == null) throw new BcgogoException("shopId nullPointException!");
    }
//    if(!(ArrayUtil.isNotEmpty(conditions.getSearchStrategy()) && Arrays.asList(conditions.getSearchStrategy()).contains(OrderSearchConditionDTO.SEARCHSTRATEGY_NO_SHOP_RESTRICT))){
//      if (conditions.getShopId() == null) throw new BcgogoException("shopId nullPointException!");
//    }else{
//      if (conditions.getShopKind() ==null) throw new BcgogoException("shopKind nullPointException!");
//    }
    if(conditions.getShopId()!=null){
      fQueryString.append(" AND shop_id:").append(conditions.getShopId());
    }

    if(conditions.getShopKind() !=null){
      fQueryString.append(" AND shop_kind:").append(conditions.getShopKind());
    }


    query.setFilterQueries(fQueryString.toString());

    query.setParam("fl", "*,score");
    query.setStart(conditions.getRowStart());
    query.setRows(conditions.getPageRows());
    query.setParam("q.op", "AND");

    if (conditions.getSearchStrategy()!=null
        && Arrays.asList(conditions.getSearchStrategy()).contains(OrderSearchConditionDTO.SEARCHSTRATEGY_STATS_FACET)
        && !ArrayUtils.isEmpty(conditions.getStatsFields())
        && !ArrayUtils.isEmpty(conditions.getFacetFields())) {
      query.setFacet(true);
      query.setParam(StatsParams.STATS, "true");
      query.setParam(StatsParams.STATS_FIELD, conditions.getStatsFields());
      query.setParam(StatsParams.STATS_FACET, conditions.getFacetFields());
    } else if (conditions.getSearchStrategy()!=null
        && Arrays.asList(conditions.getSearchStrategy()).contains(OrderSearchConditionDTO.SEARCHSTRATEGY_STATS)
        && !ArrayUtils.isEmpty(conditions.getStatsFields())) {
      query.setParam(StatsParams.STATS, "true");
      query.setParam(StatsParams.STATS_FIELD, conditions.getStatsFields());
    }
    if (StringUtils.isNotBlank(conditions.getSort())) {
      query.setParam("sort", conditions.getSort());
    }
    QueryResponse response = SolrClientHelper.getOrderSolrClient().query(query);
    return processSearchResponse(response, conditions);
  }



  private OrderSearchResultListDTO processSearchResponse(QueryResponse response, OrderSearchConditionDTO conditions) {
    OrderSearchResultListDTO resultListDTO = new OrderSearchResultListDTO();
    generateStatInfo(response, conditions, resultListDTO);

    SolrDocumentList docs = response.getResults();
    resultListDTO.setNumFound(docs.getNumFound());
    for (SolrDocument doc : docs) {
      OrderSearchResultDTO resultDTO = new OrderSearchResultDTO();
      if (doc.getFirstValue("order_total_amount") != null){
        resultDTO.setAmount((Double) doc.getFirstValue("order_total_amount"));
      }else {
        resultDTO.setAmount(0d);
      }
      if (doc.getFirstValue("after_member_discount_total") != null){
        resultDTO.setAfterMemberDiscountTotal((Double) doc.getFirstValue("after_member_discount_total"));
      }else {
        resultDTO.setAfterMemberDiscountTotal(0d);
      }
      if (doc.getFirstValue("order_settled_amount") != null){
        resultDTO.setSettled((Double) doc.getFirstValue("order_settled_amount"));
      }else {
        resultDTO.setSettled(0d);
      }
      if (doc.getFirstValue("order_debt_amount") != null){
        resultDTO.setDebt((Double) doc.getFirstValue("order_debt_amount"));
      }else{
        resultDTO.setDebt(0d);
      }
      if (doc.getFirstValue("total_cost_price") != null){
        resultDTO.setTotalCostPrice((Double) doc.getFirstValue("total_cost_price"));
      }else {
        resultDTO.setTotalCostPrice(0d);
      }
      if (doc.getFirstValue("discount") != null){
        resultDTO.setDiscount((Double) doc.getFirstValue("discount"));
      }else{
        resultDTO.setDiscount(0d);
      }
      if (doc.getFirstValue("gross_profit") != null){
        resultDTO.setGrossProfit((Double) doc.getFirstValue("gross_profit"));
      }else{
        resultDTO.setGrossProfit(0d);
      }
      if(NumberUtil.round(resultDTO.getSettled()+resultDTO.getDebt(),NumberUtil.MONEY_PRECISION)==0d){
        resultDTO.setGrossProfitRate(0d);
      }else{
        resultDTO.setGrossProfitRate(NumberUtil.round(resultDTO.getGrossProfit()*100/(resultDTO.getSettled()+resultDTO.getDebt()),1));
      }

      //会员消费统计
      if (doc.getFirstValue("member_balance_pay") != null){
        resultDTO.setMemberBalancePay((Double) doc.getFirstValue("member_balance_pay"));
      }else{
        resultDTO.setMemberBalancePay(0d);
      }
      resultDTO.setAccountMemberNo((String) doc.getFirstValue("account_member_no"));

      resultDTO.setContactNum((String) doc.getFirstValue("contact_num"));
      resultDTO.setCreatedTime(NumberUtil.longValue(doc.getFirstValue("created_time")));
      resultDTO.setCustomerOrSupplierName((String) doc.getFirstValue("customer_or_supplier_name"));
      if(doc.getFirstValue("customer_or_supplier_id")!=null){
        resultDTO.setCustomerOrSupplierId(Long.parseLong(doc.getFirstValue("customer_or_supplier_id").toString()));
      }
      resultDTO.setContact((String) doc.getFirstValue("contact"));
      resultDTO.setMemberNo((String) doc.getFirstValue("member_no"));
      resultDTO.setMemberStatus((String) doc.getFirstValue("member_status"));
      resultDTO.setMemberLastRecharge((Double) doc.getFirstValue("member_last_recharge"));
      resultDTO.setMemberType((String) doc.getFirstValue("member_type"));
      resultDTO.setMemberLastBuyTotal((Double) doc.getFirstValue("member_last_buy_total"));
      resultDTO.setMemberLastBuyDate(NumberUtil.longValue(doc.getFirstValue("member_last_buy_date")));
      if (doc.getFirstValue("member_balance") != null) resultDTO.setMemberBalance((Double) doc.getFirstValue("member_balance"));
      if (doc.getFirstValue("worth") != null) resultDTO.setWorth((Double) doc.getFirstValue("worth"));
      resultDTO.setAddress((String) doc.getFirstValue("address"));
      resultDTO.setVestDate(NumberUtil.longValue(doc.getFirstValue("created_time")));
      resultDTO.setEndDate(NumberUtil.longValue(doc.getFirstValue("end_time")));
      resultDTO.setReceiptNo((String)doc.getFieldValue("receipt_no"));
      resultDTO.setNotPaid((Boolean) doc.getFirstValue("unpaid"));
      resultDTO.setOrderId(NumberUtil.longValue(doc.getFirstValue("id")));
      resultDTO.setOrderStatus((String) doc.getFirstValue("order_status"));
      resultDTO.setPaymentTime(NumberUtil.longValue(doc.getFirstValue("payment_time")));
      Collection<Object> values = doc.getFieldValues("pay_method");
      if (values != null) {
        String[] str = new String[values.size()];
        int i = 0;
        for (Object o : values) {
          str[i++] = (String) o;
        }
        resultDTO.setPayMethod(str);
      }
      resultDTO.setShopId(NumberUtil.longValue(doc.getFirstValue("shop_id")));
      resultDTO.setOrderType((String) doc.getFirstValue("order_type"));
      resultDTO.setMemo((String) doc.getFirstValue("memo"));
      resultDTO.setvBrand((String) doc.getFirstValue("vehicle_brand"));
      resultDTO.setvModel((String) doc.getFirstValue("vehicle_model"));
      resultDTO.setVehicle((String) doc.getFirstValue("vehicle"));
      resultDTO.setOrderContent((String) doc.getFirstValue("order_content"));
      resultDTO.setMemberDiscountRatio((Double)doc.getFieldValue("member_discount_ratio"));
      resultDTO.setAfterMemberDiscountTotal((Double)doc.getFieldValue("after_member_discount_total"));
      resultDTO.setTitle((String)doc.getFieldValue("title"));
      resultDTO.setShopAreaInfo((String)doc.getFieldValue("shop_area_info"));
      resultDTO.setEditor((String)doc.getFieldValue("editor"));
      resultDTO.setShopName((String)doc.getFieldValue("shop_name"));

      values = doc.getFieldValues("item_detail");
      if (values != null) {
        List<ItemIndexDTO> itemIndexDTOs = new ArrayList<ItemIndexDTO>();
        for (int i = 0; i < values.size(); i++) {
          itemIndexDTOs.add(new ItemIndexDTO((String) values.toArray()[i]));
        }
        resultDTO.setItemIndexDTOs(itemIndexDTOs);
      }

      values = doc.getFieldValues("product");
      if (values != null) {
        String[] products = new String[values.size()];
        for (int i = 0; i < values.size(); i++) {
          products[i] = (String) values.toArray()[i];
        }
        resultDTO.setProducts(products);
      }

      values = doc.getFieldValues("sales_man");
      if (values != null) {
        String[] salesMan = new String[values.size()];
        for (int i = 0; i < values.size(); i++) {
          salesMan[i] = (String) values.toArray()[i];
        }
        resultDTO.setSalesman(salesMan);
      }

      values = doc.getFieldValues("service_worker");
      if (values != null) {
        String[] serviceWorker = new String[values.size()];
        for (int i = 0; i < values.size(); i++) {
          serviceWorker[i] = (String) values.toArray()[i];
        }
        resultDTO.setServiceWorker(serviceWorker);
      }

      values = doc.getFieldValues("pay_method");
      if (values != null) {
        String[] payMethod = new String[values.size()];
        for (int i = 0; i < values.size(); i++) {
          payMethod[i] = (String) values.toArray()[i];
        }
        resultDTO.setPayMethod(payMethod);
      }

      resultDTO.setStorehouseName((String) doc.getFirstValue("storehouse_name"));
      resultDTO.setCustomerOrSupplierShopId(NumberUtil.longValue((String)doc.getFirstValue("customer_or_supplier_shop_id")));
      resultListDTO.getOrders().add(resultDTO);
    }
    return resultListDTO;
  }

  private boolean generateBooleanQuery(String field, Boolean value, boolean started, StringBuilder qString) {
    if (value == null) return started;
    if (started) {
      qString.append(" AND ");
    }
    qString.append(field).append(":");
    if (value) {
      qString.append("true");
    } else {
      qString.append("false");
    }
    return started;
  }

  private boolean generateRangeQuery(String field, String start, String end, boolean started,StringBuilder qString,boolean isClose) {
    if (start == null && end == null) return started;
    if (started) {
      qString.append(" AND ");
    }
    if (start != null && end != null) {
      qString.append(field).append(":").append(isClose?"[":"{").append(start).append(" TO ").append(end).append(isClose?"]":"}");
    } else if (start != null) {
      qString.append(field).append(":").append(isClose ? "[" : "{").append(start).append(" TO *").append(isClose?"]":"}");
    } else {
      qString.append(field).append(":").append(isClose?"[":"{").append("* TO ").append(end).append(isClose?"]":"}");
    }
    started = true;
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
      qString.append(field).append(":").append("(\"").append(value).append("\")^100");
      qString.append(" OR ").append(field).append(":").append("(").append(value).append("*").append(")^10");
      qString.append(" OR ").append(field).append("_ngram_continuous").append(":").append("(").append(value).append(")");
      qString.append(")");
      started = true;
    }
    return started;
  }


  private boolean generateShopNameFuzzyRelatedQuery(String field, String value, Boolean flag, boolean started, StringBuilder qString) {
    if (!StringUtils.isBlank(value)) {
      value = SolrQueryUtils.escape(value);
      if (started) {
        if(flag) {
          qString.append(" AND ");
        } else {
          qString.append(" OR ");
        }

      }
      qString.append("(");
      qString.append(field).append(":").append("(\"").append(value).append("\")^100");
      qString.append(" OR ").append(field).append(":").append("(").append(value).append("*").append(")^10");
      qString.append(")");
      started = true;
    }
    return started;
  }

  private boolean generateFuzzyCustomerOrSupplierInfoQuery(String value, boolean started, StringBuilder qString) {
    String nameField = "customer_or_supplier_name";
    String contactField = "contact";
    String mobileField = "contact_num";


    if (!StringUtils.isBlank(value)) {
      value = SolrQueryUtils.escape(value);
      String mobileValue = null;
      if (value.matches("^[0-9]+$")) {
        mobileValue = value;
      } else {
        mobileValue = StringUtil.extractNumbers(value, 3);
      }


      if (started) {
        qString.append(" AND ");
      }
      qString.append("(");
      qString.append(nameField).append(":").append("(\"").append(value).append("\")^100");
      qString.append(" OR ").append(nameField).append(":").append("(").append(value).append("*").append(")^10");
      qString.append(" OR ").append(nameField).append("_ngram_continuous").append(":").append("(").append(value).append(")");

      qString.append(contactField).append(":").append("(\"").append(value).append("\")^100");
      qString.append(" OR ").append(contactField).append(":").append("(").append(value).append("*").append(")^10");
      qString.append(" OR ").append(contactField).append("_ngram_continuous").append(":").append("(").append(value).append(")");

      if (StringUtils.isNotBlank(mobileValue)) {
        qString.append(mobileField).append(":").append("(\"").append(mobileValue).append("\")^100");
        qString.append(" OR ").append(mobileField).append(":").append("(").append(mobileValue).append("*").append(")^10");
        qString.append(" OR ").append(mobileField).append("_ngram_continuous").append(":").append("(").append(mobileValue).append(")");
      }
      qString.append(")");
      started = true;
    }
    return started;
  }

  private boolean generateStringRelatedQuery(String field, String value, boolean started, StringBuilder qString) {
    if (!StringUtils.isBlank(value)) {
      value = SolrQueryUtils.escape(value);
      if (started) {
        qString.append(" AND ");
      }
      qString.append(field).append(":").append("(\"").append(value).append("\")");
      started = true;
    }
    return started;
  }

  private boolean generateLongRelatedQuery(String field, Long value, boolean started, StringBuilder qString) {
    if (value != null) {
      if (started) {
        qString.append(" AND ");
      }
      qString.append(field).append(":").append("(").append(value).append(")");
      started = true;
    }
    return started;
  }

  /**
   * 建立在q.op  为OR 的基础上
   * solr  拼接  field : (a b c d c);  代替(field:A OR field:B OR field:C)
   */
  private boolean generateStringArrayRelatedUseQueryIn(String field, String[] values, boolean started, StringBuilder qString) {
    if (ArrayUtils.isEmpty(values)) return started;
    int i = 0;
    boolean flag = false; //排除 values{"",""}
    for (String value : values) {
      if (StringUtils.isBlank(value)) continue;
      flag = true;
      value = SolrQueryUtils.escape(value);
      if (i > 0) {
        qString.append(" ").append(value);
      } else if (i == 0) {
        if(started){
          qString.append(" AND ").append(field).append(":(").append(value);
        }else{
          qString.append(field).append(":(").append(value);
        }
      }
      started = true;
      i++;
    }
    if (flag)
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

  public boolean generateMemberNoRelatedQuery(OrderSearchConditionDTO conditions, boolean started, StringBuilder qString) {
    if(StringUtil.isEmpty(conditions.getAccountMemberNo())) return started;
    if(started){
      qString.append(" AND ");
    }
    String value = SolrQueryUtils.escape(conditions.getAccountMemberNo());
    qString.append("(");
    if (!(StringUtil.isNotEmpty(conditions.getQueryPageType()) && "customerOrSupplierDetail".equals(conditions.getQueryPageType()))) {
      qString.append("member_no").append(":").append("(").append(value).append(")");
      qString.append(" OR ").append("member_no").append(":").append("(").append(value).append("*").append(")^4");
      qString.append(" OR ").append("member_no").append(":").append("(").append("*").append(value).append("*").append(")");
      qString.append(" OR ").append("member_no").append(":").append("(\"").append(value).append("\")^100");

      qString.append(" OR ").append("account_member_no").append(":").append("(").append(value).append(")");
      qString.append(" OR ").append("account_member_no").append(":").append("(").append(value).append("*").append(")^4");
      qString.append(" OR ").append("account_member_no").append(":").append("(").append("*").append(value).append("*").append(")");
      qString.append(" OR ").append("account_member_no").append(":").append("(\"").append(value).append("\")^100");
    }else{
      qString.append("").append("account_member_no").append(":").append("(").append(value).append(")");
      qString.append(" OR ").append("account_member_no").append(":").append("(").append(value).append("*").append(")^4");
      qString.append(" OR ").append("account_member_no").append(":").append("(").append("*").append(value).append("*").append(")");
      qString.append(" OR ").append("account_member_no").append(":").append("(\"").append(value).append("\")^100");
    }


    qString.append(")");
    started = true;
    return started;
  }

  public boolean generateProductRelatedQuery(OrderSearchConditionDTO conditions, boolean started, StringBuilder qString,boolean isOrder) {
    if(conditions.isEmptyOfProductInfo() && StringUtils.isNotBlank(conditions.getSearchWord())){//单据查询的  product 混合框功能
      String value = SolrQueryUtils.escape(conditions.getSearchWord());
      if (started) {
        qString.append(" AND ");
      }
      qString.append("(");

      qString.append("commodity_code").append(":").append("(\"").append(value).append("\")^100");
      qString.append(" OR commodity_code").append(":").append("(").append(value).append("*").append(")^10");
      qString.append(" OR commodity_code_ngram_continuous").append(":").append("(").append(value).append(")");

      qString.append(" OR product_name").append(":").append("(").append(value).append(")^4");
      qString.append(" OR product_name_ngram_continuous").append(":").append("(").append(value).append(")");
      qString.append(" OR product_name_exact").append(":").append("(").append(value).append("*").append(")^100");

      qString.append(" OR product_brand").append(":").append("(").append(value).append(")^4");
      qString.append(" OR product_brand_ngram_continuous").append(":").append("(").append(value).append(")");
      qString.append(" OR product_brand_exact").append(":").append("(").append(value).append("*").append(")^100");

      qString.append(" OR product_spec").append(":").append("(").append(value).append(")^4");
      qString.append(" OR product_spec_ngram_continuous").append(":").append("(").append(value).append(")");
      qString.append(" OR product_spec_exact").append(":").append("(").append(value).append("*").append(")^100");

      qString.append(" OR product_model").append(":").append("(").append(value).append(")^4");
      qString.append(" OR product_model_ngram_continuous").append(":").append("(").append(value).append(")");
      qString.append(" OR product_model_exact").append(":").append("(").append(value).append("*").append(")^100");

      qString.append(" OR product_vehicle_brand").append(":").append("(").append(value).append(")^4");
      qString.append(" OR product_vehicle_brand_ngram_continuous").append(":").append("(").append(value).append(")");
      qString.append(" OR product_vehicle_brand_exact").append(":").append("(").append(value).append("*").append(")^100");

      qString.append(" OR product_vehicle_model").append(":").append("(").append(value).append(")^4");
      qString.append(" OR product_vehicle_model_ngram_continuous").append(":").append("(").append(value).append(")");
      qString.append(" OR product_vehicle_model_exact").append(":").append("(").append(value).append("*").append(")^100");

      if(isOrder){
        qString.append(" OR vehicle_brand").append(":").append("(").append(value).append(")^4");
        qString.append(" OR vehicle_brand_ngram_continuous").append(":").append("(").append(value).append(")");
        qString.append(" OR vehicle_brand_exact").append(":").append("(").append(value).append("*").append(")^100");

        qString.append(" OR vehicle_model").append(":").append("(").append(value).append(")^4");
        qString.append(" OR vehicle_model_ngram_continuous").append(":").append("(").append(value).append(")");
        qString.append(" OR vehicle_model_exact").append(":").append("(").append(value).append("*").append(")^100");
      }
      qString.append(")");
      started = true;
    }
    started = generateFuzzyRelatedQuery("commodity_code", conditions.getCommodityCode(), started, qString);
    started = generateMultifieldQuery("product_name", conditions.getProductName(), started, qString);
    started = generateMultifieldQuery("product_brand", conditions.getProductBrand(), started, qString);
    started = generateMultifieldQuery("product_spec", conditions.getProductSpec(), started, qString);
    started = generateMultifieldQuery("product_model", conditions.getProductModel(), started, qString);
    started = generateMultifieldQuery("product_vehicle_brand", conditions.getProductVehicleBrand(), started, qString);
    started = generateMultifieldQuery("product_vehicle_model", conditions.getProductVehicleModel(), started, qString);
    if(isOrder){
      started = generateMultifieldQuery("vehicle_brand", conditions.getvBrand(), started, qString);//施工单有
      started = generateMultifieldQuery("vehicle_model", conditions.getvModel(), started, qString);//施工单有
    }

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
      qString.append(" OR ").append(field).append("_ngram_continuous").append(":").append("(").append(value).append(")");
      qString.append(" OR ").append(field).append("_exact").append(":").append("(").append(value).append("*").append(")^100");
      qString.append(")");
      started = true;
    }
    return started;
  }

  @Override
  public QueryResponse queryOrderByServiceWorker(Long shopId, long orderId) throws Exception {
    SolrQuery query = new SolrQuery();
    StringBuffer stringBuffer = new StringBuffer();

    if (StringUtils.isBlank(String.valueOf(orderId))) {
      stringBuffer.append("id:*");
    } else {
      stringBuffer.append("id:").append(orderId);
    }
    query.setQuery(stringBuffer.toString());

    query.setParam("q.op", "AND");
    StringBuffer fQueryString = new StringBuffer();
    if (shopId == null) throw new BcgogoException("shopId nullPointException!");
    fQueryString.append("(shop_id:").append(shopId.toString()).append(")");

    String fqString = fQueryString.toString();
    query.setFilterQueries(fqString);

    return SolrClientHelper.getOrderSolrClient().query(query);
  }

  /**
   * 根据员工查询单据
   *
   * @param shopId
   * @param assistantName :员工姓名
   * @param start
   * @param rows
   * @return
   * @throws Exception
   */
  @Override
  public QueryResponse queryOrderByServiceWorker(Long shopId, String assistantName, Long startTime, Long endTime, int start, int rows) throws Exception {
    SolrQuery query = new SolrQuery();
    StringBuffer stringBuffer = new StringBuffer();

    if (startTime != null && endTime != null) {
      stringBuffer.append(" created_time:[").append(startTime).append(" TO ").append(endTime).append("]");
    }
    assistantName = SolrQueryUtils.escape(assistantName);
    if (StringUtil.isEmpty(stringBuffer.toString())) {
    if (StringUtils.isBlank(assistantName)) {
        stringBuffer.append("  (service_worker:* OR sales_man:* )");
    } else {
        stringBuffer.append("  (service_worker:").append(assistantName).append(" OR sales_man:").append(assistantName).append(" ) ");
    }
    } else {
      if (StringUtils.isBlank(assistantName)) {
        stringBuffer.append(" AND (service_worker:* OR sales_man:* )");
      } else {
        stringBuffer.append(" AND (service_worker:").append(assistantName).append(" OR sales_man:").append(assistantName).append(" ) ");
    }
    }
    query.setQuery(stringBuffer.toString());

    query.setParam("q.op", "AND");
    StringBuffer fQueryString = new StringBuffer();
    //只查询销售。维修美容单，和洗车单
    if (shopId == null) throw new BcgogoException("shopId nullPointException!");
    fQueryString.append("(shop_id:").append(shopId.toString())
        .append(") AND (order_type:SALE OR order_type:REPAIR OR order_type:WASH OR order_type:WASH_BEAUTY OR order_type:MEMBER_BUY_CARD OR order_type:3 OR order_type:4 OR order_type:5)");
    fQueryString.append("AND(order_status:REPAIR_SETTLED OR order_status:WASH_SETTLED OR order_status:SALE_DONE OR order_status:MEMBERCARD_ORDER_STATUS OR order_status:SALE_DEBT_DONE OR order_status:SETTLED) ");
    String fqString = fQueryString.toString();
    query.setFilterQueries(fqString);
    query.setStart(start);
    query.setRows(rows);

    return SolrClientHelper.getOrderSolrClient().query(query);
  }

  @Override
  public OrderSearchResultListDTO queryOrderItems(OrderSearchConditionDTO conditions) throws Exception {
    boolean started = false;
    StringBuilder qString = new StringBuilder();

    started = generateStringArrayRelatedQuery("order_status", conditions.getOrderStatus(), started, qString);
    started = generateStringArrayRelatedQuery("item_type", conditions.getItemTypes(), started, qString);
    started = generateStringArrayRelatedQuery("customer_or_supplier_id", conditions.getCustomerOrSupplierIds(), started, qString);
    started = generateFuzzyRelatedQuery("vehicle_licence_no", conditions.getVehicle(), started, qString);
    started = generateStringRelatedQuery("business_category_id", conditions.getBusinessCategoryId(), started, qString);
    started = generateStringArrayRelatedUseQueryIn("service_id", conditions.getServiceIds(), started, qString);
    started = generateStringRelatedQuery("consume_type", conditions.getConsumeType(), started, qString);
    started = generateFuzzyRelatedQuery("coupon_type", conditions.getCouponType(), started, qString);
    started = generateLongRelatedQuery("order_id", conditions.getOrderId(), started, qString);

    started = generateStringArrayRelatedUseQueryIn("product_id", conditions.getProductIds(), started, qString);
    started = generateStringArrayRelatedUseQueryIn("supplier_product_id", conditions.getSupplierProductIds(), started, qString);
    started = generateFuzzyRelatedQuery("customer_or_supplier_name", conditions.getCustomerOrSupplierName(), started, qString);
    started = generateFuzzyRelatedQuery("shop_name", conditions.getShopName(), started, qString);

    started = generateMultifieldQuery("services", conditions.getService(), started, qString);

    started = generateProductRelatedQuery(conditions, started, qString,false);

    started = generateRangeQuery("order_created_time", conditions.getStartTime() == null ? null : conditions.getStartTime().toString(),
        conditions.getEndTime() == null ? null : conditions.getEndTime().toString(), started, qString,true);

    if (conditions.getProvinceNo()!=null || conditions.getCityNo()!=null || conditions.getRegionNo()!=null) {
      Long shopAreaId = conditions.getRegionNo()!=null?conditions.getRegionNo():(conditions.getCityNo()!=null?conditions.getCityNo():conditions.getProvinceNo());
      started = generateLongRelatedQuery("shop_area_ids",shopAreaId, started, qString);
    }
    if(OrderSearchConditionDTO.PreBuyOrderStatus.VALID.equals(conditions.getPreBuyOrderStatus())){
      started = generateRangeQuery("end_time",DateUtil.getTheDayTime().toString(),null, started, qString,true);
    }else if(OrderSearchConditionDTO.PreBuyOrderStatus.EXPIRED.equals(conditions.getPreBuyOrderStatus())){
      started = generateRangeQuery("end_time",null,DateUtil.getTheDayTime().toString(), started, qString,false);
    }
    if (!ArrayUtils.isEmpty(conditions.getExcludeShopIds())) {
      if (started) {
        qString.append(" AND ");
      }
      qString.append("!shop_id:(");
      for (int i = 0, max = conditions.getExcludeShopIds().length; i < max; i++) {
        qString.append(conditions.getExcludeShopIds()[i]);
        if (i < (max - 1)) qString.append(" OR ");
      }
      qString.append(")");
      started = true;
    }

    if (qString.length() == 0) {
      qString.append("*:*");
    }
    SolrQuery query = new SolrQuery();
    query.setQuery(qString.toString());
    query.setParam("q.op", "OR");

    StringBuilder fQueryString = new StringBuilder();
    if (ArrayUtils.isEmpty(conditions.getOrderType())) throw new BcgogoException("orderType nullPointException!");
    fQueryString.append(" doc_type:").append(SolrClientHelper.BcgogoSolrDocumentType.ORDER_ITEM_DOC_TYPE.getValue());
    if(conditions.getExcludeOnlineOrder() != null && conditions.getExcludeOnlineOrder()){
      fQueryString.append(" AND !supplier_product_id:[0 TO *] ");
    }
    generateStringArrayRelatedQuery("order_type", conditions.getOrderType(), true, fQueryString);

    if(!(!ArrayUtils.isEmpty(conditions.getSearchStrategy()) && Arrays.asList(conditions.getSearchStrategy()).contains(OrderSearchConditionDTO.SEARCHSTRATEGY_NO_SHOP_RESTRICT))){
      if (conditions.getShopId() == null) throw new BcgogoException("shopId nullPointException!");
    }else{
      if (conditions.getShopKind() ==null) throw new BcgogoException("shopKind nullPointException!");
    }
    if(conditions.getShopId()!=null){
      fQueryString.append(" AND shop_id:").append(conditions.getShopId());
    }
    if(conditions.getShopKind() !=null){
      fQueryString.append(" AND shop_kind:").append(conditions.getShopKind());
    }
    if(conditions.getBusinessChanceType() != null) {
      fQueryString.append(" AND business_chance_type:").append(conditions.getBusinessChanceType().toString());
    }
    query.setFilterQueries(fQueryString.toString());





    if(conditions.getJoinSearchConditionDTO()!=null){
      generateFilterJoinCustomerOrSupplierQuery(conditions.getJoinSearchConditionDTO(),query);
    }

    query.setParam("fl", "*,score");
    query.setStart(conditions.getRowStart());
    query.setRows(conditions.getPageRows());

    if (conditions.getSearchStrategy()!=null
        && Arrays.asList(conditions.getSearchStrategy()).contains(OrderSearchConditionDTO.SEARCHSTRATEGY_STATS_FACET)
        && !ArrayUtils.isEmpty(conditions.getStatsFields())
        && !ArrayUtils.isEmpty(conditions.getFacetFields())) {
      query.setFacet(true);
      query.setParam(StatsParams.STATS, "true");
      query.setParam(StatsParams.STATS_FIELD, conditions.getStatsFields());
      query.setParam(StatsParams.STATS_FACET, conditions.getFacetFields());
    } else if (conditions.getSearchStrategy()!=null
        && Arrays.asList(conditions.getSearchStrategy()).contains(OrderSearchConditionDTO.SEARCHSTRATEGY_STATS)
        && !ArrayUtils.isEmpty(conditions.getStatsFields())) {
      query.setParam(StatsParams.STATS, "true");
      query.setParam(StatsParams.STATS_FIELD, conditions.getStatsFields());
    }
    if (StringUtils.isNotBlank(conditions.getSort())) {
      query.setParam("sort", conditions.getSort());
    }
    QueryResponse response = SolrClientHelper.getOrderItemSolrClient().query(query);
    return processSearchOrderItemResponse(response, conditions);
  }

    @Override
  public OrderSearchResultListDTO queryOrderItemsByExactCondition(OrderSearchConditionDTO conditions) throws Exception {
    boolean started = false;
    StringBuilder qString = new StringBuilder();

    started = generateStringArrayRelatedQuery("order_status", conditions.getOrderStatus(), started, qString);
    started = generateStringArrayRelatedQuery("item_type", conditions.getItemTypes(), started, qString);
    started = generateStringArrayRelatedQuery("customer_or_supplier_id", conditions.getCustomerOrSupplierIds(), started, qString);
    started = generateStringRelatedQuery("business_category_id", conditions.getBusinessCategoryId(), started, qString);
    started = generateStringArrayRelatedUseQueryIn("service_id", conditions.getServiceIds(), started, qString);

    started = generateStringArrayRelatedUseQueryIn("product_id", conditions.getProductIds(), started, qString);
    started = generateFuzzyRelatedQuery("customer_or_supplier_name", conditions.getCustomerOrSupplierName(), started, qString);

    started = generateStringRelatedQuery("product_name", conditions.getProductName(), started, qString);
    started = generateStringRelatedQuery("product_brand", conditions.getProductBrand(), started, qString);
    started = generateStringRelatedQuery("product_spec", conditions.getProductSpec(), started, qString);
    started = generateStringRelatedQuery("product_model", conditions.getProductModel(), started, qString);
    started = generateStringRelatedQuery("product_vehicle_brand", conditions.getProductVehicleBrand(), started, qString);
    started = generateStringRelatedQuery("product_vehicle_model", conditions.getProductVehicleModel(), started, qString);

    started = generateStringRelatedQuery("commodity_code", conditions.getCommodityCode(), started, qString);

    started = generateRangeQuery("order_created_time", conditions.getStartTime() == null ? null : conditions.getStartTime().toString(),
        conditions.getEndTime() == null ? null : conditions.getEndTime().toString(), started, qString,true);

    if (qString.length() == 0) {
      qString.append("*:*");
    }
    SolrQuery query = new SolrQuery();
    query.setQuery(qString.toString());
    query.setParam("q.op", "OR");

    StringBuilder fQueryString = new StringBuilder();
    if (conditions.getShopId() == null) throw new BcgogoException("shopId nullPointException!");
    if (ArrayUtils.isEmpty(conditions.getOrderType())) throw new BcgogoException("orderType nullPointException!");
    fQueryString.append("shop_id:").append(conditions.getShopId()).append(" AND doc_type:").append(SolrClientHelper.BcgogoSolrDocumentType.ORDER_ITEM_DOC_TYPE.getValue());
    generateStringArrayRelatedQuery("order_type", conditions.getOrderType(), true, fQueryString);
    query.setFilterQueries(fQueryString.toString());


    if(conditions.getJoinSearchConditionDTO()!=null){
      generateFilterJoinCustomerOrSupplierQuery(conditions.getJoinSearchConditionDTO(),query);
    }

    query.setParam("fl", "*,score");
    query.setStart(conditions.getRowStart());
    query.setRows(conditions.getPageRows());

    if (conditions.getSearchStrategy()!=null
        && Arrays.asList(conditions.getSearchStrategy()).contains(OrderSearchConditionDTO.SEARCHSTRATEGY_STATS_FACET)
        && !ArrayUtils.isEmpty(conditions.getStatsFields())
        && !ArrayUtils.isEmpty(conditions.getFacetFields())) {
      query.setFacet(true);
      query.setParam(StatsParams.STATS, "true");
      query.setParam(StatsParams.STATS_FIELD, conditions.getStatsFields());
      query.setParam(StatsParams.STATS_FACET, conditions.getFacetFields());
    } else if (conditions.getSearchStrategy()!=null
        && Arrays.asList(conditions.getSearchStrategy()).contains(OrderSearchConditionDTO.SEARCHSTRATEGY_STATS)
        && !ArrayUtils.isEmpty(conditions.getStatsFields())) {
      query.setParam(StatsParams.STATS, "true");
      query.setParam(StatsParams.STATS_FIELD, conditions.getStatsFields());
    }
    if (StringUtils.isNotBlank(conditions.getSort())) {
      query.setParam("sort", conditions.getSort());
    }
    QueryResponse response = SolrClientHelper.getOrderItemSolrClient().query(query);
    return processSearchOrderItemResponse(response, conditions);
  }

  @Override
  public List<SearchSuggestionDTO> queryOrderItemSuggestion(OrderSearchConditionDTO conditions) throws Exception {
    boolean started = false;
    StringBuilder qString = new StringBuilder();
    if (!StringUtils.isBlank(conditions.getSearchWord()) && !StringUtils.isBlank(conditions.getSearchField())) {
      generateSinglefieldSuggestionQuery(qString, conditions.getSearchWord(), conditions.getSearchField());
      started = true;
    }
    if (!StringUtils.isBlank(conditions.getProductName())) {
      if (started) qString.append(" AND ");
      qString.append("product_name_exact:").append("(\"").append(SolrQueryUtils.escape(conditions.getProductName())).append("\") ");
      started = true;
    }
    if (!StringUtils.isBlank(conditions.getProductBrand())) {
      if (started) qString.append(" AND ");
      qString.append("product_brand_exact:").append("(\"").append(SolrQueryUtils.escape(conditions.getProductBrand())).append("\") ");
      started = true;
    }
    if (!StringUtils.isBlank(conditions.getProductModel())) {
      if (started) qString.append(" AND ");
      qString.append("product_model_exact:").append("(\"").append(SolrQueryUtils.escape(conditions.getProductModel())).append("\") ");
      started = true;
    }
    if (!StringUtils.isBlank(conditions.getProductSpec())) {
      if (started) qString.append(" AND ");
      qString.append("product_spec_exact:").append("(\"").append(SolrQueryUtils.escape(conditions.getProductSpec())).append("\") ");
      started = true;
    }

    if (!StringUtils.isBlank(conditions.getProductVehicleBrand())) {
      if (started) qString.append(" AND ");
      qString.append("product_vehicle_brand_exact:").append("(\"").append((conditions.getProductVehicleBrand())).append("\") ");
      started = true;
    }

    if (!StringUtils.isBlank(conditions.getProductVehicleModel())) {
      if (started) qString.append(" AND ");
      qString.append("product_vehicle_model_exact:").append("(\"").append(SolrQueryUtils.escape(conditions.getProductVehicleModel())).append("\") ");
      started = true;
    }

    if (qString.length() == 0) {
      qString.append("*:*");
    }
    SolrQuery query = new SolrQuery();
    query.setQuery(qString.toString());
    query.setParam("q.op", "OR");

    StringBuilder fQueryString = new StringBuilder();
    if (conditions.getShopId() == null) throw new BcgogoException("shopId nullPointException!");
    fQueryString.append("shop_id:").append(conditions.getShopId());
    fQueryString.append("doc_type:").append(SolrClientHelper.BcgogoSolrDocumentType.ORDER_ITEM_DOC_TYPE.getValue());
    generateStringArrayRelatedQuery("order_type", conditions.getOrderType(), true, fQueryString);
    query.setFilterQueries(fQueryString.toString());

    if(StringUtils.isBlank(conditions.getSearchField())){
      query.setParam("fl", "*,score");
    }else{
      query.setParam("fl", conditions.getSearchField()+",score");
    }

    int rows = conditions.getPageRows();
    int stepSize = rows * 5;
    query.setRows(stepSize);
    return collapsingSuggestionResult(query, conditions,stepSize);
  }

  private List<SearchSuggestionDTO> collapsingSuggestionResult(SolrQuery query, OrderSearchConditionDTO searchConditionDTO, int stepSize) throws Exception {
    IProductCurrentUsedService productCurrentUsedService = ServiceManager.getService(IProductCurrentUsedService.class);
    List<SearchSuggestionDTO> suggestionResults = new ArrayList<SearchSuggestionDTO>();
    QueryResponse rsp = SolrClientHelper.getOrderItemSolrClient().query(query);
    if (searchConditionDTO.gotoMemCacheFunction()) {
      //如果是商品（品名和品牌）SearchWord为空
      suggestionResults.addAll(productCurrentUsedService.getProductSuggestionFromMemory(new SearchMemoryConditionDTO(searchConditionDTO)));
    } else {
      int start = 0;
      Map<String, SolrDocument> res = new HashMap<String, SolrDocument>();
      SolrDocumentList documents = rsp.getResults();
      int queryTime = 1;
      while (documents.size() > 0 && suggestionResults.size() < searchConditionDTO.getPageRows()) {
        for (SolrDocument document : documents) {

          SearchSuggestionDTO r = new SearchSuggestionDTO();
          //如果除了product_name的其他字段 只拼接该field
          r.addEntry(searchConditionDTO.getSearchField(), (String) document.getFirstValue(searchConditionDTO.getSearchField()));
          r.setUuid(searchConditionDTO.getUuid());

          List<String[]> entries = r.suggestionEntry;
          StringBuffer value = new StringBuffer();
          for (int i = 0; i < entries.size(); i++) {
            if (StringUtils.isNotBlank(entries.get(i)[1]))
              value.append(entries.get(i)[1]).append(" ");
          }
          if (res.containsKey(value.toString())) continue;
          res.put(value.toString(), document);
          if (value.length() > 0) suggestionResults.add(r);
          if (suggestionResults.size() == searchConditionDTO.getPageRows()) return suggestionResults;
        }
        if (suggestionResults.size() >= searchConditionDTO.getPageRows() || documents.size() < stepSize) break;
        start += stepSize;
        query.setStart(start);
        rsp = SolrClientHelper.getOrderItemSolrClient().query(query);
        documents = rsp.getResults();
        queryTime++;
        if (queryTime > MAX_QUERY_TIMES) {
          break;
        }
      }
    }
    return suggestionResults;
  }

  private OrderSearchResultListDTO processSearchOrderItemResponse(QueryResponse response, OrderSearchConditionDTO conditions) {
    OrderSearchResultListDTO resultListDTO = new OrderSearchResultListDTO();
    generateStatInfo(response, conditions, resultListDTO);

    SolrDocumentList docs = response.getResults();
    resultListDTO.setItemNumFound(docs.getNumFound());
    for (SolrDocument doc : docs) {
      OrderItemSearchResultDTO resultDTO = new OrderItemSearchResultDTO();
      //日期	单据号	单据类型	客户	车牌号	施工内容	工时费	材料（品名+品牌/产地+规格+型号）	单价	数量	小计
      resultDTO.setItemId(Long.parseLong(doc.getFirstValue("id").toString()));
      resultDTO.setShopId(NumberUtil.longValue(doc.getFirstValue("shop_id")));
      resultDTO.setOrderId(NumberUtil.longValue(doc.getFirstValue("order_id")));
      resultDTO.setOrderType((String) doc.getFirstValue("order_type"));
      resultDTO.setOrderStatus((String) doc.getFirstValue("order_status"));
      resultDTO.setCreatedTime(NumberUtil.longValue(doc.getFirstValue("order_created_time")));
      resultDTO.setEndDate(NumberUtil.longValue(doc.getFirstValue("end_time")));
      resultDTO.setVestDate(NumberUtil.longValue(doc.getFirstValue("vest_date")));
      resultDTO.setOrderReceiptNo((String) doc.getFirstValue("order_receipt_no"));
      resultDTO.setEditor((String) doc.getFirstValue("editor"));
      resultDTO.setShopAreaInfo((String)doc.getFieldValue("shop_area_info"));
      resultDTO.setShopName((String)doc.getFieldValue("shop_name"));
      resultDTO.setItemMemo((String)doc.getFieldValue("item_memo"));
      resultDTO.setBusinessChanceType((String)doc.getFieldValue("business_chance_type"));
      if(doc.getFirstValue("quoted_result") != null) {
        resultDTO.setQuotedResult(QuotedResult.valueOf((String) doc.getFirstValue("quoted_result")));
      }

      if(doc.getFirstValue("item_price")==null){
        resultDTO.setItemPrice(0d);
      }else {
        resultDTO.setItemPrice((Double)doc.getFirstValue("item_price"));
      }

      if(doc.getFirstValue("item_count")==null){
        resultDTO.setItemCount(0d);
      }else {
        resultDTO.setItemCount((Double)doc.getFirstValue("item_count"));
      }

      if(doc.getFirstValue("item_total")==null){
        resultDTO.setItemTotal(0d);
      }else {
        resultDTO.setItemTotal((Double)doc.getFirstValue("item_total"));
      }
      if(doc.getFirstValue("item_total_cost_price")==null){
        resultDTO.setItemTotalCostPrice(0d);
      }else {
        resultDTO.setItemTotalCostPrice((Double)doc.getFirstValue("item_total_cost_price"));
      }
      resultDTO.setCustomerOrSupplierName((String)doc.getFirstValue("customer_or_supplier_name"));
      if(doc.getFirstValue("customer_or_supplier_id")!=null){
        resultDTO.setCustomerOrSupplierId(Long.parseLong(doc.getFirstValue("customer_or_supplier_id").toString()));
      }

      resultDTO.setVehicle((String)doc.getFirstValue("vehicle_licence_no"));

      resultDTO.setItemType((String)doc.getFirstValue("item_type"));
      if(ItemTypes.SERVICE.equals(resultDTO.getItemType())){
        resultDTO.setServiceId(NumberUtil.longValue(doc.getFirstValue("service_id")));
        resultDTO.setService((String) doc.getFirstValue("services"));
        resultDTO.setConsumeType((String)doc.getFirstValue("consume_type"));
        resultDTO.setCouponType((String)doc.getFirstValue("coupon_type"));
      }else if(ItemTypes.MATERIAL.equals(resultDTO.getItemType())){
        if(doc.getFirstValue("supplier_product_id")!=null){
          resultDTO.setSupplierProductId(NumberUtil.longValue(doc.getFirstValue("supplier_product_id")));
        }
        resultDTO.setProductId(NumberUtil.longValue(doc.getFirstValue("product_id")));
        resultDTO.setProductName((String)doc.getFirstValue("product_name"));
        resultDTO.setProductBrand((String)doc.getFirstValue("product_brand"));
        resultDTO.setProductModel((String)doc.getFirstValue("product_model"));
        resultDTO.setProductSpec((String)doc.getFirstValue("product_spec"));
        resultDTO.setProductVehicleBrand((String)doc.getFirstValue("product_vehicle_brand"));
        resultDTO.setProductVehicleModel((String)doc.getFirstValue("product_vehicle_model"));
        resultDTO.setCommodityCode((String) doc.getFirstValue("commodity_code"));
        resultDTO.setUnit((String) doc.getFirstValue("unit"));
      }
      resultDTO.setProductInfoStr(resultDTO.generateProductInfo());
      resultListDTO.getOrderItems().add(resultDTO);
    }
    return resultListDTO;
  }


  private void generateStatInfo(QueryResponse response, OrderSearchConditionDTO conditions, OrderSearchResultListDTO resultListDTO) {
    if (conditions.getSearchStrategy()!=null
        && Arrays.asList(conditions.getSearchStrategy()).contains(OrderSearchConditionDTO.SEARCHSTRATEGY_STATS)
        &&!ArrayUtils.isEmpty(conditions.getStatsFields())) {
      Map<String, FieldStatsInfo> statsInfoMap = response.getFieldStatsInfo();
      if (statsInfoMap!=null && !statsInfoMap.isEmpty()) {
        Map<String, Long> totalCounts = new HashMap<String, Long>();
        Map<String, Double> sums = new HashMap<String, Double>();
        for (String statsField : conditions.getStatsFields()) {
          FieldStatsInfo statsInfo = response.getFieldStatsInfo().get(statsField);
          if (statsInfo != null) {
            totalCounts.put(statsField.toUpperCase(), NumberUtil.longValue(statsInfo.getCount())+NumberUtil.longValue(statsInfo.getMissing()));
            sums.put(statsField.toUpperCase(), NumberUtil.round((Double)statsInfo.getSum(), 2));
          }
        }
        resultListDTO.setTotalCounts(totalCounts);
        resultListDTO.setTotalAmounts(sums);
      }

    }else if (conditions.getSearchStrategy()!=null
        && Arrays.asList(conditions.getSearchStrategy()).contains(OrderSearchConditionDTO.SEARCHSTRATEGY_STATS_FACET)
        &&!ArrayUtils.isEmpty(conditions.getStatsFields())
        &&!ArrayUtils.isEmpty(conditions.getFacetFields())) {
      Map<String, FieldStatsInfo> statsInfoMap = response.getFieldStatsInfo();
      if (statsInfoMap!=null && !statsInfoMap.isEmpty()) {
        Map<String, Long> counts = new HashMap<String, Long>();
        Map<String, Double> sums = new HashMap<String, Double>();
        StringBuilder key = null;//statsField_facetField_facetName
        FieldStatsInfo statsInfo = null;
        Map<String, List<FieldStatsInfo>> statsInfoFacetMap = null;
        for (String statsField : conditions.getStatsFields()) {
          statsInfo = statsInfoMap.get(statsField);
          if (statsInfo != null && !statsInfo.getFacets().isEmpty()) {
            statsInfoFacetMap = statsInfo.getFacets();
            for (String facetField : conditions.getFacetFields()) {
              List<FieldStatsInfo> statsInfoList = statsInfoFacetMap.get(facetField);
              for (FieldStatsInfo stats : statsInfoList) {
                String facetName = stats.getName();
                double sum = NumberUtil.round((Double) stats.getSum(), 2);
                key = new StringBuilder(statsField).append(KEY_INVERVAL).append(facetField).append(KEY_INVERVAL).append(facetName);
                sums.put(key.toString().toUpperCase(), sum);
                long count = NumberUtil.longValue(stats.getCount())+NumberUtil.longValue(stats.getMissing());
                counts.put(key.toString().toUpperCase(), count);
              }
            }
          }
        }
        resultListDTO.setTotalCounts(counts);
        resultListDTO.setTotalAmounts(sums);
        resultListDTO.addTotalDebtSettled();
      }

    }

    SolrDocumentList docs = response.getResults();
    Map<String, Double> pageSums = new HashMap<String, Double>();
    if (conditions.getSearchStrategy()!=null
        && Arrays.asList(conditions.getSearchStrategy()).contains(OrderSearchConditionDTO.SEARCHSTRATEGY_CURRENT_PAGE_STATS)
        && !ArrayUtils.isEmpty(conditions.getPageStatsFields())) {
      for (String statsField : conditions.getPageStatsFields()) {
        if(!ArrayUtils.isEmpty(conditions.getPageFacetFields())){
          StringBuilder key = null;
          for(String facetField : conditions.getPageFacetFields()){
            for (SolrDocument doc : docs) {
              Object obj = doc.getFirstValue(statsField);
              String facetName = (String)doc.getFirstValue(facetField);
              key= new StringBuilder(statsField).append(KEY_INVERVAL).append(facetField).append(KEY_INVERVAL).append(facetName);
              if (obj != null && NumberUtil.isNumber(obj.toString())) {
                if (pageSums.get(key.toString()) == null) {
                  pageSums.put(key.toString(), 0d);
                }
                pageSums.put(key.toString(), NumberUtil.round(pageSums.get(key.toString()) + Double.parseDouble(obj.toString()), 2));
              }
            }
          }

        }else{
          if (pageSums.get(statsField) == null) {
            pageSums.put(statsField, 0d);
          }
          for (SolrDocument doc : docs) {
            Object obj = doc.getFirstValue(statsField);
            if(obj!=null && NumberUtil.isNumber(obj.toString())) {
                pageSums.put(statsField, NumberUtil.round(pageSums.get(statsField) + Double.parseDouble(obj.toString()),2));
            }
          }
        }
      }
    }
    resultListDTO.setCurrentPageTotalAmounts(pageSums);
  }

  private void generateSinglefieldSuggestionQuery(StringBuilder qString, String q, String field) {
    q = SolrQueryUtils.escape(q);
    qString.append("(");
    qString.append(field).append(":").append("(").append(q).append(")");
    qString.append(" OR ").append(field).append("_ngram:").append("(").append(q).append(")");
    qString.append(" OR ").append(field).append("_exact:").append("(").append(q + "*").append(")^100");
    qString.append(" OR ").append(field).append("_fl:").append("(").append(q + "*").append(")");
    qString.append(" OR ").append(field).append("_py:").append("(").append(q + "*").append(")");
    qString.append(")");
    return;
  }

  private String getOrderStatisticsOperatorsByOrderType(OrderTypes orderType,String numberStr){
    if(StringUtils.isBlank(numberStr)) return "0";
    if(OrderTypes.SALE_RETURN.equals(orderType)){
      return "-"+numberStr;
    }else{
      return numberStr;//+
    }
  }

  @Override
  public ProductThroughSearchResultListDTO queryInOutRecords(ProductThroughSearchDTO throughSearchDTO) throws Exception {
    boolean started = false;
    StringBuilder qString = new StringBuilder();

    if(StringUtils.isNotBlank(throughSearchDTO.getSupplierId())){
      started = generateStringRelatedQuery("related_supplier_id", throughSearchDTO.getSupplierId(), started, qString);
    }else{
      started = generateStringRelatedQuery("related_supplier_name", throughSearchDTO.getSupplierName(), started, qString);
    }
    if(StringUtils.isNotBlank(throughSearchDTO.getCustomerId())){
      started = generateStringRelatedQuery("related_customer_id", throughSearchDTO.getCustomerId(), started, qString);
    }else{
      started = generateStringRelatedQuery("related_customer_name", throughSearchDTO.getCustomerName(), started, qString);
    }
    started = generateStringArrayRelatedQuery("storehouse_id", throughSearchDTO.getStorehouseIds(), started, qString);
    if(!StringUtil.strArrayIsBlank(throughSearchDTO.getProductIds())){
      started = generateStringArrayRelatedUseQueryIn("product_id", throughSearchDTO.getProductIds(), started, qString);//按商品分类
    }
    //按商品统计
    started = generateStringRelatedQuery("product_name_exact", throughSearchDTO.getProductName(), started, qString);
    started = generateStringRelatedQuery("product_brand_exact", throughSearchDTO.getProductBrand(), started, qString);
    started = generateStringRelatedQuery("product_spec_exact", throughSearchDTO.getProductSpec(), started, qString);
    started = generateStringRelatedQuery("product_model_exact", throughSearchDTO.getProductModel(), started, qString);
    started = generateStringRelatedQuery("product_vehicle_brand_exact", throughSearchDTO.getProductVehicleBrand(), started, qString);
    started = generateStringRelatedQuery("product_vehicle_model_exact", throughSearchDTO.getProductVehicleModel(), started, qString);
    started = generateStringRelatedQuery("commodity_code", throughSearchDTO.getCommodityCode(), started, qString);

    started = generateRangeQuery("order_created_time", throughSearchDTO.getStartDate() == null ? null : throughSearchDTO.getStartDate().toString(),
        throughSearchDTO.getEndDate() == null ? null : throughSearchDTO.getEndDate().toString(), started, qString,true);


    if (qString.length() == 0) {
      qString.append("*:*");
    }
    SolrQuery query = new SolrQuery();
    query.setQuery(qString.toString());
    query.setParam("q.op", "OR");

    StringBuilder fQueryString = new StringBuilder();
    if (throughSearchDTO.getShopId() == null) throw new BcgogoException("shopId nullPointException!");
    fQueryString.append("shop_id:").append(throughSearchDTO.getShopId()).append(" AND doc_type:").append(SolrClientHelper.BcgogoSolrDocumentType.INOUT_RECORD_DOC_TYPE.getValue());
    generateStringArrayRelatedQuery("order_type", throughSearchDTO.getOrderType(), true, fQueryString);
    generateStringArrayRelatedQuery("item_type", throughSearchDTO.getItemType(), true, fQueryString);
    query.setFilterQueries(fQueryString.toString());
    query.setParam("fl", "*,score");
    query.setStart((throughSearchDTO.getStartPageNo() - 1) * throughSearchDTO.getMaxRows());
    query.setRows(throughSearchDTO.getMaxRows());
    if (StringUtils.isNotBlank(throughSearchDTO.getSort())) {
      query.setParam("sort", throughSearchDTO.getSort());
    }
      query.setParam(StatsParams.STATS, "true");
      query.setParam(StatsParams.STATS_FIELD, throughSearchDTO.getStatsFields());
    QueryResponse response = SolrClientHelper.getOrderItemSolrClient().query(query);

    SolrDocumentList docs = response.getResults();

    ProductThroughSearchResultListDTO resultListDTO = new ProductThroughSearchResultListDTO();

      Map<String, FieldStatsInfo> statsInfoMap = response.getFieldStatsInfo();
      if (statsInfoMap!=null && !statsInfoMap.isEmpty()) {
          Map<String, Double> sums = new HashMap<String, Double>();
          for (String statsField : throughSearchDTO.getStatsFields()) {
              FieldStatsInfo statsInfo = response.getFieldStatsInfo().get(statsField);
              if (statsInfo != null) {
                  sums.put(statsField.toUpperCase(), NumberUtil.round((Double)statsInfo.getSum(), 2));
              }
          }

          resultListDTO.setTotalAmounts(sums);
      }

    resultListDTO.setNumFound(docs.getNumFound());
    for (SolrDocument doc : docs) {
      ProductThroughSearchResultDTO resultDTO = new ProductThroughSearchResultDTO();

      resultDTO.setShopId(NumberUtil.longValue(doc.getFirstValue("shop_id")));
      resultDTO.setOrderId(NumberUtil.longValue(doc.getFirstValue("order_id")));
      resultDTO.setOrderType((String) doc.getFirstValue("order_type"));
      resultDTO.setCreatedTime(NumberUtil.longValue(doc.getFirstValue("order_created_time")));
      resultDTO.setOrderReceiptNo((String)doc.getFirstValue("order_receipt_no"));
      //resultDTO.setItemCount(NumberUtil.round(NumberUtil.doubleValue(doc.getFirstValue("item_count").toString(), 0d),1));

      resultDTO.setRelatedSupplierName((String)doc.getFirstValue("related_supplier_name"));
      resultDTO.setRelatedCustomerName((String)doc.getFirstValue("related_customer_name"));

      resultDTO.setItemType((String)doc.getFirstValue("item_type"));
      resultDTO.setProductId(NumberUtil.longValue(doc.getFirstValue("product_id")));
      resultDTO.setProductName((String)doc.getFirstValue("product_name"));
      resultDTO.setProductBrand((String)doc.getFirstValue("product_brand"));
      resultDTO.setProductModel((String)doc.getFirstValue("product_model"));
      resultDTO.setProductSpec((String)doc.getFirstValue("product_spec"));
      resultDTO.setProductVehicleBrand((String)doc.getFirstValue("product_vehicle_brand"));
      resultDTO.setProductVehicleModel((String)doc.getFirstValue("product_vehicle_model"));
      resultDTO.setCommodityCode((String) doc.getFirstValue("commodity_code"));
      resultDTO.setStorehouseName((String) doc.getFirstValue("storehouse_name"));
      resultDTO.setUnit((String) doc.getFirstValue("unit"));

     /* resultDTO.setItemPrice(NumberUtil.round(NumberUtil.doubleValue(doc.getFirstValue("item_price").toString(), 0d),1));
      resultDTO.setItemTotalCostPrice(NumberUtil.round(NumberUtil.doubleValue(doc.getFirstValue("item_total_cost_price").toString(), 0d),1));
      resultDTO.setItemTotal(NumberUtil.round(NumberUtil.doubleValue(doc.getFirstValue("item_total").toString(), 0d),1));*/

        if(doc.getFirstValue("item_price")==null){
            resultDTO.setItemPrice(0d);
        }else {
            resultDTO.setItemPrice((Double)doc.getFirstValue("item_price"));
        }

        if(doc.getFirstValue("item_count")==null){
            resultDTO.setItemCount(0d);
        }else {
            resultDTO.setItemCount((Double)doc.getFirstValue("item_count"));
        }

        if(doc.getFirstValue("item_total")==null){
            resultDTO.setItemTotal(0d);
        }else {
            resultDTO.setItemTotal((Double)doc.getFirstValue("item_total"));
        }
        if(doc.getFirstValue("item_total_cost_price")==null){
            resultDTO.setItemTotalCostPrice(0d);
        }else {
            resultDTO.setItemTotalCostPrice((Double)doc.getFirstValue("item_total_cost_price"));
        }
      resultListDTO.getInOutRecords().add(resultDTO);
    }
    return resultListDTO;
  }

  //join
  private void generateFilterJoinCustomerOrSupplierQuery(JoinSearchConditionDTO joinSearchConditionDTO,SolrQuery query) {
    if(joinSearchConditionDTO==null) return;
    StringBuilder joinQueryString = new StringBuilder();
    boolean started = false;
    if(SolrClientHelper.BcgogoSolrCore.CUSTOMER_SUPPLIER_CORE.getValue().equals(joinSearchConditionDTO.getFromIndex())){
      joinQueryString.append("doc_type:").append(SolrClientHelper.BcgogoSolrDocumentType.CUSTOMER_SUPPLIER.getValue());
      started = true;
      if(joinSearchConditionDTO.getShopId()!=null){
        joinQueryString.append(" AND shop_id:").append(joinSearchConditionDTO.getShopId());
      }
      started = generateLongRelatedQuery("area_ids", joinSearchConditionDTO.getAreaId(), started, joinQueryString);
      started = generateStringArrayRelatedQuery("customer_or_supplier", joinSearchConditionDTO.getCustomerOrSupplier(), started, joinQueryString);

    }
    StringBuilder qString = new StringBuilder();
    if(StringUtils.isNotBlank(joinQueryString.toString())){
      qString.append("{!join from=").append(joinSearchConditionDTO.getFromColumn()).append(" to=").append(joinSearchConditionDTO.getToColumn()).append(" fromIndex=").append(joinSearchConditionDTO.getFromIndex()).append("}");
      qString.append("(").append(joinQueryString).append(")");
      query.addFilterQuery(qString.toString());
    }
  }



  @Override
  public OrderSearchResultListDTO queryPreBuyRecommend(OrderSearchConditionDTO conditions) throws Exception {
    boolean started = false;
    StringBuilder qString = new StringBuilder();

    if(OrderSearchConditionDTO.PreBuyOrderStatus.VALID.equals(conditions.getPreBuyOrderStatus())){
      started = generateRangeQuery("end_time",DateUtil.getTheDayTime().toString(),null, started, qString,true);
    }else if(OrderSearchConditionDTO.PreBuyOrderStatus.EXPIRED.equals(conditions.getPreBuyOrderStatus())){
      started = generateRangeQuery("end_time",null,DateUtil.getTheDayTime().toString(), started, qString,false);
    }
    String customMatchPContent = SolrQueryUtils.escape(conditions.getCustomMatchPContent());
    String customMatchPVContent = SolrQueryUtils.escape(conditions.getCustomMatchPVContent());

    if (!StringUtils.isBlank(customMatchPContent)) {
      if (started) {
        qString.append(" AND ");
      }
      qString.append(" custom_match_p_content").append(":").append("(").append(customMatchPContent).append(")");
      started = true;
    }
    if (!StringUtils.isBlank(customMatchPVContent)) {
      if (started) {
        qString.append(" AND ");
      }
      qString.append(" custom_match_pv_content").append(":").append("(").append(customMatchPVContent).append(")");
      started = true;
    }

    if (!ArrayUtils.isEmpty(conditions.getExcludeShopIds())) {
      if (started) {
        qString.append(" AND ");
      }
      qString.append("!shop_id:(");
      for (int i = 0, max = conditions.getExcludeShopIds().length; i < max; i++) {
        qString.append(conditions.getExcludeShopIds()[i]);
        if (i < (max - 1)) qString.append(" OR ");
      }
      qString.append(")");
      started = true;
    }

    if (qString.length() == 0) {
      qString.append("*:*");
    }
    SolrQuery query = new SolrQuery();
    query.setQuery(qString.toString());
    query.setParam("q.op", "OR");

    StringBuilder fQueryString = new StringBuilder();
    fQueryString.append(" doc_type:").append(SolrClientHelper.BcgogoSolrDocumentType.ORDER_ITEM_DOC_TYPE.getValue());
    fQueryString.append(" AND order_type:").append(OrderTypes.PRE_BUY_ORDER.toString());
    fQueryString.append(" AND item_type:").append(ItemTypes.MATERIAL.toString());

    if(conditions.getShopKind() !=null){
      fQueryString.append(" AND shop_kind:").append(conditions.getShopKind());
    }
    query.setFilterQueries(fQueryString.toString());


    query.setParam("fl", "*,score");
    query.setStart(conditions.getRowStart());
    query.setRows(conditions.getPageRows());

    QueryResponse response = SolrClientHelper.getOrderItemSolrClient().query(query);

    return processSearchOrderItemResponse(response, conditions);
  }

  private boolean generateSearchStrategyQuery(String[] searchStrategy, boolean started, StringBuilder qString) {
    if (ArrayUtils.isEmpty(searchStrategy)) return started;
    for (String value : searchStrategy) {
      if (StringUtils.isBlank(value)) continue;
      if (OrderSearchConditionDTO.SEARCHSTRATEGY_ONLINE_ORDERS.equals(value)) {
        if (started) {
          qString.append(" AND ");
        }
        qString.append("customer_or_supplier_shop_id:[\"\" TO *]");
        started = true;
      }
    }
    return started;
  }

  //在线销售单，新订单，用客户名查询时用到
  //1.选择已有的客户  2.输入的客户不在本地客户列表里  3.输入的客户在本地列表里
  private boolean generateShopNameAndCustomerShopIdQuery(Long[] customerShopIds, String shopName, boolean exactSearch, boolean started, StringBuilder qString) {
     if(ArrayUtils.isEmpty(customerShopIds) && StringUtil.isEmpty(shopName)) {
        return started;
     }
     // 2
     if(ArrayUtils.isEmpty(customerShopIds)) {
       return generateShopNameFuzzyRelatedQuery("shop_name", shopName, true, started, qString);
     } else if(exactSearch) {
       //1
        if(started) {
          qString.append(" AND ");
        }
       qString.append(" shop_id:(" + customerShopIds[0] + ") ");
       started = true;
     } else {
       //3
       if(started) {
         qString.append(" AND (");
       }
       qString.append(" shop_id:( ");
       for(int i = 0; i < customerShopIds.length; i++) {
           if(i != customerShopIds.length - 1) {
             qString.append(customerShopIds[i] + " OR ");
           } else {
             qString.append(customerShopIds[i] + " )");
           }
       }
       qString.append(" )");
       started = true;
     }
    return started;
  }
}
