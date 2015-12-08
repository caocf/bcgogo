;
(function () {
    var me,
        C = {
            searchcomplete:"bcgogo-ui-searchcomplete",
            autocomplete:"bcgogo-ui-searchcomplete-autocomplete",
            detailsList:"bcgogo-ui-searchcomplete-detailsList-order",
            searchBar:"bcgogo-ui-searchcomplete-searchBar"
        },
        T = {
            content:'<div class=' + C.searchcomplete + '>'
                + '    <div class=' + C.autocomplete + '></div>'
                + '    <div class=' + C.detailsList + '></div>'
                + '    <div class=' + C.searchBar + '></div>'
                + '</div>'
        },
        SALE_ORDER_ADJUST = "-230px";

    APP_BCGOGO.namespace("APP_BCGOGO.Module.searchcompleteMultiselect");
    /**
     * @author 潘震
     * @description searchcomplete 组件的 detailsList 是可以多选的
     *              在用户单击选中列表中的数据后 ，点击 “确认” 按钮， 会触发 回调函数，回调函数中将会将所有选中的数据都回传给 回调函数，
     *              若要使用此组件， 可以从 sampleCode 中快速上手
     * @date 2012-08-28
     */
    APP_BCGOGO.Module.searchcompleteMultiselect = {
        _uuid:"",
        _target:null,
        _relInst:null,
        autocomplete:APP_BCGOGO.Module.autocompleteMultiselect,
        detailsList:APP_BCGOGO.Module.detailsListMultiselect,
        searchBar:APP_BCGOGO.Module.searchBar,
        _data:null,
        isScroll:false,
        isTransparentAuto:false,
        /*
        * searching,
        * detailsSearch,
        * autoSearch
        * */
        state:"autoSearch",
        STATE:{
            SEARCHING:"searching",
            DETAILS_SEARCH:"detailsSearch",
            AUTO_SEARCH:"autoSearch"
        },

        /**
         * @description 设置组件的 UUID， 同时将UUID 设置给两个子组件 autocomplete, detailsList
         * @param {String} value
         */
        setUUID:function (value) {
            me._uuid = value;
            me.autocomplete.setUUID(value);
            me.detailsList.setUUID(value);
        },

        /**
         * @description 获取组件记录的 UUID
         * @return {String}
         */
        getUUID:function () {
            return me._uuid;
        },

        setPageSize:function (pageSize) {
            me.detailsList._pageSize = pageSize;
        },

        getPageSize:function () {
            return me.detailsList._pageSize;
        },

        getPageIndex:function () {
            return me.detailsList.getPageIndex();
        },

        setPageIndex:function (value) {
            me.detailsList.setPageIndex(value < 0 ? 0 : value);
        },

        /**
         * @description 获取详细信息记录的数量
         * @returns {Number} 数量
         */
        getDetailsListCount:function () {
            return me.detailsList ? me.detailsList.getCount() : 0;
        },

        /**
         * @description 初始化
         * @param {Object} p {
         *     "selector":"",
         *     "isScroll":false,
         *     "isTransparentAuto":false,
         *     "theme":"normal",
         *     "autocompleteClick":function(){},
         *     "detailsListCategoryList":[],
         *     "onDetailsListSelect":function(){},
         *     "onMore":function(){},
         *     "onDetialsListDbClick":function(){},
         *     "onPrev":function(){},
         *     "pageSize":function(){},
         *     "orderType":,
         *     "beforeSearch":,
         *     "afterSearch":,
         * }
         */
        init:function (p) {
            me._target = p["selector"];
            me.isScroll = p["isScroll"] || false;
            me.isTransparentAuto = p["isTransparentAuto"] || false;

            // content
            $(me._target)
                .html(T.content)
                .css("position", "absolute")
                .css("float", "left")
                .unbind("mouseenter", me._onTargetMouseenter)
                .bind("mouseenter", me._onTargetMouseenter)
                .unbind("mouseleave", me._onTargetMouseleave)
                .bind("mouseleave", me._onTargetMouseleave)
                .find("." + C.searchcomplete).css("top", "0px");

            var paramsAutocomplete = {
                "selector":$("." + C.autocomplete, me._target)
            };
            if (p.hasOwnProperty("autocompleteClick")) paramsAutocomplete.click = p["autocompleteClick"];

            var paramsDetailsList = {
                "selector":$("." + C.detailsList, me._target),
                "categoryList":p["detailsListCategoryList"],
                "theme":p["theme"] || "normal"
            };
            if (p.hasOwnProperty("onDetailsListSelect")) paramsDetailsList.onSelect = p["onDetailsListSelect"];
            if (p.hasOwnProperty("onMore")) paramsDetailsList.onMore = p["onMore"];
            if (p.hasOwnProperty("onDetialsListDbClick")) paramsDetailsList.onDbClick = p["onDetialsListDbClick"];
            if (p.hasOwnProperty("onPrev")) paramsDetailsList.onPrev = p["onPrev"];
            if (p.hasOwnProperty("pageSize")) paramsDetailsList.pageSize = p["pageSize"];

            var paramsSearchBar = {};
            // TODO 之后将 if 去掉
            if(p.hasOwnProperty("orderType")) paramsSearchBar.orderType = p["orderType"];
            if(p.hasOwnProperty("beforeSearch")) paramsSearchBar.beforeSearch = p["beforeSearch"];
            if(p.hasOwnProperty("afterSearch")) paramsSearchBar.afterSearch = p["afterSearch"];
            // searchBar searchBarInterface set
            if(p.hasOwnProperty("searchBarInterface")) paramsSearchBar.searchBarInterface = p["searchBarInterface"];

            me.autocomplete.init(paramsAutocomplete);
            me.detailsList.init(paramsDetailsList);

            var $searchBarUiInst = me.searchBar.getInstance(paramsSearchBar);
            $("." + C.searchBar, me._target).append($searchBarUiInst);
            if(getOrderType() == 'SALE'){
                $("." + C.searchBar, me._target).css("left", SALE_ORDER_ADJUST);
            }
            // TODO 进行面向对象的实例化改造
        },
        /**
         * @description 渲染数据
         * @param {JSON} data
         * @param {String} cpName 组件名字
         */
        draw:function (data, cpName) {
//        GLOBAL.debug(data);
            if (cpName && me[cpName]) {
                if(cpName === "autocomplete") {
                    me["autocomplete"].draw(data);
                } else if(cpName === "detailsList"){
                    me.state = me.STATE.AUTO_SEARCH;
                    me["detailsList"].draw(data);
                }
            } else if (data["uuid"] && data["uuid"] == me._uuid) {
                me.autocomplete.draw(data["dropDown"]);
                me.detailsList.draw(data["history"]);
            }
        },
        /**
         * @description 清空数据
         * @param {String} cpName 组件名字
         */
        clear:function (cpName) {
            if(!cpName) {
                me.autocomplete.clear();
                me.detailsList.clear();
                me.searchBar.resetUi();
            } else if (cpName === "autocomplete") {
                me["autocomplete"].clear();
            } else if (cpName === "detailsList") {
                me["detailsList"].clear();
                me["searchBar"].resetUi();
            }
        },
        /**
         * @description 显示
         * @param {String} cpName 组件名字
         */
        show:function (cpName) {
            if(!cpName) {
                $(me._target).show();
                if (me.isScroll) {
                    me.scrollIntoView();
                }
            } else if(cpName === "autocomplete" ) {
                me["autocomplete"].show();
            } else if(cpName === "detailsList") {
                me["detailsList"].show();
                me["searchBar"].show();
                // 此时需要设置  供应商 或者 用户名
                me["searchBar"].setCustomerOrSupplierNameOrVehicle($(me._relInst).val());
            }
            if(getOrderType() == 'SALE'){
                $("." + C.detailsList, me._target).css("left", SALE_ORDER_ADJUST);
            }else{
                $("." + C.detailsList, me._target).css("left", "0px");
            }
        },
        /**
         * @description 隐藏
         * @param {String} cpName 组件名字
         */
        hide:function (cpName) {
            if (!cpName) {
                $(me._target).hide();
                me._viewState = null;
            } else if(cpName === "autocomplete") {
                me["autocomplete"].hide();
            } else if(cpName === "detailsList") {
                me["detailsList"].hide();
                me["searchBar"].hide();
            }
        },
        /**
         * @description 传入需要黏住的 node， 那么浮动框将 浮动在此元素的下方, 并主动清空数据， 如果你需要保留数据，那么请将参数 "moveOnly" 设置成true
         * @param {Object} param
         *        {
         *            // Dom Node
         *            node:xxx,
         *            // [true|false]
         *            isMoveOnly:xxx
         *        }
         */
        moveFollow:function (param) {
            var o = param.node;
            if (!param.isMoveOnly) {
                me.clear();
            }

            me._relInst = o;
            me.autocomplete.setWidth($(o).width());

            var oX = G.getX(o);
            var oY = G.getY(o);
            var contentPosition = {
                left:oX,
                top:oY + 22
            };
            if ($(o).is(":hidden")) {
                var p = $(o).parent()[0];
                contentPosition = {
                    left: G.getX(p),
                    top: G.getY(p) + 22
                }
            }

            $(o)
                .unbind("click", me._onRelInstClick).bind("click", me._onRelInstClick)
                .unbind("focus", me._onRelInstFocus).bind("focus", me._onRelInstFocus)
                .unbind("keyup", me._onRelInstKeyup).bind("keyup", me._onRelInstKeyup);

            $(me._target)
                .css("left", contentPosition["left"] + "px")
                .css("top", contentPosition["top"] + "px");

            me._opaqueContent();
        },

        /**
         * 数据格式
         * {
         *     // "on"|"off"
         *     detailsList:""
         *     // "on"|"off"
         *     autocomplete:""
         * }
         */
        _viewState:null,

        _transparentContent:function () {
            if (!me["isTransparentAuto"]) {
                return;
            }

            // 按照程总需求 ，将 历史查询的 弹出框的 功能去除
            if(me.autocomplete.isVisible() === false && me.detailsList.isVisible() === true) {
                return;
            }

            var detailsListWidth = me.detailsList.isVisible() ? me.detailsList._target.width() : 0,
                detailsListHeight = me.detailsList._target.height(),
                autocompleteWidth = me.autocomplete.isVisible() ? me.autocomplete._target.width() : 0;

            me._viewState = {
                detailsList:me.detailsList.isVisible() ? "on" : "off",
                autocomplete:me.autocomplete.isVisible() ? "on" : "off"
            };

            me.detailsList._target.hide();
            me.autocomplete._target.hide();

            me._target.css({
                "border":"2px solid #3366FF",
                "border-radius":"4px",
                "marginBottom":"-4px",
                "width":detailsListWidth + autocompleteWidth + "px",
                "height":detailsListHeight + "px",
                "background":"transparent url(js/components/themes/res/transparent.png)",
                "background-repeat":"repeat-x"
            });
        },

        _opaqueContent:function () {
            if (!me["isTransparentAuto"]) {
                return;
            }

//            if(me.autocomplete.isVisible() || me.detailsList.isVisible()) {
//                return;
//            }

            me._target.css({
                "border":"none",
                "marginBottom":"0px",
                "border-radius":"none"
            });

            if (!me._viewState)
                return;

            if (me._viewState.autocomplete === "on")
                me.autocomplete._target.show();

            if (me._viewState.detailsList === "on")
                me.detailsList._target.show();
        },

        _onTargetMouseenter:function (event) {
            me._opaqueContent();
        },

        _onTargetMouseleave:function (event) {
            me._transparentContent();
        },

        _onRelInstClick:function (event) {
            me._opaqueContent();
        },

        _onRelInstFocus:function (event) {
            me._opaqueContent();
        },

        _onRelInstKeyup:function (event) {
            me._opaqueContent();
        },

        themes:{
            "NORMAL":"normal",
            "SMALL":"small",
            "LARGE":"large",
            "GIANT":"giant"
        },

        changeThemes:function (theme, categoryList) {
            if (!theme) {
                GLOBAL.error("theme set error!");
                return;
            }
            me.detailsList.changeThemes(theme, categoryList);
        }
    };
    me = APP_BCGOGO.Module.searchcompleteMultiselect;
})();

