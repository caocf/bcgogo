/**
 * Created by IntelliJ IDEA.
 * User: liyi
 * Date: 12-8-22
 * Time: 上午9:55
 * Description: 该文件依赖于
 *              js/extension/jquery/jquery-1.4.2.min.js,
 *              js/extension/jquery/plugin/jquery-ui/themes/flick/jquery-ui-1.8.21.custom.css,
 *              js/extension/jquery/plugin/jquery-ui/ui/jquery-ui-1.8.21.custom.min.js
 */
(function() {
    APP_BCGOGO.namespace("Module.noticeDialog");
    APP_BCGOGO.Module.noticeDialog = {
        //==============================================
        // 模拟浏览器alert
        //==============================================
        jAlert:function(content, title, callback, isDraggable) {
            //判断是否是从iframe触发的事件
            var isIframe = (self.frameElement != null && self.frameElement.tagName == "IFRAME"),
                isDraggable = isDraggable || false;

            //判断该元素是否已经存在
            var jAlert = ($("#jAlert") > 0) ? $("#jAlert") : parent.$("#jAlert");
            if (jAlert.length > 0) {
                jAlert.dialog('close');
                jAlert.remove();
            }

            //创建对话框的HTML
            var _title = title || '信息提示';
            var _body = isIframe ? parent.$('body') : $('body');
            var _html = '<div id="jAlert" title="' + _title + '">' +
                '<p>' +
                content +
                '</p>' +
                '</div>';
            $(_html).appendTo(_body);

            //构建dialog
            $("#jAlert", _body).dialog({
                resizable: false,
                modal: true,
                draggable:isDraggable,
                buttons: {
                    "确定": function() {
                        parent.$("#jAlert").dialog("close");
                        if (callback != undefined) {
                            callback();
                        }
                    }
                },
                open: function(event, ui) { 
                    $(".ui-dialog-titlebar-close").hide();
                }
            });
        },
        //==============================================
        // 模拟浏览器confirm
        //==============================================
    jConfirm :function(content, title, callback, isDraggable) {
            //判断是否是从iframe触发的事件
            var isIframe = (self.frameElement != null && self.frameElement.tagName == "IFRAME"),
                isDraggable = isDraggable || false;

            //判断该元素是否已经存在
            var jConfirm = ($("#jConfirm") > 0) ? $("#jConfirm") : parent.$("#jConfirm");
            if (jConfirm.length > 0) {
                jConfirm.dialog('close');
                jConfirm.remove();
            }

            //创建对话框的HTML
            var _title = title || '信息提示';
            var _body = isIframe ? parent.$('body') : $('body');
            var _html = '<div id="jConfirm" title="' + _title + '">' +
                '<p>' +
                content +
                '</p>' +
                '</div>';
            $(_html).appendTo(_body);

            //构建dialog
            $("#jConfirm", _body).dialog({
                resizable: false,
                modal: true,
                draggable:isDraggable,
                buttons: {
                    "确定": function() {
                        parent.$("#jConfirm").dialog("close");
                        callback(true);
                    },
                    "取消": function() {
                        parent.$("#jConfirm").dialog("close");
                        callback(false);
                    }
                },
                open: function(event, ui) { 
                    $(".ui-dialog-titlebar-close").hide(); 
                }
            });
        }
    };
})();
var nsDialog = APP_BCGOGO.Module.noticeDialog;