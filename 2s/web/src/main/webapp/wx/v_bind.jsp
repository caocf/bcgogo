<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="com.bcgogo.config.ConfigController" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="org.springframework.security.core.AuthenticationException" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>车辆绑定</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no" />
    <%
        response.setHeader("Cache-Control", "no-cache"); //HTTP 1.1
        response.setHeader("Pragma", "no-cache"); //HTTP 1.0
        response.setDateHeader("Expires", 0); //prevents caching at the proxy server
    %>
    <link rel="stylesheet" type="text/css" href="/web/styles/wechat<%=ConfigController.getBuildVersion()%>.css">
    <script type="text/javascript" src="/web/js/extension/jquery/jquery-1.4.2.min.js"></script>
    <script type="text/javascript" src="/web/js/mobile/mBase<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="/web/js/components/ui/bcgogo-wait-mask<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="/web/js/mobile/weChat<%=ConfigController.getBuildVersion()%>.js"></script>
    <script language="javascript" type="text/javascript">

        function cityBind() {
            var result = $.ajax({
                type: "POST",
                url: "/web/shop.do?method=selectarea",
                data: {"parentNo": $("#provinceNo").val()},
                dataType: "json",
                async:false,
                success: function(result){
                    if (!result || result.length == 0) return;
                    $("#cityNo option").remove();
                    var option = $("<option>")[0];
                    option.value ="";
                    option.innerHTML = "--城市--";
                    $("#cityNo")[0].appendChild(option);
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
                url: "/web/shop.do?method=selectarea",
                data: {"parentNo":"1"},
                dataType: "json" ,
                async:false,
                success: function(result){
                    if (!result || result.length == 0) return;
                    $("#provinceNo option").remove();
                    var option = $("<option>")[0];
                    option.value ="";
                    option.innerHTML = "--省份--";
                    $("#provinceNo")[0].appendChild(option);
                    for (var i = 0, l = result.length; i < l; i++) {
                        var option = $("<option>")[0];
                        option.value = result[i].no;
                        option.innerHTML = result[i].name;
                        option.style.color = "#000000";
                        $("#provinceNo")[0].appendChild(option);
                    }
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

            $("#vBindBtn").click(function(){
                var mask=APP_BCGOGO.Module.waitMask;
                mask.login({dev:"wx"});
                var vehicleNo=$("#vehicleNo").val();
                if(!vehicleNo){
                    $("#errorMsg").text("抱歉，您输入的车牌号为空，请检查后重新输入。");
                    mask.open();
                    return;
                }
                var vin=$("#vinNo").val();
                var uVehicleId=$("#uVehicleId").val();
                var engineNo=$("#engineNo").val();
                var pType=$("#page_type").val();
                var province=$("#provinceNo").val();
                var city=$("#cityNo").val();
                var url="/web/weChat.do?method=sBind";
                if(pType=="EDIT"){
                    url="/web/weChat.do?method=edit";
                }
                $.ajax({
                    type: "POST",
                    url:url,
                    data: {
                        id:uVehicleId,
                        openId:$("#openId").val(),
                        vehicleNo:vehicleNo,
                        vin:vin,
                        engineNo:engineNo,
                        province:province,
                        city:city
                    },
                    dataType: "json",
                    success: function(result){
                        if(!result.success){
                            $("#errorMsg").text(result.msg);
                            mask.open();
                            return;
                        }
                        var weChat=new APP_BCGOGO.Module.WeChat();
                        weChat.closeWindow();
                    },
                    error:function(){
                        mask.open();
                    }
                });

            });

            $("#vUnBindBtn").click(function(){
                var mask=APP_BCGOGO.Module.waitMask;
                mask.login();
                var vehicleNo=$("#vehicleNo").val();
                if(!vehicleNo){
                    mask.open();
                    $("#errorMsg").text("抱歉，您输入的车牌号为空，请检查后重新输入。");
                    return;
                }
                $.ajax({
                    type: "POST",
                    url: "/web/weChat.do?method=unBind",
                    data: {
                        openId:$("#openId").val(),
                        vehicleNo:vehicleNo
                    },
                    dataType: "json",
                    success: function(result){
                        if(!result.success){
                            mask.open();
                            $("#errorMsg").text(result.msg);
                            return;
                        }
                        var weChat=new APP_BCGOGO.Module.WeChat();
                        weChat.closeWindow();
                    }
                });

            });



        });
    </script>
</head>
<body>
<div id="expressbox">
    <input type="hidden" id="openId" value="${openId}" autocomplete="off"/>
    <input type="hidden" id="page_type"  value="${p_type}"/>

    <input type="hidden" id="uVehicleId" value="${uVehicleId}"/>
    <input type="hidden" id="province" value="${userVehicleDTO.province}"/>
    <input type="hidden" id="city" value="${userVehicleDTO.city}"/>
    <div class="exline clearfix">
        <label>车牌号<span class="red_txt">*</span></label>
        <div class="labelright">
            <input  id="vehicleNo" value="${userVehicleDTO.vehicleNo}" name="vehicleNo" placeholder="车牌号"
                    class="roundinput upper-case" type="text" style="width:93%;">
        </div>
    </div>
    <div class="exline clearfix">
        <label>交管局</label>
        <div class="labelright" id="orgright">
            <select name="lstype" id="provinceNo">
                <option value="">--省份--</option>
            </select>
            <select name="lstype" id="cityNo">
                <option value="">--城市--</option>
            </select>
        </div>
    </div>
    <div class="exline clearfix">
        <input  id="vinNo" name="vin" value="${userVehicleDTO.vin}" placeholder="车架号/车辆识别码/VIN" class="roundinput e-input" type="text">
    </div>
    <div class="exline clearfix">
        <input  id="engineNo" value="${userVehicleDTO.engineNo}" name="engineNo" placeholder="发动机号" class="roundinput e-input" type="text">
    </div>
    <div class="mtop5">

        <label id="errorMsg" class="error-msg"></label>
        <c:choose>
            <c:when test="${p_type=='BIND'}">
                <input name="query" id="vBindBtn" value="绑定" class="btn" type="button">
            </c:when>
            <c:otherwise>
                <input name="query" id="vBindBtn" value="绑定" class="btn" type="button" style="width: 42%">
                <input name="query" id="vUnBindBtn" value="解绑" class="btn" type="button" style="width: 42%">
            </c:otherwise>
        </c:choose>
    </div>

    <div class="note_h2">备注：车架号，发动机号均在您的行驶证上。</div>
    <div class="vehicle_license"></div>
</div>
</body>
</html>