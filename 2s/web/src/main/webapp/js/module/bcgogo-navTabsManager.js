/**
 * Created with IntelliJ IDEA.
 * User: Jimuchen
 * Date: 13-10-29
 * Time: 下午5:40
 * To change this template use File | Settings | File Templates.
 */
(function () {
    App.namespace("Module.navTabsManager");

    var _storage = GLOBAL.localStorage;
    var _window = window;
    var STORAGE_KEY_ALL_TABS = "bcgogo_all_tabs",       //所有BCGOGO标签页，存入TABS
        STORAGE_KEY_ACTIVE_TAB = "bcgogo_active_tab",   //当前激活状态的BCGOGO标签页
        TABS = {};      //key: tab名, value: tab创建时间

    var navTabsManager = function () {

    };

    navTabsManager.method("_setWindowName", function () {
        _window.name = "BCGOGO_TAB_" + G.generateUUID();
    });

    navTabsManager.method("getWindowName", function(){
        if (_window == null || _window == undefined) {
            throw Error("no window found");
        }
        if (G.isEmpty(_window.name)) {
            this._setWindowName();
        }
        return _window.name;
    });

    navTabsManager.method("addTab", function () {
        this.getAllTabs();
        var windowName = this.getWindowName();
        if (G.isEmpty(TABS[windowName])) {
            TABS[windowName] = new Date().getTime();
        }
        this.setAllTabs(TABS);
    });

    navTabsManager.method("removeTab", function () {
        this.getAllTabs();
        var windowName = this.getWindowName();
        if (!G.isEmpty(TABS[windowName])) {
            delete TABS[windowName];
        }
        this.setAllTabs(TABS);
    });

    navTabsManager.method("getAllTabs", function () {
        var stringJson = _storage.get(STORAGE_KEY_ALL_TABS);
        if (G.isEmpty(stringJson)) {
            TABS = {};
        } else {
            TABS = JSON.parse(stringJson);
        }
        return TABS;
    });

    navTabsManager.method("setAllTabs", function (_TABS) {
        _storage.set(STORAGE_KEY_ALL_TABS, JSON.stringify(_TABS));
    });

    navTabsManager.method("setActiveTab", function(){
        var storageActiveTab = this.getActiveTab();
        if(G.isEmpty(storageActiveTab) || storageActiveTab != this.getWindowName()){
            _storage.set(STORAGE_KEY_ACTIVE_TAB, this.getWindowName());
        }
    });

    navTabsManager.method("getActiveTab", function(){
        return _storage.get(STORAGE_KEY_ACTIVE_TAB);
    });

    navTabsManager.method("removeActiveTab", function(){
        if(G.isEmpty(this.getActiveTab)){
            return;
        }
        if(this.getActiveTab() == this.getWindowName()){
            _storage.remove(STORAGE_KEY_ACTIVE_TAB);
        }
    });


    navTabsManager.method("init", function () {
        var that = this;
        $(window).bind("load", function () {
            that.addTab();
            that.setActiveTab();
            GLOBAL.warning("window opened: " + window.name);
        });

        $(window).bind("beforeunload unload", function () {
            that.removeTab();
            that.removeActiveTab();
            GLOBAL.warning("window closed: " + window.name);
        });

        $(document).bind("mousemove keyup", function () {
            that.setActiveTab();
        });
        return this;
    });

    App.Module.navTabsManager = new navTabsManager().init();
}());

