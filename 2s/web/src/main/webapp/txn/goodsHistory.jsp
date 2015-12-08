<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.bcgogo.config.ConfigController" %>
<%@ page import="com.bcgogo.search.dto.ItemIndexDTO" %>
<%@ page import="java.util.List" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<%
    ItemIndexDTO itemIndex = (ItemIndexDTO) request.getAttribute("command");
    List<ItemIndexDTO> itemIndexList = (List<ItemIndexDTO>) request.getAttribute("itemIndexList");
    Integer listSize = itemIndexList != null ? itemIndexList.size() : 0;
//  Boolean isGoodsBuy = itemIndex != null ? itemIndex.getGoodsBuyOrderType() : false;
//  Boolean isGoodsStorage = itemIndex != null ? itemIndex.getGoodsStorageOrderType() : false;
//  Boolean isGoodsSale = itemIndex != null ? itemIndex.getGoodsSaleOrderType() : false;
//  Boolean isRepairOrder = itemIndex != null ? itemIndex.getRepairOrderType() : false;
//  Boolean isReturnOrder = itemIndex != null ? itemIndex.getReturnOrderType() : false;
    String pageNo = "1";
    if (null != itemIndex && null != itemIndex.getPageNo() && !"".equals(itemIndex.getPageNo())) {
        pageNo = itemIndex.getPageNo();
    }
%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>进销存_库存查询</title>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/moreHistory<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/up<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/carSearch<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/goodsHistory<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="js/extension/jquery/plugin/jquery-ui/themes/redmond/jquery-ui-1.8.21.custom.css"/>
    <link rel="stylesheet" type="text/css" href="js/extension/jquery/plugin/jquery-ui/themes/redmond/jquery.ui.timepicker-addon.css"/>
	<link rel="stylesheet" type="text/css" href="js/components/themes/bcgogo-scanning<%=ConfigController.getBuildVersion()%>.css" />
    <style>
        #ui-datepicker-div, .ui-datepicker {
            font-size: 100%;
        }
    </style>
    <script type="text/javascript" src="js/extension/jquery/jquery-1.4.2.min.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery-ui/ui/jquery-ui-1.8.21.custom.min.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery-ui/ui/jquery.ui.timepicker-addon.min.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery-ui/ui/i18n/jquery.ui.datepicker-zh-CN.min.js"></script>
    <script type="text/javascript" src="js/base<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/application<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/module/bcgogo-scanning<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/showtime<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/common<%=ConfigController.getBuildVersion()%>.js"></script>


    <script type="text/javascript">
        $(document).ready(function() {
            $(document).keydown(function(e) {
                checkHistoryEvent(e);
            });

            $("#searchButton_id").bind("click", function() {
                document.thisform.pageNo.value = 1;
                document.thisform.submit();
            });
            var flag = $("#flag").val();
            if (flag == "fresh") {
//                $("#searchButton_id").click();
                document.thisform.submit();
            }

            //设置事件endDateStr
            $("#startDateStr").val(new Date().now);
            $("#endDateStr").val(new Date().now);

            document.getElementById("div_close").onclick = closeWindow;

        });

        function closeWindow() {
            window.parent.document.getElementById("mask").style.display = "none";
            if (window.parent.document.getElementById("iframe_PopupBox") != null) {
                window.parent.document.getElementById("iframe_PopupBox").style.display = "none";
                window.parent.document.getElementById("iframe_PopupBox").src = "";
            }
            if (window.parent.document.getElementById("iframe_PopupBox_1") != null) {
                window.parent.document.getElementById("iframe_PopupBox_1").style.display = "none";
                window.parent.document.getElementById("iframe_PopupBox_1").src = "";
            }
            try {
                $(window.parent.document.body).find("input[type='button']").eq(0).focus().blur();
            } catch(e) {
                ;
            }
        }
        $(function() {
            var pn = parseInt("${itemIndex.pageNo}", 10);
            $("#pageNo_id").html(pn);

            if ($("#pageNo_id").html() == "1") {
                $(".lastPage").css('display', 'none');
            }
        });
        function checkBoxSelect(domObj, flag) {
            var domClass = $(domObj).attr("class");
            if (domClass == "search_chk") {
                $(domObj).attr("class", "search_chked");
                isSelectItem(flag, true);
            } else {
                $(domObj).attr("class", "search_chk");
                isSelectItem(flag, false);
            }
        }
        function isSelectItem(flag, booleanValue) {
            if (flag == 1) {
                document.thisform.goodsBuyOrderType.value = booleanValue;
            }
            if (flag == 2) {
                document.thisform.goodsStorageOrderType.value = booleanValue;
            }
            if (flag == 3) {
                document.thisform.goodsSaleOrderType.value = booleanValue;
            }
            if (flag == 4) {
                document.thisform.repairOrderType.value = booleanValue;
            }
            if (flag == 8) {
                document.thisform.returnOrderType.value = booleanValue;
            }
        }

        $().ready(function() {
            $("#startDateStr,#endDateStr").datetimepicker({
                "numberOfMonths" : 1,
                "showButtonPanel": true,
                "changeYear":true,
                "changeMonth":true,
                "yearRange":"c-100:c+100",
                "yearSuffix":""
            });
        });
    </script>
</head>
<body>

<div class="i_supplierInfo more_supplier" id="div_show">
<div class="i_arrow"></div>
<div class="i_upLeft"></div>
<div class="i_upCenter i_two">
    <div class="i_note more_title" id="moreGoodHistory">商品历史查询</div>
    <div class="i_close" id="div_close"></div>
</div>
<div class="i_upRight"></div>
<div class="i_upBody clear">
<div class="i_main clear">
<!--历史搜素-->
<form:form commandName="command" action="goodsHistory.do?method=searchGoodsHistory" method="post"
           name="thisform">
    <form:hidden path="orderType"/>
    <form:hidden path="pageNo"/>

    <input type="hidden" value="${flag}" id="flag"/>

    <div class="his_search clear">
        <div class="goods_chk clear">
            <bcgogo:hasPermission permissions="WEB.TXN.PURCHASE_MANAGE.PURCHASE" resourceType="menu">
                <form:checkbox path="selectedOrderTypes" value="PURCHASE" cssClass="orderTypeCheckbox"
                               label="采购单"/>
            </bcgogo:hasPermission>
            <bcgogo:hasPermission permissions="WEB.TXN.PURCHASE_MANAGE.STORAGE" resourceType="menu">
                <form:checkbox path="selectedOrderTypes" value="INVENTORY" cssClass="orderTypeCheckbox"
                               label="入库单"/>
            </bcgogo:hasPermission>
            <bcgogo:hasPermission permissions="WEB.TXN.SALE_MANAGE.SALE" resourceType="menu">
                <form:checkbox path="selectedOrderTypes" value="SALE" cssClass="orderTypeCheckbox" label="销售单"/>
            </bcgogo:hasPermission>
            <bcgogo:hasPermission permissions="WEB.VEHICLE_CONSTRUCTION.CONSTRUCT.BASE" resourceType="menu">
                <form:checkbox path="selectedOrderTypes" value="REPAIR" cssClass="orderTypeCheckbox"
                               label="施工单"/>
            </bcgogo:hasPermission>
            <bcgogo:hasPermission permissions="WEB.TXN.PURCHASE_MANAGE.RETURN"  resourceType="menu">
                <form:checkbox path="selectedOrderTypes" value="RETURN" cssClass="orderTypeCheckbox"
                               label="入库退货单"/>
            </bcgogo:hasPermission>
            <ul>
                <li class="search_user"><label>供应商/客户</label>
                    <form:input path="customerOrSupplierName" onchange="document.thisform.pageNo.value=1;pn=1"/>
                </li>
                <li class="clear"><label>品 名</label><form:input path="itemName"
                                                                onchange="document.thisform.pageNo.value=1;pn=1"/>
                        <%--<input type="button" class="i_icon" onfocus="this.blur();"/>--%>
                </li>
                <li><label>品 牌/产地</label><form:input path="itemBrand"
                                                     onchange="document.thisform.pageNo.value=1;pn=1"/>
                        <%--<input type="button" class="i_icon" onfocus="this.blur();"/></li>--%>
                <li><label>规 格</label><form:input path="itemSpec"
                                                  onchange="document.thisform.pageNo.value=1;pn=1"/>
                        <%--<input type="button" class="i_icon" onfocus="this.blur();"/></li>--%>
                <li><label>型 号</label><form:input path="itemModel"
                                                  onchange="document.thisform.pageNo.value=1;pn=1"/>
                        <%--<input type="button" class="i_icon" onfocus="this.blur();"/></li>--%>
            </ul>
        </div>
        <div class="his_time">
            <div class="time_search">
                <label>时 间</label>

                <div class="i_searchTime">
                        <%--<form:input id="endDateStr" path="endDateStr" readonly="readonly"/>--%>
                    <input id="startDateStr" name="startDateStr" readonly="readonly"
                           value="${command.startDateStr}"/>
                </div>
                <label>至</label>

                <div class="i_searchTime">
                        <%--<form:input id="endDateStr2" path="endDateStr2" readonly="readonly"/>--%>
                    <input id="endDateStr" name="endDateStr" readonly="readonly"
                           value="${command.endDateStr}"/>
                </div>

            </div>
            <div class="his_list clear">

                <div class="today_list his_first">
                    <a id="a_today" class="his_now">今天</a>
                        <%--<a href="#" class="his_top"></a>--%>
                        <%--<a href="#" class="his_bottom"></a>--%>
                </div>
                <div class="today_list">
                    <a id="a_week" class="his_now">本周</a>
                        <%--<a href="#" class="his_top"></a>--%>
                        <%--<a href="#" class="his_bottom"></a>--%>
                </div>
                <div class="today_list">
                    <a id="a_month" class="his_now">本月</a>
                        <%--<a href="#" class="his_top"></a>--%>
                        <%--<a href="#" class="his_bottom"></a>--%>
                </div>
                <div class="today_list">
                    <a id="a_year" class="his_now">今年</a>
                        <%--<a href="#" class="his_top"></a>--%>
                        <%--<a href="#" class="his_bottom"></a>--%>
                </div>

            </div>
            <div class="search_his clear" id="searchButton_id">

                <label>搜索</label>
            </div>
        </div>
        <div class="clear"></div>
    </div>
</form:form>

<!--历史搜素结束-->
<!--数据显示-->
<p><label class="qian_blue">
</label></p>
<table cellpadding="0" cellspacing="0" class="table2" style="table-layout:fixed">
    <col width="45"/>
    <col width="85"/>
    <col width="80"/>
    <col width="70"/>
    <col width="65"/>
    <col width="50"/>
    <col width="35"/>
    <col width="65"/>
    <col width="75"/>
    <col width="85"/>
    <col width="55"/>
    <col width="60"/>
    <col width="60"/>
    <col width="70"/>
    <tr class="table_title">
        <td style="border-left:none;">NO</td>
        <td>时间</td>
        <td>客户/供应商</td>
        <td>单据类型</td>
        <td class="txt_right">单价</td>
        <td class="txt_right">数量</td>
        <td class="txt_right" style="text-overflow:clip;">单位</td>
        <td class="txt_right">金额</td>
        <td>品名</td>
        <td>品牌/产地</td>
        <td>规格</td>
        <td>型号</td>
        <td>状态</td>
        <td style="border-right:none;">操作</td>
    </tr>
    <c:forEach items="${itemIndexList}" var="itemDTO" varStatus="status">
        <tr>
            <td style="border-left:none;">${status.index+1}</td>
            <td>${itemDTO.orderTimeCreatedStr}</td>
            <td style="text-overflow:ellipsis;overflow:hidden;"
                title="${itemDTO.customerOrSupplierName}">${itemDTO.customerOrSupplierName}</td>
            <td>${itemDTO.orderTypeStr}</td>
            <td class="txt_right">${itemDTO.itemPrice}</td>
            <td class="txt_right">${itemDTO.itemCount}</td>
            <td class="txt_right">${itemDTO.unit}</td>
            <td class="txt_right">${itemDTO.itemTotalAmount}</td>
            <td style="text-overflow:ellipsis;overflow:hidden;"
                title="${itemDTO.itemName}">${itemDTO.itemName}</td>
            <td style="text-overflow:ellipsis;overflow:hidden;"
                title="${itemDTO.itemBrand}">${itemDTO.itemBrand}</td>
            <td style="text-overflow:ellipsis;overflow:hidden;"
                title="${itemDTO.itemSpec}">${itemDTO.itemSpec}</td>
            <td style="text-overflow:ellipsis;overflow:hidden;"
                title="${itemDTO.itemModel}">${itemDTO.itemModel}</td>
            <td><c:if test="${itemDTO.orderStatus!=null}">${itemDTO.orderStatus.name}</c:if></td>

            <td style="border-right:none;" class="qian_blue">
                <c:if test="${itemDTO.orderType == 'PURCHASE'}">
                    <a href="${basePath}RFbuy.do?method=show&id=${itemDTO.orderId}"
                       target="_parent">点击详细</a>
                </c:if>
                <c:if test="${itemDTO.orderType == 'INVENTORY'}">
                    <a href="${basePath}storage.do?method=getPurchaseInventory&menu-uid=WEB.TXN.PURCHASE_MANAGE.STORAGE&purchaseInventoryId=${itemDTO.orderId}"
                       target="_parent">点击详细</a>
                </c:if>
                <c:if test="${itemDTO.orderType == 'SALE'}">
                    <a href="${basePath}sale.do?method=getSalesOrder&menu-uid=WEB.TXN.SALE_MANAGE.SALE&salesOrderId=${itemDTO.orderId}"
                       target="_parent">点击详细</a>
                </c:if>
                <c:if test="${itemDTO.orderType == 'REPAIR'}">
                    <a href="${basePath}txn.do?method=getRepairOrder&menu-uid=VEHICLE_CONSTRUCTION_REPAIR&repairOrderId=${itemDTO.orderId}"
                       target="_parent">点击详细</a>
                </c:if>
                <c:if test="${itemDTO.orderType == 'RETURN'}">
                    <a href="${basePath}goodsReturn.do?method=getPurchaseReturnOrder&purcahseReturnId=${itemDTO.orderId}"
                       target="_parent">点击详细</a>
                </c:if>

                <c:if test="${itemDTO.orderType == 'WASH'}">
                    <a href="<%=basePath%>washBeauty.do?method=getWashBeautyOrder&washBeautyOrderId=${itemDTO.orderId}"
                       target="_parent">点击详细</a>
                </c:if>
            </td>
        </tr>

    </c:forEach>
</table>

<c:if test="${pager != null}">
    <jsp:include page="/common/paging.jsp">
        <jsp:param name="url" value="goodsHistory.do?method=searchGoodsHistory"></jsp:param>
        <jsp:param name="submit" value="thisform"></jsp:param>
    </jsp:include>
</c:if>

<%--<div class="i_leftBtn clear" id="i_leftBtn_id">--%>
<%--<div class="lastPage">上一页</div>--%>
<%--<div class="onlin_his" id="pageNo_id">1</div>--%>
<%--<%if (listSize >= 5) {%>--%>
<%--<div class="nextPage">下一页</div>--%>
<%--<%}%>--%>
<%--</div>--%>
<!--数据显示结束-->
</div>
<!--数据显示结束-->
</div>
</div>
<%--<div class="i_upBottom">
    <div class="i_upBottomLeft"></div>
    <div class="i_upBottomCenter"></div>
    <div class="i_upBottomRight"></div>
</div>--%>
</div>


</body>
</html>