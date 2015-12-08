$(document).ready(function () {


    jQuery(document).click(function(e) {
        var e = e || event;
        var target = e.srcElement || e.target;
        if (target.id != "div_serviceName" && target.id != "Scroller-Container_ServiceName") {
          jQuery("#div_serviceName").hide();
        }
    });

    var errorMsg = $("#errorMsg").html();
    if (!GLOBAL.Lang.isEmpty(errorMsg) && errorMsg!="true") {
        nsDialog.jAlert(errorMsg);
    }
    if ($("#status").val() == 'REPEAL') {
        $(".zuofei").show();
    }
    if ($("#status").val() == 'ACCEPTED') {
        $(".acceptedImg").show();
    }
    if ($("#status").val() == 'PENDING') {
        $(".pendingImg").show();
    }
    if ($("#status").val() == 'REFUSED') {
        $(".refusedImg").show();
    }
    if ($("#status").val() == 'STOP') {
        $(".stopImg").show();
    }
    if ($("#status").val() == 'WAITING_STORAGE') {
        $(".waittingStorageImg").show();
    }
    if ($("#status").val() == 'SETTLED') {
      if ($("#statementAccountOrderId").val()) {
        jQuery(".jie_suan").removeClass("jie_suan").addClass("statement_accounted").show();
      } else {
        if($("#returnDebt").html() && $("#returnDebt").html().replace("元", '')*1 >0)
        {
            $(".debtSettleImg").show();
        }
        else
        {
            $(".jie_suan").show();
        }
      }
    }

    //结算
    $("#accountBtn").bind("click", function () {
        if(APP_BCGOGO.Permission.Version.StoreHouse && G.Lang.isEmpty($("#storehouseId").val())){
            nsDialog.jAlert("请选择仓库！");
            return;
        }
        var hasEmpty = false;
        $(".checkNumberEmpty").each(function(){
            if(GLOBAL.Lang.isEmpty(this.value) || this.value == 0){
                hasEmpty = true;
                return false;       // 相当于break;
            }
        });
        if(hasEmpty){
            nsDialog.jAlert("请输入正确的入库数量！");
            return false;
        }
        bcgogo.checksession({"parentWindow":window.parent, 'iframe_PopupBox':$("#iframe_PopupBox_account")[0], 'src':'salesReturn.do?method=accountDetail&customerId=' + $("#customerId").text()+'&salesReturnId='+$("#salesReturnForm #id").val()});
    });

    $("#nullifyBtn").bind("click", function(){
        var validatePass = false;
        APP_BCGOGO.Net.syncAjax({
            url: "salesReturn.do?method=validateRepeal",
            type: "POST",
            dataType: "json",
            data: {
                orderId: $("#id").val()
            },
            success: function (json) {
                if (json.success) {
                    validatePass = true;
                }else{
                    if(json.operation && json.operation == 'NOT_ENOUGH_INVENTORY'){
                        var msg = "对不起，";
                        if(json.data.storehouse){
                            msg += "仓库'" + json.data.storehouse.name + "'中，";
                        }
                        msg += "以下商品的库存不足，无法作废！其他商品可通过销售单解决。<p style='height:7px;'><ul>";
                        var products = json.data.products;
                        for(var i in products){
                            msg += '<li style="list-style: inside">' + products[i].name + ' ' + products[i].brand + '</li>'
                        }
                        msg += "</ul>";
                        nsDialog.jAlert(msg);
                    }else{
                        nsDialog.jAlert(json.msg);
                    }
                }
            },
            error: function(){
                nsDialog.jAlert("验证异常!");
            }
        });
        if(!validatePass){
            return;
        }
        var url = "salesReturn.do?method=repeal&orderId=";
        nsDialog.jConfirm("友情提醒：作废后可能会有库存和金额的变化，为保证账面完整，请将原单据回笼，并标记作废！", null, function (returnVal) {
            if(returnVal){
                url = url + $("#id").val();
                window.location = url;
            }
        });
    });

    $("#memo")
        .blur(function (event) {
            var initialValue = $(event.target).attr("initialValue");
            if (initialValue != null && initialValue != "") {
                if (event.target.value == '') {
                    event.target.value = initialValue;
                    $(this).css({"color":"#ADADAD"});
                }
            } else {
                $(this).css({"color":"#000000"});
            }
        })
        .focus(function (event) {
            var initialValue = $(event.target).attr("initialValue");
            if (initialValue != null && initialValue != "") {
                if (event.target.value == initialValue) {
                    event.target.value = "";
                }
                $(this).css({"color":"#000000"});
            }
        });
    $("#acceptBtn").bind("click", function () {
        var allSame = true;
        var allResult =[];
        var idList = ["productName", "brand", "spec", "model", "vehicleModel", "vehicleBrand"];
        $("#tb_tui .item").each(function(idx){
            allResult[idx] = true;
            for(var index in idList){
                var idName = idList[index];
                if($.trim($("span[id='"+idName +idx+"']").text()) != $.trim($("span[id='"+idName +"Supplier" + idx+"']").text())){
                    allSame = false;
                    allResult[idx] = false;
                    return true;
                }
            }

        });
        if(allSame){
        nsDialog.jConfirm("确认接受退货请求？", null, function (returnVal) {
            if (returnVal) {
                    if($("#memo").val()==$("#memo").attr("initialValue")){
                        $("#memo").val("");
                    }
                $("#salesReturnForm").attr("action", "salesReturn.do?method=acceptSalesReturnOrder");
                $("#salesReturnForm").submit();
            }
        });
        }else{
            var content = "<div style='padding: 10px;font-size: 14px;'>友情提示：由于对方修改购买商品信息或本店修改出售商品信息，造成部分退货商品在目前的库存中不存在！（退货商品对应的库存商品信息见下表）<p/><p style='padding-top:5px;'>是否确定接受退货？</p></div>";
            content += "<table id='tb_tui'><tr class='tab_title'><td colspan='6'>退货店面商品</td><td></td><td colspan='6' class='blue'>本店库存商品</td></tr>";
            content += "<tr><td>品名</td><td>品牌/产地</td><td>规格</td><td>型号</td><td>车型</td><td>车辆品牌</td><td></td>" +
                "<td class='blue'>品名</td><td class='blue'>品牌/产地</td><td class='blue'>规格</td><td class='blue'>型号</td><td class='blue'>车型</td><td class='blue'>车辆品牌</td></tr>"
            for(var index in allResult){
                if(allResult[index]){
                    continue;
                }
                var shopProduct ="<td>";
                var supplierProduct = "<td class='blue'>";
                for(var idx in idList){
                    var shopProperty = $.trim($("span[id='"+idList[idx] +index+"']").text());
                    var supplierProperty = $.trim($("span[id='"+idList[idx]+"Supplier"+index+"']").text());
                    shopProduct += shopProperty+"</td>";
                    supplierProduct += supplierProperty+"</td>";
                    if(idx<idList.length){
                        shopProduct += "<td>";
                        supplierProduct += "<td class='blue'>";
                    }
                }
                shopProduct = shopProduct.substr(0, shopProduct.length-9);
                supplierProduct = supplierProduct.substr(0, supplierProduct.length-22);
                content += "<tr>"+shopProduct+"<td> <--> </td>"+supplierProduct+"</tr>";
            }
            content += "</table>";

            $("#acceptProductPrompt").html("<p>"+content+"</p>");
            $("#acceptProductPrompt").dialog({
                resizable: true,
                modal: true,
                draggable:true,
                height:"auto",
                width: "auto",
                buttons: {
                    "确定": function() {
                        $("#acceptProductPrompt").dialog("close");
                        if($("#memo").val()==$("#memo").attr("initialValue")){
                            $("#memo").val("");
                        }
                        $("#salesReturnForm").attr("action", "salesReturn.do?method=acceptSalesReturnOrder");
                        $("#salesReturnForm").submit();
                    },
                    "取消": function() {
                        $("#acceptProductPrompt").dialog("close");
                    }
                }
    });
        }
    });
    $("#refuseBtn").bind("click", function () {
        $("#refuseReasonDialog").dialog({ width: 458 });
    });
    $("#refuseConfirmBtn").bind("click", function () {
        $("#refuseReason").val($("#refuseReasonTextarea").val());
        if($("#memo").val()==$("#memo").attr("initialValue")){
            $("#memo").val("");
        }
        $("#salesReturnForm").attr("action", "salesReturn.do?method=refuseSalesReturnOrder");
        $("#salesReturnForm").submit();
    });
    $("#refuseCancelBtn").bind("click", function () {
        $("#refuseReasonTextarea").val("");
        $("#refuseReason").val("");
        $("#refuseReasonDialog").dialog("close");
    });

    //去打印页面
    $("#printBtn").click(function () {
        if ($("#id").val()) {
            window.showModalDialog("salesReturn.do?method=printSalesReturnOrder&salesReturnOrderId=" + $("#id").val(), '', "dialogWidth=1024px;dialogHeight=768px");
            return;
        }
//        if ($("#draftOrderIdStr").val()) {
//            window.showModalDialog("draft.do?method=getDraftOrderToPrint&id=" + $("#draftOrderIdStr").val() + "&type=RETURN&now=" + new Date(), '', "dialogWidth=1024px;dialogHeight=768px");
//        }
    });

    if($("#id").val() && (GLOBAL.Util.getUrlParameter("print") || GLOBAL.Util.getUrlParameter("print") == "true")){
        $("#printBtn").click();
    }

    if ($("#id").val() && "true" == $("#print").val()) {
        $("#printBtn").click();
    }
    $(".itemAmount").live("blur",
        function() {
            if (!$(this).val()) {
                var idPrefix = $(this).attr("id");
                idPrefix = idPrefix.substring(0, idPrefix.indexOf("."));
                $(this).val($("#"+idPrefix+"\\.purchaseReturnItemDTO\\.amount").val());
            }
            $(this).val(APP_BCGOGO.StringFilter.priceFilter($(this).val(), 1));
            var num = $.trim($(this).val());
            var returnNum = parseFloat($.trim($(this).next().val()));
            if (num > returnNum - 0.0001) {
                $(this).val(returnNum);
            }
//            setTotal();
        }).live('keyup', function() {
            $(this).val(APP_BCGOGO.StringFilter.inputtingPriceFilter($(this).val(), 1));
            var num = $.trim($(this).val());
            var returnNum = parseFloat($.trim($(this).next().val()));
            if (num > returnNum - 0.0001) {
                $(this).val(returnNum);
            }
//            setTotal();
        });
    if(GLOBAL.Util.getUrlParameter("accept") == 'true'){
        $("#acceptBtn").click();
    }
    $(".J_QQ").each(function (index, value) {
        $(this).multiQQInvoker({
            QQ: $(this).attr("data_qq")
        });
    });
});

function setTotal(){

}

function newOtherOrder(url) {
    if (openNewOrderPage()) {
       window.open(url, "_blank");
    } else {
      openOrAssign(url);
    }
}

function newSaleReturnOrder() {
  window.open($("#basePath").val() + "salesReturn.do?method=createSalesReturn", "_blank");
}