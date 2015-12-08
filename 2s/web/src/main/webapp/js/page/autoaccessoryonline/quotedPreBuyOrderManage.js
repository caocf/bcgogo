/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 13-3-8
 * Time: 上午10:54
 * To change this template use File | Settings | File Templates.
 */
$(document).ready(function () {
    $("#startDate,#endDate")
        .datepicker({
            "numberOfMonths":1,
            "showButtonPanel":false,
            "changeYear":true,
            "showHour":false,
            "showMinute":false,
            "changeMonth":true,
            "yearRange":"c-100:c+100",
            "yearSuffix":""
        })
        .blur(function() {
            var startDate = $("#startDate").val();
            var endDate = $("#endDate").val();
            if (startDate == "" || endDate == "") return;
            if (APP_BCGOGO.Validator.stringIsZhCn(startDate) && APP_BCGOGO.Validator.stringIsZhCn(endDate)) {
                return;
            } else {
                if (startDate > endDate) {
                    $("#endDate").val(startDate);
                    $("#startDate").val(endDate);
                }
            }
        })
        .bind("click", function() {
            $(this).blur();
        })
        .change(function() {
            var startDate = $("#startDate").val();
            var endDate = $("#endDate").val();
            $(".good_his > .today_list").removeClass("hoverList");
            if (endDate == "" || startDate == "") {
                return;
            }
            if (APP_BCGOGO.Validator.stringIsZhCn(startDate) && APP_BCGOGO.Validator.stringIsZhCn(endDate)) {
                return;
            } else {
                if (startDate > endDate) {
                    $("#endDate").val(startDate);
                    $("#startDate").val(endDate);
                }
            }
        });

    $(":radio[name='vestDateRadio']").bind("click",function(e){
        $("#endDate").val(dateUtil.getToday());
        $("#startDate").val(dateUtil.getNDayCloseToday($(this).val()*-1));
    });

    $("#clearConditionBtn").bind('click', function () {
        $(".J_clear_input").val("");
        $(":radio").attr('checked',false);
        $(".J-initialCss").placeHolder("reset");
    });
    $(".J-productSuggestion")
        .bind('click', function () {
            productSuggestion($(this));
        })
        .bind('keyup', function (event) {
            var eventKeyCode = event.which || event.keyCode;
            if (GLOBAL.Interactive.keyNameFromKeyCode(eventKeyCode).search(/left|up|right|down/g) == -1) {
                productSuggestion($(this));
            }
        });

    function productSuggestion($domObject) {
        var searchWord = $domObject.val().replace(/[\ |\\]/g, "");
        var dropList = APP_BCGOGO.Module.droplist;
        dropList.setUUID(GLOBAL.Util.generateUUID());
        var currentSearchField =  $domObject.attr("searchField");
        var ajaxData = {
            searchWord: searchWord,
            searchField: currentSearchField,
            uuid: dropList.getUUID()
        };
        $domObject.prevAll(".J-productSuggestion").each(function(){
            var val = $(this).val().replace(/[\ |\\]/g, "");
            if($(this).attr("name")!="searchWord"){
                ajaxData[$(this).attr("name")] = val == $(this).attr("initialValue") ? "" : val;
            }
        });

        var ajaxUrl = "product.do?method=getProductSuggestion";
        APP_BCGOGO.wjl.LazySearcher.lazySearch(ajaxUrl, ajaxData, function (result) {
            if (currentSearchField == "product_info") {
                dropList.show({
                    "selector": $domObject,
                    "autoSet": false,
                    "data": result,
                    onGetInputtingData: function() {
                        var details = {};
                        $domObject.nextAll(".J-productSuggestion").each(function () {
                            var val = $(this).val().replace(/[\ |\\]/g, "");
                            details[$(this).attr("searchField")] = val == $(this).attr("initialValue") ? "" : val;
                        });
                        return {
                            details:details
                        };
                    },
                    onSelect: function (event, index, data, hook) {
                        $domObject.nextAll(".J-productSuggestion").each(function () {
                            var label = data.details[$(this).attr("searchField")];
                            if (G.Lang.isEmpty(label) && $(this).attr("initialValue")) {
                                $(this).val($(this).attr("initialValue"));
                                $(this).css({"color": "#ADADAD"});
                            } else {
                                $(this).val(G.Lang.normalize(label));
                                $(this).css({"color": "#000000"});
                            }
                        });
                        dropList.hide();
                        $("#searchConditionBtn").click();
                    },
                    onKeyboardSelect: function (event, index, data, hook) {
                        $domObject.nextAll(".J-productSuggestion").each(function () {
                            var label = data.details[$(this).attr("searchField")];
                            if (G.Lang.isEmpty(label) && $(this).attr("initialValue")) {
                                $(this).val($(this).attr("initialValue"));
                                $(this).css({"color": "#ADADAD"});
                            } else {
                                $(this).val(G.Lang.normalize(label));
                                $(this).css({"color": "#000000"});
                            }
                        });
                    }
                });
            }else{
                dropList.show({
                    "selector": $domObject,
                    "data": result,
                    "onSelect": function (event, index, data) {
                        $domObject.val(data.label);
                        $domObject.css({"color": "#000000"});
                        $domObject.nextAll(".J-productSuggestion").each(function () {
                            clearSearchInputValueAndChangeCss(this);
                        });
                        dropList.hide();
                        $("#searchConditionBtn").click();
                    }
                });
            }
        });
    }
    $(".J-initialCss").placeHolder();

    $("#searchConditionBtn").click(function () {
        $(".J-initialCss").placeHolder("clear");
        var param = $("#searchConditionForm").serializeArray();
        var data = {};
        $.each(param, function (index, val) {
            if(!G.Lang.isEmpty(data[val.name])){
                data[val.name] = data[val.name]+","+val.value;
            }else{
                data[val.name] = val.value;
            }
        });
        $(".J-initialCss").placeHolder("reset");
        var ajaxUrl = "preBuyOrder.do?method=getQuotedPreBuyOrderList";

        APP_BCGOGO.Net.syncPost({
            url: ajaxUrl,
            data: data,
            dataType: "json",
            success: function (json) {
                drawQuotedPreBuyOrdersTable(json);
                initPages(json, "QuotedPreBuyOrders", ajaxUrl, '', "drawQuotedPreBuyOrdersTable", '', '',data,'');
            }
        });
    });

    $("#searchConditionBtn").click();
});

function drawQuotedPreBuyOrdersTable(data) {

    $("#myQuotedPreBuyOrderTable tr:not('.J_title')").remove();
    if (data == null || data[0] == null || data[0].otherDataMap.length==0 ) {
        $("#allQuotedPreBuyOrderCount").text("0");
        $("#allOrdersCount").text("0");
    }else{
        $("#allQuotedPreBuyOrderCount").text(data[0].otherDataMap["allQuotedPreBuyOrderCount"]);
        $("#allOrdersCount").text(data[0].otherDataMap["allOrdersCount"]);
    }

    var bottomTr='<tr class="titBottom_Bg"><td colspan="6"></td></tr>';

    if (data == null || data[0] == null || data[0].orderItems.length==0 ) {
        $("#myQuotedPreBuyOrderTable").append("<tr class='titBody_Bg'><td style='padding-left:10px;' colspan='6'>没有数据!</td></tr>");
        $("#myQuotedPreBuyOrderTable").append($(bottomTr));
        return;
    }
    $.each(data[0].orderItems, function (index, orderItem) {
        var commodityCode = G.Lang.isEmpty(orderItem.commodityCode)?"":(orderItem.commodityCode+" ");
        var productName =G.Lang.isEmpty(orderItem.productName)?"":(orderItem.productName+" ");
        var productBrand =G.Lang.isEmpty(orderItem.productBrand)?"":(orderItem.productBrand+" ");
        var productSpec =G.Lang.isEmpty(orderItem.productSpec)?"":(orderItem.productSpec+" ");
        var productModel =G.Lang.isEmpty(orderItem.productModel)?"":(orderItem.productModel+" ");
        var productVehicleBrand =G.Lang.isEmpty(orderItem.productVehicleBrand)?"":(orderItem.productVehicleBrand+" ");
        var productVehicleModel =G.Lang.isEmpty(orderItem.productVehicleModel)?"":(orderItem.productVehicleModel);
        var productInfo = commodityCode+productName+productBrand+productSpec+productModel+productVehicleBrand+productVehicleModel;
        var orderItemIdStr = orderItem.itemIdStr;
        var createdTimeStr=orderItem.createdTimeStr;
        var editor = orderItem.editor;
        var customerOrSupplierName = orderItem.customerOrSupplierName;
        var customerOrSupplierShopIdStr = orderItem.customerOrSupplierShopIdStr;
        var quotedResult=orderItem.quotedResultValue;

        var price = orderItem.itemPrice+"元";
        var unit = G.Lang.normalize(orderItem.unit);

        var tr = '<tr class="titBody_Bg">';
        tr += '<td style="padding-left:10px;" title="'+productInfo.trim()+'">';
        tr += '        <a class="blue_color" style="cursor: pointer" href="preBuyOrder.do?method=showBuyInformationDetailByQuotedPreBuyOrderItemId&quotedPreBuyOrderItemId=' + orderItemIdStr + '">'+productInfo.trim()+'</a>';
        tr += '    </td>';
        tr += '    <td>'+price+(G.Lang.isEmpty(unit)?"":("/"+unit))+'</td>';
        tr += '    <td>'+createdTimeStr+'</td>';
        tr += '    <td>'+editor+'</td>';
        tr += '    <td><a class="blue_color" href="shopMsgDetail.do?method=renderShopMsgDetail&paramShopId='+customerOrSupplierShopIdStr+'">'+customerOrSupplierName+'</a></td>';
        tr += '    <td>'+quotedResult+'</td>';
        tr += ' </tr>';

        $("#myQuotedPreBuyOrderTable").append($(tr));
        $("#myQuotedPreBuyOrderTable").append($(bottomTr));
    });
}