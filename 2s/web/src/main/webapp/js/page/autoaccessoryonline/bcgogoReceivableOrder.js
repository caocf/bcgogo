$(document).ready(function () {
    initQQTalk($("#onlineServiceQQ"));

    $("#bcgogoReceivableOrderPrintBtn").bind("click",function(e){
        var orderId=$(this).attr("data-order-id");
        if (!G.isEmpty(orderId)) {
            window.showModalDialog("bcgogoReceivable.do?method=print&orderId=" + orderId, '', "dialogWidth=1024px;dialogHeight=768px");
        }
    });

    $("#hardwareSubmitBcgogoReceivableOrderBtn").bind("click",function(){
        if (G.isEmpty($("#address").val())) {
            nsDialog.jAlert("收货地址不能为空！");
            return false;
        }

        if (G.isEmpty($("#contact").val())) {
            nsDialog.jAlert("收货人不能为空！");
            return false;
        }
        if (G.isEmpty($("#mobile").val())) {
            nsDialog.jAlert("手机号不能为空！");
            return false;
        }
        if (!APP_BCGOGO.Validator.stringIsMobilePhoneNumber($("#mobile").val())) {
            nsDialog.jAlert("手机号格式错误，请确认后重新输入！");
            return false;
        }
        var hasAmount = 0;
        $(".J_ModifyAmount").each(function(){
           if($(this).val()*1>0){
               hasAmount++;
           }
        });
        if(hasAmount==0){
            nsDialog.jAlert("商品数量不准确,请重新输入！");
            return false;
        }else{
            var msg = "是否确认提交当前订单?";
            if(hasAmount<$(".J_ModifyAmount").length){
                msg="友情提示：订单中包含购买数量为0的商品，此类商品购买将不提交！<br>是否确定继续提交订单？";
            }
            nsDialog.jConfirm(msg, null, function (flag) {
                if (flag) {
                    $("#bcgogoReceivableOrderForm").submit();
                }
            });
        }

    });

    $("#mobile").bind("keyup", function () {
        $(this).val($(this).val().replace(/[^\d]+/g, ""));
    });

    $(".J_ModifyAmount").bind("keyup", function () {
        $(this).val(APP_BCGOGO.StringFilter.inputtingNumberFilter($(this).val(), 1)*1);

        var $tr = $(this).closest("tr");
        $tr.find(".J_itemTotal").text($tr.find(".J_itemPrice").val()*$(this).val());
        calculateOrderTotalAmount();
    });

    $(".J_subtractBtn").bind("click", function () {
        var $tr = $(this).closest("tr");
        var $amountDom = $tr.find(".J_ModifyAmount");
        var amount = G.normalize($amountDom.val(),"0")*1;
        if(amount>1){
            $amountDom.val(amount-1);
            $tr.find(".J_itemTotal").text($tr.find(".J_itemPrice").val()*$amountDom.val());
            calculateOrderTotalAmount();
        }
    });
    $(".J_addBtn").bind("click", function () {
        var $tr = $(this).closest("tr");
        var $amountDom = $tr.find(".J_ModifyAmount");
        var amount = G.normalize($amountDom.val(),"0")*1;
        if(amount<99999){
            $amountDom.val(amount+1);
            $tr.find(".J_itemTotal").text($tr.find(".J_itemPrice").val()*$amountDom.val());
            calculateOrderTotalAmount();
        }
    });

    function calculateOrderTotalAmount(){
        var orderTotalAmount = 0;
        $(".J_itemTotal").each(function(){
            orderTotalAmount+=$(this).text()*1;
        });
        $("#orderTotalAmountSpan").text(dataTransition.rounding(orderTotalAmount,0));
    }

    $("#address").bind("blur",function(){
        $("#addressDetailSpan").text(generateAddressDetail());
    });

    $("#contact,#mobile").bind("blur",function(){
        $("#contactInfoSpan").text($("#contact").val()+" "+$("#mobile").val());
    });


    provinceBind();
    $("#provinceNo").bind("change",function(){
        $("#province").val($(this).val());
        $("#cityNo option").remove();
        $("#regionNo option").remove();
        cityBind($("#province").val());
        $("#cityNo").change();
        $("#addressDetailSpan").text(generateAddressDetail());
    });
    $("#cityNo").bind("change",function(){
        $("#city").val($(this).val());
        $("#regionNo option").remove();
        regionBind($("#city").val());
        $("#regionNo").change();
        $("#addressDetailSpan").text(generateAddressDetail());
    });

    $("#regionNo").bind("change",function(){
        $("#region").val($(this).val());
        $("#addressDetailSpan").text(generateAddressDetail());
    });

    $("#provinceNo").val($("#province").val());
    $("#provinceNo").change();
    $("#cityNo").val($("#city").val());
    $("#cityNo").change();
    $("#regionNo").val($("#region").val());


    function generateAddressDetail(){
        var addressDetail = "";
        if(!G.isEmpty($("#provinceNo option:selected").text())){
            addressDetail+=$("#provinceNo option:selected").text();
        }
        if(!G.isEmpty($("#cityNo option:selected").text())){
            addressDetail+=$("#cityNo option:selected").text();
        }
        if(!G.isEmpty($("#regionNo option:selected").text())){
            addressDetail+=$("#regionNo option:selected").text();
        }
        if(!G.isEmpty($("#address").val())){
            addressDetail+=$("#address").val();
        }
        return addressDetail;
    }
    function cityBind(parentNo) {
        var r = APP_BCGOGO.Net.syncGet({url: "shop.do?method=selectarea",
            data: {"parentNo": parentNo}, dataType: "json"});
        if (!r || r.length == 0) return;
        else {
            for (var i = 0, l = r.length; i < l; i++) {
                var option = $("<option>")[0];
                option.value = r[i].no;
                option.innerHTML = r[i].name;
                $("#cityNo")[0].appendChild(option);
            }
        }
    }

    function regionBind(parentNo) {
        var r = APP_BCGOGO.Net.syncGet({url: "shop.do?method=selectarea",
            data: {"parentNo": parentNo}, dataType: "json"});
        if (!r || r.length == 0) return;
        else {
            for (var i = 0, l = r.length; i < l; i++) {
                var option = $("<option>")[0];
                option.value = r[i].no;
                option.innerHTML = r[i].name;
                $("#regionNo")[0].appendChild(option);
            }
        }
    }
    function provinceBind() {
        var r = APP_BCGOGO.Net.syncGet({url: "shop.do?method=selectarea",
            data: {"parentNo":"1"}, dataType: "json"});
        if (!r || r.length == 0) return;
        else {
            for (var i = 0, l = r.length; i < l; i++) {
                var option = $("<option>")[0];
                option.value = r[i].no;
                option.innerHTML = r[i].name;
                if($("#provinceNo")[0]){
                    $("#provinceNo")[0].appendChild(option);
                }
            }
        }
    }

    /*****In List Pay  Button  start****/
    $("#doCombinedPayBcgogoReceivableOrderBtn").bind("click",function (e) {
        e.preventDefault();
        var totalPayReceivableAmount = $("#totalPayReceivableAmount").text()*1;
        if($(".J_orderCheck:checked").length==0 || totalPayReceivableAmount<=0){
            nsDialog.jAlert("请选择需要合并付款的订单!");
            return;
        }

        var bcgogoReceivableOrderRecordRelationIds = [],paidAmounts=[];
        $(".J_orderCheck:checked").each(function(){
            var currentPayableAmount = $(this).attr("data-current-payable-amount")*1;
            if(currentPayableAmount>0){
                paidAmounts.push($(this).attr("data-current-payable-amount"));
            }
            bcgogoReceivableOrderRecordRelationIds.push($(this).attr("data-bcgogoreceivableordertobepaidrecordrelation-id"));
        });

        $("#bcgogoReceivableOrderRecordRelationIds").val(bcgogoReceivableOrderRecordRelationIds.join(","));
        $("#paidAmounts").val(paidAmounts.join(","));

        if(G.isEmpty($("#bcgogoReceivableOrderRecordRelationIds")) || G.isEmpty($("#paidAmounts").val())){
            nsDialog.jAlert("数据异常!");
            return;
        }
        $("#combinedPayBcgogoReceivableOrderForm").submit();
    });

    $("#doSoftwareFirstPayBtn").live("click",function () {
        var bcgogoReceivableOrderId = $(this).closest(".J_contentItem").find(".J_OrderData").val();
        window.location.href="bcgogoReceivable.do?method=bcgogoReceivableOrderDetail&bcgogoReceivableOrderId="+bcgogoReceivableOrderId;
    });

    $("#doSoftwareInstallmentPayBtn").live("click",function () {
        var $orderData = $(this).closest(".J_contentItem").find(".J_OrderData");
        var bcgogoReceivableOrderId = $orderData.val();
        var currentPayableAmount = $orderData.attr("data-current-payable-amount");
        var bcgogoReceivableOrderRecordRelationId = $orderData.attr("data-bcgogoreceivableordertobepaidrecordrelation-id");

        $("#bcgogoReceivableOrderId").val(bcgogoReceivableOrderId);
        $("#currentPayableAmount").val(currentPayableAmount);
        $("#bcgogoReceivableOrderRecordRelationId").val(bcgogoReceivableOrderRecordRelationId);
        $("#paidAmount").val(currentPayableAmount);

        $("#payBcgogoReceivableOrderForm").attr('action', 'bcgogoReceivable.do?method=instalmentOnLineReceivable');
        $("#payBcgogoReceivableOrderForm").submit();
    });

    $("#doSoftwareOtherPayBtn").live("click",function () {
        var $orderData = $(this).closest(".J_contentItem").find(".J_OrderData");
        var bcgogoReceivableOrderId = $orderData.val();
        var currentPayableAmount = $orderData.attr("data-current-payable-amount");
        var bcgogoReceivableOrderRecordRelationId = $orderData.attr("data-bcgogoreceivableordertobepaidrecordrelation-id");

        $("#bcgogoReceivableOrderId").val(bcgogoReceivableOrderId);
        $("#currentPayableAmount").val(currentPayableAmount);
        $("#bcgogoReceivableOrderRecordRelationId").val(bcgogoReceivableOrderRecordRelationId);
        $("#paidAmount").val(currentPayableAmount);

        $("#receivableMethod").val("UNCONSTRAINED");
        $("#payBcgogoReceivableOrderForm").attr('action', 'bcgogoReceivable.do?method=softwareOnlineReceivable');
        $("#payBcgogoReceivableOrderForm").submit();
    });

    $("#doHardwareInstallmentPayBtn").live("click",function () {
        var $orderData = $(this).closest(".J_contentItem").find(".J_OrderData");
        var bcgogoReceivableOrderId = $orderData.val();
        var currentPayableAmount = $orderData.attr("data-current-payable-amount");
        var bcgogoReceivableOrderRecordRelationId = $orderData.attr("data-bcgogoreceivableordertobepaidrecordrelation-id");

        $("#bcgogoReceivableOrderId").val(bcgogoReceivableOrderId);
        $("#currentPayableAmount").val(currentPayableAmount);
        $("#bcgogoReceivableOrderRecordRelationId").val(bcgogoReceivableOrderRecordRelationId);
        $("#paidAmount").val(currentPayableAmount);

        $("#payBcgogoReceivableOrderForm").attr('action', 'bcgogoReceivable.do?method=hardwareOnlineReceivable');
        $("#payBcgogoReceivableOrderForm").submit();
    });

    /*****In List Pay  Button  End****/



    /*****In Detail Pay  Button  start****/
    $(".J_hardwarePayBcgogoReceivableOrderBtn").bind("click",function (e) {
        e.preventDefault();
        $("#paidAmount").val($("#currentPayableAmount").val());
        $("#payBcgogoReceivableOrderForm").attr('action', 'bcgogoReceivable.do?method=hardwareOnlineReceivable');
        $("#payBcgogoReceivableOrderForm").submit();
    });

    $("#otherSoftwarePayBcgogoReceivableOrderBtn").bind("click",function () {
        $("#paidAmount").val($("#currentPayableAmount").val());
        $("#receivableMethod").val("UNCONSTRAINED");

        $("#payBcgogoReceivableOrderForm").attr('action', 'bcgogoReceivable.do?method=softwareOnlineReceivable');
        $("#payBcgogoReceivableOrderForm").submit();
    });

    $("#installmentSoftwarePayBcgogoReceivableOrderBtn").bind("click",function () {
        var payableAmount = $("input[name='paymentAmount']").val()*1;
        if (payableAmount<=0) {
            nsDialog.jAlert("请输入正确的付款金额");
            return;
        }
        var orderReceivableAmount = $("#orderReceivableAmount").val() * 1;
        if (payableAmount > orderReceivableAmount) {
            nsDialog.jAlert("支付金额不能大于未付总额!");
            return;
        }
        if (payableAmount < $("#currentPayableAmount").val()*1) {
            nsDialog.jAlert("支付金额必须大于本期的应付金额");
            return;
        }

        $("#paidAmount").val(payableAmount);
        $("#payBcgogoReceivableOrderForm").attr('action', 'bcgogoReceivable.do?method=instalmentOnLineReceivable');
        $("#payBcgogoReceivableOrderForm").submit();
    });

    $("#firstSoftwarePayBcgogoReceivableOrderBtn").bind("click",function () {
        var receivableMethod = $("input[name='receivableMethod']:checked").val();
        if(receivableMethod=='INSTALLMENT'){
            var payableAmount = $("input[name='paymentAmount']").val()*1;
            if (payableAmount<=0) {
                nsDialog.jAlert("请输入正确的付款金额");
                return;
            }
            if (payableAmount > $("#orderReceivableAmount").val() * 1) {
                nsDialog.jAlert("支付金额不能大于应付总额!");
                return;
            }
            if (payableAmount < $("#currentPayableAmount").val()*1) {
                nsDialog.jAlert("支付金额必须大于第一期的应付金额");
                return;
            }
            $("#paidAmount").val(payableAmount);
        }else if(receivableMethod=='FULL'){
            $("#paidAmount").val($("#orderReceivableAmount").val());
        }

        if(receivableMethod=='INSTALLMENT'){
            $("#instalmentPlanAlgorithmId").val($("#instalmentSelect").val());
        }else if(receivableMethod=='FULL'){
            $("#instalmentPlanAlgorithmId").val("");
        }

        $("#receivableMethod").val(receivableMethod);
        $("#payBcgogoReceivableOrderForm").attr('action', 'bcgogoReceivable.do?method=softwareOnlineReceivable');
        $("#payBcgogoReceivableOrderForm").submit();
    });
    /***********In Detail Pay Button End*******************/

    $("input[name='paymentAmount']").bind("keyup", function () {
        $(this).val(APP_BCGOGO.StringFilter.inputtingNumberFilter($(this).val(), 1)*1);
    });

    $("input[name='receivableMethod']").bind("change",function(){
        if($(this).val()=='FULL'){
            $("#instalmentPaySelect").hide();
            $("#instalmentPlanAlgorithmDiv").empty();
            $("#instalmentPlanAlgorithmDiv").hide();
            $("#fullPaymentTotalDiv").show();
            $("#installmentPaymentTotalDiv").hide();
        }else{
            $("#instalmentPaySelect").show();
            $("#instalmentSelect").change();
            $("#fullPaymentTotalDiv").hide();
            $("#installmentPaymentTotalDiv").show();
        }
    });

    $("#instalmentSelect").bind("change",function(){
        var planId = $("#instalmentSelect").val();
        if (G.isEmpty(planId)) {
            return;
        }
        var url = "bcgogoReceivable.do?method=getInstalmentPlanAlgorithmsById";
        var data = {instalmentPlanAlgorithmIdStr: planId};
        APP_BCGOGO.Net.asyncPost({
            url: url,
            dataType: "json",
            data:data,
            success: function (result) {
                if (!G.isEmpty(result)) {
                    var terminallyRatios = result.terminallyRatio.split(",");
                    var colHtml ="",titleHtml="",payScaleHtml="",payAmountHtml="";
                    var temp = 0,firstInstallmentPayment=0;
                    $.each(terminallyRatios,function(index, terminallyRatio){
                        colHtml+='<col width="70">';
                        titleHtml+='<td>'+(index+1)+'期</td>';
                        payScaleHtml+='<td>'+ terminallyRatio * 100 + '%</td>';
                        if(index==terminallyRatios.length-1){
                            payAmountHtml+='<td>&yen;'+($("#orderReceivableAmount").val()*1-temp)+'</td>';
                        }else{
                            if(index==0) {
                                firstInstallmentPayment = dataTransition.rounding($("#orderReceivableAmount").val() * terminallyRatio, 0);
                            }
                            temp += dataTransition.rounding($("#orderReceivableAmount").val() * terminallyRatio, 0);
                            payAmountHtml+='<td>&yen;'+dataTransition.rounding($("#orderReceivableAmount").val() * terminallyRatio, 0)+'</td>';
                        }

                    });
                    var instalmentPlanTableHtml="";
                    instalmentPlanTableHtml+='    <table style="table-layout:fixed;width: 100%" border="0" cellspacing="0" class="equal2">';
                    instalmentPlanTableHtml+='        <colgroup>';
                    instalmentPlanTableHtml+='            <col width="70">';
                    instalmentPlanTableHtml+=colHtml;
                    instalmentPlanTableHtml+='        </colgroup>';
                    instalmentPlanTableHtml+='        <tr>';
                    instalmentPlanTableHtml+='            <td class="equal2_title">期 数</td>';
                    instalmentPlanTableHtml+=titleHtml;
                    instalmentPlanTableHtml+='       </tr>';
                    instalmentPlanTableHtml+='        <tr>';
                    instalmentPlanTableHtml+='            <td class="equal2_title">支付比例</td>';
                    instalmentPlanTableHtml+=payScaleHtml;
                    instalmentPlanTableHtml+='       </tr>';
                    instalmentPlanTableHtml+='        <tr>';
                    instalmentPlanTableHtml+='            <td class="equal2_title">付款金额</td>';
                    instalmentPlanTableHtml+=payAmountHtml;
                    instalmentPlanTableHtml+='       </tr>';
                    instalmentPlanTableHtml+='        <tr>';
                    instalmentPlanTableHtml+='            <td class="equal2_title">合计</td>';
                    instalmentPlanTableHtml+='            <td colspan="'+terminallyRatios.length+'"><strong><span class="yellow_color">'+$("#orderReceivableAmount").val()+'</span></strong></td>';
                    instalmentPlanTableHtml+='       </tr>';
                    instalmentPlanTableHtml+='   </table>';

                    $("#instalmentPlanAlgorithmDiv").empty();
                    $("#instalmentPlanAlgorithmDiv").append($("<span>每期付款规则：</span>"));
                    $("#instalmentPlanAlgorithmDiv").append($(instalmentPlanTableHtml));
                    $("#instalmentPlanAlgorithmDiv").show();
                    $("#firstInstallmentPayment").text(firstInstallmentPayment);
                    $("#currentPayableAmount").val(firstInstallmentPayment);
                    $("input[name='paymentAmount']").val(firstInstallmentPayment);

                    if (result.name == "6期") {
                        $("#secondInstalmentTable").css("display", "none");
                        $("#firstInstalmentTable").css("display", "block");
                        $("#instalmentSelectName").val(result.name);
                        $("#payableAmount").val($("#firstAmountTdSix").text());

                    } else if (result.name == "12期") {
                        $("#secondInstalmentTable").css("display", "block");
                        $("#firstInstalmentTable").css("display", "none");
                        $("#instalmentSelectName").val(result.name);
                        $("#payableAmount").val($("#firstAmountTd").text());
                    }
                }
            }
        });
    });

    $("#checkAllOrderBtn").bind("click",function(){
        $(".J_orderCheck").attr("checked",this.checked);
        statTotalPayReceivableAmount();
    });
    $(".J_orderCheck").live("click",function(){
        $("#checkAllOrderBtn").attr("checked",$(".J_orderCheck").length==$(".J_orderCheck:checked").length);
        statTotalPayReceivableAmount();
    });

    function statTotalPayReceivableAmount(){
        var total = 0;
        $(".J_orderCheck:checked").each(function(){
            total+=$(this).attr("data-current-payable-amount")*1;
        });
        $("#totalPayReceivableAmount").text(total);
    }


    $("#paymentStatuses,#searchMonths").bind("change",function(){
        $("#searchBcgogoReceivableOrderForm").submit();
    });


    $("#searchNonPaymentOrderBtn").bind("click",function(e){
        e.preventDefault();
        $("#searchBcgogoReceivableOrderForm").find("[name='paymentStatuses']").val("PARTIAL_PAYMENT,NON_PAYMENT");
        $("#searchBcgogoReceivableOrderForm").submit();
    });
    $("#searchFullPaymentOrderBtn").bind("click",function(e){
        e.preventDefault();
        $("#searchBcgogoReceivableOrderForm").find("[name='paymentStatuses']").val("FULL_PAYMENT");
        $("#searchBcgogoReceivableOrderForm").submit();
    });
    $("#searchShippedOrderBtn").bind("click",function(e){
        e.preventDefault();
        $("#searchBcgogoReceivableOrderForm").find("[name='paymentStatuses']").val("SHIPPED");
        $("#searchBcgogoReceivableOrderForm").submit();
    });
    $("#searchCanceledOrderBtn").bind("click",function(e){
        e.preventDefault();
        $("#searchBcgogoReceivableOrderForm").find("[name='paymentStatuses']").val("CANCELED");
        $("#searchBcgogoReceivableOrderForm").submit();
    });

    $("#cancelBcgogoReceivableOrderBtn").bind("click",function(e){
        e.preventDefault();
        $("#cancelBcgogoReceivableOrderReasonDialog").dialog({ width: 420, modal:true});
    });

    $(".J_CancelBcgogoReceivableOrderBtn").live("click",function(){
        var orderId = $(this).closest(".J_contentItem").find(".J_OrderData").val();
        $("#cancelBcgogoReceivableOrderReasonForm").find("input[name='bcgogoReceivableOrderId']").val(orderId);
        $("#cancelBcgogoReceivableOrderReasonDialog").dialog({ width: 420, modal:true});
    });

    $("#cancelBcgogoReceivableOrderReasonConfirmBtn").bind("click",function(){
        if(G.isEmpty($("#cancelBcgogoReceivableOrderReason").val())){
            nsDialog.jAlert("拒绝理由必须填写！");
        }else{
            $("#cancelBcgogoReceivableOrderReasonForm").ajaxSubmit(function(){
                $("#cancelBcgogoReceivableOrderReasonDialog").dialog("close");
                $("#cancelBcgogoReceivableOrderReasonForm")[0].reset();
                if(!G.isEmpty($("#searchBcgogoReceivableOrderForm"))){
                    $("#searchBcgogoReceivableOrderForm").submit();
                }else{
                    window.location.reload();
                }
            });

        }
    });
    $("#cancelBcgogoReceivableOrderReasonCancelBtn").bind("click",function(){
        $("#cancelBcgogoReceivableOrderReasonDialog").dialog("close");
    });


    $("#searchBcgogoReceivableOrderForm").submit(function (e) {
        e.preventDefault();

        var param = $("#searchBcgogoReceivableOrderForm").serializeArray();
        var data = {};
        $.each(param, function (index, val) {
            data[val.name] = val.value;
        });
        $("#checkAllOrderBtn").attr("checked",false);
        $("#bcgogoReceivableOrder_dd").empty();
        $("#totalPayReceivableAmount").text("0");
        APP_BCGOGO.Net.asyncPost({
            url: "bcgogoReceivable.do?method=searchBcgogoReceivableOrderList",
            dataType: "json",
            data:data,
            success: function (result) {
                drawBcgogoReceivableOrderHtml(result);
                initPages(result, "bcogogReceivaleOrders", "bcgogoReceivable.do?method=searchBcgogoReceivableOrderList", '', "drawBcgogoReceivableOrderHtml", '', '', data, '');
            }
        });
        return false;
    });

    //初始化待支付记录
    $("#searchBcgogoReceivableOrderForm").submit();

});

function parseBcgogoReceivableOrderStatusName(status){
    var statusName = "";
    if(!G.isEmpty(status)){
        switch (status) {
            case "PARTIAL_PAYMENT":
                var statusName = "<span style='color: #FF6600'>待支付</span>";
                break;
            case "FULL_PAYMENT":
                var statusName = "<span style='color: #008000'>已支付</span>";
                break;
            case "NON_PAYMENT":
                var statusName = "<span style='color: #FF6600'>待支付</span>";
                break;
            case "SHIPPED":
                var statusName = "<span style='color: #008000'>已发货</span>";
                break;
            case "CANCELED":
                var statusName = "<span style='color: #666666'>交易已取消</span>";
                break;
            default:
                G.error("BcgogoReceivableOrder status error![bcgogoReceivableOrder.js]");
                break;
        }
    }
    return statusName;
}

function drawBcgogoReceivableOrderHtml(json) {
    $("#bcgogoReceivableOrder_dd").empty();
    if(json==null || json[0]==null || json[0].length == 0 ){
        $("#payNoDataDiv").show();
        return;
    }
    $("#payNoDataDiv").hide();
    $.each(json[0], function (index, bcgogoReceivableOrderDTO) {
        var bcgogoReceivableOrderItemDTOList = bcgogoReceivableOrderDTO.bcgogoReceivableOrderItemDTOList;
        var bcgogoReceivableOrderToBePaidRecordDTO = bcgogoReceivableOrderDTO.bcgogoReceivableOrderToBePaidRecordDTO;
        var instalmentPlanDTO = bcgogoReceivableOrderDTO.instalmentPlanDTO;

        var rowHtml = "";
        rowHtml+='<dl class="content-item J_contentItem" style="width: auto">';
        rowHtml+='    <dt>';
        rowHtml+='        <div class="bg-top-hr"></div>';
        rowHtml+='        <div class="bar-tab">';
        if(!G.isEmpty(bcgogoReceivableOrderToBePaidRecordDTO) && ((bcgogoReceivableOrderDTO.paymentType == "SOFTWARE" && bcgogoReceivableOrderDTO.status=="PARTIAL_PAYMENT") || (bcgogoReceivableOrderDTO.paymentType == "HARDWARE" && bcgogoReceivableOrderDTO.status=="NON_PAYMENT"))){
            rowHtml+='            <input type="checkbox" class="J_orderCheck J_OrderData" value="'+bcgogoReceivableOrderDTO.idStr+'" ';
        }else{
            rowHtml+='            <input type="hidden" class="J_OrderData" value="'+bcgogoReceivableOrderDTO.idStr+'" ';
        }
        if(!G.isEmpty(bcgogoReceivableOrderDTO.currentPayableAmount)){
            rowHtml+='          data-current-payable-amount="'+bcgogoReceivableOrderDTO.currentPayableAmount+'" ';
        }
        if(!G.isEmpty(bcgogoReceivableOrderToBePaidRecordDTO)){
            rowHtml+='          data-bcgogoreceivableordertobepaidrecordrelation-id="'+bcgogoReceivableOrderToBePaidRecordDTO.bcgogoReceivableOrderRecordRelationIdStr+'"';
        }
        rowHtml+='          /> ';
        rowHtml+='            订单号：<a href="bcgogoReceivable.do?method=bcgogoReceivableOrderDetail&bcgogoReceivableOrderId='+bcgogoReceivableOrderDTO.idStr+'" class="blue_color">'+bcgogoReceivableOrderDTO.receiptNo+'</a>&nbsp;';
        rowHtml+='        购买时间：'+bcgogoReceivableOrderDTO.createdTimeStr+' &nbsp;';
        rowHtml+='            <a class="button-rate-additional word-blue a-button" href="bcgogoReceivable.do?method=bcgogoReceivableOrderDetail&bcgogoReceivableOrderId='+bcgogoReceivableOrderDTO.idStr+'">查看订单详情</a>';
        rowHtml+='            <div class="cl"></div>';
        rowHtml+='        </div>';
        rowHtml+='    </dt>';
        rowHtml+='    <dd>';
        rowHtml+='        <table class="item-content" cellpadding="0" cellspacing="0">';
        if(!G.isEmpty(bcgogoReceivableOrderItemDTOList)){
            $.each(bcgogoReceivableOrderItemDTOList, function (itemIndex, bcgogoReceivableOrderItemDTO) {
                var productInfo = bcgogoReceivableOrderItemDTO.productName+"<br>"+ G.normalize(bcgogoReceivableOrderItemDTO.productKind);
                if(!G.isEmpty(bcgogoReceivableOrderItemDTO.productType)){
                    productInfo+="【"+bcgogoReceivableOrderItemDTO.productType+"】";
                }

                rowHtml+='            <tr>';
                rowHtml+='                <td class="item-product-info" style="width: 300px">';
                if(bcgogoReceivableOrderDTO.paymentType=="HARDWARE"){
                    if(bcgogoReceivableOrderItemDTO.canShow){
                        rowHtml+='                    <div class="bcgogo-info-icon">';
                        rowHtml+='                        <a href="bcgogoProduct.do?method=bcgogoProductDetail&bcgogoProductId='+bcgogoReceivableOrderItemDTO.productIdStr+'" target="_blank">';
                        rowHtml+='                            <img src="'+bcgogoReceivableOrderItemDTO.imageUrl+'" style="width: 60px;height: 60px;"/>';
                        rowHtml+='                        </a>';
                        rowHtml+='                    </div>';
                        rowHtml+='                    <div class="bcgogo-info-details"><a href="bcgogoProduct.do?method=bcgogoProductDetail&bcgogoProductId='+bcgogoReceivableOrderItemDTO.productIdStr+'" target="_blank" class="blue_color">'+productInfo+'</a></div>';
                    }else{
                        rowHtml+='                    <div class="bcgogo-info-icon">';
                        rowHtml+='                       <img src="'+bcgogoReceivableOrderItemDTO.imageUrl+'" style="width: 60px;height: 60px;"/>';
                        rowHtml+='                    </div>';
                        rowHtml+='                    <div class="bcgogo-info-details">'+productInfo+'</div>';
                    }
                }else{
                    rowHtml+='                    <div class="bcgogo-info-icon">';
                    rowHtml+='                       <img src="'+bcgogoReceivableOrderItemDTO.imageUrl+'" style="width: 60px;height: 60px;"/>';
                    rowHtml+='                    </div>';
                    rowHtml+='                    <div class="bcgogo-info-details">'+productInfo+'</div>';
                }

                rowHtml+='                    <div class="cl"></div>';
                rowHtml+='                </td>';
                rowHtml+='                <td class="item-product-unit-price" style="width: 80px"> '+bcgogoReceivableOrderItemDTO.price+'元';
                if('YEARLY'==bcgogoReceivableOrderDTO.chargeType){
                    rowHtml+='            <div class="yellow_color">(按年收费)</div>';
                }
                rowHtml+='                </td>';
                rowHtml+='                <td class="item-product-unit-price" style="width: 80px"> '+bcgogoReceivableOrderItemDTO.amount+'条</td>';
                if(itemIndex==0){
                    rowHtml+='                <td rowspan="'+bcgogoReceivableOrderItemDTOList.length+'" class="item-product-payables" style="width: 150px;text-align: center;" >';
                    rowHtml+='                    <strong><div class="yellow_color" style="height: 18px;">'+bcgogoReceivableOrderDTO.totalAmount+'</div></strong>';
                    if('YEARLY'==bcgogoReceivableOrderDTO.chargeType){
                        rowHtml+='            <div>(下一年应付)</div>';
                    }else{
                        if(!G.isEmpty(bcgogoReceivableOrderToBePaidRecordDTO)){
                            if (!G.isEmpty(instalmentPlanDTO) && bcgogoReceivableOrderDTO.paymentType == "SOFTWARE" && bcgogoReceivableOrderToBePaidRecordDTO.receivableMethod == "INSTALLMENT") {
                                rowHtml+='                <div>(本次应付第<span class="yellow_color" style="font-weight:bold">'+bcgogoReceivableOrderDTO.currentPeriodNumberInfo+'</span>期,共<span class="yellow_color" style="font-weight:bold">' + bcgogoReceivableOrderDTO.currentPayableAmount + '</span>元)</div>';
                            }else if(bcgogoReceivableOrderDTO.paymentType == "SOFTWARE" && bcgogoReceivableOrderToBePaidRecordDTO.receivableMethod == "UNCONSTRAINED"){
                                rowHtml+='                <div>(本次应付金额：'+bcgogoReceivableOrderDTO.currentPayableAmount+'元)</div>';
                            }
                        }
                    }

                    rowHtml+='                </td>';
                    rowHtml+='                <td rowspan="'+bcgogoReceivableOrderItemDTOList.length+'" class="item-product-order-status" style="width:120px;text-align: center">';
                    if('YEARLY'==bcgogoReceivableOrderDTO.chargeType){
                        rowHtml+='                  <strong><span class="yellow_color">第1年免费,<br>第2年待支付</span></strong>';
                    }else{
                        rowHtml+='                  <strong>'+parseBcgogoReceivableOrderStatusName(bcgogoReceivableOrderDTO.status)+'</strong>';
                    }
                    rowHtml+='                </td>';
                    rowHtml+='                <td rowspan="'+bcgogoReceivableOrderItemDTOList.length+'" class="item-product-operating" style="width:80px;">';
                    if('ONE_TIME'==bcgogoReceivableOrderDTO.chargeType && !G.isEmpty(bcgogoReceivableOrderToBePaidRecordDTO) && (bcgogoReceivableOrderDTO.status=="PARTIAL_PAYMENT" || bcgogoReceivableOrderDTO.status=="NON_PAYMENT")){
                        if (bcgogoReceivableOrderDTO.paymentType == "SOFTWARE") {
                            if (bcgogoReceivableOrderDTO.status=="NON_PAYMENT") {//首次软件付款  跳详细
                                rowHtml+='                    <div class="button-storage button-yellow-deep-gradient" id="doSoftwareFirstPayBtn">在线付款</div>';
                            } else if (bcgogoReceivableOrderDTO.status=="PARTIAL_PAYMENT" && bcgogoReceivableOrderToBePaidRecordDTO.receivableMethod == "INSTALLMENT") {//软件分期付款
                                rowHtml+='                    <div class="button-storage button-yellow-deep-gradient" id="doSoftwareInstallmentPayBtn">在线付款</div>';
                            } else if (bcgogoReceivableOrderDTO.status=="PARTIAL_PAYMENT" && bcgogoReceivableOrderToBePaidRecordDTO.receivableMethod == "UNCONSTRAINED") {
                                rowHtml+='                    <div class="button-storage button-yellow-deep-gradient" id="doSoftwareOtherPayBtn">在线付款</div>';
                            }
                        } else if (bcgogoReceivableOrderDTO.paymentType == "HARDWARE") {
                            rowHtml+='                    <div class="button-storage button-yellow-deep-gradient" id="doHardwareInstallmentPayBtn">在线付款</div>';
                        }
                    }

                    if(bcgogoReceivableOrderDTO.paymentType=="HARDWARE" && bcgogoReceivableOrderDTO.status=="NON_PAYMENT"){
                        rowHtml+='                <div><a class="blue_color J_CancelBcgogoReceivableOrderBtn">取消交易</a></div>';
                    }
                    rowHtml+='                </td>';
                }

                rowHtml+='            </tr>';
            });
        }

        rowHtml+='        </table>';

        rowHtml+='    </dd>';
        rowHtml+='</dl>';

        if('ONE_TIME'==bcgogoReceivableOrderDTO.chargeType && bcgogoReceivableOrderDTO.paymentType == "SOFTWARE" && !G.isEmpty(bcgogoReceivableOrderDTO.instalmentPlanDTO) && !G.isEmpty(bcgogoReceivableOrderDTO.instalmentPlanDTO.instalmentPlanItemDTOList) ){
            var colHtml ="",titleHtml="",payScaleHtml="",payAmountHtml="",payStatusHtml="",endTimeHtml="";
            $.each(bcgogoReceivableOrderDTO.instalmentPlanDTO.instalmentPlanItemDTOList,function(index, instalmentPlanItemDTO){
                colHtml+='<col width="115">';
                titleHtml+='<td>'+instalmentPlanItemDTO.periodNumber+'期</td>';
                payScaleHtml+='<td>'+ instalmentPlanItemDTO.proportion * 100 + '%</td>';
                payAmountHtml+='<td>&yen;'+instalmentPlanItemDTO.currentAmount+'</td>';
                if(instalmentPlanItemDTO.status=="FULL_PAYMENT"){
                    payStatusHtml+='<td><span class="paySucess">已付</span></td>';
                    endTimeHtml+='<td>--</td>';
                }else if(instalmentPlanItemDTO.status=="PARTIAL_PAYMENT"){
                    payStatusHtml+='<td><strong><span style="color: #008000">已付&yen;'+instalmentPlanItemDTO.paidAmount+'</span></strong></td>';
                    endTimeHtml+='<td>'+instalmentPlanItemDTO.endTimeStr+'</td>';
                }else if(instalmentPlanItemDTO.status=="NON_PAYMENT"){
                    payStatusHtml+='<td>待付</td>';
                    endTimeHtml+='<td>'+instalmentPlanItemDTO.endTimeStr+'</td>';
                }
            });
            rowHtml+='<div class="paySucess-content" style="margin-bottom: 10px; margin-top: -22px;padding: 10px;overflow-x:auto;">';
            rowHtml+='    <div style="float: left;width: 70px"><h1>付款期数</h1></div>';
            rowHtml+='    <div>共<span style="font-weight:bold">'+instalmentPlanDTO.periods+'</span>期,已付总额:<span style="color: #008000;font-weight:bold">'+bcgogoReceivableOrderDTO.receivedAmount+'</span>元,未付总额:<span class="yellow_color" style="font-weight:bold">'+bcgogoReceivableOrderDTO.receivableAmount+'</span>元.</div>';

            rowHtml+='    <table style="table-layout:fixed;width: 100%" border="0" cellspacing="0" class="equal2">';
            rowHtml+='        <colgroup>';
            rowHtml+='            <col width="80">';
            rowHtml+=colHtml;
            rowHtml+='        </colgroup>';
            rowHtml+='        <tr>';
            rowHtml+='            <td class="equal2_title">期 数</td>';
            rowHtml+=titleHtml;
            rowHtml+='       </tr>';
            rowHtml+='        <tr>';
            rowHtml+='            <td class="equal2_title">支付比例</td>';
            rowHtml+=payScaleHtml;
            rowHtml+='       </tr>';
            rowHtml+='        <tr>';
            rowHtml+='            <td class="equal2_title">付款金额</td>';
            rowHtml+=payAmountHtml;
            rowHtml+='       </tr>';
            rowHtml+='       <tr>';
            rowHtml+='           <td class="equal2_title">付款状态</td>';
            rowHtml+=payStatusHtml;
            rowHtml+='       </tr>';
            rowHtml+='       <tr>';
            rowHtml+='           <td class="equal2_title">付款截止</td>';
            rowHtml+=endTimeHtml;
            rowHtml+='       </tr>';
            rowHtml+='   </table>';
            rowHtml+='</div>';
        }
        if('ONE_TIME'==bcgogoReceivableOrderDTO.chargeType && !G.isEmpty(bcgogoReceivableOrderDTO.bcgogoReceivableOrderPaidRecordDTOList)){
            rowHtml+='<div class="paySucess-content" style="margin-bottom: 10px; margin-top: -12px;padding: 10px;">';
            rowHtml+='    <h1>付款记录</h1>';

            rowHtml+='    <div>';
            rowHtml+='        <ul>';
            $.each(bcgogoReceivableOrderDTO.bcgogoReceivableOrderPaidRecordDTOList,function(recordIndex, bcgogoReceivableOrderPaidRecordDTO){
                rowHtml+='           <li>'+bcgogoReceivableOrderPaidRecordDTO.recordPaymentTimeStr+' &nbsp;&nbsp;'+(bcgogoReceivableOrderPaidRecordDTO.paymentMethod=="DOOR_CHARGE"?"现金支付":"银联支付")+bcgogoReceivableOrderPaidRecordDTO.recordPaidAmount+'元；</li>';
            });
            rowHtml+='       </ul>';
            rowHtml+='   </div>';
            rowHtml+='</div>';
        }
        $("#bcgogoReceivableOrder_dd").append($(rowHtml));
    });
}

