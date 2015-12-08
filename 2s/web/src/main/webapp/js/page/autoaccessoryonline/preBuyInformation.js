/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 13-3-8
 * Time: 上午10:54
 * To change this template use File | Settings | File Templates.
 */
var trCount=0;

$(document).ready(function () {
    $("#clearConditionBtn").bind('click', function () {
        $(".J_clear_input").val("");
        $(".J-initialCss").placeHolder("reset");
        $("#shopId").val("");
        $("#provinceNo").get(0).selectedIndex=0;
        $("#cityNo").get(0).selectedIndex=0;
        $("#regionNo").get(0).selectedIndex=0;
        $("#provinceNo,#cityNo,#regionNo").change();
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
                        // 查询
                        $("#shopId").val("");
                        searchPreBuyInformation();
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
                        // 查询
                        $("#shopId").val("");
                        searchPreBuyInformation();
                    }
                });
            }
        });
    }

    provinceBind();
    $("#provinceNo").bind("change",function(){
        $(this).css("color", $(this).find("option:selected").css("color"));
        $("#cityNo option").not(".default").remove();
        $("#regionNo option").not(".default").remove();
        cityBind();
        $("#cityNo").change();
        $("#regionNo").change();
    });
    $("#cityNo").bind("change",function(){
        $(this).css("color", $(this).find("option:selected").css("color"));
        $("#regionNo option").not(".default").remove();
        regionBind();
        $("#regionNo").change();
    });

    $("#regionNo").bind("change",function(){
        $(this).css("color", $(this).find("option:selected").css("color"));
    });


    function cityBind() {
        var r = APP_BCGOGO.Net.syncGet({url: "shop.do?method=selectarea", data: {"parentNo": $("#provinceNo").val()}, dataType: "json"});
        if (!r || r.length == 0) return;
        else {
            for (var i = 0, l = r.length; i < l; i++) {
                var option = $("<option>")[0];
                option.value = r[i].no;
                option.innerHTML = r[i].name;
                option.style.color="#000000";
                $("#cityNo")[0].appendChild(option);
            }
        }
    }

    function regionBind() {
        var r = APP_BCGOGO.Net.syncGet({url: "shop.do?method=selectarea",data: {"parentNo": $("#cityNo").val()}, dataType: "json"});
        if (!r || r.length == 0) return;
        else {
            for (var i = 0, l = r.length; i < l; i++) {
                var option = $("<option>")[0];
                option.value = r[i].no;
                option.innerHTML = r[i].name;
                option.style.color="#000000";
                $("#regionNo")[0].appendChild(option);
            }
        }
    }
    function provinceBind() {
        var r = APP_BCGOGO.Net.syncGet({url: "shop.do?method=selectarea", data: {"parentNo":"1"}, dataType: "json"});
        if (!r || r.length == 0) return;
        else {
            for (var i = 0, l = r.length; i < l; i++) {
                var option = $("<option>")[0];
                option.value = r[i].no;
                option.innerHTML = r[i].name;
                option.style.color="#000000";
                if($("#provinceNo")[0]){
                    $("#provinceNo")[0].appendChild(option);
                }
            }
        }
    }


    $(".J-initialCss").placeHolder();

    $("#searchConditionBtn").click(function () {
        $("#shopId").val("");
        searchPreBuyInformation();
    });

    function searchPreBuyInformation() {
        $(".J-initialCss").placeHolder("clear");
        var param = $("#searchConditionForm").serializeArray();
        var data = {};
        $.each(param, function (index, val) {
            if (!G.Lang.isEmpty(data[val.name])) {
                data[val.name] = data[val.name] + "," + val.value;
            } else {
                data[val.name] = val.value;
            }
        });
        $(".J-initialCss").placeHolder("reset");
        var ajaxUrl = "preBuyOrder.do?method=getPreBuyInformationList";

        APP_BCGOGO.Net.syncPost({
            url: ajaxUrl,
            data: data,
            dataType: "json",
            success: function (json) {
                drawPreBuyInformationTable(json);
                initPages(json, "preBuyInformation", ajaxUrl, '', "drawPreBuyInformationTable", '', '', data, '');
            }
        });
    }
    searchPreBuyInformation();

    if($("#preBuyItemRecommendDiv").length > 0){
        APP_BCGOGO.Net.asyncPost({
            url: "supplyDemand.do?method=getWholesalerPreBuyOrderRecommendByShopId",
            dataType: "json",
            success: function (json) {
                _initWholesalerPreBuyOrderRecommend(json);
            }
        });
        function _initWholesalerPreBuyOrderRecommend(json){
            var size =10;
            if (json == null || json[0] == null || json[0].length == 0) {
                $("#preBuyItemRecommendDiv").css("display", "none");
                return;
            }
            var length = json[0].length;
            var liSize = length % size == 0 ? parseInt((length / size))  : parseInt((length / size)) + 1;
            var str = '<div class="JScrollGroup">';
            for (var liIndex = 0; liIndex < liSize; liIndex ++) {
                str += '<div id="li_' + liIndex + '" class="JScrollItem"><div class="prev JScrollPrevButton"></div><div class="next JScrollNextButton"></div></div>';
            }
            str += '</div>';
            $("#preBuyItemRecommendDiv").html(str);
            for (var liIndex = 0; liIndex < liSize; liIndex ++) {
                var liHtml = '';
                liHtml +='<div class="cartBody newBody">' +
                    '<table class="tab_cuSearch tabSales tab_cart" cellpadding="0" cellspacing="0">' +
                    '<col><col width="350"><col width="350">';

                for (var index = 0; index < size; index ++) {
                    var s = liIndex * size + index;
                    if (s >= length) {
                        continue;
                    }
                    var preBuyOrderItemDTO = json[0][s];
                    var productName = G.Lang.isEmpty(preBuyOrderItemDTO.productName) ? "" : preBuyOrderItemDTO.productName;
                    var brand = G.Lang.isEmpty(preBuyOrderItemDTO.brand) ? "" : preBuyOrderItemDTO.brand;
                    var model = G.Lang.isEmpty(preBuyOrderItemDTO.model) ? "" : preBuyOrderItemDTO.model;
                    var spec = G.Lang.isEmpty(preBuyOrderItemDTO.spec) ? "" : preBuyOrderItemDTO.spec;
                    var vehicleBrand = G.Lang.isEmpty(preBuyOrderItemDTO.vehicleBrand) ? "" : preBuyOrderItemDTO.vehicleBrand;
                    var vehicleModel = G.Lang.isEmpty(preBuyOrderItemDTO.vehicleModel) ? "" : preBuyOrderItemDTO.vehicleModel;
                    var productInfoStr = "";
                    if(productName != ""){
                        productInfoStr += productName + " ";
                    }
                    if(brand != ""){
                        productInfoStr += brand + " ";
                    }
                    if (model != "" && spec != "") {
                        productInfoStr += model + "/" + spec;
                    } else if (model != "") {
                        productInfoStr += model;
                    } else if (spec != "") {
                        productInfoStr += spec;
                    }
                    if(vehicleBrand != ""){
                        productInfoStr += vehicleBrand + " ";
                    }
                    if(vehicleModel != ""){
                        productInfoStr += vehicleModel;
                    }
                    var preBuyOrderItemId = preBuyOrderItemDTO.idStr;
                    var shopName = preBuyOrderItemDTO.shopName;
                    var shopIdStr = preBuyOrderItemDTO.shopIdStr;
                    var shopAreaInfo = preBuyOrderItemDTO.shopAreaInfo.replace("省", "");
                    var vestDateStr = preBuyOrderItemDTO.vestDateStr;
                    var businessChanceTypeStr = preBuyOrderItemDTO.businessChanceTypeStr;
                    var businessChanceType = preBuyOrderItemDTO.businessChanceType;
                    var changeClass='';
                    switch(businessChanceType){
                        case 'Normal':
                            changeClass='yellow_color';
                            break;
                        case 'SellWell':
                            changeClass='green_color';
                            break;
                        case 'Lack':
                            changeClass='red_color';
                            break;
                        default:
                            changeClass='yellow_color'
                    }
                    var endDateCount =G.rounding(preBuyOrderItemDTO.endDateCount);
                    var isLocalCity = preBuyOrderItemDTO.localCity;
                    var imageSmallURL = preBuyOrderItemDTO.imageCenterDTO.recommendProductListImageDetailDTO.imageURL;
                    var tr='<tr>';
                    tr+='<div class="purchase">'+
                        '<div class="pic" style="cursor: pointer" onclick="showBuyInformationDetail(\''+preBuyOrderItemId+'\')"><img src="'+imageSmallURL+'"/></div>'+
                        '<div class="right p-right">'+
                        '<div class="p-info-detail text-overflow" onclick="showBuyInformationDetail(\''+preBuyOrderItemId+'\')">[<a class="'+changeClass+'">'+businessChanceTypeStr+'</a>]<span title="'+productInfoStr+'">'+productInfoStr+'</span></div>'+
                        '<div class="p-info-detail text-overflow" onclick="renderShopMsgDetail(\''+shopIdStr+'\',\''+"true"+'\')">[<a class="yellow_color">买家</a>] '+shopName+'</div>'+
                        '<div>'+shopAreaInfo+'</div>'+
                        '<div><div onclick="showBuyInformationDetail(\''+preBuyOrderItemId+'\')" class="accessories-btn fr" style="margin-top:5px;">查看详细</div><span class="yellow_color">还剩'+endDateCount+'天有效</span></div>'+
                        '</div>'+
                        '</div>';
                    tr += '</tr>';
                    liHtml += tr;
                }
                liHtml += '</table>' +
                    '</div>' +
                    '<div class="lineBottom"></div>';
                $("#li_" + liIndex).append(liHtml);
            }
            $("#preBuyItemRecommendDiv").css("display", "block");
            var scrollFlowHorizontal = new App.Module.ScrollFlowHorizontal();
            scrollFlowHorizontal
                .init({
                "selector": "#preBuyItemRecommendDiv",
                "width":792,
                "height": 540,
                "background": "#FFFFFF",
                "scrollInterval":500000
            }).startAutoScroll();
            window.scrollFlowHorizontal = scrollFlowHorizontal;
        }
    }

    $(".J_supplierOnlineSuggestion")
        .bind('click', function () {
            getCustomerOrSupplierOnlineSuggestion($(this));
        })
        .bind('keyup', function (event) {
            var eventKeyCode = event.which || event.keyCode;
            if (GLOBAL.Interactive.keyNameFromKeyCode(eventKeyCode).search(/left|up|right|down/g) == -1) {
                getCustomerOrSupplierOnlineSuggestion($(this));
            }
        });

    function getCustomerOrSupplierOnlineSuggestion($domObject) {
        var searchWord = $domObject.val().replace(/[\ |\\]/g, "");
        var dropList = APP_BCGOGO.Module.droplist;
        dropList.setUUID(GLOBAL.Util.generateUUID());
        var ajaxData = {
            name: searchWord,
            shopRange:"notRelated",
           uuid: dropList.getUUID()
        };
        var ajaxUrl = "searchInventoryIndex.do?method=getShopSuggestion";
        APP_BCGOGO.wjl.LazySearcher.lazySearch(ajaxUrl, ajaxData, function (result) {
            dropList.show({
                "selector":$domObject,
                "data":result,
                "onSelect":function (event, index, data) {
                    $domObject.val(data.label);
                    $domObject.css({"color":"#000000"});
                    dropList.hide();
                }
            });
        });

    }

});

function getDifDay(endDate){
    var now=new Date().getTime();
    if(G.isEmpty(endDate)||G.isEmpty(now)||endDate<=now){
        return "0天0小时";
    }
    var dif= endDate-new Date().getTime();
    var day= dif/(dateUtil.millSecondOneDay);
    day=G.rounding(G.round(day<1?0:day,0));
    var dif_hour=dif-dateUtil.millSecondOneDay*day;
    var hour=0;
    if(dif_hour>0){
        hour=dif_hour/(dateUtil.millSecondOneHour);
    }
    hour=G.rounding(hour<1?0:hour,0);
    return day+"天"+hour+"小时";
}

function drawPreBuyInformationTable(data) {
    $("#preBuyInformationTable tr:not('.J_title')").remove();
    var bottomTr='<tr class="titBottom_Bg"><td colspan="6"></td></tr>';
    if (data == null || data[0] == null || data[0].orderItems.length==0 ) {
        $("#preBuyInformationTable").append("<tr class='titBody_Bg'><td style='padding-left:10px;' colspan='6'>暂无有效的求购资讯!</td></tr>");
        $("#preBuyInformationTable").append($(bottomTr));
        return;
    }
    $.each(data[0].orderItems, function (index, orderItem) {
        var endDateStr=orderItem.endDateStr;
        var vestDateStr=G.normalize(orderItem.vestDateStr);
        var vestDate=G.normalize(orderItem.vestDate);
        var endDate= orderItem.endDate+dateUtil.millSecondOneDay;
        var difStr=getDifDay(endDate);
        var commodityCode =G.normalize(orderItem.commodityCode)+(G.Lang.isEmpty(orderItem.productName)?"":" ");
        var productName = G.normalize(orderItem.productName)+(G.Lang.isEmpty(orderItem.productBrand)?"":" ");
        var productBrand = G.normalize(orderItem.productBrand)+(G.Lang.isEmpty(orderItem.productSpec)?"":" ");
        var productSpec = G.normalize(orderItem.productSpec)+(G.Lang.isEmpty(orderItem.productModel)?"":" ");
        var productModel = G.normalize(orderItem.productModel)+(G.Lang.isEmpty(orderItem.productVehicleBrand)?"":" ");
        var productVehicleBrand = G.normalize(orderItem.productVehicleBrand)+(G.Lang.isEmpty(orderItem.productVehicleModel)?"":" ");
        var productVehicleModel = G.normalize(orderItem.productVehicleModel);
        var productInfo = commodityCode+productName+productBrand+productSpec+productModel+productVehicleBrand+productVehicleModel;
        var itemUnit=orderItem.unit;
        var itemMemo= G.Lang.normalize(orderItem.itemMemo);
        var itemMemoShow = itemMemo;
        if(itemMemoShow.length>30){
            itemMemoShow=itemMemoShow.substr(0,30)+"...";
        }
        var myQuoted=orderItem.myQuoted;
        var businessChanceType=G.normalize(orderItem.businessChanceType);
        var businessChanceTypeValue=G.normalize(orderItem.businessChanceTypeValue);
        var changeClass='';
        switch(businessChanceType){
            case 'Normal':
                changeClass='yellow_color';
                break;
            case 'SellWell':
                changeClass='green_color';
                break;
            case 'Lack':
                changeClass='red_color';
                break;
            default:
                changeClass='yellow_color'
        }
        var itemCount;
        if(businessChanceType=='SellWell'){
            itemCount=orderItem.fuzzyAmountStr+ (G.Lang.isEmpty(itemUnit)?"":(itemUnit));
        }else{
            itemCount=orderItem.itemCount+ (G.Lang.isEmpty(itemUnit)?"":(itemUnit));
        }
        var shopAreaInfo = orderItem.shopAreaInfo;
        var shopName = orderItem.shopName;
        var itemIdStr = orderItem.itemIdStr;
        var shopIdStr = orderItem.shopIdStr;
        var imageSmallURL = orderItem.imageCenterDTO.productListSmallImageDetailDTO.imageURL;
        var tr = "<tr class='titBody_Bg'> ";
        tr += "<td style='padding-left:10px;' title='"+productInfo+"'>" ;

        tr+='<input type="hidden" class="productName" value="'+G.normalize(orderItem.productName)+'"/>';
        tr+='<input type="hidden" class="brand" value="'+G.normalize(orderItem.productBrand)+'"/>';
        tr+='<input type="hidden" class="spec" value="'+G.normalize(orderItem.productSpec)+'"/>';
        tr+='<input type="hidden" class="model" value="'+G.normalize(orderItem.productModel)+'"/>';
        tr+='<input type="hidden" class="vehicleBrand" value="'+G.normalize(orderItem.productVehicleBrand)+'"/>';
        tr+='<input type="hidden" class="vehicleModel" value="'+G.normalize(orderItem.productVehicleModel)+'"/>';
        if(dateUtil.inToday(vestDate)){
            tr+='<span class="new-relative"><div class="new-tip">新</div></span>';
        }
        tr+="<div class='product-image-60x60' onclick='showBuyInformationDetail(\""+itemIdStr+"\")'><img src='"+imageSmallURL+"'></img></div>";
        tr+= "<span class='p-info-detail' style='cursor: pointer' onclick='showBuyInformationDetail(\""+itemIdStr+"\")'>"+" [<a class='"+changeClass+"'>"+businessChanceTypeValue+"</a>]";
        tr+="<span>"+productInfo+"</span></span>" +
            "</td>";
        tr += "    <td title='"+itemCount+"'>"+itemCount+"</td>";
        tr += "    <td title='"+shopName+"'><a class='blue_color' target='_blank' href='shopMsgDetail.do?method=renderShopMsgDetail&paramShopId=" + shopIdStr + "'>"+shopName+"</a></td>";
        tr += "    <td title='"+shopAreaInfo+"'>"+shopAreaInfo+"</td>";
      tr += "    <td><div>发布:"+vestDateStr+"</div><div>截至:"+endDateStr+"</div><div>剩余"+difStr+"</div></td>";
        if(myQuoted){
          tr += '<td><span style="color: #828282;">我已报价</span></td>';
        }else{
            tr += '    <td><a class="blue_color J_doQuoted" data-itemId="'+itemIdStr+'" data-shopId="'+shopIdStr+'">我要报价</a></td>';
        }
      tr += "</tr>";

        $("#preBuyInformationTable").append($(tr));
        $("#preBuyInformationTable").append($(bottomTr));
    });
}