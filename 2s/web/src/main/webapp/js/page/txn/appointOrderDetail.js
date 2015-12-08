$(function(){
    $("#printBtn").bind("click",function(){
        window.showModalDialog("appoint.do?method=getAppointOrderToPrint&appointOrderId=" + $("#appointOrderId").val()
            + "&now=" + new Date(), '预约单', "dialogWidth=1024px;dialogHeight=768px,status=no;help=no");
    });

    $("#showWashOrRepairOrder").bind("click",function(){
        APP_BCGOGO.Net.asyncAjax({
            url:"appoint.do?method=validateDraftOrderAndAppointOrderStatus",
            type:"POST",
            data:{appointOrderId:$("#appointOrderId").val()},
            cache: false,
            dataType: "json",
            success:function(result){
              if(result.success) {
                  if(result.data) {
                      var orderIdStr = result.data.orderIdStr == null ? "" : result.data.orderIdStr;
                      var orderType = result.data.orderType == null ? "" : result.data.orderType;
                      var appointOrderStatus = result.data.status;
                      if('WASH_BEAUTY' == orderType) {
                          window.location.href = "washBeauty.do?method=getWashBeautyOrder&washBeautyOrderId=" + orderIdStr;
                      } else if('REPAIR' == orderType) {
                          if('TO_DO_REPAIR' == appointOrderStatus) {
                             window.location.href = "txn.do?method=getRepairOrderByDraftOrder&menu-uid=VEHICLE_CONSTRUCTION_REPAIR&draftOrderId=" + orderIdStr;
                          } else if('HANDLED' == appointOrderStatus) {
                             window.location.href = "txn.do?method=getRepairOrder&menu-uid=VEHICLE_CONSTRUCTION_REPAIR&repairOrderId=" + orderIdStr;
                          }
                      } else if ('REPAIR_DRAFT_ORDER' == orderType) {
                          window.location.href = "txn.do?method=getRepairOrderByDraftOrder&menu-uid=VEHICLE_CONSTRUCTION_REPAIR&draftOrderId=" + orderIdStr;
                      }
                  }
              } else {
                  nsDialog.jAlert(result.msg);
              }
            },
            error: function(result){
               nsDialog.jAlert(result.msg);
            }
        });
    });

    $("#modifyBtn").click(function(){
        var   orderId =  $("#appointOrderId").val();
        if(G.Lang.isEmpty(orderId)){
            return;
        }
        APP_BCGOGO.Net.syncPost({
            url: "appoint.do?method=validateUpdateAppointOrder",
            dataType: "json",
            data: {
                id:orderId
            },
            success: function (result) {
                if (result && result.success) {
                    window.location.href = "appoint.do?method=showAppointOrderDetail&operationType=EDIT&appointOrderId=" + orderId;
                    }else if(result && !result.success){
                    nsDialog.jAlert(result.msg);
                }
            },
            error: function () {
                nsDialog.jAlert("网络异常！");
            }
        });
    });

    $("#acceptBtn").click(function () {
        var orderId = $("#appointOrderId").val();
        if (G.Lang.isEmpty(orderId)) {
            return;
        }
        var $acceptBtn = $(this);
        if($acceptBtn.attr("lock")){
            return;
        }
        $acceptBtn.attr("lock",true);

        APP_BCGOGO.Net.syncPost({
            url: "appoint.do?method=validateAcceptAppointOrder",
            dataType: "json",
            data: {
                id: orderId
            },
            success: function (result) {
                if (result && result.success) {
                    nsDialog.jConfirm("确定接受当前预约单？",null,function(value){
                       if(value){
                           window.location.href = "appoint.do?method=acceptAppointOrder&id=" + orderId;
                       }else{
                           $acceptBtn.removeAttr("lock");
                       }
                    })
                } else if (result && !result.success) {
                    nsDialog.jAlert(result.msg);
                    $acceptBtn.removeAttr("lock");
                }
            },
            error: function () {
                nsDialog.jAlert("网络异常！");
                $acceptBtn.removeAttr("lock");
            }
        });
    });

    $("#refuseBtn").click(function () {
        var orderId = $("#appointOrderId").val();
        if (G.Lang.isEmpty(orderId)) {
            return;
        }
        var $refuseBtn = $(this);
        if($refuseBtn.attr("lock")){
            return;
        }
        $refuseBtn.attr("lock",true);

        APP_BCGOGO.Net.syncPost({
            url: "appoint.do?method=validateRefuseAppointOrder",
            dataType: "json",
            data: {
                id: orderId
            },
            success: function (result) {
                if (result && result.success) {
                    var html = '';
                    $("#refuseDialog").dialog({
                        resizable: false,
                        title: "拒绝理由",
                        height: 200,
                        width: 445,
                        modal: true,
                        closeOnEscape: false,
                       close:function(){
                           $refuseBtn.removeAttr("lock");
                           $("#refuseMsg").val("");
                       }
                    });
                } else if (result && !result.success) {
                    nsDialog.jAlert(result.msg);
                    $refuseBtn.removeAttr("lock");
                }
            },
            error: function () {
                nsDialog.jAlert("网络异常！");
                $refuseBtn.removeAttr("lock");
            }
        });
    });

    $("#confirmRefuse").click(function(){
        var $confirmRefuse = $(this);
        if ($confirmRefuse.attr("lock")) {
            return;
        }
        $confirmRefuse.attr("lock", true);
        var orderId = $("#appointOrderId").val();
        if(G.Lang.isEmpty(orderId)){
            nsDialog.jAlert("需要拒绝的单据不存在，请从预约单管理页面重新打开单据！");
            $confirmRefuse.removeAttr("lock");
             return;
        }
         var refuseMsg = $("#refuseMsg").val();
         if(G.Lang.isEmpty(refuseMsg)){
             nsDialog.jAlert("请填写拒绝理由！");
             $confirmRefuse.removeAttr("lock");
             return;
         }
        window.location.href = "appoint.do?method=refuseAppointOrder&id=" + orderId + "&refuseMsg=" + refuseMsg;
    });

    $("#cancelRefuse").click(function(){
        $("#refuseDialog").dialog("close");
    });

    $("#cancelBtn").click(function () {
        var orderId = $("#appointOrderId").val();
        if (G.Lang.isEmpty(orderId)) {
            return;
        }
        var $cancelBtn = $(this);
        if($cancelBtn.attr("lock")){
            return;
        }
        $cancelBtn.attr("lock",true);

        APP_BCGOGO.Net.syncPost({
            url: "appoint.do?method=validateCancelAppointOrder",
            dataType: "json",
            data: {
                id: orderId
            },
            success: function (result) {
                if (result && result.success) {
                    $("#cancelDialog").dialog({
                        resizable: false,
                        title: "取消理由",
                        height: 200,
                        width: 445,
                        modal: true,
                        closeOnEscape: false,
                        close: function () {
                            $cancelBtn.removeAttr("lock");
                            $("#cancelMsg").val("");
                        }
                    });
                } else if (result && !result.success) {
                    nsDialog.jAlert(result.msg);
                    $cancelBtn.removeAttr("lock");
                }
            },
            error: function () {
                nsDialog.jAlert("网络异常！");
                $cancelBtn.removeAttr("lock");
            }
        });
    });

    $("#confirmCancel").click(function(){
        var $confirmCancel = $(this);
        if ($confirmCancel.attr("lock")) {
            return;
        }
        $confirmCancel.attr("lock", true);
        var orderId = $("#appointOrderId").val();
        if(G.Lang.isEmpty(orderId)){
            nsDialog.jAlert("需要取消的单据不存在，请从预约单管理页面重新打开单据！");
            $confirmCancel.removeAttr("lock");
             return;
        }
         var cancelMsg = $("#cancelMsg").val();
         if(G.Lang.isEmpty(cancelMsg)){
             nsDialog.jAlert("请填写取消理由！");
             $confirmCancel.removeAttr("lock");
             return;
         }
        window.location.href = "appoint.do?method=cancelAppointOrder&id=" + orderId + "&cancelMsg=" + cancelMsg;
    });

    $("#cancelCancel").click(function(){
        $("#cancelDialog").dialog("close");
    });

    $("#returnBtn").click(function(){
        window.location.href = "appoint.do?method=showAppointOrderList";
    });

    $("#createOtherOrder").click(function () {
        var orderId = $("#appointOrderId").val();
        if (G.Lang.isEmpty(orderId)) {
            return;
        }
        var $createOtherOrder = $(this);
        if ($createOtherOrder.attr("lock")) {
            return;
        }
        $createOtherOrder.attr("lock", true);
        APP_BCGOGO.Net.syncPost({
            url: "appoint.do?method=validateCreateOtherOrder",
            dataType: "json",
            data: {
                id: orderId
            },
            success: function (result) {
                if (result && result.success) {
                    window.location.href = "appoint.do?method=createOtherOrder&id=" + orderId;
//                    nsDialog.jConfirm("确定接受当前预约单？", null, function (value) {
//                        if (value) {
//                            window.location.href = "appoint.do?method=createOtherOrder&id=" + orderId;
//                        } else {
//                            $createOtherOrder.removeAttr("lock");
//                        }
//                    })
                } else if (result && !result.success) {
                    if(!G.Lang.isEmpty(result["operation"]) &&  result["operation"] =="CONFIRM" ){
                        nsDialog.jConfirm(result["msg"],null,function(value){
                            if(value){
                                window.location.href = "txn.do?method=getRepairOrder&repairOrderId="+result["data"];
                            }
                        });
                    }else{
                        nsDialog.jAlert(result.msg);
                    }
                    $createOtherOrder.removeAttr("lock");
                }
            },
            error: function () {
                nsDialog.jAlert("网络异常！");
                $createOtherOrder.removeAttr("lock");
            }
        });


    });


    initAppointOrderPosition($("#appointOrderPositionIframe"),$("#coordinateLat").val(),$("#coordinateLon").val());
     $("#viewBigPositionBtn").click(function(){
        var  coordinateLat =  $("#coordinateLat").val();
         var  coordinateLon =  $("#coordinateLon").val();
         var url = "api/proxy/baidu/map/appoint?size=big&coordinateLat=" + coordinateLat + "&coordinateLon=" + coordinateLon;
         if(coordinateLat &&  coordinateLon){
             $("#bigMapContainer").show();
             $("#bigMapContainerLayer").show();
             $("#iframe_big_map").show().attr("src",url);
         }
     });

    $("#bigMapContainerClose").click(function (e) {
        $("#bigMapContainer").hide();
        $("#bigMapContainerLayer").hide();
        $("#iframe_big_map").hide()
    });

});

function initAppointOrderPosition($iframe,coordinateLat,coordinateLon){
   if(!$iframe || !coordinateLat || !coordinateLon){
       return;
   }
    var url = "api/proxy/baidu/map/appoint?coordinateLat=" + coordinateLat + "&coordinateLon=" + coordinateLon;
    $iframe.attr("src",url);
}
