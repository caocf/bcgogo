// Module shadow
/**
 * @author zhen.pan
 * @description 这是个遮罩组件
 */
(function () {
    App.namespace("Module.shadow");

    var T = {
        shadow:"<div class='bcgogo-shadow'></div>",
        border:"<div class='bcgogo-shadow-border'></div>"
    };

    var mask = {
        $top:$(T.shadow).attr("id", "id-shadow-top"),
        $right:$(T.shadow).attr("id", "id-shadow-right"),
        $bottom:$(T.shadow).attr("id", "id-shadow-bottom"),
        $left:$(T.shadow).attr("id", "id-shadow-left"),
        $single:$(T.shadow).attr("id", "id-shadow-single")
    };

    var Z_INDEX = {
        TOP:2000,
        NORMAL:900
    };

    var borderManager = {
        stack:[],

        // {x,  y,  w,  h,  color}
        create:function (borderConfig) {
            var $border = $(T.border).attr("id", "id-shadow-border" + G.generateUUID());

            var tag = ["top", "bottom", "left", "right"];
            var border = {};
            for (var i = 0, len = tag.length; i < len; i++) {
                border["$" + tag[i]] = $(T.border).attr("id", "id-shadow-border-" + tag[i] + G.generateUUID());
            }

            border.$top.css({
                position:"absolute",
                left:borderConfig.x,
                top:borderConfig.y,
                width:borderConfig.w,
                height:borderConfig.borderWidth,
                background:borderConfig.borderColor,
                "z-index":borderConfig["z-index"]
            });
            border.$bottom.css({
                position:"absolute",
                left:borderConfig.x,
                top:borderConfig.y + borderConfig.h,
                width:borderConfig.w + borderConfig.borderWidth,
                height:borderConfig.borderWidth,
                background:borderConfig.borderColor,
                "z-index":borderConfig["z-index"]
            });
            border.$left.css({
                position:"absolute",
                left:borderConfig.x,
                top:borderConfig.y,
                width:borderConfig.borderWidth,
                height:borderConfig.h,
                background:borderConfig.borderColor,
                "z-index":borderConfig["z-index"]
            });
            border.$right.css({
                position:"absolute",
                left:borderConfig.x + borderConfig.w,
                top:borderConfig.y,
                width:borderConfig.borderWidth,
                height:borderConfig.h,
                background:borderConfig.borderColor,
                "z-index":borderConfig["z-index"]
            });

            for (var itemKey in border) {
                document.body.appendChild(border[itemKey][0]);
                this.stack.push(border[itemKey]);
            }

            return border;
        },

        clearAll:function () {
            for (var i = 0, len = this.stack.length; i < len; i++) {
                this.stack[i].remove();
            }
            this.stack = [];
        }
    };

    var checkTimerId = 0;
    var init = function () {
        $(document.body).append(mask.$top, mask.$right, mask.$bottom, mask.$left, mask.$single);
        for (var k in mask) {
            mask[k].hide();
        }

        // binding resize event handler
        $(window)
            .unbind("resize", updateShadowSize)
            .bind("resize", updateShadowSize);

        clearInterval(checkTimerId);
        checkTimerId = setInterval(function(){
            updateShadowSize();
        }, 800);
    };

    var isContain = function(selector) {
        return $(selector).length > 0;
    };

    var clearBackgroundStyles = function (instanceList) {
        var list = instanceList ? instanceList : mask;
            loopRule = G.isArray(list) ? "for(var k=0,len=list.length;k<len;k++)" : "for(var k in list)",
            loopContent = "{list[k][0].style = '';}"

        eval(loopRule + loopContent);
    };
    var getBackgroundCss = function (config) {
        if(config.isFullTransparent) {
            return {
                "background-color":"#e5e5e5",
                "opacity":0,
                "filter": "alpha(opacity=0)"
            };
        } else if(config.background) {
            return {
                "background-color":config.background.color,
                "opacity":config.background.opacity,
                "filter": "alpha(opacity=" + config.background.opacity + ")"
            };
        } else {
            return {
                "background-color":"#e5e5e5",
                "opacity":0.3,
                "filter": "alpha(opacity=0.3)"
            };
        }
    };

    var updateShadowSize = function() {
        var c = coverExceptConfig,
            $d = $(document);
        if(c) {
            mask.$top.css({
                left:0,
                top:0,
                width:$d.width(),
                height:c.y
            });
            mask.$bottom.css({
                left:0,
                top:c.y + c.h,
                width:$d.width(),
                height:$d.height() - (c.y + c.h)
            });
            mask.$left.css({
                left:0,
                top:c.y,
                width:c.x,
                height:c.h
            });
            mask.$right.css({
                left:c.x + c.w,
                top:c.y,
                width:$d.width() - (c.x + c.w),
                height:c.h
            });
        } else {
            mask.$single.css({
                width:$d.width(),
                height:$d.height()
            });
        }
    };

    /**
     * 遮罩部分页面
     * @param config {
     *          x:0,
     *          y:0,
     *          w:0,
     *          h:0,
     *
     *          // 可不设
     *          isFullTransparent:false
     *          // 可不设
     *          hasBorder:false,
     *          // 可不设， 但要生效， hasBorder 必须为 true
     *          isBorderFlicker:false,
     *
     *          // 可不设
     *          background:{
     *              color:"#e5e5e5",
     *              opacity:0.3
     *          },
     *
     *          // 可不设
     *          z-index:Z_INDEX.NORMAL,
     *
     *          // 可不设，但要生效， hasBorder 必须为 true
     *          border:{
     *              color:"red",
     *              width:3
     *          }
     *        }
     * */
    var cover = function (config) {
        if(!isContain("div[id^='id-shadow-']")) {
            init();
        }

        clearBackgroundStyles([mask.$single]);
        borderManager.clearAll();
        mask.$single
            .css({
                left:config.x,
                top:config.y,
                width:config.w,
                height:config.h,
                "z-index":config["z-index"] || Z_INDEX.NORMAL
            })
            .css(getBackgroundCss(config))
            .show()

        setBorder(config);
    };

    /**
     * 遮罩整个页面
     * @param config {
     *          // 可不设
     *          isFullTransparent:false
     *          // 可不设
     *          background:{
     *              color:"#e5e5e5",
     *              opacity:0.3
     *          }
     *          // 可不设
     *          z-index:Z_INDEX.NORMAL
     *      }
     */
    var coverAll = function ( config ) {
        var $doc = $(document),
            param = {x:0, y:0, w:$doc.width(), h:$doc.height()};

        if(config) {
            if( config.hasOwnProperty("isFullTransparent")) {
                param.isFullTransparent = config.isFullTransparent;
            }
            if( config.hasOwnProperty("background") ) {
                param.background = config.background;
            }
            if( config.hasOwnProperty("z-index")) {
                param["z-index"] = config["z-index"] || Z_INDEX.NORMAL
            }
        }
        cover( param);
    };

    var coverExceptConfig = null;
    /**
     * 除了部分页面， 其余全遮罩
     * @param config {
     *          x:0,
     *          y:0,
     *          w:0,
     *          h:0,
     *
     *          // 可不设
     *          isFullTransparent:false
     *          // 可不设
     *          hasBorder:false,
     *          // 可不设， 但要生效， hasBorder 必须为 true
     *          isBorderFlicker:false,
     *
     *          // 可不设
     *          background:{
     *              color:"#e5e5e5",
     *              opacity:0.3
     *          },
     *
     *          // 可不设，但要生效， hasBorder 必须为 true
     *          border:{
     *              color:"red",
     *              width:3
     *          },
     *
     *          // 可不设
     *          z-index:Z_INDEX.NORMAL
     *
     *        }
     * */
    var coverExcept = function (config) {
        if(!isContain("div[id^='id-shadow-']")) {
            init();
        }

        coverExceptConfig = config;

        var docW = $(document).width(),
            docH = $(document).height(),
            coverInstanceList = [
                mask.$top, mask.$bottom,
                mask.$left, mask.$right
            ];
        clearBackgroundStyles(coverInstanceList);
        borderManager.clearAll();
        mask.$top.css({
            left:0,
            top:0,
            width:docW,
            height:config.y,
            "z-index":config["z-index"] || Z_INDEX.NORMAL
        });
        mask.$bottom.css({
            left:0,
            top:config.y + config.h,
            width:docW,
            height:docH - (config.y + config.h),
            "z-index":config["z-index"] || Z_INDEX.NORMAL
        });
        mask.$left.css({
            left:0,
            top:config.y,
            width:config.x,
            height:config.h,
            "z-index":config["z-index"] || Z_INDEX.NORMAL
        });
        mask.$right.css({
            left:config.x + config.w,
            top:config.y,
            width:docW - (config.x + config.w),
            height:config.h,
            "z-index":config["z-index"] || Z_INDEX.NORMAL
        });

        for (var k = 0, len = coverInstanceList.length; k < len; k++) {
            coverInstanceList[k].css(getBackgroundCss(config)).show();
        }

        setBorder(config);
    };

    var setBorder = function(config) {
        if (config.hasBorder) {
            var borderConfig = {
                x:config.x,
                y:config.y,
                w:config.w,
                h:config.h
            };

            if(config.border) {
                borderConfig.borderColor = config.border.color;
                borderConfig.borderWidth = config.border.width;
            }else {
                borderConfig.borderColor = "red";
                borderConfig.borderWidth = 1;
            }
            borderConfig["z-index"] = (config["z-index"] || Z_INDEX.NORMAL) + 1;

            borderManager.create(borderConfig);

            if(config.isBorderFlicker) {
                flicker.start({
                    interval:500,
                    toggleCallbackList:[
                        function () {
                            for (var i = 0, len = borderManager.stack.length; i < len; i++) {
                                borderManager.stack[i].show();
                            }
                        },
                        function () {
                            for (var i = 0, len = borderManager.stack.length; i < len; i++) {
                                borderManager.stack[i].hide();
                            }
                        }
                    ]
                });
            }

        }
    };

    var flicker = {
        _timerId:0,
        _config:null,
        _count:0,
        /**
         *
         * @param config
         * {
         *     interval:0,
         *     toggleCallbackList:[
         *         function1, function2
         *     ]
         * }
         */
        start:function(config) {
            this._config = config;
            this._timerId = setInterval(function(){
                (config.toggleCallbackList[flicker._count])();
                if(++flicker._count >= config.toggleCallbackList.length) {
                    flicker._count = 0;
                }
            }, config.interval)
        },
        stop:function(){
            clearInterval(this._timerId);
            this._count = 0;
            this._config = null;
        }
    };

    /**
     * 清除所有本对象操控的遮罩
     */
    var clear = function () {
        $(window).unbind("resize", updateShadowSize);
        clearInterval(checkTimerId);

        for (var k in mask) {
            mask[k].remove();
        }

        flicker.stop();
        borderManager.clearAll();
    };

    App.Module.shadow = {
        cover:cover,
        coverExcept:coverExcept,
        coverAll:coverAll,
        clear:clear,
        Z_INDEX:Z_INDEX
    };

})();
