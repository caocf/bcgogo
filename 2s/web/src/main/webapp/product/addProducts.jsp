<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%--
  Created by IntelliJ IDEA.
  User: wjl
  Date: 11-9-30
  Time: 上午9:15
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="com.bcgogo.config.ConfigController" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<%@ include file="/WEB-INF/views/includes.jsp" %>
<head>
    <title>店面新增产品</title>

    <%
        String webapp = request.getContextPath();
        Integer flagId = Integer.parseInt(request.getParameter("flagId"));
        Integer pvs = Integer.parseInt((request.getParameter("productVehicleStatus")) == "" ? "-1" : (request.getParameter("productVehicleStatus")));
    %>
    <link rel="stylesheet" type="text/css" href="<%=webapp%>/styles/up<%=ConfigController.getBuildVersion()%>.css"/>
    <script type="text/javascript" src="js/extension/jquery/jquery-1.4.2.min.js"></script>
    <script type="text/javascript" src="js/base<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/application<%=ConfigController.getBuildVersion()%>.js"></script>

    <script type="text/javascript">
        var productType = "0";
        $(function () {
            var pvs = '<%=pvs%>';
            if (pvs == '0') {
                $("#checkbox-01")[0].checked = true;
                productType = "0";
            } else if (pvs == '1') {
                $("#checkbox-02")[0].checked = true;
                productType = "1";
            }

            $().ready(function () {
                window.parent.document.getElementById("iframe_PopupBox").style.display = "block";
                $("#div_close").bind('click', function () {
//                window.parent.document.getElementById("mask").style.display = "none";
                    //防止点击关闭按钮造成500错误
                    window.parent.document.getElementById("itemDTOs<%=flagId%>.productType").value = productType;
                    window.parent.document.getElementById("itemDTOs<%=flagId%>.purchasePrice").value = $("#addText").val();

                    window.parent.document.getElementById("iframe_PopupBox").style.display = "none";
                    //window.parent.document.getElementById("iframe_PopupBox").src = "";
                    try {
                        $(window.parent.document.body).find("input[type='button']").eq(0).focus().blur();
                    } catch(e) {
                        ;
                    }
                });

                $("#addSure").bind('click', function () {
                    <%--parent.thisform.itemDTOs[<%=flagId%>].productType.value = productType;--%>
                    <%--parent.thisform.itemDTOs[<%=flagId%>].price.value = $("#addText").val();--%>
                    <%--parent.$("#itemDTOs<%=flagId%>.productType").val(productType);--%>
                    <%--parent.$("#itemDTOs<%=flagId%>.price").val(300);--%>
                    <%----%>
                    window.parent.document.getElementById("itemDTOs<%=flagId%>.productType").value = productType;
                    window.parent.document.getElementById("itemDTOs<%=flagId%>.purchasePrice").value = $("#addText").val();

//                window.parent.document.getElementById("mask").style.display = "none";
                    window.parent.document.getElementById("iframe_PopupBox").style.display = "none";
                    //window.parent.document.getElementById("iframe_PopupBox").src = "";
                });
            });
        });
        function getcheckvalue(checkvalue) {
            productType = checkvalue;
        }
    </script>
</head>
<body>
<div class="i_productRate" id="div_show">
    <div class="i_addTable">
        <div class="i_rateTop"></div>
        <div class="i_rateCenter"></div>
        <div class="i_rateBody">
            <div class="i_rateLogo">店面新增产品</div>
            <div class="addClose" id="div_close"></div>
            <table cellpadding="0" cellspacing="0" class="rate_table">
                <tr>
                    <td>适用车型</td>
                    <td style="border-right:none;">采购价</td>
                </tr>
                <tr>
                    <td style="width:80px;">
                        <div class="a_checkBox"><label for="checkbox-01"><input type="radio" id="checkbox-01"
                                                                                name="name"
                                                                                onclick="getcheckvalue('0')"/>本车型</label>
                        </div>
                        <div class="a_checkBox"><label for="checkbox-02"><input type="radio" id="checkbox-02"
                                                                                name="name"
                                                                                onclick="getcheckvalue('1')"/>通用</label>
                        </div>
                    </td>
                    <td style="border-right:none;"><input type="text" value="0" class="addText" id="addText"/></td>
                </tr>
                <tr>
                    <td colspan="2" style="border-right:none; text-align:right;"><input type="button" value="确认新增"
                                                                                        class="addSure"
                                                                                        onfocus="this.blur();"
                                                                                        id="addSure"/></td>
                </tr>
            </table>
        </div>
        <div class="i_rateLeft"></div>
        <div class="i_addMiddle">
            <div class="i_rateArrow">
            </div>
        </div>
        <div class="i_rateRight"></div>
    </div>

</div>
</body>
</html>
