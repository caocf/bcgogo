$(document).ready(function () {
    App.Menu.Function = {
        doMenu: function (uid) {
            if (uid) {
                var menu = getRoot(uid);
                if (!$.isEmptyObject(menu)) {
                    defaultStorage.setItem(storageKey.MenuRoad, JSON.stringify(menu['road']));
                    defaultStorage.setItem(storageKey.MenuCurrentData, JSON.stringify(menu['menuCurrentData']));
                    defaultStorage.removeItem(storageKey.MenuUid);
                }
            }
        },
        doNavigate: function (navigate,home) {
            var navigates = navigate.split(".");
            var div = '<div class="bcgogo-menubar"><ul class="Jmenu">';
            if(G.isNotEmpty(home)){
                div += '<li class="Jcontent" href="'+home.href+'">' + home.label + '</li>';
                div += '<li class="Jarrow">&gt;</li>';
            }

            for (var i = 0; i < navigates.length; i++) {
                div += '<li class="Jcontent" href="">' + navigates[i] + '</li>';
                if (i < navigates.length - 1) div += '<li class="Jarrow">&gt;</li>';
            }
            div += '</ul><div style="clear:both;float:none;" class="Jclear"></div></div>';
            $("#menu-navigate").html(div);
            $("#menu-navigate .Jcontent").bind("click", function (event) {
                if(G.isNotEmpty($(this).attr("href"))){
                    window.location.href = $(this).attr("href");
                }
            });
        }
    };

    if (!defaultStorage.getItem(storageKey.Menus)) {
        APP_BCGOGO.Net.syncAjax({
            type: "POST",
            url: "menu.do?method=getMenu",
            cache: false,
            dataType: "json",
            success: function (result) {
                if (result && result.success) {
                    defaultStorage.setItem(storageKey.Menus, result['data']);
                } else {
                    G.error("menu.do?method=getMenu responsed successful, but data is null......");
                }
            },
            error: function () {
                G.error("menu.do?method=getMenu error response!");
            }
        });
    }
    var menuPanel = new App.Module.MenuPanel();
    var menuDataProxy = new App.Module.MenuDataProxy();
    var menubar = new App.Module.MenuBar();

    try {
        var menus = $.parseJSON(defaultStorage.getItem(storageKey.Menus));
        if (APP_BCGOGO.Menu.Excludes.indexOf(App.page) == -1) {
            //首先判断url中有没有menu-uid
            if (GLOBAL.Util.getUrlParameter(storageKey.MenuUid)) {
                App.Menu.Function.doMenu(GLOBAL.Util.getUrlParameter(storageKey.MenuUid));
            } else if (defaultStorage.getItem(storageKey.MenuUid)) {
                App.Menu.Function.doMenu(defaultStorage.getItem(storageKey.MenuUid));
            }
            var lastItem;
            if (defaultStorage.getItem(storageKey.MenuCurrentItem)) {
                lastItem = {"label":defaultStorage.getItem(storageKey.MenuCurrentItem)};
                defaultStorage.removeItem(storageKey.MenuCurrentItem);
            }
            var menuRoad = $.parseJSON(defaultStorage.getItem(storageKey.MenuRoad));
            menubar.show({
                data: $.parseJSON(defaultStorage.getItem(storageKey.MenuCurrentData)),
                home:{"href":"user.do?method=createmain","label":"首页"},
                road: menuRoad,
                lastItem:lastItem,
                config: {
                    selector: "#menu-navigate",
                    autoTurnning: false,
                    onSelect: function (road, data, uid) {
                        removeMenu();
                        defaultStorage.setItem(storageKey.MenuRoad, JSON.stringify(road));
                        defaultStorage.setItem(storageKey.MenuCurrentData, JSON.stringify(data));
                        var leaf = getLeaf(road);
                        if (leaf.href) {
                            window.location = leaf.href;
                        }
                    }
                }
            });
            window._$ = menubar._$;
        }
    } catch (e) {
        G.error(e);
    }

    $("[action-type=menu-click]").click(function (event) {
        try {
            var $this = $(this),
                menuName = $this.attr("menu-name");
            removeMenu();
            var menu = getRoot(menuName);
            defaultStorage.setItem(storageKey.MenuRoad, JSON.stringify(menu['road']));
            defaultStorage.setItem(storageKey.MenuCurrentData, JSON.stringify(menu['menuCurrentData']));
            if ($this.attr("url")) {
                window.location = $this.attr("url");
            } else if ($this.attr("callback")) {
                eval($this.attr("callback"))
            }
        } catch (e) {
            G.error(e);
        }
    });

    $("[action-type=toSysAnnouncement],#toHelper,#messageCenterNumber").click(function () {
        var url = $(this).attr("url");
        removeMenu();
        window.location = url;
    });

    $("#mainModule").find('div[menu-name]').bind("click", function (event) {
        var $_this = $(this),
            menuName = $_this.attr("menu-name"),
            url = $_this.attr("url"),
            keepCurrentStep = $_this.attr("keep-current-step");
        $.cookie("keepCurrentStep", keepCurrentStep);
        if (menus && menus[menuName]) {
            defaultStorage.setItem(storageKey.MenuRoad, JSON.stringify(menuDataProxy.getRoad(menus[menuName], menuName, true)));
            defaultStorage.setItem(storageKey.MenuCurrentData, JSON.stringify(menus[menuName]));
        }
        if($_this.attr("open-target")=="_blank"){
            openWindow(url);
        }else{
            openOrAssign(url);
        }

    });
    var menuLis = $("#head_menu").children('li');
    if (menuRoad) {
        for (var i = 0; i < menuLis.length; i++) {
            if (menuRoad.uid == $(menuLis[i]).attr("menu-name")) {
                $(menuLis[i]).addClass("icon_hover_font");
            } else {
                $(menuLis[i]).removeClass("icon_hover_font");
            }
        }
    }
    $(menuLis)
        .click(function (event) {
            var menuName = $(this).attr("menu-name");
            var url = $(this).attr("url");
            if (url) {
                removeMenu();
                if (menus[menuName]) {
                    defaultStorage.setItem(storageKey.MenuRoad, JSON.stringify(menuDataProxy.getRoad(menus[menuName], menuName, true)));
                    defaultStorage.setItem(storageKey.MenuCurrentData, JSON.stringify(menus[menuName]));
                }
                if($(this).attr("open-target")=="_blank"){
                    openWindow(url);
                }else{
                    openOrAssign(url);
                }
            }
        })
        .bind("mouseenter", function (event) {
            var headName = $(this).attr("menu-name");
            for (var i = 0; i < menuLis.length; i++) {
                if (headName != $(menuLis[i]).attr("menu-name")) {
                    $(menuLis[i]).removeClass("icon_hover");
                }
            }
            $(this).addClass("icon_hover");
            menuPanel.remove();
            if (menus[headName]) {
                menuPanel.show({
                    data: menus[headName],
                    config: {
                        autoTurnning: false,
                        column: 3,
                        align: "left",
                        manualPosition: false,
                        hookSelector: $(this),
                        "z-index": 20,
                        "onSelect": function (road, data, uid, event) {
                            try {
                                removeMenu();
                                defaultStorage.setItem(storageKey.MenuRoad, JSON.stringify(road));
                                defaultStorage.setItem(storageKey.MenuCurrentData, JSON.stringify(data));
                                var leaf = getLeaf(road);
                                if (leaf.href) {
                                    if(headName == 'INQUIRY_CENTER') {
                                        window.open(leaf.href,"_blank");
                                    } else{
                                        window.location = leaf.href;
                                    }
                                }
                            } catch (e) {
                                G.error(e);
                            }
                        }
                    }
                });
            }
        })
        .bind("mouseleave", function (event) {
            for (var i = 0; i < menuLis.length; i++) {
                $(this).removeClass("icon_hover");
            }
        });
    function getLeaf(road) {
        if (road.item) {
            return getLeaf(road.item[0]);
        } else {
            return road;
        }
    }

    function getRoot(menuName) {
        var currentRoot;
        for (var r in menus) {
            currentRoot = menuDataProxy.getRoad(menus[r], menuName, true);
            if (currentRoot) {
                return {road: currentRoot, menuCurrentData: menus[r]};
            }
        }
        return null;
    }


    function removeMenu() {
        defaultStorage.removeItem(storageKey.MenuUid);
        defaultStorage.removeItem(storageKey.MenuRoad);
        defaultStorage.removeItem(storageKey.MenuCurrentData);
        defaultStorage.removeItem(storageKey.MenuCurrentItem);
    }

});