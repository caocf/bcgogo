;
var adShow = (function () {

    var _config = {
        screen: "wideScreen",//wideScreen,narrowScreen,leftNarrowScreen 宽屏和窄屏,左窄屏
        leftAdTemplate:'<div class="r_commodities_relative">'+
            '<div class="r_commodities_absolute">'+
            '<div class="title_top"><div class="title_off J_left_close"></div>推荐商品</div>'+
            '<div class="content">' +
            '<div class="search">' +
            '<input class="s_input J_left_search_word" type="text" >' +
            '<div class="search_retrieve_btn J_left_search_btn"></div>' +
            '</div>' +
            '<ul>' +
            '</ul>'+
            '<div class="page">' +
            '<div class="prev_page J_left_prev_page"></div>' +
            '<div class="next_page J_left_next_page"></div>' +
            '<div class="clear"></div>' +
            '</div>'+
            '<div class="clear"></div>'+
            '</div>'+
            '</div>'+
            '</div>'
            ,
        bottomAdTemplate:
            '<div class="r_commodities_relative">' +
                '<div class="bottom_commodities">' +
                    '<div class="title_off J_bottom_close"></div>' +
                '<div class="content">'+
                '<ul>' +
                '<li><div class="title_top">推<br>荐<br>商<br>品</div></li>'+
                '<li class="J_bottom_prev_page_container"><div class="prev_img J_bottom_prev_page"></div></li>'+
                '</ul>'+
                '<div class="search_r">' +
                    '<div class="right_last_point"><div class="next_img J_bottom_next_page"></div></div>' +
                    '<div class="right_r_s">'+
                        '<div class="font14">配件自定义搜索：</div>' +
                        '<div class="search"><input class="s_input J_bottom_search_word" type="text" name=""><div class="search_retrieve_btn J_bottom_search_btn"></div></div>' +
                    '</div>' +
                    '<div class="clear"></div>'+
                '</div>' +
            '</div>' +
            '</div>',
        leftNarrowAdTemplate:
            '<div class="r_commodities_relative">'+
                '<div class="wid100_absolute">'+
                    '<div class="title_top"><div class="title_off J_left_close"></div>推荐商品</div>'+
                    '<div class="content">'+
                        '<div class="search">'+
                            '<input type="text" class="s_input J_left_search_word" />'+
                            '<div class="search_retrieve_btn J_left_search_btn"></div>'+
                        '</div>'+
                        '<ul></ul>'+
                        '<div class="page">' +
                            '<div class="prev_page J_left_prev_page"></div>'+
                            '<div class="next_page J_left_next_page"></div>'+
                            '<div class="clear"></div>'+
                        '</div>'+
                        '<div class="clear"></div>'+
                    '</div>'+
                '</div>'+
            '</div>',

        hideAdTemplate:'<div class="r_commodities_relative"><div class="tip_add J_show_ad"> </div></div>',
        leftProductTemplate:"",
        bottomProductTemplate:"",
        $leftAdDom: null,
        $leftNarrowAdDom:null,
        $bottomAdDom: null,
        $hideAdDom:null,
        maxAutoReadSize: 50,
        uuid:null,

        pageSize: 5,
        currentPage:1,
        totalResult:0,
        hasNextPage:true,
        hasPreviousPage:true,
        isLastPage:false,
        isFirstPage:true,

        intervalTime: 7000,
        scanProductTime: 2000,
        intervalFun:null,
        isActive: true,//广告展示栏是否隐藏
        isRequesting:false,//正在请求后台数据
        isStartAutoRead: false,
        isClickDelay:false,
        wideScreenWidth: 1400,
        leftNarrowScreenWidth: 1200,
        bottomProductWidth :183,
        commodityAdUrl:"autoAccessoryOnline.do?method=getCommodityAd",
        searchWordAdUrl:"autoAccessoryOnline.do?method=getCommodityQuotationsList",
        searchWord:"",
        productName:"",
        productBrand:"",
        productSpec:"",
        productModel:"",
        productVehicleModel:"",
        productVehicleBrand:"",
        currentUrl:"autoAccessoryOnline.do?method=getCommodityAd"
    };

    function _setConfig(name, value) {
        _config[name] = value;
    }

    function _documentHidden(){
        if (typeof document.hidden !== "undefined") {
            return document.hidden;
        } else if (typeof document.mozHidden !== "undefined") {
            return document.mozHidden;
        } else if (typeof document.msHidden !== "undefined") {
            return document.msHidden;
        } else if (typeof document.webkitHidden !== "undefined") {
            return document.webkitHidden;
        }
        return undefined;
    }

    function _isScheduleRun(){
        return !App.Module.Login.isTimeout() && !_documentHidden() && _config.isActive;
    }

    function _getConfig (name) {
        if(name){
            return _config[name];
        }
        return _config;
    }

    function _getSearchData(){
        return {
            startPageNo: _config.currentPage,
            maxRows: _config.pageSize,
            searchWord: _config.searchWord,
            productName: _config.productName,
            productBrand: _config.productBrand,
            productSpec: _config.productSpec,
            productModel: _config.productModel,
            productVehicleModel: _config.productVehicleModel,
            productVehicleBrand: _config.productVehicleBrand,
            fromSource: "AdSearch"
        };
    }

    function _drawWideScreenProducts(result) {
        if (result && result.length == 2) {
            var data = result[0] ? result[0]["data"] : null;
            var pager = result[1];

            if(pager){
                _config.totalResult = pager.totalRows;
                _config.hasNextPage = pager.hasNextPage;
                _config.hasPreviousPage = pager.hasPreviousPage;
                _config.currentPage = pager.currentPage;
                _config.isFirstPage = pager.isFirstPage;
                _config.isLastPage = pager.isLastPage;
                _config.currentPage = pager.currentPage;
            }

            var $prevPage = _config.$leftAdDom.find(".J_left_prev_page");
            var $nextPage = _config.$leftAdDom.find(".J_left_next_page");
            if (_config.hasPreviousPage) {
                if ($prevPage.hasClass("prev_page_first")) {
                    $prevPage.removeClass("prev_page_first").addClass("prev_page");
                }
            } else {
                if ($prevPage.hasClass("prev_page")) {
                    $prevPage.removeClass("prev_page").addClass("prev_page_first");
                }
            }
            if(_config.hasNextPage){
                if ($nextPage.hasClass("next_page_last")) {
                    $nextPage.removeClass("next_page_last").addClass("next_page");
                }
            }else{
                if ($nextPage.hasClass("next_page")) {
                    $nextPage.removeClass("next_page").addClass("next_page_last");
                }
            }

            var products = data && data["products"] ? data["products"] : [];
            if (products && products.length > 0) {
                if (_getConfig("screen") == "wideScreen") {
                    var $ulDom = _config.$leftAdDom.find("ul");
                    var drawFn = function(){
                        var html = '';
                        for (var i = 0; i < products.length; i++) {
                            var productLocalInfoId = G.Lang.normalize(products[i].productLocalInfoIdStr);
                            var shopId = G.Lang.normalize(products[i].shopIdStr);
                            var productName = G.Lang.normalize(products[i].name);
                            var productBrand = G.Lang.normalize(products[i].brand);
                            var productSpec = G.Lang.normalize(products[i].spec);
                            var productModel = G.Lang.normalize(products[i].model);
                            var productVehicleBrand = G.Lang.normalize(products[i].productVehicleBrand);
                            var productVehicleModel = G.Lang.normalize(products[i].productVehicleModel);
                            var inSalesPrice = G.Lang.normalize(products[i].inSalesPrice);
                            var isBargain = false;
                            var bargainPrice = 0;
                            var productImageUrl = null;
                            var productInfo = '<b>' + productName + '</b>';
                            if (G.Lang.isNotEmpty(productBrand)) {
                                productInfo += '&nbsp;' + productBrand;
                            }
                            if (G.Lang.isNotEmpty(productSpec)) {
                                productInfo += '&nbsp;' + productSpec;
                            }
                            if (G.Lang.isNotEmpty(productModel)) {
                                productInfo += '&nbsp;' + productModel;
                            }
                            if (G.Lang.isNotEmpty(productVehicleBrand)) {
                                productInfo += '&nbsp;' + productVehicleBrand;
                            }
                            if (G.Lang.isNotEmpty(productVehicleModel)) {
                                productInfo += '&nbsp;' + productVehicleModel;
                            }

                            if (G.Lang.isNotEmpty(products[i].promotionsDTOs)) {
                                for (var j = 0; j < products[i].promotionsDTOs.length; j++) {
                                    var promotionsDTO = products[i].promotionsDTOs[j];
                                    if (promotionsDTO && promotionsDTO.type == "BARGAIN"
                                        && promotionsDTO.status == "USING"
                                        && G.Lang.isNotEmpty(promotionsDTO.promotionsProductDTOList)) {
                                        var promotionProductDTO = promotionsDTO.promotionsProductDTOList[0];
                                        isBargain = true;
                                        bargainPrice = promotionProductDTO.discountAmount;
                                    }
                                }
                            }
                            if (G.Lang.isNotEmpty(products[i].imageCenterDTO)
                                && G.Lang.isNotEmpty(products[i].imageCenterDTO.productListSmallImageDetailDTO)) {
                                var imageDTO = products[i].imageCenterDTO.productListSmallImageDetailDTO
                                if (imageDTO && imageDTO.imageURL) {
                                    productImageUrl = imageDTO.imageURL;
                                }

                            }
                            html += '<li class="J_left_product_container"  data-shop-id = "' + shopId + '" data-product-id = "' + productLocalInfoId + '">';
                            if (_config.currentUrl == _config.commodityAdUrl && pager && pager.currentPage == 1 && i<=1) {
                                html += '<div class="r_commodities_relative"><div class="new"></div></div>';
                            }
                            html +=
                                '<div class="left_img"><img src="' + productImageUrl + '"></div>' +
                                    '<div class="right_txt">' +
                                '<div class="l_top">' + productInfo + '</div>';
                            if (isBargain) {
                                html += '<span class="price_through">&yen;' + inSalesPrice + '</span>';
                                html += '<span class="ad_price">&yen;' + bargainPrice + '</span>' ;
                            }else{
                                html += '<span class="ad_price">&yen;' + inSalesPrice + '</span>';
                            }
                            html += '</div>' +
                                '<div class="clear"></div>' +
                                '</li>';
                        }
                        $ulDom.html(html);
                        $ulDom.children().hide();
                        $ulDom.children().fadeIn("slow");
                        _config.$leftAdDom.show();
                    };
                    if(!$.isEmptyObject($ulDom.children()) && $ulDom.children().size()>0){
                        $ulDom.children().fadeOut("slow",drawFn);
                    }else{
                        drawFn();
                    }
                }
            }
        }
    }

    function _drawLeftNarrowScreenProducts(result) {
        if (result && result.length == 2) {
            var data = result[0] ? result[0]["data"] : null;
            var pager = result[1];

            if(pager){
                _config.totalResult = pager.totalRows;
                _config.hasNextPage = pager.hasNextPage;
                _config.hasPreviousPage = pager.hasPreviousPage;
                _config.currentPage = pager.currentPage;
                _config.isFirstPage = pager.isFirstPage;
                _config.isLastPage = pager.isLastPage;
                _config.currentPage = pager.currentPage;
            }

            var $prevPage = _config.$leftNarrowAdDom.find(".J_left_prev_page");
            var $nextPage = _config.$leftNarrowAdDom.find(".J_left_next_page");
            if (_config.hasPreviousPage) {
                if ($prevPage.hasClass("prev_page_first")) {
                    $prevPage.removeClass("prev_page_first").addClass("prev_page");
                }
            } else {
                if ($prevPage.hasClass("prev_page")) {
                    $prevPage.removeClass("prev_page").addClass("prev_page_first");
                }
            }
            if(_config.hasNextPage){
                if ($nextPage.hasClass("next_page_last")) {
                    $nextPage.removeClass("next_page_last").addClass("next_page");
                }
            }else{
                if ($nextPage.hasClass("next_page")) {
                    $nextPage.removeClass("next_page").addClass("next_page_last");
                }
            }

            var products = data && data["products"] ? data["products"] : [];
            if (products && products.length > 0) {
                    var $ulDom = _config.$leftNarrowAdDom.find("ul");
                    var drawFn = function(){
                        var html = '';
                        for (var i = 0; i < products.length; i++) {
                            var productLocalInfoId = G.Lang.normalize(products[i].productLocalInfoIdStr);
                            var shopId = G.Lang.normalize(products[i].shopIdStr);
                            var productName = G.Lang.normalize(products[i].name);
                            var productBrand = G.Lang.normalize(products[i].brand);
                            var productSpec = G.Lang.normalize(products[i].spec);
                            var productModel = G.Lang.normalize(products[i].model);
                            var productVehicleBrand = G.Lang.normalize(products[i].productVehicleBrand);
                            var productVehicleModel = G.Lang.normalize(products[i].productVehicleModel);
                            var inSalesPrice = G.Lang.normalize(products[i].inSalesPrice);
                            var isBargain = false;
                            var bargainPrice = 0;
                            var productImageUrl = null;
                            var productInfo = '<b>' + productName + '</b>';
                            if (G.Lang.isNotEmpty(productBrand)) {
                                productInfo += '&nbsp;' + productBrand;
                            }
                            if (G.Lang.isNotEmpty(productSpec)) {
                                productInfo += '&nbsp;' + productSpec;
                            }
                            if (G.Lang.isNotEmpty(productModel)) {
                                productInfo += '&nbsp;' + productModel;
                            }
                            if (G.Lang.isNotEmpty(productVehicleBrand)) {
                                productInfo += '&nbsp;' + productVehicleBrand;
                            }
                            if (G.Lang.isNotEmpty(productVehicleModel)) {
                                productInfo += '&nbsp;' + productVehicleModel;
                            }

                            if (G.Lang.isNotEmpty(products[i].promotionsDTOs)) {
                                for (var j = 0; j < products[i].promotionsDTOs.length; j++) {
                                    var promotionsDTO = products[i].promotionsDTOs[j];
                                    if (promotionsDTO && promotionsDTO.type == "BARGAIN"
                                        && promotionsDTO.status == "USING"
                                        && G.Lang.isNotEmpty(promotionsDTO.promotionsProductDTOList)) {
                                        var promotionProductDTO = promotionsDTO.promotionsProductDTOList[0];
                                        isBargain = true;
                                        bargainPrice = promotionProductDTO.discountAmount;
                                    }
                                }
                            }
                            if (G.Lang.isNotEmpty(products[i].imageCenterDTO)
                                && G.Lang.isNotEmpty(products[i].imageCenterDTO.productListSmallImageDetailDTO)) {
                                var imageDTO = products[i].imageCenterDTO.productListSmallImageDetailDTO
                                if (imageDTO && imageDTO.imageURL) {
                                    productImageUrl = imageDTO.imageURL;
                                }

                            }
                            html += '<li class="J_left_product_container"  data-shop-id = "' + shopId + '" data-product-id = "' + productLocalInfoId + '">';
                            if (_config.currentUrl == _config.commodityAdUrl && pager && pager.currentPage == 1 && i<=1) {
                                html += '<div class="r_commodities_relative"><div class="new"></div></div>';
                            }
                            html +=
                                '<div class="left_img"><img src="' + productImageUrl + '"></div>' +
                                    '<div class="right_txt">' +
                                '<div class="l_top">' + productInfo + '</div>';
                            if (isBargain) {
                                html += '<span class="price_through">&yen;' + inSalesPrice + '</span>';
                                html += '<span class="ad_price">&yen;' + bargainPrice + '</span>' ;
                            }else{
                                html += '<span class="ad_price">&yen;' + inSalesPrice + '</span>';
                            }
                            html += '</div>' +
                                '<div class="clear"></div>' +
                                '</li>';
                        }
                        $ulDom.html(html);
                        $ulDom.children().hide();
                        $ulDom.children().fadeIn("slow");
                        _config.$leftNarrowAdDom.show();
                    };
                    if(!$.isEmptyObject($ulDom.children()) && $ulDom.children().size()>0){
                        $ulDom.children().fadeOut("slow",drawFn);
                    }else{
                        drawFn();
                    }
            }
        }
    }

    function _drawNarrowScreenProducts(result) {
        if (result && result.length == 2) {
            var data = result[0] ? result[0]["data"] : null;
            var pager = result[1];

            if(pager){
                _config.totalResult = pager.totalRows;
                _config.hasNextPage = pager.hasNextPage;
                _config.hasPreviousPage = pager.hasPreviousPage;
                _config.currentPage = pager.currentPage;
                _config.isFirstPage = pager.isFirstPage;
                _config.isLastPage = pager.isLastPage;
                _config.currentPage = pager.currentPage;
            }

            var $prevPage = _config.$bottomAdDom.find(".J_bottom_prev_page");
            var $nextPage = _config.$bottomAdDom.find(".J_bottom_next_page");
            if (_config.hasPreviousPage) {
                if ($prevPage.hasClass("prev_img_first")) {
                    $prevPage.removeClass("prev_img_first").addClass("prev_img");
                }
            } else {
                if ($prevPage.hasClass("prev_img")) {
                    $prevPage.removeClass("prev_img").addClass("prev_img_first");
                }
            }
            if(_config.hasNextPage){
                if ($nextPage.hasClass("next_img_last")) {
                    $nextPage.removeClass("next_img_last").addClass("next_img");
                }
            }else{
                if ($nextPage.hasClass("next_img")) {
                    $nextPage.removeClass("next_img").addClass("next_img_last");
                }
            }

            var products = data && data["products"] ? data["products"] : [];
            if (products && products.length > 0) {
                    var $oldProductItems = _config.$bottomAdDom.find("ul .J_bottom_product_container");
                    var drawFn = function(){
                        var html = '';
                        for (var i = 0; i < products.length; i++) {
                            var productLocalInfoId = G.Lang.normalize(products[i].productLocalInfoIdStr);
                            var shopId = G.Lang.normalize(products[i].shopIdStr);
                            var productName = G.Lang.normalize(products[i].name);
                            var productBrand = G.Lang.normalize(products[i].brand);
                            var productSpec = G.Lang.normalize(products[i].spec);
                            var productModel = G.Lang.normalize(products[i].model);
                            var productVehicleBrand = G.Lang.normalize(products[i].productVehicleBrand);
                            var productVehicleModel = G.Lang.normalize(products[i].productVehicleModel);
                            var inSalesPrice = G.Lang.normalize(products[i].inSalesPrice);
                            var isBargain = false;
                            var bargainPrice = 0;
                            var productImageUrl = null;
                            var productInfo = '<b>' + productName + '</b>';
                            if (G.Lang.isNotEmpty(productBrand)) {
                                productInfo += '&nbsp;' + productBrand;
                            }
                            if (G.Lang.isNotEmpty(productSpec)) {
                                productInfo += '&nbsp;' + productSpec;
                            }
                            if (G.Lang.isNotEmpty(productModel)) {
                                productInfo += '&nbsp;' + productModel;
                            }
                            if (G.Lang.isNotEmpty(productVehicleBrand)) {
                                productInfo += '&nbsp;' + productVehicleBrand;
                            }
                            if (G.Lang.isNotEmpty(productVehicleModel)) {
                                productInfo += '&nbsp;' + productVehicleModel;
                            }

                            if (G.Lang.isNotEmpty(products[i].promotionsDTOs)) {
                                for (var j = 0; j < products[i].promotionsDTOs.length; j++) {
                                    var promotionsDTO = products[i].promotionsDTOs[j];
                                    if (promotionsDTO && promotionsDTO.type == "BARGAIN"
                                        && promotionsDTO.status == "USING"
                                        && G.Lang.isNotEmpty(promotionsDTO.promotionsProductDTOList)) {
                                        var promotionProductDTO = promotionsDTO.promotionsProductDTOList[0];
                                        isBargain = true;
                                        bargainPrice = promotionProductDTO.discountAmount;
                                    }
                                }
                            }
                            if (G.Lang.isNotEmpty(products[i].imageCenterDTO)
                                && G.Lang.isNotEmpty(products[i].imageCenterDTO.productListSmallImageDetailDTO)) {
                                var imageDTO = products[i].imageCenterDTO.productListSmallImageDetailDTO
                                if (imageDTO && imageDTO.imageURL) {
                                    productImageUrl = imageDTO.imageURL;
                                }

                            }
                            html += '<li class="J_bottom_product_container" data-shop-id = "' + shopId + '" data-product-id = "' + productLocalInfoId + '">';
                            html += '<div class="li_border">';
                            if (_config.currentUrl == _config.commodityAdUrl && pager && pager.currentPage == 1 && i<=1) {
                                html += '<div class="r_commodities_relative"><div class="new"></div></div>';
                            }
                            html +=
                                '<div class="left_img"><img src="' + productImageUrl + '"></div>' +
                                    '<div class="right_txt">' +
                                '<div class="l_top">' + productInfo + '</div>';
                            if (isBargain) {
                                html += '<span class="price_through">&yen;' + inSalesPrice + '</span>';
                                html += '<span class="ad_price">&yen;' + bargainPrice + '</span>' ;
                            }else{
                                html += '<span class="ad_price">&yen;' + inSalesPrice + '</span>';
                            }
                            html += '</div>' +
                                '</div>' +
                                '<div class="clear"></div>'+
                                '</li>';
                        }


                        var $newProductItems = $(html);
                        $newProductItems.hide();
                        _config.$bottomAdDom.find(".J_bottom_prev_page_container").after($newProductItems);
                        if(!$.isEmptyObject($oldProductItems) && $oldProductItems.size()>0){
                            $oldProductItems.fadeOut("slow",function(){
                                $newProductItems.fadeIn("slow",function(){
                                   $oldProductItems.remove();
                                });

                            });
                        }else{
                            $newProductItems.fadeIn("slow");
                        }

                        //***
//                        if(!$.isEmptyObject($oldProductItems) && $oldProductItems.size()>0){
//                            $oldProductItems.fadeOut("slow",function(){$oldProductItems.remove()});
//                        }else{
//
//                        }
//                        var $newProductItems = $(html);
//                        $newProductItems.hide();
//                        _config.$bottomAdDom.find(".J_bottom_prev_page_container").after($newProductItems);
//                        $newProductItems.fadeIn("slow");
                        //****

//                        var $newProductItems = _config.$bottomAdDom.find("ul .J_bottom_product_container");
//                        $newProductItems.hide();
//                        $newProductItems.fadeIn("slow");

                    };
                    if(!$.isEmptyObject($oldProductItems) && $oldProductItems.size()>0){
                        drawFn();
                    }else{
                        _config.$bottomAdDom.show();
                        drawFn();
                }
            }
        }
    }



    return {
        start: function () {
            var self = this;
            var windowWidth = $(window).width();
            if (windowWidth >= _config.wideScreenWidth) {
                _config.screen="wideScreen";
                _config.pageSize = 5;
            }else if(windowWidth >= _config.leftNarrowScreenWidth){
                _config.screen="leftNarrowScreen";
                _config.pageSize = 3;
            } else {
                _config.screen="narrowScreen";
                $(".i_main").last().after("<div class='clear' style='height: 15px;'></div>");

                var bottomPageSize = Math.floor((windowWidth - 300)/_config.bottomProductWidth);
                if(bottomPageSize <=0){
                    bottomPageSize = 1;
                }else if(bottomPageSize >5){
                    bottomPageSize = 5;
                }
                _config.pageSize = bottomPageSize;
            }
            _config.currentPage = 0;

            _config.$leftAdDom = $(_config.leftAdTemplate).appendTo($(document.body));
            _config.$leftAdDom.hide();
            _config.$bottomAdDom = $(_config.bottomAdTemplate).appendTo($(document.body));
            _config.$bottomAdDom.hide();
            _config.$leftNarrowAdDom = $(_config.leftNarrowAdTemplate).appendTo($(document.body));
            _config.$leftNarrowAdDom.hide();
            _config.$hideAdDom = $(_config.hideAdTemplate).appendTo($(document.body));
            _config.$hideAdDom.hide();


            _config.$leftAdDom.find(".J_left_close").click(function(){
                _config.$leftAdDom.hide();
                _config.isActive = false;
                _config.$hideAdDom.find(".J_show_ad").css({bottom:"50%"});
                _config.$hideAdDom.show();
            });

            _config.$leftNarrowAdDom.find(".J_left_close").click(function(){
                _config.$leftNarrowAdDom.hide();
                _config.isActive = false;
                _config.$hideAdDom.find(".J_show_ad").css({bottom:"50%"});
                _config.$hideAdDom.show();
            });
            _config.$hideAdDom.find(".J_show_ad").click(function(){
                if(_config.screen == "wideScreen"){
                    _config.$leftAdDom.show();
                }else if(_config.screen == "leftNarrowScreen"){
                    _config.$leftNarrowAdDom.show();
                }else{
                    _config.$bottomAdDom.show();
                }
                _config.isActive = true;
                _config.$hideAdDom.hide();
            });

            _config.$leftAdDom.find(".J_left_search_btn").click(function () {
                var searchWord = _config.$leftAdDom.find(".J_left_search_word").val();
                window.open("autoAccessoryOnline.do?method=toCommodityQuotations&searchWord=" + searchWord);
            });

            _config.$leftNarrowAdDom.find(".J_left_search_btn").click(function () {
                var searchWord = _config.$leftNarrowAdDom.find(".J_left_search_word").val();
                window.open("autoAccessoryOnline.do?method=toCommodityQuotations&searchWord=" + searchWord);
            });

            _config.$leftAdDom.find(".J_left_prev_page").click(function () {
                if(_config.hasPreviousPage){
                    var data = _getSearchData();
                    data.startPageNo = _config.currentPage - 1;
                    var uuid = GLOBAL.generateUUID();
                    data.uuid = uuid;
                    _config.uuid = uuid;
                    _config.isClickDelay = true;
                    $.ajax({
                        type:"POST",
                        url:_config.currentUrl,
                        async:true,
                        data:data,
                        cache:false,
                        success:function(result){
                            if(_config.uuid == uuid){
                                _drawWideScreenProducts(result);
                            }
                        }
                    });
                }
            });

            _config.$leftAdDom.find(".J_left_next_page").click(function () {
                if(_config.hasNextPage){
                    var data = _getSearchData();
                    data.startPageNo = _config.currentPage + 1;
                    if(_config.isLastPage || _config.currentPage*_config.pageSize >= _config.maxAutoReadSize){
                        data.startPageNo = 1;
                    }
                    var uuid = GLOBAL.generateUUID();
                    data.uuid = uuid;
                    _config.uuid = uuid;
                    _config.isClickDelay = true;
                    $.ajax({
                        type:"POST",
                        url:_config.currentUrl,
                        async:true,
                        data:data,
                        cache:false,
                        success:function(result){
                            if(_config.uuid == uuid){
                                 _drawWideScreenProducts(result);
                            }
                        }
                    });
                }
            });


            _config.$leftNarrowAdDom.find(".J_left_prev_page").click(function () {
                if(_config.hasPreviousPage){
                    var data = _getSearchData();
                    data.startPageNo = _config.currentPage - 1;
                    var uuid = GLOBAL.generateUUID();
                    data.uuid = uuid;
                    _config.uuid = uuid;
                    _config.isClickDelay = true;
                    $.ajax({
                        type:"POST",
                        url:_config.currentUrl,
                        async:true,
                        data:data,
                        cache:false,
                        success:function(result){
                            if(_config.uuid == uuid){
                                _drawLeftNarrowScreenProducts(result);
                            }
                        }
                    });
                }
            });

            _config.$leftNarrowAdDom.find(".J_left_next_page").click(function () {
                if(_config.hasNextPage){
                    var data = _getSearchData();
                    data.startPageNo = _config.currentPage + 1;
                    if(_config.isLastPage || _config.currentPage*_config.pageSize >= _config.maxAutoReadSize){
                        data.startPageNo = 1;
                    }
                    var uuid = GLOBAL.generateUUID();
                    data.uuid = uuid;
                    _config.uuid = uuid;
                    _config.isClickDelay = true;
                    $.ajax({
                        type:"POST",
                        url:_config.currentUrl,
                        async:true,
                        data:data,
                        cache:false,
                        success:function(result){
                            if(_config.uuid == uuid){
                                _drawLeftNarrowScreenProducts(result);
                            }
                        }
                    });
                }
            });

            _config.$bottomAdDom.find(".J_bottom_close").click(function(){
                _config.$bottomAdDom.hide();
                _config.isActive = false;
                _config.$hideAdDom.find(".J_show_ad").css({bottom:0});
                _config.$hideAdDom.show();
            });
            _config.$bottomAdDom.find(".J_bottom_search_btn").click(function(){
                var searchWord = _config.$bottomAdDom.find(".J_bottom_search_word").val();
                window.open("autoAccessoryOnline.do?method=toCommodityQuotations&searchWord=" + searchWord);
            });

            _config.$bottomAdDom.find(".J_bottom_prev_page").click(function () {
                if(_config.hasPreviousPage){
                    var data = _getSearchData();
                    data.startPageNo = _config.currentPage - 1;
                    var uuid = GLOBAL.generateUUID();
                    data.uuid = uuid;
                    _config.uuid = uuid;
                    _config.isClickDelay = true;
                    $.ajax({
                        type:"POST",
                        url:_config.currentUrl,
                        async:true,
                        data:data,
                        cache:false,
                        success:function(result){
                            if(_config.uuid == uuid){
                                if(_config.screen == "wideScreen"){
                                    _drawWideScreenProducts(result);

                                }else{
                                    _drawNarrowScreenProducts(result);
                                }
                            }
                        }
                    });
                }
            });
            _config.$bottomAdDom.find(".J_bottom_next_page").click(function () {
                if(_config.hasNextPage){
                    var data = _getSearchData();
                    data.startPageNo = _config.currentPage + 1;
                    if(_config.isLastPage || _config.currentPage*_config.pageSize >= _config.maxAutoReadSize){
                        data.startPageNo = 1;
                    }
                    var uuid = GLOBAL.generateUUID();
                    data.uuid = uuid;
                    _config.uuid = uuid;
                    _config.isClickDelay = true;
                    $.ajax({
                        type:"POST",
                        url:_config.currentUrl,
                        async:true,
                        data:data,
                        cache:false,
                        success:function(result){
                            if(_config.uuid == uuid){
                                if(_config.screen == "wideScreen"){
                                    _drawWideScreenProducts(result);

                                }else{
                                    _drawNarrowScreenProducts(result);
                                }
                            }
                        }
                    });
                }
            });

            $(".J_left_product_container,.J_bottom_product_container").live("click",function(){
                var productId = $(this).attr("data-product-id");
                var shopId = $(this).attr("data-shop-id");
                if(G.Lang.isNotEmpty(productId) && G.Lang.isNotEmpty(shopId)){
                    window.open("shopProductDetail.do?method=toShopProductDetail&paramShopId="+shopId+"&productLocalId="+productId);
                }
            });
            var intervalFun = function(){
                //前台超时，页面隐藏，手动hidden的
                if(_isScheduleRun()){
                    if(_config.isClickDelay ){
                        _config.isClickDelay = false;
                        return;
                    }
                    var url = _config.commodityAdUrl;
                    var data = _getSearchData();
                    data.startPageNo = _config.currentPage + 1;
                    if(_config.isLastPage || _config.currentPage*_config.pageSize >= _config.maxAutoReadSize){
                        data.startPageNo = 1;
                    }
                    if(!G.Lang.isAllEmpty(_config.searchWord,_config.productName,_config.productBrand,_config.productSpec,
                        _config.productModel,_config.productVehicleModel,_config.productVehicleBrand)){
                        url = _config.searchWordAdUrl;
                    }
                    _config.currentUrl = url;
                    var uuid = GLOBAL.generateUUID();
                    data.uuid = uuid;
                    _config.uuid = uuid;
                    $.ajax({
                        type:"POST",
                        url:_config.currentUrl,
                        async:true,
                        data:data,
                        cache:false,
                        success:function(result){
                            if(_config.uuid == uuid){
                                if(_config.screen == "wideScreen"){
                                    _drawWideScreenProducts(result);
                                }else if(_config.screen == "leftNarrowScreen"){
                                    _drawLeftNarrowScreenProducts(result);
                                }else{
                                    _drawNarrowScreenProducts(result);
                                }
                            }
                        }
                    });

                }
            };
            intervalFun();
            _config.intervalFun = setInterval(intervalFun,_config.intervalTime);
            var orderType = $("#orderType").val() || $("#pageType").val();
            if(orderType == "repairOrder" || orderType == "goodsSaleOrder"  || orderType == "purchaseInventoryOrder" ){
                setInterval(function(){
                    if(_isScheduleRun()){
                        var $productNames  = $("input[id$='.productName']");
                        if($productNames && $productNames.size()>0){
                            var isOrderEmpty = true;
                            for (var productNameIndex = $productNames.size() - 1; productNameIndex >= 0; productNameIndex--) {
                                var $ProductName = $productNames.eq(productNameIndex);
                                var idPrefix = $ProductName.attr("id").split(".")[0];
                                var productName = $("#"+idPrefix + "\\.productName").val();
                                var productBrand = $("#"+idPrefix + "\\.brand").val();
                                var productSpec = $("#"+idPrefix + "\\.spec").val();
                                var productModel = $("#"+idPrefix + "\\.model").val();
                                var productVehicleModel = $("#"+idPrefix + "\\.vehicleModel").val();
                                var productVehicleBrand = $("#"+idPrefix + "\\.vehicleBrand").val();

                                if (!G.Lang.isAllEmpty(productName, productBrand, productSpec, productModel,productVehicleModel,productVehicleBrand)) {
                                    isOrderEmpty = false;
                                    if (!(G.Lang.compareStrSame(productName, _config.productName)
                                        && G.Lang.compareStrSame(productBrand, _config.productBrand)
                                        && G.Lang.compareStrSame(productSpec, _config.productSpec)
                                        && G.Lang.compareStrSame(productModel, _config.productModel)
                                        && G.Lang.compareStrSame(productVehicleModel, _config.productVehicleModel)
                                        && G.Lang.compareStrSame(productVehicleBrand, _config.productVehicleBrand)
                                        )) {
                                        _config.productName = productName;
                                        _config.productBrand = productBrand;
                                        _config.productSpec = productSpec;
                                        _config.productModel = productModel;
                                        _config.productVehicleModel = productVehicleModel;
                                        _config.productVehicleBrand = productVehicleBrand;
                                        _config.currentPage = 0;
                                        return;
                                    }
                                }
                            }
                            if(isOrderEmpty && !G.Lang.isAllEmpty(_config.productName,_config.productBrand,_config.productSpec,
                                _config.productModel,_config.productVehicleModel,_config.productVehicleBrand)){
                                _config.productName = "";
                                _config.productBrand = "";
                                _config.productSpec = "";
                                _config.productModel = "";
                                _config.productVehicleModel = "";
                                _config.productVehicleBrand = "";
                                _config.currentPage = 0;
                            }

                        }

                    }

                },_config.scanProductTime);
            }else if(orderType == "stockSearch"){
                setInterval(function () {
                    if (_isScheduleRun()) {
                        var isOrderEmpty = true;
                        var strUtil = new commonStrUtil();
                        var searchWord =strUtil.excludeSpecifiedStr($("#searchWord").attr("initialValue"), $("#searchWord").val());
                        var productName = strUtil.excludeSpecifiedStr($("#productName").attr("initialValue"), $("#productName").val());
                        var productBrand = strUtil.excludeSpecifiedStr($("#productBrand").attr("initialValue"), $("#productBrand").val());
                        var productSpec = strUtil.excludeSpecifiedStr($("#productSpec").attr("initialValue"), $("#productSpec").val());
                        var productModel = strUtil.excludeSpecifiedStr($("#productModel").attr("initialValue"), $("#productModel").val());
                        var productVehicleBrand = strUtil.excludeSpecifiedStr($("#productVehicleBrand").attr("initialValue"), $("#productVehicleBrand").val());
                        var productVehicleModel = strUtil.excludeSpecifiedStr($("#productVehicleModel").attr("initialValue"), $("#productVehicleModel").val());

                        if (!G.Lang.isAllEmpty(searchWord, productName, productBrand, productSpec, productModel,
                            productVehicleModel, productVehicleBrand)) {
                            isOrderEmpty = false;
                            if (!(G.Lang.compareStrSame(searchWord, _config.searchWord)
                                && G.Lang.compareStrSame(productName, _config.productName)
                                && G.Lang.compareStrSame(productBrand, _config.productBrand)
                                && G.Lang.compareStrSame(productSpec, _config.productSpec)
                                && G.Lang.compareStrSame(productModel, _config.productModel)
                                && G.Lang.compareStrSame(productVehicleModel, _config.productVehicleModel)
                                && G.Lang.compareStrSame(productVehicleBrand, _config.productVehicleBrand)
                                )) {
                                _config.searchWord = searchWord;
                                _config.productName = productName;
                                _config.productBrand = productBrand;
                                _config.productSpec = productSpec;
                                _config.productModel = productModel;
                                _config.productVehicleModel = productVehicleModel;
                                _config.productVehicleBrand = productVehicleBrand;
                                _config.currentPage = 0;
                                return;
                            }
                        }
                        if (isOrderEmpty && !G.Lang.isAllEmpty(_config.searchWord, _config.productName, _config.productBrand, _config.productSpec,
                            _config.productModel, _config.productVehicleModel, _config.productVehicleBrand)) {
                            _config.searchWord = "";
                            _config.productName = "";
                            _config.productBrand = "";
                            _config.productSpec = "";
                            _config.productModel = "";
                            _config.productVehicleModel = "";
                            _config.productVehicleBrand = "";
                            _config.currentPage = 0;
                        }
                    }

                },_config.scanProductTime);
            }

        },
        getConfig: function (name) {
            _getConfig(name);
        },
        setConfig: function (name, value) {
            _setConfig(name, value);
        }
    }
}());
App.Module.AdShow = adShow;

$(function () {
//    adShow.start();
});