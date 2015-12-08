/**
 * @description BCGOGO APP 的公用组件
 * @author 潘震
 *
 * @version 0.1.2 2012-07-10
 */

var APP_BCGOGO = {};

/**
 * @description 建立命名空间
 * @example APP_BCGOGO.namespace( 'MySpace' );
 *
 * @param {String} str
 */
APP_BCGOGO.namespace = function (str) {
    var arr = str.split("."), o = APP_BCGOGO;
    for (var i = (arr[0] == "APP_BCGOGO") ? 1 : 0; i < arr.length; i++) {
        o[arr[i]] = o[arr[i]] || {};
        o = o[arr[i]];
    }
};


// Module related
APP_BCGOGO.Module = {};

// Module related
APP_BCGOGO.Page = {};

// Verifier related
APP_BCGOGO.Verifier = {};

// String Filter related
APP_BCGOGO.StringFilter = {
    /**
     * @description 字符过滤器，保证字符只能输入指定长度，过滤完后，返回过滤后的字符
     * @param value
     * @param len
     * @returns {String}
     */
    stringLengthFilter:function (value, len) {
        return (typeof value != "string" || len < 0) ? "" : value.slice(0, len);
    },
    /**
     * @description 输入过程中使用的整数过滤器
     * @param value
     * @returns {String}
     */
    inputtingIntFilter:function (value) {
        var istr = value, ostr = "";
        if (istr) {
            ostr = istr.toString().replace(/[^\d]/g, "").replace(/^0+/, "0");
            if (ostr.length > 1) {
                ostr = ostr.replace(/^0/g, "");
            }
        }
        return ostr;
    },
    /**
     * @description 输入完成后使用的整数过滤器，保证滤除后的字符串是整数, 返回过滤后的字符
     * @param value
     * @returns {String}
     */
    intFilter:function (value) {
        var foo = APP_BCGOGO.Interactive;
        return foo.intFilter(value);
    },
    /**
     * @description 输入进行中 ，float 过滤器
     * @param value
     * @returns {String}
     */
    inputtingFloatFilter:function (value) {
        var istr = value, ostr = "";
        if (istr) {
            // 先做基本过滤
            ostr = istr.toString().replace(/[^\.\d]+/g, "").replace(/\.+/g, ".").replace(/^\./g, "").replace(/^0+/g, "0");
            // 进一步过滤边界
            var regMatch = ostr.match(/^\d+\.\d+/);
            if (regMatch) {
                ostr = regMatch[0];
            }
            var ptIndex = ostr.search(/\./);
            if (ostr.length >= 2 && ptIndex == -1 || ostr.length > 2 && ptIndex > 1) {
                ostr = ostr.replace(/^0+/, "");
            }
        }
        return ostr;
    },
    /**
     * @description 输入结束后， float 过滤器
     * @param value
     * @returns {String}
     */
    floatFilter:function (value) {
        var foo = APP_BCGOGO.Interactive, istr = value, ostr = "";
        ostr = foo.inputtingFloatFilter(istr);
        return ostr ? parseFloat(ostr).toString() : ostr;
    },
    /**
     * @description 价格, 正在输入状态字符过滤器
     * @examples 例1: inputString: "900s" --> outputString: "900"
     *           例2: inputString: "900.9." --> outputString: "900.9"
     *           例3: inputString: "900." --> outputString: "900."
     *           例4: inputString: "09" --> outputString: "9"
     *           例5: inputString: "." --> outputString: ""
     *           例6: inputString: ".9" --> outputString: "9"
     * @param value
     * @returns {String}
     */
    inputtingPriceFilter:function (value) {
        var foo = APP_BCGOGO.Interactive, istr = value, ostr = "", ptIndex = -1;
        ostr = foo.inputtingFloatFilter(istr);
        ptIndex = ostr.search(/\./);
        if (ptIndex != -1 && ostr.length - (ptIndex + 1) > 2) {
            ostr = ostr.slice(0, (ptIndex + 1) + 2);
        }
        return ostr;
    },
    /**
     * @description 价格, 输入完毕后，字符过滤器
     * @examples 例1: inputString: "900s" --> outputString: "900"
     *           例2: inputString: "900.9." --> outputString: "900.9"
     *           例3: inputString: "900." --> outputString: "900"
     *           例4: inputString: "09" --> outputString: "9"
     *           例5: inputString: "900.9.9.9.9.9" --> outputString: "900.9"
     *           例6: inputString: "900.900" --> outputString: "900.9"
     *           例7: inputString: "." --> outputString:""
     *           例8: inputString: ".9" --> outputString:"9"
     *
     * @param value
     * @returns {String}
     */
    priceFilter:function (value) {
        var foo = APP_BCGOGO.Interactive, istr = value, ostr = "";
        ostr = foo.inputtingPriceFilter(istr);
        return ostr ? parseFloat(ostr).toString() : ostr;
    }
};

APP_BCGOGO.Validator = {
    _regTextMatchInt:"^\d+$",
    _regTextMatchHasDecimals:"^\d+\.\d+$",
    _regTextMatchMobilePhoneNumber:"^1\d{10}$",
    _regTextMatchPrice:"^0$|^[1-9]\d*$|^0\.([1-9]|\d[1-9])$|^[1-9]\d*\.([1-9]|\d[1-9])$",
    _equalsTo:function (s, regString) {
        var foo = APP_BCGOGO.Validator, matchResult = s.match(new RegExp(regString));
        return s && matchResult && s === matchResult[0];
    },
    /**
     * @description 判断字符串是不是 整数
     * @param {String} s
     * @returns {Boolean}
     */
    stringIsInt:function (s) {
        var foo = APP_BCGOGO.Validator;
        return foo._equalsTo(s, foo._regTextMatchInt);
    },
    /**
     * @description 判断字符串是不是 数字
     * @param {String} s
     * @returns {Boolean}
     */
    stringIsNumber:function (s) {
        var foo = APP_BCGOGO.Validator;
        return foo._equalsTo(s, foo._regTextMatchInt + "|" + foo._regTextMatchHasDecimals);
    },
    /**
     * @description 判断字符串是不是 手机号码
     * @param {String} s
     * @returns {Boolean}
     */
    stringIsMobilePhoneNumber:function (s) {
        var foo = APP_BCGOGO.Validator;
        return foo._equalsTo(s, foo._regTextMatchMobilePhoneNumber);
    },
    /**
     * @description 判断字符串是不是 价格格式
     * @param {String} s
     * @returns {Boolean}
     */
    stringIsPrice:function (s) {
        var foo = APP_BCGOGO.Validator;
        return foo._equalsTo(s, foo._regTextMatchPrice);
    }
};


// net related
APP_BCGOGO.Net = {};

/**
 * @description 发送 ajax <同步>请求并获得数据,  暂不提供参数校检 , 本接口基于 jQuery 封装
 * @param value
 * {
 *     url:"",   必须
 *     type:"",  必须
 *     data:{},  非必须
 *     success:function(){}, 非必须
 *     error:function(){}  非必须
 * }
 */
APP_BCGOGO.Net.syncAjax = function (value) {
    var retVal;
    // 设置 requestType ，告诉 service ,我是一个 ajax 请求
    if (!value['data']) {
        value['data'] = {};
    }
    value['data']['requestType'] = 'AJAX';
    value['async'] = false;
    // callback
    value['success'] = !value['success'] ? function (data, textStatus, jqXHR) {
        retVal = data;
    } : value['success'];
    value['error'] = !value['error'] ? function (jqXHR, textStatus, errorThrown) {
        retVal = null;
    } : value['error'];
    $.ajax(value);
    return retVal;
};

/**
 * @description 基于 syncAjax 封装的 get ajax 函数
 * @param value
 * {
 *     url:"",   必须
 *     data:{},  非必须
 *     success:function(){}, 非必须
 *     error:function(){}  非必须
 * }
 * @returns {*}
 */
APP_BCGOGO.Net.syncGet = function (value) {
    value['type'] = "GET";
    return APP_BCGOGO.Net.syncAjax(value);
};

/**
 * @description 基于 syncAjax 封装的 post ajax 函数
 * @param value
 * {
 *     url:"",   必须
 *     data:{},  非必须
 *     success:function(){}, 非必须
 *     error:function(){}  非必须
 * }
 * @returns {*}
 */
APP_BCGOGO.Net.syncPost = function (value) {
    value['type'] = "POST";
    return APP_BCGOGO.Net.syncAjax(value);
};

/**
 * @description 发送 ajax <异步>请求并获得数据,  暂不提供参数校检 , 本接口基于 jQuery 封装
 * @param value
 * {
 *     url:"",   必须
 *     type:"",  必须
 *     data:{},  非必须
 *     success:function(){}, 非必须
 *     error:function(){}  非必须
 * }
 */
APP_BCGOGO.Net.asyncAjax = function (value) {
    if (!value['data']) {
        value['data'] = {};
    }
    value['data']['requestType'] = 'AJAX';
    value['async'] = true;
    //callback
    value['success'] = !value['success'] ? function (data, textStatus, jqXHR) {
        // console.log(data);   IE 不支持
    } : value['success'];
    value['error'] = !value['error'] ? function (jqXHR, textStatus, errorThrown) {
        GLOBAL.error("request error");
    } : value['error'];
    $.ajax(value);
};

/**
 * @description 基于 asyncAjax 封装的 get ajax 函数
 * @param value
 * {
 *     url:"",   必须
 *     data:{},  非必须
 *     success:function(){}, 非必须
 *     error:function(){}  非必须
 * }
 * @returns {*}
 */
APP_BCGOGO.Net.asyncGet = function (value) {
    value['type'] = "GET";
    APP_BCGOGO.Net.asyncAjax(value);
};

/**
 * @description 基于 asyncAjax 封装的 post ajax 函数
 * @param value
 * {
 *     url:"",   必须
 *     data:{},  非必须
 *     success:function(){}, 非必须
 *     error:function(){}  非必须
 * }
 * @returns {*}
 */
APP_BCGOGO.Net.asyncPost = function (value) {
    value["type"] = "POST";
    APP_BCGOGO.Net.asyncAjax(value);
};

// TODO  尚未整合的代码， from 蛟龙
(function () {
    APP_BCGOGO.namespace("wjl.LazySearcher");
    /**
     * 延迟搜索器 当用户keyUp后，一定时间内未操作，则发送请求。利于减少下拉建议的请求次数
     */
    var LazySearcher = function () {
        var timeId = null;
        var searchParams = {
            url:"",
            data:{},
            callback:function (response) {
            },
            dataType:"json"
        };
        var ajaxQuery = function () {
            $.get(searchParams.url, searchParams.data, searchParams.callback, searchParams.calltype);
        };
        var getTimeout = function () {
            return  setTimeout(function () {
                if (v) {
                    ajaxQuery();
                }
            }, 300);
        };
        return {
            setSearchParams:function (url, data, callback, calltype) {
                if (!url) {
                    GLOBAL.error("url is null");
                    return;
                }
                searchParams.url = url;
                if (data)searchParams.data = data;
                if (callback)searchParams.callback = callback;
                if (calltype)searchParams.calltype = calltype;
            },
            lazySearch:function (searchWord) {
                if (timeId)
                    clearTimeout(timeId);
                timeId = getTimeout(searchWord);
            }
        }
    }();
    APP_BCGOGO.wjl.LazySearcher = LazySearcher;
})();

/**
 * @description 集合常用工具
 * @author 潘震
 *
 */
APP_BCGOGO.namespace("APP_BCGOGO.Collection");
APP_BCGOGO.Collection.Comparator = {
    /**
     * @description is two array|Object(of strict array and strict Hash Map) equal
     * @param {Array|Object(Hash Map)} a
     * @param {Array|Object(Hash Map)} b
     * @returns {Boolean} is really equal?
     */
    equalsTo:function (a, b) {
        var isEqual = false;
        if (a.length !== b.length) {
            return isEqual;
        }

        isEqual = true;
        $.each(a, function (index, value) {
            if (b[index] !== value) {
                isEqual = false;
                return false;
            }
        });
        return isEqual;
    }
};


//// 蛟龙 TODO to merge to common lib
(function () {
    APP_BCGOGO.namespace("wjl.Collection");
    //SET 简单比对器
    var Comparator = {
        propertyArray:[],
        clear:function () {
            this.propertyArray.length = 0;
        },
        //比对源对象与指定对象 所有属性是否都相等，是：true；否：false.
        equalsTo:function (src, target) {
            var pa = this.propertyArray;
            var propertyLength = pa.length;
            if (propertyLength == 0) {
                return src == target;
            }
            for (var i = 0; i < propertyLength; i++)
                if (src[pa[i]] != target[pa[i]]) {
                    return false;
                }
            return true;
        }
    }

    //集合工具类  SET
    var Set = function () {
        var array = new Array();
        var length = 0;

        this.add = function (target) {
            if (!this.contains(target)) {
                array[length++] = target;
            }
        }
        this.size = function () {
            return length;
        }
        this.getObject = function (index) {
            return array[index];
        }

        //判断Set中是否包含指定的对象
        this.contains = function (target) {
            for (var i = 0, len = array.length; i < len; i++)
                if (Comparator.equalsTo(array[i], target)) {
                    return true;
                }
            return false;
        }
    }

    APP_BCGOGO.wjl.Collection.Set = Set;
    APP_BCGOGO.wjl.Collection.Comparator = Comparator;
})();



