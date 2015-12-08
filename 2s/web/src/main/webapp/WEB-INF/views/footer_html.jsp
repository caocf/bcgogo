<%--
  Created by IntelliJ IDEA.
  User: jinyuan
  Date: 13-6-20
  Time: 下午4:09
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="org.springframework.security.core.AuthenticationException" %>
<!----------------------------页脚----------------------------------->
<div class="J-gap-footer"></div>
<div class="height"></div>
<div class="footer">
    <div class="footer_line footer_first">
        <a href="user.do?method=createmain">首&nbsp;页</a>&nbsp;|&nbsp;<a href="http://www.bcgogo.com" target="_blank">关于我们</a>&nbsp;|&nbsp;<a href="help.do?method=toHelper">帮助中心</a>
    </div>
    <div class="footer_line">
        版权所有(c) 2012 -苏州统购信息科技有限公司，保留所有权利。
    </div>
</div>

<script type="text/javascript">
    $(document).ready(function() {
        var $gapFooter = $(".J-gap-footer");

        $gapFooter.css({
            "margin":0,
            "padding":0,
            "display":"block",
            "clear":"both",
            "float":"none",
            "position":"relative",
            "background":"none",
            "border":"none"
        });

        var $body = $(document.body),
            $window = $(window),
            $doc = $(document);

        $body.css("position", "absolute");

        var heightListener = setInterval(function() {
            var gap = $window.height() - $body.height();
            var gapFooterHeight = $gapFooter.height();

            var height = gap + gapFooterHeight;
            height = height < 0 ? 0 : height;

            $gapFooter.height(height);
        }, 500)
    });


</script>
<%--sesion过期后登陆弹出框--%>
<div class="b_login" id="loginDiv" style="display:none;">
    <form name="loginForm" id="loginForm" method="post" action="j_spring_security_check">
        <div class="cancel_relative">
            <div class="login_cancel"></div>
        </div>
    <div class="b_table">
        <div class="b_name">
            <span>用户名</span><input type="hidden" id="lastUserNo" name="lastUserNo"/>
            <input type="text" kissfocus="on" id="j_username" name="j_username">
            <div class="tex_wrong" id="userNoWrong" style="display:none;">
                用户名错误！
            </div>
        </div>
        <div class="b_name" style="margin-bottom:1px;">
            <span>密&#12288;码</span>
            <input type="password" id="j_password" name="j_password">
            <div class="tex_wrong" id="passwordWrong" style="display:none;">
                密码错误！
            </div>
        </div>
        <input type="hidden" id="isSameUser" name="isSameUser">
        <div class="b_btn">
            <input type="button" onfocus="this.blur()" id="input_submit">
        </div>
    </div>
    </form>
</div>


