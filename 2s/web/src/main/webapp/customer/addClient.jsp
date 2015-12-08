<%@ page import="com.bcgogo.config.ConfigController" %>
<%--
  Created by IntelliJ IDEA.
  User: MZDong
  Date: 11-11-8
  Time: 上午11:58
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>添加用户</title>
    <link rel="stylesheet" type="text/css" href="styles/up2<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/addClient<%=ConfigController.getBuildVersion()%>.css"/>
    <script type="text/javascript" src="js/extension/jquery/jquery-1.4.2.min.js"></script>
    <script type="text/javascript" src="js/base<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/application<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/module/bcgogo-noticeDialog<%=ConfigController.getBuildVersion()%>.js"></script>

    <script type="text/javascript" src="js/customer<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/addclientsolr<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/mask<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">
        $().ready(function() {
            $("#div_close")[0].onclick = function() {
                window.parent.document.getElementById("mask").style.display = "none";
                window.parent.document.getElementById("iframe_PopupBox").style.display = "none";
                //window.parent.document.getElementById("iframe_PopupBox").src = "";
                <%
                    String fromPage = (String)request.getAttribute("fromPage");
                    //if(fromPage!=null&&fromPage.trim().equals("guid")){
                    if(fromPage==null){
                %>
                         window.parent.location = "customer.do?method=customerdata";
                <%
                    }
                %>
                try {
                    $(window.parent.document.body).find("input[type='button']").eq(0).focus().blur();
                } catch(e) {
                    ;
            }
            };

            $("#cancleBtn")[0].onclick = function() {
                $("#div_close")[0].onclick();
            };

//            window.parent.addHandle(document.getElementById('div_drag'), window);
        });

        $().ready(function() {
            $("#div_close")[0].onclick();
        });

        $().ready(function() {
            $("#birthday,#carDate").datetimepicker({
                "numberOfMonths" : 1,
                "showButtonPanel": true,
                "changeYear":true,
                "changeMonth":true
            });
        });
    </script>
    <script type="text/javascript">

        function checkSubmit() {
            //手机号码验证     11位数字,以1开头
            var mobileReg = /^[1]{1}[0-9]{10}$/g;
            var mobile = document.getElementById("mobile").value;
            if (!mobileReg.test(mobile)) {
                nsDialog.jAlert("请输入正确的手机号码!");
                return false;
            }
            //电话号码验证
            var landlineReg = /^((1\d{10})|^((\d{7,8})|(\d{4}|\d{3})-(\d{7,8})|(\d{4}|\d{3})(\d{7,8})|(\d{4}|\d{3})-(\d{7,8})-(\d{4}|\d{3}|\d{2}|\d{1})|(\d{7,8})-(\d{4}|\d{3}|\d{2}|\d{1}))$)$/;
            var landline = document.getElementById("landline").value;
            if (!landlineReg.test(landline)) {
                nsDialog.jAlert("请输入正确的座机号码!");
                return false;
            }
            return true;
        }


    </script>
</head>

<body>

<div class="i_addClient" id="div_show">
    <div class="i_arrow"></div>
    <div class="i_upLeft"></div>
    <div class="i_upCenter">
        <div class="i_note" id="div_drag">新增客户</div>
        <div class="i_close" id="div_close"></div>
    </div>
    <div class="i_upRight"></div>
    <div class="i_upBody">
        <form action="customer.do?method=addcustomer" method="post"  onsubmit="return checkSubmit()">
            <table cellpadding="0" cellspacing="0" class="table4" width="300">
                <tr>
                    <td width="50">车主</td>
                    <td><input name="name" id="name" type="text"/></td>
                </tr>
                <tr>
                    <td>手机</td>
                    <td><input name="mobile" id="mobile" type="text"/></td>
                </tr>
                <tr>
                    <td>电话</td>
                    <td><input name="landline" id="landline" type="text"/></td>
                </tr>
                <tr>
                    <td>QQ</td>
                    <td><input name="qq" id="qq" type="text"/></td>

                </tr>
                <tr>
                    <td>Email</td>
                    <td><input name="email" id="email" type="text"/></td>

                </tr>
                <tr>
                    <td>生日</td>
                    <td><input name="birthday" id="birthday" type="text"/></td>

                </tr>
                <tr>
                    <td>单位</td>
                    <td><input name="company" type="text"/></td>
                </tr>
                <tr>
                    <td>地址</td>
                    <td><input name="address" type="text"/></td>
                </tr>
            </table>
            <table cellpadding="0" cellspacing="0" class="table4" width="310" style="margin-right:0px;">
                <tr>
                    <td width="70">车牌号<font color=red>*</font></td>
                    <td><input name="licenceNo" id="licenceNo" type="text"/></td>
                </tr>
                <tr>
                    <td>品 牌</td>
                    <td><input name="brand" type="text" id="brand"/></td>
                </tr>
                <tr>
                    <td>车 型</td>
                    <td><input name="model" type="text" id="model"/></td>
                </tr>
                <tr>
                    <td>年 代</td>
                    <td><input name="year" id="year" type="text"/></td>
                </tr>
                <tr>
                    <td>排 量</td>
                    <td><input name="engine" type="text" id="engine"/></td>

                </tr>
                <tr>
                    <td>车架号</td>
                    <td><input name="vin" type="text"/></td>

                </tr>
                <tr>
                    <td>购车日期</td>
                    <td><input name="carDate" id="carDate" type="text"/></td>
                </tr>
              <tr>
                <td class="label">备注</td>
                <td class="inputBox">
                  <textarea rows="2" cols="40" name="memo" id="memo" maxlength="400"></textarea>
                </td>
              </tr>
            </table>
            <div class="clientBtn">
                <input type="button" value="取消"  id="cancleBtn"/>
                <input type="submit" id="addCustomerBtn" value="确认添加" onfocus="this.blur();"/>
            </div>
        </form>
    </div>
    <div class="i_upBottom">
        <div class="i_upBottomLeft"></div>
        <div class="i_upBottomCenter"></div>
        <div class="i_upBottomRight"></div>
    </div>
</div>
<div id="div_brand" class="i_scroll" style="display:none;">
    <div class="Container">
        <div id="Scroller-1">
            <div class="Scroller-Container" id="Scroller-Container_id">
            </div>
        </div>
    </div>
</div>

</body>
</html>