/**
 /**
 * 供应商搜索
 * @author zhangjuntao
 */
var bcgogoAjaxQuery = APP_BCGOGO.Module.wjl.ajaxQuery;
var supplierInvitationCodeFlash;
var ajaxDataTemp = {};

$(function() {
    if (APP_BCGOGO.Permission.Version.RelationSupplier) {
        checkSupplierWithoutSendInvitationCodeSms();
    }
    var currentColor;
    $(".i_mainRight .table2 a").live("mouseover",function(){
        currentColor = $(this).css("color");
        $(this).css({"color":"#fd5300","textDecoration":"underline"});
    });
    $(".i_mainRight .table2 a").live("mouseout",function(){
        $(this).css({"color":currentColor,"textDecoration":"none"});
    });
    $("#selectAll").live("click",function(){
        if($(this).attr("checked")) {
            $("#supplierDataTable input[type='checkbox']").attr("checked",true);
        } else {
            $("#supplierDataTable input[type='checkbox']").attr("checked",false);
        }
    });

    $("#supplierDataTable input[name='selectSupplier']").live("click",function(){
        if(!$(this).attr("checked")) {
            $("#selectAll").attr("checked",false);
        } else {
            $("#supplierDataTable input[name='selectSupplier']").each(function(index,box){
                if(!$(box).attr("checked")) {
                    return false;
                }
                if(index == $("#supplierDataTable input[name='selectSupplier']").length - 1) {
                    $("#selectAll").attr("checked",true);
                }
            });
        }
    });
  //排序
  $(".ascending,.descending")
      .live("click",function(e) {
          var dom = e.target;
          var inputs = $(".sort");
          for (var i = 0,max = inputs.length; i < max; i++) {
              if (dom.id == inputs[i].id) continue;
              $(inputs[i]).addClass("ascending").removeClass("descending");
          }
          var sortStatus = "";
          var sortStr ="";
          var sorts = $(dom).attr("sort").split(",");
          if ($(dom).hasClass("ascending")) {
              $(dom).addClass("descending").removeClass("ascending");
              for (var i = 0,max = sorts.length; i < max; i++) {
                  if (i == max - 1) {
                      sortStatus += sorts[i] + " desc ";
                      sortStr += sorts[i] + ",desc ";
                  } else {
                      sortStatus += sorts[i] + " desc ,";;
                  }
              }
          } else {
              $(dom).addClass("ascending").removeClass("descending");
              for (var i = 0,max = sorts.length; i < max; i++) {
                  if (i == max - 1) {
                      sortStatus += sorts[i] + " asc ";
                      sortStr += sorts[i] + ",asc";
                  } else {
                      sortStatus += sorts[i] + " asc ,";

                  }
              }
          }
          $("#sortStr").val(sortStr);
          $("#sortStatus").val(sortStatus);
        $("#supplierSearchBtn").click();
      }).each(function() {

      });
  //搜索
  $("#supplierSearchBtn").click(function() {
    $("#rowStart").val(0);
    searchSupplierDataAction();
    $("#hasDebt").val("");
    $("#hasDeposit").val("");
  });

  $("#lastInventoryTimeStart,#lastInventoryTimeEnd")
      .datepicker({
        "numberOfMonths":1,
        "showButtonPanel":true,
        "changeMonth":true,
        "yearRange":"c-100:c+100",
        "yearSuffix":"",
        "changeYear":true,
        "showHour":false,
        "showMinute":false
      })
      .bind("click", function() {
        $(this).blur();
      })
      .change(function() {
        var startDate = $("#lastInventoryTimeStart").val();
        var endDate = $("#lastInventoryTimeEnd").val();
        if (!endDate || !startDate) {
          return;
        }
        if (Number(startDate.replace(/\-/g, "")) > Number(endDate.replace(/\-/g, ""))) {
          $("#lastInventoryTimeEnd").val(startDate);
          $("#lastInventoryTimeStart").val(endDate);
        }
      });

  $("#totalTradeAmountStart,#totalTradeAmountEnd").keyup(function(e) {
    var $txt = $(e.target);
    $txt.val(App.StringFilter.priceFilter($txt.val()));
  })

  //应付款
  $("#debtAmount>a").click(function(e) {
    //处理 数字逻辑
    var start ,end;
    var amountArea = "";
    if ($(e.target).hasClass("cusSure")) {
      start = $("#debtAmount>#debtAmountStart").val();
      end = $("#debtAmount>#debtAmountEnd").val();
      if ((!start && !end) || (start.length == 0 && end.length == 0)) return;
      if ((start && end) && (start.length != 0 && end.length != 0) && Number(start) > Number(end)) {
        var temp = start;
        start = end;
        end = temp;
      }
      amountArea = start + "~" + end;
    } else {
      amountArea = $(e.target).html();
    }
    $("#debtAmount").hide();
    var $typeSpan = $('<span>' + "累计应付金额:" + amountArea + '</span>');
    var $image = $('<img src="images/cus_close.png"/>');
    $image.click(function(e) {
      $(e.target).parent().remove();
      if ($("#conditions").children().length == 0) {
        $("#conditions").hide();
      }
      $("#debtAmount").show();
      $("#supplierSearchBtn").click();
    });
    var $condition = $('<div id="debtAmountCondition" debtamount="'+amountArea + '" value="' + amountArea + '"></div>');
    $condition.addClass("btnMenber").css({width:(getByteLen(amountArea)) * 8 + 90 + "px"});
    $condition.append($typeSpan).append($image);
    $("#conditions").after($condition);
    $("#conditions").append($condition).show();
    $("#supplierSearchBtn").click();
    $("#debtAmount").hide();
  });

  $("#totalTradeAmount>a").click(function(e) {
    //处理 数字逻辑
    var start ,end;
    var amountArea = "";
    if ($(e.target).hasClass("cusSure")) {
      start = $("#totalTradeAmount>#totalTradeAmountStart").val();
      end = $("#totalTradeAmount>#totalTradeAmountEnd").val();
      if ((!start && !end) || (start.length == 0 && end.length == 0)) return;
      if ((start && end) && (start.length != 0 && end.length != 0) && Number(start) > Number(end)) {
        var temp = start;
        start = end;
        end = temp;
      }
      amountArea = start + "~" + end;
    } else {
      amountArea = $(e.target).html();
    }
    $("#totalTradeAmount").hide();
    var $typeSpan = $('<span>' + "累计交易金额:" + amountArea + '</span>');
    var $image = $('<img src="images/cus_close.png"/>');
    $image.click(function(e) {
      $(e.target).parent().remove();
      if ($("#conditions").children().length == 0) {
        $("#conditions").hide();
      }
      $("#totalTradeAmount").show();
      $("#supplierSearchBtn").click();
    });
    var $condition = $('<div id="totalAmountCondition" totaltradeamount="' + amountArea + '" value="' + amountArea + '"></div>');
    $condition.addClass("btnMenber").css({width:(getByteLen(amountArea)) * 8 + 90 + "px"});
    $condition.append($typeSpan).append($image);
    $("#conditions").after($condition);
    $("#conditions").append($condition).show();
    $("#supplierSearchBtn").click();
    $("#totalTradeAmount").hide();
  });

  //入库时间
  $("#lastInventoryTime>a").click(function(e) {
    //处理 数字逻辑
    var start ,end;
    var lastInventoryTime = "";
    if ($(e.target).hasClass("cusSure")) {
      start = $("#lastInventoryTime>#lastInventoryTimeStart").val();
      end = $("#lastInventoryTime>#lastInventoryTimeEnd").val();
      if ((!start && !end) || (start.length == 0 && end.length == 0)) return;
      if ((start && end) && (start.length != 0 && end.length != 0) && Number(start) > Number(end)) {
        var temp = start;
        start = end;
        end = temp;
      }
      lastInventoryTime = start + "~" + end;
    } else {
      lastInventoryTime = $(e.target).html();
    }
    $("#lastInventoryTime").hide();
    var $typeSpan = $('<span id="lastInventoryTimeCondition">' + "入库时间:" + lastInventoryTime + '</span>');
    var $image = $('<img src="images/cus_close.png"/>');
//          var $image = $('<img src="images/close_input.png"/>');
    $image.click(function(e) {
      $(e.target).parent().remove();
      if ($("#conditions").children().length == 0) {
        $("#conditions").hide();
      }
      $("#lastInventoryTime").show();
      $("#supplierSearchBtn").click();
    });
    var $condition = $('<div id="lastInventoryTimeCondition" lastInventoryTime="'+lastInventoryTime +'" value="' + lastInventoryTime + '"></div>');
    $condition.addClass("btnMenber").css({width:(getByteLen(lastInventoryTime)) * 8 + 60 + "px"});
    $condition.append($typeSpan).append($image);
    $("#conditions").append($condition).show();

    $("#supplierSearchBtn").click();
    $("#lastInventoryTime").hide();
  });



    //应付应收款对帐单提示
    $(".pay").live("mouseover", function(event){
        var _currentTarget = $("#payableReceivableAlert");
        _currentTarget.css({"top": GLOBAL.Display.getY(this)+21, "left": GLOBAL.Display.getX(this)});
        _currentTarget.show();
    }).live("mouseout", function(){
        $("#payableReceivableAlert").hide();
    });
  $(".customer_cancel_shop_relation").live("click", function () {
    if ($(this).attr("lock") || !$(this).attr("supplierId")) {
      return;
    }
    $(this).attr("lock", true);
    var $cancelDom = $(this);
    var supplierId = $(this).attr("supplierId");
    var ajaxData = {supplierId: $(this).attr("supplierId")};
    var ajaxUrl = "apply.do?method=validateCustomerCancelSupplierShopRelation";
    bcgogoAjaxQuery.setUrlData(ajaxUrl, ajaxData);
    bcgogoAjaxQuery.ajaxQuery(function (result) {
      if (result.success) {
        $("#cancelShopRelationDialog").dialog({
          resizable: false,
          title: "取消关联",
          height: 210,
          width: 300,
          modal: true,
          closeOnEscape: false,
          buttons: {
            "确定": function () {
              var refuseMsg = $("#cancel_msg").val();
                var params = {supplierId: $cancelDom.attr("supplierId"), cancelMsg: (refuseMsg == "取消关联理由" ? "无" : refuseMsg)},
                  url = "apply.do?method=customerCancelSupplierShopRelation";
              APP_BCGOGO.Net.asyncAjax({
                type: "POST",
                url: url,
                data: params,
                cache: false,
                dataType: "json",
                success: function (result) {
                  if (result.success) {
                    nsDialog.jAlert("您已取消与对方的关联关系！", "", function () {
                        $("#supplierSearchBtn").click();
                    });
                  } else {
                    nsDialog.jAlert(result.msg);
                  }
                }
              });
              $(this).dialog("close");
            },
            "取消": function () {
              $(this).dialog("close");
            }
          },
          close: function () {
            $cancelDom.removeAttr("lock");
            $("#cancel_msg").removeClass("black_color").addClass("gray_color");
            $("#cancel_msg").val($("#cancel_msg").attr("init_word"));
          }
        });
      } else {
        nsDialog.jAlert(result.msg);
      }
      $cancelDom.removeAttr("lock");
    });
  });

    $("[pop-window-name='input-mobile']").dialog({
        autoOpen: false,
        resizable: false,
        title: "请输入手机号码：",
        height: 130,
        width: 250,
        modal: true,
        closeOnEscape: false,
        buttons: {
            "确定": function () {
                var $mobileInput = $("[pop-window-input-name='mobile']"),
                    supplierId = $mobileInput.attr("data-supplier-id") ,
                    callback = $mobileInput.attr("callback") ,
                    mobile = $mobileInput.val(),
                    me = this,data={};
                if (!supplierId) {
                    $(this).dialog("close");
                } else {
                    if (!APP_BCGOGO.Validator.stringIsMobilePhoneNumber(mobile)) {
                        nsDialog.jAlert("手机号码输入有误！请重新输入！");
                        return;
                    }
                    if (!(APP_BCGOGO.Net.syncGet({"url":"customer.do?method=getSupplierByMobile",data:{"mobile":mobile},dataType:"json"}).length == 0)) {
                        nsDialog.jAlert("手机号码重复！请重新输入！");
                        return;
                    }
                    APP_BCGOGO.Net.asyncAjax({
                        type: "POST",
                        url: "customer.do?method=updateMobile",
                        data: {supplierId: supplierId, mobile: mobile},
                        cache: false,
                        async: true,
                        success: function () {
                            nsDialog.jAlert("手机号码保存成功！", "", function () {
                                $(me).dialog("close");
                                if (callback) {
                                    data['searchSupplier'] = true;
                                    data['supplierId'] = supplierId;
                                    eval(callback)(data);
                                }
                            });
                        }
                    });
                }
            },
            "取消": function () {
                $(this).dialog("close");
            }
        },
        close: function () {
            $("[pop-window-input-name='mobile']").val("")
                .removeAttr("data-supplier-id").removeAttr("callback");
        }
    });
    function sentInvitationCodeSms(data){
        var shopMoney = $("#smsBalance").html();
        if (!shopMoney) {
            nsDialog.jAlert("您的短信余额不足");
            return ;
        }
        APP_BCGOGO.Net.asyncAjax({
            type: "POST",
            url: "invitationCodeSms.do?method=sentInvitationCodeSms",
            data: { id: data["supplierId"], customerOrSupplier: "SUPPLIER"},
            cache: false,
            dataType: "json",
            success: function (result) {
                if (result.success) {
                    nsDialog.jAlert("已成功发送推荐短信！", "", function () {
                        if (data["searchSupplier"]) {
                            $("#supplierSearchBtn").click();
                        }
                        checkSupplierWithoutSendInvitationCodeSms();
                    });
                }else{
                    nsDialog.jAlert(result.msg);
                }
            }
        });
    }
    //推荐
    $(".sentInvitationCodeSmsBtn").live("click", function (e) {
        var supplierId = $(e.target).attr("data-supplier-id");
        var mobile = $(e.target).attr("data-mobile"), data = {};
        if (!supplierId)return;
        if(!APP_BCGOGO.Validator.stringIsMobilePhoneNumber(mobile)) {
            $("[pop-window-input-name='mobile']").attr("data-supplier-id", supplierId)
                .attr("callback", 'sentInvitationCodeSms');
            $("[pop-window-name='input-mobile']").dialog("open");
        } else {
            data['supplierId'] = supplierId;
            sentInvitationCodeSms(data);
        }
    });

    //一键推荐
    $("#recommendSupplier").bind("click", function (e) {
        var shopMoney = $("#smsBalance").html();
        if (!shopMoney) {
            nsDialog.jAlert("您的短信余额不足");
            return;
        }
        nsDialog.jConfirm("您是否确定发短信推荐你的供应商使用一发软件?", "", function (returnVal) {
            if (returnVal) {
                APP_BCGOGO.Net.asyncAjax({
                    type: "POST",
                    url: "invitationCodeSms.do?method=sentInvitationCodePromotionalSms",
                    data: {customerOrSupplier: "SUPPLIER"},
                    cache: false,
                    dataType: "json",
                    success: function (result) {
                        if (result.success) {
                            nsDialog.jAlert("已成功发送推荐短信！","",function(){
                                if(supplierInvitationCodeFlash)clearInterval(supplierInvitationCodeFlash);
                                checkSupplierWithoutSendInvitationCodeSms();
                            });
                        }
                    }
                });
            }
        });

    });

  $("#cancel_msg").bind("keydown",function () {
    if ($(this).hasClass("gray_color") && $(this).val() == $(this).attr("init_word")) {
      $(this).removeClass("gray_color").addClass("black_color").val("");
    }
  }).bind("blur", function () {
        if (!$(this).val()) {
          $(this).removeClass("black_color").addClass("gray_color");
          $(this).val($("#cancel_msg").attr("init_word"));
        }
      });


    $("#recommendSupplier").hover(function (event) {
        var _currentTarget = $("#multi_alert");
        var offset = $(this).offset();
        var height = $(this).css("height");
        height = height.replace('px', '') * 1;
        _currentTarget.css({"top": offset.top + height, "left": offset.left});
        _currentTarget.show();
    }, function (event) {
        event.stopImmediatePropagation();
        if ($(event.relatedTarget)[0] != $("#multi_alert")[0] && $(event.relatedTarget).parent()[0] != $("#multi_alert")[0] && $(event.relatedTarget).parent().parent()[0] != $("#multi_alert")[0]) {
            $("#multi_alert").hide();
        }
    });

    $("#multi_alert").mouseleave(function (event) {
        event.stopImmediatePropagation();
        if ($(event.relatedTarget).find(".alert")[0] != $("#sentInvitationCodePromotionalSms")) {
            $("#multi_alert").hide();
        }
    });

    $(".sentInvitationCodeSmsBtn").live("mouseover",function() {

        $("#single_alert").hide();

        var _currentTarget = $("#single_alert");
        var x = G.getX(this);
        var y = G.getY(this);
        var height = $(this).css("line-height");
        height = height.replace('px', '') * 1;
        _currentTarget.css({"top":y + height, "left": x});
        _currentTarget.show();

        $("#single_alert").mouseleave(function (event) {
            event.stopImmediatePropagation();
            if ($(event.relatedTarget).find(".alert")[0] != $(this)) {
                $("#single_alert").hide();
            }
        });
    }).live("mouseout",function(event) {

        event.stopImmediatePropagation();
        if ($(event.relatedTarget)[0] != $("#single_alert")[0] && $(event.relatedTarget).parent()[0] != $("#single_alert")[0] && $(event.relatedTarget).parent().parent()[0] != $("#single_alert")[0]) {
            $("#single_alert").hide();
        }
    });

//  var $sentInvitationCodePromotionalSms = $(".hoverReminder"),
//        $sentInvitationCodePromotionalSmsInfo = $(".hoverReminder").parent().find(".tixing"),
//        $sentInvitationCodePromotionalSmsFather = $(".hoverReminder").parent();
//
//    $sentInvitationCodePromotionalSms.bind("mouseenter", function(){
//        $sentInvitationCodePromotionalSmsInfo.show();
//    });
//    $sentInvitationCodePromotionalSmsInfo.bind("mouseleave", function(){
//        $sentInvitationCodePromotionalSmsInfo.hide();
//    });
//    $sentInvitationCodePromotionalSmsFather.bind("mouseleave", function(){
//        $sentInvitationCodePromotionalSmsInfo.hide();
//    });

});

function doCustomerOrSupplierProductSearch() {
    $("#rowStart").val(0);
    searchSupplierDataAction();
    $("#hasDebt").val("");
    $("#hasDeposit").val("");
}

function checkSupplierWithoutSendInvitationCodeSms(){
    APP_BCGOGO.Net.asyncAjax({
        type: "POST",
        url: "invitationCodeSms.do?method=checkCustomerOrSupplierWithoutSendInvitationCodeSms",
        data: { customerOrSupplier: "SUPPLIER"},
        cache: false,
        dataType: "json",
        success: function (result) {
            var $sentInvitationCodePromotionalSms = $("#sentInvitationCodePromotionalSms");
            if (result["success"] && result["total"] > 0) {
                supplierInvitationCodeFlash = setInterval(function () {
                    if ($sentInvitationCodePromotionalSms.hasClass("invitation_code_need_sending_blinking")) {
                        $sentInvitationCodePromotionalSms.removeClass("invitation_code_need_sending_blinking").css({"color": "#a26207"});
                    } else {
                        $sentInvitationCodePromotionalSms.addClass("invitation_code_need_sending_blinking").css({"color": "#fff"});
                    }
                }, 250);
            }else{
                $sentInvitationCodePromotionalSms.css({"color": "#fff"});
                if(supplierInvitationCodeFlash)clearInterval(supplierInvitationCodeFlash);
            }
        }
    });
}
function searchSupplierDataActionFilter() {

  if ($("#sortStatus").val() == defaultSortStatus || $("#sortStatus").val() == "") {
    if (!$("#lastInventoryTimeSortSpan").hasClass("arrowDown")) {
      $("#lastInventoryTimeSortSpan").addClass("arrowDown").removeClass("arrowUp");
      $("#lastInventoryTimeSort").attr("currentSortStatus", "Desc");
    }
    $("#lastInventoryTimeSort").addClass("hover");

  }
  var ajaxData = ajaxDataTemp;
  ajaxData.hasDebt = $("#hasDebt").val();
  ajaxData.hasDeposit = $("#hasDeposit").val();
  ajaxData.hasDeposit = $("#hasDeposit").val();
//  ajaxData.relationType = $("#relationType").val();
  ajaxData.searchStrategies = $("#searchStrategy").val();
  lStorage.setItem(storageKey.SearchConditionKey, JSON.stringify(ajaxData));
  var resetStatNum = false;
  var ajaxUrl = "supplier.do?method=searchSupplierDataAction";
  bcgogoAjaxQuery.setUrlData(ajaxUrl, ajaxData);
  bcgogoAjaxQuery.ajaxQuery(function(json) {
    initSupplierList(json, resetStatNum);
    initPages(json, "supplierSuggest", ajaxUrl, '', "initSupplierList", '', '', ajaxData, '');
  });
}

function searchSupplierDataAction() {
    var ajaxData;
    if($("#resetSearchCondition").val() == 'true') {
        //重置搜索条件
        ajaxData = jQuery.parseJSON(lStorage.getItem(storageKey.SearchConditionKey));
        if(ajaxData) {
            resetSearchCondition(ajaxData);
        }
    } else {
        if ($("#sortStatus").val() == defaultSortStatus || $("#sortStatus").val() == "") {
            if (!$("#lastInventoryTimeSortSpan").hasClass("arrowDown")) {
                $("#lastInventoryTimeSortSpan").addClass("arrowDown").removeClass("arrowUp");
                $("#lastInventoryTimeSort").attr("currentSortStatus", "Desc");
            }
            $("#lastInventoryTimeSort").addClass("hover");
        }
    }

  var ajaxData = beforeSearchSupplier();
  ajaxDataTemp = ajaxData;
  //放入LocalStorage
  lStorage.setItem(storageKey.SearchConditionKey,JSON.stringify(ajaxData));
  var ajaxUrl = "supplier.do?method=searchSupplierDataAction";
  bcgogoAjaxQuery.setUrlData(ajaxUrl, ajaxData);
  bcgogoAjaxQuery.ajaxQuery(function(json) {
    initSupplierList(json,true);
    initPages(json, "supplierSuggest", ajaxUrl, '', "initSupplierList", '', '',ajaxData, '');
  });
}


function initWholesalerInfo(json){
  var sortStr=$("#sortStr").val();
  var suppliers = json.supplierDTOs;
  var tr='<colgroup><col width="50"><col width="60"><col width="130"><col width="60"><col width="78"><col width="78"><col width="78"><col width="166">';
  tr+='<col width="54"><col width="85"><col width="70"><col width="68"></colgroup>';
  tr+='<tr class="divSlip titleBg"><td style="padding-left:8px;"></td><td>评分</td><td>供应商</td><td>联系人</td><td>联系方式</td>';
  tr+='<td>累计交易</td><td>累计退货</td><td><span style="float:left;">应收</span><input id="total_return_debt" sort="total_return_debt" class="ascending sort fl" type="button" onfocus="this.blur();">' +
      '<span style="margin-left:50px">应付</span><input id="total_debt" sort="total_debt" class="ascending sort fl" type="button" onfocus="this.blur();">' +
      '</td><td>定金余额</td><td>最后入库时间</td><td>供应商类型</td><td>操作</td></tr>';
  $("#supplierDataTable").append($(tr));
    if(stringUtil.isNotEmpty(sortStr)){
        var sortField=sortStr.split(",")[0];
        var sortType=sortStr.split(",")[1];
        if(sortType=="asc"){
            $("#"+sortField).addClass("ascending").removeClass("descending");
        }else{
            $("#"+sortField).addClass("descending").removeClass("ascending");
        }
    }
  for (var i = 0,max = suppliers.length; i < max; i++) {
    var supplier = suppliers[i];
    var supplierId = supplier.idString;
    var contact = G.normalize(supplier.contact);
    var name = G.normalize(supplier.name);
    var mobile = supplier.mobile;
    var contactMethod = G.normalize(supplier.mobile, G.normalize(supplier.landLine) );
    var address = G.normalize(supplier.address);
    var totalTradeAmount = supplier.totalTradeAmount ? App.StringFilter.priceFilter(supplier.totalTradeAmount,2) : "0";
    var totalReturnAmount = supplier.totalReturnAmount ? App.StringFilter.priceFilter(supplier.totalReturnAmount,2) : "0";
    var debt = (supplier.totalDebt ? App.StringFilter.priceFilter(supplier.totalDebt,2) : "0");
    var deposit = supplier.deposit ? App.StringFilter.priceFilter(supplier.deposit,2) : "0";
    var lastInventoryTime = supplier.lastInventoryTimeStr ? supplier.lastInventoryTimeStr : "";
    var totalReturnDebt = supplier.totalReturnDebt ? App.StringFilter.priceFilter(supplier.totalReturnDebt,2) : "0";
    var supplierShopId = supplier.supplierShopId || '';
    tr = '<tr class="table-row-original">';
    tr += '<td style="border-left:none;"><input type="checkbox" class="check" style="margin-right:1px;" supplierShopId="'+supplierShopId+'" name="selectSupplier" value="' + supplierId + '"  id=check' + (i + 1) + '/>'+'</td>';

    if (!(!supplier.relationType || supplier.relationType == "UNRELATED")) {

      var totalAverageScore = (supplier.totalAverageScore ? App.StringFilter.priceFilter(supplier.totalAverageScore, 1) : "0");
      var qualityAverageScore = (supplier.qualityAverageScore ? App.StringFilter.priceFilter(supplier.qualityAverageScore, 1) : "0");
      var performanceAverageScore = (supplier.performanceAverageScore ? App.StringFilter.priceFilter(supplier.performanceAverageScore, 1) : "0");
      var speedAverageScore = (supplier.speedAverageScore ? App.StringFilter.priceFilter(supplier.speedAverageScore, 1) : "0");
      var attitudeAverageScore = (supplier.attitudeAverageScore ? App.StringFilter.priceFilter(supplier.attitudeAverageScore, 1) : "0");
      var commentRecordCount = (supplier.commentRecordCount ? App.StringFilter.priceFilter(supplier.commentRecordCount, 1) : "0");
      var totalAverageScoreStr;
      if(totalAverageScore == 0 || totalAverageScore == "0"){
         totalAverageScoreStr = "暂无";
      }else{
         totalAverageScoreStr = totalAverageScore + '分';
      }

      var idStr = "relatedSupplier" + i;
      tr += '<td id="'+ idStr + '" onmouseover="showSupplierCommentScore(this' + ',' + totalAverageScore  + ',' + commentRecordCount + ',' + qualityAverageScore + ',' + performanceAverageScore + ',' + speedAverageScore + ',' + attitudeAverageScore
          + ');" onmouseout="scorePanelHide();"><span class="star" style="margin: 0 3px 4px 0; vertical-align: middle;" onclick="redirectShopCommentDetail(\'' +supplierShopId  +'\')"></span><b class="color_yellow" ><span onclick="redirectShopCommentDetail(\'' +supplierShopId  +'\')">' + totalAverageScoreStr  + '</span></b> ' +
          '<td title=' + name + '><a class="blue_col" href="unitlink.do?method=supplier&supplierId=' + supplierId + '">' + name + '</a></td>';

    } else {
      tr +='<td></td>'
      tr += '<td title=' + name + '><a class="blue_col" href="unitlink.do?method=supplier&supplierId=' + supplierId + '">' + name + '</a></td>';
    }


    tr += '<td title="' + contact + '">' + contact + '</td>';
    tr += '<td title="' + contactMethod + '"><span>' + contactMethod + '</span></td>';
//    tr += '<td title="' + address + '">' + tableUtil.limitLen(address,15) + '</td> ';
    tr += '<td> ' + totalTradeAmount + '</td>';
    tr += '<td> ' + totalReturnAmount + '</td>';
    tr += '<td class="qian_red"><div class="pay" onclick="toCreateStatementOrder(\''+supplierId+'\', \'SUPPLIER_STATEMENT_ACCOUNT\') ">';
    if(totalReturnDebt>0){
      tr+='<span class="red_color payMoney">收¥'+totalReturnDebt+'</span>';
    }
    else{
      tr+='<span class="gray_color fuMoney">收¥'+totalReturnDebt+'</span>';
    }
    if(debt>0){
      tr +='<span class="green_color fuMoney">付¥'+debt+'</span></div></td>';
    }else{
      tr +='<span class="gray_color fuMoney">付¥'+debt+'</span></div></td>';
    }
    tr += '<td>' + deposit + '</td>';
    tr += '<td>' + lastInventoryTime + '</td>';
    if (!supplier.relationType || supplier.relationType == "UNRELATED") {
//        tr += '<td><a style="color: #CB0000">非关联</a></td>';
//        tr += '<td></td>'
        tr += '<td><img class="icon" src="images/icons.png"><a style="color: #CB0000">非关联</a></td>';
        tr += '<td><a class="sentInvitationCodeSmsBtn" data-supplier-id="' + supplierId + '" data-mobile="' + mobile + '">推荐</a></td>';
    } else {
        tr += '<td>已关联</td>';
        tr += '<td><a class="customer_cancel_shop_relation" supplierId = "' + supplierId + '"> 	取消关联</a></td>';
    }
    tr += '</tr>';
    $("#supplierDataTable").append($(tr));
  }
}

function initSupplierInfo(json){
  var sortStr=$("#sortStr").val();
  var suppliers = json.supplierDTOs;
  var tr='<colgroup><col width="60px"><col width="140px"><col width="52px"><col width="87px"><col width="160px"><col width="100px">';
  tr+='<col width="80px"><col width="80px"><col width="176px"><col width="70px"><col></colgroup>';
  tr+='<tr class="divSlip titleBg"><td style="padding-left:10px;"><input id="selectAll" type="checkbox" class="check" style="margin-left:-4px;" />全选</td><td>供应商</td><td>联系人</td><td>联系方式</td><td>地址</td>';
  tr+='<td>经营产品</td><td>累计交易</td><td>累计退货</td><td><span style="float:left;">应收</span><input id="total_return_debt" sort="total_return_debt" class="ascending sort fl" type="button" onfocus="this.blur();">' +
      '<span style="margin-left:50px">应付</div><input id="total_debt" sort="total_debt" class="ascending sort fl" type="button" onfocus="this.blur();">' +
      '</td><td>定金余额</td><td>最后入库时间</td></tr>';
  $("#supplierDataTable").append($(tr));
    if(stringUtil.isNotEmpty(sortStr)){
        var sortField=sortStr.split(",")[0];
        var sortType=sortStr.split(",")[1];
        if(sortType=="asc"){
            $("#"+sortField).addClass("ascending").removeClass("descending");
        }else{
            $("#"+sortField).addClass("descending").removeClass("ascending");
        }
    }
  for (var i = 0,max = suppliers.length; i < max; i++) {
    var supplier = suppliers[i];
    var supplierId = supplier.idString;
    var contact = G.normalize(supplier.contact);
    var name = G.normalize(supplier.name);
    var contactMethod = G.normalize(supplier.mobile, G.normalize(supplier.landLine) );
    var address = G.normalize(supplier.address);
    var businessScope = G.normalize(supplier.businessScope);
    var totalTradeAmount = supplier.totalTradeAmount ? App.StringFilter.priceFilter(supplier.totalTradeAmount,2) : "0";
    var totalReturnAmount = supplier.totalReturnAmount ? App.StringFilter.priceFilter(supplier.totalReturnAmount,2) : "0";
    var debt = (supplier.totalDebt ? App.StringFilter.priceFilter(supplier.totalDebt,2) : "0");
    var deposit = supplier.deposit ? App.StringFilter.priceFilter(supplier.deposit,2) : "0";
    var lastInventoryTime = supplier.lastInventoryTimeStr ? supplier.lastInventoryTimeStr : "";
    var supplierShopId = supplier.supplierShopId || '';
    var totalReturnDebt = supplier.totalReturnDebt ? App.StringFilter.priceFilter(supplier.totalReturnDebt,2) : "0";
    tr = '<tr class="table-row-original">';
    tr += '<td style="border-left:none;"><input type="checkbox" class="check" style="margin-right:1px;" supplierShopId="'+supplierShopId+'" name="selectSupplier" value="' + supplierId + '"  id=check' + (i + 1) + '/>'+'</td>';
    tr += '<td title=' + name + '><a href="unitlink.do?method=supplier&supplierId=' + supplierId + '">' + name + '</td>';
    tr += '<td title="' + contact + '">' + contact + '</td>';
    tr += '<td title="' + contactMethod + '"><span>' + contactMethod + '</span></td>';
    tr += '<td title="' + address + '">' + tableUtil.limitLen(address,15) + '</td> ';
    tr += '<td title="' + businessScope + '">' + businessScope + '</td> ';
//        tr += '<td> ' + totalInventoryAmount + '</td>';
    tr += '<td> ' + totalTradeAmount + '</td>';
    tr += '<td> ' + totalReturnAmount + '</td>';
    tr += '<td class="qian_red"><div class="pay" onclick="toCreateStatementOrder(\''+supplierId+'\', \'SUPPLIER_STATEMENT_ACCOUNT\') ">'
    if(totalReturnDebt>0){
      tr+='<span class="red_color payMoney">收¥'+totalReturnDebt+'</span>';
    }
    else{
      tr+='<span class="gray_color fuMoney">收¥'+totalReturnDebt+'</span>';
    }
    if(debt>0){
      tr +='<span class="green_color fuMoney">付¥'+debt+'</span></div></td>';
    }else{
      tr +='<span class="gray_color fuMoney">付¥'+debt+'</span></div></td>';
    }
    tr += '<td>' + deposit + '</td>';
    tr += '<td style="border-right:none;">' + lastInventoryTime + '</td>';
    tr += '</tr>';
    $("#supplierDataTable").append($(tr));
  }
}

function initSupplierDataTr(json) {
  $("#supplierDataTable colgroup,#supplierDataTable tbody").remove();
  $("#totalRows").val(json.numFound);
  var totalDeposit = App.StringFilter.priceFilter(json.totalDeposit);
  var totalDebt = App.StringFilter.priceFilter(json.totalDebt);
  $("#totalDeposit").html(totalDeposit ? totalDeposit : 0);
  $("#totalDebt").html(totalDebt ? totalDebt : 0);
  if($("#relationType").val() == '') {
    $("#totalNum").html(json.numFound);
  }
  $("#relatedNum").html(json.relatedNum);
  if (!json.supplierDTOs) return;
  if(APP_BCGOGO.Permission.Version.RelationSupplier){
    initWholesalerInfo(json);
  }else{
    initSupplierInfo(json);
  }
  initAndBindSelectCheckBoxs();
  tableUtil.tableStyle('#supplierDataTable','.titleBg');
  $("#relationType").val('');


}

function initAndBindSelectCheckBoxs(){
  $("[name='selectSupplier']").each(function(){
    if(isContainSelectedId($(this).val())){
      $(this).attr("checked",true);
    }else{
      $(this).attr("checked",false);
    }
  });

  $("[name='selectSupplier']").click(function(){
    var selectedId= $(this).val();
    var supplierShopId=$(this).attr("supplierShopId");
    if($(this).attr("checked")){
      if(!isContainSelectedId($(this).val())){
        var selectData='<input type="hidden" supplierShopId="'+supplierShopId+'"  value="'+selectedId+'"/>';
        $("#selectedIdArray").append(selectData);
      }
    }else{
      $("#selectedIdArray input").each(function(){
        if($(this).val()==selectedId){
          $(this).remove();
        }
      });
    }
  });
}


function isContainSelectedId(selectedId){
  var flag=false;
  $("#selectedIdArray input").each(function(){
    if($(this).val()==selectedId){
      flag=true;
      return;
    }
  });
  return flag;
}

function getByteLen(val) {    //传入一个字符串
  var len = 0;
  for (var i = 0; i < val.length; i++) {
    if (val[i].match(/[^\x00-\xff]/ig) != null) //全角
      len += 2; //如果是全角，占用两个字节
    else
      len += 1; //半角占用一个字节
  }
  return len;
}
function getTodayMilliseconds(date) {
  return ((date.getHours() * 60 + date.getMinutes()) * 60 + date.getSeconds()) * 1000;
}

function resetSearchCondition(ajaxData) {
    $(".lineBody input").css("color","#272727");
    var searchWord = ajaxData.searchWord == "" ? $("#supplierInfoText").attr("initialvalue") : ajaxData.searchWord;
    var productName = ajaxData.productName == "" ? $("#productName").attr("initialvalue") : ajaxData.productName;
    var productBrand = ajaxData.productBrand == "" ? $("#productBrand").attr("initialvalue") : ajaxData.productBrand;
    var productSpec = ajaxData.productSpec == "" ? $("#productSpec").attr("initialvalue") : ajaxData.productSpec;
    var productModel = ajaxData.productModel == "" ? $("#productModel").attr("initialvalue") : ajaxData.productModel;
    var productVehicleBrand = ajaxData.productVehicleBrand == "" ? $("#productVehicleBrand").attr("initialvalue") : ajaxData.productVehicleBrand;
    var productVehicleModel = ajaxData.productVehicleModel == "" ? $("#productVehicleModel").attr("initialvalue") : ajaxData.productVehicleModel;
    var commodityCode = ajaxData.commodityCode == "" ? $("#commodityCode").attr("initialvalue") : ajaxData.commodityCode;

    if(G.Lang.isNotEmpty(ajaxData.lastInventoryTimeStart)) {
        $("#startDate").val(dateUtil.formatDate(new Date(ajaxData.lastInventoryTimeStart),dateUtil.dateStringFormatDay));
    }
    if(G.Lang.isNotEmpty(ajaxData.lastInventoryTimeEnd)) {
        $("#endDate").val(dateUtil.formatDate(new Date(ajaxData.lastInventoryTimeEnd - 1000 * 60 * 60 * 24 + 1),dateUtil.dateStringFormatDay));
    }
    if(G.Lang.isNotEmpty(ajaxData.lastInventoryTimeStart) && G.Lang.isNotEmpty(ajaxData.lastInventoryTimeEnd)) {
        if($("#startDate").val() == dateUtil.getYesterday() && $("#endDate").val() == dateUtil.getYesterday()) {
            $("#date_yesterday").addClass("clicked");
        } else if($("#startDate").val() == dateUtil.getToday() && $("#endDate").val() == dateUtil.getToday()) {
            $("#date_today").addClass("clicked");
        } else if($("#startDate").val() == dateUtil.getOneWeekBefore() && $("#endDate").val() == dateUtil.getToday()) {
            $("#date_last_week").addClass("clicked");
        } else if($("#startDate").val() == dateUtil.getOneMonthBefore() && $("#endDate").val() == dateUtil.getToday()) {
            $("#date_last_month").addClass("clicked");
        } else if($("#startDate").val() == dateUtil.getOneYearBefore() && $("#endDate").val() == dateUtil.getToday()) {
            $("#date_last_year").addClass("clicked");
        }
    }

    $("#supplierInfoText").val(searchWord);
    $("#supplierId").val(ajaxData.ids);
    if(ajaxData.province != '') {
        $("#provinceNo").val(ajaxData.province);
        $("#provinceNo").change();
    }
    if(ajaxData.city != '') {
        $("#cityNo").val(ajaxData.city);
        $("#cityNo").change();
    }
    $("#regionNo").val(ajaxData.region);
    $("#productName").val(productName);
    $("#productBrand").val(productBrand);
    $("#productSpec").val(productSpec);
    $("#productModel").val(productModel);
    $("#productVehicleBrand").val(productVehicleBrand);
    $("#productVehicleModel").val(productVehicleModel);
    $("#commodityCode").val(commodityCode);
    $("#totalTradeAmountStart").val(ajaxData.totalTradeAmountDown);
    $("#totalTradeAmountEnd").val(ajaxData.totalTradeAmountUp);
    $("#totalReceivableStart").val(ajaxData.totalReceivableDown);
    $("#totalReceivableEnd").val(ajaxData.totalReceivableUp);
    $("#debtAmountStart").val(ajaxData.totalDebtDown);
    $("#debtAmountEnd").val(ajaxData.totalDebtUp);
    if(ajaxData.totalDebtDown || ajaxData.totalDebtUp) {
        $("#hasDebt").val('true');
    }
    $("#maxRows").val(ajaxData.maxRows);
    $("#relationType").val(ajaxData.relationType);
    $("#hasDeposit").val(ajaxData.hasDeposit);
    $("#sortStatus").val(ajaxData.sort);
    $(".J_supplier_sort").removeClass("hover");
    if(ajaxData.sort == ' created_time desc ') {
        $("#createdTimeSortSpan").addClass("arrowDown").removeClass("arrowUp");
        $("#createdTimeSort").addClass("hover");
    } else if(ajaxData.sort == ' created_time asc ') {
        $("#createdTimeSortSpan").addClass("arrowUp").removeClass("arrowDown");
        $("#createdTimeSort").addClass("hover");
    } else if(ajaxData.sort == ' last_inventory_time desc ') {
        $("#lastInventoryTimeSortSpan").addClass("arrowDown").removeClass("arrowUp");
        $("#lastInventoryTimeSort").addClass("hover");
    } else if(ajaxData.sort == ' last_inventory_time asc ') {
        $("#lastInventoryTimeSortSpan").addClass("arrowUp").removeClass("arrowDown");
        $("#lastInventoryTimeSort").addClass("hover");
    } else if(ajaxData.sort == ' total_trade_amount desc ') {
        $("#totalTradeAmountSortSpan").addClass("arrowDown").removeClass("arrowUp");
        $("#totalTradeAmountSort").parent().addClass("hover");
    } else if(ajaxData.sort == ' total_trade_amount asc ') {
        $("#totalTradeAmountSortSpan").addClass("arrowUp").removeClass("arrowDown");
        $("#totalTradeAmountSort").parent().addClass("hover");
    } else if(ajaxData.sort == ' total_return_debt asc ') {
        $("#totalReceivableSortSpan").addClass("arrowUp").removeClass("arrowDown");
        $("#totalReceivableSort").parent().addClass("hover");
    }  else if(ajaxData.sort == ' total_return_debt desc ') {
        $("#totalReceivableSortSpan").addClass("arrowDown").removeClass("arrowUp");
        $("#totalReceivableSort").parent().addClass("hover");
    }  else if(ajaxData.sort == ' total_debt asc ') {
        $("#totalPayableSortSpan").addClass("arrowUp").removeClass("arrowDown");
        $("#totalPayableSort").parent().addClass("hover");
    }  else if(ajaxData.sort == ' total_debt desc ') {
        $("#totalPayableSortSpan").addClass("arrowDown").removeClass("arrowUp");
        $("#totalPayableSort").parent().addClass("hover");
    }  else if(ajaxData.sort == ' total_deposit asc ') {
        $("#depositSortSpan").addClass("arrowUp").removeClass("arrowDown");
        $("#depositSort").parent().addClass("hover");
    }  else if(ajaxData.sort == ' total_deposit desc ') {
        $("#depositSortSpan").addClass("arrowDown").removeClass("arrowUp");
        $("#depositSort").parent().addClass("hover");
    } else {
        $("#createdTimeSortSpan").addClass("arrowDown").removeClass("arrowUp");
        $("#createdTimeSort").addClass("hover");
    }
    $(".lineBody input").each(function(){
        if($(this).attr("initialvalue") == $(this).val()) {
            $(this).css("color","#ADADAD");
        }
    });
}