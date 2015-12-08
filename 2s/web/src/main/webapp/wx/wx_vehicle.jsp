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
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="apple-mobile-web-app-status-bar-style" content="black">
    <meta content="telephone=no,email=no" name="format-detection">
    <link rel="stylesheet" type="text/css" href="/web/styles/base_mirror<%=ConfigController.getBuildVersion()%>.css" />
    <link rel="stylesheet" type="text/css" href="/web/styles/drive_mirror<%=ConfigController.getBuildVersion()%>.css" />
    <script type="text/javascript" src="/web/js/extension/jquery/jquery-1.4.2.min.js"></script>
    <script type="text/javascript" src="/web/js/mobile/mBase<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript"
            src="/web/js/components/ui/bcgogo-wait-mask<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="/web/js/mobile/weChat<%=ConfigController.getBuildVersion()%>.js"></script>
    <script language="javascript" type="text/javascript">
        //已处理
        $(function () {

            $(".j_vehicle_item_back").click(function () {
                var openId = $("#openId").val();
                var url = "/web/mirror/vehicleList/"+openId ;
                window.location.href = url
            });


            $(".j_vehicle_item").click(function () {
                var appUserNo = $("#appUserNo").val();
                var maintainPeriod = $("#maintainPeriod").val();
                var mobile = $("#mobile").val();
                var nextMaintainMileage = $("#nextMaintainMileage").val();
                var nextMaintainTimeStr = $("#nextMaintainTimeStr").val();
                var nextExamineTimeStr = $("#nextExamineTimeStr").val();
                var vehicleId = $("#vehicleId").val();
                var mask = APP_BCGOGO.Module.waitMask;
                mask.login({dev: "wx"});
                if (""==maintainPeriod) {
                    mask.open();
                    $("#errorMsg").text("请填写保养周期");
                    return;
                }
                if (""==mobile) {
                    mask.open();
                    $("#errorMsg").text("请填写紧急联系方式");
                    return;
                }
                var re = /^1[35]\d{9}$/i;

                if (!re.test(mobile)) {
                    mask.open();
                    $("#errorMsg").text("紧急联系手机号码格式不正确");
                    return;
                }
                if (""==nextMaintainMileage) {
                    mask.open();
                    $("#errorMsg").text("请填写下次保养里程");
                    return;
                }
                if (""==nextMaintainTimeStr) {
                    mask.open();
                    $("#errorMsg").text("请选择下次保养时间");
                    return;
                }
                if (""==nextExamineTimeStr) {
                    mask.open();
                    $("#errorMsg").text("请选择下次验车时间");
                    return;
                }


                var appVehicleDTO = {
                    appUserNo: appUserNo,
                    maintainPeriod: maintainPeriod,
                    mobile: mobile,
                    nextMaintainMileage: nextMaintainMileage,
                    nextMaintainTimeStr: nextMaintainTimeStr,
                    nextExamineTimeStr: nextExamineTimeStr,
                    vehicleId:vehicleId
                };
                $.ajax({
                    type: "POST",
                    url: "/web/mirror/updateAppVehicleDTO",
                    data: appVehicleDTO,
                    dataType: "json",
                    async: false,
                    success: function (result) {
                        if (!result.success) {
                            $("#errorMsg").text(result.msg);
                            mask.open();
                            return;
                        }
                        var openId = $("#openId").val();
                        var url = "/web/mirror/vehicleList/"+openId ;
                        window.location.href = url
                    }
                });
            });
        });
    </script>
    <title>修改车辆</title>
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
            $11("#nextMaintainTimeStr").mobiscroll().date({
                lang: 'zh',
//                dateFormat: 'yyyy-MM-dd',
                display: 'bottom'
            });
        });
        $11(function () {
            $11("#nextExamineTimeStr").mobiscroll().date({
                lang: 'zh',
//                dateFormat: 'yyyy-MM-dd',
                display: 'bottom'
            });
        });
    </script>
</head>
<body>
<input type="hidden" id = "appUserNo" class="appUserNo" value="${appUserNo}">
<input type="hidden" id = "vehicleId" class="vehicleId" value="${vehicleId}">
<input type="hidden" id = "openId"  value="${openId}">
<div id="wrapper">
    <section class="content">
        <div style="margin:-50px auto auto auto; " class="trajectory">
            <div class="illegal_li">
                <div class="line">
                    <div class="w30 fl">车辆品牌</div>
                    <div class="w70 fr tr">${appVehicleDTO.vehicleBrand}
                                                  </div>
                    <div class="clr"></div>
                </div>
                <div class="line">
                    <div class="w30 fl">车辆型号</div>
                    <div class="w70 fr tr">${appVehicleDTO.vehicleModel}
                                                  </div>
                    <div class="clr"></div>
                </div>
                <div class="line">
                    <div class="w30 fl">车牌号码</div>
                    <div class="w70 fr tr">${appVehicleDTO.vehicleNo}
                                                 </div>
                    <div class="clr"></div>
                </div>
                <div class="line">
                    <div class="w30 fl">当前油价</div>
                    <div class="w70 fr tr">${appVehicleDTO.gasoline_price}
                    </div>
                    <div class="clr"></div>
                </div>
                <div class="line">
                    <div class="w30 fl">当前里程</div>
                    <div class="w70 fr tr">${appVehicleDTO.currentMileage}&nbsp;km
                                                 </div>
                    <div class="clr"></div>
                </div>
                <div class="line">
                    <div class="w30 fl">上次保养里程</div>
                    <div class="w70 fr tr">${appVehicleDTO.lastMaintainMileage}&nbsp;km
                                                 </div>
                    <div class="clr"></div>
                </div>
                <%--<div class="line">--%>
                    <%--<div class="w30 fl">保养里程周期</div>--%>
                    <%--<div class="w70 fr tr"><span  style="margin-bottom:5px; margin-top:6px; "><input id="maintainPeriod"  value="${appVehicleDTO.maintainPeriod}" style="width:10%;"--%>
                                                  <%--autocomplete="off">&nbsp;km</span></div>--%>
                    <%--<div class="clr"></div>--%>
                <%--</div>--%>
                <div class="line">
                    <div class="w30 fl">保养里程周期</div>
                    <div class="w70 fr tr"><span class="select select-area" style="margin-bottom:5px;"><input id="maintainPeriod"  value="${appVehicleDTO.maintainPeriod}"
                                                                                                              style="width:20%" autocomplete="off">&nbsp;km</span></div>
                    <div class="clr"></div>
                </div>
                <div class="line">
                    <div class="w30 fl">紧急联系方式</div>
                    <div class="w70 fr tr"><span class="select select-area" style="margin-bottom:5px; "><input id="mobile"  value="${appVehicleDTO.mobile}" style="width:35%; margin-top:6px;"
                                                  autocomplete="off"></span></div>
                    <div class="clr"></div>
                </div>

                <div class="line">
                    <div class="w30 fl">下次保养里程</div>
                    <div class="w70 fr tr"><span class="select select-area" style="margin-bottom:5px;"><input id="nextMaintainMileage"  value="${appVehicleDTO.nextMaintainMileage}"
                                                 style="width:20%" autocomplete="off">&nbsp;km</span></div>
                    <div class="clr"></div>
                </div>

                <div class="line">
                    <div class="w30 fl">下次保养时间</div>
                    <div class="w70 fr tr"><span class="select select-area" style="width:33%;margin-bottom:5px;float: right;margin-top:6px;  ">
                       <input id="nextMaintainTimeStr" value="${appVehicleDTO.nextMaintainTimeStr}" placeholder="选日期" class="j_reg_date"
                              type="text" style="width:90%;">
                    </span></div>
                    <div class="clr"></div>
                </div>


                <div class="line">
                    <div class="w30 fl">下次验车时间</div>
                    <div class="w70 fr tr"><span class="select select-area" style="width:33%;margin-bottom:5px;float: right;margin-top:6px;  ">
                       <input id="nextExamineTimeStr" value="${appVehicleDTO.nextExamineTimeStr}" placeholder="选日期" class="j_reg_date"
                              type="text" style="width:90%;">
                    </span></div>
                    <div class="clr"></div>
                </div>
                <div class="line">
                    <div class="w30 fl">后视镜imei号</div>
                    <div class="w70 fr tr">${appVehicleDTO.imei}
                                                  </div>
                    <div class="clr"></div>
                </div>
                <div class="line">
                    <div class="w30 fl">发动机号</div>
                    <div class="w70 fr tr">${appVehicleDTO.engineNo}
                                                  </div>
                    <div class="clr"></div>
                </div>
                <div class="line">
                    <div class="w30 fl">车架号</div>
                    <div class="w70 fr tr">${appVehicleDTO.vehicleVin}
                                                 </div>
                    <div class="clr"></div>
                </div>
            </div>
        </div>
        <div class="sub-b-p" style="text-align:center"><label id="errorMsg" class="red_txt"></label></div>
        <div class="statistical">
            <div class="j_vehicle_item btn">保存</div>
            <div class="j_vehicle_item_back btn">取消</div>
        </div>
    </section>
</div>
</body>
</html>
