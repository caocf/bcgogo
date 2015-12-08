$().ready(function(){

    $(".itemChk").live("click",function(){
        $(".select_all").attr("checked",$(".itemChk").length==$(".itemChk:checked").length)
    });

//    $('[name="guaranteePeriod"]')

    $(".batchOffSalesBtn").click(function(){
        var productIdArr=new Array();
        $(".itemChk").each(function(){
            if($(this).attr("checked")&&!G.isEmpty($(this).attr("productId"))){
                productIdArr.push($(this).attr("productId"))
            }
        });
        if(productIdArr.length==0){
            nsDialog.jAlert("请选择需下架的商品。");
            return;
        }

        nsDialog.jConfirm("是否确认要下架所选商品？",null,function(flag){
            if(flag){
                goodsInOffSales(productIdArr.toString(),"off",refreshPage);
            }
        });
    });

    $("#batchInSalesBtn").click(function(){
//        if( $(".itemChk:checked").length==0){
//            nsDialog.jAlert("请选择要上架的商品。");
//            return;
//        }
        var errorMsg="";
        $(".itemChk").each(function(index){
            var $tr=$(this).closest("tr");
            var errorItem="";
            var inSalesAmount=G.rounding($tr.find('[name$="inSalesAmount"]').val());
            if($tr.find('[field="radio-inSalesAmount-amount"]').attr("checked")&&inSalesAmount<=0){
                errorItem+=" 上架量;";
            }
            var inSalesPrice=G.rounding($tr.find('[name$="inSalesPrice"]').val());
            if(inSalesPrice<=0){
                errorItem+=" 销售价;";
            }
            var unit=$tr.find('[name$="unit"]').val();
            if(G.isEmpty(unit)){
                errorItem+=" 单位;";
            }
            var productCategoryName=$tr.find('[name$="productCategoryName"]').val();
            if(G.isEmpty(productCategoryName)){
                errorItem+=" 上架分类;";
            }
            if(!G.isEmpty(errorItem)){
                errorMsg+= "第"+(index+1)+"行，请填写"+ errorItem+"<br/>";
            }
        });
        if(!G.isEmpty(errorMsg)){
            nsDialog.jAlert(errorMsg);
            return;
        }
        var data={};
        $(".itemChk").each(function(index){
            var $tr=$(this).closest("tr");
            var productId=$tr.find('[name$="productId"]').val();
            var productCategoryId=$tr.find('[name$="productCategoryId"]').val();
            var productCategoryName=$tr.find('[name$="productCategoryName"]').val();
            var inSalesAmount=$tr.find('[name$="inSalesAmount"]').val();
            if($('[field="radio-inSalesAmount-exist"]').attr("checked")){
                inSalesAmount=-1;
            }
            var inSalesPrice=$tr.find('[name$="inSalesPrice"]').val();
            var promotionsId=$tr.find('.promotionSelector').val();
            var discountAmount=G.rounding($(this).attr("discountAmount"));
            var promotionsType=G.normalize($(this).attr("promotionsType"));
            var bargainType=G.normalize($(this).attr("bargainType"));
            var unit=G.normalize($tr.find('[name$="unit"]').val());
            var guaranteePeriod = G.normalize($tr.find('[name$="guaranteePeriod"]').val());
            var storageUnit=G.normalize($tr.find('[name$="storageUnit"]').val());
            var sellUnit=G.normalize($tr.find('[name$="sellUnit"]').val());
            if(!G.isEmpty(unit)&&G.isEmpty(storageUnit)&&G.isEmpty(sellUnit)){ //新增单位
                sellUnit=unit;
                storageUnit=unit;
            }
            var limitFlag=$(this).attr("limitFlag");
            var limitAmount=G.rounding($(this).attr("limitAmount"));
            data['productDTOs['+index+'].productLocalInfoId']=productId;
            data['productDTOs['+index+'].productCategoryId']=productCategoryId;
            data['productDTOs['+index+'].productCategoryName']=productCategoryName;
            data['productDTOs['+index+'].inSalesAmount']=inSalesAmount;
            data['productDTOs['+index+'].inSalesPrice']=inSalesPrice;
            data['productDTOs['+index+'].unit']=unit;
            data['productDTOs['+index+'].guaranteePeriod']=guaranteePeriod;
            data['productDTOs['+index+'].storageUnit']=storageUnit;
            data['productDTOs['+index+'].sellUnit']=sellUnit;
            data['productDTOs['+index+'].promotionsDTO']={};
            data['productDTOs['+index+'].promotionsDTO.id']=promotionsId;
            data['productDTOs['+index+'].promotionsDTO.promotionsProductDTO']={};
            data['productDTOs['+index+'].promotionsDTO.promotionsProductDTO.productLocalInfoId']=productId;
            data['productDTOs['+index+'].promotionsDTO.promotionsProductDTO.promotionsId']=promotionsId;
            data['productDTOs['+index+'].promotionsDTO.promotionsProductDTO.discountAmount']=discountAmount;
            data['productDTOs['+index+'].promotionsDTO.promotionsProductDTO.promotionsType']=promotionsType;
            data['productDTOs['+index+'].promotionsDTO.promotionsProductDTO.bargainType']=bargainType;
            data['productDTOs['+index+'].promotionsDTO.promotionsProductDTO.limitFlag']=limitFlag;
            data['productDTOs['+index+'].promotionsDTO.promotionsProductDTO.limitAmount']=limitAmount;
        });

        APP_BCGOGO.Net.syncPost({
            url: "goodsInOffSales.do?method=batchSaveGoodsInSales",
            data: data,
            dataType: "json",
            success: function (json) {
                if (!json.success) {
                    nsDialog.jAlert(json.msg);
                }else{
                    var productIdArr=new Array();
                    $(".itemChk").each(function(){
                        productIdArr.push($(this).val());
                    });
                    toGoodsInSalesFinish(productIdArr.toString(),"batchGoodsInSalesEditor");
                }
            },
            error: function () {
                nsDialog.jAlert("网络异常!");
            }
        });
    });


    $(":radio[name='setInSalesAmount']").bind("change",function() {
        if($(":radio[name='setInSalesAmount']:checked").val()=="inputInSalesAmount"){
            $("#input_inSalesAmount").removeAttr("disabled");
            $("#input_inSalesAmount").focus();
        }else{
            $("#input_inSalesAmount").attr("disabled",true);
            $("#input_inSalesAmount").val("");
        }
    });

    $("#radio_percent").bind("click",function() {
        $("#input_percent").removeAttr("disabled");
        $("#input_percent").focus();
        $("#input_value").val("");
        $("#input_value").attr("disabled",true);
    });

    $("#radio_value").bind("click",function() {
        $("#input_value").removeAttr("disabled");
        $("#input_value").focus();
        $("#input_percent").val("");
        $("#input_percent").attr("disabled",true);
    });

    $(".goodsOffSalesBtn").live("click",function(){
        var $me=$(this);
        nsDialog.jConfirm("是否确认要下架该商品？",null,function(flag){
            if(flag){
                goodsInOffSales($me.attr("productId"),"off",refreshPage)
            }
        });
    });




    $(".bar-item").bind("mouseover",function () {
        $(this).find(".alert_develop").show();
    }) .bind("mouseout",function () {
            $(this).find(".alert_develop").hide();
        }).bind("click", function () {
            $(".bar-item").each(function(){
                $(this).removeClass("hover");
            });
            $(this).addClass("hover");
            var currentSortStatus = $(this).attr("currentSortStatus");
            $(this).find(".J-sort-span").removeClass("arrowDown").removeClass("arrowUp");
            if(currentSortStatus == "Desc"){
                $(this).find(".J-sort-span").addClass("arrowUp");
                $(this).attr("currentSortStatus","Asc");
                $(this).find(".alertBody").html($(this).attr("descContact"));
            } else {
                $(this).find(".J-sort-span").addClass("arrowDown");
                $(this).attr("currentSortStatus", "Desc");
                $(this).find(".alertBody").html($(this).attr("ascContact"));
            }
            $("#sortStatus").val($(this).attr("sortFiled") +  $(this).attr("currentSortStatus"));
            $("#searchPromotionsProductBtn").click();

        });

    $(document).click(function(event){
        var $target=$(event.target);
        var selectorArray = [
            '[filed^="bar-priceInput"]',
            ".dashBoard"
        ];
        if($target.closest(selectorArray).length==0) {
            $(".dashBoard").hide();
        }
    });

    $('[filed^="bar-priceInput"]').bind("focus",function(){
        $(".dashBoard").hide();
        $(this).closest(".dashBoardHolder").find(".dashBoard").show();

    })

    $(".dashBoardHolder .clean").click(function(){
        $(this).closest(".dashBoardHolder").find('[filed^="bar-priceInput"]').val("");
    });
    $(".dashBoardHolder .btnSure").click(function(){
        $(this).closest(".dashBoardHolder").find(".dashBoard").hide();
        $("#searchProductBtn").click();
    });

    $(".dashBoardHolder .pItem").click(function(){
        var $holder=$(this).closest(".dashBoardHolder");
        if(!G.isEmpty($(this).attr("start"))){
            $holder.find('[filed="bar-priceInput-start"]').val(G.rounding($(this).attr("start")));
        }else{
            $holder.find('[filed="bar-priceInput-start"]').val("");
        }
        if(!G.isEmpty($(this).attr("end"))){
            $holder.find('[filed="bar-priceInput-end"]').val(G.rounding($(this).attr("end")));
        }else{
            $holder.find('[filed="bar-priceInput-end"]').val("");
        }
        $holder.find(".dashBoard").hide();
        $("#searchProductBtn").click();
    });
    //批量设置上架量
    $(".batchSetInSalesAmount").bind("click",function(){
        if($(".itemChk:checked").length<=0){
            nsDialog.jAlert("请选择要设置上架量的商品!");
            return false;
        }
        $("#setInSalesAmountDialog").dialog({
            width: 300,
            height:200,
            modal: true,
            resizable: false,
            title: "批量设置本页上架量",
            beforeclose: function (event, ui) {
                $("#sameInventoryRadio").click().change();
                return true;
            },
            buttons:{
                "确定":function(){
                    if ($(":radio[name='setInSalesAmount']:checked").val()=="inputInSalesAmount"
                        && (!G.Lang.isNumber($("#input_inSalesAmount").val()) || $("#input_inSalesAmount").val()*1<0 )) {
                        $("#input_inSalesAmount").focus().select();
                        nsDialog.jAlert("请输入正确的数字!");
                        return false;
                    }
                    var data = {};
                    //库存   自定义  有货 无货
                    var setInSalesAmount=$(":radio[name='setInSalesAmount']:checked").val();
                    if(setInSalesAmount=="sameInventory"){
                        $(".itemChk:checked").each(function(i) {
                            var inSalesAmount=G.rounding($(this).closest("tr").find(".j_inventoryNum").val());
                            data["productDTOs["+i+"].productLocalInfoId"]=$(this).attr("productId");
                            data["productDTOs["+i+"].inSalesAmount"]=inSalesAmount;
                            $(this).closest("tr").find('[field="inSalesAmount"]').text(inSalesAmount).val(inSalesAmount);
                        });
                    }else if(setInSalesAmount=="inputInSalesAmount"){
                        var inSalesAmount = G.rounding($("#input_inSalesAmount").val(), 1);
                        $(".itemChk:checked").each(function(i) {
                            data["productDTOs["+i+"].productLocalInfoId"]=$(this).attr("productId");
                            data["productDTOs["+i+"].inSalesAmount"]=inSalesAmount;
                            $(this).closest("tr").find('[field="inSalesAmount"]').text(inSalesAmount).val(inSalesAmount);
                        });
                    }else if(setInSalesAmount=="haveGoods"){
                        $(".itemChk:checked").each(function(i) {
                            var $tr=$(this).closest("tr");
                            data["productDTOs["+i+"].productLocalInfoId"]=$(this).attr("productId");
                            data["productDTOs["+i+"].inSalesAmount"]=-1;
                            $tr.find('[field="inSalesAmount"]').text("有货");
                            $tr.find('[field="radio-inSalesAmount-exist"]').attr("checked",true);
                        });
                    }

                    APP_BCGOGO.Net.asyncAjax({
                        url: "product.do?method=updateMultipleInSalesAmount",
                        type: "POST",
                        cache: false,
                        data:data,
                        dataType: "json",
                        success: function (json) {
                            ;
                        },
                        error:function(){
                            nsDialog.jAlert("网络异常！");
                        }
                    });
                    $(this).dialog("close");
                },
                "取消":function(){
                    $(this).dialog("close");
                }
            }
        });
    });
    //批量设置质保时间
    $(".batchSetGuaranteePeriod").bind("click",function(){
        if($(".itemChk:checked").length<=0){
            nsDialog.jAlert("请选择要设置质保时间的商品!");
            return false;
        }
        $("#setGuaranteePeriodDialog").dialog({
            width: 200,
            height:150,
            modal: true,
            resizable: false,
            title: "批量设置本页质保时间",
            beforeclose: function (event, ui) {
                $("#sameInventoryRadio").click().change();
                return true;
            },
            buttons:{
                "确定":function(){
                    var guaranteePeriod=$('[name="guaranteePeriod"]').val();
                    if(guaranteePeriod.length>10){
                        nsDialog.jAlert("质保时间过长，请重新输入。");
                        return;
                    }
                    var data = {};
                    $(".itemChk:checked").each(function(i) {
                        data["productDTOs["+i+"].productLocalInfoId"]=$(this).attr("productId");
                        data["productDTOs["+i+"].guaranteePeriod"]=guaranteePeriod;
                    });

                    APP_BCGOGO.Net.asyncAjax({
                        url: "product.do?method=updateMultipleGuaranteePeriod",
                        type: "POST",
                        cache: false,
                        data:data,
                        dataType: "json",
                        success: function (json) {
                            if (!json.success) {
                                nsDialog.jAlert(json.msg);
                            }else{
                                $(".itemChk:checked").each(function(i) {
                                    $(this).closest("tr").find('[field="guaranteePeriod"]').text(guaranteePeriod+'个月');
                                });
                            }
                        },
                        error:function(){
                            nsDialog.jAlert("网络异常！");
                        }
                    });
                    $(this).dialog("close");
                },
                "取消":function(){
                    $(this).dialog("close");
                }
            }
        });
    });

    //批量设置上架售价
    $(".batchSetInSalesPrice").bind("click",function(){
        if($(".itemChk:checked").length<=0){
            nsDialog.jAlert("请选择要设置销售价的商品!");
            return false;
        }
        $("#setInSalesPriceDialog").dialog({
            width: 230,
            modal: true,
            resizable: false,
            title: "批量设置上架售价",
            beforeclose: function (event, ui){
                $("#input_percent").val("");
                $("#input_value").val("");
                return true;
            },
            open: function () {
                $("#radio_percent").click();
                return true;
            },
            buttons:{
                "确定":function(){
                    if($(".itemChk:checked").length<=0){
                        nsDialog.jAlert("请选择要设置上架量的商品!");
                        return false;
                    }
                    var data = {};
                    $(".itemChk:checked").each(function(i) {
                        var inventoryAveragePrice =  G.rounding($(this).closest("tr").find(".j_inventoryAveragePrice").val());
                        var  newInSalesPrice = G.rounding(inventoryAveragePrice * (1 + $("#input_percent").val() / 100) + $("#input_value").val() * 1);
                        $(this).closest("tr").find('[name$="inSalesPrice"]').text(newInSalesPrice).val(newInSalesPrice);
                        data["productDTOs["+i+"].productLocalInfoId"]=$(this).attr("productId");
                        data["productDTOs["+i+"].inSalesPrice"]=newInSalesPrice;
                    });

                    APP_BCGOGO.Net.asyncAjax({
                        url: "product.do?method=updateMultipleInSalesPrice",
                        type: "POST",
                        cache: false,
                        data:data,
                        dataType: "json",
                        success: function (json) {
                            if(json.success){
//                                $(".itemChk:checked").each(function(i) {
//                                    $(this).closest("tr").find('[name="inSalesPrice"]').text(newInSalesPrice);
//                                });
                            }else{
                                nsDialog.jAlert("批量设置本页上架售价失败!");
                            }
                        },
                        error:function(){
                            nsDialog.jAlert("网络异常！");
                        }
                    });
                    $(this).dialog("close");
                },
                "取消":function(){
                    $(this).dialog("close");
                }
            }
        });
    });

    $(".batch-operate").click(function(){
        $(".batch-operate").removeClass("up-batch-selected");
        $(this).addClass("up-batch-selected");
    });

    $("#saveGoodsInSalesBtn").bind("click",function(){
        if(!validateSaveGoodsInSales()){
            return;
        }
        var $pOpt=$(".promotionSelector option:checked");
        var promotionsId=$pOpt.val();
        if(G.isEmpty(promotionsId)){
            _doSaveGoodsInSales();
            return;
        }
        var data={};
        data['id']=$pOpt.val();
        data['startTimeStr']=$pOpt.attr("startTimeStr");
        data['endTimeStr']=$pOpt.attr("endTimeStr");
        data['type']=$pOpt.attr("type");
        data['promotionsProductDTOList']={};
        data['promotionsProductDTOList[0].productLocalInfoId']=$("#productId").val();
        var flag=true;
        APP_BCGOGO.Net.syncGet({
            "url": "promotions.do?method=validateSavePromotionsForInSales",
            data:data,
            type: "POST",
            cache: false,
            "dataType": "json",
            success: function (result) {
                if(!G.isEmpty(result)&&!result.success){
                    nsDialog.jAlert(result.msg);
                    flag=false;
                }
                if(G.isEmpty(result.data)){
                     _doSaveGoodsInSales();
                    return;
                }
                var lappingMap=result.data;
                var promotion="";
                var errorMsg='<div>对不起，';
                for(var key in lappingMap){
                    errorMsg+='<br/>';
                    promotion=lappingMap[key];
                    errorMsg+='商品'+ $('[name="name"]').val()+'在同时段已参与促销'+ G.normalize(promotion.name)+'，是否覆盖之前的促销？';
                }
                errorMsg+='</div>';
                $(errorMsg).dialog({
                    width: 370,
                    height:180,
                    modal: true,
                    resizable: true,
                    title: "友情提示",
                    buttons:{
                        "确定":function(){
                            $(this).dialog("close");
                            var promotionsId= G.isEmpty(promotion)?"":promotion.idStr;
                                    _doSaveGoodsInSales(promotionsId);
                        },
                        "取消":function(){
                            $(this).dialog("close");
                        }
                    }
                });
            },
            error:function(){
                nsDialog.jAlert("网络异常！");
            }
        });


        function _doSaveGoodsInSales(promotionsId){
            var data={
                lappingPromotionsId:promotionsId
            }
            var unit=$('[name="unit"]').val();
            var sellUnit=$('[name="sellUnit"]').val();
            var storageUnit=$('[name="storageUnit"]').val();
            var rate=$('[name="rate"]').val();
            if(!G.isEmpty(unit)&&G.isEmpty(storageUnit)&&G.isEmpty(sellUnit)){ //新增单位
                data['sellUnit']=unit;
                data['storageUnit']=unit;
                data['inSalesUnit']=unit;
                data['rate']=1;
            }
            $("#goodsInSalesForm").ajaxSubmit({
                url:"goodsInOffSales.do?method=saveGoodsInSales",
                data:data,
                dataType: "json",
                type: "POST",
                success: function(result){
                    if(G.isEmpty(result)){
                        nsDialog.jAlert("保存异常。");
                    }else{
                        if(result.success){
                            toGoodsInSalesFinish(result.dataStr,"goodsInSalesEditor");
                        }else{
                            nsDialog.jAlert(result.msg);
                        }
                    }
                },
                error:function(){
                    nsDialog.jAlert("网络异常！");
                }
            });
        }



    });
//增加单位
    $("#addUnit").live("click",function (){
        $("#addUnit").hide();
        $("#secondUnitContainer").show();
    });

    $(".itemUnitSpan").live("click",function(){
        var $unitContainer=$(this).closest(".unit-Container");
        var itemUnitSpan=$unitContainer.find('.itemUnitSpan').text();
        var sellUnit=$unitContainer.find('[name="sellUnit"]').val();
        var storageUnit=$unitContainer.find('[name="storageUnit"]').val();
        var rate=G.rounding($unitContainer.find('[name="rate"]').val());
        if(G.isEmpty(sellUnit)||G.isEmpty(storageUnit)||sellUnit==storageUnit||rate==0){
            return;
        }
        if(itemUnitSpan==sellUnit){   //转换成大单位
            $unitContainer.find('.itemUnitSpan').text(storageUnit);
            $unitContainer.find('[name="unit"]').val(storageUnit);

            $('[name="inSalesPrice"]').val(G.rounding($('[name="inSalesPrice"]').val()*rate));
            $('span[name="recommendedPrice"]').text(G.rounding($('span[name="recommendedPrice"]').text()*rate));
            $('span[name="tradePrice"]').text(G.rounding($('span[name="tradePrice"]').text()*rate));
            var inventoryNum=G.rounding($('span[name="inventoryNum"]').val()/rate);
            $('span[name="inventoryNum"]').text(inventoryNum).val(inventoryNum);
            $('[field="inSalesAmount"]').val(G.rounding($('[field="inSalesAmount"]').val()*1/rate));
            if($('[field="radio-inSalesAmount-amount"]').attr("checked")){
                $('[name="inSalesAmount"]').val(G.rounding($('[name="inSalesAmount"]').val()*1/rate));
            }
        }else if(itemUnitSpan==storageUnit){  //转换成小单位
            $unitContainer.find('.itemUnitSpan').text(sellUnit);
            $unitContainer.find('[name="unit"]').val(sellUnit);

            $('[name="inSalesPrice"]').val(G.rounding($('[name="inSalesPrice"]').val()/rate));
            $('span[name="recommendedPrice"]').text(G.rounding($('span[name="recommendedPrice"]').text()/rate));
            $('span[name="tradePrice"]').text(G.rounding($('span[name="tradePrice"]').text()/rate));
            var inventoryNum=G.rounding($('span[name="inventoryNum"]').val()*rate);
            $('span[name="inventoryNum"]').text(inventoryNum).val(inventoryNum);
            $('[field="inSalesAmount"]').val(G.rounding($('[field="inSalesAmount"]').val()*1*rate));
            if($('[field="radio-inSalesAmount-amount"]').attr("checked")){
                $('[name="inSalesAmount"]').val(G.rounding($('[name="inSalesAmount"]').val()*1*rate));
            }
        }

    });

    $("#cancelAddUnit").live("click",function(){
        $("#addUnit").show();
        $("#secondUnitContainer,#rateContainer").hide();
        $("#secondUnit,#secondRate,#firstRate").val("");

    });
    $("#secondUnit").live("blur",function () {
        var secondUnit = $.trim($("#secondUnit").val());
        $("#secondUnit").val(secondUnit);
        $("#secondUnitSpan").text(secondUnit);
        $("#secondUnitSpan").attr("title",secondUnit);
        if(secondUnit){
            $("#rateContainer").show();
        }
    }).live("keyup", function () {
            var secondUnit = $.trim($("#secondUnit").val());
            $("#secondUnitSpan").text(secondUnit);
            $("#secondUnitSpan").attr("title",secondUnit);
        });

    $("#sureAddUnit").live("click",function(){
        if($("#sureAddUnit").attr("clickFlag")){
            return;
        }
        $("#sureAddUnit").attr("clickFlag",true);
        var sellUnit = '',
            storageUnit = '',
            rate = '',
            firstRate=1,
            secondRate=1;
        if (validateAddSecondUnit()) {
            var firstUnit = $("#firstUnit_Hidden").val();
            var secondUnit = $.trim($("#secondUnit").val());
            secondRate = $.trim($("#secondRate").val()) * 1;
            firstRate = $.trim($("#firstRate").val()) * 1;
            if (secondRate > firstRate) {
                sellUnit = secondUnit;
                storageUnit = firstUnit;
                rate = secondRate;
            } else {
                sellUnit = firstUnit;
                storageUnit = secondUnit;
                rate = firstRate;
            }
        } else {
            $("#sureAddUnit").removeAttr("clickFlag");
            return;
        }
        var productId = $("#productId").val();
        if (G.isEmpty(productId)) {
            $("#sureAddUnit").removeAttr("clickFlag");
            return;
        }
        APP_BCGOGO.Net.syncAjax({
            url: "txn.do?method=setSellUnitAndRate",
            dataType: "json",
            data: {
                productId:productId ,
                storageUnit: storageUnit,
                sellUnit: sellUnit,
                rate: rate
            },
            success: function (jsonStr) {
                if(jsonStr[jsonStr.length-1].result == "success"){
                    nsDialog.jAlert("您已成功添加一个新单位！");
                    var product=jsonStr[0];
                    var sellUnit=product.sellUnit;
                    var storageUnit=product.storageUnit;
                    var rate=product.rate;

                    var unitHtml = '<span class="itemUnitSpan">'+sellUnit+'</span>';
                    unitHtml += '<input type="hidden"  name="unit" class="txt" value="'+sellUnit+'">';
                    unitHtml +='<input type="hidden" name="sellUnit" value="'+sellUnit+'"/>';
                    unitHtml +='<input type="hidden" name="storageUnit" value="'+storageUnit+'"/>';
                    unitHtml +='<input type="hidden" name="rate" value="'+rate+'"/>';


//                    var unitHtml='<span>'+sellUnit+'</span>';
//                    unitHtml += '<span class="sureUnit" style="margin-right: 10px;">';
//                    unitHtml += '&nbsp;包装单位:'+storageUnit;
//                    unitHtml += '<span style="color:#787878;">（1'+storageUnit+'&nbsp;='+rate+sellUnit+'）</span>';
//                    unitHtml += '</span>';
                    $("#_unitContainer").html(unitHtml);
                    if (secondRate > firstRate) {
                        var inSalesAmount=G.rounding($('[field="inSalesAmount"]').val());
                        var inSalesPrice=G.rounding($('[name="inSalesPrice"]').val());
                        var inventoryNum=G.rounding($(".add-product [name='inventoryNum']").val());
                        $('[field="inSalesAmount"]').val(inSalesAmount*secondRate);
                        $('[name="inSalesPrice"]').val(inSalesPrice/secondRate);
                        inventoryNum=inventoryNum*secondRate;
                        $(".add-product [name='inventoryNum']").val(inventoryNum).text(inventoryNum+sellUnit);
                    }

                }
            },
            error: function () {
                nsDialog.jAlert("网络异常，请联系客服！")
            }
        });
    });

    $("#productInSalesNum").click(function(){
        var pageType=$(this).attr("pageType");
        if(pageType=="_unInSalingGoodsList"){
            window.open("goodsInOffSales.do?method=toInSalingGoodsList","_self");
        } else if(pageType=="_inSalingGoodsList"){
            clearSearchCondition();
            searchProductSubmit(pageType,'','');
        }

    });

    $("#promotionsProductNum").click(function(){
        var pageType=$(this).attr("pageType");
        if(pageType=="_unInSalingGoodsList"){
            window.open("goodsInOffSales.do?method=toInSalingGoodsList&promotionsTypeList=MLJ,MJS,BARGAIN,FREE_SHIPPING","_self");
        } else if(pageType=="_inSalingGoodsList"){
            clearSearchCondition();
            $("#promotionsType").val('MLJ,MJS,BARGAIN,FREE_SHIPPING');
            searchProductSubmit(pageType,'','');
        }

    });

    $("#productUnInSaleNum").click(function(){
        window.open("goodsInOffSales.do?method=toUnInSalingGoodsList","_self");
    });
});

function validateAddSecondUnit() {
    var firstUnit = $("#firstUnit_Hidden").val();
    var secondUnit = $.trim($("#secondUnit").val());
    var secondRate = $.trim($("#secondRate").val());
    var firstRate = $.trim($("#firstRate").val());

//    if (G.isEmpty(secondUnit) && G.isEmpty(secondRate) && G.isEmpty(firstRate)) {
//        return true;
//    }
    if (G.isEmpty(secondUnit)) {
        nsDialog.jAlert("增加单位不能为空！请重新填写");
        return false;
    }else if(secondUnit == firstUnit){
        nsDialog.jAlert("增加单位与原单位不能相同！请重新填写");
        return false;
    }
    if (isNaN(firstRate) || isNaN(secondRate) || G.isEmpty(secondRate) || G.isEmpty(firstRate)
        || !(secondRate * 1 == 1 || firstRate * 1 == 1) || firstRate * 1<1 || secondRate * 1<1 ) {
        nsDialog.jAlert("增加单位换算比例格式不正确！请重新填写<br><br><span>例如：1箱=10个 或者 10个=1箱<span>");
        return false;
    }
    return true;
}


//商品下拉建议
$().ready(function(){

    $("#searchProductBtn").click(function(){
        var pageType=$(this).attr("pageType");
        var startTimeStr=$('[name="startTimeStr"]').val();
        var endTimeStr=$('[name="endTimeStr"]').val();
        searchProductSubmit(pageType,startTimeStr,endTimeStr);
    });


    $(".J-productSuggestion").bind('click', function () {
        _productSuggestion($(this));
    }).bind('keyup', function (event) {
            var eventKeyCode = event.which || event.keyCode;
            if (GLOBAL.Interactive.keyNameFromKeyCode(eventKeyCode).search(/left|up|right|down/g) == -1) {
                _productSuggestion($(this));
            }
        })
        .bind("change",function(){
            $("#productId").val("");
        });

    var _productSuggestion=function productSuggestion($domObject) {
        var searchWord = $domObject.val().replace(/[\ |\\]/g, "");
        var dropList = APP_BCGOGO.Module.droplist;
        dropList.setUUID(GLOBAL.Util.generateUUID());
        var currentSearchField =  $domObject.attr("searchField");
        var ajaxData = {
            searchWord: searchWord,
            searchField: currentSearchField,
            salesStatus: $("#salesStatus").val(),
//            promotionsId:$('#promotionsId').val(),
//            promotionsFilter:$('#promotionsFilter').val(),
            mustQuerySolr: "true",
            uuid: dropList.getUUID()
        };
        $domObject.parent().prevAll().each(function(){
            var $productSearchInput = $(this).find(".J-productSuggestion");
            if($productSearchInput && $productSearchInput.length>0){
                var val = $productSearchInput.val().replace(/[\ |\\]/g, "");
                if($productSearchInput.attr("name")!="searchWord"){
                    ajaxData[$productSearchInput.attr("name")] = val == $productSearchInput.attr("initialValue") ? "" : val;
                }
            }
        });

        var ajaxUrl = "product.do?method=getProductSuggestion";
        if(G.isEmpty(ajaxData['searchWord'])){
            ajaxData['sort'] = 'last_in_sales_time desc';
        }
        APP_BCGOGO.wjl.LazySearcher.lazySearch(ajaxUrl, ajaxData, function (result) {
            if (currentSearchField == "product_info") {
                dropList.show({
                    "selector": $domObject,
                    "autoSet": false,
                    "data": result,
                    onGetInputtingData: function() {
                        var details = {};
                        $domObject.nextAll(".J-productSuggestion").each(function () {
                            var $productSearchInput = $(this);
                            if ($productSearchInput && $productSearchInput.length > 0) {
                                var val = $productSearchInput.val().replace(/[\ |\\]/g, "");
                                details[$productSearchInput.attr("searchField")] = val == $productSearchInput.attr("initialValue") ? "" : val;
                            }
                        });
                        return {
                            details:details
                        };
                    },
                    onSelect: function (event, index, data, hook) {
                        $domObject.nextAll(".J-productSuggestion").each(function () {
                            var $productSearchInput = $(this);
                            if ($productSearchInput && $productSearchInput.length > 0) {
                                var label = data.details[$productSearchInput.attr("searchField")];
                                if (G.Lang.isEmpty(label) && $productSearchInput.attr("initialValue")) {
                                    $productSearchInput.val($productSearchInput.attr("initialValue"));
                                    $productSearchInput.css({"color": "#ADADAD"});
                                } else {
                                    $productSearchInput.val(G.Lang.normalize(label));
                                    $productSearchInput.css({"color": "#000000"});
                                }
                            }
                        });
                        dropList.hide();
                    },
                    onKeyboardSelect: function (event, index, data, hook) {
                        $domObject.nextAll(".J-productSuggestion").each(function () {
                            var $productSearchInput = $(this);
                            if ($productSearchInput && $productSearchInput.length > 0) {
                                var label = data.details[$productSearchInput.attr("searchField")];
                                if (G.Lang.isEmpty(label) && $productSearchInput.attr("initialValue")) {
                                    $productSearchInput.val($productSearchInput.attr("initialValue"));
                                    $productSearchInput.css({"color": "#ADADAD"});
                                } else {
                                    $productSearchInput.val(G.Lang.normalize(label));
                                    $productSearchInput.css({"color": "#000000"});
                                }
                            }
                        });
                    }
                });
            }else{
                dropList.show({
                    "selector": $domObject,
                    "data": result,
                    "onSelect": function (event, index, data) {
                        $domObject.val(data.label);
                        $domObject.css({"color": "#000000"});
                        $domObject.nextAll(".J-productSuggestion").each(function () {
                            var $productSearchInput = $(this);
                            if ($productSearchInput) {
                                clearSearchInputValueAndChangeCss($productSearchInput[0]);
                            }
                        });
                        dropList.hide();
                    }
                });
            }
        });
    }
//    $(".J-productSuggestion").placeHolder();
    $(".s-product-inSales").bind("mouseover",function () {
        $(this).find(".alert_develop").show();
    }).bind("mouseout",function () {
            $(this).find(".alert_develop").hide();
        }).bind("click", function () {
            $(".s-product-inSales").removeClass("hover");
            $(this).addClass("hover");
            var currentSort = $(this).attr("currentSort");
            $(this).find(".J-sort-span").removeClass("arrowDown").removeClass("arrowUp");
            if(currentSort == "Desc"){
                $(this).find(".J-sort-span").addClass("arrowUp");
                $(this).attr("currentSort","Asc");
                $(this).find(".alertBody").html($(this).attr("descContact"));
            } else {
                $(this).find(".J-sort-span").addClass("arrowDown");
                $(this).attr("currentSort", "Desc");
                $(this).find(".alertBody").html($(this).attr("ascContact"));
            }
            $("#sortStatus").val($(this).attr("sortFiled")+$(this).attr("currentSort"));
            $("#searchProductBtn").click();
        });

//    $(".s-product-inSales").each(function() {
//        if ($(this).attr("sortFiled") == "startTime") {
//            $(this).addClass("hover");
//        }
//    });

});

function validateSaveGoodsInSales(){
    var productDescriptionEditor =UE.getEditor('productDescriptionEditor');
    if(G.isEmpty($(".add-product [name='name']").val())){
        nsDialog.jAlert("品名不应为空。");
        return false;
    }
    if(G.isEmpty($(".add-product [name='productCategoryName']").val())){
        nsDialog.jAlert("请选择商品分类。");
        return false;
    }
//    if(productDescriptionEditor.getContentLength(true)==0){
//        nsDialog.jAlert("商品描述不能为空！");
//        return false;
//    }
    if(G.rounding($(".add-product [name='inSalesPrice']").val())<=0){
        nsDialog.jAlert("上架价格应大于0。");
        return false;
    }
    if($('[field="radio-inSalesAmount-amount"]').attr("checked")&&G.rounding($(".add-product [name='inSalesAmount']").val())<=0){
        nsDialog.jAlert("上架量应大于0。");
        return false;
    }
    if(G.isEmpty($('[name="unit"]').val())){
        nsDialog.jAlert("请填写商品单位。");
        return false;
    }
//    var imageCount = 0;
//    $("input[id^='imageCenterDTO.productInfoImagePaths']").each(function(index){
//        if(!G.Lang.isEmpty($(this).val())){
//            imageCount++;
//        };
//    });
//    if(imageCount==0){
//        nsDialog.jAlert("请上传商品主图！");
//        return false;
//    }
    return true;
}


function initStockStatInfo(){
    APP_BCGOGO.Net.asyncAjax({
        url: "goodsInOffSales.do?method=getStockProductStat",
        type: "POST",
        cache: false,
        dataType: "json",
        success: function (sockStat) {
            if(!G.isEmpty(sockStat)){
                $("#allStockProductNum").text(G.rounding(sockStat.allStockProductNum));
                $("#productInSalesNum").text(G.rounding(sockStat.productInSalesNum));
                $("#promotionsProductNum").text(G.rounding(sockStat.promotionsProductNum));
                $("#productUnInSaleNum").text(G.rounding(sockStat.productUnInSaleNum));
            }
        },
        error:function(){
            G.error("goodsInOffSales.do?method=getStockProductStat error, from stockSearch.js");
        }
    });
}

function toBatchGoodsInSalesEditor(productIdList){
    if(G.isEmpty(productIdList)){
        return;
    }
    window.location.href="goodsInOffSales.do?method=toBatchGoodsInSalesEditor&productIdList="+productIdList;
}

//仓库中的商品
function toUnInSalingGoodsList(){
    window.location.href="goodsInOffSales.do?method=toUnInSalingGoodsList";
}

//单个商品上架
function toGoodsInSalesFinish(productIds,fromSource){
    window.location.href="goodsInOffSales.do?method=toGoodsInSalesFinish&productIds="+productIds+"&fromSource="+fromSource;
}

var promotionsPruductMap;
function initInSalingGoodsList(result){
    $("#inSalingGoodsTable tr:gt(1)").remove();
    $(".select_all").attr("checked",false);
    promotionsPruductMap={};
    if(G.isEmpty(result)){
        return;
    }
    var products=result.results;
    var tr="";
    if(G.isEmpty(products)){
        tr+="<tr><td colspan='9' style='text-align: center;'>没有上架的商品。</td></tr>";
        $("#inSalingGoodsTable").append(tr);
        return;
    }
    var paramShopId=$("#paramShopId").val();
    for(var i=0;i<products.length;i++){
        var product=products[i];
        var productInfo=generateProductInfo(product,false);
        var inventoryNum=G.rounding(product.inventoryNum)+G.normalize(product.sellUnit);
        var inventoryAveragePrice=G.rounding(product.inventoryAveragePrice);
        var inSalesPrice=G.rounding(product.inSalesPrice);
        var inSalesAmount=G.rounding(product.inSalesAmount);
        var productId=G.normalize(product.productLocalInfoIdStr);
        var lastInSalesTime=G.normalize(product.lastInSalesTimeStr);
        var name=G.normalize(product.name);
        var guaranteePeriod=G.normalize(product.guaranteePeriod);
        var promotionsList=product.promotionsDTOs;
        if(!G.isEmpty(promotionsList)){
            promotionsPruductMap[productId]=promotionsList;
        }
        var imageCenterDTO = product.imageCenterDTO;
        var imageSmallURL = imageCenterDTO.productListSmallImageDetailDTO.imageURL;
        tr+='<tr>';
        tr+='<td style="padding-left:10px;"><input class="itemChk" productId="'+productId+'" type="checkbox"/>';
        tr+='<input type="hidden" field="productId" value="'+productId+'">';
        tr+='<input type="hidden" field="name" value="'+name+'">';
        tr+='<input type="hidden" field="inSalesPrice" value="'+inSalesPrice+'">';
        tr+='<input type="hidden" class="j_inventoryAveragePrice" value="'+inventoryAveragePrice+'">';
        tr+='<input type="hidden"  class="j_inventoryNum" value="'+G.rounding(product.inventoryNum)+'">';
        tr+='</td>';
        tr+='<td field="productInfo" class="item-product-infomation">';
        tr+='   <div class="product-icon"><img style="width: 60px;height: 60px" src="'+imageSmallURL+'"/></div>';
        tr+='   <div class="product-info-details"><a class="blue_color" onclick="toShopProductDetail(\''+paramShopId+'\',\''+productId+'\')">'+productInfo+'</a></div>';
        tr+='   <div class="cl"></div>';
        tr+='</td>';
        tr+='<td field="inventoryNum">'+inventoryNum+'</td>';
        tr+='<td><div type="show">';
        if(inSalesAmount==-1){
            tr+='<span field="inSalesAmount">有货</span><span field="productUnit" style="display:none;">'+G.normalize(product.sellUnit) + '</span>'
        }else{
            tr+='<span field="inSalesAmount">'+G.rounding(product.inSalesAmount)+'</span><span field="productUnit">'+G.normalize(product.sellUnit) + '</span>';
        }
        tr+='<img field="editor-icon-inSalesAmount" src="../web/images/bi.png"></div>';
        tr+='<div type="edit" style="display: none">';
        tr+= '<div><input field="radio-inSalesAmount-amount" name="radio-inSalesAmount-'+i+'" type="radio"/><input field="inSalesAmount" class="w30 price-input" type="text" />';
        tr+='<div><input field="radio-inSalesAmount-exist" name="radio-inSalesAmount-'+i+'" type="radio"/>显示有货</div></div>';
        tr+='<div><input type="button" class="saveBtn-inSalesAmount in-sale-list-button" value="保存"/><input class="cancelBtn in-sale-list-button" type="button" value="取消"/></div>';
        tr+='</td>';
        tr+='<td><div type="show">'+'<span class="arialFont">&yen;</span><span>'+inSalesPrice+'</span><img field="editor-icon-inSalesPrice" src="../web/images/bi.png"></div>';
        tr+='<div type="edit" style="display: none"><div><span class="arialFont">&yen;</span><input field="inSalesPrice" type="text" class="price-input w30" value="' + inSalesPrice + '"/></div>';
        tr+='<div><input type="button" class="saveBtn-inSalesPrice in-sale-list-button" value="保存"/><input class="cancelBtn in-sale-list-button" type="button" value="取消"/></div></div>';
        tr+= '</td>';
        tr+='<td><div type="show">';
        if(G.isEmpty(guaranteePeriod)){
            tr+='<span field="guaranteePeriod">--</span><img field="editor-icon-guaranteePeriod" src="../web/images/bi.png"></div>';
//            tr+='<div type="edit" style="display: none"><div><input type="text" class="w30" value="'+guaranteePeriod+'" />个月</div><div><input type="button" class="saveBtn-guaranteePeriod" value="保存"/><input class="cancelBtn" type="button" value="取消"/></div></div>';
        }else{
            tr+='<span field="guaranteePeriod">'+guaranteePeriod+'个月</span><img field="editor-icon-guaranteePeriod" src="../web/images/bi.png"></div>';
        }
        tr+='<div type="edit" style="display: none"><div><input type="text" field="guaranteePeriod" class="w30" value="'+guaranteePeriod+'" />个月</div>';
        tr+= '<div><input type="button" class="saveBtn-guaranteePeriod in-sale-list-button" value="保存"/><input class="cancelBtn in-sale-list-button" type="button" value="取消"/></div></div>';
        tr+='</td>';
        tr+='<td>'+lastInSalesTime+'</td>';
        if(G.isEmpty(promotionsList)){
            tr+='<td class="promotion-info">'+'未参与'+'</td>';
        }else{
            tr+='<td class="promotion-info">'+'已参与<br/><a class="blue_color promotionsDetailBtn">查看促销</a>'+'</td>';
        }
        tr+='<td>'+'<a class="blue_color goodsOffSalesBtn" productId="'+productId+'">下架商品</a><br/><a onclick="toGoodsInSalesEditor(\''+productId+'\')" class="blue_color">编辑商品</a>'+'</td>';
        tr+='</tr>';
        tr+='';
    }
    $("#inSalingGoodsTable").append(tr);
}



function initUnInSalingGoodsList(result){
    $("#unInSalingGoodsTable tr:gt(1)").remove();
    $(".select_all").attr("checked",false);
    if(G.isEmpty(result)){
        return;
    }
    var products=result.results;
    var tr="";
    if(G.isEmpty(products)){
        tr+="<tr><td colspan='8' style='text-align: center;'>没有仓库中的商品。</td></tr>";
        $("#unInSalingGoodsTable").append(tr);
        return;
    }
    for(var i=0;i<products.length;i++){
        var product=products[i];
        var productInfo=generateProductInfo(product);
        var inventoryNum=G.rounding(product.inventoryNum)+G.normalize(product.sellUnit);
        var inventoryAveragePrice=G.rounding(product.inventoryAveragePrice);
        var tradePrice=G.rounding(product.tradePrice);
        var inSalesPrice=G.rounding(product.inSalesPrice);
        var recommendedPrice=G.rounding(product.recommendedPrice);
        var productId=G.normalize(product.productLocalInfoIdStr);
        var lastInSalesTimeStr=G.normalize(product.lastInSalesTimeStr);
        var imageCenterDTO = product.imageCenterDTO;
        var imageSmallURL = imageCenterDTO.productListSmallImageDetailDTO.imageURL;
        var productCategoryInfo=G.normalize(product.productCategoryInfo);
        var kindName=G.normalize(product.kindName);
        tr+='<tr>';
        tr+='<td class="item-checkbox"><input productId="'+productId+'" class="itemChk" type="checkbox"/>';
        tr+='<input class="lastInSalesTime" value="'+lastInSalesTimeStr+'" type="hidden"/>';
        tr+='<input class="productCategory" value="'+productCategoryInfo+'" type="hidden"/>';
        tr+='<input class="productId" value="'+productId+'" type="hidden"/>';
        tr+= '</td>';
        tr+='<td class="item-product-infomation">';
        tr+='<div class="product-icon"><img style="width: 60px;height: 60px" src="'+imageSmallURL+'"/></div>';
        tr+= '<div style="width: 200px" class="product-info-details">'+productInfo+'<br/><span style="color: #ADADAD;padding-top: 5px">零售价：<span class="arialFont">¥</span>'+recommendedPrice+'&nbsp;批发价：<span class="arialFont">¥</span>'+tradePrice+'</span></div><div class="cl"></div>';
        tr+='</td>';
        tr+='<td class="item-product-categories">'+kindName+'</td>';
        tr+='<td class="item-product-inventory">'+inventoryNum+'</td>';
        tr+='<td class="item-product-wholesale">'+'<span class="arialFont">¥</span>'+inSalesPrice+'</td>';
        tr+='<td class="item-product-operating">'+'<a class="blue_color" onclick="goodsInSalesForSingle(\''+productId+'\')">我要上架</a>'+'</td>';
        tr+='</tr>';
    }
    $("#unInSalingGoodsTable").append(tr);
}


function initShopPromotionInfo(promotionList){
    $(".promotionSelector").text("");
    if(G.isEmpty(promotionList)){
        $(".promotionSelector").append('<option value="">请选择促销</option>');
        return;
    }
    var promotionOptStr="";
    for(var i=0;i<promotionList.length;i++){
        var promotion=promotionList[i];
        var content=generatePromotionsContentByPromotions(promotion,"td");
        promotionOptStr+='<option value="'+promotion.idStr+'" type="'+promotion.type+'" startTimeStr="'+promotion.startTimeStr+'" endTimeStr="'+promotion.endTimeStr+'" pContent="'+generatePromotionsContentByPromotions(promotion,"td")+'">'+promotion.name+'</option>';
    }
    promotionOptStr="<option value=''>请选择促销</option>"+promotionOptStr;
    $(".promotionSelector").append(promotionOptStr);
}

/**
 * 商品上下架
 * @param productIdList
 * @param operate
 */
function goodsInOffSales(productIdList,operate,callBack){
    if(G.isEmpty(productIdList)||G.isEmpty(operate)){
        return;
    }
    APP_BCGOGO.Net.syncPost({
        url: "goodsInOffSales.do?method=goodsInOffSales",
        data: {productIdList: productIdList, operate: operate},
        dataType: "json",
        success: function (json) {
            if (!json.success) {
                nsDialog.jAlert(json.msg);
            }else{
                if($.isFunction(callBack))
                    callBack();
            }
        },
        error: function () {
            nsDialog.jAlert("网络异常!");
        }
    });
}

function goodsInSalesForSingle(productId){
    if(G.isEmpty(productId)){
        return;
    }
    var $tr=$('.itemChk:[productId="'+productId+'"]').closest("tr");
    var lastInSalesTime=$tr.find(".lastInSalesTime").val();
    var productId=$tr.find(".productId").val();
    var productCategory=$tr.find(".productCategory").val();
    if(!G.isEmpty(lastInSalesTime)&&!G.isEmpty(productCategory)){  //上过架，产品分类不为空
        $("<div>该商品您之前已经编辑上过架，您可以直接上架或者修改信息后再上架！</div>").dialog({
            width: 300,
            height:150,
            modal: true,
            resizable: false,
            title: "友情提示",
            buttons:{
                "直接上架":function(){
                    goodsInOffSales(productId,"in",refreshPage);
                    $(this).dialog("close");
                },
                "编辑商品":function(){
                    $(this).dialog("close");
                    toGoodsInSalesEditor(productId);
                }
            }
        });
        return;
    }
    toGoodsInSalesEditor(productId);
}

function previewShopProductDetail(){
    if(!validateSaveGoodsInSales()){
        return;
    }
    $("#goodsInSalesForm").submit();
}

function searchProductSubmit(pageType,startTimeStr,endTimeStr) {

    var url="product.do?method=getProducts";
    var data = {};
    $('[name="startLastInSalesTime"]').val(G.isEmpty(startTimeStr)?"": GLOBAL.Util.getExactDate(startTimeStr).getTime());
    $('[name="endLastInSalesTime"]').val(G.isEmpty(endTimeStr)?"": GLOBAL.Util.getExactDate(endTimeStr).getTime());
    var param = $("#searchProductForm").serializeArray();
    $.each(param, function (index, val) {
        data[val.name] = val.value;
    });
    $("#searchProductForm").ajaxSubmit({
        url:url,
        dataType: "json",
        type: "POST",
        success: function(result){
            if(G.isEmpty(result)){
                G.error("product.do?method=getProducts error, from goodsInOffSales.js");
            }else{
                if(!result.success){
                    nsDialog.jAlert(result.msg);
                    return;
                }else{
                    var postFn;
                    var dynamicalId;
                    if(pageType=="_unInSalingGoodsList"){
                        initUnInSalingGoodsList(result);
                        dynamicalId="_unInSalingGoodsList";
                        postFn="initUnInSalingGoodsList";
                    }else if(pageType=="_inSalingGoodsList"){
                        initInSalingGoodsList(result);
                        dynamicalId="_inSalingGoodsList";
                        postFn="initInSalingGoodsList";
                    }
                    initPage(result,dynamicalId,url, null, postFn, '', '',data,null);
                }
            }
        },
        error:function(){
            $(".J-initialCss").placeHolder("reset");
            G.error("product.do?method=getProducts error, from goodsInOffSales.js");
        }
    });
}

function clearSearchCondition() {
    $(".J-productSuggestion").val("").blur();
    $("#product_kind").val("").blur();
    $(".select-data-60").click(); //默认查询60天内
    $('[field="promotionsName"]').val("").blur();
    $(".promotionsTypes,#allChk").attr("checked",false);
    $("#promotionsType").val("");
//        $("#promotionsType").val("MLJ,MJS,BARGAIN,FREE_SHIPPING");
    $('[name^="inSalesPrice"]').val("");
}

function generatePromotionTypes() {
    var promotionsType=new Array();
    $(".promotionsTypes:checked").each(function(){
        promotionsType.push($(this).attr("promotionsTypes"));
    });
    return promotionsType.toString();   //全选不全选都要生成
}




