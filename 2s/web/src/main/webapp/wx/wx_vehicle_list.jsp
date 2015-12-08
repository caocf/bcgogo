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
    <title>我的车辆</title>
    <script language="javascript" type="text/javascript">
        $(function () {
            $(".j_appVehicle_item").click(function () {
                var openId = $("#openId").val();
                var appUserNo = $(this).find(".appUserNo").val();
                var url = "/web/mirror/vehicle/"+openId+"/"+appUserNo ;
                window.location.href = url
            });
        });
    </script>
</head>
<body>
<input type="hidden" id = "openId"  value="${openId}">
<div id="wrapper" class="t-wp">
<section class="content">
<div class="trajectory">
<c:forEach items="${appVehicleDTOList}" var="appVehicleDTO" varStatus="status">
<div class="j_appVehicle_item illegal_li">
    <input type="hidden" class="appUserNo" value="${appVehicleDTO.appUserNo}">
    <div class="line">
        <div class="w50 fl border_r">
            <div class="w50 fl">车辆型号</div>
            <div class="w50 fl grey_txt tr padding_r">${appVehicleDTO.vehicleBrand}${appVehicleDTO.vehicleModel}
            </div>
        </div>
        <div class="w50 fr">
            <div class="w50 fl padding_l">车牌号码</div>
            <div class="w50 fl grey_txt tr">${appVehicleDTO.vehicleNo}

            </div>
        </div>
        <div class="clr"></div>
    </div>
    <div class="line">
        <div class="w50 fl border_r">
            <div class="w50 fl">保养周期</div>
            <div class="w50 fl grey_txt tr padding_r">${appVehicleDTO.maintainPeriod}km
            </div>
        </div>

        <div class="w50 fr">
            <div class="w50 fl padding_l">当前里程</div>
            <div class="w50 fl grey_txt tr">${appVehicleDTO.currentMileage}km
            </div>
        </div>
        <div class="clr"></div>
    </div>
    <div class="line">
        <div class="w50 fl border_r">
            <div class="w50 fl">下次保养</div>
            <div class="w50 fl grey_txt tr padding_r">${appVehicleDTO.nextMaintainTimeStr}
            </div>
        </div>
        <div class="w50 fr">
            <div class="w50 fl padding_l">上次里程</div>
            <div class="w50 fl grey_txt tr">${appVehicleDTO.lastMaintainMileage}km
            </div>
        </div>
        <div class="clr"></div>
    </div>
    <div class="line">
        <div class="w50 fl border_r">
            <div class="w50 fl">下次验车</div>
            <div class="w50 fl grey_txt padding_r tr">${appVehicleDTO.nextExamineTimeStr}
            </div>
        </div>
        <div class="w50 fr">
            <div class="w50 fl padding_l">下次里程</div>
            <div class="w50 fl grey_txt tr">${appVehicleDTO.nextMaintainMileage}km

            </div>
        </div>
        <div class="clr"></div>
    </div>

    <div class="line">
        <div class="w50 fl border_r">
            <div class="w50 fl">违章查询</div>
            <div class="w50 fl grey_txt tr padding_r">${appVehicleDTO.juheCityName}
            </div>
        </div>
        <div class="w50 fr">
            <div class="w50 fl padding_l">发动机号</div>
            <div class="w50 fl grey_txt tr"> ${appVehicleDTO.engineNo}
            </div>
        </div>
        <div class="clr"></div>
    </div>
    <div class="line">
        <div class="w50 fl border_r">
            <div class="w50 fl">车险公司</div>
            <div class="w50 fl grey_txt tr padding_r">${appVehicleDTO.insuranceCompanyName}
            </div>
        </div>
        <div class="w50 fr">
            <div class="w50 fl padding_l">当前油价</div>
            <div class="w50 fl grey_txt tr">${appVehicleDTO.gasoline_price}

            </div>
        </div>
        <div class="clr"></div>
    </div>
    <div class="line">
        <div class="w30 fl">保养4S店</div>
        <div class="w70 fr grey_txt">${appVehicleDTO.shopName}</div>
        <div class="clr"></div>
    </div>
    <div class="line">
        <div class="w30 fl">紧急联系</div>
        <div class="w70 fr grey_txt">${appVehicleDTO.mobile}</div>
        <div class="clr"></div>
    </div>
    <div class="line">
        <div class="w30 fl">设备IMEI</div>
        <div class="w70 fr grey_txt">${appVehicleDTO.imei}</div>
        <div class="clr"></div>
    </div>
    <div class="line">
        <div class="w30 fl">车架号</div>
        <div class="w70 fr grey_txt">${appVehicleDTO.vehicleVin}</div>
        <div class="clr"></div>
    </div>
</div>
</c:forEach>
</div>
<div class="statistical">当前共有${appVehicleDTONum}台车辆</div>
</section>
</div>
</body>
</html>
