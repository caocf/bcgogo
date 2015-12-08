/**
 * 客户搜索
 * @author zhangjuntao
 */
var ajaxDataTemp = null;//用于保存上次查询条件
var bcgogoAjaxQuery = APP_BCGOGO.Module.wjl.ajaxQuery;
$(function() {
    var customerInvitationCodeFlash;
    if (APP_BCGOGO.Permission.Version.RelationCustomer) {
        checkCustomerWithoutSendInvitationCodeSms();
    }

    function checkCustomerWithoutSendInvitationCodeSms() {
        APP_BCGOGO.Net.asyncAjax({
            type: "POST",
            url: "invitationCodeSms.do?method=checkCustomerOrSupplierWithoutSendInvitationCodeSms",
            data: { customerOrSupplier: "CUSTOMER"},
            cache: false,
            dataType: "json",
            success: function (result) {
                var $sentInvitationCodePromotionalSms = $("#sentInvitationCodePromotionalSms");
                if (result["success"] && result["total"] > 0) {
                    $("#sentInvitationCodePromotionalSms").css("display", "block");
                } else {
                    $("#sentInvitationCodePromotionalSms").css("display", "none");
                }
            }
        });
    }

    var currentColor;
    $(".i_mainRight .table2 a").live("mouseover", function() {
        currentColor = $(this).css("color");
        $(this).css({"color":"#fd5300","textDecoration":"underline"});
    });
    $(".i_mainRight .table2 a").live("mouseout", function() {
        $(this).css({"color":currentColor,"textDecoration":"none"});
    });
    $("#selectAll").live("click", function() {
        if ($(this).attr("checked")) {
            $("#customerDataTable input[type='checkbox']").attr("checked", true);
        } else {
            $("#customerDataTable input[type='checkbox']").attr("checked", false);
        }
        isTodayAdd = false;
    });

    $("#customerDataTable input[name='selectCustomer']").live("click", function() {
        if (!$(this).attr("checked")) {
            $("#selectAll").attr("checked", false);
        } else {
            $("#customerDataTable input[name='selectCustomer']").each(function(index, box) {
                if (!$(box).attr("checked")) {
                    return false;
                }
                if (index == $("#customerDataTable input[name='selectCustomer']").length - 1) {
                    $("#selectAll").attr("checked", true);
                }
            });
        }
    });

    //更多卡
    $("#showMoreCard").click(function(event) {
        if ($(event.target).attr("details") == "true") {
            $(".moreCardName").hide();
            $(event.target).css("background", "url('images/rightArrow.png') no-repeat right");
            $(event.target).attr("details", "false");
        } else {
            $(".moreCardName").show();
            $(event.target).css("background", "url('images/rightTop.png') no-repeat right");
            $(event.target).attr("details", "true");
        }
    });

    $("#customerInfoText").blur(function() {
        $("#filterType").val('');
    });
    //搜索
    $("#customerSearchBtn").click(function() {
        isTodayAdd = false;
        $("#rowStart").val(0);     //  todo fenye  customerSuggest
        searchCustomerDataAction();
        $("#hasDebt").val("");
    });
    //按会员类型选择
    $("#memberTypes>a").click(function(e) {
        $("#filterType").val('');
        var type;
        if ($(e.target).hasClass("cusSure")) {
            type = $("#memberTypes>input").val();
            if (!type || type.length == 0) return;
        } else {
            type = $(e.target).html();
        }
        $("#memberTypeCondition").remove();
        var $typeSpan = $('<span>' + "会员类型:" + type + '</span>');
        var $image = $('<img src="images/cus_close.png"/>');
        $image.click(function(e) {
            $("#filterType").val('');
            $(e.target).parent().remove();
            if ($("#conditions").children().length == 0) {
                $("#conditions").hide();
            }
            $("#memberTypes").show();
            $("#customerSearchBtn").click();
        });
        var $condition = $('<div id="memberTypeCondition" value="' + type + '"></div>');
        $condition.addClass("btnMenber").css({width:(getByteLen(type)) * 6.5 + 80 + "px"});
        $condition.append($typeSpan).append($image);
        $("#conditions").append($condition).show();
        $("#customerSearchBtn").click();
        $("#memberTypes").hide();
    });
    //按累计金额选择
    $("#totalAmount>a").click(function(e) {
        $("#filterType").val('');
        //处理 数字逻辑
        var start ,end;
        var amountArea = "";
        if ($(e.target).hasClass("cusSure")) {
            start = $("#totalAmount>#totalAmountStart").val();
            end = $("#totalAmount>#totalAmountEnd").val();
            if ((!start && !end) || (start.length == 0 && end.length == 0)) return;
            if ((start && end) && (start.length != 0 && end.length != 0) && Number(start) > Number(end)) {
                var temp = start;
                start = end;
                end = temp;
            }
            amountArea = start + "~" + end;
        } else {
            amountArea = $(e.target).html();
        }
        $("#totalAmount").hide();
        var $typeSpan = $('<span>' + "累计金额:" + amountArea + '</span>');
        var $image = $('<img src="images/cus_close.png"/>');
        $image.click(function(e) {
            $("#filterType").val('');
            $(e.target).parent().remove();
            if ($("#conditions").children().length == 0) {
                $("#conditions").hide();
            }
            $("#totalAmount").show();
            $("#customerSearchBtn").click();
        });
        var $condition = $('<div id="totalAmountCondition" value="' + amountArea + '"></div>');
        $condition.addClass("btnMenber").css({width:(getByteLen(amountArea)) * 8 + 70 + "px"});
        $condition.append($typeSpan).append($image);
        $("#conditions").append($condition).show();
        $("#customerSearchBtn").click();
        $("#totalAmount").hide();
    });
    //按欠款金额选择
    $("#totalDebt>a").click(function(e) {
        $("#filterType").val('');
        //处理 数字逻辑
        var start ,end;
        var amountArea = "";
        if ($(e.target).hasClass("cusSure")) {
            start = $("#totalDebt>#totalDebtStart").val();
            end = $("#totalDebt>#totalDebtEnd").val();
            if ((!start && !end) || (start.length == 0 && end.length == 0)) return;
            if ((start && end) && (start.length != 0 && end.length != 0) && Number(start) > Number(end)) {
                var temp = start;
                start = end;
                end = temp;
            }
            amountArea = start + "~" + end;
        } else {
            amountArea = $(e.target).html();
        }
        $("#totalDebt").hide();
        var $typeSpan = $('<span>' + "欠款金额:" + amountArea + '</span>');
        var $image = $('<img src="images/cus_close.png"/>');
        $image.click(function(e) {
            $("#filterType").val('');
            $(e.target).parent().remove();
            if ($("#conditions").children().length == 0) {
                $("#conditions").hide();
            }
            $("#totalDebt").show();
            $("#customerSearchBtn").click();
        });
        var $condition = $('<div id="totalDebtCondition" value="' + amountArea + '"></div>');
        $condition.addClass("btnMenber").css({width:(getByteLen(amountArea)) * 8 + 85 + "px"});
        $condition.append($typeSpan).append($image);
        $("#conditions").append($condition).show();
        $("#customerSearchBtn").click();
        $("#totalDebt").hide();
    });

    $("#lastExpenseTime>a").click(function(e) {
        $("#filterType").val('');
        //处理 数字逻辑
        var start ,end;
        var lastExpenseTime = "";
        if ($(e.target).hasClass("cusSure")) {
            start = $("#lastExpenseTime>#lastExpenseTimeStart").val();
            end = $("#lastExpenseTime>#lastExpenseTimeEnd").val();
            if ((!start && !end) || (start.length == 0 && end.length == 0)) return;
            if ((start && end) && (start.length != 0 && end.length != 0) && Number(start) > Number(end)) {
                var temp = start;
                start = end;
                end = temp;
            }
            lastExpenseTime = start + "~" + end;
        } else {
            lastExpenseTime = $(e.target).html();
        }
        $("#lastExpenseTime").hide();
        var $typeSpan = $('<span>' + "消费时间:" + lastExpenseTime + '</span>');
        var $image = $('<img src="images/cus_close.png"/>');
        $image.click(function(e) {
            $("#filterType").val('');
            $(e.target).parent().remove();
            if ($("#conditions").children().length == 0) {
                $("#conditions").hide();
            }
            $("#lastExpenseTime").show();
            $("#customerSearchBtn").click();
        });
        var $condition = $('<div id="lastExpenseTimeCondition" value="' + lastExpenseTime + '"></div>');
        $condition.addClass("btnMenber").css({width:(getByteLen(lastExpenseTime)) * 8 + 60 + "px"});
        $condition.append($typeSpan).append($image);
        $("#conditions").append($condition).show();
        $("#customerSearchBtn").click();
        $("#lastExpenseTime").hide();
    });


    $("#memberNumSpan").click(function() {
        $("#hasDebt").val("");
        $("#hasDeposit").val("");
        searchCustomerDataActionFilter("memberNum");
    });
    $("#totalNumSpan").click(function() {
        $("#hasDebt").val("");
        $("#hasDeposit").val("");
        searchCustomerDataActionFilter("totalNum");
    });
    $("#todayCustomerSpan").click(function() {
        $("#hasDebt").val("");
        $("#hasDeposit").val("");
        searchCustomerDataActionFilter("todayCustomer");
    });
    $("#mobileNumSpan").click(function() {
        $("#hasDebt").val("");
        $("#hasDeposit").val("");
        searchCustomerDataActionFilter("mobileNum");
    });
    $("#totalOBDSpan").click(function () {
        $("#hasDebt").val("");
        $("#hasDeposit").val("");
        searchCustomerDataActionFilter("totalOBD");
    });
    $("#totalAppSpan").click(function () {
      $("#hasDebt").val("");
      $("#hasDeposit").val("");
      searchCustomerDataActionFilter("totalApp");
    });

    $("#relatedNumSpan").click(function() {
        $("#hasDebt").val("");
        $("#hasDeposit").val("");
        searchCustomerDataActionFilter("relatedNum");
    });
    $("#debtCustomerCountSpan").click(function() {
        $("#hasDebt").val("true");
        $("#hasDeposit").val("");
        searchCustomerDataActionFilter('');
        $("#hasDebt").val("");
    });
    $("#totalDepositStatSpan").click(function() {
        $("#hasDeposit").val("true");
        $("#hasDebt").val("");
        searchCustomerDataActionFilter('');
        $("#hasDeposit").val("");
    });

    $("#totalDebtStatSpan").click(function() {
        $("#hasDebt").val("true");
        $("#hasDeposit").val("");
        searchCustomerDataActionFilter('');
        $("#hasDebt").val("");
    });

    $("#totalReturnDebtSpan").click(function () {
        $("#hasReturnDebt").val("true");
        $("#hasDeposit").val("");
        searchCustomerDataActionFilter('');
        $("#hasReturnDebt").val("");
    });

    $("#totalBalanceSpan").click(function () {
        $("#hasBalance").val("true");
        searchCustomerDataActionFilter('');
        $("#hasBalance").val("");
    });

    $("#totalConsumptionSpan").click(function () {
        $("#hasTotalConsumption").val("true");
        searchCustomerDataActionFilter('');
        $("#hasTotalConsumption").val("");
    });

    $("#totalAmountStart,#totalAmountEnd,#totalDebtStart,#totalDebtEnd,#lastExpenseTime")
        .keyup(function(e) {
            var $txt = $(e.target);
            $txt.val(APP_BCGOGO.StringFilter.inputtingPriceFilter($txt.val()));
        })
        .blur(function(e) {
            var $txt = $(e.target);
            $txt.val(APP_BCGOGO.StringFilter.priceFilter($txt.val()));
        });

    $("#lastExpenseTimeStart,#lastExpenseTimeEnd")
        .datepicker({
            "numberOfMonths":1,
            "showButtonPanel":true,
            "changeYear":true,
            "changeMonth":true,
            "constrainInput":false,
            "yearRange":"c-100:c+100",
            "yearSuffix":""
        })
        .bind("click", function() {
            $(this).blur();
        })
        .change(function() {
            var startDate = $("#lastExpenseTimeStart").val();
            var endDate = $("#lastExpenseTimeEnd").val();
            if (!endDate || !startDate) {
                return;
            }
            if (Number(startDate.replace(/\-/g, "")) > Number(endDate.replace(/\-/g, ""))) {
                $("#lastExpenseTimeEnd").val(startDate);
                $("#lastExpenseTimeStart").val(endDate);
            }
        });

    $("#sendMulSms").click(function(e) {
        var $mobileNum = $("#mobileNum")
        if (Number($mobileNum.html()) <= 0)return;
        if (Number($mobileNum.html()) > 1000) {
            if (!confirm("您发送的号码数超过1000个,确认发送？")) {
                return;
            }
        }
        var ajaxData = beforeSearchCustomer();
        var condition = (ajaxData.searchWord ? ("&searchWord=" + ajaxData.searchWord) : "") +
            (ajaxData.lastExpenseTimeStart ? ("&lastExpenseTimeStart=" + ajaxData.lastExpenseTimeStart) : "") +
            (ajaxData.lastExpenseTimeEnd ? ("&lastExpenseTimeEnd=" + ajaxData.lastExpenseTimeEnd) : "") +
            (ajaxData.totalDebt ? ("&totalDebt=" + ajaxData.totalDebt) : "") +
            (ajaxData.name ? ( "&name=" + ajaxData.name) : "") +
            (ajaxData.contact ? ("&contact=" + ajaxData.contact) : "") +
            (ajaxData.mobile ? ("&mobile=" + ajaxData.mobile) : "") +
            "&customerOrSupplier=customer" +
            (ajaxData.hasDebt ? ("&hasDebt=" + ajaxData.hasDebt) : "") +
            (ajaxData.totalDebtUp ? ("&totalDebtUp=" + ajaxData.totalDebtUp) : "") +
            (ajaxData.totalDebtDown ? ("&totalDebtDown=" + ajaxData.totalDebtDown) : "") +
            (ajaxData.totalPayableUp ? ("&totalPayableUp=" + ajaxData.totalPayableUp) : "") +
            (ajaxData.totalPayableDown ? ("&totalPayableDown=" + ajaxData.totalPayableDown) : "") +
            (ajaxData.totalAmountUp ? ("&totalAmountUp=" + ajaxData.totalAmountUp) : "") +
            (ajaxData.totalAmountDown ? ("&totalAmountDown=" + ajaxData.totalAmountDown) : "") +
            (ajaxData.filterType ? ("&filterType=" + ajaxData.filterType) : "") +
            (ajaxData.productBrand ? ("&productBrand=" + ajaxData.productBrand) : "") +
            (ajaxData.productModel ? ("&productModel=" + ajaxData.productModel) : "") +
            (ajaxData.productName ? ("&productName=" + ajaxData.productName) : "") +
            (ajaxData.productSpec ? ("&productSpec=" + ajaxData.productSpec) : "") +
            (ajaxData.productVehicleBrand ? ("&productVehicleBrand=" + ajaxData.productVehicleBrand) : "") +
            (ajaxData.productVehicleModel ? ("&productVehicleModel=" + ajaxData.productVehicleModel) : "") +
            (ajaxData.vehicleBrand ? ("&vehicleBrand=" + ajaxData.vehicleBrand) : "") +
            (ajaxData.vehicleColor ? ("&vehicleColor=" + ajaxData.vehicleColor) : "") +
            (ajaxData.vehicleModel ? ("&filterType=" + ajaxData.vehicleModel) : "") +
            (ajaxData.memberType ? ( "&memberType=" + ajaxData.memberType) : "")
        window.location = "customer.do?method=sendMsgBySearchCondition" + condition;
    });

    $('#sendMulSmsODB').click(function(){
        var  odbContactIdList = $('#sendMulSmsODB').data('odbContactIdList');
        odbContactIdList && G.Lang.isNotEmpty(odbContactIdList.join()) && (window.location.href = "sms.do?method=smswrite&contactIds=" + odbContactIdList.join());
    });

    $(".deleteCustomerClass > span").live("click", function() {
        var customerId = $(this).attr("customerId");
        var obj = this;

        if (!validateArrears(customerId)) {
            alert('该客户有欠款未结算不能删除！');
            return;
        }

        if (!validateRepair(customerId)) {
            var html = "";
            $.ajax({
                type:"POST",
                url:"txn.do?method=getRepairOrderReceiptNoOfNotSettled",
                async:false,
                data:{
                    customerId:customerId,
                    tsLog: 10000000000 * (1 + Math.random())
                },
                cache:false,
                dataType:"json",
                success:function(jsonObject) {
                    var resu = jsonObject.resu;
                    if (resu == "success") {
                        for (var i = 0,len = jsonObject.repair.length; i < len; i++) {
                            var url = "txn.do?method=getRepairOrder&menu-uid=VEHICLE_CONSTRUCTION_REPAIR&repairOrderId=" + jsonObject.repair[i].idStr;
                            html += "<a target='_blank' style='cursor: pointer;color: #6699CC;line-height:28px;'  href='" + url + "'>" + jsonObject.repair[i].receiptNo + "</a>&nbsp;&nbsp;&nbsp;&nbsp;"
                            if (i % 2 == 1) {
                                html += "<br/>";
                            }
                        }
                    }
                }
            });

            $("#deleteReceiptNo").html(html);

//        if("" != html)
//        {
            $("#deleteCustomer_dialog").dialog({
                resizable: false,
                title:"该客户还有未结算的单据，请结算完再删除!",
                height:150,
                width:330,
                modal: true,
                closeOnEscape: false,
                buttons:{
                    "确定":function() {
                        $("#deleteReceiptNo").html("");
                        $("#deleteCustomer_dialog").dialog("close");

                    }
                },
                close:function() {
                    $("#deleteReceiptNo").html("");
//                    $("#deleteCustomer_dialog").dialog("close");

                }
            });
//        }
//        alert("该客户还有未结算的单据，请结算完再删除!");
            return;
        }

        if (!validateMember(customerId)) {
            if (!confirm("该客户是会员且有储值金额，是否删除？")) {
                return;
            }
        }
        else {
            if (!confirm("请确认是否删除？")) {
                return;
            }
        }

        $.ajax({
            type:"POST",
            url:"customer.do?method=deleteCustomerAjax",
            async:false,
            data:{
                customerId:customerId,
                tsLog: 10000000000 * (1 + Math.random())
            },
            cache:false,
            dataType:"json",
            success:function(jsonObject) {
                var resu = jsonObject.resu;
                if (resu == "error") {
                    alert("删除失败!");
                }
                else {
                    $(obj).closest("tr").find("td").eq(1).find("a").eq(0)[0].href = "#";
                    $(obj).closest("tr").find("td").eq(10).find("a").eq(0)[0].href = "#";
                    $(obj).css("display", "none");
                    $(obj).css("display", "none");
                    $(obj).closest("tr").css("background-color", "#E5E5E5");
                    alert("删除成功!");
                }
            }
        });
    });
    $(".supplier_cancel_shop_relation").live("click", function () {
        if ($(this).attr("lock") || !$(this).attr("customerId")) {
            return;
        }
        $(this).attr("lock", true);
        var $cancelDom = $(this);
        var customerId = $(this).attr("customerId");
        var ajaxData = {customerId: $(this).attr("customerId")};
        var ajaxUrl = "apply.do?method=validateSupplierCancelCustomerShopRelation";
        bcgogoAjaxQuery.setUrlData(ajaxUrl, ajaxData);
        bcgogoAjaxQuery.ajaxQuery(function (result) {
            if (result.success) {
                $("#cancelShopRelationDialog").dialog({
                    resizable: false,
                    title: "取消关联",
                    height: 210,
                    width: 300,
                    modal: true,
                    closeOnEscape: false,
                    buttons: {
                        "确定": function () {
                            var refuseMsg = $("#cancel_msg").val();
                            var params = {customerId: $cancelDom.attr("customerId"), cancelMsg: (refuseMsg == "取消关联理由" ? "无" : refuseMsg)},
                                url = "apply.do?method=supplierCancelCustomerShopRelation";
                            APP_BCGOGO.Net.asyncAjax({
                                type: "POST",
                                url: url,
                                data: params,
                                cache: false,
                                dataType: "json",
                                success: function (result) {
                                    if (result.success) {
                                        nsDialog.jAlert("您已取消与对方的关联关系！", "", function () {
                                            $("#customerSearchBtn").click();
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
                        $cancelDom.removeAttr("lock");
                        $("#cancel_msg").removeClass("black_color").addClass("gray_color");
                        $("#cancel_msg").val($("#cancel_msg").attr("init_word"));
                    }
                });
            } else {
                nsDialog.jAlert(result.msg);
            }
            $cancelDom.removeAttr("lock");
        });
    });
    //推荐
    $(".updateCustomer").live("click", function (e) {
        var customerId = $(e.target).attr("data-customer-id");
        if (!customerId)return;
        if ($(this).attr("lock")) {
            return;
        }
        $(this).attr("lock", true);
        var $thisDom = $(this);
        bcgogoAjaxQuery.setUrlData("shop.do?method=validateShopRegBasicInfo", {'customerId':customerId});
        bcgogoAjaxQuery.ajaxQuery(function (result) {
            if (result.success) {
                window.location.href = "shopRegister.do?method=registerMain&registerType=SUPPLIER_REGISTER&customerId=" + customerId;
            } else {
                nsDialog.jAlert(result.msg);
            }
        }, function (result) {
            nsDialog.jAlert("网络异常！");
        });
        $thisDom.removeAttr("lock");
    });

    $("[pop-window-name='input-mobile']").dialog({
        autoOpen: false,
        resizable: false,
        title: "请输入手机号码：",
        height: 130,
        width: 250,
        modal: true,
        closeOnEscape: false,
        buttons: {
            "确定": function () {
                var $mobileInput = $("[pop-window-input-name='mobile']"),
                    customerId = $mobileInput.attr("data-customer-id") ,
                    callback = $mobileInput.attr("callback") ,
                    mobile = $mobileInput.val(),
                    me = this, data = {};
                if (!customerId) {
                    $(this).dialog("close");

                } else {
                    if (!APP_BCGOGO.Validator.stringIsMobilePhoneNumber(mobile)) {
                        nsDialog.jAlert("手机号码输入有误！请重新输入！");
                        return;
                    }
                    if (!(APP_BCGOGO.Net.syncGet({"url": "customer.do?method=getCustomerByMobile", data: {"mobile": mobile}, dataType: "json"}).length == 0)) {
                        nsDialog.jAlert("手机号码重复！请重新输入！");
                        return;
                    }
                    APP_BCGOGO.Net.asyncAjax({
                        type: "POST",
                        url: "customer.do?method=updateMobile",
                        data: {customerId: customerId, mobile: mobile},
                        cache: false,
                        async: true,
                        success: function () {
                            nsDialog.jAlert("手机号码保存成功！", "", function () {
                                $(me).dialog("close");
                                if (callback) {
                                    data['searchCustomer'] = true;
                                    data['customerId'] = customerId;
                                    eval(callback)(data);
                                }
                            });
                        }
                    });
                }
            },
            "取消": function () {
                $(this).dialog("close");
            }
        },
        close: function () {
            $("[pop-window-input-name='mobile']").val("")
                .removeAttr("data-customer-id").removeAttr("callback");
        }
    });

    function sentInvitationCodeSms(data) {
        var shopMoney = $("#smsBalance").html();
        if (!shopMoney) {
            nsDialog.jAlert("您的短信余额不足");
            return;
        }
        APP_BCGOGO.Net.asyncAjax({
            type: "POST",
            url: "invitationCodeSms.do?method=sentInvitationCodeSms",
            data: { id: data["customerId"], customerOrSupplier: "CUSTOMER"},
            cache: false,
            dataType: "json",
            success: function (result) {
                if (result.success) {
                    nsDialog.jAlert("已成功发送推荐短信！", "", function () {
                        if (data["searchCustomer"]) {
                            $("#customerSearchBtn").click();
                        }
                        checkCustomerWithoutSendInvitationCodeSms();
                    });
                } else {
                    nsDialog.jAlert(result.msg);
                }
            }
        });
    }

    //推荐
    $(".sentInvitationCodeSmsBtn").live("click", function (e) {
        var customerId = $(e.target).attr("data-customer-id");
        var mobile = $(e.target).attr("data-mobile"), data = {};
        if (!customerId)return;
        if (!APP_BCGOGO.Validator.stringIsMobilePhoneNumber(mobile)) {
            if (APP_BCGOGO.Permission.isMobileHidden) {
                nsDialog.jAlert("此客户无手机号，无法发送推荐短信。");
                return;
            }
            $("[pop-window-input-name='mobile']").attr("data-customer-id", customerId)
                .attr("callback", 'sentInvitationCodeSms');
            $("[pop-window-name='input-mobile']").dialog("open");
        } else {
            data['customerId'] = customerId;
            sentInvitationCodeSms(data);
        }
    });

    //一键推荐
    $("#sentInvitationCodePromotionalSms").bind("click", function (e) {
        var shopMoney = $("#smsBalance").html();
        if (!shopMoney) {
            nsDialog.jAlert("您的短信余额不足");
            return;
        }
        nsDialog.jConfirm("您是否确定发短信推荐你的客户使用一发软件?", "", function (returnVal) {
            if (returnVal) {
                APP_BCGOGO.Net.asyncAjax({
                    type: "POST",
                    url: "invitationCodeSms.do?method=sentInvitationCodePromotionalSms",
                    data: {customerOrSupplier: "CUSTOMER"},
                    cache: false,
                    dataType: "json",
                    success: function (result) {
                        if (result.success) {
                            nsDialog.jAlert("已成功发送推荐短信！", "", function() {
                                if (customerInvitationCodeFlash)clearInterval(customerInvitationCodeFlash);
                            });
                        }
                    }
                });
            }
        });
    });

    //应付应收款对帐单提示
    $(".pay").live("mouseover",
        function(event) {
            var _currentTarget = $("#payableReceivableAlert");
            _currentTarget.css({"top": GLOBAL.Display.getY(this) + 21, "left": GLOBAL.Display.getX(this)});
            _currentTarget.show();
        }).live("mouseout", function() {
            $("#payableReceivableAlert").hide();
        });
    $("#cancel_msg").bind("keydown",
        function () {
            if ($(this).hasClass("gray_color") && $(this).val() == $(this).attr("init_word")) {
                $(this).removeClass("gray_color").addClass("black_color").val("");
            }
        }).bind("blur", function () {
            if (!$(this).val()) {
                $(this).removeClass("black_color").addClass("gray_color");
                $(this).val($("#cancel_msg").attr("init_word"));
            }
        });

//  $(".hoverReminder").hover(function (event) {
////      var _currentTarget = $(event.target).parent().find(".alert");
//      var _currentTarget = $(".alert");
//      var offset = $(this).offset();
//      var height = $(this).css("height");
//      height = height.replace('px','')*1;
//      _currentTarget.css({"top":offset.top+height, "left": offset.left});
//      _currentTarget.show();
//      $(this).parent().mouseleave(function (event) {
//          event.stopImmediatePropagation();
//          if ($(event.relatedTarget).find(".alert")[0] != _currentTarget[0]) {
//              _currentTarget.hide();
//          }
//      });
//      //因为有2px的空隙,所以绑定在parent上.
////      _currentTarget.parent().mouseleave(function (event) {
////          event.stopImmediatePropagation();
////          if ($(event.relatedTarget).find(".alert")[0] != _currentTarget[0]) {
////              _currentTarget.hide();
////          }
////      });
//  }, function (event) {
////      var _currentTarget = $(".alert");
////      if ($(event.relatedTarget).parent()[0] != _currentTarget[0]) {
////          $(".alert").hide();
////      }
////      var _currentTarget = $(event.target).parent().find(".alert");
////      if ($(event.relatedTarget).find(".alert")[0] != _currentTarget[0]) {
////          $(event.target).parent().find(".alert").hide();
////      }
//  });
    $("#sentInvitationCodePromotionalSms").hover(function (event) {
        var _currentTarget = $("#multi_alert");
        var offset = $(this).offset();
        var height = $(this).css("height");
        height = height.replace('px', '') * 1;
        _currentTarget.css({"top": offset.top + height, "left": offset.left});
        _currentTarget.show();
    }, function (event) {
        event.stopImmediatePropagation();
        if ($(event.relatedTarget)[0] != $("#multi_alert")[0] && $(event.relatedTarget).parent()[0] != $("#multi_alert")[0] && $(event.relatedTarget).parent().parent()[0] != $("#multi_alert")[0]) {
            $("#multi_alert").hide();
        }
    });

    $("#multi_alert").mouseleave(function (event) {
        event.stopImmediatePropagation();
        if ($(event.relatedTarget).find(".alert")[0] != $("#sentInvitationCodePromotionalSms")) {
            $("#multi_alert").hide();
        }
    });

    $(".sentInvitationCodeSmsBtn").live("mouseover",
        function() {

            $("#single_alert").hide();

            var _currentTarget = $("#single_alert");
            var x = G.getX(this);
            var y = G.getY(this);
            var height = $(this).css("line-height");
            height = height.replace('px', '') * 1;
            _currentTarget.css({"top":y + height, "left": x});
            _currentTarget.show();

            $("#single_alert").mouseleave(function (event) {
                event.stopImmediatePropagation();
                if ($(event.relatedTarget).find(".alert")[0] != $(this)) {
                    $("#single_alert").hide();
                }
            });
        }).live("mouseout", function(event) {

            event.stopImmediatePropagation();
            if ($(event.relatedTarget)[0] != $("#single_alert")[0] && $(event.relatedTarget).parent()[0] != $("#single_alert")[0] && $(event.relatedTarget).parent().parent()[0] != $("#single_alert")[0]) {
                $("#single_alert").hide();
            }
        });


//  var $sentInvitationCodePromotionalSms = $(".hoverReminder"),
//        $sentInvitationCodePromotionalSmsInfo = $(".hoverReminder").parent().find(".tixing"),
//        $sentInvitationCodePromotionalSmsFather = $(".hoverReminder").parent();
//
//    $sentInvitationCodePromotionalSms.bind("mouseenter", function(){
//        $sentInvitationCodePromotionalSmsInfo.show();
//    });
//    $sentInvitationCodePromotionalSmsInfo.bind("mouseleave", function(){
//        $sentInvitationCodePromotionalSmsInfo.hide();
//    });
//    $sentInvitationCodePromotionalSmsFather.bind("mouseleave", function(){
//        $sentInvitationCodePromotionalSmsInfo.hide();
//    });


});


// myCustomerOrSupplier.js下拉选中以后　马上进行查询的回调钩子实现 暂时不可删除 否则
function doCustomerOrSupplierProductSearch() {
    $("#rowStart").val(0);     //  todo fenye  customerSuggest
    searchCustomerDataAction();
    $("#hasDebt").val("");
}

function searchCustomerDataAction() {
    var ajaxData;
    if ($("#resetSearchCondition").val() == 'true') {
        //重置搜索条件
        ajaxData = jQuery.parseJSON(lStorage.getItem(storageKey.SearchConditionKey));

        if (ajaxData) {
            resetSearchCondition(ajaxData);
        }
    } else {
        if ($("#sortStatus").val() == defaultSortStatus || $("#sortStatus").val() == "") {
            if (!$("#createdTimeSortSpan").hasClass("arrowDown")) {
                $("#createdTimeSortSpan").addClass("arrowDown").removeClass("arrowUp");
                $("#createdTimeSort").attr("currentSortStatus", "Desc");
            }
            $("#createdTimeSort").addClass("hover");
        }
    }
    ajaxData = beforeSearchCustomer();
    ajaxData.todayAdd = isTodayAdd;
    ajaxDataTemp = ajaxData;
    var resetStatNum = true;
    var ajaxUrl = "customer.do?method=searchCustomerDataAction";
    bcgogoAjaxQuery.setUrlData(ajaxUrl, ajaxData);
    //判断是否是点击会员，与全部选择所有会员卡相区别
    if ($("#memberRadio").hasClass("clicked")) {
        ajaxData.memberRadioClicked = true;
    }
    //放入LocalStorage,以便返回列表时使用
    lStorage.setItem(storageKey.SearchConditionKey, JSON.stringify(ajaxData));
    bcgogoAjaxQuery.ajaxQuery(function(json) {
        initCustomerDataTr(json, resetStatNum);
        if (isTodayAdd && json[0].licenceNoList) {
            var licenceNo = {};
            $.each(json[0].licenceNoList, function (i, n) {
                licenceNo[n] = true;
            });
            $('.J_vehicleDetailHighlight').each(function () {
                var text = $(this).text();
                var target = text.split(' ')[0];
                if (licenceNo[target]) {
                    text = text.replace(target, '<span class="red_color">' + target + '</span>');
                }
                $(this).html(text);
            });
        }

        initPages(json, "customerSuggest", ajaxUrl, '', "initCustomerDataTr", '', '', ajaxData, '');
        if ($.cookie("currentStepName") == "CONTRACT_MESSAGE_NOTICE_GUIDE_MESSAGE" && fromUserGuideStep
            && fromUserGuideStep == "CONTRACT_MESSAGE_NOTICE_CHECK_MESSAGE") {
            userGuide.caller("CONTRACT_MESSAGE_NOTICE_GUIDE_CUSTOMER_MESSAGE", "CONTRACT_MESSAGE_NOTICE");
        }
    });
}


function searchCustomerDataActionFilter(filter) {
    $("#rowStart").val(0);

    if ($("#sortStatus").val() == defaultSortStatus || $("#sortStatus").val() == "") {
        if (!$("#createdTimeSortSpan").hasClass("arrowDown")) {
            $("#createdTimeSortSpan").addClass("arrowDown").removeClass("arrowUp");
            $("#createdTimeSort").attr("currentSortStatus", "Desc");
        }
        $("#createdTimeSort").addClass("hover");
    }
    var ajaxData = {};
    cloneAll(ajaxDataTemp, ajaxData);

    if (ajaxData == null) {
        ajaxData = beforeSearchCustomer();
    }

    ajaxData.hasDebt = $("#hasDebt").val();
    ajaxData.hasDeposit = $("#hasDeposit").val();
    ajaxData.hasReturnDebt = $('#hasReturnDebt').val();
    ajaxData.hasBalance = $('#hasBalance').val();
    ajaxData.hasTotalConsumption = $('#hasTotalConsumption').val();

    if (ajaxData.memberType == "非会员" && filter == "memberNum") {
        $("#customerDataTable tr:not(:first)").remove();
        return;
    } else if (filter == "memberNum") {

        var memberType = "";

        if ($("#noMemberRadio").hasClass("clicked")) {
            memberType = "非会员";
        } else if ($("a[name='memberCardTypes'].clicked").length > 0) {
            $("a[name='memberCardTypes']").each(function () {
                if ($(this).hasClass("clicked")) {
                    memberType += $(this).attr("value") + ",";
                }
            });
        } else if ($("#memberRadio").hasClass("clicked")) {
            memberType = $("#allCardName").val();
        }

        memberType = memberType ? memberType : "";

        if (memberType == "") {
            memberType = $("#allCardName").val();
        }
        ajaxData.memberType = memberType;
        ajaxData.filterType = filter;
    } else {
        ajaxData.filterType = filter;
    }

    lStorage.setItem(storageKey.SearchConditionKey, JSON.stringify(ajaxData));
    var resetStatNum = false;
    var ajaxUrl = "customer.do?method=searchCustomerDataAction";
    bcgogoAjaxQuery.setUrlData(ajaxUrl, ajaxData);
    bcgogoAjaxQuery.ajaxQuery(function(json) {
        initCustomerDataTr(json, resetStatNum);
        initPages(json, "customerSuggest", ajaxUrl, '', "initCustomerDataTr", '', '', ajaxData, '');
    });
}


function initCustomerInfo(json) {
    var sortStr = $("#sortStr").val();
    var tr = '<colgroup><col width="50"><col ><col width="102"><col width="100"><col width="90"><col width="55"><col width="80"><col width="70">';
    tr += '<col width="168"><col width="58"><col width="70"><col width="80"></colgroup>';
    tr += '<tr class="divSlip titleBg">';
    tr += '<td style="padding-left:10px;"></td><td>客户名</td><td>联系人</td><td>手机</td><td>会员卡号</td><td>会员类型</td><td>储值余额</td>';
    tr += '<td><div class="fl">累计消费</div><input id="total_amount" sort="total_amount" class="ascending sort fl" type="button" onfocus="this.blur();"></td>';
    tr += '<td><div class="fl">应收</div><input id="total_debt" sort="total_debt" class="ascending sort fl" type="button" onfocus="this.blur();">' +
        '<span style="margin-left:50px">应付</span><input id="total_return_debt" sort="total_return_debt" class="ascending sort fl" type="button" onfocus="this.blur();">' +
        '</td>';
    tr += '<td>车辆数量</td><td>退货次数</td><td>退货金额</td></tr>';
    $("#customerDataTable").append($(tr));
    if (stringUtil.isNotEmpty(sortStr)) {
        var sortField = sortStr.split(",")[0];
        var sortType = sortStr.split(",")[1];
        if (sortType == "asc") {
            $("#" + sortField).addClass("ascending").removeClass("descending");
        } else {
            $("#" + sortField).addClass("descending").removeClass("ascending");
        }
    }
    if (!json.customerSuppliers) return;
    var customers = json.customerSuppliers;
    for (var i = 0; i < customers.length; i++) {
        var customer = customers[i];
        var memberNo = customer.memberNo ? customer.memberNo : "";
        var memberType = customer.memberType ? customer.memberType : "";
        var vehicleCount = customer.vehicleCount ? customer.vehicleCount : "";
        var licenceNo = customer.licenceNo ? customer.licenceNo : "";
        var member = customer.memberDTO;
        var balance = "";
        if (stringUtil.isNotEmpty(member)) {
            balance = APP_BCGOGO.StringFilter.priceFilter(member.balance);
        }
        var address;
        var customerShopId = customer.customerOrSupplierShopId || '';
        var customerId = customer.idStr;
        var lastBill = customer.lastBill ? customer.lastBill : "";
        var lastDateStr = customer.lastDateStr ? customer.lastDateStr : "";
        var totalReceivable = customer.totalDebt ? APP_BCGOGO.StringFilter.priceFilter(customer.totalDebt) : '0';
        var totalAmount = APP_BCGOGO.StringFilter.priceFilter(customer.totalAmount);
        var mobile = customer.mobile ? customer.mobile : "";
        var mobileTitle = customer.mobile ? customer.mobile : "";
        if (APP_BCGOGO.Permission["isMobileHidden"]) {
            mobile = mobile.substr(0, 3) + "****" + mobile.substr(7, 4);
        }
        var repayDateStr = customer.repayDateStr ? customer.repayDateStr : "";
        var name = customer.name ? customer.name : "";
        var contact = customer.contact ? customer.contact : "";
        var totalCounts = json.totalCounts;
        var countCustomerReturn = APP_BCGOGO.StringFilter.priceFilter(customer.countCustomerReturn);
        var returnAmount = APP_BCGOGO.StringFilter.priceFilter(customer.totalReturnAmount);
        var totalReturnDebt = customer.totalReturnDebt ? APP_BCGOGO.StringFilter.priceFilter(customer.totalReturnDebt) : '0';

        tr = '<tr class="table-row-original">';
        tr += '<td style="border-left:none;"><input type="checkbox" class="check" style="margin-right:1px;" customerShopId="' + customerShopId + '" name="selectCustomer" value="' + customerId + '"  id=check' + (i + 1) + '/>' + '</td>';
        tr += '<td title="' + name + '">';

        if (stringUtil.isNotEmpty(member) && member.status != 'DISABLED') {
            tr += '<div class="customerName"><a class="i_clickInfo2" href="unitlink.do?method=customer&customerId=' + customerId + '">' + name + '</a></div>';
            tr += '<div class="vips" />';
        } else {
            tr += '<a class="i_clickInfo2" href="unitlink.do?method=customer&customerId=' + customerId + '">' + name + '</a>';
        }
        tr += "</td>";
        tr += '<td title="' + contact + '"><a class="i_clickInfo2" href="unitlink.do?method=customer&customerId=' + customerId + '">' + contact + '</a></td>';
        //todo 自定义 不要放在权限中
        if (APP_BCGOGO.Permission["isMobileHidden"]) {
            tr += '<td class="photo"><span title="' + mobileTitle + '">' + mobile + '</span>';
        } else {
            tr += '<td class="photo"><span>' + mobileTitle + '</span>';
        }
        if ($("#smsSendPermission").val() == "true") {
            tr += '<a style="cursor: pointer;" src="images/duan.png" onclick="sendSms(\'' + mobileTitle + '\',\'3\',\'' + totalReceivable + '\',\'' + licenceNo + '\',\'' + repayDateStr + '\',\'' + name + '\',\'' + customerId + '\')"/>';
        }
        tr += '</td>';
        if (stringUtil.isNotEmpty(member) && member.status != 'DISABLED') {
            tr += '<td title="' + memberNo + '">' + memberNo + '</td>';
            tr += ' <td>' + memberType + '</td> ';
            tr += ' <td>' + balance + '</td> ';
        } else {
            tr += '<td></td><td></td><td></td>';
        }
        tr += ' <td>' + totalAmount + '</td>';

        if (APP_BCGOGO.Permission.CustomerManager.CustomerArrears) {
            tr += '<td class="qian_red"><div class="pay" onclick="toCreateStatementOrder(\'' + customerId + '\', \'CUSTOMER_STATEMENT_ACCOUNT\') ">';
            if (totalReceivable > 0) {
                tr += '<span class="red_color payMoney">收' + totalReceivable + '</span>';
            } else {
                tr += '<span class="gray_color fuMoney">收' + totalReceivable + '</span>';
            }
            if (totalReturnDebt > 0) {
                tr += '<span class="green_color fuMoney">付' + totalReturnDebt + '</span></div></td>';
            } else {
                tr += '<span class="gray_color fuMoney">付' + totalReturnDebt + '</span></div></td>';
            }
        } else {
            tr += '<td class="qian_red"><div class="pay">';
            if (totalReceivable > 0) {
                tr += '<span class="red_color payMoney">应收' + totalReceivable + '</span>';
            } else {
                tr += '<span class="gray_color fuMoney">应收' + totalReceivable + '</span>';
            }
            if (totalReturnDebt > 0) {
                tr += '<span class="green_color fuMoney">应付' + totalReturnDebt + '</span></div></td>';
            } else {
                tr += '<span class="gray_color fuMoney">应付' + totalReturnDebt + '</span></div></td>';
            }
        }
        tr += '<td>' + vehicleCount + '</td>';
        tr += '<td>' + countCustomerReturn + '</td>';
        tr += '<td>' + returnAmount + '</td>';
        var memberBalance;
        tr += '</tr>';
        $("#customerDataTable").append($(tr));
    }
}

function initRelatedCustomerInfo(json) {
    var sortStr = $("#sortStr").val();
    var tr = '<colgroup><col width="50"><col width="146"><col width="60"><col width="100"><col width="120"><col width="70"><col width="165">';
    tr += '<col width="65"><col width="80"><col width="70"><col width="75"></colgroup>';
    tr += '<tr class="divSlip titleBg"><td style="padding-left:10px;"></td><td>客户名</td><td>联系人</td><td>手机</td><td>地址</td>';
    tr += '<td><div class="fl">累计消费</div><input id="total_amount" sort="total_amount" class="ascending sort fl" type="button" onfocus="this.blur();">';
    tr += '</td><td><div class="fl">应收</div><input id="total_debt" sort="total_debt" class="ascending sort fl" type="button" onfocus="this.blur();">' +
        '<span style="margin-left:50px">应付</div><input id="total_return_debt" sort="total_return_debt" class="ascending sort fl" type="button" onfocus="this.blur();">' +
        '</td><td>退货次数</td><td>退货金额</td><td>客户类型</td><td>操作</td></tr>';
    $("#customerDataTable").append($(tr));
    if (stringUtil.isNotEmpty(sortStr)) {
        var sortField = sortStr.split(",")[0];
        var sortType = sortStr.split(",")[1];
        if (sortType == "asc") {
            $("#" + sortField).addClass("ascending").removeClass("descending");
        } else {
            $("#" + sortField).addClass("descending").removeClass("ascending");
        }
    }
    if (!json.customerSuppliers) return;
    var customers = json.customerSuppliers;
    for (var i = 0; i < customers.length; i++) {
        var customer = customers[i];
        var address;
        var customerShopId = customer.customerOrSupplierShopId || '';
        var licenceNo = customer.licenceNo ? customer.licenceNo : "";
        address = customer.address ? customer.address : "——";

        var customerId = customer.idStr;
        var lastBill = customer.lastBill ? customer.lastBill : "";
        var lastDateStr = customer.lastDateStr ? customer.lastDateStr : "";
        var totalReceivable = customer.totalDebt ? APP_BCGOGO.StringFilter.priceFilter(customer.totalDebt) : '0';
        var totalAmount = APP_BCGOGO.StringFilter.priceFilter(customer.totalAmount);
        var mobile = customer.mobile ? customer.mobile : "";
        var mobileTitle = customer.mobile ? customer.mobile : "";
        if (APP_BCGOGO.Permission["isMobileHidden"]) {
            mobile = mobile.substr(0, 3) + "****" + mobile.substr(7, 4);
        }
        var repayDateStr = customer.repayDateStr ? customer.repayDateStr : "";
        var name = customer.name ? customer.name : "";
        var contact = customer.contact ? customer.contact : "";
        var countCustomerReturn = APP_BCGOGO.StringFilter.priceFilter(customer.countCustomerReturn);
        var returnAmount = APP_BCGOGO.StringFilter.priceFilter(customer.totalReturnAmount);
        var totalReturnDebt = customer.totalReturnDebt ? APP_BCGOGO.StringFilter.priceFilter(customer.totalReturnDebt) : '0';
        tr = '<tr class="table-row-original">';
        tr += '<td style="border-left:none;"><input type="checkbox" class="check" style="margin-right:1px;" customerShopId="' + customerShopId + '" name="selectCustomer" value="' + customerId + '"  id=check' + (i + 1) + '/>' + '</td>';
        tr += '<td title="' + name + '">';
        tr += '<a class="i_clickInfo2" href="unitlink.do?method=customer&customerId=' + customerId + '">' + name + '</a></td>';
        tr += '<td title="' + contact + '"><a class="i_clickInfo2" href="unitlink.do?method=customer&customerId=' + customerId + '">' + contact + '</a></td>';
        //todo 自定义 不要放在权限中
        if (APP_BCGOGO.Permission["isMobileHidden"]) {
            tr += '<td class="photo"><span title="' + mobileTitle + '">' + mobile + '</span>';
        } else {
            tr += '<td class="photo"><span>' + mobileTitle + '</span>';
        }
        if ($("#smsSendPermission").val() == "true") {
            tr += '<a style="cursor: pointer;" src="images/duan.png" onclick="sendSms(\'' + mobileTitle + '\',\'3\',\'' + totalReceivable + '\',\'' + licenceNo + '\',\'' + repayDateStr + '\',\'' + name + '\',\'' + customerId + '\')"/>';
        }
        tr += '</td>';
        tr += ' <td title="' + address + '">' + tableUtil.limitLen(address, 15) + '</td> ';
        tr += ' <td>' + totalAmount + '</td>';
        if (APP_BCGOGO.Permission.CustomerManager.CustomerArrears) {
            tr += '<td class="qian_red"><div class="pay" onclick="toCreateStatementOrder(\'' + customerId + '\', \'CUSTOMER_STATEMENT_ACCOUNT\') ">';
            if (totalReceivable > 0) {
                tr += '<span class="red_color payMoney">收' + totalReceivable + '</span>';
            } else {
                tr += '<span class="gray_color fuMoney">收' + totalReceivable + '</span>';
            }
            if (totalReturnDebt > 0) {
                tr += '<span class="green_color fuMoney">付' + totalReturnDebt + '</span></div></td>';
            } else {
                tr += '<span class="gray_color fuMoney">付' + totalReturnDebt + '</span></div></td>';
            }
        } else {
            tr += '<td class="qian_red"><div class="pay">';
            if (totalReceivable > 0) {
                tr += '<span class="red_color payMoney">应收' + totalReceivable + '</span>';
            } else {
                tr += '<span class="gray_color fuMoney">应收' + totalReceivable + '</span>';
            }
            if (totalReturnDebt > 0) {
                tr += '<span class="green_color fuMoney">应付' + totalReturnDebt + '</span></div></td>';
            } else {
                tr += '<span class="gray_color fuMoney">应付' + totalReturnDebt + '</span></div></td>';
            }
        }
        tr += '<td>' + countCustomerReturn + '</td>';
        tr += '<td>' + returnAmount + '</td>';
        if (!customer.relationType || customer.relationType == "UNRELATED") {
            tr += '<td><img class="icon" src="images/icons.png"><a style="color: #CB0000">非关联</a></td>';
            tr += '<td><a class="sentInvitationCodeSmsBtn" data-customer-id="' + customerId + '" data-mobile="' + mobileTitle + '" >推荐</a>&nbsp;';
            if (APP_BCGOGO.Permission.CustomerManager.UpdateCustomer) {
                tr += '<a class="updateCustomer" data-customer-id="' + customerId + '">升级</a>';
            }
            tr += '</td>';
//            tr += '<td><span  style="color: #CB0000">非关联</span></td>';
//            tr += '<td>';
//            if (APP_BCGOGO.Permission.CustomerManager.UpdateCustomer) {
//                tr += '<a class="updateCustomer" data-customer-id="' + customerId + '">升级</a>';
//            }
//            tr +='</td>';
        } else {
            tr += "<td>已关联</td>"
            tr += '<td><a class="supplier_cancel_shop_relation" customerId="' + customerId + '">取消关联</a></td>';
        }
        tr += '</tr>';
        $("#customerDataTable").append($(tr));
    }

}

function initCustomerDataTr(json, resetStatNum) {
    $("#customerDataTable colgroup,#customerDataTable tbody").remove();
    if (json == null || json[0] == null) {
        return;
    }
    json = json[0];

    if (resetStatNum) {
        $("#memberNum").html(json.memberNumFound);
        $("#totalNum").html(json.numFound);
        $("#totalDebtStat").html(G.Lang.isEmpty(json.totalDebt) ? "0" : new Number(json.totalDebt).toFixed(2));
        $("#totalReturnDebt").html(G.Lang.isEmpty(json.totalReturnDebt) ? "0" : new Number(json.totalReturnDebt).toFixed(2));
        $("#totalBalance").html(G.Lang.isEmpty(json.totalBalance) ? "0" : new Number(json.totalBalance).toFixed(2));
        $("#sendMulSms").attr("value", json.mobiles);
        $("#mobileNum").html(Number(json.hasMobileNumFound));
        $("#totalOBD").html(Number(json.hasObdNumFound));
        $("#totalApp").html(Number(json.hasAppNumFound));
        $('#sendMulSmsODB').data('odbContactIdList',json.odbContactIdList);
        $("#todayCustomer").html(json.todayNewCustomerNumFound);
        $("#relatedNum").html(json.relatedNum);
        $("#totalRows").val(json.numFound);
        $("#debtCustomerCount").html(json.totalReceivableNumFound);
        $('#totalConsumption').html(new Number(json.totalConsumption).toFixed(2));
        var totalDeposit = App.StringFilter.priceFilter(json.totalDeposit);
        $("#totalDepositStat").html(totalDeposit ? totalDeposit : 0);
    }

    if (APP_BCGOGO.Permission.Version.RelationCustomer) {
        initRelatedCustomerInfoTable(json);
    } else {
        initCustomerInfoTable(json);
    }
    initAndBindSelectCheckBoxs();
    initSortColumn();
    $("#resetSearchCondition").val(''); //清除数据
}

//排序
function initSortColumn() {
    $(".ascending,.descending")
        .click(
        function(e) {
            var dom = e.target;
            var inputs = $(".sort");
            for (var i = 0,max = inputs.length; i < max; i++) {
                if (dom.id == inputs[i].id) continue;
                $(inputs[i]).addClass("ascending").removeClass("descending");
            }
            var sortStatus = "";
            var sortStr = "";
            var sorts = $(dom).attr("sort").split(",");
            if ($(dom).hasClass("ascending")) {
                $(dom).addClass("descending").removeClass("ascending");
                for (var i = 0,max = sorts.length; i < max; i++) {
                    if (i == max - 1) {
                        sortStatus += sorts[i] + " desc ";
                        sortStr += sorts[i] + ",desc ";
                    } else {
                        sortStatus += sorts[i] + " desc ,";
                        ;
                    }
                }
            } else {
                $(dom).addClass("ascending").removeClass("descending");
                for (var i = 0,max = sorts.length; i < max; i++) {
                    if (i == max - 1) {
                        sortStatus += sorts[i] + " asc ";
                        sortStr += sorts[i] + ",asc";
                    } else {
                        sortStatus += sorts[i] + " asc ,";

                    }
                }
            }
            $("#sortStr").val(sortStr);
            $("#sortStatus").val(sortStatus);
            $("#customerSearchBtn").click();
        }).each(function() {

        });

}

function initAndBindSelectCheckBoxs() {
    //to init selected checkbox
    $("[name='selectCustomer']").each(function() {
        if (isContainSelectedId($(this).val())) {
            $(this).attr("checked", true);
        } else {
            $(this).attr("checked", false);
        }
    });

    $("[name='selectCustomer']").click(function() {
        var selectedId = $(this).val();
        var customerShopId = $(this).attr("customerShopId");
        var isObdCustomer = $(this).attr("isobdcustomer");
//    var customerShopId=$(this).attr()
        if ($(this).attr("checked")) {
            if (!isContainSelectedId($(this).val())) {
                var selectData = '<input type="hidden" customerShopId="' + customerShopId + '"  value="' + selectedId + '" isObdCustomer="' + isObdCustomer + '" />';
                $("#selectedIdArray").append(selectData);
            }
        } else {
            $("#selectedIdArray input").each(function() {
                if ($(this).val() == selectedId) {
                    $(this).remove();
                }
            });
        }
    });

}

function isContainSelectedId(selectedId) {
    var flag = false;
    $("#selectedIdArray input").each(function() {
        if ($(this).val() == selectedId) {
            flag = true;
            return;
        }
    });
    return flag;
}

function getByteLen(val) {    //传入一个字符串
    var len = 0;
    for (var i = 0; i < val.length; i++) {
        if (val[i].match(/[^\x00-\xff]/ig) != null) //全角
            len += 2; //如果是全角，占用两个字节
        else
            len += 1; //半角占用一个字节
    }
    return len;
}

function getTodayMilliseconds(date) {
    return ((date.getHours() * 60 + date.getMinutes()) * 60 + date.getSeconds()) * 1000;
}

//把8转换成08
function addZero(data) {
    if (data < 10) return "0" + data;
    else return data;
}


function validateRepair(customerId) {
    var flag = true;

    $.ajax({
        type:"POST",
        url:"txn.do?method=checkRepairOrderStatus",
        async:false,
        data:{
            customerId:customerId,
            tsLog: 10000000000 * (1 + Math.random())
        },
        cache:false,
        dataType:"json",
        success:function(jsonObject) {
            var resu = jsonObject.resu;
            if (resu == "error") {
                flag = false;
            }
        }
    });

    return flag;
}

function validateArrears(customerId) {
    var flag = true;

    $.ajax({
        type:"POST",
        url:"customer.do?method=checkArrears",
        async:false,
        data:{
            customerId:customerId,
            tsLog: 10000000000 * (1 + Math.random())
        },
        cache:false,
        dataType:"json",
        success:function(jsonObject) {
            var resu = jsonObject.resu;
            if (resu == "error") {
                flag = false;
            }
        }
    });

    return flag;
}

function validateMember(customerId) {
    var flag = true;

    $.ajax({
        type:"POST",
        url:"member.do?method=checkMemberBalanceExist",
        async:false,
        data:{
            customerId:customerId,
            tsLog: 10000000000 * (1 + Math.random())
        },
        cache:false,
        dataType:"json",
        success:function(jsonObject) {
            var resu = jsonObject.resu;
            if (resu == "error") {
                flag = false;
            }
        }
    });

    return flag;
}


function cloneAll(fromObj, toObj) {
    for (var i in fromObj) {
        if (typeof fromObj[i] == "object") {
            toObj[i] = {};
            cloneAll(fromObj[i], toObj[i]);
            continue;
        }
        toObj[i] = fromObj[i];
    }
}

function resetSearchCondition(ajaxData) {
    $(".lineBody input").css("color", "#272727");
    var searchWord = ajaxData.searchWord == "" ? $("#customerInfoText").attr("initialvalue") : ajaxData.searchWord;
    var productName = ajaxData.productName == "" ? $("#productName").attr("initialvalue") : ajaxData.productName;
    var productBrand = ajaxData.productBrand == "" ? $("#productBrand").attr("initialvalue") : ajaxData.productBrand;
    var productSpec = ajaxData.productSpec == "" ? $("#productSpec").attr("initialvalue") : ajaxData.productSpec;
    var productModel = ajaxData.productModel == "" ? $("#productModel").attr("initialvalue") : ajaxData.productModel;
    var productVehicleBrand = ajaxData.productVehicleBrand == "" ? $("#productVehicleBrand").attr("initialvalue") : ajaxData.productVehicleBrand;
    var productVehicleModel = ajaxData.productVehicleModel == "" ? $("#productVehicleModel").attr("initialvalue") : ajaxData.productVehicleModel;
    var commodityCode = ajaxData.commodityCode == "" ? $("#commodityCode").attr("initialvalue") : ajaxData.commodityCode;
    if (ajaxData.memberType == '非会员') {
        $("#noMemberRadio").click();
    } else if (ajaxData.memberType != '') {
        if (ajaxData.memberType == $("#allCardName").val() && ajaxData.memberRadioClicked) {
            $("#memberRadio").click();
        } else {
            var memberTypes = ajaxData.memberType.split(",");
            for (var i = 0; i < memberTypes.length; i++) {
                $("a[name='memberCardTypes']").each(function() {
                    if ($(this).attr("value") == memberTypes[i]) {
                        $(this).click();
                    }
                });
            }
        }

    }
    if (ajaxData.lastExpenseTimeStart != '') {
        $("#startDate").val(dateUtil.formatDate(new Date(ajaxData.lastExpenseTimeStart), dateUtil.dateStringFormatDay));
    }
    if (ajaxData.lastExpenseTimeEnd != '') {
        $("#endDate").val(dateUtil.formatDate(new Date(ajaxData.lastExpenseTimeEnd - 1000 * 60 * 60 * 24 + 1), dateUtil.dateStringFormatDay));
    }
    if (ajaxData.lastExpenseTimeStart != '' && ajaxData.lastExpenseTimeEnd != '') {
        if ($("#startDate").val() == dateUtil.getYesterday() && $("#endDate").val() == dateUtil.getYesterday()) {
            $("#date_yesterday").click();
        } else if ($("#startDate").val() == dateUtil.getToday() && $("#endDate").val() == dateUtil.getToday()) {
            $("#date_today").click();
        } else if ($("#startDate").val() == dateUtil.getOneWeekBefore() && $("#endDate").val() == dateUtil.getToday()) {
            $("#date_last_week").click();
        } else if ($("#startDate").val() == dateUtil.getOneMonthBefore() && $("#endDate").val() == dateUtil.getToday()) {
            $("#date_last_month").click();
        } else if ($("#startDate").val() == dateUtil.getOneYearBefore() && $("#endDate").val() == dateUtil.getToday()) {
            $("#date_last_year").click();
        }
    }

    $("#customerInfoText").val(searchWord);
    $("#customerId").val(ajaxData.ids);
    if (ajaxData.province != '') {
        $("#provinceNo").val(ajaxData.province);
        $("#provinceNo").change();
    }
    if (ajaxData.city != '') {
        $("#cityNo").val(ajaxData.city);
        $("#cityNo").change();
    }
    $("#regionNo").val(ajaxData.region);
    $("#productName").val(productName);
    $("#productBrand").val(productBrand);
    $("#productSpec").val(productSpec);
    $("#productModel").val(productModel);
    $("#productVehicleBrand").val(productVehicleBrand);
    $("#productVehicleModel").val(productVehicleModel);
    $("#commodityCode").val(commodityCode);
    $("#totalTradeAmountStart").val(ajaxData.totalAmountDown);
    $("#totalTradeAmountEnd").val(ajaxData.totalAmountUp);
    $("#totalReceivableStart").val(ajaxData.totalDebtDown);
    $("#totalReceivableEnd").val(ajaxData.totalDebtUp);
    if (ajaxData.totalDebtDown || ajaxData.totalDebtUp) {
        $("#hasDebt").val('true');
    }
    $("#debtAmountStart").val(ajaxData.totalPayableDown);
    $("#debtAmountEnd").val(ajaxData.totalPayableUp);
    $("#maxRows").val(ajaxData.maxRows);
    $("#filterType").val(ajaxData.filterType);
    $("#relationType").val(ajaxData.relationType);
    $("#sortStatus").val(ajaxData.sort);
    $(".J_supplier_sort").removeClass("hover");
    if (ajaxData.sort == ' created_time desc ') {
        $("#createdTimeSortSpan").addClass("arrowDown").removeClass("arrowUp");
        $("#createdTimeSort").addClass("hover");
    } else if (ajaxData.sort == ' created_time asc ') {
        $("#createdTimeSortSpan").addClass("arrowUp").removeClass("arrowDown");
        $("#createdTimeSort").addClass("hover");
    } else if (ajaxData.sort == ' last_expense_time desc ') {
        $("#lastInventoryTimeSortSpan").addClass("arrowDown").removeClass("arrowUp");
        $("#lastInventoryTimeSort").addClass("hover");
    } else if (ajaxData.sort == ' last_expense_time asc ') {
        $("#lastInventoryTimeSortSpan").addClass("arrowUp").removeClass("arrowDown");
        $("#lastInventoryTimeSort").addClass("hover");
    } else if (ajaxData.sort == ' total_amount desc ') {
        $("#totalTradeAmountSortSpan").addClass("arrowDown").removeClass("arrowUp");
        $("#totalTradeAmountSort").parent().addClass("hover");
    } else if (ajaxData.sort == ' total_amount asc ') {
        $("#totalTradeAmountSortSpan").addClass("arrowUp").removeClass("arrowDown");
        $("#totalTradeAmountSort").parent().addClass("hover");
    } else if (ajaxData.sort == ' total_debt asc ') {
        $("#totalReceivableSortSpan").addClass("arrowUp").removeClass("arrowDown");
        $("#totalReceivableSort").parent().addClass("hover");
    } else if (ajaxData.sort == ' total_debt desc ') {
        $("#totalReceivableSortSpan").addClass("arrowDown").removeClass("arrowUp");
        $("#totalReceivableSort").parent().addClass("hover");
    } else if (ajaxData.sort == ' total_return_debt asc ') {
        $("#totalPayableSortSpan").addClass("arrowUp").removeClass("arrowDown");
        $("#totalPayableSort").parent().addClass("hover");
    } else if (ajaxData.sort == ' total_return_debt desc ') {
        $("#totalPayableSortSpan").addClass("arrowDown").removeClass("arrowUp");
        $("#totalPayableSort").parent().addClass("hover");
    } else if (ajaxData.sort == ' total_deposit asc ') {
        $("#depositSortSpan").addClass("arrowUp").removeClass("arrowDown");
        $("#depositSort").parent().addClass("hover");
    } else if (ajaxData.sort == ' total_deposit desc ') {
        $("#depositSortSpan").addClass("arrowDown").removeClass("arrowUp");
        $("#depositSort").parent().addClass("hover");
    } else {
        $("#createdTimeSortSpan").addClass("arrowDown").removeClass("arrowUp");
        $("#createdTimeSort").addClass("hover");
    }
    $(".lineBody input").each(function() {
        if ($(this).attr("initialvalue") == $(this).val()) {
            $(this).css("color", "#ADADAD");
        }
    });
}