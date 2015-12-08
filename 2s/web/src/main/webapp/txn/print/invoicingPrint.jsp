<%@ page import="com.bcgogo.config.ConfigController" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>维修保养打印单</title>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/print<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/printShow<%=ConfigController.getBuildVersion()%>.css"/>
    <script type="text/javascript" src="js/extension/jquery/jquery-1.4.2.min.js"></script>
    <script type="text/javascript" src="js/base<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/application<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">
        $().ready(function() {
            var serviceTotal = 0;
            var itemTotal = 0;
            $(".serviceTotal").each(function(i) {
                var txt = $(this);
                if ($.trim(txt.text()) != '')
                    serviceTotal += parseFloat(txt.text());
            });
            $("#serviceAmount").text(serviceTotal);
            $(".itemTotal").each(function(i) {
                var txt = $(this);
                if ($.trim(txt.text()) != '')
                    itemTotal += parseFloat(txt.text());
            });
            $("#itemAmount").text(itemTotal);
            window.print();
            window.close();
        });
    </script>
</head>
<body class="bodyMain">
<!--内容-->
<div class="print_cont">
<h3>${repairOrderDTO.shopName}</h3>
<!--第一部分-->
<div class="i_searchTitle clear">
    <label class="danju">单据号:</label><label class="danju receipt">${repairOrderDTO.id}</label><label>维修保养单</label>
    <label class="zhidan">制单时间：${repairOrderDTO.startDateStr}</label>

</div>
<!--第一部分结束-->

<!--第二部分-->
<table cellpadding="0" cellspacing="0" class="table2">
    <col width="60" style="*width:60px;"/>
    <col/>
    <col width="65" style="*width:65px;"/>
    <col/>
    <col width="59" style="*width:55px;"/>
    <col/>
    <tr class="table_title">
        <td>车牌号</td>
        <td>${repairOrderDTO.licenceNo}</td>
        <td>客户名</td>
        <td>${repairOrderDTO.customerName}</td>
        <td>进厂时间</td>
        <td>${repairOrderDTO.startDateStr}</td>
    </tr>
    <tr>
        <td><span class="fonty">品   牌</span></td>
        <td>${repairOrderDTO.brand}</td>
        <td><span class="fonty">手   机</span></td>
        <td>${repairOrderDTO.mobile}</td>
        <td>预计出厂</td>
        <td><span>${repairOrderDTO.endDateStr}</span></td>
    </tr>
    <tr>
        <td><span class="fonty">车   型</span></td>
        <td>${repairOrderDTO.model}</td>
        <td><span class="fonty">座   机</span></td>
        <td>${repairOrderDTO.landLine}</td>
        <td>车辆里程</td>
        <td>${repairOrderDTO.startMileage} 公里</td>
    </tr>
    <tr>

        <td>购车日期</td>
        <td>${customerRecordDTO.carDateString}</td>
        <td style="display:none"><span class="fonty">年   代</span></td>
        <td style="display:none">${repairOrderDTO.year}</td>
        <td>联系地址</td>
        <td>${customerRecordDTO.address}</td>
        <td>剩余油量</td>
        <td>${fuelNumberList[repairOrderDTO.fuelNumber]}</td>
    </tr>
    <tr style="display:none">
        <td><span class="fonty">排   量</span></td>
        <td>${repairOrderDTO.engine}</td>

    </tr>
</table>
<!--第二部分结束-->

<!--施工单-->
<div class="work_order clear">
    <div class="print_top">

        <div class="order_info"><label class="name_dan">施工人：${repairOrderDTO.serviceWorker}</label><label>施工单</label></div>
    </div>
    <table cellpadding="0" cellspacing="0" class="table2" id="table_productNo">
        <col width="10"/>
        <col width="70"/>
        <col width="20"/>
        <col width="40"/>
        <tr class="table_title">
            <td>序号</td>
            <td>施工内容</td>
            <td>工时费</td>
            <td> 备注</td>
        </tr>
        <c:forEach items="${repairOrderDTO.serviceDTOs}" var="serviceDTO" varStatus="status">
            <tr>
                <td>${status.index+1}</td>

                <td>${serviceDTO.service}</td>
                <td class="serviceTotal">${serviceDTO.total}</td>
                <td>${serviceDTO.memo}</td>
            </tr>
        </c:forEach>
    </table>
    <div class="clear"></div>
</div>
<!--施工单结束-->

<!--材料单号-->
<div class="work_order clear">
    <div class="print_top">
        <!--<img src="../images/print_left.png"/>-->
        <div class="order_info"><!--<span>材料单号：<label>B0001</label></span>--><label class="name_dan">销售人：${repairOrderDTO.productSaler}</label>
            <label>销售单</label></div>
        <!--<img src="../images/print_right.png"/>-->
    </div>
    <table cellpadding="0" cellspacing="0" class="table2" id="table_productNo">
        <col width="97"/>
        <col width="120"/>
        <col width="100"/>
        <col/>
        <col/>
        <col width="58"/>
        <col width="45"/>
        <col width="40"/>
        <col width="55"/>
        <tr class="table_title">
            <td>商品编号</td>
            <td>品名</td>
            <td>品牌</td>
            <td>型号</td>
            <td> 规格</td>
            <td>单价</td>
            <td>数量</td>
            <td>单位</td>
            <td>金额</td>
        </tr>

        <c:forEach items="${repairOrderDTO.itemDTOs}" var="itemDTO" varStatus="status">
            <tr>

                <td>${status.index+1}</td>

                <td>${itemDTO.productName}</td>
                <td>${itemDTO.brand}</td>
                <td>${itemDTO.spec}</td>
                <td>${itemDTO.model}</td>
                <td>${itemDTO.price}</td>
                <td>${itemDTO.amount}</td>
                <td>${itemDTO.unit}</td>
                <td class="itemTotal">${itemDTO.total}</td>
            </tr>
        </c:forEach>
    </table>
    <div class="clear"></div>
</div>
<!--材料单号结束-->

<!--维修费用结算-->
<div class="work_order clear">
    <div class="print_top">
        <!--<img src="../images/print_left.png"/>-->
        <div class="order_info"><label class="name_dan">结算人：</label> <label>维修费用结算单</label></div>
        <!--            <img src="../images/print_right.png"/>
        -->        </div>
    <table cellpadding="0" cellspacing="0" class="table2">
        <col width="58" style="*width:55px;"/>
        <col width="58" style="*width:55px;"/>
        <col width="78" style="*width:75px;"/>
        <col width="58" style="*width:55px;"/>
        <col width="59" style="*width:55px;"/>
        <col width="59" style="*width:55px;"/>
        <col width="83"/>
        <col width="79"/>
        <tr class="table_title">
            <td>工时费</td>
            <td>材料费</td>
            <td>合计</td>
            <td>实收</td>
            <td>折扣</td>
            <td> 欠款</td>
            <td>预计还款</td>
            <td>日期</td>
        </tr>
        <tr>
            <td id="serviceAmount"></td>
            <td id="itemAmount"></td>
            <td>${repairOrderDTO.total}</td>

            <td>${repairOrderDTO.settledAmount}</td>
            <td>${repairOrderDTO.total-(repairOrderDTO.settledAmount+repairOrderDTO.debt)}</td>
            <td>${repairOrderDTO.debt}</td>
            <td>${customerRecordDTO.repayDateStr}</td>
            <td>${repairOrderDTO.editDateStr}</td>
        </tr>
        <tr>
            <td>实收</td>
            <td colspan="2">${repairOrderDTO.settledAmount}¥</td>
            <td>实收(大写)</td>
            <td colspan="4" class="font_set">${repairOrderDTO.settledAmountStr}</td>
        </tr>
    </table>
    <div class="clear"></div>
</div>
<div>备注：${repairOrderDTO.memo}</div>
<!--维修费用结算结束-->
<div class="qianzi clear">
    <div>店长签字：</div>
    	<span class="print_num">结算人签字：
    	<label></label></span>

    <div class="kehu_qian ">客户签字：</div>
</div>
<div>
    <div class=" time_pr time_gai">(盖章)</div>
    <div class=" time_pr">日期：<label></label></div>
</div>
<div class="address clear">
    <div class=" time time_p"><span>地址：</span><label>${repairOrderDTO.shopAddress}</label></div>
    <div class=" phone_time">电话：<label>${repairOrderDTO.shopLandLine}
        <c:if test="${storeManagerMobile != null && storeManagerMobile !=''}">
            <c:if test="${repairOrderDTO.shopLandLine != null && repairOrderDTO.shopLandLine != ''}">
                ,
            </c:if>
            ${storeManagerMobile}
        </c:if>
    </label></div>
</div>
</div>
<!--内容结束-->


</body>

</html>
