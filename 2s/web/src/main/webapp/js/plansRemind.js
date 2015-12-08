// JavaScript Document


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
var userInfoMap = new map();
$(function() {
    $("#tb_tui tr:even").css("background", "#E5E5E5");
    $("#tb_tui tr").hover(

    function() {
        $(this).css("background", "#cce6f9");
        $(this).css("cursor", "pointer");
    }, function() {
        $(this).css("background", "#fff");
        jQuery("#tb_tui tr:even").css("background", "#E5E5E5");

    });


    var customerSmsInput = APP_BCGOGO.Module.customerSmsInput;

    customerSmsInput.init({
        "selector": $("#testDiv"),
        "width": 1000,
        "onSelectPerson": function(event) {
            var length = customerSmsInput.getData().length;

            bcgogo.checksession({
                "parentWindow": window.parent,
                'iframe_PopupBox': $("#iframe_PopupBox")[0],
                'src': 'remind.do?method=selectMan&personNum=' + length
            });
        },
        "onSave": function(event, isSendImmediately) {

            var nameStr = customerSmsInput.getValuesByKey("name");


            if(nameStr.indexOf("全体用户") != -1) {
                customerSmsInput.delData("allCustomer");
                customerSmsInput.delData("allSupplier");
                customerSmsInput.delData("allMember");
            } else if(nameStr.indexOf("全体客户") != -1) {
                customerSmsInput.delData("allMember");
            }

            saveOrUpdateShopPlan(customerSmsInput, isSendImmediately);

        },
        "onClear": function(event) {
            emptyPlan();
            customerSmsInput.setState(customerSmsInput.STATE.CREATE);
            customerSmsInput.clearData();
            userInfoMap.remove("currentId");
        }
    });

    $("#addContent").live("click", function() {
        customerSmsInput.addData(JSON.parse($("#addContent").val()));
    });

    $(".edit").live("click", function() {

        if("已提醒" == $.trim($(this).closest("td").prev().text())) {
            return;
        }

        $("#confirm").attr("idStr", $(this).attr("plansid"));
        customerSmsInput.clearData();
        customerSmsInput.addData(userInfoMap.get($(this).attr("plansid")));
        userInfoMap.put("currentId", $(this).attr("plansid"));
        customerSmsInput.setState(customerSmsInput.STATE.CHANGE);
        initEditPlansInfo(this);
    });

    $(".tab_last").live("dblclick", function() {

        if("已提醒" == $.trim($(this).find("td").eq(7).text())) {
            return;
        }

        var editSpan = $(this).find("span").eq(1);
       customerSmsInput.clearData();
        $("#confirm").attr("idStr", editSpan.attr("plansid"));
        customerSmsInput.addData(userInfoMap.get(editSpan.attr("plansid")));
        userInfoMap.put("currentId", editSpan.attr("plansid"));
        customerSmsInput.setState(customerSmsInput.STATE.CHANGE);
        initEditPlansInfo(editSpan);
    });

    $(".delete").live("click", function() {
        var id = $(this).attr("plansId");
        var status = $.trim($(this).closest("tr").find("td").eq(7).text());
        var remindStr = $.trim($(this).closest("tr").find("td").eq(6).text());
        if("已提醒" == status) {
            return;
        }
        $.ajax({
            type: "POST",
            data: {
                idStr: id
            },
            url: "remind.do?method=dropPlan",
            success: function(data) {
                var temp = ($("#totalRows").html()) * 1 - 1;
                $("#totalRows").html(temp);
                if("已提醒" == status) {
                    $("#remindedNum").html($("#remindedNum").html() * 1 - 1);
                } else {
                    if(judgeExpired(remindStr)) {
                        $("#activityNoExpired").html($("#activityNoExpired").html() - 1);
                    } else {
                        $("#activityExpired").html($("#activityExpired").html() - 1);
                }
                    }
                var currentPage = $("#getPagedynamical5").val() * 1;
                if(temp % 10 == 0 && currentPage > 1) {
                    currentPage = currentPage - 1;
                }
                var currentId = userInfoMap.get("currentId");
                initPlancurrentPage(currentPage);
                if(id == currentId) {
                    customerSmsInput.setState(customerSmsInput.STATE.CREATE);
                    emptyPlan();
                    customerSmsInput.clearData();
                } else {
                    userInfoMap.put("currentId", currentId);
                }

            }
        });
    });

    $("#confirm").live("click", function() {
        saveOrUpdateShopPlan();
    });

    $("#totalRows").live("click", function() {
        if("totalRows" != $.trim($("#tableStatus").val())) {
            $("#tableStatus").val("totalRows");
            initPlancurrentPage(1);
            //            initRows();
        }
    });
    $("#activityNoExpired").live("click", function() {
        if("activityNoExpired" != $.trim($("#tableStatus").val())) {
            $("#tableStatus").val("activityNoExpired");

            initPlancurrentPage(1);
        }
    });
    $("#activityExpired").live("click", function() {
        if("activityExpired" != $.trim($("#tableStatus").val())) {
            $("#tableStatus").val("activityExpired");

            initPlancurrentPage(1);
        }
    });

    $("#remindedNum").live("click", function() {
        if("reminded" != $.trim($("#tableStatus").val())) {
            $("#tableStatus").val("reminded");

            initPlancurrentPage(1);
        }
    });

    //    $("#contact,#person").keyup(function(e)
    //    {
    //        var e = e || event;
    //
    //        $(this).val($(this).val().replace("，",","));
    //
    //        if(e.keyCode==188)
    //        {
    //            var mobiles = $.trim($(this).val());
    //            mobiles =  mobiles.substring(0,mobiles.length-1);
    //
    //            $(this).val(mobiles);
    //
    //            if(mobiles && mobiles.substring(mobiles.length-1,mobiles.length)!=",")
    //            {
    //                $(this).val(mobiles+",");
    //            }
    //        }
    //
    //        if(e.keyCode == 108 || e.keyCode==13 || e.keyCode==32)
    //        {
    //            var contact = $.trim($(this).val());
    //            $(this).val(contact);
    //            if(contact && contact.substring(contact.length-1,contact.length)!="," && contact.substring(contact.length-1,contact.length)!="，")
    //            {
    //                $(this).val(contact+",");
    //            }
    //        }
    //    });
    $("#addUser,#addMobile").keyup(function(e) {
        e = e || event;

        if(e = 108 || e == 13) {
            //回车的处理
            var user = $("#addUser").val();
            var mobile = $("#addUser").val();
            if(!$("#addUser").val() && !$("#addMobile").val()) {
                return;
            } else {
                if($("#addMobile").val() && !APP_BCGOGO.Validator.stringIsMobilePhoneNumber(mobile)) {
                    //手机号不正确的处理
                    return;
                }

                var obj = {
                    name: user,
                    mobile: mobile
                };
            }
        } else {
            //不是回车的处理。。。过滤
        }
    });

    $("#addBtn").live("click", function() {
        var user = $("#addUser").val();
        var mobile = $("#addUser").val();
        if(!$("#addUser").val() && !$("#addMobile").val()) {
            return;
        } else {
            if($("#addMobile").val() && !APP_BCGOGO.Validator.stringIsMobilePhoneNumber(mobile)) {
                //手机号不正确的处理
                return;
            }

            var obj = {
                name: user,
                mobile: mobile
            };
        }
    });

    $("#selectMan").live("click", function() {


        var length = 0;

        var persons = $("#person").val();
        if(persons) {
            var personArr = persons.split(",");
            length = personArr.length;
        }


        bcgogo.checksession({
            "parentWindow": window.parent,
            'iframe_PopupBox': $("#iframe_PopupBox")[0],
            'src': 'remind.do?method=selectMan&personNum=' + length
    });
    });

    $("#cancel").live("click", function() {
        emptyPlan();
    });

    //    $("#person,#contact").mouseenter(function(){
    //        $(this)[0].title = $(this).val();
    //    });
    $(".sendMsg").live("click", function() {
        if("已提醒" == $.trim($(this).closest("td").prev().text())) {
            return;
        }
        window.document.location = "sms.do?method=smswrite&shopPlanId=" + $(this).next().attr("plansId");
    });
});

function initEditPlansInfo(obj) {
    var currentTd = $(obj).closest("td");

    var tds = currentTd.closest("tr");


    $("#confirm").val("修改计划");
    $("#remindType").val(tds.find(".remindType").text());
    $("#remindTime").val(tds.find(".remindTime").text());
    $("#remindContext").val(tds.find(".content").text());
    $("#person").val(tds.find(".customerNames").text());
    $("#contact").val(tds.find(".contact").text());
}

function initTr4(jsonStr) {
    $("#tb_tui tr:not(:first)").remove();
    userInfoMap.clearMap();
    if(jsonStr.length > 1) {
        for(var i = 0; i < jsonStr.length - 1; i++) {

            var statusStr = jsonStr[i].status == "reminded" ? "已提醒" : "未提醒";
            var idStr = jsonStr[i].idStr == null ? " " : jsonStr[i].idStr;
            var remindType = jsonStr[i].remindType == null ? " " : jsonStr[i].remindType;
            var content = jsonStr[i].content == null ? " " : jsonStr[i].content;
            var customerNames = jsonStr[i].customerNames == null ? " " : jsonStr[i].customerNames;
            var remindTimeStr = jsonStr[i].remindTimeStr == null ? " " : jsonStr[i].remindTimeStr;
            var customerIds = jsonStr[i].customerIds == null ? " " : jsonStr[i].customerIds;
            var customerType = jsonStr[i].customerType == null ? " " : jsonStr[i].customerType;
            var contact = jsonStr[i].contact == null ? " " : jsonStr[i].contact;
            var useInfo = jsonStr[i].userInfo == null ? " " : jsonStr[i].userInfo;
            var tr = '<tr class="tab_last table-row-original">';
            tr += '<td style="border-left:none;">' + (i + 1) + '</td>';
            tr += '<td title="' + remindType + '" class="remindType" >' + remindType + '</td>';
            tr += '<td title="' + content + '" class="content" >' + content + '&nbsp;</td>';
            tr += '<td title="' + customerNames + '" class="customerNames" >' + customerNames + '<input type="hidden" value="' + customerIds + '" class="Ids"/>&nbsp;</td>';
            if(differenceDay(remindTimeStr, 2) && "未提醒" == statusStr) {
                tr += '<td title="' + contact + '" style="color:red" class="contact" >' + contact + '&nbsp;</td>';
                tr += '<td style="color:red" class="remindTime" >' + remindTimeStr + '&nbsp;</td>';
                tr += '<td style="color:red" class="status" >' + statusStr + '&nbsp;</td>';
            } else {
                tr += '<td title="' + contact + '" class="contact" >' + contact + '&nbsp;</td>';
                tr += '<td class="remindTime" >' + remindTimeStr + '&nbsp;</td>';
                tr += '<td class="status" >' + statusStr + '&nbsp;</td>';
            }
            if("已提醒" == statusStr) {
                tr += '<td><span class="sendMsg a" style="color:#000" contact="' + contact + '" content="' + content + '">发短信</span>&nbsp;|&nbsp;<span class="edit a" style="color:#000" plansId="' + idStr + '">编辑</span>&nbsp;|&nbsp;<span class="delete a" style="color:#000" plansId="' + idStr + '">删除</span></td>';
            } else {
                tr += '<td><span class="sendMsg a" contact="' + contact + '" content="' + content + '">发短信</span>&nbsp;|&nbsp;<span class="edit a" plansId="' + idStr + '">编辑</span>&nbsp;|&nbsp;<span class="delete a" plansId="' + idStr + '">删除</span></td>';
            }
            tr += '</tr >';
            $("#tb_tui").append($(tr));
            userInfoMap.put(idStr, JSON.parse(useInfo));
        }
    }
    tableUtil.tableStyle('#tb_tui', '.tab_title');

    $("#tb_tui tr:even").css("background", "#E5E5E5");
    $("#tb_tui tr").hover(

    function() {
        $(this).css("background", "#cce6f9");
        $(this).css("cursor", "pointer");
    }, function() {
        $(this).css("background", "#fff");
        jQuery("#tb_tui tr:even").css("background", "#E5E5E5");

    });
}

/**
 *  dateStr为字符串，day为整形
 * @param dateStr 需要传入的时间（比当前时间大，否则返回false）
 * @param day  date-当前时间差的天数是否<=day，是则返回true，否则为false
 */

function differenceDay(dateStr, day) {
    var nowTime = new Date(); // 当前时间
    var the_year  = nowTime.getFullYear();
    var the_month = nowTime.getMonth() + 1;
    var the_day   = nowTime.getDate();
    nowTime = new Date(the_year, the_month, the_day);

    var date = new Date(dateStr.substring(0, 4), dateStr.substring(5, 7), dateStr.substring(8, dateStr.length));

    if(date < nowTime) {
        return false;
    }

    var thesecond = 24 * 60 * 60 * 1000;

    var result = (date - nowTime) / thesecond;

    if(result > 2) {
        return false;
    } else if(result == 2 && (date - nowTime) % thesecond > 0) {
        return false;
    } else {
        return true;
    }
}

function todayStr() {
    var nowTime = new Date(); // 当前时间
    var the_year  = nowTime.getFullYear();
    var the_month = nowTime.getMonth() + 1;
    var the_day   = nowTime.getDate();

    return the_year + "-" + (the_month < 10 ? "0" + the_month : the_month) + "-" + (the_day < 10 ? "0" + the_day : the_day);
}

function initPlancurrentPage(currentPage) {
    $.ajax({
        type: "POST",
        url: "remind.do?method=getPlans",
        data: {
            startPageNo: currentPage,
            tableStatus: $("#tableStatus").val()
        },
        cache: false,
        dataType: "json",
        success: function(jsonStr) {
            initTr4(jsonStr);
            initPages(jsonStr, "dynamical5", "remind.do?method=getPlans", '', "initTr4", '', '', {
                startPageNo: currentPage,
                tableStatus: $("#tableStatus").val()
            }, '');
        }
    });
}

function judgeExpired(dateStr) {
    var nowTime = new Date(); // 当前时间
    var the_year  = nowTime.getFullYear();
    var the_month = nowTime.getMonth() + 1;
    var the_day   = nowTime.getDate();
    nowTime = new Date(the_year, the_month, the_day);

    var date = new Date(dateStr.substring(0, 4), dateStr.substring(5, 7), dateStr.substring(8, dateStr.length));

    if(date < nowTime) {
        return false;
    } else {
        return true;
    }
}

function updatePlan(customerSmsInput, isSendImmediately) {
    var date = {
        id: userInfoMap.get("currentId"),
        remindType: $("#remindType").val(),
        content: $("#remindContext").val(),
        customerNames: customerSmsInput.getValuesByKey("name"),
                remindTimeStr: $("#remindTime").val(),
        contact: customerSmsInput.getValuesByKey("mobile"),
        userInfo: JSON.stringify(customerSmsInput.getData())
                };

    $.ajax({
        type: "POST",
        data: {
            plans: JSON.stringify(date)
        },
        dataType: 'json',
        url: "remind.do?method=updatePlan",
        success: function(data) {
            if("error" == data.resu) {
                alert("更新失败！");
                return false;
            }

            if(isSendImmediately) {
                window.document.location = "sms.do?method=smswrite&shopPlanId=" + data.id;
            } else {
                emptyPlan();
                $("#tableStatus").val("totalRows");
                initPlancurrentPage(1);
                customerSmsInput.clearData();

                customerSmsInput.setState(customerSmsInput.STATE.CREATE);
            }
        }
    });
}

function savePlan(customerSmsInput, isSendImmediately) {
    var date = {
        remindType: $("#remindType").val(),
        content: $("#remindContext").val(),
        customerNames: customerSmsInput.getValuesByKey("name"),
                remindTimeStr: $("#remindTime").val(),
        contact: customerSmsInput.getValuesByKey("mobile"),
        userInfo: JSON.stringify(customerSmsInput.getData())
                };

    $.ajax({
        type: "POST",
        data: {
            plans: JSON.stringify(date)
        },
        dataType: 'json',
        url: "remind.do?method=createPlan",
        success: function(data) {
            if("error" == data) {
                alert("保存失败");
                return;
            }
            if(isSendImmediately) {
                window.document.location = "sms.do?method=smswrite&shopPlanId=" + data.id;
            } else {
                $("#totalRows").html($("#totalRows").html() * 1 + 1);
                $("#activityNoExpired").html($("#activityNoExpired").html() * 1 + 1);
                emptyPlan();
                $("#tableStatus").val("totalRows");
                initPlancurrentPage(1);
                customerSmsInput.clearData();

                customerSmsInput.setState(customerSmsInput.STATE.CREATE);
            }
        }
    });
}

function emptyPlan() {
    $("#confirm").val("新增计划");
    $("#confirm").attr("idStr", "");
    $("#remindType").val("");
    $("#remindContext").val("");
    $("#person").val("");
    $("#remindTime").val("");
    $("#contact").val("");
    $("#addUser").val("");
    $("#addMobile").val("");
}

function initRows() {
    $.ajax({
        type: "POST",
        data: {
            now: new Date()
        },
        dataType: 'json',
        url: "remind.do?method=getRowInfo",
        success: function(data) {
            $("#totalRows").html(data.totalRows);
            $("#activityNoExpired").html(data.activityNoExpired);
            $("#activityExpired").html(data.activityExpired);
            $("#remindedNum").html(data.reminded);
        }
    });
}

function showDatepicker(node) {
    if($(node).hasClass("isDatepickerInited")) {
        $(node).removeClass("isDatepickerInited");
        $(node).datepicker({
            "changeYear": true,
            "changeMonth": true,
            "showButtonPanel": true,
            "numberOfMonths": 1,
            "yearRange": "c-100:c+100",
            "showOn": "span",
            "yearSuffix": ""
        });
    }
    $(node).datepicker("show");

    $(node).blur();
}

function validateContact() {
    var mobiles = $("#contact").val().toString().split(",");
    var mobile = "";
    var msg = "";
    for(var i = 0; i < mobiles.length; i++) {
        mobile = mobiles[i];
        if("全体人员" != mobile && "全体客户" != mobile && "全体供应商" != mobile && "全体会员" != mobile && !APP_BCGOGO.Validator.stringIsMobilePhoneNumber(mobile)) {
            msg += "第" + (i + 1) + "个手机号格式不对\n";
        }
    }

    if(msg) {
        alert(msg);
        return false;
    } else {
        return true;
    }
}


function saveOrUpdateShopPlan(customerSmsInput,isSendImmediately)
{
    if(GLOBAL.Lang.isEmpty($("#remindType").val()) || GLOBAL.Lang.isEmpty($("#remindTime").val()))
    {
        nsDialog.jAlert("提醒项目和提醒时间不能为空!");
    }
    else{
        if(!$("#remindTime").val() || !judgeExpired($("#remindTime").val())) {
            nsDialog.jAlert("提醒时间不能小于今天");
        }
        else{
            if(userInfoMap.get("currentId")) {
                updatePlan(customerSmsInput, isSendImmediately);
            } else {
                savePlan(customerSmsInput, isSendImmediately);
            }
        }
    }
}