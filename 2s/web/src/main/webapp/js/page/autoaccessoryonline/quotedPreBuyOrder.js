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
    });

    $("#q_clear_condition_btn").bind('click', function () {
        $(".J_clear_input").val("");
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
            if (!G.Lang.isEmpty(data[val.name])) {
                data[val.name] = data[val.name] + "," + val.value;
            } else {
                data[val.name] = val.value;
            }
        });
        $(".J-initialCss").placeHolder("reset");
        var ajaxUrl = "preBuyOrder.do?method=quotedSelectProduct";

        APP_BCGOGO.Net.syncPost({
            url: ajaxUrl,
            data: data,
            dataType: "json",
            success: function (json) {
                drawProductTable(json);
                initPages(json, "InSalesProduct", ajaxUrl, '', "drawProductTable", '', '', data, '');
            }
        });
    });

    $(".J_QuotedStepFirst").live("click", function () {
        var preShopId = $(this).attr("data-shopId");
      var shopId=$("#shopId").val();
      if(preShopId==shopId){
        nsDialog.jAlert("不能给自己店铺报价!");
        return;
      }
        var preBuyOrderItemId = $(this).attr("data-prebuyorderitemid");
        var $currentSpan = $(this).parent().parent();
        if(!G.Lang.isEmpty(preBuyOrderItemId)){
            selectQuotedProduct($currentSpan,preBuyOrderItemId);
        }

    });

    function selectQuotedProduct($currentSpan,preBuyOrderItemId){
        $("#selectQuotedProductDiv").dialog({
            width: 750,
            zIndex: 20,
            modal: true,
            position:'top',
            beforeclose: function(event, ui) {
                $("#inSalesProductTable tr:not('.J_title')").remove();
                var dropList = APP_BCGOGO.Module.droplist;
                dropList.hide();
                $(".J_clear_input").val("");
                $(".j_clear_input").blur();
                $("#currentPreBuyOrderItemId").val("");
                return true;
            },
            open: function() {
                $(this).find("input[type='text']").first().blur();
                $("#currentPreBuyOrderItemId").val(preBuyOrderItemId);
                $("#productName").val($currentSpan.find("[id$='.productName']").text());
                $("#productBrand").val($currentSpan.find("[id$='.brand']").text());
                $("#productSpec").val($currentSpan.find("[id$='.spec']").text());
                $("#productModel").val($currentSpan.find("[id$='.model']").text());
                $("#productVehicleBrand").val($currentSpan.find("[id$='.vehicleBrand']").text());
                $("#productVehicleModel").val($currentSpan.find("[id$='.vehicleModel']").text());
                $("#commodityCode").val($currentSpan.find("input[id$='.commodityCode']").text());

                $("#searchConditionBtn").click();
            }
        });
    }

    $("#quotedFirstNextBtn").bind("click",function(e){
        var productLocalInfoId = $("input[name='quotedProductId']:checked").val();
        if(G.Lang.isEmpty(productLocalInfoId)){
            nsDialog.jAlert("请选择报价商品!");
            return;
        }
        doQuotedPreBuyOrder(productLocalInfoId);
    });



    $("#saveProductFastInSalesBtn").bind("click",function(e){
        $("#productFastInSalesForm").ajaxSubmit({
            dataType: "json",
            success: function (json) {
                if (json.success) {
                    var product = json.data;

                    var productLocalInfoId = (!product.productLocalInfoIdStr ? "" : product.productLocalInfoIdStr);
                    var commodityCode = (!product.commodityCode ? "" : product.commodityCode+" ");
                    var productName = (!product.name ? "" : product.name+" ");
                    var productBrand = (!product.brand ? "" : product.brand+" ");
                    var productSpec = (!product.spec ? "" : product.spec+" ");
                    var productModel = (!product.model ? "" : product.model+" ");
                    var productVehicleBrand = (!product.productVehicleBrand ? "" : product.productVehicleBrand+" ");
                    var productVehicleModel = (!product.productVehicleModel ? "" : product.productVehicleModel+" ");

                    var productInfo =commodityCode+productName+productBrand+productSpec+productModel+productVehicleBrand+productVehicleModel;
                    var inventoryNum = dataTransition.simpleRounding(G.Lang.normalize(product.inventoryNum,"0"), 1);
                    var sellUnit = G.Lang.normalize(product.sellUnit,"");
                    var inventoryAveragePrice = dataTransition.simpleRounding(G.Lang.normalize(product.inventoryAveragePrice,"0"), 2);
                    var tradePrice = dataTransition.simpleRounding(G.Lang.normalize(product.tradePrice,"0"), 2);

                    var inventoryInfo = inventoryNum+sellUnit;
                    var currentPreBuyOrderId = $("#productFastInSalesForm").find("input[id='preBuyOrderId']").val();
                    var currentPreBuyOrderItemId = $("#productFastInSalesForm").find("input[id='preBuyOrderItemId']").val();

                    $("#productFastInSalesDiv").dialog( "close" );
                    $("#quotedPreBuyOrderDiv").dialog({
                        width: 730,
                        zIndex: 20,
                        modal: true,
                        position:'top',
                        beforeclose: function(event, ui) {
                            $("#quotedPreBuyOrderForm")[0].reset();
                            $("#currentPreBuyOrderItemId").val("");
                            return true;
                        },
                        open: function() {
                            $("#quotedPreBuyOrderForm").find("input[id='preBuyOrderId']").val(currentPreBuyOrderId);
                            $("#quotedPreBuyOrderForm").find("input[id='preBuyOrderItemId']").val(currentPreBuyOrderItemId);
                            $("#quotedPreBuyOrderForm").find("input[id='productId']").val(productLocalInfoId);
                            $("#quotedPreBuyOrderForm").find("input[id='unit']").val(sellUnit);

                            $("#quotedPreBuyOrderForm").find("span[id='productInfoSpan']").text(productInfo);
                            $("#quotedPreBuyOrderForm").find("span[id='inventoryInfoSpan']").text(inventoryInfo);
                            $("#quotedPreBuyOrderForm").find("span[id='inventoryAveragePriceSpan']").text(inventoryAveragePrice);
                            $("#quotedPreBuyOrderForm").find("span[id='tradePriceSpan']").text(tradePrice);
                        }
                    });
                }else{
                    nsDialog.jAlert("出现异常，保存失败！");
                }
            },
            error: function () {
                nsDialog.jAlert("出现异常，保存失败！");
            }
        });
    });

    $("#productFastInSalesBackSelectProductBtn").bind("click",function(e){
        var currentPreBuyOrderItemId = $("#productFastInSalesForm").find("input[id='preBuyOrderItemId']").val();
        $("#productFastInSalesDiv").dialog( "close" );

        $("#selectQuotedProductDiv").dialog({
            width: 730,
            zIndex: 20,
            modal: true,
            position:'top',
            beforeclose: function(event, ui) {
                $("#inSalesProductTable tr:not('.J_title')").remove();
                var dropList = APP_BCGOGO.Module.droplist;
                dropList.hide();
                $(".J_clear_input").val("");
                $(".j_clear_input").blur();
                $("#currentPreBuyOrderItemId").val("");
                return true;
            },
            open: function() {
                $(this).find("input[type='text']").first().blur();
                $("#currentPreBuyOrderItemId").val(currentPreBuyOrderItemId);

                $("#searchConditionBtn").click();
            }
        });
    });

    $("#quotedBackSelectProductBtn").bind("click",function(e){
        var currentPreBuyOrderItemId = $("#quotedPreBuyOrderForm").find("input[id='preBuyOrderItemId']").val();
        $("#quotedPreBuyOrderDiv").dialog( "close" );

        $("#selectQuotedProductDiv").dialog({
            width: 730,
            zIndex: 20,
            modal: true,
            position:'top',
            beforeclose: function(event, ui) {
                $("#inSalesProductTable tr:not('.J_title')").remove();
                var dropList = APP_BCGOGO.Module.droplist;
                dropList.hide();
                $(".J_clear_input").val("");
                $(".j_clear_input").blur();
                $("#currentPreBuyOrderItemId").val("");
                return true;
            },
            open: function() {
                $(this).find("input[type='text']").first().blur();
                $("#currentPreBuyOrderItemId").val(currentPreBuyOrderItemId);

                $("#searchConditionBtn").click();
            }
        });
    });

    $('[field="inSalesAmount"]').blur(function(){
        $('[name="inSalesAmount"]').val(G.rounding($(this).val()));
    });

    $('[name="radio-inSalesAmount"]').click(function(){
        if($(this).attr("field")=="radio-inSalesAmount-exist"){
            $('[name="inSalesAmount"]').val(-1);
        }else{
            $('[name="inSalesAmount"]').val($('[field="inSalesAmount"]').val());
        }
    });

    $("#saveQuotedBtn").bind("click",function(e){

        var productId=$("#quotedPreBuyOrderForm").find("input[id='productId']").val();
        //新商品
        if(G.isEmpty(productId)){
            var productName= $('.product-info-new [name="productName"]').val();
            if(G.isEmpty(productName)){
                nsDialog.jAlert("商品名不能为空。");
                return;
            }
        }
        if($("#salesStatus").val()!="InSales"){
            var inSalesAmount=G.rounding($('[name$="inSalesAmount"]').val());
            if($('[field="radio-inSalesAmount-amount"]').attr("checked")&&inSalesAmount<=0){
                nsDialog.jAlert("上架量应大于0。");
                return;
            }
            var unit=$('[name="unit"]').val();
            if(G.isEmpty(unit)){
                nsDialog.jAlert("上架单位不能为空。");
                return;
            }
            var categoryName = $("#productCategorySelector #_productCategorySelectedName").val();
            if(G.isEmpty(categoryName)){
                nsDialog.jAlert("请选择二级或者三级商品分类！");
                return;
            }
        }
        if(G.Lang.isEmpty($(".J_QuotedPrice").val())){
            nsDialog.jAlert("请填写具体报价！");
            return;
        }
        if(G.rounding($("#arrivalTime").val())==0){
            nsDialog.jAlert("请填写到货时间！");
            return;
        }
        var selectDataInfo = $("#productCategorySelector #_productCategorySelectedInfo").text();
        var categoryId = $("#productCategorySelector #_productCategorySelectedId").val();
        var categoryName = $("#productCategorySelector #_productCategorySelectedName").val();
        var categoryType = $("#productCategorySelector #_productCategorySelectedType").val();
        setSelectedProductCategoryData(selectDataInfo,categoryId,categoryName,categoryType);
        $("#quotedPreBuyOrderForm").ajaxSubmit({
            dataType: "json",
            success: function (json){
                if (!json.success) {
                    nsDialog.jAlert(json.msg);
                    return;
                }
                $("#quotedPreBuyOrderDiv").dialog( "close" );
                window.location.reload();
            },
            error: function () {
                nsDialog.jAlert("出现异常，保存失败！");
            }
        });
    });



    $(".J_QuotedPrice").live("blur", function () {
        if (!$(this).val()) {
            $(this).val(0);
        }
        $(this).val(APP_BCGOGO.StringFilter.priceFilter($(this).val(), 2));
        var num = $.trim($(this).val());
    }).live('keyup', function () {
            $(this).val(APP_BCGOGO.StringFilter.inputtingPriceFilter($(this).val(), 2));
        });

//    $(".J_ArrivalTime").live("blur", function () {
//        if (!$(this).val()) {
//            $(this).val(0);
//        }
//        $(this).val(APP_BCGOGO.StringFilter.priceFilter($(this).val(), 0));
//        var num = $.trim($(this).val());
//    }).live('keyup', function () {
//            $(this).val(APP_BCGOGO.StringFilter.inputtingPriceFilter($(this).val(),0));
//        });

    $("input[name='arrivalTimeRadio']").bind("click",function(){
        if(!G.Lang.isEmpty($(this).val())){
            $("#arrivalTime").val($(this).val());
        }

    });

    $("#applyCustomerRelationBtn").bind("click",function(e){
        var customerShopId = $(this).attr("data-customershopid");
        if ($(this).attr("lock")|| G.Lang.isEmpty(customerShopId)) {
            return;
        }
        $(this).attr("lock", true);
        var $thisDom =  $(this);
        APP_BCGOGO.Net.syncPost({
            url: "apply.do?method=applyCustomerRelation",
            dataType: "json",
            data:{"customerShopId":customerShopId},
            success: function (result) {
                if (result.success) {
                    nsDialog.jAlert( "您的申请提交成功，请等待对方同意！");
                    $("#relationOperationDiv").html("<input type=\"hidden\" id=\"relationMidStatus\" value=\"APPLY_RELATED\"/>" +
                        "<div class=\"list\"><span class=\"button_associate\" style=\"margin-left: 50px;\">已提交申请</span></div>");
                } else {
                    nsDialog.jAlert(result.msg);
                    $thisDom.removeAttr("lock");
                }
            },
            error: function () {
                nsDialog.jAlert("网络异常,请刷新页面!");
                $thisDom.removeAttr("lock");
            }
        });
    });

    $("#acceptCustomerApplyBtn").bind("click",function(e){
        var inviteId = $(this).attr("data-inviteId")
        if ($(this).attr("lock")|| G.Lang.isEmpty(inviteId)) {
            return;
        }
        $(this).attr("lock", true);
        var $thisDom =  $(this);
        APP_BCGOGO.Net.asyncAjax({
            type: "POST",
            url: "apply.do?method=acceptCustomerApply",
            data: {inviteId: inviteId},
            dataType: "json",
            success: function (result) {
                if (result.success) {
                    nsDialog.jAlert("同意关联成功！", "", function () {
                        //
                        $("#relationOperationDiv").html("<input type=\"hidden\" id=\"relationMidStatus\" value=\"RELATED\"/>" + "<div class=\"list\"><span class=\"button_associate\" style=\"margin-left: 50px;\">已经关联</span></div>");
                        var customerDTO = result.data;
                        var contactInfoHtml = "<div><span>"+G.Lang.normalize(customerDTO.contact,"暂无信息")+"</span>&nbsp;<span>"+G.Lang.normalize(customerDTO.mobile,"暂无信息")+"</span></div>";
                        $("#contactInfoDiv").html(contactInfoHtml);

                        $("#landLineSpan").text(G.Lang.normalize(customerDTO.landLine,"暂无信息"));
                        $("#faxSpan").text(G.Lang.normalize(customerDTO.fax,"暂无信息"));
                        $("#emailSpan").text(G.Lang.normalize(customerDTO.email,"暂无信息"));
                        $("#businessScopeSpan").text(G.Lang.normalize(customerDTO.businessScopeStr,"暂无信息"));
                    });
                } else {
                    nsDialog.jAlert(result.msg);
                    $thisDom.removeAttr("lock");
                }
            },
            error: function () {
                nsDialog.jAlert("网络异常,请刷新页面!");
                $thisDom.removeAttr("lock");
            }
        });
    });
    $("#quotedForNewProduct").live("click",function(){
        doQuotedPreBuyOrder();
    });
    $("#QQ").multiQQInvoker({QQ:$("#QQ").attr("data")});
});



function drawProductTable(data) {
    $("#inSalesProductTable tr:not('.J_title')").remove();
    var bottomTr='<tr class="titBottom_Bg"><td colspan="7"></td></tr>';
    if (data == null || data[0] == null || data[0].products == null || data[0].products.length == 0) {
        $("#inSalesProductTable").append("<tr class='titBody_Bg'><td style='padding-left:10px;' colspan='7'>对不起，没有您所需的商品，您可以新增商品报价！新增的商品将同时存入库存并上架！" +
            "<input type='button' class='txt' id='quotedForNewProduct' style='height: 22px; width: 80px; cursor: pointer;' value='新增商品报价'></td></tr>");
        $("#inSalesProductTable").append($(bottomTr));
        $("#quotedFirstNextBtn").hide();
        return;
    }
    $("#quotedFirstNextBtn").show();
    $.each(data[0].products, function(index, product) {
        var productLocalInfoIdStr = (!product.productLocalInfoIdStr ? "" : product.productLocalInfoIdStr);
        var commodityCode = (!product.commodityCode ? "" : product.commodityCode+" ");
        var productName = (!product.name ? "" : product.name+" ");
        var productBrand = (!product.brand ? "" : product.brand+" ");
        var productSpec = (!product.spec ? "" : product.spec+" ");
        var productModel = (!product.model ? "" : product.model+" ");
        var productVehicleBrand = (!product.productVehicleBrand ? "" : product.productVehicleBrand+" ");
        var productVehicleModel = (!product.productVehicleModel ? "" : product.productVehicleModel+" ");

        var productInfo =commodityCode+productName+productBrand+productSpec+productModel+productVehicleBrand+productVehicleModel;
        var inventoryNum = dataTransition.simpleRounding(G.Lang.normalize(product.inventoryNum,"0"), 1);
        var storageUnit = G.normalize(product.storageUnit);
        var sellUnit = G.normalize(product.sellUnit);
        var inSalesUnit = G.normalize(product.inSalesUnit);
        var rate = G.rounding(product.rate);
        var salesStatus = G.Lang.normalize(product.salesStatus,"");
        var salesStatusStr=(salesStatus=="InSales")?"已上架":"未上架";
        var inSalesPrice=G.rounding(product.inSalesPrice);
        var inventoryAveragePrice = dataTransition.simpleRounding(G.Lang.normalize(product.inventoryAveragePrice,"0"), 2);
        var tradePrice = dataTransition.simpleRounding(G.Lang.normalize(product.tradePrice,"0"), 2);
        var tr = "<tr class='titBody_Bg'> ";
        tr +='<input type="hidden" name="unit" value="'+sellUnit+'">';
        tr +='<input type="hidden" name="sellUnit" value="'+sellUnit+'">';
        tr +='<input type="hidden" name="storageUnit" value="'+storageUnit+'">';
        tr +='<input type="hidden" name="rate" value="'+rate+'">';
        tr += "<td><input type='radio'class='radio' name='quotedProductId' data-sales-status='"+salesStatus+"' value='"+productLocalInfoIdStr+"'/></td>";
        tr += "<td style='padding-left:10px;' title='"+productInfo+"'>"+productInfo+"<input type='hidden' name='productInfo' value='"+productInfo+"'></td>";
        tr += "<td>"+inventoryNum+sellUnit+"<input type='hidden' name='inventoryNum' value='"+inventoryNum+"'></td>";
        tr += "<td><span class='arialFont'>&yen;</span>"+inventoryAveragePrice+"<input type='hidden' name='inventoryAveragePrice' value='"+inventoryAveragePrice+"'></td>";
        tr += "<td><span class='arialFont'>&yen;</span>"+tradePrice+"<input type='hidden' name='tradePrice' value='"+tradePrice+"'></td>";
        tr+="<td>"+salesStatusStr+"</td>";
        tr+="<td>"+inSalesPrice+"</td>";
        tr += "</tr>";

        $("#inSalesProductTable").append($(tr));
        $("#inSalesProductTable").append($(bottomTr));
    });
}