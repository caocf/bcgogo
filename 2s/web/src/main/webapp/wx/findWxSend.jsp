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
    <style type="text/css">

    </style>

    <%@include file="/WEB-INF/views/header_script.jsp" %>
    <script type="text/javascript"
            src="js/extension/jquery/plugin/sliptree-tokenfield/bootstrap-tokenfield-v0.12.0-bg.js"></script>
    <script type="text/javascript" src="js/wx/wxSendRecord<%=ConfigController.getBuildVersion()%>.js"></script>

    <script type="text/javascript">
        function chancel() {
            window.location.href = "weChat.do?method=toWxSent";
        }
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
            <jsp:param name="currPage" value="wxSent"/>
        </jsp:include>

        <div class="messageRight">
            <div class="messageRight_radius">
                <strong style="font-size:14px;">发送详情</strong>

                <div class="clear"></div>
                <div class="content_03">
                    <div class="detailTop_bg">
                        <div class="content_txt">
                            <div class="left">发送操作人：</div>
                            <div class="right" id="userName">${userName}</div>
                            <div class="clear"></div>
                        </div>
                        <div class="content_txt">
                            <div class="left">发送时间 ：</div>

                            <div id="sendTime" class="right">${wxMsgDTO.sendTimeStr}</div>
                            <div class="clear"></div>
                        </div>
                        <div class="content_txt">
                            <div class="left">收信人 ：</div>
                            <div class="right" id="contactContainer">${receiver}</div>
                            <div class="clear"></div>
                        </div>
                    </div>
                    <div class="clear"></div>

                    <div id="content" class="msg-detail attached">
                        <div class="m-title">${wxMsgDTO.title}</div>
                        <div><img width="250" height="250" src="${wxMsgDTO.picUrl}"></div>
                        <div class="m-description">${wxMsgDTO.description}</div>
                    </div>
                    <div class="clear height"></div>
                </div>
                <div class="clear"></div>
                <div class="addressList">
                    <a class="main-btn" id="backBtn">返 回</a>
                    <%--<a class="assis-btn" id="reSendBtn">转 发</a>--%>
                    <a class="assis-btn" id="delBtn" msgId="${wxMsgDTO.idStr}">删 除</a>
                </div>
                <div class="clear"></div>
            </div>
        </div>
    </div>
</div>

<%@include file="/WEB-INF/views/footer_html.jsp" %>

</body>

</html>