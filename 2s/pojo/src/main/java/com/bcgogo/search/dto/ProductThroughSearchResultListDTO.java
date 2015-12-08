package com.bcgogo.search.dto;

import com.bcgogo.utils.NumberUtil;
import org.apache.commons.collections.MapUtils;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: caiweili
 * Date: 8/3/12
 * Time: 8:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class ProductThroughSearchResultListDTO {
  private static final String ITEM_TOTAL="item_total";
  private static final String ITEM_TOTAL_COST_PRICE="item_total_cost_price";
  private List<ProductThroughSearchResultDTO> inOutRecords = new ArrayList<ProductThroughSearchResultDTO>();
  private Map<String, Double> totalAmounts;
    private Map<String, Double> currentPageTotalAmounts;

  private long numFound;

  public long getNumFound() {
    return numFound;
  }
  public void setNumFound(long numFound) {
    this.numFound = numFound;
  }

  public List<ProductThroughSearchResultDTO> getInOutRecords() {
    return inOutRecords;
  }

  public void setInOutRecords(List<ProductThroughSearchResultDTO> inOutRecords) {
    this.inOutRecords = inOutRecords;
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
}
