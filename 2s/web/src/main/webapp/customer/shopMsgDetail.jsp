<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="bcgogo" uri="http://www.bcgogo.com/taglibs/tags" %>
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

    <%@include file="/WEB-INF/views/header_script.jsp" %>
    <script type="text/javascript" src="js/shopMsgDetail<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/page/autoaccessoryonline/promotionsUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery.ztree.core-3.5.min.js"></script>
    <script type="text/javascript">
    defaultStorage.setItem(storageKey.MenuUid, "CUSTOMER_MANAGER_SEARCH_APPLY_CUSTOMER");
    defaultStorage.setItem(storageKey.MenuCurrentItem,"店铺资料");


    var selfShopId = '${sessionScope.shopId}';

        $(document).ready(function () {
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
        });

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

            if (!G.isEmpty($("#simpleSearchFlag").val())) {
                $("#productQuickSearchBtn").trigger("click");
            } else {
                $("#searchWholeSalerStockForm").submit(); //进入页面进行一次查询
            }

        });
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
                }
            };

            var result = APP_BCGOGO.Net.syncPost(
                    {
                        url: "/web/shopMsgDetail.do?method=getProductCategoryListSimpleJsonFormat",
                        dataType: "json",
                        data: {shopId: $("#paramShopId").val()}
                    }
            );

            var zNodes;

            if (!G.isEmpty(result) && !G.isEmpty(result.data)) {
                zNodes = result.data;
            }

            console.log(zNodes);

            $(document).ready(function () {
                $.fn.zTree.init($("#treeDemo"), setting, zNodes);
            });

        }


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
            $(".vtab-item").bind("click", function (e) {
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
                e.preventDefault();
                var $domObject = $(this).closest("tr").find("input[name='selectProduct']");
                var productId = $domObject.attr("data-productlocalinfoid");
                var amount = G.normalize($(this).closest("tr").find(".J-Amount").val(), "1");
                var supplierShopId = $domObject.attr("data-suppliershopid");
                var paramString = supplierShopId + "_" + productId + "_" + amount;
                addProductToShoppingCart(paramString);
            });

            // 批量加入购物车
            $("#addToShoppingCartBtn").bind("click", function (e) {
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
                e.preventDefault();
                var productId = $(this).closest("tr").find("input[name='selectProduct']").attr("data-productlocalinfoid");
                var amount = 1;
                if (!G.Lang.isEmpty(productId)) {
                    purchaseProduct(productId, amount);
                }
            });
            // 批量下采购单
            $("#purchaseBtn").click(function () {
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
<div class="i_main clear">
    <div class="clear i_height"></div>
    <div class="titBody">
        <div class="store_top_blue">
            <div class="storeName"><span>${shopDTO.name}</span> <span class="storeAddress">${shopDTO.areaName}</span></div>
            <div class="storeManu"><a id="default">店铺介绍</a><a id="comment">店铺评价</a></div>
        </div>
        <div class="store_left">
            <div class="store_kind"><span class="store_title_blue">店铺信息</span>

                <div class="shop store_list">
                    <div class="title"><span class="shop_name">${shopDTO.name}</span><span class="yellow_bg">商家</span></div>
                    <div class="clear i_height"></div>
                    <div class="store_connecter">入驻日期：<span>${shopDTO.registrationDateStr}</span></div>
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
                    <div class="store_connecter">收藏人气：已有<a class="blue_color"><b>${shopDTO.beStored}</b></a>家</div>
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
            <div class="store_kind">
                <span class="store_title">收藏店铺到手机</span>
                <div>
                    <img id="rq" src="${shopDTO.imageCenterDTO.shopRQImageDetailDTO.imageURL}" style="margin-left: 50px;"/>
                </div>
            </div>
        </div>
        <div class="store_right">
            <div class="tab-content">
                <div class="store_kind">
                    <span class="store_title_blue">店铺介绍</span>

                    <div class="store_content">
                        <b>服务范围：</b>
                <span>
                    ${empty shopDTO.serviceCategoryStr?'':shopDTO.serviceCategoryStr}
                </span>

                        <div class="clear i_height"></div>
                        <b>主营车型：</b>
                <span>
                    ${empty shopDTO.shopVehicleBrandModelStr?'':shopDTO.shopVehicleBrandModelStr}
                </span>

                        <div class="clear i_height"></div>
                        <b>经营产品：</b>
                <span>
                    ${empty shopDTO.businessScopeStr?'暂无信息':shopDTO.businessScopeStr}
                </span>

                        <div class="clear i_height"></div>
                        <b>联系方式</b>

                        <div class="clear"></div>
                        <c:forEach items="${shopDTO.contacts}" var="contact" varStatus="status">
                            <div class="contact_list" ${status.last?'last_noBorder':''}>
                                <span class="name">${contact.name}</span>

                                <div class="mode" style="overflow: hidden;text-overflow: ellipsis;">
                                    <a class="icon_phone">${empty contact.mobile?"暂无信息":contact.mobile}</a>
                                    <a class="icon_QQ">${empty contact.qq?"暂无信息":contact.qq}</a>
                                    <a class="icon_email" style="overflow: hidden;text-overflow: ellipsis;width:120px;" title="${empty contact.email?"暂无信息":contact.email}">${empty contact.email?"暂无信息":contact.email}</a>
                                </div>
                            </div>
                        </c:forEach>
                        <div class="clear i_height"></div>
                        <c:if test="${!empty shopDTO.imageCenterDTO && !empty shopDTO.imageCenterDTO.shopImageDetailDTOs}">
                            <b>店面风采</b>

                            <div class="clear i_height"></div>
            <span class="store_photo">
            <c:forEach var="shopImageDetailDTO" items="${shopDTO.imageCenterDTO.shopImageDetailDTOs}">
                <span class="store_photo"><img src="${shopImageDetailDTO.imageURL}"/></span>
            </c:forEach>
            </span>
                        </c:if>
                    </div>
                </div>
            </div>
            <div class="tab-content" style="display: none;">
                <div class="store_introduce"> <span class="store_title">店铺评价</span>
                    <div class="shopevaluation" id="averageComment"><b>店铺综合评分</b> <a  class="star" id="averageCommentStar"></a> <strong class="yellow_color" id="averageScoreAmount">0分</strong> <span style="color:#999999;">共<span id="recordAmount">0</span>名客户参与评价</span></div>
                    <div class="shopevaluation" style="display:none;color:#999999;" id="noComment">暂无评价！</div>
                    <div class="shoppingCart cuSearch storeCart">
                        <div class="cartTop"></div>
                        <div class="gray-radius">
                            <table cellpadding="0" cellspacing="0" class="tabCart" style="width:771px;" id="appUserCommentTable">
                                <col width="50">
                                <col width="80">
                                <col width="180">
                                <col width="220">
                                <col width="120">
                                <tr class="titleBg">
                                    <td style="padding-left:10px;">No</td>
                                    <td>评价时间</td>
                                    <td>评分</td>
                                    <td>详细评论</td>
                                    <td>客户</td>
                                </tr>
                            </table>
                            <div class="i_height"></div>
                            <div class="i_pageBtn" style="float:right;margin: 10px 0 10px 0">
                                <bcgogo:ajaxPaging url="supplier.do?method=getAppUserCommentRecord" postFn="initAppUserCommentRecord" dynamical="initAppUserCommentRecord" data="{startPageNo:'1',maxRows:5,paramShopId:$('#paramShopId').val()}"/>
                            </div>
                        </div>
                        <div class="cartBottom"></div>
                    </div>
                </div>
            </div>

        </div>
    </div>
</div>
<div class="height"></div>
<div id="mask" style="display:block;position: absolute;"></div>
<iframe id="iframe_PopupBox" style="position:absolute;z-index:5; left:400px; top:400px; display:none;"
        allowtransparency="true" width="630px" height="450px" frameborder="0" src=""></iframe>
<!----------------------------页脚----------------------------------->
<%@include file="/WEB-INF/views/footer_html.jsp" %>

</body>
</html>