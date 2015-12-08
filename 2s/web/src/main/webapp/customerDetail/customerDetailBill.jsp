<%--
  Created by IntelliJ IDEA.
  User: liuWei
  Date: 13-1-9
  Time: 上午10:48
  To change this template use File | Settings | File Templates.
--%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%@ page contentType="text/html;charset=UTF-8" language="java" %>


<form id="statementAccountOrderForm" name="statementAccountOrderForm"
      action="statementAccount.do?method=getStatementAccountOrder" method="post">
  <input type="hidden" name="startPageNo" id="startPageNo" value="1">
  <input type="hidden" name="maxRows" id="maxRows" value="15">
  <input type="hidden" name="customerOrSupplierId" id="customerOrSupplierId" value="${customerOrSupplierId}"
         autocomplete="off">
  <input type="hidden" name="orderType" id="orderType" value="${orderType}" autocomplete="off">
  <input type="hidden" name="customerOrSupplierIdSArray" id="customerOrSupplierIdSArray" autocomplete="off">

  <div class="titBody">
    <div>
      <div class="lineTitle" style="padding-right: 3px;width: 988px;">对账单查询</div>
      <div class="lineBody lineAll">
        <div class="i_height"></div>
        <div class="divTit">
          <span class="spanName">对账日期</span>&nbsp;<a class="btnList" name="date_select">昨天</a>&nbsp;
          <a class="btnList" name="date_select">今天</a>&nbsp;<a class="btnList" name="date_select">最近一周</a>&nbsp;
          <a class="btnList" name="date_select">最近一月</a>&nbsp;<a class="btnList" name="date_select">最近一年</a>&nbsp;
          <input type="text" id="startDateBill" name="startTimeStr" readonly="readonly" class="txt" autocomplete="off">至
          <input type="text" id="endDateBill" readonly="readonly" name="endTimeStr" class="txt" autocomplete="off">
        </div>
        <c:if test="${empty customerOrSupplierId}">
          <div class="divTit divWarehouse member">
            <span class="spanName">对账对象</span>

            <div class="warehouseList">
              <input id="customerOrSupplierName" name="customerOrSupplierName" type="text" class="txt"
                     style="width:134px;" value="${customerOrSupplierName}" placeholder="客户/供应商" autocomplete="off"/>
              <input id="mobile" name="mobile" type="text" class="txt" style="width:134px;" value="${mobile}"
                     placeholder="手机号" autocomplete="off"/>
            </div>
          </div>
        </c:if>

        <div class="divTit divWarehouse member">
          <span class="spanName">单据信息</span>

          <div class="warehouseList">
            <input id="receiptNo" name="receiptNo" type="text" class="txt" style="width:134px;" placeholder="对账单号"
                   autocomplete="off"/>
            <input id="operator" name="operator" type="text" class="txt" style="width:134px;" placeholder="结算人"
                   autocomplete="off"/>
          </div>
        </div>
        <div class="divTit button_conditon button_search"><a id="clearAway"
                                                             data-page-type="<c:if test="${!empty customerOrSupplierId}">customerOrSupplierInfo</c:if>" class="blue_color clean">清空条件</a><a id="searchStatementAccount" class="button">查询</a></div>
      </div>
      <div class="lineBottom"></div>
      <div class="clear i_height"></div>
    </div>

    <div class="clear"></div>

    <div class="supplier group_list2 listStyle">
      <div style="float: left;width:300px;">
        <strong>共有<span id="pageTotalSpan">0</span>条对账记录</strong>
      </div>
      <div class="wordTitle" style="float: right;width: 300px;padding-top: 6px;">
        <input id="currentStatementAccount" type="button" value="生成本期对账" class="addNew"
               style="<c:if test="${customerOrSupplierId==null}">display:none;</c:if>"/>
        <span class="shangYu" style="<c:if test="${customerOrSupplierId==null}">display:none;</c:if>">上期对账余额：<strong
          class="red_color" id="lastStatementOrder">应收 0</strong>元</span>
      </div>
    </div>
    <div class="clear"></div>

    <table id="stateAccountOrderTable" cellpadding="0" cellspacing="0" class="tabSlip tabPick">
      <col width="50">
      <col width="120">
      <col width="120">
      <col width="130">
      <col width="130">
      <col width="120">
      <col width="120">
      <col width="120">
      <col width="100">
      <col width="100">
      <tr class="divSlip">
        <td>NO</td>
        <td>对账日期</td>
        <td>对账类型</td>
        <td>对账单号</td>
        <td>对账对象</td>
        <td>对账总额</td>
        <td>本期收支</td>
        <td>本期优惠</td>
        <td>本期挂账</td>
        <td>结算人</td>
      </tr>
    </table>
    <div id="noReceivableList" style="display: none;text-align:center;color: #444443;">对不起！没有符合条件的对账单信息！</div>
    <div class="height"></div>
    <jsp:include page="/common/pageAJAX.jsp">
      <jsp:param name="url" value="statementAccount.do?method=searchStatementAccountOrder"></jsp:param>
      <jsp:param name="dynamical" value="dynamicalStatementAccountList"></jsp:param>
      <jsp:param name="data" value="{startPageNo:'1',maxRows:15}"></jsp:param>
      <jsp:param name="jsHandleJson" value="initStatementAccountOrder"></jsp:param>
      <jsp:param name="display" value="none"></jsp:param>
    </jsp:include>
  </div>

</form>


