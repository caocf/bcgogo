;
(function () {
    APP_BCGOGO.namespace("Module.droplist");

    var self,

    // Class Names
        C = {
            droplist: "ui-bcgogo-droplist",
            droplistContainer: "ui-bcgogo-droplist-container",
            highlight: "ui-bcgogo-droplist-highlighted",
            option: "ui-bcgogo-droplist-option",
            category: "ui-bcgogo-droplist-category",
            buttonBar: "ui-bcgogo-droplist-buttonBar",
            button: "ui-bcgogo-droplist-button",
            optionStatictext: "ui-bcgogo-droplist-option-staticText",
            optionEditabletext: "ui-bcgogo-droplist-option-text"
        },

    // Templates
        T = {
            confirmBar: "<div class='ui-bcgogo-droplist-delete-confirmBar'>"
                + "    <div class='J-button-ok'></div>"
                + "    <div class='J-button-cancel'></div>"
                + "</div>",
            buttonBar: "<div class='" + C.buttonBar + "'></div>",
            droplist: "<ul class='" + C.droplist + "'></ul>",
            editableText: "<input type='text' class='" + C.optionEditabletext + "' />",
            staticText: "<span class='" + C.optionStatictext + "'></span>"
        };

    /**
     * @description droplist
     * @author 潘震
     * @data 2012-10-16
     */
    APP_BCGOGO.Module.droplist = {
        _$root:null,
        _$target: null,
        _$relNode: null,
        _storageValue: null,
        _data: [],
        _params: null,
        _uuid: "",
        // editing, idle
        _state: "idle",
        MIN_WIDTH: 150,
        DEFAULT_HEIGHT: 250,
        isIgnoreMinWidth:false,
        isEditable: false,
        isDeletable: true,
        isNoticeWhenSave: true,
        isNoticeWhenDelete: true,
        isSupportKeyboard:true,

        // TODO this properties not used, but will be use
        maxDataLength:30,

        autoSet: true,
        saveWarning: "",
        deleteWarning: "",
        isPreInputIsCtrAction: true,

        setUUID: function (value) {
            self._uuid = value;
        },

        getUUID: function () {
            return self._uuid;
        },

        getTarget: function () {
            return self._$target ? self._$target[0] : null;
        },

        getFollowNode: function () {
            return self._$relNode ? self._$relNode[0] : null;
        },

        /**
         * @description init mouse and keyboard events
         * @param self
         */
        _initEvents: function () {
            self._removeOptionEventListener();
            self._addOptionEventListener();
        },

        _highlightOption: function ($option) {
            $("." + C.option, self._$target)
                .not($option)
                .each(function () {
                    $(this)
                        .removeClass(C.highlight)
                        .find("." + C.optionStatictext).removeClass(C.highlight);
                });

            $option
                .addClass(C.highlight)
                .find("." + C.optionStatictext).addClass(C.highlight);
        },

        _greyOption: function ($option) {
            $option
                .removeClass(C.highlight)
                .find("." + C.optionStatictext).removeClass(C.highlight);
        },

        _addOptionEventListener: function () {
            $("." + C.option, self._$target)
                .hover(function () {
                    if (self._state === "editing")
                        return;
                    self._highlightOption($(this));
                }, function () {
                    self._greyOption($(this));
                })
                .bind("click", self._clickBefore);

            if (self.isEditable) {
                $("." + C.option, self._$target)
                    .bind("mouseenter", self._onOptionMouseEnter)
                    .bind("mouseleave", self._onOptionMouseLeave);
            }
        },

        _removeOptionEventListener: function () {
            $("." + C.option, self._$target).unbind("mouseenter mouseleave click");
        },

        _onOptionMouseEnter: function (event) {
            var $option = $(event.currentTarget).closest("." + C.option),
                index = self._getOptionIndex($option);
            if (self._state === "editing" || self._data[index]["isEditable"] === false)
                return;

            $("." + C.option, self._$target)
                .not($option)
                .each(function () {
                    self._removeButtonBar($(this));
                });

            self._createButtonBar($option, "idle");
        },

        _onOptionMouseLeave: function (event) {
            if (self._state === "editing")
                return;

            self._removeButtonBar($(event.currentTarget));
        },

        _removeButtonBar: function ($option) {
            $option.find("." + C.buttonBar).remove();

            self._changeStaticTextWidth($option, false);
            $.fn.tipsy.revalidate();
        },

        _createButtonBar: function ($option, state) {
            $option.find("." + C.buttonBar).remove();
            $option.append(self._getButton$BarByState(state));

            self._changeStaticTextWidth($option, true);
        },

        _changeStaticTextWidth: function ($option, isShowButton) {
            if (isShowButton) {
                var btWidth = btWidth = $("." + C.buttonBar, $option).width();
                $option.find("." + C.optionStatictext).css("width", $option.width() - btWidth - 8 + "px");
            } else {
                $option.find("." + C.optionStatictext).css("width", $option.width() + "px");
            }
        },

        _getButton$NodeByName: function (name) {
            var $div = $("<div class='" + C.button + " ui-bcgogo-droplist-" + name + "Button-normal'></div>"),
                warning = "";
            $div
                .bind("mousedown", function (event) {
                    self._changeButtonStyleByEvent(event, "press");
                })
                .bind("mouseout mouseup", function (event) {
                    self._changeButtonStyleByEvent(event, "unpress");
                })
                .bind("click", self["_on" + name.charAt(0).toUpperCase() + name.slice(1, name.length) + "Before"]);


            if (name === "save") {
                warning = self.saveWarning;
            } else if (name === "delete") {
                warning = self.deleteWarning;
            }

            if (warning !== "") {
                $div.attr("title", "<h3 style=\"font-family:'微软雅黑', 'Arial';\">" + warning + "</h3>");
                $div.tipsy({delay: 0, gravity: "s", html: true});
            }

            return $div;
        },

        _getButton$BarByState: function (state) {
            var $bar = $(T.buttonBar);

            if (state === "editing") {
                $bar.append(
                    self._getButton$NodeByName("save"),
                    self._getButton$NodeByName("resume")
                );
            } else if (state === "idle") {
                // 添加了 delete 按钮
                if (self.isDeletable) {
                    $bar.append(self._getButton$NodeByName("delete"));
                }
                $bar.append(self._getButton$NodeByName("edit"));
            }
            return $bar;
        },

        /**
         * @description init component
         * @param p
         */
        init: function () {
            // create hook
            if (!self._$target || !self._$target[0]) {
                self._$target = $("<div id='bcgogo-droplist-" + G.generateUUID() + "' class='" + C.droplistContainer + "'></div>");
                self._$root = $(window.document.body);
                self._$target.appendTo(self._$root);
            }
        },

        /**
         *
         * @param p
         * {
         *     isIgnoreMinWidth {Boolean}:false,
         *     isEditable {Boolean}:false,
         *     isDeletable {Boolean}:true,
         *     isSupportKeyboard {Boolean}:true,
         *     autoSet {Boolean}:true,
         *     isNoticeWhenSave {Boolean}:true,
         *     isNoticeWhenDelete {Boolean}:true,
         *     saveWarning {String}:"",
         *     deleteWarning {String}:"",
         *     onSelect {function}:defaultFunction,
         *     onSave {function}:defaultFunction,
         *     onEdit {function}:defaultFunction,
         *     onDelete {function}:defaultFunction,
         *     onKeyboardSelect {function}:defaultFunction,
         *     originalValue {String}:null,
         *     data {Object}: need,
         *     selector {JqueryObject}: need,
         *     moveOnly {Boolean}: false,
         *     // 非必须，此参数用在自定义数据时，此时需要注意，autoSet需设为false，并且同时需要自己处理 onKeyboardSelect 函数
         *     onGetInputtingData:function()
         *
         * }
         */
        show: function (p) {
            if(!p
                || !p.data
                || p.data.uuid !== self._uuid) return;

            self._params = p;
            self.isIgnoreMinWidth = p.hasOwnProperty("isIgnoreMinWidth") ? p["isIgnoreMinWidth"] : false;
            self.isEditable = p.hasOwnProperty("isEditable") ? p["isEditable"] : false;
            self.isDeletable = p.hasOwnProperty("isDeletable") ? p["isDeletable"] : true;
            self.isSupportKeyboard = p.hasOwnProperty("isSupportKeyboard") ? p["isSupportKeyboard"] : true;
            self.autoSet = p.hasOwnProperty("autoSet") ? p["autoSet"] : true;
            self.isNoticeWhenSave = p["isNoticeWhenSave"] || true;
            self.isNoticeWhenDelete = p["isNoticeWhenDelete"] || true;
            self.saveWarning = p["saveWarning"] || "";
            self.deleteWarning = p["deleteWarning"] || "";
            self.onSelect = p["onSelect"] || self.onDefaultSelect;
            self.onSave = p["onSave"] || self.onDefaultSave;
            self.onEdit = p["onEdit"] || self.onDefaultEdit;
            self.onDelete = p["onDelete"] || self.onDefaultDelete;
            self.onKeyboardSelect = p["onKeyboardSelect"] || self.onDefaultKeyboardSelect;
            self._storageValue = p["originalValue"] || null;
            self.onGetInputtingData = p["onGetInputtingData"] || null;

            self._$target.remove();
            self._$root = $(p["root"] || window.document.body);
            self._$target.appendTo( self._$root );

            var s = T.droplist;
            self._$target.html("").html(s);
            self._$target.hide();

            self._$target.css("z-index", 2000);
            self.follow(p);
            self.hide();
            self._show();
            self.draw(p.data || {});
        },

        /**
         * @description draw view by data
         *              data format:
         *              [
         *                  {"type":"", "label":""},
         *                  {"type":"", "label":""},
         *                  ...
         *              ]
         * @param data
         */
        draw: function (data) {
            self._state = "idle";

//        GLOBAL.debug(data);
            if (!data.hasOwnProperty("uuid")
                || data["uuid"] != self._uuid
                || !data["data"]) {
                return;
            }

            self._data = data["data"];
            // 暂时解决 js假死的数据太多的问题 TODO 这个限制应该通过后台的数据推送来保证数据的数量不大于一个  给定值
            if(self._data.length > 50) {
                self._data = self._data.slice(0, 50);
            }
            self.clear();

            var s = "", type = "", label = "";
            for (var i = 0, len = self._data.length; i < len; i++) {
                type = self._data[i]["type"] === "category" ? "category" : "option";
                label = self._data[i]["label"];
                s += "<li class='ui-bcgogo-droplist-" + type + "'>"
                    + "  <span class='" + C.optionStatictext + "'>" + label + "</span>"
                    + "</li>";
            }

            $("ul", self._$target)
                .html(s)
                .find("." + C.optionStatictext).each(function () {
                    $(this).attr("title", $(this).text());
                    $(this).tooltip({"delay": 0, "track": false});
                });

            var height = self._params["height"] ? self._params["height"] + "px" : self.DEFAULT_HEIGHT + "px";
            $("." + C.droplist, self._$target).css("height", height);
            self._setWidth(self._$relNode.outerWidth(true));

            self._initEvents();
        },

        _changeButtonStyleByEvent: function (event, action) {
            var prefix = event.currentTarget.className.split(" ")[1].replace(/(-normal|-press){1}$/g, "");
            if (action === "press") {
                $(event.currentTarget).removeClass(prefix + "-normal").addClass(prefix + "-press");
            } else if (action === "unpress") {
                $(event.currentTarget).removeClass(prefix + "-press").addClass(prefix + "-normal")
            }
        },

        _visible: false,

        _show: function () {
            self._visible = true;
            $(self._$target).show(); // blind, fast
        },

        hide: function () {
            self._visible = false;
            $(self._$target).hide();
            $.fn.tooltip.clearTip();
        },

        follow: function (param) {
            var $node = $(param["selector"]);

            if (param["moveOnly"]) {
                self.clear();
            }

            $node.unbind("keydown", self._onHookKeyboardEvent);

            self._$relNode = $node;
            $node.addClass("J-bcgogo-droplist-on");

            if(self._$root[0].tagName.toUpperCase() === "BODY" ) {
                var oX = G.getX($node[0]),
                    oY = G.getY($node[0]),
                    mePosition = {
                        left: oX,
                        top: oY + self._$relNode.outerHeight() + 2
                    };
            }else {
                var oX = G.getX($node[0]) - G.getX(self._$root[0]),
                    oY = G.getY($node[0]) - G.getY(self._$root[0]),
                    mePosition = {
                        left: oX,
                        top: oY + self._$relNode.outerHeight() + 2
                    };
            }

            $(self._$target)
                .css("left", mePosition["left"] + "px")
                .css("top", mePosition["top"] + "px");

            $node.bind("keydown", self._onHookKeyboardEvent);
        },

        _onHookKeyboardEvent: function (event) {
            if( !self.isSupportKeyboard ) {
                return;
            }

            if (!self.isVisible() || self._state === "editing") {
                return;
            }

            var keyName = G.keyNameFromEvent(event);

            if (G.contains(keyName, ["up", "down"])) {
                var $optionArr = $(self._$target).find("li"),
                    $optionActived = $("." + C.highlight, self._$target),
                    index = $optionActived[0] ? $optionArr.index($optionActived) : -1,
                    hash = {};

                // init and update store-value , if necessary -- when index is -1
                if (!self._storageValue
                    || (index === -1 && self.isPreInputIsCtrAction === false) ) {

                    if(self.onGetInputtingData) {
                        self._storageValue = self.onGetInputtingData();
                    } else {
                        self._storageValue = {label: self._$relNode.val()};
                    }
                }

                // add value to hash
//                var optionDataArr=[];
//                $.each(self._data, function (index, val) {
//                    if("category"!=val["type"])
//                        optionDataArr.push(val);
//                });
                $.each(self._data, function (index, val) {
                    hash[index + ""] = val;
                });
                hash["-1"] = self._storageValue;

                // generate new index
                if (G.contains(keyName, ["up"])) {
                    index--;
                    if(G.isNotEmpty(self._data[index]) && "category"==self._data[index]["type"]){
                        index--;
                    }
                    index = index < -1 ? $optionArr.length - 1 : index;
                } else {
                    index++;
                    if(G.isNotEmpty(self._data[index]) && "category"==self._data[index]["type"]){
                        index++;
                    }
                    index = index > $optionArr.length - 1 ? -1 : index;
                }

                // set option styles
                $optionArr.each(function () {
                    self._greyOption($(this));
                    self._removeButtonBar($(this));
                });

                if (index !== -1) {
                    // add buttonBar to option
                    if (self.isEditable === true
                        && self._state !== "editing"
                        && self._data[index]["isEditable"] === true) {
                        self._createButtonBar($optionArr.eq(index), "idle");
                    }
                    self._highlightOption($optionArr.eq(index));
                    // auto scroll
                    self._scrollIntoView($optionArr.eq(index), $("ul", self._$target));
                }

                // set new value to View
                if (self.autoSet) {
                    event.currentTarget.value = hash[index + ""]["label"];
                }

                self.onKeyboardSelect(event, index, hash[index + ""], event.currentTarget);
            }

            self.isPreInputIsCtrAction = G.contains(keyName, ["up", "down", "left", "right", "enter", "backspace", "esc"]);
        },

        _hasScroll: function ($root) {
            return $root.height() < $root[ $.fn.prop ? "prop" : "attr" ]("scrollHeight");
        },

        _scrollIntoView: function ($item, $root) {
            if (this._hasScroll($root)) {
                var offset = $item.offset().top - $root.offset().top,
                    scroll = $root.scrollTop(),
                    elementHeight = $root.height();
                if (offset < 0) {
                    $root.scrollTop(scroll + offset);
                } else if (offset >= elementHeight) {
                    $root.scrollTop(scroll + offset - elementHeight + $item.height());
                }
            }
        },

        isVisible: function () {
            return self._visible || self._$target.is(":visible");
        },

        /**
         * @description clear view
         */
        clear: function () {
            $("ul > li", self._$target).mouseout();
            $("ul", self._$target).html("");

            // clear data

        },

        _setWidth: function (value) {
            var $droplist = $("." + C.droplist, self._$target);
            if( self.isIgnoreMinWidth ) {
                $droplist.css("width", value + "px");
            }else {
                $droplist.css("width", (value < self.MIN_WIDTH ? self.MIN_WIDTH : value) + "px");
            }
        },


        _showDeleteConfirmBar: function (self, $option, callback) {
            var s = T.confirmBar,
                $bar = $(s),
                $ok = $(".J-button-ok", $bar).button({label: "删除"}).css("float", "left"),
                $cancel = $(".J-button-cancel", $bar).button({label: "取消"}).css("float", "left"),
                index = self._getOptionIndex($option),
                deleteData = self._data[index];

            self._state = "editing";

            $ok.bind("click", function (event) {
                event.stopPropagation();
                self._data.splice(index, 1);
                $("." + C.highlight, self._$target).mouseout();
                $option.remove();
                callback(event, index, deleteData);
                self._state = "idle";
            });

            $cancel.bind("click", function (event) {
                event.stopPropagation();
                self._clearDeleteConfirmBar($bar);
                $option.find("." + C.optionStatictext).show();
                $option.find("." + C.buttonBar).show();
                self._state = "idle";
            });

            $option.find("." + C.optionStatictext).hide();
            $option.find("." + C.buttonBar).hide();
            $option.append($bar);
        },

        _clearDeleteConfirmBar: function ($element) {
            $element.remove();
        },

        _getOptionIndex: function ($option) {
            return $(self._$target).find("li").index($option);
        },

        _clickBefore: function (event) {
            var $option = $(event.currentTarget), $target = $(event.target);
            if (self._state === "editing")
                return;

            if ($target.not("." + C.button)[0]) {
                var index = self._getOptionIndex($option);
                self.onSelect(event, index, self._data[index], self._$relNode[0]);
            }
        },

        _onEditBefore: function (event) {
            var $option = $(event.currentTarget).closest("." + C.option),
                text = {value: $option.find("." + C.optionStatictext).text()},
                $editText = $(T.editableText),
                index = self._getOptionIndex($option);

            self._createButtonBar($option, "editing");

            $editText
                .css("width", $option.width() - $option.find("." + C.buttonBar).width() - 8 + "px")
                .val(G.Lang.normalize(text.value))
                .bind("keyup", function (event) {
                    if (G.isKeyName(event, "enter")) {
                        $(".ui-bcgogo-droplist-saveButton-normal", $option).click();
                    }
                });

            $option.find("." + C.optionStatictext).remove();

            // store temp text value
            $option
                .attr("lastvalue", text.value)
                .append($editText);

            self._state = "editing";

            self.onEdit(event, index, self._data[index], self._$relNode[0]);
        },

        _onSaveBefore: function (event) {
            var $option = $(event.currentTarget).closest("." + C.option),
                text = {value: $option.find("input[type='text']").val()},
                $staticText = $("<span class='" + C.optionStatictext + "'></span>");

            self._createButtonBar($option, "idle");
            $staticText
                .css("width", $option.width() - $option.find("." + C.buttonBar).width() - 2 + "px")
                .text(G.Lang.normalize(text.value))
                .attr("title", G.Lang.normalize(text.value))
                .tooltip({"delay": 0, "track": false});

            // clear temp text value
            $option.find("input[type='text']").remove();
            $option
                .attr("lastvalue", "")
                .append($staticText);

            self._state = "idle";

            var index = self._getOptionIndex($option);
            self._data[index]["label"] = G.Lang.normalize(text.value);
            self.onSave(event, index, self._data[index], self._$relNode[0]);
        },

        _onDeleteBefore: function (event) {
            var $option = $(event.currentTarget).closest("." + C.option),
                text = {value: $option.find("input[type='text']").val()};

            self._showDeleteConfirmBar(self, $option, self.onDelete, self._$relNode[0])
        },


        _onResumeBefore: function (event) {
            var $option = $(event.currentTarget).closest("." + C.option),
                text = {value: $option.attr("lastvalue")},
                $staticText = $(T.staticText);

            self._createButtonBar($option, "idle");

            $staticText
                .css("width", $option.width() - $option.find("." + C.buttonBar).width() - 2 + "px")
                .text(G.Lang.normalize(text.value))
                .attr("title", G.Lang.normalize(text.value))
                .tooltip({"delay": 0, "track": false});

            $option.find("input[type='text']").remove();
            $option.append($staticText);

            self._state = "idle";
        },

        // ==== callbacks ====
        onDefaultSelect: function (event, index, data, hook) {
            G.debug("onSelect the : " + $(event.currentTarget).text() + "// and index is : " + index + "// and data is : " + data);
        },

        onDefaultEdit: function (event, index, data, hook) {
            G.debug("onEdit the : " + $(event.currentTarget).text() + "// and index is : " + index + "// and data is : " + data);
        },

        onDefaultSave: function (event, index, data, hook) {
            G.debug("onSave the : " + $(event.currentTarget).text() + "// and index is : " + index + "// and data is : " + data);
        },

        onDefaultDelete: function (event, index, data, hook) {
            G.debug("onDelete the : " + $(event.currentTarget).text() + "// and index is : " + index + "// and data is : " + data);
        },

        onDefaultKeyboardSelect: function (event, index, data, hook) {
            G.debug("onKeyboardSelect the : " + $(event.currentTarget).text() + "// and index is : " + index + "// and data is : " + data);
        }

    };

    self = APP_BCGOGO.Module.droplist;
})();

// handler for click events
$(document).ready(function () {
    var self = APP_BCGOGO.Module.droplist;
    self.init();

    $(document).bind("click focus", function (event) {
        if(event.target === document.lastChild) {
            self.clear();
            self.hide();
            return;
        }

        if (self.isVisible()
            && self.getTarget()
            && self.getFollowNode()
            && $(event.target).closest(["#" + self.getTarget().id, $(".J-bcgogo-droplist-on")]).length === 0
            && !$(event.target).hasClass("ui-bcgogo-droplist-button")) {
            self.clear();
            self.hide();
        }
    });
});