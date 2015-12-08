<%@ include file="/WEB-INF/views/includes.jsp" %>
<%@ page import="com.bcgogo.config.ConfigController" %>

<%--
  Created by IntelliJ IDEA.
  User: MZDong
  Date: 12-1-9
  Time: 下午5:07
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head><title></title>

  <link rel="stylesheet" type="text/css" href="styles/up2<%=ConfigController.getBuildVersion()%>.css"/>
  <link rel="stylesheet" type="text/css" href="styles/cleanCar<%=ConfigController.getBuildVersion()%>.css"/>

    <script type="text/javascript" src="js/extension/jquery/jquery-1.4.2.min.js"></script>
    <script type="text/javascript" src="js/base<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/application<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript">
        $().ready( function() {
            $("#div_close")[0].onclick = function() {
        window.parent.document.getElementById("mask").style.display = "none";
        window.parent.document.getElementById("iframe_tippage").style.display = "none";
        //window.parent.document.getElementById("iframe_tippage").src = "";
      }

            $("#input_confirm")[0].onclick = function() {
                var uvmValue = document.getElementById("uvmValue").value;
                var newTab = document.getElementById("newTab").value;
                if (uvmValue == null || uvmValue == "null") {
                    uvmValue = "";
                }
                //判断输入值 手机号还是用户名

                if ($("#radio_supplier").attr("checked") == true) {
                    if (newTab == "true") {
                        window.open(encodeURI("unitlink.do?method=supplier&uvmValue=" + uvmValue));
                        $("#div_close")[0].onclick();
                    } else {
                        window.parent.document.location.assign(encodeURI("unitlink.do?method=supplier&uvmValue=" + uvmValue));
                    }
                }
                else if (jQuery("#customermodifyPermission").val() == "true" && $("#radio_customer").attr("checked") == true) {
                    if (newTab == "true") {
                        window.open("unitlink.do?method=customer&uvmValue=" + encodeURIComponent(uvmValue));
                        $("#div_close")[0].onclick();
                    } else {
                        window.parent.document.location.assign("unitlink.do?method=customer&uvmValue=" + encodeURIComponent(uvmValue));
                    }
                }
                else {
                    nsDialog.jAlert("请选择客户或供应商！");
                }
            }

            $("#input_cancel")[0].onclick = function() {
            $("#div_close")[0].onclick();
      }

    });
  </script>
</head>
<body>
<div class="tab_repay" id="div_show">
  <div class="i_arrow"></div>
  <div class="i_upLeft"></div>
  <div class="i_upCenter">
    <div class="i_note" id="div_drag">&nbsp;<!--供应商确定--></div>
    <div class="i_close" id="div_close"></div>
  </div>
  <div class="i_upRight"></div>
  <div class="i_upBody">
    <input type="hidden" id="uvmValue" value="<%=(request.getAttribute("uvmValue"))%>"/>
    <bcgogo:permissionParam permissions="WEB.SUPPLIER_MANAGER.SUPPLIER_DATA.UPDATE,WEB.CUSTOMER_MANAGER.CUSTOMER_MODIFY">
    <input type="hidden" id="suppliermodifyPermission" value="${permissionParam1}"/>
    <input type="hidden" id="customermodifyPermission" value="${permissionParam2}"/>
    </bcgogo:permissionParam>
    <input type="hidden" id="newTab" value="<%=(request.getAttribute("newTab"))%>"/>

    <div class="tab_repayTime clear">未查询到此单位联系人信息,请确认新建:</div>
    <div class="height clear"></div>
    <div class="rad_user"><label>
      <bcgogo:hasPermission permissions="WEB.CUSTOMER_MANAGER.CUSTOMER_MODIFY">
      <input id="radio_customer" type="radio" name="user" checked/>客户</label>
      </bcgogo:hasPermission>
      <bcgogo:hasPermission permissions="WEB.SUPPLIER_MANAGER.SUPPLIER_DATA.UPDATE">
        <label><input id="radio_supplier" type="radio" name="user"/>供应商</label>
      </bcgogo:hasPermission>
    </div>
    <div class="sure"><input id="input_confirm" type="button" value="确认" onfocus="this.blur();"/>
      <input id="input_cancel" value="取消" onfocus="this.blur();"/></div>
  </div>
  <div class="i_upBottom">
    <div class="i_upBottomLeft"></div>
    <div class="i_upBottomCenter"></div>
    <div class="i_upBottomRight"></div>
  </div>
</div>
</body>
</html>