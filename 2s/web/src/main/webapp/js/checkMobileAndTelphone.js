(function () {
    window.check = {version:1.0};
    // 检查手机号码
    function checkMobile(mobile) {
        //先通过正则验证
        if(APP_BCGOGO.Validator.stringIsMobilePhoneNumber(mobile)){
            //如果是多个号码，再进行重复判断
            if(mobile.indexOf(",")!=-1){
                var mobileArray = mobile.split(",");
                for(var i=0;i<mobileArray.length-1;i++){
                    if(mobileArray[i]==(mobileArray[i+1])){
                        alert("存在相同的手机号，请确认和重新输入！");
                        return false;
    }
                }
            }
            return true;
        }else{
            return false;
        }
    }

    //验证座机号
    function checkTelephone(telephone) {
        //先通过正则验证
        if(APP_BCGOGO.Validator.stringIsTelephoneNumber(telephone)) {
            //如果是多个号码，再进行重复判断
            if(telephone.indexOf(",")!=-1){
                var telephoneArray = telephone.split(",");
                for(var i=0;i<telephoneArray.length-1;i++){
                    telephoneArray[i] = telephoneArray[i].split("-").join("").split(" ").join("");
                    telephoneArray[i+1] = telephoneArray[i+1].split("-").join("").split(" ").join("");
                    if(telephoneArray[i]==(telephoneArray[i+1])){
                        nsDialog.jAlert("存在相同的座机号，请确认和重新输入！");
                        return false;
    }
                }
            }
            return true;
        }else{
            return false;
        }
    }

//    手机号输入框：
    check.inputMobile = function (mobile, telephone) {
        //不符合mobile
        if (!checkMobile(mobile.value)) {
            if (telephone&&checkTelephone(mobile.value)) {
                if (confirm("输入的号码可能是座机号，要帮您修改成座机号吗？")) {
                    //清空手机栏位,将输入的放入座机号框内
                    telephone.value = mobile.value;
                    mobile.value = "";
                    return "yes";
                } else {
                    mobile.select();
                    mobile.focus();
                    return "no";
                    //否(停留在本输入框)
                }
            } else {
                nsDialog.jAlert("输入的手机号码可能不正确，为了不影响您的后续使用，请确认后重新输入！");
                mobile.select();
                mobile.focus();
                return "success";
            }
        }
    }

//    手机号输入后，要判断是否存在同名的手机号。已经实现功能如下（如果有，则提示已有XXX的手机号相同，是否修改原资料，是，则修改原资料。否则停留本页）
    check.inputCustomerMobileBlur = function (mobile, telephone) {
        //不符合mobile
        if (!checkMobile(mobile.value)) {
            if (telephone && checkTelephone(mobile.value)) {
                if (confirm("输入的号码可能是座机号，要帮您修改成座机号吗？")) {
                    //清空手机栏位,将输入的放入座机号框内
                    telephone.value = mobile.value;
                    mobile.value = "";
                    $(telephone).blur();
                    return false;
                } else {
                    //否(停留在本输入框)
                    mobile.select();
                    mobile.focus();
                    return false;
                }
            } else {
                nsDialog.jAlert("输入的手机号码可能不正确，为了不影响您的后续使用，请确认后重新输入！");
                mobile.value = "";
                mobile.select();
                mobile.focus();
                return false;
            }
        } else {
            //要判断是否存在同名的手机号
            var r = APP_BCGOGO.Net.syncGet({url:"customer.do?method=getCustomerJsonDataByMobile",
                data:{mobile: mobile.value, customerId:G.normalize($("#customerId").val())},dataType:"json"});
            if (!r.success)
                return true;
            var obj = r.data;
            if (r.msg == 'customer' && $("#customer").val() == obj.name) {
                $("#customerId").val(obj.idStr);
                getCustomerJsonToShow(obj);
            } else if(r.msg == 'customer' && $("#customerId").val() != obj.idStr) {
                if(isInProcessingRepairOrderPage()){
                    nsDialog.jAlert("与已存在客户【" + obj.name + "】的手机号相同，请重新输入！");
                    mobile.value = "";
                    return false;
                }
                if (confirm('与已存在客户【' + obj.name + '】的手机号相同，是否跳转到该客户页面')) {
                    getCustomerJsonToShow(obj);
                  window.location = "unitlink.do?method=customer&customerId=" + obj.idStr;
                  return true;
                } else {
                    mobile.value = "";
                    return false;
                }
            } else if(r.msg == 'supplier'){
                nsDialog.jAlert("与已存在供应商【" + obj.name + "】的手机号相同，请重新输入！");
                mobile.value = "";
                return false;
            }
        }
    }

    check.inputCustomerMobileBlur2 = function (mobile, telephone) {
        //不符合mobile
        if (!checkMobile(mobile.value)) {
            if (telephone && checkTelephone(mobile.value)) {
                if (confirm("输入的号码可能是座机号，要帮您修改成座机号吗？")) {
                    //清空手机栏位,将输入的放入座机号框内
                    telephone.value = mobile.value;
                    mobile.value = "";
                    $(telephone).blur();
                    return false;
                } else {
                    //否(停留在本输入框)
                    mobile.select();
                    mobile.focus();
                    return false;
                }
            } else {
                nsDialog.jAlert("输入的手机号码可能不正确，为了不影响您的后续使用，请确认后重新输入！");
                mobile.value = "";
                mobile.select();
                mobile.focus();
                return false;
            }
        } else {
            //要判断是否存在同名的手机号
            var r = APP_BCGOGO.Net.syncGet({url:"customer.do?method=getCustomersByMobile",
                data:{mobile: mobile.value},dataType:"json"});
            if (!r || !r.results || r.results.length == 0)
                return true;
            if(r.results.length == 1) {
                var customerDTO = r.results[0];
                if ($("#customer").val() == customerDTO.name) {
                    $("#customerId").val(customerDTO.idStr);
                    getCustomerJsonToShow(customerDTO)
                } else if($("#customerId").val() != customerDTO.idStr) {
                    if (confirm('与已存在客户【' + customerDTO.name + '】的手机号相同，是否使用原资料')) {
                        getCustomerJsonToShow(customerDTO);
                        return true;
                    } else {
                        mobile.value = "";
                        return false;
                    }
                }
            } else {
                for(var customerIndex in r.results) {
                    if($("#customerId").val() == r.results[customerIndex].idStr) {
                        return;
                    }
                }
                for (var customerIndex in r.results) {
                    var $sin_customer = $('<div class="sin_customer"><label class="rad"><input type="radio" name="sin_customer"/>'
                        + r.results[customerIndex].name +
                        '</label>&nbsp;&nbsp;<span>'
                        + G.normalize(r.results[customerIndex].contact, "")
                        + '</span>&nbsp;&nbsp;<span>'
                        + G.normalize(r.results[customerIndex].mobile, "")
                        + '</span><br>'
                        + '<input type="hidden" class="contactName" value="' + G.normalize(r.results[customerIndex].contact, "") + '"/>'
                        + '<input type="hidden" class="contactMobile" value="' + G.normalize(r.results[customerIndex].mobile, "") + '"/>'
                        + '<input type="hidden" class="contactId" value="' + G.normalize(r.results[customerIndex].contactIdStr, "") + '"/>'
                        + '<input type="hidden" class="customerId" value="' + G.normalize(r.results[customerIndex].idStr, "") + '"/>'
                        + '<input type="hidden" class="customerName" value="' + G.normalize(r.results[customerIndex].name, "") + '"/>'
                        + '</div>');
                    $sin_customer.insertAfter($("#mobileDupCustomers > div:last"));
                }

                $("#mobileDupTip").dialog({
                    resizable: false,
                    height:200,
                    width:400,
                    title: "友情提示",
                    modal: true,
                    closeOnEscape: false,
                    close: function () {
                        $(".sin_customer").remove();
                    }
                });

            }

        }
    }


    check.inputVehicleMobileBlur = function (mobile) {
        //不符合mobile
        if (!checkMobile(mobile.value)) {

                nsDialog.jAlert("输入的手机号码可能不正确，为了不影响您的后续使用，请确认后重新输入！");
                mobile.value = '';
                $(mobile).select();
                $(mobile).focus();
                return;

        } else {
            //要判断是否存在同名的车辆手机号
            var result = APP_BCGOGO.Net.syncGet({url:"customer.do?method=getCustomerJsonDataByVehicleMobile",
                data:{mobile: mobile.value},dataType:"json"});
            if(result.id != null && $("#customerId").val() != result.id) {

              nsDialog.jAlert('与已存在客户【' + result.name + '】下车主的手机号相同,请重新输入');
              $(mobile).select();
              $(mobile).focus();
              mobile.value = '';
              return;
            }
            //要判断是否存在同名的客户手机号
            var r = APP_BCGOGO.Net.syncGet({url:"customer.do?method=getCustomerJsonDataByMobile",
                data:{mobile: mobile.value, customerId:G.normalize($("#customerId").val())},dataType:"json"});
            if (!r.success)
                return;
            var obj = r.data;
            if (r.msg == 'customer' && $("#customer").val() == obj.name) {
                $("#customerId").val(obj.idStr);
            } else if(r.msg == 'customer' && $("#customerId").val() != obj.idStr) {
                nsDialog.jAlert('与已存在客户【' + obj.name + '】的手机号相同,请重新输入');
                $(mobile).select();
                $(mobile).focus();
                mobile.value = '';
            } else if(r.msg == 'supplier'){
                nsDialog.jAlert("与已存在供应商【" + obj.name + "】的手机号相同，请重新输入！");
                $(mobile).select();
                $(mobile).focus();
                mobile.value = '';
            }
        }
    };

    check.inputCustomerLandlineBlur = function (telephone) {
        //判断是否存在相同的座机号
        var r = APP_BCGOGO.Net.syncGet({url:"customer.do?method=getCustomerJsonDataByTelephone",
            data:{telephone: telephone.value},dataType:"json"});
        if (!r || !r.name) return;
        if (r.name == $("#customer").val()) {
            $("#customerId").val(r.idStr);
        }else {
            if (confirm('与已存在客户【' + r.name + '】的座机号相同，是否修改原资料')) {
                getCustomerJsonToShow(r);
                return;
            }
        }
    }

    //    手机号输入后，要判断是否存在同名的手机号。已经实现功能如下（如果有，则提示已有XXX的手机号相同，是否修改原资料，是，则修改原资料。否则停留本页）
    check.inputSupplierMobileBlur = function (mobile, telephone) {
        //不符合mobile
        if (!checkMobile(mobile.value)) {
            if (checkTelephone(mobile.value)) {
                nsDialog.jConfirm("输入的号码可能是座机号，要帮您修改成座机号吗？", null, function(returnVal){
                    if(returnVal){
                        //清空手机栏位,将输入的放入座机号框内
                        telephone.value = mobile.value;
                        mobile.value = "";
                        return;
                    }else{
                        //否(停留在本输入框)
                        return;
                    }
                });
            } else {
                nsDialog.jAlert("输入的手机号码可能不正确，为了不影响您的后续使用，请确认后重新输入！");
                return;
            }
        } else {
            //要判断是否存在同名的手机号
            var r = APP_BCGOGO.Net.syncGet({url:"customer.do?method=getSupplierJsonDataByMobile",
                data:{mobile: mobile.value},dataType:"json"});
            if (!r || !r.name) return;
            if ($("#supplier").val() == r.name) {
                $("#supplierId").val(r.idString);
            } else{
                nsDialog.jConfirm('与已存在供应商【' + r.name + '】的手机号相同，是否使用【' + r.name + '】的资料？', null, function(returnVal){
                    if(returnVal){
                        getSupplierJsonToShow(r);
                        return;
                    }else{
                        $("#mobile").val("");
                    }
                });
            }
        }
    }

    check.inputSupplierMobileBlur2 = function (mobile, telephone) {
        //不符合mobile
        if (!checkMobile(mobile.value)) {
            if (checkTelephone(mobile.value)) {
                nsDialog.jConfirm("输入的号码可能是座机号，要帮您修改成座机号吗？", null, function(returnVal){
                    if(returnVal){
                        //清空手机栏位,将输入的放入座机号框内
                        telephone.value = mobile.value;
                        mobile.value = "";
                        return;
                    }else{
                        //否(停留在本输入框)
                        mobile.value = "";
                        return;
                    }
                });
            } else {
                nsDialog.jAlert("输入的手机号码可能不正确，为了不影响您的后续使用，请确认后重新输入！");
                return;
            }
        } else {
            //要判断是否存在同名的手机号
            var r = APP_BCGOGO.Net.syncGet({url:"customer.do?method=getSuppliersByMobile",
                data:{mobile: mobile.value},dataType:"json"});
            if (!r || !r.results || r.results.length == 0) return;
            if(r.results.length == 1) {
                if ($("#supplier").val() == r.results[0].name) {
                    $("#supplierId").val(r.results[0].idString);
                } else{
                    nsDialog.jConfirm('与已存在供应商【' + r.results[0].name + '】的手机号相同，是否使用【' + r.results[0].name + '】的资料？', null, function(returnVal){
                        if(returnVal){
                            getSupplierJsonToShow(r.results[0]);
                            return;
                        }else{
                            $("#mobile").val("");
                        }
                    });
                }
            } else {
                for(var supplierIndex in r.results) {
                    if($("#supplierId").val() == r.results[supplierIndex].idStr) {
                        return;
                    }
                }
                for (var customerIndex in r.results) {
                    var $sin_supplier = $('<div class="sin_customer"><label class="rad"><input type="radio" name="sin_customer"/>'
                        + r.results[customerIndex].name +
                        '</label>&nbsp;&nbsp;<span>'
                        + G.normalize(r.results[customerIndex].contact, "")
                        + '</span>&nbsp;&nbsp;<span>'
                        + G.normalize(r.results[customerIndex].mobile, "")
                        + '</span><br>'
                        + '<input type="hidden" class="contactName" value="' + G.normalize(r.results[customerIndex].contact, "") + '"/>'
                        + '<input type="hidden" class="contactMobile" value="' + G.normalize(r.results[customerIndex].mobile, "") + '"/>'
                        + '<input type="hidden" class="contactId" value="' + G.normalize(r.results[customerIndex].contactIdStr, "") + '"/>'
                        + '<input type="hidden" class="customerId" value="' + G.normalize(r.results[customerIndex].idStr, "") + '"/>'
                        + '<input type="hidden" class="customerName" value="' + G.normalize(r.results[customerIndex].name, "") + '"/>'
                        + '</div>');
                    $sin_supplier.insertAfter($("#mobileDupCustomers > div:last"));
                }

                $("#mobileDupTip").dialog({
                    resizable: false,
                    height:200,
                    width:400,
                    title: "友情提示",
                    modal: true,
                    closeOnEscape: false,
                    close: function () {
                        $(".sin_customer").remove();
                    }
                });
            }

        }
    }


//    座机号输入框：
    check.inputTelephone = function (telephone, mobile) {
        //不符合电话号码
        if (!checkTelephone(telephone.value)) {
                nsDialog.jAlert("输入的座机号码可能不正确，为了不影响您的后续使用，请确认后重新输入！");
                telephone.select();
                telephone.focus();
                return "success";
            }
        }
//    手机号输入框：
    check.inputMobileBlur = function (mobile) {
        //不符合电话号码
        if (!checkMobile(mobile.value)) {
            nsDialog.jAlert("输入的手机号有误，请确认和重新输入！");
            mobile.value = "";
            mobile.select();
            mobile.focus();
            return;
        }
    }
    check.saveContact = function (contract) {
        if (!checkMobile(contract.value) && !checkTelephone(contract.value)) {
            nsDialog.jAlert("输入的联系方式有误，请确认和重新输入！");
            contract.select();
            contract.focus();
            return;
        }
    }

//  customer联系电话输入框blur事件：
    check.contactCustomer = function (contract,telephone) {
        $("#hiddenMobile").val("");
        //如果为手机号码
        if (checkMobile(contract.value)) {
            //作ajax,找出原来号码
            //判断此人是否已存过手机号
            //已存在原来的手机号1XXXXX,是否修改为新号码1XXXXX
            //要判断是否存在同名的手机号
            $("#hiddenMobile").val("mobile");
            var r = APP_BCGOGO.Net.syncGet({
                url:"customer.do?method=getCustomerJsonDataByMobile",
                data:{mobile: contract.value, customerId:$("#customerId").val()},
                dataType:"json"
            });
            if (!r.success) return;
            var obj = r.data;
            if (r.msg == 'customer' && obj.name == $("#customer").val()) {
                $("#customerId").val(obj.idStr);
            } else if(r.msg == 'customer') {
                if (confirm('已有【' + obj.name + '】的手机号相同，是否修改原资料')) {
                    getCustomerJsonToShow(obj);
                    return;
                } else {
                    contract.value = "";
                }
            } else if(r.msg == 'supplier'){
                nsDialog.jAlert("与已存在供应商【" + obj.name + "】的手机号相同，请重新输入！");
                contract.value = "";
            }
        } else if (checkTelephone(contract.value)) {
            // TODO 这里只是为了保证和之前需求一样的效果， 如果是座机电话，弹出告知用户是是否转为座机 ，其实这没实际用途，具体靠后台来自己判断是存手机还是存座机
            // TODO 这个是重构中 需要全局考虑的因素之一
            if(confirm("输入的号码可能是座机号，要帮您修改成座机号吗？")){
                $("#hiddenMobile").val("phone");
                telephone.value = contract.value;
                contract.value = "";
                //判断是否存在相同的座机号
                var r = APP_BCGOGO.Net.syncGet({url:"customer.do?method=getCustomerJsonDataByTelephone",
                    data:{telephone: telephone.value},dataType:"json"});
                if (!r || !r.name) return;
                if (r.name == $("#customer").val()) {
                    $("#customerId").val(r.idStr);
                }else {
                    if (confirm('与已存在客户【' + r.name + '】的座机号相同，是否修改原资料')) {
                        getCustomerJsonToShow(r);
                        return;
                    }
                    else {
                        contract.value = "";
                    }
                }
            }else{
                contract.select();
                contract.focus();
                $("#hiddenMobile").val("mobile");
            }
            /*
            if ($("#hiddenMobile").html() != contract.value) {
                //判断是否存在相同的座机号
                var r = APP_BCGOGO.Net.syncGet({url:"customer.do?method=getCustomerByTelephone",
                    data:{telephone: contract.value},dataType:"json"});
                if (r.length == 0) return;
                if (r.customer == $("#customer").val()) {
                    $("#customerId").val(r.customerIdStr);
                }
                else {
                    if (confirm('与已存在客户【' + r.customer + '】的手机号相同，是否修改原资料')) {
                        $("#customerId").val(r.customerIdStr);
                        return;
                    }
                    else {
                        contract.value = "";
                    }
                }
            }
            */
        }
        else {
            nsDialog.jAlert("输入的联系方式有误，请确认和重新输入！");
            contract.value = "";
            contract.select();
            contract.focus();
            return;
        }
    }

    //  supplier联系人手机号输入框blur事件：
    check.contactMobileSupplier = function (mobile) {
        //如果为手机号码
        if (checkMobile(mobile.value)) {
            //如果是绑定客户的供应商，则需判断新填的手机号是否与已有客户、供应商重复。非绑定客户的供应商不需判重
            var r = APP_BCGOGO.Net.syncGet({url:"customer.do?method=getCustomerJsonDataByMobile",
                data:{mobile: mobile.value, supplierId:$("#supplierId").val()},dataType:"json"});
            if (!r.success)
                return;
            var obj = r.data;
            if (r.msg == 'supplier' && $("#supplier").val() == obj.name) {
                $("#supplierId").val(obj.idStr);
            } else if(r.msg == 'customer') {
                nsDialog.jAlert('与已存在客户【' + obj.name + '】的手机号相同,请重新输入');
                $(mobile).select().focus().val("");
            } else if(r.msg == 'supplier' && $("#supplierId").val() != obj.idStr){
                nsDialog.jAlert("与已存在供应商【" + obj.name + "】的手机号相同，请重新输入！");
                $(mobile).select().focus().val("");
            }
        } else {
            nsDialog.jAlert("输入的联系方式有误，请确认和重新输入！");
            $(mobile).select().focus().val("");
            return;
        }
    };

    $(".sin_customer").live("click", function () {
        if($("#customer")[0]) {
            $("#customer").val($(this).find("input[class=customerName]").val());
            $("#customerId").val($(this).find("input[class=customerId]").val());
        }
        if($("#supplier")[0]) {
            $("#supplier").val($(this).find("input[class=customerName]").val());
            $("#supplierId").val($(this).find("input[class=customerId]").val());
        }
        $("#contact").val($(this).find("input[class=contactName]").val());
        $("#mobile").val($(this).find("input[class=contactMobile]").val());
        $("#contactId").val($(this).find("input[class=contactId]").val());

        if($("#contactId").val()){
            contactDeal(true); // @see suggestion.js
        }else{
            contactDeal(false); // @see suggestion.js
        }

    });

    $(".J_selectSure").live("click",function(){
        var oldChecked = false;
        $("#mobileDupCustomers input[name='sin_customer']").each(function () {
            if ($(this).attr("checked")) {
                oldChecked = true;
            }
        });
        if (!oldChecked) {
            nsDialog.jAlert("请在客户列表中选择！");
            return;
        }
        $(".sin_customer").remove();
        $("#mobileDupTip").dialog("close");
    });
})();


function getSupplierJsonToShow(jsonData){
    $("#supplierId").val(jsonData.idString);
    $("#supplier").val(jsonData.name);
    $("#contactId").val(jsonData.contactIdStr);
    $("#contact").val(jsonData.contact);
    $("#mobile").val(jsonData.mobile);
    if(!G.isEmpty(jsonData.contactId)){
        if(!G.isEmpty(jsonData.contact)){
            $("#contact").attr("readonly", true);
            $("#mobile").attr("readonly", true);
            $("#email").attr("readonly", true);
            $("#qq").attr("readonly", true);
        }
    }
    $("#hiddenMobile").html(jsonData.mobile);
    $("#bank").val(jsonData.bank);
    $("#account").val(jsonData.account);
    $("#accountName").val(jsonData.accountName);
    if(!jsonData.businessScope) {
        $("#businessScope").val("");
    } else {
        var scopes = jsonData.businessScope.split(",");
        for(var i= 0;i < scopes.length;i++) {
            $(".divTit :checkbox[name=businessScope]").each(function(index,checkbox){
                if($(checkbox).val() == scopes[i]) {
                    $(checkbox).attr("checked",true);
                    return false;
                }  else {
                    if(index == $(".divTit :checkbox[name=businessScope]").length - 1) {
                        if(i == scopes.length - 1) {
                            $("#otherCheckbox").val($("#otherInput").val() + scopes[i]);
                            $("#otherCheckbox").attr("checked",true);
                            $("#otherInput").val($("#otherInput").val() + scopes[i]);
                        } else {
                            $("#otherCheckbox").val($("#otherInput").val() + scopes[i] + ',');
                            $("#otherCheckbox").attr("checked",true);
                            $("#otherInput").val($("#otherInput").val() + scopes[i] + ',');
                        }

                    }

                }
            });
        }
    }
    $("#category").val(jsonData.category);
    $("#abbr").val(jsonData.abbr);
    $("#settlementType").val(jsonData.settlementTypeId);
    $("#landline").val(jsonData.landLine);
    $("#fax").val(jsonData.fax);
    $("#qq").val(jsonData.qq);
    $("#invoiceCategory").val(jsonData.invoiceCategoryId);
    $("#email").val(jsonData.email);
    if($("#select_province")[0]){
        if(jsonData.province!=null){
            $("#select_province").val(jsonData.province);
            cityBind($("#select_province")[0]);
        }
        if(jsonData.city!= null){
            $("#select_city").val(jsonData.city);
            townshipBind($("#select_city")[0]);
        }
        if(jsonData.region!=null){
            $("#select_township").val(jsonData.region);
        }
        $("#input_address").val(jsonData.address);
    }
    $("#address").val(jsonData.address);
    $("#memo").text(jsonData.memo);
    if($("#isCustomer")[0]){        //同时是供应商
        if (jsonData.customerId) {
            $("#isCustomer").attr('checked', true).attr("disabled", true);
        } else {
            $("#isCustomer").attr("checked", false).attr("disabled", false);
        }
    }
    if (getOrderType() == "PURCHASE" || getOrderType() == "INVENTORY" || getOrderType() == "RETURN") {
        if (jsonData.totalDebt) {
            $("#receivable").html(dataTransition.rounding(jsonData.totalDebt, 2));
        } else {
            $("#receivable").html("0");
        }
        if (jsonData.totalPayable) {
            $("#payable").html(dataTransition.rounding(jsonData.totalPayable, 2));
        } else {
            $("#payable").html("0");
        }

        initDuiZhanInfo();
    }

}

function getCustomerJsonToShow(jsonData) {
    $("#customerId").val(jsonData.idStr);
    $("#customer,#name").val(jsonData.name);
    $("#contact").val(jsonData.contact);
    $("#contactId").val(jsonData.contactIdStr); // add by zhuj
    $("#isAdd").val("false"); // add by zhuj
    $("#mobile").val(jsonData.mobile);
    $("#hiddenMobile").val(jsonData.mobile);
    $("#email").val(jsonData.email);
    $("#qq").val(jsonData.qq);
    $("#shortName").val(jsonData.shortName);
    $("#fax").val(jsonData.fax);
    if(jsonData.customerKind!=null){
        $("#customerKind").val(jsonData.customerKind);
    }
    $("#birthdayString").val(jsonData.birthdayString);
    $("#returnInfo").remove();
    $("#receivable").html(jsonData.totalReceivable);
    $("#customerConsume").html(jsonData.totalConsume);
    $("#landline,#phone").val(jsonData.landLine);
    $("#bank").val(jsonData.bank);
    $("#bankAccountName").val(jsonData.bankAccountName);
    $("#account").val(jsonData.account);
    $("#settlementType").val(jsonData.settlementType);
    $("#invoiceCategory").val(jsonData.invoiceCategory);
    $("#memo").val(jsonData.memo);
    if($("#select_province")[0]){
        if (jsonData.province != null) {
            $("#select_province").val(jsonData.province);
            cityBind($("#select_province")[0]);
        }
        if (jsonData.city != null) {
            $("#select_city").val(jsonData.city);
            townshipBind($("#select_city")[0]);
        }
        if (jsonData.region != null) {
            $("#select_township").val(jsonData.region);
        }
    }
    $("#address,#input_address").val(jsonData.address);
    if($("#isSupplier")[0]){        //同时是供应商
        if(jsonData.supplierId){
            $("#isSupplier").attr('checked', true).attr("disabled", true);;
        }else{
            $("#isSupplier").attr("checked", false).attr("disabled", false);
        }
    }
    if (getOrderType() == 'SALE' || getOrderType() == 'SALE_RETURN') {
        if (jsonData.totalReceivable) {
            $("#receivable").html(dataTransition.rounding(jsonData.totalReceivable, 2));
        } else {
            $("#receivable").html("0");
        }
        if (jsonData.totalReturnDebt) {
            $("#payable").html(dataTransition.rounding(jsonData.totalReturnDebt, 2));
        } else {
            $("#payable").html("0");
        }

        initDuiZhanInfo();
        isReadOnly();
    } else if (getOrderType() == 'REPAIR') {
        ajaxGetCustomerInfo(jsonData.idStr);
    } else if (getOrderType() == 'WASH_BEAUTY') {
        var receiptNo = null;
        if(document.getElementsByName("receiptNo")[0]) {
            receiptNo = document.getElementsByName("receiptNo")[0].value;
        }
        if (null == receiptNo) {
            receiptNo = "";
        }
        window.location = "washBeauty.do?method=getCustomerInfoByName" + "&customerId=" + jsonData.idStr + "&licenceNo=" + encodeURIComponent($("#licenceNo").val()) + "&brand=" + encodeURIComponent($("#brand").val()) + "&model=" + encodeURIComponent($("#model").val()) + "&receiptNo=" + receiptNo;
    }
}

function isReadOnly() {
    var infoNameList = ["contact","mobile"];
    for (var i = 0,len = infoNameList.length; i < len; i++) {
        $("#" + infoNameList[i]).attr("readonly", $.trim($("#" + infoNameList[i]).val()) != "");
    }
}

//是否处在进行中的施工单据
function isInProcessingRepairOrderPage(){
    if(getOrderType() == "REPAIR" && !G.isEmpty($("#id").val())){
        return true;
    }else if(getOrderType() == 'clientInfo'){
        if($("#orderType", window.parent.document).val() == 'repairOrder' && !G.isEmpty($("#id").val())){
            return true;
        }else{
            return false;
        }
    }
    return false;
}
