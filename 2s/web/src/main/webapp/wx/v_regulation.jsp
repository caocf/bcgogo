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
    <title>车辆违章</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <%--<meta name="viewport" content="width=device-width, initial-scale=1" />--%>
    <meta name="viewport" content="width=device-width, initial-scale=1, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no" />
    <link rel="stylesheet" type="text/css" href="styles/wechat<%=ConfigController.getBuildVersion()%>.css">
    <script type="text/javascript" src="js/extension/jquery/jquery-1.4.2.min.js"></script>
    <script type="text/javascript" src="js/mobile/weChat<%=ConfigController.getBuildVersion()%>.js"></script>
    <script language="javascript" type="text/javascript">

    </script>
</head>
<body>
<div id="illegal">
    <div class="content">
        <div class="illegal_title">
            <div class="illegal_50">累计：罚款<span class="red_txt">￥${tMoney}</span>
                <c:choose>
                    <c:when test="${tFen==0}">
                        未扣分
                    </c:when>
                    <c:otherwise>
                        扣<span class="green_txt">${tFen}</span>分
                    </c:otherwise>
                </c:choose>

            </div>
            <span>${vehicle}</span>
        </div>
        <c:choose>
            <c:when test="${not empty recordDTOs}">
                <c:forEach items="${recordDTOs}" var="recordDTO" varStatus="status">
                    <div class="illegal_01">
                        <div class="time">${recordDTO.date}</div>
                        <table width="100%" border="0" cellspacing="0" cellpadding="0">
                            <colgroup>
                                <col  width="17%"/>
                                <col  width="33%"/>
                                <col  width="17%"/>
                                <col  width="33%"/>
                            </colgroup>
                            <tr>
                                <td valign="top">地点：</td>
                                <td colspan="3">${recordDTO.area}</td>
                            </tr>
                            <tr>
                                <td valign="top">内容：</td>
                                <td colspan="3">${recordDTO.act}</td>
                            </tr>
                            <tr>
                                <td>罚款：</td>
                                <td><span class="red_txt">￥${recordDTO.money}</span></td>

                                <c:choose>
                                    <c:when test="${recordDTO.fen==0}">
                                        <td colspan="2">未扣分</td>
                                    </c:when>
                                    <c:otherwise>
                                        <td>扣分：</td>
                                        <td><span class="green_txt">${recordDTO.fen}</span> </td>
                                    </c:otherwise>
                                </c:choose>

                            </tr>
                        </table>
                    </div>
                </c:forEach>
            </c:when>
            <c:otherwise>
            </c:otherwise>
        </c:choose>

    </div>
</div>
</body>
</html>