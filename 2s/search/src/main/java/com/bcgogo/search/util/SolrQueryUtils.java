package com.bcgogo.search.util;

import com.bcgogo.config.service.IConfigService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.utils.ShopConstant;
import com.bcgogo.utils.SolrConstant;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.queryparser.classic.QueryParser;

/**
 * @author xzhu  07/08/2012
 */
public class SolrQueryUtils {
  /**
   * 下拉建议显示数量默认设置
   *
   * @return
   */
  public static int getSelectOptionNumber() {
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    String config = configService.getConfig(SolrConstant.SELECT_OPTION_NUMBER, ShopConstant.BC_SHOP_ID);
    int selectOptionNum = 0;
    if(config!=null){
      selectOptionNum= Integer.valueOf(config);
    }
    if (selectOptionNum <= 0) {
      selectOptionNum = 15;
    }
    return selectOptionNum;
  }


  /**
   * Memory中最近更新或者添加商品保存的超时时间设置  单位为 秒

   * @return
   */
  public static int getRecentChangedProductExpirationTime() {
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    String config = configService.getConfig("RecentChangedProductExpirationTime", ShopConstant.BC_SHOP_ID);
    int expirationTime = 0;
    if(config!=null){
      expirationTime = Integer.valueOf(config);
    }
    if (expirationTime <= 0) {
      expirationTime = 60;
    }
    return expirationTime;
  }

  /**
   * analyze english query words
   */
  public static String analyzeEngQueryStr(String q) {
    if (StringUtils.isBlank(q)) return q;
    String str = q.trim();
    return "(" + escape(str).replaceAll("(\\S+)", "$1*") + ")";
  }

  /**
   * escape special character before query
   */
  public static String escape(String queryStr) {
	  if (queryStr == null) return queryStr;
    if (StringUtils.isBlank(queryStr)) return queryStr.trim();
    if(queryStr.trim().length()>=50) queryStr = queryStr.trim().substring(0,50);
    queryStr = QueryParser.escape(queryStr).trim();
    if(queryStr.equals("AND") || queryStr.equals("OR") || queryStr.equals("NOT")) queryStr = queryStr.toLowerCase();
    return queryStr;
  }

  public static void main(String[] args) {
    System.out.println(analyzeEngQueryStr("123:33"));
  }
}
