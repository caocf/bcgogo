<%--
  Created by IntelliJ IDEA.
  User: ndong
  Date: 13-3-6
  Time: 上午6:00
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>外部借调</title>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/borrowOrder<%=ConfigController.getBuildVersion()%>.css"/>
    <%@include file="/WEB-INF/views/header_script.jsp" %>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.js"></script>
    <script type="text/javascript" src="js/suggestion<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/utils/tableUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/stockManager/borrowOrder<%=ConfigController.getBuildVersion()%>.js"></script>


</head>
<body class="bodyMain">
<%@include file="/WEB-INF/views/header_html.jsp" %>

<div class="i_main clear">
    <jsp:include page="../txnNavi.jsp">
        <jsp:param name="currPage" value="inventory"/>
    </jsp:include>
    <jsp:include page="../inventroyNavi.jsp">
        <jsp:param name="currPage" value="borrowOrder"/>
        <jsp:param name="isRepairPickingSwitchOn" value="<%=isRepairPickingSwitchOn%>"/>
    </jsp:include>
    <div class="titBody ">
        <div class="lineTitle">借调单搜索</div>
        <div class="allocation">
            <div class="allocation_left"></div>
            <div class="allocation_center" style="background-position:0px -1px;">
                <div class="divTit">借调单号：<input id="receiptNo" type="text" class="txt" /></div>
                <c:if test="${isHaveStoreHouse}">
                    <div class="divTit">调出仓库：
                        <select id="storehouseId" style="width:128px;">
                            <option value="">--所有--</option>
                            <c:forEach items="${storeHouseDTOs}" var="storeHouseDTO" varStatus="status">
                                <option value="${storeHouseDTO.idStr}">${storeHouseDTO.name}</option>
                            </c:forEach>
                        </select>
                    </div>
                </c:if>
                <div class="divTit">借调者类型：<select id="borrowerType" class="txt"><option value="">--所有--</option><option value="customer">客户</option><option value="supplier">供应商</option></select></div>
                <div class="divTit">借调者：<input id="borrower" type="text" class="borrower txt" /></div>
                <div class="divTit">操作人：<input id="operator" type="text" class="operator txt" /></div>
                <div class="divTit">归还状态：<select id="returnStatus" class="txt" style="width:108px;">
                    <option value="">--所有--</option>
                    <option value="RETURN_NONE">未归还</option>
                    <option value="RETURN_PARTLY">部分归还</option>
                    <option value="RETURN_ALL">全部归还</option>
                </select>
                </div>
                <div class="divTit">借调日期：
                    <input id="startTimeInput" type="text" class="txt" value="" style="width:120px" readonly="readonly"/>&nbsp;至&nbsp;<input id="endTimeInput" type="text" class="txt" value="" style="width:120px"  readonly="readonly"/>
                </div>
                <div class="divTit search"><input class="btn_so" type="button" id="searchBtn"></div>
            </div>
            <div class="allocation_right"></div>
        </div>
        <div class="lineTitle tabTitle">
            共有<span id="allBorrowOrderSize" class="blue_color"></span>条借调记录&nbsp;&nbsp;其中未归还<span id="RETURN_NONE_SIZE" returnStatus="RETURN_NONE" class="blue_color"></span>条&nbsp;
            部分归还<span id="RETURN_PARTLY_SIZE" returnStatus="RETURN_PARTLY" class="blue_color"></span>条&nbsp;全部归还<span id="RETURN_ALL_SIZE" returnStatus="RETURN_ALL" class="blue_color"></span>条
           <bcgogo:hasPermission permissions="WEB.BORROW_ORDER.ADD">
                <a class="btnPan" onfocus="this.blur();" id="addBorrowOrder">新增借调</a>
           </bcgogo:hasPermission>
        </div>
        <div class="tab">
            <table cellpadding="0" cellspacing="0" class="tabSlip" id="borrowOrderTable">
                <col width="80">
                <col width="140">
                <c:if test="${isHaveStoreHouse}">
                    <col width="140">
                </c:if>
                <col width="140">
                <col width="140">
                <col>
                <col width="130">
                <col width="100">
                <tr class="titleBg">
                    <td  style="padding-left:10px;">NO</td>
                    <td>单据号</td>
                    <c:if test="${isHaveStoreHouse}">
                        <td>调出仓库<a class="ascending"></a></td>
                    </c:if>
                    <td>借调者<a class="descending"></a></td>
                    <td>借调成本总计<a class="ascending"></a></td>
                    <td>操作人<a class="ascending"></a></td>
                    <td>借调日期<a class="ascending"></a></td>
                    <td>归还状态</td>
                </tr>
            </table>
        </div>
        <div class="height"></div>
        <!--分页-->
        <div class="hidePageAJAX" style="float:left">
            <jsp:include page="/common/pageAJAX.jsp">
                <jsp:param name="url" value="borrow.do?method=getBorrowOrders"></jsp:param>
                <jsp:param name="data" value="{startPageNo:1,pageSize:15}"></jsp:param>
                <jsp:param name="jsHandleJson" value="initBorrowOrderList"></jsp:param>
                <jsp:param name="hide" value="hideComp"></jsp:param>
                <jsp:param name="dynamical" value="dynamical1"></jsp:param>
            </jsp:include>
        </div>
        <div class="height"></div>
    </div>
</div>

</div>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>