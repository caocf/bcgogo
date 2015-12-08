var shadow = APP_BCGOGO.Module.shadow;
var userGuide = APP_BCGOGO.UserGuide;
APP_BCGOGO.UserGuide.funLib["PRODUCT_ONLINE_GUIDE"] = {
    PRODUCT_ONLINE_GUIDE_BEGIN: function () {
        userGuide.clear();
        var currentFlowName = userGuide.currentFlowName;
        var currentStepName = userGuide.currentStepName;
        if (!currentFlowName || !currentStepName) return;
        userGuide.coverAll();
        var startPanel = new App.Module.GuideTipStartPanel();
        guideTipDisplayManager.addChild(startPanel);
        var tip = {
            width: 464,
            height: 233
        };
        var left = 0.5 * ( $(document).width() - tip.width);
        var top = 0.5 * $(document).height() - tip.height;

        startPanel.show({
            htmlText: '<div style="color:#FFF">'
                + '<p><span style="font-size:20px; margin: 10px;">您要去做商品上架吗？</span></p><br>'
                + '<p><span style="font-size:16px;  margin: 10px;">商品上架后可以在线销售商品！</span></p>'
                + '<p><span style="font-size:16px;  margin: 10px;">商品上架后可以快速促销商品！</span></p>'
                + '</div>',
            left: left,
            top: top,
            associated:{
                label:"马上上架",
                "click": function (event) {
                    userGuide.notRemind(userGuide.currentFlowName);
                    APP_BCGOGO.Net.asyncAjax({
                        type: "POST",
                        url: "guide.do?method=nextUserGuideStep",
                        cache: false,
                        dataType: "json",
                        success: function (result) {
                            if (result.success) {
                                userGuide.funLib["PRODUCT_ONLINE_GUIDE"]["PRODUCT_ONLINE_GUIDE_TXN"]();
                            }
                        }
                    });
                }
            }
        });
    },
//    _PRODUCT_ONLINE_GUIDE_BEGIN: function () {
//        userGuide.clear();
//        shadow.clear();
//        var $target = $("#applyCustomerData .gray_color"),
//            me = APP_BCGOGO.UserGuide,
//            size = $("#applyCustomerData tr").size() ,
//            currentFlowName = me.currentFlowName,
//            currentStepName = me.currentStepName,
//            tip = {
//                width: 464,
//                height: 233
//            };
//        if ($target.length == 0 || size == 0)return;
//        if (!currentFlowName || !currentStepName) return;
//        coverExcept['x'] = $target.offset().left - 5;
//        coverExcept['y'] = $target.offset().top - 6;
//        coverExcept['w'] = $target.width() + 10;
//        coverExcept['h'] = $target.height() + 11.5;
//        shadow.coverExcept(coverExcept);
//
//        cover['x'] = $target.offset().left - 5;
//        cover['y'] = $target.offset().top - 6;
//        cover['w'] = $target.width() + 10;
//        cover['h'] = ($target.height() + 11.5) * (size - 1);
//        shadow.cover(cover);
//        var cancelButton = new App.Module.GuideTipCancelButton();
//        var panel = new App.Module.GuideTipPanel();
//        var tryButton = new App.Module.GuideTipOkButton();
//        guideTipDisplayManager.addChild(cancelButton);
//        guideTipDisplayManager.addChild(panel);
//        guideTipDisplayManager.addChild(tryButton);
//        panel.show({
//            backgroundImageUrl: panel.IMG.R_T_big_green,
//            left: coverExcept.x - coverExcept.w - 56,
//            top: coverExcept.y + 29,
//            content: {
//                top: 60,
//                left: 35,
//                htmlText: "<span style='font-size:16px;font-weight:bold;'>关联客户后可享受在线销售、查看客户库存服务！<br>" +
//                    "上架的商品可以在线销售，您暂时还没有商品上架！</span>"
//            }
//        });
//        tryButton.show({
//            left: coverExcept.x - coverExcept.w + 60,
//            top: coverExcept.y + 173,
//            label: "马上去上架",
//            width: 60,
//            click: function (event) {
//                window.location.href = "stockSearch.do?method=getStockSearch&type=txn";
//            }
//        });
//        cancelButton.show({
//            label: "跳出引导",
//            left: (coverExcept.x - 100),
//            top: (coverExcept.y),
//            click: function (event) {
//                me.dropFlow("CONTRACT_CUSTOMER_GUIDE_BEGIN");
//            }
//        });
//    },
    PRODUCT_ONLINE_GUIDE_TXN: function () {
        userGuide.clear();
        hideAnnouncement();
        var currentFlowName = userGuide.currentFlowName;
        var currentStepName = userGuide.currentStepName;
        if (!currentFlowName || !currentStepName) return;
        var $target = $("[userGuideTartet='PRODUCT_ONLINE_GUIDE_TXN']").eq(0);
        coverExcept['x'] = $target.offset().left;
        coverExcept['y'] = $target.offset().top;
        coverExcept['w'] = $target.width();
        coverExcept['h'] = $target.height();
        shadow.coverExcept(coverExcept);

        var left = coverExcept.x + 0.5 * coverExcept.w - 40;
        var top = coverExcept.y + coverExcept.h;
        var panel = new App.Module.GuideTipPanel();
        guideTipDisplayManager.addChild(panel);
        panel.show({
            left: left,
            top: top,
            backgroundImageUrl: panel.IMG.L_T_small_blue,
            content: {
                htmlText: "<H3>点击进入订单中心</H3>",
                top: 40,
                left: 35
            }
        });
        var cancelButton = new App.Module.GuideTipCancelButton();
        guideTipDisplayManager.addChild(cancelButton);
        cancelButton.show({
            label: "跳出引导",
            left: left + 200,
            top: top + 135,
            click: function (event) {
                userGuide.dropFlow("PRODUCT_ONLINE_GUIDE");
            }
        });
    },
//    PRODUCT_ONLINE_GUIDE_SALE: function () {
//        userGuide.clear();
//        var currentFlowName = userGuide.currentFlowName;
//        var currentStepName = userGuide.currentStepName;
//        if (!currentFlowName || !currentStepName) return;
//        var $target = $("#goodsSaleALink");
//        coverExcept['x'] = $target.offset().left;
//        coverExcept['y'] = $target.offset().top;
//        coverExcept['w'] = $target.width();
//        coverExcept['h'] = $target.height();
//        shadow.coverExcept(coverExcept);
//        var left = coverExcept.x + 0.5 * coverExcept.w - 170;
//        var top = coverExcept.y + coverExcept.h;
//        var panel = new App.Module.GuideTipPanel();
//        guideTipDisplayManager.addChild(panel);
//        panel.show({
//            left: left,
//            top: top,
//            backgroundImageUrl: panel.IMG.R_T_small_blue,
//            content: {
//                htmlText: "<H3>点击进入销售管理</H3>",
//                top: 40,
//                left: 35
//            }
//        });
//        var cancelButton = new App.Module.GuideTipCancelButton();
//        guideTipDisplayManager.addChild(cancelButton);
//        cancelButton.show({
//            label: "跳出引导",
//            left: left + 200,
//            top: top + 135,
//            click: function (event) {
//                userGuide.dropFlow("PRODUCT_ONLINE_GUIDE");
//            }
//        });
//    },

    //商品上架导航栏
    PRODUCT_ONLINE_GUIDE_GOODS_ONLINE: function () {
        userGuide.clear();
        var currentFlowName = userGuide.currentFlowName;
        var currentStepName = userGuide.currentStepName;
        if (!currentFlowName || !currentStepName) return;
        var $target = $("[userGuideTartet='PRODUCT_ONLINE_GUIDE_GOODS_ONLINE']").eq(0);
        coverExcept['x'] = $target.offset().left ;
        coverExcept['y'] = $target.offset().top;
        coverExcept['w'] = $target.width();
        coverExcept['h'] = $target.height();
        coverExcept['hasBorder'] = true;
        shadow.coverExcept(coverExcept);

        var left = coverExcept.x + 0.5 * coverExcept.w - 40;
        var top = coverExcept.y + coverExcept.h;
        var panel = new App.Module.GuideTipPanel();
        guideTipDisplayManager.addChild(panel);
        panel.show({
            left: left,
            top: top,
            backgroundImageUrl: panel.IMG.L_T_small_blue,
            content: {
                htmlText: "<H3>点击进入商品上架维护</H3>",
                top: 40,
                left: 35
            }
        });
        var cancelButton = new App.Module.GuideTipCancelButton();
        guideTipDisplayManager.addChild(cancelButton);
        cancelButton.show({
            label: "跳出引导",
            left: left + 200,
            top: top + 135,
            click: function (event) {
                userGuide.dropFlow("PRODUCT_ONLINE_GUIDE");
            }
        });
    },
    //无商品状态
    _PRODUCT_ONLINE_GUIDE_INVENTORY: function () {
        userGuide.clear();
        var currentFlowName = userGuide.currentFlowName;
        var currentStepName = userGuide.currentStepName;
        if (!currentFlowName || !currentStepName) return;
        var $target = $("#guide_inventory_location");
        coverExcept['x'] = $target.offset().left - 10;
        coverExcept['y'] = $target.offset().top - 10;
        coverExcept['w'] = $target.width() + 40;
        coverExcept['h'] = $target.height() + 20;
        shadow.coverExcept(coverExcept);
        var left = coverExcept.x + 0.5 * coverExcept.w - 170;
        var top = coverExcept.y - 135;
        var panelT = new App.Module.GuideTipPanel();
        guideTipDisplayManager.addChild(panelT);
        panelT.show({
            left: left,
            top: top,
            backgroundImageUrl: panelT.IMG.R_B_small_blue,
            content: {
                htmlText: "<H3>您仓库中没有商品，无法选择商品上架！</H3>",
                top: 40,
                left: 35,
                width: 137
            }
        });
        var left = coverExcept.x + 0.5 * coverExcept.w - 170;
        var top = coverExcept.y + coverExcept.h;
        var panelB = new App.Module.GuideTipPanel();
        guideTipDisplayManager.addChild(panelB);
        panelB.show({
            left: left,
            top: top,
            backgroundImageUrl: panelB.IMG.R_T_small_green,
            content: {
                htmlText: "<H3>商品上架前必须要先入库商品！</H3>",
                top: 40,
                left: 35,
                width: 137
            }
        });
        var okButton = new App.Module.GuideTipOkButton();
        guideTipDisplayManager.addChild(okButton);
        okButton.show({
            left: left + 47,
            top: top + 85,
            label: "马上去入库",
            click: function (event) {
                window.location.href = "storage.do?method=getProducts&type=txn";
            }
        });
        var cancelButton = new App.Module.GuideTipCancelButton();
        guideTipDisplayManager.addChild(cancelButton);
        cancelButton.show({
            label: "跳出引导",
            left: left + 200,
            top: top + 135,
            click: function (event) {
                userGuide.dropFlow("PRODUCT_ONLINE_GUIDE");
            }
        });

    } ,
    //无商品入完库
    _PRODUCT_ONLINE_GUIDE_INVENTORY_FINISH:function(){
        userGuide.clear();
        userGuide.coverAll();
        var contentHtml = '<div style="color:#FFF">';
        contentHtml += '<br><p><span style="font-size:20px; margin: 10px;">您已成功入库商品，马上去上架吧!</span></p></div>'

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
                label: "上架",
                "click": function (event) {
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
    //有商品状态    请填写上架量
    _PRODUCT_ONLINE_GUIDE_FILL_PRODUCT_INFO:function(){
        userGuide.clear();
        var currentFlowName = userGuide.currentFlowName;
               var currentStepName = userGuide.currentStepName;
               if (!currentFlowName || !currentStepName) return;
        var $target = $("#goodsOffSalesTable #productDTOs0\\.inSalesAmountShow").parent();
        coverExcept['x'] = $target.offset().left;
        coverExcept['y'] = $target.offset().top;
        coverExcept['w'] = $target.width();
        coverExcept['h'] = $target.height() + 18;
        shadow.coverExcept(coverExcept);
        var left = coverExcept.x + 0.5 * coverExcept.w - 170;
        var top = coverExcept.y + coverExcept.h;
        var panelFirst = new App.Module.GuideTipPanel();
        guideTipDisplayManager.addChild(panelFirst);
        panelFirst.show({
            left: left,
            top: top,
            backgroundImageUrl: panelFirst.IMG.R_T_small_blue,
            content: {
                htmlText: "<H3>请填写上架量</H3>",
                top: 40,
                left: 35,
                width: 137
            }
        });
        var okButtonFirst = new App.Module.GuideTipOkButton();
        guideTipDisplayManager.addChild(okButtonFirst);
        okButtonFirst.show({
            left: left + 47,
            top: top + 85,
            label: "下一步",
            click: function (event) {
                var $amount =  $("#goodsOffSalesTable #productDTOs0\\.inSalesAmountShow");
               if(!$amount.val() || !($amount.val() * 1 > 0)){
                   nsDialog.jAlert("请输入正确的数量!");
               }else{
                  shadow.clear();
                   panelFirst.remove();
                   okButtonFirst.remove();
                   APP_BCGOGO.UserGuide.funLib["PRODUCT_ONLINE_GUIDE"]["_PRODUCT_ONLINE_GUIDE_FILL_PRODUCT_INFO_SECOND"]();
               }
            }
        });
        var cancelButton = new App.Module.GuideTipCancelButton();
        guideTipDisplayManager.addChild(cancelButton);
        cancelButton.show({
            label: "跳出引导",
            left: left + 200,
            top: top + 135,
            click: function (event) {
                userGuide.dropFlow("PRODUCT_ONLINE_GUIDE");
            }
        });
    },
    _PRODUCT_ONLINE_GUIDE_FILL_PRODUCT_INFO_SECOND: function () {
        userGuide.clear();
        var currentFlowName = userGuide.currentFlowName;
        var currentStepName = userGuide.currentStepName;
        if (!currentFlowName || !currentStepName) return;
        var $target = $("#goodsOffSalesTable #productDTOs0\\.tradePrice").parent();
        coverExcept['x'] = $target.offset().left;
        coverExcept['y'] = $target.offset().top;
        coverExcept['w'] = $target.width();
        coverExcept['h'] = $target.height() + 18;
        shadow.coverExcept(coverExcept);
        var left = coverExcept.x + 0.5 * coverExcept.w - 170;
        var top = coverExcept.y + coverExcept.h;
        var panel = new App.Module.GuideTipPanel();
        guideTipDisplayManager.addChild(panel);
        panel.show({
            left: left,
            top: top,
            backgroundImageUrl: panel.IMG.R_T_small_blue,
            content: {
                htmlText: "<H3>请填写批发价</H3>",
                top: 40,
                left: 35,
                width: 137
            }
        });
        var okButton = new App.Module.GuideTipOkButton();
        guideTipDisplayManager.addChild(okButton);
        okButton.show({
            left: left + 47,
            top: top + 85,
            label: "下一步",
            click: function (event) {
                var $amount = $("#goodsOffSalesTable #productDTOs0\\.tradePrice");
                if (!$amount.val() || !($amount.val() * 1 > 0)) {
                    nsDialog.jAlert("请输入正确的价格!");
                } else {
                    shadow.clear();
                    panel.remove();
                    okButton.remove();
                    APP_BCGOGO.UserGuide.funLib["PRODUCT_ONLINE_GUIDE"]["_PRODUCT_ONLINE_GUIDE_FILL_PRODUCT_INFO_THIRD"]();
                }
            }
        });
        var cancelButton = new App.Module.GuideTipCancelButton();
        guideTipDisplayManager.addChild(cancelButton);
        cancelButton.show({
            label: "跳出引导",
            left: left + 200,
            top: top + 135,
            click: function (event) {
                userGuide.dropFlow("PRODUCT_ONLINE_GUIDE");
            }
        });
    },
    _PRODUCT_ONLINE_GUIDE_FILL_PRODUCT_INFO_THIRD: function () {
        userGuide.clear();
        var currentFlowName = userGuide.currentFlowName;
        var currentStepName = userGuide.currentStepName;
        if (!currentFlowName || !currentStepName) return;
        var $target = $("#goodsOffSalesTable #productDTOs0\\.tradePrice").parent().parent().find(".caozuo");
        coverExcept['x'] = $target.offset().left - 5;
        coverExcept['y'] = $target.offset().top - 5;
        coverExcept['w'] = $target.width()+10;
        coverExcept['h'] = $target.height() + 28;
        shadow.coverExcept(coverExcept);
        var left = coverExcept.x + 0.5 * coverExcept.w - 170;
        var top = coverExcept.y + coverExcept.h;
        var panel = new App.Module.GuideTipPanel();
        guideTipDisplayManager.addChild(panel);
        panel.show({
            left: left,
            top: top,
            backgroundImageUrl: panel.IMG.R_T_small_blue,
            content: {
                htmlText: "<H3>请点击上架</H3>",
                top: 40,
                left: 35,
                width: 137
            }
        });
        var cancelButton = new App.Module.GuideTipCancelButton();
        guideTipDisplayManager.addChild(cancelButton);
        cancelButton.show({
            label: "跳出引导",
            left: left + 200,
            top: top + 135,
            click: function (event) {
                userGuide.dropFlow("PRODUCT_ONLINE_GUIDE");
            }
        });
    },
    _PRODUCT_ONLINE_GUIDE_EDIT_ONLINE_PRODUCT_INFO: function () {
        userGuide.clear();
        var currentFlowName = userGuide.currentFlowName;
        var currentStepName = userGuide.currentStepName;
        if (!currentFlowName || !currentStepName) return;
        var $target = $("input:checkbox[name='subBoxInSales']").first().parent().parent();
        cover['x'] = $target.offset().left - 5;
        cover['y'] = $target.offset().top ;
        cover['w'] = $target.width() + 10;
        cover['h'] = $target.height();

        shadow.cover(cover);
        coverExcept['x'] = $target.offset().left - 5;
        coverExcept['y'] = $target.offset().top ;
        coverExcept['w'] = $target.width() + 10;
        coverExcept['h'] = $target.height();

        shadow.coverExcept(coverExcept);
        var left = coverExcept.x + 0.5 * coverExcept.w - 170;
        var top = coverExcept.y + coverExcept.h;
        var panel = new App.Module.GuideTipPanel();
        guideTipDisplayManager.addChild(panel);
        panel.show({
            left: left,
            top: top,
            backgroundImageUrl: panel.IMG.R_T_small_blue,
            content: {
                htmlText: "<H3>上架商品信息可以修改更新!</H3>",
                top: 40,
                left: 35,
                width: 137
            }
        });
        var okButton = new App.Module.GuideTipOkButton();
        guideTipDisplayManager.addChild(okButton);
        okButton.show({
            left: left + 47,
            top: top + 85,
            label: "我知道了",
            click: function (event) {
                APP_BCGOGO.Net.asyncAjax({
                    type: "POST",
                    url: "guide.do?method=nextUserGuideStep",
                    cache: false,
                    dataType: "json",
                    success: function (result) {
                        if (result.success) {
                            userGuide.clear();
                            userGuideInvoker("PRODUCT_ONLINE_GUIDE_CREATE_SPECIFIC_PROMOTE");
                        }
                    }
                });
            }
        });
        var cancelButton = new App.Module.GuideTipCancelButton();
        guideTipDisplayManager.addChild(cancelButton);
        cancelButton.show({
            label: "跳出引导",
            left: left + 200,
            top: top + 135,
            click: function (event) {
                userGuide.dropFlow("PRODUCT_ONLINE_GUIDE");
            }
        });
    },
    _PRODUCT_ONLINE_GUIDE_CREATE_SPECIFIC_PROMOTE: function () {
        userGuide.clear();
        var currentFlowName = userGuide.currentFlowName;
        var currentStepName = userGuide.currentStepName;
        if (!currentFlowName || !currentStepName) return;
        var $unPromotion ;
        $("#goodsInSalesTable input:checkbox[name='subBoxInSales']").each(function(){
            if($(this).parent().parent().find(".wei").size()>0){
                $unPromotion = $(this);
                return false;
            }
        })
        if ($unPromotion) {
            $unPromotion.parent().parent().find(".tixing").show();
            var $target = $unPromotion.parent().parent().find(".tixing").find(".j_addGeXingPromotions");
            coverExcept['x'] = $target.offset().left - 5;
            coverExcept['y'] = $target.offset().top - 5;
            coverExcept['w'] = $target.width() + 10;
            coverExcept['h'] = $target.height() + 10;
            shadow.coverExcept(coverExcept);
            var left = coverExcept.x + 0.5 * coverExcept.w - 170;
            var top = coverExcept.y + coverExcept.h;
            var panel = new App.Module.GuideTipPanel();
            guideTipDisplayManager.addChild(panel);
            panel.show({
                left: left,
                top: top,
                backgroundImageUrl: panel.IMG.R_T_small_blue,
                content: {
                    htmlText: "<H3>请点击创建编辑个性促销!</H3>",
                    top: 40,
                    left: 35,
                    width: 137
                }
            });
            var cancelButton = new App.Module.GuideTipCancelButton();
            guideTipDisplayManager.addChild(cancelButton);
            cancelButton.show({
                label: "跳出引导",
                left: left + 200,
                top: top + 135,
                click: function (event) {
                    userGuide.dropFlow("PRODUCT_ONLINE_GUIDE");
                }
            });
        }
    },
    _PRODUCT_ONLINE_GUIDE_CREATE_SPECIFIC_PROMOTE_FIRST: function () {
        userGuide.clear();
        var currentFlowName = userGuide.currentFlowName;
        var currentStepName = userGuide.currentStepName;
        if (!currentFlowName || !currentStepName) return;
        var $target = $("#addPromotionsRule").parent();
        var z_Index_Top = getTopJqueryUiDialogZIndex();
        coverExcept['x'] = $target.offset().left - 5;
        coverExcept['y'] = $target.offset().top - 5;
        coverExcept['w'] = $target.width() - 60;
        coverExcept['h'] = $target.height() + 10;
        coverExcept["z-index"] = z_Index_Top ;
        shadow.coverExcept(coverExcept);
        var left = coverExcept.x + 0.5 * coverExcept.w - 170;
        var top = coverExcept.y + coverExcept.h;
        var panel = new App.Module.GuideTipPanel();
        guideTipDisplayManager.addChild(panel);
        panel.show({
            left: left,
            top: top,
            "z-index":z_Index_Top,
            backgroundImageUrl: panel.IMG.R_T_small_blue,
            content: {
                htmlText: "<H3>请参照示例填写促销方案信息</H3>",
                top: 40,
                left: 35,
                width: 137
            }
        });
        var okButton = new App.Module.GuideTipOkButton();
        guideTipDisplayManager.addChild(okButton);
        okButton.show({
            left: left + 47,
            top: top + 85,
            "z-index":z_Index_Top,
            label: "下一步",
            click: function (event) {

              if(validatePromote()){
                  APP_BCGOGO.UserGuide.funLib["PRODUCT_ONLINE_GUIDE"]["_PRODUCT_ONLINE_GUIDE_CREATE_SPECIFIC_PROMOTE_SECOND"]();
              }
            }
        });
        var cancelButton = new App.Module.GuideTipCancelButton();
        guideTipDisplayManager.addChild(cancelButton);
        cancelButton.show({
            label: "跳出引导",
            left: left + 200,
            top: top + 135,
            "z-index":z_Index_Top,
            click: function (event) {
                userGuide.dropFlow("PRODUCT_ONLINE_GUIDE");
            }
        });
    },
    _PRODUCT_ONLINE_GUIDE_CREATE_SPECIFIC_PROMOTE_SECOND: function () {
        userGuide.clear();
        var currentFlowName = userGuide.currentFlowName;
        var currentStepName = userGuide.currentStepName;
        if (!currentFlowName || !currentStepName) return;
        var $target = $("#addPromotionsRule");
        var z_Index_Top = getTopJqueryUiDialogZIndex();
        coverExcept['x'] = $target.offset().left - 5;
        coverExcept['y'] = $target.offset().top - 5;
        coverExcept['w'] = $target.width() + 10;
        coverExcept['h'] = $target.height() + 10;
        coverExcept["z-index"] = z_Index_Top;
        shadow.coverExcept(coverExcept);
        var left = coverExcept.x + 0.5 * coverExcept.w -35;
        var top = coverExcept.y + coverExcept.h;
        var panel = new App.Module.GuideTipPanel();
        guideTipDisplayManager.addChild(panel);
        panel.show({
            left: left,
            top: top,
            "z-index":z_Index_Top,
            backgroundImageUrl: panel.IMG.L_T_small_green,
            content: {
                htmlText: "<H3>若您促销方案已全部完成，请点击</H3>",
                top: 40,
                left: 35,
                width: 137
            }
        });
        var okButton = new App.Module.GuideTipOkButton();
        guideTipDisplayManager.addChild(okButton);
        okButton.show({
            left: left + 47,
            top: top + 85,
            "z-index":z_Index_Top,
            label: "我写好了",
            click: function (event) {
                APP_BCGOGO.UserGuide.funLib["PRODUCT_ONLINE_GUIDE"]["_PRODUCT_ONLINE_GUIDE_CREATE_SPECIFIC_PROMOTE_FOURTH"]();
            }
        });
        var cancelButton = new App.Module.GuideTipCancelButton();
        guideTipDisplayManager.addChild(cancelButton);
        cancelButton.show({
            label: "跳出引导",
            left: left + 200,
            top: top + 135,
            "z-index":z_Index_Top,
            click: function (event) {
                userGuide.dropFlow("PRODUCT_ONLINE_GUIDE");
            }
        });
        var left = coverExcept.x + 0.5 * coverExcept.w - 37;
        var top = coverExcept.y - 136;
        var panelT = new App.Module.GuideTipPanel();
        guideTipDisplayManager.addChild(panelT);
        panelT.show({
            left: left,
            top: top,
            "z-index":z_Index_Top,
            backgroundImageUrl: panelT.IMG.L_B_small_blue,
            content: {
                htmlText: "<H3>点击【+】符号可以增加促销方案段的编辑档次</H3>",
                top: 40,
                left: 35,
                width: 137
            }
        });
    },
    _PRODUCT_ONLINE_GUIDE_CREATE_SPECIFIC_PROMOTE_THIRD: function () {
        userGuide.clear();
        var currentFlowName = userGuide.currentFlowName;
        var currentStepName = userGuide.currentStepName;
        if (!currentFlowName || !currentStepName) return;
        var $target = $("#promotionsRuleDl dd").last();
        var z_Index_Top = getTopJqueryUiDialogZIndex();
        coverExcept['x'] = $target.offset().left - 5;
        coverExcept['y'] = $target.offset().top - 5;
        coverExcept['w'] = $target.width() - 52;
        coverExcept['h'] = $target.height() + 10;
        coverExcept["z-index"] = z_Index_Top;
        shadow.coverExcept(coverExcept);
        var left = coverExcept.x + 0.5 * coverExcept.w - 170;
        var top = coverExcept.y + coverExcept.h;
        var panel = new App.Module.GuideTipPanel();
        guideTipDisplayManager.addChild(panel);
        panel.show({
            left: left,
            top: top,
            "z-index": z_Index_Top,
            backgroundImageUrl: panel.IMG.R_T_big_blue,
            content: {
                htmlText: "<H3>请参照示例填写促销方案信息！若您已填好请点击</H3>",
                top: 58,
                left: 40,
                width: 137
            }
        });
        var okButton = new App.Module.GuideTipOkButton();
        guideTipDisplayManager.addChild(okButton);
        okButton.show({
            left: left + 47,
            top: top + 94,
            "z-index": z_Index_Top,
            label: "我写好了",
            click: function (event) {
                if (validatePromote()) {
                    APP_BCGOGO.UserGuide.funLib["PRODUCT_ONLINE_GUIDE"]["_PRODUCT_ONLINE_GUIDE_CREATE_SPECIFIC_PROMOTE_FOURTH"]();
                }
            }
        });
        var cancelButton = new App.Module.GuideTipCancelButton();
        guideTipDisplayManager.addChild(cancelButton);
        cancelButton.show({
            label: "跳出引导",
            left: left + 270,
            top: top + 165,
            "z-index": z_Index_Top,
            click: function (event) {
                userGuide.dropFlow("PRODUCT_ONLINE_GUIDE");
            }
        });
    },
    _PRODUCT_ONLINE_GUIDE_CREATE_SPECIFIC_PROMOTE_FOURTH: function () {
        userGuide.clear();
        var currentFlowName = userGuide.currentFlowName;
        var currentStepName = userGuide.currentStepName;
        if (!currentFlowName || !currentStepName) return;
        var $target = $("#savePromotionsBtn");
        var z_Index_Top = getTopJqueryUiDialogZIndex();
        coverExcept['x'] = $target.offset().left - 5;
        coverExcept['y'] = $target.offset().top - 5;
        coverExcept['w'] = $target.width() + 10;
        coverExcept['h'] = $target.height() + 10;
        shadow.coverExcept(coverExcept);
        var left = coverExcept.x + 0.5 * coverExcept.w - 170;
        var top = coverExcept.y + coverExcept.h;
        var panel = new App.Module.GuideTipPanel();
        guideTipDisplayManager.addChild(panel);
        panel.show({
            left: left,
            top: top,
            backgroundImageUrl: panel.IMG.R_T_small_blue,
            "z-index": z_Index_Top,
            content: {
                htmlText: "<H3>请点击保存个性促销方案</H3>",
                top: 40,
                left: 35,
                width: 137
            }
        });
        var cancelButton = new App.Module.GuideTipCancelButton();
        guideTipDisplayManager.addChild(cancelButton);
        cancelButton.show({
            label: "跳出引导",
            left: left + 200,
            top: top + 135,
            "z-index": z_Index_Top,
            click: function (event) {
                userGuide.dropFlow("PRODUCT_ONLINE_GUIDE");
            }
        });

    },
    _PRODUCT_ONLINE_GUIDE_SPECIFIC_PROMOTE_SUCCESS: function () {
        userGuide.clear();
        var currentFlowName = userGuide.currentFlowName;
        var currentStepName = userGuide.currentStepName;
        if (!currentFlowName || !currentStepName) return;
        var $singlePromotion;
        $("#goodsInSalesTable input:checkbox[name='subBoxInSales']").each(function () {
            if ($(this).parent().parent().find(".gexing").size() > 0) {
                $singlePromotion = $(this).parent().parent().find(".gexing").eq(0);
                return false;
            }
        })
        if ($singlePromotion) {
            $singlePromotion.parent().parent().find(".tixing").show();
            cover['x'] = $singlePromotion.offset().left - 5;
            cover['y'] = $singlePromotion.offset().top - 5;

            var $target = $singlePromotion.parent().parent().find(".tixing");
            cover['w'] = $target.width() + 10;
            cover['h'] = $singlePromotion.height()+ $target.height() + 10;
            shadow.cover(cover);


            coverExcept['x'] = cover.x;
            coverExcept['y'] = cover.y ;
            coverExcept['w'] = cover.w;
            coverExcept['h'] = cover.h;
            shadow.coverExcept(coverExcept);
            var left = coverExcept.x + 0.5 * coverExcept.w - 170;
            var top = coverExcept.y + coverExcept.h;
            var panel = new App.Module.GuideTipPanel();
            guideTipDisplayManager.addChild(panel);
            panel.show({
                left: left,
                top: top,
                "z-index":panel.Z_INDEX.TOP,
                backgroundImageUrl: panel.IMG.R_T_small_blue,
                content: {
                    htmlText: "<H3>促销发布成功后，显示促销图标及促销方案!</H3>",
                    top: 40,
                    left: 35,
                    width: 137
                }
            });
            var okButton = new App.Module.GuideTipOkButton();
            guideTipDisplayManager.addChild(okButton);
            okButton.show({
                left: left + 47,
                top: top + 90,
                "z-index":okButton.Z_INDEX.TOP,
                label: "我知道了",
                click: function (event) {
                    $("#goodsInSalesTable .tixing").hide();
                    $.cookie("currentStepName", null);
                  APP_BCGOGO.Net.asyncAjax({
                      type: "POST",
                      url: "guide.do?method=updateCurrentUserGuideFlowFinished",
                      data: {flowName: "PRODUCT_ONLINE_GUIDE"},
                      cache: false,
                      dataType: "json",
                      success: function (result) {
                          userGuide.clear();
                          userGuide.load();
                      }
                  });
                }
            });

        }
    }
}