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
        var $target = $(p["selector"]), sString = $target.val(), dString = p["value"].toString();
        if (GLOBAL.Lang.trim(sString).length == 0) {
            return false;
        }
        // IE 8 不支持 selectionStart 属性设置，所以遇到 ie 8 跳过
        if( $.browser.msie && $.browser.version.startWith("8") ) {
            return false;
        }

        if( GLOBAL.Lang.startWith(dString, sString) ) {
            $target.val(dString);
            $target[0].selectionStart = sString.length;
            return true;
        }
        return false;
    }
};
