<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page import="com.bcgogo.txn.dto.RepairOrderDTO" %>
<%@ page import="com.bcgogo.user.dto.CouponConsumeRecordDTO" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<html xmlns="http://www.w3.org/1999/xhtml" pagetype='order'>

<head xmlns="http://www.w3.org/1999/xhtml">
<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
<title id="title">${repairOrderDTO.licenceNo==null?"车辆施工":repairOrderDTO.licenceNo}</title>
<%
    boolean storageBinTag = ServiceManager.getService(IShopConfigService.class).isStorageBinSwitchOn(WebUtil.getShopId(request));//选配仓位功能 默认开启这个功能false
    boolean tradePriceTag = ServiceManager.getService(IShopConfigService.class).isTradePriceSwitchOn(WebUtil.getShopId(request), WebUtil.getShopVersionId(request));//选配批发价功能
%>
<link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/moreHistory<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/draftOrder<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/addCheck<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/customerTooltip<%=ConfigController.getBuildVersion()%>.css"/>

<c:choose> <c:when test="<%=storageBinTag%>">
    <link rel="stylesheet" type="text/css" href="styles/storageBinOn<%=ConfigController.getBuildVersion()%>.css"/>
</c:when> <c:otherwise>
    <link rel="stylesheet" type="text/css" href="styles/storageBinOff<%=ConfigController.getBuildVersion()%>.css"/>

</c:otherwise> </c:choose>
<link rel="stylesheet" type="text/css" href="styles/yinshouyinfu<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/activeRecommendSupplierTip<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/prompt_box<%=ConfigController.getBuildVersion()%>.css"/>
<style type="text/css">
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

        /*#table_task, #table_productNo_2, #table_otherIncome {*/
        /*border-width: 1px;*/
        /*border-style: solid;*/
        /*border-color: #BBBBBB;*/
        /*}*/

        /*为解决布局错乱的问题， 原页面style.css 中的布局有问题， 但考虑到可能影响全局布局样式， 故采用此种淫巧应对之*/
    .invalidImg {
        margin: 0 0 0 18px;
    }

    .chassisNumber {
        text-transform: uppercase;
    }

</style>
<%@include file="/WEB-INF/views/header_script.jsp" %>
<script type="text/javascript">

    <bcgogo:permissionParam permissions="WEB.SET_THE_BUSINESS_CONSTRUCTION,WEB.VERSION.ACTIVE_RECOMMEND_SUPPLIER">
    APP_BCGOGO.Permission.SetBusinessConstruction =${WEB_SET_THE_BUSINESS_CONSTRUCTION};
    APP_BCGOGO.Permission.Version.ActiveRecommendSupplier = ${WEB_VERSION_ACTIVE_RECOMMEND_SUPPLIER}
            </bcgogo:permissionParam>
            APP_BCGOGO.PersonalizedConfiguration.TradePriceTag =<%=tradePriceTag%>;
    APP_BCGOGO.PersonalizedConfiguration.StorageBinTag =<%=storageBinTag%>;
    defaultStorage.setItem(storageKey.MenuUid, "VEHICLE_CONSTRUCTION_REPAIR");
    <c:choose>
    <c:when test="${not empty repairOrderDTO.id}">
    defaultStorage.setItem(storageKey.MenuCurrentItem,"编辑");
    </c:when>
    <c:otherwise>
    defaultStorage.setItem(storageKey.MenuCurrentItem,"新增");
    </c:otherwise>
    </c:choose>

    APP_BCGOGO.Permission.Version.FourSShopVersion =${empty fourSShopVersions?false:fourSShopVersions};
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
<script type="text/javascript" src="js/module/invoiceCommon<%=ConfigController.getBuildVersion()%>.js"></script>
<%--<script type="text/javascript" src="js/utils/tableUtil.js"></script>--%>
<script type="text/javascript" src="js/invoicesolr<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/script<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/detailsArrears<%=ConfigController.getBuildVersion()%>.js"></script>

<script type="text/javascript" src="js/invoice<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/inventorySearchIndex<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/vehiclelicenceNo<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/vehicleValidator<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/checkMobileAndTelphone<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/invoiceCustomerSearch<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/selectOptions<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/member<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/suggestion<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/draftOrderBox<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript"
        src="js/components/ui/bcgogo-droplist<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/page/txn/txn<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/otherIncomeKind<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/insuranceCommon<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/storeHouseDialog<%=ConfigController.getBuildVersion()%>.js"></script>


<script type="text/javascript">
function domBlur(node, field) {
    var $trList = $(node).parents("tr");
    $trList.find("td:eq(0)>input").eq(3).val("");
    $trList.find("td:eq(9)>span").first().html("0.0");
    $trList.find("td:eq(9)>input").eq(0).val("");
    $trList.find("td:eq(0)>input").eq(2).val("");
    if ("product_name" == field) {
        $trList.find("td:eq(2)>input").first().val("");
        $trList.find("td:eq(3)>input").first().val("");
        $trList.find("td:eq(4)>input").first().val("");
    } else if ("product_brand" == field) {
        $trList.find("td:eq(3)>input").first().val("");
        $trList.find("td:eq(4)>input").first().val("");
    } else if ("product_spec" == field) {
        $trList.find("td:eq(4)>input").first().val("");
    }
}

function usersolr(node, flag, position, searchField, e) {
    node.value = node.value.replace(/[\ |\\]/g, "");
    var searchValue = "",
            $trList = $(node).parents("tr");
    if ("keyup" == flag) {
        searchValue = node.value;
    }
    var inputArray = new Array(8);
    inputArray[0] = $trList.find("td:eq(1)>input[type!='hidden']")[0];
    inputArray[1] = $trList.find("td:eq(2)>input").first()[0];
    inputArray[2] = $trList.find("td:eq(3)>input").first()[0];
    inputArray[3] = $trList.find("td:eq(4)>input").first()[0];
    inputArray[4] = $trList.find("td:eq(8)>input").first()[0];
    inputArray[5] = $trList.find("td:eq(0)>input").eq(3)[0];
    inputArray[6] = $trList.find("td:eq(0)>input").eq(1)[0];
    inputArray[7] = $trList.find("td:eq(0)>input").eq(2)[0];
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
// TODO 这段代码看着应该没有用， 但是还得测试， 我又不敢删。
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
    $(document).click(function (event) {
        var target = event.target;
        if (!target || !target.id || (target.type != "text" && target.id != "div_brand")) {
            $("#div_brand").css({'display': 'none'});
        }
        if (!target || !target.id ||
                ((target.type != "text" && target.tagName != "FORM" && target.id != "div_works" && target.id != "productSaler") && target.id && target.id.indexOf('workers') < 0)) {
            $("#div_works").css({'display': 'none'});
        }
    });

    <bcgogo:hasPermission permissions="WEB.TXN.PURCHASE_MANAGE.STORAGE" resourceType="menu">
    $(".lackMaterial").live('click', function () {
        var id1 = $(this).attr("id").split(".")[0];
        var id = id1 + ".productId";
        if (checkRepairPickingSwitchOn()) {
            nsDialog.jAlert("友情提示：本店已经开通维修领料流程，在本单据中不能直接采购商品！");
            return;
        }
        if ($("#id").val()) {
            var ajaxData = {"orderId": $("#id").val(), "orderType": "REPAIR"};
            APP_BCGOGO.Net.syncPost({
                url: "txn.do?method=validatorLackProductTodo&" + Math.random() * 10000000,
                dataType: "json",
                data: ajaxData,
                success: function (result) {
                    if (!result.success && result.operation == "ALLOCATE_OR_PURCHASE") {
                        $("#allocate_or_purchase_div").dialog({
                            resizable: false,
                            title: "缺料提醒！",
                            height: 150,
                            width: 288,
                            modal: true,
                            closeOnEscape: false,
                            buttons: {
                                "仓库调拨": function () {
                                    $("#repairOrderForm").attr("action", "allocateRecord.do?method=createAllocateRecordByRepairOrder&returnType=REPAIR");
                                    $("#repairOrderForm").submit();
                                    $(this).dialog("close");
                                },
                                "商品入库": function () {
                                    window.location = "storage.do?method=getProducts&type=good&productIds=" + document.getElementById(id).value + "&repairOrderId=" + $("#id").val();
                                    $(this).dialog("close");
                                }
                            }
                        });
                    } else {
                        window.location = "storage.do?method=getProducts&type=good&productIds=" + document.getElementById(id).value + "&repairOrderId=" + $("#id").val();
                    }
                },
                error: function () {
                    nsDialog.jAlert("网络异常");
                }
            });

        } else {
            if ($("#serviceType").val() == 'REPAIR') {
                nsDialog.jAlert("请先派单，然后点我入库");
            } else if ($("#serviceType").val() == 'SALES') {
                nsDialog.jAlert("无此商品信息，请先采购或入库");
            }
        }
    });
    </bcgogo:hasPermission>
    var urlParams = $.url(window.location.search).param();

    if (urlParams && urlParams['task'] && urlParams['task'] === 'wash') {
//		$("#carWash").attr("class", "title_hover");
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
        $("#carWash").attr("class", "");
//		$("#carMaintain")[0].className = "title_hover";
        $("#table_carWash")[0].style.display = "none";
        $("#table_task")[0].style.display = "";
        $("#table_productNo_2")[0].style.display = "";
        $("#div_tablereservateTime")[0].style.display = "block";
        $("#save_div")[0].style.display = "block";
        $("#finish_div")[0].style.display = "block";
        $("#account_div")[0].style.display = "block";
        $("#serviceType")[0].value = "REPAIR";
        $("#pageType").val('');
    }


//    if ($("#input_startMileage")[0] != null && $("#input_startMileage")[0].value == "0") {
//        $("#input_startMileage")[0].value = "";
//    }

    <bcgogo:permissionParam permissions="WEB.VEHICLE_CONSTRUCTION.CONSTRUCT.SAVE">
    if (${!permissionParam1}) {
        $("#bxId,#ycId,#byId,#maintainMileage,#endDateStr,#startDateStr,#startMileage,#vehicleHandover,#fuelNumber,#settledAmount,#debt,.table_input,.serviceTotal,.edit1,.opera1,.opera2,.table_input")
                .attr("disabled", "disabled");
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
        "beforeShow": function (ele, inst) {
            if (G.isEmpty(ele.value)) {
                ele.value = G.getCurrentFormatDateMin();
            }
        },
        "onClose": function (dateText, inst) {
            if (!$(this).val()) {
                return;
            }
        },
        "onSelect": function (dateText, inst) {
            if (inst.lastVal == dateText) {
                return;
            }
            $(this).val(dateText);
            var This = inst.input;
            if ($("#endDateStr").val() && $().val() > $("#endDateStr").val()) {
                nsDialog.jAlert("预约出厂时间不能早于进厂时间，请修改!", null, function () {
                    $("#startDateStr").val($("#startDateStr").attr("initstartdatestrvalue"));
                });
                return;
            }
            if (inst.id == "startDateStr") {
                //如果选在非当前的时间 提醒逻辑
                if (!This.val()) return;
                if (G.getCurrentFormatDate() != This.val().substr(0, 10)) {
                    $("#dialog-confirm-invoicing").dialog('open');
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
                $(this).dialog("close");
            }, "否": function () {
                $("#startDateStr").val(G.getCurrentFormatDateMin());
                $(this).dialog("close");
            }
        }
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
        "onClose": function (dateText, inst) {
            var lastValue = $(this).attr("lastValue");
            if (lastValue == dateText) {
                return;
            }
            var currentDate = G.getCurrentFormatDate().replace(/[- ]+/, "");
            var selectedDate = dateText.replace(/[- ]+/, "");
            if (selectedDate != "" && currentDate > selectedDate) {
                if ($(this).attr("id") == "byId") {
                    nsDialog.jAlert("保养日期请选择今天及以后的日期。");
                } else if ($(this).attr("id") == "bxId") {
                    nsDialog.jAlert("保险日期请选择今天及以后的日期。");
                } else if ($(this).attr("id") == "ycId") {
                    nsDialog.jAlert("验车请选择今天及以后的日期。");
                } else {
                    nsDialog.jAlert("请选择今天及以后的日期。");
                }
                $(this).val("");
            } else {
                updateYuye();
            }
            $(this).attr("lastValue", dateText);
        }
    });

    $("#maintainMileage").bind("keyup",function () {
        $(this).val(App.StringFilter.inputtingIntFilter($(this).val()));
    }).bind("blur",function () {
                $(this).val(App.StringFilter.inputtingIntFilter($(this).val()));
                if ($(this).val() != $(this).attr("lastVal")) {
                    updateYuye();
                }
            }).bind("focus", function () {
                $(this).attr("lastVal", $(this).val());
            });

    if ($("#id").val()) {
        var url = "txn.do?method=getQualifiedCredentials"
        APP_BCGOGO.Net.syncAjax({url: url, dataType: "json", data: {orderId: $("#id").val()}, success: function (json) {
            var _$createQualifiedDiv = $("#createQualifiedDiv");
            var _$showQualifiedSpan = $("#showQualifiedSpan");
            _$createQualifiedDiv.empty();
            _$showQualifiedSpan.empty();
            if (json && json.no) {
                _$showQualifiedSpan.append('<span class="blue_color">|</span>');
                _$showQualifiedSpan.append('<a class="blue_color" id="createQualified"><img src="images/icon_right.png"/>合格证'+json.no+'</a>');
            }else {
                _$createQualifiedDiv.append('<input type="button" id="createQualified" class="qualified" value="" style="margin: 12px 0 0;" onfocus="this.blur();"/>');
                _$createQualifiedDiv.append('<div style="width:100%; ">生成合格证</div>');
            }
        }});
    }


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

    if (!G.isEmpty($("#receiptNo").val())) {
        $("#print_div").show();
    } else {
        $("#print_div").hide();
    }
    // 车牌号 单据，  快速输入功能强化
    var $licenceNo = $("#licenceNo");
    $licenceNo
            .attr("warning", "请先输入")
            .tipsy({title: "warning", delay: 0, gravity: "s", html: true, trigger: 'manual'})
            .bind("focus", function () {
                $(this).tipsy("hide");
            });

    $(document).bind("click", function (event) {
        if ($(event.target).attr("id") !== "historySearchButton_id"
                && $licenceNo[0]) {
            $licenceNo.tipsy("hide");
        }
    });

    App.Module.searchcompleteMultiselect.moveFollow({
        node: $licenceNo[0]
    });

    $("#historySearchButton_id")
            .tipsy({delay: 0, gravity: "s", html: true})
            .bind("click", function (event) {
                var foo = APP_BCGOGO.Module.searchcompleteMultiselect;

                $licenceNo.tipsy("hide");
                if (foo.detailsList.isVisible()) {
                    foo.hide();
                    return;
                }

                if (!foo._relInst || G.isEmpty(foo._relInst.value)) {
                    $licenceNo.tipsy("show");
                    return;
                }

                foo.hide();
                foo.moveFollow({node: $("#licenceNo")[0]});
                searchOrderSuggestion(foo, foo._relInst, "");

                try {
                    App.Module.searchcomplete.hide();
                } catch (e) {
                    G.debug("error searchcomplete instance is undefined!");
                }

                event.stopPropagation();
            })
            .toggle(!G.isEmpty(G.normalize($("#licenceNo").val())));

    window.timerCheckHistoryButton = 0;
    function toggleHistoryButton() {
        $("#historySearchButton_id").toggle(!G.isEmpty(G.normalize($("#licenceNo").val())));
        timerCheckHistoryButton = setTimeout(toggleHistoryButton, 500);
    }

    timerCheckHistoryButton = setTimeout(toggleHistoryButton, 500);

});

//新客户要新增后才能选择卡
function selectCard() {

    if ($("#gouka").attr("disabled")) {
        return;
    }

    $("#gouka").attr("disabled", true);
    var statusFlag = true;

    if ($("#customerId").val()) {
        var r = APP_BCGOGO.Net.syncGet({async: false, url: "customer.do?method=checkCustomerStatus", data: {customerId: $("#customerId").val(), now: new Date()}, dataType: "json"});
        if (!r.success) {
            statusFlag = false;
        }
    }

    if (!statusFlag) {
        alert("此客户已被删除或合并，不能购卡！");
        $("#gouka").removeAttr("disabled");
        return;
    }


    if (!$("#customerId").val() && !$("#customerName").val()) {
        alert("请填写客户信息");
        $("#gouka").removeAttr("disabled");
        return;
    }


    bcgogo.checksession({"parentWindow": window.parent, 'iframe_PopupBox': $("#iframe_CardList")[0], 'src': 'member.do?method=selectCardList&time=' + new Date()});
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
        data: {
            customerId: customerId,
            vehicleId: vehicleId,
            maintainTimeStr: maintainTimeStr,
            insureTimeStr: insureTimeStr,
            examineTimeStr: examineTimeStr,
            maintainMileage: maintainMileage},
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


$(function () {
    $("#showTemplateContainer").toggle(function (e) {
        $(this).addClass("template_r_li_hover");
        $("#templateContainer").show();
    }, function (e) {
        $(this).removeClass("template_r_li_hover");
        $("#templateContainer").hide();
    });

    $('body').click(function (e) {
        var tc = $(e.target).closest('#templateContainer');
        if (tc.length == 0 && $("#templateContainer").css('display') != 'none') {
            $("#showTemplateContainer").click();
        }
    });

//    $("#showTemplateContainer").bind("click",function () {
//        $(this).addClass("template_r_li_hover");
//        $("#templateContainer").mouseleave(function (event) {
//            if (event.relatedTarget != $("#showTemplateContainer")[0]) {
//                $("#templateContainer").hide();
//                $("#showTemplateContainer").removeClass("template_r_li_hover");
//            }
//        });
//        $("#templateContainer").show();
//
//    });
//    $("#showTemplateContainer").bind('mouseleave', function (event) {
//        if (event.relatedTarget != $("#templateContainer")[0]) {
//            $("#templateContainer").hide();
//            $("#showTemplateContainer").removeClass("template_r_li_hover");
//        }
//    });
    $("#templateContainer ul li").live("mouseenter",function (event) {
        if($(this).find(".J_RenameTemplateInput").is(":hidden")){
            $(this).find(".J_DeleteTemplate").show();
        }
    }).live("mouseleave", function (event) {
        $(this).find(".J_DeleteTemplate").hide();
    });

    APP_BCGOGO.Net.asyncGet({
        url: "txn.do?method=getAllRepairOrderTemplateOrder",
        dataType: "json",
        success: function(data) {
            $("#templateContainer ul").empty();
            if (G.isNotEmpty(data)) {
                $.each(data,function(index,repairOrderTemplateDTO){
                    var liHtml = '<li class="construction_template_li1" data-template-name="'+repairOrderTemplateDTO.templateName+'" data-template-id="'+repairOrderTemplateDTO.idStr+'">';
                    liHtml += '<div class="shut_down_02 J_DeleteTemplate" style="display: none;"></div>';
                    liHtml += '<div class="template_name J_TemplateName" title="' + repairOrderTemplateDTO.templateName + '">'+repairOrderTemplateDTO.templateName+'</div>';
                    liHtml += '<input class="txt J_RenameTemplateInput" maxlength="20" type="text" style="display: none;width: 60px" autocomplete="off"/>';
                    liHtml += '</li>';
                    $("#templateContainer ul").append(liHtml);
                });
            }
        },
        error: function () {
            showMessage.fadeMessage("45%", "34%", "slow", 3000, "施工模板初始化异常！ ");
            return;
        }
    });
});
</script>

<bcgogo:hasPermission permissions="WEB.AD_SHOW">
    <script type="text/javascript" src="js/adShow<%=ConfigController.getBuildVersion()%>.js"></script>
</bcgogo:hasPermission>

<%
    boolean isMemberSwitchOn = WebUtil.checkMemberStatus(request);
%>

</head>


<body class="bodyMain" pagetype="order" ordertype="">
<script type="text/javascript">
    $(document.body).attr("ordertype", App.OrderTypes.REPAIR);
</script>

<%@ include file="/WEB-INF/views/header_html.jsp" %>
<%@ include file="/common/messagePrompt.jsp" %>
<bcgogo:permissionParam permissions="WEB.TXN.PURCHASE_MANAGE.STORAGE,WEB.VEHICLE_CONSTRUCTION.BASE" resourceType="menu">
    <input autocomplete="off" type="hidden" id="permissionGoodsStorage" value="${permissionParam1}"/>
    <input autocomplete="off" type="hidden" id="invoicingPermission" value="${permissionParam2}"/> </bcgogo:permissionParam>
<input autocomplete="off" type="hidden" id="basePath" name="bathPath" value="<%=basePath%>"/>
<input autocomplete="off" type="hidden" value="<%=isMemberSwitchOn%>" id="isMemberSwitchOn"/>
<input autocomplete="off" type="hidden" value="${copyRepairOrderDTO}" id="copyRepairOrderDTO"/>
<input autocomplete="off" type="hidden" value="RepairOrder" id="pageName"/>

<div class="i_main">

<div class="mainTitles">
    <bcgogo:permission>
        <bcgogo:if resourceType="logic" permissions="WEB.VERSION.DISABLE_VEHICLE_CONSTRUCTION">
            <jsp:include page="vehicleConstructionNavi.jsp">
                <jsp:param name="currPage" value="invoicingOrder"/>
                <jsp:param name="repairOrderDTO" value="${repairOrderDTO}"/>
                <jsp:param name="orderId" value="${repairOrderDTO.id}"/>
            </jsp:include>
        </bcgogo:if>
        <bcgogo:else>
            <jsp:include page="vehicleNavi.jsp">
                <jsp:param name="currPage" value="invoicingOrder"/>
                <jsp:param name="repairOrderDTO" value="${repairOrderDTO}"/>
                <jsp:param name="orderId" value="${repairOrderDTO.id}"/>
            </jsp:include>
        </bcgogo:else>
    </bcgogo:permission>
</div>
<div class="booking-management">
<form:form commandName="repairOrderDTO" id="repairOrderForm" action="repair.do?method=dispatchRepairOrder" method="post" class="J_leave_page_prompt" name="thisform">
<jsp:include page="unit.jsp"/>
<input autocomplete="off" type="hidden" value="${repairOrderDTO.receiptNo}" id="receiptNo" name="receiptNo"/>
<input autocomplete="off" type="hidden" id="repairOrderTemplateName" name="repairOrderTemplateName"/>
<input autocomplete="off" type="hidden" id="repairOrderTemplateId" name="repairOrderTemplateId"/>
<input autocomplete="off" type="hidden" id="currentRepairOrderTemplateId"/>
<input autocomplete="off" type="hidden" id="currentRepairOrderTemplateName"/>
<form:hidden autocomplete="off" path="id" value="${repairOrderDTO.id}"/>
<input autocomplete="off" id="orderType" name="orderType" value="repairOrder" type="hidden"/>
 <input id="confirm_account_date" name="accountDateStr" type="hidden" />
<form:hidden path="insuranceOrderId" autocomplete="off"/>
<form:hidden path="appointOrderId" autocomplete="off"/>
<input autocomplete="off" type="hidden" id="status" name="status" value="${repairOrderDTO.status}"/>
<input autocomplete="off" type="hidden" id="draftOrderIdStr" name="draftOrderIdStr"
       value="${repairOrderDTO.draftOrderIdStr==null?"":repairOrderDTO.draftOrderIdStr}"/>
<form:hidden autocomplete="off" path="receivableId" value="${repairOrderDTO.receivableId}"/>
<input type="hidden" id="dealingType" name="dealingType" value="${repairOrderDTO.insuranceOrderDTO.statusStr}"/>
<form:hidden autocomplete="off" path="vechicleId" value="${repairOrderDTO.vechicleId}"/>
<form:hidden autocomplete="off" path="address" value="${repairOrderDTO.address}"/>
<form:hidden autocomplete="off" path="year" value="${repairOrderDTO.year}"/>
<form:hidden autocomplete="off" path="engine" value="${repairOrderDTO.engine}"/>
<form:hidden autocomplete="off" path="vehicleBuyDate" value="${repairOrderDTO.vehicleBuyDate}"/>
<form:hidden autocomplete="off" id="serviceType" path="serviceType" value="${repairOrderDTO.serviceType}"/> <%--判断是维修还是洗车还是销售--%>
<form:checkbox id="sendMemberSms" path="sendMemberSms" style="display:none;"/>
<%--记录代金券消费记录的id--%>
<form:hidden autocomplete="off" path="consumingRecordId" value="${repairOrderDTO.consumingRecordId}"/>

    <div class="titBody J_RepairOrderClearDiv">
    <div  style="width:58%; float:left; ">
        <div class="shelvesed clear" style="width:100%">
            <div class="topTitle" style="color: #444443;line-height: 32px;height:32px;text-align: left">
                <div class="title-r">
                    <bcgogo:hasPermission permissions="WEB.CUSTOMER_MANAGER.CUSTOMER_MODIFY">
                        <a class="blue_color" id="input_clientInfo">客户详细资料<img style="float:right; margin:12px 0 0 1px;" src="images/rightArrows.PNG"></a>
                    </bcgogo:hasPermission>
                </div>
                客户车辆信息
                <c:if test="<%=isMemberSwitchOn%>">
                    <div class="card_vip" id="customerMemberInfoImg" style="display: ${not empty repairOrderDTO.memberNo?'':'none'}">
                        VIP
                        <div style="margin: 0px 0px 0px -12px;display: none;" class="tooltip" id="customerMemberInfoDiv">
                            <div class="tooltipTop"></div>
                            <div class="tooltipBody"><a class="icon_close"></a>
                                <div class="title">
                                    <div style="float: left;width: 150px;">
                                        <strong>会员信息</strong>
                                        （<span id="memberStatus" class="right">${repairOrderDTO.memberStatus}</span>）
                                    </div>
                                    <div style="float: right;width: 20px;"></div>
                                </div>
                                <div class="prompt-left">
                                    <div class="clear"><div class="left">卡号：</div><div id="memberNumber" title="${repairOrderDTO.memberNo}" style="text-overflow :ellipsis;overflow: hidden;white-space: nowrap; " class="right">${repairOrderDTO.memberNo}</div></div>
                                    <div class="clear"><div class="left">卡类型：</div><div id="memberType" style="text-overflow :ellipsis;overflow: hidden;white-space: nowrap;" class="right">${repairOrderDTO.memberType}</div></div>
                                    <div class="clear"><div class="left">入会日期：</div><div id="memberJoinDateStr" style="text-overflow :ellipsis;overflow: hidden;white-space: nowrap;" class="right">${repairOrderDTO.memberJoinDateStr}</div></div>
                                    <div class="clear"><div class="left">过期日期：</div><div id="memberServiceDeadLineStr" style="text-overflow :ellipsis;overflow: hidden;white-space: nowrap;" class="right">${repairOrderDTO.memberServiceDeadLineStr}</div></div>
                                    <div class="clear"><div class="left">会员储值：</div><div id="memberRemainAmount" style="text-overflow :ellipsis;overflow: hidden;white-space: nowrap;" class="right">${repairOrderDTO.memberRemainAmount}</div></div>
                                </div>
                                <div class="prompt-right">
                                    <div style="width: 106px;">服务项目(共<span id="memberServiceCount">${not empty repairOrderDTO.memberServiceDTOs?fn:length(repairOrderDTO.memberServiceDTOs):0}</span>项)</div>
                                    <div id="memberServiceInfo">
                                        <c:if test="${not empty repairOrderDTO.memberServiceDTOs}">
                                            <c:forEach var="memberServiceDTO" items="${repairOrderDTO.memberServiceDTOs}">
                                                <div style="overflow: hidden;"><div style="text-overflow :ellipsis;overflow: hidden;white-space: nowrap; " title="${memberServiceDTO.serviceName}" class="div left">${memberServiceDTO.serviceName}</div><div class="div right">${memberServiceDTO.timesStr}</div></div>
                                            </c:forEach>
                                        </c:if>
                                    </div>
                                </div>
                            </div>
                            <div class="tooltipBottom"></div></div>
                    </div>
                    <bcgogo:hasPermission permissions="WEB.CUSTOMER_MANAGER.MEMBERSHIP_PACKAGE_MANAGER.BUY_CARD">
                        <a id="gouka" class="blue_color" onclick="selectCard()" style="font-size:12px; font-weight:normal; margin-left:5px;">${empty repairOrderDTO.memberNo?'购卡':'续卡'}</a>
                    </bcgogo:hasPermission>
                </c:if>

            </div>
            <div class="customer" style="border:0; font-size:12px;padding: 2px">
                <table  width="100%"cellpadding="0" cellspacing="0" class="table1">
                    <tr>
                        <td>车牌号<a class="red_color">*</a></td>
                        <td style="width: 150px">
                            <input type="text" id="licenceNo" name="licenceNo" value="${repairOrderDTO.licenceNo}" autocomplete="off" maxlength="20" class="checkStringEmpty txt"  style="text-transform:uppercase;width: 120px"/>
                            <bcgogo:hasPermission permissions="WEB.VEHICLE_CONSTRUCTION.BASE">
                                <div class="history_c" id="historySearchButton_id" original-title="历史查询"></div>
                            </bcgogo:hasPermission>
                        </td>
                        <td>车辆品牌</td>
                        <td id="td_brand">
                            <input type="text" id="brand" name="brand" value="${repairOrderDTO.brand}" style="width:100px;" class="J_checkVehicleBrandModel checkStringEmpty txt" autocomplete="off"/>
                            <input type="hidden" id="input_brandname" value="${repairOrderDTO.brand}"/>
                        </td>
                        <td>车型</td>
                        <td id="td_model">
                            <input type="text" id="model" name="model" value="${repairOrderDTO.model}" style="width:100px;" class="J_checkVehicleBrandModel checkStringEmpty txt" autocomplete="off"/>
                            <input type="hidden" id="input_modelname" value="${repairOrderDTO.model}"/>
                        </td>
                    </tr>
                    <tr>
                        <td>客户名<a class="red_color">*</a></td>
                        <td id="td_name">
                            <form:input path="customerName" value="${repairOrderDTO.customerName}" autocomplete="off" cssClass="checkStringEmpty txt"  size="15" style="width: 120px;"/>
                            <form:hidden path="customerId" value="${repairOrderDTO.customerId}" autocomplete="off"/>
                            <form:hidden path="customerMemberNo" value="${repairOrderDTO.customerMemberNo}" autocomplete="off"/>
                            <form:hidden path="customerMemberType" value="${repairOrderDTO.customerMemberType}" autocomplete="off"/>
                            <form:hidden path="customerMemberStatus" value="${repairOrderDTO.customerMemberStatus}" autocomplete="off"/>
                        </td>
                        <td>联系人</td>
                        <td>
                            <form:input autocomplete="off" path="contact" value="${repairOrderDTO.contact}" cssClass="checkStringEmpty txt" size="14" style="width: 100px;"/>
                            <form:hidden autocomplete="off" path="contactId" value="${repairOrderDTO.contactId}"/>
                            <form:hidden autocomplete="off" path="qq" value="${repairOrderDTO.qq}"/>
                            <form:hidden autocomplete="off" path="email" value="${repairOrderDTO.email}"/>
                        </td>
                        <td>
                            手机
                            <bcgogo:hasPermission permissions="WEB.CUSTOMER_MANAGER.SMS_SEND">
                                <a class="phone" style="float:none" href="#" onclick="sendSms($('#mobile').val(),'3',$.trim($('#allDebt').text()), $('#licenceNo').val(),'',$('#customerName').val(),$('#customerId').val())"></a>
                            </bcgogo:hasPermission>
                            <span id="hiddenMobile" style="display:none">${repairOrderDTO.mobile}</span>
                        </td>
                        <td>
                            <div class="title-r" style="height:23px"><a class="blue_color" id="showCustomerInfoBtn">详细<img style="float:right; margin:9px 0 0 0px;" src="images/rightArrow.png"></a></div>
                            <div class="title-r" style="height:23px;display: none"><a class="blue_color" id="hideCustomerInfoBtn">收起<img style="float:right; margin:9px 0 0 0px;" src="images/rightTop.png"></a></div>
                            <form:input path="mobile" value="${repairOrderDTO.mobile}" autocomplete="off" cssClass="checkStringEmpty txt" size="12" maxlength="11" style="width: 100px;"/>
                        </td>
                    </tr>
                    <tr class="J_CustomerInfoDiv" style="display: none">
                        <td>车主</td>
                        <td><form:input path="vehicleContact" autocomplete="off"  value="${repairOrderDTO.vehicleContact}" cssClass="txt" cssStyle="width:120px" maxlength="20"/></td>
                        <td>车主手机</td>
                        <td><form:input path="vehicleMobile" autocomplete="off"  value="${repairOrderDTO.vehicleMobile}" cssClass="txt" cssStyle="width:100px" maxlength="11"/></td>
                        <td>座机</td>
                        <td><form:input path="landLine" value="${repairOrderDTO.landLine}" autocomplete="off" cssClass="checkStringEmpty txt" size="14" style="width: 100px;"/></td>
                    </tr>
                    <tr class="J_CustomerInfoDiv" style="display: none">
                        <td>车架号</td>
                        <td><input type="text" id="vehicleChassisNo" autocomplete="off"  name="vehicleChassisNo" value="${repairOrderDTO.vehicleChassisNo}" style="width:120px;" class="txt chassisNumber"maxlength="17"></td>
                        <td>发动机号</td>
                        <td><input type="text" id="vehicleEngineNo" autocomplete="off"  name="vehicleEngineNo" value="${repairOrderDTO.vehicleEngineNo}" style="width:100px;" class="txt" maxlength="30"/></td>
                        <td>车身颜色</td>
                        <td><input type="text" id="vehicleColor" autocomplete="off"  name="vehicleColor" value="${repairOrderDTO.vehicleColor}" style="width:100px;" class="txt" maxlength="10"/></td>
                    </tr>
                </table>
            </div>
        </div>
        <div class="shelvesed clear" style="width:100%; border-top:0;">
            <div class="topTitle" style="color: #444443;line-height: 32px;height:32px;text-align: left">消费信息</div>
            <div class="customer" style="border:0; font-size:12px;padding: 2px;">
                <div class="divTit">
                    <div class="title-r" style="margin:0px 10px 3px 5px;"><a id="duizhan" class="blue_color">对账<img style="float:none; margin:0px 0px 0px 2px;" src="images/rightArrows.PNG"></a></div>
                    累计消费 <span class="arialFont">&yen;</span><div id="customerConsume" style="display: inline;" data-filter-zero="true">${empty totalConsume?0:totalConsume}</div>
                    应    收<span class="red_color"> <span class="arialFont">&yen;</span><span id="receivable" data-filter-zero="true">${empty totalReceivable?0:totalReceivable}</span></span>
                    应    付<span class="green_color"><span class="arialFont">&yen;</span><span id="payable" data-filter-zero="true">${repairOrderDTO.totalReturnDebt}</span></span>
                </div>
            </div>
            <div class="clear i_height" style="height: 16px"></div>
        </div>
    </div>
    <div style="width:40%; float:left; margin-left:2px;">
        <div class="shelvesed shelves" style="width:100%">
            <div class="topTitle" style="color: #444443;line-height: 32px;height:32px;text-align: left"> 车辆进厂交接</div>
            <div class="customer" style="border:0; font-size:12px; padding: 2px;">
                <table  width="100%"cellpadding="0" cellspacing="0" class="table1">
                    <tr>
                        <td>进厂时间</td>
                        <td>
                            <form:input id="startDateStr" autocomplete="off" cssStyle="width:115px" path="startDateStr" cssClass="checkStringChanged txt" initstartdatestrvalue="${repairOrderDTO.startDateStr}" readonly="true"/>
                        </td>
                        <td>接车人</td>
                        <td style="width:135px ">
                            <div class="title-r" style="height:23px"><a class="blue_color" id="showCustomerVehicleHandoverInfoBtn">详细<img style="float:right; margin:9px 0 0 0px;" src="images/rightArrow.png"></a></div>
                            <div class="title-r" style="height:23px;display: none"><a class="blue_color" id="hideCustomerVehicleHandoverInfoBtn">详细<img style="float:right; margin:9px 0 0 0px;" src="images/rightTop.png"></a></div>
                            <span id="vehicleHandoverDiv" style="width: 90px; height: 25px; display: inline; line-height: 25px;">
                                <form:input path="vehicleHandover" readOnly="true" cssClass="checkStringChanged txt" initvehiclehandover="${sessionScope.userName}" cssStyle="width: 65px;" autocomplete="off"/>
                                <img src="images/list_close.png" id="deleteVehicleHandoverBtn" style="width:12px;cursor:pointer;display: none;">
                                <form:hidden path="vehicleHandoverId" initvehiclehandoverid="${sessionScope.userId}"/>
                            </span>
                        </td>
                    </tr>
                    <tr class="J_CustomerVehicleHandoverInfo" style="display: none">
                        <td>进厂里程</td>
                        <td>
                            <form:input path="startMileage" autocomplete="off" cssClass="checkNumberEmpty txt" style="width:85px;" maxlength="10"></form:input>
                            公里
                        </td>
                        <td>剩余油量</td>
                        <td>
                            <form:select path="fuelNumber" autocomplete="off" cssClass="checkSelectChanged txt" cssStyle="width: 105px; height: 20px;">
                                <form:option value="" label="--请选择--"/>
                                <form:options items="${fuelNumberList}"/>
                            </form:select>
                        </td>
                    </tr>
                </table>
            </div>
        </div>
        <bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.APPOINT_SERVICE">
        <div class="shelvesed shelves" style="width:100%; border-top:0;">
            <div class="topTitle" style="color: #444443;line-height: 32px;height:32px;text-align: left">
                <div class="title-r"><a id="addAppointBtn" class="blue_color">新增预约</a></div>
                下次预约服务
            </div>
            <div class="customer" style="overflow-x: hidden;overflow-y: auto;border:0; font-size:12px; padding: 2px;max-height: 100px">
                <table id="handover_info_table" width="100%"cellpadding="0" cellspacing="0" class="table1">
                    <tr>
                        <td>保养时间</td>
                        <td>
                            <input type="text" name="maintainTimeStr" autocomplete="off" class="checkStringEmpty txt" value="${repairOrderDTO.maintainTimeStr}" id="byId" readonly="true">
                        </td>
                        <td>保养里程</td>
                        <td>
                            <input type="text" name="maintainMileage" autocomplete="off" class="checkStringEmpty txt" value="${repairOrderDTO.maintainMileage}" id="maintainMileage" maxlength="6" style="width: 65px;">公里
                        </td>
                    </tr>
                    <tr lineNum="-1">
                        <td>保险时间</td>
                        <td>
                            <input type="text" name="insureTimeStr" autocomplete="off" class="checkStringEmpty txt" value="${repairOrderDTO.insureTimeStr}" id="bxId" readonly="true">
                        </td>
                        <td>验车时间</td>
                        <td>
                            <input type="text" name="examineTimeStr" autocomplete="off" class="checkStringEmpty txt" value="${repairOrderDTO.examineTimeStr}" id="ycId" readonly="true">
                        </td>
                    </tr>
                    <c:forEach items="${repairOrderDTO.appointServiceDTOs}" var="appointService" varStatus="status">
                        <c:if test="${appointService!=null}">
                            <tr lineNum="${status.index}" style="height: 25px;">
                                <td colspan="3">
                                    <form:hidden path="appointServiceDTOs[${status.index}].idStr" autocomplete="off" value="${appointService.idStr}"/>
                                    <form:input path="appointServiceDTOs[${status.index}].appointName" autocomplete="off" type="text" cssClass="checkStringEmpty appointName txt" value="${appointService.appointName}"/>
                                    <form:input path="appointServiceDTOs[${status.index}].appointDate" autocomplete="off" type="text" cssClass="checkStringEmpty appointDate txt" readonly="true" value="${appointService.appointDate}"/>
                                </td>
                                <td><input class="opera1 delAppointBtn" isDef="true" type="button"/></td>
                            </tr>
                            <input type="hidden" class="relatedCustomerId" autocomplete="off"  value="${customerDTO.idStr}">
                        </c:if>
                    </c:forEach>
                </table>
            </div>
        </div>
        </bcgogo:hasPermission>
    </div>
    <div class="clear i_height"></div>
    <div class="titBody">
        <div class="lineTitle" style="width: 991px">
            <div style="float: left">故障说明</div>
            <div class="title-r" style="float: left;margin-left: 10px"><a class="blue_color" id="showProblemDescriptionBtn">点击展开<img style="float:right; margin:14px 0 0 0px;" src="images/rightArrow.png"></a></div>
            <div class="title-r" style="float: left;margin-left: 10px;display: none"><a class="blue_color" id="hideProblemDescriptionBtn">点击收起<img style="float:right; margin:14px 0 0 0px;" src="images/rightTop.png"></a></div>
        </div>
        <div class="clear"></div>
        <div class="customer" id="problemdescriptionDiv" style="display: none;">
            <div style="background:#fff; padding:5px;">
                <form:textarea path="description" style="height:141px; width:570px;padding:10px; margin-right:10px; border:#ddd 1px solid" autocomplete="off"/>
                <img src="images/discription.png"/></div>
            <div class="clear"></div>
        </div>
    </div>
    <div class="clear i_height"></div>
</div>

<div class="titBody" style="width: 1003px;">
<div class="lineTitle" style="width: 991px">
    <div class="template_r">
        <div>
            <div class="template_r_li" style="z-index: 1" id="showTemplateContainer"><a class="blue_color">选择模板</a></div>
            <div class="template_r_li2"><a id="saveTemplateBtn" class="blue_color">保存模板</a></div>

        </div>
        <div class="construction_template_a" id="templateContainer" style="display: none;width: 190px">
            <strong>请单击选择模版：</strong>
            <ul style="overflow-y: auto; overflow-x:hidden;height: 160px"></ul>
            <div class="clear"></div>
            <div align="right">（双击可修改名称）</div>
        </div>
    </div>
    单据信息
</div>
<div class="lineBody bodys" style="width: 993px;padding: 5px 5px 0;">

<div class="cuSearch">
    <div class="gray-radius" style="margin:0;">
        <h3 class="titleName" style="height: 22px;line-height:19px">施工单</h3>
        <table id="table_task" class="tab_cuSearch" cellpadding="0" cellspacing="0" style="width:970px;">
            <col width="180">
            <col width="70">
            <col width="70">
            <col width="70">
            <col width="80">
            <col width="70">
            <col width="130">
            <col>
            <col width="80">

            <tr class="titleBg">
                <td style="padding-left:10px;border-left:none;">施工内容</td>
                <td>标准工时</td>
                <td>工时单价</td>
                <td>实际工时</td>
                <td>金额</td>
                <td>施工人</td>
                <td>营业分类</td>
                <td>备注</td>
                <td style="border-right:none;">操作<input class="opera2" type="button" style="display:none;">
                </td>
            </tr>
            <tr class="space">
                <td colspan="15"></td>
            </tr>

            <c:forEach items="${repairOrderDTO.serviceDTOs}" var="serviceDTO" varStatus="status">
                <tr class="bg titBody_Bg item table-row-original">
                    <td>
                        <form:hidden autocomplete="off" path="serviceDTOs[${status.index}].id" value="${serviceDTO.id}"/>
                        <form:hidden autocomplete="off" path="serviceDTOs[${status.index}].serviceHistoryId"
                                     value="${serviceDTO.serviceHistoryId}"/>
                        <form:hidden autocomplete="off" path="serviceDTOs[${status.index}].serviceId" value="${serviceDTO.serviceId}"/>
                        <form:input autocomplete="off" path="serviceDTOs[${status.index}].service" value="${serviceDTO.service}"
                                    class="table_input checkStringEmpty J-hide-empty-droplist"
                                    title="${serviceDTO.service}"
                                    size="50"/>
                    </td>
                    <td style="display:none">
                        <form:select autocomplete="off" path="serviceDTOs[${status.index}].consumeType" value="MONEY" style="width:120px;" class="consumeType checkSelectChanged">
                        <form:option value="MONEY" label="金额"/>
                    </form:select>
                    </td>
                    <td>
                        <bcgogo:permission>

                            <bcgogo:if resourceType="render" permissions="WEB.SET_THE_BUSINESS_CONSTRUCTION">
                                <form:input autocomplete="off" path="serviceDTOs[${status.index}].standardHours"
                                            value="${serviceDTO.standardHours}"
                                            cssClass="table_input standardHours checkStringEmpty"
                                            title="${serviceDTO.standardHours}" cssStyle="width: 70px" data-filter-zero="true"/>
                            </bcgogo:if>
                            <bcgogo:else>
                                <form:input autocomplete="off" path="serviceDTOs[${status.index}].standardHours"
                                            title="${serviceDTO.standardHours}"
                                            value="${serviceDTO.standardHours}" readonly="true"
                                            cssStyle="border:none;background-color:transparent;width:70px;text-align: center;" data-filter-zero="true"/>
                            </bcgogo:else>
                        </bcgogo:permission>

                    </td>
                    <td>
                        <bcgogo:permission>
                            <bcgogo:if resourceType="render" permissions="WEB.SET_THE_BUSINESS_CONSTRUCTION">
                                <form:input autocomplete="off" path="serviceDTOs[${status.index}].standardUnitPrice"
                                            value="${serviceDTO.standardUnitPrice}"
                                            cssClass="table_input standardUnitPrice checkStringEmpty"
                                            title="${serviceDTO.standardUnitPrice}" cssStyle="width: 70px" data-filter-zero="true"/>
                            </bcgogo:if>
                            <bcgogo:else>
                                <form:input autocomplete="off" path="serviceDTOs[${status.index}].standardUnitPrice"
                                            title="${serviceDTO.standardUnitPrice}"
                                            value="${serviceDTO.standardUnitPrice}" readonly="true"
                                            cssStyle="border:none;background-color:transparent;width:70px;text-align: center;" data-filter-zero="true"/>
                            </bcgogo:else>
                        </bcgogo:permission>
                    </td>
                    <td>
                        <form:input autocomplete="off" path="serviceDTOs[${status.index}].actualHours" value="${serviceDTO.actualHours}"
                                    class="table_input actualHours checkStringEmpty" title="${serviceDTO.actualHours}"
                                    size="6" data-filter-zero="true"/>
                    </td>

                    <td>
                        <form:input autocomplete="off" path="serviceDTOs[${status.index}].total" value="${serviceDTO.total}"
                                    cssClass="serviceTotal table_input checkNumberEmpty " title="${serviceDTO.total}" data-filter-zero="true"/>
                        <form:hidden autocomplete="off" path="serviceDTOs[${status.index}].costPrice" value="0"/>
                    </td>

                    <td><span class="workersSpan">
                <form:input autocomplete="off" path="serviceDTOs[${status.index}].workers" value="${serviceDTO.workers}"
                            class="table_input checkStringEmpty" title="${serviceDTO.workers}"/>
                <img src="images/list_close.png" class="deleteWorkers" style="width:10px;cursor:pointer;display:none">

	              <form:hidden autocomplete="off" path="serviceDTOs[${status.index}].workerIds" value="${serviceDTO.workerIds}"/>
                <form:hidden autocomplete="off" path="serviceDTOs[${status.index}].templateServiceIdStr"
                             value="${serviceDTO.templateServiceIdStr}"/>
                <form:hidden autocomplete="off" path="serviceDTOs[${status.index}].templateServiceId"
                             value="${serviceDTO.templateServiceId}"/>
              </span>
                    </td>

                    <td>
                        <form:input autocomplete="off" path="serviceDTOs[${status.index}].businessCategoryName"
                                    value="${serviceDTO.businessCategoryName}"
                                    class="table_input businessCategoryName serviceCategory"
                                    hiddenValue="${serviceDTO.businessCategoryName}"/>
                        <form:hidden autocomplete="off" path="serviceDTOs[${status.index}].businessCategoryId"
                                     value="${serviceDTO.businessCategoryId}"/>
                    </td>

                    <td>
                        <form:input autocomplete="off" path="serviceDTOs[${status.index}].memo" value="${serviceDTO.memo}"
                                    cssClass="table_input checkStringEmpty" title="${serviceDTO.memo}"/>
                    </td>
                    <td style="border-right: medium none;">
                        <bcgogo:hasPermission permissions="WEB.VEHICLE_CONSTRUCTION.BASE">
                            <%--<input class="opera1" type="button">--%>

                            <a id="serviceDTOs${status.index}.deletebutton"
                               name="serviceDTOs[${status.index}].deletebutton"
                               class="opera1">删除</a>

                        </bcgogo:hasPermission>
                    </td>
                </tr>
            </c:forEach>
        </table>

        <div class="clear total-of">施工合计：
            <span class="yellow_color" id="serviceTotalSpan" data-filter-zero="true">${repairOrderDTO.serviceTotal}</span>元
        </div>
        <div class="clear"></div>
    </div>
    <div class="clear i_height"></div>
</div>

<div class="cuSearch">
    <div class="gray-radius" style="margin:0;">
        <h3 class="titleName" style="height: 22px;line-height:19px">材料单&nbsp;&nbsp;销售人&nbsp;
            <span><form:input autocomplete="off" path="productSaler" value="${repairOrderDTO.productSaler}" type="text" cssClass="checkStringEmpty textbox" style="width:200px;vertical-align:baseline"/>
            </span>
            <bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.STOREHOUSE">
                &nbsp;&nbsp;&nbsp;
                仓库<img src="images/star.jpg" class="i_tableStar" style="margin-right:5px;">
                <form:select path="storehouseId" cssClass="j_checkStoreHouse checkSelectChanged"
                             cssStyle="width:120px;height:21px;border: 1px solid #BBBBBB;" autocomplete="off">
                    <option value="">—请选择仓库—</option>
                    <form:options items="${storeHouseDTOList}" itemValue="id" itemLabel="name"/>
                </form:select>
                <input autocomplete="off" type="hidden" id="hiddenStorehouseId" value="${repairOrderDTO.storehouseId}">
                <span id="storehouseSelectTip" style="display:none;" class="red_color">必填项，请选择仓库！</span>
            </bcgogo:hasPermission>
            <bcgogo:hasPermission permissions="WEB.TXN.INVENTORY_MANAGE.REPAIR_PICKING" resourceType="menu">
                <c:if test="<%=isRepairPickingSwitchOn%>">
                    &nbsp;&nbsp;&nbsp;领料单号： <a id="repairPickingReceiptNo"
                                               style="color: #006ECA;cursor:pointer">${repairOrderDTO.repairPickingReceiptNo}</a>
                    <form:hidden autocomplete="off" path="repairPickingId"/>
                </c:if>
            </bcgogo:hasPermission>
        </h3>
        <table id="table_productNo_2" class="tab_cuSearch" cellpadding="0" cellspacing="0" style="width:970px;">

            <col width="80">
            <col width="180">
            <col width="80">
            <col width="75">
            <col width="75">
            <col width="60">
            <col width="50">
            <col width="35">
            <col width="71">
            <col width="40" class="storage_bin_col">
            <col width="60">
            <col width="75">
            <col width="75">
            <col width="70"/>


            <tr class="titleBg">
                <td style="padding-left:10px;border-left:none;">商品编号</td>
                <td>品名</td>
                <td>品牌/产地</td>
                <td>规格</td>
                <td>型号</td>
                <td>单价</td>
                <td>数量</td>
                <td>单位</td>
                <td>小计</td>
                <td class="storage_bin_td">货位</td>
                <td style="padding-left:0;">库存数量</td>
                <td style="padding-left:2px;">预留</td>
                <td>营业分类</td>
                <td style="border-right:none;">操作<input class="opera2" type="button" style="display:none;"></td>
            </tr>
            <tr class="space">
                <td colspan="14"></td>
            </tr>
            <c:forEach items="${repairOrderDTO.itemDTOs}" var="itemDTO" varStatus="status">

                <tr class="bg titBody_Bg item1 table-row-original">
                    <td style="borderLeft:none;padding:0 4px 0 2px;min-width:100px;">
                        <form:input autocomplete="off" path="itemDTOs[${status.index}].commodityCode"
                                    value='${itemDTO.commodityCode!=null?itemDTO.commodityCode:""}'
                                    class="table_input checkStringEmpty"
                                    title='${itemDTO.commodityCode!=null?itemDTO.commodityCode:""}' style="width:85%"
                                    maxlength="20"/>
                        <form:hidden autocomplete="off" path="itemDTOs[${status.index}].id" value="${itemDTO.id}"/>
                        <form:hidden autocomplete="off" path="itemDTOs[${status.index}].productType" value="${itemDTO.productType}"/>
                        <form:hidden autocomplete="off" path="itemDTOs[${status.index}].purchasePrice" value="${itemDTO.purchasePrice}" class="cPurchasePrice"/>
                        <form:hidden autocomplete="off" path="itemDTOs[${status.index}].inventoryAveragePrice" value="${itemDTO.inventoryAveragePrice}" class="cPurchasePrice"/>
                        <form:hidden autocomplete="off" path="itemDTOs[${status.index}].productId" value="${itemDTO.productId}"/>
                        <form:hidden autocomplete="off" path="itemDTOs[${status.index}].productHistoryId" value="${itemDTO.productHistoryId}"/>
                        <form:hidden autocomplete="off" path="itemDTOs[${status.index}].vehicleBrand" value="${itemDTO.vehicleBrand}"/>
                        <form:hidden autocomplete="off" path="itemDTOs[${status.index}].vehicleModel" value="${itemDTO.vehicleModel}"/>
                        <form:hidden autocomplete="off" path="itemDTOs[${status.index}].vehicleYear" value="${itemDTO.vehicleYear}"/>
                        <form:hidden autocomplete="off" path="itemDTOs[${status.index}].vehicleEngine" value="${itemDTO.vehicleEngine}"/>

                        <input autocomplete="off" id="itemDTOs${status.index}.isNewAdd" type="hidden"
                               name="itemDTOs[${status.index}].isNewAdd">
                    </td>
                    <td style="padding:0 4px 0 2px;">
                        <form:input autocomplete="off" path="itemDTOs[${status.index}].productName" value="${itemDTO.productName}"
                                    cssClass="table_input checkStringEmpty" style="width:85%"/>
                        <input autocomplete="off" class="edit1" type="button" id="itemDTOs${status.index}.editbutton"
                               name="itemDTOs[${status.index}].editbutton" onclick="searchInventoryIndex(this)"
                               style="margin-left:6px"> <input autocomplete="off" type="hidden" name="lack" id="lack"
                                                               value="${itemDTO.lack}"/>
                    </td>
                    <td style="padding:0 4px 0 2px;">
                        <form:input autocomplete="off" path="itemDTOs[${status.index}].brand" value="${itemDTO.brand}"
                                    cssClass="table_input checkStringEmpty" maxlength="100" cssStyle="width:100%;"
                                    title="${itemDTO.brand}"/>
                    </td>
                    <td style="padding:0 4px 0 2px;">
                        <form:input autocomplete="off" path="itemDTOs[${status.index}].spec" value="${itemDTO.spec}"
                                    cssClass="table_input checkStringEmpty" cssStyle="width:100%;"
                                    title="${itemDTO.spec}"/>
                    </td>
                    <td style="padding:0 4px 0 2px;">
                        <form:input autocomplete="off" path="itemDTOs[${status.index}].model" value="${itemDTO.model}"
                                    cssClass="table_input checkStringEmpty" cssStyle="width:100%;"
                                    title="${itemDTO.model}"/>
                    </td>
                    <td style="padding:0 4px 0 2px;">
                        <form:input autocomplete="off" path="itemDTOs[${status.index}].price" value="${itemDTO.price}"
                                    cssStyle="width:100%;" maxlength="8" data-filter-zero="true"
                                    cssClass="itemPrice table_input checkNumberEmpty" title="${itemDTO.price}"/>
                    </td>
                    <td style="padding:0 4px 0 2px;">
                        <form:input autocomplete="off" path="itemDTOs[${status.index}].amount" value="${itemDTO.amount}"
                                    cssStyle="width:100%;" data-filter-zero="true"
                                    cssClass="itemAmount table_input checkNumberEmpty" title="${itemDTO.amount}"/>
                    </td>
                    <td style="padding:0 4px 0 2px;">
                        <form:input autocomplete="off" path="itemDTOs[${status.index}].unit" value="${itemDTO.unit}" cssStyle="width:100%;"
                                    cssClass="itemUnit table_input checkStringEmpty" title="${itemDTO.unit}"/>
                        <form:hidden autocomplete="off" path="itemDTOs[${status.index}].storageUnit" value="${itemDTO.storageUnit}"
                                     class="itemStorageUnit table_input"/>
                        <form:hidden autocomplete="off" path="itemDTOs[${status.index}].sellUnit" value="${itemDTO.sellUnit}"
                                     class="itemSellUnit table_input"/>
                        <form:hidden autocomplete="off" path="itemDTOs[${status.index}].rate" value="${itemDTO.rate}"
                                     class="itemRate table_input"/>
                    </td>
                    <td style="padding:0 4px 0 2px;">
                        <span id="itemDTOs${status.index}.total_span" class="itemTotalSpan" data-filter-zero="true">${itemDTO.total}</span>
                        <c:if test="${not empty itemDTO.activeRecommendSupplierHtml}">
                            <a class="jian" action-type="supplier-active-recommend"
                               detail='${itemDTO.activeRecommendSupplierHtml}'></a>
                        </c:if>
                        <form:hidden autocomplete="off" path="itemDTOs[${status.index}].total" value="${itemDTO.total}"
                                     class="itemTotal table_input checkNumberEmpty"/>
                    </td>
                    <td class="storage_bin_td" style="padding:0 4px 0 2px;">
						 <span id="itemDTOs${status.index}.storageBinSpan"
                               name="itemDTOs[${status.index}].storageBinSpan">${itemDTO.storageBin}</span>
                    </td>
                    <td style="padding:0 4px 0 2px;">
                <span id="itemDTOs${status.index}.inventoryAmountSpan" data-filter-zero="true"
                      name="itemDTOs[${status.index}].inventoryAmountSpan">${itemDTO.inventoryAmountApprox}</span>
                        <form:hidden autocomplete="off" path="itemDTOs[${status.index}].inventoryAmount" value="${itemDTO.inventoryAmount}"
                                     cssClass="itemInventoryAmount table_input" readonly="true"
                                     title="${itemDTO.inventoryAmount}"/>
                        <input autocomplete="off" type="hidden" name="itemDTOs[${status.index}].inventoryAmountHid"
                               id="itemDTOs${status.index}.inventoryAmountHid" value="${itemDTO.inventoryAmount}"/>
                        <span style="display: none;" class="j_new">新</span></td>

                    <td style="text-overflow:clip;padding:0 4px 0 2px;">
                <span class="reserved" id="itemDTOs${status.index}.reservedSpan" data-filter-zero="true"
                      name="itemDTOs[${status.index}].reservedSpan">${repairOrderDTO.status=='REPAIR_SETTLED'?"":itemDTO.reserved}</span>
                        <c:choose>
                            <c:when test="${repairOrderDTO.status=='REPAIR_SETTLED'}">
                                <input type="button" name="lackMaterial" class="lackMaterial"
                                       id="itemDTOs${status.index}.yuliu"
                                       style="display:none"/>
                            </c:when>
                            <c:otherwise>
                                <input type="button" name="lackMaterial" class="lackMaterial"
                                       id="itemDTOs${status.index}.yuliu"/>
                            </c:otherwise>
                        </c:choose>
                        <form:hidden autocomplete="off" path="itemDTOs[${status.index}].reserved" value="${itemDTO.reserved}"/>
                        <form:hidden autocomplete="off" path="itemDTOs[${status.index}].templateItemId" value="${itemDTO.templateItemId}"/>
                        <form:hidden autocomplete="off" path="itemDTOs[${status.index}].templateItemIdStr"
                                     value="${itemDTO.templateItemIdStr}"/>
                    </td>

                    <td style="padding:0 4px 0 2px;">
                        <form:input autocomplete="off" path="itemDTOs[${status.index}].businessCategoryName"
                                    value="${itemDTO.businessCategoryName}"
                                    class="table_input businessCategoryName"
                                    hiddenValue="${itemDTO.businessCategoryName}"/>
                        <form:hidden autocomplete="off" path="itemDTOs[${status.index}].businessCategoryId"
                                     value="${itemDTO.businessCategoryId}"/>
                    </td>

                    <td style="border-right: medium none; padding: 0 4px 0 2px; ">
                        <bcgogo:hasPermission permissions="WEB.VEHICLE_CONSTRUCTION.BASE">
                            <%--<input class="opera1" type="button" id="itemDTOs${status.index}.opera1Btn">--%>
                            <a id="itemDTOs${status.index}.opera1Btn" name="itemDTOs[${status.index}].opera1Btn"
                               class="opera1">删除</a>
                        </bcgogo:hasPermission>
                    </td>
                </tr>
            </c:forEach>
        </table>
        <div class="clear total-of"> 材料合计：
            <span class="yellow_color" id="salesTotalSpan" data-filter-zero="true">${repairOrderDTO.salesTotal}</span>元
        </div>
        <div class="clear"></div>
    </div>
    <div class="clear i_height"></div>
</div>



<div class="cuSearch">
    <div class="gray-radius" style="margin:0;">
        <h3 class="titleName" style="height: 22px;line-height:19px">其他费用信息</h3>

        <table id="table_otherIncome" class="tab_cuSearch" cellpadding="0" cellspacing="0" style="width:970px;">
            <col width="150">
            <col width="300">
            <col>
            <col width="250">
            <col width="70">
            <tr class="titleBg">
                <td style="padding-left:10px;">费用名称</td>
                <td>金额</td>
                <td>是否计入成本</td>
                <td>备注</td>
                    <%--<td>操作</td>--%>

                <td style="border-right:none;">操作<input class="opera2" type="button" style="display:none;"></td>

            </tr>
            <tr class="space">
                <td colspan="4"></td>
            </tr>

            <c:forEach items="${repairOrderDTO.otherIncomeItemDTOList}" var="itemDTO" varStatus="status">
                <c:if test="${itemDTO!=null}">
                    <tr class="titBody_Bg item2 table-row-original">
                        <td>
                            <form:input autocomplete="off" path="otherIncomeItemDTOList[${status.index}].name"
                                        value='${itemDTO.name}' class="table_input otherIncomeKindName checkStringEmpty" maxlength="50"/>
                        </td>

                        <td>
                            <c:if test="${itemDTO.name=='材料管理费'}">

                                <form:hidden autocomplete="off" path="otherIncomeItemDTOList[${status.index}].id" value="${itemDTO.id}"/>
                                <form:hidden autocomplete="off" path="otherIncomeItemDTOList[${status.index}].templateIdStr" value="${serviceDTO.templateServiceIdStr}"/>
                                <form:hidden autocomplete="off" path="otherIncomeItemDTOList[${status.index}].templateId" value="${serviceDTO.templateServiceId}"/>

                                <c:if test="${itemDTO.otherIncomeCalculateWay=='RATIO'}">

                                    <input autocomplete="off" id="otherIncomeItemDTOList${status.index}.otherIncomePriceByRate"
                                           maxlength="100" type="radio" checked="checked"
                                           class="otherIncomePriceByRate"
                                           name="otherIncomeItemDTOList[${status.index}].priceCheckBox"
                                           style="float:left; margin-right:4px;"/>
                                    <label></label>按材料费比率计算
                                    <input autocomplete="off" id="otherIncomeItemDTOList${status.index}.otherIncomePriceRate"
                                           value="${itemDTO.otherIncomeRate}"
                                           class="txt otherIncomePriceRate txt_color"
                                           style="width:70px;"/>&nbsp;%&nbsp;&nbsp;<span id="otherIncomeItemDTOList${status.index}.otherIncomePriceSpan">${itemDTO.price}</span>元
                                    <div class="clear i_height"></div>
                                    <input autocomplete="off" id="otherIncomeItemDTOList${status.index}.otherIncomePriceByAmount"
                                           maxlength="100" type="radio"
                                           name="otherIncomeItemDTOList[${status.index}].priceCheckBox"
                                           class="otherIncomePriceByAmount"
                                           style="float:left; margin-right:4px;" data-filter-zero="true"/>
                                    <label></label>按固定金额计算&nbsp;&nbsp;

                                    <input autocomplete="off" id="otherIncomeItemDTOList${status.index}.otherIncomePriceText" value="请输入金额"
                                           style="width:100px;color:#9a9a9a;"
                                           class="txt otherIncomePriceText txt_color" data-filter-zero="true"/>元


                                </c:if>

                                <c:if test="${itemDTO.otherIncomeCalculateWay!='RATIO'}">
                                    <input autocomplete="off" id="otherIncomeItemDTOList${status.index}.otherIncomePriceByRate"
                                           maxlength="100" type="radio"
                                           name="otherIncomeItemDTOList[${status.index}].priceCheckBox"
                                           class="otherIncomePriceByRate"
                                           style="float:left; margin-right:4px;"/>
                                    <label></label>按材料费比率计算
                                    <input autocomplete="off" id="otherIncomeItemDTOList${status.index}.otherIncomePriceRate"
                                           value="请输入比率"
                                           class="txt otherIncomePriceRate txt_color"
                                           style="width:70px;color:#9a9a9a;"/>&nbsp;%&nbsp;&nbsp;<span id="otherIncomeItemDTOList${status.index}.otherIncomePriceSpan">0</span>元
                                    <div class="clear i_height"></div>
                                    <input autocomplete="off" id="otherIncomeItemDTOList${status.index}.otherIncomePriceByAmount"
                                           maxlength="100" type="radio" checked="checked"
                                           name="otherIncomeItemDTOList[${status.index}].priceCheckBox"
                                           class="otherIncomePriceByAmount" value="${itemDTO.price}"
                                           style="float:left; margin-right:4px;"/>
                                    <label></label>按固定金额计算&nbsp;&nbsp;

                                    <input autocomplete="off" id="otherIncomeItemDTOList${status.index}.otherIncomePriceText"
                                           value="${itemDTO.price}"
                                           style="width:100px;" class="txt otherIncomePriceText txt_color"/>元

                                </c:if>

                                <form:input autocomplete="off" path="otherIncomeItemDTOList[${status.index}].price"
                                            value="${itemDTO.price}" type="hidden"
                                            class="table_input otherIncomePrice checkStringEmpty" style="width:90%"/>
                                <form:input autocomplete="off" path="otherIncomeItemDTOList[${status.index}].otherIncomeRate"
                                            value="${itemDTO.otherIncomeRate}" type="hidden" style="width:90%"/>
                                <form:input autocomplete="off" path="otherIncomeItemDTOList[${status.index}].otherIncomeCalculateWay"
                                            value="${itemDTO.otherIncomeCalculateWay}" type="hidden"/>

                            </c:if>

                            <c:if test="${itemDTO.name!='材料管理费'}">

                                <form:hidden autocomplete="off" path="otherIncomeItemDTOList[${status.index}].id" value="${itemDTO.id}"/>
                                <form:hidden autocomplete="off" path="otherIncomeItemDTOList[${status.index}].templateIdStr"
                                             value="${serviceDTO.templateServiceIdStr}"/>
                                <form:hidden autocomplete="off" path="otherIncomeItemDTOList[${status.index}].templateId"
                                             value="${serviceDTO.templateServiceId}"/>
                                <form:input autocomplete="off" path="otherIncomeItemDTOList[${status.index}].price"
                                            value="${itemDTO.price}" data-filter-zero = "true"
                                            class="table_input otherIncomePrice checkStringEmpty" style="width:90%"/>
                            </c:if>

                        </td>

                        <td>

                            <c:if test="${itemDTO.calculateCostPrice=='TRUE'}">

                                <input autocomplete="off" id="otherIncomeItemDTOList${status.index}.otherIncomeCostPriceCheckbox"
                                       maxlength="100" type="checkbox" checked="checked"
                                       name="otherIncomeItemDTOList[${status.index}].checkbox"
                                       class="otherIncomeCostPriceCheckbox"
                                       style="float:left; margin-right:4px; margin-top:3px;"/>
                                <label style="float:left; margin-right:4px;"/></label>计入成本
                             <span id="otherIncomeItemDTOList${status.index}.otherIncomeSpan">
                               <form:input autocomplete="off" path="otherIncomeItemDTOList[${status.index}].otherIncomeCostPrice"
                                           value="${itemDTO.otherIncomeCostPrice}"
                                           class="table_input otherIncomeCostPrice checkStringEmpty"
                                           style="width:70px;" data-filter-zero="true"/>

                             </span>
                            </c:if>

                            <c:if test="${itemDTO.calculateCostPrice !='TRUE'}">

                                <input autocomplete="off" id="otherIncomeItemDTOList${status.index}.otherIncomeCostPriceCheckbox"
                                       maxlength="100" type="checkbox"
                                       name="otherIncomeItemDTOList[${status.index}].checkbox"
                                       class="otherIncomeCostPriceCheckbox"
                                       style="float:left; margin-right:4px;"/>
                                <label style="float:left; margin-right:4px;"/></label>计入成本
                                       <span style="display:none;"
                                             id="otherIncomeItemDTOList${status.index}.otherIncomeSpan">
                                         <form:input autocomplete="off" path="otherIncomeItemDTOList[${status.index}].otherIncomeCostPrice"
                                                     value="${itemDTO.otherIncomeCostPrice}"
                                                     class="table_input otherIncomeCostPrice checkStringEmpty"
                                                     style="width:70px;"/>

                                       </span>
                            </c:if>

                        </td>


                        <td>
                            <form:input autocomplete="off" path="otherIncomeItemDTOList[${status.index}].memo" maxlength="100"
                                        value="${itemDTO.memo}"
                                        class="table_input  memo checkStringEmpty" style="width:90%"/>
                        </td>

                        <td style="border-right:none;">
                            <a id="otherIncomeItemDTOList${status.index}.deletebutton" style="margin-left: 5px;"
                               name="otherIncomeItemDTOList[${status.index}].deletebutton"
                               class="opera1">删除</a>
                        </td>
                    </tr>

                </c:if> </c:forEach>
        </table>

        <div class="clear total-of">其他合计：<span class="yellow_color"
                                               id="otherTotalSpan" data-filter-zero="true">${repairOrderDTO.otherIncomeTotal}</span>元
        </div>
        <div class="clear"></div>
    </div>
    <div class="clear i_height"></div>
</div>



<div class="tableInfo" id="div_tablereservateTime">
    <div class="reservateTime" style="width: 500px;float: left">
        <div class="reservateText" style="font-size:14px; font-weight:bold;">预约（实际）出厂时间：
            <form:input id="endDateStr" autocomplete="off" path="endDateStr" cssClass="checkStringEmpty textbox" initenddatestrvalue="${repairOrderDTO.endDateStr}" readonly="true"/>
        </div>
    </div>


    <div class="total" style="width: 400px;float: right">
        <div style="font-size:14px; font-weight:bold;float: right">单据总额：
            <span class="yellow_color" style="color:#FF5E04;" id="totalSpan" title="${repairOrderDTO.total}" data-filter-zero="true">${repairOrderDTO.total == 0?"0":repairOrderDTO.total}</span>元
            <form:hidden autocomplete="off" path="total" value="${repairOrderDTO.total}"/>
            <form:hidden autocomplete="off" path="totalHid" value="${repairOrderDTO.total}"/></div>
        <form:hidden autocomplete="off" path="serviceTotal" value="${repairOrderDTO.serviceTotal}"/>
        <form:hidden autocomplete="off" path="salesTotal" value="${repairOrderDTO.salesTotal}"/>
        <form:hidden autocomplete="off" path="cashAmount" value="${repairOrderDTO.cashAmount}"/>
        <form:hidden autocomplete="off" path="bankAmount" value="${repairOrderDTO.bankAmount}"/>
        <form:hidden autocomplete="off" path="bankCheckAmount" value="${repairOrderDTO.bankCheckAmount}"/>
        <form:hidden autocomplete="off" path="bankCheckNo" value="${repairOrderDTO.bankCheckNo}"/>
        <form:hidden autocomplete="off" path="orderDiscount" value="${repairOrderDTO.orderDiscount}"/>
        <form:hidden autocomplete="off" path="accountMemberNo" value="${repairOrderDTO.accountMemberNo}"/>
        <form:hidden autocomplete="off" path="accountMemberPassword" value="${repairOrderDTO.accountMemberPassword}"/>
        <form:hidden autocomplete="off" path="memberAmount" value="${repairOrderDTO.memberAmount}"/>
        <form:hidden autocomplete="off" path="memberDiscountRatio"/>
        <form:hidden autocomplete="off" path="afterMemberDiscountTotal"/>
        <input type="hidden" id="couponAmount" name="couponAmount" autocomplete="off"  value="${repairOrderDTO.couponAmount}"/>

        <div style="display:none">实收：
            <span>
                <form:input autocomplete="off" path="settledAmount" title="${repairOrderDTO.settledAmount}" cssClass="checkNumberEmpty" value="${repairOrderDTO.settledAmount}" cssStyle="width: 40px;"/>
                <form:hidden autocomplete="off" path="settledAmountHid" value="${repairOrderDTO.settledAmountHid}"/>
            </span>
        </div>
        <div style="display:none">欠款：
            <span>
                <form:input autocomplete="off" path="debt" value="${repairOrderDTO.debt}" title="${repairOrderDTO.debt}" cssClass="checkNumberEmpty" cssStyle="width: 40px;"/>
                <form:hidden autocomplete="off" path="debtHid" value="${repairOrderDTO.debt}"/>
            </span>
        </div>
        <div style="display:none">
            <input autocomplete="off" id="input_makeTime" type="button" value="设置还款时间" style="display: none"/>
            <input autocomplete="off" type="hidden" id="isMakeTime" value="0"/>
            <input autocomplete="off" type="hidden" id="pageType" value=""/>
            <input autocomplete="off" type="hidden" id="isInvoicing" value="true"/>
            <form:hidden autocomplete="off" path="huankuanTime" value=""/> <c:if test="${repairOrderDTO.orderDiscount>0}">
            <div>
                折扣:${repairOrderDTO.orderDiscount}
            </div>
        </c:if>
            <c:if test="${repairOrderDTO.debt>0&&(customerRecordDTO.repayDateStr!=null&&customerRecordDTO.repayDateStr !=\"\")}">
                <div>预计还款日期：${customerRecordDTO.repayDateStr}</div>
                <input autocomplete="off" type="hidden" id="repayDateStr" value="${customerRecordDTO.repayDateStr}"/> </c:if>
        </div>
    </div>
    <div class="danju_beizhu mt-30 clear">
        <span style="font-size:14px; font-weight:bold; color:#000000; ">备注： </span>
        <form:input autocomplete="off" path="memo" value="${repairOrderDTO.memo}" cssClass="checkStringEmpty textbox" maxlength="500"
                    cssStyle="width: 920px; margin-left:-10px;"/>
    </div>

</div>


</div>
<div class="lineBottom"></div>
<div class="clear i_height"></div>
</div>
<div class="height"></div>
<div class="btn_div_Img" id="saveDraftOrder_div"
     <c:if test="${not empty repairOrderDTO.id}">style="display:none;"</c:if>>
    <input type="button" id="saveDraftBtn" class="i_savedraft" value="" onfocus="this.blur();"/>

    <div style="width:100%; ">保存草稿</div>
</div>
<bcgogo:permission>
    <bcgogo:if permissions="WEB.VEHICLE_CONSTRUCTION.CONSTRUCT.CANCEL&&WEB.VEHICLE_CONSTRUCTION.CONSTRUCT.BASE">
        <div class="invalidImg">
            <input id="nullifyBtn" type="button" onfocus="this.blur();">

            <div class="invalidWords" id="invalidWords">作废</div>
        </div>


    </bcgogo:if> </bcgogo:permission>
<c:if test="${repairOrderDTO.status eq 'REPAIR_SETTLED' || repairOrderDTO.status eq 'REPAIR_REPEAL' }">
    <div class="copyInput_div" id="copyInput_div" style="margin:0">
        <input id="copyInput" type="button" onfocus="this.blur();"/>

        <div class="copyInput_text_div" id="copyInput_text">复制</div>
    </div>
</c:if>
<bcgogo:permission>
    <bcgogo:if permissions="WEB.VEHICLE_CONSTRUCTION.BASE&&WEB.VEHICLE_CONSTRUCTION.CONSTRUCT.BASE">
        <div class="btn_div_Img" id="cancel_div">
            <input id="cancelBtn" type="button" class="cancel j_btn_i_operate" onfocus="this.blur();"/>

            <div class="optWords">清空</div>
        </div>
    </bcgogo:if>
</bcgogo:permission>

<bcgogo:hasPermission permissions="WEB.VEHICLE_CONSTRUCTION.INSURANCE">
    <div class="btn_div_Img" id="toInsuranceDiv" style="width: 72px;">
        <c:if test="${empty repairOrderDTO.insuranceOrderDTO}">
            <input type="button" id="toInsuranceBtn" class="sureInventory" value="" style="margin: 12px 0 0;" onfocus="this.blur();"/>
            <div style="width:100%; ">生成保险理赔</div>
        </c:if>
    </div>
</bcgogo:hasPermission>
<div class="btn_div_Img" id="createQualifiedDiv" style="width: 72px;">
    <c:if test="${not empty repairOrderDTO && empty repairOrderDTO.qualifiedNo && repairOrderDTO.status!='REPAIR_REPEAL'}">
        <input type="button" id="createQualified" class="qualified" value="" style="margin: 12px 0 0;" onfocus="this.blur();"/>
        <div style="width:100%; ">生成合格证</div>
    </c:if>
</div>


<div class="shopping_btn" style="float:right; clear:right; margin-top:16px;">
        <%----%>
    <bcgogo:hasPermission permissions="WEB.VEHICLE_CONSTRUCTION.CONSTRUCT.SAVE">
        <c:if test="${('REPAIR_SETTLED' ne repairOrderDTO.status) && ('REPAIR_REPEAL' ne repairOrderDTO.status)}">
            <div class="btn_div_Img" id="save_div">
                <input id="saveBtn" type="button" class="sendSingle j_btn_i_operate" onfocus="this.blur();"/>

                <div class="optWords" id="saveA">派单</div>
            </div>
        </c:if> </bcgogo:hasPermission> <bcgogo:hasPermission permissions="WEB.VEHICLE_CONSTRUCTION.CONSTRUCT.FINISH">
    <c:if test="${('REPAIR_SETTLED' ne repairOrderDTO.status) && ('REPAIR_REPEAL' ne repairOrderDTO.status)}">
        <div class="btn_div_Img" id="finish_div" ${repairOrderDTO.isFinishBtnShow ? null:"style='display:none'"}>
            <input id="finishBtn" type="button" class="completion j_btn_i_operate" onfocus="this.blur();"/>

            <div class="optWords" id="saveB">完工提醒</div>
        </div>
    </c:if> </bcgogo:hasPermission> <bcgogo:hasPermission permissions="WEB.VEHICLE_CONSTRUCTION.CONSTRUCT.SETTLE">
    <%--<c:if test="${('REPAIR_SETTLED' ne repairOrderDTO.status) && ('REPAIR_REPEAL' ne repairOrderDTO.status)}">--%>
    <c:if test="${('REPAIR_REPEAL' ne repairOrderDTO.status)}">
        <div class="btn_div_Img" id="account_div" ${repairOrderDTO.isAccountBtnShow ? null:"style='display:none'"}>
            <input id="accountBtn" type="button" class="saleAccount j_btn_i_operate" onfocus="this.blur();"/>

            <div class="optWords" id="saveC">交车结算</div>
        </div>
    </c:if> </bcgogo:hasPermission>
    <bcgogo:permission>
        <bcgogo:if permissions="WEB.VEHICLE_CONSTRUCTION.CONSTRUCT.PRINT&&WEB.VEHICLE_CONSTRUCTION.CONSTRUCT.BASE">
            <div class="btn_div_Img" id="print_div" style="display:none">
                <input id="printBtn" type="button" class="print j_btn_i_operate" onfocus="this.blur();"/>

                <div class="optWords">打印</div>
            </div>
        </bcgogo:if>

    </bcgogo:permission>
</div>
<table cellpadding="0" cellspacing="0" class="carWash" id="table_carWash" style="display:none;">
    <col width="250"/>
    <col width="350"/>
    <col width="300"/>
    <col width="228"/>
    <tr class="i_tabelBorder">
        <td colspan="3" style="padding-right:5px; line-height:25px;text-align:left;font-weight:bold;font-size:14px;">
            施工人：<input autocomplete="off" id="washWorker" name="washWorker" type="text" style="width:200px;"/>若施工有多人，请以逗号分开
        </td>
        <td>
            <input type="button" id="printWash" class="carWashBtn" value="打印洗车票" onfocus="this.blur();"/>
        </td>
    </tr>
    <tr class="stock_bottom" id="stock_bottom_wash" <c:if
            test="${customerCardDTO == null}"> style="display: none"</c:if>>
        <td style="text-align:center;">会员</td>
        <td>上次消费时间：
                <span id="lastWashTime">
                    <c:forEach var="washOrderDTO" items="${washOrderDTOs}"
                               end="0">${washOrderDTO.creationDate}</c:forEach>
                </span>
        </td>
        <td class="surplus">
            还剩<span id="remainWashTimes">${customerCardDTO.washRemain}
            <input autocomplete="off" type="hidden" id="washRemain" value="${customerCardDTO.washRemain}"/></span>次
            <input autocomplete="off" type="hidden" id="todayWashTimes" value="${todayWashTimes}"/>
        </td>
        <td><input autocomplete="off" type="button" id="sureWashBtn" class="carWashBtn" value="确认洗车" onfocus="this.blur();"/></td>
    </tr>
    <tr class="border">
        <td style="text-align:center;">非会员</td>
        <td class="surplus">本次洗车 <input autocomplete="off" id="normalCash" name="normalCash" type="text"
                                        onkeyup='this.value=this.value.replace(/[^0-9]\D*$/,"")'/>元
        </td>
        <td></td>
        <td><input type="button" id="normalWashBtn" class="carWashBtn" value="结 账" onfocus="this.blur();"/></td>
    </tr>
    <bcgogo:hasPermission permissions="WEB.CUSTOMER_MANAGER.MEMBERSHIP_PACKAGE_MANAGER.BUY_CARD">
        <tr class="btn_invent">
            <td style="text-align:center;">购卡/续卡</td>
            <td class="surplus">
                充值金额 <input autocomplete="off" id="chargeCash" name="chargeCash" type="text"
                            onkeyup='this.value=this.value.replace(/[^0-9]\D*$/,"")'/>元
            </td>
            <td class="surplus">
                购买次数 <input autocomplete="off" id="chargeTimes" name="chargeTimes" type="text"
                            onkeyup='this.value=this.value.replace(/[^0-9]\D*$/,"")'/>次
            </td>
            <td class="btnOperas">
                <input type="button" id="chargeBtn" class="carWashBtns" value="确 定" onfocus="this.blur();"/>
            </td>
        </tr>
    </bcgogo:hasPermission>
</table>
<input autocomplete="off" id="isAllMakeTime" type="hidden" value="0"> <%--缓存更多客户信息--%>
<input autocomplete="off" type="hidden" id="hidName"/>
<input autocomplete="off" type="hidden" id="hidShortName"/>
<input autocomplete="off" type="hidden" id="hidAddress"/>
<input autocomplete="off" type="hidden" id="hidContact"/>
<input autocomplete="off" type="hidden" id="hidMobile"/>
<input autocomplete="off" type="hidden" id="hidPhone"/>
<input autocomplete="off" type="hidden" id="hidFax"/>
<input autocomplete="off" type="hidden" id="hidMemberNumber"/>
<input autocomplete="off" type="hidden" id="hidBirthdayString"/>
<input autocomplete="off" type="hidden" id="hidQQ"/>
<input autocomplete="off" type="hidden" id="hidEmail"/>
<input autocomplete="off" type="hidden" id="hidBank"/>
<input autocomplete="off" type="hidden" id="hidBankAccountName"/>
<input autocomplete="off" type="hidden" id="hidAccount"/>
<input autocomplete="off" type="hidden" id="isPrint" value="${repairOrderDTO.print}" name="print">
</form:form>
<div class="height"></div>
</div>
</div>

<%--购卡续卡完后回来调用这个input的click方法来初始化车主信息--%>
<input autocomplete="off" type="hidden" id="callBackBuyCard"/>
<input autocomplete="off" type="hidden" id="lastWashOrderId" value="${lastWashOrderId}">


<div class="zuofei" id="orderStatusImag" style="left: 540px;top: 360px"></div>
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
<input type="button" id="idAddRow" value="确定" style="display:none;"
       onclick="addOneRow()"/> <%--------------------欠款结算  2011-12-14-------------------------------%>
<iframe id="iframe_qiankuan" style="position:absolute; left:0px; top:300px; display:none;z-index:8;"
        allowtransparency="true" width="1000px" height="900px" frameborder="0" src="" scrolling="no">
</iframe>

<iframe id="iframe_PopupBox_account" style="position:absolute;z-index:5; left:500px; top:300px; display:none;"
        allowtransparency="true" width="900" height="450px" frameborder="0" src="" scrolling="no"></iframe>
<iframe id="iframe_CardList" style="position:absolute;z-index:9; left:300px; top:200px; display:none;"
        allowtransparency="true" width="850px" height="300px" frameborder="0" src=""></iframe>
<iframe id="iframe_buyCard" style="position:absolute;z-index:7; left:300px; top:10px; display:none;"
        allowtransparency="true" width="800px" height="740px" scrolling="no" frameborder="0" src=""></iframe>

<iframe id="iframe_moreUserInfo"  style="position:absolute;z-index:5; left:200px; top:50px; display:none;"
        allowtransparency="true" width="1000px" height="900px" scrolling="no" frameborder="0" src=""></iframe>
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
<div id="repairAndDraftOrder_dialog" style="display:none">
    <div class="i_draft_table">
        <table cellpadding="0" cellspacing="0" class="i_draft_table_box" id="repair_draft_table">
            <col>
            <col width="50">
            <col width="100">
            <col width="150">
            <col width="100">
            <col width="100">
            <col width="250">
            <col width="250">
            <col width="100">
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
                <td>单据类型</td>
                <td class="tab_last"></td>
            </tr>
        </table>

        <!--分页-->
        <div class="hidePageAJAX">
            <jsp:include page="/common/pageAJAX.jsp">
                <jsp:param name="url" value="txn.do?method=getRepairAndDraftOrders"></jsp:param>
                <jsp:param name="data" value="{startPageNo:1,orderTypes:'REPAIR',vehicleId:jQuery('#vechicleId').val()}"></jsp:param>
                <jsp:param name="jsHandleJson" value="initReapirAndDraftOrderTable"></jsp:param>
                <jsp:param name="dynamical" value="dynamical2"></jsp:param>
            </jsp:include>
        </div>
    </div>
</div>

<div id="storeHouseDialog" style="display:none;" class="alertMain">
    <div style="margin-top: 10px;">
        <span style="font-size: 14px;">您没有选择仓库信息！请选择仓库：</span>
        <select id="storehouseDiv"
                style="width:120px;height:21px;border: 1px solid #BBBBBB;" autocomplete="off">
            <option value="">—请选择仓库—</option>
            <c:forEach items="${storeHouseDTOList}" var="storeHouseDTO">
                <option value="${storeHouseDTO.id}">${storeHouseDTO.name}</option>
            </c:forEach>
        </select>
        <input id="btnType" type="hidden"/>
    </div>
    <div class="button" style="width:100%;margin-top: 10px;">
        <a id="confirmBtn1" class="btnSure" href="javascript:;">确 定</a>
        <a id="cancleBtn" class="btnSure" href="javascript:;">取消</a>
    </div>
</div>
<div id="commodityCode_dialog" style="display:none">
    <span id="commodityCode_dialog_msg"></span>
</div>
<div id="mask" style="display:block;position: absolute;"></div>
<%@ include file="/sms/enterPhone.jsp" %>
<div id="dialog-confirm-invoicing" title="提醒" style="display: none;">
    <p><span class="ui-icon ui-icon-alert" style="float:left; margin:0 7px 20px 0;"></span>

    <div id="dialog-confirm-text">单据日期与当前日期不一致，是否确定修改单据日期？</div>
    </p>
</div>
<div id="dialog-confirm-account-date" class="prompt_box" style="display: none">
    <div class="">
        <p><strong>友情提示：</strong>出厂日期与当前系统日期不一致！<br/>请正确选择单据结算日期，财务营业流水按结算日期统计！</p>
        <table width="100%" border="0" cellspacing="0" cellpadding="0">
            <tr>
                <td width="35%">请选择单据结算日期：</td>
                <td width="65%"><div class="fl"><input id="confirm_account_date_radio" name="account-date-radio" type="radio" checked="true" />
                    <span id="confirm_account_date_span"></span><span style="color:#767C7C">(单据日期)</span></div>
                </td>
            </tr>
            <tr>
                <td>&nbsp;</td>
                <td><div class="fl"><input name="account-date-radio" type="radio" value="" />
                    <span id="confirm_current_date_span"></span><span style="color:#767C7C">(当前日期)</span></div>
                </td>
            </tr>
        </table>
        <div class="clear"></div>
        <div class="wid275">
            <div class="addressList"> <a class="ok_btn" href="#">确 定</a> <a class="cancel_btn" href="#">取 消</a></div>
        </div>
        <div class="clear"></div>
    </div>
</div>
<div id="allocate_or_purchase_div" style="display:none">
    <div>当前单据缺料商品在其他仓库有库存，是否调拨？</div>
</div>
<div id="templateNameInputDiv" style="display: none">
    <p class="validateTips">请输入模板名称：</p>
    <br/>
    <input autocomplete="off" class="txt" id="repairOrderTemplateNameInput" value="" maxlength="10" style="width:220px;height:20px"/>
</div>

<bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.STOREHOUSE">
    <jsp:include page="../txn/common/selectStoreHouse.jsp"/>
</bcgogo:hasPermission>
<jsp:include page="dispatchRepairOrderPreBuyOrder.jsp"></jsp:include>
<jsp:include page="/txn/orderOperationLog.jsp" />
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>