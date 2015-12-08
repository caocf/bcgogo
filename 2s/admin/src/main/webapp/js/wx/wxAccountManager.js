;
$(function () {

    $(".j_shop_name").live("keyup", function (e) {
        if (!checkKeyUp(this, e)) {
            return;
        }
        var shopName = $(this)[0];
        if (shopName.value == '' || shopName.value == null) {
            $("#div_shopName").css({'display': 'none'});
        }
        else {
            shopName.value = shopName.value.replace(/[\ |\\]/g, "");
            $.ajax({
                    type: "POST",
                    url: "print.do?method=getShopNameByName",
                    async: true,
                    data: {
                        name: shopName.value,
                        now: new Date()
                    },
                    cache: false,
                    dataType: "json",
                    error: function (XMLHttpRequest, error, errorThrown) {
                        $("#div_shopName").css({'display': 'none'});
                    },
                    success: function (jsonStr) {
                        ajaxStyleShopName(shopName, jsonStr);
                    }
                }
            );
        }
    });

    $("#addShopBtn").click(function () {
        if ($("#addWXShopAccountTable .j_shop_id").length == 0) {
            $("#addWXShopAccountTable tr:gt(0)").remove();
        }
        var trStr = '<tr>';
        trStr += '<input class="j_shop_id" type="hidden">'
        trStr += '<td><input class="j_shop_name" placeholder="店铺名"/></td>'
        trStr += '<td><a class="j_delete_shop_account">删除</a></td>'
        trStr += '</tr>';
        $("#addWXShopAccountTable").append(trStr);
    });

    $("#addWXAccount").click(function () {
        $("#addWXAccountDiv").dialog({
            resizable: true,
            title: "添加公共号",
//            height:150,
            width: 600,
            modal: true,
            closeOnEscape: false,
            open: function () {
                $("#addWXAccountTable input").val("");
//                $(".add-shop-info").hide();
                $("#addWXShopAccountTable tr:gt(0)").remove();
            },
            buttons: {
                "确定": function () {
                    _doSaveWXAccount();
                },
                "取消": function () {
                    $(this).dialog("close");
                }
            }
        });
    });


    $(".j_account_edit").live("click", function () {
        var accountId = $(this).attr("accountId");
        if (G.isEmpty(accountId)) {
            alert("accountId is empty");
            return;
        }
        APP_BCGOGO.Net.asyncAjax({
            url: "weChat.do?method=getWXAccountDetail",
            type: "POST",
            cache: false,
            data: {accountId: accountId},
            dataType: "json",
            success: function (account) {
                _editWXAccount(account);
            },
            error: function () {
                nsDialog.jAlert("网络异常。");
            }
        });

    });


    $(".j_wx_menu_opr").live("click", function () {
        var mask = APP_BCGOGO.Module.waitMask;
        mask.login();
        var publicNo = $(this).attr("publicNo");
        if (G.isEmpty(publicNo)) {
            alert("publicNO can't be null");
            return;
        }
        APP_BCGOGO.Net.asyncAjax({
            url: "weChat.do?method=reCreateMenu",
            type: "POST",
            cache: false,
            data: {publicNo: publicNo},
            dataType: "json",
            success: function (result) {
                if (!result.success) {
                    alert(result.msg);
                    return;
                }
                alert("重建成功。");
                mask.open();
            },
            error: function () {
                nsDialog.jAlert("网络异常。");
            }
        });

    });


    $(".j_delete_shop_account").live("click", function () {
        $(this).closest("tr").remove();
    });


    $("#searchBtn").click(function () {
        var url = "weChat.do?method=getWXAccount";
        var data = {
            currentPage: 1,
            pageSize: 10
        };
        APP_BCGOGO.Net.asyncAjax({
            url: url,
            type: "POST",
            cache: false,
            data: data,
            dataType: "json",
            success: function (result) {
                drawWXAccount(result);
                initPage(result, "_drawWXAccount", url, null, "drawWXAccount", '', '', data, null);
            },
            error: function () {
                nsDialog.jAlert("网络异常。");
            }
        });
    });

    $("#searchBtn").click();
});

function _doSaveWXAccount() {
    var mask = APP_BCGOGO.Module.waitMask;
    mask.login();
    var data = {
        id: $.trim($("#accountId").val()),
        name: $.trim($("#public_name").val()),
        publicNo: $.trim($("#public_no").val()),
        appId: $.trim($("#app_id").val()),
        secret: $.trim($("#secret").val()),
        token: $.trim($("#token").val()),
        encodingKey: $.trim($("#encodingKey").val()),
        remark: $.trim($("#remark").val())
    }
    var shopAccountDTOs = {};
    $(".j_shop_id").each(function (i) {
        if (!G.isEmpty($(this).val())) {
            data['shopAccountDTOs[' + i + '].shopId'] = $(this).val();
        }
    });
    data['shopAccountDTOs'] = shopAccountDTOs;
    APP_BCGOGO.Net.asyncAjax({
        url: "weChat.do?method=saveOrUpdateWXAccount",
        type: "POST",
        cache: false,
        data: data,
        dataType: "json",
        success: function (result) {
            mask.open();
            if (!result.success) {
                alert(result.msg);
                return;
            }
            window.location.reload();
        },
        error: function () {
            nsDialog.jAlert("网络异常。");
        }
    });
}

function _editWXAccount(account) {
    if (G.isEmpty(account)) {
        alert("账户不存在");
        return;
    }
    $("#accountId").val(account.idStr);
    $("#public_name").val(account.name);
    $("#public_no").val(account.publicNo);
    $("#app_id").val(account.appId);
    $("#secret").val(account.secret);
    $("#token").val(account.token);
    $("#encodingKey").val(account.encodingKey);
    $("#remark").val(account.remark);
    var shopId = account.shopId;
    //shop info
    $("#addWXShopAccountTable tr:gt(0)").remove();
    $("#addShopBtn").show();
    var shopAccountDTOs = account.shopAccountDTOs;
    if (G.isEmpty(shopAccountDTOs)) {
        var txt = "暂无店铺使用";
        if (shopId == 1) {
            txt = "缺省";
            $("#addShopBtn").hide();
        }
        var trStr = '<tr>';
        trStr += '<td colspan="3">' + txt + '</td>'
        trStr += '</tr>';
        $("#addWXShopAccountTable").append(trStr);
    } else {
        for (var i = 0; i < shopAccountDTOs.length; i++) {
            var shopAccountDTO = shopAccountDTOs[i];
            var trStr = '<tr>';
            trStr += '<input class="j_shop_id" type="hidden" value="' + shopAccountDTO.shopId + '"/>'
            trStr += '<td>' + shopAccountDTO.shopName + '</td>'
            trStr += '<td><a class="j_delete_shop_account">删除</a></td>'
            trStr += '</tr>';
            $("#addWXShopAccountTable").append(trStr);
        }
    }
    $("#addWXAccountDiv").dialog({
        resizable: true,
        title: "编辑公共号",
        width: 600,
        modal: true,
        closeOnEscape: false,
        open: function () {
        },
        buttons: {
            "确定": function () {
                _doSaveWXAccount();
            },
            "取消": function () {
                $(this).dialog("close");
            }
        }
    });

}


function drawWXAccount(json) {
    var results = json.results;
    if (G.isEmpty(results)) {
        return;
    }
    for (var i = 0; i < results.length; i++) {
        var account = results[i];
        var idStr = account.idStr;
        var name = account.name;
        var publicNo = account.publicNo;
        var appId = account.appId;
        var remark = account.remark;
        var shopNames = account.shopNames;
        var shopId = account.shopId;
        if (G.isEmpty(shopNames)) {
            shopNames = shopId == 1 ? "缺省" : "暂无店铺使用";
        }
        var shopNamesShort = shopNames.length > 20 ? (shopNames.substr(20) + '...') : shopNames;
        var trStr = '<tr>';
        trStr += '<td>' + (i + 1) + '</td>';
        trStr += '<td>' + name + '</td>';
        trStr += '<td>' + publicNo + '</td>';
        trStr += '<td title="' + shopNames + '">' + shopNamesShort + '</td>';
        trStr += '<td>' + remark + '</td>';
        trStr += '<td>';
        if (shopId != 1) {
            trStr += '<a class="shop-account-opr-btn j_account_edit" accountId="' + idStr + '">编辑</a>';
        }
        trStr += '<a class="shop-account-opr-btn j_wx_menu_opr" publicNo="' + publicNo + '">重建菜单</a>';
        trStr += '</td>';
        trStr += '</tr>';
        $("#table_account").append(trStr);


    }
}

var lastvalue;
function checkKeyUp(domObj, domEvent) {
    var e = domEvent || event;
    var eventKeyCode = e.which || e.keyCode;
    if (eventKeyCode == 38 || eventKeyCode == 40) {
        return false;
    } else {
        var domvalue = domObj.value;
        if (domvalue != lastvalue) {
            lastvalue = domvalue;
            return true;
        } else {
            return false;
        }
    }
}

var selectValue,
    selectItemNum,
    isout;
function ajaxStyleShopName(domObject, jsonStr) {
    var offset = $(domObject).offset();
    var offsetHeight = $(domObject).height();
    var offsetWidth = $(domObject).width();
    var domTitle = domObject.name;
    var x = getX(domObject);
    var y = getY(domObject);
    var selectmore = jsonStr.length;
    if (selectmore <= 0) {
        $("#div_shopName").css({'display': 'none'});
    } else {
        $("#div_shopName").css({
            'display': 'block', 'position': 'absolute',
            'left': x + 'px',
            'top': y + offsetHeight + 8 + 'px'
        });
        $("#Scroller-Container_shopName").html("");

        for (var i = 0; i < (jsonStr.length > 10 ? 10 : jsonStr.length); i++) {
            var id = jsonStr[i].id;
            var a = $("<a id=" + id + "></a>");
            a.html(jsonStr[i].name + "   " + jsonStr[i].mobile + "<br>");
            $(a).bind("mouseover", function () {
                isout = false;
                $("#Scroller-Container_shopName > a").removeAttr("class");
                $(this).attr("class", "hover");
                selectValue = jsonStr[$("#Scroller-Container_shopName > a").index($(this)[0])].name;// $(this).html();
                selectItemNum = parseInt(this.id.substring(10));
            });

            $(a).bind("mouseout", function (event) {
                isout = true;
                selectValue = "";
            });

            $(a).click(function () {
                var shopId = this.id;
                $(domObject).val(selectValue = jsonStr[$("#Scroller-Container_shopName > a").index($(this)[0])].name); //取的第一字符串
//                $("#uploadShopName").text($(domObject).val());
//                $("#shopId").val(sty);

                $(domObject).closest("tr").find(".j_shop_id").val(shopId);
                selectItemNum = -1;
                $("#div_shopName").css({'display': 'none'});
            });

            $("#Scroller-Container_shopName").append(a);
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