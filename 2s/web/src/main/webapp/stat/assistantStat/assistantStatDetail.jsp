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
  <title>
    员工业绩明细统计表
  </title>


  <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
  <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
  <link rel="stylesheet" type="text/css" href="styles/statistics<%=ConfigController.getBuildVersion()%>.css"/>

  <style>
    .contentTitle a:hover {
      color: #FF6000;
    }

    .cuSearch .tab_cuSearch tr.titBody_Bg td {
      vertical-align: top;
      padding:6px 6px 5px 0px;
      word-break: break-all;
      word-wrap: break-word;
      color: #000000;
    }


  </style>


  <%@include file="/WEB-INF/views/header_script.jsp" %>

  <script type="text/javascript"
          src="js/stat/assistantStat/assistantRecord<%=ConfigController.getBuildVersion()%>.js"></script>

  <script type="text/javascript"
          src="js/stat/assistantStat/assistantStatDetail<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/exportExcel<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript">
    defaultStorage.setItem(storageKey.MenuUid, "WEB.STAT.AGENT_ACHIEVEMENTS.STAT");
  </script>

  <script>


  </script>

</head>
<body class="bodyMain">
<%@include file="/WEB-INF/views/header_html.jsp" %>
<div class="clear"></div>
<div class="i_main clear statistics_main" style="margin-top: 30px;">

<input id="recordType" type="hidden" name="recordType" value="assistantProductRecord"/>
<input id="startTime" type="hidden" name="startTime" value="${startTime}"/>
<input id="endTime" type="hidden" name="endTime" value="${endTime}"/>
<input id="achievementStatTypeStr" name="achievementStatTypeStr" type="hidden" value="${achievementStatTypeStr}"/>
<input id="assistantOrDepartmentId" name="assistantOrDepartmentId" type="hidden" value="${assistantOrDepartmentId}"/>
<input type="hidden" id="startPageNoHiddenHidden" name="startPageNoHiddenHidden" value="${startPageNoHiddenHidden}"/>

<input type="hidden" id="achievementCalculateWayHidden" value="${achievementCalculateWayHidden}"/>
<input type="hidden" id="achievementOrderTypeStrHidden" value="${achievementOrderTypeStrHidden}"/>
<input type="hidden" id="serviceIdStrHidden" value="${serviceIdStrHidden}"/>

<div class="clear"></div>
<div class="cuSearch">
<div class="cartTop"></div>
<div class="cartBody">
<h1>员工业绩明细统计表</h1>

<div class="message-div">
  <div class="divTit">统计日期：
    ${assistantStatSearchDTO.startYear}年${assistantStatSearchDTO.startMonth}月 至
    &nbsp;${assistantStatSearchDTO.endYear}年${assistantStatSearchDTO.endMonth}月
  </div>
  <div class="divTit">统计方式：
    <c:if test="${assistantStatSearchDTO.achievementCalculateWayStr =='CALCULATE_BY_ASSISTANT'}">
      按员工提成设置
    </c:if>
    <c:if test="${assistantStatSearchDTO.achievementCalculateWayStr =='CALCULATE_BY_DETAIL'}">
      按详细提成设置
    </c:if>
  </div>
  <div class="divTit">员工/部门：
    <c:if test="${assistantStatSearchDTO.achievementStatTypeStr =='DEPARTMENT'}">
      按部门&nbsp;&nbsp;部门：
    </c:if>
    <c:if test="${assistantStatSearchDTO.achievementStatTypeStr =='ASSISTANT'}">
      按员工&nbsp;&nbsp;员工：
    </c:if>
    ${assistantStatSearchDTO.assistantOrDepartmentName}
  </div>
  <div class="divTit">分类：
    <span id="category">
    <c:if test="${assistantStatSearchDTO.achievementOrderTypeStr =='ALL'}">
      全部
    </c:if>
    <c:if test="${assistantStatSearchDTO.achievementOrderTypeStr =='WASH_BEAUTY'}">
      洗车美容
    </c:if>

    <c:if test="${assistantStatSearchDTO.achievementOrderTypeStr =='REPAIR_SERVICE'}">
      施工
    </c:if>
    <c:if test="${assistantStatSearchDTO.achievementOrderTypeStr =='MEMBER'}">
      会员卡销售
    </c:if>

    <c:if test="${assistantStatSearchDTO.achievementOrderTypeStr =='BUSINESS_ACCOUNT'}">
      营业外记账
    </c:if>

    <c:if test="${assistantStatSearchDTO.achievementOrderTypeStr =='SALES'}">
      商品销售
    </c:if>
      </span>

    &nbsp;&nbsp;

    <c:if test="${assistantStatSearchDTO.serviceIdStr =='NEW'}">
      购卡
    </c:if>
    <c:if test="${assistantStatSearchDTO.serviceIdStr =='RENEW'}">
      续卡
    </c:if>
    <c:if test="${assistantStatSearchDTO.serviceName !=''}">
      项目：${assistantStatSearchDTO.serviceName}
    </c:if>
  </div>

</div>
<div id="assistantStatDetailDiv" class="contentTitle cont_title">


  <c:if test="${assistantStatSearchDTO.achievementOrderTypeStr =='ALL' ||assistantStatSearchDTO.achievementOrderTypeStr =='REPAIR_SERVICE' }">

    <bcgogo:hasPermission permissions="WEB.VERSION.VEHICLE_CONSTRUCTION">
      <a id="repair" class="detailTitle" style="width: 80px;">车辆施工</a>
    </bcgogo:hasPermission>
  </c:if>

  <c:if test="${assistantStatSearchDTO.achievementOrderTypeStr =='ALL'||assistantStatSearchDTO.achievementOrderTypeStr =='WASH_BEAUTY'}">

    <bcgogo:hasPermission permissions="WEB.VEHICLE_CONSTRUCTION.WASH_BEAUTY.BASE">
      <a id="washBeauty" class="detailTitle" style="width: 80px;">洗车美容</a>
    </bcgogo:hasPermission>
  </c:if>

  <c:if test="${assistantStatSearchDTO.achievementOrderTypeStr =='ALL'||assistantStatSearchDTO.achievementOrderTypeStr =='SALES'}">

    <bcgogo:hasPermission permissions="WEB.TXN.SALE_MANAGE.SALE" resourceType="menu">
      <a id="sales" class="detailTitle" style="width: 80px;">商品销售</a>
    </bcgogo:hasPermission> </c:if>

  <c:if test="${assistantStatSearchDTO.achievementOrderTypeStr =='ALL'||assistantStatSearchDTO.achievementOrderTypeStr =='MEMBER'}">

    <bcgogo:hasPermission permissions="WEB.VERSION.MEMBER_STORED_VALUE">
      <a id="member" class="detailTitle" style="width: 80px;">会员卡销售</a>
    </bcgogo:hasPermission>
  </c:if>

  <bcgogo:hasPermission permissions="WEB.STAT.NONOPERATING_ACCOUNT.BASE">
    <c:if test="${assistantStatSearchDTO.achievementOrderTypeStr =='ALL'||assistantStatSearchDTO.achievementOrderTypeStr =='BUSINESS_ACCOUNT'}">
      <a id="businessAccount" class="detailTitle" style="width: 80px;">营业外收入</a>
    </c:if>
  </bcgogo:hasPermission>

</div>
<div class="bus_stock add tb_add">

<bcgogo:hasPermission permissions="WEB.VERSION.VEHICLE_CONSTRUCTION">
<div id="assistantServiceRecordDiv" style="display: none;">
  <input id="assistantServiceRecordNum" type="hidden" />
  <table id="assistantServiceRecord" class="tab_cuSearch" cellpadding="0" cellspacing="0">
    <col/>
    <col/>
    <col width="90">
    <col width="80">
    <col width="80">
    <col width="55px">
    <col/>
    <col/>
    <col/>
    <col width="80">
    <col width="55px">
    <col width="80px">
    <tr class="titleBg">
      <td style="padding-left:10px;">员工</td>
      <td>部门</td>
      <td>日期</td>
      <td>车辆</td>
      <td>客户</td>
      <td>内容</td>
      <td>标准工时</td>
      <td>工时单价</td>
      <td>实际工时</td>
      <td>金额</td>
      <td>提成</td>
      <td>单据</td>
    <tr class="space">
      <td colspan="13"></td>
    </tr>

  </table>
  <div class="clear i_height"></div>
  <jsp:include page="/common/pageAJAX.jsp">
    <jsp:param name="url" value="assistantStat.do?method=getAssistantServiceByPage"></jsp:param>
    <jsp:param name="jsHandleJson" value="initAssistantService"></jsp:param>
    <jsp:param name="dynamical" value="dynamicalassistantServiceRecord"></jsp:param>
    <jsp:param name="data"
               value="{assistantOrDepartmentIdStr:$('#assistantOrDepartmentId').val(),achievementStatTypeStr:$('#achievementStatTypeStr').val(),
                 startTimeStr:$('#startTime').val(),endTimeStr:$('#endTime').val(),orderType:'repair',startPageNo:1,maxRows:25}"></jsp:param>
    <jsp:param name="display" value="none"></jsp:param>
  </jsp:include>

  <span id="repairNoDataSpan" style="text-align: center;color: #272727;display: none;font-weight:bold;">未找到相关数据！</span>


  </div>
</bcgogo:hasPermission>

<bcgogo:hasPermission permissions="WEB.VEHICLE_CONSTRUCTION.WASH_BEAUTY.BASE">

<div id="assistantWashBeautyRecordDiv" style="display: none;">
    <input id="assistantWashRecordNum" type="hidden" />
  <table id="assistantWashBeautyRecord" class="tab_cuSearch" cellpadding="0" cellspacing="0">
    <col/>
    <col/>
    <col width="90">
    <col width="80">
    <col width="80">
    <col width="55px">

    <col width="80">
    <col width="55px">
    <col width="80px">
    <tr class="titleBg">
      <td style="padding-left:10px;">员工</td>
      <td>部门</td>
      <td>日期</td>
      <td>车辆</td>
      <td>客户</td>
      <td>内容</td>
      <td>金额</td>
      <td>提成</td>
      <td>单据</td>
    </tr>
    <tr class="space">
      <td colspan="10"></td>
    </tr>

  </table>
  <div class="clear i_height"></div>
  <jsp:include page="/common/pageAJAX.jsp">
    <jsp:param name="url" value="assistantStat.do?method=getAssistantServiceByPage"></jsp:param>
    <jsp:param name="jsHandleJson" value="initAssistantWash"></jsp:param>
    <jsp:param name="dynamical" value="dynamicalassistantWashRecord"></jsp:param>
    <jsp:param name="data"
               value="{assistantOrDepartmentIdStr:$('#assistantOrDepartmentId').val(),achievementStatTypeStr:$('#achievementStatTypeStr').val(),
           startTimeStr:$('#startTime').val(),endTimeStr:$('#endTime').val(),orderType:'washBeauty',startPageNo:1,maxRows:25}"></jsp:param>
    <jsp:param name="display" value="none"></jsp:param>
  </jsp:include>
  <span id="washNoDataSpan" style="text-align: center;color: #272727;display: none;font-weight:bold;">未找到相关数据！</span>

</div>
</bcgogo:hasPermission>

<bcgogo:hasPermission permissions="WEB.TXN.SALE_MANAGE.SALE" resourceType="menu">
<div id="assistantProductRecordDiv">
  <input id="assistantProductRecordNum" type="hidden" />
  <table id="assistantProductRecord" class="tab_cuSearch" cellpadding="0" cellspacing="0">
    <col/>
    <col/>
    <col width="90px"/>
    <col/>
    <col/>
    <col/>
    <col/>
    <col/>
    <col/>
    <col/>
    <col/>
    <col/>
    <col width="80px">
    <tr class="titleBg">
      <td style="padding-left:10px;">员工</td>
      <td>部门</td>
      <td>日期</td>
      <td>类型</td>
      <td>客户</td>
      <td>品名</td>
      <td>数量</td>
      <td>单价</td>
      <td>收入</td>
      <td>提成</td>
      <td>利润</td>
      <td>利润提成</td>
      <td>单据</td>
    </tr>
    <tr class="space">
      <td colspan="13"></td>
    </tr>
  </table>
  <div class="clear i_height"></div>

  <jsp:include page="/common/pageAJAX.jsp">
    <jsp:param name="url" value="assistantStat.do?method=getAssistantProductByPage"></jsp:param>
    <jsp:param name="jsHandleJson" value="initAssistantProduct"></jsp:param>
    <jsp:param name="dynamical" value="dynamicalassistantProductRecord"></jsp:param>
    <jsp:param name="data"
               value="{assistantOrDepartmentIdStr:$('#assistantOrDepartmentId').val(),achievementStatTypeStr:$('#achievementStatTypeStr').val(),
                 startTimeStr:$('#startTime').val(),endTimeStr:$('#endTime').val(),orderType:'repair',startPageNo:1,maxRows:25}"></jsp:param>
    <jsp:param name="display" value="none"></jsp:param>
  </jsp:include>

  <span id="productNoDataSpan" style="text-align: center;color: #272727;display: none;font-weight:bold;">未找到相关数据！</span>

</div>
</bcgogo:hasPermission>

<bcgogo:hasPermission permissions="WEB.VERSION.MEMBER_STORED_VALUE">
<div id="assistantMemberRecordDiv" style="display: none;">
    <input id="assistantMemberRecordNum" type="hidden" />
  <table id="assistantMemberRecord" class="tab_cuSearch" cellpadding="0" cellspacing="0">
    <col/>
    <col/>
    <col/>
    <col/>
    <col width="80">
    <col width="55px">
    <col/>
    <col/>
    <col/>
    <col width="80">
    <col width="55px">
    <tr class="titleBg">
      <td style="padding-left:10px;">员工</td>
      <td>部门</td>
      <td>日期</td>
      <td>卡号</td>
      <td>卡名</td>
      <td>卡类型</td>
      <td>卡额</td>
      <td>客户名称</td>
      <td>购卡/退卡</td>
      <td>金额</td>
      <td>提成</td>
    </tr>
    <tr class="space">
      <td colspan="12"></td>
    </tr>

  </table>
  <div class="clear i_height"></div>
  <jsp:include page="/common/pageAJAX.jsp">
    <jsp:param name="url" value="assistantStat.do?method=getAssistantMemberByPage"></jsp:param>
    <jsp:param name="jsHandleJson" value="initAssistantMember"></jsp:param>
    <jsp:param name="dynamical" value="dynamicalassistantMemberRecord"></jsp:param>
    <jsp:param name="data"
               value="{assistantOrDepartmentIdStr:$('#assistantOrDepartmentId').val(),achievementStatTypeStr:$('#achievementStatTypeStr').val(),
                 startTimeStr:$('#startTime').val(),endTimeStr:$('#endTime').val(),startPageNo:1,maxRows:25}"></jsp:param>
    <jsp:param name="display" value="none"></jsp:param>
  </jsp:include>

  <span id="memberNoDataSpan" style="text-align: center;color: #272727;display: none;font-weight:bold;">未找到相关数据！</span>

</div>
</bcgogo:hasPermission>

<bcgogo:hasPermission permissions="WEB.STAT.NONOPERATING_ACCOUNT.BASE">

<div id="assistantBusinessAccountRecordDiv" style="display: none;">
    <input id="assistantBusinessAccountRecordNum" type="hidden" />
<table id="assistantBusinessAccountRecord" class="tab_cuSearch" cellpadding="0" cellspacing="0">
  <col/>
  <col/>
  <col/>
  <col/>
  <col/>
  <col/>
  <col/>
  <col/>
  <tr class="titleBg">
    <td style="padding-left:10px;">部门</td>
    <td>员工</td>
    <td>日期</td>
    <td>类型</td>
    <td>凭证号</td>
    <td>内容</td>
    <td>营业分类</td>
    <td>金额</td>
  </tr>
  <tr class="space">
    <td colspan="8"></td>
  </tr>

</table>
<div class="clear i_height"></div>
<jsp:include page="/common/pageAJAX.jsp">
  <jsp:param name="url" value="assistantStat.do?method=getAssistantBusinessAccountByPage"></jsp:param>
  <jsp:param name="jsHandleJson" value="initAssistantBusinessAccount"></jsp:param>
  <jsp:param name="dynamical" value="dynamicalassistantBusinessAccountRecord"></jsp:param>
  <jsp:param name="data"
             value="{assistantOrDepartmentIdStr:$('#assistantOrDepartmentId').val(),achievementStatTypeStr:$('#achievementStatTypeStr').val(),
               startTimeStr:$('#startTime').val(),endTimeStr:$('#endTime').val(),startPageNo:1,maxRows:25}"></jsp:param>
  <jsp:param name="display" value="none"></jsp:param>
</jsp:include>

  <span id="businessAccountNoDataSpan" style="text-align: center;color: #272727;display: none;font-weight:bold;">未找到相关数据！</span>

</div>
</bcgogo:hasPermission>


</div>


</div>
<div class="cartBottom"></div>
<div class="clear i_height"></div>

<%--<div class="shopping_btn" style="float:left; clear:left;display: none;">--%>
  <%--<div class="divImg" id="exportButton" style="margin-left:8px;">--%>
    <%--<img src="images/generate.jpg">--%>

    <%--<div class="sureWords" style="width:auto;">导出</div>--%>
  <%--</div>--%>
<%--</div>--%>

<div class="shopping_btn">
  <img id="exporting" style="display: none;" title="正在导出" alt="正在导出" src="images/loadinglit.gif">
  <div class="divImg" id="printButton">
    <img src="images/print.png">

    <div class="sureWords">打印</div>
  </div>


  <div class="divImg" id="exportButton">
    <img src="images/print.png">

    <div class="sureWords">导出</div>
  </div>

  <div class="divImg" id="backButton">
    <img src="images/return.png">

    <div class="sureWords" style="padding-top:3px;">返回列表</div>
  </div>
</div>
</div>
<div class="clear"></div>
</div>

<div class="height"></div>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>