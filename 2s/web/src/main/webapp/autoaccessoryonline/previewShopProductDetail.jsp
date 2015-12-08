<%--
  Created by IntelliJ IDEA.
  User: ndong
  Date: 13-9-4
  Time: 上午11:34
  To change this template use File | Settings | File Templates.
--%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>上架预览</title>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/shopMsgDetail<%=ConfigController.getBuildVersion()%>.css"/>

    <%@include file="/WEB-INF/views/header_script.jsp" %>
    <%@include file="/WEB-INF/views/image_script.jsp" %>
    <script type="text/javascript" src="js/page/autoaccessoryonline/promotionsUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery.ztree.core-3.5.min.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery-ui/ui/jquery-ui-1.8.21.custom.min.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/spinner/ui.spinner.min.js"></script>

    <script type="text/javascript" charset="utf-8" src="js/extension/ueditor/ueditor.config<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" charset="utf-8" src="js/extension/ueditor/ueditor.all<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" charset="utf-8" src="js/extension/ueditor/ueditor.parse<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">

        uParse('#productDescription');

        $(document).ready(function(){
            $(".J_smallImageSwitch").bind("click",function(){
                var index = $(this).attr("data-index");
                $(".J_smallImageSwitch").closest("div").removeClass("actived");
                $(this).closest("div").addClass("actived");
                $(".J_bigImageSwitch").hide();
                $("#bigImageSwitch_"+index).show();
            });

            var $productSalesPromotionFloat = $(".product-sales-promotion-float");
            var $buttonPromotionDetails = $(".product-control .content-button-hover");
            $buttonPromotionDetails.bind("mouseenter", function(event){
                var position = $(this).position(),
                        height = $(this).height();
                $productSalesPromotionFloat.css({
                    top:position.top + height,
                    left:0
                }) .show();
            }).bind("mouseleave", function(event){
                        $productSalesPromotionFloat.hide();
                    });

            $(".product-details .info-quantity .info-content .content-count").spinner({
                min:0
            });

        });



        $().ready(function(){
            //初始化促销信息
            var productJson='${product}';
            var product=JSON.parse(productJson);
            var promotionsList=product.promotionsDTOs;
            if(G.isEmpty(promotionsList)){
                $(".info-promotions").hide();
            }else{
                _initProductPromotionInfo(product);
            }

        });

        function _initProductPromotionInfo(product){
            if(G.isEmpty(product)){
                return;
            }
            var promotionsList=product.promotionsDTOs;
            if(G.isEmpty(promotionsList)){
                return;
            }
            //促销标题
            var title='<span class="content-note" class="promotionTitles" >'+generatePromotionsTitle(promotionsList)+'</span>';
            $("#promotionTitle").append(title);
            //促销详细内容
            var pDetail="";
            var inSalesPrice=G.rounding(product.inSalesPrice);
            for(var i=0;i<promotionsList.length;i++){
                var promotions=promotionsList[i];
                var name=promotions.name;
                //时间
                var timeStr='活动时间截止时间：';
                var endTime=G.normalize(promotions.endTimeStr);
                timeStr+=G.isEmpty(endTime)?'不限期':endTime;
                //内容
                var content="";
                var iconName="";
                var type=promotions.type;
                if(type=="MLJ"){
                    content+=_generateMLJContent(promotions,"PRODUCT_DETIAL");
                    iconName="满立减";
                }else if(type=="MJS"){
                    content+= _generateMJSContent(promotions,"PRODUCT_DETIAL");
                    iconName="满就送";
                }else if(type=="FREE_SHIPPING"){
                    content+= _generateFreeShippingContent(promotions,"PRODUCT_DETIAL");
                    iconName="送货上门";
                    var areaDTOs=promotions.areaDTOs;
//                    var postType=promotions.postType=="UN_POST"?"不包邮地区":"包邮地区";
//                    if(!G.isEmpty(areaDTOs)){
//                        var areaArr=new Array();
//                        for(var i=0;i<areaDTOs.length;i++){
//                            areaArr.push(areaDTOs[i].name);
//                        }
//                        content+=postType+"("+areaArr.toString() +")";
//                    }
                }else if(type=="BARGAIN"){
                    iconName="特价商品";
                    content+= _generateBargainContent(promotions,inSalesPrice,"PRODUCT_DETIAL");
                }
                pDetail+='<div class="details-note-img fl">'+
                        '<img src="images/promotions/'+iconName+'.png">'+
                    '</div>';
                pDetail+='<div class="details-label fl">'+name+':</div>';
                pDetail+='<div class="details-info fl">'+content+'';
                pDetail+='<div class="info-comment">'+timeStr+'</div></div><div class="cl"></div>';

            }
            if(!G.isEmpty(pDetail)){
                $(".promotionInfo").append(pDetail);
            }
        }

    </script>

</head>

<body class="bodyMain">
<%@include file="/WEB-INF/views/header_html.jsp" %>
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
        <a href="shopMsgDetail.do?method=renderShopMsgDetail&paramShopId=${paramShopId}" target="_self">店铺介绍</a>
        <a href="shopMsgDetail.do?method=renderShopMsgDetail&paramShopId=${paramShopId}&shopMsgTabFlag=comment" target="_self">店铺评价</a>
        <a href="shopMsgDetail.do?method=renderShopMsgDetail&paramShopId=${paramShopId}&shopMsgTabFlag=productList" target="_self">商品列表</a>
        <a class="click">商品详情</a>
    </div>
</div>
<%--left content--%>
<div class="store_left">
    <div class="store_kind">
        <span class="store_title">店铺信息</span>

        <div class="shop store_list">
            <div class="title"><b>${shopDTO.name}</b></div>
            <div class="divStar">
                综合评分：<span class="total_star"></span>
                <%--style=" background-position: 0px -${supplierCommentStatDTO.totalScoreSpan}px;"></span>--%>
                <span class="yellow_color">
                    <c:choose>
                        <c:when test="${empty shopDTO.commentStatDTO}">
                            暂无分数
                        </c:when>
                        <c:otherwise>
                            <a class="picStar normal-light-star-level-${shopDTO.commentStatDTO.totalScore*2}"></a>
                            ${shopDTO.commentStatDTO.totalScore} 分
                        </c:otherwise>
                    </c:choose>
                </span>
            </div>
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
            <div class="store_connecter">入驻日期：<span>${shopDTO.registrationDateStr}</span></div>
            <div class="store_connecter">商品数量：<a class="blue_color"><b>${shopDTO.totalProductCount}</b></a>种</div>
            <div class="store_connecter">
                <div>认证情况
                    ：</div>
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

        </div>
    </div>

</div>

<div class="store_right">

    <div class="tab-content product-details preview-product">
        <div class="group-content  product-control">
            <div class="content-details">
                <div class="details-img fl">
                    <c:choose>
                        <c:when test="${empty product.imageCenterDTO.productInfoBigImageDetailDTOs}">
                            <div class="img-original">
                                <img src="${notFindImageURL_200X200}" alt=""/>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="img-original">
                                <c:forEach var="productInfoBigImageDetailDTO" items="${product.imageCenterDTO.productInfoBigImageDetailDTOs}" varStatus="status">
                                    <img src="${productInfoBigImageDetailDTO.imageURL}" style="display: ${status.index==0?'block':'none'}" alt="" class="J_bigImageSwitch" id="bigImageSwitch_${status.index}"/>
                                </c:forEach>
                            </div>
                            <div class="group-img-thumbnails">
                                <c:forEach var="productInfoSmallImageDetailDTO" items="${product.imageCenterDTO.productInfoSmallImageDetailDTOs}" varStatus="status">
                                    <div class="img-thumbnails-item fl ${status.index==0?'actived':''}">
                                        <div class="item-arrow"></div>
                                        <img src="${productInfoSmallImageDetailDTO.imageURL}" style="cursor: pointer" alt="" class="J_smallImageSwitch" data-index="${status.index}"/>
                                    </div>
                                </c:forEach>
                                <div class="cl"></div>
                            </div>
                        </c:otherwise>
                    </c:choose>

                </div>
                <div class="details-info fl">
                    <div class="info-title">${product.productInfoStr}</div>
                    <div class="info-item info-price">
                        <span class="info-label">价格：</span>
                        <span class="info-content"><span class="arialFont">&yen;</span>${product.inSalesPrice==null?0:product.inSalesPrice}</span>
                    </div>
                    <div class="info-item info-promotions">
                        <span class="info-label">促销：</span>
                        <span class="info-content">
                           <span id="promotionTitle"></span>
                            <span class="content-button-hover">店铺活动 <span style="font-size: 10px">∨</span></span>
                        </span>
                    </div>

                    <div class="info-item info-quantity">
                        <span class="info-label" style="float: left">数量：</span>
                        <span class="info-content">
                            <input class="content-count txt w100"  type="text" style="float: left"/>
                            <span class="content-additional" style="float: left;padding-left: 5px">上架量
                                <c:choose>
                                    <c:when test="${product.inSalesAmount==-1}">
                                         有货</span>
                                    </c:when>
                                    <c:otherwise>
                                ${product.inSalesAmount}${product.sellUnit}</span>
                        </c:otherwise>
                        </c:choose>
                        </span>
                    </div>
                    <div class="cl"></div>
                    <div class="info-control" style="margin-top:10px">
                        <div class="button-buy">立即购买</div>
                        <div class="button-buy-cart-added">加入购物车</div>
                    </div>

                </div><!--details-info-->
                <div class="cl"></div>

            </div><!--content-details-->
        </div><!--end group-content-->

        <div class="group-content product-sales-promotion-static info-promotions">
            <div class="content-details">
                <ul class="fl promotionInfo">

                </ul>
                <div class="cl"></div>
            </div>
        </div><!--end group-content product-sales-promotion--static-->

        <div class="group-content product-sales-promotion-float" style="display:none">
            <div class="content-details">
                <%--<div class="details-note-img fl">--%>
                    <%--<img src="../web/images/dummy/test-thumbnails-img.png" alt=""/>--%>
                <%--</div>--%>
                <ul class="fl promotionInfo"></ul>
                <div class="cl"></div>
            </div>
        </div>


        <div class="group-content product-basic">
            <div class="content-title">
                <div class="title-bg-hr"></div>
                <div class="title-label">商品基本信息</div>
            </div>
            <div class="content-details">
                <div class="details-line">
                    <span class="line-item col-1" style="overflow: hidden;text-overflow: ellipsis;white-space: nowrap;">
                        <label class="item-label product-name">品名：</label>
                        <span class="item-content" title="${product.name}">${product.name}</span>
                    </span>
                    <span class="line-item col-2">
                        <label class="item-label product-specifications">规格：</label>
                        <span class="item-content">${product.spec}</span>
                    </span>
                    <span class="line-item col-3">
                        <label class="item-label product-vehicle-type">适用车牌：</label>
                        <span class="item-content">${product.productVehicleBrand}</span>
                    </span>
                </div>
                <div class="details-line">
                    <span class="line-item col-1">
                        <label class="item-label product-brand">品牌：</label>
                        <span class="item-content">${product.brand}</span>
                    </span>
                    <span class="line-item col-2">
                        <label class="item-label product-type">型号：</label>
                        <span class="item-content">${product.model}</span>
                    </span>
                    <span class="line-item col-3">
                        <label class="item-label product-vehicle-type">适用车型：</label>
                        <span class="item-content">${product.productVehicleModel}</span>
                    </span>
                </div>
                <div class="details-line">
                    <span class="line-item col-1">
                        <label class="item-label product-categories">质保时间：</label>
                        <span class="item-content">${product.guaranteePeriod}个月</span>
                    </span>
                     <span class="line-item col-2" style="width: 520px;">
                        <label class="item-label product-categories">标准商品分类：</label>
                        <span class="item-content">${product.productCategoryInfo}</span>
                    </span>
                </div>

            </div><!--content-details-->
        </div><!--end group-content product-basic-->

        <div class="group-content product-details-info">
            <div class="content-title">
                <div class="title-bg-hr"></div>
                <div class="title-label">详细说明</div>
            </div>
            <div class="content-details">
                <div class="info-description" id="productDescription">
                    ${product.description}
                </div>
            </div>
        </div><!--end group-content product-details-info-->

    </div><!--end tab-content product-details-->


</div>
<div class="height"></div>

<%--<!-- shopping Car -->--%>
<%--<div class="alertCheck" id="returnDialog" style="display:none">--%>
<%--<div class="alert_top"></div>--%>
<%--<div class="alert_body">--%>
<%--<div class="alertIcon">--%>
<%--<a class="right"></a>--%>

<%--<div class="line"><h3 id="resultMsg"></h3></div>--%>
<%--</div>--%>
<%--<div class="clear height"></div>--%>
<%--<div class="line lines" id="warnMsg"></div>--%>
<%--<div class="line lines">共有<b id="shoppingCartItemCount">0</b>种商品，合计：¥<b class="yellow_color"--%>
<%--id="shoppingCartTotal">0</b></div>--%>
<%--<div class="clear height"></div>--%>
<%--<div class="button">--%>
<%--<a class="btnHover" id="goShoppingCartBtn">去购物车结算</a>--%>
<%--<a class="blue_color" id="closeBtn">继续采购</a>--%>
<%--</div>--%>
<%--</div>--%>
<%--<div class="alert_bottom"></div>--%>
<%--</div>--%>



<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>