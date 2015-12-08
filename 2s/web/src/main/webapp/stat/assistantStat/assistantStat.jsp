<%@ include file="/WEB-INF/views/includes.jsp" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%--
  User: liuWei
  Date: 12-4-16
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
  <title>业绩统计</title>

  <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
  <link rel="stylesheet" type="text/css" href="styles/head<%=ConfigController.getBuildVersion()%>.css"/>

  <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
  <link rel="stylesheet" type="text/css" href="styles/assistantStat<%=ConfigController.getBuildVersion()%>.css"/>

  <%@include file="/WEB-INF/views/header_script.jsp" %>
    <script type="text/javascript" src="js/extension/jquery/plugin/Highcharts-2.3.2/js/highcharts.js"></script>
  <script type="text/javascript" src="js/extension/jquery/plugin/Highcharts-2.3.2/js/modules/exporting.js"></script>
  <script type="text/javascript"
          src="js/stat/assistantStat/assistantStat<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript"
            src="js/stat/assistantStat/assistantStatUtil<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/utils/stringUtil<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript">
    defaultStorage.setItem(storageKey.MenuUid,"WEB.STAT.AGENT_ACHIEVEMENTS.STAT");
    <bcgogo:permissionParam resourceType="logic" permissions="WEB.VERSION.VEHICLE_CONSTRUCTION,WEB.VERSION.MEMBER_STORED_VALUE">
    APP_BCGOGO.Permission.Version.VehicleConstruction =${WEB_VERSION_VEHICLE_CONSTRUCTION};
    APP_BCGOGO.Permission.Version.MemberStoredValue =${WEB_VERSION_MEMBER_STORED_VALUE};
    </bcgogo:permissionParam>

    <bcgogo:permissionParam resourceType="render" permissions="WEB.VEHICLE_CONSTRUCTION.WASH_BEAUTY.BASE">
    APP_BCGOGO.Permission.Stat.BusinessStat.Business.WashCar = ${WEB_VEHICLE_CONSTRUCTION_WASH_BEAUTY_BASE};
    </bcgogo:permissionParam>

    <bcgogo:permissionParam resourceType="render" permissions="WEB.TXN.SALE_MANAGE.SALE">
    APP_BCGOGO.Permission.Txn.SaleManage.Sale = ${WEB_TXN_SALE_MANAGE_SALE};
    </bcgogo:permissionParam>

    <bcgogo:permissionParam resourceType="render" permissions="WEB.STAT.NONOPERATING_ACCOUNT.BASE">
    APP_BCGOGO.Permission.Stat.NonOperatingAccount.Update = ${WEB_STAT_NONOPERATING_ACCOUNT_BASE};
    </bcgogo:permissionParam>

  </script>
</head>
<body class="bodyMain">
<%@include file="/WEB-INF/views/header_html.jsp" %>


<div class="i_main clear">
<%--<div class="group_list listStyle" style="margin:10px 0px;">--%>
  <%--<span style="font-weight:bold;">员工提成统计</span>--%>
<%--</div>--%>

   <div class="mainTitles">
      <div class="titleWords">员工业绩统计</div>

   	</div>
 <input type="hidden" id="pageContent" value="assistantStat" />

 <input type="hidden" id="startYearHidden" value="${startYearHidden}" />
 <input type="hidden" id="startMonthHidden" value="${startMonthHidden}" />
 <input type="hidden" id="endYearHidden" value="${endYearHidden}" />
 <input type="hidden" id="endMonthHidden" value="${endMonthHidden}" />
 <input type="hidden" id="achievementStatTypeHidden" value="${achievementStatTypeHidden}" />
 <input type="hidden" id="startPageNoHiddenHidden" value="${startPageNoHiddenHidden}" />
 <input type="hidden" id="achievementCalculateWayHidden" value="${achievementCalculateWayHidden}" />
 <input type="hidden" id="assistantOrDepartmentIdStrHidden" value="${assistantOrDepartmentIdStrHidden}" />
 <input type="hidden" id="achievementOrderTypeStrHidden" value="${achievementOrderTypeStrHidden}"/>
 <input type="hidden" id="serviceIdStrHidden" value="${serviceIdStrHidden}"/>

<div class="titBody">
  <div class="lineTop"></div>
  <div class="lineBody lineAll">
    <div class="txt_Search">
      <label>统计日期：</label>
          <div class="divTit">
        <a class="btnList" id="lastMonth" name="my_date_select">上月</a>&nbsp;
        <a class="btnList" id="thisMonth" name="my_date_select">本月</a>&nbsp;
        <a class="btnList" id="thisYear" name="my_date_select">本年</a>&nbsp;
        <a class="btnList" id="selfDefine" name="my_date_select">自定义</a>&nbsp;
          </div>
        <select id="startYear" style="width:55px;">
        <option>2012</option>
        <option>2013</option>
        <option>2014</option>
        <option>2015</option>
      </select>
      <label>年</label>
      <select id="startMonth" style="width:40px; ">
        <option>1</option>
        <option>2</option>
        <option>3</option>
        <option>4</option>
        <option>5</option>
        <option>6</option>
        <option>7</option>
        <option>8</option>
        <option>9</option>
        <option>10</option>
        <option>11</option>
        <option>12</option>
      </select>
      <label>月</label>

      <label>到</label>
      <select id="endYear" style="width:55px;margin-left:10px;">
        <option>2012</option>
        <option>2013</option>
        <option>2014</option>
        <option>2015</option>
      </select>
      <label>年</label>

      <select id="endMonth" style="width:40px;clear:right">
        <option>1</option>
        <option>2</option>
        <option>3</option>
        <option>4</option>
        <option>5</option>
        <option>6</option>
        <option>7</option>
        <option>8</option>
        <option>9</option>
        <option>10</option>
        <option>11</option>
        <option>12</option>
      </select>
      <label>月</label>

      <label style="margin-left: 30px;">统计方式：</label>
      <select id="achievementCalculateWayStr">
        <option value="CALCULATE_BY_DETAIL">按详细提成设置</option>
        <option VALUE="CALCULATE_BY_ASSISTANT">按员工提成设置</option>
      </select>
    </div>

    <div class="clear i_height"></div>
    <div class="txt_Search">
      <label>部门/员工：</label>
      <select id="achievementStatType" name="select" style="width:65px;margin-left:10px;">
        <option value="ASSISTANT">按员工</option>
        <option value="DEPARTMENT">按部门</option>
      </select>
      <select id="assistantOrDepartmentIdStr" name="select" style="width:65px;margin-left:10px;">
        <option value="">请选择</option>
      </select>
      <label style="margin-left: 30px;">分类：</label>
      <select id="achievementOrderTypeStr" name="select" style="margin-left:10px;">
        <option value="ALL">全部</option>

        <bcgogo:hasPermission permissions="WEB.VEHICLE_CONSTRUCTION.WASH_BEAUTY.BASE">
          <option value="WASH_BEAUTY">洗车美容</option>
        </bcgogo:hasPermission>

        <bcgogo:hasPermission permissions="WEB.VERSION.VEHICLE_CONSTRUCTION">
          <option value="REPAIR_SERVICE">施工</option>
        </bcgogo:hasPermission>

        <bcgogo:hasPermission permissions="WEB.VERSION.MEMBER_STORED_VALUE">
          <option value="MEMBER">会员卡销售</option>
        </bcgogo:hasPermission>

        <bcgogo:hasPermission permissions="WEB.TXN.SALE_MANAGE.SALE" resourceType="menu">
          <option value="SALES">商品销售</option>
        </bcgogo:hasPermission>

        <bcgogo:hasPermission permissions="WEB.STAT.NONOPERATING_ACCOUNT.BASE">
          <option value="BUSINESS_ACCOUNT">营业外记账</option>
        </bcgogo:hasPermission>

      </select>
      <select id="serviceIdStr" name="select" style="width:65px;margin-left:10px;">
        <option value="">请选择</option>
      </select>
      <input style="margin-left: 30px;" id="queryAssistantStat" class="btn_so" type="button" value="查询">
     </div>
  </div>
  <div class="lineBottom"></div>
  <div class="clear i_height"></div>
</div>



<div class="cuSearch" id="queryByDepartmentDiv">
  <div class="cartTop"></div>
  <div class="cartBody">
    <div id="scrollDiv" style="overflow-x:scroll; width:981px;">
    <table cellspacing="0" cellpadding="0" class="cuTable"  style="width:120%" id="departmentStatTable">
    </table>
    </div>
    <br class="clear"/>
    <div class="clear i_height"></div>
    <jsp:include page="/common/pageAJAX.jsp">
      <jsp:param name="url" value="assistantStat.do?method=getAssistantStatByPage"></jsp:param>
      <jsp:param name="jsHandleJson" value="initAchievementStatTable"></jsp:param>
      <jsp:param name="dynamical" value="dynamicalAssistantDepartStat"></jsp:param>
      <jsp:param name="data" value="{startPageNo:'1',maxRows:15,achievementStatTypeStr:'DEPARTMENT'}"></jsp:param>
      <jsp:param name="display" value="none"></jsp:param>
    </jsp:include>
    <input type="hidden" value="${maxRows}" id="totalRows"/>
  </div>
  <br class="clear"/>
</div>

  <span id="noDataSpan" style="text-align: center;color: #272727;display: none;font-weight:bold;">未找到相关数据！</span>

  <div class="cartBottom"></div>
</div>
<div class="height"></div>


<div class="divTit" id="button" style=" margin:0 auto;  width:100px;">
  <a class="button" id="printButton">打&nbsp;印</a>
</div>


<div class="divImg" id="exportButton">
    <img src="images/print.png">
    <div class="sureWords" style="width:auto;">导出</div>
</div>


<img id="exporting" style="margin-left: 30px; display: none;" title="正在导出" alt="正在导出" src="images/loadinglit.gif">
<div class="height"></div>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>