package com.bcgogo.search.dto;

import java.util.Comparator;

/**
 * 在数据库没有时间字段用于排序的时候，需要将单据时间组装到DTO以后存入List，本类用于OrderSearchResultDTOList对vestDate进行排序
 * Created by IntelliJ IDEA.
 * User: Wei Lingfeng
 * Date: 13-1-10
 * Time: 下午7:26
 * To change this template use File | Settings | File Templates.
 */
public class OrderSearchResultComparator implements Comparator<OrderSearchResultDTO> {
  @Override
  public int compare(OrderSearchResultDTO o1, OrderSearchResultDTO o2) {
    if(o1.getVestDate() > o2.getVestDate()) {
      return -1;
    } else if(o1.getVestDate() == o2.getVestDate()) {
      if(o1.getCreatedTime() >= o2.getCreatedTime()){
        return -1;
      }else{
        return 1;
      }
    } else {
      return 1;
    }
  }
}