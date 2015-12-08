/**
 * @依赖 js/base.js;  js/application.js;  js/module/invoiceCommon.js;  js/module/bcgogoValidate.js
 */

//TODO 采购单脚本文件
var trCount;
var idPrefixLastModified;
var flag;
var submitFlag;

var invoiceCommon = APP_BCGOGO.Module.wjl.invoiceCommon;
var bcgogoAjaxQuery = APP_BCGOGO.Module.wjl.ajaxQuery;
var mySlider;


//init 采购单验证器
$(document).ready(function() {

//    initDuiZhanInfo();
//    $("#clickShow").hover(function(){
//        $(".tuihuo_first table tr .xiangxi_td a #clickShow").css("color","#FD5300");
//    },function(){
//        $(".tuihuo_first table tr .xiangxi_td a #clickShow").css("color","#007CDA");
//    });
    //采购查看页面用
    $(".J-priceInfo").live("mouseenter", function(event) {
        event.stopImmediatePropagation();

        var _currentTarget = $(event.target).find(".alert");
        _currentTarget.show();

        _currentTarget.mouseleave(function(event) {
            if($(event.relatedTarget)[0] != $(event.target).parents(".J-priceInfo")[0]) {
                _currentTarget.hide();
            }
        });

    }).live("mouseleave", function(event) {
            event.stopImmediatePropagation();
            var _currentTarget = $(event.target).find(".alert");
            if(event.relatedTarget != _currentTarget[0]) {
                _currentTarget.hide();
            }
        });
    //初始化推拉层

    mySlider = new tableUtil.sliderBar({
        mainLayer: '.slider-main-area',
        mainTable: '.slider-main-table',

        subLayer: '.slider-sub-area',
        subTable: '.slider-sub-table',
        slideExtraLength:-1,
        slideExtraTop: 10,
        subLayerWidth: 350
    });


//    tableUtil.tableStyle.hasSubTable("#table_productNo", "#table_productDetail", ".titleBg,.s_tabelBorder");
  var bcgogoValidator = APP_BCGOGO.Module.wjl.bcgogoValidator;
  var rules = {
    supplier: {
      required: true,
      maxlength: 30
    },
    deliveryDateStr: {
      required: true,
      dateISO: true
    },
    mobile: {
      mobile: true
    }
  };
  var messages = {
    supplier: {
      required: "请输入供应商",
      maxlength: "供应商有效长度30"
    },
    deliveryDateStr: {
      required: "请输入预计交货日期",
      dateISO: "预计交货日期请输入正确格式的日期"
    },
        mobile: {
            mobile: "输入的手机号码可能不正确，为了不影响您的后续使用，请确认后重新输入！"
    }
  };
  bcgogoValidator.setRules(rules);
  bcgogoValidator.setMessages(messages);
  bcgogoValidator.setConfirmMessage("是否确认采购?");
  bcgogoValidator.setDisabledDom($("#purchaseSaveBtn,#printBtn,#cancelBtn,#saveDraftBtn"));
  bcgogoValidator.validate($("#purchaseOrderForm")[0]);


  // TODO 删除这个函数 ，这样做会把代码搞混淆而已, 起不到任何的节省劳力的作用
  String.prototype.replaceAll = function(s1, s2) {
    return this.replace(new RegExp(s1, "gm"), s2);
  }

  //供应商栏
  $(document).bind("keydown", function(event) {

    //获得触发元素
    var target = event.target;
    if($(target).hasClass("supplierSuggestion")) {
      return;
    }
    if(target.type != "text" && !$(target).hasClass("j_btn_i_operate")) {
      return;
    }

    //获得按键名
    var keyName = GLOBAL.Interactive.keyNameFromEvent(event);
    //当这些按键的时候触发以下的事件
    if(keyName.search(/enter|right|left/g) != -1) {
      if($(target).attr("id") && $(target).attr("id").endWith(".commodityCode")) {
        if(keyName == "left") {
          return;
        }
      }
      if($("#div_brandvehiclelicenceNo").css("display") != "none") return;
      //修正 ie8 不触发 blur bug
      invoiceCommon.keyBoardSelectTarget(target, keyName);
    }
  });

  $("#table_productNo").bind("keydown", function(event) {
    var target = event.target,
      isMatchProductName = false;
    $("#table_productNo input[id$='\\.productName']").each(function(index) {
      if(target.id == this.id) isMatchProductName = true;
    });
    if(isMatchProductName == false) return;

    var keyName = GLOBAL.Interactive.keyNameFromEvent(event);
    //TODO 当库存明细页面显示时，按空格隐藏,否则显示库存页面
    //        if (keyName == "space") {
    //            if ($("#iframe_PopupBox").css("display") == "block") {
    //                $("#mask").hide();
    //                $("#iframe_PopupBox").hide();
    //            } else {
    //                $("#div_brand").hide();
    //                searchInventoryIndex(target);
    //            }
    //            return;
    //        }
    //TODO  按上、下、左、右键，使相应的文本框被选择并获得焦点  Begin-->
    if(keyName == "enter") {
      if($(this).attr("id") == $(".item :text:last").attr("id")) {
        $(this).parent().next().children(".opera2").trigger("click");
        invoiceCommon.selectAndFocusByNode($($(this).parent().parent().next().children("td").get(1)).children(":text"));
      }
    }
  });

  //button 右下事件     //TODO 取消按钮 按右或下，则使供应商被选择并获得焦点
  $(document).bind("keydown", function(event) {
    if(!$(event.target).hasClass("j_btn_i_operate")) return;
    //TODO 确认采购、打印、取消，被选择后回车，则单击该元素
    var keyName = GLOBAL.Interactive.keyNameFromEvent(event);
    if(keyName == "enter") event.target.onclick();
  });

    $("#purchaseSaveBtn,#purchaseModify_div").click(function () { //TODO “确认采购”按钮被单击后的处理脚本
        var btnType;
        if ($(this)[0] == $("#purchaseSaveBtn")[0]) {
            btnType = "SAVE";
        } else if ($(this)[0] == $("#purchaseModify_div")[0]) {
            btnType = "MODIFY";
        }

        //自动删除最后的空白行
        var $last_tr = $("#table_productNo").find("tbody").find("tr:last");
        var allTrSize = $("#table_productNo tbody tr.item").size();
        while (allTrSize>1 && $last_tr.index() >= 3 && checkEmptyRow($last_tr)) {
            $last_tr.find("[a[id$='.deletebutton']").click();
            $last_tr = $("#table_productNo").find("tbody").find("tr:last");
        }
        if (!validateAll()) {
            return false;
        }
        if (trCount >= 2 && checkTheSame()) {
            //采购单检查是否相同
            alert("单据有重复内容，请修改或删除。");
            return false;
        }
        if (invoiceCommon.checkSameCommodityCode("item")) {
            alert("商品编码有重复内容，请修改或删除。");
            return false;
        }
        if ($.trim($("#deliveryDateStr").val())) {
            // 判断时间不能早于系统当前时间
            if (GLOBAL.Util.getDate($("#orderVestDate").val()).getTime() - GLOBAL.Util.getDate($("#deliveryDateStr").val()).getTime() > 0) {
                alert("请选择采购日期以后的日期。");
                return;
            }
        } else {
            $("#btnType").val('purchaseSaveBtn');
            $("#deliveryDateDialog").dialog("open");
            return;
        }
        flag = 1;
        //验证为null的置为空
        invoiceCommon.reSetDomVal($("#invoiceCategory")[0], "null", "");
        invoiceCommon.reSetDomVal($("#settlementType")[0], "null", "");

        if (checkSupplierInfo() == false) {
            return;
        }

        //TODO 库存量为空字符，则赋上默认值
        $(".itemInventoryAmount").each(function () {
            invoiceCommon.reSetDomVal(this, "", 0);
        });
        $("#div_brand").hide();

        if ($("#supplier").val() && !$.trim($("#mobile").val())) {
            $("#inputMobile").dialog("open");
            $("#inputMobile").attr("type", btnType);
            return;
        }

        $("#purchaseOrderForm").ajaxSubmit({
            url: "RFbuy.do?method=ajaxValidatorPurchaseOrderDTOSave",
            dataType: "json",
            type: "POST",
            success: function (json) {
                if (json.success) {
                    if (!idPrefixLastModified) {
                        if (btnType == "SAVE") {
                            purchaseOrderSubmit();
                        } else if (btnType = "MODIFY") {
                            purchaseOrderModifySubmit();
                        }
                        return false;
                    }
                    if (btnType == "SAVE") {
                        operation = "purchaseOrderSubmit";
                    } else if (btnType = "MODIFY") {
                        operation = "purchaseOrderModifySubmit";
                    }
                    exactSearchInventorySearchIndex();
                    $("#saveDraftBtn").attr('disabled', true);
                } else if (!json.success && json.operation == "confirm_deleted_product") {
                    nsDialog.jConfirm(json.msg, null, function (resultVal) {
                        if (resultVal) {
                            if (!idPrefixLastModified) {
                                if (btnType == "SAVE") {
                                    purchaseOrderSubmit();
                                } else if (btnType = "MODIFY") {
                                    purchaseOrderModifySubmit();
                                }
                                return false;
                            }
                            if (btnType == "save") {
                                operation = "purchaseOrderSubmit";
                            } else if (btnType = "MODIFY") {
                                operation = "purchaseOrderModifySubmit";
                            }
                            exactSearchInventorySearchIndex();
                            $("#saveDraftBtn").attr('disabled', true);
                        }
                    });

                } else if (!json.success) {
                    nsDialog.jAlert(json.msg);
                    return false;
                }
            },
            error: function (json) {
                nsDialog.jAlert("网络异常，请联系客服");
            }
        });
        return false;
    });


  //关闭按钮    //TODO 取消按钮，重载页面
  $("#cancelBtn").click(function() {
    window.location = "RFbuy.do?method=create";
    });

    $("#mobile").blur(function() {
        if(this.value) {
            var landline = document.getElementById("landline");
            var mobile = document.getElementById("mobile");
            if(mobile.value != "" && mobile.value != null) {
                check.inputSupplierMobileBlur2(mobile, landline);
            }
        }
    });

    $(".itemAmount").bind('change', function () {
        setTotal();
    });
    $(".itemPrice").bind('change', function () {
        var price = APP_BCGOGO.StringFilter.inputtingPriceFilter($(this).val(), 2);
        $(this).val(price);
        $(this).attr("lastValue",$(this).val());
        setTotal();
    });


    $(".itemAmount,.itemPrice").bind('blur', function () {
        dataTransition.roundingSpanNumber("totalSpan");
        var format = $(this).val();
        format = dataTransition.rounding(format, 2);
        $(this).val(format);
        setTotal();
    });

    $(".itemPrice").bind("focus", function () {
        $(this).attr("lastValue", $(this).val());
    });

  //TODO 采购单列表单行模版
  trCount = $(".item").size();

  //增加行      //TODO 加号按钮单击触发的脚本
  $(document).bind('click', function(event) {
    var target = event.target;
    if(target.className != "opera2") return;
    //TODO 在旧单据 除改单外 中不允许新增一行
    if($("#id").val() && $("#status").val() && $("#status").val() != "SELLER_PENDING") return false;
    var ischeck = checkVehicleInfo(target); //TODO 检查改行数据是否符合验证规则
    if(!ischeck && ischeck != null) return;

    //采购单检查是否相同
    if(trCount >= 2) if(checkTheSame()) {
      alert("单据有重复内容，请修改或删除。");
      return false;
    }
    if(invoiceCommon.checkSameCommodityCode("item")) {
      alert("商品编码有重复内容，请修改或删除。");
      return false;
    }
    //车辆维修美容判断商品是否是新商品
    if($(target).prev() && $(target).prev().attr("id")) {
      $("#div_brand").hide();
      if(idPrefixLastModified == null) {
        purchaseOrderAdd();
        return false;
      }
      operation = "purchaseOrderAdd";
      exactSearchInventorySearchIndex();
      return false;
    } else {
      purchaseOrderAdd();
      return false;
    }
  });
  //删除行  //TODO 点击减号所做的删除改行的操作
  $(document).bind('click', function(event) {
    var target = event.target;
    if(target.className != "opera1") return;
    if($("#id").val() && $("#status").val() && $("#status").val() != "SELLER_PENDING") return false;
    var idPrefix = $(target).attr("id").split(".")[0];
    if(idPrefixLastModified == idPrefix) idPrefixLastModified = null;

    $(target).closest("tr").remove();
    $("#" + idPrefix + "\\.upperLimit").closest("tr").remove();
    isShowAddButton();
    setTotal();
    trCount = $(".item").size();

    mySlider.setSubLayerHeight();

    //动态设置每行的高度
    mySlider.setRowHeight();

//    tableUtil.setRowBackgroundColor("#table_productNo", "#table_productDetail", ".table_title,.s_tabelBorder", 'odd');
  });
  //初始化的时候判断是否显示+按钮
  isShowAddButton();
  setTotal();

  //判断单据是否已经生成，如果已经生成将所有input锁定 隐藏一些功能按钮
  if($("#id").val() && $("#status").val() && ($("#status").val() == "SELLER_DISPATCH" || $("#status").val() == "SELLER_STOCK")) { //已发货
    $("#saveDraftOrder_div,#purchaseSave_div").each(function() {
      $(this).hide();
    });
    $("#purchaseOrderForm input").not($("#printBtn,#cancelBtn,#nullifyBtn,#copyInput,#inventoryBtn")).each(function() {
      $(this).attr("disabled", true);
    });
    $("#printBtn,#cancelBtn,#nullifyBtn,#copyInput,#inventoryBtn").each(function() {
      $(this).attr("disabled", false);
    });
  } else if($("#id").val() && $("#status").val() && $("#status").val() == "SELLER_PENDING") {
    $("#saveDraftOrder_div,#purchaseSave_div").each(function() {
      $(this).hide();
    });
    $("#supplier").attr("disabled", true);
    //        $("#purchaseOrderForm input").not($("#printBtn,#cancelBtn,#nullifyBtn,#copyInput")).each(function () {
    //            $(this).attr("disabled", true);
    //        });
    //        $("#printBtn,#cancelBtn,#nullifyBtn,#copyInput").each(function () {
    //            $(this).attr("disabled", false);
    //        });
  } else if($("#id").val() && $("#status").val() && $("#status").val() != "SELLER_PENDING") {
    $("#saveDraftOrder_div,#purchaseSave_div").each(function() {
      $(this).hide();
    });
    $("#purchaseOrderForm input").not($("#printBtn,#cancelBtn,#nullifyBtn,#copyInput,#toOnlinePurchaseReturn")).each(function() {
      $(this).attr("disabled", true);
    });
    $("#printBtn,#cancelBtn,#nullifyBtn,#copyInput,#toOnlinePurchaseReturn").each(function() {
      $(this).attr("disabled", false);
    });
  }


  //打印页面
  $("#printBtn").click(function() {
    if($("#id").val()) {
      window.showModalDialog("RFbuy.do?method=print&id=" + $("#id").val());
      return;
    }
    if($("#draftOrderIdStr").val()) {
      window.showModalDialog("draft.do?method=getDraftOrderToPrint&id=" + $("#draftOrderIdStr").val() + "&type=PURCHASE&now=" + new Date());
    }
  });


  if($("#status").val() == 'PURCHASE_ORDER_WAITING') {
    $(".invalidImg").show();
    $("#zuofei").removeClass("zuofei").addClass("caigou").show();
  } else if($("#status").val() == 'PURCHASE_ORDER_REPEAL') {
    $("#zuofei").show();
    $(".invalidImg").hide();
  } else if($("#status").val() == '') {
    $("#zuofei").hide();
    $(".invalidImg").hide();
  } else if($("#status").val() == 'PURCHASE_ORDER_DONE') {
    $("#zuofei").removeClass("zuofei").addClass("ruku").show();
    $(".invalidImg").hide();
  } else if($("#status").val() == 'SELLER_PENDING') {
    $("#zuofei").removeClass("zuofei").addClass("pendingImgForPurchase").hide();
    $(".invalidImg").show();
  } else if($("#status").val() == 'SELLER_REFUSED') {
    $("#zuofei").removeClass("zuofei").addClass("refusedImg").show();
    $(".invalidImg").show();
  } else if($("#status").val() == 'SELLER_STOCK') {
    $("#zuofei").removeClass("zuofei").addClass("stockingImg").show();
    $(".invalidImg").hide();
  } else if($("#status").val() == 'SELLER_DISPATCH') {
    $("#zuofei").removeClass("zuofei").addClass("dispatchImg").show();
    $(".invalidImg").hide();
  } else if($("#status").val() == 'PURCHASE_SELLER_STOP' || $("#status").val() == 'STOP') {
    $("#zuofei").removeClass("zuofei").addClass("stopImg").show();
    $(".invalidImg").show();
  }

  $("#nullifyBtn").bind('click', function(event) {
    if($(this).attr("status") == "checkDeleteProduct") {
      return;
    }
    $(this).attr("status", "checkDeleteProduct");
    var ajaxUrl = "txn.do?method=validatorDeletedProductOrderRepeal";
    var ajaxData = {
      orderId: $("#id").val(),
      orderType: getOrderType()
    };
    bcgogoAjaxQuery.setUrlData(ajaxUrl, ajaxData);
    bcgogoAjaxQuery.ajaxQuery(function(json) {
      $("#nullifyBtn").removeAttr("status");
      if(json.success) {
        var confirmMsg = "";
        if($("#status").val() == 'SELLER_PENDING') {
          confirmMsg = "友情提示：采购单作废后，将不再有效，交易会被取消！您确定要作废该采购单吗？";
        } else if($("#status").val() == 'SELLER_REFUSED' || $("#status").val() == 'PURCHASE_SELLER_STOP') {
          confirmMsg = "友情提示：作废后该采购单不再属于待办单据，只能通过查询找出！您确定要作废该采购单吗？";
        } else {
          confirmMsg = "友情提醒：作废后可能会有库存和金额的变化，为保证账面完整，请将原单据回笼，并标记作废！";
        }

        if(confirm(confirmMsg)) {
          window.location = "RFbuy.do?method=purchaseOrderRepeal&id=" + $("#id").val();
        }
      } else if(!json.success && json.operation == "confirm_deleted_product") {
        if(confirm(json.msg)) {
          window.location = "RFbuy.do?method=purchaseOrderRepeal&id=" + $("#id").val();
        }
      } else if(!json.success) {
        alert(json.msg);
      }
    }, function(json) {
      $("#nullifyBtn").removeAttr("status");
      nsDialog.jAlert("网络异常，请联系客服！");
    });
    //        if (confirm("友情提醒：作废后可能会有库存和金额的变化，为保证账面完整，请将原单据回笼，并标记作废！")) {
    //            window.location = "RFbuy.do?method=purchaseOrderRepeal&id=" + $("#id").val();
    //        }
  });
  //显示复制按钮
  if(!GLOBAL.Lang.isEmpty($("#id").val())) {
    $(".copyInput_div").show();
  }
  $("#copyInput").bind("click", function() {
    if(GLOBAL.Lang.isEmpty($("#id").val())) {
      nsDialog.jAlert("单据ID不存在，请刷新后重试");
      return false;
    }
    APP_BCGOGO.Net.syncPost({
      url: "RFbuy.do?method=validateCopy",
      dataType: "json",
      data: {
        "id": $("#id").val()
      },
      success: function(result) {
        if(result.success) {
          window.location = "RFbuy.do?method=copyPurchaseOrder&id=" + $("#id").val();
        } else {
          if(result.operation == 'ALERT') {
            nsDialog.jAlert(result.msg, result.title);
          } else if(result.operation == 'CONFIRM') {
            nsDialog.jConfirm(result.msg, result.title, function(resultVal) {
              if(resultVal) {
                window.location = "RFbuy.do?method=copyPurchaseOrder&id=" + $("#id").val();
              }
            });
          }
        }
      },
      error: function() {
        nsDialog.jAlert("验证时产生异常，请重试！");
      }
    });
  });
  $("#inventoryBtn_div").bind("click", function() {
      if (GLOBAL.Lang.isEmpty($("#id").val())) {
          return;
      }
      APP_BCGOGO.Net.syncPost({
          url: "storage.do?method=validatePurchaseOrder",
          dataType: "json",
          data: {
              "purchaseOrderId": $("#id").val()
          },
          success: function(result) {
              if (result.success) {
                  window.location = "storage.do?method=getProducts&type=txn&purchaseOrderId=" + $("#id").val();
              } else {
                  nsDialog.jAlert(result.msg, null, function(){
                      window.location.reload();
                  });
              }
          },
          error: function() {
              nsDialog.jAlert("验证时产生异常，请重试！");
          }
      });
  });

  $("#saveDraftOrder").bind('click', function(event) {
    $("#saveDraftOrder").attr("disabled", true);
    if(isLegalTxnDataLength() && openNewOrderPage()) {
      $("#purchaseOrderForm").ajaxSubmit({
        url: "draft.do?method=savePurchaseOrderDraft",
        dataType: "json",
        type: "POST",
        success: function(data) {
            showMessage.fadeMessage("60%", "40%", "slow", 3000, "草稿保存成功！" + getCurrentTime());
            $("#draftOrderIdStr").val(data.idStr);
            $("#saveDraftOrder").attr("disabled", false);
            if(!G.isEmpty(data)){
                $("#receiptNoSpan").text(data.receiptNo);
                $("#receiptNo").val(data.receiptNo);
                $("#print_div").show();
            }
        },
        error: function() {
          showMessage.fadeMessage("45%", "34%", "slow", 3000, "保存草稿异常！ " + getCurrentTime());
          $("#saveDraftOrder").attr("disabled", false);
        }
      });
    }
  });

    $(".tradePriceCheck").live("keyup", function() {
        if($(this).val()!= APP_BCGOGO.StringFilter.inputtingPriceFilter(this.value, 2)) {
            $(this).val(APP_BCGOGO.StringFilter.inputtingPriceFilter(this.value, 2));
        }
    });

    $(".tradePriceCheck").live("blur", function() {
        var tradePrice = dataTransition.rounding($(this).val(), 2);
        $(this).val(tradePrice);
        var idPrefix = $(this).attr("id").split(".")[0];
        if(G.Lang.isEmpty($("#" + idPrefix + "\\.productName").val()) || $(this).val()==$(this).attr("lastValue")){
            return;
        }
        if(tradePrice<=0){
            nsDialog.jAlert("请输入正确的批发价！");
            $(this).val($(this).attr("lastValue"));
            return;
        }
        var inventoryAveragePrice = dataTransition.rounding($("#" + idPrefix + "\\.inventoryAveragePrice").val(),2);
        if(tradePrice<inventoryAveragePrice){
            showMessage.fadeMessage("35%", "40%", "slow", 2000, "该商品的批发价低于成本价" + inventoryAveragePrice + "元,请确认.");
        }
    });

    $(".tradePriceCheck").live("focus", function() {
        $(this).attr("lastValue", $(this).val());
    });

  $(".priceCheck").live("keyup", function() {
    if($(this).val()!= APP_BCGOGO.StringFilter.inputtingPriceFilter(this.value, 2)) {
      $(this).val(APP_BCGOGO.StringFilter.inputtingPriceFilter(this.value, 2));
    }
  });

  $(".priceCheck").live("blur", function() {
    if($(this).val()!= APP_BCGOGO.StringFilter.inputtingPriceFilter(this.value, 2)) {
      $(this).val(APP_BCGOGO.StringFilter.inputtingPriceFilter(this.value, 2));
    }
  });

  $("input[name$='.storageBin']").live("keyup", function() {
    $(this).val(APP_BCGOGO.StringFilter.inputtingProductNameFilter(this.value));
  });

  $("input[name$='.storageBin']").live("blur", function() {
    $(this).val(APP_BCGOGO.StringFilter.inputtingProductNameFilter(this.value));

  });
  $(".wholesalerSearchField").bind("click", function() {
    $(this).addClass("hoverTit").siblings().removeClass("hoverTit");
    $("#product_wholesaler_search").val("");
    $("#product_wholesaler_search").attr("searchField", $(this).attr("searchField"));
  });

    mySlider.resetPosition();
    //供应商下拉框
    $(".J-wholesalerSuggestion")
        .bind('click', function () {
            wholesalerSuggestion($(this));
        })
        .bind('keyup', function (event) {
            var eventKeyCode = event.which || event.keyCode;
            if (GLOBAL.Interactive.keyNameFromKeyCode(eventKeyCode).search(/left|up|right|down/g) == -1) {
               wholesalerSuggestion(this,eventKeyCode);
            }
        });
    //

    $("#inventoryBtn").removeAttr("disabled");

    $("#inputMobile").dialog({
        autoOpen:false,
        resizable: false,
        title:"手机号码未填写，请填写手机号，方便以后联系沟通！",
        height:150,
        width:355,
        modal: true,
        closeOnEscape: false,
        buttons:{
            "确定":function() {
                if($("#divMobile").val())
                {
                    //验证格式 NO -- 给出提醒
                    if(!APP_BCGOGO.Validator.stringIsMobilePhoneNumber($("#divMobile").val()))
                    {
                        nsDialog.jAlert("输入的手机号码可能不正确，为了不影响您的后续使用，请确认后重新输入！");
                        return;
                    }
                    //验证同名 Y-- div影藏 赋值给父窗口mobile。blur

                    $("#mobile").val($("#divMobile").val());

                    var r = APP_BCGOGO.Net.syncGet({url:"customer.do?method=getSupplierByMobile",data:{mobile: $("#divMobile").val()},dataType:"json"});
                    if(r && r.supplierIdStr)
                    {
                        if ($("#supplier").val() == r.supplier) {
                            $("#supplierId").val(r.supplierIdStr);
                        } else {
                            $("#inputMobile").dialog("close");
                            $("#mobile").blur();
                            return;
                        }
                    }

                    $("#inputMobile").dialog("close");
                    submitAfterInputMobile($("#inputMobile").attr("type"));

                    //
                }
                else
                {
                    $("#inputMobile").dialog("close");
                    submitAfterInputMobile($("#inputMobile").attr("type"));
                }

            },
            "取消":function(){
                $("#inputMobile").dialog("close");
                submitAfterInputMobile($("#inputMobile").attr("type"));
            }
        },
        close:function() {
            $("#divMobile").val("");
        }
    });


    //设定联系人相关的input框为readonly
    contactDeal(!G.isEmpty($("#supplierId").val()) && !G.isEmpty($("#contactId").val()));
});

function submitAfterInputMobile(btnType){
    $("#purchaseOrderForm").ajaxSubmit({
        url: "RFbuy.do?method=ajaxValidatorPurchaseOrderDTOSave",
        dataType: "json",
        type: "POST",
        success: function(json) {
            if (json.success) {
                if (!idPrefixLastModified) {
                    if (btnType == "SAVE") {
                        purchaseOrderSubmit();
                    } else if (btnType = "MODIFY") {
                        purchaseOrderModifySubmit();
                    }
                    return false;
                }
                if (btnType == "SAVE") {
                    operation = "purchaseOrderSubmit";
                } else if (btnType = "MODIFY") {
                    operation = "purchaseOrderModifySubmit";
                }
                exactSearchInventorySearchIndex();
                $("#saveDraftBtn").attr('disabled', true);
            } else if (!json.success && json.operation == "confirm_deleted_product") {
                if (confirm(json.msg)) {
                    if (!idPrefixLastModified) {
                        if (btnType == "SAVE") {
                            purchaseOrderSubmit();
                        } else if (btnType = "MODIFY") {
                            purchaseOrderModifySubmit();
                        }
                        return false;
                    }
                    if (btnType == "save") {
                        operation = "purchaseOrderSubmit";
                    } else if (btnType = "MODIFY") {
                        operation = "purchaseOrderModifySubmit";
                    }
                    exactSearchInventorySearchIndex();
                    $("#saveDraftBtn").attr('disabled', true);
                }
            } else if (!json.success) {
                alert(json.msg);
                return false;
            }
        },
        error: function(json) {
            nsDialog.jAlert("网络异常，请联系客服");
        }
    });
}
//TODO 采购单提交

function purchaseOrderSubmit() {
  initBusinessScope();
  idPrefixLastModified = null;
  $("#purchaseOrderForm").attr('action', 'RFbuy.do?method=save&btnType=SAVE');
  submitFlag = "purchase";
  if(flag == 1) {
    $("#purchaseOrderForm").submit();
  }
}
//TODO 采购单改单

function purchaseOrderModifySubmit() {
  idPrefixLastModified = null;
  $("#supplier").removeAttr("disabled");
  $("#purchaseOrderForm").attr('action', 'RFbuy.do?method=save&btnType=MODIFY');
  submitFlag = "purchase";
  if(flag == 1) {
    $("#purchaseOrderForm").submit();
  }
}
//TODO 用于在采购单中添加新的一行，比如加号按钮

function purchaseOrderAdd() {
    idPrefixLastModified = null;
    var tr = $(getTrSample()).clone(); //TODO 克隆模版，初始化所有的INPUT
    $(tr).find("input").val("");
    $(tr).find(".itemProductVehicleStatus").val("1");

//    $(tr).find(".itemAmount").bind('keyup', function () {
//        setTotal();
//    });
    $(".itemPrice").bind('change', function () {
        var purchasePrice = APP_BCGOGO.StringFilter.inputtingPriceFilter($(this).val(), 2);;
        $(this).val(purchasePrice);
        $(this).attr("lastValue",$(this).val());
        setTotal();
    });


    $(tr).find(".itemAmount,.itemPrice").bind('blur', function () {
        dataTransition.roundingSpanNumber("totalSpan");
        var format = $(this).val();
        format = dataTransition.rounding(format, 2);
        $(this).val() != '' && $(this).val(format);
        setTotal();
    });

    $(tr).find(".itemPrice").bind("focus", function () {
        $(this).attr("lastValue", $(this).val());
    });


    $(tr).find("input,span,a").each(function (i) {
        //去除文本框的自动填充下拉框
        if ($(this).attr("type") == "text") {
            $(this).attr("autocomplete", "off");
        }

        //TODO ID为空则跳过
        if (!this || !this.id) return;
        var idSuffix = this.id.split(".")[1];

        var tcNum = trCount;
        while (checkThisDom(tcNum, idSuffix)) { //TODO 计算行号，如果目标行号对应的元素存在，则该行号加一
            tcNum = ++tcNum;
        }

        //TODO 组装新的ID和NAME  Begin-->
        var newId = "itemDTOs" + tcNum + "." + idSuffix;
        $(this).attr("id", newId);
        if ($(this).attr("name").split(".")[1]) {
            var newName = "itemDTOs[" + tcNum + "]." + $(this).attr("name").split(".")[1];
            $(this).attr("name", newName);
        }
    });
    $(tr).appendTo("#table_productNo");

    var tr2 = $(getTrSample2()).clone(); //TODO 克隆模版，初始化所有的INPUT
    $(tr2).find("input").val("");
    $(tr2).find(".itemProductVehicleStatus").val("1");
    $(tr2).find("input,span,a").each(function (i) {
        //去除文本框的自动填充下拉框
        if ($(this).attr("type") == "text") {
            $(this).attr("autocomplete", "off");
        }

        //TODO ID为空则跳过
        if (!this || !this.id) return;
        var idSuffix = this.id.split(".")[1];

        var tcNum = trCount;
        while (checkThisDom(tcNum, idSuffix)) { //TODO 计算行号，如果目标行号对应的元素存在，则该行号加一
            tcNum = ++tcNum;
        }

        //TODO 组装新的ID和NAME  Begin-->
        var newId = "itemDTOs" + tcNum + "." + idSuffix;
        $(this).attr("id", newId);
        if ($(this).attr("name").split(".")[1]) {
            var newName = "itemDTOs[" + tcNum + "]." + $(this).attr("name").split(".")[1];
            $(this).attr("name", newName);
        }
    });
    $(tr2).appendTo("#table_productDetail");

    trCount++;
    isShowAddButton();

    mySlider.setSubLayerHeight();

    //动态设置每行的高度
    mySlider.setRowHeight();

//    tableUtil.tableStyle.hasSubTable("#table_productNo", "#table_productDetail", ".table_title,.s_tabelBorder");

    return $(tr);
}

function getTrSample() {
  var permissionInventoryAlarmSettings = $("#permissionInventoryAlarmSettings").val();
  var permissionGoodsSale = $("#permissionGoodsSale").val();
  var trSample = '<tr class="bg item table-row-original">';
  trSample += '<td style="border-left:none;">' + '<input id="itemDTOs0.commodityCode" name="itemDTOs[0].commodityCode" type="text" class="table_input checkStringEmpty" value="" maxlength="20"/>' + '</td>' + '<td><input id="itemDTOs0.id" name="itemDTOs[0].id" type="hidden" value=""/>' + '    <input id="itemDTOs0.productVehicleStatus" name="itemDTOs[0].productVehicleStatus" value="" type="hidden" class="itemProductVehicleStatus"/>' + '    <input id="itemDTOs0.hidden_productVehicleStatus" name="itemDTOs[0].hidden_productVehicleStatus" value="" type="hidden" class="itemProductVehicleStatus"/>' + '    <input id="itemDTOs0.isOldProduct" name="itemDTOs[0].isOldProduct" type="hidden" value=""/>' + '    <input id="itemDTOs0.vehicleBrandId" name="itemDTOs[0].vehicleBrandId" type="hidden" value=""/>' + '    <input id="itemDTOs0.vehicleModelId" name="itemDTOs[0].vehicleModelId" type="hidden" value=""/>' + '    <input id="itemDTOs0.vehicleYearId" name="itemDTOs[0].vehicleYearId" type="hidden" value=""/>' + '    <input id="itemDTOs0.vehicleEngineId" name="itemDTOs[0].vehicleEngineId" type="hidden" value=""/>' + '    <input id="itemDTOs0.productId" name="itemDTOs[0].productId" value="" type="hidden"/>' + '    <input id="itemDTOs0.hidden_productId" name="itemDTOs[0].hidden_productId" value="" type="hidden"/>' + '    <input type="hidden" id="itemDTOs0.lastPrice"/>' + '    <input type="hidden" id="itemDTOs0.lastPurchasePrice"/>' + '    <input id="itemDTOs0.productName" name="itemDTOs[0].productName" class="table_input checkStringEmpty" value="" type="text" style="width:90%"/><input type="hidden" name="itemDTOs[0].promotionsInfoJson" id="itemDTOs0.promotionsInfoJson"/><input type="hidden" name="itemDTOs[0].promotionsId" id="itemDTOs0.promotionsId"/> <input type="hidden" name="itemDTOs[0].hidden_productName" id="itemDTOs0.hidden_productName" />' + '<input type="hidden" class="edit" onfocus="this.blur();" id="itemDTOs0.editbutton" name="itemDTOs[0].editbutton" onclick="searchInventoryIndex(this)" style="margin-left: 6px"/>' + '</td>';
  trSample += '<td><input id="itemDTOs0.brand" name="itemDTOs[0].brand" maxlength="100" class="table_input checkStringEmpty" value="" type="text"/><input type="hidden" name="itemDTOs[0].hidden_brand" id="itemDTOs0.hidden_brand" /></td>';
  trSample += '<td><input id="itemDTOs0.spec" name="itemDTOs[0].spec" class="table_input checkStringEmpty" value="" type="text"/><input type="hidden" name="itemDTOs[0].hidden_spec" id="itemDTOs0.hidden_spec"  /></td>';
  trSample += '<td><input id="itemDTOs0.model" name="itemDTOs[0].model" class="table_input checkStringEmpty" value="" type="text"/><input type="hidden" name="itemDTOs[0].hidden_model" id="itemDTOs0.hidden_model"  /></td>';
  trSample += '<td><input id="itemDTOs0.vehicleModel" name="itemDTOs[0].vehicleModel" class="table_input checkStringEmpty" maxlength="200" type="text"/><input type="hidden" id="itemDTOs0.hidden_vehicleModel" name="itemDTOs[0].hidden_vehicleModel" /></td>';
  trSample += '<td><input id="itemDTOs0.vehicleBrand" name="itemDTOs[0].vehicleBrand" class="table_input checkStringEmpty" maxlength="200" type="text"/><input type="hidden" id="itemDTOs0.hidden_vehicleBrand" name="itemDTOs[0].hidden_vehicleBrand" /></td>';
  trSample += '<td style="display:none"><input id="itemDTOs0.vehicleYear" name="itemDTOs[0].vehicleYear" class="table_input" value="" type="text"/><input type="hidden" id="itemDTOs0.hidden_vehicleYear" name="itemDTOs[0].hidden_vehicleYear" /></td>';
  trSample += '<td style="display:none"><input id="itemDTOs0.vehicleEngine" name="itemDTOs[0].vehicleEngine" class="table_input" value="" type="text"/><input type="hidden" id="itemDTOs0.hidden_vehicleEngine" name="itemDTOs[0].hidden_vehicleEngine" /></td>';
  trSample += '<td><input id="itemDTOs0.price" name="itemDTOs[0].price"  value="" class="itemPrice table_input checkNumberEmpty" type="text" data-filter-zero="true"/></td>';
  trSample += '<td><input id="itemDTOs0.amount" name="itemDTOs[0].amount"  value="" class="itemAmount table_input checkNumberEmpty" type="text" data-filter-zero="true"/></td>';
  trSample += '<td><input id="itemDTOs0.unit" name="itemDTOs[0].unit"  value="" class="itemUnit table_input checkStringEmpty" style="width: 80%"  type="text"/>' + '       <input type="hidden" id="itemDTOs0.storageUnit" name="itemDTOs[0].storageUnit" value="" class="itemStorageUnit table_input"/>' + '       <input type="hidden" id="itemDTOs0.sellUnit" name="itemDTOs[0].sellUnit" value="" class="itemSellUnit table_input"/>' + '       <input type="hidden" id="itemDTOs0.rate" name="itemDTOs[0].rate" value="" class="itemRate table_input"/>' + '</td>';
  trSample += '<td><span class="itemTotalSpan" name="itemDTOs[0].total_span" id="itemDTOs0.total_span"></span><input id="itemDTOs0.total" name="itemDTOs[0].total"  value="" class="itemTotal table_input checkNumberEmpty" readonly="readonly" type="hidden"/></td>';
  //  trSample += '<td class="storage_bin_td">' +
  //        '<input id="itemDTOs0.storageBin" name="itemDTOs[0].storageBin" maxlength="10" value="" class="table_input checkStringEmpty" type="text" style="width:90%"/> ' +
  //        '</td>';
  trSample += '<td>' + '    <span id="itemDTOs0.inventoryAmountSpan" name="itemDTOs[0].inventoryAmountSpan" style="display: block;"></span>' + '   <input type="hidden" id="itemDTOs0.inventoryAmount" name="itemDTOs[0].inventoryAmount"  value="" class="itemInventoryAmount table_input" readonly="readonly" type="text"/>' + '   <span style="display: none;">新</span>' + '</td>';
  //  if (permissionInventoryAlarmSettings == "true") {
  //    trSample += '<td class="txt_right">' +
  //        '    <input type="text" id="itemDTOs0.lowerLimit" class="order_input_lowerLimit checkNumberEmpty" name="itemDTOs[0].lowerLimit" value="" style="width:100%;">' +
  //        '</td>';
  //    trSample += '<td class="txt_right">' +
  //        '    <input type="text" id="itemDTOs0.upperLimit" class="order_input_upperLimit checkNumberEmpty" name="itemDTOs[0].upperLimit" value="" style="width:100%;">' +
  //        '</td>';
  //  } else {
  //    trSample += '<td class="txt_right">' +
  //        '    <input type="text" id="itemDTOs0.lowerLimit" class="order_input_lowerLimit checkNumberEmpty" name="itemDTOs[0].lowerLimit" value="" disabled="true" style="width:100%;">' +
  //        '</td>';
  //    trSample += '<td class="txt_right">' +
  //        '    <input type="text" id="itemDTOs0.upperLimit" class="order_input_upperLimit checkNumberEmpty" name="itemDTOs[0].upperLimit" value="" disabled="true" style="width:100%;">' +
  //        '</td>';
  //  }
  //  trSample += '<td class="trade_price_td">' +
  //        '<input id="itemDTOs0.tradePrice" maxlength="10" name="itemDTOs[0].tradePrice" value="" class="table_input checkNumberEmpty priceCheck"  type="text" style="width:90%"/>' +
  //                  '</td>';
  trSample += '<td style="border-right:none;">' + '    <a class="opera1" id="itemDTOs0.deletebutton" name="itemDTOs[0].deletebutton">删除</a>' + '</td>';
  trSample += '</tr>';
  return trSample;
}


function getTrSample2() {
  var permissionInventoryAlarmSettings = jQuery("#permissionInventoryAlarmSettings").val();
  var trSample = '<tr class="bg item2 table-row-original">';
  var permissionGoodsSale = $("#permissionGoodsSale").val();

  // trSample += '<td class="storage_bin_td">' + '<input id="itemDTOs0.storageBin" name="itemDTOs[0].storageBin" maxlength="10" value="" class="table_input checkStringEmpty" type="text" style="width:90%"/> ' + '</td>';
  trSample += '<td class="txt_right">' + '<input id="itemDTOs0.productKind" name="itemDTOs[0].productKind" maxlength="50" value="" class="table_input checkStringEmpty" type="text" style="width:90%"/> ' + '</td>';

  if(permissionInventoryAlarmSettings == "true") {
    trSample += '<td class="txt_right">' + '    <input type="text" id="itemDTOs0.lowerLimit" class="order_input_lowerLimit table_input checkNumberEmpty" name="itemDTOs[0].lowerLimit" value="" style="width:100%;">' + '</td>';
    trSample += '<td class="txt_right">' + '    <input type="text" id="itemDTOs0.upperLimit" class="order_input_upperLimit table_input checkNumberEmpty" name="itemDTOs[0].upperLimit" value="" style="width:100%;">' + '</td>';
  } else {
    trSample += '<td class="txt_right">' + '    <input type="text" id="itemDTOs0.lowerLimit" class="order_input_lowerLimit table_input checkNumberEmpty" name="itemDTOs[0].lowerLimit" value="" disabled="true" style="width:100%;">' + '</td>';
    trSample += '<td class="txt_right">' + '    <input type="text" id="itemDTOs0.upperLimit" class="order_input_upperLimit table_input checkNumberEmpty" name="itemDTOs[0].upperLimit" value="" disabled="true" style="width:100%;">' + '</td>';
  }
  if(permissionGoodsSale == "true") {
    trSample += '<td class="trade_price_td" style="display:none">' + '<input id="itemDTOs0.tradePrice" maxlength="10" name="itemDTOs[0].tradePrice" value="" class="table_input checkNumberEmpty tradePriceCheck"  type="text" style="width:90%"/>'+ '<input id="itemDTOs0.inventoryAveragePrice" value="" class="table_input"  type="hidden" />' + '</td>';
  }
  trSample += '</tr>';
  return trSample;
}

//检查单据是否相同

function checkTheSame() {
  return invoiceCommon.checkSameItemForOrder("item");
}
//检查商品编码是否相同
//判断是否显示+按钮

function isShowAddButton() {
  //如果初始化的话就默认加一行
  if($(".item").size() <= 0) {
    $(".opera2").trigger("click");
  }
  $(".item .opera2").remove();
  var opera1Id = $(".item:last").find("td:last>a[class='opera1']").attr("id");
  if(!opera1Id) return;

  $(".item:last").find("td:last>a[class='opera1']").after('<a class="opera2" ' + ' id="itemDTOs' + (opera1Id.split(".")[0].substring(8)) + '.plusbutton">增加</a>');
}

//TODO 计算“小计”

function setItemTotal() {
    $(".itemPrice").each(function (i) {
        var price = $(this).val();
        var idPrefix = $(this).attr("id").split(".")[0];
        var amount = $("#" + idPrefix + "\\.amount").val();
        var itemTotal = G.rounding(parseFloat(price) * parseFloat(amount), 2);
        if(price == '' && amount == ''){
            $("#" + idPrefix + "\\.total").val(0);
            $("#" + idPrefix + "\\.total_span").text('');
            $("#" + idPrefix + "\\.total_span").attr("title", '');
        } else{
            $("#" + idPrefix + "\\.total").val(itemTotal);
            $("#" + idPrefix + "\\.total_span").text(itemTotal);
            $("#" + idPrefix + "\\.total_span").attr("title", itemTotal.toString());
        }
        htmlNumberFilter($("#" + idPrefix + "\\.price").add("#" + idPrefix + "\\.amount").add("#" + idPrefix + "\\.total_span").add("#" + idPrefix + "\\.inventoryAmountSpan"), true);
    });
}

//TODO 将“总计”加入页面元素

function setTotal() {
    formatNumber();
    setItemTotal();
    setOrderTotal();
}
function setOrderTotal(){
    var total = 0;
    $(".itemTotal").each(function (i) {
        if ($.trim($(this).val())) {
            total += parseFloat($(this).val());
        }
    });
    total = dataTransition.rounding(total, 2);
    if(total == 0) {
        $("#totalSpan").text("0");
    } else {
        $("#totalSpan").text(total);
    }

    $("#total").val(total);

    if ($("#debt").val() * 1 <= 0) {
        $("#input_makeTime_sale").hide();
        $("#huankuanTime").val("");
    }
}

function newOtherOrder(url) {
  if(openNewOrderPage()) {
    window.open(url, "_blank");
  } else {
    openOrAssign(url);
  }
}

function newPurchaseOrder() {
  if(openNewOrderPage()) {
    window.open($("#basePath").val() + "RFbuy.do?method=create", "_blank");
  }
}


$().ready(function() {

  mySlider.setSubLayerHeight();

  //动态设置每行的高度
  mySlider.setRowHeight();
  //推拉按钮
  mySlider.bindButton();

  var droplist = APP_BCGOGO.Module.droplist;
  $("input[id$='.productKind']").live("click focus", function(event) {
    askForAssistDroplist(event, null);
  }).live("keyup", function(event) {
    askForAssistDroplist(event, "enter");
  });

  function askForAssistDroplist(event, action) {

    var keyCode = event.keyCode || event.which;

    if(keyCode == 37 || keyCode == 38 || keyCode == 39 || keyCode == 40) {
      return;
    }

    var uuid = GLOBAL.Util.generateUUID();
    droplist.setUUID(uuid);
    //ajax获得最近15次使用的商品分类
    var jsonStr = null;
    if(action == null) {
      jsonStr = APP_BCGOGO.Net.syncGet({
        url: "stockSearch.do?method=getProductKindsRecentlyUsed",
        data: {
          uuid: uuid
        },
        dataType: "json"
      });
    } else if(action == "enter") {
      jsonStr = APP_BCGOGO.Net.syncGet({
        url: "stockSearch.do?method=getProductKindsWithFuzzyQuery",
        data: {
          uuid: uuid,
          keyword: $.trim(event.target.value)
        },
        dataType: "json"
      });
    }
    var inputId = event.target;
    var result = {
      uuid: uuid,
      data: (jsonStr && jsonStr.data) ? jsonStr.data : ""
    };
    if(jsonStr && uuid == jsonStr.uuid) {
      droplist.show({
        "selector": $(event.currentTarget),
        "isEditable": true,
        "data": result,
        "onSelect": function(event, index, data) {
          inputId.value = $.trim(data.label);
          if(data.label != "") {
            var idPrefix = inputId.id.split(".")[0];
            var rowId = idPrefix.substring(11, idPrefix.length);
            //库存列表处发生
            if(document.getElementById("productDTOs" + rowId + ".productLocalInfoId") != null) {
              var productId = document.getElementById("productDTOs" + rowId + ".productLocalInfoId").value;
              if(!productId) {
                return;
              }
              APP_BCGOGO.Net.asyncGet({
                url: "stockSearch.do?method=ajaxSaveProductKind",
                data: {
                  kindName: $.trim(data.label),
                  productId: productId
                },
                dataType: "json"
              });
            }
          }
          droplist.hide();
        },
        "onEdit": function(event, index, data) {
          //记下修改前的分类名称
          $("#oldKindName").val(data.label);
        },
        "onSave": function(event, index, data) {
          var newKindName = $.trim(data.label);
          if($.trim(newKindName) == "") {
            droplist.hide();
            nsDialog.jAlert("空字符串不能保存！");
          } else if(newKindName != $("#oldKindName").val()) {
            var oldKindName = $("#oldKindName").val();
            var r = APP_BCGOGO.Net.syncGet({
              url: "stockSearch.do?method=saveOrUpdateProductKind",
              data: {
                oldKindName: $("#oldKindName").val(),
                newKindName: newKindName
              },
              dataType: "json"
            });
            if(r == null || r.flag == undefined) {
              event.target.value = $("#oldKindName").val();
              droplist.hide();
              nsDialog.jAlert("保存失败！");
            } else if(r.flag == "false") {
              nsDialog.jAlert("分类名“" + newKindName + "”已经存在！");
            } else if(r.flag == "true") {
              nsDialog.jAlert("修改成功！", null, function() {
                $("input[id$='.productKind']").each(function() {
                  if($.trim($(this).val()) == $.trim(oldKindName)) {
                    $(this).val($.trim(oldKindName));
                  }
                });
              });
            }
            //保存后清空，避免影响下次保存
            $("#oldKindName").val("");
          }
        },
        "onDelete": function(event, index, data) {
          var r = APP_BCGOGO.Net.syncGet({
            url: "stockSearch.do?method=deleteProductKind",
            data: {
              kindName: data.label
            },
            dataType: "json"
          });
          if(r == null || r.flag == undefined) {
            nsDialog.jAlert("删除失败！");
          } else if(r.flag == "true") {
            nsDialog.jAlert("删除成功！", null, function() {
              $("input[id$='.productKind']").each(function() {
                if($.trim($(this).val()) == $.trim(data.label)) {
                  $(this).val("");
                }
              });
            });
          }
        }
      });
    }
  }

  $("#duizhan").bind("click",function(){
    toCreateStatementOrder($("#supplierId").val(), "SUPPLIER_STATEMENT_ACCOUNT");
  });
});


function showSetproductKind() {
  bcgogo.checksession({
    "parentWindow": window.parent,
    'iframe_PopupBox': $("#iframe_PopupBox_Kind")[0],
    'src': "txn.do?method=setProductKind"
  });
}

function setProducKind(kind) {
  $("input[id$='.productKind']").each(function() {
    $(this).val(kind);
  });
}

function initDuiZhanInfo() {
    if (!$("#receivable").html() || $("#receivable").html() * 1 == 0) {
        $("#receivableDiv").css("display", "none");
    }
    else {
        $("#receivableDiv").css("display", "inline");
    }

    if (!$("#payable").html() || $("#payable").html() * 1 == 0) {
        $("#payableDiv").css("display", "none");
    }
    else {
        $("#payableDiv").css("display", "inline");
    }

    if ($("#receivableDiv").css("display") == "none" && $("#payableDiv").css("display") == "none") {
        $("#duizhan").hide();
    }
    else {
        $("#duizhan").show();
    }
}

function formatNumber(){
    $(".itemPrice").each(function (i) {
        var idPrefix = $(this).attr("id").split(".")[0];
        var amount = $("#" + idPrefix + "\\.amount").val();
        if (!G.Lang.isNumber(amount)) {
            amount = "";
        } else {
            amount = G.rounding(amount, 2);
        }
        $("#" + idPrefix + "\\.amount").val(amount);
    });
}