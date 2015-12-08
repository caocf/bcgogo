//TODO 主要用于维修单的车牌号搜索，涉及到车牌号搜索的共有3个JS文件。 vehiclelicenceNo.js  vehiclenosolr.js vehiclenosolrheader.js
var selectItemNum = -1;
var selectmore = -1;
var domTitle;
var selectValue = '';

var bcgogoAjaxQuery = APP_BCGOGO.Module.wjl.ajaxQuery;

$(document).ready(function() {

    var elementCarNo = document.getElementById("licenceNo");

    $("#licenceNo").bind("keyup", function(e) {  //TODO 维修单车牌号键盘事件
        var eventKeyCode = e.which || e.keyCode;
        if (GLOBAL.Interactive.keyNameFromKeyCode(eventKeyCode).search(/left|up|right|down/g) == -1) {
            webChangelicenceNo(this,eventKeyCode);
        }
    });

    $("#licenceNo").bind("click", function(e) {
        var keycode = e.which || e.keyCode;
        var customerId = $("#customerId").val();
        if (GLOBAL.Lang.isEmpty(customerId)){
            webChangelicenceNo(this,keycode);
        }else{
            searchLicenceNoByCustomerId(this, customerId, false);
        }
//        searchOrderSuggestion(this, null);
    });

    //TODO 以下代码与head.js中的相应的代码重复   BEGIN-->
    //检查输入中的非法字符
    function checkChar(InString) {
        for (Count = 0; Count < InString.length; Count++) {
            TempChar = InString.substring(Count, Count + 1);
            if (!checkshuzi(TempChar) && !checkzimu(TempChar) && !checkhanzi(TempChar)) {
                return (true);
            }
        }
        return (false);
    }

    //判断数字
    function checkshuzi(shuziString) {
        return shuziString.match(/\d/g) != null;
    }

//判断字母
    function checkzimu(zimuString) {
        return zimuString.match(/[a-z]/ig) != null;
    }

//判断汉字

    function checkhanzi(hanziString) {
        return hanziString.match(/[^ -~]/g) != null
    }

    //TODO <-- END

    //enter key event for licenceNo

    //TODO 车牌号文本框回车事件
    $("#licenceNo").keydown(function(event) {
        var keyName = GLOBAL.Interactive.keyNameFromEvent(event);
        if (keyName == "enter") {
            event.preventDefault();
            GLOBAL.Event.stopPropagation(event);
            jQuery(this).unbind("blur");
            var This = this;
            if (!selectValue) {
                if ($("#licenceNo")[0].value) {
                    var nameValue = $('#licenceNo').val();
                    var resultStr = nameValue;
                    //去空格，去短横
                    resultStr = nameValue.split(" ").join("").split("-").join("");
                    if (!APP_BCGOGO.Validator.stringIsLicensePlateNumber(resultStr)) {
                        alert("输入的车牌号码不符合规范，请检查！");
                        return;
                    }
                    if (checkChar(resultStr))return;
                    else if ($('#licenceNo').val() != $('#licenceNo').defaultValue) {
                        if ($("#licenceNo") != null || $("#licenceNo") != '') {
                            if (resultStr.length == 5 && !checkhanzi(resultStr)) { //前缀
                                var r;
                                APP_BCGOGO.Net.syncGet({url: "product.do?method=userLicenseNo",
                                    dataType: "json", success: function (json) {
                                        r = json;
                                    }
                                });
                                if (r.length == 0) return;
                                else {
                                    var locaono = r[0].localCarNo;
                                    $("#licenceNo").val((locaono + $("#licenceNo").val()).toUpperCase());
                                }
                                locaono = '';
                            }
                        }
                        if (getOrderType() == "INSURANCE_ORDER") {
                            var date = {"licenceNo": $("#licenceNo").val()};
                            initInsuranceCustomerAndVehicle(ajaxToGetCustomerInfo(date));
                        } else {
                            var ajaxUrl = "txn.do?method=getRepairOrderByVehicleNumber&type=ajax&btnType="
                                + $("#pageType").val() + "&vehicleNumber=" + encodeURIComponent($("#licenceNo").val());
                            APP_BCGOGO.Net.syncAjax({url: ajaxUrl, dataType: "json", success: function (json) {
                                initCustomerVehicleAndOrder(json);
                            }});
                        }
                    }
                    else {
                        window.location.assign('customer.do?method=carindex');
                    }
                }
            }//end
            else {
                if (getOrderType() == "INSURANCE_ORDER") {
                    var date = {"licenceNo": $("#licenceNo").val()};
                    initInsuranceCustomerAndVehicle(ajaxToGetCustomerInfo(date));
                } else {
                    var ajaxUrl = "txn.do?method=getRepairOrderByVehicleNumber&type=ajax&btnType="
                        + $("#pageType").val() + "&vehicleNumber=" + encodeURIComponent(selectValue);
                    APP_BCGOGO.Net.syncAjax({url: ajaxUrl, dataType: "json", success: function (json) {
                        initCustomerVehicleAndOrder(json);
                    }});
                }
                elementCarNo.value = selectValue;
                selectValue = "";
            }
//            $("#licenceNo").blur();
            $("#div_brandvehiclelicenceNo").hide();
            selectItemNum = -1;
            $(this).bind("blur", licenceNoBlurHandler);
            if (getOrderType() != "INSURANCE_ORDER") {
                if ($(this).val() != "") {
                    setTimeout(function () {
                        var searchcompleteMultiselect = APP_BCGOGO.Module.searchcompleteMultiselect;
                        searchOrderSuggestion(searchcompleteMultiselect, $("#licenceNo")[0], "");
                        $("#div_brandvehiclelicenceNo").hide();
                    }, 100);
                }
            }
        }
    });
    $("#licenceNo").bind("blur", licenceNoBlurHandler);


    //TODO 车牌号下拉建议查询
    function webChangelicenceNo(thisObj,keycode) {
        if (!elementCarNo.value) {
            $("#div_brandvehiclelicenceNo").hide();
        }else{
            elementCarNo.value = elementCarNo.value.toUpperCase().replace(/[^A-Z\d\u4e00-\u9fa5\-_\(\)]+/g, "");
            searchSuggestionlicenceNo(thisObj, elementCarNo.value, "notclick",keycode);
        }
    }

    //TODO 下拉建议上下键快速选取内容  Begin-->
    $("#licenceNo").keydown(function(e) {
        var e = e || event;
        var eventKeyCode = e.witch || e.keyCode;
        if ($("#div_brandvehiclelicenceNo").css("display") == "block") {
            if (eventKeyCode == 38 && (selectItemNum - 1 >= 0 || selectItemNum == 0 || selectItemNum == -1)) {
                if (domTitle == 'brand' || domTitle == 'model' || domTitle == 'product_name') {
                    selectItemNum = selectItemNum == 0 || selectItemNum == -1 ? (selectmore + 1) : selectItemNum;
                } else {
                    selectItemNum = selectItemNum == 0 || selectItemNum == -1 ? (selectmore) : selectItemNum;
                }
                $("#Scroller-Container_idlicenceNo > a").removeAttr("class");
                $("#selectItem" + (selectItemNum - 1)).mouseover();
            } else if (eventKeyCode == 40) {
                if (domTitle == 'brand' || domTitle == 'model' || domTitle == 'product_name') {
                    selectItemNum = selectItemNum == selectmore ? -1 : selectItemNum;
                } else {
                    selectItemNum = selectItemNum == selectmore - 1 ? -1 : selectItemNum;
                }
                $("#Scroller-Container_idlicenceNo > a").removeAttr("class");
                $("#selectItem" + (selectItemNum + 1)).mouseover();
            } else if (selectItemNum != -1 && eventKeyCode == 13) {
                $("#selectItem" + (selectItemNum)).click();
            }
        }
    });
    //TODO <-- END
});

var searchLicenceNoByCustomerId = function (domObject, customerId, isDefaultVehicle) {
    if (!customerId) return;
    var ajaxUrl = "product.do?method=searchLicenceNoByCustomerId";
    var ajaxData = {customerId: customerId};
    bcgogoAjaxQuery.setUrlData(ajaxUrl, ajaxData);
    bcgogoAjaxQuery.ajaxQuery(function (jsonStr) {
        if (getOrderType() == "INSURANCE_ORDER") {
            if (jsonStr.length == 1){
                $("#licenceNo").val(jsonStr[0].carno);
                $("#vehicleId").val(jsonStr[0].id);
                $("#brand").val(jsonStr[0].brand);
                $("#model").val(jsonStr[0].model);
                $("#chassisNumber").val(jsonStr[0].chassisNumber);
                $("#engineNumber").val(jsonStr[0].engineNo);
            } else if (jsonStr.length > 1) {
                ajaxStylelicenceNo(domObject, jsonStr);
            } else {
                $("#licenceNo").val("");
                $("#vehicleId").val("");
            }
        } else {
            if (jsonStr.length == 1 && !jQuery("#licenceNo").val() && isDefaultVehicle) {
                jQuery("#licenceNo").val(jsonStr[0].carno);
                if (getOrderType() == "INSURANCE_ORDER") {
                    var date = {"licenceNo": $("#licenceNo").val()};
                    initInsuranceCustomerAndVehicle(ajaxToGetCustomerInfo(date));
                } else {
                    ajaxToGetVehicleInfo(jsonStr[0].carno);
                }
            }
            else if (!isDefaultVehicle)
                ajaxStylelicenceNo(domObject, jsonStr);
        }

    });
}

function ajaxToGetVehicleInfo(resultStr) {
    var ajaxUrl = "txn.do?method=getRepairOrderByVehicleNumber&type=ajax&btnType=" + jQuery("#pageType").val()
        + "&vehicleNumber=" + encodeURIComponent(resultStr);
    bcgogoAjaxQuery.setUrlData(ajaxUrl, {});
    bcgogoAjaxQuery.ajaxQuery(function (jsonStr) {
        initCustomerVehicleAndOrder(jsonStr);
    });
}

function ajaxStylelicenceNo(domObject, jsonStr) {
    domTitle = domObject.name;
    var offsetHeight = $(domObject).height();
    suggestionPosition(domObject, 0, offsetHeight + 8, "div_brandvehiclelicenceNo");
    if (jsonStr.length <= 0) {
        $("#div_brandvehiclelicenceNo").hide();
    }
    else {   //TODO 下拉建议定位
        $("#Scroller-Container_idlicenceNo").html("");
        $("#div_brandvehiclelicenceNo").css({
            'overflow-x':"hidden",
            'overflow-y':"auto"
        });
        for (var i = 0; i < jsonStr.length; i++) {
            var a = $("<a id='selectItem" + i + "'></a>");
            a.attr("vehicleInfo",(JSON.stringify(jsonStr[i])));
            a.html(jsonStr[i].carno);
            a.mouseover(function() {
                $("#Scroller-Container_idlicenceNo > a").removeAttr("class");
                $(this).attr("class", "hover");
                selectValue = $(this).html();
                var selectValueOther = $(this).html();
                if (typeof(selectValueOther) != "undefined") {
                    selectValue = selectValueOther;
                }
                else {
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
                var resultStr = nameValue;
                //去空格，去短横
                resultStr = nameValue.split(" ").join("").split("-").join("");
                if(!APP_BCGOGO.Validator.stringIsLicensePlateNumber(resultStr)){
                    alert("输入的车牌号码不符合规范，请检查！");
                    return;
                }
                if (getOrderType() == "INSURANCE_ORDER") {
                    var date = {"licenceNo": $("#licenceNo").val()};
                    initInsuranceCustomerAndVehicle(ajaxToGetCustomerInfo(date));
                } else {
                    ajaxToGetVehicleInfo(nameValue);
                }

                $("#div_brandvehiclelicenceNo")[0].style.display = "none";

                $(domObject).change();
                if (getOrderType() != "INSURANCE_ORDER") {
                    setTimeout(function () {
                        var searchcompleteMultiselect = APP_BCGOGO.Module.searchcompleteMultiselect;
                        searchOrderSuggestion(searchcompleteMultiselect, domObject, "");
                    }, 100);
                }
            });
            $("#Scroller-Container_idlicenceNo").append(a);
        }
    }

    //弹出复选框的最后一项
    if ((domTitle == 'brand' || domTitle == 'model') && jsonStr.length == 9) {
        var a = $("<a id='selectItem" + (jsonStr.length) + "'></a>");
        a.html("更多");
        a.mouseover(function() {
            $("#Scroller-Container_idlicenceNo > a").removeAttr("class");
            $(this).attr("class", "hover");
            selectItemNum = parseInt(this.id.substring(10));
        });
        a.click(function() {
            $("#div_brandvehiclelicenceNo").css({'display':'none'});
            $("#iframe_PopupBox").attr("src", encodeURI("product.do?method=createsearchvehicleinfo&domtitle="
                + domTitle + "&brandvalue=" + $("#brand").val()));
            $("#iframe_PopupBox").css({'display':'block'});
            Mask.Login();
        });
        $("#Scroller-Container_idheader").append(a);
    }
}
//TODO <--END

//TODO 车牌号搜索
function searchSuggestionlicenceNo(domObject, elementCarNo, eventStr,keycode) { //车辆信息查询
    var searchWord = (eventStr == "click" ? "" : domObject.value);
    var ajaxUrl = "product.do?method=searchlicenseplate";
    var ajaxData = {
        plateValue:searchWord,
        now:new Date()
    }
    LazySearcher.lazySearch(ajaxUrl, ajaxData, function(json) {
        if(!G.isEmpty(json[0])){
            G.completer({
                    'domObject':domObject,
                    'keycode':keycode,
                    'title':json[0].licenceNo}
            );
        }
        ajaxStylelicenceNo(domObject, json);
    });
}


function licenceNoBlurHandler(event) {        //tag blur
    if (selectValue) {
        return;
    } else {
        $("#licenceNo").val($("#licenceNo").val().toUpperCase());
        $("#div_brandvehiclelicenceNo")[0].style.display = "none";
        var nameValue = $("#licenceNo").val();
        if (GLOBAL.Lang.isEmpty(nameValue)){
            if(getOrderType() == "INSURANCE_ORDER" && typeof  initInsuranceCustomerAndVehicle == "function"){
                initInsuranceCustomerAndVehicle(null);
            }else{
                initCustomerVehicleAndOrder(null);
            }
            return;
        }

        var resultStr = nameValue;
        //去空格，去短横
        resultStr = nameValue.split(" ").join("").split("-").join("");
        if (!APP_BCGOGO.Validator.stringIsLicensePlateNumber(resultStr)) {
//            alert("输入的车牌号码不符合规范，请检查！");
            nsDialog.jAlert("输入的车牌号码不符合规范，请检查！",null,function(){
                $("#licenceNo").focus();
            });
            return;
        }
        if ($("#licenceNo") != null || $("#licenceNo") != '') {
            if (resultStr.length == 5 && !checkhanzi(resultStr)) { //前缀
                var r;
                APP_BCGOGO.Net.syncGet({url: "product.do?method=userLicenseNo",
                    dataType: "json", success: function (json) {
                        r = json;
                    }
                });
                if (r.length == 0) return;
                else {
                    var locaono = r[0].localCarNo;
                    $("#licenceNo").val((locaono + $("#licenceNo").val()).toUpperCase());
                }
                locaono = '';
            }
        }
        if (getOrderType() == "INSURANCE_ORDER") {
            var date = {"licenceNo": $("#licenceNo").val()};
            initInsuranceCustomerAndVehicle(ajaxToGetCustomerInfo(date));
        } else {
            var ajaxUrl = "txn.do?method=getRepairOrderByVehicleNumber&type=ajax&btnType="
                + $("#pageType").val() + "&vehicleNumber=" + encodeURIComponent($("#licenceNo").val());
            APP_BCGOGO.Net.syncAjax({url: ajaxUrl, dataType: "json", success: function (json) {
                initCustomerVehicleAndOrder(json);
            }});
        }
    }
    selectItemNum = -1;
    selectValue = "";
}

function ajaxToGetCustomerInfo(date){
    var returnJson ;
    APP_BCGOGO.Net.syncPost({
        url: "customer.do?method=ajaxGetCustomerInfo",
        dataType: "json",
        data: date,
        success: function (json) {
            returnJson = json;
        },
        error: function () {
            nsDialog.jAlert("网络异常！");
        }
    });
    return returnJson;
}
