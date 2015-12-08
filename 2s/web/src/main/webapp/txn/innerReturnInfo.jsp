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
    <link rel="stylesheet" type="text/css" href="styles/addCheck<%=ConfigController.getBuildVersion()%>.css"/>
    <style type="text/css">
        <%--style.css中的hover 被salesSlip.css影响到了,再盖一层--%>
        .hover {
            margin-left: 0px;
            margin-right: 0px;
            margin-top: 0px;
            color: #6699CC;
        }
        ul li {
            float: none;
        }
    </style>
    <%@include file="/WEB-INF/views/header_script.jsp" %>
    <!-- BCSHOP-6044 缺少光标定位函数 begin -->
    <script type="text/javascript" src="js/script<%=ConfigController.getBuildVersion()%>.js"></script>
    <!-- BCSHOP-6044 缺少光标定位函数 end -->
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.js"></script>
    <script type="text/javascript" src="js/page/txn/txn<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/inventorySearchIndex<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/selectOptions<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/utils/tableUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/suggestion<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/module/invoiceCommon<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/pickingCommon<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/innerReturnInfo<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/storeHouseDialog<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">
        defaultStorage.setItem(storageKey.MenuCurrentItem,"新增");
    </script>
</head>
<body class="bodyMain">
<%@include file="/WEB-INF/views/header_html.jsp" %>

<div class="i_main clear">
    <jsp:include page="unit.jsp"/>
    <jsp:include page="txnNavi.jsp">
        <jsp:param name="currPage" value="inventory"/>
    </jsp:include>
    <jsp:include page="inventroyNavi.jsp">
        <jsp:param name="currPage" value="innerReturn"/>
        <jsp:param name="isRepairPickingSwitchOn" value="<%=isRepairPickingSwitchOn%>"/>
    </jsp:include>
    <div id="i_mainRight" class="i_mainRight shoppingCart">
        <div class="cartBody ">
            <input type="hidden" id="orderType" value="innerReturn">

            <c:if test="${!empty result}">
                <input type="hidden" id="innerReturnMessage" value="${result.msg}" resultDate="${result.data}"
                       resultOperation="${result.operation}">
            </c:if>
            <form:form action="pick.do?method=saveInnerReturn" id="innerReturnForm" commandName="innerReturnDTO" method="post" class="J_leave_page_prompt">
                <%--<c:if test="${innerReturnDTO.receiptNo}">--%>
                <%--<div class="divTit">--%>
                <%--单据号：<span>${innerReturnDTO.receiptNo}</span>--%>
                <%--<form:hidden path="receiptNo"/>--%>
                <%--</div>--%>
                <%--</c:if>--%>
                 <form:hidden path="selectSupplier" value="${WEB_VERSION_PRODUCT_THROUGH_SELECT_SUPPLIER}"/>
                <div class="divTit" style="width:170px;">
                    单据号
                    <span id="receiptNoSpan" class="receiptNoSpan" style="font-weight: normal;">系统自动生成</span>
                </div>
                <div id="pickingManDiv" class="divTit" style="width:190px;">退料人：<form:input path="pickingMan" readonly="true" cssClass="txt"/>
                    <img id="deleteSaler" style="width: 12px; cursor: pointer; display: none;" src="images/list_close.png">
                    <form:hidden path="pickingManId" readonly="true" cssClass="txt"/>
                </div>
                <c:if test="${innerReturnDTO.isHaveStoreHouse}">
                    <div class="divTit">退料仓库：
                        <form:select path="storehouseId" cssClass="j_checkStoreHouse"
                                     cssStyle="width:120px;height:21px;border: 1px solid #BBBBBB;" autocomplete="off">
                            <option value="">—请选择仓库—</option>
                            <form:options items="${storeHouseDTOs}" itemValue="id" itemLabel="name"/>
                        </form:select>
                    </div>
                </c:if>
                <div class="divTit">操作人：<span>${innerReturnDTO.operationMan}</span></div>
                <div class="divTit">退料日期：<form:input path="vestDateStr" readonly="true" cssClass="txt" style="width:140px;"/></div>

                <table cellpadding="0" cellspacing="0" class="table2" id="table_productNo">
                    <col width="90">
                    <col width="100">
                    <col width="100">
                    <col width="95">
                    <col width="95">
                    <col width="95">
                    <col width="95">
                    <col width="40">
                    <col width="40">
                    <col width="100">
                    <col width="50">
                    <col width="70">
                    <tr class="titleBg">
                        <td style="padding-left:10px;">商品编号</td>
                        <td>品名</td>
                        <td>品牌/产地</td>
                        <td>规格</td>
                        <td>型号</td>
                        <td>车辆品牌</td>
                        <td>车型</td>
                        <td>成本价</td>
                        <td>单位</td>
                        <td>退料数</td>
                        <td>小计</td>
                        <td>操作</td>
                    </tr>
                    <tr class="space">
                        <td colspan="15"></td>
                    </tr>
                    <c:forEach items="${innerReturnDTO.itemDTOs}" var="itemDTO" varStatus="status">
                        <tr class="bg item table-row-original">
                            <td style="padding-left:10px;">
                                <form:input path="itemDTOs[${status.index}].commodityCode" value='${itemDTO.commodityCode}'
                                            class="txt checkStringEmpty" title='${itemDTO.commodityCode}' maxlength="20"/>
                                <form:hidden path="itemDTOs[${status.index}].id" value='${itemDTO.id}'/>
                                <form:hidden path="itemDTOs[${status.index}].productId" value='${itemDTO.productIdStr}'/>
                            </td>
                            <td>
                                <form:input path="itemDTOs[${status.index}].productName" value='${itemDTO.productName}'
                                            class="txt checkStringEmpty" title='${itemDTO.productName}' maxlength="50"/>
                            </td>
                            <td>
                                <form:input path="itemDTOs[${status.index}].brand" value='${itemDTO.brand}'
                                            class="txt checkStringEmpty" title='${itemDTO.brand}' maxlength="50"/>
                            </td>
                            <td>
                                <form:input path="itemDTOs[${status.index}].spec" value='${itemDTO.spec}'
                                            class="txt checkStringEmpty" title='${itemDTO.spec}' maxlength="50"/>
                            </td>
                            <td>
                                <form:input path="itemDTOs[${status.index}].model" value='${itemDTO.model}'
                                            class="txt checkStringEmpty" title='${itemDTO.model}' maxlength="50"/>
                            </td>
                            <td>
                                <form:input path="itemDTOs[${status.index}].vehicleBrand" value='${itemDTO.vehicleBrand}'
                                            class="txt checkStringEmpty" title='${itemDTO.vehicleBrand}' maxlength="200"/>
                            </td>
                            <td>
                                <form:input path="itemDTOs[${status.index}].vehicleModel" value='${itemDTO.vehicleModel}'
                                            class="txt checkStringEmpty" title='${itemDTO.vehicleModel}' maxlength="200"/>
                            </td>
                            <td>
                            <span id="itemDTOs${status.index}.price_span" name="itemDTOs[${status.index}].price_span"
                                  title="${itemDTO.price}">${itemDTO.price}</span>
                                <form:hidden path="itemDTOs[${status.index}].price" cssClass="itemPrice" value='${itemDTO.price}'/>
                            </td>

                            <td>
                                <form:input path="itemDTOs[${status.index}].unit" value="${itemDTO.unit}"
                                            class="itemUnit txt checkStringEmpty"/>
                                <form:hidden path="itemDTOs[${status.index}].storageUnit" value="${itemDTO.storageUnit}"
                                             class="itemStorageUnit txt"/>
                                <form:hidden path="itemDTOs[${status.index}].sellUnit" value="${itemDTO.sellUnit}"
                                             class="itemSellUnit txt"/>
                                <form:hidden path="itemDTOs[${status.index}].rate" value="${itemDTO.rate}"
                                             class="itemRate txt"/>
                            </td>

                            <td>
                                <form:input path="itemDTOs[${status.index}].amount" value='${itemDTO.amount}'
                                            class="txt checkNumberEmpty itemAmount" title='${itemDTO.amount}' maxlength="20" data-filter-zero="true"
                                            cssStyle="width: 40px;"/>
                            </td>
                            <td>
                            <span id="itemDTOs${status.index}.total_span"
                                  name="itemDTOs[${status.index}].total_span"
                                  title="${itemDTO.total}">${itemDTO.total}</span>
                                <form:hidden path="itemDTOs[${status.index}].total"
                                             value='${itemDTO.total}'/>
                            </td>
                            <td>
                                <a id="itemDTOs${status.index}.btnMinus" class="btnMinus" type="button"
                                       name="itemDTOs[${status.index}].btnMinus">删除</a>
                                <%--<input id="itemDTOs${status.index}.btnPlus" class="btnPlus" type="button"--%>
                                       <%--name="itemDTOs[${status.index}].btnPlus">--%>
                            </td>
                        </tr>
                    </c:forEach>
                    <tr style="font-weight:bold;border-top:1px solid #AAAAAA" id="totalRowTR">
                        <td colspan="9" style="text-align:center;">合计：</td>
                        <td>
                            <span id="totalItemAmount_span" data-filter-zero="true"></span>
                        </td>
                        <td>
                            <span id="total_span" data-filter-zero="true">${innerReturnDTO.total}</span>
                            <form:hidden path="total"/>
                        </td>
                        <td></td>
                    </tr>
                </table>

            </form:form>
        </div>
        <div class="height"></div>
        <%--<div class="invalidImgLeftShow">--%>
        <%--<input id="print_btn" class="print j_btn_i_operate" type="button" onfocus="this.blur();">--%>
        <%--<div class="sureWords">打印</div>--%>
        <%--</div>--%>
        <div class="shopping_btn">
            <div class="btn_div_Img">
                <input id="picking_btn" class="sureInventory j_btn_i_operate" type="button" onfocus="this.blur();">
                <div class="sureWords">确 定</div>
            </div>
            <div class="btn_div_Img">
                <input id="show_innerReturn_list_btn" class="return_list j_btn_i_operate" type="button" onfocus="this.blur();">
                <div class="sureWords">返回列表</div>
            </div>
        </div>
    </div>
</div>
<div id="mask" style="display:block;position: absolute;">
</div>
<div id="commodityCode_dialog" style="display:none">
    <span id="commodityCode_dialog_msg"></span>
</div>
<div id="storeHouseDialog" style="display:none;" class="alertMain">
    <div style="margin-top: 10px;">
        <span style="font-size: 14px;">您没有选择仓库信息！请选择仓库：</span>
        <select id="storehouseDiv"
                style="width:120px;height:21px;border: 1px solid #BBBBBB;" autocomplete="off">
            <option value="">—请选择仓库—</option>
            <c:forEach items="${storeHouseDTOs}" var="storeHouseDTO">
                <option value="${storeHouseDTO.id}">${storeHouseDTO.name}</option>
            </c:forEach>
        </select>
        <input id="btnType" type="hidden" />
    </div>
    <div class="button" style="width:100%;margin-top: 10px;">
        <a id="confirmBtn1" class="btnSure" href="javascript:;">确 定</a>
        <a id="cancleBtn" class="btnSure" href="javascript:;">取消</a>
    </div>
</div>
<div id="id-searchcomplete"></div>
<div id="id-searchcompleteMultiselect"></div>
<div id="div_brand" class="i_scroll" style="display:none;height:230px">
    <div class="Scroller-Container" id="Scroller-Container_id"></div>
</div>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>