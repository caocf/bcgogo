/**
 * score panel
 *
 * @dependency bcgogo-scorePanel.css, bcgogo-ratting.js, bcgogo-ratting.css
 */
;
(function () {
    App.namespace("Module.ScorePanel");

    var ScorePanel = function () {
        // jqDom for self
        this._$ = null;
        // parameters to init self
        this._param = null;
    };

    ScorePanel.prototype.T = {
        content:"" +
            "<div class='bcgogo-scorePanel'>" +
            "    <div class='arrow'></div>" +
            "    <div class='content'>" +
            "        <div class='avg'>" +
            "            <div class='name'></div>" +
            "            <div class='rate'></div>" +
            "            <div class='score-num'></div>" +
            "            <div class='amount'></div>" +
            "            <div class='note'></div>" +
            "            <div style='clear:both;float: none;height: 0px;'></div>" +
            "        </div>" +
            "        <div class='hr'></div>" +
            "        <ul class='sub'>" +
            // dynamic append items
            "        </ul>" +
            "    </div>" +
            "</div>",
        item:"" +
            "<li class='item'>" +
            "    <div class='name'></div>" +
            "    <div class='rate'></div> " +
            "    <div class='score-num'></div>" +
            "    <div style='clear:both;float: none;height: 0px;'></div>" +
            "</li>"
    };

    /**
     * @param param {
     *     selector:xxx,
     *     config:{
     *         avgLabelWidth,
     *         subLabelWidth,
     *         width,
     *         height,
     *         "z-index"
     *     },
     *     avgScore:{
     *         value:4.5,
     *         htmlLabel:"",
     *         amount:2301，
     *         noScoreHtmlText:""
     *     },
     *     subScore:[
     *         {
     *             value:4.5,
     *             htmlLabel:""
     *         },
     *         {
     *             value:4.5,
     *             htmlLabel:""
     *         },
     *         {
     *             value:4.5,
     *             htmlLabel:""
     *         }
     *     ]
     * }
     */
    ScorePanel.prototype.show = function (param) {
        var This = this;

        This._param = param;

        var avgLabelWidth = param.config.avgLabelWidth,
            subLabelWidth = param.config.subLabelWidth,
            width = param.config.width,
            height = param.config.height,
            starType= param.config.starType || "yellow_big";


        if (!$(param.selector)[0]) {
          return;
        }
        var $root = $(param.selector);



        This._$ = $(This.T.content);

        var _$ = This._$,
            $avg = _$.find(".avg"),
            $sub = _$.find(".sub");

        _$
            .width(width)
            .height(height)
            .attr("id", "id_bcgogo_scorePanel_" + G.generateUUID())
            .appendTo($root);

        if(param.config && param.config["z-index"]) {
            _$.css("z-index", param.config["z-index"]);
        }

        // init average score
        $avg.find(".name")
            .html(param.avgScore.htmlLabel)
            .width(avgLabelWidth);

        var averageScore = 0;
        if (param.avgScore.value > parseInt(param.avgScore.value)) {
          averageScore = parseInt(param.avgScore.value) + 0.5;
        }else{
          averageScore = parseInt(param.avgScore.value);
        }

        var rate = new App.Module.Ratting();
        rate.show({
            selector:$avg.find(".rate"),
            score:{
                total:10,
                current:Math.floor(averageScore * 2)
            },
            config:{
                isLocked:true,
                starType:starType
//                starType:"big"
            }
        });

        $avg.find(".score")
            .html("<span class='red'>" + param.avgScore.value + "</span>分");

        $avg.find(".amount")
            .html("共" + param.avgScore.amount + "人");

        $avg.find(".note")
            .html(param.avgScore.noScoreHtmlText || "");



        if(param.avgScore.value === 0) {
            $avg.find(".name").hide();
            $avg.find(".rate").hide();
            $avg.find(".score").hide();
            $avg.find(".amount").hide();
            $avg.find(".note").show();

            _$.find(".hr").hide();
            $sub.hide();
        }else{
           $avg.find(".note").hide();

            for (var i = 0, len = param.subScore.length; i < len; i++) {
                var $item = $(This.T.item);
                $item.find(".name")
                    .html(param.subScore[i].htmlLabel)
                    .width(subLabelWidth);

                var subItemScore = 0;
                if (param.subScore[i].value > parseInt(param.subScore[i].value)) {
                  subItemScore = parseInt(param.subScore[i].value) + 0.5;
                } else {
                  subItemScore = parseInt(param.subScore[i].value);
                }

                var rate = new App.Module.Ratting();
                rate.show({
                    selector:$item.find(".rate"),
                    score:{
                        total:10,
                        current:Math.floor(subItemScore * 2)
                    },
                    config:{
                        isLocked:true,
                        starType:starType
//                        starType:"big"
                    }
                });

                $item.find(".score")
                    .html("<span class='red'>" + param.subScore[i].value + "</span>分");

                $sub.append($item);
            }
        }


    };


    /**
     * clear dom
     */
    ScorePanel.prototype.clear = function () {
        this._param = null;
        if (this._$) {
            this._$.remove();
        }
        this._$ = null;
    };

    /**
     *
     * @return {*} 返回 jqDom
     */
    ScorePanel.prototype.getJqDom = function () {
        return this._$;
    };


    App.Module.ScorePanel = ScorePanel;
})();
