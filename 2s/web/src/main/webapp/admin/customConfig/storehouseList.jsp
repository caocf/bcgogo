<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>仓库管理</title>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/staffManage<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/remindSet<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/storehouse<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
    <%@include file="/WEB-INF/views/header_script.jsp" %>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.js"></script>
    <script type="text/javascript" src="js/utils/tableUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/page/admin/storehouse<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">
        defaultStorage.setItem(storageKey.MenuUid,"WEB.SYSTEM_SETTINGS.STORE_MANAGE");
    </script>
</head>
<body class="bodyMain">
<%@include file="/WEB-INF/views/header_html.jsp" %>
<%@ include file="/common/messagePrompt.jsp" %>
<div class="i_main clear">
    <div class="mainTitles">
        <jsp:include page="customConfigNav.jsp">
            <jsp:param name="currPage" value="storehouseManager"/>
        </jsp:include>
    </div>

    <div class="titBody">
        <h3 class="h3_title"></h3>
        <div class="height"></div>
        <div class="wordTitle">
            共 <span class="pageCount" id="storeHouseDataCount">0</span> 条记录
            <input type="button" value="新增仓库" class="addNew" id="addStoreHouse"  />
        </div>
        <div class="clear"></div>
        <table id="storehouseTable" cellpadding="0" cellspacing="0" class="tabSlip tabPick">
            <col width="80">
            <col width="170">
            <col width="250">
            <col width="160">
            <col width="70">
            <tr class="divSlip">
                <td style="padding-left:10px;">NO</td>
                <td>仓库名称</td>
                <td>仓库地址</td>
                <td>备注</td>
                <td>操作</td>
            </tr>
        </table>
        <div class="height"></div>
        <div>
            <jsp:include page="/common/pageAJAX.jsp">
                <jsp:param name="url" value="storehouse.do?method=getStoreHouseList"></jsp:param>
                <jsp:param name="data" value="{startPageNo:1,maxRows:15}"></jsp:param>
                <jsp:param name="jsHandleJson" value="drawStoreHouseTable"></jsp:param>
                <jsp:param name="dynamical" value="dynamical1"></jsp:param>
            </jsp:include>
        </div>

    </div>
</div>
</div>

<div class="tab_repay i_supplierInfo" id="storeHouseDialog" style="display:none">
    <div class="i_addStoreHouse_body">
        <form id="storehouseForm" action="storehouse.do?method=saveStoreHouse">
            <input type="hidden" id="id" name="id"/>
            <table cellpadding="0" cellspacing="0" class="supplierTable " style="width: 280px">
                <col width="80"/>
                <col/>
                <tr>
                    <td>仓库名称 <span style="color: red">*</span>：</td>
                    <td><input type="text" style="height:19px;width:150px;" id="name" name="name" maxlength="10"/></td>
                </tr>
                <tr>
                    <td>仓库地址：</td>
                    <td><input type="text" style="height:19px;width:150px;" id="address" maxlength="150" name="address"/></td>
                </tr>
                <tr>
                    <td style="vertical-align:top;">备　　注：</td>
                    <td><textarea style="width:150px;" name="memo" id="memo" maxlength="350"></textarea></td>
                </tr>
                <tr>
                    <td style="text-align: center" colspan="2">
                        <input class="btn hover" id="saveStoreHouseBtn" type="button" onfocus="this.blur();" value="确  定">
                        <input class="btn" type="button" id="closeBtn" onfocus="this.blur();" value="取  消"></td>
                </tr>
            </table>
        </form>
    </div>
</div>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>