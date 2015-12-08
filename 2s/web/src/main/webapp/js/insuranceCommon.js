$(function () {
    $("#toInsuranceBtn").live("click", function (e) {
        e.preventDefault();
        var dealingType=$("#dealingType").val();

        if(dealingType==""){
            dealingType="待结算";
        }
        if (!$("#id").val() &&  !$("#insuranceOrderId").val() ) {
            nsDialog.jAlert("当前单据还未派单无法生成保险单，请先派单!");
            return;
        } else if(!G.isEmpty($("#receiptNo").val()) && !G.isEmpty($("#insuranceOrderId").val())){
            window.open("insurance.do?method=createInsuranceOrder&repairOrderId=" + $("#id").val() + "&repairDraftOrderId=" + $("#draftOrderIdStr").val()+
                "&dealingType="+dealingType, "_blank");
        } else if ($("#status").val() == "REPAIR_REPEAL") {
            nsDialog.jAlert("已作废无法无法生成保险单！");
            return;
        } else {
            window.open("insurance.do?method=createInsuranceOrder&repairOrderId=" + $("#id").val() + "&repairDraftOrderId=" + $("#draftOrderIdStr").val() +
                "&insuranceOrderId=" + $("#insuranceOrderId").val()+"&dealingType="+dealingType, "_self");

        }
    });
});