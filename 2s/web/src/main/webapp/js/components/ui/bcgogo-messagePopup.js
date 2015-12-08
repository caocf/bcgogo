;
(function () {
    var me,
        C = {
            hook_hover: "bcgogo-messagePopup-hook-hover",
            group: "bcgogo-messagePopup-group",
            panel: "bcgogo-messagePopup-panel",
            request: "bcgogo-messagePopup-request",
            notification: "bcgogo-messagePopup-notification",
            news: "bcgogo-messagePopup-news",
            title: "bcgogo-messagePopup-title",
            content: "bcgogo-messagePopup-content",
            nilMessage: "bcgogo-messagePopup-nil",
            closeButton: "bcgogo-messagePopup-closeButton",
            J_visibled: "J-bcgogo-messagePopup-visibled",
            J_hasMessage: "J-bcgogo-messagePopup-hasMessage",
            bubble: "bcgogo-bubble",
            message: "bcgogo-messageImg",
            messageHover: "bcgogo-messageImg-hover",
            arrow: "bcgogo-messagePopup-arrow",
            locked: "bcgogo-messagePopup-locked"
        },
        T = {
            messagePopup: '' +
//                '<div class="' + C.group + '">' +
                '   <div class="' + C.panel + '">' +
                '       <div class="' + C.request + '">' +  // relative
                '           <p class="'+ C.title+'"><a>请求</a></p>' +
                '           <div class="' + C.content + '"><a>您有&nbsp;<span style="font-weight: bold;font-size: 14px;">0</span>&nbsp;条待处理请求</a></div>' +
                '           <div class="' + C.nilMessage + '"><a>您暂无新请求!</a></div>' +
                '           <div style="display: none;" class="' + C.locked + '"><b style="color:#ddd;">您无权使用该功能</b></div>' +
                '       </div>' +
                '       <div class="' + C.notification + '">' + // relative
                '           <p class="'+ C.title+'"><a>通知</a></p>' +
                '           <div class="' + C.content + '"><a>您有&nbsp;<span style="font-weight: bold;font-size: 14px;">0</span>&nbsp;条新通知</a></div>' +
                '           <div class="' + C.nilMessage + '"><a>您暂无新通知!</a></div>' +
                '           <div style="display: none;" class="' + C.locked + '"><b style="color:#ddd;">您无权使用该功能</b></div>' +
                '       </div>' +
                '       <div class="' + C.news + '">' + // relative
                '           <p class="'+ C.title+'"><a>站内消息</a></p>' +
                '           <div class="' + C.content + '"><a>您有&nbsp;<span style="font-weight: bold;font-size: 14px;">0</span>&nbsp;条新消息</a></div>' +
                '           <div class="' + C.nilMessage + '"><a>您暂无新消息!</a></div>' +
                '           <div style="display: none;" class="' + C.locked + '"><b style="color:#ddd;">您无权使用该功能</b></div>' +
                '       </div>' +
                '   </div>' +
//                '</div>' +
//                '<div class="' + C.closeButton + '" >╳</div>',
                '<div class="' + C.closeButton + '" >x</div>',
            bubble: '<div class="' + C.bubble + '"><span>(0)</span></div>',
//            arrow: '<div class="' + C.arrow + '"></div>',
            message: '<div class="' + C.message + '"></div>'
        },
    // unit: (ms)
        INTERVAL = 10 * 1000;

    if (window.top.App.Module.messagePopup) {
        return;
    }

    APP_BCGOGO.namespace("Module.messagePopup");
    App.Module.messagePopup = {
        _$: null,
        _interval: INTERVAL,
        _timerPolling: 0,
        _pingUrl: null,
        _$hook: null,
        _$bubble: null,
        _$message: null,
        _$arrow: null,
        _isPolling: false,
        _inquireCallback: null,
        _isMouseEventEnabled: true,

        /**
         *
         * @param p
         * {
         *     url:"",
         *
         *     // default is false
         *     isPolling:false,
         *     // default is null
         *     // @param status     "success" | "failed"
         *     inquireCallback:function(status){},
         *     // default is null
         *     showCallback:function(){}
         * }
         * @return {*}
         */
        init: function (p) {
            if (me._$) {
                return;
            }

            // set parameters
            me._pingUrl = p["url"];

            me._initArrow();
            me._initMessagePopup();
            me._initBubble();
            me._initMessage();

            me.hook(p["selector"]);

            me._isPolling = p["isPolling"] || false;

            me._inquireCallback = p["inquireCallback"] || null;
            me._showCallback = p["showCallback"] || null;

            if (me._isPolling) {
                me._startPolling();
            }

            return me;
        },

        getIsMouseEventEnabled: function () {
            return me._isMouseEventEnabled;
        },

        setIsMouseEventEnabled: function (bool) {
            me._isMouseEventEnabled = bool;
        },

        hook: function (node) {
            if (me._$hook) {
                return;
            }

            me._$hook = $(node);

            var offset = me._$hook.offset();

            me._$arrow.css({
                "top": offset.top + me._$hook.height(),
                "left": offset.left + (me._$hook.width() - me._$arrow.width()) * 0.5,
                "z-index": 2
            });

            me._$.css({
                "top": offset.top + me._$hook.height() + 5,
                "left": offset.left/*$("." + C.panel, me._$).width()*//* + me._$hook.width() - */,
                "z-index": 1
            });

            //////////////////////
            me._$.find("." + C.closeButton).css({
                "top": 5,
                "right": 5
            }).hide();

            me._$bubble
                .css({
                    top: offset.top + (me._$hook.height() - me._$bubble.height()) * 0.5 - 1,
                    left: offset.left + me._$hook.width(),
                    "z-index": 1
                })
                .addClass(C.J_visibled);
            me.updateBubble();

            me._$message
                .css({
                    top: offset.top + (me._$hook.height() - me._$message.height()) * 0.5,
                    left: offset.left - me._$message.width() - 3,
                    "z-index": 1
                });

            me._$hook
                .css("cursor", "pointer")
                .bind("mouseenter", function (event) {

                    $(this).css("text-decoration", "underline");
                    $(this).addClass(C.hook_hover);

                    if (!me._isMouseEventEnabled) {
                        return;
                    }
                    me._showMessagePopup();
                    me._showArrow();

                    me._$message.addClass(C.messageHover);

                })
                .bind("mouseleave", function (event) {
                    $(this).css("text-decoration", "none");
                    me._$message.removeClass(C.messageHover);

                    var p = {
                        x: (event.pageX || event.layerX),
                        y: (event.pageY || event.layerY)
                    };

                    var $target = $(event.currentTarget),
                        offset = $target.offset(),
                        pTarget = {
                            w: $target.width(),
                            h: $target.height(),
                            x: offset.left,
                            y: offset.top
                        };

                    if (p.y >= (pTarget.y + pTarget.h)) return;
                    $(this).removeClass(C.hook_hover);
                    me._hideArrow();
                    me._hideMessagePopup();

                })
                .bind("click", function (event) {
                    me._$
                        .toggleClass(C.J_visibled)
                        .toggle(me._$.hasClass(C.J_visibled));

                    me._$arrow
                        .toggleClass(C.J_visibled)
                        .toggle(me._$arrow.hasClass(C.J_visibled));
                    if(me._$.hasClass(C.J_visibled)){
                        $(this).addClass(C.hook_hover);
                    }else{
                        $(this).removeClass(C.hook_hover);
                    }
                    if (me._$.hasClass(C.J_visibled) && me._showCallback) {
                        me._showCallback();
                    }
                });

            me._$
                .bind("mouseleave", function (event) {
                    if (!me._isMouseEventEnabled) {
                        return;
                    }

                    var p = {
                            x: (event.pageX || event.layerX),
                            y: (event.pageY || event.layerY)
                        },
                        offsetTarget = $(this).offset(),
                        pTarget = {
                            x: offsetTarget.left,
                            y: offsetTarget.top
                        },
                        offsetHook = me._$hook.offset(),
                        pHook = {
                            x: offsetHook.left,
                            y: offsetHook.top,
                            w: me._$hook.width(),
                            h: me._$hook.height()
                        };

                    if( p.y <= (pTarget.y + 2)
                        && p.x >= pHook.x
                        && p.x <= (pHook.x + pHook.w)) {
                        return;
                    }

                    me._hideMessagePopup();
                    me._$hook.removeClass(C.hook_hover);
                    me._hideArrow();
                });
        },

        getBubbleState: function () {
            var $bubble = me._$bubble;
            // hide, show
            if ($bubble === null) {
                return null;
            } else {
                return $bubble.hasClass(C.J_hasMessage)
                    && $bubble.hasClass(C.J_visibled);
            }
        },

        updateBubble: function () {
            var bubbleState = me.getBubbleState();
            if (bubbleState === null) {
                return;
            }

            me._$bubble.toggle(bubbleState);
        },

        inquire: function (a) {
            clearTimeout(me._timerPolling);
            App.Net.asyncGet({
                url: G.normalize(me._pingUrl),
                dataType: "json",
                success: function (data, textStatus, jqXHR) {
                    if (me._$) {
                        me.renderData(data);
                    }
                    me._inquireCallback("success", data);
                },
                error: function () {
                    G.error("MessagePopup 51Ping Server Error!");
                    me._inquireCallback("failed");
                },
                complete: function () {
                    if (me._isPolling) {
                        setTimeout(me.inquire, me._interval);
                    }
                }
            });

            return me;
        },

        renderData: function (data) {
            var $item, amount = 0, isNil = true, isLocked = false;
            for (var k in data) {
                $item = $("." + C[k], me._$);
                $item.find("span").text(data[k]["count"]);
                $item.find("p").find("a").attr("href", data[k]["allDetailsUrl"]);
                $item.find("div").find("a").attr("href", data[k]["unreadDetailsUrl"]);

                isNil = parseInt(data[k]["count"]) === 0;
                // 这里前后台不统一，  后台的数据应该使用字段名 locked
                isLocked = data[k]["lock"];
                isLocked = (isLocked !== false && isLocked !== "false");
                $item.find("." + C.content).toggle(!isNil && !isLocked);
                $item.find("." + C.nilMessage).toggle(isNil && !isLocked);
                $item.find("." + C.locked).toggle(isLocked);
                if(isLocked){
                    $item.hide();
                }

                amount += parseInt(data[k]["count"]) || 0;
            }

            me._$bubble.find("span").text("(" + amount + ")");
            if (amount > 0) {
                me._$bubble.addClass(C.J_hasMessage);
            }
            me.updateBubble();

            // TODO index() ...

        },

        clear: function () {
            clearTimeout(me._timerPolling);
            //clear hook
            if (me._$hook) {
                me._$hook
                    .unbind("mouseenter mouseleave click")
                    .css("cursor", "default");
                me._$hook = null;
            }

            //clear bubble
            if (me._$bubble) {
                me._$bubble.remove();
                me._$bubble = null;
            }

            //clear arrow
            if (me._$arrow) {
                me._$arrow.remove();
                me._$arrow = null;
            }

            //clear _$
            if (me._$) {
                me._$.unbind("mouseleave");
                me._$.remove();
                me._$ = null;
            }
        },

        _initArrow: function () {
            me._$arrow = $("<div id='bcgogo-messagePopup-arrow-" + G.generateUUID() + "'></div>");
            me._$arrow
                .append($(T.arrow))
                .css("position", "absolute");
            $(window.top.document.body).append(me._$arrow);
            me._hideArrow();
        },

        _showArrow: function () {
            me._$arrow
                .addClass(C.J_visibled)
                .show(0, function () {
                    if (me._showCallback) {
                        me._showCallback();
                    }
                });
        },

        _hideArrow: function () {
            me._$arrow
                .removeClass(C.J_visibled)
                .hide();
        },

        _initMessagePopup: function () {
            me._$ = $("<div id='bcgogo-messagePopup-" + G.generateUUID() + "'></div>");
            me._$
                .append($(T.messagePopup))
                .css("position", "absolute");
            $(window.top.document.body).append(me._$);
            // bind events
            $("." + C.closeButton, me._$).bind("click", function (event) {
                me._hideMessagePopup();
                me._hideArrow();
            });
            me._hideMessagePopup();

            $("." + C.content, me._$).hide();
        },

        _showMessagePopup: function () {
            var offset = me._$hook.offset();
            me._$.css({
                "left": offset.left//再次调整
            });
            me._$
                .addClass(C.J_visibled)
                .show();
        },

        _hideMessagePopup: function () {
            me._$
                .removeClass(C.J_visibled)
                .hide();
        },

        _initBubble: function () {
            me._$bubble = $(T.bubble);
            me._$bubble.attr("id", "bcgogo-bubble-" + G.generateUUID());
            me._$bubble.addClass(C.J_visibled);
            $(window.top.document.body).append(me._$bubble);
        },

        _initMessage: function () {
            me._$message = $(T.message);
            me._$message.attr("id", "bcgogo-message-" + G.generateUUID());
            me._$message.addClass(C.J_visibled);
            $(window.document.body).append(me._$message);
        },

        // polling to check whether have new system messages
        _startPolling: function () {
            if (me._timerPolling > 0) {
                return;
            }


            setTimeout(me.inquire, me._interval);
//            me._timerPolling = setInterval(function (me) {
//                return function (me) {
//                    ;
//                };
//            }(me), me._interval);
        }

        // end
    };
    me = App.Module.messagePopup;
})();