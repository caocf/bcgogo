/**
 * 引导面板
 * @author zhen.pan
 */
(function () {
    App.namespace("Module.GuideTipPanel");

    // constructor
    var GuideTipPanel = function () {
        this._config = null;
        this._$inst = null;
        this.TEMPLATE = "" +
            "<div class='bcgogo_guideTipPanel' id='id_bcgogo_guideTipPanel_" + G.generateUUID() + "'>" +
            "    <img class='bcgogo_guideTipPanel_background'/>" +
            "    <div class='bcgogo_guideTipPanel_content'></div>" +
            "</div>";
    };

    GuideTipPanel.prototype.IMG = {
        L_B_big_blue:"js/components/themes/res/guide/L_B_big_blue.png",
        L_T_big_blue:"js/components/themes/res/guide/L_T_big_blue.png",
        R_B_big_blue:"js/components/themes/res/guide/R_B_big_blue.png",
        R_T_big_blue:"js/components/themes/res/guide/R_T_big_blue.png",

        L_B_small_blue:"js/components/themes/res/guide/L_B_small_blue.png",
        L_T_small_blue:"js/components/themes/res/guide/L_T_small_blue.png",
        R_B_small_blue:"js/components/themes/res/guide/R_B_small_blue.png",
        R_T_small_blue:"js/components/themes/res/guide/R_T_small_blue.png",

        L_B_big_green:"js/components/themes/res/guide/L_B_big_green.png",
        L_T_big_green:"js/components/themes/res/guide/L_T_big_green.png",
        R_B_big_green:"js/components/themes/res/guide/R_B_big_green.png",
        R_T_big_green:"js/components/themes/res/guide/R_T_big_green.png",

        L_B_small_green:"js/components/themes/res/guide/L_B_small_green.png",
        L_T_small_green:"js/components/themes/res/guide/L_T_small_green.png",
        R_B_small_green:"js/components/themes/res/guide/R_B_small_green.png",
        R_T_small_green:"js/components/themes/res/guide/R_T_small_green.png"
    };

    GuideTipPanel.prototype.DEFAULT_CONTENT_LEFT = 35;
    GuideTipPanel.prototype.DEFAULT_CONTENT_TOP = 40;

    GuideTipPanel.prototype.Z_INDEX = {
        NORMAL:902,
        TOP:2002
    };

    /**
     *
     * @param config
     * {
     *     left:0,
     *     top:0,
     *     backgroundImageUrl:"",
     *     content:{
     *         htmlText:"",
     *         // default 50
     *         top:0,
     *         // default 50
     *         left:0
     *     }
     * }
     */
    GuideTipPanel.prototype.show = function (config) {
        var This = this;
        This._config = config;

        This._$inst = $(This.TEMPLATE);

        var img = This._$inst.find("img")[0],
            width = 0,
            height = 0;

        This._$inst
            .css("z-index",config["z-index"] || This.Z_INDEX.NORMAL)
            .css("top", config.top || 0)
            .css("left", config.left || 0);
        $(document.body).append(This._$inst);
        img.onload = function () {
            width = img.width;
            height = img.height;

            // init panel
            This._$inst.css({
                width:img.width,
                height:img.height
            });

            // init content
            if (config.content) {
                var content = {
                    left:config.content.left || This.DEFAULT_CONTENT_LEFT,
                    top:config.content.top || This.DEFAULT_CONTENT_TOP
                }
                This._$inst.find(".bcgogo_guideTipPanel_content")
                    .css({
                        left:content.left,
                        top:content.top,
                        width:width - content.left * 2,
                        height:height - content.top * 2
                    })
                    .html(config.content.htmlText);
            }

        };

        img.src = config.backgroundImageUrl;
    };

    /**
     * 移除视图， 清除对象
     */
    GuideTipPanel.prototype.remove = function () {
        if (!this._$inst) {
            return;
        }
        this._$inst.remove();
        this._config = null;
        this._$inst = null;
    };

    GuideTipPanel.prototype.getUIProperty = function () {
        var prop = this._$inst.offset();
        prop.width = this._$inst.width();
        prop.height = this._$inst.height();
        return prop;
    };

    GuideTipPanel.prototype.getJqdom = function() {
        return $(this._$inst);
    };

    App.Module.GuideTipPanel = GuideTipPanel;
})();


/**
 * ok 按钮
 * @author zhen.pan
 */
(function () {
    App.namespace("Module.GuideTipOkButton");

    // constructor
    var GuideTipOkButton = function () {
        this._config = null;
        this._$inst = null;
        this.TEMPLATE = "" +
            "<div class='bcgogo_guideTipOkButton' id='id_bcgogo_guideTipOkButton_" + G.generateUUID() + "'>" +
            "    <p></p>" +
            "</div>"
    };

    GuideTipOkButton.prototype.DEFAULT = {
        WIDTH:100,
        HEIGHT:25,
        COLOR:"#842800",
        BORDER_STYLE:"none;"
    };

    GuideTipOkButton.prototype.Z_INDEX = {
        NORMAL:903,
        TOP:2003
    };

    /**
     *
     * @param config
     * {
     *     label:"",
     *     color:"",
     *     width:0,
     *     height:0,
     *     left:0,
     *     top:0,
     *     click:function() {}
     * }
     */
    GuideTipOkButton.prototype.show = function (config) {
        var This = this;

        This._config = config;
        This._$inst = $(This.TEMPLATE);

        var buttonCss = {
            width:config.width || this.DEFAULT.WIDTH,
            height:config.height || this.DEFAULT.HEIGHT,
            color:config.color || this.DEFAULT.COLOR,
            "border-radius":"3px",
            cursor:"pointer",
            left:config.left || 0,
            top:config.top || 0,
            "z-index":config["z-index"] || This.Z_INDEX.NORMAL
        };
        This._$inst
            .css(buttonCss)
            .bind("click", config.click)
            .find("p")
            .text(config.label || "按钮")
            .css({
                "line-height":buttonCss.height + "px",
                "padding":0,
                "margin":0
            });

        $(document.body).append(This._$inst);
    };

    GuideTipOkButton.prototype.lockButton = function () {
        var This = this;
        if(This._$inst) {
            This._$inst.unbind("click", This.config.click);
        }
    };

    GuideTipOkButton.prototype.unlockButton = function () {
        var This = this;
        if(This._$inst) {
            This._$inst.bind("click", This.config.click);
        }
    };

    GuideTipOkButton.prototype.remove = function () {
        if (!this._$inst) {
            return;
        }

        this._$inst.remove();
        this._config = null;
        this._$inst = null;
    };

    GuideTipOkButton.prototype.getUIProperty = function () {
        var prop = this._$inst.offset();
        prop.width = this._$inst.width();
        prop.height = this._$inst.height();
        return prop;
    };

    GuideTipOkButton.prototype.getJqdom = function() {
        return $(this._$inst);
    };

    App.Module.GuideTipOkButton = GuideTipOkButton;
})();


/**
 * cancel 按钮
 * @author zhen.pan
 * */
(function () {
    App.namespace("Module.GuideTipCancelButton");

    // constructor
    var GuideTipCancelButton = function () {
        this._config = null;
        this._$inst = null;
        this.TEMPLATE = "" +
            "<div class='bcgogo_guideTipCancelButton' id='id_bcgogo_guideTipCancelButton_" + G.generateUUID() + "'>" +
            "    <p></p>" +
            "</div>";
    };

    GuideTipCancelButton.prototype.DEFAULT = {
        COLOR:"#3f3f3f"
    };

    GuideTipCancelButton.prototype.Z_INDEX = {
        NORMAL:903,
        TOP:2003
    };

    /**
     *
     * @param config
     * {
     *     label:"",
     *     left:0,
     *     top:0,
     *     click:function(){}
     * }
     */
    GuideTipCancelButton.prototype.show = function (config) {
        var This = this;
        This._config = config;
        This._$inst = $(This.TEMPLATE);

        This._$inst
            .css({
                cursor:"pointer",
                left:config.left || 0,
                top:config.top || 0,
                "z-index":config["z-index"] || This.Z_INDEX.NORMAL
            })
            .bind("click", config.click)
            .find("p")
            .text(config.label || "按钮")
            .css("color", config.color || This.DEFAULT.COLOR);
        this.click = config.click;

        $(document.body).append(This._$inst);
    };

    GuideTipCancelButton.prototype.lockButton = function () {
        var This = this;
        if(This._$inst) {
            This._$inst.unbind("click", This.config.click);
        }
    };

    GuideTipCancelButton.prototype.unlockButton = function () {
        var This = this;
        if(This._$inst) {
            This._$inst.bind("click", This.config.click);
        }
    };

    GuideTipCancelButton.prototype.remove = function () {
        if (!this._$inst) {
            return;
        }

        this._$inst.remove();
        this._config = null;
        this._$inst = null;
    };

    GuideTipCancelButton.prototype.getUIProperty = function () {
        var prop = this._$inst.offset();
        prop.width = this._$inst.width();
        prop.height = this._$inst.height();
        return prop;
    };

    GuideTipCancelButton.prototype.getJqdom = function() {
        return $(this._$inst);
    };

    App.Module.GuideTipCancelButton = GuideTipCancelButton;
})();

/**
 * GuideTipStartPanel
 * @author zhen.pan
 */
(function () {
    App.namespace("Module.GuideTipStartPanel");

    var GuideTipStartPanel = function () {
        this._config = null;
        this._$inst = null;
        this.TEMPLATE = "" +
            "<div class='bcgogo_guideTipStartPanel' id='id_bcgogo_guideTipStartPanel_" + G.generateUUID() + "'>" +
            "    <div class='content'></div>" +
            "    <div class='buttonGroup'>" +
            "         <div class='button_associated'></div>" +
            "         <div class='button_drop'></div>" +
            "         <div class='checkBox_notRemind'><input type='checkbox'>以后不再提醒 </div>" +
            "    </div>" +
            "</div>";
    };

    GuideTipStartPanel.prototype.Z_INDEX = {
        NORMAL:902,
        TOP:2002
    };

    /**
     *
     * @param config
     * {
     *     left:0,
     *     top:0,
     *     htmlText:"",
     *     autoLock:true,
     *     drop:{
     *         label:"",
     *         click:function(){}
     *     },
     *     associated:{
     *         label:"",
     *         click:function(){}
     *     }
     * }
     */
    GuideTipStartPanel.prototype.show = function (config) {
        var This = this;
        This._config = config;
        This._$inst = $(This.TEMPLATE);

        This._$inst
            .css({
                left:config.left,
                top:config.top,
                "z-index":config["z-index"] || This.Z_INDEX.NORMAL
            })
            .find(".content")
            .html(config.htmlText || "请填上文本");

        // associated button
        if(config.associated) {
            This._$inst.find(".button_associated")
                .text(config.associated.label || "关联")
                .bind("click", config.associated.click);
        } else if(config.ok) {
            This._$inst.find(".button_associated")
                .text(config.ok.label || "关联")
                .bind("click", config.ok.click);
        } else {
            This._$inst.find(".button_drop").css({
                "float":"none",
                "margin-left":"auto",
                "margin-right":"auto"
            });
            This._$inst.find(".button_associated").hide();
        }

        // drop button
        if(config.drop) {
            This._$inst.find(".button_drop")
                .text(config.drop.label || "跳出引导")
                .bind("click", config.drop.click);
        } else {
            This._$inst.find(".button_associated").css({
                "float":"left",
                "margin-left":135
            });
            This._$inst.find(".button_drop").hide();
        }

        $(document.body).append(This._$inst);
    };

    GuideTipStartPanel.prototype.lockButton = function () {
        var This = this;
        if(This._$inst) {
            if(This._config.drop) {
                This._$inst.find(".button_drop").unbind("click", This.config.drop.click);
            }
            if(This._config.associated) {
                This._$inst.find(".button_associated").unbind("click", This.config.associated.click);
            }
            if(This._config.ok) {
                This._$inst.find(".button_associated").unbind("click", This.config.ok.click);
            }
        }
    };

    GuideTipStartPanel.prototype.unlockButton = function () {
        var This = this;
        if(This._$inst) {
            if(This._config.drop) {
                This._$inst.find(".button_drop").bind("click", This.config.drop.click);
            }
            if(This._config.associated) {
                This._$inst.find(".button_associated").bind("click", This.config.associated.click);
            }
            if(This._config.ok) {
                This._$inst.find(".button_associated").bind("click", This.config.ok.click);
            }
        }
    };

    GuideTipStartPanel.prototype.remove = function () {
        if (!this._$inst) {
            return;
        }

        this._$inst.remove();
        this._config = null;
        this._$inst = null;
    };

    GuideTipStartPanel.prototype.getUIProperty = function () {
        var prop = this._$inst.offset();
        prop.width = this._$inst.width();
        prop.height = this._$inst.height();
        return prop;
    };

    GuideTipStartPanel.prototype.getJqdom = function() {
        return $(this._$inst);
    };

    App.Module.GuideTipStartPanel = GuideTipStartPanel;
})();


/**
 * GuideTipTryPanel 继承自 GudieTipStartPanel
 * @method show(config)
 *      @param config
 *      {
 *          left:0,
 *          top:0,
 *          htmlText:"",
 *          autoLock:true,
 *          // 可不设置此参数
 *          drop:{
 *              label:"",
 *              click:function(){}
 *          },
 *
 *          // 可不设置此参数
 *          ok:{
 *              label:"",
 *              click:function(){}
 *          }
 * }
 *
 * @method remove()
 *
 * @method getUIProperty()
 *
 * @method getJqdom()
 */
(function(){
    var GuideTipTryPanel = function() {};

    // GuideTipTryPanel 继承自  GuideTipStartPanel
    GuideTipTryPanel.prototype = new App.Module.GuideTipStartPanel();
    GuideTipTryPanel.prototype.constructor = GuideTipTryPanel;

    App.Module.GuideTipTryPanel = GuideTipTryPanel;

})();


// GuideTip 的 Display 层管理层
(function () {
    App.namespace("Module.guideTipDisplayManager");

    var stack = [];

    var addChild = function (child) {
        stack.push(child);
    };

    var removeAllFromPage = function () {
        for (var i = 0, len = stack.length; i < len; i++) {
            stack[i].remove();
        }
        stack = [];
    };

    var getChildren = function () {
        return stack;
    };

    App.Module.guideTipDisplayManager = {
        /**
         * 添加子对象, 子对象必须实现 接口  remove()
         *
         */
        addChild:addChild,
        /**
         * 将子对象从屏幕上移除, 将调用管理类内通过 addChild() 方法添加的所有子对象的 remove() 方法
         * 并在调用 remove() 的过程结束后 ，从 管理类中移除 所有子对象的引用
         */
        removeAllFromPage:removeAllFromPage,
        /**
         * 获取应push 进 manager 的所有 child
         *     如果你要获取这些 子对象的 dom ， 你可以调用 子对象.getJqdom() 来获取它的 jquery dom 对象
         * @return {Array}
         */
        getChildren:getChildren
    };
})();