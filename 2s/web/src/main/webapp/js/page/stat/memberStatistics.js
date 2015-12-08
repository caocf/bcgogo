/**
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 12-11-13
 * Time: 上午11:13
 * To change this template use File | Settings | File Templates.
 */

/**
 * Created by IntelliJ IDEA.
 * User: liuWei
 * Date: 12-9-11
 * Time: 下午7:43
 * 依赖 suggestion.js
 */

function map() {
    var struct = function(key, value) {
        this.key = key;
        this.value = value;
    }

    var put = function(key, value) {
            for(var i = 0; i < this.arr.length; i++) {
                if(this.arr[i].key === key) {
                this.arr[i].value = value;
                return;
            }
        }
        this.arr[this.arr.length] = new struct(key, value);
    }

    var get = function(key) {
            for(var i = 0; i < this.arr.length; i++) {
                if(this.arr[i].key === key) {
                return this.arr[i].value;
            }
        }
        return null;
    }

    var remove = function(key) {
        var v;
            for(var i = 0; i < this.arr.length; i++) {
            v = this.arr.pop();
                if(v.key === key) {
                break;
            }
            this.arr.unshift(v);
        }
    }

    var size = function() {
        return this.arr.length;
    }

    var isEmpty = function() {
        return this.arr.length <= 0;
    }

    var clearMap = function() {
        this.arr = [];
    }
    this.arr = new Array();
    this.get = get;
    this.put = put;
    this.remove = remove;
    this.size = size;
    this.isEmpty = isEmpty;
    this.clearMap = clearMap;
}
var jsonStrMap = new map();

$(document).ready(function() {
    $("#print,#print2,#print3").hover(function(){
        $(this).css("color","#FD5300");
    },function(){
        $(this).css("color","#6699cc");
    });

  jQuery("#buy").click(function() {
    $(this).addClass("big_hover_title").siblings().removeClass("big_hover_title");
    $("#recordBuy").css("display", "");
    $("#cardInfo").css("display", "none");
    $("#recordMember").css("display", "none");
  })

  jQuery("#member").click(function() {
    $(this).addClass("big_hover_title").siblings().removeClass("big_hover_title");
    $("#recordBuy").css("display", "none");
    $("#cardInfo").css("display", "none");
    $("#recordMember").css("display", "");
  })

  jQuery("#back").click(function() {
    $(this).addClass("big_hover_title").siblings().removeClass("big_hover_title");
    $("#recordBuy").css("display", "none");
    $("#cardInfo").css("display", "");
    $("#recordMember").css("display", "none");
  })


  //绑定事件radio
 /* $("input[name='date_select']").bind("click", function() {
    var now = new Date();
    var year = now.getFullYear();
    $(this).parent().parent().find("label[name='radioLabel']").removeClass("radioTextChecked");
    $(this).parent().parent().find("label[name='radioLabel']").addClass("radioText");
    $(this).parent().removeClass("radioText");
    $(this).parent().addClass("radioTextChecked");

    if ($(this).val() == "this_week") {
      $("#startDate").val(dateUtil.getWeekStartDate());
      $("#endDate").val(dateUtil.getWeekEndDate());
    } else if ($(this).val() == "this_month") {
      $("#startDate").val(dateUtil.getMonthStartDate());
      $("#endDate").val(dateUtil.getMonthEndDate());
    } else if ($(this).val() == "this_year") {
      $("#startDate").val(year + "-01-01");
      $("#endDate").val(year + "-12-31");
    }
  });*/
  $("#my_date_thismonth").click();

  $("#radMonth").click(function () {
    checkedChartTypeRadio("month");
  });
  $("#radDay").click(function () {
    checkedChartTypeRadio("day");
  });

  $("#runningStat").click(function() {
    window.location.href = "runningStat.do?method=getRunningStat";
  });

  $("#first_cont").click(function() {
    window.location.href = "businessStat.do?method=getBusinessStat";
  });

  $("#itemStat").click(function() {
    window.location.href = "itemStat.do?method=getItemStat";
  });
  $("#memberStat").click(function() {
    window.location.href = "member.do?method=memberStat";
  });
  $("#couponConsumeStat").click(function() {
    window.location.href = "couponConsume.do?method=couponConsumeStat";
  });

  $("#startDate,#endDate")
      .datepicker({
        "numberOfMonths":1,
        "showButtonPanel":false,
        "changeYear":true,
        "showHour":false,
        "showMinute":false,
        "changeMonth":true,
        "yearRange":"c-100:c+100",
        "yearSuffix":""
      })
      .blur(function() {
        var startDate = $("#startDate").val();
        var endDate = $("#endDate").val();
        if (startDate == "" || endDate == "") return;
        if (APP_BCGOGO.Validator.stringIsZhCn(startDate) && APP_BCGOGO.Validator.stringIsZhCn(endDate)) {
          return;
        } else {
          if (startDate > endDate) {
            $("#endDate").val(startDate);
            $("#startDate").val(endDate);
          }
        }
      })
      .bind("click", function() {
        $(this).blur();
      })
      .change(function() {
        var startDate = $("#startDate").val();
        var endDate = $("#endDate").val();
        $(".good_his > .today_list").removeClass("hoverList");
        if (endDate == "" || startDate == "") {
          return;
        }
        if (APP_BCGOGO.Validator.stringIsZhCn(startDate) && APP_BCGOGO.Validator.stringIsZhCn(endDate)) {
          return;
        } else {
          if (startDate > endDate) {
            $("#endDate").val(startDate);
            $("#startDate").val(endDate);
          }
        }
      });


  $("#resetSearchCondition").click(function() {
    //reset form
    $("#memberStatisticsForm").resetForm();
      if(!$("#my_date_thismonth").hasClass("clicked")){
          $("#my_date_thismonth").click();
      }else{
          $("#my_date_thismonth").click();
          $("#my_date_thismonth").click();
      }


  });

  $("#statistics").click(function(e) {
    $("#infoCard tr:not(:first)").remove();
    $("#consumeCard tr:not(:first)").remove();
    $("#returnCard tr:not(:first)").remove();
    $("#back").text("退卡记录(0)");
    $("#member").text("会员卡消费详细(0)");
    $("#buy").text("购卡续卡记录(0)");
    e.preventDefault();
    var param = $("#memberStatisticsForm").serializeArray();
    var paramJson = {};
    $.each(param, function(index, val) {
      paramJson[val.name] = val.value;
    });

    initMemberCardOrder(paramJson);
    initMemberCardReturn(paramJson);
    initMemberConsume(paramJson);
  });


  //客户 或者  供应商下拉框
  $("#customerName")
      .bind('click', function () {
        getCustomerOrSupplierSuggestion($(this));
      })
      .bind('keyup', function(event) {
        var eventKeyCode = event.which || event.keyCode;
        if (GLOBAL.Interactive.keyNameFromKeyCode(eventKeyCode).search(/left|up|right|down/g) == -1) {
          getCustomerOrSupplierSuggestion(this,eventKeyCode);
        }

      })
      .bind('change', function() {
        $("#customerId").val('');
      });
  //客户车牌下拉框
  $("#vehicle").bind('click', function () {
    getVehicleLicenceNoSuggestion($(this));
  })
      .bind('keyup', function (event) {
        var eventKeyCode = event.which || event.keyCode;
        if (GLOBAL.Interactive.keyNameFromKeyCode(eventKeyCode).search(/left|up|right|down/g) == -1) {
          getVehicleLicenceNoSuggestion($(this));
        }
      });



    $("#print,#print2,#print3").bind("click",function(){
        var orderType = $(this).attr("orderType");
        var jsonObj = null;
        var url = "member.do?method=getBusinessMemberInfoToPrint";
        if("memberCardOrder"==orderType)
        {
            jsonObj = jsonStrMap.get("memberCardOrder");
        }
        if("memberConsume" == orderType)
        {
            jsonObj = jsonStrMap.get("memberConsume");
        }
        if("memberReturn" == orderType)
        {
            jsonObj = jsonStrMap.get("memberReturn");
        }

        if(jsonObj[1].totalRows==0)
        {
            nsDialog.jAlert("无数据，不能打印!");
            return;
        }

        var dataObj = JSON.stringify(jsonObj[0]);
        var startDateStr =  jsonObj[1].startDateStr;
        var endDateStr = jsonObj[1].endDateStr;

        var data={
            dataObj:dataObj,
            startDateStr:startDateStr,
            endDateStr:endDateStr,
            orderType:orderType,
            now:new Date()
        };

        $.ajax({
            url:url,
            data:data,
            type: "POST",
            cache:false,
            success:function(data){
                if(!data) return;

                var printWin = window.open("", "", "width=1024,height=768");

                with(printWin.document){
                    open("text/html", "replace");
                    write(data);
                    close();
                }
            }
        });
    });

    $("#my_date_self_defining,#my_date_thisyear,#my_date_thismonth,#my_date_thisweek").bind("click",function(){
        $("#statistics").click();

    })

});

function initMemberCardOrder(paramJson) {
  var str = 'member.do?method=getMemberCardOrder';
  $.ajax({
    type:"POST",
    url:"member.do?method=getMemberCardOrder",
    data:paramJson,
    cache:false,
    dataType:"json",
    success:function(json) {
      initMemberCardByJson(json);
      initPages(json, "dynamicalMemberBuyCard", str, '', "initMemberCardByJson", '', '', paramJson, '');
    }
  });
}


function initMemberConsume(paramJson) {
  var str = 'member.do?method=getMemberConsume';
  $.ajax({
    type:"POST",
    url:"member.do?method=getMemberConsume",
    data:paramJson,
    cache:false,
    dataType:"json",
    success:function(json) {
      initMemberConsumeByJson(json);
      initPages(json, "dynamicalMemberConsume", str, '', "initMemberConsumeByJson", '', '', paramJson, '');
    }
  });
}


function initMemberConsumeByJson(data) {
    jsonStrMap.put("memberConsume",data);
  $("#consumeTotal,#pageConsumeTotal").text("0");
  $("#consumeCard tr:not(:first)").remove();
  if (data == null || data[0] == null || data[0].orders == null || data[0].orders == 0) {
    return;
  }
  $("#member").text("会员卡消费详细(" + data[1].totalRows + ")");

  $.each(data[0].orders, function(index, order) {
    var orderId = (!order.orderIdStr ? "" : order.orderIdStr);
    var receiptNo = (!order.receiptNo ? "--" : order.receiptNo);
    var orderType = (!order.orderType ? "--" : order.orderType);
    var orderTypeValue = (!order.orderTypeValue ? "--" : order.orderTypeValue);
    var vehicle = (!order.vehicle ? "--" : order.vehicle);
    var consumeType = (!order.consumeType ? "--" : order.consumeType);
    var customerName = (!order.customerOrSupplierName ? "--" : order.customerOrSupplierName);
    var memberNo = (!order.accountMemberNo ? "--" : order.accountMemberNo);
    var memberServiceChange = (!order.orderContent ? "--" : order.orderContent);
    var memberServiceChangeShort = (!order.orderContentShort ? "--" : order.orderContentShort);
    var customerStatus = (!order.customerStatus ? "" : order.customerStatus);
    var vestDateStr = (!order.vestDateStr ? "" : order.vestDateStr);
    var tr = '<tr class="table-row-original">';
    tr += '<td class="first-padding">' + (index + 1) + '</td>';
    tr += '<td title="' + vestDateStr + '">' + vestDateStr + '</td>';
    tr += '<td title="' + orderTypeValue + '">' + orderTypeValue + '</td>';
    tr += '<td title="' + receiptNo + '">';
    if ("SALE" == orderType) {
      tr += '<a target="_blank" style="cursor:pointer;" href="sale.do?method=getSalesOrder&menu-uid=WEB.TXN.SALE_MANAGE.SALE&salesOrderId=' + orderId + '">' + receiptNo + '</a>';
    } else if ("WASH_BEAUTY" == orderType) {
      tr += '<a target="_blank" style="cursor:pointer;" href="washBeauty.do?method=getWashBeautyOrder&washBeautyOrderId=' + orderId + '">' + receiptNo + '</a>';
    } else if ("REPAIR" == orderType) {
      tr += '<a target="_blank" style="cursor:pointer;" href="txn.do?method=getRepairOrder&menu-uid=VEHICLE_CONSTRUCTION_REPAIR&repairOrderId=' + orderId + '">' + receiptNo + '</a>';
    }else if ("CUSTOMER_STATEMENT_ACCOUNT" == orderType) {
        tr += '<a target="_blank" style="cursor:pointer;" href="statementAccount.do?method=showStatementAccountOrderById&print=false&statementOrderId=' + orderId + '">' + receiptNo + '</a>';
    }  else {
      tr += receiptNo;
    }
    tr += '</td>';
    if ("DISABLED" == customerStatus) {
      tr += '<td style="color:#999999" title="' + customerName + '"><span class="customer limit-span">' + customerName + '</span></td>';
    } else {
      tr += '<td title="' + customerName + '"><span class="customer limit-span">' + customerName + '</span></td>';
    }
    tr += '<td title="' + vehicle + '">' + vehicle + '</td>';
    tr += '<td title="' + consumeType + '">' + consumeType + '</td>';
    tr += '<td title="' + memberNo + '">' + memberNo + '</td>';
    tr += '<td title="' + memberServiceChange + '" class="last-padding">' + memberServiceChangeShort + '</td>';

    tr += '</tr>';
    $("#consumeCard").append($(tr));
  //  tableUtil.limitSpanWidth($(".customer","#consumeCard"),10);
    tableUtil.tableStyle('#consumeCard','.tab_title');
  });

  if (data[0].totalAmounts != null && data[0].totalAmounts.MEMBER_BALANCE_PAY != 0) {
    $("#consumeTotal").text(data[0].totalAmounts.MEMBER_BALANCE_PAY);
  }
  if (data[0].currentPageTotalAmounts != null && data[0].currentPageTotalAmounts.member_balance_pay != 0) {
    $("#pageConsumeTotal").text(data[0].currentPageTotalAmounts.member_balance_pay);
  }
}


function initMemberCardReturn(paramJson) {
  var str = 'member.do?method=getMemberReturnOrder';
  $.ajax({
    type:"POST",
    url:"member.do?method=getMemberReturnOrder",
    data:paramJson,
    cache:false,
    dataType:"json",
    success:function(json) {
      initMemberCardReturnByJson(json);
      initPages(json, "dynamicalMemberReturnCard", str, '', "initMemberCardReturnByJson", '', '', paramJson, '');
    }
  });
}

function initMemberCardByJson(data) {
    jsonStrMap.put("memberCardOrder",data);
  $(".j_clear_span").text("0");
  $("#infoCard tr:not(:first)").remove();
  if (data == null || data[0] == null || data[0].orders == null || data[0].orders == 0) {
    return;
  }
  $("#buy").text("购卡续卡记录(" + data[1].totalRows + ")");
  $.each(data[0].orders, function(index, order) {

    var customerName = (!order.customerName ? "**客户**" : order.customerName);
    var memberNo = (!order.memberNo ? "--" : order.memberNo);
    var memberBalanceChange = (!order.memberBalanceChange ? "--" : order.memberBalanceChange);
    var memberServiceChange = (!order.memberServiceChange ? "--" : order.memberServiceChange);
    var memberServiceChangeShort = (!order.memberServiceChangeShort ? "--" : order.memberServiceChangeShort);
    var total = (order.total == null ? "--" : order.total);
    var settledAmount = (order.settledAmount == null ? "--" : order.settledAmount );
    var discount = (order.discount == null ? "--" : order.discount );
    var debt = (order.debt == null ? "--" : order.debt);
    var customerStatus = (!order.customerStatus ? "" : order.customerStatus);
    var vestDateStr = (!order.vestDateStr ? "" : order.vestDateStr);
    var tr = '<tr class="table-row-original">';
    tr += '<td class="first-padding">' + (index + 1) + '</td>';
    if ("DISABLED" == customerStatus) {
      tr += '<td style="color:#999999" title="' + customerName + '"><span class="customer limit-span">' + customerName + '</span></td>';
    } else {
      tr += '<td title="' + customerName + '"><span class="customer limit-span">' + customerName + '</span></td>';
    }
    tr += '<td title="' + memberNo + '">' + memberNo + '</td>';
    tr += '<td title="' + memberBalanceChange + '">' + memberBalanceChange + '</td>';
    tr += '<td title="' + memberServiceChange + '"><div style="margin:0 5px 0 0;">' + memberServiceChangeShort + '</div></td>';
    tr += '<td title="' + total + '">' + total + '</td>';
    tr += '<td title="' + settledAmount + '">' + settledAmount + '</td>';
    tr += '<td title="' + discount + '">' + discount + '</td>';
    tr += '<td title="' + debt + '">' + debt + '</td>';
    tr += '<td title="' + vestDateStr + '" class="last-padding">' + vestDateStr + '</td>';
    tr += '</tr>';
    $("#infoCard").append($(tr));

//    tableUtil.limitSpanWidth($(".customer","#infoCard"),10);
    tableUtil.tableStyle('#infoCard','.tab_title');
  });
  initPageStatistics(data);
}


function initMemberCardReturnByJson(data) {
    jsonStrMap.put("memberReturn",data);
  $("#returnTotal,#pageReturnTotal").text("0");
  $("#returnCard tr:not(:first)").remove();
  if (data == null || data[0] == null || data[0].orders == null || data[0].orders == 0) {
    return;
  }
  $("#back").text("退卡记录(" + data[1].totalRows + ")");
  $.each(data[0].orders, function(index, order) {

    var customerName = (!order.customerName ? "**客户**" : order.customerName);
    var memberNo = (!order.memberNo ? "--" : order.memberNo);
    var memberType = (!order.memberType ? "--" : order.memberType);
    var memberServiceChange = (!order.memberServiceChange ? "--" : order.memberServiceChange);
    var memberServiceChangeShort = (!order.memberServiceChangeShort ? "--" : order.memberServiceChangeShort);
    var memberBalance = (order.memberBalance == null ? "--" : order.memberBalance);
    var settledAmount = (order.settledAmount == null ? "--" : order.settledAmount );
    var vestDateStr = (!order.vestDateStr ? "" : order.vestDateStr);
    var customerStatus = (!order.customerStatus ? "" : order.customerStatus);
    var tr = '<tr class="table-row-original">';
    tr += '<td class="first-padding">' + (index + 1) + '</td>';
    if ("DISABLED" == customerStatus) {
      tr += '<td style="color:#999999" title="' + customerName + '"><span class="customer limit-span">' + customerName + '</span></td>';
    } else {
      tr += '<td title="' + customerName + '"><span class="customer limit-span">' + customerName + '</span></td>';
    }
    tr += '<td title="' + memberNo + '">' + memberNo + '</td>';
    tr += '<td title="' + memberType + '">' + memberType + '</td>';
    tr += '<td title="' + memberServiceChange + '">' + memberServiceChangeShort + '</td>';
    tr += '<td title="' + memberBalance + '">' + memberBalance + '</td>';
    tr += '<td title="' + settledAmount + '">' + settledAmount + '</td>';
    tr += '<td title="' + vestDateStr + '" class="last-padding">' + vestDateStr + '</td>';
    tr += '</tr>';
    $("#returnCard").append($(tr));
    
   // tableUtil.limitSpanWidth($(".customer","#returnCard"),10);
    tableUtil.tableStyle('#returnCard','.tab_title');
  });
  if (data[0].total != null && data[0].total != 0) {
    $("#returnTotal").text(data[0].total);
  }
  if (data[0].pageTotal != null && data[0].pageTotal != 0) {
    $("#pageReturnTotal").text(data[0].pageTotal);
  }
}


function initPageStatistics(data) {
  //总计 小计
  if (data[0].resultTotal != null && data[0].resultTotal != 0) {
    $("#buyCardTotal").text(data[0].resultTotal);
  }
  if (data[0].resultSettledAmount != null && data[0].resultSettledAmount != 0) {
    $("#buyCardTotalSettledAmount").text(data[0].resultSettledAmount);
  }
  if (data[0].pageTotal != null && data[0].pageTotal != 0) {
    $("#pageBuyCardTotal").text(data[0].pageTotal);
  }
  if (data[0].pageTotalSettledAmount != null && data[0].pageTotalSettledAmount != 0) {
    $("#pageBuyCardSettledAmount").text(data[0].pageTotalSettledAmount);
  }
  if (data[0].pageDebt != null && data[0].pageDebt != 0) {
    $("#pageBuyCardDebt").text(data[0].pageDebt);
  }
  if (data[0].pageDiscount != null && data[0].pageDiscount != 0) {
    $("#pageBuyCardDiscount").text(data[0].pageDiscount);
  }
}

function getVehicleLicenceNoSuggestion($domObject) {
  var searchWord = $domObject.val().replace(/[\ |\\]/g, "");
  if (GLOBAL.Lang.isEmpty(searchWord) && GLOBAL.Lang.isEmpty($("#customerId").val())) return;
  var droplist = APP_BCGOGO.Module.droplist;
  droplist.setUUID(GLOBAL.Util.generateUUID());
  var ajaxUrl = "searchInventoryIndex.do?method=getVehicleLicenceNoSuggestion";
  var ajaxData = {
    searchWord:searchWord,
    uuid:droplist.getUUID(),
    customerId:$("#customerId").val()
  };
  APP_BCGOGO.wjl.LazySearcher.lazySearch(ajaxUrl, ajaxData, function (result) {
    droplist.show({
      "selector":$domObject,
      "data":result,
      "onSelect":function (event, index, data) {
        $domObject.val(data.label);
        $domObject.css({"color":"#000000"});
        droplist.hide();
      }
    });
  });
}


function getMemberSuggestion($domObject) {
  var searchWord = $domObject.val().replace(/[\ |\\]/g, "");
  if (GLOBAL.Lang.isEmpty(searchWord) && GLOBAL.Lang.isEmpty($("#customerId").val())) return;
  var droplist = APP_BCGOGO.Module.droplist;
  droplist.setUUID(GLOBAL.Util.generateUUID());
  var ajaxUrl = "searchInventoryIndex.do?method=getVehicleLicenceNoSuggestion";
  var ajaxData = {
    searchWord:searchWord,
    uuid:droplist.getUUID(),
    customerId:$("#customerId").val()
  };
  APP_BCGOGO.wjl.LazySearcher.lazySearch(ajaxUrl, ajaxData, function (result) {
       if(!G.isEmpty(result.data[0])){
            G.completer({
                    'domObject':domObject,
                    'keycode':keycode,
                    'title':result.data[0].details.name}
            );
        }
    droplist.show({
      "selector":$domObject,
      "data":result,
      "onSelect":function (event, index, data) {
        $domObject.val(data.label);
        $domObject.css({"color":"#000000"});
        droplist.hide();
      }
    });
  });
}

function getCustomerOrSupplierSuggestion(domObject,keycode) {
  var $domObject=$(domObject);
  var searchWord = $domObject.val().replace(/[\ |\\]/g, "");
  var droplist = APP_BCGOGO.Module.droplist;
  droplist.setUUID(GLOBAL.Util.generateUUID());
  var ajaxData = {
    searchWord:searchWord,
    uuid:droplist.getUUID()
  };
  if ($domObject.attr("id") == "customerName") {
    ajaxData["customerOrSupplier"] = "customer";
    ajaxData["titles"] = "name,mobile";
  }
    var ajaxUrl = "searchInventoryIndex.do?method=getCustomerSupplierSuggestion";
    APP_BCGOGO.wjl.LazySearcher.lazySearch(ajaxUrl, ajaxData, function (result) {
        if(!G.isEmpty(result.data[0])){
            G.completer({
                    'domObject':domObject,
                    'keycode':keycode,
                    'title':result.data[0].details.name}
            );
        }
    droplist.show({
      "selector":$domObject,
      "data":result,
      "onSelect":function (event, index, data) {
        $domObject.val(data.details.name);
        if ($domObject.attr("id") == "customerName") {
          $("#customerId").val(data.details.id);
          $("#mobile").val(data.details.mobile);
        }

        $domObject.css({"color":"#000000"});
        droplist.hide();
        $("#statistics").click();
      },
      "onKeyboardSelect":function(event, index, data, hook) {
        $domObject.val(data.details.name);
        if ($domObject.attr("id") == "customerName") {
          $("#customerId").val(data.details.id);
          $("#mobile").val(data.details.mobile);
        }
      }
    });
  });
}


function notOpen() {
  var time = new Array(), timeFlag = true;
  time[0] = new Date().getTime();
  time[1] = new Date().getTime();
  var reg = /^(\d+)$/;
  time[1] = new Date().getTime();
  if (time[1] - time[0] > 3000 || timeFlag) {
    time[0] = time[1];
    timeFlag = false;
    showMessage.fadeMessage("35%", "40%", "slow", 3000, "此功能稍后开放！");     // top left fadeIn fadeOut message
  }
}

