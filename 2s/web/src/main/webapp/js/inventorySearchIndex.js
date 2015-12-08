var isAllowOYuanStorageFlag = false;  //是否允许0元入库 或者采购

//TODO 进销存页面品名后面的放大镜，用于查询库存明细
function confirmModification(idPrefix) {   //TODO idPrefixLastModified应该用于确定最后一次内容发生变化的文本框。此处将其清空
    if (idPrefixLastModified == idPrefix) {
        idPrefixLastModified = null;
    }
}
function searchInventoryIndex(domObj) { //TODO 放大镜查询库存明细
    var idPrefix = domObj.id.split(".")[0];
    var indexNum = idPrefix.substring(8);
    var vehicleBrand;
    var vehicleModel;
    var vehicleYear;
    var vehicleEngine;
    var orderType = "";
    //TODO 由于维修单页面和其它单据页面不同  所以此处选择车型做判断 BEGIN-->
    if ($("#" + idPrefix + "\\.vehicleBrand").length > 0) {
        vehicleBrand = $("#" + idPrefix + "\\.vehicleBrand").val();
    } else {
        vehicleBrand = $("#brand").val();
        orderType = "vehicle";
    }
    if ($("#" + idPrefix + "\\.vehicleModel").length > 0) {
        vehicleModel = $("#" + idPrefix + "\\.vehicleModel").val();
    } else {
        vehicleModel = $("#model").val();
    }
    if ($("#" + idPrefix + "\\.vehicleYear").length > 0) {
        vehicleYear = $("#" + idPrefix + "\\.vehicleYear").val();
    } else {
        vehicleYear = $("#year").val();
    }
    if ($("#" + idPrefix + "\\.vehicleEngine").length > 0) {
        vehicleEngine = $("#" + idPrefix + "\\.vehicleEngine").val();
    } else {
        vehicleEngine = $("#engine").val();
    }
    //TODO <-- END
    var productName = $("#" + idPrefix + "\\.productName").val();
    if (orderType != "vehicle") {      //TODO 如果不是维修单，品名不能为空
        if (productName == "") {
            alert("请选择或输入品名!");
            return false;
        }
    }
    var productBrand = $("#" + idPrefix + "\\.brand").val();
    var productSpec = $("#" + idPrefix + "\\.spec").val();
    var productModel = $("#" + idPrefix + "\\.model").val();
    $("#div_brand").css({'display':'none'});
    if ($("#goodSalePage") != null && $("#goodSalePage").val() == "1") {
        orderType = "goodsale";
    }
    operation = "search";
    //TODO 弹出库存查询页面
    bcgogo.checksession({"parentWindow":window.parent,'iframe_PopupBox':$("#iframe_PopupBox")[0],
        'src': "searchInventoryIndex.do?method=createCuDeatil"
            + "&productName=" + encodeURIComponent(productName)
            + "&productBrand=" + encodeURIComponent(productBrand)
            + "&productSpec=" + encodeURIComponent(productSpec)
            + "&productModel=" + encodeURIComponent(productModel)
            + "&productVehicleBrand=" + encodeURIComponent(vehicleBrand)
            + "&productVehicleModel=" + encodeURIComponent(vehicleModel)
            + "&indexNum=" + encodeURIComponent(indexNum)
            + "&orderType=" + encodeURIComponent(orderType)
            + "&maxResult=10&pageStatus=Home"});
    return false;
}

function checkVehicleInfo(domObj) {    //TODO 当点击放大镜时，验证查询条件是否符合格式
    var idPrefix = domObj.id.split(".")[0];
    if (idPrefix == "" || idPrefix == "itemDTOs-1") {
        return null;
    }
    var productName = $("#" + idPrefix + "\\.productName").val();
    var priceDom = $("#" + idPrefix + "\\.price")[0];
    var price = null;
    if (priceDom != null && priceDom.type != "hidden") price = priceDom.value;//TODO 有些页面没有该元素，所以加判断
    var purchasePriceDom = $("#" + idPrefix + "\\.purchasePrice")[0];
    var purchasePrice = null;
    if (purchasePriceDom != null && purchasePriceDom.type != "hidden") purchasePrice = purchasePriceDom.value;
    var recommendedPriceDom = $("#" + idPrefix + "\\.recommendedPrice")[0];
    var recommendedPrice = null;
    if (recommendedPriceDom != null && recommendedPriceDom.type != "hidden") recommendedPrice = recommendedPriceDom.value;
    var amount = $("#" + idPrefix + "\\.amount").val();

    if (productName == "") {
        alert("请输入或选择品名!");
        return false;
    }
    if (price != null && (isNaN(price) || price < -0.001)) {
        alert("请输入正确的价格！");
        return false;
    }
    if (purchasePrice != null && (isNaN(purchasePrice) || purchasePrice < -0.001)) {
        alert("请输入正确的价格！");
        return false;
    }
    if (isNaN(recommendedPrice) || recommendedPrice < -0.001) {
        alert("请输入正确的设定销售价！");
        return false;
    }
    if (isNaN(amount) || amount < 0.001) {
        alert("请输入正确的数量！");
        return false;
    }
}

function exactSearchInventorySearchIndex() {
    if (operation == "purchaseOrderAdd") {
        purchaseOrderAdd();
    } else if (operation == "purchaseOrderSubmit") {
        purchaseOrderSubmit();
    } else if (operation == "inventoryOrderAdd") {
        inventoryOrderAdd();
    } else if (operation == "inventoryOrderSubmit") {
        inventoryOrderSubmit();
    } else if(operation == "purchaseOrderModifySubmit"){    //采购改单
	     purchaseOrderModifySubmit();
    }
}

//TODO 校验单据的信息完整与正确性
function validateAll() {
    var msg = "";
		var priceMsg = "";
    $(".itemPurchasePrice,.itemPrice").each(function(i) {
        var msgi = "第" + (i + 1) + "行";
	      var priceMsgi = "第" + (i + 1) + "行";
        var idPrefix = $(this).attr("id").split(".")[0];
        var productName = $("#" + idPrefix + "\\.productName").val();
        var purchasePrice = $("#" + idPrefix + "\\.purchasePrice").val();
        if(getOrderType()=='PURCHASE'){
             purchasePrice = $("#" + idPrefix + "\\.price").val();
        }
        var unit = G.normalize($.trim($("#" + idPrefix + "\\.unit").val()));
        var recommendedPriceDom = $("#" + idPrefix + "\\.recommendedPrice")[0];
        var recommendedPrice = null;
        if (recommendedPriceDom != null && recommendedPriceDom.type != "hidden") recommendedPrice = recommendedPriceDom.value;
        var amount = $("#" + idPrefix + "\\.amount").val();
        if (productName == "") {
            msgi += "，缺少品名";
        }
        if (isNaN(purchasePrice) || purchasePrice < -0.001) {
            msgi += "，单价不正确";
        }
	      if(purchasePrice == "" || purchasePrice == null){
		      $("#" + idPrefix + "\\.purchasePrice").val(0);
		      $("#" + idPrefix + "\\.purchasePrice").keyup();
		       purchasePrice = 0;
	      }
	      if(purchasePrice<0.0001&&purchasePrice>-0.0001){
		      priceMsgi +="商品价格为 0";
	      }
        if (isNaN(amount) || amount < 0.001) {
            msgi += "，数量不正确";
        }
        if (unit == "") {
          msgi += "，缺少单位";
        }
        if (recommendedPrice != null && recommendedPrice != "" && (isNaN(recommendedPrice) || recommendedPrice < -0.0001)) {
            msgi += "，设定销售价不正确";
        }
        if (msgi != "第" + (i + 1) + "行") {
            msg += msgi + "，请补充完整。\n"
        }
	       if (priceMsgi != "第" + (i + 1) + "行") {
		       var orderType = $("#orderType").val();
		       if(orderType == "purchaseOrder"){
			        priceMsg += priceMsgi + "，是否确认采购。\n" ;
		       }else if(orderType == "purchaseInventoryOrder"){
			        priceMsg += priceMsgi + "，是否确认入库。\n"  ;
		       }
        }
    });
    if (msg != "") {
        alert(msg);
        return false;
    } else {
	    if(!isAllowOYuanStorageFlag && priceMsg!=""){
		    if(confirm(priceMsg)){
			    isAllowOYuanStorageFlag = true;
			    return true;
		    }else{
			    isAllowOYuanStorageFlag = false;
			    return false;
		    }
	    }
	    return true;
    }
}