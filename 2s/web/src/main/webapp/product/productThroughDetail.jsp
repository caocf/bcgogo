<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
<title>出入库明细</title>

    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/moreHistory<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/messageManage<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
    <%@include file="/WEB-INF/views/header_script.jsp" %>
    <script type="text/javascript" src="js/productThrough/productThroughDetail<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">
        defaultStorage.setItem(storageKey.MenuUid, "PRODUCT_THROUGH_DETAIL");
    </script>
    <style type="text/css">
        .height {
            clear: both;
            height: 10px;
        }
    </style>
</head>
<body class="bodyMain">

<%@include file="/WEB-INF/views/header_html.jsp" %>


<div class="i_main">
<div class="i_search">
<div class="mainTitles">
    <div class="titleWords">出入库明细</div>
</div>
<div class="titBody">
<div class="lineTitle">出入库明细</div>

<form id="productThroughSearchForm" name="productThroughSearchForm" action="productThrough.do?method=getProductThroughRecord" method="post">
    <input type="hidden" name="maxRows" id="maxRows" value="15">
    <div class="lineBody bodys">
        <div class="divTit" style="width:100%;">
            <label class="lblConcition">日期：</label>
            <a class="btnList" id="my_date_lastyear" name="my_date_select">去年</a>&nbsp;
            <a class="btnList" id="my_date_lastmonth" name="my_date_select">上月</a>&nbsp;
            <a class="btnList" id="my_date_thismonth" name="my_date_select">本月</a>&nbsp;
            <a class="btnList" id="my_date_thisyear" name="my_date_select">今年</a>&nbsp;
            <input id="startDate" name="startDateStr" type="text" style="color: #000000" class="my_startdate txt" />&nbsp;至&nbsp;
            <input id="endDate" name="endDateStr"  style="color: #000000" type="text" class="my_enddate txt" />
        </div>
        <div class="divTit" style="width:100%;">
            <span id="productCategorySpan" style="display:inline-block;">
                <label class="lblConcition">商品分类：</label>
                <input id="productCategory" name="productKind" initialValue="--所有商品分类--" value="--所有商品分类--" type="text" style="width:150px;color: #ADADAD;" autocomplete="off" class="J-initialCss J_clear_input textbox J-productCategory" />
            </span>
        </div>
        <div class="divTit" style="width:100%;">
            <span id="proInfoSpan" style="display:inline-block;">
                 <label class="lblConcition">商品信息：</label>
                <input type="hidden" id="productId" class="J_clear_input" name="productId" value="${productId}">
                <input class="txt J-productSuggestion J-initialCss J_clear_input" id="productName" name="productName" value="${productName}"
                       searchField="product_name" initialValue="品名" style="width:100px;"/>
                <input class="txt J-productSuggestion J-initialCss J_clear_input" id="productBrand" name="productBrand" value="${productBrand}"
                       searchField="product_brand" initialValue="品牌/产地" style="width:100px;"/>
                <input class="txt J-productSuggestion J-initialCss J_clear_input" id="productSpec" name="productSpec" value="${productSpec}"
                       searchField="product_spec" initialValue="规格" style="width:100px;"/>
                <input class="txt J-productSuggestion J-initialCss J_clear_input" id="productModel" name="productModel" value="${productModel}"
                       searchField="product_model" initialValue="型号" style="width:100px;"/>
                <input class="txt J-productSuggestion J-initialCss J_clear_input" id="productVehicleBrand" value="${productVehicleBrand}"
                       name="productVehicleBrand" searchField="product_vehicle_brand" initialValue="车辆品牌" style="width:100px;"/>
                <input class="txt J-productSuggestion J-initialCss J_clear_input" id="productVehicleModel" value="${productVehicleModel}"
                       name="productVehicleModel" searchField="product_vehicle_model" initialValue="车型" style="width:100px;"/>
                <input class="txt J-productSuggestion J-initialCss J_clear_input" id="commodityCode" name="commodityCode" value="${commodityCode}"
                       searchField="commodity_code" initialValue="商品编号" style="text-transform: uppercase;width:100px;"/>
            </span>
      </div>



        <div class="divTit" id="supInfo">
            <label class="lblConcition">供应商/客户信息：</label>
            <input type="text" class="txt J-supplierSuggestion J-initialCss J_clear_input" initialValue="供应商/手机号" id="supplierName"
                   name="supplierName" value="${supplierName}" style="width:212px;"/>
            <input type="hidden" id="supplierId" class="J_clear_input" name="supplierId" value="${supplierId}">
            <input type="text" class="txt J-customerSuggestion J-initialCss J_clear_input" initialValue="客户/手机号" id="customerName"
                   name="customerName" value="${customerName}"  style="width:212px;"/>
            <input type="hidden" id="customerId" class="J_clear_input" name="customerId" value="${customerId}">
        </div>

        <bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.STOREHOUSE">

            <c:if test="${storeHouseDTOList!=null}">
                <div class="divTit divWarehouse" style="width:100%;height: 100%;display: block">
                    <span class="lblConcition" style="float: left;display: block">仓库：</span>
                    <div class="warehouseList">
                        <label for="allStoreHouseCheck" class="lblChk lblList">
                            <input type="checkbox" id="allStoreHouseCheck"/>全部仓库
                        </label>
                        <c:forEach items="${storeHouseDTOList}" var="storeHouse" varStatus="status">
                            <label class="lblChk lblList">
                                <input type="checkbox" name="storehouseIds" class="J_storehouseCheck" value='${storeHouse.id}'/>${storeHouse.name}
                            </label>
                        </c:forEach>
                    </div>
                </div>
            </c:if>
        </bcgogo:hasPermission>


        <div class="divTit" style="width:100%;">
            <label class="lblConcition">出入类型：</label>
            <label class="lblChk lblList"><input type="checkbox" name="itemType" value="OUT"/>出库</label>
            <label class="lblChk lblList"><input type="checkbox" name="itemType" value="IN"/>入库</label>
        </div>
        <div class="divTit button_conditon button_search">
            <a class="blue_color clean" id="clearBtn">清空条件</a>
            <a class="button" id="searchBtn">查&nbsp;询</a>
        </div>
        <input type="hidden" value="order_created_time desc" id="sort" name="sort"/>
    </div>
</form>

    <div class="lineBottom"></div>
    <div class="clear height"></div>
    <div class="supplier group_list2 listStyle" id="countForList">

    </div>
<div class="clear height"></div>
<div class="cuSearch">
    <div class="cartTop"></div>
    <div class="cartBody">

        <div class="line_develop list_develop" style="width:980px;border-right:1px solid #C5C5C5"><span class="sorting-total">排序方式：</span>
            <a id="dateSort" name="J_sortStyle">时间<span class="arrowUp" id="dateSortSpan"></span></a>
            <a id="storageSort" name="J_sortStyle">入库量<span class="arrowDown" id="storageSortSpan"></span></a>
            <a id="shipmentsSort" name="J_sortStyle">出库量<span class="arrowDown" id="shipmentsSortSpan"></span>
            </a>
        </div>

        <bcgogo:permission>
    <bcgogo:if resourceType="logic" permissions="WEB.VERSION.STOREHOUSE">
        <table cellpadding="0" cellspacing="0" class="tab_cuSearch" id="productThroughTable">
            <col width="120">
            <col width="100">
            <col width="90">
            <col width="70">
            <col width="70">
            <col width="70">
            <col width="80">
            <col width="80">
            <col width="60">
            <col width="60">
            <col width="60">
            <col width="80">
            <tr class="titleBg J-title">
                <td style="padding-left:10px;">时间</td>
                <td>商品</td>
                <td>供应商</td>
                <td>单据类型</td>
                <td>客户</td>
                <td>仓库</td>
                <td>入库数量</td>
                <td>出库数量</td>
                <td>成本</td>
                <td>收入</td>
                <td>利润</td>
                <td>单据号</td>
            </tr>
            <tr class="space J-title"><td colspan="9"></td></tr>
        </table>
    </bcgogo:if>
    <bcgogo:else>
        <table cellpadding="0" cellspacing="0" class="tab_cuSearch" id="productThroughTable">
            <col width="130">
            <col>
            <col width="100">
            <col width="80">
            <col width="100">
            <col width="80">
            <col width="80">
            <col width="40">
            <col width="40">
            <col width="40">
            <col width="80">
            <tr class="titleBg J-title">
                <td style="padding-left:10px;">时间</td>
                <td>商品</td>
                <td>供应商</td>
                <td>单据类型</td>
                <td>客户</td>
                <td>入库数量</td>
                <td>出库数量</td>
                <td>成本</td>
                <td>收入</td>
                <td>利润</td>
                <td>单据号</td>
            </tr>
            <tr class="space J-title"><td colspan="8"></td></tr>
        </table>
    </bcgogo:else>
    </bcgogo:permission>
    <div class="clear i_height"></div>
    <jsp:include page="/common/pageAJAX.jsp">
        <jsp:param name="url" value="productThrough.do?method=getProductThroughRecord"></jsp:param>
        <jsp:param name="jsHandleJson" value="initProductThroughRecord"></jsp:param>
        <jsp:param name="dynamical" value="productThroughRecord"></jsp:param>
        <jsp:param name="display" value="none"></jsp:param>
    </jsp:include>

    </div>
</div>
</div>

</div>
</div>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>

</html>