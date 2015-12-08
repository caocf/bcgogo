<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>员工列表</title>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/cuSearch<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/moreHistory<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/messageManage<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/staffManage<%=ConfigController.getBuildVersion()%>.css"/>

    <style type="text/css">
        .allocatedAccountLabel{ display:block; }
        .allocatedAccountInput { margin-bottom:12px; width:95%; padding: .4em; display:block;}
        #dialog-form form fieldset { padding:0; border:0; margin-top:5px; }
    </style>
    <%@include file="/WEB-INF/views/header_script.jsp" %>
    <script type="text/javascript">

        defaultStorage.setItem(storageKey.MenuUid, "WEB.SYSTEM_SETTINGS.STAFF_LIST");
        <bcgogo:permissionParam permissions="WEB.SYSTEM_SETTINGS.RESET_STAFF_PASSWORD,WEB.SYSTEM_SETTINGS.ENABLE_DISABLE_STAFF,WEB.SYSTEM_SETTINGS.STAFF_DELETE,WEB.SYSTEM_SETTINGS.STAFF_UPDATE,WEB.SYSTEM_SETTINGS.STAFF_ALLOCATED_ACCOUNT,WEB.SYSTEM_SETTINGS.STAFF_ADD">
            APP_BCGOGO.Permission.SystemSetting.StaffManager.ResetStaffPassword = ${WEB_SYSTEM_SETTINGS_RESET_STAFF_PASSWORD};
            APP_BCGOGO.Permission.SystemSetting.StaffManager.EnableDisableStaff = ${WEB_SYSTEM_SETTINGS_ENABLE_DISABLE_STAFF};
            APP_BCGOGO.Permission.SystemSetting.StaffManager.DeleteStaff = ${WEB_SYSTEM_SETTINGS_STAFF_DELETE};
            APP_BCGOGO.Permission.SystemSetting.StaffManager.UpdateStaff = ${WEB_SYSTEM_SETTINGS_STAFF_UPDATE};
            APP_BCGOGO.Permission.SystemSetting.StaffManager.AllocatedAccountStaff = ${WEB_SYSTEM_SETTINGS_STAFF_ALLOCATED_ACCOUNT};
            APP_BCGOGO.Permission.SystemSetting.StaffManager.AddStaff = ${WEB_SYSTEM_SETTINGS_STAFF_ADD};
        </bcgogo:permissionParam>

        <bcgogo:permissionParam resourceType="logic" permissions="WEB.VERSION.VEHICLE_CONSTRUCTION,WEB.VERSION.MEMBER_STORED_VALUE">
        APP_BCGOGO.Permission.Version.VehicleConstruction = ${WEB_VERSION_VEHICLE_CONSTRUCTION};
        APP_BCGOGO.Permission.Version.MemberStoredValue = ${WEB_VERSION_VEHICLE_CONSTRUCTION};
        </bcgogo:permissionParam>

    </script>
    <script type="text/javascript" src="js/utils/tableUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/module/bcgogo-IDCardValidate<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/page/admin/staffList<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/page/admin/department<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/utils/stringUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/page/admin/userGroupSuggestion<%=ConfigController.getBuildVersion()%>.js"></script>
</head>
<body class="bodyMain">
<%@include file="/WEB-INF/views/header_html.jsp" %>
<div class="i_main clear">
    <jsp:include page="../systemManagerNavi.jsp">
        <jsp:param name="currPage" value="staffManagerNaviMenu"/>
    </jsp:include>
<%--    <div class="mainTitles">
        <div class="titleWords">员工列表</div>
    </div>--%>
    <bcgogo:hasPermission permissions="WEB.SYSTEM_SETTINGS.PERMISSION_MANGER">
        <div class="titleList">
            <a class="click" action-type="menu-click"
               menu-name="WEB.SYSTEM_SETTINGS.STAFF_LIST" href="staffManage.do?method=showStaffManagePage">员工列表</a>

            <a class="" action-type="menu-click"
               menu-name="WEB.SYSTEM_SETTINGS.STAFF_OCCUPATION_LIST"
               href="navigator.do?method=permissionManager">职位权限列表</a>
        </div>
    </bcgogo:hasPermission>
    </div>
<div class="titBodys">
    <div class="quanHelp blue_col">权限分配帮助</div>
</div>
    <div class="titBody">
        <div class="lineTitle">员工搜索</div>
        <div class="lineBody bodys">
        <div class="divTit"><label for="name">姓名：</label><input name="name" id="name"  class="txt"/></div>
        <div class="divTit"><label for="userGroupName">职务：</label>
          <select name="userGroupName" id="userGroupName1" value="${configUserGroupName}" userGroupId="${configUserGroupId}" style="width:100px;" class="txt">
            <option value="" id="firstOption1">--请选择--</option>
            <option value="">所有</option>
          </select>
        </div>
        <div class="divTit"><label for="departmentName">部门：</label>
        <select id="departmentName1" name="departmentName" class="txt" style="width:100px;">
            <option value="" id="firstOption2">--请选择--</option>
            <option value="" id="second">所有</option>
        </select>
        <input id="departmentId" type="hidden" class="txt" /></div>

        <div class="divTit">
            <label for="status">在职状态：</label>
            <select id="status" style="width: 100px" class="txt">
                <option id="firstOption3">--请选择---</option>
                <option>所有</option>
                <option>在职</option>
                <option>离职</option>
                <option>试用</option>
            </select>
        </div>
        <div class="divTit">
            <label for="sex">性别：</label>
             <select id="sex" style="width: 100px" class="txt">
                <option>所有</option>
                <option>男</option>
                <option>女</option>
            </select>
        </div>
        <input id="userStatus" style="display:none" />
        <div class="divTit">
        <input type="button" onfocus="this.blur();" class="btn_search" id="searchStaff"  style="margin-top: 6px;"/></div>
        </div>
        <div class="lineBottom"></div>
        <div class="clear height"></div>
        <div class="lineTitle tabTitle">
        	共有员工<span class="blue_col" id="all">0</span>名&nbsp;&nbsp;账号已启用<span class="blue_col" id="active">0</span>名&nbsp;&nbsp;账号已禁用<span class="blue_col" id="inActive">0</span>名
        <bcgogo:hasPermission permissions="WEB.SYSTEM_SETTINGS.STAFF_ADD">
            <a onfocus="this.blur();" class="btnPan" id="addNewStaff"/>新增员工</a>
        </bcgogo:hasPermission>
        </div>
        <div class="tab" style="width:981px;">
        <table cellpadding="0" cellspacing="0" class="tabSlip" id="staffTable">
            <col width="60">
            <col width="60">
            <col width="80">
            <col width="80">
            <col width="50">
            <col width="100">
            <col>
            <col width="100">
            <col width="100">
            <col width="90">
            <col width="150">
            <tr class="divSlip" id="table_title">
              <input type="hidden" id="sortStr"/>
                <td style="padding-left:10px;">NO</td>
              <td>姓名<a class="ascending" id="nameSort"></a></td>
              <td>账户名<a class="ascending" id="userNoSort"></a></td>
              <td>账户状态</td>
              <td>性别</td>
              <td>部门</td>
              <td>职位权限</td>
              <td>电话</td>
              <td>入职日期</td>
              <td>在职状态</td>
              <td style="text-align: center">操作</td>
            </tr>
        </table>

            <div class="height"></div>
        </div>
        <bcgogo:ajaxPaging url="staffManage.do?method=getStaffByCondition" postFn="drawStaffTable"
                           dynamical="_staff_manage" data='{startPageNo:1,maxRows:15}'/>
      </div>

</div>
<div id="mask" style="display:block;position: absolute;">
</div>
<%--<div id="dialog-form" title="分配账户">--%>
    <%--<form>--%>
        <%--<fieldset>--%>
            <%--<label for="userNo" class="allocatedAccountLabel">账户名</label>--%>
            <%--<input type="text" name="name" id="userNo" class="text" style="width:155px"/><br/><br/>--%>
            <%--<label for="userGroup" class="allocatedAccountLabel">职位权限</label>--%>
            <%--<select style="width:160px;" class="text" id="userGroup3">--%>
            <%--</select>--%>
            <%--<input type="hidden" name="name" id="salesManId"/>--%>
            <%--<input type="hidden" name="name" id="userGroupId"/>--%>
            <%--&lt;%&ndash;<label for="password">密码</label>&ndash;%&gt;--%>
            <%--&lt;%&ndash;<input type="password" name="password" id="password" value="" class="text ui-widget-content ui-corner-all" />&ndash;%&gt;--%>
        <%--</fieldset>--%>
    <%--</form>--%>
<%--</div>--%>
<input type="hidden" name="name" id="salesManId"/>
<input type="hidden" name="name" id="userGroupId"/>
<input type="hidden" name="name" id="userId"/>
<input type="hidden" name="name" id="userNo"/>
<iframe id="iframe_PopupBox" style="position:absolute;z-index:5; left:400px; top:200px; display:none;" allowtransparency="true" width="830px" height="670px" frameborder="0" src="">

</iframe>
<!------------------------------弹出框--------------------------------------->

  <div id="upInfo" class="upInfo" style="margin-left:1px; position:relative; z-index:99; display:none;width:980px">
      <table cellpadding="0" cellspacing="0" class="tabInfo">
      <col width="70">
      <col width="110">
      <col width="80">
      <col width="120">
      <col width="60">
      <col width="110">
      <col width="80">
      <col width="140">
      <col width="60">
      <col width="120">
      <tr><td><h4>基本信息</h4></td></tr>
      <tr>
          <td align="right">姓&nbsp;名<span class="red_color">*</span>：</td>
          <td>
            <input type="hidden" id="id"/>
            <input type="text" class="txt" id="salesManName" name="name" maxlength="6"/>
          </td>
          <td align="right">手机号码<span class="red_color">*</span>：</td>
          <td><input type="text" class="txt" id="mobile" name="mobile" maxlength="20"/></td>
          <td align="right">部&nbsp;门：</td>
          <td>
            <select class="txt" style="width:111px;" id="department" name="departmentName"></select>
            <input type="hidden" id="departmentId2" />
          </td>
          <td align="right">身份证号：</td>
          <td><input type="text" class="txt" id="identityCard" name="identityCard"/></td>
          <td align="right">QQ：</td>
          <td><input type="text" class="txt" id="qq" name="qq"/></td>
      </tr>
      <tr>
          <td align="right">工&nbsp;号 ：</td>
          <td><input type="text" class="txt" id="salesManCode" name="salesManCode"/></td>
          <td align="right">基本工资 ：</td>
          <td><input type="text" class="txt" id="salary" name="salary"/></td>
          <td align="right">津&nbsp;贴：</td>
          <td><input type="text" class="txt" id="allowance" name="allowance"/></td>
          <td align="right">入职日期：</td>
          <td><input type="text" class="txt" id="careerDate" name="careerDateStr"/></td>
          <td align="right">Email：</td>
          <td><input type="text" class="txt" id="email" name="email"/></td>
      </tr>
      <tr>
        <td align="right">性&nbsp;别 ：</td>
          <td style="text-align:left"><label class="lblRad"><input type="radio" id="male" name="sexStr" value="MALE"/>男</label>&nbsp;<label class="lblRad"><input type="radio" id="female" name="sexStr" value="FEMALE" />女</label></td>
        <td align="right">状&nbsp;态：</td>
          <td colspan="2" style="text-align: left;"><label class="lblRad"><input type="radio" name="statusValue" id="INSERVICE" value="INSERVICE"/>在职</label>&nbsp;<label class="lblRad"><input type="radio" name="statusValue" id="DEMISSION" value="DEMISSION"/>离职</label>&nbsp;<label class="lblRad"><input type="radio" name="statusValue" id="ONTRIAL" value="ONTRIAL"/>试用</label></td>
      </tr>
      <tr><td><h4>账户信息</h4></td></tr>
      <tr id="userInfo">
        <td align="right">用户名<span class="red_color">*</span>：</td>
          <td><input type="text" class="txt" id="userNo2" name="userNo"/></td>
          <td align="right">职位权限：</td>
          <td>
            <select class="txt" style="width:120px;" id="userGroup" name="userGroupName">

            </select>
            <input type="hidden" id="userGroup2" />
          </td>
          <td align="right">备注：</td>
          <td colspan="5"><input type="text" class="txt" style="width:98%;" id="memo" name="memo"/></td>
      </tr>

      </table>
      <table cellpadding="0" cellspacing="0" class="tabInfo">
        <col width="130">
        <col width="110">
        <col width="120">
        <col>
        <col width="130">
        <col>
        <tr><td><h4>员工业绩配置信息</h4></td></tr>
        <tr>

          <bcgogo:hasPermission permissions="WEB.VEHICLE_CONSTRUCTION.WASH_BEAUTY.BASE">
            <td align="right">洗车（元/次）：</td>
            <td><input type="text" id="washBeautyAchievement" name="washBeautyAchievement" class="txt" style="width:96%;"/></td>
          </bcgogo:hasPermission>


          <bcgogo:hasPermission resourceType="menu" permissions="WEB.VEHICLE_CONSTRUCTION.BASE">
            <td align="right">施工（百分比%）：</td>
            <td><input type="text" class="txt" id="serviceAchievement" name="serviceAchievement" style="width:96%;"/>
            </td>
          </bcgogo:hasPermission>

          <bcgogo:hasPermission permissions="WEB.TXN.SALE_MANAGE.SALE" resourceType="menu">
            <td align="right">销售（百分比%）：</td>
            <td><input type="text" id="salesAchievement" name="salesAchievement" class="txt" style="width:96%;"/></td>
          </bcgogo:hasPermission>

        </tr>
        <tr>
          <td align="right">销售利润（百分比%）：</td>
          <td><input type="text" id="salesProfitAchievement" name="salesProfitAchievement" style="width:96%;" class="txt"/></td>


        <bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.MEMBER_STORED_VALUE">
          <td align="right">会员购卡：</td>
          <td><select id="memberNewType" name="memberNewType"
                      style="width:105px; height:22px; float:left; margin-right:10px;">
            <option value="CARD_AMOUNT">按销售量</option>
            <option value="CARD_TOTAL">按销售额</option>
          </select>
            <input id="memberNewAchievement" name="memberNewAchievement" type="text" style="width:120px;"/>
          </td>

          <td align="right">会员续卡：</td>
          <td><select id="memberRenewType" name="memberRenewType"
                      style="width:105px; height:22px; float:left; margin-right:10px;">
            <option value="CARD_AMOUNT">按销售量</option>
            <option value="CARD_TOTAL">按销售额</option>
          </select>
            <input id="memberReNewAchievement" name="memberReNewAchievement" type="text" style="width:120px;"/>
          </td>
        </bcgogo:hasPermission>

        </tr>

        <tr>
          <td colspan="10" class="btnClick" style="text-align:center;">
            <input type="button" value="保&nbsp;存" class="btnClick" onfocus="this.blur();" id="updateSalesMan"/>&nbsp;&nbsp;
            <input type="button" value="重&nbsp;置" class="btnClick" onfocus="this.blur();" id="resetButtion"/>
          </td>
        </tr>
      </table>
  </div>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>
