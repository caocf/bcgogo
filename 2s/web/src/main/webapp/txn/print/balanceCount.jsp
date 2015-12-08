<%@ page import="com.bcgogo.config.ConfigController" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<%--
  Created by IntelliJ IDEA.
  User: zyj
  Date: 12-3-16
  Time: 上午9:18
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>欠款结算单</title>

    <link rel="stylesheet" type="text/css" href="styles/style.css">
    <link rel="stylesheet" type="text/css" href="styles/print.css"/>
    <link rel="stylesheet" type="text/css" href="styles/printShow.css"/>
    <script type="text/javascript" src="js/extension/jquery/jquery-1.4.2.min.js"></script>
    <script type="text/javascript" src="js/base<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/application<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">
        $(document).ready(function () {
            window.print();
            window.close();
        });

    </script>
</head>
<body class="bodyMain">
<!--内容-->
<div class="print_cont balance_count">
    <h3>${shopDTO.name}</h3>
    <!--第一部分-->
    <div class="i_searchTitle  clear">
        欠款结算单
        <!--<div class="balance_top"><label>欠款结算单</label></div>-->
        <label class="zhidan">制单时间：${dataStr}</label>
    </div>
    <!--第一部分结束-->

    <!--第二部分-->
    <table class="pruch_tab">
        <col width="50"/>
        <col width="85" style="width:83px\9;"/>
        <col width="55"/>
        <col width="80"/>
        <col width="65"/>
        <col width="110"/>
        <col width="40"/>
        <col/>
        <tr class="table_title">
            <td>供应商:</td>
            <td>${customerDTO.name}</td>
            <td>联系人:</td>
            <td>
                <c:if test="${customerDTO.contact!=''}">
                    ${customerDTO.contact}
                </c:if>
            </td>
            <td>联系电话:</td>
            <td>${customerDTO.mobile}</td>
            <td>地址:</td>
            <td>
                <c:if test="${customerDTO.address!=''}">
                    ${customerDTO.address}
                </c:if>
            </td>
        </tr>
    </table>
    <%--<div class="pruch_use clear">--%>
    <%--<label>客户名:</label><span style="width:200px;">${customerDTO.name}</span>--%>
    <%--<c:if test="${customerDTO.contact!=''}">--%>
    <%--<label>联系人:</label><span>${customerDTO.contact}</span>--%>
    <%--</c:if>--%>
    <%--<label>联系电话:</label><span class="non_pruch">${customerDTO.mobile}</span>--%>
    <%--<c:if test="${customerDTO.address!=''}">--%>
    <%--<label class="address_lab">地址:</label><span class="address_show">${customerDTO.address}</span>--%>
    <%--</c:if>--%>
    <%--<div class="clear"></div>--%>
    <%--</div>--%>
    <!--施工单-->
    <table cellpadding="0" cellspacing="0" class="table2 balance_pruch">
        <col width="10"/>
        <col width="32"/>
        <col width="30"/>
        <col width="55"/>
        <col width="40"/>
        <col width="35"/>
        <col width="25"/>
        <col width="25"/>
        <col width="25"/>
        <tr class="table_title">
            <td>No</td>
            <td>消费时间</td>
            <td>车牌号</td>
            <td>内容</td>
            <td>施工</td>
            <td>材料/品名</td>
            <td>消费金额</td>
            <td>实收金额</td>
            <td>欠款金额</td>
        </tr>
        <c:forEach items="${debtDTOList}" var="debtDTO" varStatus="status">
            <tr>
                <td>${status.index+1}</td>
                <td>${debtDTO.date}</td>
                <td>${debtDTO.vehicleNumber}</td>
                <td>${debtDTO.content}</td>
                <td>${debtDTO.service}</td>
                <td>${debtDTO.material}</td>
                <td>${debtDTO.totalAmount}</td>
                <td>${debtDTO.settledAmount}</td>
                <td>${debtDTO.debt}</td>
            </tr>
        </c:forEach>
        <tr>
            <td colspan="2">合计</td>
            <td colspan="2">${totalAmount}¥</td>
            <td>实收金额</td>
            <td colspan="4">${payedAmount}¥</td>
        </tr>
        <tr>
            <td colspan="2">合计(大写)</td>
            <td colspan="2">${totalAmountStr}</td>
            <td>实收金额(大写)</td>
            <td colspan="4" class="font_set">${payedAmountStr}</td>
        </tr>
    </table>
    <div class="clear" style="height:10px;"></div>
    <!--施工单结束-->
    <h1 style=" margin-bottom:0px;font-size:12px;">备注:</h1>

    <div class="qianzi clear">
    	<span class="print_num man_zi">店长签字：
    	<label></label></span>

        <div class="kehu_qian ">客户签字：</div>
    </div>
    <div>
        <div class=" time_pr ">(盖章)</div>
        <div class=" time_pr">日期：<label></label></div>

    </div>
    <div class="address clear">
        <div class=" time  time_p" style="font-weight: normal;"><span>地址：</span><label>${shopDTO.address}</label></div>
        <div class=" phone_time">电话：<label>${shopDTO.landline}
            <c:if test="${shopDTO.storeManagerMobile != null &&shopDTO.storeManagerMobile !=''}">
                <c:if test="${shopDTO.landline != null && shopDTO.landline != ''}">
                    ,
                </c:if>
                ${shopDTO.storeManagerMobile}
            </c:if>
        </label></div>
    </div>
    <div class="clear"></div>
</div>
<!--内容结束-->
</body>
</html>