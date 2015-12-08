/**
 * @description BCGOGO APP 的公用组件
 * @author 潘震
 *
 * @version 0.1.3 2012-10-10
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

APP_BCGOGO.Menu = {
    Excludes: "uncleIndex,carIndex,productThroughDetail,main"
};

APP_BCGOGO.LogoutStopApp = {
    MessageBottomPush:undefined
};
// Module related
APP_BCGOGO.Module = {};

// Module related
APP_BCGOGO.Page = {};

// Verifier related
APP_BCGOGO.Verifier = {
    isShopIdCorrect:true
};

//个性化配置
APP_BCGOGO.PersonalizedConfiguration = {
    TradePriceTag: false,
    StorageBinTag: false,
    //是否开通了维修缺料
    IsRepairPickingSwitchOn:false
};

//权限
APP_BCGOGO.Permission = {
    Version:{
        VehicleConstruction:false, //车辆施工
        MemberStoredValue:false,    //会员储值
        StoreHouse:false,//仓库资源
        IgnorVerifierInventory:false,//校验库存
        RelationSupplier:false,
        RelationCustomer:false,
        OrderMobileRemind:false,
        SearchVehicle:false,
        SearchProduct:false,
        SearchCustomerSupplier:false,
        SearchAccessoryOnline:false,
        SearchCustomerInventoryOnline:false,
        SearchCustomerOnline:false,
        SearchSupplierOnline:false,
        ProductThroughSelectSupplier:false, //出入库打通，选择供应商逻辑
        WholesalerVersion:false,  //汽配版
        ProductThroughDetail:false,//出入库明细,
        FourSShopVersion:false,//4S店专业版版本
        ActiveRecommendSupplier:false
    },
    //进销存
    Txn:{
        Base:false,
        //库存管理
        PurchaseManage:{
            //新增入库
            StorageSave:false,
            Purchase:false,
            Storage:false
        },
        //库存管理
        InventoryManage:{
            ProductModify:false,
            ProductDelete:false,
           //库存查询
            StockSearch:{
                Base:false,
                //库存上下限
                AlarmSettings:false,
                //缺货商品查看
                Alarm:false,
                //商品分类
                ProductClassify:false,
                //查看均价
                AveragePrice:false,
                //最新入库价
                NewStoragePrice:false,
                //批发价
                TradePrice:false,
                //设定批发价
                SetTradePrice:false,
                //销售价
                SalePrice:false,
                //设定销售价
                SetSalePrice:false,
                //仓位
                StorageBin:false,
                SetUnit:false,
                //库存量
                Inventory:false
            }
        },
        SaleManage:{
            Sale:false
        }
    },
    //待办事项
    Schedule:{
        Base:false,
        MessageCenter:{
            Base:false,
            ReceiverDelete:false

        }
    },
    //系统管理
    SystemSetting:{
        Base:false,
        //员工管理
        StaffManager:{
            AddStaff:false,
            DeleteStaff:false,
            AllocatedAccountStaff:false,
            ResetStaffPassword:false,
            EnableDisableStaff:false,
            UpdateStaff:false
        },
        PermissionManager:{
            AddPermission:false,
            UpdatePermission:false,
            CopyPermission:false,
            DeletePermission:false
        }
    },
    //车辆施工
    VehicleConstruction:{
        Base:false,
        Construct:{
            Base:false
        },
        AppointOrder:{
            Manager:false
        },
        WashBeauty:{
            Base:false
        }
    },

    //客户管理
    CustomerManager: {
        Base: false,
        CustomerDelete: false,
        CustomerModify: false,
        MembershipPackageManager: false,
        CustomerArrears: false,
        UpdateCustomer: false,
        SmsSend: false,
        CustomerApplyAction: false,
        VehicleDetail:false,//车辆详情页面
        VehiclePosition:false,//车辆智能定位
        VehicleDriveLog:false //车辆行车日志
    },
    //供应商管理
    SupplierManager:{
        Base:false,
        //付定金
        PayEarnestMoney:false,
        //应付款结算
        DueSettlement:false,
        SupplierApplyAction:false
    },
    //在线汽配
    AutoAccessoryOnline:{
        Base:false,
        //推荐供应商
        ApplySupplier:false,
        //推荐客户
        ApplyCustomer:false,
        //客户库存
        RelatedCustomerStock:false,
        //配件报价
        CommodityQuotations:false,
        //上下架促销
        InOffSalesPromotion:false,
        //报价
        QuotedPreBuyOrder:false,

        PreBuyOrderMessage:false,
        //上架操作链接
        OnSaleOperation:false
    },

    //查询中心
    InquiryCenter:false,
    //财务统计
    Stat:{
        base:false,
        BusinessStat:{
            Business:{
                Construction:false,
                Sale:false,
                WashCar:false
            }
        },
        //营业外记账
        NonOperatingAccount:{
            Delete:false,
            Update:false
        }
    }
};



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
    stringSpaceFilter:function(value){
        return value ? value.replace(/\s/g, "") : "";
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
        var foo = APP_BCGOGO.StringFilter;
        return foo.inputtingIntFilter(value);
    },
    /**
     * @description 自然数过滤器
     * @param value 字符串
     * @returns {String} "" 或者 过滤后的 自然数字符串, 仅当没有找到任何数字时返回 ""
     */
    naturalFilter:function (value) {
        var ostr = "";
        if (value) {
            ostr = value.replace(/[^\d]+/g, "").replace(/0+/g, "0");
            if (ostr && ostr.length > 1)
                ostr = ostr.replace(/^0/g, "");
        }
        return ostr;
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
        var foo = APP_BCGOGO.StringFilter, istr = value, ostr = "";
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
     * @param figures
     * @returns {String}
     */
    inputtingPriceFilter:function (value, figures) {
        var foo = APP_BCGOGO.StringFilter, istr = value, ostr = "", ptIndex = -1;
        if (isNaN(figures) || !isNaN(figures) && (figures < 0 || figures >= 4)) {
            var figures = 2;
        }
        ostr = foo.inputtingFloatFilter(istr);
        ptIndex = ostr.search(/\./);
        if (ptIndex != -1 && ostr.length - (ptIndex + 1) > figures) {
            ostr = ostr.slice(0, (ptIndex + 1) + figures);
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
     * @param figures
     * @returns {String}
     */
    priceFilter:function (value, figures) {
        var foo = APP_BCGOGO.StringFilter, istr = value, ostr = "";
        ostr = foo.inputtingPriceFilter(istr, figures);
        return ostr ? parseFloat(ostr).toString() : ostr;
    },

    inputtedPriceFilter:function(value, figures) {
        return App.StringFilter.priceFilter(value, figures);
    },

    inputtingNameFilter:function (value) {
        return value.replace(/[^\u4e00-\u9fa5\w\(\)（）×\*/\.\-_\-‘’“”、,，]+/g, "");
    },

    inputtingOtherCostNameFilter:function(value) {
        return value.replace(/[^\u4e00-\u9fa5\w\(\)（）×\*/\.\-_\-‘’“”、,，\$% ]+/g, "");
    },

    /**
     * @description 归一化，正在输入的品名
     * @param {String} value
     * @returns {String}
     */
    inputtingProductNameFilter:function (value) {
        var foo = APP_BCGOGO.StringFilter;
        return foo.inputtingNameFilter(value);
    },
    /**
     * @description 一个输入框可输入多个内容，符号中只可输入逗号,顿号,空格
     * @param value
     */
    multiStringFilter:function(value) {
        // TODO 潘震：这段代码是不对的， 需要将其修改成白名单， 黑名单没法枚举所有的错误
        value = value.replace(/[`~!@#$^&*()=|\\{\}%_\+\-"':;'\[\].<>\/?~！@#￥……&*（）——|{}【】‘；：”“'。？·～《》]/g, "");
        return value.replace(/[、,，\s]/g, ",");
    },
    /**
     * 车架号输入中验证，只允许输入数字字母
     * @param value
     */
    inputtingVinFilter:function(value) {
        return value.replace(/[^A-Z\d]+/g, "")
    },
    /**
     * 商品编码输入中验证，含 0-9 十个数字,A-Z 26 个字母,+ - * / $ %  空格，43 个符合
     * 小写变成大写
     */
    inputtingCommodityCodeFilter:function(value) {
        return  value.replace(/[—]/g, "-")
            .replace(/[×]/g, "*")
            .replace(/[^0-9a-zA-Z\+\-\*\/\$\%]/g, "")
            .toUpperCase();
    },
    //商品编码输入完之后验证 首位排除空格
    commodityCodeFilter:function(value) {
        return  value.replace(/[—]/g, "-")
            .replace(/[×]/g, "*")
            .replace(/(^\s+)|(\s+$)/g, "")
            .replace(/[^0-9a-zA-Z\+\-\*\/\$\%]/g, "")
            .toUpperCase();
    },
    //车牌号输入过滤
    inputtingLicenseNoFilter:function (value) {
        if(value != null ){
            return value.toUpperCase().replace(/[^A-Z\d\u4e00-\u9fa5\-_\(\)]+/g, "");
        }else{
            return "";
        }
    },
    //整数输入过滤
    inputtingNumberFilter:function(value){
        if(value != null){
            return value.replace(/[^\d]+/g, "");
        }else{
            return "";
        }
    },
    //非空输入过滤
    inputtingBlankFilter:function(value){
        if(value != null){
            return value.replace(/[\ |\\]/g, "");
        }else{
            return "";
        }
    },
    //单据号输入过滤 输入的时候小写变大写，取消空格，"—"，变"-"中文的短横变成字母的短横
    inputtingReceiptNoFilter:function(value){
       if(value != null){
          return value.toUpperCase().replace(/[—]/g,"-").replace(/[^0-9a-zA-Z\-]/g, "");
       }else{
           return "";
       }
    },
    /**
     * DEMO  :
             var pos = APP_BCGOGO.StringFilter.getCursorPosition(this,APP_BCGOGO.StringFilter.inputtingProductNameFilter);
             $(this).val(APP_BCGOGO.StringFilter.inputtingCommodityCodeFilter($(this).val()));
             APP_BCGOGO.StringFilter.setCursorPosition(this, pos);
     * @param ctrl
     * @param filterFunction
     * @returns {number}
     */
    //getCursorPosition   dom， filterFunction 传入stringFilter方法
    getCursorPosition:function(ctrl,filterFunction){
        var CaretPos = 0;
        // IE Support
        if (ctrl.type != "text") {
            return;
        }
        if (document.selection) {
            ctrl.focus();
            var Sel = document.selection.createRange();
            Sel.moveStart('character', -ctrl.value.length);
            CaretPos = Sel.text.length;
        }
        // Firefox support
        else if (ctrl.selectionStart != NaN || ctrl.selectionStart == '0'){
            CaretPos = ctrl.selectionStart;
        }
        if (CaretPos > 0 && filterFunction && typeof (filterFunction) == 'function' ) {
            var leftVal = $(ctrl).val().substring(0, CaretPos);
            leftVal = filterFunction(leftVal);
            CaretPos = leftVal ? leftVal.length : 0;
        }
        return (CaretPos);
    },
    setCursorPosition: function (ctrl, pos) {
        if (ctrl.type != "text") {
            return;
        }
        if (ctrl.setSelectionRange) {
            ctrl.focus();
            ctrl.setSelectionRange(pos, pos);
        }
        else if (ctrl.createTextRange) {
            var range = ctrl.createTextRange();
            range.collapse(true);
            range.moveEnd('character', pos);
            range.moveStart('character', pos);
            range.select();
        }
    }

};

APP_BCGOGO.Validator = {
    _regTextMatchInt:"^\\d+$",
    _regTextMatchNatural:"^([1-9]\\d+)|\\d$",
    _regTextMatchIntGreaterThanNegativeOne:"^(\\-1)|(\\d+)$",
    _regTextMatchHasDecimals:"^\\d+\\.\\d+$",
    // TODO to merge 支持多个手机号，逗号隔开
//    _regTextMatchMobilePhoneNumber:"^(1\\d{10})$",
    _regTextMatchMobilePhoneNumber:"^(?:1[3|4|5|7|8]\\d)-?\\d{5}(\\d{3}|\\*{3})$",
    // _regTextMatchMobilePhoneNumber:"^(\\+\\d{1,4})?1\\d{10}$",
    //_regTextMatchMultiMobilePhoneNumber:"^(\\+\\d{1,4})?1\\d{10}((,\\+\\d{1,4})?1\\d{10})*$",
    _regTextMatchMultiMobilePhoneNumber:"^(1\\d{10})(,1\\d{10})*$",
    // TODO to merge 支持多个座机号，逗号隔开
    _regTextMatchTelephoneNumber:"^(((0\\d{2,3}[- ]?)?\\d{7,8})|(\\d{3,4}[- ]?(\\d{3,4}[- ]?)?\\d{4,6}))(,(((0\\d{2,3}[- ]?)?\\d{7,8})|(\\d{3,4}[- ]?(\\d{3,4}[- ]?)?\\d{4,6})))*$",
    _regTextMatchStrictPrice:"^0$|^[1-9]\\d*$|^0\\.([1-9]|\\d[1-9])$|^[1-9]\\d*\\.([1-9]|\\d[1-9])$",
    _regTextMatchPrice:"^0$|^[1-9]\\d*$|^0\\.(\\d){1,2}$|^[1-9]\\d*\.(\\d){1,2}$",
    _regTextMatchEmail:"^[0-9A-Za-z_\\-]+@[0-9A-Za-z\\-]+\\.[0-9A-Za-z]{2,3}(\\.[0-9A-Za-z]{2,3})?$",
    _regTextMatchQq:"^\\d{5,10}$",
    _regTextMatchEn:"^[a-zA-Z]+$",
    _regTextMatchCharacter:"^[A-Za-z0-9]+$",
    _regTextMatchZhCn:"^[\\u4E00-\\u9FFF]+$",
    _regTextMatchStartMileage:"^\\d{1,7}(.\\d)?$",
    _regTextMatchEngine:"^1?\\d(.\\d)?$",
    // TODO to merge
    _regTextMatchLicensePlateNumber:"(^[\\u4e00-\\u9fa5]{2}[a-zA-Z\\d]{4}$)|(^([a-zA-Z\\d]{5,7}|[a-zA-Z\\d]{9}|[a-zA-Z]{2}[\\u4e00-\\u9fa5]{1}[a-zA-Z\\d]{5})$)|(^[\\u4e00-\\u9fa5]{1}([a-zA-Z\\d]{6,7}|[a-zA-Z\\d]{5}[\\u4e00-\\u9fa5]{1})$)|(^[\\u4e00-\\u9fa5]{2,3}[a-zA-Z][a-zA-Z\\d]{5}$)|(^\\u9886[a-zA-Z][a-zA-Z\\d]{4}$)",
                                            //警备2a32/京安2a32               BK32134 军车12年   WJ0913425  WJ沪5005X 苏E14E21，苏E1342警，苏E123A学，苏E12A2试，苏0213E15（农用1）                               江苏C13E12（农用2）                                    领A231C
    _equalsTo:function (s, regString) {
        var foo = APP_BCGOGO.Validator, matchResult = s.match(new RegExp(regString, "g"));
        return (s && matchResult != null && s === matchResult[0]) ? true : false;
    },
    /**
     * @description 是否是 大于等于 -1 的整数
     * @param {String} istr
     */
    stringIsIntGreaterThanNegativeOne:function (s) {
        var foo = APP_BCGOGO.Validator;
        return foo._equalsTo(s, foo._regTextMatchIntGreaterThanNegativeOne);
    },
    stringIsNatualNumber:function (s) {
        var foo = APP_BCGOGO.Validator;
        return foo._equalsTo(s, foo._regTextMatchNatural);
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
     * @param {String} type
     * @param {Int} phoneCount 不是必须 ，或者为 大于等于1的整数
     * @returns {Boolean}
     */
    stringIsMobilePhoneNumber:function (s, type, phoneCount) {
        // TODO zhen.pan 这里应该是在type === multiple 时才启用 phoneCount，这块尚需整理
        var foo = APP_BCGOGO.Validator, c = Math.round(phoneCount);
        if (type && type === "multiple") {
            return foo._equalsTo(s, foo._regTextMatchMultiMobilePhoneNumber);
        } else {
            var rule = !isNaN(c) && c >= 1 ?
                foo._regTextMatchMultiMobilePhoneNumber.replace(/\*\$/g, "{0," + c + "}"):
                foo._regTextMatchMobilePhoneNumber;
            return foo._equalsTo(s, rule);
        }
    },
    /**
     *  判断字符串是不是 手机号码
     * @param s
     */
    stringIsMobile:function (s) {
        var foo = APP_BCGOGO.Validator;
        return foo._equalsTo(s, foo._regTextMatchMobilePhoneNumber);
    },
    /**
     * @description 判断字符串是不是 座机号码
     * @param {String} s
     * @returns {Boolean}
     */
    stringIsTelephoneNumber:function (s) {
        var foo = APP_BCGOGO.Validator;
        return foo._equalsTo(s, foo._regTextMatchTelephoneNumber);
    },
    /**
     * @description 判断字符串是不是 邮箱
     * @param {String} s
     * @returns {Boolean}
     */
    stringIsEmail:function (s) {
        var foo = APP_BCGOGO.Validator;
        return foo._equalsTo(s, foo._regTextMatchEmail);
    },
    /**
     * @description 判断字符串是不是 QQ号
     * @param {String} s
     * @returns {Boolean}
     */
    stringIsQq:function (s) {
        var foo = APP_BCGOGO.Validator;
        return foo._equalsTo(s, foo._regTextMatchQq);
    },
    /**
     * @description 判断字符串是不是 价格格式
     * @param {String} s
     * @returns {Boolean}
     */
    stringIsPrice:function (s, isStrict) {
        var foo = APP_BCGOGO.Validator;
        return foo._equalsTo(s, isStrict ? foo._regTextMatchStrictPrice : foo._regTextMatchPrice);
    },
    /**
     * @description 判断字符串是不是 纯英文
     * @param s
     * @return {*}
     */
    stringIsEn:function (s) {
        var foo = APP_BCGOGO.Validator;
        return foo._equalsTo(s, foo._regTextMatchEn);
    },
    /**
     * @description 判断字符串是不是 纯中文
     * @param s
     * @return {*}
     */
    stringIsZhCn:function (s) {
        var foo = APP_BCGOGO.Validator;
        return foo._equalsTo(s, foo._regTextMatchZhCn);
    },
    /**
     * @description 判断字符串是不是0-9或者A-Z
     * @param s
     */
    stringIsCharacter:function(s) {
        var foo = APP_BCGOGO.Validator;
        return foo._equalsTo(s, foo._regTextMatchCharacter)
    },

    // TODO to merge 车牌号验证
    stringIsLicensePlateNumber:function(s) {
        var foo = APP_BCGOGO.Validator;
        return foo._equalsTo(s, foo._regTextMatchLicensePlateNumber);
    },

    /**
     * TODO to merge 验证 进厂里程
     * @description 判断字符串是不是 进厂里程
     * @param s
     * @return {*}
     */
    stringIsStartMileage:function(s) {
        var foo = APP_BCGOGO.Validator;
        return foo._equalsTo(s, foo._regTextMatchStartMileage);
    },
    /**
     * TODO to merge 验证 排量
     * @description 判断字符串是不是 汽车排量
     * @param s
     * @return {*}
     */
    stringIsEngine:function(s) {
        var foo = APP_BCGOGO.Validator;
        return foo._equalsTo(s, foo._regTextMatchEngine);
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
    var retVal = null;
    // 设置 requestType ，告诉 service ,我是一个 ajax 请求
    if (!value['data']) {
        value['data'] = {};
    }
    value['data']['requestType'] = 'AJAX';
    value['data']['now'] = "" + new Date().getTime();
    value['data']['requestUid'] = G.generateUUID();
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
 *     dataType: "xml|html|script|json|jsonp|text",  非必须, 返回手动拼接的Json时建议添加 dataType: "json"
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
 * @returns xhr XMLHttpRequest 对象
 */
APP_BCGOGO.Net.asyncAjax = function (value) {
    var xhr = undefined;
    if (!value['data']) {
        value['data'] = {};
    }
    value['data']['requestType'] = 'AJAX';
    value['async'] = true;
    //callback
    value['success'] = !value['success'] ? function (data, textStatus, jqXHR) {
        GLOBAL.info(data);
    } : value['success'];
    value['error'] = !value['error'] ? function (jqXHR, textStatus, errorThrown) {
        GLOBAL.error("request error");
    } : value['error'];
    xhr = $.ajax(value);
    return xhr;
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
 * @returns xhr XMLHttpRequest 对象,  xhr 一般用来干什么呢， 比如客户端可以主动断开服务器连接（当服务器尚未 reponse 时）
 */
APP_BCGOGO.Net.asyncGet = function (value) {
    value['type'] = "GET";
    return APP_BCGOGO.Net.asyncAjax(value);
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
 * @returns xhr XMLHttpRequest 对象,  xhr 一般用来干什么呢， 比如客户端可以主动断开服务器连接（当服务器尚未 reponse 时）
 */
APP_BCGOGO.Net.asyncPost = function (value) {
    value["type"] = "POST";
    return APP_BCGOGO.Net.asyncAjax(value);
};

APP_BCGOGO.namespace("Events");
APP_BCGOGO.Events.hasBind = function (type, ele, handler) {
    var notBind = true,
        eventsObj = $(ele).data("events"),
        specEventList = eventsObj ? eventsObj[type] : undefined;

    if (eventsObj && specEventList) {
        $.each(specEventList, function (index, value) {
            notBind = !(value.handler === handler);
            return notBind;
        });
    }
    return !notBind;
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
            jQuery.post(searchParams.url, searchParams.data, searchParams.callback,
                searchParams.dataType);
        };
        var getTimeout = function () {
            return  setTimeout(function () {
                ajaxQuery();
            }, 300);
        };
        var setSearchParams = function (url, data, callback, dataType) {
            if (!url) {
                GLOBAL.warning("url is null");
                return;
            }
            searchParams.url = url;
            if (!data) {
                data = {};
            }
            data['now'] = "" + new Date().getTime();
            searchParams.data = data;
            if (callback)searchParams.callback = callback;
            if (dataType)searchParams.dataType = dataType;

        }
        return {
            lazySearch:function (url, data, callback, dataType) {
                setSearchParams(url, data, callback, dataType);
                if (timeId)
                    clearTimeout(timeId);
                timeId = getTimeout();
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
APP_BCGOGO.namespace("APP_BCGOGO.Collection.Comparator");
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
;
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
            var proArr = this.propertyArray,
                proLen = proArr.length;

            if (proLen === 0) return src === target;

            for (var i = 0; i < proLen; i++) {
                if (src[proArr[i]] !== target[proArr[i]]) return false;
            }
            return true;
        }
    };

    //集合工具类  SET
    var Set = function () {
        var array = new Array();
        var length = 0;

        this.add = function (target) {
            if (!this.contains(target)) {
                array[length++] = target;
            }
        };
        this.size = function () {
            return length;
        };
        this.getObject = function (index) {
            return array[index];
        };

        //判断Set中是否包含指定的对象
        this.contains = function (target) {
            for (var i = 0, len = array.length; i < len; i++) {
                if (Comparator.equalsTo(array[i], target)) return true;
            }
            return false;
        };
    };

    APP_BCGOGO.wjl.Collection.Set = Set;
    APP_BCGOGO.wjl.Collection.Comparator = Comparator;

        //集合工具类  Map
    var Map = function () {
        this.elements = new Array();
        //获取MAP元素个数
        this.size = function () {
            return this.elements.length;
        };
        //判断MAP是否为空
        this.isEmpty = function () {
            return (this.elements.length < 1);
        };
        //删除MAP所有元素
        this.clear = function () {
            this.elements = new Array();
        };

        //向MAP中增加元素（key, value)
        this.put = function (_key, _value) {
            this.remove(_key);
            this.elements.push({
                key: _key,
                value: _value
            });
        };
        //删除指定KEY的元素，成功返回True，失败返回False
        this.remove = function (_key) {
            var bln = false;
            try {
                for (var i = 0; i < this.elements.length; i++) {
                    if (this.elements[i].key == _key) {
                        this.elements.splice(i, 1);
                        return true;
                    }
                }
            } catch (e) {
                bln = false;
            }
            return bln;
        };
        //获取指定KEY的元素值VALUE，失败返回NULL
        this.get = function (_key) {
            try {
                for (var i = 0; i < this.elements.length; i++) {
                    if (this.elements[i].key == _key) {
                        return this.elements[i].value;
                    }
                }
            } catch (e) {
                return null;
            }
        };

        //获取指定索引的元素（使用element.key，element.value获取KEY和VALUE），失败返回NULL
        this.element = function (_index) {
            if (_index < 0 || _index >= this.elements.length) {
                return null;
            }
            return this.elements[_index];
        };

        //判断MAP中是否含有指定KEY的元素
        this.containsKey = function (_key) {
            var bln = false;
            try {
                for (var i = 0; i < this.elements.length; i++) {
                    if (this.elements[i].key == _key) {
                        bln = true;
                    }
                }
            } catch (e) {
                bln = false;
            }
            return bln;
        };
        //判断MAP中是否含有指定VALUE的元素
        this.containsValue = function (_value) {
            var bln = false;
            try {
                for (var i = 0; i < this.elements.length; i++) {
                    if (this.elements[i].value == _value) {
                        bln = true;
                    }
                }
            } catch (e) {
                bln = false;
            }
            return bln;
        };
        //获取MAP中所有VALUE的数组（ARRAY）
        this.values = function () {
            var arr = new Array();
            for (var i = 0; i < this.elements.length; i++) {
                arr.push(this.elements[i].value);
            }
            return arr;
        };
        //获取MAP中所有KEY的数组（ARRAY）
        this.getKeys = function () {
            var arr = new Array();
            for (var i = 0; i < this.elements.length; i++) {
                arr.push(this.elements[i].key);
            }
            return arr;
        };
    };
    APP_BCGOGO.wjl.Collection.Map = Map;
})();

/**
 * @description debug 工具类  配合firebug
 * V1.1  date 2013-08-01 10:02
 * V1.2  date 2013-08-13 添加了简化用法 $dp($("#id")),$dp("selector");
 * v1.3  date 2013-09-10 zhen.pan
 *       1. fixed null point exception
 *       2. try catch to console.log, primarily fix urgly IE bug
 * option 参数列表：dom 需要打印的dom对象，eventTypes 绑定的事件对象 数组 不传为全部，
 * 目前仅对jquery 1.4.2 +fireFox 测试ok
 * demo：fireBug控制台输入 APP_BCGOGO.debugUtil.printDomBindEvent(dom:$("#mobile"))
 * iframe内部元素:用法：App.debugUtil.printDomBindEvent({dom:$("#iframe_PopupBox_account").contents().find("#confirmBtn")})
 * todo 如果对象在子页面里，目前还无法监控到该对象的代理事件有待大神解决
 * @param info
 */
APP_BCGOGO.debugUtil = (function () {
    var trace = function() {
        try {
            console.log.apply(window, arguments);
        } catch (e) {;}
    };

    return {
        printDomBindEvent: function (option) {
            var varDefault = {
                dom: $(document),
                eventTypes: []
            };

            if(!option) return;
            if (typeof option == "object" && option && !option.dom) {
                option.dom = option;
            } else if (!G.Lang.isEmpty(option) && typeof option == "string") {
                var temp = {};
                temp["dom"] = $(option);
                option = temp;
            }

            if (typeof option == "object" && option && !option.dom) {
                option.dom = option;
            } else if (!G.Lang.isEmpty(option) && typeof option == "string") {
                var temp = {};
                temp["dom"] = $(option);
                option = temp;
            }

            if(G.isObject(option) && G.isEmpty(option.dom) || G.isString(option)) {
                option.dom = $(option, document);
            }

            var opt = $.extend(varDefault, option);
            trace("DOM事件函数：");
            opt.dom.each(function () {
                var thisDom = $(this).get(0);
                for (var eventType in thisDom) {
                    if (eventType && eventType.indexOf("on") == 0 && typeof thisDom[eventType] == "function"
                        && (opt.eventTypes && opt.eventTypes.length > 0 && $.inArray(eventType, opt.eventTypes) != -1
                        || !opt.eventTypes || opt.eventTypes.length == 0)) {
                        trace("%o", $(this), eventType, eval(thisDom[eventType]));
                    }
                }
            });

            trace("DOM事件侦听器函数：");
            opt.dom.each(function () {
                for (var eventType in $(this).data().events) {
                    if ($(this).data().events[eventType] && $(this).data().events[eventType].length > 0
                        && (opt.eventTypes && opt.eventTypes.length > 0 && $.inArray(eventType, opt.eventTypes) != -1
                        || !opt.eventTypes || opt.eventTypes.length == 0)) {
                        if ($(this).data().events[eventType] && $(this).data().events[eventType].length > 0) {
                            for (var j = 0, len = $(this).data().events[eventType].length; j < len; j++) {
                                trace("%o", $(this), eventType, $(this).data().events[eventType][j].handler);
                            }
                        }
                    }
                }
            });
            trace("代理事件函数：");
            opt.dom.each(function () {
                var liveEventsList = $(document).data().events.live;
                if(!liveEventsList) return;

                for (var i = 0, len = liveEventsList.length; i < len; i++) {
                    var liveEvent = liveEventsList[i];
                    var selector = liveEvent.selector;
                    var origType = liveEvent.origType;
                    var preType = liveEvent.preType;
                    if (($(selector).length - $(selector).not(this).length) > 0 &&
                        (opt.eventTypes && opt.eventTypes.length > 0 && $.inArray(origType, opt.eventTypes) != -1
                            || !opt.eventTypes || opt.eventTypes.length == 0 )) {
                        trace("%o", $(this), origType, liveEvent.handler);
                    }
                }
            });
        }
    }
})();
var $dp = APP_BCGOGO.debugUtil.printDomBindEvent;

// TODO 这两个 函数 将考虑 使用 css 来实现 ......
function setTextInputDefaultStatus( $node ) {
    var initValue = $node.attr("initialValue");
    if( GLOBAL.Lang.isEmpty(initValue) )
        return;

    if( GLOBAL.Lang.isEmpty($node.val()) === true) {
        $node
            .val(initValue)
            .css({"color":"#666666"});
    }else {
        $node.css({"color":"#000000"});
    }
}

function setTextInputChangedStatus( $node ) {
    var initValue = $node.attr("initialValue");
    if( GLOBAL.Lang.isEmpty(initValue) )
        return;

    if($node.val() === initValue)
        $node.val("");

    $node.css({"color":"#000000"});
}


APP_BCGOGO.namespace("OrderTypes");
APP_BCGOGO.OrderTypes = {
    PURCHASE:"采购单",
    INVENTORY:"入库单",
    SALE:"销售单",
    REPAIR:"施工单",
    REPAIR_SALE:"材料销售单",
    WASH:"洗车单",
    RETURN:"入库退货单",
    SALE_RETURN:"销售退货单",
    ALL:"所有单据",
    //会员相关
    WASH_MEMBER:"会员洗车单",
    RECHARGE:"会员充值单",
    MEMBER_BUY_CARD:"会员购卡续卡",
    MEMBER_RETURN_CARD:"会员退卡",
    ORDER_TYPE_MEMBER_CARD_ORDER:"购卡续卡",
    WASH_BEAUTY:"洗车美容单",

    //打印专用
    DEBT:"欠款结算单",
    PAYABLE:"应付款结算单",
    DEPOSIT:"定金结算单",
    BIZSTAT:"营收统计单",
    WASH_TICKET:"洗车小票",
    INVENTORY_PRINT:"库存打印单",
    PAYABLE_STATISTICAL:"应付统计打印单",
    RECEIVABLE_STATISTICAL:"应收统计打印单",
    BIZSTAT_SALES_DETAIL:"营业统计销售详情",
    BIZSTAT_REPAIR_DETAIL:"营业统计施工详情",
    BIZSTAT_WASH_DETAIL:"营业统计洗车详情",
    BUSINESS_MEMBER_CARD_ORDER:"会员购卡记录统计单",
    BUSINESS_MEMBER_CONSUME:"会员消费记录统计单",
    BUSINESS_MEMBER_RETURN:"会员退卡记录单",
    RUNNING_DAY_INCOME:"流水日收入统计打印",
    RUNNING_MONTH_INCOME:"流水月收入统计打印",
    RUNNING_YEAR_INCOME:"流水年收入统计打印",
    RUNNING_DAY_EXPEND:"流水日支出统计打印",
    RUNNING_MONTH_EXPEND:"流水月支出统计打印",
    RUNNING_YEAR_EXPEND:"流水年支出统计打印",
    //营业外记账
    BUSINESS_ACCOUNT:"营业外记账",

    CUSTOMER_BUSINESS_STATISTICS:"客户交易统计单",
    SUPPLIER_BUSINESS_STATISTICS:"供应商交易统计单",
    PRODUCT_CATEGORY_SALES_STATISTICS:"商品分类销售额统计",
    BUSINESS_CATEGORY_SALES_STATISTICS:"营业分类销售额统计"
};




// alias
var App = APP_BCGOGO;
