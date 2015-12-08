<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>职位权限查询</title>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/cuSearch<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/staffManage<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/messageManage<%=ConfigController.getBuildVersion()%>.css"/>
    <%@include file="/WEB-INF/views/header_script.jsp" %>
    <script type="text/javascript">
        defaultStorage.setItem(storageKey.MenuUid, "WEB.SYSTEM_SETTINGS.STAFF_OCCUPATION_LIST");

        <bcgogo:permissionParam permissions="WEB.SYSTEM_SETTINGS.PERMISSION_UPDATE,WEB.SYSTEM_SETTINGS.PERMISSION_COPY,WEB.SYSTEM_SETTINGS.PERMISSION_DELETE">
        APP_BCGOGO.Permission.SystemSetting.PermissionManager.UpdatePermission = ${WEB_SYSTEM_SETTINGS_PERMISSION_UPDATE};
        APP_BCGOGO.Permission.SystemSetting.PermissionManager.CopyPermission = ${WEB_SYSTEM_SETTINGS_PERMISSION_COPY};
        APP_BCGOGO.Permission.SystemSetting.PermissionManager.DeletePermission = ${WEB_SYSTEM_SETTINGS_PERMISSION_DELETE};
        </bcgogo:permissionParam>
    </script>
    <script type="text/javascript" src="js/utils/tableUtil.js"></script>
    <script type="text/javascript" src="js/page/admin/userGroupsManage<%=ConfigController.getBuildVersion()%>.js"></script>
</head>
<body class="bodyMain">
<%@include file="/WEB-INF/views/header_html.jsp" %>
<div class="i_main clear">
    <jsp:include page="../systemManagerNavi.jsp">
        <jsp:param name="currPage" value="staffManagerNaviMenu"/>
    </jsp:include>
<%--    <div class="mainTitles">
        <div class="titleWords">职位权限列表</div>
    </div>--%>
    <div class="titleList">
        <bcgogo:hasPermission permissions="WEB.SYSTEM_SETTINGS.STAFF_MANAGER">
            <a class="" action-type="menu-click"
               menu-name="WEB.SYSTEM_SETTINGS.STAFF_LIST" href="staffManage.do?method=showStaffManagePage">员工列表</a>
            <a class="click" action-type="menu-click"
               menu-name="WEB.SYSTEM_SETTINGS.STAFF_OCCUPATION_LIST"
               href="navigator.do?method=permissionManager">职位权限列表</a>
        </bcgogo:hasPermission>
    </div>
    </div>
<div class="titBodys">
    <div class="quanHelp blue_col">权限分配帮助</div>
</div>
    <div class="titBody">
         <div class="lineTitle">职位权限搜索</div>
       <div class="lineBody bodys">
        <div class="divTit"><label for="userGroupName">名称：</label><input type="text" class="txt" id="userGroupName"/></div>
    	<div class="divTit" >
            <label for="userGroupVariety">类型：</label>
            <select id="userGroupVariety"  style="width: 100px">
                <option>--请选择---</option>
                <option>所有</option>
                <option>系统默认</option>
                <option>自定义</option>
            </select>
        </div>
        <input type="button"  onfocus="this.blur();" class="btnSo" value="查&nbsp;询" id="searchUserGroup" />
         </div>
        <div class="lineBottom"></div>
        <div class="clear height"></div>
        <div class="lineTitle tabTitle">
          <input type="hidden" id="hiddenCondition">
        	共有职位权限<span class="blue_col" id="all"></span>个&nbsp;&nbsp;系统默认<span class="blue_col" id="countSystemDefault"></span>个&nbsp;&nbsp;自定义<span class="blue_col" id="custom"></span>个
        <bcgogo:hasPermission permissions="WEB.SYSTEM_SETTINGS.PERMISSION_ADD">
           <a onfocus="this.blur();" class="btnPan" id="addUserGroup" style="float:right;">新增权限</a>
        </bcgogo:hasPermission>
        </div>
        <div class="tab" style="width:978px;">
        <table cellpadding="0" cellspacing="0" class="tabSlip" id="userGroupTable">
            <col width="60">
            <col width="180">
            <col width="200">
            <col>
            <col width="150">
            <tr class="divSlip" id="table_title">
                <td style="padding-left:10px;">NO</td>
                <td>职位权限</td>
                <td>类型</td>
                <td>备注</td>
                <td>操作</td>
            </tr>
        </table>

        </div>
        <jsp:include page="/common/pageAJAX.jsp">
            <jsp:param name="url" value="userGroupsManage.do?method=getUserGroupsByCondition"></jsp:param>
            <jsp:param name="data" value="{startPageNo:'1',maxRows:15}"></jsp:param>
            <jsp:param name="dynamical" value="usergroups"></jsp:param>
            <jsp:param name="jsHandleJson" value="drawUserGroupTable"></jsp:param>
        </jsp:include>
    </div>
</div>
<div id="mask"  style="display:block;position: absolute;">
</div>

<iframe id="iframe_PopupBox" style="position:absolute;z-index:5; left:400px; top:400px; display:none;" allowtransparency="true" width="630px" height="450px" frameborder="0" src=""></iframe>
 <!------------------------------弹出框--------------------------------------->

            <div id="upInfo" class="upInfo" style="margin-left:1px; position:relative; z-index:99; display:none;width:980px">
            	<label class="yellow_color" style="padding-left:10px;">该权限用户如下</label>
                <table cellpadding="0" cellspacing="0" class="tabUser" id="tabUserTable">
                <col width="60">
                <col width="80">
                <col width="80">
                <col width="80">
                <col width="40">
                <col width="110">
                <col >
                <col width="110">
                <col width="110">
                <col width="80">
                <col width="80">
                <tr class="bgTit">
                    <td style="padding-left:6px;">No</td>
                    <td>姓名</td>
                    <td>账户名</td>
                    <td>账户状态</td>
                    <td>性别</td>
                    <td>部门</td>
                    <td>权限名称</td>
                    <td>电话</td>
                    <td>入职日期</td>
                    <td>状态</td>
                    <td>操作</td>
                </tr>

                </table>
            </div>

            <div class="height"></div>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>
