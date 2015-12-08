<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>配件报价</title>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/addCheck<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/commodityQuotations<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" href="js/components/themes/bcgogo-ratting<%=ConfigController.getBuildVersion()%>.css">
    <link rel="stylesheet" href="js/components/themes/bcgogo-scorePanel<%=ConfigController.getBuildVersion()%>.css">
    <link rel="stylesheet" href="js/components/themes/bcgogo-sidebarListPanel<%=ConfigController.getBuildVersion()%>.css"/>

    <%@include file="/WEB-INF/views/header_script.jsp" %>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.min.js"></script>
    <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/utils/tableUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/jsScroller<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/jsScrollbar<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/page/autoaccessoryonline/promotionsUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/page/autoaccessoryonline/commodityQuotations<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/customerOrSupplier/supplierCommentUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/components/ui/bcgogo-scorePanel<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/components/ui/bcgogo-ratting<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/components/ui/bcgogo-scrollFlow<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/components/ui/bcgogo-sidebarListPanel<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">
        defaultStorage.setItem(storageKey.MenuUid,"AUTO_ACCESSORY_ONLINE_COMMODITYQUOTATIONS");
        function setProductCategoryIds(sidebarListPanel){
            var productCategoryIds = [];
            $("#productCategorySelected div.select").each(function(){
                var parentId = $(this).attr("data-parent-id");
                var dataId = $(this).attr("data-id");
                if(G.isEmpty(parentId)){
                    var childDatas = sidebarListPanel.getData("secondCategoryIdStr", dataId);
                    $.each(childDatas, function(index, val) {
                        productCategoryIds.push(val.idStr);
                    });
                    productCategoryIds.push(dataId);
                }else{
                    productCategoryIds.push($(this).attr("data-id"));
                }
            });
            if(productCategoryIds.length > 0){
                $("#productCategoryDl").show();
            }else{
                $("#productCategoryDl").hide();
            }
            $("#productCategoryIds").val(productCategoryIds.join(","));
        }

        $(document).ready(function() {
            function _genProductCategoryCondition(data){
                if(data && data.length>0){
                    //判断点击的是否已经被选择   （父类被选择或者自己被选择）
                    //更新选择的div
                    var currentDataSiblingSize = sidebarListPanel.getData('secondCategoryIdStr',data[0].secondCategoryIdStr).length;
                    var selectedDataSibling = $("#productCategorySelected").find('div[data-parent-id="'+data[0].secondCategoryIdStr+'"]');
                    var selectedSize = data.length>1?data.length:G.Number.normalize(selectedDataSibling.length)+1;
                    if(currentDataSiblingSize>0 && currentDataSiblingSize==selectedSize && !$("#productCategorySelected").find('div[data-id="'+data[0].secondCategoryIdStr+'"]').length>0){
                        selectedDataSibling.remove();
                        var htmlStr ='<div class="select" data-id="'+data[0].secondCategoryIdStr+'"><span class="select_left"></span><span class="select_body">'+data[0].secondCategoryName+'<a class="select_close"></a></span><span class="select_right"></span></div>';
                        $("#productCategorySelected").append(htmlStr);
                    }else{
                        var htmlStr="";
                        $.each(data, function(index, val) {
                            if(!$("#productCategorySelected").find('div[data-id="'+val.idStr+'"]').length>0
                                    && !$("#productCategorySelected").find('div[data-id="'+val.secondCategoryIdStr+'"]').length>0)
                                htmlStr +='<div class="select" data-parent-id="'+val.secondCategoryIdStr+'" data-id="'+val.idStr+'"><span class="select_left"></span><span class="select_body">'+val.name+'<a class="select_close"></a></span><span class="select_right"></span></div>';
                        });
                        $("#productCategorySelected").append(htmlStr);
                    }
                    $(".item-product-selected").show();
                }
            }
            var sidebarListPanel = new App.Module.SidebarListPanel();
//            window.sidebarListPanel = sidebarListPanel;
            sidebarListPanel.init({
                selector:".sidebar-list-panel-group",
                data:JSON.parse('${thirdProductCategoryDTOsJson}'),
                onSelect:function(data, event) {//data中的数据肯定 同一个parent
                    _genProductCategoryCondition(data);
                    setProductCategoryIds(sidebarListPanel);

                    $("#searchQuotationBtn").trigger("click");
                }
            });
            $(".select_close").live("click", function() {
                var selectDiv = $(this).closest("div");
                selectDiv.remove();
                setProductCategoryIds(sidebarListPanel);
                $("#searchQuotationBtn").trigger("click");
            });
            var productCategoryDTOsJson='${productCategoryDTOsJson}';
            if(!G.isEmpty(productCategoryDTOsJson)){
                var productCategoryDTOs=JSON.parse('${productCategoryDTOsJson}');
                _genProductCategoryCondition(productCategoryDTOs);
                  setProductCategoryIds(sidebarListPanel);
            }

            if(GLOBAL.Util.getUrlParameter("fromSource")=="allPromotion"){
                var promotionsType=new Array();
                var  promotionsTypesCount=0;
                $(".promotionsTypes").each(function(){
                        promotionsType.push($(this).attr("promotionsTypes"))
                });
                    $(".promotionsTypes,#allChk").attr("checked",true);
                $("#promotionsType").val(promotionsType.toString());
            }
            //初始化  SearchConditionInput css
            $(".J-initialCss").placeHolder();
            $("#searchWholeSalerStockForm").submit();
        });
    </script>
</head>

<body class="bodyMain">
<%@ include file="/WEB-INF/views/header_html.jsp" %>
<div class="i_main clear">
<jsp:include page="autoAccessoryOnlineNavi.jsp">
    <jsp:param name="currPage" value="commodityQuotations"/>
</jsp:include>

<div class="titBody">


<div class="content-sidebar parts-quoted-price fl">
    <div class="sidebar-title sidebar-yellow-gradient">
        全部商品分类
    </div>

    <div class="sidebar-list-panel-group">

        </div>
        <c:if test="${!empty recentlyViewedProductDTOList}">
            <dl class="sidebar-latest-view">
                <dt class="view-title">最近浏览</dt>
                <dd class="view-content">
                    <c:forEach var="recentlyViewedProductDTO" items="${recentlyViewedProductDTOList}">
                        <div class="content-item">
                            <div class="item-img fl w60 h60" style="position: relative;">
                                <a style="cursor: pointer;position: absolute;z-index: 1;" data-suppliershopid="${recentlyViewedProductDTO.shopId}" data-productlocalinfoid="${recentlyViewedProductDTO.productLocalInfoIdStr}" class="J_showProductInfo">
                                    <img class="w60 h60" src="${recentlyViewedProductDTO.imageCenterDTO.productListSmallImageDetailDTO.imageURL}"/>
                                </a>
                                <c:if test="${!empty recentlyViewedProductDTO.promotionsDTOs}">
                                    <div class="promotionsing <c:if test="${recentlyViewedProductDTO.hasBargain}">hasBargain</c:if>">
                                            ${recentlyViewedProductDTO.promotionTypesShortStr}
                                    </div>
                                </c:if>
                            </div>
                            <div class="item-info mgl5">
                                <div title="${recentlyViewedProductDTO.productInfo}" class="info-name fl w90 mgl5" style="height: 48px;overflow: hidden;"><a style="word-wrap:break-word" data-suppliershopid="${recentlyViewedProductDTO.shopId}" data-productlocalinfoid="${recentlyViewedProductDTO.productLocalInfoIdStr}" class="blue_color J_showProductInfo">${recentlyViewedProductDTO.productInfo}</a></div>
                                <div class="info-price fl mgl5">
                                    <c:if test="${!recentlyViewedProductDTO.hasBargain}">
                                        &yen;${empty recentlyViewedProductDTO.inSalesPrice ? 0 : recentlyViewedProductDTO.inSalesPrice}
                                    </c:if>
                                    <c:if test="${recentlyViewedProductDTO.hasBargain}">
                                        <span style="color: #848484;text-decoration: line-through;font-weight: normal;">&yen;${empty recentlyViewedProductDTO.inSalesPrice ? 0 : recentlyViewedProductDTO.inSalesPrice}</span>
                                        <span>&yen;${empty recentlyViewedProductDTO.inSalesPriceAfterCal ? 0 : recentlyViewedProductDTO.inSalesPriceAfterCal}</span>
                                    </c:if>
                                </div>
                                <div class="cl"></div>
                            </div>
                        </div>
                </c:forEach>
            </dd>
        </dl>
    </c:if>
</div>


<div class="content-main parts-quoted-price">
<c:if test="${!isWholesaler}">
    <div class="matched-recommend J_recommendProductShow" style="display: none">
        <dl>
            <dt class="content-title">
            <div class="bg-top-hr"></div>
            <div class="bar-tab">
                <span class="title-name">匹配推荐</span>
            </div>
            </dt>
            <dd class="content-details">
                <!--add horizontal scroll component-->
                <div id="recommendProductDiv" class="JScrollFlowHorizontal">
                        <%--<ul class="JScrollGroup">--%>
                        <%--<c:forEach var="recommendProductDTO" items="${recommendProductDTOList}" varStatus="status">--%>
                        <%--<c:if test="${status.index%3==0}">--%>
                        <%--<li class="JScrollItem">--%>
                        <%--<div class="details-group">--%>
                        <%--</c:if>--%>

                        <%--<div class="group-item fl ">--%>
                        <%--<div class="item-img fl">--%>
                        <%--<div class="img-comment tip-note-delivery tip-note-special-offers"></div>--%>
                        <%--<div class="img-main">--%>
                        <%--<a style="cursor: pointer" data-productlocalinfoid="${recommendProductDTO.productLocalInfoIdStr}" class="J_showProductInfo">--%>
                        <%--<img src="${recommendProductDTO.imageCenterDTO.recommendProductListImageDetailDTO.imageURL}" />--%>
                        <%--</a>--%>
                        <%--</div>--%>
                        <%--</div>--%>
                        <%--<div class="item-info fl">--%>
                        <%--<div class="info-name fl"><a data-productlocalinfoid="${recommendProductDTO.productLocalInfoIdStr}" class="blue_color J_showProductInfo">${recommendProductDTO.productInfo}</a></div>--%>
                        <%--<div class="info-price fl">&yen;${empty recommendProductDTO.inSalesPrice?0:recommendProductDTO.inSalesPrice}</div>--%>
                        <%--<div class="cl"></div>--%>
                        <%--</div>--%>
                        <%--<div class="cl"></div>--%>
                        <%--</div>--%>

                        <%--<c:if test="${status.index%3==2}">--%>
                        <%--<div class="cl"></div>--%>
                        <%--</div>--%>
                        <%--</li>--%>
                        <%--</c:if>--%>
                        <%--</c:forEach>--%>
                        <%--</ul>--%>
                </div>
            </dd>
        </dl>
    </div>
</c:if>

<form:form commandName="searchConditionDTO" id="searchWholeSalerStockForm" action="autoAccessoryOnline.do?method=getCommodityQuotationsList" method="post" name="thisform">
    <form:hidden path="productIds" value="${searchConditionDTO.productIds}"/>
    <form:hidden path="shopId" />
    <form:hidden path="inSalesPriceStart" cssClass="J_clear_input" />
    <form:hidden path="inSalesPriceEnd" cssClass="J_clear_input"/>
    <input type="hidden" id="fromPage" value="${fromPage}">
    <input type="hidden" name="maxRows" id="maxRows" value="15">
    <input type="hidden" name="fromSource" id="fromSource" value="${fromSource}">
    <input type="hidden" name="productCategoryIds" id="productCategoryIds">
    <input type="hidden" id="shopRange" value="${isWholesaler?'related':''}">
    <div class="search-param">
        <div class="content-title">
            <div class="bg-top-hr"></div>
            <div class="bar-tab">配件报价查询</div>
        </div>
        <dl class="item-product-selected" id="productCategoryDl" style="display:none;">
            <dt style="margin-top: 5px;">已选商品分类</dt>
            <dd id="productCategorySelected" class="selectList" style="margin-top: 0px;">

            </dd>
            <div class="cl"></div>
        </dl>
        <dl class="param-product-info">
            <dt>商品信息</dt>
            <dd>
                <form:input cssClass="search-fuzzy product-info J-productSuggestion J-initialCss J_clear_input" path="searchWord" searchField="product_info" initialValue="品名/品牌/规格/型号/适用车辆"/>
                <form:input cssClass="search-exact product-name J-productSuggestion J-initialCss J_clear_input" path="productName" searchField="product_name" initialValue="品名"/>
                <form:input cssClass="search-exact product-brand J-productSuggestion J-initialCss J_clear_input" path="productBrand" searchField="product_brand" initialValue="品牌/产地"/>
                <form:input cssClass="search-exact product-specifications J-productSuggestion J-initialCss J_clear_input" path="productSpec" searchField="product_spec" initialValue="规格"/>
                <form:input cssClass="search-exact product-type J-productSuggestion J-initialCss J_clear_input" path="productModel" searchField="product_model" initialValue="型号"/>
                <form:input cssClass="search-exact product-vehicle-brand J-productSuggestion J-initialCss J_clear_input" path="productVehicleBrand" searchField="product_vehicle_brand" initialValue="车辆品牌"/>
                <form:input cssClass="search-exact product-vehicle-type J-productSuggestion J-initialCss J_clear_input" path="productVehicleModel" searchField="product_vehicle_model" initialValue="车型"/>
                <form:input cssClass="search-exact product-number J-productSuggestion J-initialCss J_clear_input" path="commodityCode" searchField="commodity_code" initialValue="商品编号" cssStyle="text-transform: uppercase;"/>
            </dd>
            <div class="cl"></div>
        </dl>
        <dl class="param-area">
            <dt>供应商</dt>
            <dd style="width: 206px"><form:input cssClass="txt J-wholesalerSuggestion J-initialCss J_clear_input" path="shopName" initialValue="供应商" style="width:194px;"/></dd>

            <dt style="width: 60px">所属区域</dt>
            <dd style="width: 430px">
                <select class="txt" style="width:130px;height: 20px;color:#BBBBBB" id="provinceNo" name="provinceNo">
                    <option class="default" style="color:#BBBBBB" value="">--请选择省份--</option>
                    <option class="default" style="color:#000000" value="">全国</option>
                </select>&nbsp;&nbsp;
                <select class="txt" style="width:130px;height: 20px;color:#BBBBBB" id="cityNo" name="cityNo">
                    <option class="default" style="color:#BBBBBB" value="">--请选择城市--</option>
                    <option class="default" style="color:#000000" value="">全省</option>
                </select>&nbsp;&nbsp;
                <select class="txt" style="width:130px;height: 20px;color:#BBBBBB" id="regionNo" name="regionNo">
                    <option class="default" style="color:#BBBBBB" value="">--请选择区域--</option>
                    <option class="default" style="color:#000000" value="">全市</option>
                </select>
            </dd>
            <div class="cl"></div>
        </dl>
        <dl class="param-promotion">
            <dt>促销类型</dt>
            <dd>
                <input id="promotionsType" name="promotionsTypeStatusList" type="hidden" value=""/>
                <label class="rad"><input type="checkbox"  id="allChk" promotionsTypes="ALL"/>所有</label>
                <label class="rad"><input type="checkbox" class="promotionsTypes" id="mljChk" promotionsTypes="MLJ_USING"/>满立减</label>
                <label class="rad"><input type="checkbox" class="promotionsTypes" id="mjsChk" promotionsTypes="MJS_USING"/>满就送</label>
                <label class="rad"><input type="checkbox" class="promotionsTypes" id="bargainChk" promotionsTypes="BARGAIN_USING"/>特价商品</label>
                <label class="rad"><input type="checkbox" class="promotionsTypes" id="freeShippingChk" promotionsTypes="FREE_SHIPPING_USING"/>送货上门</label>
            </dd>
            <div class="cl"></div>
        </dl>
        <div class="group-button-control">
            <div class="button-search button-blue-gradient" id="searchQuotationBtn">搜&nbsp;&nbsp;索</div>
            <div class="button-clear" id="clearConditionBtn">清空条件</div>
        </div>
    </div>
</form:form>
<div class="search-result" style="padding-top: 7px;">
    <div class="line_develop list_develop sort_title_min_width" style="margin-left: 8px; padding-right: 3px; padding-top: 2px;">
        <span class="txtTransaction" style="padding-left:10px;">共<span id="totalNumber">0</span>条记录</span>
        <a class="J_span_sort_commodityQuotations" sortFiled="shopName" currentSortStatus="Desc" ascContact="点击后按供应商升序排列！"  descContact="点击后按供应商降序排列！">供应商<span class="arrowDown J_sort_span_image"></span>
            <div class="J_sort_div_info alert_develop" style="margin:-5px 0px 0px -15px;display: none">
                <span class="arrowTop" style="margin-left:20px;"></span>
                <div class="alertAll">
                    <div class="alertLeft"></div>
                    <div class="alertBody J_sort_div_info_val">
                        点击后按供应商升序排列！
                    </div>
                    <div class="alertRight"></div>
                </div>
            </div>
        </a>
        <a class="accumulative J_span_sort_commodityQuotations" sortFiled="inSalesPrice" currentSortStatus="Desc" ascContact="点击后按价格升序排列！"  descContact="点击后按价格降序排列！">价格<span class="arrowDown J_sort_span_image"></span>
            <div class="J_sort_div_info alert_develop" style="margin:-5px 0px 0px -15px;display: none">
                <span class="arrowTop" style="margin-left:20px;"></span>
                <div class="alertAll">
                    <div class="alertLeft"></div>
                    <div class="alertBody J_sort_div_info_val">
                        点击后按价格升序排列！
                    </div>
                    <div class="alertRight"></div>
                </div>
            </div>
        </a>
        <div style="float: left">
                    <span class="txtTransaction" style="padding-left: 4px">
                    <input type="text" class="txt J_clear_input" id="inSalesPriceStartInput" style="width:40px; height:17px;" />
                    &nbsp;~&nbsp;
                    <input type="text" class="txt J_clear_input" id="inSalesPriceEndInput" style="width:40px; height:17px;" />
                    </span>
            <div class="txtList" id="inSalesPriceDiv" style="padding-left: 10px;padding-top:30px; display:none;">
                <span style="cursor: pointer;" class="clean" id="inSalesPriceClearBtn">清除</span>
                <span class="btnSure" id="inSalesPriceSureBtn">确定</span>

                <div class="listNum" id="inSalesPriceListNum">
                    <span class="blue_color" id="inSalesPrice_1">1千以下</span>
                    <span class="blue_color" id="inSalesPrice_2">1千~5千</span>
                    <span class="blue_color" id="inSalesPrice_3">5千~1万</span>
                    <span class="blue_color" id="inSalesPrice_4">1万以上</span>
                </div>
            </div>
        </div>
        <div class="group-button fr" style="display: inline-block;margin-top: 1px;">
            <div id="purchaseBtn" class="button-purchase fl button-blue-gradient" style="height: 22px;line-height: 22px;">立即采购</div>
            <div id="addToShoppingCartBtn" class="button-buy-cart-added fl button-blue-gradient" style="height: 22px;line-height: 22px;">加入购物车</div>
            <div class="cl"></div>
        </div>
    </div>

    <table class="list-result" cellpadding="0" id="commodityTable" cellspacing="0">
        <thead>
        <tr>
            <td class="item-checkbox"><input type="checkbox" id="checkAll" class="chk" /></td>
            <td class="item-product-information">配件名</td>
            <td class="item-product-price">上架售价</td>
            <td class="item-product-supplier">供应商</td>
            <td class="item-product-area">所在区域</td>
            <td class="item-product-added-count">上架量</td>
            <td class="item-product-operating">操作</td>
        </tr>
        </thead>

    </table>
    <!--end search-result-->
</div>

<div class="height"></div>
<!--分页控制部分，use common code snip-->
<jsp:include page="/common/pageAJAX.jsp">
    <jsp:param name="url" value="autoAccessoryOnline.do?method=getCommodityQuotationsList"></jsp:param>
    <jsp:param name="data" value="{startPageNo:1,maxRows:15,promotionsTypeStatusList:$('#promotionsType').val()}"></jsp:param>
    <jsp:param name="jsHandleJson" value="drawCommodityQuotationsTable"></jsp:param>
    <jsp:param name="dynamical" value="commodityQuotations"></jsp:param>
    <jsp:param name="display" value="none"></jsp:param>
</jsp:include>

<div class="batch-buy" style="width:815px;">
    <div class="all-select">
        <label><input id="floatBarSelectAll" class="select_all" type="checkbox">&nbsp;全选</label>
    </div>
    <div class="button-batch-shelves button-yellow-gradient" id="floatBarPurchaseBtn">立即采购</div>
    <div class="button-batch-shelves button-yellow-gradient" id="floatBarAddToShoppingCartBtn">加入购物车</div>
    <div class="cl"></div>
</div>
</div>
<div class="height"></div>
<!--end content-main-->
</div>
<div class="alertCheck" id="returnDialog" style="display:none">
    <div class="alert_top"></div>
    <div class="alert_body">
        <div class="alertIcon">
            <a class="right"></a>
            <div class="line"><h3 id="resultMsg"></h3></div>
        </div>
        <div class="clear height"></div>
        <div class="line lines" id="warnMsg"></div>
        <div class="line lines">共有<b id="shoppingCartItemCount">0</b>种商品，合计：¥<b class="yellow_color" id="shoppingCartTotal">0</b></div>
        <div class="clear height"></div>
        <div class="button">
            <a class="btnHover" style="border:0px" id="goShoppingCartBtn">去购物车结算</a>
            <a class="blue_color" id="closeBtn">继续采购</a>
        </div>
    </div>
    <div class="alert_bottom"></div>
</div>
<div style="margin-left:170px;"><%-- 为浮动工具栏按钮,向右偏移一些 --%>
    <%@include file="/WEB-INF/views/footer_html.jsp" %>
</div>
</body>

</html>