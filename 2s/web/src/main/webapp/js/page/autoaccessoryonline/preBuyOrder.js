/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 13-3-8
 * Time: 上午10:54
 * To change this template use File | Settings | File Templates.
 */
var trCount=0;
$(document).ready(function () {
    trCount = $(".item").size();

    $("#cleanFormBtn").bind('click', function (e) {
        e.preventDefault();
        nsDialog.jConfirm("是否要清空所有输入项？","全部清空", function (returnVal) {
            if (returnVal) {
                window.location.reload();
            }
        });

    });
    $("#savePreBuyOrderBtn").bind('click', function (e) {
        e.preventDefault();
        //校验
        if(!validateForm().validateResult){
            return;
        }
        if (G.Lang.isEmpty($("#preBuyOrderValidDate").val())) {
//            nsDialog.jAlert("请选择有效期!");
            $(".preBuyOrderValidDateDiv").dialog({
                title:"友情提示",
                buttons:{
                    "确定":function(){
                          $('[name="preBuyOrderValidDate"]').val($('#preBuyOrderValidDateSelect').val());
                         $(this).dialog("close");
                        $("#savePreBuyOrderBtn").click();
                    },
                    "取消":function(){
                       $(this).dialog("close");
                    }
                }
            });
            return false;
        }

        if($(this).attr("disabled") !== "disabled"){
            nsDialog.jConfirm("是否确认提交求购？","", function (returnVal) {
                if (returnVal) {
                    $(this).attr("disabled", "disabled");
                    $("#preBuyOrderForm").submit();
                }
            });

        }
    });

    $("[id$='.addRowBtn']").live('click', function (e) {
        e.preventDefault();
        if (G.Lang.isEmpty($(this).closest("tr").find("input[name$='.productName']").val())) {
            nsDialog.jAlert("请输入或选择品名!");
            return false;
        }
        if (!G.Lang.isNumber($(this).closest("tr").find("input[name$='.amount']").val())) {
            nsDialog.jAlert("请输入正确的数量！");
            return false;
        }
        //采购单检查是否相同
        if (trCount >= 2 && checkSameItem()) {
            nsDialog.jAlert("单据有重复内容，请修改或删除!");
            return false;
        }

        preBuyOrderAddItemRow();
    });

    $("[id$='.deleteRowBtn']").live('click', function (event) {
        event.preventDefault();
        var target = event.target;
        var idPrefix=$(target).attr("id").split(".")[0];
        imageUploaders[idPrefix+".imageUploader"].remove();
        imageUploaderViews[idPrefix+".imageUploaderView"].remove();
        delete imageUploaders[idPrefix+".imageUploader"];
        delete imageUploaderViews[idPrefix+".imageUploaderView"];
        $(target).closest("tr").next(".J_itemMemo").remove();
        $(target).closest("tr").next(".J_itemBottom").remove();
        $(target).closest("tr").remove();
        isShowAddButton()
        trCount = $(".item").size();
    });

    //单据结算前进行校验
    function validateForm() {
        //自动删除最后的空白行
        var $last_tr = $("#table_productNo").find("tbody").find(".item:last");
        while ($("#table_productNo .item").size() != 1 && checkEmptyRow($last_tr)) {
            $last_tr.find("[a[id$='.deleteRowBtn']").click();
            $last_tr = $("#table_productNo").find("tbody").find(".item:last");
        }
        if ($(".item").size() == 1 && checkEmptyRow($(".item"))) {
            nsDialog.jAlert("单据内容不能为空!");
            return {validateResult: false};
        }
        //
        if (checkSameItem()) {
            nsDialog.jAlert("单据有重复内容，请修改或删除。");
            return {validateResult: false};
        }

        //验证数量不为0
        var validateMsg = "";
        $(".item").each(function(i) {
            var rowMsg="第" + (i+1)+"行,";
            var errorMsg = "";
            //品名
            var $productNameInput = $(this).find("input[name$='.productName']");
            if (G.Lang.isEmpty($productNameInput.val())) {
                errorMsg =errorMsg+ (G.Lang.isEmpty(errorMsg)?"":",") +  "缺少品名";
            }
            //数量
            var $amountInput = $(this).find("input[name$='.amount']");
            if (G.Lang.isEmpty($amountInput.val())) {
                $amountInput.val(0);
            }
            if (!G.Lang.isNumber($amountInput.val()) || $amountInput.val()*1<=0) {
                errorMsg = errorMsg +(G.Lang.isEmpty(errorMsg)?"":",")+ "商品数量不正确";
            }
            //单位
            var $unitInput = $(this).find("input[name$='.unit']");
            if (G.Lang.isEmpty($unitInput.val())) {
                errorMsg = errorMsg+(G.Lang.isEmpty(errorMsg)?"":",") +  "缺少单位";
            }
            if(!G.Lang.isEmpty(errorMsg)){
                validateMsg+=rowMsg+errorMsg+"<br>";
            }
        });
        if (!G.Lang.isEmpty(validateMsg)) {
            nsDialog.jAlert(validateMsg);
            return {validateResult: false};
        }
        return {validateResult:true};
    }


    $(".itemAmount").live("blur", function () {
        if (!$(this).val()) {
            $(this).val(0);
        }
        $(this).val(APP_BCGOGO.StringFilter.priceFilter($(this).val(), 1));
    }).live('keyup', function () {
            $(this).val(APP_BCGOGO.StringFilter.inputtingPriceFilter($(this).val(), 1));
        });

    $(".product-image-uploader").live("click",function(){
        var idPrefix=$(this).attr("id").split(".")[0];
        $("#"+idPrefix+"\\.productMainImageUploader").click();
    });
    isShowAddButton();
});

function preBuyOrderAddItemRow() {
    var tr = $(getTrSample()).clone(); //克隆模版，初始化所有的INPUT
    var itemIndex;
    $(tr).find("input,div,a").each(function (i) {
        //ID为空则跳过
        if (!this || !this.id)return;
        var idSuffix = this.id.split(".")[1];
        var tcNum = trCount;
        while (checkThisDom(tcNum, idSuffix)) {     //计算行号，如果目标行号对应的元素存在，则该行号加一
            tcNum = ++tcNum;
        }
        //组装新的ID和NAME  Begin-->
        var newId = "itemDTOs" + tcNum + "." + idSuffix;
        $(this).attr("id", newId);
        var name=$(this).attr("name");
        if (!G.isEmpty(name)&&!G.isEmpty(name.split(".")[1])) {
            var name=$(this).attr("name");
            name=name.substr(name.split(".")[0].length-1);
            var newName = "itemDTOs[" + tcNum + name;
            $(this).attr("name", newName);
        }
        itemIndex=tcNum;
    });
    $(tr).find("input[type='text']").each(function (i) {
        //去除文本框的自动填充下拉框
        $(this).attr("autocomplete", "off");
    });
    $("#table_productNo").append($(tr));

    var trItemMemo = '<tr class="titBody_description J_itemMemo">';
    trItemMemo += '    <td colspan="9">';
    trItemMemo += '        <div class="buying_trList buying_description">';
    trItemMemo += '           <div>';
    trItemMemo += '                <textarea name="itemDTOs[0].memo" id="itemDTOs0.memo" maxlength="200" placeholder="可输入求购商品描述（200字以内）" style="width: 98%;height:30px" class="txt"/>';
    trItemMemo += '            </div>';
    trItemMemo += '        </div>';
    trItemMemo += '    </td>';
    trItemMemo += '</tr>';

    trItemMemo = $(trItemMemo).clone(); //克隆模版，初始化所有的INPUT
    $(trItemMemo).find("textarea").each(function (i) {
        //ID为空则跳过
        if (!this || !this.id)return;
        var idSuffix = this.id.split(".")[1];
        var tcNum = trCount;
        while (checkThisDom(tcNum, idSuffix)) {     //计算行号，如果目标行号对应的元素存在，则该行号加一
            tcNum = ++tcNum;
        }
        //组装新的ID和NAME  Begin-->
        var newId = "itemDTOs" + tcNum + "." + idSuffix;
        $(this).attr("id", newId);
        if ($(this).attr("name").split(".")[1]) {
            var newName = "itemDTOs[" + tcNum + "]." + $(this).attr("name").split(".")[1];
            $(this).attr("name", newName);
        }
    });
    $("#table_productNo").append($(trItemMemo));
    $("#table_productNo").append('<tr class="titBottom_Bg J_itemBottom"><td colspan="10"></td></tr>');
    bindProductImageCmp(itemIndex);
    trCount++;
    isShowAddButton();
    return $(tr);
}

function getTrSample() {
    var trSample = '<tr class="titBody_Bg item" style="line-height: 8px;" >';
    trSample +='<td rowspan="3">' +
        '<div style="padding-left: 5px">'+
        '<input type="hidden" class="J_productMainImageView" id="itemDTOs0.productMainImage" />'+
        '<input type="hidden" class="J_productInfoImagePath" id="itemDTOs0.productInfoImagePath" name="itemDTOs[0].imageCenterDTO.productInfoImagePaths[0]"/>'+
        '<div style="position:absolute;" id="itemDTOs0.productMainImageUploader"></div>'+
        '<div id="itemDTOs0.addProductMainImage" class="add-img J_addProductMainImage">上传图片</div>'+
        '<div id="itemDTOs0.productMainImageView" style="position: relative;display: none;height:60px;width: 60px"></div>'+
        '</div>' +
        '</td>'+
        '<td>' +
        '<input id="itemDTOs0.commodityCode" name="itemDTOs[0].commodityCode" placeholder="商品编码" type="text" class="txt txt_color" maxlength="20" />' +
        '</td>';
    trSample += '<td>' +
        '<input id="itemDTOs0.id" name="itemDTOs[0].id" type="hidden" autocomplete="off"/>' +
        '<input id="itemDTOs0.productId" name="itemDTOs[0].productId" type="hidden" autocomplete="off"/>' +
        '<input id="itemDTOs0.productName" name="itemDTOs[0].productName" type="text" placeholder="品名" class="txt txt_color"/>' +
        '</td>';
    trSample += '<td>' +
        '<input id="itemDTOs0.brand" name="itemDTOs[0].brand" placeholder="品牌" class="txt txt_color" type="text"/>' +
        '</td>';
    trSample += '<td>' +
        '<input id="itemDTOs0.spec" name="itemDTOs[0].spec" placeholder="规格" class="txt txt_color" type="text"/>' +
        '</td>';
    trSample += '<td>' +
        '<input id="itemDTOs0.model" name="itemDTOs[0].model" placeholder="型号" class="txt txt_color" type="text"/>' +
        '</td>';
    trSample += '<td>' +
        '<input id="itemDTOs0.vehicleBrand" name="itemDTOs[0].vehicleBrand" placeholder="车辆品牌" class="txt txt_color" type="text"/>' +
        '</td>';
    trSample += '<td>' +
        '<input id="itemDTOs0.vehicleModel" name="itemDTOs[0].vehicleModel" placeholder="车辆型号" class="txt txt_color" type="text"/>' +
        '</td>';
    trSample += '<td>' +
        '<input id="itemDTOs0.amount" name="itemDTOs[0].amount" class="itemAmount txt txt_color" type="text" value="0"/>' +
        '</td>';
    trSample += '<td>' +
        '<input id="itemDTOs0.unit" name="itemDTOs[0].unit" placeholder="单位" class="itemUnit txt txt_color" type="text"/>' +
        '</td>';
    trSample += '<td rowspan="3">' +
        '<a class="blue_color" id="itemDTOs0.deleteRowBtn" onfocus="this.blur();">删除</a>' +
        '</td>';
    trSample += '</tr>';

    return trSample;
}
//检查单据是否相同

function checkSameItem() {
    return APP_BCGOGO.Module.wjl.invoiceCommon.checkSameItemForOrder("item");
}
//检查商品编码是否相同
function checkSameCommodityCode() {
    return APP_BCGOGO.Module.wjl.invoiceCommon.checkSameCommodityCode("item");
}
//判断是否显示+按钮
function isShowAddButton() {
    //如果初始化的话就默认加一行
    if ($(".item").size() <= 0) {
        preBuyOrderAddItemRow();
    }
    $(".item").find("a[id$='.addRowBtn']").remove();
    var opera1Id = $(".item:last").find("td:last>a[id$='.deleteRowBtn']").attr("id");
    if (!opera1Id) return;
    $(".item:last").find("td:last>a[id$='.deleteRowBtn']").after('&nbsp;<a class="blue_color" ' + ' id="itemDTOs' + (opera1Id.split(".")[0].substring(8)) + '.addRowBtn">增加</a>');
}
var imageUploaderViews={};
var imageUploaders={};
function bindProductImageCmp(rowIndex){
    var $addProductMainImageBtnObject = $("#itemDTOs"+rowIndex+"\\.addProductMainImage");
    if(G.isEmpty($addProductMainImageBtnObject)){
        return;
    }
    var imageUploader = new App.Module.ImageUploader();
    imageUploaders["itemDTOs"+rowIndex+".imageUploader"]=imageUploader;
    var currentItemNum = 0;
    if(!G.Lang.isEmpty($("#itemDTOs"+rowIndex+"\\.productMainImage").val())){
        currentItemNum = 1;
    }
    imageUploader.init({
        "selector":"#itemDTOs"+rowIndex+"\\.productMainImageUploader",
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
            $("#itemDTOs"+rowIndex+"\\.productMainImageView").show();
        },
        "uploadCompleteCallback":function(message, data) {
            var dataInfoJson = JSON.parse(data.info);
            $("#itemDTOs"+rowIndex+"\\.productMainImage").val(dataInfoJson.url);
            $("#itemDTOs"+rowIndex+"\\.productInfoImagePath").val(dataInfoJson.url);
        },
        "uploadErrorCallback":function(message, data) {
            var errorData = JSON.parse(data.info);
            errorData["content"] = data.info;
            saveUpLoadImageErrorInfo(errorData);
            $("#itemDTOs"+rowIndex+"\\.productMainImageView").hide();
            imageUploader.show();
            $addProductMainImageBtnObject.show();
            nsDialog.jAlert("上传图片失败！");
        },
        "uploadAllCompleteCallback":function() {
            var imagePath =  $("#itemDTOs"+rowIndex+"\\.productMainImage").val();
            // 设置 视图组件  idle 状态
            imageUploaderView.setState("idle");
            //更新input
            var outData = [{
                "url": APP_BCGOGO.UpYun.UP_YUN_DOMAIN_URL + imagePath + APP_BCGOGO.UpYun.UP_YUN_SEPARATOR + APP_BCGOGO.ImageScene.PRODUCT_LIST_IMAGE_SMALL,
                "name": ""
            }];
            imageUploaderView.update(outData);
        }
    });
    /**
     * 视图组建
     * */
    var imageUploaderView = new App.Module.ImageUploaderView();
    imageUploaderViews["itemDTOs"+rowIndex+".imageUploaderView"]=imageUploaderView;
    imageUploaderView.init({
        // 你所需要注入的 dom 节点
        "selector":"#itemDTOs"+rowIndex+"\\.productMainImageView",
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
        beforeDelete:function(event){
            if($("#itemDTOs"+rowIndex+"\\.dataImageRelationId").val()){
                nsDialog.jConfirm("此图片为库存商品图片，删除后库存中的图片将一并删除，是否确定删除？", "", function (flag) {
                    if(flag){
                        APP_BCGOGO.Net.asyncAjax({
                            url: "product.do?method=deleteProductImageRelation",
                            data:{
                                "dataImageRelationId":$("#itemDTOs"+rowIndex+"\\.dataImageRelationId").val()
                            },
                            type: "POST",
                            dataType: "json",
                            success: function (result) {
                                if(result.success){
                                    imageUploaderView.doDelete(event);
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
            }else{
                 imageUploaderView.doDelete(event);
            }

        },
        // 当删除某张图片时会触发此回调
        onDelete: function (event, data, index) {
//            $("#itemDTOs"+rowIndex+"\\.productMainImage").val(""); ss
            $("#itemDTOs"+rowIndex+"\\.productInfoImagePath").val("");
            $("#itemDTOs"+rowIndex+"\\.dataImageRelationId").val("");
            $("#itemDTOs"+rowIndex+"\\.productMainImageView").hide();
            imageUploader.show();
            $addProductMainImageBtnObject.show();
            imageUploader.getFlashObject().deleteFile(index);

        }

    });
//    if(!G.Lang.isEmpty($("#itemDTOs"+rowIndex+"\\.productMainImage").val())){
//        imageUploader.hide();
//        $addProductMainImageBtnObject.hide();
//        imageUploaderView.setState("idle");
//        var outData = [{
//            "url": $("#itemDTOs"+rowIndex+"\\.productMainImage").val()
//        }];
//        imageUploaderView.update(outData);
//        $("#itemDTOs"+rowIndex+"\\.productMainImageView").show();
//    }
}

function refreshProductImageCmp(idPrefix,imageCenterDTO){
    if(G.isEmpty(imageCenterDTO)
        ||G.isEmpty(imageCenterDTO.productListSmallImageDetailDTO)
        ||G.isEmpty(imageCenterDTO.productListSmallImageDetailDTO.imageURL)
        ){
        return;
    }
    if(!G.isEmpty($("#"+idPrefix+"\\.productInfoImagePath").val())){
        nsDialog.jConfirm("您已上传了图片，是否使用商品已存在图片？", "", function (flag) {
            if(flag){
                _doRefresh(imageCenterDTO);
            }
        });
    }else{
        _doRefresh(imageCenterDTO);
    }

    function _doRefresh(imageCenterDTO){
        var imageSmallURL=imageCenterDTO.productListSmallImageDetailDTO.imageURL;
        var dataImageRelationId=imageCenterDTO.productListSmallImageDetailDTO.dataImageRelationIdStr;
        var imageUploaderView=imageUploaderViews[idPrefix+".imageUploaderView"];
        if(!G.isEmpty(imageCenterDTO)&&!G.isEmpty(imageUploaderView)){
            $("#" + idPrefix + "\\.productMainImage").val(imageSmallURL);
            imageUploaderView.setState("idle");
            var outData = [{
                "url": imageSmallURL,
                "name": ""
            }];
            imageUploaderView.update(outData);
        }
        imageUploaders[idPrefix+".imageUploader"].hide();
        $("#" +idPrefix+"\\.addProductMainImage").hide();
        $("#" +idPrefix+"\\.dataImageRelationId").val(dataImageRelationId);
        $("#" +idPrefix+"\\.productMainImageView").show();
        var imagePath=imageCenterDTO.productListSmallImageDetailDTO.imagePath;
        $("#" +idPrefix+"\\.productInfoImagePath").val(imagePath);
        $("#" +idPrefix+"\\.productMainImage").val(imagePath);
    }
}

