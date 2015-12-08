<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>内部领料</title>

    <link rel="stylesheet" type="text/css" href="styles/up<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/addPlan<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/moreHistory<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/statistics<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/addWarehouse<%=ConfigController.getBuildVersion()%>.css">

    <%@include file="/WEB-INF/views/header_script.jsp" %>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.js"></script>
    <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/utils/tableUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/pickingCommon<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/innerPickingList<%=ConfigController.getBuildVersion()%>.js"></script>


</head>
<body class="bodyMain">
<%@include file="/WEB-INF/views/header_html.jsp" %>

<div class="i_main clear">
    <jsp:include page="txnNavi.jsp">
         <jsp:param name="currPage" value="inventory"/>
     </jsp:include>
     <jsp:include page="inventroyNavi.jsp">
         <jsp:param name="currPage" value="innerPicking"/>
         <jsp:param name="isRepairPickingSwitchOn" value="<%=isRepairPickingSwitchOn%>"/>
     </jsp:include>
    <div class="titBody ">
        <form:form action="pick.do?method=showInnerPickingListPage" id="thisform" name="thisform" method="post" commandName="searchCondition">
            <input type="hidden"id="pageNo" name="pageNo" value="1"/>
            <input type="hidden"id="sortStatus" name="sortStatus" value="${searchCondition.sortStatus}"/>
            <div class="allocation">
                <div class="allocation_left"></div>
                <div class="allocation_center">
                    <div class="divTit">领料单号：<form:input path="receiptNo" class="txt"/></div>
                    <bcgogo:hasPermission permissions="WEB.TXN.INVENTORY_MANAGE.ALLOCATE_RECORD" resourceType="menu">
                        <div class="divTit">领料仓库：
                            <form:select path="storehouseId" cssClass="j_checkStoreHouse"
                                         cssStyle="width:120px;height:21px;border: 1px solid #BBBBBB;" autocomplete="off">
                                <option value="">所有</option>
                                <form:options items="${storeHouseDTOs}" itemValue="id" itemLabel="name"/>
                            </form:select>
                        </div>
                    </bcgogo:hasPermission>
                    <div class="divTit">领料人：
                        <form:input path="pickingMan" cssClass="txt choosePickingMan"/>
                    </div>
                    <div class="divTit">操作人：<form:input path="operationMan" cssClass="txt choosePickingMan"/></div>
                    <div class="divTit">领料日期：<form:input path="startTimeStr" readonly="true" cssClass="txt"/>&nbsp;至&nbsp;
                        <form:input path="endTimeStr" readonly="true" cssClass="txt"/></div>
                    <div class="divTit search"><input class="btn_so" type="button" id="searchBtn" value="查询"></div>
                </div>
                <div class="allocation_right"></div>
            </div>
        </form:form>
        <div class="wordTitle">
            共有<span>${totalAmount}</span>条内部领料记录
            <bcgogo:hasPermission permissions="WEB.INNER_PICKING.ADD">
                <input type="button" value="新增内部领料" class="addNew" id="addNewInnerPicking"/>
            </bcgogo:hasPermission>
        </div>
        <div class="clear"></div>

        <table cellpadding="0" cellspacing="0" class="tabSlip tabPick">
            <col width="110">
            <col width="140">
            <bcgogo:hasPermission permissions="WEB.TXN.INVENTORY_MANAGE.ALLOCATE_RECORD" resourceType="menu">
            <col width="140">
            </bcgogo:hasPermission>
            <col width="130">
            <col width="140">
            <col width="150">
            <col width="100">
            <col width="110">
            <tr class="divSlip">
                <th>NO</th>
                <th>单据号</th>
                <bcgogo:hasPermission permissions="WEB.TXN.INVENTORY_MANAGE.ALLOCATE_RECORD" resourceType="menu">
                    <th>领料仓库<input id="sortStorehouseName" class="ascending jsp_sort" type="button" onfocus="this.blur();" sortField="storehouseName"></th>
                </bcgogo:hasPermission>
                <th>领料人<input id="sortPickingMan" class="ascending jsp_sort" type="button" onfocus="this.blur();" sortField="pickingMan"></th>
                <th>领料数量合计</th>
                <th>领料成本合计<input id="sortTotal" class="ascending jsp_sort" type="button" onfocus="this.blur();" sortField="total"></th>
                <th>操作人<input id="sortOperationMan" class="ascending jsp_sort" type="button" onfocus="this.blur();" sortField="operationMan"></th>
                <th>领料日期<input id="sortVestDate" class="descending jsp_sort" type="button" onfocus="this.blur();" sortField="vestDate"></th>
            </tr>
            <c:forEach items="${innerPickingDTOs}" var="innerPickingDTO" varStatus="status">
                <tr style="cursor: pointer;text-align: center;">
                    <td style="padding-left:10px;">${status.index+1}</td>
                    <td><a class="blue_col showInnerPicking" url="pick.do?method=showInnerPicking&innerPickingId=${innerPickingDTO.id}">${innerPickingDTO.receiptNo}</a></td>
                    <bcgogo:hasPermission permissions="WEB.TXN.INVENTORY_MANAGE.ALLOCATE_RECORD" resourceType="menu">
                    <td>${innerPickingDTO.storehouseName}</td>
                    </bcgogo:hasPermission>
                    <td>${innerPickingDTO.pickingMan}</td>
                    <td>${innerPickingDTO.totalAmount}</td>
                    <td>${innerPickingDTO.total}</td>
                    <td>${innerPickingDTO.operationMan}</td>
                    <td>${innerPickingDTO.vestDateStr}</td>
                </tr>
            </c:forEach>
        </table>
        <c:if test="${empty innerPickingDTOs}">
            <br>
            <div style="color: #000000">
                无内部领料记录！
            </div>
        </c:if>
        <div class="height"></div>
        </table>
        <div>
            <jsp:include page="/common/paging.jsp">
                <jsp:param name="url" value="pick.do?method=showInnerPickingListPage"/>
                <jsp:param name="submit" value="thisform"/>
            </jsp:include>
            <div class="clear"></div>
        </div>
    </div>
</div>

<div id="mask" style="display:block;position: absolute;">
</div>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>