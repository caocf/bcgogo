<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>内部退料</title>

    <link rel="stylesheet" type="text/css" href="styles/up<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/addPlan<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/moreHistory<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/statistics<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/addWarehouse<%=ConfigController.getBuildVersion()%>.css">

    <%@include file="/WEB-INF/views/header_script.jsp" %>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.js"></script>
    <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/pickingCommon<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">
        defaultStorage.setItem(storageKey.MenuCurrentItem,"详情");
    </script>

</head>
<body class="bodyMain">
<%@include file="/WEB-INF/views/header_html.jsp" %>

<div class="i_main clear">
    <jsp:include page="txnNavi.jsp">
        <jsp:param name="currPage" value="inventory"/>
    </jsp:include>
    <jsp:include page="inventroyNavi.jsp">
        <jsp:param name="currPage" value="innerReturn"/>
        <jsp:param name="isRepairPickingSwitchOn" value="<%=isRepairPickingSwitchOn%>"/>
    </jsp:include>

    <div class="titBody ">
        <input type="hidden" id="orderType" value="innerReturnFinish">
        <form:form action="pick.do?method=showInnerReturn" id="innerReturnForm" commandName="innerReturnDTO"
                   method="post">
            <form:hidden path="id"/>
            <div class="divTit">单据号：<span>${innerReturnDTO.receiptNo}</span></div>
            <div class="divTit">退料人：<span>${innerReturnDTO.pickingMan}</span></div>
            <c:if test="${innerReturnDTO.isHaveStoreHouse}">
                <div class="divTit">退料仓库：<span>${innerReturnDTO.storehouseName}</span></div>
            </c:if>
            <div class="divTit">操作人：<span>${innerReturnDTO.operationMan}</span></div>
            <div class="divTit">退料日期：<span>${innerReturnDTO.vestDateStr}</span></div>
            <div class="clear i_height"></div>
            <table cellpadding="0" cellspacing="0" class="tabSlip tabPrev">
                <col width="90">
                <col width="110">
                <col width="110">
                <col width="110">
                <col width="110">
                <col width="110">
                <col width="110">
                <col>
                <col width="30">
                <col width="100">
                <col width="55">
                <tr class="tr_bg">
                    <td>商品编号</td>
                    <td>品名</td>
                    <td>品牌/产地</td>
                    <td>规格</td>
                    <td>型号</td>
                    <td>车辆品牌</td>
                    <td>车型</td>
                    <td>成本价</td>
                    <td>单位</td>
                    <td>退料数/库存量</td>
                    <td>小计</td>
                </tr>
                <c:forEach items="${innerReturnDTO.itemDTOs}" var="itemDTO" varStatus="status">
                    <tr>
                        <td>${itemDTO.commodityCode}</td>
                        <td>${itemDTO.productName}</td>
                        <td>${itemDTO.brand}</td>
                        <td>${itemDTO.spec}</td>
                        <td>${itemDTO.model}</td>
                        <td>${itemDTO.vehicleBrand}</td>
                        <td>${itemDTO.vehicleModel}</td>
                        <td>${itemDTO.price}</td>
                        <td>${itemDTO.unit}</td>
                        <td>${itemDTO.amount}/${itemDTO.inventoryAmount}</td>
                        <td>${itemDTO.total}</td>
                    </tr>
                </c:forEach>
                <tr style="font-weight:bold;">
                    <td colspan="9" style="text-align:center;">合计：</td>
                    <td>${innerReturnDTO.totalAmount}</td>
                    <td>${innerReturnDTO.total}</td>
                </tr>
            </table>
            <div class="height"></div>
            <bcgogo:hasPermission permissions="WEB.INNER_RETURN.PRINT">
                <div class="invalidImgLeftShow">
                    <input id="print_innerReturn_btn" class="print j_btn_i_operate" type="button" onfocus="this.blur();">
                    <div class="sureWords">打印</div>
                </div>
            </bcgogo:hasPermission>
            <div class="shopping_btn">
                <div class="btn_div_Img" style="margin-top:-12px">
                    <input id="show_innerReturn_list_btn" class="cancel j_btn_i_operate" type="button" onfocus="this.blur();">
                    <div class="sureWords">返回列表</div>
                </div>
            </div>
        </form:form>
    </div>
</div>

<div id="mask" style="display:block;position: absolute;">
</div>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>