;
(function () {
    var me,
        C = {
            option: "ui-bcgogo-autocomplete-option",
            highlighted: "ui-bcgogo-autocomplete-highlighted",
            autocomplete: "ui-bcgogo-autocomplete",
            fontPre: "bcgogo-autocomplete-font-"
        },
        T = {
            content: "<ul class='" + C.autocomplete + "'></ul>"
        };
    APP_BCGOGO.namespace("Module.autocomplete");

    /**
     * @description autocomlete
     * @author 潘震
     * @data 2012-08-21
     */
    APP_BCGOGO.Module.autocomplete = {
        // TODO 太长， 暂时保留此接口， 冗余定义 _$ 接口
        _target: null,
        _$:null,
        _data: [],
        _params: null,
        _uuid: "",
        _theme: null,
        _$relInst: null,

        // TODO 新增变量，尚未对其赋值
        _$relNode: null,

        setUUID: function (value) {
            me._uuid = value;
        },

        getUUID: function () {
            return me._uuid;
        },

        /**
         * @description init mouse and keyboard events
         * @param n
         */
        _initEvents: function (n) {
            $("." + C.option, n)
                .hover(function () {
                    me.setRowHoverState($(this), true);
                }, function () {
                    me.setRowHoverState($(this), false);
                })
                .bind("click", me._clickBefore);
        },
        /**
         * @description init component
         * @param p
         */
        init: function (p) {
            me._target = $(p["selector"]);
            me._$ = me._target;
            me.click = p["click"] || me.click;
            me._params = p;
            me._theme = p["theme"] || null;

            $(me._$).html("").html(T.content);

            $(me._$).hide();
        },
        /**
         * @description draw view by data
         * @param data
         */
        draw: function (data, inputtingUserData) {
//        GLOBAL.debug(data);
            if (!data.hasOwnProperty("uuid") || data["uuid"] != me._uuid) {
                return;
            }

            me._data = data["data"];
            me.clear();
            me.storeUserData(inputtingUserData || {});

            var s = "";
            for (var i = 0, len = me._data.length; i < len; i++) {
                var type = "-" + (me._data[i]["type"] == "category" ? "category" : "option"),
                    theme = " " + C.fontPre + ( (me._theme && type === "-option") ? me._theme : "normal" );
                s += "<li class='" + C.autocomplete + type + theme + "'>" + me._data[i]["label"] + "</li>";
            }
            $("ul", me._$).html(s);
            $("ul li", me._$)
                .each(function () {
                    $(this).attr("title", $(this).text());
                })
                .tooltip({"delay": 0});

            if (me._params.hasOwnProperty("height")) {
                $("." + C.autocomplete, me._$).css("height", me._params["height"]);
            }
            this._initEvents(me._$);
        },

        show: function () {
            me._visible = true;
            // fast or blind or nothing
            $(me._$).show();
        },

        _visible: false,

        hide: function () {
            me._visible = false;
            $(me._$).hide();
        },

        isVisible: function () {
            return me._visible || me._$.is(":visible");
        },

        /**
         * @description clear view
         */
        clear: function () {
            $("ul", me._$).html("");
        },

        action: function (actionName) {
            switch (actionName) {
                case "up":
                    this.prev();
                    break;
                case "down":
                    this.next();
                    break;
            }
        },

        next: function () {
            var $liList = $("." + C.autocomplete, me._$).find("li"),
                $liHighlight = $("." + C.highlighted, me._$),
                len = $liList.length;
            var index = $liList.index($liHighlight);

            if (index > -1) {
                $liList.eq(index).removeClass(C.highlighted);
            }

            if (len > 0) {
                if (index == len - 1) {
                    index = -1
                } else {
                    index++;
                }

                if (index !== -1) {
                    $liList.eq(index).addClass(C.highlighted);
                }
            }
        },

        prev: function () {
            var $liList = $("." + C.autocomplete, me._$).find("li"),
                $liHighlight = $("." + C.highlighted, me._$),
                len = $liList.length;
            var index = $liList.index($liHighlight);

            if (index > -1) {
                $liList.eq(index).removeClass(C.highlighted);
            }

            if (len > 0) {
                if (index == -1) {
                    index = len - 1;
                } else {
                    index--;
                }

                if (index !== -1) {
                    $liList.eq(index).addClass(C.highlighted);
                }
            }
        },

        _clickBefore: function (event) {
            var index = $("ul ." + C.option, me._$).index(event.target);
            me.click(event, index, me._data[index]);
        },

        // ==== callbacks ====
        /**
         * @description callback for click event , default is alert the option details , to override
         * @param event
         */
        click: function (event, index, data) {
            GLOBAL.debug("select the : " + $(event.target).text()
                + " and index is : " + index);
        },

        setWidth: function (value) {
            $("." + C.autocomplete, me._$).css("width", value + "px");
        },

        setRowHoverState: function ($row, isHover) {
            $row[isHover ? "addClass" : "removeClass"](C.highlighted);
        },

        setRelInst: function ($node) {
            this._$relInst = $node;
        },

        /**
         * hashMap
         * {
         *     "-1":"",
         *     "0":"",
         *     "1":"",
         *     "2":""
         * }
         */
        _storedData: [],

        /**
         * TODO 提供新的接口 inputtingUserData
         * @param inputtingUserData
         * {
         *
         * }
         */
        storeUserData: function (inputtingUserData) {
            this._storedData = {};

            var userData = {
                label:"",
                type:"",
                details:inputtingUserData
            };

            if (!this._$relInst) return;
            if (!this._data) return;

            // 如果 存在 关联文本框
            this._storedData["-1"] = userData;

            var data = this._data;
            // 如果存在数据
            for (var i = 0, len = data.length; i < len; i++) {
                this._storedData[i + ""] = data[i];
            }
        },

        getActivedData: function () {
            var $options = $("." + C.option, me._$),
                index = $options.index($("." + C.highlighted, me._$));

            return this._storedData[index+""];
        }

    };
    me = APP_BCGOGO.Module.autocomplete;
})();

