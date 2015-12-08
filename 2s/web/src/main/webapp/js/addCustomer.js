/**
 * Created by IntelliJ IDEA.
 * User: sl
 * Date: 12-4-11
 * Time: 下午5:23
 * To change this template use File | Settings | File Templates.
 */
var isTheLastPage = false;
var nextPageNo = 1;
var checkedIds = "";
var checkedNames = "";
$(document).ready(function() {
//  window.parent.addHandle(document.getElementById('div_drag'), window);
//  $.ajax({
//    type:"POST",
//    url:"remind.do?method=getCustomers",
//    data:{startPageNo:nextPageNo},
//    cache:false,
//    dataType:"json",
//    success:function(jsonStr) {
////          alert(jsonStr);
//      initTr(jsonStr);
//      if (isTheLastPage == true && nextPageNo == 1) {
//        $("#pageNo_id>div:eq(1)").css('display', 'none');
//      }
//      if (isTheLastPage == true) {
//        $("#pageNo_id>div:last").css('display', 'none');
//
//      }
//      if (nextPageNo == 1) {
//        $("#pageNo_id>div:first").css('display', 'none');
//      }
//    }
//
//
//  });
  //checkbox
  $(".checkB").live("click", function() {
    keepChecked();
    isAllChecked();
  });
  //分页
  $("#pageNo_id>div").bind("click", function() {
    $("#chk_show tr").remove();
    var selectItem = $(this).html();
    if (selectItem == "上一页") {
      if (nextPageNo > 1) {
        nextPageNo = nextPageNo - 1;
        $("#thisPageNo").html(nextPageNo);
        $("#pageNo_id>div:last").css('display', 'block');
        if (nextPageNo == 1) {
          $(this).css('display', 'none');
        }
      }
    } else if (selectItem == "下一页") {
      if (!isTheLastPage) {
        nextPageNo = nextPageNo + 1;
        $("#thisPageNo").html(nextPageNo);
        $("#pageNo_id>div:first").css('display', 'block');
      }
    }
    $.ajax({
      type:"POST",
      url:"remind.do?method=getCustomers",
      data:{startPageNo:nextPageNo},
      cache:false,
      dataType:"json",
      success:function(jsonStr) {
        initTr(jsonStr);
        getChecked();
        isAllChecked();
        if (jsonStr[jsonStr.length - 1].isTheLastPage == "true") {
          $("#pageNo_id>div:last").css('display', 'none');
        }
      }
    });
  });
//  确定选择按钮
  $("#submitBtn").click(function() {
    var trNum = window.parent.document.getElementById("flagTrNum").value;
    //显示在客户框中最后会以逗号结尾，把它截取掉
        checkedNames = checkedNames.substring(0, checkedNames.length - 1);
    var checkNamesArray = checkedNames.split(",");
        if (checkNamesArray.length > 3) {
            checkedNames = checkNamesArray[0] + "," + checkNamesArray[1] + "," + checkNamesArray[2] + "...(" + checkNamesArray.length + "位)";
    }
    window.parent.document.getElementById("customerNames_" + trNum).value = checkedNames;
    window.parent.document.getElementById("customerIds_" + trNum).value = checkedIds;
    window.parent.document.getElementById("customerType_" + trNum).value = "normal";
    closeWindow();
  });
  //添加所有客户按钮
    $("#addAllCustomer").click(function() {
    var trNum = window.parent.document.getElementById("flagTrNum").value;
    window.parent.document.getElementById("customerNames_" + trNum).value = "全体客户";
    window.parent.document.getElementById("customerIds_" + trNum).value = "";
    window.parent.document.getElementById("customerType_" + trNum).value = "all";
    closeWindow();
  });
  //关闭按钮
    $("#div_close,#cancleBtn").click(function() {
    closeWindow();
  });
  //全选按钮
    $("#checkAll").click(function() {
    checkAll();
    keepChecked();
  });
});


function initTr(jsonStr) {
  $("#chk_show tr").remove();
  if (jsonStr.length > 1) {
    for (var i = 0; i < jsonStr.length - 1; i++) {

      var licenceNo = jsonStr[i].licenceNo == null ? " " : jsonStr[i].licenceNo;
      var customerId = jsonStr[i].customerIdString == null ? " " : jsonStr[i].customerIdString;
      var name = jsonStr[i].name == null ? " " : jsonStr[i].name;
            var nameStr = name.length > 8 ? name.substring(0, 8) : name;
      var contact = jsonStr[i].contact == null ? " " : jsonStr[i].contact;
            var contactStr = contact.length > 8 ? contact.substring(0, 8) : contact;
      var mobile = jsonStr[i].mobile == null ? " " : jsonStr[i].mobile;
      var tr = '<tr>';
      tr += '<td style="border-left:none;"><input type="checkbox" name="checks" class="checkB"/></td>';
            tr += '<td >' + ((nextPageNo * 1 - 1) * 5 + i + 1) + '</td>';
      tr += '<td>' + '<input type="hidden" value="' + customerId + '">' + licenceNo + '</td>';
            tr += '<td title="' + name + '">' + nameStr + '</td>';
            tr += '<td title="' + contact + '">' + contactStr + '</td>';
      tr += '<td>' + mobile + '</td>';
      tr += '</tr >';
      $("#chk_show").append($(tr));
    }
  }

            getChecked();
        isAllChecked();
}

function getChecked() {
  $(".checkB").each(function(index) {
    var customerId = $(this).parent().next().next().children("input").val();
    if (checkedIds.indexOf(customerId) != -1) {
      $(this).attr('checked', true);
    }
  });
}

function keepChecked() {
  $(".checkB").each(function(index) {
    var customerId = $(this).parent().next().next().children("input").val();
    var customerName = $(this).parent().next().next().next().html();
    if ($(this).attr('checked') == true) {
      if (checkedIds.indexOf(customerId) == -1 && checkedNames.indexOf(customerName) == -1) {
        checkedIds = checkedIds + customerId + ",";
        checkedNames = checkedNames + customerName + ",";
      }
    }
    if ($(this).attr('checked') == false) {
      if (checkedIds.indexOf(customerId) != -1 && checkedNames.indexOf(customerName) != -1) {
        checkedIds = checkedIds.replace((customerId + ","), "");
        checkedNames = checkedNames.replace((customerName + ","), "");
      }
    }
  });
}

function checkAll() {
    if ($("#checkAll").attr("checked") == true) {
        $(".checkB").attr('checked', true);
    } else {
        $(".checkB").attr('checked', false);
  }
}


function isAllChecked() {
  var i = $(".checkB").size();
    for (i = 0; i < $(".checkB").size(); i++) {
        if ($(".checkB").eq(i).attr('checked') == false) {
            $("#checkAll").attr('checked', false);
      break;
    }
        $("#checkAll").attr('checked', true);
  }
}


function closeWindow() {
  window.parent.document.getElementById("iframe_PopupBox").style.display = "none";
//  window.parent.document.getElementById("mask").style.display = "none";
    try {
        $(window.parent.document.body).find("input[type='button']").eq(0).focus().blur();
    } catch(e) {
        ;
    }
}


