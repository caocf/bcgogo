(function () {
    APP_BCGOGO.namespace("UserGuide");
    APP_BCGOGO.UserGuide = {
        currentStepName: '',
        nextStepName: '',
        currentFlowName: '',
        hasUserGuide: '',
        isContinueGuide: '', //是否继续引导
        currentStepStatus: '',
        currentPageIncludeGuideStep: '',
        currentPage: '',
        currentStepIsHead: '',
        url: '', //进入这个step的Url
        funLib: {},
        initUserGuideValue: function () {
            var foo = APP_BCGOGO.UserGuide;
            foo.currentStepName = $.cookie("currentStepName");
            foo.currentFlowName = $.cookie("currentFlowName");
            foo.currentStepStatus = $.cookie("currentStepStatus");
            foo.nextStepName = $.cookie("nextStepName");
            foo.currentStepIsHead = $.cookie("currentStepIsHead");
            foo.isContinueGuide = $.cookie("isContinueGuide");
            foo.hasUserGuide = $.cookie("hasUserGuide");
            foo.url = decodeURI($.cookie("url"));
        },
        dropFlow: function (flowName) {
            if (!flowName) return;
            var excludeFlowName = $.cookie("excludeFlowName");
            if (excludeFlowName && !excludeFlowName.search(flowName) > -1) {
                excludeFlowName += "," + flowName;
            } else if (!excludeFlowName) {
                excludeFlowName = flowName;
            }
            if (excludeFlowName)$.cookie("excludeFlowName", excludeFlowName);
            userGuide.clearFlow();
        },
        continueFlow: function () {
            if (userGuide.isContinueGuide != "YES")return;
            userGuide.clear();
            userGuide.coverAll();
            var contentHtml = '<div style="color:#FFF">';
            if (userGuide.currentFlowName == "CONTRACT_CUSTOMER_GUIDE") {
                contentHtml += '<p><span style="font-size:20px;  margin: 10px;">您上次登录未完成所有的关联客户操作引导！</span></p><br>';
            } else if (userGuide.currentFlowName == "CONTRACT_SUPPLIER_GUIDE") {
                contentHtml += '<p><span style="font-size:20px;  margin: 10px;">您上次登录未完成所有的关联供应商操作引导！</span></p><br>';
            } else if (userGuide.currentFlowName == "PRODUCT_ONLINE_GUIDE") {
                contentHtml += '<p><span style="font-size:20px;  margin: 10px;">您尚未完成所有的上架操作引导！</span></p><br>';
            } else if (userGuide.currentFlowName == "PRODUCT_PRICE_GUIDE") {
                contentHtml += '<p><span style="font-size:20px;  margin: 10px;">您上次登录未完成所有的供应商报价操作引导！</span></p><br>';
            } else if (userGuide.currentFlowName == "SUPPLIER_APPLY_GUIDE") {
                contentHtml += '<p><span style="font-size:20px;  margin: 10px;">您上次登录未完成请求处理操作引导！</span></p><br>';
            } else if (userGuide.currentFlowName == "CUSTOMER_APPLY_GUIDE") {
                contentHtml += '<p><span style="font-size:20px;  margin: 10px;">您上次登录未完成请求处理操作引导！</span></p><br>';
            } else if (userGuide.currentFlowName == "CONTRACT_MESSAGE_NOTICE") {
                contentHtml += '<p><span style="font-size:20px;  margin: 10px;">您上次登录未完成消息处理操作引导！</span></p><br>';
            } else {
                G.error("新手引导,继续未完成的引导出错！");
            }
            contentHtml += '<br><p><span style="font-size:16px; margin: 10px;">是否要继续进行引导？</span></p></div>'

            var currentFlowName = userGuide.currentFlowName;
            var currentStepName = userGuide.currentStepName;
            if (!currentFlowName || !currentStepName) return;
            var startPanel = new App.Module.GuideTipStartPanel();
            guideTipDisplayManager.addChild(startPanel);
            var tip = {
                width: 464,
                height: 233
            };
            var left = 0.5 * ( $(document).width() - tip.width);
            var top = 0.5 * $(document).height() - tip.height;
            startPanel.show({
                htmlText: contentHtml,
                left: left,
                top: top,
                associated: {
                    label: "继续",
                    "click": function (event) {
                        userGuide.notRemind(userGuide.currentFlowName);
                        var url = userGuide.url;
                        if (userGuide.url) {
                            $.cookie("isContinueGuide", "NO");
                            $.cookie("keepCurrentStep", "YES");
                            window.location.href = url;
                        } else {
                            G.error("新手引导出错（userGuideMain.js）：continueFlow");
                        }
                    }
                },
                drop: {
                    label: "跳出引导",
                    "click": function (event) {
                        userGuide.notRemind(userGuide.currentFlowName);
                        userGuide.clear();
                        APP_BCGOGO.Net.asyncAjax({
                            type: "POST",
                            url: "guide.do?method=skipUserGuideFlow",
                            data: {flowName: userGuide.currentFlowName},
                            cache: false,
                            dataType: "json",
                            success: function (result) {
                                if (result.success) {
                                    userGuide.clear();
                                    userGuide.clearFlow();
                                }
                            }
                        });
                    }
                }
            });
        },
        finishFlow: function (step) {
            userGuide.clear();
            userGuide.initUserGuideValue();
            $.cookie("keepCurrentStep", "YES");
            if (step) userGuideInvoker(step);
        },
        invoker: function (params) {
            if (userGuide.isContinueGuide == "YES") {
                userGuide.load();
                return;
            }
            var me = APP_BCGOGO.UserGuide;
            me.initUserGuideValue();
            if (!me.currentFlowName || !me.currentStepName || !me.funLib[me.currentFlowName])return;
            var initFunction = me.funLib[me.currentFlowName][ "_" + me.currentStepName];
            if (initFunction && typeof initFunction === "function" && params.search(me.currentStepName) != -1) {
                initFunction();
            }
        },
        caller: function (step, flow, excludeStepNames, includeStep) {
            var me = APP_BCGOGO.UserGuide;
            me.initUserGuideValue();
            if (userGuide.hasUserGuide != "YES")return;
            if (userGuide.isContinueGuide == "YES") {
                userGuide.load();
                return;
            }
            if (excludeStepNames && excludeStepNames.search(userGuide.currentStepName) != -1)return;

            //包含指定步骤
            if (!step || (includeStep && includeStep.search(me.currentStepName) == -1))return;
            if (!flow) {
                flow = me.currentFlowName;
            }
            if (!flow || !me.funLib[flow]) return;
            var initFunction = me.funLib[flow][ "_" + step];
            if (initFunction && typeof initFunction === "function") {
                initFunction();
            }
        },
        clear: function () {
            clearTimes++;
            if (clearTimes > 1) {
                G.info("clearTimes"+clearTimes);
            }
            G.info("clear user guide!");
            App.Module.guideTipDisplayManager.removeAllFromPage();
            shadow.clear();
        },
        clearFlow: function () {
            $.cookie("currentStepName", null);
            $.cookie("currentFlowName", null);
            $.cookie("currentStepStatus", null);
            $.cookie("nextStepName", null);
            $.cookie("currentStepIsHead", null);
            $.cookie("isContinueGuide", null);
            $.cookie("url", null);
            userGuide.clear();
        },
        coverAll: function () {
            shadow.coverAll({background: {color: "#000", opacity: 0.5}});
        },
        load: function () {
            var me = APP_BCGOGO.UserGuide;
            if (me.currentPage == "main" || me.currentPage == "goodsBuyOnline")return;
            me.initUserGuideValue();
            //页面ready的时候自动加载的新手指引 需要在  每张页面配置currentPageIncludeGuideStep，ajax加载的不需要配置
            if (me.currentStepName
                && me.isContinueGuide != "YES"
                && me.funLib[me.currentFlowName]
                && me.funLib[me.currentFlowName][me.currentStepName]) {
//                && me.currentPageIncludeGuideStep
//                && me.currentPageIncludeGuideStep.search(me.currentStepName) > -1
                var initFunction = me.funLib[me.currentFlowName][me.currentStepName];
                if (initFunction && typeof initFunction === "function") {
                    initFunction();
                }
                //首页出现上次指引未完成是否继续的操作
            } else if (
                me.currentStepIsHead == "FALSE"
                    && me.isContinueGuide == "YES"
                    && me.url
                    && me.currentStepName
                ) {
                me.continueFlow();
            }
        },
        notRemind: function (currentFlowName, successHandler) {
            if ($(".checkBox_notRemind").eq(0) && $(".checkBox_notRemind").eq(0).find("input[type='checkbox']").attr("checked")) {
                APP_BCGOGO.Net.syncAjax({
                    type: "POST",
                    url: "guide.do?method=notRemind",
                    cache: false,
                    dataType: "json",
                    data: {flowName: currentFlowName},
                    success: successHandler
                });
            }
        }
    }
})();
var clearTimes = 0;
//$(window).load(function () {
//    userGuide.load();
//});
var userGuide = APP_BCGOGO.UserGuide,
    userGuideInvoker = userGuide.invoker,
    guideTipDisplayManager = APP_BCGOGO.Module.guideTipDisplayManager,
    shadow = APP_BCGOGO.Module.shadow,
    coverExcept = {
        isBorderFlicker: true,
        background: {color: "#000", opacity: 0.5},
        border: {color: "red", width: 3},
        hasBorder: true
    }, cover = {
        isBorderFlicker: true,
        border: {color: "red", width: 3},
        isFullTransparent: true,
        hasBorder: false
    };
function getTopJqueryUiDialogZIndex() {
    var maxZ_index = 1000;
    $(".ui-dialog").each(function () {
        if ($(this).css("display") != "none" && $(this).zIndex() > maxZ_index) {
            maxZ_index = $(this).zIndex();
        }
    });
    return maxZ_index;
}