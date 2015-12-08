package com.bcgogo.user.merge;

import com.bcgogo.base.BaseDTO;
import com.bcgogo.base.BaseResult;
import com.bcgogo.user.MergeType;
import com.bcgogo.user.dto.CustomerDTO;

import java.util.List;
import java.util.Locale;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 12-11-22
 * Time: 上午1:47
 * To change this template use File | Settings | File Templates.
 */
public class SearchMergeResult<T> extends BaseResult {
  private boolean mergeRelatedFlag;
  private BaseDTO parent;
  private BaseDTO child;
  private Locale locale;
  private List<T> results;
  private MergeType mergeType;

  public boolean isMergeRelatedFlag() {
    return mergeRelatedFlag;
  }

  public void setMergeRelatedFlag(boolean mergeRelatedFlag) {
    this.mergeRelatedFlag = mergeRelatedFlag;
  }

  public BaseDTO getParent() {
    return parent;
  }

  public void setParent(BaseDTO parent) {
    this.parent = parent;
  }

  public BaseDTO getChild() {
    return child;
  }

  public void setChild(BaseDTO child) {
    this.child = child;
  }

  public void setChild(CustomerDTO child) {
    this.child = child;
  }

  public List<T> getResults() {
    return results;
  }

  public void setResults(List<T> results) {
    this.results = results;
  }

  public Locale getLocale() {
    return locale;
  }

  public void setLocale(Locale locale) {
    this.locale = locale;
  }

  public MergeType getMergeType() {
    return mergeType;
  }

  public void setMergeType(MergeType mergeType) {
    this.mergeType = mergeType;
  }
}
