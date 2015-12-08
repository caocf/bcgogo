var bcgogoAjaxQuery = APP_BCGOGO.Module.wjl.ajaxQuery;

$(document).ready(function() {
  var ajaxUrl ="";
  var ajaxData={};

  if ($("#famousSupplierSpan").length > 0) {
    ajaxUrl = "supplyDemand.do?method=getBcgogoRecommendSupplierShop";
    ajaxData = {};
    bcgogoAjaxQuery.setUrlData(ajaxUrl, ajaxData);
    bcgogoAjaxQuery.ajaxQuery(function(json) {
      initFamousSupplier(json);
    });
  }

//    if($("#recommendSupplierTable").length > 0){
//        var url = "supplyDemand.do?method=getRecommendShopByShopId";
//        var data = {pageSize:9};
//        bcgogoAjaxQuery.setUrlData(url, data);
//        bcgogoAjaxQuery.ajaxQuery(function(json) {
//            $("#recommendSupplierTable tr:not(:first)").remove();
//            initRecommendSupplier(json);
//        });
//    }
  //推荐客户 推荐供应商
  if($(".recommend-shop-div").length > 0){
    var url = "supplyDemand.do?method=getRecommendShopByShopId";
    var data = {pageSize:9};
    bcgogoAjaxQuery.setUrlData(url, data);
    bcgogoAjaxQuery.ajaxQuery(function(json) {
      $("#recommendSupplierTable tr:not(:first)").remove();
      initRecommendShop(json);
    });
  }

  if ($("#preBuyItemRecommendDiv").length > 0) {
    var url = "supplyDemand.do?method=getWholesalerPreBuyOrderRecommendByShopId";
    var data = {};
    bcgogoAjaxQuery.setUrlData(url, data);
    bcgogoAjaxQuery.ajaxQuery(function(json) {
      initWholesalerPreBuyOrderRecommend(json);
    });
  }

  if ($("#recommendProductDiv").length > 0) {
    APP_BCGOGO.Net.asyncPost({
      url: "supplyDemand.do?method=getProductRecommendByShopId",
      data: {},
      cache: false,
      dataType: "json",
      success: function (data) {
        initRecommendAccessory(data);
      }
    });
  }

  if ($("#lastWeekStatDiv").length > 0) {
    var url = "supplyDemand.do?method=getLastWeekSalesInventoryStatByShopId";
    var data = {};
    bcgogoAjaxQuery.setUrlData(url, data);
    bcgogoAjaxQuery.ajaxQuery(function(json) {
      initLastWeekSalesInventoryStat(json);

      var scrollFlowHorizontal = new App.Module.ScrollFlowHorizontal();
      scrollFlowHorizontal
        .init({
        "selector": "#lastWeekStatDiv",
        "width": 610,
        "height": 135,
        "background": "#fff",
        "scrollInterval":5000
      }).startAutoScroll();
      window.scrollFlowHorizontal = scrollFlowHorizontal;
    });
  }

  $("#goShoppingCartBtn").bind("click",function(e){
    e.preventDefault();
    window.location.href="shoppingCart.do?method=shoppingCartManage";
  });

  $(".J-purchaseSingleBtn").live("click",function(e){
    e.preventDefault();
    var productId = $(this).attr("productId");
    var amount = 1;
    if(!G.Lang.isEmpty(productId)){
      purchaseProduct(productId,amount);
    }
  });

  $(".J-addToShoppingCartSingleBtn").live("click",function(e){
    e.preventDefault();
    var productId = $(this).attr("productId");
    var amount ="1";
    var supplierShopId=$(this).attr("supplierShopId");
    if(G.isEmpty(productId)||G.isEmpty(supplierShopId)){
      return;
    }
    var paramString = supplierShopId+"_"+productId+"_"+amount;
    addProductToShoppingCart(paramString);
  });

  $("#closeBtn").bind("click",function(e){
    e.preventDefault();
    $("#returnDialog").dialog("close");
  });

  $("#searchQuotationBtn").click(function(){
    $("#searchQuotationForm").submit();
  });

  $("#searchApplySupplierBtn").click(function(){
    $("#searchApplySupplierForm").submit();
  });

  $("#searchApplySupplierForm .search_word").live("focus",function(){
    $("#cityDiv").dialog({
      width: 580,
      height:360,
      modal: true,
      draggable:false,
      title:"请选择城市",
//      resizable:false,
      position:'center',
      open: function() {
//        $(".ui-dialog-titlebar", $(this).parent()).hide();
//        $(this).removeClass("ui-dialog-content").removeClass("ui-widget-content");
      }
    });
  });

  APP_BCGOGO.Net.asyncAjax({
    url: "shop.do?method=getAllAreaToCity",
    type: "POST",
    cache: false,
    dataType: "json",
    success: function (provinceList) {
      if(G.isEmpty(provinceList)){
        return;
      }
      for(var i=0;i<provinceList.length;i++){
        var province=provinceList[i];
        $(".pro_item").each(function(){
          if(province.name.indexOf($(this).text())>=0){
            $(this).attr("no",province.no);
            var city_items="";
            var cityList=province.childAreaDTOList;
            city_items+='<div style="display: none" class="hide_content">';
            city_items+='<div class="t_prov" no="'+province.no+'">'+province.name+'</div>'
            for(var j=0;j<cityList.length;j++){
              var city=cityList[j];
              city_items+='<a class="city_item" no="'+city.no+'">'+city.name+'</a>';
            }
            city_items+='</div>';
            $(this).append(city_items);
          }
        });
      }
    }
  });

  $(".pro_item").live("click",function(){
    $(".hide_content").hide();
   var $h_content=$(this).find(".hide_content");
    if($h_content.css("display")=="block"){
      $h_content.hide();
    }else{
      if($(this).closest(".c_right").length>0){
          $h_content.find(".t_prov").css("text-align","right");
         $h_content.css("left",-266);
      }else{
        $h_content.find(".t_prov").css("text-align","left");
         $h_content.css("left",12);
      }
      $h_content.show();
    }
  });

  $(".pro_item").live("mouseleave",function(){
    $(this).find(".hide_content").hide();
  });
//
//  $(".hide_content").live("mouseleave",function(){
//    $(this).hide();
//  });

  $(".city_item,.t_prov").live("click",function(){
    $("#searchApplySupplierForm .search_word").val($(this).text());
    $("#searchApplySupplierForm [name='area_code']").val($(this).attr("no"));
    $("#cityDiv").dialog("close");
    $("#searchApplySupplierBtn").click();
  });

//  $("#emptyCityBtn").click(function(){
//    $("#searchApplySupplierForm .search_word").val("");
//    $("#searchApplySupplierForm [name='area_code']").val("");
//    $("#cityDiv").dialog("close");
//  });

});

function purchaseProduct(productLocalInfoIds,amounts){
  APP_BCGOGO.Net.syncPost({
    url: "autoAccessoryOnline.do?method=validatorPurchaseProduct",
    dataType: "json",
    data: {
      productLocalInfoIds: productLocalInfoIds,
      amounts:amounts
    },
    success: function (json) {
      if (json.success) {
        window.location = "RFbuy.do?method=createPurchaseOrderOnlineByProduct&paramString=" + json.data.join(",");
      } else {
        nsDialog.jAlert(json.msg);
      }
    },
    error: function () {
      nsDialog.jAlert("数据异常!");
    }
  });
}

//function createPurchaseOrder(quotedPreBuyOrderItemIds,_blank){
//    if (G.isEmpty(quotedPreBuyOrderItemIds)) {
//        return;
//    }
//    if(_blank){
//        window.open("RFbuy.do?method=createPurchaseOrderOnlineByQuotedPreBuyOrder&quotedPreBuyOrderItemIds="+quotedPreBuyOrderItemIds);
//    }else{
//        window.location.href="RFbuy.do?method=createPurchaseOrderOnlineByQuotedPreBuyOrder&quotedPreBuyOrderItemIds="+quotedPreBuyOrderItemIds;
//    }
//}

function addProductToShoppingCart(paramString){
  if(!G.Lang.isEmpty(paramString)){
    APP_BCGOGO.Net.syncPost({
      url: "shoppingCart.do?method=addProductToShoppingCart",
      dataType: "json",
      data: {
        paramString:paramString
      },
      success: function (json) {
        updateShoppingCartNumber();
        $("#resultMsg").text(json.data.resultMsg);
        $("#warnMsg").text(json.data.warnMsg);
        $("#shoppingCartItemCount").text(json.data.shoppingCartItemCount);
        $("#shoppingCartTotal").text(json.data.shoppingCartTotal);
        $("#goShoppingCartBtn").text(json.data.goShoppingCartBtnName);
        $("#returnDialog").dialog({
          width: 333,
          minHeight:140,
          modal: true,
          resizable:false,
          position:'center',
          open: function() {
            $(".ui-dialog-titlebar", $(this).parent()).hide();
            $(this).removeClass("ui-dialog-content").removeClass("ui-widget-content");
          }
        });
//                if(userGuide.currentStepName == "PRODUCT_PRICE_GUIDE_PURCHASE"){
//                    userGuide.funLib["PRODUCT_PRICE_GUIDE"]["_PRODUCT_PRICE_GUIDE_PURCHASE_STEP3_2"]();
//                }
      },
      error: function () {
        nsDialog.jAlert("数据异常!");
      }
    });
  }
}


function initRecommendAccessory(json) {
  var size = 10;

  if (json == null || json.length == 0) {
    $("#recommendProductDiv").css("display", "none");
    return;
  }

  var length = json.length;
  var liSize = length < size ? 1 : parseInt((length / size));
  var str = '<ul class="JScrollGroup">';
  for (var liIndex = 0; liIndex < liSize; liIndex ++) {
    str += '<li id="li_' + liIndex + '" class="JScrollItem"></li>';
  }
  str += '</ul>';
  $("#recommendProductDiv").html(str);


  for (var liIndex = 0; liIndex < liSize; liIndex ++) {
    var liHtml = '';
    liHtml += '<div class="lineTop"></div>' +
      '<span class="cart_title accessories_title"></span>' +
      '<div class="cartBody lineBody newBody">' +
      '<table class="tab_cuSearch tabSales tab_cart" cellpadding="0" cellspacing="0">' +
      '<col><col width="210"><col width="100"><col width="85">';

    for (var index = 0; index < size; index ++) {
      var s = liIndex * size + index;
      if (s >= length) {
        continue;
      }
      var productDTO = json[s];
      var productLocalInfoIdStr = productDTO.productLocalInfoIdStr;
      var productInfo = productDTO.productInfo;
      var shopIdStr = productDTO.shopIdStr;
      var shopName = productDTO.shopName;
      var isLocalCity = productDTO.localCity;
      var shopAreaInfo = productDTO.shopAreaInfo;
      var inSalesPrice = G.Lang.normalize(productDTO.inSalesPrice, "0");
      var unit = G.Lang.isEmpty(productDTO.unit) ? "" : ("/" + productDTO.unit);
      var tr = '<tr>';
      tr += '<td style="padding-left:50px;" class="promotions_info_td">供应&nbsp;' + productInfo;

      var promotionsList=productDTO.promotionsDTOs;
      var promotionsing=!G.isEmpty(promotionsList);  //判断商品是否正在促销
      if(promotionsing){
        var pTitle=generatePromotionsAlertTitle(productDTO,"search");
        tr += pTitle;
      }
      tr += '</td>';
      tr += '<td><div class="line"><a class="blue_color" href="shopMsgDetail.do?method=renderShopMsgDetail&paramShopId='+shopIdStr+'">' + shopName +'</a></div>';
      if (isLocalCity || isLocalCity == "true") {
        tr += '<span class="yellow_color">（' + shopAreaInfo + '）</span>';
      } else {
        tr += '<span class="gray_color">（' + shopAreaInfo + '）</span>';
      }
      tr += '</td>';
      tr += '     <td><span class="yellow_color">' + inSalesPrice + '</span>&nbsp;元' + unit + '</td>';
      tr += '     <td><a class="addCart J_addToShoppingCartSingleBtn" data-suppliershopid="' + shopIdStr + '" data-productlocalinfoid="' + productLocalInfoIdStr + '">加入购物车</a></td>';
      tr += '</tr>';
      liHtml += tr;
    }
    liHtml += '</table>' +
      '<a class="blue_color more" onclick="toCommodityQuotations()">更多配件报价>></a>' +
      '</div>' +
      '<div class="lineBottom"></div>';
    $("#li_" + liIndex).html(liHtml);
  }
  $("#recommendProductDiv").css("display", "block");


  var scrollFlowVertical = new App.Module.ScrollFlowVertical();
  scrollFlowVertical.init({
    "selector": "#recommendProductDiv",
    "width": 820,
    "height": length <= size ? (((length +2)  * 45) - 15) : 520,
    "background": "#fff",
    "onNextComplete": function () {

    },
    "onPrevComplete": function () {

    }
  }).startAutoScroll();
  window.scrollFlowVertical = scrollFlowVertical;
}


function initLastWeekSalesInventoryStat(json) {
  var size = 8;
  var hTitle = '';
  hTitle += '<div class="last_statistics">' +
    '<div class="statistics_top"></div>' +
    '<div class="statistics_body"><div class="statistics_title_top">感兴趣商品销售统计</div>';
  if (G.isEmpty(json)||json[0]==null) {
    hTitle+='<div style="padding-top: 10px;padding-left: 20px;">暂无数据</div>';
    $("#lastWeekStatDiv").html(hTitle);
    $("#lastWeekStatDiv").show();
    return;
  }
  var length = json[0].length;
  var liSize = length % size == 0 ? parseInt((length / size))  : parseInt((length / size)) + 1;
  var str = '<ul class="JScrollGroup">';
  for (var liIndex = 0; liIndex < liSize; liIndex ++) {
    str += '<li id="lastWeekLi_' + liIndex + '" class="JScrollItem"></li>';
  }
  str += '</ul>';
  $("#lastWeekStatDiv").html(str);
  for (var liIndex = 0; liIndex < liSize; liIndex ++) {
    var liHtml = '';
    liHtml += hTitle;
    var str = "";
    for (var index = 0; index < size; index ++) {
      var s = liIndex * size + index;
      if (s >= length) {
        continue;
      }
      if (index == 0) {
        str += '<ul class="statistics_list">';
      }

      var resultDTO = json[0][s];
      var productName = resultDTO.productName;
      var productBrand = resultDTO.productBrand;
      var salesAmount = resultDTO.salesAmount;
      var salesUnit = resultDTO.salesUnit;
      var inventoryAmount = resultDTO.inventoryAmount;
      var inventoryUnit = resultDTO.inventoryUnit;
      if (!G.Lang.isEmpty(resultDTO.productBrand)) {
        productName += productBrand;
      }
      str += '<li><span title="' + productName + '" style="width:90px; display:inline-block; color:#272727;margin-right:5px">'
        + (productName.length > 6 ? (productName.substring(0, 6) + "...") : productName);

      str += '</span><span style="width:80px; display:inline-block;margin-right:5px">采购量&nbsp;<span class="red_color">'
        + inventoryAmount + '</span>' + '</span><span style="width:80px; display:inline-block;">销售量&nbsp;<span class="green_color">' + salesAmount + '</span>' + '</span></li>';
      if (index == 3) {
        str += '</ul>';
        str += '<ul class="statistics_list">';
      }
      if (index == 7) {
        str += '</ul>';
        str += '<ul class="statistics_list">';
      }
      if (index == 11) {
        str += '</ul>';
      }

    }

    str +='</div><div class="statistics_bottom"></div></div><div class="clear i_height"></div>';

    liHtml += str;
    $("#lastWeekLi_" + liIndex).html(liHtml);
  }
  $("#lastWeekStatDiv").css("display", "block");
}

function initWholesalerPreBuyOrderRecommend(json) {
  var size =10;
  if (json == null || json[0] == null || json[0].length == 0) {
//        $("#preBuyItemRecommendDiv").css("display", "none");
    $(".preBuyItemRecommendContainer").hide();
    return;
  }
  var length = json[0].length;
  var liSize = length % size == 0 ? parseInt((length / size))  : parseInt((length / size)) + 1;
  var str = '<div class="JScrollGroup">';
  for (var liIndex = 0; liIndex < liSize; liIndex ++) {
    str += '<div id="li_' + liIndex + '" class="JScrollItem"><div class="prev JScrollPrevButton"></div><div class="next JScrollNextButton"></div></div>';
  }
  str += '</div>';
  $("#preBuyItemRecommendDiv").html(str);
  for (var liIndex = 0; liIndex < liSize; liIndex ++) {
    var liHtml = '';
    liHtml +='<div class="cartBody newBody">' +
      '<table class="tab_cuSearch tabSales tab_cart" cellpadding="0" cellspacing="0">' +
      '<col><col width="350"><col width="350">';

    for (var index = 0; index < size; index ++) {
      var s = liIndex * size + index;
      if (s >= length) {
        continue;
      }
      var preBuyOrderItemDTO = json[0][s];
      var productName = G.Lang.isEmpty(preBuyOrderItemDTO.productName) ? "" : preBuyOrderItemDTO.productName;
      var brand = G.Lang.isEmpty(preBuyOrderItemDTO.brand) ? "" : preBuyOrderItemDTO.brand;
      var model = G.Lang.isEmpty(preBuyOrderItemDTO.model) ? "" : preBuyOrderItemDTO.model;
      var spec = G.Lang.isEmpty(preBuyOrderItemDTO.spec) ? "" : preBuyOrderItemDTO.spec;
      var vehicleBrand = G.Lang.isEmpty(preBuyOrderItemDTO.vehicleBrand) ? "" : preBuyOrderItemDTO.vehicleBrand;
      var vehicleModel = G.Lang.isEmpty(preBuyOrderItemDTO.vehicleModel) ? "" : preBuyOrderItemDTO.vehicleModel;
      var productInfoStr = "";
      if(productName != ""){
        productInfoStr += productName + " ";
      }
      if(brand != ""){
        productInfoStr += brand + " ";
      }
      if (model != "" && spec != "") {
        productInfoStr += model + "/" + spec;
      } else if (model != "") {
        productInfoStr += model;
      } else if (spec != "") {
        productInfoStr += spec;
      }
      if(vehicleBrand != ""){
        productInfoStr += vehicleBrand + " ";
      }
      if(vehicleModel != ""){
        productInfoStr += vehicleModel;
      }
      var preBuyOrderItemId = preBuyOrderItemDTO.idStr;
      var shopName = preBuyOrderItemDTO.shopName;
      var shopIdStr = preBuyOrderItemDTO.shopIdStr;
      var shopAreaInfo = preBuyOrderItemDTO.shopAreaInfo.replace("省", "");
      var vestDateStr = preBuyOrderItemDTO.vestDateStr;
      var businessChanceTypeStr = preBuyOrderItemDTO.businessChanceTypeStr;
      var businessChanceType = preBuyOrderItemDTO.businessChanceType;
      var changeClass='';
      switch(businessChanceType){
        case 'Normal':
          changeClass='yellow_color';
          break;
        case 'SellWell':
          changeClass='green_color';
          break;
        case 'Lack':
          changeClass='red_color';
          break;
        default:
          changeClass='yellow_color'
      }
      var endDateCount =G.rounding(preBuyOrderItemDTO.endDateCount);
      var isLocalCity = preBuyOrderItemDTO.localCity;
      var imageSmallURL = preBuyOrderItemDTO.imageCenterDTO.recommendProductListImageDetailDTO.imageURL;
      var tr='<tr>';
      tr+='<div class="purchase">'+
        '<div class="pic" style="cursor: pointer" onclick="showBuyInformationDetail(\''+preBuyOrderItemId+'\')"><img src="'+imageSmallURL+'"/></div>'+
        '<div class="right p-right">'+
        '<div class="p-info-detail text-overflow" onclick="showBuyInformationDetail(\''+preBuyOrderItemId+'\')">[<a class="'+changeClass+'">'+businessChanceTypeStr+'</a>]<span title="'+productInfoStr+'">'+productInfoStr+'</span></div>'+
        '<div class="p-info-detail text-overflow" onclick="renderShopMsgDetail(\''+shopIdStr+'\',\''+"true"+'\')">[<a class="yellow_color">买家</a>] '+shopName+'</div>'+
        '<div>'+shopAreaInfo+'</div>'+
        '<div><div onclick="showBuyInformationDetail(\''+preBuyOrderItemId+'\')" class="accessories-btn fr" style="margin-top:5px;">查看详细</div><span class="yellow_color">还剩'+endDateCount+'天有效</span></div>'+
        '</div>'+
        '</div>';
      tr += '</tr>';
      liHtml += tr;
    }
    liHtml += '</table>' +
      '</div>' +
      '<div class="lineBottom"></div>';
    $("#li_" + liIndex).append(liHtml);
  }
//    $("#preBuyItemRecommendDiv").css("display", "block");
  $(".preBuyItemRecommendContainer").show();
  var scrollFlowHorizontal = new App.Module.ScrollFlowHorizontal();
  scrollFlowHorizontal
    .init({
    "selector": "#preBuyItemRecommendDiv",
    "width":792,
    "height": 540,
    "background": "#FFFFFF",
    "scrollInterval":5000
  }).startAutoScroll();
  window.scrollFlowHorizontal = scrollFlowHorizontal;
}

function initFamousSupplier(json) {
  if (json == null || (json && !json.success) || json.data.length == 0) {
    $("#famousSupplierSpan").css("display", "none");
    return;
  }
  $("#famousSupplierSpan").css("display", "block");

  var shopDTOs = json.data;
  var tr = '<div class="leftTit">明星供应商</div>';
  for (var i = 0; i < shopDTOs.length; i++) {
    var shop = shopDTOs[i];
    var idStr = shop.idStr;
    var name = shop.name;
    var areaName = shop.areaName;
    tr += '<div class="list">' +
      '<a class="blue_color" href="supplier.do?method=redirectSupplierComment&paramShopId=' + idStr + '">' + name + '</a><span class="address gray_color">（' + areaName + '）</span></div>';
  }
  $("#famousSupplierSpan").css("display", "block");
  $("#famousSupplierSpan").html(tr);
}

function initRecommendShop(json) {
  if (json == null || (json && !json.success)) {
    return;
  }
  var shopDTOs = json.data;
  var shopStr = '';
  for (var i = 0; i < shopDTOs.length; i++) {
    var shop = shopDTOs[i];
    var shopIdStr = shop.idStr;
    var name =G.normalize(shop.name);
    var nameShort= name.length>5?name.substr(0,10)+'...':name;
    var areaName = shop.areaName;
    var businessScopeStr = G.isEmpty(shop.businessScopeStr) ? "暂无信息" : shop.businessScopeStr;
    var imageSmallURL = shop.imageCenterDTO.shopSmallMainImageDetailDTO.imageURL;
//        var shortBusinessScope = businessScopeStr.length > 32 ? businessScopeStr.substring(0, 32) + '...' : businessScopeStr;
    var isLicenced=shop.licensed;
    shopStr+='<div class="clients text-overflow">'+
      '<div class="pic" onclick="renderShopMsgDetail(\''+shopIdStr+'\',\''+"true"+'\')"><img src="'+imageSmallURL+'"/></div>'+
      '<div class="right">'+
      '<div class="right_h1"><a class="blue_color text-overflow" title="'+name+'" onclick="renderShopMsgDetail(\''+shopIdStr+'\',\''+"true"+'\')">'+nameShort+'</a></div>'+
      '<div>'+areaName+'</div>';
    if(isLicenced){
      shopStr+= '<div class="red-bg">已认证</div>';
    }else{
      shopStr+= '<div class="red-bg2">未认证</div>';
    }
    shopStr+= '</div>'+
      '<div class="clear"></div>'+
      '经营范围：<span class="gray_color" title="'+businessScopeStr+'">'+businessScopeStr+'</span>'+
      '</div>';
  }
  $(".recommend-shop-div").append(shopStr);
  $('#recommendShopDiv').show()
}

//function showBuyInformationDetail(preBuyOrderItemId){
//    if(G.isEmpty(preBuyOrderItemId)){
//        return;
//    }
//    window.location.href='preBuyOrder.do?method=showBuyInformationDetailByPreBuyOrderItemId&preBuyOrderItemId='+preBuyOrderItemId;
//}

function createPurchaseOrder(quotedPreBuyOrderItemIds,_blank){
  if (G.isEmpty(quotedPreBuyOrderItemIds)) {
    return;
  }
  if(_blank){
    window.open("RFbuy.do?method=createPurchaseOrderOnlineByQuotedPreBuyOrder&quotedPreBuyOrderItemIds="+quotedPreBuyOrderItemIds);
  }else{
    window.location.href="RFbuy.do?method=createPurchaseOrderOnlineByQuotedPreBuyOrder&quotedPreBuyOrderItemIds="+quotedPreBuyOrderItemIds;
  }
}


function preBuyInformationByShop(name){
   window.location.href="preBuyOrder.do?method=preBuyInformation&shopName="+name;
}


