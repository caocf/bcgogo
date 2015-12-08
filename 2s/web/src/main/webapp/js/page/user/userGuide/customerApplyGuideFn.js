userGuide.funLib["CUSTOMER_APPLY_GUIDE"] = {
    //STEP1:start main page
//    _CUSTOMER_APPLY_GUIDE_BEGIN: function () {
//        userGuide.funLib["CUSTOMER_APPLY_GUIDE"]._begin();
//    },
    CUSTOMER_APPLY_GUIDE_BEGIN: function () {
        userGuide.funLib["CUSTOMER_APPLY_GUIDE"]._begin();
    },
    _begin: function () {
        userGuide.clear();
        var me = userGuide,
            currentFlowName = me.currentFlowName,
            currentStepName = me.currentStepName,
            tip = {
                width: 464,
                height: 233
            };
        if (!currentFlowName || !currentStepName) return;
        userGuide.coverAll();
        var startPanel = new App.Module.GuideTipStartPanel();
        guideTipDisplayManager.addChild(startPanel);
        startPanel.show({
            "z-index": startPanel.Z_INDEX.TOP,
            htmlText: '<div style="color:#FFF">'
                + '<p><span style="font-size:20px; margin: 10px;">您有新的关联请求！</span></p><br><br>'
                + '<p><span style="font-size:16px;  margin: 10px;">有客户想与您建立关联，想知道是谁么？来看看关联请求吧！</span></p>'
                + '</div>',
            left: 0.5 * ( $(document).width() - tip.width),
            top: 0.5 * ( $(document).height() - tip.height),
            associated: {
                label: "查看请求",
                "click": function (event) {
                    userGuide.notRemind(userGuide.currentFlowName);
                    APP_BCGOGO.Net.asyncAjax({
                        type: "POST",
                        url: "guide.do?method=nextUserGuideStep",
                        cache: false,
                        dataType: "json",
                        success: function (result) {
                            if (result.success) {
                                userGuideInvoker("CUSTOMER_APPLY_GUIDE_ENTER_MESSAGE");
                            }
                        }
                    });
                }
            }
        });
    },
    //STEP2:查看头部
    _CUSTOMER_APPLY_GUIDE_ENTER_MESSAGE: function () {
        userGuide.funLib["CUSTOMER_APPLY_GUIDE"]["_enter_message"]();
    },
    CUSTOMER_APPLY_GUIDE_ENTER_MESSAGE: function () {
        userGuide.funLib["CUSTOMER_APPLY_GUIDE"]["_enter_message"]();
    },

    _enter_message:function(){
        userGuide.clear();
                var me = userGuide,
                    currentFlowName = me.currentFlowName,
                    currentStepName = me.currentStepName;
                if (!currentFlowName || !currentStepName) return;
                var $target = $("#messageCenterNumber");
                coverExcept['x'] = $target.offset().left - 20;
                coverExcept['y'] = $target.offset().top;
                coverExcept['w'] = $target.width() + 40;
                coverExcept['h'] = $target.height();
                shadow.coverExcept(coverExcept);
                var left = coverExcept.x;
                var top = coverExcept.y + coverExcept.h + 5;
                var panel = new App.Module.GuideTipPanel();
                var cancelButton = new App.Module.GuideTipCancelButton();
                guideTipDisplayManager.addChild(panel);
                guideTipDisplayManager.addChild(cancelButton);
                panel.show({
                    backgroundImageUrl: panel.IMG.L_T_small_blue,
                    top: top,
                    left: left,
                    content: {
                        htmlText: "<span style='font-size:18px;font-weight:bold;'>请将鼠标移入!</span>"
                    }
                });
                cancelButton.show({
                    label: "跳出引导",
                    left: left + 200,
                    top: top + 135,
                    click: function (event) {
                        me.dropFlow("CUSTOMER_APPLY_GUIDE");
                    }
                });
    },

    //查看关联请求
    _CUSTOMER_APPLY_GUIDE_SHOW_APPLY_TIP: function () {
        userGuide.clear();
        var me = userGuide,
            currentFlowName = me.currentFlowName,
            currentStepName = me.currentStepName;
        if (!currentFlowName || !currentStepName) return;
        var $target = $(".bcgogo-messagePopup-request");
        $(".bcgogo-messagePopup-closeButton").hide();
        coverExcept['x'] = $target.offset().left;
        coverExcept['y'] = $target.offset().top;
        coverExcept['w'] = $target.width();
        coverExcept['h'] = $target.height();
        shadow.coverExcept(coverExcept);
        var left = coverExcept.x;
        var top = coverExcept.y + coverExcept.h + 5;
        var panel = new App.Module.GuideTipPanel();
        var cancelButton = new App.Module.GuideTipCancelButton();
        guideTipDisplayManager.addChild(panel);
        guideTipDisplayManager.addChild(cancelButton);
        App.Module.messagePopup.setIsMouseEventEnabled(false);
        panel.show({
            backgroundImageUrl: panel.IMG.L_T_small_blue,
            top: top,
            left: left,
            content: {
                htmlText: "<span style='font-size:18px;font-weight:bold;'>点击后可以查看关联请求!</span>"
            }
        });
        cancelButton.show({
            label: "跳出引导",
            left: left + 200,
            top: top + 135,
            click: function (event) {
                App.Module.messagePopup.setIsMouseEventEnabled(true);
                me.dropFlow("CUSTOMER_APPLY_GUIDE");
                $(".bcgogo-messagePopup-closeButton").show();
            }
        });
    },

    //STEP3:同意或拒绝
    _CUSTOMER_APPLY_GUIDE_SHOW_APPLY: function () {
        userGuide.clear();
        var me = userGuide,
            currentFlowName = me.currentFlowName,
            currentStepName = me.currentStepName;
        if (!currentFlowName || !currentStepName) return;
        var $target = $("#customerApplies .lineDiv");
        if (!$target || $target.length == 0) {
            userGuide.funLib["SUPPLIER_APPLY_GUIDE"].finish();
        }
        $target = $($target[0])
        coverExcept['x'] = $target.offset().left;
        coverExcept['y'] = $target.offset().top;
        coverExcept['w'] = $target.width();
        coverExcept['h'] = $target.height();
        shadow.coverExcept(coverExcept);
        var left = coverExcept.x;
        var top = coverExcept.y + coverExcept.h + 5;
        var panel = new App.Module.GuideTipPanel();
        var cancelButton = new App.Module.GuideTipCancelButton();
        guideTipDisplayManager.addChild(panel);
        guideTipDisplayManager.addChild(cancelButton);
        panel.show({
            backgroundImageUrl: panel.IMG.L_T_big_blue,
            top: top,
            left: left,
            content: {
                top: 60,
                htmlText: "<span style='font-size:18px;font-weight:bold;'>有客户请求与您建立关联，点击同意可建立关联哦！关联后客户可向您在线采购商品呦！</span>"
            }
        });
        cancelButton.show({
            label: "跳出引导",
            left: left + 260,
            top: top + 145,
            click: function (event) {
                me.dropFlow("CUSTOMER_APPLY_GUIDE");
            }
        });
    },

    //STEP4:同意
    _CUSTOMER_APPLY_GUIDE_APPLY_AGREE: function () {
        userGuide.clear();
        var me = userGuide,
            currentFlowName = me.currentFlowName,
            currentStepName = me.currentStepName,
            tip = {
                width: 300,
                height: 160
            },
            left = 0.5 * ( $(document).width() - tip.width) + tip.width - 100,
            top = 0.5 * ( $(document).height() - tip.height) + tip.height;
        if (!currentFlowName || !currentStepName) return;
        var z_Index_Top = getTopJqueryUiDialogZIndex() + 1000;
        var panel = new App.Module.GuideTipPanel();
        var cancelButton = new App.Module.GuideTipCancelButton();
        guideTipDisplayManager.addChild(panel);
        guideTipDisplayManager.addChild(cancelButton);
        panel.show({
            backgroundImageUrl: panel.IMG.L_T_big_blue,
            top: top,
            left: left,
            "z-index": z_Index_Top,
            content: {
                top: 60,
                htmlText: "<span style='font-size:18px;font-weight:bold;'>恭喜您已成功与他建立关联！您可查看该客户详细信息！请点击确定</span>"
            }
        });
        cancelButton.show({
            label: "跳出引导",
            left: left + 300,
            top: top + 135,
            "z-index": z_Index_Top,
            click: function (event) {
                me.dropFlow("CUSTOMER_APPLY_GUIDE");
            }
        });
    },
    _CUSTOMER_APPLY_GUIDE_APPLY_REJECT: function () {
        $.cookie("currentStepName", null);
        APP_BCGOGO.Net.asyncAjax({
            type: "POST",
            url: "guide.do?method=updateCurrentUserGuideFlowFinished",
            data: {flowName: "CUSTOMER_APPLY_GUIDE"},
            cache: false,
            dataType: "json",
            success: function (result) {
                userGuide.finishFlow();
                userGuide.load();
            }
        });
    },
    CUSTOMER_APPLY_GUIDE_SHOW_APPLY_NAVIGATE: function () {
        userGuide.funLib["CUSTOMER_APPLY_GUIDE"]._navigate();
    },
    _CUSTOMER_APPLY_GUIDE_SHOW_APPLY_NAVIGATE: function () {
        userGuide.funLib["CUSTOMER_APPLY_GUIDE"]._navigate();
    },
    _navigate: function () {
        userGuide.clear();
        var me = userGuide,
            currentFlowName = me.currentFlowName,
            currentStepName = me.currentStepName;
        if (!currentFlowName || !currentStepName) return;
        var $target = $("#customerApplyRequestDiv");
        coverExcept['x'] = $target.offset().left;
        coverExcept['y'] = $target.offset().top;
        coverExcept['w'] = $target.width() + 16;
        coverExcept['h'] = $target.height();
        shadow.coverExcept(coverExcept);
        var left = coverExcept.x;
        var top = coverExcept.y + coverExcept.h + 5;
        var panel = new App.Module.GuideTipPanel();
        var cancelButton = new App.Module.GuideTipCancelButton();
        guideTipDisplayManager.addChild(panel);
        guideTipDisplayManager.addChild(cancelButton);
        panel.show({
            backgroundImageUrl: panel.IMG.L_T_small_blue,
            top: top,
            left: left,
            content: {
                top: 60,
                htmlText: "<span style='font-size:18px;font-weight:bold;'>请点击客户关联请求!</span>"
            }
        });
        cancelButton.show({
            label: "跳出引导",
            left: left + 200,
            top: top + 135,
            click: function (event) {
                me.dropFlow("CUSTOMER_APPLY_GUIDE");
            }
        });
    },

    //STEP5:
    CUSTOMER_APPLY_GUIDE_SHOW_HANDLED_APPLY: function () {
        userGuide.clear();
        var me = userGuide,
            currentFlowName = me.currentFlowName,
            currentStepName = me.currentStepName;
        if (!currentFlowName || !currentStepName) return;
        var $target = $("a[invitestatus=handledCustomer]");
        coverExcept['x'] = $target.offset().left;
        coverExcept['y'] = $target.offset().top;
        coverExcept['w'] = $target.width();
        coverExcept['h'] = $target.height();
        shadow.coverExcept(coverExcept);
        var left = coverExcept.x;
        var top = coverExcept.y + coverExcept.h + 5;
        var panel = new App.Module.GuideTipPanel();
        var cancelButton = new App.Module.GuideTipCancelButton();
        guideTipDisplayManager.addChild(panel);
        guideTipDisplayManager.addChild(cancelButton);
        panel.show({
            backgroundImageUrl: panel.IMG.L_T_small_blue,
            top: top,
            left: left,
            content: {
                htmlText: "<span style='font-size:18px;font-weight:bold;'>请点击已处理请求!</span>"
            }
        });
        cancelButton.show({
            label: "跳出引导",
            left: left + 200,
            top: top + 135,
            click: function (event) {
                me.dropFlow("CUSTOMER_APPLY_GUIDE");
            }
        });
    },
    _CUSTOMER_APPLY_GUIDE_SHOW_HANDLED_APPLY_LIST: function () {
        userGuide.clear();
        var me = userGuide,
            currentFlowName = me.currentFlowName,
            currentStepName = me.currentStepName;
        if (!currentFlowName || !currentStepName) return;
        var $target = $("#customerApplies .ACCEPTED");
        if (!$target || $target.length == 0) {
            userGuide.funLib["CUSTOMER_APPLY_GUIDE"]._success();
            return;
        }
        $target = $("#customerApplies");
        coverExcept['x'] = $target.offset().left;
        coverExcept['y'] = $target.offset().top;
        coverExcept['w'] = $target.width();
        coverExcept['h'] = $target.height();
        shadow.coverExcept(coverExcept);
        var left = coverExcept.x;
        var top = coverExcept.y - 183;
        var panel = new App.Module.GuideTipPanel();
        var cancelButton = new App.Module.GuideTipCancelButton();
        guideTipDisplayManager.addChild(panel);
        guideTipDisplayManager.addChild(cancelButton);
        panel.show({
            backgroundImageUrl: panel.IMG.R_B_big_blue,
            top: top,
            left: left,
            content: {
                top: 57,
                htmlText: "<span style='font-size:18px;font-weight:bold;'>查找到您刚同意请求的客户，点击客户名称后可查看他的详细信息哦!</span>"
            }
        });
        cancelButton.show({
            label: "跳出引导",
            left: left + 270,
            top: top + 135,
            click: function (event) {
                me.dropFlow("CUSTOMER_APPLY_GUIDE");
            }
        });
    },
    CUSTOMER_APPLY_GUIDE_SUCCESS: function () {
        userGuide.funLib["CUSTOMER_APPLY_GUIDE"]._success();
    },
    _CUSTOMER_APPLY_GUIDE_SUCCESS: function () {
        userGuide.funLib["CUSTOMER_APPLY_GUIDE"]._success();
    },
    _success: function () {
        userGuide.clear();
        var me = userGuide,
            currentFlowName = me.currentFlowName,
            currentStepName = me.currentStepName,
            tip = {
                width: 464,
                height: 233
            };
        if (!currentFlowName || !currentStepName) return;
        userGuide.coverAll();
        var startPanel = new App.Module.GuideTipStartPanel();
        guideTipDisplayManager.addChild(startPanel);
        startPanel.show({
            "z-index": startPanel.Z_INDEX.TOP,
            htmlText: '<div>'
                + '<p><span style="font-size:20px; margin: 10px;"></span></p><br><br>'
                + '<p><span style="font-size:18px;  margin: 10px;font-weight:bold;color: #FFFFFF;">恭喜您已成功处理完该条请求！您可查看该客户详细信息！</span></p>'
                + '</div>',
            left: 0.5 * ( $(document).width() - tip.width),
            top: 0.5 * ( $(document).height() - tip.height),
            associated: {
                label: "我知道了",
                "click": function (event) {
                    userGuide.clear();
                    APP_BCGOGO.Net.asyncAjax({
                        type: "POST",
                        url: "guide.do?method=updateCurrentUserGuideFlowFinished",
                        data:{flowName:"CUSTOMER_APPLY_GUIDE"},
                        cache: false,
                        dataType: "json",
                        success: function (result) {
                            if (result.success) {
                                userGuide.clear();
                                userGuide.load();
                            }
                        }
                    });
                }
            }
        });
    }
}