package com.bcgogo.common;

import org.springframework.beans.support.PagedListHolder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 13-2-18
 * Time: 上午9:50
 * To change this template use File | Settings | File Templates.
 */
public class PagingDetailResult<T,P> extends PagingListResult<T>{
  private Map<String,P> totals= new HashMap<String, P>();

  public Map<String, P> getTotals() {
    return totals;
  }

  public void setTotals(Map<String, P> totals) {
    this.totals = totals;
  }
}
