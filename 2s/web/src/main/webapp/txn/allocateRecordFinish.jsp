<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>仓库调拨</title>
    <%
        boolean storageBinTag = ServiceManager.getService(IShopConfigService.class).isStorageBinSwitchOn((Long) request.getSession().getAttribute("shopId"));//选配仓位功能 默认开启这个功能false
    %>
    <%@include file="/WEB-INF/views/header_script.jsp" %>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/returnStorage<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/returnStorageFinish<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/allocateRecord<%=ConfigController.getBuildVersion()%>.css"/>
    <c:choose>
        <c:when test="<%=storageBinTag%>">
            <link rel="stylesheet" type="text/css"
                  href="styles/storageBinOn<%=ConfigController.getBuildVersion()%>.css"/>
        </c:when>
        <c:otherwise>
            <link rel="stylesheet" type="text/css"
                  href="styles/storageBinOff<%=ConfigController.getBuildVersion()%>.css"/>
        </c:otherwise>
    </c:choose>
    <style>
        .blue {
            color: blue !important;
        }
    </style>
    <script type="text/javascript" src="js/script<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.js"></script>
    <script type="text/javascript" src="js/messagePrompt<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/utils/tableUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/page/txn/allocateRecord<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">
        defaultStorage.setItem(storageKey.MenuUid, "WEB.TXN.INVENTORY_MANAGE.ALLOCATE_RECORD");
        defaultStorage.setItem(storageKey.MenuCurrentItem,"详情");
    </script>
</head>
<body>
<%@ include file="/WEB-INF/views/header_html.jsp" %>
<%@ include file="/common/messagePrompt.jsp" %>

<div style="display:none" id="errorMsg">${errorMsg}</div>
<div class="i_main">
    <jsp:include page="txnNavi.jsp">
        <jsp:param name="currPage" value="inventory"/>
    </jsp:include>
    <jsp:include page="inventroyNavi.jsp">
        <jsp:param name="currPage" value="allocateRecord"/>
        <jsp:param name="isRepairPickingSwitchOn" value="<%=isRepairPickingSwitchOn%>"/>
    </jsp:include>

    <div class="i_mainRight" id="i_mainRight">
            <input id="orderType" name="orderType" value="allocateRecord" type="hidden"/>
            <input type="hidden" id="basePath" name="bathPath" value="<%=basePath%>"/>
            <input type="hidden" id="id" name="id" value="${allocateRecordDTO.id == null?'': allocateRecordDTO.id}"/>
            <input type="hidden" id="returnType" name="returnType" value="${returnType}"/>
            <input type="hidden" id="updateLackOrderId" name="updateLackOrderId" value="${updateLackOrderId}"/>
            <input type="hidden" id="updateLackOrderType" name="updateLackOrderType" value="${updateLackOrderType}"/>
            <ul class="yinye_title clear">
                <li id="fencount">仓库调拨</li>

                <li class="danju" style="width:170px;">
                    单据号：<strong>${allocateRecordDTO.receiptNo}</strong>
                </li>
            </ul>
            <div class="clear"></div>
            <div class="tuihuo_tb">
                <table>
                    <col width="80"/>
                    <col width="80"/>
                    <tr>
                        <td style="width:150px;">仓库调拨信息</td>
                        <td style="width:200px;">
                            <div style="float:left;width:180px;">
                                调出仓库：${allocateRecordDTO.outStorehouseName}
                            </div>
                        </td>
                        <td style="width:200px;">
                            <div style="float:left;width:180px;">
                                调入仓库：${allocateRecordDTO.inStorehouseName}
                            </div>
                        </td>
                        <td style="width:120px;">
                            <div style="float:left;width:120px;">
                                操作人：${allocateRecordDTO.editor}
                            </div>
                        </td>
                        <td style="width:200px;">
                            调拨日期：${allocateRecordDTO.vestDateStr}
                        </td>
                    </tr>
                </table>

                <table class="clear" id="tb_tui" style="margin-bottom:10px;">
                    <col width="75"/>
                    <col width="75"/>
                    <col width="70"/>
                    <col width="95"/>
                    <col width="80"/>
                    <col width="80"/>
                    <col width="80"/>
                    <col width="80"/>
                    <col width="100"/>
                    <col width="80"/>
                    <tr class="tab_title">
                        <td>商品编号</td>
                        <td>品名</td>
                        <td>品牌/产地</td>
                        <td>规格</td>
                        <td>型号</td>
                        <td>车型</td>
                        <td>车辆品牌</td>
                        <%--<td>调入货位</td>--%>
                        <td>成本</td>
                        <td>调拨数</td>
                        <td>小计</td>
                    </tr>
                    <c:forEach items="${allocateRecordDTO.itemDTOs}" var="itemDTO" varStatus="status">
                        <tr class="item">
                            <td>${itemDTO.commodityCode}</td>
                            <td>${itemDTO.productName}</td>
                            <td>${itemDTO.brand}</td>
                            <td>${itemDTO.spec}</td>
                            <td>${itemDTO.model}</td>
                            <td>${itemDTO.vehicleModel}</td>
                            <td>${itemDTO.vehicleBrand}</td>
                            <%--<td>${itemDTO.inStorageBin}</td>--%>
                            <td>${itemDTO.costPrice==null?"0":itemDTO.costPrice}</td>
                            <td>${itemDTO.amount} ${itemDTO.unit}</td>
                            <td style="color:#FF0000;">${itemDTO.totalCostPrice!=null?itemDTO.totalCostPrice:"0"}</td>
                        </tr>
                    </c:forEach>
                    <tr>
                        <td colspan="8">合计：</td>
                        <td>${allocateRecordDTO.totalAmount == null ? "0":allocateRecordDTO.totalAmount}</td>
                        <td>${allocateRecordDTO.totalCostPrice!=null?allocateRecordDTO.totalCostPrice:"0"}</td>
                    </tr>
                </table>
                <div class="height"></div>
            </div>

            <div class="clear"></div>
            <div class="shopping_btn" style="float:right; clear:right; margin-top:16px;">
                <div class="btn_div_Img" id="return_list_div">
                    <input id="returnListBtn" type="button" class="return_list j_btn_i_operate" onfocus="this.blur();"/>

                    <div class="sureWords">返回列表</div>
                </div>
                <bcgogo:hasPermission permissions="WEB.ALLOCATE_RECORD.PRINT">
                    <div class="btn_div_Img" id="print_div">
                        <input id="printBtn" type="button" class="print j_btn_i_operate" onfocus="this.blur();"/>
                        <input type="hidden" name="print" id="print" value="${allocateRecordDTO.print}">

                        <div class="optWords">打印</div>
                    </div>
                </bcgogo:hasPermission>
            </div>
            <div class="clear"></div>
    </div>

</div>
<div id="mask" style="display:block;position: absolute;"></div>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>