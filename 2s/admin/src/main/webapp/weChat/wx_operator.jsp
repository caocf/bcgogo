<%--
  Created by IntelliJ IDEA.
  User: ndong
  Date: 14-9-28
  Time: 下午4:24
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>后台管理系统——微信管理</title>
    <link rel="stylesheet" type="text/css" href="styles/dataMaintenance.css"/>
    <link rel="stylesheet" type="text/css" href="styles/backstage.css"/>
    <link rel="stylesheet" type="text/css" href="styles/backAgent.css"/>
    <link rel="stylesheet" type="text/css" href="styles/storesMange.css"/>
    <link rel="stylesheet" type="text/css" href="styles/style.css"/>
    <%@include file="/WEB-INF/views/style-thirdpartLibs.jsp"%>

    <%@include file="/WEB-INF/views/script-thirdpartLibs.jsp"%>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery.validate.js"></script>
    <%@include file="/WEB-INF/views/script-common.jsp"%>
    <script type="text/javascript" src="js/bcgogo.js"></script>
    <script type="text/javascript" src="js/wxArticle.js"></script>
    <script type="text/javascript" src="js/mask.js"></script>
    <script type="text/javascript">
        $(function(){

            $("#createMenuBtn").click(function(){
                _doCall(function(){
                    APP_BCGOGO.Net.asyncGet({
                        url: 'weChat.do?method=createMenu',
                        data:{
                            pass:$("#operatorPass").val()
                        },
                        dataType: "json",
                        success: function (result) {
                            if(!result||!result.success){
                                alert(result.msg);
                                return;
                            }
                            nsDialog.jAlert(result.msg,null,function(){
                                window.location.reload();
                            });
                        }
                    });
                });
            });



            $("#synUserDTOBtn").click(function(){
                _doCall(function(){
                    APP_BCGOGO.Net.asyncGet({
                        url: 'weChat.do?method=synUserDTOList',
                        dataType: "json",
                        data:{
                            pass:$("#operatorPass").val()
                        },
                        success: function (result) {
                            if(!result||!result.success){
                                alert(result.msg);
                                return;
                            }
                            nsDialog.jAlert(result.msg,null,function(){
                                window.location.reload();
                            });
                        }
                    });
                });

            });

            $("#buildWXImageLibBtn").click(function(){
                _doCall(function(){
                    APP_BCGOGO.Net.asyncGet({
                        url: 'weChat.do?method=buildWXImageLib',
                        data:{
                            pass:$("#operatorPass").val()
                        },
                        dataType: "json",
                        success: function (result) {
                            if(!result||!result.success){
                                alert(result.msg);
                                return;
                            }
                            nsDialog.jAlert(result.msg,null,function(){
                                window.location.reload();
                            });
                        }
                    });
                });
            });

            $("#clearMemBtn").click(function(){
                _doCall(function(){
                    APP_BCGOGO.Net.asyncGet({
                        url: 'weChat.do?method=clearMemCache',
                        data:{
                            pass:$("#operatorPass").val()
                        },
                        dataType: "json",
                        success: function (result) {
                            if(!result||!result.success){
                                alert(result.msg);
                                return;
                            }

                            nsDialog.jAlert(result.msg,null,function(){
                                window.location.reload();
                            });
                        }
                    });
                });
            });


            function _doCall(fun){
                $("#operatorPassDiv").dialog({
                    title:"prompt",
                    width:300,
                    height:180,
                    modal: true,
                    open:function(){
                        $("#operatorPass").val("")
                    },
                    buttons: {
                        "ok": function() {
                            if($(this).attr("lock")) return;
                            $(this).attr("lock","lock");
                            fun();
                        } ,
                        "cancel": function() {
                            $("#operatorPassDiv").dialog("close");
                        }
                    }
                });
            }

        });
    </script>
</head>
<body>

<div id="operatorPassDiv">
    <div>

    </div>
    <div style="margin-top: 15px">
        <span>operator pass:</span> <input id="operatorPass" type="text" />
    </div>

</div>


<div class="main">
    <%@include file="/WEB-INF/views/header.jsp" %>
    <div class="body">
        <%@include file="/WEB-INF/views/left.jsp" %>
        <div class="bodyRight">
            <div class="rightText">
                <div class="textLeft"></div>
                <div class="textBody"><input type="text" value="店铺名" id="txt_shopName"/></div>
                <div class="textRight"></div>
            </div>
            <div class="rightText">
                <div class="textLeft"></div>
                <div class="textBody"><input type="text" value="店主" id="txt_shopOwner"/></div>
                <div class="textRight"></div>
            </div>
            <div class="rightText">
                <div class="textLeft"></div>
                <div class="textAddressbody"><input type="text" value="地址" id="txt_address"/></div>
                <div class="textRight"></div>
            </div>
            <div class="rightText">
                <div class="textLeft"></div>
                <div class="textBody"><input type="text" value="手机/电话" id="txt_phone"/></div>
                <div class="textRight"></div>
            </div>
            <input type="button" class="rightSearch" value="搜 索"/>
            <div class="rightMain clear">
                <%@include file="/weChat/wxNav.jsp"%>
                <div style="height: 500px">
                    <div style="margin-top: 20px"><input type="button" id="createMenuBtn" value="生成菜单" /> </div>
                    <div style="margin-top: 20px"><input type="button" id="synUserDTOBtn" value="同步用户" /> </div>
                    <div style="margin-top: 20px"><input type="button" id="buildWXImageLibBtn" value="生成微信图库" /> </div>
                    <div style="margin-top: 20px"><input type="button" id="clearMemBtn" value="clearMemCache" /> </div>
                    <%--<div style="margin-top: 20px"><input type="button" id="encryptAccountBtn" value="加密账户" /> </div>--%>
                </div>
            </div>
        </div>
    </div>

</body>
</html>