<%--
  Created by IntelliJ IDEA.
  User: ndong
  Date: 13-7-31
  Time: 上午9:29
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
<title>商品上架</title>
<link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="js/extension/uploadify/uploadify<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="js/components/themes/bcgogo-imageUploader<%=ConfigController.getBuildVersion()%>.css"/>
<%@include file="/WEB-INF/views/header_script.jsp" %>
<script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.js"></script>
<script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/page/txn/addNewProduct<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/page/autoaccessoryonline/promotionsUtil<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/page/autoaccessoryonline/promotions<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/page/autoaccessoryonline/goodsInOffSales<%=ConfigController.getBuildVersion()%>.js"></script>

<%@ include file="/WEB-INF/views/image_script.jsp" %>
<script type="text/javascript" charset="utf-8" src="js/extension/ueditor/ueditor.config<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" charset="utf-8" src="js/extension/ueditor/ueditor.all<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" charset="utf-8" src="js/extension/ueditor/ueditor.parse<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" charset="utf-8" src="js/components/ui/bcgogo-imageUploader<%=ConfigController.getBuildVersion()%>.js"></script>

<script type="text/javascript">
defaultStorage.setItem(storageKey.MenuUid, "AUTO_ACCESSORY_ONLINE_GOODS_IN_OFF_SALES_MANAGE");
defaultStorage.setItem(storageKey.MenuCurrentItem,"上架");

$(function(){
    if(!swfobject.hasFlashPlayerVersion("11.8.0")){
        var content='<div>检测到您尚未安装Flash插件。请按以下步骤安装：</div>'+
                '<div>1. <a class="blue_color" href="http://mail.bcgogo.com:8088/install_flash_player.exe" style="color: #007CDA">点我下载</a>，'+
                '并记住下载到本机的文件位置</div>'+
                '<div>2. 下载完成后关闭一发软件</div>'+
                '<div>3. 找到下载好的文件，双击并安装</div>'+
                '<div>4. 重新打开一发软件</div>';
        nsDialog.jAlert(content);
    }
});

var newProductCache = {};
$(document).ready(function(){
    var productImageUrlData = [];

    UE.getEditor('productDescriptionEditor');

    getPromotionDetailForInsales(function(promotionList){
        initShopPromotionInfo(promotionList);
        _getProductDetailFn();
    });

    var _getProductDetailFn=function(){
        if(!G.isEmpty($("#productId").val())){
            APP_BCGOGO.Net.asyncAjax({
                url: "goodsInOffSales.do?method=getProductDetail",
                type: "POST",
                cache: false,
                dataType: "json",
                data:{
                    productId:$("#productId").val()
                },
                success:function (product) {
                    bindProductImageInfo(product);
                    initGoodsInSalesEditor(product);
                    $(".J_selectProductCategory").show();
                },
                error:function(){
                    nsDialog.jAlert("网络异常！");
                }
            });
        }else{
            bindProductImageInfo(null);
            $(".J_selectProductCategory").show();
        }
    }

    $(".promotionSelector").change(function(){
        $("#pContent").text(G.normalize($(this).find("option:selected").attr("pContent")));
        $("#promotionsId").val($(this).find("option:selected").val());
    });

    $('[name="guaranteePeriod"]').keyup(function(){
        $(this).val(G.rounding($(this).val(),0));
    });


    $('[field="inSalesAmount"]').blur(function(){
        $('[name="inSalesAmount"]').val(G.rounding($(this).val()));
    });

    $('[name="guaranteePeriodRd"]').click(function(){
        var $target=$(this);
        $('.self-define-guaranteePeriod').hide();
        if($target.hasClass("month12")){
            $('[name="guaranteePeriod"]').val(12);
        }else if($target.hasClass("month24")){
            $('[name="guaranteePeriod"]').val(24);
        }else{
            $('.self-define-guaranteePeriod').show();
        }
    });

    $('[name="radio-inSalesAmount"]').click(function(){
        if($(this).attr("field")=="radio-inSalesAmount-exist"){
            $('[name="inSalesAmount"]').val(-1);
        }else{
            $('[name="inSalesAmount"]').val($('[field="inSalesAmount"]').val());
        }
    });

    function initGoodsInSalesEditor(product){
        if(G.isEmpty(product)) return;
        var storageUnit = G.normalize(product.storageUnit);
        var sellUnit = G.normalize(product.sellUnit);
        var inSalesUnit = G.normalize(product.inSalesUnit);
        var rate = G.rounding(product.rate);
        var inSalesPrice = G.rounding(product.inSalesPrice);
        var inventoryAveragePrice = G.rounding(product.inventoryAveragePrice);
        var recommendedPrice = G.rounding(product.price);
        var tradePrice = G.rounding(product.tradePrice);
        var inventoryNum = G.rounding(product.inventoryNum);
        inSalesUnit=G.isEmpty(inSalesUnit)?sellUnit:inSalesUnit;
//        if(!G.isEmpty(inSalesUnit)&&!G.isEmpty(storageUnit)&&inSalesUnit!=storageUnit){      //用大单位上架
//            inSalesPrice=G.rounding(inSalesPrice*rate);
//            recommendedPrice=G.rounding(recommendedPrice*rate);
//            tradePrice=G.rounding(tradePrice*rate);
//            inventoryAveragePrice=G.rounding(inventoryAveragePrice*rate);
//            inventoryNum=G.rounding(inventoryNum/rate);
//        }
        if(G.isNotEmpty(product.productCategoryDTO)){
            $(".add-product .J_productCategoryInfoSpan").text(product.productCategoryInfo);
            $(".add-product [name='productCategoryName']").val(product.productCategoryDTO.name);
            $(".add-product [name='productCategoryId']").val(product.productCategoryDTO.idStr);
            $(".add-product [id='productCategoryType']").val(product.productCategoryDTO.categoryType);
        }

        $(".add-product [name='commodityCode']").val(G.normalize(product.commodityCode));
        $(".add-product [name='name']").val(G.normalize(product.name));
        $(".add-product [name='brand']").val(G.normalize(product.brand));
        $(".add-product [name='spec']").val(G.normalize(product.spec));
        $(".add-product [name='model']").val(G.normalize(product.model));
        $(".add-product [name='productVehicleBrand']").val(G.normalize(product.productVehicleBrand));
        $(".add-product [name='productVehicleModel']").val(G.normalize(product.productVehicleModel));
        $(".add-product [name='tradePrice']").text(tradePrice);
        $(".add-product [field='inventoryAveragePrice']").val(inventoryAveragePrice);
        $(".add-product span[name='recommendedPrice']").text(recommendedPrice).val(recommendedPrice);
        $(".add-product [name='inSalesPrice']").val(inSalesPrice);
        $(".add-product [name='inventoryNum']").text(inventoryNum).val(inventoryNum);
        var guaranteePeriod=G.rounding(product.guaranteePeriod);
        if(guaranteePeriod==12){
            $(".guaranteePeriodDiv .month12").click();
        }else if(guaranteePeriod==24){
            $(".guaranteePeriodDiv .month24").click();
        }else{
            $(".add-product [name='guaranteePeriod']").val(G.rounding(product.guaranteePeriod));
            $('.guaranteePeriodDiv .month-define').click();
        }

        var inSalesAmount=G.rounding(product.inSalesAmount);
        $('[name="inSalesAmount"]').val(inSalesAmount);
        if(inSalesAmount==-1){
            $('[field="radio-inSalesAmount-exist"]').attr("checked",true);
        }else{
            $(".add-product [field='inSalesAmount']").val(inSalesAmount);
        }
        //单位
        var unitHtml = '';
        if(G.isEmpty(sellUnit)){
            unitHtml += '<input type="txt" id="firstUnit" name="unit" maxlength="10" class="txt" style="width:50px;">';
        }else if(sellUnit == storageUnit){

            unitHtml += '<input type="txt"  name="unit" style="display: none" class="txt" value="'+inSalesUnit+'" style="width:50px;">';
            unitHtml +='<input type="hidden" name="sellUnit" value="'+sellUnit+'"/>';
            unitHtml +='<input type="hidden" name="storageUnit" value="'+sellUnit+'"/>';
            unitHtml +='<input type="hidden" name="rate" value="'+rate+'"/>';

            unitHtml += '<span class="units" title="' + sellUnit + '">'+sellUnit+'<input type="hidden" id="firstUnit_Hidden"  value="' + sellUnit + '"></span>';
            unitHtml += '<a class="blue_color" id="addUnit" style="padding-left:5px;display: none">增加单位</a>';
            unitHtml += '<span class="divUnit" id="secondUnitContainer"  style="padding-left: 5px;display:none">';
            unitHtml +=     '<span>增加单位：<input type="text" id="secondUnit" class="txt" maxlength="7" style="width:50px;"/>&nbsp;&nbsp;</span>';
            unitHtml += '</span>';

            unitHtml += '<div class="inventory divUnit" id="rateContainer"  style="margin-left:70px;margin-top:5px;display:none">';
            unitHtml +=     '<span style="float:left">换算比例：<input type="text" class="txt" style="width:30px;" id="secondRate" />&nbsp;</span>';
            unitHtml +=     '<span id="secondUnitSpan" class="units"  style="width:15px;float:left"></span>&nbsp;';
            unitHtml +=     '<span style="float: left">=&nbsp;<input id="firstRate" type="text" class="txt" style="width:30px;" /></span>';
            unitHtml +=     '<span class="units" title="'+sellUnit+'" style="width:15px;float: left">&nbsp;'+sellUnit+'&nbsp;</span>&nbsp;';
            unitHtml +=     '<a class="btnSure" id="sureAddUnit">确 定</a>&nbsp;<a class="blue_color btnCancel" id="cancelAddUnit">取消</a>';
            unitHtml += '</div>';
        }else{
            unitHtml += '<span>'+inSalesUnit+'</span>';
            unitHtml += '<input type="hidden"  name="unit" class="txt" value="'+sellUnit+'">';
            unitHtml +='<input type="hidden" name="sellUnit" value="'+sellUnit+'"/>';
            unitHtml +='<input type="hidden" name="storageUnit" value="'+storageUnit+'"/>';
            unitHtml +='<input type="hidden" name="rate" value="'+rate+'"/>';
        }
        $("#_unitContainer").html(unitHtml);
        var salesStatus = G.normalize(product.salesStatus);
        if(salesStatus=="InSales"){
            $("#saveGoodsInSalesBtn").text("确认修改");
        }
        var productDescriptionEditor =UE.getEditor('productDescriptionEditor');
        productDescriptionEditor.ready(function(){
            //需要ready后执行，否则可能报错
            productDescriptionEditor.setContent(G.normalize(product.description));
        });
        //
    }
    function getTotelUrlToData(inUrlData) {
        var outData = [];
        for (var i = 0; i < inUrlData.length; i++) {
            var outDataItem = {
                "url": APP_BCGOGO.UpYun.UP_YUN_DOMAIN_URL + inUrlData[i] + APP_BCGOGO.UpYun.UP_YUN_SEPARATOR + APP_BCGOGO.ImageScene.PRODUCT_LIST_IMAGE_SMALL,
                "name": i==0?"*主图":"辅图",
                "isAssist":i==0?false:true,
                "assistButtonLabel": i==0?"":"设为主图",
                "isEmphasis": i==0?true:false,
                "emphasisColor":"#ff7800"
            };

            outData.push(outDataItem);
        }
        return outData;
    };

    function bindProductImageInfo(product){
        var currentItemNum = 0;var productInfoBigImageDetailDTOs;
        if(!G.isEmpty(product) && !G.Lang.isEmpty(product.imageCenterDTO.productInfoBigImageDetailDTOs)){
            productInfoBigImageDetailDTOs = product.imageCenterDTO.productInfoBigImageDetailDTOs;
            currentItemNum = productInfoBigImageDetailDTOs.length;
        }
        var imageUploader = new App.Module.ImageUploader();

        imageUploader.init({
            "selector":"#product_imageUploader",
            "flashvars":{
                "debug":"off",
                "maxFileNum":5,
                "currentItemNum": currentItemNum,
                "width":61,
                "height":24,
                "buttonBgUrl":"images/imageUploader.png",
                "buttonOverBgUrl":"images/imageUploader.png",
                "url":APP_BCGOGO.UpYun.UP_YUN_UPLOAD_DOMAIN_URL+"/"+APP_BCGOGO.UpYun.UP_YUN_BUCKET+"/",
                "ext":{
                    "policy":$("#policy").val(),
                    "signature":$("#signature").val()
                }
            },

            "startUploadCallback":function(message) {
                // 设置 视图组件 uploading 状态
                imageUploaderView.setState("uploading");
            },
            "uploadCompleteCallback":function(message, data) {
                var dataInfoJson = JSON.parse(data.info);
                productImageUrlData.push(dataInfoJson.url);
            },
            "uploadErrorCallback":function(message, data) {
                var errorData = JSON.parse(data.info);
                errorData["content"] = data.info;
                saveUpLoadImageErrorInfo(errorData);
                nsDialog.jAlert("上传图片失败！");
            },
            "uploadAllCompleteCallback":function() {
                // 设置 视图组件  idle 状态
                imageUploaderView.setState("idle");
                //更新input
                $("input[id^='imageCenterDTO.productInfoImagePaths']").each(function(index){
                    $(this).val(G.Lang.normalize(productImageUrlData[index],""));
                });
                imageUploaderView.update(getTotelUrlToData(productImageUrlData));
            }
        });

        /**
         * 视图组建的 样例代码
         * */
        var imageUploaderView = new App.Module.ImageUploaderView();
        imageUploaderView.init({
            // 你所需要注入的 dom 节点
            selector:"#product_imageUploaderView",
            width:460,
            height:150,
            iWidth:60,
            iHeight:60,
            maxFileNum:5,
            // 当删除某张图片时会触发此回调
            onDelete: function (event, data, index) {
                imageUploader.getFlashObject().deleteFile(index);

                // 从已获得的图片数据池中删除 图片数据
                productImageUrlData.splice(index, 1);
                //更新input
                $("input[id^='imageCenterDTO.productInfoImagePaths']").each(function(index){
                    $(this).val(G.Lang.normalize(productImageUrlData[index],""));
                });
                // 设置 视图组件  idle 状态
                imageUploaderView.setState("idle");
                imageUploaderView.update(getTotelUrlToData(productImageUrlData));
            },
            onAssistButtonClick:function(event, data, index) {
                productImageUrlData.unshift(productImageUrlData.splice(index,1));
                //更新input
                $("input[id^='imageCenterDTO.productInfoImagePaths']").each(function(index){
                    $(this).val(G.Lang.normalize(productImageUrlData[index],""));
                });
                // 设置 视图组件  idle 状态
                imageUploaderView.setState("idle");
                imageUploaderView.update(getTotelUrlToData(productImageUrlData));
            }
        });

        if(!G.Lang.isEmpty(productInfoBigImageDetailDTOs)){
            //更新input
            $("input[id^='imageCenterDTO.productInfoImagePaths']").each(function(index){
                if(productInfoBigImageDetailDTOs[index]){
                    $(this).val(G.Lang.normalize(productInfoBigImageDetailDTOs[index].imagePath,""));
                    if(!G.Lang.isEmpty($(this).val()))
                        productImageUrlData.push($(this).val());
                }else{
                    $(this).val("");
                }
            });
            // 设置 视图组件  idle 状态
            imageUploaderView.setState("idle");
            imageUploaderView.update(getTotelUrlToData(productImageUrlData));
        }
    }

    $('[pageSource="goodsInSalesEditor"] .savePromotionsBtn-InSales').click(function(){
        var _me=this;
        if($(_me).attr("disabled")){
            return;
        }
        $(_me).attr("disabled", true);
        $(".J-initialCss").placeHolder("clear");
        $(this).closest("#promotionsForm").ajaxSubmit({
            url:"promotions.do?method=validateSavePromotionsForInSales",
            dataType: "json",
            type: "POST",
            success: function(result){
                if(G.isEmpty(result)){
                    return;
                }
                if(!result.success){
                    nsDialog.jAlert(result.msg);
                    return;
                }
                var lappingMap=result.data;
                if(G.isEmpty(lappingMap)){
                    _doSavePromotionsInGoodsInSalesEditor(_me,function(){
                        if($(_me).attr("pagetype") == 'manageFreeShipping') {
                            doHiddeDiv();
                        } else {
                            doCloseDialog(_me);
                        }
                    });
                }else{
                     $(_me).removeAttr("disabled");
                    var errorMsg='<div>对不起，';
                    for(var key in lappingMap){
                        errorMsg+='<br/>';
                        var promotion=lappingMap[key];
                        errorMsg+='<div>该商品在同时段已参与促销'+ G.normalize(promotion.name)+'，是否覆盖之前的促销？';
                    }
                    errorMsg+='</div>';
                    $(errorMsg).dialog({
                        width: 370,
                        height:180,
                        modal: true,
                        resizable: true,
                        title: "友情提示",
                        buttons:{
                            "确定":function(){
                                $(this).dialog("close");
                                _doSavePromotionsInGoodsInSalesEditor(_me,function(){
                                    if($(_me).attr("pagetype") == 'manageFreeShipping') {
                                        doHiddeDiv();
                                    } else {
                                        doCloseDialog(_me);
                                    }
                                });
                            },
                            "取消":function(){
                                $(this).dialog("close");
                            }
                        }
                    });

                }

            },
            error:function(){
                nsDialog.jAlert("网络异常！");
            }
        });

        function _doSavePromotionsInGoodsInSalesEditor(target,callBack){
            savePromotions(target,function(result,flag){
                if(!flag){
                    return;
                }
                $(target).removeAttr("disabled");
                nsDialog.jAlert("促销创建成功。",null,function(){
                    var promotions=result.data;
                    $("#promotionsId").val(promotions.idStr);
                    getPromotionDetailForInsales(function(promotionList){
                        if(!G.isEmpty(promotions)&&promotions.type=="BARGAIN"){
                            promotionList=promotionList||{};
                            promotionList[promotionList.length]=promotions;
                        }
                        initShopPromotionInfo(promotionList);
                        if(!G.isEmpty(promotions)){
                            $(".promotionSelector").val(promotions.idStr);
                        }
                        $(".promotionSelector").change();
                    });
                    callBack();
                })
            });
        }

    })


});


$(function(){

    $("[name='commodityCode']").live("keyup",function () {
        var pos = APP_BCGOGO.StringFilter.getCursorPosition(this, APP_BCGOGO.StringFilter.inputtingCommodityCodeFilter);
        $(this).val(APP_BCGOGO.StringFilter.inputtingCommodityCodeFilter($(this).val()));
        APP_BCGOGO.StringFilter.setCursorPosition(this, pos);
    }).live("blur", function () {
                var $thisDom = $(this);
                $thisDom.val(APP_BCGOGO.StringFilter.commodityCodeFilter($thisDom.val()));
                var validateCommodityCode = $thisDom.val();
                if ($(this).val() && $(this).val() != $(this).attr("lastValue")) {
                    var ajaxUrl = "stockSearch.do?method=ajaxToGetProductByCommodityCode";
                    var ajaxData = {
                        commodityCode: validateCommodityCode
                    };
                    APP_BCGOGO.Net.syncAjax({
                        url: ajaxUrl,
                        dataType: "json",
                        data: ajaxData,
                        success: function (jsonStr) {
                            if (jsonStr[0] && jsonStr[0].productIdStr) {
                                nsDialog.jAlert("商品编码【" + validateCommodityCode + "】已经使用，请重新输入商品编码", null, function () {
                                    $thisDom.val("");
                                });
                            }
                        },
                        error: function () {
                            nsDialog.jAlert("网络异常，请联系管理员！", null, function () {
                                $thisDom.val("");
                            })
                        }
                    });
                }
            });

    $('[name="productVehicleBrand"]').live("keyup click", function (e) {
        e.stopImmediatePropagation();
        var eventKeyCode = e.which || e.keyCode;
        if (GLOBAL.Interactive.keyNameFromKeyCode(eventKeyCode).search(/left|up|right|down/g) == -1) {
            var $thisDom = $(this);
            var pos = APP_BCGOGO.StringFilter.getCursorPosition($thisDom[0], APP_BCGOGO.StringFilter.inputtingProductNameFilter);
            $thisDom.val(APP_BCGOGO.StringFilter.inputtingProductNameFilter($thisDom.val()));
            APP_BCGOGO.StringFilter.getCursorPosition($thisDom[0], pos);
            var searchField = $thisDom.attr("searchField");
            var cacheField = $thisDom.attr("cacheField");
            var thisDomVal = $thisDom.val();
            if (newProductCache.isTimeOut($thisDom.val(), cacheField)) {
                var ajaxData = {
                    searchWord: $thisDom.val(),
                    searchField: searchField,
                    uuid: GLOBAL.Util.generateUUID()
                };
                var ajaxUrl = "product.do?method=searchBrandSuggestion";
                APP_BCGOGO.wjl.LazySearcher.lazySearch(ajaxUrl, ajaxData, function (jsonStr) {
                    var source = [];
                    if (jsonStr && jsonStr.length > 0) {
                        for (var i = 0, len = jsonStr.length; i < len; i++) {
                            if (!G.Lang.isEmpty(jsonStr[i].name)) {
                                source.push(APP_BCGOGO.StringFilter.inputtingProductNameFilter(jsonStr[i].name));
                            }
                        }
                    }
                    newProductCache[cacheField].data[thisDomVal] = {};
                    newProductCache[cacheField].data[thisDomVal]["source"] = source;
                    newProductCache[cacheField].data[thisDomVal]["syncTime"] = (new Date()).valueOf();
                    if ($(document.activeElement).attr("name") == $thisDom.attr("name")) {
                        $thisDom.autocomplete({
                            minLength: 0,
                            delay: 0,
                            source: source
                        });
                        $thisDom.autocomplete("search", "");
                    }
                })
            } else {
                $thisDom.autocomplete({
                    minLength: 0,
                    delay: 0,
                    source: newProductCache[cacheField].data[$thisDom.val()]["source"]
                });
                $thisDom.autocomplete("search", "");
            }
        }
    }).live("blur",function () {
                if ($(this).val() != $(this).attr("lastVal")) {
                    $('[name="productVehicleModel"]').val("");
                }
            }).live("focus", function () {
                $(this).attr("lastVal", $(this).val());
            });

    $("[name='productVehicleModel']").live("keyup click",function(e){
        var eventKeyCode = e.which || e.keyCode;
        if (GLOBAL.Interactive.keyNameFromKeyCode(eventKeyCode).search(/left|up|right|down/g) == -1) {
            var $thisDom = $(this);
            var pos = APP_BCGOGO.StringFilter.getCursorPosition($thisDom[0], APP_BCGOGO.StringFilter.inputtingProductNameFilter);
            $thisDom.val(APP_BCGOGO.StringFilter.inputtingProductNameFilter($thisDom.val()));
            APP_BCGOGO.StringFilter.getCursorPosition($thisDom[0], pos);
            var searchField = $thisDom.attr("searchField");
            var cacheField = $thisDom.attr("cacheField");
            var brandVal = $('[name="productVehicleBrand"]').val();
            var sourceKey = brandVal + "_&&_" + $thisDom.val();
            if (newProductCache.isTimeOut(sourceKey, cacheField)) {
                var ajaxData = {
                    brandValue: brandVal,
                    searchWord: $thisDom.val(),
                    searchField: searchField,
                    uuid: GLOBAL.Util.generateUUID()
                };
                var ajaxUrl = "product.do?method=searchBrandSuggestion";
                APP_BCGOGO.wjl.LazySearcher.lazySearch(ajaxUrl, ajaxData, function (jsonStr) {
                    var source = [];
                    if (jsonStr && jsonStr.length > 0) {
                        for (var i = 0, len = jsonStr.length; i < len; i++) {
                            if (!G.Lang.isEmpty(jsonStr[i].name)) {
                                source.push(APP_BCGOGO.StringFilter.inputtingProductNameFilter(jsonStr[i].name));
                            }
                        }
                    }
                    newProductCache[cacheField].data[sourceKey] = {};
                    newProductCache[cacheField].data[sourceKey]["source"] = source;
                    newProductCache[cacheField].data[sourceKey]["syncTime"] = (new Date()).valueOf();
                    if ($(document.activeElement).attr("name") == $thisDom.attr("name")) {
                        $thisDom.autocomplete({
                            minLength: 0,
                            delay: 0,
                            source: source,
                            select:function(e,ui){
                                $.ajax({
                                    type: "POST",
                                    data:{
                                        modelValue:ui.item.value,
                                        searchField:"brand"
                                    },
                                    url: "product.do?method=searchBrandSuggestion",
                                    cache: false,
                                    dataType: "json",
                                    error: function (XMLHttpRequest, error, errorThrown) {
                                    },
                                    success: function (jsonStr) {
                                        if (jsonStr && jsonStr.length > 0 && !G.Lang.isEmpty(jsonStr[0].name)) {
                                            $('[name="productVehicleBrand"]').val(jsonStr[0].name);
                                        }
                                    }
                                });
                            }
                        });
                        $thisDom.autocomplete("search", "");
                    }
                })
            } else {
                $thisDom.autocomplete({
                    minLength: 0,
                    delay: 0,
                    source: newProductCache[cacheField].data[sourceKey]["source"]  ,
                    select: function (e, ui) {
                        $.ajax({
                            type: "POST",
                            data: {
                                modelValue: ui.item.value,
                                searchField: "brand"
                            },
                            url: "product.do?method=searchBrandSuggestion",
                            cache: false,
                            dataType: "json",
                            error: function (XMLHttpRequest, error, errorThrown) {
                            },
                            success: function (jsonStr) {
                                if (jsonStr && jsonStr.length > 0 && !G.Lang.isEmpty(jsonStr[0].name)) {
                                    $('[name="productVehicleBrand"]').val(jsonStr[0].name);
                                }
                            }
                        });
                    }
                });
                $thisDom.autocomplete("search", "");
            }
        }
    });
});

function setSelectedProductCategoryData(selectDataInfo,categoryId,categoryName,categoryType){
    $(".J_productCategoryInfoSpan").text(selectDataInfo);
    $("#productCategoryId").val(categoryId);
    $("#productCategoryName").val(categoryName);
    $("#productCategoryType").val(categoryType);
}

</script>
</head>

<body class="bodyMain">
<%@include file="/WEB-INF/views/header_html.jsp" %>


<input type="hidden" id="policy" value="${upYunFileDTO.policy}">
<input type="hidden" id="signature" value="${upYunFileDTO.signature}">
<input type="hidden" field="inventoryAveragePrice">



<div class="i_main clear">
<div class="clear i_height"></div>
<div class="titBody">
<jsp:include page="supplyCenterLeftNavi.jsp">
    <jsp:param name="currPage" value="inSalingGoodsList"/>
</jsp:include>
<!-- TODO上架信息请关注此处 -->
<div class="content-main">
<div class="add-product-follow">
    <span style="margin-left:83px;">选择上架商品</span>
    <span style="margin-left:163px;">编辑商品详细信息</span>
    <span style="margin-left:176px;">上架成功</span>
</div>

<div class="add-product">
<div class="content-title">
    <div class="bg-top-hr"></div>
    <div class="bar-tab">
        <span class="label" >填写商品信息</span>
    </div>
</div>

<div class="content-details-anchor">
    <a href="#info-basic" class="anchor-on">基本信息</a>&nbsp;&nbsp;-&nbsp;
    <a href="#info-details">详细描述</a>&nbsp;&nbsp;-&nbsp;
    <a href="#info-price-count">价格数量</a>&nbsp;&nbsp;-&nbsp;
    <a href="#info-shop-picture">商品图片</a>&nbsp;&nbsp;-&nbsp;
    <a href="#info-promo-edit">促销编辑</a>
</div>

<form:form commandName="fromProductDTO" id="goodsInSalesForm" action="goodsInOffSales.do?method=toPreviewShopProductDetail" target="_blank" method="post" name="thisform">
<input type="hidden" id="productId" name="productLocalInfoId" value="${productId}"/>

<input type="hidden" name="promotionsDTO.id" id="promotionsId"/>
<input type="hidden" name="promotionsDTO.promotionsProductDTO.productLocalInfoId" value="${productId}" field="productId"/>
    <span class="dataSpan">
        <input type="hidden" name="promotionsDTO.promotionsProductDTO.discountAmount" field="discountAmount"/>
        <input type="hidden" name="promotionsDTO.promotionsProductDTO.bargainType" field="bargainType"/>
        <input type="hidden" name="promotionsDTO.promotionsProductDTO.limitFlag" field="limitFlag" value="false"/>
        <input type="hidden" name="promotionsDTO.promotionsProductDTO.limitAmount" field="limitAmount"/>
    </span>
<div class="content-details">
    <dl>
        <dt>
            <span class="item-number">1、</span>
            <span class="item-name"><a data-anchor name="info-basic">商品基本信息</a></span>
        </dt>
        <dd>
            <div>
                <input type="hidden" id="productCategoryName" name="productCategoryName"/>
                <input type="hidden" id="productCategoryId" name="productCategoryId"/>
                <input type="hidden" id="productCategoryType"/>
                <em class="label-import-tag">*</em>
                <span class="label-name">您选择的分类: </span>
                <span class="label-content J_productCategoryInfoSpan"></span>
                <span class="button-select-category button-blue-gradient J_selectProductCategory" style="display: none">选择分类</span>
            </div>
            <div>
                <span class="label-name">商品编号</span>
                <span class="label-content"><input name="commodityCode" class="txt" type="text"/></span>
            </div>
            <div>
                <em class="label-import-tag">*</em>
                <span class="label-name">品&nbsp;&nbsp;&nbsp;&nbsp;名</span>
                <span class="label-content"><input name="name" class="txt" type="text"/></span>
                <span class="label-name">品&nbsp;&nbsp;&nbsp;&nbsp;牌</span>
                <span class="label-content"><input name="brand" class="txt" type="text"/></span>
            </div>
            <div>
                <span class="label-name">规&nbsp;&nbsp;&nbsp;&nbsp;格</span>
                <span class="label-content"><input name="spec" class="txt" type="text"/></span>
                <span class="label-name">型&nbsp;&nbsp;&nbsp;&nbsp;号</span>
                <span class="label-content"><input name="model" class="txt" type="text"/></span>
            </div>
            <div>
                <span class="label-name">车辆品牌</span>
                                <span class="label-content">
                                    <input cachefield="productVehicleBrandSource" searchfield="brand" type="text" name="productVehicleBrand" class="txt"/>
                                </span>
                <span class="label-name">车&nbsp;&nbsp;&nbsp;&nbsp;型</span>
                                <span class="label-content">
                                    <input cachefield="productVehicleModelSource" searchfield="model" type="text" name="productVehicleModel" class="txt"/>
                                </span>
            </div>
            <div class="guaranteePeriodDiv">
                <span class="label-name">质保时间</span>
                                <span class="label-content"><input checked="true" name="guaranteePeriodRd" class="month12" type="radio"/>一年<input name="guaranteePeriodRd" class="month24" type="radio"/>二年
                                   <input name="guaranteePeriodRd" class="month-define"  type="radio"/>自定义<span class="self-define-guaranteePeriod" style="display: none"> <input name="guaranteePeriod" value="12" class="txt" style="width:82px;" type="text"/>&nbsp;&nbsp;个月</span></span>
            </div>
        </dd>
        <div class="cl"></div>


        <dt>
            <span class="item-number">2、</span>
            <span class="item-name"><a data-anchor name="info-price-count">价格数量</a></span>
        </dt>
        <dd>
            <c:if test="${productId==null}">
                <div>
                    <span class="label-name">零&nbsp;售&nbsp;价</span>
                    <span class="label-content"><input name="recommendedPrice" class="price-input txt" type="text"/></span>
                    <span class="label-note">零售价为库存设置的零售价格，不对外公开</span>
                </div>
                <div>
                    <span class="label-name">批&nbsp;发&nbsp;价</span>
                    <span class="label-content"><input name="tradePrice" class="price-input txt" type="text"/></span>
                    <span class="label-note">批发价为库存设置的批发价格，不对外公开</span>
                </div>
                <div>
                    <em class="label-import-tag">*</em>
                    <span class="label-name">上架售价</span>
                    <span class="label-content"><span class="arialFont" style="padding-right: 2px">&yen;</span><input name="inSalesPrice" maxlength="8" class="price-input txt" type="text"/></span>
                    <span class="label-note">上架售价为对外公开的价格</span>
                </div>
            </c:if>
            <c:if test="${productId!=null}">
                <div>
                    <span class="label-name">零&nbsp;售&nbsp;价</span>
                                <span class="label-content">
                                    <span class="arialFont">&yen;</span><span name="recommendedPrice">0</span>
                                    <input type="hidden" name="recommendedPrice"/>
                                </span>
                    <span class="label-name" style="padding-left: 5px">批&nbsp;发&nbsp;价</span>
                                <span class="label-content">
                                    <span class="arialFont">&yen;</span><span name="tradePrice">0</span>
                                    <input type="hidden" name="tradePrice"/>
                                </span>
                </div>
                <div>
                    <em class="label-import-tag">*</em>
                    <span class="label-name">上架售价</span>
                    <span class="label-content"><span class="arialFont" style="padding-right: 2px">&yen;</span><input name="inSalesPrice" class="price-input txt" type="text"/></span>
                    <span class="label-note">上架售价为对外公开的价格</span>
                </div>
                <div>
                    <span class="label-name">当前库存</span>
                    <span class="label-content" name="inventoryNum">0</span>
                </div>
            </c:if>


            <div>
                <em class="label-import-tag">*</em>
                <span class="label-name">上&nbsp;架&nbsp;量</span>
                    <span class="label-content">
                        <input type="hidden" name="inSalesAmount"/>
                        <span>
                            <input type="radio" name="radio-inSalesAmount" field="radio-inSalesAmount-amount" checked="true" style="vertical-align: top"/>
                            <input field="inSalesAmount" class="price-input txt" type="text" value="0"/>
                        </span>
                        <span>
                            <input type="radio" name="radio-inSalesAmount" field="radio-inSalesAmount-exist" style="vertical-align: top;"/>&nbsp;显示为"有货"
                        </span>
                    </span>
            </div>
            <div>
                <em class="label-import-tag">*</em>
                <span class="label-name">单&nbsp;&nbsp;&nbsp;&nbsp;位</span>
                    <%--<input name="unit" class="price-input txt" type="text"/>--%>
                        <span id="_unitContainer" class="unit-Container">
                            <c:if test="${productId==null}">
                                <input type="txt" name="unit" maxlength="10" class="txt" style="width:50px;">
                            </c:if>
                        </span>
            </div>
        </dd>
        <div class="cl"></div>


        <dt>
            <span class="item-number">3、</span>
            <span class="item-name"><a data-anchor name="info-shop-picture">商品图片</a></span>
        </dt>
        <dd>
            <input type="hidden" id="imageCenterDTO.productInfoImagePaths0" name="imageCenterDTO.productInfoImagePaths[0]"/>
            <input type="hidden" id="imageCenterDTO.productInfoImagePaths1" name="imageCenterDTO.productInfoImagePaths[1]"/>
            <input type="hidden" id="imageCenterDTO.productInfoImagePaths2" name="imageCenterDTO.productInfoImagePaths[2]"/>
            <input type="hidden" id="imageCenterDTO.productInfoImagePaths3" name="imageCenterDTO.productInfoImagePaths[3]"/>
            <input type="hidden" id="imageCenterDTO.productInfoImagePaths4" name="imageCenterDTO.productInfoImagePaths[4]"/>

            <div style="height: 60px">
                <div style="line-height:24px;float: left;width: 80px;height: 60px">选择本地图片</div>
                <div style="float: left;width: 90px;height: 60px" id="product_imageUploader"></div>
                <div style="float: left;color: red;padding-left: 5px">提示： </div>
                <div style="float: left">1. 如果您要上传商品图片，请点击以上按钮。<br>2. 所选图片都必须是 jpg、png 或 jpeg 格式。<br>3. 每张图片的大小不得超过5M。<br> 4. 您可一次选择多张图片哦！最多上传 5 张图片。</div>
            </div>
            <div id="product_imageUploaderView" style="width:460px;height:150px;position: relative;"></div>
        </dd>
        <div class="cl"></div>



        <dt>
            <span class="item-number">4、</span>
            <span class="item-name"><a data-anchor name="info-details">详细描述</a></span>
        </dt>
        <dd>
            <script id="productDescriptionEditor" name="description" type="text/plain"></script>
        </dd>
        <div class="cl"></div>


        <dt>
            <span class="item-number">5、</span>
            <span class="item-name"><a data-anchor name="info-promo-edit">参与促销</a></span>
        </dt>
        <dd>
            <span class="label-name">参与促销:</span>
                            <span class="label-content">
                                <select class="promotionSelector txt"   style="width: 150px;font-size: 12px"></select>
                                <span onclick="getPromotionManagerAlert(this)" pageSource="inSalingGoodsList" class="button-create-promotions button-yellow-deep-gradient">创建新促销</span>
                            </span>
            <div style="margin-top: 10px">
                <span class="label-name">促销内容:</span>
                <span id="pContent" class="label-content"></span>
            </div>
        </dd>
        <div class="cl"></div>
    </dl>
    </form:form>

    <div class="group-button-finish">
        <span id="saveGoodsInSalesBtn" class="button-submit button-yellow-deep-gradient">提交上架</span>
        <span onclick="previewShopProductDetail()" class="button-preview button-grey-light-gradient">预&nbsp;&nbsp;览</span>
    </div>
</div>
</div>
</div>
</div>

<div pageSource="goodsInSalesEditor">
    <%@ include file="./promotions/promotionManagerAlert.jsp" %>
</div>

<%@ include file="productCategorySelector.jsp" %>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>