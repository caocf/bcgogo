try {
    var test = new Map();
} catch (e) {
    window.Map = function () {
        var struct = function (key, value) {
            this.key = key;
            this.value = value;
        };

        var put = function (key, value) {
            for (var i = 0; i < this.arr.length; i++) {
                if (this.arr[i].key === key) {
                    this.arr[i].value = value;
                    return;
                }
            }
            this.arr[this.arr.length] = new struct(key, value);
        };

        var get = function (key) {
            for (var i = 0; i < this.arr.length; i++) {
                if (this.arr[i].key === key) {
                    return this.arr[i].value;
                }
            }
            return null;
        };

        var remove = function (key) {
            var v;
            for (var i = 0; i < this.arr.length; i++) {
                v = this.arr.pop();
                if (v.key === key) {
                    break;
                }
                this.arr.unshift(v);
            }
        };

        var size = function () {
            return this.arr.length;
        };

        var isEmpty = function () {
            return this.arr.length <= 0;
        };

        var clearMap = function () {
            this.arr = [];
        };
        this.arr = new Array();
        this.get = get;
        this.put = put;
        this.remove = remove;
        this.size = size;
        this.isEmpty = isEmpty;
        this.clearMap = clearMap;
    };
}

var jsonStrMap = new Map();
$(document).ready(function () {
  $("#incomeCategory").attr("checked","checked");
  $("#expenditureCategory").attr("checked","checked");

  $("#clearButton").bind("click", function() {
    $("a[name='date_select']").removeClass("clicked");
    $("#accountCategory").val("");
    $("#docNo").val("");
    $("#dept").val("");
    $("#person").val("");
    $("#moneyCategoryStr").val("");

    $("#incomeCategory").attr("checked","checked");
    $("#expenditureCategory").attr("checked","checked");
    $("#editDateStartStr").val("");
    $("#editDateEndStr").val("");
    $("#businessCategory").val("");
  });


  $("#accountCategory").live("click focus keyup",function(event){

      event = event || event.which;

      var keyCode = event.keyCode;

      if(keyCode == 37 || keyCode == 38 || keyCode == 39 || keyCode == 40)
      {
          return;
      }
      $(this).val(APP_BCGOGO.StringFilter.inputtingOtherCostNameFilter($(this).val()));
      var obj = this;
    askForAssistDropList(event,obj);
  });


  $("#dept").live("click focus keyup",function(event){

      event = event || event.which;

      var keyCode = event.keyCode;

      if(keyCode == 37 || keyCode == 38 || keyCode == 39 || keyCode == 40)
      {
          return;
      }
      $(this).val(APP_BCGOGO.StringFilter.inputtingOtherCostNameFilter($(this).val()));
      var obj = this;
    askForDeptDropList(event,obj);
  });

  $("a[name='date_select']").bind("click", function() {
    var now = new Date();
    var year = now.getFullYear();
    var idStr = $(this).attr("id");
    $("a[name='date_select']").not(this).removeClass("clicked");

    if (!$(this).hasClass("clicked")) {
      $(this).addClass("clicked");

      if (idStr == "date_yesterday") {
        $("#editDateStartStr").val(dateUtil.getYesterday());
        $("#editDateEndStr").val(dateUtil.getYesterday());
      } else if (idStr == "date_today") {
        $("#editDateStartStr").val(dateUtil.getToday());
        $("#editDateEndStr").val(dateUtil.getToday());
      } else if (idStr == "date_last_week") {
        $("#editDateStartStr").val(dateUtil.getOneWeekBefore());
        $("#editDateEndStr").val(dateUtil.getToday());
      } else if (idStr == "date_last_month") {
        $("#editDateStartStr").val(dateUtil.getOneMonthBefore());
        $("#editDateEndStr").val(dateUtil.getToday());
      } else if (idStr == "date_last_year") {
        $("#editDateStartStr").val(dateUtil.getOneYearBefore());
        $("#editDateEndStr").val(dateUtil.getToday());
      } else if (idStr == "date_self_define") {
        $("#editDateStartStr").val("");
        $("#editDateEndStr").val("");
      } else if (idStr == "date_three_month") {
        $("#editDateStartStr").val(dateUtil.getThreeMonthStartDate());
        $("#editDateEndStr").val(dateUtil.getToday());
      }
    } else {
      $(this).removeClass("clicked");
      $("#editDateStartStr").val("");
      $("#editDateEndStr").val("");
    }


  });


  $("input[name='moneyCategorySelect']").bind("click", function() {

    var moneyCategory ="";
    if($("#incomeCategory").attr("checked")){
      moneyCategory += "income,";
    }
    if($("#expenditureCategory").attr("checked")){
      moneyCategory += "expenses";
    }

    $("#moneyCategoryStr").val(moneyCategory);

   });

//    $('#accountCategory').change(function () {
//        var selectedCategory = $(this).children('option:selected').val();
//        var incomeCategoryArray = ["营业外收入"];
//        var expensesCategoryArray = ["房租", "工资提成", "水电杂项", "其他支出"];
//
//        if (selectedCategory == "营业外收入") {
//            $("#moneyCategory1").attr("checked", "checked");
//            $("#moneyCategory1").removeAttr("disabled");
//
//            $("#moneyCategory2").removeAttr("checked");
//            $("#moneyCategory2").attr("disabled", "true");
//
//
//        } else if (selectedCategory == "房租" || selectedCategory == "工资提成" || selectedCategory == "水电杂项" || selectedCategory == "其他支出") {
//            $("#moneyCategory2").attr("checked", "checked");
//            $("#moneyCategory2").removeAttr("disabled");
//
//            $("#moneyCategory1").removeAttr("checked");
//            $("#moneyCategory1").attr("disabled", "true");
//        } else if (selectedCategory == "") {
//
//            $("#moneyCategory1").removeAttr("checked");
//            $("#moneyCategory1").attr("disabled", "true");
//            $("#moneyCategory2").removeAttr("checked");
//            $("#moneyCategory2").attr("disabled", "true");
//
//
//        }
//
//    });

    $("#addBusinessAccountBtn").click(function () {
        bcgogo.checksession({
            "parentWindow": window.parent,
            'iframe_PopupBox': $("#iframe_PopupBox")[0],
            'src': "businessAccount.do?method=addBusinessAccount&isNewBusinessAccount=true"
        });
    });

    $(".editItem").live("click", function (e) {
        var id = $(this).parent().parent().attr('id');
        var url = "businessAccount.do?method=editBusinessAccount&isEditBusinessAccount=true&id=" + id;
        bcgogo.checksession({
            "parentWindow": window.parent,
            'iframe_PopupBox': $("#iframe_PopupBox")[0],
            'src': url
        });
    });

    $(".deleteItem").live("click", function (e) {
        if (confirm("确认删除？")) {
            var businessAccountId = $(this).parent().parent().attr('id');
            if (businessAccountId != '' && businessAccountId != null) {
                APP_BCGOGO.Net.syncAjax({
                    url: "businessAccount.do?method=deleteBusinessAccount",
                    dataType: "json",
                    data: {
                        businessAccountId: businessAccountId
                    },
                    success: function (json) {
                        if (json != null && json != '' && json.status == "delete") {
                            searchBusinessAccount($("#getPagedynamical").val());
                            showMessage.fadeMessage("45%", "40%", "slow", 3000, "删除成功！");
                        }
                    }
                });
            }
        }
    });

    $("#searchBtn").bind("click", function (e) {
        searchBusinessAccount(1);
    });

    $("#printBtn").bind("click", function () {
        var jsonObj = jsonStrMap.get("jsonStr");
        if (jsonObj.results == null || jsonObj.pager.totalRows == 0) {
            nsDialog.jAlert("无数据，不能打印");
            return;
        }
        var url = "businessAccount.do?method=getDateToPrint";

        var sum = Math.abs(jsonObj.data.sum);
        var incomeSum = jsonObj.data.incomeSum;
        var expensesSum = jsonObj.data.expensesSum;
        var editDateStartStr = jsonObj.data.editDateStartStr;
        var editDateEndStr = jsonObj.data.editDateEndStr;
        var accountCategory = jsonObj.data.accountCategory;
        var moneyCategoryStr = jsonObj.data.moneyCategoryStr;
        var data = {
            sum: sum,
            incomeSum: incomeSum,
            expensesSum: expensesSum,
            editDateStartStr: editDateStartStr,
            editDateEndStr: editDateEndStr,
            accountCategory: accountCategory,
            dataList: JSON.stringify(jsonObj.results),
            pager: JSON.stringify(jsonObj.pager),
            moneyCategoryStr: moneyCategoryStr,
            now: new Date()
        };
        $.ajax({
            url: url,
            data: data,
            type: "POST",
            cache: false,
            success: function (data) {
                if (!data) return;

                var printWin = window.open("", "", "width=1024,height=768");

                with (printWin.document) {
                    open("text/html", "replace");
                    write(data);
                    close();
                }
            }
        });
    });


}) ;

function compareDate(DateOne, DateTwo) {

    var OneMonth = DateOne.substring(5, DateOne.lastIndexOf("-"));
    var OneDay = DateOne.substring(DateOne.length, DateOne.lastIndexOf("-") + 1);
    var OneYear = DateOne.substring(0, DateOne.indexOf("-"));

    var TwoMonth = DateTwo.substring(5, DateTwo.lastIndexOf("-"));
    var TwoDay = DateTwo.substring(DateTwo.length, DateTwo.lastIndexOf("-") + 1);
    var TwoYear = DateTwo.substring(0, DateTwo.indexOf("-"));

    if (Date.parse(OneMonth + "/" + OneDay + "/" + OneYear) > Date.parse(TwoMonth + "/" + TwoDay + "/" + TwoYear)) {
        return true;
    } else {
        return false;
    }

}

function searchBusinessAccount(startPageNo) {

    var editDateStartStr = $("#editDateStartStr").val();
    var editDateEndStr = $("#editDateEndStr").val();
    var accountCategory = $("#accountCategory").val();
    var docNo = $("#docNo").val();
    var dept = $("#dept").val();
    var person = $("#person").val();
    var moneyCategoryStr = $("#moneyCategoryStr").val();

    if (editDateStartStr != "" && editDateStartStr != null && editDateEndStr != "" && editDateEndStr != null) {
        if (compareDate(editDateStartStr, editDateEndStr)) {
            var dateTemp = editDateStartStr;
            editDateStartStr = editDateEndStr;
            editDateEndStr = dateTemp;
        }
    }

    $("#businessAccount_show tr:not(:first)").remove();
    pagingAjaxPost({
        url: "businessAccount.do?method=searchBusinessAccount",
        data: {
          editDateStartStr: editDateStartStr,
          editDateEndStr: editDateEndStr,
          accountCategory: accountCategory,
          docNo: docNo,
          dept: dept,
          person: person,
          startPageNo: startPageNo,
          moneyCategoryStr: moneyCategoryStr,
          businessCategory: $("#businessCategory").val()
        },
        functionName: "businessAccountSearchInitTr",
        dynamical: "dynamical",
        dataChange:[]
    })
}


//营业外记账查询列表的初始化

function businessAccountSearchInitTr(json) {

    jsonStrMap.put("jsonStr", json);

    $("#businessAccount_show tr:not(:first)").remove();
    var incomeTotal = 0;
    var expensesTotal = 0;

    var results = json.results;
    results = (results == null ? [] : results);

   if(results.length > 0 ){
     var str = '<tr class="space"><td colspan="10"></td></tr>';
     $("#businessAccount_show").append(str);
     $("#printBtn").css("display","block");
   }else{
     $("#printBtn").css("display","none");
   }

    for (var i = 0; i < results.length; i++) {
        if (results[i] != null) {
            var tr = "<tr class='titBody_Bg' id='" + results[i].idStr + "'>";
            tr += "<td style='padding-left:10px;'>" + results[i].editDateStr + "</td>";
            tr += "<td>" + results[i].accountCategory + "</td>";
            tr += "<td>" + results[i].docNo + "</td>";
            var content = results[i].content.length > 20 ? (results[i].content.substr(0, 17) + "...") : results[i].content;
            tr += "<td title='" + results[i].content + "'>" + content + "</td>";
            tr += "<td>" + results[i].businessCategory + "</td>";
            tr += "<td>" + (stringUtil.isEmpty(results[i].dept) ? "" : results[i].dept) + "</td>";
            tr += "<td>" + (stringUtil.isEmpty(results[i].person) ? "" : results[i].person) + "</td>";


            var itemTotal = results[i].total;
            if (results[i].moneyCategory == "income") {
                incomeTotal += itemTotal;
                tr += "<td><span class='red_color'>收入<span class='arialFont'>¥</span>" + results[i].total + "</span></td>";
            } else {
                expensesTotal += itemTotal;
                tr += "<td><span class='green_color'>支出<span class='arialFont'>¥</span>" + results[i].total + "</span></td>";
            }
            tr += "<td>";
            if (results[i].cash > 0) {
              tr += "现金<span class='arialFont'>¥</span>" + results[i].cash;
            }
            if (results[i].unionpay > 0) {
              tr += " 银联<span class='arialFont'>¥</span>" + results[i].unionpay;
            }
            if (results[i].check > 0) {
              tr += " 支票<span class='arialFont'>¥</span>" + results[i].check;
            }
            tr += "</td>";

            tr += "<td>";
            if (APP_BCGOGO.Permission.Stat.NonOperatingAccount.Update) {
                //tr += "<img src='images/bi.png' style=' margin-right:5px;cursor: pointer;' class='editItem'/>";
                tr += "<a class='editItem blue_col' href='javascript:void(0);'>修改</a>&nbsp;";
            }
            if (APP_BCGOGO.Permission.Stat.NonOperatingAccount.Delete) {
                //tr += "<img src='images/cha.png' style='cursor: pointer;' class='deleteItem'/>";
                tr += "<a class='deleteItem blue_col' href='javascript:void(0);'>删除</a>";
            }
            tr += "</td>";
            tr += "</tr>";

            $("#businessAccount_show").append(tr);
            $("#businessAccount_show").append('<tr class="titBottom_Bg"><td colspan="10"></td></tr>');
        }
    }

    if (json.results != null) {
        var total = incomeTotal - expensesTotal;
        var tr = "<tr class='titBody_Bg'>";
        if (total > 0) {
            tr += "<td style='text-align: right' colspan='8'>本页小计：<span class='red_color'>收入" + dataTransition.rounding(total, 2) + "元</span></td>";
        } else {
            tr += "<td style='text-align: right' colspan='8'>本页小计：<span class='green_color'> 支出" + Math.abs(dataTransition.rounding(total, 2)) + "元</span></td>";
        }

        tr += " <td colspan='2'>其中（<span class='red_color'>收入" + dataTransition.rounding(incomeTotal, 2) + "元</span>";
        tr += "<span class='green_color'> 支出" + dataTransition.rounding(expensesTotal, 2)  + "元</span>）" +
            "</td>";
        tr += " </tr>";
        $("#businessAccount_show").append(tr);
        $("#businessAccount_show").append('<tr class="titBottom_Bg"><td colspan="10"></td></tr>');


        var statistic = json.data;

       if(statistic.sum > 0){
         $("#totalAmountSpan").html('<span class="red_color">收入' + dataTransition.rounding(statistic.sum, 2) + '元</span>');
       }else{
         $("#totalAmountSpan").html('<span class="green_color">支出' + Math.abs(dataTransition.rounding(statistic.sum, 2)) + '元</span>');
       }
        $("#totalIncome").text(dataTransition.rounding(statistic.incomeSum, 2));
        $("#totalExpense").text(dataTransition.rounding(statistic.expensesSum, 2));
//        var tr = "<tr class='totalRow table-row-original'>";
//        var sum = parseFloat(statistic.sum);
//        if (sum > 0) {
//            tr += "<td style='color:#FF0000;' colspan='10'>总计：" + Math.round(sum * 10) / 10 + "元</td>";
//        } else {
//            tr += "<td style='color:#3CA701;' colspan='10'>总计：" + Math.round(Math.abs(sum) * 10) / 10 + "元</td>";
//        }
//
//        tr += " <td class='red'>" + statistic.incomeSum + "</td>";
//        tr += " <td class='blue'>" + statistic.expensesSum + "</td><td></td>";
//        tr += " </tr>";
//        $("#businessAccount_show").append(tr);
    }else{
      $("#totalAmountSpan").html('<span class="red_color">收入' +0 + '元</span>');

      $("#totalIncome").text("0");
      $("#totalExpense").text("0");
    }
//    tableUtil.tableStyle('#businessAccount_show', '.title_tb,.totalRow');




}

$("#saveBtn").live("click", function () {

    if (jQuery("#accountCategory").val() == "") {
        nsDialog.jAlert("请选择类别！");
        return false;
    }

    if ($("#moneyCategory").val()=="") {
        nsDialog.jAlert("请选择分类：收入或支出！");
        return false;
    }
    var currentDate = GLOBAL.Date.getCurrentFormatDate();
    if ($("#editDateStr").val() != currentDate) {
        if (confirm("日期与当前系统日期不符，确认保存？")) {
            $("#businessAccountForm").submit();
        }
    } else {
        $("#businessAccountForm").submit();
    }

});

$("#moneyCategoryIncome").live("click", function () {
  $("#moneyCategory").val("income");
});

$("#moneyCategoryExpenses").live("click", function () {
  $("#moneyCategory").val("expenses");
});


$("#div_close,#cancelBtn").live("click", function () {
    if (window.parent) {
        window.parent.document.getElementById("mask").style.display = "none";
        window.parent.document.getElementById("iframe_PopupBox").style.display = "none";
        window.parent.document.getElementById("iframe_PopupBox").src = "";
    } else {
        window.close();
    }
});



function setTotal() {
    var cash = jQuery("#cash").val();
    var unionpay = jQuery("#unionpay").val();
    var checkValue = jQuery("#check").val();

    if (cash == "" || cash == null) {
        cash = 0.0;
        jQuery("#cash").val("0");
    }

    if (unionpay == "" || unionpay == null) {
        unionpay = 0.0;
        jQuery("#unionpay").val("0");
    }

    if (checkValue == "" || checkValue == null) {
        checkValue = 0.0;
        jQuery("#check").val("0");
    }

    cash = parseFloat(cash);
    unionpay = parseFloat(unionpay);
    checkValue = parseFloat(checkValue);

    cash = isNaN(cash) ? 0 : cash;
    unionpay = isNaN(unionpay) ? 0 : unionpay;
    checkValue = isNaN(checkValue) ? 0 : checkValue;


    var total = cash + unionpay + checkValue;

    jQuery("#total").val(dataTransition.rounding(total, 2));
  jQuery("#totalSpan").text(dataTransition.rounding(total, 2));

}


$("#cash").live("blur",function () {
  jQuery(this).val(APP_BCGOGO.StringFilter.priceFilter(jQuery(this).val(), 2));
  setTotal();
}).live('keyup',function () {
      jQuery(this).val(APP_BCGOGO.StringFilter.inputtingPriceFilter(jQuery(this).val(), 2));
    }).live('focus', function () {
      if ($(this).val() == 0) {
        $(this).val("");
      }
      jQuery(this).val(APP_BCGOGO.StringFilter.inputtingPriceFilter(jQuery(this).val(), 2));
    });
$("#unionpay").live("blur",function () {
  jQuery(this).val(APP_BCGOGO.StringFilter.priceFilter(jQuery(this).val(), 2));
  setTotal();
}).live('keyup',function () {
      jQuery(this).val(APP_BCGOGO.StringFilter.inputtingPriceFilter(jQuery(this).val(), 2));
    }).live('focus', function () {
      if ($(this).val() == 0) {
        $(this).val("");
      }
      jQuery(this).val(APP_BCGOGO.StringFilter.inputtingPriceFilter(jQuery(this).val(), 2));
    });
$("#check").live("blur",function () {
  jQuery(this).val(APP_BCGOGO.StringFilter.priceFilter(jQuery(this).val(), 2));
  setTotal();
}).live('keyup',function () {
      jQuery(this).val(APP_BCGOGO.StringFilter.inputtingPriceFilter(jQuery(this).val(), 2));
    }).live('focus', function () {
      if ($(this).val() == 0) {
        $(this).val("");
      }
      jQuery(this).val(APP_BCGOGO.StringFilter.inputtingPriceFilter(jQuery(this).val(), 2));
    });

$("#businessCategory")._dropdownlist('businessCategary');


$("input").live("keyup",function () {
    this.value = jQuery.trim(this.value);
}).live("blur", function () {
        this.value = jQuery.trim(this.value);
    });


$("#departmentId").live("change",
    function (e) {
      if ($(e.target).attr("lastValue") == $(e.target).val()) {
        return;
      }
      $(this).attr("lastValue", $(this).val());
      $("#salesManId").find('option').not('#personFirstSelect').remove();
      if ($(this).val() == "") {
        $("#salesManId").attr('disabled', true);
      } else {
        $("#salesManId").attr('disabled', false);
        var departmentIdStr = $("#departmentId").val();
        APP_BCGOGO.Net.asyncGet({
          url: "businessAccount.do?method=getSalesManByDepartmentId",
          data: {
            "keyWord": "",
            "departmentIdStr": departmentIdStr,
            "now": new Date()
          },
          dataType: "json",
          success: function (result) {
            if (result && result.length > 0) {
              for (var i = 0; i < result.length; i++) {
                var option = "<option value='" + result[i].idStr + "'>" + result[i].name + "</option>";
                $(option).appendTo($("#salesManId"));
              }

            }
          }
        });
      }
    }).live("focus", function () {
      $(this).attr("lastValue", $(this).val());
    });


function askForAssistDropList(event,obj) {
       var keycode= event.which || event.keyCode;
       var droplist = APP_BCGOGO.Module.droplist;
       clearTimeout(droplist.delayTimerId || 1);
       droplist.delayTimerId = setTimeout(function () {
           var droplist = APP_BCGOGO.Module.droplist;
           // 我们dummy 一个数据集， 这个数据集即符合 droplist 的需要。
          var accountCategory=$(obj).val();
         var accountCategoryId = $("#accountCategoryId").val();
           var uuid = GLOBAL.Util.generateUUID();
           droplist.setUUID(uuid);
           APP_BCGOGO.Net.asyncGet({
               url: "businessAccount.do?method=getBusinessCategoryLikeItemName",
               data:{
                   "uuid":uuid,
                   "keyWord":accountCategory,
                   "now":new Date()
               },
               dataType:"json",
               success:function(result) {
                   if(null == result || null == result.data) return;
                   if(!G.isEmpty(result.data[0])){
                       G.completer({
                               'domObject':obj,
                               'keycode':keycode,
                               'title':result.data[0].label}
                       );
                   }
                   droplist.show({
                       "selector":$(event.currentTarget),
                       "isEditable":false,
                       "originalValue":{label:accountCategory,idStr:accountCategoryId},
                       "data":result,
                       "isDeletable":false,
                       "onSelect":function (event, index, data,hook) {
                           var id = data.idStr;
                           var name=data.itemName;

                           $(hook).val(name);

                           var moneyCategory = data.moneyCategory;

                           if (moneyCategory == "income") {
                             $("#moneyCategoryIncome").click();
                           } else if (moneyCategory == "expenses") {
                             $("#moneyCategoryExpenses").click();
                           }


                           droplist.hide();
                       },

                       "onKeyboardSelect":function(event, index, data, hook) {
                           // TODO;
                           $(hook).val(data.label);
                       }
                   });

               }
           });
       }, 200);
   }


function askForDeptDropList(event,obj) {
       var keycode= event.which || event.keyCode;
       var droplist = APP_BCGOGO.Module.droplist;
       clearTimeout(droplist.delayTimerId || 1);
       droplist.delayTimerId = setTimeout(function () {
           var droplist = APP_BCGOGO.Module.droplist;
           // 我们dummy 一个数据集， 这个数据集即符合 droplist 的需要。
           var department=$(obj).val();
           var departmentId = $("#departmentId").val();
           var uuid = GLOBAL.Util.generateUUID();
           droplist.setUUID(uuid);
           APP_BCGOGO.Net.asyncGet({
               url: "businessAccount.do?method=getDepartmentLikeName",
               data:{
                   "uuid":uuid,
                   "keyWord":department,
                   "now":new Date()
               },
               dataType:"json",
               success:function(result) {
                   if(null == result || null == result.data) return;
                   if(!G.isEmpty(result.data[0])){
                       G.completer({
                               'domObject':obj,
                               'keycode':keycode,
                               'title':result.data[0].label}
                       );
                   }
                   droplist.show({
                       "selector":$(event.currentTarget),
                       "isEditable":false,
                       "originalValue":{label:department,idStr:departmentId},
                       "data":result,
                       "isDeletable":false,
                       "onSelect":function (event, index, data,hook) {
                           var id = data.idStr;
                           var name=data.name;
                           $(hook).val(name);
                           droplist.hide();
                       },

                       "onKeyboardSelect":function(event, index, data, hook) {
                           // TODO;
                           $(hook).val(data.label);
                       }
                   });

               }
           });
       }, 200);
   }