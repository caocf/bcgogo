<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>购物车</title>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/cuxiao<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" href="js/components/themes/bcgogo-ratting<%=ConfigController.getBuildVersion()%>.css">
    <link rel="stylesheet" href="js/components/themes/bcgogo-scorePanel<%=ConfigController.getBuildVersion()%>.css">
    <%@include file="/WEB-INF/views/header_script.jsp" %>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.js"></script>
    <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/page/autoaccessoryonline/shoppingCart<%=ConfigController.getBuildVersion()%>.js"></script>
       <script type="text/javascript" src="js/page/autoaccessoryonline/promotionsUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/customerOrSupplier/supplierCommentUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/components/ui/bcgogo-scorePanel<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/components/ui/bcgogo-ratting<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">
        defaultStorage.setItem(storageKey.MenuUid, "AUTO_ACCESSORY_ONLINE_SHOPPINGCART");
        $().ready(function(){
            var supplierProductIdArr=new Array();
            $(".item").each(function(){
                supplierProductIdArr.push($(this).find(".productId").val());
            });
            initOrderPromotionsDetail(supplierProductIdArr);
            $("#selectAll,#floatBarSelectAll,.J-ForShop,.J-CheckBoxItem").attr("checked",true);
        });

    </script>
</head>
<body class="bodyMain">
<%@include file="/WEB-INF/views/header_html.jsp" %>
<%@ include file="/common/messagePrompt.jsp" %>
<div class="i_main clear">
<jsp:include page="autoAccessoryOnlineNavi.jsp">
    <jsp:param name="currPage" value="shoppingCart"/>
</jsp:include>
<div class="mainTitles">
    <div class="cart">
        <div class="cartBody">
            <label>购物车状态</label>
            <div class="status">
                <a class="statusBg J-ShoppingCartStatusImage" style="width:${shoppingCartDTO.shoppingCartItemCount*100/shoppingCartDTO.shoppingCartMaxCapacity}%"></a>
            </div>&nbsp;
            <span class="J-ShoppingCartStatus">${shoppingCartDTO.shoppingCartItemCount}/${shoppingCartDTO.shoppingCartMaxCapacity}</span>
        </div>
    </div>
</div>
<div class="clear"></div>
<div class="shoppingCart" >
<div class="cartTop"></div>
<div class="cartBody">
<table cellpadding="0" cellspacing="0" class="tabCart" id="mainCartTable">
<col width="70">
<col>
<col width="220">
<col width="80">
<col width="100">
<col width="80">
<col width="40">
<tr class="titleBg">
    <td style="padding-left:10px;"><label for="selectAll"><input id="selectAll" type="checkbox" class="chk" />全选</label></td>
    <td>商品详情</td>
    <td>单价（元）</td>
    <td>数量</td>
    <td>优惠</td>
    <td>小计（元）</td>
    <td>操作</td>
</tr>
<c:if test="${not empty shoppingCartDTO.shoppingCartDetailMap}">
<c:forEach var="shoppingCartDetailMap" items="${shoppingCartDTO.shoppingCartDetailMap}"  varStatus="shopStatus">
<c:set var="supplierDTO" value="${shoppingCartDetailMap.key}" />
<tr>
<td colspan="7">
<table cellpadding="0" cellspacing="0" class="tabCart J-ShopDataBlock J-purchaseOnlineTable" style="margin:-10px 0px 0px;">
<col width="30">
<col width="375">
<col width="200">
<col width="80">
<col width="100">
<col width="80">
<col width="40">

<c:choose>
<c:when test="${false}">
    <tr class="J-ItemHead">
        <td colspan="7" style="padding-left:10px;">
            <div style="float: left">
                <input type="checkbox" class="chk J-ForShop" style="float: left"/>

                供应商：<a class="gray_color">${supplierDTO.name}</a>
                <a class="Non-associated" >非关联</a>
            </div>
            <div style="float: left">
                <a onmouseout="scorePanelHide();"
                   id="commentScoreObject${shopStatus.index}"
                   onmouseover="showSupplierCommentScore(this,
                           ${supplierDTO.totalAverageScore == null?0:supplierDTO.totalAverageScore},
                           ${supplierDTO.commentRecordCount == null?0:supplierDTO.commentRecordCount},
                           ${supplierDTO.qualityAverageScore == null?0:supplierDTO.qualityAverageScore},
                           ${supplierDTO.performanceAverageScore == null?0:supplierDTO.performanceAverageScore},
                           ${supplierDTO.speedAverageScore == null?0:supplierDTO.speedAverageScore},
                           ${supplierDTO.attitudeAverageScore == null?0:supplierDTO.attitudeAverageScore});"
                   onclick="redirectShopCommentDetail('${supplierDTO.supplierShopId}')">
                    <span class="star" style="margin: 0 3px 4px 0; vertical-align: middle;"></span>
                    <c:set var="totalAverageScore" value="${supplierDTO.totalAverageScore == null?0:supplierDTO.totalAverageScore}"/>
                    <b class="color_yellow"><span>${totalAverageScore>0.001 ?totalAverageScore:"暂无"}${totalAverageScore>0.001 ?"分":""}</span></b>
                </a>
            </div>
        </td>
    </tr>
    <c:forEach items="${shoppingCartDetailMap.value}" var="shoppingCartItemDTO" varStatus="itemStatus">
        <c:choose>
            <c:when test="${shoppingCartItemDTO.salesStatus eq 'InSales'}">
                <tr class="bg tr_ban J-ItemBody item" data-shoppingcartitemid="${shoppingCartItemDTO.id}">
                    <td colspan="2" style="padding-left:20px;">
                        <input type="hidden" class="productId" value="${shoppingCartItemDTO.productLocalInfoIdStr}"/>
                        <input type="checkbox" class="chk J-CheckBoxItem" />
                            ${shoppingCartItemDTO.commodityCode}
                        <span style='font-weight: bold;'>${shoppingCartItemDTO.productName}</span>
                        <span style='font-weight: bold;'>${shoppingCartItemDTO.productBrand}</span>
                            ${shoppingCartItemDTO.productSpec}
                            ${shoppingCartItemDTO.productModel}
                            ${shoppingCartItemDTO.productVehicleBrand}
                            ${shoppingCartItemDTO.productVehicleModel}
                    </td>
                    <td>

                            <a class="J-oldPrice" style="color: #848484">${shoppingCartItemDTO.quotedPrice}</a>
                            <a class="J-newPrice yellow_color"></a>
                            <span class="save J-SaveInfo" style="display: none">
                                <div class="saveLeft"></div>
                                <div class="saveBody J-SaveValue"> </div>
                                <div class="saveRight"></div>
                            </span>

                    </td>
                    <td><input type="text" autocomplete="off" maxlength="6" class="txt J-ModifyAmount" value="<fmt:formatNumber value="${shoppingCartItemDTO.amount}" pattern="#.#"/>" style="width:50px; height:16px;"/>&nbsp;${shoppingCartItemDTO.sellUnit}</td>
                    <td class="promotions_info_td"></td>
                    <td><b class="yellow_color J-ItemTotal"><fmt:formatNumber value="${shoppingCartItemDTO.total}" pattern="#.##"/></b></td>
                    <td><a class="J-deleteShoppingCartItem blue_color">删除</a></td>
                </tr>
            </c:when>
            <c:otherwise>
                <tr class="bg tr_ban J-ItemBody"  data-shoppingcartitemid="${shoppingCartItemDTO.id}">
                    <td colspan="2" style="padding-left:20px;">
                        <a class="ban J-showAlert"></a>
                        <div class="alert" style="display: none">
                            <a class="arrowTop"></a>
                            <div class="alertAll">
                                <div class="alertLeft"></div>
                                <div class="alertBody">
                                    商品已下架
                                </div>
                                <div class="alertRight"></div>
                            </div>
                        </div>

                            ${shoppingCartItemDTO.commodityCode}
                        <span style='font-weight: bold;'>${shoppingCartItemDTO.productName}</span>
                        <span style='font-weight: bold;'>${shoppingCartItemDTO.productBrand}</span>
                            ${shoppingCartItemDTO.productSpec}
                            ${shoppingCartItemDTO.productModel}
                            ${shoppingCartItemDTO.productVehicleBrand}
                            ${shoppingCartItemDTO.productVehicleModel}
                    </td>
                    <td>商品已下架</td>
                    <td><div style="float:left; width:50px; padding-left:8px;"><fmt:formatNumber value="${shoppingCartItemDTO.amount}" pattern="#.#"/>&nbsp;${shoppingCartItemDTO.sellUnit}</div></td>
                    <td></td>
                    <td>--</td>
                    <td><a class="J-deleteShoppingCartItem blue_color">删除</a></td>
                </tr>
            </c:otherwise>
        </c:choose>
    </c:forEach>
</c:when>
<c:otherwise>
    <tr class="J-ItemHead">

        <td colspan="7" style="padding-left:10px;">
            <input type="checkbox" class="chk J-ForShop" style="float: left"/>
            <span style="float: left;font-weight: bold;"> 供应商：</span>
            <a class="blue_color J-goSupplier" style="float:left;"  data-supplierid="${supplierDTO.id} " data_supplierShopId="${supplierDTO.supplierShopId}">${supplierDTO.name}</a>
            <div style="float: left; margin-top: -2px; margin-left: 6px;">
                <a onmouseout="scorePanelHide();"
                   id="commentScoreObject${shopStatus.index}"
                   onmouseover="showSupplierCommentScore(this,
                           ${supplierDTO.totalAverageScore == null?0:supplierDTO.totalAverageScore},
                           ${supplierDTO.commentRecordCount == null?0:supplierDTO.commentRecordCount},
                           ${supplierDTO.qualityAverageScore == null?0:supplierDTO.qualityAverageScore},
                           ${supplierDTO.performanceAverageScore == null?0:supplierDTO.performanceAverageScore},
                           ${supplierDTO.speedAverageScore == null?0:supplierDTO.speedAverageScore},
                           ${supplierDTO.attitudeAverageScore == null?0:supplierDTO.attitudeAverageScore});"
                   onclick="redirectShopCommentDetail('${supplierDTO.supplierShopId}')">
                    <span class="star" style="margin: 0 3px 4px 0; vertical-align: middle;"></span>
                    <c:set var="totalAverageScore" value="${supplierDTO.totalAverageScore == null?0:supplierDTO.totalAverageScore}"/>
                    <b class="color_yellow"><span>${totalAverageScore>0.001 ?totalAverageScore:"暂无"}${totalAverageScore>0.001 ?"分":""}</span></b>
                </a>
            </div>
            <span style="float: left;font-weight: bold;margin-left:10px"> 联系方式：</span> <span style="float: left;">${supplierDTO.contact} ${supplierDTO.mobile}</span>&nbsp;
            <a class="J_QQ" data_qq="${supplierDTO.qqArray}"></a>
            <%--&nbsp;&nbsp;联系人：${supplierDTO.contact}--%>
            <%--&nbsp;&nbsp;联系方式：${supplierDTO.mobile}--%>
    </tr>
    <c:forEach items="${shoppingCartDetailMap.value}" var="shoppingCartItemDTO">
        <c:choose>
            <c:when test="${shoppingCartItemDTO.salesStatus eq 'InSales'}">
                <tr class="bg J-ItemBody item" data-shoppingcartitemid="${shoppingCartItemDTO.id}">
                    <td style="padding-left:20px;">
                        <input type="hidden" class="productId" value="${shoppingCartItemDTO.productLocalInfoIdStr}"/>
                        <input type="checkbox" class="chk J-CheckBoxItem" />
                    </td>
                    <td>
                        <div style="margin: 10px 0;float: left">
                            <a style="width: 60px;height: 60px;cursor: pointer" class="J_open_productHistory">
                                <img src="${shoppingCartItemDTO.imageCenterDTO.productListSmallImageDetailDTO.imageURL}" style="width: 60px;height: 60px">
                            </a>
                        </div>
                        <input type="hidden" name="paramShopId" value="${shoppingCartItemDTO.supplierShopId}" />
                        <input type="hidden" name="productLocalId" value="${shoppingCartItemDTO.productLocalInfoId}" />
                        <div style="margin: 10px 5px;float: left;width:320px">
                            <a class="blue_color J_open_productHistory">
                                    ${shoppingCartItemDTO.commodityCode}
                                    ${shoppingCartItemDTO.productName}
                                    ${shoppingCartItemDTO.productBrand}
                                    ${shoppingCartItemDTO.productSpec}
                                    ${shoppingCartItemDTO.productModel}
                                    ${shoppingCartItemDTO.productVehicleBrand}
                                    ${shoppingCartItemDTO.productVehicleModel}
                            </a>
                        </div>
                    </td>
                    <%--<td colspan="2" style="padding-left:20px;">--%>
                        <%--<input type="hidden" class="productId" value="${shoppingCartItemDTO.productLocalInfoIdStr}"/>--%>
                        <%--<input type="checkbox" class="chk J-CheckBoxItem" />--%>
                            <%--${shoppingCartItemDTO.commodityCode}--%>
                        <%--<span style='font-weight: bold;'>${shoppingCartItemDTO.productName}</span>--%>
                        <%--<span style='font-weight: bold;'>${shoppingCartItemDTO.productBrand}</span>--%>
                            <%--${shoppingCartItemDTO.productSpec}--%>
                            <%--${shoppingCartItemDTO.productModel}--%>
                            <%--${shoppingCartItemDTO.productVehicleBrand}--%>
                            <%--${shoppingCartItemDTO.productVehicleModel}--%>
                    <%--</td>--%>
                    <td>
                          <a class="J-oldPrice" style="color: #848484">${shoppingCartItemDTO.quotedPrice}</a>
                            <a class="J-newPrice yellow_color"></a>
                              <input type="hidden" class="purchasePrice" value="${shoppingCartItemDTO.quotedPrice}" />
                              <input type="hidden" class="bargainSaveValue" value="0"/>
                            <span class="save J-SaveInfo" style="display: none">
                                <div class="saveLeft"></div>
                                <div class="saveBody J-SaveValue"> </div>
                                <div class="saveRight"></div>
                            </span>
                    </td>
                    <td><input type="text" autocomplete="off" maxlength="6" class="txt J-ModifyAmount" value="<fmt:formatNumber value="${shoppingCartItemDTO.amount}" pattern="#.#"/>" style="width:50px; height:16px;"/>&nbsp;${shoppingCartItemDTO.sellUnit}</td>
                    <td class="promotions_info_td"></td>
                    <td><b class="yellow_color J-ItemTotal"><fmt:formatNumber value="${shoppingCartItemDTO.total}" pattern="#.##"/></b></td>
                    <td><a class="J-deleteShoppingCartItem blue_color">删除</a></td>
                </tr>
            </c:when>
            <c:otherwise>
                <tr class="bg tr_ban J-ItemBody"  data-shoppingcartitemid="${shoppingCartItemDTO.id}">
                    <td style="padding-left:20px;">
                        <input type="hidden" class="productId" value="${shoppingCartItemDTO.productLocalInfoIdStr}"/>
                        <a class="ban J-showAlert" style="float: left"></a>
                        <div class="alert" style="margin-top:12px;display: none">
                            <a class="arrowTop"></a>
                            <div class="alertAll">
                                <div class="alertLeft"></div>
                                <div class="alertBody">
                                    商品已下架
                                </div>
                                <div class="alertRight"></div>
                            </div>
                        </div>
                    </td>
                    <td>
                        <div style="margin: 10px 0;float: left">
                            <a style="width: 60px;height: 60px;cursor: pointer" class="J_open_productHistory">
                                <img src="${shoppingCartItemDTO.imageCenterDTO.productListSmallImageDetailDTO.imageURL}" style="width: 60px;height: 60px">
                            </a>
                        </div>
                        <div style="margin: 10px 5px;float: left;width:320px">
                            <a style="cursor: pointer" class="gay_color J_open_productHistory">
                                    ${shoppingCartItemDTO.commodityCode}
                                    ${shoppingCartItemDTO.productName}
                                    ${shoppingCartItemDTO.productBrand}
                                    ${shoppingCartItemDTO.productSpec}
                                    ${shoppingCartItemDTO.productModel}
                                    ${shoppingCartItemDTO.productVehicleBrand}
                                    ${shoppingCartItemDTO.productVehicleModel}
                            </a>
                        </div>
                    </td>
                    <td>商品已下架</td>
                    <td><div style="float:left; width:50px; padding-left:8px;"><fmt:formatNumber value="${shoppingCartItemDTO.amount}" pattern="#.#"/>&nbsp;${shoppingCartItemDTO.sellUnit}</div></td>
                    <td></td>
                    <td>--</td>
                    <td><a class="J-deleteShoppingCartItem blue_color">删除</a></td>
                </tr>
            </c:otherwise>
        </c:choose>
    </c:forEach>
</c:otherwise>
</c:choose>
</table>
</td>
</tr>
<c:if test="${!shopStatus.last}"><tr class="border J-Separator"><td colspan="8"></td></tr></c:if>
</c:forEach>
</c:if>
<c:if test="${empty shoppingCartDTO.shoppingCartDetailMap}">
    <tr class="bg">
        <td colspan="7" style=" text-align:center;">购物车没有添加任何商品</td>
    </tr>
</c:if>
</table>
</div>
<div class="cartBottom"></div>
<div style="clear: both;height: 50px"></div>
<input id="promotionsTotal" type="hidden" />
<c:if test="${not empty shoppingCartDTO.shoppingCartDetailMap}">
    <div class="order" style="position:fixed;bottom:0px;" id="bottomFloatBar">
        <label for="floatBarSelectAll">
            <input id="floatBarSelectAll" type="checkbox" class="chk" />
            <a class="blue_color">全选</a>
        </label>&nbsp;
        <a class="blue_color J-DeleteMultipleShoppingCartItem">批量删除</a>&nbsp;
        <a class="blue_color J-ClearInvalidItem">清除失效商品</a>
        <div style="float:right;">
            <a class="btnOrder J-CreatePurchaseOrder"></a>
        </div>
        <div class="priceTotal" style="padding:6px;">
            商品价格总计：<span class="yellow_color">¥<b class="J-AllTotal"><fmt:formatNumber value="${shoppingCartDTO.total}" pattern="#.##"/></b></span>
            <span class="green_color" id="promotionsTotalDiv" style="display:none;">（已优惠¥<span id="promotionsTotalSpan"></span>）</span>
        </div>


    </div>
</c:if>
</div>

</div>
<div id="mask" style="display:block;position: absolute;">
</div>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>