;(function(){
    var me,
        C = {
            autocomplete:"ui-bcgogo-autocomplete",
            option:"ui-bcgogo-autocomplete-option",
            highlighted:"ui-bcgogo-autocomplete-highlighted",
            fontNormal:"bcgogo-autocomplete-font-normal"
        },
        T = {
            content:"<ul class='" + C.autocomplete + "'></ul>"
        };
    APP_BCGOGO.namespace("Module.autocompleteMultiselect");

    /**
     * @description autocomleteMultiselect , 这个实例只是 autocomplete 的一个拷贝 ，本组件本身并不支持 多选, 之后重整的时候将写成类的形式，通过新建类实例进行调用
     * @author 潘震
     * @data 2012-08-21
     */
    APP_BCGOGO.Module.autocompleteMultiselect = {
        _target:null,
        _data:[],
        _params:null,
        _$relInst:null,
        _uuid:"",

        setUUID:function(value) {
            me._uuid = value;
        },

        getUUID:function() {
            return me._uuid;
        },

        /**
         * @description init mouse and keyboard events
         * @param n
         */
        _initEvents:function(n) {
            $("." + C.option, n)
                .hover(function() {
                    $(this).addClass(C.highlighted);
                }, function() {
                    $(this).removeClass(C.highlighted);
                })
                .bind("click", me._clickBefore);
        },
        /**
         * @description init component
         * @param p
         */
        init:function(p) {
            me._target = $(p["selector"]);
            me.click = p["click"] || me.click;
            me._params = p;

            $(me._target).html("").html(T.content);

            $(me._target).hide();
        },
        /**
         * @description draw view by data
         * @param data
         */
        draw:function(data) {
//        GLOBAL.debug(data);
            if (!data.hasOwnProperty("uuid") || data["uuid"] != me._uuid) {
                return;
            }

            me._data = data["data"];
            me.clear();

            var s = "";
            for (var i = 0,len = me._data.length; i < len; i++) {
                var type = "-" + (me._data[i]["type"] == "category" ? "category" : "option"),
                    color = " " + C.fontNormal;
                s += "<li class='" + C.autocomplete + type + color + "'>" + me._data[i]["label"] + "</li>";
            }
            $("ul", me._target).html(s);
            $("ul li", me._target)
                .each(function() {
                    $(this).attr("title", $(this).text());
                })
                .tooltip({"delay": 0});

            if (me._params.hasOwnProperty("height")) {
                $("." + C.autocomplete, me._target).css("height", me._params["height"]);
            }
            me._initEvents(me._target);
        },

        show:function() {
            me._visible = true;
            $(me._target).show("fast");
        },

        _visible:false,

        hide:function() {
            me._visible = false;
            $(me._target).hide();
        },

        isVisible:function(){
            return me._visible || me._target.is(":visible");
        },

        /**
         * @description clear view
         */
        clear:function() {
            $("ul", me._target).html("");
        },

        _clickBefore:function(event) {
            var index = $("ul ." + C.option, me._target).index(event.target);
            me.click(event, index, me._data[index]);
        },

        // ==== callbacks ====
        /**
         * @description callback for click event , default is alert the option details , to override
         * @param event
         */
        click:function(event, index, data) {
            GLOBAL.debug("select the : " + $(event.target).text()
                + " and index is : " + index);
        },

        setWidth:function(value) {
            $("." + C.autocomplete, me._target).css("width", value + "px");
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

        // move down highlight
        next: function () {
            var $liList = $("." + C.autocomplete, me._target).find("li"),
                $liHighlight = $("." + C.highlighted, me._target),
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

        // move up highlight
        prev: function () {
            var $liList = $("." + C.autocomplete, me._target).find("li"),
                $liHighlight = $("." + C.highlighted, me._target),
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
            // 如果存在数据                 q
            for (var i = 0, len = data.length; i < len; i++) {
                this._storedData[i + ""] = data[i];
            }
        },

        getActivedData: function () {
            var $options = $("." + C.option, me._target),
                index = $options.index($("." + C.highlighted, me._target));

            return this._storedData[index+""];
        }

    };
    me = APP_BCGOGO.Module.autocompleteMultiselect;
})();