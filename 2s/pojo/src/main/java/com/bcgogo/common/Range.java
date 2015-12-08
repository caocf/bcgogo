package com.bcgogo.common;

import com.bcgogo.utils.NumberUtil;

import java.util.Comparator;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 13-7-19
 * Time: 下午3:55
 * To change this template use File | Settings | File Templates.
 */
public class Range {

 private Long start;
 private Long end;

  public Long getStart() {
    return start;
  }

  public void setStart(Long start) {
    this.start = start;
  }

  public Long getEnd() {
    return end;
  }

  public void setEnd(Long end) {
    this.end = end;
  }

   //比较器的内部类
  public static final Comparator<Range> SORT_BY_START = new Comparator<Range>() {
    public int compare(Range r1, Range r2) {
      return Integer.valueOf (String.valueOf(r1.getStart()-r2.getEnd()));
    }
  };

}
