<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="com.bcgogo.config.ConfigController" %>
<%--
  Created by IntelliJ IDEA.
  User: ndong
  Date: 2015-4-22
  Time: 17:08
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="apple-mobile-web-app-status-bar-style" content="black">
    <meta content="telephone=no,email=no" name="format-detection">
    <link rel="stylesheet" type="text/css" href="/web/styles/base_mirror<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="/web/styles/drive_mirror<%=ConfigController.getBuildVersion()%>.css"/>
    <script type="text/javascript" src="/web/js/extension/jquery/jquery-1.4.2.min.js"></script>
    <script type="text/javascript" src="/web/js/mobile/mBase<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="/web/js/mobile/weChat<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript"
            src="/web/js/components/ui/bcgogo-wait-mask<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript"
            src="/web/js/wx/wx_mirror_appoint<%=ConfigController.getBuildVersion()%>.js"></script>
    <%--<script type="text/javascript"--%>
            <%--src="/web/js/checkMobileAndTelphone<%=ConfigController.getBuildVersion()%>.js"></script>--%>
    <%--<script type="text/javascript"--%>
            <%--src="/web/js/enterPhone<%=ConfigController.getBuildVersion()%>.js"></script>--%>
    <%--<script type="text/javascript"--%>
            <%--src="/web/js/application<%=ConfigController.getBuildVersion()%>.js"></script>--%>
    <script language="javascript" type="text/javascript">
        $(function () {
            $(".j_appoint_item").click(function () {
                var mask = APP_BCGOGO.Module.waitMask;
                mask.login({dev: "wx"});

                var appUserNo = $("#vehicleNoSelect").val();
                var shopId = $("#shopId").val();
                var vehicleNo = $("#vehicleNo").val();
                var appointDate = $("#appointDate").val();
                var mobile = $("#mobile").val();
                if ("" == appointDate) {
                    mask.open();
                    $("#errorMsg").text("请选择预约日期");
                    return;
                }
                appointDate = appointDate.replace("/", "-").replace("/", "-");
                var appointTime = $("#appointTime").val();
                if ("" == appointTime) {
                    mask.open();
                    $("#errorMsg").text("请选择预约时间");
                    return;
                }
                if ("" == mobile) {
                    mask.open();
                    $("#errorMsg").text("请填好联系手机号码方便联系");
                    return;
                }

                var isMobile = /^1\d{10}$/;

                if (!isMobile.test(mobile)) {
                    mask.open();
                    $("#errorMsg").text("联系手机号码格式不正确");
                    return;
                }
                appointDate += " " + appointTime;
                var appServiceDTO = {
                    shopId: shopId,
                    vehicleNo: vehicleNo,
                    userNo: appUserNo,
                    contact: $("#contact").val(),
                    mobile: $("#mobile").val(),
                    serviceCategoryId: $("#serviceCategoryId").val(),
                    openId: $("#openId").val(),
                    appointTimeStr: appointDate,
                    remark: $("#remark").val()
                };
                $.ajax({
                    type: "POST",
                    url: "/web/mirror/saveAppoint",
                    data: appServiceDTO,
                    dataType: "json",
                    async: false,
                    success: function (result) {
                        if (!result.success) {
                            $("#errorMsg").text(result.msg);
                            mask.open();
                            return;
                        }
                        var openId = $("#openId").val();
                        var appUserNo = $("#appUserNo").val();
                        var url = "/web/mirror/myAppoint/" + openId +"/"+appUserNo ;
                        window.location.href = url
                    }
                });
            });

        });
        $(function () {
            $(".j_my_appoint_item").click(function () {
                var openId = $("#openId").val();
                var appUserNo = $("#appUserNo").val();
                var url = "/web/mirror/myAppoint/" + openId+"/"+appUserNo;
                window.location.href = url
            });

        });

        function checkMobile(mobile) {
            //先通过正则验证
            if(APP_BCGOGO.Validator.stringIsMobilePhoneNumber(mobile)){
                //如果是多个号码，再进行重复判断
                if(mobile.indexOf(",")!=-1){
                    var mobileArray = mobile.split(",");
                    for(var i=0;i<mobileArray.length-1;i++){
                        if(mobileArray[i]==(mobileArray[i+1])){
                            alert("存在相同的手机号，请确认和重新输入！");
                            return false;
                        }
                    }
                }
                return true;
            }else{
                return false;
            }
        }
    </script>
    <link href="/web/js/extension/jquery/plugin/mobiscroll/css/mobiscroll.widget.css" rel="stylesheet"
          type="text/css"/>
    <link href="/web/js/extension/jquery/plugin/mobiscroll/css/mobiscroll.scroller.css" rel="stylesheet"
          type="text/css"/>
    <script src="/web/js/extension/jquery/jquery-1.11.0.min.js"></script>

    <script src="/web/js/extension/jquery/plugin/mobiscroll/mobiscroll.core.js"></script>
    <script src="/web/js/extension/jquery/plugin/mobiscroll/mobiscroll.widget.js" type="text/javascript"></script>
    <script src="/web/js/extension/jquery/plugin/mobiscroll/mobiscroll.scroller.js" type="text/javascript"></script>
    <script src="/web/js/extension/jquery/plugin/mobiscroll/i18n/mobiscroll.i18n.zh.js"
            type="text/javascript"></script>
    <script src="/web/js/extension/jquery/plugin/mobiscroll/mobiscroll.util.datetime.js"
            type="text/javascript"></script>
    <script src="/web/js/extension/jquery/plugin/mobiscroll/mobiscroll.datetime.js" type="text/javascript"></script>

    <script type="text/javascript">
        var $11 = jQuery.noConflict(true);
    </script>
    <script type="text/javascript">
        $11(function () {
            $11("#appointDate").mobiscroll().date({
                lang: 'zh',
//                dateFormat: 'yyyy-MM-dd',
                display: 'bottom'
            });
        });
        $11(function () {
            $11("#appointTime").mobiscroll().time({
                lang: 'zh',
                mode: 'scroller',
                display: 'bottom'
            });
        });

        //车辆下拉列表onchange事件
        function alert(a) {
            var openId = $("#openId").val();
            var appUserNo = a;
            var url = "/web/mirror/2Appoint/" + openId + "/" + appUserNo;
            window.location.href = url
        }
    </script>
    <title>在线预约</title>
</head>
<body>
<input type="hidden" id="openId" value="${openId}" autocomplete="off"/>
<input type="hidden" id="appUserNo" value="${appUserNo}" autocomplete="off"/>
<input type="hidden" id="shopId" value="${shopId}" autocomplete="off"/>
<input type="hidden" id="vehicleNo" value="${vehicleNo}" autocomplete="off"/>

<div id="wrapper">
    <header id="header">
        <div class="selected_bg">
            <select id="vehicleNoSelect" onchange="alert(this.value);">
                <c:forEach items="${appWXUserDTOs}" var="appUserDTO" varStatus="status">
                    <option value="${appUserDTO.appUserNo}">${appUserDTO.vehicleNo}</option>
                </c:forEach>
            </select>
            <span class="char_white"></span></div>
        <a class="j_my_appoint_item light_blue" style="float:right; margin-top:6px;">我的预约</a>

        <div class="clr"></div>
    </header>
    <section class="content">
        <div class="trajectory">
            <div class="illegal_li">
                <div class="line">
                    <div class="w30 fl">服务店面</div>
                    <div class="w70 grey_txt fr tr">${shopName}</div>
                    <div class="clr"></div>
                </div>
                <div class="line">
                    <div class="w30 fl">车牌号</div>
                    <div class="w70 grey_txt fr tr">${vehicleNo}</div>
                    <div class="clr"></div>
                </div>
                <div class="line">
                    <div class="w30 fl">服务类型<span class="red_txt">*</span></div>
                    <div class="w70 grey_txt fr tr">请选择
                            <span class="select select-area">
                               <select id="serviceCategoryId">
                                   <option value="10000010001000002">保养</option>
                                   <option value="10000010001000001">维修</option>
                                   <option value="10000010002000001">美容</option>
                               </select>
                            </span>
                    </div>
                    <div class="clr"></div>
                </div>
                <div class="line">
                    <div class="w30 fl">预约日期<span class="red_txt">*</span></div>
                    <div class="w70 grey_txt fr tr">
                        <p>
                    <span class="select select-area" style="width:29%;margin-bottom:5px;float: right;margin-top:6px; ">
                       <input id="appointDate" value="${date}" placeholder="选日期" class="j_reg_date"
                              type="text" style="width:100%;">
                    </span>
                        </p>
                    </div>
                    <div class="clr"></div>
                </div>
                <div class="line">
                    <div class="w30 fl">预约时间<span class="red_txt">*</span></div>
                    <div class="w70 grey_txt fr tr">
                        <p>
                    <span class="select select-area" style="width:29%;margin-bottom:5px;float: right;margin-top:6px; ">
                           <input id="appointTime" value="${time}" placeholder="选时间" class="j_reg_date"
                                  type="text" style="width:100%">
                    </span>
                        </p>
                    </div>
                    <div class="clr"></div>
                </div>

                <div class="line">
                    <div class="w30 fl">联系人</div>
                    <div class="w70 grey_txt fr tr"><input id="contact" placeholder="请输入联系人"
                                                           style="width:35%;float: right;margin-top:6px;  "
                                                           autocomplete="off"></div>
                    <div class="clr"></div>
                </div>
                <div class="line">
                    <div class="w30 fl">联系手机号码<span class="red_txt">*</span></div>
                    <div class="w70 grey_txt fr tr"><input id="mobile" placeholder="请输入联系手机"
                                                           style="width:40%;float: right;margin-top:6px;  "
                                                           autocomplete="off"></div>
                    <div class="clr"></div>
                </div>
                <div class="line">
                    <div class="w30 fl">备注</div>

                    <div class="clr"></div>
                </div>
                <div class="line">
                    <div class="w30 fl"><textarea id="remark" placeholder="请输入备注内容" autocomplete="off"
                                                  style="width:333%"></textarea></div>
                    <div class="clr"></div>
                </div>
            </div>
            <div class="sub-b-p" style="text-align:center"><label id="errorMsg" class="red_txt"></label></div>
            <div class="j_appoint_item add_btn">提交</div>
        </div>
    </section>
</div>
</body>
</html>
