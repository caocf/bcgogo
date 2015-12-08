;
var newProductCache = {};
$(document).ready(function () {
    /**
     *  cache 当前域单位，车辆信息
     *  timeOut 表示当前key过期时间，-1 表示不cache，0，表示不会过期，>0 表示多少ms后过期
     *  syncTime 时间拿前台时间。
     */
    newProductCache = {

        defaultTimeOut: 1000 * 60 * 10,
        //cache field
        unitSource: [],
        productVehicleBrandSource: {
            timeOut: 1000 * 60 * 10, //过期时间
            data: {"key": {syncTime: 0, source: []}}     //syncTime 同步时间
        }, productVehicleModelSource: {
            timeOut: 1000 * 60 * 10, //过期时间
            data: {"key": {syncTime: 0, source: []}}
        }, productKindSource: {
            timeOut: 1000 * 60 * 10, //过期时间
            data: {"key": {syncTime: 0, source: []}}
        },


        //目前只用来判断  productVehicleBrandSource，productVehicleModelSource 这两个 field  ,true 表示超时，false 表示未超时
        isTimeOut: function (key, cacheField) {
            if (!this[cacheField]) {
                return true;
            }
            if (this[cacheField] && this[cacheField].data && this[cacheField].data[key] && this[cacheField].data[key].syncTime) {
                var lastSyncTime = this[cacheField].data[key]["syncTime"] || 0;
                var maxTimeOut = this[cacheField].timeOut || this.defaultTimeOut || 5000;
                var timestamp = (new Date()).valueOf();
                if (timestamp - lastSyncTime < maxTimeOut) {
                    return false;
                }
            }
            return true;
        }

    };


    //--------------------------------------新增商品逻辑------------------------------------------
    var newProductImageUploader,newProductImageUploaderView;

    $(".J_addNewProduct").bind("click", function () {
        $("#addNewProduct_dialog").dialog({
            title: "新增商品",
            width: 450,
            modal: true,
            resizable: false,
            draggable: false,
            close: function () {
                resetAddNewProduct();
                if(!G.Lang.isEmpty(newProductImageUploaderView)){
                    newProductImageUploaderView.remove();
                    newProductImageUploaderView = null;
                }
                if(!G.Lang.isEmpty(newProductImageUploader)){
                    newProductImageUploader.remove();
                    newProductImageUploader=null;
                }
            },
            open: function() {
                bindNewProductImageInfo();
            }
        })
    });

    function bindNewProductImageInfo(){
        if(!G.Lang.isEmpty(newProductImageUploaderView)){
            newProductImageUploaderView.remove();
            newProductImageUploaderView = null;
        }
        if(!G.Lang.isEmpty(newProductImageUploader)){
            newProductImageUploader.remove();
            newProductImageUploader=null;
        }
        var buttonBgUrl = "images/imageUploader.png";
        var buttonOverBgUrl="images/imageUploader.png";
        newProductImageUploader = new App.Module.ImageUploader();
        newProductImageUploader.init({
            "selector":"#_addNewProductMainImageBtn",
            "flashvars":{
                "debug":"off",
                "maxFileNum":1,
                "width":61,
                "height":24,
                "buttonBgUrl":buttonBgUrl,
                "buttonOverBgUrl":buttonBgUrl,
                "url":APP_BCGOGO.UpYun.UP_YUN_UPLOAD_DOMAIN_URL+"/"+APP_BCGOGO.UpYun.UP_YUN_BUCKET+"/",
                "ext":{
                    "policy":$("#policy").val(),
                    "signature":$("#signature").val()
                }
            },

            "startUploadCallback":function(message) {
                // 设置 视图组件 uploading 状态
                newProductImageUploaderView.setState("uploading");
            },
            "uploadCompleteCallback":function(message, data) {
                var dataInfoJson = JSON.parse(data.info);
                $("#new_imagePath").val(dataInfoJson.url);
            },
            "uploadErrorCallback":function(message, data) {
                var errorData = JSON.parse(data.info);
                errorData["content"] = data.info;
                saveUpLoadImageErrorInfo(errorData);
                nsDialog.jAlert("上传图片失败！");
            },
            "uploadAllCompleteCallback":function() {
                var imagePath = $("#new_imagePath").val();
                // 设置 视图组件  idle 状态
                newProductImageUploaderView.setState("idle");
                //更新input
                var outData = [{
                    "url": APP_BCGOGO.UpYun.UP_YUN_DOMAIN_URL + imagePath + APP_BCGOGO.UpYun.UP_YUN_SEPARATOR + APP_BCGOGO.ImageScene.PRODUCT_LIST_IMAGE_SMALL
                }];
                newProductImageUploaderView.update(outData);
                newProductImageUploader.getFlashObject().deleteFile(0);
                //
                buttonBgUrl = "images/imageUploaderAgain.png";
                buttonOverBgUrl="images/imageUploaderAgain.png";
                newProductImageUploader.getFlashObject().resetAddButton(false, buttonBgUrl, buttonOverBgUrl, 61, 24);
            }
        });
        /**
         * 视图组建
         * */
        newProductImageUploaderView = new App.Module.ImageUploaderView();
        newProductImageUploaderView.init({
            // 你所需要注入的 dom 节点
            selector:"#_newProductMainImageView",
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
                $("#new_imagePath").val("");
                buttonBgUrl = "images/imageUploader.png";
                buttonOverBgUrl="images/imageUploader.png";
                newProductImageUploader.getFlashObject().resetAddButton(false, buttonBgUrl, buttonOverBgUrl, 61, 24);
            }

        });
        if(!G.Lang.isEmpty($("#new_imagePath").val())){
            newProductImageUploaderView.setState("idle");
            var outData = [{
                "url": APP_BCGOGO.UpYun.UP_YUN_DOMAIN_URL + $("#new_imagePath").val() + APP_BCGOGO.UpYun.UP_YUN_SEPARATOR + APP_BCGOGO.ImageScene.PRODUCT_LIST_IMAGE_SMALL
            }];
            newProductImageUploaderView.update(outData);
        }
    }
    /**
     * 商品编码输入校验 APP_BCGOGO.StringFilter.inputtingCommodityCodeFilter
     * blur 校验，不允许同名商品编码
     */

    $("#addNewProduct_dialog [name='new_commodityCode']").live("keyup",function () {
        var pos = APP_BCGOGO.StringFilter.getCursorPosition(this, APP_BCGOGO.StringFilter.inputtingCommodityCodeFilter);
        $(this).val(APP_BCGOGO.StringFilter.inputtingCommodityCodeFilter($(this).val()));
        APP_BCGOGO.StringFilter.setCursorPosition(this, pos);
    }).live("blur", function () {
            var $thisDom = $(this);
            $thisDom.val(APP_BCGOGO.StringFilter.commodityCodeFilter($thisDom.val()));
            var validateCommodityCode = $thisDom.val();
            if ($(this).val() && $(this).val() != $(this).attr("lastValue")) {
                var ajaxUrl = "stockSearch.do?method=ajaxToGetProductByCommodityCode";
                var ajaxData = {
                    commodityCode: validateCommodityCode
                };
                APP_BCGOGO.Net.syncAjax({
                    url: ajaxUrl,
                    dataType: "json",
                    data: ajaxData,
                    success: function (jsonStr) {
                        if (jsonStr[0] && jsonStr[0].productIdStr) {
                            nsDialog.jAlert("商品编码【" + validateCommodityCode + "】已经使用，请重新输入商品编码", null, function () {
                                $thisDom.val("");
                            });
                        }
                    },
                    error: function () {
                        nsDialog.jAlert("网络异常，请联系管理员！", null, function () {
                            $thisDom.val("");
                        })
                    }
                });
            }
        });


    $("#addNewProduct_dialog [name='new_productType']").live("click keyup", function (e) {
        e.stopImmediatePropagation();
        var eventKeyCode = e.which || e.keyCode;
        if (GLOBAL.Interactive.keyNameFromKeyCode(eventKeyCode).search(/left|up|right|down/g) == -1) {
            var $thisDom = $(this);
            var pos = APP_BCGOGO.StringFilter.getCursorPosition($thisDom[0], APP_BCGOGO.StringFilter.inputtingProductNameFilter);
            $thisDom.val(APP_BCGOGO.StringFilter.inputtingProductNameFilter($thisDom.val()));
            APP_BCGOGO.StringFilter.getCursorPosition($thisDom[0], pos);
            var cacheField = $thisDom.attr("cacheField");
            var thisDomVal = $thisDom.val();
            if (newProductCache.isTimeOut($thisDom.val(), cacheField)) {
                var ajaxData = {
                    keyword: thisDomVal,
                    uuid: GLOBAL.Util.generateUUID()
                };
                var ajaxUrl = "stockSearch.do?method=getProductKindsWithFuzzyQuery";
                APP_BCGOGO.wjl.LazySearcher.lazySearch(ajaxUrl, ajaxData, function (jsonStr) {
                    var source = [];
                    if (jsonStr && jsonStr.data && jsonStr.data.length > 0) {
                        for (var i = 0, len = jsonStr.data.length; i < len; i++) {
                            var data = jsonStr.data[i];
                            if (data && !G.Lang.isEmpty(data.label)) {
                                source.push(data.label);
                            }
                        }
                    }
                    newProductCache[cacheField].data[thisDomVal] = {};
                    newProductCache[cacheField].data[thisDomVal]["source"] = source;
                    newProductCache[cacheField].data[thisDomVal]["syncTime"] = (new Date()).valueOf();
                    if ($(document.activeElement).attr("name") == $thisDom.attr("name")) {
                        $thisDom.autocomplete({
                            minLength: 0,
                            delay: 0,
                            source: source
                        });
                        $thisDom.autocomplete("search", "");
                    }
                })
            } else {
                $thisDom.autocomplete({
                    minLength: 0,
                    delay: 0,
                    source: newProductCache[cacheField].data[$thisDom.val()]["source"]
                });
                $thisDom.autocomplete("search", "");
            }
        }
    });
    /**
     * 品名，品牌，规格，型号，输入校验，blur 校验
     */
    $("#addNewProduct_dialog").find("[name='new_name'],[name='new_brand'],[name='new_spec'],[name='new_model'],[name='new_storageBin']")
        .bind("keyup",function () {
            var $thisDom = $(this);
            var pos = APP_BCGOGO.StringFilter.getCursorPosition($thisDom[0], APP_BCGOGO.StringFilter.inputtingProductNameFilter);
            $thisDom.val(APP_BCGOGO.StringFilter.inputtingProductNameFilter($thisDom.val()));
            APP_BCGOGO.StringFilter.getCursorPosition($thisDom[0], pos);
        }).bind("blur", function () {
            var $thisDom = $(this);
            $thisDom.val(APP_BCGOGO.StringFilter.inputtingProductNameFilter($thisDom.val()));
        });


    /**
     * 大小单位输入校验
     */
    $("#addNewProduct_dialog [name='new_sellUnit'],#addNewProduct_dialog [name='new_storageUnit'],#secondUnit,#firstUnit,[name='unit'],.j-promotion-itemUnit").live("keyup",function (e) {
        var eventKeyCode = e.which || e.keyCode;
        if (GLOBAL.Interactive.keyNameFromKeyCode(eventKeyCode).search(/left|up|right|down/g) == -1) {
            var pos = APP_BCGOGO.StringFilter.getCursorPosition(this, APP_BCGOGO.StringFilter.inputtingProductNameFilter);
            $(this).val(APP_BCGOGO.StringFilter.inputtingProductNameFilter($(this).val()));
            APP_BCGOGO.StringFilter.setCursorPosition(this, pos);
            $(this).autocomplete("search", $(this).val());
        }
    }).live("click focus",function (e) {
            if (!newProductCache.unitSource || newProductCache.unitSource.length < 1) {
                $.ajax({
                    type: "POST",
                    url: "shop.do?method=getShopUnit",
                    async: false,
                    cache: false,
                    dataType: "json",
                    error: function (XMLHttpRequest, error, errorThrown) {
                        newProductCache.unitSource = [];
                    },
                    success: function (json) {
                        if (json && json.shopUnitStatus && json.shopUnitStatus == "true") {
                            for (var i = 0; i < json.shopUnitDTOs.length; i++) {
                                newProductCache.unitSource[i] = json.shopUnitDTOs[i].unitName;
                            }
                        }
                    }
                });
            }
            $(this).autocomplete({
                minLength: 0,
                delay: 0,
                source: newProductCache.unitSource,
                select: function (event, ui) {
                    $(this).val(APP_BCGOGO.StringFilter.inputtingCommodityCodeFilter(ui.item.label));
                    newProductCache.unitSource = reSortUnitSource(ui.item.label, newProductCache.unitSource);
                }
            });
            $(this).autocomplete("search", "");
        }).live("blur", function () {
            $(this).val(APP_BCGOGO.StringFilter.inputtingProductNameFilter($(this).val()));
            var thisVal = $(this).val();
            if (!G.Lang.isEmpty(thisVal)) {
                newProductCache.unitSource = reSortUnitSource($.trim(thisVal), newProductCache.unitSource);
            }
            if($(this).attr("name") == "new_sellUnit"){
                $("#addNewProduct_dialog .J_newSellUnitSpan").html(thisVal).attr("title",thisVal);
            }else if($(this).attr("name") == "new_storageUnit"){
                $("#addNewProduct_dialog .J_newStorageUnitSpan").html(thisVal).attr("title",thisVal);
            }
        });


    $("#addNewProduct_dialog [name='new_productVehicleBrand']").bind("keyup click", function (e) {
        e.stopImmediatePropagation();
        var eventKeyCode = e.which || e.keyCode;
        if (GLOBAL.Interactive.keyNameFromKeyCode(eventKeyCode).search(/left|up|right|down/g) == -1) {
            var $thisDom = $(this);
            var pos = APP_BCGOGO.StringFilter.getCursorPosition($thisDom[0], APP_BCGOGO.StringFilter.inputtingProductNameFilter);
            $thisDom.val(APP_BCGOGO.StringFilter.inputtingProductNameFilter($thisDom.val()));
            APP_BCGOGO.StringFilter.getCursorPosition($thisDom[0], pos);
            var searchField = $thisDom.attr("searchField");
            var cacheField = $thisDom.attr("cacheField");
            var thisDomVal = $thisDom.val();
            if (newProductCache.isTimeOut($thisDom.val(), cacheField)) {
                var ajaxData = {
                    searchWord: $thisDom.val(),
                    searchField: searchField,
                    uuid: GLOBAL.Util.generateUUID()
                };
                var ajaxUrl = "product.do?method=searchBrandSuggestion";
                APP_BCGOGO.wjl.LazySearcher.lazySearch(ajaxUrl, ajaxData, function (jsonStr) {
                    var source = [];
                    if (jsonStr && jsonStr.length > 0) {
                        for (var i = 0, len = jsonStr.length; i < len; i++) {
                            if (!G.Lang.isEmpty(jsonStr[i].name)) {
                                source.push(APP_BCGOGO.StringFilter.inputtingProductNameFilter(jsonStr[i].name));
                            }
                        }
                    }
                    newProductCache[cacheField].data[thisDomVal] = {};
                    newProductCache[cacheField].data[thisDomVal]["source"] = source;
                    newProductCache[cacheField].data[thisDomVal]["syncTime"] = (new Date()).valueOf();
                    if ($(document.activeElement).attr("name") == $thisDom.attr("name")) {
                        $thisDom.autocomplete({
                            minLength: 0,
                            delay: 0,
                            source: source
                        });
                        $thisDom.autocomplete("search", "");
                    }
                })
            } else {
                $thisDom.autocomplete({
                    minLength: 0,
                    delay: 0,
                    source: newProductCache[cacheField].data[$thisDom.val()]["source"]
                });
                $thisDom.autocomplete("search", "");
            }
        }
    }).bind("blur",function () {
            if ($(this).val() != $(this).attr("lastVal")) {
                $("#addNewProduct_dialog [name='new_productVehicleModel']").val("");
            }
        }).bind("focus", function () {
            $(this).attr("lastVal", $(this).val());
        });

    $("#addNewProduct_dialog .J_addProduct_addUnit").bind("click", function (e) {
        $(this).hide();
        $("#addNewProduct_dialog .J_addProductDivUnit").show();
    });

    $("#addNewProduct_dialog .J_cancel_addProduct").bind("click", function (e) {
        $("#addNewProduct_dialog .J_addProduct_addUnit").show();
        $("#addNewProduct_dialog .J_addProductDivUnit").hide();
        $("#addNewProduct_dialog [name='new_storageUnit']").val("");
        $("#addNewProduct_dialog [name='new_sellUnitRate']").val("");
        $("#addNewProduct_dialog [name='new_storageUnitRate']").val("");
        $("#addNewProduct_dialog .J_newStorageUnitSpan").text("").attr("title","");
    });



    $("#addNewProduct_dialog [name='new_productVehicleModel']").bind("keyup click", function (e) {
        var eventKeyCode = e.which || e.keyCode;
        if (GLOBAL.Interactive.keyNameFromKeyCode(eventKeyCode).search(/left|up|right|down/g) == -1) {
            var $thisDom = $(this);
            var pos = APP_BCGOGO.StringFilter.getCursorPosition($thisDom[0], APP_BCGOGO.StringFilter.inputtingProductNameFilter);
            $thisDom.val(APP_BCGOGO.StringFilter.inputtingProductNameFilter($thisDom.val()));
            APP_BCGOGO.StringFilter.getCursorPosition($thisDom[0], pos);
            var searchField = $thisDom.attr("searchField");
            var cacheField = $thisDom.attr("cacheField");
            var brandVal = $("#addNewProduct_dialog [name='new_productVehicleBrand']").val();
            var sourceKey = brandVal + "_&&_" + $thisDom.val();
            if (newProductCache.isTimeOut(sourceKey, cacheField)) {
                var ajaxData = {
                    brandValue: brandVal,
                    searchWord: $thisDom.val(),
                    searchField: searchField,
                    uuid: GLOBAL.Util.generateUUID()
                };
                var ajaxUrl = "product.do?method=searchBrandSuggestion";
                APP_BCGOGO.wjl.LazySearcher.lazySearch(ajaxUrl, ajaxData, function (jsonStr) {
                    var source = [];
                    if (jsonStr && jsonStr.length > 0) {
                        for (var i = 0, len = jsonStr.length; i < len; i++) {
                            if (!G.Lang.isEmpty(jsonStr[i].name)) {
                                source.push(APP_BCGOGO.StringFilter.inputtingProductNameFilter(jsonStr[i].name));
                            }
                        }
                    }
                    newProductCache[cacheField].data[sourceKey] = {};
                    newProductCache[cacheField].data[sourceKey]["source"] = source;
                    newProductCache[cacheField].data[sourceKey]["syncTime"] = (new Date()).valueOf();
                    if ($(document.activeElement).attr("name") == $thisDom.attr("name")) {
                        $thisDom.autocomplete({
                            minLength: 0,
                            delay: 0,
                            source: source,
                            select:function(e,ui){
                                $.ajax({
                                    type: "POST",
                                    data:{
                                        modelValue:ui.item.value,
                                        searchField:"brand"
                                    },
                                    url: "product.do?method=searchBrandSuggestion",
                                    cache: false,
                                    dataType: "json",
                                    error: function (XMLHttpRequest, error, errorThrown) {
                                    },
                                    success: function (jsonStr) {
                                        if (jsonStr && jsonStr.length > 0 && !G.Lang.isEmpty(jsonStr[0].name)) {
                                            $("#addNewProduct_dialog [name='new_productVehicleBrand']").val(jsonStr[0].name);
                                        }
                                    }
                                });
                            }
                        });
                        $thisDom.autocomplete("search", "");
                    }
                })
            } else {
                $thisDom.autocomplete({
                    minLength: 0,
                    delay: 0,
                    source: newProductCache[cacheField].data[sourceKey]["source"]  ,
                    select: function (e, ui) {
                        $.ajax({
                            type: "POST",
                            data: {
                                modelValue: ui.item.value,
                                searchField: "brand"
                            },
                            url: "product.do?method=searchBrandSuggestion",
                            cache: false,
                            dataType: "json",
                            error: function (XMLHttpRequest, error, errorThrown) {
                            },
                            success: function (jsonStr) {
                                if (jsonStr && jsonStr.length > 0 && !G.Lang.isEmpty(jsonStr[0].name)) {
                                    $("#addNewProduct_dialog [name='new_productVehicleBrand']").val(jsonStr[0].name);
                                }
                            }
                        });
                    }
                });
                $thisDom.autocomplete("search", "");
            }
        }
    });


    $("#_productVehicleBrand").live("keyup click", function (e) {
          e.stopImmediatePropagation();
          var eventKeyCode = e.which || e.keyCode;
          if (GLOBAL.Interactive.keyNameFromKeyCode(eventKeyCode).search(/left|up|right|down/g) == -1) {
              var $thisDom = $(this);
              var pos = APP_BCGOGO.StringFilter.getCursorPosition($thisDom[0], APP_BCGOGO.StringFilter.inputtingProductNameFilter);
              $thisDom.val(APP_BCGOGO.StringFilter.inputtingProductNameFilter($thisDom.val()));
              APP_BCGOGO.StringFilter.getCursorPosition($thisDom[0], pos);
              var searchField = $thisDom.attr("searchField");
              var cacheField = $thisDom.attr("cacheField");
              var thisDomVal = $thisDom.val();
              if (newProductCache.isTimeOut($thisDom.val(), cacheField)) {
                  var ajaxData = {
                      searchWord: $thisDom.val(),
                      searchField: searchField,
                      uuid: GLOBAL.Util.generateUUID()
                  };
                  var ajaxUrl = "product.do?method=searchBrandSuggestion";
                  APP_BCGOGO.wjl.LazySearcher.lazySearch(ajaxUrl, ajaxData, function (jsonStr) {
                      var source = [];
                      if (jsonStr && jsonStr.length > 0) {
                          for (var i = 0, len = jsonStr.length; i < len; i++) {
                              if (!G.Lang.isEmpty(jsonStr[i].name)) {
                                  source.push(APP_BCGOGO.StringFilter.inputtingProductNameFilter(jsonStr[i].name));
                              }
                          }
                      }
                      newProductCache[cacheField].data[thisDomVal] = {};
                      newProductCache[cacheField].data[thisDomVal]["source"] = source;
                      newProductCache[cacheField].data[thisDomVal]["syncTime"] = (new Date()).valueOf();
                      if ($(document.activeElement).attr("name") == $thisDom.attr("name")) {
                          $thisDom.autocomplete({
                              minLength: 0,
                              delay: 0,
                              source: source
                          });
                          $thisDom.autocomplete("search", "");
                      }
                  })
              } else {
                  $thisDom.autocomplete({
                      minLength: 0,
                      delay: 0,
                      source: newProductCache[cacheField].data[$thisDom.val()]["source"]
                  });
                  $thisDom.autocomplete("search", "");
              }
          }
    }).live("blur",function () {
            if ($(this).val() != $(this).attr("lastVal")) {
                $("#_productVehicleModel").val("");
            }
        }).live("focus", function () {
            $(this).attr("lastVal", $(this).val());
        });

    $("#_productVehicleModel").live("keyup click", function (e) {
        e.stopImmediatePropagation();
        var eventKeyCode = e.which || e.keyCode;
        if (GLOBAL.Interactive.keyNameFromKeyCode(eventKeyCode).search(/left|up|right|down/g) == -1) {
            var $thisDom = $(this);
            var pos = APP_BCGOGO.StringFilter.getCursorPosition($thisDom[0], APP_BCGOGO.StringFilter.inputtingProductNameFilter);
            $thisDom.val(APP_BCGOGO.StringFilter.inputtingProductNameFilter($thisDom.val()));
            APP_BCGOGO.StringFilter.getCursorPosition($thisDom[0], pos);
            var searchField = $thisDom.attr("searchField");
            var cacheField = $thisDom.attr("cacheField");
            var brandVal = $("#_productVehicleBrand").val();
            var sourceKey = brandVal + "_&&_" + $thisDom.val();
            if (newProductCache.isTimeOut(sourceKey, cacheField)) {
                var ajaxData = {
                    brandValue: brandVal,
                    searchWord: $thisDom.val(),
                    searchField: searchField,
                    uuid: GLOBAL.Util.generateUUID()
                };
                var ajaxUrl = "product.do?method=searchBrandSuggestion";
                APP_BCGOGO.wjl.LazySearcher.lazySearch(ajaxUrl, ajaxData, function (jsonStr) {
                    var source = [];
                    if (jsonStr && jsonStr.length > 0) {
                        for (var i = 0, len = jsonStr.length; i < len; i++) {
                            if (!G.Lang.isEmpty(jsonStr[i].name)) {
                                source.push(APP_BCGOGO.StringFilter.inputtingProductNameFilter(jsonStr[i].name));
                            }
                        }
                    }
                    newProductCache[cacheField].data[sourceKey] = {};
                    newProductCache[cacheField].data[sourceKey]["source"] = source;
                    newProductCache[cacheField].data[sourceKey]["syncTime"] = (new Date()).valueOf();
                    if ($(document.activeElement).attr("name") == $thisDom.attr("name")) {
                        $thisDom.autocomplete({
                            minLength: 0,
                            delay: 0,
                            source: source,
                            select: function (e, ui) {
                                  $.ajax({
                                      type: "POST",
                                      data: {
                                          modelValue: ui.item.value,
                                          searchField: "brand"
                                      },
                                      url: "product.do?method=searchBrandSuggestion",
                                      cache: false,
                                      dataType: "json",
                                      error: function (XMLHttpRequest, error, errorThrown) {
                                      },
                                      success: function (jsonStr) {
                                          if (jsonStr && jsonStr.length > 0 && !G.Lang.isEmpty(jsonStr[0].name)) {
                                              $("#_productVehicleBrand").val(jsonStr[0].name);
                                          }
                                      }
                                  });
                              }
                        });
                        $thisDom.autocomplete("search", "");
                    }
                })
            } else {
                $thisDom.autocomplete({
                    minLength: 0,
                    delay: 0,
                    source: newProductCache[cacheField].data[sourceKey]["source"],
                    select: function (e, ui) {
                          $.ajax({
                              type: "POST",
                              data: {
                                  modelValue: ui.item.value,
                                  searchField: "brand"
                              },
                              url: "product.do?method=searchBrandSuggestion",
                              cache: false,
                              dataType: "json",
                              error: function (XMLHttpRequest, error, errorThrown) {
                              },
                              success: function (jsonStr) {
                                  if (jsonStr && jsonStr.length > 0 && !G.Lang.isEmpty(jsonStr[0].name)) {
                                      $("#_productVehicleBrand").val(jsonStr[0].name);
                                  }
                              }
                          });
                      }
                });
                $thisDom.autocomplete("search", "");
            }
        }
    });

    $("#addNewProduct_dialog").find("[name='new_recommendedPrice'],[name='new_tradePrice']")
        .bind("keyup",function () {
            var $thisDom = $(this);
            var pos = APP_BCGOGO.StringFilter.getCursorPosition($thisDom[0], APP_BCGOGO.StringFilter.inputtingPriceFilter);
            $thisDom.val(APP_BCGOGO.StringFilter.inputtingPriceFilter($thisDom.val(), 2));
            APP_BCGOGO.StringFilter.setCursorPosition($thisDom[0], pos);
        }).bind("blur", function () {
            var $thisDom = $(this);
            $thisDom.val(APP_BCGOGO.StringFilter.inputtedPriceFilter($thisDom.val(), 2));
        });

    /**
     * 上下限输入校验
     */
    $("#addNewProduct_dialog").find("[name='new_upperLimit'],[name='new_lowerLimit']")
        .bind("keyup",function () {
            var $thisDom = $(this);
            var pos = APP_BCGOGO.StringFilter.getCursorPosition($thisDom[0], APP_BCGOGO.StringFilter.inputtingIntFilter);
            $thisDom.val(APP_BCGOGO.StringFilter.inputtingIntFilter($thisDom.val()));
            APP_BCGOGO.StringFilter.setCursorPosition($thisDom[0], pos);
        }).bind("blur", function () {
            var $thisDom = $(this);
            $thisDom.val(APP_BCGOGO.StringFilter.inputtingIntFilter($thisDom.val()));
            var upperLimitVal = $("#addNewProduct_dialog").find("[name='new_upperLimit']").val() * 1;
            var lowerLimitVal = $("#addNewProduct_dialog").find("[name='new_lowerLimit']").val() * 1;

            if (upperLimitVal < lowerLimitVal && upperLimitVal != 0 && lowerLimitVal != 0) {
                var temp = lowerLimitVal;
                lowerLimitVal = upperLimitVal;
                upperLimitVal = temp;
                $("#addNewProduct_dialog").find("[name='new_upperLimit']").val(upperLimitVal);
                $("#addNewProduct_dialog").find("[name='new_lowerLimit']").val(lowerLimitVal);
            } else if (upperLimitVal == lowerLimitVal && upperLimitVal != 0 && lowerLimitVal != 0) {
                upperLimitVal++;
                $("#addNewProduct_dialog").find("[name='new_upperLimit']").val(upperLimitVal);
            }
        });

    $("#addNewProduct_dialog").find("[name='new_sellUnitRate'],[name='new_storageUnitRate']")
        .bind("keyup",function () {
            var $thisDom = $(this);
            var pos = APP_BCGOGO.StringFilter.getCursorPosition($thisDom[0], APP_BCGOGO.StringFilter.inputtingIntFilter);
            $thisDom.val(APP_BCGOGO.StringFilter.inputtingIntFilter($thisDom.val()));
            APP_BCGOGO.StringFilter.setCursorPosition($thisDom[0], pos);
        }).bind("blur", function () {
            var $thisDom = $(this);
            $thisDom.val(APP_BCGOGO.StringFilter.inputtingIntFilter($thisDom.val()));
        });

    $("#addNewProduct_dialog .J_saveNewProduct").bind("click", function () {
        var $thisDom = $(this);
        if ($thisDom.attr("lock")) {
            return;
        }
        $thisDom.attr("lock", true);
        if (validateSaveNewProduct()) {
            nsDialog.jConfirm("确定新增该商品", null, function (val) {
                if (val) {
                    var data = {};
                    data.commodityCode = $("#addNewProduct_dialog").find("[name='new_commodityCode']").val();
                    data.KindName = $("#addNewProduct_dialog").find("[name='new_productType']").val();
                    data.name = $("#addNewProduct_dialog").find("[name='new_name']").val();
                    data.brand = $("#addNewProduct_dialog").find("[name='new_brand']").val();
                    data.spec = $("#addNewProduct_dialog").find("[name='new_spec']").val();
                    data.model = $("#addNewProduct_dialog").find("[name='new_model']").val();
                    data.productVehicleBrand = $("#addNewProduct_dialog").find("[name='new_productVehicleBrand']").val();
                    data.productVehicleModel = $("#addNewProduct_dialog").find("[name='new_productVehicleModel']").val();
                    data.recommendedPrice = $("#addNewProduct_dialog").find("[name='new_recommendedPrice']").val();
                    data.tradePrice = $("#addNewProduct_dialog").find("[name='new_tradePrice']").val();
                    data.upperLimit = $("#addNewProduct_dialog").find("[name='new_upperLimit']").val();
                    data.lowerLimit = $("#addNewProduct_dialog").find("[name='new_lowerLimit']").val();
                    data.storageBin = $("#addNewProduct_dialog").find("[name='new_storageBin']").val();
                    data["storeHouseInventoryDTOs[0].storehouseId"] = $("#addNewProduct_dialog").find("[name='new_storehouseId']").val();
                    var sellUnit =  $("#addNewProduct_dialog").find("[name='new_sellUnit']").val();
                    var storageUnit = $("#addNewProduct_dialog").find("[name='new_storageUnit']").val();
                    var sellUnitRate = $("#addNewProduct_dialog").find("[name='new_sellUnitRate']").val()*1;
                    var storageUnitRate = $("#addNewProduct_dialog").find("[name='new_storageUnitRate']").val()*1;
                    if(storageUnitRate > 1){
                        data.sellUnit = storageUnit;
                        data.storageUnit = sellUnit;
                        data.rate = storageUnitRate;
                    }else if(sellUnitRate >1  ) {
                        data.sellUnit = sellUnit;
                        data.storageUnit = storageUnit;
                        data.rate = sellUnitRate;
                    }else{
                        data.sellUnit = sellUnit;
                        data.storageUnit = sellUnit;
                    }
                    data["imageCenterDTO.productMainImagePath"]=$("#addNewProduct_dialog").find("[name='new_imagePath']").val();
                    $.ajax({
                        type: "POST",
                        url: "product.do?method=validateSaveNewProduct",
                        data: data,
                        async: false,
                        cache: false,
                        dataType: "json",
                        error: function (XMLHttpRequest, error, errorThrown) {
                            nsDialog.jAlert("网络异常，请联系管理员！");
                            $thisDom.removeAttr("lock");
                        },
                        success: function (json) {
                            if (json && json.success) {
                                $.ajax({
                                    type: "POST",
                                    url: "product.do?method=saveNewProduct",
                                    data: data,
                                    async: false,
                                    cache: false,
                                    dataType: "json",
                                    error: function (XMLHttpRequest, error, errorThrown) {
                                        nsDialog.jAlert("网络异常，请联系管理员！");
                                        $thisDom.removeAttr("lock");
                                    },
                                    success: function (json) {
                                        if (json && json.success) {
                                            nsDialog.jAlert("保存成功", null, function () {
                                                $("#addNewProduct_dialog").dialog("close");
                                                window.location.reload();
                                            });
                                        } else {
                                            nsDialog.jAlert(json.msg);
                                            $thisDom.removeAttr("lock");
                                        }
                                    }
                                });
                            } else {
                                nsDialog.jAlert(json.msg);
                                $thisDom.removeAttr("lock");
                            }
                        }
                    });
                } else {
                    $thisDom.removeAttr("lock");
                }
            });
        } else {
            $thisDom.removeAttr("lock");
        }
    });

    $("#addNewProduct_dialog .J_resetNewProduct").bind("click", function () {
        resetAddNewProduct();
        bindNewProductImageInfo();
    });

    //对输入的单位重新排序
    function reSortUnitSource(unit, unitSource) {
        if (!unitSource) {
            unitSource = [];
        }
        if (G.Lang.isEmpty(unit)) {
            return unitSource;
        }
        var newUnitSource = [];
        newUnitSource.unshift(unit);

        for (var i = 0, len = unitSource.length; i < len; i++) {
            if (!G.Lang.isEmpty(unitSource[i]) && unitSource[i] != unit) {
                newUnitSource.push(unitSource[i]);
            }
        }
        return newUnitSource;
    }

    function resetAddNewProduct() {
        $("#addNewProduct_dialog").find("[name='new_productVehicleBrand'],[name='new_productVehicleModel']," +
            "[name='new_sellUnit'],[name='new_productVehicleBrand'],[name='new_productVehicleModel'],[name='new_productType'],[name='new_imagePath']").autocomplete("close");
        var nameArrays = ["new_commodityCode", "new_productType", "new_name", "new_brand", "new_spec", "new_model", "new_productVehicleBrand",
            "new_productVehicleModel", "new_recommendedPrice", "new_tradePrice", "new_upperLimit", "new_lowerLimit", "new_storageBin", "new_storehouseId",
            "new_sellUnit", "new_storageUnit", "new_sellUnitRate","new_storageUnitRate","new_imagePath"];
        for (var i = 0, len = nameArrays.length; i < len; i++) {
            $("#addNewProduct_dialog").find("[name='" + nameArrays[i] + "']").val("");
        }
        $(".J_saveNewProduct").removeAttr("lock");
        $("#addNewProduct_dialog .J_newSellUnitSpan").text("").attr("title", "");
        $("#addNewProduct_dialog .J_newStorageUnitSpan").text("").attr("title", "");
        $("#addNewProduct_dialog .J_addProduct_addUnit").show();
        $("#addNewProduct_dialog .J_addProductDivUnit").hide();
    }

    function validateSaveNewProduct() {
        var $parentDom = $("#addNewProduct_dialog");
        if (G.Lang.isEmpty($parentDom.find("[name='new_name']").val())) {
            nsDialog.jAlert("品名不能为空，请输入品名！");
            return false;
        }

        if (!G.Lang.isEmpty($parentDom.find("[name='new_storageBin']").val()) &&$parentDom.find("[name='new_storehouseId']").val() &&
            G.Lang.isEmpty($parentDom.find("[name='new_storehouseId']").val())) {
            nsDialog.jAlert("您填写了仓位,请选择仓库！");
            return false;
        }
        if (G.Lang.isEmpty($parentDom.find("[name='new_sellUnit']").val())) {
            nsDialog.jAlert("商品单位不能为空，请输入单位！");
            return false;
        } else if (!G.Lang.isEmpty($parentDom.find("[name='new_sellUnit']").val())
            && !G.Lang.isEmpty($parentDom.find("[name='new_storageUnit']").val())) {
            if ($parentDom.find("[name='new_sellUnit']").val() == $parentDom.find("[name='new_storageUnit']").val()) {
                nsDialog.jAlert("请输入不相同的大小单位！");
                return false;
            }
            var sellUnitRate = $("#addNewProduct_dialog").find("[name='new_sellUnitRate']").val()*1;
            var storageUnitRate = $("#addNewProduct_dialog").find("[name='new_storageUnitRate']").val()*1;

            if (isNaN(sellUnitRate) || isNaN(storageUnitRate) || G.isEmpty(sellUnitRate) || G.isEmpty(storageUnitRate)
                ||(sellUnitRate == 1 && storageUnitRate == 1) ||  !(sellUnitRate == 1 || storageUnitRate == 1) || sellUnitRate * 1<1 || storageUnitRate * 1<1 ) {
                nsDialog.jAlert("增加单位换算比例格式不正确！请重新填写<br><br><span>例如：1箱=10个 或者 10个=1箱<span>");
                return false;
            }

        }
        return true;


    }


});