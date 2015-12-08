
<%@ include file="/WEB-INF/views/includes.jsp" %>
<%
    //会员个性化
    boolean isMemberSwitchOn = WebUtil.checkMemberStatus(request);
%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>客户详情</title>

    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/message<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/addCheck<%=ConfigController.getBuildVersion()%>.css"/>
    <%--<link rel="stylesheet" type="text/css" href="styles/zTreeStyle<%=ConfigController.getBuildVersion()%>.css"/>--%>
    <%--<link href="js/extension/bootstrap/css/bootstrap.min.css" rel="stylesheet">--%>
    <link rel="stylesheet" href="js/extension/jquery/plugin/sliptree-tokenfield/bootstrap-tokenfield.css"/>
    <style type="text/css">

    </style>

    <%--<script type="text/javascript" src="js/extension/jquery/jquery-1.10.2.js"></script>--%>
    <%--<script>--%>
        <%--var _$10 = jQuery.noConflict(true);--%>
    <%--</script>--%>

    <%@include file="/WEB-INF/views/header_script.jsp" %>
    <%--<script type="text/javascript" src="js/customer<%=ConfigController.getBuildVersion()%>.js"></script>--%>
    <%--<script type="text/javascript" src="js/script<%=ConfigController.getBuildVersion()%>.js"></script>--%>
    <%--<script type="text/javascript" src="js/txnbase<%=ConfigController.getBuildVersion()%>.js"></script>--%>
    <%--<script type="text/javascript" src="js/components/ui/bcgogo-customerSms-input<%=ConfigController.getBuildVersion()%>.js"></script>--%>
    <%--<script type="text/javascript" src="js/components/ui/bcgogo-iframe-post<%=ConfigController.getBuildVersion()%>.js"></script>--%>
    <%--<script type="text/javascript" src="js/smsWrite<%=ConfigController.getBuildVersion()%>.js"></script>--%>
    <%--<script type="text/javascript" src="js/smsWriteUtil<%=ConfigController.getBuildVersion()%>.js"></script>--%>
    <%--<script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>--%>
    <script type="text/javascript" src="js/extension/jquery/plugin/sliptree-tokenfield/bootstrap-tokenfield-v0.12.0-bg.js"></script>
    <script type="text/javascript" src="js/wx/wxSendRecord<%=ConfigController.getBuildVersion()%>.js"></script>

    <script type="text/javascript">
        //defaultStorage.setItem(storageKey.MenuUid, "WEB.CUSTOMER_MANAGER.SMS_MANAGER");
        //defaultStorage.setItem(storageKey.MenuCurrentItem,"<a href='sms.do?method=smswrite' style='color: #007CDA;cursor:pointer;font-size:14px;height: 30px;line-height: 30px;'>写短信</a>");
        function chancel(){
            window.location.href="weChat.do?method=toWXFans";
        }
    </script>

</head>
<body class="bodyMain">
<%@include file="/WEB-INF/views/header_html.jsp" %>



<div class="i_main clear">
    <div class="mainTitles">
        <div class="titleWords">客户详情</div>
    </div>
    <div class="messageContent">
        <jsp:include page="wxNavi.jsp">
            <jsp:param name="currPage" value="wxFan" />
        </jsp:include>


        <div class="messageRight">
            <div class="messageRight_radius">
                <div>
                    <table cellpadding="0" id="configTable" cellspacing="0">
                        <col width="100">
                        <col  width="100"/>
                        <%--<input type="hidden" id="weChatId" name="id" value="${wxMsg.id}"/>--%>
                        <br/>
                        <tr>
                            <td class="label">客户姓名</td>
                            <td style="height:20px;width: 224px;">${customer.name}
                            </td>
                        </tr>
                        <tr>
                            <td class="label">手机</td>
                            <td style="height:20px;width: 224px;">${customer.mobile}
                            </td>
                        </tr>
                        <tr>
                            <td class="label">座机</td>
                            <td style="height:20px;width: 224px;">${customer.landLine}
                            </td>
                        </tr>
                        <tr>
                            <td class="label">地址</td>
                            <td style="height:20px;width: 224px;">${customer.address}
                            </td>
                        </tr>
                        <tr>
                            <td class="label">联系人</td>
                            <td style="height:20px;width: 224px;">${customer.contact}
                            </td>
                        </tr>
                    </table>
                </div>
                <div style="margin-left: 190px">
                    <input type="button" value="返回" onclick="chancel()" class="rightSearch" id="cancleBtn" />
                </div>
                <div class="clear"></div>
            </div>
        </div>




    </div>
</div>

<%@include file="/WEB-INF/views/footer_html.jsp" %>



</body>

</html>