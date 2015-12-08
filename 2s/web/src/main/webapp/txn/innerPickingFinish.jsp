<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>内部领料</title>

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
         <jsp:param name="currPage" value="innerPicking"/>
         <jsp:param name="isRepairPickingSwitchOn" value="<%=isRepairPickingSwitchOn%>"/>
     </jsp:include>
    <div class="titBody ">
        <input type="hidden" id="orderType" value="innerPickingFinish">
        <form:form action="pick.do?method=showInnerPicking" id="innerPickingForm" commandName="innerPickingDTO"
                   method="post">
            <form:hidden path="id"/>
            <div class="divTit">单据号：<span>${innerPickingDTO.receiptNo}</span></div>
            <div class="divTit">领料人：<span>${innerPickingDTO.pickingMan}</span></div>
            <c:if test="${innerPickingDTO.isHaveStoreHouse}">
                <div class="divTit">领料仓库：<span>${innerPickingDTO.storehouseName}</span></div>
            </c:if>
            <div class="divTit">操作人：<span>${innerPickingDTO.operationMan}</span></div>
            <div class="divTit">领料日期：<span>${innerPickingDTO.vestDateStr}</span></div>
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
                    <td>领料数/库存量</td>
                    <td>小计</td>
                </tr>
                <c:forEach items="${innerPickingDTO.itemDTOs}" var="itemDTO" varStatus="status">
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
                    <td>${innerPickingDTO.totalAmount}/${innerPickingDTO.totalInventoryAmount}</td>
                    <td>${innerPickingDTO.total}</td>
                </tr>
            </table>
            <div class="height"></div>
            <bcgogo:hasPermission permissions="WEB.INNER_PICKING.PRINT">
                <div class="invalidImgLeftShow">
                    <input id="print_innerPicking_btn" class="print j_btn_i_operate" type="button" onfocus="this.blur();">

                    <div class="sureWords">打印</div>
                </div>
            </bcgogo:hasPermission>
            <div class="shopping_btn">
                <div class="btn_div_Img" style="margin-top:-12px">
                    <input id="show_innerPicking_list_btn" class="cancel j_btn_i_operate" type="button" onfocus="this.blur();">
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