;(function () {
    APP_BCGOGO.namespace("APP_BCGOGO.Module.searchcomplete");
    var me,
        C = {
            searchcomplete:"bcgogo-ui-searchcomplete",
            autocomplete:"bcgogo-ui-searchcomplete-autocomplete",
            detailsList:"bcgogo-ui-searchcomplete-detailsList",
            arrow:"bcgogo-ui-searchcomplete-arrow"
        },
        T = {
            searchcomplete:'<div class="' + C.searchcomplete + '">'
                + '    <div class="' + C.autocomplete + '"></div>'
                + '    <div class="' + C.detailsList + '"></div>'
                + '</div>',
            arrow:"<div class='" + C.arrow + "'></div>"
        };

    var empty = GLOBAL.Lang.isEmpty,
        display = GLOBAL.Display;

    /**
     * @author 潘震
     * @description searchcomplete 组件的 detailsList 是可以多选的
     *              在用户单击选中列表中的数据后 ， 会触发 回调函数，回调函数会将选中的数据通过函数传参的方式回传给调用者
     *              若要使用此组件， 可以从 sampleCode 中快速上手
     * @date 2012-08-28
     */
    APP_BCGOGO.Module.searchcomplete = {
        _uuid:"",
        _target:null,
        _relInst:null,
        _$arrow:null,
        _theme:"normal",
        autocomplete:APP_BCGOGO.Module.autocomplete,
        detailsList:APP_BCGOGO.Module.detailsList,
        _data:null,
        isScroll:false,
        isTransparentAuto:false,
        isPreInputIsCtrAction:true,
        isKeyBoardControlEnabled:false,
        _width:undefined,
        _horizontalCenter:undefined,

        judgeInputIsCtrAction:function(keyName) {
            return G.contains(keyName, ["up", "down", "left", "right", "enter", "backspace", "esc"]);
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

        /**
         * @description 获取详细信息记录的数量
         * @returns {Number} 数量
         */
        getDetailsListCount:function () {
            return me.detailsList ? me.detailsList.getCount() : 0;
        },


        /**
         * @description 初始化
         * @param {Object} p
         */
        init:function (p) {
            me._target = p["selector"];
//            me.isScroll = p["isScroll"] || false;

            //-ongoing
            me.autoSet = p["autoSet"] || true;
            me.onKeyboardSelect = p["onKeyboardSelect"] || undefined;

            me.isScroll = false;
            me.isTransparentAuto = p["isTransparentAuto"] || false;

            // content
            var s = T.searchcomplete;
            $(me._target)
                .html(s)
                .css("position", "absolute").css("float", "left")
                .unbind("mouseenter", me._onTargetMouseenter).bind("mouseenter", me._onTargetMouseenter)
                .unbind("mouseleave", me._onTargetMouseleave).bind("mouseleave", me._onTargetMouseleave);

            if (!empty(p["isKeyBoardControlEnabled"])) this.isKeyBoardControlEnabled = p["isKeyBoardControlEnabled"];
            if (!empty(p["onGetInputtingData"])) this.onGetInputtingData = p["onGetInputtingData"];
            if (!empty(p["onFinishedKeyboardControl"])) this.onFinishedKeyboardControl = p["onFinishedKeyboardControl"];
            // 此参数不经需要在子组件计算子它自己的width, 此参数只在 theme 为 fixWidth 时有用
            if (!empty(p["width"])) this._width = p["width"];
            // 此参数只在 theme 为 fixWidth 有用， 并且如果此参数未设置的话 按照 fixWidth 布局默认的居中方式进行定位, horizontalCenter 使用 flex layout 定位
            if (!empty(p["horizontalCenter"])) this._horizontalCenter = p["horizontalCenter"];


            // autocomplete
            var paramsAutocomplete = {
                "selector":$("." + C.autocomplete, me._target),
                "theme":"blue"
            };
            if (!empty(p["autocompleteClick"]))     paramsAutocomplete.click = p["autocompleteClick"];
            // TODO 新增加的 autocomplete 键盘事件处理
            if (!empty(p["autocompleteKeyboardControl"])) paramsAutocomplete.onKeyControl = p["autocompleteKeyboardControl"];



            // detailsList
            var paramsDetailsList = {
                "selector":$("." + C.detailsList, me._target),
                "categoryList":p["detailsListCategoryList"],
                "theme":p["theme"] || "normal"
            };
            if (!empty(p["onDetailsListSelect"]))           paramsDetailsList.onSelect = p["onDetailsListSelect"];
            if (!empty(p["onMore"]))                        paramsDetailsList.onMore = p["onMore"];
            if (!empty(p["onDetialsListDbClick"]))          paramsDetailsList.onDbClick = p["onDetialsListDbClick"];
            if (!empty(p["onPrev"]))                        paramsDetailsList.onPrev = p["onPrev"];
            if (!empty(p["pageSize"]))                      paramsDetailsList.pageSize = p["pageSize"];
            if (!empty(p["enableColumnContentHidden"]))     paramsDetailsList.enableColumnContentHidden = p["enableColumnContentHidden"];
            if (!empty(p["columnContentHiddenKeycode"]))    paramsDetailsList.columnContentHiddenKeycode = p["columnContentHiddenKeycode"];

            me.autocomplete.init(paramsAutocomplete);
            me.detailsList.init(paramsDetailsList);

            // init arrow
            var body = window.document.body,
                $arrow = $(T.arrow);
            $arrow
                .attr("id", "bcgogo-searchcomplete-arrow-" + G.generateUUID())
                .appendTo(body)
                .css("left", G.getX(me._target[0]));
            me._$arrow = $arrow;
        },
        /**
         * @description 渲染数据
         * @param {JSON} data
         * @param {String} cpName 组件名字
         */
        draw:function (data, cpName) {
            if (cpName && me[cpName]) {
                if( cpName === "autocomplete"
                    && me.isKeyBoardControlEnabled
                    && me.onGetInputtingData ) {
                    me[cpName].draw(data, me.onGetInputtingData())
                } else {
                    me[cpName].draw(data);
                }
            } else if (data["uuid"] && data["uuid"] == me._uuid) {
                if(me.isKeyBoardControlEnabled
                    && me.onGetInputtingData) {
                    me.autocomplete.draw(data["dropDown"], me.onGetInputtingData());
                }
                me.detailsList.draw(data["history"]);
                if (me.isScroll) {
                    me.scrollIntoView();
                }
            }
        },
        /**
         * @description 清空数据
         * @param {String} cpName 组件名字
         */
        clear:function (cpName) {
            if (cpName) {
                me[cpName].clear();
            } else {
                me.autocomplete.clear();
                me.detailsList.clear();
            }
        },
        /**
         * @description 显示
         * @param {String} cpName 组件名字
         */
        show:function (cpName) {
            if (cpName) {
                me[cpName].show();
            } else {
                $(me._target).show();
            }

            if (me.autocomplete.isVisible() && me.detailsList.isVisible()) {
                var left = $("ul", me.autocomplete._target).width();
                $("." + C.detailsList, me._target).css("left", left);
                me._$arrow.show();

                if (me.isScroll) {
//                    setTimeout(function () {
                    me.scrollIntoView()
//                    }, 150);
                }
            }
        },
        /**
         * @description 隐藏
         * @param {String} cpName 组件名字
         */
        hide:function (cpName) {
            if (cpName) {
                me[cpName].hide();
            } else {
                $(me._target).hide();
                me._$arrow.hide();
                me._viewState = null;
            }
        },

        updateArrow:function () {
            if (me._relInst) {
                var $inst = $(me._relInst),
                    p = {
                        height:$inst.outerHeight(),
                        top:G.getY($inst[0]),
                        left:G.getX($inst[0])
                    };
                me._$arrow
                    .css("left", p.left)
                    .css("top", p.top + p.height);
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

            // refresh events
            $(o)
                .unbind("click", me._onRelInstClick).bind("click", me._onRelInstClick)
                .unbind("focus", me._onRelInstFocus).bind("focus", me._onRelInstFocus)
                .unbind("keyup", me._onRelInstKeyup).bind("keyup", me._onRelInstKeyup);

            // update UI styles
            me._relInst = o;
            me.autocomplete.setRelInst($(o));

            // TODO set the horizontalCenter="" verticalCenter="" with flex layout


            var pos = {},
                isFixWidth = (me._theme === me.themes.FIX_WIDTH);
            if (isFixWidth) {
                me.autocomplete.setWidth(200);

                var bodyWidth = $(document.body).outerWidth(),
                    autocompleteWidth = parseFloat($(".ui-bcgogo-autocomplete", me.autocomplete._target).css("width")),
                    detailsListWidth = null;

                // 如果存在 width
                if (me._width) {
                    me._target.width(me._width);
                    detailsListWidth = me._width - autocompleteWidth;
                    // TODO  应当在这里设置 detailsList的 width  为计算出来的  detailsListWidth， 提供自定义宽度功能，当然， detailsList 本身要提供一个方法
                    me.detailsList.setWidth(detailsListWidth);
                } else {
                    detailsListWidth = parseFloat($(".bcgogo-ui-detailsList-accordion-fixWidth", me.detailsList._target).css("width"));
                }

                // 设置 horizontalCenter
                if(me._horizontalCenter) {
                    pos.left = ( bodyWidth - detailsListWidth - autocompleteWidth ) / 2 + me._horizontalCenter;
                } else {
                    pos.left = ( bodyWidth - detailsListWidth - autocompleteWidth ) / 2;
                }
            } else {
                me.autocomplete.setWidth($(o).outerWidth());
                pos.left = G.getX(o);
            }
            pos.top = G.getY(o) + $(o).outerHeight();
            $(me._target)
                .css("left", pos["left"] + "px")
                .css("top", pos["top"] + "px");

            me._opaqueContent();
            me.updateArrow();

            if (me.autocomplete.isVisible()
                && me.detailsList.isVisible()
                && me.isScroll) {
//                setTimeout(function () {
                me.scrollIntoView()
//                }, 150);
            }
        },

        scrollIntoView:function () {
            var arrowHeight = me._$arrow.outerHeight(),
                autocompleteHeight = me.autocomplete._target.outerHeight(),
                contentHeight = arrowHeight + autocompleteHeight,
                relInstY = display.getY(me._relInst),
                relInstHeight = $(me._relInst).height();

            $("html body").animate({"scrollTop":(relInstY - relInstHeight - contentHeight)}, 800 );
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

            var detailsListWidth = me.detailsList.isVisible() ? me.detailsList._target.outerWidth() : 0,
                detailsListHeight = me.detailsList._target.height(),
                autocompleteWidth = me.autocomplete.isVisible() ? me.autocomplete._target.outerWidth() : 0;

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

        _onKeyboardControlCommand:function(event){
            // 1) store list data
            // 2) get current selected option index
            // 3) change rowIndex and reset list color
            // 4) set value to relInst:bcgogo-searchcomplete
            // 5) call onKeyboardSelect method

            if(!me.isKeyBoardControlEnabled) return;

            var name = G.keyNameFromEvent(event);
            if(!G.contains(name, ["up", "down", "left", "right"])
                && me.autocomplete.isVisible()) {
                // 在外部的函數中， 調用  searchcomplete.autocomplete.storeUserData(inputtingUserData);
                me.autocomplete.storeUserData(me.onGetInputtingData())
            }

            if(G.contains(name, ["up", "down"])
                && me.autocomplete.isVisible()) {
                me.autocomplete.action(name);

                me.onFinishedKeyboardControl(event,  me.autocomplete.getActivedData());
            } else if(G.contains(name, ["enter"])
                && me.autocomplete.isVisible()) {
                G.info("press enter!");
            }
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

            // new functionality
            me._onKeyboardControlCommand(event);
        },

        themes:{
            "NORMAL":"normal",
            "SMALL":"small",
            "LARGE":"large",
            "GIANT":"giant",
            "FIX_WIDTH":"fixWidth"
        },

        changeThemes:function (theme, categoryList) {
            if (!theme) {
                G.error("theme set error!");
                return;
            }
            me._theme = theme;
            me.detailsList.changeThemes(theme, categoryList);
        }
    };

    me = APP_BCGOGO.Module.searchcomplete;
})();