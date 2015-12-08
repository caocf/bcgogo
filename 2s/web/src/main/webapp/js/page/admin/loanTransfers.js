$(function() {
    $("#loan_transfers_amount")
        .keyup(function(e) {
            e.target.value = APP_BCGOGO.StringFilter.inputtingPriceFilter($(this).val());
        })
        .blur(function(e) {
            var price = APP_BCGOGO.StringFilter.priceFilter($(this).val());
            e.target.value = Number(price).toFixed(0);
        });
    $("#loanMoneyForm").submit(function(e) {
        if (checkRequest()) {
            var options = {
                target: '#loanMoneyForm',
                type:'post',dataType:"text",resetForm:true
            };
            $(e.target).ajaxSubmit(options);
        } else {
            return false;
        }
    });

    $("#softProduct").click(function(e) {
        if (!e.target.checked) {
            $(".shopVersion input").each(function(index, domObject) {
                domObject.checked = false;
            });
        }
    });
    $("#hardProduct").click(function(e) {
        if (!e.target.checked) {
            $(".products input").each(function(index, domObject) {
                domObject.checked = false;
            });
        }
    });

    $(".products input").click(function() {
        var inputs = $(".products input");
        var hasProducts = false;
        for (var i = 0,max = inputs.length; i < max; i++) {
            if ($(inputs[i]).attr("checked")) {
                hasProducts = true;
                break;
            }
        }
        if (hasProducts) {
            $("#hardProduct").attr("checked", true);
        } else {
            $("#hardProduct").attr("checked", false);
        }
    });

    $(".shopVersion input").click(function() {
        var inputs = $(".shopVersion input");
        var hasShopType = false;
        for (var i = 0,max = inputs.length; i < max; i++) {
            if ($(inputs[i]).attr("checked")) {
                hasShopType = true;
                break;
            }
        }
        if (hasShopType) {
            $("#softProduct").attr("checked", true);
        } else {
            $("#softProduct").attr("checked", false);
        }
    });

    function checkRequest() {
        var form = $("#loanMoneyForm")[0];
        var amount = form.amount.value;
        if (!amount || Number(amount) == 0) {
            nsDialog.jAlert("请输入货款金额！", null, function() {
            });
            form.amount.focus();
            return false;
        }
        if (Number(amount) >= 1000000) {
            nsDialog.jAlert("订单金额超过100,0000.00元！", null, function() {
            });
            form.amount.focus();
            return false;
        }
        if (Number(amount) < 1) {
            nsDialog.jAlert("订单金额小于1.00元！", null, function() {
            });
            form.amount.focus();
            return false;
        }
        if ($("#hardProduct").attr("checked")) {
            var productInputs = $(".products input");
            var hasProducts = false;
            for (var i = 0,max = productInputs.length; i < max; i++) {
                if ($(productInputs[i]).attr("checked")) {
                    hasProducts = true;
                    break;
                }
            }
            if (!hasProducts) {
                nsDialog.jAlert("请选择硬件产品!", null, function() {
                });
                return false;
            }
        }
        if ($("#softProduct").attr("checked")) {
            var shopTypeInputs = $(".shopVersion input");
            var hasShopType = false;
            for (i = 0,max = shopTypeInputs.length; i < max; i++) {
                if ($(shopTypeInputs[i]).attr("checked")) {
                    hasShopType = true;
                    break;
                }
            }
            if (!hasShopType) {
                nsDialog.jAlert("请选择软件产品!", null, function() {
                });
                return false;
            }
        }
        if (!$("#hardProduct").attr("checked") && !$("#softProduct").attr("checked")) {
            nsDialog.jAlert("请选择相应的产品!", null, function() {
            });
            return false;
        }
        return true;
    }

    tableUtil.tableStyle('#tb_tui','.tab_title');
});