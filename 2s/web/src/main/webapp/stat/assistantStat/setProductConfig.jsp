<%@ page import="com.bcgogo.config.ConfigController" %>
<%--
  Created by IntelliJ IDEA.
  User: zyj
  Date: 12-4-12
  Time: 上午11:25
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <title>商品分类批量修改</title>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>

  <link rel="stylesheet" href="js/extension/jquery/plugin/jquery-ui/themes/redmond/jquery-ui-1.8.21.custom.css"/>
  <link rel="stylesheet" href="js/extension/jquery/plugin/jquery-tooltip/jquery.tooltip.css"/>
  <link rel="stylesheet" href="js/extension/jquery/plugin/dataTables/css/jquery.dataTables_themeroller.css"/>
  <link rel="stylesheet" type="text/css" href="styles/up<%=ConfigController.getBuildVersion()%>.css"/>
  <link rel="stylesheet" type="text/css" href="styles/setSale<%=ConfigController.getBuildVersion()%>.css"/>
  <link rel="stylesheet" href="js/components/themes/bcgogo-droplist<%=ConfigController.getBuildVersion()%>.css"/>
  <link rel="stylesheet"
        href="js/extension/jquery/plugin/tipsy-master/src/stylesheets/tipsy<%=ConfigController.getBuildVersion()%>.css"/>

  <script type="text/javascript" src="js/extension/json2/json2.min.js"></script>
  <script type="text/javascript" src="js/extension/jquery/jquery-1.4.2.min.js"></script>
  <script type="text/javascript"
          src="js/extension/jquery/plugin/jquery-ui/ui/jquery-ui-1.8.21.custom.min.js"></script>
  <script type="text/javascript"
          src="js/extension/jquery/plugin/jquery-tooltip/jquery.tooltip<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript"
          src="js/extension/jquery/plugin/tipsy-master/src/javascripts/jquery.tipsy<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/base<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/application<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript"
          src="js/module/bcgogo-noticeDialog<%=ConfigController.getBuildVersion()%>.js"></script>


  <script type="text/javascript">
    $(document).ready(function () {

      document.getElementById("div_close").onclick = function () {
        window.parent.document.getElementById("mask").style.display = "none";
        window.parent.document.getElementById("iframe_PopupBox_Kind").style.display = "none";
        window.parent.document.getElementById("iframe_PopupBox_Kind").src = "";
        if (jQuery("#btnSaleja", parent.document)) {
          jQuery("#btnSaleja", parent.document).focus().select();
        }
      };

      document.getElementById("submit").onclick = function () {
        if ($("#achievementAmount").val() == "" || $("#achievementAmount").val() == null) {
          alert("请输入提成金额");
        }

        if ($("#achievementType").val() == "RATIO") {
          $("#achievementAmount").val($("#achievementAmount").val().replace("%", "") + "%");
        }

        window.parent.setProductAchievement($("#achievementType").val(), $("#achievementAmount").val());
        document.getElementById("div_close").click();
      };

      document.getElementById("cancel").onclick = function () {
        document.getElementById("div_close").click();
      };
    });
  </script>

</head>
<body>

<div class="tabSale" id="div_show">
  <div class="i_arrow"></div>
  <div class="i_upLeft"></div>
  <div class="i_upCenter">
    <div class="i_note" id="div_drag">设定商品提成</div>
    <div class="i_close" id="div_close"></div>
  </div>
  <div class="i_upRight"></div>
  <div class="i_upBody">
    <table>
      <tr>
        <td>提成类型</td>
        <td><select id="achievementType" class="txt selec_jin" style="width:70px;">
          <option value="AMOUNT">按销售量</option>
          <option value="RATIO">按销售额</option>
        </select></td>
      </tr>
      <tr>
        <td>提&nbsp;&nbsp;&nbsp;&nbsp;成</td>
        <td><input id="achievementAmount" style="width:64px;" maxlength="20"/></td>
      </tr>
    </table>

    <div class="addInput">
      <input id="submit" type="button" value="确定" class="cancel" onfocus="this.blur();"/>
      <input id="cancel" type="button" value="取消" class="cancel" onfocus="this.blur();"/>
    </div>

  </div>
</div>
</body>
</html>