$(function(){
    $(".J_applyRelation").live("click", function(){
        if($("#fromSource").val()=="batchGoodsInSalesEditor"){
            nsDialog.jAlert("不能和自己店铺关联。");
            return;
        }
        var url = $(this).attr("url");
        APP_BCGOGO.Net.syncPost({
            url: url,
            dataType: "json",
            success:function(json){
                if(json.success){
                    nsDialog.jAlert("申请已发送！");
                    $("#relationStatusDiv").html('<a class="search_button" href="javascript:void(0);">已提交关联申请</a>');
                }else{
                    nsDialog.jAlert(json.msg);
                }
            }
        })
    });
    $(".J_supplier_accept").live("click", function (e) {
        e.preventDefault();
        e.stopPropagation();
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
                    nsDialog.jAlert("通过请求成功！" + originShopName + "已经成为您的关联客户！", "", function () {
                        $("#relationStatusDiv").empty().html('<a class="search_button" href="javascript:void(0);">已经关联</a>');
                    });
                } else {
                    nsDialog.jAlert(result.msg);
                }
            }
        });
    });
    $(".J_customer_accept").live("click", function (e) {
        e.preventDefault();
        e.stopPropagation();
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
                    nsDialog.jAlert("通过请求成功！" + originShopName + "已经成为您的关联供应商！", "", function () {
                        $("#relationStatusDiv").empty().html('<a class="search_button" href="javascript:void(0);">已经关联</a>');
                    });
                } else {
                    nsDialog.jAlert(result.msg);
                }
            }
        });
    });
});


/**
 * 下拉提示(产品)
 * @param $domObject
 */
function productSuggestion($domObject) {
    var searchWord = $domObject.val().replace(/[\ |\\]/g, ""); // 替换空格和\
    var dropList = APP_BCGOGO.Module.droplist;
    dropList.setUUID(GLOBAL.Util.generateUUID());
    var currentSearchField = $domObject.attr("searchField");
    var ajaxData = {
        searchWord: searchWord,
        searchField: currentSearchField,
        shopId: $("input[name='shopId']").val(),
        uuid: dropList.getUUID()
    };
    $domObject.parent().find(".J-productSuggestion").each(function () {
        var val = $(this).val().replace(/[\ |\\]/g, "");
        if ($(this).attr("name") != "searchWord") {
            ajaxData[$(this).attr("name")] = val == $(this).attr("initialValue") ? "" : val;
        }
    });

    var ajaxUrl = "product.do?method=searchWholeSalerProductInfo";
    if(G.isEmpty(ajaxData['searchWord'])){
        ajaxData['sort'] = 'last_in_sales_time desc';
    }
    APP_BCGOGO.wjl.LazySearcher.lazySearch(ajaxUrl, ajaxData, function (result) {
        if (currentSearchField == "product_info") {
            dropList.show({
                "selector": $domObject,
                "autoSet": false,
                "data": result,
                onGetInputtingData: function () {
                    var details = {};
                    $domObject.parent().find(".J-productSuggestion").each(function () {
                        var val = $(this).val().replace(/[\ |\\]/g, "");
                        details[$(this).attr("searchField")] = val == $(this).attr("initialValue") ? "" : val;
                    });
                    return {
                        details: details
                    };
                },
                onSelect: function (event, index, data) {
                    $domObject.parent().find(".J-productSuggestion").each(function () {
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
                    $("#searchShopOnlineProductBtn").click();
                },
                onKeyboardSelect: function (event, index, data) {
                    $domObject.parent().find(".J-productSuggestion").each(function () {
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
        } else {
            dropList.show({
                "selector": $domObject,
                "data": result,
                "onSelect": function (event, index, data) {
                    $domObject.val(data.label);
                    $domObject.css({"color": "#000000"});
                    $domObject.nextAll().find(".J-productSuggestion").each(function () {
                        clearSearchInputValueAndChangeCss($(this)[0]);
                    });
                    dropList.hide();
                    $("#searchShopOnlineProductBtn").click();
                }
            });
        }
    });
}

/**
 * 渲染客户评价列表
 * @param data
 */
function initSupplierCommentRecord(data) {

    $("#supplierCommentRecordTable tr:gt(1)").remove();

    if (data == null || data[0] == null || data[0].recordDTOList == null || data[0].recordDTOList == 0) {
        $("#noSupplierCommentRecord").css("display", "block");
        return;
    } else {
        $("#noSupplierCommentRecord").css("display", "none");
    }

    $.each(data[0].recordDTOList, function (index, order) {

        var tr = "<tr class='space'><td colspan='5'></td></tr> ";

        var commentTimeStr = (!order.commentTimeStr ? "---" : order.commentTimeStr);
        var commentContent = (!order.commentContent ? "" : order.commentContent);
        var firstCommentContent = (!order.firstCommentContent ? "" : order.firstCommentContent);
        var addCommentContent = (!order.addCommentContent ? "" : order.addCommentContent);
        var customer = (!order.customer ? "---" : order.customer);

        tr += "<tr class='offerBg'>";
        tr += "<td style='padding-left:10px;'>" + (index + 1) + "</td>";
        tr += "<td title='" + commentTimeStr + "'>" + commentTimeStr + "</td>";

        tr += '<td>' +
            '<div class="shopTit" ><label >货品质量</label><a class="bigStar" style="background-position:0px ' + '-' + order.qualityScoreSpan + 'px;"' + '></a>&nbsp;<span class="color_yellow">' + order.qualityScore + '</span>分</div>' +
            '<div class="shopTit" ><label >货品性价比</label><a class="bigStar" style="background-position:0px ' + '-' + order.performanceScoreSpan + 'px;"' + '></a>&nbsp;<span class="color_yellow">' + order.performanceScore + '</span>分</div>' +
            '<div class="shopTit" ><label >发货速度</label><a class="bigStar" style="background-position:0px ' + '-' + order.speedScoreSpan + 'px;"' + '></a>&nbsp;<span class="color_yellow">' + order.speedScore + '</span>分</div>' +
            '<div class="shopTit" ><label >服务态度</label><a class="bigStar" style="background-position:0px ' + '-' + order.attitudeScoreSpan + 'px;"' + '></a>&nbsp;<span class="color_yellow">' + order.attitudeScore + '</span>分</div>' +
            '</td>';

        if (addCommentContent != "") {
            tr += "<td style='word-break: break-all; word-wrap:break-word;' title='" + firstCommentContent + addCommentContent + "'>" + firstCommentContent + "<div class='addWord gray_color'> " + addCommentContent + "</div>" + "</td>";
        } else {
            tr += "<td style='word-break: break-all; word-wrap:break-word;' title='" + firstCommentContent + "'>" + firstCommentContent + "</td>";
        }
        tr += "<td title='" + customer + "'>" + customer + "</td>";
        tr += "</tr>";
        $("#supplierCommentRecordTable").append($(tr));
    });
}

/**
 * 渲染产品列表
 * @param jsonData
 */
function initProductList(json) {

    $("#productListTable").find(".J-ItemBody,.J-ItemBodySpace").remove();
    $("#checkAll").attr("checked", false);
    var $trBottom = '<tr class="titBottom_Bg J-ItemBodySpace"><td colspan="7"></td></tr>';
    if (json == null || json[0].data == null || json[0].data.products == null || json[0].data.products.length == 0) {
        $("#productListTable").append($("<tr class='titBody_Bg J-ItemBody'><td colspan='7' style='text-align: left;margin-left: 10px;'><span style='margin-left: 10px;'>暂无符合条件的商品!</span></td></tr>"));
        $("#productListTable").append($trBottom);
        $("#totalNumber").text("0");
        return;
    }
    $("#totalNumber").text(json[0].data.numFound);
    $.each(json[0].data.products, function (index, productDTO) {
        var commodityCode = productDTO.commodityCode == null ? "" : productDTO.commodityCode + "&nbsp;";
        var productLocalInfoIdStr = productDTO.productLocalInfoIdStr;
        var productName = productDTO.name == null ? "" : productDTO.name;
        var productBrand = G.isEmpty(productDTO.brand) ? "" : "&nbsp;" + productDTO.brand;
        var productSpec = productDTO.spec == null ? "" : productDTO.spec;
        var productModel = productDTO.model == null ? "" : productDTO.model;
        var productVehicleBrand = productDTO.productVehicleBrand == null ? "" : productDTO.productVehicleBrand;
        var productVehicleModel = productDTO.productVehicleModel == null ? "" : productDTO.productVehicleModel;
        var inSalesAmount = productDTO.inSalesAmount == null ? "" : dataTransition.rounding(productDTO.inSalesAmount, 1);
        var inSalesPrice = productDTO.inSalesPrice == null ? "" : dataTransition.rounding(productDTO.inSalesPrice, 2);
        var sellUnit = productDTO.sellUnit == null ? "" : productDTO.sellUnit;
        var inSalesUnit = productDTO.inSalesUnit == null ? "" : productDTO.inSalesUnit;
        var shopId = productDTO.shopIdStr == null ? "" : productDTO.shopIdStr;
        var productInfo = commodityCode + "<span style='font-weight: bold;'>" + productName + productBrand + "</span>" + " " + productSpec + " " + productModel + " " + productVehicleBrand + " " + productVehicleModel;
        var imageCenterDTO = productDTO.imageCenterDTO;
        var imageSmallURL = imageCenterDTO.productListSmallImageDetailDTO.imageURL;
        //promotions info
        filterUsingPromotion(productDTO,"search");
        var promotionsList = productDTO.promotionsDTOs;
        var isPromotion = !G.isEmpty(promotionsList);  //判断商品是否正在促销
        var tr = '<tr class="titBody_Bg J-ItemBody">';
        tr += '<td style="padding-left:10px;">';
        tr += '    <input type="checkbox" name="selectProduct" data-suppliershopid="' + shopId + '" data-productlocalinfoid="' + productLocalInfoIdStr + '" />';
        tr += '</td>';
        tr += '<td>';
        tr += '  <div style="padding:0;width:60px;float:left;">';
        tr += '      <a style="cursor: pointer" class="J_showProductInfo"><img style="width: 60px;height: 60px" src="'+imageSmallURL+'"/></a>';
        tr += '  </div>';
        tr += '  <div style="width:300px;height:55px;float:left;margin-left:10px;overflow: hidden;">';
        tr += '  <a class="blue_color line J_showProductInfo" >'+productInfo.trim()+'</a>';
        tr += '  </div>';
        tr += '</td>';

        tr += '<td class="promotions_info_td">';
        if (isPromotion) {
            var newPrice = _calculateBargainPrice(productDTO, inSalesPrice);
            if (newPrice < inSalesPrice) {
                tr += '<a style="color: #848484;text-decoration: line-through;">&yen;' + inSalesPrice + '</a>';
                tr += '<a style="margin-left:5px;" class="J-newPrice yellow_color">&yen;' + newPrice + '</a>';
            } else {
                tr += '<span class="arialFont">&yen;' + inSalesPrice + '</span>';
            }
            var pTitle = generatePromotionsAlertTitle(productDTO, "search");
            tr += pTitle;
        } else {
            tr += '<span class="arialFont">&yen;' + inSalesPrice + '</span>';
        }
        tr += '</td>';
        if(inSalesAmount==-1){
              tr += '<td>有货</td>';
        }else{
            tr += '<td>' + inSalesAmount + '&nbsp;' + inSalesUnit + '</td>';
        }
        tr += '<td><div class="line"><a class="blue_color J-purchaseSingleBtn">立即采购</a>&nbsp;<a class="blue_color J-addToShoppingCartSingleBtn">加入购物车</a></div></td>';
        tr += '</tr>';
        $("#productListTable").append($(tr));
        $("#productListTable").append($trBottom);
    });

}

/**
 * 添加到购物车
 * @param paramString
 */
function addProductToShoppingCart(paramString) {
    if (!G.Lang.isEmpty(paramString)) {
        APP_BCGOGO.Net.syncPost({
            url: "shoppingCart.do?method=addProductToShoppingCart",
            dataType: "json",
            data: {
                paramString: paramString
            },
            success: function (json) {
                updateShoppingCartNumber();
                $("#resultMsg").text(json.data.resultMsg);
                $("#warnMsg").text(json.data.warnMsg);
                $("#shoppingCartItemCount").text(json.data.shoppingCartItemCount);
                $("#shoppingCartTotal").text(json.data.shoppingCartTotal);
                $("#goShoppingCartBtn").text(json.data.goShoppingCartBtnName);
                $("#returnDialog").dialog({
                    width: 333,
                    minHeight: 140,
                    modal: true,
                    resizable: false,
                    position: 'center',
                    open: function () {
                        $(".ui-dialog-titlebar", $(this).parent()).hide();
                        $(this).removeClass("ui-dialog-content").removeClass("ui-widget-content");
                    }
                });
                if (userGuide.currentStepName == "PRODUCT_PRICE_GUIDE_PURCHASE") {
                    userGuide.funLib["PRODUCT_PRICE_GUIDE"]["_PRODUCT_PRICE_GUIDE_PURCHASE_STEP3_2"]();
                }
            },
            error: function () {
                nsDialog.jAlert("数据异常!");
            }
        });
    }
}

/**
 * 采购产品
 * @param productLocalInfoIds
 * @param amounts
 */
function purchaseProduct(productLocalInfoIds, amounts) {
    APP_BCGOGO.Net.syncPost({
        url: "autoAccessoryOnline.do?method=validatorPurchaseProduct",
        dataType: "json",
        data: {
            productLocalInfoIds: productLocalInfoIds,
            amounts: amounts
        },
        success: function (json) {
            if (json.success) {
                if (userGuide.currentStepName == "PRODUCT_PRICE_GUIDE_PURCHASE") {
                    window.location = "RFbuy.do?method=createPurchaseOrderOnlineByProduct&paramString="
                        + json.data.join(",") + "&currentStepName=PRODUCT_PRICE_GUIDE_PURCHASE";
                } else {
                    window.location = "RFbuy.do?method=createPurchaseOrderOnlineByProduct&paramString=" + json.data.join(",");
                }

            } else {
                if (json.operation == "CONFIRM_RELATED_SUPPLIER") {
                    nsDialog.jConfirm(json.msg, null, function (returnVal) {
                        if (returnVal) {
                            applySupplierRelation(json.data);
                        }
                    });
                } else {
                    nsDialog.jAlert(json.msg);
                }
            }
        },
        error: function () {
            nsDialog.jAlert("数据异常!");
        }
    });
}

function initAppUserCommentRecord(data) {
    $("#appUserCommentTable tr:gt(1)").remove();

    if(!data || !data.appUserCommentRecordDTOs || data.appUserCommentRecordDTOs.length == 0) {
        $("#averageComment").css("display","none");
        $("#noComment").css("display","block");
        $("#appUserCommentTableDiv").css("display","none");
        return;
    } else {
        $("#averageComment").css("display","block");
        $("#noComment").css("display","none");
        $("#appUserCommentTableDiv").css("display","");
    }
    //综合评分
    if(data.commentStatDTO) {
        var averageScore = data.commentStatDTO.averageScore;
      var totalScoreWidth = data.commentStatDTO.totalScoreWidth;
        $("#averageScoreAmount").text(averageScore == 0 ? "暂无分数" :  averageScore + "分");
        $("#averageCommentStar").addClass("normal-light-star-level-"+totalScoreWidth);
        $("#recordAmount").text(data.commentStatDTO.recordAmount);
    }
    //评分列表
    var html = '';
    for(var i = 0; i < data.appUserCommentRecordDTOs.length; i++) {
        var appUserCommentRecord = data.appUserCommentRecordDTOs[i];
        var commentTimeStr = appUserCommentRecord.commentTimeStr == null ? "" : appUserCommentRecord.commentTimeStr;
        var commentScore = appUserCommentRecord.commentScore = null ? "0" : appUserCommentRecord.commentScore;
        var commentContent = appUserCommentRecord.commentContent == null ? "" : appUserCommentRecord.commentContent;
        var commentator = appUserCommentRecord.commentator == null ? "" : appUserCommentRecord.commentator;
        var receiptNo = appUserCommentRecord.receiptNo == null ? "" : appUserCommentRecord.receiptNo;
        var orderType = appUserCommentRecord.orderType;
        var orderIdStr = appUserCommentRecord.orderIdStr == null ? "" : appUserCommentRecord.orderIdStr;
        html += '<tr class="space"><td colspan="5"></td></tr>';
        html += '<tr class="offerBg"><td style="padding-left:10px;">' + (i+1) + '</td>';
        html += '<td>' + commentTimeStr + '</td>';
        html += '<td><div class="shopTit"><a class="normal-light-star-level-' + G.rounding(commentScore,0)*2 + '"></a>&nbsp;<span class="yellow_color">' + commentScore + '</span>分</div></td>';
        html += '<td>' + commentContent + '</td>';
        html += '<td>' + commentator + '</td>';
        html += '</tr>';
        $("#appUserCommentTable").append(html);
    }
}



/**
 * 初始化产品分类的树
 */
function initProductCategoryTree() {

    var setting = {
        view: {
            showIcon: false,
            showLine: false

        },
        data: {
            simpleData: {
                enable: true
            }
        },
        callback: {
            onClick: onTreeClick
        }
    };

    var result = APP_BCGOGO.Net.syncPost(
        {
            url: "shopMsgDetail.do?method=getProductCategoryListSimpleJsonFormat",
            dataType: "json",
            data: {shopId: $("#paramShopId").val()}
        }
    );



    var zNodes;

    if (!G.isEmpty(result) && !G.isEmpty(result.data)) {
        zNodes = result.data;
    }

    $(document).ready(function () {
        $.fn.zTree.init($("#productCategoryTree"), setting, zNodes);
    });
}

function onTreeClick(event, treeId, treeNode, clickFlag) {
    if(G.isEmpty($("#pageType").val())){
        return;
    }
    var seelectedNodeIds = getSelectedNodeIds(treeNode);
    if($("#pageType").val() == 'shopProductDetail'){
        window.location='shopMsgDetail.do?method=renderShopMsgDetail&paramShopId=' + $("#paramShopId").val() + '&shopMsgTabFlag=productList&selectedCategory=' + seelectedNodeIds.toString();
    }else if($("#pageType").val() == 'wholesalerShopMsgDetail'){
        $("#productCategoryIds").val(seelectedNodeIds.toString());
        $("#productTab").trigger("click");
        $("#searchShopOnlineProductBtn").click();
    }
}

function getSelectedNodeIds(treeNode){
    var nodes = [];
    if(treeNode.isParent){
        nodes = treeNode.children;
        var categoryIds = new Array(nodes.length);
        for(var i in nodes){
            categoryIds[i] = nodes[i].categoryIdStr;
        }
        categoryIds.push(treeNode.categoryIdStr);
        return categoryIds;
    }else{
        return [treeNode.categoryIdStr];
    }
}
