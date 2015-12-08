<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
<title>批量上架</title>
<link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="js/extension/uploadify/uploadify<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="js/components/themes/bcgogo-imageUploader<%=ConfigController.getBuildVersion()%>.css"/>
<style type="text/css">
    .ui-autocomplete {
        max-height: 250px;
        min-width: 50px;
        overflow-y: auto;
        overflow-x: hidden;
    }

</style>
<%@include file="/WEB-INF/views/header_script.jsp" %>

<script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.js"></script>
<script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/page/txn/addNewProduct<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/page/autoaccessoryonline/promotionsUtil<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/page/autoaccessoryonline/promotions<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/page/autoaccessoryonline/goodsInOffSales<%=ConfigController.getBuildVersion()%>.js"></script>

<%@ include file="/WEB-INF/views/image_script.jsp" %>
<script type="text/javascript" src="js/components/ui/bcgogo-imageUploader<%=ConfigController.getBuildVersion()%>.js"></script>


<script type="text/javascript">
defaultStorage.setItem(storageKey.MenuUid, "AUTO_ACCESSORY_ONLINE_GOODS_IN_OFF_SALES_MANAGE");
defaultStorage.setItem(storageKey.MenuCurrentItem,"上架");

$().ready(function(){

     if(!swfobject.hasFlashPlayerVersion("11.8.0")){
        var content='<div>检测到您尚未安装Flash插件。请按以下步骤安装：</div>'+
                '<div>1. <a class="blue_color" href="http://mail.bcgogo.com:8088/install_flash_player.exe" style="color: #007CDA">点我下载</a>，'+
                '并记住下载到本机的文件位置</div>'+
                '<div>2. 下载完成后关闭一发软件</div>'+
                '<div>3. 找到下载好的文件，双击并安装</div>'+
                '<div>4. 重新打开一发软件</div>';
        nsDialog.jAlert(content);
    }

    $('[name$="inSalesAmount"]').each(function(){
        if(G.rounding($(this).val())==-1){
            $(this).val("");
            $(this).closest("td").find('[field="radio-inSalesAmount-exist"]').click();
        }
    });

    $(".itemUnitShow").each(function(){
        var itemStorageUnit=$(this).closest("td").find(".itemStorageUnit").val();
        var itemSellUnit=$(this).closest("td").find(".itemSellUnit").val();
        if((itemStorageUnit==itemSellUnit)){
            $(this).css("color","#272727");
        }
    });

    getPromotionDetailForInsales(function(promotionList){
        initShopPromotionInfo(promotionList);
    });

    //初始化图片组件
    $(".J_addProductMainImage").each(function(index){
        var $addProductMainImageBtnObject = $(this);
        var rowIndex = $addProductMainImageBtnObject.attr("data-index");
        var imageUploader = new App.Module.ImageUploader();
        var currentItemNum = 0;
        if(!G.Lang.isEmpty($("#productMainImage"+rowIndex).val())){
            currentItemNum = 1;
        }

        imageUploader.init({
            "selector":"#productMainImageUploader"+rowIndex,
            "flashvars":{
                "debug":"off",
                "transparent":"true",
                "currentItemNum": currentItemNum,
                "width":60,
                "height":60,
                "maxFileNum":1,
                "url":APP_BCGOGO.UpYun.UP_YUN_UPLOAD_DOMAIN_URL+"/"+APP_BCGOGO.UpYun.UP_YUN_BUCKET+"/",
                "ext":{
                    "policy":$("#policy").val(),
                    "signature":$("#signature").val()
                }
            },

            "startUploadCallback":function(message) {
                imageUploader.hide();
                $addProductMainImageBtnObject.hide();
                // 设置 视图组件 uploading 状态
                imageUploaderView.setState("uploading");
                $("#productMainImageView"+rowIndex).show();
            },
            "uploadCompleteCallback":function(message, data) {
                var dataInfoJson = JSON.parse(data.info);
                $("#productMainImage"+rowIndex).val(dataInfoJson.url);
            },
            "uploadErrorCallback":function(message, data) {
                var errorData = JSON.parse(data.info);
                errorData["content"] = data.info;
                saveUpLoadImageErrorInfo(errorData);
                $("#productMainImageView"+rowIndex).hide();
                imageUploader.show();
                $addProductMainImageBtnObject.show();
                nsDialog.jAlert("上传图片失败！");
            },
            "uploadAllCompleteCallback":function() {
                var imagePath = $("#productMainImage"+rowIndex).val();
                if(!G.Lang.isEmpty(imagePath)){
                    var productLocalInfoId=$("#productMainImage"+rowIndex).closest("tr").find('[name$="productId"]').val();

                    APP_BCGOGO.Net.asyncAjax({
                        url: "product.do?method=saveProductImageRelation",
                        data:{"imagePath":imagePath,"productLocalInfoId":productLocalInfoId},
                        type: "POST",
                        dataType: "json",
                        success: function (result) {
                            if(result.success){
                                $("#dataImageRelationId"+rowIndex).val(result.data.idStr);
                                // 设置 视图组件  idle 状态
                                imageUploaderView.setState("idle");
                                //更新input
                                var outData = [{
                                    "url": APP_BCGOGO.UpYun.UP_YUN_DOMAIN_URL + imagePath + APP_BCGOGO.UpYun.UP_YUN_SEPARATOR + APP_BCGOGO.ImageScene.PRODUCT_LIST_IMAGE_SMALL,
                                    "name": ""
                                }];
                                imageUploaderView.update(outData);
                                nsDialog.jAlert("上传图片成功！");
                            }else{
                                $("#productMainImageView"+rowIndex).hide();
                                imageUploader.show();
                                $addProductMainImageBtnObject.show();
                                nsDialog.jAlert("上传图片失败！");
                            }
                        },
                        error:function(){
                            $("#productMainImageView"+rowIndex).hide();
                            imageUploader.show();
                            $addProductMainImageBtnObject.show();
                            nsDialog.jAlert("网络异常！");
                        }
                    });
                }
            }
        });
        /**
         * 视图组建
         * */
        var imageUploaderView = new App.Module.ImageUploaderView();
        imageUploaderView.init({
            // 你所需要注入的 dom 节点
            selector:"#productMainImageView"+rowIndex,
            width:60,
            height:62,
            iWidth:60,
            iHeight:60,
            paddingTop:0,
            paddingBottom:0,
            paddingLeft:0,
            paddingRight:0,
            horizontalGap:0,
            verticalGap:0,
            waitingInfo:"加载中...",
            showWaitingImage:false,
            maxFileNum:1,
            // 当删除某张图片时会触发此回调
            onDelete: function (event, data, index) {
                // 从已获得的图片数据池中删除 图片数据
                if(G.Lang.isEmpty($("#dataImageRelationId"+rowIndex).val())) return;
                APP_BCGOGO.Net.asyncAjax({
                    url: "product.do?method=deleteProductImageRelation",
                    data:{"dataImageRelationId":$("#dataImageRelationId"+rowIndex).val()},
                    type: "POST",
                    dataType: "json",
                    success: function (result) {
                        if(result.success){
                            $("#productMainImage"+rowIndex).val("");
                            $("#dataImageRelationId"+rowIndex).val("");
                            $("#productMainImageView"+rowIndex).hide();
                            imageUploader.show();
                            $addProductMainImageBtnObject.show();
                            imageUploader.getFlashObject().deleteFile(index);
                            nsDialog.jAlert("删除图片成功！");
                        }else{
                            nsDialog.jAlert("删除图片失败！");
                        }
                    },
                    error:function(){
                        nsDialog.jAlert("网络异常！");
                    }
                });

            }

        });
        if(!G.Lang.isEmpty($("#productMainImage"+rowIndex).val())){
            imageUploader.hide();
            $addProductMainImageBtnObject.hide();
            imageUploaderView.setState("idle");
            var outData = [{
                "url": $("#productMainImage"+rowIndex).val()
            }];
            imageUploaderView.update(outData);
            $("#productMainImageView"+rowIndex).show();
        }

    });

    $('[pageSource="batchGoodsInSalesEditor"] .savePromotionsBtn-InSales').click(function(){
        var _me=$(this);
        if($(_me).attr("disabled")){
            return;
        }
        $(_me).attr("disabled", true);
        savePromotions($(this),function(result,flag){
            if(!flag){
                return;
            }
            nsDialog.jAlert("促销创建成功。",null,function(){
                if($(_me).attr("pagetype") == 'manageFreeShipping'){
                    doHiddeDiv();
                }else{
                    doCloseDialog(_me);
                }
                $(_me).removeAttr("disabled");
                var promotions=result.data;
                getPromotionDetailForInsales(function(promotionList){
                    if(!G.isEmpty(promotions)&&promotions.type=="BARGAIN"){
                        promotionList=promotionList||{};
                        promotionList[promotionList.length]=promotions;
                    }
                    initShopPromotionInfo(promotionList);
                    var $trList= $(".itemChk:checked").closest("tr");
                    if(!G.isEmpty(promotions)){
                        $trList.find(".promotionSelector").val(promotions.idStr);
                    }
                });
            })
        });

    })

    $(".batchPromotionManagerBtn").live("click",function(){
        if($(".itemChk:checked").length==0){
            nsDialog.jAlert("请选择要促销的商品。");
            return;
        }
        getServiceTime(function(timeMap){
            if(!G.isEmpty(timeMap)){
                $("#serviceStartTime").val(G.normalize(timeMap.currentTime));
                $("[name='startTimeStr']").val(G.normalize(timeMap.currentTime));
                $(".date_select_month").click();
            }
        });
        $("#batchPromotionManagerAlert").dialog({
            title:"友情提示",
            resizable: false,
            modal: true,
            draggable:true,
            open: function () {
                return true;
            },
            buttons:{
                "确定":function(){
                    var _me=$(this);
                    doCloseDialog(_me);
                    var promotionSource=$("[name='promotionSource']:checked").attr("promotionSource");
                    if(promotionSource=="new"){

                        var promotionType=$("#promotionTypeSelector").val();
                        if(G.isEmpty(promotionType)){
                            nsDialog.jAlert("请选择促销类型。");
                            return;
                        }
                        if(promotionType=="MLJ"){
                            getMLJAlert('batch');
                        }else if(promotionType=="MJS"){
                            getMJSAlert('batch');
                        }else if(promotionType=="BARGAIN"){
                            getBargainAlert('batch');
                        }else if(promotionType=="FREE_SHIPPING"){
                            getFreeShippingDivAlert('batch');
                        }
                    }else if(promotionSource=="exist"){
                        var promotionsId=$("[name='promotionSource']:checked").closest(".s-item").find(".pSelector option:selected").val();
                        $(".itemChk:checked").each(function(i){
                            $(this).closest("tr").find(".promotionSelector").val(promotionsId);
                        });
                    }
                },
                "取消":function(){
                    $(this).dialog("close");
                }
            }
        });

    });

});


function setTotal(){

}

function setSelectedProductCategoryData(selectDataInfo,categoryId,categoryName,categoryType,$parentDomObject){
    $parentDomObject.find(".J_productCategoryInfoSpan").text(selectDataInfo);
    $parentDomObject.find("input[name$='productCategoryId']").val(categoryId);
    $parentDomObject.find("input[name$='productCategoryName']").val(categoryName);
    $parentDomObject.find("input[id$='productCategoryType']").val(categoryType);
}
function batchSetSelectedProductCategoryData(selectDataInfo,categoryId,categoryName,categoryType,$parentDomObject){
    if($parentDomObject.find(".itemChk:checked").length<=0){
        nsDialog.jAlert("请选择要设置分类的商品!");
        return false;
    }
    $parentDomObject.find(".itemChk:checked").each(function(i) {
        var $currentRow = $(this).closest("tr");
        $currentRow.find(".J_productCategoryInfoSpan").text(selectDataInfo);
        $currentRow.find("input[name$='productCategoryId']").val(categoryId);
        $currentRow.find("input[name$='productCategoryName']").val(categoryName);
        $currentRow.find("input[id$='productCategoryType']").val(categoryType);
    });
}
</script>
</head>
<body class="bodyMain">
<%@ include file="/WEB-INF/views/header_html.jsp" %>

<input type="hidden" id="policy" value="${upYunFileDTO.policy}">
<input type="hidden" id="signature" value="${upYunFileDTO.signature}">

<%@ include file="/common/messagePrompt.jsp" %>
<div class="i_main clear">
<div class="clear i_height"></div>
<div class="titBody">
    <jsp:include page="supplyCenterLeftNavi.jsp">
        <jsp:param name="currPage" value="inSalingGoodsList"/>
    </jsp:include>
    <jsp:include page="../txn/unit.jsp"/>

    <div class="bodyLeft">
        <div class="upload-goods"><img src="../web/images/upload_r2_c2.png" /></div>
        <div class="cuSearch">
            <div class="lineTitle">
                填写商品信息
            </div>
            <div class="clear"></div>
            <div class="fillBody">
                <div class="added-management">
                    <div class="group-content" >
                        <form:form commandName="heavyProductDTO" id="goodsInSalesForm" action="goodsInOffSales.do?method=batchSaveGoodsInSales" target="_blank" method="post" name="thisform">
                            <div class="group-display">
                                <!--end search-param-->
                                <div class="search-result" style="padding-top:3px;">
                                    <table class="list-result" cellpadding="0" cellspacing="0" style="width:773px; margin-top:10px;font-size: 12px">
                                        <col width="20">
                                        <col>
                                        <col width="200">
                                        <col width="100">
                                        <col width="150">
                                        <col width="80">
                                        <col width="80">
                                        <col width="105">
                                        <col width="60">
                                        <col width="100">
                                        <thead>
                                        <tr>
                                            <td class="item-checkbox"></td>
                                            <td>商品信息</td>
                                            <td>上架分类</td>
                                            <td>当前库存</td>
                                            <td>上架量</td>
                                            <td>单位</td>
                                            <td>销售价</td>
                                            <td>质保时间</td>
                                            <td>促销设置</td>
                                            <td>商品图片</td>
                                        </tr>
                                        </thead>

                                        <tbody>
                                        <tr>
                                            <td colspan="9" class="greyline" style="height:42px;">
                                                <input class="select_all" type="checkbox"/>
                                                <div class="batchSetInSalesAmount batch-operate up-batch">批量设置上架量</div>
                                                <div class="batchSetInSalesPrice batch-operate up-batch">批量设置销售价</div>
                                                <div class="batchSetProductCategory up-batch">批量设置上架分类</div>
                                                <div pageSource="batchGoodsInSalesEditor" class="batchPromotionManagerBtn batch-operate up-batch">批量设置促销</div>
                                            </td>
                                        </tr>
                                        <c:if test="${not empty products}">
                                            <c:forEach items="${products}" var="product" varStatus="status">
                                                <tr>
                                                    <td class="txt-top item-checkbox">
                                                        <input id="productDTOs${status.index}.productId" class="itemChk" name="productDTOs[${status.index}].productId" value="${product.productLocalInfoIdStr}" productId="${product.productLocalInfoIdStr}" type="checkbox"/>
                                                        <input type="hidden" field="name" value="${product.name}" />
                                                        <input type="hidden" value="${product.productLocalInfoIdStr}" field="productId">
                                                        <input type="hidden"  class="j_inventoryNum" value="${product.inventoryNum}">
                                                        <input type="hidden" class="j_inventoryAveragePrice" value="${product.inventoryAveragePrice}">
                                                    </td>
                                                    <td class="txt-top">
                                                        <div class="goods-informaiton">
                                                               <span field="productInfo"> ${product.productInfoStr} </span>
                                                            <p class="price">
                                                                零售价：<span class="arialFont">&yen;</span> <span id="productDTOs${status.index}.recommendedPriceSpan">${product.recommendedPrice}</span><br/>
                                                                批发价：<span class="arialFont">&yen;</span><span id="productDTOs${status.index}.tradePriceSpan">${product.tradePrice}</span>
                                                            </p>
                                                        </div>
                                                    </td>
                                                    <td class="txt-top">
                                                        <input type="hidden" id="productDTOs${status.index}.productCategoryName" name="productDTOs[${status.index}].productCategoryName" value="${empty product.productCategoryDTO?'':product.productCategoryDTO.name}"/>
                                                        <input type="hidden" id="productDTOs${status.index}.productCategoryId" name="productDTOs[${status.index}].productCategoryId" value="${product.productCategoryDTO.id}"/>
                                                        <input type="hidden" id="productDTOs${status.index}.productCategoryType" value="${empty product.productCategoryDTO?'':product.productCategoryDTO.categoryType}"/>
                                                        <span class="J_productCategoryInfoSpan">${product.productCategoryInfo}</span>
                                                        <br/> <a class="blue_color J_selectProductCategory">修改分类</a>
                                                    </td>
                                                    <td class="txt-top">
                                                        <span id="productDTOs${status.index}.inventoryAmountSpan">${product.inventoryNum}</span>

                                                    </td>
                                                    <td class="txt-top">
                                                        <div>
                                                            <input name="radio-inSalesAmount-${status.index}" checked="true" field="radio-inSalesAmount-amount" type="radio" />
                                                            <input id="productDTOs${status.index}.inSalesAmount"  name="productDTOs[${status.index}].inSalesAmount" field="inSalesAmount" style="width: 40px" class="price-input txt" value="${product.inSalesAmount}">
                                                        </div>
                                                        <div>
                                                            <input name="radio-inSalesAmount-${status.index}" field="radio-inSalesAmount-exist" type="radio" /><span style="padding-left:2px">有货</span>
                                                        </div>
                                                    </td>
                                                    <td class="txt-top">
                                                        <c:choose>
                                                            <c:when test="${empty product.sellUnit}">
                                                                <input id="productDTOs${status.index}.unit" name="productDTOs[${status.index}].unit" value="${product.sellUnit}"
                                                                       class="j-promotion-itemUnit txt w30"/>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <span>${product.sellUnit}</span>
                                                                 <input id="productDTOs${status.index}.unit" name="productDTOs[${status.index}].unit" value="${product.sellUnit}"
                                                                       class="j-promotion-itemUnit txt w30" style="display: none;"/>
                                                            </c:otherwise>
                                                        </c:choose>
                                                        <input id="productDTOs${status.index}.storageUnit" type="hidden" name="productDTOs[${status.index}].storageUnit" value="${product.storageUnit}"
                                                               class="itemStorageUnit"/>
                                                        <input id="productDTOs${status.index}.sellUnit" type="hidden" name="productDTOs[${status.index}].sellUnit" value="${product.sellUnit}"
                                                               class="itemSellUnit"/>
                                                        <input id="productDTOs${status.index}.rate" type="hidden" name="productDTOs[${status.index}].rate" value="${product.rate}"
                                                               class="itemRate"/>

                                                    </td>
                                                    <td class="txt-top">
                                                        <span class="arialFont" style="margin-right:0;">&yen;</span>
                                                        <input id="productDTOs${status.index}.inSalesPrice" name="productDTOs[${status.index}].inSalesPrice" field="inSalesPrice"
                                                               class="price-input txt w30" maxlength="8" value="${product.inSalesPrice}">
                                                    </td>
                                                    <td class="txt-top">
                                                        <input id="productDTOs${status.index}.guaranteePeriod" name="productDTOs[${status.index}].guaranteePeriod" class="price-input txt w30" value="${product.guaranteePeriod}">月
                                                    </td>
                                                    <td class="txt-top">
                                                        <select class="promotionSelector txt" name="promotionsId"  style="width:95px;"></select>
                                                    </td>
                                                    <td class="txt-top">
                                                        <input type="hidden" class="J_productMainImageView" id="productMainImage${status.index}" value="${product.imageCenterDTO.productListSmallImageDetailDTO.imageURL}"/>
                                                        <input type="hidden" id="dataImageRelationId${status.index}" value="${product.imageCenterDTO.productListSmallImageDetailDTO.dataImageRelationId}"/>
                                                        <div style="position:absolute;" id="productMainImageUploader${status.index}"></div>
                                                        <div data-index="${status.index}" class="add-img J_addProductMainImage">上传图片</div>
                                                        <div id="productMainImageView${status.index}" style="position: relative;display: none;height:60px;width: 60px"></div>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </c:if>
                                        </tbody>

                                    </table>
                                    <div class="greyline">
                                        <input class="select_all" type="checkbox"/>
                                        <div class="batchSetInSalesAmount batch-operate up-batch">批量设置上架量</div>
                                        <div class="batchSetInSalesPrice batch-operate up-batch">批量设置销售价</div>
                                        <div class="batchSetProductCategory batch-operate up-batch">批量设置上架分类</div>
                                        <div class="batchPromotionManagerBtn batch-operate up-batch" pageSource="batchGoodsInSalesEditor">批量设置促销</div>
                                    </div>
                                </div><!--end search-result-->

                                <!-- 分页代码 -->

                            </div><!--end group-display-->
                        </form:form>
                        <!-- 底部批量选择 Bar -->

                    </div><!--end group-content-->

                </div>
                <div class="clear"></div>
                <div class="uploadBar"><input id="batchInSalesBtn"  type="button" class="upload-btn" /></div>
            </div>

        </div>
    </div>


</div>

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
<div pageSource="batchGoodsInSalesEditor">
    <%@ include file="./promotions/batchPromotionManagerAlert.jsp" %>
    <%@ include file="productCategorySelector.jsp" %>

    <%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>