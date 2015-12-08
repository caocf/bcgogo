function map() {
    var struct = function(key, value) {
            this.key = key;
            this.value = value;
        }

    var put = function(key, value) {
            for(var i = 0; i < this.arr.length; i++) {
                if(this.arr[i].key === key) {
                    this.arr[i].value = value;
                    return;
                }
            }
            this.arr[this.arr.length] = new struct(key, value);
        }

    var get = function(key) {
            for(var i = 0; i < this.arr.length; i++) {
                if(this.arr[i].key === key) {
                    return this.arr[i].value;
                }
            }
            return null;
        }

    var remove = function(key) {
            var v;
            for(var i = 0; i < this.arr.length; i++) {
                v = this.arr.pop();
                if(v.key === key) {
                    break;
                }
                this.arr.unshift(v);
            }
        }

    var size = function() {
            return this.arr.length;
        }

    var isEmpty = function() {
            return this.arr.length <= 0;
        }

    var clearMap = function() {
            this.arr = [];
        }
    this.arr = new Array();
    this.get = get;
    this.put = put;
    this.remove = remove;
    this.size = size;
    this.isEmpty = isEmpty;
    this.clearMap = clearMap;
}

var map = new map();

$().ready(function() {
    $("#div_close,#closeBtn").live("click", function() {
        closeWin();
    });

    $(".checkB").live("click", function() {
        var id = $(this).attr("idStr");
        var name = $(this).closest("tr").find("td").eq(2).html();
        var mobile = $("#mobile"+"\\."+id).html();
        var obj = {name:name,mobile:mobile,userId:id};
        if($(this)[0].checked == true)
        {
            if($("#personNum").val()*1 + map.size() >= 100)
            {
                $(this)[0].checked = false;
                alert("相关人不能大于100个，请选择全部人\n或者全部客户,或者全部供应商,或者全部会员！");
                return;
            }

            map.put(id, obj);

            var checkBoxs = document.getElementsByName("checks");

            if($("#checkAll")[0].checked == false) {
                var num = 0;
                for(var i = 0; i < checkBoxs.length; i++) {
                    if($(checkBoxs[i])[0].checked == true) {
                        num++;
                    }
                }

                if(0 != num && num == checkBoxs.length) {
                    $("#checkAll")[0].checked = true;
                }
            }
        } else {
            if($("#checkAll")[0].checked == true) {
                $("#checkAll")[0].checked = false;
            }
            map.remove(id);
        }
    });

    $("#checkAll").live("click", function() {

        var checkBoxs = document.getElementsByName("checks");
        if($(this)[0].checked == true) {
            var num = 0;

            for(var i = 0; i < checkBoxs.length; i++) {
                if(null != map.get($(checkBoxs[i]).attr("idStr"))) {
                    num++;
                }
            }

            if($("#tb_tui tr:not(:first)").size() + $("#personNum").val() * 1 + map.size() - num > 100) {
                $(this)[0].checked = false;
                alert("相关人不能大于100个，请选择全部人\n或者全部客户,或者全部供应商,或者全部会员！");
                return;
            }

            for(var i = 0; i < checkBoxs.length; i++) {
                if(null != map.get($(checkBoxs[i]).attr("idStr"))) {
                    continue;
                }

                $(checkBoxs[i])[0].checked = true;
                var id = $(checkBoxs[i]).attr("idStr");
                var name=$(checkBoxs[i]).closest("tr").find("td").eq(2).html();
                var mobile = $("#mobile"+"\\."+id).html();
                var obj = {
                    name: name,
                    mobile: mobile,
                    userId: id
                };
                map.put(id, obj);
            }
        } else {
            for(var i = 0; i < checkBoxs.length; i++) {
                $(checkBoxs[i])[0].checked = false;

                map.remove($(checkBoxs[i]).attr("idStr"), obj);
            }
        }
    });

    $("#confirm").live("click", function() {
        //        var persons = "";
        //        var mobiles = "";
        var msg = "";
        var jsonArrStr = "[";
        var foo = APP_BCGOGO.Validator;
        if(0 != map.size()) {
            for(var i = 0; i < map.size(); i++) {
                var flag = true;
                if(null != map.arr[i].value.mobile && "" != map.arr[i].value.mobile) {
                    if(!foo.stringIsMobilePhoneNumber(map.arr[i].value.mobile)) {
                        flag = false;
                        msg += map.arr[i].value.name + "的手机号格式不对！\n";
                        map.arr[i].value.mobile = "";
                    }
                }
                if("[" == jsonArrStr) {
                    jsonArrStr += JSON.stringify(map.arr[i].value);
                } else {
                    jsonArrStr += "," + JSON.stringify(map.arr[i].value);
                }
            }
            GLOBAL.info(msg);
            jsonArrStr = jsonArrStr + "]";

            $("#addContent", parent.document).val(jsonArrStr);
            $("#addContent", parent.document).click();
            closeWin();

        } else {
            closeWin();
        }
    });

    $("#allUser,#allCustomer,#allSupplier,#allMember,#allHasMobile").live("click", function() {
        var name = "";
        var mobile = "";
        var id = this.id
        if(id == "allUser") {
            name = "所有联系人";
        } else if(id == "allCustomer") {
            name = "所有客户";
        } else if(id == "allSupplier") {
            name = "所有供应商";
        } else if(id == "allMember") {
            name = "会员"
        } else if(id == "allHasMobile") {
          name = "所有手机联系人";
        }
        mobile = name;
        var obj = [{
            name: name,
            mobile: mobile,
            userId: id
        }];
        var jsonArrStr = JSON.stringify(obj);
        $("#addContent", parent.document).val(jsonArrStr);
        $("#addContent", parent.document).click();
        closeWin();
    });

    $("#seatchBtn").live("click", function() {
        var keyWords = $.trim($('#customer_supplierInfoText').val());
        if("客户/供应商/联系人/车牌号/会员号/手机号" == keyWords || keyWords=="客户/供应商/联系人/手机号") {
            keyWords = "";
        }
        var data = {
            startPageNo: 1,
            maxRows: 10,
            keyWords: keyWords
        };
        var url = "remind.do?method=getCustomerAndSupplier";
        APP_BCGOGO.Net.asyncPost({
            dateType: "json",
            url: url,
            data: data,
            success: function(jsonStr) {
                jsonStr = JSON.parse(jsonStr);
                initTr(jsonStr);
                initPages(jsonStr, "dynamical1", "remind.do?method=getCustomerAndSupplier", '', "initTr", '', '', data, '');
            }
        });
    });
});

function initTr(jsonStr) {
    $("#tb_tui tr:not(:first)").remove();
    var num = 0;
    if(jsonStr.length > 1) {
        for(var i = 0; i < jsonStr.length - 1; i++) {

            var licenseNos = jsonStr[i].licenseNos == null ? "" : jsonStr[i].licenseNos;
            var licenseNo = (licenseNos == null || $.trim(licenseNos.toString()) == "") ? "" : licenseNos[0];
            var id = jsonStr[i].idStr == null ? "" : jsonStr[i].idStr;
            var name = jsonStr[i].name == null ? "" : jsonStr[i].name;
            var contact = jsonStr[i].contact == null ? "" : jsonStr[i].contact;
            var mobile = jsonStr[i].mobile == null ? "" : jsonStr[i].mobile;
            var memberNo = jsonStr[i].memberNo == null ? "" : jsonStr[i].memberNo;
            var tr = '<tr class="table-row-original">';
            if(null == map.get(id)) {
                tr += '<td style="border-left:none;"><input type="checkbox" idStr="' + id + '" name="checks" class="checkB"/></td>';
            } else {
                num++;
                tr += '<td style="border-left:none;"><input type="checkbox" checked="checked" idStr="' + id + '" name="checks" class="checkB"/></td>';
            }
            tr += '<td >' + (i + 1) + '</td>';
            tr += '<td title="' + name + '">' + name + '</td>';
            tr += '<td title="' + contact + '">' + contact + '</td>';
            if(repairPermission && washPermission) {
                tr += '<td>' + licenseNo + '</td>';
            }
            if(memberPermission) {
                tr += '<td>' + memberNo + '</td>';
            }
            tr += '<td id="mobile.'+id+'">' + mobile + '</td>';

            tr += '</tr>';
            $("#tb_tui").append($(tr));
        }

        if(0 != num && $("#tb_tui tr:not(:first)").size() == num) {
            $("#checkAll")[0].checked = true;
        } else {
            $("#checkAll")[0].checked = false;
        }
    }
    tableUtil.tableStyle('#tb_tui', '.tab_title');

    //    getChecked();
    //    isAllChecked();
}

function getChecked() {
    $(".checkB").each(function(index) {
        var customerId = $(this).parent().next().next().children("input").val();
        if(checkedIds.indexOf(customerId) != -1) {
            $(this).attr('checked', true);
        }
    });
}

function isAllChecked() {
    var i = $(".checkB").size();
    for(i = 0; i < $(".checkB").size(); i++) {
        if($(".checkB").eq(i).attr('checked') == false) {
            $("#checkAll").attr('checked', false);
            break;
        }
        $("#checkAll").attr('checked', true);
    }
}


function closeWin() {
    jQuery(window.parent.document).find("#mask").hide();
    jQuery(window.parent.document).find("#iframe_PopupBox").hide();
}