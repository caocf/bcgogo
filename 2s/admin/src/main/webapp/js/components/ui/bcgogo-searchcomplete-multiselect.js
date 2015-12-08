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
    _data:null,
    isScroll:false,
    isTransparentAuto:false,

    /**
     * @description 设置组件的 UUID， 同时将UUID 设置给两个子组件 autocomplete, detailsList
     * @param {String} value
     */
    setUUID:function(value) {
        this._uuid = value;
        this.autocomplete.setUUID(value);
        this.detailsList.setUUID(value);
    },

    /**
     * @description 获取组件记录的 UUID
     * @return {String}
     */
    getUUID:function() {
        return this._uuid;
    },

    setPageSize:function(pageSize) {
        this.detailsList._pageSize = pageSize;
    },

    getPageSize:function() {
        return this.detailsList._pageSize;
    },

    getPageIndex:function() {
        return this.detailsList.getPageIndex();
    },

    /**
     * @description 获取详细信息记录的数量
     * @returns {Number} 数量
     */
    getDetailsListCount:function() {
        return this.detailsList ? this.detailsList.getCount() : 0;
    },

    /**
     * @description 初始化
     * @param {Object} p
     */
    init:function(p) {
        this._target = p["selector"];
        this.isScroll = p["isScroll"] || false;
        this.isTransparentAuto = p["isTransparentAuto"] || false;

        // content
        var s = '<div class="bcgogo-ui-searchcomplete">'
            + '    <div class="bcgogo-ui-searchcomplete-autocomplete"></div>'
            + '    <div class="bcgogo-ui-searchcomplete-detailsList"></div>'
            + '</div>';

        $(this._target)
            .html(s)
            .css("position", "absolute")
            .css("float", "left")
            .unbind("mouseenter", this._onTargetMouseenter)
            .bind("mouseenter", this._onTargetMouseenter)
            .unbind("mouseleave", this._onTargetMouseleave)
            .bind("mouseleave", this._onTargetMouseleave);

        var paramsAutocomplete = {
            "selector":$(".bcgogo-ui-searchcomplete-autocomplete", this._target)
        };
        if (p.hasOwnProperty("autocompleteClick")) paramsAutocomplete.click = p["autocompleteClick"];

        var paramsDetailsList = {
            "selector":$(".bcgogo-ui-searchcomplete-detailsList", this._target),
            "categoryList":p["detailsListCategoryList"],
            "theme":p["theme"] || "normal"
        };
        if (p.hasOwnProperty("onDetailsListSelect")) paramsDetailsList.onSelect = p["onDetailsListSelect"];
        if (p.hasOwnProperty("onMore")) paramsDetailsList.onMore = p["onMore"];
//        if (p.hasOwnProperty("onFinish")) paramsDetailsList.onFinish = p["onFinish"];
        if (p.hasOwnProperty("onDetialsListDbClick")) paramsDetailsList.onDbClick = p["onDetialsListDbClick"];
        if (p.hasOwnProperty("onPrev")) paramsDetailsList.onPrev = p["onPrev"];
        if (p.hasOwnProperty("pageSize")) paramsDetailsList.pageSize = p["pageSize"];

        this.autocomplete.init(paramsAutocomplete);
        this.detailsList.init(paramsDetailsList);
    },
    /**
     * @description 渲染数据
     * @param {JSON} data
     * @param {String} cpName 组件名字
     */
    draw:function(data, cpName) {
//        GLOBAL.debug(data);
        if (cpName && this[cpName]) {
            this[cpName].draw(data);
        } else if (data["uuid"] && data["uuid"] == this._uuid) {
            this.autocomplete.draw(data["dropDown"]);
            this.detailsList.draw(data["history"]);
        }
    },
    /**
     * @description 清空数据
     * @param {String} cpName 组件名字
     */
    clear:function(cpName) {
        if (cpName) {
            this[cpName].clear();
        } else {
            this.autocomplete.clear();
            this.detailsList.clear();
        }
    },
    /**
     * @description 显示
     * @param {String} cpName 组件名字
     */
    show:function(cpName) {
        if (cpName) {
            this[cpName].show();
        } else {
            $(this._target).show();
            if(this.isScroll) {
                this.scrollIntoView();
            }
        }
        GLOBAL.debug("searchcompleteMultiselect function name==show:" + this.autocomplete.isVisible());
        var autocompelteLeft = this.autocomplete.isVisible() ? $(this._relInst).width() + "px" : "0px";
        $(".bcgogo-ui-searchcomplete-detailsList", this._target).css("left", autocompelteLeft);
    },
    /**
     * @description 隐藏
     * @param {String} cpName 组件名字
     */
    hide:function(cpName) {
        if (cpName) {
            this[cpName].hide();
        } else {
            $(this._target).hide();
            this._viewState = null;
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
    moveFollow:function(param) {
        var foo = APP_BCGOGO.Module.searchcompleteMultiselect, o = param.node;
        if (!param.isMoveOnly) {
            foo.clear();
        }

        foo._relInst = o;
        foo.autocomplete.setWidth($(o).width());

        var oX = GLOBAL.Display.getX(o);
        var oY = GLOBAL.Display.getY(o);
        var contentPosition = {
            left: oX,
            top: oY + 22
        };

        $(o)
            .unbind("click", foo._onRelInstClick).bind("click", foo._onRelInstClick)
            .unbind("focus", foo._onRelInstFocus).bind("focus", foo._onRelInstFocus)
            .unbind("keyup", foo._onRelInstKeyup).bind("keyup", foo._onRelInstKeyup);

        $(foo._target)
            .css("left", contentPosition["left"] + "px")
            .css("top", contentPosition["top"] + "px");

        foo._opaqueContent(foo);
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

    _transparentContent:function(foo) {
        if(!foo["isTransparentAuto"]) {
            return;
        }

        var detailsListWidth = foo.detailsList.isVisible() ? foo.detailsList._target.width() : 0,
            detailsListHeight = foo.detailsList._target.height(),
            autocompleteWidth = foo.autocomplete.isVisible() ? foo.autocomplete._target.width() : 0;

        foo._viewState = {
            detailsList:foo.detailsList.isVisible() ? "on" : "off",
            autocomplete:foo.autocomplete.isVisible() ? "on" : "off"
        };

        foo.detailsList._target.hide();
        foo.autocomplete._target.hide();

        foo._target.css({
            "border":"2px solid #3366FF",
            "border-radius":"4px",
            "marginBottom":"-4px",
            "width":detailsListWidth + autocompleteWidth + "px",
            "height":detailsListHeight + "px",
            "background":"transparent url(js/components/themes/res/transparent.png)",
            "background-repeat": "repeat-x"
        });
    },

    _opaqueContent:function(foo) {
        if(!foo["isTransparentAuto"]) {
            return;
        }

        foo._target.css({
            "border":"none",
            "marginBottom":"0px",
            "border-radius":"none"
        });

        if (!foo._viewState)
            return;

        if (foo._viewState.autocomplete === "on")
            foo.autocomplete._target.show();

        if (foo._viewState.detailsList === "on")
            foo.detailsList._target.show();
    },

    _onTargetMouseenter:function(event) {
        var foo = APP_BCGOGO.Module.searchcompleteMultiselect;
        foo._opaqueContent(foo);
    },

    _onTargetMouseleave:function(event) {
        var foo = APP_BCGOGO.Module.searchcompleteMultiselect;
        foo._transparentContent(foo);
    },

    _onRelInstClick:function(event) {
        var foo = APP_BCGOGO.Module.searchcompleteMultiselect;
        foo._opaqueContent(foo);
    },

    _onRelInstFocus:function(event) {
        var foo = APP_BCGOGO.Module.searchcompleteMultiselect;
        foo._opaqueContent(foo);
    },

    _onRelInstKeyup:function(event) {
        var foo = APP_BCGOGO.Module.searchcompleteMultiselect;
        foo._opaqueContent(foo);
    },

    themes:{
        "NORMAL":"normal",
        "SMALL":"small",
        "LARGE":"large",
        "GIANT":"giant"
    },

    changeThemes:function(theme, categoryList) {
        if (!theme) {
            GLOBAL.error("theme set error!");
            return;
        }
        this.detailsList.changeThemes(theme, categoryList);
    }
};
