<%--
  Created by IntelliJ IDEA.
  User: ndong
  Date: 13-11-13
  Time: 上午9:54
  To change this template use File | Settings | File Templates.
--%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="numberUtil" uri="http://www.bcgogo.com/taglibs/tags" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>我的报价</title>
    <%@include file="/WEB-INF/views/header_script.jsp" %>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/addCheck<%=ConfigController.getBuildVersion()%>.css">
    <style type="text/css">
        #otherBusinessChanceTable tr td {
            padding: 0 0 0 10px;
        }

        .accessoriesLeft .content {
            color:#000000;
        }
        .hover_yellow {
            display: inline-block;
            width:590px;
        }
    </style>
    <script type="text/javascript" src="js/messagePrompt<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/page/autoaccessoryonline/quotedPreBuyOrder<%=ConfigController.getBuildVersion()%>.js"></script>
    <bcgogo:permissionParam permissions="WEB.AUTOACCESSORYONLINE.ADD_QUOTEDPREBUYORDER">
        <c:set var="addQuotedPreBuyOrderPerm" value="${WEB_AUTOACCESSORYONLINE_ADD_QUOTEDPREBUYORDER}"/>
    </bcgogo:permissionParam>
    <script type="text/javascript">
        defaultStorage.setItem(storageKey.MenuUid, "WEB_AUTOACCESSORYONLINE_MY_QUOTEDPREBUYORDER");
        defaultStorage.setItem(storageKey.MenuCurrentItem,"详情");
        var loop;
        function autoScroll(){
            var $scrollItem=$('.JBusinessChance .scrollItem');
            var intervalHeight=-30;
            $scrollItem.animate({
                marginTop:intervalHeight+"px"
            },1000,function(){
                var $tr = $(this).find("tr:first");
                $tr.remove();
                $(this).find("table").append($tr);
                $(this).css({marginTop:"0px"});
            });
        }

//         function autoScroll(){
//            var intervalHeight=-30;
//            $('.JBusinessChance .scrollItem').animate({
//                marginTop:intervalHeight+"px"
//            },1000,function(){
//              var $tr=$(".scrollItem tr:first");
//                $tr.remove();
//                $('.scrollItem table').append($tr);
//            });
//        }

        $(function(){
            var itemIndex=G.rounding($("#otherBusinessChanceTable tr:last").attr("itemIndex"));
            if(itemIndex>=5){
                $('.JBusinessChance').height(150);
                loop = setInterval(autoScroll,2000);
            }else{
                $('.JBusinessChance').height(30*(itemIndex+1));
            }
            $(".JBusinessChance").hover(function(){
                clearInterval(loop);
            },function(){
                if(itemIndex>=5) {
                    loop = setInterval(autoScroll,2000);
                }
            });
            $(".hover_yellow").live("click",function(){
                var itemId = $(this).attr("itemid");
                if(itemId) {
                    window.open("preBuyOrder.do?method=showBuyInformationDetailByPreBuyOrderItemId&preBuyOrderItemId=" + itemId,"_blank");
                }
            });
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
            <jsp:param name="currPage" value="quotedPreBuyOrderManage"/>
        </jsp:include>
        <div class="added-management">
            <div class="product-details">
                <div class="date-relative">
                    <div class="date-absolute">
                        <c:if test="${preBuyOrderItemDTO.statusStr =='过期'}">
                            <h2>商机已过期</h2>
                        </c:if>
                         <c:if test="${preBuyOrderItemDTO.statusStr !='过期'}">
                            距信息失效仅剩 ${preBuyOrderItemDTO.endDateCount}天
                            <br />
                        </c:if>
                        截止：${preBuyOrderItemDTO.endDateStr} <br />
                        发布日期：${preBuyOrderItemDTO.vestDateStr} </div>
                </div>
                <div class="details-pic">
                    <img src="${preBuyOrderItemDTO.imageCenterDTO.productInfoBigImageDetailDTOs[0].imageURL}"/>
                </div>
                <div class="details-right">
                    <div class="font14">
                        <strong>
                            <c:choose>
                                <c:when test="${preBuyOrderItemDTO.businessChanceType=='Normal'}">
                                    [<a class="yellow_color">求购</a>]
                                </c:when>
                                <c:when test="${preBuyOrderItemDTO.businessChanceType=='SellWell'}">
                                    [<a class="green_color">畅销</a>]
                                </c:when>
                                <c:otherwise>
                                    [<a class="red_color">缺料</a>]
                                </c:otherwise>
                            </c:choose>
                            ${preBuyOrderItemDTO.productName} ${preBuyOrderItemDTO.brand}
                        </strong>
                    </div>
                    <div class="product-line clear">
                        <div class="line-01">商品编号：</div>
                        <div class="line-02">${preBuyOrderItemDTO.commodityCode}</div>
                    </div>
                    <div class="product-line clear">
                        <div class="line-01">品&nbsp;&nbsp;&nbsp;&nbsp;名：</div>
                        <div class="line-02">${preBuyOrderItemDTO.productName}</div>
                    </div>
                    <div class="product-line clear">
                        <div class="line-01">品&nbsp;&nbsp;&nbsp;&nbsp;牌：</div>
                        <div class="line-02">${preBuyOrderItemDTO.brand}</div>
                    </div>
                    <div class="product-line clear">
                        <div class="line-01">规格/型号：</div>
                        <div class="line-02">${preBuyOrderItemDTO.spec} ${preBuyOrderItemDTO.model}</div>
                    </div>
                    <div class="product-line clear">
                        <div class="line-01">适合车型：</div>
                        <div class="line-02">${preBuyOrderItemDTO.vehicleBrand} ${preBuyOrderItemDTO.vehicleModel}</div>
                    </div>
                    <div class="product-line clear">
                        <div class="line-01">求购数量：</div>
                        <div class="line-02">${preBuyOrderItemDTO.amount} ${preBuyOrderItemDTO.unit}</div>
                    </div>
                    <div class="product-line clear">
                        <div class="line-01">描&nbsp;&nbsp;&nbsp;&nbsp;述：</div>
                        <div class="line-02">
                            ${empty preBuyOrderItemDTO.memo?"无":preBuyOrderItemDTO.memo}
                        </div>
                        <div class="i_height clear"></div>
                    </div>
                    <div class="clear"></div>
                    <div class="black-color">
                        浏览量 <a class="yellow_color"><strong>${numberUtil:roundInt(preBuyOrderItemDTO.viewedCount)}</strong></a> 次 |
                        已有 <a class="yellow_color"> <strong>${preBuyOrderItemDTO.quotedCount}</strong></a> 家卖家参与报价
                    </div>
                    <div class="accessoriesOffer" style="width:500px;">
                        <div class="title" style="width:480px;"><strong>我的报价信息：</strong>（于${quotedPreBuyOrderItemDTO.quotedDateStr}报价）</div>
                        <div>
                            <div class="i_height clear"></div>
                            <table width="96%" border="0" cellspacing="0" class="Offer-table">
                                <tr>
                                    <td>报价商品：<a href="shopProductDetail.do?method=toShopProductDetail&paramShopId=${quotedPreBuyOrderItemDTO.shopIdStr}&productLocalId=${quotedPreBuyOrderItemDTO.productIdStr}" class="blue_color"><span>${quotedPreBuyOrderItemDTO.productInfo}</span></a></td>
                                </tr>
                                <tr>
                                    <td>报价价格：<strong class="yellow_color">${quotedPreBuyOrderItemDTO.price}</strong>元/${quotedPreBuyOrderItemDTO.unit}（${quotedPreBuyOrderItemDTO.includingTax=="TRUE"?"含税、":""}<c:if test="${not empty quotedPreBuyOrderItemDTO.shippingMethod}">${quotedPreBuyOrderItemDTO.shippingMethod.name}、</c:if>下单后${quotedPreBuyOrderItemDTO.arrivalTime}天到货）</td>
                                </tr>
                            </table>

                        </div>
                        <div class="clear"></div>
                    </div>
                </div>
                <div class="clear"></div>
            </div>
            <%--<c:if test="${not empty otherPreBuyOrderInfo.preBuyOrderDTOList}">--%>
            <div class="accessoriesLeft other-business-chance-div">
                <div class="title" style="width:794px;border: none"><a class="blue_color" href="preBuyOrder.do?method=preBuyInformation&shopId=${preBuyOrderItemDTO.shopId}" target="_blank">查看所有>></a> 此买家其他商机
                    <span class="font12-normal">该买家共有</span>
                    <span class="yellow_color">${otherPreBuyOrderInfo.countPreBuyNormal}</span> <span class="font12-normal">条求购商机 |</span>
                    <span class="yellow_color">${otherPreBuyOrderInfo.countPreBuyLack}</span><span class="font12-normal"> 条缺料商机 | </span>
                    <span class="yellow_color">${otherPreBuyOrderInfo.countPreBuySellWell}</span> <span class="font12-normal">条畅销商机！</span></div>
                <div  class="JBusinessChance content" style="height: 30px">
                    <div class="scrollItem">
                        <table id="otherBusinessChanceTable" width="100%" border="0" class="accessories-table">
                            <c:if test="${empty otherPreBuyOrderInfo.preBuyOrderDTOList}">
                                <tr itemIndex="0">
                                    <td>
                                        <span style="margin-left: 10px;font-weight: bold;">暂无数据</span>
                                    </td>
                                </tr>
                            </c:if>
                            <c:if test="${not empty otherPreBuyOrderInfo.preBuyOrderDTOList}">
                                <c:forEach items="${otherPreBuyOrderInfo.preBuyOrderDTOList}" var="orderDTO" varStatus="status">
                                    <c:if test="${not empty orderDTO.itemDTO}">
                                        <tr itemIndex="${status.index}">
                                            <td style="width: 80%"><span class="hover_yellow text-overflow" itemId="${orderDTO.itemDTO.idStr}" title="${orderDTO.itemDTO.commodityCode == null ? '' : orderDTO.itemDTO.commodityCode} ${orderDTO.itemDTO.productName} ${orderDTO.itemDTO.brand} ${orderDTO.itemDTO.spec} ${orderDTO.itemDTO.model} ${orderDTO.itemDTO.vehicleBrand} ${orderDTO.itemDTO.vehicleModel} <c:if test="${!(orderDTO.businessChanceType eq 'SellWell')}" var="status1">${orderDTO.itemDTO.amount} ${orderDTO.itemDTO.unit}</c:if><c:if test="${!status1}">上周销量 ${orderDTO.itemDTO.fuzzyAmountStr} ${orderDTO.itemDTO.unit}</c:if>">[<span class="yellow_color">${orderDTO.businessChanceType.name} </span>] （${orderDTO.vestDateStr}发布）
                                 <span id="itemDTOs${status.index}.commodityCode">${orderDTO.itemDTO.commodityCode == null ? "" : orderDTO.itemDTO.commodityCode}</span>
                                 <span id="itemDTOs${status.index}.productName">${orderDTO.itemDTO.productName}</span>
                                 <span id="itemDTOs${status.index}.brand">${orderDTO.itemDTO.brand}</span>
                                 <span id="itemDTOs${status.index}.spec">${orderDTO.itemDTO.spec}</span>
                                 <span id="itemDTOs${status.index}.model">${orderDTO.itemDTO.model}</span>
                                 <span id="itemDTOs${status.index}.vehicleBrand">${orderDTO.itemDTO.vehicleBrand}</span>
                                 <span id="itemDTOs${status.index}.vehicleModel">${orderDTO.itemDTO.vehicleModel}</span>
                             <c:if test="${!(orderDTO.businessChanceType eq 'SellWell')}" var="status2">
                                  <span class="yellow_color"><strong>${orderDTO.itemDTO.amount}</strong></span> ${orderDTO.itemDTO.unit} </span>
                                                </c:if>
                                                <c:if test="${!status2}">
                                                    上周销量 <span class="yellow_color"><strong>${orderDTO.itemDTO.fuzzyAmountStr}</strong></span> ${orderDTO.itemDTO.unit}
                                                </c:if>
                                            </td>
                                            <td style="width:100px;"><c:if test="${!(orderDTO.businessChanceType eq 'SellWell')}"><span style="margin-left: -40px;">（还剩${orderDTO.endDateCount}天有效）</span></c:if></td>
                                            <td>
                                                <c:if test="${orderDTO.itemDTO.myQuoted}">
                                                    <div class="accessories-btn" style="background: #ddd;margin-left: -40px;">我已报价</div>
                                                </c:if>
                                                <c:if test="${!orderDTO.itemDTO.myQuoted}">
                                                    <div class="accessories-btn J_QuotedStepFirst" data-prebuyorderitemid="${orderDTO.itemDTO.idStr}" style="margin-left: -40px;">我要报价</div>
                                                </c:if>
                                            </td>
                                        </tr>
                                    </c:if>
                                </c:forEach>
                            </c:if>
                        </table>
                    </div>
                    <div class="clear"></div>
                </div>
                <div class="clear"></div>
            </div>
            <%--</c:if>--%>
        </div>

    </div>
</div>

<input type="hidden" id="currentPreBuyOrderId" value="${preBuyOrderDTO.id}">
<input type="hidden" id="currentPreBuyOrderItemId">
<%@ include file="./alert/quotedPreBuyOrderAlert.jsp" %>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>