<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <title>新增配置</title>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <%-- styles --%>
    <link rel="stylesheet" type="text/css" href="styles/dataMaintenance.css"/>
    <%@include file="/WEB-INF/views/style-thirdpartLibs.jsp" %>

    <%-- scripts --%>
    <%@include file="/WEB-INF/views/script-thirdpartLibs.jsp" %>
  <script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.js"></script>
    <%@include file="/WEB-INF/views/script-common.jsp" %>
</head>
<script type="text/javascript">
    jQuery(function () {

        jQuery("#div_close,#cancleBtn").click(function () {
      window.parent.document.getElementById("iframe_PopupBox").style.display = "none";
      window.parent.document.getElementById("mask").style.display = "none";
    });
        jQuery("#confirmBtn").click(function () {
      if (!checkFormData()) {
        return;
      }
      window.parent.document.getElementById("mask").style.display = "none";
      window.parent.document.getElementById("iframe_PopupBox").style.display = "none";
            jQuery('#addConfig_table').ajaxSubmit(function (data) {
        if (data == "succ") {
          window.parent.nextPageNo = 1;
          window.parent.searchConfig();
        }
      });
    });
        jQuery("#config_key").blur(function () {
      var config_key = jQuery("#config_key").val();
      jQuery.ajax({
        type:"POST",
        url:"dataMaintenance.do?method=searchConfig",
                data:{name:config_key, value:"", pageNo:1, shopId:-1},
        cache:false,
        dataType:"json",
                success:function (jsonStr) {
          if (jsonStr.length == 2) {
            jQuery("#config_key").val("");
            alert("key重复，请重新输入！");
          }
        }
      });
    });
  });
  function checkFormData() {
    var config_key = jQuery("#config_key").val().replace("", " ");
    var config_value = jQuery("#config_value").val().replace("", " ");
    if (config_key == "") {
      alert("key不为空，请重新输入！");
      return false;
    }
    if (config_key.toLowerCase() == "key") {
      alert("请使用非key的变量名！");
      return false;
    }
    if (config_value.toLowerCase() == "value") {
      alert("请使用非value的变量名！");
      return false;
    }
    return true;
  }
</script>

<body>
<form:form commandName="configDTO" id="addConfig_table" action="dataMaintenance.do?method=saveOrUpdateConfig"
           method="post" onsubmit="return false;">
    <form:hidden path="shopId" value="${configDTO.shopId}"/>

    <div>
  <div  id="div_show">
    <div class="i_arrow"></div>
    <div class="i_upLeft"></div>
    <div class="i_upCenter">
      <div class="config_title" id="div_drag">新增配置</div>
      <div class="i_close" id="div_close"></div>
    </div>
    <div class="i_upRight"></div>
    <div class="i_upBody">
      <table cellpadding="0" id="configTable" cellspacing="0" class="configTable">
        <col width="100">
        <col  width="100"/>

        <tr>
          <td class="label">key</td>
          <td><form:input path="name" id="config_key" value="${configDTO.name}" class="txt"/></td>
        </tr>
        <tr>
          <td class="label">value</td>
          <td><form:input path="value"  id="config_value" value="${configDTO.value}" class="txt"/></td>
        </tr>
        <tr>
          <td class="label">描述</td>
          <td><form:input path="description" value="${configDTO.description}" class="txt"/></td>
        </tr>
          <%--<tr>--%>
          <%--<td class="label">店铺</td>--%>
          <%--<td>--%>
          <%--<label><input type="radio" name="radio" id="radio_check1"/>所有</label>--%>
          <%--<label><input type="radio" name="radio" id="radio_check2"/>单个店铺</label>--%>
          <%--</td>--%>
          <%--</tr>--%>
      </table>
      <div class="more_his">
        <input type="submit" value="确认" onfocus="this.blur();" class="btn" id="confirmBtn"/>
        <input type="button" value="取消" onfocus="this.blur();" class="btn" id="cancleBtn"/>
      </div>
        <%--<div class="i_upBottom">--%>
        <%--<div class="i_upBottomLeft"></div>--%>
        <%--<div class="i_upBottomCenter"></div>--%>
        <%--<div class="i_upBottomRight"></div>--%>
        <%--</div>--%>
    </div>
  </div>
</form:form>


</body>
</html>