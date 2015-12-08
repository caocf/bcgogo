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
  <link rel="stylesheet" type="text/css" href="styles/statistics<%=ConfigController.getBuildVersion()%>.css"/>
  <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>

  <%@include file="/WEB-INF/views/header_script.jsp" %>
  <script type="text/javascript" src="js/extension/jquery/plugin/jquery-ui/ui/jquery.ui.timepicker-addon.min.js"></script>
  <script type="text/javascript" src="js/extension/jquery/plugin/jquery-ui/ui/i18n/jquery.ui.datepicker-zh-CN.min.js"></script>
  <script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.js"></script>
  <script type="text/javascript" src="js/utils/tableUtil<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/repairPickingList<%=ConfigController.getBuildVersion()%>.js"></script>
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
        <input type="hidden" id="orderType" value="repairPickingList">
        <div class="lineTitle">维修领料查询</div>
        <div class="lineBody bodys">

        <form id="thisform" action="pick.do?method=showRepairPickingListPage"
              method="post" name="thisform">

            <input type="hidden"id="pageNo" name="pageNo" value="1"/>
            <div class="divTit">领料单号：<input type="text" id="receiptNo" name="receiptNo" class="txt" value="${searchCondition.receiptNo}" /></div>
            <div class="divTit">&nbsp;&nbsp;&nbsp;&nbsp;关联施工单号：<input type="text" id="repairOrderReceiptNo" name="repairOrderReceiptNo" value="${searchCondition.repairOrderReceiptNo}" class="txt" /></div>
            <div class="divTit">&nbsp;&nbsp;&nbsp;&nbsp;销售人：<input type="text" class="txt choosePickingMan"id="productSeller" name="productSeller"  value="${searchCondition.productSeller}"/></div>

            <bcgogo:hasPermission permissions="WEB.TXN.INVENTORY_MANAGE.ALLOCATE_RECORD" resourceType="menu">
            <div class="divTit">&nbsp;&nbsp;&nbsp;&nbsp;仓库：
                <%--<form:select path="storehouseId" cssClass="selTit" cssStyle="width:100px;height:21px;margin-top: 0px;" autocomplete="off">--%>
                    <%--<form:options items="${storeHouseDTOs}" itemValue="id" itemLabel="name"/>--%>
                <%--</form:select>--%>

                <select id="warehouse" class="selTit"  name="storehouseId" style="width:100px;height:21px;margin-top: 0px;" autocomplete="off">
                    <option selected="selected" value="">--所有--</option>
                    <c:forEach items="${storeHouseDTOs}" var="storeHouseDTO">
                        <option value="${storeHouseDTO.id}" ${searchCondition.storehouseId == storeHouseDTO.id ? "selected":null}>${storeHouseDTO.name}</option>
                    </c:forEach>
                </select>
            </div>
            </bcgogo:hasPermission>
            <div class="divTit">下单时间：<input type="text" class="txt " id="startTimeStr" name="startTimeStr"  value="${searchCondition.startTimeStr}"/>
                至&nbsp;<input type="text" class="txt " id="endTimeStr" name="endTimeStr"  value="${searchCondition.endTimeStr}"/>
            </div>



            <div class="divTit">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;状态：
                <select id="repairStatus" class="selTit" name="selectStatus" style="width:100px;height:21px;margin-top: 0px;" autocomplete="off" onChange="isHavePickingMan();">
                    <option ${searchCondition.selectStatus == 'ALL'?"selected":null} value="ALL">
                        所有状态
                    </option>
                    <option ${searchCondition.selectStatus == 'PENDING'?"selected":null} value="PENDING">
                        待处理状态
                    </option>
                </select>
                &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            </div>

                <%--<c:if test="${searchCondition.selectStatus == 'ALL'}">--%>
                <span id="pickingMan_showOrHidden" ${searchCondition.selectStatus == 'ALL' ?"style='display:block'": "style='display:none'"}>
                    <div class="divTit">领料人：<input type="text" class="txt selectedPickingMan" id="pickingMan" name="pickingMan" value="${searchCondition.pickingMan}"/>
                        &nbsp;&nbsp;&nbsp;&nbsp;</div>
                </span>
                <%--</c:if>--%>

<%--                 <span id="pickingMan_showOrHidden" style="display:none">
                    <div class="divTit">领料人_FORPENDING：<input type="text" class="txt selectedPickingMan" id="pickingMan_forPending" name="pickingMan_forPending" value="${searchCondition.pickingMan}"/>
                        &nbsp;&nbsp;&nbsp;&nbsp;</div>
                </span>--%>

            <input type="button" id="searchBtn" onfocus="this.blur();" class="btn_search" style="float:left;"/>
          <p style="float:left;"> &nbsp;&nbsp; </p>

            <a id="clearCondition" class="blue_color clean" style="float:left; margin-top: 9px;font-size:15px">清空条件</a>


        </form>
            </div>
        <div class="lineBottom"></div>

        <div class="clear i_height"></div>

        <div class="lineTop"></div>
        <div class="lineBody bodys">

        <table style="BORDER-COLLAPSE:collapse">

        <div class="divSlip">
            <div style="width:80px; padding-left:10px;">商品编号</div>
            <div style="width:103px;">品名</div>
            <div style="width:102px;">品牌/产地</div>
            <div style="width:110px;">规格</div>
            <div style="width:108px;">型号</div>
            <div style="width:90px;">车辆品牌</div>
            <div style="width:105px;">车型</div>
            <div style="width:52px;">库存量</div>
            <div style="width:182px;">领料状态</div>

        </div>

        <c:if test="${empty repairPickingDTOs}">
            <br>
            <div style="color: #000000">
                无维修领料记录！
            </div>
        </c:if>

        <c:forEach items="${repairPickingDTOs}" var="repairPickingDTO" varStatus="status">
        <form action="" id="repairPickingDTOs${status.index}.form" method="post">
            <input type="hidden" name="id" value="${repairPickingDTO.id}">
            <input type="hidden" name="toStorehouseId">
        <div class="i_height"></div>
        <table cellpadding="0" cellspacing="0" class="tabSlip" >
            <col width="90">
            <col width="100">
            <col width="100">
            <col width="110">
            <col width="110">
            <col width="100">
            <col width="100">
            <col width="60">
            <col width="170">
            <col width="60">
            <tr class="titleBg titleService">
                <td colspan="10">
                    <div class="titLbl" style="width: 150px" align="center">
                        单号：<a class="showRepairPicking" style="cursor: pointer;"
                              url="pick.do?method=showRepairPicking&repairPickingId=${repairPickingDTO.id}">${repairPickingDTO.receiptNo}</a>
                    </div>
                    <div class="titLbl" style="width: 185px">下单时间：<span >${repairPickingDTO.vestDateStr}</span></div>
                    <div class="titLbl" style="width: 115px" >销售人：<span>${repairPickingDTO.productSeller}</span></div>
                    <%--<div class="titLbl" style="width: 125px" >领料人：<span>${repairPickingDTO.pickingMan}</span></div>--%>
                    <%--<div class="titLbl" style="width: 96px">车牌：<span>${repairPickingDTO.vehicle}</span></div>--%>
                    <bcgogo:hasPermission permissions="WEB.TXN.INVENTORY_MANAGE.ALLOCATE_RECORD" resourceType="menu">
                    <div class="titLbl" style="width: 135px">领料仓库：<span title="${repairPickingDTO.storehouseName}">${repairPickingDTO.storehouseName}</span></div>
                    </bcgogo:hasPermission>
                    <div class="titLbl">
                        关联施工单号：
                        <a class="showRepair" style="cursor: pointer;"
                           url="txn.do?method=getRepairOrder&menu-uid=VEHICLE_CONSTRUCTION_REPAIR&repairOrderId=${repairPickingDTO.repairOrderId}">${repairPickingDTO.repairOrderReceiptNo}
                        </a>
                    </div>
                    <c:choose>
                        <c:when test="${status.index == 0}">
                            <a class="down" id="clickShow">收拢</a>
                        </c:when>
                        <c:otherwise>
                            <a class="up" id="clickShow">更多</a>
                        </c:otherwise>
                    </c:choose>

                    <c:if test="${repairPickingDTO.isCanReturnStorage}">
                        <a class="btns returnStorage" id="repairPickingDTOs${status.index}.returnStorage">退&nbsp;料</a>
                    </c:if>
                    <bcgogo:hasPermission permissions="WEB.REPAIR_PICKING_OUT_STORAGE">
                        <c:if test="${repairPickingDTO.isCanOutStorage}">
                            <a class="btns outStorage" id="repairPickingDTOs${status.index}.outStorage">出&nbsp;库</a>
                        </c:if>
                    </bcgogo:hasPermission>
                </td>
            </tr>
            <c:forEach items="${repairPickingDTO.pendingItemDTOs}" var="pendingItemDTO" varStatus="itemStatus">
            <tr ${status.index >0 ? "style=\"display:none\"":null}>
                <td style="padding-left:10px;" title="${pendingItemDTO.commodityCode}">${pendingItemDTO.commodityCode}</td>
                <td title="${pendingItemDTO.productName}">${pendingItemDTO.productName}</td>
                <td title="${pendingItemDTO.brand}">${pendingItemDTO.brand}</td>
                <td title="${pendingItemDTO.spec}">${pendingItemDTO.spec}</td>
                <td title="${pendingItemDTO.model}">${pendingItemDTO.model}</td>
                <td title="${pendingItemDTO.vehicleBrand}">${pendingItemDTO.vehicleBrand}</td>
                <td title="${pendingItemDTO.vehicleModel}">${pendingItemDTO.vehicleModel}</td>
                <td title="${pendingItemDTO.inventoryAmount}${pendingItemDTO.unit}">${pendingItemDTO.inventoryAmount}${pendingItemDTO.unit}</td>
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
                <td>
                    <input  class="checkStringChanged textbox J-bcgogo-droplist-on chooseRepairPickingMan" type="text"
                            value="${pendingItemDTO.defaultPickingMan}" readonly="true" style="width: 40px;"
                            id="repairPickingDTOs${status.index}.pendingItemDTOs${itemStatus.index}.pickingMan"
                           name="pendingItemDTOs[${itemStatus.index}].pickingMan" autocomplete="off" title="${pendingItemDTO.defaultPickingMan}" >
                    <input type="hidden" id="repairPickingDTOs${status.index}.pendingItemDTOs${itemStatus.index}.pickingManId"
                           name="pendingItemDTOs[${itemStatus.index}].pickingManId" >
                    <input type="hidden" id="repairPickingDTOs${status.index}.pendingItemDTOs${itemStatus.index}.id"
                           name="pendingItemDTOs[${itemStatus.index}].id" value="${pendingItemDTO.id}" >
                    <input type="hidden" id="repairPickingDTOs${status.index}.pendingItemDTOs${itemStatus.index}.status"
                           name="pendingItemDTOs[${itemStatus.index}].status" value="${pendingItemDTO.status}" >
                </td>
            </tr>
            </c:forEach>
            <c:forEach items="${repairPickingDTO.handledItemDTOs}" var="handledItemDTO" varStatus="itemStatus">
            <tr ${status.index >0 ? "style=\"display:none\"":null}>
                <td style="padding-left:10px;" title="${handledItemDTO.commodityCode}">${handledItemDTO.commodityCode}</td>
                <td title="${handledItemDTO.productName}">${handledItemDTO.productName}</td>
                <td title="${handledItemDTO.brand}">${handledItemDTO.brand}</td>
                <td title="${handledItemDTO.spec}">${handledItemDTO.spec}</td>
                <td title="${handledItemDTO.model}">${handledItemDTO.model}</td>
                <td title="${handledItemDTO.vehicleBrand}">${handledItemDTO.vehicleBrand}</td>
                <td title="${handledItemDTO.vehicleModel}">${handledItemDTO.vehicleModel}</td>
                <td title="${handledItemDTO.inventoryAmount}${handledItemDTO.unit}">${handledItemDTO.inventoryAmount}${handledItemDTO.unit}</td>
                <td title="${handledItemDTO.pickingDetail}">${handledItemDTO.pickingDetail}</td>
                <td title="${handledItemDTO.pickingMan}">${handledItemDTO.pickingMan}</td>
            </tr>
            </c:forEach>
        </table>
        <div class="slipInfo">
            共<span>${fn:length(repairPickingDTO.handledItemDTOs) + fn:length(repairPickingDTO.pendingItemDTOs)}</span>条记录&nbsp;&nbsp;待处理<span>${fn:length(repairPickingDTO.pendingItemDTOs)}</span>条记录
        </div>
        </form>
        </c:forEach>


        </table>

        <div >
            <jsp:include page="/common/paging.jsp">
                <jsp:param name="url" value="pick.do?method=showRepairPickingListPage"/>
                <jsp:param name="submit" value="thisform"/>
            </jsp:include>
            <div class="clear"></div>
        </div>


    </div>
        <div class="lineBottom"></div>
</div>

<div id="mask"  style="display:block;position: absolute;">
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