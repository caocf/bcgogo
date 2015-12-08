/**
 * Created with IntelliJ IDEA.
 * User: Jimuchen
 * Date: 13-10-30
 * Time: 下午1:46
 * To change this template use File | Settings | File Templates.
 */
(function(){
    App.namespace("Module.pageVisible");

    var prefixSupport;

    var pageVisible = function(){

    };

    var keyWithPrefix = function(prefix, key) {
        if (prefix !== "") {
            // 首字母大写
            return prefix + key.slice(0,1).toUpperCase() + key.slice(1);
        }
        return key;
    };
    var isPageVisibilitySupport = (function() {
        var support = false;
        if (typeof window.screenX === "number") {
            ["webkit", "moz", "ms", "o", ""].forEach(function(prefix) {
                if (support == false && document[keyWithPrefix(prefix, "hidden")] != undefined) {
                    prefixSupport = prefix;
                    support = true;
                }
            });
        }
        return support;
    })();

    pageVisible.method("isHidden", function() {
        if (isPageVisibilitySupport) {
            return document[keyWithPrefix(prefixSupport, "hidden")];
        }
        return undefined;
    });

    pageVisible.method("visibilityState", function() {
        if (isPageVisibilitySupport) {
            return document[keyWithPrefix(prefixSupport, "visibilityState")];
        }
        return undefined;
    });

    pageVisible.method("getCurrStatus", function(){
        return {
            hidden: this.isHidden(),
            visibilityState: this.visibilityState(),
            visibilitychange: function(fn, usecapture) {
                usecapture = undefined || false;
                if (isPageVisibilitySupport && typeof fn === "function") {
                    return document.addEventListener(prefixSupport + "visibilitychange", function(evt) {
                        this.hidden = this.isHidden();
                        this.visibilityState = this.visibilityState();
                        fn.call(this, evt);
                    }.bind(this), usecapture);
                }
                return undefined;
            }
        }
    });

    APP_BCGOGO.Module.pageVisible = new pageVisible();
}());
