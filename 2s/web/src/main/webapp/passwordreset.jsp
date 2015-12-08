<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>密码重置</title>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/up<%=ConfigController.getBuildVersion()%>.css"/>
    <script type="text/javascript" src="js/extension/jquery/jquery-1.4.2.min.js"></script>
    <script type="text/javascript" src="js/base<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/application<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/module/bcgogo-noticeDialog<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/mask<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">
        $(document).ready(function () {
            $("#div_close,#cancel").click(function () {
                window.parent.document.getElementById("mask").style.display = "none";
                window.parent.document.getElementById("iframe_PopupBox").style.display = "none";
                window.parent.document.getElementById("iframe_PopupBox").src = "";
//            window.parent.addHandle(document.getElementById('div_drag'), window);
                try {
                    $(window.parent.document.body).find("input[type='button']").eq(0).focus().blur();
                } catch(e) {
                    ;
                }
        });
        });
    </script>
</head>
<body>
<form name="frm" action="userreset.do?method=resetPassword" method="post">
    <div class="tab_repay tab_password">
        <div class="i_arrow"></div>
        <div class="i_upLeft"></div>
        <div class="i_upCenter">
            <div class="i_note" id="div_drag">密码重置</div>
            <div class="i_close" id="div_close"></div>
        </div>
        <div class="i_upRight"></div>
        <div class="i_upBody">
            <div class="boxContent">
                <div>
                    用　户　名：<input type="text" id="username" name="username" value="${username}"/><a id="sendVerCode"  class="blue_col"
                        href="javascript:void(0);" onclick="sendVerCode();">点击发送验证码</a>
                </div>
                <div>填写验证码：<input type="text" id="vercode" name="vercode"/></div>
                <div>新　密　码：<input type="password" id="password" name="password"/>（数字，大小写字母）</div>
                <div>确认密码 &nbsp;：<input type="password" id="repass" name="repass"/></div>
            </div>
            <div class="passwordContent">（验证码30分钟内有效，如未收到，请联系BCGOGO客服0512-66733331）</div>
            <div class="sure">
                <input type="button" onfocus="this.blur();" value="确定" onclick="frmsubmit();">
                <input type="button" onfocus="this.blur();" value="取消" id="cancel">
            </div>
        </div>
        <div class="i_upBottom">
            <div class="i_upBottomLeft"></div>
            <div class="i_upBottomCenter"></div>
            <div class="i_upBottomRight"></div>
        </div>
    </div>
</form>
</body>
<%@ include file="/common/messagePrompt.jsp" %>
<script>
    function sendVerCode() {
        document.getElementById("sendVerCode").onclick = "";
        $(document.getElementById("sendVerCode")).removeClass("blue_col").addClass("sendVerCode");
        setTimeout(function(){
            document.getElementById("sendVerCode").onclick = sendVerCode;
            $(document.getElementById("sendVerCode")).removeClass("sendVerCode").addClass("blue_col");
        },60*1000);
        var userName = $.trim($("#username").val());
        if (!userName) {
            showMessage.fadeMessage("25%", "24%", "slow", 3000, "用户名不能为空！");
            return;
        }
        $.ajax({
            type:"POST",
            url:"userreset.do?method=sendVerCode",
            data:{username:userName},
            async:true,
            cache:false,
            dataType:"text",
            success:function (text) {
                showMessage.fadeMessage("25%", "24%", "slow", 3000, text);
            },
            error:function (e) {
                nsDialog.jAlert(e);
                showMessage.fadeMessage("25%", "24%", "slow", 3000, "发送验证码失败！\n\n请拨打客服电话。");
            }
        });
    }


    function frmsubmit() {
        var userName = $("#username").val().replace(/(^\s*)|(\s*$)/g, "");
        var vercode = $("#vercode").val().replace(/(^\s*)|(\s*$)/g, "");
        var password = $("#password").val().replace(/(^\s*)|(\s*$)/g, "");
        var repass = $("#repass").val().replace(/(^\s*)|(\s*$)/g, "");

        if (!userName) {
            showMessage.fadeMessage("25%", "24%", "slow", 3000, "请输入用户名！");     // top left fadeIn fadeOut message
            return;
        }
        if (!vercode) {
            showMessage.fadeMessage("25%", "24%", "slow", 3000, "请输入验证码！");
            return;
        }
        if (!password) {
            showMessage.fadeMessage("25%", "24%", "slow", 3000, "请输入新密码！");
            return;
        }
        if (!repass) {
            showMessage.fadeMessage("25%", "24%", "slow", 3000, "请再次输入密码！");
            return;
        }
        if (password != repass) {
            showMessage.fadeMessage("25%", "24%", "slow", 3000, "两次密码不一致！");
            return;
        }
        document.frm.submit();
    }
    if ("" != "${info}") {
        if ("${info}" == 1) {
//     showMessage.fadeMessage("25%","24%", "slow", 3000, "密码设置成功!请重新登陆!");
            nsDialog.jAlert("密码设置成功!请重新登陆!",null,function(){
            $('#mask', parent.document).css('display', 'none');
            $('#iframe_PopupBox', parent.document).css('display', 'none');
            $('#iframe_PopupBox', parent.document)[0].src = '';
            });
        } else {
            showMessage.fadeMessage("25%", "24%", "slow", 3000, "${info}");
        }
    }
</script>
</html>
