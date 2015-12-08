package com.bcgogo.utils;

import org.apache.commons.lang.StringUtils;

/**
 * Created with IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 13-1-29
 * Time: 上午9:52
 */
public class SQLBuilder {
  public static StringBuilder isNotEmpty(StringBuilder sql, String field) {
    if (sql == null || StringUtil.isEmpty(field) || StringUtils.isEmpty(sql.toString())) return sql;
    sql.append(" ").append(field).append(" is not null and ").append(field).append(" != '' and ").append(field).append(" !='\0' ");
    return sql;
  }

  public static StringBuilder isEmpty(StringBuilder sql, String field) {
    if (sql == null || StringUtil.isEmpty(field) || StringUtils.isEmpty(sql.toString())) return sql;
    sql.append(" and (").append(field).append(" is null or ").append(field).append(" = '' or ").append(field).append(" ='\0') ");
    return sql;
  }

}
