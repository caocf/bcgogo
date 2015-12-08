;
$(function () {
    $("#customerSearchWord")
        .bind("click focus keyup", function (event) {
            if (G.contains(G.keyNameFromEvent(event), ["up", "down", "left", "right"])) {
                return;
            }

            var $customer = $(this);
            var $hiddenCustomerIds = $("#customerIds");
            var uuid = GLOBAL.Util.generateUUID();
            var searchWord = $customer.val();
            var ajaxUrl = "customer.do?method=getCustomerOrSupplierSuggestion";
            var ajaxData = {
                searchWord: searchWord,
                customerOrSupplier: "customer",
                uuid: uuid
            };
            var dropList = APP_BCGOGO.Module.droplist;
            dropList.setUUID(uuid);
            APP_BCGOGO.wjl.LazySearcher.lazySearch(ajaxUrl, ajaxData, function (result) {
                var drawData = {};
                var data = [];
                if (!G.Lang.isEmpty(result)) {
                    $.each(result, function (index, value) {
                        if (!G.Lang.isEmpty(value) && !G.Lang.isEmpty(value))
                            var suggestionEntry = value["suggestionEntry"];
                        var customerDetail = {};
                        $.each(value["suggestionEntry"], function (itemIndex, itemValue) {
                            customerDetail[itemValue[0]] = itemValue[1];
                        });
                        var customerName = G.Lang.normalize(customerDetail["name"]);
                        var customerMobile = G.Lang.normalize(customerDetail["mobile"]);
                        var label = "";
                        if (!G.Lang.isEmpty(customerName)) {
                            label = customerName;
                        }
                        if (!G.Lang.isEmpty(label) && !G.Lang.isEmpty(customerMobile)) {
                            label += "+";
                        }
                        if (!G.Lang.isEmpty(customerMobile)) {
                            label += customerMobile;
                        }
                        var item = {
                            details: customerDetail,
                            label: label
                        };
                        data.push(item);
                    });
                }
                drawData["data"] = data;
                drawData["uuid"] = uuid;
                dropList.show({
                    "selector": $customer,
                    "data": drawData,
                    "onSelect": function (event, index, data) {
                        if (!G.Lang.isEmpty(data) && !G.Lang.isEmpty(data.details)) {
                            var selectCustomerName = data.details["name"];
                            $customer.val(selectCustomerName);
                            var customerId = G.Lang.normalize(data.details["id"]);
                            $hiddenCustomerIds.val(customerId);
                        }
                        $customer.removeAttr("onKeyboardSelect");
                        dropList.hide();
                    }, "onKeyboardSelect": function (event, index, data) {
                        if (!G.Lang.isEmpty(data) && !G.Lang.isEmpty(data.details)) {
                            var selectCustomerName = G.Lang.normalize(data.details["name"]);
                            $customer.val(selectCustomerName);
                            $customer.attr("onKeyboardSelect", true);
                            var customerId = G.Lang.normalize(data.details["id"]);
                            $("#customerIds").val(customerId);
                        } else {
                            $customer.removeAttr("onKeyboardSelect");
                            $hiddenCustomerIds.val("");
                        }
                    }
                });
            });
        })
        .bind("focus",function () {
            $(this).attr("lastVal", $(this).val());
        }).bind("blur", function () {
            if ($(this).val() != $(this).attr("lastVal")) {
                $("#customerIds").val("");
            }
        });

    dateUtil.bindPairDatePicker($("#enquiryTimeStartStr"),$("#enquiryTimeEndStr"));
    dateUtil.bindPairDatePicker($("#responseTimeStartStr"),$("#responseTimeEndStr"));

    $("#receiptNo").bind("keyup",function(){
      var $this = $(this);
      var thisVal = $this.val();
        var pos = APP_BCGOGO.StringFilter.getCursorPosition(this, APP_BCGOGO.StringFilter.inputtingReceiptNoFilter);
        $this.val(APP_BCGOGO.StringFilter.inputtingReceiptNoFilter(thisVal));
        APP_BCGOGO.StringFilter.setCursorPosition(this, pos);
    }).bind("blur",function(){
            var $this = $(this);
            var thisVal = $this.val();
            $this.val(APP_BCGOGO.StringFilter.inputtingReceiptNoFilter(thisVal));
        });

    $("#clearSearchCondition").bind("click",function(){
        clearSearchCondition();
    });

    $("#searchEnquiryOrderBtn").bind("click",function(){
        searchEnquiryOrder();
    });

    $(".J_to_enquiryDetail").live("click", function () {
        var orderPrefix = $(this).attr("id").split(".")[0];
        var orderId = $("#" + orderPrefix + "\\.id").val();
        if (!G.Lang.isEmpty(orderId)) {
            window.location.href = "enquiry.do?method=showEnquiryDetail&enquiryId=" + orderId;
        }
    });

    var linkIdArray = ["toAllUnResponse", "toTodayUnResponse", "toBeforeTodayUnResponse", "toAllResponse",
        "toTodayResponse", "toBeforeTodayResponse"];
    var $responseStatuses = $("#responseStatuses");

    $.each(linkIdArray, function (index, value) {
        $("#" + value).bind("click", function () {
            clearSearchCondition();
            switch (value) {
                case "toAllUnResponse":
                    $responseStatuses.val("UN_RESPONSE");
                    break;
                case "toTodayUnResponse":
                    $responseStatuses.val("UN_RESPONSE");
                    $("#enquiryTimeStartStr").val(dateUtil.getToday());
                    break;
                case "toBeforeTodayUnResponse":
                    $responseStatuses.val("UN_RESPONSE");
                    $("#enquiryTimeEndStr").val(dateUtil.getYesterday());
                    break;
                case "toAllResponse":
                    $responseStatuses.val("RESPONSE");
                    break;
                case "toTodayResponse":
                    $responseStatuses.val("RESPONSE");
                    $("#responseTimeStartStr").val(dateUtil.getToday());
                    break;
                case "toBeforeTodayResponse":
                    $responseStatuses.val("RESPONSE");
                    $("#responseTimeEndStr").val(dateUtil.getYesterday());
                    break;
            }
            searchEnquiryOrder();
        });
    });
});

function clearSearchCondition() {
    $("#customerSearchWord").val("");
    $("#customerIds").val("");
    $("#receiptNo").val("");
    $("#enquiryTimeStartStr").val("");
    $("#enquiryTimeEndStr").val("");
    $("#responseTimeStartStr").val("");
    $("#responseTimeEndStr").val("");
    $("#responseStatuses").val("");
}

function searchEnquiryOrder(){
  var data = {};
    data["customerSearchWord"] = G.trim($("#customerSearchWord").val());
    data["receiptNo"] = G.trim($("#receiptNo").val());
    data["enquiryTimeStartStr"] = $("#enquiryTimeStartStr").val();
    data["enquiryTimeEndStr"] = $("#enquiryTimeEndStr").val();
    data["responseTimeStartStr"] = $("#responseTimeStartStr").val();
    data["responseTimeEndStr"] = $("#responseTimeEndStr").val();
    data["responseStatuses"] = $("#responseStatuses").val();
    data["maxRows"] = $("#maxRows").val();
    var $customerIds = $("#customerIds");
    if(!G.Lang.isEmpty($customerIds.val())){
        data["customerIds"] = $customerIds.val();
    }

    APP_BCGOGO.Net.syncPost({
        url: "enquiry.do?method=searchShopEnquiryList",
        dataType: "json",
        data: data,
        success: function (result) {
            drawEnquiryOrderList(result);
            initPage(result, "enquiryOrderList", "enquiry.do?method=searchShopEnquiryList", '', "drawEnquiryOrderList", '', '', data, '');
        },
        error: function () {
            nsDialog.jAlert("数据异常，请刷新页面！");
        }
    });
}

function drawEnquiryOrderList(result){
   var $enquiryListTb = $("#enquiryListTb");
    $enquiryListTb.find("tr:gt(1)").remove();
   var enquiryOrderDTOs = [];
    if(result && !G.Lang.isEmpty(result["data"])){
        enquiryOrderDTOs = result["data"];
    }
    if(G.Lang.isEmpty(enquiryOrderDTOs)){
        var emptyHtml = '<tr><td colspan="8">对不起，没有找到您要的单据信息!</td></tr>';
        $enquiryListTb.append(emptyHtml);
    } else {
        for (var i = 0; i < enquiryOrderDTOs.length; i++) {
            var html = '';
            var enquiryOrderDTO = enquiryOrderDTOs[i];
            var orderPrefix = "enquiryOrderDTOs" + i;
            html += '<tr class="titBody_Bg" id="' + orderPrefix + '.orderContent">';
            html += buildSingleEnquiryContentHtml(enquiryOrderDTO, orderPrefix);
            html += '</tr>';
            html += '<tr class="titBottom_Bg"><td colspan="10"></td></tr>';
            $enquiryListTb.append(html);
        }
    }
}

function buildSingleEnquiryContentHtml(orderDTO ,orderPrefix){
    if (G.Lang.isEmpty(orderDTO) || G.Lang.isEmpty(orderPrefix)) {
            return;
        }

    var id = G.Lang.normalize(orderDTO["idStr"], "");
    var receiptNo = G.Lang.normalize(orderDTO["receiptNo"], "暂无");
    var appUserName = G.Lang.normalize(orderDTO["appUserName"], "暂无");
    var vehicleNo = G.Lang.normalize(orderDTO["vehicleNo"], "暂无");  //车牌号
    var appUserMobile = G.Lang.normalize(orderDTO["appUserMobile"], "暂无");  //客户手机号
    var sendTimeStr = G.Lang.normalize(orderDTO["sendTimeStr"], "");  //询价时间
    var lastResponseTimeStr = G.Lang.normalize(orderDTO["lastResponseTimeStr"], "");  //报价时间
    var responseStatusStr = G.Lang.normalize(orderDTO["responseStatusStr"], "--");  //单据状态文案
    var responseStatus = G.Lang.normalize(orderDTO["responseStatus"], "");  //单据状态枚举



    var html = '';
    html += '<td style="padding-left:10px;">';
    html += '<a class="blue_color J_to_enquiryDetail" id="' + orderPrefix + '.receiptNo">' + receiptNo + '</a>';
    html += '<input type="hidden" id="' + orderPrefix + '.id" value="' + id + '">';
    html += '</td>';
    html += '<td>' + appUserName + '</td>';
    html += ' <td>' + vehicleNo + '</td>';
    html += ' <td>' + appUserMobile + '</td>';
    html += ' <td>' + sendTimeStr + '</td>';
    html += ' <td>' + lastResponseTimeStr + '</td>';
    html += ' <td>' + responseStatusStr + '</td>';
    html += ' <td>';

    if (responseStatus == "RESPONSE") {
        html += '<a class="blue_color J_to_enquiryDetail" id="' + orderPrefix + '.response">再次报价</a>&nbsp;';
    }else if (responseStatus == "UN_RESPONSE" ) {
        html += '<a class="blue_color J_to_enquiryDetail" id="' + orderPrefix + '.response">立即报价</a>&nbsp;';
    }
    html += '</td>';
    return html;
}


