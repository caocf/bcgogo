userGuide.funLib["PRODUCT_PRICE_GUIDE"] = {
    //start main page
    _PRODUCT_PRICE_GUIDE_BEGIN: function () {
        userGuide.funLib["PRODUCT_PRICE_GUIDE"]._begin();
    },
    PRODUCT_PRICE_GUIDE_BEGIN: function () {
        userGuide.funLib["PRODUCT_PRICE_GUIDE"]._begin();
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
                + '<br><br><p><span style="font-size:20px; margin: 10px;font-weight:bold;">您想查看关联供应商报价吗？</span></p><br><br>'
                + '<p><span style="font-size:16px;">查看报价可以了解配件行情，并且可以直接在线下单采购！</span></p>'
                + '</div>',
            left: 0.5 * ( $(document).width() - tip.width),
            top: 0.5 * ( $(document).height() - tip.height),
            associated: {
                label: "去看报价",
                "click": function (event) {
                    userGuide.notRemind(userGuide.currentFlowName);
                    APP_BCGOGO.Net.asyncAjax({
                        type: "POST",
                        url: "guide.do?method=nextUserGuideStep",
                        cache: false,
                        dataType: "json",
                        success: function (result) {
                            if (result.success) {
                                userGuide.funLib["PRODUCT_PRICE_GUIDE"]["PRODUCT_PRICE_GUIDE_PURCHASE_CENTER"]();
                            }
                        }
                    });
                }
            }
        });
    },
    //订单中心
    PRODUCT_PRICE_GUIDE_PURCHASE_CENTER: function () {
        userGuide.clear();
        hideAnnouncement();
        var me = userGuide,
            currentFlowName = me.currentFlowName,
            currentStepName = me.currentStepName,
            tip = {
                width: 464,
                height: 233
            };
        if (!currentFlowName || !currentStepName) return;
        var $target = $("[userGuideTartet='PRODUCT_PRICE_GUIDE_PURCHASE_CENTER']").eq(0);
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
                htmlText: "<span style='font-size:18px;font-weight:bold;'>点击进入采购中心!</span>"
            }
        });
        cancelButton.show({
            label: "跳出引导",
            left: left + 200,
            top: top + 135,
            click: function (event) {
                me.dropFlow("PRODUCT_PRICE_GUIDE");
            }
        });
    },

    //勾选商品采购
    PRODUCT_PRICE_GUIDE_PURCHASE: function () {
        userGuide.funLib["PRODUCT_PRICE_GUIDE"]._PRODUCT_PRICE_GUIDE_PURCHASE_STEP1();
    },
    _PRODUCT_PRICE_GUIDE_PURCHASE: function () {
        userGuide.funLib["PRODUCT_PRICE_GUIDE"]._PRODUCT_PRICE_GUIDE_PURCHASE_STEP1();
    },
    _PRODUCT_PRICE_GUIDE_PURCHASE_STEP1:function(){
        userGuide.clear();
        var $target = $("#commodityTable").find(".J-Amount").eq(0).parent(),
            me = APP_BCGOGO.UserGuide,
            currentFlowName = me.currentFlowName,
            currentStepName = me.currentStepName;
        if ($target.length == 0 || $target.size() == 0)return;
        if (!currentFlowName || !currentStepName) return;
        coverExcept['x'] = $target.offset().left -10;
        coverExcept['y'] = $target.offset().top -10;
        coverExcept['w'] = $target.width();
        coverExcept['h'] = $target.height() * $target.size() +20;
        shadow.coverExcept(coverExcept);
        var panel = new App.Module.GuideTipPanel();
        var cancelButton = new App.Module.GuideTipCancelButton();
        var checkButton = new App.Module.GuideTipOkButton();
        guideTipDisplayManager.addChild(cancelButton);
        guideTipDisplayManager.addChild(panel);
        guideTipDisplayManager.addChild(checkButton);
        panel.show({
            backgroundImageUrl: panel.IMG.L_T_small_blue,
            left: (coverExcept.x -8),
            top: (coverExcept.y + 44),
            content: {
                htmlText: "<span style='font-size:16px;'>请填写购买量!</span>"
            }
        });
        cancelButton.show({
            label: "跳出引导",
            left: (coverExcept.x +180),
            top: (coverExcept.y  + 150),
            click: function (event) {
                me.dropFlow("PRODUCT_PRICE_GUIDE");
            }
        });
        checkButton.show({
            left: (coverExcept.x +66),
            top: (coverExcept.y + 122),
            label: "下一步",
            width: 50,
            click: function (event) {
                userGuide.funLib["PRODUCT_PRICE_GUIDE"]._PRODUCT_PRICE_GUIDE_PURCHASE_STEP2();
            }
        });

    },
    _PRODUCT_PRICE_GUIDE_PURCHASE_STEP2:function(){
        userGuide.clear();
        var $target = $("#commodityTable").find(".J-purchaseSingleBtn").eq(0),
            me = APP_BCGOGO.UserGuide,
            currentFlowName = me.currentFlowName,
            currentStepName = me.currentStepName;
        if ($target.length == 0 || $target.size() == 0)return;
        if (!currentFlowName || !currentStepName) return;
        coverExcept['x'] = $target.offset().left -10;
        coverExcept['y'] = $target.offset().top -10;
        coverExcept['w'] = $target.width() *2 + 50;
        coverExcept['h'] = $target.height() * $target.size() +20;
        shadow.coverExcept(coverExcept);
        var panel = new App.Module.GuideTipPanel();
        var cancelButton = new App.Module.GuideTipCancelButton();
        var checkButton = new App.Module.GuideTipOkButton();
        guideTipDisplayManager.addChild(cancelButton);
        guideTipDisplayManager.addChild(panel);
        guideTipDisplayManager.addChild(checkButton);
        panel.show({
            backgroundImageUrl: panel.IMG.R_T_small_blue,
            left: (coverExcept.x -148),
            top: (coverExcept.y + 44),
            content: {
                htmlText: "<span style='font-size:16px;'>你可以选择立即采购或者加入购物车哦!</span>"
            }
        });
        cancelButton.show({
            label: "跳出引导",
            left: (coverExcept.x +40),
            top: (coverExcept.y  + 150),
            click: function (event) {
                me.dropFlow("PRODUCT_PRICE_GUIDE");
            }
        });

    },
    //立即采购下一步
    _PRODUCT_PRICE_GUIDE_PURCHASE_STEP3_1:function(){
        userGuide.clear();
        var $target = $(".J-ItemBody").eq(0),
            me = APP_BCGOGO.UserGuide,
            currentFlowName = me.currentFlowName,
            currentStepName = me.currentStepName;
        if ($target.length == 0 || $target.size() == 0)return;
//        if (!currentFlowName || !currentStepName) return;
        cover['x'] = $target.offset().left -10;
        cover['y'] = $target.offset().top -10;
        cover['w'] = $target.width() ;
        cover['h'] = $target.height() * $target.size() +20;
        cover['isFullTransparent'] = true;

        coverExcept['x'] = $target.offset().left -10;
        coverExcept['y'] = $target.offset().top -10;
        coverExcept['w'] = $target.width() ;
        coverExcept['h'] = $target.height() * $target.size() +20;
        coverExcept['isFullTransparent'] = false;
        shadow.cover(cover);
        shadow.coverExcept(coverExcept);
        var panel = new App.Module.GuideTipPanel();
        var cancelButton = new App.Module.GuideTipCancelButton();
        var checkButton = new App.Module.GuideTipOkButton();
        guideTipDisplayManager.addChild(panel);
        guideTipDisplayManager.addChild(checkButton);
        panel.show({
            backgroundImageUrl: panel.IMG.L_T_small_blue,
            left: (coverExcept.x +148),
            top: (coverExcept.y + 60),
            content: {
                htmlText: "<span style='font-size:15px;'>您可以确认商品的信息与采购量后提交订单!</span>"
            }
        });
        checkButton.show({
            label: "我知道了",
            left: (coverExcept.x +198),
            top: (coverExcept.y  + 156),
            click: function (event) {
                $.cookie("currentStepName", null);
                  APP_BCGOGO.Net.asyncAjax({
                      type: "POST",
                      url: "guide.do?method=updateCurrentUserGuideFlowFinished",
                      data: {flowName: "PRODUCT_PRICE_GUIDE"},
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
        });
    },
        //加入购物车下一步
    _PRODUCT_PRICE_GUIDE_PURCHASE_STEP3_2:function(){
        userGuide.clear();
        var $target = $("#returnDialog").eq(0),
            me = APP_BCGOGO.UserGuide,
            currentFlowName = me.currentFlowName,
            currentStepName = me.currentStepName;
        if ($target.length == 0 || $target.size() == 0)return;
        if (!currentFlowName || !currentStepName) return;
        cover['x'] = $target.offset().left -10;
        cover['y'] = $target.offset().top -10;
        cover['w'] = $target.width()+20 ;
        cover['h'] = $target.height() * $target.size() +20;
        cover["z-index"] = 1005;
        cover['isFullTransparent'] = true;

        coverExcept['x'] = $target.offset().left -10;
        coverExcept['y'] = $target.offset().top -10;
        coverExcept['w'] = $target.width() +20;
        coverExcept['h'] = $target.height() * $target.size() +20;
        coverExcept['isFullTransparent'] = false;
        shadow.cover(cover);
        shadow.coverExcept(coverExcept);
        var panel = new App.Module.GuideTipPanel();
        var cancelButton = new App.Module.GuideTipCancelButton();
        var checkButton = new App.Module.GuideTipOkButton();
        guideTipDisplayManager.addChild(panel);
        guideTipDisplayManager.addChild(checkButton);
        panel.show({
            backgroundImageUrl: panel.IMG.L_T_small_blue,
            left: (coverExcept.x +148),
            top: (coverExcept.y + 160),
            "z-index":1006,
            content: {
                htmlText: "<span style='font-size:16px;'>你可以去购物车结算或者继续采购哦!</span>"
            }
        });
        checkButton.show({
            label: "我知道了",
            left: (coverExcept.x +198),
            top: (coverExcept.y  + 256),
            "z-index":1007,
            click: function (event) {
                $.cookie("currentStepName", null);
                  APP_BCGOGO.Net.asyncAjax({
                      type: "POST",
                      url: "guide.do?method=updateCurrentUserGuideFlowFinished",
                      data: {flowName: "PRODUCT_PRICE_GUIDE"},
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
        });
    },

        //查看更多供应商报价
    PRODUCT_PRICE_GUIDE_MORE_PRODUCT: function () {
        userGuide.funLib["PRODUCT_PRICE_GUIDE"]._MORE_PRODUCT_MOUSE_ENTER();
    },
    _moreProduct: function () {
        userGuide.clear();
        var me = userGuide,
            currentFlowName = me.currentFlowName,
            currentStepName = me.currentStepName;
        if (!currentFlowName || !currentStepName) return;
        var $target = $("#autoSearch");
        cover['x'] = $target.offset().left -10;
        cover['y'] = $target.offset().top -10;
        cover['w'] = $target.width()+20 ;
        cover['h'] = $target.height() * $target.size() +20;
        cover['isFullTransparent'] = true;

        coverExcept['x'] = $target.offset().left -10;
        coverExcept['y'] = $target.offset().top -10;
        coverExcept['w'] = $target.width() +20;
        coverExcept['h'] = $target.height() * $target.size() +20;
        coverExcept['isFullTransparent'] = false;
        shadow.cover(cover);
        shadow.coverExcept(coverExcept);


        var left = coverExcept.x - 120;
        var top = coverExcept.y + coverExcept.h + 5;
        var panel = new App.Module.GuideTipPanel();
        var cancelButton = new App.Module.GuideTipCancelButton();
        var checkButton = new App.Module.GuideTipOkButton();
        guideTipDisplayManager.addChild(panel);
        guideTipDisplayManager.addChild(cancelButton);
        guideTipDisplayManager.addChild(checkButton);
        panel.show({
            backgroundImageUrl: panel.IMG.R_T_small_blue,
            top: top,
            left: left,
            content: {
                htmlText: "<span style='font-size:18px;font-weight:bold;'>点击可查看非关联供应商报价信息!</span>"
            }
        });
        checkButton.show({
            label: "我知道了",
            left: left+96,
            top: top + 51,
            "z-index": 1007,
            click: function (event) {
                $.cookie("currentStepName", null);
                APP_BCGOGO.Net.asyncAjax({
                    type: "POST",
                    url: "guide.do?method=updateCurrentUserGuideFlowFinished",
                    data: {flowName: "PRODUCT_PRICE_GUIDE"},
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
        });
        cancelButton.show({
            label: "跳出引导",
            left: left + 120,
            top: top + 155,
            click: function (event) {
                me.dropFlow("PRODUCT_PRICE_GUIDE");
            }
        });
    },
    //鼠标移上去看看
    _MORE_PRODUCT_MOUSE_ENTER: function () {
        userGuide.clear();
        shadow.clear();
        var $target = $("td[action=showMoreProductProductPriceGuide]"),
            $tixing = $("td[action=showMoreProductProductPriceGuide]").find(".tixing"),
            me = APP_BCGOGO.UserGuide,
            currentFlowName = me.currentFlowName,
            currentStepName = me.currentStepName;
        if ($target.length == 0) {
            $.cookie("currentStepName", null);
            APP_BCGOGO.Net.asyncAjax({
                type: "POST",
                url: "guide.do?method=updateCurrentUserGuideFlowFinished",
                data: {flowName: "PRODUCT_PRICE_GUIDE"},
                cache: false,
                dataType: "json",
                success: function (result) {
                    userGuide.clear();
                    userGuide.load();
                }
            });
            return;
        }
        if (!currentFlowName || !currentStepName) return;
        coverExcept['x'] = $target.offset().left;
        coverExcept['y'] = $target.offset().top;
        coverExcept['w'] = $target.width();
        coverExcept['h'] = $target.height();
        shadow.coverExcept(coverExcept);
        var panel = new App.Module.GuideTipPanel();
        var cancelButton = new App.Module.GuideTipCancelButton();
        guideTipDisplayManager.addChild(cancelButton);
        guideTipDisplayManager.addChild(panel);
        panel.show({
            backgroundImageUrl: panel.IMG.L_B_small_blue,
            left: (coverExcept.x-34),
            top: (coverExcept.y - 135),
            content: {
                htmlText: "<span style='font-size:16px;'>鼠标移上去看看!</span>"
            }
        });
        cancelButton.show({
            label: "跳出引导",
            left: (coverExcept.x + 120),
            top: (coverExcept.y + 50),
            click: function (event) {
                me.dropFlow("PRODUCT_PRICE_GUIDE");
            }
        });
    },
    _MORE_PRODUCT_APPLY: function () {
        userGuide.clear();
        shadow.clear();
        var $target = $("td[action=showMoreProductProductPriceGuide]").eq(0).find(".tixing"),
            me = APP_BCGOGO.UserGuide,
            currentFlowName = me.currentFlowName,
            currentStepName = me.currentStepName;
        if ($target.length == 0)return;
        if (!currentFlowName || !currentStepName) return;
        $target.show("fast", function () {
            cover['x'] = $target.offset().left;
            cover['y'] = $target.offset().top + 4;
            cover['w'] = $target.width() - 40;
            cover['h'] = $target.height() - 6;
            cover['isFullTransparent'] = true;

            coverExcept['x'] = $target.offset().left;
            coverExcept['y'] = $target.offset().top + 4;
            coverExcept['w'] = $target.width() - 40;
            coverExcept['h'] = $target.height() - 6;
            coverExcept['isFullTransparent'] = false;
            shadow.cover(cover);
            shadow.coverExcept(coverExcept);

            var panel = new App.Module.GuideTipPanel();
            var cancelButton = new App.Module.GuideTipCancelButton();
            var checkButton = new App.Module.GuideTipOkButton();
            guideTipDisplayManager.addChild(cancelButton);
            guideTipDisplayManager.addChild(panel);
            guideTipDisplayManager.addChild(checkButton);
            panel.show({
                backgroundImageUrl: panel.IMG.R_T_small_blue,
                left: (coverExcept.x) - 120,
                top: (coverExcept.y + coverExcept.h),
                content: {
                    htmlText: "<span style='font-size:16px;'>点击后可以申请关联!</span>"
                }
            });
            checkButton.show({
                label: "我知道了",
                left: (coverExcept.x -68),
                top: (coverExcept.y  + 150),
                click: function (event) {
                    $.cookie("currentStepName", null);
                      APP_BCGOGO.Net.asyncAjax({
                          type: "POST",
                          url: "guide.do?method=updateCurrentUserGuideFlowFinished",
                          data: {flowName: "PRODUCT_PRICE_GUIDE"},
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
            });
            cancelButton.show({
                label: "跳出引导",
                left: (coverExcept.x + 120),
                top: (coverExcept.y + coverExcept.h + 50),
                click: function (event) {
                    me.dropFlow("PRODUCT_PRICE_GUIDE");
                }
            });
        });
    },
    //成功
    _PRODUCT_PRICE_GUIDE_SUCCESS: function () {
        $.cookie("currentStepName", null);
        APP_BCGOGO.Net.asyncAjax({
            type: "POST",
            url: "guide.do?method=updateCurrentUserGuideFlowFinished",
            data: {flowName: "PRODUCT_PRICE_GUIDE"},
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
};