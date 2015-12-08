<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>库存盘点</title>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/inventoryCheck<%=ConfigController.getBuildVersion()%>.css"/>
    <%@include file="/WEB-INF/views/header_script.jsp" %>
    <script type="text/javascript" src="js/utils/tableUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/script<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/suggestion<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/module/invoiceCommon<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/page/txn/txn<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/page/txn/inventoryCheck<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/selectOptions<%=ConfigController.getBuildVersion()%>.js"></script>
     <script type="text/javascript">
         defaultStorage.setItem(storageKey.MenuUid,"WEB.TXN.INVENTORY_MANAGE.INVENTORY_PRICE_ADJUSTMENT.BASE");
         <c:choose>
         <c:when test="${not empty inventoryCheckDTO.id}">
         defaultStorage.setItem(storageKey.MenuCurrentItem,"详情");
         </c:when>
         <c:otherwise>
         defaultStorage.setItem(storageKey.MenuCurrentItem,"新增");
         </c:otherwise>
         </c:choose>
     </script>
</head>
<body class="bodyMain">
<%@ include file="/WEB-INF/views/header_html.jsp" %>
<%@ include file="/common/messagePrompt.jsp" %>

<input type="hidden" id="basePath" name="bathPath" value="<%=basePath%>"/>
<!-- 搜索下拉, TODO 以后移到组件里 -->
<div id="id-searchcomplete"></div>
<div id="id-searchcompleteMultiselect"></div>
<div id="div_brand" class="i_scroll" style="display:none;">
    <div class="Scroller-Container" id="Scroller-Container_id">
    </div>
</div>
<div class="i_main clear">
    <jsp:include page="txnNavi.jsp">
        <jsp:param name="currPage" value="inventory"/>
    </jsp:include>
    <jsp:include page="inventroyNavi.jsp">
        <jsp:param name="currPage" value="inventoryCheck"/>
        <jsp:param name="isRepairPickingSwitchOn" value="<%=isRepairPickingSwitchOn%>"/>
    </jsp:include>
    <form:form commandName="inventoryCheckDTO" id="inventoryCheckForm" action="inventoryCheck.do?method=saveInventoryCheck"  class="J_leave_page_prompt"
               method="post" name="inventoryCheckForm">
    <input id="orderType" name="orderType" value="inventoryCheckOrder" type="hidden"/>
    <form:hidden path="id" value="${inventoryCheckDTO.id==null?'':inventoryCheckDTO.id}" />
    <jsp:include page="unit.jsp"/>
    <div class="i_mainRight shoppingCart" id="i_mainRight">
        <div class="cartTop"></div>
        <div class="cartBody">
            <form:hidden path="selectSupplier" value="${WEB_VERSION_PRODUCT_THROUGH_SELECT_SUPPLIER}"/>
            <div class="divTit" style="width:170px;">
                单据号
                <span id="receiptNoSpan" class="receiptNoSpan">系统自动生成</span>
                <input type="hidden" value="${inventoryCheckDTO.receiptNo}" id="receiptNo" name="receiptNo">
            </div>

            <div class="divTit" style="width:150px;"><div style="float:left;">盘点人</div><div style="float:left;width:100px;font-weight: normal;margin-left: 3px;text-overflow: ellipsis;white-space: nowrap; overflow:hidden;word-break: break-all;">${inventoryCheckDTO.editor}</div></div>
            <div class="divTit" style="width:210px;">盘点日期<form:input type="text" class="txt" cssStyle="width: 130px;margin-left: 3px" id="inventoryCheckTime" path="editDateStr" value="${inventoryCheckDTO.editDateStr}"/></div>
            <bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.STOREHOUSE">
                <div class="divTit" id="storehouseDiv" style="width:210px;">盘点仓库
                    <form:select path="storehouseId" cssClass="j_checkStoreHouse checkSelectChanged" cssStyle="width:120px;height:21px;border: 1px solid #BBBBBB;" autocomplete="off">
                        <option value="">—请选择仓库—</option>
                        <form:options items="${storeHouseDTOList}" itemValue="id" itemLabel="name"/>
                    </form:select>
                </div>
                <form:hidden path="storehouseName" value="${inventoryCheckDTO.storehouseName}"/>
            </bcgogo:hasPermission>
            <div class="clear i_height" />
            <div>
                <table cellpadding="0" cellspacing="0" class="table2 inventoryCheck_show clear" id="table_productNo">
                    <col width="70"/>
                    <col width="80"/>
                    <col width="80"/>
                    <col width="80"/>
                    <col width="71"/>
                    <col width="50"/>
                    <col width="50"/>
                    <col width="50"/>
                    <col width="50"/>
                    <col width="50"/>
                    <col width="50"/>
                    <col width="50"/>
                    <col width="80"/>
                    <tr class="titleBg title_tb">
                        <td style="border-left:none;">商品编号</td>
                        <td>品名</td>
                        <td>品牌/产地</td>
                        <td>规格</td>
                        <td>型号</td>
                        <td>车辆品牌</td>
                        <td>车型</td>
                        <td>库存均价</td>
                        <td>账面库存</td>
                        <td>实际库存</td>
                        <td>盘点数量</td>
                        <td>盘点金额</td>
                            <%--<td>调整均价</td>--%>
                            <%--<td>调整差额</td>--%>
                            <%--<td style="border-right:none;">操作--%>
                            <%--<img id ="addRowBtn" style=" display:block; float:right; margin:2px 5px 0px 0px;cursor: pointer;" src="images/opera2.png">--%>
                            <%--</td>--%>
                        <td style="border-right:none;padding-left: 18px;">操作<input class="opera2" type="button" style="display:none;"></td>
                    </tr>
                    <tr class="space">
                        <td colspan="15"></td>
                    </tr>
                    <c:forEach items="${inventoryCheckDTO.itemDTOs}" var="itemDTO" varStatus="status">
                        <tr class="bg item">
                            <td style="border-left:none;padding-left: 10px;">
                                <form:input path="itemDTOs[${status.index}].commodityCode"
                                            value="${itemDTO.commodityCode!=null?itemDTO.commodityCode:''}" />
                            </td>

                            <td>
                                <form:input path="itemDTOs[${status.index}].productName"
                                            value="${itemDTO.productName!=null?itemDTO.productName:''}"  />
                                <form:hidden path="itemDTOs[${status.index}].productId"/>
                            </td>

                            <td>
                                <form:input path="itemDTOs[${status.index}].brand" value="${itemDTO.brand!=null?itemDTO.brand:''}"/>
                            </td>
                            <td>
                                <form:input path="itemDTOs[${status.index}].spec" value="${itemDTO.spec!=null?itemDTO.spec:''}"/>
                            </td>
                            <td>
                                <form:input path="itemDTOs[${status.index}].model" value="${itemDTO.model!=null?itemDTO.model:''}" />
                            </td>
                            <td>
                                <form:input path="itemDTOs[${status.index}].vehicleBrand" value="${itemDTO.vehicleBrand!=null?itemDTO.vehicleBrand:''}" maxlength="200"/>
                            </td>
                            <td>
                                <form:input path="itemDTOs[${status.index}].vehicleModel" value="${itemDTO.vehicleModel!=null?itemDTO.vehicleModel:''}" maxlength="200"/>
                            </td>

                                <%--<td>--%>
                                <%--<form:input path="itemDTOs[${status.index}].inventoryAveragePrice"--%>
                                <%--value="${itemDTO.inventoryAveragePrice!=null?itemDTO.inventoryAveragePrice:0}"--%>
                                <%--readonly="true" style="border:none;text-align: center;background-color:transparent;"/>--%>
                                <%--</td>--%>
                                <%--<td>--%>
                                <%--<form:input path="itemDTOs[${status.index}].inventoryAmount"--%>
                                <%--value="${itemDTO.inventoryAmount!=null?itemDTO.inventoryAmount:0}" readonly="true"--%>
                                <%--style="border:none;text-align: center;background-color:transparent;"/>--%>
                                <%--</td>--%>
                                <%--<td align="center">--%>
                                <%--<form:input path="itemDTOs[${status.index}].actualInventoryAmount"--%>
                                <%--value="${itemDTO.actualInventoryAmount!=null?itemDTO.actualInventoryAmount:0}"/>--%>
                                <%--</td>--%>

                            <td>
                                <form:input path="itemDTOs[${status.index}].inventoryAveragePrice"
                                            value="${itemDTO.inventoryAveragePrice!=null?itemDTO.inventoryAveragePrice:''}"
                                            readonly="true" class="text_style" data-filter-zero="true"/>
                            </td>
                            <td>
                                <form:input path="itemDTOs[${status.index}].inventoryAmountUnit" data-filter-zero-advanced="true"
                                            value="${itemDTO.inventoryAmountUnit!=null?itemDTO.inventoryAmountUnit:''}" readonly="true"
                                            class="text_style"/>

                                <form:hidden path="itemDTOs[${status.index}].inventoryAmount"
                                             value="${itemDTO.inventoryAmount!=null?itemDTO.inventoryAmount:''}"
                                             />
                                <form:hidden path="itemDTOs[${status.index}].unit"
                                             value="${itemDTO.unit}"/>
                            </td>
                            <td align="center">
                                <form:input path="itemDTOs[${status.index}].actualInventoryAmount" cssClass="itemAmount"
                                            value="${itemDTO.actualInventoryAmount!=null?itemDTO.actualInventoryAmount:''}" data-filter-zero="true"/>
                            </td>
                            <td>
                                <form:input path="itemDTOs[${status.index}].inventoryAmountAdjustment" data-filter-zero="true"
                                            value="${itemDTO.inventoryAmountAdjustment!=null?itemDTO.inventoryAmountAdjustment:''}"
                                            readonly="true" class="text_style"/>
                            </td>
                            <td>
                                <form:input path="itemDTOs[${status.index}].inventoryAdjustmentPrice"
                                            value="${itemDTO.inventoryAdjustmentPrice!=null?itemDTO.inventoryAdjustmentPrice:''}"
                                            readonly="true" class="text_style" cssStyle="width:120%"/>
                            </td>

                            <td style="text-align:center;">
                                <a id="itemDTOs0.deletebutton" class="opera1" onfocus="this.blur();" >删除</a>
                                <a class="opera2" onfocus="this.blur();">增加</a>
                            </td>
                        </tr>
                    </c:forEach>
                </table>
                <div class="tableInfo">
                    <div class="t_total">
                        合计：
                        <span class="yellow_color" style="display: inline-block;" id="countInventoryAmount" data-filter-zero="true">${inventoryCheckDTO.inventoryAmount}</span>&nbsp;
                        <span class="yellow_color" style="display: inline-block;width: 60px;padding-left:10px;" id="countModifyInventoryAmount" data-filter-zero="true">${inventoryCheckDTO.modifyInventoryAmount}</span>
                        <div style="display: inline-block;width: 190px;text-align: right;">
                            <span id="checkResult" class="yellow_color">0</span>元
                        </div>
                        <form:input type="hidden" path="adjustPriceTotal"  value=""/>
                    </div>
                </div>

                    <%--<table cellpadding="0" cellspacing="0" class="inventoryCheck_total clear">--%>
                    <%--<tr class="title_total">--%>
                    <%--<td colspan="11">--%>
                    <%--<div style="font-weight:bold;">--%>
                    <%--<span style="padding-left: 400px">合计：</span><span id="checkResult" style="padding-left: 400px">0</span>--%>
                    <%--</div>--%>
                    <%--</td>--%>
                    <%--</tr>--%>
                    <%--</table>--%>
            </div>


        </div>
    </div>
    <div class="height"></div>
    <bcgogo:hasPermission permissions="WEB.INVENTORY_PRICE_ADJUSTMENT.PRINT">
        <c:if test="${inventoryCheckDTO.id != null}">
        <div class="height"></div>
        <div class="printBtn" id="print" style="cursor: pointer">
            <img src="images/print.png">
            <div class="sureWords" >打印</div>
        </div>
        </c:if>
    </bcgogo:hasPermission>
    <div class="shopping_btn">
        <div class="divImg" id="saveBtn">
            <img src="images/sureStorage.jpg">
            <div class="sureWords">确认盘点</div>
        </div>
        <div class="divImg" id="backBtn">
            <img src="images/cancel.PNG">
            <div class="sureWords">返回列表</div>
        </div>
    </div>
</div>
</form:form>
<div class="inventoryCheckedImg" id="inventoryCheckedImg"></div>
<div id="mask" style="display:block;position: absolute;">
</div>
<div id="commodityCode_dialog" style="display:none">
    <span id="commodityCode_dialog_msg"></span>
</div>
<%@include file="/WEB-INF/views/footer_html.jsp" %>

</body>
</html>
