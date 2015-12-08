<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>系统管理——权限管理——权限配置</title>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/staffManage<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/messageManage<%=ConfigController.getBuildVersion()%>.css"/>
    <%@include file="/WEB-INF/views/header_script.jsp" %>
    <script type="text/javascript" src="js/page/admin/permissionConfig<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/page/admin/userGroupSuggestion<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">
        defaultStorage.setItem(storageKey.MenuUid, "WEB.SYSTEM_SETTINGS.STAFF_OCCUPATION_LIST");
        defaultStorage.setItem(storageKey.MenuCurrentItem,"新增");
    </script>
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
        <a class=""  action-type="menu-click"
           menu-name="WEB.SYSTEM_SETTINGS.STAFF_LIST"  href="staffManage.do?method=showStaffManagePage">员工列表</a>
        <bcgogo:hasPermission permissions="WEB.SYSTEM_SETTINGS.PERMISSION_ADD">
            <a class="click"  action-type="menu-click"
               menu-name="WEB.SYSTEM_SETTINGS.STAFF_OCCUPATION_LIST"  href="navigator.do?method=permissionManager">职位权限列表</a>
        </bcgogo:hasPermission>
    </div>
</div>
    <div class="titBodys">
      <div class="quanHelp blue_col">权限分配帮助</div>
    </div>
    <input type="hidden" value="${userGroupDTO.id}" id="userGroupId">
    <input type="hidden" value="${userGroupDTO.userGroupNo}" id="userGroupNo">
    <input type="hidden" value="${defaultSysUserGroup}" id="defaultSysUserGroup">
    <input type="hidden" value="${copyUserGroupId}" id="copyUserGroupId">
    <input type="hidden" value="${copyPermission}" id="copyPermission">
    <div class="titBody">
        <h3 style="color:#272727; line-height:22px; padding-left:6px;" id="pageTitle">新增权限</h3>
    	<div class="titComp">
        	<div class="left"></div>
            <div class="body">
            	<h3 class="blue_color">第一步：请输入权限名称与备注信息</h3>
            </div>
            <div class="right"></div>
        </div>
        <div class="i_height clear"></div>
        <div class="divTit">&nbsp;&nbsp;<span class="red_color">*</span>权限名称&nbsp;<input type="text" class="txt" style="width:130px;" id="userGroupName" maxlength="15" value="${userGroupDTO.name}"/></div>
        <div class="divTit" id="copyDiv"><input type="checkbox" id="copy"/>复制已有权限&nbsp;<select class="txt" style="width:130px;" id="userGroup5" disabled="true"><option>--请选择--</option></select></div>
        <div class="divTit">权限说明&nbsp;<input type="text" class="txt" style="width:439px;" maxlength="100" id="userGroupMemo" initialValue="请输入备注信息:" value="${userGroupDTO.memo}"/></div>
        <div class="i_height clear"></div>
        <div class="titComp">
        	<div class="left"></div>
            <div class="body">
            	<h3 class="blue_color">第二步：请勾选所需权限</h3>
            </div>
            <div class="right"></div>
        </div>

        <div class="mainBody">
        	<div class="top"></div>
            <div class="body">
            <div class="i_height"></div>
            <div class="box bgOne" id="chks1"></div>
            <div class="box bgTwo" id="chks2"></div>
            <div class="box bgThree" id="chks3"></div>
            <div class="box bgThree" id="chks4"></div>
            <div class="box bgThree" id="chks5"></div>
            <div class="box bgThree" id="chks6"></div>
            <div class="box bgThree" id="chks7"></div>
            <div class="box bgThree" id="chks8"></div>
            <div class="box bgThree" id="chks9"></div>
            <div class="box bgThree" id="chks10"></div>
            <div class="box bgThree" id="chks11"></div>
            <div class="box bgThree" id="chks12"></div>
            </div>
        </div>
        <div class="height"></div>
        <div class="wordTitle">
            <input class="addNew" type="button" value="取&nbsp;消" id="cancel">
            <c:if test="${defaultSysUserGroup!='true'}">
                <input class="addNew" type="button" value="重&nbsp;置" id="reset">
                <input class="addNew" type="button" value="确认添加职位" id="saveUserGroup">
            </c:if>
        </div>
    </div>
</div>
<div id="mask" style="display:block;position: absolute;"></div>
<iframe id="iframe_PopupBox" style="position:absolute;z-index:5; left:400px; top:1300px; display:none;" allowtransparency="true" width="830px" height="670px" frameborder="0" src=""></iframe>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>
