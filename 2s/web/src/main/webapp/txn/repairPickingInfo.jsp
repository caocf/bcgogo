<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>进销存——库存管理——维修领料</title>

    <link rel="stylesheet" type="text/css" href="styles/up<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/addPlan<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/moreHistory<%=ConfigController.getBuildVersion()%>.css"/>
    <%--<link rel="stylesheet" type="text/css" href="styles/newTodo<%=ConfigController.getBuildVersion()%>.css"/>--%>
    <link rel="stylesheet" type="text/css" href="styles/statistics<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/addWarehouse<%=ConfigController.getBuildVersion()%>.css">

    <%@include file="/WEB-INF/views/header_script.jsp" %>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery-ui/ui/jquery.ui.timepicker-addon.min.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery-ui/ui/i18n/jquery.ui.datepicker-zh-CN.min.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.js"></script>
    <script type="text/javascript" src="js/utils/tableUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/repairPickingInfo<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/pickingCommon<%=ConfigController.getBuildVersion()%>.js"></script>

</head>
<body class="bodyMain">
<%@include file="/WEB-INF/views/header_html.jsp" %>

<div class="i_main clear">
    <jsp:include page="txnNavi.jsp">
        <jsp:param name="currPage" value="inventory"/>
    </jsp:include>
    <jsp:include page="inventroyNavi.jsp">
        <jsp:param name="currPage" value="repairPicking"/>
        <jsp:param name="isRepairPickingSwitchOn" value="<%=isRepairPickingSwitchOn%>"/>
    </jsp:include>
<div class="titBody ">
    <input type="hidden" id="orderType" value="repairPickingInfo">
    <form id="repairPickingForm" action="" method="post" style="float: left;">
        <input type="hidden" id="id" name = "id" value="${repairPickingDTO.id}">
        <input type="hidden" id="toStorehouseId" name = "toStorehouseId" value="">
        <c:if test="${!empty result}">
        <input type="hidden" id="repairPickingMessage" value="${result.msg}" resultDate="${result.data}"
               resultOperation="${result.operation}">
        </c:if>
    <div class="wordTitle">
        单据号：<span>${repairPickingDTO.receiptNo}</span>&nbsp;&nbsp;单据时间：<span>${repairPickingDTO.vestDateStr}</span>
    </div>
    <table cellpadding="0" cellspacing="0" class="tabDan">
        <tr>
            <td class="tabBg">车牌号</td>
            <td>${repairPickingDTO.vehicle}</td>
            <td class="tabBg">客户名</td>
            <td>${repairPickingDTO.customer}</td>
            <td class="tabBg">销售人</td>
            <td>${repairPickingDTO.productSeller}</td>
            <td class="tabBg">关联施工单号</td>
            <td>${repairPickingDTO.repairOrderReceiptNo}</td>
        </tr>
    </table>
    <div class="clear i_height"></div>
    <div class="wordTitle">
        <bcgogo:permission>
          <bcgogo:if permissions="WEB.TXN.INVENTORY_MANAGE.ALLOCATE_RECORD" resourceType="menu">
              材料单&nbsp;&nbsp;领料仓库：<span>${repairPickingDTO.storehouseName}</span><a class="lookRecord" href="javascript:getHandledItemDetail()">查看领/退料流水记录</a>
          </bcgogo:if>
          <bcgogo:else>
              材料单<a class="lookRecord" href="javascript:getHandledItemDetail()">查看领/退料流水记录</a>
          </bcgogo:else>
        </bcgogo:permission>
    </div>
    <div class="clear"></div>
    <div class="divSlip">
        <div style="width:85px; padding-left:10px;">商品编号</div>
        <div style="width:120px;">品名</div>
        <div style="width:113px;">品牌/产地</div>
        <div style="width:109px;">规格</div>
        <div style="width:93px;">型号</div>
        <div style="width:50px;">货位</div>
        <div style="width:70px;">库存量</div>
        <div style="width:175px;">领料详细</div>
        <div style="width:56px;">操作人</div>
        <div style="width:65px;">领/退料人</div>
        <div style="width:45px;"><label><input type="checkbox" class="chks" id="checkAll"/>操作</label></div>
    </div>
    <div class="i_height"></div>
    <table cellpadding="0" cellspacing="0" class="tabSlip">
        <col width="95">
        <col width="120">
        <col width="110">
        <col width="110">
        <col width="85">
        <col width="65">
        <col width="60">
        <col width="180">
        <col width="55">
        <col width="65">
        <col width="55">
        <c:forEach items="${repairPickingDTO.handledItemDTOs}" var="handledItemDTO" varStatus="status">
            <tr>
                <td style="padding-left:10px;">${handledItemDTO.commodityCode}</td>
                <td>${handledItemDTO.productName}</td>
                <td>${handledItemDTO.brand}</td>
                <td>${handledItemDTO.spec}</td>
                <td>${handledItemDTO.model}</td>
                <td>${handledItemDTO.storageBin}</td>
                <td>${handledItemDTO.inventoryAmount}${handledItemDTO.unit}</td>
                <td title="${handledItemDTO.pickingDetail}">${handledItemDTO.pickingDetail}</td>
                <td title="${handledItemDTO.operationMan}">${handledItemDTO.operationMan}</td>
                <td title="${handledItemDTO.pickingMan}">${handledItemDTO.pickingMan}</td>
                <td align="center">——</td>
            </tr>
        </c:forEach>
    </table>
    <div class="i_height"></div>
    <c:if test="${!empty repairPickingDTO.pendingItemDTOs}">
    <div class="wordTitle">待处理材料</div>
    <table cellpadding="0" cellspacing="0" class="tabSlip" id="tab_slip">
        <col width="95">
        <col width="120">
        <col width="110">
        <col width="110">
        <col width="85">
        <col width="65">
        <col width="60">
        <col width="180">
        <col width="55">
        <col width="65">
        <col width="55">
        <c:forEach items="${repairPickingDTO.pendingItemDTOs}" var="pendingItemDTO" varStatus="status">
            <tr>
                <td style="padding-left:10px;">${pendingItemDTO.commodityCode}</td>
                <td>${pendingItemDTO.productName}</td>
                <td>${pendingItemDTO.brand}</td>
                <td>${pendingItemDTO.spec}</td>
                <td>${pendingItemDTO.model}</td>
                <td>${pendingItemDTO.storageBin}</td>
                <td> ${pendingItemDTO.inventoryAmount}${pendingItemDTO.unit}</td>
                <td>
                    <c:choose>
                        <c:when test="${pendingItemDTO.status eq 'WAIT_OUT_STORAGE'}">
                            <label class="green_color">${pendingItemDTO.pickingDetail}</label>
                            <c:if test="${pendingItemDTO.isLack}">
                                <label class="red_color repairPickingLack" style="cursor: pointer; padding-left: 2px"
                                       repairPickingId="${repairPickingDTO.id}" storehouseId ="${repairPickingDTO.storehouseId}">缺料</label>
                            </c:if>
                        </c:when>
                        <c:when test="${pendingItemDTO.status eq 'WAIT_RETURN_STORAGE'}">
                            <label class="red_color">${pendingItemDTO.pickingDetail}</label>
                        </c:when>
                    </c:choose>
                </td>
                <td></td>
                <td>
                    <%--<c:if test="${!pendingItemDTO.isLack}">--%>
                        <input class="checkStringChanged textbox J-bcgogo-droplist-on chooseRepairPickingMan"
                               type="text" autocomplete="off" name="pendingItemDTOs[${status.index}].pickingMan"
                               id="pendingItemDTOs${status.index}.pickingMan" style="width: 40px;" readonly="true"
                               value="${pendingItemDTO.defaultPickingMan}">
                        <input type="hidden" name="pendingItemDTOs[${status.index}].pickingManId"
                               id="pendingItemDTOs${status.index}.pickingManId">
                    <%--</c:if>--%>
                </td>
                <td>
                    <c:if test="${ pendingItemDTO.status == 'WAIT_OUT_STORAGE'}">
                    <label class="green_color">
                    <input type="checkbox" class="chks chks_item" id="pendingItemDTOs${status.index}.id"
                             name="pendingItemDTOs[${status.index}].id" value="${pendingItemDTO.id}"/>出库</label>
                    </c:if>
                    <c:if test="${pendingItemDTO.status == 'WAIT_RETURN_STORAGE'}">
                    <label class="red_color">
                    <input type="checkbox" class="chks chks_item" id="pendingItemDTOs${status.index}.id"
                             name="pendingItemDTOs[${status.index}].id" value="${pendingItemDTO.id}"/>退料</label>
                    </c:if>
                </td>
            </tr>
        </c:forEach>
    </table>
    </c:if>
</div>
</form>
<div class="height"></div>

<div class="shopping_btn">
    <c:if test="${!empty repairPickingDTO.pendingItemDTOs}">
        <div class="btn_div_Img">
            <input id="out_return_btn" class="sureInventory j_btn_i_operate" type="button" onfocus="this.blur();">
            <div class="sureWords">确&nbsp;定</div>
        </div>
    </c:if>
    <bcgogo:hasPermission permissions="WEB.REPAIR_PICKING.PRINT">
        <div class="btn_div_Img">
            <input id="print_repairPicking_Btn" class="print j_btn_i_operate" type="button" onfocus="this.blur();">
            <div class="sureWords">打&nbsp;印</div>
        </div>
    </bcgogo:hasPermission>
</div>
</div>
</div>
</div>

<div id="mask" style="display:block;position: absolute;">
</div>
<div id="handledItem_dialog" style="display:none;">
    <div  class="tab_record" style="display:block;">
            <div class="titBody" id="handledItem_dialog_div">
                <c:if test="${empty repairPickingDTO.handledItemDTOMap}">
                    <span style="color: #000000">无商品领/退料流水记录！</span>
                </c:if>
                <c:forEach items="${repairPickingDTO.handledItemDTOMap}" var="handledItemDTOMap">
                    <div class="wordTitle">
                        操作时间：<span>${handledItemDTOMap.value[0].operationDateStr}</span>
                        &nbsp;&nbsp;操作人：<span>${handledItemDTOMap.value[0].operationMan}</span>
                    </div>
                    <table cellpadding="0" cellspacing="0" class="tabDan handledItemDTO_tb">
                        <col width="300"/>
                        <col width="120"/>
                        <col width="60"/>
                        <tr class="tabBg">
                            <td>商品信息</td>
                            <td>领/退料记录</td>
                            <td>领/退料人</td>
                        </tr>
                        <c:forEach items="${handledItemDTOMap.value}" var="handledItemDTO" varStatus="index">
                            <tr>
                                <td>${handledItemDTO.productDetail}</td>
                                <td>
                                    <c:if test="${handledItemDTO.status eq 'OUT_STORAGE'}">
                                        <label>出库 ${handledItemDTO.amount}</label>
                                    </c:if>
                                    <c:if test="${handledItemDTO.status eq 'RETURN_STORAGE'}">
                                        <label>退料 ${handledItemDTO.amount}</label>
                                    </c:if>
                                </td>
                                <td>${handledItemDTO.pickingMan}</td>
                            </tr>
                        </c:forEach>
                    </table>
                    <div class="height"></div>
                </c:forEach>
            </div>
        </div>
    </div>
</div>
<div id="allocate_or_purchase_div" style="display:none">
    <div>当前单据缺料商品在其他仓库有库存，是否调拨？</div>
</div>
<bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.STOREHOUSE">
    <jsp:include page="../txn/common/selectStoreHouse.jsp"/>
</bcgogo:hasPermission>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>