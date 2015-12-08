<%@ page import="com.bcgogo.config.ConfigController" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>


<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>首页</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>

<%@include file="style_thirdparty_extension_core.jsp"%>

<link rel="stylesheet" href="js/components/themes/bcgogo-searchcomplete<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" href="js/components/themes/bcgogo-autocomplete<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" href="js/components/themes/bcgogo-detailsListPanel<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" href="js/components/themes/bcgogo-droplist<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" href="js/components/themes/bcgogo-scanning<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" href="js/components/themes/bcgogo-menupanel<%=ConfigController.getBuildVersion()%>.css">
<link rel="stylesheet" href="js/components/themes/bcgogo-shadow<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" href="js/components/themes/bcgogo-guideTip<%=ConfigController.getBuildVersion()%>.css"/>

<link rel="stylesheet" type="text/css" href="styles/loginCheck<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/base<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" href="styles/customerService<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" href="styles/head<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/login<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/nanoscroller<%=ConfigController.getBuildVersion()%>.css"/>
<!--
<link rel="stylesheet" type="text/css" href="styles/advertisement<%=ConfigController.getBuildVersion()%>.css"/>
-->
<style type="text/css">
    .testcss {
        cursor: pointer;
        float: right;
        height: 24px;
        line-height: 27px;
        padding-left: 25px;
    }

    .Scroller-Containermain {
        position: absolute;
        top: 0px;
        left: 0px;
        margin: 5px 0px 0px 4px;
    }

    .Scroller-Containermain a {
        display: block;
        width: 199px;
        height: 20px;
        line-height: 20px;
        padding-left: 5px;
        color: #000000;
    }

    #Scroller-1 {
        overflow-x: hidden;
        overflow-y: auto;
        height: 100%;
        width: 100%;
    }

    .hover {
        color: #ffffff;
    }

    .hover:hover {
        color: #FD5300;
    }
    .footer .footer_line a,
    .footer .footer_line {
        color: #FFFFFF;
    }

    .main-body {
        width:100%;
    }

    a.rqPrintLink{
        color:#FFFFFF;
    }

    .rqPrintLink:hover{
        color: #FD5300;
        text-decoration: underline;
    }
    img#mainRqImg{
        clip: rect(15px, 95px, 95px, 15px);
        position: absolute;
        left: 13px;
        top: 133px;
    }

</style>
 <script type="text/javascript" src="js/extension/jquery/jquery-1.10.2.js"></script>
 <script type="text/javascript">
     var jQuery_1_10_2 = $.noConflict(true);

 </script>
<%@include file="script_thirdparty_extension_core.jsp"%>

<script type="text/javascript" src="js/base<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/application<%=ConfigController.getBuildVersion()%>.js"></script>

<script type="text/javascript" src="js/module/bcgogo-WebStorage<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/module/ajaxQuery<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/module/bcgogo-scanning<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/module/bcgogo-noticeDialog<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/components/ui/bcgogo-menupanel<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/components/ui/bcgogo-qqInvoker<%=ConfigController.getBuildVersion()%>.js"></script>

<script type="text/javascript" src="js/common<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/basecommon<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/components/ui/bcgogo-autocomplete<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/components/ui/bcgogo-detailsList<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/components/ui/bcgogo-searchcomplete<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/components/ui/bcgogo-highlightcomplete<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/components/ui/bcgogo-droplist<%=ConfigController.getBuildVersion()%>.js"></script>
<jsp:include page="/user/userGuide/userGuideMain.jsp"/>
<script type="text/javascript" src="js/mask<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/bcgogo<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/vehiclenosolr<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/page/search/suggestionBase<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/page/search/productSuggestion<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/page/search/customerSuggestion<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/utils/stringUtil<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/page/menu<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/page/client<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/extension/jquery.nanoscroller<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/md5<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/utils/fingerprint<%=ConfigController.getBuildVersion()%>.js"></script>

<bcgogo:hasPermission permissions="WEB.AD_SHOW">
    <%--<script type="text/javascript" src="js/advertisement<%=ConfigController.getBuildVersion()%>.js"></script>--%>
    <%--<jsp:include page="/js/advertisement/advertisement.jsp"></jsp:include>--%>
    <%--<script type="text/javascript" src="js/extension/jquery/jquery-1.10.2.js">--%>
    <%--<script type="text/javascript" src="js/advertisement/advertisement<%=ConfigController.getBuildVersion()%>.js"></script>--%>
    <%--<link rel="stylesheet" type="text/css" href="js/advertisement/advertisement<%=ConfigController.getBuildVersion()%>.css" />--%>
    <%--<%@include file="/js/advertisement/advertisement.jsp"%>--%>
</bcgogo:hasPermission>
<%--<bcgogo:hasPermission permissions="WEB.AD_SHOW">--%>
    <%--<script type="text/javascript" src="js/module/recommendShop<%=ConfigController.getBuildVersion()%>.js"></script>--%>
<%--</bcgogo:hasPermission>--%>
<script type="text/javascript">
bcClient.clearClientRedirect();
App.page="main";
$(document).ready(function () {
    $(".pop_close,.pop_button").bind("click", function () {
        $(".pop-upbox").css("display", "none");

    });
    $(".l_topTitle,.kefu,.zhuce,.sysAnnounce,.help,#changeUserPassword,#j_logout,.zhuce").hover(function () {
        $(this).css({"color": "#fd5300", "textDecoration": "underline"});
    }, function () {
        $(this).css({"color": "#BEBEBE", "textDecoration": "none"});
    });
    initQQTalk($(".icon_QQchat"));
    initQQTalk($(".mainQQ"));

    <%--<bcgogo:hasPermission permissions="WEB.AD_SHOW">--%>
    //推荐商户
//    APP_BCGOGO.Net.asyncGet({
//        url: 'recommendShop.do?method=getRecommendTreeNode',
//        data: {
//            "now": new Date()
//        },
//        dataType: "json",
//        success: function (data) {
//            var recommendShop = APP_BCGOGO.Module.recommendShop;
//            recommendShop.show({
//                data:data
//            });
//        }
//    });
   <%--</bcgogo:hasPermission>--%>



});
<bcgogo:permissionParam  permissions="WEB.VEHICLE_CONSTRUCTION.CONSTRUCT.BASE,WEB.CUSTOMER_MANAGER.BASE,WEB.TXN.INVENTORY_MANAGE.STOCK_SEARCH,WEB.SUPPLIER_MANAGER.SEARCH.APPLY.SUPPLIER,WEB.CUSTOMER_MANAGER.SEARCH.APPLY.CUSTOMER,WEB.AUTOACCESSORYONLINE.COMMODITYQUOTATIONS.BASE,WEB.AUTOACCESSORYONLINE.RELATEDCUSTOMERSTOCK.BASE">
APP_BCGOGO.Permission.VehicleConstruction.Construct.Base = ${WEB_VEHICLE_CONSTRUCTION_CONSTRUCT_BASE};
APP_BCGOGO.Permission.CustomerManager.Base = ${WEB_CUSTOMER_MANAGER_BASE};
APP_BCGOGO.Permission.Txn.InventoryManage.StockSearch.Base = ${WEB_TXN_INVENTORY_MANAGE_STOCK_SEARCH};
APP_BCGOGO.Permission.AutoAccessoryOnline.ApplySupplier = ${WEB_SUPPLIER_MANAGER_SEARCH_APPLY_SUPPLIER};
APP_BCGOGO.Permission.AutoAccessoryOnline.ApplyCustomer = ${WEB_CUSTOMER_MANAGER_SEARCH_APPLY_CUSTOMER};
APP_BCGOGO.Permission.AutoAccessoryOnline.CommodityQuotations = ${WEB_AUTOACCESSORYONLINE_COMMODITYQUOTATIONS_BASE};
APP_BCGOGO.Permission.AutoAccessoryOnline.RelatedCustomerStock = ${WEB_AUTOACCESSORYONLINE_RELATEDCUSTOMERSTOCK_BASE};
</bcgogo:permissionParam>

<bcgogo:permissionParam resourceType="logic" permissions="WEB.VERSION.DISABLE_SEARCH_CUSTOMER,WEB.VERSION.DISABLE_SEARCH_PRODUCT,WEB.VERSION.DISABLE_SEARCH_VEHICLE,WEB.VERSION.DISABLE.SEARCH.SUPPLIER.ONLINE,WEB.VERSION.DISABLE.SEARCH.ACCESSORY.ONLINE,WEB.VERSION.DISABLE.SEARCH.CUSTOMER.INVENTORY.ONLINE,WEB.VERSION.DISABLE.SEARCH.CUSTOMER.ONLINE">
APP_BCGOGO.Permission.Version.SearchVehicle = ${!WEB_VERSION_DISABLE_SEARCH_VEHICLE};
APP_BCGOGO.Permission.Version.SearchProduct = ${!WEB_VERSION_DISABLE_SEARCH_PRODUCT};
APP_BCGOGO.Permission.Version.SearchCustomerSupplier = ${!WEB_VERSION_DISABLE_SEARCH_CUSTOMER};
APP_BCGOGO.Permission.Version.SearchAccessoryOnline = ${!WEB_VERSION_DISABLE_SEARCH_ACCESSORY_ONLINE};
APP_BCGOGO.Permission.Version.SearchCustomerInventoryOnline = ${!WEB_VERSION_DISABLE_SEARCH_CUSTOMER_INVENTORY_ONLINE};
APP_BCGOGO.Permission.Version.SearchCustomerOnline = ${!WEB_VERSION_DISABLE_SEARCH_CUSTOMER_ONLINE};
APP_BCGOGO.Permission.Version.SearchSupplierOnline = ${!WEB_VERSION_DISABLE_SEARCH_SUPPLIER_ONLINE};
</bcgogo:permissionParam>

APP_BCGOGO.UserGuide.currentPage = "main";
var mainModuleNum = 0;
<bcgogo:permissionParam resourceType="menu" permissions="WEB.SCHEDULE.BASE,WEB.TXN.BASE,WEB.CUSTOMER_MANAGER.BASE,WEB.VEHICLE_CONSTRUCTION.BASE,WEB.STAT.BASE,WEB.SYSTEM_SETTINGS.BASE,WEB.SUPPLIER_MANAGER.BASE,">
APP_BCGOGO.Permission.Schedule.Base =${WEB_SCHEDULE_BASE};
APP_BCGOGO.Permission.Txn.Base =${WEB_TXN_BASE};
APP_BCGOGO.Permission.CustomerManager.Base =${WEB_CUSTOMER_MANAGER_BASE};
APP_BCGOGO.Permission.VehicleConstruction.Base =${WEB_VEHICLE_CONSTRUCTION_BASE}; // todo 施工单作为 刷卡权限控制
APP_BCGOGO.Permission.Stat.Base =${WEB_STAT_BASE};
APP_BCGOGO.Permission.SystemSetting.Base =${WEB_SYSTEM_SETTINGS_BASE};
APP_BCGOGO.Permission.SupplierManager.Base =${WEB_SUPPLIER_MANAGER_BASE};
</bcgogo:permissionParam>

if (APP_BCGOGO.Permission.Schedule.Base) {
    mainModuleNum++;
}
if (APP_BCGOGO.Permission.Txn.Base) {
    mainModuleNum++;
}
if (APP_BCGOGO.Permission.CustomerManager.Base) {
    mainModuleNum++;
}
if (APP_BCGOGO.Permission.VehicleConstruction.Base) {
    mainModuleNum++;
}
if (APP_BCGOGO.Permission.Stat.Base) {
    mainModuleNum++;
}
if (APP_BCGOGO.Permission.SystemSetting.Base) {
    mainModuleNum++;
}
if (APP_BCGOGO.Permission.SupplierManager.Base) {
    mainModuleNum++;
}
if (mainModuleNum <= 4) {
    $("#mainModule").addClass("mt-70");
}

function openOrAssign(url) {
    window.location.assign(url);
}


$(function () {

    if ($(".newAnnouncement").find(".border").size() > 1) {
        $(".newAnnouncement .border").not(":last").css("border-bottom", "1px solid #FFB562");
    }

    var isDenied = GLOBAL.Util.getUrlParameter("permissionFlag");
    if (isDenied != null && isDenied == "true") {
        nsDialog.jAlert("您没有权限！");
    }


    $("#j_logout").bind("click", function () {
        clearUserGuideCookie();
        $.cookie("clientUrl", null);
        defaultStorage.clear();
//        window.location.href = 'j_spring_security_logout';
        $("#loginOutForm").submit();

    });
    var isTrialExpired = "";
    var trialData = $.parseJSON($.parseJSON($.cookie("hasTrialReminder")));
    if (stringUtil.isNotEmpty(trialData)) {
        isTrialExpired = trialData.isTrialExpired;
    }
    if (isTrialExpired) {
        var trialLeftTime = trialData.trialLeftTime;
        var trialEndDate = trialData.trialEndDate;

        $("#overdueNotice").css("display", "block");
        $("#remainDate").text(trialLeftTime);
        $("#deadLine").text(trialEndDate);

        $.cookie("hasTrialReminder", null);
    }
    $("#mainModule > div").not(':first,#rqDiv').addClass("hover");

    $("#finger").val(new Fingerprint({canvas: true,hasher:hex_md5}).get());

});

function toLoanTransfers() {
    window.open("bcgogoReceivable.do?method=bcgogoReceivableOrderList");
}

function clearUserGuideCookie() {
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
}

//检查输入中的非法字符
function checkChar(InString) {
    for (Count = 0; Count < InString.length; Count++) {
        TempChar = InString.substring(Count, Count + 1);
        if (!checkshuzi(TempChar) && !checkzimu(TempChar) && !checkhanzi(TempChar)) {
            return (true);
        }
    }
    return (false);
}

//判断数字
function checkshuzi(shuziString) {
    var shuzi = shuziString.match(/\d/g);
    if (shuzi == null)
        return (false);
    else
        return (true);
}

//判断字母
function checkzimu(zimuString) {
    var zimu = zimuString.match(/[a-z]/ig);
    if (zimu == null)
        return (false);
    else
        return (true);
}

//判断汉字
function checkhanzi(hanziString) {
    var hanzi = hanziString.match(/[^ -~]/g);
    if (hanzi == null)
        return (false);
    else
        return (true);
}

function isTyping() {
    var typing = false;
    $("#supplier").each(function () {
        if ($(this).val() != "") {
            typing = true;
        }
    });
    $("#contact").each(function () {
        if ($(this).val() != "") {
            typing = true;
        }
    });
    $("#mobile").each(function () {
        if ($(this).val() != "") {
            typing = true;
        }
    });
    $("#address").each(function () {
        if ($(this).val() != "") {
            typing = true;
        }
    });
    $("#customer").each(function () {
        if ($(this).val() != "") {
            typing = true;
        }
    });
    $("#licenceNo").each(function () {
        if ($(this).val() != "") {
            typing = true;
        }
    });
    $(".table_input").each(function () {
        if ($(this).val() != ""
                && $(this).val() != "0"
                && $(this).val() != "0.0") {
            typing = true;
        }
    });
    $("#saveBtn").each(function () {
        if ($(this).attr("disabled")) {
            typing = false;
        }
    });
    $("#carWash").each(function () {
        if ($(this).attr("class") == "title_hover") {
            typing = false;
        }
    });


    $("#saveA").each(function () {
        if ($(this).html() == "改单" && !isedit) {
            typing = false;
        }
    });
    return typing;
}

function showCustomerServiceInfo(dom) {
    var offset = $(dom).offset();
    var offsetHeight = $(dom).height();
    var offsetWidth = $(dom).width();
    $(".QQ_chat")
            .css({
                'position': 'absolute',
                'z-index': '8',
                'left': offset.left  + 'px',
                'top': offset.top + offsetHeight - 5 + 'px',
                'padding-left': 0 + 'px'
            })
            .fadeIn("slow");
}

function toSysAnnouncement() {
    window.location = "sysReminder.do?method=toSysAnnouncement";
}

function toSmsSend(lastReleaseDate, dom) {
    window.open("sms.do?method=smswrite");
}

function closeRemider(type, lastReleaseDate, dom) {
    App.Net.asyncPost({
        url: "sysReminder.do?method=updateUserReadRecord",
        data: {reminderType: type, lastReadDate: lastReleaseDate},
        cache: false,
        dataType: "json"
    });
    var reminderNum = $(dom).closest(".newAnnouncement").find(".border").size();
    if (reminderNum > 1) {
        $(dom).closest(".border").remove();
    } else {
        $(".newAnnouncement").children().remove()
    }
    $(".newAnnouncement .border:last").css("border-bottom", "none");
}

function initQQTalk($qq) {
    var qqInvoker = new App.Module.QQInvokerStatic();
    qqInvoker.init($qq);
}

</script>



</head>
<body class="blueBg main-body">
<%@ include file="/common/customerQQService.jsp" %>
<div class="m_topMain">
    <div class="l_top">
        <div class="l_topBorder"></div>
        <div class="home"></div>
        <div class="l_topTitle" style="width:30px;">首页</div>
        <div class="l_topBorder"></div>
        <div class="l_topTitle">欢迎使用一发软件</div>
        <div class="l_topBorder"></div>
        <div class="kefu" onclick="showCustomerServiceInfo(this)">客服</div>
        <div class="mainQQ"></div>
        <bcgogo:hasPermission permissions="WEB.CUSTOMER_MANAGER.CUSTOMER_UPDATE">
            <div class="l_topBorder"></div>
            <div style="float:left; width: 60px; text-align:center; line-height:27px;">
                <a class="zhuce" style="color:#BEBEBE;" href="<%=request.getContextPath()%>/shopRegister.do?method=registerMain&registerType=SUPPLIER_REGISTER">注册店铺</a>
            </div>
        </bcgogo:hasPermission>
        <div class="l_topBorder"></div>
        <bcgogo:hasPermission permissions="WEB.SYS.REMINDER_ANNOUNCEMENT">
            <div style="float:left; width:70px; text-align:center; line-height:27px; cursor:pointer;">
                <a style="color:#BEBEBE;text-decoration: none;" id="toSysAnnouncement" class="sysAnnounce" href="sysReminder.do?method=toSysAnnouncement" menu-name="TO_SYS_ANNOUNCEMENT">公告</a>&nbsp;|
                <a class="help" style="color:#BEBEBE;text-decoration: none;" url="help.do?method=toHelper" id="toHelper" menu-name="TO_HELPER">帮助</a>
                <bcgogo:hasNewReminder>
                    <c:if test="${newAnnouncementFlag || festivals!=null || (trialUseDays!=null && trialUseDays < 30)}">
                    <div class="newAnnouncement" style="margin: 0px">
                        <div class="ti_top"></div>
                        <div class="ti_body">
                            <c:if test="${newAnnouncementFlag}">
                                <div class="border" style="border-bottom:none;">
                                    <span onclick="toSysAnnouncement()">新版本公告</span>
                                    <a class="close"
                                       onclick="closeRemider('ANNOUNCEMENT',${announce_lastReleaseDate},this)"></a>
                                </div>
                            </c:if>
                            <c:if test="${festivals!=null}">
                                <div class="border" style="border-bottom:none;">
                                    <span onclick="toSmsSend(${festival_lastReleaseDate})"> ${festivals}快到了，赶紧发短信祝福您的客户吧！</span>
                                    <a class="close" onclick="closeRemider('FESTIVAL',${festival_lastReleaseDate},this)"></a>
                                </div>
                            </c:if>
                            <c:if test="${trialUseDays!=null && trialUseDays < 30}">
                                <div class="border not_underline" style="border-bottom:none;">
                                    <c:if test="${chargeType == 'ONE_TIME'}">
                                        <a href="bcgogoReceivable.do?method=bcgogoReceivableOrderList">软件试用期还剩${trialUseDays}天，请联系尽快交费！如有疑问请联系客服！</a>
                                    </c:if>

                                    <c:if test="${chargeType == 'YEARLY'}">
                                        <span>软件一年免费使用期还剩${trialUseDays}天，请尽快缴年费！如有疑问请联系客服！</span>
                                    </c:if>
                                    <a class="close"
                                       onclick="closeRemider('TRIAL_USE_DAYS',${trialUseDays_lastReleaseDate},this)"></a>
                                </div>
                            </c:if>
                        </div>
                        <div class="ti_bottom"></div>
                    </div>
                    </c:if>
                </bcgogo:hasNewReminder>
            </div>
        </bcgogo:hasPermission>

        <div class="l_topBorder"></div>
        <div class="l_topRight">
            <div style="float:left; line-height:27px;padding-right:8px;" id="div_shopName">${shopName}</div>
            <div class="l_topBorder"></div>
            <div style="float:left; text-align:center; line-height:27px;">欢迎您，
                <span id="span_userName">${userName}【${userGroupType}&nbsp;▪&nbsp;${shopVersion.value}】</span>
            </div>
            <div class="l_topBorder"></div>
            <div class="exist">
                <form  id="loginOutForm" method="post" action="j_spring_security_logout" style="display: none">
                    <input name="finger" id="finger"/>
                </form>
                <a id="j_logout">退出</a></div>
            <div class="l_topBorder"></div>
        </div>
    </div>
</div>

<div class="m_main">
    <div class="m_condition_group clearfix" id="mainModule">
        <%--<bcgogo:permissionParam resourceType="logic"--%>
                                <%--permissions="WEB.VERSION.DISABLE_SEARCH_CUSTOMER,WEB.VERSION.DISABLE_SEARCH_PRODUCT,WEB.VERSION.DISABLE_SEARCH_VEHICLE,WEB.VERSION.DISABLE.SEARCH.SUPPLIER.ONLINE,WEB.VERSION.DISABLE.SEARCH.ACCESSORY.ONLINE,WEB.VERSION.DISABLE.SEARCH.CUSTOMER.INVENTORY.ONLINE,WEB.VERSION.DISABLE.SEARCH.CUSTOMER.ONLINE">--%>
        <bcgogo:permissionParam  permissions="WEB.VEHICLE_CONSTRUCTION.CONSTRUCT.BASE,WEB.CUSTOMER_MANAGER.BASE,WEB.TXN.INVENTORY_MANAGE.STOCK_SEARCH,WEB.SUPPLIER_MANAGER.SEARCH.APPLY.SUPPLIER,WEB.CUSTOMER_MANAGER.SEARCH.APPLY.CUSTOMER,WEB.AUTOACCESSORYONLINE.COMMODITYQUOTATIONS.BASE,WEB.AUTOACCESSORYONLINE.RELATEDCUSTOMERSTOCK.BASE">
            <%--<c:if test="${!WEB_VERSION_DISABLE_SEARCH_CUSTOMER || !WEB_VERSION_DISABLE_SEARCH_PRODUCT || !WEB_VERSION_DISABLE_SEARCH_VEHICLE ||!WEB_VERSION_DISABLE_SEARCH_ACCESSORY_ONLINE||!WEB_VERSION_DISABLE_SEARCH_CUSTOMER_INVENTORY_ONLINE||!WEB_VERSION_DISABLE_SEARCH_CUSTOMER_ONLINE||!WEB_VERSION_DISABLE_SEARCH_SUPPLIER_ONLINE}">--%>
            <c:if test="${WEB_VEHICLE_CONSTRUCTION_CONSTRUCT_BASE || WEB_CUSTOMER_MANAGER_BASE || WEB_TXN_INVENTORY_MANAGE_STOCK_SEARCH || WEB_SUPPLIER_MANAGER_SEARCH_APPLY_SUPPLIER || WEB_CUSTOMER_MANAGER_SEARCH_APPLY_CUSTOMER || WEB_AUTOACCESSORYONLINE_COMMODITYQUOTATIONS_BASE || WEB_AUTOACCESSORYONLINE_RELATEDCUSTOMERSTOCK_BASE}">
                <div id="searchBox" class="m_condition Search">
                    <a class="logo"></a>

                    <div class="search_body">
                        <div class="Big_listName" id="_searchNameSelect">

                            <div id="_searchNameBar" class="hoverButton">
                                <span id="_searchName">加载中...</span>
                                <a class="Big_icon_listUp"></a>
                            </div>

                            <div class="listInfo" id="_searchNameList" style="display:none;">
                                <c:if test="${WEB_VEHICLE_CONSTRUCTION_CONSTRUCT_BASE}">
                                    <a class="J-selectOption" searchMethod="licenceNo"  menu-name="VEHICLE_CONSTRUCTION">车牌号</a>
                                </c:if>
                                <c:if test="${WEB_TXN_INVENTORY_MANAGE_STOCK_SEARCH}">
                                    <a class="J-selectOption" searchMethod="accessoryName"  menu-name="TXN_INVENTORY_MANAGE_STOCK_SEARCH">商品库存</a>
                                </c:if>
                                <c:if test="${WEB_CUSTOMER_MANAGER_BASE}">
                                    <a class="J-selectOption" searchMethod="customerOrSupplier">客户/供应商</a>
                                </c:if>
                                <c:if test="${WEB_AUTOACCESSORYONLINE_COMMODITYQUOTATIONS_BASE}">
                                    <a class="J-selectOption" searchMethod="accessoryOnline" menu-name="AUTO_ACCESSORY_ONLINE_COMMODITYQUOTATIONS">配件报价</a>
                                </c:if>
                                <c:if test="${WEB_AUTOACCESSORYONLINE_RELATEDCUSTOMERSTOCK_BASE}">
                                    <a class="J-selectOption" searchMethod="customerInventoryOnline" menu-name="AUTO_ACCESSORY_ONLINE_RELATEDCUSTOMERSTOCK">客户库存</a>
                                </c:if>
                                <c:if test="${WEB_CUSTOMER_MANAGER_SEARCH_APPLY_CUSTOMER}">
                                    <a class="J-selectOption" menu-name="CUSTOMER_MANAGER_SEARCH_APPLY_CUSTOMER" searchMethod="customerOnline">推荐客户</a>
                                </c:if>
                                <c:if test="${WEB_SUPPLIER_MANAGER_SEARCH_APPLY_SUPPLIER}">
                                    <a class="J-selectOption" searchMethod="supplierOnline" menu-name="APPLY_GET_APPLY_SUPPLIERS">推荐供应商</a>
                                </c:if>
                            </div>
                        </div>
                        <a class="so"></a>
                        <input type="text" id="_searchInputText" class="txt" kissfocus="on"/>
                        <input type="hidden" id="_searchMethod"/>
                        <a id="_searchInputButton" class="btnSo"></a>
                    </div>
                    <div class="divList"><a class="iconList"></a><div class="list_info" id="_searchDescription"></div></div>
                </div>
            </c:if>
        </bcgogo:permissionParam>
        <bcgogo:hasPermission permissions="WEB.VERSION.NOT_WHOLESALERS">
            <div id="rqDiv" style="position: absolute; margin-left: 890px; margin-top: 1px;">
            <div class="mobileVersion-relative">
                <div class="mobileVersion-btn" onclick="window.open('weChat.do?method=printWXQRCode');" class="blue_color" >微信二维码</div>
                <%--<img id="mainRqImg" src="${shopDTO.imageCenterDTO.shopRQImageDetailDTO.imageURL}" /><br/>--%>

                <div class="mobile-absolute">
                    <ul>
                       <%--<li> 扫一扫，本店名称</li>--%>

                      <bcgogo:permissionParam permissions="WEB.VERSION.FOUR_S_VERSION_BASE">
                        <%--<c:if test="${!WEB_VERSION_FOUR_S_VERSION_BASE}">--%>
                          <%--<li><img src="images/rq/rq_down_app_200x200.png" width="67" height="67"/><br/>--%>
                            <%--扫一扫，下载APP--%>
                          <%--</li>--%>
                        <%--</c:if>--%>
                        <c:if test="${WEB_VERSION_FOUR_S_VERSION_BASE}">
                          <li><img src="images/gsm_app_down/gsm_app_200x200.png" width="67" height="67"/><br/>
                            扫一扫，下载APP
                          </li>
                        </c:if>
                      </bcgogo:permissionParam>

                    </ul>
                    <div class="clear i_height"></div>
                    <%--<div class="mobileVersion-btn" onclick="window.open('shopData.do?method=printShopRQ');" class="blue_color" href="#">打 印</div>--%>
                      </div>
                     </div>
            </div>

        </bcgogo:hasPermission>
        <bcgogo:permission>
            <bcgogo:if resourceType="logic" permissions="WEB.VERSION.DISABLE_SCHEDULE">
            </bcgogo:if>
            <bcgogo:else>
                <bcgogo:hasPermission permissions="WEB.SCHEDULE.BASE" resourceType="menu">
                    <div class="m_label1" style="cursor: pointer;" menu-name="SCHEDULE"
                         url="navigator.do?method=schedule">
                        <div class="m_labelName2">待办事项</div>
                    </div>
                </bcgogo:hasPermission>
            </bcgogo:else>
        </bcgogo:permission>
        <bcgogo:hasPermission resourceType="menu" permissions="WEB.VEHICLE_CONSTRUCTION.BASE">
            <div class="m_label6" style="cursor: pointer;"
                 url="navigator.do?method=vehicleConstruction"
                 menu-name="VEHICLE_CONSTRUCTION">
                <div class="m_labelName2">车辆施工</div>
            </div>
        </bcgogo:hasPermission>
        <bcgogo:permission>
            <bcgogo:if resourceType="logic" permissions="WEB.VERSION.WASHBEAUTY_SHORTCUTS">
                <div class="m_label2" style="cursor: pointer;"
                     menu-name="TXN"
                     url="washBeauty.do?method=createWashBeautyOrder">
                    <div class="m_labelName2">洗车单</div>
                </div>
            </bcgogo:if>
            <bcgogo:else>
                <bcgogo:permission>
                    <bcgogo:hasPermission permissions="WEB.TXN.BASE" resourceType="menu">
                        <div class="m_label2" style="cursor: pointer;" keep-current-step="YES"
                             menu-name="TXN"
                             url="navigator.do?method=txnNavigator">
                            <div class="m_labelName2">进销存</div>
                        </div>
                    </bcgogo:hasPermission>
                </bcgogo:permission>
            </bcgogo:else>
        </bcgogo:permission>

        <bcgogo:hasPermission permissions="WEB.CUSTOMER_MANAGER.BASE">
            <div class="m_label3" style="cursor: pointer;" keep-current-step="YES"
                 menu-name="CUSTOMER_MANAGER"
                 url="navigator.do?method=customerManager">
                <div class="m_labelName2">客户管理</div>
            </div>
        </bcgogo:hasPermission>

        <bcgogo:hasPermission permissions="WEB.SUPPLIER_MANAGER.BASE">
            <div class="m_label5" style="cursor: pointer;" keep-current-step="YES"
                 url="navigator.do?method=supplierManager"
                 menu-name="SUPPLIER_MANAGER">
                <div class="m_labelName2">供应商管理</div>
            </div>
        </bcgogo:hasPermission>
        <bcgogo:hasPermission permissions="WEB.AUTOACCESSORYONLINE.BASE">
            <div class="m_label8" style="cursor: pointer;" keep-current-step="YES"
                 menu-name="AUTO_ACCESSORY_ONLINE"
                 url="navigator.do?method=autoAccessoryOnline">
                <div class="m_labelName2">供求中心</div>
            </div>
        </bcgogo:hasPermission>
        <bcgogo:permissionParam permissions="WEB.STAT.BASE,WEB.VERSION.FOUR_S_VERSION_BASE">
          <c:if test="${!WEB_VERSION_FOUR_S_VERSION_BASE && WEB_STAT_BASE}">
            <div class="m_label_stat" style="cursor: pointer;"
                 menu-name="STAT"
                 url="navigator.do?method=stat">
              <div class="m_labelName2">财务统计</div>
            </div>
          </c:if>
        </bcgogo:permissionParam>

        <bcgogo:hasPermission permissions="WEB.SYSTEM_SETTINGS.BASE">
            <div class="m_label4" style="cursor: pointer;" url="navigator.do?method=systemSetting"
                 menu-name="SYSTEM_SETTINGS">
                <div class="m_labelName2">系统管理</div>
            </div>
        </bcgogo:hasPermission>

    </div>
</div>

<iframe id="iframe_tippage" style="position:absolute;z-index:5; left:400px; top:400px; display:none;"
        allowtransparency="true" width="420px" height="200px" frameborder="0" src="">
</iframe>

<div id="div_brand_head" class="i_scroll" style="display:none;height:230px;width:204px;">
    <div class="Scroller-Container" id="Scroller-Container_id_head" style="width:200px;"></div>
</div>

<div id="mask" style="display:block;position: absolute;"></div>

<div id="div_brandvehicle" class="i_scroll" style="display:none;width:199px;">
    <div id="Scroller-1" style="width:100%;padding:0;margin:0;">
        <div id="Scroller-Container_vehicleid" style="width:100%;padding:0;margin:0;"></div>
    </div>
</div>


<div class="pop-upbox pop-upboxMaturity" id="overdueNotice" style="display:none;">
    <a class="pop_close"></a>
    <span class="pop_iconMaturity"></span>

    <div class="pop_info">
        <label class="pop_title">亲！您的软件试用期还剩
            <div><b class="yellow_color" id="remainDate"></b></div>
        </label>

        <div class="pop_tel">
            <label class="pop_line">店铺所有账号将在&nbsp;<b class="red_color" id="deadLine"></b></label>
            <label class="pop_line">无法正常使用！</label>
            <label class="pop_line">请尽快&nbsp;<a class="blue_color" href="#" onclick="toLoanTransfers();">在线支付</a>&nbsp;或联系客服付款哦！</label>
            <span class="pop_line">客服热线：</span>

            <div class="telephone">
                <span>0512-66733331</span>

            </div>
            <a class="pop_button">我知道了</a>
        </div>
    </div>
</div>

<div class="add_mask"></div>

     <%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>