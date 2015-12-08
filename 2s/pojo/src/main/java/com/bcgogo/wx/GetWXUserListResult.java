package com.bcgogo.wx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 14-8-29
 * Time: 下午1:02
 * To change this template use File | Settings | File Templates.
 */
public class GetWXUserListResult {
  private String total;
  private String count;
  private String next_openid;
  private Map<String,List<String>> data;

  {
    data=new HashMap<String, List<String>>();
    data.put("openid",new ArrayList<String>());
  }

  public String getTotal() {
    return total;
  }

  public void setTotal(String total) {
    this.total = total;
  }

  public String getCount() {
    return count;
  }

  public void setCount(String count) {
    this.count = count;
  }

  public String getNext_openid() {
    return next_openid;
  }

  public void setNext_openid(String next_openid) {
    this.next_openid = next_openid;
  }

  public Map<String, List<String>> getData() {
    return data;
  }

  public void setData(Map<String, List<String>> data) {
    this.data = data;
  }
}
