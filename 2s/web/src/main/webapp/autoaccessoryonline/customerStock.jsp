<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
  <title>客户库存</title>
  <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
  <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
  <link rel="stylesheet" type="text/css" href="styles/customerStock<%=ConfigController.getBuildVersion()%>.css"/>
  <%@include file="/WEB-INF/views/header_script.jsp" %>
  <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/utils/tableUtil<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/jsScroller<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/jsScrollbar<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/page/autoaccessoryonline/customerStock<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">
        defaultStorage.setItem(storageKey.MenuUid, "AUTO_ACCESSORY_ONLINE_RELATEDCUSTOMERSTOCK");
    </script>
</head>

<c:forEach items="${customerDTOs}" var="customerDTO" varStatus="status">
  <c:if test="${customerDTO!=null}">
    <input type="hidden" class="relatedCustomerId" value="${customerDTO.idStr}">
  </c:if>
</c:forEach>
<%@ include file="/WEB-INF/views/header_html.jsp" %>

<body class="bodyMain">

<input type="hidden" id="pageType" value="customerStock" />
<div class="i_main clear">
    <jsp:include page="autoAccessoryOnlineNavi.jsp">
        <jsp:param name="currPage" value="relatedCustomerStock"/>
    </jsp:include>
  <div class="i_search">
    <div class="i_main clear">
        <div class="titBody">
            <div class="lineTitle">客户库存搜索</div>
            <div class="lineTop"></div>
            <div class="lineBody bodys">
                <form:form commandName="searchConditionDTO" id="searchCustomerStockForm" action="autoAccessoryOnline.do?method=getAllRelatedCustomerStock" method="post" name="thisform">
                <form:hidden path="inventoryAmountUp"/>
                <form:hidden path="inventoryAmountDown"/>
                <div class="divTit" style="padding-left: 7px;"><form:input cssClass="txt J-productSuggestion J-initialCss J_clear_input" path="searchWord" searchField="product_info" initialValue="品名/品牌/规格/型号/适用车辆" cssStyle="width:190px;"/></div>
                <div class="divTit" style="padding-left: 7px;"><form:input cssClass="txt J-productSuggestion J-initialCss J_clear_input" path="productName" searchField="product_name" initialValue="品名" cssStyle="width:90px;"/></div>
                <div class="divTit" style="padding-left: 7px;"><form:input cssClass="txt J-productSuggestion J-initialCss J_clear_input" path="productBrand" searchField="product_brand" initialValue="品牌/产地" cssStyle="width:90px;"/></div>
                <div class="divTit" style="padding-left: 7px;"><form:input cssClass="txt J-productSuggestion J-initialCss J_clear_input" path="productSpec" searchField="product_spec" initialValue="规格" cssStyle="width:80px;"/></div>
                <div class="divTit" style="padding-left: 7px;"><form:input cssClass="txt J-productSuggestion J-initialCss J_clear_input" path="productModel" searchField="product_model" initialValue="型号" cssStyle="width:80px;"/></div>
                <div class="divTit" style="padding-left: 7px;"><form:input cssClass="txt J-productSuggestion J-initialCss J_clear_input" path="productVehicleBrand" searchField="product_vehicle_brand" initialValue="车辆品牌" cssStyle="width:80px;"/></div>
                <div class="divTit" style="padding-left: 7px;"><form:input cssClass="txt J-productSuggestion J-initialCss J_clear_input" path="productVehicleModel" searchField="product_vehicle_model" initialValue="车型" cssStyle="width:80px;"/></div>
                <div class="divTit" style="padding-left: 7px;"><form:input cssClass="txt J-productSuggestion J-initialCss J_clear_input" path="commodityCode" searchField="commodity_code" initialValue="商品编号" cssStyle="text-transform: uppercase;width:80px;"/></div>
                <div class="divTit" style="padding-left: 7px;">
                    <select id="stockScope" class="txt" style=" height: 20px;width:95px;padding: 0;border: 0.5px solid #BBBBBB;">
                        <option style="color:#BBBBBB" value="" inventoryAmountDown="" inventoryAmountUp="">库存量不限</option>
                        <option style="color:#000000" value="" inventoryAmountDown="0" inventoryAmountUp="0">库存为0</option>
                        <option style="color:#000000" value="" inventoryAmountDown="0" inventoryAmountUp="10">库存小于10</option>
                        <option style="color:#000000" value="" inventoryAmountDown="0" inventoryAmountUp="50">库存小于50</option>
                    </select>
                </div>
                <div class="divTit" style="padding-left: 7px;">
                    <form:input cssClass="txt J-relatedCustomerSuggestion J-initialCss J_clear_input" path="relatedCustomerName" initialValue="客户" style="width:130px;"/>
                </div>
                <div class="divTit" style="line-height: 24px;padding-left: 7px;">
                    <input id="searchCustomerStockBtn" type="button" class="btn_search" onfocus="this.blur();" style="margin-left:5px">
                    <input class="btn_clear" type="button" id="clearConditionBtn" style="margin-top:0px;" value="清空条件"/>
                </div>
                </form:form>
            </div>
        </div>
        <div class="lineBottom"></div>
        <div class="clear height"></div>
        <div class="lineTitle tabTitle">
            客户库存列表
        </div>
        <div class="tab" style="width: 981px">
            <div id="noData" style="color:#000000;float:left; clear:both; display:none; position:relative;">
                没有数据!
            </div>
            <table cellpadding="0" cellspacing="0" class="tabSlip" id="cusStock0" style="width: 100%;display: none">
                <input type="hidden"  id="customerShopId0" value="-1">
                <col>
                <col width="90">
                <col width="90">
                <col width="90">
                <col width="75">
                <tr class="titleBg J-title">
                    <td style="padding-left:10px;">配件名</td>
                    <td>客户当前库存</td>
                    <td>最后交易价格</td>
                    <td>最后交易数量</td>
                    <td>最后交易日期</td>
                </tr>
            </table>
            <div class="stockPager" id="stockPager0" style="margin-right:15px;float: left">
                <jsp:include page="/common/specailStockPager.jsp">
                    <jsp:param name="dynamical" value="cusStock0"></jsp:param>
                    <jsp:param name="buttonId" value="getCustomerStock0"></jsp:param>
                </jsp:include>
            </div>

            <table cellpadding="0" cellspacing="0" class="tabSlip" id="cusStock1" style="width: 100%;display: none">
                <input type="hidden"  id="customerShopId1" value="-1">
                <col>
                <col width="90">
                <col width="90">
                <col width="90">
                <col width="75">
                <tr class="titleBg J-title">
                    <td style="padding-left:10px;">配件名</td>
                    <td>客户当前库存</td>
                    <td>最后交易价格</td>
                    <td>最后交易数量</td>
                    <td>最后交易日期</td>
                </tr>
            </table>
            <div class="stockPager" id="stockPager1" style="margin-right:15px;float: left">
                <jsp:include page="/common/specailStockPager.jsp">
                    <jsp:param name="dynamical" value="cusStock1"></jsp:param>
                    <jsp:param name="buttonId" value="getCustomerStock1"></jsp:param>
                </jsp:include>
            </div>
            <table cellpadding="0" cellspacing="0" class="tabSlip" id="cusStock2" style="width: 100%;display: none">
                <input type="hidden"  id="customerShopId2" value="-1">
                <col>
                <col width="90">
                <col width="90">
                <col width="90">
                <col width="75">
                <tr class="titleBg J-title">
                    <td style="padding-left:10px;">配件名</td>
                    <td>客户当前库存</td>
                    <td>最后交易价格</td>
                    <td>最后交易数量</td>
                    <td>最后交易日期</td>
                </tr>
            </table>
            <div class="stockPager" id="stockPager2" style="margin-right:15px;float: left">
                <jsp:include page="/common/specailStockPager.jsp">
                    <jsp:param name="dynamical" value="cusStock2"></jsp:param>
                    <jsp:param name="buttonId" value="getCustomerStock2"></jsp:param>
                </jsp:include>
            </div>

            <table cellpadding="0" cellspacing="0" class="tabSlip" id="cusStock3" style="width: 100%;display: none">
                <input type="hidden"  id="customerShopId3" value="-1">
                <col>
                <col width="90">
                <col width="90">
                <col width="90">
                <col width="75">
                <tr class="titleBg J-title">
                    <td style="padding-left:10px;">配件名</td>
                    <td>客户当前库存</td>
                    <td>最后交易价格</td>
                    <td>最后交易数量</td>
                    <td>最后交易日期</td>
                </tr>
            </table>
            <div class="stockPager" id="stockPager3" style="margin-right:15px;float: left">
                <jsp:include page="/common/specailStockPager.jsp">
                    <jsp:param name="dynamical" value="cusStock3"></jsp:param>
                    <jsp:param name="buttonId" value="getCustomerStock3"></jsp:param>
                </jsp:include>
            </div>

            <table cellpadding="0" cellspacing="0" class="tabSlip" id="cusStock4" style="width: 100%;display: none">
                <input type="hidden"  id="customerShopId4" value="-1">
                <col>
                <col width="90">
                <col width="90">
                <col width="90">
                <col width="75">
                <tr class="titleBg J-title">
                    <td style="padding-left:10px;">配件名</td>
                    <td>客户当前库存</td>
                    <td>最后交易价格</td>
                    <td>最后交易数量</td>
                    <td>最后交易日期</td>
                </tr>
            </table>
            <div class="i_height"></div>
            <div class="stockPager" id="stockPager4" style="margin-right:15px;float: left">
                <jsp:include page="/common/specailStockPager.jsp">
                    <jsp:param name="dynamical" value="cusStock4"></jsp:param>
                    <jsp:param name="buttonId" value="getCustomerStock4"></jsp:param>
                </jsp:include>
            </div>

            <div class="i_height"></div>
            <div class="i_height"></div>
            <div class="allCustomerStockPager" id="allCustomerStockPager" style="margin-right:150px;margin-top: 20px;float: left;display: none">
                <jsp:include page="/common/specailStockPager.jsp">
                    <jsp:param name="dynamical" value="allCustomerStock"></jsp:param>
                    <jsp:param name="buttonId" value="getAllCustomerStock"></jsp:param>
                </jsp:include>
            </div>
        </div>
    </div>
</div>

<div id="div_suggestionList" class="i_scroll suggestionMain" style="display:none;">
  <div class="Container">
    <div class="Scroller-Container" id="suggestionContainer"></div>
  </div>
</div>
<div id="mask" style="display:block;position: absolute;"></div>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>