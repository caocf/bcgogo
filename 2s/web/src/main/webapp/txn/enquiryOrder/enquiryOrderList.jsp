<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<html xmlns="http://www.w3.org/1999/xhtml" pagetype='order'>
<head xmlns="http://www.w3.org/1999/xhtml">
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title >询价比价</title>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
    <%@include file="/WEB-INF/views/header_script.jsp" %>
    <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/page/txn/enquiry/enquiryOrderList<%=ConfigController.getBuildVersion()%>.js"></script>
    <%--<script type="text/javascript" src="js/page/search/customerOrVehicleBaseSuggestion<%=ConfigController.getBuildVersion()%>.js"></script>--%>
    <script type="text/javascript">
        defaultStorage.setItem(storageKey.MenuUid,"WEB.SCHEDULE.ENQUIRY_LIST");
    </script>
</head>
<body style="color: #000000;position: absolute;">
<%@ include file="/WEB-INF/views/header_html.jsp" %>
<div class="i_main clear">
    <div class="mainTitles">
        <div class="titleWords">询价比价</div>
    </div>
    <div class="titBody">
        <div style="width:1000px;" class="added-management">
            <div style="padding:10px;" class="group-notice">
                <form id="enquirySearchForm">
                    <table width="100%" cellspacing="0" border="0">
                    <tbody>
                    <tr>
                        <td width="8%">未报价：</td>
                        <td width="10%">
                            <span class="line-info" id="toAllUnResponse">共
                                <a class="number">${TODAY_UN_RESPONSE_COUNT+BEFORE_TODAY_UN_RESPONSE_COUNT}&nbsp;</a>条
                            </span>
                        </td>
                        <td width="15%">其中
                            <span class="line-info" id="toTodayUnResponse">
                                <em class="number">今日询价
                                    <a class="yellow_color">（${TODAY_UN_RESPONSE_COUNT}）</a>
                                </em>
                            </span>
                        </td>
                        <td width="67%">
                            <span class="line-info" id="toBeforeTodayUnResponse">
                                <em class="number">往日询价
                                    <a class="yellow_color">（${BEFORE_TODAY_UN_RESPONSE_COUNT}）</a>
                                </em>
                            </span>
                        </td>
                    </tr>
                    <tr>
                        <td>已报价：</td>
                        <td width="10%">
                            <span class="line-info" id="toAllResponse">共
                                <a class="number">${TODAY_RESPONSE_COUNT+BEFORE_TODAY_RESPONSE_COUNT}&nbsp;</a>条
                            </span>
                        </td>
                        <td>其中
                            <span class="line-info" id="toTodayResponse">
                                <em class="number">今日报价
                                    <a class="yellow_color">（${TODAY_RESPONSE_COUNT}）</a>
                                </em>
                            </span>
                        </td>
                        <td>
                            <span class="line-info" id="toBeforeTodayResponse">
                                <em class="number">往日报价
                                    <a class="yellow_color">（${BEFORE_TODAY_RESPONSE_COUNT}）</a>
                                </em>
                            </span>
                        </td>
                    </tr>
                    </tbody>
                </table>
                </form>
            </div>
        </div>
        <div class="lineTitle">询价查询</div>
        <div class="lineBody bodys">
            <table  border="0" cellpadding="0" cellspacing="0" class="order-management">
                <tr>
                    <td>客户</td>
                    <td colspan="2">
                        <input id="customerSearchWord" name="customerSearchWord" maxlength="20"
                                           type="text" class="txt txt-long" placeHolder="客户名/车牌号/手机号"/>
                        <input type="hidden" id="customerIds">
                    </td>
                    <td>单据号</td>
                    <td><input id="receiptNo" type="text" class="txt txt-long" maxlength="20"/></td>
                    <td>询价时间</td>
                    <td>
                        <input id="enquiryTimeStartStr" readonly="readonly" type="text" class="txt" />
                        &nbsp;到&nbsp;
                        <input id="enquiryTimeEndStr" readonly="readonly" type="text" class="txt"/>
                    </td>
                </tr>
                <tr>
                    <td>报价时间</td>
                    <td colspan="2">
                        <input id="responseTimeStartStr" type="text" class="txt" />
                        &nbsp;到&nbsp;
                        <input id="responseTimeEndStr" type="text" class="txt"/>
                    </td>
                    <td>状态</td>
                    <td>
                        <select id="responseStatuses" class="txt txt_color">
                            <option value="">所有</option>
                            <option value="UN_RESPONSE">未报价</option>
                            <option value="RESPONSE">已报价</option>
                        </select>
                    </td>
                </tr>
            </table>
            <div class="clear height"></div>
            <div class="divTit button_conditon button_search">
                <a id="clearSearchCondition" class="blue_color clean">清空条件</a>
                <a id="searchEnquiryOrderBtn"  class="button">查 询</a>
            </div>
        </div>
        <div class="lineBottom"></div>
    </div>
    <div class="clear i_height"></div>
    <div class="cuSearch">
        <div class="cartTop"></div>
        <div class="cartBody">
            <table id="enquiryListTb" class="tab_cuSearch" cellpadding="0" cellspacing="0">
                <tr class="titleBg">
                    <td style="padding-left:10px;">单据号</td>
                    <td>客户名</td>
                    <td>车牌号</td>
                    <td>手机号</td>
                    <td>询价时间</td>
                    <td>报价时间</td>
                    <td>状态</td>
                    <td>操作</td>
                </tr>
                <tr class="space">
                    <td colspan="8"></td>
                </tr>
                <%--<tr class="titBody_Bg">--%>
                    <%--<td style="padding-left:10px;"><a class="blue_color">XJ130909-006</a></td>--%>
                    <%--<td>张三</td>--%>
                    <%--<td>苏E2214</td>--%>
                    <%--<td>1516445552</td>--%>
                    <%--<td>2013-09-03 13：45</td>--%>
                    <%--<td>——</td>--%>
                    <%--<td>待确认</td>--%>
                    <%--<td><a class="blue_color">立即报价</a></td>--%>
                <%--</tr>--%>
                <%--<tr class="titBottom_Bg">--%>
                    <%--<td colspan="8"></td>--%>
                <%--</tr>--%>
                <%--<tr class="titBody_Bg">--%>
                    <%--<td style="padding-left:10px;"><a class="blue_color">XJ130909-006</a></td>--%>
                    <%--<td>张三</td>--%>
                    <%--<td>苏E2214</td>--%>
                    <%--<td>1516445552</td>--%>
                    <%--<td>2013-09-03 13：45</td>--%>
                    <%--<td>——</td>--%>
                    <%--<td>待确认</td>--%>
                    <%--<td><a class="blue_color">再次报价</a></td>--%>
                <%--</tr>--%>
                <%--<tr class="titBottom_Bg">--%>
                    <%--<td colspan="8"></td>--%>
                <%--</tr>--%>
                <%--<tr class="titBody_Bg">--%>
                    <%--<td style="padding-left:10px;"><a class="blue_color">XJ130909-006</a></td>--%>
                    <%--<td>张三</td>--%>
                    <%--<td>苏E2214</td>--%>
                    <%--<td>1516445552</td>--%>
                    <%--<td>2013-09-03 13：45</td>--%>
                    <%--<td>2013-09-04 12：05</td>--%>
                    <%--<td>待确认</td>--%>
                    <%--<td><a class="blue_color">再次报价</a></td>--%>
                <%--</tr>--%>
                <%--<tr class="titBottom_Bg">--%>
                    <%--<td colspan="8"></td>--%>
                <%--</tr>--%>
            </table>
            <div class="clear i_height"></div>
            <bcgogo:ajaxPaging url="enquiry.do?method=searchShopEnquiryList" dynamical="enquiryOrderList"
                                        data='{startPageNo:1,maxRows:15}'
                                        postFn="drawEnquiryOrderList"/>
            <!----------------------------分页----------------------------------->
            <%--<div class="i_pageBtn"> <a class="first">首&nbsp;页</a> <a class="lastPage">上一页</a> <a class="num numButton">1</a> <a class="num numButton">2</a> <a class="num numButton">3</a> <a class="num numButton">4</a> <a class="ellipsis">...</a> <a class="nextPage wordButton">下一页</a> <a class="last wordButton">尾&nbsp;页</a>--%>
                <%--<div class="pageNum">共<span class="recordCount">130</span>条记录&nbsp;&nbsp;共<span class="pageCount">6</span>页&nbsp;&nbsp;到第--%>
                    <%--<input type="text" class="selectPage" />--%>
                    <%--页--%>
                    <%--<input class="pageSure" type="button" value="跳&nbsp;转" onfocus="this.blur();">--%>
                <%--</div>--%>
            <%--</div>--%>
        </div>
        <div class="cartBottom"></div>
    </div>
</div>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>