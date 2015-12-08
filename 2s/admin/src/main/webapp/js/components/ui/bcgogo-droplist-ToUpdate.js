;
(function () {
    APP_BCGOGO.namespace("Module.droplist");

    var self,
        lang = GLOBAL.Lang,
        interactive = GLOBAL.Interactive,

    // Class Names
        C = {
            droplist:"ui-bcgogo-droplist",
            droplistContainer:"ui-bcgogo-droplist-container",
            highlight:"ui-bcgogo-droplist-highlighted",
            option:"ui-bcgogo-droplist-option",
            category:"ui-bcgogo-droplist-category",
            buttonBar:"ui-bcgogo-droplist-buttonBar",
            button:"ui-bcgogo-droplist-button",
            optionStatictext:"ui-bcgogo-droplist-option-staticText",
            optionEditabletext:"ui-bcgogo-droplist-option-text"
        },

    // Templates
        T = {
            confirmBar:"<div class='ui-bcgogo-droplist-delete-confirmBar'>"
                + "    <div class='J-button-ok'></div>"
                + "    <div class='J-button-cancel'></div>"
                + "</div>",
            buttonBar:"<div class='" + C.buttonBar + "'></div>",
            droplist:"<ul class='" + C.droplist + "'></ul>",
            editableText:"<input type='text' class='" + C.optionEditabletext + "' />",
            staticText:"<span class='" + C.optionStatictext + "'></span>"
        };

    /**
     * @description droplist
     * @author 潘震
     * @data 2012-10-16
     */
    APP_BCGOGO.Module.droplist = {
        _$target:null,
        _$relNode:null,
        _storageValue:"",
        _data:[],
        _params:null,
        _uuid:"",
        // editing, idle
        _state:"idle",
        MIN_WIDTH:150,
        DEFAULT_HEIGHT:250,

        setUUID:function (value) {
            self._uuid = value;
        },

        getUUID:function () {
            return self._uuid;
        },

        getTarget:function () {
            return self._$target ? self._$target[0] : null;
        },

        getFollowNode:function () {
            return self._$relNode ? self._$relNode[0] : null;
        },

        /**
         * @description init mouse and keyboard events
         * @param self
         */
        _initEvents:function () {
            self._removeOptionEventListener();
            self._addOptionEventListener();
        },

        _highlightOption:function ($option) {
            $("." + C.option, self._$target)
                .not($option)
                .each(function(){
                    $(this)
                        .removeClass(C.highlight)
                        .find("." + C.optionStatictext).removeClass(C.highlight);
                });

            $option
                .addClass(C.highlight)
                .find("." + C.optionStatictext).addClass(C.highlight);
        },

        _greyOption:function ($option) {
            $option
                .removeClass(C.highlight)
                .find("." + C.optionStatictext).removeClass(C.highlight);
        },

        _addOptionEventListener:function () {
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

        _removeOptionEventListener:function () {
            $("." + C.option, self._$target).unbind("mouseenter mouseleave click");
        },

        _onOptionMouseEnter:function (event) {
            var $option = $(event.currentTarget).closest("." + C.option),
                index = self._getOptionIndex($option);
            if (self._state === "editing" || self._data[index]["isEditable"] === false)
                return;

            $("." + C.option, self._$target)
                .not($option)
                .each(function(){
                    self._removeButtonBar($(this));
                });

            self._createButtonBar($option, "idle");
        },

        _onOptionMouseLeave:function (event) {
            if (self._state === "editing")
                return;

            self._removeButtonBar($(event.currentTarget));
        },

        _removeButtonBar:function ($option) {
            $option.find("." + C.buttonBar).remove();

            self._changeStaticTextWidth($option, false);
        },

        _createButtonBar:function ($option, state) {
            $option.find("." + C.buttonBar).remove();
            $option.append(self._getButton$BarByState(state));

            self._changeStaticTextWidth($option, true);
        },

        _changeStaticTextWidth:function ($option, isShowButton) {
            if (isShowButton) {
                var btWidth = btWidth = $("." + C.buttonBar, $option).width();
                $option.find("." + C.optionStatictext).css("width", $option.width() - btWidth - 8 + "px");
            } else {
                $option.find("." + C.optionStatictext).css("width", $option.width() + "px");
            }
        },

        _getButton$NodeByName:function (name) {
            var $div = $("<div class='" + C.button + " ui-bcgogo-droplist-" + name + "Button-normal'></div>");
            $div
                .bind("mousedown", function (event) {
                    self._changeButtonStyleByEvent(event, "press");
                })
                .bind("mouseout mouseup", function (event) {
                    self._changeButtonStyleByEvent(event, "unpress");
                })
                .bind("click", self["_on" + name.charAt(0).toUpperCase() + name.slice(1, name.length) + "Before"]);
            return $div;
        },

        _getButton$BarByState:function (state) {
            var $bar = $(T.buttonBar);

            if (state === "editing") {
                $bar.append(
                    self._getButton$NodeByName("resume"),
                    self._getButton$NodeByName("save")
                );
            } else if (state === "idle") {
                // 添加了 delete 按钮
                $bar.append(
                    self._getButton$NodeByName("delete"),
                    self._getButton$NodeByName("edit")
                );
            }
            return $bar;
        },

        /**
         * @description init component
         * @param p
         */
        init:function () {
            // create hook
            if (self._$target) {
                $(window.document.body).remove(self._$target);
            }
            self._$target = $("<div id='bcgogo-droplist-" + GLOBAL.Util.generateUUID() + "' class='" + C.droplistContainer + "'></div>");
            self._$target.appendTo(window.document.body);
        },

        show:function (p) {
            self._params = p;
            self.onSelect = p["onSelect"] || self.onSelect;
            self.isEditable = p["isEditable"] || false;
            self.isNoticeWhenSave = p["isNoticeWhenSave"] || false;
            self.onSave = p["onSave"] || self.onSave;
            self.onEdit = p["onEdit"] || self.onEdit;
            self.onDelete = p["onDelete"] || self.onDelete;

            var s = T.droplist;
            self._$target.html("").html(s);
            self._$target.hide();

            self._$target.css("z-index", 1000);
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
        draw:function (data) {
            self._state = "idle";

//        GLOBAL.debug(data);
            if (!data.hasOwnProperty("uuid")
                || data["uuid"] != self._uuid
                || !data["data"]) {
                return;
            }

            self._data = data["data"];
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
                .find("li")
                .each(function () {
                    $(this).attr("title", $("." + C.optionStatictext, this).text());
                })
                .tooltip({"delay":0, "track":false});

            var height = self._params["height"] ? self._params["height"] + "px" : self.DEFAULT_HEIGHT + "px";
            $("." + C.droplist, self._$target).css("height", height);
            self._setWidth(self._$relNode.width());

            self._initEvents();
        },

        _changeButtonStyleByEvent:function (event, action) {
            var prefix = event.currentTarget.className.split(" ")[1].replace(/(-normal|-press){1}$/g, "");
            if (action === "press") {
                $(event.currentTarget).removeClass(prefix + "-normal").addClass(prefix + "-press");
            } else if (action === "unpress") {
                $(event.currentTarget).removeClass(prefix + "-press").addClass(prefix + "-normal")
            }
        },

        _visible:false,

        _show:function () {
            self._visible = true;
            $(self._$target).show(); // blind, fast
        },

        hide:function () {
            self._visible = false;
            $(self._$target).hide();
        },

        follow:function (param) {
            var $node = $(param["selector"]);

            if (param["moveOnly"]) {
                self.clear();
            }

            $node.unbind("keydown", self._onHookKeyboardEvent);

            self._$relNode = $node;
            $node.addClass("J-bcgogo-droplist-on");

            var oX = GLOBAL.Display.getX($node[0]),
                oY = GLOBAL.Display.getY($node[0]),
                rootPosition = {
                    left:oX,
                    top:oY + 22
                };

            $(self._$target)
                .css("left", rootPosition["left"] + "px")
                .css("top", rootPosition["top"] + "px");

            $node.bind("keydown", self._onHookKeyboardEvent);
        },

        _onHookKeyboardEvent:function (event) {
            if (self.isVisible() === false || self._state === "editing") {
                return;
            }

            var keyName = interactive.keyNameFromEvent(event);
            if (keyName.search(/up|down/g) === -1) {
                return;
            }

            var $optionArr = $("." + C.option, self._$target),
                $optionActived = $("." + C.highlight, self._$target),
                index = 0,
                hash = {};

            // get index value
            if ($optionActived[0]) {
                index = $optionArr.index($optionActived);
            } else {
                index = -1;
            }
            // store value , if necessary
            if(index === -1) {
                self._storageValue = self._$relNode.val();
            }

            // add value to hash
            $.each($optionArr, function (index, element) {
                hash[index + ""] = $("." + C.optionStatictext, element).text();
            });
            hash["-1"] = self._storageValue;

            // generate new index
            if (keyName === "up") {
                index--;
                index = index < -1 ? $optionArr.length - 1 : index;
            } else {
                index++;
                index = index > $optionArr.length - 1 ? -1 : index;
            }

            // set option styles
            $optionArr.each(function(){
                self._greyOption($(this));
                self._removeButtonBar($(this));
            });

            if (index !== -1) {
                // add buttonBar to option
                if ( !( self._state === "editing" || self._data[index]["isEditable"] === false) ) {
                    self._createButtonBar($optionArr.eq(index), "idle");
                }

                // highlight option
                self._highlightOption($optionArr.eq(index));
            }

            // set new value to View
            event.currentTarget.value = hash[index+""];
        },

        isVisible:function () {
            return self._visible || self._$target.is(":visible");
        },

        /**
         * @description clear view
         */
        clear:function () {
            $("ul > li", self._$target).mouseout();
            $("ul", self._$target).html("");
        },

        _setWidth:function (value) {
            $("." + C.droplist, self._$target).css("width", value < self.MIN_WIDTH ? self.MIN_WIDTH : value + "px");
        },

        _showDeleteConfirmBar:function (self, $option, callback) {
            var s = T.confirmBar,
                $bar = $(s),
                $ok = $(".J-button-ok", $bar).button({label:"删除"}).css("float", "left"),
                $cancel = $(".J-button-cancel", $bar).button({label:"取消"}).css("float", "left"),
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

        _clearDeleteConfirmBar:function ($element) {
            $element.remove();
        },

        _getOptionIndex:function ($option) {
            return $("." + C.option, self._$target).index($option);
        },

        _clickBefore:function (event) {
            var $option = $(event.currentTarget), $target = $(event.target);
            if (self._state === "editing")
                return;

            if ($target.not("." + C.button)[0]) {
                var index = self._getOptionIndex($option);
                self.onSelect(event, index, self._data[index]);
            }
        },

        _onEditBefore:function (event) {
            var $option = $(event.currentTarget).closest("." + C.option),
                text = {value:$option.find("." + C.optionStatictext).text()},
                $editText = $(T.editableText),
                index = self._getOptionIndex($option);

            self._createButtonBar($option, "editing");

            $editText
                .css("width", $option.width() - $option.find("." + C.buttonBar).width() - 8 + "px")
                .val(lang.normalize(text.value))
                .bind("keyup", function (event) {
                    if (interactive.isKeyName(event, "enter")) {
                        $(".ui-bcgogo-droplist-saveButton-normal", $option).click();
                    }
                });

            $option.find("." + C.optionStatictext).remove();

            // store temp text value
            $option
                .attr("lastvalue", text.value)
                .append($editText);

            self._state = "editing";

            self.onEdit(event, index, self._data[index]);
        },

        _onSaveBefore:function (event) {
            var $option = $(event.currentTarget).closest("." + C.option),
                text = {value:$option.find("input[type='text']").val()},
                $staticText = $("<span class='" + C.optionStatictext + "'></span>");

            self._createButtonBar($option, "idle");
            $staticText
                .css("width", $option.width() - $option.find("." + C.buttonBar).width() - 2 + "px")
                .text(lang.normalize(text.value))
                .attr("title", lang.normalize(text.value))
                .tooltip({"delay":0, "track":false});

            // clear temp text value
            $option.find("input[type='text']").remove();
            $option
                .attr("lastvalue", "")
                .append($staticText);

            self._state = "idle";

            var index = self._getOptionIndex($option);
            self._data[index]["label"] = lang.normalize(text.value);
            self.onSave(event, index, self._data[index]);
        },

        _onDeleteBefore:function (event) {
            var $option = $(event.currentTarget).closest("." + C.option),
                text = {value:$option.find("input[type='text']").val()};

            self._showDeleteConfirmBar(self, $option, self.onDelete)
        },


        _onResumeBefore:function (event) {
            var $option = $(event.currentTarget).closest("." + C.option),
                text = {value:$option.attr("lastvalue")},
                $staticText = $(T.staticText);

            self._createButtonBar($option, "idle");

            $staticText
                .css("width", $option.width() - $option.find("." + C.buttonBar).width() - 2 + "px")
                .text(lang.normalize(text.value));

            $option.find("input[type='text']").remove();
            $option.append($staticText);

            self._state = "idle";
        },

        // ==== callbacks ====
        onSelect:function (event, index, data) {
            GLOBAL.debug("onSelect the : " + $(event.target).text()
                + "// and index is : " + index
                + "// and data is : " + data);
        },

        onEdit:function (event, index, data) {
            GLOBAL.debug("onEdit the : " + $(event.target).text()
                + "// and index is : " + index
                + "// and data is : " + data);
        },

        onSave:function (event, index, data) {
            GLOBAL.debug("onSave the : " + $(event.target).text()
                + "// and index is : " + index
                + "// and data is : " + data);
        },

        onDelete:function (event, index, data) {
            GLOBAL.debug("onSave the : " + $(event.target).text()
                + "// and index is : " + index
                + "// and data is : " + data);
        }

    };

    self = APP_BCGOGO.Module.droplist;
}());

// handler for click events
$(document).ready(function () {
    var self = APP_BCGOGO.Module.droplist;
    self.init();

    $(document).bind("click focus", function (event) {
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