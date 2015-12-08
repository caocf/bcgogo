//数据转换域 add by zhangjuntao
var dataTransition = {
    defaultRoundingBit:2,    //四舍五入缺省为2位
    //四舍五入保留两位
    rounding:function (data, des) {
        if(G.isEmpty(des)){
          des=dataTransition.defaultRoundingBit;
        }
        //判断数字
        if (!G.isNumber(data) || !G.isNumber(des)) return 0;
        data = Number(data).toFixed(des);
        data.toString().replace(/(^\d*\.\d*?)(0*$)/, "$1 ");
        return Number(data);
    },
    sub:function(data1,data2){
       return G.rounding(G.rounding(data1)-G.rounding(data2));
    },
 add:function(data1,data2){
       return G.rounding(G.rounding(data1)+G.rounding(data2));
    },

    //四舍五入保留des位数字，小数点最后不保留0,
    simpleRounding:function (data, des) {
        if (!G.isNumber(data) || !G.isNumber(des)) return 0;
        var returnNumber = Number(data).toFixed(des);
        returnNumber = returnNumber.toString().replace(/(^\d*\.\d*?)(0*$)/, "$1");
        if (returnNumber.indexOf(".") == returnNumber.length - 1) {
            returnNumber = returnNumber.substring(0, returnNumber.length - 1);
        }
        return returnNumber;
    },

    //span 下的数据四舍五入保留两位
    roundingSpanNumber:function (id) {
        var num = $("#totalSpan").html();
        return dataTransition.rounding(Number(num), 2);
    },
    //金额 判断
    amountConvert:function (time1, time2, id, flag) {
        var value = $(id).val();
        if (/^\.{1}\d+$/.test(value)) {
            value = $(id).val(0 + value).val();
        }
        if (!regExpPattern.number.test(value) && !/^\.{1}$/.test(value) && "" != value) {
            time2 = new Date().getTime();
            var d = time2 - time1;
            if (d > 3000 || flag) {
                time1 = time2;
                flag = false;
                showMessage.fadeMessage("35%", "", "slow", 2000, "只能输入合适的金额！");
            }
            if (!/^\.{1}$/.test(value)) {
                $(id).val("");
            }
        }
        return time1;
    },
    //过滤掉html标签
    stripHTML:function (data) {
        return data.replace(/<[^>]*>/g, "");
    }
};


var calculate = {
    //总价  应付 欠款
    subtraction:function (totalAmountId, payedAmountId, owedAmountId, makeTime, message) {
        var value = $(payedAmountId).val();
        if ("" == value || /^\.{1}$/.test(value)) {
            $(payedAmountId).val("0");
        }
//        else if (/^\d+\.{1}$/.test(value)) {
////            $(payedAmountId).val(value.substring(0, value.length - 1));
//            if ($(payedAmountId).val() == 0) {
//                $(payedAmountId).val("0");
//            }
//        }
        //获得到的id的相应dom不一定是input,不一定是通过val()的方式得到值. By Lee.E
        var totalAmount=$(totalAmountId).val()?$(totalAmountId).val():$(totalAmountId).text();
        totalAmount = Number(totalAmount);
        var payedAmount = Number($(payedAmountId).val());
//        $(payedAmountId).val(dataTransition.rounding(payedAmount, 2));
        var owedAmount = totalAmount - payedAmount;
//    $("#isMakeTime").val("0");   //isMakeTime = 0 表示未设还款时间，1表示已设
        if (owedAmount < 0) {
            $(payedAmountId).val(totalAmount);
            $(owedAmountId).val(0);
            message = "实收金额大于总计金额。";
            showMessage.fadeMessage("35%", "40%", "slow", 2000, message);   // top left fadeIn fadeOut message
            $(makeTime).hide();
            $(this)
                .select()
                .focus();
        } else if (owedAmount > 0) {
            $(owedAmountId).val(dataTransition.rounding(owedAmount, 2));
            if (Number($(owedAmountId).val()) + payedAmount - totalAmount>0.0001) {
                $(owedAmountId).val(dataTransition.rounding(owedAmount, 2));
                message = "请输入合适的欠款金额。";
                showMessage.fadeMessage("35%", "40%", "slow", 2000, message);
            } else {
                message = "";
            }
//      message = "请设置还款时间";   //BCSHOP-2562 还款时间不强制设置
            //显示还款时间
            $(makeTime).show();
        } else {
            $(owedAmountId).val(dataTransition.rounding(owedAmount, 2));
            message = "";
            $(makeTime).hide();
        }
        return message;
    },
    subtraction2:function (totalAmountId, payedAmountId, owedAmountId, makeTime, message) {
        var totalAmount = Number($(totalAmountId).val());
        var payedAmount = Number($(payedAmountId).val());
        var owedAmount = Number($(owedAmountId).val());
        if(owedAmount>totalAmount){
            $(owedAmountId).val(totalAmount);
            $(payedAmountId).val(0);
            message = "请输入合适的欠款金额。";
            showMessage.fadeMessage("35%", "40%", "slow", 2000, message);
            $(makeTime).show();
        }else if($(owedAmountId).val()!="" && $(owedAmountId).val()!="0"){
            $(payedAmountId).val(dataTransition.rounding(totalAmount-owedAmount,2));
            $(makeTime).show();
        }

    }
};

var stringMethod = {
    substring:function (str, num) {
        if ("" == str) {
            return str;
        }
        if (str.length < num) {
            num = str.length;
        }
        return str.substring(0, num);
    }
};

/**
 * 字符串过滤方法
 * 使用：把replacedStr中的regExp过滤成replaceStr
 * @param replacedStr(被过滤的str)
 * @param regExp (过滤表达式)
 * @param replaceStr
 */
var dataFilter = {
    replace:function (replacedStr, regExp, replaceStr) {
        replacedStr = replacedStr.replace(regExp, replaceStr);
        return replacedStr;
    }
};

var dataValidate = {
    test:function (str, regExp) {
        return regExp.test(str);
    }
};

//正则表达式RegExp
var regExpPattern = {
    //字符串
    /**
     * 特殊符号模板
     */
    specificPattern:new RegExp("[`~!@#$^&*()=|\\\\{\\}%_\\+\\-\"':;',\\[\\].<>/?~！@#￥……&*（）——|{}【】‘；：”“'。，、？·～《》]", "g"),

    /**
     * 非数字
     */
    notDigital:/[^0-9]/g,

    /**
     * 校验userNo
     * 用处：登录、增加新用户
     */
    loginPattern:new RegExp("[`~!@#$^&*()=|\\\\{\\}%\\+\\-\"':;',\\[\\]<>/?~！￥……&*（）——|{}【】‘；：”“'。，、？·～《》]", "g"),

    /**
     * 过滤email 特殊字符
     * 用处：登录、增加新用户
     */
    emailPattern:new RegExp("[`~!#$^&*()=|\\\\{\\}%\\+\\-\"':;',\\[\\]<>/?~！￥……&*（）——|{}【】‘；：”“'。，、？·～《》]", "g"),

    //正则表达式

    /**
     * 手机号校验
     */
    mobile:/^(?:1[3|5|8]\d)-?\d{5}(\d{3}|\*{3})$/,

    /**
     * u4E00-\u9FA5 表示中文
     * \w 表示字母及下划线
     * d 表示数字
     */
    userNo:/^[\u4E00-\u9FA5\w\d]+$/,

    /**
     * 数字
     */
    digital:/^\d+$/,

    /**
     * 字母
     */
    alpha:/^[A-Za-z]+$/,

    /**
     * 数字
     */
    number:/^\d+(\.{0,1}\d*)$/,

    /**
     *  /^(([a-zA-Z0-9_-])是表示 @ 符号之前的字符串是由 小写字母、大写字母、数字、下划线、中划线多个字符组成字符串
     *  \.[a-zA-Z0-9_-] 表示由小黑点和小写字母、大写字母、数字、下划线、中划线多个字符组成字符串
     */
    email:/^([a-zA-Z0-9_-|\.])+@([a-zA-Z0-9_-])+\.([a-zA-Z0-9_-])+$/
};

GLOBAL.rounding=dataTransition.rounding;
GLOBAL.sub=dataTransition.sub;
GLOBAL.add=dataTransition.add;
GLOBAL.round=APP_BCGOGO.StringFilter.inputtingPriceFilter;

