APP_BCGOGO.namespace("Module.detailsList");
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
    _target:null,
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

    getPageSize:function() {
        var foo = APP_BCGOGO.Module.detailsList;
        return foo._pageSize;
    },
    setPageSize:function(pageSize) {
        var foo = APP_BCGOGO.Module.detailsList;
        foo._pageSize = pageSize;
    },
    getPageIndex:function() {
        var foo = APP_BCGOGO.Module.detailsList;
        return foo._pageIndex;
    },
    setPageIndex:function(pageIndex) {
        var foo = APP_BCGOGO.Module.detailsList;
        foo._pageIndex = pageIndex;
    },

    _isCached:function(){
        var foo = APP_BCGOGO.Module.detailsList;
        return foo._cachedPageData.hasOwnProperty( foo.getPageIndex().toString() );
    },

    /**
     * @description 返回数据的条数
     * @returns {Number}
     */
    getCount:function() {
        var foo = APP_BCGOGO.Module.detailsList;
        return foo._count;
    },

    /**
     * @description 设置 UUID( 全局唯一 ID )
     * @param {Number} value
     */
    setUUID:function(value) {
        var foo = APP_BCGOGO.Module.detailsList;
        foo._uuid = value;
    },

    /**
     * @description 获取 UUID( 全局唯一 ID )
     * @returns {Number}
     */
    getUUID:function() {
        var foo = APP_BCGOGO.Module.detailsList;
        return foo._uuid;
    },

    // ========
    // private functions
    // ========
    _nullOrUndefinedToEmptyString:function(istr) {
        return (!istr || istr.toString().toLowerCase() === "null" || istr.toString().toLowerCase() === "undefined") ? "" : istr;
    },

    /**
     * @description get aoColumns
     * @param arr
     */
    _getAoColumns:function(arr) {
        var retVal = [], foo = APP_BCGOGO.Module.detailsList;
        for (var i = 0, len = arr.length; i < len; i++)
            retVal.push(foo._getOColumn(arr[i]));
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
    _getOColumn:function(category) {
        var foo = APP_BCGOGO.Module.detailsList;
        return {
            "sTitle":category["title"],
            "sCategory":category["name"].split(","),
            "sClass":"bcgogo-ui-detailsList-center",
            "sCharLength":category["charLength"] || foo.DEFAULT_FIXED_LEN
        };
    },

    _parseAaData:function(idata) {
        var self = APP_BCGOGO.Module.detailsList, odata = [], odataBreaked = [], foo = self._aoColumns;
        for (var i = 0, len_i = idata.length; i < len_i; i++) {
            odata[i] = [];
            odataBreaked[i] = [];
            for (var j = 0, len_j = foo.length; j < len_j; j++) {
                // if is array , merge the value to one String
                var itemValue = "";
                for (var k = 0,len_k = foo[j]["sCategory"].length; k < len_k; k++)
                    itemValue += self._nullOrUndefinedToEmptyString(idata[i][foo[j]["sCategory"][k]]) + "/";
                itemValue = itemValue.replace(/\/$/g, "");
                odata[i].push(itemValue);
                odataBreaked[i].push( self._getBreakString(itemValue, parseInt(foo[j]["sCharLength"])) );
            }
        }
        return {"aData":odata, "aDataBreaked":odataBreaked};
    },

    _initParams:function(p) {
        var foo = APP_BCGOGO.Module.detailsList;
        foo._params = p;
        foo._target = $(p["selector"]);
        foo._aoColumns = foo._getAoColumns(p["categoryList"]);
        foo.onSelect = p["onSelect"] || foo.onSelect;
        foo.onPrev = p["onPrev"] || foo.onPrev;
        foo.onMore = p["onMore"] || foo.onMore;
//        foo.onFinish = p["onFinish"] || foo.onFinish;
        foo.onDbClick = p["onDbClick"] || foo.onDbClick;
        foo._theme = p["theme"] || foo._theme;
        foo._pageSize = p.hasOwnProperty("pageSize") ? p["pageSize"] : 0;
    },

    _initView:function(theme) {
        var foo = APP_BCGOGO.Module.detailsList;
        // set themes
        var newStyles = {
            detailsListClass:"bcgogo-ui-detailsList-accordion-" + theme,
            moreButtonClass:"J-bcgogo-ui-detailsList-moreButton-" + theme,
            // finishButtonClass:"J-bcgogo-ui-detailsList-finishButton-"+theme,
            prevButtonClass:"J-bcgogo-ui-detailsList-prevButton-" + theme
        }

        // small-->   newStyles.sScrollXInner:"439px",
        // normal-->  newStyles.sScrollXInner:"539px",
        // large-->   newStyles.sScrollXInner:"639px"

        // init html
        $(foo._target)
            .html("")
            .html(
            "  <div class='" + newStyles["detailsListClass"] + "'>"
                + "    <div class='bcgogo-ui-detailsList-finishButton'></div>"
                + "    <div class='bcgogo-ui-detailsList-container'>"
                + "        <table class='bcgogo-ui-detailsList-content'></table>"
                + "    </div>"
                + "    <div class='bcgogo-ui-detailsList-pageButtonContainer'>"
                + "        <div class='bcgogo-ui-detailsList-prevButton'>"
                + "            <img src='js/components/themes/res/more-arrow-vertical-background-prev.png' />"
                + "        </div>"
                + "        <div class='bcgogo-ui-detailsList-moreButton'>"
                + "            <img src='js/components/themes/res/more-arrow-vertical-background-next.png' />"
                + "        </div>"
                + "    </div>"
                + "</div>");

        // prepare table
        foo._$table = $(".bcgogo-ui-detailsList-content", foo._target)
            .dataTable({
                "bSort" : false,
                "bFilter": false,
                "sScrollY" : (229) + "px",
                "bPaginate" : false,
                "bScrollCollapse" : true,
//                "sScrollXInner" : newStyles["sScrollXInner"],
                "sScrollWrapper": "dataTables_scroll",
                "bRetrieve" : true,
                "bInfo":false,
                "aoColumns" : foo._aoColumns
            });

        // margin 5px
//        $(".bcgogo-ui-detailsList-container", foo._target).css("margin-top", "5px");

        // prepare "更前" button
        $(".bcgogo-ui-detailsList-prevButton", foo._target)
            .button()
            .addClass(newStyles["prevButtonClass"])
            .bind("click", {action:"prev"}, foo._onPageBefore);

//        // prepare "确定" button
//        $(".bcgogo-ui-detailsList-finishButton", foo._target)
//            .button({"label":"确定"})
//            .addClass(newStyles["finishButtonClass"])
//            .bind("click", foo._onFinishBefore);

        // prepare "更后" button
        $(".bcgogo-ui-detailsList-moreButton", foo._target)
            .button()
            .addClass(newStyles["moreButtonClass"])
            .bind("click", {action:"next"}, foo._onPageBefore);

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
     *        }
     */
    init:function(p) {
        var foo = APP_BCGOGO.Module.detailsList;
        foo._initParams(p);
        foo._initView(this._theme);
    },

    _getBreakString:function(s, len) {
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

    /**
     * 通过数据渲染视图
     * @param data
     */
    draw:function(data) {
//        GLOBAL.debug(data);
        var foo = APP_BCGOGO.Module.detailsList;
        if (data == null) {
            foo._$table.fnClearTable();
            return;
        }

        if (!data.hasOwnProperty("uuid") || data["uuid"] != foo._uuid) {
            foo._$table.fnClearTable();
            foo._$table.fnAddData(foo._aDataBreaked);
            return;
        }

        foo._data = data["data"];
        if( foo._cachedPageData.hasOwnProperty(foo.getPageIndex().toString()) === false ) {
            foo._cachedPageData[foo.getPageIndex().toString()] = data;
        }
        foo._totalCount = data.hasOwnProperty("totalCount") ? data["totalCount"] : -1;
        // normalize values to string, convert : null|undefined|"null"|"undefined" to ""
        $.each(foo._data, function(index, item) {
            $.each(foo._data[index], function(key, value) {
                foo._data[index][key] = foo._nullOrUndefinedToEmptyString(value);
            });
        });

        foo._count = foo._data.length;

        // prepare data
        var drawData = foo._parseAaData(foo._data);
        foo._aData = drawData.aData;
        foo._aDataBreaked = drawData.aDataBreaked;

        // prepare accordion
        foo._$table.fnClearTable();
        foo._$table.fnAddData(foo._aDataBreaked);

        // prepare prevButton render
        var state = foo._getPageState();
        $(".bcgogo-ui-detailsList-prevButton", foo._target).button(state === foo._pageState.FIRST || state === foo._pageState.ONE_PAGE_ONLY ? "disable" : "enable");
        $(".bcgogo-ui-detailsList-moreButton", foo._target).button(state === foo._pageState.LAST || state === foo._pageState.ONE_PAGE_ONLY ? "disable" : "enable");

        // prepare table row
        $("tr", foo._$table)
            .bind("click", foo._onRowClick)
            .dblclick(foo._onDbClickBefore)
            .hover(function() {
                $("td", this).addClass("bcgogo-ui-table-content-highlighted");
            }, function() {
                $("td", this).removeClass("bcgogo-ui-table-content-highlighted");
            })
            .css("cursor", "pointer");

        $("tbody tr", foo._$table).each(function(index) {
            if (!isNaN(index)) {
                var $tdList = $("td", this), len = $tdList.length;
                for (var i = 0; i < len; i++) {
                    if ("表格中没有数据" === $tdList.eq(i).text()) {
                        $(this).unbind("click");
                        break;
                    } else {
                        $tdList.eq(i).attr("title", foo._aData[index][i]);
                    }
                }
            }
        });
        $("tbody tr td[title]").tooltip({"delay": 0, "track":true});
    },

    /**
     * @description 清空视图和数据
     */
    clear:function() {
        var foo = APP_BCGOGO.Module.detailsList;
        foo._count = 0;
        foo._cachedPageData = {};
        foo.setPageIndex(0);
        foo._$table.fnClearTable();
    },

    show:function() {
        var foo = APP_BCGOGO.Module.detailsList;
        $(foo._target).show("fast");
        foo._visible = true;
    },

    _visible:false,

    hide:function() {
        var foo = APP_BCGOGO.Module.detailsList;
        $(foo._target).hide();
        foo._visible = false;
    },

    isVisible:function(){
        var foo = APP_BCGOGO.Module.detailsList;
        return foo._visible || foo._target.is(":visible");
    },

    /**
     * @description change themes, 当你调用 changeThemes() 后再调用 draw(), 用数据渲染视图
     * @param {String} type for styles , ["normal" | "large" | "small"]
     */
    changeThemes:function(theme, categoryList) {
        var foo = APP_BCGOGO.Module.detailsList;
        if (categoryList && GLOBAL.Lang.isArray(categoryList)) {
            foo._aoColumns = foo._getAoColumns(categoryList);
        }
        foo._theme = theme;
        foo._initView(foo._theme);
    },

    /**
     * @description highlight row color, and add tag to className
     * @param event
     * @returns {Boolean} Is this row selected?
     */
    _toggleSelectedRowStyles:function(event) {
        return $("td", event.currentTarget).toggleClass("bcgogo-ui-table-content-selected").hasClass("bcgogo-ui-table-content-selected");
    },

    _removeSelectedRowStyles:function(event) {
        if ($("td", event.currentTarget).hasClass("bcgogo-ui-table-content-selected"))
            $("td", event.currentTarget).removeClass("bcgogo-ui-table-content-selected");
    },

    /**
     * @description get row index from Table Dom node
     * @param {Dom node} node
     */
    _getRowIndexFromNode:function(node) {
        var foo = APP_BCGOGO.Module.detailsList;
        return $(".bcgogo-ui-detailsList-content tbody tr", foo._target).index(node);
    },

    _getPageState:function(){
        var foo = APP_BCGOGO.Module.detailsList, state = "";
        if(foo.getPageIndex() === 0) {
            state = foo._pageState.FIRST;
        }

        if(!( foo._totalCount !== -1 && foo._totalCount > (foo.getPageSize() * foo.getPageIndex() + foo.getCount()) )) {
            state = state === "" ? foo._pageState.LAST : foo._pageState.ONE_PAGE_ONLY;
        }

        if(state === "") {
            state = foo._pageState.MIDDLE;
        }

        return state;
    },

    _getPageIndexByAction:function(action){
        var foo = APP_BCGOGO.Module.detailsList;
        if(action === "next") {
            return foo.getPageIndex() + (foo._getPageState() === foo._pageState.LAST ? 0 : 1);
        }

        if(action === "prev") {
            return foo.getPageIndex() + (foo._getPageState() === foo._pageState.FIRST ? 0 : -1);
        }
    },

    // ========
    // private events callback
    // ========
    /**
     * @description 此回调函数不对外提供
     * @param event
     */
    _onRowClick:function(event) {
        var foo = APP_BCGOGO.Module.detailsList;
//        if (foo._toggleSelectedRowStyles(event)) {
        var index = foo._getRowIndexFromNode(event.currentTarget);
        foo.onSelect(event, index, foo._data[index]);
//        }
    },

    /**
     * @description before Dbclick event callback , pretreatment
     * @param event
     */
    _onDbClickBefore:function(event) {
        var foo = APP_BCGOGO.Module.detailsList, index = foo._getRowIndexFromNode(event.currentTarget);
        foo.onDbClick(event, index, foo._data[index]);
    },

    _onPageBefore:function(event){
        var foo = APP_BCGOGO.Module.detailsList, fnName;

        foo.setPageIndex(foo._getPageIndexByAction(event.data.action));
        if(event.data.action === "next") 
            fnName = "onMore";
        else if(event.data.action === "prev") 
            fnName = "onPrev";

        if(foo._isCached()) {
            foo.draw( foo._cachedPageData[foo.getPageIndex().toString()] );
            return;
        }
        foo[fnName](event, foo._isCached());
    },

    // ========
    // public events callback
    // ========
    /**
     * @description to override , 点击 "更多" 按钮后的 回调函数
     * @param event
     */
    onMore:function(event, isCached) {
        GLOBAL.debug("button \"更多\" has been clicked");
    },

    onPrev:function(event, isCached) {
        GLOBAL.debug("button \"更前\" has been clicked");
    },

    /**
     * @description to override, 当选中后的函数回调
     * @param event
     * @param {Number} index
     * @param {Object} data
     */
    onSelect:function(event, index, data) {
        GLOBAL.debug(index);
    },


    /**
     * @description to override , 双击 某数据行后的回调函数
     * @param event
     */
    onDbClick:function(event, index, data) {
        GLOBAL.debug("double clicked one row,  index is : " + index + "    , data is : " + data.toString());
    }

//    /**
//     * @description prepare to callback "onFinish" method
//     * @param event
//     */
//    _onFinishBefore:function(event) {
//        var foo = APP_BCGOGO.Module.detailsList, indexList = [], dataList = [];
//        $(".bcgogo-ui-detailsList-content tbody tr", foo._target).each(function(index) {
//            if ($("td", $(this)).hasClass("bcgogo-ui-table-content-selected")) {
//                indexList.push(index);
//            }
//        });
//        $.each(indexList, function(index, value) {
//            dataList[index] = foo._data[value];
//        });
//        foo.onFinish(event, indexList, dataList);
//    },

//    /**
//     * @description to override , 点击 "确定" 按钮后的回调函数
//     * @param event
//     * @param {Array of Number} indexList
//     * @param {Array of Object} dataList
//     */
//    onFinish:function(event, indexList, dataList) {
//        GLOBAL.debug("clicked \"确定\" button,  indexList is : " + indexList.toString() + "    , dataList is : " + dataList.toString());
//    },

};