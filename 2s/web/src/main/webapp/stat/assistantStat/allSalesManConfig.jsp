<%@ include file="/WEB-INF/views/includes.jsp" %>
<%--
  User: liuWei
  Date: 12-4-16
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
  <title>员工业绩配置</title>

  <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
  <link rel="stylesheet" type="text/css" href="styles/head<%=ConfigController.getBuildVersion()%>.css"/>

  <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
  <link rel="stylesheet" type="text/css" href="styles/achievementService<%=ConfigController.getBuildVersion()%>.css"/>
  <link rel="stylesheet" type="text/css" href="styles/addCheck<%=ConfigController.getBuildVersion()%>.css"/>


  <%@include file="/WEB-INF/views/header_script.jsp" %>

  <script type="text/javascript"
          src="js/stat/assistantStat/allSalesManConfig<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript"
          src="js/page/admin/userGroupSuggestion<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript">
    defaultStorage.setItem(storageKey.MenuUid,"WEB.STAT.AGENT_ACHIEVEMENTS.CONFIG");
    <bcgogo:permissionParam resourceType="logic" permissions="WEB.VERSION.VEHICLE_CONSTRUCTION,WEB.VERSION.MEMBER_STORED_VALUE">
    APP_BCGOGO.Permission.Version.VehicleConstruction =${WEB_VERSION_VEHICLE_CONSTRUCTION};
    APP_BCGOGO.Permission.Version.MemberStoredValue =${WEB_VERSION_MEMBER_STORED_VALUE};
    </bcgogo:permissionParam>

  </script>
</head>
<body class="bodyMain">
<%@include file="/WEB-INF/views/header_html.jsp" %>

<div class="i_main clear">

    <div class="mainTitles">
    <div class="titleWords">提成配置</div>

        <input id="unConfig" type="hidden" name="unConfig" value="${unConfig}" class="txt"/>
        <input id="pageType" type="hidden" name="pageType" value="allSalesManConfig" class="txt"/>


  </div>
  <div class="titBodys">
    <bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.VEHICLE_CONSTRUCTION">
      <a class="normal_btn" href="assistantStat.do?method=redirectSalesManConfig">施工提成</a>
    </bcgogo:hasPermission>
    <a class="hover_btn" href="assistantStat.do?method=searchSalesManConfig">员工部门</a>
    <a class="normal_btn" href="assistantStat.do?method=redirectProductConfig">销售提成</a>
  </div>


  <div class="group_list all_shu">
    <span style="cursor:pointer;" id="totalNumSpan">员工总数&nbsp;<b class="blue_color">${totalNum}</b>&nbsp;名</span>
    <span style="cursor:pointer;" id="totalConfigNumSpan">未配置部门&nbsp;<b id="totalConfigNum"
                                                                        class="blue_color">${totalShopAchievementConfig}</b>&nbsp;名</span>
    <strong class="fr"><a href="#" onclick="achievementNotify()" class="yellow_color">进入业绩统计></a></strong>

  </div>
  <div class="clear i_height"></div>


  <div class="titBody">

                 <div class="lineTop"></div>

            <div class="lineBody lineAll">
                <div class="divTit"><label for="name">姓名：</label><input name="name" id="name" class="txt"/></div>
      <div class="divTit"><label for="userGroupName">职务：</label>
        <select name="userGroupName" id="userGroupName1" value="${configUserGroupName}"
                userGroupId="${configUserGroupId}" style="width:100px;" class="txt">
          <option value="" id="firstOption1">--请选择--</option>
          <option value="">所有</option>
        </select>
      </div>
      <div class="divTit"><label for="departmentName">部门：</label>
        <select id="departmentName1" name="departmentName" class="txt" style="width:100px;">
          <option value="" id="firstOption2">--请选择--</option>
          <option value="" id="second">所有</option>
        </select>
        <input id="departmentId" type="hidden" class="txt"/></div>

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
      <input id="userStatus" style="display:none"/>

      <div class="divTit search">
        <input type="button" class="btn_so" onfocus="this.blur();" id="searchStaff" value="查询"/></div>
    </div>


            <div class="lineBottom"></div>
            <div class="clear i_height"></div>

  </div>


  <div class="cuSearch" id="totalNumDiv" style="display:block;">
    <div class="cartTop"></div>
    <div class="cartBody">

      <table class="tab_cuSearch" cellpadding="0" cellspacing="0" id="staffTable">
        <col/>
        <col/>
        <col/>
        <col width="200px"/>
        <col width="200px">
        <tr class="titleBg">
          <td style="padding-left:10px;">姓名</td>
          <td>性别</td>
          <td>职务</td>
          <td>在职状态</td>
          <td><input type="button" value="部门" onclick="setAssistantDepartment();" class="ticheng bumen"/></td>
        </tr>

      </table>


      <div class="clear i_height"></div>

      <bcgogo:ajaxPaging url="staffManage.do?method=getStaffByCondition" postFn="drawStaffConfigTable"
                         dynamical="dynamicalStaff" data='{startPageNo:1,maxRows:25}' display="none"/>

    </div>
    <div class="cartBottom"></div>
  </div>

  <div class="cuSearch" id="totalSpanNumDiv" style="display:none;">
    <div class="cartTop"></div>
    <div class="cartBody">

      <table class="tab_cuSearch" cellpadding="0" cellspacing="0" id="staffConfigAchievementTable">
        <col/>
        <col/>
        <col width="200px"/>
        <col width="200px">
        <tr class="titleBg">
          <td style="padding-left:10px;">姓名</td>
          <td>性别</td>
          <td>在职状态</td>
          <td><input type="button" value="部门" onclick="setAssistantDepartment();" class="ticheng bumen"/></td>
        </tr>

      </table>


      <div class="clear i_height"></div>

      <bcgogo:ajaxPaging url="assistantStat.do?method=getAssistantAchievementByPager"
                         postFn="drawStaffConfigAchievementTable"
                         dynamical="dynamicalConfigStaff" data='{startPageNo:1,maxRows:25}' display="none"/>

    </div>
    <div class="cartBottom"></div>
  </div>


  <div class="height"></div>
</div>
</div>



<div id="dispatchDialog" class="alertMain" style="display: none">
  <div class="height"></div>
  <div class="divTit alert_divTit">部门：&nbsp;<select class="txt" style="width:121px;" id="setDepartmentName">
  </select></div>
  <div class="height"></div>
  <div class="button">
    <a class="btnSure" id="confirmBtn">确&nbsp;定</a>
    <a class="btnSure" id="cancelBtn">取&nbsp;消</a>
  </div>
</div>


<div class="height"></div>
<div class="di_an" id="button" style=" margin:0 auto;  width:100px;">
  <a class="button bigButton" onclick="achievementNotify();" href="#">进入业绩统计</a>
</div>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>