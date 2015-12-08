//TODO 在main.jsp中被引用，用于查询和下拉车牌号
var selectItemNum = -1;
var selectmore = -1;
var domTitle;
var selectValue = '';

var bcgogoAjaxQuery = APP_BCGOGO.Module.wjl.ajaxQuery;
var foo = APP_BCGOGO.Validator;
$().ready(function() {
    document.onclick = function(e) { //todo 当点击车牌号下拉外围，下拉建议隐藏
        var e = e || event;
        var target = e.srcElement || e.target;
        if (!target || !target.id) {
            $("#div_brandvehicle").hide();
            selectItemNum = -1;
        }
    }

    var elementCarNo = document.getElementById("m_text_id");

    $("#m_text_id").bind("keyup",function (e) {  //TODO 车牌号文本框键盘事件
        webChangemain(this);
        }).bind("click", function (e) {
            webChangemain(this);
    });

    $("#m_text_id").click(function() {
        jQuery("#div_brand").css({ 'display':'none'});
    });

    function webChangemain(thisObj) {
        if (!elementCarNo.value) {
            $("#div_brandvehicle").hide();
        }else {
            elementCarNo.value = elementCarNo.value.replace(/[\ |\\]/g, ""); //TODO 排除空格和反斜杠
            if (GLOBAL.Lang.isEmpty(elementCarNo.value)) return;
            searchSuggestionmain(thisObj, elementCarNo.value, "notclick");
        }
    }

    //TODO 下拉建议键盘上下键快速选取   这些方法都统一在common.js中，但仍有一些零散的代码因为一些需要，仍然保留。需要再次重写
    $("#m_text_id").keydown(function(e) {
        var e = e || event;
        var eventKeyCode = e.witch || e.keyCode;

        if ($("#div_brandvehicle").css("display") == "block") {
            if (eventKeyCode == 38 && (selectItemNum - 1 >= 0 || selectItemNum == 0 || selectItemNum == -1)) {
                if (domTitle == 'brand' || domTitle == 'model' || domTitle == 'product_name') {
                    selectItemNum = selectItemNum == 0 || selectItemNum == -1 ? (selectmore + 1) : selectItemNum;
                } else {
                    selectItemNum = selectItemNum == 0 || selectItemNum == -1 ? (selectmore) : selectItemNum;
                }
                $("#Scroller-Container_id > a").removeAttr("class");
                $("#selectItem" + (selectItemNum - 1)).mouseover();
            } else if (eventKeyCode == 40) {
                if (domTitle == 'brand' || domTitle == 'model' || domTitle == 'product_name') {
                    selectItemNum = selectItemNum == selectmore ? -1 : selectItemNum;
                } else {
                    selectItemNum = selectItemNum == selectmore - 1 ? -1 : selectItemNum;
                }
                $("#Scroller-Container_id > a").removeAttr("class");
                $("#selectItem" + (selectItemNum + 1)).mouseover();
            } else if (selectItemNum != -1 && eventKeyCode == 13) {
                $("#selectItem" + (selectItemNum)).click();
            }
        }
    });
    //TODO  车牌号文本框，回车事件处理功能
    var locaono = '';

    $("#m_text_id").keydown(function(event) {
        var keyName = GLOBAL.Interactive.keyNameFromEvent(event);
        if (keyName == "enter") {

            if (!selectValue) {
                if ($('#m_text_id').val()) {
                    var nameValue = $('#m_text_id').val();
                    var ResultStr = "";
                    var ResultStr1 = "";
                    //去除空格    //TODO 车牌号字符串内部不允许有空格
                    Temp = nameValue.split(" ");
                    for (i = 0; i < Temp.length; i++)
                        ResultStr1 += Temp[i];
                    //去除横杠 "-"   //TODO 车牌号字符串内部不允许有“-”
                    Temp1 = ResultStr1.split("-");
                    for (i = 0; i < Temp1.length; i++)
                        ResultStr += Temp1[i];
                    if ($('#m_text_id').val() != $('#m_text_id').defaultValue) {
                        var nameValue = ResultStr;
                        if ((nameValue.length == 5 || nameValue.length == 6) && foo.stringIsCharacter(nameValue)) { //前缀
                                var r = APP_BCGOGO.Net.syncGet({url:"product.do?method=userLicenseNo",dataType:"json"});
                                if (r.length == 0) return;
                                else {
                                    var locaono = r[0].localCarNo;
                                    $("#m_text_id").val((locaono + $("#m_text_id").val()).toUpperCase());
                                    locaono = '';
                                }
                        } else {
                            if (!APP_BCGOGO.Validator.stringIsLicensePlateNumber(ResultStr)) {
                                alert("输入的车牌号码不符合规范，请检查！");
                                return;
                            }
                        }
                            window.location.assign('txn.do?method=getRepairOrderByVehicleNumber&task=maintain&vehicleNumber=' + ($("#m_text_id").val() == '车牌号' ? '' : $("#m_text_id").val()));
                                    }
                    else {
                        window.location.assign('customer.do?method=carindex');
                    }
                }
            }
        }
    });  //end

});

//TODO 将车牌号的JSON，组装成下拉建议
function ajaxStyleForVehicleNomain(domObject, jsonStr) {
    domTitle = domObject.name;
    var offsetHeight = $(domObject).height();
    suggestionPosition(domObject, 0, offsetHeight, "div_brandvehicle")
    selectmore = jsonStr.length;
    if (selectmore <= 0) {//TODO 没有数据，隐藏下拉框
        $("#div_brandvehicle").hide();
    }
    else {
        $("#Scroller-Container_vehicleid").html("");
        for (var i = 0; i < jsonStr.length; i++) {
            var a = $("<a id='selectItem" + i + "'></a>");
            a
                .html(jsonStr[i].carno)
                .css("display", "block")
                .css("width", "100%")
                .css("padding", "0 2px 0 2px")
                .css("color", "#000000")
                .bind("mouseenter", function() {    //TODO 鼠标位于下拉框之上，为下拉框加上样式
                $("#Scroller-Container_vehicleid > a").removeAttr("class");
                    $(this)
                        .css("background-color","#0063DC")
                        .css("color", "#ffffff");
                var selectValueOther = $(this).html();
                if (selectValueOther) {
                    selectValue = selectValueOther;
                }
                else {
                    selectValue = "";
                }
                selectItemNum = parseInt(this.id.substring(10));
                })
                .bind("mouseleave", function(event){
                    $(this)
                        .css("background-color","#ffffff")
                        .css("color", "#000000");
                })
                .click(function() {   //TODO 单击选中下拉内容，跳转到维修单
                var valflag = domObject.value != $(this).html() ? true : false;
                $(domObject).val($(this).html());
                var selectValueOther = $(this).html();
                window.location.assign('txn.do?method=getRepairOrderByVehicleNumber&task=maintain&vehicleNumber=' + (selectValueOther == '车牌号' ? '' : selectValueOther));
                $(domObject).blur();
                $("#div_brandvehicle").hide();
                $("#div_brandvehicle").focus();
                selectItemNum = -1;
            });
            $("#Scroller-Container_vehicleid").append(a);
        }
    }
    //弹出复选框的最后一项
    if ((domTitle == 'brand' || domTitle == 'model') && jsonStr.length == 9) {
        var a = $("<a id='selectItem" + (selectmore) + "'></a>");
        a.html("更多");
        a.mouseover(function() {
            $("#Scroller-Container_vehicleid > a").removeAttr("class");
            $(this).attr("class", "hover");
            selectItemNum = parseInt(this.id.substring(10));
        });
        a.click(function() {
            $("#div_brand").css({'display':'none'});
            $("#iframe_PopupBox").attr("src", encodeURI("product.do?method=createsearchvehicleinfo&domtitle="
                + domTitle + "&brandvalue=" + $("#brand").val()));
            $("#iframe_PopupBox").css({'display':'block'});
            var selectValueOther = $(this).html();
            window.location.assign('txn.do?method=getRepairOrderByVehicleNumber&task=maintain&vehicleNumber=' + (selectValueOther == '车牌号' ? '' : selectValueOther));
            if (typeof(selectValueOther) != "undefined") {
                selectValue = selectValueOther;
            }
            else {
                selectValue = "";
            }
            Mask.Login();
        });
        $("#Scroller-Container_vehicleid").append(a);
    }
}

//TODO 查询车牌号下拉
function searchSuggestionmain(domObject, elementCarNo, eventStr) { //车辆信息查询
    var searchWord = (eventStr == "click" ? "" : domObject.value);
    var ajaxUrl = "product.do?method=searchlicenseplate";
    var ajaxData = {
        plateValue:searchWord
    }
    LazySearcher.lazySearch(ajaxUrl, ajaxData, function(jsonStr) {
        ajaxStyleForVehicleNomain(domObject, jsonStr);
    });
}
