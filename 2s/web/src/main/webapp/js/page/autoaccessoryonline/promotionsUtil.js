


function bindPromotionsAlertInfo(bindClassName){
    if(G.Lang.isEmpty(bindClassName)){
        bindClassName=".promotions_info_td";//默认
    }
    $(".p_icon_cheap").die();
    $(".p_icon_cheap").live("mouseover",function(e){
        var $target=$(e.target);
        var top = $target.position().top + 20;
        var left = $target.position().left;
        var content=$target.attr("pContent");
        var alertStr = '    <div class="promotions_alert little_prompt" style=" position:absolute;display:block;z-index:2000;left:' +left +'px;top:'+top+'px;">';
        alertStr += '        <div class="promptTop"></div>';
        alertStr += '        <div class="promptBody little_promptBody">';
        alertStr += '            <a class="icon_close"></a>';
        alertStr += '            <div class="promptLine" style="white-space:normal;word-break:break-all;">'+content+'</div>';
        alertStr += '        </div>';
        alertStr += '        <div class="promptBottom"></div>';
        alertStr += '    </div>';
        $target.after(alertStr);
    }).live("mouseleave",function(e){
            var $target=$(e.target);
            $target.closest(bindClassName).find(".promotions_alert").remove();
        });
}

function generatePromotionsTypeByProduct(productDTO){
    var typeArr=new Array();
    var promotionsProductDTOList=productDTO.promotionsProductDTOList;
    var promotionsList=productDTO.promotionsDTOs;
    if(!G.isEmpty(promotionsList)){
        for(var i=0;i<promotionsList.length;i++){
            var promotions=promotionsList[i];
            var type=promotions.type;
            if(!G.isEmpty(type)){
                typeArr.push(type);
            }
        }
    }
    return typeArr.toString();
}

function generatePromotionsIdListByProduct(productDTO){
    var typeArr=new Array();
    var promotionsProductDTOList=productDTO.promotionsProductDTOList;
    var promotionsList=productDTO.promotionsDTOs;
    if(!G.isEmpty(promotionsList)){
        for(var i=0;i<promotionsList.length;i++){
            var promotions=promotionsList[i];
            var pId=promotions.idStr;
            if(!G.isEmpty(pId)){
                typeArr.push(pId);
            }
        }
    }
    return typeArr.toString();
}

function _generateMJSContent(promotions,from){
    var content="";
    if(G.isEmpty(promotions)||promotions.type!="MJS"){
        return content;
    }
    var limiter=promotions.promotionsLimiter;
    var rules=promotions.promotionsRuleDTOList;
    if(G.isEmpty(rules)){
        return content;
    }
    var amountSpan='<span>';
    var amountBR='<br/>';
    if(from==alertFrom.productDetail){
        amountSpan='<span class="promotion-red-amount">';
        amountBR="";
    }
    var contentArr=new Array();
    for(var j=0;j<rules.length;j++){
        var rule=rules[j];
        var minAmount=rule.minAmount;
        var ruleMJSDTOs=rule.promotionsRuleMJSDTOs;
        if(!G.isEmpty(ruleMJSDTOs)){
            var temp="";
            if(limiter=="OVER_MONEY"){
                temp="满"+amountSpan+minAmount+"</span>元,";
            }else if(limiter=="OVER_AMOUNT"){
                temp="满"+amountSpan+minAmount+"</span>件,";
            }
            for(var k=0;k<ruleMJSDTOs.length;k++){
                var ruleMJSDTO=ruleMJSDTOs[k];
                var giftType=ruleMJSDTO.giftType;
                var giftName=ruleMJSDTO.giftName;
                var amount=ruleMJSDTO.amount;
                if(giftType=="GIFT"){
                    temp+="送"+amountSpan+giftName+'</span>';
                    if(G.rounding(amount)>0){
                        temp+= " " + amount+" 件 ";
                    }
                    temp+=amountBR;
                }else  if(giftType=="DEPOSIT"){
                    temp+="送预付金"+amount+"元 <br/>";
                    temp+="<br/>";
                }
            }
        }
        contentArr.push(temp);
    }
    content+='单个商品'+contentArr.toString()
    return content;
}

function _generateFreeShippingContent(promotions,from){
    var content="";
    if(G.isEmpty(promotions)||promotions.type!="FREE_SHIPPING"){
        return content;
    }
    var limiter=promotions.promotionsLimiter;
    if(G.isEmpty(limiter)){
        return "送货上门 ";
    }
    var rule=promotions.promotionsRuleDTOList[0];
    var minAmount=rule.minAmount;
    if(limiter=='OVER_MONEY'){
        if(G.rounding(minAmount)>0){
            content+="满"+minAmount+"元，";
        }
        content+="送货上门 ";
    }else if(limiter=='OVER_AMOUNT'){
        if(G.rounding(minAmount)>0){
            content+="满"+minAmount+"件，";
        }
        content+="送货上门 ";
    }
    return content;
}

var alertFrom={
    productDetail:"PRODUCT_DETIAL"
};

function _generateMLJContent(promotions,from){
    var content="";
    if(G.isEmpty(promotions)||promotions.type!="MLJ"){
        return content;
    }
    var limiter=promotions.promotionsLimiter;
    var rules=promotions.promotionsRuleDTOList;
    if(G.isEmpty(rules)){
        return content;
    }
    var amountSpan='<span>';
    var amountBR='<br/>';
    if(from==alertFrom.productDetail){
        amountSpan='<span class="promotion-red-amount">';
        amountBR="";
    }
    var contentArr=new Array();
    for(var j=0;j<rules.length;j++){
        var rule=rules[j];
        var minAmount=amountSpan+rule.minAmount+'</span>';
        var discountAmount=rule.discountAmount;
        var ruleType=rule.promotionsRuleType;
        if(ruleType=="DISCOUNT_FOR_OVER_MONEY"){
            contentArr.push(amountBR+'满'+minAmount+'元，打'+discountAmount+'折');
            limiter="OVER_MONEY";
        }else if(ruleType=="DISCOUNT_FOR_OVER_AMOUNT"){
            contentArr.push(amountBR+'满'+minAmount+'件，打'+discountAmount+'折');
            limiter="OVER_AMOUNT";
        }else if(ruleType=="REDUCE_FOR_OVER_MONEY"){
            contentArr.push(amountBR+'满'+minAmount+'元，减'+discountAmount+'元');
            limiter="OVER_MONEY";
        }else if(ruleType=="REDUCE_FOR_OVER_AMOUNT"){
            contentArr.push(amountBR+'满'+minAmount+'件，减'+discountAmount+'元');
            limiter="OVER_AMOUNT";
        }else if(ruleType=="GIVE_FOR_OVER_MONEY"){

        }else if(ruleType=="GIVE_FOR_OVER_AMOUNT"){

        }
    }
    if(limiter=="OVER_AMOUNT"){
        content+='每件商品'+contentArr.toString();
    }else  if(limiter=="OVER_MONEY"){
        content+='单笔金额'+contentArr.toString();
    }
    return content;
}

function _generateBargainContent(promotions,inSalesPrice,from){
    var content="";
    if(G.isEmpty(promotions)||promotions.type!="BARGAIN"){
        return content;
    }
    var ppList=promotions.promotionsProductDTOList;
    if(G.isEmpty(ppList)){
        return content;
    }
    var bargainProduct=ppList[0];
    if(G.isEmpty(bargainProduct)){
        return content;
    }
    if(bargainProduct.promotionsType!="BARGAIN"){
        return content;
    }
    var bargainType=bargainProduct.bargainType;
    var discountAmount=G.rounding(bargainProduct.discountAmount);
    var limitAmount=G.rounding(bargainProduct.limitAmount);
    content="特价商品";
    if(bargainType=="BARGAIN"){
        content+='省'+ G.sub(inSalesPrice,discountAmount) +'元';
    }else if(bargainType=="DISCOUNT"){
        content+='打'+discountAmount+'折';
    }
    if(limitAmount>0){
        content+='，每位限购'+limitAmount+'件,';
    }
    return content;
}


function generatePromotionsContentByProduct(productDTO,from){
    var content="";
    var promotionsList=productDTO.promotionsDTOs;
    var inSalesPrice=G.rounding(productDTO.inSalesPrice);
    if(G.isEmpty(promotionsList)){
        return content;
    }
    for(var i=0;i<promotionsList.length;i++){
        var promotions=promotionsList[i];
        var type=promotions.type;
        var timeStr=',活动时间：'+G.normalize(promotions.startTimeStr);
        var endTime=G.normalize(promotions.endTimeStr);
        if(G.isEmpty(endTime)){
            timeStr+=',不限期';
        }else{
            timeStr+='至'+G.normalize(promotions.endTimeStr);
        }
        if(type=="MLJ"){
            content+=_generateMLJContent(promotions,from);
        }else if(type=="MJS"){
            content+= _generateMJSContent(promotions,from);
        }else if(type=="FREE_SHIPPING"){
            content+= _generateFreeShippingContent(promotions,from);
        }else if(type=="BARGAIN"){
            content+= _generateBargainContent(promotions,inSalesPrice,from);
        }
        if(!G.isEmpty(content)){
            content+=timeStr;
            if(i!=promotionsList.length - 1){
                if(from=="alert"){
                    content += "<hr class='dashed'/>";
                }else{
                    content += "<br/>";
                }
            }
        }
    }
    if(from=="msg"){
        content+='赶紧来看看吧！';
    }
    return content;
}


function generatePromotionsContentByPromotions(promotions,from){
    if(G.isEmpty(promotions)){
        return;
    }
    var content="";
    var type=promotions.type;

    if(type=="MLJ"){
        var limiter="";
        var rules=promotions.promotionsRuleDTOList;
        if(!G.isEmpty(rules)){
            var contentArr=new Array();
            for(var i=0;i<rules.length;i++){
                var rule=rules[i];
                var minAmount=rule.minAmount;
                var discountAmount=rule.discountAmount;
                var ruleType=rule.promotionsRuleType;
                if(ruleType=="DISCOUNT_FOR_OVER_MONEY"){
                    limiter="OVER_MONEY";
                    contentArr.push('满'+minAmount+'元，打'+discountAmount+'折');
                }else if(ruleType=="DISCOUNT_FOR_OVER_AMOUNT"){
                    limiter="OVER_AMOUNT";
                    contentArr.push('满'+minAmount+'件，打'+discountAmount+'折');
                }else if(ruleType=="REDUCE_FOR_OVER_MONEY"){
                    limiter="OVER_MONEY";
                    contentArr.push('满'+minAmount+'元，减'+discountAmount+'元');
                }else if(ruleType=="REDUCE_FOR_OVER_AMOUNT"){
                    limiter="OVER_AMOUNT";
                    contentArr.push('满'+minAmount+'件，减'+discountAmount+'元');
                }else if(ruleType=="GIVE_FOR_OVER_MONEY"){

                }else if(ruleType=="GIVE_FOR_OVER_AMOUNT"){

                }
            }

        }
        if(limiter=="OVER_AMOUNT"){
            content+='每件商品'+contentArr.toString();
        }else  if(limiter=="OVER_MONEY"){
            content+='单笔金额'+contentArr.toString();
        }

    }else if(type=="MJS"){
        var promotionsLimiter=promotions.promotionsLimiter;
        var rules=promotions.promotionsRuleDTOList;
        if(!G.isEmpty(rules)){
            var contentArr=new Array();
            for(var j=0;j<rules.length;j++){
                var rule=rules[j];
                var minAmount=rule.minAmount;
                var ruleMJSDTOs=rule.promotionsRuleMJSDTOs;
                var temp="";
                if(!G.isEmpty(ruleMJSDTOs)){
                    if(promotionsLimiter=="OVER_MONEY"){
                        temp="满"+minAmount+"元,";
                    }else if(promotionsLimiter=="OVER_AMOUNT"){
                        temp="满"+minAmount+"件,";
                    }
                    for(var k=0;k<ruleMJSDTOs.length;k++){
                        var ruleMJSDTO=ruleMJSDTOs[k];
                        var giftType=ruleMJSDTO.giftType;
                        var giftName=ruleMJSDTO.giftName;
                        var amount=ruleMJSDTO.amount;
                        if(giftType=="GIFT"){
                            temp+="送"+giftName+" ";
                            if(G.rounding(amount)>0){
                                temp+=amount+" 件 ";
                            }
                        }else  if(giftType=="DEPOSIT"){
                            temp+="送预付金"+amount+"元 ";
                        }
                    }
                }
                if(!G.isEmpty(temp))
                    contentArr.push(temp);
            }
            if(contentArr.length>0){
                content+='单个商品'+contentArr.toString()
            }
            if(from=="alert"){

            }
        }

    }else if(type=="FREE_SHIPPING"){
        var limiter=promotions.promotionsLimiter;
        if(G.isEmpty(limiter)){
            content="送货上门";
        }else{
            if(!G.isEmpty(promotions.promotionsRuleDTOList)){
                var rule=promotions.promotionsRuleDTOList[0];
                var minAmount=rule.minAmount;
                if(limiter=='OVER_MONEY'){
                    if(G.rounding(minAmount)==0){
                        content+="送货上门";
                    }else{
                        content+="满"+minAmount+"元，送货上门";
                    }
                }else  if(limiter=='OVER_AMOUNT'){
                    if(G.rounding(minAmount)==0){
                        content+="送货上门";
                    }else{
                        content+="满"+minAmount+"件，送货上门";
                    }
                }
            }
        }

    }else if(type=="BARGAIN"){
        content="特价商品";
    }
    if(from=="msg"){
        content+=';活动时间：'+G.normalize(promotions.startTimeStr);
        var endTime=G.normalize(promotions.endTimeStr);
        if(G.isEmpty(endTime)){
            content+=',不限期';
        }else{
            content+='至'+G.normalize(promotions.endTimeStr);
        }
        content+=',赶紧来看看吧！';
    }

    return content;
}


function addUsedBargainNum(product, $itemDom, orderId){
    var usedBargainLimitAmount = null;
    var promotionsProducts= product.promotionsProductDTOList;
    if(!G.isEmpty(promotionsProducts)){
        var promotionsProduct = promotionsProducts[0];
        usedBargainLimitAmount = getBargainLimitHistoryAmount(product.productLocalInfoIdStr, promotionsProduct.promotionsIdStr, orderId);
    }

    if(usedBargainLimitAmount!=null){
        $itemDom.find(".promotions_info_td .promotionsInfo").attr('usedBargainLimitAmount', usedBargainLimitAmount);
    }
}

var pMap="";
/**
 * 整个单据
 * @param productIdArr
 */
function initOrderPromotionsDetail(productIdArr, orderId){
    if(G.isEmpty(productIdArr)){
        return ;
    }
    APP_BCGOGO.Net.syncAjax({
        url: "promotions.do?method=getOrderPromotionsDetail",
        data:{
            productIdArr:productIdArr.toString(),
            promotionsTypeStatusList:"MLJ_USING,MJS_USING,FREE_SHIPPING_USING,BARGAIN_USING",
            orderId:orderId
        },
        type: "POST",
        cache: false,
        dataType: "json",
        success: function (javaMap) {
            if(G.isEmpty(javaMap)){
                return;
            }
            _addToPromotionsMap(javaMap);
            //init promotions info
            $(".item").each(function(){
                var productId =$(this).find("[name$='supplierProductId']").val();
                if(G.isEmpty(productId)){
                    productId=$(this).find(".productId").val();
                }
                if(arrayUtil.contains(productIdArr,productId)){
                    var product=pMap.get(productId);
                    if(product && $.inArray(productId, productIdArr)!=-1){
                        var isPromotionsing=!G.isEmpty(product.promotionsDTOs);
                        if(isPromotionsing){
                            product=filterUsingPromotion(product,"search");
                            var pTitle=generatePromotionsAlertTitle(product);
                            $(this).find(".promotions_info_td a.promotionsInfo").remove();
                            $(this).find(".promotions_info_td").append(pTitle);
                            addUsedBargainNum(product, $(this), orderId);
                        }
                    }
                }
            });

            $(".item").each(function(){
                var productId =$(this).find("[name$='supplierProductId']").val();
                if(G.isEmpty(productId)){
                    productId=$(this).find(".productId").val();
                }
                if(arrayUtil.contains(productIdArr,productId)){
                    if($.isFunction(window.calculate)){
                        calculate($(this));
                    }else if($.isFunction(window.calculateShoppingCart)){
                        calculateShoppingCart($(this));
                    }
                }
            });
        },
        error:function(error){
            nsDialog.jAlert("网络异常！");
        }
    });
}

function initOrderPromotionsDetailForShowPage(productIdArr, $orderTable){
    if(G.isEmpty(productIdArr)){
        return ;
    }
    var orderId = $orderTable.find(".J-orderId").val();
    APP_BCGOGO.Net.asyncAjax({
        url: "promotions.do?method=getOrderPromotionsDetail",
        data:{
            productIdArr:productIdArr.toString(),
            orderId:orderId
        },
        type: "POST",
        cache: false,
        dataType: "json",
        success: function (javaMap) {
            _addToPromotionsMap(javaMap);
            //init promotions info
            $orderTable.find(".item").each(function(){
                var productId =$(this).find(".j_supplierProductId").text();
                if(G.isEmpty(productId)){
                    productId = $(this).find("input[id$='productId']").val();
                }
                if(G.isEmpty(pMap)){
                    return;
                }
                var product=pMap.get(productId);
                if(product){
                    var isPromotionsing=!G.isEmpty(product.promotionsDTOs);
                    if(isPromotionsing){
                        var pContent=generatePromotionsContentByProduct(product,"alert");
                        var content = '            <div style="white-space:normal;width:160px;word-break: normal;">'+pContent+'</div>';
                        $(this).find(".J-priceInfo div.alertBody").append(content);
                    }
                }
            });
        }
    });
}

function contains(arr,elem){
    if(G.isEmpty(arr)) return false;
    for (var i = 0;i<arr.length;i++) {
        if(arr[i]==elem){
            return true;
        }
    }
    return false;
}

function getPromotionDetailForInsales(callBack){
    APP_BCGOGO.Net.asyncGet({
        "url": "promotions.do?method=getPromotionDetail",
        data:{
            promotionStatusList:"UN_USED,UN_STARTED,USING",
            types:"MLJ,MJS,FREE_SHIPPING"
        },
        type: "POST",
        cache: false,
        "dataType": "json",
        success: function (promotionList) {
            if($.isFunction(callBack)){
                callBack(promotionList);
            }
        },
        error:function(){
            G.error("promotions.do?method=getPromotionDetail error, from promotionsUtil.js");
        }
    });
}

function getProductPromotionDetail(productIds,callBack){
    if(G.isEmpty(productIds)){
        return;
    }
    APP_BCGOGO.Net.asyncGet({
        "url": "promotions.do?method=getProductPromotionDetail",
        data:{
            productIds:productIds
        },
        type: "POST",
        cache: false,
        "dataType": "json",
        success: function (productMap) {
            if($.isFunction(callBack)){
                callBack(productMap);
            }
        },
        error:function(){
            nsDialog.jAlert("网络异常！");
        }
    });
}

function initItemPromotionsDetail(productId){
    if(G.isEmpty(productId)){
        return ;
    }
    var productIdArr=new Array();
    productIdArr.push(productId);
    APP_BCGOGO.Net.asyncAjax({
        url: "promotions.do?method=getOrderPromotionsDetail",
        data:{
            productIdArr:productIdArr.toString()
        },
        type: "POST",
        cache: false,
        dataType: "json",
        success: function (javaMap) {
            _addToPromotionsMap(javaMap);
            $(".item").each(function(){
                var itemProductId =$(this).find("[name$='supplierProductId']").val();
                if(G.isEmpty(productId)){
                    productId=$row.find(".productId").val();
                }
                if(productId==itemProductId){
                    var product=pMap.get(productId);
                    var isPromotionsing=!G.isEmpty(product.promotionsDTOs);
                    if(isPromotionsing){
                        var pTitle=generatePromotionsAlertTitle(product);
                        $(this).find(".promotions_info_td").append(pTitle);
                    }
                }

            });
        },
        error:function(error){
            nsDialog.jAlert("网络异常！");
        }
    });
}

function _addToPromotionsMap(javaMap){
    if(G.isEmpty(javaMap)){
        return;
    }
    if(G.isEmpty(pMap)){
        pMap=new Map();
    }
    for(var key in javaMap){
        if(!G.isEmpty(pMap.get(key))){
            pMap.remove(key);
        }
        pMap.put(key,javaMap[key]);
    }
    return pMap;
}

//过滤出使用中
function filterUsingPromotion(product,from){
    var promotionsList=product.promotionsDTOs;
    if(G.isEmpty(promotionsList)){
        return product;
    }
    var promotionsArr=new Array();
    for(var i=0;i<promotionsList.length;i++){
        var promotions=promotionsList[i];
        if(from=="search"&&promotions.status!="USING"){   //只过滤关联店铺的
            continue;
        }
        promotionsArr.push(promotions);
    }
    product["promotionsDTOs"]=promotionsArr;
    return product;
}

function generatePromotionsAlertTitle(product,from,bindClassName){
    product=filterUsingPromotion(product,from);
    var promotionsList=product.promotionsDTOs;
    if(arrayUtil.isEmpty(promotionsList)){
        return "";
    }
    var pTitle=generatePromotionsTitle(promotionsList);
    var pContent=generatePromotionsContentByProduct(product,"alert");
    var promotionsType=generatePromotionsTypeByProduct(product);
    var promotionsIdList=generatePromotionsIdListByProduct(product);


    var pDom = '<a class="p_icon_cheap promotionsInfo" promotionsIdList="'+promotionsIdList+'" promotionsType="'+promotionsType
        +'" pContent="'+pContent+'">'+pTitle+'</a>';
    bindPromotionsAlertInfo(bindClassName);
    return pDom;
}
/**
 * 鼠标移上去 不出现浮出框(促销详细信息)
 * @param product
 * @param from
 * @returns {string}
 */
function generateSimplePromotionsAlertTitle(product,from){
    product=filterUsingPromotion(product,from);
    var promotionsList=product.promotionsDTOs;
    if(arrayUtil.isEmpty(promotionsList)){
        return "";
    }
    var pTitle=generatePromotionsTitle(promotionsList);
    var pDom = '<a class="promotionsInfo">'+pTitle+'</a>';
    return pDom;
}


function generatePromotionsTitle(promotionsList){
    var title="";
    if(G.isEmpty(promotionsList)){
        return title;
    }
    if(promotionsList.length==1){
        title=promotionsList[0].typeStr;
    }else{
        var freeShippingTitle="";
        for(var i=0;i<promotionsList.length;i++){
            var promotions=promotionsList[i];
            var type=promotions.type;
            if(type=="MLJ"){
                title+="减+";
            }else if(type=="MJS"){
                title+="送+";
            }else if(type=="BARGAIN"){
                title+="特价+";
            }else if(type=="FREE_SHIPPING"){
                freeShippingTitle="送货+";
            }
        }
        title=freeShippingTitle+title;
        title=title.substr(0,title.length-1);
    }
    return title;
}


function getPromotions(productId,type){
    var product=pMap.get(productId);
    if(G.isEmpty(product)) return "";
    var  promotionsList=product.promotionsDTOs;
    for(var i=0;i<promotionsList.length;i++){
        var promotions=promotionsList[i];
        if(promotions.type==type){
            return promotions;
        }
    }
    return "";
}

function getBargainLimitHistoryAmount(productId, promotionsId, orderId) {
    var amount = 0;
    APP_BCGOGO.Net.syncPost({
        url:"promotions.do?method=getBargainLimitHistoryAmount",
        data:{productId: productId, promotionsId: promotionsId, orderId: orderId},
        type:"json",
        success:function(json){
            if(json){
                amount = json.data;
            }
        }
    });
    return amount;
}

function _calculateBargainPrice(product,oldPrice,amount, limitUsedAmount){
    var newPrice=oldPrice;
    var  promotionsList=product.promotionsDTOs;
    if(G.isEmpty(promotionsList)||G.isEmpty(oldPrice)){
        return newPrice;
    }
    for(var i=0;i<promotionsList.length;i++){
        var promotions=promotionsList[i];
        var type=promotions.type;
        if(promotions==null||type!="BARGAIN"){
            continue;
        }
        var promotionsProducts= product.promotionsProductDTOList;
        if(G.isEmpty(promotionsProducts)){
            return newPrice;
        }
        var promotionsProduct;
        for(var j in promotionsProducts){
            if(promotionsProducts[j].promotionsType == 'BARGAIN'){
                promotionsProduct = promotionsProducts[j];
            }
        }
        if(G.isEmpty(promotionsProduct)){
            continue;
        }
        var bargainType=promotionsProduct.bargainType;
        var discountAmount=G.rounding(promotionsProduct.discountAmount);

        if(!G.isEmpty(amount) && !G.isEmpty(limitUsedAmount) && promotionsProduct.limitFlag){
            if(promotionsProduct.limitAmount<amount){
                return newPrice;
            }else if((parseFloat(limitUsedAmount) + amount) > promotionsProduct.limitAmount ){
                return newPrice;
            }
        }

        if(bargainType=="BARGAIN"){
            if(discountAmount<=0||oldPrice<discountAmount){         //特价的商品不能比原价还贵
                return newPrice;
            }
            newPrice=discountAmount;
        }else if(bargainType=="DISCOUNT"){
            if(discountAmount<=0||discountAmount>=10){
                return newPrice;
            }
            newPrice=G.rounding(oldPrice *discountAmount/10);
        }
    }
    return newPrice;
}

function _getRuleMJSs(limiter,rules,total,amount){
    var ruleMJS="";
    if(G.isEmpty(rules)){
        return ruleMJS;
    }
    for(var i=0;i<rules.length;i++){
        var rule=rules[i];
        var minAmount=G.rounding(rule.minAmount);
        //over money
        if(limiter=="OVER_MONEY"&&(total-minAmount>-0.001)){
            ruleMJS=rule.promotionsRuleMJSDTOs;
            break;
        }else if(limiter=="OVER_AMOUNT"&&(amount-minAmount>-0.001)){
            ruleMJS=rule.promotionsRuleMJSDTOs;
            break;
        }
    }
    return ruleMJS;
}

function _satisfyFreeShipping(limiter,rules,total,amount){
    total=G.rounding(total);
    amount=G.rounding(amount);
    if(G.isEmpty(rules)){
        return false;
    }
    for(var i=0;i<rules.length;i++){
        var rule=rules[i];
        var minAmount=G.rounding(rule.minAmount);
        //over money
        if(limiter=="OVER_MONEY"&&(total-minAmount>-0.001)){
            return true;
        }
        //over amount
        if(limiter=="OVER_AMOUNT"&&(amount-minAmount>-0.001)){
            return true;
        }
    }
    return false;
}

function _calculateMLJPrice(limiter,rules,total,amount){
    total=G.rounding(total);
    amount=G.rounding(amount);
    var newPrice=total;
    if(G.isEmpty(rules)){
        return newPrice;
    }
    for(var i=0;i<rules.length;i++){
        var rule=rules[i];
        var minAmount=G.rounding(rule.minAmount);
        var discountAmount=G.rounding(rule.discountAmount);
        var ruleType=rule.promotionsRuleType;
        //over money
        if(limiter=="OVER_MONEY"&&(total-minAmount>-0.001)){
            if(ruleType=="DISCOUNT_FOR_OVER_MONEY"&&total>0){
                newPrice=G.rounding(total * discountAmount/ 10);
                break;                 //满足最高 level 要 break。防止重复计算
            }else if(ruleType=="REDUCE_FOR_OVER_MONEY"&&total>0){
                newPrice=G.rounding(total-discountAmount);
                break;
            }
        }
        //over amount
        if(limiter=="OVER_AMOUNT"&&(amount-minAmount>-0.001)){
            if(ruleType=="DISCOUNT_FOR_OVER_AMOUNT"&&amount>0){
                newPrice=G.rounding(total * discountAmount / 10);
                break;
            }else if(ruleType=="REDUCE_FOR_OVER_AMOUNT"&&amount>0){
                newPrice=G.rounding(total-discountAmount);
                break;
            }
        }
    }
    return newPrice;
}

function resetOrderPriceInfo($row,newPrice){
    var oldPrice=G.rounding($row.find(".J-oldPrice").text());
    var amount=G.rounding($row.find(".J-ModifyAmount").val());
    $row.find(".J-newPrice").text(newPrice);
    var savedPrice = G.rounding(oldPrice -newPrice);
    $row.find(".J-SaveValue").text("省"+savedPrice);
    if (savedPrice> 0.001) {
        $row.find(".J-oldPrice").addClass("gray");
        $row.find(".J-newPrice").show();
        $row.find(".J-SaveInfo").show();
    }else {
        $row.find(".J-oldPrice").removeClass("gray");
        $row.find(".J-newPrice").hide();
        $row.find(".J-SaveInfo").hide();
    }
    $row.find(".J-ItemTotal").text(G.rounding(newPrice*amount,2));
}

function resetPromotionMap($table){
    $table.data("mljMap", '');
    $table.data("mjsMap", '');
}

function getPurchaseOnlineTableByRow($row){
    return $row.parents("table.J-purchaseOnlineTable");
}

//例如:key=(type+promotionsId); value=total
function getPromotionMapFromTable($table, mapType){
    if(mapType == 'mljMap'){
        var mljMap = $table.data("mljMap");
        if(G.isEmpty(mljMap)){
            mljMap = new Map();
            $table.data("mljMap", mljMap);
            return mljMap;
        }else{
            return mljMap;
        }
    }else if(mapType == 'mjsMap'){
        var mjsMap = $table.data("mjsMap");
        if(G.isEmpty(mjsMap)){
            mjsMap = new Map();
            $table.data("mjsMap", mjsMap);
            return mjsMap;
        }else{
            return mjsMap;
        }
    }
}

function setPromotionMapToTable($table, mapType, map){
    if(mapType == 'mljMap'){
        $table.data("mljMap", map);
    }else if(mapType == 'mjsMap'){
        $table.data("mjsMap", map);
    }
}

function calculateOrderTotal($row) {
    var oldPrice =G.rounding($row.find(".J-oldPrice").text());
    var rPrice=oldPrice;
    if(G.isEmpty(oldPrice)){
        return false;
    }
    var $table = getPurchaseOnlineTableByRow($row);
    var productId=$row.find("[name$='supplierProductId']").val();
    if(G.isEmpty(productId)){
        productId=$row.find(".productId").val();
    }
    if(G.isEmpty(pMap)||G.isEmpty(productId)){
        return false;
    }
    $row.find(".bargainSaveValue").val("0");
    if(hasPromotionsType($row,"BARGAIN")){
        var product=pMap.get(productId);
        var amount =G.rounding($row.find(".J-ModifyAmount").val());

        rPrice=_calculateBargainPrice(product,oldPrice,amount, $row.find(".promotionsInfo").attr("usedbargainlimitamount"));
        if(oldPrice == rPrice){
            $row.find(".J-oldPrice").css("color", "#000000").show();
        }
        $row.find(".bargainSaveValue").val(G.rounding(oldPrice-rPrice));
        resetOrderPriceInfo($row,rPrice);
    }else if(hasPromotionsType($row,"MLJ")){
        var promotions=getPromotions(productId,"MLJ");
        var originRules=promotions.promotionsRuleDTOList;

        var rules = [];
        for(var i = originRules.length- 1, j=0; i>=0; i--, j++){
            rules[j] = originRules[i];
        }
        var limiter=promotions.promotionsLimiter;

        if(limiter=="OVER_MONEY"){
            var mljItems=getTrByPromotionsId(promotions.idStr, $row);
            if(G.isEmpty(mljItems)){
                return;
            }
            var mljTotal=0;
            for(var i=0;i<mljItems.length;i++){
                var $mlj=mljItems[i];
                var productId=$mlj.find("[name$='supplierProductId']").val();
                if(G.isEmpty(productId)){
                    productId=$mlj.find(".productId").val();
                }
                var product= pMap.get(productId);
                var amount=G.rounding($mlj.find(".J-ModifyAmount").val());
                if(!G.isEmpty(product))
                    mljTotal+=G.rounding(product.inSalesPrice)*amount;
            }
            var newPrice=G.rounding(_calculateMLJPrice(limiter,rules,mljTotal,0));
            var pVal=G.rounding(mljTotal-newPrice);
            var mljMap = getPromotionMapFromTable($table, "mljMap");
            mljMap.put(stringUtil.generateKey("MLJ",promotions.idStr),pVal);
            setPromotionMapToTable($row, 'mljMap', mljMap);
        }else if(limiter=="OVER_AMOUNT"){
            var mljItems=getTrByPromotionsId(promotions.idStr, $row);
            if(G.isEmpty(mljItems)){
                return;
            }
            var pValTotal=0;
            for(var i=0;i<mljItems.length;i++){
                var $mlj=mljItems[i];
                var amount=$mlj.find(".J-ModifyAmount").val();
                var oldPriceTotal=G.rounding($mlj.find(".J-oldPrice").text()) * amount;
                var newPriceTotal = _calculateMLJPrice(limiter,rules,oldPriceTotal,amount);
                var pVal=G.rounding(oldPriceTotal-newPriceTotal);
                pValTotal+=pVal;
            }
            var mljMap = getPromotionMapFromTable($table, "mljMap");
            if(pValTotal>0){
                mljMap.put(stringUtil.generateKey("MLJ",promotions.idStr),pValTotal);
            }else{
                if(!G.isEmpty(mljMap)){
                    mljMap.remove(stringUtil.generateKey("MLJ",promotions.idStr));
                }
            }
            setPromotionMapToTable($row, 'mljMap', mljMap);
        }
    }else if(hasPromotionsType($row,"MJS")){
        var ruleMJSs="";
        var promotions=getPromotions(productId,"MJS");
        var originRules=promotions.promotionsRuleDTOList;
        var rules = [];
        for(var i = originRules.length- 1, j=0; i>=0; i--, j++){
            rules[j] = originRules[i];
        }
        var limiter=promotions.promotionsLimiter;
        var product= pMap.get(productId);

        if(limiter=="OVER_MONEY"){
            var mjsItems=getTrByPromotionsId(promotions.idStr, $row);
            if(G.isEmpty(mjsItems)){
                return false;
            }
            var mjsTotal=0;
            for(var i=0;i<mjsItems.length;i++){
                var $mjs=mjsItems[i];
                var productId=$mjs.find("[name$='supplierProductId']").val();
                if(G.isEmpty(productId)){
                    productId=$row.find(".productId").val();
                }
                var product= pMap.get(productId);
                var amount=G.rounding($mjs.find(".J-ModifyAmount").val());
                mjsTotal+=G.rounding(product.inSalesPrice)*amount;
            }
            ruleMJSs=_getRuleMJSs(limiter,rules,mjsTotal,0);
            var mjsMap = getPromotionMapFromTable($table, "mjsMap");
            mjsMap.put(stringUtil.generateKey("MJS",promotions.idStr),ruleMJSs);
            setPromotionMapToTable($row, 'mjsMap', mjsMap);
        }else if(limiter=="OVER_AMOUNT"){
            var mjsItems=getTrByPromotionsId(promotions.idStr, $row);
            if(G.isEmpty(mjsItems)){
                return false;
            }
            var mjsTotal=0;
            for(var i=0;i<mjsItems.length;i++){
                var $mjs=mjsItems[i];
                var amount=$row.find(".J-ModifyAmount").val();
                ruleMJSs=_getRuleMJSs(limiter,rules,0,amount);
                var mjsMap = getPromotionMapFromTable($table, "mjsMap");
                mjsMap.put(stringUtil.generateKey("MJS",productId),ruleMJSs);
                setPromotionMapToTable($row, 'mjsMap', mjsMap);
            }
        }

    }
    //送货上门要单独计算,有一条符合即符合
    var satisfyOneFreeShippingFlag = false;
    $table.find(".item").each(function(){
        var $itemRow = $(this);
        var productId=$itemRow.find("[name$='supplierProductId']").val();
        if(hasPromotionsType($itemRow,"FREE_SHIPPING")){
            var satisfyFreeShippingFlag=false;
            var promotions=getPromotions(productId,"FREE_SHIPPING");
            var rules=promotions.promotionsRuleDTOList;
            var limiter=promotions.promotionsLimiter;
            var product= pMap.get(productId);
            if(limiter=="OVER_MONEY"){
                var freeShippingItems=getTrByPromotionsId(promotions.idStr, $itemRow);
                var freeShippingTotal=0;
                for(var i=0;i<freeShippingItems.length;i++){
                    var $freeShipping=freeShippingItems[i];
                    var productId=$freeShipping.find("[name$='supplierProductId']").val();
                    if(G.isEmpty(productId)){
                        productId=$itemRow.find(".productId").val();
                    }
                    var product= pMap.get(productId);
                    var amount=G.rounding($freeShipping.find(".J-ModifyAmount").val());
                    freeShippingTotal+=G.rounding(product.inSalesPrice)*amount;
                }
                satisfyFreeShippingFlag=_satisfyFreeShipping(limiter,rules,freeShippingTotal,0);
            }else if(limiter=="OVER_AMOUNT"){
                var amount=$itemRow.find(".J-ModifyAmount").val();
                satisfyFreeShippingFlag=_satisfyFreeShipping(limiter,rules, 0,amount);
            }else{
                satisfyFreeShippingFlag=true;//无条件限制
            }
            if(satisfyFreeShippingFlag){
                satisfyOneFreeShippingFlag = true;
            }
        }
        if(satisfyOneFreeShippingFlag){
            $table.find(".freeShippingDiv").show();
        }else{
            $table.find(".freeShippingDiv").hide();
        }
    });

    generatePromotionsStat(getPurchaseOnlineTableByRow($row));
    return rPrice;
}

function generatePromotionsStat($table){
    var mjsMap = getPromotionMapFromTable($table, "mjsMap");
    var mljMap = getPromotionMapFromTable($table, "mljMap");
    //mjs
    if(!G.isEmpty(mjsMap)){
        var ruleMJSArr=new Array();
        var keyList=mjsMap.keySet();
        for(var i=0;i<keyList.length;i++ ){
            var mjsRules=mjsMap.get(keyList[i]);
            if(!G.isEmpty(mjsRules)){
                for(var j=0;j<mjsRules.length;j++){
                    ruleMJSArr.push(mjsRules[j]);
                }
            }
        }
        if(ruleMJSArr.length>0){
            var gift="";
            for(var i=0;i<ruleMJSArr.length;i++){
                var ruleMJS=ruleMJSArr[i];
                var giftType=ruleMJS.giftType;
                var giftName=ruleMJS.giftName;
                var amount=ruleMJS.amount;
                if(giftType=="GIFT"){
                    gift+="送"+giftName+" "+amount+" 件;"
                }else if(giftType=="DEPOSIT"){
                    gift+="送预收款"+amount+"元;"
                }
            }
            if(G.isEmpty(gift)){
                $table.find(".mjsDiv").hide();
            }else{
                $table.find("#mjsSpan").text(gift);
                $table.find(".mjsDiv").show();
            }
        }else{
            $table.find(".mjsDiv").hide();
        }
    }else{
        $table.find(".mjsDiv").hide();
    }
    //mlj
    var mljTotal=0;
    if(!G.isEmpty(mljMap)){
        var keyList=mljMap.keySet();
        for(var i=0;i<keyList.length;i++ ){
            mljTotal+=G.rounding(mljMap.get(keyList[i]));
        }
        if(mljTotal>0){
            $table.find("#mljSpan").text(G.rounding(mljTotal));
            $table.find(".mljDiv").show();
        }else{
            $table.find(".mljDiv").hide();
        }
    }else{
        $table.find(".mljDiv").hide();
    }
    //bargain
    var bargainTotal=0;
    $table.find(".item").each(function(){
        if(!$(this).find("input[id$='customPriceFlag']")[0] || $(this).find("input[id$='customPriceFlag']").val() == 'false'){
            bargainTotal += G.rounding($(this).find(".bargainSaveValue").val() * $(this).find(".J-ModifyAmount").val(), 2);
        }
    });
    $table.find("#bargainSpan").text(G.rounding(bargainTotal));
    if(bargainTotal>0){
        $table.find(".bargainDiv").show();
    }else{
        $table.find(".bargainDiv").hide();
    }
    //特价和满立减的总和
    var promotionsTotal=G.add(mljTotal,bargainTotal);
    $table.find("#promotionsTotalSpan").text(promotionsTotal);
    if(promotionsTotal>0){
        $table.find("#promotionsTotalDiv").show();
    }else{
        $table.find("#promotionsTotalDiv").hide();
    }
    setPromotionMapToTable($table, 'mljMap', mljMap);
    setPromotionMapToTable($table, 'mjsMap', mjsMap);
}


//针对单条促销的 主要指 bargain
function  isBargain(productId){
    var product=pMap.get(productId);
    if(G.isEmpty(product)) return false;
    var promotionsList=product.promotionsDTOs;
    if(G.isEmpty(promotionsList)){
        return false;
    }
    for(var i=0;i<promotionsList.length;i++){
        var promotions= promotionsList[i];
        if(promotions.type=="BARGAIN"){
            return true;
        }
    }
    return false;
}

function hasPromotionsByPromotionsId($row,promotionsId){
    var flag=false;
    if(G.isEmpty($row)||G.isEmpty(promotionsId)){
        return flag;
    }
    var $promotionsInfo=$row.find(".promotionsInfo");
    if(!arrayUtil.isEmpty($promotionsInfo)) {
        var idArr=$promotionsInfo.attr("promotionsIdList").split(",");
        flag= contains(idArr,promotionsId);
    }
    return flag;
}

function hasPromotionsType($row,type){
    var flag=false;
    if(G.isEmpty($row)||G.isEmpty(type)||$row.find("input[id$='customPriceFlag']").val() == 'true'){
        return flag;
    }
    var $promotionsInfo=$row.find(".promotionsInfo");
    if(!arrayUtil.isEmpty($promotionsInfo)) {
        var typeArr=$promotionsInfo.attr("promotionsType").split(",");
        flag= contains(typeArr,type);
    }
    return flag;
}

function getTrByPromotionsId(promotionsId, $row){
    var trArr=new Array();
    if(G.isEmpty(promotionsId)) return trArr;
    getPurchaseOnlineTableByRow($row).find(".item").each(function(){
        if(hasPromotions($(this)) && hasPromotionsByPromotionsId($(this),promotionsId)){
            trArr.push($(this));
        }
    });
    return trArr;
}

function hasPromotions($row){
    var flag=false;
    if(!G.isEmpty($row)&&!arrayUtil.isEmpty($row.find(".promotionsInfo"))) {
        if($row.find("input[id$='customPriceFlag']")[0] && $row.find("input[id$='customPriceFlag']").val()=='true'){
            return false;
        }
        flag=true;
    }
    return flag;
}

//促销的开始时间从当前时间的一个小时之后
function getTodayOfPromotions(){
    var date=new Date();
    var year = date.getFullYear(),
        month = date.getMonth() + 1,
        day = date.getDate(),
        hour = date.getHours()-1,
        minute = date.getMinutes();
    if(hour>23){
        hour=0;
    }else{
        hour++;
    }
    return year+"-"+month+"-"+day+" "+hour+":"+minute;
}

function getPromotionManagerAlert(target){
    getServiceTime(function(timeMap){
        if(!G.isEmpty(timeMap)){
            $("#serviceStartTime").val(G.normalize(timeMap.currentTime));
            $("[name='startTimeStr']").val(G.normalize(timeMap.currentTime));
            $(".date_select_month").click();
        }
    });
//     var pageSource=$(target).attr("pageSource");
//    $("#addPromotions_pageSource").val(pageSource);
    $("#promotionManagerAlert").dialog({
        title:"创建促销",
        resizable: false,
        modal: true,
        draggable:true,
        height: 360,
        width: 645,
        open: function () {
            return true;
        }
    });
}

function _generateAddProductForInSales(operateType){
    var pmsProductIdStr="";
    if(operateType=="batch"){
        $(".itemChk:checked").each(function(index){
            var $tr=$(this).closest("tr");
            var productId= $tr.find("[field='productId']").val();
            pmsProductIdStr+='<input type="hidden" name="promotionsProductDTOList['+index+'].productLocalInfoId" value="'+productId+'"/>';
        });
    }else{
        var productId=$("#productId").val();
        if(!G.isEmpty(productId)){
            pmsProductIdStr+='<input type="hidden" name="promotionsProductDTOList[0].productLocalInfoId" value="'+productId+'"/>';
        }
    }
    return pmsProductIdStr;
}

function getMLJAlert(operateType){
    $(".mlj-addPromotionsProduct").html(_generateAddProductForInSales(operateType));
    $("#manageMLJAlert").dialog({
        title:"创建促销",
        resizable: false,
        modal: true,
        draggable:true,
        resizable:true,
        height: 550,
        width: 800,
        open: function () {
            $("#promotionManagerAlert").dialog("close");
            //清空dialog输入框内容
            $(".table_mlj [name='name']").val("");
            $(".table_mlj [name='description']").val("");
            $(".last-step-btn").attr("lastStepType",G.normalize(operateType));
            $(".table_mlj .date_select_month").click();
            $(".table_mlj .promotions_rule_default").click();
            $(".table_mlj .icon_close").click();
            return true;
        }
    });
}

function getMJSAlert(operateType){
    $(".mjs-addPromotionsProduct").html(_generateAddProductForInSales(operateType));
    $("#manageMJSAlert").dialog({
        title:"创建促销",
        resizable: false,
        modal: true,
        draggable:true,
        resizable:true,
        height: 500,
        width: 750,
        open: function () {
            $("#promotionManagerAlert").dialog("close");
            //清空dialog输入框内容
            $(".table_mjs [name='name']").val("");
            $(".table_mjs [name='description']").val("");
            $(".last-step-btn").attr("lastStepType",G.normalize(operateType));
            $(".table_mjs .date_select_month").click();
            $(".table_mjs .icon_close").click();
            $(".J-initialCss").placeHolder("reset");
            return true;
        }
    });
}

function getBargainAlert(operateType){
    $(".bargain-addPromotionsProduct").html(_generateAddProductForInSales(operateType));
    if(operateType=="batch"){
        $(".bargain-addPromotionsProductTable tr").remove();
        var trStr='<col width="120"><col width="30"><col width="140">';
        trStr+='<tr class="titleBg"><td style="padding-left: 10px">商品信息</td><td>原售价</td><td>特价设置</td></tr>';
        $(".itemChk:checked").each(function(index){
            var $tr=$(this).closest("tr");
            var inventoryAveragePrice=$tr.find(".j_inventoryAveragePrice").val();
            var productId= $tr.find("[field='productId']").val();
            var productInfo= $tr.find("[field='productInfo']").text();
            var inventoryNum=G.rounding($tr.find("[field='inventoryNum']").text());
            var inSalesPrice=G.rounding($tr.find("[field='inSalesPrice']").val());
            trStr+='<tr class="product-item titBody_Bg">';
//                trStr+='<input type="hidden" field="productId" value="'+productId+'"/>';
            trStr+='<input type="hidden" field="productId" name="promotionsProductDTOList['+index+'].productLocalInfoId" value="'+productId+'"/>';
            trStr+='<td style="width: 270px">'+productInfo+'<br/>成本价：<span class="arialFont">&yen;</span>'+inventoryAveragePrice+'&nbsp;库存量：'+inventoryNum+'</td>'
            trStr+='<td><span class="arialFont">&yen;</span><span class="inSalesPrice">'+inSalesPrice+'</span></td>';
            trStr+='<td class="bargainTD"><input name="promotionsProductDTOList['+index+'].discountAmount" class="price-input txt discount_amount"  type="text" style="width:50px;" placeholder="现价">';
            trStr+='<input name="promotionsProductDTOList['+index+'].bargainType" type="hidden" class="bargainType"  value="BARGAIN"/>';
            trStr+='<select class="bargainTypeSelector" style="margin-left:5px"><option value="BARGAIN">金额</option><option value="DISCOUNT">折扣</option></select>';
            trStr+='<label style="margin-left:5px" class="rad"><input type="checkbox" class="limitFlagChk">每位客户限购</label>';
            trStr+='<input name="promotionsProductDTOList['+index+'].limitAmount" disabled="true" class="limit_amount price-input txt" type="text" style="width:50px;" placeholder="数量">件</td>';
            trStr+='</tr>';
            trStr+='<tr class="titBottom_Bg"><td colspan="4"></td></tr>';
        });
        $(".bargain-addPromotionsProductTable").append(trStr);
    }else{
        $(".bargain-addPromotionsProductTable .bargainTD").remove();
        var inSalesPrice=$('[name="inSalesPrice"]').val();
        var trStr='<div style="color: #242424;padding-left: 45px" class="bargainTD">'
//        if(!G.isEmpty($("#productId").val())){
        trStr+='原售价&nbsp;<span class="arialFont">&yen;</span><span class="inSalesPrice">'+inSalesPrice+'</span><br/>';
//        }
        trStr+='<input name="promotionsProductDTOList[0].discountAmount" class="price-input txt discount_amount"  type="text" style="width:50px;" placeholder="现价">';
        trStr+='<input name="promotionsProductDTOList[0].bargainType" type="hidden" class="bargainType"  value="BARGAIN"/>';
        trStr+='<select class="bargainTypeSelector" style="margin-left:5px"><option value="BARGAIN">金额</option><option value="DISCOUNT">折扣</option></select>';
        trStr+='<label style="margin-left:5px" class="rad"><input type="checkbox" class="limitFlagChk">每位客户限购</label>';
        trStr+='<input name="promotionsProductDTOList[0].limitAmount" disabled="true" class="limit_amount price-input txt" type="text" style="width:50px;" placeholder="数量">件 ';
        trStr+='</div>';
        $(".bargain-addPromotionsProductTable").append(trStr);
    }
    $("#manageBargainAlert").dialog({
        title:"创建促销",
        resizable: false,
        modal: true,
        draggable:true,
        resizable:true,
        height: 500,
        width: 760,
        open: function () {
            $("#promotionManagerAlert").dialog("close");
            //清空dialog输入框内容
            $(".table_bargain [name='name']").val("");
            $(".table_bargain [name='description']").val("");
            $(".last-step-btn").attr("lastStepType",G.normalize(operateType));
            $(".table_bargain .date_select_month").click();

            return true;
        }
    });
}

function getFreeShippingAlert(operateType){
    $(".freeShipping-addPromotionsProduct").html(_generateAddProductForInSales(operateType));
    $("#manageFreeShippingAlert").dialog({
        title:"创建促销",
        resizable: false,
        modal: true,
        draggable:true,
        resizable:true,
        height: 600,
        width: 700,
        open: function () {
            $("#promotionManagerAlert").dialog("close");
            //清空dialog输入框内容
            $(".table_free_shipping [name='name']").val("");
            $(".table_free_shipping [name='description']").val("");
            $(".last-step-btn").attr("lastStepType",G.normalize(operateType));
            $(".table_free_shipping .date_select_month").click();
            $(".table_free_shipping .minAmount").val("");
            $(".table_free_shipping .promotionsLimiterSelector").val();
            $(".table_free_shipping  [name='promotionsRuleRio']").click();
            return true;
        }
    });
}

function getFreeShippingDivAlert(operateType) {
    $("#promotionManagerAlert").dialog("close");
    Mask.Login();
    $("#mask").css("z-index",1000);
    $(".freeShipping-addPromotionsProduct").html(_generateAddProductForInSales(operateType));
    var $manageFreeShippingAlertDiv = $("#manageFreeShippingAlertDiv");
    var leftPosition = $(document).width() / 2 - $manageFreeShippingAlertDiv.width() / 2;
    $manageFreeShippingAlertDiv.css("left",leftPosition).show();
    //清空dialog输入框内容
    $(".table_free_shipping [name='name']").val("");
    $(".table_free_shipping [name='description']").val("");
    $(".last-step-btn").attr("lastStepType",G.normalize(operateType));
    $(".table_free_shipping .date_select_month").click();
    $(".table_free_shipping .minAmount").val("");
    $(".table_free_shipping #areaBoardDiv input").attr("checked",false);
    $(".table_free_shipping #postType").val("POST");
    $(".table_free_shipping .postSelector").val("POST");
    $(".table_free_shipping .postSelector").change();
    $(".table_free_shipping .promotionsLimiterSelector").val("OVER_MONEY");
    $(".table_free_shipping .promotionsLimiterSelector").change();
    $(".table_free_shipping [name='promotionsRuleRio'][value='YES']").click();
    //地区选择面板
    $(".areaList").children().remove();
    $(".j_owned_province").attr("checked", true);   //默认本省选中
    promotionsAreaClick($(".j_owned_province")[0]);
}




