//TODO 用于维修单的客户搜索
var selectItemNumCustomer = -1;
var selectmoreCustomer = -1;
var domTitle;
var selectValueCustomer = '';

var bcgogoAjaxQuery = APP_BCGOGO.Module.wjl.ajaxQuery;

$().ready(function() {

    var elementCustomer = document.getElementById("customer")||$("#customerName")[0];

    //TODO 客户名文本框键盘事件，用于下拉建议
    $("#customer,#customerName").live("keyup", function(e) {
        var keycode=e.which;
        webChangeCustomer(this,keycode);
    }).bind("focus",function(){
            if (getOrderType() == "INSURANCE_ORDER" && typeof customerFocus == "function") {
                eval(customerFocus());
            }
        });


    function webChangeCustomer(thisObj,keycode) {
        if (!elementCustomer.value) {
            $("#div_brand").css({'display':'none'});
        }else {
            elementCustomer.value = elementCustomer.value.replace(/[\ |\\]/g, "");
            searchSuggestionCustomer(thisObj, elementCustomer.value, "notclick",keycode);  //TODO 客户名下拉查询
        }
    }
    $("#customer").bind("blur", function(e) {
        if ($("#div_brand").css("display") == "block" && selectValueCustomer){
            return;
        } else if(getOrderType() == "INSURANCE_ORDER" && typeof customerBlur == "function"){
            eval(customerBlur());
            return;
        }else {
            if ($("#customerId").val()) {     //TODO  失去焦点，通过客户ID查询客户信息
                var ajaxUrl = "sale.do?method=searchCustomerById";
                var ajaxData = {shopId:$("#shopId").val(),customerId:$("#customerId").val()};
                bcgogoAjaxQuery.setUrlData(ajaxUrl, ajaxData);
                bcgogoAjaxQuery.ajaxQuery(function(data) {
                    if (data.infos.length > 0) {
                        if ($("#customer").val() == data.infos[0].customer) {   //TODO 取出客户名
                            $("#div_brandCustomer").css("display", "none");
                            return;
                        }
                        else {
                            $(".stock_bottom").hide();                        //todo  清楚上一个客户的洗车卡信息
                            $("#lastWashTime,#remainWashTimes").text('');
                            $("#washRemain,#todayWashTimes").val('');

                            $("#customerId").val("");
                            $("#contact").val("");
                            $("#mobile").val("");
                            $("#landLine").val("");
                            $("#customerConsume").html("");
                            $("#memberStatus").html("");
                            $("#memberType").html("");
                            $("#memberNumber").html("");
                            $("#hiddenMobile").html("");
                            $("#div_brand").css("display", "none");
                            $("#allDebt").css("display", "none");
                            $("#a_jiesuan").css("display", "none");
                            isReadOnly();
                        }
                    }
                    else {
                        $("#div_brandCustomer").css("display", "none");
                    }
                });
            }
            selectValueCustomer = "";
        }
    });

    jQuery("#callBackBuyCard").bind("click",function(){
        if (jQuery("#customerId").val() != "") {
            jQuery.ajax({
                    type:"POST",
                    url:"sale.do?method=getTotalDebts",
                    async:true,
                    data:{customerId:jQuery("#customerId").val()},
                    cache:false,
                    dataType:"json",
                    error:function(XMLHttpRequest, error, errorThrown) {
                    },
                    success:function(data) {
                        var totalDebt = data.totalDebt;
                        jQuery("#allDebt").html(totalDebt);
                        jQuery("#customerConsume").html(data.totalAmount);
                        jQuery("#allDebt").css("display", "none");
                        jQuery("#a_jiesuan").css("display", "none");
                        if (totalDebt * 1 > 0) {
                            jQuery("#allDebt").show();
                            jQuery("#a_jiesuan").show();
                        }
                        searchLicenceNoByCustomerId(jQuery("#licenceNo")[0], jQuery("#customerId").val(), true);
                    }
                }
            );

            $.ajax({
                    type:"POST",
                    url:"member.do?method=getMemberInfo",
                    async:true,
                    data:{customerId:jQuery("#customerId").val()},
                    cache:false,
                    dataType:"json",
                    error:function(XMLHttpRequest, error, errorThrown) {
                    },
                    success:function(data) {
                        var type=data.type;
                        var memberNo = data.memberNo;
                        var statusStr = data.statusStr;
                        var remainAmount = data.balance;
                        if(remainAmount==null){
                            remainAmount = 0;
                        }
                        $("#memberServiceDeadLineStr").html(G.normalize(data.serviceDeadLineStr));
                        $("#memberJoinDateStr").html(G.normalize(data.joinDateStr));
                        $("#memberRemainAmount").html(remainAmount);
                        $("#memberType").html(type);
                        $("#memberNumber").html(memberNo);
                        $("#memberStatus").html(statusStr);
                        $("#memberServiceInfo").empty();
                        if(G.isNotEmpty(data.memberServiceDTOs)){
                            $("#memberServiceCount").html(data.memberServiceDTOs.length);
                            $.each(data.memberServiceDTOs,function(index,memberServiceDTO){
                                $("#memberServiceInfo").append('<div style="overflow: hidden;"><div style="text-overflow :ellipsis;overflow: hidden;white-space: nowrap;" title="'+memberServiceDTO.serviceName+'" class="div left">'+memberServiceDTO.serviceName+'</div><div class="div right">'+memberServiceDTO.timesStr+'</div></div>');
                            });
                        }else{
                            $("#memberServiceCount").html(0);
                        }
                        if(!G.Lang.isEmpty(data.memberNo)){
                            $("#customerMemberInfoImg").css("display","inline-block");
                        }
                    }
                }
            );
        }
    });
});

//TODO 将包含客户名的JSON组装成下拉建议
function ajaxGetCustomerInfo(customerId) {
    var ajaxUrl = "sale.do?method=searchCustomerById";
    var ajaxData = {shopId: $("#shopId").val(), customerId: customerId};
    bcgogoAjaxQuery.setUrlData(ajaxUrl, ajaxData);
    bcgogoAjaxQuery.ajaxQuery(function (data) {
        if (data.infos.length > 0) {
            $("#customerId").val(data.infos[0].idStr);
            $("#customerName").val(data.infos[0].name).attr('title', data.infos[0].name);
            $("#contact").val(data.infos[0].contact);
            $("#mobile").val(data.infos[0].mobile);
            $("#landLine").val(data.infos[0].landLine);
            $("#hiddenMobile").html(data.infos[0].mobile);
            $("#returnInfo").remove();
            $("#qq").val(data.infos[0].qq);
            $("#email").val(data.infos[0].email);
            $("#contactId").val(data.infos[0].contactIdStr);
            isReadOnly();
            //查询累计欠款
            if ($("#customerId").val()) {
                var ajaxUrl2 = "sale.do?method=getTotalDebts";
                var ajaxData2 = {customerId: $("#customerId").val()};
                bcgogoAjaxQuery.setUrlData(ajaxUrl2, ajaxData2);
                bcgogoAjaxQuery.ajaxQuery(function (data) {
                    var totalDebt = data.totalDebt;
                    var totalReturnDebt = data.totalReturnDebt;

                    if(totalDebt){
                        $("#receivable").html(totalDebt);
                    }else{
                        $("#receivable").html("0");
                    }
                    if(totalReturnDebt){
                        $("#payable").html(totalReturnDebt);
                    }else{
                        $("#payable").html("0");
                    }

                    initDuiZhanInfo();
                    $("#customerConsume").html(data.totalAmount);
                    searchLicenceNoByCustomerId(jQuery("#licenceNo")[0], jQuery("#customerId").val(), true);
                });

                $.ajax({
                        type: "POST",
                        url: "member.do?method=getMemberInfo",
                        async: true,
                        data: {customerId: jQuery("#customerId").val()},
                        cache: false,
                        dataType: "json",
                        error: function (XMLHttpRequest, error, errorThrown) {
                        },
                        success: function (data) {
                            var type = data.type;
                            var memberNo = data.memberNo;
                            var statusStr = data.statusStr;
                            var remainAmount = data.balance;
                            if (remainAmount == null) {
                                remainAmount = 0;
                            }
                            $("#memberServiceDeadLineStr").html(G.normalize(data.serviceDeadLineStr));
                            $("#memberJoinDateStr").html(G.normalize(data.joinDateStr));
                            $("#memberRemainAmount").html(remainAmount);
                            $("#memberType").html(type);
                            $("#memberNumber").html(memberNo);
                            $("#memberStatus").html(statusStr);
                            $("#memberServiceInfo").empty();
                            if(G.isNotEmpty(data.memberServiceDTOs)){
                                $("#memberServiceCount").html(data.memberServiceDTOs.length);
                                $.each(data.memberServiceDTOs,function(index,memberServiceDTO){
                                    $("#memberServiceInfo").append('<div style="overflow: hidden;"><div style="text-overflow :ellipsis;overflow: hidden;white-space: nowrap;" title="'+memberServiceDTO.serviceName+'" class="div left">'+memberServiceDTO.serviceName+'</div><div class="div right">'+memberServiceDTO.timesStr+'</div></div>');
                                });
                            }else{
                                $("#memberServiceCount").html(0);
                            }
                            if(!G.Lang.isEmpty(data.memberNo)){
                                $("#customerMemberInfoImg").css("display","inline-block");
                            }
                        }
                    }
                );
            }
        }
    });
    $("#div_brand").css({'display': 'none'});
    $("#customerName").blur();
    new ajaxForInvoiceUtil().ajaxToGetWashTimes(customerId);
}
function ajaxStyleCustomer(domObject, jsonStr) {
    domTitle = domObject.name;
    selectmoreCustomer = jsonStr.length;
    if (jsonStr.length <= 0)
        $("#div_brand").css({'display':'none'});
    else {
        var offsetWidth = $(domObject).width();
        var offsetHeight = $(domObject).height();
        suggestionPosition(domObject, 0 ,offsetHeight+3);
        $("#Scroller-Container_id").html("");
        for (var i = 0; i < jsonStr.length; i++) {
            var a = $("<a id='selectItem" + i + "'></a>");
            a.html(jsonStr[i].name);
            a.css({"text-overflow":"ellipsis","overflow":"hidden","white-space":"nowrap"});
            var titleVal = jsonStr[i].name;
            if(jsonStr[i].contacts && jsonStr[i].contacts[0] &&jsonStr[i].contacts[0].mobile){
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
                selectValueCustomer = jsonStr[$("#Scroller-Container_id > a").index($(this)[0])].name;// $(this).html();
                selectItemNumCustomer = parseInt(this.id.substring(10));
            });
            a.mouseleave(function(){
                $("#Scroller-Container_id> a").removeAttr("class");
                selectValueCustomer = "";
                selectItemNumCustomer = -1;
            });
            a.click(function () {
                if (getOrderType() == "INSURANCE_ORDER") {
                    $(domObject).val(jsonStr[$("#Scroller-Container_id > a").index($(this)[0])].name);
                    var ajaxUrl = "sale.do?method=searchCustomerById";
                    var ajaxData = {shopId: $("#shopId").val(), customerId: jsonStr[$("#Scroller-Container_id > a").index($(this)[0])].idStr};
                    bcgogoAjaxQuery.setUrlData(ajaxUrl, ajaxData);
                    bcgogoAjaxQuery.ajaxQuery(function (data) {
                        if (data.infos.length > 0) {
                            $("#customerId").val(data.infos[0].customerIdStr);
                            $("#customer").val(data.infos[0].customer);
                            $("#customer").attr('title', data.infos[0].customer);
                            $("#mobile").val(data.infos[0].mobile);
                            $("#div_brand").css({'display': 'none'});
                            searchLicenceNoByCustomerId(jQuery("#licenceNo")[0], jQuery("#customerId").val(), true)
                        }
                    })
                } else {
                    $(domObject).val(jsonStr[$("#Scroller-Container_id > a").index($(this)[0])].name);
                    ajaxGetCustomerInfo(jsonStr[$("#Scroller-Container_id > a").index($(this)[0])].idStr);
                }
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
            $("#div_brandCustomer").css({'display':'none'});
            $("#iframe_PopupBox").attr("src", encodeURI("product.do?method=createsearchvehicleinfo&domtitle="
                + domTitle + "&brandvalue=" + $("#brand").val()));
            $("#iframe_PopupBox").css({'display':'block'});
            Mask.Login();
        });
        $("#Scroller-Container_idheader").append(a);
    }
}

//TODO 根据关键字查询客户名
function searchSuggestionCustomer(domObject, elementCustomer, eventStr,keycode) { //车辆信息查询
    var searchWord = (eventStr == "click" ? "" : domObject.value);
    var ajaxUrl = "txn.do?method=getCustomerName";
    var ajaxData = {
        name:searchWord
    };
    bcgogoAjaxQuery.setUrlData(ajaxUrl, ajaxData);
    bcgogoAjaxQuery.ajaxQuery(function(json) {
        if(!G.isEmpty(json[0])){
            G.completer({
                    'domObject':domObject,
                    'keycode':keycode,
                    'title':json[0].name}
            );
        }
        ajaxStyleCustomer(domObject, json);
    });
}

//TODO 将联系人、联系电话、座机电话 设置为只读
function isReadOnly() {
    if (getOrderType() == "REPAIR") {
        var attrs = ["contact", "mobile", "landLine"];
        for (var i = 0, len = attrs.length; i < len; i++) {
            if (document.getElementById(attrs[i])) {
                if (document.getElementById(attrs[i]).value) {
                    document.getElementById(attrs[i]).disabled = true;
                }
                else {
                    document.getElementById(attrs[i]).disabled = false;
                }
            }
        }
    }

}
