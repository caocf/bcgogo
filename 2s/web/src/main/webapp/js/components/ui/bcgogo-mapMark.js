/**
 * 地图标点组件
 *
 * 这两个方法的详细说明， 已经写在了下面方法上， 请直接搜索查看即可
 * @method initDefault (p)
 * @method init (p)
 */
;
(function () {
    App.namespace("Module.MapMark");

    // TODO scale automatically support



    // alias
    var net = App.Net;

    var T = {
            "content": "" +
                "<div class='J-mapMark'>" +
                "    <img />" +
                "</div>",
            "item": "" +
                "<div class='J-coordinate-object'></div>"
        },
        //[{"label":"贵州","point":"226,265","":""},{"label":"云南","point":"183,289"},{"label":"宁夏","point":"223,157"},{"label":"陕西","point":"237,192"},{"label":"吉林","point":"350,80"},{"label":"黑龙江","point":"357,50"},{"label":"辽宁","point":"333,109"},{"label":"山西","point":"265,163"},{"label":"内蒙古","point":"200,126"},{"label":"青海","point":"145,166"},{"label":"甘肃","point":"145,126"},{"label":"北京","point":"301,128"},{"label":"天津","point":"316,142"},{"label":"四川","point":"190,225"},{"label":"重庆","point":"230,230"},{"label":"西藏","point":"70,210"},{"label":"新疆","point":"70,120"},{"label":"上海","point":"358,213"},{"label":"江苏","point":"334,191"},{"label":"浙江","point":"334,235"},{"label":"安徽","point":"312,211"},{"label":"江西","point":"300,245"},{"label":"台湾","point":"350,285"},{"label":"香港","point":"314,306"},{"label":"澳门","point":"287,315"},{"label":"广东","point":"290,295"},{"label":"海南","point":"256,340"},{"label":"福建","point":"324,270"},{"label":"山东","point":"310,165"},{"label":"河南","point":"275,190"},{"label":"河北","point":"290,150"},{"label":"湖南","point":"264,254"},{"label":"湖北","point":"270,216"},{"label":"广西","point":"243,296"}]
        DEFAULTS = {
            W:411,
            H:361,
            FONT_SIZE:12,
            FONT_FAMILY:"黑体",
            DATA:'[{"label":"贵州","point":"226,265","provinceNo":"1024"},' +
                '{"label":"云南","point":"183,289","provinceNo":"1025"},' +
                '{"label":"宁夏","point":"223,157","provinceNo":"1030"},' +
                '{"label":"陕西","point":"237,192","provinceNo":"1027"},' +
                '{"label":"吉林","point":"350,80","provinceNo":"1007"},' +
                '{"label":"黑龙江","point":"357,50","provinceNo":"1008"},' +
                '{"label":"辽宁","point":"333,109","provinceNo":"1006"},' +
                '{"label":"山西","point":"265,163","provinceNo":"1004"},' +
                '{"label":"内蒙古","point":"200,126","provinceNo":"1005"},' +
                '{"label":"青海","point":"145,166","provinceNo":"1029"},' +
                '{"label":"甘肃","point":"145,126","provinceNo":"1028"},' +
                '{"label":"北京","point":"301,128","provinceNo":"1001"},' +
                '{"label":"天津","point":"316,142","provinceNo":"1002"},' +
                '{"label":"四川","point":"190,225","provinceNo":"1023"},' +
                '{"label":"重庆","point":"230,230","provinceNo":"1022"},' +
                '{"label":"西藏","point":"70,210","provinceNo":"1026"},' +
                '{"label":"新疆","point":"70,120","provinceNo":"1031"},' +
                '{"label":"上海","point":"358,213","provinceNo":"1009"},' +
                '{"label":"江苏","point":"334,191","provinceNo":"1010"},' +
                '{"label":"浙江","point":"334,235","provinceNo":"1011"},' +
                '{"label":"安徽","point":"312,211","provinceNo":"1012"},' +
                '{"label":"江西","point":"300,245","provinceNo":"1014"},' +
                '{"label":"台湾","point":"350,285","provinceNo":"1034"},' +
                '{"label":"香港","point":"314,306","provinceNo":"1032"},' +
                '{"label":"澳门","point":"287,315","provinceNo":"1033"},' +
                '{"label":"广东","point":"290,295","provinceNo":"1019"},' +
                '{"label":"海南","point":"256,340","provinceNo":"1021"},' +
                '{"label":"福建","point":"324,270","provinceNo":"1013"},' +
                '{"label":"山东","point":"310,165","provinceNo":"1015"},' +
                '{"label":"河南","point":"275,190","provinceNo":"1016"},' +
                '{"label":"河北","point":"290,150","provinceNo":"1003"},' +
                '{"label":"湖南","point":"264,254","provinceNo":"1018"},' +
                '{"label":"湖北","point":"270,216","provinceNo":"1017"},' +
                '{"label":"广西","point":"243,296","provinceNo":"1020"}]',
            BACKGROUND_IMAGE:"js/components/themes/res/map/map.png"
        };

    /**
     *
     * @constructor
     */
    var MapMark = function () {
        this._$ = undefined;
        this._param = undefined;
        this._$hook = undefined;

        this._zoom = 1;
    };

    MapMark.method("_getCoordinateNodeArr", function (coordinateList) {
        var that = this,
            $arr = [];

        for (var i = 0; i < coordinateList.length; i++) {
            var obj = coordinateList[i],
                $item = $(T.item);

            $item
                .html("<span class='J-text-label'>" + obj.label + "</span>")
                .attr("jshref", obj.href || "")
                .attr("data-original", encodeURIComponent(JSON.stringify(obj)))
                .css("position", "absolute")
                .css("left", (parseFloat(G.trim(obj.point.split(",")[0])) || 0) * that._zoom + "px")
                .css("top", (parseFloat(G.trim(obj.point.split(",")[1])) || 0) * that._zoom + "px")
                .css("color", "#000")
                .bind("mouseenter", function (event) {
                    $(this)
                        .css("text-decoration", "underline")
                        .css("color", "#0000ff")
                        .css("cursor", "pointer");
                })
                .bind("mouseleave", function (event) {
                    $(this)
                        .css("text-decoration", "none")
                        .css("color", "#000")
                        .css("cursor", "auto");
                })
                .bind("click", function (event) {
                    var itemData = JSON.parse(decodeURIComponent($(this).attr("data-original")));

                    that._param.onSelect(event, itemData);
                    return false;
                });

            $arr.push($item);
        }

        return $arr;
    });

    /**
     * 此方法是真正的init()方法， 他没有默认参数自动设置，都需要用户自己将param 自己构造好， 然后作为完整的参数传进来。
     * 而 initDefault() 方法的本质也是调用了 init() 方法
     *
     * @param p {
     *     "selector":xxx,
     *     "backgroundUrl":"",  // 此参数非必传
     *     "width":411,  // 此参数非必传
     *     "coordinateList":[
     *         {
     *             "label":"北京",
     *             "href":"xxxxxx",
     *             "point":"10,10",
     *             "...":"..."
     *         },
     *         {
     *             "label":"北京",
     *             "href":"xxxxxx",
     *             "point":"-20,100",
     *             "...":"..."
     *         }
     *     ],
     *     "onSelect":function() {}
     * }
     *
     *
     */
    MapMark.method("init", function (p) {
        if (this._$) {
            throw new Error("init duplicated!");
            return this;
        }

        this._param = p;
        if (!this._param) {
            throw new Error("param error!");
            return this;
        }

        this._$hook = $(p.selector);
        if (!p.selector) {
            throw new Error("param error!");
            return this;
        }

        var _$ = $(T.content);

        this._$ = _$;

        var width = 0,
            height = 0;

        width = p.width || DEFAULTS.W;
        this._zoom = width / DEFAULTS.W;
        height = DEFAULTS.H * this._zoom;

        _$
            .appendTo(this._$hook)
//            .css("background", "url(" + (p.backgroundUrl || DEFAULTS.BACKGROUND_IMAGE) + ")")
            .css("position", "absolute")
            .css("margin", 0)
            .css("padding", 0)
            .css("font-size", DEFAULTS.FONT_SIZE)
            .css("font-family", DEFAULTS.FONT_FAMILY)
            .css("width", width)
            .css("height", height)
            .find("img")
            .attr("src", DEFAULTS.BACKGROUND_IMAGE)
            .css("width", width)
            .css("height", height);

        p.onSelect = p.onSelect || function () {
            G.error("Please set onSelect handler!");
        };

        var $coordinateNodeArr = this._getCoordinateNodeArr(p.coordinateList);

        $.each($coordinateNodeArr, function (index, value) {
            _$.append($(value));
        });
    });

    /**
     * 快捷创建一个中国地图， 地图上包含了各个省的名字坐标。
     *
     * @param p {
     *     "selector":"xxx",
     *     "onSelect":function(event, itemData){}
     * }
     * 其中 onSelect 是回调函数。 回调函数会返回两个参数，event是事件原事件对象，itemData所点击的坐标点对应的数据。
     *
     * 好， 那么我对这个数据来做个说明：
     *     itemData 的数据格式如下
     *     {
     *         "label":"xx",
     *         "point":"xxxxx",
     *         "xxx":"xxx"
     *     }
     *
     * 而本方法 initDefault(p) ，如上面所示，本方法的参数 p 中只需要设置两个参数。 却没有设置对应的地图数据。
     * 这是因为，对于此函数，组件会自动加载自的地图（组件自带的中国地图），而坐标数据，也使用在组件内部的静态数据
     *
     * 样例数据如下：
     *     '[{"label":"贵州","point":"226,265"},{"label":"云南","point":"183,289"},{"label":"宁夏","point":"223,157"},{"label":"陕西","point":"237,192"},{"label":"吉林","point":"350,80"},{"label":"黑龙江","point":"357,50"},{"label":"辽宁","point":"333,109"},{"label":"山西","point":"265,163"},{"label":"内蒙古","point":"200,126"},{"label":"青海","point":"145,166"},{"label":"甘肃","point":"145,126"},{"label":"北京","point":"301,128"},{"label":"天津","point":"316,142"},{"label":"四川","point":"190,225"},{"label":"重庆","point":"230,230"},{"label":"西藏","point":"70,210"},{"label":"新疆","point":"70,120"},{"label":"上海","point":"358,213"},{"label":"江苏","point":"334,191"},{"label":"浙江","point":"334,235"},{"label":"安徽","point":"312,211"},{"label":"江西","point":"300,245"},{"label":"台湾","point":"350,285"},{"label":"香港","point":"314,306"},{"label":"澳门","point":"287,315"},{"label":"广东","point":"290,295"},{"label":"海南","point":"256,340"},{"label":"福建","point":"324,270"},{"label":"山东","point":"310,165"},{"label":"河南","point":"275,190"},{"label":"河北","point":"290,150"},{"label":"湖南","point":"264,254"},{"label":"湖北","point":"270,216"},{"label":"广西","point":"243,296"}]'
     *
     * 默认的参数中只包含 label, point 两个字段
     * 如果是公用的方法调用，请看init 方法
     *
     */
    MapMark.method("initDefault", function (p) {
        var coordinateList = JSON.parse(DEFAULTS.DATA);

        this.init({
            "width": p.width,
            "selector": p.selector,
            "coordinateList": coordinateList,
            "onSelect": p.onSelect
        });
    });

    MapMark.method("show", function () {
        this._$.show();
    });

    MapMark.method("hide", function () {
        this._$.hide();
    });

    MapMark.method("dispose", function () {
        if(!this._$) return;

        this._$.remove();
        this._$ = undefined;
        this._$hook = undefined;
        this._param = undefined;
    });

    App.Module.MapMark = MapMark;

}());
