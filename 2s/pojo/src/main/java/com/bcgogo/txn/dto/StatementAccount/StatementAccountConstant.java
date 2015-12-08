package com.bcgogo.txn.dto.StatementAccount;

/**
 * 对账单相关常量
 * Created by IntelliJ IDEA.
 * User: liuWei
 * Date: 13-1-8
 * Time: 下午4:30
 * To change this template use File | Settings | File Templates.
 */
public class StatementAccountConstant {

  public static final String LAST_STATEMENT_ACCOUNT = "上期对账余额"; //对账单类型

  public static final String DEFAULT_START_DATE = "2000-01-01";//默认对账单开始时间

  public static final String RECEIVABLE_NO_FOUND = "根据对账单查不到实收记录";//根据对账单查不到实收记录

  public static final String STATEMENT_ACCOUNT_ORDER_ERROR = "对账单结算失败";

  public static final String PAY_STR = "应付";

  public static final String RECEIVABLE_STR = "应收";

  public static final String TYPE = "type"; //客户或者供应商对账单类型

  public static final String CUSTOMER_TYPE = "customer";//客户对账单类型

  public static final String SUPPLIER_TYPE = "supplier";//供应商对账单

  public static final String ORDER_TYPE_NULL = "单据类型为空";

  public static final String RECEIVABLE = "收入";

  public static final String PAY = "支出";

  public static final String RECEIPT_NO = "对账单号";

  public static final String OPERATOR = "结算人";

  public static final String MEMBER_ROW_SPAN = "4";

  public static final String NORMAL_ROW_SPAN = "3";

  public static final String ROW_SPAN = "rowspan";

  public static final String TOTAL_ERROR_MESSAGE = "实收、挂账、优惠与总额不符";

  public static final String SETTLE_ERROR_MESSAGE = "实收金额不正确";

  public static final String TOTAL_DEBT_ERROR_MESSAGE = "单据欠款信息被更新，不能对账";

  public static final String SOLR_RECEIVABLE_DEBT_TYPE ="RECEIVABLE";

  public static final String SOLR_PAYABLE_DEBT_TYPE="PAYABLE";

  public static final String ORDER_SELECTED_EMPTY = "请选择你要对账的单据";

  public static final String IDENTITY ="isCustomerAndSupplier";//对账单 如果某个客户又是供应商 statementAccountOrder.identity

  public static final String CUSTOMER_ERROR ="该客户已不是供应商,不能对账";

  public static final String SUPPLIER_ERROR ="该供应商已不是客户,不能对账";

  public static final String CUSTOMER_MORE_ONE = "对账单据列表存在多个客户";

  public static final String SUPPLIER_MORE_ONE = "对账单据列表存在多个供应商";

  public static final String CUSTOMER_RELATED_SUPPLIER_ERROR ="该客户所对应的供应商已更新,不能对账";

  public static final String SUPPLIER_RELATED_CUSTOMER_ERROR ="该供应商所对应的客户已更新,不能对账";
}
