/**
 * 供发送短信时选择联系人页面使用
 * Created by IntelliJ IDEA.
 * User: ZouJianhong
 * Date: 12-3-20
 * Time: 上午8:58
 * To change this template use File | Settings | File Templates.
 */

$(document).ready(

    function() {

        initDisplay();

        //绑定上一页点击事件
        $("#lastPage").click(function() {
            var pageNo = parseInt($("#pageNo").val());
            if (pageNo <= 1) {
                pageNo = 1;
            } else {
                pageNo = pageNo - 1;
            }
            getNewPageContent(pageNo);
        });

        //绑定下一页点击事件
        $("#nextPage").click(function() {
            var pageNo = parseInt($("#pageNo").val());
            var totalPage = parseInt($("#totalPage").val())
            if (pageNo >= totalPage) {
                pageNo = totalPage;
            } else {
                pageNo = pageNo + 1;
            }
            getNewPageContent(pageNo);
        });

        //绑定点击确认按钮事件
        $("#submitBtn").click(function() {
            var selectMobiles = "";
            var selectAmount = 0;
            $("#selectedInfo").children().each(function(index, element) {
                if ($(element).val() != null && $.trim($(element).val()) != '') {
                selectMobiles = selectMobiles + $(element).val() + ",";
                selectAmount = selectAmount + 1;
              }
            });
            if (selectMobiles != null && selectMobiles != "") {
                selectMobiles = selectMobiles.substring(0, selectMobiles.length - 1);
            }
            backToParentWindow();
            $("#phoneNumbers", window.parent.document).val(selectMobiles);
            $("#phoneAmount", window.parent.document).text(selectAmount);
        });

        //绑定点击取消按钮事件
        $("#cancleBtn").click(function() {
            backToParentWindow();
        });

        $("#div_close").click(function() {
            backToParentWindow();
        });

        //绑定选中全部或取消选中全部点击事件
        $("#check_all").click(function() {
            clickAllCustomer(this);
        });

    }
);

/**
 * 点击单个按钮：选中/取消选中
 * @param obj
 */
function clickSingleCustomer(obj) {
    var checkStatus = judgeIsSelected(obj);
    if (checkStatus == "on") {
        unselectSingleCustomer(obj);
    } else {
        selectSingleCustomer(obj);
    }
}

/**
 * 选中单个客户复选框事件
 * @param obj
 */
function selectSingleCustomer(obj) {
    var inputElement = $("#id_" + $(obj).attr("id"));
    if ($("#id_" + $(obj).attr("id")).attr("id") == undefined) {
        addInputElement(obj);
    }
    changeImageOn(obj);
}

/**
 * 取消选中单个客户复选框事件
 * @param obj
 */
function unselectSingleCustomer(obj) {
    removeInputElement(obj);
    changeImageOff(obj);
}

function clickAllCustomer(obj) {
    if ($(obj).attr("src") == "/web/images/check_on.jpg") {
        unselectAllCustomer();
    } else {
        selectAllCustomer();
    }
}

/**
 * 选中全部客户复选框事件
 */
function selectAllCustomer() {
    changeImageOn($("#check_all"));
    $("img[src='/web/images/check_off.jpg']").each(function(index, element) {
        selectSingleCustomer($(element));
    })
}

/**
 * 取消选中全部客户复选框事件
 */
function unselectAllCustomer() {
    changeImageOff($("#check_all"));
    $("img[src='/web/images/check_on.jpg']").each(function(index, element) {
        unselectSingleCustomer(element);
    })
}

/**
 * 添加一个选中元素到hidden列表
 * @param obj
 */
function addInputElement(obj) {
    var inputHtml = "<input type=\"hidden\" id=\"id_" + $(obj).attr("id") + "\" name=\"\" value=\"" + $(obj).attr("name") + "\" />";
    $("#selectedInfo").append($(inputHtml));
}

/**
 * 从hidden列表移除一个取消选中的hidden元素
 * @param obj
 */
function removeInputElement(obj) {
    $("#id_" + $(obj).attr("id")).remove();
}

/**
 * 判断当前客户是否被选中
 * @param obj
 */
function judgeIsSelected(obj) {
    var hiddenElement = $("#id_" + $(obj).attr("id"));
    if ($("#id_" + $(obj).attr("id")).val() != undefined) {
        return "on";
    } else {
        return "off";
    }
}

/**
 * 把图标切换为选中
 * @param obj
 */
function changeImageOn(obj) {
    $(obj).attr("src", "/web/images/check_on.jpg");
}

/**
 * 把图标切换为未选中
 * @param obj
 */
function changeImageOff(obj) {
    $(obj).attr("src", "/web/images/check_off.jpg");
}

/**
 * 初始化按钮显示状态（下一页、上一页、当前页码、每一条数据的是否选中图标）
 */
function initDisplay() {
    var currentPage = parseInt($("#pageNo").val());
    var totalPage = parseInt($("#totalPage").val());
    if (currentPage <= 1) {
        $("#lastPage").css("display", "none");
    } else {
        $("#lastPage").css("display", "block");
    }
    if (currentPage >= totalPage) {
        $("#nextPage").css("display", "none");
    } else {
        $("#nextPage").css("display", "block");
    }

    $("#currentPage").text($("#pageNo").val());

    isChecked();

//    $("td img").click(function(){
//        clickSingleCustomer($(this));
//    })
}

function isChecked() {
       var allSelected = "on";
    $("td img").each(function(index, element) {
        var checkStatus = judgeIsSelected($(element));
        if (checkStatus == "on") {
            selectSingleCustomer($(element))
        } else {
            allSelected = "off";
            unselectSingleCustomer($(element));
        }
    });
    if (allSelected == "on") {
        changeImageOn($("#check_all"));
    } else {
        changeImageOff($("#check_all"));
    }
}

/**
 * 根据页码从后台获取一页客户信息的html文本，并替换原有的内容
 * @param pageNo
 */
function getNewPageContent(pageNo) {
    $.ajax({
        type:"POST",
        url:"customer.do?method=getSmsOnePageCustomer",
        async:true,
        data:{
            pageNo:pageNo
        },
        cache:false,
        success:function(html) {
            if (html == null || html == "") {
                return;
            }
            $("#customerPage").html(html);
            initDisplay();
        }
    });
}

function init(jsonStr) {
    $(".cus_current").html("共有" + jsonStr[jsonStr.length - 1].totalRows + "人");
    $("#chk_show tr").remove();
    if (jsonStr.length > 1) {
        for (var i = 0; i < jsonStr.length - 1; i++) {
            var licenceNo = jsonStr[i].licenceNo == null ? " " : jsonStr[i].licenceNo;
            var name = jsonStr[i].name == null ? " " : jsonStr[i].name;
            var mobile = jsonStr[i].mobile == null ? " " : jsonStr[i].mobile;
            var carDateStr = jsonStr[i].carDateStr == null ? " " : jsonStr[i].carDateStr;
            var birthdayStr = jsonStr[i].birthdayStr == null ? " " : jsonStr[i].birthdayStr;
            var customerIdString = jsonStr[i].customerIdString == null ? " " : jsonStr[i].customerIdString;
            var nameStr = name.length > 8 ? name.substring(0, 8) : name;

            var tr = '<tr>';
            tr += '<td><img src=\"/web/images/check_off.jpg\" onclick=\"clickSingleCustomer(this)\" alt=\"\" class=\"noclass\" id=' + customerIdString + ' name=' + mobile + '></td>'
            tr += '<td >' + (i + 1) + '&nbsp;</td>';
            tr += '<td >' + licenceNo + '&nbsp;</td>';
            tr += '<td title=' + name + '>' + nameStr + '&nbsp;</td>';
            tr += '<td>' + mobile + '&nbsp;</td>';
            tr += '<td>' + carDateStr + '&nbsp;</td>';
            tr += '<td>' + birthdayStr + '&nbsp;</td>';
            tr += '<input type="hidden" id=\"phone\" ' + i + ' value=' + mobile + '/>';
            tr += '<td></td>'
            tr += '</tr >';
            $("#chk_show").append($(tr));
        }
    }
//  $("#chk_show").html(html);
  initDisplay();
}

/**
 * 操作界面回到父页面
 */
function backToParentWindow() {
    $("#iframe_PopupBox", window.parent.document).css("display", "none");
    $("#mask", window.parent.document).css("display", "none");
    try {
        $(window.parent.document.body).find("input[type='button']").eq(0).focus().blur();
    } catch(e) {
        ;
    }
}
