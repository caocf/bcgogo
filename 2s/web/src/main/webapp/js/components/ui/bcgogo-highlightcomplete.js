/**
 * @author 潘震, 张峻滔
 * @description 自动高亮补全
 *
 */
APP_BCGOGO.namespace("APP_BCGOGO.Module.highlightcomplete");
APP_BCGOGO.Module.highlightcomplete = {
    /**
     * @description 补全内容
     * @param data
     * @returns {Boolean}
     */
    complete:function(p){
        if( p.hasOwnProperty("selector") == false || p.hasOwnProperty("value") == false) {
            return false;
        }
        var $target = $(p["selector"]), sString = $target.val(), dString = G.normalize(p["value"]).toString();
        if (GLOBAL.Lang.trim(sString).length == 0) {
            return false;
        }
        // IE 8 不支持 selectionStart 属性设置，所以遇到 ie 8 跳过
        if( $.browser.msie && $.browser.version.startWith("8") ) {
            return false;
        }

//        if( GLOBAL.Lang.startWith(dString, sString) ) {
//            $target.val(dString);
//            $target[0].selectionStart = sString.length;
//            return true;
//        }
        return false;
    },
    completer:function(p){
        if(p["keycode"]) {
            if(p["title"].length > 0 && p["domObject"]== document.activeElement) {
                var inputtingTimerId = 0;
                clearTimeout(inputtingTimerId);
                if(G.keyNameFromKeyCode(p["keycode"]).search(/left|up|right|down|enter|backspace/g) == -1) {
                    inputtingTimerId = setTimeout(function() {
                        APP_BCGOGO.Module.highlightcomplete.complete({
                            "selector": $(p["domObject"]),
                            "value": p["title"] //
                        });
                    }, 300);
                }
            }
        }
    }
};

GLOBAL.completer = APP_BCGOGO.Module.highlightcomplete.completer;
