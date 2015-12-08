<%--
  Created by IntelliJ IDEA.
  User: ndong
  Date: 14-1-13
  Time: 下午5:54
  To change this template use File | Settings | File Templates.
--%>

<%@ include file="/WEB-INF/views/includes.jsp" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>写短信</title>

    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/message<%=ConfigController.getBuildVersion()%>.css"/>

    <%@include file="/WEB-INF/views/header_script.jsp" %>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery.ztree.core-3.5.min.js"></script>
    <script type="text/javascript" src="js/script<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/txnbase<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/smsWrite<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/smsWriteUtil<%=ConfigController.getBuildVersion()%>.js"></script>

    <script type="text/javascript">
        defaultStorage.setItem(storageKey.MenuUid, "WEB.CUSTOMER_MANAGER.SMS_MANAGER");
        defaultStorage.setItem(storageKey.MenuCurrentItem,"<a href='sms.do?method=smswrite' style='color: #007CDA;cursor:pointer;font-size:14px;height: 30px;line-height: 30px;'>写短信</a>");


        function toSmswrite(){
            window.location.href="sms.do?method=smswrite";
        }

        function toSmsSent(){
            window.location.href="sms.do?method=toSmsList&smsType=SMS_SENT";
        }

        function toSmsDetail(){
            var smsId=$("#smsId").val();
            if(!G.isEmpty(smsId)){
                window.location.href="sms.do?method=toSmsDetail&viewFlag=1&smsId="+smsId;
            }
        }


    </script>

</head>
<body class="bodyMain">
<%@include file="/WEB-INF/views/header_html.jsp" %>
<input id="smsId" type="hidden" value="${smsId}"/>
<input id="smsType" type="hidden" value="${smsType}"/>
<div class="i_main clear">

    <div class="mainTitles">
        <div class="titleWords">短信管理</div>
    </div>
    <div class="messageContent">

        <jsp:include page="smsNavi.jsp">
            <jsp:param name="currPage" value="smsWrite" />
        </jsp:include>
        <div class="messageRight">
            <div class="messageRight_radius">
                <div class="message_send">
                    <div class="left"></div>
                    <div class="right">
                        <h1>您的短信已成功提交!</h1>
                        <c:choose>
                            <c:when test="${smsType eq 'SMS_SEND'}">
                                此短信暂时保存在“待发送”文件夹中，它将在您指定的时间发出！
                            </c:when>
                            <c:otherwise>
                                此短信已经提交给短信平台运营商，等待审核发送！<br />该短信已保存到"已发送" 文件夹中！
                            </c:otherwise>
                        </c:choose>

                        <br />
                        共计：
                        <c:if test="${smsFlag}">
                            发送手机号${countMobile}个（短信${countSmsSent}条）
                        </c:if>
                        <c:if test="${appFlag}">
                            发送手机客户端消息${countAppSent}条！
                        </c:if>

                        <c:if test="${hasFiltered}">
                            <br />（系统自动过滤重复手机号，相同号码只发送一条信息！）
                        </c:if>
                        <br />
                        <a onclick="toSmsDetail()" class="blue_color">查看该短信</a>
                        <div class="clear"></div>
                        <div onclick="toSmsSent()" class="message_back">返回发送列表</div>
                        <div onclick="toSmswrite()" class="message_again">再发一条</div>
                        <div class="clear"></div>
                    </div>
                </div>
                <div class="clear"></div>
            </div>
        </div>

    </div>
</div>

</div>

<%@include file="/WEB-INF/views/footer_html.jsp" %>



</body>
</html>