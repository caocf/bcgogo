/**
 *   操作后菊花mask转起
 *   @author ndong
 *   @data 2014-10-27
 *
 * sample
 *   var mask=APP_BCGOGO.Module.waitMask;
 *   var option={display:'mobile'}
 *   mask.login(option);
 *

 */
;
(function () {
    APP_BCGOGO.namespace("Module.waitMask");
    var self,
        T={
            m_wait_icon:"<img src='images/wait.gif'>",
            wx_wait_icon:"<img src='/web/images/wait.gif'>", //微信端
            web_wait_icon:"<img src='images/wait.gif'>"   //todo
        };

    APP_BCGOGO.Module.waitMask = {
        opacity:'0.5',

        login:function (option) {
            option=option||{};
            if(self._isLogin()) {
                self.open();
            }
            var $mask=$("<div id='_wait_mask'></div>");
            $mask.css('display', 'block');
            $mask.css('position', 'absolute');
            $mask.css('top', '0px');
            $mask.css('left', '0px');
            $mask.css('width', '100%');
            $mask.css('height', '100%');
            $mask.css('className', 'b');
            $mask.css('zIndex', '1003');
            $mask.css('filter', 'alpha(opacity=40)');
            $mask.css('opacity', self.opacity);
            $mask.css('backgroundColor', '#000000');
            $(document.body).append($mask);
            //等待图标
            var $waitImg=null;
            if(option['dev']=="wx"){ //展示的平台 手机或者web浏览器
                $waitImg=$(T.wx_wait_icon)
            }else{
                $waitImg=$(T.web_wait_icon)
            }
//            $waitImg.attr("src",self.waitIcon);
            $waitImg.css("position","absolute");
            $waitImg.css("left",document.documentElement.clientWidth/2-50);
            $waitImg.css("top",document.documentElement.scrollHeight/2);
            $mask.append($waitImg);
//
        },

        open :function () {
            $("#_wait_mask").remove();
        },

        _isLogin :function () {
            return G.isEmpty($("#_wait_mask"))?false:true;
        }
    }

    self=APP_BCGOGO.Module.waitMask;

})();


