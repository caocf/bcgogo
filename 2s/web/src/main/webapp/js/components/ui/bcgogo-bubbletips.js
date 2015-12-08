/**
 * 冒泡
 * author:zhangjuntao
 * dom 属性detail
 */
$(function () {
    $.fn.bubbleTips = function (options) {
        var bid = parseInt(Math.random() * 1000000),
            $this = $(this),
            $bTip = $('<div class="bcgogo-bubble-tip" id="btip-' + bid + '">' +
//                '   <i class="triangle-' + options.position + '"></i>' +
//                '   <div class="tl">' +
//                '       <div class="inner">' +
//                '           <div class="cont"></div>' +
//                '       </div>' +
//                '   </div>' +
//                '   <div class="tr"></div>' +
//                '   <div class="bl"></div>' +
                '</div>'),
            timerIdForHide = 0;

        $("body").prepend($bTip);

        var offset, h , w;
        offset = $this.offset();
        h = $this.height();
        w = $this.width();
        $bTip.html($this.attr("detail"));

        var defaults = {
//            position: "b",		//箭头指向上(t)、箭头指向下(b)、箭头指向左(l)、箭头指向右(r)
//            triangleOffsetX: 35,		    //小箭头偏离左边和上边的位置
            left: offset.left - 23,
            top: offset.top + h + $bTip.height()
        };

        options = $.extend(defaults, options);

        $this
            .bind("mouseenter mousemove", function () {
                clearTimeout(timerIdForHide);
                $("[id^=btip-]").not($bTip).hide();
//                $bTip.find(".cont").html($this.attr("detail"));
//                $bTip.find(".triangle-b").css('left', options.triangleOffsetX);
                $bTip.css({ "left": options.left, "top": options.top  }).show();
            })
            .bind("mouseleave", function (e) {
                if (($bTip.has(e.relatedTarget).length == 0) && ($this.has(e.relatedTarget).length == 0)) {
                    timerIdForHide = setTimeout(function () {
                        $("[id^=btip-]").hide();
                    }, 50);
                }
            });


        $bTip
            .bind("mouseenter", function (event) {
                clearTimeout(timerIdForHide);
            })
            .bind("mouseleave", function (e) {
                if ($bTip.has(e.relatedTarget).length == 0 && $this.has(e.relatedTarget).length == 0) {
                    timerIdForHide = setTimeout(function () {
                        $("[id^=btip-]").hide();
                    }, 50);
                }
            });

    }
});