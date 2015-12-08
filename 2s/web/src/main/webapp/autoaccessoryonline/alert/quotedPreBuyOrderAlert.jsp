<%--
  Created by IntelliJ IDEA.
  User: ndong
  Date: 13-10-30
  Time: 下午2:35
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<link rel="stylesheet" type="text/css" href="js/components/themes/bcgogo-categorySelector<%=ConfigController.getBuildVersion()%>.css"/>
<script type="text/javascript" charset="utf-8" src="js/components/ui/bcgogo-categorySelector<%=ConfigController.getBuildVersion()%>.js"></script>
<%--<<div id="qutedPreBuyOrderAlert" class="alertMain" style="display: none">--%>

<%--</div>--%>
<script type="text/javascript">
var categorySelector="";
$().ready(function(){
    //分类选择
    categorySelector = new App.Module.CategorySelector();
    APP_BCGOGO.Net.syncAjax({
        url: "searchInventoryIndex.do?method=getAllProductCategorySelectorData",
        type: "POST",
        dataType: "json",
        success:function (data) {
            categorySelector.init({
                "selector":"#productCategorySelectorDiv",
                "data":data,
                "isHighlight":true,
                "highlightClassName":"yellow_color",
                "onInput":function(event, index, value, viewData,parentIdArr) {
                    if(categorySelector.xhr) {
                        categorySelector.xhr.abort();
                    }
                    if(G.isEmpty(value)){
                        categorySelector.resetCategory(index,viewData);
                        return viewData;
                    }

                    var url = "searchInventoryIndex.do?method=getProductCategorySuggestion";
                    if(index === 0) {
                        categorySelector.xhr = APP_BCGOGO.Net.asyncAjax({
                            url:url,
                            type: "POST",
                            data:{searchWord:value,productCategoryType:"FIRST_CATEGORY",parentId:-1},
                            dataType: "json",
                            success:function (result) {
                                var resultData = [];
                                if(!G.isEmpty(result)&&!G.isEmpty(result.data)){
                                    $.each(result.data,function(index,val){
                                        if(!G.isEmpty(val) && !G.isEmpty(val.details)){
                                            resultData.push({id:val.details["id"],name:val.details["name"]});
                                        }
                                    });
                                }
                                categorySelector.resetCategory(index,resultData,value);
                            }
                        });
                    } else if(index === 1) {
                        categorySelector.xhr = APP_BCGOGO.Net.asyncAjax({
                            url:url,
                            type: "POST",
                            data:{searchWord:value,productCategoryType:"SECOND_CATEGORY",parentId:parentIdArr[index-1]["id"]},
                            dataType: "json",
                            success:function (result) {
                                var resultData = [];
                                if(!G.isEmpty(result)&&!G.isEmpty(result.data)){
                                    $.each(result.data,function(index,val){
                                        if(!G.isEmpty(val) && !G.isEmpty(val.details)){
                                            resultData.push({id:val.details["id"],name:val.details["name"]});
                                        }
                                    });
                                }
                                categorySelector.resetCategory(index,resultData,value);
                            }
                        });
                    } else if(index === 2) {
                        categorySelector.xhr = APP_BCGOGO.Net.asyncAjax({
                            url:url,
                            type: "POST",
                            data:{searchWord:value,productCategoryType:"THIRD_CATEGORY",parentId:parentIdArr[index-1]["id"]},
                            dataType: "json",
                            success:function (result) {
                                var resultData = [];
                                if(!G.isEmpty(result)&&!G.isEmpty(result.data)){
                                    $.each(result.data,function(index,val){
                                        if(!G.isEmpty(val) && !G.isEmpty(val.details)){
                                            resultData.push({id:val.details["id"],name:val.details["name"]});
                                        }
                                    });
                                }
                                categorySelector.resetCategory(index,resultData,value);
                            }
                        });
                    }
                },
                onSelect:function(event, data, index) {
                    $("#productCategorySelector #usedProductCategorySelect").val("");
                    var selectDataInfo = "";
                    if(index === 0) {
                        selectDataInfo = data[0]["level_1_name"];
                        $("#productCategorySelector #_productCategorySelectedId").val("");
                        $("#productCategorySelector #_productCategorySelectedName").val("");
                        $("#productCategorySelector #_productCategorySelectedType").val("");
                    } else if(index === 1) {
                        selectDataInfo = data[0]["level_1_name"]+" >> "+data[0]["level_2_name"];
                        $("#productCategorySelector #_productCategorySelectedId").val(data[0]["level_2_id"]);
                        $("#productCategorySelector #_productCategorySelectedName").val(data[0]["level_2_name"]);
                        $("#productCategorySelector #_productCategorySelectedType").val("SECOND_CATEGORY");
                    } else if(index === 2) {
                        selectDataInfo = data[0]["level_1_name"]+" >> "+data[0]["level_2_name"]+" >> "+data[0]["level_3_name"];
                        $("#productCategorySelector #_productCategorySelectedId").val(data[0]["level_3_id"]);
                        $("#productCategorySelector #_productCategorySelectedName").val(data[0]["level_3_name"]);
                        $("#productCategorySelector #_productCategorySelectedType").val("THIRD_CATEGORY");
                    }
                    $("#productCategorySelector #_productCategorySelectedInfo").text(selectDataInfo);
                }
            });

        },
        error:function(){
            nsDialog.jAlert("网络异常！");
        }
    });

    var $currentClickBtnObject;
    if($(".batchSetProductCategory")){
        //批量设置商品分类
        $(".batchSetProductCategory").bind("click",function(){
            var obj = this
            if($(".itemChk:checked").length<=0){
                nsDialog.jAlert("请选择要设置分类的商品!");
                return false;
            }
            $("#productCategorySelector").dialog({
                width: "auto",
                modal: true,
                resizable:false,
                title: "批量设置商品分类",
                beforeclose: function(event, ui) {
                    resetProductCategorySelector();
                    categorySelector.reset();
                    $currentClickBtnObject = null;
                    return true;
                },
                open: function () {
                    $currentClickBtnObject = $(obj);
                    return true;
                }
            });
        });
    }

    $(".J_selectProductCategory").bind("click",function(e){
        var obj = this
        $("#productCategorySelector").dialog({
            width: "auto",
            modal: true,
            resizable:false,
            title: "选择商品分类",
            beforeclose: function(event, ui) {
                resetProductCategorySelector();
                categorySelector.reset();
                $currentClickBtnObject = null;
                return true;
            },
            open: function () {
                $currentClickBtnObject = $(obj);
                var $parent = $currentClickBtnObject.parent();

                var productCategoryType = $parent.find("input[id$='productCategoryType']").val();
                var productCategoryId = $parent.find("input[name$='productCategoryId']").val();
                if(!G.isEmpty(productCategoryId) && !G.isEmpty(productCategoryType)){
                    var productCategoryName = $parent.find("input[name$='productCategoryName']").val();
                    var selectDataInfo = $parent.find(".J_productCategoryInfoSpan").text();
                    if("SECOND_CATEGORY" == productCategoryType){
                        categorySelector.reset({"level_2_id":productCategoryId},2);
                    }else if("THIRD_CATEGORY" == productCategoryType){
                        categorySelector.reset({"level_3_id":productCategoryId},3);
                    }
                    $("#productCategorySelector #_productCategorySelectedId").val(productCategoryId);
                    $("#productCategorySelector #_productCategorySelectedName").val(productCategoryName);
                    $("#productCategorySelector #_productCategorySelectedType").val(productCategoryType);
                    $("#productCategorySelector #_productCategorySelectedInfo").text(selectDataInfo);
                }

                return true;
            }
        });
    });
    //快速搜索
    $("#productCategorySelector #searchProductCategoryBtn").bind("click",function(e){
        e.preventDefault();
        var searchWord = $("#productCategorySelector #searchWord").val().replace(/[\ |\\]/g, "");
        if(G.isEmpty(searchWord)){
            nsDialog.jAlert("请输入搜索关键字！");
            return;
        }
        APP_BCGOGO.Net.asyncAjax({
            url: "searchInventoryIndex.do?method=getProductCategoryDetailList",
            type: "POST",
            data:{searchWord:searchWord},
            dataType: "json",
            success:function (data) {
                $("#productCategorySelector #usedProductCategorySelect").val("");
                $("#productCategorySelector #productCategorySelectorDiv").hide();

                $("#productCategorySelector #_productCategorySelectedId").val("");
                $("#productCategorySelector #_productCategorySelectedName").val("");
                $("#productCategorySelector #_productCategorySelectedType").val("");
                $("#productCategorySelector #_productCategorySelectedInfo").text("");
                $("#productCategorySelector #searchResultCount").text("0");
                if(G.isEmpty(data)){
                    $("#productCategorySelector #hasSearchResultDiv").hide();
                    $("#productCategorySelector #hasNotSearchResultDiv").show();
                }else{
                    var searchResultHtml = "<ul>";
                    $.each(data,function(index,val){
                        var itemData={};
                        itemData["firstCategoryName"] = val["firstCategoryName"];
                        itemData["firstCategoryId"] = val["firstCategoryIdStr"];
                        itemData["secondCategoryName"] = val["secondCategoryName"];
                        itemData["secondCategoryId"] = val["secondCategoryIdStr"];
                        itemData["name"] = val["name"];
                        itemData["id"] = val["idStr"];
                        var itemTextHtml = (index+1)+"&nbsp;&nbsp;&nbsp;"+val["firstCategoryName"]+" >> <span class='J_highlight'>"+val["secondCategoryName"];
                        if("THIRD_CATEGORY" == val["categoryType"]){
                            itemData["thirdCategoryName"] = val["thirdCategoryName"];
                            itemData["thirdCategoryId"] = val["thirdCategoryIdStr"];
                            itemTextHtml += " >> "+val["thirdCategoryName"];
                        }
                        itemTextHtml +="</span>";

                        searchResultHtml += "<li class='item-li J_productCategoryItemData' item-data='"+encodeURIComponent(JSON.stringify(itemData))+"'>";
                        searchResultHtml +=itemTextHtml;
                        searchResultHtml +="</li>"
                    });
                    searchResultHtml +="</ul>";
                    $("#productCategorySelector #searchResultCount").text(data.length);
                    $("#productCategorySelector #searchResultDiv").html(searchResultHtml);
                    $("#productCategorySelector .J_highlight").highlight(searchWord, {className: "yellow_color"});
                    $("#productCategorySelector #hasNotSearchResultDiv").hide();
                    $("#productCategorySelector #hasSearchResultDiv").show();
                }
            },
            error:function(){
                nsDialog.jAlert("网络异常！");
            }
        });
    });
    //搜索结果单击
    $("#productCategorySelector .J_productCategoryItemData").live("click",function(e){
        e.preventDefault();
        var selectDataStr = $(this).attr("item-data");
        var selectData = JSON.parse(decodeURIComponent(selectDataStr));
        $("#productCategorySelector #_productCategorySelectedId").val(selectData["id"]);
        $("#productCategorySelector #_productCategorySelectedName").val(selectData["name"]);
        $("#productCategorySelector #_productCategorySelectedType").val(selectData["categoryType"]);
        var selectDataInfo = selectData["firstCategoryName"]+" >> "+selectData["secondCategoryName"];
        if(G.isNotEmpty(selectData["thirdCategoryName"])){
            selectDataInfo +=" >> "+selectData["thirdCategoryName"];
        }
        $("#productCategorySelector #_productCategorySelectedInfo").text(selectDataInfo);
        $(this).parent("ul").find("li").removeClass("actived");
        $(this).addClass("actived");
    });
    //搜索结果双击
//    $("#productCategorySelector .J_productCategoryItemData").live("dblclick",function(e){
//        e.preventDefault();
//        categorySelector.reset();
//        var selectDataStr = $(this).attr("item-data");
//        var selectData = JSON.parse(decodeURIComponent(selectDataStr));
//        $("#productCategorySelector #_productCategorySelectedId").val(selectData["id"]);
//        $("#productCategorySelector #_productCategorySelectedName").val(selectData["name"]);
//        $("#productCategorySelector #_productCategorySelectedType").val(selectData["categoryType"]);
//        var selectDataInfo = selectData["firstCategoryName"]+" >> "+selectData["secondCategoryName"];
//        if(G.isNotEmpty(selectData["thirdCategoryName"])){
//            selectDataInfo +=" >> "+selectData["thirdCategoryName"];
//        }
//        $("#productCategorySelector #_productCategorySelectedInfo").text(selectDataInfo);
//        if($.isFunction(window.batchSetSelectedProductCategoryData) && G.isNotEmpty($currentClickBtnObject) && $currentClickBtnObject.hasClass("batchSetProductCategory")){
//            batchSetSelectedProductCategoryData(selectDataInfo,selectData["id"],selectData["name"],selectData["categoryType"],$currentClickBtnObject.closest("form"));
//        }else if($.isFunction(window.setSelectedProductCategoryData) && G.isNotEmpty($currentClickBtnObject)){
//            setSelectedProductCategoryData(selectDataInfo,selectData["id"],selectData["name"],selectData["categoryType"],$currentClickBtnObject.parent());
//        }
//        $("#productCategorySelector").dialog("close");
//    });
    //确定按钮
//        $("#productCategorySelector #productCategorySelectorConfirmBtn").bind("click",function(e){
//            e.preventDefault();
//            if($("#productCategorySelector #productCategorySelectorConfirmBtn").attr("locked")=="true"){
//                return;
//            }
//            var selectDataInfo = $("#productCategorySelector #_productCategorySelectedInfo").text();
//            var categoryId = $("#productCategorySelector #_productCategorySelectedId").val();
//            var categoryName = $("#productCategorySelector #_productCategorySelectedName").val();
//            var categoryType = $("#productCategorySelector #_productCategorySelectedType").val();
//            if(!G.isEmpty(categoryName)){
//                if($.isFunction(window.batchSetSelectedProductCategoryData) && G.isNotEmpty($currentClickBtnObject) && $currentClickBtnObject.hasClass("batchSetProductCategory")){
//                    batchSetSelectedProductCategoryData(selectDataInfo,categoryId,categoryName,categoryType,$currentClickBtnObject.closest("form"));
//                }else if($.isFunction(window.setSelectedProductCategoryData) && G.isNotEmpty($currentClickBtnObject)){
//                    setSelectedProductCategoryData(selectDataInfo,categoryId,categoryName,categoryType,$currentClickBtnObject.parent());
//                }
//            }else{
//                nsDialog.jAlert("请选择二级或者三级商品分类！");
//                return;
//            }
//            $("#productCategorySelector").dialog("close");
//        });

    $("#productCategorySelector #categoryName").bind("blur",function(e){
        var categoryName = $(this).val().replace(/[\ |\\]/g, "");
        $("#productCategorySelector #productCategorySelectorConfirmBtn").attr("locked","true");
        if(!G.isEmpty(categoryName)){
            APP_BCGOGO.Net.syncAjax({
                url: "searchInventoryIndex.do?method=getProductCategoryExactByName",
                type: "POST",
                data:{name:categoryName},
                dataType: "json",
                success:function (data) {
                    if(!G.isEmpty(data)){
                        if("FIRST_CATEGORY" == data["categoryType"]){
                            $("#productCategorySelector #_productCategorySelectedId").val("");
                            $("#productCategorySelector #_productCategorySelectedName").val("");
                            $("#productCategorySelector #_productCategorySelectedType").val("");
                            $("#productCategorySelector #_productCategorySelectedInfo").text("");
                            nsDialog.jConfirm("您输入的内容为标准一级分类，是否返回到标准分类重新选择？",null,function(flag){
                                if(flag){
                                    resetProductCategorySelector();
                                    $("#productCategorySelector #_productCategorySelectedInfo").text(categoryName);
                                    categorySelector.reset({"level_1_id":data["idStr"]},1);
                                    $("#productCategorySelector #productCategorySelectorConfirmBtn").removeAttr("locked");
                                }else{
                                    $("#productCategorySelector #productCategorySelectorConfirmBtn").removeAttr("locked");
                                }
                            });
                        }else if("SECOND_CATEGORY" == data["categoryType"]){
                            $("#productCategorySelector #_productCategorySelectedId").val(data["idStr"]);
                            $("#productCategorySelector #_productCategorySelectedName").val(data["name"]);
                            $("#productCategorySelector #_productCategorySelectedType").val(data["categoryType"]);
                            var selectDataInfo = data["firstCategoryName"]+" >> "+data["secondCategoryName"];
                            $("#productCategorySelector #_productCategorySelectedInfo").text(selectDataInfo);
                            $("#productCategorySelector #productCategorySelectorConfirmBtn").removeAttr("locked");
                        }else if("THIRD_CATEGORY" == data["categoryType"]){
                            $("#productCategorySelector #_productCategorySelectedId").val(data["idStr"]);
                            $("#productCategorySelector #_productCategorySelectedName").val(data["name"]);
                            $("#productCategorySelector #_productCategorySelectedType").val(data["categoryType"]);
                            var selectDataInfo = data["firstCategoryName"]+" >> "+data["secondCategoryName"]+" >> "+data["thirdCategoryName"];
                            $("#productCategorySelector #_productCategorySelectedInfo").text(selectDataInfo);
                            $("#productCategorySelector #productCategorySelectorConfirmBtn").removeAttr("locked");
                        }else{
                            $("#productCategorySelector #_productCategorySelectedId").val("");
                            $("#productCategorySelector #_productCategorySelectedName").val(categoryName);
                            $("#productCategorySelector #_productCategorySelectedType").val("");
                            var selectDataInfo = "其他 >> "+categoryName;
                            $("#productCategorySelector #_productCategorySelectedInfo").text(selectDataInfo);
                            $("#productCategorySelector #productCategorySelectorConfirmBtn").removeAttr("locked");
                        }
                    }else{
                        $("#productCategorySelector #_productCategorySelectedId").val("");
                        $("#productCategorySelector #_productCategorySelectedName").val(categoryName);
                        $("#productCategorySelector #_productCategorySelectedType").val("");
                        var selectDataInfo = "其他 >> "+categoryName;
                        $("#productCategorySelector #_productCategorySelectedInfo").text(selectDataInfo);
                        $("#productCategorySelector #productCategorySelectorConfirmBtn").removeAttr("locked");
                    }
                },
                error:function(){
                    nsDialog.jAlert("网络异常！");
                    $("#productCategorySelector #productCategorySelectorConfirmBtn").removeAttr("locked");
                }
            });
        }else{
            $("#productCategorySelector #_productCategorySelectedId").val("");
            $("#productCategorySelector #_productCategorySelectedName").val("");
            $("#productCategorySelector #_productCategorySelectedType").val("");
            $("#productCategorySelector #_productCategorySelectedInfo").text("");
            $("#productCategorySelector #productCategorySelectorConfirmBtn").removeAttr("locked");
        }
    });

    //关闭 返回分类
    $("#productCategorySelector .J_closeBackProductCategory").bind("click",function(e){
        e.preventDefault();
        resetProductCategorySelector();
        categorySelector.reset();
    });
    //最近使用选择
    $("#productCategorySelector #usedProductCategorySelect").bind("change",function(e){
        e.preventDefault();
        categorySelector.reset();
        if(G.isNotEmpty($(this).val())){
            var $selectOption =$(this).find("option:selected");
            $("#productCategorySelector #_productCategorySelectedId").val($(this).val());
            $("#productCategorySelector #_productCategorySelectedName").val($selectOption.attr("data-name"));
            $("#productCategorySelector #_productCategorySelectedType").val($selectOption.attr("data-type"));
            $("#productCategorySelector #_productCategorySelectedInfo").text($selectOption.text());
        }else{
            $("#productCategorySelector #_productCategorySelectedId").val("");
            $("#productCategorySelector #_productCategorySelectedName").val("");
            $("#productCategorySelector #_productCategorySelectedType").val("");
            $("#productCategorySelector #_productCategorySelectedInfo").text("");
        }
    });
    function resetProductCategorySelector(){
        $("#productCategorySelector #searchWord").val("");
        $("#productCategorySelector #_productCategorySelectedId").val("");
        $("#productCategorySelector #_productCategorySelectedName").val("");
        $("#productCategorySelector #_productCategorySelectedType").val("");
        $("#productCategorySelector #_productCategorySelectedInfo").text("");
        $("#productCategorySelector #hasNotSearchResultDiv").hide();
        $("#productCategorySelector #categoryName").val("");
        $("#productCategorySelector #hasSearchResultDiv").hide();
        $("#productCategorySelector #searchResultDiv").html("");
        $("#productCategorySelector #searchResultCount").text("0");
        $("#productCategorySelector #productCategorySelectorDiv").show();
        $("#productCategorySelector #usedProductCategorySelect").val("");
    }
});

function doQuotedPreBuyOrder(productLocalInfoId){
    var $checkedRow = $("input[name='quotedProductId']:checked").closest("tr");
    var salesStatus = $checkedRow.find("input[name='quotedProductId']").attr("data-sales-status");
    $("#salesStatus").val(salesStatus);
    var currentPreBuyOrderId = $("#currentPreBuyOrderId").val();
    var currentPreBuyOrderItemId = $("#currentPreBuyOrderItemId").val();
    var productInfo = G.Lang.normalize($checkedRow.find("input[name='productInfo']").val());
    var inventoryNum = G.Lang.normalize($checkedRow.find("input[name='inventoryNum']").val());
    var unit = G.Lang.normalize($checkedRow.find("input[name='unit']").val());
    var sellUnit=$checkedRow.find("input[name='sellUnit']").val();
    var storageUnit=$checkedRow.find("input[name='storageUnit']").val();
    var rate=$checkedRow.find("input[name='rate']").val();
    var productCategoryId=$checkedRow.find("input[name='productCategoryId']").val();
    var productCategoryName=$checkedRow.find("input[name='productCategoryName']").val();
    var productCategoryType=$checkedRow.find("input[name='productCategoryType']").val();
    var selectDataInfo=$checkedRow.find("input[name='selectDataInfo']").val();
    var inSalesUnit=sellUnit;
    var inventoryAveragePrice = G.Lang.normalize($checkedRow.find("input[name='inventoryAveragePrice']").val(),"0");
    var tradePrice = G.Lang.normalize($checkedRow.find("input[name='tradePrice']").val(),"0");

    $("#selectQuotedProductDiv").dialog( "close" );
    $("#quotedPreBuyOrderDiv").dialog({
        width: 730,
        zIndex: 20,
        modal: true,
        position:'top',
        beforeclose: function(event, ui) {
            $("#quotedPreBuyOrderForm")[0].reset();
            $("#currentPreBuyOrderItemId").val("");
            $("#salesStatus").val("");
            $("#quotedPreBuyOrderForm").find("input[id='productId']").val("");
            return true;
        },
        open: function() {
            $("#quotedPreBuyOrderForm").find("input[id='preBuyOrderId']").val(currentPreBuyOrderId);
            $("#quotedPreBuyOrderForm").find("input[id='preBuyOrderItemId']").val(currentPreBuyOrderItemId);
            $("#quotedPreBuyOrderForm").find("input[id='productId']").val(productLocalInfoId);
//                $("#quotedPreBuyOrderForm").find("input[id='unit']").val(unit);

            $("#quotedPreBuyOrderForm").find("span[id='productInfoSpan']").text(productInfo);
            $("#quotedPreBuyOrderForm").find("span[id='inventoryInfoSpan']").text(inventoryNum+unit);
            $("#quotedPreBuyOrderForm").find("span[id='inventoryAveragePriceSpan']").text(inventoryAveragePrice);
            $("#quotedPreBuyOrderForm").find("span[id='tradePriceSpan']").text(tradePrice);
            if("SECOND_CATEGORY" == productCategoryType){
                categorySelector.reset({"level_2_id":productCategoryId},2);
            }else if("THIRD_CATEGORY" == productCategoryType){
                categorySelector.reset({"level_3_id":productCategoryId},3);
            }
            $("#productCategorySelector #_productCategorySelectedId").val(productCategoryId);
            $("#productCategorySelector #_productCategorySelectedName").val(productCategoryName);
            $("#productCategorySelector #_productCategorySelectedType").val(productCategoryType);
            $("#productCategorySelector #_productCategorySelectedInfo").text(selectDataInfo);

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
            if(G.isEmpty(productLocalInfoId)){
                $(".product-info-exist").hide();
                $(".product-info-new").show();
            }else{
                $(".product-info-exist").show();
                $(".product-info-new").hide();
            }
            if(salesStatus=="InSales"){
                $(".product-info-inSales").hide();
                $(".category-selector").hide();
            }else{
                $(".product-info-inSales").show();
                $(".category-selector").show();
            }
        }
    });
}

function setSelectedProductCategoryData(selectDataInfo,categoryId,categoryName,categoryType){
    $(".J_productCategoryInfoSpan").text(selectDataInfo);
    $("#productCategoryId").val(categoryId);
    $("#productCategoryName").val(categoryName);
    $("#productCategoryType").val(categoryType);
}

</script>

<div class="alertMain newCustomers offerMain" id="selectQuotedProductDiv" title="我要报价" style="display:none">
    <input type="hidden" id="currentPreBuyOrderItemId">
    <input type="hidden" field="productCategoryId">
    <input type="hidden" field="productCategoryName">
    <input type="hidden" field="productCategoryType">
    <input type="hidden" field="selectDataInfo">
    <%--<input type="hidden" id="currentPreBuyOrderItemId">--%>
    <%--<input type="hidden" id="currentPreBuyOrderItemId">--%>
    <%--<input type="hidden" id="currentPreBuyOrderItemId">--%>
    <div class="line yellow_color">友情提示：请选择您与求购所对应的报价商品，若没有找到可新增商品报价哦！不要忘记商品必须先上架哦！</div>
    <div class="offer_cuSearch">
        <div class="cartTop"></div>
        <div class="cartBody">
            <form id="searchConditionForm" action="preBuyOrder.do?method=quotedSelectProduct" method="post">
                <input type="hidden" name="maxRows" id="maxRows" value="15">
                <div class="divTit">
                    <span class="spanName">商品查询</span>
                    <div class="conditionList">
                        <input class="txt J-productSuggestion J-initialCss J_clear_input" id="searchWord" name="searchWord" searchField="product_info" initialValue="品名/品牌/规格/型号/适用车辆" style="width:330px;"/>
                        <br>
                        <input class="txt J-productSuggestion J-initialCss J_clear_input" id="productName" name="productName" searchField="product_name" initialValue="品名" style="width:70px;"/>&nbsp;
                        <input class="txt J-productSuggestion J-initialCss J_clear_input" id="productBrand" name="productBrand" searchField="product_brand" initialValue="品牌/产地" style="width:70px;"/>&nbsp;
                        <input class="txt J-productSuggestion J-initialCss J_clear_input" id="productSpec" name="productSpec" searchField="product_spec" initialValue="规格" style="width:70px;"/>&nbsp;
                        <input class="txt J-productSuggestion J-initialCss J_clear_input" id="productModel" name="productModel" searchField="product_model" initialValue="型号" style="width:70px;"/>&nbsp;
                        <input class="txt J-productSuggestion J-initialCss J_clear_input" id="productVehicleBrand" name="productVehicleBrand" searchField="product_vehicle_brand" initialValue="车辆品牌" style="width:70px;"/>&nbsp;
                        <input class="txt J-productSuggestion J-initialCss J_clear_input" id="productVehicleModel" name="productVehicleModel" searchField="product_vehicle_model" initialValue="车型" style="width:70px;"/>&nbsp;
                        <input class="txt J-productSuggestion J-initialCss J_clear_input" id="commodityCode" name="commodityCode" searchField="commodity_code" initialValue="商品编号" style="text-transform: uppercase;width:70px;"/>&nbsp;
                    </div>

                </div>
                <div class="button_search">
                    <a class="blue_color clean" id="q_clear_condition_btn">清空条件</a>
                    <a class="button search-product-Btn" id="searchConditionBtn">查&nbsp;询</a>
                </div>
            </form>

            <div class="height"></div>
            <table class="tab_cuSearch" cellpadding="0" cellspacing="0" id="inSalesProductTable">
                <col width="30">
                <col>
                <col width="90">
                <col width="90">
                <col width="90">
                <col width="90">
                <col width="90">
                <tr class="titleBg J_title">
                    <td></td>
                    <td>上架商品信息</td>
                    <td>库存量</td>
                    <td>成本均价</td>
                    <td>批发价</td>
                    <td>是否上架</td>
                    <td>上架售价</td>
                </tr>
                <tr class="space J_title"><td colspan="5"></td></tr>
            </table>
            <div class="clear i_height"></div>
            <!----------------------------分页----------------------------------->
            <jsp:include page="/common/pageAJAX.jsp">
                <jsp:param name="url" value="preBuyOrder.do?method=quotedSelectProduct"></jsp:param>
                <jsp:param name="jsHandleJson" value="drawProductTable"></jsp:param>
                <jsp:param name="dynamical" value="InSalesProduct"></jsp:param>
                <jsp:param name="display" value="none"></jsp:param>
            </jsp:include>
        </div>
        <div class="cartBottom"></div>
    </div>
    <div class="height"></div>
    <div class="button button_step">
        <a class="btnSure" id="quotedFirstNextBtn" style="display: inline-block;">下一步</a>
        <a class="btnSure" onclick="javascript:$('#selectQuotedProductDiv').dialog('close');">关&nbsp;闭</a>
    </div>
</div>

<div class="alertMain newCustomers offerMain" id="quotedPreBuyOrderDiv" title="我要报价" style="display:none">
    <input type="hidden" id="salesStatus"/>
    <form action="preBuyOrder.do?method=saveQuotedPreBuyOrder" id="quotedPreBuyOrderForm" method="post">
        <input type="hidden" id="preBuyOrderId" name="preBuyOrderId">
        <input type="hidden" id="preBuyOrderItemId" name="preBuyOrderItemId">
        <input type="hidden" id="productId" name="productId">
        <input type="hidden" id="productCategoryName" name="productCategoryName"/>
        <input type="hidden" id="productCategoryId" name="productCategoryId"/>
        <%--<input type="hidden" id="productCategoryType"/>--%>
        <%--<input type="hidden" id="unit" name="unit">--%>
        <div class="offer_cuSearch">
            <%--<div class="cartTop"></div>--%>
            <div>
                <div class="product-info-exist" style="display: none">
                    <div class="line yellow_color">报价信息：</div>
                    <div class="divTit">
                        <span class="spanName">商品信息：</span>
                        <span class="spanContent" id="productInfoSpan"></span>
                        <span class="spanName">库存量：</span>
                        <span class="spanContent" id="inventoryInfoSpan"></span>
                    </div>
                    <div class="divTit">
                        <span class="spanName">成本均价：</span>
                        <span class="spanContent"><span class="arialFont">&yen;</span><span id="inventoryAveragePriceSpan"></span><a class="gray_color" style="color: #999999;">（不会给买家展示）</a></span>
                        <span class="spanName">批发价：</span>
                        <span class="spanContent"><span class="arialFont">&yen;</span><span id="tradePriceSpan"></span></span>
                    </div>
                </div>
                <div class="height"></div>
                <div class="category-selector" style="display: none">
                    <div class="order-container" id="productCategorySelector" style="border:#FFFFFF">
                        <div class="order-content">
                            <p></p>
                            <table width="100%" border="0" class="order-table" id="searchProductCategoryTable">
                                <tr>
                                    <th style="width:100px">商品分类搜索</th>
                                    <td style="width:270px"><input id="searchWord" style="font-size:12px;line-height:22px;height:22px" type="text"  class="txt"/></td>
                                    <td><div class="add_search"><a class="add_search" id="searchProductCategoryBtn" style="cursor: pointer">快速找到分类</a></div></td>
                                </tr>
                            </table>
                            <div class="classification-content"></div>
                            <table width="100%" border="0" class="order-table">
                                <tr>
                                    <th style="width:100px">您最近使用的分类</th>
                                    <td style="width:270px">
                                        <select id="usedProductCategorySelect" style="font-size:12px;line-height:22px;height:22px" class="txt">
                                            <option value="">----请选择----</option>
                                            <c:forEach var="usedProductCategoryDTO" items="${usedProductCategoryDTOList}">
                                                <option value="${usedProductCategoryDTO.idStr}" data-name="${usedProductCategoryDTO.name}" data-type="${usedProductCategoryDTO.categoryType}">${usedProductCategoryDTO.firstCategoryName} >> ${usedProductCategoryDTO.secondCategoryName}<c:if test="${not empty usedProductCategoryDTO.thirdCategoryName}"> >> ${usedProductCategoryDTO.thirdCategoryName}</c:if></option>
                                            </c:forEach>
                                        </select>
                                    </td>
                                    <td>
                                    </td>
                                </tr>
                            </table>
                            <div id="productCategorySelectorDiv" style="height: 300px"></div>
                            <div id="hasSearchResultDiv" style="width:540px;height:300px;height: 300px;display: block;border:1px solid #ccc; padding:6px;display: none">
                                <div style="width: 100%;height: 22px"><strong>匹配到<span class="yellow_color" id="searchResultCount">0</span>个商品分类</strong><span class="gay_color"></span><div class="gray550 J_closeBackProductCategory" style="padding:0 0 0 8px;line-height:24px;margin:0;width:100px;float: right;cursor: pointer">关闭,返回分类</div></div>
                                <div id="searchResultDiv" style="width: 100%;height: 270px;overflow-y:scroll;border:1px solid #ccc;"></div>
                            </div>
                            <div id="hasNotSearchResultDiv" style="width:540px;height:300px;height: 300px;display: block;border:1px solid #ccc; padding:6px;display: none">
                                对不起，未找到您所需的上架分类，请在此输入商品品名:<input id="categoryName" style="font-size:12px;width:110px;line-height:22px;height:22px" type="text" class="txt"/>
                                <div class="gray550 J_closeBackProductCategory" style="padding:0 0 0 8px;line-height:24px;margin:0;width:100px;float: right;cursor: pointer">关闭,返回分类</div>
                            </div>
                            <input type="hidden" id="_productCategorySelectedId"/>
                            <input type="hidden" id="_productCategorySelectedName"/>
                            <input type="hidden" id="_productCategorySelectedType"/>
                            <div  style="margin-top: 20px "><em class="label-import-tag">*</em><span style="margin-left: 5px">您选择的分类：</span><span id="_productCategorySelectedInfo"></span></div>
                            <%--<div class="padding10">--%>
                            <%--<a class="confirm-button" id="productCategorySelectorConfirmBtn">确定</a>--%>
                            <%--<div class="clear"></div>--%>
                            <%--</div>--%>
                            <div class="clear"></div>
                        </div>
                    </div>
                </div>
                <div class="height"></div>
                <div class="product-info-new quoted-product-detail" style="display: none;padding-left: 12px;">
                    <div class="divTit offer_divTit">
                        <span class="label-name">商品编号</span>
                        <span class="label-content"><input name="commodityCode" class="txt" type="text"/></span>
                        <em class="label-import-tag">*</em>
                        <span class="label-name">品&nbsp;&nbsp;&nbsp;&nbsp;名</span>
                        <span class="label-content"><input name="productName" class="txt" type="text"/></span>
                        <span class="label-name">品&nbsp;&nbsp;&nbsp;&nbsp;牌</span>
                        <span class="label-content"><input name="brand" class="txt" type="text"/></span>
                    </div>

                    <div class="divTit offer_divTit">
                        <span class="label-name">规&nbsp;&nbsp;&nbsp;&nbsp;格</span>
                        <span class="label-content"><input name="spec" class="txt" type="text"/></span>
                        <span style="margin-left: 12px;" class="label-name">型&nbsp;&nbsp;&nbsp;&nbsp;号</span>
                        <span class="label-content"><input name="model" class="txt" type="text"/></span>
                        <span class="label-name">车辆品牌</span>
                        <span class="label-content"><input type="text"  searchfield="brand" cachefield="productVehicleBrandSource" name="vehicleBrand" class="txt"/></span>
                    </div>
                    <div class="divTit offer_divTit">
                        <span class="label-name">车&nbsp;&nbsp;&nbsp;&nbsp;型</span>
                        <span class="label-content"><input  type="text" name="vehicleModel" searchfield="model" cachefield="productVehicleModelSource"  class="txt"/></span>
                    </div>
                </div>
                <div class="height"></div>

                <div class="product-info-inSales" style="display: none;">
                    <div class="divTit offer_divTit">
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
                    <div class="divTit offer_divTit">
                        <em class="label-import-tag">*</em>
                        <span class="label-name">单&nbsp;&nbsp;&nbsp;&nbsp;位</span>
                        <span id="_unitContainer" class="unit-Container">
                                <input type="txt" name="unit" maxlength="10" class="txt" style="width:50px;">
                        </span>
                    </div>
                </div>
                <div class="divTit offer_divTit">
                    <em class="label-import-tag">*</em>
                    <span class="spanName">我的报价：</span>
                    <span class="spanContent"><input type="text" style="color: #000000;" class="txt J_QuotedPrice price-input" id="price" name="price" />&nbsp;元&nbsp;
                        （说明：此价格 <label class="rad"><input type="checkbox" name="includingTax" value="TRUE" />含税</label>&nbsp;
                        <label class="rad"><input type="checkbox" name="shippingMethod" value="DELIVERY_HOME"/>送货上门</label>）
                    </span>
                </div>
                <div class="divTit offer_divTit">
                    <span class="spanName">到货时间：</span>
                    <span class="spanContent">
                        <label class="rad"><input type="radio" name="arrivalTimeRadio" value="1"/>一天内到货</label>&nbsp;&nbsp;
                        <label class="rad"><input type="radio" name="arrivalTimeRadio" value="2"/>两天内到货</label>&nbsp;&nbsp;
                        <label class="rad"><input type="radio" name="arrivalTimeRadio" value="3"/>三天内到货</label>&nbsp;&nbsp;
                        <label class="rad"><input type="radio" name="arrivalTimeRadio" value=""/></label><input type="text" class="txt J_ArrivalTime jRoundNumber" id="arrivalTime" name="arrivalTime" style="width:40px;color: #000000;" />天内到货</span>
                </div>
                <div class="height"></div>
            </div>
            <div class="cartBottom"></div>
        </div>
        <div class="height"></div>
        <div class="button button_step">
            <a class="btnSure" id="saveQuotedBtn">提交报价</a>
            <a class="btnSure" id="quotedBackSelectProductBtn">返回上一步</a>
        </div>
    </form>
</div>
<%--<%@ include file="../productCategorySelector.jsp" %>--%>

<%--<div class="alertMain newCustomers offerMain" id="quotedPreBuyOrderFinishDiv" title="我要报价——完成" style="display:none">--%>
<%--<div class="step">--%>
<%--<label class="title">报价流程：</label>--%>
<%--<div class="stepLeft"></div>--%>
<%--<div class="stepBody">--%>
<%--<span>1、选择我的商品</span>--%>
<%--<a class="stepImg"></a>--%>
<%--<span>2、填写我的价格并提交报价</span>--%>
<%--<a class="stepImg"></a>--%>
<%--<span class="hover">3、完成</span>--%>
<%--</div>--%>
<%--<div class="stepRight"></div>--%>
<%--</div>--%>
<%--<div class="height"></div>--%>
<%--<div class="offer_cuSearch">--%>
<%--<div class="cartTop"></div>--%>
<%--<div class="cartBody">--%>
<%--<div class="divTit">--%>
<%--<span class="spanName">商品名：</span>--%>
<%--<span class="spanContent" id="confirmProductInfoSpan"></span>--%>
<%--<span class="spanName">库存量：</span>--%>
<%--<span class="spanContent" id="confirmInventoryInfoSpan"></span>--%>
<%--</div>--%>
<%--<div class="divTit">--%>
<%--<span class="spanName">平均成本：</span>--%>
<%--<span class="spanContent"><span class="arialFont">&yen;</span><span id="confirmInventoryAveragePriceSpan"></span><a class="gray_color" style="color: #999999;">（不会给买家展示）</a></span>--%>
<%--<span class="spanName">批发价：</span>--%>
<%--<span class="spanContent"><span class="arialFont">&yen;</span><span id="confirmTradePriceSpan"></span></span>--%>
<%--</div>--%>
<%--<div class="divTit offer_divTit">--%>
<%--<span class="spanName">我的报价：</span>--%>
<%--<span class="spanContent"><span id="confirmQuotedPriceSpan"></span>&nbsp;元&nbsp;（说明：此价格 <label class="rad"><input type="checkbox" disabled="disabled" name="includingTaxCheckBox" value="TRUE"/>含税</label>&nbsp;<label class="rad"><input type="checkbox" disabled="disabled" value="DELIVERY_HOME" name="shippingMethodCheckBox"/>送货上门</label>）</span>--%>
<%--</div>--%>
<%--<div class="divTit offer_divTit">--%>
<%--<span class="spanName">到货时间：</span>--%>
<%--<span class="spanContent"><span id="confirmArrivalTimeSpan"></span>天内到货</span>--%>
<%--</div>--%>
<%--<div class="height"></div>--%>
<%--</div>--%>
<%--<div class="cartBottom"></div>--%>
<%--<div class="height"></div>--%>
<%--<c:if test="${allValidPreBuyOrderItemsCount>1}">--%>
<%--<div class="line">该买家共有&nbsp;<span class="red_color">${allValidPreBuyOrderItemsCount}</span>&nbsp;条求购信息&nbsp;<a class="blue_color" href="preBuyOrder.do?method=preBuyInformation&shopId=${customerDTO.customerShopId}">点击查看</a></div>--%>
<%--</c:if>--%>
<%--</div>--%>
<%--<div class="height"></div>--%>
<%--<div class="button button_step">--%>
<%--<a class="btnSure" id="confirmFinishBtn">确&nbsp;定</a>--%>
<%--</div>--%>
<%--</div>--%>