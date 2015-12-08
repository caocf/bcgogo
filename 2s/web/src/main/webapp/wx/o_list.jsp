<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page import="com.bcgogo.config.ConfigController" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>账单列表</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=0, minimum-scale=1.0, maximum-scale=1.0">
<meta name="apple-mobile-web-app-capable" content="yes">
<meta name="apple-mobile-web-app-status-bar-style" content="black">
<meta content="telephone=no" name="format-detection" />
<link rel="stylesheet" type="text/css" href="styles/wechat<%=ConfigController.getBuildVersion()%>.css">
<style type="text/css" media="all">

</style>
<script type="text/javascript" src="js/extension/jquery/jquery-1.4.2.min.js"></script>
<script type="text/javascript" src="js/extension/mobile/iscroll.js"></script>
<script type="text/javascript">

    var myScroll,
            pullDownEl, pullDownOffset,
            pullUpEl, pullUpOffset;



    function loaded() {
        pullUpEl = document.getElementById('pullUp');
        pullUpOffset = pullUpEl.offsetHeight;

        myScroll = new iScroll('wrapper', {
            useTransition: true,
            topOffset: pullDownOffset,
            onRefresh: function () {
                if (pullUpEl.className.match('loading')) {
                    pullUpEl.className = '';
                    pullUpEl.querySelector('.pullUpLabel').innerHTML = '上拉查看更多...';
                }
            },
            onScrollMove: function () {
                if (this.y < (this.maxScrollY - 5) && !pullUpEl.className.match('flip')) {
                    pullUpEl.className = 'flip';
                    pullUpEl.querySelector('.pullUpLabel').innerHTML = '释放刷新...';
                    this.maxScrollY = this.maxScrollY;
                } else if (this.y > (this.maxScrollY + 5) && pullUpEl.className.match('flip')) {
                    pullUpEl.className = '';
                    pullUpEl.querySelector('.pullUpLabel').innerHTML = '上拉查看更多...';
                    this.maxScrollY = pullUpOffset;
                }
            },
            onScrollEnd: function () {
                if (pullUpEl.className.match('flip')) {
                    pullUpEl.className = 'loading';
                    pullUpEl.querySelector('.pullUpLabel').innerHTML = '加载中...';
                    pullUpAction();	// Execute custom function (ajax call?)
                }
            }
        });
        setTimeout(function () { document.getElementById('wrapper').style.left = '0'; }, 800);
        pullUpAction();
    }

    document.addEventListener('touchmove', function (e) { e.preventDefault(); }, false);
    document.addEventListener('DOMContentLoaded', function () {
        setTimeout(
                loaded, 200);
    }, false);

    var startPageNo=0;
    function pullUpAction () {
        startPageNo++;
         var vehices=new Array();
        $(".vehicle-hidden").each(function(){
              vehices.push($(this).val());
        });

        $.ajax({
            type: "POST",
            url: "./wxTxn.do?method=gList",
            data: {
                startPageNo:startPageNo,
                pageRows:4,
                vehicleList:vehices.toString()
            },
            dataType: "json",
            success: function(orders){
                if(!orders) return;
                var oStr='';
                for (var i=0;i<orders.length; i++) {
                    var order=orders[i];
                    oStr+=getOrderStr(order);
                }
                $("#history_bill").append(oStr)
            }
        });
        // Remember to refresh when contents are loaded (ie: on ajax completion)
        myScroll.refresh();
    }

    function normalize(s){
        if(s==null){
            return "";
        }else{
            return s;
        }
//          var nullDefined = [null, undefined, "null", "undefined"].concat(nullExtended);
//    return G.Lang.contains(s, nullDefined) ? (std || "") : s;
    }

    function getOrderStr(data){
        var orderStr= '<li>'+
                '<div class="history_01">'+
                '<div class="title">'+
                '<div class="check_details"><a href="'+data.orderDetailUrl+'">查看详情</a></div>'+
                '<h2>'+data.shopName+'</h2>'+
                '<div class="clear"></div>'+
                '</div>'+
                '<div style="margin-top: 2px;">'+
                '<div class="wash_car">'+data.orderType+'</div>'+
                '<div class="txt_r">'+normalize(data.payMethod)+'</div>'+
                '<div class="clear"></div>'+
                '<div class="time_car">'+data.vestDateStr+'</div>'+
                '<div class="txt_r">金额<span class="red_txt">￥'+data.total+'</span></div>'+
                '<div class="clear"></div>'+
                '</div>'+
                '</div> '+
                '</li>';
        return orderStr;
    }

</script>

</head>

<body>
<c:forEach items="${vehicleNos}" var="vehicleNo" varStatus="status">
    <input type="hidden" class="vehicle-hidden" value="${vehicleNo}" />
</c:forEach>

<div id="wrapper">
    <div id="scroller">
        <ul id="history_bill">
            <c:forEach items="${orders}" var="order" varStatus="status">
                <li>
                    <div class="history_01">
                        <div class="title">
                            <div class="check_details"><a href="${order.orderDetailUrl}">查看详情</a></div>
                            <h2>${order.shopName}</h2>
                            <div class="clear"></div>
                        </div>
                        <div>
                            <div class="wash_car">${order.orderTypeValue}</div>
                            <c:if test="${fn:length(order.payMethod)>1}">
                                <c:forEach items="${order.payMethod}" var="method" varStatus="status">
                                    <c:if test="${method=='MEMBER_BALANCE_PAY'}">
                                        <div class="txt_r">计次卡消费</div>
                                    </c:if>
                                </c:forEach>
                            </c:if>

                            <div class="clear"></div>
                            <div class="time_car">${order.vestDateStr}</div>
                            <div class="txt_r">金额<span class="red_txt">￥${order.amount}</span></div>
                            <div class="clear"></div>
                        </div>
                    </div>
                </li>
            </c:forEach>
        </ul>
        <div id="pullUp">
            <span class="pullUpIcon"></span><span class="pullUpLabel">上拉刷新...</span>
        </div>
    </div>
</div>
</body>

</html>
