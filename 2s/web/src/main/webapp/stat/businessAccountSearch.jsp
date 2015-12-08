<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <title>营业外记账</title>
  <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>

  <link rel="stylesheet" type="text/css" href="styles/head<%=ConfigController.getBuildVersion()%>.css"/>
  <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>

  <%--<link rel="stylesheet" type="text/css" href="styles/businessAccountSearch<%=ConfigController.getBuildVersion()%>.css"/>--%>
  <%@include file="/WEB-INF/views/header_script.jsp" %>
    <script type="text/javascript">
        <bcgogo:permissionParam permissions="WEB.STAT.NONOPERATING_ACCOUNT.UPDATE,WEB.STAT.NONOPERATING_ACCOUNT.DELETE">
            APP_BCGOGO.Permission.Stat.NonOperatingAccount.Update = ${permissionParam1};
            APP_BCGOGO.Permission.Stat.NonOperatingAccount.Delete = ${permissionParam2};
        </bcgogo:permissionParam>
    </script>
  <script type="text/javascript" src="js/utils/tableUtil<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/module/bcgogo-noticeDialog<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/page/stat/businessAccount<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/script<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/utils/stringUtil<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript">
    $(function() {
      $("#editDateStartStr")
          .datepicker({
            "changeYear":true,
            "changeMonth":true,
            "yearSuffix":"",
            "yearRange":"c-10:c+10",
                  "showButtonPanel":true
          }).bind("click", function() {
            $(this).blur();
            $("a[name='date_select']").removeClass("clicked");

          });
      $("#editDateEndStr")
          .datepicker({
            "changeYear":true,
            "changeMonth":true,
            "yearSuffix":"",
            "yearRange":"c-10:c+10",
                  "showButtonPanel":true
          }).bind("click", function() {
            $(this).blur();
            $("a[name='date_select']").removeClass("clicked");
          });
    });
  </script>
</head>
<body class="bodyMain">
<%@ include file="/WEB-INF/views/header_html.jsp" %>
<%@ include file="/common/messagePrompt.jsp" %>
<div class="i_main clear">

    <div class="mainTitles">
        <div class="titleWords">营业外记账</div>
    </div>
  <div class="i_mainRight">

  <div class="titBody">
      <div class="lineTitle">记账查询</div>

    <form:form commandName="businessAccountSearchConditionDTO" id="searchConditionForm" action="" method="post">
      <div class="lineBody bodys">


        <div class="divTit">日期&nbsp;
          <a name="date_select" id="date_today" class="btnList">今天</a>
          <a name="date_select" id="date_last_week" class="btnList">最近一周</a>
          <a name="date_select" id="date_last_month" class="btnList">最近一月</a>
          <a name="date_select" id="date_three_month" class="btnList">最近三月</a>
          <a name="date_select" id="date_last_year" class="btnList">最近一年</a>
          <form:input path="editDateStartStr" value="" style="color:#444443;" readonly="true" class="txt"/>&nbsp;至
          <form:input path="editDateEndStr" value="" style="color:#444443;" readonly="true" class="txt"/>&nbsp;
          部门&nbsp;<form:input path="dept" value="" class="txt" style="width:180px;color:#444443;"/>
          <input id="departmentId"  type="hidden" />
          人员&nbsp;<form:input path="person" style="color:#444443;" class="txt" value=""/>
        </div>
        <div class="clear"></div>

          <table  width="90%" border="0" style="line-height:20px;">
              <tr>
                  <td width="8%" valign="top">收支分类</td>
                  <td width="8%" style="word-break:break-all">
                    <label class="lbl">
                      <input style="margin-top: 3px;float: left;margin-right: 5px;" id="incomeCategory" name="moneyCategorySelect" type="checkbox" checked="checked"/>
                      收入
                    </label>
                  </td>
                  <td width="8%">
                    <label class="lbl">
                      <input style="margin-top: 3px;float: left;margin-right: 5px;" id="expenditureCategory" name="moneyCategorySelect" checked="checked" type="checkbox"/>
                      支出
                    </label>
                  </td>
                <form:input path="moneyCategoryStr" style="color:#444443;" type="hidden" class="txt"/>

                  <td width="20%">类别 <form:input path="accountCategory" style="color:#444443;width:150px;" value=""  class="txt" />
                    <input id="accountCategoryId"  type="hidden" />

                  </td>
                  <td width="25%">营业分类 <form:input path="businessCategory" value="" class="txt" style="width:180px;color:#444443;"/>
                    <input id="departmentId"  type="hidden" />

                  </td>
                <td width="15%">凭证号 <form:input path="docNo" value=""  style="color:#444443;" class="txt"/></td>
              </tr>
          </table>
          <div class="clear height"></div>
          <div class="divTit button_conditon button_search"> <a id="clearButton" class="blue_color clean">清空条件</a>
            <a id="searchBtn" class="button">查 询</a>
          </div>
      </div>
    </form:form>
      <div class="lineBottom"></div>
      <div class="clear i_height"></div>
  </div>

    <div class="i_mainRight">

    <div class="supplier group_list2 listStyle">
      <div><strong>记账总计：</strong><span id="totalAmountSpan"><span class="red_color">收入0元</span></span> 其中（<span
          class="red_color">收入<span id="totalIncome">0</span>元</span> <span class="green_color">支出<span id="totalExpense">0</span>元</span>）

        <bcgogo:hasPermission permissions="WEB.STAT.NONOPERATING_ACCOUNT.ADD">
          <a id="addBusinessAccountBtn" class="addNewSup blue_color">新增记账</a>
        </bcgogo:hasPermission>
      </div>
    </div>

    <div class="clear i_height"></div>
    <div class="cuSearch">
        <div class="cartTop"></div>
        <div class="cartBody">
            <table class="tab_cuSearch" cellpadding="0" cellspacing="0" id="businessAccount_show">

              <col width="70"/>
              <col width="60"/>
              <col width="70"/>
              <col width="120"/>
              <col width="70"/>
              <col width="70"/>
              <col width="70"/>
              <col width="80"/>
              <col width="200"/>
              <col width="60"/>


                <tr class="titleBg">
                    <td style="padding-left:10px;">记账日期</td>
                    <td>类别</td>
                    <td>凭证号</td>
                    <td>内容</td>
                    <td>营业分类</td>
                    <td>相关部门</td>
                    <td>相关人员</td>
                    <td>记账金额</td>
                    <td>金额详情</td>
                    <td>操作</td>
                </tr>
            </table>
          <div class="clear"></div>
          <div class="clear i_height"></div>
          <div class="clear i_height"></div>

          <div class="his_bottom">
            <bcgogo:hasPermission permissions="WEB.STAT.NONOPERATING_ACCOUNT.BASE_PRINT">
              <div style="float: right">
                <a style="display: none;" id="printBtn" class="buttonBig">打 印</a>
              </div>
            </bcgogo:hasPermission>
            <bcgogo:ajaxPaging url="businessAccount.do?method=searchBusinessAccount" data="{startPageNo:1,maxRows:20}"
                               postFn="businessAccountSearchInitTr" dynamical="dynamical"/>
          </div>
      <div class="clear"></div>

            <div class="clear i_height"></div>
        </div>
        <div class="cartBottom"></div>
    </div>

</div>

<iframe id="iframe_PopupBox" style="position:absolute;z-index:5; left:400px; top:150px; display:none;" scrolling="no"
        allowtransparency="false" width="800px" height="650px" frameborder="0" src=""></iframe>
<div id="mask" style="display:block;position: absolute;"></div>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>