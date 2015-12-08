<%@ page import="org.springframework.security.core.AuthenticationException" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>Bcgogo Admin</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>

    <%
        String path = request.getContextPath();
        String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";

        // Logic of showing authentication failed
        AuthenticationException authenticationFailed = (AuthenticationException) session.getAttribute("SPRING_SECURITY_LAST_EXCEPTION");
        if (authenticationFailed != null) {
    %>
    <script language="javascript" type="text/javascript">
        alert("<%= authenticationFailed.getMessage()%>");
    </script>
    <%
        }
    %>

    <%-- styles --%>
    <link rel="stylesheet" type="text/css" href="<%=basePath%>styles/backstage.css"/>
    <link rel="stylesheet" type="text/css" href="<%=basePath%>styles/backLogin.css"/>
    <%@include file="WEB-INF/views/style-thirdpartLibs.jsp" %>

    <%-- scripts --%>
    <%@include file="WEB-INF/views/script-thirdpartLibs.jsp" %>
    <%@include file="WEB-INF/views/script-common.jsp" %>
    <script type="text/javascript" src="js/components/ui/bcgogo-noticeDialog.js"></script>

    <script type="text/javascript">
        var interactive = GLOBAL.Interactive;
        $(document).ready(function () {
            var isSessionExpired = GLOBAL.Util.getUrlParameter("sessionExpired");
            if (isSessionExpired != null && isSessionExpired == "true") {
                nsDialog.jAlert("您很久未操作,系统已经自动退出!", null, function () {
                    if (window.parent) {
                        window.parent.location = "login.jsp";
                    }
                    else {
                        window.location = "login.jsp";
                    }
                });
            }
            $("#j_username").bind("keydown", function (event) {
                if (interactive.keyNameFromEvent(event) === "enter") {
                    if ($(this).val() !== "") {
                        $("#j_password").focus().select();
                    }
                }
            });

            $("#j_password").bind("keydown", function (event) {
                if (interactive.keyNameFromEvent(event) === "enter") {
                    if ($("#j_username").val() === "") {
                        $("#j_username").focus().select();
                        return;
                    }

                    setTimeout( function(){
                    $("#input_submit").click();
                    }, 200 );
                }
            });

            //检查用户名，密码是否为空，保存密码，提交表单
            $("#input_submit").bind("click", function (event) {
                if (!$.trim($("#j_username").val())) {
//                    alert("用户名不能为空！");
                    nsDialog.jAlert("请输入您的用户名！", "亲情提示", function(){
                        setTimeout(function(){
                            $("#j_username").focus().select();
                        }, 200);
                    });
                    return;
            }
                if (!$.trim($("#j_password").val())) {
//                    alert("密码不能为空！");
                    nsDialog.jAlert("请输入您的密码！", "亲情提示", function(){
                        setTimeout(function(){
                            $("#j_password").focus().select();
                        }, 200);
                    });
                    return;
            }

                // Ext version ， parent content show side-bar
//                if(window.parent !== window) {
//                    window.parent.Main.layoutPage("login");
//                }
                $("#loginForm").submit();
            });
        });
    </script>
</head>

<body>
<%--onsubmit="return false;"--%>
<form name="loginForm" id="loginForm" method="post" action="j_spring_security_check">
    <div class="main">
        <!--头部-->
        <div class="top">
            <div class="top_name">统购后台管理系统</div>
            <div class="top_right"><span></span></div>
        </div>
        <!--头部结束-->
        <div class="body">
            <div class="login_bg">
                <h1>统购信息后台管理系统</h1>
                <span></span>

                <div class="txt_name">
                    <div class="name_left"></div>
                    <input type="text" name="j_username" id="j_username" class="txtLogin linLogin"/>

                    <div class="name_right"></div>
                    <div class="clear"></div>
                </div>
                <div class="txt_name clear">
                    <div class="name_left"></div>
                    <input type="password" name="j_password" id="j_password" class="txtLogin linLogin"/>

                    <div class="name_right"></div>
                    <div class="clear"></div>
                </div>

                <div class="txt_name clear">
                    <select name="loginType">
                        <option value="crm">CRM</option>
                        <option value="admin">Admin</option>
                    </select>
                </div>
                <input type="button" id="input_submit" class="btnLogin" value="登陆"/>
            </div>
        </div>
    </div>
</form>
</body>
</html>
