//单例图片放大缩小组件
(function ($) {
    // 插件的定义
    //依赖于jquery，base.js,application.js
    //1.0 先不实现buttons 以后需要实现可以参照1.0设计思路实现，
    /**
     * $(".imgclass").imageChange({
     * buttons:
     * {"打印":function(imageDialogInfo){},"旋转":function(imageDialogInfo){}}
     * });
     * @param options
     * @returns {*}
     */
    $.fn.imageChange = function (options) {
        // build main options before element iteration
        var opts = $.extend({}, $.fn.imageChange.defaults, options);
        var properties = $.fn.imageChange.properties;
        // iterate and reformat each matched element
        if (this.length > 0) {
            var bigImageUrls = [];
            this.each(function(i){
                var $this = $(this);
                bigImageUrls[i] = $this.attr("bigSrc");
            });
            properties["imageUrls"] = bigImageUrls;
            properties["totalImages"] = bigImageUrls.length;
            $(".img_dialog_pic_box").bind("mousemove", function (e) {
                var mouseX = e.originalEvent.x || e.originalEvent.layerX || 0;  //鼠标的x坐标
                var mouseY = e.originalEvent.y || e.originalEvent.layerY || 0;  //鼠标的y坐标
                var $this = $(this);
                var divWidth = $(this).width();
                if (mouseX < divWidth * 0.4) {
                    $this.css("cursor", 'url("images/common/cursor/pic_prev.cur"),auto');
                    $this.attr("action-type","prev");
                } else if (mouseX < divWidth * 0.6) {
                    $this.css("cursor", 'url("images/common/cursor/small.cur"),auto');
                    $this.attr("action-type","close");
                } else if(mouseX < divWidth){
                    $this.css("cursor", 'url("images/common/cursor/pic_next.cur"),auto');
                    $this.attr("action-type","next");
                }
            }).bind("click",function(){
                    var $this = $(this);
                    var actionType = $this.attr("action-type");
                    if (actionType == "close") {
                        $(".img_dialog_mask").hide();
                        $(".img_dialog_layer").hide();
                    } else if (actionType == "prev") {
                        if (properties["currentIndex"] > 0) {
                            properties["currentIndex"] = properties["currentIndex"] -1;
                            $(".img_dialog_pic_box img").attr("src", properties["imageUrls"][properties["currentIndex"]]);
                        }
                    } else if (actionType == "next") {
                        if (properties["currentIndex"] < properties["totalImages"] -1)  {
                            properties["currentIndex"] = properties["currentIndex"] + 1;
                            $(".img_dialog_pic_box img").attr("src", properties["imageUrls"][properties["currentIndex"]]);
                        }
                    }
                });
            var $totalImageDomes = $(this);
            return this.each(function (i) {
                var $this = $(this);
                var bigImgUrl = $this.attr("bigSrc");
                $this.css("cursor", 'url("images/common/cursor/big.cur"),auto')
                    .bind("click", function () {
                        var $this = $(this);
                        $(".img_dialog_mask").show();
                        $(".img_dialog_layer").show();
                        $(".img_dialog_pic_box img").attr("src", bigImgUrl);
                        properties["currentUrl"] = $this.attr("bigImgUrl");
                        properties["currentIndex"] = $totalImageDomes.index($this);
                    });
            });
        }

    };


    // 插件的defaults
    $.fn.imageChange.defaults = {

    };
    //照片组件属性
    $.fn.imageChange.properties = {
        $dialogDom:null,
        currentUrl: "",  //当前照片的url
        direction: "UP",  //当前照片的方向，默认是UP，向上，还有LEFT<-,还有RIGHT-> DOWN向下
        currentIndex:0,
        totalImages:0,
        imageUrls:[]
    };
// 闭包结束
})(jQuery);