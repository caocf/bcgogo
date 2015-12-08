<%--
@description web 登陆页面， 现在不做用户名、密码cookie自定义存储， 使用默认浏览器存储功能
 --%>
<%@ page import="com.bcgogo.config.ConfigController" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="org.springframework.security.core.AuthenticationException" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>Bcgogo</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <%
        String path = request.getContextPath();
        String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";

        // authentication failed
        AuthenticationException authenticationFailed = (AuthenticationException) session.getAttribute("SPRING_SECURITY_LAST_EXCEPTION");
    %>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/head<%=ConfigController.getBuildVersion()%>.css">
    <%--<link rel="stylesheet" href="<%=basePath%>styles/style<%=ConfigController.getBuildVersion()%>.css"/>--%>
    <link rel="stylesheet" href="js/extension/jquery/plugin/jquery-ui/themes/redmond/jquery-ui-1.8.21.custom.css"/>
    <link rel="stylesheet" type="text/css" href="styles/loginCheck<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" href="js/extension/jquery/plugin/tipsy-master/src/stylesheets/tipsy.css"/>
    <style type="text/css">
        .icon_QQchat {
            background: url(images/qq.png) no-repeat;
            float: left;
            width: 13px;
            height: 16px;
            cursor: pointer;
            margin-bottom: 10px;
            margin-top: 5px;
        }
    </style>
    <script type="text/javascript" src="js/extension/jquery/jquery-1.4.2.min.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery-ui/ui/jquery-ui-1.8.21.custom.min.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/tipsy-master/src/javascripts/jquery.tipsy.min.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery.cookie-min.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery.url.min.js"></script>
    <script type="text/javascript" src="js/base<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/application<%=ConfigController.getBuildVersion()%>.js"></script>

    <script type="text/javascript" src="js/module/bcgogo-noticeDialog<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/module/bcgogo-WebStorage<%=ConfigController.getBuildVersion()%>.js"></script>

    <script type="text/javascript" src="js/components/ui/bcgogo-kissFocus<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/components/ui/bcgogo-qqInvoker<%=ConfigController.getBuildVersion()%>.js"></script>

    <script type="text/javascript" src="js/md5<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/utils/fingerprint<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/mask<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
	<script type="text/javascript" src="js/page/client<%=ConfigController.getBuildVersion()%>.js"></script>

    <script language="javascript" type="text/javascript">
        ;
        (function () {
            // 只能输 英文、数字、中文
            function filterUsername(s) {
                return s.replace(/[^a-zA-Z_\d\u4e00-\u9fa5]+/g, "");
            }

            function filterUserName($node, s) {
                if ($node.val() !== s) $node.val(s);
            }

            /**
             * @description 检测是否符合提交条件, 是：则提交数据 ，否：则等待用户输入
             *                按照顺序检测 username, password， 一旦检测到某项值为空 ，则获取此项焦点， 并等待用户输入。
             *                如果都不为空，顺利通过检测，则触发提交数据函数
             */
            function runLoginStrategy(task) {
                if (task === "username") {
                    if (G.isEmpty($("#j_username").val())) {
                        // TODO show notice to input "username"
                        ;
                    } else {
                        $("#j_password").focus().select().select();
                    }
                } else if (task === "password") {
                    if (G.isEmpty($("#j_password").val())) {
                        // TODO show notice to input "password"
                        ;
                    } else if (G.isEmpty($("#j_username").val())) {
                        $("#j_username").focus().select().select();
                    } else {
                        $("#input_submit").click();
                    }
                }
            }


            //==============
            // event listener handlers
            //==============
            function addUserNameInputTextListener() {
                $("#j_username")
                        .bind("keyup", function (e) {
                            filterUserName($(this), filterUsername($(this).val()));

                            if (GLOBAL.Interactive.keyNameFromEvent(e) === "enter") {
                                runLoginStrategy("username");
                            }
                        })
                        .bind("blur", function (e) {
                            filterUserName($(this), filterUsername($(this).val()));
                        });
            }

            function addPassWordInputTextListener() {
                $("#j_password").bind("keyup", function (e) {
                    if (GLOBAL.Interactive.keyNameFromEvent(e) === "enter") {
                        runLoginStrategy("password");
                    }
                });
            }

            function addSubmitButtonListener() {
                $("#input_submit").bind("click", function (e) {
                    if (!$("#j_username").val()) {
                        nsDialog.jAlert("用户名不能为空！");
                        return false;
                    }
                    if (!$("#j_password").val()) {
                        nsDialog.jAlert("密码不能为空！");
                        return false;
                    }
                    $.cookie("username", $.trim($("#j_username").val()), {"expires": 14});
                    $("#loginForm").submit();
                });
            }

            function addResetPasswordLinkListener() {
                $(".l_password > a").bind("click", function (e) {
                    var userName = $.trim($("#j_username").val());
                    $("#iframe_PopupBox")[0].src = "userreset.do?method=resetPasswordInit&username=" + userName;
                    $("#iframe_PopupBox").css('display', '');

                    Mask.Login();
                    // get (width|height) params
                    var screenWidth = parseFloat(GLOBAL.Interactive.W.getWidth());
                    var screenHeight = parseFloat(GLOBAL.Interactive.W.getHeight());
                    var panelWidth = parseFloat($("#iframe_PopupBox").width());

                    $("#iframe_PopupBox").css('left', parseInt((screenWidth - panelWidth) / 2) + "px");
                });
            }

            $(document).ready(function () {
                defaultStorage.clear();
                $(".pop_close,.pop_button").bind("click", function () {
                    $(".pop-upbox").css("display", "none");
                });

                // === 事件添加 ===
                addUserNameInputTextListener();
                addPassWordInputTextListener();
                addSubmitButtonListener();
                addResetPasswordLinkListener();

                var isDenied = GLOBAL.Util.getUrlParameter("permissionFlag"),
                        isSessionExpired = GLOBAL.Util.getUrlParameter("sessionExpired");
//				if (isDenied != null && isDenied == "true") {
//					nsDialog.jAlert("您没有权限！");
//				}
                bcClient.loginSplicing();

                if (isSessionExpired != null && isSessionExpired == "true") {
                    nsDialog.jAlert("您很久未操作,系统已经自动退出!", null, function () {
                        $.cookie("excludeFlowName", null);
                        $.cookie("currentStepName", null);
                        $.cookie("currentFlowName", null);
                        $.cookie("currentStepStatus", null);
                        $.cookie("nextStepName", null);
                        $.cookie("currentStepIsHead", null);
                        $.cookie("url", null);
                        $.cookie("hasUserGuide", null);
                        $.cookie("isContinueGuide", null);
                        $.cookie("keepCurrentStep", null);
                        $.cookie("clientUrl", null);
                        defaultStorage.clear();
                        if (window.parent) {
                            window.parent.location = "login.jsp";
                        }
                        else {
                            window.location = "login.jsp";
                        }
                    });
                }
            });


        })();
        function showOrHidden(withType){
            $("#dimensional").css("display",withType);
            $("#sticksIsSelected").css("display",withType);
        }

        $(function () {
            $(".pop-upbox").css("display", "none");
            <%-- Logic of showing authentication failed  --%>
            <% if (authenticationFailed != null) { %>
            <%--nsDialog.jAlert("<%= authenticationFailed.getMessage()%>");--%>
            $("#<%= authenticationFailed.getMessage()%>").css("display", "block");

            <% } %>
            $("#input_register").click(function () {
                window.open(encodeURI("shopRegister.do?method=registerShopInfo"));
            });


            $("#appIsSelected").mouseenter(function(){
                $("#appIsSelected").css('background','url(<%=basePath%>images/up_03.png) no-repeat 95% 11px #fff');
                $("#appIsSelected").css("color","#000");
                showOrHidden("block");
            });
            $("#appIsSelected").mouseleave(function(){
                showOrHidden("none");
            });
            $("#dimensional").mouseenter(function(){
                $("#appIsSelected").css('background','url(<%=basePath%>images/up_03.png) no-repeat 95% 11px #fff');
                $("#appIsSelected").css("color","#000");
                showOrHidden("block");

            });
            $("#dimensional").mouseleave(function(){
                $("#appIsSelected").css('background','url(<%=basePath%>images/down_03.png) no-repeat 95% 11px');
                $("#appIsSelected").css("color","");
                showOrHidden("none");

            });
            //generate canvas finger
            $("#finger").val(new Fingerprint({canvas: true,hasher:hex_md5}).get());
            // init QQTalk
            (function ($qq) {
                var qqInvoker = new App.Module.QQInvokerStatic();
                qqInvoker.init($qq);

                $qq.tipsy({
                    "title":"data-tipsy",
                    "gravity":"s",
                    "offset":0,
                    "html":true
                });
            }($(".icon_QQchat")));
        });

    </script>
</head>



<body class="blueBg">
<form name="loginForm" id="loginForm" method="post" action="j_spring_security_check">
    <input type="hidden" name="finger" id="finger"/>
    <div class="m_topMain">
        <div class="l_top">
            <div class="l_topBorder"></div>
            <a target="_blank" href="http://www.bcgogo.com">
                <div class="home"></div></a>
            <div class="l_topBorder"></div>
            <div class="l_topTitle">感谢使用一发软件</div>

            <div class="l_topTitle">
                <a style="color: #BEBEBE;width:180px;" href="http://mail.bcgogo.com:8088/client32/" target="_blank">客户端下载</a>
            </div>

            <div class="l_topRight">
                <div class="mobileVersion hover" id="appIsSelected">APP下载</div>
                <div class="mobileVersion-relative">
                    <div class="mobileVersion-absolute" id="dimensional" style="display:none">
                        <ul>
                            <li><img src="<%=basePath%>/images/gsm_app_down/gsm_app_200x200.png" width="67" height="67"/><br />
                                扫一扫，下载APP<br/>
                              <%--  <a href="#" class="blue_color">下载安桌版</a><br /><a href="#" class="blue_color">下载苹果版</a></li>--%>
                        </ul>
                    </div>
                </div>
                <div class="l_topBorder" id="sticksIsSelected" style="display:none"></div>
            </div>
            <div class="l_topBorder"></div>
            <div style="float:left; width:70px; text-align:center; line-height:27px; cursor:pointer;">
                <a href="http://www.bcgogo.com/industrynews.htm" style=" color:#BEBEBE;display:none" target="_blank">公告中心</a>
            </div>
            <div class="l_topBorder"></div>
        </div>

    </div>

    <div class="loginbody">
        <%--<div class="gonggao">--%>
            <%--<ul>--%>
                <%--<li>--%>
                    <%--<p class="gonggao_title">公告</p>--%>
                    <%--<p>--%>
                        <%--<b>一发软件目前已经打通和微信的互通功能<br />--%>
                            <%--车主添加微信之后：<br /></b>--%>
                        <%--　1、会员信息会到车主微信上<br />--%>
                        <%--　2、账单会发送到车主微信<br />--%>
                        <%--　3、每月几次的营销广告发送<br />--%>
                        <%--　4、全面取代短信功能，会员卡功能，省钱省时省力<br />--%>
                        <%--　5、车主点评每次服务帮助提升管理<br />--%>
                        <%--<b>车主通过微信，可以获取：</b><br />--%>
                        <%--　违章查询，会员卡消费信息，电子账单等<br />--%>
                        <%--　具体内容请点击链接 &nbsp;&nbsp;<a href="http://wenku.baidu.com/view/c719d97e0b4e767f5acfceb8.html"  target="_blank" class="gonggao_title" style="text-decoration:underline" >详情</a><br />--%>
                        <%--电话：0512-66733331 &nbsp;&nbsp; 客服QQ：800060787--%>
                            <%--<span class="icon_QQchat" data-tipsy="<span style='font-size:14px;'>联系我们</span>"  style="cursor: pointer;float:right;margin-right:38px;margin-top:1px"> </span>--%>

                    <%--</p>--%>
                <%--</li>--%>
            <%--</ul>--%>
        <%--</div>--%>

    <div class="l_login">
        <div class="l_table">
            <div class="l_name">
                <span>用户名</span>
                <input type="text" name="j_username" id="j_username" kissfocus='on'/>
            </div>
            <div class="height"></div>
            <div class="l_name">
                <span>密　码</span>
                <input type="password" name="j_password" id="j_password"/>
            </div>
            <div class="l_password">
                <label for="input_rememberPassword">
                    <input id="input_rememberPassword" type="checkbox" onFocus="this.blur()" autocomplete="off"/>
                    记住密码
                </label>
                <!-- <a> 元素已绑定 click 事件  -->
                <a class="white_col" href="javascript:void(0);" onFocus="this.blur()" style="float: right">重置密码？</a>
            </div>
            <input type="hidden"  name="client-url" id="client-url">
            <div class="height"></div>
            <div class="l_btn">
                <input id="input_submit" type="button" onFocus="this.blur()"/>
                <div style="float:left;position:absolute;margin-left:104px;margin-top:16px;display:inline;">
                    <a class="icon_QQchat"
                       style="width:110px;background:none;float:left;display:block;margin-left: 0px;margin-top: 0;margin-bottom: 0;"
                       data-tipsy="<span style='font-size:14px;'>联系我们</span>">
                        <img src="js/components/themes/res/qq/qq_img_abs_unanonymity.png" style="width:100%;"/>
                    </a>
                </div>
            </div>
        </div>
    </div>
        </div>
    <div class="copyright">版本3.0 | 版权所有(c) 2012 -苏州统购信息科技有限公司，保留所有权利。</div>
    <div style="color: #BEBEBE;margin: 0px auto 0;position: relative; width: 420px;">
        <div style="float: left;line-height: 27px; width: 320px;text-align: center;margin-left: 12px;">
            客服电话：0512-66733331 &nbsp;&nbsp;

        </div>
    </div>
</form>

<div id="mask" style="display:block;position: absolute;">
</div>

<iframe id="iframe_PopupBox" style="position:absolute;z-index:5; left:400px; top:150px; display: none;"
        allowtransparency="true" width="600px" height="300px" frameborder="0" src=""></iframe>


<div class="pop-upbox" id="userRoleWrong" style="display:none;">
    <a class="pop_close"></a>
    <span class="pop_iconAccountbanned"></span>

    <div class="pop_info" style=" width:205px; margin-left:0px;">
        <label class="pop_title">您好！您的账号未分配角色</label>

        <div class="pop_tel">
            <label class="pop_line">请联系您店铺的管理员！</label>

            <div class="height"></div>
            <a class="pop_button">我知道了</a>
        </div>
    </div>
</div>

<div id="shopForbid" class="pop-upbox" style="display:none;">
    <a class="pop_close"></a>
    <span class="pop_iconAccountbanned"></span>

    <div class="pop_info" style=" width:205px; margin-left:0px;">
        <label class="pop_title">您好,您的账户已被管理员禁用</label>

        <div class="pop_tel">
            <label class="pop_line">请联系您店铺的管理员！</label>

            <div class="height"></div>
            <a class="pop_button">我知道了</a>
        </div>
    </div>
</div>


<div class="pop-upbox" style="display:none;" id="loginPermission">
    <a class="pop_close"></a>
    <span class="pop_iconExpired"></span>

    <div class="pop_info">
        <label class="pop_title">您好！您没有店铺登陆权限</label>

        <div class="pop_tel">
            <label class="pop_line">详情请联系客服！</label>
            <span class="pop_line">客服热线：</span>

            <div class="telephone"><span>0512-66733331</span></div>
            <a class="pop_button">我知道了</a>
        </div>
    </div>
</div>


<div class="pop-upbox" style="display:none;" id="deleteShop">
    <a class="pop_close"></a>
    <span class="pop_iconExpired"></span>

    <div class="pop_info">
        <label class="pop_title">您好！您的店铺已经无效</label>

        <div class="pop_tel">
            <label class="pop_line">详情请联系客服！</label>
            <span class="pop_line">客服热线：</span>

            <div class="telephone"><span>0512-66733331</span></div>
            <a class="pop_button">我知道了</a>
        </div>
    </div>
</div>

<div class="pop-upbox" style="display:none;" id="shopNull">
    <a class="pop_close"></a>
    <span class="pop_iconExpired"></span>

    <div class="pop_info">
        <label class="pop_title">您好！您的店铺不存在</label>

        <div class="pop_tel">
            <label class="pop_line">详情请联系客服！</label>
            <span class="pop_line">客服热线：</span>

            <div class="telephone"><span>0512-66733331</span></div>
            <a class="pop_button">我知道了</a>
        </div>
    </div>
</div>

<div class="pop-upbox" style="display:none;" id="shopOverdue">
    <a class="pop_close"></a>
    <span class="pop_iconExpired"></span>

    <div class="pop_info">
        <label class="pop_title">您好！您的软件试用期已过</label>

        <div class="pop_tel">
            <label class="pop_line">无法正常使用！详情请联系客服！</label>
            <span class="pop_line">客服热线：</span>

            <div class="telephone"><span>0512-66733331</span></div>
            <a class="pop_button">我知道了</a>
        </div>
    </div>
</div>

<div id="passwordWrong" class="pop-upbox" style="display:none;">
    <a class="pop_close"></a>
    <span class="pop_iconUsername"></span>

    <div class="pop_info">
        <label class="pop_title">您好！您输入的密码错误！</label>

        <div class="pop_tel">
            <label class="pop_line">请重新输入或联系客服！</label>
            <span class="pop_line">客服热线：</span>

            <div class="telephone"><span>0512-66733331</span></div>
            <a class="pop_button">我知道了</a>
        </div>
    </div>
</div>

<div id="adminForbid" class="pop-upbox" style="display:none;">
    <a class="pop_close"></a>
    <span class="pop_iconShopsbanned"></span>

    <div class="pop_info">
        <label class="pop_title">您好,您的店铺账户已被禁用！</label>

        <div class="pop_tel">
            <label class="pop_line">详情请联系客服！</label>
            <span class="pop_line">客服热线：</span>

            <div class="telephone"><span>0512-66733331</span></div>
            <a class="pop_button">我知道了</a>
        </div>
    </div>
</div>

<div id="userNoWrong" class="pop-upbox" style="display:none;">
    <a class="pop_close"></a>
    <span class="pop_iconUsername"></span>

    <div class="pop_info">
        <label class="pop_title">您好！您输入的用户名错误，</label>

        <div class="pop_tel">
            <label class="pop_line">请重新输入或联系客服！</label>
            <span class="pop_line">客服热线：</span>

            <div class="telephone"><span>0512-66733331</span></div>
            <a class="pop_button">我知道了</a>
        </div>
    </div>
</div>

<div class="pop-upbox" id="arrears" style="display: none;">
    <a class="pop_close"></a>
    <span class="pop_iconArrears"></span>

    <div class="pop_info">
        <label class="pop_title">您好！您的软件费用还未缴清，</label>

        <div class="pop_tel">
            <label class="pop_line">无法正常使用！请联系客服尽快缴费哦！</label>
            <span class="pop_line">客服热线：</span>

            <div class="telephone"><span>0512-66733331</span></div>
            <a class="pop_button">我知道了</a>
        </div>
    </div>
</div>

</body>
</html>
