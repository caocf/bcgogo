<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    response.setHeader("Cache-Control","no-store");
    response.setHeader("Pragrma","no-cache");
    response.setDateHeader("Expires",0);
%>
<%@ page import="com.bcgogo.config.ConfigController" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="org.springframework.security.core.AuthenticationException" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>账单详情</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no" />
    <link rel="stylesheet" type="text/css" href="styles/wechat.css">
    <script type="text/javascript" src="js/extension/jquery/jquery-1.4.2.min.js"></script>

    <script language="javascript" type="text/javascript">


        $(function(){

            $("#commentBtn").click(function(){
                $(this).attr("lock","lock");
                var commentContent=$("#commentArea").val();
                var commentScore=$(".current_s").attr("score");
                $.ajax({
                    url: 'wxTxn.do?method=saveCommentRecord',
                    type:"post",
                    dataType: "json",
                    data: {
                        orderId: $("#orderId").val(),
                        vechicle: $("#vehicleNo").val(),
                        commentTargetShopId: $("#shopId").val(),
                        orderType:$("#orderTypes").val(),
                        receiptNo: $("#receiptNo").val(),
                        customerId: $("#customerId").val(),
                        commentScore:commentScore ,
                        commentContent:commentContent
                    },
                    success: function (result) {
                        if(!result.success){
                            alert(result.msg);
                            return;
                        }
                        $(".comment_input").hide();
                        $(".comment_container").show();
                        $(".comment_container .commentStr").text("好评");
                        $(".comment_container .commentContent").text(commentContent);

                    },
                    error:function(e){
                        console.debug(e);
                    }
                });
            });

            $(".comment_btn").click(function(){
                $(".comment_btn").removeClass("current_s");
                $(this).addClass("current_s");
            });

        });
    </script>
</head>

<body>
<input type="hidden" id="shopId" value="${order.shopIdStr}"/>
<input type="hidden" id="receiptNo" value="${order.receiptNo}"/>
<input type="hidden" id="vehicleNo" value="${vehicleNo}"/>
<input type="hidden" id="orderTypes" value="${orderTypes}"/>
<input type="hidden" id="orderId" value="${order.idStr}"/>
<input type="hidden" id="customerId" value="${order.customerId}"/>
<div id="container">
<div class="content">
<div class="information">
    <ul>
        <li>消费车辆：${vehicleNo}</li>
        <li style="float:right; text-align:right;">消费金额：<span class="red_txt">￥${order.total}</span> </li>
        <li style="width:100%;">消费日期：${settleDate}</li>
        <div class="clear"></div>
    </ul>
</div>
<table width="100%" border="0" cellpadding="0" cellspacing="0" class="equa1">
    <tr>
        <th>实付</th>
        <th>优惠</th>
        <th>挂账</th>
    </tr>
    <tr>
        <td>${order.settledAmount}元</td>
        <td>${order.discount}元</td>
        <td>${order.debt}元</td>
    </tr>
</table>

<div class="details_t comment_input" style="display:${empty commentRecordDTO ? 'block':'none' }">
    <div class="title">
        <h3 style="line-height:2em">服务评价：</h3>
        <div class="comment_btn highReview current_s" score="4">好评</div>
        <div class="comment_btn middleReview" score="3">中评</div>
        <div class="comment_btn badReview" score="2">差评</div>
    </div>
    <div class="comment_content">
        <textarea id="commentArea" style="resize:none" name=""></textarea>
    </div>
    <div class="clear"></div>
    <div id="commentBtn" class="comments">发表评论</div>
    <div class="clear" style="height:1px;"></div>
</div>

<div class="details_t comment_container" style="display:${empty commentRecordDTO ? 'none':'block' }">
    <div class="title">
        <table width="100%" border="0" cellspacing="0" cellpadding="0">
            <tr>
                <td valign="top" class="txt_01"><h3>服务评价：</h3>
                </td>
                <td><div style="float:left">
                    <p class="commentStr">${commentRecordDTO.commentStr}</p>
                    <span class="commentContent"> ${commentRecordDTO.commentContent} </span>
                </div>
                </td>
            </tr>
        </table>

    </div>
</div>
<c:if test="${orderTypes eq 'SALE'||orderTypes eq 'REPAIR'}">
    <div class="details_t">
        <div class="title">材料清单：</div>
        <table id="t_product" width="100%" border="0" cellpadding="0" cellspacing="0"  class="equa2">
            <colgroup>
                <col width="49%" />
                <col width="17%" />
                <col width="17%" />
                <col width="17%" />
            </colgroup>
            <tr>
                <th><span class="txt-left">品名</span></th>
                <th>单价</th>
                <th>数量</th>
                <th>金额</th>
            </tr>
            <c:forEach items="${order.itemDTOs}" var="itemDTO" varStatus="status">
                <tr>
                    <td><span class="list_01">${itemDTO.productInfo}</span></td>
                    <td>￥${itemDTO.price}</td>
                    <td>${itemDTO.amount}个</td>
                    <td><span class="red_txt">￥${itemDTO.total}</span></td>
                </tr>
            </c:forEach>
            <tr>
                <td colspan="4"><span class="list_03">工时费用合计：<span class="red_txt">￥${order.salesTotal}</span></span></td>
            </tr>
        </table>
    </div>
</c:if>

<c:if test="${orderTypes eq 'REPAIR'}">
    <div class="details_t">
        <div class="title">工时清单：</div>
        <table id="t_service" width="100%" border="0" cellpadding="0" cellspacing="0"  class="equa2">
            <colgroup>
                <col width="49%" />
                <col width="17%" />
                <col width="17%" />
                <col width="17%" />
            </colgroup>
            <tr>
                <th><span class="txt-left">施工内容</span></th>
                <th>单价</th>
                <th>工时</th>
                <th>金额</th>
            </tr>
            <c:forEach items="${order.serviceDTOs}" var="serviceDTO" varStatus="status">
                <tr>
                    <td><span class="list_01">${serviceDTO.name}</span></td>
                    <td>￥${serviceDTO.standardUnitPrice}</td>
                    <td>${serviceDTO.actualHours}</td>
                    <td><span class="red_txt">￥${serviceDTO.total}</span></td>
                </tr>
            </c:forEach>
            <tr>
                <td colspan="4"><span class="list_03">工时费用合计：<span class="red_txt">￥${order.serviceTotal}</span></span></td>
            </tr>
        </table>
    </div>
</c:if>

<c:if test="${orderTypes eq 'WASH_BEAUTY'}">
    <div class="details_t">
        <div class="title">工时清单：</div>
        <table  width="100%" border="0" cellpadding="0" cellspacing="0"  class="equa2">
            <colgroup>
                <col width="49%" />
                <col width="17%" />
                <col width="17%" />
                <col width="17%" />
            </colgroup>
            <tr>
                <th><span class="txt-left">施工内容</span></th>
                <th>金额</th>
            </tr>
            <c:forEach items="${order.washBeautyOrderItemDTOs}" var="itemDTO" varStatus="status">
                <tr>
                    <td><span class="list_01">${itemDTO.serviceName}</span></td>
                    <td>
                        <c:choose>
                            <c:when test="${itemDTO.payType=='MONEY'}">
                                <span class="red_txt">￥${itemDTO.priceStr}</span>
                            </c:when>
                            <c:otherwise>
                                ${itemDTO.priceStr}
                            </c:otherwise>
                        </c:choose>
                    </td>
                </tr>
            </c:forEach>
            <tr>
                <td colspan="4"><span class="list_03">工时费用合计：<span class="red_txt">￥${order.serviceTotal}</span></span></td>
            </tr>
        </table>
    </div>
</c:if>

<c:if test="${orderTypes eq 'SALE'||orderTypes eq 'REPAIR'}">
    <div class="details_t">
        <div class="title">
            <h3>其他费用：</h3>
        </div>
        <table width="100%" border="0" cellpadding="0" cellspacing="0"  class="equa2">
            <colgroup>
                <col width="80%" />
                <col width="20%" />
            </colgroup>
            <tr>
                <th><span class="txt-left">费用项目</span></th>
                <th>金额</th>
            </tr>
            <c:forEach items="${oItemDTOs}" var="itemDTO" varStatus="status">
                <tr>
                    <td><span class="list_01">${itemDTO.name}</span></td>
                    <td><span class="red_txt">￥${itemDTO.price}</span></td>
                </tr>
            </c:forEach>
            <tr>
                <td colspan="4"><span class="list_03">其他费用合计：<span class="red_txt">￥${order.otherIncomeTotal}</span></span></td>
            </tr>
        </table>
    </div>
</c:if>
<c:if test="${memberDTO!=null}">
    <div class="details_t">
        <div class="title">
            <h3>会员卡服务信息：</h3>
            <br />
            <p>会员卡号（${memberDTO.memberNo}）<span style="float:right"> 卡内余额：<span class="red_txt">￥${memberDTO.balance}</span></span></p>
        </div>
        <table width="100%" border="0" cellpadding="0" cellspacing="0"  class="equa2">
            <colgroup>
                <col width="40%" />
                <col width="30%" />
                <col width="30%" />
            </colgroup>
            <tr>
                <th><span class="txt-left">项目</span></th>
                <th>失效日期</th>
                <th>剩余次数</th>
            </tr>
            <c:forEach items="${memberDTO.memberServiceDTOs}" var="service" varStatus="status">
                <tr>
                    <td><span class="list_01">${service.serviceName}</span></td>
                    <td>${service.deadlineStr}</td>
                    <td>${service.timesStr}</td>
                </tr>
            </c:forEach>
        </table>
    </div>
</c:if>
<div class="bottom">
    <table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr>
            <td valign="top"><span class="txt_01">消费店铺：</span></td>
            <td valign="top">${shopDTO.name}</td>
        </tr>
        <tr>
            <td valign="top">店铺地址：</td>
            <td valign="top">${shopDTO.address}</td>
        </tr>
        <tr>
            <td valign="top">店铺电话：</td>
            <td valign="top">${shopDTO.landline}</td>
        </tr>
    </table>
</div>
</div>
</div>
</body>

</html>
