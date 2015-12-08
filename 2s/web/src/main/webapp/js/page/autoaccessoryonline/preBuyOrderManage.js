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

    $("a[name='vestDateSelect']").bind("click", function() {
        var selectDateType=$(this).attr("id");
        $("a[name='vestDateSelect']").not(this).removeClass("clicked");
        if (!$(this).hasClass("clicked")) {
            $(this).addClass("clicked");
            $("#endDate").val(dateUtil.getToday());
            if(selectDateType=="my_date_5days"){
                $("#startDate").val(dateUtil.getNDayCloseToday(5*-1));
            }
            if(selectDateType=="my_date_10days"){
                $("#startDate").val(dateUtil.getNDayCloseToday(10*-1));
            }
            if(selectDateType=="my_date_30days"){
                $("#startDate").val(dateUtil.getNDayCloseToday(30*-1));
            }
            if(selectDateType=="my_date_defining"){
                $("#startDate").val(dateUtil.getNDayCloseToday(5*-1));
            }
        }else{
            $(this).removeClass("clicked");
            $("#startDate").val("");
            $("#endDate").val("");

        }


    });

    $("#clearConditionBtn").bind('click', function () {
        $("a[name='my_date_select']").not(this).removeClass("clicked");
        $("a[name='vestDateSelect']").not(this).removeClass("clicked");
        $(".J_clear_input").val("");
        $(":radio").attr('checked',false);
        $("#radioAll").attr('checked',true);
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
        var ajaxUrl = "preBuyOrder.do?method=getPreBuyOrderItem";

        APP_BCGOGO.Net.syncPost({
            url: ajaxUrl,
            data: data,
            dataType: "json",
            success: function (json) {
                drawPreBuyOrderItemsTable(json);
                initPages(json, "PreBuyOrderItems", ajaxUrl, '', "drawPreBuyOrderItemsTable", '', '',data,'');
            }
        });
    });

    $("#searchConditionBtn").click();
});

function drawPreBuyOrderItemsTable(data) {
    $("#myPreBuyOrderTable tr:not('.J_title')").remove();
    if (data == null || data[0] == null || data[0].otherDataMap.length==0 ) {
        $("#allPreBuyOrderCount").text("0");
        $("#allQuotedCount").text("0");
    }else{
        $("#allPreBuyOrderCount").text(data[0].otherDataMap["allPreBuyOrderCount"]);
        $("#allQuotedCount").text(data[0].otherDataMap["allQuotedCount"]);
    }
    var bottomTr='<tr class="titBottom_Bg"><td colspan="6"></td></tr>';
    if (data == null || data[0] == null || data[0].orderItems.length==0 ) {
        $("#myPreBuyOrderTable").append("<tr class='titBody_Bg'><td style='padding-left:10px;' colspan='6'>没有数据!</td></tr>");
        $("#myPreBuyOrderTable").append($(bottomTr));
        return;
    }
    $.each(data[0].orderItems, function (index, orderItem) {
        var vestDateStr=orderItem.createdTimeStr;
        var productInfo =G.normalize(orderItem.productInfoStr);
        var orderIdStr = orderItem.orderIdStr;
        var editor =G.normalize(orderItem.editor);
        var itemCount =G.rounding(orderItem.itemCount);
        var quotedCount =G.rounding(orderItem.quotedCount);
        quotedCount=(quotedCount==0)?"暂无报价":quotedCount;
        var status=orderItem.preBuyOrderStatusStr;
        var imageCenterDTO = orderItem.imageCenterDTO;
        var imageSmallURL = imageCenterDTO.productListSmallImageDetailDTO.imageURL;
        var tr = '<tr class="titBody_Bg">';
        tr += '<td style="padding-left:10px;">' +
            '<div class="product-image-60x60" onclick="showPreBuyOrderById(\''+orderIdStr+'\')"><img src="'+imageSmallURL+'"></div>' +
            '<div style="width: 70%;"><a onclick="showPreBuyOrderById(\''+orderIdStr+'\')"  class="blue_color"  style="text-align: center;">'+productInfo+'</a></div>' +
            '</td>';
        tr+='<td>'+itemCount+'</td>';
        tr += '<td>'+vestDateStr+'</td>';
        tr += '<td>'+editor+'</td>';
        tr += '<td>'+quotedCount+'</td>';
        tr += '<td>'+status+'</td>';
        tr += ' </tr>';

        $("#myPreBuyOrderTable").append($(tr));
        $("#myPreBuyOrderTable").append($(bottomTr));
    });
}

function showPreBuyOrderById(preBuyOrderId){
      window.open('preBuyOrder.do?method=showPreBuyOrderById&preBuyOrderId='+preBuyOrderId);
}