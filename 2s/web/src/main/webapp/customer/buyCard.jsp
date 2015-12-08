<%@ page import="com.bcgogo.config.ConfigController" %>
<%@ page import="com.bcgogo.enums.PasswordValidateStatus" %>
<%--
  Created by IntelliJ IDEA.
  User: cfl
  Date: 12-7-9
  Time: 下午2:23
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
<title>购卡/续卡</title>

<link rel="stylesheet" type="text/css" href="styles/up<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/moreUserinfo<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/moreHistory<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="js/extension/jquery/plugin/jquery-tooltip/jquery.tooltip.css"/>

<link rel="stylesheet" type="text/css"
      href="js/extension/jquery/plugin/jquery-ui/themes/redmond/jquery-ui-1.8.21.custom.css"/>
<link rel="stylesheet" type="text/css"
      href="js/extension/jquery/plugin/jquery-ui/themes/redmond/jquery.ui.timepicker-addon.css"/>
<link rel="stylesheet" type="text/css"
      href="js/components/themes/bcgogo-scanning<%=ConfigController.getBuildVersion()%>.css"/>
<style>
    #ui-datepicker-div, .ui-datepicker {
        font-size: 90%;
    }

    #table_productNo {
        border: 1px solid #BBBBBB;
    }

    .userName {
        white-space: nowrap;
        overflow: hidden;
        width: 100px;
        text-overflow: ellipsis;
    }

    .table2 td a {
        background: #ffffff;
    }
</style>

<script type="text/javascript" src="js/extension/json2/json2.min.js"></script>
<script type="text/javascript" src="js/extension/jquery/jquery-1.4.2.min.js"></script>
<script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.min.js"></script>
<script type="text/javascript" src="js/extension/jquery/plugin/jquery.validate.min.js"></script>
<script type="text/javascript"
        src="js/extension/jquery/plugin/jquery-tooltip/jquery.tooltip<%=ConfigController.getBuildVersion()%>.js"></script>

<script type="text/javascript" src="js/utils/tableUtil.js"></script>
<script type="text/javascript" src="js/extension/jquery/plugin/jquery-ui/ui/jquery-ui-1.8.21.custom.min.js"></script>
<script type="text/javascript" src="js/extension/jquery/plugin/jquery-ui/ui/jquery.ui.timepicker-addon.min.js"></script>
<script type="text/javascript"
        src="js/extension/jquery/plugin/jquery-ui/ui/i18n/jquery.ui.datepicker-zh-CN.min.js"></script>
<script type="text/javascript" src="js/extension/jquery/plugin/jquery-ui/ui/jquery.ui.dialog.js"></script>
<script type="text/javascript" src="js/base<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/application<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/basecommon<%=ConfigController.getBuildVersion()%>.js"></script>

<script type="text/javascript" src="js/module/ajaxQuery<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/module/bcgogo-noticeDialog<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/module/bcgogoValidate<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/module/bcgogo-scanning<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/module/bcgogo-WebStorage<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/mask<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/common<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/vehiclenosolrheader<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/bcgogo<%=ConfigController.getBuildVersion()%>.js"></script>

<script type="text/javascript" src="js/uploadPreview.js"></script>

<script type="text/javascript" src="js/buyCard<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript">
var blurFlag = true;
var timeOutId;
jQuery(document).ready(function () {
    $('.userName').tooltip();

    jQuery('.i_upBody').focus();
    jQuery(".addService").live("click", function () {
        var obj = this;
        createDropdownlistDom(this);
        jQuery.ajax({
                    type: "POST",
                    url: "txn.do?method=searchService",
                    async: true,
                    data: {
                        name: $(obj).val(),
                        now: new Date()
                    },
                    cache: false,
                    dataType: "json",
                    error: function (XMLHttpRequest, error, errorThrown) {
                        jQuery("#div_serviceName").css({'display': 'none'});
                    },
                    success: function (jsonObject) {
                        initServiceName(obj, jsonObject);
                    }
                }
        );
    });

    jQuery(".addService").live("blur", function () {
        var obj = this;
        if (!$(obj).val()) {
            blurFlag = false;
            initNoServiceName(obj);
        }
        if (!blurFlag) {
            blurFlag = true;
        }
        else {
            timeOutId = setTimeout(function () {
                APP_BCGOGO.Net.asyncAjax({url: "member.do?method=getServiceByCardServiceCombineMemberService",
                    data: {memberId: jQuery("#memberDTO\\.id").val(), cardId: jQuery("#memberCardOrderItemDTOs0\\.cardId").val(),
                        serviceId: "", serviceName: $(obj).val(), now: new Date()}, dataType: "json", success: function (data) {

                        if (null == data.serviceIdStr || undefined == data.serviceIdStr) {
                            data.serviceIdStr = "";
                        }
                        jQuery("#" + obj.id.split(".")[0] + "\\.serviceId").val(data.serviceIdStr);
                        jQuery("#" + obj.id.split(".")[0] + "\\.serviceName").val(data.serviceName);
                        initOneService(obj, data);
                    }
                });
            }, 200);
        }
    });

    jQuery(".addService").live("keyup", function (e) {
        var obj = this;
        var e = e || event;

        if (e.keyCode == "13" || e.keyCode == "108") {
            jQuery("#div_serviceName").hide();

            if (!$(obj).val()) {
                blurFlag = false;
                initNoServiceName(obj);
            }

            if (!blurFlag) {
                blurFlag = true;
            }
            else {
                APP_BCGOGO.Net.asyncAjax({url: "member.do?method=getServiceByCardServiceCombineMemberService",
                    data: {memberId: jQuery("#memberDTO\\.id").val(), cardId: jQuery("#memberCardOrderItemDTOs0\\.cardId").val(),
                        serviceId: "", serviceName: $(obj).val(), now: new Date()}, dataType: "json", success: function (data) {
                        if (null == data.serviceIdStr || undefined == data.serviceIdStr) {
                            data.serviceIdStr = "";
                        }
                        jQuery("#" + obj.id.split(".")[0] + "\\.serviceId").val(data.serviceIdStr);
                        jQuery("#" + obj.id.split(".")[0] + "\\.serviceName").val(data.serviceName);
                        initOneService(obj, data);
                    }
                });
            }
        }
        else {
            jQuery.ajax({
                        type: "POST",
                        url: "txn.do?method=searchService",
                        async: true,
                        data: {
                            name: $(obj).val(),
                            now: new Date()
                        },
                        cache: false,
                        dataType: "json",
                        error: function (XMLHttpRequest, error, errorThrown) {
                            jQuery("#div_serviceName").css({'display': 'none'});
                        },
                        success: function (jsonObject) {
                            initServiceName(obj, jsonObject);
                        }
                    }
            );
        }
    });

    $("input[id$='balanceTimes']").live("focus", function () {

        jQuery("#div_serviceName").hide();

        var obj = this;

        var jsonStr = [
            {'times': '不限次'},
            {'times': '10'},
            {'times': '20'},
            {'times': '30'},
            {'times': '40'},
            {'times': '50'}
        ];
        initTimes(obj, jsonStr);
    });

    $(".checkDeadLine").live("click", function () {

        var now = new Date();
        var nowDate = now.getFullYear() + "-" + ((now.getMonth() + 1) < 10 ? ("0" + (now.getMonth() + 1)) : (now.getMonth() + 1)) + "-" +
                (now.getDate() < 10 ? ("0" + now.getDate()) : now.getDate());
        jQuery("#div_serviceName").hide();

        var obj = this;

        var oldDeadlineStr = $("#" + obj.id.split(".")[0] + "\\.oldDeadlineStr").val();
        var addTerm = $("#" + obj.id.split(".")[0] + "\\.addTerm").val();
        if (!addTerm) {
            addTerm = 0;
        }
        if (!oldDeadlineStr) {
            oldDeadlineStr = nowDate;
        }
        var jsonStr = [
            {hiddenDeadlineStr: "不限期", deadlineStr: "不限期"},
            {hiddenDeadlineStr: getDeadLine(oldDeadlineStr, 1), deadlineStr: "1 月 (" + getDeadLine(oldDeadlineStr, 1) + ")"},
            {hiddenDeadlineStr: getDeadLine(oldDeadlineStr, 2), deadlineStr: "2 月 (" + getDeadLine(oldDeadlineStr, 2) + ")"},
            {hiddenDeadlineStr: getDeadLine(oldDeadlineStr, 3), deadlineStr: "3 月 (" + getDeadLine(oldDeadlineStr, 3) + ")"},
            {hiddenDeadlineStr: getDeadLine(oldDeadlineStr, 4), deadlineStr: "4 月 (" + getDeadLine(oldDeadlineStr, 4) + ")"},
            {hiddenDeadlineStr: getDeadLine(oldDeadlineStr, 5), deadlineStr: "5 月 (" + getDeadLine(oldDeadlineStr, 5) + ")"},
            {hiddenDeadlineStr: getDeadLine(oldDeadlineStr, 6), deadlineStr: "6 月 (" + getDeadLine(oldDeadlineStr, 6) + ")"},
            {hiddenDeadlineStr: getDeadLine(oldDeadlineStr, 7), deadlineStr: "7 月 (" + getDeadLine(oldDeadlineStr, 7) + ")"},
            {hiddenDeadlineStr: getDeadLine(oldDeadlineStr, 8), deadlineStr: "8 月 (" + getDeadLine(oldDeadlineStr, 8) + ")"},
            {hiddenDeadlineStr: getDeadLine(oldDeadlineStr, 9), deadlineStr: "9 月 (" + getDeadLine(oldDeadlineStr, 9) + ")"},
            {hiddenDeadlineStr: getDeadLine(oldDeadlineStr, 10), deadlineStr: "10月 (" + getDeadLine(oldDeadlineStr, 10) + ")"},
            {hiddenDeadlineStr: getDeadLine(oldDeadlineStr, 11), deadlineStr: "11月 (" + getDeadLine(oldDeadlineStr, 11) + ")"},
            {hiddenDeadlineStr: getDeadLine(oldDeadlineStr, 12), 'deadlineStr': "12月 (" + getDeadLine(oldDeadlineStr, 12) + ")"},
            {hiddenDeadlineStr: '自定义时间', 'deadlineStr': '自定义时间'}
        ];

        initDeadline(obj, jsonStr);
    });

    jQuery("#saleMan").live("click", function () {
        var obj = this;
        jQuery.ajax({
                    type: "POST",
                    url: "member.do?method=getSaleMans",
                    async: true,
                    data: {
                        now: new Date()
                    },
                    cache: false,
                    dataType: "json",
                    error: function (XMLHttpRequest, error, errorThrown) {
                        jQuery("#div_serviceName").css({'display': 'none'});
                    },
                    success: function (jsonObject) {
                        initSaleMan(obj, jsonObject);
                    }
                }
        );
    });

    jQuery("#memberNo").live("click", function () {
        var obj = this;
        jQuery.ajax({
                    type: "POST",
                    url: "member.do?method=getVehiclesAndMobile",
                    async: true,
                    data: {
                        customerId: jQuery("#customerId").val(),
                        now: new Date()
                    },
                    cache: false,
                    dataType: "json",
                    error: function (XMLHttpRequest, error, errorThrown) {
                        jQuery("#div_serviceName").css({'display': 'none'});
                    },
                    success: function (jsonObject) {
                        initMemberNo(obj, jsonObject);
                    }
                }
        );
    });

    jQuery("#executor").live("click", function () {
        var obj = this;
        jQuery.ajax({
                    type: "POST",
                    url: "member.do?method=getExecutor",
                    async: true,
                    data: {
                        now: new Date()
                    },
                    cache: false,
                    dataType: "json",
                    error: function (XMLHttpRequest, error, errorThrown) {
                        jQuery("#div_serviceName").css({'display': 'none'});
                    },
                    success: function (jsonObject) {
                        initExecutor(obj, jsonObject);
                    }
                }
        );
    });

    jQuery(document).click(function (e) {
        var e = e || event;
        var target = e.srcElement || e.target;
        if (target.id != "div_serviceName" && target.id != "Scroller-Container_ServiceName"
                && target.id.indexOf("balanceTimes") == -1 && target.id.indexOf("deadlineStr") == -1) {
            jQuery("#div_serviceName").hide();
        }
    });

    jQuery("#deleteSaleMan").hide();

    jQuery("#deleteExecutor").hide();

    jQuery("#deleteSaleMan").live("click", function () {
        jQuery("#saleMan").val("");
        jQuery("#saleManId").val("");
        jQuery("#deleteSaleMan").hide();
    });

    jQuery("#deleteExecutor").live("click", function () {
        jQuery("#executor").val("");
        jQuery("#executorId").val("");
        jQuery("#deleteExecutor").hide();
    });

    jQuery("#saleManDiv").mouseenter(function () {
        if (jQuery("#saleMan").val()) {
            jQuery("#deleteSaleMan").show();
        }
    });

    jQuery("#executorDiv").mouseenter(function () {
        if (jQuery("#executor").val()) {
            jQuery("#deleteExecutor").show();
        }
    });

    jQuery("#saleManDiv").mouseleave(function () {
        jQuery("#deleteSaleMan").hide();
    });

    jQuery("#executorDiv").mouseleave(function () {
        jQuery("#deleteExecutor").hide();
    });

    jQuery("#memberType").live("click", function () {
        var obj = this;
        createDropdownlistDom(this);
        jQuery.ajax({
                    type: "POST",
                    url: "member.do?method=getMemberType",
                    async: true,
                    data: {
                        now: new Date()
                    },
                    cache: false,
                    dataType: "json",
                    error: function (XMLHttpRequest, error, errorThrown) {
                        jQuery("#div_serviceName").css({'display': 'none'});
                    },
                    success: function (jsonObject) {
                        if (null != jsonObject && null != jsonObject.data && jsonObject.data.length > 0) {
                            initMemberType(obj, jsonObject.data);
                        }

                    }
                }
        );
    });
});

function showDatepicker(node) {
    if ($(node).hasClass("isDatepickerInited")) {
        $(node).removeClass("isDatepickerInited");
        $(node).datepicker({
            "changeYear": true,
            "changeMonth": true,
            "showButtonPanel": true,
            "numberOfMonths": 1,
            "yearRange": "c-100:c+100",
            "showOn": "span",
            "yearSuffix": ""
        });
    }
    $(node).datepicker("show");


    $(node).blur();
}


function initSaleMan(domObject, jsonObject) {
    var offset = jQuery(domObject).offset();
    var offsetHeight = jQuery(domObject).height();
    var offsetWidth = jQuery(domObject).width();
    var domTitle = domObject.name;
    var position = domObject.getBoundingClientRect();
    var selectmore = jsonObject.length;
    createDropdownlistDom(domObject);
    if (selectmore <= 0) {
        jQuery("#div_serviceName").css({'display': 'none'});
    }
    else {
        setDomPosition((domObject.offsetTop + offsetHeight), (domObject.offsetLeft), 80, 200);

        jQuery("#Scroller-Container_ServiceName").html("");

        for (var i = 0; i < jsonObject.length; i++) {
            var id = jsonObject[i].idStr;
            var a = jQuery("<a id=" + id + "></a>");
            jQuery(a).css("color", "#000");
            a.html(jsonObject[i].name + "<br>");
            jQuery(a).bind("mouseover", function () {
                jQuery("#Scroller-Container_ServiceName > a").removeAttr("class");
                jQuery(this).attr("class", "hover");
                selectValue = jsonObject[jQuery("#Scroller-Container_ServiceName > a").index(jQuery(this)[0])].name;// jQuery(this).html();
                selectItemNum = parseInt(this.id.substring(10));
            });

            jQuery(a).click(function () {
                var sty = this.id;

                jQuery("#saleManId").val(sty);

                jQuery(domObject).val(selectValue = jsonObject[jQuery("#Scroller-Container_ServiceName > a").index(jQuery(this)[0])].name); //取的第一字符串

                selectItemNum = -1;
            });

            jQuery("#Scroller-Container_ServiceName").append(a);
        }
    }
}


function initExecutor(domObject, jsonObject) {
    var offset = jQuery(domObject).offset();
    var offsetHeight = jQuery(domObject).height();
    var offsetWidth = jQuery(domObject).width();
    var domTitle = domObject.name;
    var position = domObject.getBoundingClientRect();
    var x = position.left;
    var y = position.top;
    var selectmore = jsonObject.length;
    createDropdownlistDom(domObject);
    if (selectmore <= 0) {
        jQuery("#div_serviceName").css({'display': 'none'});
    }
    else {
        setDomPosition((domObject.offsetTop + offsetHeight), (domObject.offsetLeft), 80, 200);

        jQuery("#Scroller-Container_ServiceName").html("");

        for (var i = 0; i < jsonObject.length; i++) {
            var id = jsonObject[i].idStr;
            var a = jQuery("<a id=" + id + "></a>");
            jQuery(a).css("color", "#000");
            a.html(jsonObject[i].name + "<br>");
            jQuery(a).bind("mouseover", function () {
                jQuery("#Scroller-Container_ServiceName > a").removeAttr("class");
                jQuery(this).attr("class", "hover");
                selectValue = jsonObject[jQuery("#Scroller-Container_ServiceName > a").index(jQuery(this)[0])].name;// jQuery(this).html();
                selectItemNum = parseInt(this.id.substring(10));
            });

            jQuery(a).click(function () {
                var sty = this.id;

                jQuery("#executorId").val(sty);

                jQuery(domObject).val(selectValue = jsonObject[jQuery("#Scroller-Container_ServiceName > a").index(jQuery(this)[0])].name); //取的第一字符串

                selectItemNum = -1;
            });

            jQuery("#Scroller-Container_ServiceName").append(a);
        }
    }
}


function initMemberNo(domObject, jsonObject) {
    var offset = jQuery(domObject).offset();
    var offsetHeight = jQuery(domObject).height();
    var offsetWidth = jQuery(domObject).width();
    var domTitle = domObject.name;
    var position = domObject.getBoundingClientRect();
    var x = position.left;
    var y = position.top;
    var selectmore = jsonObject.length;
    createDropdownlistDom(domObject);
    if (selectmore <= 0) {
        jQuery("#div_serviceName").css({'display': 'none'});
    }
    else {
        setDomPosition(25, x - 11, 100, 200);

        jQuery("#Scroller-Container_ServiceName").html("");

        for (var i = 0; i < jsonObject.length; i++) {
            var a = jQuery("<a></a>");
            jQuery(a).css("color", "#000");
            a.html(jsonObject[i].memberNo + "<br>");
            jQuery(a).bind("mouseover", function () {
                jQuery("#Scroller-Container_ServiceName > a").removeAttr("class");
                jQuery(this).attr("class", "hover");
                selectValue = jsonObject[jQuery("#Scroller-Container_ServiceName > a").index(jQuery(this)[0])].name;// jQuery(this).html();
                selectItemNum = parseInt(this.id.substring(10));
            });

            jQuery(a).click(function () {

                jQuery("#memberNo").val(jQuery(this).text()); //取的第一字符串

                selectItemNum = -1;
            });

            jQuery("#Scroller-Container_ServiceName").append(a);
        }
    }
}

function initServiceName(domObject, jsonObject) {
    var offset = jQuery(domObject).offset();
    var offsetHeight = jQuery(domObject).height();
    var offsetWidth = jQuery(domObject).width();
    var domTitle = domObject.name;

    var position = domObject.getBoundingClientRect();
    var x = position.left;
    var y = position.top;
    var selectmore = jsonObject.length;
    createDropdownlistDom(domObject);
    if (selectmore <= 0) {
        jQuery("#div_serviceName").css({'display': 'none'});
    }
    else {
        setDomPosition((offset.top + $('.i_upBody')[0].scrollTop - 178), x - 11, 130, 200);

        jQuery("#Scroller-Container_ServiceName").html("");

        for (var i = 0; i < jsonObject.length; i++) {
            var id = jsonObject[i].id;
            var a = jQuery("<a id=" + id + "></a>");
            a.html(jsonObject[i].name + "<br>");
            jQuery(a).css("color", "#000");
            jQuery(a).bind("mouseover", function () {
                jQuery("#Scroller-Container_ServiceName > a").removeAttr("class");
                jQuery(this).attr("class", "hover");
                selectValue = jsonObject[jQuery("#Scroller-Container_ServiceName > a").index(jQuery(this)[0])].name;// jQuery(this).html();
                selectItemNum = parseInt(this.id.substring(10));
            });

            jQuery(a).click(function () {
                if (timeOutId) {
                    clearTimeout(timeOutId);
                }
                var sty = this.id;
                jQuery("#" + domObject.id.split(".")[0] + "\\.serviceId").val(sty);
                jQuery("#" + domObject.id.split(".")[0] + "\\.serviceName").val(jQuery(this).text());
                selectItemNum = -1;
                jQuery.ajax({
                            type: "POST",
                            url: "member.do?method=getServiceByCardServiceCombineMemberService",
                            async: true,
                            data: {
                                memberId: jQuery("#memberDTO\\.id").val(),
                                cardId: jQuery("#memberCardOrderItemDTOs0\\.cardId").val(),
                                serviceId: sty,
                                now: new Date()
                            },
                            cache: false,
                            dataType: "json",
                            error: function (XMLHttpRequest, error, errorThrown) {
                                jQuery("#div_serviceName").css({'display': 'none'});
                            },
                            success: function (jsonObject) {
                                blurFlag = false;

                                initOneService(domObject, jsonObject);

                            }
                        }
                );
            });

            jQuery("#Scroller-Container_ServiceName").append(a);
        }
    }
}

function initNoServiceName(domObject) {
    jQuery("#" + domObject.id.split(".")[0] + "\\.serviceId").val("");
    jQuery("#" + domObject.id.split(".")[0] + "\\.oldTimes").val(0);
    jQuery("#" + domObject.id.split(".")[0] + "\\.cardTimes").val(0);
    jQuery("#" + domObject.id.split(".")[0] + "\\.increasedTimes").val(0);
    jQuery("#" + domObject.id.split(".")[0] + "\\.cardTimesStatus").val(0);

    jQuery("#" + domObject.id.split(".")[0] + "\\.serviceName").parent().next().html(0);
    jQuery("#" + domObject.id.split(".")[0] + "\\.serviceName").parent().next().next().html(0);

    jQuery("#" + domObject.id.split(".")[0] + "\\.balanceTimes").val("");

    jQuery("#" + domObject.id.split(".")[0] + "\\.vehicles").val("");

//    jQuery("#" + domObject.id.split(".")[0] + "\\.deadlineStr").prev().attr("checked", true);
    jQuery("#" + domObject.id.split(".")[0] + "\\.deadlineStr").val("");

}

function initOneService(domObject, jsonObject) {
    jQuery("#" + domObject.id.split(".")[0] + "\\.oldTimes").val(jsonObject.oldTimes);
    jQuery("#" + domObject.id.split(".")[0] + "\\.cardTimes").val(jsonObject.cardTimes);
    jQuery("#" + domObject.id.split(".")[0] + "\\.cardTimesStatus").val(jsonObject.cardTimesStatus);
    if (jsonObject.oldTimes * 1 == -1) {
        jQuery("#" + domObject.id.split(".")[0] + "\\.serviceName").parent().next().html("不限次");
    }
    else {
        jQuery("#" + domObject.id.split(".")[0] + "\\.serviceName").parent().next().html(jsonObject.oldTimes);
    }
    if (jsonObject.cardTimes * 1 == -1) {
        jQuery("#" + domObject.id.split(".")[0] + "\\.serviceName").parent().next().next().html("不限次");
    }
    else {
        jQuery("#" + domObject.id.split(".")[0] + "\\.serviceName").parent().next().next().html(jsonObject.cardTimes);
    }

    if (jsonObject.balanceTimes * 1 == -1) {
        jQuery("#" + domObject.id.split(".")[0] + "\\.balanceTimes").val("");
    }
    else {
        jQuery("#" + domObject.id.split(".")[0] + "\\.balanceTimes").val(jsonObject.balanceTimes);
    }
    jQuery("#" + domObject.id.split(".")[0] + "\\.vehicles").val(null == jsonObject.vehicles ? "" : jsonObject.vehicles);

    if (jsonObject.deadline * 1 == -1) {
//        jQuery("#" + domObject.id.split(".")[0] + "\\.deadlineStr").next().next().children(0).attr("checked", true);
        jQuery("#" + domObject.id.split(".")[0] + "\\.deadlineStr").val("不限期");
    }
    else {
//        jQuery("#" + domObject.id.split(".")[0] + "\\.deadlineStr").prev().attr("checked", true);
        jQuery("#" + domObject.id.split(".")[0] + "\\.deadlineStr").val(jsonObject.deadlineStr);
    }
}


function initTimes(domObject, jsonStr) {
    var offset = jQuery(domObject).offset();
    var offsetHeight = jQuery(domObject).height();
    var offsetWidth = jQuery(domObject).width();
    var domTitle = domObject.name;
    var x = G.getX(domObject);
    var y = G.getY(domObject);
    var selectmore = jsonStr.length;
    createDropdownlistDom(domObject);
    if (selectmore <= 0) {
        jQuery("#div_serviceName").css({'display': 'none'});
    }
    else {
        setDomPosition((offset.top + $('.i_upBody')[0].scrollTop - 178), x - 11, 100, 150);

        jQuery("#Scroller-Container_ServiceName").css({

        });
        jQuery("#Scroller-Container_ServiceName").html("");

        for (var i = 0; i < jsonStr.length; i++) {
            var a = jQuery("<a></a>");
            a.html(jsonStr[i].times + "<br>");
            jQuery(a).css("color", "#000");
            jQuery(a).bind("mouseover", function () {
                jQuery("#Scroller-Container_ServiceName > a").removeAttr("class");
                jQuery(this).attr("class", "hover");
                selectValue = jsonStr[jQuery("#Scroller-Container_ServiceName > a").index(jQuery(this)[0])].name;// jQuery(this).html();
                selectItemNum = parseInt(this.id.substring(10));
            });

            jQuery(a).click(function () {
                var sty = jQuery(this).text();
                jQuery(domObject).val(sty)
                selectItemNum = -1;
            });

            jQuery("#Scroller-Container_ServiceName").append(a);
        }
    }
}

function initDeadline(domObject, jsonStr) {
    var offset = jQuery(domObject).offset();
    var offsetHeight = jQuery(domObject).height();
    var offsetWidth = jQuery(domObject).width();
    var domTitle = domObject.name;
    var x = G.getX(domObject);
    var y = G.getY(domObject);
    var selectmore = jsonStr.length;
    createDropdownlistDom(domObject);
    if (selectmore <= 0) {
        jQuery("#div_serviceName").css({'display': 'none'});
    }
    else {
        setDomPosition((offset.top + $('.i_upBody')[0].scrollTop - 178), x - 11, 150, 300);

        jQuery("#Scroller-Container_ServiceName").css({

        });
        jQuery("#Scroller-Container_ServiceName").html("");

        for (var i = 0; i < jsonStr.length; i++) {
            var a = jQuery("<a id=" + jsonStr[i].hiddenDeadlineStr + "></a>");
            a.html(jsonStr[i].deadlineStr + "<br>");
            jQuery(a).css("color", "#000");
            jQuery(a).bind("mouseover", function () {
                jQuery("#Scroller-Container_ServiceName > a").removeAttr("class");
                jQuery(this).attr("class", "hover");
                selectValue = jsonStr[jQuery("#Scroller-Container_ServiceName > a").index(jQuery(this)[0])].name;// jQuery(this).html();
                selectItemNum = parseInt(this.id.substring(10));
            });

            jQuery(a).click(function () {
                var sty = jQuery(this)[0].id;
                if ("自定义时间" == sty) {
                    showDatepicker(domObject);
                }
                else {
                    jQuery(domObject).val(sty);
                }
                selectItemNum = -1;
            });

            jQuery("#Scroller-Container_ServiceName").append(a);
        }
    }
}

function getDeadLine(oldDeadlineStr, n) {
    var oldYear = oldDeadlineStr.substring(0, 4) * 1;
    var oldMonth = oldDeadlineStr.substring(5, 7) * 1;
    var oldDays = oldDeadlineStr.substring(8, 10) * 1;

    var newYear, newMonth, newDays;
    if (oldMonth + n > 12) {
        newYear = oldYear + 1;
        newMonth = oldMonth + n - 12;
    }
    else {
        newYear = oldYear;
        newMonth = oldMonth + n;
    }

    newDays = oldDays;
    switch (newMonth) {
        case 1:
            if (oldDays > 31) newDays = 31;
            break;
        case 2:
            if (oldDays > 29 && newYear % 4 == 0) newDays = 29
            else if (oldDays > 28 && newYear % 4 != 0) newDays = 28;
            break;
        case 3:
            if (oldDays > 31) newDays = 31;
            break;
        case 4:
            if (oldDays > 30) newDays = 30;
            break;
        case 5:
            if (oldDays > 31) newDays = 31;
            break;
        case 6:
            if (oldDays > 30) newDays = 30;
            break;
        case 7:
            if (oldDays > 31) newDays = 31;
            break;
        case 8:
            if (oldDays > 31) newDays = 31;
            break;
        case 9:
            if (oldDays > 30) newDays = 30;
            break;
        case 10:
            if (oldDays > 31) newDays = 31;
            break;
        case 11:
            if (oldDays > 30) newDays = 30;
            break;
        case 12:
            if (oldDays > 31) newDays = 31;
            break;
    }


    return newYear + "-" + (newMonth < 10 ? ("0" + newMonth) : newMonth) + "-" + (newDays < 10 ? ("0" + newDays) : newDays);
}

var setDomPosition = function (_top, _left, _width, _height) {
    jQuery("#div_serviceName").css({
        'display': 'block', 'position': 'absolute',
        'top': _top + 'px',
        'left': _left + 'px',
        'width': _width + 'px',
        'height': _height + 'px',
        overflowY: "scroll",
        overflowX: "hidden"
    });
};
var createDropdownlistDom = function (obj) {
    if ($('#div_serviceName').length > 0) {
        $('#div_serviceName').remove();
    }
    var domService = document.createElement('div');
    domService.id = 'div_serviceName';
    domService.className = 'i_scroll';
    domService.style.cssText = 'display:none;width:150px;';

    var domContainer = document.createElement('div');
    domContainer.id = 'Scroller-Container_ServiceName';
    domContainer.className = 'Scroller-Container';
    domService.appendChild(domContainer);

    if (obj == document) {
        document.body.appendChild(domService);
    }
    else {
        obj.parentNode.appendChild(domService);
    }
};

function initMemberType(domObject, jsonObject) {
    var offset = jQuery(domObject).offset();
    var offsetHeight = jQuery(domObject).height();
    var offsetWidth = jQuery(domObject).width();
    var domTitle = domObject.name;
    var position = domObject.getBoundingClientRect();
    var selectmore = jsonObject.length;
    var x = position.left;
    var y = position.top;
    createDropdownlistDom(domObject);
    if (selectmore <= 0) {
        jQuery("#div_serviceName").css({'display': 'none'});
    }
    else {

        setDomPosition(25, x - 11, 100, 200);

        jQuery("#Scroller-Container_ServiceName").html("");

        for (var i = 0; i < jsonObject.length; i++) {
            var a = jQuery("<a></a>");
            jQuery(a).css("color", "#000");
            a.html(jsonObject[i].name + "<br>");
            jQuery(a).bind("mouseover", function () {
                jQuery("#Scroller-Container_ServiceName > a").removeAttr("class");
                jQuery(this).attr("class", "hover");
                selectValue = jsonObject[jQuery("#Scroller-Container_ServiceName > a").index(jQuery(this)[0])].name;// jQuery(this).html();
                selectItemNum = parseInt(this.id.substring(10));
            });

            jQuery(a).click(function () {

                jQuery(domObject).val(selectValue = jsonObject[jQuery("#Scroller-Container_ServiceName > a").index(jQuery(this)[0])].name); //取的第一字符串

                selectItemNum = -1;
            });

            jQuery("#Scroller-Container_ServiceName").append(a);
        }
    }
}
</script>
</head>
<body class="bodyMain" style="width:740px;">
<form:form commandName="memberCardOrderDTO" id="memberCardOrderForm" action="member.do?method=saveMemberCardOrder"
           method="post" name="thisform" autocomplete="off">
<form:hidden path="memberCardOrderItemDTOs[0].cardId" value="${memberCardDTO.id}"></form:hidden>
<form:hidden path="memberCardOrderItemDTOs[0].percentageAmount" value="${memberCardDTO.percentageAmount}"></form:hidden>
<form:checkbox path="sendMemberMsg" id="sendMemberMsg" cssStyle="display:none"></form:checkbox>
<form:hidden path="memberDTO.id" value="${memberDTO.id}"></form:hidden>
<form:hidden path="customerId" value="${customerId}"></form:hidden>
<form:hidden path="customerName" value="${customerDTO.name}"></form:hidden>
<form:hidden path="memberOrderType"/>
<c:forEach items="${memberDTO.memberServiceDTOs}" var="memberServiceDTO" varStatus="status">
    <form:hidden path="memberDTO.memberServiceDTOs[${status.index}].id" value="${memberServiceDTO.id}"></form:hidden>
    <form:hidden path="memberDTO.memberServiceDTOs[${status.index}].memberId"
                 value="${memberServiceDTO.memberId}"></form:hidden>
    <form:hidden path="memberDTO.memberServiceDTOs[${status.index}].serviceId"
                 value="${memberServiceDTO.serviceId}"></form:hidden>
    <form:hidden path="memberDTO.memberServiceDTOs[${status.index}].consumeType"
                 value="${memberServiceDTO.consumeType}"></form:hidden>
    <form:hidden path="memberDTO.memberServiceDTOs[${status.index}].times"
                 value="${memberServiceDTO.times}"></form:hidden>
    <form:hidden path="memberDTO.memberServiceDTOs[${status.index}].timesStr"
                 value="${memberServiceDTO.timesStr}"></form:hidden>
    <form:hidden path="memberDTO.memberServiceDTOs[${status.index}].deadline"
                 value="${memberServiceDTO.deadline}"></form:hidden>
    <form:hidden path="memberDTO.memberServiceDTOs[${status.index}].deadlineStr"
                 value="${memberServiceDTO.deadlineStr}"></form:hidden>
    <form:hidden path="memberDTO.memberServiceDTOs[${status.index}].serviceName"
                 value="${memberServiceDTO.serviceName}"></form:hidden>
    <form:hidden path="memberDTO.memberServiceDTOs[${status.index}].vehicles"
                 value="${memberServiceDTO.vehicles}"></form:hidden>
    <form:hidden path="memberDTO.memberServiceDTOs[${status.index}].status"
                 value="${memberServiceDTO.status}"></form:hidden>
</c:forEach>
<div class="i_supplierInfo more_supplier i_buyCards">
<div class="i_arrow"></div>
<div class="i_upLeft"></div>
<div class="i_upCenter i_two">
    <div class="i_note more_title" id="div_buycard">购卡/续卡</div>
    <div class="i_close" id="div_close"></div>
</div>
<div class="i_upRight"></div>
<div class="i_upBody" style="position:absolute;top:30px;width: 741px">
<table cellpadding="0" cellspacing="0" class="table3 supplierTable">
    <col width="180"/>
    <col width="180"/>
    <col width="210"/>
    <col/>
    <tr>
        <td class="clearfix">
            <div class="fl">客　户　名:</div>
            <div class="fl userName" title='${customerDTO.name}'>${customerDTO.name}</div>
        </td>
        <td colspan="2">会　员(卡)号:<input type="text" style="width:100px;" class="tab_input" id="memberNo"
                                       value="${memberDTO.memberNo}" name="memberDTO.memberNo" maxlength="25"
                                       autocomplete="off"/>（刷卡/手输卡号/车牌号/手机号）
        </td>
        <input type="hidden" id="oldMemberNo" value="${memberDTO.memberNo}">
            <%--<td>卡 密 码:<input type="password" style="width:100px;" class="tab_input" value="${memberDTO.password}" name="memberDTO.password" id="password" autocomplete="off"/></td>--%>
            <%--<td>手　机　号:<input type="text" style="width:80px;" class="tab_input" id="mobile" name="mobile" autocomplete="off"/></td>--%>
        <td>会 员 级 别:
            <div class="cardType"><form:input path="memberType" readOnly="true" class="tab_input"
                                              value="${memberDTO.type}" style="width:80px"></form:input></div>
        </td>
    </tr>
    <tr>
        <td>购卡/续卡类型:
            <span>${memberCardOrderDTO.memberCardName}</span>
            <input type="hidden" name="memberCardName" value="${memberCardOrderDTO.memberCardName}"/>
        </td>
        <td>
            <span style="color:#f17a16;">金　　额:</span><span><input type="text"
                                                                  style="border: 1px #f17a16 solid;color:#871d29;width:100px"
                                                                  id="memberCardPrice"
                                                                  value="${memberCardDTO.price}"></span>&nbsp;&nbsp;
        </td>
        <td>
            享 受<input type="text" id="memberDiscount" name="memberDiscount" value="${memberDTO.memberDiscount}"
                      style="border: 1px #f17a16 solid;color:#871d29;width:50px"/>折优惠(0-10之间数字)</span>
        </td>
        <td>日&nbsp;&nbsp;&nbsp;期:<span>${dateStr}</span></td>
    </tr>
</table>
<div class="clear height"></div>
<table cellpadding="0" cellspacing="0" class="table2">
    <col/>
    <col width="110"/>
    <col width="110"/>
    <col width="110"/>
    <tr>
        <td style="border-left:none;">项目</td>
        <td class="txt_right">原有</td>
        <td class="txt_right">新增</td>
        <td style="border-right:none;" class="txt_right">剩余</td>
    </tr>
    <tr>
        <td style="border-left:none;">储值金额</td>
        <td class="txt_right" id="balance">
            <c:choose>
                <c:when test="${memberDTO.balance == null || memberDTO.balance==''}">
                    0.0
                </c:when>

                <c:otherwise>
                    ${memberDTO.balance}
                </c:otherwise>
            </c:choose>
        </td>
        <td style="border-right:none;" class="txt_right" id="addbalance">

            <c:choose>
            <c:when test="${memberCardDTO.worth == null || memberCardDTO.worth==''}">
            0.0
            </c:when>

            <c:otherwise>
                ${memberCardDTO.worth}
            </c:otherwise>
            </c:choose>
        <td class="txt_right">
            <input type="text" value="0.0" class="tab_input" id="totalBalance" name="memberDTO.balance"
                   autocomplete="off"/>
            <form:hidden path="memberCardOrderItemDTOs[0].worth" id="memberCardOrderItemDTOs0.worth"></form:hidden>
        </td>

    </tr>
</table>
<div class="clear height"></div>
<table cellpadding="0" cellspacing="0" class="table2" id="table_productNo">
    <col width="150">
    <col width="67"/>
    <col width="50"/>
    <col width="80"/>
    <col width="150"/>
    <col width="135"/>
    <col width="50"/>
    <tr class="buycardTr">
        <td style="border-left:none;color:#147bda">服务项目<input type="button" class="opera2" onfocus="this.blur();"/></td>
        <td class="txt_right" style="color:#147bda">原有次数</td>
        <td class="txt_right" style="color:#147bda">新增</td>
        <td class="txt_right" style="color:#147bda">剩余</td>
        <td class="txt_right" style="color:#147bda">限消费车牌</td>
        <td class="txt_right" style="color:#147bda">失效日期</td>
        <td style="border-right:none;color:#147bda">操作</td>
    </tr>

    <c:forEach items="${memberCardOrderDTO.memberCardOrderServiceDTOs}" var="memberCardOrderServiceDTO"
               varStatus="status">
    <tr class="item table-row-original">
        <td>
            <form:hidden path="memberCardOrderServiceDTOs[${status.index}].serviceId" value="${memberCardOrderServiceDTO.serviceId}" autocomplete="off"/>
            <form:hidden path="memberCardOrderServiceDTOs[${status.index}].oldTimes" value="${memberCardOrderServiceDTO.oldTimes}" autocomplete="off"/>
            <form:hidden path="memberCardOrderServiceDTOs[${status.index}].increasedTimes" value="${memberCardOrderServiceDTO.increasedTimes}" autocomplete="off"/>
            <form:hidden path="memberCardOrderServiceDTOs[${status.index}].cardTimes" value="${memberCardOrderServiceDTO.cardTimes}" autocomplete="off"/>
            <form:hidden path="memberCardOrderServiceDTOs[${status.index}].cardTimesStatus" value="${memberCardOrderServiceDTO.cardTimesStatus}" autocomplete="off"/>
            <form:hidden path="memberCardOrderServiceDTOs[${status.index}].oldDeadlineStr" value="${memberCardOrderServiceDTO.oldDeadlineStr}" autocomplete="off"/>
            <form:hidden path="memberCardOrderServiceDTOs[${status.index}].addTerm" value="${memberCardOrderServiceDTO.addTerm}" autocomplete="off"/>
            <form:input path="memberCardOrderServiceDTOs[${status.index}].serviceName" value="${memberCardOrderServiceDTO.serviceName}" class="tab_input addService" style="width:90%" autocomplete="off"/>
        </td>
        <td class="txt_right">
            <c:choose>
                <c:when test="${memberCardOrderServiceDTO.oldTimes==-1}">
                    不限次
                </c:when>
                <c:otherwise>
                    ${memberCardOrderServiceDTO.oldTimes}
                </c:otherwise>
            </c:choose>
        </td>
        <td class="txt_right">
            <c:choose>
                <c:when test="${memberCardOrderServiceDTO.cardTimes==-1}">
                    不限次
                </c:when>
                <c:otherwise>
                    ${memberCardOrderServiceDTO.cardTimes}
                </c:otherwise>
            </c:choose>

        </td>
        <td class="txt_right" style="padding-right: 4px;">

            <c:choose>
                <c:when test="${memberCardOrderServiceDTO.balanceTimes==-1}">
                    <%--<input type="radio" class="timesStatus1" id="memberCardOrderServiceDTOs${status.index}.timesStatus1"--%>
                    <%--name="memberCardOrderServiceDTOs[${status.index}].timesStatus" value="0"/>--%>
                    <input type="text" class="tab_input" style="width:50px;"
                           name="memberCardOrderServiceDTOs[${status.index}].balanceTimes"
                           id="memberCardOrderServiceDTOs${status.index}.balanceTimes" autocomplete="off" value="不限次"/>
                    次
                    <%--<label><input type="radio" class="timesStatus2" id="memberCardOrderServiceDTOs${status.index}.timesStatus2"--%>
                    <%--name="memberCardOrderServiceDTOs[${status.index}].timesStatus" checked="checked" value="1"/>不限次</label>--%>
                </c:when>
                <c:otherwise>
                    <%--<input type="radio" class="timesStatus1" id="memberCardOrderServiceDTOs${status.index}.timesStatus1"--%>
                    <%--name="memberCardOrderServiceDTOs[${status.index}].timesStatus" checked="checked" value="0"/>--%>
                    <input type="text" class="tab_input" style="width:50px;"
                           name="memberCardOrderServiceDTOs[${status.index}].balanceTimes"
                           id="memberCardOrderServiceDTOs${status.index}.balanceTimes" autocomplete="off"
                           value="${memberCardOrderServiceDTO.balanceTimes}"/>
                    次
                    <%--<label><input type="radio" class="timesStatus2" id="memberCardOrderServiceDTOs${status.index}.timesStatus2"--%>
                    <%--name="memberCardOrderServiceDTOs[${status.index}].timesStatus" value="1"/>不限次</label>--%>
                </c:otherwise>
            </c:choose>
        </td>
        <td class="txt_right">
                <%--<div  class="txtSelect">--%>
                <%--<div class="select_icon">--%>
                <%--<input type="button" onfocus="this.blur();" id="input_select">--%>
                <%--</div>--%>
            <input type="text" class="tab_input" id="memberCardOrderServiceDTOs${status.index}.vehicles"
                   name="memberCardOrderServiceDTOs[${status.index}].vehicles" autocomplete="off"
                   value="${memberCardOrderServiceDTO.vehicles}"/>
                <%--</div>--%>

        </td>
        <td class="txt_right">
            <c:choose>
                <c:when test="${memberCardOrderServiceDTO.deadline==-1}">
                    <%--<input type="radio" class="deadlineStatus1" id="memberCardOrderServiceDTOs${status.index}.deadlineStatus1"--%>
                    <%--name="memberCardOrderServiceDTOs[${status.index}].deadlineStatus" value="0" style="float:left;"/>--%>

                    <%-- 日历组件 --%>
                    <input type="text" id="memberCardOrderServiceDTOs${status.index}.deadlineStr"
                           name="memberCardOrderServiceDTOs[${status.index}].deadlineStr" value="不限期"
                           class="tab_input isDatepickerInited checkDeadLine"
                           autocomplete="off" style="width:150px; float:left;" readOnly="true"/>
                    <%--<label style="float:left; padding-left:8px;">--%>
                    <%--<input type="radio" class="deadlineStatus2" id="memberCardOrderServiceDTOs${status.index}.deadlineStatus2"--%>
                    <%--name="memberCardOrderServiceDTOs[${status.index}].deadlineStatus" value="1"--%>
                    <%--checked="checked"/>无限期</label>--%>
                </c:when>
                <c:otherwise>
                    <%--<input type="radio" class="deadlineStatus1" id="memberCardOrderServiceDTOs${status.index}.deadlineStatus1"--%>
                    <%--name="memberCardOrderServiceDTOs[${status.index}].deadlineStatus" value="0" style="float:left;"--%>
                    <%--checked="checked"/>--%>
                    <%-- 日历组件 --%>
                    <form:input path="memberCardOrderServiceDTOs[${status.index}].deadlineStr" autocomplete="off"
                                value="${memberCardOrderServiceDTO.deadlineStr}"
                                class="tab_input isDatepickerInited checkDeadLine"
                                readOnly="true"
                                style="width:150px; float:left;"/>
                    <%--<label style="float:left; padding-left:8px;"><input type="radio" class="deadlineStatus2"--%>
                    <%--id="memberCardOrderServiceDTOs${status.index}.deadlineStatus2"--%>
                    <%--name="memberCardOrderServiceDTOs[${status.index}].deadlineStatus"--%>
                    <%--value="1"/>无限期</label>--%>
                </c:otherwise>
            </c:choose>
        </td>
        <td>
            <input class="opera1" type="button" id="memberCardOrderServiceDTOs${status.index}.opera1Btn">
        </td>
    </tr>
    <tr>
        </c:forEach>

</table>
<div style="color:#F00000; padding-left:300px;">（*不限次消费项目建议限定车牌号，多车牌用逗号“，”分隔）</div>
<div class="clear height"></div>

<div class="remark">
    备注:
    <input type="text" id="memo" name="memo" value="${memberCardOrderDTO.memo}" class="tab_input" autocomplete="off"/>
    <%--<form:input path="memo" class="tab_input" value="${memberCardOrderDTO.memo}"--%>
                <%--autocomplete="off"/>--%>
</div>

<div class="clear height"></div>
<div class="divSettle">
    <span>消费结算验证方式：</span>
        <%--<label><input type="checkbox" checked="checked" />消费短信通知</label>--%>
        <%--<label><input type="checkbox" />打印签字</label>--%>

    <label><input type="checkbox" id="checkPwd" value="<%=PasswordValidateStatus.VALIDATE %>"
        ${memberDTO.passwordStatus eq 'VALIDATE' ? "checked":"" }
                  name="memberDTO.passwordStatus"/>密码</label>

    <c:choose>
        <c:when test="${memberStatus eq 'DISABLED'}">
            <input type="password" style="margin-right:25px;" value=''
                   name="memberDTO.password" id="password"
                   autocomplete="off"/>
        </c:when>
        <c:otherwise>
            <input type="password" style="margin-right:25px;" value='${not empty memberDTO.password?"^^^^^^":""}'
                   name="memberDTO.password" id="password"
                   autocomplete="off"/>
        </c:otherwise>
    </c:choose>
</div>
<div class="clear height"></div>
<div class="postTitle">
    <div style="float:left;">付款结算</div>
    <div style="float:left;padding-left:420px;width:120px;position:relative;" id="saleManDiv">导购员:<span
            id="saleManSpan"><input
            type="text" readOnly="true"
            style="width:50px;"
            id="saleMan"
            name="memberCardOrderItemDTOs[0].salesMan"
            class="tab_input"
            autocomplete="off"/>
        <img src="images/list_close.png" id="deleteSaleMan" style="width:12px;cursor:pointer">
        <input type="hidden" name="memberCardOrderItemDTOs[0].salesId" id="saleManId"/></span>
    </div>
    <div style="float:left;padding-left:10px;position:relative;" id="executorDiv">操作员:<span id="executorSpan"><input
            type="text"
            id="executor"
            value="${user}"
            style="width:50px;"
            readOnly="true"
            class="tab_input"
            autocomplete="off"/>
        <img src="images/list_close.png" id="deleteExecutor" style="width:12px;cursor:pointer">
        <form:hidden path="executorId" value="${userId}"></form:hidden></span>
    </div>
</div>
<table cellpadding="0" cellspacing="0" class="table3 supplierTable">
    <col width="200"/>
    <col width="170"/>
        <%--<col width="170" />--%>
    <col/>
    <tr>
        <td>应&nbsp;&nbsp;收:<span id="totalSpan">${memberCardDTO.price}</span><input type="hidden" class="tab_input"
                                                                                    id="total"
                                                                                    name="receivableDTO.total"
                                                                                    autocomplete="off"
                                                                                    style="width:120px;"/></td>
        <form:hidden path="memberCardOrderItemDTOs[0].price" value="${memberCardDTO.price}"></form:hidden>
        <td>
            优 惠:<input type="text" class="tab_input" id="discount" name="receivableDTO.discount" value=""
                       autocomplete="off"
                       style="width:100px;"/>
        </td>
        <td>
            挂 账:<input type="text" class="tab_input" id="debt" name="receivableDTO.debt" value="" autocomplete="off"
                       style="width:100px;"/>
        </td>

        <td>
            <span style="float:left;">还款日期:</span><input type="text" autocomplete="off" readonly="true"
                                                         id="huankuanTime"
                                                         name="repayTime" class="tab_input isDatepickerInited"
                                                         style="width:84px; float:left;margin-top:5px;"
                                                         onclick="showDatepicker(this)"/>
        </td>

    </tr>
    <tr class="tr_bgColor tr_border">
        <td>现&nbsp;&nbsp;金:<input type="text" class="tab_input" style="width:120px;" id="cash" name="receivableDTO.cash"
                                  autocomplete="off"/></td>
        <td></td>
        <td></td>
        <td></td>
    </tr>
    <tr class="tr_bgColor">
        <td>银行卡:<input type="text" class="tab_input" style="width:120px;" id="bankCard" name="receivableDTO.bankCard"
                       autocomplete="off"/></td>
        <td colspan="3" style="text-align:right; padding-right:40px;"><span class="words">实 收</span>:<input type="text"
                                                                                                            class="tab_input"
                                                                                                            autocomplete="off"
                                                                                                            id="settledAmount"
                                                                                                            name="receivableDTO.settledAmount"
                                                                                                            value="0"
                                                                                                            style="width:100px;"/>
        </td>
    </tr>
    <tr class="tr_bgColor">
        <td>
            支&nbsp;&nbsp;票:<input type="text" class="tab_input" style="width:120px;" id="check"
                                  name="receivableDTO.cheque"
                                  autocomplete="off"/>

        <td class="divNum">号&nbsp;&nbsp;码:<input type="text" name="receivableDTO.recordDTOs[0].chequeNo"
                                                 class="tab_input" style="width:95px;" id="checkNo"
                                                 autocomplete="off"/>
        </td>
        </td>
        <td></td>
        <td></td>
        <td></td>
    </tr>
</table>
<div class="clear"></div>
<div class="height"></div>
<div class="noticeWords">*双击更改付款方式</div>
<div class="more_his">
    <label style="padding-right:10px;">
        <input type="checkbox" value="true" id="sendMsg" name="sendMsg" <c:if test="${smsSwitch}">checked="true"</c:if>/ >发送短信</label>
    <label style="padding-right:10px;">
        <input type="checkbox" value="true" id="print" name="print"/>打印</label>
    <input type="button" value="确认" onfocus="this.blur();" id="saveBtn" class="confirmbtn"/>
    <input type="button" value="取消" onfocus="this.blur();" id="cancleBtn" class="conclebtn"/>
</div>
<div class="height"></div>
</div>
<div class="i_upBottom">
    <div class="i_upBottomLeft"></div>
    <div class="i_upBottomCenter"></div>
    <div class="i_upBottomRight"></div>
</div>
</div>

<div class="tab_repay" id="inputMobile"
     style="display:none;position:absolute; left:250px; top:200px; width:300px;z-index:100">
    <div class="i_arrow"></div>
    <div class="i_upLeft"></div>
    <div class="i_upCenter">
        <div class="i_note" id="div_drag">有欠款请最好填写手机号码</div>
    </div>
    <div class="i_upRight"></div>
    <div class="i_upBody">
        <div class="boxContent" style="float:none; text-align:center; color:#000">
            <input type="text" name="mobile" id="mobile" value="${customerDTO.mobile}">
        </div>
        <div class="sure" style="float:none; text-align:center;">
            <input type="hidden" type="hidden" id="flag" isNoticeMobile="false">
            <input type="button" value="确定" onfocus="this.blur();" onclick="inputMobile();"/>
            <input type="button" value="取消" onfocus="this.blur();" onclick="cancleInputMobile();"/>
        </div>
    </div>
    <div class="i_upBottom">
        <div class="i_upBottomLeft"></div>
        <div class="i_upBottomCenter"></div>
        <div class="i_upBottomRight"></div>
    </div>
</div>
</form:form>
<div id="div_select" class="selectList" style="display:none;">
    <label><input type="checkbox"/><a>苏E000001</a></label>
    <label><input type="checkbox"/><a>苏E000002</a></label>
    <label><input type="checkbox"/><a>苏E000003</a></label>
    <label><input type="checkbox"/><a>苏E000004</a></label>
    <label><input type="checkbox"/><a>苏E000005</a></label>
    <label><input type="checkbox"/><a>苏E000006</a></label>
</div>

<div class="tab_repay" id="selectBtn"
     style="display:none;position:absolute; left:250px; top:200px; width:300px;z-index:100">
    <div class="i_arrow"></div>
    <div class="i_upLeft"></div>
    <div class="i_upCenter">
        <div class="i_note" id="div_drag"></div>
    </div>
    <div class="i_upRight"></div>
    <div class="i_upBody">
        <div class="boxContent" style="float:none; text-align:center; color:#000">
            实收为0，是否挂账或优惠赠送？
        </div>
        <div class="sure" style="float:none; text-align:center;">
            <input type="button" value="挂账" onfocus="this.blur();" onclick="selectDebt();"/>
            <input type="button" value="赠送" onfocus="this.blur();" onclick="selectDiscount();"/>
        </div>
    </div>
    <div class="i_upBottom">
        <div class="i_upBottomLeft"></div>
        <div class="i_upBottomCenter"></div>
        <div class="i_upBottomRight"></div>
    </div>
</div>
<div id="inputMobileDiv">
    <div style="margin-left: 45px;margin-top: 15px">
        <label>手机号：</label>
        <input type="text" id="divMobile" style="width:125px;height: 20px">
    </div>
</div>
<div style="height: 0px;overflow: hidden;">
    <div id="dialog-confirm" title="友情提示：此会员卡中存在已经过期的续卡项目！">
        <div id="dialog-confirm-content" style="margin: 10px;padding: 6px;background-color: #DCDCDC;line-height: 20px;border: 1px solid #808080;"></div>
        <div style="width:320px;overflow: hidden;margin: 10px;line-height: 20px;">
            <div style="float: left;width:60px;">你可以：</div>
            <div style="float: left;width:250px;overflow: hidden;">
                <div><input type="radio" name="opt" value="1" id="opt1" checked="checked"><label for="opt1">累计续卡<span style="color:#808080;">(不清空原有次数，累计续卡)</span></label></div>
                <div><input type="radio" name="opt" value="2" id="opt2"><label for="opt2">归零续卡<span style="color:#808080;">(清空原有次数，重新续卡)</span></label></div>
            </div>
        </div>
    </div>
</div>
</body>
</html>