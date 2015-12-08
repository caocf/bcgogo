<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<%@ taglib prefix="jUtil" uri="http://www.bcgogo.com/taglibs/tags" %>
<%@ taglib prefix="numberUtil" uri="http://www.bcgogo.com/taglibs/tags" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>求购详情</title>
<%@include file="/WEB-INF/views/header_script.jsp" %>
<link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/addCheck<%=ConfigController.getBuildVersion()%>.css">
<style type="text/css">
    #otherBusinessChanceTable tr td {
        padding: 0 0 0 10px;
    }
    .other-business-chance-div,.other-business-chance-div .content {
        width: 794px;
    }
    .accessoriesLeft .content {
        color:#000000;
    }
    .hover_yellow {
        display: inline-block;
        width:590px;
    }
</style>
<script type="text/javascript" src="js/page/txn/addNewProduct<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/page/autoaccessoryonline/quotedPreBuyOrder<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/messagePrompt<%=ConfigController.getBuildVersion()%>.js"></script>

<bcgogo:permissionParam permissions="WEB.AUTOACCESSORYONLINE.ADD_QUOTEDPREBUYORDER">
    <c:set var="addQuotedPreBuyOrderPerm" value="${WEB_AUTOACCESSORYONLINE_ADD_QUOTEDPREBUYORDER}"/>
</bcgogo:permissionParam>
<script type="text/javascript">
    defaultStorage.setItem(storageKey.MenuUid,"WEB_AUTOACCESSORYONLINE_BUYING_INFORMATION");

    $().ready(function(){
        function _saveViewedBusinessChance(viewedPreBuyOrderItemId){
            if(G.isEmpty(viewedPreBuyOrderItemId)){
                return;
            }
            APP_BCGOGO.Net.asyncPost({
                url: "autoAccessoryOnline.do?method=saveViewedBusinessChance",
                dataType: "json",
                data: {
                    viewedPreBuyOrderItemId: viewedPreBuyOrderItemId
                }
            });
        }
        _saveViewedBusinessChance($("#preBuyOrderItemId").val());
        APP_BCGOGO.Net.asyncAjax({
            url: "autoAccessoryOnline.do?method=getViewedBusinessChance",
            type: "POST",
            cache: false,
            dataType: "json",
            success: function (result) {
                if(G.isEmpty(result)||!result.success){
                    return;
                }
                var preBuyOrderItems=result.data;
                if(G.isEmpty(preBuyOrderItems)){
                    $('#viewedBusinessChance').append("<li>您暂无最近浏览的商机</li>");
                    return;
                }
                var trStr='';
                for(var i=0;i<preBuyOrderItems.length;i++){
                    var preBuyOrderItem=preBuyOrderItems[i];
                    var commodityCode=preBuyOrderItem.commodityCode;
                    var productName=preBuyOrderItem.productName;
                    var brand=preBuyOrderItem.brand;
                    var model=preBuyOrderItem.model;
                    var spec=preBuyOrderItem.spec;
                    var vehicleBrand=preBuyOrderItem.vehicleBrand;
                    var vehicleModel=preBuyOrderItem.vehicleModel;
                    var productInfoStr = "";
                    if(!G.isEmpty(productName)){
                        productInfoStr += productName + " ";
                    }
                    if(!G.isEmpty(brand)){
                        productInfoStr += brand + " ";
                    }
                    if (!G.isEmpty(model) &&!G.isEmpty(spec)) {
                        productInfoStr += model + "/" + spec;
                    } else if (!G.isEmpty(model)) {
                        productInfoStr += model;
                    } else if (!G.isEmpty(spec)) {
                        productInfoStr += spec;
                    }
                    if(!G.isEmpty(vehicleBrand)){
                        productInfoStr += vehicleBrand + " ";
                    }
                    if(!G.isEmpty(vehicleModel)){
                        productInfoStr += vehicleModel;
                    }
                    var businessChanceTypeStr = preBuyOrderItem.businessChanceTypeStr;
                    var businessChanceType = preBuyOrderItem.businessChanceType;
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
                    var itemId=preBuyOrderItem.idStr;
                    trStr+='<li class="p-info-detail text-overflow" onclick="showBuyInformationDetail(\''+itemId+'\')" title="'+productInfoStr+'">[<a class="'+changeClass+'">'+businessChanceTypeStr+'</a>]'+productInfoStr+'</li>';
                }
                $('#viewedBusinessChance').append(trStr);
            },
            error:function(){
                nsDialog.jAlert("网络异常。");
            }
        });
        var data = {
            startPageNo:1,
            pageSize:5,
            noneShopId:${preBuyOrderDTO.shopId}
        };
        APP_BCGOGO.Net.asyncAjax({
            url: "preBuyOrder.do?method=getOtherShopPreBuyOrders",
            type: "POST",
            cache: false,
            dataType: "json",
            data:data,
            success: function (result) {
                initOtherShopPreBuyOrderInfo(result);
                initPage(result, "otherShopPreBuyOrderInfo", "preBuyOrder.do?method=getOtherShopPreBuyOrders", '', "initOtherShopPreBuyOrderInfo", '', '', data, '');
            },
            error:function(){
                nsDialog.jAlert("网络异常。");
            }
        });
        var loop;
        function autoScroll(){
            var $scrollItem=$('.JBusinessChance .scrollItem');
            var intervalHeight=-30;
            $scrollItem.animate({
                marginTop:intervalHeight+"px"
            },1000,function(){
//                $(this).css({marginTop:"0px"});
                var $tr = $(this).find("tr:first");
                $tr.remove();
                $(this).find("table").append($tr);
                $(this).css({marginTop:"0px"});
//                .find("a:first").appendTo(this);
            });
        }
        var itemIndex=G.rounding($("#otherBusinessChanceTable tr:last").attr("itemIndex"));
        if(itemIndex>=5){
            $('.JBusinessChance').height(150);
            loop = setInterval(autoScroll,2000);
        }else{
            $('.JBusinessChance').height(30*(itemIndex+1));
        }

        $(".hover_yellow").live("click",function(){
            var itemId = $(this).attr("itemid");
            if(itemId) {
                window.open("preBuyOrder.do?method=showBuyInformationDetailByPreBuyOrderItemId&preBuyOrderItemId=" + itemId,"_blank");
            }
        });

        $(".JBusinessChance").hover(function(){
            clearInterval(loop);
        },function(){
            if(itemIndex>=5){
                loop = setInterval(autoScroll,2000);
            }
        });
    });
    function initOtherShopPreBuyOrderInfo(data) {
        $("#otherShopPrebuyOrderInfo tr").remove();
        var tr = '';
        if(data == null || data.results == null || data.results.length == 0) {
            tr = '<tr><td>暂无其他店铺商机信息！</td></tr>';
            $("#otherShopPrebuyOrderInfo").append($(tr));
            return;
        }
        for(var i = 0; i < data.results.length; i++) {
            var otherShopPreBuyOrder = data.results[i];
            var businessChanceTypeStr = '';
            if('Normal' == otherShopPreBuyOrder.businessChanceType) {
                businessChanceTypeStr = '求购';
            } else if('Lack' == otherShopPreBuyOrder.businessChanceType) {
                businessChanceTypeStr = '缺料';
            } else if('SellWell' == otherShopPreBuyOrder.businessChanceType) {
                businessChanceTypeStr = '畅销';
            }
            tr += '<tr><td style="width: 70%"><span class="hover_yellow text-overflow" itemId="' + otherShopPreBuyOrder.itemDTO.idStr + '" title="' + (otherShopPreBuyOrder.itemDTO.commodityCode == null ? "" : otherShopPreBuyOrder.itemDTO.commodityCode) + '&nbsp;' + otherShopPreBuyOrder.itemDTO.productName + '&nbsp;' + otherShopPreBuyOrder.itemDTO.brand + '&nbsp;' + otherShopPreBuyOrder.itemDTO.spec + '&nbsp;' + otherShopPreBuyOrder.itemDTO.model + '&nbsp;' + otherShopPreBuyOrder.itemDTO.vehicleBrand + '&nbsp;' + otherShopPreBuyOrder.itemDTO.vehicleModel + '&nbsp;';
            if('SellWell' != otherShopPreBuyOrder.businessChanceType) {
                tr += otherShopPreBuyOrder.itemDTO.amount + '&nbsp;' + otherShopPreBuyOrder.itemDTO.unit;
            } else {
                tr += '上周销量：' + otherShopPreBuyOrder.itemDTO.fuzzyAmountStr + otherShopPreBuyOrder.itemDTO.unit;
            }
            tr += '">[<a class="yellow_color">' + businessChanceTypeStr + '</a>] （' + otherShopPreBuyOrder.vestDateStr + '发布）';
            tr += '<span id="itemDTOs' + i + '.commodityCode">' + (otherShopPreBuyOrder.itemDTO.commodityCode == null ? "" : otherShopPreBuyOrder.itemDTO.commodityCode) + '</span>&nbsp;<span id="itemDTOs' + i + '.productName">' + otherShopPreBuyOrder.itemDTO.productName + '</span>&nbsp;<span id="itemDTOs' + i + '.brand">' + otherShopPreBuyOrder.itemDTO.brand +
                    '</span>&nbsp;<span id="itemDTOs' + i + '.spec">' + otherShopPreBuyOrder.itemDTO.spec + '</span>&nbsp;<span id="itemDTOs' + i + '.model">' +  otherShopPreBuyOrder.itemDTO.model + '</span>&nbsp;<span id="itemDTOs' + i + '.vehicleBrand">' +  otherShopPreBuyOrder.itemDTO.vehicleBrand + '</span>&nbsp;<span id="itemDTOs' + i + '.vehicleModel">' +  otherShopPreBuyOrder.itemDTO.vehicleModel + '</span>&nbsp;'
            if('SellWell' != otherShopPreBuyOrder.businessChanceType) {
                tr += '<a class="yellow_color"><strong>' + otherShopPreBuyOrder.itemDTO.amount + '</strong></a>' + otherShopPreBuyOrder.itemDTO.unit;
            } else {
                tr += '上周销量：<a class="yellow_color"><strong>' + otherShopPreBuyOrder.itemDTO.fuzzyAmountStr + '</strong></a>' + otherShopPreBuyOrder.itemDTO.unit
            }
            tr += '</span></td><td style="width:100px;">';
            if('SellWell' != otherShopPreBuyOrder.businessChanceType) {
                tr += '（还剩' + otherShopPreBuyOrder.endDateCount + '天有效）';
            }
            tr += '</td>';
            tr += '<td><div class="accessories-btn J_QuotedStepFirst" data-prebuyorderitemid="' + otherShopPreBuyOrder.itemDTO.idStr + '">我要报价</div></td></tr>'
        }
        $("#otherShopPrebuyOrderInfo").append($(tr));
    }
</script>
</head>
<body class="bodyMain">
<%@ include file="/WEB-INF/views/header_html.jsp" %>
<%@ include file="/common/messagePrompt.jsp" %>
<div class="i_main clear">
<div class="mainTitles parts-quoted-price">
    <div class="titleWords">商品求购详情</div>
    <div class="cl"></div>
</div>
<div class="titBody">
<input type="hidden" id="shopId" value="${sessionScope.shopId}"/>
<div class="accessories-container">
    <div class="product-details">
        <div class="date-relative">
            <div class="date-absolute">
                <c:if test="${preBuyOrderDTO.statusStr =='过期'}" var="invalid">
                    <h2>商机已过期</h2>
                </c:if>
                <c:if test="${!invalid}">
                    距信息失效仅剩 ${preBuyOrderDTO.endDateCount}天
                    <br />
                </c:if>
                截止：${preBuyOrderDTO.endDateStr}
                <br />
                发布日期：${preBuyOrderDTO.vestDateStr} </div>
        </div>
        <div class="details-pic">
            <img src="${currentPreBuyOrderItemDTO.imageCenterDTO.productInfoBigImageDetailDTOs[0].imageURL}"/>
        </div>
        <div class="details-right">
            <div class="font14"><strong>[<a class="yellow_color">${preBuyOrderDTO.businessChanceType.name}</a>] ${currentPreBuyOrderItemDTO.productName}
                ${currentPreBuyOrderItemDTO.brand}</strong></div>
            <div class="product-line">
                <div class="line-01">商品编号：</div>
                <div class="line-02"><span id="itemDTOs.commodityCode">${currentPreBuyOrderItemDTO.commodityCode}</span>&nbsp;</div>
            </div>
            <div class="product-line">
                <div class="line-01">品名：</div>
                <div class="line-02"><span id="itemDTOs.productName">${currentPreBuyOrderItemDTO.productName}</span>&nbsp;</div>
            </div>
            <div class="product-line">
                <div class="line-01">品牌：</div>
                <div class="line-02"><span id="itemDTOs.brand">${currentPreBuyOrderItemDTO.brand}</span>&nbsp;</div>
            </div>
            <div class="product-line">
                <div class="line-01">规格/型号：</div>
                <div class="line-02"><span id="itemDTOs.spec">${currentPreBuyOrderItemDTO.spec}</span>&nbsp;<span id="itemDTOs.model">${currentPreBuyOrderItemDTO.model}</span></div>
            </div>
            <div class="product-line">
                <div class="line-01">适合车型：</div>
                <div class="line-02"><span id="itemDTOs.vehicleBrand">${currentPreBuyOrderItemDTO.vehicleBrand}</span>&nbsp;<span id="itemDTOs.vehicleModel">${currentPreBuyOrderItemDTO.vehicleModel}</span></div>
            </div>
            <div class="product-line">
                <c:if test="${'Normal' eq preBuyOrderDTO.businessChanceType}">
                    <div class="line-01">求购数量：</div>
                    <div class="line-02">${currentPreBuyOrderItemDTO.amount}&nbsp;${currentPreBuyOrderItemDTO.unit}</div>
                </c:if>
                <c:if test="${'Lack' eq preBuyOrderDTO.businessChanceType}">
                    <div class="line-01">缺料数量：</div>
                    <div class="line-02">${currentPreBuyOrderItemDTO.amount}&nbsp;${currentPreBuyOrderItemDTO.unit}</div>
                </c:if>
                <c:if test="${'SellWell' eq preBuyOrderDTO.businessChanceType}">
                    <div class="line-01">销售数量：</div>
                    <div class="line-02">${currentPreBuyOrderItemDTO.fuzzyAmountStr}&nbsp;${currentPreBuyOrderItemDTO.unit}</div>
                </c:if>

            </div>
            <div class="product-line">
                <div class="line-01">描述：</div>
                <div class="line-02">${empty currentPreBuyOrderItemDTO.memo?"无":currentPreBuyOrderItemDTO.memo}</div>
                <div class="i_height clear"></div>
            </div>
            <div class="clear"></div>
            <div class="black-color">浏览量 <a class="yellow_color"><strong>${numberUtil:roundInt(currentPreBuyOrderItemDTO.viewedCount)}</strong></a> 次 | 已有 <a class="yellow_color"> <strong>${empty currentPreBuyOrderItemDTO.quotedCount?"0":currentPreBuyOrderItemDTO.quotedCount}</strong></a> 家卖家参与报价</div>
            <c:if test="${empty currentPreBuyOrderItemDTO.myQuotedPreBuyOrderItemDTO}" var="noQuote">
                <c:if test="${preBuyOrderDTO.statusStr !='过期'}">
                    <input name="" type="button" class="offer-btn J_QuotedStepFirst" data-shopId="${currentPreBuyOrderItemDTO.shopIdStr}" data-prebuyorderitemid="${currentPreBuyOrderItemDTO.idStr}"/>
                </c:if>
            </c:if>
            <c:if test="${!noQuote}">
                <input name="" type="button" class="offer-btn2" />
                <div class="i_height clear"></div>
                <table width="96%" border="0" cellspacing="0" class="Offer-table" style="margin-left: 0px;">
                    <tr>
                        <td>报价商品：<a href="shopProductDetail.do?method=toShopProductDetail&paramShopId=${currentPreBuyOrderItemDTO.myQuotedPreBuyOrderItemDTO.shopIdStr}&productLocalId=${currentPreBuyOrderItemDTO.myQuotedPreBuyOrderItemDTO.productIdStr}" class="blue_color">${currentPreBuyOrderItemDTO.myQuotedPreBuyOrderItemDTO.commodityCode}&nbsp;${currentPreBuyOrderItemDTO.myQuotedPreBuyOrderItemDTO.productName}&nbsp;${currentPreBuyOrderItemDTO.myQuotedPreBuyOrderItemDTO.brand}&nbsp;${currentPreBuyOrderItemDTO.myQuotedPreBuyOrderItemDTO.spec}&nbsp;${currentPreBuyOrderItemDTO.myQuotedPreBuyOrderItemDTO.model}&nbsp;${currentPreBuyOrderItemDTO.myQuotedPreBuyOrderItemDTO.vehicleBrand}&nbsp;${currentPreBuyOrderItemDTO.myQuotedPreBuyOrderItemDTO.vehicleModel}</a>  </td>
                    </tr>
                    <tr>
                        <td>报价价格：<strong class="yellow_color">${currentPreBuyOrderItemDTO.myQuotedPreBuyOrderItemDTO.price}</strong>元/${currentPreBuyOrderItemDTO.myQuotedPreBuyOrderItemDTO.unit} （<c:if test="${currentPreBuyOrderItemDTO.myQuotedPreBuyOrderItemDTO.includingTax == 'TRUE'}">含税、</c:if><c:if test="${not empty currentPreBuyOrderItemDTO.myQuotedPreBuyOrderItemDTO.shippingMethodStr}">${currentPreBuyOrderItemDTO.myQuotedPreBuyOrderItemDTO.shippingMethodStr}、</c:if>下单后<c:if test="${not empty currentPreBuyOrderItemDTO.myQuotedPreBuyOrderItemDTO.arrivalTime}">${currentPreBuyOrderItemDTO.myQuotedPreBuyOrderItemDTO.arrivalTime}</c:if>天到货）</td>
                    </tr>
                </table>
                <div class="clear"></div>
            </c:if>

        </div>
        <div class="clear"></div>
    </div>
    <div class="accessoriesLeft other-business-chance-div">
        <div class="title"><a href="preBuyOrder.do?method=preBuyInformation&shopId=${preBuyOrderDTO.shopId}">查看所有></a> 此买家其他商机  <span class="font12-normal">该买家共有</span> ${empty otherPrebuyOrderInfo.countPreBuyNormal ? 0 : otherPrebuyOrderInfo.countPreBuyNormal} <span class="font12-normal">条求购商机 |</span> ${empty otherPrebuyOrderInfo.countPreBuyLack ? 0 : otherPrebuyOrderInfo.countPreBuyLack} <span class="font12-normal"> 条缺料商机 | </span> ${empty otherPrebuyOrderInfo.countPreBuySellWell ? 0 : otherPrebuyOrderInfo.countPreBuySellWell} <span class="font12-normal">条畅销商机！</span></div>
        <div class="content JBusinessChance">
            <div class="scrollItem">
                <table width="100%" border="0" class="accessories-table" id="otherBusinessChanceTable">
                    <c:if test="${empty otherPrebuyOrderInfo.preBuyOrderDTOList}" var="emptyList">
                        <tr itemIndex="0">
                            <td>
                                暂无该买家其他商机信息！
                            </td>
                        </tr>
                    </c:if>
                    <c:if test="${!emptyList}">
                        <c:forEach items="${otherPrebuyOrderInfo.preBuyOrderDTOList}" var="preBuyOrderDTO" varStatus="status">
                            <tr itemIndex="${status.index}">
                                <td style="width: 80%"><span class="hover_yellow text-overflow" itemId="${preBuyOrderDTO.itemDTO.idStr}" title="${preBuyOrderDTO.itemDTO.commodityCode == null ? '' : preBuyOrderDTO.itemDTO.commodityCode} ${preBuyOrderDTO.itemDTO.productName} ${preBuyOrderDTO.itemDTO.brand} ${preBuyOrderDTO.itemDTO.spec} ${preBuyOrderDTO.itemDTO.model} ${preBuyOrderDTO.itemDTO.vehicleBrand} ${preBuyOrderDTO.itemDTO.vehicleModel} <c:if test="${!(preBuyOrderDTO.businessChanceType eq 'SellWell')}" var="status1">${preBuyOrderDTO.itemDTO.amount} ${preBuyOrderDTO.itemDTO.unit}</c:if><c:if test="${!status1}">上周销量 ${preBuyOrderDTO.itemDTO.fuzzyAmountStr} ${preBuyOrderDTO.itemDTO.unit}</c:if>">[<span class="yellow_color">${preBuyOrderDTO.businessChanceType.name} </span>] （${preBuyOrderDTO.vestDateStr}发布）
                                 <span id="itemDTOs${status.index}.commodityCode">${preBuyOrderDTO.itemDTO.commodityCode == null ? "" : preBuyOrderDTO.itemDTO.commodityCode}</span>
                                 <span id="itemDTOs${status.index}.productName">${preBuyOrderDTO.itemDTO.productName}</span>
                                 <span id="itemDTOs${status.index}.brand">${preBuyOrderDTO.itemDTO.brand}</span>
                                 <span id="itemDTOs${status.index}.spec">${preBuyOrderDTO.itemDTO.spec}</span>
                                 <span id="itemDTOs${status.index}.model">${preBuyOrderDTO.itemDTO.model}</span>
                                 <span id="itemDTOs${status.index}.vehicleBrand">${preBuyOrderDTO.itemDTO.vehicleBrand}</span>
                                 <span id="itemDTOs${status.index}.vehicleModel">${preBuyOrderDTO.itemDTO.vehicleModel}</span>
                             <c:if test="${!(preBuyOrderDTO.businessChanceType eq 'SellWell')}" var="status2">
                                  <span class="yellow_color"><strong>${preBuyOrderDTO.itemDTO.amount}</strong></span> ${preBuyOrderDTO.itemDTO.unit} </span>
                                    </c:if>
                                    <c:if test="${!status2}">
                                        上周销量 <span class="yellow_color"><strong>${preBuyOrderDTO.itemDTO.fuzzyAmountStr}</strong></span> ${preBuyOrderDTO.itemDTO.unit}
                                    </c:if>
                                </td>
                                <td style="width:100px;"><c:if test="${!(preBuyOrderDTO.businessChanceType eq 'SellWell')}"><span style="margin-left: -40px;">（还剩${preBuyOrderDTO.endDateCount}天有效）</span></c:if></td>
                                <td>
                                    <c:if test="${preBuyOrderDTO.itemDTO.myQuoted}">
                                        <div class="accessories-btn" style="background: #ddd;margin-left: -40px;">我已报价</div>
                                    </c:if>
                                    <c:if test="${!preBuyOrderDTO.itemDTO.myQuoted}">
                                        <div class="accessories-btn J_QuotedStepFirst" data-prebuyorderitemid="${preBuyOrderDTO.itemDTO.idStr}" style="margin-left: -40px;">我要报价</div>
                                    </c:if>
                                </td>
                            </tr>
                        </c:forEach>
                    </c:if>
                </table>
            </div>
            <div class="clear"></div>
        </div>
        <div class="clear"></div>
    </div>
    <div class="accessoriesLeft">
        <div class="title">更多店铺商机</div>
        <div class="content">
            <table width="100%" border="0" class="accessories-table" id="otherShopPrebuyOrderInfo">
            </table>
            <bcgogo:ajaxPaging url="preBuyOrder.do?method=getOtherShopPreBuyOrders" dynamical="otherShopPreBuyOrderInfo"
                               data='{startPageNo:1,pageSize:5, noneShopId:${preBuyOrderDTO.shopId}}' postFn="initOtherShopPreBuyOrderInfo" display="none"/>
            <div class="clear"></div>
        </div>
        <div class="clear"></div>
    </div>
</div>
<div class="accessories-right">
    <div class="accessoriesRight">
        <div class="title">买家档案</div>
        <div class="clear"></div>
        <div class="content" style="padding-left:10px;">
            <a href="shopMsgDetail.do?method=renderShopMsgDetail&paramShopId=${preBuyOrderDTO.shopId}" class="blue_col"><strong class="font14">${customerDTO.name}</strong></a>
            <div class="product-line" style="margin-top: 5px;">
                <div class="line-01">所在地区：</div>
                <div class="line-02">${customerDTO.areaInfo}</div>
            </div>
            <div class="product-line">
                <div class="line-01">联 系 人：</div>
                <div class="line-02">${empty customerDTO.contact?"暂无信息":customerDTO.contact}</div>
            </div>
            <div class="product-line">
                <div class="line-01">手    机：</div>
                <div class="line-02">${empty customerDTO.mobile?"暂无信息":customerDTO.mobile}</div>
            </div>
            <div class="product-line">
                服务范围：
                ${empty customerDTO.serviceCategoryRelationContent ? "暂无信息" : customerDTO.serviceCategoryRelationContent}
            </div>
            <div class="product-line">
                主营车型：
                <span title="${empty customerDTO.vehicleModelContent?"暂无信息":(customerDTO.vehicleModelContent)}">${empty customerDTO.vehicleModelContent?"暂无信息":jUtil:getShortStr(customerDTO.vehicleModelContent,41)}</span>
            </div>
            <div class="product-line">
                经营产品：
                <span title="${empty customerDTO.businessScopeStr?"暂无信息":(customerDTO.businessScopeStr)}">${empty customerDTO.businessScopeStr?"暂无信息":jUtil:getShortStr(customerDTO.businessScopeStr,41)}</span>
            </div>

            <div class="product-line">
                <div class="line-01">收藏人气：</div>
                <div class="line-02">已有 <a class="yellow_color"><strong>${customerDTO.beStored}</strong></a> 家</div>
            </div>
            <c:if test="${ pre_shop_id  != sessionScope.shopId}">
                <div id="relationOperationDiv">
                    <c:choose>
                        <c:when test="${'UN_APPLY_RELATED' eq relationMidStatus}">
                            <bcgogo:hasPermission permissions="WEB.CUSTOMER_MANAGER.CUSTOMER_APPLY_ACTION">
                                <div class="list"><a class="button_associate" id="applyCustomerRelationBtn" data-customershopid="${customerDTO.customerShopId}" style="margin-left: 50px;">申请关联</a></div>
                            </bcgogo:hasPermission>
                        </c:when>
                        <c:when test="${'RELATED' eq relationMidStatus}">
                            <input type="hidden" id="relationMidStatus" value="${relationMidStatus}"/>
                            <div class="list"><span class="button_associate" style="margin-left: 50px;">已经关联</span></div>
                        </c:when>
                        <c:when test="${'APPLY_RELATED' eq relationMidStatus}">
                            <input type="hidden" id="relationMidStatus" value="${relationMidStatus}"/>
                            <div class="list"><span class="button_associate" style="margin-left: 50px;">已提交申请</span></div>
                        </c:when>
                        <c:when test="${'BE_APPLY_RELATED' eq relationMidStatus}">
                            <div class="list"><a class="button_associate" id="acceptCustomerApplyBtn" data-inviteId="${inviteId}" style="margin-left: 50px;">同意关联</a></div>
                        </c:when>
                        <c:otherwise>
                            <input type="hidden" id="relationMidStatus" value="${relationMidStatus}"/>
                        </c:otherwise>
                    </c:choose>

                </div>
            </c:if>
            <div class="clear"></div>
        </div>
    </div>
    <div class="accessoriesRight">
        <div class="title">您最近浏览过的商机</div>
        <div class="clear"></div>
        <div class="content">
            <ul class="ul-04" id="viewedBusinessChance">
            </ul>
            <div class="clear"></div>
        </div>
    </div>
</div>
<div class="height"></div>
<%--<input type="hidden" id="currentPreBuyOrderId" value="${preBuyOrderDTO.idStr}">--%>
<input type="hidden" id="currentPreBuyOrderItemId">
<input type="hidden" id="preBuyOrderItemId" value="${currentPreBuyOrderItemDTO.idStr}">
<%@ include file="./alert/quotedPreBuyOrderAlert.jsp" %>
<!----------------------------页脚----------------------------------->
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</div>
</div>
<div id="mask"  style="display:block;position: absolute;"> </div>
<iframe id="iframe_PopupBox" style="position:absolute;z-index:5; left:400px; top:400px; display:none;" allowtransparency="true" width="630px" height="450px" frameborder="0" src=""></iframe>
</body>
</html>
