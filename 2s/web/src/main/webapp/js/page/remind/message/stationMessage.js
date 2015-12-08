$(function () {

    /*------------------checkbox  全选  start------------------------------ */
    $("#selectAll").bind("click",function() {
        $(".J_itemSelectAll").attr("checked",this.checked);
    });

    $(".J_itemSelectAll").live("click",function() {
        var isSelectAll = $(".J_itemSelectAll").length == $(".J_itemSelectAll:checked").length ? true : false;
        $("#selectAll").attr("checked",isSelectAll);
    });
    $(".J_showProductDetailBtn").live("click", function (e) {
        e.preventDefault();
        var productShopId = $(this).attr("data-productshop-id");
        var productLocalInfoId = $(this).attr("data-productlocalinfo-id");
        if (!G.Lang.isEmpty(productShopId) && !G.Lang.isEmpty(productLocalInfoId)) {
            window.open("shopProductDetail.do?method=toShopProductDetail&paramShopId="+productShopId+"&productLocalId="+productLocalInfoId, "_blank");
        }
    });
    $("#searchSenderPushMessageBtn").click(function (e) {
        e.preventDefault();
        searchSenderPushMessage();
    });
    function searchSenderPushMessage(){
        var param = $("#searchSenderPushMessageForm").serializeArray();
        var data = {startPageNo: 1, maxRows: 10};
        $.each(param, function (index, val) {
            data[val.name] = val.value;
        });
        var url = "stationMessage.do?method=searchSenderStationMessages";
        APP_BCGOGO.Net.asyncPost({
            url: url,
            data: data,
            dataType: "json",
            success: function (json) {
                if (!G.isEmpty(json) && !G.isEmpty(json[0]) && (!G.isEmpty($("#receiver").val()) ||!G.isEmpty($("#dayRange").val()))) {
                    $(".J_showAllPushMessage").closest("div").show()
                }else{
                    $(".J_showAllPushMessage").closest("div").hide()
                }

                initPages(json, '_senderPushMessage', url, '', 'drawSenderPushMessageTable', '', '', data, '');
                drawSenderPushMessageTable(json);
            },
            error: function () {
                nsDialog.jAlert("数据异常!");
            }
        });
    }
    searchSenderPushMessage();

    $(".J_showAllPushMessage").bind("click",function(e){
        $("#receiver").val("");
        $("#dayRange").val("");
        searchSenderPushMessage();
    });
    $("#batchDeleteSenderPushMessageBtn").bind("click",function(e){
        e.preventDefault();
        var pushMessageIds =[];
        $(".J_itemSelectAll:checked").each(function(index){
            pushMessageIds.push($(this).attr("data-pushmessage-id"));
        });
        if(pushMessageIds.length==0){
            nsDialog.jAlert("请选择需要删除的消息！");
            return;
        }else{
            nsDialog.jConfirm("确认是否删除当前选中的消息?", "删除消息", function (returnVal) {
                if (returnVal) {
                    APP_BCGOGO.Net.asyncPost({
                        url: "stationMessage.do?method=deleteStationMessage",
                        data: { pushMessageIds: pushMessageIds.join(",")},
                        dataType: "json",
                        success: function (result) {
                            if (result.success) {
                                nsDialog.jAlert("删除成功!");
                                searchSenderPushMessage();
                            }
                        },
                        error: function () {
                            nsDialog.jAlert("数据异常!");
                        }
                    });
                }
            });
        }
    });


//    $(".showProductBtn").live("click", function () {
//        var productLocalInfoIds = $(this).attr("data-productids");
//        if(productLocalInfoIds.endWith(",")){
//            productLocalInfoIds = productLocalInfoIds.substring(0,productLocalInfoIds.length - 1);
//        }
//        var messageId = $(this).attr("data-message-id");
//        if (!G.isEmpty(messageId) &&!G.isEmpty(productLocalInfoIds)) {
//            //校验商品有没有全部被下架
//            APP_BCGOGO.Net.syncPost({
//                url: "message.do?method=validateProduct",
//                data: {productIds: productLocalInfoIds, messageId: messageId},
//                dataType: "json",
//                success: function (json) {
//                    if (json.success && json.data) {
//                        if(json.data=="redirectToCommodityQuotations") {
//                            window.open("autoAccessoryOnline.do?method=toCommodityQuotations&productIds=" + productLocalInfoIds);
//                        } else if(json.data=="redirectToStockSearch") {
//                            window.open("stockSearch.do?method=getStockSearch&productIds=" + productLocalInfoIds + "&fromPage=stationMessage");
//                        }
//
//                    } else {
//                        nsDialog.jAlert(json.msg);
//                    }
//                },
//                error: function () {
//                    nsDialog.jAlert("数据异常!");
//                }
//            });
//
//        }
//    });
});
function drawSenderPushMessageTable(json) {
    $("#senderPushMessageTables > table").remove();
    if (!G.isEmpty(json) && !G.isEmpty(json[0]) && json[0].length>0) {
        var html="";
        html +='<table width="796" border="0" cellspacing="0" class="news-table">';
        html +='    <colgroup valign="top">';
        html +='        <col width="30" />';
        html +='        <col width="110" />';
        html +='        <col width="170" />';
        html +='        <col/>';
        html +='        <col width="162" />';
        html +='    </colgroup>';
        html +='    <tr class="news-thbody">';
        html +='        <td></td>';
        html +='        <td>发布日期</td>';
        html +='        <td>收件店铺</td>';
        html +='        <td>消息内容</td>';
        html +='        <td>有效日期</td>';
        html +='    </tr>';

        $.each(json[0], function(index,pushMessageDTO) {
            var receiverShopHtml = '';
            if(!G.isEmpty(pushMessageDTO.senderCustomerDTOList)){
                $.each(pushMessageDTO.senderCustomerDTOList, function (index, customerDTO) {
                    if(!G.isEmpty(customerDTO.customerShopIdStr)){
                        receiverShopHtml+='<a class="blue_color" target="_blank" href="shopMsgDetail.do?method=renderShopMsgDetail&paramShopId='+customerDTO.customerShopIdStr+'">';
                    }
                    receiverShopHtml+=customerDTO.name;
                    if(!G.isEmpty(customerDTO.customerShopIdStr)){
                        receiverShopHtml+='</a>';
                    }
                    receiverShopHtml+='<br/>';
                });
            }
            var paramsJson = {};
            if(!G.isEmpty(pushMessageDTO.params)){
                paramsJson = JSON.parse(pushMessageDTO.params);
            }
            html +='    <tr>';
            html +='        <td valign="top"><input type="checkbox" data-pushmessage-id="'+pushMessageDTO.idStr+'"  class="J_itemSelectAll"/></td>';
            html +='        <td>'+pushMessageDTO.createDateStr+' '+pushMessageDTO.createMinTimeStr+'</td>';

            html +='        <td>'+receiverShopHtml+'</td>';
            html +='        <td><div style="width:370px;line-height:20px;"><div style="width:370px; line-height:20px;white-space: normal;cursor: pointer" class="showProductBtn" data-message-id="' + pushMessageDTO.idStr + '" data-productids="' + G.normalize(paramsJson["productLocalInfoIds"]) + '">' +pushMessageDTO.content+ '</div></td>';
            html +='        <td>'+pushMessageDTO.validStatusStr+'</td>';
            html +='    </tr>';




        });
        html +='</table>';
        $("#senderPushMessageTables").append(html);
    } else {
        var html = "";
        html +='<table width="796" border="0" cellspacing="0" class="news-table">';
        html +='    <tbody><tr>';
        html +='        <td valign="top"><span class="gray_color">暂无符合条件的消息！</span></td>';
        html +='    </tr>';
        html +='    </tbody></table>';
        $("#senderPushMessageTables").append(html);
    }
}
