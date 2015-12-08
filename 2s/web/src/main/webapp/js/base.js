/**
 * @description base.js 是一个公用组件
 * @author 潘震
 * @date create 2012-06-03
 * @version v0.2.0  13-05-14
 */
var GLOBAL = {};

/**
 * @description add switch to log-info ,
 *              set to true to turn on "GLOBAL.debug()" output
 * @type {Boolean} default is false
 */
GLOBAL.isDebug = false;

/**
 * @description 建立命名空间
 * @example GLOBAL.namespace( 'GLOBAL.John' );
 *
 * @param {String} str
 */
GLOBAL.namespace = function (str) {
    "use strict";
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
    "use strict";
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
    "use strict";
    node = typeof node == "stirng" ? document.getElementById(node) : node;
    if (document.all) {
        node.style.filter = 'alpha(opacity=' + level + ')';
    } else {
        node.style.opacity = level / 100;
    }
};

GLOBAL.Dom.get = function (id) {
    "use strict";
    return document.getElementById(id);
};

/**
 * 通过class name 获得元素
 * @param {String} str class name
 * @param {Object} root
 * @param {String} tag
 * @returns {Array}
 */
GLOBAL.Dom.getElementsByClassName = function (str, root, tag) {
    "use strict";
    if (root) {
        root = (typeof root == "string") ? document.getElementById(root) : root;
    } else {
        root = document.body;
    }
    tag = tag || "*";
    var els = root.getElementsByTagName(tag), arr = [];
    for (var i = 0, n = els.length; i < n; i++) {
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
    "use strict";
    return window.event || e;
};
/**
 * @description 获得事件源对象
 * @param {Event} e
 * @returns {Object}
 */
GLOBAL.Event.getEventTarget = function (e) {
    "use strict";
    e = window.event || e;
    return e.srcElement || e.target;
};
/**
 * @description 停止事件冒泡
 * @param {Event} e
 */
GLOBAL.Event.stopPropagation = function (e) {
    "use strict";
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
    "use strict";
    node = typeof node == "string" ? document.getElementById(node) : node;
    if (document.all) {
        node.attachEvent("on" + eventType, handler);
    } else {
        node.addEventListener(eventType, handler, false);
    }
};


// interactive 相关
GLOBAL.Interactive = {
    _keyMap: {
        "8": "backspace", "9": "tab", "13": "enter", "16": "shift",
        "17": "ctrl", "18": "alt", "27": "esc", "32": "space",
        "37": "left", "38": "up", "39": "right", "40": "down",
        "46": "delete"
    }
};

/**
 * @description 从键盘事件对象取得 keyCode
 * @param {KeyboardEvent} e
 * @returns {Number}
 */
GLOBAL.Interactive.keyCodeFromEvent = function (e) {
    "use strict";
    return e.which || e.keyCode;
};

/**
 * @description 从键盘事件对象获得 keyName
 * @param {KeyboardEvent} e
 * @returns {String}
 */
GLOBAL.Interactive.keyNameFromEvent = function (e) {
    "use strict";
    var foo = GLOBAL.Interactive;
    if (!foo.keyCodeFromEvent(e)) {
        return "other";
    }
    return foo._keyMap[foo.keyCodeFromEvent(e).toString()] || "other";
};

/**
 * @description 从keyCode 获得 keyName
 * @param keyCode
 * @returns {String}
 */
GLOBAL.Interactive.keyNameFromKeyCode = function (keyCode) {
    "use strict";
    var foo = GLOBAL.Interactive;
    if(keyCode){
        return foo._keyMap[keyCode.toString()] || "other";
    }else{
        return "other";
    }

};

/**
 * @description 比较键盘事件中 keyCode 和 targetCode 是否相等
 * @param {KeyboardEvent} e
 * @param {Number|String} targetCode
 * @returns {Boolean}
 */
GLOBAL.Interactive.isKeyCode = function (e, targetCode) {
    "use strict";
    return GLOBAL.Interactive.keyCodeFromEvent(e).toString() === targetCode.toString();
};

/**
 * @description 比较键盘事件中 keyName 和 targetName 是否相等
 * @param {KeyboardEvent} e
 * @param {String} targetName
 * @returns {Boolean}
 */
GLOBAL.Interactive.isKeyName = function (e, targetName) {
    "use strict";
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
    getWidth: function (type) {
        "use strict";
        var a, b, retVal;
        if ($.browser.msie && $.browser.version >= 8) {
            a = Math.max(document.documentElement.scrollWidth, document.body.scrollWidth);
            b = Math.max(document.documentElement.offsetWidth, document.body.offsetWidth);
            if (a < b) {
                retVal = $(window).width();
            } else {
                retVal = a;
            }
        } else {
            retVal = $(document).width();
        }
        return type && type == "number" ? retVal : retVal + "px";
    },
    getHeight: function (type) {
        "use strict";
        var b, a, retVal;
        if ($.browser.msie && $.browser.version >= 8) {
            b = Math.max(document.documentElement.scrollHeight, document.body.scrollHeight);
            a = Math.max(document.documentElement.offsetHeight, document.body.offsetHeight);
            if (b < a) {
                retVal = (window).height();
            } else {
                retVal = b;
            }
        } else {
            retVal = $(document).height();
        }
        return type && type == "number" ? retVal : retVal + "px";
    }
};

/**
 * @description textArea html组件的鼠标焦点位置获取
 * @param textarea
 */
GLOBAL.Interactive.getCursorPosition = function (textarea) {
    "use strict";
    var rangeData = {text: "", start: 0, end: 0 };
    textarea.focus();
    if (textarea.setSelectionRange) { // W3C
        rangeData.start = textarea.selectionStart;
        rangeData.end = textarea.selectionEnd;
        rangeData.text = (rangeData.start != rangeData.end) ? textarea.value.substring(rangeData.start, rangeData.end) : "";
    } else if (document.selection) { // IE
        var i,
            oS = document.selection.createRange(),
        // Don't: oR = textarea.createTextRange()
            oR = document.body.createTextRange();
        oR.moveToElementText(textarea);

        rangeData.text = oS.text;
        rangeData.bookmark = oS.getBookmark();

        // object.moveStart(sUnit [, iCount])
        // Return Value: Integer that returns the number of units moved.
        for (i = 0; oR.compareEndPoints('StartToStart', oS) < 0 && oS.moveStart("character", -1) !== 0; i++) {
            // Why? You can alert(textarea.value.length)
            if (textarea.value.charAt(i) == '\n') {
                i++;
            }
        }
        rangeData.start = i;
        rangeData.end = rangeData.text.length + rangeData.start;
    }

    return rangeData;
};

/**
 * @description textArea html 组件的鼠标焦点设置
 * @param textarea
 * @param rangeData
 */
GLOBAL.Interactive.setCursorPosition = function (textarea, rangeData) {
    "use strict";
    if (!rangeData) {
        GLOBAL.warning("You must get cursor position first.")
        return;
    }

    if (textarea.setSelectionRange) {
        // W3C
        textarea.focus();
        textarea.setSelectionRange(rangeData.start, rangeData.end);
    } else if (textarea.createTextRange) {
        // IE
        var oR = textarea.createTextRange();
        // Fixbug :
        // In IE, if cursor position at the end of textarea, the setCursorPosition function don't work
        if (textarea.value.length === rangeData.start) {
            oR.collapse(false)
            oR.select();
        } else {
            oR.moveToBookmark(rangeData.bookmark);
            oR.select();
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
    "use strict";
    return ostr ? ostr.replace(/^\s+|\s+$/g, "") : ostr;
};

/**
 * @description 是 Number 吗？
 * @param {*} s
 * @returns {Boolean}
 */
GLOBAL.Lang.isNumber = function (s, isStrict) {
    "use strict";
    var foo = GLOBAL.Lang;
    return  ( foo._isNumberPrototype(s) || (!isStrict && foo.isString(s)) )
        && (!G.isEmpty(s) && !isNaN(s));
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
    "use strict";
    return Object.prototype.toString.call(s) === '[object String]';
};

/**
 * @description 是 undefined 吗？
 * @param {*} s
 * @return {Boolean}
 */
GLOBAL.Lang.isUndefined = function (s) {
    "use strict";
    return Object.prototype.toString.call(s) === '[object Undefined]' || s === undefined;
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

//arguments 全部为空
GLOBAL.Lang.isAllEmpty = function () {
    if(!arguments){
        return true;
    }
    for(var i=0;i<arguments.length;i++){
        if(!G.Lang.isEmpty(arguments[i])){
            return false;
        }
    }
    return true;
};

/**
 * @description 是否为空, 空的定义：null, "null", undefined, "undefined", "", "   ", [], {}
 * @param {*} str
 * @returns {Boolean}
 */
GLOBAL.Lang.isNotEmpty = function (o, emptyExtended) {
     return !GLOBAL.Lang.isEmpty(o, emptyExtended);
};

/**
 * @description 是 Boolean 类型吗？
 * @param {*} s
 * @return {Boolean}
 */
GLOBAL.Lang.isBoolean = function (s) {
    "use strict";
    return Object.prototype.toString.call(s) === '[object Boolean]';
};

GLOBAL.Lang.isObject = function (s) {
    "use strict";
    return Object.prototype.toString.call(s) === '[object Object]';
};

/**
 * @description 判断字符串是否为 空字符
 * @param s
 */
GLOBAL.Lang.isStringEmpty = function (s) {
    "use strict";
    return GLOBAL.Lang.isString(s) && s === "";
};

/**
 * @description 判断字符串是否包含实际的值
 * @param s
 */
GLOBAL.Lang.isStringHasRealValue = function (s) {
    "use strict";
    var foo = GLOBAL.Lang;
    return !foo.isUndefined(s) && !foo.isNull(s) && !foo.isStringEmpty(s);
};

/**
 * @description 将字符串中的单引号与双引号加转义符
 * @param {String} s
 */
GLOBAL.Lang.addSlash = function (s) {
    "use strict";
    return s.replace(/\"/g, '\\"').replace(/\'/g, "\\'");
};

/**
 * @description 继承类
 * @param {Object} subClass
 * @param {Object} superClass
 */
GLOBAL.Lang.extend = function (subClass, superClass) {
    "use strict";
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

/**
 * @description 查找字符串是否以 目标字符串开始
 * @param sourceStr
 * @param str
 * @return {Boolean}
 */
GLOBAL.Lang.startWith = function (sourceStr, str) {
    "use strict";
    if(typeof str !== "string" || typeof sourceStr !== "string") {
        return false;
    }
    return sourceStr.indexOf(str) === 0;
};

/**
 * @description 查找字符串是否以 目标字符串结束
 * @param sourceStr
 * @param str
 * @return {Boolean}
 */
GLOBAL.Lang.endWith = function (sourceStr, str) {
    "use strict";
    if(typeof str !== "string" || typeof sourceStr !== "string") {
        return false;
    }

    var index = sourceStr.lastIndexOf(str);

    if(index === -1) {
        return false;
    } else {
        var orginalStr = sourceStr.substring(index);
        return orginalStr === str;
    }
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

//判断scope中是否包含o
GLOBAL.Lang.strContains = function (o, scope) {
    "use strict";
    if(typeof o !== "string" || typeof scope !== "string") {
        return false;
    }
    return scope.indexOf(o) >= 0;
};


// 尽量少扩展原有String 对象，为了避免冲突。
// String Class Extends
String.prototype.startWith = function (str) {
    "use strict";
    var foo = GLOBAL.Lang;
    return foo.startWith(this.toString(), str);
};

String.prototype.endWith = function (str) {
    "use strict";
    var foo = GLOBAL.Lang;
    return foo.endWith(this.toString(), str);
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
 * @description 遍历对象中的属性， 并且合并成字符串输出 , 主要用于 debug
 * @param obj
 * @return {String}
 */
GLOBAL.Lang.displayPro = function (obj) {
    "use strict";
    var names = "";
    for (var name in obj) {
        if (GLOBAL.Lang.isObject(obj[name])) {
            names += GLOBAL.Lang.displayPro(obj[name]);
        }
        else {
            names += name + ": " + obj[name] + ", ";
        }
    }
    return names;
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
 * 比较两个字符串是否相同，"",NULL,undefined 当相同处理
 * @param str1
 * @param str2
 * @returns {Boolean} 相同的时候return true
 */
GLOBAL.Lang.compareStrSame = function (str1, str2) {

    if (G.Lang.isEmpty(str1) && G.Lang.isEmpty(str2)) {
        return true;
    } else if (G.Lang.isEmpty(str1) && !G.Lang.isEmpty(str2) || !G.Lang.isEmpty(str1) && G.Lang.isEmpty(str2)) {
        return false;
    } else {
        return str1 === str2;
    }
};

/**
 ["a","b","c"],",","--"
 组装成a,b,c
 */
GLOBAL.Lang.generateString = function (array, splitStr, emptyExtended) {
    var resultStr = "";
    var foo = GLOBAL.Lang;
    if (foo.isNotEmpty(array)) {
        for (var i = 0; i < array.length; i++) {
            if (foo.isNotEmpty(array[i])) {
                if (splitStr && foo.isNotEmpty(resultStr)) {
                    resultStr += splitStr;
                }
                resultStr += array[i];
            }
        }
    }
    if (foo.isNotEmpty(emptyExtended) && foo.isEmpty(resultStr)) {
        resultStr += emptyExtended;
    }
    return resultStr;
};

// Number Package
GLOBAL.Number = {};

/**
 * @description 比较 number 是否在 min 和 max 之间,
 *              mode 代表区间定义： "[]" -- 闭闭， "()" -- 开开, "[)" -- 闭开, "(]" -- 开闭
 * @param number
 * @param min
 * @param max
 * @param mode
 * @return {*}
 */
GLOBAL.Number.between = function (number, min, max, mode) {
    "use strict";
    if (!GLOBAL.Lang.isNumber(number, true)
        || !GLOBAL.Lang.isNumber(min, true)
        || !GLOBAL.Lang.isNumber(max, true)
        || (mode && mode.search(/[\[\(][\]\)]/g) === -1)) {
        throw new Error("Parameter Error");
        return false;
    }

    // mode state : [], (), (], [)
    if (GLOBAL.Lang.isUndefined(mode)) mode = "[]";

    var compare = {
        greater: function (number, base, mode) {
            return mode === "[" ? (number >= base) : (number > base);
        },

        less: function (number, base, mode) {
            return mode === "]" ? (number <= base) : (number < base)
        }
    };
    return compare.greater(number, min, mode.charAt(0)) && compare.less(number, max, mode.charAt(1));
};

/**
 * @description 查看一个数 number 是否在 min 和 max 之间？ 如果在，返回原值；不在，返回min 或 max
 * @param number
 * @param min
 * @param max
 * @returns {Number}
 */
GLOBAL.Number.constrain = function (number, min, max) {
    "use strict";
    return Math.min(Math.max(number, min), max);
};
Number.prototype.constrain = function (min, max) {
    "use strict";
    return GLOBAL.Number.constrain(this, min, max);
};

GLOBAL.Number.equalsTo = function (numA, numB, decimals) {
    "use strict";
    var regNumber = /^[-+]?\d+$|^[-+]?\d*\.\d+$/g,
        lang = GLOBAL.Lang;
    if (lang.isNumber(numA) === false && !(lang.isString(numA) && numA.search(regNumber) === -1 )
        || lang.isNumber(numB) === false && !(lang.isString(numB) && numB.search(regNumber) === -1)) {
        throw new Error("arguments[0] or arguments[1] error! It must be a \"Number\"!");
    }

    var a = parseFloat(numA),
        b = parseFloat(numB),
        d = lang.isNumber(decimals) && decimals >= 0 ? decimals : 5; // default to 5
    return Math.abs(a - b) < Math.pow(0.1, d);
};

/**
 * @description 归一化数字， 如果输入是 null, undefined, "" 等非法输入都输出0， 其余功能同 parseFloat
 * @param a
 * @return {Number}
 */
GLOBAL.Number.normalize = function (a) {
    "use strict";
    return parseFloat("0" + a);
};

/**
 * 四舍五入保留len位小数，同时过滤num末尾的0
 * @param num
 * @param len 默认值为2
 * @returns {number}
 */
GLOBAL.Number.filterZero = function (num, len) {
    if (isNaN(num)) {
        return 0;
    } else {
        var target = new Number(num).toFixed(len == null ? 2 : len);
        var result = Number(target);
        var exec = /0*$/.exec(target);
        if (exec != null) {
            result = Number(target.substring(0, exec.index));
        }
        return result
    }
}

GLOBAL.namespace("GLOBAL.Math");

/**
 * @description 对数组求和， 必须保证数组的每个元素都是数字， 或者为字符数字
 */
GLOBAL.Math.summation = function () {
    "use strict";
    var retVal = 0,
        p = arguments[0],
        t = 0;
    if (arguments.length === 1 && GLOBAL.Lang.isArray(p)) {
        for (var i = 0, len = p.length; i < len; i++) {
            t = parseFloat(p[i]);

            if (GLOBAL.Lang.isNumber(t)) {
                retVal += t;
            } else {
                throw new Error("function arguments Error!");
                break;
            }
        }
        return retVal;
    } else {
        throw new Error("function arguments Error!");
    }
};


// 大数运算库
GLOBAL.namespace("GLOBAL.Big");


// Collection related
GLOBAL.Collection = {};
GLOBAL.Collection.Comparator = {
    equalTo: function (sourceArr, targetArr) {
        "use strict";
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
    "use strict";
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
    "use strict";
    node.className = node.className.replace(new RegExp("(^|\\s+)" + str), "");
};


// Util 相关
GLOBAL.Util = {};

/**
 * @description 生成 UUID(GUID)
 * @returns {String} UUID(GUID)
 */
GLOBAL.Util.generateUUID = (typeof(window.crypto) != 'undefined' &&
    typeof(window.crypto.getRandomValues) != 'undefined') ?
    function () {
        "use strict";
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
        "use strict";
        // 否则，我们使用 Math.random() 来生成伪随机数
        return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
            var r = Math.random() * 16 | 0, v = c == 'x' ? r : (r & 0x3 | 0x8);
            return v.toString(16);
        });
    };

/**
 * @description 得到URL中param值
 * @param {String} name
 */
GLOBAL.Util.getUrlParameter = function (name) {
    "use strict";
    return decodeURIComponent((new RegExp('[?|&]' + name + '=' + '([^&;]+?)(&|#|;|$)').exec(location.search) || [, ""])[1].replace(/\+/g, '%20')) || "";
};

/**
 * 动态加载Js/Css文件
 * @param fileName
 * @param fileType  可为 js 或 css
 */
GLOBAL.Util.loadJsCssFile = function (fileName, fileType) {
    "use strict";
    if (document.createStyleSheet) {
        document.createStyleSheet(fileName);
        return;
    }
    if (fileType == "js") {
        var fileref = document.createElement('script');
        fileref.setAttribute("type", "text/javascript");
        fileref.setAttribute("src", fileName);
    }
    else if (fileType == "css") {
        var fileref = document.createElement("link");
        fileref.setAttribute("rel", "stylesheet");
        fileref.setAttribute("type", "text/css");
        fileref.setAttribute("href", fileName);
    }
    if (typeof fileref != "undefined")
        document.getElementsByTagName("head")[0].appendChild(fileref);
};

/**
 * 动态移除Js或Css文件
 * @param fileName
 * @param fileType 可为 js 或 css
 */
GLOBAL.Util.removeJsCssFile = function (fileName, fileType) {
    "use strict";
    var targetelement = (fileType == "js") ? "script" : (fileType == "css") ? "link" : "none";
    var targetattr = (fileType == "js") ? "src" : (fileType == "css") ? "href" : "none";
    var allsuspects = document.getElementsByTagName(targetelement);
    for (var i = allsuspects.length; i >= 0; i--) {
        if (allsuspects[i] && allsuspects[i].getAttribute(targetattr) != null && allsuspects[i].getAttribute(targetattr).indexOf(fileName) != -1)
            allsuspects[i].parentNode.removeChild(allsuspects[i]);
    }
};

//Array.prototype.unique = function () {
//    var i = 0,
//        gid = '_' + (+new Date) + Math.random(),
//        objs = [],
//        hash = {
//            'string': {},
//            'boolean': {},
//            'number': {}
//        }, p, l = this.length,
//        ret = [];
//    for (; i < l; i++) {
//        p = this[i];
//        if (p == null) continue;
//        tp = typeof p;
//        if (tp in hash) {
//            if (!(p in hash[tp])) {
//                hash[tp][p] = 1;
//                ret.push(p);
//            }
//        } else {
//            if (p[gid]) continue;
//            p[gid] = 1;
//            objs.push(p);
//            ret.push(p);
//        }
//    }
//    for (i = 0, l = objs.length; i < l; i++) {
//        p = objs[i];
//        p[gid] = undefined;
//        delete p[gid];
//    }
//    return ret;
//};

///**
// * @description Array unique function,同时将去掉null及undefined
// * @param {Array} ary 需要进行unique的数组.
// * @returns {Array} 返回经过去重的新的数组，不会修改原来的数组内容.
// */
//GLOBAL.Util.unique = function (ary) {
//    return ary.unique();
//};

GLOBAL.namespace("Array");
GLOBAL.Array.indexOf = function (arr, item) {
    "use strict";
    for (var i = 0, len = arr.length; i < len; i++) {
        if (arr[i] === item) {
            return i;
        }
    }
    return -1;
};

GLOBAL.Array.lastIndexOf = function (arr, item) {
    "use strict";
    for (var len = arr.length, i = len - 1; i >= 0; i--) {
        if (arr[i] === item) {
            return i
        }
    }
    return -1;
};

/**
 * 通过 index 删除 数组某个元素
 * @param arr 必须是索引数组
 * @param index
 * @returns {Array}
 */
GLOBAL.Array.remove = function(arr, index) {
    "use strict";
    var retArr = [];
    for (var i = 0, len = arr.length; i < len; i++) {
        if (-1 === index) {
            break;
        }

        if (i === index) {
            continue;
        }

        retArr.push(arr[i]);
    }

    return retArr;
};


/*
 * @description 把字符串转换为日期对象, 精确到天
 *              注：IE下不支持直接实例化日期对象，如new Date("2011-04-06"), 这个方法为解决此问题而存在
 * @param {String} yyyy-mm-dd或dd/mm/yyyy或 yyyy-MM-dd HH:mm 形式的字符串
 * @return {Date}
 */
GLOBAL.Util.getDate = function (date) {
    "use strict";
    date = $.trim(date);
    var formatPattern = {
            "yyyy-mm-dd": /\d{4}-\d{1,2}-\d{1,2}$/g,
            "yyyy-mm-dd HH:mm": /\d{4}-\d{1,2}-\d{1,2}\s\d{1,2}:\d{1,2}$/g,
            "yyyy年mm月dd日 HH时mm分": /\d{4}年\d{1,2}月\d{1,2}日\s\d{1,2}时\d{1,2}分$/g,
            "dd/mm/yyyy": /\d{1,2}\/\d{1,2}\/\d{4}$/g
        },
        format = "",
        arr,
        newDate = new Date();

    for (var k in formatPattern) {
        if (formatPattern[k].test(date)) {
            format = k;
            break;
        }
    }

    // if now match , return null
    if (format === "")
        return null;

    if (format === "yyyy-mm-dd HH:mm") {
        date = date.split(/\s/)[0];
    }
    arr = date.split(/\/|-/g);
    if (format === "dd/mm/yyyy") {
        arr.reverse();
    }
    newDate.setFullYear(arr[0], arr[1] - 1, arr[2]);
    newDate.setHours(0, 0, 0);
    return newDate;
};

/*
 * @description 把字符串转换为日期对象, 精确到分
 * @param {String} yyyy-mm-dd或dd/mm/yyyy或 yyyy-MM-dd HH:mm 形式的字符串
 * @return {Date}
 */
GLOBAL.Util.getExactDate = function (date) {
    "use strict";
    date = $.trim(date);
    var formatPattern = {
            "yyyy-mm-dd": /\d{4}-\d{1,2}-\d{1,2}$/g,
            "yyyy-mm-dd HH:mm": /\d{4}-\d{1,2}-\d{1,2}\s\d{1,2}:\d{1,2}$/g,
            "dd/mm/yyyy": /\d{1,2}\/\d{1,2}\/\d{4}$/g
        },
        format = "",
        arr,
        newDate = new Date();

    for (var k in formatPattern) {
        if (formatPattern[k].test(date)) {
            format = k;
            break;
        }
    }

    // if now match , return null
    if (format === "")
        return null;

    var hour = 0, min = 0;
    if (format === "yyyy-mm-dd HH:mm") {
        var houmin = date.split(/\s/)[1];
        date = date.split(/\s/)[0];
        hour = houmin.split(/:/)[0];
        min = houmin.split(/:/)[1];
    }
    arr = date.split(/\/|-/g);
    if (format === "dd/mm/yyyy") {
        arr.reverse();
    }
    newDate.setFullYear(arr[0], arr[1] - 1, arr[2]);
    newDate.setHours(hour, min, 0);
    return newDate;
};


// Cookie 相关
GLOBAL.Cookie = {
    /**
     * @description 通过键值名读取 Cookie
     * @param {String} name
     * @returns {String}
     */
    read: function (name) {
        "use strict";
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
    set: function (foo, value, expires) {
        "use strict";
        var expDays = expires * 24 * 60 * 60 * 1000;
        var expDate = new Date();
        expDate.setTime(expDate.getTime() + expDays);
        var expString = expires ? "; expires=" + expDate.toGMTString() : "";
        document.cookie = foo + "=" + escape(value) + expString;
    },

    /**
     * @description 通过键值名删除 Cookie
     * @param {String} name
     */
    del: function (name) {
        "use strict";
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
    getX: function (element) {
        "use strict";
        var x = 0;
        while (element) {
            x = x + element.offsetLeft;
            element = element.offsetParent;
        }
        return x;
    },

    // get y-axis position (px)
    getY: function (element) {
        "use strict";
        var y = 0;
        while (element) {
            y = y + element.offsetTop;
            element = element.offsetParent;
        }
        return y;
    }
};

/**
 * @description 有时候我们需要判断当前页面是否处于iframe中，这个问题看起来简单，但其实有很多潜在的问题。下面我们就来看看几种常见的方法以及它们的问题。
 *              常见方法：
 *              方法一：
 *  if(top!=this){
 *      //在iframe中
 *  }
 *              问题：当top被重置（参见：top和parent的重置）时，则无法通过此方法来判断。
 *              方法二：
 *  if(self.frameElement.tagName=="IFRAME"){
 *      //在iframe中
 *  }
 *              问题：若当前页面与上层页面之间跨域时，则无法通过此方法来判断。
 *              方法三：
 *  var isInIframe = (window.location != window.parent.location) ? true : false;
 *              问题：当parent被重置（参见：top和parent的重置）时，则无法通过此方法来判断。
 *
 *  新方法从另外一个角度来检测：
 *      优点是解决了上面其它方法所不能解决的问题；
 *      缺点是过程中进行了DOM操作，因此不适用于高频度的调用。
 */
GLOBAL.Display.isInIframe = function () {
    "use strict";
    var tmp_iframe = document.body.appendChild(document.createElement("iframe")),
        result = window != tmp_iframe.contentWindow.top;
    document.body.removeChild(tmp_iframe);
    return result;
};

// Date 相关
GLOBAL.Date = {};

/**
 * 获取当前格式化后的时间字符串
 * @returns {String} 格式化后的时间
 */
GLOBAL.Date.getCurrentFormatDate = function () {
    "use strict";
    var date = new Date();
    var y = 0, m = 0, d = 0, formattedDate = "";
    // date init
    // compatible for IE and Firefox
    y = date.getFullYear();
    m = date.getMonth() + 1;
    d = date.getDate();

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
 * 获取当前格式化后的时间字符串, 精确到分钟
 * @param {long} 毫秒数- 可选, 不传则为当前客户端时间.
 * @returns {String} 格式化后的时间
 */
GLOBAL.Date.getCurrentFormatDateMin = function (timeInMilli) {
    "use strict";
    var date = new Date();
    if(timeInMilli){
        date.setTime(timeInMilli);
    }
    var y = 0, m = 0, d = 0, h = 0, min = 0, formattedDate = "";
    // date init
    // compatible for IE and Firefox
    y = date.getFullYear();
    m = date.getMonth() + 1;
    d = date.getDate();
    h = date.getHours();
    min = date.getMinutes();

    formattedDate += y + "-";
    if (m < 10) {
        formattedDate += "0";
    }
    formattedDate += m + "-";
    if (d < 10) {
        formattedDate += "0";
    }
    formattedDate += d;

    formattedDate += " ";
    if (h < 10) {
        formattedDate += "0";
    }
    formattedDate += h;
    formattedDate += ":";
    if (min < 10) {
        formattedDate += "0";
    }
    formattedDate += min;

    return formattedDate;
};

/**
 * TODO 全屏 api, 开发中接口, 请暂勿使用
 */
GLOBAL.namespace("FullScreen");
GLOBAL.FullScreen.enterFullScreen = function(ele) {
    "use strict";
    var funNameList = ["mozRequestFullScreen", "webkitRequestFullScreen", "requestFullscreen"];
    for(var i = 0, len = funNameList.length; i < len; i++) {
        if( ele[funNameList[i]] ) {
            ele[funNameList[i]]();
            break;
        }  
    }
};

GLOBAL.FullScreen.exitFullScreen = function() {
    "use strict";
    var funNameList = ["mozCancelFullScreen", "webkitCancelFullScreen", "cancelFullscreen"];
    for(var i = 0, len = funNameList.length; i < len; i++) {
        if( document[funNameList[i]] ) {
            document[funNameList[i]]();
            break;
        }
    }
};

GLOBAL.FullScreen.isFullScreen = function() {
    "use strict";
    var propList = ["mozFullScreen", "webkitIsFullScreen", "fullScreen"];
    for(var i = 0, len = propList.length; i < len; i++) {
        if(document[propList[i]] !== undefined) {
            return document[propList[i]];
        }
    }
};

/**
 * @description 此方法为私有方法，不对外提供，打印日志基础函数
 * @param info
 * @private
 */
GLOBAL._log = function (info) {
    "use strict";
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
    "use strict";
    if (!GLOBAL.isDebug)
        return;
    GLOBAL._log("== INFO ==");
    GLOBAL._log(info);
};

/**
 * @description debug 级别 log 打印，此log
 * @param info
 */
GLOBAL.debug = function (info) {
    "use strict";
    if (!GLOBAL.isDebug)
        return;

    GLOBAL._log("== DEBUG ==");
    GLOBAL._log(info);
};

/**
 * @description warning 级别 log 打印
 * @param info
 */
GLOBAL.warning = function (info) {
    "use strict";
    GLOBAL._log("== WARNING ==");
    GLOBAL._log(info);
};

/**
 * @description error 级别 log 打印
 * @param info
 */
GLOBAL.error = function (info) {
    "use strict";
    GLOBAL._log("== ERROR ==");
    GLOBAL._log(info);
};

/**
 * Browser checker
 */
GLOBAL.namespace("Browser");
(function(){
    "use strict";

    // brower check
    var userAgent = navigator.userAgent.toLocaleLowerCase(),
        isIOS = (userAgent.search("chrome") == -1 && userAgent.search("safari") != -1),
        isAndroid = userAgent.search("android") != -1,
        isWphone = userAgent.search("windows phone os") != -1;

    if(isAndroid) {
        var oldVersionBase = "300",
            androidVersion = userAgent.match(/android.*?;/g)[0].replace(/[a-z\.\s;]/g, ""),
            isOldAndroid = parseInt(androidVersion) < parseInt(oldVersionBase);
    }

    GLOBAL.Browser.isIOS = isIOS;
    GLOBAL.Browser.isAndroid = isAndroid;
    GLOBAL.Browser.isOldAndroid = isOldAndroid;
    GLOBAL.Browser.isWphone = isWphone;
})();

/**
 * 原生扩展
 * */
Function.prototype.method = function (name, func) {
    "use strict";
    if (!this.prototype[name]) {
        this.prototype[name] = func;
    }
    return this;
};

/**
 * extends the WebStorage , WebStorage is a class
 */
GLOBAL.namespace("WebStorage");
;
(function () {
    "use strict";

    var w = window,
        isSupport = function () {
            return !!localStorage;
        };

    var WebStorage = function (name) {
        var errorMsg = {
            "notSupport":"not support webStorage",
            "paramError":"parameters error, only support undefined|\"localStorage\"|\"messageStorage\""
        };

        if(!isSupport()) {
            G.error(errorMsg.notSupport);
            throw new Error(errorMsg.notSupport);
            return;
        }

        if(!G.isEmpty(name) && !G.contains(name, ["localStorage", "sessionStorage"])) {
            G.error(errorMsg.paramError);
            throw new Error(errorMsg.paramError);
            return;
        }

        this._name = name || "localStorage";
    };

    WebStorage.method("name", function () {
        return this._name;
    });

    WebStorage.method("set", function (key, value) {
        w[this.name()].setItem(key, value);
        return this;
    });

    WebStorage.method("get", function (key) {
        return w[this.name()].getItem(key);
    });

    WebStorage.method("has", function (key) {
        return !G.isEmpty(w[this.name()].getItem(key));
    });

    WebStorage.method("equal", function (key, targetValue) {
        if (!this.has(key)) return false;

        return w[this.name()].getItem(key) === targetValue;
    });

    WebStorage.method("remove", function(key) {
        w[this.name()].removeItem(key);
        return this;
    });

    WebStorage.method("clear", function() {
        w[this.name()].clear();
        return this;
    });

    GLOBAL.WebStorage = WebStorage;
}());

var G = GLOBAL;
GLOBAL.get = GLOBAL.Dom.get;
GLOBAL.trim = GLOBAL.Lang.trim;
GLOBAL.betweenNum = GLOBAL.Number.between;
GLOBAL.constrainNum = GLOBAL.Number.constrain;
GLOBAL.isNumber = GLOBAL.Lang.isNumber;
GLOBAL.isArray = GLOBAL.Lang.isArray;
GLOBAL.isBoolean = GLOBAL.Lang.isBoolean;
GLOBAL.isString = GLOBAL.Lang.isString;
GLOBAL.isObject = GLOBAL.Lang.isObject;
GLOBAL.isEmpty = GLOBAL.Lang.isEmpty;
GLOBAL.isNotEmpty = GLOBAL.Lang.isNotEmpty;
GLOBAL.isNull = GLOBAL.Lang.isNull;
GLOBAL.isUndefined = GLOBAL.Lang.isUndefined;
GLOBAL.normalize = GLOBAL.Lang.normalize;
GLOBAL.contains = GLOBAL.Lang.contains;
GLOBAL.getCurrentFormatDate = GLOBAL.Date.getCurrentFormatDate;
GLOBAL.getCurrentFormatDateMin = GLOBAL.Date.getCurrentFormatDateMin;
GLOBAL.getDate = GLOBAL.Util.getDate;
GLOBAL.isInIframe = GLOBAL.Display.isInIframe;
GLOBAL.getX = GLOBAL.Display.getX;
GLOBAL.getY = GLOBAL.Display.getY;
GLOBAL.generateUUID = GLOBAL.Util.generateUUID;
GLOBAL.isKeyCode = GLOBAL.Interactive.isKeyCode;
GLOBAL.isKeyName = GLOBAL.Interactive.isKeyName;
GLOBAL.keyCodeFromEvent = GLOBAL.Interactive.keyCodeFromEvent;
GLOBAL.keyNameFromEvent = GLOBAL.Interactive.keyNameFromEvent;
GLOBAL.keyNameFromKeyCode = GLOBAL.Interactive.keyNameFromKeyCode;
GLOBAL.getEvent = GLOBAL.Event.getEvent;
GLOBAL.getEventTarget = GLOBAL.Event.getEventTarget;
GLOBAL.stopPropagation = GLOBAL.Event.stopPropagation;
GLOBAL.localStorage = new GLOBAL.WebStorage("localStorage");
GLOBAL.sessionStorage = new GLOBAL.WebStorage("sessionStorage");



// Add  Windjs

/*Copyright 2012 (c) Jeffrey Zhao jeffz@live.com
 Based on UglifyJS (https://github.com/mishoo/UglifyJS).

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:

 * Redistributions of source code must retain the above
 copyright notice, this list of conditions and the following
 disclaimer.

 * Redistributions in binary form must reproduce the above
 copyright notice, this list of conditions and the following
 disclaimer in the documentation and/or other materials
 provided with the distribution.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDER “AS IS” AND ANY
 EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER BE
 LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY,
 OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
 TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF
 THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 SUCH DAMAGE.*/

/***********************************************************************
 wind-core-0.7.0.js
 ***********************************************************************/
;(function(){var Wind;var _=(function(){var isArray=function(obj){return Object.prototype.toString.call(obj)==="[object Array]"};var each=function(obj,action){if(isArray(obj)){for(var i=0,len=obj.length;i<len;i++){var value=action.length===1?action(obj[i]):action(i,obj[i]);if(value!==undefined){return value}}}else{for(var key in obj){if(obj.hasOwnProperty(key)){var value=action.length===1?action(obj[key]):action(key,obj[key]);if(value!==undefined){return value}}}}};var isEmpty=function(obj){if(isArray(obj)){return obj.length===0}return !(_.each(obj,function(v){return true}))};var map=function(obj,mapper,valueMapper){if(isArray(obj)){var array=new Array(obj.length);for(var i=0,len=obj.length;i<len;i++){array[i]=mapper(obj[i])}return array}else{var keyMapper=valueMapper?mapper:null;valueMapper=valueMapper||mapper;var newObj={};for(var key in obj){if(obj.hasOwnProperty(key)){var value=obj[key];var newKey=keyMapper?keyMapper(key):key;var newValue=valueMapper?valueMapper(value):value;newObj[newKey]=newValue}}return newObj}};var clone=function(obj){return map(obj)};var v2n=function(version){var value=0;var parts=version.split(".");for(var i=0;i<3;i++){value*=100;if(i<parts.length){value+=parseInt(parts[i],10)}}return value};var testVersion=function(expected,version){var expectedMinVersion=expected.substring(1);var expectedMaxParts=expectedMinVersion.split(".");expectedMaxParts[expectedMaxParts.length-1]="0";expectedMaxParts[expectedMaxParts.length-2]=(parseInt(expectedMaxParts[expectedMaxParts.length-2],10)+1).toString();var expectedMaxVersion=expectedMaxParts.join(".");var versionNumber=v2n(version);return v2n(expectedMinVersion)<=versionNumber&&versionNumber<v2n(expectedMaxVersion)};var format=function(f,args){if(!isArray(args)){var newArgs=new Array(arguments.length-1);for(var i=1;i<arguments.length;i++){newArgs[i-1]=arguments[i]}return format(f,newArgs)}return f.replace(/\{{1,2}\d+\}{1,2}/g,function(ph){if(ph.indexOf("{{")==0&&ph.indexOf("}}")==ph.length-2){return ph.substring(1,ph.length-1)}var left=0;while(ph[left]=="{"){left++}var right=ph.length-1;while(ph[right]=="}"){right--}var index=parseInt(ph.substring(left,right+1),10);return ph.replace("{"+index+"}",args[index])})};var once=function(fn){var called=false;return function(){if(called){return}fn.apply(this,arguments);called=true}};return{isArray:isArray,each:each,isEmpty:isEmpty,map:map,clone:clone,v2n:v2n,testVersion:testVersion,format:format,once:once}})();var Level={ALL:0,TRACE:1,DEBUG:2,INFO:3,WARN:4,ERROR:5,OFF:100};var Logger=function(){this.level=Level.ERROR};Logger.prototype={log:function(level,msg){if(this.level<=level){try{console.log(msg)}catch(ex){}}},trace:function(msg){this.log(Level.TRACE,msg)},debug:function(msg){this.log(Level.DEBUG,msg)},info:function(msg){this.log(Level.INFO,msg)},warn:function(msg){this.log(Level.WARN,msg)},error:function(msg){this.log(Level.ERROR,msg)}};var exportBasicOptions=function(exports,options){exports.name=options.name;exports.version=options.version;if(options.autoloads){exports.autoloads=options.autoloads}if(options.dependencies){exports.dependencies=options.dependencies}};var initModule=function(options){var existingModule=Wind.modules[options.name];if(existingModule&&existingModule.version!=options.version){Wind.logger.warn(_.format('The module "{0}" with version "{1}" has already been initialized, skip version "{2}".',options.name,existingModule.version,options.version))}checkDependencies(options);options.init();var module={};exportBasicOptions(module,options);Wind.modules[options.name]=module};var checkDependencies=function(options){_.each(options.dependencies||[],function(name,expectedVersion){var module=Wind.modules[name];if(!module){throw new Error(_.format('Missing required module: "{0}" (expected version: "{1}").',name,expectedVersion))}if(!_.testVersion(expectedVersion,module.version)){throw new Error(_.format('Version of module "{0}" mismatched, expected: "{1}", actual: "{2}".',name,expectedVersion,module.version))}})};var isCommonJS=!!(typeof require==="function"&&typeof module!=="undefined"&&module.exports);var isAmd=!!(typeof require==="function"&&typeof define==="function"&&define.amd);var defineModule=function(options){var autoloads=options.autoloads||[];if(isCommonJS){var require=options.require;_.each(autoloads,function(name){try{require("./wind-"+name)}catch(ex){require("wind-"+name)}});initModule(options)}else{if(isAmd){var dependencies=_.map(autoloads,function(name){return"wind-"+name});define("wind-"+options.name,dependencies,function(){if(options.onerror){try{initModule(options)}catch(ex){options.onerror(ex)}}else{initModule(options)}})}else{initModule(options)}}};var init=function(){Wind.logger=new Logger();Wind.Logging={Logger:Logger,Level:Level};Wind._=_;Wind.modules={core:{name:"core",version:"0.7.0"}};Wind.binders={};Wind.builders={};Wind.define=defineModule};if(isCommonJS){Wind=module.exports;init()}else{if(isAmd){define("wind-core",function(){Wind={};init();return Wind})}else{var Fn=Function,global=Fn("return this")();if(global.Wind){throw new Error("There's already a Wind root here, please load the component only once.")}Wind=global.Wind={};init()}}})();(function(){var parse=(function(){var KEYWORDS=array_to_hash(["break","case","catch","const","continue","default","delete","do","else","finally","for","function","if","in","instanceof","new","return","switch","throw","try","typeof","var","void","while","with"]);var RESERVED_WORDS=array_to_hash(["abstract","boolean","byte","char","class","debugger","double","enum","export","extends","final","float","goto","implements","import","int","interface","long","native","package","private","protected","public","short","static","super","synchronized","throws","transient","volatile"]);var KEYWORDS_BEFORE_EXPRESSION=array_to_hash(["return","new","delete","throw","else","case"]);var KEYWORDS_ATOM=array_to_hash(["false","null","true","undefined"]);var OPERATOR_CHARS=array_to_hash(characters("+-*&%=<>!?|~^"));var RE_HEX_NUMBER=/^0x[0-9a-f]+$/i;var RE_OCT_NUMBER=/^0[0-7]+$/;var RE_DEC_NUMBER=/^\d*\.?\d*(?:e[+-]?\d*(?:\d\.?|\.?\d)\d*)?$/i;var OPERATORS=array_to_hash(["in","instanceof","typeof","new","void","delete","++","--","+","-","!","~","&","|","^","*","/","%",">>","<<",">>>","<",">","<=",">=","==","===","!=","!==","?","=","+=","-=","/=","*=","%=",">>=","<<=",">>>=","|=","^=","&=","&&","||"]);var WHITESPACE_CHARS=array_to_hash(characters(" \n\r\t\u200b"));var PUNC_BEFORE_EXPRESSION=array_to_hash(characters("[{}(,.;:"));var PUNC_CHARS=array_to_hash(characters("[]{}(),;:"));var REGEXP_MODIFIERS=array_to_hash(characters("gmsiy"));var UNICODE={letter:new RegExp("[\\u0041-\\u005A\\u0061-\\u007A\\u00AA\\u00B5\\u00BA\\u00C0-\\u00D6\\u00D8-\\u00F6\\u00F8-\\u02C1\\u02C6-\\u02D1\\u02E0-\\u02E4\\u02EC\\u02EE\\u0370-\\u0374\\u0376\\u0377\\u037A-\\u037D\\u0386\\u0388-\\u038A\\u038C\\u038E-\\u03A1\\u03A3-\\u03F5\\u03F7-\\u0481\\u048A-\\u0523\\u0531-\\u0556\\u0559\\u0561-\\u0587\\u05D0-\\u05EA\\u05F0-\\u05F2\\u0621-\\u064A\\u066E\\u066F\\u0671-\\u06D3\\u06D5\\u06E5\\u06E6\\u06EE\\u06EF\\u06FA-\\u06FC\\u06FF\\u0710\\u0712-\\u072F\\u074D-\\u07A5\\u07B1\\u07CA-\\u07EA\\u07F4\\u07F5\\u07FA\\u0904-\\u0939\\u093D\\u0950\\u0958-\\u0961\\u0971\\u0972\\u097B-\\u097F\\u0985-\\u098C\\u098F\\u0990\\u0993-\\u09A8\\u09AA-\\u09B0\\u09B2\\u09B6-\\u09B9\\u09BD\\u09CE\\u09DC\\u09DD\\u09DF-\\u09E1\\u09F0\\u09F1\\u0A05-\\u0A0A\\u0A0F\\u0A10\\u0A13-\\u0A28\\u0A2A-\\u0A30\\u0A32\\u0A33\\u0A35\\u0A36\\u0A38\\u0A39\\u0A59-\\u0A5C\\u0A5E\\u0A72-\\u0A74\\u0A85-\\u0A8D\\u0A8F-\\u0A91\\u0A93-\\u0AA8\\u0AAA-\\u0AB0\\u0AB2\\u0AB3\\u0AB5-\\u0AB9\\u0ABD\\u0AD0\\u0AE0\\u0AE1\\u0B05-\\u0B0C\\u0B0F\\u0B10\\u0B13-\\u0B28\\u0B2A-\\u0B30\\u0B32\\u0B33\\u0B35-\\u0B39\\u0B3D\\u0B5C\\u0B5D\\u0B5F-\\u0B61\\u0B71\\u0B83\\u0B85-\\u0B8A\\u0B8E-\\u0B90\\u0B92-\\u0B95\\u0B99\\u0B9A\\u0B9C\\u0B9E\\u0B9F\\u0BA3\\u0BA4\\u0BA8-\\u0BAA\\u0BAE-\\u0BB9\\u0BD0\\u0C05-\\u0C0C\\u0C0E-\\u0C10\\u0C12-\\u0C28\\u0C2A-\\u0C33\\u0C35-\\u0C39\\u0C3D\\u0C58\\u0C59\\u0C60\\u0C61\\u0C85-\\u0C8C\\u0C8E-\\u0C90\\u0C92-\\u0CA8\\u0CAA-\\u0CB3\\u0CB5-\\u0CB9\\u0CBD\\u0CDE\\u0CE0\\u0CE1\\u0D05-\\u0D0C\\u0D0E-\\u0D10\\u0D12-\\u0D28\\u0D2A-\\u0D39\\u0D3D\\u0D60\\u0D61\\u0D7A-\\u0D7F\\u0D85-\\u0D96\\u0D9A-\\u0DB1\\u0DB3-\\u0DBB\\u0DBD\\u0DC0-\\u0DC6\\u0E01-\\u0E30\\u0E32\\u0E33\\u0E40-\\u0E46\\u0E81\\u0E82\\u0E84\\u0E87\\u0E88\\u0E8A\\u0E8D\\u0E94-\\u0E97\\u0E99-\\u0E9F\\u0EA1-\\u0EA3\\u0EA5\\u0EA7\\u0EAA\\u0EAB\\u0EAD-\\u0EB0\\u0EB2\\u0EB3\\u0EBD\\u0EC0-\\u0EC4\\u0EC6\\u0EDC\\u0EDD\\u0F00\\u0F40-\\u0F47\\u0F49-\\u0F6C\\u0F88-\\u0F8B\\u1000-\\u102A\\u103F\\u1050-\\u1055\\u105A-\\u105D\\u1061\\u1065\\u1066\\u106E-\\u1070\\u1075-\\u1081\\u108E\\u10A0-\\u10C5\\u10D0-\\u10FA\\u10FC\\u1100-\\u1159\\u115F-\\u11A2\\u11A8-\\u11F9\\u1200-\\u1248\\u124A-\\u124D\\u1250-\\u1256\\u1258\\u125A-\\u125D\\u1260-\\u1288\\u128A-\\u128D\\u1290-\\u12B0\\u12B2-\\u12B5\\u12B8-\\u12BE\\u12C0\\u12C2-\\u12C5\\u12C8-\\u12D6\\u12D8-\\u1310\\u1312-\\u1315\\u1318-\\u135A\\u1380-\\u138F\\u13A0-\\u13F4\\u1401-\\u166C\\u166F-\\u1676\\u1681-\\u169A\\u16A0-\\u16EA\\u1700-\\u170C\\u170E-\\u1711\\u1720-\\u1731\\u1740-\\u1751\\u1760-\\u176C\\u176E-\\u1770\\u1780-\\u17B3\\u17D7\\u17DC\\u1820-\\u1877\\u1880-\\u18A8\\u18AA\\u1900-\\u191C\\u1950-\\u196D\\u1970-\\u1974\\u1980-\\u19A9\\u19C1-\\u19C7\\u1A00-\\u1A16\\u1B05-\\u1B33\\u1B45-\\u1B4B\\u1B83-\\u1BA0\\u1BAE\\u1BAF\\u1C00-\\u1C23\\u1C4D-\\u1C4F\\u1C5A-\\u1C7D\\u1D00-\\u1DBF\\u1E00-\\u1F15\\u1F18-\\u1F1D\\u1F20-\\u1F45\\u1F48-\\u1F4D\\u1F50-\\u1F57\\u1F59\\u1F5B\\u1F5D\\u1F5F-\\u1F7D\\u1F80-\\u1FB4\\u1FB6-\\u1FBC\\u1FBE\\u1FC2-\\u1FC4\\u1FC6-\\u1FCC\\u1FD0-\\u1FD3\\u1FD6-\\u1FDB\\u1FE0-\\u1FEC\\u1FF2-\\u1FF4\\u1FF6-\\u1FFC\\u2071\\u207F\\u2090-\\u2094\\u2102\\u2107\\u210A-\\u2113\\u2115\\u2119-\\u211D\\u2124\\u2126\\u2128\\u212A-\\u212D\\u212F-\\u2139\\u213C-\\u213F\\u2145-\\u2149\\u214E\\u2183\\u2184\\u2C00-\\u2C2E\\u2C30-\\u2C5E\\u2C60-\\u2C6F\\u2C71-\\u2C7D\\u2C80-\\u2CE4\\u2D00-\\u2D25\\u2D30-\\u2D65\\u2D6F\\u2D80-\\u2D96\\u2DA0-\\u2DA6\\u2DA8-\\u2DAE\\u2DB0-\\u2DB6\\u2DB8-\\u2DBE\\u2DC0-\\u2DC6\\u2DC8-\\u2DCE\\u2DD0-\\u2DD6\\u2DD8-\\u2DDE\\u2E2F\\u3005\\u3006\\u3031-\\u3035\\u303B\\u303C\\u3041-\\u3096\\u309D-\\u309F\\u30A1-\\u30FA\\u30FC-\\u30FF\\u3105-\\u312D\\u3131-\\u318E\\u31A0-\\u31B7\\u31F0-\\u31FF\\u3400\\u4DB5\\u4E00\\u9FC3\\uA000-\\uA48C\\uA500-\\uA60C\\uA610-\\uA61F\\uA62A\\uA62B\\uA640-\\uA65F\\uA662-\\uA66E\\uA67F-\\uA697\\uA717-\\uA71F\\uA722-\\uA788\\uA78B\\uA78C\\uA7FB-\\uA801\\uA803-\\uA805\\uA807-\\uA80A\\uA80C-\\uA822\\uA840-\\uA873\\uA882-\\uA8B3\\uA90A-\\uA925\\uA930-\\uA946\\uAA00-\\uAA28\\uAA40-\\uAA42\\uAA44-\\uAA4B\\uAC00\\uD7A3\\uF900-\\uFA2D\\uFA30-\\uFA6A\\uFA70-\\uFAD9\\uFB00-\\uFB06\\uFB13-\\uFB17\\uFB1D\\uFB1F-\\uFB28\\uFB2A-\\uFB36\\uFB38-\\uFB3C\\uFB3E\\uFB40\\uFB41\\uFB43\\uFB44\\uFB46-\\uFBB1\\uFBD3-\\uFD3D\\uFD50-\\uFD8F\\uFD92-\\uFDC7\\uFDF0-\\uFDFB\\uFE70-\\uFE74\\uFE76-\\uFEFC\\uFF21-\\uFF3A\\uFF41-\\uFF5A\\uFF66-\\uFFBE\\uFFC2-\\uFFC7\\uFFCA-\\uFFCF\\uFFD2-\\uFFD7\\uFFDA-\\uFFDC]"),non_spacing_mark:new RegExp("[\\u0300-\\u036F\\u0483-\\u0487\\u0591-\\u05BD\\u05BF\\u05C1\\u05C2\\u05C4\\u05C5\\u05C7\\u0610-\\u061A\\u064B-\\u065E\\u0670\\u06D6-\\u06DC\\u06DF-\\u06E4\\u06E7\\u06E8\\u06EA-\\u06ED\\u0711\\u0730-\\u074A\\u07A6-\\u07B0\\u07EB-\\u07F3\\u0816-\\u0819\\u081B-\\u0823\\u0825-\\u0827\\u0829-\\u082D\\u0900-\\u0902\\u093C\\u0941-\\u0948\\u094D\\u0951-\\u0955\\u0962\\u0963\\u0981\\u09BC\\u09C1-\\u09C4\\u09CD\\u09E2\\u09E3\\u0A01\\u0A02\\u0A3C\\u0A41\\u0A42\\u0A47\\u0A48\\u0A4B-\\u0A4D\\u0A51\\u0A70\\u0A71\\u0A75\\u0A81\\u0A82\\u0ABC\\u0AC1-\\u0AC5\\u0AC7\\u0AC8\\u0ACD\\u0AE2\\u0AE3\\u0B01\\u0B3C\\u0B3F\\u0B41-\\u0B44\\u0B4D\\u0B56\\u0B62\\u0B63\\u0B82\\u0BC0\\u0BCD\\u0C3E-\\u0C40\\u0C46-\\u0C48\\u0C4A-\\u0C4D\\u0C55\\u0C56\\u0C62\\u0C63\\u0CBC\\u0CBF\\u0CC6\\u0CCC\\u0CCD\\u0CE2\\u0CE3\\u0D41-\\u0D44\\u0D4D\\u0D62\\u0D63\\u0DCA\\u0DD2-\\u0DD4\\u0DD6\\u0E31\\u0E34-\\u0E3A\\u0E47-\\u0E4E\\u0EB1\\u0EB4-\\u0EB9\\u0EBB\\u0EBC\\u0EC8-\\u0ECD\\u0F18\\u0F19\\u0F35\\u0F37\\u0F39\\u0F71-\\u0F7E\\u0F80-\\u0F84\\u0F86\\u0F87\\u0F90-\\u0F97\\u0F99-\\u0FBC\\u0FC6\\u102D-\\u1030\\u1032-\\u1037\\u1039\\u103A\\u103D\\u103E\\u1058\\u1059\\u105E-\\u1060\\u1071-\\u1074\\u1082\\u1085\\u1086\\u108D\\u109D\\u135F\\u1712-\\u1714\\u1732-\\u1734\\u1752\\u1753\\u1772\\u1773\\u17B7-\\u17BD\\u17C6\\u17C9-\\u17D3\\u17DD\\u180B-\\u180D\\u18A9\\u1920-\\u1922\\u1927\\u1928\\u1932\\u1939-\\u193B\\u1A17\\u1A18\\u1A56\\u1A58-\\u1A5E\\u1A60\\u1A62\\u1A65-\\u1A6C\\u1A73-\\u1A7C\\u1A7F\\u1B00-\\u1B03\\u1B34\\u1B36-\\u1B3A\\u1B3C\\u1B42\\u1B6B-\\u1B73\\u1B80\\u1B81\\u1BA2-\\u1BA5\\u1BA8\\u1BA9\\u1C2C-\\u1C33\\u1C36\\u1C37\\u1CD0-\\u1CD2\\u1CD4-\\u1CE0\\u1CE2-\\u1CE8\\u1CED\\u1DC0-\\u1DE6\\u1DFD-\\u1DFF\\u20D0-\\u20DC\\u20E1\\u20E5-\\u20F0\\u2CEF-\\u2CF1\\u2DE0-\\u2DFF\\u302A-\\u302F\\u3099\\u309A\\uA66F\\uA67C\\uA67D\\uA6F0\\uA6F1\\uA802\\uA806\\uA80B\\uA825\\uA826\\uA8C4\\uA8E0-\\uA8F1\\uA926-\\uA92D\\uA947-\\uA951\\uA980-\\uA982\\uA9B3\\uA9B6-\\uA9B9\\uA9BC\\uAA29-\\uAA2E\\uAA31\\uAA32\\uAA35\\uAA36\\uAA43\\uAA4C\\uAAB0\\uAAB2-\\uAAB4\\uAAB7\\uAAB8\\uAABE\\uAABF\\uAAC1\\uABE5\\uABE8\\uABED\\uFB1E\\uFE00-\\uFE0F\\uFE20-\\uFE26]"),space_combining_mark:new RegExp("[\\u0903\\u093E-\\u0940\\u0949-\\u094C\\u094E\\u0982\\u0983\\u09BE-\\u09C0\\u09C7\\u09C8\\u09CB\\u09CC\\u09D7\\u0A03\\u0A3E-\\u0A40\\u0A83\\u0ABE-\\u0AC0\\u0AC9\\u0ACB\\u0ACC\\u0B02\\u0B03\\u0B3E\\u0B40\\u0B47\\u0B48\\u0B4B\\u0B4C\\u0B57\\u0BBE\\u0BBF\\u0BC1\\u0BC2\\u0BC6-\\u0BC8\\u0BCA-\\u0BCC\\u0BD7\\u0C01-\\u0C03\\u0C41-\\u0C44\\u0C82\\u0C83\\u0CBE\\u0CC0-\\u0CC4\\u0CC7\\u0CC8\\u0CCA\\u0CCB\\u0CD5\\u0CD6\\u0D02\\u0D03\\u0D3E-\\u0D40\\u0D46-\\u0D48\\u0D4A-\\u0D4C\\u0D57\\u0D82\\u0D83\\u0DCF-\\u0DD1\\u0DD8-\\u0DDF\\u0DF2\\u0DF3\\u0F3E\\u0F3F\\u0F7F\\u102B\\u102C\\u1031\\u1038\\u103B\\u103C\\u1056\\u1057\\u1062-\\u1064\\u1067-\\u106D\\u1083\\u1084\\u1087-\\u108C\\u108F\\u109A-\\u109C\\u17B6\\u17BE-\\u17C5\\u17C7\\u17C8\\u1923-\\u1926\\u1929-\\u192B\\u1930\\u1931\\u1933-\\u1938\\u19B0-\\u19C0\\u19C8\\u19C9\\u1A19-\\u1A1B\\u1A55\\u1A57\\u1A61\\u1A63\\u1A64\\u1A6D-\\u1A72\\u1B04\\u1B35\\u1B3B\\u1B3D-\\u1B41\\u1B43\\u1B44\\u1B82\\u1BA1\\u1BA6\\u1BA7\\u1BAA\\u1C24-\\u1C2B\\u1C34\\u1C35\\u1CE1\\u1CF2\\uA823\\uA824\\uA827\\uA880\\uA881\\uA8B4-\\uA8C3\\uA952\\uA953\\uA983\\uA9B4\\uA9B5\\uA9BA\\uA9BB\\uA9BD-\\uA9C0\\uAA2F\\uAA30\\uAA33\\uAA34\\uAA4D\\uAA7B\\uABE3\\uABE4\\uABE6\\uABE7\\uABE9\\uABEA\\uABEC]"),connector_punctuation:new RegExp("[\\u005F\\u203F\\u2040\\u2054\\uFE33\\uFE34\\uFE4D-\\uFE4F\\uFF3F]")};function is_letter(ch){return UNICODE.letter.test(ch)}function is_digit(ch){ch=ch.charCodeAt(0);return ch>=48&&ch<=57}function is_alphanumeric_char(ch){return is_digit(ch)||is_letter(ch)}function is_unicode_combining_mark(ch){return UNICODE.non_spacing_mark.test(ch)||UNICODE.space_combining_mark.test(ch)}function is_unicode_connector_punctuation(ch){return UNICODE.connector_punctuation.test(ch)}function is_identifier_start(ch){return ch=="$"||ch=="_"||is_letter(ch)}function is_identifier_char(ch){return is_identifier_start(ch)||is_unicode_combining_mark(ch)||is_digit(ch)||is_unicode_connector_punctuation(ch)||ch=="\u200c"||ch=="\u200d"}function parse_js_number(num){if(RE_HEX_NUMBER.test(num)){return parseInt(num.substr(2),16)}else{if(RE_OCT_NUMBER.test(num)){return parseInt(num.substr(1),8)}else{if(RE_DEC_NUMBER.test(num)){return parseFloat(num)}}}}function JS_Parse_Error(message,line,col,pos){this.message=message;this.line=line;this.col=col;this.pos=pos;try{({})()}catch(ex){this.stack=ex.stack}}JS_Parse_Error.prototype.toString=function(){return this.message+" (line: "+this.line+", col: "+this.col+", pos: "+this.pos+")\n\n"+this.stack};function js_error(message,line,col,pos){throw new JS_Parse_Error(message,line,col,pos)}function is_token(token,type,val){return token.type==type&&(val==null||token.value==val)}var EX_EOF={};function tokenizer($TEXT){var S={text:$TEXT.replace(/\r\n?|[\n\u2028\u2029]/g,"\n").replace(/^\uFEFF/,""),pos:0,tokpos:0,line:0,tokline:0,col:0,tokcol:0,newline_before:false,regex_allowed:false,comments_before:[]};function peek(){return S.text.charAt(S.pos)}function next(signal_eof){var ch=S.text.charAt(S.pos++);if(signal_eof&&!ch){throw EX_EOF}if(ch=="\n"){S.newline_before=true;++S.line;S.col=0}else{++S.col}return ch}function eof(){return !S.peek()}function find(what,signal_eof){var pos=S.text.indexOf(what,S.pos);if(signal_eof&&pos==-1){throw EX_EOF}return pos}function start_token(){S.tokline=S.line;S.tokcol=S.col;S.tokpos=S.pos}function token(type,value,is_comment){S.regex_allowed=((type=="operator"&&!HOP(UNARY_POSTFIX,value))||(type=="keyword"&&HOP(KEYWORDS_BEFORE_EXPRESSION,value))||(type=="punc"&&HOP(PUNC_BEFORE_EXPRESSION,value)));var ret={type:type,value:value,line:S.tokline,col:S.tokcol,pos:S.tokpos,nlb:S.newline_before};if(!is_comment){ret.comments_before=S.comments_before;S.comments_before=[]}S.newline_before=false;return ret}function skip_whitespace(){while(HOP(WHITESPACE_CHARS,peek())){next()}}function read_while(pred){var ret="",ch=peek(),i=0;while(ch&&pred(ch,i++)){ret+=next();ch=peek()}return ret}function parse_error(err){js_error(err,S.tokline,S.tokcol,S.tokpos)}function read_num(prefix){var has_e=false,after_e=false,has_x=false,has_dot=prefix==".";var num=read_while(function(ch,i){if(ch=="x"||ch=="X"){if(has_x){return false}return has_x=true}if(!has_x&&(ch=="E"||ch=="e")){if(has_e){return false}return has_e=after_e=true}if(ch=="-"){if(after_e||(i==0&&!prefix)){return true}return false}if(ch=="+"){return after_e}after_e=false;if(ch=="."){if(!has_dot&&!has_x){return has_dot=true}return false}return is_alphanumeric_char(ch)});if(prefix){num=prefix+num}var valid=parse_js_number(num);if(!isNaN(valid)){return token("num",valid)}else{parse_error("Invalid syntax: "+num)}}function read_escaped_char(){var ch=next(true);switch(ch){case"n":return"\n";case"r":return"\r";case"t":return"\t";case"b":return"\b";case"v":return"\v";case"f":return"\f";case"0":return"\0";case"x":return String.fromCharCode(hex_bytes(2));case"u":return String.fromCharCode(hex_bytes(4));default:return ch}}function hex_bytes(n){var num=0;for(;n>0;--n){var digit=parseInt(next(true),16);if(isNaN(digit)){parse_error("Invalid hex-character pattern in string")}num=(num<<4)|digit}return num}function read_string(){return with_eof_error("Unterminated string constant",function(){var quote=next(),ret="";for(;;){var ch=next(true);if(ch=="\\"){ch=read_escaped_char()}else{if(ch==quote){break}}ret+=ch}return token("string",ret)})}function read_line_comment(){next();var i=find("\n"),ret;if(i==-1){ret=S.text.substr(S.pos);S.pos=S.text.length}else{ret=S.text.substring(S.pos,i);S.pos=i}return token("comment1",ret,true)}function read_multiline_comment(){next();return with_eof_error("Unterminated multiline comment",function(){var i=find("*/",true),text=S.text.substring(S.pos,i),tok=token("comment2",text,true);S.pos=i+2;S.line+=text.split("\n").length-1;S.newline_before=text.indexOf("\n")>=0;if(/^@cc_on/i.test(text)){warn("WARNING: at line "+S.line);warn('*** Found "conditional comment": '+text);warn("*** UglifyJS DISCARDS ALL COMMENTS.  This means your code might no longer work properly in Internet Explorer.")}return tok})}function read_name(){var backslash=false,name="",ch;while((ch=peek())!=null){if(!backslash){if(ch=="\\"){backslash=true,next()}else{if(is_identifier_char(ch)){name+=next()}else{break}}}else{if(ch!="u"){parse_error("Expecting UnicodeEscapeSequence -- uXXXX")}ch=read_escaped_char();if(!is_identifier_char(ch)){parse_error("Unicode char: "+ch.charCodeAt(0)+" is not valid in identifier")}name+=ch;backslash=false}}return name}function read_regexp(){return with_eof_error("Unterminated regular expression",function(){var prev_backslash=false,regexp="",ch,in_class=false;while((ch=next(true))){if(prev_backslash){regexp+="\\"+ch;prev_backslash=false}else{if(ch=="["){in_class=true;regexp+=ch}else{if(ch=="]"&&in_class){in_class=false;regexp+=ch}else{if(ch=="/"&&!in_class){break}else{if(ch=="\\"){prev_backslash=true}else{regexp+=ch}}}}}}var mods=read_name();return token("regexp",[regexp,mods])})}function read_operator(prefix){function grow(op){if(!peek()){return op}var bigger=op+peek();if(HOP(OPERATORS,bigger)){next();return grow(bigger)}else{return op}}return token("operator",grow(prefix||next()))}function handle_slash(){next();var regex_allowed=S.regex_allowed;switch(peek()){case"/":S.comments_before.push(read_line_comment());S.regex_allowed=regex_allowed;return next_token();case"*":S.comments_before.push(read_multiline_comment());S.regex_allowed=regex_allowed;return next_token()}return S.regex_allowed?read_regexp():read_operator("/")}function handle_dot(){next();return is_digit(peek())?read_num("."):token("punc",".")}function read_word(){var word=read_name();return !HOP(KEYWORDS,word)?token("name",word):HOP(OPERATORS,word)?token("operator",word):HOP(KEYWORDS_ATOM,word)?token("atom",word):token("keyword",word)}function with_eof_error(eof_error,cont){try{return cont()}catch(ex){if(ex===EX_EOF){parse_error(eof_error)}else{throw ex}}}function next_token(force_regexp){if(force_regexp){return read_regexp()}skip_whitespace();start_token();var ch=peek();if(!ch){return token("eof")}if(is_digit(ch)){return read_num()}if(ch=='"'||ch=="'"){return read_string()}if(HOP(PUNC_CHARS,ch)){return token("punc",next())}if(ch=="."){return handle_dot()}if(ch=="/"){return handle_slash()}if(HOP(OPERATOR_CHARS,ch)){return read_operator()}if(ch=="\\"||is_identifier_start(ch)){return read_word()}parse_error("Unexpected character '"+ch+"'")}next_token.context=function(nc){if(nc){S=nc}return S};return next_token}var UNARY_PREFIX=array_to_hash(["typeof","void","delete","--","++","!","~","-","+"]);var UNARY_POSTFIX=array_to_hash(["--","++"]);var ASSIGNMENT=(function(a,ret,i){while(i<a.length){ret[a[i]]=a[i].substr(0,a[i].length-1);i++}return ret})(["+=","-=","/=","*=","%=",">>=","<<=",">>>=","|=","^=","&="],{"=":true},0);var PRECEDENCE=(function(a,ret){for(var i=0,n=1;i<a.length;++i,++n){var b=a[i];for(var j=0;j<b.length;++j){ret[b[j]]=n}}return ret})([["||"],["&&"],["|"],["^"],["&"],["==","===","!=","!=="],["<",">","<=",">=","in","instanceof"],[">>","<<",">>>"],["+","-"],["*","/","%"]],{});var STATEMENTS_WITH_LABELS=array_to_hash(["for","do","while","switch"]);var ATOMIC_START_TOKEN=array_to_hash(["atom","num","string","regexp","name"]);function NodeWithToken(str,start,end){this.name=str;this.start=start;this.end=end}NodeWithToken.prototype.toString=function(){return this.name};function parse($TEXT,exigent_mode,embed_tokens){var S={input:typeof $TEXT=="string"?tokenizer($TEXT,true):$TEXT,token:null,prev:null,peeked:null,in_function:0,in_loop:0,labels:[]};S.token=next();function is(type,value){return is_token(S.token,type,value)}function peek(){return S.peeked||(S.peeked=S.input())}function next(){S.prev=S.token;if(S.peeked){S.token=S.peeked;S.peeked=null}else{S.token=S.input()}return S.token}function prev(){return S.prev}function croak(msg,line,col,pos){var ctx=S.input.context();js_error(msg,line!=null?line:ctx.tokline,col!=null?col:ctx.tokcol,pos!=null?pos:ctx.tokpos)}function token_error(token,msg){croak(msg,token.line,token.col)}function unexpected(token){if(token==null){token=S.token}token_error(token,"Unexpected token: "+token.type+" ("+token.value+")")}function expect_token(type,val){if(is(type,val)){return next()}token_error(S.token,"Unexpected token "+S.token.type+", expected "+type)}function expect(punc){return expect_token("punc",punc)}function can_insert_semicolon(){return !exigent_mode&&(S.token.nlb||is("eof")||is("punc","}"))}function semicolon(){if(is("punc",";")){next()}else{if(!can_insert_semicolon()){unexpected()}}}function as(){return slice(arguments)}function parenthesised(){expect("(");var ex=expression();expect(")");return ex}function add_tokens(str,start,end){return str instanceof NodeWithToken?str:new NodeWithToken(str,start,end)}var statement=embed_tokens?function(){var start=S.token;var ast=$statement.apply(this,arguments);ast[0]=add_tokens(ast[0],start,prev());return ast}:$statement;function $statement(){if(is("operator","/")){S.peeked=null;S.token=S.input(true)}switch(S.token.type){case"num":case"string":case"regexp":case"operator":case"atom":return simple_statement();case"name":return is_token(peek(),"punc",":")?labeled_statement(prog1(S.token.value,next,next)):simple_statement();case"punc":switch(S.token.value){case"{":return as("block",block_());case"[":case"(":return simple_statement();case";":next();return as("block");default:unexpected()}case"keyword":switch(prog1(S.token.value,next)){case"break":return break_cont("break");case"continue":return break_cont("continue");case"debugger":semicolon();return as("debugger");case"do":return(function(body){expect_token("keyword","while");return as("do",prog1(parenthesised,semicolon),body)})(in_loop(statement));case"for":return for_();case"function":return function_(true);case"if":return if_();case"return":if(S.in_function==0){croak("'return' outside of function")}return as("return",is("punc",";")?(next(),null):can_insert_semicolon()?null:prog1(expression,semicolon));case"switch":return as("switch",parenthesised(),switch_block_());case"throw":return as("throw",prog1(expression,semicolon));case"try":return try_();case"var":return prog1(var_,semicolon);case"const":return prog1(const_,semicolon);case"while":return as("while",parenthesised(),in_loop(statement));case"with":return as("with",parenthesised(),statement());default:unexpected()}}}function labeled_statement(label){S.labels.push(label);var start=S.token,stat=statement();if(exigent_mode&&!HOP(STATEMENTS_WITH_LABELS,stat[0])){unexpected(start)}S.labels.pop();return as("label",label,stat)}function simple_statement(){return as("stat",prog1(expression,semicolon))}function break_cont(type){var name=is("name")?S.token.value:null;if(name!=null){next();if(!member(name,S.labels)){croak("Label "+name+" without matching loop or statement")}}else{if(S.in_loop==0){croak(type+" not inside a loop or switch")}}semicolon();return as(type,name)}function for_(){expect("(");var init=null;if(!is("punc",";")){init=is("keyword","var")?(next(),var_(true)):expression(true,true);if(is("operator","in")){return for_in(init)}}return regular_for(init)}function regular_for(init){expect(";");var test=is("punc",";")?null:expression();expect(";");var step=is("punc",")")?null:expression();expect(")");return as("for",init,test,step,in_loop(statement))}function for_in(init){var lhs=init[0]=="var"?as("name",init[1][0]):init;next();var obj=expression();expect(")");return as("for-in",init,lhs,obj,in_loop(statement))}var function_=embed_tokens?function(){var start=prev();var ast=$function_.apply(this,arguments);ast[0]=add_tokens(ast[0],start,prev());return ast}:$function_;function $function_(in_statement){var name=is("name")?prog1(S.token.value,next):null;if(in_statement&&!name){unexpected()}expect("(");return as(in_statement?"defun":"function",name,(function(first,a){while(!is("punc",")")){if(first){first=false}else{expect(",")}if(!is("name")){unexpected()}a.push(S.token.value);next()}next();return a})(true,[]),(function(){++S.in_function;var loop=S.in_loop;S.in_loop=0;var a=block_();--S.in_function;S.in_loop=loop;return a})())}function if_(){var cond=parenthesised(),body=statement(),belse;if(is("keyword","else")){next();belse=statement()}return as("if",cond,body,belse)}function block_(){expect("{");var a=[];while(!is("punc","}")){if(is("eof")){unexpected()}a.push(statement())}next();return a}var switch_block_=curry(in_loop,function(){expect("{");var a=[],cur=null;while(!is("punc","}")){if(is("eof")){unexpected()}if(is("keyword","case")){next();cur=[];a.push([expression(),cur]);expect(":")}else{if(is("keyword","default")){next();expect(":");cur=[];a.push([null,cur])}else{if(!cur){unexpected()}cur.push(statement())}}}next();return a});function try_(){var body=block_(),bcatch,bfinally;if(is("keyword","catch")){next();expect("(");if(!is("name")){croak("Name expected")}var name=S.token.value;next();expect(")");bcatch=[name,block_()]}if(is("keyword","finally")){next();bfinally=block_()}if(!bcatch&&!bfinally){croak("Missing catch/finally blocks")}return as("try",body,bcatch,bfinally)}function vardefs(no_in){var a=[];for(;;){if(!is("name")){unexpected()}var name=S.token.value;next();if(is("operator","=")){next();a.push([name,expression(false,no_in)])}else{a.push([name])}if(!is("punc",",")){break}next()}return a}function var_(no_in){return as("var",vardefs(no_in))}function const_(){return as("const",vardefs())}function new_(){var newexp=expr_atom(false),args;if(is("punc","(")){next();args=expr_list(")")}else{args=[]}return subscripts(as("new",newexp,args),true)}function expr_atom(allow_calls){if(is("operator","new")){next();return new_()}if(is("operator")&&HOP(UNARY_PREFIX,S.token.value)){return make_unary("unary-prefix",prog1(S.token.value,next),expr_atom(allow_calls))}if(is("punc")){switch(S.token.value){case"(":next();return subscripts(prog1(expression,curry(expect,")")),allow_calls);case"[":next();return subscripts(array_(),allow_calls);case"{":next();return subscripts(object_(),allow_calls)}unexpected()}if(is("keyword","function")){next();return subscripts(function_(false),allow_calls)}if(HOP(ATOMIC_START_TOKEN,S.token.type)){var atom=S.token.type=="regexp"?as("regexp",S.token.value[0],S.token.value[1]):as(S.token.type,S.token.value);return subscripts(prog1(atom,next),allow_calls)}unexpected()}function expr_list(closing,allow_trailing_comma,allow_empty){var first=true,a=[];while(!is("punc",closing)){if(first){first=false}else{expect(",")}if(allow_trailing_comma&&is("punc",closing)){break}if(is("punc",",")&&allow_empty){a.push(["atom","undefined"])}else{a.push(expression(false))}}next();return a}function array_(){return as("array",expr_list("]",!exigent_mode,true))}function object_(){var first=true,a=[];while(!is("punc","}")){if(first){first=false}else{expect(",")}if(!exigent_mode&&is("punc","}")){break}var type=S.token.type;var name=as_property_name();if(type=="name"&&(name=="get"||name=="set")&&!is("punc",":")){a.push([as_name(),function_(false),name])}else{expect(":");a.push([name,expression(false)])}}next();return as("object",a)}function as_property_name(){switch(S.token.type){case"num":case"string":return prog1(S.token.value,next)}return as_name()}function as_name(){switch(S.token.type){case"name":case"operator":case"keyword":case"atom":return prog1(S.token.value,next);default:unexpected()}}function subscripts(expr,allow_calls){if(is("punc",".")){next();return subscripts(as("dot",expr,as_name()),allow_calls)}if(is("punc","[")){next();return subscripts(as("sub",expr,prog1(expression,curry(expect,"]"))),allow_calls)}if(allow_calls&&is("punc","(")){next();return subscripts(as("call",expr,expr_list(")")),true)}if(allow_calls&&is("operator")&&HOP(UNARY_POSTFIX,S.token.value)){return prog1(curry(make_unary,"unary-postfix",S.token.value,expr),next)}return expr}function make_unary(tag,op,expr){if((op=="++"||op=="--")&&!is_assignable(expr)){croak("Invalid use of "+op+" operator")}return as(tag,op,expr)}function expr_op(left,min_prec,no_in){var op=is("operator")?S.token.value:null;if(op&&op=="in"&&no_in){op=null}var prec=op!=null?PRECEDENCE[op]:null;if(prec!=null&&prec>min_prec){next();var right=expr_op(expr_atom(true),prec,no_in);return expr_op(as("binary",op,left,right),min_prec,no_in)}return left}function expr_ops(no_in){return expr_op(expr_atom(true),0,no_in)}function maybe_conditional(no_in){var expr=expr_ops(no_in);if(is("operator","?")){next();var yes=expression(false);expect(":");return as("conditional",expr,yes,expression(false,no_in))}return expr}function is_assignable(expr){if(!exigent_mode){return true}switch(expr[0]){case"dot":case"sub":case"new":case"call":return true;case"name":return expr[1]!="this"}}function maybe_assign(no_in){var left=maybe_conditional(no_in),val=S.token.value;if(is("operator")&&HOP(ASSIGNMENT,val)){if(is_assignable(left)){next();return as("assign",ASSIGNMENT[val],left,maybe_assign(no_in))}croak("Invalid assignment")}return left}function expression(commas,no_in){if(arguments.length==0){commas=true}var expr=maybe_assign(no_in);if(commas&&is("punc",",")){next();return as("seq",expr,expression(true,no_in))}return expr}function in_loop(cont){try{++S.in_loop;return cont()}finally{--S.in_loop}}return as("toplevel",(function(a){while(!is("eof")){a.push(statement())}return a})([]))}function curry(f){var args=slice(arguments,1);return function(){return f.apply(this,args.concat(slice(arguments)))}}function prog1(ret){if(ret instanceof Function){ret=ret()}for(var i=1,n=arguments.length;--n>0;++i){arguments[i]()}return ret}function array_to_hash(a){var ret={};for(var i=0;i<a.length;++i){ret[a[i]]=true}return ret}function slice(a,start){return Array.prototype.slice.call(a,start==null?0:start)}function characters(str){return str.split("")}function member(name,array){for(var i=array.length;--i>=0;){if(array[i]===name){return true}}return false}function HOP(obj,prop){return Object.prototype.hasOwnProperty.call(obj,prop)}var warn=function(){};return parse})();var Wind;var codeGenerator=(typeof eval("(function () {})")=="function")?function(code){return code}:function(code){return"false || "+code};var stringify=(typeof JSON!=="undefined"&&JSON.stringify)?function(s){return JSON.stringify(s)}:(function(){var escapable=/[\\\"\x00-\x1f\x7f-\x9f\u00ad\u0600-\u0604\u070f\u17b4\u17b5\u200c-\u200f\u2028-\u202f\u2060-\u206f\ufeff\ufff0-\uffff]/g;var meta={"\b":"\\b","\t":"\\t","\n":"\\n","\f":"\\f","\r":"\\r",'"':'\\"',"\\":"\\\\"};return function(s){escapable.lastIndex=0;return escapable.test(s)?'"'+s.replace(escapable,function(a){var c=meta[a];return typeof c==="s"?c:"\\u"+("0000"+a.charCodeAt(0).toString(16)).slice(-4)})+'"':'"'+s+'"'}})();function sprintf(format){var args=arguments;return format.toString().replace(new RegExp("{\\d+}","g"),function(p){var n=parseInt(p.substring(1,p.length-1),10);return args[n+1]})}function trim(s){return s.replace(/ +/g,"")}function getPrecedence(ast){var type=ast[0];switch(type){case"dot":case"sub":case"call":return 1;case"unary-postfix":case"unary-prefix":return 2;case"var":case"binary":switch(ast[1]){case"*":case"/":case"%":return 3;case"+":case"-":return 4;case"<<":case">>":case">>>":return 5;case"<":case"<=":case">":case">=":case"instanceof":return 6;case"==":case"!=":case"===":case"!==":return 7;case"&":return 8;case"^":return 9;case"|":return 10;case"&&":return 11;case"||":return 12}case"conditional":return 13;case"assign":return 14;case"new":return 15;case"seq":case"stat":case"name":case"object":case"array":case"num":case"regexp":case"string":case"function":case"defun":case"for":case"for-in":case"block":case"while":case"do":case"if":case"break":case"continue":case"return":case"throw":case"try":case"switch":return 0;default:return 100}}var CodeWriter=function(indent){this._indent=indent||"    ";this._indentLevel=0;this.lines=[]};CodeWriter.prototype={write:function(str){if(str===undefined){return}if(this.lines.length==0){this.lines.push("")}this.lines[this.lines.length-1]+=str;return this},writeLine:function(){this.write.apply(this,arguments);this.lines.push("");return this},writeIndents:function(){var indents=new Array(this._indentLevel);for(var i=0;i<this._indentLevel;i++){indents[i]=this._indent}this.write(indents.join(""));return this},addIndentLevel:function(diff){this._indentLevel+=diff;return this}};var SeedProvider=function(){this._seeds={}};SeedProvider.prototype.next=function(key){var value=this._seeds[key];if(value==undefined){this._seeds[key]=0;return 0}else{this._seeds[key]=++value;return value}};function isWindPattern(ast){if(ast[0]!="call"){return false}var evalName=ast[1];if(evalName[0]!="name"||evalName[1]!="eval"){return false}var compileCall=ast[2][0];if(!compileCall||compileCall[0]!="call"){return false}var compileMethod=compileCall[1];if(!compileMethod||compileMethod[0]!="dot"||compileMethod[2]!="compile"){return false}var windName=compileMethod[1];if(!windName||windName[0]!="name"||windName[1]!=compile.rootName){return false}var builder=compileCall[2][0];if(!builder||builder[0]!="string"){return false}var func=compileCall[2][1];if(!func||func[0]!="function"){return false}return true}function compileWindPattern(ast,seedProvider,codeWriter,commentWriter){var builderName=ast[2][0][2][0][1];var funcAst=ast[2][0][2][1];var windTreeGenerator=new WindTreeGenerator(builderName,seedProvider);var windAst=windTreeGenerator.generate(funcAst);commentWriter.write(builderName+" << ");var codeGenerator=new CodeGenerator(builderName,seedProvider,codeWriter,commentWriter);var funcName=funcAst[1]||"";codeGenerator.generate(funcName,funcAst[2],windAst);return funcName}var WindTreeGenerator=function(builderName,seedProvider){this._binder=Wind.binders[builderName];this._seedProvider=seedProvider};WindTreeGenerator.prototype={generate:function(ast){var params=ast[2],statements=ast[3];var rootAst={type:"delay",stmts:[]};this._visitStatements(statements,rootAst.stmts);return rootAst},_getBindInfo:function(stmt){var type=stmt[0];if(type=="stat"){var expr=stmt[1];if(expr[0]=="call"){var callee=expr[1];if(callee[0]=="name"&&callee[1]==this._binder&&expr[2].length==1){return{expression:expr[2][0],argName:"",assignee:null}}}else{if(expr[0]=="assign"){var assignee=expr[2];expr=expr[3];if(expr[0]=="call"){var callee=expr[1];if(callee[0]=="name"&&callee[1]==this._binder&&expr[2].length==1){return{expression:expr[2][0],argName:"_result_$",assignee:assignee}}}}}}else{if(type=="var"){var defs=stmt[1];if(defs.length==1){var item=defs[0];var name=item[0];var expr=item[1];if(expr&&expr[0]=="call"){var callee=expr[1];if(callee[0]=="name"&&callee[1]==this._binder&&expr[2].length==1){return{expression:expr[2][0],argName:name,assignee:null}}}}}else{if(type=="return"){var expr=stmt[1];if(expr&&expr[0]=="call"){var callee=expr[1];if(callee[0]=="name"&&callee[1]==this._binder&&expr[2].length==1){return{expression:expr[2][0],argName:"_result_$",assignee:"return"}}}}}}return null},_visitStatements:function(statements,stmts,index){if(arguments.length<=2){index=0}if(index>=statements.length){stmts.push({type:"normal"});return this}var currStmt=statements[index];var bindInfo=this._getBindInfo(currStmt);if(bindInfo){var bindStmt={type:"bind",info:bindInfo};stmts.push(bindStmt);if(bindInfo.assignee!="return"){bindStmt.stmts=[];this._visitStatements(statements,bindStmt.stmts,index+1)}}else{var type=currStmt[0];if(type=="return"||type=="break"||type=="continue"||type=="throw"){stmts.push({type:type,stmt:currStmt})}else{if(type=="if"||type=="try"||type=="for"||type=="do"||type=="while"||type=="switch"||type=="for-in"){var newStmt=this._visit(currStmt);if(newStmt.type=="raw"){stmts.push(newStmt);this._visitStatements(statements,stmts,index+1)}else{var isLast=(index==statements.length-1);if(isLast){stmts.push(newStmt)}else{var combineStmt={type:"combine",first:{type:"delay",stmts:[newStmt]},second:{type:"delay",stmts:[]}};stmts.push(combineStmt);this._visitStatements(statements,combineStmt.second.stmts,index+1)}}}else{stmts.push({type:"raw",stmt:currStmt});this._visitStatements(statements,stmts,index+1)}}}return this},_visit:function(ast){var type=ast[0];function throwUnsupportedError(){throw new Error('"'+type+'" is not currently supported.')}var visitor=this._visitors[type];if(visitor){return visitor.call(this,ast)}else{throwUnsupportedError()}},_visitBody:function(ast,stmts){if(ast[0]=="block"){this._visitStatements(ast[1],stmts)}else{this._visitStatements([ast],stmts)}},_noBinding:function(stmts){switch(stmts[stmts.length-1].type){case"normal":case"return":case"break":case"throw":case"continue":return true}return false},_collectCaseStatements:function(cases,index){var res=[];for(var i=index;i<cases.length;i++){var rawStmts=cases[i][1];for(var j=0;j<rawStmts.length;j++){if(rawStmts[j][0]=="break"){return res}res.push(rawStmts[j])}}return res},_visitors:{"for":function(ast){var bodyStmts=[];var body=ast[4];this._visitBody(body,bodyStmts);if(this._noBinding(bodyStmts)){return{type:"raw",stmt:ast}}var delayStmt={type:"delay",stmts:[]};var setup=ast[1];if(setup){delayStmt.stmts.push({type:"raw",stmt:setup})}var forStmt={type:"for",bodyStmt:{type:"delay",stmts:bodyStmts}};delayStmt.stmts.push(forStmt);var condition=ast[2];if(condition){forStmt.condition=condition}var update=ast[3];if(update){forStmt.update=update}return delayStmt},"for-in":function(ast){var body=ast[4];var bodyStmts=[];this._visitBody(body,bodyStmts);if(this._noBinding(bodyStmts)){return{type:"raw",stmt:ast}}var forInStmt={type:"for-in",bodyStmts:bodyStmts,obj:ast[3]};var argName=ast[2][1];if(ast[1][0]=="var"){forInStmt.argName=argName}else{var keyVar="_forInKey_$"+this._seedProvider.next("forInKey");forInStmt.argName=keyVar;forInStmt.bodyStmts.unshift({type:"raw",stmt:parse(argName+" = "+keyVar+";")[1][0]})}return forInStmt},"while":function(ast){var bodyStmts=[];var body=ast[2];this._visitBody(body,bodyStmts);if(this._noBinding(bodyStmts)){return{type:"raw",stmt:ast}}var loopStmt={type:"while",bodyStmt:{type:"delay",stmts:bodyStmts}};var condition=ast[1];loopStmt.condition=condition;return loopStmt},"do":function(ast){var bodyStmts=[];var body=ast[2];this._visitBody(body,bodyStmts);if(this._noBinding(bodyStmts)){return{type:"raw",stmt:ast}}var doStmt={type:"do",bodyStmt:{type:"delay",stmts:bodyStmts},condition:ast[1]};return doStmt},"switch":function(ast){var noBinding=true;var switchStmt={type:"switch",item:ast[1],caseStmts:[]};var cases=ast[2];for(var i=0;i<cases.length;i++){var caseStmt={item:cases[i][0],stmts:[]};switchStmt.caseStmts.push(caseStmt);var statements=this._collectCaseStatements(cases,i);this._visitStatements(statements,caseStmt.stmts);noBinding=noBinding&&this._noBinding(caseStmt.stmts)}if(noBinding){return{type:"raw",stmt:ast}}else{return switchStmt}},"if":function(ast){var noBinding=true;var ifStmt={type:"if",conditionStmts:[]};var currAst=ast;while(true){var condition=currAst[1];var condStmt={cond:condition,stmts:[]};ifStmt.conditionStmts.push(condStmt);var thenPart=currAst[2];this._visitBody(thenPart,condStmt.stmts);noBinding=noBinding&&this._noBinding(condStmt.stmts);var elsePart=currAst[3];if(elsePart&&elsePart[0]=="if"){currAst=elsePart}else{break}}var elsePart=currAst[3];if(elsePart){ifStmt.elseStmts=[];this._visitBody(elsePart,ifStmt.elseStmts);noBinding=noBinding&&this._noBinding(ifStmt.elseStmts)}if(noBinding){return{type:"raw",stmt:ast}}else{return ifStmt}},"try":function(ast,stmts){var bodyStmts=[];var bodyStatements=ast[1];this._visitStatements(bodyStatements,bodyStmts);var noBinding=this._noBinding(bodyStmts);var tryStmt={type:"try",bodyStmt:{type:"delay",stmts:bodyStmts}};var catchClause=ast[2];if(catchClause){var exVar=catchClause[0];tryStmt.exVar=exVar;tryStmt.catchStmts=[];this._visitStatements(catchClause[1],tryStmt.catchStmts);noBinding=noBinding&&this._noBinding(tryStmt.catchStmts)}var finallyStatements=ast[3];if(finallyStatements){tryStmt.finallyStmt={type:"delay",stmts:[]};this._visitStatements(finallyStatements,tryStmt.finallyStmt.stmts);noBinding=noBinding&&this._noBinding(tryStmt.finallyStmt.stmts)}if(noBinding){return{type:"raw",stmt:ast}}else{return tryStmt}}}};var CodeGenerator=function(builderName,seedProvider,codeWriter,commentWriter){this._builderName=builderName;this._binder=Wind.binders[builderName];this._seedProvider=seedProvider;this._codeWriter=codeWriter;this._commentWriter=commentWriter};CodeGenerator.prototype={_code:function(){this._codeWriter.write.apply(this._codeWriter,arguments);return this},_codeLine:function(){this._codeWriter.writeLine.apply(this._codeWriter,arguments);return this},_codeIndents:function(){this._codeWriter.writeIndents();return this},_codeIndentLevel:function(diff){this._codeWriter.addIndentLevel(diff);return this},_comment:function(){this._commentWriter.write.apply(this._commentWriter,arguments);return this},_commentLine:function(){this._commentWriter.writeLine.apply(this._commentWriter,arguments);return this},_commentIndents:function(){this._commentWriter.writeIndents();return this},_commentIndentLevel:function(diff){this._commentWriter.addIndentLevel(diff);return this},_both:function(){this._codeWriter.write.apply(this._codeWriter,arguments);this._commentWriter.write.apply(this._commentWriter,arguments);return this},_bothLine:function(){this._codeWriter.writeLine.apply(this._codeWriter,arguments);this._commentWriter.writeLine.apply(this._commentWriter,arguments);return this},_bothIndents:function(){this._codeWriter.writeIndents();this._commentWriter.writeIndents();return this},_bothIndentLevel:function(diff){this._codeWriter.addIndentLevel(diff);this._commentWriter.addIndentLevel(diff);return this},_newLine:function(){this._codeWriter.writeLine.apply(this._codeWriter,arguments);this._commentWriter.writeLine();return this},generate:function(name,params,windAst){this._normalMode=false;this._builderVar="_builder_$"+this._seedProvider.next("builderId");this._codeLine("(function "+name+"("+params.join(", ")+") {")._commentLine("function ("+params.join(", ")+") {");this._bothIndentLevel(1);this._codeIndents()._newLine("var "+this._builderVar+" = "+compile.rootName+".builders["+stringify(this._builderName)+"];");this._codeIndents()._newLine("return "+this._builderVar+".Start(this,");this._codeIndentLevel(1);this._pos={};this._bothIndents()._visitWind(windAst)._newLine();this._codeIndentLevel(-1);this._codeIndents()._newLine(");");this._bothIndentLevel(-1);this._bothIndents()._code("})")._comment("}")},_visitWind:function(ast){this._windVisitors[ast.type].call(this,ast);return this},_visitRaw:function(ast){var type=ast[0];var visitor=this._rawVisitors[type];if(visitor){visitor.call(this,ast)}else{throw new Error('"'+type+'" is not currently supported.')}return this},_visitWindStatements:function(statements){for(var i=0;i<statements.length;i++){var stmt=statements[i];if(stmt.type=="raw"||stmt.type=="if"||stmt.type=="switch"){this._bothIndents()._visitWind(stmt)._newLine()}else{if(stmt.type=="delay"){this._visitWindStatements(stmt.stmts)}else{this._bothIndents()._code("return ")._visitWind(stmt)._newLine(";")}}}},_visitRawStatements:function(statements){for(var i=0;i<statements.length;i++){var s=statements[i];this._bothIndents()._visitRaw(s)._bothLine();switch(s[0]){case"break":case"return":case"continue":case"throw":return}}},_visitRawBody:function(body){if(body[0]=="block"){this._visitRaw(body)}else{this._bothLine();this._bothIndentLevel(1);this._bothIndents()._visitRaw(body);this._bothIndentLevel(-1)}return this},_visitRawFunction:function(ast){var funcName=ast[1]||"";var args=ast[2];var statements=ast[3];this._bothLine("function "+funcName+"("+args.join(", ")+") {");this._bothIndentLevel(1);var currInFunction=this._pos.inFunction;this._pos.inFunction=true;this._visitRawStatements(statements);this._bothIndentLevel(-1);this._pos.inFunction=currInFunction;this._bothIndents()._both("}")},_windVisitors:{delay:function(ast){if(ast.stmts.length==1){var subStmt=ast.stmts[0];switch(subStmt.type){case"delay":case"combine":case"normal":case"break":case"continue":case"for":case"for-in":case"while":case"do":case"try":this._visitWind(subStmt);return;case"return":if(!subStmt.stmt[1]){this._visitWind(subStmt);return}}}this._newLine(this._builderVar+".Delay(function () {");this._codeIndentLevel(1);this._visitWindStatements(ast.stmts);this._codeIndentLevel(-1);this._codeIndents()._code("})")},combine:function(ast){this._newLine(this._builderVar+".Combine(");this._codeIndentLevel(1);this._bothIndents()._visitWind(ast.first)._newLine(",");this._bothIndents()._visitWind(ast.second)._newLine();this._codeIndentLevel(-1);this._codeIndents()._code(")")},"for":function(ast){if(ast.condition){this._codeLine(this._builderVar+".For(function () {")._commentLine("for (");this._codeIndentLevel(1);this._bothIndents()._code("return ")._comment("; ")._visitRaw(ast.condition)._newLine(";");this._codeIndentLevel(-1);this._bothIndents()._code("}, ")}else{this._code(this._builderVar+".For(null, ")._comment("for (; ")}if(ast.update){this._newLine("function () {");this._codeIndentLevel(1);this._bothIndents()._comment("; ")._visitRaw(ast.update)._codeLine(";")._commentLine(") {");this._codeIndentLevel(-1);this._codeIndents()._newLine("},")}else{this._codeLine("null,")._commentLine("; ) {")}this._bothIndentLevel(1);this._bothIndents()._visitWind(ast.bodyStmt)._newLine();this._bothIndentLevel(-1);this._bothIndents()._code(")")._comment("}")},"for-in":function(ast){this._code(this._builderVar+".ForIn(")._comment("for (var "+ast.argName+" in ")._visitRaw(ast.obj)._codeLine(", function ("+ast.argName+") {")._commentLine(") {");this._bothIndentLevel(1);this._visitWindStatements(ast.bodyStmts);this._bothIndentLevel(-1);this._bothIndents()._code("})")._comment("}")},"while":function(ast){this._newLine(this._builderVar+".While(function () {");this._codeIndentLevel(1);this._bothIndents()._code("return ")._comment("while (")._visitRaw(ast.condition)._codeLine(";")._commentLine(") {");this._codeIndentLevel(-1);this._codeIndents()._newLine("},");this._bothIndentLevel(1);this._bothIndents()._visitWind(ast.bodyStmt)._newLine();this._bothIndentLevel(-1);this._bothIndents()._code(")")._comment("}")},"do":function(ast){this._codeLine(this._builderVar+".Do(")._commentLine("do {");this._bothIndentLevel(1);this._bothIndents()._visitWind(ast.bodyStmt)._newLine(",");this._commentIndentLevel(-1);this._codeIndents()._newLine("function () {");this._codeIndentLevel(1);this._bothIndents()._code("return ")._comment("} while (")._visitRaw(ast.condition)._codeLine(";")._commentLine(");");this._codeIndentLevel(-1);this._codeIndents()._newLine("}");this._codeIndentLevel(-1);this._codeIndents()._code(")")},raw:function(ast){this._visitRaw(ast.stmt,true)},bind:function(ast){var info=ast.info;var commentPrefix="";if(info.assignee=="return"){commentPrefix="return "}else{if(info.argName!=""){commentPrefix="var "+info.argName+" = "}}this._code(this._builderVar+".Bind(")._comment(commentPrefix+this._binder+"(")._visitRaw(info.expression)._comment(");")._newLine(", function ("+info.argName+") {");this._codeIndentLevel(1);if(info.assignee=="return"){this._codeIndents()._newLine("return "+this._builderVar+".Return("+info.argName+");")}else{if(info.assignee){this._bothIndents()._visitRaw(info.assignee)._bothLine(" = "+info.argName+";")}this._visitWindStatements(ast.stmts)}this._codeIndentLevel(-1);this._codeIndents()._code("})")},"if":function(ast){for(var i=0;i<ast.conditionStmts.length;i++){var stmt=ast.conditionStmts[i];this._both("if (")._visitRaw(stmt.cond)._bothLine(") {");this._bothIndentLevel(1);this._visitWindStatements(stmt.stmts);this._bothIndentLevel(-1);if(i<ast.conditionStmts.length-1||ast.elseStmts){this._bothIndents()._both("} else ")}else{this._bothIndents()._code("} else ")._comment("}")}}if(ast.elseStmts){this._bothLine("{");this._bothIndentLevel(1)}else{this._newLine("{");this._codeIndentLevel(1)}if(ast.elseStmts){this._visitWindStatements(ast.elseStmts)}else{this._codeIndents()._newLine("return "+this._builderVar+".Normal();")}if(ast.elseStmts){this._bothIndentLevel(-1)}else{this._codeIndentLevel(-1)}if(ast.elseStmts){this._bothIndents()._both("}")}else{this._codeIndents()._code("}")}},"switch":function(ast){this._both("switch (")._visitRaw(ast.item)._bothLine(") {");this._bothIndentLevel(1);for(var i=0;i<ast.caseStmts.length;i++){var caseStmt=ast.caseStmts[i];if(caseStmt.item){this._bothIndents()._both("case ")._visitRaw(caseStmt.item)._bothLine(":")}else{this._bothIndents()._bothLine("default:")}this._bothIndentLevel(1);this._visitWindStatements(caseStmt.stmts);this._bothIndentLevel(-1)}this._bothIndents()._code("}")},"try":function(ast){this._codeLine(this._builderVar+".Try(")._commentLine("try {");this._bothIndentLevel(1);this._bothIndents()._visitWind(ast.bodyStmt)._newLine(",");this._commentIndentLevel(-1);if(ast.catchStmts){this._bothIndents()._codeLine("function ("+ast.exVar+") {")._commentLine("} catch ("+ast.exVar+") {");this._bothIndentLevel(1);this._visitWindStatements(ast.catchStmts);this._bothIndentLevel(-1);this._bothIndents()._codeLine("},");if(ast.finallyStmt){this._commentLine("} finally {")}else{this._commentLine("}")}}else{this._bothIndents()._codeLine("null,")._commentLine("} finally {")}if(ast.finallyStmt){this._commentIndentLevel(1);this._bothIndents()._visitWind(ast.finallyStmt)._newLine();this._commentIndentLevel(-1)}else{this._codeIndents()._newLine("null")}this._codeIndentLevel(-1);this._codeIndents()._code(")");if(ast.finallyStmt){this._commentIndents()._comment("}")}},normal:function(ast){this._code(this._builderVar+".Normal()")},"throw":function(ast){this._code(this._builderVar+".Throw(")._comment("throw ")._visitRaw(ast.stmt[1])._code(")")._comment(";")},"break":function(ast){this._code(this._builderVar+".Break()")._comment("break;")},"continue":function(ast){this._code(this._builderVar+".Continue()")._comment("continue;")},"return":function(ast){this._code(this._builderVar+".Return(")._comment("return");if(ast.stmt[1]){this._comment(" ")._visitRaw(ast.stmt[1])}this._code(")")._comment(";")}},_rawVisitors:{"var":function(ast){this._both("var ");var items=ast[1];for(var i=0;i<items.length;i++){this._both(items[i][0]);if(items[i].length>1){this._both(" = ")._visitRaw(items[i][1])}if(i<items.length-1){this._both(", ")}}this._both(";")},seq:function(ast,noBracket){var left=ast[1];var right=ast[2];if(!noBracket){this._both("(")}this._visitRaw(left);this._both(", ");if(right[0]=="seq"){arguments.callee.call(this,right,true)}else{this._visitRaw(right)}if(!noBracket){this._both(")")}},binary:function(ast){var op=ast[1],left=ast[2],right=ast[3];if(getPrecedence(ast)<getPrecedence(left)){this._both("(")._visitRaw(left)._both(") ")}else{this._visitRaw(left)._both(" ")}this._both(op);if(getPrecedence(ast)<=getPrecedence(right)){this._both(" (")._visitRaw(right)._both(")")}else{this._both(" ")._visitRaw(right)}},sub:function(ast){var prop=ast[1],index=ast[2];if(getPrecedence(ast)<getPrecedence(prop)){this._both("(")._visitRaw(prop)._both(")[")._visitRaw(index)._both("]")}else{this._visitRaw(prop)._both("[")._visitRaw(index)._both("]")}},"unary-postfix":function(ast){var op=ast[1];var item=ast[2];if(getPrecedence(ast)<=getPrecedence(item)){this._both("(")._visitRaw(item)._both(")")}else{this._visitRaw(item)}this._both(" "+op)},"unary-prefix":function(ast){var op=ast[1];var item=ast[2];this._both(op+" ");if(getPrecedence(ast)<getPrecedence(item)){this._both("(")._visitRaw(item)._both(")")}else{this._visitRaw(item)}},assign:function(ast){var op=ast[1];var name=ast[2];var value=ast[3];if(name[0]=="assign"){this._both("(")._visitRaw(name)._both(")")}else{this._visitRaw(name)}if((typeof op)=="string"){this._both(" "+op+"= ")}else{this._both(" = ")}this._visitRaw(value)},stat:function(ast){this._visitRaw(ast[1])._both(";")},dot:function(ast){var left=ast[1];var right=ast[2];if(getPrecedence(ast)<getPrecedence(left)){this._both("(")._visitRaw(left)._both(").")._both(right)}else{this._visitRaw(left)._both(".")._both(right)}},"new":function(ast){var ctor=ast[1];this._both("new ")._visitRaw(ctor)._both("(");var args=ast[2];for(var i=0,len=args.length;i<len;i++){this._visitRaw(args[i]);if(i<len-1){this._both(", ")}}this._both(")")},call:function(ast){if(isWindPattern(ast)){compileWindPattern(ast,this._seedProvider,this._codeWriter,this._commentWriter)}else{var caller=ast[1];var invalidBind=(caller[0]=="name")&&(caller[1]==this._binder);if(getPrecedence(ast)<getPrecedence(caller)){this._both("(")._visitRaw(caller)._both(")")}else{this._visitRaw(caller)}this._both("(");var args=ast[2];for(var i=0;i<args.length;i++){this._visitRaw(args[i]);if(i<args.length-1){this._both(", ")}}this._both(")")}},name:function(ast){this._both(ast[1])},object:function(ast){var items=ast[1];if(items.length<=0){this._both("{ }")}else{this._bothLine("{");this._bothIndentLevel(1);for(var i=0;i<items.length;i++){this._bothIndents()._both(stringify(items[i][0])+": ")._visitRaw(items[i][1]);if(i<items.length-1){this._bothLine(",")}else{this._bothLine("")}}this._bothIndentLevel(-1);this._bothIndents()._both("}")}},array:function(ast){this._both("[");var items=ast[1];for(var i=0;i<items.length;i++){this._visitRaw(items[i]);if(i<items.length-1){this._both(", ")}}this._both("]")},num:function(ast){this._both(ast[1])},regexp:function(ast){this._both("/"+ast[1]+"/"+ast[2])},string:function(ast){this._both(stringify(ast[1]))},"function":function(ast){this._visitRawFunction(ast)},defun:function(ast){this._visitRawFunction(ast)},"for":function(ast){this._both("for (");var setup=ast[1];if(setup){this._visitRaw(setup);if(setup[0]!="var"){this._both("; ")}else{this._both(" ")}}else{this._both("; ")}var condition=ast[2];if(condition){this._visitRaw(condition)}this._both("; ");var update=ast[3];if(update){this._visitRaw(update)}this._both(") ");var currInLoop=this._pos.inLoop;this._pos.inLoop=true;var body=ast[4];this._visitRawBody(body);this._pos.inLoop=currInLoop},"for-in":function(ast){this._both("for (");var declare=ast[1];if(declare[0]=="var"){this._both("var "+declare[1][0][0])}else{this._visitRaw(declare)}this._both(" in ")._visitRaw(ast[3])._both(") ");var currInLoop=this._pos.inLoop;this._pos.inLoop=true;var body=ast[4];this._visitRawBody(body);this._pos.inLoop=currInLoop},block:function(ast){if(ast.length>1){this._bothLine("{");this._bothIndentLevel(1);this._visitRawStatements(ast[1]);this._bothIndentLevel(-1);this._bothIndents()._both("}")}else{this._both(";")}},"while":function(ast){var condition=ast[1];var body=ast[2];var currInLoop=this._pos.inLoop;this._pos.inLoop=true;this._both("while (")._visitRaw(condition)._both(") ")._visitRawBody(body);this._pos.inLoop=currInLoop},"do":function(ast){var condition=ast[1];var body=ast[2];var currInLoop=this._pos.inLoop;this._pos.inLoop=true;this._both("do ")._visitRawBody(body);this._pos.inLoop=currInLoop;if(body[0]=="block"){this._both(" ")}else{this._bothLine()._bothIndents()}this._both("while (")._visitRaw(condition)._both(");")},"if":function(ast){var condition=ast[1];var thenPart=ast[2];this._both("if (")._visitRaw(condition)._both(") ")._visitRawBody(thenPart);var elsePart=ast[3];if(elsePart){if(thenPart[0]=="block"){this._both(" ")}else{this._bothLine("")._bothIndents()}if(elsePart[0]=="if"){this._both("else ")._visitRaw(elsePart)}else{this._both("else ")._visitRawBody(elsePart)}}},"break":function(ast){if(this._pos.inLoop||this._pos.inSwitch){this._both("break;")}else{this._code("return ")._visitWind({type:"break",stmt:ast})._code(";")}},"continue":function(ast){if(this._pos.inLoop){this._both("continue;")}else{this._code("return ")._visitWind({type:"continue",stmt:ast})._code(";")}},"return":function(ast){if(this._pos.inFunction){this._both("return");var value=ast[1];if(value){this._both(" ")._visitRaw(value)}this._both(";")}else{this._code("return ")._visitWind({type:"return",stmt:ast})._code(";")}},"throw":function(ast){var pos=this._pos;if(pos.inTry||pos.inFunction){this._both("throw ")._visitRaw(ast[1])._both(";")}else{this._code("return ")._visitWind({type:"throw",stmt:ast})._code(";")}},conditional:function(ast){this._both("(")._visitRaw(ast[1])._both(") ? (")._visitRaw(ast[2])._both(") : (")._visitRaw(ast[3])._both(")")},"try":function(ast){this._bothLine("try {");this._bothIndentLevel(1);var currInTry=this._pos.inTry;this._pos.inTry=true;this._visitRawStatements(ast[1]);this._bothIndentLevel(-1);this._pos.inTry=currInTry;var catchClause=ast[2];var finallyStatements=ast[3];if(catchClause){this._bothIndents()._bothLine("} catch ("+catchClause[0]+") {");this._bothIndentLevel(1);this._visitRawStatements(catchClause[1]);this._bothIndentLevel(-1)}if(finallyStatements){this._bothIndents()._bothLine("} finally {");this._bothIndentLevel(1);this._visitRawStatements(finallyStatements);this._bothIndentLevel(-1)}this._bothIndents()._both("}")},"switch":function(ast){this._both("switch (")._visitRaw(ast[1])._bothLine(") {");this._bothIndentLevel(1);var currInSwitch=this._pos.inSwitch;this._pos.inSwitch=true;var cases=ast[2];for(var i=0;i<cases.length;i++){var c=cases[i];this._bothIndents();if(c[0]){this._both("case ")._visitRaw(c[0])._bothLine(":")}else{this._bothLine("default:")}this._bothIndentLevel(1);this._visitRawStatements(c[1]);this._bothIndentLevel(-1)}this._bothIndentLevel(-1);this._pos.inSwitch=currInSwitch;this._bothIndents()._both("}")}}};var merge=function(commentLines,codeLines){var length=commentLines.length;var maxShift=0;for(var i=0;i<length;i++){var matches=codeLines[i].match(" +");var spaceLength=matches?matches[0].length:0;var shift=commentLines[i].length-spaceLength+10;if(shift>maxShift){maxShift=shift}}var shiftBuffer=new Array(maxShift);for(var i=0;i<maxShift;i++){shiftBuffer[i]=" "}var shiftSpaces=shiftBuffer.join("");var buffer=[];for(var i=0;i<length;i++){var comment=commentLines[i];if(comment.replace(/ +/g,"").length>0){comment="/* "+comment+" */   "}var code=shiftSpaces+codeLines[i];buffer.push(comment);buffer.push(code.substring(comment.length));if(i!=length-1){buffer.push("\n")}}return buffer.join("")};var sourceUrlSeed=0;var getOptions=function(options){options=options||{};options.root=options.root||"Wind";options.noSourceUrl=options.noSourceUrl||false;return options};var compile=function(builderName,func,options){options=getOptions(options);var funcCode=func.toString();var evalCode="eval("+compile.rootName+".compile("+stringify(builderName)+", "+funcCode+"))";var evalCodeAst=parse(evalCode);var codeWriter=new CodeWriter();var commentWriter=new CodeWriter();var evalAst=evalCodeAst[1][0][1];var funcName=compileWindPattern(evalAst,new SeedProvider(),codeWriter,commentWriter);var newCode=merge(commentWriter.lines,codeWriter.lines);if(!options.noSourceUrl){newCode+=("\n//@ sourceURL=wind/"+(sourceUrlSeed++)+"_"+(funcName||"anonymous")+".js")}Wind.logger.debug("// Original: \r\n"+funcCode+"\r\n\r\n// Compiled: \r\n"+newCode+"\r\n");return codeGenerator(newCode)};compile.rootName="Wind";var isCommonJS=!!(typeof require==="function"&&typeof module!=="undefined"&&module.exports);var isAmd=!!(typeof require==="function"&&typeof define==="function"&&define.amd);var defineModule=function(){Wind.define({name:"compiler",version:"0.7.2",require:isCommonJS&&require,dependencies:{core:"~0.7.0"},init:function(){Wind.parse=parse;Wind.compile=compile}})};if(isCommonJS){try{Wind=require("./wind-core")}catch(ex){Wind=require("wind-core")}defineModule()}else{if(isAmd){require(["wind-core"],function(wind){Wind=wind;defineModule()})}else{var Fn=Function,global=Fn("return this")();if(!global.Wind){throw new Error('Missing the root object, please load "wind" component first.')}Wind=global.Wind;defineModule()}}})();(function(){var BuilderBase=function(){};BuilderBase.prototype={For:function(condition,update,body){return{next:function(_this,callback){var loop=function(skipUpdate){try{if(update&&!skipUpdate){update.call(_this)}if(!condition||condition.call(_this)){body.next(_this,function(type,value,target){if(type=="normal"||type=="continue"){loop(false)}else{if(type=="throw"||type=="return"){callback(type,value)}else{if(type=="break"){callback("normal")}else{throw new Error('Invalid type for "Loop": '+type)}}}})}else{callback("normal")}}catch(ex){callback("throw",ex)}};loop(true)}}},ForIn:function(obj,bodyGenerator){return{next:function(_this,callback){var keys=[];for(var k in obj){keys.push(k)}var loop=function(i){try{if(i<keys.length){var body=bodyGenerator(keys[i]);body.next(_this,function(type,value,target){if(type=="normal"||type=="continue"){loop(i+1)}else{if(type=="throw"||type=="return"){callback(type,value)}else{if(type=="break"){callback("normal")}else{throw new Error('Invalid type for "Loop": '+type)}}}})}else{callback("normal")}}catch(ex){callback("throw",ex)}};loop(0)}}},While:function(condition,body){return{next:function(_this,callback){var loop=function(){try{if(condition.call(_this)){body.next(_this,function(type,value,target){if(type=="normal"||type=="continue"){loop()}else{if(type=="throw"||type=="return"){callback(type,value)}else{if(type=="break"){callback("normal")}else{throw new Error('Invalid type for "Loop": '+type)}}}})}else{callback("normal")}}catch(ex){callback("throw",ex)}};loop()}}},Do:function(body,condition){return{next:function(_this,callback){var loop=function(){body.next(_this,function(type,value,target){if(type=="normal"||type=="continue"){try{if(condition.call(_this)){loop()}else{callback("normal")}}catch(ex){callback("throw",ex)}}else{if(type=="throw"||type=="return"){callback(type,value)}else{if(type=="break"){callback("normal")}else{throw new Error('Invalid type for "Loop": '+type)}}}})};loop()}}},Delay:function(generator){return{next:function(_this,callback){try{var step=generator.call(_this);step.next(_this,callback)}catch(ex){callback("throw",ex)}}}},Combine:function(s1,s2){return{next:function(_this,callback){s1.next(_this,function(type,value,target){if(type=="normal"){try{s2.next(_this,callback)}catch(ex){callback("throw",ex)}}else{callback(type,value,target)}})}}},Return:function(result){return{next:function(_this,callback){callback("return",result)}}},Normal:function(){return{next:function(_this,callback){callback("normal")}}},Break:function(){return{next:function(_this,callback){callback("break")}}},Continue:function(){return{next:function(_this,callback){callback("continue")}}},Throw:function(ex){return{next:function(_this,callback){callback("throw",ex)}}},Try:function(tryTask,catchGenerator,finallyStep){return{next:function(_this,callback){tryTask.next(_this,function(type,value,target){if(type!="throw"||!catchGenerator){if(!finallyStep){callback(type,value,target)}else{finallyStep.next(_this,function(finallyType,finallyValue,finallyTarget){if(finallyType=="normal"){callback(type,value,target)}else{callback(finallyType,finallyValue,finallyTarget)}})}}else{if(catchGenerator){var catchTask;try{catchTask=catchGenerator.call(_this,value)}catch(ex){if(finallyStep){finallyStep.next(_this,function(finallyType,finallyValue,finallyTarget){if(finallyType=="normal"){callback("throw",ex)}else{callback(finallyType,finallyValue,finallyTarget)}})}else{callback("throw",ex)}}if(catchTask){catchTask.next(_this,function(catchType,catchValue,catchTarget){if(catchType=="throw"){if(finallyStep){finallyStep.next(_this,function(finallyType,finallyValue,finallyTarget){if(finallyType=="normal"){callback(catchType,catchValue,catchTarget)}else{callback(finallyType,finallyValue,finallyTarget)}})}else{callback(catchType,catchValue,catchTarget)}}else{if(finallyStep){finallyStep.next(_this,function(finallyType,finallyValue,finallyTarget){if(finallyType=="normal"){callback(catchType,catchValue,catchTarget)}else{callback(finallyType,finallyValue,finallyTarget)}})}else{callback(catchType,catchValue,catchTarget)}}})}}else{finallyStep.next(_this,function(finallyType,finallyValue,finallyTarget){if(finallyType=="normal"){callback(type,value,target)}else{callback(finallyType,finallyValue,finallyTarget)}})}}})}}}};var isCommonJS=!!(typeof require==="function"&&typeof module!=="undefined"&&module.exports);var isAmd=!!(typeof require==="function"&&typeof define==="function"&&define.amd);var Wind;var defineModule=function(){Wind.define({name:"builderbase",version:"0.7.0",require:isCommonJS&&require,dependencies:{core:"~0.7.0"},init:function(){Wind.BuilderBase=BuilderBase}})};if(isCommonJS){try{Wind=require("./wind-core")}catch(ex){Wind=require("wind-core")}defineModule()}else{if(isAmd){require(["wind-core"],function(wind){Wind=wind;defineModule()})}else{var Fn=Function,global=Fn("return this")();if(!global.Wind){throw new Error('Missing the root object, please load "wind" component first.')}Wind=global.Wind;defineModule()}}})();(function(){var supportDefineProperty=(function(){var i=0;var getter=function(){if(i===0){throw new Error("Execute too soon.")}return i};var obj={};try{Object.defineProperty(obj,"value",{get:getter});i=1;return obj.value===1}catch(ex){return false}})();var Wind,_;var Async={};var CanceledErrorTypeID="670a1076-712b-4edd-9b70-64b152fe1cd9";var isCanceledError=function(ex){return ex._typeId==CanceledErrorTypeID};var CanceledError=Async.CanceledError=function(){};CanceledError.prototype={isTypeOf:isCanceledError,_typeId:CanceledErrorTypeID,message:"The task has been cancelled."};var AggregateErrorTypeID="4a73efb8-c2e2-4305-a05c-72385288650a";var AggregateError=Async.AggregateError=function(errors){this.children=[];if(errors){for(var i=0;i<errors.length;i++){this.children.push(errors[i])}}};AggregateError.prototype={_typeId:AggregateErrorTypeID,message:"This is an error contains sub-errors, please check the 'children' collection for more details.",isTypeOf:function(ex){return ex._typeId==AggregateErrorTypeID}};var CancellationToken=Async.CancellationToken=function(){};CancellationToken.prototype={register:function(handler){if(this.isCancellationRequested){handler()}if(!this._handlers){this._handlers=[]}this._handlers.push(handler)},unregister:function(handler){if(!this._handlers){return}var index=this._handlers.indexOf(handler);if(index>=0){this._handlers.splice(index,1)}},cancel:function(){if(this.isCancellationRequested){return}this.isCancellationRequested=true;var handlers=this._handlers;delete this._handlers;for(var i=0;i<handlers.length;i++){try{handlers[i]()}catch(ex){Wind.logger.warn("Cancellation handler threw an error: "+ex)}}},throwIfCancellationRequested:function(){if(this.isCancellationRequested){throw new CanceledError()}}};var EventManager=function(){this._listeners={};this._firing=null};EventManager.prototype={add:function(name,listener){if(this._firing===name){var self=this;setTimeout(function(){self.add(name,listener)},0);return}var eventListeners=this._listeners[name];if(!eventListeners){eventListeners=this._listeners[name]=[]}eventListeners.push(listener)},remove:function(name,listener){if(this._firing===name){var self=this;setTimeout(function(){self.remove(name,listener)},0);return}var eventListeners=this._listeners[name];if(!eventListeners){return}var index=eventListeners.indexOf(listener);if(index>=0){eventListeners.splice(index,1)}},fire:function(name,self,args){var listeners=this._listeners[name];if(!listeners){return}this._firing=name;for(var i=0;i<listeners.length;i++){try{listeners[i].call(self,args)}catch(ex){Wind.logger.warn('An error occurred in "'+name+' listener": '+ex)}}this._firing=null}};var taskEventManager=new EventManager();var Task=Async.Task=function(delegate){this._delegate=delegate;this._eventManager=new EventManager();this.status="ready"};Task.prototype={start:function(){if(this.status!="ready"){throw new Error('Task can only be started in "ready" status.')}this.status="running";try{this._delegate(this)}catch(ex){if(this.status=="running"){this.complete("failure",ex)}else{Wind.logger.warn("An unexpected error occurred after the task is completed: "+ex)}}return this},complete:function(type,value){if(type!=="success"&&type!=="failure"){throw new Error("Unsupported type: "+type)}if(this.status!="running"){throw new Error('The "complete" method can only be called in "running" status.')}var eventManager=this._eventManager;this._eventManager=null;if(type==="success"){this.status="succeeded";if(supportDefineProperty){this._result=value}else{this.result=value}eventManager.fire("success",this)}else{if(isCanceledError(value)){this.status="canceled"}else{this.status="faulted"}if(supportDefineProperty){this._error=value}else{this.error=value}eventManager.fire("failure",this)}eventManager.fire("complete",this);if(type!=="failure"){return}if(!supportDefineProperty){return}if(this._errorObserved){return}var self=this;this._unobservedTimeoutToken=setTimeout(function(){self._handleUnobservedError(value)},Task.unobservedTimeout)},observeError:function(){if(this.status==="ready"||this.status==="running"){throw new Error("The method could only be called when it's completed.")}if(!supportDefineProperty){return this.error}var token=this._unobservedTimeoutToken;if(token){clearTimeout(token);this._unobservedTimeoutToken=null}this._errorObserved=true;return this._error},_handleUnobservedError:function(error){this._unobservedTimeoutToken=null;var args={task:this,error:error,observed:false};taskEventManager.fire("unobservedError",Task,args);if(!args.observed){throw error}},then:function(nextGenerator){var firstTask=this;return Task.create(function(t){var nextOnComplete=function(){if(this.error){t.complete("failure",this.error)}else{t.complete("success",this.result)}};var processNext=function(nextTask){if(nextTask.status=="ready"){nextTask.start()}if(nextTask.status=="running"){nextTask.on("complete",nextOnComplete)}else{nextOnComplete.call(nextTask)}};var firstOnComplete=function(){if(this.error){return t.complete("failure",this.error)}var nextTask;try{nextTask=nextGenerator(this.result)}catch(ex){return t.complete("failure",ex)}processNext(nextTask)};if(firstTask.status=="ready"){firstTask.start()}if(firstTask.status=="running"){firstTask.on("complete",firstOnComplete)}else{firstOnComplete.call(firstTask)}})}};Task.prototype.on=Task.prototype.addEventListener=function(){var eventManager=this._eventManager;if(!eventManager){throw new Error("Cannot add event listeners when the task is complete.")}eventManager.add.apply(eventManager,arguments);return this};Task.prototype.off=Task.prototype.removeEventListener=function(){var eventManager=this._eventManager;if(!eventManager){throw new Error("All the event listeners have been removed when the task was complete.")}eventManager.remove.apply(eventManager,arguments);return this};if(supportDefineProperty){Object.defineProperty(Task.prototype,"error",{get:function(){return this.observeError()}});Object.defineProperty(Task.prototype,"result",{get:function(){var error=this.observeError();if(error){throw error}return this._result}})}var observeErrorListener=function(){this.observeError()};Task.on=Task.addEventListener=function(){taskEventManager.add.apply(taskEventManager,arguments)};Task.off=Task.removeEventListener=function(name,listener){taskEventManager.remove.apply(taskEventManager,arguments)};Task.unobservedTimeout=10*1000;var isTask=Task.isTask=function(t){return t&&(typeof t.start==="function")&&(typeof t.addEventListener)==="function"&&(typeof t.removeEventListener)==="function"&&(typeof t.complete)==="function"};var create=Task.create=function(delegate){return new Task(delegate)};var whenAll=Task.whenAll=function(){var inputTasks;if(arguments.length==1){var arg=arguments[0];if(isTask(arg)){inputTasks=[arg]}else{inputTasks=arg}}else{inputTasks=new Array(arguments.length);for(var i=0;i<arguments.length;i++){inputTasks[i]=arguments[i]}}return create(function(taskWhenAll){var errors;var done=function(){if(errors){taskWhenAll.complete("failure",new AggregateError(errors))}else{var results=_.map(inputTasks,function(t){return t.result});taskWhenAll.complete("success",results)}};var runningNumber=0;_.each(inputTasks,function(key,task){if(!task){return}if(!isTask(task)){inputTasks[key]=task=whenAll(task)}if(task.status==="ready"){task.start()}if(task.status==="running"){runningNumber++;task.addEventListener("complete",function(){if(this.status!=="succeeded"){if(!errors){errors=[]}errors.push(this.error)}if(--runningNumber==0){done()}})}else{if(task.status==="faulted"||task.status==="canceled"){if(!errors){errors=[]}errors.push(task.error)}}});if(runningNumber==0){done()}})};var whenAny=Task.whenAny=function(){var inputTasks={};var isArray=true;if(arguments.length==1){var arg=arguments[0];if(isTask(arg)){inputTasks[0]=arg}else{isArray=_.isArray(arg);_.each(arg,function(key,task){if(isTask(task)){inputTasks[key]=task}})}}else{for(var i=0;i<arguments.length;i++){var task=arguments[i];if(isTask(task)){inputTasks[i]=task}}}var processKey=isArray?function(key){return parseInt(key,10)}:function(key){return key};return create(function(taskWhenAny){if(_.isEmpty(inputTasks)){return taskWhenAny.complete("failure","There's no valid input tasks.")}var result;_.each(inputTasks,function(key,task){if(task.status==="ready"){task.start()}if(task.status!=="running"){task.observeError();if(!result){result={key:processKey(key),task:task}}}});if(result){_.each(inputTasks,function(key,task){if(task.status==="running"){task.on("failure",observeErrorListener)}});return taskWhenAny.complete("success",result)}var onComplete=function(){this.observeError();var taskCompleted=this;var keyCompleted;_.each(inputTasks,function(key,task){if(taskCompleted===task){keyCompleted=key;return}task.off("complete",onComplete);task.on("failure",observeErrorListener)});taskWhenAny.complete("success",{key:processKey(keyCompleted),task:this})};_.each(inputTasks,function(task){task.addEventListener("complete",onComplete)})})};var sleep=Async.sleep=function(delay,ct){return Task.create(function(t){if(ct&&ct.isCancellationRequested){t.complete("failure",new CanceledError())}var seed;var cancelHandler;if(ct){cancelHandler=function(){clearTimeout(seed);t.complete("failure",new CanceledError())}}var seed=setTimeout(function(){if(ct){ct.unregister(cancelHandler)}t.complete("success")},delay);if(ct){ct.register(cancelHandler)}})};var onEvent=Async.onEvent=function(target,eventName,ct){return Task.create(function(t){if(ct&&ct.isCancellationRequested){t.complete("failure",new CanceledError())}var cleanUp=function(){if(target.removeEventListener){target.removeEventListener(eventName,eventHandler)}else{if(target.removeListener){target.removeListener(eventName,eventHandler)}else{target.detachEvent(eventName,eventHandler)}}};var eventHandler;var cancelHandler;if(ct){cancelHandler=function(){cleanUp();t.complete("failure",new CanceledError())}}var eventHandler=function(ev){if(ct){ct.unregister(cancelHandler)}cleanUp();t.complete("success",ev)};if(target.addEventListener){target.addEventListener(eventName,eventHandler)}else{if(target.addListener){target.addListener(eventName,eventHandler)}else{target.attachEvent(eventName,eventHandler)}}if(ct){ct.register(cancelHandler)}})};var AsyncBuilder=Async.AsyncBuilder=function(){};AsyncBuilder.prototype={Start:function(_this,task){return Task.create(function(t){task.next(_this,function(type,value,target){if(type=="normal"||type=="return"){t.complete("success",value)}else{if(type=="throw"){t.complete("failure",value)}else{throw new Error("Unsupported type: "+type)}}})})},Bind:function(task,generator){return{next:function(_this,callback){var onComplete=function(){if(this.error){callback("throw",this.error)}else{var nextTask;try{nextTask=generator.call(_this,this.result)}catch(ex){callback("throw",ex);return}nextTask.next(_this,callback)}};if(task.status=="ready"){task.addEventListener("complete",onComplete);task.start()}else{if(task.status=="running"){task.addEventListener("complete",onComplete)}else{onComplete.call(task)}}}}}};var Binding=Async.Binding={};var collectArgs=function(args,requiredArgs){var result=[];for(var i=0;i<args.length;i++){result.push(args[i])}while(result.length<requiredArgs){result.push(undefined)}return result};var collectCallbackArgNames=function(args){if(args.length<=1){return null}var result=[];for(var i=1;i<args.length;i++){result.push(args[i])}return result};var fromStandard=Binding.fromStandard=function(fn){var callbackArgNames=collectCallbackArgNames(arguments);return function(){var _this=this;var args=collectArgs(arguments,fn.length-1);return Task.create(function(t){args.push(function(error,result){if(error){t.complete("failure",error)}else{if(!callbackArgNames){t.complete("success",result)}else{var data={};for(var i=0;i<callbackArgNames.length;i++){data[callbackArgNames[i]]=arguments[i+1]}return t.complete("success",data)}}});fn.apply(_this,args)})}};var fromCallback=Binding.fromCallback=function(fn){var callbackArgNames=collectCallbackArgNames(arguments);return function(){var _this=this;var args=collectArgs(arguments,fn.length-1);return Task.create(function(t){args.push(function(result){if(callbackArgNames){var data={};for(var i=0;i<callbackArgNames.length;i++){data[callbackArgNames[i]]=arguments[i]}t.complete("success",data)}else{t.complete("success",result)}});fn.apply(_this,args)})}};var isCommonJS=!!(typeof require==="function"&&typeof module!=="undefined"&&module.exports);var isAmd=!!(typeof require==="function"&&typeof define==="function"&&define.amd);var defineModule=function(){Wind.define({name:"async",version:"0.7.1",require:isCommonJS&&require,autoloads:["builderbase"],dependencies:{builderbase:"~0.7.0"},init:function(){_=Wind._;_.each(Wind.BuilderBase.prototype,function(m,fn){AsyncBuilder.prototype[m]=fn});Wind.Async=Async;Wind.binders.async="$await";Wind.builders.async=new AsyncBuilder()}})};if(isCommonJS){try{Wind=require("./wind-core")}catch(ex){Wind=require("wind-core")}defineModule()}else{if(isAmd){require(["wind-core"],function(wind){Wind=wind;defineModule()})}else{var Fn=Function,global=Fn("return this")();if(!global.Wind){throw new Error('Missing the root object, please load "wind" component first.')}Wind=global.Wind;defineModule()}}})();(function(){var Wind;var defaultCreate=function(){throw new Error('Please set "Wind.Promise.create" to provide a factory method for creating a promise object.')};var PromiseBuilder=function(){};PromiseBuilder.prototype={Start:function(_this,task){return Wind.Promise.create(function(complete,error){task.next(_this,function(type,value,target){if(type=="normal"||type=="return"){complete(value)}else{if(type=="throw"){error(value)}else{throw new Error("Unsupported type: "+type)}}})})},Bind:function(promise,generator){return{next:function(_this,callback){promise.then(function(result){var nextTask;try{nextTask=generator.call(_this,result)}catch(ex){return callback("throw",ex)}nextTask.next(_this,callback)},function(error){callback("throw",error)})}}}};var isCommonJS=!!(typeof require==="function"&&typeof module!=="undefined"&&module.exports);var isAmd=!!(typeof require==="function"&&typeof define==="function"&&define.amd);var defineModule=function(){Wind.define({name:"promise",version:"0.7.0",require:isCommonJS&&require,autoloads:["builderbase"],dependencies:{builderbase:"~0.7.0"},init:function(){Wind._.each(Wind.BuilderBase.prototype,function(m,fn){PromiseBuilder.prototype[m]=fn});if(!Wind.Promise){Wind.Promise={}}Wind.Promise.create=defaultCreate;Wind.binders.promise="$await";Wind.builders.promise=new PromiseBuilder()}})};if(isCommonJS){try{Wind=require("./wind-core")}catch(ex){Wind=require("wind-core")}defineModule()}else{if(isAmd){require(["wind-core"],function(wind){Wind=wind;defineModule()})}else{var Fn=Function,global=Fn("return this")();if(!global.Wind){throw new Error('Missing the root object, please load "wind" component first.')}Wind=global.Wind;defineModule()}}})();


/*!	SWFObject v2.2 <http://code.google.com/p/swfobject/>
 is released under the MIT License <http://www.opensource.org/licenses/mit-license.php>
 */
;var swfobject=swfobject||function(){var D="undefined",r="object",S="Shockwave Flash",W="ShockwaveFlash.ShockwaveFlash",q="application/x-shockwave-flash",R="SWFObjectExprInst",x="onreadystatechange",O=window,j=document,t=navigator,T=false,U=[h],o=[],N=[],I=[],l,Q,E,B,J=false,a=false,n,G,m=true,M=function(){var aa=typeof j.getElementById!=D&&typeof j.getElementsByTagName!=D&&typeof j.createElement!=D,ah=t.userAgent.toLowerCase(),Y=t.platform.toLowerCase(),ae=Y?/win/.test(Y):/win/.test(ah),ac=Y?/mac/.test(Y):/mac/.test(ah),af=/webkit/.test(ah)?parseFloat(ah.replace(/^.*webkit\/(\d+(\.\d+)?).*$/,"$1")):false,X=!+"\v1",ag=[0,0,0],ab=null;if(typeof t.plugins!=D&&typeof t.plugins[S]==r){ab=t.plugins[S].description;if(ab&&!(typeof t.mimeTypes!=D&&t.mimeTypes[q]&&!t.mimeTypes[q].enabledPlugin)){T=true;X=false;ab=ab.replace(/^.*\s+(\S+\s+\S+$)/,"$1");ag[0]=parseInt(ab.replace(/^(.*)\..*$/,"$1"),10);ag[1]=parseInt(ab.replace(/^.*\.(.*)\s.*$/,"$1"),10);ag[2]=/[a-zA-Z]/.test(ab)?parseInt(ab.replace(/^.*[a-zA-Z]+(.*)$/,"$1"),10):0}}else{if(typeof O.ActiveXObject!=D){try{var ad=new ActiveXObject(W);if(ad){ab=ad.GetVariable("$version");if(ab){X=true;ab=ab.split(" ")[1].split(",");ag=[parseInt(ab[0],10),parseInt(ab[1],10),parseInt(ab[2],10)]}}}catch(Z){}}}return{w3:aa,pv:ag,wk:af,ie:X,win:ae,mac:ac}}(),k=function(){if(!M.w3){return}if((typeof j.readyState!=D&&j.readyState=="complete")||(typeof j.readyState==D&&(j.getElementsByTagName("body")[0]||j.body))){f()}if(!J){if(typeof j.addEventListener!=D){j.addEventListener("DOMContentLoaded",f,false)}if(M.ie&&M.win){j.attachEvent(x,function(){if(j.readyState=="complete"){j.detachEvent(x,arguments.callee);f()}});if(O==top){(function(){if(J){return}try{j.documentElement.doScroll("left")}catch(X){setTimeout(arguments.callee,0);return}f()})()}}if(M.wk){(function(){if(J){return}if(!/loaded|complete/.test(j.readyState)){setTimeout(arguments.callee,0);return}f()})()}s(f)}}();function f(){if(J){return}try{var Z=j.getElementsByTagName("body")[0].appendChild(C("span"));Z.parentNode.removeChild(Z)}catch(aa){return}J=true;var X=U.length;for(var Y=0;Y<X;Y++){U[Y]()}}function K(X){if(J){X()}else{U[U.length]=X}}function s(Y){if(typeof O.addEventListener!=D){O.addEventListener("load",Y,false)}else{if(typeof j.addEventListener!=D){j.addEventListener("load",Y,false)}else{if(typeof O.attachEvent!=D){i(O,"onload",Y)}else{if(typeof O.onload=="function"){var X=O.onload;O.onload=function(){X();Y()}}else{O.onload=Y}}}}}function h(){if(T){V()}else{H()}}function V(){var X=j.getElementsByTagName("body")[0];var aa=C(r);aa.setAttribute("type",q);var Z=X.appendChild(aa);if(Z){var Y=0;(function(){if(typeof Z.GetVariable!=D){var ab=Z.GetVariable("$version");if(ab){ab=ab.split(" ")[1].split(",");M.pv=[parseInt(ab[0],10),parseInt(ab[1],10),parseInt(ab[2],10)]}}else{if(Y<10){Y++;setTimeout(arguments.callee,10);return}}X.removeChild(aa);Z=null;H()})()}else{H()}}function H(){var ag=o.length;if(ag>0){for(var af=0;af<ag;af++){var Y=o[af].id;var ab=o[af].callbackFn;var aa={success:false,id:Y};if(M.pv[0]>0){var ae=c(Y);if(ae){if(F(o[af].swfVersion)&&!(M.wk&&M.wk<312)){w(Y,true);if(ab){aa.success=true;aa.ref=z(Y);ab(aa)}}else{if(o[af].expressInstall&&A()){var ai={};ai.data=o[af].expressInstall;ai.width=ae.getAttribute("width")||"0";ai.height=ae.getAttribute("height")||"0";if(ae.getAttribute("class")){ai.styleclass=ae.getAttribute("class")}if(ae.getAttribute("align")){ai.align=ae.getAttribute("align")}var ah={};var X=ae.getElementsByTagName("param");var ac=X.length;for(var ad=0;ad<ac;ad++){if(X[ad].getAttribute("name").toLowerCase()!="movie"){ah[X[ad].getAttribute("name")]=X[ad].getAttribute("value")}}P(ai,ah,Y,ab)}else{p(ae);if(ab){ab(aa)}}}}}else{w(Y,true);if(ab){var Z=z(Y);if(Z&&typeof Z.SetVariable!=D){aa.success=true;aa.ref=Z}ab(aa)}}}}}function z(aa){var X=null;var Y=c(aa);if(Y&&Y.nodeName=="OBJECT"){if(typeof Y.SetVariable!=D){X=Y}else{var Z=Y.getElementsByTagName(r)[0];if(Z){X=Z}}}return X}function A(){return !a&&F("6.0.65")&&(M.win||M.mac)&&!(M.wk&&M.wk<312)}function P(aa,ab,X,Z){a=true;E=Z||null;B={success:false,id:X};var ae=c(X);if(ae){if(ae.nodeName=="OBJECT"){l=g(ae);Q=null}else{l=ae;Q=X}aa.id=R;if(typeof aa.width==D||(!/%$/.test(aa.width)&&parseInt(aa.width,10)<310)){aa.width="310"}if(typeof aa.height==D||(!/%$/.test(aa.height)&&parseInt(aa.height,10)<137)){aa.height="137"}j.title=j.title.slice(0,47)+" - Flash Player Installation";var ad=M.ie&&M.win?"ActiveX":"PlugIn",ac="MMredirectURL="+encodeURI(window.location).toString().replace(/&/g,"%26")+"&MMplayerType="+ad+"&MMdoctitle="+j.title;if(typeof ab.flashvars!=D){ab.flashvars+="&"+ac}else{ab.flashvars=ac}if(M.ie&&M.win&&ae.readyState!=4){var Y=C("div");X+="SWFObjectNew";Y.setAttribute("id",X);ae.parentNode.insertBefore(Y,ae);ae.style.display="none";(function(){if(ae.readyState==4){ae.parentNode.removeChild(ae)}else{setTimeout(arguments.callee,10)}})()}u(aa,ab,X)}}function p(Y){if(M.ie&&M.win&&Y.readyState!=4){var X=C("div");Y.parentNode.insertBefore(X,Y);X.parentNode.replaceChild(g(Y),X);Y.style.display="none";(function(){if(Y.readyState==4){Y.parentNode.removeChild(Y)}else{setTimeout(arguments.callee,10)}})()}else{Y.parentNode.replaceChild(g(Y),Y)}}function g(ab){var aa=C("div");if(M.win&&M.ie){aa.innerHTML=ab.innerHTML}else{var Y=ab.getElementsByTagName(r)[0];if(Y){var ad=Y.childNodes;if(ad){var X=ad.length;for(var Z=0;Z<X;Z++){if(!(ad[Z].nodeType==1&&ad[Z].nodeName=="PARAM")&&!(ad[Z].nodeType==8)){aa.appendChild(ad[Z].cloneNode(true))}}}}}return aa}function u(ai,ag,Y){var X,aa=c(Y);if(M.wk&&M.wk<312){return X}if(aa){if(typeof ai.id==D){ai.id=Y}if(M.ie&&M.win){var ah="";for(var ae in ai){if(ai[ae]!=Object.prototype[ae]){if(ae.toLowerCase()=="data"){ag.movie=ai[ae]}else{if(ae.toLowerCase()=="styleclass"){ah+=' class="'+ai[ae]+'"'}else{if(ae.toLowerCase()!="classid"){ah+=" "+ae+'="'+ai[ae]+'"'}}}}}var af="";for(var ad in ag){if(ag[ad]!=Object.prototype[ad]){af+='<param name="'+ad+'" value="'+ag[ad]+'" />'}}aa.outerHTML='<object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000"'+ah+">"+af+"</object>";N[N.length]=ai.id;X=c(ai.id)}else{var Z=C(r);Z.setAttribute("type",q);for(var ac in ai){if(ai[ac]!=Object.prototype[ac]){if(ac.toLowerCase()=="styleclass"){Z.setAttribute("class",ai[ac])}else{if(ac.toLowerCase()!="classid"){Z.setAttribute(ac,ai[ac])}}}}for(var ab in ag){if(ag[ab]!=Object.prototype[ab]&&ab.toLowerCase()!="movie"){e(Z,ab,ag[ab])}}aa.parentNode.replaceChild(Z,aa);X=Z}}return X}function e(Z,X,Y){var aa=C("param");aa.setAttribute("name",X);aa.setAttribute("value",Y);Z.appendChild(aa)}function y(Y){var X=c(Y);if(X&&X.nodeName=="OBJECT"){if(M.ie&&M.win){X.style.display="none";(function(){if(X.readyState==4){b(Y)}else{setTimeout(arguments.callee,10)}})()}else{X.parentNode.removeChild(X)}}}function b(Z){var Y=c(Z);if(Y){for(var X in Y){if(typeof Y[X]=="function"){Y[X]=null}}Y.parentNode.removeChild(Y)}}function c(Z){var X=null;try{X=j.getElementById(Z)}catch(Y){}return X}function C(X){return j.createElement(X)}function i(Z,X,Y){Z.attachEvent(X,Y);I[I.length]=[Z,X,Y]}function F(Z){var Y=M.pv,X=Z.split(".");X[0]=parseInt(X[0],10);X[1]=parseInt(X[1],10)||0;X[2]=parseInt(X[2],10)||0;return(Y[0]>X[0]||(Y[0]==X[0]&&Y[1]>X[1])||(Y[0]==X[0]&&Y[1]==X[1]&&Y[2]>=X[2]))?true:false}function v(ac,Y,ad,ab){if(M.ie&&M.mac){return}var aa=j.getElementsByTagName("head")[0];if(!aa){return}var X=(ad&&typeof ad=="string")?ad:"screen";if(ab){n=null;G=null}if(!n||G!=X){var Z=C("style");Z.setAttribute("type","text/css");Z.setAttribute("media",X);n=aa.appendChild(Z);if(M.ie&&M.win&&typeof j.styleSheets!=D&&j.styleSheets.length>0){n=j.styleSheets[j.styleSheets.length-1]}G=X}if(M.ie&&M.win){if(n&&typeof n.addRule==r){n.addRule(ac,Y)}}else{if(n&&typeof j.createTextNode!=D){n.appendChild(j.createTextNode(ac+" {"+Y+"}"))}}}function w(Z,X){if(!m){return}var Y=X?"visible":"hidden";if(J&&c(Z)){c(Z).style.visibility=Y}else{v("#"+Z,"visibility:"+Y)}}function L(Y){var Z=/[\\\"<>\.;]/;var X=Z.exec(Y)!=null;return X&&typeof encodeURIComponent!=D?encodeURIComponent(Y):Y}var d=function(){if(M.ie&&M.win){window.attachEvent("onunload",function(){var ac=I.length;for(var ab=0;ab<ac;ab++){I[ab][0].detachEvent(I[ab][1],I[ab][2])}var Z=N.length;for(var aa=0;aa<Z;aa++){y(N[aa])}for(var Y in M){M[Y]=null}M=null;for(var X in swfobject){swfobject[X]=null}swfobject=null})}}();return{registerObject:function(ab,X,aa,Z){if(M.w3&&ab&&X){var Y={};Y.id=ab;Y.swfVersion=X;Y.expressInstall=aa;Y.callbackFn=Z;o[o.length]=Y;w(ab,false)}else{if(Z){Z({success:false,id:ab})}}},getObjectById:function(X){if(M.w3){return z(X)}},embedSWF:function(ab,ah,ae,ag,Y,aa,Z,ad,af,ac){var X={success:false,id:ah};if(M.w3&&!(M.wk&&M.wk<312)&&ab&&ah&&ae&&ag&&Y){w(ah,false);K(function(){ae+="";ag+="";var aj={};if(af&&typeof af===r){for(var al in af){aj[al]=af[al]}}aj.data=ab;aj.width=ae;aj.height=ag;var am={};if(ad&&typeof ad===r){for(var ak in ad){am[ak]=ad[ak]}}if(Z&&typeof Z===r){for(var ai in Z){if(typeof am.flashvars!=D){am.flashvars+="&"+ai+"="+Z[ai]}else{am.flashvars=ai+"="+Z[ai]}}}if(F(Y)){var an=u(aj,am,ah);if(aj.id==ah){w(ah,true)}X.success=true;X.ref=an}else{if(aa&&A()){aj.data=aa;P(aj,am,ah,ac);return}else{w(ah,true)}}if(ac){ac(X)}})}else{if(ac){ac(X)}}},switchOffAutoHideShow:function(){m=false},ua:M,getFlashPlayerVersion:function(){return{major:M.pv[0],minor:M.pv[1],release:M.pv[2]}},hasFlashPlayerVersion:F,createSWF:function(Z,Y,X){if(M.w3){return u(Z,Y,X)}else{return undefined}},showExpressInstall:function(Z,aa,X,Y){if(M.w3&&A()){P(Z,aa,X,Y)}},removeSWF:function(X){if(M.w3){y(X)}},createCSS:function(aa,Z,Y,X){if(M.w3){v(aa,Z,Y,X)}},addDomLoadEvent:K,addLoadEvent:s,getQueryParamValue:function(aa){var Z=j.location.search||j.location.hash;if(Z){if(/\?/.test(Z)){Z=Z.split("?")[1]}if(aa==null){return L(Z)}var Y=Z.split("&");for(var X=0;X<Y.length;X++){if(Y[X].substring(0,Y[X].indexOf("="))==aa){return L(Y[X].substring((Y[X].indexOf("=")+1)))}}}return""},expressInstallCallback:function(){if(a){var X=c(R);if(X&&l){X.parentNode.replaceChild(l,X);if(Q){w(Q,true);if(M.ie&&M.win){l.style.display="block"}}if(E){E(B)}}a=false}}}}();


// JSON2 min
//if(!this.JSON){JSON={}}(function(){function f(n){return n<10?"0"+n:n}if(typeof Date.prototype.toJSON!=="function"){Date.prototype.toJSON=function(key){return this.getUTCFullYear()+"-"+f(this.getUTCMonth()+1)+"-"+f(this.getUTCDate())+"T"+f(this.getUTCHours())+":"+f(this.getUTCMinutes())+":"+f(this.getUTCSeconds())+"Z"};String.prototype.toJSON=Number.prototype.toJSON=Boolean.prototype.toJSON=function(key){return this.valueOf()}}var cx=/[\u0000\u00ad\u0600-\u0604\u070f\u17b4\u17b5\u200c-\u200f\u2028-\u202f\u2060-\u206f\ufeff\ufff0-\uffff]/g,escapable=/[\\\"\x00-\x1f\x7f-\x9f\u00ad\u0600-\u0604\u070f\u17b4\u17b5\u200c-\u200f\u2028-\u202f\u2060-\u206f\ufeff\ufff0-\uffff]/g,gap,indent,meta={"\b":"\\b","\t":"\\t","\n":"\\n","\f":"\\f","\r":"\\r",'"':'\\"',"\\":"\\\\"},rep;function quote(string){escapable.lastIndex=0;return escapable.test(string)?'"'+string.replace(escapable,function(a){var c=meta[a];if(typeof c==="string"){return c}return"\\u"+("0000"+a.charCodeAt(0).toString(16)).slice(-4)})+'"':'"'+string+'"'}function str(key,holder){var i,k,v,length,mind=gap,partial,value=holder[key];if(value&&typeof value==="object"&&typeof value.toJSON==="function"){value=value.toJSON(key)}if(typeof rep==="function"){value=rep.call(holder,key,value)}switch(typeof value){case"string":return quote(value);case"number":return isFinite(value)?String(value):"null";case"boolean":case"null":return String(value);case"object":if(!value){return"null"}gap+=indent;partial=[];if(Object.prototype.toString.apply(value)==="[object Array]"){length=value.length;for(i=0;i<length;i+=1){partial[i]=str(i,value)||"null"}v=partial.length===0?"[]":gap?"[\n"+gap+partial.join(",\n"+gap)+"\n"+mind+"]":"["+partial.join(",")+"]";gap=mind;return v}if(rep&&typeof rep==="object"){length=rep.length;for(i=0;i<length;i+=1){k=rep[i];if(typeof k==="string"){v=str(k,value);if(v){partial.push(quote(k)+(gap?": ":":")+v)}}}}else{for(k in value){if(Object.hasOwnProperty.call(value,k)){v=str(k,value);if(v){partial.push(quote(k)+(gap?": ":":")+v)}}}}v=partial.length===0?"{}":gap?"{\n"+gap+partial.join(",\n"+gap)+"\n"+mind+"}":"{"+partial.join(",")+"}";gap=mind;return v}}if(typeof JSON.stringify!=="function"){JSON.stringify=function(value,replacer,space){var i;gap="";indent="";if(typeof space==="number"){for(i=0;i<space;i+=1){indent+=" "}}else{if(typeof space==="string"){indent=space}}rep=replacer;if(replacer&&typeof replacer!=="function"&&(typeof replacer!=="object"||typeof replacer.length!=="number")){throw new Error("JSON.stringify")}return str("",{"":value})}}if(typeof JSON.parse!=="function"){JSON.parse=function(text,reviver){var j;function walk(holder,key){var k,v,value=holder[key];if(value&&typeof value==="object"){for(k in value){if(Object.hasOwnProperty.call(value,k)){v=walk(value,k);if(v!==undefined){value[k]=v}else{delete value[k]}}}}return reviver.call(holder,key,value)}cx.lastIndex=0;if(cx.test(text)){text=text.replace(cx,function(a){return"\\u"+("0000"+a.charCodeAt(0).toString(16)).slice(-4)})}if(/^[\],:{}\s]*$/.test(text.replace(/\\(?:["\\\/bfnrt]|u[0-9a-fA-F]{4})/g,"@").replace(/"[^"\\\n\r]*"|true|false|null|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?/g,"]").replace(/(?:^|:|,)(?:\s*\[)+/g,""))){j=eval("("+text+")");return typeof reviver==="function"?walk({"":j},""):j}throw new SyntaxError("JSON.parse")}}})();