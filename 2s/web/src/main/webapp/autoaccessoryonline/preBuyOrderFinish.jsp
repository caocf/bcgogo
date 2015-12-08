<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>我的求购</title>
    <%@include file="/WEB-INF/views/header_script.jsp" %>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
    <script type="text/javascript" src="js/messagePrompt<%=ConfigController.getBuildVersion()%>.js"></script>

    <script type="text/javascript">
        defaultStorage.setItem(storageKey.MenuUid, "WEB_AUTOACCESSORYONLINE_MY_PREBUYORDER");
        defaultStorage.setItem(storageKey.MenuCurrentItem,"详情");
        $(document).ready(function() {
            $(".J_toggle_down").toggle(
                    function () {
                        $(".J_toggle_up").click();
                        $(this).removeClass("J_toggle_down").addClass("J_toggle_up");
                        $("#quoted_" + $(this).attr("data-index")).show();
                    },
                    function () {
                        $("#quoted_" + $(this).attr("data-index")).hide();
                        $(this).removeClass("J_toggle_up").addClass("J_toggle_down");
                    }
            );
        });

    </script>
</head>
<body>
<%@ include file="/WEB-INF/views/header_html.jsp" %>
<%@ include file="/common/messagePrompt.jsp" %>
<div class="i_main clear">
    <div class="clear i_height"></div>
    <div class="titBody">
        <jsp:include page="supplyCenterLeftNavi.jsp">
            <jsp:param name="currPage" value="preBuyOrderManage"/>
        </jsp:include>

        <div class="bodyLeft">
            <div class="cuSearch">
                <div class="lineTitle">
                    求购详细信息  <a class="blue_color" target="_blank" href="preBuyOrder.do?method=showSupplierOtherQuotedItems&quotedPreBuyOrderItemId=10000010277267624">
                    （此卖家还给其他3种商品报价了）
                </a>
                </div>
                <div class="cartBody lineBody">
                    <div class="divTit" style="width: 450px">
                        <b>求购标题：</b>
                        <span>${preBuyOrderDTO.title}</span>
                    </div>
                    <div class="divTit">
                        <b>发布时间：</b>
                        <span>${preBuyOrderDTO.vestDateStr}</span><span style="color:#999999">（${preBuyOrderDTO.endDateStr}截止）</span>
                    </div>
                    <div class="divTit">
                        <b>发布人：</b>
                        <span>${preBuyOrderDTO.editor}</span>
                    </div>
                    <div class="clear i_height"></div>
                    <table class="tab_cuSearch tabSales" cellpadding="0" cellspacing="0">
                        <col width="70">
                        <col>
                        <col width="100">
                        <col width="100">
                        <col width="65">
                        <tr class="titleBg">
                            <td style="padding-left:10px;">序号</td>
                            <td>求购商品信息</td>
                            <td>求购量</td>
                            <td>报价次数</td>
                            <td>操作</td>
                        </tr>
                        <tr class="space"><td colspan="5"></td></tr>
                        <c:forEach items="${preBuyOrderDTO.itemDTOs}" var="itemDTO" varStatus="status">
                            <tr class="titBody_Bg">
                                <td style="padding-left:10px;">
                                        ${status.index+1}
                                </td>
                                <td>
                                        ${itemDTO.commodityCode}
                                        ${itemDTO.productName}
                                        ${itemDTO.brand}
                                        ${itemDTO.spec}
                                        ${itemDTO.model}
                                        ${itemDTO.vehicleBrand}
                                        ${itemDTO.vehicleModel}
                                </td>
                                <td>${itemDTO.amount}${itemDTO.unit}</td>
                                <td>${empty itemDTO.quotedCount?"暂无":itemDTO.quotedCount}</td>
                                <td><a data-index="${status.index}" class="${empty itemDTO.quotedCount?'gray_color':'blue_color J_toggle_down'}" <c:if test="${empty itemDTO.quotedCount}">disabled="disabled"</c:if>>报价详情</a></td>
                            </tr>
                            <tr class="titBody_description">
                                <td colspan="10">
                                    <div class="buying_trList buying_description">
                                        <div class="divList">
                                            <span>求购商品描述：</span>
                                            <span class="descriptionList">${empty itemDTO.memo?"无":itemDTO.memo}</span>
                                        </div>
                                    </div>
                                </td>
                            </tr>
                            <tr class="titBottom_Bg"><td colspan="5"></td></tr>
                            <c:if test="${ !empty itemDTO.quotedPreBuyOrderItemDTOList}">
                                <tr id="quoted_${status.index}" style="display: none">
                                    <td colspan="5">
                                        <div class="trList buying_trList">
                                            <c:forEach items="${itemDTO.quotedPreBuyOrderItemDTOList}" var="quotedItemDTO">
                                                <div class="divList">
                                                    <span>${quotedItemDTO.quotedDateStr}</span>
                                                    <span><a class="blue_color" onclick="window.open('shopMsgDetail.do?method=renderShopMsgDetail&paramShopId=${quotedItemDTO.shopId}');">${quotedItemDTO.shopName}</a></span>
                                                    <span>发布报价：<label class="yellow_color">${quotedItemDTO.price}</label>元/${quotedItemDTO.unit}（${quotedItemDTO.includingTax=="TRUE"?"含税、":""}<c:if test="${not empty quotedItemDTO.shippingMethod}">${quotedItemDTO.shippingMethod.name}、</c:if>下单后${quotedItemDTO.arrivalTime}天到货）</span>
                                                    <span class="buying_order" href="${quotedItemDTO.id}"><a class="blue_color" href="preBuyOrder.do?method=showSupplierQuotedItemDetailByItemId&quotedPreBuyOrderItemId=${quotedItemDTO.id}">我要下单</a></span>
                                                </div>
                                            </c:forEach>
                                        </div>
                                    </td>
                                </tr>
                            </c:if>

                        </c:forEach>
                    </table>

                    <div class="clear i_height"></div>
                    <div class="divTit button_conditon" style="float: right;">
                        <bcgogo:hasPermission permissions="WEB.AUTOACCESSORYONLINE.RELEASE_PREBUYORDER">
                            <a class="blue_color clean" style="float: right;" href="preBuyOrder.do?method=createPreBuyOrder">继续发布</a>
                        </bcgogo:hasPermission>
                        <a class="button buying_button" style="float: right;" href="preBuyOrder.do?method=preBuyOrderManage">返回我的求购</a>
                    </div>
                </div>
                <div class="lineBottom"></div>
            </div>
        </div>
        <div class="height"></div>
        <!----------------------------页脚----------------------------------->

    </div>
</div>
<div id="mask" style="display:block;position: absolute;"></div>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>