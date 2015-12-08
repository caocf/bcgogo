userGuide.funLib["CONTRACT_SUPPLIER_GUIDE"] = {
    //start main page
    CONTRACT_SUPPLIER_GUIDE_BEGIN: function () {
        userGuide.clear();
        var me = APP_BCGOGO["UserGuide"],
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
            htmlText: '<div style="color:#FFF">'
                + '<p><span style="font-size:20px;  margin: 10px;font-weight:bold;line-height:30px;">您所在的地区有供应商正在使用一发软件！</span></p>'
                + '<p><span style="font-size:16px;  margin: 10px;line-height:30px;">您想查看最新的商品吗？</span></p>'
                + '<p><span style="font-size:16px;  margin: 10px;line-height:30px;">您想了解最新的采购报价吗？</span></p>'
                + '<p><span style="font-size:16px;  margin: 10px;line-height:30px;">您想降低采购成本吗？</span></p>'
                + '<p><span style="font-size:16px;  margin: 10px;line-height:30px;">那么先与他们建立关联吧？</span></p>'
                + '</div>',
            left: 0.5 * ( $(document).width() - tip.width),
            top: 0.5 * ( $(document).height() - tip.height),

            associated: {
                label: "关联",
                "click": function (event) {
                    userGuide.notRemind(userGuide.currentFlowName);
                    APP_BCGOGO.Net.asyncAjax({
                        type: "POST",
                        url: "guide.do?method=nextUserGuideStep",
                        cache: false,
                        dataType: "json",
                        success: function (result) {
                            if (result.success) {
                                userGuide.funLib["CONTRACT_SUPPLIER_GUIDE"]["CONTRACT_SUPPLIER_GUIDE_SUPPLIER_DATA"]();
                            }
                        }
                    });

                }
            }
        });
    },
    CONTRACT_SUPPLIER_GUIDE_SUPPLIER_DATA: function () {
        userGuide.clear();
        hideAnnouncement();
        var me = APP_BCGOGO["UserGuide"],
            currentFlowName = me.currentFlowName,
            currentStepName = me.currentStepName,
            tip = {
                width: 464,
                height: 233
            };
        if (!currentFlowName || !currentStepName) return;
        var $target = $("#supplierManagerLi");
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
                htmlText: "<span style='font-size:18px;font-weight:bold;'>点击进入供应商管理!</span>"
            }
        });
        cancelButton.show({
            label: "跳出引导",
            left: left + 200,
            top: top + 135,
            click: function (event) {
                me.dropFlow("CONTRACT_SUPPLIER_GUIDE");
            }
        });
    },
    CONTRACT_SUPPLIER_GUIDE_RECOMMEND_SUPPLIER: function () {
        userGuide.clear();
        var me = APP_BCGOGO["UserGuide"],
            currentFlowName = me.currentFlowName,
            currentStepName = me.currentStepName,
            tip = {
                width: 464,
                height: 233
            };
        if (!currentFlowName || !currentStepName) return;
        var $target = $("#applierSupplierA");
        coverExcept['x'] = $target.offset().left;
        coverExcept['y'] = $target.offset().top;
        coverExcept['w'] = $target.width();
        coverExcept['h'] = $target.height();
        shadow.coverExcept(coverExcept);
        var left = coverExcept.x + 16;
        var top = coverExcept.y + coverExcept.h;
        var panel = new App.Module.GuideTipPanel();
        var cancelButton = new App.Module.GuideTipCancelButton();
        guideTipDisplayManager.addChild(panel);
        guideTipDisplayManager.addChild(cancelButton);
        panel.show({
            backgroundImageUrl: panel.IMG.L_T_small_blue,
            top: top,
            left: left,
            content: {
                htmlText: "<span style='font-size:18px;font-weight:bold;'>点击进入推荐供应商!</span>"
            }
        });
        cancelButton.show({
            label: "跳出引导",
            left: left + 200,
            top: top + 135,
            click: function (event) {
                me.dropFlow("CONTRACT_SUPPLIER_GUIDE");
            }
        });
    },
    //申请单个关联
    _CONTRACT_SUPPLIER_GUIDE_SINGLE_APPLY: function () {
        userGuide.clear();
        var $target = $("#applySupplierData .showSupplierGuideSingleApply"),
            me = APP_BCGOGO.UserGuide,
            currentFlowName = me.currentFlowName,
            currentStepName = me.currentStepName,
            tip = {
                width: 464,
                height: 233
            };
        if ($target.length == 0)return;
        if (!currentFlowName || !currentStepName) return;
        coverExcept['x'] = $target.offset().left - 5;
        coverExcept['y'] = $target.offset().top - 6;
        coverExcept['w'] = $target.width() + 10;
        coverExcept['h'] = $target.height() + 11.5;
        shadow.coverExcept(coverExcept);
        var panel = new App.Module.GuideTipPanel();
        var cancelButton = new App.Module.GuideTipCancelButton();
        guideTipDisplayManager.addChild(panel);
        guideTipDisplayManager.addChild(cancelButton);
        var left = coverExcept.x + 0.5 * coverExcept.w - 170;
        var top = coverExcept.y + coverExcept.h;
        panel.show({
            backgroundImageUrl: panel.IMG.R_T_small_blue,
            left: coverExcept.x - coverExcept.w - 50,
            top: coverExcept.y + 30,
            content: {
                htmlText: "<span style='font-size:18px;font-weight:bold;'>请点击可申请建立关联!</span>"
            }
        });
        cancelButton.show({
            label: "跳出引导",
            left: left + 200,
            top: top + 135,
            click: function (event) {
                me.dropFlow("CONTRACT_SUPPLIER_GUIDE");
            }
        });
    },
    _CONTRACT_SUPPLIER_GUIDE_SINGLE_APPLY_SUCCESS:function(){
      if(($("#applySupplierData .applySupplier").length - $("#applySupplierData .OPPOSITES_PENDING").length)>0){
         userGuide.funLib["CONTRACT_SUPPLIER_GUIDE"]["_CONTRACT_SUPPLIER_GUIDE_SINGLE_APPLY_SUCCESS_HAVE_BATCH_APPLY"]();
        }else{
          userGuide.funLib["CONTRACT_SUPPLIER_GUIDE"]["_CONTRACT_SUPPLIER_GUIDE_SINGLE_APPLY_SUCCESS_NO_BATCH_APPLY"]();
      }
    },
     //可以批量申请
    _CONTRACT_SUPPLIER_GUIDE_SINGLE_APPLY_SUCCESS_HAVE_BATCH_APPLY: function () {
        userGuide.clear();
        var $target = $("#applySupplierData .gray_color"),
            size = $("#applySupplierData tr").size() ,
            me = APP_BCGOGO.UserGuide,
            currentFlowName = me.currentFlowName,
            currentStepName = me.currentStepName,
            tip = {
                width: 464,
                height: 233
            };
        if ($target.length == 0 || size == 0)return;
        if (!currentFlowName || !currentStepName) return;
        coverExcept['x'] = $target.offset().left - 5;
        coverExcept['y'] = $target.offset().top - 6;
        coverExcept['w'] = $target.width() + 10;
        coverExcept['h'] = $target.height() + 11.5;
        shadow.coverExcept(coverExcept);

        cover['x'] = $target.offset().left - 5;
        cover['y'] = $target.offset().top - 6;
        cover['w'] = $target.width() + 10;
        cover['h'] = ($target.height() + 11.5) * (size - 1);
        shadow.cover(cover);

        var currentPanel = new App.Module.GuideTipPanel();
        var nextPanel = new App.Module.GuideTipPanel();
        var cancelButton = new App.Module.GuideTipCancelButton();
        var tryButton = new App.Module.GuideTipOkButton();
        var skipButton = new App.Module.GuideTipOkButton();

        guideTipDisplayManager.addChild(nextPanel);
        guideTipDisplayManager.addChild(currentPanel);
        guideTipDisplayManager.addChild(tryButton);
        guideTipDisplayManager.addChild(cancelButton);
        guideTipDisplayManager.addChild(skipButton);
        currentPanel.show({
            backgroundImageUrl: currentPanel.IMG.R_B_small_blue,
            left: coverExcept.x - coverExcept.w - 50,
            top: coverExcept.y - 135,
            content: {
                htmlText: "<span style='font-size:16px;'>关联申请已提交，对方同意后，将建立关联!</span>"
            }
        });
        nextPanel.show({
            backgroundImageUrl: currentPanel.IMG.R_T_small_green,
            left: coverExcept.x - coverExcept.w - 50,
            top: coverExcept.y + 35,
            content: {
                htmlText: "<span style='font-size:14px;'>您还可以尝试批量与供应商申请建立关联：</span>"
            }
        });
        tryButton.show({
            left: coverExcept.x - coverExcept.w + 5,
            top: coverExcept.y + 123,
            label: "马上尝试",
            width: 50,
            click: function (event) {
                APP_BCGOGO.Net.asyncAjax({
                    type: "POST",
                    url: "guide.do?method=nextUserGuideStep",
                    cache: false,
                    dataType: "json",
                    success: function (result) {
                        if (result.success) {
                            userGuideInvoker("CONTRACT_SUPPLIER_GUIDE_BATCH_APPLY");
                        }
                    }
                });
            }
        });
        skipButton.show({
            left: coverExcept.x - coverExcept.w + 70,
            top: coverExcept.y + 123,
            width: 50,
            label: "跳过",
            click: function (event) {
                $.cookie("currentStepName", null);
                APP_BCGOGO.Net.asyncAjax({
                    type: "POST",
                    url: "guide.do?method=updateCurrentUserGuideFlowFinished",
                    data: {flowName: "CONTRACT_SUPPLIER_GUIDE"},
                    cache: false,
                    dataType: "json",
                    success: function (result) {
                        userGuide.clear();
                        userGuide.load();
                    }
                });
            }
        });

        cancelButton.show({
            label: "跳出引导",
            left: coverExcept.x - coverExcept.w + 200,
            top: coverExcept.y + 135,
            click: function (event) {
                me.dropFlow("CONTRACT_SUPPLIER_GUIDE");
            }
        });
    },
    //无法批量申请
    _CONTRACT_SUPPLIER_GUIDE_SINGLE_APPLY_SUCCESS_NO_BATCH_APPLY: function () {
        userGuide.clear();
        var $target = $("#applySupplierData .gray_color"),
            size = $("#applySupplierData tr").size() ,
            me = APP_BCGOGO.UserGuide,
            currentFlowName = me.currentFlowName,
            currentStepName = me.currentStepName,
            tip = {
                width: 464,
                height: 233
            };
        if ($target.length == 0 || size == 0)return;
        if (!currentFlowName || !currentStepName) return;
        coverExcept['x'] = $target.offset().left - 5;
        coverExcept['y'] = $target.offset().top - 6;
        coverExcept['w'] = $target.width() + 10;
        coverExcept['h'] = $target.height() + 11.5;
        shadow.coverExcept(coverExcept);

        cover['x'] = $target.offset().left - 5;
        cover['y'] = $target.offset().top - 6;
        cover['w'] = $target.width() + 10;
        cover['h'] = ($target.height() + 11.5) * (size - 1);
        shadow.cover(cover);

        var currentPanel = new App.Module.GuideTipPanel();
        var cancelButton = new App.Module.GuideTipCancelButton();
        var skipButton = new App.Module.GuideTipOkButton();

        guideTipDisplayManager.addChild(currentPanel);
        guideTipDisplayManager.addChild(cancelButton);
        guideTipDisplayManager.addChild(skipButton);
        currentPanel.show({
            backgroundImageUrl: currentPanel.IMG.R_T_small_blue,
            left: coverExcept.x - coverExcept.w - 50,
            top: coverExcept.y + 35,
            content: {
                htmlText: "<span style='font-size:14px;'>关联申请已提交，对方同意后，将建立关联!</span>"
            }
        });

        skipButton.show({
            left: coverExcept.x - coverExcept.w + 50,
            top: coverExcept.y + 123,
            width: 50,
            label: "我知道了",
            click: function (event) {
                $.cookie("currentStepName", null);
                APP_BCGOGO.Net.asyncAjax({
                    type: "POST",
                    url: "guide.do?method=updateCurrentUserGuideStepFinished",
                    data: {flowName: "CONTRACT_SUPPLIER_GUIDE"},
                    cache: false,
                    dataType: "json",
                    success: function (result) {
                        userGuide.clear();
                        userGuide.load();
                    }
                });
            }
        });

        cancelButton.show({
            label: "跳出引导",
            left: coverExcept.x - coverExcept.w + 170,
            top: coverExcept.y + 135,
            click: function (event) {
                me.dropFlow("CONTRACT_SUPPLIER_GUIDE");
            }
        });
    },
    //申请多个关联
    _CONTRACT_SUPPLIER_GUIDE_BATCH_APPLY: function () {
        userGuide.clear();
        shadow.clear();
        var $target = $("#applySupplierData"),
            me = APP_BCGOGO.UserGuide,
            currentFlowName = me.currentFlowName,
            currentStepName = me.currentStepName,
            tip = {
                width: 464,
                height: 233
            };
        if ($target.length == 0)return;
        if (!currentFlowName || !currentStepName) return;
        coverExcept['x'] = $target.offset().left;
        coverExcept['y'] = $target.offset().top;
        coverExcept['w'] = $target.width() - 130;
        coverExcept['h'] = $target.height();
        shadow.coverExcept(coverExcept);
        var panel = new App.Module.GuideTipPanel();
        var cancelButton = new App.Module.GuideTipCancelButton();
        var checkButton = new App.Module.GuideTipOkButton();
        guideTipDisplayManager.addChild(cancelButton);
        guideTipDisplayManager.addChild(panel);
        guideTipDisplayManager.addChild(checkButton);
        panel.show({
            backgroundImageUrl: panel.IMG.L_B_small_blue,
            left: (coverExcept.x + 150),
            top: (coverExcept.y - 135),
            content: {
                htmlText: "<span style='font-size:16px;'>请点击勾选供应商!</span>"
            }
        });
        cancelButton.show({
            label: "跳出引导",
                        left: coverExcept.x + coverExcept.w + 100,
                        top: coverExcept.y,
            click: function (event) {
                me.dropFlow("CONTRACT_SUPPLIER_GUIDE");
            }
        });
        checkButton.show({
            left: (coverExcept.x + 270),
            top: (coverExcept.y - 60),
            label: "勾选好了",
            width: 50,
            click: function (event) {
                if ($("input:checkbox[name$='supplierShopId'][checked=true]").size() > 0) {
                    userGuide.clear();
                    shadow.clear();
                    var $childTarget = $("#applySupplierBtn");
                    coverExcept['x'] = $childTarget.offset().left;
                    coverExcept['y'] = $childTarget.offset().top;
                    coverExcept['w'] = $childTarget.width();
                    coverExcept['h'] = $childTarget.height();
                    shadow.coverExcept(coverExcept);
                    var applyPanel = new App.Module.GuideTipPanel();
                    var applyButton = new App.Module.GuideTipCancelButton();
                    guideTipDisplayManager.addChild(applyPanel);
                    guideTipDisplayManager.addChild(applyButton);
                    applyPanel.show({
                        backgroundImageUrl: applyPanel.IMG.L_B_small_blue,
                        left: coverExcept.x - coverExcept.w + 80,
                        top: coverExcept.y - 135,
                        content: {
                            htmlText: "<span style='font-size:16px;'>请点击申请建立关联!</span>"
                        }
                    });
                    applyButton.show({
                        label: "跳出引导",
                        left: coverExcept.x + coverExcept.w + 77,
                        top: coverExcept.y - 25,
                        click: function (event) {
                            me.dropFlow("CONTRACT_SUPPLIER_GUIDE");
                        }
                    });
                } else {
                    nsDialog.jAlert("请点击勾选供应商");
                }
            }
        });
    },
    _CONTRACT_SUPPLIER_GUIDE_BATCH_APPLY_SUCCESS: function () {
        userGuide.clear();
        var $target = $("#applySupplierData .gray_color"),
            me = APP_BCGOGO.UserGuide,
            size = $("#applySupplierData tr").size() ,
            currentFlowName = me.currentFlowName,
            currentStepName = me.currentStepName,
            tip = {
                width: 464,
                height: 233
            };
        if ($target.length == 0 || size == 0)return;
        if (!currentFlowName || !currentStepName) return;
        coverExcept['x'] = $target.offset().left - 5;
        coverExcept['y'] = $target.offset().top - 6;
        coverExcept['w'] = $target.width() + 10;
        coverExcept['h'] = $target.height() + 11.5;
        shadow.coverExcept(coverExcept);

        cover['x'] = $target.offset().left - 5;
        cover['y'] = $target.offset().top - 6;
        cover['w'] = $target.width() + 10;
        cover['h'] = ($target.height() + 11.5) * (size - 1);
        shadow.cover(cover);
        var cancelButton = new App.Module.GuideTipCancelButton();
        var panel = new App.Module.GuideTipPanel();
        var okButton = new App.Module.GuideTipOkButton();
        guideTipDisplayManager.addChild(cancelButton);
        guideTipDisplayManager.addChild(panel);
        guideTipDisplayManager.addChild(okButton);
        panel.show({
            backgroundImageUrl: panel.IMG.R_T_big_green,
            left: coverExcept.x - coverExcept.w - 56,
            top: coverExcept.y + 29,
            content: {
                top: 60,
                left: 35,
                htmlText: "<span style='font-size:16px;font-weight:bold;'>关联申请已提交，对方同意后，将建立关联！</span>"
            }
        });
        okButton.show({
            left: coverExcept.x - coverExcept.w + 60,
            top: coverExcept.y + 173,
            label: "我知道了",
            width: 60,
            click: function (event) {
                userGuide.clear();
                shadow.clear();
                $.cookie("currentStepName", null);
                APP_BCGOGO.Net.asyncAjax({
                    type: "POST",
                    url: "guide.do?method=updateCurrentUserGuideFlowFinished",
                    data: {flowName: "CONTRACT_SUPPLIER_GUIDE"},
                    cache: false,
                    dataType: "json",
                    success: function (result) {
                        userGuide.clear();
                        userGuide.load();
                    }
                });
                /*APP_BCGOGO.Net.asyncAjax({
                 type: "POST",
                 url: "guide.do?method=nextUserGuideStep",
                 cache: false,
                 dataType: "json",
                 success: function (result) {
                 if (result.success) {
                 userGuide.finishFlow("PRODUCT_PRICE_GUIDE_BEGIN");
                 }
                 }
                 });*/
            }
        });
        cancelButton.show({
            label: "跳出引导",
            left: (coverExcept.x - 100),
            top: (coverExcept.y),
            click: function (event) {
                me.dropFlow("CONTRACT_SUPPLIER_GUIDE");
            }
        });
    }
};
