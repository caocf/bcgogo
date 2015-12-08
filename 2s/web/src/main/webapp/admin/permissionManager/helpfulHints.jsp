<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.bcgogo.config.ConfigController" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>无标题文档</title>
<link rel="stylesheet" type="text/css" href="../../styles/up<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="../../styles/accept<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="../../styles/cleanCar<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="../../styles/helpulHints<%=ConfigController.getBuildVersion()%>.css"/>
<script type="text/javascript" src="../../js/extension/jquery/jquery-1.4.2.js"></script>
<script type="text/javascript" src="../../js/base<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="../../js/application<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="../../js/page/admin/helpfulHints<%=ConfigController.getBuildVersion()%>.js"></script>

</head>
<body>
    <div class="tab_repay">
      <div class="i_arrow"></div>
      <div class="i_upLeft"></div>
      <div class="i_upCenter">
        <div class="i_note"  id="div_drag">友情提示</div>
        <div class="i_close" id="div_close" ></div>
      </div>
      <div class="i_upRight"></div>
      <div class="i_upBody">
        <h1>您已成功添加该权限!</h1>
        <div class="btnClick"><label>如果您想新增员工分配权限，请点击</label><input type="button" onfocus="this.blur();" id="addNewStaff"  value="新增员工"/><div class="clear"></div></div>
        
        <div class="btnClick"><label>如果您想选择已有员工分配权限，请点击</label><input type="button"   value="选择已有员工" id="selectEmployee"/><div class="clear"></div></div>
        <div class="btnClick"><label>如果您想返回权限列表，请点击</label><input type="button"  value="返回权限列表" id="returnPermissionList"/><div class="clear"></div></div>
      </div>
      <div class="i_upBottom">
        <div class="i_upBottomLeft"></div>
        <div class="i_upBottomCenter"></div>
        <div class="i_upBottomRight"></div>
      </div>
     </div>
<div id="mask"  style="display:block;position: absolute;">
</div>
<iframe id="iframe_PopupBox"  style="position:absolute;z-index:5;left:400px; top:400px;   display:none; overflow:hidden" allowtransparency="true" width="550px" height="450px" frameborder="0" src="" marginheight="0" scrolling="no" ></iframe>
</body>
</html>
