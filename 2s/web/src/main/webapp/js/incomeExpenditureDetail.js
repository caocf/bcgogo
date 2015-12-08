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

//日收入记录


function initDayIncomeStat(jsonStr) {

  jsonStrMap.put("dayIncome", jsonStr);

  if(null == jsonStr || jsonStr[jsonStr.length - 1].totalRows == 0) {
      $("#printBtn1").hide();
  } else {
      $("#printBtn1").show();
  }

  $("#runningInfoDay tr:not(:first)").remove();
   /*好凌乱，加上标注啊亲 亲~~~~~~~~~~~~~~~~~~~~~~~~*/
  if(jsonStr != null && jsonStr.length > 13) {
    var length = jsonStr.length;
    for(var i = 0; i < length - 13; i++) {
      var orderTime = jsonStr[i].hasOwnProperty("receptionDateStr") ? jsonStr[i].receptionDateStr : "";
      var customer = jsonStr[i].hasOwnProperty("customerName") ? jsonStr[i].customerName : "";
      var vehicle = jsonStr[i].hasOwnProperty("vehicle") ? jsonStr[i].vehicle : "";
      var orderStatus = jsonStr[i].hasOwnProperty("orderStatusEnum") ? jsonStr[i].orderStatusEnum : "";
      var orderType = jsonStr[i].hasOwnProperty("orderType") ? jsonStr[i].orderType : "";
      var orderContent = jsonStr[i].hasOwnProperty("orderContent") ? jsonStr[i].orderContent : "";
      var orderTotal = jsonStr[i].hasOwnProperty("orderTotal") ? jsonStr[i].orderTotal : " ";
      var amount = jsonStr[i].hasOwnProperty("amount") ? jsonStr[i].amount : "";
      var shortOrderContent = jsonStr[i].hasOwnProperty("shortOrderContent") ? jsonStr[i].shortOrderContent : "";

      var cash = jsonStr[i].hasOwnProperty("cash") ? jsonStr[i].cash : "";
      var bankCard = jsonStr[i].hasOwnProperty("bankCard") ? jsonStr[i].bankCard : "";
      var cheque = jsonStr[i].hasOwnProperty("cheque") ? jsonStr[i].cheque : "";
      var coupon = jsonStr[i].hasOwnProperty("coupon") ? jsonStr[i].coupon : ""; //代金券 add by litao
      var customerDeposit = jsonStr[i].hasOwnProperty("deposit") ? jsonStr[i].deposit:""; // add by zhuj
      var memberBalancePay = jsonStr[i].hasOwnProperty("memberBalancePay") ? jsonStr[i].memberBalancePay : "";
      var remainDebt = jsonStr[i].hasOwnProperty("remainDebt") ? jsonStr[i].remainDebt : "";
      var discount = jsonStr[i].hasOwnProperty("discount") ? jsonStr[i].discount : "";

      var tr = '<tr class="table-row-original">';
      if(i <= 8){//No
        tr += '<td class="first-padding">' + '0' + (i + 1) + '</td>';
      }else{
        tr += '<td class="first-padding">' + (i + 1) + '</td>';
      }

      var orderId =  jsonStr[i].hasOwnProperty("orderIdStr") ? jsonStr[i].orderIdStr : " ";
      var idStr = "'" + orderId + "'";

      if(orderType == "施工单") {//日期
        tr += '<td title="' + orderTime + '">' + '<a href ="#" onclick="openWinRepair(' + idStr + ')">' + orderTime + '</a> ' + '</td> ';
      } else if(orderType == "销售单") {
        tr += '<td title="' + orderTime + '">' + '<a href ="#" onclick="openWinSale(' + idStr + ')">' + orderTime + '</a> ' + '</td> ';
      } else if(orderType == "洗车美容单") {
        tr += '<td title="' + orderTime + '">' + '<a href ="#" onclick="openWinWashBeauty(' + idStr + ')">' + orderTime + '</a> ' + '</td> ';
      } else if(orderType == "销售退货单") {
        tr += '<td title="' + orderTime + '">' + '<a href ="#" onclick="openSalesReturn(' + idStr + ')">' + orderTime + '</a> ' + '</td> ';
      }else if(orderType == "客户对账单") {
        tr += '<td title="' + orderTime + '">' + '<a href ="#" onclick="openStatementOrder(' + idStr + ')">' + orderTime + '</a> ' + '</td> ';
      }else {
        tr += '<td title="' + orderTime + '">' + orderTime + '</td>';
      }

      tr += '<td title="' + customer + '">' + customer + '</td> ';//客户
        if (APP_BCGOGO.Permission.Version.VehicleConstruction) {//车牌
            tr += '<td title="' + vehicle + '">' + vehicle + '</td> ';
        }

        if (orderStatus != "" && orderStatus.indexOf("REPEAL") != -1) {//类别
            tr += '<td title="' + orderType + '">' + orderType +'(作废)' + '</td> ';
        } else {
            tr += '<td title="' + orderType + '">' + orderType + '</td> ';
        }

      tr += '<td title="' + orderContent + '">' + shortOrderContent + '</td>';//内容
      var subLength = 10;
      tr += '<td title="' + orderTotal + '">' + orderTotal.toString().substring(0, subLength) + '</td>';//单据总额
      tr += '<td title="' + amount + '">' + amount.toString().substring(0, subLength) + '</td>';//实收
      tr += '<td title="' + cash + '">' + cash.toString().substring(0, subLength) + '</td>';//现金
      tr += '<td title="' + bankCard + '">' + bankCard.toString().substring(0, subLength) + '</td>';//银联
      tr += '<td title="' + cheque + '">' + cheque.toString().substring(0, subLength) + '</td>';//支票
      tr += '<td title="' + coupon + '">' + coupon.toString().substring(0, subLength) + '</td>';//代金券
      if(APP_BCGOGO.Permission.Version.MemberStoredValue){
        tr += '<td title="' + memberBalancePay + '">' + memberBalancePay.toString().substring(0, subLength) + '</td>';
      }
      if(APP_BCGOGO.Permission.Version.CustomerDeposit){ // add by zhuj   //会员储值
        tr += '<td title="' + customerDeposit + '">' + customerDeposit.toString().substring(0, subLength) + '</td>';
      }
      //优惠
      tr += '<td title="' + discount + '">' + discount.toString().substring(0, subLength) + '</td>';
      //欠款挂账
      tr += '<td title="' + remainDebt + '" class="last-padding">' + remainDebt.toString().substring(0, subLength) + '</td>';
      tr += '</tr>';
      $("#runningInfoDay").append(tr);
      tableUtil.tableStyle('#runningInfoDay', '.tab_title');
    }
    var tr = '<tr class="font_bold">'
    if(!APP_BCGOGO.Permission.Version.MemberStoredValue && !APP_BCGOGO.Permission.Version.CustomerDeposit) {
      tr += '<td style="border-left:none; text-align:right; " colspan="7">本页总计:</td>'
    } else if(APP_BCGOGO.Permission.Version.MemberStoredValue){
      tr += '<td style="border-left:none; text-align:right; " colspan="7">本页总计:</td>'
    }else if(APP_BCGOGO.Permission.Version.CustomerDeposit){
      tr += '<td style="border-left:none; text-align:right; " colspan="6">本页总计:</td>'
    } else{
      tr += '<td style="border-left:none; text-align:right; " colspan="5">本页总计:</td>'
    }
//      tr += '<td title="' + jsonStr[length - 12] + '">' + jsonStr[length - 12].toString().substring(0, subLength) + '</td>';
    tr += '<td title="' + jsonStr[length - 11] + '">' + jsonStr[length - 11].toString().substring(0, subLength) + '</td>';
    tr += '<td title="' + jsonStr[length - 10] + '">' + jsonStr[length - 10].toString().substring(0, subLength) + '</td> ';
    tr += '<td title="' + jsonStr[length - 9] + '">' + jsonStr[length - 9].toString().substring(0, subLength) + '</td> ';
    tr += '<td title="' + jsonStr[length - 8] + '">' + jsonStr[length - 8].toString().substring(0, subLength) + '</td> ';
    tr += '<td title="' + jsonStr[length - 13] + '">' + jsonStr[length - 13].toString().substring(0, subLength) + '</td> ';//new add 代金券
    if (APP_BCGOGO.Permission.Version.MemberStoredValue) {
       tr += '<td title="' + jsonStr[length - 7] + '">' + jsonStr[length - 7].toString().substring(0, subLength) + '</td> ';
    }
    if (APP_BCGOGO.Permission.Version.CustomerDeposit){ // add by zhuj
       tr += '<td title="' + jsonStr[length - 6] + '">' + jsonStr[length - 6].toString().substring(0, subLength) + '</td> ';
    }
    tr += '<td title="' + jsonStr[length - 5] + '">' + jsonStr[length - 5].toString().substring(0, subLength) + '</td>';
    tr += '<td title="' + jsonStr[length - 4] + '">' + jsonStr[length - 4].toString().substring(0, subLength) + '</td>';
    tr += '</tr>';
    $("#runningInfoDay").append(tr);

  }
}

function getDayIncomeDetail(yearHid, monthHid, dayHid) {
  $("#currentDayIncome").text(monthHid + "月" + dayHid + "日");
  $("#runningInfoDay tr:not(:first)").remove();
  $("#dayIncome").click();
  $("#selectDayHid").val(dayHid);
  $("#selectMonthHid").val(monthHid);
  $("#selectYearHid").val(yearHid);
  initIncomeRunningStatInfoDay(dayHid, monthHid, yearHid, "day", defaultArrayType);
  initRunningStatDate(yearHid, monthHid, dayHid, "incomeDay");
}

//月收入记录


function initMonthIncomeStat(jsonStr) {

  jsonStrMap.put("monthIncome", jsonStr);
  if(null == jsonStr || jsonStr.length <= 0 || jsonStr[jsonStr.length - 1].totalRows == 0) {
        $("#printBtn2").hide();
  } else {
        $("#printBtn2").show();
    }
  $("#runningInfoMonth tr:not(:first)").remove();
  if(jsonStr != null && jsonStr.length > 13) {
    var length = jsonStr.length;

    for(var i = 0; i < length - 13; i++) {

      var statYear = jsonStr[i].hasOwnProperty("statYear") ? jsonStr[i].statYear : 0;
      var statMonth = jsonStr[i].hasOwnProperty("statMonth") ? jsonStr[i].statMonth : 0;
      var statDay = jsonStr[i].hasOwnProperty("statDay") ? jsonStr[i].statDay : 0;
      var orderTime = jsonStr[i].hasOwnProperty("runningStatDateStr") ? jsonStr[i].runningStatDateStr : 0;
      var incomeSum = jsonStr[i].hasOwnProperty("incomeSum") ? jsonStr[i].incomeSum : 0;
      var cashIncome = jsonStr[i].hasOwnProperty("cashIncome") ? jsonStr[i].cashIncome : 0;
      var unionPayIncome = jsonStr[i].hasOwnProperty("unionPayIncome") ? jsonStr[i].unionPayIncome : 0;
      var chequeIncome = jsonStr[i].hasOwnProperty("chequeIncome") ? jsonStr[i].chequeIncome : 0;
      var couponIncome = jsonStr[i].hasOwnProperty("couponIncome") ? jsonStr[i].couponIncome : 0;//new add 代金券
      var memberIncome = jsonStr[i].hasOwnProperty("memberPayIncome") ? jsonStr[i].memberPayIncome : 0;
      var customerDepositExpenditure = jsonStr[i].hasOwnProperty("customerDepositExpenditure") ? jsonStr[i].customerDepositExpenditure : 0; // add by zhuj
      var debtNewIncome = jsonStr[i].hasOwnProperty("debtNewIncome") ? jsonStr[i].debtNewIncome : 0;
      var debtWithdrawalIncome = jsonStr[i].hasOwnProperty("debtWithdrawalIncome") ? jsonStr[i].debtWithdrawalIncome : 0;
      var tr = '<tr class="table-row-original">';
      if(i<=8){
        tr += '<td class="first-padding">' + '0' + (i + 1) + '</td>';
      }else{
        tr += '<td class="first-padding">' + (i + 1) + '</td>';
      }

      tr += '<td style="color:#0094FF;cursor:pointer;" ondblclick="getDayIncomeDetail(' + statYear + ',' + statMonth + ',' + statDay + ')">' + orderTime + '</td>';

      tr += '<td title="' + incomeSum + '">' + incomeSum + '</td>';
      tr += '<td title="' + cashIncome + '">' + cashIncome + '</td>';
      tr += '<td title="' + unionPayIncome + '">' + unionPayIncome + '</td>';
      tr += '<td title="' + chequeIncome + '">' + chequeIncome + '</td>';
      tr += '<td title="' + couponIncome + '">' + couponIncome + '</td>';//new add 代金券
      if(APP_BCGOGO.Permission.Version.CustomerDeposit){ // add by zhuj
          tr += '<td title="' + customerDepositExpenditure + '">' + customerDepositExpenditure + '</td>';
      }
      if(APP_BCGOGO.Permission.Version.MemberStoredValue){
        tr += '<td title="' + memberIncome + '">' + memberIncome + '</td>';
      }
      tr += '<td title="' + debtNewIncome + '">' + debtNewIncome + '</td>';
      tr += '<td title="' + debtWithdrawalIncome + '" class="last-padding">' + debtWithdrawalIncome + '</td>';
      tr += '</tr>';
      $("#runningInfoMonth").append(tr);
      tableUtil.tableStyle('#runningInfoMonth', '.tab_title');
    }


    var tr = '<tr class="font_bold">'

    tr += '<td style="border-left:none; text-align:right; " colspan="2">本页总计:</td>';


    tr += '<td title="' + jsonStr[length - 12] + '">' + jsonStr[length - 12] + '</td>';
    tr += '<td title="' + jsonStr[length - 10] + '">' + jsonStr[length - 10] + '</td> ';
    tr += '<td title="' + jsonStr[length - 9] + '">' + jsonStr[length - 9] + '</td> ';
    tr += '<td title="' + jsonStr[length - 8] + '">' + jsonStr[length - 8] + '</td> ';
    tr += '<td title="' + jsonStr[length - 13] + '">' + jsonStr[length - 13] + '</td> ';
    if(APP_BCGOGO.Permission.Version.MemberStoredValue){
        tr += '<td title="' + jsonStr[length - 7] + '">' + jsonStr[length - 7] + '</td>';
    }
    if(APP_BCGOGO.Permission.Version.CustomerDeposit){ // add by zhuj
       tr += '<td title="' + jsonStr[length - 6] + '">' + jsonStr[length - 6] + '</td> ';
    }
    tr += '<td title="' + jsonStr[length - 4] + '">' + jsonStr[length - 4] + '</td>';
    tr += '<td title="' + jsonStr[length - 5] + '">' + jsonStr[length - 5] + '</td>';
    tr += '</tr>';

    $("#runningInfoMonth").append(tr);

  }
}


function getMonthIncomeDetail(yearHid, monthHid, dayHid) {
  if(dayHid == 0) {
    dayHid = 1; //默认为每个月的第一天
  }
  $("#currentMonthIncome").text(yearHid + "年" + monthHid + "月");
  $("#runningInfoMonth tr:not(:first)").remove();
  $("#monthIncome").click();
  $("#selectYearHid").val(yearHid);
  $("#selectMonthHid").val(monthHid);
  var selectDate = new Date(yearHid, monthHid, 0);
  var selectDay = selectDate.getDate();
  $("#selectDayHid").val(selectDay);
  initIncomeRunningStatInfoMonth(dayHid, monthHid, yearHid, "day", defaultArrayType);
  initRunningStatDate(yearHid, monthHid, dayHid, "incomeMonth");
}

//初始化年收入记录


function initYearIncomeStat(jsonStr) {

  jsonStrMap.put("yearIncome", jsonStr);
  if(null == jsonStr || jsonStr.length <= 0 || jsonStr[jsonStr.length - 1].totalRows == 0) {
      $("#printBtn3").hide();
  } else {
      $("#printBtn3").show();
  }
  if(jsonStr != null && jsonStr.length > 13) {
    $("#runningInfoYear tr:not(:first)").remove();

    var length = jsonStr.length;

    for(var i = 0; i < length - 13; i++) {
      var statYear = jsonStr[i].hasOwnProperty("statYear") ? jsonStr[i].statYear : 2012;
      var statMonth = jsonStr[i].hasOwnProperty("statMonth") ? jsonStr[i].statMonth : 0;
      var statDay = jsonStr[i].hasOwnProperty("statDay") ? jsonStr[i].statDay : 0;
      var orderTime = jsonStr[i].hasOwnProperty("runningStatDateStr") ? jsonStr[i].runningStatDateStr : 0;
      var incomeSum = jsonStr[i].hasOwnProperty("incomeSum") ? jsonStr[i].incomeSum : 0;
      var cashIncome = jsonStr[i].hasOwnProperty("cashIncome") ? jsonStr[i].cashIncome : 0;
      var unionPayIncome = jsonStr[i].hasOwnProperty("unionPayIncome") ? jsonStr[i].unionPayIncome : 0;
      var chequeIncome = jsonStr[i].hasOwnProperty("chequeIncome") ? jsonStr[i].chequeIncome : 0;
      var couponIncome = jsonStr[i].hasOwnProperty("couponIncome") ? jsonStr[i].couponIncome : 0; //new added 代金券
      var customerDepositExpenditure = jsonStr[i].hasOwnProperty("customerDepositExpenditure") ? jsonStr[i].customerDepositExpenditure : 0; // add by zhuj
      var memberIncome = jsonStr[i].hasOwnProperty("memberPayIncome") ? jsonStr[i].memberPayIncome : 0;
      var debtNewIncome = jsonStr[i].hasOwnProperty("debtNewIncome") ? jsonStr[i].debtNewIncome : 0;
      var debtWithdrawalIncome = jsonStr[i].hasOwnProperty("debtWithdrawalIncome") ? jsonStr[i].debtWithdrawalIncome : 0;
      var tr = '<tr class="table-row-original">';
      if(i<=8){
        tr += '<td class="first-padding">' + '0' + (i + 1) + '</td>';
      }else{
        tr += '<td class="first-padding">' + (i + 1) + '</td>';
      }
      tr += '<td style="color:#0094FF;cursor:pointer;" ondblclick="getMonthIncomeDetail(' + statYear + ',' + statMonth + ',' + statDay + ')">' + orderTime + '</td>';
      tr += '<td title="' + incomeSum + '">' + incomeSum + '</td>';
      tr += '<td title="' + cashIncome + '">' + cashIncome + '</td>';
      tr += '<td title="' + unionPayIncome + '">' + unionPayIncome + '</td>';
      tr += '<td title="' + chequeIncome + '">' + chequeIncome + '</td>';
      tr += '<td title="' + couponIncome + '">' + couponIncome + '</td>';//new added
      if(APP_BCGOGO.Permission.Version.CustomerDeposit){ // add by zhuj  //用预收款
          tr += '<td title="' + customerDepositExpenditure + '">' + customerDepositExpenditure + '</td>';
      }
      if(APP_BCGOGO.Permission.Version.MemberStoredValue) {//会员储值
        tr += '<td title="' + memberIncome + '">' + memberIncome + '</td>';
      }
      tr += '<td title="' + debtNewIncome + '">' + debtNewIncome + '</td>';//新增欠款
      //欠款回笼
      tr += '<td title="' + debtWithdrawalIncome + '" class="last-padding">' + debtWithdrawalIncome + '</td>';
      tr += '</tr>';
      $("#runningInfoYear").append(tr);
      tableUtil.tableStyle('#runningInfoYear', '.tab_title');
    }


    var tr = '<tr class="font_bold">'

    tr += '<td style="border-left:none; text-align:right; " colspan="2">本页总计:</td>';
    tr += '<td title="' + jsonStr[length - 12] + '">' + jsonStr[length - 12] + '</td>';
    tr += '<td title="' + jsonStr[length - 10] + '">' + jsonStr[length - 10] + '</td> ';
    tr += '<td title="' + jsonStr[length - 9] + '">' + jsonStr[length - 9] + '</td> ';
    tr += '<td title="' + jsonStr[length - 8] + '">' + jsonStr[length - 8] + '</td> ';
    tr += '<td title="' + jsonStr[length - 13] + '">' + jsonStr[length - 13] + '</td> '; //new add 代金券
    if(APP_BCGOGO.Permission.Version.MemberStoredValue) {
        tr += '<td title="' + jsonStr[length - 7] + '">' + jsonStr[length - 7] + '</td>';
    }
    if(APP_BCGOGO.Permission.Version.CustomerDeposit) { // add by zhuj
        tr += '<td title="' + jsonStr[length - 6] + '">' + jsonStr[length - 6] + '</td>';
    }
    tr += '<td title="' + jsonStr[length - 4] + '">' + jsonStr[length - 4] + '</td>';
    tr += '<td title="' + jsonStr[length - 5] + '">' + jsonStr[length - 5] + '</td>';

    tr += '</tr>';

    $("#runningInfoYear").append(tr);

  }
}

//初始化日支出记录


function initDayExpenditureStat(jsonStr) {

  jsonStrMap.put("dayExpend", jsonStr);
  if(null == jsonStr || jsonStr.length <= 0 || jsonStr[jsonStr.length - 1].totalRows == 0) {
      $("#printBtn4").hide();
  } else {
      $("#printBtn4").show();
  }
  if(jsonStr != null && jsonStr.length > 12) {
    $("#expenditureInfoDay tr:not(:first)").remove();

    var length = jsonStr.length;

    for(var i = 0; i < length - 12; i++) {

      var orderTime = jsonStr[i].hasOwnProperty("paidTimeStr") ? jsonStr[i].paidTimeStr : " ";
      var orderType = jsonStr[i].hasOwnProperty("orderType") ? jsonStr[i].orderType : " ";
      var orderContent = jsonStr[i].hasOwnProperty("materialName") ? jsonStr[i].materialName : " ";
      var customer = jsonStr[i].hasOwnProperty("customerName") ? jsonStr[i].customerName : " ";
      var shortOrderContent = jsonStr[i].hasOwnProperty("shortMaterialName") ? jsonStr[i].shortMaterialName : " ";

      var orderTotal = jsonStr[i].hasOwnProperty("amount") ? jsonStr[i].amount : " ";
      var actuallyPaid = jsonStr[i].hasOwnProperty("actuallyPaid") ? jsonStr[i].actuallyPaid : " ";

      var cash = jsonStr[i].hasOwnProperty("cash") ? jsonStr[i].cash : " ";
      var bankCard = jsonStr[i].hasOwnProperty("bankCardAmount") ? jsonStr[i].bankCardAmount : " ";
      var cheque = jsonStr[i].hasOwnProperty("checkAmount") ? jsonStr[i].checkAmount : " ";
      //var coupon = jsonStr[i].hasOwnProperty("couponAmount") ? jsonStr[i].couponAmount : " ";
      var depositAmount = jsonStr[i].hasOwnProperty("depositAmount") ? jsonStr[i].depositAmount : " ";
      var discount = jsonStr[i].hasOwnProperty("deduction") ? jsonStr[i].deduction : " ";
      var debt = jsonStr[i].hasOwnProperty("creditAmount") ? jsonStr[i].creditAmount : " ";

      var tr = '<tr class="table-row-original">';
      if(i<=8){
        tr += '<td class="first-padding">' + '0' + (i + 1) + '</td>';
      }else{
        tr += '<td class="first-padding">' + (i + 1) + '</td>';
      }

      var orderId =  jsonStr[i].hasOwnProperty("purchaseInventoryIdStr") ? jsonStr[i].purchaseInventoryIdStr : " ";
      var idStr = "'" + orderId + "'";

      if(orderType == "入库单" || orderType == "供应商欠款结算单" || orderType == "入库作废单") {
        tr += '<td title="' + orderTime + '">' + '<a href ="#" style="color:#0094FF" onclick="openWinPurchaseInventory(' + idStr + ')">' + orderTime + '</a> ' + '</td> ';
      } else if(orderType == "入库退货单" || orderType == "入库退货单退现金" || orderType == "入库退货单退定金") {
        tr += '<td title="' + orderTime + '">' + '<a href ="#" style="color:#0094FF" onclick="openWinPurchaseReturn(' + idStr + ')">' + orderTime + '</a> ' + '</td> ';
      }else if(orderType == "供应商对账单") {
        tr += '<td title="' + orderTime + '">' + '<a href ="#" style="color:#0094FF" onclick="openStatementOrder(' + idStr + ')">' + orderTime + '</a> ' + '</td> ';
      } else {
      tr += '<td title="' + orderTime + '">' + orderTime + '</td>';
      }
      tr += '<td title="' + orderType + '">' + orderType + '</td> ';
      tr += '<td title="' + orderContent + '">' + shortOrderContent + '</td> ';
      tr += '<td title="' + customer + '">' + customer + '</td> ';

      tr += '<td title="' + orderTotal + '">' + orderTotal + '</td>';
      tr += '<td title="' + actuallyPaid + '">' + actuallyPaid + '</td>';
      tr += '<td title="' + cash + '">' + cash + '</td>';
      tr += '<td title="' + bankCard + '">' + bankCard + '</td>';
      tr += '<td title="' + cheque + '">' + cheque + '</td>';
      //tr += '<td title="' + coupon + '">' + coupon + '</td>';//new add 代金券
      tr += '<td title="' + discount + '">' + discount + '</td>';
      tr += '<td title="' + depositAmount + '">' + depositAmount + '</td>';
      tr += '<td title="' + debt + '">' + debt + '</td>';
      tr += '</tr>';
      $("#expenditureInfoDay").append(tr);
      tableUtil.tableStyle('#expenditureInfoDay', '.tab_title');
    }


    var tr = '<tr class="font_bold">'

    tr += '<td style="border-left:none; text-align:right; " colspan="6">本页总计:</td>'
    tr += '<td title="' + jsonStr[length - 10] + '">' + jsonStr[length - 10] + '</td>';
    tr += '<td title="' + jsonStr[length - 9] + '">' + jsonStr[length - 9] + '</td> ';
    tr += '<td title="' + jsonStr[length - 8] + '">' + jsonStr[length - 8] + '</td> ';
    tr += '<td title="' + jsonStr[length - 7] + '">' + jsonStr[length - 7] + '</td> ';
    //tr += '<td title="' + jsonStr[length - 13] + '">' + jsonStr[length - 13] + '</td> ';//new add 代金券
    tr += '<td title="' + jsonStr[length - 4] + '">' + jsonStr[length - 4] + '</td>';
    tr += '<td title="' + jsonStr[length - 5] + '">' + jsonStr[length - 5] + '</td>';
    tr += '<td title="' + jsonStr[length - 3] + '">' + jsonStr[length - 3] + '</td>';

    tr += '</tr>';

    $("#expenditureInfoDay").append(tr);

  }
}


function getMonthExpenditureDetail(yearHid, monthHid, dayHid) {
  $("#currentDayExpenditure").text(monthHid + "月" + dayHid + "日");
  $("#expenditureInfoDay tr:not(:first)").remove();
  $("#dayExpenditure").click();
  $("#selectDayHid").val(dayHid);
  $("#selectMonthHid").val(monthHid);
  $("#selectYearHid").val(yearHid);
  initExpenditureRunningStatInfoDay(dayHid, monthHid, yearHid, "day", defaultArrayType);
  initRunningStatDate(yearHid, monthHid, dayHid, "expenditureDay");
}

//初始化月支出记录


function initMonthExpenditureStat(jsonStr) {

  jsonStrMap.put("monthExpend", jsonStr);

  if(null == jsonStr || jsonStr.length <= 0 || jsonStr[jsonStr.length - 1].totalRows == 0) {
      $("#printBtn5").hide();
  } else {
      $("#printBtn5").show();
  }
  $("#expenditureInfoMonth tr:not(:first)").remove();
  if(jsonStr != null && jsonStr.length > 13) {
    var length = jsonStr.length;

    for(var i = 0; i < length - 13; i++) {

      var statYear = jsonStr[i].hasOwnProperty("statYear") ? jsonStr[i].statYear : 0;
      var statMonth = jsonStr[i].hasOwnProperty("statMonth") ? jsonStr[i].statMonth : 0;
      var statDay = jsonStr[i].hasOwnProperty("statDay") ? jsonStr[i].statDay : 0;

      var orderTime = jsonStr[i].hasOwnProperty("runningStatDateStr") ? jsonStr[i].runningStatDateStr : 0;
      var expenditureSum = jsonStr[i].hasOwnProperty("expenditureSum") ? jsonStr[i].expenditureSum : 0;
      var cashExpenditure = jsonStr[i].hasOwnProperty("cashExpenditure") ? jsonStr[i].cashExpenditure : 0;
      var unionPayExpenditure = jsonStr[i].hasOwnProperty("unionPayExpenditure") ? jsonStr[i].unionPayExpenditure : 0;
      var chequeExpenditure = jsonStr[i].hasOwnProperty("chequeExpenditure") ? jsonStr[i].chequeExpenditure : 0;
      //var couponExpenditure = jsonStr[i].hasOwnProperty("couponExpenditure") ? jsonStr[i].couponExpenditure : 0;
      var depositPayExpenditure = jsonStr[i].hasOwnProperty("depositPayExpenditure") ? jsonStr[i].depositPayExpenditure : 0;
      var debtNewExpenditure = jsonStr[i].hasOwnProperty("debtNewExpenditure") ? jsonStr[i].debtNewExpenditure : 0;
      var debtWithdrawalExpenditure = jsonStr[i].hasOwnProperty("debtWithdrawalExpenditure") ? jsonStr[i].debtWithdrawalExpenditure : 0;
      var tr = '<tr class="table-row-original">';
      if(i<=8){
        tr += '<td class="first-padding">' + '0' + (i + 1) + '</td>';
      }else{
        tr += '<td class="first-padding">' + (i + 1) + '</td>';
      }

      tr += '<td style="color:#0094FF;cursor:pointer;" ondblclick="getMonthExpenditureDetail(' + statYear + ',' + statMonth + ',' + statDay + ')">' + orderTime + '</td>';
      tr += '<td title="' + expenditureSum + '">' + expenditureSum + '</td>';
      tr += '<td title="' + cashExpenditure + '">' + cashExpenditure + '</td>';
      tr += '<td title="' + unionPayExpenditure + '">' + unionPayExpenditure + '</td>';
      tr += '<td title="' + chequeExpenditure + '">' + chequeExpenditure + '</td>';
      //tr += '<td title="' + couponExpenditure + '">' + couponExpenditure + '</td>';
      tr += '<td title="' + depositPayExpenditure + '">' + depositPayExpenditure + '</td>';
      tr += '<td title="' + debtNewExpenditure + '">' + debtNewExpenditure + '</td>';
      tr += '<td title="' + debtWithdrawalExpenditure + '">' + debtWithdrawalExpenditure + '</td>';
      tr += '</tr>';
      $("#expenditureInfoMonth").append(tr);
      tableUtil.tableStyle('#expenditureInfoMonth', '.tab_title');
    }


    var tr = '<tr class="font_bold">'

    tr += '<td style="border-left:none; text-align:right;"  colspan="2">本页总计:</td>'
    tr += '<td title="' + jsonStr[length - 12] + '">' + jsonStr[length - 12] + '</td>';
    tr += '<td title="' + jsonStr[length - 10] + '">' + jsonStr[length - 10] + '</td> ';
    tr += '<td title="' + jsonStr[length - 9] + '">' + jsonStr[length - 9] + '</td>';
    tr += '<td title="' + jsonStr[length - 8] + '">' + jsonStr[length - 8] + '</td>';
    //tr += '<td title="' + jsonStr[length - 13] + '">' + jsonStr[length - 13] + '</td>';
    tr += '<td title="' + jsonStr[length - 6] + '">' + jsonStr[length - 6] + '</td>';
    tr += '<td title="' + jsonStr[length - 4] + '">' + jsonStr[length - 4] + '</td>';
    tr += '<td title="' + jsonStr[length - 5] + '">' + jsonStr[length - 5] + '</td>';
    tr += '</tr>';

    $("#expenditureInfoMonth").append(tr);

  }
}


function getYearExpenditureDetail(yearHid, monthHid, dayHid) {
  if(dayHid == 0) {
    dayHid = 1; //默认为每个月的第一天
  }
  $("#currentMonthExpenditure").text(yearHid + "年" + monthHid + "月");
  $("#monthExpenditure").click();
  $("#expenditureInfoMonth tr:not(:first)").remove();
  $("#selectYearHid").val(yearHid);
  $("#selectMonthHid").val(monthHid);
  var selectDate = new Date(yearHid, monthHid, 0);
  var selectDay = selectDate.getDate();
  $("#selectDayHid").val(selectDay);
  initExpenditureRunningStatInfoMonth(dayHid, monthHid, yearHid, "day", defaultArrayType);
  initRunningStatDate(yearHid, monthHid, dayHid, "expenditureMonth");
}

//初始化年支出记录


function initYearExpenditureStat(jsonStr) {

  jsonStrMap.put("yearExpend", jsonStr);
  if(null == jsonStr || jsonStr.length <= 0 || jsonStr[jsonStr.length - 1].totalRows == 0) {
      $("#printBtn6").hide();
  } else {
      $("#printBtn6").show();
  }
  if(jsonStr != null && jsonStr.length > 13) {
    $("#expenditureInfoYear tr:not(:first)").remove();

    var length = jsonStr.length;

    for(var i = 0; i < length - 13; i++) {

      var statYear = jsonStr[i].hasOwnProperty("statYear") ? jsonStr[i].statYear : 0;
      var statMonth = jsonStr[i].hasOwnProperty("statMonth") ? jsonStr[i].statMonth : 0;
      var statDay = jsonStr[i].hasOwnProperty("statDay") ? jsonStr[i].statDay : 0;


      var orderTime = jsonStr[i].hasOwnProperty("runningStatDateStr") ? jsonStr[i].runningStatDateStr : 0;
      var expenditureSum = jsonStr[i].hasOwnProperty("expenditureSum") ? jsonStr[i].expenditureSum : 0;
      var cashExpenditure = jsonStr[i].hasOwnProperty("cashExpenditure") ? jsonStr[i].cashExpenditure : 0;
      var unionPayExpenditure = jsonStr[i].hasOwnProperty("unionPayExpenditure") ? jsonStr[i].unionPayExpenditure : 0;
      var chequeExpenditure = jsonStr[i].hasOwnProperty("chequeExpenditure") ? jsonStr[i].chequeExpenditure : 0;
      //var couponExpenditure = jsonStr[i].hasOwnProperty("couponExpenditure") ? jsonStr[i].couponExpenditure : 0;
      var depositPayExpenditure = jsonStr[i].hasOwnProperty("depositPayExpenditure") ? jsonStr[i].depositPayExpenditure : 0;
      var debtNewExpenditure = jsonStr[i].hasOwnProperty("debtNewExpenditure") ? jsonStr[i].debtNewExpenditure : 0;
      var debtWithdrawalExpenditure = jsonStr[i].hasOwnProperty("debtWithdrawalExpenditure") ? jsonStr[i].debtWithdrawalExpenditure : 0;

      var tr = '<tr class="table-row-original">';
      if(i<=8){
        tr += '<td class="first-padding">' + '0' + (i + 1) + '</td>';
      }else{
        tr += '<td class="first-padding">' + (i + 1) + '</td>';
      }

      tr += '<td style="color:#0094FF;cursor:pointer;" ondblclick="getYearExpenditureDetail(' + statYear + ',' + statMonth + ',' + statDay + ')">' + orderTime + '</td>';
      tr += '<td title="' + expenditureSum + '">' + expenditureSum + '</td>';
      tr += '<td title="' + cashExpenditure + '">' + cashExpenditure + '</td>';
      tr += '<td title="' + unionPayExpenditure + '">' + unionPayExpenditure + '</td>';
      tr += '<td title="' + chequeExpenditure + '">' + chequeExpenditure + '</td>';
      //tr += '<td title="' + couponExpenditure + '">' + couponExpenditure + '</td>';
      tr += '<td title="' + depositPayExpenditure + '">' + depositPayExpenditure + '</td>';
      tr += '<td title="' + debtNewExpenditure + '">' + debtNewExpenditure + '</td>';
      tr += '<td title="' + debtWithdrawalExpenditure + '">' + debtWithdrawalExpenditure + '</td>';
      tr += '</tr>';
      $("#expenditureInfoYear").append(tr);
      tableUtil.tableStyle('#expenditureInfoYear', '.tab_title');
    }

    var tr = '<tr class="font_bold">'

    tr += '<td style="border-left:none; text-align:right; " colspan="2" >本页总计:</td>'
    tr += '<td title="' + jsonStr[length - 12] + '">' + jsonStr[length - 12] + '</td>';
    tr += '<td title="' + jsonStr[length - 10] + '">' + jsonStr[length - 10] + '</td> ';
    tr += '<td title="' + jsonStr[length - 9] + '">' + jsonStr[length - 9] + '</td> ';
    tr += '<td title="' + jsonStr[length - 8] + '">' + jsonStr[length - 8] + '</td> ';
    //tr += '<td title="' + jsonStr[length - 13] + '">' + jsonStr[length - 13] + '</td> ';
    tr += '<td title="' + jsonStr[length - 6] + '">' + jsonStr[length - 6] + '</td>';
    tr += '<td title="' + jsonStr[length - 4] + '">' + jsonStr[length - 4] + '</td>';
    tr += '<td title="' + jsonStr[length - 5] + '">' + jsonStr[length - 5] + '</td>';
    tr += '</tr>';
    $("#expenditureInfoYear").append(tr);
  }
}

function changeRunningDate(year, month, day) {
  $("#dayHid").val(day);
  $("#monthHid").val(month);
  $("#yearHid").val(year);
  $("#day").text(day + "日");
  $("#month").text(month + "月");
  $("#year").text(year + "年");

}


function openWinRepair(orderId) {
    window.open('txn.do?method=getRepairOrder&menu-uid=VEHICLE_CONSTRUCTION_REPAIR&repairOrderId=' + orderId);
}

function openWinSale(orderId) {
    window.open('sale.do?method=getSalesOrder&menu-uid=WEB.TXN.SALE_MANAGE.SALE&salesOrderId=' + orderId);
}

function openWinWashBeauty(orderId) {
    window.open('washBeauty.do?method=getWashBeautyOrder&washBeautyOrderId=' + orderId);
}

function openWinPurchaseInventory(orderId) {
    window.open('storage.do?method=getPurchaseInventory&purchaseInventoryId=' + orderId  + "&type=txn&menu-uid=WEB.TXN.PURCHASE_MANAGE.STORAGE");
}

function openWinPurchaseReturn(orderId) {
    window.open('goodsReturn.do?method=showReturnStorageByPurchaseReturnId&purchaseReturnId=' + orderId);
}

function openSalesReturn(orderId) {
    window.open('salesReturn.do?method=showSalesReturnOrderBySalesReturnOrderId&salesReturnOrderId=' + orderId);
}

$().ready(function() {
    $("#printBtn1,#printBtn2,#printBtn3,#printBtn4,#printBtn5,#printBtn6").bind("click", function () {

        var runningType = $(this).attr("runningType");

        var url = "runningStat.do?method=getDataToPrint";

        var dataList = "[";

        var total = 0;
        var pageTotal = 0;
        var settleTotal = 0;
        var cash = 0;
        var bank = 0;
        var check = 0;
        var memberAmount = 0;
        var debt = 0;
        var debtSteam = 0;
        var jsonObj = null;
        var discount = 0;
        var deposit = 0;
        var coupon = 0;
        if ("dayIncome" == runningType) {
            jsonObj = jsonStrMap.get("dayIncome");
            for (var i = 0; i < jsonObj.length - 13; i++) {
                if (null == jsonObj[i].afterMemberDiscountTotal) {
                    jsonObj[i].afterMemberDiscountTotal = jsonObj[i].orderTotal;
                }
                if ("[" == dataList) {
                    dataList += JSON.stringify(jsonObj[i]);
                } else {
                    dataList += "," + JSON.stringify(jsonObj[i]);
                }
            }
            total = $("#dayIncomeTotal").html();
            coupon = jsonObj[jsonObj.length - 13];  //add by litao
            pageTotal = jsonObj[jsonObj.length - 12];
            settleTotal = jsonObj[jsonObj.length - 11];
            cash = jsonObj[jsonObj.length - 10];
            bank = jsonObj[jsonObj.length - 9];
            check = jsonObj[jsonObj.length - 8];
            if (APP_BCGOGO.Permission.Version.MemberStoredValue){
                memberAmount = jsonObj[jsonObj.length - 7];
            }
            if (APP_BCGOGO.Permission.Version.CustomerDeposit){
                memberAmount = jsonObj[jsonObj.length - 6];
            }

            debt = jsonObj[jsonObj.length - 4];
        }
        else if ("monthIncome" == runningType) {
            jsonObj = jsonStrMap.get("monthIncome");
            for (var i = 0; i < jsonObj.length - 13; i++) {
                if ("[" == dataList) {
                    dataList += JSON.stringify(jsonObj[i]);
                } else {
                    dataList += "," + JSON.stringify(jsonObj[i]);
                }
            }

            total = $("#monthIncomeTotal").html();
            coupon = jsonObj[jsonObj.length - 13];  //add by litao
            pageTotal = jsonObj[jsonObj.length - 12];
            cash = jsonObj[jsonObj.length - 10];
            bank = jsonObj[jsonObj.length - 9];
            check = jsonObj[jsonObj.length - 8];
            if (APP_BCGOGO.Permission.Version.MemberStoredValue){
                memberAmount = jsonObj[jsonObj.length - 7];
            }
            if (APP_BCGOGO.Permission.Version.CustomerDeposit){
                memberAmount = jsonObj[jsonObj.length - 6];
            }
            debt = jsonObj[jsonObj.length - 4];
            debtSteam = jsonObj[jsonObj.length - 5];
        }
        else if ("yearIncome" == runningType) {

            jsonObj = jsonStrMap.get("yearIncome");
            for (var i = 0; i < jsonObj.length - 13; i++) {
                if ("[" == dataList) {
                    dataList += JSON.stringify(jsonObj[i]);
                } else {
                    dataList += "," + JSON.stringify(jsonObj[i]);
                }
            }

            total = $("#yearIncomeTotal").html();
            coupon = jsonObj[jsonObj.length - 13];  //add by litao
            pageTotal = jsonObj[jsonObj.length - 12];
            cash = jsonObj[jsonObj.length - 10];
            bank = jsonObj[jsonObj.length - 9];
            check = jsonObj[jsonObj.length - 8];
            if (APP_BCGOGO.Permission.Version.MemberStoredValue){
                memberAmount = jsonObj[jsonObj.length - 7];
            }
            if (APP_BCGOGO.Permission.Version.CustomerDeposit){
                memberAmount = jsonObj[jsonObj.length - 6];
            }
            debt = jsonObj[jsonObj.length - 4];
            debtSteam = jsonObj[jsonObj.length - 5];
        }
        else if ("dayExpend" == runningType) {
            jsonObj = jsonStrMap.get("dayExpend");
            for (var i = 0; i < jsonObj.length - 13; i++) {
                if ("[" == dataList) {
                    dataList += JSON.stringify(jsonObj[i]);
                } else {
                    dataList += "," + JSON.stringify(jsonObj[i]);
                }
            }

            total = $("#dayExpenditureTotal").html();
            //coupon = jsonObj[jsonObj.length - 13];  //add by litao
            pageTotal = jsonObj[jsonObj.length - 11];
            settleTotal = jsonObj[jsonObj.length - 10];
            cash = jsonObj[jsonObj.length - 9];
            bank = jsonObj[jsonObj.length - 8];
            check = jsonObj[jsonObj.length - 7];
            discount = jsonObj[jsonObj.length - 4];
            deposit = jsonObj[jsonObj.length - 5];
            debt = jsonObj[jsonObj.length - 3];
        }
        else if ("monthExpend" == runningType) {
            jsonObj = jsonStrMap.get("monthExpend");
            for (var i = 0; i < jsonObj.length - 13; i++) {
                if ("[" == dataList) {
                    dataList += JSON.stringify(jsonObj[i]);
                } else {
                    dataList += "," + JSON.stringify(jsonObj[i]);
                }
            }

            total = $("#monthExpenditureTotal").html();
            //coupon = jsonObj[jsonObj.length - 13];  //add by litao
            pageTotal = jsonObj[jsonObj.length - 12];
            cash = jsonObj[jsonObj.length - 10];
            bank = jsonObj[jsonObj.length - 9];
            check = jsonObj[jsonObj.length - 8];
            deposit = jsonObj[jsonObj.length - 6];
            debt = jsonObj[jsonObj.length - 4];
            debtSteam = jsonObj[jsonObj.length - 5];
        }
        else if ("yearExpend" == runningType) {
            jsonObj = jsonStrMap.get("yearExpend");
            for (var i = 0; i < jsonObj.length - 13; i++) {
                if ("[" == dataList) {
                    dataList += JSON.stringify(jsonObj[i]);
                } else {
                    dataList += "," + JSON.stringify(jsonObj[i]);
                }
            }

            total = $("#yearExpenditureTotal").html();
            //coupon = jsonObj[jsonObj.length - 13];  //add by litao
            pageTotal = jsonObj[jsonObj.length - 12];
            cash = jsonObj[jsonObj.length - 10];
            bank = jsonObj[jsonObj.length - 9];
            check = jsonObj[jsonObj.length - 8];
            deposit = jsonObj[jsonObj.length - 6];
            debt = jsonObj[jsonObj.length - 4];
            debtSteam = jsonObj[jsonObj.length - 5];
        }

        dataList += "]";

        var startDateStr = jsonObj[jsonObj.length - 1].startDateStr;
        var endDateStr = jsonObj[jsonObj.length - 1].endDateStr;
        var data = {
            now: new Date(),
            runningType: runningType,
            dataList: dataList,
            total: total,
            pageTotal: pageTotal,
            settleTotal: settleTotal,
            cash: cash,
            bank: bank,
            check: check,
            coupon: coupon, //add by litao
            memberAmount: memberAmount,
            debt: debt,
            debtSteam: debtSteam,
            discount: discount,
            deposit: deposit,
            startDateStr: startDateStr,
            endDateStr: endDateStr
        }

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
});