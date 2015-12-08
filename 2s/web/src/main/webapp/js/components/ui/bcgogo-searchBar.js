/*
 * 此文件依赖，  base.js, application.js , jquery-1.4.2+, jquery-tooltip, tipsy, bcgogo-droplist.js  , bcgogo-droplist-lite.js ,  jquery.datePicker.js
 *
 *
 * */
;
(function () {
    APP_BCGOGO.namespace("Module.searchBar");

    var me,
        droplist = APP_BCGOGO.Module.droplist,
        C = {
            content: "bcgogo-searchBar-content",
            rules_input: "bcgogo-searchBar-rules-input",
            rules_time: "bcgogo-searchBar-rules-time",
            rules_product: "bcgogo-searchBar-rules-product",
            search: "bcgogo-searchBar-searchButton",
            searchButtonImg: "bcgogo-searchBar-searchButtonImg",
            waiting: "bcgogo-searchBar-waiting",
            content_block: "bcgogo-searchBar-content-block",

            // fuzzy field
            J_product_info: "J-fuzzy_search",

            J_product_name: "J-product_name",
            J_product_brand: "J-product_branch",
            J_product_spec: "J-product_spec",
            J_product_model: "J-product_model",
            J_product_vehicle_model: "J-product_vehicle_model",
            J_product_vehicle_brand: "J-product_vehicle_brand",
            J_commodity_code: "J-commodity_code",
            J_noInput: "bcgogo-searchBar-NoInput",
            J_alreadyInput: "bcgogo-searchBar-AlreadyInput",
            J_startTimeStr: "bcgogo-searchBar-startTimeStr",
            J_endTimeStr: "bcgogo-searchBar-endTimeStr",
            J_services: "bcgogo-searchBar-services"
        },
        NoticeValue = {
            // fuzzy field
            product_info: "品名/品牌/规格/型号/车型/车辆品牌/商品编号",

            product_name: "品名",
            product_brand: "品牌",
            product_spec: "规格",
            product_model: "型号",
            product_vehicle_model: "车型",
            product_vehicle_brand: "车辆品牌",
            commodity_code: "商品编号",
            services: "施工内容"
        },
        T = {
            content: '' +
                '<div class="' + C.content + '">' +
                '    <div class="' + C.rules_input + ' ' + C.content_block + '">' +
                '        <ol class="' + C.rules_time + '">' +
                '            <li>时间条件:</li>' +
                '            <li><label><input type="radio" name="searchBar-time" value="thisDay">今日</label></li>' +
                '            <li><label><input type="radio" name="searchBar-time" value="thisMonth">本月</label></li>' +
                '            <li><label><input type="radio" name="searchBar-time" value="thisYear">本年</label></li>' +
                '            <li>' +
                '                &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;' +
                '                <label><input type="radio" name="searchBar-time" value="custom">自定义&nbsp;&nbsp;&nbsp;</label>' +
                '                <input type="text" class="w-100 ' + C.J_startTimeStr + '">&nbsp;至&nbsp;<input type="text" class="w-100 ' + C.J_endTimeStr + '">' +
                '            </li>' +
                '        </ol>' +
                '        <ol class="' + C.rules_product + '">' +
                '            <li>商品条件:</li>' +
                '            <li><input type="text" class="w-170 fuzzy_search ' + C.J_product_info + '"/></li>' +
                '            <li><input type="text" class="w-80 ' + C.J_product_name + '"></li>' +
                '            <li><input type="text" class="w-60 ' + C.J_product_brand + '"></li>' +
                '            <li><input type="text" class="w-60 ' + C.J_product_spec + '"></li>' +
                '            <li><input type="text" class="w-60 ' + C.J_product_model + '"></li>' +
                '            <li><input type="text" class="w-60 ' + C.J_product_vehicle_model + '"></li>' +
                '            <li><input type="text" class="w-60 ' + C.J_product_vehicle_brand + '"></li>' +
                '            <li><input type="text" class="w-100 ' + C.J_commodity_code + '"></li>' +
                '            <li><input type="text" class="w-60 ' + C.J_services + '"></li>' +
                '         </ol>' +
                '    </div>' +
                '    <div class="' + C.search + ' ' + C.content_block + '">' +
                '         <div class="' + C.searchButtonImg + '">查询</div>' +
                '    </div>' +
                '    <div class="' + C.waiting + '"></div>' +
                '</div>'
        },
    // parameters module
        P = {
            // 单据查询  传参 样例
            orderSearch: {
                //车牌号
                vehicle: "",
                // uuid
                uuid: "",
                // 单据类型
                orderType: "",
                // 单据类型
                pageOrderType: "",
                // 单据状态
                orderStatus: "",
                // 时间
                now: "",
                // 供应商 / 客户
                customerOrSupplierName: "",
                // 品名
                productName: "",
                // 商品编号
                commodityCode: "",
                // 品牌、产地
                productBrand: "",
                // 规格
                productSpec: "",
                // 型号
                productModel: "",
                // 车辆品牌
                productVehicleBrand: "",
                // 车型
                productVehicleModel: "",
                // 施工内容
                service: "",
                // 每行多少个数据
                pageRows: 15,
                // 开始于第几个
                rowStart: 0
            },

            // 自动补全  传参 样例
            autocompleteSearch: {
                // uuid
                uuid: "",
                // 显示的 ， 搜索内容
                searchWord: "",
                // 显示的 ， 搜索框名
                searchField: "",
                // field name is : "commodity_code"
                commodityCode: "",
                // field name is : "product_info"
                productName: "",
                // field name is : "product_brand"
                productBrand: "",
                // field name is : "product_spec"
                productSpec: "",
                // field name is : "product_model"
                productModel: "",
                // field name is : "product_vehicle_brand"
                productVehicleBrand: "",
                // field name is : "product_vehicle_model"
                productVehicleModel: "",
                // field name is : "services"
                services: "",

                pageRows: 15
            }
        },
        MAP = {
            classToParam: {
                "orderSearch": {
                    "J_product_name": "productName",
                    "J_product_brand": "productBrand",
                    "J_product_spec": "productSpec",
                    "J_product_model": "productModel",
                    "J_product_vehicle_brand": "productVehicleBrand",
                    "J_product_vehicle_model": "productVehicleModel",
                    "J_commodity_code": "commodityCode",
                    "J_services": "service"
                },
                "autocomplete": {
                    "J_product_name": "productName",
                    "J_product_brand": "productBrand",
                    "J_product_spec": "productSpec",
                    "J_product_model": "productModel",
                    "J_product_vehicle_brand": "productVehicleBrand",
                    "J_product_vehicle_model": "productVehicleModel",
                    "J_commodity_code": "commodityCode",
                    "J_services": "services"
                }
            },
            classToSearchFieldName: {
                "orderSearch": {},
                "autocomplete": {
                    "J_product_name": "product_name",
                    "J_product_model": "product_model",
                    "J_product_brand": "product_brand",
                    "J_product_spec": "product_spec",
                    "J_product_vehicle_model": "product_vehicle_model",
                    "J_product_vehicle_brand": "product_vehicle_brand",
                    "J_commodity_code": "commodity_code",
                    "J_services": "services"
                },
                "fuzzySearch": {
                    "J_product_info": "product_info"
                }
            }
        },
        URL = {
            autocompleteSearch: "product.do?method=getProductSuggestion",
            orderSearch: "searchInventoryIndex.do?method=getOrderItemDetails",
            // TODO 混合搜索框
            fuzzySearch: "product.do?method=getProductSuggestion"
        },
        RULES = {
            orderType: {
                "INVENTORY": "INVENTORY",
                "RETURN": "INVENTORY",
                "PURCHASE": "PURCHASE",
                "SALE": "SALE",
                "SALE_RETURN": "SALE,REPAIR",
                "REPAIR": "REPAIR"
            },
            orderStatus: {
                "INVENTORY": "",
                "RETURN": "",
                "PURCHASE": "PURCHASE_ORDER_DONE",
                "SALE": "",
                "SALE_RETURN": "REPAIR_SETTLED,SALE_DONE,SALE_DEBT_DONE",
                "REPAIR": ""
            }
        };

    APP_BCGOGO.Module.searchBar = {
        _$: null,
        _uuid: "",
        _orderType: "",
        _responseData: null,
        customerOrSupplierNameVehicle: "",
        _autocompleteTimerId: 0,
        _autocompleteDelay: 300,
        _isTest: false,

        _getDroplist: function () {
            return APP_BCGOGO.Module.droplist;
        },

        _init: function (param) {
            me._orderType = param["orderType"];
            me.customerOrSupplierNameVehicle = param["customerOrSupplierName"] || "";
            me.beforeSearch = param["beforeSearch"] || me.beforeSearch;
            me.afterSearch = param["afterSearch"] || me.afterSearch;

            // searchBarInterface set
            if (param["searchBarInterface"]) {
                URL.autocompleteSearch = param["searchBarInterface"]["autocompleteSearch"] || URL.autocompleteSearch;
                URL.orderSearch = param["searchBarInterface"]["orderSearch"] || URL.orderSearch;
                URL.fuzzySearch = param["searchBarInterface"]["fuzzySearch"] || URL.fuzzySearch;
            }

            me._$ = $("<div id='id-bcgogo-searchBar-" + G.generateUUID() + "'></div>");
            me._$.append($(T.content));

            // 暂时不使用 waiting 状态
            $("." + C.waiting, me._$).hide();

            // TODO events, uis
            me._initUi();
            me._initEvent();
        },

        _initExactSearchFieldEvent: function () {
            var droplist = me._getDroplist();

            // 搜素框的提示字词功能
            for (var p in NoticeValue) {
                if (p === "product_info") continue;

                $("." + C["J_" + p], me._$)
                    .bind("focus", function (event) {
                        me._checkNoticeValueToClear($(this));
                    })
                    .bind("blur", function (event) {
                        me._checkNoticeValueToRevert($(this));
                    });
            }

            var getAutocompleteParameters = function ($input) {
                var retParams = {};
                for (var p in MAP.classToParam.autocomplete) {
                    if ($input.hasClass(C[p])) {
                        retParams[MAP.classToParam.autocomplete[p]] = "";
                    } else {
                        retParams[MAP.classToParam.autocomplete[p]] = G.normalize($("." + C[p], me._$).not("." + C.J_noInput).val());
                    }
                }

                var searchField = "";
                for (var q in MAP.classToParam.autocomplete) {
                    if ($input.hasClass(C[q])) {
                        searchField = MAP.classToSearchFieldName.autocomplete[q];
                        break;
                    }
                }
                retParams["searchField"] = searchField;
                retParams["searchWord"] = $input.val();
                return retParams;
            };

            var onAutocomplete = function (event) {
                clearTimeout(me._autocompleteTimerId);
                var uuid = G.generateUUID(),
                    $inst = $(event.currentTarget),
                    data = getAutocompleteParameters($inst);

                data.uuid = uuid;
                droplist.setUUID(uuid);

                me._autocompleteTimerId = setTimeout(function () {
                    App.Net.asyncAjax({
                        url: URL.autocompleteSearch,
                        dataType: "json",
                        data: data,
                        success: function (responseData) {
                            if (me._isTest) {
                                droplist.setUUID(responseData["uuid"]);
                            }

                            droplist.show({
                                isEditable: false,
                                isDeletable: false,
                                autoSet: true,
                                data: responseData,
                                selector: $inst,
                                isIgnoreMinWidth: false,
                                onSelect: function (event, index, data, hook) {
                                    $inst
                                        .val(data["label"])
                                        .removeClass(C.J_noInput)
                                        .addClass(C.J_alreadyInput);
                                    droplist.hide();
                                },
                                onKeyboardSelect: function (event, index, data, hook) {
                                }
                            });
                        },
                        error: function () {
                        },
                        complete: function () {
                        }
                    });
                }, me._autocompleteDelay);
            };

            // 下拉菜单, 键盘补全功能
            // click 、 focus 、 键盘输入（非控制键时 , 控制键有 up,down,left,right,enter,esc）
            var autocompleteSelector_rulesOfCommidityCode = "" +
                    "." + C.J_commodity_code,
                autocompleteSelector_rulesOfName = "" +
                    "." + C.J_product_name +
                    ", ." + C.J_product_model +
                    ", ." + C.J_product_brand +
                    ", ." + C.J_product_spec +
                    ", ." + C.J_product_vehicle_model +
                    ", ." + C.J_product_vehicle_brand;
//                    ", ." + C.J_services;
            $(autocompleteSelector_rulesOfCommidityCode +
                ", " + autocompleteSelector_rulesOfName, me._$).bind("focus", onAutocomplete);

            var onKeyBoardComplete = function (event) {
                if (G.contains(G.keyNameFromEvent(event), ["up", "down", "left", "right", /*"enter",*/ "backspace", "esc"]) === false) {
                    onAutocomplete(event);
                }
            };

            var filter = App.StringFilter;
            $(autocompleteSelector_rulesOfName, me._$).bind("keyup", function (event) {
                var filedValue = filter.inputtingNameFilter(this.value)
                if (filedValue !== this.value) {
                    this.value = filedValue;
                }
                onKeyBoardComplete(event);
            });
            $(autocompleteSelector_rulesOfCommidityCode, me._$).bind("keyup", function (event) {
                var filedValue = filter.commodityCodeFilter(this.value);
                if (filedValue !== this.value) {
                    this.value = filedValue;
                }
                onKeyBoardComplete(event);
            });
        },

        _initFuzzySearchFieldEvent: function () {
            var droplist = me._getDroplist();

            var $fuzzySearch = $("." + C.J_product_info, me._$);
            $fuzzySearch
                .bind("focus", function (event) {
                    me._checkNoticeValueToClear($(this));
                })
                .bind("blur", function (event) {
                    me._checkNoticeValueToRevert($(this));
                })
                .attr("title", NoticeValue.product_info);


            // get autocomplete params
            var onAutocomplete = function (event) {
                clearTimeout(me._autocompleteTimerId);
                var uuid = G.generateUUID(),
                    $inst = $(event.currentTarget),
                    data = {
                        "searchField": MAP.classToSearchFieldName["fuzzySearch"]["J_product_info"],
                        "searchWord": $fuzzySearch.val()
                    };

                data.uuid = uuid;
                droplist.setUUID(uuid);

                me._autocompleteTimerId = setTimeout(function () {
                    App.Net.asyncAjax({
                        url: URL.fuzzySearch,
                        dataType: "json",
                        data: data,
                        success: function (responseData) {
                            if (me._isTest) {
                                droplist.setUUID(responseData["uuid"]);
                            }

                            droplist.show({
                                isEditable: false,
                                isDeletable: false,
                                autoSet: false,
                                data: responseData,
                                selector: $inst,
                                isIgnoreMinWidth: false,
                                onGetInputtingData: function() {
                                    return {
                                        details:me._getExactInputsValues()
                                    };
                                },
                                onSelect: function (event, index, data, hook) {
                                    var details = data.details;
                                    me._setExactInputsValues(details);
                                    // TODO 这里要替换成 补全后面的字段， 并且 fuzzySearch 的框颜色需要修改
                                    droplist.hide();
                                },
                                onKeyboardSelect: function (event, index, data, hook) {
                                    // TODO 这里要替换成 补全后面的字段， 并且 fuzzySearch 的框颜色需要修改
                                    var details = data.details;
                                    me._setExactInputsValues(details);
                                    G.debug(details);
                                }
                            });
                        },
                        error: function () {
                        },
                        complete: function () {
                        }
                    });
                }, me._autocompleteDelay);
            };

            var onKeyBoardComplete = function (event) {
                if (G.contains(G.keyNameFromEvent(event), ["up", "down", "left", "right", /*"enter",*/ "backspace", "esc"]) === false) {
                    onAutocomplete(event);
                }
            };


            $fuzzySearch.bind("focus", onAutocomplete);

            var filter = App.StringFilter;
            $fuzzySearch.bind("keyup", function (event) {
                var filedValue = filter.inputtingNameFilter(this.value)
                if (filedValue !== this.value) {
                    this.value = filedValue;
                }
                onKeyBoardComplete(event);
            });

        },

        _getExactInputsValues: function () {
            var retData = {};
            for (var k in NoticeValue) {
                if (k === "product_info") {
                    continue;
                }

                var $item = $("." + C["J_" + k]);
                if (!$item[0]) {
                    continue;
                }

                retData[k] = $item.val();
            }
            return retData;
        },

        _setExactInputsValues: function (data) {
            for (var k in data) {
                var $item = $("." + C["J_" + k]);
                if (!$item[0]) {
                    continue;
                }

                $item.val(data[k]);
                me._checkNoticeValueToRevert($item);
                me._checkNoticeValueToSetAlreayInput($item);
            }
        },

        _checkNoticeValueToClear: function ($node) {
            if ($node.val() === $node.attr("noticeValue")
                && $node.hasClass(C.J_noInput)) {
                $node.removeClass(C.J_noInput).addClass(C.J_alreadyInput).val("");
            }
        },

        _checkNoticeValueToSetAlreayInput: function($node) {
            if(!G.isEmpty($node.val())
                && $node.val() !== $node.attr("noticeValue")) {
                $node.removeClass(C.J_noInput).addClass(C.J_alreadyInput);
            }
        },

        _checkNoticeValueToRevert: function ($node) {
            if (G.isEmpty($node.val()) || $node.val() === $node.attr("noticeValue")) {
                $node.removeClass(C.J_alreadyInput).addClass(C.J_noInput).val($node.attr("noticeValue"));
            }
        },

        _initSearchButtonEvent: function () {
            // 搜索按钮
            $("." + C.search, me._$).bind("click", me._onSearchButtonClick);
        },

        _initEvent: function () {
            me._initSearchButtonEvent();
            me._initExactSearchFieldEvent();
            me._initFuzzySearchFieldEvent();
        },
        _initUi: function () {
            // init Date
            var selector = "" +
                "." + C.J_startTimeStr +
                ", ." + C.J_endTimeStr;
            $(selector, me._$)
                .bind("click focus", function () {
                    this.blur();
                    $("input[type='radio'][value='custom']").attr("checked", true);
                })
                .datepicker({
                    numberOfMonths: 1,
                    showButtonPanel: false,
                    changeYear: true,
                    changeMonth: true,
                    yearRange: "c-100:c+100",
                    yearSuffix: "",
                    hideIfNoPrevNext: true
                });

            me.resetUi();

        },

        updateStyles: function (state) {
        },

        /**
         *
         * @param param 格式如 { rowStart:0, pageRows:25, now:"123" }
         */
        manualSearch: function (param) {
            me._onSearch(param);
        },

        /*
         * 可以在这里加入预处理逻辑， 比如， 如果我们使用 searchcomplete 组件， 那么我们可能需要 告诉 searchcomplete 本身，
         * 你从现在开始 上一页 下一页按钮 ， 和现在的搜索上下文相关
         * */
        beforeSearch: function (event) {
            G.debug("before search!");
        },
        /*
         * 可以在这里加入扫尾工作， 比如， 如果我们使用 searchcomplete 组件， 可能会有一些状态重置操作， 但是现在还用不到
         * */
        afterSearch: function (responseData, textStatus) {
            G.debug("after search!");
        },
        /**
         *
         * @param param 自定义参数
         * @private
         */
        _onSearch: function (param) {
            var requestParam = me.getSearchRequestData();
            if (param) {
                for (var p in param) {
                    requestParam[p] = param[p];
                }
            }

            // use "sync" now,  if we have plenty of time, refactoring to "async".
            App.Net.syncPost({
                data: requestParam,
                url: URL.orderSearch,
                dataType: "json",
                success: function (data) {
                    me._responseData = data;
                },
                error: function () {
                    me._responseData = null;
                },
                complete: function (jqXHR, textStatus) {
                    me.afterSearch(me._responseData, textStatus);
                    // TODO update UIs "idle" , pending!
                }
            });
        },

        _onSearchButtonClick: function (event) {
            // TODO 做搜索的功能
            me.beforeSearch(event);

            var This = event.currentTarget;
            // TODO update UIs to "waiting", pending!

            me._onSearch(null);
        },

        getSearchRequestData: function () {
            // TODO "fuzzy" -- "模糊", "exact" -- "精确" 现在只实现精确搜索
            var param = {};

            // init param modules
            for (var q in P.orderSearch) {
                param[q] = P.orderSearch[q];
            }

            // get ui parameters
            var $item;
            for (var p in MAP.classToParam.orderSearch) {
                $item = $("." + C[p], me._$);
                if ($item.hasClass(C.J_alreadyInput)) {
                    param[MAP.classToParam.orderSearch[p]] = G.normalize(G.trim($item.val()));
                } else {
                    param[MAP.classToParam.orderSearch[p]] = "";
                }
            }

            // set "uuid"
            me._uuid = G.generateUUID();
            param["uuid"] = me._uuid;

            // set "pageOrderType"
            param["pageOrderType"] = me._orderType;

            // set "orderType"
            param["orderType"] = RULES.orderType[me._orderType];

            // set "orderStatus"
            param["orderStatus"] = RULES.orderStatus[me._orderType];

            // set "now"
            param["now"] = new Date().getTime() + "";

            // set "customerOrSupplierName or vehicle"
            if (param["pageOrderType"] === "REPAIR") {
                param["vehicle"] = me.customerOrSupplierNameVehicle;
            } else {
                param["customerOrSupplierName"] = me.customerOrSupplierNameVehicle;
            }

            // default  pageRows = 25;  rowStart = 0;
            //          if developer want to change this, please call manualSearch() function outside!

//            P.orderSearch << test data

            // time
            var timeScope = me.getTimeScope(me.getTimeSelectedValue());
            param["startTimeStr"] = timeScope["startTimeStr"];
            param["endTimeStr"] = timeScope["endTimeStr"];

            return param;
        },

        /**
         * 返回 请求获得的数据
         * @returns {*}
         */
        getSearchResponseData: function () {
            return me._responseData;
        },

        /**
         * 返回 searchBar 本身的 jqDom 对象
         * @param param {
         *     // @optional
         *     searchBarInterface:{
         *         autocompleteSearch:"",
         *         orderSearch:"",
         *         fuzzySearch:""
         *     },
         *
         *     //@required
         *     orderType:"",
         *
         *     //@required
         *     customerOrSupplierName:"",
         *
         *     //@required
         *     beforeSearch:function(){},
         *
         *     //@required
         *     afterSearch:function(){}
         * }
         * @returns {*|jQuery|HTMLElement|ScorePanel._$|MenuPanel._$|Ratting._$|_$|_$}
         */
        getInstance: function (param) {
            if (!me._$) {
                me._init(param);
            } else {
                G.info("The SearchBar has already been created.  param will not be set!");
            }
            return me._$;
        },

        show: function () {
            me._$.show();
        },
        hide: function () {
            me._$.hide();
        },

        /**
         * 返回 radio 时间选择组件选中选项的 值
         * @returns {*|jQuery}
         */
        getTimeSelectedValue: function () {
            return $("input[type='radio']")
                .filter(function () {
                    return $(this).attr("checked") == true
                })
                .val();
        },

        /**
         * 根据 radio 时间选择组件选中的值， 来返回 时间范围参数
         * @param scopeName
         * @returns {*}
         */
        getTimeScope: function (scopeName) {
            switch (scopeName) {
                case "thisDay":
                    return {"startTimeStr": "今天", "endTimeStr": "今天"};
                    break;

                case "thisMonth":
                    return {"startTimeStr": "本月第一天", "endTimeStr": "今天"};
                    break;

                case "thisYear":
                    return {"startTimeStr": "今年第一天", "endTimeStr": "今天"};
                    break;

                case "custom":
                    return {
                        "startTimeStr": $("." + C.J_startTimeStr, me._$).val(),
                        "endTimeStr": $("." + C.J_endTimeStr, me._$).val()
                    }
                    break;
            }
        },

//        hook:function() {},
        resetUi: function () {
            me._data = null;
            // reset fuzzy textInput
            $("." + C.J_product_info)
                .val(NoticeValue.product_info)
                .attr("noticeValue", NoticeValue.product_info)
                .removeClass(C.J_alreadyInput)
                .addClass(C.J_noInput);

            // reset textInputs
            for (var p in NoticeValue) {
                $("." + C["J_" + p], me._$)
                    .val(NoticeValue[p])
                    .attr("noticeValue", NoticeValue[p])
                    .removeClass(C.J_alreadyInput)
                    .addClass(C.J_noInput);
            }

            // reset radios
            $("input[type='radio'][value='thisYear']").attr("checked", true);
            $("input", me._$).filter("." + C.J_startTimeStr + ", ." + C.J_endTimeStr).val(G.getCurrentFormatDate());

            if (me._orderType !== "REPAIR") {
                $("." + C.J_services, me._$).hide();
            }
        },

        /*
         * if customerOrSupplierName is changed， don't forget to call this function to tell me
         * */
        setCustomerOrSupplierNameOrVehicle: function (value) {
            me.customerOrSupplierNameVehicle = value;
        }
    };
    me = APP_BCGOGO.Module.searchBar;

})();
