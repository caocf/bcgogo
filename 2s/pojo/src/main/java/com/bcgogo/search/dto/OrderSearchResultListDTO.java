package com.bcgogo.search.dto;

import com.bcgogo.common.Pager;
import com.bcgogo.utils.NumberUtil;
import org.apache.commons.collections.MapUtils;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: caiweili
 * Date: 8/3/12
 * Time: 8:09 PM
 */
public class OrderSearchResultListDTO {
  public static final String ORDER_DEBT_AND_SETTLED_AMOUNT = "debt_and_settled_amount";
  public static final String ORDER_TOTAL_AMOUNT = "order_total_amount";
  public static final String ORDER_DEBT_AMOUNT = "order_debt_amount";
  public static final String ORDER_SETTLED_AMOUNT = "order_settled_amount";

  private Map<String, Long> currentPageTotalCounts;
  private Map<String, Double> currentPageTotalAmounts;
  private List<OrderSearchResultDTO> orders = new ArrayList<OrderSearchResultDTO>();
  private Map<String, Long> totalCounts;
  private Map<String, Double> totalAmounts;
  private List orderTypeStat;

  private List<OrderItemSearchResultDTO> orderItems = new ArrayList<OrderItemSearchResultDTO>();
  private long itemNumFound;
  private long numFound;

  private Map<String,String> otherDataMap = new HashMap<String, String>();
  private Pager pager;

  public Pager getPager() {
    return pager;
  }

  public void setPager(Pager pager) {
    this.pager = pager;
  }

  public void addTotalDebtSettled() {
    Set<String> keySet = new HashSet<String>(totalAmounts.keySet());
    for (String key : keySet) {
      if (key.startsWith(ORDER_DEBT_AMOUNT.toUpperCase())) {
        String newkey = ORDER_DEBT_AND_SETTLED_AMOUNT.toUpperCase() + key.substring(ORDER_DEBT_AMOUNT.length());
        totalAmounts.put(newkey, NumberUtil.doubleVal(totalAmounts.get(key)) + NumberUtil.doubleVal(totalAmounts.get(newkey)));
      }
      if (key.startsWith(ORDER_SETTLED_AMOUNT.toUpperCase())) {
        String newkey = ORDER_DEBT_AND_SETTLED_AMOUNT.toUpperCase() + key.substring(ORDER_SETTLED_AMOUNT.length());
        totalAmounts.put(newkey, NumberUtil.doubleVal(totalAmounts.get(key)) + NumberUtil.doubleVal(totalAmounts.get(newkey)));
      }
    }
  }


  public long getNumFound() {
    return numFound;
  }

  public void setNumFound(long numFound) {
    this.numFound = numFound;
  }

  public Map<String, Long> getCurrentPageTotalCounts() {
    return currentPageTotalCounts;
  }

  public void setCurrentPageTotalCounts(Map<String, Long> currentPageTotalCounts) {
    this.currentPageTotalCounts = currentPageTotalCounts;
  }

  public Map<String, Double> getCurrentPageTotalAmounts() {
    return currentPageTotalAmounts;
  }

  public void setCurrentPageTotalAmounts(Map<String, Double> currentPageTotalAmounts) {
    if (MapUtils.isEmpty(currentPageTotalAmounts)) return;
    for (String key : currentPageTotalAmounts.keySet()) {
      currentPageTotalAmounts.put(key, NumberUtil.round(currentPageTotalAmounts.get(key), NumberUtil.MONEY_PRECISION));
    }
    this.currentPageTotalAmounts = currentPageTotalAmounts;
  }

  public List<OrderSearchResultDTO> getOrders() {
    return orders;
  }

  public void setOrders(List<OrderSearchResultDTO> orders) {
    this.orders = orders;
  }

  public Map<String, Long> getTotalCounts() {
    return totalCounts;
  }

  public void setTotalCounts(Map<String, Long> totalCounts) {
    this.totalCounts = totalCounts;
  }

  public Map<String, Double> getTotalAmounts() {
    return totalAmounts;
  }

  public void setTotalAmounts(Map<String, Double> totalAmounts) {
    if (MapUtils.isEmpty(totalAmounts)) return;
    for (String key : totalAmounts.keySet()) {
      totalAmounts.put(key, NumberUtil.round(totalAmounts.get(key), NumberUtil.MONEY_PRECISION));
    }
    this.totalAmounts = totalAmounts;
  }

  public List<OrderItemSearchResultDTO> getOrderItems() {
    return orderItems;
  }

  public void setOrderItems(List<OrderItemSearchResultDTO> orderItems) {
    this.orderItems = orderItems;
  }

  public long getItemNumFound() {
    return itemNumFound;
  }

  public void setItemNumFound(long itemNumFound) {
    this.itemNumFound = itemNumFound;
  }

  public List getOrderTypeStat() {
    return orderTypeStat;
  }

  public void setOrderTypeStat(List orderTypeStat) {
    this.orderTypeStat = orderTypeStat;
  }

  public Map<String, String> getOtherDataMap() {
    return otherDataMap;
  }

  public void setOtherDataMap(Map<String, String> otherDataMap) {
    this.otherDataMap = otherDataMap;
  }
}
