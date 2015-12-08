<%--
  Created by IntelliJ IDEA.
  User: ndong
  Date: 13-11-6
  Time: 下午4:43
  To change this template use File | Settings | File Templates.
--%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>报价信息展开</title>
    <%@include file="/WEB-INF/views/header_script.jsp" %>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
    <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/messagePrompt<%=ConfigController.getBuildVersion()%>.js"></script>

    <script type="text/javascript">
        defaultStorage.setItem(storageKey.MenuUid, "WEB_AUTOACCESSORYONLINE_MY_PREBUYORDER");

        function createPurchaseOrder(quotedPreBuyOrderItemIds,_blank){
            if (G.isEmpty(quotedPreBuyOrderItemIds)) {
                return;
            }
            if(_blank){
                window.open("RFbuy.do?method=createPurchaseOrderOnlineByQuotedPreBuyOrder&quotedPreBuyOrderItemIds="+quotedPreBuyOrderItemIds);
            }else{
            window.location.href="RFbuy.do?method=createPurchaseOrderOnlineByQuotedPreBuyOrder&quotedPreBuyOrderItemIds="+quotedPreBuyOrderItemIds;
            }
        }

        function drawSupplierOtherQuotedItems(json){
            $("#quotedPreBuyOrderContainer").children().remove();
            var quotedPreBuyOrderItemDTOs=json.results;
            if(G.isEmpty(quotedPreBuyOrderItemDTOs)) return;
            var infoStr="";
            for(var i=0;i<quotedPreBuyOrderItemDTOs.length;i++){
                var quotedPreBuyOrderItemDTO=quotedPreBuyOrderItemDTOs[i];
                var idStr=quotedPreBuyOrderItemDTO.idStr;
                var qShopId=quotedPreBuyOrderItemDTO.shopIdStr;
                var qProductInfo=G.normalize(quotedPreBuyOrderItemDTO.productInfo);
                var productIdStr=quotedPreBuyOrderItemDTO.productIdStr;
                var quotedDateStr=G.normalize(quotedPreBuyOrderItemDTO.quotedDateStr);
                var qPrice=G.rounding(quotedPreBuyOrderItemDTO.price);
                var qUnit=G.normalize(quotedPreBuyOrderItemDTO.unit);
                var preBuyOrderItemDTO=quotedPreBuyOrderItemDTO.preBuyOrderItemDTO;
                var preBuyOrderId=quotedPreBuyOrderItemDTO.preBuyOrderIdStr;
                var pProductInfo=G.normalize(preBuyOrderItemDTO.productInfo);
                var qInfo='(';
                qInfo+=quotedPreBuyOrderItemDTO.includingTax=="TRUE"?"含税、":"";
                qInfo+=G.normalize(quotedPreBuyOrderItemDTO.shippingMethodStr);
                qInfo+='下单后'+quotedPreBuyOrderItemDTO.arrivalTime+'天到货';
                qInfo+=')';
                var quotedCount=G.normalize(preBuyOrderItemDTO.quotedCount);
                var pCount = G.normalize(preBuyOrderItemDTO.amount);
                var pUnit=G.normalize(preBuyOrderItemDTO.unit);
                var memo=preBuyOrderItemDTO.memo;
                memo=G.isEmpty(memo)?"无":memo;
                var status =  preBuyOrderItemDTO.statusStr;
                var endDateCount = preBuyOrderItemDTO.endDateCount;
                infoStr+='<div class="accessoriesOffer">'+
                        '<div class="title"><strong>我的求购信息：</strong><a href="preBuyOrder.do?method=showPreBuyOrderById&preBuyOrderId='+preBuyOrderId+'" class="blue_color p-info-detail">'+pProductInfo+'&nbsp;求购'+pCount+pUnit+'</a>';
                if(status == '过期') {
                    infoStr += '（求购已过期）';
                }else {
                    infoStr += '（还剩 <strong class="yellow_color">'+endDateCount+'</strong> 天失效）';
                }

             infoStr += '<a style="float:right" target="_blank" href="preBuyOrder.do?method=showPreBuyOrderById&preBuyOrderId='+preBuyOrderId+'" class="blue_color">求购详情</a></div>'+
                        '<div class="left">'+
                        '<div class="describeOffer">'+
                        '<div class="left">描述：</div>'+
                        '<div class="right">'+memo+'</div>'+
                        '<div class="clear"></div>'+
                        '</div>'+
                        '<table width="95%" border="0" cellspacing="0" class="Offer-table">'+
                        '<tr>'+
                        '<td>报价商品：<a class="blue_color p-info-detail" href="shopProductDetail.do?method=toShopProductDetail&paramShopId='+qShopId+'&productLocalId='+productIdStr+'&quotedPreBuyOrderItemId='+idStr+'">'+qProductInfo+'</a></td>'+
                        '<td>（于'+quotedDateStr+'报价）</td>'+
                        '</tr>'+
                        '<tr>'+
                        '<td>报价价格：<strong class="yellow_color">'+qPrice+'</strong>'+qUnit+qInfo+'</td>'+
                        '<td><a target="_blank" onclick="createPurchaseOrder(\''+idStr+'\','+true+')" class="blue_color">我要下单</a></td>'+
                        '</tr>'+
                        '</table>'+
                        '</div>'+
                        '<div quotedItemId="'+idStr+'" class="J-pre-item offer-select offer-select-common"></div>'+
                        '<div class="clear"></div>'+
                        '</div>'+
                        '<div class="clear"></div>';
            }
            $("#allOtherItems").text(json.pager.totalRows);
            $("#quotedPreBuyOrderContainer").append(infoStr);
        }

        $(function(){

            $(".J-pre-item").live("click",function(){
                if($(this).hasClass('offer-select-common')){
                    $(this).removeClass('offer-select-common').addClass('offer-select-click');
                }else{
                    $(this).removeClass('offer-select-click').addClass('offer-select-common');
                }
            });

            $("#batchCreatePurchaseOrder").click(function(){
                var itemIdArr=new Array();
                $('.J-pre-item').each(function(){
                    if($(this).hasClass('offer-select-click')){
                        itemIdArr.push($(this).attr('quotedItemId'));
                    }
                });
                if(itemIdArr.length==0){
                    nsDialog.jAlert("请选择要下单的项目。");
                    return;
                }
                createPurchaseOrder(itemIdArr.toString());
            });

            var contactsJson='${contactsJson}';
            if(!G.isEmpty(contactsJson)){
                var contacts=JSON.parse(contactsJson);
                var contactArr=new Array();
                for(var i=0;i<contacts.length;i++){
                    var contact=contacts[i];
                    if(G.isEmpty(contact)) continue;
                    contactArr.push(contact.qq);
                }
                $(".J_QQ").multiQQInvoker({
                    QQ:contactArr
                });
            }

            if(!G.isEmpty($("#quotedPreBuyOrderItemId").val())){
                var data={
                    startPageNo:1,
                    maxRows:10,
                    quotedPreBuyOrderItemId:$("#quotedPreBuyOrderItemId").val()
                };
                var url="preBuyOrder.do?method=getSupplierOtherQuotedItems";
                APP_BCGOGO.Net.asyncAjax({
                    url:url ,
                    type: "POST",
                    cache: false,
                    data:data,
                    dataType: "json",
                    success: function (json) {
                        if(G.isEmpty(json)||!json.success){
                            return;
                        }
                        drawSupplierOtherQuotedItems(json);
                        initPage(json,"_drawSupplierOtherQuotedItems",url, null, "drawSupplierOtherQuotedItems", '', '',data,null);
                    },
                    error:function(){
                        nsDialog.jAlert("网络异常！");
                    }
                });
            }
        });
    </script>
</head>
<body>
<%@ include file="/WEB-INF/views/header_html.jsp" %>
<%@ include file="/common/messagePrompt.jsp" %>
<input type="hidden" id="quotedPreBuyOrderItemId" value="${quotedPreBuyOrderItemId}"/>
<div class="i_main clear">
    <div class="clear i_height"></div>
    <div class="titBody">
        <jsp:include page="supplyCenterLeftNavi.jsp">
            <jsp:param name="currPage" value="preBuyOrderManage"/>
        </jsp:include>
        <div class="added-management pre-info-spread">
            <div id="quotedItemIdDiv"></div>
            <div class="product-details" style="padding-bottom: 40px;">
                <div>
                    [报价卖家]<a class="blue_color" onclick="renderShopMsgDetail('${shopDTO.idStr}','true')">${shopDTO.name}</a>
                    <%--<img src="images/common/qq/icon_qq_online.gif" width="23" height="16" />--%>
                    <a class="J_QQ"></a>
                    （共报价 <strong class="yellow_color" id="allOtherItems">0</strong> 次）
                    <div id="quotedPreBuyOrderContainer">

                    </div>
                </div>
                <!-- 分页代码 -->
                <div class="i_pageBtn" style="float:left;margin-top: 7px;">
                    <bcgogo:ajaxPaging
                            url="preBuyOrder.do?method=getSupplierOtherQuotedItems"
                            postFn="drawSupplierOtherQuotedItems"
                            display="none"
                            dynamical="_drawSupplierOtherQuotedItems"/>
                </div>
            </div>
            <div class="height"></div>
            <div class="divTit" style="float:right; padding-right:0">
                <div class="clear i_height"></div>
                <a id="batchCreatePurchaseOrder" class="button">批量下单</a>
                <a class="button2" style="margin-right:0" href="preBuyOrder.do?method=preBuyOrderManage">返回</a>
            </div>
        </div>
    </div>
    <%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>