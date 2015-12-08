/**
 * 通过id type获取联系人列表
 *
 * @param id customerId or supplierId or shopId
 * @param type enum {"customer","supplier","shop"}
 * @param domObject
 * @param keycode
 */
function getContactListByIdAndType(id,type, domObject, keycode) {
    var $domObject = $(domObject);
    var searchWord = $domObject.val().replace(/[\ |\\]/g, "");
    searchWord = $.trim(searchWord).toUpperCase();
    var droplist = APP_BCGOGO.Module.droplist;
    droplist.setUUID(GLOBAL.Util.generateUUID());
    var ajaxData = {
        id: id,
        type: type,
        uuid: droplist.getUUID()
    };
    var ajaxUrl = "contact.do?method=getContactsByIdAndType"; // 查询联系人
    APP_BCGOGO.wjl.LazySearcher.lazySearch(ajaxUrl, ajaxData, function (result) {
        // 如果result存在 diabled 输入框 回调函数调用 则置为可用
        droplist.show({
            "selector": $domObject,
            "data": result,
            "onSelect": function (event, index, data) {
                $domObject.val(data.details.name);
                $("#contactId").val(data.details.id);
                $("#mobile").val(data.details.mobile);
                $("#mobile").css("color", "#000");

                if(getOrderType() == "BORROW_ORDER"){
                    $("#email").val(G.Lang.normalize(data.details.email));
                    $("#qq").val(G.Lang.normalize(data.details.qq));
                }
                $domObject.css({"color": "#000000"});
                droplist.hide();
            },
            "onKeyboardSelect": function (event, index, data, hook) {
                $domObject.val(data.details.name);
                $("#contactId").val(data.details.id);
                $("#mobile").val(data.details.mobile);
                $("#mobile").css("color", "#000");
            }
        });
    });
}

/**
 * 在保证单个客户下面手机号不重复的情况下
 * 通过手机号来查询联系人->客户
 * 客户id如果和传入的id不同 返回false 否则返回true
 * @param mobile
 * @param customerId
 * @returns {boolean}
 */
function checkContactSameMobile(mobile, customerId) {
    //要判断是否存在同名的手机号
    var r = APP_BCGOGO.Net.syncGet({url: "customer.do?method=getCustomerJsonDataByMobile",
        data: {mobile: mobile, customerId: customerId}, dataType: "json"});
    if (r.success && r.data) {
        var obj = r.data;
        var customerId = obj.idStr;
        if (r.msg == 'customer' && customerId != "" && customerId != null && customerId != $("#customerId").val()) {
            nsDialog.jAlert("已存在与【" + obj.name + "】相同的客户手机号，请重新输入");
            return false;
        } else if(r.msg == 'supplier'){
            nsDialog.jAlert("与已存在供应商【" + obj.name + "】的手机号相同，请重新输入！");
            return false;
        } else {
            return true;
        }
    } else {
        return true;
    }
}

// 通过手机查询数组中的联系人
function isMobileContactExist(contacts, mobile) {
    if (contacts.constructor !== Array || G.isEmpty(mobile)) {
        return;
    }
    for (var i = 0; i < contacts.length; i++) {
        if (contacts[i].mobile === mobile) {
            return {index: i, obj: contacts[i]};
        }
    }
}

function getCustomerByName(name) {
    return APP_BCGOGO.Net.syncPost({
        url: "customer.do?method=searchCustomerByName",
        data: {
            customerName: name
        },
        cache: false,
        dataType: "json"
    });
}


function filterNullObjInArray(objs) {
    if (objs.constructor == Array) {
        var newArray = new Array();
        for (var index = 0; index < objs.length;) {
            var ele = objs.shift();
            if (!G.isEmpty(ele)) {
                newArray.push(ele);
            }
        }
        return newArray;
    }
}

/**
 * 参数contact为json格式
 *  id:，
    name:，
    mobile:，
    email:,
    qq:,
    level:,
    mainContact:
 * @param contact
 */
function isValidContact(contact){
    if(G.isEmpty(contact)){
        return false;
    }
    return !(G.isEmpty(contact.name) && G.isEmpty(contact.mobile) && G.isEmpty(contact.email) && G.isEmpty(contact.qq));
}

/**
 * 计算合法的联系人数量
 * @param contacts
 * @returns {number}
 */
function countValidContact(contacts) {
    var count = 0;
    if (G.isEmpty(contacts) || !contacts.constructor == Array) {
        return count;
    }
    for (var i = 0; i < contacts.length; i++) {
        if (isValidContact(contacts[i])) {
            count++;
        }
    }
    return count;
}

function countMainContact(contacts) {
    var count = 0;
    if (G.isEmpty(contacts) || !contacts.constructor == Array) {
        return count;
    }
    for (var i = 0; i < contacts.length; i++) {
        if (isValidContact(contacts[i]) && (contacts[i].mainContact === "1" || contacts[i].mainContact == 1) ) {
            count++;
        }
    }
    return count;
}

function getContactIndexByFrom(contacts) {
    var fromPageIndex;
    var fromDBIndex;
    if (G.isEmpty(contacts) || !contacts.constructor == Array) {
        return;
    }
    for (var i = 0; i < contacts.length; i++) {
        if (isValidContact(contacts[i]) && (contacts[i].mainContact === "1"||contacts[i].mainContact == 1) && contacts[i].from == "page") {
            fromPageIndex = i;
        } else if (isValidContact(contacts[i]) && (contacts[i].mainContact === "1"||contacts[i].mainContact == 1)&& contacts[i].from == "db") {
            fromDBIndex = i;
        }
    }
    return {page: fromPageIndex, db: fromDBIndex}
}

function getValidContactIndexs(contacts) {
    var indexs = new Array();
    if (G.isEmpty(contacts) || !contacts.constructor == Array) {
        return;
    }
    for (var i = 0; i < contacts.length; i++) {
        if (isValidContact(contacts[i])){
            indexs.push(i);
        }
    }
    return indexs;
}

/**
 * 从联系人列表里面抓取主联系人
 * @param contacts
 */
function getMainContactFromContacts(contacts) {
    if (!G.isEmpty(contacts) && contacts.constructor == Array) {
        for (var i = 0; i < contacts.length; i++) {
            if (contacts[i].mainContact === "1"||contacts[i].mainContact == 1) {
                return i;
            }
        }
    }
}

/**
 * 获取第一个合法的联系人下表
 * @param contacts
 * @returns {number}
 */
function firstValidContactFromContacts(contacts) {
    if (!G.isEmpty(contacts) && contacts.constructor == Array) {
        for (var i = 0; i < contacts.length; i++) {
            if (isValidContact(contacts[i])) {
                return i;
            }
        }
    }
}


function setFirstValidToMainContact(contacts){
  var index = firstValidContactFromContacts(contacts); // 只有一个联系人的时候 设为主联系人


  var mainContactId = 0;
  for (var i = 0; i < 3; i++) {
     if($("#contacts\\[" + i + "\\]\\.mainContact").val() == "1" || $("#contacts\\[" + i + "\\]\\.mainContact").val() == 1){
       mainContactId = i;
     }
  }

  if (!($("#contacts\\[" + index + "\\]\\.mainContact").val() == "1")) {
    $("#contacts\\[" + mainContactId + "\\]\\.name").val(contacts[index].name);
    $("#contacts\\[" + mainContactId + "\\]\\.mobile").val(contacts[index].mobile);
    $("#contacts\\[" + mainContactId + "\\]\\.qq").val(contacts[index].qq);
    $("#contacts\\[" + mainContactId + "\\]\\.email").val(contacts[index].email);
    $("#contacts\\[" + mainContactId + "\\]\\.level").val("0");


    $("#contacts\\[" + index + "\\]\\.name").val("");
    $("#contacts\\[" + index + "\\]\\.mobile").val("");
    $("#contacts\\[" + index + "\\]\\.qq").val("");
    $("#contacts\\[" + index + "\\]\\.email").val("");
  }
}

/**
 * 主联系人是否是合法的联系人(是否填写了相关信息)
 * @param contacts
 * @returns {number}
 */
function mainContactIsValid(contacts){
    if (!G.isEmpty(contacts) && contacts.constructor == Array) {
        for (var i = 0; i < contacts.length; i++) {
            if (contacts[i].mainContact === "1"||contacts[i].mainContact == 1) {
                return isValidContact(contacts[i]);
            }
        }
    }
}

/**
 * 这个方法依赖页面中的联系人的id值
 * 如果页面组件化的话 挪到对应的页面里面去 不通用
 */
function buildNormalKeyContacts(){

    var contacts = new Array();

    var contact1 =
    {
        id: $("#contacts\\[0\\]\\.id").val(),
        name: $("#contacts\\[0\\]\\.name").val(),
        mobile: $("#contacts\\[0\\]\\.mobile").val(),
        email:$("#contacts\\[0\\]\\.email").val(),
        qq:$("#contacts\\[0\\]\\.qq").val(),
        level:$("#contacts\\[0\\]\\.level").val(),
        mainContact:$("#contacts\\[0\\]\\.mainContact").val()
    };
    contacts.push(contact1);
/*    if(isValidContact(contact1)){
        contacts.push(contact1);
    }*/

    var contact2 =
    {
        id: $("#contacts\\[1\\]\\.id").val(),
        name: $("#contacts\\[1\\]\\.name").val(),
        mobile: $("#contacts\\[1\\]\\.mobile").val(),
        email: $("#contacts\\[1\\]\\.email").val(),
        qq:$("#contacts\\[1\\]\\.qq").val(),
        level:$("#contacts\\[1\\]\\.level").val(),
        mainContact:$("#contacts\\[1\\]\\.mainContact").val()
    };
    contacts.push(contact2);
    /*if(isValidContact(contact2)){
        contacts.push(contact2);
    }*/

    var contact3 =
    {
        id: $("#contacts\\[2\\]\\.id").val(),
        name: $("#contacts\\[2\\]\\.name").val(),
        mobile: $("#contacts\\[2\\]\\.mobile").val(),
        email:$("#contacts\\[2\\]\\.email").val(),
        qq:$("#contacts\\[2\\]\\.qq").val(),
        level:$("#contacts\\[2\\]\\.level").val(),
        mainContact:$("#contacts\\[2\\]\\.mainContact").val()
    };
    contacts.push(contact3);
    /*if(isValidContact(contact3)){
        contacts.push(contact3);
    };*/

    return contacts;

}

/**
 * 这个方法依赖页面中的联系人的id值
 * 如果页面组件化的话 挪到对应的页面里面去 不通用
 */
function buildNormalKeyContacts2() {

    var contacts = new Array();
    var contactCount = 6;
    for (var count = 0; count < contactCount; count++) {
        var contactTemp =
        {
            id: $("#contacts2" + count + "\\.id").val(),
            name: $("#contacts2" + count + "\\.name").val(),
            mobile: $("#contacts2" + count + "\\.mobile").val(),
            email: $("#contacts2" + count + "\\.email").val(),
            qq: $("#contacts2" + count + "\\.qq").val(),
            level: $("#contacts2" + count + "\\.level").val(),
            mainContact: $("#contacts2" + count + "\\.mainContact").val()
        }
        contacts.push(contactTemp);
    }
    return contacts;

}

/**
 * 这个方法依赖页面中的联系人的id值
 * 如果页面组件化的话 挪到对应的页面里面去 不通用
 */
function buildNormalKeyContacts3(){

    var contacts = new Array();

    var contact1 =
    {
        id: $("#contacts30\\.id").val(),
        name: $("#contacts30\\.name").val(),
        mobile: $("#contacts30\\.mobile").val(),
        email:$("#contacts30\\.email").val(),
        qq:$("#contacts30\\.qq").val(),
        level:$("#contacts30\\.level").val(),
        mainContact:$("#contacts30\\.mainContact").val()
    };
    contacts.push(contact1);
    /*    if(isValidContact(contact1)){
     contacts.push(contact1);
     }*/

    var contact2 =
    {
        id: $("#contacts31\\.id").val(),
        name: $("#contacts31\\.name").val(),
        mobile: $("#contacts31\\.mobile").val(),
        email: $("#contacts31\\.email").val(),
        qq:$("#contacts31\\.qq").val(),
        level:$("#contacts31\\.level").val(),
        mainContact:$("#contacts31\\.mainContact").val()
    };
    contacts.push(contact2);
    /*if(isValidContact(contact2)){
     contacts.push(contact2);
     }*/

    var contact3 =
    {
        id: $("#contacts32\\.id").val(),
        name: $("#contacts32\\.name").val(),
        mobile: $("#contacts32\\.mobile").val(),
        email:$("#contacts32\\.email").val(),
        qq:$("#contacts32\\.qq").val(),
        level:$("#contacts32\\.level").val(),
        mainContact:$("#contacts32\\.mainContact").val()
    };
    contacts.push(contact3);
    /*if(isValidContact(contact3)){
     contacts.push(contact3);
     };*/

    return contacts;

}

function initContactStyle(){
    $(".tabRecord tr").not(".tabTitle").css({"border":"1px solid #bbbbbb","border-width":"1px 0px"});
    $(".tabRecord tr:nth-child(odd)").not(".tabTitle").css("background", "#eaeaea");
    $(".tabRecord tr").not(".tabTitle").hover(
        function () {
            $(this).find("td").css({"background":"#fceba9","border":"1px solid #ff4800","border-width":"1px 0px"});
            $(this).css("cursor", "pointer");
        },
        function () {
            $(this).find("td").css({"background-Color":"#FFFFFF","border":"1px solid #bbbbbb","border-width":"1px 0px 0px 0px"});
            $(".tabRecord tr:nth-child(odd)").not(".tabTitle").find("td").css("background", "#eaeaea");
        }
    );
    $(".close").hide();
    $(".table_inputContact tr").find("td:last").hover(
        function() {
            $(".close").show();
        },
        function() {
            $(".close").hide();
        }
    )
    $(".alert").hide();
    $(".hover").live("hover", function(event) {
        var _currentTarget = $(event.target).parent().find(".alert");
        _currentTarget.show();
        //因为有2px的空隙,所以绑定在parent上.
        _currentTarget.parent().mouseleave(function(event) {
            event.stopImmediatePropagation();
            if ($(event.relatedTarget).find(".alert")[0] != _currentTarget[0]) {
                _currentTarget.hide();
            }
        });
    }, function(event) {
        var _currentTarget = $(event.target).parent().find(".alert");
        if ($(event.relatedTarget).find(".alert")[0] != _currentTarget[0]) {
            $(event.target).parent().find(".alert").hide();
        }
    });
}

function validateSupplierMobiles(mobile, supplierId) {
    if (G.Lang.isEmpty(mobile)) {
        return true;
    }
    var mobileArray = new Array();
    if ($.isArray(mobile)) {
        mobileArray = mobile;
    } else {
        mobileArray.push(mobile);
    }
    var data = {};
    data["mobiles"] = mobileArray;
    data["supplierId"] = supplierId;


    //要判断是否存在同名的手机号
    var result = APP_BCGOGO.Net.syncGet({
        url: "supplier.do?method=validateSupplierMobiles",
        data: $.param(data,true) ,
        dataType: "json"
    });
    if (result && result.success) {
        return true;
    } else if (result) {
        nsDialog.jAlert(result.msg);
        return false;
    }
    return false;
}


function validateCustomerMobiles(mobile, customerId) {

    if(G.Lang.isEmpty(mobile)){
        return true;
    }
    var mobileArray = new Array();
    if($.isArray(mobile)){
        mobileArray = mobile;
    }else{
        mobileArray.push(mobile);
    }
    var data = {};
    data["mobiles"] = mobileArray;
    data["customerId"] = customerId;
    var result = APP_BCGOGO.Net.syncGet({
        url: "customer.do?method=validateCustomerMobiles",
        data: $.param(data,true),
        dataType: "json"
    });
    if(result && result.success){
        return true;
    } else if (result) {
        nsDialog.jAlert(result.msg);
        return false;
    }
    return false;
}