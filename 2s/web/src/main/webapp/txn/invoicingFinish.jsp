<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page import="com.bcgogo.txn.dto.RepairOrderDTO" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<html xmlns="http://www.w3.org/1999/xhtml">

<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
<title id="title">${repairOrderDTO.licenceNo==null?"施工销售":repairOrderDTO.licenceNo}</title>
<%
    boolean storageBinTag = ServiceManager.getService(IShopConfigService.class).isStorageBinSwitchOn((Long) request.getSession().getAttribute("shopId"));//选配仓位功能 默认开启这个功能false
%>
<link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/moreHistory<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/draftOrder<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/invoicingFinish<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/customerTooltip<%=ConfigController.getBuildVersion()%>.css"/>
<c:choose>
    <c:when test="<%=storageBinTag%>">
        <link rel="stylesheet" type="text/css" href="styles/storageBinOn<%=ConfigController.getBuildVersion()%>.css"/>
    </c:when>
    <c:otherwise>
        <link rel="stylesheet" type="text/css" href="styles/storageBinOff<%=ConfigController.getBuildVersion()%>.css"/>
    </c:otherwise>
</c:choose>
<link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/supplierLook<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/yinshouyinfu<%=ConfigController.getBuildVersion()%>.css"/>

<style type="text/css">
    ui-dialog-titlebar ui-widget-header ui-corner-all ui-helper-clearfix{
        display: none;
    }
    .item1 input {
        text-overflow: clip;
        font-size: 12px;
    }

        /* 以下两个样式为解决服务下拉框只显示10个服务的问题。覆盖style.css中的内容, 为不影响搜索优化后其他页面使用到此样式 */
    #Scroller-1 {
        overflow-x: hidden;
        overflow-y: auto;
        height: 100%;
        width: 100%;
    }

    .i_scroll .Container {
        position: absolute;
        float: left;
        width: 100%;
        height: 100%;
    }

    .i_main{
        position: static;
    }

	/*为解决布局错乱的问题， 原页面style.css 中的布局有问题， 但考虑到可能影响全局布局样式， 故采用此种淫巧应对之*/
	.invalidImg {
		margin:-12px 0 0 18px;
	}

    .customer-vip{
        margin: 8px 0px 0 0;

    }

    .customer-touch{
        cursor:auto;
    }


</style>

<%@include file="/WEB-INF/views/header_script.jsp" %>
<script>
    var repairOrderId = '${repairOrderDTO.id}';
    var customerId = '${repairOrderDTO.customerId}';
    var customer = '${repairOrderDTO.customerName}';
    var vehicleId = '${repairOrderDTO.vechicleId}';
    var licenceNo = '${repairOrderDTO.licenceNo}';
    var submitBtnType = '${btnType}';
    var submitResultMsg = '${resultMsg}';
    var isCheckPriceFlag = true;
    <%Long vehicleIdTemp =(request.getAttribute("repairOrderDTO")==null?null:((RepairOrderDTO)request.getAttribute("repairOrderDTO")).getVechicleId());  %>
    var vechicleAndCustomerStatus = "<%=request.getParameter("customerId")!=null &&request.getParameter("customerId")!=""&&(vehicleIdTemp==null)?1:0%>";//0,表示新建单据或者是需要结算的单据，1表示从客户管理传值过来，2，表示先输入旧车牌号更换新车之后
    var isRepairPickingSwitchOn = <%=isRepairPickingSwitchOn%>;
</script>
<script type="text/javascript" src="js/utils/tableUtil<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/module/invoiceCommon<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/invoicesolr<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/script<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/invoice<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/inventorySearchIndex<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/vehiclelicenceNo<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/checkMobileAndTelphone<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/invoiceCustomerSearch<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/selectOptions<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/member<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/suggestion<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/draftOrderBox<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/page/txn/txn<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/detailsArrears<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/insuranceCommon<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript"
          src="js/statementAccount/statementAccountUtil<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript">
defaultStorage.setItem(storageKey.MenuUid,"VEHICLE_CONSTRUCTION_REPAIR");
defaultStorage.setItem(storageKey.MenuCurrentItem,"详情");

function domBlur(domObj, field) {
    $(domObj).parents("tr").find("td:eq(0)>input").eq(3).val("");
    $(domObj).parents("tr").find("td:eq(9)>span").first().html("0.0");
    $(domObj).parents("tr").find("td:eq(9)>input").eq(0).val("");
    $(domObj).parents("tr").find("td:eq(0)>input").eq(2).val("");
    if ("product_name" == field) {
        $(domObj).parents("tr").find("td:eq(2)>input").first().val("");
        $(domObj).parents("tr").find("td:eq(3)>input").first().val("");
        $(domObj).parents("tr").find("td:eq(4)>input").first().val("");
    } else if ("product_brand" == field) {
        $(domObj).parents("tr").find("td:eq(3)>input").first().val("");
        $(domObj).parents("tr").find("td:eq(4)>input").first().val("");
    } else if ("product_spec" == field) {
        $(domObj).parents("tr").find("td:eq(4)>input").first().val("");
    }
}

function usersolr(domObj, flag, position, searchField, e) {
    domObj.value = domObj.value.replace(/[\ |\\]/g, "");
    var searchValue = "";
    if ("keyup" == flag) {
        searchValue = domObj.value;
    }
    var inputArray = new Array(8);
    inputArray[0] = $(domObj).parents("tr").find("td:eq(1)>input[type!='hidden']")[0];
    inputArray[1] = $(domObj).parents("tr").find("td:eq(2)>input").first()[0];
    inputArray[2] = $(domObj).parents("tr").find("td:eq(3)>input").first()[0];
    inputArray[3] = $(domObj).parents("tr").find("td:eq(4)>input").first()[0];
    inputArray[4] = $(domObj).parents("tr").find("td:eq(8)>input").first()[0];
    inputArray[5] = $(domObj).parents("tr").find("td:eq(0)>input").eq(3)[0];
    inputArray[6] = $(domObj).parents("tr").find("td:eq(0)>input").eq(1)[0];
    inputArray[7] = $(domObj).parents("tr").find("td:eq(0)>input").eq(2)[0];
    var sv1 = "", sv2 = "", sv3 = "";
    if ("product_brand" == searchField) {
        sv1 = inputArray[0].value;
    } else if ("product_spec" == searchField) {
        sv1 = inputArray[0].value;
        sv2 = inputArray[1].value;
    }
    else if ("product_model" == searchField) {
        sv1 = inputArray[0].value;
        sv2 = inputArray[1].value;
        sv3 = inputArray[2].value;
    }
    searchSuggestion3(inputArray, position, searchValue, searchField, sv1, sv2, sv3, "", trCount1, 0);
}
//zhangjuntao 欠款的验证    todo 冗余代码 zhangjuntao   几张单据都有
var time = new Array(), timeFlag1 = true, timeFlag2 = true;
time[0] = new Date().getTime();
time[1] = new Date().getTime();
time[2] = new Date().getTime();
time[3] = new Date().getTime();
var reg = /^\d+(\.{0,1}\d*)$/;


$(document).ready(function () {


    var reg = new RegExp("[`~!@#$^&*()=|\\\\{\\}%_\\+\\-\"':;',\\[\\].<>/?~！@#￥……&*（）——|{}【】‘；：”“'。，、？·～《》]", "g");
    $("#chargeTimes").blur(function () {
        var chargeTimes = $("#chargeTimes").val();
        chargeTimes = chargeTimes.replace(reg, "");
        if (Number(chargeTimes) > 9999) {
            chargeTimes = "";
            showMessage.fadeMessage("35%", "40%", "slow", 2000, "购买次数不得大于9999");
        }
        $("#chargeTimes").val(chargeTimes);
    });
    $("#table_productNo_2 input").bind("mouseover", function () {
        this.title = this.value;
    });
    $(document).click(function (e) {
        var e = e || event;
        var target = e.srcElement || e.target;
        if (!target || !target.id || (target.type != "text" && target.id != "div_brand" && target.id != "div_works")) {
            $("#div_brand")[0].style.display = "none";
            $("#div_works").css({'display': 'none'});
        }
    });

    var permissionGoodsStorage = $("#permissionGoodsStorage").val();
    if (permissionGoodsStorage == "true" && !APP_BCGOGO.Permission.Version.IgnorVerifierInventory) {
        $(".lackMaterial").live('click', function () {
            var id1 = $(this).attr("id").split(".")[0];
            var id = id1 + ".productId";
            if ($("#id").val()) {
                window.location = "storage.do?method=getProducts&type=good&productIds=" + document.getElementById(id).value + "&repairOrderId=" + $("#id").val();
            }
            else {
                if ($("#serviceType").val() == 'REPAIR') {
                    alert("请先派单，然后点我入库");
                } else if ($("#serviceType").val() == 'SALES') {
                    alert("无此商品信息，请先采购或入库");
                }
            }
        });
    }


    var urlParams = $.url(window.location.search).param();

    if (urlParams && urlParams['task'] && urlParams['task'] === 'wash') {
        $("#carWash")[0].className = "title_hover";
        $("#carMaintain")[0].className = "";
        $("#table_carWash")[0].style.display = "block";
        $("#table_task")[0].style.display = "none";
        $("#table_productNo_2")[0].style.display = "none";
        $("#div_tablereservateTime")[0].style.display = "none";
        $("#save_div")[0].style.display = "block";
        $("#finish_div")[0].style.display = "block";
        $("#account_div")[0].style.display = "block";
        $("#serviceType")[0].value = "WASH";
        $(".tableInfo2").hide();
        $("#pageType").val('washcar');
        $(".invalidImg").hide();
        $(".reInput_div").hide();
        $("#orderStatusImag").hide();

        $(".i_tableStar").hide();
        $($(".i_tableStar").get(0)).show();
    } else if (urlParams['task'] === "maintain") {
        $("#carWash")[0].className = "";
        $("#carMaintain")[0].className = "title_hover";
        $("#table_carWash")[0].style.display = "none";
        $("#table_task")[0].style.display = "";
        $("#table_productNo_2")[0].style.display = "block";
        $("#div_tablereservateTime")[0].style.display = "block";
        $("#save_div")[0].style.display = "block";
        $("#finish_div")[0].style.display = "block";
        $("#account_div")[0].style.display = "block";
        $("#serviceType")[0].value = "REPAIR";
        $("#pageType").val('');
    }


    if ($("#input_startMileage")[0] != null && $("#input_startMileage")[0].value == "0") {
        $("#input_startMileage")[0].value = "";
    }

//    $("#settledAmount").live("keyup blur",function () {
//        var temp = dataTransition.amountConvert(time[0], time[1], "#settledAmount", timeFlag1);
//        if (temp != time[0]) {
//            timeFlag1 = false;
//            time[0] = temp;
//        }
//    });
//    $("#debt").keyup(function () {
//        var temp = dataTransition.amountConvert(time[2], time[3], "#debt", timeFlag2);
//        if (temp != time[2]) {
//            timeFlag2 = false;
//            time[2] = temp;
//        }
//    });
//    $("#debt").blur(function () {
//        if ("" == $("#debt").val()) {
//            $("#debt").val(0);
//        }
//    });
  <bcgogo:permissionParam permissions="WEB.VEHICLE_CONSTRUCTION.CONSTRUCT.BASE">
    if (${!permissionParam1}) {
        $("#bxId,#ycId"
                + ",#byId,#maintainMileage,#endDateStr"
                + ",#startDateStr,#startMileage,#vehicleHandover"
                + ",#fuelNumber,#settledAmount"
                + ",#debt,.table_input"
                + ",.serviceTotal,.edit1"
                + ",.opera1,.opera2"
                + ",.table_input")
                .attr("disabled", true);
    }
  </bcgogo:permissionParam>

    <%if(request.getAttribute("redirect")!=null){%>
    window.parent.location = "txn.do?method=getRepairOrderByVehicleNumber&vehicleNumber=<%=request.getAttribute("vehicleNumber")%>";
    <%}%>


    $("#endDateStr,#startDateStr").datetimepicker({
        "numberOfMonths": 1,
        "showButtonPanel": true,
        "changeYear": true,
        "changeMonth": true,
        "yearRange": "c-100:c+100",
        "yearSuffix": "",
        "onClose": function (dateText, inst) {
            if (!$(this).val()) {
                return;
            }
            if ($("#endDateStr").val() && $("#startDateStr").val() > $("#endDateStr").val()) {
                alert("预约出厂时间不能早于进厂时间，请修改!");
                $("#endDateStr").val($("#startDateStr").val());
                return;
            }
            if (GLOBAL.Util.getDate($("#startDateStr").val()).getTime() - GLOBAL.Util.getDate(GLOBAL.Date.getCurrentFormatDate()).getTime() > 0) {
                alert("请选择今天之前的时间。");
                $("#startDateStr").val(GLOBAL.Date.getCurrentFormatDate());
                return;
            }
        },
        "onSelect": function (dateText, inst) {
            if (inst.lastVal == dateText) {
                return;
            }
            $(this).val(dateText);
            var This = inst.input;
            if (inst.id == "startDateStr") {
                //如果选在非当前的时间 提醒逻辑
                if (!This.val()) return;
                if ((GLOBAL.Util.getDate(GLOBAL.Date.getCurrentFormatDate()).getTime() - new Date(Date.parse(This.val().replace(/-/g,
                        "/"))).getTime() > 0)) {
                    $("#dialog-confirm-invoicing").dialog('open')
                }
            }
        }
    });
    $("#dialog-confirm-invoicing").dialog({
        resizable: false,
        autoOpen: false,
        height: 140,
        modal: true,
        buttons: {
            "是": function () {
                $("#endDateStr").val($("#startDateStr").val());
                $(this).dialog("close");
            }, "否": function () {
                $("#startDateStr").val(GLOBAL.Date.getCurrentFormatDate());
                $("#endDateStr").val(GLOBAL.Date.getCurrentFormatDate());
                $(this).dialog("close");
            }
        }
    });
    $("#appRemindHelpBottom_dialog").dialog({
        resizable: false,
        autoOpen: false,
        height:360,
        width:360,
        modal: true
    });

    $("#appRemindHelp_dialog").dialog({
        resizable: false,
        autoOpen: false,
        height:360,
        width:360,
        modal: true
    });

    $("#appRemind_dialog").dialog({
        resizable: false,
        autoOpen: false,
        height:210,
        width:319,
        modal: true
    });

    $("#turnOff,#abandon").bind("click",function(){
        $("#appRemind_dialog").dialog('close');
    })

    $("#helpAbandon").bind("click",function(){
        $("#appRemindHelpBottom_dialog").dialog("close");
    })

    $("#send").bind("click",function(){
        if($("input[name='noRemind']:checked").size()==0){
            $("input[name='helpNoRemind']").attr("checked",false);
        }else{
            $('input[name="helpNoRemind"]').attr("checked","checked");
        }
        $("#appRemind_dialog").dialog('close');
        if($("#isApp").val()=="true"){
            var statusApp="";
            var statusSms="";
            if($("#sendApp").attr("checked")==true){
                    statusApp="ON";
            }else{
                statusApp="OFF";
            }
            if($("#sendSms").attr("checked")==true){
                statusSms="ON";
            }else{
                statusSms="OFF";
            }
            if($("#noRemind").attr("checked")==true){
                $("#noRemindFlag").val("noRemindChecked");
            }else{
                $("#noRemindFlag").val("noRemindUnchecked");
            }
            var ajaxSmsUrl="txn.do?method=sendMessageRemind";
            var ajaxSmsData={
                noRemind:$("#noRemindFlag").val(),
                sceneApp:"MOBILE_APP",
                statusApp:statusApp,
                sceneSms:"MOBILE_SMS",
                statusSms:statusSms,
                ids:$("#customerId").val(),
                type:8,
                mobile:($("#mobile").val()==""||$("#mobile").val()==null)?$("#vehicleMobile").val():$("#mobile").val(),
                licenceNo:$("#licenceNo").val(),
                id:$("#id").val()
            };
            bcgogoAjaxQuery.setUrlData(ajaxSmsUrl, ajaxSmsData);
            bcgogoAjaxQuery.ajaxQuery(function(json) {

            });
        }else{
            $("#appRemindHelpBottom_dialog").dialog("open");
            $(".ui-dialog,ui-widget,ui-widget-content,ui-corner-all,ui-draggable").css("width", "330px");
            $(".ui-dialog,ui-widget,ui-widget-content,ui-corner-all,ui-draggable").css("height", "317px");
        }

    })

    $("#helpSend").bind("click",function(){
        $("#appRemindHelpBottom_dialog").dialog("close");
        var statusApp="";
        var statusSms="";
        if($("#sendApp").attr("checked")==true){
            statusApp="ON";
        }else{
            statusApp="OFF";
        }
        if($("#sendSms").attr("checked")==true){
            statusSms="ON";
        }else{
            statusSms="OFF";
        }
        if($("#helpNoRemind").attr("checked")==true){
            $("#noRemindFlag").val("noRemindChecked");
        }else{
            $("#noRemindFlag").val("noRemindUnchecked");
        }
        var ajaxSmsUrl="txn.do?method=sendMessageRemind";
        var ajaxSmsData={
            noRemind:$("#noRemindFlag").val(),
            sceneApp:"MOBILE_APP",
            statusApp:statusApp,
            sceneSms:"MOBILE_SMS",
            statusSms:statusSms,
            ids:$("#customerId").val(),
            type:8,
            mobile:($("#mobile").val()==""||$("#mobile").val()==null)?$("#vehicleMobile").val():$("#mobile").val(),
            licenceNo:$("#licenceNo").val(),
            id:$("#id").val()
        };
        bcgogoAjaxQuery.setUrlData(ajaxSmsUrl, ajaxSmsData);
        bcgogoAjaxQuery.ajaxQuery(function(json) {
        });
    })
    $('input[name="sendApp"]').bind("click",function(){
        if($("#sendApp").attr("checked")==true){
            $("#isApp").val("true");
        }else{
            $("#isApp").val("false");
        }
    })

    $('input[name="sendSMS"]').bind("click",function(){
        if($("#sendSMS").attr("checked")==true){
            $("#isSms").val("true");
        }else{
            $("#isSms").val("false");
        }
    })

    $('input[name="helpNoRemind"]').bind("click",function(){
        if($("#helpNoRemind").attr("checked")==true){
            $("#noRemindFlag").val("noRemindChecked");
        }
    })

    $('input[name="noRemind"]').bind("click",function(){
        if($("#noRemind").attr("checked")==true){
            $("#noRemindFlag").val("noRemindChecked");
        }
    })

    $("#helpBottomTurnOff").bind("click",function(){
        $("#appRemindHelpBottom_dialog").dialog('close');
        $("#appRemind_dialog").dialog('open');
        $(".ui-dialog,ui-widget,ui-widget-content,ui-corner-all,ui-draggable").css("width", "330px");
        $(".ui-dialog,ui-widget,ui-widget-content,ui-corner-all,ui-draggable").css("height", "210px");
    })

    $("#helpTurnOff").bind("click",function(){
        $("#appRemindHelp_dialog").dialog('close');
        $("#appRemind_dialog").dialog('open');
        $(".ui-dialog,ui-widget,ui-widget-content,ui-corner-all,ui-draggable").css("width", "330px");
        $(".ui-dialog,ui-widget,ui-widget-content,ui-corner-all,ui-draggable").css("height", "210px");
    })

    $("#helpIcon").bind("click",function(){
        $("#appRemind_dialog").dialog('close');
        $("#appRemindHelp_dialog").dialog('open');
        $(".ui-dialog,ui-widget,ui-widget-content,ui-corner-all,ui-draggable").css("width", "330px");
        $(".ui-dialog,ui-widget,ui-widget-content,ui-corner-all,ui-draggable").css("height", "294px");

    })


    $("#createQualifiedBtn").bind("click",function(e){
        if (!$("#id").val()) {
            return;
        }
        window.location.href="txn.do?method=createQualified&repairOrderId=" + $("#id").val();
    });

    $("#bxId,#byId,#ycId").each(function () {
        $(this).attr("lastValue", $(this).val());
    });

    $("#byId,#bxId,#ycId").datepicker({
        "numberOfMonths": 1,
        "showButtonPanel": true,
        "changeYear": true,
        "changeMonth": true,
        "yearRange": "c-100:c+100",
        "yearSuffix": "",
        "onSelect": function (dateText, inst) {
            var lastValue = $(this).attr("lastValue");
            if (lastValue == dateText) {
                return;
            }

            if (dateText) {
                var myDate = GLOBAL.Date.getCurrentFormatDate();
                if (myDate.replace(/[- ]+/, "") > dateText.replace(/[- ]+/, "")) {
                    if ($(this).attr("id") == "byId") {
                        alert("保养日期请选择今天及以后的日期。");
                    } else if ($(this).attr("id") == "bxId") {
                        alert("保险日期请选择今天及以后的日期。");
                    } else if ($(this).attr("id") == "ycId") {
                        alert("验车请选择今天及以后的日期。");
                    }
                    $(this).val("");
                }
                else {
                    updateYuye();
                }
                $(this).attr("lastValue", dateText);
            }
        }
    });


    if (!$("#lastWashOrderId").val()) {
        $("#printWash").hide();
    }

    $("#printWash").bind("click", function () {
        if (!$("#lastWashOrderId").val()) {
            return;
        }
        window.showModalDialog("wash.do?method=printWashTicket&orderId=" + $("#lastWashOrderId").val() + "&now=" + new Date(),
                '', "dialogWidth=280px;dialogHeight=768px");
    });

    if ($('.td_startMile').attr('title').length <= 0) {
        if (!$("#startMileage").attr('disabled')) {
            $('.td_startMile').attr({'title': '双击可以编辑'});
        }
        $('.td_startMile').tooltip({delay: 0});
    }

    $('.td_startMile').dblclick(function () {
        if (!$("#startMileage").attr('disabled')) {
            $('.startMile').css({'display': 'none'});
            $('.startMileInput').css({'display': 'inline'});
            $('#startMileage').focus();
        }
    });

    document.onreadystatechange = beforeGetMemberData;
    function beforeGetMemberData(){
        if(document.readyState == "complete") {
            var customerId=$("#customerId").val()!=""?$("#customerId").val():"";
            var ajaxData={
                customerId: customerId
            };
            var ajaxUrl = "repair.do?method=getMemberData";
            bcgogoAjaxQuery.setUrlData(ajaxUrl, ajaxData);
            bcgogoAjaxQuery.ajaxQuery(function(json) {
                if(json){
                    var nodeId="VipSpan";
                    nodeId && tooltip(nodeId, json);
                }
            });
            if($("#isSend").val()!="SEND"){
                if($("#smsSwitch").val()=="true"){
                    var ajaxUrl="txn.do?method=judgeCustomerType";
                    var ajaxData={};
                    bcgogoAjaxQuery.setUrlData(ajaxUrl, ajaxData);
                    bcgogoAjaxQuery.ajaxQuery(function(json) {
                        if(json!=null){
                            if(json[0]){
                                $("#isApp").val(json[0]);
                                $('input[name="sendApp"]').attr("checked","checked");
                            }else{
                                $('input[name="sendApp"]').attr("checked",false);
                            }
                            if(json[2]=="老板/财务"){
                                $("#noRemindDiv").show();
                            }else{
                                $("#noRemindDiv").hide();
                            }
                            if(json[1]){
                                $("#isSms").val(json[1]);
                                $('input[name="sendSMS"]').attr("checked","checked");
                            }else{
                                $('input[name="sendSMS"]').attr("checked",false);
                            }
                            $('input[name="noRemind"]').attr("checked",false);

                        }

                        //公用
                        $(".ui-dialog-titlebar,ui-widget-header,ui-corner-all,ui-helper-clearfix").hide();
                        //appRemind_dialog
                        $("#appRemind_dialog").dialog('open');
                        $(".ui-dialog,ui-widget,ui-widget-content,ui-corner-all,ui-draggable").css("width", "330px");
                        $(".ui-dialog,ui-widget,ui-widget-content,ui-corner-all,ui-draggable").css("height", "210px");
                    });
                }else{
                    $("#noRemindFlag").val("afterNoRemindChecked");
                    var ajaxSmsUrl="txn.do?method=sendMessageRemind";
                    var ajaxSmsData={
                        noRemind:$("#noRemindFlag").val(),
                        ids:$("#customerId").val(),
                        type:8,
                        mobile:($("#mobile").val()==""||$("#mobile").val()==null)?$("#vehicleMobile").val():$("#mobile").val(),
                        licenceNo:$("#licenceNo").val(),
                        id:$("#id").val()
                    };
                    bcgogoAjaxQuery.setUrlData(ajaxSmsUrl, ajaxSmsData);
                    bcgogoAjaxQuery.ajaxQuery(function(json) {

                    });


                }

            }
        }
    }






});

//新客户要新增后才能选择卡
function selectCard() {
//      var r;
//      APP_BCGOGO.Net.syncGet({url:"member.do?method=checkNoMemberCard",dataType:"json",
//        success:function(json) {
//          r = json;
//        }
//      });
//
//      if("error" == r.resu)
//      {
//        alert("暂无会员卡种类,请先去客户管理\n界面设置会员卡种类！");
//        return;
//      }
    var statusFlag = true;

    if ($("#customerId").val()) {
        var r = APP_BCGOGO.Net.syncGet({async: false, url: "customer.do?method=checkCustomerStatus", data: {customerId: $("#customerId").val(), now: new Date()}, dataType: "json"});
        if (!r.success) {
            statusFlag = false;
        }
    }

    if (!statusFlag) {
        alert("此客户已被删除或合并，不能购卡！");
        return;
    }


    if (!jQuery("#customerId").val() && !jQuery("#customerName").val()) {
        alert("请填写客户信息");
        return;
    }
    //客户id为空，根据手机号来判定是不是老客户，不是就增加并且返回id赋值给页面
//      if(!jQuery("#customerId").val())
//      {
    jQuery.ajax({
        type: "POST",
        url: "customer.do?method=checkCustomerExistAndSave",
        data: {
            customerName: jQuery("#customerName").val(),
            mobile: jQuery("#mobile").val(),
            landLine: jQuery("#landLine").val(),
            licenceNo: $("#licenceNo").val(),
            brand: $("#brand").val(),
            model: $("#model").val(),
            customerId: $("#customerId").val(),
            tsLog: 10000000000 * (1 + Math.random())
        },
        cache: false,
        dataType: "json",
        success: function (data) {
            var msg = data.msg;
            var id = "";
            if ("existGtOne" == msg) {
                alert("存在多个客户同一个手机，请修改客户手机信息");
                return;
            }
            else if ("existOne" == msg) {
                id = data.id;
                jQuery("#customerId").val(id);
                $("#vechicleId").val(data.vehicleId);
                bcgogo.checksession({"parentWindow": window.parent, 'iframe_PopupBox': $("#iframe_CardList")[0], 'src': 'member.do?method=selectCardList&customerId=' + id + '&time=' + new Date()});
                return;
            }
            else if ("saveSuccess" == msg) {
                id = data.id;
                jQuery("#customerId").val(id);
                $("#vechicleId").val(data.vehicleId);
                bcgogo.checksession({"parentWindow": window.parent, 'iframe_PopupBox': $("#iframe_CardList")[0], 'src': 'member.do?method=selectCardList&customerId=' + id + '&time=' + new Date()});
                return;
            }
            else if ("saveError" == msg) {
                alert("客户信息保存失败，请重新点击！");
                return;
            }
            else {
                alert("网络异常");
                return;
            }
        },
        error: function () {
            alert("网络异常");
            return;
        }
    });
//      }
//      else
//      {
//        bcgogo.checksession({"parentWindow":window.parent,'iframe_PopupBox':$("#iframe_CardList")[0],'src':'member.do?method=selectCardList&customerId='+jQuery("#customerId").val()+'&time='+new Date()});
//      }
}

//预约功能
function updateYuye() {

    var maintainTimeStr = $("#byId").val();
    var insureTimeStr = $("#bxId").val();
    var examineTimeStr = $("#ycId").val();
    var customerId = $("#customerId").val();
    var vehicleId = $("#vechicleId").val();
    var maintainMileage = $("#maintainMileage").val();
    if (vehicleId == null || vehicleId == "") {
        return;
    }
    $.ajax({
        type: "POST",
        url: "unitlink.do?method=addOrUpdateCustomerVehicle&" + Math.random() * 10000000,
        cache: false,
        data: {customerId: customerId, vehicleId: vehicleId, maintainTimeStr: maintainTimeStr,
            insureTimeStr: insureTimeStr, examineTimeStr: examineTimeStr,maintainMileage: maintainMileage},
        success: function (data) {
            showMessage.fadeMessage("45%", "24%", "slow", 3000, "预约服务更新成功！");
        },
        error: function (e) {
            showMessage.fadeMessage("45%", "24%", "slow", 3000, "网络异常！");
        }
    });
}
$().ready(function () {
    $("#memberCardId").click(function () {
        bcgogo.checksession({"parentWindow": window.parent, 'iframe_PopupBox': $("#iframe_buyCard")[0], 'src': 'member.do?method=buyCard&customerId=' + jQuery("#customerId").val() + '&cardId=' + jQuery("#memberCardId").val() + '&time=' + new Date()});
    });
});

function addNewRepairOrder() {
    window.open($("#basePath").val() + "txn.do?method=getRepairOrderByVehicleNumber&task=maintain", "_blank");
}
function tooltip(id, customer){
    var memberInfo = customer;
    var node = setTooltipHtml(id, customer.customerIdStr);
    setTooltipContent(node,memberInfo);
}

function setTooltipHtml(id , customerId){
    var root = $('#'+id);
    var body = $('<div class="tooltipBody"></div>').append('<a class="icon_close"></a>') .append('<div class="title"><div style="float: left;width: 60px;"><strong>会员信息</strong></div><div style="float: right;width: 72px;"><a href="javascript:void(0)">查看更多资料</a></div></div>').append('<div class="prompt-left"></div>').append('<div class="prompt-right"></div>')
    var node = $('<div class="tooltip" style="margin:0px 0px 0px -12px;display:none;"></div>').append('<div class="tooltipTop"></div>').append(body).append('<div class="tooltipBottom"></div>')
    root.append(node).bind('mouseover',function(e){
        node.addClass("customer-touch");
        node.show();
    }).bind('mouseout',function(){
        node.hide();
    });
    $('.icon_close',node).click(function(){
        node.hide();
    });
    $('a', node).last().click(function(){
        window.open('unitlink.do?method=customer&customerId=' + customerId);
    });
    return node;
}

function setTooltipContent(node,memberInfo){
    var leftList = [{name:'卡号：',val:'memberNo'}, {name:'卡类型：',val:'type'}, {name:'入会日期：',val:'joinDateStr'}, {name:'过期日期：',val:'serviceDeadLineStr'}, {name:'会员储值：',val:'balanceStr'}];
    var left = $('.prompt-left',node);
    var right = $('.prompt-right',node);
    $.each(leftList,function(i,n){
        var val = memberInfo[n.val];
        if(val && val.length>11){
            val = '<span title="' + val + '">' + val.substr(0,8) + '...</span>';
        } else {
            val = G.normalize(val);
        }
        left.append('<div class="clear"><div class="left">' + n.name + '</div><div class="right">' + val + '</div></div>');
    });
    var memberServices = memberInfo.memberServiceDTOs;
    if (memberServices) {
        right.append('<div>服务项目(共' + memberServices.length + '项)</div>');
        $.each(memberServices, function (i, memberService) {
            var name = memberService.serviceName && memberService.serviceName.length > 11 ? '<span title="' + memberService.serviceName + '">' + memberService.serviceName.substr(0, 8) + '...</span>' : memberService.serviceName;
            right.append('<div style="overflow: hidden;"><div class="div left">'+ name +'</div><div class="div right">' + memberService.timesStr + '</div></div>');
        });
    }else{
        right.append('<div class="gray_color">暂无服务项目）</div>');
    }
}


</script>

<%
    boolean isMemberSwitchOn = WebUtil.checkMemberStatus(request);
%>

</head>


<body class="bodyMain">

<%@ include file="/WEB-INF/views/header_html.jsp" %>
<%@ include file="/common/messagePrompt.jsp" %>
<bcgogo:permissionParam permissions="WEB.TXN.PURCHASE_MANAGE.STORAGE,WEB.VEHICLE_CONSTRUCTION.BASE" resourceType="menu">
<input type="hidden" id="permissionGoodsStorage" value="${permissionParam1}"/>
<input type="hidden" id="invoicingPermission" value="${permissionParam2}"/>
</bcgogo:permissionParam>
<input type="hidden" id="basePath" name="bathPath" value="<%=basePath%>"/>
<input type="hidden" value="<%=isMemberSwitchOn%>" id="isMemberSwitchOn"/>
<input type="hidden" value="${copyRepairOrderDTO}" id="copyRepairOrderDTO"/>
<input type="hidden" value="${insuranceStatus}" id="insuranceStatus" name="insuranceStatus"/>
<input type="hidden" value="${smsSwitch}" id="smsSwitch" name="smsSwitch"/>
<input type="hidden" value="noRemindUnchecked" id="noRemindFlag" name="noRemindFlag"/>
<input type="hidden" value="" id="isApp" name="isApp"/>
<input type="hidden" value="" id="isSms" name="isSms"/>



<div class="i_main">
<div class="mainTitles">
    <bcgogo:permission>
        <bcgogo:if resourceType="logic" permissions="WEB.VERSION.DISABLE_VEHICLE_CONSTRUCTION">
            <%--<jsp:include page="txnNavi.jsp">
                <jsp:param name="currPage" value="repair"/>
            </jsp:include>--%>
            <jsp:include page="vehicleConstructionNavi.jsp">
                <jsp:param name="currPage" value="invoicingOrder"/>
                <jsp:param name="orderId" value="${repairOrderDTO.id}"/>
            </jsp:include>
        </bcgogo:if>
        <bcgogo:else>
            <jsp:include page="vehicleNavi.jsp">
                <jsp:param name="currPage" value="invoicingOrder"/>
                <jsp:param name="orderId" value="${repairOrderDTO.id}"/>
            </jsp:include>
        </bcgogo:else>
    </bcgogo:permission>
</div>
<div class="i_mainRight" id="i_mainRight">
<form:form commandName="repairOrderDTO" id="repairOrderForm" action="repair.do?method=dispatchRepairOrder" method="post"
           name="thisform">
<jsp:include page="unit.jsp"/>
<input type="hidden" value="${repairOrderDTO.receiptNo}" id="receiptNo" name="receiptNo"/>
<form:hidden path="id" value="${repairOrderDTO.id}" cssClass="checkStringEmpty"/>
<form:hidden path="repairOrderTemplateName"
             value="${repairOrderDTO.repairOrderTemplateName==null?'':repairOrderDTO.repairOrderTemplateName}"/>
<form:hidden path="repairOrderTemplateId"
             value="${repairOrderDTO.repairOrderTemplateId==null?'':repairOrderDTO.repairOrderTemplateId}"/>
<input id="orderType" name="orderType" value="repairOrder" type="hidden"/>
<input type="hidden" id="insuranceOrderId" value="${repairOrderDTO.insuranceOrderDTO.id}" />
<input type="hidden" id="status" name="status" value="${repairOrderDTO.status}"/>
<input type="hidden" id="draftOrderIdStr" name="draftOrderIdStr"
       value="${repairOrderDTO.draftOrderIdStr==null?"":repairOrderDTO.draftOrderIdStr}"/>
<input type="hidden" id="dealingType" name="dealingType" value="${repairOrderDTO.insuranceOrderDTO.statusStr}"/>
<input type="hidden" id="isSend" name="isSend" value="${repairOrderDTO.isSmsSend}"/>
<form:hidden path="statementAccountOrderId" value="${repairOrderDTO.statementAccountOrderId}"/>
<%--<form:hidden path="status" value="${repairOrderDTO.status}"/>--%>
<form:hidden path="receivableId" value="${repairOrderDTO.receivableId}"/>
<form:hidden path="vechicleId" value="${repairOrderDTO.vechicleId}"/>
<input type="hidden" id="appointOrderId" value="${repairOrderDTO.appointOrderId}" />
<form:hidden id="serviceType" path="serviceType" value="${repairOrderDTO.serviceType}"/> <%--判断是维修还是洗车还是销售--%>
<form:checkbox id="sendMemberSms" path="sendMemberSms" style="display:none;"/>

<div class="tuihuo_tb">

<table>
    <colgroup>
        <col width="80">
    </colgroup>
    <tbody>
    <tr>
        <td>车辆信息</td>
    </tr>
    </tbody>
</table>

<table class="clear" id="tb_tui">
    <colgroup>
        <col width="75">
        <col width="75">
        <col width="70">
        <col width="95">
        <col width="80">
        <col width="80">
        <col width="80">
        <col width="150">
    </colgroup>
    <tbody>
    <tr>
        <td class="td_title">车牌号</td>
        <td>${repairOrderDTO.licenceNo}
            <input type="hidden" id="licenceNo" value="${repairOrderDTO.licenceNo}"/>
            <input type="hidden" id="vehicleContact" value="${repairOrderDTO.vehicleContact}"/>
            <input type="hidden" id="vehicleMobile" value="${repairOrderDTO.vehicleMobile}"/>
        </td>
        <td class="td_title">品牌</td>
        <td>${repairOrderDTO.brand}
            <input type="hidden" id="brand" value="${repairOrderDTO.brand}"/>
        </td>
        <td class="td_title">车型</td>
        <td>${repairOrderDTO.model}
            <input type="hidden" id="model" value="${repairOrderDTO.model}"/>
        </td>
        <td class="td_title">购车日期</td>
        <td>${vehicleDTO.carDateStr}</td>
    </tr>

    <tr>
        <td class="td_title">发动机号</td>
        <td><span id="vehicleEngineNo" title="${repairOrderDTO.vehicleEngineNo}">${repairOrderDTO.vehicleEngineNo}</span>
        <script type="text/javascript">
            $('#vehicleEngineNo').text() && $('#vehicleEngineNo').text().length>15 && $('#vehicleEngineNo').text($('#vehicleEngineNo').text().substr(0,14)+'...');
        </script>
        </td>
        <td class="td_title">车架号</td>
        <td>${repairOrderDTO.vehicleChassisNo}</td>
        <td class="td_title">车辆颜色</td>
        <td>${repairOrderDTO.vehicleColor}</td>
        <td></td>
        <td></td>
    </tr>

    <tr>
        <td class="td_title">客户名</td>
        <td>${repairOrderDTO.customerName}
            <c:if test="${not empty repairOrderDTO.memberType}">
                <span class="customer-vip" style="line-height: 13px;" id="VipSpan">VIP</span>
            </c:if>



            <input type="hidden" id="customerName" value="${repairOrderDTO.customerName}"/>
            <input type="hidden" id="customerId" value="${repairOrderDTO.customerId}"/>
        </td>
        <td class="td_title">客户手机</td>
        <td>${repairOrderDTO.mobile}
            <input type="hidden" id="mobile" value="${repairOrderDTO.mobile}"/>
        </td>
        <td class="td_title">车主</td>
        <td>${repairOrderDTO.vehicleContact}</td>
        <td class="td_title">车主手机</td>
        <td>${repairOrderDTO.vehicleMobile}</td>
    </tr>

    <tr>
        <td class="td_title">座机</td>
        <td>${repairOrderDTO.landLine}
            <input type="hidden" id="landLine" value="${repairOrderDTO.landLine}"/>
        </td>
            <td class="td_title">联系地址</td>
            <td colspan="3">${repairOrderDTO.address}</td>
            <td class="td_title">当前应收应付</td>
            <td class="qian_red">
                <div class="pay" id="duizhan" style="text-align:left;margin-left: 25px">
                    <span class="red_color payMoney">收¥<span id="receivable" >${totalReceivable}</span></span>
                    <span class="green_color fuMoney">付¥<span id="payable">${repairOrderDTO.totalReturnDebt > 0? repairOrderDTO.totalReturnDebt: 0}</span></span>
                </div>
            </td>
    </tr>
    <tr>
        <td class="td_title">进厂时间</td>
        <td>${repairOrderDTO.startDateStr}</td>
        <td class="td_title">预计出厂</td>
        <td>${repairOrderDTO.endDateStr}</td>
        <td class="td_title">车辆里程</td>
        <td class="td_startMile">
            <span class='startMile'>${repairOrderDTO.startMileage}</span>
            <span class='startMileInput'><form:input path="startMileage" value="${repairOrderDTO.startMileage}"
                                                     cssClass="checkStringEmpty" maxlength="10"></form:input></span>
            公里
        </td>
        <td class="td_title">剩余油量</td>
        <td>
                ${fuelNumberList[repairOrderDTO.fuelNumber]}
        </td>
    </tr>
    </tbody>
</table>

<table>
    <col width="80"/>
    <tr>
        <td>故障说明</td>
    </tr>
</table>
<div class="description" id="tb_tui">
    <div style="height:141px; width:600px; border:1px solid #CCCCCC; float:left;">${repairOrderDTO.description}</div>
    <img src="images/discription.png"/>
</div>

<div class="clear" ></div>
<table>
    <colgroup>
        <col width="80">
        <col width="150">
    </colgroup>
    <tbody>
    <tr>
        <td>施工信息</td>
        <td style="width:200px;">接车人：${repairOrderDTO.vehicleHandover}</td>
    </tr>
    </tbody>
</table>

<table class="clear" id="tb_tui">
    <colgroup>
        <col width="80">
        <col width="200">
        <col width="150">
        <col width="150">
        <col width="150">
        <col width="150">
        <col>
    </colgroup>
    <tbody>
    <tr class="tab_title">
        <td>序号</td>
        <td>施工内容</td>
        <td>实际工时</td>
        <td>工时费</td>
        <td>施工人</td>
        <td>营业分类</td>
        <td>备注</td>
    </tr>

    <c:forEach items="${repairOrderDTO.serviceDTOs}" var="serviceDTO" varStatus="status">
        <tr class="item">
            <td>${status.index+1}</td>
            <td>${serviceDTO.service}</td>
            <td>${serviceDTO.actualHours}</td>
            <td>${serviceDTO.total}</td>
            <td><span class="workersSpan">${serviceDTO.workers}</span></td>
            <td>${serviceDTO.businessCategoryName}</td>
            <td>${serviceDTO.memo}</td>
        </tr>
    </c:forEach>
    <tr>
        <td colspan="2" style="text-align:right; padding-right:20px;">合计：</td>
        <td>${repairOrderDTO.actualHoursTotal}</td>

        <td colspan="4" style="text-align:left; padding-left:60px;">${repairOrderDTO.serviceTotal}</td>
    </tr>
    </tbody>
</table>

<table>
    <colgroup>
        <col width="80">
        <col width="80">
    </colgroup>
    <tbody>
    <tr>
        <td>销售信息</td>
        <bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.STOREHOUSE">
            <td style="width:15%;">仓库：${repairOrderDTO.storehouseName}</td>
        </bcgogo:hasPermission>
        <td style="width:200px;">销售人：${repairOrderDTO.productSaler}</td>
    </tr>
    </tbody>
</table>

<table class="clear" id="tb_tui">
    <colgroup>
        <col width="75">
        <col width="75">
        <col width="70">
        <col width="95">
        <col width="80">
        <col width="80">
        <col width="80">
        <col width="80">
        <col width="95">
        <col width="95">
        <col width="95">
    </colgroup>
    <tbody>
    <tr class="tab_title">
        <td style="width:10%;">商品编号</td>
        <td style="width:10%;">品名</td>
        <td style="width:10%;">品牌/产地</td>
        <td style="width:10%;">规格</td>
        <td style="width:10%;">型号</td>
        <td style="width:10%;">单价</td>
        <td style="width:10%;">数量</td>
        <td style="width:5%;">单位</td>
        <td style="width:10%;">金额</td>
        <td style="width:5%;">货位</td>
        <td style="width:10%;">营业分类</td>
    </tr>

    <c:forEach items="${repairOrderDTO.itemDTOs}" var="itemDTO" varStatus="status">

        <tr class="item1">
            <td>${itemDTO.commodityCode!=null?itemDTO.commodityCode:""}</td>
            <td>${itemDTO.productName}</td>
            <td>${itemDTO.brand}</td>
            <td>${itemDTO.spec}</td>
            <td>${itemDTO.model}</td>
            <td>${itemDTO.price}</td>
            <td>${itemDTO.amount}</td>
            <td>${itemDTO.unit}</td>
            <td>${itemDTO.total}</td>
            <td class="storage_bin_td">
                    <span id="itemDTOs${status.index}.storageBinSpan"
                          name="itemDTOs[${status.index}].storageBinSpan">${itemDTO.storageBin}</span>
            </td>
            <td>${itemDTO.businessCategoryName}</td>
        </tr>
    </c:forEach>
    <tr>
        <td colspan="5" style="text-align:right;">合计：</td>
        <td colspan="6">${repairOrderDTO.salesTotal}</td>
    </tr>
    <tr>
        <td class="td_title">备注</td>
        <td colspan="10"><span class="sale-memo">${repairOrderDTO.memo}</span></td>
    </tr>
    </tbody>
</table>
<c:if test="${repairOrderDTO.otherIncomeItemDTOList != null}">
    <span style="margin-left:10px">其他费用信息</span>
    <table class="clear" id="tb_tui">
        <col width="360"/>
        <col width="360"/>
        <col/>
        <tr class="tab_title">
            <td style="width:300px;">其他费用</td>
            <td>金额</td>
            <td>备注</td>
        </tr>

        <c:forEach items="${repairOrderDTO.otherIncomeItemDTOList}" var="itemDTO" varStatus="status">
            <c:if test="${itemDTO!=null}">
                <tr class="item">
                    <td>${itemDTO.name}</td>
                    <td>${itemDTO.price}</td>
                    <td>${itemDTO.memo}</td>
                </tr>
            </c:if>
        </c:forEach>
        <tr>
            <td colspan="2">合计：</td>
            <td>${repairOrderDTO.otherIncomeTotal}</td>
        </tr>

    </table>
</c:if>
<div class="height"></div>
<c:if test="${receptionRecordDTOs != null}">
<strong class="jie_info clear">结算信息</strong>
        <div class="jie_detail clear almost">
          <c:if test="${repairOrderDTO.memberDiscountRatio != null}" var="isMember">
           <div>应收总额：&nbsp;<span class="borders">${repairOrderDTO.total}元</span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${repairOrderDTO.memberDiscountRatio}折后价格：<span>${repairOrderDTO.afterMemberDiscountTotal}元</span></div>
          </c:if>
          <c:if test="${!isMember}">
           <div>应收总额：&nbsp;<span>${repairOrderDTO.total}元</span></div>
          </c:if>
            <div style="">实收总计：&nbsp;<span>${repairOrderDTO.settledAmount}元</span></div>
            <div style="">优惠总计：&nbsp;<span>
              <c:if test="${repairOrderDTO.memberDiscountRatio != null}" var="isMember">

                  ${afterMemberDeduction}元
              </c:if>
              <c:if test="${!isMember}">
                ${repairOrderDTO.orderDiscount}元
              </c:if>
            </span></div>
            <div style="">挂账金额：&nbsp;<span class="red_color" id="orderDebt" debtVal="${repairOrderDTO.debt}">${repairOrderDTO.debt}元</span></div>
            <c:if test="${repairOrderDTO.couponAmount!=null}">
                <div style="">代金券：&nbsp;<span style="opacity:0;width:auto;">空</span><span class="red_color">${repairOrderDTO.couponAmount}元</span></div>
            </c:if>
        <table cellpadding="0" cellspacing="0" class="tabDan clear tabRu">
        <col width="120">
        <col width="60">
        <col width="90">
        <col width="90">
        <col width="70">
        <col width="70">
        <col>

        <tr  class="tabBg">
        	<td>结算日期</td>
            <td >结算人</td>
            <td >上次结余</td>
            <td >本次实收</td>
        	<td >本次优惠</td>
            <td >本次挂账</td>
            <td >附加信息</td>
        </tr>
      <c:forEach items="${receptionRecordDTOs}" var="receptionRecordDTO" varStatus="status">

        <tr>
        	<td>${receptionRecordDTO.receptionDateStr}</td>
            <td>${receptionRecordDTO.payee}</td>

            <td>
          <c:if test="${status.index==0}" var="isFirstRecord">
                       &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;--
          </c:if>
          <c:if test="${!isFirstRecord}">
            ${receptionRecordDTO.originDebt}
          </c:if>
            </td>
            <td>${receptionRecordDTO.amount}</td>
            <td>${receptionRecordDTO.discount}</td>
            <td>${receptionRecordDTO.remainDebt}</td>
            <td>
              <c:if test="${receptionRecordDTO.cash>0}">
                    现金：¥${receptionRecordDTO.cash}<br />
              </c:if>

              <c:if test="${receptionRecordDTO.bankCard>0}">
                    银联：¥${receptionRecordDTO.bankCard}<br />
              </c:if>

              <c:if test="${receptionRecordDTO.cheque>0}">
                    支票：¥${receptionRecordDTO.cheque}
                    使用支票号${receptionRecordDTO.chequeNo}<br/>
              </c:if>
              <c:if test="${receptionRecordDTO.memberId != null && receptionRecordDTO.memberBalancePay>0}">
                    会员卡：¥${receptionRecordDTO.memberBalancePay}
                    卡号：${receptionRecordDTO.memberNo}
                <c:if test="${repairOrderDTO.afterMemberDiscountTotal != repairOrderDTO.total}">
                     ${receptionRecordDTO.memberDiscountRatio*10}折优惠<br />
                </c:if>
              </c:if>
              <c:if test='${"CUSTOMER_STATEMENT_DEBT" eq receptionRecordDTO.orderTypeEnum}'>
                    对账结算：¥${receptionRecordDTO.statementAmount}
                    对账单号：<a class="blue_color" onclick="openStatementOrder('${repairOrderDTO.statementAccountOrderId}');" href="#">${receiveNo}</a>
                <br />
              </c:if>
              <c:if test="${not empty receptionRecordDTO.toPayTimeStr}">
                 预计还款日期：${receptionRecordDTO.toPayTimeStr}
              </c:if>
            </td>
        </tr>

      </c:forEach>


        <%--<tr>--%>
        	<%--<td>2012-12-12 12:23</td>--%>
            <%--<td>张三</td>--%>
            <%--<td>4700.0</td>--%>
            <%--<td>10000.0</td>--%>
            <%--<td>83.0</td>--%>
            <%--<td>4700.0</td>--%>
            <%--<td>--%>
                <%--对账结算：¥700.0&nbsp;&nbsp;对账单号：<a class="blue_color" href="#">GZ3232323232</a>--%>
            <%--</td>--%>
        <%--</tr>--%>
        </table>
        </div></div>
</c:if>
</div>

<ul class="i_operaPic clearfix">
    <li class="liRepeal">
      <bcgogo:permission>
        <bcgogo:if permissions="WEB.VEHICLE_CONSTRUCTION.CONSTRUCT.CANCEL">
          <c:if test="${ repairOrderDTO.statementAccountOrderId == null}">
            <div class="invalidImg">
              <div>
                <input id="nullifyBtn" type="button" onfocus="this.blur();">
              </div>
              <div class="invalidWords" id="invalidWords">作废</div>
            </div>
          </c:if>
        </bcgogo:if>
      </bcgogo:permission>
    </li>
    <li class="liReinput">
      <%--<bcgogo:permission>--%>
        <%--<bcgogo:if permissions="WEB.VEHICLE_CONSTRUCTION.CONSTRUCT.COPY">--%>
          <c:if test="${repairOrderDTO.status eq 'REPAIR_SETTLED' || repairOrderDTO.status eq 'REPAIR_REPEAL' }">
            <div class="copyInput_div" id="copyInput_div">
                <input id="copyInput" type="button" onfocus="this.blur();"/>
                <div class="copyInput_text_div" id="copyInput_text">复制</div>
            </div>
        </c:if>
        <%--</bcgogo:if>--%>
      <%--</bcgogo:permission>--%>
    </li>
    <c:if test="${not empty repairOrderDTO && empty repairOrderDTO.insuranceOrderDTO && repairOrderDTO.status!='REPAIR_REPEAL'}">
        <li class="liReinput">
            <bcgogo:hasPermission permissions="WEB.VEHICLE_CONSTRUCTION.INSURANCE">
                <div class="btn_div_Img" id="toInsuranceDiv">
                    <input type="button" id="toInsuranceBtn" class="sureInventory" value="" onfocus="this.blur();"/>
                    <div style="width:100%; ">保险理赔</div>
                </div>
            </bcgogo:hasPermission>
        </li>
    </c:if>
    <c:if test="${not empty repairOrderDTO && empty repairOrderDTO.qualifiedNo && repairOrderDTO.status!='REPAIR_REPEAL'}">
    <li class="liReinput" style="margin-left: 5px;">
      <div class="btn_div_Img" style="width: 72px;margin-left: 0px">
          <input type="button" id="createQualifiedBtn" class="qualified" value="" onfocus="this.blur();"/>
          <div style="width:100%; ">生成合格证</div>
      </div>
    </li>
    </c:if>
    <li class="right_li">
      <bcgogo:permission>
        <bcgogo:if permissions="WEB.VEHICLE_CONSTRUCTION.CONSTRUCT.PRINT">
            <div class="btn_div_Img" id="print_div">
                <div>
                    <input id="printBtn" type="button" class="print j_btn_i_operate" onfocus="this.blur();"/>
                </div>
                <div class="optWords">打印</div>
            </div>
        </bcgogo:if>
      </bcgogo:permission>
    </li>
    <li class="right_li" style="margin-left: 15px;">
        <bcgogo:hasPermission permissions="WEB.TXN.SALE_MANAGE.RETURN">
            <c:if test="${repairOrderDTO.status eq 'REPAIR_SETTLED' && repairOrderDTO.containMaterial}">
                <div class="salesReturn_invoice_div" id="salesReturnDiv">
                    <input id="createSalesReturn" type="button" onfocus="this.blur();"/>

                    <div class="salesReturn_text_invoice_div" id="createSalesReturn_text">退货</div>
                </div>
            </c:if>
        </bcgogo:hasPermission>
    </li>
    <li class="right_li">
        <bcgogo:hasPermission permissions="WEB.CUSTOMER_MANAGER.CUSTOMER_DATA.ARREARS">
            <c:if test="${repairOrderDTO.status=='REPAIR_SETTLED' && repairOrderDTO.debt>0}">
                <div class="btn_div_Img">
                    <input type="button" id="repairDebtAccount" class="saleAccount j_btn_i_operate"
                           onclick="toReceivableSettle('${repairOrderDTO.customerId}','repairOrder')"
                           onfocus="this.blur();"/>
                    <div class="optWords">欠款结算</div>
                </div>
            </c:if>
        </bcgogo:hasPermission>
    </li>

    <c:if test="${repairOrderSecondaryDTO == null}">
        <li class="right_li">
            <div class="btn_div_Img" id="secondary_div">
                <div>
                    <input id="secondaryBtn" type="button" class="secondary j_btn_i_operate" onfocus="this.blur();"/>
                </div>
                <div class="optWords">结算附表</div>
            </div>
        </li>
    </c:if>

    <c:if test="${ repairOrderDTO.status eq 'REPAIR_SETTLED' &&repairOrderDTO.finishOrderDownType eq 'jiangsu' }">
        <li class="right_li">
            <div class="btn_div_Img" id="clearList_div">
                <div>
                    <input id="clearListBtn" type="button" class="clearList j_btn_i_operate"
                           onclick="toExportList('${repairOrderDTO.id}')"
                           onfocus="this.blur();"/>
                </div>
                <div class="optWords">江苏省结算清单</div>
            </div>
        </li>
    </c:if>
</ul>



<input id="isAllMakeTime" type="hidden" value="0">
<%--缓存更多客户信息--%>
<input type="hidden" id="hidName"/>
<input type="hidden" id="hidShortName"/>
<input type="hidden" id="hidAddress"/>
<input type="hidden" id="hidContact"/>
<input type="hidden" id="hidMobile"/>
<input type="hidden" id="hidPhone"/>
<input type="hidden" id="hidFax"/>
<input type="hidden" id="hidMemberNumber"/>
<input type="hidden" id="hidBirthdayString"/>
<input type="hidden" id="hidQQ"/>
<input type="hidden" id="hidEmail"/>
<input type="hidden" id="hidBank"/>
<input type="hidden" id="hidBankAccountName"/>
<input type="hidden" id="hidAccount"/>
<input type="hidden" id="isPrint" value="${repairOrderDTO.print}" name="print">
</form:form>
</div>
<%--购卡续卡完后回来调用这个input的click方法来初始化车主信息--%>
<input type="hidden" id="callBackBuyCard"/>
<input type="hidden" id="lastWashOrderId" value="${lastWashOrderId}">


<div class="zuofei" id="orderStatusImag"></div>
<div id="mask" style="display:block;position: absolute;">

</div>
<div id="isInvo"></div>

<span id="washTimes" style="display: none">${customerCardDTO.washRemain}</span>

<div id="div_brand" class="i_scroll" style="display:none;">
    <div class="Container">
        <div id="Scroller-1">
            <div class="Scroller-Container" id="Scroller-Container_id">
            </div>
        </div>
    </div>
</div>


<div id="div_works" class="i_scroll" style="display:none;">
    <div class="Container">
        <div id="div_works-1">
            <div class="Scroller-Container" id="works-Container_id">
            </div>
        </div>
    </div>
</div>

<input id="memberCardId" type="hidden">
<!-- 车牌号下拉菜单 zhangchuanlong-->
<div id="div_brandvehiclelicenceNo" class="i_scroll" style="display:none;width:132px;">
    <div class="Container" style="width:132px;">
        <div id="Scroller-1licenceNo" style="width:132px;">
            <div class="Scroller-Containerheader" id="Scroller-Container_idlicenceNo">
            </div>
        </div>
    </div>
</div>
<!-- 客户商下拉菜单 zhangchuanlong-->
<div id="div_brandCustomer" class="i_scroll" style="display:none;width:300px;">
    <div class="Container" style="width:300px;">
        <div id="Scroller-1licenceNo1" style="width:300px;">
            <div class="Scroller-ContainerSupplier" id="Scroller-Container_idCustomer">
            </div>
        </div>
    </div>
</div>
<!-- 增加下拉建议框 -->
<div id="id-searchcomplete"></div>
<div id="id-searchcompleteMultiselect"></div>
<div id="draftOrder_dialog" style="display:none">
    <div class="i_draft_table">
        <table cellpadding="0" cellspacing="0" class="i_draft_table_box" id="draft_table">
            <col>
            <col width="50">
            <col width="100">
            <col width="200">
            <col width="120">
            <col width="120">
            <col width="250">
            <col width="250">
            <col>
            <tr class="tab_title">
                <td class="tab_first"></td>
                <td>No</td>
                <td>单据号</td>
                <td>保存时间</td>
                <td>客户</td>
                <td>车牌号</td>
                <td>施工项目</td>
                <td>材料</td>
                <td class="tab_last"></td>
            </tr>
        </table>

        <!--分页-->
        <div class="hidePageAJAX">
            <jsp:include page="/common/pageAJAX.jsp">
                <jsp:param name="url" value="draft.do?method=getDraftOrders"></jsp:param>
                <jsp:param name="data" value="{startPageNo:1,orderTypes:'REPAIR'}"></jsp:param>
                <jsp:param name="jsHandleJson" value="initReapirOrderDraftTable"></jsp:param>
                <jsp:param name="hide" value="hideComp"></jsp:param>
                <jsp:param name="dynamical" value="dynamical1"></jsp:param>
            </jsp:include>
        </div>
    </div>
</div>
<bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.STOREHOUSE">
    <jsp:include page="../txn/common/selectStoreHouse.jsp"/>
</bcgogo:hasPermission>
<div id="commodityCode_dialog" style="display:none">
    <span id="commodityCode_dialog_msg"></span>
</div>
<div id="mask" style="display:block;position: absolute;"></div>
<%@ include file="/sms/enterPhone.jsp" %>
<div id="dialog-confirm-invoicing" title="提醒">
    <p><span class="ui-icon ui-icon-alert" style="float:left; margin:0 7px 20px 0;"></span>

    <div id="dialog-confirm-text">单据日期与当前日期不一致，是否确定修改单据日期？</div>
    </p>
</div>
<div id="newSettlePage" style="display:none"></div>

<div id="appRemindHelp_dialog" style="padding:0 0 0 0;">
        <jsp:include page="appOrSMSRemindHelp.jsp"/>
</div>

<div id="appRemind_dialog" style="padding:0 0 0 0;">
    <jsp:include page="AppOrSMSRemind.jsp"/>
</div>

<div id="appRemindHelpBottom_dialog" style="padding:0 0 0 0;">
    <jsp:include page="appOrSMSRemindHelpBottom.jsp"/>
</div>


<iframe id="iframe_PopupBox"
        style="position:absolute;z-index:6;top:210px;left:87px;display:none;overflow-x:hidden;overflow-y:auto; "
        allowtransparency="true" width="1000px" height="100%" frameborder="0" src=""></iframe>
<iframe id="iframe_PopupBox_1" style="position:absolute;z-index:6;top:210px;left:87px;display:none; "
        allowtransparency="true" width="1000px" height="500px" frameborder="0" src="" scrolling="no"></iframe>
<iframe id="iframe_PopupBox_2"
        style="position:absolute;z-index:6;top:210px;left:87px;display:none;  background: none repeat scroll;"
        allowtransparency="true" width="1000px" height="650px" frameborder="0" src="" scrolling="no"></iframe>
<iframe id="iframe_PopupBoxMakeTime" style="position:absolute;z-index:10;display:none; " allowtransparency="true"
        width="350px" height="500px" frameborder="0" src="" scrolling="no"></iframe>
<input type="button" id="idAddRow" value="确定" style="display:none;" onclick="addOneRow()"/>
<%--------------------欠款结算  2011-12-14-------------------------------%>
<iframe id="iframe_qiankuan" style="position:absolute; left:0px; top:300px; display:none;z-index:8;"
        allowtransparency="true" width="1000px" height="650px" frameborder="0" src="" scrolling="no">
</iframe>

<iframe id="iframe_PopupBox_account" style="position:absolute;z-index:5; left:500px; top:300px; display:none;"
        allowtransparency="true" width="900px" height="450px" frameborder="0" src="" scrolling="no"></iframe>
<iframe id="iframe_CardList" style="position:absolute;z-index:9; left:300px; top:200px; display:none;"
        allowtransparency="true" width="850px" height="300px" frameborder="0" src=""></iframe>
<iframe id="iframe_buyCard" style="position:absolute;z-index:7; left:300px; top:10px; display:none;"
        allowtransparency="true" width="800px" height="740px" scrolling="no" frameborder="0" src=""></iframe>

<iframe id="iframe_moreUserInfo"
        style="position:absolute;z-index:7; left:200px; top:200px; display:none;overflow:hidden;"
        allowtransparency="true" width="840px" height="600px" frameborder="0" scrolling="no" src=""></iframe>
<jsp:include page="/txn/orderOperationLog.jsp" />
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>
