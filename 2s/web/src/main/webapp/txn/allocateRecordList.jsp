<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>仓库调拨</title>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
    <%@include file="/WEB-INF/views/header_script.jsp" %>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.js"></script>
    <script type="text/javascript" src="js/utils/tableUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/page/txn/allocateRecordList<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">
        defaultStorage.setItem(storageKey.MenuUid, "WEB.TXN.INVENTORY_MANAGE.ALLOCATE_RECORD");
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
    <div class="titBody">
        <div class="allocation">
            <div class="allocation_left"></div>
            <form action="allocateRecord.do?method=getAllocateRecordList" id="allocateRecordSearchForm" name="allocateRecordSearchForm" method="post">
                <div class="allocation_center">
                    <div class="divTit">调拨单号：<input type="text" class="txt" name="receiptNo" id="receiptNo" style="text-transform: uppercase;"/></div>
                    <div class="divTit" style="width: 200px">调出仓库：
                        <select class="selTit" id="outStorehouseId" name="outStorehouseId" style="width:120px;height:21px;" autocomplete="off">
                            <option value="">——所有——</option>
                            <c:forEach var="storehouseDTO" items="${storeHouseDTOList}">
                                <option value="${storehouseDTO.id}">${storehouseDTO.name}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="divTit">调入仓库：
                        <select class="selTit" id="inStorehouseId" name="inStorehouseId" style="width:120px;height:21px;" autocomplete="off">
                            <option value="">——所有——</option>
                            <c:forEach var="storehouseDTO" items="${storeHouseDTOList}">
                                <option value="${storehouseDTO.id}">${storehouseDTO.name}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="divTit">操作人：
                        <input type="text" class="txt" id="editor" name="editor" value="${editor}" autocomplete="off"/>
                        <input type="hidden" id="editorId" name="editorId" autocomplete="off"/>
                        <img id="deleteAcceptor" style="width: 12px; cursor: pointer; display: none;"
                             src="images/list_close.png">
                    </div>
                    <div class="divTit" style="clear:both;">调拨日期：
                        <input id="startDate" type="text" class="txt" value="${startDateStr}" name="startDateStr"/>
                        &nbsp;至&nbsp;
                        <input id="endDate" type="text" class="txt" value="${endDateStr}" name="endDateStr"/>
                    </div>
                    <div class="divTit" style="width: 200px">生成类型：
                        <select class="selTit" id="generateType" name="generateType" style="height:21px;width: 120px;" autocomplete="off">
                            <option value="">——所有——</option>
                            <option value="USER">自主生成</option>
                            <option value="SYSTEM">系统生成</option>
                        </select>
                    </div>
                    <div class="divTit search"><input class="btn_so" type="submit" value="查询"></div>
                </div>
            </form>
            <div class="allocation_right"></div>
        </div>

        <div class="wordTitle">
            共有 <span class="pageCount" id="allocateRecordDataCount">0</span> 条调拨记录
            <bcgogo:hasPermission permissions="WEB.ALLOCATE_RECORD.ADD">
                <input type="button" value="新增仓库调拨" class="addNew" id="addAllocateRecordBtn"/>
            </bcgogo:hasPermission>
        </div>
        <div class="clear"></div>

        <table id="allocateRecordTable" cellpadding="0" cellspacing="0" class="tabSlip tabPick">
            <col width="30">
            <col width="120">
            <col width="150">
            <col width="150">
            <col width="130">
            <col width="130">
            <col width="100">
            <col width="100">
            <tr class="table-row-title">
                <th class="first-padding">NO</th>
                <th>单据号</th>
                <th>调出仓库<input id="sortOutStorehouseName" class="ascending j_sort" type="button" onfocus="this.blur();" sortField="outStorehouseName"></th>
                <th>调入仓库<input id="sortInStorehouseName" class="ascending j_sort" type="button" onfocus="this.blur();" sortField="inStorehouseName"></th>
                <th>调拨数量合计<input id="sortTotalAmount" class="ascending j_sort" type="button" onfocus="this.blur();" sortField="totalAmount"></th>
                <th>调拨成本合计<input id="sortTotalCostPrice" class="ascending j_sort" type="button" onfocus="this.blur();" sortField="totalCostPrice"></th>
                <th>操作人<input id="sortEditor" class="ascending j_sort" type="button" onfocus="this.blur();" sortField="editor"></th>
                <th>调拨日期<input id="sortVestDate" class="descending j_sort" type="button" onfocus="this.blur();" sortField="vestDate"></th>
                <th class="last-padding">生成类型</th>
            </tr>
        </table>
        <div class="height"></div>
        <!----------------------------分页----------------------------------->
        <div>
            <jsp:include page="/common/pageAJAX.jsp">
                <jsp:param name="url" value="allocateRecord.do?method=getAllocateRecordList"></jsp:param>
                <jsp:param name="data" value="{startPageNo:1,maxRows:15,startDateStr:$('#startDate').val(),endDateStr:$('#endDate').val(),editor:$('#editor').val()}"></jsp:param>
                <jsp:param name="jsHandleJson" value="drawAllocateRecordTable"></jsp:param>
                <jsp:param name="dynamical" value="dynamical1"></jsp:param>
            </jsp:include>
        </div>
    </div>
</div>
<div id="mask" style="display:block;position: absolute;">
</div>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>