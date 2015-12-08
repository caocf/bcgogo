var bcgogoAjaxQuery = APP_BCGOGO.Module.wjl.ajaxQuery;
$(function() {
  $(".blue_color").hover();
  $(".blue_color").live("mouseover",function(){
    $(this).css({"color":"#fd5300","textDecoration":"underline"});
  });
  $(".blue_color").live("mouseout",function(){
    $(this).css({"color":"#0094ff","textDecoration":"none"});
  });
//  provinceBind();
  $("#provinceNo").bind("change",function(){
    $(this).css("color", $(this).find("option:selected").css("color"));
    $("#cityNo option").not(".default").remove();
    $("#regionNo option").not(".default").remove();
    cityBind();
    $("#cityNo").change();
  });
  $("#cityNo").bind("change",function(){
    $(this).css("color", $(this).find("option:selected").css("color"));
    $("#regionNo option").not(".default").remove();
    regionBind();
    $("#regionNo").change();
  });
  $("#regionNo").bind("change",function(){
    $(this).css("color", $(this).find("option:selected").css("color"));
  });

  $(".J_supplierOnlineSuggestion")
    .bind('click', function () {
      getCustomerOrSupplierOnlineSuggestion($(this));
    })
    .bind('keyup', function (event) {
      var eventKeyCode = event.which || event.keyCode;
      if (GLOBAL.Interactive.keyNameFromKeyCode(eventKeyCode).search(/left|up|right|down/g) == -1) {
        getCustomerOrSupplierOnlineSuggestion($(this));
      }
    });
  function getCustomerOrSupplierOnlineSuggestion($domObject) {
    var searchWord = $domObject.val().replace(/[\ |\\]/g, "");
    var dropList = APP_BCGOGO.Module.droplist;
    dropList.setUUID(GLOBAL.Util.generateUUID());
    var ajaxData = {
      searchWord: searchWord,
      customerOrSupplier:"supplierOnline",
      shopRange:"notRelated",
      uuid: dropList.getUUID()
    };
    var ajaxUrl = "searchInventoryIndex.do?method=getCustomerSupplierOnlineSuggestion";
    APP_BCGOGO.wjl.LazySearcher.lazySearch(ajaxUrl, ajaxData, function (result) {
      dropList.show({
        "selector":$domObject,
        "data":result,
        "onSelect":function (event, index, data) {
          $domObject.val(data.label);
          $domObject.css({"color":"#000000"});
          dropList.hide();
        }
      });
    });

  }

  $("#searchBtn").bind("click",function(){
    applySupplierData();
  });

  $(".applySupplier").live("click", function () {
    if ($(this).attr("lock") || !$(this).attr("shopId")) {
      return;
    }
    $(this).attr("lock", true);
    var $thisDom =  $(this);
    var url = "apply.do?method=applySupplierRelation";
    var data = {"supplerShopId":$(this).attr("shopId")};
    bcgogoAjaxQuery.setUrlData(url, data);
    bcgogoAjaxQuery.ajaxQuery(function (result) {
      userGuide.clear();
      if (result.success) {
        userGuide.clear();
        nsDialog.jAlert( "您的申请提交成功，请等待对方同意！","", function () {
          $thisDom.addClass("showSupplierGuideSingleApplySuccess");
          userGuideInvoker("CONTRACT_SUPPLIER_GUIDE_SINGLE_APPLY_SUCCESS,CONTRACT_SUPPLIER_GUIDE_BATCH_APPLY_SUCCESS");
        });


        $thisDom.removeClass("blue_color").removeAttr("style")
          .addClass("gray_color")
          .html("已提交关联申请");
        $thisDom.parent().parent().find("input:checkbox[name$='supplierShopId']").parent().html('<img src="images/disabledSelect.png">');
      } else {
        nsDialog.jAlert(result.msg);
        $thisDom.removeAttr("lock");
      }
    });
  });
  $(".checkAll").bind("change", function () {
    var isChecked = $(this).attr("checked");
    $("input[name$='supplierShopId']").attr("checked", isChecked);
  });
  $("input[name$='supplierShopId']").live("click", function () {
    checkAllCheckBox();
  });
  $("#applySupplierBtn").bind("click", function () {
    if ($("input:checkbox[name$='supplierShopId'][checked=true]").size() == 0) {
      nsDialog.jAlert("请选择想要关联的供应商！");
      return;
    }
    if ($(this).attr("lock")) {
      return;
    }
    $(this).attr("lock", true);
    var supplierShopId = "";
    $("input:checkbox[name$='supplierShopId'][checked=true]").each(function (index, check) {
      if ($(this).val()) {
        supplierShopId += $(this).val();
        supplierShopId += ",";
      }
    });
    var url = "apply.do?method=applySupplierRelation";
    var data = {"supplerShopId": supplierShopId};
    bcgogoAjaxQuery.setUrlData(url, data);
    bcgogoAjaxQuery.ajaxQuery(function (result) {
      if (result.success) {
        userGuide.clear();
        shadow.clear();
        nsDialog.jAlert("您的申请提交成功，请等待对方同意！","",function(){
        });

        pagingAjaxPostForUpAndDownFunction["_ApplySupplier"]["flush"]();
      } else {
        nsDialog.jAlert(result.msg);
      }
      $("#applySupplierBtn").removeAttr("lock");
    }, function () {
      nsDialog.jAlert("网络异常，请联系客服！")
      $("#applySupplierBtn").removeAttr("lock");
    });
  });

  $(".J-productSuggestion").bind('click', function () {
    productSuggestion($(this));
  }).bind('keyup', function (event) {
      var eventKeyCode = event.which || event.keyCode;
      if (GLOBAL.Interactive.keyNameFromKeyCode(eventKeyCode).search(/left|up|right|down/g) == -1) {
        productSuggestion($(this));
      }
    })
    .bind("change",function(){
      $("#productId").val("");
    });


  //init
  if(!G.Lang.isEmpty($("#initProvinceNo").val())){
    $("#provinceNo").val($("#initProvinceNo").val());
    $("#provinceNo").change();
  }
  if(!G.isEmpty(area_code)){
    $("#provinceNo").val(area_code.substr(0,4));
     $("#provinceNo").change();
    $("#cityNo").val(area_code);
     $("#cityNo").change();
  }
  resetCheckedClassByThirdCategoryIdStr($("#thirdCategoryIdStr").val());
  applySupplierData();
});

function productSuggestion($domObject) {
  var searchWord = $domObject.val().replace(/[\ |\\]/g, "");
  var dropList = APP_BCGOGO.Module.droplist;
  dropList.setUUID(GLOBAL.Util.generateUUID());
  var currentSearchField =  $domObject.attr("searchField");
  var ajaxData = {
    searchWord: searchWord,
    searchField: currentSearchField,
    salesStatus: $("#salesStatus").val(),
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

function applySupplierData() {
  resetThirdCategoryIdStr();
  var ajaxUrl = "apply.do?method=searchApplySuppliers";
  var data = getSearchData();
  APP_BCGOGO.Net.syncPost({
    url: ajaxUrl,
    data: data,
    dataType: "json",
    success: function (json) {
      initApplySupplierDataTr(json);
      initUpAndDownPage(json, "_ApplySupplier", ajaxUrl, 'initApplySupplierDataTr', data);
    }
  });
}

function provinceBind() {
  var r = APP_BCGOGO.Net.syncGet({url: "shop.do?method=selectarea",
    data: {"parentNo":"1"}, dataType: "json"});
  if (!r || r.length == 0){
    return;
  }else {
    for (var i = 0, l = r.length; i < l; i++) {
      var option = $("<option>")[0];
      option.value = r[i].no;
      option.style.color="#000000";
      option.innerHTML = r[i].name;
      $("#provinceNo")[0].appendChild(option);
    }
  }
}

function cityBind() {
  var r = APP_BCGOGO.Net.syncGet({url: "shop.do?method=selectarea",
    data: {"parentNo": $("#provinceNo").val()}, dataType: "json"});
  if (!r || r.length == 0) return;
  else {
    for (var i = 0, l = r.length; i < l; i++) {
      var option = $("<option>")[0];
      option.value = r[i].no;
      option.style.color="#000000";
      option.innerHTML = r[i].name;
      $("#cityNo")[0].appendChild(option);
    }
  }
}

function regionBind() {
  var r = APP_BCGOGO.Net.syncGet({url: "shop.do?method=selectarea",
    data: {"parentNo": $("#cityNo").val()}, dataType: "json"});
  if (!r || r.length == 0) return;
  else {
    for (var i = 0, l = r.length; i < l; i++) {
      var option = $("<option>")[0];
      option.value = r[i].no;
      option.style.color="#000000";
      option.innerHTML = r[i].name;
      $("#regionNo")[0].appendChild(option);
    }
  }
}

function initApplySupplierDataTr(json){
  $("#applySupplierData tr").not('.titleBg').remove();
  var showSupplierGuideSingleApplySuccess = true;
  var showSupplierGuideSingleApply = true;
  if(json && json.shopDTOs && json.shopDTOs.length>0){
    for(var i= 0,len = json.shopDTOs.length;i<len;i++){
      var shopDTO = json.shopDTOs[i];
      var shopId = G.Lang.normalize(shopDTO.shopIdStr,"");
      var shopIdStr = "'" + shopId + "'";

      var totalAverageScore = (shopDTO.totalAverageScore ? App.StringFilter.priceFilter(shopDTO.totalAverageScore, 1) : "0");
      var qualityAverageScore = (shopDTO.qualityAverageScore ? App.StringFilter.priceFilter(shopDTO.qualityAverageScore, 1) : "0");
      var performanceAverageScore = (shopDTO.performanceAverageScore ? App.StringFilter.priceFilter(shopDTO.performanceAverageScore, 1) : "0");
      var speedAverageScore = (shopDTO.speedAverageScore ? App.StringFilter.priceFilter(shopDTO.speedAverageScore, 1) : "0");
      var attitudeAverageScore = (shopDTO.attitudeAverageScore ? App.StringFilter.priceFilter(shopDTO.attitudeAverageScore, 1) : "0");
      var commentRecordCount = (shopDTO.commentRecordCount ? App.StringFilter.priceFilter(shopDTO.commentRecordCount, 1) : "0");
      var totalAverageScoreStr;
      if (totalAverageScore == 0 || totalAverageScore == "0") {
        totalAverageScoreStr = "暂无";
      } else {
        totalAverageScoreStr = totalAverageScore + '分';
      }
      var totalAverageSpan = 0 - parseInt((5 - totalAverageScore) / 0.5) * 19;

      var businessScopeStr = stringUtil.isEmpty(shopDTO.businessScope) ? "暂无信息" : shopDTO.businessScope;
      var shortBusinessScope = businessScopeStr.length > 55 ? businessScopeStr.substring(0, 55) + '...' : businessScopeStr;
      var shopVehicleBrandModelStr = stringUtil.isEmpty(shopDTO.shopVehicleBrandModelStr) ? "暂无信息" : shopDTO.shopVehicleBrandModelStr;
      var shopVehicleBrandModel = shopVehicleBrandModelStr.length > 55 ? shopVehicleBrandModelStr.substring(0, 55) + '...' : shopVehicleBrandModelStr;

      var tr = '<tr class="titBody_Bg">';
      if (shopDTO.inviteStatus && shopDTO.inviteStatus == 'PENDING') {
        tr += '<td style="padding-left:10px;"><img src="images/disabledSelect.png"></td>';
      } else {
        tr += '<td style="padding-left:10px;"><input name="supplierShopId" type="checkbox" value="'+shopId+'"></td>'
      }
      var idStr = "relatedSupplier" + i;
      tr += '    <td style="padding-left:10px;">';
      tr += '        <div class="divStar" style="width: 250px"><a class="blue_color" title="' + shopDTO.name + '" href ="#" onclick="redirectShopCommentDetail(' + shopIdStr + ')">'+shopDTO.name+'</a></div>';
      tr += '         <div class="divStar" id="' + idStr + '" onmouseover="showSupplierCommentScore(this' + ',' + totalAverageScore + ',' + commentRecordCount + ',' + qualityAverageScore + ',' + performanceAverageScore + ',' + speedAverageScore + ',' + attitudeAverageScore+');" onmouseout="scorePanelHide();" onclick="redirectShopCommentDetail(\'' + shopId + '\')">';
      tr += '         <span class="picStar"  style="background-position: 0px ' + totalAverageSpan + 'px;"></span>';
      tr += '         <a class="yellow_color">' + totalAverageScoreStr + '</a>&nbsp;';
      tr += '         <span class="gray_color">共' + commentRecordCount + '人评分</span>';
      tr + '          </div> ';
      tr += '    </td>';
      tr += '    <td style="padding-left:10px;">'+ shopDTO.address+'</td>';
      tr += '    <td style="padding-left:10px;" title="'+businessScopeStr+'">'+shortBusinessScope+'</td>';


      tr += '    <td style="padding-left:10px;" title="'+shopVehicleBrandModelStr+'">'+shopVehicleBrandModel+'</td>';


      if(APP_BCGOGO.Permission.SupplierManager.SupplierApplyAction){
        if (shopDTO.inviteStatus && shopDTO.inviteStatus == 'PENDING') {
          tr += '<td style="padding-left:10px;"><a class="gray_color ' + (showSupplierGuideSingleApplySuccess ? " showSupplierGuideSingleApplySuccess " : "") + '">已提交关联申请</a></td>';
          showSupplierGuideSingleApplySuccess = false;
        }else if(shopDTO.inviteStatus && shopDTO.inviteStatus == 'OPPOSITES_PENDING'){
          tr += '<td style="padding-left:10px;"><a class="blue_color applySupplier OPPOSITES_PENDING' +  '" shopId = "' + shopId + '">申请关联</a></td>';
        } else {
          tr += '<td style="padding-left:10px;"><a class="blue_color applySupplier ' + (showSupplierGuideSingleApply ? " showSupplierGuideSingleApply " : "") + '" shopId = "' + shopId + '">申请关联</a></td>';
          showSupplierGuideSingleApply = false;
        }
      } else{
        if (shopDTO.inviteStatus && shopDTO.inviteStatus == 'PENDING') {
          tr += '<td style="padding-left:10px;"><a class="gray_color">已提交关联申请</a></td>';
          showSupplierGuideSingleApplySuccess = false;
        }else if(shopDTO.inviteStatus && shopDTO.inviteStatus == 'OPPOSITES_PENDING'){
          tr += '<td style="padding-left:10px;"><a class="blue_color">申请关联</a></td>';
        } else {
          tr += '<td style="padding-left:10px;"><a class="blue_color">申请关联</a></td>';
          showSupplierGuideSingleApply = false;
        }
      }
      tr += '</tr>';
      $("#applySupplierData").append($(tr));
      $("#applySupplierData").append('<tr class="titBottom_Bg"><td colspan="5"></td></tr>');
    }
  }else{
    var tr = '<tr class="titBody_Bg"><td colspan="5" style="padding-left:10px;">对不起，本区域暂无与您合适的推荐供应商！</td></tr>';
    $("#applySupplierData").append($(tr));
    $("#applySupplierData").append('<tr class="titBottom_Bg"><td colspan="5"></td></tr>');
  }
  checkAllCheckBox();
}

function checkAllCheckBox() {
  if($("input[name$='customerShopId']").size() == 0){
    $(".checkAll").attr("checked", false);
  }else if ($("input:checkbox[name$='customerShopId'][checked=true]").size() < $("input[name$='customerShopId']").size()) {
    $(".checkAll").attr("checked", false);
  } else if ($("input:checkbox[name$='customerShopId'][checked=true]").size() > 0) {
    $(".checkAll").attr("checked", true);
  }
}

function getSearchData() {
  var $_pushMessageId = $("#pushMessageId"), data = {
    "currentPage": 1,
    "name": $("#name").val(),
    "keyword": $("#keyword").val(),
    "pushMessageId": $_pushMessageId.val(),
    "provinceNo": $("#provinceNo").val(),
    "cityNo": $("#cityNo").val(),
    "regionNo": $("#regionNo").val(),
    "thirdCategoryIdStr":$("#thirdCategoryIdStr").val(),
    "brandName":$("#vehicles0\\.vehicleBrand").val(),
    "modelName":$("#vehicles0\\.vehicleModel").val()
  };
  $_pushMessageId.val("");
  return data;
}