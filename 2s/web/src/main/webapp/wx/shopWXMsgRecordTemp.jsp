
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
    <title>发送微信</title>

    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/message<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/addCheck<%=ConfigController.getBuildVersion()%>.css"/>
    <%--<link rel="stylesheet" type="text/css" href="styles/zTreeStyle<%=ConfigController.getBuildVersion()%>.css"/>--%>
    <%--<link href="js/extension/bootstrap/css/bootstrap.min.css" rel="stylesheet">--%>
    <link rel="stylesheet" href="js/extension/jquery/plugin/sliptree-tokenfield/bootstrap-tokenfield.css"/>
    <style type="text/css">

    </style>


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

    </script>

</head>
<body class="bodyMain">
<%@include file="/WEB-INF/views/header_html.jsp" %>



<div class="i_main clear">
    <div class="mainTitles">
        <div class="titleWords">微信管理</div>
    </div>
    <div class="messageContent">

        <jsp:include page="wxNavi.jsp">
            <jsp:param name="currPage" value="wxSent" />
        </jsp:include>

        <div class="messageRight">
            <div class="messageRight_radius">
                <div>
                    <table  id="table_AdultJob" class="news-table">
                        <col width="50">
                        <col width="150">
                        <col width="100">
                        <col width="260">
                        <col width="300">
                        <col width="100">
                        <col width="210">
                        <tr>
                            <td style="border-left:none;">序号</td>
                            <td>收信人</td>
                            <td>标题</td>
                            <td>发送时间</td>
                            <td>发送状态</td>
                            <td>送达人数</td>
                            <td>操作</td>
                        </tr>
                    </table>
                </div>
                <div>
                    <div class="i_pageBtn" style="font-size:12px;">
                       <bcgogo:ajaxPaging
                                url="weChat.do?method=getShopWXMsgRecord"
                                data="{
                                startPageNo:1,
                                maxRows:15,
                                 }"
                                postFn="inintTable_SendRecord"
                                dynamical="_getShopWXMsgRecord"/>
                    </div>
                </div>
                <div class="clear"></div>
            </div>
        </div>




    </div>
</div>

<%@include file="/WEB-INF/views/footer_html.jsp" %>



</body>

</html>