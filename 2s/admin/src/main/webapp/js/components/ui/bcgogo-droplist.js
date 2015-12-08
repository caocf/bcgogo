APP_BCGOGO.namespace("Module.droplist");

/**
 * @description droplist
 * @author 潘震
 * @data 2012-10-16
 */
APP_BCGOGO.Module.droplist = {
    _$target:null,
    _$relNode:null,
    _data:[],
    _params:null,
    _uuid:"",
    // editing, idle
    _state:"idle",
    MIN_WIDTH:150,
    DEFAULT_HEIGHT:250,

    setUUID:function (value) {
        this._uuid = value;
    },

    getUUID:function () {
        return this._uuid;
    },

    getTarget:function () {
        var foo = APP_BCGOGO.Module.droplist;
        return foo._$target ? foo._$target[0] : null;
    },

    getFollowNode:function () {
        var foo = APP_BCGOGO.Module.droplist;
        return foo._$relNode ? foo._$relNode[0] : null;
    },

    /**
     * @description init mouse and keyboard events
     * @param foo
     */
    _initEvents:function (foo) {
        foo._removeOptionEventListener(foo);
        foo._addOptionEventListener(foo);
    },

    _addOptionEventListener:function (foo) {
        $(".ui-bcgogo-droplist-option", foo._$target)
            .hover(function () {
                if (foo._state === "editing")
                    return;

                $(this)
                    .addClass("ui-bcgogo-droplist-highlighted")
                    .find("span").addClass("ui-bcgogo-droplist-highlighted");
            }, function () {
                if (foo._state === "editing")
                    return;

                $(this)
                    .removeClass("ui-bcgogo-droplist-highlighted")
                    .find("span").removeClass("ui-bcgogo-droplist-highlighted");
            })
            .bind("click", foo._clickBefore);

        if (foo.isEditable) {
            $(".ui-bcgogo-droplist-option", foo._$target)
                .bind("mouseenter", foo._onOptionMouseEnter)
                .bind("mouseleave", foo._onOptionMouseLeave);
        }
    },

    _removeOptionEventListener:function (foo) {
        $(".ui-bcgogo-droplist-option", foo._$target).unbind("mouseenter mouseleave click");
    },

    _onOptionMouseEnter:function (event) {
        var foo = APP_BCGOGO.Module.droplist
            $option = $(event.currentTarget).closest(".ui-bcgogo-droplist-option"),
            index = foo._getOptionIndex($option);
        if (foo._state === "editing" || foo._data[index]["isEditable"] === false)
            return;

        $(".ui-bcgogo-droplist-highlighted", foo._$target)
            .not(event.currentTarget)
            .removeClass("ui-bcgogo-droplist-highlighted")
            .find(".ui-bcgogo-droplist-buttonBar").remove();
        foo._createButtonBar($(this), "idle");
    },

    _onOptionMouseLeave:function (event) {
        var foo = APP_BCGOGO.Module.droplist,
            $option = $(event.currentTarget).closest(".ui-bcgogo-droplist-option"),
            index = foo._getOptionIndex($option);
        if (foo._state === "editing")
            return;

        foo._removeButtonBar($(this));
    },

    _removeButtonBar:function ($option) {
        var foo = APP_BCGOGO.Module.droplist;
        $option.find(".ui-bcgogo-droplist-buttonBar").remove();

        // width change
        foo._changeStaticTextWidth($option, false);
    },

    _createButtonBar:function ($option, state) {
        var foo = APP_BCGOGO.Module.droplist;
        $option.find(".ui-bcgogo-droplist-buttonBar").remove();
        $option.append(foo._getButton$BarByState(state));

        //width change
        foo._changeStaticTextWidth($option, true);
    },

    _changeStaticTextWidth:function ($option, isShowButton) {
        if (isShowButton) {
            var btWidth = 0;
            btWidth = $(".ui-bcgogo-droplist-buttonBar", $option).width();
            $option.find("span").css("width", $option.width() - btWidth - 8 + "px");
        } else {
            $option.find("span").css("width", $option.width() + "px");
        }
    },

    _getButton$NodeByName:function (name) {
        var foo = APP_BCGOGO.Module.droplist,
            $div = $("<div class='ui-bcgogo-droplist-button ui-bcgogo-droplist-" + name + "Button-normal'></div>");
        $div
            .bind("mousedown", function (event) {
                foo._changeButtonStyleByEvent(event, "press");
            })
            .bind("mouseout mouseup", function (event) {
                foo._changeButtonStyleByEvent(event, "unpress");
            })
            .bind("click", foo["_on" + name.charAt(0).toUpperCase() + name.slice(1, name.length) + "Before"]);
        return $div;
    },

    _getButton$BarByState:function (state) {
        var foo = APP_BCGOGO.Module.droplist,
            $bar = $("<div class='ui-bcgogo-droplist-buttonBar'></div>");

        if (state === "editing") {
            $bar.append(foo._getButton$NodeByName("resume"), foo._getButton$NodeByName("save"));
        } else if (state === "idle") {
            // 添加了 delete 按钮
            $bar.append(foo._getButton$NodeByName("delete"), foo._getButton$NodeByName("edit"));
        }
        return $bar;
    },

    /**
     * @description init component
     * @param p
     */
    init:function () {
        var foo = APP_BCGOGO.Module.droplist;
        // create hook
        if (foo._$target) {
            $(window.document.body).remove(foo._$target);
        }
        foo._$target = $("<div id='bcgogo-droplist-" + GLOBAL.Util.generateUUID() + "' class='ui-bcgogo-droplist-container'></div>");
        foo._$target.appendTo(window.document.body);
    },

    show:function (p) {
        var foo = APP_BCGOGO.Module.droplist;

        foo._params = p;
        foo.onSelect = p["onSelect"] || foo.onSelect;
        foo.isEditable = p["isEditable"] || false;
        foo.isNoticeWhenSave = p["isNoticeWhenSave"] || false;
        foo.onSave = p["onSave"] || foo.onSave;
        foo.onEdit = p["onEdit"] || foo.onEdit;
        foo.onDelete = p["onDelete"] || foo.onDelete;

        var s = "<ul class='ui-bcgogo-droplist'></ul>";
        foo._$target.html("").html(s);
        foo._$target.hide();

        foo._$target.css("z-index", 1000);
        foo.follow(p);
        foo.hide();
        foo._show();
        foo.draw(p.data || {});
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
        var foo = APP_BCGOGO.Module.droplist;
        foo._state = "idle";

//        GLOBAL.debug(data);
        if ( !data.hasOwnProperty("uuid")
            || data["uuid"] != foo._uuid
            || !data["data"]) {
            return;
        }

        foo._data = data["data"];
        foo.clear();

        var s = "", type = "", label = "";
        for (var i = 0, len = foo._data.length; i < len; i++) {
            type = foo._data[i]["type"] === "category" ? "category" : "option";
            label = foo._data[i]["label"];
            s += "<li class='ui-bcgogo-droplist-" + type + "'>"
                + "  <span class='ui-bcgogo-droplist-option-staticText '>" + label + "</span>"
                + "</li>";
        }

        $("ul", foo._$target)
            .html(s)
            .find("li")
            .each(function () {
                $(this).attr("title", $("span", this).text());
            })
            .tooltip({"delay":0, "track":false});

        var height = foo._params["height"] ? foo._params["height"] + "px" : foo.DEFAULT_HEIGHT + "px";
        $(".ui-bcgogo-droplist", this._$target).css("height", height);
        foo._setWidth(foo._$relNode.width());

        foo._initEvents(foo);
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
        this._visible = true;
//        $(this._$target).show("blind");
        $(this._$target).show();
    },

    hide:function () {
        this._visible = false;
        $(this._$target).hide();
    },

    follow:function (param) {
        var foo = APP_BCGOGO.Module.droplist, $node = $(param["selector"]);

        if (param["moveOnly"]) {
            foo.clear();
        }

        foo._$relNode = $node;
        $node.addClass("J-bcgogo-droplist-on");

        var oX = GLOBAL.Display.getX($node[0]),
            oY = GLOBAL.Display.getY($node[0]),
            rootPosition = {
                left:oX,
                top:oY + 22
            };

        $(foo._$target)
            .css("left", rootPosition["left"] + "px")
            .css("top", rootPosition["top"] + "px");
    },

    isVisible:function () {
        return this._visible || this._$target.is(":visible");
    },

    /**
     * @description clear view
     */
    clear:function () {
//        $(".ui-bcgogo-droplist-highlighted", this._$target).mouseout();
        $("ul > li", this._$target).mouseout();
        $("ul", this._$target).html("");
    },

//    destory:function () {
//        if(this._$target)
//            this._$target.parent().remove(this._$target);
//        if(this._$relNode && this._$relNode.hasClass("J-bcgogo-droplist-on"))
//            this._$relNode.removeClass("J-bcgogo-droplist-on");
//        this._$target = null;
//    },

    _clickBefore:function (event) {
        var foo = APP_BCGOGO.Module.droplist, $option = $(event.currentTarget), $target = $(event.target);
        if (foo._state === "editing")
            return;

        if ($target.not(".ui-bcgogo-droplist-button")[0]) {
            var index = foo._getOptionIndex($option);
            foo.onSelect(event, index, foo._data[index]);
        }
    },

    _getOptionIndex:function ($node) {
        var foo = APP_BCGOGO.Module.droplist;
        return $("ul .ui-bcgogo-droplist-option", foo._$target).index($node);
    },

    // ==== callbacks ====
    onSelect:function (event, index, data) {
        GLOBAL.debug("onSelect the : " + $(event.target).text()
            + "// and index is : " + index
            + "// and data is : " + data);
    },

    _onEditBefore:function (event) {
        var foo = APP_BCGOGO.Module.droplist,
            $option = $(event.currentTarget).closest(".ui-bcgogo-droplist-option"),
            text = {value:$option.find("span").text()},
            $textEdit = $("<input type='text' class='ui-bcgogo-droplist-option-text' />");

        foo._createButtonBar($option, "editing");

        $textEdit
            .css("width", $option.width() - $option.find(".ui-bcgogo-droplist-buttonBar").width() - 8 + "px")
            .val(GLOBAL.Lang.normalize(text.value))
            .bind("keyup", function (event) {
                if (GLOBAL.Interactive.isKeyName(event, "enter")) {
                    $(".ui-bcgogo-droplist-saveButton-normal", $option).click();
                }
            });

        $option.find("span").remove();

        // store temp text value
        $option
            .attr("lastvalue", text.value)
            .append($textEdit);

        foo._state = "editing";

        var index = foo._getOptionIndex($option);
        foo.onEdit(event, index, foo._data[index]);
    },

    onEdit:function (event, index, data) {
        GLOBAL.debug("onEdit the : " + $(event.target).text()
            + "// and index is : " + index
            + "// and data is : " + data);
    },

    _onSaveBefore:function (event) {
        var foo = APP_BCGOGO.Module.droplist,
            $option = $(event.currentTarget).closest(".ui-bcgogo-droplist-option"),
            text = {value:$option.find("input[type='text']").val()},
            $textStatic = $("<span class='ui-bcgogo-droplist-option-staticText'></span>");

        foo._createButtonBar($option, "idle");
        $textStatic
            .css("width", $option.width() - $option.find(".ui-bcgogo-droplist-buttonBar").width() - 2 + "px")
            .text(GLOBAL.Lang.normalize(text.value))
            .attr("title", GLOBAL.Lang.normalize(text.value))
            .tooltip({"delay":0, "track":false});

        // clear temp text value
        $option.find("input[type='text']").remove();
        $option
            .attr("lastvalue", "")
            .append($textStatic);

        foo._state = "idle";

        var index = foo._getOptionIndex($option);
        foo._data[index]["label"] = GLOBAL.Lang.normalize(text.value);
        foo.onSave(event, index, foo._data[index]);
    },

    onSave:function (event, index, data) {
        GLOBAL.debug("onSave the : " + $(event.target).text()
            + "// and index is : " + index
            + "// and data is : " + data);
    },

    _showDeleteConfirmBar:function (foo, $option, callback) {
        var s = "<div class='ui-bcgogo-droplist-delete-confirmBar'>"
                + "    <div class='J-button-ok'></div>"
                + "    <div class='J-button-cancel'></div>"
                + "</div>",
            $bar = $(s),
            $ok = $(".J-button-ok", $bar).button({label:"删除"}).css("float", "left"),
            $cancel = $(".J-button-cancel", $bar).button({label:"取消"}).css("float", "left"),
            index = foo._getOptionIndex($option),
            deleteData = foo._data[index];

        foo._state = "editing";

        $ok.bind("click", function (event) {
            event.stopPropagation();
            foo._data.splice(index, 1);
            $(".ui-bcgogo-droplist-highlighted", foo._$target).mouseout();
            $option.remove();
            callback(event, index, deleteData);
            foo._state = "idle";
        });

        $cancel.bind("click", function (event) {
            event.stopPropagation();
            foo._clearDeleteConfirmBar($bar);
            $option.find(".ui-bcgogo-droplist-option-staticText").show();
            $option.find(".ui-bcgogo-droplist-buttonBar").show();
            foo._state = "idle";
        });

        $option.find(".ui-bcgogo-droplist-option-staticText").hide();
        $option.find(".ui-bcgogo-droplist-buttonBar").hide();
        $option.append($bar);
    },

    _clearDeleteConfirmBar:function ($element) {
        $element.remove();
    },

    _onDeleteBefore:function (event) {
        var foo = APP_BCGOGO.Module.droplist,
            $option = $(event.currentTarget).closest(".ui-bcgogo-droplist-option"),
            text = {value:$option.find("input[type='text']").val()};

        foo._showDeleteConfirmBar(foo, $option, foo.onDelete)
    },

    onDelete:function (event, index, data) {
        GLOBAL.debug("onSave the : " + $(event.target).text()
            + "// and index is : " + index
            + "// and data is : " + data);
    },

    _onResumeBefore:function (event) {
        var foo = APP_BCGOGO.Module.droplist,
            $option = $(event.currentTarget).closest(".ui-bcgogo-droplist-option"),
            text = {value:$option.attr("lastvalue")},
            $textStatic = $("<span class='ui-bcgogo-droplist-option-staticText'></span>");

        foo._createButtonBar($option, "idle");
//        foo._addOptionEventListener(foo);

        $textStatic
            .css("width", $option.width() - $option.find(".ui-bcgogo-droplist-buttonBar").width() - 2 + "px")
            .text(GLOBAL.Lang.normalize(text.value));

        $option.find("input[type='text']").remove();
        $option.append($textStatic);

        foo._state = "idle";
    },

    _setWidth:function (value) {
        var foo = APP_BCGOGO.Module.droplist;
        $(".ui-bcgogo-droplist", foo._$target).css("width", value < foo.MIN_WIDTH ? foo.MIN_WIDTH : value + "px");
    }
};

// handler for click events
$(document).ready(function () {
    var droplist = APP_BCGOGO.Module.droplist;
    droplist.init();

    $(document).bind("click focus", function (event) {
        var droplist = APP_BCGOGO.Module.droplist;
        if (droplist.isVisible()
            && droplist.getTarget()
            && droplist.getFollowNode()
            && $(event.target).closest(["#" + droplist.getTarget().id, $(".J-bcgogo-droplist-on")]).length === 0
            && !$(event.target).hasClass("ui-bcgogo-droplist-button")) {
            droplist.clear();
            droplist.hide();
        }
    });
});