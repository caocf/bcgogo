<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
<title>店铺资料</title>
<link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/shopMsgDetail<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/zTreeStyle<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/addCheck<%=ConfigController.getBuildVersion()%>.css"/>
<style type="text/css">
    .ztree a.level0 span{
        font-size:15px;
    }
    .ztree li.level0{
        margin-bottom:5px;
    }
    .ztree ul.level0{
        margin: 5px 0;
    }
</style>

<%@include file="/WEB-INF/views/header_script.jsp" %>
<script type="text/javascript" src="js/shopMsgDetail<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript"
        src="js/page/autoaccessoryonline/promotionsUtil<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/extension/jquery/plugin/jquery.ztree.core-3.5.min.js"></script>
<script type="text/javascript">
defaultStorage.setItem(storageKey.MenuUid, "APPLY_GET_APPLY_SUPPLIERS");
defaultStorage.setItem(storageKey.MenuCurrentItem,"店铺资料");

var selfShopId = '${sessionScope.shopId}';
$(document).ready(function () {
    // Tab切换事件绑定
    tabSwitchBind();
    vtabSwitchBind();
    // 商品搜索Field相关注册绑定
    productSearchFieldBind();
    //productSimpleSearchSuggestionBind();
    quickSearchBind();
    //促销选择绑定注册
    promotionsRelatedBind();
    // 价格查询条件相关的绑定
    priceRelatedBind();
    // 点击搜索 form提交之前的一些预处理
    doSearchBefore();
    // Form提交
    searchFormSubmit();
    // 搜索条件重置
    resetSearchCondBind();
    // 采购和购物车相关的绑定逻辑
    purchaseAndShoppingCarRelatedBind();
    // 初始化查询条件（和reset方法一起使用）
    initSearchCond();
    // 初始化产品分类树
    initProductCategoryTree();

    // tab显示
    var tabFlag = $("#shopMsgDetailFlag").val();
    if (!G.isEmpty(tabFlag)) {
        if (tabFlag === "comment") {
            $("#comment").trigger("click");
        } else if (tabFlag === "productList") {
            $("#productTab").trigger("click");
        }
    }

    if(!G.isEmpty(GLOBAL.Util.getUrlParameter("selectedCategory"))){
        $("#productCategoryIds").val(GLOBAL.Util.getUrlParameter("selectedCategory"));
    }

    if (!G.isEmpty($("#simpleSearchFlag").val())) {
        $("#productQuickSearchBtn").trigger("click");
    } else {
        $("#searchShopOnlineProductBtn").trigger("click"); //进入页面进行一次查询
    }

    $(".J_showProductInfo").live("click", function(){
        var trInput = $(this).closest("tr").find("input[name='selectProduct']");
        var localInfoId = trInput.attr("data-productlocalinfoid");
        var shopId = trInput.attr("data-suppliershopid");
        window.open("shopProductDetail.do?method=toShopProductDetail&paramShopId="+shopId+"&productLocalId="+localInfoId, "_blank");
    });

    $("#simpleTradePriceStart, #simpleTradePriceEnd").bind("keyup", function(){
        $(this).val(APP_BCGOGO.StringFilter.inputtedPriceFilter($(this).val()));
    })
    //从批量上架成功页面跳转过来
    if($("#fromSource").val()=="batchGoodsInSalesEditor"){
        $("#productTab").click();
    }

});


/**
 * tab切换
 */
function tabSwitchBind() {
    var $tabContentList = $(".store_right .tab-content"),
            $storeMenuList = $(".i_main .storeManu").find("a");
    var _updateTabContent = function (index) {
        $tabContentList.hide();
        $tabContentList.eq(index).show();
        $storeMenuList.removeClass("click");
        $storeMenuList.eq(index).addClass("click");
    };
    _updateTabContent(0);
    $storeMenuList.each(function (index, value) {
        $(value).bind("click", function (event) {
            _updateTabContent(index);
        });
    });
}

function vtabSwitchBind() {
    $(".vtab-item").bind("click mouseover", function (e) {
        e.preventDefault();
        e.stopPropagation();
        $(".vtab-item").removeClass("actived");
        $(this).addClass("actived");
        var id = $(this).attr("id").toString();
        var suffix = (id.split("-"))[1];
        $(".rate.fl").hide();
        $("[id$=" + suffix + "]").show();
    });
}

/**
 *  产品搜索相关field的绑定
 */
function productSearchFieldBind() {
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
}

/**
 * 促销相关的注册绑定
 */
function promotionsRelatedBind() {

    $("#allChk").click(function () {
        $(".promotionsTypes,#allChk").attr("checked", $(this).attr("checked"));
        if ($(this).attr("checked")) {
            var promotionsType = new Array();
            $(".promotionsTypes").each(function () {
                promotionsType.push($(this).attr("promotionsTypes"));
            });
            $("#promotionsType").val(promotionsType.toString());
        } else {
            $("#promotionsType").val("");
        }

    });

    $(".promotionsTypes").click(function () {
        var promotionsType = new Array();
        var promotionsTypesCount = 0;
        $(".promotionsTypes").each(function () {
            if ($(this).attr("checked")) {
                promotionsTypesCount++;
                promotionsType.push($(this).attr("promotionsTypes"))
            }
        });
        if (promotionsTypesCount == $(".promotionsTypes").size()) {
            $(".promotionsTypes,#allChk").attr("checked", true);
        } else {
            $("#allChk").attr("checked", false);
        }
        $("#promotionsType").val(promotionsType.toString());
    });

}

/**
 * 点击搜索以后 表单提交以前的预处理
 */
function doSearchBefore() {
    $("#searchShopOnlineProductBtn").click(function () {
        $('[name="productIds"]').val("");
        $('[name="fromSource"]').val("shopMsgDetail");
        $("#tradePriceStart").val($("#tradePriceStartInput").val());
        $("#tradePriceEnd").val($("#tradePriceEndInput").val());
        $(".J_span_sort_initProductList").each(function () {
            $(this).removeClass("hover");
            $(this).find(".J_sort_span_image").removeClass("arrowUp").addClass("arrowDown");
            $(this).attr("currentSortStatus", "Desc");
            $(this).find(".alertBody").html($(this).attr("ascContact"));
        });
        $("#searchWholeSalerStockForm").submit();
    });
}

/**
 * 搜索的表单提交
 */
function searchFormSubmit() {
    $("#searchWholeSalerStockForm").submit(function (e) {
        e.preventDefault();
        $(".J-initialCss").placeHolder("clear");
        $(".j_sort").each(function () {
            $(this).addClass("ascending").removeClass("descending");
        });

        var param = $("#searchWholeSalerStockForm").serializeArray();
        var data = {};
        $.each(param, function (index, val) {
            data[val.name] = val.value;
        });
        APP_BCGOGO.Net.syncPost({
            url: "autoAccessoryOnline.do?method=getCommodityQuotationsList",
            dataType: "json",
            data: data,
            success: function (result) {
                initProductList(result);
                initPages(result, "initProductList", "autoAccessoryOnline.do?method=getCommodityQuotationsList", '', "initProductList", '', '', data, '');
                $(".J-initialCss").placeHolder("reset");
            },
            error: function () {
                $(".J-initialCss").placeHolder("reset");
                $("#productListTable").find(".J-ItemBody,.J-ItemBodySpace").remove();
                $("#productListTable").append($("<tr class='titBody_Bg J-ItemBody'><td colspan='7' style='text-align: left;margin-left: 10px;'><span style='margin-left: 10px;'>数据异常，请刷新页面！</span></td></tr>"));
                $("#productListTable").append($("<tr class=\"titBottom_Bg J-ItemBodySpace\"><td colspan=\"7\"></td></tr>"));
                $("#totalNumber").text("0");
                nsDialog.jAlert("数据异常，请刷新页面！");
            }
        });

        return false;
    });
}

/**
 * 重置搜索条件绑定
 */
function resetSearchCondBind() {
    $("#clearConditionBtn").bind('click', function () {
        $(".J_clear_input").val("");
        $(".J-initialCss").placeHolder("reset");
        $(".promotion_type_selector input").attr("checked", false);
        $("#promotionsType").val("");

    });
}

function initSearchCond() {
    $(".J-initialCss").placeHolder("init");
}

/**
 * 价格搜索条件相关的绑定
 */
function priceRelatedBind() {

    $("#tradePriceStartInput,#tradePriceEndInput").bind("click", function (e) {
        e.preventDefault();
        $("#tradePriceDiv").show();
    });

    $("#tradePriceClearBtn").bind("click", function (e) {
        e.preventDefault();
        $("#tradePriceStartInput,#tradePriceEndInput").val("");
        $("#searchShopOnlineProductBtn").click();
    });

    $("#tradePriceSureBtn").bind("click", function (e) {
        e.preventDefault();
        var start = $("#tradePriceStartInput").val();
        var end = $("#tradePriceEndInput").val();
        if (!G.Lang.isEmpty(start) && !G.Lang.isEmpty(end) && Number(start) > Number(end)) {
            $("#tradePriceStartInput").val(end);
            $("#tradePriceEndInput").val(start);
        }
        $("#searchShopOnlineProductBtn").click();
    });

    $("#tradePriceListNum>span").click(function (e) {
        var idStr = $(this).attr("id");
        if (idStr == "tradePrice_1") {
            $("#tradePriceStartInput").val("");
            $("#tradePriceEndInput").val(1000);
        } else if (idStr == "tradePrice_2") {
            $("#tradePriceStartInput").val(1000);
            $("#tradePriceEndInput").val(5000);
        } else if (idStr == "tradePrice_3") {
            $("#tradePriceStartInput").val(5000);
            $("#tradePriceEndInput").val(10000);
        } else if (idStr == "tradePrice_4") {
            $("#tradePriceStartInput").val(10000);
            $("#tradePriceEndInput").val("");
        }
        $("#searchShopOnlineProductBtn").click();
    });

    $("#tradePriceStartInput,#tradePriceEndInput").bind("keyup blur", function (event) {
        if (event.type == "focusout") {
            event.target.value = APP_BCGOGO.StringFilter.inputtingPriceFilter(event.target.value, 2);
        } else if (event.type == "keyup") {
            if (event.target.value != APP_BCGOGO.StringFilter.inputtingPriceFilter(event.target.value, 2)) {
                event.target.value = APP_BCGOGO.StringFilter.inputtingPriceFilter(event.target.value, 2);
            }
        }
    });

    $(document).click(function (event) {
        var target = event.target;
        if (!target || !target.id || (target.id != "tradePriceStartInput" && target.id != "tradePriceEndInput")) {
            $("#tradePriceDiv").hide();
        }
    })
}

/**
 * 采购和购物车相关的绑定逻辑
 */
function purchaseAndShoppingCarRelatedBind() {

    // 去购物车结算
    $("#goShoppingCartBtn").bind("click", function (e) {
        e.preventDefault();
        window.location.href = "shoppingCart.do?method=shoppingCartManage";
    });
    // 继续采购（隐藏购物车）
    $("#closeBtn").bind("click", function (e) {
        e.preventDefault();
        $("#returnDialog").dialog("close");
    });

    // 单笔加入购物车
    $(".J-addToShoppingCartSingleBtn").live("click", function (e) {
        var $domObject = $(this).closest("tr").find("input[name='selectProduct']");
        if($("#fromSource").val()=="batchGoodsInSalesEditor" || $domObject.attr("data-suppliershopid") == selfShopId){
            nsDialog.jAlert("不能将自己店铺的商品加入购物车。");
            return;
        }
        e.preventDefault();

        var productId = $domObject.attr("data-productlocalinfoid");
        var amount = G.normalize($(this).closest("tr").find(".J-Amount").val(), "1");
        var supplierShopId = $domObject.attr("data-suppliershopid");
        var paramString = supplierShopId + "_" + productId + "_" + amount;
        addProductToShoppingCart(paramString);
    });

    // 批量加入购物车
    $("#addToShoppingCartBtn").bind("click", function (e) {
        if($("#fromSource").val()=="batchGoodsInSalesEditor" || $("input[name='shopId']").val() == selfShopId){
            nsDialog.jAlert("不能将自己店铺的商品加入购物车。");
            return;
        }
        e.preventDefault();
        //遍历页面勾选的checkbox，取出所有选中的productId
        var paramString = [];
        $("input[name='selectProduct']:checked").each(function () {
            var supplierShopId = $(this).attr("data-suppliershopid");
            var productId = $(this).attr("data-productlocalinfoid");
            var amount = G.normalize($(this).closest("tr").next(".J-ItemBody").find(".J-Amount").val(), "1");
            paramString.push(supplierShopId + "_" + productId + "_" + amount);
        });
        if (paramString.length == 0) {
            nsDialog.jAlert("请选择需要加入购物车的商品!");
        } else {
            addProductToShoppingCart(paramString.join(","));
        }
    });

    // 立即采购（单笔采购）
    $(".J-purchaseSingleBtn").live("click", function (e) {
        var checkbox = $(this).closest("tr").find("input[name='selectProduct']");
        if($("#fromSource").val()=="batchGoodsInSalesEditor" || selfShopId == checkbox.attr("data-suppliershopid")){
            nsDialog.jAlert("不能采购自己店铺的商品。");
            return;
        }
        e.preventDefault();
        var productId = checkbox.attr("data-productlocalinfoid");
        var amount = 1;
        if (!G.Lang.isEmpty(productId)) {
            purchaseProduct(productId, amount);
        }
    });
    // 批量下采购单
    $("#purchaseBtn").click(function () {

        if($("#fromSource").val()=="batchGoodsInSalesEditor" || $("input[name='shopId']").val() == selfShopId){
            nsDialog.jAlert("不能采购自己店铺的商品。");
            return;
        }
        //遍历页面勾选的checkbox，取出所有选中的productId
        var productIds = [], amounts = [];
        $("input[name='selectProduct']:checked").each(function () {
            productIds.push($(this).attr("data-productlocalinfoid"));
            amounts.push(1);
        });

        if (productIds.length == 0) {
            nsDialog.jAlert("请选择需要采购的商品!");
        } else {
            purchaseProduct(productIds.join(","), amounts.join(","));
        }
    });

    // 全选按钮
    $("#checkAll").bind("click", function () {
        $("#floatBarSelectAll").attr("checked", this.checked);
        $("input[name='selectProduct']").attr("checked", this.checked);
    });
    $("input[name='selectProduct']").live("click", function () {
        var checked = $("input[name='selectProduct']").length == $("input[name='selectProduct']:checked").length ? true : false;
        $("#checkAll").attr("checked", checked);
    });

}

function quickSearchBind() {
    $("#productQuickSearchBtn").click(function () {
        $("#productSimpleSearchForm").submit();
    });
    $("#productSimpleSearchForm").submit(function (e) {
        e.preventDefault();
        e.stopPropagation();
        var param = $("#productSimpleSearchForm").serializeArray();
        var data = {};
        $.each(param, function (index, val) {
            data[val.name] = val.value;
        });
        APP_BCGOGO.Net.syncPost({
            url: "autoAccessoryOnline.do?method=getCommodityQuotationsList",
            dataType: "json",
            data: data,
            success: function (result) {
                $("#productTab").click();
                $("#clearConditionBtn").click();
                initSearchFields();
                initProductList(result);
                initPages(result, "initProductList", "autoAccessoryOnline.do?method=getCommodityQuotationsList", '', "initProductList", '', '', data, '');
                $(".J-initialCss").placeHolder("reset");
            },
            error: function () {
                $(".J-initialCss").placeHolder("reset");
                $("#productListTable").find(".J-ItemBody,.J-ItemBodySpace").remove();
                $("#productListTable").append($("<tr class='titBody_Bg J-ItemBody'><td colspan='7' style='text-align: left;margin-left: 10px;'><span style='margin-left: 10px;'>数据异常，请刷新页面！</span></td></tr>"));
                $("#productListTable").append($("<tr class=\"titBottom_Bg J-ItemBodySpace\"><td colspan=\"7\"></td></tr>"));
                $("#totalNumber").text("0");
                nsDialog.jAlert("数据异常，请刷新页面！");
            }
        });
        return false;
    });
}

function initSearchFields() {
    $("#productInfo").val($("#productSimpleSearchField").val());
    $("#tradePriceStartInput").val($("#simpleTradePriceStart").val());
    $("#tradePriceEndInput").val($("#simpleTradePriceEnd").val());
}

</script>

</head>

<body class="bodyMain">
<%@include file="/WEB-INF/views/header_html.jsp" %>
<input type="hidden" id="paramShopId" name="paramShopId" value="${paramShopId}"/>
<input type="hidden" id="shopMsgDetailFlag" value="${shopMsgDetailFlag}"/>
<input type="hidden" id="fromSource" value="${fromSource}"/>
<input type="hidden" id="pageType" value="wholesalerShopMsgDetail"/>
<div class="i_main clear">
<div class="clear i_height"></div>
<div class="titBody">
<%--<title --%>
<div class="store_top">
    <div class="storeName">
        <span>${shopDTO.name}</span>
        <span class="storeAddress">${shopDTO.areaName}</span>
    </div>
    <div class="storeManu">
        <a id="default">店铺介绍</a>
        <a id="comment">店铺评价</a>
        <a id="productTab">商品列表</a>
    </div>
</div>
<%--left content--%>
<div class="store_left">
    <div class="store_kind">
        <span class="store_title">店铺信息</span>

        <div class="shop store_list">
            <div class="title"><span class="shop_name">${shopDTO.name}</span></div>
            <div class="height"></div>
            <div class="divStar">
                综合评分：<span class="picStar" onclick='$("#comment").trigger("click");'
                           style=" background-position: 0px -${supplierCommentStatDTO.totalScoreSpan}px;"></span>
                <span class="yellow_color">
                    <c:choose>
                        <c:when test="${supplierCommentStatDTO.totalScore == 0}">
                            暂无分数
                        </c:when>
                        <c:otherwise>
                            ${supplierCommentStatDTO.totalScore} 分
                        </c:otherwise>
                    </c:choose>
                </span>
            </div>
            <c:if test="${isQQExist eq true}">
                <div class="store_connecter"><b>在线咨询</b></div>
                <c:choose>
                    <c:when test="${empty shopDTO.contacts}">
                        暂无联系方式
                    </c:when>
                    <c:otherwise>
                        <c:forEach items="${shopDTO.contacts}" var="contact">
                            <c:if test="${not empty contact && not empty contact.qq}">
                                <div class="store_connecter"><span class="name">${contact.name}</span>
                                    <a href="http://wpa.qq.com/msgrd?v=3&uin=${contact.qq}&site=qq&menu=yes" target="_blank">
                                        <img src="http://wpa.qq.com/pa?p=2:${contact.qq}:41"/>
                                    </a>
                                </div>
                            </c:if>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </c:if>
            <div class="store_connecter">入驻日期：<span>${shopDTO.registrationDateStr}</span></div>
            <div class="store_connecter">商品数量：<a class="blue_color" href="shopMsgDetail.do?method=renderShopMsgDetail&paramShopId=${paramShopId}&shopMsgTabFlag=productList"><b>${shopDTO.totalProductCount}</b></a>种</div>
            <div class="store_connecter">
                <div>认证情况：</div>
                <c:choose>
                    <c:when test="${shopDTO.licensed}">
                        <img src="images/license.png"/>
                    </c:when>
                    <c:otherwise>
                        <img src="images/unlicense.png"/>
                    </c:otherwise>
                </c:choose>
            </div>

            <div class="store_connecter">收藏人气：已有<b>${shopDTO.beStored}</b>家</div>

            <c:if test="${shopDTO.id != sessionScope.shopId}">
                <div id="relationStatusDiv" style="float:left;width:100%;">
                    <c:choose>
                        <c:when test="${relationStatus=='UN_APPLY_RELATED'}">
                            <c:choose>
                                <c:when test="${relateFlag eq 'relatedAsCustomer'}">
                                    <a class="search_button J_applyRelation" url="apply.do?method=applySupplierRelation&supplerShopId=${paramShopId}">申请关联</a>
                                </c:when>
                                <c:otherwise>
                                    <a class="search_button J_applyRelation" url="apply.do?method=applyCustomerRelation&customerShopId=${paramShopId}">申请关联</a>
                                </c:otherwise>
                            </c:choose>
                        </c:when>
                        <c:when test="${relationStatus=='APPLY_RELATED'}">
                            <a class="search_button" href="javascript:void(0);">已提交关联申请</a>
                        </c:when>
                        <c:when test="${relationStatus=='RELATED'}">
                            <a class="search_button" href="javascript:void(0);">已经关联</a>
                        </c:when>
                        <c:when test="${relationStatus=='BE_APPLY_RELATED'}">
                            <div class="txt-box-grey-center">对方已经向本店提交关联申请</div>
                            <c:if test="${inviteDTO.inviteType=='CUSTOMER_INVITE'}">
                                <a class="search_button J_supplier_accept" applyinvitestatus="pendingCustomer" originshopname="${shopDTO.name}" inviteid="${inviteDTO.id}" href="#">同意关联</a>
                            </c:if>
                            <c:if test="${inviteDTO.inviteType=='SUPPLIER_INVITE'}">
                                <a class="search_button J_customer_accept" applyinvitestatus="pendingSupplier" originshopname="${shopDTO.name}" inviteid="${inviteDTO.id}" href="#">同意关联</a>
                            </c:if>
                        </c:when>
                    </c:choose>
                </div>
            </c:if>
        </div>
    </div>
    <c:if test="${isShowProductRelateMsg}">
        <div class="store_kind">
            <span class="store_title">产品搜索</span>
            <form method="post" id="productSimpleSearchForm" action="#">
                <input type="hidden" id="simpleSearchFlag" value="${simpleSearchFlag}"/>
                <input type="hidden" name="fromSource" value="${fromSource}"/>
                <input type="hidden" name="shopId" value="${paramShopId}"/>
                <div class="shop store_list store_search">
                    <div class="store_connecter">
                        <span class="name">关键词</span>
                        <input type="text" id="productSimpleSearchField" name="searchWord" searchField="product_info"  class="txt" value="${searchWord}" placeHolder="品名/品牌/规格/型号/车型" style="width:150px;"/>
                    </div>
                    <div class="store_connecter">
                        <span class="name">价格</span>
                        <input type="text" class="txt" name="inSalesPriceStart" value="${tradePriceStart}" id="simpleTradePriceStart" style="width:60px;"/>&nbsp;至&nbsp;<input type="text" class="txt" name="inSalesPriceEnd" value="${tradePriceEnd}" id="simpleTradePriceEnd"  style="width:60px;"/>
                    </div>
                    <a class="search_button" id="productQuickSearchBtn">搜&nbsp;索</a>
                </div>
            </form>
        </div>
        <div class="store_kind">
            <span class="store_title">上架商品分类</span>

            <div class="shop store_list store_kindList">
                <ul id="productCategoryTree" class="ztree"></ul>
            </div>
        </div>
    </c:if>
</div>

<div class="store_right">
<%--店铺介绍--%>
<div class="tab-content">
    <div class="store_kind">
        <span class="store_title">店铺介绍</span>

        <div class="store_content">
            <b>本店主要经营产品</b>
                <span class="content">
                    ${empty shopDTO.businessScopeStr?'暂无信息':shopDTO.businessScopeStr}
                </span>

            <div class="clear i_height"></div>
            <b>主营车型</b>
             <span class="content">
                 ${empty shopDTO.shopVehicleBrandModelStr?'暂无信息':shopDTO.shopVehicleBrandModelStr}
             </span>
            <div class="clear i_height"></div>
            <b>联系方式</b>

            <div class="clear"></div>
            <c:forEach items="${shopDTO.contacts}" var="contact" varStatus="status">
                <div class="contact_list ${status.last?'last_noBorder':''}">
                    <span class="name">${contact.name}</span>

                    <div class="mode" style="overflow: hidden;text-overflow: ellipsis;">
                        <a class="icon_phone">${empty contact.mobile?"暂无信息":contact.mobile}</a>
                        <a class="icon_QQ">${empty contact.qq?"暂无信息":contact.qq}</a>
                        <a class="icon_email" style="overflow: hidden;text-overflow: ellipsis;width:120px;" title="${empty contact.email?"暂无信息":contact.email}">${empty contact.email?"暂无信息":contact.email}</a>
                    </div>
                </div>
            </c:forEach>
            <div class="clear"></div>
            <c:if test="${!empty shopDTO.imageCenterDTO && !empty shopDTO.imageCenterDTO.shopImageDetailDTOs}">
                <b>店面风采</b>

                <div class="clear i_height"></div>
                <!-- 店面风采的图片 -->
                <c:forEach var="shopImageDetailDTO" items="${shopDTO.imageCenterDTO.shopImageDetailDTOs}">
                    <span class="store_photo"><img src="${shopImageDetailDTO.imageURL}"/></span>
                </c:forEach>
            </c:if>

        </div>
    </div>
</div>

<%--动态评分--%>
<div class="tab-content" style="display: none;">

<div class="content-main" style="width:764px;">
<dl class="shop-rate">
<dt class="content-title">
<div class="bg-top-hr store_title">店铺动态评分</div>
</dt>
<div class="cl"></div>

<dd class="content-details">
<div class="info-basic fl">
    <div class="shop-name">${shopDTO.name}</div>
    <div class="rate">
        <div class="rate-star fl normal-light-star-level-${supplierCommentStatDTO.totalScore * 2}"></div>
        <div class="rate-score fl">
            <c:choose>
                <c:when test="${supplierCommentStatDTO.totalScore == 0}">
                    暂无分数
                </c:when>
                <c:otherwise>
                    ${supplierCommentStatDTO.totalScore} 分
                </c:otherwise>
            </c:choose>
        </div>
        <div class="cl"></div>
    </div>
    <div class="rate-note">共${supplierCommentStatDTO.recordAmount}名客户参与评价</div>
</div>
<div class="bg-vr-grey fl"></div>
<div class="info-details fl">
<div class="rate fl" id="rate-fl-quality">
    <div class="rate-average">
        <div class="rate-star fl normal-light-star-level-<fmt:formatNumber value='${supplierCommentStatDTO.qualityTotalScore * 2}' pattern='#0'/> "></div>
        <div class="rate-score fl">
            <c:choose>
                <c:when test="${supplierCommentStatDTO.qualityTotalScore == 0}">
                    暂无分数
                </c:when>
                <c:otherwise>
                    ${supplierCommentStatDTO.qualityTotalScore} 分
                </c:otherwise>
            </c:choose>
        </div>
        <div class="cl"></div>
    </div>
    <div class="rate-details">
        <div class="rate-item">
            <div class="rate-star fl lite-light-star-level-10"></div>
            <div class="rate-score fl"><span class="number-red">4.0 - 5.0&nbsp;</span>分</div>
            <div class="rate-percent fl">
                <div class="percent-bar fl">
                    <div class="percent-value" style="width:${supplierCommentStatDTO.qualityFiveAmountPer}"></div>
                </div>
                <div class="percent-number fl">${supplierCommentStatDTO.qualityFiveAmountPer}</div>
                <div class="cl"></div>
            </div>
            <div class="cl"></div>
        </div>
        <div class="rate-item">
            <div class="rate-star fl lite-light-star-level-8"></div>
            <div class="rate-score fl"><span class="number-red">3.0 - 4.0&nbsp;</span>分</div>
            <div class="rate-percent fl">
                <div class="percent-bar fl">
                    <div class="percent-value" style="width:${supplierCommentStatDTO.qualityFourAmountPer}"></div>
                </div>
                <div class="percent-number fl">${supplierCommentStatDTO.qualityFourAmountPer}</div>
                <div class="cl"></div>
            </div>
            <div class="cl"></div>
        </div>
        <div class="rate-item">
            <div class="rate-star fl lite-light-star-level-6"></div>
            <div class="rate-score fl"><span class="number-red">2.0 - 3.0&nbsp;</span>分</div>
            <div class="rate-percent fl">
                <div class="percent-bar fl">
                    <div class="percent-value" style="width:${supplierCommentStatDTO.qualityThreeAmountPer}"></div>
                </div>
                <div class="percent-number fl">${supplierCommentStatDTO.qualityThreeAmountPer}</div>
                <div class="cl"></div>
            </div>
            <div class="cl"></div>
        </div>
        <div class="rate-item">
            <div class="rate-star fl lite-light-star-level-4"></div>
            <div class="rate-score fl"><span class="number-red">1.0 - 2.0&nbsp;</span>分</div>
            <div class="rate-percent fl">
                <div class="percent-bar fl">
                    <div class="percent-value" style="width:${supplierCommentStatDTO.qualityTwoAmountPer}"></div>
                </div>
                <div class="percent-number fl">${supplierCommentStatDTO.qualityTwoAmountPer}</div>
                <div class="cl"></div>
            </div>
            <div class="cl"></div>
        </div>
        <div class="rate-item">
            <div class="rate-star fl lite-light-star-level-2"></div>
            <div class="rate-score fl"><span class="number-red">0.0 - 1.0&nbsp;</span>分</div>
            <div class="rate-percent fl">
                <div class="percent-bar fl">
                    <div class="percent-value" style="width:${supplierCommentStatDTO.qualityOneAmountPer}"></div>
                </div>
                <div class="percent-number fl">${supplierCommentStatDTO.qualityOneAmountPer}</div>
                <div class="cl"></div>
            </div>
            <div class="cl"></div>
        </div>
        <div class="cl"></div>
    </div>
    <div class="cl"></div>
</div>

<div class="rate fl" id="rate-fl-performance">
    <div class="rate-average">

        <div class="rate-star fl normal-light-star-level-<fmt:formatNumber value='${supplierCommentStatDTO.performanceTotalScore * 2}' pattern='#0'/>"></div>
        <div class="rate-score fl">
            <c:choose>
                <c:when test="${supplierCommentStatDTO.performanceTotalScore == 0}">暂无分数</c:when>
                <c:otherwise>${supplierCommentStatDTO.performanceTotalScore} 分</c:otherwise>
            </c:choose>
        </div>
        <div class="cl"></div>
    </div>
    <div class="rate-details">
        <div class="rate-item">
            <div class="rate-star fl lite-light-star-level-10"></div>
            <div class="rate-score fl"><span class="number-red">4.0 - 5.0&nbsp;</span>分</div>
            <div class="rate-percent fl">
                <div class="percent-bar fl">
                    <div class="percent-value" style="width:${supplierCommentStatDTO.performanceFiveAmountPer}"></div>
                </div>
                <div class="percent-number fl">${supplierCommentStatDTO.performanceFiveAmountPer}</div>
                <div class="cl"></div>
            </div>
            <div class="cl"></div>
        </div>
        <div class="rate-item">
            <div class="rate-star fl lite-light-star-level-8"></div>
            <div class="rate-score fl"><span class="number-red">3.0 - 4.0&nbsp;</span>分</div>
            <div class="rate-percent fl">
                <div class="percent-bar fl">
                    <div class="percent-value" style="width:${supplierCommentStatDTO.performanceFourAmountPer}"></div>
                </div>
                <div class="percent-number fl">${supplierCommentStatDTO.performanceFourAmountPer}</div>
                <div class="cl"></div>
            </div>
            <div class="cl"></div>
        </div>
        <div class="rate-item">
            <div class="rate-star fl lite-light-star-level-6"></div>
            <div class="rate-score fl"><span class="number-red">2.0 - 3.0&nbsp;</span>分</div>
            <div class="rate-percent fl">
                <div class="percent-bar fl">
                    <div class="percent-value" style="width:${supplierCommentStatDTO.performanceThreeAmountPer}"></div>
                </div>
                <div class="percent-number fl">${supplierCommentStatDTO.performanceThreeAmountPer}</div>
                <div class="cl"></div>
            </div>
            <div class="cl"></div>
        </div>
        <div class="rate-item">
            <div class="rate-star fl lite-light-star-level-4"></div>
            <div class="rate-score fl"><span class="number-red">1.0 - 2.0&nbsp;</span>分</div>
            <div class="rate-percent fl">
                <div class="percent-bar fl">
                    <div class="percent-value" style="width:${supplierCommentStatDTO.performanceTwoAmountPer}"></div>
                </div>
                <div class="percent-number fl">${supplierCommentStatDTO.performanceTwoAmountPer}</div>
                <div class="cl"></div>
            </div>
            <div class="cl"></div>
        </div>
        <div class="rate-item">
            <div class="rate-star fl lite-light-star-level-2"></div>
            <div class="rate-score fl"><span class="number-red">0.0 - 1.0&nbsp;</span>分</div>
            <div class="rate-percent fl">
                <div class="percent-bar fl">
                    <div class="percent-value" style="width:${supplierCommentStatDTO.performanceOneAmountPer}"></div>
                </div>
                <div class="percent-number fl">${supplierCommentStatDTO.performanceOneAmountPer}</div>
                <div class="cl"></div>
            </div>
            <div class="cl"></div>
        </div>
        <div class="cl"></div>
    </div>
    <div class="cl"></div>
</div>

<div class="rate fl" id="rate-fl-speed">
    <div class="rate-average">

        <div class="rate-star fl normal-light-star-level-<fmt:formatNumber value='${supplierCommentStatDTO.speedTotalScore * 2}' pattern='#0'/>"></div>
        <div class="rate-score fl">
            <c:choose>
                <c:when test="${supplierCommentStatDTO.speedTotalScore == 0}">暂无分数</c:when>
                <c:otherwise>${supplierCommentStatDTO.speedTotalScore} 分</c:otherwise>
            </c:choose>
        </div>
        <div class="cl"></div>
    </div>
    <div class="rate-details">
        <div class="rate-item">
            <div class="rate-star fl lite-light-star-level-10"></div>
            <div class="rate-score fl"><span class="number-red">4.0 - 5.0&nbsp;</span>分</div>
            <div class="rate-percent fl">
                <div class="percent-bar fl">
                    <div class="percent-value" style="width:${supplierCommentStatDTO.speedFiveAmountPer}"></div>
                </div>
                <div class="percent-number fl">${supplierCommentStatDTO.speedFiveAmountPer}</div>
                <div class="cl"></div>
            </div>
            <div class="cl"></div>
        </div>
        <div class="rate-item">
            <div class="rate-star fl lite-light-star-level-8"></div>
            <div class="rate-score fl"><span class="number-red">3.0 - 4.0&nbsp;</span>分</div>
            <div class="rate-percent fl">
                <div class="percent-bar fl">
                    <div class="percent-value" style="width:${supplierCommentStatDTO.speedFourAmountPer}"></div>
                </div>
                <div class="percent-number fl">${supplierCommentStatDTO.speedFourAmountPer}</div>
                <div class="cl"></div>
            </div>
            <div class="cl"></div>
        </div>
        <div class="rate-item">
            <div class="rate-star fl lite-light-star-level-6"></div>
            <div class="rate-score fl"><span class="number-red">2.0 - 3.0&nbsp;</span>分</div>
            <div class="rate-percent fl">
                <div class="percent-bar fl">
                    <div class="percent-value" style="width:${supplierCommentStatDTO.speedThreeAmountPer}"></div>
                </div>
                <div class="percent-number fl">${supplierCommentStatDTO.speedThreeAmountPer}</div>
                <div class="cl"></div>
            </div>
            <div class="cl"></div>
        </div>
        <div class="rate-item">
            <div class="rate-star fl lite-light-star-level-4"></div>
            <div class="rate-score fl"><span class="number-red">1.0 - 2.0&nbsp;</span>分</div>
            <div class="rate-percent fl">
                <div class="percent-bar fl">
                    <div class="percent-value" style="width:${supplierCommentStatDTO.speedTwoAmountPer}"></div>
                </div>
                <div class="percent-number fl">${supplierCommentStatDTO.speedTwoAmountPer}</div>
                <div class="cl"></div>
            </div>
            <div class="cl"></div>
        </div>
        <div class="rate-item">
            <div class="rate-star fl lite-light-star-level-2"></div>
            <div class="rate-score fl"><span class="number-red">0.0 - 1.0&nbsp;</span>分</div>
            <div class="rate-percent fl">
                <div class="percent-bar fl">
                    <div class="percent-value" style="width:${supplierCommentStatDTO.speedOneAmountPer}"></div>
                </div>
                <div class="percent-number fl">${supplierCommentStatDTO.speedOneAmountPer}</div>
                <div class="cl"></div>
            </div>
            <div class="cl"></div>
        </div>
        <div class="cl"></div>
    </div>
    <div class="cl"></div>
</div>

<div class="rate fl" id="rate-fl-attitude">
    <div class="rate-average">
        <div class="rate-star fl normal-light-star-level-<fmt:formatNumber value='${supplierCommentStatDTO.attitudeTotalScore * 2}' pattern='#0'/>"></div>
        <div class="rate-score fl">
            <c:choose>
                <c:when test="${supplierCommentStatDTO.attitudeTotalScore == 0}">暂无分数</c:when>
                <c:otherwise>${supplierCommentStatDTO.attitudeTotalScore} 分</c:otherwise>
            </c:choose>
        </div>
        <div class="cl"></div>
    </div>
    <div class="rate-details">
        <div class="rate-item">
            <div class="rate-star fl lite-light-star-level-10"></div>
            <div class="rate-score fl"><span class="number-red">4.0 - 5.0&nbsp;</span>分</div>
            <div class="rate-percent fl">
                <div class="percent-bar fl">
                    <div class="percent-value" style="width:${supplierCommentStatDTO.attitudeFiveAmountPer}"></div>
                </div>
                <div class="percent-number fl">${supplierCommentStatDTO.attitudeFiveAmountPer}</div>
                <div class="cl"></div>
            </div>
            <div class="cl"></div>
        </div>
        <div class="rate-item">
            <div class="rate-star fl lite-light-star-level-8"></div>
            <div class="rate-score fl"><span class="number-red">3.0 - 4.0&nbsp;</span>分</div>
            <div class="rate-percent fl">
                <div class="percent-bar fl">
                    <div class="percent-value" style="width:${supplierCommentStatDTO.attitudeFourAmountPer}"></div>
                </div>
                <div class="percent-number fl">${supplierCommentStatDTO.attitudeFourAmountPer}</div>
                <div class="cl"></div>
            </div>
            <div class="cl"></div>
        </div>
        <div class="rate-item">
            <div class="rate-star fl lite-light-star-level-6"></div>
            <div class="rate-score fl"><span class="number-red">2.0 - 3.0&nbsp;</span>分</div>
            <div class="rate-percent fl">
                <div class="percent-bar fl">
                    <div class="percent-value" style="width:${supplierCommentStatDTO.attitudeThreeAmountPer}"></div>
                </div>
                <div class="percent-number fl">${supplierCommentStatDTO.attitudeThreeAmountPer}</div>
                <div class="cl"></div>
            </div>
            <div class="cl"></div>
        </div>
        <div class="rate-item">
            <div class="rate-star fl lite-light-star-level-4"></div>
            <div class="rate-score fl"><span class="number-red">1.0 - 2.0&nbsp;</span>分</div>
            <div class="rate-percent fl">
                <div class="percent-bar fl">
                    <div class="percent-value" style="width:${supplierCommentStatDTO.attitudeTwoAmountPer}"></div>
                </div>
                <div class="percent-number fl">${supplierCommentStatDTO.attitudeTwoAmountPer}</div>
                <div class="cl"></div>
            </div>
            <div class="cl"></div>
        </div>
        <div class="rate-item">
            <div class="rate-star fl lite-light-star-level-2"></div>
            <div class="rate-score fl"><span class="number-red">0.0 - 1.0&nbsp;</span>分</div>
            <div class="rate-percent fl">
                <div class="percent-bar fl">
                    <div class="percent-value" style="width:${supplierCommentStatDTO.attitudeOneAmountPer}"></div>
                </div>
                <div class="percent-number fl">${supplierCommentStatDTO.attitudeOneAmountPer}</div>
                <div class="cl"></div>
            </div>
            <div class="cl"></div>
        </div>
        <div class="cl"></div>
    </div>
    <div class="cl"></div>
</div>

<div class="vtab">
    <div class="vtab-item actived" id="vtabl-quality">
        <div class="score-name fl">卖家货品质量:</div>
        <div class="score-number fl">
            <c:choose>
                <c:when test="${supplierCommentStatDTO.qualityTotalScore == 0}">
                    暂无分数
                </c:when>
                <c:otherwise>
                    ${supplierCommentStatDTO.qualityTotalScore} 分
                </c:otherwise>
            </c:choose>
        </div>
        <div class="cl"></div>
    </div>
    <div class="vtab-item" id="vtab-performance">
        <div class="score-name fl">货品的性价比:</div>
        <div class="score-number fl">
            <c:choose>
                <c:when test="${supplierCommentStatDTO.performanceTotalScore == 0}">暂无分数</c:when>
                <c:otherwise>${supplierCommentStatDTO.performanceTotalScore} 分</c:otherwise>
            </c:choose>
        </div>
        <div class="cl"></div>
    </div>
    <div class="vtab-item" id="vtab-speed">
        <div class="score-name fl">卖家发货速度:</div>
        <div class="score-number fl">
            <c:choose>
                <c:when test="${supplierCommentStatDTO.speedTotalScore == 0}">暂无分数</c:when>
                <c:otherwise>${supplierCommentStatDTO.speedTotalScore} 分</c:otherwise>
            </c:choose>
        </div>
        <div class="cl"></div>
    </div>
    <div class="vtab-item" id="vtab-attitude">
        <div class="score-name fl">卖家服务态度:</div>
        <div class="score-number fl">
            <c:choose>
                <c:when test="${supplierCommentStatDTO.attitudeTotalScore == 0}">暂无分数</c:when>
                <c:otherwise>${supplierCommentStatDTO.attitudeTotalScore} 分</c:otherwise>
            </c:choose>
        </div>
        <div class="cl"></div>
    </div>
</div>
<div class="cl"></div>
</div>
<!--end info-details-->
<div class="cl"></div>
</dd>
</dl>
<!--end shop-rate-->
<div class="store_introduce">
    <div class="shoppingCart cuSearch storeCart">
        <span class="store_title">来自客户的评价</span>

        <div class="cartTop"></div>
        <div class="cartBody">
            <table cellpadding="0" cellspacing="0" class="tabCart" id="supplierCommentRecordTable">
                <col width="50"/>
                <col width="80"/>
                <col width="220"/>
                <col/>
                <col width="180"/>

                <tr class="titleBg">
                    <td style="padding-left:10px;">No</td>
                    <td>评价时间</td>
                    <td>评分</td>
                    <td>详细评论</td>
                    <td>客户</td>
                </tr>
                <tr class="space">
                    <td colspan="5"></td>
                </tr>
            </table>

            <div id="noSupplierCommentRecord"
                 style="width:480px;height:30px;color: black;display: none;text-align:center;">该供应商暂无评价记录
                ！
            </div>

            <jsp:include page="/common/pageAJAX.jsp">
                <jsp:param name="url" value="supplier.do?method=getSupplierCommentRecord"></jsp:param>
                <jsp:param name="jsHandleJson" value="initSupplierCommentRecord"></jsp:param>
                <jsp:param name="dynamical" value="initSupplierCommentRecord"></jsp:param>
                <jsp:param name="data"
                           value="{startPageNo:'1',maxRows:5,paramShopId:$('#paramShopId').val()}"></jsp:param>
            </jsp:include>

        </div>
        <div class="cartBottom"></div>
    </div>
</div>
</div>
</div>


<div class="tab-content" style="display: none;">
    <div class="store_introduce">
        <div class="cuSearch storeCart">
            <span class="store_title">本店商品搜索</span>

            <div class="cartTop"></div>
            <div class="cartBody">
                <div class="lineAll">
                    <form name="searchConditionDTO" id="searchWholeSalerStockForm"
                          action="autoAccessoryOnline.do?method=getCommodityQuotationsList" method="post">
                        <input type="hidden" name="shopId" value="${paramShopId}"/>
                        <input type="hidden" name="productIds" value="${productIds}"/>
                        <input type="hidden" name="fromSource" value="${fromSource}"/>
                        <input type="hidden" name="inSalesPriceStart" id="tradePriceStart" class="J_clear_input"/>
                        <input type="hidden" name="inSalesPriceEnd" id="tradePriceEnd" class="J_clear_input"/>
                        <input type="hidden" name="maxRows" id="maxRows" value=5>
                        <input type="hidden" name="productCategoryIds" id="productCategoryIds" class="J_clear_input"/>

                        <div class="divTit divWarehouse divShopping">
                            <span class="spanName">商品信息&nbsp;</span>

                            <div class="warehouseList" style="width:660px; margin:0px;">
                                <input type="text" class="txt J-productSuggestion J-initialCss J_clear_input"
                                       name="searchWord"
                                       searchField="product_info" initialValue="品名/品牌/规格/型号/适用车辆"
                                       style="width:255px;" id="productInfo"/>
                                <input type="text" class="txt J-productSuggestion J-initialCss J_clear_input"
                                       name="productName" searchField="product_name" initialValue="品名"/>
                                <input type="text" class="txt J-productSuggestion J-initialCss J_clear_input"
                                       name="productBrand" searchField="product_brand" initialValue="品牌/产地"
                                       style="width:80px;"/>
                                <input type="text" class="txt J-productSuggestion J-initialCss J_clear_input"
                                       name="productSpec" searchField="product_spec" initialValue="规格"
                                       style="width:80px;"/>
                                <input type="text" class="txt J-productSuggestion J-initialCss J_clear_input"
                                       name="productModel" searchField="product_model" initialValue="型号"
                                       style="width:80px;"/>
                                <input type="text"
                                       class="txt J-productSuggestion J-initialCss J_clear_input"
                                       name="productVehicleBrand" searchField="product_vehicle_brand"
                                       initialValue="车辆品牌" style="width:80px;"/>
                                <input type="text"
                                       class="txt J-productSuggestion J-initialCss J_clear_input"
                                       name="productVehicleModel" searchField="product_vehicle_model" initialValue="车型"
                                       style="width:80px;"/>
                                <input type="text"
                                       class="txt J-productSuggestion J-initialCss J_clear_input"
                                       name="commodityCode" searchField="commodity_code" initialValue="商品编号"
                                       style="width:60px;"/>
                            </div>
                        </div>
                        <div class="divTit promotion_type_selector">
                            <span class="spanName">促销折扣&nbsp;</span>
                            <input id="promotionsType" name="promotionsTypeStatusList" type="hidden" value=""/>
                            <label class="rad"><input type="checkbox" id="allChk" promotionsTypes="ALL"/>所有</label>
                            <label class="rad"><input type="checkbox" class="promotionsTypes" id="mljChk"
                                                      promotionsTypes="MLJ_USING"/>满立减</label>
                            <label class="rad"><input type="checkbox" class="promotionsTypes" id="mjsChk"
                                                      promotionsTypes="MJS_USING"/>满就送</label>
                            <label class="rad"><input type="checkbox" class="promotionsTypes" id="bargainChk"
                                                      promotionsTypes="BARGAIN_USING"/>特价商品</label>
                            <label class="rad"><input type="checkbox" class="promotionsTypes" id="freeShippingChk"
                                                      promotionsTypes="FREE_SHIPPING_USING"/>送货上门</label>
                        </div>

                    <div class="divTit button_conditon button_search">
                        <a class="blue_color clean" id="clearConditionBtn">清空条件</a>
                        <a class="button" id="searchShopOnlineProductBtn">搜 索</a>
                    </div>
                    </form>
                </div>
            </div>
            <div class="cartBottom"></div>
        </div>
    </div>
    <div class="clear i_height"></div>
    <div class="cuSearch storeCart">
        <div class="cartTop"></div>
        <div class="cartBody">
            <div class="line_develop list_develop" style="width:740px;">
                <span class="txtTransaction" style="padding-left:10px; width:75px;">共<span id="totalNumber">0</span>条记录</span>
                <a class="accumulative J_span_sort_initProductList" sortFiled="tradePrice" currentSortStatus="Desc"
                   ascContact="点击后按价格升序排列！" descContact="点击后按价格降序排列！">价格<span
                        class="arrowDown J_sort_span_image"></span>

                    <div class="J_sort_div_info alert_develop" style="margin:-5px 0px 0px -15px;display: none">
                        <span class="arrowTop" style="margin-left:20px;"></span>

                        <div class="alertAll">
                            <div class="alertLeft"></div>
                            <div class="alertBody J_sort_div_info_val">
                                点击后按价格升序排列
                                ！
                            </div>
                            <div class="alertRight"></div>
                        </div>
                    </div>
                </a>
                <a class="J_span_sort_initProductList" sortFiled="inventoryAmount" currentSortStatus="Desc"
                   ascContact="点击后按库存量升序排列！"
                   descContact="点击后按库存量排列！">库存量<span class="arrowDown J_sort_span_image"></span>

                    <div class="J_sort_div_info alert_develop" style="margin:-5px 0px 0px -15px;display: none">
                        <span class="arrowTop" style="margin-left:20px;"></span>

                        <div class="alertAll">
                            <div class="alertLeft"></div>
                            <div class="alertBody J_sort_div_info_val">
                                点击后按库存量升序排列
                                ！
                            </div>
                            <div class="alertRight"></div>
                        </div>
                    </div>
                </a>

            <span class="txtTransaction" style="margin-left:10px;">
                <input type="text" class="txt J_clear_input" id="tradePriceStartInput" name="inSalesPriceStart"
                       style="width:40px; height:17px;"/>
                &nbsp;~&nbsp;
                <input type="text" class="txt J_clear_input" id="tradePriceEndInput" name="inSalesPriceEnd"
                       style="width:40px; height:17px;"/>
            </span>

                <div class="txtList" id="tradePriceDiv" style=" left:253px; padding-top:30px; display:none;">
                    <span style="cursor: pointer;" class="clean" id="tradePriceClearBtn">清除</span>
                    <span class="btnSure" id="tradePriceSureBtn">确定</span>

                    <div class="listNum" id="tradePriceListNum">
                        <span class="blue_color" id="tradePrice_1">1千以下</span>
                        <span class="blue_color" id="tradePrice_2">1千~5千</span>
                        <span class="blue_color" id="tradePrice_3">5千~1万</span>
                        <span class="blue_color" id="tradePrice_4">1万以上</span>
                    </div>
                </div>

                <div class="button_opera" style="display: inline-block;float: right;line-height: 26px;margin-top: 3px;margin-right: 5px;">
                    <a class="add_new" id="addToShoppingCartBtn" style="color: white;border-right-width:0px;padding:0px;">加入购物车</a>
                    <a class="add_new" id="purchaseBtn" style="color: white;border-right-width:0px;padding:0px;">立即采购</a>
                </div>
            </div>



            <table class="tab_cuSearch tabSales tabCart" cellpadding="0" cellspacing="0" id="productListTable">
                <col width="30">
                <col>
                <col width="90">
                <col width="90">
                <col width="140">
                <tr class="titleBg">
                    <td style="padding-left:10px;">
                        <input type="checkbox" id="checkAll" class="chk"/>
                    </td>
                    <td>商品信息</td>
                    <td>上架售价</td>
                    <td>库存量</td>
                    <td>操作</td>
                </tr>
                <tr class="titBottom_Bg">
                    <td colspan="5"></td>
                </tr>
            </table>
            <div class="clear i_height"></div>
            <jsp:include page="/common/pageAJAX.jsp">
                <jsp:param name="url" value="autoAccessoryOnline.do?method=getCommodityQuotationsList"></jsp:param>
                <jsp:param name="jsHandleJson" value="initProductList"></jsp:param>
                <jsp:param name="dynamical" value="initProductList"></jsp:param>
                <jsp:param name="data"
                           value="{startPageNo:'1',maxRows:5,shopId:$('#paramShopId').val()}"></jsp:param>
                <jsp:param name="display" value="none"></jsp:param>
            </jsp:include>
        </div>
        <div class="cartBottom"></div>
    </div>
</div>
</div>
<div class="height"></div>

</div>


<!-- shopping Car -->
<div class="alertCheck" id="returnDialog" style="display:none">
    <div class="alert_top"></div>
    <div class="alert_body">
        <div class="alertIcon">
            <a class="right"></a>

            <div class="line"><h3 id="resultMsg"></h3></div>
        </div>
        <div class="clear height"></div>
        <div class="line lines" id="warnMsg"></div>
        <div class="line lines">共有<b id="shoppingCartItemCount">0</b>种商品，合计：¥<b class="yellow_color"
                                                                                id="shoppingCartTotal">0</b></div>
        <div class="clear height"></div>
        <div class="button">
            <a class="btnHover" id="goShoppingCartBtn">去购物车结算</a>
            <a class="blue_color" id="closeBtn">继续采购</a>
        </div>
    </div>
    <div class="alert_bottom"></div>
</div>

<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>