<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<head>
    <meta charset="UTF-8"/>
</head>
<c:set var="currPage" value="${type}"/>
<a id="myMessage" class="${currPage=='myMessage'?'blue_radius':'light_blue'}">消息</a>
<a id="talkWith4SBtn" class="${currPage=='MSG_FROM_WX_USER_TO_SHOP'?'blue_radius':'light_blue'}">4S店对话</a>
<a id="talkWithCatBtn" class="${currPage=='MSG_FROM_WX_USER_TO_MIRROR'?'blue_radius':'light_blue'}">与车对话</a>

<input id="openId" type="hidden" value="${userDTO.openid}">
<input id="headimgurl" type="hidden" value="${userDTO.headimgurl}">
<input id="appUserNo" type="hidden" value="${appUserNo}">
<input id="start" type="hidden" value="0">
<input id="limit" type="hidden" value="5">


<script language="javascript" type="text/javascript">
    $(function () {

        $("#talkWithCatBtn").click(function () {
            var openId = $("#openId").val();
            var appUserNo = $("#appUserNo").val();
            window.location.href = "/web/mirror/to_talk/MSG_FROM_WX_USER_TO_MIRROR/" + openId + "/" + appUserNo;
        });

        $("#talkWith4SBtn").click(function () {
            var openId = $("#openId").val();
            var appUserNo = $("#appUserNo").val();
            window.location.href = "/web/mirror/to_talk/MSG_FROM_WX_USER_TO_SHOP/" + openId + "/" + appUserNo;
        });

        $("#myMessage").click(function () {
            var openId = $("#openId").val();
            window.location.href = "/web/mirror/myMsg/" + openId;
        });
    });

</script>
