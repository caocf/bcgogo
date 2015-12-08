/**
 * Created by IntelliJ IDEA.
 * User: sl
 * Date: 12-4-9
 * Time: 下午2:30
 * To change this template use File | Settings | File Templates.
 */
var trCount = 0;
//设定时间控件
function initTodoCalendars() {
    $("input[id^='remindTime']").datepicker("destroy");
    $("input[id^='remindTime']").datepicker({
        "numberOfMonths" : 1,
        "showButtonPanel": true,
        "changeYear":true,
        "changeMonth":true,
        "yearRange":"c-100:c+100",
        "yearSuffix":""
    });
}

$(document).ready(function() {
    initTodoCalendars();
    //确认添加计划按钮
    $("#submitBtn").click(function() {
        var flag = validateIt();
        if (flag == false) {
            return;
        }
        createPlan();
    });
    //添加客户按钮
    $(".addCusBtn").click(function() {
        addCustomer();
        $("#flagTrNum").val($(this).prev().attr("id").split("_")[1]);
    });
    //计划更新成功后刷新本店计划栏
    $("#hidBtn").live('click', function() {
        $.ajax({
            type:"POST",
            url:"remind.do?method=getPlans&time=" + new Date(),
            data:{startPageNo:nextPageNo4},
            cache:false,
            dataType:"json",
            success:function(jsonStr) {
                initTr4(jsonStr);
                initPages(jsonStr, "dynamical5", "remind.do?method=getPlans", '', "initTr4", '', '',
                    {startPageNo:nextPageNo4}, '');
            }


        });
    });
});
//增加一行
function addRow() {
    var isReturn = false;
    var lastRowValue = $($(".addPlan:last").find(":text"));
    lastRowValue.each(function(index) {
        if ($.trim($(this).val()) == "") {
            isReturn = true;
            return false;
        }
    });
    if (isReturn == true) {
        return;
    }
    trCount++;
    var trObj = $(".addPlan:last").clone(true);
    $(trObj).attr('id', trCount);
    $(trObj).find("td").first().html(trCount + 1);
    $(trObj).find("input").each(function() {
        var inputId = $(this).attr("id");
        $(this).attr("id", inputId.split("_")[0] + "_" + trCount);
    });
    $($(".addPlan:last img").last()).css({'display':'none'});
    $(".addPlan:last").after(trObj);
    $(".addPlan:last input").val("");
    initTodoCalendars();
}
//删除一行
function deleteRow(domObj) {

    var trNum = $(".addPlan").size();
    if (trNum > 1) {
        $(domObj).closest("tr").remove();
        $($(".addPlan").last().find("img").last()).css({'display':''});
        trCount = trCount - 1;
    } else {
        $(domObj).parent().parent().find("input").each(function() {
            $(this).val("");
            trCount = 0;
        });
    }
}

//添加计划
function createPlan() {
    var plans;
    jQuery(".addPlan").each(function(index) {
        var id = jQuery(this).attr('id');
        var temp = {"remindType":jQuery("#remindType_" + id).val(),
            "content": jQuery("#content_" + id).val(),
            "customerNames":jQuery("#customerNames_" + id).val(),
            "customerIds":jQuery("#customerIds_" + id).val(),
            "remindTimeStr": jQuery("#remindTime_" + id).val(),
            "customerType": jQuery("#customerType_" + id).val()};
        if (plans == null) {
            plans = [];
        }
        plans.push(temp);
    });
    $.ajax({
        type:"POST",
        data:{plans:JSON.stringify(plans)},
        dataType:'json',
        url:"remind.do?method=createPlan",
        success:function(data) {
            closeDiv();
            var temp = ($("#plans").html()) * 1 + data.addNumber * 1;
            $("#plans").html(temp);

            $("#hidBtn").click();
            initDiv();
        }
    });

}
//添加客户
function addCustomer() {
    bcgogo.checksession({"parentWindow":window.parent,'iframe_PopupBox':$("#iframe_PopupBox")[0],'src': encodeURI("remind.do?method=addCustomer")});
}
//验证
function validateIt() {
    var flag = true;
    $(".addPlan").each(function(index) {
        var remindType = $($(this).find("input").get(0)).val();
        var content = $($(this).find("input").get(1)).val();
        var name = $($(this).find("input").get(4)).val();
        var time = $($(this).find("input").get(5)).val();

        if ($.trim(name) == "" || $.trim(remindType) == "" || $.trim(time) == "" || $.trim(content) == "") {
            alert("请填写全部内容！");
            flag = false;
            return false;
        }
    });
    return flag;
}


function initDiv() {
    $(".addPlan").each(function(index) {
        $(this).find("img:eq(2)").click();
    });
}
