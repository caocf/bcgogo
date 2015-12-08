$().ready(function(){



    $("#addPromotionBtn").click(function(event){
        toPromotionsManager();
    });

    $(".createPromotionsBtn").click(function(){
        toPromotionsManager($(this).attr("promotions_type"),"");
    });

    $(".salePriceInput").live("focus",function(){
        $(".salePriceBoard,.priceBoard").hide();
        $(".salePriceBoard").show();
    });

    $(".priceInput").live("focus",function(){
        $(".salePriceBoard,.priceBoard").hide();
        $(".priceBoard").show();
    });

    $(".limitFlagChk").live("click",function(event){
        var $target=$(this);
        var _td=$target.closest("td");
        _td.find(".limit_amount").attr("disabled",!$target.attr("checked"));
    });

    $(".bargainTypeSelector").live("change",function(){
        var $bargainType=$(this).closest(".bargainTD").find(".bargainType");
        $bargainType.val($(this).val());
        if($bargainType.val()=="DISCOUNT"){
            var $dAmount=$bargainType.closest(".bargainTD").find(".discount_amount");
            var dVal=G.round($dAmount.val());
            if(dVal<1||dVal>10){
                $dAmount.val(0);
            }
        }
    });

    $(".select_all").click(function(){
        $(".itemChk").attr("checked",$(this).attr("checked"));
        $(".select_all").attr("checked",$(this).attr("checked"));
        $("#selectProductCount").text($(".itemChk:checked").length);
    });


    $(".table_mlj .icon_close").live("click",function(){
        var $target=$(this);
        var $table=$target.closest("table");
        $target.closest(".promotion_content").remove();
        if($table.find(".promotion_content").size()==0){
            $("#addMLJRuleLevel").click();
        }
        if($table.find(".promotion_content").size()<3){
            $("#addMLJRuleLevel").show();
        }
    });

    $(".table_mjs .icon_close").live("click",function(){
        var $target=$(this);
        var $table=$target.closest("table");
        if($target.hasClass("first_level")){
            var $first= $($table.find(".promotion_content").get(0));
            $first.find("[name$='minAmount']").val("");
            $first.find("[name$='discountAmount']").val("");
            $first.find("[name$='giftName']").val("");
            $first.find("[name$='amount']").val("");
//            $first.find(".giveGiftChk").attr("checked",false);
        }else{
            $target.closest(".promotion_content").remove();
            var pSize=$table.find(".promotion_content").size();
            if(pSize==0){
                $("#addMJSRuleLevel").click();
            }
            if($table.find(".promotion_content").size()<3){
                $("#addMJSRuleLevel").show();
            }
        }
    });

    $(document).bind("keyup",function(event){
        var $target=$(event.target);
        var selectorArray = [
            ".minAmount",
            ".discountAmount",
            ".salePriceInput",
            ".giftAmount",
            ".limit_amount"
        ];
        if($target.closest(selectorArray).length!=0) {
            $target.val(G.round($target.val()));
        }
    });

    $("#promotionsLimiterSelector").live("change",function(e){
        var unit = getOverUnit($(this));
        $("span.j_over_unit").text(unit);
//        var $target=$(this);
//        $("#promotionsLimiter").val($target.val());
//        if($target.val() == 'OVER_MONEY'){
//            $("#promotionUnit").text("元");
//        }else if($target.val() == 'OVER_AMOUNT'){
//            $("#promotionUnit").text("件");
//        }
    });

    //送货上门
    $(".postSelector").live("change",function(e){
        var $target=$(this);
        $("#postType").val($target.val());
        $("#postTitle").text($target.find("option:selected").text()+"：");
    });



    $("#promotionsRangesTD input[name='promotions_ranges']").click(function(){
        $("#promotionsRanges").val($(this).val());
        if($(this).val() == 'ALL') {
            $("#nextToAddPromotionsProduct").hide();
        } else {
            $("#nextToAddPromotionsProduct").show();
        }
    });



    $("#searchPromotionsBtn").click(function(){
        var url="promotions.do?method=getPromotionsDTO";
        var data = {
            status: $("#promotionStatus").val(),
            type:$("#promotionsType").val(),
            name:$('[field="promotionsName"]').val(),
            startPageNo:1,
            currentSort:$("#sortCondition").attr("currentSort"),
            sortFiled:$("#sortCondition").attr("sortFiled"),
            now: new Date()
        };
        APP_BCGOGO.Net.asyncAjax({
            url: url,
            type: "POST",
            cache: false,
            data: data,
            dataType: "json",
            success: function (json) {
                initPromotionList(json);
                initPage(json, "_promotionList",url, null, "initPromotionList", '', '',data,null);
            },
            error:function(){
                nsDialog.jAlert("网络异常！");
            }
        });

    });

    $("#searchPromotionsProductBtn").click(function(){
        var pageType=$(this).attr("pageType");
        $(".J-initialCss").placeHolder("clear");
        var url="promotions.do?method=getPromotionsProduct";
        clearSort();
        var param = $("#searchPromotionsProductForm").serializeArray();
        var data = {};
        $.each(param, function (index, val) {
            data[val.name] = val.value;
        });
        $("#searchPromotionsProductForm").ajaxSubmit({
            url:url,
            dataType: "json",
            type: "POST",
            success: function(json){
                var postFn;
                if(pageType=="promotionsProductList"){
                    postFn="initPromotionsProductTable";
                    initPromotionsProductTable(json);
                }else if(pageType=="addPromotionsProduct"){
                    postFn="initPromotionsProductTable";
                    initAddPromotionsProductTable(json);
                }else if(pageType=="addBargainProduct"){
                    postFn="initAddBargainProductTable";
                    initAddBargainProductTable(json);
                }else if(pageType=="productInPromotion"){
                    postFn="initProductInPromotionTable";
                    initProductInPromotionTable(json);
                }
                initPage(json,"_promotionsProduct",url, null, postFn, '', '',data,null);
                $(".J-initialCss").placeHolder("reset");
            },
            error:function(){
                nsDialog.jAlert("网络异常！");
                $(".J-initialCss").placeHolder("reset");
            }
        });
    });

    $("#addMLJRuleLevel").click(function(){
        var $promotionsRuleTypeRadio;
        $(".table_mlj input[name='promotions_rule']").each(function(){
            if($(this).attr("checked")){
                $promotionsRuleTypeRadio=$(this);
            }
        });
        if(G.isEmpty($promotionsRuleTypeRadio)){
            nsDialog.jAlert("请选择一种促销内容！");
            return;
        }

        var promotionsRule=$promotionsRuleTypeRadio.val();
        if(!validateMLJPromotionsRule()){
            return;
        }

        var $discountAmounts=$(".table_mlj .beforeEdit .discountAmount");
        if($discountAmounts.size()==3){
            nsDialog.jAlert("最多可添加三档促销！");
            return;
        }

        var $lastBeforeEdit= $(".table_mlj .promotion_content .beforeEdit:last");
        var minAmount=G.rounding($lastBeforeEdit.find(".minAmount").val());
        var discountAmount=G.rounding($lastBeforeEdit.find(".discountAmount").val());

        var count=$(".table_mlj .promotion_content:last").find("[name$='level']").val();
        if(G.isEmpty(count)){
            count=0;
        }
        count=Number(count);
        var idPrefix="promotionsRuleDTOList["+(count+1)+"]";
        var promotionsRuleType=$promotionsRuleTypeRadio.val();
        if(promotionsRuleType=="DISCOUNT_FOR_OVER_MONEY"){

            var addPromotionsRuleDiv='<div class="promotion_content"><input value="DISCOUNT_FOR_OVER_MONEY" class="promotionsRuleType" type="hidden" name="'+idPrefix+'.promotionsRuleType"/><div class="line beforeEdit"><a class="icon_close"></a>满&nbsp;' +
                '<input name="'+idPrefix+'.minAmount"  type="text" class="txt txt_color minAmount" style="width:50px;" />&nbsp;<span class="t2">元</span>，&nbsp;<span class="t3">打</span>&nbsp;' +
                '<input type="hidden" class="ruleLevel" name="'+idPrefix+'.level" value="'+(count+1)+'"/>'+
                '<input name="'+idPrefix+'.discountAmount" type="text" class="txt txt_color discountAmount" style="width:50px;" />&nbsp;<span class="t4">折</span></div>';
        }else if(promotionsRuleType=="DISCOUNT_FOR_OVER_AMOUNT"){
            var addPromotionsRuleDiv= '<div class="promotion_content"><input class="promotionsRuleType" value="DISCOUNT_FOR_OVER_AMOUNT" type="hidden" name="'+idPrefix+'.promotionsRuleType"/><div class="line beforeEdit"><a class="icon_close"></a>满&nbsp;' +
                '<input name="'+idPrefix+'.minAmount"  type="text" class="minAmount txt txt_color" style="width:50px;" />&nbsp;<span class="t2">件</span>，&nbsp;<span class="t3">打</span>&nbsp;' +
                '<input type="hidden" class="ruleLevel" name="'+idPrefix+'.level" value="'+(count+1)+'"/>'+
                '<input name="'+idPrefix+'.discountAmount" type="text" class="discountAmount txt txt_color" style="width:50px;" />&nbsp;<span class="t4">折</span></div>';
        }else if(promotionsRuleType=="REDUCE_FOR_OVER_MONEY"){
            var addPromotionsRuleDiv= '<div class="promotion_content"><input value="REDUCE_FOR_OVER_MONEY" class="promotionsRuleType" type="hidden" name="'+idPrefix+'.promotionsRuleType"/><div class="line beforeEdit"><a class="icon_close"></a>满&nbsp;' +
                '<input name="'+idPrefix+'.minAmount" type="text" class="minAmount txt txt_color" style="width:50px;" />&nbsp;<span class="t2">元</span>，&nbsp;<span class="t3">减</span>&nbsp;' +
                '<input type="hidden" class="ruleLevel" name="'+idPrefix+'.level" value="'+(count+1)+'"/>'+
                '<input name="'+idPrefix+'.discountAmount" type="text" class="discountAmount txt txt_color" style="width:50px;" />&nbsp;<span class="t4">元</span></div>';
        }else if(promotionsRuleType=="REDUCE_FOR_OVER_AMOUNT"){
            var addPromotionsRuleDiv='<div class="promotion_content"><input value="REDUCE_FOR_OVER_AMOUNT" class="promotionsRuleType" type="hidden" name="'+idPrefix+'.promotionsRuleType"/><div class="line beforeEdit"><a class="icon_close"></a>满&nbsp;' +
                '<input name="'+idPrefix+'.minAmount" type="text" class="minAmount txt txt_color" style="width:50px;" />&nbsp;<span class="t2">件</span>，&nbsp;<span class="t3">减</span>&nbsp;' +
                '<input type="hidden" class="ruleLevel" name="'+idPrefix+'.level" value="'+(count+1)+'"/>'+
                '<input name="'+idPrefix+'.discountAmount" type="text" class="discountAmount txt txt_color" style="width:50px;" />&nbsp;<span class="t4">元</span></div>';
        }

        $(".table_mlj #promotionsRuleDiv").append(addPromotionsRuleDiv);
        if($(".table_mlj .promotion_content").size()==3){
            $("#addMLJRuleLevel").hide();
        }
    });



    $("#promotionsProductFilter").click(function(){
        if($(this).attr("checked")){
            $("#promotionsFilter").val("add_promotions_product");
        }else{
            $("#promotionsFilter").val("add_promotions_product_current");
        }
        $("#searchPromotionsProductBtn").click();
    });

    $("#promotionStatusSelect").change(function(){
        $("#promotionStatus").val($(this).val());
    });

    $("#resetConditionBtn").click(function(){
        $(".salePriceInput,.priceInput,#promotionsType").val("");
        $("#promotionsProductFilter").attr("checked",false);
        $("#promotionsFilter").val("add_promotions_product_current");

        $(".J-initialCss").each(function(){
            $(this).val($(this).attr("initialValue"));
        });
        $(".J-initialCss").placeHolder("reset");
        $(".promotionsTypes,#allChk").attr("checked",false);
        $("#searchPromotionsProductBtn").click();
    });

    $(document).bind("click", function(event) {
        var slideArray=[
            ".priceInput",
            ".salePriceInput",
            ".priceBoard"
        ];
        if($(event.target).closest(slideArray).length == 0){
            $(".salePriceBoard,.priceBoard").hide();
        }
    });


    //从添加商品链接到管理
    $("#preToManagePromotionsBtn").click(function(){
        var promotionsId=$("#promotionsId").val();
        var status=$("#promotionStatus").val();
        if(status!="UN_USED" && status!="UN_STARTED"){
            nsDialog.jAlert("只有未使用和未开始的促销可以编辑！");
            return;
        }
        var type=$("#promotionsType").val();
        toPromotionsManager(type,promotionsId);
    });

    //客户优惠
    $("#promotionsRuleTypeSelect").change(function(){
        var ruleType=$(this).val();
        $(".promotionsRuleType").val(ruleType);
        if(ruleType=="DISCOUNT_FOR_OVER_MONEY"){
            $(".rText").text("折扣");
        }else if(ruleType=="REDUCE_FOR_OVER_MONEY"){
            $(".rText").text("金额");
        }
    });


    $(".time_input").bind("click", function(){
        $(this).blur();
    }).datetimepicker({
            "numberOfMonths": 1,
            "showButtonPanel": true,
            "changeYear": true,
            "changeMonth": true,
            "yearSuffix": "",
            "yearRange":"c-100:c+100",
            "onClose": function(dateText, inst) {
                var $table= $(this).closest("table");
                var startTimeStr=$table.find("[name='startTimeStr']").val();
                var endTimeStr=$table.find("[name='endTimeStr']").val();
//                if($(this).hasClass("startTimeStr")){
                if(G.isEmpty(startTimeStr)){
                    nsDialog.jAlert("促销开始时间不能为空。");
                    $(this).val(inst.lastVal);
                    return;
                }
                var oneHour=60*60*1000;
                if(GLOBAL.Util.getExactDate(dateUtil.getCurrentTime()).getTime()-GLOBAL.Util.getExactDate(startTimeStr).getTime()>oneHour) {
                    nsDialog.jAlert("促销开始时间不能小于当前时间。");
                    $(this).val(inst.lastVal);
                    return;
                }

                if(!$table.find(".date_select:checked").hasClass("date_select_define")){
                    $table.find(".date_select:checked").click();
                }

                if(!G.isEmpty(startTimeStr)&&!G.isEmpty(endTimeStr)&& GLOBAL.Util.getExactDate(startTimeStr) >= GLOBAL.Util.getExactDate(endTimeStr)) {
                    nsDialog.jAlert("开始时间应小于结束时间，请重新选择！");
                    $(this).val(inst.lastVal);
                }else{
                    generateTimeStat($(this).closest("table"));
                }

            },
            "onSelect": function(dateText, inst) {
                if(inst.lastVal == dateText) {
                    return;
                }
                $(this).val(dateText);
            }
        });


    $(".date_select").live("click",function(){
        var $target=$(this);
        var $table=$target.closest("table");
        $('[name="endTimeStr"]').hide();
        $(".un_limited").hide();
        var endTimeStr;
        var hourStr=" 23:59";
        var startTime=$table.find("[name='startTimeStr']").val();
        if($target.hasClass("date_select_week")){
            var n=7
            endTimeStr=dateUtil.getAfterDays(n,startTime);
            endTimeStr+=hourStr;
            $table.find('[name^="endTimeStr"]').val(endTimeStr);
            $table.find(".limited_Date").show();
            $table.find("#cDay").text(n);
            $table.find("#cHour").text(0);
        }else if($target.hasClass("date_select_month")){
            var n=30;
            endTimeStr=dateUtil.getAfterDays(n,startTime);
            endTimeStr+=hourStr;
            $table.find('[name^="endTimeStr"]').val(endTimeStr);
            $table.find(".limited_Date").show();
            $table.find("#cDay").text(n);
            $table.find("#cHour").text(0);
        }else if($target.hasClass("date_select_three_month")){
//            $("[name='startTimeStr']").val($("#serviceStartTime").val());
            var n=90;
            endTimeStr=dateUtil.getAfterDays(n,startTime);
            endTimeStr+=hourStr;
            $table.find('[name^="endTimeStr"]').val(endTimeStr);
            $table.find(".limited_Date").show();
            $table.find("#cDay").text(n);
            $table.find("#cHour").text(0);
        }else if($target.hasClass("date_select_unlimited")){
//            $("[name='startTimeStr']").val($("#serviceStartTime").val());
            $table.find('[name^="endTimeStr"]').val("");
            $table.find(".limited_Date").hide();
            $table.find(".un_limited").show();

        }else if($target.hasClass("date_select_define")){
            $table.find('[name="endTimeStr"]').show();
            $table.find('[name^="endTimeStr"]').val("");
        }
        generateTimeStat($table);
    });

    $("#addMJSRuleLevel").click(function(){
        if(!validateMJSPromotionsRule()){
            return;
        }
        var idPrefix=$(".table_mjs .promotion_content:last").attr("idPrefix");
        idPrefix = isNaN(idPrefix)?0:(Number(idPrefix)+1);
        var unit = getOverUnit($(this));
        var content='<div class="promotion_content" idPrefix="'+idPrefix+'"><a class="icon_close" style="margin: 0px"></a><div class="line">满&nbsp;';
        content+='<input type="text" name="promotionsRuleDTOList['+idPrefix+'].minAmount" class="txt" style="width:100px;" />&nbsp;<span class="j_over_unit">' + unit + '</span></div><div class="line"><label class="rad">';
        content+= '<input type="hidden" name="promotionsRuleDTOList['+idPrefix+'].promotionsRuleMJSDTOs[0].giftType" value="GIFT">';
        content+='送 <input  name="promotionsRuleDTOList['+idPrefix+'].promotionsRuleMJSDTOs[0].giftName" type="text" class="J-initialCss giftNameChk txt" initialValue="礼品名称" style="width:100px;" />&nbsp';
        content+='<input name="promotionsRuleDTOList['+idPrefix+'].promotionsRuleMJSDTOs[0].amount" type="text" class="J-initialCss giftAmount txt" initialValue="数量" style="width:60px;" />&nbsp件</div>';
        content+='</div>';
        $(".table_mjs #promotionsRuleDiv").append(content);
        if($(".table_mjs .promotion_content").size()==3){
            $("#addMJSRuleLevel").hide();
        }
    });

    //送货上门 地区面板
    APP_BCGOGO.Net.asyncAjax({
        url: "shop.do?method=getAllAreaToCity",
        type: "POST",
        cache: false,
        dataType: "json",
        success: function (provinceList) {
            initAreaBoard(provinceList);
            if(!G.isEmpty($("#promotionsId").val())){
                APP_BCGOGO.Net.asyncAjax({
                    url: "promotions.do?method=getFreeShippingDetail",
                    data:{
                        promotionsId:$("#promotionsId").val()
                    },
                    type: "POST",
                    cache: false,
                    dataType: "json",
                    success: function (promotions) {
                        initPromotionsArea(promotions);
                        if(!G.isEmpty(promotions.promotionsRuleDTOList) && promotions.promotionsRuleDTOList[0].minAmount>0){
                            $("input[name='promotionsRuleRio'][value='YES']").attr("checked", true);
                            addPromotionsCondition($("input[name='promotionsRuleRio'][value='YES']").attr("checked", true)[0]);
                        } else {
                            $("input[name='promotionsRuleRio'][value='NO']").attr("checked", true);
                            addPromotionsCondition($("input[name='promotionsRuleRio'][value='NO']")[0]);

                        }
                    },
                    error:function(){
                        G.error("promotions.do?method=getFreeShippingDetail error, from promotions.js");
                    }
                });
            }else{
                $(".j_owned_province").attr("checked", true);   //默认本省选中
                promotionsAreaClick($(".j_owned_province")[0]);
            }
        },
        error:function(){
            G.error("shop.do?method=getAllAreaToCity error, from promotions.js");
        }
    });


    $("#savePromotionsBtn,#nextToAddPromotionsProduct").click(function(){
        var _me=this;
        if($(_me).attr("disabled")){
            return;
        }
        if($(_me).attr("id") == 'savePromotionsBtn'){
            $(_me).attr("disabled", true);
        }
        var range=$(this).closest("#promotionsForm").find("#promotionsRanges").val()
        savePromotions(_me,function(result,flag){
            if(!flag){
                return;
            }
            if($(_me).attr("id")=="nextToAddPromotionsProduct"){
                if(range=="ALL"){
                    nsDialog.jConfirm("促销已创建成功！是否发送促销消息？",null,function(flag){
                        if(flag){
                            toSendPromotionMsg();
                        }else{
                            toPromotionsList()
                        }
                    });
                }else{
                    toAddPromotionsProduct(result.data);
                }
            }else if($(_me).attr("id")=="savePromotionsBtn"){
                toPromotionsList();
            }
        });
    })

    $("#promotionsName").bind("blur",function(){
        if(G.Lang.isEmpty($(this).val())){
            $(this).parent("td").find(".J_promotionsName_tip").removeClass("right").addClass("wrong").show();
        }else if($(this).val().length>20){
            $(this).parent("td").find(".J_promotionsName_tip").removeClass("right").addClass("wrong").show();
        }else{
            $(this).parent("td").find(".J_promotionsName_tip").removeClass("wrong").addClass("right").show();
        }
    });

    if(!G.isEmpty($("#promotionsId").val())){
        if($("#timeFlag").val()=="true"){
            $(".date_select_define").attr("checked",true);
            $(".time_input").attr("disabled",false);
            $('[name="endTimeStr"]').show();
        }else{
            var startTime =  dateUtil.convertDateStrToDate($("[name='startTimeStr']").val(),dateUtil.dateStringFormatDayHourMin);
            var endTime =  dateUtil.convertDateStrToDate($("[name='endTimeStr']").val(),dateUtil.dateStringFormatDayHourMin);
            if(G.isEmpty($("[name='endTimeStr']").val())){
                $(".date_select_unlimited").attr("checked",true);
            }else if(!G.isEmpty(startTime)&&!G.isEmpty(endTime)){
                var days = parseInt(dateUtil.getDayBetweenTwoDate(startTime,endTime));
                if(days<=7){
                    $(".date_select_week").attr("checked",true);
                }else if(days<=30&&days>7){
                    $(".date_select_month").attr("checked",true);
                }else if(days<=90 && days>30){
                    $(".date_select_three_month").attr("checked",true);
                }
            }
//            $(".time_input").attr("disabled",true);
            $(".time_input").each(function(){
                generateTimeStat($(this).closest("table"));
            });
        }
    }else{
        $("[name='startTimeStr']").val($("#serviceStartTime").val());
        $(".td_time .date_select_month").click();
    }
    $("#last_date").change();

    //init
    $("#promotionsRuleTypeSelect").val($("#promotionsRuleTypeHidden").val());
    $("#promotionsRuleTypeSelect").change();


});

function generateTimeStat($table){
    var startTimeStr=$table.find("[name='startTimeStr']").val();
    var endTimeStr=$table.find("[name='endTimeStr']").val();
    if(G.isEmpty(endTimeStr)){
        $table.find(".limited_Date").hide();
        $table.find(".un_limited").show();
//          if($table.find(".date_select_unlimited").attr("checked")){
//            $table.find(".limited_Date").hide();
//            $table.find(".un_limited").show();
//        }else{
//            $table.find(".limited_Date").show();
//            $table.find(".un_limited").hide();
//            $table.find("#cDay").text(0);
//            $table.find("#cHour").text(0);
//        }
    } else if(!G.isEmpty(startTimeStr) && !G.isEmpty(endTimeStr)){
        $table.find(".limited_Date").show();
        $table.find(".un_limited").hide();
        var durationTime = GLOBAL.Util.getExactDate(endTimeStr).getTime() - GLOBAL.Util.getExactDate(startTimeStr).getTime();
        var cDay = parseInt(durationTime / (24 * 60 * 60 * 1000));
        var cHours = parseInt((durationTime - cDay * (24 * 60 * 60 * 1000)) / (60 * 60 * 1000));
        $table.find("#cDay").text(cDay);
        $table.find("#cHour").text(cHours);
    }
}

//商品信息
$().ready(function(){

    $(".J_product_sort").bind("mouseover",function () {
        $(this).find(".alert_develop").show();
    }) .bind("mouseout",function () {
            $(this).find(".alert_develop").hide();
        }).bind("click", function () {
            $(".J_product_sort").each(function(){
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

    $(".J_promotions_sort")
        .bind("mouseover",function () {
            $(this).find(".alert_develop").show();
        })
        .bind("mouseout",function () {
            $(this).find(".alert_develop").hide();
        })
        .bind("click", function () {
            $(".J_promotions_sort").each(function(){
                $(this).removeClass("hover");
            });
            $(this).addClass("hover");
            var currentSort = $(this).attr("currentSort");
            $(this).find(".J-sort-span").removeClass("arrowDown").removeClass("arrowUp");
            if(currentSort == "DESC"){
                $(this).find(".J-sort-span").addClass("arrowUp");
                $(this).attr("currentSort","ASC");
                $(this).find(".alertBody").html($(this).attr("descContact"));
            } else {
                $(this).find(".J-sort-span").addClass("arrowDown");
                $(this).attr("currentSort", "DESC");
                $(this).find(".alertBody").html($(this).attr("ascContact"));
            }
            $("#sortCondition").attr("currentSort",$(this).attr("currentSort"));
            $("#sortCondition").attr("sortFiled",$(this).attr("sortFiled"));
            $("#searchPromotionsBtn").click();

        });

    $(".J_promotions_sort").each(function() {
        if ($(this).attr("sortFiled") == "startTime") {
            $(this).addClass("hover");
        }
    });

    $("#product_kind").bind("click focus", function(event) {
        var $domObject = $(this);
        var dropList = APP_BCGOGO.Module.droplist;
        dropList.setUUID(GLOBAL.Util.generateUUID());
        APP_BCGOGO.Net.syncPost({
            url: "stockSearch.do?method=getProductKindsRecentlyUsed",
            dataType: "json",
            data: {"uuid": dropList.getUUID()},
            success: function (result) {
                dropList.show({
                    "selector":$domObject,
                    "data":result,
                    "onSelect":function (event, index, data) {
                        $domObject.val(data.label);
                        $domObject.css({"color":"#000000"});
                        dropList.hide();
                    }
                });
            }
        });
    }).bind("keyup", function(event) {
            var $domObject = $(this);
            var dropList = APP_BCGOGO.Module.droplist;
            dropList.setUUID(GLOBAL.Util.generateUUID());
            APP_BCGOGO.Net.syncPost({
                url: "stockSearch.do?method=getProductKindsWithFuzzyQuery",
                dataType: "json",
                data: {"uuid": dropList.getUUID(),keyword: $.trim($domObject.val())},
                success: function (result) {
                    dropList.show({
                        "selector":$domObject,
                        "data":result,
                        "onSelect":function (event, index, data) {
                            $domObject.val(data.label);
                            $domObject.css({"color":"#000000"});
                            dropList.hide();
                        }
                    });
                }
            });
        });

    $('[field="promotionsName"]').bind("click focus", function(event) {
        var $domObject = $(this);
        var dropList = APP_BCGOGO.Module.droplist;
        dropList.setUUID(GLOBAL.Util.generateUUID());
        APP_BCGOGO.Net.syncPost({
            url: "promotions.do?method=getPromotionsSuggestion",
            dataType: "json",
            data: {"uuid": dropList.getUUID()},
            success: function (result) {
                dropList.show({
                    "selector":$domObject,
                    "data":result,
                    "onSelect":function (event, index, data) {
                        $domObject.val(data.label);
                        $domObject.css({"color":"#000000"});
                        dropList.hide();
                    }
                });
            }
        });
    }).bind("keyup", function(event) {
            var $domObject = $(this);
            var dropList = APP_BCGOGO.Module.droplist;
            dropList.setUUID(GLOBAL.Util.generateUUID());
            APP_BCGOGO.Net.syncPost({
                url: "promotions.do?method=getPromotionsSuggestion",
                dataType: "json",
                data: {"uuid": dropList.getUUID(),keyword: $.trim($domObject.val())},
                success: function (result) {
                    dropList.show({
                        "selector":$domObject,
                        "data":result,
                        "onSelect":function (event, index, data) {
                            $domObject.val(data.label);
                            $domObject.css({"color":"#000000"});
                            dropList.hide();
                        }
                    });
                }
            });
        });

    $(".J-productSuggestion").bind('click', function () {
        _productSuggestion($(this));
    })
        .bind('keyup', function (event) {
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
            salesStatus: "InSales",
            promotionsId:$('#promotionsId').val(),
            promotionsFilter:$('#promotionsFilter').val(),
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
    $(".J-initialCss").placeHolder();
});





function toPromotionsManager(pmnType,promotionsId){
    if(pmnType=="MLJ"){
        toManageMLJ(promotionsId);
    }else if(pmnType=="MJS"){
        toManageMJS(promotionsId);
    }else if(pmnType=="BARGAIN"){
        toManageBargain(promotionsId);
    }else if(pmnType=="FREE_SHIPPING"){
        toManageFreeShipping(promotionsId);
    }else if(pmnType=="SPECIAL_CUSTOMER"){
        toManageSpecialCustomer(promotionsId);
    }else{
        window.location.href="promotions.do?method=toPromotionsManager";
    }
}

function toPromotionsList(){
    window.location.href="promotions.do?method=toPromotionsList";
}

function toManageMLJ(promotionsId){
    window.location.href="promotions.do?method=toManageMLJ&promotionsId="+promotionsId;
}


function toManageMJS(promotionsId){
    window.location.href="promotions.do?method=toManageMJS&promotionsId="+promotionsId;
}


function toManageBargain(promotionsId){
    window.location.href="promotions.do?method=toManageBargain&promotionsId="+promotionsId;
}


function toManageFreeShipping(promotionsId){
    window.location.href="promotions.do?method=toManageFreeShipping&promotionsId="+promotionsId;
}

function toSpecialCustomerList(){
    window.location.href="promotions.do?method=toSpecialCustomerList";
}

function toAddPromotionsProduct(promotionsId){
    if(G.isEmpty(promotionsId)){
        promotionsId=$("#promotionsId").val();
        if(G.isEmpty(promotionsId)){
            GLOBAL.error("promotionsId is null!");
            return;
        }
    }
    window.location.href="promotions.do?method=toAddPromotionsProduct&promotionsId="+promotionsId;
}

function toProductInPromotion(promotionsId){
    if(G.isEmpty(promotionsId)){
        return;
    }
    window.location.href="promotions.do?method=toProductInPromotion&promotionsId="+promotionsId;
}

function toPromotionsProductList(){
    window.location.href="promotions.do?method=toPromotionsProductList";
}

function toSendPromotionMsg(){
    var promotionsId=$("#promotionsId").val();
    var pContent=$("#pContent").val();
    window.location.href="promotions.do?method=toSendPromotionMsg&promotionsIdStr="+promotionsId+"&pContent="+pContent;
}

function toManageSpecialCustomer(){
    window.location.href="promotions.do?method=toManageSpecialCustomer";
}

function validateMJSPromotionsRule(){
    var flag=true;
    var pSize=$(".table_mjs .promotion_content").size();
    //去掉空行
    $(".table_mjs .promotion_content").each(function(){
        var minAmount=G.rounding($(this).find("[name$='minAmount']").val());
        var giftNameChk=G.normalize($(this).find(".giftNameChk").val());
        var giftNameInitialvalue = G.normalize($(this).find(".giftNameChk").attr("initialvalue"));
        var giftAmount=G.rounding($(this).find(".giftAmount").val());
        if(minAmount==0&&giftAmount==0&&giftNameChk == giftNameInitialvalue){
            if(pSize==1){
                nsDialog.jAlert("请填写促销规则！");
                flag=false;
                return;
            }else{
                $(this).remove();
            }
        }
    });
    if(!flag){
        return false;
    }
    var limiter= $(".table_mjs .promotionsLimiterSelector").val();
    $(".table_mjs .promotion_content").each(function(i){
//        if($(this).find(".giveGiftChk").attr("checked")){
        var minAmount=G.rounding($(this).find("[name$='minAmount']").val());
        if(minAmount<=0){
            if("OVER_AMOUNT"==limiter){
                nsDialog.jAlert("请在第"+(i+1)+"级促销数量应大于0。");
            }else{
                nsDialog.jAlert("请在第"+(i+1)+"级促销金额数应大于0。");
            }
            flag=false;
            return false;
        }
        var giftName=G.normalize($(this).find("[name$='giftName']").val());
        if(G.isEmpty(giftName) || giftName == '礼品名称'){
            nsDialog.jAlert("请在第"+(i+1)+"级输入礼品名称。");
            flag=false;
            return false;
        }
//        var giftAmount=G.rounding($(this).find("[name$='amount']").val());
//        if(giftAmount==0){
//            nsDialog.jAlert("请在第"+(i+1)+"级输入输入礼品数量。");
//            flag=false;
//            return false;
//        }
    });
    if(!flag){
        return false;
    }
    var $minAmounts=$(".table_mjs .promotion_content").find("[name$='minAmount']");
    var lastMinAmount=-1;
    for(var i=0;i<$minAmounts.length;i++,lastMinAmount=minAmount){
        var minAmount=$minAmounts[i];
        minAmount=G.rounding($(minAmount).val());
        if(minAmount<=lastMinAmount){
            if("OVER_AMOUNT"==limiter){
                nsDialog.jAlert("第"+(i+1)+"级金额应大于"+i+"级的满足条件的数量！");
            }else{
                nsDialog.jAlert("第"+(i+1)+"级数量应大于"+i+"级的满足条件的金额！");
            }
            return false;
        }
    }

    if(!flag){
        return false;
    }
    return true;
}

function validateFreeShipping(){
    var flag=true;
    $(".table_free_shipping [name='promotionsRuleRio']").each(function(){
        if($(this).attr("checked")){
            if($(this).val()=="YES"){
                if(G.rounding($(".table_free_shipping .minAmount").val())==0){
                    flag=false;
                    return;
                }
            }
        }
    });
    if(!flag){
        nsDialog.jAlert("促销条件不应为空！");
        return false;
    }

    return true;
}

function validateMLJPromotionsRule(){
    var $promotionsRuleTypeRadio;
    $(".table_mlj input[name='promotions_rule']").each(function(){
        if($(this).attr("checked")){
            $promotionsRuleTypeRadio=$(this);
        }
    });
    if(G.isEmpty($promotionsRuleTypeRadio)){
        nsDialog.jAlert("请选择一种促销内容！");
        return false;
    }
    var flag=true;
    $(".table_mlj .promotion_content").each(function(i){
        var discountAmount=G.rounding($(this).find(".discountAmount").val(),1);
        var minAmount=G.rounding($(this).find(".minAmount").val());
        if((minAmount!=0&&discountAmount==0)||(minAmount==0&&discountAmount!=0)){
            nsDialog.jAlert("第"+(i+1)+"级促销规则信息不完整！");
            flag=false;
            return;
        }
    });
    if(!flag){
        return;
    }
    var pSize=$(".table_mlj .promotion_content").size();
    //去掉空行
    $(".table_mlj .promotion_content").each(function(){
        var discountAmount=G.rounding($(this).find(".discountAmount").val());
        var minAmount=G.rounding($(this).find(".minAmount").val());
        if(minAmount==0&&discountAmount==0){
            if(pSize==1){
                nsDialog.jAlert("请填写促销规则！");
                flag=false;
                return;
            }else{
                $(this).remove();
            }
        }
    });
    if(!flag){
        return;
    }

    var promotionsRule=$promotionsRuleTypeRadio.val();
    var $discountAmounts=$(".table_mlj .promotion_content .discountAmount");
    var $minAmounts=$(".table_mlj .promotion_content .minAmount");

    for(var i=0;i<$discountAmounts.length;i++){
        var discountAmount=$discountAmounts[i];
        discountAmount=G.rounding($(discountAmount).val(),1);
        if(promotionsRule=="DISCOUNT_FOR_OVER_MONEY"||promotionsRule=="DISCOUNT_FOR_OVER_AMOUNT"){
            if(discountAmount<=0||discountAmount>=10){
                nsDialog.jAlert("请在折扣输入框,输入0到10之间的整数或小数！");
                return false;
            }
        }
    }

    for(var i=0;i<$minAmounts.length;i++){
        var minAmount=$minAmounts[i];
        minAmount=G.rounding($(minAmount).val());
        if(promotionsRule=="DISCOUNT_FOR_OVER_MONEY"||promotionsRule=="REDUCE_FOR_OVER_MONEY"){
            if(minAmount<=0) {
                nsDialog.jAlert("第"+(i+1)+"级促销金额应大于0！");
                return false;
            }
        }else  if(promotionsRule=="DISCOUNT_FOR_OVER_AMOUNT"||promotionsRule=="REDUCE_FOR_OVER_AMOUNT"){
            if(minAmount<=0){
                nsDialog.jAlert("第"+(i+1)+"级促销数量应大于0！");
                return false;
            }
        }
    }
    var lastMinAmount=-1;
    for(var i=0;i<$minAmounts.length;i++,lastMinAmount=minAmount){
        var minAmount=$minAmounts[i];
        minAmount=G.rounding($(minAmount).val());
        if(minAmount<=lastMinAmount){
            if(promotionsRule=="DISCOUNT_FOR_OVER_MONEY"||promotionsRule=="REDUCE_FOR_OVER_MONEY"){
                nsDialog.jAlert("第"+(i+1)+"级促销金额应大于第"+i+"级促销金额！");
                return false;
            }else  if(promotionsRule=="DISCOUNT_FOR_OVER_AMOUNT"||promotionsRule=="REDUCE_FOR_OVER_AMOUNT"){
                nsDialog.jAlert("第"+(i+1)+"级促销数量应大于第"+i+"级促销数量！");
                return false;
            }

        }
    }
    var lastDiscountAmount=-1;
    for(var i=0;i<$discountAmounts.length;i++,lastDiscountAmount=discountAmount){
        var discountAmount=$discountAmounts[i];
        discountAmount=G.rounding($(discountAmount).val(),1);
        if(promotionsRule=="DISCOUNT_FOR_OVER_MONEY"||promotionsRule=="DISCOUNT_FOR_OVER_AMOUNT"){

            if(i!=0&&discountAmount>=lastDiscountAmount){
                nsDialog.jAlert("第"+(i+1)+"级折扣应小于"+i+"级的折扣！！");
                return false;
            }
        }
        if(promotionsRule=="REDUCE_FOR_OVER_MONEY"||promotionsRule=="REDUCE_FOR_OVER_AMOUNT"){
            if(i!=0&&discountAmount<=lastDiscountAmount){
                nsDialog.jAlert("第"+(i+1)+"级促销金额应大于"+i+"级的促销金额！");
                return false;
            }
        }
    }
    return true;
}

function setMJSRuleSequence(){
    if( $(".table_mjs .promotion_content").size()<2){
        return;
    }
    var $promotionsRuleTypeRadio=$(".table_mjs input[name='promotions_rule']:checked");
    var promotionsRule=$promotionsRuleTypeRadio.val();
    var $discountAmounts=$(".table_mjs .promotion_content .discountAmount");
    var $minAmounts=$(".table_mjs .promotion_content .minAmount");
    //1.假设倒序
    var minAmountSeq="reverse";
    var lastMinAmount=-1;
    for(var i=0;i<$minAmounts.length;i++,lastMinAmount=minAmount){
        var minAmount=$minAmounts[i];
        minAmount=G.rounding($(minAmount).val());
        if(minAmount>=lastMinAmount){
            minAmountSeq="none";
        }
    }

    if(minAmountSeq=="none"){   //假设正序
        var minAmountSeq="seq";
//        for(var i=0;i<$minAmounts.length;i++,lastMinAmount=minAmount){
//            var minAmount=$minAmounts[i];
//            minAmount=G.rounding($(minAmount).val());
//            if(minAmount<=lastMinAmount){
//                if(promotionsRule=="DISCOUNT_FOR_OVER_MONEY"||promotionsRule=="REDUCE_FOR_OVER_MONEY"){
//                    nsDialog.jAlert("第"+(i+1)+"级促销金额应大于第"+i+"级促销金额！");
//                    return false;
//                }else  if(promotionsRule=="DISCOUNT_FOR_OVER_AMOUNT"||promotionsRule=="REDUCE_FOR_OVER_AMOUNT"){
//                    nsDialog.jAlert("第"+(i+1)+"级促销数量应第"+i+"级促销数量大于0！");
//                    return false;
//                }
//
//            }
//        }
    }

    var lastDiscountAmount=-1;
    for(var i=0;i<$discountAmounts.length;i++,lastDiscountAmount=discountAmount){
        var discountAmount=$discountAmounts[i];
        discountAmount=G.rounding($(discountAmount).val(),1);
        if(promotionsRule=="DISCOUNT_FOR_OVER_MONEY"||promotionsRule=="DISCOUNT_FOR_OVER_AMOUNT"){

            if(i!=0&&discountAmount>=lastDiscountAmount){
                nsDialog.jAlert("第"+(i+1)+"级折扣应小于"+i+"级的折扣！！");
                return false;
            }
        }
        if(promotionsRule=="REDUCE_FOR_OVER_MONEY"||promotionsRule=="REDUCE_FOR_OVER_AMOUNT"){
            if(i!=0&&discountAmount<=lastDiscountAmount){
                nsDialog.jAlert("第"+(i+1)+"级促销金额应大于"+i+"级的促销金额！");
                return false;
            }
        }
    }

}

function validatePromotionsInfo($table){
    var promotionsName=$table.find("#promotionsName").val();
    if(G.isEmpty(promotionsName)){
        nsDialog.jAlert("请填写促销名称！");
        return false;
    }
    if(promotionsName.length>20){
        nsDialog.jAlert("促销名称仅限20个字以内！");
        return false;
    }
    var description=$table.find("#description").val();
    if(description.length>200){
        nsDialog.jAlert("促销描述仅限200个字以内！");
        return false;
    }
    if(G.isEmpty($table.find("[name=startTimeStr]").val())){
        nsDialog.jAlert("开始时间不能为空！");
        return;
    }
    var reduceForOverMoneyValidate = true;
    $table.find("#promotionsRuleDiv .promotion_content").each(function(){
        if($(this).find("input[name$='promotionsRuleType']").val() == 'REDUCE_FOR_OVER_MONEY'){
            if(parseFloat($(this).find("input[name$='minAmount']").val()) < parseFloat($(this).find("input[name$='discountAmount']").val())){
                reduceForOverMoneyValidate = false;
                return false;
            }
        }
    });
    if(!reduceForOverMoneyValidate){
        nsDialog.jAlert("购买金额不可小于优惠金额！");
        return false;
    }
    return true;
}



function _submitPromotionsForm(target,$table,callBack){
    var fromType=$(target).attr("fromType")
    var url="promotions.do?method=savePromotions";
    if(fromType=="inSales"){
        url="promotions.do?method=savePromotionsForInSales"
    }
    var isBargain=$(target).attr("pageType")=="manageBargain";
    $table.closest("#promotionsForm").ajaxSubmit({
        url:url,
        data:{
            timeFlag:$table.find(".date_select_define").attr("checked")
        },
        dataType: "json",
        type: "POST",
        success: function(result){
            if(!G.isEmpty(result)&&!result.success){
                nsDialog.jAlert(result.msg);
                return;
            }
            if(isBargain){
                var pageSource= $("#addPromotions_pageSource").val();
                var bTable=$($table.find(".bargain-addPromotionsProductTable"));
                if(pageSource=="goodsInSalesEditor"){
                    $('[field="discountAmount"]').val(G.rounding(bTable.find('[name$="discountAmount"]').val()));
                    $('[field="bargainType"]').val(G.normalize(bTable.find('[name$="bargainType"]').val()));
                    $('[field="promotionsType"]').val("BARGAIN");
                    $('[field="limitFlag"]').val(bTable.find('.limitFlagChk').attr("checked"));
                    $('[field="limitAmount"]').val(G.rounding(bTable.find('[name$="limitAmount"]').val()));
                }else if(pageSource=="batchGoodsInSalesEditor"){
                    bTable.find(".product-item").each(function(){
                        var productId=$(this).find('[field="productId"]').val()
                        var $parentItem=$('.itemChk:[productId="'+productId+'"]');
                        $parentItem.attr("discountAmount",G.rounding(bTable.find('[name$="discountAmount"]').val()));
                        $parentItem.attr("bargainType",G.normalize(bTable.find('[name$="bargainType"]').val()));
                        $parentItem.attr("promotionsType","BARGAIN");
                        $parentItem.attr("limitFlag",bTable.find('.limitFlagChk').attr("checked"));
                        $parentItem.attr("limitAmount",G.rounding(bTable.find('[name$="limitAmount"]').val()));
                    });
                }
            }
            callBack(result,true);
        },
        error:function(){
            nsDialog.jAlert("网络异常！");
            $(".J-initialCss").placeHolder("reset");
        }
    });
}

function saveMLJ(target,callBack){
    var $target=$(target);
    var $table=$(".table_mlj");
    if(G.isEmpty($table)||!validatePromotionsInfo($table)){
        $target.removeAttr("disabled");
        return;
    }
    if(!validateMLJPromotionsRule()){
        $target.removeAttr("disabled");
        return;
    }
    _submitPromotionsForm(target,$table,callBack);
}

function saveMJS(target,callBack){
    var $target=$(target);
    var $table=$(".table_mjs");

    if(G.isEmpty($table)||!validatePromotionsInfo($table)){
        $target.removeAttr("disabled");
        return;
    }
    if(!validateMJSPromotionsRule()){
        $target.removeAttr("disabled");
        return;
    }
    $(".J-initialCss").placeHolder("clear");
    _submitPromotionsForm(target,$table,callBack);
}

function saveFreeShipping(target,callBack){
    var $target=$(target);
    var $table=$(".table_free_shipping");

    if(G.isEmpty($table)||!validatePromotionsInfo($table)){
        $target.removeAttr("disabled");
        return;
    }
    if(!validateFreeShipping()){
        $target.removeAttr("disabled");
        return;
    }
    $table.find("#areaData").children().remove();
    _submitPromotionsForm($target,$table,callBack);
}

function saveBargain(target,callBack){
    var $target=$(target);
    var $table=$(".table_bargain");

    if(G.isEmpty($table)||!validatePromotionsInfo($table)){
        $target.removeAttr("disabled");
        return;
    }
    var fromType=$(target).attr("fromType")
    if(fromType=="inSales"){
        var checkedHasValue = true;
        $(".bargainTD").each(function(){
            var discountAmount=G.rounding($(this).find(".discount_amount").val());
            var bargainType=$(this).find(".bargainType").val();
            if(bargainType=="DISCOUNT"){
                if(discountAmount<=0||discountAmount>=10){
                    nsDialog.jAlert("折扣应是0到10的数字。");
                    checkedHasValue = false;
                    $target.removeAttr("disabled");
                    return false;
                }
            }
            if(bargainType=="BARGAIN"){
                if(discountAmount<=0){
                    nsDialog.jAlert("特价应大于0。");
                    checkedHasValue = false;
                    $target.removeAttr("disabled");
                    return false;
                }
            }
            var limitAmount =G.rounding($(this).find(".limit_amount").val());
            if($(this).find(".limitFlagChk").attr("checked")){
                if(limitAmount<=0){
                    nsDialog.jAlert("限购数量应大于0。");
                    checkedHasValue = false;
                    $target.removeAttr("disabled");
                    return false;
                }
            }
        });
        if(!checkedHasValue){
            $target.removeAttr("disabled");
            return false;
        }
    }
    _submitPromotionsForm(target,$table,callBack);
}

function savePromotions(target,callBack){
    var pageType=$(target).attr("pageType");
    if(pageType=="manageMLJ"){
        saveMLJ(target,callBack);
    }else if(pageType=="manageMJS"){
        saveMJS(target,callBack);
    }else if(pageType=="manageBargain"){
        saveBargain(target,callBack);
    }else if(pageType=="manageFreeShipping"){
        saveFreeShipping(target,callBack);
    }
}



function initPromotionList(result){
    $("#promotionsTable tr:gt(1)").remove();
    if(G.isEmpty(result)||G.isEmpty(result.results)){
        $("#total_promotions").text(0);
        var  tr='<tr class="titBody_Bg"><td colspan="8" style="text-align:center;">您还没有创建任何促销！</td></tr><tr class="titBottom_Bg"><td colspan="8"></td></tr>';
        $("#promotionsTable").append(tr);
        return;
    }
    var promotionsList=result.results;
    var tr;
    for(var i=0;i<promotionsList.length;i++){
        var promotion=promotionsList[i];
        var promotionsId=promotion.idStr;
        var name=G.normalize(promotion.name);
        var content=generatePromotionsContentByPromotions(promotion,"td");
        var startTimeStr=promotion.startTimeStr;
        var endTimeStr=G.normalize(promotion.endTimeStr);
        var typeStr=G.normalize(promotion.typeStr);
        var type=promotion.type;
        var statusStr=promotion.statusStr;
        var status=promotion.status;

        tr+="<tr class='titBody_Bg item' status='"+status+"'>";
        if(status=="UN_USED"||status=="UN_STARTED"){
            tr+='<td style="padding-left:10px;"><a class="blue_color" onclick="toPromotionsManager(\''+type+'\',\''+promotionsId+'\')">'+name+'</a></td>';
        }else{
            tr+='<td style="padding-left:10px;">'+name+'</td>';
        }
        tr+='<td>'+content+'</td>';
        tr+='<td class="startTime">'+startTimeStr+'</td>';
        tr+='<td class="endTime">'+endTimeStr+'</td>';
        tr+='<td>'+typeStr+'</td>';
        tr+='<td>'+statusStr+'</td>';
        tr+='<td>';
        if(status!="SUSPEND"&&status!="EXPIRE"){
            tr+='<a style="margin-right: 5px" class="blue_color" onclick="toAddPromotionsProduct(\''+promotionsId+'\')">添加商品</a>';
        }
        if(status!="USING"){
            tr+='<a style="margin-right: 5px" class="blue_color" onclick="deletePromotions(\''+promotionsId+'\')">删除促销</a>';
        }


        if(status=="USING"){
            tr+='<a style="margin-right: 5px" class="blue_color" onclick="suspendPromotions(\''+promotionsId+'\')">暂停促销</a>';
        }
        if(status=="SUSPEND"){
            tr+='<a style="margin-right: 5px" class="blue_color" onclick="reStartPromotions(\''+promotionsId+'\',this)">开启促销</a>';
        }
        if(status!="UN_USED"&&status!="EXPIRE"){
            tr+='<a style="margin-right: 5px" class="blue_color" onclick="toProductInPromotion(\'' + promotionsId + '\')">促销详情</a>';
        }
        if(status=="UN_STARTED"||status=="USING"){
            tr+='<a style="margin-right: 5px" class="blue_color" onclick="toSendPromotionMsgByPromotionsId(\'' + promotionsId + '\')">发送消息</a>';
            tr+='<input id="item'+promotionsId+'" type="hidden" value="'+generatePromotionsContentByPromotions(promotion,"msg")+'"/>';
        }
        tr+='</td>';
        tr+="</tr>";
        tr+='<tr class="titBottom_Bg"><td colspan="8"></td></tr>';
    }
    $("#total_promotions").text(G.normalize(result.total));
    $("#promotionsTable").append(tr);
    $(".item").each(function(){
        if($(this).attr("status")=="EXPIRE"){
            $(this).find("td").css("color","#999999");
        }
    });
}

function initAddPromotionsProductTable(result){
    $("#addPromotionsProductTable tr:gt(1)").remove();
    if(G.isEmpty(result)){
        return;
    }
    var products=result.results;
    var tr;
    if(G.isEmpty(products)){
        tr+="<tr class='productItem titBody_Bg'><td colspan='6' style='text-align: center;'>没有上架商品可以添加。</td></tr>";
        tr+='<tr class="titBottom_Bg"><td colspan="8"></td></tr>';
        $("#addPromotionsProductTable").append(tr);
        return;
    }
    for(var i=0;i<products.length;i++){
        var product=products[i];
        var productInfo=generateProductInfo(product);
        var inventoryNum=G.rounding(product.inventoryNum)+G.normalize(product.sellUnit);
        var inventoryAveragePrice=G.rounding(product.inventoryAveragePrice);
        var tradePrice=G.rounding(product.tradePrice);
        var productId=G.normalize(product.productLocalInfoIdStr);

        var promotions=product.promotionsDTOs;
        var pomotionsNames=new Array();
        if(!G.isEmpty(promotions)){
            for(var j=0;j<promotions.length;j++){
                pomotionsNames.push(promotions[j].name);
            }
        }
        pomotionsNames=pomotionsNames.toString();

        var promotionsing=!G.isEmpty(promotions);  //判断商品是否正在促销
        if(promotionsing){
            var pTitle=generatePromotionsAlertTitle(product);
            productInfo += pTitle;
        }
        tr+='<tr class="productItem titBody_Bg">';
//        tr+='<input type="hidden" name="promotionsProductDTOList['+i+'].productLocalInfoId" value="'+productId+'"></td>';
        tr+='<td style="padding-left:10px;"><input class="productChk itemChk" type="checkbox" productId="'+productId+'"/></td>';
        tr+='<td class="promotions_info_td">'+productInfo+'</td>'
        tr+='<td>'+inventoryNum+'</td>'
        tr+='<td>'+'<span class="arialFont">&yen;</span>'+inventoryAveragePrice+'</td>'
        tr+='<td>'+'<span class="arialFont">&yen;</span>'+tradePrice+'</td>'
        tr+='<td>'+pomotionsNames+'</td>'
        tr+='</tr>';
        tr+='<tr class="titBottom_Bg"><td colspan="8"></td></tr>';
    }
    $("#addPromotionsProductTable").append(tr);
}

function initProductInPromotionTable(result){
    $("#productInPromotionTable tr:gt(1)").remove();
    if(G.isEmpty(result)){
        return;
    }
    var products=result.results;
    if(G.isEmpty(products)){
        tr+="<tr class='productItem titBody_Bg'><td colspan='8' style='text-align: center;'>未添加任何商品。</td></tr>";
        tr+='<tr class="titBottom_Bg"><td colspan="8"></td></tr>';
        $("#productInPromotionTable").append(tr);
        return;
    }
    var product_temp="";
    var pContent="";
    var tr;
    for(var i=0;i<products.length;i++){
        var product=products[i];
        var productInfo=generateProductInfo(product);
        var inventoryNum=G.rounding(product.inventoryNum)+G.normalize(product.sellUnit);
        var inventoryAveragePrice=G.rounding(product.inventoryAveragePrice);
        var inSalesPrice = G.rounding(product.inSalesPrice);
        var tradePrice=G.rounding(product.tradePrice);
        var promotionsType="";
        var productId=G.normalize(product.productLocalInfoIdStr);
        var promotionsList=product.promotionsDTOs;
        var pomotionsNames=new Array();
        var promotionsId;
        var content=generatePromotionsContentByProduct(product,"td");
        product_temp=product;
        if(!G.isEmpty(promotionsList)){
            for(var j=0;j<promotionsList.length;j++){
                var promotions=promotionsList[j];
                pomotionsNames.push(promotions.name);
                promotionsId=promotions.idStr;
            }
        }
        pomotionsNames=pomotionsNames.toString();

        tr+='<tr class="titBody_Bg">';
        tr+='<td style="padding-left:10px;"><input class="itemChk" type="checkbox" productId="'+productId+'"/></td>';
        tr+='<td>'+productInfo+'</td>';
        tr+='<td>'+inventoryNum+'</td>';
        tr+='<td>'+'<span class="arialFont">&yen;</span>'+inventoryAveragePrice+'</td>';
        tr+='<td>'+'<span class="arialFont">&yen;</span>'+inSalesPrice+'</td>';
        tr+='<td>'+pomotionsNames+'</td>';
        tr+='<td>'+content+'</td>';
        tr+='<td>'+'<a onclick="deletePromotionsProduct(\''+promotionsId+'\',\''+productId+'\')" class="blue_color">退出</a>'+'</td>';
        tr+='</tr>';
        tr+='<tr id="itemBottom'+productId+'" class="titBottom_Bg"><td colspan="9"></td></tr>';
    }
    if(!G.isEmpty(product_temp)){
        $("#pContent").val(generatePromotionsContentByProduct(product_temp,"msg").replaceAll("<br/>","").replaceAll("<span>","").replaceAll("</span>",""));
    }
    $("#productInPromotionTable").append(tr);
}

var promotionsPruductMap;
function initPromotionsProductTable(result){
    $("#promotionsProductTable tr:gt(1)").remove();
    promotionsPruductMap={};
    if(G.isEmpty(result)){
        return;
    }
    var products=result.results;
    var tr="";
    if(G.isEmpty(products)){
        tr+="<tr class='titBody_Bg'><td colspan='8' style='text-align: center;'>没有正在进行的促销商品。</td></tr>";
        tr+='<tr class="titBottom_Bg"><td colspan="8"></td></tr>';
        $("#promotionsProductTable").append(tr);
        return;
    }
//    pMap=new Map();
    for(var i=0;i<products.length;i++){
        var product=products[i];
//        pMap.put(product.productLocalInfoIdStr,product);
        var productInfo=generateProductInfo(product);
        var inventoryNum=G.rounding(product.inventoryNum)+G.normalize(product.sellUnit);
        var inventoryAveragePrice=G.rounding(product.inventoryAveragePrice);
        var tradePrice=G.rounding(product.tradePrice);
        var productId=G.normalize(product.productLocalInfoIdStr);
        var promotionsType="";
        var promotionsList=product.promotionsDTOs;
        if(G.isEmpty(promotionsList)){
            continue;
        }
        var typeArr=new Array();
        for(var j=0;j<promotionsList.length;j++){
            var promotions=promotionsList[j];
            typeArr.push(getPromotionsName(promotions.type));
        }
        promotionsType=typeArr.toString();
        promotionsPruductMap[productId]=promotionsList;
        tr+='<tr class="titBody_Bg">';
        tr+='<td style="padding-left:10px;">'+productInfo+'</td>'
        tr+='<td>'+inventoryNum+'</td>'
        tr+='<td>'+'<span class="arialFont">&yen;</span>'+inventoryAveragePrice+'</td>'
        tr+='<td>'+'<span class="arialFont">&yen;</span>'+tradePrice+'</td>'
        tr+='<td>'+promotionsType+'</td>'
        tr+='<td>'+'<a class="blue_color arrowDown promotionsDetailBtn" onclick="getPromotionsProductDetail(\''+productId+'\',this)">促销活动详情</a>'+'</td>'
        tr+='</tr>';
        tr+='<tr id="itemBottom'+productId+'" class="titBottom_Bg"><td colspan="8"></td></tr>';
    }
    $("#promotionsProductTable").append(tr);
}

function getPromotionsName(type) {
    var orderEnum = {
        "MLJ": "满立减",
        "MJS": "满就送",
        "BARGAIN": "特价商品",
        "FREE_SHIPPING": "送货上门",
        "SPECIAL_CUSTOMER": "客户优惠"
    };
    return orderEnum[type] || type;
}

function getPromotionsProductDetail(productId,target){
    var $target=$(target);
    var promotionsDetial= $("#itemBottom"+productId).next();
    if(promotionsDetial.hasClass("promotionsDetail")){
        promotionsDetial.remove();
    }
    if($target.hasClass("arrowUp")){
        $target.removeClass("arrowUp");
        $target.addClass("arrowDown");
    }else{
        $target.removeClass("arrowDown");
        $target.addClass("arrowUp");
        var promotionsDTOs="";
        promotionsDTOs=promotionsPruductMap[String(productId)];
        if(G.isEmpty(promotionsDTOs)){
            return;
        }
        var pTr='<tr class="promotionsDetail"><td colspan="8"><div style="padding-left: 20px" class="divListInfo div_promotionInfo"><div class="title">参与促销</div>';
        pTr+='<table cellpadding="0" cellspacing="0" class="tab_document"><col width="110"><col width="140"><col width="250"><col width="100"><col width="80"><col width="45">';
        pTr+='<tr class="tit_bg"><td style="padding-left:5px;">促销名称</td><td>促销时间</td><td>促销内容</td><td>促销类型</td><td>促销状态</td><td>操作</td></tr>';
        for(var i=0;i<promotionsDTOs.length;i++){
            var promotions=promotionsDTOs[i];
            var promotionsId=promotions.idStr;
            var name=promotions.name;
            var description=promotions.description;
            var startTimeStr=G.normalize(promotions.startTimeStr);
            var endTimeStr=G.normalize(promotions.endTimeStr);
            var typeStr=promotions.typeStr;
            var statusStr=promotions.statusStr;
            var status=promotions.status;
            var date=startTimeStr+"~"+endTimeStr;
            if(G.isEmpty(endTimeStr)){
                date="不限期";
            }
            var pArr=new Array();
            pArr.push(promotions);
            var content=generatePromotionsContentByPromotions(promotions);

            pTr+='<tr><td style="padding-left:5px;">'+name+'</td>'
            pTr+='<td><div class="line">'+date+'</div></td>'
            pTr+='<td><div class="line">'+content+'</div></td>'
            pTr+='<td>'+typeStr+'</td>';
            pTr+='<td>'+statusStr+'</td>';
            pTr+='<td><a class="blue_color" onclick="deletePromotionsProduct(\''+promotionsId+'\',\''+productId+'\',\''+"pList"+'\')">退出</a></td>';
            pTr+='</tr>';
        }
        pTr+='</table></div></td></tr>';
        $("#itemBottom"+productId).after(pTr);
    }

}


function deletePromotionsProduct(promotionsId,productIdArr,from){
    if(G.isEmpty(promotionsId)||G.isEmpty(productIdArr)){
        return;
    }
    nsDialog.jConfirm("是否确认将该商品退出活动?",null,function(resultVal){
        if(resultVal){
            APP_BCGOGO.Net.asyncAjax({
                url: "promotions.do?method=deletePromotionsProduct",
                type: "POST",
                cache: false,
                data: {
                    promotionsId:promotionsId,
                    productIds:productIdArr
                },
                dataType: "json",
                success: function (json) {
                    if(json.success){
                        if(from=="pList"){
                            refreshPage();
                        }else{
                            toProductInPromotion(promotionsId);
                        }
                    }else{
                        nsDialog.jAlert(json.msg);
                    }
                },
                error:function(){
                    nsDialog.jAlert("网络异常！");
                }
            });
        }
    });

}

function deletePromotions(promotionsId){
    if(G.isEmpty(promotionsId)){
        return;
    }
    nsDialog.jConfirm("是否确认删除该促销?",null,function(resultVal){
        if(resultVal){
            APP_BCGOGO.Net.asyncAjax({
                url:"promotions.do?method=deletePromotions",
                type: "POST",
                cache: false,
                data: {
                    promotionsId:promotionsId
                },
                dataType: "json",
                success: function (json) {
                    if(json.success){
                        toPromotionsList()
                    }else{
                        nsDialog.jAlert(json.msg);
                    }
                }
            });
        }
    });
}

//function generateProductInfo(product){
//    if(G.isEmpty(product)) return "";
//    var productInfo="";
//    productInfo+="<span class='yellow_color'>"+G.normalize(product.name)+"</span>&nbsp;";
//    productInfo+=G.normalize(product.brand)+'&nbsp;';
//    productInfo+=G.normalize(product.spec);
//    productInfo+=G.normalize(product.model);
//    productInfo+=G.normalize(product.vehicleModel);
//    productInfo+=G.normalize(product.vehicleBrand);
//    return productInfo;
//}

function cleanPriceInput(target){
    var $target=$(target);
    if($target.hasClass("clearBoardBtn")){
        $(".priceInput").val("");
    }else if($target.hasClass("cleanSaleBoardBtn")){
        $(".salePriceInput").val("");
    }
    $(".salePriceBoard,.priceBoard").hide();
}

function searchForPriceBoard(){
    $(".salePriceBoard,.priceBoard").hide();
    $("#searchPromotionsProductBtn").click();
}

function reStartPromotions(promotionsId,target){
    if(G.isEmpty(promotionsId)){
        return;
    }
    var startTime=$(target).closest("tr").find(".startTime").text();
    var endTime=$(target).closest("tr").find(".endTime").text();
    $("#reStartPromotionsConfirm").find("[name='startTimeStr']").val($("#serviceStartTime").val());
    $("#reStartPromotionsConfirm").find("[name='endTimeStr']").val(endTime);
    $("#reStartPromotionsConfirm").dialog({
//        resizable: false,
        modal: true,
        draggable:true,
        height: 200,
        width: 380,
        buttons: {
            "确定": function() {
                var reStartTime= $("#reStartPromotionsConfirm").find("[name='startTimeStr']").val();
                var reEndTime=$("#reStartPromotionsConfirm").find("[name='endTimeStr']").val();
                if(G.isEmpty(reStartTime)){
                    nsDialog.jAlert("促销开始时间不应为空！");
                    return;
                }
                APP_BCGOGO.Net.asyncAjax({
                    url: "promotions.do?method=updatePromotionStatus",
                    data:{
                        id:promotionsId,
                        status:"USING",
                        startTimeStr:reStartTime,
                        endTimeStr:reEndTime,
                        from:"reStartPromotion"
                    },
                    type: "POST",
                    cache: false,
                    dataType: "json",
                    success: function (result) {
                        $("#reStartPromotionsConfirm").dialog("close");
                        if(!result.success){
                            nsDialog.jAlert(result.msg);
                            return;
                        }
                        window.location.reload();
                    },
                    error:function(){
                        $(this).dialog("close");
                        nsDialog.jAlert("网络异常！");
                    }
                });
            },
            "取消": function() {
                $(this).dialog("close");
            }
        }
    });

}

function suspendPromotions(promotionsId){
    nsDialog.jConfirm("是否确认暂停该促销?暂停后将取消活动，且活动中的商品不可被同时段其他促销使用！",null,function(resultVal){
        if(resultVal){
            updatePromotionStatus(promotionsId,"SUSPEND",function(){
                window.location.reload();
            });
        }
    });
}


function updatePromotionStatus(promotionsId,status,callBack){
    if(G.isEmpty(promotionsId)||G.isEmpty(status)){
        return;
    }
    APP_BCGOGO.Net.asyncAjax({
        url: "promotions.do?method=updatePromotionStatus",
        data:{
            id:promotionsId,
            status:status
        },
        type: "POST",
        cache: false,
        dataType: "json",
        success: function (result) {
            if(!result.success){
                nsDialog.jAlert(result.msg);
                return;
            }
            callBack();
        },
        error:function(){
            nsDialog.jAlert("网络异常！");
        }
    });
}

function clearSort() {
    var searchField = ["searchWord", "productName", "productBrand", "productSpec", "productModel",
        "productVehicleBrand", "productVehicleModel", "product_kind", "commodityCode"];
    var isEmpty = true;
    for (var i = 0; i < searchField.length; i++) {
        var thisVal = $.trim($("#" + searchField[i]).val());
        var thisInitVal = $.trim($("#" + searchField[i]).attr("initialValue"));
        if (!GLOBAL.Lang.isEmpty(thisVal) && thisVal != thisInitVal) {
            isEmpty = false;
        }
    }
    if (!isEmpty) {
        $(".J_product_sort").each(function () {
            $(this).removeClass("hover");
        });
        $("#sortStatus").val("");
    } else {
        if (GLOBAL.Lang.isEmpty($("#sortStatus").val())) {
            var $dom = $("[sortFiled='inventoryAmount']");
            $dom.addClass("hover");
            $dom.find(".J-sort-span").removeClass("arrowUp").addClass("arrowDown");
            $dom.attr("currentSortStatus", "Desc");
            $dom.find(".alertBody").html($dom.attr("ascContact"));
            $("#sortStatus").val($dom.attr("sortFiled") + $dom.attr("currentSortStatus"));
        }
    }

}



function backToPromotionManagerAlert(target){
    if($(target).attr("pagetype") == 'manageFreeShipping') {
        doHiddeDiv();
    } else {
        doCloseDialog(target);
    }

    if($(target).attr("lastStepType")=="batch"){
//         getBatchPromotionManagerAlert(target);
        $("#batchPromotionManagerAlert").dialog();
    }else{
        getPromotionManagerAlert();
    }
}

function doCloseDialog(target){
    $(target).closest(".alertMain").dialog("close");
}

function doHiddeDiv() {
    $("#manageFreeShippingAlertDiv").hide();
    $("#mask").css("display", "none");
}

function getOverUnit($target){
    var $form = $target.closest("form");
    if(G.isEmpty($form)){
        return;
    }
    var limiter=$form.find("#promotionsLimiterSelector").val();
    var unit;
    if(limiter == 'OVER_AMOUNT'){
        unit = "件";
    }else if(limiter == "OVER_MONEY"){
        unit = "元";
    }
    return unit;
}

//送货上门
function initPromotionsArea(promotions){
    if(G.isEmpty(promotions)) return;
    var areaList=new Array();
    var areas=promotions.areaDTOs;
    if(!G.isEmpty(areas)){
        for(var i=0;i<areas.length;i++){
            areaList.push(areas[i].no);
        }
    }
    if(areaList.length!=0){
        $("input[name='gArea']").each(function(){
            if(arrayUtil.contains(areaList, $(this).attr("areaNo")) && !$(this).attr("checked")){
                $(this).attr("checked",true);
                promotionsAreaClick($(this)[0]);
            }
        })
        $(".cityContent input[type='checkbox']").each(function(){
            if(arrayUtil.contains(areaList,$(this).attr("areaNo"))){
                $(this).attr("checked",true);
                syncProvinceWhenClickingCity($(this));
            }
        });
        generateAreaList();
        $(".postSelector").val(promotions.postType);
        $(".postSelector").change();
    }
}

function initAreaBoard(provinceList){
    if(G.isEmpty(provinceList)) return;
    var areaStr="";
    for(var i=0;i<provinceList.length;i++){
        var province=provinceList[i];
        var cityList=province.childAreaDTOList;
        var name=G.normalize(province.name);
        name=name.substr(0,2);
        var no=province.no;
        areaStr+='<span class="areaSpan j_provinceDiv"><label class="rad checkbox"><input onclick="areaBoardProvinceClick(this)" class="areaChk j_province" type="checkbox" value="'+name+'" areaNo="'+no+'"/>'+name+'</label>';
        areaStr+='<div class="cityContent hide_content" style="display: none;">';
        for(var j=0;j<cityList.length;j++){
            var city=cityList[j];
            var cityName=city.name;
            cityName=cityName.substr(0,3);
            var cityNo=city.no;
            areaStr+='<label class="rad"><input type="checkbox" onclick="areaBoardCityClick(this)" class="j_city" value="'+cityName+'" areaNo="'+cityNo+'"/>'+cityName+'</label>';
        }
        areaStr+='</div></span>';
    }
    $("#areaBoardDiv").append(areaStr);

    $(".areaChk").live("mouseover",function(){
        $(".cityContent").hide();
        $(".areaSpan").removeClass("hover");
        var $target=$(this).closest(".areaSpan");
        $target.addClass("hover")
        $target.find(".cityContent").show() ;
    });

    $(".areaSpan").live("mouseleave",function(){
        var $target=$(this).closest(".areaSpan");
        $target.removeClass("hover")
        $target.find(".cityContent").hide() ;
    });

}

function areaBoardProvinceClick(target){
    var $areaSpan=$(target).closest(".areaSpan");
    var $cityCkb=$areaSpan.find(".cityContent input[type='checkbox']");
    if($(target).attr("checked")){
        if($(".j_owned_province").attr('areano') == $(target).attr("areano")){
            $(".j_owned_province, .j_owned_city").attr("checked", true);
        }
        $cityCkb.attr("checked",true);
    }else{
        if($(".j_owned_province").attr('areano') == $(target).attr("areano")){
            $(".j_owned_province, .j_owned_city").attr("checked", "");
        }
        $cityCkb.attr("checked",false);
        $("#countryArea").attr("checked", false);
    }
    syncCountryWhenClickingProvince($(target));
    generateAreaList();
}

function syncCountryWhenClickingProvince($province){
    if($province.attr("checked")){
        var allProvinceChecked = true;
        $(".j_province").each(function(){
            if(!$(this).attr("checked")){
                allProvinceChecked = false;
                return false;
            }
        });
        if(allProvinceChecked){
            $("#countryArea").attr("checked", true);
        }
    }else{
        $("#countryArea").attr("checked", false);
    }
}

function syncProvinceWhenClickingCity($target) {
    var allCityChecked = false;
    $target.parents("div.cityContent").find("input.j_city").each(function () {
        if (!$(this).attr("checked")) {
            allCityChecked = false;
            return false;
        }
        allCityChecked = true;
    });
    var $province = $target.parents("span.j_provinceDiv").find("input.j_province");
    if (allCityChecked) {
        $province.attr("checked", true);
        if ($(".j_owned_province").attr('areano') == $province.attr("areano")) {
            $(".j_owned_province").attr("checked", true);
        }
    } else {
        $province.attr("checked", "");
        if ($(".j_owned_province").attr('areano') == $province.attr("areano")) {
            $(".j_owned_province").attr("checked", "");
        }
    }
    syncCountryWhenClickingProvince($province);
}

function areaBoardCityClick(target){
    var $target = $(target);
    syncProvinceWhenClickingCity($target);
    if($(".j_owned_city").attr("areano") == $target.attr("areano")){
        if($target.attr("checked")){
            $(".j_owned_city").attr("checked", true);
        }else{
            $(".j_owned_city").attr("checked", false);
        }
    }
    generateAreaList();
}

function generateAreaList(){
    $(".areaList .sArea").remove();
    var areaSpanList="";
    if($("#countryArea").attr("checked")){
        areaSpanList+='<span class="sArea"><input type="hidden" name="areaDTOs[0].no" value="-1"/>全国</span>';
    }else{
        var count=0;
        $(".cityContent input[type='checkbox']").each(function(){
            if($(this).attr("checked")){
                var name=$(this).val();
                var areaNo=$(this).attr("areaNo");
                areaSpanList+='<input type="hidden" name="areaDTOs['+count+'].no" value="'+areaNo+'"/>';
                count++;
            }
        });
        $(".j_provinceDiv").each(function(){
            var $provinceDiv = $(this);
            if($provinceDiv.find(".j_province").attr("checked")){
                var name=$provinceDiv.find(".j_province").val();
                var areaNo=$provinceDiv.find(".j_province").attr("areano");
                areaSpanList += '<span areaNo='+areaNo+' class="sArea">'+name+'</span>';
            }else{
                $provinceDiv.find("input.j_city").each(function(){
                    if($(this).attr("checked")){
                        var name=$(this).val();
                        var areaNo = $(this).attr("areano");
                        areaSpanList += '<span area='+areaNo +' class="sArea">'+name+'</span>';
                    }
                });
            }
        })
    }

    $(".areaList").html(areaSpanList);
}

function promotionsAreaClick(target){
    var $target=$(target);
    var isChecked=$target.attr("checked");
    $('[name="gArea"]').not($target).attr("checked",false);

    var area=$target.val();
    var areaNo=$target.attr("areaNo");
    if(G.isEmpty(areaNo)) return;
    if(area == 'COUNTRY'){
        if(isChecked){
            $("#areaBoardDiv .j_provinceDiv input").attr("checked", true);
            $(".j_owned_province, .j_owned_city").attr("checked", true);
        }else{
            $("#areaBoardDiv .j_provinceDiv input").attr("checked", false);
            $(".j_owned_province, .j_owned_city").attr("checked", false);
        }
    }else if(area=="PROVINCE"){
        $(".areaSpan .areaChk").each(function(){
            var $me=$(this);
            if($me.attr("areaNo")==areaNo){
                if(isChecked){
                    $me.attr("checked",true);
                    $me.closest(".areaSpan").find(".cityContent input[type='checkbox']").attr("checked",true);
                }else{
                    $me.attr("checked",false);
                    $me.closest(".areaSpan").find(".cityContent input[type='checkbox']").attr("checked",false);
                }
                syncCountryWhenClickingProvince($me);
            }
        });
        if(isChecked){
            $(".j_owned_city").attr("checked", true);
        }else{
            $(".j_owned_city").attr("checked", false);
        }
    }else if(area=="CITY"){
        $(".cityContent input[type='checkbox']").each(function(){
            var $me=$(this);
            if($me.attr("areaNo")==areaNo){
                if(isChecked){
                    $me.attr("checked",true);
                }else{
                    $me.attr("checked",false);
                }
            }
            syncProvinceWhenClickingCity($me);
        });
    }
    generateAreaList();
}

function addPromotionsCondition(target){
    var $target=$(target);
    var $table=$target.closest("table");
    if($target.val()=="NO"){
        $table.find('#promotionsRuleDiv').hide();
        $table.find("#minAmount").val("");
    }else{
        $table.find('#promotionsRuleDiv').show()

    }
}
//other


