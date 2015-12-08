<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
@description web 登陆页面， 现在不做用户名、密码cookie自定义存储， 使用默认浏览器存储功能
--%>
<%@ page import="com.bcgogo.config.ConfigController" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="org.springframework.security.core.AuthenticationException" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>会员卡详情</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <%--<meta name="viewport" content="width=device-width, initial-scale=1" />--%>
    <meta name="viewport" content="width=device-width, initial-scale=1, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no" />
    <link rel="stylesheet" type="text/css" href="styles/wechat.css">
    <script type="text/javascript" src="js/extension/jquery/jquery-1.4.2.min.js"></script>
    <script type="text/javascript" src="js/mobile/weChat<%=ConfigController.getBuildVersion()%>.js"></script>
    <script language="javascript" type="text/javascript">
        $(function(){
//            weChat.hideOptionMenu();
//            $(".card").click(function(){
//                 weChat.hideOptionMenu();
//            });

            $(".get_detail_btn").click(function(){
                window.location.href=$(this).attr("url");
            });
        });
    </script>
</head>

<body>
<div id="membership_card">
    <div class="card">
        <h2>${memberDTO.shopName}</h2>
        <div class="num">NO.${memberDTO.memberNo}</div>
    </div>
    <div class="history_01">
        <div class="card_title">
            <h2>${memberDTO.shopName}</h2>
            <div class="clear"></div>
        </div>
        <%--<div class="card_exclusive">会员专享<span class="red_txt">8.5折</span></div>--%>
        <div class="card_exclusive" style="border-bottom:0">地址：${address}</div>
        <div class="clear"></div>
    </div>
    <div class="details_t" style="margin-top:1em">
        <div class="title"> <span class="fr">余额<span class="red_txt">￥${memberDTO.balanceStr}</span></span> 会员卡号（${memberDTO.memberNo}）</div>
        <table width="100%" border="0" cellpadding="0" cellspacing="0"  class="equa2">
            <colgroup>
                <col width="40%" />
                <col width="33%" />
                <col width="27%" />
            </colgroup>
            <tr>
                <th><span class="txt-left">会员项目</span></th>
                <th>截止日期</th>
                <th>剩余次数</th>
            </tr>
            <c:forEach items="${memberDTO.memberServiceDTOs}" var="service" varStatus="status">
                <tr>
                    <td><span class="list_01">${service.serviceName}</span></td>
                    <td>${service.deadlineStr} </td>
                    <td>${service.timesStr}</td>
                </tr>
            </c:forEach>
        </table>
    </div>

    <c:forEach items="${orders}" var="order" varStatus="status">
        <div class="details_t">
            <div class="title"> 消费记录（${order.vehicle}）</div>
            <table width="100%" border="0" cellpadding="0" cellspacing="0"  class="equa2">
                <colgroup>
                    <col width="40%" />
                    <col width="33%" />
                    <col width="13%" />
                </colgroup>
                <tr>
                    <th><span class="txt-left">日期</span></th>
                    <th><span class="txt-left">消费内容</span></th>
                    <th></th>
                </tr>
                <tr>
                    <td><span class="list_01">${order.vestDateStr} </span></td>
                    <td><span class="list_01">金额&nbsp;<span class="red_txt">${order.total}</span> </span></td>
                    <td class="get_detail_btn" url="${order.orderDetailUrl}"><img src="images/down_next.png" /></td>
                </tr>
            </table>
        </div>
    </c:forEach>
</div>
</body>

</html>