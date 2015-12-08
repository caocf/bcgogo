$(document).ready(function () {
    initQQTalk($("#bcgogoQQ"));

    $(".J_smallImageSwitch").bind("click",function(){
        var index = $(this).attr("data-index");
        $(".J_smallImageSwitch").closest("div").removeClass("actived");
        $(this).closest("div").addClass("actived");
        $(".J_bigImageSwitch").hide();
        $("#bigImageSwitch_"+index).show();
    });

    $(".J_ModifyAmount").bind("keyup", function () {
        $(this).val(APP_BCGOGO.StringFilter.inputtingNumberFilter($(this).val(), 1)*1);
        if($("#productScene").val()=="MULTI_BUY"){
            if($(this).val()*1>0){
                var $selectType = $(this).closest(".J_TypeAmountLine").find(".J_selectType");
                if(!$selectType.hasClass("card-select")){
                    $selectType.addClass("card-select").removeClass("card-default");
                }
            }else{
                var $selectType = $(this).closest(".J_TypeAmountLine").find(".J_selectType");
                if($selectType.hasClass("card-select")){
                    $selectType.addClass("card-default").removeClass("card-select");
                }
            }
        }
    });

    $("#skjAmount").bind("keyup", function () {
        $(this).val(APP_BCGOGO.StringFilter.inputtingNumberFilter($(this).val(), 1)*1);
        if($(this).val()*1>0){
            if(!$("#skj_checkbox").attr("checked")){
                $("#skj_checkbox").attr("checked",true);
            }
        }else{
            if($("#skj_checkbox").attr("checked")){
                $("#skj_checkbox").attr("checked",false);
            }
        }
    });


    $(".J_subtractBtn").bind("click", function () {
        var $amountDom = $(this).parent(".J_ProductAmountDiv").find(".J_ModifyAmount");
        var amount = G.normalize($amountDom.val(),"0")*1;
        if($("#productScene").val()=="MULTI_BUY"){
            if(amount>0){
                $amountDom.val(amount-1);
            }
            if($amountDom.val()*1<=0){
                var $selectType = $(this).closest(".J_TypeAmountLine").find(".J_selectType");
                if($selectType.hasClass("card-select")){
                    $selectType.addClass("card-default").removeClass("card-select");
                }
            }
        }else{
            if(amount>1){
                $amountDom.val(amount-1);
            }
        }

    });
    $(".J_addBtn").bind("click", function () {
        var $amountDom = $(this).parent(".J_ProductAmountDiv").find(".J_ModifyAmount");
        var amount = G.normalize($amountDom.val(),"0")*1;
        if(amount<99999){
            $amountDom.val(amount+1);
        }
        if($("#productScene").val()=="MULTI_BUY" && $amountDom.val()*1>0){
            var $selectType = $(this).closest(".J_TypeAmountLine").find(".J_selectType");
            if(!$selectType.hasClass("card-select")){
                changeSelectTypeToSelected($selectType);
            }
        }
    });

    $("#skj_checkbox").bind("change",function(){
        if($(this).attr("checked")){
            $("#skjAmount").val("1");
        }else{
            $("#skjAmount").val("0");
        }
    });
    $(".J_selectType").live("click",function(){
        if($(this).hasClass("card-disable")) return;
        if($(this).hasClass("card-select")){
            $(this).removeClass("card-select").addClass("card-default");
            if($("#productScene").val()=="MULTI_BUY"){
                $(this).closest(".J_TypeAmountLine").find("input[name='amount']").val("0");
            }else{
                $("input[name='amount']").removeAttr("data-product-property-id");
            }
            $("#smallImage_default").click();
        }else{
            if($("#productScene").val()!="MULTI_BUY"){
                $(".J_selectType").each(function(){
                    $(this).addClass("card-default").removeClass("card-select");
                });
                $("input[name='amount']").attr("data-product-property-id",$(this).attr("data-product-property-id"));
            }else{
                $(this).closest(".J_TypeAmountLine").find("input[name='amount']").val("1");
            }
            $("#smallImage_"+$(this).attr("data-product-property-id")).click();

            changeSelectTypeToSelected($(this));
        }

    });
    function changeSelectTypeToSelected($selectType){
        $selectType.addClass("card-select").removeClass("card-default");
        $("#bcgogoProductPrice").text($selectType.attr("data-product-property-price"));
        var $kindObjectDom,dataProductPropertyIds;
        $(".J_selectKind").each(function(){
            dataProductPropertyIds = $(this).attr("data-product-property-ids");
            if(dataProductPropertyIds.indexOf($selectType.attr("data-product-property-id"))>-1){
                $kindObjectDom = $(this);
            }
        });
        if(!G.isEmpty($kindObjectDom)){
            $kindObjectDom.addClass("card-select").removeClass("card-default");
        }
        if(!G.isEmpty(dataProductPropertyIds)){
            changeTypeToDisable(dataProductPropertyIds);
        }
    }
    $(".J_selectKind").live("click",function(){
        if($(this).hasClass("card-select")){
            $(this).removeClass("card-select").addClass("card-default");
            if($("#productScene").val()!="MULTI_BUY"){
                $("input[name='amount']").removeAttr("data-product-property-id");
            }
            $("#smallImage_default").click();
            changeTypeToDisable(null);
        }else{
            $(".J_selectKind").each(function(){
                $(this).addClass("card-default").removeClass("card-select");
            });
            $(this).addClass("card-select").removeClass("card-default");
            changeTypeToDisable($(this).attr("data-product-property-ids"));
        }

    });

    function changeTypeToDisable(dataProductPropertyIds){
        $(".J_selectType").each(function(){
            if(G.isEmpty(dataProductPropertyIds)){
                $(this).addClass("card-default").removeClass("card-select").removeClass("card-disable");
            }else{
                if(dataProductPropertyIds.indexOf($(this).attr("data-product-property-id"))>-1){
                    $(this).removeClass("card-disable");
                    if(!$(this).hasClass("card-select")){
                        $(this).addClass("card-default");
                    }
                }else{
                    $(this).addClass("card-disable").removeClass("card-default").removeClass("card-select");
                }
            }
            if($("#productScene").val()=="MULTI_BUY"){
                if(!$(this).hasClass("card-select")){
                    $(this).closest(".J_TypeAmountLine").find("input[name='amount']").val("0");
                }
            }
        });
    }

    $("#createBcgogoOrderBtn").bind("click",function(){
        var $form = $("#bcgogoProductForm");
        $form.empty();

        var verifyResult = false;
        $(".J_selectType").each(function(){
            if($(this).hasClass("card-select")){
                verifyResult = true;
                return;
            }
        });
        if(!verifyResult){
            nsDialog.jAlert("请选择需要购买商品的性质和类型！");
            return false;
        }

        var productScene = $("#productScene").val();
        if(productScene=="ATTACH_BUY"){
            if($("input[name='amount']").val()*1<=0){
                nsDialog.jAlert("请填写需要购买商品的数量！");
                return false;
            }
            $form.append($("<input type='hidden' name='bcgogoReceivableOrderItemDTOList[0].productId' value='"+$("#productId").val()+"'/>"))
            $form.append($("<input type='hidden' name='bcgogoReceivableOrderItemDTOList[0].amount' value='"+$("input[name='amount']").val()+"'/>"))
            $form.append($("<input type='hidden' name='bcgogoReceivableOrderItemDTOList[0].productPropertyId' value='"+$("input[name='amount']").attr("data-product-property-id")+"'/>"))

            if($("#skj_checkbox").attr("checked")){
                if($("#skjAmount").val()*1<=0){
                    nsDialog.jAlert("请填写需要购买磁条读卡器的数量！");
                    return false;
                }
                $form.append($("<input type='hidden' name='bcgogoReceivableOrderItemDTOList[1].productId' value='"+$("#skjAmount").attr("data-product-id")+"'/>"))
                $form.append($("<input type='hidden' name='bcgogoReceivableOrderItemDTOList[1].amount' value='"+$("#skjAmount").val()+"'/>"))
                $form.append($("<input type='hidden' name='bcgogoReceivableOrderItemDTOList[1].productPropertyId' value='"+$("#skjAmount").attr("data-product-property-id")+"'/>"))
            }
        }else if(productScene=="MULTI_BUY"){
            $("input[name='amount']").each(function(index,value){
                if($(this).val()*1>0){
                    $form.append($("<input type='hidden' name='bcgogoReceivableOrderItemDTOList["+index+"].productId' value='"+$("#productId").val()+"'/>"))
                    $form.append($("<input type='hidden' name='bcgogoReceivableOrderItemDTOList["+index+"].amount' value='"+$(this).val()+"'/>"))
                    $form.append($("<input type='hidden' name='bcgogoReceivableOrderItemDTOList["+index+"].productPropertyId' value='"+$(this).attr("data-product-property-id")+"'/>"))
                }
            });
        }else{
            if($("input[name='amount']").val()*1<=0){
                nsDialog.jAlert("请填写需要购买商品的数量！");
                return false;
            }
            $form.append($("<input type='hidden' id='bcgogoReceivableOrderItemDTOList0.productId' name='bcgogoReceivableOrderItemDTOList[0].productId' value='"+$("#productId").val()+"'/>"))
            $form.append($("<input type='hidden' name='bcgogoReceivableOrderItemDTOList[0].amount' value='"+$("input[name='amount']").val()+"'/>"))
            $form.append($("<input type='hidden' name='bcgogoReceivableOrderItemDTOList[0].productPropertyId' value='"+$("input[name='amount']").attr("data-product-property-id")+"'/>"))
        }

        var verifyResult = true;
        $form.find("input").each(function(){
           if(G.isEmpty($(this).val())){
               verifyResult = false;
               return false;
           }
        });
        if(verifyResult){
            $form.submit();
        }else{
            nsDialog.jAlert("数据异常,请刷新页面或者联系客服！");
            return false;
        }
    });
});
