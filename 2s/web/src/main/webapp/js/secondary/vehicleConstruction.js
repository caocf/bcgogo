var vc = {};
vc.setTotal = function () {
    var serviceTotal = $('#serviceTotal').val();
    var salesTotal = $('#salesTotal').val();
    var otherIncomeTotal = $('#otherIncomeTotal').val();
    var total = GLOBAL.Number.filterZero(Number(serviceTotal) + Number(salesTotal) + Number(otherIncomeTotal));
    $('#total').val(total);
    $('span[data-node-type=total]').text(total);
}

$(function () {
    var root = $('#constructionOrder');
    vc.setServiceTotal = function () {
        var total = 0;
        var list = $('input[name$=total]', root);
        $.each(list, function () {
            total = total + Number($(this).val());
        });
        total = GLOBAL.Number.filterZero(total);
        $('span[data-node-type=serviceTotal]', root).text(total);
        $('#serviceTotal').val(total);
        vc.setTotal();
        return total;
    }
    vc.setServiceItemTotal = function (tr) {
        var standardUnitPrice = $('input[name$=standardUnitPrice]', tr);
        var actualHours = $('input[name$=actualHours]', tr);
        var serviceDTOs = $('input[name$=total]', tr);
        serviceDTOs.val(standardUnitPrice.val() * actualHours.val());
        vc.setServiceTotal();
    }

    vc.haveServiceItem = function () {
        var result = false;
        $('tr.tableLine', root).each(function () {
            if (vc.validServiceItem(this)) {
                result = true;
                return false;
            }
        });
        return result;
    }

    vc.allValidServiceItem = function () {
        var result = true;
        $('tr.tableLine', root).each(function () {
            if (!vc.validServiceItem(this)) {
                result = false;
                return false;
            }
        });
        return result;
    }

    vc.validServiceItem = function (tr) {
        return $('input[name$=service]', tr).val() != '';
    }

    $('input[name$=standardUnitPrice]', root).bind('blur', function () {
        var tr = $(this).parents('tr');
        vc.setServiceItemTotal(tr);
    });

    $('input[name$=actualHours]', root).bind('blur', function () {
        var tr = $(this).parents('tr');
        vc.setServiceItemTotal(tr);
    });

    $('input[name$=total]', root).bind('blur', function () {
        vc.setServiceTotal();
    });

    $('#constructionOrder a').bind('click', function () {
        var tr = $(this).parents('tr');
        var table = $(this).parents('table');
        if ($(this).text() == '删除') {
            var id = $('input[name$=id]', tr).val();
            Number(id) > 0 && root.append('<input type="hidden" name="deleteServiceDTOs" autocomplete="off" value="' + id + '">');
            if ($('tr', table).length > 2) {
                tr.empty().remove();
            } else {
                $(':input', $('tr', table)).val('');
            }
            $('a:last', $('tr:last', table)).show();
            vc.setServiceTotal();
        } else if ($(this).text() == '添加') {
            if (vc.allValidServiceItem()) {
                $('tr a:last', table).hide();
                var node = tr.clone(true);
                $('tr:last', table).after(node);
                $(':input', node).val('');
                $('a:last', node).show();
                vc.adjustmentIndex($('tr', root));
            } else {
                alert('施工单有内容重复或为空，请修改或删除!');
            }
        }
    });

    $('a:last', $('#constructionOrder tr:last')).show();

    var droplist = APP_BCGOGO.Module.droplist;

    var showServiceDropList = function (e) {
        droplistLite.show({
            event: e,
            isEditable: "false",
            isDeletable: "false",
            hiddenId: "serviceId",
            id: "id",
            name: "name",
            data: "txn.do?method=searchService",
            afterSelected: function (event, index, data, hook) {
                var parent = $(hook).parents('tr');
                $(':input:eq(1)', parent).val(data.label);
                App.Net.syncGet({
                    url: "txn.do?method=getServiceByServiceName",
                    data: {serviceName: data.label},
                    dataType: "json",
                    success: function (json) {
                        if (json.length) {
                            var item = json[0];
                            $(':input:eq(2)', parent).val(item.standardHours);
                            $(':input:eq(3)', parent).val(item.standardUnitPrice);
                            $(':input:eq(4)', parent).val(item.standardHours);
                            $(':input:eq(5)', parent).val(item.priceStr);
                            vc.setServiceItemTotal($(hook).parents('tr'))
                        }
                        var table = $(hook).parents('table');
                        $('tr.tableLine', table).each(function () {
                            if ($('input[name$=service]', this).attr('name') != $('input[name$=service]', parent).attr('name') && $('input[name$=service]', this).val() == $('input[name$=service]', parent).val()) {
                                if (parent.next().length) {
                                    $('a:first', parent).click();
                                } else {
                                    $(':input', parent).val('');
                                }
                            }
                        });
                    }
                });
                vc.adjustmentIndex($('tr', root));
                vc.allValidServiceItem() && $('a:eq(1)', parent).click();
            }
        });
    }

    $('input[name$=service]', root).bind('focus', showServiceDropList).bind('input', showServiceDropList);
    $('input[name$=workers]', root).bind("focus", showWorkersDropList).bind("input", showWorkersDropList);
});

$(function () {
    var root = $('#materialBill');

    vc.setMaterialTotal = function () {
        var totalNode = $('input[name$=total]', root);
        var total = 0
        $.each(totalNode, function () {
            total = total + Number($(this).val());
        });
        total = GLOBAL.Number.filterZero(total)
        $('span[data-node-type=salesTotal]', root).text(total);
        $('#salesTotal').val(total);
        vc.setTotal();
    };
    vc.setMaterialItemTotal = function (tr) {
        var priceNode = $('input[name$=price]', tr);
        var amountNode = $('input[name$=amount]', tr);
        var totalNode = $('input[name$=total]', tr);
        var totalSpan = $('span[data-node-type=materialTotal]', tr);
        var total = GLOBAL.Number.filterZero(priceNode.val()) * Number(amountNode.val());
        totalNode.val(total);
        totalSpan.text(total);
        htmlNumberFilter(amountNode);
        htmlNumberFilter(totalSpan,true);
        vc.setMaterialTotal();
    };

    window.setTotal = function () {
        $('tr', root).each(function () {
            vc.setMaterialItemTotal(this);
        });
    }

    vc.haveMaterialItem = function () {
        var result = false;
        $('tr.tableLine', root).each(function () {
            if (vc.validMaterialItem(this)) {
                result = true;
                return false;
            }
        });
        return result;
    }

    vc.allValidMaterialItem = function () {
        var result = true;
        $('tr.tableLine', root).each(function () {
            if (!vc.validMaterialItem(this)) {
                result = false;
                return false;
            }
        });
        return result;
    }

    vc.validMaterialItem = function (tr) {
        return $('input[name$=commodityCode]', tr).val() != '' || $('input[name$=productName]', tr).val() != '';
    }

    $('input[name$=price]', root).add('input[name$=amount]', root).bind('blur', function () {
        var tr = $(this).parents('tr');
        vc.setMaterialItemTotal(tr);
    });

    $('a', root).bind('click', function () {
        var tr = $(this).parents('tr');
        var table = $(this).parents('table');
        if ($(this).text() == '删除') {
            var id = $('input[name$=id]', tr).val();
            Number(id) > 0 && root.append('<input type="hidden" name="deleteItemDTOs" autocomplete="off" value="' + id + '">');
            if ($('tr', table).length > 2) {
                tr.empty().remove();
            } else {
                if ($('span[data-node-type=unit]', tr).length) {
                    $('input[name$=unit]', tr).show();
                    $('span[data-node-type=unit]', tr).remove();
                }
                $(':input', tr).val('');
                $('span', tr).text('');
            }
            $('a:last', $('tr:last', table)).show();
            vc.adjustmentIndex($('tr', table));
            vc.setMaterialTotal()
        } else if ($(this).text() == '添加') {
            if (vc.allValidMaterialItem()) {
                var node = tr.clone(true);
                $('tr:last', table).after(node);
                if ($('span[data-node-type=unit]', node).length) {
                    $('input[name$=unit]', node).show();
                    $('span[data-node-type=unit]', node).remove();
                }
                $(':input', node).val('');
                $('span', node).text('');
                $('a:last', tr).hide();
                vc.adjustmentIndex($('tr', root));
            } else {
                alert('材料单有重复项目或者空行，请修改或删除!');
            }
        }
    });

    $('a:last', $('#materialBill tr:last')).show();
    $('#productSaler', root).bind("focus", showWorkersDropList).bind("input", showWorkersDropList);

    $('#vehicleHandover').click(function (event) {
        var obj = this;
        droplistLite.show({
            event: event,
            name: "name",
            keyword: "keyWord",
            searchValue: '',
            data: "txn.do?method=searchWorks",
            autoSet: "false",
            beforeSelected: function (data) {
                return removeSelectedMan(data, $(obj).val());
            },
            afterSelected: function (event, index, data, hook) {
                $(hook).val(data.label);
            }
        });
    });
});

$(function () {
    var root = $('#otherExpenses');

    vc.setOtherIncomeTotal = function () {
        var total = 0;
        $('input[name$=price]', root).each(function () {
            total = total + Number($(this).val());
        });
        total = GLOBAL.Number.filterZero(total);
        $('#otherIncomeTotal').val(total);
        $('span[data-node-type=otherIncomeTotal]', root).text(total);
        vc.setTotal();
    }

    vc.haveOtherIncomeItem = function () {
        var result = false;
        $('tr.tableLine', root).each(function () {
            if (vc.validOtherIncomeItem(this)) {
                result = true;
                return false;
            }
        });
        return result;
    }

    vc.allValidOtherIncomeItem = function () {
        var result = true;
        $('tr.tableLine', root).each(function () {
            if (!vc.validOtherIncomeItem(this)) {
                result = false;
                return false;
            }
        });
        return result;
    }

    vc.validOtherIncomeItem = function (tr) {
        return $('input[name$=name]', tr).val() != '' && $('input[name$=price]', tr).val() != '';
    }

    $('input[name$=price]', root).bind('blur', function () {
        vc.setOtherIncomeTotal();
    });

    $('input[name$=otherIncomeRate]', root).bind('blur', function () {
        var salesTotal = $('#salesTotal').val();
        var price = GLOBAL.Number.filterZero($(this).val() * salesTotal / 100);
        var td = $(this).parents('td');
        $('span[data-node-type=price]', td).text(price);
        $('input[name$=price]', td).val(price);
        vc.setOtherIncomeTotal();
    });

    $('input[name$=otherIncomePrice]', root).bind('blur', function () {
        var td = $(this).parents('td');
        $('input[name$=price]', td).val($(this).val());
        vc.setOtherIncomeTotal();
    });

    $('a', root).bind('click', function () {
        var tr = $(this).parents('tr');
        var table = $(this).parents('table');
        if ($(this).text() == '删除') {
            var id = $('input[name$=id]', tr).val();
            Number(id) > 0 && root.append('<input type="hidden" name="deleteOtherIncomeItemDTOs" autocomplete="off" value="' + id + '">');
            if ($('tr', table).length > 2) {
                tr.empty().remove();
            } else {
                $(':input', table).val('');
                var list = $('td:eq(1)', $('tr:eq(1)', table)).children();
                list.eq(0).removeClass('hide');
                list.eq(1).addClass('hide');
            }
            $('a:last', $('tr:last', table)).show();
            vc.setOtherIncomeTotal();
        } else if ($(this).text() == '添加') {
            if (vc.allValidOtherIncomeItem()) {
                var node = tr.clone(true);
                var list = $('td:eq(1)', node).children();
                list.eq(0).removeClass('hide');
                list.eq(1).addClass('hide');
                var otherIncomeCalculateWay = $(':radio', node);
                otherIncomeCalculateWay.attr('name', otherIncomeCalculateWay.attr('name').replace(/\d+(?=\]\.)/, $('tr', table).length - 1));
                $('tr:last', table).after(node);
                $('label[for]', node).each(function () {
                    var uuid = GLOBAL.Util.generateUUID();
                    $(this).attr('for', uuid).prev().attr('id', uuid);
                });
                $(':input', node).val('');
                $('a:last', tr).hide();
                vc.adjustmentIndex($('tr', root));
            } else {
                alert('其他费用名称或金额为空，请修改或删除!');
            }
        }
    });

    $("label:not(for)", root).each(function () {
        var uuid = GLOBAL.Util.generateUUID();
        $(this).attr('for', uuid).prev().attr('id', uuid);
    });

    $("input[name$=otherIncomeRate]", root).add("input[name$=otherIncomePrice]", root).click(function () {
        $(':radio', $(this).parent()).attr('checked', true);
    });

    $("input[name$='otherIncomeCalculateWay']", root).bind("change", function () {
        var root = $(this).parents('td').children().eq(1);
        $(':text', root).val('');
        $('span[data-node-type=price]', root).text('0元');
        if ($(this).val() == 'RATIO') {
            $(':text:eq(0)', root).focus();
        } else if ($(this).val() == 'AMOUNT') {
            $(':text:eq(1)', root).focus();
        }
        vc.setOtherIncomeTotal();
    });

    $('a:last', $('#otherExpenses tr:last')).show();

    var droplist = APP_BCGOGO.Module.droplist;

    $("input[name$='name']", root).bind("click", function (e) {
        askForAssistDroplist(e, this);
    });

    function askForAssistDroplist(event, obj) {
        var keycode = event.which || event.keyCode;
        var otherIncomeKindName = $(obj).val();
        var otherIncomeKindId = $("#" + obj.id.split(".")[0] + "\\.otherIncomeKindId").val();
        var uuid = GLOBAL.Util.generateUUID();
        droplist.setUUID(uuid);
        APP_BCGOGO.Net.asyncGet({
            url: "txn.do?method=getOtherIncomeKind",
            data: {
                "uuid": uuid,
                "keyWord": otherIncomeKindName,
                "now": new Date()
            },
            dataType: "json",
            success: function (result) {
                if (null == result || null == result.data) return;
                if (!G.isEmpty(result.data[0])) {
                    G.completer({
                            'domObject': obj,
                            'keycode': keycode,
                            'title': result.data[0].label}
                    );
                }
                droplist.show({
                    "selector": $(event.currentTarget),
                    "isEditable": false,
                    "isDeletable": false,
                    "originalValue": {label: otherIncomeKindName, idStr: otherIncomeKindId},
                    "data": result,
                    "onSelect": function (event, index, data, hook) {
                        var name = data.kindName;
                        $(hook).val(name);
                        var table = $(hook).parents('table');
                        var parent = $(hook).parents('tr');
                        droplist.hide();
                        var tr = $(hook).parents('tr');
                        var list = $('td:eq(1)', tr).children();
                        if (name == "材料管理费") {
                            list.eq(0).addClass('hide');
                            list.eq(1).removeClass('hide');
                            $(':input[name$=otherIncomePrice]', list.eq(1)).click();
                        } else {
                            list.eq(0).removeClass('hide');
                            list.eq(1).addClass('hide');
                        }
                        $('tr.tableLine', table).each(function () {
                            if ($('input[name$=name]', this).attr('name') != $('input[name$=name]', parent).attr('name') && $('input[name$=name]', this).val() == $('input[name$=name]', parent).val()) {
                                if (parent.next().length) {
                                    $('a:first', parent).click();
                                } else {
                                    if ($('input[name$=name]', parent).val() == '材料管理费') {
                                        var list = $('td:eq(1)', parent).children();
                                        list.eq(0).removeClass('hide');
                                        list.eq(1).addClass('hide');
                                    }
                                    $('input', parent).val('');
                                }
                            }
                        });
                    }
                });
            }
        });
    }
});

$(function () {
    $('#reset').click(function () {
        window.location.reload();
    });


    $('#settlement').click(function () {
        if (G.isEmpty($('#vehicleLicense').val())) {
            alert('请输入车牌号！');
            return false;
        }
        if (G.isEmpty($('#customerName').val())) {
            alert('请输入客户名！');
            return false;
        }
        if (G.isEmpty($('#startDateStr').val())) {
            alert('请输入正确的进厂时间！');
            return false;
        }
        if (G.isEmpty($('#endDateStr').val())) {
            var bool = confirm('出厂时间未填写！出厂时间是否设置为当前时间');
            if (bool) {
                $('#endDateStr').val(dateUtil.formatDate(new Date(), 'yyyy-MM-dd HH:mm'));
            } else {
                $('#endDateStr').focus();
                return false;
            }
        }

        if (!App.Validator.stringIsLicensePlateNumber($('#vehicleLicense').val().replace(/\s|\-/g, ""))) {
            alert("输入的车牌号码不符合规范，请检查！");
            return false;
        }
        if ($('#vehicleMobile').val() != '') {
            if (!/^(13[0-9]|15[012356789]|18[0-9]|14[57])[0-9]{8}$/.test($('#vehicleMobile').val())) {
                alert("输入的手机号码可能不正确，请确认后重新输入！");
                return false;
            }
        }
        if ($('#customerLandline').val() != '') {
            if (!/^(0\d{2,3})*-?\d{7,8}$/.test($('#customerLandline').val())) {
                alert("输入的座机可能不正确，请确认后重新输入！");
                return false;
            }
        }
        if ($('#customerMobile').val() != '') {
            if (!/^(13[0-9]|15[012356789]|18[0-9]|14[57])[0-9]{8}$/.test($('#customerMobile').val())) {
                alert("输入的手机号码可能不正确，请确认后重新输入！");
                return false;
            }
        }

        if (!(vc.haveServiceItem() || vc.haveMaterialItem())) {
            alert("施工单和材料单均未填写！");
            return false;
        }

        var bool = true;
        $('#otherExpenses tr').each(function (i) {
            if ($('input[name$=name]', this).val() != '') {
                if ($('input[name$=price]', this).val() == '') {
                    alert('其他费用列表：第' + Number(1) + '行缺少费用金额 ');
                    bool = false;
                }
            }
        });
        if (!bool) {
            return false;
        }

        var startDate = dateUtil.convertDateStrToDate($('#startDateStr').val(), 'yyyy-MM-dd HH:mm');
        var endDate = dateUtil.convertDateStrToDate($('#endDateStr').val(), 'yyyy-MM-dd HH:mm');
        if (startDate.getTime() > endDate.getTime()) {
            alert('预约出厂时间不能早于进厂时间，请修改!');
            return;
        }

        var isModify = false;
        $('input[data-original-value]').each(function () {
            if ($(this).val() != $(this).attr('data-original-value')) {
                isModify = true;
                return false;
            }
        });
        isModify && nsDialog.jConfirm("本单据为结算附表，客户资料的所有修改只对本单据有效!如果需要修改保存客户信息，请在施工单或客户管理中修改！", "友情提示", function (val) {
            val && bcgogo.checksession({
                "parentWindow": window.parent,
                'iframe_PopupBox': $("#iframe_PopupBox_account")[0],
                'src': 'repairOrderSecondary.do?method=settleAccounts'
            });
        });
        isModify || bcgogo.checksession({
            "parentWindow": window.parent,
            'iframe_PopupBox': $("#iframe_PopupBox_account")[0],
            'src': 'repairOrderSecondary.do?method=settleAccounts'
        });
    });

    vc.adjustmentIndex = function (trs) {
        $.each(trs, function (i) {
            $(':input', this).each(function () {
                $(this).attr('name', $(this).attr('name').replace(/\d+(?=\]\.)/, i - 1));
                $(this).attr('id') != null && $(this).attr('id', $(this).attr('id').replace(/\d+(?=\.)/, i - 1));
            });
            $('span', this).each(function () {
                $(this).attr('id') != null && $(this).attr('id', $(this).attr('id').replace(/\d+(?=\.)/, i - 1));
            });
        });
    }

    window.submitForm = function () {
        var trs = [$('#constructionOrder tr'), $('#materialBill tr'), $('#otherExpenses tr')];
        $.each(trs, function () {
            vc.adjustmentIndex(this);
        });
        APP_BCGOGO.Net.asyncAjax({
            dataType: 'json',
            type: 'POST',
            data: $('#repairOrderSecondary').serialize(),
            url: 'repairOrderSecondary.do?method=submitRepairOrderSecondary',
            success: function (response) {
                if (response.success) {
                    var repairOrderSecondaryDTO = response.data;
                    $('#isPrint').val() == 'true' && window.showModalDialog('repairOrderSecondary.do?method=printRepairOrderSecondary&repairOrderSecondaryId=' + repairOrderSecondaryDTO.idStr);
                    window.location.href = 'repairOrderSecondary.do?method=showRepairOrderSecondary&repairOrderSecondaryId=' + repairOrderSecondaryDTO.idStr;
                } else {
                    alert(response.response);
                }
            }
        });
    }
});

var removeSelectedMan = function (_json, _textVal) {
    if (!_json) {
        return;
    }
    _textVal = _textVal.replace(/\，+/g, ",").replace(/\,+/g, ",");
    if (_textVal.search(/,/) != -1) {
        _textVal = _textVal.substr(0, _textVal.lastIndexOf(","));
    } else {
        _textVal = "";
    }
    var nameList = _textVal.split(',');
    for (var j = 0; j < nameList.length; j++) {
        for (var i = 0, len = _json.length; i < len; i++) {
            if (_json[i].label == nameList[j]) {
                _json.splice(i, 1);
                i = 0;
                len = _json.length;
                break;
            }
        }
    }
    return _json;
};

var showWorkersDropList = function (event) {
    var obj = this;
    var searchValue;
    if ($(obj).val().search(/[,，、]/) != -1) {
        var allTxt = $(obj).val().replace(/[,，、]/g, ",");
        var txtAry = allTxt.split(",");
        searchValue = txtAry[txtAry.length - 1];
    } else {
        searchValue = $(obj).val();
    }
    droplistLite.show({
        event: event,
        name: "name",
        keyword: "keyWord",
        searchValue: searchValue,
        data: "txn.do?method=searchWorks",
        autoSet: "false",
        beforeSelected: function (data) {
            return removeSelectedMan(data, $(obj).val());
        },
        afterSelected: function (event, index, data, hook) {
            var val = $(hook).val();
            if (val.indexOf(',') > 0) {
                $(hook).val($(hook).val().replace(/,[^,]*$/, ',' + data.label) + ',');
            } else {
                $(hook).val(data.label + ',');
            }
        }
    });
}

$(function () {
    $('#fault a').bind('click', function () {
        $(this).text() == '(点击展开)' ? $(this).text('(点击收起)') : $(this).text('(点击展开)');
        $(this).parent().next().toggleClass('hide');
    });

    $('input[placeholder]').bind('focus',function () {
        var $this = this;
        setTimeout(function () {
            var placeholder = $($this).attr('placeholder');
            if (placeholder) {
                $($this).attr('data-placeholder', placeholder);
                $($this).attr('placeholder', '');
            }
        }, 100);
    }).bind('blur', function () {
            var $this = this;
            setTimeout(function () {
                var placeholder = $($this).attr('data-placeholder');
                if (placeholder) {
                    $($this).attr('placeholder', placeholder);
                }
            }, 100);
        });

    $("#endDateStr,#startDateStr").datetimepicker({
        "numberOfMonths": 1,
        "showButtonPanel": true,
        "changeYear": true,
        "changeMonth": true,
        "yearRange": "c-100:c+100",
        "yearSuffix": ""
    });
    $('input[data-node-type=decimal]').bind('input', function () {
        $(this).val($(this).val().replace(/[^0-9.]/g, '')).val(new RegExp('\\d+.?\\d{0,2}').exec($(this).val()));
    });
    $('input[data-node-type=number]').bind('input', function () {
        $(this).val($(this).val().replace(/[^0-9]/g, ''));
    });
    $('input[name$=unit]').bind('click', initUnitClick).each(function () {
        window.initUnit(this);
    });
});

function productValidate() {
}

var newProductCache = {unitSource: []};

window.initUnit = function (dom) {
    var td = $(dom).parents('td');
    var storageUnit = $('input[name$=storageUnit]', td);
    var sellUnit = $('input[name$=sellUnit]', td);
    var unit = $('input[name$=unit]', td);
    var rate = $('input[name$=rate]', td);
    if (storageUnit.val() != sellUnit.val() && rate.val() != '') {
        var tr = td.parents('tr');
        $('span[data-node-type=unit]', tr).unbind().remove();
        var price = $('input[name$=price]', tr);
        vc.setMaterialItemTotal(tr);
        var node = $('<span data-node-type="unit" style="color:#0094FF;cursor: pointer;">' + unit.val() + '</span>').click(function () {
            var text = $(this).text();
            if (text == storageUnit.val()) {
                $(this).text(sellUnit.val());
                unit.val(sellUnit.val());
                price.val(new Number(price.val() / rate.val()).toFixed(1));
            } else {
                price.val(new Number(price.val() * rate.val()).toFixed(1));
                $(this).text(storageUnit.val());
                unit.val(storageUnit.val());
            }
            vc.setMaterialItemTotal(tr);
        });
        $('input[name$=unit]', td).hide().after(node);
    }
}

window.initUnitClick = function () {
    if (!newProductCache.unitSource || newProductCache.unitSource.length < 1) {
        $.ajax({
            type: "POST",
            url: "shop.do?method=getShopUnit",
            async: false,
            cache: false,
            dataType: "json",
            error: function (XMLHttpRequest, error, errorThrown) {
                newProductCache.unitSource = [];
            },
            success: function (json) {
                if (json && json.shopUnitStatus && json.shopUnitStatus == "true") {
                    for (var i = 0; i < json.shopUnitDTOs.length; i++) {
                        newProductCache.unitSource[i] = json.shopUnitDTOs[i].unitName;
                    }
                }
            }
        });
    }
    $(this).autocomplete({
        minLength: 0,
        delay: 0,
        source: newProductCache.unitSource,
        select: function (event, ui) {
            $(this).val(APP_BCGOGO.StringFilter.inputtingCommodityCodeFilter(ui.item.label));
        }
    });
    $(this).autocomplete("search", "");
}

window.initUnitTd = function (dom) {
    window.initUnit(dom);
}

window.addProductItemRow = function (orderType, type, data) {
    if (vc.allValidMaterialItem()) {
        var tr = $('#materialBill tr:last');
        var node = tr.clone(true);
        tr.after(node);
        if ($('span[data-node-type=unit]', node).length) {
            $('input[name$=unit]', node).show();
            $('span[data-node-type=unit]', node).remove();
        }
        $(':input', node).val('');
        $('span', node).text('');
        $('a:last', tr).hide();
        vc.adjustmentIndex($('#materialBill tr'))
    }
    return $('#materialBill tr:last');
}

window.autoAddBlankRow = function (dom) {
    window.addProductItemRow();
}

window.isCanEditUnit = function () {
    return true;
}

var callback = window.initOrderIr;
window.initOrderIr = function (domId, orderType, data) {
    callback(domId, orderType, data);
    var tr = $('#' + domId.replace('.', '\\.')).parents('tr');
    $('input[name$=price]', tr).val(data.recommendedPrice);
    window.setTotal();
}