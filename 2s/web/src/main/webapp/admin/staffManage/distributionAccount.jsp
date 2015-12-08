<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.bcgogo.config.ConfigController" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>无标题文档</title>
<link rel="stylesheet" type="text/css" href="../../styles/up<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="../../styles/accept<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="../../styles/addWoker<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="../../styles/distributionAccount<%=ConfigController.getBuildVersion()%>.css"/>
<script type="text/javascript" src="../../js/extension/jquery/jquery-1.4.2.min.js"></script>
<script type="text/javascript" src="../../js/base<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="../../js/application<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="../../js/page/admin/distributionAccount<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="../../js/module//bcgogo-noticeDialog<%=ConfigController.getBuildVersion()%>.js"></script>
</head>
<body>
<div class="tab_repay">
  <div class="i_arrow"></div>
  <div class="i_upLeft"></div>
  <div class="i_upCenter">
    <div class="i_note" id="div_drag">分配账户</div>
    <div class="i_close" id="div_close"></div>
  </div>
  <div class="i_upRight"></div>
  <div class="i_upBody">
    <table cellpadding="0" cellspacing="0" id="add_info">
      <col width="75px;"/>
      <col/>
      <tr>
        <td class="right">职位权限<img src="/web/images/xinhao.png"/></td>
        <td><select style="width:155px; height:22px;" id="userGroup3">
         <option value="" userGroupId="">请选择</option>
        </select>
        <input type="hidden" value="" id="userGroupId"/>
        </td>
      </tr>
      <tr>
        <td class="right">账户名<img src="/web/images/xinhao.png"/></td>
        <td><input type="text" id="userNo"/></td>
        <input type="hidden" id="userId"/>
      </tr>

      <tr>
        <td>&nbsp;</td>
        <td style="color:#666">(建议使用字母、汉字、数字)</td>
      </tr>
    </table>
    <div class="btnClick clear">
      <input type="button" value="确&nbsp;认" id="save">
      <input type="button" onfocus="this.blur();" value="取&nbsp;消" id="cancel">
    </div>
  </div>
  <div class="i_upBottom">
    <div class="i_upBottomLeft"></div>
    <div class="i_upBottomCenter"></div>
    <div class="i_upBottomRight"></div>
  </div>
</div>

</body>
</html>
