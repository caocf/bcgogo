var st = {
    dom: {
        orderTotal: null,               //总计
        cashAmount: null,               //现金
        bankAmount: null,               //银联
        bankCheckAmount: null,          //支票
        memberAmount: null,             //会员储值
        prepaidAmount: null,            //预付款
        settledAmount: null,            //实收金额
        settledAmountLabel: null,       //实收金额标签
        accountDebtAmount: null,        //挂账金额
        accountDebtAmountLabel: null,   //挂账金额标签
        accountDiscount: null,          //优惠金额
        accountDiscountLabel: null,     //优惠金额标签
        discount: null,                 //折扣
        totalLabel: null,               //合计标签
        confirmBtn: null,               //提交按钮
        memberDiscountCheck: null,      //享受会员折扣
        discountWord: null,             //“折”
        accountMemberNo: null,          //会员卡号
        smsSwitch: null,                //发送短信开关
        sendMessage: null,               //是否发生短信
        couponAmount: null              //代金券金额
    },
    getDomList: function () {   //对输入金额有影响的输入框
        var dom = st.dom;
        return [dom.cashAmount, dom.bankAmount, dom.bankCheckAmount, dom.memberAmount, dom.accountDiscount, dom.accountDebtAmount, dom.discount];
    },
    getDomList2: function () {  //用于双击事件处理
        var dom = st.dom;
        return [dom.cashAmount, dom.bankAmount, dom.bankCheckAmount, dom.memberAmount, dom.accountDebtAmount, dom.accountDiscount];
    },
    getDomList3: function () {  //用于计算实付金额
        var dom = st.dom;
        return [dom.cashAmount, dom.bankAmount, dom.bankCheckAmount, dom.memberAmount];
    },
    initDom: function () {
    },
    initCommonDom: function () {
        st.dom.orderTotal = $('#orderTotal');
        st.dom.cashAmount = $('#cashAmount');
        st.dom.bankAmount = $('#bankAmount');
        st.dom.bankCheckAmount = $('#bankCheckAmount');
        st.dom.memberAmount = $('#memberAmount');
        st.dom.settledAmount = $('#settledAmount');
        st.dom.accountDebtAmount = $('#accountDebtAmount');
        st.dom.accountDiscount = $('#accountDiscount');
        st.dom.discount = $('#discount');
        st.dom.confirmBtn = $('#confirmBtn');
        st.dom.memberDiscountCheck = $('#memberDiscountCheck');
        st.dom.discountWord = $('#discountWord');
        st.dom.accountMemberNo = $('#accountMemberNo');
        st.dom.errorInfo = $('#errorInfo');
        st.dom.settledAmountLabel = $('#settledAmountLabel');
        st.dom.accountDebtAmountLabel = $('#accountDebtAmountLabel');
        st.dom.accountDiscountLabel = $('#accountDiscountLabel');
        st.dom.totalLabel = $('#totalLabel');
        st.dom.smsSwitch = $('#smsSwitch');
        st.dom.sendMessage = $('#sendMessage');
        st.dom.couponAmount = $('#couponAmount');
        //应收总计小于0则不允许点击结算
        if(st.dom.orderTotal.text()*1<0){
            st.dom.confirmBtn.attr('disabled', true);
        }
    },
    initBind: function () {
        //挂账金额
        st.dom.accountDebtAmount.bind('input', st.handle.accountDebtAmount);
        //优惠金额
        st.dom.accountDiscount.bind('input', st.handle.accountDiscount.input);
        //折扣
        st.dom.discount.bind('input', st.handle.discount);
        //付款输入框
        $.each(st.getDomList(), function () {
            this.bind('input', st.handle.numberFilter).bind('input', st.calculateTotal).bind('input', delayCall(st.handle.discountPlaceholder, 300, 'discountPlaceholder')).
                bind('input', delayCall(st.addErrorStyle, 300, 'addErrorStyle')).bind('input', delayCall(st.moneyColor, 300, 'moneyColor')).
                bind('input', delayCall(st.setSmsStatus, 500, 'setSmsStatus')).bind('blur', st.handle.inputAfter);
        });
        $.each(st.getDomList3(), function () {
            this.bind('input', st.handle.settledAmountInput).bind('input', delayCall(st.handle.settledAmount));
        });
        //享受会员折扣
        st.dom.memberDiscountCheck.bind('change', st.handle.memberDiscountCheck).bind('change', st.addErrorStyle);
        //双击事情处理
        $.each(st.getDomList2(), function () {
            this.bind('dblclick', st.handle.dblclick);
        });
    },
    handle: {
        settledAmountInput: function () { //当且仅当挂账金额和优惠金额为空时实收金额两两相互关联
            var amount = st.getAmount();
            getNumber($(this).val()) > amount.orderTotal && $(this).val(amount.orderTotal);
            if (st.isBlank(st.dom.accountDebtAmount.val()) && st.isBlank(st.dom.accountDiscount.val())) {
                var node = $(this);
                var list = [];
                $.each(st.getDomList3(), function () {
                    node.attr('id') != this.attr('id') && st.isNotBlank(this.val()) && list.push(this);
                });
                if (list.length == 1) {
                    var target = list[0];
                    amount = st.getAmount();
                    var money = decimalFilter(amount.orderTotal - node.val(), 2);
                    target.val(money);
                }
            }
        },
        settledAmount: function () {
            st.calculateCash();
            var dom = st.dom;
            if (st.isBlank(dom.accountDebtAmount.val()) && st.isNotBlank(dom.accountDiscount.val())) {
                st.calculateDiscountCash();
            } else {
                st.calculateDebtCash();
            }
        },
        discount: function () {
            var amount = st.getAmount();
            amount.discount > 10 && (amount.discount = 10) && st.dom.discount.val(amount.discount);
            if (amount.discount == 10) {
                st.dom.accountDiscount.val(0);
            } else {
                var accountDiscount = decimalFilter((1 - amount.discount / 10) * getNumber(st.dom.orderTotal.text()), 2);
                st.dom.accountDiscount.val(accountDiscount);
            }
            st.handle.accountDiscount.handle();
        },
        discountPlaceholder: function () {
            if (st.isBlank(st.dom.discount.val())) {
                st.dom.discount.attr('data-placeholder', '请输入折扣').attr('placeholder', '请输入折扣');
            } else {
                var discount = getNumber(st.dom.discount.val());
                if (discount == 0) {
                    st.dom.discount.attr('data-placeholder', '全额优惠').attr('placeholder', '全额优惠');
                    st.dom.discount.val('');
                } else if (discount == 10) {
                    st.dom.discount.attr('data-placeholder', '无优惠').attr('placeholder', '无优惠');
                    st.dom.discount.val('');
                }
            }
            st.isNotBlank(st.dom.discount.val()) ? st.dom.discountWord.show() : st.dom.discountWord.hide();
        },
        accountDiscount: {
            handle: function () { //当挂账金额为空时，优惠金额变化后，计算实收金额的值，并付给实收金额中的某个节点
                if (st.isBlank(st.dom.accountDebtAmount.val()) && (st.assist.isNotSettledAmount() || st.assist.haveOne())) {
                    var amount = st.getAmount();
                    var target = st.assist.isNotSettledAmount() ? st.dom.cashAmount : st.assist.getKey();
                    var money = decimalFilter(amount.orderTotal - amount.accountDebtAmount - amount.accountDiscount, 2);
                    money < 0 && (money = 0);
                    (money > 0 || st.isNotBlank(target.val())) && target.val(money);
                }
            },
            input: function () {    //计算折扣,并把技术结果渲染到discount输入框中
                if (st.isBlank(st.dom.accountDiscount.val())) {
                    st.dom.discount.val('');
                } else {
                    var amount = st.getAmount();
                    amount.accountDiscount > amount.orderTotal && st.dom.accountDiscount.val(amount.orderTotal);
                    amount = st.getAmount();
                    var dt = decimalFilter((1 - amount.accountDiscount / amount.orderTotal) * 10, 2);
                    st.dom.discount.val(dt);
                }
                st.handle.accountDiscount.handle();
            }
        },
        accountDebtAmount: function () {
            var amount = st.getAmount();
            amount.accountDebtAmount > amount.orderTotal && st.dom.accountDebtAmount.val(amount.orderTotal);
            amount = st.getAmount();
            if (st.isBlank(st.dom.accountDiscount.val()) && (st.assist.isNotSettledAmount() || st.assist.haveOne())) {
                var target = st.assist.isNotSettledAmount() ? st.dom.cashAmount : st.assist.getKey();
                var money = decimalFilter(amount.orderTotal - amount.accountDebtAmount - amount.accountDiscount, 2);
                money < 0 && (money = 0);
                target.val(money);
            } else {
                st.calculateDiscountCash();
            }
        },
        memberDiscountCheck: function () {
            var $this = this;
            if ($this.checked) {
                if (!$.trim(st.dom.accountMemberNo.val())) {
                    $this.checked = false;
                    nsDialog.jAlert("请输入会员号!");
                } else {
                    APP_BCGOGO.Net.syncAjax({
                        url: "member.do?method=getMemberDiscount",
                        dataType: "json",
                        data: {memberNo: st.dom.accountMemberNo.val(), now: new Date()},
                        success: function (json) {
                            if (json.resu == "error") {
                                $this.checked = false;
                                json.msg == "noMember" && nsDialog.jAlert("没有此会员号!");
                                json.msg == "noCustomer" && nsDialog.jAlert("此会员号没有对应的客户!");
                                json.msg == "customerDelete" && nsDialog.jAlert("此会员号对应的客户已删除!");
                            } else {
                                var memberDiscount = json.memberDiscount ? json.memberDiscount * 1 / 10 : 1;
                                var amount = st.getAmount();
                                amount.orderTotal = decimalFilter(amount.orderTotal * memberDiscount, 2);
                                st.dom.orderTotal.text(amount.orderTotal);
                                var list = [];
                                $.each(st.getDomList2(), function () {
                                    getNumber(this.val()) > 0 && list.push(this);
                                });
                                list.length == 1 && list[0].val(amount.orderTotal);
                            }
                        }
                    });
                }
            } else {
                st.dom.orderTotal.text(st.dom.orderTotal.data('orderTotal'));
                var list = [];
                $.each(st.getDomList2(), function () {
                    getNumber(this.val()) > 0 && list.push(this);
                });
                list.length == 1 && list[0].val(st.dom.orderTotal.text());
            }
        },
        numberFilter: function () {
            $(this).val() != null && $(this).val($(this).val().replace(/[^0-9.]/g, '')).val(new RegExp('\\d+.?\\d{0,2}').exec($(this).val()));
        },
        inputAfter: function () {
            st.isNotBlank($(this).val()) && $(this).val(decimalFilter($(this).val(), 2));
        },
        dblclick: function () {
            var amount = st.getAmount();
            st.cleanInput();
            $(this).val(amount.orderTotal);
            st.handle.accountDiscount.input();
            st.handle.discountPlaceholder();
            st.calculateCash();
            st.moneyColor();
            st.addErrorStyle();
            delayCall(st.setSmsStatus, 500, 'setSmsStatus')();
        }
    },
    cleanInput: function () {
        $.each(st.getDomList(), function () {
            this.val('');
        });
    },
    getAmount: function () {    //获取全部金额
        var result = {};
        result.orderTotal = getNumber(st.dom.orderTotal.text());                //应收总计
        result.settledAmount = getNumber(st.dom.settledAmount.val());           //实收金额
        result.cashAmount = getNumber(st.dom.cashAmount.val());                 // 现金
        result.bankAmount = getNumber(st.dom.bankAmount.val());                 //银联
        result.bankCheckAmount = getNumber(st.dom.bankCheckAmount.val());       //支票
        result.memberAmount = getNumber(st.dom.memberAmount.val());             //会员储值
        result.accountDebtAmount = getNumber(st.dom.accountDebtAmount.val());   //挂账金额
        result.accountDiscount = getNumber(st.dom.accountDiscount.val());       //优惠金额
        result.discount = getNumber(st.dom.discount.val());                     //优惠金额折扣
        result.prepaidAmount = getNumber(st.dom.prepaidAmount.val());           //预付款
        result.couponAmount = getNumber(st.dom.couponAmount.text());            //代金券金额
        return result;
    },
    calculateCash: function () {    //计算实收金额
        var amount = st.getAmount();
        var settledAmount = decimalFilter(amount.cashAmount + amount.bankAmount + amount.bankCheckAmount + amount.memberAmount, 2);//实收金额
        st.dom.settledAmount.val(settledAmount);
        st.dom.settledAmountLabel.text(settledAmount);
        return  settledAmount;
    },
    calculateDebtCash: function () {//计算挂账金额
        var amount = st.getAmount();
        var settledAmount = st.calculateCash();
        var accountDebtAmount = decimalFilter(amount.orderTotal - amount.accountDiscount - settledAmount, 2);
        accountDebtAmount < 0 && (accountDebtAmount = 0);
        accountDebtAmount > 0 ? st.dom.accountDebtAmount.val(accountDebtAmount) : (st.isNotBlank(st.dom.accountDebtAmount.val()) && st.dom.accountDebtAmount.val(0));
        return accountDebtAmount;
    },
    calculateDiscountCash: function () {//计算优惠金额
        var amount = st.getAmount();
        var settledAmount = st.calculateCash();
        var accountDiscount = decimalFilter(amount.orderTotal - amount.accountDebtAmount - settledAmount, 2);
        accountDiscount < 0 && (accountDiscount = 0);
        accountDiscount > 0 ? st.dom.accountDiscount.val(accountDiscount) : (st.isNotBlank(st.dom.accountDiscount.val()) && st.dom.accountDiscount.val(0));
        st.handle.accountDiscount.input();
        return accountDiscount;
    },
    calculateTotal: function () {
        var amount = st.getAmount();
        var total = decimalFilter(st.calculateCash() + amount.accountDebtAmount + amount.accountDiscount, 2);
        st.dom.accountDebtAmountLabel.text(amount.accountDebtAmount);
        st.dom.accountDiscountLabel.text(amount.accountDiscount);
        st.dom.totalLabel.text(total);
        return total;
    },
    addErrorStyle: function () {
        st.removeErrorStyle();
        var amount = st.getAmount();
        if (st.calculateTotal() > amount.orderTotal) {
            st.dom.confirmBtn.attr('disabled', true);
            var settledAmount = st.calculateCash();//实收金额
            settledAmount > amount.orderTotal && $.each(st.getDomList3(), function () {
                getNumber(this.val()) > 0 && this.addClass('error');
            });
            (settledAmount > amount.orderTotal || settledAmount + amount.accountDebtAmount > amount.orderTotal) && amount.accountDebtAmount > 0 && st.dom.accountDebtAmount.addClass('error');
            amount.accountDiscount > 0 && st.dom.accountDiscount.addClass('error');
            settledAmount > amount.orderTotal ? st.dom.errorInfo.text('实收金额大于应收金额！') : st.dom.errorInfo.text('合计金额大于应收金额！');
        } else if (st.calculateTotal() < amount.orderTotal) {
            st.dom.confirmBtn.attr('disabled', true);
            amount.accountDiscount > 0 && st.dom.accountDiscount.addClass('error');
            st.dom.errorInfo.text('合计金额小于应收金额！');
        } else if(amount.orderTotal<0&&amount.couponAmount>0){
            st.dom.confirmBtn.attr('disabled', true);
            st.dom.errorInfo.text('合计金额小于0！');
        }
        else {
            st.removeErrorStyle();
        }
    },
    removeErrorStyle: function () {     //移出错误样式
        st.dom.accountDebtAmount.removeClass('error');
        st.dom.accountDiscount.removeClass('error');
        st.dom.confirmBtn.attr('disabled', false);
        $.each(st.getDomList3(), function () {
            this.removeClass('error');
        });
        st.dom.errorInfo.text('');
    },
    moneyColor: function () {
        var domList = st.getDomList2();
        $.each(domList, function () {
            getNumber(this.val()) > 0 ? this.addClass('txt_blue') : this.removeClass('txt_blue');
        });
    },
    setSmsStatus: function () {
        var map = {};
        $("select[name$='.consumeTypeStr']", window.parent.document).each(function () {
            map[$(this).val()] = true;
        });
        if ((getNumber(st.dom.memberAmount.val()) > 0 && st.isNotBlank(st.dom.accountMemberNo.val())) || map['TIMES']) {
            st.dom.sendMessage.attr('disabled', false);
            st.dom.smsSwitch.val() == 'true' && st.dom.sendMessage.attr('checked', true);
        } else {
            st.dom.sendMessage.attr('checked', false);
            st.dom.sendMessage.attr('disabled', true);
        }
    },
    assist: {
        haveOne: function () {
            var result = 0;
            var domList = st.getDomList3();
            for (var i = 0; i < domList.length; i++) {
                if (domList[i].val() == '') continue;
                result = result + (Number(domList[i].val()) >= 0 ? 1 : 0);
            }
            return result == 1;
        },
        getKey: function () {
            var result = null;
            var domList = st.getDomList3();
            for (var i = 0; i < domList.length; i++) {
                if (domList[i].val() == '') continue;
                if (Number(domList[i].val()) >= 0) result = domList[i];
            }
            return result;
        },
        isNotSettledAmount: function () {
            var result = true;
            var domList = st.getDomList3();
            $.each(domList, function () {
                if (st.isNotBlank(this.val())) {
                    result = false;
                    return false;
                }
            });
            return result;
        }
    },
    isNotBlank: function (val) {
        if (val == null) {
            return false;
        } else {
            val = val.replace(/\s/g, '');
            return val != '';
        }
    },
    isBlank: function (val) {
        if (val == null) {
            return true;
        } else {
            val = val.replace(/\s/g, '');
            return val == '';
        }
    },
    before: function () {
        var placeholder = $([]);
        for (var dom in st.dom) {
            st.dom[dom] == null && (st.dom[dom] = placeholder);
        }
        st.dom.orderTotal.data('orderTotal', st.dom.orderTotal.text());
    },
    placeholderOptimization: function () {
        $('input[placeholder]').bind('focus',function () {
            var placeholder = $(this).attr('placeholder');
            st.isNotBlank(placeholder) && $(this).attr('data-placeholder', placeholder);
            $(this).removeAttr('placeholder');
        }).bind('blur', function () {
                $(this).attr('placeholder', $(this).attr('data-placeholder'));
            });
    },
    start: function (callback) {
        st.initCommonDom();
        st.initDom();
        st.before();
        st.initBind();
        st.calculateTotal();
        callback && callback();
        st.moneyColor();
        st.placeholderOptimization();
        if(st.dom.sendMessage.length){
            st.dom.sendMessage.attr('checked', st.dom.smsSwitch.val() == 'true');
            st.setSmsStatus();
        }
    }
};

/**
 *  保留小数点长度为len，过滤掉最后的0
 * @param val  需要处理的浮点数
 * @param len  保留小数的位数
 * @returns {*}
 */
var decimalFilter = function (val, len) {
    if (isNaN(val)) {
        return 0;
    } else {
        len || (len = 2);
        var target = new Number(val).toFixed(len);
        var result = Number(target);
        var exec = /0*$/.exec(target);
        if (exec != null) {
            result = Number(target.substring(0, exec.index));
        }
        return result
    }
}

/**
 * 延迟调用
 * @param fun  延迟调用函数
 * @param time  延迟时间
 * @returns {Function}
 */
var delayCall = function (fun, time, key) {
    delayCall.map == null && (delayCall.map = {});
    time == null && (time = 200);
    key == null && (key = 'default');
    return function () {
        var $this = this;
        delayCall.map[key] && window.clearTimeout(delayCall.map[key]);
        delayCall.map[key] = window.setTimeout(function () {
            fun.apply($this);
        }, time);
    }
}

var getNumber = function (val) {
    return isNaN(Number(val)) ? 0 : Number(val);
}