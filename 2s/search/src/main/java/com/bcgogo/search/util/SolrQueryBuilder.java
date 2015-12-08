package com.bcgogo.search.util;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;

/**
 * @author xzhu  07/08/2012
 */
public class SolrQueryBuilder {
  private SolrQuery query;

  public SolrQuery getSolrQuery() {
    return query;
  }

  /**
   * @param queryStr
   * @param filterQueryStr each filter query will be cached
   */
  public SolrQueryBuilder(String queryStr, String... filterQueryStr) {
    query = new SolrQuery();
    query.setQuery(queryStr);
    if (filterQueryStr != null) {
      query.setFilterQueries(filterQueryStr);
    }
  }

  /**
   * @param queryStr
   * @param filterQueryStr each filter query will be cached
   * @param sort
   */
  public SolrQueryBuilder(String queryStr, String[] filterQueryStr, String sort) {
    this(queryStr, filterQueryStr);
    setSortQuery(sort);
  }

  private void setSortQuery(String sort) {
    if (StringUtils.isNotBlank(sort))
      query.setParam("sort", sort);
  }

  /**
   * @param queryStr
   * @param filterQueryStr each filter query will be cached
   * @param start
   * @param rows
   */
  public SolrQueryBuilder(String queryStr, String[] filterQueryStr, int start, int rows) {
    this(queryStr, filterQueryStr);
    query.setStart(start);
    query.setRows(rows);
  }

  /**
   * @param queryStr
   * @param filterQueryStr each filter query will be cached
   * @param start
   * @param rows
   * @param sort
   */
  public SolrQueryBuilder(String queryStr, String[] filterQueryStr, int start, int rows, String sort) {
    this(queryStr, filterQueryStr, sort);
    query.setStart(start);
    query.setRows(rows);
  }

  /**
   * only affect main query<br/>
   * query str example: *:* && _val_:"div(age,status)"
   */
  public SolrQueryBuilder enableFuncQuery() {
    query.set("defType", "func");
    return this;
  }


  public SolrQueryBuilder enableDebugQuery() {
    query.setParam("debugQuery", "true");
    return this;
  }

  //覆盖schema.xml的defaultOperator（有空格时用"AND"还是用"OR"操作逻辑）
  public SolrQueryBuilder setQOPAndQuery() {
    query.setParam("q.op", "AND");
    return this;
  }

  public SolrQueryBuilder setQOPORQuery() {
    query.setParam("q.op", "OR");
    return this;
  }

  /**
   * @param fields multi values with comma or blank space
   */
  public SolrQueryBuilder setReturnFields(String fields) {
    query.set("fl", fields);
    return this;
  }

  /**
   * @param fields
   * @param ngroups //    true 只返回统计数量   默认为false
   */
  public SolrQueryBuilder setGroupFields(String fields, Boolean ngroups) {
    query.setParam("group", "true");
    query.setParam("group.field", fields);
    query.setParam("group.main", "true");
    if (ngroups != null) {
      query.setParam("group.ngroups", ngroups);
    }
    return this;
  }

}
