;
(function () {
    App.namespace("Module.MessageBottomPush");

    // Data Proxy
    var DataProxy = function () {
        this.sourceData = undefined;
        this.data = undefined;
    };

    DataProxy.method("parse", function (sourceData) {
        if (G.isEmpty(sourceData)) {
            this.sourceData = sourceData;
        }

        if (typeof sourceData === "string") {
            this.data = JSON.parse(sourceData);
        } else {
            this.data = sourceData;
        }

        return this;
    });

    DataProxy.method("getData", function () {
        return this.data;
    });

    DataProxy.method("setSourceData", function (sourceData) {
        this.sourceData = sourceData;

        return this;
    });

    // TODO 比如 messageBottomVisited:""
    // TODO messageBottomVisited 数据格式    json string  [{"uuid":"", }, {"uuid":"", ]
    // TODO messageBottomToggleOnTimer 数据格式    string number
    // TODO messageBottomToggleOffTimer 数据格式    string number
    // webStorageClass


    /**
     * ==================================
     * @public
     *   init(param)
     *   show()
     *   hide()
     *   dispose()
     * ==================================
     * @constructor
     */
    var MessageBottomPush = function () {
        this.C = {
            self: "bcgogo-messageBottomPush",
            title: "JTitle",
            closeButton: "JCloseButton",
            content: "JContent",
            ui_state_hover: "ui-state-hover",
            ui_dialog_title: "ui-dialog-title",
            ui_dialog_content: "ui-dialog-content"
        };

        this.T = {};
        this.T.self = "" +
            '<div style="font-size:12px;display: block; z-index: 1008; outline: 0px; position: fixed; height: auto; width: 300px; bottom: 0px; right: 0px;" class="ui-dialog ui-widget ui-widget-content ui-corner-all  ui-draggable ui-resizable" tabindex="-1" role="dialog" aria-labelledby="ui-dialog-title-id-test">' +
            '    <div style="cursor: auto;" class="ui-dialog-titlebar ui-widget-header ui-corner-all ui-helper-clearfix">' +
            '        <span class="ui-dialog-title" id="ui-dialog-title-id-test">&nbsp;</span>' +
            '        <a href="#" class="ui-dialog-titlebar-close ui-corner-all" role="button">' +
            '            <span class="ui-icon ui-icon-closethick">close</span>' +
            '        </a>' +
            '    </div>' +
            '    <div style="width: auto; min-height: 92px; height: auto;" class="ui-dialog-content ui-widget-content">' +
            '        &nbsp;' +
            '    </div>' +
            '    <div class="ui-resizable-handle ui-resizable-n" style="z-index: 1000;"></div>' +
            '    <div class="ui-resizable-handle ui-resizable-e" style="z-index: 1000;"></div>' +
            '    <div class="ui-resizable-handle ui-resizable-s" style="z-index: 1000;"></div>' +
            '    <div class="ui-resizable-handle ui-resizable-w" style="z-index: 1000;"></div>' +
            '    <div class="ui-resizable-handle ui-resizable-sw" style="z-index: 1000;"></div>' +
            '    <div class="ui-resizable-handle ui-resizable-ne" style="z-index: 1000;"></div>' +
            '    <div class="ui-resizable-handle ui-resizable-nw" style="z-index: 1000;"></div>' +
            '</div>';


        this._$ = undefined;
        this._param = undefined;
        this._dataProxy = undefined;

        this._initTimeout = 0;
        this._initTimeoutTimerId = 0;

        this._requestInterval = 2 * 60 * 1000;
        this._requestIntervalTimerId = 0;
        this._requestIntervalTs = 0;

        this._hideTimeout = 30 * 1000;
        this._hideTimeoutTimerId = 0;
//        this._isPolling = false;

        // TODO do multi-page timer synchronous!
    };

    MessageBottomPush.method("show", function () {
        var that = this;

        if(this._$){
            this._$
                .css("bottom", -1000)
                .show()
                .animate({
                    "bottom": 0
                });
        }else{
            throw new Error({"errorId": "", "error": {"name": "MessageBottomPush _$ is undefined!"}});
        }


        this.startAutoHide();

        return this;
    });

    MessageBottomPush.method("startAutoHide", function() {
        var that = this;
        this._hideTimeoutTimerId = setTimeout(function() {
            that.hide();

            var onAutoHide = that._param.onAutoHide || function() {G.error("default onAutoHide sample");};
            onAutoHide(that._dataProxy.getData());
        }, this._hideTimeout);
        return this;
    });

    MessageBottomPush.method("stopAutoHide", function() {
        clearTimeout(this._hideTimeoutTimerId);
        return this;
    });

    MessageBottomPush.method("hide", function () {
        this._$
            .css("bottom", 0)
            .animate({
                "bottom": -1000
            }, 400, "linear", function () {
                $(this).hide();
            });

        if (this._hideTimeoutTimerId > 0) {
            clearTimeout(this._hideTimeout);
        }

        return this;
    });

    /**
     *
     * @param param {
     *     "title":"",
     *     "contentDefault":"",
     *     "onTweet":function(data, self){},
     *     "onItemClick":function(data, itemData, $inst, event, self){},
     *     "onClose":function(data, event, self) {},
     *     "url":"xxxxxxxx",
     *     “onAutoHide":function(data){},
     *     "requestParamDecorator":function(){}
     * }
     */
    MessageBottomPush.method("init", function (param) {
        if (this._$) {
            throw new Error({"errorId": "", "error": {"name": "duplicate init!"}});
            return this;
        }

        var that = this,
            _$ = $(this.T.self);

        this._$ = _$;
        $(document.body).append(_$);
        _$
            .css("bottom", -1000)
            .attr("id", "id-messageBottomPush-" + G.generateUUID());

        $(".ui-dialog-titlebar-close", _$)
            .hover(function () {
                $(this)
                    .addClass(that.C.ui_state_hover);
            }, function () {
                $(this)
                    .removeClass(that.C.ui_state_hover);
            })
            .bind("click", function (event) {
                that.hide();

                if (that._param.onClose && that._dataProxy) {
                    that._param.onClose(that._dataProxy.getData(), event, that);
                }
            });

        // store params
        if (!G.isEmpty(param)) {
            this._param = param;
        }

        if (!this._param.requestParamDecorator) {
            this._param.requestParamDecorator = function () {
                return {};
            }
        }

        this._requestIntervalTs = G.localStorage.get("messageBottomPushSyncTs") || 0;

        // messageBottomPushSyncInterval
        this._initTimeout = parseFloat(G.localStorage.get("messageBottomPushSyncInterval")) || 0;
        this._initTimeoutTimerId = setTimeout(function () {
            that._manuallyPing(that._onPingRequest);
            that._startPing(that._requestInterval, that._onPingRequest);
        }, this._initTimeout);


        // multi-page timer strategy
        $(window).bind("beforeunload unload", function (event) {
            var lastRequestTs = (that._requestIntervalTs === 0) ? (new Date()).getTime() : that._requestIntervalTs,
                currentTs = (new Date()).getTime(),
                diff = currentTs - lastRequestTs,
                interval = that._requestInterval - diff;

            G.localStorage.set("messageBottomPushSyncTs", lastRequestTs + "");
            G.localStorage.set("messageBottomPushSyncInterval", interval + "");
            // if you want to show a confirm when window's closing, code 'return "confirm notice infos"'
        });

        return this;
    });

    MessageBottomPush.method("_onPingRequest", function (sourceData) {
        G.debug("MessageBottomPush's sourceData : \n" + sourceData);

        if (G.isEmpty(sourceData)) {
            G.error("MessageBottomPush's method _ping() data is empty");
            return;
        }

        var dataProxy = new DataProxy(),
            data = dataProxy.parse(sourceData).getData();

        this._dataProxy = dataProxy;
        this._requestInterval = parseFloat(data.requestInterval || this._requestInterval);
        this._hideTimeout = parseFloat(data.hideInterval || this._hideTimeout);

        var that = this;
        if (this._param.onTweet) {
            this._param.onTweet(data, that);
        }

        if (G.isEmpty(data) || G.isEmpty(data.data)) return;
        this
            ._updataView(data)
            .show();
    });

    /**
     *
     * @param data get full data by ping()
     */
    MessageBottomPush.method("_updataView", function (data) {
        var that = this,
            $content = $(".ui-dialog-content", this._$);

        if (G.isEmpty(data)) return this;

        if (G.isEmpty(data.data)) return this;

        $(".ui-dialog-title", this._$).html(data.title);

        var itemDataList = data.data,
            itemTemplate = "";

        for (var i = 0, len = itemDataList.length; i < len; i++) {
            itemTemplate +=
                "<div style='padding:0;margin:0;padding-bottom: 5px;font-size: 13px;font-family: \"宋体\"; '>" + itemDataList[i].promptContent + "<a class='details'>点击查看详情</a>" + "</div>";
        }

        $content
            .html("")
            .html(itemTemplate);

        var hasRedirectUrl = false;
        $.each(itemDataList, function(index, value) {
            hasRedirectUrl = !G.isEmpty(value.redirectUrl);
            return !hasRedirectUrl;
        });

        $(".details", this._$)
            .css({
                "position": "absolute",
                "float": "right",
                "right": 5,
                "bottom": 5
            })
            .toggle(hasRedirectUrl)
            .parent("div")
            .hover(function () {
                $("a", this)
                    .css({
                        "text-decoration": "underline",
                        "color": "#FF8817",
                        "cursor": "pointer"
                    });
            }, function () {
                $("a", this)
                    .css({
                        "text-decoration": "none",
                        "color": "#000"
                    });
            })
            .closest("div").bind("click", function (event) {
                // click one item total div to toggle the event , so when we click text or button can do action
                if (!that._param.onItemClick) return;

                var $inst = $(event.currentTarget),
                    proxy = that._dataProxy,
                    data = proxy.getData(),
                    itemData = data.data[0];

                that._param.onItemClick(data, itemData, $inst, event, that);
            })
            .css("cursor", (hasRedirectUrl ? "pointer" : "normal"));







//        for (var i = 0, len = itemDataList.length; i < len; i++) {
//            itemTemplate +=
//                "<div style='padding:0;margin:0;padding-bottom: 5px;font-size: 13px;font-family: \"宋体\"; '>" + itemDataList[i].content +"</div>";
//        }
//
//        $content
//            .html("")
//            .html(itemTemplate).find("a").each(function (index, value) {
//                $(this)
//                    .attr("itemindex", index)
//                    .bind("click", function (event) {
//                        if (!that._param.onItemClick) return;
//
//                        var $inst = $(event.currentTarget),
//                            proxy = that._dataProxy,
//                            data = proxy.getData(),
//                            itemData = data.data[parseFloat($inst.attr("itemindex"))];
//
//                        that._param.onItemClick(data, itemData, $inst, event, that);
//                    })
//                    .parent("div")
//                    .hover(function () {
//                        $("a", this)
//                            .css({
//                                "text-decoration": "underline",
//                                "color": "#FF8817",
//                                "cursor": "pointer"
//                            });
//                    }, function () {
//                        $("a", this)
//                            .css({
//                                "text-decoration": "none",
//                                "color": "#000"
//                            });
//                    });
//            });

        $content
            .bind("mouseenter", function (event) {
                that.stopAutoHide();
                that._stopPing();
            })
            .bind("mouseleave", function(event) {
                that.startAutoHide();
                that._startPing(that._requestInterval, that._onPingRequest);;
            });


        return this;
    });

    MessageBottomPush.method("_ping", function (url, data, cbFnOnResponse) {
        var visibleStatus = App.Module.pageVisible.getCurrStatus();
        if(visibleStatus['hidden'] != undefined && visibleStatus['hidden']){
            return this;
        }
        var result = "";

//        App.Net.syncGet({
        App.Net.asyncPost({
            url: url,
            data: data,
            dataType: "json",
            success: function (json) {
                result = json;
            },
            error: function () {
                G.error("ajax error");
            },
            complete:function() {
                cbFnOnResponse(result);
            }
        });
        return this;
    });

    MessageBottomPush.method("dispose", function () {
        this._stopPing();
        clearTimeout(this._hideTimeout);
        clearTimeout(this._initTimeoutTimerId);
        if(this._$){
            this._$
                .css("bottom", -1000)
                .html("")
                .remove();
        }

        this._$ = undefined;
        this._param = undefined;
        this._dataProxy = undefined;
        this._requestInterval = undefined;
        this._hideTimeout = undefined;

        this._requestIntervalTimerId = undefined;
        this._hideTimeoutTimerId = undefined;
    });

    /**
     * @duration {Number} default is 0
     * @param fnCallback this callback will be called after _ping() every time
     */
    MessageBottomPush.method("_startPing", function (duration, fnCallback) {
        var that = this,
            result = undefined;

        if (this._requestIntervalTimerId > 0) return this;

        // send result to callback
        this._requestIntervalTimerId = setInterval(function () {
            var url = that._param.url,
                data = that._param.requestParamDecorator() || {};

            data.uuid = G.generateUUID();
            that._ping(url, data, function(responseResult) {
                result = responseResult;
                that._requestIntervalTs = (new Date()).getTime();
                fnCallback.call(that, result);
            });
        }, duration);

        // TODO set timer interval by data automatically

        return this;
    });

    MessageBottomPush.method("_manuallyPing", function (fnCallback) {
        var that = this,
            url = this._param.url,
            data = this._param.requestParamDecorator() || {};

        data.uuid = G.generateUUID();
        this._requestIntervalTs = (new Date()).getTime();

        this._ping(url, data, function(responseResult) {
            fnCallback.call(that, responseResult);
        });
        return this;
    });

    MessageBottomPush.method("_stopPing", function () {
        if (this._requestIntervalTimerId <= 0) return this;

        clearTimeout(this._initTimeoutTimerId);
        clearInterval(this._requestIntervalTimerId);
    });



    App.Module.MessageBottomPush = MessageBottomPush;
}());
