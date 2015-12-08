$().ready(function(){

 $("#acceptMemo").blur(function (event) {
        var initialValue = $(event.target).attr("initialValue");
        if (initialValue != null && initialValue != "") {
            if (event.target.value == '') {
                event.target.value = initialValue;
                $(this).css({"color":"#666666"});
            }
        } else {
            $(this).css({"color":"#000000"});
        }
    }).focus(function (event) {
            var initialValue = $(event.target).attr("initialValue");
            if (initialValue != null && initialValue != "") {
                if (event.target.value == initialValue) {
                    event.target.value = "";
                }
                $(this).css({"color":"#000000"});
            }
        });

    //接受采购
    $("#accept_div").bind("click", function (){
        var $me=$(this);
        if($me.attr("lock")=="true"){
            return;
        }
        if(APP_BCGOGO.Permission.Version.StoreHouse && G.Lang.isEmpty($("#storehouseId").val())){
            nsDialog.jAlert("请选择仓库！");
            return;
        }
        var msg="";
        $(".item").each(function(i){
            var amount=G.rounding($(this).find("[id$='amount']").val());
            var pAmount=G.rounding($(this).find("[id$='pAmount']").text());
            var supplierInventoryTotal=G.rounding($(this).find("[id$='pAmount']").attr("supplierInventoryTotal"));
            if(supplierInventoryTotal>=pAmount&&amount!=pAmount){
                msg+="第"+(i+1)+"行，出货量应等于采购量！<br/>"
            }
        });
        if(!G.isEmpty(msg)){
            nsDialog.jAlert(msg);
            return;
        }
        //预计交货时间
        var radios = document.getElementsByName("dispatchDateRadio");
        for(var i=0;i<radios.length;i++){
            if(radios[i].checked==true){
                $("#dispatchDate").val(radios[i].value);
                if(i==3){
                    $("#dispatchDate").val($("#definedDate").val());
                }
            }
        }
        if($("#dispatchDate").val()==""){
            nsDialog.jAlert("预计交货时间必须填写！");
            return;
        }
        if ($("#acceptMemo").val() == $("#acceptMemo").attr("initialValue")) {
            $("#acceptMemo_hidden").val("");
        } else {
            $("#acceptMemo_hidden").val($("#acceptMemo").val());
        }
        $(this).attr("lock",true);
        var acceptDialog="<div>确认接受订单？</div>";
        $(acceptDialog).dialog({
            resizable: true,
            title: "提示",
            height: 150,
            width: 270,
            modal: true,
            closeOnEscape: false,
            buttons:{
                "确定":function(){
                    $("#salesOrderForm").attr("action","sale.do?method=acceptPendingPurchaseOrder");
                    $("#salesOrderForm").submit();
                    $me.attr("lock",false);
                    $(this).dialog("close");
                },
                "取消":function(){
                    $me.attr("lock",false);
                    $(this).dialog("close");
                }
            }
        });
    });

    //拒绝采购
    $("#refuseConfirmBtn").bind("click", function () {
        if(GLOBAL.Lang.isEmpty($("#refuseMsg").val())){
            nsDialog.jAlert("拒绝理由必须填写！");
            return;
        }
        $("#salesRefuseForm").attr("action","sale.do?method=refusePendingPurchaseOrder");
        $("#salesRefuseForm").submit();
    });

    $("#refuseCancelBtn").bind("click", function () {
        $("#refuseReasonDialog").dialog("close");
    });

    //发货
//    $("#dispatchConfirmBtn").bind("click", function () {
//        if(GLOBAL.Lang.isEmpty($("#company").val())){
//            nsDialog.jAlert("物流公司名称必须填写！");
//            return;
//        }
//        $("#dispatchForm").attr("action","sale.do?method=dispatchSaleOrder");
//        $("#dispatchForm").submit();
//    });
//
//    $("#dispatchCancelBtn").bind("click", function () {
//        $("#dispatchDialog").dialog("close");
//    });

    $("#definedDate").datepicker({
        "numberOfMonths" : 1,
        "changeYear":true,
        "changeMonth":true,
        "dateFormat": "yy-mm-dd",
        "minDate":0,
        "yearRange": "c:c+1",
        "yearSuffix":"",
        "showButtonPanel":true
    }).bind("click", function() {
            $("#salesAcceptForm input[name='dispatchDateRadio']").eq(3).attr("checked", "checked");
        });
    //终止交易
    $("#repealConfirmBtn").bind("click", function () {
        if(!$.trim($("#repealMsg").val())){
            nsDialog.jAlert("请填写作废理由",null,function(){});
            return;
        }
        $("#salesRepealForm").submit();
    });
    $("#repealCancelBtn").bind("click", function () {
        $("#repealReasonDialog").dialog("close");
    });

    $("#acceptMemo").bind("focus",function(){
        if($("#acceptMemo").val() == $("#acceptMemo").attr("initialvalue")){
            $("#acceptMemo").val("");
            $("#acceptMemo").css("color","#000000");
        }}).bind("blur", function() {
            if (!$("#acceptMemo").val()) {
                $("#acceptMemo").val($("#acceptMemo").attr("initialvalue"));
                $("#acceptMemo").css("color", $("#acceptMemo").attr("initialvalueColor"));
            }
        });

    $("#supplierSalesAccount").bind("click", function () {
        var ajaxUrl = "sale.do?method=validateSupplierSalesOrder";
        var ajaxData = {orderId: $("#id").val()};
        bcgogoAjaxQuery.setUrlData(ajaxUrl, ajaxData);
        bcgogoAjaxQuery.ajaxQuery(function (json) {
            $("#nullifyBtn").removeAttr("status");
            if (json.success) {
                bcgogo.checksession({parentWindow: window.parent, iframe_PopupBox: $("#iframe_PopupBox_account")[0], src: "sale.do?method=supplierSalesAccount&orderId=" + $("#id").val()});
            } else if (!json.success) {
                showMessage.fadeMessage("45%", "34%", "slow", 300, json.msg);
            }
        }, function (json) {
            showMessage.fadeMessage("45%", "34%", "slow", 300, "网络异常，请联系客服!");
        });

    });

        //打印页面
    $("#printBtn").click(function() {
        if (!$("#id").val() && !$("#draftOrderIdStr").val()) {
            return;
        }
        APP_BCGOGO.Net.syncGet({
            url:"print.do?method=getTemplates",
            data:{
                "orderType":"SALE",
                "now":new Date()
            },
            dataType:"json",
            success:function(result) {
                if(result && result.length >1){
                    var selects = "<div style='margin:10px;font-size:15px;line-height: 22px;'>" +
                        "<div style='margin-bottom:5px;'>请选择打印模板：</div>";
                    for(var i = 0; i<result.length; i++){
                        var radioId = "selectTemplate" + i;
                        selects += "<input type='radio' id='"+radioId+"' name='selectTemplate' value='"+result[i].idStr+"'";
                        if(i==0){
                            selects += " checked='checked'";
                        }
                        selects += " />" +"<label for='"+radioId+"'>"+result[i].displayName +"</label><br/>";
                    }
                    selects += "</div>";
                    nsDialog.jConfirm(selects, "请选择打印模板", function (returnVal) {
                        if (returnVal) {
                            printSalesOrder($("input:radio[name='selectTemplate']:checked").val());
                        }
                    });
                }else{
                    printSalesOrder();
                }
            }
        });
    });

    $("#print_pending_purchaseOrder").click(function() {
        if (!$("#purchaseOrderId").val()) {
            return;
        }
        APP_BCGOGO.Net.syncGet({
            url:"print.do?method=getTemplates",
            data:{
                "orderType":"PENDING_PURCHASE_ORDER",
                "now":new Date()
            },
            dataType:"json",
            success:function(result) {
                if(result && result.length >1){
                    var selects = "<div style='margin:10px;font-size:15px;line-height: 22px;'>" +
                        "<div style='margin-bottom:5px;'>请选择打印模板：</div>";
                    for(var i = 0; i<result.length; i++){
                        var radioId = "selectTemplate" + i;
                        selects += "<input type='radio' id='"+radioId+"' name='selectTemplate' value='"+result[i].idStr+"'";
                        if(i==0){
                            selects += " checked='checked'";
                        }
                        selects += " />" +"<label for='"+radioId+"'>"+result[i].displayName +"</label><br/>";
                    }
                    selects += "</div>";
                    nsDialog.jConfirm(selects, "请选择打印模板", function (returnVal) {
                        if (returnVal) {
                            printPendingPurchaseOrder($("input:radio[name='selectTemplate']:checked").val());
                        }
                    });
                }else{
                    printPendingPurchaseOrder();
                }
            }
        });
    });

    if(verifyProductThroughOrderVersion(getOrderType())){
        if($("#status").val()=="STOCKING"){
           $("#storehouseId").attr("disabled","disabled");
        }
        $("[id$='_supplierInfo'] .useRelatedAmount").blur();
    }
    setTotal();

    if ($("#status").val() == "PENDING") {
        jQuery("#zuofei").removeClass("zuofei").addClass("pendingImg").show();
    } else if ($("#status").val() == "STOCKING") {
        jQuery("#zuofei").removeClass("zuofei").addClass("stockingImg").show();
    } else if ($("#status").val() == "DISPATCH") {
        jQuery("#zuofei").removeClass("zuofei").addClass("dispatchImg").show();
    } else if ($("#status").val() == "REFUSED") {
        jQuery("#zuofei").removeClass("zuofei").addClass("refusedImg").show();
    } else if ($("#status").val() == 'SALE_DONE') {
        $(".invalidImg").show();
        jQuery(".copyInput_div").show();

        if ($("#statementAccountOrderId").val()) {
            jQuery("#zuofei").removeClass("zuofei").addClass("statement_accounted").show();
        } else {

            if (debt * 1 > 0) {
                jQuery("#zuofei").removeClass("zuofei").addClass("debtSettleImg").show();
            }
            else {
                jQuery("#zuofei").removeClass("zuofei").addClass("jie_suan_sale").show();
            }
        }


    }  else if ($("#status").val() == 'SALE_REPEAL') {
        $("#zuofei").show();
        $(".invalidImg").hide();
        jQuery(".copyInput_div").show();
    } else if ($("#status").val() == "SELLER_STOP") {
        jQuery("#zuofei").removeClass("zuofei").addClass("stopImg").show();
    } else if ($("#status").val() == "STOP") {
        jQuery("#zuofei").removeClass("zuofei").addClass("stopImg").show();
    } else if ($("#status").val() == '') {
        $("#zuofei").hide();
        $(".invalidImg").hide();
        jQuery(".copyInput_div").hide();
    } else if('PURCHASE_ORDER_REPEAL' == $("#status").val()) {
        jQuery("#zuofei").removeClass("zuofei").addClass("stopImg").show();
    }
    $("#duizhan").bind("click",function(){
        toCreateStatementOrder($("#customerId").val(), "CUSTOMER_STATEMENT_ACCOUNT");
    });
});

//function acceptSale() {

//    $("#goodsSaler_hidden").val($("#goodsSaler").val());
//    $("#goodsSalerId_hidden").val($("#goodsSalerId").val());

//}
//终止交易
function stopSale() {
    if($("#purchaseOrderDTOStatus") && $("#purchaseOrderDTOStatus").val() == "PURCHASE_ORDER_DONE"){
        nsDialog.jAlert("买家已入库，不能终止交易！");
    }else{
        $("#repealReasonDialog").dialog({ width: 520, modal:true});
    }

}

function refuseSale() {
    if($("#acceptMemo").val()==$("#acceptMemo").attr("initialValue")){
        $("#saleMemo_hidden").val("");
    }else{
        $("#saleMemo_hidden").val($("#acceptMemo").val());
    }
    $("#refuseReasonDialog").dialog({ width: 520, modal:true});
}

function validateDispatch(){

}

function dispatch(){

    if (APP_BCGOGO.Permission.Version.ProductThroughSelectSupplier) {
        if(APP_BCGOGO.Permission.Version.StoreHouse && G.Lang.isEmpty($("#storehouseId").val())){
        nsDialog.jAlert("请选择仓库！");
        return;
    }
        var msg="";
        $(".item").each(function(i){
            var amount=G.rounding($(this).find("[id$='amount']").val(),2);
            var pAmount=G.rounding($(this).find("[id$='pAmount']").text(),2);
            if(amount!=pAmount){
                msg+="第"+(i+1)+"行，出货量应和买家采购量相等！<br/>"
            }
        });
        if(!G.isEmpty(msg)){
            nsDialog.jAlert(msg);
            return;
        }
    }

    App.Net.syncPost({
        url: "sale.do?method=validateDispatchSaleOrder",
        data: {salesOrderId: $("#salesOrderId").val()},
        dataType: "json",
        success: function (json) {
            if(json && json.success){
                $("#dispatchDialog").dialog({
                    resizable: true,
                    title: "提示",
                    height: 300,
                    width: 550,
                    modal: true,
                    closeOnEscape: false,
                    buttons:{
                        "确定":function(){
                             $(this).dialog("close");
                            $("#company").val($("#dCompany").val());
                            $("#waybills").val($("#waybill_id").val());
                            $("#dispatchMemo").val($("#dispatch_memo").val());
                            if(GLOBAL.Lang.isEmpty($("#dCompany").val())){
                                nsDialog.jAlert("物流公司名称必须填写！");
                                return;
                            }
                            $("#salesOrderForm").attr("action","sale.do?method=dispatchSaleOrder");
                            $("#salesOrderForm").submit();
                        },
                        "取消":function(){
                            $(this).dialog("close");
                        }
                    }
                })}else{
                if(json && json.operation=="CONFIRM_ALLOCATE_RECORD"){
                    nsDialog.jConfirm(json.msg, "确认仓库调拨提示", function (returnVal) {
                        if(returnVal){
                            window.open("allocateRecord.do?method=createAllocateRecordBySaleOrderId&salesOrderId="+$("#salesOrderId").val(),"_blank");
                        }
                    });
                }else if(json && json.operation=="ALERT_SALE_LACK"){
                    nsDialog.jAlert(json.msg);
                }else{
                    nsDialog.jAlert(json.msg);
                }
            }
        }
    });
}

function printSalesOrder(templateId){
    if ($("#id").val()) {
       window.showModalDialog("sale.do?method=getSalesOrderToPrint&salesOrderId=" + $("#id").val() +"&templateId=" + templateId + "&now=" + new Date(), '', "dialogWidth=1024px;dialogHeight=768px");
        return;
    }
    if ($("#draftOrderIdStr").val()) {
        window.showModalDialog("draft.do?method=getDraftOrderToPrint&id=" + $("#draftOrderIdStr").val() + "&type=SALE&now=" + "&templateId="+templateId + new Date());
    }
}

function printPendingPurchaseOrder(templateId){
    if ($("#purchaseOrderId").val()) {
        window.showModalDialog("sale.do?method=getPendingPurchaseOrderToPrint&purchaseOrderId=" + $("#purchaseOrderId").val() +"&templateId=" + templateId + "&now=" + new Date(), '', "dialogWidth=1024px;dialogHeight=768px");
        return;
    }
}

function  setTotal(){

}

function updateProductStorageBin(data){
    $("#table_productNo").find(".item").each(function () {
        var productId = $(this).find("input[id$='.productId']").val();
        if (!G.Lang.isEmpty(productId)) {
            $(this).find(".storage_bin_td").html(G.normalize(data[productId],""));
        }
    });
}