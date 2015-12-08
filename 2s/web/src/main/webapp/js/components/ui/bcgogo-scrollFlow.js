/**
 * ScrollFlowVertical
 *
 * html 结构:
 * <div class="JScrollFlowVertical">
 *     <ul class="JScrollGroup">
 *         <li class="JScrollItem">your content</li>
 *         <li class="JScrollItem">your content</li>
 *         <li class="JScrollItem">your content</li>
 *     </ul>
 * </div>
 *
 */
;(function() {
    App.namespace("Module.ScrollFlowVertical");

    var ScrollFlowVertical = function() {
        var _ = this;

        // 从外部传入的参数列表
        _._param = undefined;

        // 从外部传入的 JqDom 对象， 此参数取自 this._param.selector
        _._$external = undefined;

        // 内部创建的 ScrollBar
        _._$scrollBar = undefined;

        // current Pagination
        _._pageIndex = 0;

        // total Pagination
        _._pageLength = 0;

        // 自动滚动的时间间隔
        _._scrollInterval = 10 * 1000;

        // scroll timerId
        _._scrollTimerId = 0;

        _._isShowPagination = true;
    };

    /**
     * @param {
        *     "selector":"xxx",
     *     "onNextComplete":function(){},
     *     "onPrevComplete":function(){},
     *     "width":600,
     *     "height":300,
     *     "background":"xxx",
     *     "scrollInterval": 10 * 1000,
     *     "isShowPagination":true
     * }
     */
    ScrollFlowVertical.method("init", function(param) {
        var _ = this,
            $external = undefined,
            $scrollItemList = undefined;

        if(_._param) {
            G.error("already initialled!");
            return _;
        }
        _._param = param;

        $external = $(param.selector);
        if(!$external[0]) {
            G.error("a invalid parameter \"selector\", \"selector\" can't be found.");
            return _;
        }
        _._$external = $external;

        _._scrollInterval = param.scrollInterval || _._scrollInterval;
        _._isShowPagination = param.hasOwnProperty("isShowPagination") ? param.isShowPagination : _._isShowPagination;

        $external
            .css("width", param.width)
            .css("height", param.height)
            .css("margin", 0)
            .css("color", "#000")
            .css("position", "relative")
            .css("padding", 0);

        $external.find(".JScrollGroup")
            .css("width", param.width)
            .css("height", param.height)
            .css("background", param.background)
            .css("overflow", "hidden")
            .css("margin", 0)
            .css("padding", 0)
            .css("list-style", "none");

        $scrollItemList = $external.find(".JScrollGroup").find(".JScrollItem");
        $scrollItemList
            .css("width", param.width)
            .css("height", param.height)
            .css("overflow", "hidden")
            .css("display", "block")
            .css("margin", 0)
            .css("padding", 0);

        _._pageIndex = 0;
        _._pageLength = $scrollItemList.length;

        _._initScrollBar();

        $scrollItemList.stop(true, true).hide();
        $scrollItemList.eq(_._pageIndex).slideDown("slow");

        _._$external
            .bind("mouseenter", function(event) {
            if(_._pageLength <= 1) {
                return;
            }

            _._$external.find(".JScrollBar").stop(true, true).fadeIn("fast");

            if(_._scrollTimerId > 0) {
                $(this).attr("data-scrollStatus", "pause");
                _.stopAutoScroll();
            }

            var $scrollPagination = _._$external.find(".JScrollPagination");
            _._updatePaginationDisplay($scrollPagination);
        })
            .bind("mouseleave", function(event) {
                if(_._pageLength <= 1) {
                    return;
                }

                _._$external.find(".JScrollBar").stop(true, true).fadeOut("fast");

                if($(this).attr("data-scrollStatus") === "pause") {
                    $(this).removeAttr("data-scrollStatus");
                    _.startAutoScroll();
                }
            });

        return _;
    });

    ScrollFlowVertical.method("startAutoScroll", function() {
        var _ = this;

        _._scrollTimerId = setInterval(function () {
            if(_._pageLength <= 1) {
                return _;
            }

            // get offset
            var nextIndex = _._getNextPageIndex(),
                $item = _._$external.find(".JScrollItem");

            _._pageIndex = nextIndex;

            $item.stop(true, true).hide();
            if(nextIndex !== 0 ) {
                // 不是最后一页
                $item.eq(nextIndex).slideDown("slow");
            } else {
                // 是最后一页
                $item.eq(nextIndex).fadeIn("slow");
            }

        }, _._scrollInterval);
    });

    ScrollFlowVertical.method("stopAutoScroll", function() {
        var _ = this;

        clearInterval(_._scrollTimerId);
        _._scrollTimerId = 0;
        return _;
    });

    ScrollFlowVertical.method("_initScrollBar", function(){
        var _ = this;

        if(_._$scrollBar) {
            G.error("already initialled scrollBar");
            return _;
        }

        var temple = "" +
            "<div class='JScrollBar'>" +
            // previous page
            "    <div class='JScrollPrevButton' onselectstart='return false;'>" +
            "        " +
            "    </div>" +
            // Pagination
            "    <div class='JScrollPagination' onselectstart='return false;'>" +
            "        " +
            "    </div>" +
            // next page
            "    <div class='JScrollNextButton' onselectstart='return false;'>" +
            "        " +
            "    </div>" +
            "</div>";

        var $scrollBar = $(temple),
            $scrollPrevButton = $scrollBar.find(".JScrollPrevButton"),
            $scrollNextButton = $scrollBar.find(".JScrollNextButton"),
            $scrollPagination = $scrollBar.find(".JScrollPagination");

        $scrollBar.appendTo(_._$external);

        // set styles
        $scrollBar
            .css({
            "width":36,
            "height":172,
            "position":"absolute"
        })
            .css({
//                "right": (_._$external.width() - $scrollBar.width()) / 2,
                "left":8,
                "top":(_._$external.height() - $scrollBar.height()) / 2
            });

        $scrollPrevButton.css({
            "width":$scrollBar.width(),
            "height":36,
            "background":"url(js/components/themes/res/scrollFlow/arrow.png) no-repeat",
            // black
            "background-position":"0px 0px",
            "cursor":"pointer",
            "opacity":0.7,
            "filter": "alpha(opacity=0.7)"
        });

        $scrollNextButton.css({
            "width":$scrollBar.width(),
            "height":36,
            "background":"url(js/components/themes/res/scrollFlow/arrow.png) no-repeat",
            // black
            "background-position":"0px -60px",
            "cursor":"pointer",
            "opacity":0.7,
            "filter": "alpha(opacity=0.7)"
        });

        $scrollPagination.css({
            "width":$scrollBar.width(),
            "height":100,
            "font-size":"14px",
            "font-family":"Arial",
            "font-weight":"bold",
            "position":"relative",
            "opacity":0.7,
            "filter": "alpha(opacity=0.7)"
        });
        _._updatePaginationDisplay($scrollPagination);

        // set events
        $scrollBar.find(".JScrollPrevButton").bind("click", function(event) {
            var $scrollItem = _._$external.find(".JScrollItem");

            _._pageIndex =  _._getPrevPageIndex();
            $scrollItem.stop(true, true).hide();
            $scrollItem.eq(_._pageIndex).slideDown("slow");
            _._updatePaginationDisplay($scrollPagination);
        });

        $scrollBar.find(".JScrollNextButton").bind("click", function(event) {
            var $scrollItem = _._$external.find(".JScrollItem");

            _._pageIndex = _._getNextPageIndex();
            $scrollItem.stop(true, true).hide();
            $scrollItem.eq(_._pageIndex).slideDown("slow");
            _._updatePaginationDisplay($scrollPagination);
        });

        $scrollBar.hide();

        _._$scrollBar = $scrollBar;

        return this;
    });

    ScrollFlowVertical.method("_getPrevPageIndex", function() {
        var retIndex = this._pageIndex;

        retIndex--;
        return retIndex < 0 ? this._pageLength - 1 : retIndex;
    });

    ScrollFlowVertical.method("_getNextPageIndex", function() {
        var retIndex = this._pageIndex;

        retIndex++;
        return retIndex > this._pageLength - 1 ? 0 : retIndex;
    });

    ScrollFlowVertical.method("_updatePaginationDisplay", function($node){
        var _ = this;

        var innerHtml = "" +
            "<div class='JInnerHtml'>" +
            "    <div class='JInnerHtmlParagraph'>" + (this._pageIndex + 1) + "</div>" +
            "    <div class='JInnerHtmlParagraph'>/</div>" +
            "    <div class='JInnerHtmlParagraph'>" + this._pageLength + "</div>" +
            "</div>";

        $node.html(innerHtml);

        var $innerHtml = $node.find(".JInnerHtml"),
            $paragraph = $innerHtml.find(".JInnerHtmlParagraph");

        var itemHeight = 24;
        $innerHtml
            .css({
            "width":$node.width(),
            "height":itemHeight * 3,
            "text-align":"center",
            "margin":0,
            "padding":0,
            "position":"relative"
        })
            .css({
                "top":($node.height() - $innerHtml.height()) / 2
            });

        $paragraph
            .css({
            "padding":0,
            "margin":0,
            "line-height":itemHeight + "px"
        });

        $paragraph.toggle(_._isShowPagination);
    });

    App.Module.ScrollFlowVertical = ScrollFlowVertical;
} ());



/**
 * ScrollFlowHorizontal
 * <div class="JScrollFlowHorizontal">
 *     <ul class JScrollGroup>
 *         <li class="JScrollItem">your content</li>
 *         <li class="JScrollItem">your content</li>
 *         <li class="JScrollItem">your content</li>
 *     </ul>
 * </div>
 */
;(function () {
    App.namespace("Module.ScrollFlowHorizontal");

    var ScrollFlowHorizontal = function() {
        var _ = this;

        // 从外部传入的参数列表
        _._param = undefined;

        // 从外部传入的 JqDom 对象， 此参数取自 this._param.selector
        _._$external = undefined;

        // current Pagination
        _._pageIndex = 0;

        // total Pagination
        _._pageLength = 0;

        // 自动滚动的时间间隔
        _._scrollInterval = 10 * 1000;

        // scroll timerId
        _._scrollTimerId = 0;
    };

    /**
     * @param {
        *     "selector":"xxx",
     *     "width":800,
     *     "height":200,
     *     "background":"xxx",
     *     "scrollInterval":10 * 1000
     * }
     */
    ScrollFlowHorizontal.method("init", function(param) {
        var _ = this,
            $external = undefined,
            $scrollItemList = undefined;

        if (_._param) {
            G.error("already initialled!");
            return this;
        }
        _._param = param;

        $external = $(param.selector);
        if(!$external[0]) {
            G.error("a invalid parameter \"selector\", \"selector\" can't be found.");
            return _;
        }
        _._$external = $external;

        _._scrollInterval = param.scrollInterval || _._scrollInterval;

        _._$external = $external;
        $external
            .css("width", param.width)
            .css("height", param.height)
            .css("overflow", "hidden")
            .css("margin", 0)
            .css("position", "relative")
            .css("padding", 0);

        $external.find(".JScrollGroup")
            .css("height", param.height)
            .css("background", param.background)
            .css("overflow", "hidden")
            .css("margin", 0)
            .css("padding", 0)
            .css("position", "absolute")
            .css("list-style", "none");

        $scrollItemList = $external.find(".JScrollGroup").find(".JScrollItem");
        $scrollItemList
            .css("width", param.width)
            .css("height", param.height)
            .css("overflow", "hidden")
            .css("display", "block")
            .css("float","left")
            .css("margin", 0)
            .css("padding", 0);

        _._pageIndex = 0;
        _._pageLength = $scrollItemList.length;

        $external.find(".JScrollGroup")
            .css("width", param.width * _._pageLength + 100);

        $external
            .bind("mouseenter", function (event) {
            if( _._pageLength <= 1 ) {
                return;
            }

            if(_._scrollTimerId > 0) {
                $(this).attr("data-scrollStatus", "pause");
                _.stopAutoScroll();
            }
        })
            .bind("mouseleave", function (event) {
                if(_._pageLength <= 1 ) {
                    return;
                }

                if($(this).attr("data-scrollStatus") === "pause") {
                    $(this).removeAttr("data-scrollStatus");
                    _.startAutoScroll();
                }
            });

        $external.find(".JScrollNextButton")
            .bind("click", function (event) {
                if(_._pageLength <= 1) {
                    return _;
                }

                // get offset
                var nextIndex = _._getNextPageIndex(),
                    itemWidth = _._$external.find(".JScrollItem").outerWidth();

                _._pageIndex = nextIndex;

                var left = nextIndex * itemWidth;

                if(nextIndex !== 0 ) {
                    // 不是最后一页
                    _._$external.find(".JScrollGroup").stop(true, true).animate({
                        "left":left * -1
                    }, 500, "swing");
                } else {
                    // 是最后一页
                    _._$external.find(".JScrollGroup")
                        .stop(true, true)
                        .hide()
                        .css("left", left * -1)
                        .fadeIn("slow");
                }
            });

        $external.find(".JScrollPrevButton")
            .bind("click", function (event) {
                if(_._pageLength <= 1) {
                    return _;
                }
                // get offset
                var lastIndex = _._getPrevPageIndex(),
                    itemWidth = _._$external.find(".JScrollItem").outerWidth();
                _._pageIndex = lastIndex;
                var left = lastIndex * itemWidth;
                if(lastIndex !== _._pageLength - 1 ) {
                    // 不是第一页
                    _._$external.find(".JScrollGroup").stop(true, true).animate({
                        "left":left * -1
                    }, 500, "swing");
                }
                else {
                    _._$external.find(".JScrollGroup")
                        .stop(true, true)
                        .hide()
                        .css("left", left * -1)
                        .fadeIn("slow");
                }


            });

        if(_._pageLength > 1){
            $(".JScrollPrevButton,.JScrollNextButton").css("visibility","visible");
        }else{
            $(".JScrollPrevButton,.JScrollNextButton").css("visibility","hidden");
        }
        return _;
    });

    ScrollFlowHorizontal.method("startAutoScroll", function() {
        var _ = this;

        _._scrollTimerId = setInterval(function () {
            if(_._pageLength <= 1) {
                return _;
            }

            // get offset
            var nextIndex = _._getNextPageIndex(),
                itemWidth = _._$external.find(".JScrollItem").outerWidth();

            _._pageIndex = nextIndex;

            var left = nextIndex * itemWidth;

            if(nextIndex !== 0 ) {
                // 不是最后一页
                _._$external.find(".JScrollGroup").stop(true, true).animate({
                    "left":left * -1
                }, 500, "swing");
            } else {
                // 是最后一页
                _._$external.find(".JScrollGroup")
                    .stop(true, true)
                    .hide()
                    .css("left", left * -1)
                    .fadeIn("slow");
            }

        }, _._scrollInterval);

        return _;
    });

    ScrollFlowHorizontal.method("stopAutoScroll", function() {
        var _ = this;

        clearInterval(_._scrollTimerId);
        _._scrollTimerId = 0;
        return _;
    });

    ScrollFlowHorizontal.method("_getPrevPageIndex", function() {
        var retIndex = this._pageIndex;

        retIndex--;
        return retIndex < 0 ? this._pageLength - 1 : retIndex;
    });

    ScrollFlowHorizontal.method("_getNextPageIndex", function() {
        var retIndex = this._pageIndex;

        retIndex++;
        return retIndex > this._pageLength - 1 ? 0 : retIndex;
    });

    App.Module.ScrollFlowHorizontal = ScrollFlowHorizontal;
}());