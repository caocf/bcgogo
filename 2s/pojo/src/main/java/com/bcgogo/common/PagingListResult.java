package com.bcgogo.common;

import java.util.List;

/**
 * Ajax分页结果返回封装类
 * 与<bcgogo:ajaxPaging /> Tag合作使用
 * User: Jimuchen
 * Date: 13-1-9
 * Time: 下午4:04
 */
public class PagingListResult<T> extends ListResult<T>{
  Pager pager;

  public PagingListResult() {
  }

  public PagingListResult(List<T> results, boolean success, Pager pager) {
    super(results, success);
    this.pager = pager;
  }

  public Pager getPager() {
    return pager;
  }

  public void setPager(Pager pager) {
    this.pager = pager;
    if(this.pager!=null)
    this.setTotal(pager.getTotalRows());
  }
}
