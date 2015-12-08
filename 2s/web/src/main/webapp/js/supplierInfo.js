//TODO 用于处理进销存单据“更多供应商”，将父页面信息带入弹出框，确定后为父页面域赋值
$().ready(function() {
    function checkMobile(mobile) {
        var patrn = /^[1]{1}[0-9]{10}$/;
        if (!patrn.exec(mobile)) return false
        return true
    }

//    $("table tr td:even").css("width", "80px");

    $("#bank").val($(window.parent.document).find("#bank").val());
    $("#businessScope").val($(window.parent.document).find("#businessScope").val());
    $("#accountName").val($(window.parent.document).find("#accountName").val());
    $("#account").val($(window.parent.document).find("#account").val());

    $("#supplier").val($(window.parent.document).find("#supplier").val());
    $("#category").val($(window.parent.document).find("#category").val());
    $("#abbr").val($(window.parent.document).find("#abbr").val());
    $("#address").val($(window.parent.document).find("#address").val());
    $("#contact").val($(window.parent.document).find("#contact").val());
    $("#settlementType").val($(window.parent.document).find("#settlementType").val());
    if (checkMobile($(window.parent.document).find("#mobile").val())) {
        $("#mobile").val($(window.parent.document).find("#mobile").val());
    }
    if ($(window.parent.document).find("#landline").val() != "") {
        $("#landline").val($(window.parent.document).find("#landline").val());
    } else if (!checkMobile($(window.parent.document).find("#mobile").val())) {
        $("#landline").val($(window.parent.document).find("#mobile").val());
    }
    if ($(window.parent.document).find("#landlineSecond").val() != "") {
        $("#landlineSecond").val($(window.parent.document).find("#landlineSecond").val());
    } else if (!checkMobile($(window.parent.document).find("#mobile").val())) {
        $("#landlineSecond").val($(window.parent.document).find("#mobile").val());
    }
    if ($(window.parent.document).find("#landlineThird").val() != "") {
        $("#landlineThird").val($(window.parent.document).find("#landlineThird").val());
    } else if (!checkMobile($(window.parent.document).find("#mobile").val())) {
        $("#landlineThird").val($(window.parent.document).find("#mobile").val());
    }

    $("#hiddenMobile").html($(window.parent.document).find("#mobile").val());
    $("#fax").val($(window.parent.document).find("#fax").val());
    $("#qq").val($(window.parent.document).find("#qq").val());
    $("#qq").val($(window.parent.document).find("#qq").val());
    $("#email").val($(window.parent.document).find("#email").val());
    $("#invoiceCategory").val($(window.parent.document).find("#invoiceCategory").val());

    $("#confirmBtn").click(function() {
        $("form:first").submit();
        $(window.parent.document).find("#mask").hide();
        $(window.parent.document).find("#iframe_PopupBox").hide();
        $(window.parent.document).find("#bank").val($("#bank").val());
        $(window.parent.document).find("#businessScope").val($("#businessScope").val());
        $(window.parent.document).find("#accountName").val($("#accountName").val());
        $(window.parent.document).find("#account").val($("#account").val());

        $(window.parent.document).find("#supplier").val($("#supplier").val());
        $(window.parent.document).find("#category").val($("#category").val());
        $(window.parent.document).find("#abbr").val($("#abbr").val());
        $(window.parent.document).find("#address").val($("#address").val());
        $(window.parent.document).find("#contact").val($("#contact").val());
        $(window.parent.document).find("#settlementType").val($("#settlementType").val());
        $(window.parent.document).find("#mobile").val($("#mobile").val());
        $(window.parent.document).find("#landline").val($("#landline").val());
        $(window.parent.document).find("#landlineSecond").val($("#landlineSecond").val());
        $(window.parent.document).find("#landlineThird").val($("#landlineThird").val());
        $(window.parent.document).find("#fax").val($("#fax").val());
        $(window.parent.document).find("#qq").val($("#qq").val());
        $(window.parent.document).find("#email").val($("#email").val());
        $(window.parent.document).find("#invoiceCategory").val($("#invoiceCategory").val());
    });

    $("#div_close,#cancelBtn").click(function() {
        $(window.parent.document).find("#mask").hide();
        $(window.parent.document).find("#iframe_PopupBox").hide();

        try {
            $(window.parent.document.body).find("input[type='button']").eq(0).focus().blur();
        } catch(e) {
            ;
        }
    });


    $("input[id='supplier'], input[id='contact']").bind("keyup", function(){
        $(this).val(APP_BCGOGO.StringFilter.stringSpaceFilter($(this).val()));
    });

    provinceBind();
    $("#select_province").bind("change",function(){
        cityBind(this);
    });
    $("#select_city").bind("change",function(){
        townshipBind(this);
    });
    $("#input_address").focus(function () {
            if ($(this).val() == $(this).attr("initValue")) {
                $(this).val('');
                $(this).css("color", "#000000");
            }
        })
        .blur(function () {
            if ($(this).val() == '') {
                $(this).css("color", "#7e7e7e");
                $(this).val('详细地址');
            }
        });
    $("#select_province,#select_city,#select_township,#settlementType,#invoiceCategory").click(function(){
        $(this).css("color","#000000");
    });
    $("#otherInput").keyup(function(){
        if($.trim($(this).val()) != '') {
            $("#otherCheckbox").attr("checked",true);
            $("#otherCheckbox").val($.trim($(this).val()));
        }
    });
    $("#modifyClientDiv").dialog({
        autoOpen:false,
        resizable: false,
        title:"修改供应商属性",
        height:549,
        width:820,
        modal: true,
        closeOnEscape: false,
        close:function() {
            $("#modifyClientDiv").val("");
            $("#identity").attr("checked",false);
        },
        showButtonPanel:true
    });

});

//第一级菜单 select_province
function provinceBind() {
    var r = APP_BCGOGO.Net.syncGet({url:"shop.do?method=selectarea",
        data:{parentNo:1},dataType:"json"});
    if (!r||r.length == 0) return;
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
    while ($("#select_city")[0].options.length > 1) {
        $("#select_city")[0].remove(1);
    }
    while ($("#select_township")[0].options.length > 1) {
        $("#select_township")[0].remove(1);
    }
    if (select.selectedIndex == 0) {
    } else {
        var r = APP_BCGOGO.Net.syncGet({"url": "shop.do?method=selectarea&parentNo=" + select.value, "dataType": "json"});
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
}

//第三级菜单 select_township
function townshipBind(select) {
    var r = APP_BCGOGO.Net.syncGet({"url":"shop.do?method=selectarea&parentNo=" + select.value, "dataType":"json"});
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

function getSelectAddress(){
    var selectAddress = '';
    if($("select[name='province']").eq(0).val()){
        selectAddress += $("select[name='province']").find("option:selected").text();
    }
    if($("select[name='city']").eq(0).val()){
        selectAddress += $("select[name='city']").find("option:selected").text();
    }
    if($("select[name='region']").eq(0).val()){
        selectAddress += $("select[name='region']").find("option:selected").text();
    }
    return selectAddress;
}

function initTr(data) {
    $("#customerDatas tr").not(":first").remove();
    $("#totalRows").val(data[0].numFound);
    if(data && data[0] && data[0].customerSuppliers != undefined) {
        for(var i = 0; i< data[0].customerSuppliers.length; i++) {
            var customerSupplier = data[0].customerSuppliers[i];
            var contact = customerSupplier.contact==null?'暂无':customerSupplier.contact;
            var mobile = customerSupplier.mobile==null?'暂无':customerSupplier.mobile;
            var tr = '<tr><td><input type="radio" value="' + data[0].customerSuppliers[i].idStr + '"  name="customer"/></td><td>';
            tr+= data[0].customerSuppliers[i].name;
            tr+='<a class="connecter J_connector" customerId="' + customerSupplier.idStr + '"></a>' +
                '<div class="prompt J_prompt" customerId="' + customerSupplier.idStr + '" style="margin:0 0 0 30px; display:none;">' +
                '<div class="promptTop"></div>' +
                '<div class="promptBody">' +
                '<div class="lineList">联系人&nbsp;'+ contact +'&nbsp;'+ mobile + '</div>' +
                '</div>' +
                '<div class="promptBottom"></div>' +
                '</div></td>';
            tr+='<td>'+data[0].customerSuppliers[i]['areaInfo']+'</td><td>';
            if(data[0].customerSuppliers[i].address != null) {
                tr+= data[0].customerSuppliers[i].address;
            }
            tr+='</td>';
            $("#customerDatas").append($(tr));
        }

    }


}

function setInputValue() {
    var areaInfo = '';
    if(!G.isEmpty($("#select_province").val())) {
        areaInfo += $("#select_province option:selected").text();
    }

    if(!G.isEmpty($("#select_city").val())) {
        areaInfo += $("#select_city option:selected").text();
    }

    if(!G.isEmpty($("#select_township").val())) {
        areaInfo += $("#select_township option:selected").text();
    }

    if($("#areaInfo").length > 0) {
        $("#areaInfo").val(areaInfo);
    }

    if(!G.isEmpty($("#input_address").val()) && !G.isEmpty($("#select_province").val())) {
        $("#addressDiv").css("display","");
    }

    if(G.isEmpty($("#select_province").val())) {
        if(!G.isEmpty($("#input_address").val())) {
            $("#areaInfo").val($("#input_address").val());
        }
    }

    $(".onlineShop").each(function(){
        if(G.isEmpty($(this).val())) {
            $(this).val('--');
        }
    });
}

function clearDefaultValue() {
    $(".onlineShop").each(function(){
        if($(this).val() == '--') {
            $(this).val('');
        }
    });
}

