APP_BCGOGO.namespace("Module.autocomplete");

/**
 * @description autocomlete
 * @author 潘震
 * @data 2012-08-21
 */
APP_BCGOGO.Module.autocomplete = {
    _target:null,
    _data:[],
    _params:null,
    _uuid:"",

    setUUID:function(value) {
        this._uuid = value;
    },

    getUUID:function() {
        return this._uuid;
    },

    /**
     * @description init mouse and keyboard events
     * @param n
     */
    _initEvents:function(n) {
        $(".ui-bcgogo-autocomplete-option", n)
            .hover(function() {
                $(this).addClass("ui-bcgogo-autocomplete-highlighted");
            }, function() {
                $(this).removeClass("ui-bcgogo-autocomplete-highlighted");
            })
            .bind("click", this._clickBefore);
    },
    /**
     * @description init component
     * @param p
     */
    init:function(p) {
        this._target = $(p["selector"]);
        this.click = p["click"] || this.click;
        this._params = p;

        var s = "<ul class='ui-bcgogo-autocomplete'></ul>";
        $(this._target).html("").html(s);

        $(this._target).hide();
    },
    /**
     * @description draw view by data
     * @param data
     */
    draw:function(data) {
//        GLOBAL.debug(data);
        if (!data.hasOwnProperty("uuid") || data["uuid"] != this._uuid) {
            return;
        }

        this._data = data["data"];
        this.clear();

        var s = "";
        for (var i = 0,len = this._data.length; i < len; i++) {
            s += "<li class='ui-bcgogo-autocomplete-" + (this._data[i]["type"] == "category" ? "category" : "option") + "'>" + this._data[i]["label"] + "</li>";
        }
        $("ul", this._target).html(s);
        $("ul li", this._target)
            .each(function() {
                $(this).attr("title", $(this).text());
            })
            .tooltip({"delay": 0});

        if (this._params.hasOwnProperty("height")) {
            $(".ui-bcgogo-autocomplete", this._target).css("height", this._params["height"]);
        }
        this._initEvents(this._target);
    },

    show:function() {
        this._visible = true;
        $(this._target).show("fast");
    },

    _visible:false,

    hide:function() {
        this._visible = false;
        $(this._target).hide();
    },

    isVisible:function() {
        return this._visible || this._target.is(":visible");
    },

    /**
     * @description clear view
     */
    clear:function() {
        $("ul", this._target).html("");
    },

    _clickBefore:function(event) {
        var foo = APP_BCGOGO.Module.autocomplete;
        var index = $("ul .ui-bcgogo-autocomplete-option", foo._target).index(event.target);
        foo.click(event, index, foo._data[index]);
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
        $(".ui-bcgogo-autocomplete", this._target).css("width", value + "px");
    }
};