<%--
  Created by IntelliJ IDEA.
  User: ndong
  Date: 13-8-26
  Time: 上午9:35
  To change this template use File | Settings | File Templates.
--%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
<title>上架管理</title>
<link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
<%@include file="/WEB-INF/views/header_script.jsp" %>

<script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.js"></script>
<script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/page/autoaccessoryonline/promotionsUtil<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/page/autoaccessoryonline/promotions<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/page/autoaccessoryonline/goodsInOffSales<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript">
defaultStorage.setItem(storageKey.MenuUid, "AUTO_ACCESSORY_ONLINE_GOODS_IN_OFF_SALES_MANAGE");

$().ready(function(){
    //促销商品查询 全选所有促销类型
    $("#allChk").click(function(){
        $(".promotionsTypes,#allChk").attr("checked",$(this).attr("checked"));
        $("#promotionsType").val(generatePromotionTypes());
        });


    $(".promotionsTypes").click(function(){
        var promotionsType=new Array();
        $(".promotionsTypes:checked").each(function(){
            promotionsType.push($(this).attr("promotionsTypes"))
        });
        if($(".promotionsTypes:checked").size()==$(".promotionsTypes").size()){
            $(".promotionsTypes,#allChk").attr("checked",true);
        }else{
            $("#allChk").attr("checked",false);
        }
        $("#promotionsType").val(promotionsType.toString());
    });

    $("#resetBtn").click(function(){
        clearSearchCondition();
        $("#searchProductBtn").click();
    });


    $('[name="promotion_time"]').live("click",function(){
        var $inSalesTimeSearch=$(this).closest(".inSalesTime-search");
        var $target=$(this);
        $('.timeInputSpan').hide();
        if($target.hasClass("select-date-define")){
            $('.timeInputSpan').show();
            $('.timeInputSpan input').show();

        }else{
            $('[name^="endTimeStr"]').val(dateUtil.getEndOfCurrentTime());
            var startTimeStr=dateUtil.getBeforeDays($(this).attr("param"),$("[name='endTimeStr']").val());
            startTimeStr+=" 00:00";
            $('[name^="startTimeStr"]').val(startTimeStr);
        }
    });

    $('[name="guaranteePeriod"]').keyup(function(){
        $(this).val(G.rounding($(this).val(),0));
    });

    $('[field="timeInput"]').bind("click", function(){
        $(this).blur();
    }).datetimepicker({
                "numberOfMonths": 1,
                "showButtonPanel": true,
                "changeYear": true,
                "changeMonth": true,
                "yearSuffix": "",
                "yearRange":"c-100:c+100",
                "onClose": function(dateText, inst) {
                    if($(this).hasClass("startTimeStr")){
                        $(".date_select:checked").click();
                    }
                    var $form=$(this).closest("form");
                    var startTimeStr=$form.find("[name='startTimeStr']").val();
                    var endTimeStr=$form.find("[name='endTimeStr']").val();
                    if(!G.isEmpty(startTimeStr)&&!G.isEmpty(endTimeStr)&& GLOBAL.Util.getExactDate(startTimeStr) >= GLOBAL.Util.getExactDate(endTimeStr)) {
                        nsDialog.jAlert("开始时间应小于结束时间，请重新选择！");
                        $(this).val(inst.lastVal);
                    }
                },
                "onSelect": function(dateText, inst) {
                    if(inst.lastVal == dateText) {
                        return;
                    }
                    $(this).val(dateText);
                }
            });

    $('[field^="editor-icon"]').live("click",function(){
        var $show=$(this).closest("td").find('[type="show"]');
        var $edit=$(this).closest("td").find('[type="edit"]');
        $show.hide();
        $edit.show();
        if($(this).attr("field")=="editor-icon-inSalesAmount"){    //设置上架量
            var editVal=$show.find('[field="inSalesAmount"]').text();
            if(editVal=="有货"){
                $edit.find('[field="radio-inSalesAmount-exist"]').attr("checked",true);
            }else{
                $edit.find('[field="radio-inSalesAmount-amount"]').attr("checked",true);
                $edit.find('[field="inSalesAmount"]').val(G.rounding(editVal));
            }
        }
    });

    $("#inSalingGoodsTable .cancelBtn").live("click",function(){
        $(this).closest("td").find('[type="show"]').show();
        $(this).closest("td").find('[type="edit"]').hide();
    });

    $("#inSalingGoodsTable .saveBtn-inSalesAmount").live("click",function(){
        var $show=$(this).closest("td").find('[type="show"]');
        var $edit=$(this).closest("td").find('[type="edit"]');
        $show.show();
        $edit.hide();
        var data = {};
        var inSalesAmount;
        if($edit.find('[field="radio-inSalesAmount-amount"]').attr("checked")){
            inSalesAmount=G.rounding($edit.find('[field="inSalesAmount"]').val());
        }else{ //显示有货
            inSalesAmount=-1;
        }
        data["productDTOs[0].productLocalInfoId"]=$(this).closest("tr").find('[field="productId"]').val();
        data["productDTOs[0].inSalesAmount"]=inSalesAmount;
        APP_BCGOGO.Net.asyncAjax({
            url: "product.do?method=updateMultipleInSalesAmount",
            type: "POST",
            cache: false,
            data:data,
            dataType: "json",
            success: function (json) {
                if(!json.success){
                    nsDialog.jAlert("批量设置本页上架量失败!");
                    return;
                }
                if($edit.find('[field="radio-inSalesAmount-amount"]').attr("checked")){
                    $show.find('[field="inSalesAmount"]').text(G.rounding($edit.find('[field="inSalesAmount"]').val()));
                    $show.find('[field="productUnit"]').show();
                }else{ //显示有货
                    $show.find('[field="inSalesAmount"]').text("有货");
                    $show.find('[field="productUnit"]').hide();
                }
            },
            error:function(){
                nsDialog.jAlert("网络异常！");
            }
        });
    });

    $("#inSalingGoodsTable .saveBtn-inSalesPrice").live("click",function(){
        var $show=$(this).closest("td").find('[type="show"]');
        var $edit=$(this).closest("td").find('[type="edit"]');
        $show.show();
        $edit.hide();
        var data = {};
        data["productDTOs[0].productLocalInfoId"]=$(this).closest("tr").find('[field="productId"]').val();
        data["productDTOs[0].inSalesPrice"]=G.rounding($edit.find('[field="inSalesPrice"]').val());
        APP_BCGOGO.Net.asyncAjax({
            url: "product.do?method=updateMultipleInSalesPrice",
            type: "POST",
            cache: false,
            data:data,
            dataType: "json",
            success: function (json) {
                if(json.success){
                    $show.find('[field="inSalesPrice"]').text(G.rounding($edit.find('[field="inSalesPrice"]').val()));
                }else{
                    nsDialog.jAlert("批量设置本页上架售价失败!");
                }
            },
            error:function(){
                nsDialog.jAlert("网络异常！");
            }
        });
    });

    $("#inSalingGoodsTable .saveBtn-guaranteePeriod").live("click",function(){
        var $show=$(this).closest("td").find('[type="show"]');
        var $edit=$(this).closest("td").find('[type="edit"]');
        $show.show();
        $edit.hide();
        var data = {};
        data["productDTOs[0].productLocalInfoId"]=$(this).closest("tr").find('[field="productId"]').val();
        data["productDTOs[0].guaranteePeriod"]=G.rounding($edit.find('[field="guaranteePeriod"]').val());
        APP_BCGOGO.Net.asyncAjax({
            url: "product.do?method=updateMultipleGuaranteePeriod",
            type: "POST",
            cache: false,
            data:data,
            dataType: "json",
            success: function (json) {
                if(json.success){
                    $show.find('[field="guaranteePeriod"]').text(G.rounding($edit.find('[field="guaranteePeriod"]').val())+"个月");
                }else{
                    nsDialog.jAlert("批量设置本页质保时间失败!");
                }
            },
            error:function(){
                nsDialog.jAlert("网络异常！");
            }
        });
    });

    $('.promotionsDetailBtn').live("click",function(){
        var $tr=$(this).closest("tr");
        var $target=$(this);
        var productId=$tr.find('[field="productId"]').val();
        $("#promotionsDetail"+productId).remove();
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
            var pTr='<tr id="promotionsDetail'+productId+'" class="promotionsDetail"><td colspan="9"><div class="divListInfo div_promotionInfo">';
            pTr+='<table cellpadding="0" style="float:left" cellspacing="0" class="tab_document"><col width="110"><col width="220"><col width="250"><col width="100"><col width="80"><col width="45">';
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
            $tr.after(pTr);
        }

    });

    $('[pageSource="inSalingGoodsList"] .savePromotionsBtn-InSales').click(function(){
        var _me=this;
        if($(_me).attr("disabled")){
            return;
        }
        $(_me).attr("disabled", true);
        $(".J-initialCss").placeHolder("clear");
        $(this).closest("#promotionsForm").ajaxSubmit({
            url:"promotions.do?method=validateSavePromotionsForInSales",
            dataType: "json",
            type: "POST",
            success: function(result){
                if(G.isEmpty(result)){
                    return;
                }
                if(!result.success){
                    nsDialog.jAlert(result.msg);
                    return;
                }
                var lappingMap=result.data;
                if(G.isEmpty(lappingMap)){
                    _doSavePromotionsInSalingList(_me,function(flag){
                        if(!flag){
                            return;
                        }
                        if($(_me).attr("pagetype") == 'manageFreeShipping') {
                            doHiddeDiv();
                        } else {
                    doCloseDialog(_me);
                        }
                    });
                }else{
                    $(_me).removeAttr("disabled");
                    var errorMsg='<div>对不起，';
                    for(var productId in lappingMap){
                        errorMsg+='<br/>';
                        var productName=$('.itemChk:[productId="'+productId+'"]').closest("tr").find('[field="name"]').val();
                        var promotion=lappingMap[productId];
                        errorMsg+='商品<span style="font-weight: bold;">'+ productName+'</span>在同时段已参与促销<span style="font-weight: bold;">'+ G.normalize(promotion.name)+'</span>，是否覆盖之前的促销？';
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
//                                $("#batchPromotionManagerAlert").dialog("close");
                                _doSavePromotionsInSalingList(_me,function(flag){
                                    if(!flag){
                                        return;
                                    }
                                    if($(_me).attr("pagetype") == 'manageFreeShipping') {
                                        doHiddeDiv();
                                    } else {
                                doCloseDialog(_me);
                                    }
                                    for(var productId in lappingMap){
                                        var pLapping=lappingMap[productId];
                                        var promotionsList=promotionsPruductMap[String(productId)];
                                        var pArr=new Array();
                                        if(!G.isEmpty(promotionsList)){
                                            for(var i=0;i<promotionsList.length;i++){
                                                var existPromotions=promotionsList[i];
                                                if(!G.isEmpty(existPromotions)&&existPromotions.idStr!=pLapping.idStr){
                                                    pArr.push(existPromotions);
                                                }
                                            }
                                        }
                                        promotionsPruductMap[String(productId)]=pArr;
                                    }

                                });
                            },
                            "取消":function(){
                                $(this).dialog("close");
                            }
                        }
                    });

                }

            },
            error:function(){
                nsDialog.jAlert("网络异常！");
            }
        });

        function _doSavePromotionsInSalingList(target,callBack){
            savePromotions(target,function(result,flag){
                $(target).removeAttr("disabled");
                nsDialog.jAlert("促销创建成功。",null,function(){
                    var promotions=result.data;
                    $(".itemChk:checked").each(function(){
                        var productId=$(this).attr("productId");
                        $("#promotionsDetail"+productId).remove();
                        var $td=$(this).closest("tr").find(".promotion-info");
                        var tdStr='已参与<br/><a class="blue_color promotionsDetailBtn">查看促销</a>';
                        $(this).closest("tr").find(".promotion-info").text("").append(tdStr);
                        var pArr=new Array();
                        pArr.push(promotions);
                        var existPromotionList=promotionsPruductMap[String(productId)];
                        if(!G.isEmpty(existPromotionList)){
                            for(var i=0;i<existPromotionList.length;i++){
                                pArr.push(existPromotionList[i]);
                            }
                        }
                        promotionsPruductMap[String(productId)]=pArr;
                    });
                    if($.isFunction(callBack)){
                        callBack(flag);
                    }
                    initStockStatInfo();
                })
            });
        }

    });

    $(".batchPromotionManagerBtn").live("click",function(){
        if($(".itemChk:checked").length==0){
            nsDialog.jAlert("请选择要促销的商品。");
            return;
        }
        getServiceTime(function(timeMap){
            if(!G.isEmpty(timeMap)){
                $("#serviceStartTime").val(G.normalize(timeMap.currentTime));
                $(".table_promotion [name='startTimeStr']").val(G.normalize(timeMap.currentTime));
                $(".table_promotion .date_select_month").click();
            }
        });
//        var pageSource=$(target).attr("pageSource");
//        $("#addPromotions_pageSource").val(pageSource);
        $("#batchPromotionManagerAlert").dialog({
            title:"友情提示",
            width: 300,
            height:180,
            resizable: false,
            modal: true,
            draggable:true,
            open: function () {
                return true;
            },
            buttons:{
                "确定":function(){
                    var _me=$(this);
                    var promotionSource=$("[name='promotionSource']:checked").attr("promotionSource");
                    if(promotionSource=="new"){
                        var promotionType=$("#promotionTypeSelector").val();
                        if(G.isEmpty(promotionType)){
                            nsDialog.jAlert("请选择促销类型。");
                            return;
                        }
                        $('[name="addPromotionsProductFlag"]').val(true);
                        if(promotionType=="MLJ"){
                            getMLJAlert('batch');
                        }else if(promotionType=="MJS"){
                            getMJSAlert('batch');
                        }else if(promotionType=="BARGAIN"){
                            getBargainAlert('batch');
                        }else if(promotionType=="FREE_SHIPPING"){
                            getFreeShippingDivAlert('batch');
                        }
                        doCloseDialog(_me);
                    }else if(promotionSource=="exist"){
                        var $pOpt=$("[name='promotionSource']:checked").closest(".s-item").find(".pSelector option:selected");
                        if(G.isEmpty($pOpt.val())){
                            nsDialog.jAlert("请选择促销。");
                            return;
                        }
                        doCloseDialog(_me);
                        var data={};
                        data['id']=$pOpt.val();
                        data['startTimeStr']=G.normalize($pOpt.attr("startTimeStr"));
                        data['endTimeStr']=G.normalize($pOpt.attr("endTimeStr"));
                        data['type']=$pOpt.attr("type");
                        data['promotionsProductDTOList']={};
                        $(".itemChk:checked").each(function(i){
                            data['promotionsProductDTOList['+i+'].productLocalInfoId']=$(this).attr("productId");

                        });
                        APP_BCGOGO.Net.syncGet({
                            "url": "promotions.do?method=validateSavePromotionsForInSales",
                            data:data,
                            type: "POST",
                            cache: false,
                            "dataType": "json",
                            success: function (result) {
                                if(!G.isEmpty(result)&&!result.success){
                                    nsDialog.jAlert(result.msg);
                                }
                                if(G.isEmpty(result.data)){
                                    doCloseDialog(_me);
                                    _doSavePromotionsProduct();
                                    return;
                                }
                                var lappingMap=result.data;
                                var promotion="";
                                var errorMsg='<div>对不起，';
                                for(var productId in lappingMap){
                                    errorMsg+='<br/>';
                                    var productName=$('.itemChk:[productId="'+productId+'"]').closest("tr").find('[field="name"]').val();
                                    promotion=lappingMap[productId];
                                    errorMsg+='商品<span style="font-weight:bold; ">'+ G.normalize(productName)+'</span>在同时段已参与促销<span style="font-weight:bold; ">'+ G.normalize(promotion.name)+'</span>，是否覆盖之前的促销？';
                                }
                                errorMsg+='</div>';
                                $(errorMsg).dialog({
                                    width: 380,
                                    height:180,
                                    modal: true,
//                                    resizable: true,
                                    title: "友情提示",
                                    buttons:{
                                        "确定":function(){
                                            $(this).dialog("close");
                                            doCloseDialog(_me);
                                            var promotionsId= G.isEmpty(promotion)?"":promotion.idStr;
                                            _doSavePromotionsProduct(function(){
                                                for(var productId in lappingMap){
                                                    var pLapping=lappingMap[productId];
                                                    var promotionsList=promotionsPruductMap[String(productId)];  //todo ss
                                                    var pArr=new Array();
                                                    if(!G.isEmpty(promotionsList)){
                                                        for(var i=0;i<promotionsList.length;i++){
                                                            var existPromotions=promotionsList[i];
                                                            if(!G.isEmpty(existPromotions)&&existPromotions.idStr!=pLapping.idStr){
                                                                pArr.push(existPromotions);
                                                            }
                                                        }
                                                    }
                                                    promotionsPruductMap[String(productId)]=pArr;
                                                }
                                            });

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
                    }
                },
                "取消":function(){
                    $(this).dialog("close");
                }
            }
        });
        function _doSavePromotionsProduct(callBack){
            var $pOpt=$("[name='promotionSource']:checked").closest(".s-item").find(".pSelector option:selected");
            var data={
                id:$pOpt.val(),
                type:$pOpt.attr("type") ,
                startTimeStr:G.normalize($pOpt.attr("startTimeStr")),
                endTimeStr:G.normalize($pOpt.attr("endTimeStr")),
                addPromotionsProductFlag:true
            };
            data['promotionsProductDTOList']={};
            $(".itemChk:checked").each(function(i){
                data['promotionsProductDTOList['+i+'].productLocalInfoId']=$(this).attr("productId");

            });
            APP_BCGOGO.Net.syncGet({
                "url": "promotions.do?method=savePromotionsForInSales",
                data:data,
                type: "POST",
                cache: false,
                "dataType": "json",
                success: function (result) {
                    if(!G.isEmpty(result)&&!result.success){
                        nsDialog.jAlert(result.msg);
                        return false;
                    }
                    nsDialog.jAlert("促销保存成功。",null,function(){
                        var promotions=result.data;
                        $(".itemChk:checked").each(function(){
                            var productId=$(this).attr("productId");
                            $("#promotionsDetail"+productId).remove();
                            var $td=$(this).closest("tr").find(".promotion-info");
                            var tdStr='已参与<br/><a class="blue_color promotionsDetailBtn">查看促销</a>';
                            $(this).closest("tr").find(".promotion-info").text("").append(tdStr);
                            var pArr=new Array();
                            pArr.push(promotions);
                            var existPromotionList=promotionsPruductMap[String(productId)];
                            if(!G.isEmpty(existPromotionList)){
                                for(var i=0;i<existPromotionList.length;i++){
                                    pArr.push(existPromotionList[i]);
                                }
                            }
                            promotionsPruductMap[String(productId)]=pArr;
                        });
                        initStockStatInfo();
                        if($.isFunction(callBack)){
                            callBack();
                        }
                    });

                },
                error:function(){
                    nsDialog.jAlert("网络异常!");
                }
            });
        }
    });

    initStockStatInfo();
    $(".select-data-60").click(); //默认查询60天内
    $("#searchProductBtn").click();
});


</script>
</head>
<body class="bodyMain">
<%@ include file="/WEB-INF/views/header_html.jsp" %>
<%@ include file="/common/messagePrompt.jsp" %>
<div class="i_main clear">
<div class="clear i_height"></div>
<div class="titBody">
<jsp:include page="supplyCenterLeftNavi.jsp">
    <jsp:param name="currPage" value="inSalingGoodsList"/>
</jsp:include>

<input type="hidden" id="paramShopId" value="${paramShopId}"/>
<div class="added-management">
<div class="group-notice">
    <div class="line-info">
        您的库存商品：共<em id="allStockProductNum" class="number" >0</em>种&nbsp;&nbsp;&nbsp;
        已上架的商品：共<em id="productInSalesNum" class="blue_col" style="font-weight: bold;font-style: normal;" pageType="_inSalingGoodsList">0</em>种&nbsp;&nbsp;&nbsp;
        未上架的商品：共<em id="productUnInSaleNum" class="blue_col" style="font-weight: bold;font-style: normal;" pageType="_inSalingGoodsList">0</em>种&nbsp;&nbsp;&nbsp;
        正在促销的商品：共<em id="promotionsProductNum" class="blue_col" style="font-weight: bold;font-style: normal;" pageType="_inSalingGoodsList">0</em>种
    </div>
    <div class="line-info">
        友情提示：选择库存商品上架，若修改信息，库存商品信息也会随之修改！新增商品上架，库存中也会新增该商品哦！
    </div>
</div>

<div class="group-tab">
    <div onclick="toInSalingGoodsList()" class="group-item actived">已上架商品</div>
    <div onclick="toUnInSalingGoodsList()" class="group-item">仓库中的商品</div>
    <a onclick="toGoodsInSalesEditor()" style="float: right;" class="blue_color addNew">新增商品上架</a>
    <div class="cl"></div>
</div>

<div class="group-content" >
<form:form commandName="searchConditionDTO" id="searchProductForm" action="product.do?method=getProducts" method="post" name="thisform">
<input type="hidden" id="salesStatus" name="salesStatus" value="InSales"/>
<input type="hidden" id="sortStatus" name="sort" value="inventoryAmountDesc"/>
<input type="hidden" id="includeBasic" name="includeBasic" value="false"/>
<input type="hidden" name="maxRows" value="25" />
<input type="hidden" name="startPageNo" value="1" />
<div class="group-display">
    <div class="search-param search-inSales">
        <dl>
            <dt>商品信息</dt>
            <dd>
                <input id="productName" name="productName" searchField="product_name"
                       type="text" placeholder="品名" class="w100 txt J-productSuggestion"/>
                <input  id="productBrand" name="productBrand" searchField="product_brand"
                        type="text"  placeholder="品牌" class="w100 txt J-productSuggestion"/>
                <input id="productSpec" name="productSpec" searchField="product_spec"
                       type="text" placeholder="规格" class="w100 txt J-productSuggestion"/>
                <input  id="productModel"  name="productModel" searchField="product_model"
                        type="text" placeholder="型号" class="w100 txt J-productSuggestion"/>
                <input id="productVehicleBrand" name="productVehicleBrand" searchField="product_vehicle_brand"
                       type="text" placeholder="车辆品牌" class="w100 txt J-productSuggestion"/>
                <input id="productVehicleModel" name="productVehicleModel" searchField="product_vehicle_model"
                       type="text" placeholder="车型" class="w100 txt J-productSuggestion"/>
                <input id="commodityCode" name="commodityCode" searchField="commodity_code"
                       type="text" placeholder="商品编号" class="w100 txt J-productSuggestion"/>
                <input id="product_kind" name="productKind" type="text" placeholder="店铺中的分类" class="w100 txt J-bcgogo-droplist-on"/>
            </dd>
            <div class="cl"></div>
            <dt>上架时间</dt>
            <dd class="inSalesTime-search">
                <span><input param="5" type="radio" name="promotion_time"/>5天内</span>
                <span><input param="10" type="radio" name="promotion_time"/>10天内</span>
                <span><input param="30" type="radio" name="promotion_time"/>30天内</span>
                <span><input param="60" checked="true" class="select-data-60" type="radio" name="promotion_time"/>60天内</span>
                <span>
                    <input class="select-date-define" type="radio" name="promotion_time"/>自定义
                    <span class="timeInputSpan" style="display: none">
                      <input class="w100 txt" field="timeInput" name="startTimeStr"  />至
                      <input class="w100 txt" field="timeInput" name="endTimeStr" />
                        <input type="hidden" name="startLastInSalesTime"/>
                        <input type="hidden" name="endLastInSalesTime"/>
                     </span>
                 </span>
            </dd>
            <div class="cl"></div>
            <dt>促销名称</dt>
            <dd>
                <input type="text" field="promotionsName" name="promotionsName" class="w100 txt"/>
                促销类型
                <input id="promotionsType" type="hidden" value="" name="promotionsTypeList">
                <label class="rad">
                    <input id="allChk" type="checkbox" promotionstypes="ALL">
                    所有
                </label>
                <label class="rad">
                    <input id="mljChk" class="promotionsTypes" type="checkbox" promotionstypes="MLJ">
                    满立减
                </label>
                <label class="rad">
                    <input id="mjsChk" class="promotionsTypes" type="checkbox" promotionstypes="MJS">
                    满就送
                </label>
                <label class="rad">
                    <input id="bargainChk" class="promotionsTypes" type="checkbox" promotionstypes="BARGAIN">
                    特价商品
                </label>
                <label class="rad">
                    <input id="freeShippingChk" class="promotionsTypes" type="checkbox" promotionstypes="FREE_SHIPPING">
                    送货上门
                </label>
            </dd>
            <div class="cl"></div>
        </dl>

        <div class="group-button">
            <div id="searchProductBtn" pageType="_inSalingGoodsList" class="button-search button-blue-gradient" onselectstart="return false;">搜&nbsp;&nbsp;索</div>
            <div id="resetBtn" class="button-clear" onselectstart="return false;">清空条件</div>
            <div class="cl"></div>
        </div>
    </div><!--end search-param-->
    <div class="search-result">
        <div class="line_develop"  style="margin-left: 9px;width: 798px;">
            <div class="sort_label">排序方式：</div>
            <a class="s-product-inSales hover" ascContact="点击后按库存量升序排列！" descContact="点击后按库存量降序排列！" currentSort="Desc" sortFiled="inventoryAmount">库存量<span class="J-sort-span arrowDown"></span>
                <div class="alert_develop" style="margin:-5px 0px 0px -15px;display: none">
                    <span class="arrowTop" style="margin-left:20px;"></span>
                    <div class="alertAll">
                        <div class="alertLeft"></div>
                        <div class="alertBody">
                            点击后按库存量升序排列！
                        </div>
                        <div class="alertRight"></div>
                    </div>
                </div>
            </a>
            <a class="s-product-inSales" ascContact="点击后按上架量升序排列！" descContact="点击后按上架量降序排列！" currentSort="Desc" sortFiled="inSalesAmount">上架量<span class="J-sort-span arrowDown"></span>
                <div class="alert_develop" style="margin:-5px 0px 0px -15px;display: none">
                    <span class="arrowTop" style="margin-left:20px;"></span>
                    <div class="alertAll">
                        <div class="alertLeft"></div>
                        <div class="alertBody">
                            点击后按上架量升序排列！
                        </div>
                        <div class="alertRight"></div>
                    </div>
                </div>
            </a>
            <a class="s-product-inSales"  ascContact="点击后按上架时间升序排列！" descContact="点击后按上架时间降序排列！" currentSort="Desc" sortFiled="lastInSalesTime">上架时间<span class="J-sort-span arrowDown"></span>
                <div class="alert_develop" style="margin:-5px 0px 0px -15px;display: none">
                    <span class="arrowTop" style="margin-left:20px;"></span>
                    <div class="alertAll">
                        <div class="alertLeft"></div>
                        <div class="alertBody">
                            点击后按上架时间升序排列！
                        </div>
                        <div class="alertRight"></div>
                    </div>
                </div>
            </a>
            <a class="s-product-inSales" ascContact="点击后按上架售价升序排列！" descContact="点击后按上架售价降序排列！"
               currentSort="Desc" sortFiled="inSalesPrice" style="border-right: 0px">
                上架售价<span class="J-sort-span arrowDown"></span>
                <div class="alert_develop" style="margin:-5px 0px 0px -15px;display: none">
                    <span class="arrowTop" style="margin-left:20px;"></span>
                    <div class="alertAll">
                        <div class="alertLeft"></div>
                        <div class="alertBody">
                            点击后按上架售价升序排列！
                        </div>
                        <div class="alertRight"></div>
                    </div>
                </div>
            </a>
                <span class="txtTransaction dashBoardHolder">
                            <input type="text" name="inSalesPriceStart" filed="bar-priceInput-start" class="price-input txt" style="width:42px; height:17px;" />
                    <div class="dashBoard txtList" style="padding-top:10px;margin-top: -1px;width: 115px;display: none">
                        <span class="clean" style="cursor: pointer;">清除</span>
                        <span class="btnSure">确定</span>
                        <div  class="listNum">
                            <span  class="pItem blue_color" end="1000">1千以下</span>
                            <span  class="pItem blue_color" start="1000" end="5000">1千~5千</span>
                            <span  class="pItem blue_color" start="5000" end="10000">5千~1万</span>
                            <span  class="pItem blue_color" start="10000">1万以上</span>
                        </div>
                    </div>
                    &nbsp;至&nbsp;<input type="text" name="inSalesPriceEnd" filed="bar-priceInput-end" class="price-input txt" style="width:40px; height:17px;" />
                 </span>
        </div>
        </form:form>
        <div class="cl"></div>
        <table id="inSalingGoodsTable" class="list-result" cellpadding="0" cellspacing="0">
            <col width="30">
            <col>
            <col width="70">
            <col width="70">
            <col width="70">
            <col width="70">
            <col width="100">
            <col width="60">
            <col width="60">
            <thead>
            <tr class="titleBg">
                <td></td>
                <td>商品信息</td>
                <td>库存量</td>
                <td>上架量</td>
                <td>上架售价</td>
                <td>质保时间</td>
                <td>上架时间</td>
                <td>促销活动</td>
                <td>操作</td>
            </tr>
            </thead>
            <%--<tr></tr>--%>
            <tr class="space">
                <td class="greyline" style="height: 42px"  colspan="9">
                    <input class="select_all" type="checkBox" />
                    <div class="batchOffSalesBtn batch-operate up-batch">批量下架</div>
                    <div class="batchSetInSalesAmount batch-operate up-batch">批量设置上架量</div>
                    <div class="batchSetInSalesPrice batch-operate up-batch">批量设置销售价</div>
                    <div class="batchSetGuaranteePeriod batch-operate up-batch">批量设置质保期</div>
                </td>
            </tr>
        </table>

    </div><!--end search-result-->

    <!-- 分页代码 -->
    <div class="i_pageBtn" style="float:right">
        <bcgogo:ajaxPaging
                url="product.do?method=getProducts"
                data="{
                   startPageNo:1,maxRows:25,
                   sort:'inventoryAmountDesc',
                   salesStatus:'InSales',
                   promotionsTypeList:'${promotionsTypeList}'
                     }"
                postFn="initInSalingGoodsList"
                display="none"
                dynamical="_inSalingGoodsList"/>
    </div>

</div><!--end group-display-->

<!-- 底部批量选择 Bar -->
<div class="batch-shelves">
    <div class="all-select">
        <label><input class="select_all" type="checkbox"/>&nbsp;全选</label>
    </div>
    <div pageSource="inSalingGoodsList" class="batchPromotionManagerBtn button-batch-shelves button-yellow-gradient" onselectstart="return false;">我要促销</div>
    <div class="batchOffSalesBtn button-batch-shelves button-yellow-gradient" onselectstart="return false;">批量下架</div>
    <div class="cl"></div>
</div>

</div><!--end group-content-->

</div>


</div>
</div>
<%--<div id="mask" style="display:block;position: absolute;"></div>--%>
<div class="tab_repay_1" id="setInSalesPriceDialog" style="display:none">
    <div class="i_add_body">
        <h3 style="line-height:25px;">设置上架售价：</h3>
        <%--<input type="hidden" id="setTradePriceDialogFormId" name="setTradePriceDialogFormId"/>--%>
        <table cellpadding="0" cellspacing="0" class="supplierTable " style="width: 200px">
            <col width="70"/>
            <col/>
            <tr>
                <td>
                    <label><input id="radio_percent" type="radio" name="setTradePrice"/>相对成本价 加价</label>
                    <input id="input_percent"  type="text" class="jiajia price-input"/>%
                </td>
            </tr>
            <tr>
                <td>
                    <label><input id="radio_value" type="radio" name="setTradePrice"/>相对成本价 加价</label>
                    <input id="input_value" type="text" class="jiajia price-input"/>元
                </td>
            </tr>
            <%--<tr>--%>
            <%--<td style="text-align: center">--%>
            <%--<input class="btn hover" id="saveTradePriceBtn" type="button" onfocus="this.blur();"--%>
            <%--value="确  定">--%>
            <%--<input class="btn" type="button" id="setTradePriceDialogCloseBtn" onfocus="this.blur();" value="取  消"></td>--%>
            <%--</tr>--%>
        </table>
    </div>
</div>

<div class="tab_repay_1" id="setGuaranteePeriodDialog" style="display:none">
    <div class="i_add_body">
        <h3 style="line-height:25px;">设置质保时间：</h3>
        <%--<input type="hidden" id="setTradePriceDialogFormId" name="setTradePriceDialogFormId"/>--%>
        <div>
            <span><input name="guaranteePeriod" type="text" style="margin: 5px" class="w50"/>个月</span>
        </div>
    </div>
</div>

<div class="tab_repay_1" id="setInSalesAmountDialog" style="display:none">
    <div style="margin-left: 5px" class="i_add_body">
        <h3 style="line-height:25px;">设置上架量：</h3>
        <input type="hidden" id="setInSalesAmountDialogFormId" name="setInSalesAmountDialogFormId"/>
        <table cellpadding="0" cellspacing="0" class="supplierTable " style="width: 200px">
            <col width="130"/>
            <col/>
            <tr>
                <td>
                    <label><input id="sameInventoryRadio" value="sameInventory" checked="true" type="radio" name="setInSalesAmount"/>与实际库存一致</label>
                </td>
            </tr>
            <tr>
                <td>
                    <label><input value="inputInSalesAmount" type="radio" name="setInSalesAmount"/>显示输入值</label>
                    <input id="input_inSalesAmount" type="text" disabled="disabled" class="jiajia"/>
                </td>

            </tr>
            <tr>
                <td>
                    <label><input value="haveGoods" type="radio" name="setInSalesAmount"/>显示为"有货"</label>
                </td>
                <%--<td>--%>
                <%--<label><input value="noGoods" type="radio" name="setInSalesAmount"/>显示无货</label>--%>
                <%--</td>  --%>
            </tr>
            <%--<tr>--%>
            <%--<td style="text-align: center" colspan="2">--%>
            <%--<input class="btn hover" id="saveInSalesAmountBtn" type="button" onfocus="this.blur();"--%>
            <%--value="确  定">--%>
            <%--<input class="btn" type="button" id="setInSalesAmountDialogCloseBtn" onfocus="this.blur();" value="取  消"></td>--%>
            <%--</tr>--%>
        </table>
    </div>
</div>
<div pageSource="inSalingGoodsList">
    <%@ include file="./promotions/batchPromotionManagerAlert.jsp" %>
</div>
<div class="insaling-goods-footer">
    <%@include file="/WEB-INF/views/footer_html.jsp" %>
</div>
</body>
</html>