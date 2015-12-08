/**
 *   手机端报错提醒
 *
 *   @author ndong
 *   @data 2015-09-06
 *
 * sample
 *   var bcAlert=APP_BCGOGO.Module.bcAlert;
 *    bcAlert.login({
 *         info:eResult.errorMsg,
 *         level:eResult.level
 *       });
 *         ....
 *    bcAlert.open();

 */
;
(function () {
    APP_BCGOGO.namespace("Module.bcAlert");
    var self,
        T = {
            m_alert_template:"<div><div class='j_alert_title'></div><div class='j_error_info'></div></div>"
        };

    APP_BCGOGO.Module.bcAlert = {
        opacity:'0.5',

        login:function (option) {
            option = option || {};
            if (self._isLogin()) {
                self.open();
            }
            var $mask = $("<div id='__mask'></div>");
            if (option.modal||option.level=='error') {
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
            }
            $(document.body).append($mask);
            //等待图标
            var $alertTemplate = null;
            if (option['dev'] == "web") { //展示的平台 手机或者web浏览器
                $alertTemplate = $(T.m_alert_template)
            } else {
                $alertTemplate = $(T.m_alert_template)
            }
            $alertTemplate.css("position", "absolute");
            $alertTemplate.css("top", document.documentElement.scrollHeight / 2- 50);
            $alertTemplate.css("height", "6em");
            $alertTemplate.css("width", "100%");
            //报错提醒背景red，普通提醒为green
            if(option.level=='error'){
                $alertTemplate.css("background", "red");
            }else{
                $alertTemplate.css("background", "green");
            }
            $alertTemplate.css("padding", "1em");

            $alertTemplate.css("color", "#080808");
            $alertTemplate.css("text-align", "center");

            var title=!option.title?"提醒":option.title;
            $alertTemplate.find(".j_alert_title").text(title);
            var info=option.info;
            var $errorInfo=$alertTemplate.find(".j_error_info");
            $errorInfo.css("margin-top", "1em");
            $errorInfo.text(info);
            $mask.append($alertTemplate);
//
        },

        open :function () {
            $("#__mask").remove();
        },

        _isLogin :function () {
            return G.isEmpty($("#__mask")) ? false : true;
        }
    }

    self = APP_BCGOGO.Module.bcAlert;

})();


