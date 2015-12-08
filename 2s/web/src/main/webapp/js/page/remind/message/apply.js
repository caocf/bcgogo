var applyInviteStatus = "", applyDynamical = "";
$(function () {
    var params = {startPageNo: 1, maxRows: 10},
        messagePopup = App.Module.messagePopup;
    $(".supplier_accept").live("click", function () {
        if ($(this).attr("lock") || !$(this).attr("inviteId")) {
            return;
        }
        $(this).attr("lock", true);
        var params = {inviteId: $(this).attr("inviteId")},
            applyInviteStatus = $(this).attr("applyInviteStatus"),
            originShopName = $(this).attr("originShopName"),
            url = "apply.do?method=acceptCustomerApply", me = this;
        APP_BCGOGO.Net.asyncAjax({
            type: "POST",
            url: url,
            data: params,
            cache: false,
            dataType: "json",
            success: function (result) {
                if (result.success) {
                    if(userGuide.currentFlowName && userGuide.currentFlowName=="CUSTOMER_APPLY_GUIDE")
                        userGuide.caller("CUSTOMER_APPLY_GUIDE_APPLY_AGREE", "CUSTOMER_APPLY_GUIDE");
                    nsDialog.jAlert("通过请求成功！" + originShopName + "已经成为您的关联客户！", "", function () {
                        userGuide.invoker("CUSTOMER_APPLY_GUIDE_SHOW_APPLY_NAVIGATE");
                        getInviteList(applyInviteStatus, params);
                        messagePopup.inquire();
                        if ($(me).attr("applyInviteStatus") && $(me).attr("applyInviteStatus").indexOf("Customer") != -1) {
                            drawNumberOfMessageCenterByType("pendingCustomerApplyNumber", 1);
                        }
                    });
                } else {
                    nsDialog.jAlert(result.msg);
                }
                $(this).removeAttr("lock");
            }
        });
    });
    $(".customer_accept").live("click", function () {
        if ($(this).attr("lock") || !$(this).attr("inviteId")) {
            return;
        }
        $(this).attr("lock", true);
        var params = {inviteId: $(this).attr("inviteId")},
            applyInviteStatus = $(this).attr("applyInviteStatus"),
            originShopName = $(this).attr("originShopName"),
            url = "apply.do?method=acceptSupplierApply", me = this;
        APP_BCGOGO.Net.asyncAjax({
            type: "POST",
            url: url,
            data: params,
            cache: false,
            dataType: "json",
            success: function (result) {
                if (result.success) {
                    if (userGuide.currentFlowName && userGuide.currentFlowName=="SUPPLIER_APPLY_GUIDE") {
                        userGuide.caller("SUPPLIER_APPLY_GUIDE_APPLY_AGREE", "SUPPLIER_APPLY_GUIDE");
                    }
                    nsDialog.jAlert("通过请求成功！" + originShopName + "已经成为您的关联供应商！", "", function () {
                        userGuide.invoker("SUPPLIER_APPLY_GUIDE_SHOW_APPLY_NAVIGATE");
                        getInviteList(applyInviteStatus, params);
                        messagePopup.inquire();
                        if ($(me).attr("applyInviteStatus") && $(me).attr("applyInviteStatus").indexOf("Supplier") != -1) {
                            drawNumberOfMessageCenterByType("pendingSupplierApplyNumber", 1);
                        }
                    });
                } else {
                    nsDialog.jAlert(result.msg);
                }
                $(this).removeAttr("lock");
            }
        });
    });
    $(".refuseApply").live("click", function () {
        if ($(this).attr("lock") || !$(this).attr("inviteId")) {
            return;
        }
        $(this).attr("lock", true);
        applyInviteStatus = $(this).attr("applyInviteStatus");
        var $refuseDom = $(this), me = this;
        $("#refuse_msg_dialog").dialog({
            resizable: false,
            title: "拒绝理由",
            height: 185,
            width: 300,
            modal: true,
            closeOnEscape: false,
            buttons: {
                "确定": function () {
                    var refuseMsg = $("#refuse_msg").val() == "拒绝理由" || !$("#refuse_msg").val() ?
                        "本公司经营产品与您的公司不符！" : $("#refuse_msg").val();
                    var params = {inviteId: $refuseDom.attr("inviteId"), refuseMsg: refuseMsg},
                        url = "apply.do?method=refuseApply";
                    userGuide.clear();
                    APP_BCGOGO.Net.asyncAjax({
                        type: "POST",
                        url: url,
                        data: params,
                        cache: false,
                        dataType: "json",
                        success: function (result) {
                            userGuide.clear();
                            if (result.success) {
                                nsDialog.jAlert("您已拒绝对方的请求！", "", function () {
                                    if (userGuide.currentFlowName == "CUSTOMER_APPLY_GUIDE")userGuide.caller("CUSTOMER_APPLY_GUIDE_APPLY_REJECT");
                                    if (userGuide.currentFlowName == "SUPPLIER_APPLY_GUIDE")userGuide.caller("SUPPLER_APPLY_GUIDE_APPLY_REJECT");
                                    getInviteList(applyInviteStatus, params);
                                    messagePopup.inquire();
                                    if ($(me).attr("applyInviteStatus")) {
                                        if ($(me).attr("applyInviteStatus").indexOf("Customer") != -1) {
                                            drawNumberOfMessageCenterByType("pendingCustomerApplyNumber", 1);
                                        } else {
                                            drawNumberOfMessageCenterByType("pendingSupplierApplyNumber", 1);
                                        }
                                    }
                                });
                            } else {
                                nsDialog.jAlert(result.msg);
                            }
                        }
                    });
                    $(this).dialog("close");
                },
                "取消": function () {
                    $(this).dialog("close");
                }
            },
            close: function () {
                $refuseDom.removeAttr("lock");
                $("#refuse_msg").removeClass("black_color").addClass("gray_color");
                $("#refuse_msg").val($("#refuse_msg").attr("init_word"));
            }
        });
    });
    $("#refuse_msg").bind("keydown", function () {
        if ($(this).hasClass("gray_color") && $(this).val() == $(this).attr("init_word")) {
            $("#refuse_msg").removeClass("gray_color").addClass("black_color").val("");
        }
    });

    $(".inviteStatus").click(function (e) {
        var invites = $(".inviteStatus").not(e.target);
        for (var i = 0, max = invites.length; i < max; i++) {
            $(invites[i]).removeClass("hoverOne").addClass("btnOne");
        }
        $(e.target).removeClass("btnOne").addClass("hoverOne");
        getInviteList($(e.target).attr("inviteStatus"), params);
    });

    $("#deleteHandledApply").click(function (e) {
        nsDialog.jConfirm("是否确认删除该关联请求，若删除则其他用户无法查看该请求？", "提示", function (resultValue) {
            if (resultValue) {
                var checkboxes = $("#" + $(e.target).attr("operatorArea") + " input:checked").not($("#selectAllCustomerApplies")).not($("#selectAllSupplierApplies"));
                if (checkboxes.length <= 0)return;
                var shopRelationInviteIds = "", deleteCustomerInviteNum = 0, deleteSupplierInviteNum = 0;
                for (var i = 0, max = checkboxes.length; i < max; i++) {
                    shopRelationInviteIds += $(checkboxes[i]).attr("shopRelationInviteId") + ",";
                    if ($(checkboxes[i]).attr("data-invite-type") == "CUSTOMER_INVITE") deleteCustomerInviteNum++;
                    if ($(checkboxes[i]).attr("data-invite-type") == "SUPPLIER_INVITE") deleteSupplierInviteNum++;
                }
                if (shopRelationInviteIds.length == 0)return;
                APP_BCGOGO.Net.asyncAjax({
                    type: "POST",
                    url: "apply.do?method=deleteShopRelationInvite",
                    data: { shopRelationInviteIds: shopRelationInviteIds},
                    cache: false,
                    dataType: "json",
                    success: function (result) {
                        if (result.success) {
                            getInviteList(applyInviteStatus, params);
                            messagePopup.inquire();
                            if (deleteCustomerInviteNum > 0) {
                                drawNumberOfMessageCenterByType("pendingCustomerApplyNumber", deleteCustomerInviteNum);
                            }
                            if (deleteSupplierInviteNum > 0) {
                                drawNumberOfMessageCenterByType("pendingSupplierApplyNumber", deleteSupplierInviteNum);
                            }
                        }
                    }
                });
            }
        });
    });

    $(".showCustomerBtn").live("click", function () {
        var customerId = $(this).attr("data-customer-id");
        if (!G.Lang.isEmpty(customerId)) {
            if (userGuide.currentFlowName)userGuide.caller("CUSTOMER_APPLY_GUIDE_SUCCESS");
            window.open("unitlink.do?method=customer&customerId=" + customerId, "_blank");
        }
    });

    $(".showSupplierBtn").live("click", function () {
        var supplierId = $(this).attr("data-supplier-id");
        if (!G.Lang.isEmpty(supplierId)) {
            userGuide.caller("SUPPLIER_APPLY_GUIDE_SUCCESS");
            window.open("unitlink.do?method=supplier&supplierId=" + supplierId, "_blank");
        }
    });
    $("#selectAllCustomerApplies").live("click", function (e) {
        var checkboxes = $("#customerApplies").find("[type=checkbox]").not(this),
            selected = $(this).attr("checked");
        for (var i = 0, max = checkboxes.length; i < max; i++) {
            if (selected) {
                $(checkboxes[i]).attr("checked", "checked");
            } else {
                $(checkboxes[i]).removeAttr("checked");
            }
        }
    });

    $("#selectAllSupplierApplies").live("click", function (e) {
        var checkboxes = $("#supplierApplies").find("[type=checkbox]").not(this),
            selected = $(this).attr("checked");
        for (var i = 0, max = checkboxes.length; i < max; i++) {
            if (selected) {
                $(checkboxes[i]).attr("checked", "checked");
            } else {
                $(checkboxes[i]).removeAttr("checked");
            }
        }
    });


});
function getInviteList(inviteStatus, params) {
    var postFun;
    applyInviteStatus = inviteStatus;
    switch (inviteStatus) {
        case "allSupplier":
            params['inviteType'] = "SUPPLIER_INVITE";
            params['statusStr'] = "ACCEPTED,REFUSED,PENDING";
            postFun = drawSupplierApplies;
            applyDynamical = "_supplier_apply";
            break;
        case "pendingSupplier":
            params['inviteType'] = "SUPPLIER_INVITE";
            params['statusStr'] = "PENDING";
            postFun = drawSupplierApplies;
            applyDynamical = "_supplier_apply";
            break;
        case "handledSupplier":
            params['inviteType'] = "SUPPLIER_INVITE";
            params['statusStr'] = "ACCEPTED,REFUSED";
            postFun = drawSupplierApplies;
            applyDynamical = "_supplier_apply";
            break;
        case "allCustomer":
            params['inviteType'] = "CUSTOMER_INVITE";
            params['statusStr'] = "ACCEPTED,REFUSED,PENDING";
            postFun = drawCustomerApplies;
            applyDynamical = "_customer_apply";
            break;
        case "pendingCustomer":
            params['statusStr'] = "PENDING";
            params['inviteType'] = "CUSTOMER_INVITE";
            postFun = drawCustomerApplies;
            applyDynamical = "_customer_apply";
            break;
        case "handledCustomer":
            params['inviteType'] = "CUSTOMER_INVITE";
            params['statusStr'] = "ACCEPTED,REFUSED";
            postFun = drawCustomerApplies;
            applyDynamical = "_customer_apply";
            break;
        default:
            G.error("apply status error![apply.js]");
            return;
            break;
    }
    APP_BCGOGO.Net.asyncAjax({
        type: "POST",
        url: "apply.do?method=searchInvite",
        data: params,
        cache: false,
        dataType: "json",
        success: function (data) {
            initPage(data, applyDynamical, "apply.do?method=searchInvite", '', postFun.name, '', '', params, '');
            postFun(data);
        }
    });
}

function drawCustomerApplies(data) {
    applyDynamical = "_customer_apply";
    var postFun;
    switch (applyInviteStatus) {
        case "allCustomer":
            postFun = drawCustomerAllApplies;
            break;
        case "handledCustomer":
            postFun = drawCustomerHandledApplies;
            break;
        default:
            postFun = drawCustomerPendingApplies;
            break;
    }
    postFun(data);
}
function drawCustomerAllApplies(data) {
    $("#deleteHandledApply").show();
    $("#customerApplies > div").remove();
    var title = '<div class="tabTitle">' +
        '<div class="divChk" style="padding-left:10px;width: 50px;"><label class="chkbox"><input type="checkbox" id="selectAllCustomerApplies">全选</label></div>' +
        '<div style="width:60px;padding-left:10px;">请求人</div><div>所在地</div><div>经营产品</div><div>请求时间</div><div style="text-align:center;">操作</div><div>操作人/操作时间</div></div>';
    $("#customerApplies").append(title);
    if (data["results"].length > 0) {
        $.each(data["results"], function (index, result) {
            var shopRelationInviteId = result["idStr"],
                originShopName = result["originShopName"],
                originAddress = result["originAddress"],
                originBusinessScope = result["originBusinessScope"] ? result["originBusinessScope"] : "",
                originShopId = result["originShopIdStr"],
                originUserId = result["originUserIdStr"],
                inviteTime = result["inviteTime"],
                invitedShopId = result["invitedShopIdStr"],
                inviteType = result["inviteType"],
                customerId = result["customerIdStr"],
                status = result["status"],
                operationMan = result["operationMan"],
                operationManId = result["operationManIdStr"],
                operationTime = result["operationTime"],
                refuseMsg = result["refuseMsg"];
//            if (!shopRelationInviteId) return true;
            var div = '<div class="lineDiv">';
            if (status != 'PENDING') {
                div += '<div class="chkbox">';
                div += '<input type="checkbox" data-invite-type="CUSTOMER_INVITE"  shopRelationInviteId="' + shopRelationInviteId + '"/>';
                if (status == 'ACCEPTED') {
                    div += '<a title="' + originShopName + '" data-customer-id="' + customerId + '" class="showCustomerBtn blue_color">' + originShopName + '</a>';
                } else {
                    div += '<a title="' + originShopName + '" style="color:#000000">' + originShopName + '</a>';
                }
                div += '</div>';
            } else {
                div += '<div title="' + originShopName + '">' + originShopName + '</div>';
            }
          div += '<div title="' + originAddress + '">' + originAddress + '</div>';
          div += '<div title="' + originBusinessScope + '">' + originBusinessScope + '&nbsp</div>';
            div += '<div style="width:110px;">' + dateUtil.formatDate(new Date(inviteTime), dateUtil.dateStringFormatDayHourMin) + '</div>';
            div += '<div class="btn_agree" style="text-align:center;">';
            if (status == 'PENDING') {
                div += '<input type="button" class="supplier_accept" applyInviteStatus="allCustomer" originShopName="' + originShopName + '" inviteId="' + shopRelationInviteId + '" value="同&nbsp;意"/>';
                div += '<input type="button" class="refuseApply" applyInviteStatus="allCustomer"  originShopName="' + originShopName + '" inviteId="' + shopRelationInviteId + '" value="拒&nbsp;绝"/>';
            } else {
                div += status == 'ACCEPTED' ? "已接受" : "已拒绝";
            }
            div += '</div>';
            if (status == 'PENDING') {
                div += '<div style="width:100px;text-align:center;">--</div>';
            } else {
                var content = operationMan + ' ' + dateUtil.formatDate(new Date(operationTime), dateUtil.dateStringFormatDayHourMin);
                div += '<div style="width:100px;" title="' + content + '">' + content + '</div>';
            }
            div += '</div>';
            $("#customerApplies").append(div);
        });
    } else {
        $("#customerApplies").append('<div class="lineDiv" style="text-align: center">暂无待处理关联请求！</div>');
    }
}
function drawCustomerPendingApplies(data) {
    $("#deleteHandledApply").hide();
    $("#customerApplies > div").remove();
    var title = '<div class="tabTitle">' +
        '<div style="padding-left:10px;width: 150px;">请求人</div><div style="width: 150px;">所在地</div><div style="width: 150px;">经营产品</div><div style="width: 150px;">请求时间</div><div style="width: 150px;">操作</div></div>';
    $("#customerApplies").append(title);
    if (data["results"].length > 0) {
        $.each(data["results"], function (index, result) {
            var shopRelationInviteId = result["idStr"],
                originShopName = result["originShopName"],
                originAddress = result["originAddress"],
                originBusinessScope = result["originBusinessScope"],
                originShopId = result["originShopIdStr"],
                originUserId = result["originUserIdStr"],
                inviteTime = result["inviteTime"],
                invitedShopId = result["invitedShopIdStr"],
                inviteType = result["inviteType"],
                status = result["status"],
                operationMan = result["operationMan"],
                operationManId = result["operationManIdStr"],
                operationTime = result["operationTime"],
                refuseMsg = result["refuseMsg"];
            var div = '<div class="lineDiv">';
            div += '<div title="' + originShopName + '" style="width: 150px;">' + originShopName + '</div>';
            div += '<div title="'+ originAddress +'" style="width: 150px;">' + originAddress + '</div>';
            div += '<div style="width: 150px;" title="' + originBusinessScope + '">' + originBusinessScope + '&nbsp</div>';
            div += '<div style="width: 150px;">' + dateUtil.formatDate(new Date(inviteTime), dateUtil.dateStringFormatDayHourMin) + '</div>';
            div += '<div class="btn_agree" style="width: 150px;">';
            div += '<input type="button" class="supplier_accept" applyInviteStatus="pendingCustomer" originShopName="' + originShopName + '"  inviteId="' + shopRelationInviteId + '" value="同&nbsp;意"/>';
            div += '<input type="button" class="refuseApply" applyInviteStatus="pendingCustomer" originShopName="' + originShopName + '"  inviteId="' + shopRelationInviteId + '" value="拒&nbsp;绝"/></div>';
            div += '</div>';
            $("#customerApplies").append(div);
        });
        userGuide.invoker("CUSTOMER_APPLY_GUIDE_SHOW_APPLY");
    } else {
        $("#customerApplies").append('<div class="lineDiv" style="text-align: center">暂无待处理关联请求！</div>');
    }
}
function drawCustomerHandledApplies(data) {
    $("#deleteHandledApply").show();
    $("#customerApplies > div").remove();
    var title = '<div class="tabTitle">' +
        '<div class="divChk" style="padding-left:10px;width: 50px;"><label class="chkbox"><input type="checkbox" id="selectAllCustomerApplies">全选</label></div>' +
        '<div style="padding-left:10px;width: 70px;">请求人</div><div>所在地</div><div>经营产品</div><div>请求时间</div><div style="width:80px;">操作状态</div><div style="width:60px;">操作人</div><div style="width:100px;">操作时间</div></div>';
    $("#customerApplies").append(title);
    if (data["results"].length > 0) {
        $.each(data["results"], function (index, result) {
            var shopRelationInviteId = result["idStr"],
                originShopName = result["originShopName"],
                originAddress = result["originAddress"],
                originBusinessScope = result["originBusinessScope"],
                originShopId = result["originShopIdStr"],
                originUserId = result["originUserIdStr"],
                inviteTime = result["inviteTime"],
                invitedShopId = result["invitedShopIdStr"],
                customerId = result["customerIdStr"],
                inviteType = result["inviteType"],
                status = result["status"],
                operationMan = result["operationMan"],
                operationManId = result["operationManIdStr"],
                operationTime = result["operationTime"],
                refuseMsg = result["refuseMsg"];
            var div = '<div class="lineDiv '+status+'">';
            div += '<div class="chkbox">';
            div += '<input type="checkbox" data-invite-type="CUSTOMER_INVITE" shopRelationInviteId="' + shopRelationInviteId + '"/>';
            if (status == 'ACCEPTED') {
                div += '<a title="' + originShopName + '" data-customer-id="' + customerId + '" class="showCustomerBtn blue_color">' + originShopName + '</a>';
            } else {
                div += '<a title="' + originShopName + '" style="color:#000000">' + originShopName + '</a>';
            }
            div += '</div>';
            div += '<div title="' + originAddress + '">' + originAddress + '</div>';
            div += '<div title="' + originBusinessScope + '">' + originBusinessScope + '&nbsp</div>';
            div += '<div style="width:110px;">' + dateUtil.formatDate(new Date(inviteTime), dateUtil.dateStringFormatDayHourMin) + '</div>';
            div += '<div style="width:80px;text-align:center;">' + (status == 'ACCEPTED' ? "已接受" : "已拒绝") + '</div>';
            div += '<div style="width:60px;">' + operationMan + '</div>';
            div += '<div style="width:110px;">' + dateUtil.formatDate(new Date(operationTime), dateUtil.dateStringFormatDayHourMin) + '</div>';
            div += '</div>';
            $("#customerApplies").append(div);
        });
        userGuide.caller("CUSTOMER_APPLY_GUIDE_SHOW_HANDLED_APPLY_LIST", "", "", "CUSTOMER_APPLY_GUIDE_SHOW_HANDLED_APPLY");
    } else {
        $("#customerApplies").append('<div class="lineDiv" style="text-align: center">暂无待处理关联请求！</div>');
    }
}

function drawSupplierApplies(data) {
    applyDynamical = "_supplier_apply";
    var postFun;
    switch (applyInviteStatus) {
        case "allSupplier":
            postFun = drawSupplierAllApplies;
            break;
        case "handledSupplier":
            postFun = drawSupplierHandledApplies;
            break;
        default:
            postFun = drawSupplierPendingApplies;
            break;
    }
    postFun(data);
}
function drawSupplierAllApplies(data) {
    $("#deleteHandledApply").show();
    $("#supplierApplies > div").remove();
    var title = '<div class="tabTitle">' +
        '<div class="divChk" style="padding-left:10px;width: 50px;"><label class="chkbox"><input type="checkbox" id="selectAllSupplierApplies">全选</label></div>' +
        '<div style="padding-left:10px;width: 60px;">请求人</div><div>所在地</div><div>经营产品</div><div>请求时间</div><div style="text-align:center;">操作</div><div>操作人/操作时间</div></div>';
    $("#supplierApplies").append(title);
    if (data["results"].length > 0) {
        $.each(data["results"], function (index, result) {
            var shopRelationInviteId = result["idStr"],
                originShopName = result["originShopName"],
                originAddress = result["originAddress"],
                originBusinessScope = result["originBusinessScope"] ? result["originBusinessScope"] : "",
                originShopId = result["originShopIdStr"],
                originUserId = result["originUserIdStr"],
                inviteTime = result["inviteTime"],
                invitedShopId = result["invitedShopIdStr"],
                inviteType = result["inviteType"],
                supplierId = result["supplierIdStr"],
                status = result["status"],
                operationMan = result["operationMan"],
                operationManId = result["operationManIdStr"],
                operationTime = result["operationTime"],
                refuseMsg = result["refuseMsg"];
//            if (!shopRelationInviteId) return true;
            var div = '<div class="lineDiv">';
            if (status != 'PENDING') {
                div += '<div class="chkbox">';
                div += '<input type="checkbox" data-invite-type="SUPPLIER_INVITE" shopRelationInviteId="' + shopRelationInviteId + '"/>';
                if (status == 'ACCEPTED') {
                    div += '<a title="' + originShopName + '" data-supplier-id="' + supplierId + '" class="showSupplierBtn blue_color">' + originShopName + '</a>';
                } else {
                    div += '<a title="' + originShopName + '" style="color:#000000">' + originShopName + '</a>';
                }
                div += '</div>';

            } else {
                div += '<div title="' + originShopName + '">' + originShopName + '</div>';
            }
            div += '<div title="' + originAddress + '">' + originAddress + '</div>';
            div += '<div title="' + originBusinessScope + '">' + originBusinessScope + '&nbsp</div>';
            div += '<div style="width:110px;">' + dateUtil.formatDate(new Date(inviteTime), dateUtil.dateStringFormatDayHourMin) + '</div>';
            div += '<div class="btn_agree" style="text-align:center;">';
            if (status == 'PENDING') {
                div += '<input type="button" class="customer_accept" applyInviteStatus="allSupplier" originShopName="' + originShopName + '"  inviteId="' + shopRelationInviteId + '" value="同&nbsp;意"/>';
                div += '<input type="button" class="refuseApply" applyInviteStatus="allSupplier" originShopName="' + originShopName + '"  inviteId="' + shopRelationInviteId + '" value="拒&nbsp;绝"/>';
            } else {
                div += status == 'ACCEPTED' ? "已接受" : "已拒绝";
            }
            div += '</div>';
            if (status == 'PENDING') {
                div += '<div style="width:100px;text-align:center;">--</div>';
            } else {
                var content = operationMan + ' ' + dateUtil.formatDate(new Date(operationTime), dateUtil.dateStringFormatDayHourMin);
                div += '<div style="width:100px;" title="' + content + '">' + content + '</div>';
            }
            div += '</div>';
            $("#supplierApplies").append(div);
        });
    } else {
        $("#supplierApplies").append('<div class="lineDiv" style="text-align: center;">暂无待处理关联请求！</div>');
    }
}
function drawSupplierPendingApplies(data) {
    $("#deleteHandledApply").hide();
    $("#supplierApplies > div").remove();
    var title = '<div class="tabTitle"><div style="padding-left:10px;width: 150px;">请求人</div><div style="width: 150px;">所在地</div><div style="width: 150px;">经营产品</div><div style="width: 150px;">请求时间</div><div style="width: 150px;">操作</div></div>';
    $("#supplierApplies").append(title);
    if (data["results"].length > 0) {
        $.each(data["results"], function (index, result) {
            var shopRelationInviteId = result["idStr"],
                originShopName = result["originShopName"],
                originAddress = result["originAddress"],
                originBusinessScope = result["originBusinessScope"],
                originShopId = result["originShopIdStr"],
                originUserId = result["originUserIdStr"],
                inviteTime = result["inviteTime"],
                invitedShopId = result["invitedShopIdStr"],
                inviteType = result["inviteType"],
                status = result["status"],
                operationMan = result["operationMan"],
                operationManId = result["operationManIdStr"],
                operationTime = result["operationTime"],
                refuseMsg = result["refuseMsg"];
            var div = '<div class="lineDiv">';
            div += '<div title="' + originShopName + '" style="width: 150px;">' + originShopName + '</div>';
            div += '<div title="'+ originAddress +'" style="width: 150px;">' + originAddress + '</div>';
            div += '<div style="width: 150px;" title="' + originBusinessScope + '">' + originBusinessScope + '&nbsp</div>';
            div += '<div style="width: 150px;">' + dateUtil.formatDate(new Date(inviteTime), dateUtil.dateStringFormatDayHourMin) + '</div>';
            div += '<div class="btn_agree" style="width: 150px;">';
            div += '<input type="button" class="customer_accept" applyInviteStatus="pendingSupplier" originShopName="' + originShopName + '"  inviteId="' + shopRelationInviteId + '" value="同&nbsp;意"/>';
            div += '<input type="button" class="refuseApply" applyInviteStatus="pendingSupplier" originShopName="' + originShopName + '"  inviteId="' + shopRelationInviteId + '" value="拒&nbsp;绝"/></div>';
            div += '</div>';
            $("#supplierApplies").append(div);
        });
        userGuide.invoker("SUPPLIER_APPLY_GUIDE_SHOW_APPLY");
    } else {
        $("#supplierApplies").append('<div class="lineDiv" style="text-align: center">暂无待处理关联请求！</div>');
    }
}
function drawSupplierHandledApplies(data) {
    $("#deleteHandledApply").show();
    $("#supplierApplies > div").remove();
    var title = '<div class="tabTitle">' +
        '<div class="divChk" style="padding-left:10px;width: 50px;"><label class="chkbox"><input type="checkbox" id="selectAllSupplierApplies">全选</label></div>' +
        '<div style="padding-left:10px;width: 60px;">请求人</div><div>所在地</div><div>经营产品</div><div>请求时间</div><div style="width:80px;">操作状态</div><div style="width:60px;">操作人</div><div style="width:100px;">操作时间</div></div>';
    $("#supplierApplies").append(title);
    if (data["results"].length > 0) {
        $.each(data["results"], function (index, result) {
            var shopRelationInviteId = result["idStr"],
                originShopName = result["originShopName"],
                originAddress = result["originAddress"],
                originBusinessScope = result["originBusinessScope"],
                originShopId = result["originShopIdStr"],
                originUserId = result["originUserIdStr"],
                inviteTime = result["inviteTime"],
                invitedShopId = result["invitedShopIdStr"],
                inviteType = result["inviteType"],
                supplierId = result["supplierIdStr"],
                status = result["status"],
                operationMan = result["operationMan"],
                operationManId = result["operationManIdStr"],
                operationTime = result["operationTime"],
                refuseMsg = result["refuseMsg"];
            var div = '<div class="lineDiv '+status+'">';
            div += '<div class="chkbox">';
            div += '<input type="checkbox" data-invite-type="SUPPLIER_INVITE" shopRelationInviteId="' + shopRelationInviteId + '"/>';
            if (status == 'ACCEPTED') {
                div += '<a title="' + originShopName + '" data-supplier-id="' + supplierId + '" class="showSupplierBtn blue_color">' + originShopName + '</a>';
            } else {
                div += '<a title="' + originShopName + '" style="color:#000000">' + originShopName + '</a>';
            }
            div += '</div>';
            div += '<div title="'+ originAddress +'">' + originAddress + '</div>';
            div += '<div title="' + originBusinessScope + '">' + originBusinessScope + '&nbsp</div>';
            div += '<div style="width:110px;">' + dateUtil.formatDate(new Date(inviteTime), dateUtil.dateStringFormatDayHourMin) + '</div>';
            div += '<div style="width:80px;text-align:center;">' + (status == 'ACCEPTED' ? "已接受" : "已拒绝") + '</div>';
            div += '<div style="width:60px;">' + operationMan + '</div>';
            div += '<div style="width:110px;">' + dateUtil.formatDate(new Date(operationTime), dateUtil.dateStringFormatDayHourMin) + '</div>';
            div += '</div>';
            $("#supplierApplies").append(div);
        });
        userGuide.caller("SUPPLIER_APPLY_GUIDE_SHOW_HANDLED_APPLY_LIST", "", "", "SUPPLIER_APPLY_GUIDE_SHOW_HANDLED_APPLY");
    } else {
        $("#supplierApplies").append('<div class="lineDiv" style="text-align: center">暂无待处理关联请求！</div>');
    }
}
