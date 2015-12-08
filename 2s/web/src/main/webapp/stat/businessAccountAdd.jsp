<%@ page import="com.bcgogo.config.ConfigController" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--<%@ page import="com.bcgogo.config.ConfigController" %>--%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <title>新增营业外记账</title>
  <link rel="stylesheet" type="text/css" href="js/extension/jquery/plugin/jquery-ui/themes/redmond/jquery-ui-1.8.21.custom.css"/>
  <link rel="stylesheet" type="text/css" href="js/extension/jquery/plugin/jquery-ui/themes/redmond/jquery.ui.timepicker-addon.css"/>
  <link rel="stylesheet" type="text/css" href="js/extension/jquery/plugin/dataTables/css/jquery.dataTables_themeroller.css"/>
  <link rel="stylesheet" type="text/css" href="js/extension/jquery/plugin/jquery-tooltip/jquery.tooltip.css"/>
  <link rel="stylesheet" type="text/css" href="styles/up<%=ConfigController.getBuildVersion()%>.css"/>
  <link rel="stylesheet" type="text/css" href="styles/returnStorage<%=ConfigController.getBuildVersion()%>.css"/>
  <link rel="stylesheet" type="text/css" href="styles/addBookkeep<%=ConfigController.getBuildVersion()%>.css"/>
  <link rel="stylesheet" href="js/extension/jquery/plugin/jquery-tooltip/jquery.tooltip.css"/>
  <link rel="stylesheet" href="js/extension/jquery/plugin/tipsy-master/src/stylesheets/tipsy.css"/>
  <link rel="stylesheet" href="js/components/themes/bcgogo-detailsListPanel<%=ConfigController.getBuildVersion()%>.css"/>
  <link rel="stylesheet" href="js/components/themes/bcgogo-droplist<%=ConfigController.getBuildVersion()%>.css"/>
  <link rel="stylesheet" type="text/css" href="styles/addCheck<%=ConfigController.getBuildVersion()%>.css"/>

  <script type="text/javascript" src="js/extension/json2/json2.min.js"></script>
  <script type="text/javascript" src="js/extension/jquery/jquery-1.4.2.min.js"></script>
  <script type="text/javascript" src="js/extension/jquery/plugin/jquery.validate.min.js"></script>
  <script type="text/javascript" src="js/extension/jquery/plugin/jquery-ui/ui/jquery-ui-1.8.21.custom.min.js"></script>
  <script type="text/javascript"
          src="js/extension/jquery/plugin/jquery-ui/ui/jquery.ui.timepicker-addon.min.js"></script>
  <script type="text/javascript"
          src="js/extension/jquery/plugin/jquery-ui/ui/i18n/jquery.ui.datepicker-zh-CN.min.js"></script>
  <script type="text/javascript" src="js/extension/jquery/plugin/dataTables/js/jquery.dataTables.min.js"></script>
  <script type="text/javascript"
          src="js/extension/jquery/plugin/jquery-tooltip/jquery.tooltip<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/extension/jquery/plugin/jquery.rotate.min.js"></script>
  <script type="text/javascript"
          src="js/extension/jquery/plugin/tipsy-master/src/javascripts/jquery.tipsy<%=ConfigController.getBuildVersion()%>.js"></script>

  <script type="text/javascript" src="js/base<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/application<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/module/bcgogo-noticeDialog<%=ConfigController.getBuildVersion()%>.js"></script>

  <script type="text/javascript" src="js/components/ui/bcgogo-highlightcomplete<%=ConfigController.getBuildVersion()%>.js"></script>

  <script type="text/javascript"
          src="js/components/ui/bcgogo-droplist<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript"
          src="js/components/ui/bcgogo-droplist-lite<%=ConfigController.getBuildVersion()%>.js"></script>

  <script type="text/javascript" src="js/page/stat/businessAccount<%=ConfigController.getBuildVersion()%>.js"></script>

  <script type="text/javascript">

    function limit_textarea_input() {
      $("textarea[maxlength]").bind('input propertychange', function () {
        var maxLength = $(this).attr('maxlength');
        if ($(this).val().length > maxLength) {
          $(this).val($(this).val().substring(0, maxLength));
        }
      })
    }
    $(document).ready(function () {
          if ($("#id").val() != null && $("#id").val() != "") {
            window.parent.document.getElementById("mask").style.display = "none";
            window.parent.document.getElementById("iframe_PopupBox").style.display = "none";
            window.parent.document.getElementById("iframe_PopupBox").src = "";
            window.parent.searchBusinessAccount(1);
          }

          $("#editDateStr")
              .datepicker({
                "changeYear": true,
                "changeMonth": true,
                "yearSuffix": "",
                "yearRange": "c-10:c+10",
                "showButtonPanel": true
              }).bind("click", function () {
                $(this).blur();
              });

          $(document).click(function () {
            $("#div_serviceName").hide();
          });

          $("#businessCategory").live("keyup", function () {
            $(this).val(APP_BCGOGO.StringFilter.inputtingProductNameFilter(this.value));
          });

          limit_textarea_input();

        }
    );

  </script>
</head>

<body>
<div class="alertMain newCustomers" style="width:540px;">

  <div class="alert_title">
    <div class="left"></div>
    <div class="body" style="width:536px;">新增营业外记账</div>
    <div class="right"></div>
  </div>
  <div class="height"></div>
  <div class="height"></div>
  <div class="height"></div>
  <label class="businessAccountAddTitle">记账内容</label>

  <form:form commandName="businessAccountDTO" id="businessAccountForm"
             action="businessAccount.do?method=addBusinessAccount" method="post">
    <form:hidden path="id" value="${businessAccountDTO.id}"/>
    <form:hidden path="status" value="${businessAccountDTO.status}"/>
    <form:hidden path="shopId" value="${businessAccountDTO.shopId}"/>

    <div class="divTit" style="margin-right:50px;">
      <span class="name"><span class="red_color">*</span>记账日期</span>
      <form:input path="editDateStr" class="txt" style="width:160px;color: #444443;" value="${businessAccountDTO.editDateStr}"
                  readonly="true"/>
    </div>
    <div class="divTit">
      <span class="name"><span class="red_color">*</span>收支分类</span>

      <div style="display:inline-block;">
        <input id="moneyCategoryIncome"  name="moneyCategoryRadio" type="radio" value="income"/>收入
        <input id="moneyCategoryExpenses" name="moneyCategoryRadio" type="radio" value="expenses"/>支出

        <form:input path="moneyCategory" type="hidden" value=""/>

      </div>
    </div>
    <div class="clear"></div>

    <div class="divTit" style="margin-right:5px;">
      <span class="name"><span class="red_color">*</span>类别</span>
      <form:input path="accountCategory" class="txt" style="width:160px;color: #444443;" maxlength="5" value="${businessAccountDTO.accountCategory}"/>
    </div>
    <div class="divTit">
      <span class="name" style="width: 120px;"><span style="color:#D6D6D6;">(最多可以输入5个字)</span></span>
    </div>
    <div class="clear"></div>


    <div class="divTit" style="margin-right:50px;">
      <span class="name">凭证号</span>
      <form:input path="docNo" class="txt"
                  value="${businessAccountDTO.docNo==null?'':businessAccountDTO.docNo}"
                  cssStyle="width:160px;color: #444443;" maxlength="25"/>
    </div>


    <div class="divTit" style="margin-right:20px;">
      <span class="name">营业分类</span>
      <form:input path="businessCategory" class="txt"
                  value="${businessAccountDTO.businessCategory==null?'':businessAccountDTO.businessCategory}"
                  style="width:160px;color: #444443;" maxlength="40"/>
    </div>
    <div class="clear"></div>
    <div class="divTit" style="margin-right:50px;">
      <span class="name">相关部门</span>
      <%--<form:input path="dept" class="txt" style="width:160px;color: #444443;"--%>
                  <%--value="${businessAccountDTO.dept==null?'':businessAccountDTO.dept}"--%>
                  <%--maxlength="25"/>--%>


      <form:select path="departmentId" cssStyle="width:165px;">
        <option value="">请选择</option>
        <c:forEach items="${departmentDTOList}" var="item">
          <option value="${item.idStr}">${item.name}</option>
        </c:forEach>
      </form:select>

    </div>
    <div class="divTit" style="margin-right:20px;">
      <span class="name">相关人员</span>
      <%--<form:input path="person" style="width:160px;color: #444443;" class="txt"--%>
                  <%--value="${businessAccountDTO.person==null?'':businessAccountDTO.person}"--%>
                  <%--maxlength="15"/>--%>

      <form:select path="salesManId" disabled="true" cssStyle="width:165px;">
        <option id="personFirstSelect" value="">请选择</option>
      </form:select>

    </div>
    <div class="clear"></div>
    <div class="divTit" style="width:100%;">
      <span class="name" style="vertical-align:top;">内容</span>
      <form:textarea path="content" class="txt textarea" style="color: #444443;width:440px;"
                     value="${businessAccountDTO.content==null ? '':businessAccountDTO.content}"
                     maxlength="450"/>
    </div>
    <form:input path="total" type="hidden" value="${businessAccountDTO.total==null?'0.0':businessAccountDTO.total}" readonly="true"/>
    <label class="businessAccountAddTitle"><span style="float:right;">合计
      <span id="totalSpan">${businessAccountDTO.total==null?'0.0':businessAccountDTO.total}</span>元</span>记账金额</label>


    <table cellspacing="0" cellpadding="0" class="table2 tabCard">
      <colgroup>
        <col width="80">
        <col width="100">
        <col width="80">
        <col width="100">
        <col width="80">
        <col/>
      </colgroup>
      <tbody>
      <tr>
        <td align="right">现金：</td>
        <td>

          <form:input path="cash" style="width:60px;"
                      value="${businessAccountDTO.cash==null?'0':businessAccountDTO.cash}"/> 元

        </td>
        <td align="right">银联：</td>
        <td>
          <form:input path="unionpay" style="width:60px;"
                      value="${businessAccountDTO.unionpay==null?'0':businessAccountDTO.unionpay}"/>
          元
        </td>
        <td align="right">支票：</td>
        <td>
          <form:input path="check" style="width:60px;"
                      value="${businessAccountDTO.check == null?'0':businessAccountDTO.check}"/>

          元
        </td>
      </tr>
      </tbody>
    </table>

    <div class="clear"></div>

    <div class="clear i_height"></div>
    <div class="height"></div>
    <div class="height"></div>
    <div class="height"></div>
    <div class="button">
      <!--<label class="chk"><input type="checkbox" />又是供应商</label>-->
      <a id="saveBtn" class="btnSure">新 增</a>
      <a id="cancelBtn" class="btnSure">取 消</a>
    </div>
    <div class="height"></div>

  </form:form>

</div>


<div id="div_serviceName" class="i_scroll" style="display:none;width:228px;">
  <div class="Scroller-Container" id="Scroller-Container_ServiceName">
  </div>
</div>

</body>

</html>
