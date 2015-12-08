/**
 * 此组件是用于
 * @author zhen.pan
 */
(function () {
    App.namespace("Module.Ratting");

    var define = function (prop) {
        return prop !== undefined && prop !== null;
    };

    var Ratting = function () {
        this._$ = null;
        this._rateCount = 0;
        this._bgBasePositionY = 0;
        this._bgPositionGapY = 0;
        this._enableRatting = false;
        this._isLocked = true;
        this._isOneOff = true;
        this._isPrompt = false;
        this._score = null;
        this._tipContents = null;
    };

    Ratting.prototype.T = {
        content:"" +
            "<div class='bcgogo-ratting'>" +
            "   <div class='starlist'>" +
            "   </div>" +
            "   <div class='comment'>" +
            "       <div class='arrow'></div>" +
            "       <div class='content'></div>" +
            "   </div>" +
            "</div>"
    };

    Ratting.prototype.C = {
        ratting:"bcgogo-ratting",
        starlist:"starlist",
        star:"star",
        comment:"comment",
        arrow:"arrow",
        content:"content"
    };

    Ratting.prototype.DEFAULT = {
        TIP:{
            WIDTH:150,
            HEIGHT:100,
            MIN_LEFT:0
        }
    };

    /**
     *
     * @param config {
     *     selector:xxx,
     *     score:{
     *         total:10,
     *         current:5
     *
     *     },
     *     config:{
     *         // default is true
     *         isLocked:true,
     *         // default is true
     *         isOneOff:true,
     *         // default is false
     *         isPrompt:false,
     *         starType:"small"|"big",
     *         // default is 5
     *         settingType:5|10,
     *         tip:{
     *             // default is 150
     *             width:150,
     *             // default is 100
     *             height:100,
     *             contents:[
     *                 {
     *                     scope:[0,1],
     *                     htmlText:""
     *                 },
     *                 {...}
     *             ]
     *         }
     *     },
     *     onRate:function () {}
     * }
     */
    Ratting.prototype.show = function (param) {
        var This = this;

        this.clear();

        this._$ = $(this.T.content);
        this._$.attr("id", "id-bcgogo-ratting-" + G.generateUUID());

        // add to body
        if($(param.selector)[0]) {
            $(param.selector).append(this._$);
        } else {
            return;
        }

        // init
        this._isLocked = param.config.isLocked;
        this._isOneOff = param.config.isOneOff;
        this._isPrompt = param.config.isPrompt;

        this._score = param.score;

        // init startlist
        var $starlist = this._$.find("." + this.C.starlist);
        if (param.config.starType === "small") {
            this._bgBasePositionY = -178;
            this._bgPositionGapY = -11;
            $starlist.css({
                "width":45,
                "height":9
            });
        } else if (param.config.starType === "yellow_big") {
            this._bgBasePositionY = 0;
            this._bgPositionGapY = -19;
            $starlist.css({
                "width": 91,
                "height": 17
            });
        } else {
            this._bgBasePositionY = 0;
            this._bgPositionGapY = -16;
            $starlist.css({
                "width":75,
                "height":15
            });
        }

        /**
         * @param score
         * {
         *     current:3,
         *     total:10
         * }
         * @return {Number}
         */
        var getRattingNumber = function (score) {
            var rattingNumber = 10 - Math.round(score.current * 1.0 / score.total * 10);
            return rattingNumber;
        };

        /**
         * @param score
         * {
         *     current:3,
         *     total:10
         * }
         * @return {String}
         */
        var getStarlistBackgroundPositionY = function (score) {
            var starlistBackgroundPositionY = "0px " + ((getRattingNumber(score) * This._bgPositionGapY) + This._bgBasePositionY) + "px";
            return starlistBackgroundPositionY;
        };

        /**
         * @param score
         */
        var setStarlistPosition = function (score) {
            $starlist.css("background-position", getStarlistBackgroundPositionY(score));
        };

        /**
         * @param param
         * {
         *     denominator:80,
         *     numerator:25
         * }
         * @param type 5|10
         */
        var getScoreWithParams = function (param, type) {
            var percent = param.numerator * 1.0 / param.denominator;
            percent = percent > 1 ? 1 : percent;
            percent = percent < 0 ? 0 : percent;

            var current = 0,
                total = 10;

            if (type === 10) {
                current = Math.round(percent * total);
                return {
                    total:total,
                    current:current
                };
            } else {
                current = Math.round(percent * total);
                current = (current & 1) === 1 ? (current + 1) : current;
                return {
                    total:total,
                    current:current
                };
            }
        };

        /**
         * @param contents [
         *     {
         *         scope:[0,1],
         *         htmlText:""
         *     },
         *     {...}
         * ]
         */
        var parseCommentContent = function (contents) {
            var result = {};

            if (contents) {
                for (var i = 0, len = contents.length; i < len; i++) {
                    var j = contents[i]["scope"][0],
                        end = contents[i]["scope"][1];
                    for (; j <= end; j++) {
                        result[j + ""] = contents[i]["htmlText"] || "";
                    }
                }
            }
            return result;
        };

        /**
         * @param currentScore
         * @param tipContents {
         *     "0":"xxx",
         *     "1":"xxx",
         *     ...,
         *     ...
         * }
         */
        var getCommentContent = function (currentScore, tipContents) {
            return tipContents[currentScore + ""] || "";
        };


        setStarlistPosition(this._score);

        // defined onRate function
        var isDeniedSetRate = param.config.isLocked || !param.onRate,
            onRateClickBefore = function (event) {
                if (isDeniedSetRate) return;
                if (This._isOneOff && This._rateCount > 0) return;
                if (!This._enableRatting) return;

                This._enableRatting = false;
                This._rateCount++;

                var denominator = $starlist.width(),
                    numerator = G.constrainNum((event.offsetX || (event.layerX - 2)), 0, denominator),
                    score = getScoreWithParams({
                        denominator:denominator,
                        numerator:numerator
                    });

                This._score = score;
                setStarlistPosition(This._score);

                $(param.hiddenScore).val(This._score.current/2);

                param.onRate(event, {
                    total:This._score.total,
                    current:This._score.current
                });
            };


        $starlist
            .bind("click", onRateClickBefore)
            .bind("mouseenter", function (event) {
                if (This._isLocked) return;
                if (This._isOneOff && This._rateCount > 0) return;

                This._enableRatting = true;

                $starlist.css("cursor", "pointer");
            })
            .bind("mousemove", function (event) {
                if (This._isLocked) return;
                if (!This._enableRatting) return;

                var denominator = $starlist.width(),
                    numerator = G.constrainNum((event.offsetX * 1.0 || (event.layerX - 2)), 0, denominator),
                    score = getScoreWithParams({
                        denominator:denominator,
                        numerator:numerator
                    });

                setStarlistPosition(score);
            })
            .bind("mouseleave", function (event) {
                if (This._isLocked) return;

                This._enableRatting = false;

                $starlist.css("cursor", "auto");
                setStarlistPosition(This._score);
            });


        // init comment
        var $comment = This._$.find(".comment"),
            $content = $comment.find(".content");

        if (!param.config.isPrompt) {
            $comment.hide();
            return;
        }

        var tipWidth = define(param.config.tip) && define(param.config.tip.width) ?
                param.config.tip.width
                :
                This.DEFAULT.TIP.WIDTH,
            tipHeight = define(param.config.tip) && define(param.config.tip.height) ?
                param.config.tip.height
                :
                This.DEFAULT.TIP.HEIGHT;

        $comment
            .css({
                "width":tipWidth,
                "height":tipHeight
            })
            .hide();

        // bind listener for itself
        $starlist
            .bind("mouseenter", function (event) {
                if (param.config.isPrompt) {
                    $comment.show();
                }
            })
            .bind("mousemove", function (event) {
                if (param.config.isPrompt) {
                    var denominator = $starlist.width(),
                        numerator = G.constrainNum((event.offsetX || (event.layerX - 2)), 0, denominator),
                        score = getScoreWithParams({
                            denominator:denominator,
                            numerator:numerator
                        });

                    $comment.css("margin-left", (event.offsetX || (event.layerX - 2)) + "px");

                    // Set dynamic comment content
                    $content.html(getCommentContent(score.current, This._tipContents));
                }
            })
            .bind("mouseleave", function (event) {
                if (param.config.isPrompt) {
                    $comment.hide();
                }
            });

        // set current comment content
        if (!param.config.tip) {
            param.config.tip = {};
        }
        This._tipContents = parseCommentContent(param.config.tip.contents);

        var score = getScoreWithParams({
            denominator:param.score.total,
            numerator:param.score.current
        });

        $content.html(getCommentContent(score.current, This._tipContents));
    };

    Ratting.prototype.getJqDom = function () {
        return this._$;
    };

    Ratting.prototype.clear = function () {
        if (this._$) {
            this._$.remove();
            this._$ = null;
        }
    };


    App.Module.Ratting = Ratting;

})();
