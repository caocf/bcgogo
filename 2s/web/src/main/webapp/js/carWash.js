/**
 * Created by IntelliJ IDEA.
 * User: zhoudongming
 * Date: 12-7-18
 * Time: 下午3:37
 * To change this template use File | Settings | File Templates.
 */
var selectItemNumCustomer = -1;
var selectmoreCustomer = -1;
var domTitle;
var selectValueCustomer = '';
var selectItemNum = -1;
var selectmore = -1;
var selectValue = '';
var member_select_value = '';
var timeOutId;

$(document).ready(function() {
    //手动输入客户名称后，自动带出其他信息
    //    $("#customer").blur(function() {
    //        if (this.value && $("#customerId").val()==""){
    //            APP_BCGOGO.Net.asyncPost({
    //                url:"customer.do?method=searchCustomerByName",
    //                data:{
    //                    customerName:this.value
    //                },
    //                cache:false,
    //                dataType:"json",
    //                success:function(jsonStr) {
    //                    if (jsonStr.infos[0] != null && jsonStr.infos[0] != "[]") {
    //                        var customerId = jsonStr.infos[0].idStr
    //                        var mobile = jsonStr.infos[0].mobile;
    //                        var landline = jsonStr.infos[0].landline;
    //                        $("#customerId").val(customerId);
    //                        $("#mobile").val(mobile);
    //                        $("#landLine").val(landline);
    //                    }
    //                }
    //            });
    //        }
    //    });
    initDuiZhanInfo();
    $(".businessCategoryName").each(function() {
        var prefix = this.id.split(".")[0];
        var serviceId = $("#" + prefix + "\\.serviceId").val();

        if ($(this).val() == "洗车" || $(this).val() == "美容") {
            $(this).attr("disabled", "disabled");
        } else {
            $(this).removeAttr("disabled");
        }

        if (!$(this).val()) {
            APP_BCGOGO.Net.syncPost({
                url: "category.do?method=getCategoryByServiceId",
                data: {
                    "serviceId": serviceId,
                    "now": new Date()
                },
                dataType: "json",
                success: function(jsonObject) {
                    if (null == jsonObject) {
                        $("#" + prefix + "\\.businessCategoryId").val("");
                        $("#" + prefix + "\\.businessCategoryName").val("");
                        $("#" + prefix + "\\.businessCategoryName").removeAttr("disabled");
                        $("#" + prefix + "\\.businessCategoryName").removeAttr("hiddenValue");
                    } else {
                        var id = null == jsonObject.data ? "" : jsonObject.data.idStr;
                        var name = null == jsonObject.data ? "" : jsonObject.data.categoryName;

                        $("#" + prefix + "\\.businessCategoryId").val(id);
                        $("#" + prefix + "\\.businessCategoryName").val(name);
                        $("#" + prefix + "\\.businessCategoryName").attr("hiddenValue", name);

                        if (name == "洗车" || name == "美容") {
                            $("#" + prefix + "\\.businessCategoryName").attr("disabled", "disabled");
                        } else {
                            $("#" + prefix + "\\.businessCategoryName").removeAttr("disabled");
                        }
                    }

                }
            });
        }
    });

    $("#vehicleMobile").live("keyup", function() {
        $(this).val($(this).val().replace(/[^\d]+/g, ""));
    });

    $("#vehicleMobile").live("blur", function() {
        if (this.value != '') {
            var vehicleMobile = document.getElementById('vehicleMobile');
            check.inputVehicleMobileBlur(vehicleMobile);
        }
    });

    $(document).click(function(e) {
        var e = e || event;
        var target = e.srcElement || e.target;
        if (!target || !target.id || (target.type != "text" && target.id != "div_works")) {
            $("#div_works").hide();
        }
    });

    $(":text").live("focus", function(e) {
        $("#div_works").hide();
        $(this).attr("autocomplete", "off");
    });


    $("#licenceNo").keyup(function() {
        $("#licenceNo").val($("#licenceNo").val().toUpperCase().replace(/[^A-Z\d\u4e00-\u9fa5\-_\(\)]+/g, ""));
    }).focus(function(){
            $(this).attr("lastVal",$(this).val());
        });

    $("#memberCardId").click(function() {
        bcgogo.checksession({
            "parentWindow": window.parent,
            "iframe_PopupBox": $("#iframe_buyCard")[0],
            "src": "member.do?method=buyCard&customerId=" + $("#customerId").val() + "&cardId=" + $("#memberCardId").val() + "&time=" + new Date()
        });
    });

//    if ($("#id").val() != ""){
//        $("#washBeautyOrderForm input").not($("#washDebtAccount,#carwashBtn,#printBtn,#doMemberCard,#memberCardId,#isPrint,#nullifyBtn,#copyInput")).each(function() {
//            $(this).attr("disabled", "disabled");
//        });
//        $("#washBeautyOrderForm select").each(function() {
//            $(this).attr("disabled", "disabled");
//        });
//        $("#nullifyBtn,#copyInput,#washDebtAccount").each(function() {
//            $(this).removeAttr("disabled");
//        });
//    }


    $("#printBtn").bind("click", function() {
        if ($("#id").val()) {
            window.showModalDialog("washBeauty.do?method=printWashBeautyTicket&orderId=" + $("#id").val() + "&now=" + new Date(), '', "dialogWidth=800px;dialogHeight=768px");
        } else {
            if (!$("#licenceNo").val() && !$("#customer").val()) {
                nsDialog.jAlert("请输入车牌或客户名！");
                return;
            }
            var url = "washBeauty.do?method=printWashBeautyNoId";
            $("#customer").removeAttr("disabled");
            $("#washBeautyOrderForm").ajaxSubmit({
                url:url,
                data:{
                    now:new Date(),
                    vehicle:$("#licenceNo").val()
                },
//                dataType: "json",
                type: "POST",
                success:function(data) {
                    $("#customer").attr("disabled", "diabled");
                    if (!data) return;
                    var printWin = window.open("", "", "width=1024,height=768");
                    with (printWin.document) {
                        open("text/html", "replace");
                        write(data);
                        close();
                    }
                },
                error:function() {
                    $("#customer").attr("disabled", "diabled");
                }
            })
        }
    });

    if ($("#id").val() && $("#isPrint").val() == "true") {
        $("#printBtn").click();
    }

    var elementCustomer = $("#customer")[0];
    var elementCarNo = $("#licenceNo")[0];
    $("#a_jiesuan").bind("click", function(e) {
        bcgogo.checksession({
            "parentWindow": window.parent,
            "iframe_PopupBox": $("#iframe_qiankuan")[0],
            "src": "arrears.do?method=toReceivableSettle&customerId=" + $("#customerId").val()
        });
    });

    $(".i_clickClient").bind("click", function(e) {
        bcgogo.checksession({
            "parentWindow": window.parent,
            "iframe_PopupBox": $("#iframe_PopupBox")[0],
            "src": encodeURI("txn.do?method=clientInfo&customer=" + $("#customer").val()
                + "&mobile=" + $("#mobile").val() + "&customerId=" + $("#customerId").val()
                + "&contact=" + $("#contact").val() + "&landLine=" + $("#landLine").val()
                + "&licenceNo=" + $("#licenceNo").val() + "&vehicleContact=" + $("#vehicleContact").val()
                + "&vehicleMobile=" + $("#vehicleMobile").val() + "&brand=" + $("#brand").val()
                + "&model=" + $("#model").val()) + '&color=' + $('#vehicleColor').val()
                + '&chassisNumber=' + $('#vehicleChassisNo').val() + '&engineNo=' + $('#vehicleEngineNo').val()
        });
    });

    $("#doMemberCard").bind("click", function(e) {
        setTimeout(function() {
            if ($("#doMemberCard").attr("disabled")) {
                return;
            }
            $("#doMemberCard").attr("disabled", true);
            var statusFlag = true;
            if ($("#customerId").val()) {
                var result = APP_BCGOGO.Net.syncGet({
                    async: false,
                    url: "customer.do?method=checkCustomerStatus",
                    data: {
                        customerId: $("#customerId").val(),
                        now: new Date()
                    },
                    dataType: "json"
                });
                if (!result.success) {
                    statusFlag = false;
                }
            }
            if (!statusFlag) {
                alert("此客户已被删除或合并，不能购卡！");
                $("#doMemberCard").removeAttr("disabled");
                return;
            }
            if (!$("#customerId").val() && !$("#customer").val()) {
                alert("请填写客户信息");
                $("#doMemberCard").removeAttr("disabled");
                return;
            }
            bcgogo.checksession({
                "parentWindow": window.parent,
                'iframe_PopupBox': $("#iframe_CardList")[0],
                'src': 'member.do?method=selectCardList&time=' + new Date()
            });
        }, 50)
    });

    $("#licenceNo").bind("keyup", function(e) {
//        if (!checkKeyUp(this, e)) {
//            return;
//        }
        var keycode = e.which || e.keyCode;
        webChangelicenceNo(this, keycode);
    }).bind("click", function(e) {
            var keycode = e.which || e.keyCode;
            var customerId = $("#customerId").val();
            if (GLOBAL.Lang.isEmpty(customerId)) {
                webChangelicenceNo(this, keycode);
            } else {
                searchLicenceNoByCustomerId(this, customerId, false);
            }
        });

    $("#duizhan").bind("click", function() {
        toCreateStatementOrder($("#customerId").val(), "CUSTOMER_STATEMENT_ACCOUNT");
    });


    function webChangelicenceNo(thisObj, keycode) {
        if (elementCarNo.value == '' || elementCarNo.value == null) {
            $("#div_brandvehiclelicenceNo").css({
                'display': 'none'
            });
        } else {
            elementCarNo.value = elementCarNo.value.replace(/[\ |\\]/g, "");
            searchSuggestionlicenceNo(thisObj, elementCarNo.value, "notclick", keycode);
        }
    }

    $("#customer").live("keyup", function(e) {
        if (!checkKeyUp(this, e)) {
            return;
        }
        var keycode = e.which || e.keyCode;
        webChangeCustomer(this, keycode);
    });

    function webChangeCustomer(thisObj, keycode) {
        if (elementCustomer.value === '' || elementCustomer.value === null || elementCustomer === undefined) {
            $("#div_brand").css({
                'display': 'none'
            });
        } else {
            elementCustomer.value = elementCustomer.value.replace(/[\ |\\]/g, "");
            searchSuggestionCustomer(thisObj, elementCustomer.value, "notclick", keycode);
        }

    }

//    $("#customer").bind("change", function(){
//        if (!selectValueCustomer) {
//            window.location = "washBeauty.do?method=getCustomerInfoByLicenceNo&licenceNo=" + encodeURIComponent($("#licenceNo").val()) + "&customer=" + encodeURIComponent($("#customer").val()) + "&brand=" + encodeURIComponent($("#brand").val()) + "&model=" + encodeURIComponent($("#model").val()) + "&mobile=" + encodeURIComponent($("#mobile").val()) + "&landLine=" + encodeURIComponent($("#landLine").val()) + "&type=customer" + "&vehicleContact=" + encodeURIComponent($("#vehicleContact").val()) + "&vehicleMobile=" + encodeURIComponent($("#vehicleMobile").val()) + '&vehicleColor=' + encodeURIComponent($('#vehicleColor').val()) + '&vehicleEngineNo=' + $('#vehicleEngineNo').val() + '&vehicleChassisNo=' + $('#vehicleChassisNo').val();
//        }
//    });

    $("#mobile").bind("blur", function() {
        var landline = document.getElementById("landLine");
        var mobile = document.getElementById("mobile");
        if (mobile.value) {
            check.inputCustomerMobileBlur(mobile, landline);
        }
    });

    $("#landLine").blur(function() {
        if (this.value) {
            check.inputCustomerLandlineBlur($("#landLine")[0]);
        }
    });

    $("#carwashBtn").bind('click', function() {
        function _deleteEmptyServiceItem(){
            if($(".item").size()>1){
                $(".item").each(function(){
                    var serviceName=$(this).find('[name$="serviceName"]').val();
                    if(G.isEmpty(serviceName)){
                        $(this).remove();
                    }
                });
            }
            isShowAddButton();
        }

        if ($(this).attr("disabled")) {
            return;
        }
        _deleteEmptyServiceItem();
        if ($("#licenceNo").val() == "") {
            alert("请先填写车牌号");
            return false;
        }
        if (G.isNotEmpty($("#customerId").val())) {
            var r = APP_BCGOGO.Net.syncGet({
                async: false,
                url: "customer.do?method=checkCustomerStatus",
                data: {
                    customerId: $("#customerId").val(),
                    now: new Date()
                },
                dataType: "json"
            });
            if (!r.success) {
                alert("此客户已被删除或合并，不能做单，请更改客户！");
                return;
            }
        }else{
          $("#customerId").val("");
        }
//        $("#total").val(($("#total").val() * 1).toFixed(2))
//        $("#total").val($("#total").val() * 1);

//        var result = true;
//        $("input[id$='couponNo'],input[id$='couponType']").each(function() {
//            if ($(this).css("display") != "none" && $(this).val() == '') {
//                if ($(this).attr("id").contains("couponNo")) {
//                    nsDialog.jAlert("消费券号不能为空");
//                } else {
//                    nsDialog.jAlert("消费券类型不能为空");
//                }
//                result = false;
//                return false;
//            }
//        });
//        if (!result) {
//            return;
//        }
        var errorStr="";
        $(".item").each(function(i){
            var errorItem="";
            var serviceId=$(this).find('[id$=".serviceId"]').val();
            var consumeType=$(this).find('[id$=".consumeTypeStr"]').val();
            var businessCategoryName=$(this).find('[id$=".businessCategoryName"]').val();
            var salesMan=$(this).find('[id$=".salesMan"]').val();
            var price=$(this).find('[id$=".price"]').val();
            if(G.isEmpty(serviceId)){
                  errorItem='第'+(i+1)+'行,'+ "服务不存在，请删除。";
            }else{
                if(consumeType=="MONEY"){

                }else if(consumeType=="TIMES"){

                }else if(consumeType=="COUPON"){
                    var couponType=$(this).find('[id$=".couponType"]').val();
                    var couponNo=$(this).find('[id$=".couponNo"]').val();
                    if(G.isEmpty(couponType)){
                        errorItem+="请输入消费券类型。";
                    }
                    if(G.isEmpty(couponNo)){
                        errorItem+="请输入消费券号。";
                    }
                    if(!G.isEmpty(errorItem)){
                        errorItem='第'+(i+1)+'行,'+ errorItem;
                    }
                }
            }
            errorStr+=errorItem;
        });
        if(!G.isEmpty(errorStr)){
            nsDialog.jAlert(errorStr);
            return false;
        }
//        $(".item").each(function(i){
//            if(G.isEmpty($(this).find('[id$=".serviceId"]').val())){
//                errorStr+="第"+i+"行,"+ "无洗车美容相关服务，请至“设定施工项目”新増“洗车”类或“美容”类施工项目。<br/>";
//            }
//        });
        if (checkServiceItems()) {
            alert('服务内容有重复项目，请修改或删除。');
            return false;
        }
        if ($("#licenceNo").val() && !$("#customer").val()) { // 客户未填，以车牌号为客户名
            $("#customer").val($("#licenceNo").val());
        }
        bcgogo.checksession({
            "parentWindow": window.parent,
            'iframe_PopupBox': $("#iframe_PopupBox_account")[0],
            'src': 'washBeauty.do?method=washBeautyAccount&customerId=' + $("#customerId").val()
        });
        washBeautyAccountPopAdjust();
    });

    function washBeautyAccountPopAdjust() {
        $("#iframe_PopupBox_account").css("top", "250px");
    };

    $(".opera1").live('click', function(event) { //删除行
        var target = event.target;
        var bottom= $(target).closest("tr").next();
        $(target).closest("tr").remove();
        bottom.remove();
        isShowAddButton();
        setTotal();
//            trCount = $(".item").size();
    });

    $(".opera2").live('click', function() { //增加行
        if(checkWashBeautyOrderAdd()){
            washBeautyOrderAdd();
            setTotal();
        }
    });

    $("select[id$='.consumeTypeStr']").each(function() {
        if ($(this).val() != null) {
            if ($(this).val() == "MONEY") {
                $(this).parents("tr").find("input[id$='price']").show();
                $(this).siblings("span[class='j_remainTimes']").hide();
                $(this).parents("tr").find("input[id$='couponType'], input[id$='couponNo']").hide();
            }
            if ($(this).val() == "TIMES") {
                $(this).parents("tr").find("input[id$='price']").hide();
                $(this).siblings("span[class='j_remainTimes']").show();
                //TODO: hide money value.
                $(this).parents("tr").find("input[id$='couponType'], input[id$='couponNo']").hide();
            }
            if ($(this).val() == "COUPON") {
                $(this).parents("tr").find("input[id$='price']").hide();
                $(this).siblings("span[class='j_remainTimes']").hide();
                $(this).parents("tr").find("input[id$='couponType'], input[id$='couponNo']").show();
            }
            $("select[name=" + $(this).attr("name") + "] option[value=" + $(this).val() + "]").checked;
        }
    });

    $("input[id$='couponType']").live("click keyup", function(event) {
        droplistLite.show({
            event: event,
            id: "id",
            keyword: "keyWord",
            data: "washBeauty.do?method=queryCouponType",
            name: "key"
        });
    });

    $("input[id$='.price']").live("blur change", function (e) {
        setTotal();
    });

    $("input[id$='.price']").live("keyup", function(e) {
        jQuery(this).val(APP_BCGOGO.StringFilter.inputtingPriceFilter(this.value, 2));
    });

//    if ($("#id").val() == "") {
//        $(".opera1").live('click', function() {
//            var trCount = $(".stock_bottom").size();
//            if (trCount == 1) {
//                return false;
//            }
//            if ($(this).parent().next().next().children().eq(0).val() == "MONEY") {
//                var idStr = $(this).attr("id").split(".");
//                var deletePrice = parseFloat($("#" + idStr[0] + "\\.price").val());
//                deletePrice = (null == deletePrice || "" == deletePrice || isNaN(deletePrice)) ? 0 : deletePrice;
//                var total = parseFloat($("#total").val()) - deletePrice * 1;
//                $("#total").val(total);
//                $("#total_span").text(total);
//            }
//
//            var This = this;
//            setTimeout(function() {
//                $(This).parent().parent().remove();
//            }, 200);
//        });
//    }
    //检查输入中的非法字符

    function checkChar(InString) {
        for (Count = 0; Count < InString.length; Count++) {
            TempChar = InString.substring(Count, Count + 1);
            if (!checkshuzi(TempChar) && !checkzimu(TempChar) && !checkhanzi(TempChar)) {
                return(true);
            }
        }
        return(false);
    }

    //判断数字

    function checkshuzi(shuziString) {
        var shuzi = shuziString.match(/\d/g);
        if (shuzi == null) return(false);
        else return(true);
    }

    //判断字母

    function checkzimu(zimuString) {
        var zimu = zimuString.match(/[a-z]/ig);
        if (zimu == null) return(false);
        else return(true);
    }

    //判断汉字

    function checkhanzi(hanziString) {
        var hanzi = hanziString.match(/[^ -~]/g);
        if (hanzi == null) return(false);
        else return(true);
    }

    //回车处理
    $("#licenceNo").keydown(function(e) {
        var e = e || event;
        var eventKeyCode = e.witch || e.keyCode;
        if (eventKeyCode == 13) {
            $(this).unbind("blur");
            var This = this;
            setTimeout(function() {
                $(This).bind('blur', elementCarNuBlurHandler);
            }, 1000);
            if (selectValue == '') {
                if ($("#licenceNo").val()) {
                    var nameValue = $('#licenceNo').val();
                    // 去除 空格 和 "-"
                    var ResultStr = nameValue.replace(/\s|\-/g, "");
                    if (!APP_BCGOGO.Validator.stringIsLicensePlateNumber(ResultStr)) {
                        alert("输入的车牌号码不符合规范，请检查！");
                        return;
                    }
                    if (checkChar(ResultStr)) {
                        alert("输入的车牌号码不符合规范，请检查！");
                        return;
                    } else if ($('#licenceNo').val() != $('#licenceNo').defaultValue) {
                        var nameValue = ResultStr;
                        //如果首字符是汉字
                        if (checkhanzi(nameValue.substring(0, 1))) {
                            if (checkzimu(nameValue.substring(1, 2)) && (nameValue.substring(2).length == 5 || nameValue.substring(2).length == 6) && !checkhanzi(nameValue.substring(2))) {
                                getCustomerInfoByLicenceNo(nameValue);
                            } else {
                                //                                alert("输入的车牌号码不符合规范，请检查！");
                            }
                        } else if (checkzimu(nameValue.substring(0, 1))) {
//                            if(checkzimu(ResultStr.substring(1, 2)) && (ResultStr.length == 7 || ResultStr.length == 8) && !checkhanzi(ResultStr.substring(2))) {
//                                var r = APP_BCGOGO.Net.syncPost({
//                                    url: "product.do?method=sevenOrEightLicenseNo&plateValue=" + ResultStr
//                                });
//                                if(r === null) {
//                                    return;
//                                } else {
//                                    var locaono = r[0].carno;
//                                    $("#licenceNo").val(locaono);
//                                    redirectToGetVehicleInfo($("#licenceNo").val());
//                                    locaono = '';
//                                }
//                            } else if(checkzimu(ResultStr.substring(1, 2)) && ResultStr.length > 9 && !checkhanzi(ResultStr.substring(2))) {
//                                redirectToGetVehicleInfo(ResultStr);
//                            } else
                            if (!checkhanzi(nameValue) && (nameValue.length == 5)) { //前缀
                                var r;
                                APP_BCGOGO.Net.syncGet({
                                    url: "product.do?method=userLicenseNo",
                                    dataType: "json",
                                    success: function (json) {
                                        r = json;
                                    }
                                });
                                if (r === null) {
                                    return;
                                } else {
                                    var locaono = r[0].localCarNo;
                                    $("#licenceNo").val((locaono + $("#licenceNo").val()).toUpperCase());
                                    locaono = '';
                                }
                            }
//                              else {
//                                //                                alert("输入的车牌号码不符合规范，请检查！");
//                            }
                            getCustomerInfoByLicenceNo($("#licenceNo").val());
                        } else {
                            if (nameValue.length <= 4 || nameValue.length >= 7) {
                                //                                alert("输入的车牌号码不符合规范，请检查！");
                            } else {
                                //5,6为数字的判断是否有汉字
                                if (!checkhanzi(nameValue)) {
                                    //添加前缀
                                    var r;
                                    APP_BCGOGO.Net.syncGet({
                                        url: "product.do?method=userLicenseNo",
                                        dataType: "json",
                                        success: function(json) {
                                            r = json;
                                        }
                                    });
                                    if (r === null) {
                                        return;
                                    } else {
                                        var locaono = r[0].localCarNo;
                                        $("#licenceNo").val(locaono + $("#licenceNo").val());
                                        getCustomerInfoByLicenceNo($("#licenceNo").val());
                                        locaono = '';
                                    }
                                } else {
                                    //                                    alert("输入的车牌号码不符合规范，请检查！");
                                }
                            }
                        }    //
                    } else {
                        window.location.assign('customer.do?method=carindex');
                    }
                    getCustomerInfoByLicenceNo($("#licenceNo").val());
                }
            } else {
                getCustomerInfoByLicenceNo(selectValue);
                elementCarNo.value = selectValue;
                selectValue = "";
            }
            $("#div_brandvehiclelicenceNo").css({
                'display': 'none'
            });
            selectItemNum = -1;
        }
    });

    function elementCarNuBlurHandler() {
        if(G.isEmpty($("#licenceNo").val())){
            initWashBeautyOrder("");   //清空
            return;
        }
        if (selectValue != "") {
            return;
        } else {
            $("#div_brandvehiclelicenceNo").css({
                'display': 'none'
            });
            var nameValue = $("#licenceNo").val();
            if (nameValue == null || nameValue == '') {
                return;
            }
            if(nameValue ==  $("#licenceNo").attr("lastVal")){
                return;
            }
            // 去除 空格 和 "-"
            var resultStr = nameValue.replace(/\s|\-/g, "");
            if (!APP_BCGOGO.Validator.stringIsLicensePlateNumber(resultStr)) {
                alert("输入的车牌号码不符合规范，请检查！");
                return;
            }
            if ($("#licenceNo") != null || $("#licenceNo") != '') {
                if (checkChar(resultStr)) {
                    alert("输入的车牌号码不符合规范，请检查！");
                }else if (checkhanzi(resultStr.substring(0, 1))) {
                    if (checkzimu(resultStr.substring(1, 2)) && (resultStr.substring(2).length == 5 || resultStr.substring(2).length == 6) && !checkhanzi(resultStr.substring(2))) {
                        getCustomerInfoByLicenceNo(resultStr);
                    } else {
                        //                        alert("输入的车牌号码不符合规范，请检查！");
                    }
                }else if (checkzimu(resultStr.substring(0, 1))) {
                    if (!checkhanzi(nameValue) && (nameValue.length == 5)) { //前缀
                        var r;
                        APP_BCGOGO.Net.syncGet({
                            url: "product.do?method=userLicenseNo",
                            dataType: "json",
                            success: function (json) {
                                r = json;
                            }
                        });
                        if (r === null) {
                            return;
                        } else {
                            var locaono = r[0].localCarNo;
                            $("#licenceNo").val((locaono + $("#licenceNo").val()).toUpperCase());
                            locaono = '';
                        }
                    }
                    getCustomerInfoByLicenceNo($("#licenceNo").val());
//                    } else {
//                        //                        alert("输入的车牌号码不符合规范，请检查！");
//                    }
                } else {
                    if (nameValue.length <= 4 || nameValue.length >= 10) {
                        //                        alert("输入的车牌号码不符合规范，请检查！");
                    } else {
                        //5,6为数字的判断是否有汉字
                        if (!checkhanzi(nameValue)) {
                            //添加前缀
                            var r;
                            APP_BCGOGO.Net.syncGet({
                                url: "product.do?method=userLicenseNo",
                                dataType: "json",
                                success: function(json) {
                                    r = json;
                                }
                            });
                            if (r === null) {
                                return;
                            } else {
                                var locaono = r[0].localCarNo;
                                $("#licenceNo").val((locaono + $("#licenceNo").val()).toUpperCase());
//                                redirectToGetVehicleInfo($("#licenceNo").val());
                                getCustomerInfoByLicenceNo($("#licenceNo").val());
                                locaono = '';
                            }
                        } else {
                            //                            alert("输入的车牌号码不符合规范，请检查！");
                        }
                    }
                }
            }
        }
//        selectItemNum = -1;
//        selectValue = "";
    }

    $(elementCarNo).bind("blur", elementCarNuBlurHandler);
    $("#nullifyBtn").bind("click", function() {
        var orderId = $("#id").val();
        if (!orderId) {
            return;
        }
        if (validateRepealStrikeSettled("WASH_BEAUTY", orderId)) {
            return;
        }
        if (confirm("友情提醒：作废后可能会有金额的变化，为保证账面完整，请将原单据回笼，并标记作废！")) {
            window.location = "washBeauty.do?method=washBeautyOrderRepeal&washBeautyOrderId=" + orderId;
        }

    });
    $("#copyInput").bind("click", function () {
        var orderId = $("#id").val();
        if (!orderId) {
            nsDialog.jAlert("单据ID不存在，请刷新后重试");
            return false;
        }
        APP_BCGOGO.Net.syncPost({
            url:"washBeauty.do?method=validateCopy",
            dataType:"json",
            data:{"washBeautyOrderId" : orderId},
            success:function(result) {
                if (result.success) {
                    window.location.href = "washBeauty.do?method=washBeautyOrderCopy&washBeautyOrderId=" + orderId;
                } else {
                    if (result.operation == 'ALERT') {
                        nsDialog.jAlert(result.msg, result.title);
                    } else if (result.operation == 'CONFIRM') {
                        nsDialog.jConfirm(result.msg, result.title, function(resultVal) {
                            if (resultVal) {
                                window.location.href = "washBeauty.do?method=washBeautyOrderCopy&washBeautyOrderId=" + orderId;
                            }
                        });
                    }
                }
            },
            error:function() {
                nsDialog.jAlert("验证时产生异常，请重试！");
            }
        });
    });

    if ($("#disabledServiceInfoStr").val()) {
        nsDialog.jAlert($("#disabledServiceInfoStr").val());
    }

    if (GLOBAL.Lang.isEmpty($("#orderVestDate").val())) {
        $("#orderVestDate").val(GLOBAL.Date.getCurrentFormatDateMin());
    }

    if (getOrderType() == "WASH_BEAUTY") {
        $(".bcgogo_menupanel a[href^='txn.do?method=getRepairOrderByVehicleNumber'], .Jsibling li[href^='txn.do?method=getRepairOrderByVehicleNumber']").live("mouseover", function() {
            var repairUrl = $(this).attr("href");
            $(this).unbind().die().bind("click", function(e) {
                e.stopPropagation();
                e.preventDefault();
                addParamToInvoicingLink(repairUrl);
            });
        });
    }


    $("input[name$='.serviceName']").live('click focus keyup',function(event) {
        var keyCode = event.keyCode || event.which,
            obj = event.target;
        if (keyCode == 37 || keyCode == 38 || keyCode == 39 || keyCode == 40) {
            return;
        }

        if ($(this).val() != $(obj).attr("hiddenValue")) {
            $("#" + this.id.split(".")[0] + "\\.serviceId").val("");
            $(this).removeAttr("hiddenValue");
        }

        var keyword = $(obj).val();
        if($(obj).attr("initialValue") && $(obj).attr("initialValue") == $(obj).val()){
            keyword = "";
        }
        var _selectedHandler = function(event, index, data, hook){
            //fill the label into the input & fill the id into the hidden.
            $(hook).val(data.label).attr("hiddenValue", data.label);
            $(hook).closest("tr").find('input[id$="serviceId"]').val(data.id);
            $(hook).removeAttr("initialValue");
            var valflag = hook.value != $(this).html() ? true : false;
            new clearItemUtil().clearByFlag(hook, valflag);
            //$(hook).val($(this).attr("title"));
            var serviceId = data.id;
            var idPrefix = hook.id.split(".")[0];

            $("#" + idPrefix + "\\.consumeTypeStr").val("MONEY");
            $("#" + idPrefix + "\\.consumeTypeStr").change();
            $(this).find(".j_remainTimes").text("");
            $("#" + idPrefix + "\\.price").removeAttr("disabled")
                .val(G.rounding(data.price)).data("recommendedPrice", data.price);
            if (!G.isEmpty($(".item:last [id$='.serviceName']").val())){
                washBeautyOrderAdd();
                setTotal();
            }
            if (!G.Lang.isEmpty($(hook).val())) {
                setTimeout(function() {
                    var searchcompleteMultiselect = App.Module.searchcompleteMultiselect;
                    searchOrderSuggestion(searchcompleteMultiselect, hook, "");
                }, 100);
            }
        };

        App.Net.asyncPost({
            "dataType":"json",
            "url":"washBeauty.do?method=searchService&customerId=",
            "data":{"customerId":$("#customerId").val(),"name":keyword},
            "success":function(data) {
                if(!data) return;
                for(var i = 0; i < data.length; i++){
                    data[i]['label'] = data[i].name;
                }
                var uuid = G.generateUUID();
                var resultData = {};
                resultData['data'] = data;
                resultData['uuid'] = uuid;
                var droplist = App.Module.droplist;
                droplist.init();
                droplist.setUUID(uuid);
                droplist.show({
                    "selector":$(obj),
                    "data":resultData,
                    "moveOnly":false,
                    "autoSet":false,
                    "onSelect":function(event, index, data, hook) {
                        _selectedHandler(event, index, data, hook);
                        droplist.hide();
                    },
                    "onKeyboardSelect":function(event, index, data, hook) {
                        _selectedHandler(event, index, data, hook);
                        droplist.hide();
                    }
                });
            }
        });
    }).live("blur", function(e) {               //手动输入，或下拉后，补全营业分类
             var $serviceObj=$(this);
            var $targetTr=$(this).closest("tr");
            var serviceID = this.id;
            timeOutId = setTimeout(function() {
                if(!document.getElementById(serviceID)) {        //被删除
                    return;
                }
                var serviceName = document.getElementById(serviceID).value;
                serviceName = serviceName.replace(/(^\s*)|(\s*$)/g, "");
                if (serviceName == null || serviceName == "") return;
                $("#" + serviceID.split(".")[0] + "\\.total").val(0);
                var serviceId=$targetTr.find('[id$=".serviceId"]').val();
                if(G.isEmpty(serviceId)){
                    return;
                }
                var ajaxUrl = "txn.do?method=getServiceById&serviceId=" + serviceId;
                App.Net.syncAjax({
                    url: ajaxUrl,
                    dataType: "json",
                    success: function(json) {
                        var prefix = serviceID.split(".")[0];
                        if (!G.isEmpty(json)) {
                            $("#" + serviceID.split(".")[0] + "\\.serviceId").val(json.idStr);
                            $("#" + serviceID.split(".")[0] + "\\.serviceHistoryId").val("");
                            $("#" + serviceID.split(".")[0] + "\\.price").val(json.price);
                            ajaxToUpdateServiceInfo($targetTr);
                            setOrderDisableAttrs();
                            setTotal();
                            App.Net.syncPost({
                                url: "category.do?method=getCategoryByServiceId",
                                data: {
                                    "serviceId": json.idStr,
                                    "now": new Date()
                                },
                                dataType: "json",
                                success: function(jsonObject) {
                                    if (null != jsonObject.data) {
                                        var id = jsonObject.data.idStr;
                                        var name = jsonObject.data.categoryName;
                                        id = null == id ? "" : id;
                                        name = null == name ? "" : name;
                                        $("#" + prefix + "\\.businessCategoryId").val(id);
                                        $("#" + prefix + "\\.businessCategoryName").val(name);
                                        $("#" + prefix + "\\.businessCategoryName").attr("hiddenValue", name);
                                        if (name == "洗车" || name == "美容") {
                                            $("#" + prefix + "\\.businessCategoryName").attr("disabled", "disabled");
                                        } else {
                                            $("#" + prefix + "\\.businessCategoryName").removeAttr("disabled");
                                        }
                                    } else {
                                        $("#" + prefix + "\\.businessCategoryId").val("");
                                        $("#" + prefix + "\\.businessCategoryName").val("");
                                        $("#" + prefix + "\\.businessCategoryName").removeAttr("hiddenValue");
                                        $("#" + prefix + "\\.businessCategoryName").removeAttr("disabled");
                                    }
                                    disabledServiceAttrs(prefix);
                                }
                            });
                        }else {
                            $("#" + serviceID.split(".")[0] + "\\.serviceId").val("");
                            $("#" + serviceID.split(".")[0] + "\\.serviceHistoryId").val("");
                            $("#" + prefix + "\\.businessCategoryId").val("");
                            $("#" + prefix + "\\.businessCategoryName").val("");
                            $("#" + prefix + "\\.businessCategoryName").removeAttr("hiddenValue");
                            $("#" + prefix + "\\.businessCategoryName").removeAttr("disabled");
                        }

                    }
                });
            }, 500);
        }).live("change", function(){
            $(this).removeAttr("initialValue");
        });

    $("#memberNumber").live("keyup",function(e) {
        if (!checkKeyUp(this, e)) {
            return;
        }
        var keyCode = e.which || e.keyCode;
        webChangeMember(this, keyCode);
    }).live("blur",function(e){
            setTimeout(function(){
                if ($("#memberNumber").attr("clickFlag")) {
                    $("#memberNumber").removeAttr("clickFlag");
                    return;
                }else{
                    if(!G.isEmpty($("#memberNumber").val())){
                        APP_BCGOGO.Net.asyncAjax({
                            url: "washBeauty.do?method=ajaxGetWashBeautyOrderByParameter",
                            type: "POST",
                            cache: false,
                            data:{
                                memberNo:$("#memberNumber").val()
                            },
                            dataType: "json",
                            success: function (washBeautyOrderDTO) {
                                if(G.isEmpty(G.normalize(washBeautyOrderDTO).memberDTO)){
                                    nsDialog.jAlert("会员号不存在。");
                                    $("#memberNumber").val("");
                                    return;
                                }
                                initWashBeautyOrder(washBeautyOrderDTO);
//                                $("#customer").blur();
                            },
                            error:function(){
                                nsDialog.jAlert("网络异常！");
                            }
                        })
                    }
                }
            },300);
        });
});

function webChangeMember(thisObj, keyCode){
    if (G.isEmpty($(thisObj).val())){
        $("#div_brand").css({
            'display': 'none'
        });
    } else {
        var memberNo= $(thisObj).val().replace(/[\ |\\]/g, "");
        searchSuggestionMember(thisObj,memberNo,"notclick", keyCode);
    }
}

//});

function checkWashBeautyOrderAdd(){
    var $tr=$('.item:last');
    if(!G.isEmpty($tr)&&G.isEmpty($tr.find('[id$="serviceName"]').val())){
        nsDialog.jAlert("服务名不能为空。");
        return false;
    }
    var consumeType=$tr.find('[id$=".consumeTypeStr"]').val();
    var businessCategoryName=$tr.find('[id$=".businessCategoryName"]').val();
    var salesMan=$tr.find('[id$=".salesMan"]').val();
    var price=$tr.find('[id$=".price"]').val();
    if(consumeType=="MONEY"){

    }else if(consumeType=="TIMES"){

    }else if(consumeType=="COUPON"){
        var couponType=$tr.find('[id$=".couponType"]').val();
        var couponNo=$tr.find('[id$=".couponNo"]').val();
        if(G.isEmpty(couponType)){
            nsDialog.jAlert("请输入消费券类型。");
            return false;
        }
        if(G.isEmpty(couponNo)){
            nsDialog.jAlert("请输入消费券号。");
            return false;
        }
    }

    if($(".item").size()>= 2&&checkServiceItems()){
        nsDialog.jAlert("服务项目有重复内容，请修改或删除。");
        return false;
    }

    return true;
}

function washBeautyOrderAdd() {
    var tr = $(getTrSample()).clone();
    $(tr).find("input").val("");
    $(tr).find("input,a,span,select").each(function (i) {
        //去除文本框的自动填充下拉框
        if ($(this).attr("type") == "text") {
            $(this).attr("autocomplete", "off");
        }
        if (!this || !this.id) return;
        //replace id
        var idStrs = this.id.split(".");
        var tcNum = $(".item").size();
        while(checkWashBeautyItemDom(tcNum, idStrs[1])){
            tcNum = ++tcNum;                       //TODO 计算行号，如果目标行号对应的元素存在，则该行号加一
        }
        //TODO 组装新的ID和NAME  Begin-->
        var newId = "washBeautyOrderItemDTOs" + tcNum + "." + idStrs[1];
        $(this).attr("id", newId);
        //replace name
        var nameStr = $(this).attr("name");
        if (G.isEmpty(nameStr)) {
            return true;
        }
        if ($(this).attr("name").split(".")[1]) {
            var newName = "washBeautyOrderItemDTOs[" + tcNum + "]." + $(this).attr("name").split(".")[1];
            $(this).attr("name", newName);
        }
        //TODO <-- End
        $(this).attr("autocomplete", "off");
    });
    $("#table_services tr:last").after($(tr));
    isShowAddButton();
    return $(tr);
}

function isShowAddButton() {
    if ($(".item").size() <= 0) {
        washBeautyOrderAdd();
    }
    $(".item .opera2").remove();
    var opera1Id = $(".item:last").find(".opera1").attr("id");
    if (!opera1Id) return;
    var idPrefix=opera1Id.split(".")[0];
    var num=idPrefix.substring(idPrefix.length-1);
    $(".item:last").find(".opera1").after('<a class="opera2 blue_color" style="margin-left: 5px" id="washBeautyOrderItemDTOs'+num+ '.plusBtn" >增加</a>');
}

function getTrSample() {
    var trSample= '<tr class="bg item titBody_Bg">'+
        '<td style="padding-left:10px;">' +
        '<input id="washBeautyOrderItemDTOs0.serviceName" name="washBeautyOrderItemDTOs[0].serviceName"'+
        'class="txt checkStringEmpty J-hide-empty-droplist J-bcgogo-droplist-on" autocomplete="off" kissfocus="on"/>' +
        '<input id="washBeautyOrderItemDTOs0.serviceId" name="washBeautyOrderItemDTOs[0].serviceId" type="hidden"/></td>'+
        '<td class="surplus">' +
        '<select id="washBeautyOrderItemDTOs0.consumeTypeStr" style="width: 80px" class="payMethod txt" onchange="selChange(this.value,this)" name="washBeautyOrderItemDTOs[0].consumeTypeStr">'+
        '<option value="MONEY">金额</option>';
    if($("#memberSwitch").val()){
        trSample+='<option value="TIMES">计次划卡</option>';
    }
    trSample+= '<option value="COUPON">消费券</option>'+
        '</select><span class="j_remainTimes" style="padding-left:6px"></span></td>'+
        '<td>'+
        '<input id="washBeautyOrderItemDTOs0.businessCategoryName" name="washBeautyOrderItemDTOs[0].businessCategoryName" type="text" class="businessCategoryName txt" />'+
        '<input id="washBeautyOrderItemDTOs0.businessCategoryId" name="washBeautyOrderItemDTOs[0].businessCategoryId" type="hidden"/>'+
        '</td>'+
        '<td>'+
        '<input id="washBeautyOrderItemDTOs0.salesMan" name="washBeautyOrderItemDTOs[0].salesMan" type="text" class="txt" />'+
        '<input id="washBeautyOrderItemDTOs0.salesManIds" name="washBeautyOrderItemDTOs[0].salesManIds" type="hidden"/>'+
        '<input id="washBeautyOrderItemDTOs0.hiddenConsumeType" name="washBeautyOrderItemDTOs[0].hiddenConsumeType" type="hidden"/>'+
        '</td>'+
        '<td><input id="washBeautyOrderItemDTOs0.price" name="washBeautyOrderItemDTOs[0].price" type="text" class="txt" data-filter-zero="true"/></td>'+
        '<td><input id="washBeautyOrderItemDTOs0.couponType" name="washBeautyOrderItemDTOs[0].couponType" style="display:none;" type="text" class="txt" autocomplete="off"/></td>'+
        '<td><input id="washBeautyOrderItemDTOs0.couponNo" name="washBeautyOrderItemDTOs[0].couponNo" style="display:none;" type="text" class="txt" autocomplete="off"/></td>'+
        '<td><a id="washBeautyOrderItemDTOs0.deleteBtn" class="blue_color opera1">删除</a> <a id="washBeautyOrderItemDTOs0.plusBtn" class="blue_color opera2">新增</a></td>'+
        '</tr>' +
        '<tr class="titBottom_Bg"><td colspan="8"></td></tr>';
    return trSample;
}

function setTotal(){
    var total = 0;
    $("input[id$='.price']").each(function () {
        $(this).val() && $(this).val(G.rounding($(this).val()));
        if ($(this).closest("tr").find("select[id$='consumeTypeStr']").val() == "MONEY") {
            var price = $(this).val() * 1;
            price = (null == price || "" == price || isNaN(price)) ? 0 : price;
            total = total + price * 1;
        }
    });
    $("#total_span").text(dataTransition.simpleRounding(total, 2));
    $("#total").val(dataTransition.simpleRounding(total, 2));
}

// 与invoice.js 中的同名方法相同.
function ajaxStyleWorkers(domObject, jsonStr) {
    var offset = jQuery(domObject).offset();
    var offsetHeight = jQuery(domObject).height();
    var offsetWidth = jQuery(domObject).width();
    domTitle = domObject.name;
    var point = {
        x: G.getX(domObject),
        y: G.getY(domObject)
    };
    $("#div_works").css({
        'display': 'block',
        'position': 'absolute',
        'left': point.x + 'px',
        'top': point.y + offsetHeight + 3 + 'px',
        'overflow-x': "hidden",
        'overflow-y': "auto"
    });
    $("#works-Container_id").html("");
    selectmore = jsonStr.length;
    for (var i = 0; i < jsonStr.length; i++) {
        if (judgeRepeat($(domObject).val(), jsonStr[i].name)) {
            continue;
        }
        var a = $("<a id='selectItem" + i + "'></a>");
        a.html(stringMethod.substring(jsonStr[i].name, 10)).attr('title', jsonStr[i].name);
        ;
        a.val(jsonStr[i].idStr);
        a.attr("title", jsonStr[i].name);
        a.mouseover(function() {
            $("#works-Container_id > a").removeAttr("class");
            $(this).attr("class", "hover");
            selectItemNum = parseInt(this.id.substring(10));
        });
        a.click(function() {
            var valflag = domObject.value != $(this).html() ? true : false;
            new clearItemUtil().clearByFlag(domObject, valflag);
            var workerVal;
            if ($.trim($(domObject).val()) == '') {
                workerVal = $(this).html();
            } else {
                workerVal = $(domObject).val() + "," + $(this).html();
            }
            $(domObject).val(workerVal);
            limitSixWorkers(domObject);
            $(domObject).blur();
            $("#div_works").css({
                'display': 'none'
            });
            selectItemNum = -1;
            var idPrefix = domObject.id.split(".")[0];
            var workerIds = $("#" + idPrefix + "\\.workerId").val();
            if ($.trim(workerIds) == '') {
                workerIds = $(this).val();
            } else {
                workerIds += "," + $(this).val();
            }
            $("#" + idPrefix + "\\.workerId").val(workerIds);
        });
        $("#works-Container_id").append(a);
    }
}

function limitSixWorkers(obj) {
    var workers = $(obj).val().split(",");
    if (workers.length > 6) {
        alert("施工人不可超过6个！");
    }
    var workerStr = "";
    for (var i in workers) {
        if (i <= 5) {
            workerStr += (workers[i] + ",");
        }
    }
    workerStr = workerStr.substring(0, workerStr.length - 1);
    $(obj).val(workerStr);
}

function judgeRepeat(existVal, selectVal) {
    var existVals = existVal.split(",");
    var flag = false;
    for (var i in existVals) {
        if (existVals[i] && typeof(existVals[i]) != "function" && $.trim(existVals[i]) == selectVal) {
            flag = true;
            break;
        }
    }
    return flag;
}

function checkServiceItems(){
    var $items = $(".item");
    if (!$items) return false;
    if ($items.length < 2) return false;
    var s = '';
    //先获取最后一个
    var cur = ''; //当前最后添加的一条记录
    for (var i = $items.length - 1; i >= 0; i--) {
        var selects = $items[i].getElementsByTagName("select");
        if (!selects) continue;
        var index = selects[0].name.split(".")[0].substring(selects[0].name.indexOf('[') + 1, selects[0].name.indexOf(']'));
        if (i == $items.length - 1) {
            //            最后添加的一个
            cur += $("#washBeautyOrderItemDTOs" + index + "\\.serviceId").val();
        } else {
            var older = '';
            older += $("#washBeautyOrderItemDTOs" + index + "\\.serviceId").val();
            if (cur == older) {
                return true;
            }
        }
    }
    return false;
}

function searchSuggestionCustomer(domObject, elementCustomer, eventStr, keycode) {
    var searchWord;
    if (eventStr == "click") {
        searchWord = "";
    } else {
        searchWord = domObject.value;
    }
    APP_BCGOGO.Net.asyncPost({
        url: "txn.do?method=getCustomerName",
        data: {
            name: searchWord
        },
        cache: false,
        dataType: "json",
        success: function(jsonStr) {
            if (jsonStr[0] != undefined) {
                G.completer(
                    {
                        'domObject':domObject,
                        'keycode':keycode,
                        'title':jsonStr[0].name
                    }
                );
            }
            ajaxStyleCustomer(domObject, jsonStr);
        }
    });
}

function searchSuggestionMember(domObject, memberNo, eventStr, keycode) {
    var searchWord;
    if (eventStr == "click") {
        searchWord = "";
    } else {
        searchWord = memberNo;
    }
    APP_BCGOGO.Net.asyncPost({
        url: "member.do?method=getEnabledMemberLikeMemberNo",
        data: {
            memberNo: searchWord
        },
        cache: false,
        dataType: "json",
        success: function(jsonStr) {
            if (jsonStr[0] != undefined) {
                G.completer(
                    {
                        'domObject':domObject,
                        'keycode':keycode,
                        'title':jsonStr[0].memberNo
                    }
                );
            }
            ajaxStyleMember(domObject, jsonStr);
        }
    });
}

function ajaxStyleMember(domObject, jsonStr){
    var offset = $(domObject).offset();
    var offsetHeight = $(domObject).height();
    var offsetWidth = $(domObject).width();
    domTitle = domObject.name;
    var x = G.getX(domObject);
    var y = G.getY(domObject);
    selectmoreCustomer = jsonStr.length;
    if (selectmoreCustomer <= 0) {
        $("#div_brand").css({
            'display': 'none'
        });
    } else {
        $("#div_brand").css({
            'display': 'block',
            'position': 'absolute',
            'left': x + 'px',
            'top': y + offsetHeight + 'px'
        });
        $("#Scroller-Container_id").html("");
        for (var i = 0; i < jsonStr.length; i++) {
            var a = $("<a id='selectItem" + i + "'></a>");
            a.html(jsonStr[i].memberNo);
            a.css({
                "text-overflow": "ellipsis",
                "overflow": "hidden",
                "white-space": "nowrap"
            });
            a.attr("title", jsonStr[i].memberNo);
            a.mouseover(function() {
                $("#Scroller-Container_id> a").removeAttr("class");
                $(this).attr("class", "hover");
//                selectValueCustomer = jsonStr[$("#Scroller-Container_id > a").index($(this)[0])].name; // $(this).html();
//                selectItemNumCustomer = parseInt(this.id.substring(10));
            });
            a.click(function(){
                $("#memberNumber").attr("clickFlag",true);
                $("#div_brand").css({
                    'display': 'none'
                });
                var data={
                    memberNo:jsonStr[$("#Scroller-Container_id > a").index($(this)[0])].memberNo
                };
                APP_BCGOGO.Net.asyncAjax({
                    url: "washBeauty.do?method=ajaxGetWashBeautyOrderByParameter",
                    type: "POST",
                    cache: false,
                    data:data,
                    dataType: "json",
                    success: function (washBeautyOrderDTO) {
                        initWashBeautyOrder(washBeautyOrderDTO);
                    },
                    error:function(){
                        nsDialog.jAlert("网络异常！");
                    }
                });
            });
            $("#Scroller-Container_id").append(a);
        }
    }

    //弹出复选框的最后一项
    if ((domTitle == 'brand' || domTitle == 'model') && jsonStr.length == 9) {
        var a = $("<a id='selectItem" + (selectmoreCustomer) + "'></a>");
        a.html("更多");
        a.mouseover(function() {
            $("#Scroller-Container_idCustomer > a").removeAttr("class");
            $(this).attr("class", "hover");
            selectItemNumCustomer = parseInt(this.id.substring(10));
        });
        a.click(function() {
            $("#div_brandCustomer").css({
                'display': 'none'
            });
            $("#iframe_PopupBox").attr("src", encodeURI("product.do?method=createsearchvehicleinfo&domtitle=" + domTitle + "&brandvalue=" + $("#brand").val()));
            $("#iframe_PopupBox").css({
                'display': 'block'
            });
            Mask.Login();
        });
        $("#Scroller-Container_idheader").append(a);
    }
}

function searchSuggestionlicenceNo(domObject, elementCarNo, eventStr, keycode) { //车辆信息查询
    var searchWord;
    if (eventStr == "click") {
        searchWord = "";
    } else {
        searchWord = domObject.value;
    }

    APP_BCGOGO.Net.asyncPost({
        url: "product.do?method=searchlicenseplate",
        data: {
            plateValue: searchWord,
            now: new Date()
        },
        cache: false,
        dataType: "json",
        success: function(jsonStr) {
            if (!G.isEmpty(jsonStr[0])) {
                G.completer({
                        'domObject':domObject,
                        'keycode':keycode,
                        'title':jsonStr[0].licenceNo}
                );
            }

            ajaxStylelicenceNo(domObject, jsonStr);
        }
    });
}

function ajaxStyleCustomer(domObject, jsonStr){
    var offset = $(domObject).offset();
    var offsetHeight = $(domObject).height();
    var offsetWidth = $(domObject).width();
    domTitle = domObject.name;
    var x = G.getX(domObject);
    var y = G.getY(domObject);
    selectmoreCustomer = jsonStr.length;
    if (selectmoreCustomer <= 0) {
        $("#div_brand").css({
            'display': 'none'
        });
    } else {
        $("#div_brand").css({
            'display': 'block',
            'position': 'absolute',
            'left': x + offsetHeight + 102 + 'px',
            'top': y + offsetHeight + (-14) + 'px'
        });
        $("#Scroller-Container_id").html("");
        for (var i = 0; i < jsonStr.length; i++) {
            var a = $("<a id='selectItem" + i + "'></a>");
            a.html(jsonStr[i].name);
            a.css({
                "text-overflow": "ellipsis",
                "overflow": "hidden",
                "white-space": "nowrap"
            });
            var titleVal = jsonStr[i].name;
            if (jsonStr[i].mobile) {
                a.append("+" + jsonStr[i].mobile);
                titleVal = titleVal + "+" + jsonStr[i].mobile;
            } else if (jsonStr[i].landLine) {
                a.append("+" + jsonStr[i].landLine);
                titleVal = titleVal + "+" + jsonStr[i].landLine;
            }
            a.attr("title", titleVal);

            a.mouseover(function() {
                $("#Scroller-Container_id> a").removeAttr("class");
                $(this).attr("class", "hover");
                selectValueCustomer = jsonStr[$("#Scroller-Container_id > a").index($(this)[0])].name; // $(this).html();
                selectItemNumCustomer = parseInt(this.id.substring(10));
            });
            a.click(function() {
                $("#div_brand").css({
                    'display': 'none'
                });
                var receiptNo = document.getElementsByName("receiptNo"),
                    receiptNoValue = (receiptNo && receiptNo[0]) ? (receiptNo[0].value) : ("");
                var data={
                    customerId:jsonStr[$("#Scroller-Container_id > a").index($(this)[0])].idStr
                };
                APP_BCGOGO.Net.asyncAjax({
                    url: "washBeauty.do?method=ajaxGetWashBeautyOrderByParameter",
                    type: "POST",
                    cache: false,
                    data:data,
                    dataType: "json",
                    success: function (washBeautyOrderDTO) {
                        washBeautyOrderDTO=G.normalize(washBeautyOrderDTO);
                        initCustomerInfo(washBeautyOrderDTO);
                        if(!(!G.isEmpty(washBeautyOrderDTO.customerIdStr)&&G.isEmpty(washBeautyOrderDTO.licenceNo))){
                            initVehicleInfo(washBeautyOrderDTO);
                        }
                        $("#totalConsume").val(G.rounding(washBeautyOrderDTO.totalConsume));
                        $("#totalConsumeSpan").text(G.rounding(washBeautyOrderDTO.totalConsume));
                        $("#totalReceivableSpan").text(G.rounding(washBeautyOrderDTO.totalReceivable));
                        $("#totalReturnDebtSpan").text(G.rounding(washBeautyOrderDTO.totalReturnDebt));
                        $("#vestDateStr").val(G.normalize(washBeautyOrderDTO.vestDateStr));
                        $("#totalDebt").val(G.normalize(washBeautyOrderDTO.totalDebt));
                        initMemberInfo(washBeautyOrderDTO);
                        isShowAddButton();
                        ajaxToUpdateServiceInfo();
                        setOrderDisableAttrs();

                    },
                    error:function(){
                        nsDialog.jAlert("网络异常！");
                    }
                });

            });
            $("#Scroller-Container_id").append(a);
        }
    }

    //弹出复选框的最后一项
    if ((domTitle == 'brand' || domTitle == 'model') && jsonStr.length == 9) {
        var a = $("<a id='selectItem" + (selectmoreCustomer) + "'></a>");
        a.html("更多");
        a.mouseover(function() {
            $("#Scroller-Container_idCustomer > a").removeAttr("class");
            $(this).attr("class", "hover");
            selectItemNumCustomer = parseInt(this.id.substring(10));
        });
        a.click(function() {
            $("#div_brandCustomer").css({
                'display': 'none'
            });
            $("#iframe_PopupBox").attr("src", encodeURI("product.do?method=createsearchvehicleinfo&domtitle=" + domTitle + "&brandvalue=" + $("#brand").val()));
            $("#iframe_PopupBox").css({
                'display': 'block'
            });
            Mask.Login();
        });
        $("#Scroller-Container_idheader").append(a);
    }
}

function ajaxStylelicenceNo(domObject, jsonStr) {
    var offset = $(domObject).offset();
    var offsetHeight = $(domObject).height();
    var offsetWidth = $(domObject).width();
    domTitle = domObject.name;
    var x = G.getX(domObject);
    var y = G.getY(domObject);
    selectmore = jsonStr.length;

    if (selectmore <= 0) {
        $("#div_brandvehiclelicenceNo").css({
            'display': 'none'
        });
    } else {
        $("#div_brandvehiclelicenceNo").css({
            'overflow-x': "hidden",
            'overflow-y': "auto",
            'display': 'block',
            'position': 'absolute',
            'left': x + 'px',
            'top': y + offsetHeight + 8 + 'px'
        });
        $("#Scroller-Container_idlicenceNo").html("");
        for (var i = 0; i < jsonStr.length; i++) {
            var a = $("<a id='selectItem" + i + "'></a>");
            a.html(jsonStr[i].carno);
            a.mouseover(function() {
                $("#Scroller-Container_idlicenceNo > a").removeAttr("class");
                $(this).attr("class", "hover");
                selectValue = $(this).html();
                var selectValueOther = $(this).html();
                if (typeof(selectValueOther) != "undefined") {
                    selectValue = selectValueOther;
                } else {
                    selectValue = "";
                }
                selectItemNum = parseInt(this.id.substring(10));
            });
            a.mouseout(function() {
                selectValue = "";
                $(this).removeAttr("class");
            })
            a.click(function() {             //tag a.click
                $(domObject).val($(this).html());
                domObject.value = selectValue;
                var nameValue = selectValue;

                // 除去 空格 和 "-"
                var resultStr = nameValue.replace(/\s|\-/g, "");
                if (!APP_BCGOGO.Validator.stringIsLicensePlateNumber(resultStr)) {
                    alert("输入的车牌号码不符合规范，请检查！");
                    return;
                }
                if ($("#licenceNo") != null || $("#licenceNo") != '') {
                    getCustomerInfoByLicenceNo($("#licenceNo").val());
                }
                $("#div_brandvehiclelicenceNo").css({
                    'display': 'none'
                });
            });
            $("#Scroller-Container_idlicenceNo").append(a);
        }
    }
    //弹出复选框的最后一项
    if ((domTitle == 'brand' || domTitle == 'model') && jsonStr.length == 9) {
        var a = $("<a id='selectItem" + (selectmore) + "'></a>");
        a.html("更多");
        a.mouseover(function() {
            $("#Scroller-Container_idlicenceNo > a").removeAttr("class");
            $(this).attr("class", "hover");
            selectItemNum = parseInt(this.id.substring(10));
        });
        a.click(function() {
            $("#div_brandvehiclelicenceNo").css({
                'display': 'none'
            });
            $("#iframe_PopupBox").attr("src", encodeURI("product.do?method=createsearchvehicleinfo&domtitle=" + domTitle + "&brandvalue=" + $("#brand").val()));
            $("#iframe_PopupBox").css({
                'display': 'block'
            });
            Mask.Login();
        });
        $("#Scroller-Container_idheader").append(a);
    }
}

//function redirectToGetVehicleInfo(resultStr) {
//    var receiptNo = document.getElementsByName("receiptNo"),
//        receiptNoValue = (receiptNo && receiptNo[0]) ? (receiptNo[0].value) : ("");
//
//    window.location = "washBeauty.do?method=getCustomerInfoByLicenceNo&licenceNo=" + encodeURIComponent(resultStr)
//        + "&customerId=" + encodeURIComponent($("#customerId").val()) + "&customer=" + encodeURIComponent($("#customer").val())
//        + "&mobile=" + encodeURIComponent($("#mobile").val()) + "&landLine=" + encodeURIComponent($("#landLine").val())
//        + "&brand=" + encodeURIComponent($("#brand").val()) + "&model=" + encodeURIComponent($("#model").val()) + "&receiptNo=" + receiptNoValue
//        + "&vestDateStr=" + $("#orderVestDate").val();
//}

var searchLicenceNoByCustomerId = function(domObject, customerId, isDefaultVehicle) {
    if (!customerId) return;

    APP_BCGOGO.Net.asyncPost({
        "url": "product.do?method=searchLicenceNoByCustomerId",
        "cache": false,
        "dateType": "json",
        "data": {
            customerId: customerId
        },
        "success": function(jsonStr) {
            jsonStr = JSON.parse(jsonStr);
            if (jsonStr.length == 1 && !$("#licenceNo").val() && isDefaultVehicle) {
                $("#licenceNo").val(jsonStr[0].carno);
                getCustomerInfoByLicenceNo(jsonStr[0].carno);
            } else if (!isDefaultVehicle) ajaxStylelicenceNo(domObject, jsonStr);
        }
    });
}

function getCarWashHistory(licenceNo) {
    if (licenceNo == null || licenceNo == '') {
        alert("请输入车牌号!");
        return;
    }
    bcgogo.checksession({
        "parentWindow": window.parent,
        'iframe_PopupBox': $("#iframe_PopupBox")[0],
        'src': "goodsHistory.do?method=createCarHistory&orderType=WASH_BEAUTY" + "&licenceNo=" + encodeURI(licenceNo) + "&issubmit=true"
    });
}

function divMouseOver(dom) {
    $(dom).css('background-color', '#c8f0ff');
}

function divMouseOut(dom) {
    $(dom).css('background-color', 'white');
}

function doValueSet(dom) {
    var value = $.trim($(dom).html());
    var manId = $(dom).next().val();
    $(dom).parent().prev().val(value);
    $(dom).parent().prev().prev().val(manId);
    $(dom).parent().fadeOut("normal");

}

function newOtherOrder(url) {
    addParamToInvoicingLink(url);
}
//跳转到施工单信息带入

function addParamToInvoicingLink(url) {
    url = url + encodeURIComponent($("#licenceNo").val()) + "&brand=" + encodeURIComponent($("#brand").val())
        + "&model=" + encodeURIComponent($("#model").val()) + "&customer=" + encodeURIComponent($("#customer").val())
        + "&customerId=" + encodeURIComponent($("#customerId").val()) + "&mobile=" + encodeURIComponent($("#mobile").val())
        + "&landLine=" + encodeURIComponent($("#landLine").val()) + "&vehicleContact=" + encodeURIComponent($("#vehicleContact").val())
        + "&vehicleMobile=" + encodeURIComponent($("#vehicleMobile").val()) + "&vehicleColor=" + encodeURIComponent($("#vehicleColor").val())
        + "&vehicleChassisNo=" + encodeURIComponent($("#vehicleChassisNo").val()) + "&vehicleEngineNo=" + encodeURIComponent($("#vehicleEngineNo").val());
    if (openNewOrderPage()) {
        window.open(url, "_blank");
    } else {
        window.location.href = url;
    }
}

function newWashBeautyOrder() {
    if (openNewOrderPage()) {
        window.open($("#basePath").val() + "washBeauty.do?method=createWashBeautyOrder", "_blank");
    } else {
        window.location.href = $("#basePath").val() + "washBeauty.do?method=createWashBeautyOrder";
    }
}
var objEnterPhoneType, objEnterPhoneArrears, objEnterPhoneLicenceNo, objEnterPhoneDate, objEnterPhoneName, objEnterPhoneCustomerIdStr;

function enterPhoneSendSms(objEnterPhoneMobile) {
    sendSms(objEnterPhoneMobile, objEnterPhoneType, objEnterPhoneArrears, objEnterPhoneLicenceNo, objEnterPhoneDate, objEnterPhoneName, objEnterPhoneCustomerIdStr);
}

function sendSms(mobile, type, arrears, licenceNo, date, name, customerIdStr) {        // type <!-- 0 保险  1 验车  2 生日-->
    if (mobile == null || $.trim(mobile) == "") {
        if (customerIdStr == "" || customerIdStr == null) {
            alert("请先查询客户信息！");
            return;
        }

        $("#enterPhoneCustomerId").val(customerIdStr);
        Mask.Login();
        $("#enterPhoneSetLocation").fadeIn("slow");
        objEnterPhoneType = type;
        objEnterPhoneArrears = arrears;
        objEnterPhoneLicenceNo = licenceNo;
        objEnterPhoneDate = date;
        objEnterPhoneName = name;
        objEnterPhoneCustomerIdStr = customerIdStr;
        return;
    }

    if (arrears == 0.0) {
        window.location = "sms.do?method=smswrite&customerIdStr="+customerIdStr+"&mobile=" + mobile;
    } else {
        var dates = date.split("-");
        var month = dates[1];
        var day = dates[2];
        window.location = "sms.do?method=smswrite&mobile=" + $.trim(mobile) + "&type=" + type + "&arrears=" + arrears + "&licenceNo=" + encodeURI(licenceNo) + "&month=" + month + "&day=" + day + "&name=" + encodeURI(name) + "&money=" + arrears + "&customerId=" + customerIdStr;
    }
}

$().ready(function() {

    $(".businessCategoryName")._dropdownlist("businessCategary");

    //弹出施工人下拉框
    $("input[id$='.salesMan']")._dropdownlist("worker");
});

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

function searchOrderSuggestion(searchcompleteInstance, node, opt, async) {
    return;
    if("page" == opt) {
        searchcompleteInstance.moveFollow({
            node: node,
            isMoveOnly: true
        });
    } else {
        searchcompleteInstance.moveFollow({
            node: node,
            isMoveOnly: false
        });
    }

    var ajaxData = getOrderSuggestionAjaxData(searchcompleteInstance, node);
    APP_BCGOGO.Net[(async ? "async" : "sync") +"Post"]({
        url: "searchInventoryIndex.do?method=getOrderItemDetails",
        data: ajaxData,
        dataType: "json",
        success: function(json) {
            var hideSuggestion = false;
            if($(node).attr("class").search(/J-hide-empty-droplist/) != -1 && json.totalCount == 0){
                hideSuggestion = true;
            }
            if(!hideSuggestion){
                showDetailSuggestion(searchcompleteInstance, json);
            }
        }
    });
}

function ajaxToUpdateMemberInfoForCarWash(memberNo){
    if(G.isEmpty(memberNo)) return;
    APP_BCGOGO.Net.asyncAjax({
        url: "washBeauty.do?method=ajaxGetWashBeautyOrderByParameter",
        type: "POST",
        cache: false,
        data:{
            memberNo:memberNo
        },
        dataType: "json",
        success: function (washBeautyOrderDTO) {
            initMemberInfo(washBeautyOrderDTO);
            ajaxToUpdateServiceInfo();
            if(!G.isEmpty(washBeautyOrderDTO.mobile)){
                $("#mobile").val(washBeautyOrderDTO.mobile);
            }
            setOrderDisableAttrs();
        },
        error:function(){
            nsDialog.jAlert("网络异常！");
        }
    });
}

function initMemberInfo(washBeautyOrderDTO){
    washBeautyOrderDTO=G.normalize(washBeautyOrderDTO);
    var memberDTO=G.normalize(washBeautyOrderDTO.memberDTO);
    if(G.isEmpty(washBeautyOrderDTO)){
        $("#memberNumber").show();
        $("#memberNumberSpan").hide();
        $("#doMemberCard").text("");
    }else{
        if(G.isEmpty(memberDTO)){
            $("#doMemberCard").text("购卡");
            $("#memberNumber").hide();
            $("#memberNumberSpan").show();
        }else{
            $("#doMemberCard").text("续卡");
            $("#memberNumber").show();
            $("#memberNumberSpan").hide();
        }
    }
    $("#doMemberCard").show();
    $("#memberNumber").text(G.normalize(memberDTO.memberNo));
    $("#memberNumber").val(G.normalize(memberDTO.memberNo));
    $("#memberStatus").text(G.normalize(memberDTO.statusStr));
    $("#memberDTO\\.statusStr").val(G.normalize(memberDTO.statusStr));
    $("#memberType").text(G.normalize(memberDTO.type));
    $("#memberDTO\\.memberType").val(G.normalize(memberDTO.type));
    $("#memberRemainAmount").text(G.rounding(memberDTO.balanceStr));
    $("#memberDTO\\.balance").val(G.rounding(memberDTO.balanceStr));
    $("#memberServiceTable tr:gt(0)").remove();
    var memberServiceDTOs=G.normalize(memberDTO.memberServiceDTOs);
    if(!G.isEmpty(memberServiceDTOs)){
        var trStr='';
        for(var i=0;i<memberServiceDTOs.length;i++){
            var memberServiceDTO=memberServiceDTOs[i];
            trStr+='<tr>';
            trStr+='<td>'+G.normalize(memberServiceDTO.serviceName)+'</td>';
            trStr+='<td>'+G.normalize(memberServiceDTO.timesStr)+'</td>';
            trStr+='<td>'+G.normalize(memberServiceDTO.deadlineStr)+'</td>';
            trStr+='</tr>';
        }
        $("#memberServiceTable").append(trStr);
    }
}

function initCustomerInfo(washBeautyOrderDTO){
    washBeautyOrderDTO=G.normalize(washBeautyOrderDTO);
    $("#customer").val(G.normalize(washBeautyOrderDTO.customer));
    $("#mobile").val(G.normalize(washBeautyOrderDTO.mobile));
     $("#vehicleContact").val(G.normalize(washBeautyOrderDTO.vehicleContact));
    $("#vehicleMobile").val(G.normalize(washBeautyOrderDTO.vehicleMobile));
    $("#landLine").val(G.normalize(washBeautyOrderDTO.landLine));
    $("#customerId").val(washBeautyOrderDTO.customerIdStr);
    $("#contact").val(G.normalize(washBeautyOrderDTO.contact));
    $("#contactId").val(G.normalize(washBeautyOrderDTO.contactId));
    $("#qq").val(G.normalize(washBeautyOrderDTO.qq));
    $("#email").val(G.normalize(washBeautyOrderDTO.email));
    $("#address").val(G.normalize(washBeautyOrderDTO.address));
    $("#company").val(G.normalize(washBeautyOrderDTO.company));

}

function initVehicleInfo(washBeautyOrderDTO){
    $("#licenceNo").val(G.normalize(washBeautyOrderDTO.licenceNo));
    $("#brand").val(G.normalize(washBeautyOrderDTO.brand));
    $("#model").val(G.normalize(washBeautyOrderDTO.model));
    $("#vehicleChassisNo").val(G.normalize(washBeautyOrderDTO.vehicleChassisNo));
    $("#vehicleEngineNo").val(G.normalize(washBeautyOrderDTO.vehicleEngineNo));
    $("#vehicleColor").val(G.normalize(washBeautyOrderDTO.vehicleColor));
}

function initWashBeautyOrder(washBeautyOrderDTO){
    washBeautyOrderDTO=G.normalize(washBeautyOrderDTO);
    initCustomerInfo(washBeautyOrderDTO);
//    if(!(!G.isEmpty(washBeautyOrderDTO.customerIdStr)&&G.isEmpty(washBeautyOrderDTO.licenceNo))){
        initVehicleInfo(washBeautyOrderDTO);
//    }

    $("#totalConsume").val(G.rounding(washBeautyOrderDTO.totalConsume));
    $("#totalConsumeSpan").text(G.rounding(washBeautyOrderDTO.totalConsume));
    $("#totalReceivableSpan").text(G.rounding(washBeautyOrderDTO.totalReceivable));
    $("#totalReturnDebtSpan").text(G.rounding(washBeautyOrderDTO.totalReturnDebt));
    $("#vestDateStr").val(G.normalize(washBeautyOrderDTO.vestDateStr));
    $("#totalDebt").val(G.normalize(washBeautyOrderDTO.totalDebt));

    initMemberInfo(washBeautyOrderDTO);
    isShowAddButton();
    ajaxToUpdateServiceInfo();
    setOrderDisableAttrs();
}

function ajaxToUpdateServiceInfo(targetItem){
   var $targetItem=G.isEmpty(targetItem)?$(".item"):$(targetItem);
    APP_BCGOGO.Net.syncAjax({
        url: "member.do?method=getMemberByCustomerId",
        type: "POST",
        cache: false,
        data:{
            customerId:$("#customerId").val()
        },
        dataType: "json",
        success: function (memberDTO) {
            memberDTO=G.normalize(memberDTO);
            var memberServiceDTOs=G.normalize(memberDTO.memberServiceDTOs);
            var memberServiceMap={};
            for(var i=0;i< memberServiceDTOs.length;i++){
                var memberServiceDTO=G.normalize(memberServiceDTOs[i]);
                var serviceId=memberServiceDTO.serviceIdStr;
                if(G.isEmpty(serviceId)){
                    continue;
                }
                memberServiceMap[serviceId]=memberServiceDTO;
            }

            $targetItem.each(function(){
                var serviceId=$(this).find('[id$=".serviceId"]').val();
                if(!G.isEmpty(serviceId)){
                    var $consumeType=$(this).find('[id$=".consumeTypeStr"]');
                    var memberServiceDTO=memberServiceMap[serviceId];
                    if(!G.isEmpty(memberServiceDTO)){
                        var times=G.rounding(memberServiceDTO.times);
                        if(times>0||times==-1){
                            $consumeType.val("TIMES");
                            $consumeType.change();
                            var remainStr=times==-1?"不限次":"还剩"+times+"次";
                            $(this).find(".j_remainTimes").text(remainStr);
                        }else if($consumeType.val()=="TIMES"){
                            $(this).find(".j_remainTimes").text("");
                            $(this).find(".payMethod").val("MONEY");
                            $(this).find(".payMethod").change();
                        }
                    }else if($consumeType.val()=="TIMES"){
                        $(this).find(".j_remainTimes").text("");
                        $(this).find(".payMethod").val("MONEY");
                        $(this).find(".payMethod").change();
                    }
                }
            });
        },
        error:function(){
            nsDialog.jAlert("网络异常！");
        }
    });
}

function setOrderDisableAttrs(){
//    if ($.trim($("#customerId").val())) {
        var orderAttrs = ["brand","model","year","contact","engine","customer","mobile","landLine","fuelNumber","vehicleContact","vehicleMobile","vehicleColor","vehicleEngineNo","vehicleChassisNo","memberNumber"];
        var disableOrderAttrs = function(idName) {
            if ($.trim($("#" + idName).val())) {
                $("#" + idName).attr("disabled", true);
            }else{
                $("#" + idName).removeAttr("disabled");
            }
        }
        for (var i = 0,len = orderAttrs.length; i < len; i++) {
            disableOrderAttrs(orderAttrs[i]);
        }
//    }
    $(".item").each(function(i){
        if(G.isEmpty($("#washBeautyOrderItemDTOs"+i+"\\.businessCategoryName").val())){
            $("#washBeautyOrderItemDTOs"+i+"\\.businessCategoryName").removeAttr("disabled");
        }else{
            $("#washBeautyOrderItemDTOs"+i+"\\.businessCategoryName").attr("disabled","disabled");
        }
//        var consumeType = $("#washBeautyOrderItemDTOs"+i+"\\.consumeTypeStr").val();
//        if(consumeType=="TIMES"){
//            $("#washBeautyOrderItemDTOs"+i+"\\.consumeTypeStr").attr("disabled","disabled");
//        }else{
//            $("#washBeautyOrderItemDTOs"+i+"\\.consumeTypeStr").removeAttr("disabled");
//        }
    });
}

function disabledServiceAttrs(idPrefix){
    if(G.isEmpty(idPrefix)) return;
    if(!G.isEmpty($("#"+idPrefix+"\\.businessCategoryName").val())){
        $("#"+idPrefix+"\\.businessCategoryName").attr("disabled","disabled");
    }
//    var consumeType = $("#"+idPrefix+"\\.consumeTypeStr").val();
//    if(consumeType=="TIMES"){
//        $("#"+idPrefix+"\\.consumeTypeStr").attr("disabled","disabled");
//    }
}

function getCustomerInfoByLicenceNo(licenceNo){
    var data={
        licenceNo:licenceNo
    };
    APP_BCGOGO.Net.asyncAjax({
        url: "washBeauty.do?method=ajaxGetWashBeautyOrderByParameter",
        type: "POST",
        cache: false,
        data:data,
        dataType: "json",
        success: function (washBeautyOrderDTO) {
                initWashBeautyOrder(washBeautyOrderDTO);
        },
        error:function(){
            nsDialog.jAlert("网络异常！");
        }
    });
}

  function selChange(type, dom) {
            var idStr = dom.id.split(".");
            var price = parseFloat($("#" + idStr[0] + "\\.price").val());
            var total = parseFloat($("#total").val());
            price = isNaN(price) ? 0 : price;
            total = isNaN(total) ? 0 : total;
            if (type == "MONEY") {
                $(dom).parents("tr").find("input[id$='price']").show();
                $(dom).siblings("span[class='j_remainTimes']").hide();
                $(dom).parents("tr").find("input[id$='couponType'], input[id$='couponNo']").hide();
                $(dom).parents("tr").find("input[id$='couponType']").val('');
                $(dom).parents("tr").find("input[id$='couponNo']").val('');
            }
            if (type == "TIMES") {
                $(dom).parents("tr").find("input[id$='price']").hide();
                $(dom).siblings("span[class='j_remainTimes']").show();
                $(dom).parents("tr").find("input[id$='couponType'], input[id$='couponNo']").hide();
                $(dom).parents("tr").find("input[id$='couponType']").val('');
                $(dom).parents("tr").find("input[id$='couponNo']").val('');
            }
            if (type == "COUPON") {
                $(dom).parents("tr").find("input[id$='price']").hide();
                $(dom).siblings("span[class='j_remainTimes']").hide();
                $(dom).parents("tr").find("input[id$='couponType'], input[id$='couponNo']").show();
            }
            $("#" + dom.id.split(".")[0] + "\\.hiddenConsumeType").html(type);
            setTotal();
        }

        function memberChange(){
            var defaultItems = $("select[id$='.serviceId']");
            var total = 0;
            for (var i = 0; i < defaultItems.length; i++) {

                var defaultItemValue = defaultItems[i].options[defaultItems[i].options.selectedIndex].value;
                var type = $("#" + $(defaultItems[i])[0].id.split(".")[0] + "\\.hiddenConsumeType").html();
                var consumeTypeDom = $(defaultItems[i]).parents("tr").find("select[id$=consumeTypeStr]");
                if (null != type && "" != type) {
                    if ("TIMES" == type) {
                        consumeTypeDom.val('TIMES');
                    }
                    else if ("MONEY" == type) {
                        consumeTypeDom.val('MONEY');
                    } else if ("COUPON" == type) {
                        consumeTypeDom.val('COUPON');
                    }
                    selChange(type, consumeTypeDom[0]);
                }
                else {
                    if (($("select[name=serviceDTOSurplusTimesSelect] option[value=" + defaultItemValue + "]").text() != 0) && ($("select[name=memberServiceIsOverDueSelect] option[value=" + defaultItemValue + "]").text() == 'false')) {
                        consumeTypeDom.val('TIMES');
                        type = "TIMES";
                    }
                    else {
                        consumeTypeDom.val('MONEY');
                        type = "MONEY";
                    }
                    selChange(type, consumeTypeDom[0]);
                }
            }
        }