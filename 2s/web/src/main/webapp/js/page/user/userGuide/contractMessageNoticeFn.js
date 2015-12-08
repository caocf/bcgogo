userGuide.funLib["CONTRACT_MESSAGE_NOTICE"] = {
  //start main page
  CONTRACT_MESSAGE_NOTICE_BEGIN: function () {
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
          + '<br><p><span style="font-size:20px;margin: 10px;font-weight:bold;">您有新的关联请求通知啦！</span></p><br><br>'
          + '<p><span style="font-size:16px;margin: 10px;">担心提交的关联请求无人回应？来看看关联请求通知吧！</span></p>'
          + '</div>',
      left: 0.5 * ( $(document).width() - tip.width),
      top: 0.5 * ( $(document).height() - tip.height),

      associated: {
        label: "查看通知",
        "click": function (event) {
            userGuide.notRemind(userGuide.currentFlowName);
            APP_BCGOGO.Net.asyncAjax({
                type: "POST",
                url: "guide.do?method=nextUserGuideStep",
                cache: false,
                dataType: "json",
                success: function (result) {
                    if (result.success) {
                        userGuideInvoker("CONTRACT_MESSAGE_NOTICE_ENTER_MESSAGE");
                    }
                }
            });
        }
      }
    });
  },
    CONTRACT_MESSAGE_NOTICE_ENTER_MESSAGE:function(){
        userGuide.funLib["CONTRACT_MESSAGE_NOTICE"]["_enter_message"]();
    },
    _CONTRACT_MESSAGE_NOTICE_ENTER_MESSAGE:function(){
        userGuide.funLib["CONTRACT_MESSAGE_NOTICE"]["_enter_message"]();
    },
  _enter_message: function () {
    userGuide.clear();
    var me = APP_BCGOGO["UserGuide"],
        currentFlowName = me.currentFlowName,
        currentStepName = me.currentStepName,
        tip = {
          width: 464,
          height: 233
        };
    if (!currentFlowName || !currentStepName) return;
    var $target = $("#messageCenterNumber");
    coverExcept['x'] = $target.offset().left - 22;
    coverExcept['y'] = $target.offset().top;
    coverExcept['w'] = $target.width() + 50;
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
        htmlText: "<span style='font-size:13px;font-weight:bold;'>您现在有新消息，鼠标移入点击可查看消息状况!</span>"
      }
    });

    cancelButton.show({
      label: "跳出引导",
      left: left + 200,
      top: top + 135,
      click: function (event) {
        me.dropFlow("CONTRACT_MESSAGE_NOTICE");
        userGuide.clear();
      }
    });
  },

  _CONTRACT_MESSAGE_NOTICE_ENTER_NOTICE: function () {
    userGuide.clear();

    var me = APP_BCGOGO["UserGuide"],
        currentFlowName = me.currentFlowName,
        currentStepName = me.currentStepName,
        tip = {
          width: 464,
          height: 233
        };
    if (!currentFlowName || !currentStepName) return;
    var $target = $(".bcgogo-messagePopup-notification");
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

      App.Module.messagePopup.setIsMouseEventEnabled(false);
    panel.show({
      backgroundImageUrl: panel.IMG.L_T_small_blue,
      top: top,
      left: left,
      content: {
        htmlText: "<span style='font-size:18px;font-weight:bold;'>点击后可以查看关联请求回复通知!</span>"
      }
    });
    cancelButton.show({
        label: "跳出引导",
      left: left + 200,
      top: top + 135,
      click: function (event) {
        App.Module.messagePopup.setIsMouseEventEnabled(true);
        me.dropFlow("CONTRACT_MESSAGE_NOTICE");
        userGuide.clear();
      }
    });
  },

  _CONTRACT_MESSAGE_NOTICE_SIMILAR_SUPPLIER: function () {

    userGuide.clear();
    var me = APP_BCGOGO["UserGuide"],
        currentFlowName = me.currentFlowName,
        currentStepName = me.currentStepName,
        tip = {
          width: 464,
          height: 233
        };
    if (!currentFlowName || !currentStepName) return;
    var $target = $("#noticesContent .moreSimilarSupplier");
    if ($target.length == 0) {
      return;
    }
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
        htmlText: "<span style='font-size:12px;font-weight:bold;'>供应商资料可能相似!</span>"
      }
    });

    var okButtonFirst = new App.Module.GuideTipOkButton();
    guideTipDisplayManager.addChild(okButtonFirst);
    okButtonFirst.show({
      left: left + 47,
      top: top + 85,
      label: "下一步",
      click: function (event) {
        shadow.clear();
        panel.remove();
        okButtonFirst.remove();
        APP_BCGOGO.UserGuide.funLib["CONTRACT_MESSAGE_NOTICE"]["_CONTRACT_MESSAGE_NOTICE_SUPPLIER_MESSAGE"]();
      }
    });

    cancelButton.show({
      label: "跳出引导",
      left: left + 200,
      top: top + 135,
      click: function (event) {
        me.dropFlow("CONTRACT_MESSAGE_NOTICE");
        shadow.clear();
        panel.remove();
        cancelButton.remove();
      }
    });


  },
  //查看供应商信息
  _CONTRACT_MESSAGE_NOTICE_SUPPLIER_MESSAGE: function () {
    userGuide.clear();
    shadow.clear();
    var $target = $("#noticesContent .showSupplierBtn"),
        me = APP_BCGOGO.UserGuide,
        currentFlowName = me.currentFlowName,
        currentStepName = me.currentStepName,
        tip = {
          width: 464,
          height: 233
        };
    if ($target.length == 0)return;
    if (!currentFlowName || !currentStepName) return;
    coverExcept['x'] = $target.offset().left - 45;
    coverExcept['y'] = $target.offset().top - 3;
    coverExcept['w'] = $target.width() + 50;
    coverExcept['h'] = $target.height() + 10;
    shadow.coverExcept(coverExcept);

    var left = coverExcept.x;
    var top = coverExcept.y + coverExcept.h + 5;

    var panel = new App.Module.GuideTipPanel();
    var cancelButton = new App.Module.GuideTipCancelButton();
    guideTipDisplayManager.addChild(cancelButton);
    guideTipDisplayManager.addChild(panel);
    panel.show({
      backgroundImageUrl: panel.IMG.L_T_small_blue,
      left: left,
      top: top,
      content: {
        htmlText: "<span style='font-size:16px;'>请点击查看供应商信息!</span>"
      }
    });

    cancelButton.show({
      label: "跳出引导",
      left: (coverExcept.x + 200),
      top: (coverExcept.y + 135),
      click: function (event) {
        me.dropFlow("CONTRACT_MESSAGE_NOTICE");
          userGuide.clear();
      }
    });
  },


  _CONTRACT_MESSAGE_NOTICE_SIMILAR_CUSTOMER: function () {

      userGuide.clear();
      var me = APP_BCGOGO["UserGuide"],
          currentFlowName = me.currentFlowName,
          currentStepName = me.currentStepName,
          tip = {
              width: 464,
              height: 233
          };
      if (!currentFlowName || !currentStepName) return;
      var $target = $("#noticesContent .moreSimilarCustomer");
      if ($target.length == 0) {
          return;
      }
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
          "z-index":panel.Z_INDEX.TOP,
          content: {
              htmlText: "<span style='font-size:17px;font-weight:bold;'>您已成功关联该客户啦！您可查看他的信息哦!</span>"
          }
      });
      var panelB = new App.Module.GuideTipPanel();
      guideTipDisplayManager.addChild(panelB);
      panelB.show({
          backgroundImageUrl: panel.IMG.L_T_big_green,
          top: top + 70,
          left: left + 101,
          content: {
              top: 62,
              left: 35,
              htmlText: "<span style='font-size:18px;font-weight:bold;'>提示：该供客户可能与您已有的客户重复，您可进入客户列表确认!</span>"
          }
      });
      var okButtonFirst = new App.Module.GuideTipOkButton();
      guideTipDisplayManager.addChild(okButtonFirst);
      okButtonFirst.show({
          left: left + 181,
          top: top + 200,
          label: "下一步",
          click: function (event) {
              shadow.clear();
              panel.remove();
              okButtonFirst.remove();
              APP_BCGOGO.UserGuide.funLib["CONTRACT_MESSAGE_NOTICE"]["_CONTRACT_MESSAGE_NOTICE_CUSTOMER_MESSAGE"]();
          }
      });

    var okButtonFirst = new App.Module.GuideTipOkButton();
    guideTipDisplayManager.addChild(okButtonFirst);
    okButtonFirst.show({
      left: left + 47,
      top: top + 85,
      label: "下一步",
      click: function (event) {
        shadow.clear();
        panel.remove();
        okButtonFirst.remove();
        APP_BCGOGO.UserGuide.funLib["CONTRACT_MESSAGE_NOTICE"]["_CONTRACT_MESSAGE_NOTICE_CUSTOMER_MESSAGE"]();
      }
    });

    cancelButton.show({
      label: "跳出引导",
      left: left + 326,
      top: top + 242,
      click: function (event) {
        me.dropFlow("CONTRACT_MESSAGE_NOTICE");
        userGuide.clear();
      }
    });
  },


  //查看供应商信息
  _CONTRACT_MESSAGE_NOTICE_CUSTOMER_MESSAGE: function () {
      userGuide.clear();
      shadow.clear();
      var $target = $("#noticesContent .showCustomerBtn"),
          me = APP_BCGOGO.UserGuide,
          currentFlowName = me.currentFlowName,
          currentStepName = me.currentStepName,
          tip = {
              width: 464,
              height: 233
          };
      if ($target.length == 0)return;
      if (!currentFlowName || !currentStepName) return;
      coverExcept['x'] = $target.offset().left - 45;
      coverExcept['y'] = $target.offset().top - 3;
      coverExcept['w'] = $target.width() + 50;
      coverExcept['h'] = $target.height() + 10;
      shadow.coverExcept(coverExcept);

      var left = coverExcept.x;
      var top = coverExcept.y + coverExcept.h + 5;

      var panel = new App.Module.GuideTipPanel();
      var cancelButton = new App.Module.GuideTipCancelButton();
      guideTipDisplayManager.addChild(cancelButton);
      guideTipDisplayManager.addChild(panel);
      panel.show({
          backgroundImageUrl: panel.IMG.L_T_small_blue,
          left: left,
          top: top,
          content: {
              htmlText: "<span style='font-size:16px;'>请点击查看客户信息!</span>"
          }
      });

      cancelButton.show({
          left: (coverExcept.x + 196),
          top: (coverExcept.y + 150),
          label: "跳出引导",
          click: function (event) {
              me.dropFlow("CONTRACT_MESSAGE_NOTICE");
              userGuide.clear();
          }
      });
  },


   //有相似供应商，供应商列表页面提示合并
  _CONTRACT_MESSAGE_NOTICE_GUIDE_SUPPLIER_MESSAGE: function () {
    userGuide.clear();
    var $target = $("#mergeSupplierBtn"),
        me = APP_BCGOGO.UserGuide,
        currentFlowName = me.currentFlowName,
        currentStepName = me.currentStepName,
        tip = {
          width: 464,
          height: 233
        };
    if (!currentFlowName || !currentStepName) return;
      cover['x'] = $target.offset().left - 5;
      cover['y'] = $target.offset().top - 6;
      cover['w'] = $target.width() + 10;
      cover['h'] = $target.height() + 11.5;
      shadow.cover(cover);
    coverExcept['x'] = $target.offset().left - 5;
    coverExcept['y'] = $target.offset().top - 6;
    coverExcept['w'] = $target.width() + 10;
    coverExcept['h'] = $target.height() + 11.5;
    shadow.coverExcept(coverExcept);

    var cancelButton = new App.Module.GuideTipCancelButton();
    var panel = new App.Module.GuideTipPanel();
    var okButton = new App.Module.GuideTipOkButton();
    guideTipDisplayManager.addChild(cancelButton);
    guideTipDisplayManager.addChild(panel);
    guideTipDisplayManager.addChild(okButton);
    panel.show({
      backgroundImageUrl: panel.IMG.L_T_big_blue,
      left: coverExcept.x + coverExcept.w - 60,
      top: coverExcept.y + 29,
      content: {
        top: 60,
        left: 35,
        htmlText: "<span style='font-size:16px;font-weight:bold;'>若为同一供应商，您可进行合并操作哦！</span>"
      }
    });
    okButton.show({
      left: coverExcept.x - coverExcept.w + 235,
      top: coverExcept.y +162,
      label: "我知道了",
      width: 60,
      click: function (event) {

        userGuide.clear();
        userGuide.coverAll();
        var startPanel = new App.Module.GuideTipStartPanel();
        guideTipDisplayManager.addChild(startPanel);
        startPanel.show({
          htmlText: '<div>'
              + '<p><span style="font-size:20px; margin: 10px 0 20px;">恭喜您已成功处理该条消息！</span></p>'
              + '</div>',
          left: 0.5 * ( $(document).width() - tip.width),
          top: 0.5 * ( $(document).height() - tip.height),

          associated: {
            label: "我知道了",
            "click": function (event) {
              userGuide.clear();
              shadow.clear();
              if ($.cookie("currentStepName") == "CONTRACT_MESSAGE_NOTICE_CHECK_MESSAGE") {
                $.cookie("currentStepName", "CONTRACT_MESSAGE_NOTICE_GUIDE_MESSAGE");
              }
              APP_BCGOGO.Net.asyncAjax({
                    type: "POST",
                    url: "guide.do?method=nextUserGuideStep",
                    cache: false,
                    dataType: "json",
                    success: function (result) {
                        if (result.success) {
                        }
                    }
                });
            }
          }
        });

      }
    });

//      cancelButton.show({
//          left: (coverExcept.x + 268),
//          top: (coverExcept.y+205),
//          label: "跳出引导",
//          click: function (event) {
//              me.dropFlow("CONTRACT_MESSAGE_NOTICE");
//              userGuide.clear();
//          }
//      });
  },


   //有相似客户，供应商列表页面提示合并
  _CONTRACT_MESSAGE_NOTICE_GUIDE_CUSTOMER_MESSAGE: function () {
    userGuide.clear();
    var $target = $("#mergeCustomerBtn"),
        me = APP_BCGOGO.UserGuide,
        currentFlowName = me.currentFlowName,
        currentStepName = me.currentStepName,
        tip = {
          width: 464,
          height: 233
        };
    if (!currentFlowName || !currentStepName) return;
      cover['x'] = $target.offset().left - 5;
      cover['y'] = $target.offset().top - 6;
      cover['w'] = $target.width() + 10;
      cover['h'] = $target.height() + 11.5;
      shadow.cover(cover);

    coverExcept['x'] = $target.offset().left - 5;
    coverExcept['y'] = $target.offset().top - 6;
    coverExcept['w'] = $target.width() + 10;
    coverExcept['h'] = $target.height() + 11.5;
    shadow.coverExcept(coverExcept);

    var cancelButton = new App.Module.GuideTipCancelButton();
    var panel = new App.Module.GuideTipPanel();
    var okButton = new App.Module.GuideTipOkButton();
    guideTipDisplayManager.addChild(cancelButton);
    guideTipDisplayManager.addChild(panel);
    guideTipDisplayManager.addChild(okButton);
    panel.show({
      backgroundImageUrl: panel.IMG.L_T_big_blue,
      left: coverExcept.x + coverExcept.w -78,
      top: coverExcept.y + 29,
      content: {
        top: 60,
        left: 35,
        htmlText: "<span style='font-size:16px;font-weight:bold;'>若为同一客户，您可进行合并操作哦！</span>"
      }
    });
    okButton.show({
      left: coverExcept.x + coverExcept.w +29,
      top: coverExcept.y + 156,
      label: "我知道了",
      width: 60,
      click: function (event) {

        userGuide.clear();
        userGuide.coverAll();
        var startPanel = new App.Module.GuideTipStartPanel();
        guideTipDisplayManager.addChild(startPanel);
        startPanel.show({
          htmlText: '<div>'
              + '<p><span style="font-size:20px; margin: 10px 0 20px;">恭喜您已成功处理该条消息！</span></p>'
              + '</div>',
          left: 0.5 * ( $(document).width() - tip.width),
          top: 0.5 * ( $(document).height() - tip.height),

          associated: {
            label: "我知道了",
            "click": function (event) {
              if ($.cookie("currentStepName") == "CONTRACT_MESSAGE_NOTICE_CHECK_MESSAGE") {
                $.cookie("currentStepName", "CONTRACT_MESSAGE_NOTICE_GUIDE_MESSAGE")
              }
              userGuide.clear();
              shadow.clear();
              APP_BCGOGO.Net.asyncAjax({
                    type: "POST",
                    url: "guide.do?method=nextUserGuideStep",
                    cache: false,
                    dataType: "json",
                    success: function (result) {
                        if (result.success) {
                        }
                    }
                });
            }
          }
        });

      }
    });

//      cancelButton.show({
//          left: (coverExcept.x + coverExcept.w + 153),
//          top: (coverExcept.y + coverExcept.h + 158),
//          label: "跳出引导",
//          click: function (event) {
//              me.dropFlow("CONTRACT_MESSAGE_NOTICE");
//              userGuide.clear();
//          }
//    });
  },


  _CONTRACT_MESSAGE_NOTICE_GUIDE_MESSAGE_SUCCESS: function () {
    userGuide.clear();
    userGuide.coverAll();
    var  me = APP_BCGOGO.UserGuide,
        tip = {
          width: 464,
          height: 233
        };

    var startPanel = new App.Module.GuideTipStartPanel();
    var cancelButton = new App.Module.GuideTipCancelButton();
    guideTipDisplayManager.addChild(startPanel);
    startPanel.show({
      htmlText: '<div>'
          + '<p><span style="font-size:20px; margin: 10px 0 20px;">恭喜您已成功处理该条消息！</span></p>'
          + '</div>',
      left: 0.5 * ( $(document).width() - tip.width),
      top: 0.5 * ( $(document).height() - tip.height),

      associated: {
        label: "我知道了",
        "click": function (event) {
          if ($.cookie("currentStepName") == "CONTRACT_MESSAGE_NOTICE_CHECK_MESSAGE") {
            $.cookie("currentStepName", "CONTRACT_MESSAGE_NOTICE_GUIDE_MESSAGE")
          }
          userGuide.clear();
          shadow.clear();
          APP_BCGOGO.Net.asyncAjax({
                    type: "POST",
                    url: "guide.do?method=nextUserGuideStep",
                    cache: false,
                    dataType: "json",
                    success: function (result) {
                        if (result.success) {
                        }
                    }
                });
        }
      }
    });
//
//    cancelButton.show({
//      left: (coverExcept.x - 100),
//      top: (coverExcept.y),
//        label: "跳出引导",
//      click: function (event) {
//        me.dropFlow("CONTRACT_MESSAGE_NOTICE");
//          userGuide.clear();
//      }
//    });
  },


  _CONTRACT_MESSAGE_NOTICE_CANCEL_ASSOCIATION: function () {

    userGuide.clear();
    var me = APP_BCGOGO["UserGuide"],
        currentFlowName = me.currentFlowName,
        currentStepName = me.currentStepName,
        tip = {
          width: 464,
          height: 233
        };
    if (!currentFlowName || !currentStepName) return;
    var $target = $("#noticeMessageSpan0");
    if ($target.length == 0) {
      return;
    }
    coverExcept['x'] = $target.offset().left - 22;
    coverExcept['y'] = $target.offset().top  -10;
    coverExcept['w'] = $target.width() + 280;
    coverExcept['h'] = $target.height() + 15;
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
        top:60,
        htmlText: "<span style='font-size:18px;font-weight:bold;'>有店铺与您取消了关联！点击将其标为已读信息哦!</span>"
      }
    });

    cancelButton.show({
      label: "跳出引导",
      left: left + 222,
      top: top + 170,
      click: function (event) {
        me.dropFlow("CONTRACT_MESSAGE_NOTICE");
        userGuide.clear();
      }
    });
  },

    //拒绝关联请求通知指引
  _CONTRACT_MESSAGE_NOTICE_REJECT_ASSOCIATION: function () {

    userGuide.clear();
    var me = APP_BCGOGO["UserGuide"],
        currentFlowName = me.currentFlowName,
        currentStepName = me.currentStepName,
        tip = {
          width: 464,
          height: 233
        };
    if (!currentFlowName || !currentStepName) return;
    var $target = $("#noticeMessageSpan0");
    if ($target.length == 0) {
      return;
    }
    coverExcept['x'] = $target.offset().left - 22;
    coverExcept['y'] = $target.offset().top  -10;
    coverExcept['w'] = $target.width() + 280;
    coverExcept['h'] = $target.height() + 15;
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
        htmlText: "<span style='font-size:16px;font-weight:bold;'>该店铺拒绝了您的请求！点击将其标为已读信息哦！</span>"
      }
    });

    cancelButton.show({
      label: "跳出引导",
      left: left + 200,
      top: top + 135,
      click: function (event) {
        me.dropFlow("CONTRACT_MESSAGE_NOTICE");
        userGuide.clear();
      }
    });
  },

    //无相似客户关联成功通知
    _CONTRACT_MESSAGE_NOTICE_NO_SIMILAR_CUSTOMER: function () {
        userGuide.clear();
        var me = APP_BCGOGO["UserGuide"],
            currentFlowName = me.currentFlowName,
            currentStepName = me.currentStepName;
        if (!currentFlowName || !currentStepName) return;
        var $target = $("#noticesContent .showCustomerBtn");

        $target = $target.eq(0).parent();
        coverExcept['x'] = $target.offset().left;
        coverExcept['y'] = $target.offset().top;
        coverExcept['w'] = $target.width();
        coverExcept['h'] = $target.height();
        shadow.coverExcept(coverExcept);
        var left = coverExcept.x+0.5*coverExcept.w +31;
        var top = coverExcept.y + coverExcept.h + 5;
        var panel = new App.Module.GuideTipPanel();
        var cancelButton = new App.Module.GuideTipCancelButton();
        guideTipDisplayManager.addChild(panel);
        guideTipDisplayManager.addChild(cancelButton);
        panel.show({
            backgroundImageUrl: panel.IMG.R_T_big_blue,
            top: top,
            left: left,
            content: {
                top:60,
                htmlText: "<span style='font-size:18px;font-weight:bold;'>您已成功关联该客户啦!点击【查看客户信息】可查看他的详细信息哦!</span>"
            }
        });
        cancelButton.show({
            label: "跳出引导",
            left: left + 210,
            top: top + 166,
            click: function (event) {
                me.dropFlow("CONTRACT_MESSAGE_NOTICE");
              userGuide.clear();
            }
        });

    },    //无相似供应商关联成功通知


    //无相似供应商关联成功通知
    _CONTRACT_MESSAGE_NOTICE_NO_SIMILAR_SUPPLIER: function () {
        userGuide.clear();
        var me = APP_BCGOGO["UserGuide"],
            currentFlowName = me.currentFlowName,
            currentStepName = me.currentStepName;
        if (!currentFlowName || !currentStepName) return;
        var $target = $("#noticesContent .showSupplierBtn");

        $target = $target.eq(0).parent();
        coverExcept['x'] = $target.offset().left;
        coverExcept['y'] = $target.offset().top;
        coverExcept['w'] = $target.width();
        coverExcept['h'] = $target.height();
        shadow.coverExcept(coverExcept);
        var left = coverExcept.x+0.5*coverExcept.w +31;
        var top = coverExcept.y + coverExcept.h + 5;
        var panel = new App.Module.GuideTipPanel();
        var cancelButton = new App.Module.GuideTipCancelButton();
        guideTipDisplayManager.addChild(panel);
        guideTipDisplayManager.addChild(cancelButton);
        panel.show({
            backgroundImageUrl: panel.IMG.R_T_big_blue,
            top: top,
            left: left,
            content: {
                top:50,
                htmlText: "<span style='font-size:18px;font-weight:bold;'>您已成功关联该供应商啦!点击【查看供应商信息】可查看他的详细信息哦!</span>"
            }
        });
        cancelButton.show({
            label: "跳出引导",
            left: left + 210,
            top: top + 166,
            click: function (event) {
                me.dropFlow("CONTRACT_MESSAGE_NOTICE");
              userGuide.clear();
            }
        });

    },

    //关联客户，对方接受之后，且没有相似客户跳转
    _CONTRACT_MESSAGE_NOTICE_GUIDE_MESSAGE: function () {
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
                + '<p><span style="font-size:20px; margin: 10px 0 20px;">恭喜您已成功完成！</span></p>'
                + '<p><span style="font-size:20px;  margin:10px 0 20px;">您可查看该客户的详细信息哦！</span></p>'
                + '</div>',
            left: 0.5 * ( $(document).width() - tip.width),
            top: 0.5 * ( $(document).height() - tip.height),

            associated: {
                label: "我知道了",
                "click": function (event) {
                    if ($.cookie("currentStepName") == "CONTRACT_MESSAGE_NOTICE_CHECK_MESSAGE") {
                      $.cookie("currentStepName", "CONTRACT_MESSAGE_NOTICE_GUIDE_MESSAGE")
                    }
                    APP_BCGOGO.Net.asyncAjax({
                        type: "POST",
                        url: "guide.do?method=nextUserGuideStep",
                        cache: false,
                        dataType: "json",
                        success: function (result) {
                            if (result.success) {
                                userGuide.finishFlow()
                            }
                        }
                    });
                }
            }
        });
    } ,

    //关联供应商，对方接受之后，且没有相似供应商跳转到供应商详细页面
    _CONTRACT_MESSAGE_NOTICE_GUIDE_MESSAGE_SUPPLIER: function () {
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
                + '<p><span style="font-size:20px; margin: 10px 0 20px;">恭喜您已成功完成！</span></p>'
                + '<p><span style="font-size:20px;  margin:10px 0 20px;">您可查看该供应商的详细信息哦！</span></p>'
                + '</div>',
            left: 0.5 * ( $(document).width() - tip.width),
            top: 0.5 * ( $(document).height() - tip.height),

            associated: {
                label: "我知道了",
                "click": function (event) {
                    if ($.cookie("currentStepName") == "CONTRACT_MESSAGE_NOTICE_CHECK_MESSAGE") {
                      $.cookie("currentStepName", "CONTRACT_MESSAGE_NOTICE_GUIDE_MESSAGE")
                    }
                    APP_BCGOGO.Net.asyncAjax({
                        type: "POST",
                        url: "guide.do?method=nextUserGuideStep",
                        cache: false,
                        dataType: "json",
                        success: function (result) {
                            if (result.success) {
                                userGuide.finishFlow()
                            }
                        }
                    });
                }
            }
        });
    }


};
