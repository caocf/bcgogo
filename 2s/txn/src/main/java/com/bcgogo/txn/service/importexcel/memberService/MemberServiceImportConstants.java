package com.bcgogo.txn.service.importexcel.memberService;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: cfl
 * Date: 12-9-5
 * Time: 下午5:55
 * To change this template use File | Settings | File Templates.
 */
public class MemberServiceImportConstants {
  /**
   * 字段名字
   */
  public class FieldName {
    public static final String MEMBER_NO = "memberNo";
    public static final String MEMBER_NO_DESC = "会员号";

    public static final String SERVICE_NAME = "serviceName";
    public static final String SERVICE_NAME_DESC = "服务名称";

    public static final String TIMES = "times";
    public static final String TIMES_DESC = "服务次数";

    public static final String DEADLINE = "deadline";
    public static final String DEADLINE_DESC = "失效日期";
  }

  public static List<String> fieldList;

  static {
    fieldList = new ArrayList<String>();
    fieldList.add(FieldName.MEMBER_NO + "_" + FieldName.MEMBER_NO_DESC);
    fieldList.add(FieldName.SERVICE_NAME + "_" + FieldName.SERVICE_NAME_DESC);
    fieldList.add(FieldName.TIMES + "_" + FieldName.TIMES_DESC);
    fieldList.add(FieldName.DEADLINE + "_" + FieldName.DEADLINE_DESC);
  }

  public class CheckResultMessage {
    public static final String EMPTY_MEMBER_NO = "会员号为空！";
    public static final String MEMBER_NO_TOO_LONG = "会员号过长！";

    public static final String EMPTY_SERVICE_NAME = "服务名称为空！";
    public static final String SERVICE_NAME_TOO_LONG = "服务名称过长！";

    public static final String EMPTY_TIMES = "次数为空！";
    public static final String NOT_INTEGER = "不是整数！";

    public static final String FORMAT_NOT_EXPECT = "格式不对！";
    public static final String MONTH_FORMAT = "月份小于1或者大于12！";

    public static final String MEMBER_NOT_EXIST_IN_TABLE = "会员号在系统中不存在！";
  }

  public class FieldLength {
    public static final int FIELD_MEMBER_NO= 25;

    public static final int FIELD_SERVICE_NAME=50;
  }
}

