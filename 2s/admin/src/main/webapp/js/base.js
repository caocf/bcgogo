/**
 * @description base.js 是一个公用组件
 * @author 潘震
 *
 * @version v0.1.2  12-07-03 PM03:46
 */
var GLOBAL = {};

/**
 * @description 建立命名空间
 * @example GLOBAL.namespace( 'GLOBAL.John' );
 *
 * @param {String} str
 */
GLOBAL.namespace = function (str) {
    var arr = str.split("."), o = GLOBAL;
    for (var i = (arr[0] == "GLOBAL") ? 1 : 0; i < arr.length; i++) {
        o[arr[i]] = o[arr[i]] || {};
        o = o[arr[i]];
    }
};

// Dom 相关
GLOBAL.Dom = {};

/**
 * 查找并获得下一个html节点
 * @param {String|Object} node node or node name
 * @returns {Object} next node
 *
 */
GLOBAL.Dom.getNextNode = function (node) {
    node = typeof node == "string" ? document.getElementById(node) : node;
    var nextNode = node.nextSibling;
    if (!nextNode)return null;
    // document.all is a property IE supported
    if (!document.all) {
        while (true) {
            if (nextNode.nodeType == 1) {
                break;
            } else {
                if (nextNode.nextSibling) {
                    nextNode = nextNode.nextSibling;
                } else {
                    break;
                }
            }
        }
    }
    return nextNode;
};

/**
 * 设置透明
 * @param {String} 节点或节点名
 * @param {Num} level 0~100
 */
GLOBAL.Dom.setOpacity = function (node, level) {
    node = typeof node == "stirng" ? document.getElementById(node) : node;
    if (document.all) {
        node.style.filter = 'alpha(opacity=' + level + ')';
    } else {
        node.style.opacity = level / 100;
    }
};

/**
 * 通过class name 获得元素
 * @param {String} str class name
 * @param {Object} root
 * @param {String} tag
 * @returns {Array}
 */
GLOBAL.Dom.getElementsByClassName = function (str, root, tag) {
    if (root) {
        root = (typeof root == "string") ? document.getElementById(root) : root;
    } else {
        root = document.body;
    }
    tag = tag || "*";
    var els = root.getElementsByTagName(tag), arr = [];
    for (var i = 0; n = els.length, i < n; i++) {
        for (var j = 0, k = els[i].className.split(" "), l = k.length; j < l; j++) {
            if (k[j] == str) {
                arr.push(els[i]);
                break;
            }
        }
    }
    return arr;
};

// Event 相关
GLOBAL.Event = {};
/**
 * @description 获得事件对象, 为了兼容 IE 和 Firefox 而提供的一个函数
 * @param {Object} e
 * @returns {Event}
 */
GLOBAL.Event.getEvent = function (e) {
    return window.event || e;
};
/**
 * @description 获得事件源对象
 * @param {Event} e
 * @returns {Object}
 */
GLOBAL.Event.getEventTarget = function (e) {
    e = window.event || e;
    return e.srcElement || e.target;
};
/**
 * @description 停止事件冒泡
 * @param {Event} e
 */
GLOBAL.Event.stopPropagation = function (e) {
    e = window.event || e;
    if (document.all) {
        e.cancelBubble = true;
    } else {
        e.stopPropagation();
    }
};

/**
 * 绑定事件
 * @param {Object|String} node
 * @param {String} eventType
 * @param {Function} handler
 */
GLOBAL.Event.on = function (node, eventType, handler) {
    node = typeof node == "string" ? document.getElementById(node) : node;
    if (document.all) {
        node.attachEvent("on" + eventType, handler);
    } else {
        node.addEventListener(eventType, handler, false);
    }
};


// interactive 相关
GLOBAL.Interactive = {};

/**
 * @description 从键盘事件对象取得 keyCode
 * @param {KeyboardEvent} e
 * @returns {Number}
 */
GLOBAL.Interactive.keyCodeFromEvent = function (e) {
    return e.witch || e.keyCode;
};

/**
 * @description 从键盘事件对象获得 keyName
 * @param {KeyboardEvent} e
 * @returns {String}
 */
GLOBAL.Interactive.keyNameFromEvent = function (e) {
    var map = {"38":"up", "40":"down", "37":"left", "39":"right", "13":"enter", "9":"tab", "27":"esc", "32":"space", "8":"backspace"};
    var keyCodeString = GLOBAL.Interactive.keyCodeFromEvent(e).toString();
    return map[keyCodeString];
};

/**
 * @description 比较键盘事件中 keyCode 和 targetCode 是否相等
 * @param {KeyboardEvent} e
 * @param {Number|String} targetCode
 * @returns {Boolean}
 */
GLOBAL.Interactive.isKeyCode = function (e, targetCode) {
    return GLOBAL.Interactive.keyCodeFromEvent(e).toString() === targetCode.toString();
};

/**
 * @description 比较键盘事件中 keyName 和 targetName 是否相等
 * @param {KeyboardEvent} e
 * @param {String} targetName
 * @returns {Boolean}
 */
GLOBAL.Interactive.isKeyName = function (e, targetName) {
    return GLOBAL.Interactive.keyNameFromEvent(e) === targetName.toString();
};


/**
 * @description 获取浏览器的长和宽
 * @example var scrollWidth = (parseFloat(GLOBAL.Interactive.W.getWidth())-220)/2;
 *          var scrollHeight = (parseFloat(GLOBAL.Interactive.W.getHeight())-140)/2;
 *          假设有一个浮动框 B 他的长和宽分别是220,140 即 {width:220, height:140}, 那么要让 B 位于浏览器上下居中，scrollWidth 和 scrollHeight 就是位置
 * @type {String} 例如："618px"
 */
GLOBAL.Interactive.W = {
    getWidth:function () {
        var a, b;
        if ($.browser.msie && $.browser.version >= 8) {
            a = Math.max(document.documentElement.scrollWidth, document.body.scrollWidth);
            b = Math.max(document.documentElement.offsetWidth, document.body.offsetWidth);
            if (a < b) {
                return $(window).width() + "px"
            } else {
                return a + "px"
            }
        } else {
            return $(document).width() + "px"
        }
    },
    getHeight:function () {
        var b, a;
        if ($.browser.msie && $.browser.version >= 8) {
            b = Math.max(document.documentElement.scrollHeight, document.body.scrollHeight);
            a = Math.max(document.documentElement.offsetHeight, document.body.offsetHeight);
            if (b < a) {
                return $(window).height() + "px"
            } else {
                return b + "px"
            }
        } else {
            return $(document).height() + "px"
        }
    }
};


// Lang 相关
GLOBAL.Lang = {};

/**
 * @description 滤除字符串两边的空格
 * @param {String} ostr
 * @returns {*}
 */
GLOBAL.Lang.trim = function (ostr) {
    return ostr ? ostr.replace(/^\s+|\s+$/g, "") : ostr;
};

/**
 * @description 查找字符串是否以 目标字符串开始
 * @param sourceStr
 * @param str
 * @return {Boolean}
 */
GLOBAL.Lang.startWith = function (sourceStr, str) {
    return ( typeof str != 'string' || sourceStr.search(str) != 0 ) ? false : true;
};

/**
 * @description 查找字符串是否以 目标字符串结束
 * @param sourceStr
 * @param str
 * @return {Boolean}
 */
GLOBAL.Lang.endWith = function (sourceStr, str) {
    return (typeof str == 'string' && sourceStr.search(str) != -1 && (sourceStr.search(str) + str.length) == sourceStr.length)
        ? true : false;
};

// 尽量少扩展原有String 对象，为了避免冲突。
// String Class Extends
String.prototype.startWith = function (str) {
    var foo = GLOBAL.Lang;
    return foo.startWith(this, str);
};

String.prototype.endWith = function (str) {
    var foo = GLOBAL.Lang;
    return foo.endWith(this, str);
};


/**
 * @description 是 Number 吗？
 * @param {*} s
 * @returns {Boolean}
 */
GLOBAL.Lang.isNumber = function (s) {
    return !isNaN(s);
};

GLOBAL.Lang._isNumberPrototype = function (s) {
    "use strict";
    return Object.prototype.toString.call(s) === '[object Number]';
};

/**
 * @description 是 Array 类型吗？
 * @param {*} s
 * @return {Boolean}
 */
GLOBAL.Lang.isArray = function (s) {
    "use strict";
    return !!(s &&
        typeof s === 'object' &&
        typeof s.length === 'number' &&
        !s.propertyIsEnumerable('length'));

//    return Object.prototype.toString.call(s) === '[object Array]';
};

/**
 * @description 是 String 类型吗？
 * @param {*} s
 * @return {Boolean}
 */
GLOBAL.Lang.isString = function (s) {
    return Object.prototype.toString.call(s) === '[object String]';
};

/**
 * @description 是 undefined 吗？
 * @param {*} s
 * @return {Boolean}
 */
GLOBAL.Lang.isUndefined = function (s) {
    return Object.prototype.toString.call(s) === '[object Undefined]';
};

/**
 * @description 是 null 吗？
 * @param {*} s
 * @return {Boolean}
 */
GLOBAL.Lang.isNull = function (s) {
   "use strict";
    return Object.prototype.toString.call(s) === '[object Null]' || s === null;
};

/**
 * @description 是 Boolean 类型吗？
 * @param {*} s
 * @return {Boolean}
 */
GLOBAL.Lang.isBoolean = function (s) {
    return Object.prototype.toString.call(s) === '[object Boolean]';
};

GLOBAL.Lang.isObject = function (s) {
    "use strict";
    return Object.prototype.toString.call(s) === '[object Object]';
};

/**
 * @description 获取一个对象中的属性个数
 * @params {Object} o
 * @returns {Number} 属性个数
 */
GLOBAL.Lang.getPropertiesCount = function (o) {
    "use strict";
    var count = 0;
    for (var i in o) {
        if (o.hasOwnProperty(i)) {
            count++;
        }
    }
    return count;
};

/**
 * @description 判断, scope 中是否包含 o 元素
 * @param {*} o 此变量的值范围在 -- >  Number | NaN | String completer| Boolean | null | undefined
 * @param {Array} scope
 * @returns {Boolean}
 */
GLOBAL.Lang.contains = function (o, scope) {
    "use strict";
    for (var i = 0, len = scope.length; i < len; i++) {
        if (o === scope[i]
            || GLOBAL.Lang._isNumberPrototype(o) && isNaN(o))
            return true
    }
    return false;
};

/**
 * @description 是否为空, 空的定义：null, "null", undefined, "undefined", "", "   ", [], {}
 * @param {*} str
 * @returns {Boolean}
 */
GLOBAL.Lang.isEmpty = function (o, emptyExtended) {
    "use strict";
    var foo = GLOBAL.Lang,
        isEmpty = foo.isNull(o)
            || foo.isUndefined(o)
            || foo.isString(o) && foo.trim(o) === ""
            || foo.isString(o) && (foo.trim(o) === "null" || foo.trim(o) === "undefined")
            || foo.isArray(o) && o.length === 0
            || foo.isObject(o) && foo.getPropertiesCount(o) === 0,
        emptyDefined = [];

    return isEmpty || G.contains(o, emptyDefined.concat(emptyExtended));
};

/**
 * @description 字符串归一化函数,  作用为： 若传入的参数是"空", 如: null, "null", undefined, "undefined"， 则都归一化成字符串 ""
 * @param s
 * @param {String} 若要自定义 "空" 归一化后的字符串， 请传入你的标准
 * @param {Array of String} 若要扩展 "空" 的定义， 请传入参数
 * @return {String}
 */
GLOBAL.Lang.normalize = function (s, std, nullExtended) {
    "use strict";
    var nullDefined = [null, undefined, "null", "undefined"].concat(nullExtended);
    return G.Lang.contains(s, nullDefined) ? (std || "") : s;
};


/**
 * @description 继承类
 * @param {Object} subClass
 * @param {Object} superClass
 */
GLOBAL.Lang.extend = function (subClass, superClass) {
    var F = function () {
    };
    F.prototype = superClass.prototype;
    subClass.prototype = new F();
    subClass.prototype.constructor = subClass;
    subClass.superclass = SUPERCLASS.prototype;
    if (superClass.prototype.constructor == Object.prototype.constructor) {
        superClass.prototype.constructor = superClass;
    }
};

// Collection related
GLOBAL.Collection = {};
GLOBAL.Collection.Comparator = {
    equalTo:function (sourceArr, targetArr) {
        var foo = GLOBAL.Lang;
        if (foo.isArray(sourceArr) && foo.isArray(targetArr) && sourceArr.length == targetArr.length) {
            for (var i = 0, len = sourceArr.length; i < len; i++) {
                if (sourceArr[i] !== targetArr[i])
                    return false;
            }
            return true;
        }
        return false;
    }
};

// 查看数组中是否有某个值， 使用 jQuery 中的 inArray


/**
 * @description 对于指定 node 增加 class
 * @param {String|Number} node node 或者 node 名
 * @param {String} str
 */
GLOBAL.Dom.addClass = function (node, str) {
    if (!new RegExp("(^|\\s+)" + str).test(node.className)) {
        node.className = node.className + " " + str;
    }
};

/**
 * @description 从指定的 node 移除指定 class
 * @param {String|Number} node 或者 node 名
 * @param {String} str
 */
GLOBAL.Dom.removeClass = function (node, str) {
    node.className = node.className.replace(new RegExp("(^|\\s+)" + str), "");
};


// Util 相关
GLOBAL.Util = {};

GLOBAL.Util.getUrlParameter = function (name) {
    return decodeURIComponent((new RegExp('[?|&]' + name + '=' + '([^&;]+?)(&|#|;|$)').exec(location.search) || [, ""])[1].replace(/\+/g, '%20')) || "";
};

/**
 * @description 生成 UUID(GUID)
 * @returns {String} UUID(GUID)
 */
GLOBAL.Util.generateUUID = (typeof(window.crypto) != 'undefined' &&
    typeof(window.crypto.getRandomValues) != 'undefined') ?
    function () {
        // 如果我们有一个加密安全的伪随机数发生器, 参考文章: http://statmath.wu.ac.at/prng/
        var buf = new Uint16Array(8);
        // cryptographically secure PRNG(Pseudo-Random Number Generator), use this
        window.crypto.getRandomValues(buf);
        var S4 = function (num) {
            var ret = num.toString(16);
            while (ret.length < 4) {
                ret = "0" + ret;
            }
            return ret;
        };
        return (S4(buf[0]) + S4(buf[1]) + "-" + S4(buf[2]) + "-" + S4(buf[3]) + "-" + S4(buf[4]) + "-" + S4(buf[5]) + S4(buf[6]) + S4(buf[7]));
    }

    :

    function () {
        // 否则，我们使用 Math.random() 来生成伪随机数
        return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
            var r = Math.random() * 16 | 0, v = c == 'x' ? r : (r & 0x3 | 0x8);
            return v.toString(16);
        });
    };

/**
 * @description compare two numbers
 * @param {Number|String} a
 * @param {Number|String} b
 * @param {Number} figure
 * @returns {Boolean}
 */
GLOBAL.Util.isNumbersEqual = function(a, b, figure) {
    var precision = (figure || 5) * 0.1, numA = parseFloat(a), numB = parseFloat(b);
    return (isNaN(numA) || isNaN(numB)) ?
        false
        :
        Math.abs(numA - numB) < precision;
};


// Cookie 相关
GLOBAL.Cookie = {
    /**
     * @description 通过键值名读取 Cookie
     * @param {String} name
     * @returns {String}
     */
    read:function (name) {
        var cookieStr = "; " + document.cookie + "; ";
        var index = cookieStr.indexOf("; " + name + "=");
        if (index != -1) {
            var s = cookieStr.substring(index + name.length + 3, cookieStr.length);
            return unescape(s.substring(0, s.indexOf("; ")));
        } else {
            return null;
        }
    },

    /**
     * @description 通过键值名设置 Cookie
     * @param {String} name
     * @param {String} value
     * @param {Number} expires 单位是 (天)
     */
    set:function (name, value, expires) {
        var expDays = expires * 24 * 60 * 60 * 1000;
        var expDate = new Date();
        expDate.setTime(expDate.getTime() + expDays);
        var expString = expires ? "; expires=" + expDate.toGMTString() : "";
        var pathString = ";path=/";
        document.cookie = name + "=" + escape(value) + expString + pathString;
    },

    /**
     * @description 通过键值名删除 Cookie
     * @param {String} name
     */
    del:function (name) {
        var exp = new Date(new Date().getTime() - 1);
        var s = this.read(name);
        if (s != null) {
            document.cookie = name + "=" + s + ";expires=" + exp.toGMTString() + ";path=/";
        }
    }
};


// Display related
GLOBAL.Display = {
    // get x-axis position (px)
    getX:function (element) {
        var x = 0;
        while (element) {
            x = x + element.offsetLeft;
            element = element.offsetParent;
        }
        return x;
    },

    // get y-axis position (px)
    getY:function (element) {
        var y = 0;
        while (element) {
            y = y + element.offsetTop;
            element = element.offsetParent;
        }
        return y;
    }
};

// Date 相关
GLOBAL.Date = {};

/**
 * 获取当前格式化后的时间字符串
 * @returns {String} 格式化后的时间
 */
GLOBAL.Date.getCurrentFormatDate = function () {
    var date = new Date();
    var y = 0, m = 0, d = 0, formattedDate = "";
    // date init
    // compatible for IE and Firefox
    y = date.getFullYear();
    m = date.getMonth() + 1;
    d = date.getDay();

    formattedDate += y + "-";
    if (m < 10) {
        formattedDate += "0";
    }
    formattedDate += m + "-";
    if (d < 10) {
        formattedDate += "0";
    }
    formattedDate += d;

    return formattedDate;
};


/**
 * @description 此方法为私有方法，不对外提供，打印日志基础函数
 * @param info
 * @private
 */
GLOBAL._log = function (info) {
    try {
        console.log(info);
    } catch (e) {
        ;
    }
};

/**
 * @description info 级别 log 打印，此log用于基本的提示信息，log级别最低
 * @param info
 */
GLOBAL.info = function (info) {
    GLOBAL._log("== INFO ==");
    GLOBAL._log(info);
};

/**
 * @description debug 级别 log 打印，此log
 * @param info
 */
GLOBAL.debug = function (info) {
    GLOBAL._log("== DEBUG ==");
    GLOBAL._log(info);
};

/**
 * @description warning 级别 log 打印
 * @param info
 */
GLOBAL.warning = function (info) {
    GLOBAL._log("== WARNING ==");
    GLOBAL._log(info);
};

/**
 * @description error 级别 log 打印
 * @param info
 */
GLOBAL.error = function (info) {
    GLOBAL._log("== ERROR ==");
    GLOBAL._log(info);
};

/*
 函数：把字符串转换为日期对象
 参数：yyyy-mm-dd或dd/mm/yyyy形式的字符串
 返回：Date对象
 注：IE下不支持直接实例化日期对象，如new Date("2011-04-06")
 */
function convertDate(date) {
    var flag = true;
    var dateArray = date.split("-");
    if (dateArray.length != 3) {
        dateArray = date.split("/");
        if (dateArray.length != 3) {
            return null;
        }
        flag = false;
    }
    var newDate = new Date();
    if (flag) {
        // month从0开始
        newDate.setFullYear(dateArray[0], dateArray[1] - 1, dateArray[2]);
    }
    else {
        newDate.setFullYear(dateArray[2], dateArray[1] - 1, dateArray[0]);
    }
    newDate.setHours(0, 0, 0);
    return newDate;
}

var G = GLOBAL;
GLOBAL.isEmpty = GLOBAL.Lang.isEmpty;
GLOBAL.contains = GLOBAL.Lang.contains;
GLOBAL.normalize = GLOBAL.Lang.normalize;