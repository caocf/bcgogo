//TODO head.jsp中用于搜索车牌号
var selectItemNum = -1;
var selectmore = -1;
var domTitle;
var selectValue = "";
var count = 0;
var foo = APP_BCGOGO.Validator;
$(document).ready(function () {
    var selectNum = -1;
    var domObj;

    $("#vehicleNumber").click(function(e) {
        $(this).select();
    });

    //TODO 页头车牌号下拉建议，上下键快速选择 与COMMON.JS中的处理相同，只有div_brandvehicleheader不同  BEGIN-->
    $("#vehicleNumber").bind('keydown', function (e) {
        var e = e || event;
        var eventKeyCode = e.witch || e.keyCode;
        var target = e.target;
        $(target).blur(function () {
            selectNum = -1;
        });
        if ($("#div_brandvehicleheader").css("display") == "block") {
            var size = $("#Scroller-Container_idheader>a").size();
            $("#Scroller-Container_idheader> a").removeAttr("class");
            if (eventKeyCode == 38) {
                selectNum = selectNum <= 0 ? size - 1 : selectNum - 1;
                if (selectNum == (size - 1)) {
                    domObj = $("#Scroller-Container_idheader>a:last");
                    domObj.attr("class", "hover");
                    var selectValueOther = domObj.innerText;
                } else {
                    domObj = domObj.prev();
                    domObj.attr("class", "hover");
                }

            } else if (eventKeyCode == 40) {
                selectNum = selectNum >= (size - 1) ? 0 : selectNum + 1;
                if (selectNum == 0) {
                    domObj = $("#Scroller-Container_idheader>a:first");
                    domObj.attr("class", "hover");
                } else {
                    domObj = domObj.next();
                    domObj.attr("class", "hover");
                    var selectValueOther = domObj.innerText;
                    if (typeof(selectValueOther) != "undefined") {
                        selectValue = selectValueOther;
                    }
                    else {
                        selectValue = "";
                    }
                }
            }
            //xiala
            if (eventKeyCode == 13) {
                $("#selectItem" + (selectNum)).click();
                selectNum = -1;
            }
        }
    });
    //TODO <--END
});
$().ready(function () {
    var elementCarNo = document.getElementById("vehicleNumber");
    //TODO 头文件车牌号输入框查询键盘事件
    $("#vehicleNumber").bind("keyup", function (e) {
        var keycode= e.which || e.keyCode;
        webChangeheader(this,keycode);
    });

    $(elementCarNo).bind("blur", function () {
        //TODO 为文本框赋上下拉建议选中的值
        if (selectValue)
            elementCarNo.value = selectValue;
        $("#div_brandvehicleheader").hide();
        selectItemNum = -1;
        selectValue = "";
    });

    var locaono = '';
    $("#vehicleNumber").keydown(function (event) {   //TODO 车牌号文本框回车事件   与vehiclenosolr.js中相应代码重复  BEGIN-->
        var keyName = GLOBAL.Interactive.keyNameFromEvent(event);
        if (keyName == "enter") {
            if (!selectValue) {
                if ($('#vehicleNumber').val()) {
                    var nameValue = $('#vehicleNumber').val();
                    var ResultStr = "";
                    var ResultStr1 = "";
                    //去除空格
                    Temp = nameValue.split(" ");
                    for (i = 0; i < Temp.length; i++)
                        ResultStr1 += Temp[i];
                    //去除横杠 "-"
                    Temp1 = ResultStr1.split("-");
                    for (i = 0; i < Temp1.length; i++)
                        ResultStr += Temp1[i];
                    if ($('#vehicleNumber').val() != $('#vehicleNumber').defaultValue) {
                        var nameValue = ResultStr;
                        if ((nameValue.length == 5 || nameValue.length == 6)&&foo.stringIsCharacter(nameValue)) { //前缀
                            var r = APP_BCGOGO.Net.syncGet({url:"product.do?method=userLicenseNo", data:{},dataType:"json"});
                            if (!r) return;
                            else {
                                var locaono = r[0].localCarNo;
                                $("#vehicleNumber").val((locaono + $("#vehicleNumber").val()).toUpperCase());
                                locaono = '';
                            }
                        } else{
                            if(!APP_BCGOGO.Validator.stringIsLicensePlateNumber(ResultStr)){
                                alert("输入的车牌号码不符合规范，请检查！");
                                return;
                            }
                        }
                    }
                    openOrAssign('txn.do?method=getRepairOrderByVehicleNumber&vehicleNumber=' + ($("#vehicleNumber").val() == '车牌号' ? '' : $("#vehicleNumber").val()));
                } else {
                    openOrAssign('customer.do?method=carindex');
                }
            }
            else {
                openOrAssign('txn.do?method=getRepairOrderByVehicleNumber&vehicleNumber=' + (selectValue == '车牌号' ? '' : selectValue));
                selectValue = "";
            }
        }
    });  //end
    //todo <--END
    function webChangeheader(thisObj,keycode) {
        if (!elementCarNo.value) {  //todo 空值隐藏下拉建议
            $("#div_brandvehicleheader").hide();
        }
        else {
            elementCarNo.value = elementCarNo.value.replace(/[\ |\\]/g, ""); //TODO 不允许空格与斜杠
            searchSuggestionheader(thisObj, elementCarNo.value, "notclick",keycode);
        }
    }


});

//TODO 将包含车牌号的JSON组装成下拉建议
function ajaxStyleheader(domObject, jsonStr) {
    domTitle = domObject.name;
    var offsetHeight = $(domObject).height();
    suggestionPosition(domObject, 0, offsetHeight + 8, "div_brandvehicleheader");
    if (jsonStr.length <= 0) {
        $("#div_brandvehicleheader").hide();
    }
    else {
        $("#Scroller-Container_idheader").html("");
        $("#div_brandvehicleheader").css({
            'overflow-x':"hidden",
            'overflow-y':"auto"
        });

        for (var i = 0; i < jsonStr.length; i++) {
            var a = $("<a id='selectItem" + i + "'></a>");
            a.html(jsonStr[i].carno);
            a.mouseover(function () {
                $("#Scroller-Container_idheader > a").removeAttr("class");
                $(this).attr("class", "hover");
                var selectValueOther = $(this).html();
                selectValue = selectValueOther ? selectValueOther : "";
                selectItemNum = parseInt(this.id.substring(10));
            });
            a.click(function () {
                $(domObject).val($(this).html());
                var selectValueOther = $(this).html();
                selectValue = selectValueOther ? selectValueOther : "";
                $(domObject).blur();
                $("#div_brandvehicleheader").hide();
                selectItemNum = -1;
            });
            $("#Scroller-Container_idheader").append(a);
        }
    }

    //弹出复选框的最后一项
    if ((domTitle == 'brand' || domTitle == 'model') && jsonStr.length == 9) {
        var a = $("<a id='selectItem" + (jsonStr.length) + "'></a>");
        a.html("更多");
        a.mouseover(function () {
            $("#Scroller-Container_idheader > a").removeAttr("class");
            $(this).attr("class", "hover");
            selectItemNum = parseInt(this.id.substring(10));
        });
        a.click(function () {
            $("#div_brandvehicleheader").css({'display':'none'});
            $("#iframe_PopupBox").attr("src", encodeURI("product.do?method=createsearchvehicleinfo&domtitle="
                + domTitle + "&brandvalue=" + $("#brand").val()));
            $("#iframe_PopupBox").css({'display':'block'});
            Mask.Login();
        });
        $("#Scroller-Container_idheader").append(a);
    }
}

//TODO AJAX查询车牌号
function searchSuggestionheader(domObject, elementCarNo, eventStr,keycode) { //车辆信息查询
    var searchWord = eventStr == "click" ? "" : domObject.value;
    var ajaxUrl = "product.do?method=searchlicenseplate";
    var ajaxData = {plateValue:searchWord};
    LazySearcher.lazySearch(ajaxUrl, ajaxData, function (jsonStr) {
         if(!G.isEmpty(jsonStr[0])){
            G.completer({
                    'domObject':domObject,
                    'keycode':keycode,
                    'title':jsonStr[0].licenceNo}
            );
        }
        ajaxStyleheader(domObject, jsonStr);
    });
}

