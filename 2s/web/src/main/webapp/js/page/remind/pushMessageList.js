$(document).ready(function () {
    var messagePopup = App.Module.messagePopup;

    /*------------------checkbox  全选  start------------------------------ */
    $("#selectAll").bind("click",function() {
        $(".J_forDateSelectAll").attr("checked",this.checked);
        $(".J_itemSelectAll").attr("checked",this.checked);
    });
    $(".J_forDateSelectAll").live("click",function() {
        $(this).closest("table").find(".J_itemSelectAll").attr("checked",this.checked);
        var isSelectAll = $(".J_forDateSelectAll").length == $(".J_forDateSelectAll:checked").length ? true : false;
        $("#selectAll").attr("checked",isSelectAll);
    });
    $(".J_itemSelectAll").live("click",function() {
        var $currentShopDataBlock = $(this).closest("table");
        $currentShopDataBlock.find(".J_forDateSelectAll").attr("checked",$currentShopDataBlock.find(".J_forDateSelectAll").length == $currentShopDataBlock.find(".J_forDateSelectAll:checked").length ? true : false);
        var isSelectAll = $(".J_forDateSelectAll").length == $(".J_forDateSelectAll:checked").length ? true : false;
        $("#selectAll").attr("checked",isSelectAll);
    });
    /*------------------checkbox  全选  end------------------------------ */
    $(".J_optTable").live("click",function(e){
        if($(e.target).attr("type")!="checkbox"){
            var $optImg = $(this).find(".J_optImg");
            if($optImg.hasClass("drop-down")){
                $optImg.removeClass("drop-down");
                $optImg.addClass("drop-up");
                $(this).closest("table").find("tr:not(:first)").hide();
                return;
            }else if($optImg.hasClass("drop-up")){
                $optImg.removeClass("drop-up");
                $optImg.addClass("drop-down");
                $optImg.closest("table").find("tr:not(:first)").show();
                return;
            }
        }
    });

    $("#searchPushMessageBtn").bind("click",function(e){
        e.preventDefault();
        $("#relatedObjectId").val("");
        searchPushMessage();
    });
    function searchPushMessage(){
        $("#selectAll").attr("checked",false);
        $(".J_forDateSelectAll").attr("checked",false);
        $(".J_itemSelectAll").attr("checked",false);

        var param = $("#searchPushMessageForm").serializeArray();
        var data = {startPageNo: 1, maxRows: 20};
        $.each(param, function (index, val) {
            data[val.name] = val.value;
        });
        var url = "pushMessage.do?method=searchReceiverPushMessage";
        APP_BCGOGO.Net.asyncPost({
            url: url,
            data: data,
            dataType: "json",
            success: function (json) {
                var topCategory = $("#topCategory").val();
                var category = $("#category").val();
                $(".J_showAllPushMessage").text("显示所有"+parsePushMessageCategoryName(category,topCategory)+"消息");
                if (!G.isEmpty($("#keyWord").val()) ||!G.isEmpty($("#dayRange").val()) || $("#receiverStatus").attr("checked")) {
                    $(".J_showAllPushMessage").closest("div").show()
                }else{
                    $(".J_showAllPushMessage").closest("div").hide()
                }
                initPages(json, '_pushMessage', url, '', 'drawPushMessageTable', '', '', data, '');
                drawPushMessageTable(json);
            },
            error: function () {
                nsDialog.jAlert("数据异常!");
            }
        });
    }
    searchPushMessage();

    $(".J_showAllPushMessage").bind("click",function(e){
        $("#keyWord").val("");
        $("#receiverStatus").attr("checked",false);
        $("#dayRange").val("");
        $("#relatedObjectId").val("");
        searchPushMessage();
    });
    //请求操作
    $(".J_acceptCustomerApply").live("click", function () {
        if ($(this).attr("lock") || !$(this).attr("inviteId")) {
            return;
        }
        $(this).attr("lock", true);
        var params = {inviteId: $(this).attr("inviteId")},
            url = "apply.do?method=acceptCustomerApply", me = this;
        APP_BCGOGO.Net.asyncAjax({
            type: "POST",
            url: url,
            data: params,
            cache: false,
            dataType: "json",
            success: function (result) {
                if (result.success) {
                    nsDialog.jAlert("接受客户请求成功！");
                    searchPushMessage();
                    messagePopup.inquire();
                    refreshPushMessageCategoryStatNumber();
                    //
                } else {
                    nsDialog.jAlert(result.msg);
                }
                $(this).removeAttr("lock");
            }
        });
    });
    $(".J_acceptSupplierApply").live("click", function () {
        if ($(this).attr("lock") || !$(this).attr("inviteId")) {
            return;
        }
        $(this).attr("lock", true);
        var params = {inviteId: $(this).attr("inviteId")},
            url = "apply.do?method=acceptSupplierApply", me = this;
        APP_BCGOGO.Net.asyncAjax({
            type: "POST",
            url: url,
            data: params,
            cache: false,
            dataType: "json",
            success: function (result) {
                if (result.success) {
                    nsDialog.jAlert("接受供应商请求成功！");
                    searchPushMessage();
                    messagePopup.inquire();
                    refreshPushMessageCategoryStatNumber();
                } else {
                    nsDialog.jAlert(result.msg);
                }
                $(this).removeAttr("lock");
            }
        });
    });
    $(".J_refuseApply").live("click", function () {
        if ($(this).attr("lock") || !$(this).attr("inviteId")) {
            return;
        }
        $(this).attr("lock", true);
        var $refuseDom = $(this), me = this;
        $("#refuse_msg_dialog").dialog({
            resizable: false,
            title: "拒绝理由",
            height: 185,
            width: 300,
            modal: true,
            closeOnEscape: false,
            buttons: {
                "确定": function () {
                    var refuseMsg = G.isEmpty($("#refuse_msg").val()) ?"本公司经营范围与您的公司不符！" : $("#refuse_msg").val();
                    var params = {inviteId: $refuseDom.attr("inviteId"), refuseMsg: refuseMsg},
                        url = "apply.do?method=refuseApply";
                    APP_BCGOGO.Net.asyncAjax({
                        type: "POST",
                        url: url,
                        data: params,
                        cache: false,
                        dataType: "json",
                        success: function (result) {
                            if (result.success) {
                                nsDialog.jAlert("您已拒绝对方的请求！");
                                searchPushMessage();
                                messagePopup.inquire();
                                refreshPushMessageCategoryStatNumber();
                            } else {
                                nsDialog.jAlert(result.msg);
                            }
                        }
                    });
                    $(this).dialog("close");
                },
                "取消": function () {
                    $(this).dialog("close");
                }
            },
            close: function () {
                $refuseDom.removeAttr("lock");
            }
        });
    });

    $("#batchDeletePushMessageBtn").bind("click",function(e){
        e.preventDefault();
        var pushMessageReceiverIds =[];
        $(".J_itemSelectAll:checked").each(function(index){
            pushMessageReceiverIds.push($(this).attr("data-receiver-id"));
        });
        if(pushMessageReceiverIds.length==0){
            nsDialog.jAlert("请选择需要删除的消息！");
            return;
        }else{
            nsDialog.jConfirm("确认是否删除当前选中的消息?", "删除消息", function (returnVal) {
                if (returnVal) {
                    deletePushMessageByPushMessageReceiverId(pushMessageReceiverIds.join(","));
                }
            });
        }
    });

    $(".J_deletePushMessageBtn").live("click", function (e) {
        e.preventDefault();
        var receiverId = $(this).closest("tr").find(".J_itemSelectAll").attr("data-receiver-id");
        nsDialog.jConfirm("确认是否删除当前消息？", "删除消息", function (resultValue) {
            if (resultValue) {
                deletePushMessageByPushMessageReceiverId(receiverId);
            }
        });
    });
    function deletePushMessageByPushMessageReceiverId(pushMessageReceiverIds){
        APP_BCGOGO.Net.asyncPost({
            url: "pushMessage.do?method=deletePushMessageByPushMessageReceiverId",
            data: { pushMessageReceiverIds: pushMessageReceiverIds},
            dataType: "json",
            success: function (result) {
                if (result.success) {
                    searchPushMessage();
                    messagePopup.inquire();
                    refreshPushMessageCategoryStatNumber();
                }
            },
            error: function () {
                nsDialog.jAlert("数据异常!");
            }
        });
    }
    function parsePushMessageCategoryName(category,topCategory){
        var categoryName = "";

        if(!G.isEmpty(category)){
            switch (category) {
                case "RelatedApplyMessage":
                    var categoryName = "关联请求";
                    break;
                case "RelatedNoticeMessage":
                    var categoryName = "关联申请处理";
                    break;
                case "BuyingPushStatNoticeMessage":
                    var categoryName = "求购推送统计";
                    break;
                case "ProductPushStatNoticeMessage":
                    var categoryName = "商品推送统计";
                    break;
                case "QuotedBuyingIgnoredNoticeMessage":
                    var categoryName = "报价未采纳提示";
                    break;
                case "AppointNoticeMessage":
                    var categoryName = "预约提醒";
                    break;
                case "SystemNoticeMessage":
                    var categoryName = "系统通知";
                    break;
                case "QuotedBuyingInformationStationMessage":
                    var categoryName = "求购报价提醒";
                    break;
                case "RecommendProductStationMessage":
                    var categoryName = "推荐商品";
                    break;
                case "MatchStationMessage":
                    var categoryName = "匹配关联";
                    break;
                case "BuyingInformationStationMessage":
                    var categoryName = "客户商机";
                    break;
                default:
                    G.error("pushMessage category error![pushMessageList.js]");
                    break;
            }
        }else if(!G.isEmpty(topCategory)){
            switch (topCategory) {
                case "ApplyMessage":
                    var categoryName = "请求";
                    break;
                case "NoticeMessage":
                    var categoryName = "通知";
                    break;
                case "StationMessage":
                    var categoryName = "站内";
                    break;
                default:
                    G.error("pushMessage top category error![pushMessageList.js]");
                    break;
            }
        }
        return categoryName;
    }
    $("#markAllReadBtn").bind("click",function(e){
        e.preventDefault();
        var topCategory = $("#topCategory").val();
        var category = $("#category").val();
        var categoryName="<span style='color:#007CDA'>"+parsePushMessageCategoryName(category,topCategory)+"</span>"+"消息";

        nsDialog.jConfirm("确认是否将所有的"+categoryName+"标记为已读?", "全部标记已读", function (returnVal) {
            if (returnVal) {
                APP_BCGOGO.Net.asyncPost({
                    url: "pushMessage.do?method=readPushMessageByPushMessageCategory",
                    data: { topCategory: topCategory,category: category},
                    dataType: "json",
                    success: function (result) {
                        if (result.success) {
                            searchPushMessage();
                            messagePopup.inquire();
                            refreshPushMessageCategoryStatNumber();
                        }
                    },
                    error: function () {
                        nsDialog.jAlert("数据异常!");
                    }
                });
            }
        });
    });

    $("#markMultiReadBtn").bind("click",function(e){
        e.preventDefault();
        var pushMessageReceiverIds =[];
        $(".J_itemSelectAll:checked").each(function(index){
            pushMessageReceiverIds.push($(this).attr("data-receiver-id"));
        });
        if(pushMessageReceiverIds.length==0){
            nsDialog.jAlert("请选择需要标记为已读的消息！");
            return;
        }else{
            nsDialog.jConfirm("确认是否将当前选中的消息标记为已读?", "标记消息已读", function (returnVal) {
                if (returnVal) {
                    readPushMessageByPushMessageReceiverId(pushMessageReceiverIds.join(","));
                }
            });
        }
    });

    $(".J_readPushMessageBtn").live("click",function(e){
        e.preventDefault();
        var receiverId = $(this).closest("tr").find(".J_itemSelectAll").attr("data-receiver-id");
        readPushMessageByPushMessageReceiverId(receiverId);
    });
    function readPushMessageByPushMessageReceiverId(pushMessageReceiverIds){
        APP_BCGOGO.Net.asyncPost({
            url: "pushMessage.do?method=readPushMessageByPushMessageReceiverId",
            data: { pushMessageReceiverIds: pushMessageReceiverIds},
            dataType: "json",
            success: function (result) {
                if (result.success) {
                    searchPushMessage();
                    messagePopup.inquire();
                    refreshPushMessageCategoryStatNumber();
                }
            },
            error: function () {
                nsDialog.jAlert("数据异常!");
            }
        });
    }
    $(".J_showCustomerDetailBtn").live("click", function (e) {
        e.preventDefault();
        var customerId = $(this).attr("data-customer-id");
        if (!G.Lang.isEmpty(customerId)) {
            APP_BCGOGO.Net.syncPost({
                url: "customer.do?method=checkCustomerStatus",
                data: {customerId: customerId},
                dataType: "json",
                success: function (json) {
                    if (json.success) {
                        window.open("unitlink.do?method=customer&customerId=" + customerId, "_blank");
                    } else {
                        nsDialog.jAlert("当前客户已经被删除!");
                    }
                },
                error: function () {
                    nsDialog.jAlert("数据异常!");
                }
            });
        }
    });

    $(".J_showSupplierDetailBtn").live("click", function (e) {
        e.preventDefault();
        var supplierId = $(this).attr("data-supplier-id");
        if (!G.Lang.isEmpty(supplierId)) {
            APP_BCGOGO.Net.syncPost({
                url: "supplier.do?method=checkSupplierStatus",
                data: {supplierId: supplierId},
                dataType: "json",
                success: function (json) {
                    if (json.success) {
                        window.open("unitlink.do?method=supplier&supplierId=" + supplierId, "_blank");
                    } else {
                        nsDialog.jAlert("当前供应商已经被删除!");
                    }
                },
                error: function () {
                    nsDialog.jAlert("数据异常!");
                }
            });
        }
    });
    $(".J_showNeedMergeCustomerBtn").live("click", function (e) {
        e.preventDefault();
        var customerIds = $(this).attr("data-customer-ids");
        if (!G.Lang.isEmpty(customerIds)) {
            window.open("customer.do?method=customerdata&customerIds=" + customerIds, "_blank");
        }
    });

    $(".J_showNeedMergeSupplerBtn").live("click", function (e) {
        e.preventDefault();
        var supplierIds = $(this).attr("data-supplier-ids");
        if (!G.Lang.isEmpty(supplierIds)) {
            window.open("customer.do?method=searchSuppiler&supplierIds=" + supplierIds, "_blank");
        }
    });
    $(".J_showBuyingInfoDetailBtn").live("click", function (e) {
        e.preventDefault();
        var preBuyOrderId = $(this).attr("data-prebuyorder-id");
        if (!G.Lang.isEmpty(preBuyOrderId)) {
            window.open("preBuyOrder.do?method=showPreBuyOrderById&preBuyOrderId=" + preBuyOrderId, "_blank");
        }
    });
    $(".J_showProductDetailBtn").live("click", function (e) {
        e.preventDefault();
        var productShopId = $(this).attr("data-productshop-id");
        var productLocalInfoId = $(this).attr("data-productlocalinfo-id");
        if (!G.Lang.isEmpty(productShopId) && !G.Lang.isEmpty(productLocalInfoId)) {
            window.open("shopProductDetail.do?method=toShopProductDetail&paramShopId="+productShopId+"&productLocalId="+productLocalInfoId, "_blank");
        }
    });

    $(".J_showProductDetailByQuotedBtn").live("click", function (e) {
        e.preventDefault();
        var productShopId = $(this).attr("data-productshop-id");
        var productLocalInfoId = $(this).attr("data-productlocalinfo-id");
        var quotedPreBuyOrderItemId = $(this).attr("data-quotedprebuyorderitem-id");
        if (!G.Lang.isEmpty(productShopId) && !G.Lang.isEmpty(productLocalInfoId) && !G.Lang.isEmpty(quotedPreBuyOrderItemId)) {
            window.open("shopProductDetail.do?method=toShopProductDetail&paramShopId="+productShopId+"&productLocalId="+productLocalInfoId+"&quotedPreBuyOrderItemId="+quotedPreBuyOrderItemId, "_blank");
        }
    });
    $(".J_showQuotedBuyingDetailBtn").live("click", function (e) {
        e.preventDefault();
        var quotedPreBuyOrderItemId = $(this).attr("data-quotedprebuyorderitem-id");
        if (!G.Lang.isEmpty(quotedPreBuyOrderItemId)) {
            window.open("preBuyOrder.do?method=showBuyInformationDetailByQuotedPreBuyOrderItemId&quotedPreBuyOrderItemId="+quotedPreBuyOrderItemId, "_blank");
        }
    });

    $(".J_showMyPreBuyOrderDetailBtn").live("click", function (e) {
        e.preventDefault();
        var preBuyOrderId = $(this).attr("data-prebuyorderid-id");

        if (!G.Lang.isEmpty(preBuyOrderId)) {
            window.open("preBuyOrder.do?method=showPreBuyOrderById&preBuyOrderId="+preBuyOrderId, "_blank");
        }
    });

    $(".J_showBuyInformationDetailBtn").live("click", function (e) {
        e.preventDefault();
        var preBuyOrderItemId = $(this).attr("data-prebuyorderitem-id");
        if (!G.Lang.isEmpty(preBuyOrderItemId)) {
            window.open("preBuyOrder.do?method=showBuyInformationDetailByPreBuyOrderItemId&preBuyOrderItemId="+preBuyOrderItemId, "_blank");
        }
    });

    $("#sendMsgPromptForm").find("input[name='smsFlag'],input[name='appFlag']").bind("click", function (e) {
        if($("#sendMsgPromptForm").find("input[name='smsFlag']:checked,input[name='appFlag']:checked").length<2){
            $("#sendMsgPromptForm").find("input[name='smsFlag']:checked,input[name='appFlag']:checked").attr("disabled","disabled");
        }else{
            $("#sendMsgPromptForm").find("input[name='smsFlag']:checked,input[name='appFlag']:checked").removeAttr("disabled");
        }
    });

    $("#sendMsgPromptBtn").bind("click", function (e) {
      e.preventDefault();
      if (checkSendMsgPromptMobile()) {
        $("#sendMsgPromptForm").find("input[name='smsFlag']").removeAttr("disabled");
        $("#sendMsgPromptForm").find("input[name='appFlag']").removeAttr("disabled");
        var paramForm = $("#sendMsgPromptForm").serializeArray();
        var param = {};
        $.each(paramForm, function (index, val) {
          param[val.name] = val.value;
        });

        param['remindEventId']=$("#sendMsgPromptForm").find("input[name='remindEventId']").val();
        param['mobile']=$("#sendMsgPromptForm").find("input[name='mobile']").val();;

        $("#sendMsgPrompt").dialog("close");
        APP_BCGOGO.Net.asyncPost({
          url: "customer.do?method=sendVehicleMsg",
          dataType: "json",
          data: param,
          success: function (json) {
            if (G.isNotEmpty(json)) {
              if (json.success) {
                nsDialog.jAlert("短信发送成功！");
                window.location.reload();
              } else {
                nsDialog.jAlert(json.msg);
              }
            } else {
              nsDialog.jAlert("网络异常，请联系客服");
            }
          },
          error: function () {
            nsDialog.jAlert("网络异常，请联系客服");
          }
        });
      }
    });

    $(".J_closeSendMsgPrompt").bind("click", function (e) {
      e.preventDefault();
      $("#sendMsgPrompt").dialog("close");
    });




    $(".J_showEnquiryDetailBtn").live("click", function (e) {
        e.preventDefault();
        var id = $(this).attr("data-enquiry-id");
        if (!G.Lang.isEmpty(id)) {
            window.open("enquiry.do?method=showEnquiryDetail&enquiryId="+id, "_blank");
        }
    });
    $(".J_showAppointDetailBtn").live("click", function (e) {
        e.preventDefault();
        var appointOrderId = $(this).attr("data-appointorder-id");
        if (!G.Lang.isEmpty(appointOrderId)) {
            window.open("appoint.do?method=showAppointOrderDetail&appointOrderId="+appointOrderId, "_blank");
        }
    });
    $(".J_showShopDetailBtn").live("click", function (e) {
        e.preventDefault();
        var shopId = $(this).attr("data-shop-id");
        if (!G.Lang.isEmpty(shopId)) {
            window.open("shopMsgDetail.do?method=renderShopMsgDetail&paramShopId="+shopId, "_blank");
        }
    });

    $(".J_showProductListBtn").live("click", function () {
        var productLocalInfoIds = $(this).attr("data-productids");
        if(productLocalInfoIds.endWith(",")){
            productLocalInfoIds = productLocalInfoIds.substring(0,productLocalInfoIds.length - 1);
        }
        var messageId = $(this).attr("data-message-id");
        if (!G.isEmpty(messageId) &&!G.isEmpty(productLocalInfoIds)) {
            //校验商品有没有全部被下架
            APP_BCGOGO.Net.syncPost({
                url: "message.do?method=validateProduct",
                data: {productIds: productLocalInfoIds, messageId: messageId},
                dataType: "json",
                success: function (json) {
                    if (json.success && json.data) {
                        if(json.data=="redirectToCommodityQuotations") {
                            window.open("autoAccessoryOnline.do?method=toCommodityQuotations&productIds=" + productLocalInfoIds);
                        } else if(json.data=="redirectToStockSearch") {
                            window.open("stockSearch.do?method=getStockSearch&productIds=" + productLocalInfoIds + "&fromPage=stationMessage");
                        }

                    } else {
                        nsDialog.jAlert(json.msg);
                    }
                },
                error: function () {
                    nsDialog.jAlert("数据异常!");
                }
            });

        }
    });
});


function sendVehicleMaintainMessage(content,sendMobile){
  $("#sendMsgPrompt").dialog({
    width: 430,
    modal: true,
    resizable: false,
    position: 'center',
    open: function () {
      var licenceNo =  content.substring(content.indexOf("爱车（") + 3,content.length-1).substring(0,content.substring(content.indexOf("爱车（") + 3,content.length-1).indexOf("）"));
      $("#sendMsgPromptForm").find("input[name='remindEventId']").val("");
      $("#sendMsgPromptForm").find("div[id='vehicleMsgContent']").html(content);
      $("#sendMsgPromptForm").find("input[name='licenceNo']").val(licenceNo);
      $(".ui-dialog-titlebar", $(this).parent()).hide();
      $(this).removeClass("ui-dialog-content").removeClass("ui-widget-content");
      if (G.isNotEmpty(sendMobile)) {
        $("#sendMsgPromptForm").find("input[name='mobile']").val(sendMobile);
        $("#sendMsgPromptForm").find("input[name='mobile']").attr("disabled", "disabled");
      }
      checkSendMsgPromptMobile();
    },
    close: function () {
      $("#sendMsgPromptForm").find("input[name='smsFlag']").attr("disabled", "disabled");
      $("#sendMsgPromptForm").find("input[name='appFlag']").removeAttr("disabled");
      $("#sendMsgPromptForm").find("input[name='mobile']").removeAttr("disabled");
      $("#sendMsgPromptForm")[0].reset();
    }
  });
}


function checkSendMsgPromptMobile() {
  var mobile = $("#sendMsgPromptForm").find("input[name='mobile']").val();
  if (G.isEmpty(mobile)) {
    $("#sendMsgPromptForm").find("div[id='mobileWrongInfo']").hide();
    $("#sendMsgPromptForm").find("div[id='mobileEmptyInfo']").show();
    $("#sendMsgPromptForm").find("div[id='mobileRightInfo']").hide();
    return false;
  }
  if (!APP_BCGOGO.Validator.stringIsMobilePhoneNumber(mobile)) {
    $("#sendMsgPromptForm").find("div[id='mobileWrongInfo']").show();
    $("#sendMsgPromptForm").find("div[id='mobileEmptyInfo']").hide();
    $("#sendMsgPromptForm").find("div[id='mobileRightInfo']").hide();
    return false;
  }
  $("#sendMsgPromptForm").find("div[id='mobileWrongInfo']").hide();
  $("#sendMsgPromptForm").find("div[id='mobileEmptyInfo']").hide();
  $("#sendMsgPromptForm").find("div[id='mobileRightInfo']").show();
  return true;
}

function parseRelationInviteStatusValue(status){
    if(status=="PENDING") return "待处理";
    if(status=="ACCEPTED") return "已接受";
    if(status=="REFUSED") return "已拒绝";
}

function parseRelatedNoticeMessageStatusValue(type){
    if(type=="CANCEL_ASSOCIATION_NOTICE") return "已取消";
    if(type=="ASSOCIATION_REJECT_NOTICE") return "已拒绝";
    if(type=="CUSTOMER_ACCEPT_TO_SUPPLIER"
        || type=="SUPPLIER_ACCEPT_TO_CUSTOMER"
        || type=="SUPPLIER_ACCEPT_TO_SUPPLIER"
        || type=="CUSTOMER_ACCEPT_TO_CUSTOMER") return "已接受";
}

function parsePushMessageCategoryValue(type){
    if(type=="ASSOCIATION_REJECT_NOTICE"
        || type=="CANCEL_ASSOCIATION_NOTICE"
        || type=="CUSTOMER_ACCEPT_TO_SUPPLIER"
        || type=="SUPPLIER_ACCEPT_TO_CUSTOMER"
        || type=="SUPPLIER_ACCEPT_TO_SUPPLIER"
        || type=="CUSTOMER_ACCEPT_TO_CUSTOMER") return "RelatedNoticeMessage";

    if(type=="APPLY_SUPPLIER" || type=="APPLY_CUSTOMER") return "RelatedApplyMessage";
    if(type=="BUYING_INFORMATION_MATCH_RESULT") return "BuyingPushStatNoticeMessage";
    if(type=="ACCESSORY_MATCH_RESULT") return "ProductPushStatNoticeMessage";
    if(type=="QUOTED_BUYING_IGNORED") return "QuotedBuyingIgnoredNoticeMessage";
    if(type=="APP_CANCEL_APPOINT" || type=="APP_APPLY_APPOINT" || type=="OVERDUE_APPOINT_TO_SHOP" || type=="SOON_EXPIRE_APPOINT_TO_SHOP" || type=="SYS_ACCEPT_APPOINT") return "AppointNoticeMessage";
    if(type=="ANNOUNCEMENT" || type=="FESTIVAL") return "SystemNoticeMessage";
    if(type=="QUOTED_BUYING_INFORMATION") return "QuotedBuyingInformationStationMessage";
    if(type=="BUYING_MATCH_ACCESSORY" || type=="ACCESSORY" || type=="ACCESSORY_PROMOTIONS" || type=="PROMOTIONS_MESSAGE" || type=="WARN_MESSAGE" || type=="RECOMMEND_ACCESSORY_BY_QUOTED") return "RecommendProductStationMessage";
    if(type=="MATCHING_RECOMMEND_CUSTOMER" || type=="MATCHING_RECOMMEND_SUPPLIER") return "MatchStationMessage";
    if(type == "BUYING_INFORMATION" || type=="BUSINESS_CHANCE_SELL_WELL" || type=="BUSINESS_CHANCE_LACK" || type == "APP_SUBMIT_ENQUIRY"  || type == "VEHICLE_FAULT_2_SHOP"
        || type == "APP_VEHICLE_SOON_EXPIRE_MAINTAIN_MILEAGE_2_SHOP" || type == "APP_VEHICLE_SOON_EXPIRE_MAINTAIN_TIME_2_SHOP"
        || type == "APP_VEHICLE_SOON_EXPIRE_INSURANCE_TIME_2_SHOP"|| type == "APP_VEHICLE_SOON_EXPIRE_EXAMINE_TIME_2_SHOP"
        || type == "APP_VEHICLE_OVERDUE_MAINTAIN_MILEAGE_2_SHOP" || type == "APP_VEHICLE_OVERDUE_MAINTAIN_TIME_2_SHOP"
        || type == "APP_VEHICLE_OVERDUE_INSURANCE_TIME_2_SHOP"|| type == "APP_VEHICLE_OVERDUE_EXAMINE_TIME_2_SHOP"
        ) return "BuyingInformationStationMessage";
}
function drawPushMessageTable(json) {
    $("#pushMessageTables > table").remove();
    if (!G.isEmpty(json) && !G.isEmpty(json[0])) {
        var html="";
        $.each(json[0], function(key, value) {
            html +='<table width="796" border="0" cellspacing="0" class="news-table">';
            html +='    <colgroup valign="top">';
            html +='        <col width="30" />';
            html +='        <col/>';
            html +='        <col width="100" />';
            html +='        <col width="40" />';
            html +='    </colgroup>';
            html +='    <tr class="news-thbody-c J_optTable" style="cursor: pointer">';
            html +='        <td><input type="checkbox" class="J_forDateSelectAll"/></td>';
            html +='        <td>日期：'+key+' （共 '+value.length+' 条）</td>';
            html +='        <td>&nbsp;</td>';
            html +='        <td><div class="drop-down J_optImg"></div></td>';
            html +='    </tr>';
            $.each(value, function (index, pushMessageDTO) {
                var params = {};
                if(!G.isEmpty(pushMessageDTO.params)){
                    params = JSON.parse(pushMessageDTO.params) || {};
                }
                var currentPushMessageReceiverDTO = pushMessageDTO.currentPushMessageReceiverDTO;
                var pushMessageType = pushMessageDTO.type;
                var pushMessageCategory = parsePushMessageCategoryValue(pushMessageType);
                if(pushMessageCategory == "RelatedApplyMessage"){
                    if(pushMessageType == "APPLY_SUPPLIER"){
                        var shopRelationInviteDTO = pushMessageDTO.shopRelationInviteDTO;
                        var shopRelationInviteId = shopRelationInviteDTO["idStr"],
                            shopRelationInviteStatus = shopRelationInviteDTO["status"],
                            operationMan = shopRelationInviteDTO["operationMan"],
                            operationTime = shopRelationInviteDTO["operationTime"];

                        html +='    <tr>';
                        html +='        <td valign="top"><div class="news-01">';
                        html +='            <input type="checkbox" data-receiver-id="'+currentPushMessageReceiverDTO.idStr+'" class="J_itemSelectAll"/>';
                        html +='        </div></td>';
                        html +='        <td><div class="'+(currentPushMessageReceiverDTO.status=="UNREAD"?'news-unread J_readPushMessageBtn':'news-read')+'"></div><div class="news-div">供应商关联请求（'+parseRelationInviteStatusValue(shopRelationInviteStatus)+'）：<span class="gay_color">（'+pushMessageDTO.createMinTimeStr+'）</span><br/>'+pushMessageDTO.content;
                        if(shopRelationInviteStatus=="PENDING"){
                            html +='    <input type="button" value="同 意" class="query-btn J_acceptSupplierApply" inviteId="' + shopRelationInviteId + '">';
                            html +='    <input type="button" value="拒 绝" class="query-btn J_refuseApply" inviteId="' + shopRelationInviteId + '" >';
                        }else{
                            html +='已被'+operationMan + '于' + dateUtil.formatDate(new Date(operationTime), dateUtil.dateStringFormatDayHourMin)+(shopRelationInviteStatus == 'ACCEPTED' ? "接受" : "拒绝");
                        }
                        html +='        </div></td>';
                        html +='        <td></td>';
                        if(APP_BCGOGO.Permission.Schedule.MessageCenter.ReceiverDelete){
                            html +='        <td><div class="x-colse J_deletePushMessageBtn"></div></td>';
                        }else{
                            html +='        <td></td>';
                        }
                        html +='    </tr>';
                    }else if(pushMessageType == "APPLY_CUSTOMER"){
                        var shopRelationInviteDTO = pushMessageDTO.shopRelationInviteDTO;
                        var shopRelationInviteId = shopRelationInviteDTO["idStr"],
                            shopRelationInviteStatus = shopRelationInviteDTO["status"],
                            operationMan = shopRelationInviteDTO["operationMan"],
                            operationTime = shopRelationInviteDTO["operationTime"];
                        html +='    <tr>';
                        html +='        <td valign="top"><div class="news-01">';
                        html +='            <input type="checkbox" data-receiver-id="'+currentPushMessageReceiverDTO.idStr+'"  class="J_itemSelectAll"/>';
                        html +='        </div></td>';
                        html +='        <td><div class="'+(currentPushMessageReceiverDTO.status=="UNREAD"?'news-unread J_readPushMessageBtn':'news-read')+'"></div><div class="news-div">客户关联请求（'+parseRelationInviteStatusValue(shopRelationInviteStatus)+'）：<span class="gay_color">（'+pushMessageDTO.createMinTimeStr+'）</span><br/>'+pushMessageDTO.content;
                        if(shopRelationInviteStatus=="PENDING"){
                            html +='    <input type="button" value="同 意" class="query-btn J_acceptCustomerApply" inviteId="' + shopRelationInviteId + '">';
                            html +='    <input type="button" value="拒 绝" class="query-btn J_refuseApply" inviteId="' + shopRelationInviteId + '" >';
                        }else{
                            html +='已被'+operationMan + '于' + dateUtil.formatDate(new Date(operationTime), dateUtil.dateStringFormatDayHourMin)+(shopRelationInviteStatus == 'ACCEPTED' ? "接受" : "拒绝");
                        }
                        html +='        </div></td>';
                        html +='        <td></td>';
                        if(APP_BCGOGO.Permission.Schedule.MessageCenter.ReceiverDelete){
                            html +='        <td><div class="x-colse J_deletePushMessageBtn"></div></td>';
                        }else{
                            html +='        <td></td>';
                        }
                        html +='    </tr>';
                    }
                }else if(pushMessageCategory=="RelatedNoticeMessage"){
                    html +='    <tr>';
                    html +='        <td valign="top"><div class="news-01">';
                    html +='            <input type="checkbox" data-receiver-id="'+currentPushMessageReceiverDTO.idStr+'"  class="J_itemSelectAll"/>';
                    html +='        </div></td>';
                    html +='        <td><div class="'+(currentPushMessageReceiverDTO.status=="UNREAD"?'news-unread J_readPushMessageBtn':'news-read')+'"></div><div class="news-div">关联申请处理通知（'+parseRelatedNoticeMessageStatusValue(pushMessageType)+'）：<span class="gay_color">（'+pushMessageDTO.createMinTimeStr+'）</span><br/>';
                    html +='       '+pushMessageDTO.content;

                    if(pushMessageType == "CUSTOMER_ACCEPT_TO_SUPPLIER"|| pushMessageType == "SUPPLIER_ACCEPT_TO_SUPPLIER"){
                        var similarCustomerIds = params["similarCustomerIds"]
                        if(!G.isEmpty(similarCustomerIds) && similarCustomerIds.split(',').length>1){
                            html +='       '+'<br>友情提示：该客户与您列表中客户相似，请前往确认是否重复，若是则可进行客户合并！<a class="blue_color J_showNeedMergeCustomerBtn" data-customer-ids='+similarCustomerIds+'>点击查看</a>'+'</div></td>';
                        }else{
                            html +='</div></td>';
                        }

                        html +='        <td><a class="blue_color J_showCustomerDetailBtn" data-customer-id='+G.normalize(params["customerId"])+'>查看客户详情</a></td>';
                    }else if(pushMessageType == "SUPPLIER_ACCEPT_TO_CUSTOMER" || pushMessageType=="CUSTOMER_ACCEPT_TO_CUSTOMER"){
                        var similarSupplierIds = params["similarSupplierIds"]
                        if(!G.isEmpty(similarSupplierIds) && similarSupplierIds.split(',').length>1){
                            html +='       '+'<br>友情提示：该供应商与您列表中供应商相似，请前往确认是否重复，若是则可进行供应商合并！<a class="blue_color J_showNeedMergeSupplerBtn" data-supplier-ids='+similarSupplierIds+'>点击查看</a>'+'</div></td>';
                        }else{
                            html +='</div></td>';
                        }
                        html +='        <td><a class="blue_color J_showSupplierDetailBtn" data-supplier-id='+G.normalize(params["supplierId"])+'>查看供应商详情</a></td>';
                    }else{
                        html +='</div></td>';
                        html +='        <td></td>';
                    }
                    if(APP_BCGOGO.Permission.Schedule.MessageCenter.ReceiverDelete){
                        html +='        <td><div class="x-colse J_deletePushMessageBtn"></div></td>';
                    }else{
                        html +='        <td></td>';
                    }
                    html +='    </tr>';
                }else if(pushMessageCategory=="BuyingPushStatNoticeMessage"){
                    html +='    <tr>';
                    html +='        <td valign="top"><div class="news-01">';
                    html +='            <input type="checkbox" data-receiver-id="'+currentPushMessageReceiverDTO.idStr+'"  class="J_itemSelectAll"/>';
                    html +='        </div></td>';
                    html +='        <td><div class="'+(currentPushMessageReceiverDTO.status=="UNREAD"?'news-unread J_readPushMessageBtn':'news-read')+'"></div><div class="news-div">求购推送统计信息：<span class="gay_color">（'+pushMessageDTO.createMinTimeStr+'）</span><br/>';
                    html +='       '+pushMessageDTO.content+'</div></td>';
                    html +='        <td><a class="blue_color J_showBuyingInfoDetailBtn" data-prebuyorder-id="'+G.normalize(params["preBuyOrderId"])+'">查看求购详情</a></td>';
                    if(APP_BCGOGO.Permission.Schedule.MessageCenter.ReceiverDelete){
                        html +='        <td><div class="x-colse J_deletePushMessageBtn"></div></td>';
                    }else{
                        html +='        <td></td>';
                    }
                    html +='    </tr>';
                }else if(pushMessageCategory=="ProductPushStatNoticeMessage"){
                    html +='    <tr>';
                    html +='        <td valign="top"><div class="news-01">';
                    html +='            <input type="checkbox" data-receiver-id="'+currentPushMessageReceiverDTO.idStr+'"  class="J_itemSelectAll"/>';
                    html +='        </div></td>';
                    html +='        <td><div class="'+(currentPushMessageReceiverDTO.status=="UNREAD"?'news-unread J_readPushMessageBtn':'news-read')+'"></div><div class="news-div">商品推送统计信息：<span class="gay_color">（'+pushMessageDTO.createMinTimeStr+'）</span><br/>';
                    html +='       '+pushMessageDTO.content+'</div></td>';
                    html +='        <td><a class="blue_color J_showProductDetailBtn" data-productshop-id="'+G.normalize(params["productShopId"])+'"data-productlocalinfo-id="'+G.normalize(params["productLocalInfoId"])+'">查看商品详情</a></td>';
                    if(APP_BCGOGO.Permission.Schedule.MessageCenter.ReceiverDelete){
                        html +='        <td><div class="x-colse J_deletePushMessageBtn"></div></td>';
                    }else{
                        html +='        <td></td>';
                    }
                    html +='    </tr>';
                }else if(pushMessageCategory=="QuotedBuyingIgnoredNoticeMessage"){
                    html +='    <tr>';
                    html +='        <td valign="top"><div class="news-01">';
                    html +='            <input type="checkbox" data-receiver-id="'+currentPushMessageReceiverDTO.idStr+'"  class="J_itemSelectAll"/>';
                    html +='        </div></td>';
                    html +='       <td><div class="'+(currentPushMessageReceiverDTO.status=="UNREAD"?'news-unread J_readPushMessageBtn':'news-read')+'"></div><div class="news-div">求购报价未采纳提示：<span class="gay_color">（'+pushMessageDTO.createMinTimeStr+'）</span><br/>';
                    html +='       '+pushMessageDTO.content+'</div></td>';
                    html +='        <td><a class="blue_color J_showQuotedBuyingDetailBtn" data-quotedprebuyorderitem-id="'+G.normalize(params["quotedPreBuyOrderItemId"])+'">查看报价详情</a></td>';
                    if(APP_BCGOGO.Permission.Schedule.MessageCenter.ReceiverDelete){
                        html +='        <td><div class="x-colse J_deletePushMessageBtn"></div></td>';
                    }else{
                        html +='        <td></td>';
                    }
                    html +='    </tr>';
                }else if(pushMessageCategory=="AppointNoticeMessage"){
                    var subTitle="预约通知";
                    if(pushMessageType=="APP_APPLY_APPOINT"){
                        subTitle="预约请求通知";
                    }else if(pushMessageType=="SOON_EXPIRE_APPOINT_TO_SHOP"){
                        subTitle ="到期预约通知"
                    }else if(pushMessageType=="OVERDUE_APPOINT_TO_SHOP"){
                        subTitle ="过期预约通知"
                    }else if(pushMessageType=="APP_CANCEL_APPOINT"){
                        subTitle ="取消预约通知"
                    }
                    html +='    <tr>';
                    html +='        <td valign="top"><div class="news-01">';
                    html +='            <input type="checkbox" data-receiver-id="'+currentPushMessageReceiverDTO.idStr+'"  class="J_itemSelectAll"/>';
                    html +='        </div></td>';
                    html +='        <td><div class="'+(currentPushMessageReceiverDTO.status=="UNREAD"?'news-unread J_readPushMessageBtn':'news-read')+'"></div><div class="news-div">'+subTitle+'：<span class="gay_color">（'+pushMessageDTO.createMinTimeStr+'）</span><br/>';
                    html +='       '+pushMessageDTO.content+'</div></td>';
                    html +='        <td><a class="blue_color J_showAppointDetailBtn" data-appointorder-id="'+G.normalize(params["appointOrderId"])+'">查看预约详情</a></td>';
                    if(APP_BCGOGO.Permission.Schedule.MessageCenter.ReceiverDelete){
                        html +='        <td><div class="x-colse J_deletePushMessageBtn"></div></td>';
                    }else{
                        html +='        <td></td>';
                    }
                    html +='    </tr>';
                }else if(pushMessageCategory=="SystemNoticeMessage"){
                    if(pushMessageType=="ANNOUNCEMENT"){
                        html +='    <tr>';
                        html +='        <td valign="top"><div class="news-01">';
                        html +='            <input type="checkbox" data-receiver-id="'+currentPushMessageReceiverDTO.idStr+'"  class="J_itemSelectAll"/>';
                        html +='        </div></td>';
                        html +='        <td><div class="'+(currentPushMessageReceiverDTO.status=="UNREAD"?'news-unread J_readPushMessageBtn':'news-read')+'"></div><div class="news-div">系统通知：<span class="gay_color">（'+pushMessageDTO.createMinTimeStr+'）</span><br/>';
                        html +='       '+pushMessageDTO.content+'</div></td>';
                        html +='        <td><a class="blue_color" href="sysReminder.do?method=toSysAnnouncement" target="_blank">查看详情</a></td>';
                        if(APP_BCGOGO.Permission.Schedule.MessageCenter.ReceiverDelete){
                            html +='        <td><div class="x-colse J_deletePushMessageBtn"></div></td>';
                        }else{
                            html +='        <td></td>';
                        }
                        html +='    </tr>';
                    }else if(pushMessageType=="FESTIVAL"){
                        html +='    <tr>';
                        html +='        <td valign="top"><div class="news-01">';
                        html +='            <input type="checkbox" data-receiver-id="'+currentPushMessageReceiverDTO.idStr+'"  class="J_itemSelectAll"/>';
                        html +='        </div></td>';
                        html +='        <td><div class="'+(currentPushMessageReceiverDTO.status=="UNREAD"?'news-unread J_readPushMessageBtn':'news-read')+'"></div><div class="news-div">节日提醒：<span class="gay_color">（'+pushMessageDTO.createMinTimeStr+'）</span><br/>';
                        html +='       '+pushMessageDTO.content+'</div></td>';
                        html +='        <td><a class="blue_color" href="sms.do?method=smswrite" target="_blank">马上去发短信</a></td>';
                        if(APP_BCGOGO.Permission.Schedule.MessageCenter.ReceiverDelete){
                            html +='        <td><div class="x-colse J_deletePushMessageBtn"></div></td>';
                        }else{
                            html +='        <td></td>';
                        }
                        html +='    </tr>';
                    }

                } else if(pushMessageCategory=="QuotedBuyingInformationStationMessage"){
                    html +='    <tr>';
                    html +='        <td valign="top"><div class="news-01">';
                    html +='            <input type="checkbox" data-receiver-id="'+currentPushMessageReceiverDTO.idStr+'"  class="J_itemSelectAll"/>';
                    html +='        </div></td>';
                    html +='        <td><div class="'+(currentPushMessageReceiverDTO.status=="UNREAD"?'news-unread J_readPushMessageBtn':'news-read')+'"></div><div class="news-div">求购报价提醒消息：<span class="gay_color">（'+pushMessageDTO.createMinTimeStr+'）</span><br/>';
                    html +='       '+pushMessageDTO.content+'</div></td>';
                    html +='        <td><a class="blue_color J_showMyPreBuyOrderDetailBtn" data-prebuyorderid-id ="'+G.normalize(params["preBuyOrderId"])+'">查看报价详情</a></td>';
                    if(APP_BCGOGO.Permission.Schedule.MessageCenter.ReceiverDelete){
                        html +='        <td><div class="x-colse J_deletePushMessageBtn"></div></td>';
                    }else{
                        html +='        <td></td>';
                    }
                    html +='    </tr>';
                } else if(pushMessageCategory=="RecommendProductStationMessage"){
                    var subTitle = "商品推荐消息";
                    if(pushMessageType=="BUYING_MATCH_ACCESSORY"){
                        subTitle="求购匹配商品推荐";
                    }else if(pushMessageType=="ACCESSORY"){
                        subTitle ="匹配商品推荐消息"
                    }else if(pushMessageType=="ACCESSORY_PROMOTIONS"){
                        subTitle ="促销商品推荐消息"
                    }else if(pushMessageType=="PROMOTIONS_MESSAGE" || pushMessageType=="WARN_MESSAGE"){
                        subTitle = "商家促销消息";
                    }else if(pushMessageType=="RECOMMEND_ACCESSORY_BY_QUOTED"){
                        subTitle = "供应商推荐商品消息";
                    }
                    html +='    <tr>';
                    html +='        <td valign="top"><div class="news-01">';
                    html +='            <input type="checkbox" data-receiver-id="'+currentPushMessageReceiverDTO.idStr+'"  class="J_itemSelectAll"/>';
                    html +='        </div></td>';
                    html +='        <td><div class="'+(currentPushMessageReceiverDTO.status=="UNREAD"?'news-unread J_readPushMessageBtn':'news-read')+'"></div><div class="news-div">'+subTitle+'：<span class="gay_color">（'+pushMessageDTO.createMinTimeStr+'）</span><br/>';
                    html +='       '+pushMessageDTO.content+'</div></td>';
                    if(pushMessageType=="PROMOTIONS_MESSAGE" || pushMessageType=="WARN_MESSAGE"){
                        if(G.isEmpty(params["productLocalInfoIds"])){
                            html +='        <td></td>';
                        }else{
                            html +='        <td><a class="blue_color J_showProductListBtn" data-message-id="' + pushMessageDTO.idStr + '" data-productids="' + G.normalize(params["productLocalInfoIds"])+'">查看商品列表</a></td>';
                        }
                    }else if(pushMessageType=="RECOMMEND_ACCESSORY_BY_QUOTED"){
                        html +='        <td><a class="blue_color J_showProductDetailByQuotedBtn" data-productshop-id="'+G.normalize(params["productShopId"])+'" data-productlocalinfo-id="'+G.normalize(params["productLocalInfoId"])+'" data-quotedprebuyorderitem-id="'+G.normalize(params["quotedPreBuyOrderItemId"])+'">查看商品详情</a></td>';
                    }else{
                        html +='        <td><a class="blue_color J_showProductDetailBtn" data-productshop-id="'+G.normalize(params["productShopId"])+'" data-productlocalinfo-id="'+G.normalize(params["productLocalInfoId"])+'">查看商品详情</a></td>';
                    }
                    if(APP_BCGOGO.Permission.Schedule.MessageCenter.ReceiverDelete){
                        html +='        <td><div class="x-colse J_deletePushMessageBtn"></div></td>';
                    }else{
                        html +='        <td></td>';
                    }
                    html +='    </tr>';
                } else if(pushMessageCategory=="MatchStationMessage"){
                    var subTitle = "匹配消息";
                    if(pushMessageType=="MATCHING_RECOMMEND_CUSTOMER"){
                        subTitle ="匹配客户消息";
                    }else if(pushMessageType=="MATCHING_RECOMMEND_SUPPLIER"){
                        subTitle ="匹配供应商消息"
                    }
                    html +='    <tr>';
                    html +='        <td valign="top"><div class="news-01">';
                    html +='            <input type="checkbox" data-receiver-id="'+currentPushMessageReceiverDTO.idStr+'"  class="J_itemSelectAll"/>';
                    html +='        </div></td>';
                    html +='        <td><div class="'+(currentPushMessageReceiverDTO.status=="UNREAD"?'news-unread J_readPushMessageBtn':'news-read')+'"></div><div class="news-div">'+subTitle+'：<span class="gay_color">（'+pushMessageDTO.createMinTimeStr+'）</span><br/>';
                    html +='       '+pushMessageDTO.content+'</div></td>';
                    html +='        <td><a class="blue_color J_showShopDetailBtn" data-shop-id ="'+ G.normalize(params["shopId"])+'">查看店铺资料</a></td>';
                    if(APP_BCGOGO.Permission.Schedule.MessageCenter.ReceiverDelete){
                        html +='        <td><div class="x-colse J_deletePushMessageBtn"></div></td>';
                    }else{
                        html +='        <td></td>';
                    }
                    html +='    </tr>';
                } else if(pushMessageCategory=="BuyingInformationStationMessage"){
                    var subTitle = "";
                    if (pushMessageType.search(/^BUYING_INFORMATION$|^BUSINESS_CHANCE_SELL_WELL$|^BUSINESS_CHANCE_LACK$/g) != -1) {
                        if(pushMessageType=="BUSINESS_CHANCE_SELL_WELL"){
                            subTitle ="客户畅销品商机信息";
                        }else if(pushMessageType=="BUSINESS_CHANCE_LACK"){
                            subTitle ="客户缺料商机信息"
                        }else if(pushMessageType=="BUYING_INFORMATION"){
                            subTitle ="客户求购商机信息"
                        }
                        html += '    <tr>';
                        html += '        <td valign="top"><div class="news-01">';
                        html += '            <input type="checkbox" data-receiver-id="' + currentPushMessageReceiverDTO.idStr + '"  class="J_itemSelectAll"/>';
                        html += '        </div></td>';
                        html += '        <td><div class="' + (currentPushMessageReceiverDTO.status == "UNREAD" ? 'news-unread J_readPushMessageBtn' : 'news-read') + '"></div><div class="news-div">' + subTitle + '：<span class="gay_color">（' + pushMessageDTO.createMinTimeStr + '）</span><br/>';
                        html += '       ' + pushMessageDTO.content + '</div></td>';
                        html += '        <td><a class="blue_color J_showBuyInformationDetailBtn" data-prebuyorderitem-id ="' + G.normalize(params["preBuyOrderItemId"]) + '">查看商机详情</a></td>';
                        if (APP_BCGOGO.Permission.Schedule.MessageCenter.ReceiverDelete) {
                            html += '        <td><div class="x-colse J_deletePushMessageBtn"></div></td>';
                        } else {
                            html += '        <td></td>';
                        }
                        html += '    </tr>';
                    } else if (pushMessageType == "APP_SUBMIT_ENQUIRY") {
                        subTitle = "客户询价信息";
                        html += '    <tr>';
                        html += '        <td valign="top"><div class="news-01">';
                        html += '            <input type="checkbox" data-receiver-id="' + currentPushMessageReceiverDTO.idStr + '"  class="J_itemSelectAll"/>';
                        html += '        </div></td>';
                        html += '        <td><div class="' + (currentPushMessageReceiverDTO.status == "UNREAD" ? 'news-unread J_readPushMessageBtn' : 'news-read') + '"></div><div class="news-div">' + subTitle + '：<span class="gay_color">（' + pushMessageDTO.createMinTimeStr + '）</span><br/>';
                        html += '       ' + pushMessageDTO.content + '</div></td>';
                        html += '        <td><a class="blue_color J_showEnquiryDetailBtn" data-enquiry-id ="' + G.normalize(params["enquiryId"]) + '">查看详情</a></td>';
                        if (APP_BCGOGO.Permission.Schedule.MessageCenter.ReceiverDelete) {
                            html += '        <td><div class="x-colse J_deletePushMessageBtn"></div></td>';
                        } else {
                            html += '        <td></td>';
                        }
                        html += '    </tr>';
                    } else if (pushMessageType == "VEHICLE_FAULT_2_SHOP"
                        || pushMessageType == "APP_VEHICLE_SOON_EXPIRE_MAINTAIN_MILEAGE_2_SHOP" || pushMessageType == "APP_VEHICLE_SOON_EXPIRE_MAINTAIN_TIME_2_SHOP"
                        || pushMessageType == "APP_VEHICLE_SOON_EXPIRE_INSURANCE_TIME_2_SHOP" || pushMessageType == "APP_VEHICLE_SOON_EXPIRE_EXAMINE_TIME_2_SHOP"
                        || pushMessageType == "APP_VEHICLE_OVERDUE_MAINTAIN_MILEAGE_2_SHOP" || pushMessageType == "APP_VEHICLE_OVERDUE_MAINTAIN_TIME_2_SHOP"
                        || pushMessageType == "APP_VEHICLE_OVERDUE_INSURANCE_TIME_2_SHOP" || pushMessageType == "APP_VEHICLE_OVERDUE_EXAMINE_TIME_2_SHOP") {
                        var href='unitlink.do?method=smsHistory&mobile=' + params['mobile'];
                        if (pushMessageType == "VEHICLE_FAULT_2_SHOP") {
                           subTitle = "客户车辆故障";
                            href = 'shopFaultInfo.do?method=showShopFaultInfoList&shopFaultInfoId=' + pushMessageDTO['relatedObjectIdStr'];
                        } else if ("APP_VEHICLE_SOON_EXPIRE_MAINTAIN_MILEAGE_2_SHOP,APP_VEHICLE_OVERDUE_MAINTAIN_MILEAGE_2_SHOP".indexOf(pushMessageType) != -1) {
                            subTitle = "里程提醒";
                        }  else if ("APP_VEHICLE_SOON_EXPIRE_MAINTAIN_TIME_2_SHOP,APP_VEHICLE_OVERDUE_MAINTAIN_TIME_2_SHOP".indexOf(pushMessageType) != -1) {
                            subTitle = "保养提醒";
                        } else if ("APP_VEHICLE_SOON_EXPIRE_INSURANCE_TIME_2_SHOP,APP_VEHICLE_OVERDUE_INSURANCE_TIME_2_SHOP".indexOf(pushMessageType) != -1) {
                            subTitle = "保险提醒";
                        } else if ("APP_VEHICLE_SOON_EXPIRE_EXAMINE_TIME_2_SHOP,APP_VEHICLE_OVERDUE_EXAMINE_TIME_2_SHOP".indexOf(pushMessageType) != -1) {
                            subTitle = "验车提醒";
                        }
                        html += '    <tr>';
                        html += '        <td valign="top"><div class="news-01">';
                        html += '            <input type="checkbox" data-receiver-id="' + currentPushMessageReceiverDTO.idStr + '"  class="J_itemSelectAll"/>';
                        html += '        </div></td>';
                        html += '        <td><div class="' + (currentPushMessageReceiverDTO.status == "UNREAD" ? 'news-unread J_readPushMessageBtn' : 'news-read') + '"></div><div class="news-div">' + subTitle + '：<span class="gay_color">（' + pushMessageDTO.createMinTimeStr + '）</span><br/>';
                        html += '       ' + pushMessageDTO.content + '</div></td>';

                        if (pushMessageType == "VEHICLE_FAULT_2_SHOP") {
                          html += '        <td><a class="blue_color J_sendVehicleFaultSMSBtn" target="_blank" href="' + href + '" data-push-message-id ="' + G.normalize(pushMessageDTO["idStr"]) + '">联系客户</a></td>';
                        } else {
                          html += '        <td><a class="blue_color J_sendVehicleFaultSMSBtn" target="_blank" onclick="sendVehicleMaintainMessage(\'' + pushMessageDTO.content + '\',\'' + params['mobile'] + '\')" data-push-message-id ="' + G.normalize(pushMessageDTO["idStr"]) + '">联系客户</a></td>';
                        }

                        if (APP_BCGOGO.Permission.Schedule.MessageCenter.ReceiverDelete) {
                            html += '        <td><div class="x-colse J_deletePushMessageBtn"></div></td>';
                        } else {
                            html += '        <td></td>';
                        }
                        html += '    </tr>';
                    }
                }
            });

            html +='</table>';
        });

        $("#pushMessageTables").append(html);
    } else {
        var html = "";
        html +='<table width="796" border="0" cellspacing="0" class="news-table">';
        html +='    <tbody><tr>';
        html +='        <td valign="top"><span class="gray_color">暂无符合条件的消息！</span></td>';
        html +='    </tr>';
        html +='    </tbody></table>';
        $("#pushMessageTables").append(html);
    }
}
