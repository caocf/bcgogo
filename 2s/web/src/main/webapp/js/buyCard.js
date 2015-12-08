$(function () {
    var expired = [];
    $('#table_productNo tr').each(function () {
        var cardTimesStatus = $('input[id$=cardTimesStatus]', this);
        if (cardTimesStatus.val() == '1') {
            var deadline = $('input[id$=deadline]', this);
            var oldDeadlineStr = $('input[id$=oldDeadlineStr]', this);
            var t = oldDeadlineStr.val().split('-');
            var time1 = Date.UTC(t[0], t[1], t[2], 0, 0, 0);
            var today = new Date();
            var time2 = Date.UTC(today.getFullYear(), today.getMonth() + 1, today.getDate(), 0, 0, 0);
            if (time1 < time2) {
                var serviceName = $('input[id$=serviceName]', this);
                var oldTimes = $('input[id$=oldTimes]', this);
                Number(oldTimes.val()) > 0 &&  expired.push('【' + serviceName.val() + '】剩余<span style="color:red;">' + oldTimes.val() + '</span>次<span style="color:#808080;">（' + oldDeadlineStr.val() + '失效）</span>');
            }
        }
    });
    if (expired.length) {
        var root = $('#dialog-confirm-content');
        $.each(expired, function () {
            root.append('<div>' + this + '</div>');
        });
        $("#dialog-confirm").dialog({
            resizable: false,
            height: 140,
            width: 360,
            position:[-300,100],
            modal: true,
            buttons: {
                '续卡': function () {
                    var radio = $('#dialog-confirm input[name=opt]:checked');
                    if(radio.val() == '2'){
                        $('#table_productNo tr').each(function () {
                            var cardTimesStatus = $('input[id$=cardTimesStatus]', this);
                            if (cardTimesStatus.val() == '1') {
                                var deadline = $('input[id$=deadline]', this);
                                var oldDeadlineStr = $('input[id$=oldDeadlineStr]', this);
                                var t = oldDeadlineStr.val().split('-');
                                var time1 = Date.UTC(t[0], t[1], t[2], 0, 0, 0);
                                var today = new Date();
                                var time2 = Date.UTC(today.getFullYear(), today.getMonth() + 1, today.getDate(), 0, 0, 0);
                                var oldTimes = $('input[id$=oldTimes]', this);
                                if (time1 < time2 && Number(oldTimes.val()) > 0) {
                                    var balanceTimes = $('input[id$=balanceTimes]', this);
                                    var cardTimes = $('input[id$=cardTimes]', this);
                                    balanceTimes.val(Number(balanceTimes.val()) - Number(oldTimes.val()));
                                    oldTimes.val(0);
                                    $('td',$(this).parents('tr')).eq(1).text(0);
                                    $('td',this).eq(1).text(0);
                                }
                            }
                        })
                    }
                    $(this).dialog("close");
                },
                '放弃': function () {
                    $(this).dialog("close");
                    closeWindow();
                }
            }
        });
        $('#dialog-confirm').parent().css({
            left:200,
            top:70
        })
    }
});

jQuery(document).ready(function () {
    tableUtil.tableStyle('#table_productNo', '.buycardTr');

    if (!$("#memberCardPrice").val()) {
        $("#memberCardPrice").val(0);
    }


    $("#total").val($("#memberCardPrice").val());
    $("#totalSpan").html($("#memberCardPrice").val());

    $("#memberCardPrice").live("blur", function () {

        if (!$("#memberCardPrice").val()) {
            $("#memberCardPrice").val(0);
        }

        var oldPrice = $("#total").val();

        $("#total").val($("#memberCardPrice").val());
        $("#totalSpan").html($("#memberCardPrice").val());
        if (!$("#total").val()) {
            $("#total").val(0);
        }

        if (jQuery("#total").val() == 0) {
            jQuery("#discount,#debt,#cash,#bankCard,#check,#settledAmount,#checkNo,#huankuanTime").attr("disabled", "true");
        }

        if (jQuery("#total").val() > 0) {
            jQuery("#discount,#debt,#cash,#bankCard,#check,#settledAmount,#checkNo,#huankuanTime").removeAttr("disabled");
        }

        if (oldPrice == $("#total").val()) {
            return;
        }

        jQuery("#discount,#debt,#bankCard,#check,#checkNo,#huankuanTime").val("");
        $("#cash,#settledAmount").val($("#total").val())
    });

//    $("#total").live("blur",function(){
//        if(!$("#total").val())
//        {
//            $("#total").val(0);
//        }
//
//        if(jQuery("#total").val() == 0)
//        {
//            jQuery("#discount,#debt,#cash,#bankCard,#check,#settledAmount,#checkNo,#huankuanTime").attr("disabled","true");
//        }
//
//        if(jQuery("#total").val() >0)
//        {
//            jQuery("#discount,#debt,#cash,#bankCard,#check,#settledAmount,#checkNo,#huankuanTime").removeAttr("disabled");
//        }
//    });


    jQuery("#cash,#settledAmount").val(APP_BCGOGO.StringFilter.inputtingPriceFilter(jQuery("#total").val(), 2));

    jQuery("#memberNo").live("blur", function () {
        if (jQuery("#memberNo").val() && jQuery("#memberNo").val().length > 25) {
            alert("会员号不能大于25位！");
            jQuery("#memberNo").val("").select().focus();
            return;
        }
        if (jQuery("#memberNo").val() == null || jQuery("#memberNo").val() == "") {

        }
        else {
            var memberNo = jQuery("#memberNo").val();
            var oldMemberNo = jQuery("#oldMemberNo").val()
            if (memberNo != oldMemberNo) {
                jQuery.ajax({
                    type: "POST",
                    url: "member.do?method=checkMemberNo",
                    async: false,
                    data: {
                        memberNo: memberNo,
                        tsLog: 10000000000 * (1 + Math.random())
                    },
                    cache: false,
                    dataType: "json",
                    success: function (jsonObject) {
                        var tmp = jsonObject.resu;
                        if (tmp == "error") {
                            nsDialog.jAlert("此会员号已被占用，请重新输入", null, function () {
                                jQuery("#memberNo").val("").select().focus();
                            });
                            return false;
                        }
                    }
                });
            }
        }
    });


    jQuery("#memo").live("click", function () {
        if (jQuery("#memo").val() && jQuery("#memo").val().length > 200) {
            alert("备注不能超过200字！");
        }
    });

    $("#memberNo").keyup(function (e) {
        var e = e || event;

        if (e.keyCode == "13" || e.keyCode == "108") {
            $("#memberNo").blur();
        }
    });

    jQuery("#password").keyup(function () {
        if (jQuery("#password").val()) {
            jQuery("#checkPwd")[0].checked = true;
        }
        else {
            jQuery("#checkPwd")[0].checked = false;
        }
    });

    jQuery("#password").live("click", function () {
        if (jQuery("#password").val() && jQuery("#password").val().length > 20) {
            alert("密码不能超过20字！");
            jQuery("#password").val("").select().focus();
        }
    });

    jQuery(".timesStatus2").each(function (i) {
        if (jQuery(this)[0].checked == true) {
            jQuery(this).parent().prev(0).attr("readOnly", true);
        }
    });


    if (jQuery("#total").val() == 0) {
        jQuery("#discount,#debt,#cash,#bankCard,#check,#settledAmount,#checkNo,#huankuanTime").attr("disabled", "true");
    }


    jQuery("#discount,#debt,#cash,#bankCard,#check,#settledAmount,#totalBalance,#total,#memberCardPrice").keyup(function () {
        if (jQuery(this).val != APP_BCGOGO.StringFilter.inputtingPriceFilter(this.value, 2)) {
            jQuery(this).val(APP_BCGOGO.StringFilter.inputtingPriceFilter(this.value, 2));
        }
    });

    $("#memberDiscount").bind("keyup", function () {
        $(this).val(APP_BCGOGO.StringFilter.inputtingPriceFilter(this.value, 1));
    });

    $("#memberDiscount").bind("blur", function () {
        var memberDiscount = $("#memberDiscount").val();
        if (memberDiscount) {
            memberDiscount = memberDiscount * 1;
            if (memberDiscount >= 10 || memberDiscount <= 0) {
                alert("会员折扣只能是在0-10之间，不能是0或者10");
                $("#memberDiscount").val("");
                return;
            }
        }
    });

    if (jQuery("#div_close") != null) {
        jQuery("#div_close").click(function () {
            closeWindow();
        });
    }

    if (jQuery("#cancleBtn") != null) {
        jQuery("#cancleBtn").click(function () {
            closeWindow();
        });
    }

    jQuery("#totalBalance").val(jQuery("#balance").html() * 1 + jQuery("#addbalance").html() * 1);

    trCount = jQuery(".item").size();
    var trSample = '<tr class="item table-row-original">' +
        '<td style="border-left:none;">' +
        '<input type="hidden" id="memberCardOrderServiceDTOs0.serviceId" name="memberCardOrderServiceDTOs0.serviceId" value=""/>' +
        '<input type="hidden" id="memberCardOrderServiceDTOs0.oldTimes" name="memberCardOrderServiceDTOs0.oldTimes" value=""/>' +
        '<input type="hidden" id="memberCardOrderServiceDTOs0.increasedTimes" name="memberCardOrderServiceDTOs0.increasedTimes" value=""/>' +
        '<input type="hidden" id="memberCardOrderServiceDTOs0.cardTimes" name="memberCardOrderServiceDTOs0.cardTimes" value=""/>' +
        '<input type="hidden" id="memberCardOrderServiceDTOs0.cardTimesStatus" name="memberCardOrderServiceDTOs0.cardTimesStatus" value=""/>' +
        '<input type="hidden" id="memberCardOrderServiceDTOs0.oldDeadlineStr" name="memberCardOrderServiceDTOs0.oldDeadlineStr" value=""/>' +
        '<input type="hidden" id="memberCardOrderServiceDTOs0.addTerm" name="memberCardOrderServiceDTOs0.addTerm" value=""/>' +
        '<input type="text" id="memberCardOrderServiceDTOs0.serviceName" name="memberCardOrderServiceDTOs0.serviceName" class="tab_input addService" style="width:90%" value=""/>' +
        '</td>' +
        '<td class="txt_right">0</td>' +
        '<td class="txt_right">0</td>' +
        '<td class="txt_right" style="padding-right: 4px;">' +
//        '<input type="radio" class = "timesStatus1" id="memberCardOrderServiceDTOs0.timesStatus1" name="memberCardOrderServiceDTOs0.timesStatus" value="0" checked="checked"/>' +
//        '\n' +
        '<input type="text" class="tab_input" style="width:50px;" name="memberCardOrderServiceDTOs0.balanceTimes" id="memberCardOrderServiceDTOs0.balanceTimes" value=""/>' +
        '\n次\n' +
//        '<label>' +
//        '<input type="radio" class = "timesStatus2" id="memberCardOrderServiceDTOs0.timesStatus2" name="memberCardOrderServiceDTOs0.timesStatus" value="1"/>不限次' +
//        '</label>' +
        '</td>' +
        '<td class="txt_right"><input type="text" class="tab_input" id="memberCardOrderServiceDTOs0.vehicles" name="memberCardOrderServiceDTOs0.vehicles" class="tab_input" value=""/></td>' +
        '<td class="txt_right">' +
//        '<input type="radio" class="deadlineStatus1" id="memberCardOrderServiceDTOs0.deadlineStatus1" name="memberCardOrderServiceDTOs0.deadlineStatus" value="0" checked="checked" style="float:left;" />' +
        '<input type="text" id="memberCardOrderServiceDTOs0.deadlineStr" name="memberCardOrderServiceDTOs0.deadlineStr" value="" readonly = "true" class="tab_input isDatepickerInited checkDeadLine" style="width:150px; float:left;"/>' +
//      '<img src="images/datePicker.jpg" class="img"  style="margin:-3px 0px 0px 5px; float:left;">' +
//        '<label style="float:left; padding-left:8px;">' +
//        '<input type="radio" class="deadlineStatus2" id="memberCardOrderServiceDTOs0.deadlineStatus2" name="memberCardOrderServiceDTOs0.deadlineStatus" value="1" />无限期</label>' +
        '</td>' +
        '<td>' +
        '<input class="opera1" type="button" id="memberCardOrderServiceDTOs0.opera1Btn" name="memberCardServiceDTOs0.opera1Btn">' +
        '</td>' +
        '</tr>';

    //增加行
    jQuery(".opera2").live('click', function () {

        var opera2Id = "";
        var ischeck = checkAddServiceData(this);
        if (!ischeck && ischeck != null) {
            return;
        }

        //服务检查是否相同
        if (jQuery(".item").size() >= 1)
            if (checkTheSame()) {
                alert("服务有重复内容或为空，请修改或删除。");
                return false;
            }
        var tr = jQuery(trSample).clone();
        jQuery(tr).find("input").val("");

        jQuery(tr).find("input").each(function (i) {
            //replace id
            var idStr = jQuery(this).attr("id");
            var idStrs = idStr.split(".");
            if (idStr == '') {
                return true;
            }
            var tcNum = trCount > 0 ? trCount - 1 : 0;

            while (checkThisServiceDom(tcNum, idStrs[1])) {
                tcNum = ++tcNum;
            }

            var newId = "memberCardOrderServiceDTOs" + tcNum + "." + idStrs[1];
            jQuery(this).attr("id", newId);

//            if (newId.indexOf("timesStatus1") != -1) {
//                jQuery(this).attr("value", 0);
//            }
//
//            if (newId.indexOf("timesStatus2") != -1) {
//                jQuery(this).attr("value", 1);
//            }
//
//            if (newId.indexOf("deadlineStatus1") != -1) {
//                jQuery(this).attr("value", 0);
//            }
//
//            if (newId.indexOf("deadlineStatus2") != -1) {
//                jQuery(this).attr("value", 1);
//            }

            var nameStr = jQuery(this).attr("name");
            if (nameStr == '') {
                return true;
            }
            var nameStrs = nameStr.split(".");
            var newName = "memberCardOrderServiceDTOs[" + tcNum + "]." + nameStrs[1];
            jQuery(this).attr("name", newName);
            jQuery(this).attr("autocomplete", "off");
            var idPrefix = this.id.split(".")[0];
            var idSuffix = this.id.split(".")[1];
            var domrows = parseInt(idPrefix.substring(26, idPrefix.length));
            jQuery(this).bind('keyup', function (e) {         //为input绑定keyup事件

                var pos = getCursorPosition(this);
                if (!checkKeyUp(this, e)) {
                    return;
                }

                setCursorPosition(this, pos);
            });
            jQuery(this).bind('click', function () {
                var pos = getCursorPosition(this);

                setCursorPosition(this, pos);
            });
        });
        jQuery(tr).appendTo("#table_productNo");

        trCount++;
        isShowAddButton();
        tableUtil.tableStyle('#table_productNo', '.buycardTr,#div_serviceName');
    });
    // TODO settimeout 200ms to call
    setTimeout(isShowAddButton, 200);

    jQuery("#totalBalance").live("blur", function () {
        var foo = APP_BCGOGO.Validator;
        if (null == jQuery("#totalBalance").val() || "" == jQuery("#totalBalance").val()) {
            jQuery("#totalBalance").val(0);
        }

        this.value = (APP_BCGOGO.StringFilter.priceFilter(this.value, 1));
        if (!foo.stringIsPrice(this.value)) {
            alert("请输入正确的储值金额！");
            jQuery("#totalBalance").val("").select().focus();
        }
    });

    //删除行
    jQuery(".opera1").live('click', function () {

        var iPrefixId = jQuery(this).attr("id");
        iPrefixId = iPrefixId.substring(0, iPrefixId.indexOf("."));

        jQuery(this).closest("tr").remove();

        isShowAddButton();
    });

    jQuery("#discount,#debt,#cash,#bankCard,#check,#settledAmount").live("blur", function () {
        var foo = APP_BCGOGO.Validator;

        if (this.value) {
            this.value = APP_BCGOGO.StringFilter.priceFilter(this.value, 1);
            if (!this.value) {
                alert("请填写正确的价格");
            }
            var money = this.value;
            if (null != money && "" != money && !foo.stringIsPrice(money)) {
                this.value = "";
            }
        }
    });
    $("#inputMobileDiv").dialog({
        autoOpen: false,
        resizable: false,
        title: "发送短信需要填写手机号，请填写手机号！",
        height: 150,
        width: 355,
        modal: true,
        closeOnEscape: false,
        buttons: {
            "提交": function () {
                if ($.trim($("#divMobile").val()) == '') {
                    nsDialog.jAlert("请填写手机号");
                    return;
                }
                if ($("#divMobile").val()) {
                    //验证格式 NO -- 给出提醒
                    if (!APP_BCGOGO.Validator.stringIsMobilePhoneNumber($("#divMobile").val())) {
                        nsDialog.jAlert("手机号格式不正确");
                        return;
                    }

                    var r = APP_BCGOGO.Net.syncGet({
                        url: "customer.do?method=getCustomerJsonDataByMobile",
                        data: {mobile: $("#divMobile").val()},
                        dataType: "json"
                    });
                    if (r.success) {
                        alert('与已存在客户【' + r.data.name + '】的手机号相同,请重新输入');
                        return;
                    } else {
                        $("#mobile").val($("#divMobile").val());
                        $("#inputMobileDiv").dialog("close");
                        submitAfterInputMobile();
                    }

                }
            },
            "取消": function () {
                $("#inputMobileDiv").dialog("close");
            }
        },
        close: function () {
            $("#divMobile").val("");
        }
    });
    jQuery("#saveBtn").live('click', function () {
        if ($("#saveBtn").attr("disabled")) {
            return;
        }

        $("#saveBtn").attr("disabled", true);

        if (!$("#total").val()) {
            $("#total").val(0);
        }

        var foo = APP_BCGOGO.Validator;
        if (jQuery("#memberNo").val() == null || $.trim(jQuery("#memberNo").val()) == "") {
            alert("会员号不能为空");
            $("#saveBtn").removeAttr("disabled");
            return;
        }

        if (!validateMemberNo()) {
            alert("此会员号被占用请修改");
            jQuery("#memberNo").val("").select().focus();
            $("#saveBtn").removeAttr("disabled");
            return;
        }

        if (jQuery("#totalBalance").val() == null || jQuery("#totalBalance").val() == "") {
            jQuery("#totalBalance").val(0);
        }

        if (!foo.stringIsPrice(jQuery("#totalBalance").val())) {
            alert("请输入正确的储值金额！");
            $("#saveBtn").removeAttr("disabled");
            return;
        }
        var balance = jQuery("#balance").html() * 1;
        var totalBalance = jQuery("#totalBalance").val() * 1;
        jQuery("#memberCardOrderItemDTOs0\\.worth").val((totalBalance - balance).toFixed(2));

        //服务检查是否相同
        if (jQuery(".item").size() >= 1)
            if (checkTheSame()) {
                alert("服务有重复内容或为空，请修改或删除。");
                $("#saveBtn").removeAttr("disabled");
                return;
            }

        var ischeck = checkServiceData(this);
        if (!ischeck && ischeck != null) {
            $("#saveBtn").removeAttr("disabled");
            return;
        }

        var nowDate = getNowDateOnlyNumber();
        var deadlineFlag = false;
        $(".checkDeadLine").each(function () {
            if ($("#" + this.id.split(".")[0] + "\\.serviceName").val()) {
                var deadline = $(this).val();

                if ("不限期" != deadline) {
                    deadline = $(this).val().replace(/[^\d]+/g, "");
                    if (deadline * 1 < nowDate * 1) {
                        deadlineFlag = true;
                        return false;
                    }
                }
            }

        });

        if (deadlineFlag) {
            if (confirm("有项目失效期小于当前时间，是否修改！")) {
                $("#saveBtn").removeAttr("disabled");
                return;
            }
        }

        if (jQuery("#password").val()) {
            jQuery("#checkPwd")[0].checked = true;
        }

        if (jQuery("#checkPwd")[0].checked == true && (null == jQuery("#password").val() || "" == jQuery("#password").val())) {
            alert("请填写密码");
            $("#saveBtn").removeAttr("disabled");
            return;
        }

        var discount = APP_BCGOGO.StringFilter.inputtingPriceFilter(dataTransition.rounding(jQuery("#discount").val(), 2), 2);
        if (null != discount && "" != discount && !foo.stringIsPrice(discount == 0.0 ? "0" : discount)) {
            alert("请填写正确的价格！");
            jQuery("#discount").val("");
            $("#saveBtn").removeAttr("disabled");
            return;
        }
        var total = jQuery("#total").val() * 1;
        var debt = APP_BCGOGO.StringFilter.inputtingPriceFilter(dataTransition.rounding(jQuery("#debt").val(), 2), 2);
        if (null != debt && "" != debt && !foo.stringIsPrice(debt == 0.0 ? "0" : debt)) {
            alert("请填写正确的价格！");
            jQuery("#debt").val("");
            $("#saveBtn").removeAttr("disabled");
            return;
        }
        var cash = APP_BCGOGO.StringFilter.inputtingPriceFilter(dataTransition.rounding(jQuery("#cash").val(), 2), 2);
        if (null != cash && "" != cash && !foo.stringIsPrice(cash == 0.0 ? "0" : cash)) {
            alert("请填写正确的价格！");
            jQuery("#cash").val("");
            $("#saveBtn").removeAttr("disabled");
            return;
        }
        var bankCard = APP_BCGOGO.StringFilter.inputtingPriceFilter(dataTransition.rounding(jQuery("#bankCard").val(), 2), 2);
        if (null != bankCard && "" != bankCard && !foo.stringIsPrice(bankCard == 0.0 ? "0" : bankCard)) {
            alert("请填写正确的价格！");
            jQuery("#bankCard").val("");
            $("#saveBtn").removeAttr("disabled");
            return;
        }
        var check = APP_BCGOGO.StringFilter.inputtingPriceFilter(dataTransition.rounding(jQuery("#check").val(), 2), 2);
        if (null != check && "" != check && !foo.stringIsPrice(check == 0.0 ? "0" : check)) {
            alert("请填写正确的价格！");
            jQuery("#check").val("");
            $("#saveBtn").removeAttr("disabled");
            return;
        }
        if (null == discount || "" == discount) {
            discount = 0;
        }
        if (null == debt || "" == debt) {
            debt = 0;
        }
        if (null == cash || "" == cash) {
            cash = 0;
        }
        if (null == bankCard || "" == bankCard) {
            bankCard = 0;
        }

        if (null == check || "" == check) {
            check = 0;
        }
        discount = discount * 1;
        debt = debt * 1;
        cash = cash * 1;
        bankCard = bankCard * 1;
        check = check * 1;
        if (debt > 0) {
            if (null == jQuery("#mobile").val() || "" == jQuery("#mobile").val() && jQuery("#flag").attr("isNoticeMobile") == "false") {
                jQuery("#inputMobile").show();
                $("#saveBtn").removeAttr("disabled");
                return;
            }
        }
        else {
            jQuery("#huankuanTime").val("");
        }
        $("#sendMemberMsg").attr("checked",$("#sendMsg").attr("checked"));
        if($("#sendMsg").attr("checked")) {
                if(!$.trim($("input[id$='mobile']",window.parent.document).val()) && jQuery("#flag").attr("isNoticeMobile") == "false") {
                    $("#inputMobileDiv").dialog("open");
                    $("#saveBtn").removeAttr("disabled");
                    return;
                }
        }
        if (validateInfo()) {
            $("#memberCardOrderForm").ajaxSubmit(function (data) {
                var jsonObj = JSON.parse(data);

                if (jsonObj.resu == "success") {
                    alert("购卡成功");
                    if ($("#pageName", window.parent.document) && $("#pageName", window.parent.document).val() == "RepairOrder") {
                        $("#gouka", window.parent.document).text("续卡");
                    }
                    var orderId = jsonObj.orderId;
                    //如果打印复选框被选中就调用打印
                    if (jQuery("#print")[0].checked == true) {
                        jQuery("#memberCardOrderForm").attr("action", "member.do?method=printMemberOrder");
                        window.open("member.do?method=printMemberOrder&orderId=" + orderId + "&now=" + new Date(), "", "width=250px,height=768px");
                    }
                    closeWindow();
                    //施工单不能用刷新只能ajax重新查信息，这个id在父页面，click就是用来ajax请求信息的
                    if (null != window.parent.document.getElementById("callBackBuyCard")) {
                        jQuery("#callBackBuyCard", parent.document).click();
                        if ($("#mobile").val()) {
                            window.parent.document.getElementById("mobile").value = $("#mobile").val();
                        }
                    } else if (null != window.parent.document.getElementById("isUncleUser")) {
                        window.parent.document.location = "unitlink.do?method=customer&customerId=" + $("#customerId").val();
                    } else {
                        if (jQuery("#id", window.parent.document).val() != "" && jQuery("#id", window.parent.document).val() != 'undefined') {
                            window.parent.document.location = "washBeauty.do?method=getWashBeautyOrder&washBeautyOrderId=" + jQuery("#id", window.parent.document).val() + "&receiptNo" +
                                jQuery("#receiptNo", window.parent.document).val();
                        } else {
                            window.parent.ajaxToUpdateMemberInfoForCarWash(jsonObj.memberNo);
                        }
                    }
                }
                else if (jsonObj.resu == "error") {
                    alert("购卡失败");
                    $("#saveBtn").removeAttr("disabled");
                    closeWindow();
                }

            });
        }
    });

    jQuery("#debt").live("blur", function () {
        var discount = jQuery("#discount").val();
        var total = jQuery("#total").val() * 1;
        var debt = jQuery("#debt").val();
        var cash = jQuery("#cash").val();
        var bankCard = jQuery("#bankCard").val();
        var check = jQuery("#check").val();
        if (null == discount || "" == discount) {
            discount = 0;
        }
        if (null == debt || "" == debt) {
            debt = 0;
        }
        if (null == cash || "" == cash) {
            cash = 0;
        }
        if (null == bankCard || "" == bankCard) {
            bankCard = 0;
        }

        if (null == check || "" == check) {
            check = 0;
        }
        discount = discount * 1;
        debt = debt * 1;
        cash = cash * 1;
        bankCard = bankCard * 1;
        check = check * 1;

        if (debt > total) {
            jQuery("#debt").val(total.toFixed(2));
            jQuery("#discount").val("");
            clear();
        }

        if (debt == total) {
            clear();
            jQuery("#discount").val("");
        }

        if (debt < total) {
            if (null != jQuery("#discount").val() && "" != jQuery("#discount").val()) {
                discount = (total - debt) > discount ? discount : (total - debt);
                jQuery("#discount").val(discount.toFixed(2));
            }

            if (cash + bankCard + check == 0) {
                jQuery("#cash").val((total - discount - debt).toFixed(2));
                jQuery("#bankCard").val("");
                jQuery("#check").val("");
                jQuery("#checkNo").val("");

                if (jQuery("#cash").val() * 1 == 0) {
                    jQuery("#cash").val("");
                }
            }

            if (cash + bankCard + check > 0) {
                if (cash > 0) {
                    jQuery("#cash").val((total - discount - debt).toFixed(2));
                    jQuery("#bankCard").val("");
                    jQuery("#check").val("");
                    jQuery("#checkNo").val("");
                }
                else if (bankCard > 0) {
                    jQuery("#cash").val("");
                    jQuery("#bankCard").val((total - discount - debt).toFixed(2));
                    jQuery("#check").val("");
                    jQuery("#checkNo").val("");
                }
                else {
                    jQuery("#cash").val("");
                    jQuery("#bankCard").val("");
                    jQuery("#check").val((total - discount - debt).toFixed(2));
                }
            }
        }

        cash = jQuery("#cash").val();
        bankCard = jQuery("#bankCard").val();
        check = jQuery("#check").val();
        if (null == cash || "" == cash) {
            cash = 0;
        }
        if (null == bankCard || "" == bankCard) {
            bankCard = 0;
        }

        if (null == check || "" == check) {
            check = 0;
        }
        cash = cash * 1;
        bankCard = bankCard * 1;
        check = check * 1;
        jQuery("#settledAmount").val((cash + bankCard + check).toFixed(2));
    });

    jQuery("#discount").live("blur", function () {
        var discount = jQuery("#discount").val();
        var total = jQuery("#total").val() * 1;
        var debt = jQuery("#debt").val();
        var cash = jQuery("#cash").val();
        var bankCard = jQuery("#bankCard").val();
        var check = jQuery("#check").val();
        if (null == discount || "" == discount) {
            discount = 0;
        }
        if (null == debt || "" == debt) {
            debt = 0;
        }
        if (null == cash || "" == cash) {
            cash = 0;
        }
        if (null == bankCard || "" == bankCard) {
            bankCard = 0;
        }

        if (null == check || "" == check) {
            check = 0;
        }
        discount = discount * 1;
        debt = debt * 1;
        cash = cash * 1;
        bankCard = bankCard * 1;
        check = check * 1;

        if (discount > total) {
            jQuery("#discount").val(total.toFixed(2));
            jQuery("#debt").val("");
            clear();
        }

        if (discount == total) {
            clear();
            jQuery("#debt").val("");
        }

        if (discount < total) {
            if (null != jQuery("#debt").val() && "" != jQuery("#debt").val()) {
                debt = (total - discount) > debt ? debt : (total - discount);
                jQuery("#debt").val(debt.toFixed(2));
            }

            if (cash + bankCard + check == 0) {
                jQuery("#cash").val((total - discount - debt).toFixed(2));
                jQuery("#bankCard").val("");
                jQuery("#check").val("");
                jQuery("#checkNo").val("");

                if (jQuery("#cash").val() * 1 == 0) {
                    jQuery("#cash").val("");
                }
            }

            if (cash + bankCard + check > 0) {
                if (cash > 0) {
                    jQuery("#cash").val((total - discount - debt).toFixed(2));
                    jQuery("#bankCard").val("");
                    jQuery("#check").val("");
                    jQuery("#checkNo").val("");
                }
                else if (bankCard > 0) {
                    jQuery("#cash").val("");
                    jQuery("#bankCard").val((total - discount - debt).toFixed(2));
                    jQuery("#check").val("");
                    jQuery("#checkNo").val("");
                }
                else {
                    jQuery("#cash").val("");
                    jQuery("#bankCard").val("");
                    jQuery("#check").val((total - discount - debt).toFixed(2));
                }
            }
        }

        cash = jQuery("#cash").val();
        bankCard = jQuery("#bankCard").val();
        check = jQuery("#check").val();
        if (null == cash || "" == cash) {
            cash = 0;
        }
        if (null == bankCard || "" == bankCard) {
            bankCard = 0;
        }

        if (null == check || "" == check) {
            check = 0;
        }
        cash = cash * 1;
        bankCard = bankCard * 1;
        check = check * 1;
        jQuery("#settledAmount").val((cash + bankCard + check).toFixed(2));
    });

    jQuery("#cash,#bankCard,#check").live("blur", function () {
        var cash = jQuery("#cash").val();
        var bankCard = jQuery("#bankCard").val();
        var check = jQuery("#check").val();
        if (null == cash || "" == cash) {
            cash = 0;
        }
        if (null == bankCard || "" == bankCard) {
            bankCard = 0;
        }

        if (null == check || "" == check) {
            check = 0;
        }
        cash = cash * 1;
        bankCard = bankCard * 1;
        check = check * 1;
        jQuery("#settledAmount").val((cash + bankCard + check).toFixed(2));
    });

    jQuery("#settledAmount").live("blur", function () {
        var discount = jQuery("#discount").val();
        var total = jQuery("#total").val() * 1;
        var debt = jQuery("#debt").val();
        if (null == discount || "" == discount) {
            discount = 0;
        }
        if (null == debt || "" == debt) {
            debt = 0;
        }

        discount = discount * 1;
        debt = debt * 1;

        if (jQuery("#settledAmount").val() * 1 == 0) {
            jQuery("#selectBtn").show();
        }
    });

    jQuery("#discount,#debt,#cash,#bankCard,#check").live("dblclick", function () {
        jQuery(this).val(jQuery("#total").val());
        if (this == jQuery("#discount")[0]) {
            jQuery("#debt,#cash,#bankCard,#check,#checkNo").val("");
            jQuery("#settledAmount").val(0);
        }
        if (this == jQuery("#debt")[0]) {
            jQuery("#discount,#cash,#bankCard,#check,#checkNo").val("");
            jQuery("#settledAmount").val(0);
        }
        if (this == jQuery("#cash")[0]) {
            jQuery("#discount,#debt,#bankCard,#check,#checkNo").val("");
            jQuery("#settledAmount").val(jQuery("#total").val());
        }
        if (this == jQuery("#bankCard")[0]) {
            jQuery("#discount,#cash,#debt,#check,#checkNo").val("");
            jQuery("#settledAmount").val(jQuery("#total").val());
        }
        if (this == jQuery("#check")[0]) {
            jQuery("#discount,#cash,#bankCard,#debt").val("");
            jQuery("#settledAmount").val(jQuery("#total").val());
        }
    });

    jQuery(".timesStatus1").live("click", function () {
        jQuery(this).next().attr("readOnly", false);
    });

    jQuery(".timesStatus2").live("click", function () {
        jQuery(this).parent().prev(0).val("");
        jQuery(this).parent().prev(0).attr("readOnly", true);
    });

    jQuery(".deadlineStatus2").live("click", function () {
        jQuery(this).parent().prev().val("");
        jQuery(this).parent().prev(0).attr("readOnly", "true");
    });

    jQuery(".img").live("click", function () {
        var id = jQuery(this).prev()[0].id;
        if (jQuery(this).next().children(0)[0].checked == true) {
            return;
        }
        WdatePicker({el: id, dateFmt: 'yyyy-MM-dd'});
    });

});

//实收为0，选择挂账
function selectDebt() {
    var discount = jQuery("#discount").val();
    var total = jQuery("#total").val() * 1;
    var debt = jQuery("#debt").val();
    if (null == discount || "" == discount) {
        discount = 0;
    }
    if (null == debt || "" == debt) {
        debt = 0;
    }

    discount = discount * 1;
    debt = debt * 1;
    clear();
    jQuery("#debt").val((total - discount).toFixed(2));

    jQuery("#selectBtn").hide();
}

//实收为0，选择优惠
function selectDiscount() {
    var discount = jQuery("#discount").val();
    var total = jQuery("#total").val() * 1;
    var debt = jQuery("#debt").val();
    if (null == discount || "" == discount) {
        discount = 0;
    }
    if (null == debt || "" == debt) {
        debt = 0;
    }

    discount = discount * 1;
    debt = debt * 1;
    clear();
    jQuery("#discount").val((total - debt).toFixed(2));

    jQuery("#selectBtn").hide();
}

//清除：挂账、现金、银行、支票为空，实收=0
function clear() {
    jQuery("#cash").val("");
    jQuery("#bankCard").val("");
    jQuery("#check").val("");
    jQuery("#checkNo").val("");
    jQuery("#settledAmount").val(0);
}

function handleMoneyInputEmpty() {
    if (jQuery("#discount").val() == null || jQuery("#discount").val() == "") {
        jQuery("#discount").val(0);
    }
    if (jQuery("#debt").val() == null || jQuery("#debt").val() == "") {
        jQuery("#debt").val(0);
    }
    if (jQuery("#cash").val() == null || jQuery("#cash").val() == "") {
        jQuery("#cash").val(0);
    }
    if (jQuery("#bankCard").val() == null || jQuery("#bankCard").val() == "") {
        jQuery("#bankCard").val(0);
    }
    if (jQuery("#check").val() == null || jQuery("#check").val() == "") {
        jQuery("#check").val(0);
    }
    if (jQuery("#settledAmount").val() == null || jQuery("#settledAmount").val() == "") {
        jQuery("#settledAmount").val(0);
    }
}

function closeWindow() {
    jQuery(window.parent.document).find("#mask").hide();
    jQuery(window.parent.document).find("#iframe_buyCard").hide();
    $("#memberCardId", parent.document).val("");
    if ($("#doMemberCard", window.parent.document)[0]) {
        $("#doMemberCard", window.parent.document).removeAttr("disabled");

    }

    if ($("#gouka", window.parent.document)[0]) {
        $("#gouka", window.parent.document).removeAttr("disabled");

    }

    try {
        $(window.parent.document.body).find("input[type='button']").eq(0).focus().blur();
    } catch (e) {
        ;
    }
}

function checkThisServiceDom(tn, idstr) {
    if (document.getElementById("memberCardOrderServiceDTOs" + tn + "." + idstr)) {
        return true;
    }
    return false;
}

//判断是否显示+按钮
function isShowAddButton() {
    //如果初始化的话就默认加一行
    if (jQuery(".item").size() <= 0) {
        jQuery(".opera2").trigger("click");
    }
    jQuery(".item .opera2").remove();
    var opera1Id = jQuery(".item:last").find("td:last>input[class='opera1']").attr("id");
    if (opera1Id == null || opera1Id == "") {
        return;
    }
}


function setCursorPosition(ctrl, pos) {//设置光标位置函数
    if (ctrl.type != "text") {
        return;
    }
    if (ctrl.setSelectionRange) {
        ctrl.focus();
        ctrl.setSelectionRange(pos, pos);
    }
    else if (ctrl.createTextRange) {
        var range = ctrl.createTextRange();
        range.collapse(true);
        range.moveEnd('character', pos);
        range.moveStart('character', pos);
        range.select();
    }
}

function getCursorPosition(ctrl) {//获取光标位置函数
    var CaretPos = 0;
    // IE Support
    if (ctrl.type != "text") {
        return;
    }
    if (document.selection) {
        ctrl.focus();
        var Sel = document.selection.createRange();
        Sel.moveStart('character', -ctrl.value.length);
        CaretPos = Sel.text.length;
    }
    // Firefox support
    else if (ctrl.selectionStart != NaN || ctrl.selectionStart == '0')
        CaretPos = ctrl.selectionStart;
    return (CaretPos);
}

//检查服务是否相同
function checkTheSame() {
    var trs = jQuery(".item");
    if (!trs)
        return false;
    if (trs.length < 2)
        return false;
    var s = '';

    //先获取最后一个
    var cur = '';//当前最后添加的一条记录
    for (var i = trs.length - 1; i >= 0; i--) {
        var inputs = trs[i].getElementsByTagName("input");
        if (!inputs)
            continue;
        var index = inputs[0].name.split(".")[0].substring(inputs[0].name.indexOf('[') + 1, inputs[0].name.indexOf(']'));

        if (i == trs.length - 1) {
//            最后添加的一个
            cur += document.getElementById("memberCardOrderServiceDTOs" + index + ".serviceName").value + ",";

        } else {
            var older = '';
            older += document.getElementById("memberCardOrderServiceDTOs" + index + ".serviceName").value + ",";
            if (cur == older) {
                return true;
            }
        }

    }
    return false;
}

function checkServiceData(domObj) {
    var foo = APP_BCGOGO.Validator;
    var flag = true;
    var msg = ""
    jQuery(".addService").each(function (i) {
        var idPrefix = jQuery(this)[0].id.split(".")[0];
        var serviceName = document.getElementById(idPrefix + ".serviceName").value;
        var balanceTimes = document.getElementById(idPrefix + ".balanceTimes").value;
        var deadlineStr = document.getElementById(idPrefix + ".deadlineStr").value;
        var vehicles = document.getElementById(idPrefix + ".vehicles").value;
        try {
//            console.log(serviceName);
//            console.log(balanceTimes);
//            console.log(deadlineStr);
//            console.log(vehicles);
        } catch (e) {
            ;
        }

        if (jQuery(".addService").size() > 1) {
            if (serviceName == null || serviceName == "") {
                msg = "请选择服务！";
            }

            if ((balanceTimes == null || balanceTimes == "")) {
                msg = msg + "请填写次数！";
            }
            else {

            }

            if (!deadlineStr) {
                msg = msg + "请填写失效日期！";
            }

//      balanceTimes = APP_BCGOGO.StringFilter.naturalFilter(balanceTimes);

            try {
                console.log("balanceTimes  :  " + balanceTimes);
            } catch (e) {
                ;
            }

            if ("不限次" != balanceTimes) {
                if (!foo.stringIsNatualNumber(balanceTimes.toString())) {
                    msg = msg + "请填写正确的次数！";
                }
            }

            if (msg != "") {
                alert(msg);
                msg == "";
                flag = false;
                return false;
            }
        }
        else if (jQuery(".addService").size() == 1 && serviceName) {
            if ((balanceTimes == null || balanceTimes == "")) {
                msg = msg + "请填写次数！";
            }
            else {

            }

            if (!deadlineStr) {
                msg = msg + "请填写失效日期！";
            }

//      balanceTimes = APP_BCGOGO.StringFilter.naturalFilter(balanceTimes);

            try {
                console.log("balanceTimes  :  " + balanceTimes);
            } catch (e) {
                ;
            }

            if ("不限次" != balanceTimes) {
                if (!foo.stringIsNatualNumber(balanceTimes.toString())) {
                    msg = msg + "请填写正确的次数！";
                }
            }

            if (msg != "") {
                alert(msg);
                msg == "";
                flag = false;
                return false;
            }
        }


    });
    return flag;
}


function checkAddServiceData(domObj) {
    var foo = APP_BCGOGO.Validator;
    var flag = true;
    var msg = ""
    jQuery(".addService").each(function (i) {
        var idPrefix = jQuery(this)[0].id.split(".")[0];
        var serviceName = document.getElementById(idPrefix + ".serviceName").value;
        var balanceTimes = document.getElementById(idPrefix + ".balanceTimes").value;
        var deadlineStr = document.getElementById(idPrefix + ".deadlineStr").value;
        var vehicles = document.getElementById(idPrefix + ".vehicles").value;
        try {
//            console.log(serviceName);
//            console.log(balanceTimes);
//            console.log(deadlineStr);
//            console.log(vehicles);
        } catch (e) {
            ;
        }

        if (jQuery(".addService").size() > 1) {
            if (serviceName == null || serviceName == "") {
                msg = "请选择服务！";
            }

            if ((balanceTimes == null || balanceTimes == "")) {
                msg = msg + "请填写次数！";
            }
            else {

            }

            if (!deadlineStr) {
                msg = msg + "请填写失效日期！";
            }

//      balanceTimes = APP_BCGOGO.StringFilter.naturalFilter(balanceTimes);

            try {
                console.log("balanceTimes  :  " + balanceTimes);
            } catch (e) {
                ;
            }

            if ("不限次" != balanceTimes) {
                if (!foo.stringIsNatualNumber(balanceTimes.toString())) {
                    msg = msg + "请填写正确的次数！";
                }
            }


            if (msg != "") {
                alert(msg);
                msg == "";
                flag = false;
                return false;
            }
        }
        else if (jQuery(".addService").size() == 1) {
            if (!serviceName) {
                msg = msg + "请填写服务名称！";
            }

            if ((balanceTimes == null || balanceTimes == "")) {
                msg = msg + "请填写次数！";
            }

            if (!deadlineStr) {
                msg = msg + "请填写失效日期！";
            }

//      balanceTimes = APP_BCGOGO.StringFilter.naturalFilter(balanceTimes);

            try {
                console.log("balanceTimes  :  " + balanceTimes);
            } catch (e) {
                ;
            }

            if ("不限次" != balanceTimes) {
                if (!foo.stringIsNatualNumber(balanceTimes.toString())) {
                    msg = msg + "请填写正确的次数！";
                }
            }

            if (msg != "") {
                alert(msg);
                msg == "";
                flag = false;
                return false;
            }
        }


    });
    return flag;
}

function checkServiceDataNoMessage(domObj) {
    var foo = APP_BCGOGO.Validator;
    var idPrefix = domObj.id.split(".")[0];
    if (idPrefix == "" || idPrefix == null) {
        return null;
    }
    var serviceName = document.getElementById(idPrefix + ".serviceName").value;
    var times = document.getElementById(idPrefix + ".times").value;
    var term = document.getElementById(idPrefix + ".term").value;
    var balanceTimes = document.getElementById(idPrefix + ".balanceTimes").value;

    if (serviceName == null || serviceName == "") {
        return false;
    }

    if (times == null || times == "") {
        return false;
    }

    if (term == null || term == "") {
        return false;
    }

    times = APP_BCGOGO.StringFilter.naturalFilter(times);

    if ("不限次" != balanceTimes) {
        if (!foo.stringIsNatualNumber(balanceTimes.toString())) {
            return false;
        }
    }

}

function inputMobile() {
    var foo = APP_BCGOGO.Validator;
    if (null != jQuery("#mobile").val() && "" != jQuery("#mobile").val() && !foo.stringIsMobilePhoneNumber(jQuery("#mobile").val())) {
        alert("请输入正确的手机号");
        jQuery("#mobile").val("").select().focus();
        return;
    }
    var url = "member.do?method=checkMobileDifferentCustomer";
    var isMobileExist = false;
    APP_BCGOGO.Net.syncAjax({url: url, dataType: "json", data: {customerId: $("#customerId").val(), mobile: $("#mobile").val()}, success: function (json) {
        if ("hasCustomer" == json.resu) {
            alert("拥有此手机的用户已存在，请重新填写");
            isMobileExist = true;
        }
        else if ("hasCustomerGtOne" == json.resu) {
            alert("系统中此手机号已有多个用户，请重新填写");
            isMobileExist = true;
        }
    }});

    if (isMobileExist) {
        return;
    }

    jQuery("#flag").attr("isNoticeMobile", true);
    jQuery("#inputMobile").hide();
}

function cancleInputMobile() {
    jQuery("#mobile").val("");
    jQuery("#flag").attr("isNoticeMobile", true);
    jQuery("#inputMobile").hide();
}

function validateMemberNo() {
    var flag = true;
    var memberNo = jQuery("#memberNo").val();
    var oldMemberNo = jQuery("#oldMemberNo").val()
    if (memberNo != oldMemberNo) {
        jQuery.ajax({
            type: "POST",
            url: "member.do?method=checkMemberNo",
            async: false,
            data: {
                memberNo: memberNo,
                tsLog: 10000000000 * (1 + Math.random())
            },
            cache: false,
            dataType: "json",
            success: function (jsonObject) {
                var tmp = jsonObject.resu;
                if (tmp == "error") {
                    flag = false;
                }
            }
        });
    }
    return flag;
}

function getNowDateOnlyNumber() {
    var year = new Date().getFullYear();
    var month = new Date().getMonth() + 1;
    var day = new Date().getDate();
    var nowDate = "" + year + (month.toString().length == 1 ? ("0" + month) : month) + (day.toString().length == 1 ? ("0" + day) : day);
    return nowDate;
}

function validateInfo() {
    var discount = APP_BCGOGO.StringFilter.inputtingPriceFilter(dataTransition.rounding(jQuery("#discount").val(), 2), 2);
    if (null != discount && "" != discount && !foo.stringIsPrice(discount == 0.0 ? "0" : discount)) {
        alert("请填写正确的价格！");
        jQuery("#discount").val("");
        $("#saveBtn").removeAttr("disabled");
        return false;
    }
    var total = jQuery("#total").val() * 1;
    var debt = APP_BCGOGO.StringFilter.inputtingPriceFilter(dataTransition.rounding(jQuery("#debt").val(), 2), 2);
    if (null != debt && "" != debt && !foo.stringIsPrice(debt == 0.0 ? "0" : debt)) {
        alert("请填写正确的价格！");
        jQuery("#debt").val("");
        $("#saveBtn").removeAttr("disabled");
        return false;
    }
    var cash = APP_BCGOGO.StringFilter.inputtingPriceFilter(dataTransition.rounding(jQuery("#cash").val(), 2), 2);
    if (null != cash && "" != cash && !foo.stringIsPrice(cash == 0.0 ? "0" : cash)) {
        alert("请填写正确的价格！");
        jQuery("#cash").val("");
        $("#saveBtn").removeAttr("disabled");
        return false;
    }
    var bankCard = APP_BCGOGO.StringFilter.inputtingPriceFilter(dataTransition.rounding(jQuery("#bankCard").val(), 2), 2);
    if (null != bankCard && "" != bankCard && !foo.stringIsPrice(bankCard == 0.0 ? "0" : bankCard)) {
        alert("请填写正确的价格！");
        jQuery("#bankCard").val("");
        $("#saveBtn").removeAttr("disabled");
        return false;
    }
    var check = APP_BCGOGO.StringFilter.inputtingPriceFilter(dataTransition.rounding(jQuery("#check").val(), 2), 2);
    if (null != check && "" != check && !foo.stringIsPrice(check == 0.0 ? "0" : check)) {
        alert("请填写正确的价格！");
        jQuery("#check").val("");
        $("#saveBtn").removeAttr("disabled");
        return false;
    }
    if (null == discount || "" == discount) {
        discount = 0;
    }
    if (null == debt || "" == debt) {
        debt = 0;
    }
    if (null == cash || "" == cash) {
        cash = 0;
    }
    if (null == bankCard || "" == bankCard) {
        bankCard = 0;
    }

    if (null == check || "" == check) {
        check = 0;
    }
    discount = discount * 1;
    debt = debt * 1;
    cash = cash * 1;
    bankCard = bankCard * 1;
    check = check * 1;
    if (debt > 0) {
        if (null == jQuery("#mobile").val() || "" == jQuery("#mobile").val() && jQuery("#flag").attr("isNoticeMobile") == "false") {
            jQuery("#inputMobile").show();
            $("#saveBtn").removeAttr("disabled");
            return false;
        }
    }
    else {
        jQuery("#huankuanTime").val("");
    }
    if (jQuery("#total").val() != 0) {
        if (GLOBAL.Number.equalsTo(jQuery("#settledAmount").val(), 0) && !GLOBAL.Number.equalsTo(discount + debt, total)) {
            jQuery("#selectBtn").show();
            $("#saveBtn").removeAttr("disabled");
            return false;
        }

        if (GLOBAL.Number.equalsTo(jQuery("#settledAmount").val(), 0) && GLOBAL.Number.equalsTo(discount + debt, total)) {
            if (!confirm("实收为0，请再次确认是否挂账或优惠赠送。")) {
                $("#saveBtn").removeAttr("disabled");
                return false;
            }
        }

        if (!GLOBAL.Number.equalsTo(jQuery("#settledAmount").val(), cash + bankCard + check)) {
            alert("实收金额与现金、银行或支票的金额不符，请修改。如果挂账或优惠赠送，请输入0。");
            $("#saveBtn").removeAttr("disabled");
            return false;
        }
        if (GLOBAL.Number.equalsTo(jQuery("#settledAmount").val(), cash + bankCard + check)
            && !GLOBAL.Number.equalsTo(jQuery("#settledAmount").val(), total - discount - debt)) {
            alert("实收金额与优惠、挂账金额不符合，请修改。");
            $("#saveBtn").removeAttr("disabled");
            return false;
        }

        if (GLOBAL.Number.equalsTo(jQuery("#settledAmount").val(), cash + bankCard + check)
            && GLOBAL.Number.equalsTo(jQuery("#settledAmount").val(), total - discount - debt)) {

            var msg = "本次结算应收：" + total + "元"
            if (discount > 0) {
                msg += ",优惠：" + discount + "元";
            }
            if (debt > 0) {
                msg += ",挂账" + debt + "元";
            }
            if (jQuery("#settledAmount").val() > 0) {
                msg += "\n\n";
                msg += "实收：" + jQuery('#settledAmount').val() + "元(";
                msg += cash > 0 ? "现金：" + cash + "元," : "";
                msg += bankCard > 0 ? "银行卡：" + bankCard + "元," : "";
                msg += check > 0 ? "支票：" + check + "元)" : ")";
                if (msg.substring(msg.length - 2, msg.length - 1) == ',') {
                    msg = msg.substring(0, msg.length - 2) + ")";
                }
            }

            if (!confirm(msg)) {
                $("#saveBtn").removeAttr("disabled");
                return false;
            }
        }
    }

    if (null != jQuery("#mobile").val() && "" != jQuery("#mobile").val() &&
        !foo.stringIsMobilePhoneNumber(jQuery("#mobile").val())  && jQuery("#flag").attr("isNoticeMobile") == "false") {
        alert("请输入正确的手机号");
        jQuery("#mobile").val("").select().focus();
        $("#saveBtn").removeAttr("disabled");
        return false;
    }

    handleMoneyInputEmpty();

    $("input[id$='balanceTimes']").each(function () {
        if ($(this).val() == "不限次") {
            $(this).val(-1);
        }
    });
    return true;

}

function submitAfterInputMobile() {
    if (validateInfo()) {
        jQuery("#memberCardOrderForm").ajaxSubmit(function (data) {
            var jsonObj = JSON.parse(data);

            if (jsonObj.resu == "success") {
                alert("购卡成功");
                if ($("#pageName", window.parent.document) && $("#pageName", window.parent.document).val() == "RepairOrder") {
                    $("#gouka", window.parent.document).text("续卡");
                }
                var orderId = jsonObj.orderId;
                //如果打印复选框被选中就调用打印
                if (jQuery("#print")[0].checked == true) {
                    jQuery("#memberCardOrderForm").attr("action", "member.do?method=printMemberOrder");
                    window.open("member.do?method=printMemberOrder&orderId=" + orderId + "&now=" + new Date(), "", "width=250px,height=768px");
                }
                closeWindow();
                //施工单不能用刷新只能ajax重新查信息，这个id在父页面，click就是用来ajax请求信息的
                if (null != window.parent.document.getElementById("callBackBuyCard")) {
                    jQuery("#callBackBuyCard", parent.document).click();
                    if ($("#mobile").val()) {
                        window.parent.document.getElementById("mobile").value = $("#mobile").val();
                    }
                } else if (null != window.parent.document.getElementById("isUncleUser")) {
                    window.parent.document.location = "unitlink.do?method=customer&customerId=" + $("#customerId").val();
                } else {//洗车单
                    window.parent.ajaxToUpdateMemberInfoForCarWash(jsonObj.memberNo);
                }
            } else if (jsonObj.resu == "error") {
                alert("购卡失败");
                $("#saveBtn").removeAttr("disabled");
                closeWindow();
            }

        });
    }
}