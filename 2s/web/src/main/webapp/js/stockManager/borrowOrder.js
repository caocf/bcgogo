var bcgogoAjaxQuery = APP_BCGOGO.Module.wjl.ajaxQuery;
var invoiceCommon = APP_BCGOGO.Module.wjl.invoiceCommon;

//borrowOrderList
$().ready(function(){
    $("#saveBorrowOrder").hover(function(){
        $(".sure").css("color","#fd5300");
    },function(){
        $(".sure").css("color","#000000");
    });
    $("#toBorrowOrderList").hover(function(){
        $(".return").css("color","#fd5300");
    },function(){
        $(".return").css("color","#000000");
    });
    $("#startTimeInput,#endTimeInput").datetimepicker({
        "numberOfMonths": 1,
        "showButtonPanel": true,
        "changeYear": true,
        "changeMonth": true,
        "yearRange":"c-100:c+100",
        "showHour":true,
        "showMinute":true,
        "dateFormat":"yy-mm-dd",
        "yearSuffix": "",
        "onSelect": function(dateText, inst) {
            var lastValue = $(this).attr("lastValue");
            if(lastValue == dateText) {
                return;
            }
            if(dateText) {
                $(this).attr("lastValue", dateText);
            }
        }
    });

    $(".lineTitle span").click(function(){
        if($(this).text()*1==0) return;
        var returnStatus=$(this).attr("returnStatus");
        $("#returnStatus option[value='"+returnStatus+"']").attr("selected", true);
        $("#searchBtn").click();
    });

    $(".borrower").live("click focus keyup", function (event) {
        event = event || event.which;
        var keyCode = event.keyCode;
        if (keyCode == 37 || keyCode == 38 || keyCode == 39 || keyCode == 40) {
            return;
        }
        droplistLite.show({
            event: event,
            id: "idStr",
            name: "name",
            data: "borrow.do?method=getBorrowerList"
        });
    });

    $("#contact")
        .bind('click focus', function (e) {
            e.stopImmediatePropagation();//可以阻止掉同一事件的其他优先级较低的侦听器的处理
            if (!GLOBAL.Lang.isEmpty($("#customerOrSupplierId").val())) {
            getContactListByIdAndType($("#customerOrSupplierId").val(), $("#csType").val(), $(this)); //@see js/contact.js
            }
        })
        .bind('keyup', function (event) {
            var eventKeyCode = event.which || event.keyCode;
            if (GLOBAL.Interactive.keyNameFromKeyCode(eventKeyCode).search(/left|up|right|down/g) == -1) {
                //getContactListByIdAndType($("#customerOrSupplierId").val(), $("#csType").val(), $(this), eventKeyCode); //@see js/contact.js
            }
        });

    // add by zhuj 绑定customerName blur事件 替代change事件
    //blur 事件延时处理，select 下拉之后不触发blur事件
    $("#customer").live("blur",function(e){
        var $customer  =  $(this);
        setTimeout(function () {
            if($customer.attr("blurLock")){
                $customer.removeAttr("blurLock");
                return;
            }
            if($customer.val() == ''){
                clearCustomerInputs();
            }
            if($customer.val() == $customer.attr("lastValue")){
                return;
            }
            clearCustomerInputs();
            $("#mobile").removeAttr("mustInputBySameCustomer");
            var name = $customer.val();
            var jsonCustomers = getCustomerByName(name);
            if (!G.isEmpty(jsonCustomers) && !G.isEmpty(jsonCustomers.results)) {
                // 渲染tip页面
                var contactList = new Array();
                for (var customerIndex in jsonCustomers.results) {
                    var customerId = jsonCustomers.results[customerIndex].idStr;
                    var contacts = jsonCustomers.results[customerIndex].contacts;
                    if (!G.isEmpty(contacts)) {
                        contacts = filterNullObjInArray(contacts);
                    }

                    if (G.isEmpty(contacts)) {
                        var customer = {
                            customerId: customerId,
                            contactId: "",
                            contact: "",
                            mobile: ""
                        };
                        contactList.push(customer);
                    } else {
                        for (var contactIndex in contacts) {
                            if (contacts[contactIndex] && !G.Lang.isEmpty(contacts[contactIndex].idStr)) {
                                var customer = {
                                    customerId: customerId,
                                    contactId: contacts[contactIndex].idStr,
                                    contact: contacts[contactIndex].name,
                                    mobile: contacts[contactIndex].mobile
                                };
                                contactList.push(customer);
                            }
                        }
                    }

                }
                if (!G.isEmpty(contactList)) {
                    for (var contactIndex in contactList) {
                        var $sin_contact = $('<div class="sin_contact"><label class="rad"><input type="radio" name="sin_contact"/>'
                            + name +
                            '</label>&nbsp;&nbsp;<span>'
                            + G.normalize(contactList[contactIndex].contact, "")
                            + '</span>&nbsp;&nbsp;<span>'
                            + G.normalize(contactList[contactIndex].mobile, "")
                            + '</span><br>'
                            + '<input type="hidden" class="contactName" value="' + G.normalize(contactList[contactIndex].contact, "") + '"/>'
                            + '<input type="hidden" class="contactMobile" value="' + G.normalize(contactList[contactIndex].mobile, "") + '"/>'
                            + '<input type="hidden" class="contactId" value="' + G.normalize(contactList[contactIndex].contactId, "") + '"/>'
                            + '<input type="hidden" class="customerId" value="' + G.normalize(contactList[contactIndex].customerId, "") + '"/>'
                            + '</div>');
                        $sin_contact.insertAfter($("#oldCustomers > div:last"));
                    }
                }

                $("#nameDupTip").dialog({
                    resizable: false,
                    height:200,
                    width:400,
                    title: "友情提示",
                    modal: true,
                    closeOnEscape: false,
                    close: function () {
                        $(".sin_contact").remove();
                        $("#nameDupTip input[type=radio]").attr("checked", "");
                        $("#oldCustomers").hide();
                        $("#cusDupTip").show();
                    },
                    open: function (event, ui) {
                        $(event.target).parent().find(".ui-dialog-titlebar-close").hide();
                    }
                });
            }
        }, 200);
    }).live("focus",function(){
            $(this).attr("lastValue",$(this).val());
        });

    $("#newCustomer").click(function(){
        //$("#nameDupTip").dialog("close");
        $("#isAdd").val("true");
        $("#customerId").val(""); // 后台默认以这个值为判断是否为新增的依据
    });

    $("#oldCustomer").click(function(){
        $("#oldCustomers").show();
        $("#cusDupTip").hide();
        $("#isAdd").val("false");
        $("#nameDupTip .J_return").show();
    });

    $(".sin_contact").live("click", function () {
        $("#contact").val($(this).find("input[class=contactName]").val());
        $("#mobile").val($(this).find("input[class=contactMobile]").val());
        $("#contactId").val($(this).find("input[class=contactId]").val());
        $("#customerId").val($(this).find("input[class=customerId]").val());
        $("#customerOrSupplierId").val($(this).find("input[class=customerId]").val());
        if($("#contactId").val()){
            contactDeal(true); // @see suggestion.js
        }else{
            contactDeal(false); // @see suggestion.js
        }

    });

    $("#nameDupTip .J_btnSure").click(function(){
        if ($("#cusDupTip").css("display") == "block") {
            if (!$("#newCustomer").attr("checked") && !$("#oldCustomer").attr("checked")) {
                nsDialog.jAlert("请选择新客户或者老客户！");
                return;
            }else{
                $("#mobile").attr("mustInputBySameCustomer",true);
            }
        }
        if ($("#oldCustomers").css("display") == "block") {
            var oldChecked = false;
            $("#oldCustomers input[name='sin_contact']").each(function () {
                if ($(this).attr("checked")) {
                    oldChecked = true;
                }
            });
            if (!oldChecked) {
                nsDialog.jAlert("请在客户列表中选择！");
                return;
            }else{
               checkCustomerByIdAndContactId($("#customerOrSupplierId").val(),$("#contactId").val());
            }
        }
        if ($("#newCustomer").attr("checked")) {
            $("#isAdd").val("true");
            $("#customerId").val(""); // 后台默认以这个值为判断是否为新增的依据
        }
        $(".sin_contact").remove();
        $("#nameDupTip").dialog("close");
    });

    $("#nameDupTip .J_return").bind("click",function(){
        $("#oldCustomers").find("[name='sin_contact']").each(function(){
            $(this).attr("checked","");
        });
        $("#mobile").removeAttr("mustInputBySameCustomer");
        $("#oldCustomers").hide();
        $("#cusDupTip").find("[type='radio']").each(function () {
            $(this).attr("checked","");
        });
        $("#cusDupTip").show();
        $(this).hide();
        $("#contact").val("");
        $("#mobile").val("");
        $("#contactId").val("");
        $("#customerId").val("");
        $("#customerOrSupplierId").val("");
        contactDeal(false);
    });

    //复制逻辑之后要校验多联系人
    if($("#customerId").val() && $("#contactId").val()){
        contactDeal(true);
    }else{
        contactDeal(false);
    }

    $("#borrowerDetail").click(function(){
        if($("#csType").val() == 'customer') {
            bcgogo.checksession({"parentWindow":window.parent,'iframe_PopupBox':$("#iframe_PopupBox")[0],'src':"txn.do?method=clientInfo&customer="
                + encodeURIComponent($("#customer").val()) + "&mobile=" + $("#mobile").val() + "&landLine=" + $("#landline").val() + "&hiddenMobile=" + $("#hiddenMobile").val()
                + "&customerId=" + $("#customerOrSupplierId").val() + "&contact=" + encodeURIComponent($("#contact").val())});
        } else if($("#csType").val() == 'supplier') {
            bcgogo.checksession({"parentWindow": window.parent, 'iframe_PopupBox': $("#iframe_PopupBox")[0], 'src': "txn.do?method=orderSupplierInfo&supplier="
                + encodeURIComponent($("#supplier").val()) + "&mobile=" + $("#mobile").val() + "&landLine=" + $("#landline").val() + "&hiddenMobile=" + $("#hiddenMobile").val()
                + "&supplierId=" + $("#customerOrSupplierId").val() + "&contact=" + encodeURIComponent($("#contact").val())});
        }
    });

});

function clearCustomerInputs(){
    $("#customerId, #contactId, #contact, #mobile, #hiddenMobile, #landline,#customerOrSupplierId,#abbr," +
        "#email,#qq,#fax,#address,#bank,#accountName,#account").val("");
    $("#receivable, #payable").text("0");
    $("#mobile").removeAttr("mustInputBySameCustomer");
}


//addBorrowOrder init function
$().ready(function(){


    $(document).bind("click", function(event) {
//        var selectorArray = [
//           $(".customerOrSupplierName"),
//            $("#storehouseId")
//            ];
//       if($(event.target).closest(selectorArray).length != 0){
//           return;
//       }
        var target = event.target;

        if(target === document.lastChild) return;

        if (!$(target).hasClass("customerOrSupplierName")&&stringUtil.isEmpty($(".customerOrSupplierName").val())){
            $(".borrowerDetail input").not(".vestDateStr ").val("");
            $(".borrowerDetail select").not("#storehouseId").val("");
            $("#customerOrSupplierId,#contactId").val("");
            $("#contact, #mobile").removeAttr("readonly");
        }
    });

    $(".J-customerOrSupplierSuggestion")
        .bind('click', function () {
            customerOrSupplierSuggestion($(this));
        })
        .bind('keyup', function (event) {
            var eventKeyCode = event.which || event.keyCode;
            if (GLOBAL.Interactive.keyNameFromKeyCode(eventKeyCode).search(/left|up|right|down/g) == -1) {
                customerOrSupplierSuggestion($(this));
            }
        });

    //选择borrower类型，确定下拉建议
    $("[name='borrower_type_select']").click(function() {
        $(".borrowerDetail input").not(".vestDateStr ").val("");
        $(".borrowerDetail select").val("");
        $("#customerOrSupplierId").val("");
        if($(this).attr("borrowerType")=="customer"){
            $(".customerOrSupplierName").attr("id","customer");
            $("#csType").val("customer");
        }else if($(this).attr("borrowerType")=="supplier"){
            $(".customerOrSupplierName").attr("id","supplier");
            $("#csType").val("supplier");
        }
    });
    //删除行  //TODO 点击减号所做的删除改行的操作
    $(document).bind('click', function (event) {
        var target = event.target;
        if (target.className != "btnMinus") return;
        if ($("#id").val()) return false;

        var iPrefixId = $(target).attr("id");
        iPrefixId = iPrefixId.substring(0, iPrefixId.indexOf("."));
        $("#"+iPrefixId+"_supplierInfo").remove();
        $(target).closest("tr").remove();
        isShowAddButton();
//        tableUtil.setRowBackgroundColor("#table_productNo", null, "#totalRowTR", 'odd');
        setTotal();
    });

    //增加行          //TODO 加号按钮单击触发的脚本
    $(document).bind('click', function (event) {
        var target = event.target;
        if (target.className != "btnPlus")  return;

        var ischeck = checkVehicleInfo(target);       //TODO 检查改行数据是否符合验证规则
        if (!ischeck && ischeck != null) return;
        //采购单检查是否相同
        if ($(".item").size() >= 2) {
            if (invoiceCommon.checkSameItemForOrder("item")) {
                nsDialog.jAlert("单据有重复内容，请修改或删除。");
                return false;
            }
            if (invoiceCommon.checkSameCommodityCode("item")) {
                nsDialog.jAlert("商品编码有重复内容，请修改或删除。");
                return false;
            }
        }
        borrowOrderAdd();
    });

    $(".itemAmount").live("change",function () {
        setTotal();
    });

    //新增借调单
    $("#addBorrowOrder").click(function(){
        window.location.href="borrow.do?method=createBorrowOrder";
    });

    //
    $("#tabMore tr:gt(1)").hide();



    $("#saveBorrowOrder").bind("click", function () {
        if ($(this).attr("submitLock")) {
            return;
        }
        deleteEmptyItem();
        setTotal();
        $("#btnType").val('saveBorrowOrder');
        if (!validateBorrowOrder()){
            return;
        }
        $(this).attr("submitLock", true);
        $('#borrowOrderForm').ajaxSubmit({
            url: "borrow.do?method=saveBorrowOrder",
            dataType: "json",
            type: "POST",
            success: function (result) {
                $("#borrowOrderForm").removeAttr("submitLock");
                var borrowOrderId = result && result.data ? result.data : "";
                if (result.success) {
                    if(stringUtil.isNotEmpty(borrowOrderId)){
                        window.location.href="borrow.do?method=toBorrowOrderDetail&borrowOrderId="+borrowOrderId;
                    }
                }else {
                    nsDialog.jAlert(result.msg);
                }
            },
            error: function () {
                $("#borrowOrderForm").removeAttr("submitLock");
                nsDialog.jAlert("出现异常！");
            }
        });
    });

    $("#searchBtn").click(function(){
        var data={
            startTimeStr:$("#startTimeInput").val(),
            endTimeStr:$("#endTimeInput").val(),
            storehouseId:$("#storehouseId").val(),
            operator:$("#operator").val(),
            borrower:$("#borrower").val(),
            receiptNo:$("#receiptNo").val(),
            borrowerType:$("#borrowerType").val(),
            returnStatusStr:$("#returnStatus").val(),
            startPageNo:1,
            pageSize:15
        };

        App.Net.asyncPost({
            url:"borrow.do?method=getBorrowOrders",
            data:data,
            cache:false,
            dataType:"json",
            success:function(json){
                initBorrowOrderList(json);
                initPages(json, "dynamical1", "borrow.do?method=getBorrowOrders", '', "initBorrowOrderList", '', '', data, '');
            },
            error:function(){
                nsDialog.jAlert("查询借调单异常！");
            }
        });

    });

    $("#toBorrowOrderList").click(function(){
        toBorrowOrderList();
    });
    $("#mobile").blur(function() {
        if(this.value) {
            var landline = document.getElementById("landline");
            var mobile = document.getElementById("mobile");
            if(mobile.value != "" && mobile.value != null) {
                check.inputSupplierMobileBlur(mobile, landline);
            }
        }
    });
});

//borrowOrderDetail
$().ready(function(){

//    $("#orderVestDate").datetimepicker({
//        "numberOfMonths": 1,
//        "showButtonPanel": true,
//        "changeYear": true,
//        "changeMonth": true,
//        "yearRange":"c-100:c+100",
//        "showHour":true,
//        "showMinute":true,
//        "dateFormat":"yy-mm-dd",
//        "yearSuffix": "",
//        "onSelect": function(dateText, inst) {
//            var lastValue = $(this).attr("lastValue");
//            if(lastValue == dateText) {
//                return;
//            }
//            if(dateText) {
//                var myDate = GLOBAL.Date.getCurrentFormatDate();
//                if(myDate.replace(/[- ]+/, "") > dateText.replace(/[- ]+/, "")) {
//                    nsDialog.jAlert("请选择今天及以后的日期。");
//                    $(this).val("");
//                }
//                $(this).attr("lastValue", dateText);
//            }
//        }
//    });

    $("#returnDate").datepicker({
        "numberOfMonths": 1,
        "showButtonPanel": true,
        "changeYear": true,
        "changeMonth": true,
        "yearRange": "c-100:c+100",
        "yearSuffix": "",

        "onSelect": function(dateText, inst) {
            var lastValue = $(this).attr("lastValue");
            if(lastValue == dateText) {
                return;
            }

            if(dateText) {
                var myDate = GLOBAL.Date.getCurrentFormatDate();
                if(myDate.replace(/[- ]+/, "") > dateText.replace(/[- ]+/, "")) {
                    nsDialog.jAlert("请选择今天及以后的日期。");
                    $(this).val("");
                }
                $(this).attr("lastValue", dateText);
            }
        }
    });


    $("#printBtn").click(function () {
        var borrowOrderId=$("#borrowOrderId").val();
        if (stringUtil.isEmpty(borrowOrderId)) {
            nsDialog.jAlert("当前单据不存在，无法打印");
            return;
        }
        var url = "borrow.do?method=printBorrowOrder&borrowOrderId=" + borrowOrderId;
        window.open(url, "_blank");
    });




    $("#borrowOrderTable input[type='checkbox']").live("click",function(){
        if($(this).attr("id")=="selectAll"){
            if($(this).attr("checked")){
                $("#borrowOrderTable input[type='checkbox']").each(function(){
                    if(!$(this).attr("disabled")){
                        $(this).attr("checked",true);
                    }
                });
            }else{
                $("#borrowOrderTable input[type='checkbox']").each(function(){
                    if(!$(this).attr("disabled")){
                        $(this).attr("checked",false);
                    }
                });
            }
        }else{
            if(!$(this).attr("disabled")){
                if(isSelectAll()){
                    $("#selectAll").attr("checked",true);
                }else{
                    $("#selectAll").attr("checked",false);
                }
            }
        }

    });

});

function isSelectAll() {
    var flag = true;
    $("#borrowOrderTable input[type='checkbox']").not($("#selectAll")).each(function() {
        if(!$(this).attr("disabled")){
            if(!$(this).attr("checked")) {
                flag = false;
            }
        }
    });
    return flag;
}


function initBorrowOrderDetail(borrowOrder){
    $("#borrowOrderTable tr:not(:first)").remove();
    var items=borrowOrder.itemDTOs;
    if(stringUtil.isEmpty(borrowOrder.idStr)||stringUtil.isEmpty(items)) return;
    $("#receiptNo").text(G.normalize(borrowOrder.receiptNo));
    $("#borrowDate").text(G.normalize(borrowOrder.vestDateStr));
    $("#returnDateSpan").text(G.normalize(borrowOrder.returnDateStr));
    $("#borrower").text(G.normalize(borrowOrder.borrower));
    $("#operator").text(G.normalize(borrowOrder.operator));
    $("#storehouseName").text(G.normalize(borrowOrder.storehouseName));
    $("#receiptNo").text(G.normalize(borrowOrder.receiptNo));
    $("#phone").text(G.normalize(borrowOrder.phone));
    $("#memo").text(G.normalize(borrowOrder.memo));
    var borrowerType=borrowOrder.borrowerType;
    if(borrowerType=="customer"){
        $("#borrowerType").text("客户");
    }else if(borrowerType=="supplier"){
        $("#borrowerType").text("供应商");
    }
    var item;
    var borrowItemId;
    var orderTotal=0.0;
    for(var i=0;i<items.length;i++){
        item=items[i];
        borrowItemId=item.idStr;
        var trStr="<tr>";
        var price=item.price;
        var returnAmount=item.returnAmount;
        var amount=item.amount;
        var unit=G.normalize(item.unit);
        var itemTotal=price*amount;
        orderTotal+=itemTotal;
        var unReturnAmount=dataTransition.rounding(item.unReturnAmount,2);
        if(unReturnAmount==0){
            trStr+='<td>'+'<input type="checkbox" disabled="disabled" class="itemCheck" borrowItemId="'+borrowItemId+'"/>'+'</td>';
        }else{
            trStr+='<td>'+'<input type="checkbox" class="itemCheck" borrowItemId="'+borrowItemId+'"/>'+'</td>';
        }
        trStr+='<td>'+G.normalize(item.commodityCode)+'</td>';
        trStr+='<td title="'+item.productName+'">'+G.normalize(item.productName)+'</td>';
        trStr+='<td title="'+item.brand+'">'+G.normalize(item.brand)+'</td>';
        trStr+='<td>'+G.normalize(item.spec)+'</td>';
        trStr+='<td title="'+item.model+'">'+G.normalize(item.model)+'</td>';
        trStr+='<td title="'+item.vehicleBrand+'">'+G.normalize(item.vehicleBrand)+'</td>';
        trStr+='<td title="'+item.vehicleModel+'">'+G.normalize(item.vehicleModel)+'</td>';
        trStr+='<td title="'+item.price+'">'+dataTransition.rounding(price,2)+'</td>';
        trStr+='<td class="amount" title="'+item.total+'">'+dataTransition.rounding(amount,2)+unit+'</td>';
        trStr+='<td title="'+itemTotal+'">'+dataTransition.rounding(itemTotal,2)+'</td>';
        trStr+='<td class="unReturnAmount" title="'+unReturnAmount+'">'+unReturnAmount+unit+'</td>';
        trStr+='</tr>';
        $("#borrowOrderTable").append(trStr);
    }
    trStr='<tr style="font-weight:bold;"><td colspan="10" style="text-align:center;">合计：</td>';
    trStr+='<td>'+dataTransition.rounding(orderTotal,2)+'</td><td></td></tr>';
    $("#borrowOrderTable").append(trStr);
    $("#borrower").live("click",function(){
        var borrowerId=borrowOrder.borrowerIdStr;
        var borrowerType=borrowOrder.borrowerType;
        toBorrowerInfo(borrowerType,borrowerId);
    });
}

function initReturnOrder(json){
    $("#returnOrderTable tr:not(:first):not('.space')").remove();
    $(".btnClick").remove();
    var borrowOrder=json.results[0];
    if(stringUtil.isEmpty(borrowOrder)||stringUtil.isEmpty(borrowOrder.idStr)){
        return;
    }
    if(G.isEmpty($("#borrowOrderIdForm").val())){
        $("#returnOrderDTOForm").append('<input id="borrowOrderIdForm" type="hidden" value="'+borrowOrder.idStr+'" name="borrowOrderId"/>');
    }
    var items=borrowOrder.itemDTOs;
    if(stringUtil.isEmpty(items)) {
        return;
    }

    for(var i=0;i<items.length;i++){
        var item=items[i];
        var borrowItemId=item.idStr;
        var productName=G.normalize(item.productName);
        var borrowAmount=dataTransition.rounding(item.amount,2);
        var hasReturnAmount=dataTransition.rounding(item.returnAmount,2);
        var unit=G.normalize(item.unit);
        var sellUnit=G.normalize(item.sellUnit);
        var storageUnit=G.normalize(item.storageUnit);
        var rate=dataTransition.rounding(item.rate,2);
        var hasBigUnit=(sellUnit!=storageUnit&&rate*1!=0);
        var trStr="<tr class='returnItem bg item'><td>";
        trStr+='<input  type="hidden" value="'+item.productIdStr+'" name="itemDTOs['+i+'].productIdStr"/>';
        trStr+='<input  type="hidden" value="'+item.price+'" name="itemDTOs['+i+'].price"/>';
        trStr+='<input id="itemDTOs'+i+'.productId" type="hidden" value="'+item.productIdStr+'" name="itemDTOs['+i+'].productId"/>';
        trStr+='<input type="hidden" class="hasReturnAmount" value="'+hasReturnAmount+'" />';
        trStr+=''+G.normalize(item.commodityCode)+'</td>';
        trStr+='<td title="'+item.productName+'"><input type="hidden" value="'+productName+'" name="itemDTOs['+i+'].productName"/>'
        trStr+='<span name="itemDTOs['+i+'].productName" value="'+productName+'">'+productName+'</span></td>';
        trStr+='<td title="'+item.brand+'">'+G.normalize(item.brand)+'</td>';
        trStr+='<td>'+G.normalize(item.spec)+'</td>';
        trStr+='<td title="'+item.model+'">'+G.normalize(item.model)+'</td>';
        trStr+='<td title="'+item.vehicleModel+'">'+G.normalize(item.vehicleModel)+'</td>';
        trStr+='<td title="'+item.vehicleBrand+'">'+G.normalize(item.vehicleBrand)+'</td>';
        trStr+='<td  title="'+borrowAmount+'">'+borrowAmount+unit+'<input type="hidden" class="borrowAmount" value="'+borrowAmount+'" name="itemDTOs['+i+'].borrowAmount"/></td>';
        if(!hasBigUnit){
            trStr+='<td>'+sellUnit+'<input id="itemDTOs'+i+'.unit" type="hidden" value="'+unit+'" name="itemDTOs['+i+'].unit"/>';
            trStr+='<input id="itemDTOs'+i+'.borrowUnit" type="hidden" value="'+unit+'" name="itemDTOs['+i+'].borrowUnit"/>';
        }else{
            trStr+='<td><span class="itemUnitSpan" id="itemDTOs'+i+'.unitSpan" tdId="itemDTOs'+i+'">'+unit+'</span><input id="itemDTOs'+i+'.sellUnit" type="hidden" value="'+sellUnit+'" name="itemDTOs['+i+'].sellUnit"/>';
            trStr+='<input id="itemDTOs'+i+'.unit" type="hidden" value="'+unit+'" name="itemDTOs['+i+'].unit"/>';
            trStr+='<input id="itemDTOs'+i+'.borrowUnit" type="hidden" value="'+unit+'" name="itemDTOs['+i+'].borrowUnit"/>';
        }
        trStr+='<input type="hidden" id="itemDTOs'+i+'.storageUnit" name="itemDTOs['+i+'].storageUnit" value="'+storageUnit+'" autocomplete="off">';
        trStr+='<input type="hidden" id="itemDTOs'+i+'.rate" name="itemDTOs['+i+'].rate" value="'+rate+'" autocomplete="off">';
        trStr+='</td>';
        trStr+='<td><input id="itemDTOs'+i+'.returnAmount" class="returnAmount txt" name="itemDTOs['+i+'].returnAmount"/></td>';
        trStr+='<td><input class="returner txt" name="itemDTOs['+i+'].returner" style="width:70%;"/></td>';
        trStr+='</tr>';
        $("#returnOrderTable").append(trStr);
    }
    var okBtn='<div class="btnClick"><div class="height"></div><input id="saveReturnOrderBtn" type="button" value="确认归还" onfocus="this.blur();" />';
    okBtn+='<input id="cancelReturnBtn" type="button" value="取消" onfocus="this.blur();" /></div>';
    $("#returnOrder_dialog").append(okBtn);
    if(!G.isEmpty(borrowOrder.storehouseIdStr)){
        $("#storehouseId").val(borrowOrder.storehouseIdStr);
    }
    $("#cancelReturnBtn").live("click",function(){
        $("#returnOrder_dialog").dialog('close');
    });

    $("#saveReturnOrderBtn").live("click",function(){
        if(!validateSaveReturnOrder()){
            return;
        }
        $(this).attr("disabled","disabled");
        $("#returnOrderDTOForm").ajaxSubmit({
            url:"borrow.do?method=saveReturnOrder",
            dataType: "json",
            type: "POST",
            success:function(result){
                $("#returnOrder_dialog").dialog('close');
                if(result.success){
                    var borrowOrderId=$("#borrowOrderId").val();
                    window.location.href="borrow.do?method=toBorrowOrderDetail&borrowOrderId="+borrowOrderId
                }else{
                    nsDialog.jAlert(result.msg);
                }
            },
            error:function(){
                nsDialog.jAlert("借调归还出现异常！");
            }
        });
    });

    $(".itemUnitSpan").click(function(){
        var unitIdPrefix = $(this).attr("tdId");
        var storageUnit= $("#" + unitIdPrefix + "\\.storageUnit").val();
        var sellUnit= $("#" + unitIdPrefix + "\\.sellUnit").val();
        var rate=$("#" + unitIdPrefix + "\\.rate").val();
        if($(this).text()==sellUnit){
            $("#" + unitIdPrefix + "\\.unitSpan").text(storageUnit);
            $("#" + unitIdPrefix + "\\.unit").val(storageUnit);
            $("#" + unitIdPrefix + "\\.returnAmount").val(dataTransition.rounding($("#" + unitIdPrefix + "\\.returnAmount").val() * 1 / rate,2));
        }else if($(this).text()==storageUnit){
            $("#" + unitIdPrefix + "\\.unitSpan").text(sellUnit);
            $("#" + unitIdPrefix + "\\.unit").val(sellUnit);
            $("#" + unitIdPrefix + "\\.returnAmount").val(dataTransition.rounding($("#" + unitIdPrefix + "\\.returnAmount").val() * 1 * rate,2));
        }

    });
}



function initReturnRunningRecord(json){
    $("#returnRunningRecord_dialog table").remove();
    $(".recordTable").remove();
    $(".btnClick").remove();
    var dTable='<div class="recordTable"><div style="color:#515151;"><b>操作时间：</b><span></span>&nbsp;<b>操作人：</b><span></span>&nbsp;';
    if(APP_BCGOGO.Permission.Version.StoreHouse){
        dTable+= '<b>回料仓库：</b><span></span>';
    }
    dTable+='</div>';
    dTable+='<table cellpadding="0" cellspacing="0" class="tabMerge  tabMereCus">';
    dTable+='<col width="80"><col width="90"><col width="90"><col width="90"><col width="70"><col width="80"><col width="70"><col width="90"><col width="90"><col width="90">';
    dTable+='<tr class="tab_title"><td>商品编号</td><td>品名</td><td>品牌/产地</td><td>规格</td><td>型号</td><td>车辆品牌</td><td>车型</td><td>借调数</td><td>本次归还数量</td><td>归还人</td></tr>';
    dTable+='<tr><td colspan="10" style="text-align: center;">无归还记录！</td></tr></table></div>';
    var returnOrders=json.results;
    if(stringUtil.isEmpty(returnOrders)){
        $("#returnRunningRecord_dialog").append(dTable);
        return;
    }
    for(var count=0;count<returnOrders.length;count++){
        var returnOrder=returnOrders[count];
        if(stringUtil.isEmpty(returnOrder)||stringUtil.isEmpty(returnOrder.idStr)){
            continue;
        }
        var vestDateStr=G.normalize(returnOrder.vestDateStr);
        var operator=G.normalize(returnOrder.operator);
        var storehouseName=G.normalize(returnOrder.storehouseName);
        var tableId="returnRecord"+count+"Table";
        var tableStr='<div class="recordTable"><div style="color:#515151;"><b>操作时间：</b><span>'+vestDateStr+'</span>&nbsp;<b>操作人：</b><span>'+operator+'</span>&nbsp;';
        if(APP_BCGOGO.Permission.Version.StoreHouse){
            tableStr+='<b>回料仓库：</b><span>'+storehouseName+'</span>';
        }
        tableStr+='</div>';
        tableStr+='<table cellpadding="0" cellspacing="0" class="tabMerge  tabMereCus" id="'+tableId+'">';
        tableStr+='<col width="80"><col width="90"><col width="90"><col width="90"><col width="70"><col width="80"><col width="70"><col width="90"><col width="90"><col width="90">';
        tableStr+='<tr class="tab_title"><td>商品编号</td><td>品名</td><td>品牌/产地</td><td>规格</td><td>型号</td><td>车辆品牌</td><td>车型</td><td>借调数</td><td>本次归还数量</td><td>归还人</td></tr></table></div>';
        $("#returnRunningRecord_dialog").append(tableStr);

        var items=returnOrder.itemDTOs;
        if(stringUtil.isEmpty(items)) {
            return;
        }
        for(var i=0;i<items.length;i++){
            var item=items[i];
            var borrowItemId=item.idStr;
            var productName=G.normalize(item.productName);
            var unit=G.normalize(item.unit);
            var borrowUnit=G.normalize(item.borrowUnit);
            var trStr="<tr>";
            trStr+='<td>';
            var borrowAmount=dataTransition.rounding(item.borrowAmount,2);
            trStr+=''+G.normalize(item.commodityCode)+'</td>';
            trStr+='<td title="'+item.productName+'"><input type="hidden" value="'+productName+'" name="itemDTOs['+i+'].productName"/>'
            trStr+='<span name="itemDTOs['+i+'].productName" value="'+productName+'">'+productName+'</span></td>';
            trStr+='<td title="'+item.brand+'">'+G.normalize(item.brand)+'</td>';
            trStr+='<td>'+G.normalize(item.spec)+'</td>';
            trStr+='<td title="'+item.model+'">'+G.normalize(item.model)+'</td>';
            trStr+='<td title="'+item.vehicleModel+'">'+G.normalize(item.vehicleModel)+'</td>';
            trStr+='<td title="'+item.vehicleBrand+'">'+G.normalize(item.vehicleBrand)+'</td>';
            trStr+='<td title="'+borrowAmount+'">'+borrowAmount+borrowUnit+'</td>';
            trStr+='<td title="'+item.returnAmount+'">'+G.normalize(item.returnAmount)+unit+'</td>';
            trStr+='<td title="'+item.returner+'">'+G.normalize(item.returner)+'</td>';
            trStr+='</tr>';
            $("#"+tableId).append(trStr);
        }
        $("#"+tableId).append('<div class="height" />');
    }
    var okBtn='<div class="btnClick"><div class="height"><input id="closeBtn"  type="button" value="关闭" onfocus="this.blur();" /></div>';
    $("#returnRunningRecord_dialog").append(okBtn);
    $("#closeBtn").live("click",function(){
        $("#returnRunningRecord_dialog").dialog('close');
    });
}

//克隆模版，初始化所有的INPUT
function borrowOrderAdd(){
    var tr = $(getBorrowOrderTrSample()).clone();
    $(tr).find("input").val("");
    $(tr).find("input,a,span").each(function (i) {
        //去除文本框的自动填充下拉框
        if ($(this).attr("type") == "text") {
            $(this).attr("autocomplete", "off");
        }
        if (!this || !this.id) return;
        //replace id
        var idStrs = this.id.split(".");
        var idPrefix = idStrs[0];
        var idSuffix = idStrs[1];
        var domrows = parseInt(idPrefix.substring(8, idPrefix.length));

        var tcNum = $(".item").size();
        while (checkThisDom(tcNum, idStrs[1])) {
            tcNum = ++tcNum;                       //TODO 计算行号，如果目标行号对应的元素存在，则该行号加一
        }
        //TODO 组装新的ID和NAME  Begin-->
        var newId = "itemDTOs" + tcNum + "." + idStrs[1];
        $(this).attr("id", newId);
        //replace name
        var nameStr = $(this).attr("name");
        if (nameStr == undefined || nameStr == '') {
            return true;
        }
        if ($(this).attr("name").split(".")[1]) {
            var newName = "itemDTOs[" + tcNum + "]." + $(this).attr("name").split(".")[1];
            $(this).attr("name", newName);
        }
        //TODO <-- End
        $(this).attr("autocomplete", "off");
    });
    $("#table_productNo tr:last").after($(tr));
    isShowAddButton();
//    tableUtil.tableStyle("#table_productNo","#totalRowTR","odd");
//    tableUtil.setRowBackgroundColor("#table_productNo", null, "#totalRowTR", 'odd');
    return $(tr);
}

function getBorrowOrderTrSample() {
    var trSample = '<tr class="bg item table-row-original">' +
        '<td style="padding-left:10px;">' +
        '   <input id="itemDTOs0.commodityCode" name="itemDTOs[0].commodityCode" type="text" class="txt checkStringEmpty" value="" maxlength="20"/>' +
        '   <input id="itemDTOs0.id" name="itemDTOs[0].id" type="hidden" value=""/>' +
        '   <input id="itemDTOs0.productId" name="itemDTOs[0].productId" type="hidden" value=""/>' +
        '</td>' +
        '<td>' +
        '	<input id="itemDTOs0.productName" name="itemDTOs[0].productName"  class="txt checkStringEmpty" value="" type="text" maxlength="50"/>' +
        '</td>' +
        '<td>' +
        '	<input id="itemDTOs0.brand" name="itemDTOs[0].brand" maxlength="100" class="txt checkStringEmpty" value="" type="text" maxlength="100"/>' +
        '</td>' +
        '<td>' +
        '   <input id="itemDTOs0.spec" name="itemDTOs[0].spec"  class="txt checkStringEmpty" value="" type="text" maxlength="50"/>' +
        '</td>' +
        '<td>' +
        '   <input id="itemDTOs0.model" name="itemDTOs[0].model"  class="txt checkStringEmpty" value="" type="text" maxlength="50"/>' +
        '</td>' +
        '<td>' +
        '	<input id="itemDTOs0.vehicleModel" name="itemDTOs[0].vehicleModel"  value="" class="txt checkStringEmpty"  type="text" maxlength="200"/>' +
        '</td>' +
        '<td>' +
        '	<input id="itemDTOs0.vehicleBrand" name="itemDTOs[0].vehicleBrand"  value="" class="txt checkStringEmpty"  type="text" maxlength="200"/>' +
        '</td>' +
        '<td>' +
        '<span id="itemDTOs0.price_span" name="itemDTOs[0].price_span"></span> ' +
        '<input type="hidden"  name="itemDTOs0.price" id="itemDTOs0.price" class="itemPrice"/> ' +

        '</td>' +
        '    <td>' +
        '       <span id="itemDTOs0.inventoryAmountSpan" style="display: block;"></span>' +
        '       <input type="hidden" id="itemDTOs0.inventoryAmount" name="itemDTOs[0].inventoryAmount" value="" class="itemInventoryAmount table_input" readonly="readonly" type="text"/>' +
        '   </td>' +

        '<td>' +
        '	<input id="itemDTOs0.amount" name="itemDTOs[0].amount"  value="" class="txt checkStringEmpty itemAmount"  type="text" maxlength="20" data-filter-zero="true"/>' +
        '</td>' +
        '<td>' +
        '   <input id="itemDTOs0.unit" name="itemDTOs[0].unit"  value="" class="itemUnit checkStringEmpty txt"/>' +
        '   <input type="hidden" id="itemDTOs0.storageUnit" name="itemDTOs[0].storageUnit" value=""/>' +
        '   <input type="hidden" id="itemDTOs0.sellUnit" name="itemDTOs[0].sellUnit" value=""/>' +
        '   <input type="hidden" id="itemDTOs0.rate" name="itemDTOs[0].rate" value=""/>' +
        '</td>' +

        '<td>' +
        '   <span id="itemDTOs0.total_span" name="itemDTOs[0].total_span"></span>' +
        '   <input id="itemDTOs0.total" name="itemDTOs[0].total"  value="0.00" type="hidden"/>' +
        '</td>' +
        '   <td style="border-right:none;">' +
        '	<a class="btnMinus" id="itemDTOs0.btnMinus" name="itemDTOs[0].btnMinus">删除</a>' +
        '   </td>' +
        '</tr>';
    return trSample;
}

function isShowAddButton() {
    //如果初始化的话就默认加一行
    if ($(".item").size() <= 0) {
        borrowOrderAdd();
    }
    $(".item .btnPlus").remove();
    var opera1Id = $(".item:last").find("td:last>a[class='btnMinus']").attr("id");
    if (!opera1Id) return;
    $(".item:last").find("td:last>a[class='btnMinus']").after(' <a class="btnPlus" ' +
        ' id="itemDTOs' + (opera1Id.split(".")[0].substring(8)) + '.btnPlus"' +
        'name="itemDTOs[' + (opera1Id.split(".")[0].substring(8)) + '].btnPlus" ' + '>增加</a>');
}

function setTotal() {
    var total = 0;
    $(".itemPrice").each(function (i) {
        var price = $(this).val();
        var idPrefix = $(this).attr("id").split(".")[0];
        price = dataTransition.rounding(price, 2)
        $(".itemPrice").eq(i).val(price);
        $("#" + idPrefix + "\\.price_span").text(price ? price : '');
        var amount = $("#" + idPrefix + "\\.amount").val()*1;
        var itemTotal = dataTransition.rounding(price * amount,2);
        $("#" + idPrefix + "\\.total").val(itemTotal);
        $("#" + idPrefix + "\\.total_span").text(itemTotal ? itemTotal : '');
        total += itemTotal;
        htmlNumberFilter($("#" + idPrefix + "\\.amount").add("#" + idPrefix + "\\.price_span").add("#" + idPrefix + "\\.inventoryAmountSpan").add("#" + idPrefix + "\\.total_span"), true);
    })
    $("#borrowTotal").text(dataTransition.rounding(total,2));
}

function checkEmptyRow($tr) {
    var propertys = ["productName"];
    var itemInfo = "";
    for(var i = 0, len = propertys.length; i < len; i++) {
        itemInfo += $tr.find("input[id$='." + propertys[i] + "']").val();
    }
    if(GLOBAL.Lang.isEmpty(itemInfo)) {
        return true;
    }
    return false;
}

function validateSaveReturnOrder(){
    var msg = "";
    $(".returnItem").each(function (i) {
        var returnAmount =$(this).find(".returnAmount").val()*1;
        var returner =$.trim($(this).find(".returner").val());
        var hasReturnAmount = $(this).find(".hasReturnAmount").val()*1;
        var borrowAmount = $(this).find(".borrowAmount").val()*1;
        if($(this).find(".itemUnitSpan").size()==1){
            var unitIdPrefix= $(this).find(".itemUnitSpan").attr("tdId");
            var storageUnit= $("#" + unitIdPrefix + "\\.storageUnit").val();
            var sellUnit= $("#" + unitIdPrefix + "\\.sellUnit").val();
            var rUnit= $("#" + unitIdPrefix + "\\.unit").val();
            var bUnit=$("#" + unitIdPrefix + "\\.borrowUnit").val();
            var rate=$("#" + unitIdPrefix + "\\.rate").val();
            if(bUnit==sellUnit&&rUnit==storageUnit){
                returnAmount=dataTransition.rounding($("#" + unitIdPrefix + "\\.returnAmount").val() * 1 * rate,2);
            }else if(bUnit==storageUnit&&rUnit==sellUnit){
                returnAmount=dataTransition.rounding($("#" + unitIdPrefix + "\\.returnAmount").val() * 1 /rate,2);
            }
        }
        if (G.Lang.isEmpty(returner)) {
            msg += "<br>第" + (i+1) + "行，归还人为空！";
        }else if (G.Lang.isEmpty(returnAmount)) {
            msg += "<br>第" + (i+1) + "行，归还数量为空！";
        }else if (returnAmount==0) {
            msg += "<br>第" + (i+1) + "行，归还数量为0！";
        }else if ((hasReturnAmount+returnAmount)>borrowAmount) {
            msg += "<br>第" + (i+1) + "行，归还数量超过借调总数！";
        }
    });

    if(!G.Lang.isEmpty(msg)){
        nsDialog.jAlert("对不起"+msg);
        return false;
    }

    return true;
}

//校验单据的信息完整与正确性
function validateBorrowOrder() {
    if(stringUtil.isEmpty($("#orderVestDate").val())){
        nsDialog.jAlert("请选择借调日期！");
        return false;
    }
    if(stringUtil.isEmpty($("#returnDate").val())){
        alert("请选择预计还调日期！");
        $('#returnDate').focus();
        return false;
    }
    if(stringUtil.isEmpty($.trim($(".customerOrSupplierName").val()))){
        nsDialog.jAlert("请输入借调者！");
        return false;
    }
    if($.trim($(".customerOrSupplierName").val()).length > 30) {
        nsDialog.jAlert("借调者长度不能大于30");
        return;
    }
    if($("#memo").val().length>450){
        nsDialog.jAlert("备注内容过长，请重新输入！");
        return false;
    }

    //验证手机号码
//    if($.trim($("#mobile").val())) {
//        check.saveContact(document.getElementById("mobile"));
////        return false;
//    }
    if(!checkSupplierInfo()){
        return false;
    }
    var msg = "";
    var length = 0;
    var trCount =  $("#table_productNo tr .item").size();
    $(".item").each(function (i) {
        var productId = $.trim($(this).find("input[id$='.productId']").val());
        var productName = $.trim($(this).find("input[id$='.productName']").val());
        var unit = $.trim($(this).find("input[id$='.unit']").val());
        if(!$(this).find("input[id$='.productId']")[0]){
            return true;
        }
        if (trCount == 1 && checkEmptyRow($(this))) {
            msg=",单据内容不能为空!";
            return false;
        } else if (trCount>1 && checkEmptyRow($(this))) {
            length++;
        } else if (G.Lang.isEmpty(productName)) {
            msg += "<br>第" + (i+1) + "行，缺少品名，请补充完整.";
        }else if (G.Lang.isEmpty(productId)) {
            msg += "<br>第" + (i+1) + "行，商品不存在，请修改.";
        }else if (G.Lang.isEmpty(unit)) {
            msg += "<br>第" + (i+1) + "行，请输入商品单位.";
        }
    });
    if(!G.Lang.isEmpty(msg)){
        nsDialog.jAlert("对不起"+msg);
        return false;
    }
    if (invoiceCommon.checkSameCommodityCode("item")) {
        alert("商品编码有重复内容，请修改或删除。");
        return false;
    }
    //检查是否相同
    if (trCount >= 2) if (invoiceCommon.checkSameItemForOrder("item")) {
        nsDialog.jAlert("对不起，单据有重复内容，请修改或删除!");
        return false;
    }
    var amountMsg = "",lackMsg = "";

    $(".itemAmount").each(function () {
        if (!$(this).val()) {
            $(this).val(0)
        }
        if (parseFloat($.trim($(this).val())) * 1 == 0) {
            amountMsg = amountMsg + "<br>第" + ($(this).index(".itemAmount") + 1) + "行借调数为0，请填写正确的借调数！";
        }
        var idPrefix = $(this).attr("id").split(".")[0];
        if ($("#" + idPrefix + "\\.inventoryAmount").val()* 1 - $(this).val() * 1 < 0) {
            lackMsg = lackMsg + "<br>第" + ($(this).index(".itemAmount") + 1) + "行商品库存不足，请修改！";
        }
    });
    if(!G.Lang.isEmpty(amountMsg)){
        nsDialog.jAlert("对不起"+amountMsg);
        return false;
    }
    if(!G.Lang.isEmpty(lackMsg)){
        nsDialog.jAlert("对不起"+lackMsg);
        return false;
    }
    if(!checkStorehouseSelected()) {
        return false;
    }
    return true;
}

function initBorrowOrderList(json){
    $("#borrowOrderTable tr:not(:first)").remove();
    $("#allBorrowOrderSize").text(0);
    $("#RETURN_NONE_SIZE").text(0);
    $("#RETURN_PARTLY_SIZE").text(0);
    $("#RETURN_ALL_SIZE").text(0);
    var borrowOrders=json[0][0];
    var stat=json[0][1]
    if(stringUtil.isEmpty(borrowOrders)) {
        if(APP_BCGOGO.Permission.Version.StoreHouse){
            var trStr='<tr><td colspan="8" style="text-align: center;">无借调单记录！</td></tr>';
        }else{
            var trStr='<tr><td colspan="7" style="text-align: center;">无借调单记录！</td></tr>';
        }
        $("#borrowOrderTable").append(trStr);
        return;
    }

    $("#allBorrowOrderSize").text(stat.allBorrowOrderSize);
    var returnStatusStat=stat.returnStatusStat;
    for(var key in returnStatusStat){
        $("#"+returnStatusStat[key][0]+"_SIZE").text(returnStatusStat[key][1]);
    }

    var borrowOrder;
    for(var i=0;i<borrowOrders.length;i++){
        borrowOrder=borrowOrders[i];
        var borrowOrderId=borrowOrder.idStr;
        var receiptNo=G.normalize(borrowOrder.receiptNo);
        var storehouseName=G.normalize(borrowOrder.storehouseName);
        var borrower=G.normalize(borrowOrder.borrower);
        var total=dataTransition.rounding(borrowOrder.total,2);
        var vestDate=G.normalize(borrowOrder.vestDateStr);
        var operator=G.normalize(borrowOrder.operator);
        var returnStatusStr=G.normalize(borrowOrder.returnStatusStr);
        var trStr='<tr>';
        trStr+='<td style="padding-left: 10px;">'+(i+1)+'</td>';
        trStr+='<td><a class="blue_color" onclick="toBorrowOrderDetail(\''+borrowOrderId+'\')">'+ receiptNo+'</a></td>';
        if(APP_BCGOGO.Permission.Version.StoreHouse){
            trStr+='<td>'+storehouseName+'</td>';
        }
        trStr+='<td>'+borrower+'</td>';
        trStr+='<td>'+total+'</td>';
        trStr+='<td>'+operator+'</td>';
        trStr+='<td>'+vestDate+'</td>';
        trStr+='<td>'+returnStatusStr+'</td>';
        trStr+='</tr>';
        $("#borrowOrderTable").append(trStr);
    }
    tableUtil.tableStyle('#borrowOrderTable','.titleBg');

}

//目前只有库存信息要更新
function updateProductInventory(data,isShowMsg) {
    $("#table_productNo").find(".item").each(function () {
        var productId = $(this).find("input[id$='.productId']").val();
        if (!G.Lang.isEmpty(productId)) {
            $(this).find("input[id$='.inventoryAmount']").val(G.rounding(data[productId],"0"),2);
            $(this).find("span[id$='.inventoryAmountSpan']").html(G.rounding(data[productId],"0"),2);
        }
    });
    var lackMsg = "";
    var idPrefix;
    $(".itemAmount").each(function () {
        idPrefix = $(this).attr("id").split(".")[0];
        if ($("#"+idPrefix+"\\.inventoryAmount").val() * 1 - $(this).val() * 1 < 0) {
            lackMsg = lackMsg + "\n第" + ($(this).index(".itemAmount") + 1) + "行商品库存不足，无法借调！";
        }
    });
    if (!APP_BCGOGO.Permission.Version.IgnorVerifierInventory && isShowMsg) {
        if (lackMsg) {
            alert(lackMsg);
            return false;
        } else {
            return true;
        }
    }
}

function updateProductStorageBin(data) {
    $("#table_productNo").find(".item").each(function () {
        var productId = $(this).find("input[id$='.productId']").val();
        if (!G.Lang.isEmpty(productId)) {
            $(this).find("input[id$='.storageBin']").val(G.normalize(data[productId],""));
            $(this).find("span[id$='.storageBinSpan']").html(G.normalize(data[productId],""));
        }
    });
}

function toBorrowOrderDetail(borrowOrderId){
    if(stringUtil.isEmpty(borrowOrderId)){
        return;
    }
    window.open("borrow.do?method=toBorrowOrderDetail&borrowOrderId="+borrowOrderId);
}

function toBorrowOrderList(){
    window.location.href ="borrow.do?method=toBorrowOrderList";
}

function toBorrowerInfo(borrowerType,borrowerId){
    if(borrowerType=="customer"){
        window.open("unitlink.do?method=customer&customerId="+borrowerId);
    }else if(borrowerType="supplier"){
        window.open("unitlink.do?method=supplier&supplierId="+borrowerId);
    }
}
//bug7265
function initDuiZhanInfo(){

}

function customerOrSupplierSuggestion($domObject) {
    var searchWord = $domObject.val().replace(/[\ |\\]/g, "");
    var dropList = APP_BCGOGO.Module.droplist;
    dropList.setUUID(GLOBAL.Util.generateUUID());
    var ajaxData = {
        searchWord: searchWord,
        searchField:"info",
        customerOrSupplier: $("#csType").val(),
        titles:"name,contact,mobile",
        uuid: dropList.getUUID()
    };
    var ajaxUrl = "searchInventoryIndex.do?method=getCustomerSupplierSuggestion";
    APP_BCGOGO.wjl.LazySearcher.lazySearch(ajaxUrl, ajaxData, function (result) {
        dropList.show({
            "selector":$domObject,
            "data":result,
            "onSelect":function (event, index, data) {
                $domObject.val(data.details.name);
                $domObject.css({"color":"#000000"});

                dropList.hide();
                if($("#csType").val()=="supplier") {
                    $("#supplier").attr("blurLock",true);
                    checkSupplierById(data.details.id);

                }
                if($("#csType").val()=="customer") {
                    $("#customer").attr("blurLock",true);
                    checkCustomerById(data.details.id);
                }

            }
        });
    });

}

function checkCustomerByIdAndContactId(customerId, contactId) {
    var ajaxUrl = "sale.do?method=searchCustomerById";
    var ajaxData = {
        shopId: $("#shopId").val(),
        customerId: customerId
    };
    bcgogoAjaxQuery.setUrlData(ajaxUrl, ajaxData);
    bcgogoAjaxQuery.ajaxQuery(function (data) {
        if (data.infos.length > 0) {
            //原始数据清空
            $("#customerId").val("");
            $("#customer").val("");
            $("#contact").val("");
            $("#mobile").val("");
            $("#hiddenMobile").val("");
            $("#returnInfo").val("");
            $("#landline").val("");
            $("#contactId").val(""); //add by zhuj

            $("#shortName").val("");
            $("#email").val("");
            $("#bank").val("");
            $("#account").val("");
            $("#accountName").val("");
            $("#qq").val("");
            $("#mobile").val("");
            $("#fax").val("");
            $("#address").val("");
            $("#settlementType").val("");
            $("#category").val("");
            $("#invoiceCategory").val("");

            $("#customerId").val(data.infos[0].idStr);
            $("#customer").val(data.infos[0].name);
            if (getOrderType() == "BORROW_ORDER") {
                $("#customerOrSupplierId").val(data.infos[0].idStr);
                $("#name").val(data.infos[0].name);
            }
            var isSameContact = false;
            if (G.Lang.isEmpty(contactId)) {
                var contactDTOs = data.infos[0].contacts;
                if (contactDTOs) {
                    for (var i = 0, len = contactDTOs.length; i < len; i++) {
                        if (contactDTOs[i] && !G.Lang.isEmpty(contactDTOs[i].idStr) && contactDTOs[i].idStr == contactId) {
                            isSameContact = true;
                            $("#contactId").val(contactDTOs[i].idStr);
                            $("#contact").val(G.normalize(contactDTOs[i].name));
                            $("#mobile").val(contactDTOs[i].mobile);
                            $("#email").val(G.normalize(contactDTOs[i].email));
                            $("#qq").val(G.normalize(contactDTOs[i].qq));
                            break;
                        }
                    }
                }
            }
            if (!isSameContact) {
                $("#contactId").val(G.Lang.normalize(data.infos[0].contactIdStr)); // add by zhuj
                $("#contact").val(data.infos[0].contact);
                $("#mobile").val(data.infos[0].mobile);
                $("#email").val(G.normalize(data.infos[0].email));
                $("#qq").val(G.normalize(data.infos[0].qq));
            }
            $("#landline").val(data.infos[0].landline);
            $("#hiddenMobile").val(data.infos[0].mobile);
            $("#shortName").val(G.normalize(data.infos[0].shortName));
            $("#bank").val(G.normalize(data.infos[0].bank));
            $("#account").val(G.normalize(data.infos[0].account));
            $("#mobile").val(G.normalize(data.infos[0].mobile));
            $("#fax").val(G.normalize(data.infos[0].fax));
            $("#address").val(G.normalize(data.infos[0].address));
            $("#accountName").val(G.normalize(data.infos[0].bankAccountName));
            $("#settlementType").val(G.normalize(data.infos[0].settlementType));
            $("#category").val(G.normalize(data.infos[0].customerKind));
            $("#invoiceCategory").val(G.normalize(data.infos[0].invoiceCategory));
            $("#returnInfo").remove();
        }
        contactDeal(!G.Lang.isEmpty($("#contactId").val()));
    }, function (XMLHttpRequest, error, errorThrown) {
        //原始数据清空
        $("#customerId").val("");
        $("#customer").val("");
        $("#contactId").val(""); // add by zhuj
        $("#contact").val("");
        $("#mobile").val("");
        $("#hiddenMobile").html("");
        $("#returnInfo").val("");
        $("#landline").val("");
        $("#shortName").val("");
        $("#email").val("");
        $("#bank").val("");
        $("#account").val("");
        $("#accountName").val("");
        $("#qq").val("");
        $("#fax").val("");
        $("#address").val("");
        $("#settlementType").val("");
        $("#category").val("");
        $("#invoiceCategory").val("");
        contactDeal(false);
    })
}