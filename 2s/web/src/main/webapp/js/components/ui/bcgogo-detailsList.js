;(function () {
    G.localStorage.set("searchcomplete_column_isHide", "true");

    APP_BCGOGO.namespace("Module.detailsList");

    var me,
        C = {
            center:"bcgogo-ui-detailsList-center",
            finishButton:"bcgogo-ui-detailsList-finishButton",
            container:"bcgogo-ui-detailsList-container",
            content:"bcgogo-ui-detailsList-content",
            pageButtonContainer:"bcgogo-ui-detailsList-pageButtonContainer",
            prevButton:"bcgogo-ui-detailsList-prevButton",
            moreButton:"bcgogo-ui-detailsList-moreButton",
            contentHighlighted:"bcgogo-ui-table-content-highlighted",
            contentSelected:"bcgogo-ui-table-content-selected",
            title:"bcgogo-ui-detailsList-title"
        },
        RES = {
            prevImg:"js/components/themes/res/more-arrow-vertical-background-prev.png",
            nextImg:"js/components/themes/res/more-arrow-vertical-background-next.png"
        },
        ATTR = {
            tableHeight:229,
            titleHeight:20,
            tableScrollY:209
        };

    /**
     * @author 潘震
     * @description detailsList 组件
     * @data 2012-08-21
     */
    APP_BCGOGO.Module.detailsList = {
        // ========
        // private variables
        // ========
        _uuid:"",
        // TODO 太长， 暂时保留此接口， 冗余定义 _$ 接口
        _target:null,
        _$:null,
        _data:[],
        _aData:null,
        _aDataBreaked:null,
        _params:null,
        _$table:null,
        _count:0,
        _aoColumns:null,
        _theme:"normal",
        DEFAULT_FIXED_LEN:"9",
        _pageIndex:0,
        _pageSize:0,
        _totalCount:0,
        _cachedPageData:{},
        _pageState:{
            FIRST:"first",
            LAST:"last",
            MIDDLE:"middle",
            ONE_PAGE_ONLY:"onePageOnly"
        },

        getPageSize:function () {
            return me._pageSize;
        },
        setPageSize:function (pageSize) {
            me._pageSize = pageSize;
        },
        getPageIndex:function () {
            return me._pageIndex;
        },
        setPageIndex:function (pageIndex) {
            me._pageIndex = pageIndex;
        },

        _isCached:function () {
            return me._cachedPageData.hasOwnProperty(me.getPageIndex().toString());
        },

        /**
         * @description 返回数据的条数
         * @returns {Number}
         */
        getCount:function () {
            return me._count;
        },

        /**
         * @description 设置 UUID( 全局唯一 ID )
         * @param {Number} value
         */
        setUUID:function (value) {
            me._uuid = value;
        },

        /**
         * @description 获取 UUID( 全局唯一 ID )
         * @returns {Number}
         */
        getUUID:function () {
            return me._uuid;
        },

        // ========
        // private functions
        // ========
        _nullOrUndefinedToEmptyString:function (istr) {
            return (!istr || istr.toString().toLowerCase() === "null" || istr.toString().toLowerCase() === "undefined") ? "" : istr;
        },

        /**
         * @description get aoColumns
         * @param arr
         */
        _getAoColumns:function (arr) {
            var retVal = [];
            for (var i = 0, len = arr.length; i < len; i++)
                retVal.push(me._getOColumn(arr[i]));
            return retVal;
        },

        /**
         * @description get format like follow:
         *              {"sTitle" : "型号","sCategory" : ["product_model"], "sClass":"bcgogo-ui-detailsList-center"}
         *           or {"sTitle" : "车型/车辆品牌","sCategory":["product_vehicle_model","product_vehicle_brand"],"sClass":"bcgogo-ui-detailsList-center"}
         * @param {String} category
         *
         * @returns {Object}
         *              [
         *                 {"sTitle" : "型号","sCategory" : ["product_model"], "sClass":"bcgogo-ui-detailsList-center"},
         *                 {"sTitle" : "车型/车辆品牌","sCategory":["product_vehicle_model","product_vehicle_brand"],"sClass":"bcgogo-ui-detailsList-center"}
         *              ]
         */
        _getOColumn:function (category) {
            return {
                "sTitle":category["title"],
                "sCategory":category["name"].split(","),
                "sClass":C.center,
                "sCharLength":category["charLength"] || me.DEFAULT_FIXED_LEN,
                "sWidth":category["sWidth"] || undefined,
                "bEnableHidden":category["bEnableHidden"] || false
//                "sCharLength": "1000"
            };
        },

        _parseAaData:function (idata) {
            var odata = [], odataBreaked = [], aoColumns = me._aoColumns;
            for (var i = 0, len_i = idata.length; i < len_i; i++) {
                odata[i] = [];
                odataBreaked[i] = [];
                for (var j = 0, len_j = aoColumns.length; j < len_j; j++) {
                    // if is array , merge the value to one String
                    var itemValue = "";
                    for (var k = 0, len_k = aoColumns[j]["sCategory"].length; k < len_k; k++)
                        itemValue += me._nullOrUndefinedToEmptyString(idata[i][aoColumns[j]["sCategory"][k]]) + "/";
                    itemValue = itemValue.replace(/\/$/g, "");
                    odata[i].push(itemValue);
                    odataBreaked[i].push(me._getBreakString(itemValue, parseInt(aoColumns[j]["sCharLength"])));
                }
            }
            return {"aData":odata, "aDataBreaked":odataBreaked};
        },

        _initParams:function (p) {
            me._params = p;
            me._target = $(p["selector"]);
            me._$ = me._target;
            me._aoColumns = me._getAoColumns(p["categoryList"]);
            me.onSelect = p["onSelect"] || me.onSelect;
            me.onPrev = p["onPrev"] || me.onPrev;
            me.onMore = p["onMore"] || me.onMore;
            me.onDbClick = p["onDbClick"] || me.onDbClick;
            me._theme = p["theme"] || me._theme;
            me._pageSize = p.hasOwnProperty("pageSize") ? p["pageSize"] : 0;
            me._enableColumnContentHidden = p.hasOwnProperty("enableColumnContentHidden") ? p["enableColumnContentHidden"] : undefined;
            me._columnContentHiddenKeycode = p.hasOwnProperty("columnContentHiddenKeycode") ? p["columnContentHiddenKeycode"] : 17; // 左 ctrl 键
        },

        _initView:function (theme) {
            // set themes
            var newStyles = {
                detailsListClass:"bcgogo-ui-detailsList-accordion-" + theme,
                moreButtonClass:"J-bcgogo-ui-detailsList-moreButton-" + theme,
                prevButtonClass:"J-bcgogo-ui-detailsList-prevButton-" + theme
            };

            // small-->   newStyles.sScrollXInner:"439px",
            // normal-->  newStyles.sScrollXInner:"539px",
            // large-->   newStyles.sScrollXInner:"639px"

            // init html
            $(me._$)
                .html("")
                .html(""
                    + "<div class='" + newStyles["detailsListClass"] + "'>"
                    + "    <div class='" + C.title + "'>--</div>"
                    + "    <div class='" + C.finishButton + "'></div>"
                    + "    <div class='" + C.container + "'>"
                    + "        <table class='" + C.content + "'></table>"
                    + "    </div>"
                    + "    <div class='" + C.pageButtonContainer + "'>"
                    + "        <div class='" + C.prevButton + "'>"
                    + "            <img src='" + RES.prevImg + "' />"
                    + "        </div>"
                    + "        <div class='" + C.moreButton + "'>"
                    + "            <img src='" + RES.nextImg + "' />"
                    + "        </div>"
                    + "    </div>"
                    + "</div>");

            // prepare table
            me._$table = $("." + C.content, me._$)
                .dataTable({
                    "bSort":false,
                    "bFilter":false,
                    "sScrollY":ATTR.tableScrollY + "px",
                    "bPaginate":false,
                    "bScrollCollapse":true,
//                "sScrollXInner" : newStyles["sScrollXInner"],
                    "sScrollWrapper":"dataTables_scroll",
                    "bRetrieve":true,
                    "bInfo":false,
                    "aoColumns":me._aoColumns

                    // add this to add manaully setWidth feature
                    ,
                    "bAutoWidth":false
                });


//            console.log(me._aoColumns);

            // prepare "更前" button
            $("." + C.prevButton, me._$)
                .button()
                .addClass(newStyles["prevButtonClass"])
                .bind("click", {action:"prev"}, me._onPageBefore);

            // prepare "更后" button
            $("." + C.moreButton, me._$)
                .button()
                .addClass(newStyles["moreButtonClass"])
                .bind("click", {action:"next"}, me._onPageBefore);
        },

        // ========
        // public functions
        // ========
        /**
         * @description 初始化
         * @param p
         *        {
         *            // necessary, jquery object, you need use $(selector) to select the node
         *            <b>"selector": $object,</b>
         *            // necessary, set Catetory Key to show
         *            // single string option includes:
         *            //        ["product_name" | "product_brand" | "product_spec" | "product_model" | "inventoryNum" | "product_vehicle_brand" | "product_vehicle_model" | "sellUnit" | "purchasePrice" | "recommendedPrice"]
         *            //     and you also can use "," like this :
         *            //        "product_vehicle_brand,product_vehicle_model"
         *            // so , look follow formats , they are all right:
         *            //
         *            // @example 1: [{"name":"product_model","title":"规格"}, {"name":"product_vehicle_model,"title":"车型"}]
         *            // @example 2: [{"name":"product_model","title":"规格"}, {"name":"product_vehicle_model,product_vehicle_brand","title":"车型/车辆品牌"}]
         *            <b>"categoryList": [],</b>
         *            // necessary, set "onMore" callback
         *            <b>"onMore":function(event){},</b>
         *            // necessary, set "onDbClick" callback
         *            <b>"onDbClick":function(event, index, data){}</b>
         *            // necessary, set "onFinish" callback
         *            <b>"onFinish":function(event, indexList, dataList){}</b>
         *            // not necessary, set "onSelect"
         *            <b>"onSelect":function(event, index, data){}</b>
         *            // not necessary, set "enableColumnContentHidden"
         *            <b>"enableColumnContentHidden":false</b>
         *            // not necessary, set "columnContentHidden"
         *            <b>"columnContentHidden":false</b>
         *        }
         */
        init:function (p) {
            me._initParams(p);
            me._initView(this._theme);

            me._initColumnContentHiddenEvent();
        },

        _initColumnContentHiddenEvent:function() {
            $(document).bind("keyup", function(event) {
                var keycode = event.which || event.keyCode;
                if(me._enableColumnContentHidden && me.isVisible() && keycode === me._columnContentHiddenKeycode) {
                    me._toggleColumnContentDisplay();
                }
            }) ;
        },

        _toggleColumnContentDisplay:function() {
            var $trList = $(".bcgogo-ui-detailsList-content tbody tr", me._target),
                isHidden = me._columnIsHidden();

            isHidden = !isHidden;

            me._updateColumnContentDisplay(isHidden);

//            console.log("isHidden : " + isHidden);
            G.localStorage.set("searchcomplete_column_isHide", (isHidden).toString());
        },

        _columnIsHidden:function() {
            return G.localStorage.has("searchcomplete_column_isHide") ? (G.localStorage.get("searchcomplete_column_isHide") === "true") : false;
        },

        _updateColumnContentDisplay:function(isHidden) {
            var $trList = $(".bcgogo-ui-detailsList-content tbody tr", me._target);

            if(!$trList[0]) return;

            $.each(me._aoColumns, function(thatIndex, thatValue) {
                if(thatValue["bEnableHidden"]) {
                    var $item,
                        storedValue;
                    $trList.each(function(index, value) {
                        $item = $("td", $(value)).eq(thatIndex);
                        storedValue = $item.attr("data-stored-value");
                        $item.html(isHidden ? "" : decodeURIComponent(storedValue));
                    });
                }
            });
        },

        _getBreakString:function (s, len) {
            var breaked = false,
                cnArr = s.match(/[\u4e00-\u9fa5]/g),
                cnNum = cnArr ? cnArr.length : 0,
                fixLen = cnNum + s.length;

            if (fixLen > len) {
                breaked = true;
                s = s.slice(0, len);
            }

            while (fixLen > len) {
                s = s.slice(0, s.length - 1);
                cnArr = s.match(/[\u4e00-\u9fa5]/g);
                cnNum = cnArr ? cnArr.length : 0;
                fixLen = cnNum + s.length;
            }

            return breaked ? s + ".." : s;
        },

        _renderTitle:function(title) {
            var showTitle = title || "--" ;

            $("." + C.title, me._$).show().html(showTitle);
        },

        /**
         * 通过数据渲染视图
         * @param data
         */
        draw:function (data) {
            if (data == null) {
                me._$table.fnClearTable();
                return;
            }

            if (!data.hasOwnProperty("uuid") || data["uuid"] != me._uuid) {
                me._$table.fnClearTable();
                me._$table.fnAddData(me._aDataBreaked);
                return;
            }

            me._data = data["data"];

            // TODO test data
//            for(var k in me._data) {
//                me._data[k].commodityInfo = "<span style='color:red;'>" + me._data[k].commodityInfo + "</span>";
//            }




            if (me._cachedPageData.hasOwnProperty(me.getPageIndex().toString()) === false) {
                me._cachedPageData[me.getPageIndex().toString()] = data;
            }
            me._totalCount = data.hasOwnProperty("totalCount") ? data["totalCount"] : -1;
            // normalize values to string, convert : null|undefined|"null"|"undefined" to ""
            $.each(me._data, function (index, item) {
                $.each(me._data[index], function (key, value) {
                    me._data[index][key] = me._nullOrUndefinedToEmptyString(value);
                });
            });

            me._count = me._data.length;

            // prepare data
            var drawData = me._parseAaData(me._data);
            me._aData = drawData.aData;
            me._aDataBreaked = drawData.aDataBreaked;

            // prepare title
            me._title = data.hasOwnProperty("title") ? data["title"] : null;
            me._renderTitle(me._title);

            // prepare accordion
            me._$table.fnClearTable();
            me._$table.fnAddData(me._aDataBreaked);

//            console.log(me._data);
//            console.log(me._aData);
//            console.log(me._aDataBreaked);

            // prepare prevButton render
            var state = me._getPageState();
            $("." + C.prevButton, me._$).button(state === me._pageState.FIRST || state === me._pageState.ONE_PAGE_ONLY ? "disable" : "enable");
            $("." + C.moreButton, me._$).button(state === me._pageState.LAST || state === me._pageState.ONE_PAGE_ONLY ? "disable" : "enable");

            // prepare table row
            $("tr", me._$table)
                .bind("click", me._onRowClick)
                .dblclick(me._onDbClickBefore)
                .hover(function () {
                    $("td", this).addClass(C.contentHighlighted);
                }, function () {
                    $("td", this).removeClass(C.contentHighlighted);
                })
                .css("cursor", "pointer");

            $("tbody tr", me._$table).each(function (index) {
                if (!isNaN(index)) {
                    var $tdList = $("td", this), len = $tdList.length;
                    for (var i = 0; i < len; i++) {
                        if ("表格中没有数据" === $tdList.eq(i).text()) {
                            $(this).unbind("click");
                            break;
                        } else {
                            $tdList.eq(i).attr("title", me._aData[index][i]);
                        }
                    }
                }
            });
            $("tbody tr td[title]", me._$table).tooltip({"delay":0, "track":true});

            // 解决 windows XP 下 chrome 的奇怪bug， 猜测是字体引擎的问题， 待证实
            $("tbody tr td", me._$table).css("text-overflow", "ellipsis").css("-webkit-text-overflow", "ellipsis").css("-moz-text-overflow", "ellipsis");




            if(!me._enableColumnContentHidden) return;

            $(".bcgogo-ui-detailsList-content tbody tr td", me._target).each(function(index, value) {
                var tempValue = encodeURIComponent($(value).html());

                $(value).attr("data-stored-value", tempValue);
            });
            me._updateColumnContentDisplay(me._columnIsHidden());
        },

        /**
         * @description 清空视图和数据
         */
        clear:function () {
            me._count = 0;
            me._cachedPageData = {};
            me.setPageIndex(0);
            me._$table.fnClearTable();
        },

        show:function () {
            // fast or blind
            $(me._$).show("fast");
            me._visible = true;
        },

        _visible:false,

        hide:function () {
            $(me._$).hide();
            me._visible = false;
        },

        setWidth:function (width) {
            var _$ = this._$,
                theme = this._theme;

            _$.width(width);
            _$.find(".bcgogo-ui-detailsList-accordion-" + theme).width(width);
            _$.find(".J-bcgogo-ui-detailsList-finishButton-" + theme).width(width - 2);
            _$.find(".J-bcgogo-ui-detailsList-pageButtonContainer-" + theme).width(width - 2);
            _$.find(".J-bcgogo-ui-detailsList-prevButton-" + theme).width(width / 2 - 2);
            _$.find(".J-bcgogo-ui-detailsList-moreButton-" + theme).width(width / 2 - 2);
        },

        isVisible:function () {
            return me._visible || me._$.is(":visible");
        },

        /**
         * @description change themes, 当你调用 changeThemes() 后再调用 draw(), 用数据渲染视图
         * @param {String} type for styles , ["normal" | "large" | "small"]
         */
        changeThemes:function (theme, categoryList) {
            if (categoryList && G.isArray(categoryList)) {
                me._aoColumns = me._getAoColumns(categoryList);
            }
            me._theme = theme;
            me._initView(me._theme);
        },

        /**
         * @description highlight row color, and add tag to className
         * @param event
         * @returns {Boolean} Is this row selected?
         */
        _toggleSelectedRowStyles:function (event) {
            return $("td", event.currentTarget).toggleClass(C.contentSelected).hasClass(C.contentSelected);
        },

        _removeSelectedRowStyles:function (event) {
            if ($("td", event.currentTarget).hasClass(C.contentSelected))
                $("td", event.currentTarget).removeClass(C.contentSelected);
        },

        /**
         * @description get row index from Table Dom node
         * @param {Dom node} node
         */
        _getRowIndexFromNode:function (node) {
            return $("." + C.content +" tbody tr", me._$).index(node);
        },

        _getPageState:function () {
            var state = "";
            if (me.getPageIndex() === 0) {
                state = me._pageState.FIRST;
            }

            if (!( me._totalCount !== -1 && me._totalCount > (me.getPageSize() * me.getPageIndex() + me.getCount()) )) {
                state = state === "" ? me._pageState.LAST : me._pageState.ONE_PAGE_ONLY;
            }

            if (state === "") {
                state = me._pageState.MIDDLE;
            }
            return state;
        },

        _getPageIndexByAction:function (action) {
            if (action === "next") {
                return me.getPageIndex() + (me._getPageState() === me._pageState.LAST ? 0 : 1);
            }else if(action === "prev") {
                return me.getPageIndex() + (me._getPageState() === me._pageState.FIRST ? 0 : -1);
            }
        },

        // ========
        // private events callback
        // ========
        /**
         * @description 此回调函数不对外提供
         * @param event
         */
        _onRowClick:function (event) {
            var index = me._getRowIndexFromNode(event.currentTarget);
            me.onSelect(event, index, me._data[index]);
        },

        /**
         * @description before Dbclick event callback , pretreatment
         * @param event
         */
        _onDbClickBefore:function (event) {
            var index = me._getRowIndexFromNode(event.currentTarget);
            me.onDbClick(event, index, me._data[index]);
        },

        _onPageBefore:function (event) {
            var fnName;
            me.setPageIndex(me._getPageIndexByAction(event.data.action));
            if (event.data.action === "next")
                fnName = "onMore";
            else if (event.data.action === "prev")
                fnName = "onPrev";

            if (me._isCached()) {
                me.draw(me._cachedPageData[me.getPageIndex().toString()]);
                return;
            }
            me[fnName](event, me._isCached());
        },

        // ========
        // public events callback
        // ========
        onMore:function (event, isCached) {
            G.debug("button \"更多\" has been clicked");
        },

        onPrev:function (event, isCached) {
            G.debug("button \"更前\" has been clicked");
        },

        onSelect:function (event, index, data) {
            G.debug(index);
        },

        onDbClick:function (event, index, data) {
            G.debug("double clicked one row,  index is : " + index + "    , data is : " + data.toString());
        }

    };
    me = App.Module.detailsList;
})();

