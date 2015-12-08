/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 13-3-8
 * Time: 上午10:54
 * To change this template use File | Settings | File Templates.
 */
var trCount=0;

$(document).ready(function () {
//    var supplierProductIdArray=[];
//    $(".item .productId").each(function(){
//        supplierProductIdArray.push($(this).val());
//    });
//    initOrderPromotionsDetail(supplierProductIdArray);

    $(".J-showAlert").live("mouseenter", function(event) {
        event.stopImmediatePropagation();
        var _currentTarget = $(event.target);
        var _alertTarget = $(event.target).next(".alert");
        _alertTarget.show();
        _alertTarget.mouseleave(function(event) {
            if($(event.relatedTarget)[0] != _currentTarget[0]) {
                _alertTarget.hide();
            }
        });
    }).live("mouseleave", function(event) {
            event.stopImmediatePropagation();
            var _alertTarget = $(event.target).next(".alert");
            if($(event.relatedTarget).closest(".alert")[0] != _alertTarget[0]) {
                _alertTarget.hide();
            }
        });


    $("#createPurchaseOrderBtn").bind("click",function(e){
        var quotedPreBuyOrderItemIds = $(this).attr("data-itemids");
        if (G.Lang.isEmpty(quotedPreBuyOrderItemIds)) {
            return;
        }
        window.location.href="RFbuy.do?method=createPurchaseOrderOnlineByQuotedPreBuyOrder&quotedPreBuyOrderItemIds="+quotedPreBuyOrderItemIds;
    });

    $("#acceptSupplierApplyBtn").bind("click",function(e){
        var inviteId = $(this).attr("data-inviteId")
        if ($(this).attr("lock")|| G.Lang.isEmpty(inviteId)) {
            return;
        }
        $(this).attr("lock", true);
        APP_BCGOGO.Net.asyncAjax({
            type: "POST",
            url: "apply.do?method=acceptSupplierApply",
            data: {inviteId: inviteId},
            dataType: "json",
            success: function (result) {
                if (result.success) {
                    nsDialog.jAlert("同意关联成功！", "", function () {
                        //
                        $("#relationOperationDiv").html("<input type=\"hidden\" id=\"relationMidStatus\" value=\"RELATED\"/>");
                        var supplierDTO = result.data;
                        var contactInfoHtml = "<div><span>"+G.Lang.normalize(supplierDTO.contact,"暂无信息")+"</span>&nbsp;<span>"+G.Lang.normalize(supplierDTO.mobile,"暂无信息")+"</span></div>";
                        $("#contactInfoDiv").html(contactInfoHtml);
                        $("#landLineSpan").text(G.Lang.normalize(supplierDTO.landLine,"暂无信息"));
                        $("#faxSpan").text(G.Lang.normalize(supplierDTO.fax,"暂无信息"));
                        $("#emailSpan").text(G.Lang.normalize(supplierDTO.email,"暂无信息"));
                        $("#businessScopeSpan").text(G.Lang.normalize(supplierDTO.businessScope,"暂无信息"));
                    });
                } else {
                    nsDialog.jAlert(result.msg);
                    $(this).removeAttr("lock");
                }
            },
            error: function () {
                nsDialog.jAlert("网络异常,请刷新页面!");
                $(this).removeAttr("lock");
            }
        });
    });

    $("#applySupplierRelationBtn").bind("click",function(e){
        var supplierShopId = $(this).attr("data-suppliershopid");
        if ($(this).attr("lock")|| G.Lang.isEmpty(supplierShopId)) {
            return;
        }
        $(this).attr("lock", true);
        var $thisDom =  $(this);
        APP_BCGOGO.Net.syncPost({
            url: "apply.do?method=applySupplierRelation",
            dataType: "json",
            data:{"supplerShopId":supplierShopId},
            success: function (result) {
                if (result.success) {
                    $("#relationOperationDiv").html("<input type=\"hidden\" id=\"relationMidStatus\" value=\"APPLY_RELATED\"/>" +
                        "<div class=\"list\"><span style=\"color: #999999\">已申请关联</span></div>" +
                        "<div class=\"list\"><span style=\"color: #999999\">关联后可查看卖家信息</span></div>");
                    nsDialog.jAlert( "您的申请提交成功，请等待对方同意！");
                } else {
                    nsDialog.jAlert(result.msg);
                    $thisDom.removeAttr("lock");
                }
            },
            error: function () {
                nsDialog.jAlert("网络异常,请刷新页面!");
                $thisDom.removeAttr("lock");
            }
        });
    });

    $("#QQ").multiQQInvoker({QQ:$("#QQ").attr("data")});
});
