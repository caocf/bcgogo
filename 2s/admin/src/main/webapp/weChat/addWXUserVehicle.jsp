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
    <link rel="stylesheet" type="text/css" href="styles/shopIndividuation.css"/>
    <%@include file="/WEB-INF/views/style-thirdpartLibs.jsp"%>

    <%@include file="/WEB-INF/views/script-thirdpartLibs.jsp"%>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery.validate.js"></script>
    <%@include file="/WEB-INF/views/script-common.jsp"%>
    <script type="text/javascript" src="js/bcgogo.js"></script>
    <script type="text/javascript" src="js/wxArticle.js"></script>
    <script type="text/javascript" src="js/mask.js"></script>
    <script type="text/javascript">
        function confirmThis(){
            var vehicleNo = $("#vehicleNo").val();
            var vin = $("#vin").val();
            var engineNo = $("#engineNo").val();
            var provinceNo = $("#provinceNo").val();
            var cityNo = $("#cityNo").val();

            if(vehicleNo==null||vehicleNo==""){
                alert("请输入车牌号！");
                return false;
            }
            if(vin==null||vin==""){
                alert("请输入车架号！");
                return false;
            }
            if(engineNo==null||engineNo==""){
                alert("请输入发动机号！");
                return false;
            }
            if(provinceNo==null||provinceNo==""){
                alert("请输入省！");
                return false;
            }
            if(cityNo==null||cityNo==""){
                alert("请输入市！");
                return false;
            }
            document.getElementById('template_form').submit();
        }

        function chancel(){
            var openId = $("#openId").val();
            window.location.href="weChat.do?method=initWxUserVehicleList&openId="+openId;
        }

        function cityBind() {
            var result = $.ajax({
                type: "POST",
                url: "./backShop.do?method=selectarea",
                data: {"parentNo": $("#provinceNo").val()},
                dataType: "json",
                async:false,
                success: function(result){
                    if (!result || result.length == 0) return;
                    for (var i = 0, l = result.length; i < l; i++) {
                        var option = $("<option>")[0];
                        option.value = result[i].no;
                        option.innerHTML = result[i].name;
                        option.style.color = "#000000";
                        $("#cityNo")[0].appendChild(option);
                    }
                }
            });

        }

        function provinceBind() {
            $.ajax({
                type: "POST",
                url: "./backShop.do?method=selectarea",
                data: {"parentNo":"1"},
                dataType: "json" ,
                async:false,
                success: function(result){
                    if (!result || result.length == 0) return;
                    var option = $("<option>")[0];
//                        option.value = result[i].no;
                    option.innerHTML = "--省--";
                    $("#provinceNo")[0].appendChild(option);
                    for (var i = 0, l = result.length; i < l; i++) {
                        var option = $("<option>")[0];
                        option.value = result[i].no;
                        option.innerHTML = result[i].name;
                        option.style.color = "#000000";
                        $("#provinceNo")[0].appendChild(option);
                    }
                },
                error:function(e){
                     console.debug(e);
                }
            });

        }

        $(function(){
            provinceBind();

            $("#provinceNo").bind("change", function() {
                $("#cityNo option").not(".default").remove();
                cityBind();
//                if ($(this).val() == "--所有省--") {
//                    $(this).css({"color": "#ADADAD"});
//                }else{
                $(this).css({"color": "#000000"});
//                }


            });

            if($("#page_type").val()=="EDIT"&&$("#province").val()){
                $("#provinceNo").val($("#province").val());
                $("#provinceNo").change();
                $("#cityNo").val($("#city").val());
            }

        });
    </script>
</head>
<body>
<div class="main">
    <!--头部-->
    <%@include file="/WEB-INF/views/header.jsp" %>
    <!--头部结束-->
    <div class="body">
        <!--左侧列表-->
        <%@include file="/WEB-INF/views/left.jsp" %>
        <!--左侧列表结束-->
        <!--右侧内容-->
        <div class="bodyRight">
            <!--搜索-->
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
            <!--搜索结束-->
            <!--内容-->
            <div class="rightMain clear">
                <%@include file="/weChat/wxNav.jsp"%>
                <div  id="div_show">
                    <div class="i_arrow"></div>
                    <div class="i_upLeft"></div>
                    <div class="i_upCenter">
                        <div class="" id="div_drag">新增车辆</div>

                    </div>
                    <form:form id="template_form" action="weChat.do?method=addUserVehicle" commandName="WXUserVehicleDTO"
                               method="post" enctype="multipart/form-data" >

                    <div class="i_upRight"></div>
                    <div class="i_upBody">
                        <table cellpadding="0" id="configTable" cellspacing="0" class="configTable">
                            <col width="100">
                            <col  width="100"/>
                            <input type="hidden" id="openId" name="openId" value="${openId}">
                            <tr>
                                <td class="label">车牌号<img src="images/star.jpg"></td>
                                <td><input type="text" id="vehicleNo" name="vehicleNo" style="height:20px;width: 224px"/>
                                </td>
                            </tr>
                            <tr>
                                <td class="label">车架号<img src="images/star.jpg"></td>
                                <td><input type="text" id="vin" name="vin" style="height:20px;width: 224px"/>
                                </td>
                            </tr>
                            <tr>
                                <td class="label">发动机号<img src="images/star.jpg"></td>
                                <td><input type="text" id="engineNo" name="engineNo" style="height:20px;width: 224px"/>
                                </td>
                            </tr>
                            <tr>
                                <td class="label">归属地<img src="images/star.jpg"></td>
                                <td>
                                    <select class="txt" style="color: #ADADAD;" id="provinceNo" name="province">
                                        <option class="default" value="">--所有省--</option>
                                    </select>&nbsp;
                                    <select class="txt" style="color: #ADADAD;" id="cityNo" name="city">
                                        <option class="default" value="">--所有市--</option>
                                    </select>&nbsp;
                                </td>
                            </tr>

                        </table>
                        <div class="height"></div>
                        <div class="more_his">
                            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                            <input type="button" value="提交" onclick="confirmThis()" class="rightSearch" id="confirmBtn"/>
                            <input type="button" value="取消" onclick="chancel()" class="rightSearch" id="cancleBtn"/>
                        </div>
                    </div>
                </div>
                </form:form>
                <!--内容结束-->
            </div>
            <!--内容结束-->
            <!--圆角-->
            <div class="bottom_crile clear">
                <div class="crile"></div>
                <div class="bottom_x"></div>
                <div style="clear:both;"></div>
            </div>
            <!--圆角结束-->
        </div>
        <!--右侧内容结束-->
    </div>
</div>
<ul class="suggestionMain" style="list-style: none;color:#000000;">
</ul>
<div id="mask" style="display:block;position: absolute;">
</div>
<%--<div id="setLocation"   style="position: fixed; left: 37%; top: 37%;  z-index: 8; display: none;">--%>
<%--<jsp:include page="sms/editSms.jsp"></jsp:include>--%>
<%--</div>--%>
</body>
</html>