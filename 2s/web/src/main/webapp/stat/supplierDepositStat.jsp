<%@ taglib prefix="bcgogo" uri="http://www.bcgogo.com/taglibs/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>预付款</title>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/statistics<%=ConfigController.getBuildVersion()%>.css"/>
    <%@include file="/WEB-INF/views/header_script.jsp" %>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.js"></script>
    <script type="text/javascript" src="js/script<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/head<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/utils/tableUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/supplierDepositStat<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">
        defaultStorage.setItem(storageKey.MenuUid, "WEB.STAT.DEPOSIT_STAT.SUPPLIER");
    </script>
</head>
<body class="bodyMain">
<%@include file="/WEB-INF/views/header_html.jsp" %>
    <div class="i_main clear">
        <div class="mainTitles">
            <div class="titleWords">预付款统计</div>
             <bcgogo:hasPermission
                    permissions="WEB.STAT.DEPOSIT_STAT.BASE">
                <div class="titleList">
                    <bcgogo:hasPermission permissions="WEB.STAT.DEPOSIT_STAT.CUSTOMER">
                        <a id="payableStatBtn" action-type="menu-click" menu-name=""
                           href="depositOrdersStat.do?method=renderCustomerDepositOrderQueryPage&menu-uid=WEB.STAT.DEPOSIT_STAT.CUSTOMER"
                           >预收款</a>
                    </bcgogo:hasPermission>
                    <bcgogo:hasPermission permissions="WEB.STAT.DEPOSIT_STAT.SUPPLIER">
                        <a id="receivableStatBtn" action-type="menu-click" menu-name=""
                           href="depositOrdersStat.do?method=renderSupplierDepositOrderQueryPage&menu-uid=WEB.STAT.DEPOSIT_STAT.SUPPLIER" class="click">预付款</a>
                    </bcgogo:hasPermission>
                </div>
            </bcgogo:hasPermission>
        </div>
        <div class="titBody">
            <div class="lineTitle">预付款统计查询</div>
            <form id="depositStat" name="depositStat" action="depositOrdersStat.do?method=ajaxQueryDepositOrders"
                  method="post">
                <input type="hidden" name="type" id="type" value="${statType}"/>
                <input type="hidden" name="supplierId" id="supplierId"/>
                <input type="hidden" name="maxRows" id="maxRows"/>
                <input type="hidden" name="startPageNo" id="startPageNo" value="1">
                <input type="hidden" name="currentPage" id="currentPage" value="1">

                <div class="lineTop"></div>
                <div class="lineBody bodys">
                    <div class="i_height"></div>
                    <div class="divTit" style="width:100%;">
                        <span class="spanName">统计日期段</span>&nbsp;
                        <a class="btnList" id="my_date_today" name="my_date_select">本日</a>&nbsp;
                        <a class="btnList" id="my_date_thisweek" name="my_date_select">本周</a>&nbsp;
                        <a class="btnList" id="my_date_thismonth" name="my_date_select">本月</a>&nbsp;
                        <a class="btnList" id="my_date_thisyear" name="my_date_select">本年</a>&nbsp;
                        <a class="btnList" id="my_date_self_defining" name="my_date_select">自定义时间</a>&nbsp;
                        <input id="startDate" type="text" style="width:100px;" readonly="readonly" name="startTimeStr"
                               class="my_startdate txt"/>
                        &nbsp;至&nbsp;<input id="endDate" type="text" style="width:100px;" readonly="readonly"
                                            name="endTimeStr" class="my_enddate txt"/>
                    </div>
                    <div class="divTit">
                        <span class="spanName">供应商信息</span>&nbsp;
                        <input type="text" id="supplierName" name="supplierName" class="txt" value="供应商名" initialvalue="供应商名"/>&nbsp;
                        <input type="text" id="supplierMobile" name="supplierMobile" class="txt" value="手机号码" initialvalue="手机号码"/>
                        &nbsp;&nbsp;记录类型&nbsp;
                        <label class="rad"><input type="checkbox" name="inFlag" id="inFlag" checked="checked"
                                                  value="1"/>收款记录</label>
                        &nbsp;
                        <label class="rad"><input type="checkbox" name="outFlag" id="outFlag" checked="checked"
                                                  value="2"/>取用记录</label>
                    </div>
                    <div class="divTit button_conditon">
                        <a class="button" id="statBtn">统计</a>
                        <a class="button" id="resetSearchCondition">重置</a>
                    </div>
                    <%--<div class="divTit button_conditon"><input class="btn" type="submit"
                    onfocus="this.blur();"
                    value="统&nbsp;计"></div>
<div class="divTit button_conditon"><input class="btn" type="button" id="resetSearchCondition"
                    value="重&nbsp;置"/></div>--%>
                </div>
            </form>


            <div class="lineBottom"></div>
            <div class="clear height"></div>
            <div class="group_list">
                预付款统计结果：共<b class="blue_color"><span id="totalCount">100</span></b>&nbsp;条记录&nbsp;&nbsp;其中（收款总额<b
                    class="yellow_color"
                    ><span id="inTotalAmount">100</span></b>&nbsp;取用总额<b
                    class="yellow_color" ><span id="outTotalAmount">200</span></b>&nbsp;）
            </div>
            <div class="clear i_height"></div>
            <div class="cuSearch">
                <div class="cartTop"></div>
                <div class="cartBody">
                    <table class="tab_cuSearch" cellpadding="0" cellspacing="0" id="deposit_orders_table">
                        <col width="120">
                        <col width="150">
                        <col width="90">
                        <col width="80">
                        <col width="80">
                        <col width="100">
                        <col width="110">
                        <col width="80">
                        <col>
                        <tr class="titleBg">
                            <td style="padding-left:10px;">时间<a class="descending" id="depositOrdersTime"></a></td>
                            <td>供应商名</td>
                            <td>手机号</td>
                            <td>类型</td>
                            <td>金额(元)<a class="ascending" id="depositOrdersMoney"></a></td>
                            <td>方式</td>
                            <td>相关单据</td>
                            <td>操作人</td>
                            <td>备注</td>
                        </tr>
                        <tr class="space">
                            <td colspan="9"></td>
                        </tr>

                    </table>
                    <div class="i_height clear"></div>
                    <div class="hidePageAJAX">
                        <bcgogo:ajaxPaging url="depositOrdersStat.do?method=ajaxQueryDepositOrders"
                                           postFn="initDepositOrdersTable"
                                           dynamical="dynamical1" display='none'/>
                    </div>
                    <bcgogo:hasPermission permissions="WEB.STAT.SUPPLIER_DEPOSIT.STAT_PRINT">
                        <a id="printBtn" class="statPrintBtn">打印</a>
                    </bcgogo:hasPermission>
                </div>
                <div class="cartBottom"></div>
            </div>
            <div class="height"></div>


        </div>
        <%@ include file="/common/messagePrompt.jsp" %>
        <div id="mask" style="display:block;position: absolute;"></div>
        <iframe id="iframe_PopupBox" style="position:absolute;z-index:5; left:400px; top:400px; display:none;"
                allowtransparency="true" width="630px" height="450px" frameborder="0" src=""></iframe>

    </div>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>