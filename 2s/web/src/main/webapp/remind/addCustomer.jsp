<%@ page import="com.bcgogo.config.ConfigController" %>
<%--
  Created by IntelliJ IDEA.
  User: sl
  Date: 12-4-11
  Time: 下午5:22
  To change this template use File | Settings | File Templates.
--%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <%--<meta http-equiv="X-UA-Compatible" content="IE=EmulateIE7"/>--%>
    <title>增加客户</title>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css">
    <link rel="stylesheet" type="text/css" href="styles/up3<%=ConfigController.getBuildVersion()%>.css">
    <link rel="stylesheet" type="text/css" href="styles/up<%=ConfigController.getBuildVersion()%>.css">
    <link rel="stylesheet" type="text/css" href="styles/addUser<%=ConfigController.getBuildVersion()%>.css">
    <link rel="stylesheet" type="text/css" href="styles/moreHistory<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/addClient<%=ConfigController.getBuildVersion()%>.css"/>
    <script type="text/javascript" src="js/extension/jquery/jquery-1.4.2.min.js"></script>
    <script type="text/javascript" src="js/base<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/application<%=ConfigController.getBuildVersion()%>.js"></script>

    <script type="text/javascript" src="js/bcgogo<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/addCustomer<%=ConfigController.getBuildVersion()%>.js"></script>
    <%--<script type="text/javascript" src="js/dragiframe<%=ConfigController.getBuildVersion()%>.js"></script>--%>

</head>
<body style="background:none;">
<div class="i_history" id="div_show">
    <div class="i_arrow"></div>
    <div class="i_upLeft"></div>
    <div class="i_upCenter">
        <div class="i_note" id="div_drag">添加客户</div>
        <div class="i_close" id="div_close"></div>
    </div>
    <div class="i_upRight"></div>
    <div class="i_upBody i_mainRight">
        <div class="cus_current clear"><span>共有${customerNumber}人</span><input id="addAllCustomer" type="button"
                                                                               value="添加所有客户" onfocus="this.blur();"/>
        </div>
        <div class="cus_title clear">
            <div class="cus_titleLeft" ></div>
            <div class="cus_titleBody" style="width:750px;">
                <div style="margin-left:30px;">No</div>
                <div style="margin-left:45px;*margin-left:35px;">车牌号</div>
                <div style="margin-left:83px;*margin-left:63px;">客户名</div>
                <div style="margin-left:170px;*margin-left:65px;">联系人</div>
                <div style="margin-left:184px;*margin-left:64px;">联系方式</div>
            </div>
            <div class="cus_titleRight"></div>
        </div>
        <table class="cus_table clear" id="chk_show" cellpadding="0" cellspacing="0">
            <col width="35px"/>
            <col width="60px"/>
            <col width="120px"/>
            <col />
            <col width="220px"/>
            <col width="120px"/>
            <tbody>

            </tbody>
        </table>
         <div class="checkAll chk_show"><input type="checkbox" id="checkAll"></div><label class="all_show all_teo">全选</label>
        <div class="i_sure"><input id="cancleBtn" type="button" value="取消" onfocus="this.blur();"/><input id="submitBtn"
                                                                                                          type="button"
                                                                                                          value="确认"
                                                                                                          onfocus="this.blur();"/>
        </div>
         <div class="btn_show clear">
        <%--<div class="i_leftBtn" id="pageNo_id">--%>
          <%--<div class="lastPage">上一页</div>--%>
          <%--<div class="onlin_his" id="thisPageNo">1</div>--%>
          <%--<div class="nextPage">下一页</div>--%>
        <%--</div>--%>
        <jsp:include page="/common/pageAJAX.jsp">
          <jsp:param name="url" value="remind.do?method=getCustomers"></jsp:param>
          <jsp:param name="jsHandleJson" value="initTr"></jsp:param>
        </jsp:include>
        <div class="clear"></div>
    </div>
 </div>
    <div class="i_upBottom clear">
        <div class="i_upBottomLeft "></div>
        <div class="i_upBottomCenter"></div>
        <div class="i_upBottomRight "></div>
    </div>
</div>
</body>
</html>