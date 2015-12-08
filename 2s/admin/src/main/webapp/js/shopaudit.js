var establishedStr = "";//成立时间,  更新信息
var qualificationStr = "";// 资 质更新信息
var personnelStr = "";  //人 员 更新信息
var areaStr = "";//面积   更新信息
var busTimeStr = "";//  营业时间   更新信息
(function () {

    $().ready(function () {

        var regC = /[^\u4E00-\u9FA5]/g;//过滤掉非汉字字符
        var regC2 = /[\u4E00-\u9FA5]/g;//过滤掉汉字字符
        var regNum = /[0-9]/g;//过滤掉数字字符
        var regNum2 = /[^0-9]/g;//过滤掉非数字字符

        var regEmail = /^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+((\.[a-zA-Z0-9_-]{2,3}){1,2})$/;

        var regSy = new RegExp("[`~!@#$^&()=|\\\\{\\}%_\\+\"':;',\\[\\]<>/?~！@#￥……&（）——|{}【】‘；：”“'。，、？·～《》]", "g");

        //非电子邮件符号的验证
        var regSy2 = new RegExp("[`~!#$^&*()=|\\\\{\\}%_\\+\\-\"':;',\\[\\]<>/?~！#￥……&*（）——|{}【】‘；：”“'。，、？·～《》]", "g");

        //过滤掉所有特殊符号
        var regS = new RegExp("[`~!@#$^&*()=|\\\\{\\}%_\\+\\-\"':;',\\[\\].<>/?~！@#￥……&*（）——|{}【】‘；：”“'。，、？·～《》]", "g");


        $("#name").bind("change", function () {
            if (!$("#name").val() || !$("#name").val().replace(/(^\s*)|(\s*$)/g, ""))
                return;

            var r = bcgogo.get("backShop.do?method=checkshopname&name=" + $("#name").val());
            if (r === null) {
                return;
            } else if (typeof r == "string" && r == "false") {
                alert("本单位名称已经注册，请在店名后增加地区、路名等便于区别，谢谢！");
                return;
            }
        });

        //绑定下拉列表的值        select_province    select_city    select_township
        provinceBind();
        $("#select_province").bind("change", function () {
            cityBind(this)
        });
        $("#select_province").bind("change", function () {
            cityBind(this)
        });
        $("#select_city").bind("change", function () {
            //       licensePlate(this);
            townshipBind(this)
        });
        $("#select_township").bind("change", function () {
            if (this.selectedIndex != 0) {
                $("#areaId").val(this.value);

                $("#address")[0].value = $("#select_province")[0].options[$("#select_province")[0].selectedIndex].innerHTML
                    + $("#select_city")[0].options[$("#select_city")[0].selectedIndex].innerHTML
                    + $("#select_township")[0].options[$("#select_township")[0].selectedIndex].innerHTML;
            }
            else {
                $("#address")[0].value = $("#select_province")[0].options[$("#select_province")[0].selectedIndex].innerHTML
                    + $("#select_city")[0].options[$("#select_city")[0].selectedIndex].innerHTML;
            }
        });


        // 提取公用函数
        /**
         * 对目标对象的字符串值 进行正则过滤
         * @param object
         * @param regArr
         */
        function filterTextInputString(object, regArr) {
            if ((!document.all || window.event.propertyName == 'value') && object.value != object.defaultValue) {
                var targetString = object.value;
                for (var i = 0, len = regArr.length; i < regArr.length; i++) {
                    targetString = targetString.replace(regArr[i], '');
                }
                object.value = targetString;
            }
        }

        $("#legalRep")[0][document.all ? "onpropertychange" : "oninput"] = function () {
            filterTextInputString(this, [regNum, regS]);
        };

        $("#mobile")[0][document.all ? "onpropertychange" : "oninput"] = function () {
            filterTextInputString(this, [regNum2]);
        };

        $("#landline")[0][document.all ? "onpropertychange" : "oninput"] = function () {
            filterTextInputString(this, [regSy, regC2]);
        };

        $("#storeManager")[0][document.all ? "onpropertychange" : "oninput"] = function () {
            filterTextInputString(this, [regC]);
        };

        $("#storeManagerMobile")[0][document.all ? "onpropertychange" : "oninput"] = function () {
            filterTextInputString(this, [regNum2]);
        };

        $("#storeManagerMobile").bind("change", function () {

            if (!$("#storeManagerMobile")[0].value || !$("#storeManagerMobile")[0].value.replace(/(^\s*)|(\s*$)/g, "") || isNaN($("#storeManagerMobile")[0].value)) return;

            var r = bcgogo.get("backShop.do?method=checkstoremanagermobile&storeManagerMobile=" + $("#storeManagerMobile")[0].value);
            if (r === null) {
                return;
            }
            else if (typeof r == "object" && r.stat == "false") {
                if ($("#input_count")[0]) {
                    document.body.removeChild($("#input_count")[0]);
                }

                if (confirm("您用此手机已经注册了其他店面，现在新增注册管理" + $("#name")[0].value + "店")) {
                    var input = $("<input>")[0];
                    input.type = "hidden";
                    input.id = "input_count";
                    input.value = r.count;
                    document.body.appendChild(input);
                }
                else {
                    return;
                }
            }
        });

        $("#qq")[0][document.all ? "onpropertychange" : "oninput"] = function () {
            filterTextInputString(this, [regNum2]);
        };

        $("#email")[0][document.all ? "onpropertychange" : "oninput"] = function () {
            filterTextInputString(this, [regC2, regSy2]);
        };

        $("#email").bind("change", function () {
            if ($("#input_email")[0].value != "" && !regEmail.test($("#input_email").val())) {
                alert("非法电子邮箱格式");
            }
        });

        $("#softPrice")[0][document.all ? "onpropertychange" : "oninput"] = function () {
            filterTextInputString(this, [regNum2]);
        };
        $("#established").bind("change", function () {
            establishedStr = "&established=" + encodeURI(encodeURI($("#established").val()))
        });
        $("#qualification").bind("change", function () {
            qualificationStr = "&qualification=" + encodeURI(encodeURI($("#qualification").val()))
        });
        $("#personnel").bind("change", function () {
            personnelStr = "&personnel=" + encodeURI(encodeURI($("#personnel").val()))
        });
        $("#area").bind("change", function () {
            areaStr = "&area=" + encodeURI(encodeURI($("#area").val()))
        });

        $("#input_starttime_hour").bind("change", function () {
            busTimeStr = "&businessHours=" + encodeURI(getbusinessHoursStr());
        });
        $("#input_starttime_minu").bind("change", function () {
            busTimeStr = "&businessHours=" + encodeURI(getbusinessHoursStr());
        });
        $("#input_endtime_hour").bind("change", function () {
            busTimeStr = "&businessHours=" + encodeURI(getbusinessHoursStr());
        });
        $("#input_endtime_minu").bind("change", function () {
            busTimeStr = "&businessHours=" + encodeURI(getbusinessHoursStr());
        });

    });

    //第一级菜单 select_province
    function provinceBind() {
        var r = APP_BCGOGO.Net.syncPost({"url":"backShop.do?method=selectarea", "data":"{parentNo:1}"});
        if (r == null) {
            return;
        }
        else {
            for (var i = 0, l = r.length; i < l; i++) {
                var option = $("<option>")[0];
                option.value = r[i].no;
                option.innerHTML = r[i].name;
                $("#select_province")[0].appendChild(option);
            }
        }
    }

    //第二级菜单 select_city
    function cityBind(select) {
        if (select.selectedIndex == 0) {
            $("#select_city").css("display", "none");
            $("#select_township").css("display", "none");
            $("#input_areaId").val("");
            $("#input_address").val("");
            return;
        }

        $("#select_township").css("display", "none");

        while ($("#select_city")[0].options.length > 1) {
            $("#select_city")[0].remove(1);
        }

        $("#areaId").val(select.value);
        $("#select_city").css("display", "block");

        $("#address")[0].value = $("#select_province")[0].options[$("#select_province")[0].selectedIndex].innerHTML;

        var r = bcgogo.get("backShop.do?method=selectarea&parentNo=" + select.value);
        if (r === null) {
            return;
        }
        else {
            for (var i = 0, l = r.length; i < l; i++) {
                var option = $("<option>")[0];
                option.value = r[i].no;
                option.innerHTML = r[i].name;
                $("#select_city")[0].appendChild(option);
            }
        }
    }


    //第三级菜单 select_township
    function townshipBind(select) {
        if (select.selectedIndex == 0) {
            $("#select_township").css("display", "none");
            $("#address")[0].value = $("#select_province")[0].options[$("#select_province")[0].selectedIndex].innerHTML;
            return;
        }
        else {

        }
        $("#areaId").val(select.value);
        $("#select_township").css("display", "block");

        $("#address")[0].value = $("#select_province")[0].options[$("#select_province")[0].selectedIndex].innerHTML
            + $("#select_city")[0].options[$("#select_city")[0].selectedIndex].innerHTML;

        var r = bcgogo.get("backShop.do?method=selectarea&parentNo=" + select.value);
        if (r === null || typeof(r) == "undefined") {
            return;
        }
        else {
            while ($("#select_township")[0].options.length > 1) {
                $("#select_township")[0].remove(1);
            }
            if (typeof(r) != "undefined" && r.length > 0) {
                for (var i = 0, l = r.length; i < l; i++) {
                    var option = $("<option>")[0];
                    option.value = r[i].no;
                    option.innerHTML = r[i].name;
                    $("#select_township")[0].appendChild(option);
                }
            }
        }
    }

    function getX(elem) {
        var x = 0;
        while (elem) {
            x = x + elem.offsetLeft;
            elem = elem.offsetParent;
        }
        return x;
    }

    function getY(elem) {
        var y = 0;
        while (elem) {
            y = y + elem.offsetTop;
            elem = elem.offsetParent;
        }
        return y;
    }

    function getbusinessHoursStr() {
        var timeTemp = "";
        if (($("#input_starttime_hour").val() == "00") &&
            ($("#input_starttime_minu").val() == "00") &&
            ($("#input_endtime_hour").val() == "23") &&
            ($("#input_endtime_minu").val() == "59")) {
            timeTemp = "24小时营业";
        } else {
            timeTemp = $("#input_starttime_hour").val() + "："
                + $("#input_starttime_minu").val() + "~"
                + $("#input_endtime_hour").val() + "："
                + $("#input_endtime_minu").val() + "营业";
        }
        return timeTemp;
    }
})();
var editItem = "";
function editInfo(spanId, inputId) {
    editItem = editItem + inputId + ";";
    $("#" + spanId).css("display", "none");
    $("#" + inputId).css("display", "block");
    if ($("#editCfm").css("display", "none")) {
        $("#editCfm").css("display", "");
    }
    if ($("#editCancel").css("display", "none")) {
        $("#editCancel").css("display", "");
    }
}

function editAddressInfo(spanId, inputId) {
    editItem = editItem + inputId + ";";
    $("#" + spanId).css("display", "none");
    $("#" + inputId).css("display", "block");
    $("#select_province").css("display", "block");
    if ($("#editCfm").css("display") == "none") {
        $("#editCfm").css("display", "");
    }
    if ($("#editCancel").css("display") == "none") {
        $("#editCancel").css("display", "");
    }
}

function editCfm(shopId) {
    //判断日期格式是否正确
    if ($("#established").css("display") != "none") {
        var established = $("#established").val();
        var reg = /^(\d{1,4})(-|\/)(\d{1,2})\2(\d{1,2})/;
        if ((!reg.test(established)) && ($.trim(established) != "")) {
            alert("成立时间格式不正确!(格式:yyyy-MM-dd)");
            return;
        }
    }
    var editItems = editItem.split(";");
    var url = "beshop.do?method=updateShop&shopId=" + shopId;
    for (var m = 0; m < editItems.length; m++) {
        if (editItems[m] != null && (editItems[m] != "")) {
            url = url + "&" + editItems[m] + "=" + encodeURI(encodeURI($("#" + editItems[m]).val()));
            if (editItems[m] == "address") {
                url = url + "&areaId=" + encodeURI(encodeURI($("#areaId").val()))
            }
        }
    }
    if (establishedStr != null && establishedStr != "") {
        url = url + establishedStr;
    }
    if (qualificationStr != null && qualificationStr != "") {
        url = url + qualificationStr;
    }
    if (personnelStr != null && personnelStr != "") {
        url = url + personnelStr;
    }
    if (areaStr != null && areaStr != "") {
        url = url + areaStr;
    }
    if (busTimeStr != null && busTimeStr != "") {
        url = url + busTimeStr;
    }
    window.location = url;
}

function editCancel() {
    editItem = "";
    $("#name").css("display", "none");
    $("#nameSpan").css("display", "");
    $("#name").val($("#nameSpan").text());

    $("#shortname").css("display", "none");
    $("#shortnameSpan").css("display", "");
    $("#shortname").val($("#shortnameSpan").text());

    $("#licencePlate").css("display", "none");
    $("#licencePlateSpan").css("display", "");
    $("#licencePlate").val($("#licencePlateSpan").text());

    $("#mobile").css("display", "none");
    $("#mobileSpan").css("display", "");
    $("#mobile").val($("#mobileSpan").text());

    $("#landline").css("display", "none");
    $("#landlineSpan").css("display", "");
    $("#landline").val($("#landlineSpan").text());

    $("#storeManager").css("display", "none");
    $("#storeManagerSpan").css("display", "");
    $("#storeManager").val($("#storeManagerSpan").text());

    $("#legalRep").css("display", "none");
    $("#legalRepSpan").css("display", "");
    $("#legalRep").val($("#legalRepSpan").text());

    $("#qq").css("display", "none");
    $("#qqSpan").css("display", "");
    $("#qq").val($("#qqSpan").text());

    $("#storeManagerMobile").css("display", "none");
    $("#storeManagerMobileSpan").css("display", "");
    $("#storeManagerMobile").val($("#storeManagerMobileSpan").text());

    $("#email").css("display", "none");
    $("#emailSpan").css("display", "");
    $("#email").val($("#emailSpan").text());

    $("#softPrice").css("display", "none");
    $("#softPriceSpan").css("display", "");
    $("#softPrice").val($("#softPriceSpan").text());

    $("#address").css("display", "none");
    $("#addressSpan").css("display", "");
    $("#address").val($("#addressSpan").text());
    $("#areaId").val($("#hidden_areaId").val());

    $("#editCfm").css("display", "none");
    $("#editCancel").css("display", "none");

    $("#select_province").css("display", "none");
    $("#select_province").val("");
    $("#select_city").css("display", "none");
    $("#select_city").val("");
    $("#select_township").css("display", "none");
    $("#select_township").val("");

    if ($("#hidden_busTime").val()) {
        if ($("#hidden_busTime").val() == "24小时营业") {
            $("#input_starttime_hour").val("00");
            $("#input_starttime_minu").val("00");
            $("#input_endtime_hour").val("23");
            $("#input_endtime_minu").val("59");
        }
        else {
            var str = $("#hidden_busTime").val();
            str = str.substring(0, str.length - 2);
            var str1 = str.split("~");
            var str2 = str1[0].split("：");
            var str3 = str1[1].split("：");
            $("#input_starttime_hour").val(str2[0]);
            $("#input_starttime_minu").val(str2[1]);
            $("#input_endtime_hour").val(str3[0]);
            $("#input_endtime_minu").val(str3[1]);
        }
    }
}