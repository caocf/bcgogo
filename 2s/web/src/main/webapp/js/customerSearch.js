var selectItemNum = -1;
var selectmore = -1;
var domTitle;
var selectValue = '';

var bcgogoAjaxQuery = APP_BCGOGO.Module.wjl.ajaxQuery;
$().ready(function() {
    var elementCustomer = document.getElementById("customer");
    $("#customer").live("keyup", function(e) {
//        webChangeCustomer(this);
    });
    $("#contact").live("focus", function() {
        $("#div_brandCustomer").hide();
        if (!$("#customerId").val()) {
            $("#qiankuangWrap").hide();
        }
    });

    $("#customer").bind("change", function() {
        if(getOrderType() == "SALE"){
            return;//销售单先不用这个方法，其他页面这个方面待确认，我们系统中用了很多input replace value处理方式，change 事件基本失效。 by qxy
        }
        if (!selectValue) {
            if ($("#customerId").val()) {
                var ajaxUrl = "sale.do?method=searchCustomerById";
                var ajaxData = {shopId:$("#shopId").val(),customerId:$("#customerId").val()};
                bcgogoAjaxQuery.setUrlData(ajaxUrl, ajaxData);
                bcgogoAjaxQuery.ajaxQuery(function(data) {
                    if (data.infos.length > 0) {
                        if ($("#customer").val() == data.infos[0].customer) {
                            $("#div_brandCustomer").hide();
                            return;
                        }
                        else {
                            $("#customerId").val("");
                            $("#contact").val("");
                            $("#contactId").val(""); // add by zhuj 清空联系人id
                            $("#mobile").val("");
                            $("#landline").val("");
                            $("#returnInfo").val("");
                            $("#hiddenMobile").html("");
                            $("#qiankuangWrap").hide();
                            $("#div_brandCustomer").css("display", "none");

                            $("#receivable").html("0");

                            $("#payable").html("0");

                            initDuiZhanInfo();
                            isReadOnly();
                        }
                    }
                    else {
                        $("#div_brandCustomer").css("display", "none");
                    }
                });
            }
            $("#div_brandCustomer").hide();
        }
        selectItemNum = -1;
    })
       // .bind("blur",customerBlurCallBack);

   /* function customerBlurCallBack(){
        var customerName = $(this).val();
        if(!G.isEmpty(customerName)){
            // 先通过名称查询用户 如果存在重复 提示 是新增还是修改 设置标示符
            var $customerListJson = searchCustomerByCustomerName(customerName);
            // customer存在
            if($customerListJson){
                nsDialog.jConfirm("客户名称重复，是否新增？",null, function (returnVal) {
                    // 修改
                    if(!returnVal){
                        $("#isAdd").val("false");
                        $("#customerId").val($customerJson.idStr);
                        $("#contactId").val(G.Lang.normalize($customerJson.contactIdStr));
                        $("#contact").val($customerJson.contact);
                        $("#mobile").val($customerJson.contact.mobile);
                        // 灰化联系人
                        $("#contact").attr("disabled","disabled");
                        $("#mobile").attr("disabled","disabled");
                    }else{
                        $("#isAdd").val("true");
                        $("#customerId").val("");
                        $("#contactId").val("");
                        $("#contact").attr("disabled","");
                        $("#mobile").attr("disabled","");
                    }
                });
            }else{
                // nothing to do
            }
        }
    }
*/
    function searchCustomerByCustomerName(name) {
        var result = false;
        APP_BCGOGO.Net.syncPost({
            url:"customer.do?method=searchCustomerByName",
            data:{
                customerName:name
            },
            cache:false,
            dataType:"json",
            success:function(jsonStr) {
                if (!G.isEmpty(jsonStr.results) && !G.isEmpty(jsonStr.results[0])) {
                    return jsonStr;
                } else {
                    return null;
                }
            }
        });
    }

    function webChangeCustomer(thisObj) {
        if (!elementCustomer.value) {
            $("#div_brandCustomer").hide();
        } else {
            elementCustomer.value = elementCustomer.value.replace(/[\ |\\]/g, "");
            searchSuggestionCustomer(thisObj, elementCustomer.value, "notclick");
        }
    }

    $("#customer").keydown(function(e) {
        var keyCode = e.which;
        if ($("#div_brandCustomer").css("display") == "block") {
            if (keyCode == 38 && (selectItemNum - 1 >= 0 || selectItemNum == 0 || selectItemNum == -1)) {
                if (domTitle == 'brand' || domTitle == 'model' || domTitle == 'product_name') {
                    selectItemNum = selectItemNum == 0 || selectItemNum == -1 ? (selectmore + 1) : selectItemNum;
                } else {
                    selectItemNum = selectItemNum == 0 || selectItemNum == -1 ? (selectmore) : selectItemNum;
                }
                $("#Scroller-Container_idCustomer > a").removeAttr("class");
                $("#selectItem" + (selectItemNum - 1)).mouseover();
            } else if (keyCode == 40) {
                if (domTitle == 'brand' || domTitle == 'model' || domTitle == 'product_name') {
                    selectItemNum = selectItemNum == selectmore ? -1 : selectItemNum;
                } else {
                    selectItemNum = selectItemNum == selectmore - 1 ? -1 : selectItemNum;
                }
                $("#Scroller-Container_idCustomer > a").removeAttr("class");
                $("#selectItem" + (selectItemNum + 1)).mouseover();
            } else if (selectItemNum != -1 && keyCode == 13) {
                $("#selectItem" + (selectItemNum)).click();
                $(this).blur();
            }
        }
    });
});

function ajaxStyleCustomer(jsonStr, json) {
    domTitle = jsonStr.name;
    var offsetHeight = $(jsonStr).height();
    suggestionPosition(jsonStr, 0, offsetHeight + 8, "div_brandCustomer")
    if (json.length <= 0) {
        $("#div_brandCustomer").hide();
    }
    else {
        $("#Scroller-Container_idCustomer").html("");

        for (var i = 0; i < json.length; i++) {
            var $a = $("<a id='selectItem" + i + "'></a>");
            var titleValue = json[i].name;
            if (json[i].contact) {
                titleValue = titleValue + "+" + json[i].contact;
            }
            if (json[i].mobile) {
                titleValue = titleValue + "+" + json[i].mobile;
            } else if (json[i].landLine) {
                titleValue = titleValue + "+" + json[i].landLine;
            }
            $a.html(titleValue);
            $a.attr("title", titleValue);
            $a.css({"width":"290px","text-overflow":"ellipsis","overflow":"hidden","white-space":"nowrap"});

            $a.mouseover(function() {
                $("#Scroller-Container_idCustomer > a").removeAttr("class");
                $(this).attr("class", "hover");
                selectValue = json[$("#Scroller-Container_idCustomer > a").index($(this)[0])].name;// $(this).html();
                selectItemNum = parseInt(this.id.substring(10));
            });
            $a.mouseout(function() {
                selectValue = "";
            });
            $a.click(function() {
                $(jsonStr).val(json[$("#Scroller-Container_idCustomer > a").index($(this)[0])].name);
                var ajaxUrl = "sale.do?method=searchCustomerById";
                var ajaxData = {shopId:$("#shopId").val(),customerId:json[$("#Scroller-Container_idCustomer > a").index($(this)[0])].idStr};
                bcgogoAjaxQuery.setUrlData(ajaxUrl, ajaxData);
                bcgogoAjaxQuery.ajaxQuery(function(data) {
                    if (data.infos.length > 0) {
                        //原始数据清空
                        $("#customerId").val("");
                        $("#customer").val("");
                        $("#contact").val("");
                        $("#mobile").val("");
                        $("#hiddenMobile").val("");
                        $("#returnInfo").val("");

                        $("#customerId").val(data.infos[0].customerIdStr);
                        $("#customer").val(data.infos[0].customer);
                        $("#contact").val(data.infos[0].contact);
                        $("#mobile").val(data.infos[0].mobile);
                        $("#hiddenMobile").val(data.infos[0].mobile);
                        $("#returnInfo").remove();
                        isReadOnly();
                        //查询累计欠款
                        if ($("#customerId").val()) {
                            var ajaxUrl = "sale.do?method=getTotalDebts";
                            var ajaxData = {shopId:$("#shopId").val(),customerId:$("#customerId").val()};
                            bcgogoAjaxQuery.setUrlData(ajaxUrl, ajaxData);
                            bcgogoAjaxQuery.ajaxQuery(function(data) {
                                $("#customerConsume").html(data.totalAmount);
                                if(data.totalDebt){
                                    $("#receivable").html(data.totalDebt);
                                }else{
                                    $("#receivable").html("0");
                                }
                                if(data.totalReturnDebt){
                                    $("#payable").html(data.totalReturnDebt);
                                }else{
                                    $("#payable").html("0");
                                }

                                initDuiZhanInfo();
                            });
                        }
                    }
                }, function(XMLHttpRequest, error, errorThrown) {
                    //原始数据清空
                    $("#customerId").val('');
                    $("#customer").val("");
                    $("#contact").val("");
                    $("#mobile").val("");
                    $("#hiddenMobile").html("");
                    $("#returnInfo").val("");
                    isReadOnly();
                });
                $("#div_brandCustomer").hide();
            });
            selectValue = "";
            $("#Scroller-Container_idCustomer").append($a);
        }

    }

    //弹出复选框的最后一项
    if ((domTitle == 'brand' || domTitle == 'model') && json.length == 9) {
        var $a = $("<a id='selectItem" + (json.length) + "'></a>");
        $a.html("更多");
        $a.mouseover(function() {
            $("#Scroller-Container_idCustomer > a").removeAttr("class");
            $(this).attr("class", "hover");
            selectItemNum = parseInt(this.id.substring(10));
        });
        $a.click(function() {
            $("#div_brandCustomer").css({'display':'none'});
            $("#iframe_PopupBox").attr("src", encodeURI("product.do?method=createsearchvehicleinfo&domtitle="
                + domTitle + "&brandvalue=" + $("#brand").val()));
            $("#iframe_PopupBox").css({'display':'block'});
            Mask.Login();
        });
        $("#Scroller-Container_idheader").append($a);
    }
}

function searchSuggestionCustomer(node, elementCustomer, eventType) { //车辆信息查询
    var searchWord = eventType == "click" ? "" : node.value;
    var ajaxUrl = "txn.do?method=getCustomerName";
    var ajaxData = {
        name:searchWord
    };
    bcgogoAjaxQuery.setUrlData(ajaxUrl, ajaxData);
    bcgogoAjaxQuery.ajaxQuery(function(json) {
        ajaxStyleCustomer(node, json);
    }, function(XMLHttpRequest, error, errorThrown) {
        $("#div_brandCustomer").css({'display':'none'});
    });
}

function isReadOnly() {
    var infoNameList = ["contact","mobile"];
    for (var i = 0,len = infoNameList.length; i < len; i++) {
        $("#" + infoNameList[i]).attr("readonly", $.trim($("#" + infoNameList[i]).val()) != "");
    }
}

