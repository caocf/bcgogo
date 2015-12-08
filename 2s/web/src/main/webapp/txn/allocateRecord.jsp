<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>仓库调拨</title>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/allocateRecord<%=ConfigController.getBuildVersion()%>.css"/>
    <%@include file="/WEB-INF/views/header_script.jsp" %>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.js"></script>
    <script type="text/javascript" src="js/page/txn/txn<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/utils/tableUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/selectOptions<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/script<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/page/txn/txn<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/page/txn/allocateRecord<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/module/invoiceCommon<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/suggestion<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">
        defaultStorage.setItem(storageKey.MenuUid, "WEB.TXN.INVENTORY_MANAGE.ALLOCATE_RECORD");
        defaultStorage.setItem(storageKey.MenuCurrentItem,"新增");
    </script>
</head>
<body class="bodyMain">
<%@include file="/WEB-INF/views/header_html.jsp" %>
<%@ include file="/common/messagePrompt.jsp" %>
<div class="i_main clear">
    <jsp:include page="txnNavi.jsp">
        <jsp:param name="currPage" value="inventory"/>
    </jsp:include>
    <jsp:include page="inventroyNavi.jsp">
        <jsp:param name="currPage" value="allocateRecord"/>
        <jsp:param name="isRepairPickingSwitchOn" value="<%=isRepairPickingSwitchOn%>"/>
    </jsp:include>
    <div id="i_mainRight" class="i_mainRight shoppingCart">
        <div class="cartTop"></div>
        <div class="titBody cartBody">
            <form:form commandName="allocateRecordDTO" id="allocateRecordForm" action="allocateRecord.do?method=saveAllocateRecord" class="J_leave_page_prompt" method="post" style="padding-top:11px">
                <input id="orderType" name="orderType" value="allocateRecord" type="hidden"/>
                <input type="hidden" id="basePath" name="bathPath" value="<%=basePath%>"/>
                <form:hidden path="updateLackOrderId"/>
                <form:hidden path="updateLackOrderType"/>
                <form:hidden path="returnType"/>
                <form:hidden path="id"/>
                <form:hidden path="selectSupplier" value="${WEB_VERSION_PRODUCT_THROUGH_SELECT_SUPPLIER}"/>
                <div class="divTit" style="width:150px;">
                    单据号
                    <span id="receiptNoSpan" class="receiptNoSpan">系统自动生成</span>
                </div>
                <div class="divTit">调出仓库
                    <form:select path="outStorehouseId" cssClass="selTit j_checkStoreHouse" cssStyle="width:120px;height:21px;" autocomplete="off">
                        <option value="">—请选择仓库—</option>
                        <form:options items="${storeHouseDTOList}" itemValue="id" itemLabel="name"/>
                    </form:select>
                </div>
                <div class="divTit">调入仓库
                    <form:select path="inStorehouseId" cssClass="selTit" cssStyle="width:120px;height:21px;" autocomplete="off">
                        <option value="">—请选择仓库—</option>
                        <form:options items="${storeHouseDTOList}" itemValue="id" itemLabel="name"/>
                    </form:select>
                </div>
                <div class="divTit">
                    操作人<form:hidden path="editor" autocomplete="off"/><form:hidden path="editorId" autocomplete="off"/>
                    <span style="font-weight: normal;">${allocateRecordDTO.editor}</span>
                </div>
                <div class="divTit">调拨日期<form:input id="orderVestDate" path="vestDateStr" ordertype="allocateRecord" cssClass="txt" cssStyle="margin-left: 6px;"/></div>
                <table class="table2 table_product" id="table_productNo">
                    <col width="90">
                    <col width="100">
                    <col width="100">
                    <col width="95">
                    <col width="95">
                    <col width="95">
                    <col width="95">
                    <col>
                    <col width="110">
                    <col width="40">
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
                        <td>成本</td>
                        <td>调拨数/库存量</td>
                        <td>单位</td>
                        <td>小计</td>
                        <td>操作</td>
                    </tr>
                    <tr class="space">
                        <td colspan="15"></td>
                    </tr>
                    <c:forEach items="${allocateRecordDTO.itemDTOs}" var="itemDTO" varStatus="status">
                        <tr class="bg item table-row-original">
                            <td style="padding-left:10px;">
                                <form:input path="itemDTOs[${status.index}].commodityCode" cssClass="textbox" maxlength="20"/>
                            </td>
                            <td>
                                <form:hidden path="itemDTOs[${status.index}].id" autocomplete="off"/>
                                <form:hidden path="itemDTOs[${status.index}].productId" autocomplete="off"/>
                                <form:input path="itemDTOs[${status.index}].productName" cssClass="textbox"/>
                            </td>
                            <td>
                                <form:input path="itemDTOs[${status.index}].brand" cssClass="textbox"/>
                            </td>
                            <td>
                                <form:input path="itemDTOs[${status.index}].spec" cssClass="textbox"/>
                            </td>
                            <td>
                                <form:input path="itemDTOs[${status.index}].model" cssClass="textbox"/>
                            </td>
                            <td>
                                <form:input path="itemDTOs[${status.index}].vehicleBrand" cssClass="textbox"/>
                            </td>
                            <td>
                                <form:input path="itemDTOs[${status.index}].vehicleModel" cssClass="textbox"/>
                            </td>

                            <td>
                                <form:input path="itemDTOs[${status.index}].costPrice" cssClass="itemCostPrice txt"
                                            cssStyle="width:60px;border: 0 none;background-color:transparent;"
                                            readonly="true"/>
                            </td>
                                <%--<td class="storage_bin_td">--%>
                                <%--<form:input path="itemDTOs[${status.index}].inStorageBin" cssStyle="width:40px;" cssClass="txt" maxlength="10"/>--%>
                                <%--</td>--%>
                            <td>
                                <bcgogo:permission>
                                    <bcgogo:if resourceType="logic" permissions="WEB.VERSION.PRODUCT.THROUGH_SELECT_SUPPLIER">
                                        <form:input path="itemDTOs[${status.index}].amount" cssStyle="width:30px;" cssClass="itemAmount textbox" value="" data-filter-zero="true"/>
                                    </bcgogo:if>
                                    <bcgogo:else>
                                        <form:input path="itemDTOs[${status.index}].amount" cssStyle="width:30px;" cssClass="itemAmount textbox" data-filter-zero="true"/>
                                    </bcgogo:else>
                                </bcgogo:permission>

                                /
                                <form:input path="itemDTOs[${status.index}].inventoryAmount" cssClass="txt"
                                            cssStyle="width:40px;border: 0 none;background-color:transparent;display: inline;"
                                            readonly="true"/>
                            </td>
                            <td>
                                <form:input path="itemDTOs[${status.index}].unit" cssClass="txt"
                                            cssStyle="border: 0 none;background-color:transparent;"
                                            readonly="true"/>
                            </td>
                            <td>
                                <form:input path="itemDTOs[${status.index}].totalCostPrice" cssClass="itemTotalCostPrice txt"
                                            cssStyle="width:60px;border: 0 none;background-color:transparent;"
                                            readonly="true"/>
                            </td>
                            <td>
                                <a id="itemDTOs${status.index}.deleteRowBtn" class="opera1" name="itemDTOs[${status.index}].deleteRowBtn"  onfocus="this.blur();">删除</a>
                                    <%--<a id="itemDTOs${status.index}.addRowBtn" name="itemDTOs[${status.index}].addRowBtn">增加</a>--%>
                            </td>
                        </tr>
                    </c:forEach>
                    <tr class="heji_tb" style="font-weight:bold;">
                        <td colspan="8" style="text-align:center;color: #272727;">合计：</td>
                        <td style="text-align: center;">
                            <form:input path="totalAmount" cssStyle="padding-left:2px;border:0;font-weight:bold;width:80%;background-color:transparent;"
                                        readonly="true"/>
                        </td>
                        <td></td>
                        <td colspan="2">
                            <form:input path="totalCostPrice" cssStyle="border:0;font-weight:bold;width:80%;background-color:transparent;" readonly="true"/>
                        </td>
                    </tr>
                </table>
            </form:form>
        </div>
        <div class="height"></div>
        <div class="shopping_btn">
            <div class="btn_div_Img" id="confirm_div">
                <input id="confirmBtn" type="button" class="confirm j_btn_i_operate" onfocus="this.blur();"/>
                <div class="sureWords">确认调拨</div>
            </div>
            <div class="btn_div_Img" id="return_list_div">
                <input id="returnListBtn" type="button" class="return_list j_btn_i_operate" onfocus="this.blur();"/>
                <div class="sureWords">返回列表</div>
            </div>
        </div>
    </div>
</div>
<div id="mask" style="display:block;position: absolute;"></div>
<div id="commodityCode_dialog" style="display:none">
    <span id="commodityCode_dialog_msg"></span>
</div>
<div id="id-searchcomplete"></div>
<div id="id-searchcompleteMultiselect"></div>
<div id="div_brand" class="i_scroll" style="display:none;height:230px">
    <div class="Scroller-Container" id="Scroller-Container_id"></div>
</div>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>