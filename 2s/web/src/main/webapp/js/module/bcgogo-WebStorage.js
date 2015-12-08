/**
 * web缓存机制
 * ZhangJuntao,
 * @change zhen.pan
 */
(function () {
    //key
    APP_BCGOGO.namespace("Module.WebStorage.Key");
    APP_BCGOGO.Module.WebStorage.Key = {
        ClientUrl:"client-url",
        SearchBox: "search_box",
        //刷卡机
        STATE_KEY: "scanning_statekey",
        //菜单
        Menus: "menus",
        MenuRoad: "menu-road",
        MenuCurrentData: "menu-current-data",
        MenuCurrentItem: "menu-current-item",
        MenuUid: "menu-uid",
        //自定义配置
        PageCustomizerOrderConfig: "page-customizer-order-config",
        PageCustomizerProductConfig: "page-customizer-product-config",
        //搜索条件
        SearchConditionKey:"search-condition"

    };
    var key = APP_BCGOGO.Module.WebStorage.Key;

    var local = G.localStorage,
        session = G.sessionStorage;

    //local cache
    App.namespace("Module.WebStorage.Local");
    App.Module.WebStorage.Local = {
        getItem: function (key) {
            return local.get(key);
        },

        setItem: function (key, data) {
            local.set(key, data);
            return this;
        },

        removeItem: function (key) {
            local.remove(key);
            return this;
        },

        deeplyClear: function () {
            local.clear();
        },
        clear: function () {
            local
                .remove(key.Menus)
                .remove(key.MenuCurrentData)
                .remove(key.MenuUid)
                .remove(key.PageCustomizerOrderConfig)
                .remove(key.PageCustomizerProductConfig)
                .remove(key.SearchBox);
            return this;
        }
    };

    //session storage
    APP_BCGOGO.namespace("Module.WebStorage.Session");
    APP_BCGOGO.Module.WebStorage.Session = {
        getItem: function (key) {
            return session.get(key);
        },

        setItem: function (key, data) {
            session.set(key, data);
            return this;
        },

        removeItem: function (key) {
            session.remove(key);
            return this;
        },

        deeplyClear: function () {
            session.clear();
            return this;
        },

        clear: function () {
            session
                .remove(key.STATE_KEY);
            return this;
        }
    };

})();

var lStorage = APP_BCGOGO.Module.WebStorage.Local,
    sStorage = APP_BCGOGO.Module.WebStorage.Session,
    storageKey = APP_BCGOGO.Module.WebStorage.Key,
    defaultStorage = lStorage;
