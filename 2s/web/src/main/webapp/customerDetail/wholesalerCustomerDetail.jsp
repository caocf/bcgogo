<%--
  Created by IntelliJ IDEA.
  User: lw
  Date: 14-1-23
  Time: 下午4:26
  To change this template use File | Settings | File Templates.
--%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ include file="/WEB-INF/views/includes.jsp" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
<title>客户管理--客户资料</title>
<link rel="stylesheet" href="js/extension/jquery/plugin/jquery-ui/themes/redmond/jquery.ui.timepicker-addon.css"/>
<link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/addCheck<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/moneyDetail<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/register<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" href="js/components/themes/bcgogo-droplist.css"/>
<!--add by zhuj-->
<link rel="stylesheet" type="text/css" href="styles/up<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css"
      href="js/components/themes/bcgogo-multiselectTwoDialog<%=ConfigController.getBuildVersion()%>.css"/>


<style type="text/css">
  .lineTitle span{ float:left;}
  .lineTitle span a{ margin:0 5px;}
  .lineTitle{width:988px; height:32px; float:left; color:#272727; font-size:14px; font-weight:bold; line-height:32px; padding-left:10px; border:#dddddd 1px solid;}

</style>

<%@include file="/WEB-INF/views/header_script.jsp" %>
<script type="text/javascript">
  <bcgogo:permissionParam permissions="WEB.CUSTOMER_MANAGER.CUSTOMER_DELETE,WEB.CUSTOMER_MANAGER.CUSTOMER_MODIFY">
  APP_BCGOGO.Permission.CustomerManager.CustomerDelete =${WEB_CUSTOMER_MANAGER_CUSTOMER_DELETE};
  APP_BCGOGO.Permission.CustomerManager.CustomerModify =${WEB_CUSTOMER_MANAGER_CUSTOMER_MODIFY};
  userGuide.currentPageIncludeGuideStep = "CUSTOMER_APPLY_GUIDE_SUCCESS";
  </bcgogo:permissionParam>

  <bcgogo:permissionParam permissions="WEB.VERSION.HAS_CUSTOMER_CONTACTS,WEB.VERSION.HAS_SUPPLIER_CONTACTS">
  APP_BCGOGO.Permission.Web_Version_Has_Customer_Contact =${WEB_VERSION_HAS_CUSTOMER_CONTACTS}; // 客户多联系人
  APP_BCGOGO.Permission.Web_Version_Has_Supplier_Contact =${WEB_VERSION_HAS_SUPPLIER_CONTACTS};
  </bcgogo:permissionParam>

</script>
<script type="text/javascript" src="js/utils/tableUtil<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/script<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/detailsArrears<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/customerDetail<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/checkMobileAndTelphone<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/vehiclelicenceNo<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript"
        src="js/statementAccount/statementAccountUtil<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/page/customer/modifyClient<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/contact<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript"
        src="js/customerOrSupplier/customerSupplierBusinessScope<%=ConfigController.getBuildVersion()%>.js"></script>

<script type="text/javascript"
        src="js/components/ui/bcgogo-multiselectTwoDialog<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript"
        src="js/components/ui/bcgogo-multiselectTwoDialogTree<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/utils/stringUtil<%=ConfigController.getBuildVersion()%>.js"></script>


<script type="text/javascript" src="js/page/search/inquirySystemOrder<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/components/ui/bcgogo-droplist.js"></script>

<script type="text/javascript"
        src="js/customerDetail/customerDetailInfo<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/statementAccount/customerBill<%=ConfigController.getBuildVersion()%>.js"></script>

<script type="text/javascript">
// add by zhu 联系人  既是客户又是供应商的 弹出页面用到
$(document).ready(function () {
  // 绑定 联系人列表 删除事件
  $("#modifyClientDiv .close").live("click", delContact);
  // 绑定 联系人列表 点击成为主联系人事件
  $("#modifyClientDiv .icon_grayconnacter").live("click", switchTrContact);

});

function delContact() {
  var $single_contacts = $(this).closest("tr").siblings(".single_contact_gen").andSelf();
  if ($single_contacts && $single_contacts.length > 3) {
    $(this).closest("tr").remove();
    if ($single_contacts.length - 1 <= 3) {
      $(".warning").hide();
    }
  } else {
    //  $(this).parent().siblings().children('input').val("") has a bug
    // see http://www.w3.org/TR/2011/WD-html5-20110525/the-input-element.html   dirty flag related
    $(this).parent().siblings().children('input').val(" ");
  }

}

// 这个方法和tr下面的元素位置直接相关 修改该方法 请注意各个元素的位置
function switchTrContact() {

  var $mainContact = $(this).closest("tr").siblings().find('.icon_connacter');
  $mainContact.removeClass('icon_connacter').addClass('icon_grayconnacter').addClass('hover'); // 主联系人灰化

  $(this).removeClass('icon_grayconnacter').removeClass('hover').addClass('icon_connacter'); // 当前联系人转变为主联系人

  var $alert = $(this).siblings(".alert"); // 保存alert div
  $(this).siblings(".alert").remove();

  $(this).siblings("input[id$='mainContact']").val("1"); // 设置为主联系人1

  var currentLevel = $(this).siblings("input[id$='level']").val();
  $(this).siblings("input[id$='level']").val("0");

  $(this).unbind("click");

  $alert.insertAfter($mainContact).hide(); // 添加alert

  $mainContact.siblings("input[id$='mainContact']").val("0"); // 修改非主联系人
  $mainContact.siblings("input[id$='level']").val(currentLevel);
  $mainContact.live("click", switchTrContact);
}


$(document).ready(function () {
  if ($("#businessScopeTreeDiv").length > 0) {
    var multiSelectTwoDialogTree = new App.Module.MultiSelectTwoDialogTree();
    App.namespace("components.multiSelectTwoDialogTree");
    App.components.multiSelectTwoDialogTree = multiSelectTwoDialogTree;
    APP_BCGOGO.Net.asyncPost({
      url: "businessScope.do?method=getAllBusinessScope",
      success: function (data) {
        if (G.isEmpty(data)) {
          return;
        }
        var ensureDataList = [];
        if (!G.Lang.isEmpty($("#thirdCategoryNodeListJson").val())) {
          ensureDataList = JSON.parse(decodeURIComponent(G.Lang.normalize($("#thirdCategoryNodeListJson").val())));
        }
        multiSelectTwoDialogTree.init({
          "startLevel": 2,
          "data": data,
          "ensureDataList": ensureDataList,
          "selector": "#businessScopeTreeDiv",
          "onSearch": function (searchWord, event) {
            return App.Net.syncPost({
              url: "businessScope.do?method=getAllBusinessScope",
              data: {"searchWord": searchWord},
              dataType: "json"
            });
          }
        });
      }
    });
  }
  if ($("#vehicleBrandModelDiv").length > 0) {
    var multiSelectTwoDialog = new App.Module.MultiSelectTwoDialog();
    App.namespace("components.multiSelectTwoDialog");
    App.components.multiSelectTwoDialog = multiSelectTwoDialog;
    APP_BCGOGO.Net.asyncPost({
      url: "businessScope.do?method=getAllStandardVehicleBrandModel",
      success: function (data) {
        multiSelectTwoDialog.init({
          "data": data,
          "selector": "#vehicleBrandModelDiv"
        });

        if (!G.Lang.isEmpty($("#shopVehicleBrandModelDTOListJson").val())) {
          multiSelectTwoDialog.initSelectedData(JSON.parse(decodeURIComponent(G.Lang.normalize($("#shopVehicleBrandModelDTOListJson").val()))));
          $("#partBrandModel").click();
        } else {
          $("#allBrandModel").click();
        }
      }
    });

    $("input[name='selectBrandModel']").bind("click", function () {
      if ($(this).val() == "ALL_MODEL") {
        $("#disableClick").val("true");
        $("#vehicleBrandModelDiv").find(".ensure-dialog").css("display", "none");
        $("#vehicleBrandModelDiv").find(".group-ensure-dialog-title").css("display", "none");
      } else if ($(this).val() == "PART_MODEL") {
        $("#vehicleBrandModelDiv").find(".ensure-dialog").css("display", "block");
        $("#vehicleBrandModelDiv").find(".group-ensure-dialog-title").css("display", "block");
        $("#disableClick").val("false");
      }
    });
  }


  $("#alsoSupplier").click(function () {
    if (!$("#alsoSupplier").attr("checked")) {
      var permanentDualRole = false;
      APP_BCGOGO.Net.syncAjax({
        url: "customer.do?method=getCustomerById",
        dataType: "json",
        data: {customerId: $("#customerId").val()},
        success: function (data) {
          if (data != null && data.permanentDualRole) {
            permanentDualRole = true;
          }
        }
      });
      if (permanentDualRole) {
        nsDialog.jAlert("此客户已经做过对账单，无法解除关系！");
        $("#alsoSupplier").attr("checked", true);
        return;
      }
      nsDialog.jConfirm("是否确认解除绑定关系？", "", function (value) {
        if (!value) {
          $("#alsoSupplier").attr("checked", true);
        } else {
          $("#alsoSupplier").attr("checked", false);
          APP_BCGOGO.Net.syncPost({
            url: "customer.do?method=cancelCustomerBindingSupplier",
            data: {customerId: $("#customerId").val()},
            success: function (result) {
              if (result == 'success') {
                nsDialog.jAlert("解绑成功！");
                if (G.Lang.isEmpty($("#customerId").val())) {
                  window.location.reload();
                } else {
                  window.location.href = "unitlink.do?method=customer&customerId=" + $("#customerId").val();
                }
              } else {
                nsDialog.jAlert("解除绑定失败！")
              }
            },
            error: function () {
              nsDialog.jAlert("解除绑定失败！")
            }
          });
        }
      });
    } else {
      if (nsDialog.jConfirm("是否确认该客户既是客户又是供应商", "", function (value) {
        if (value) {

          $("#modifyClientDiv #newBusinessScopeSpan").text($("#businessScopeSpan").text());
          $("#modifyClientDiv #updateBusinessScopeSpan").text($("#businessScopeSpan").text());

          $("#modifyClientDiv #newThirdCategoryStr").val($("#thirdCategoryIdStr").val());
          $("#modifyClientDiv #updateThirdCategoryStr").val($("#thirdCategoryIdStr").val());

          $("#modifyClientDiv #newVehicleModelContentSpan").text($("#vehicleModelContentSpan").text());
          $("#modifyClientDiv #updateVehicleModelContentSpan").text($("#vehicleModelContentSpan").text());

          $("#modifyClientDiv #newVehicleModelIdStr").val($("#vehicleModelIdStr").val());
          $("#modifyClientDiv #updateVehicleModelIdStr").val($("#vehicleModelIdStr").val());
          var isOnlineShop = $("#isOnlineShop").val() == "true";
          var selectBrandModel = "";
          if (isOnlineShop) {
            selectBrandModel = $("#selectBrandModel").val();
          } else {
            selectBrandModel = $("input:radio[name='selectBrandModel']:checked").val();
          }

          $("#modifyClientDiv #newSelectBrandModel").val(selectBrandModel);
          $("#modifyClientDiv #updateSelectBrandModel").val(selectBrandModel);

          $("#modifyClientDiv").dialog({
            width: 820,
            beforeclose: function () {
              $(".single_contact_gen").remove();
              $(".single_contact input[name^='contacts3']").each(function () {
                $(this).val("");
              });
            }
          })

          $("#radExist").click();
          $(".select_supplier").show();
          $("#modifyClientDiv").dialog("open");

          $("#modifyClientDiv #supplierId").val("");
          var ajaxData = {
            maxRows: $("#pageRows").val(),
            customerOrSupplier: "supplier",
            filterType: "identity"
          };
          APP_BCGOGO.Net.asyncAjax({
            url: "supplier.do?method=searchSupplierDataAction",
            dataType: "json",
            data: ajaxData,
            success: function (data) {
              initTr(data);
              initPages(data, "supplierSuggest", "supplier.do?method=searchSupplierDataAction", '', "initTr", '', '', ajaxData, '');
            }
          });
        } else {
          $("#alsoSupplier").attr("checked", false);
        }

      }));
    }
  });

  $("#modifyClientDiv").dialog({
    autoOpen: false,
    resizable: false,
    title: "修改客户属性",
    height: 500,
    width: 820,
    modal: true,
    closeOnEscape: false,
    close: function () {
      $("#modifyClientDiv").val("");
      $("#alsoSupplier").attr("checked", false);
    },
    showButtonPanel: true
  });
  var isOnlineShop = $("#isOnlineShop").val() == "true";
  if (!isOnlineShop) {
    setCustomerAreaInfo();
  }

  $("#customer_deposit").bind("click", function (event) {
    Mask.Login();
    $("#balance").text($("#hiddenDeposit").val());

    var $deposit = $("#depositDiv"),
        left = $(document).width() / 2 - $deposit.width() / 2; // TODO  找到 mask 的hide 的地方
    //TODO 改用jquery dialog
    $deposit.css("display", "block")
        .css("left", left);
    return false;
  });

  $("#div_close,#cancleBtn").click(function () {
    clearCustomerDepositAddData();
    return false;
  });

  /**
   * 定金弹出框  现金,银行卡，支票keyup事件
   *
   */
  $("#cashDeposit,#bankCardAmountDeposit,#checkAmountDeposit").bind("keyup", function () {
    $(this).css("color", "#000000");
    $(this).val(APP_BCGOGO.StringFilter.inputtingPriceFilter($(this).val(), 2));
    var cash = dataTransition.rounding(parseFloat($("#cashDeposit").val() == "" ? 0 : $("#cashDeposit").val()), 2);
    var bankCardAmount = dataTransition.rounding(parseFloat($("#bankCardAmountDeposit").val() == "" ? 0 : $("#bankCardAmountDeposit").val()), 2);
    var checkAmount = dataTransition.rounding(parseFloat($("#checkAmountDeposit").val() == "" ? 0 : $("#checkAmountDeposit").val()), 2);
    jQuery("#actuallyPaidDeposit").text(dataTransition.rounding(cash + bankCardAmount + checkAmount, 2));
    return false;
  });


  /**
   * “确认”充值
   */
  jQuery("#sureBtn").click(function () {
    $(this).attr("disabled", true);
    var cash = dataTransition.rounding(parseFloat(jQuery("#cashDeposit").val() == "" ? 0 : jQuery("#cashDeposit").val()), 2);
    var bankCardAmount = dataTransition.rounding(parseFloat(jQuery("#bankCardAmountDeposit").val() == "" ? 0 : jQuery("#bankCardAmountDeposit").val()), 2);
    var checkAmount = dataTransition.rounding(parseFloat(jQuery("#checkAmountDeposit").val() == "" ? 0 : jQuery("#checkAmountDeposit").val()), 2);
    var checkNo = (jQuery("#checkNoDeposit").val() == "" || jQuery("#checkNoDeposit").val() == jQuery("#checkNoDeposit").attr("initValue")) ? 0 : jQuery("#checkNoDeposit").val();
    var actuallyPaid = dataTransition.rounding(parseFloat(jQuery("#actuallyPaidDeposit").text() == "" ? 0 : jQuery("#actuallyPaidDeposit").text()), 2);
    var customerId = jQuery("#customerId").val();
    if (actuallyPaid == 0) {
      alert("实付不能为0！");
      return;
    } else if (actuallyPaid != dataTransition.rounding(cash + bankCardAmount + checkAmount, 2)) {
      alert("现金、银行卡、支票之和与实付不符！");
      return;
    }
    var memo = $("#depositMemo").val();
    var depositDTO = {
      "cash": cash,
      "bankCardAmount": bankCardAmount,
      "checkAmount": checkAmount,
      "checkNo": checkNo,
      "actuallyPaid": actuallyPaid,
      "customerId": customerId,
      "memo": memo
    };
    APP_BCGOGO.Net.asyncPost({
      url: "customerDeposit.do?method=addCustomerDeposit",
      data: {
        depositDTO: JSON.stringify(depositDTO),
        print: $("#depositDiv #print").attr("checked")
      },
      cache: false,
      dataType: "json",
      success: function (jsonStr) {
        if (jsonStr.success) {
          $("#totalCustomerDepositSpan").text(jsonStr.data+"元");
          $("#hiddenDeposit").val(jsonStr.data);
          alert("预收金充值成功！");
          $("#depositDiv").css("display", "none");
          $("#mask").css("display", "none");
          $("#iframe_PopupBox_1").css("display", "none");
          if (jsonStr.operation && jsonStr.operation == 'print') {
            window.open("customerDeposit.do?method=printDeposit&customerId=" + customerId + "&cashDeposit=" + cash + "&bankCardAmountDeposit=" + bankCardAmount + "&checkAmountDeposit=" + checkAmount + "&checkNoDeposit=" + checkNo + "&actuallyPaidDeposit=" + actuallyPaid + "&now=" + new Date(), '', "dialogWidth=1024px;dialogHeight=768px");
          }
        } else {
          alert("预收金充值失败！");
        }
      }
    });

    clearCustomerDepositAddData();
    return false;
  });

  $("#queryDepositOrders").bind("click", function (e) {
    var startPageNo = 1;
    var inOutFlag = 0;
    var customerId = $('#customerId').val();
    queryDepositOrders(startPageNo, inOutFlag, customerId);// 进行页面默认查询
    e.stopPropagation();
  });

  // 绑定checkbox的事件 从事件上下文中获取id
  $("#inFlag,#outFlag").bind("click", function (e) {
    if ($(this) && $(this).val()) {
      $(this).val('');
    } else {
      if (e.target.id === 'inFlag') {
        $(this).val('1');
      } else if (e.target.id === 'outFlag') {
        $(this).val('2');
      }
    }
    var startPageNo = 1;
    var inOutFlag = getInOutFlag();
    var customerId = $('#customerId').val();
    queryDepositOrders(startPageNo, inOutFlag, customerId);// 进行页面默认查询
    //阻止事件冒泡
    e.stopPropagation();
  });


  // 绑定表格列标头点击事件 时间 金额
  $("#depositOrdersTime,#depositOrdersMoney").bind("click", function (e) {
    var startPageNo = 1;
    var inOutFlag = getInOutFlag();
    var customerId = $('#customerId').val();
    var sortName;
    var sortFlag;
    var sortObj = {};
    sortFlag = e.target.className === 'descending' ? 'ascending' : 'descending';
    if (e.target.id === 'depositOrdersTime') {
      sortName = 'time';
      $('#depositOrdersTime').attr('class', sortFlag);
    } else if (e.target.id === 'depositOrdersMoney') {
      sortName = 'money';
      $('#depositOrdersMoney').attr('class', sortFlag);
    }
    sortObj[sortName] = sortFlag;
    queryDepositOrders(startPageNo, inOutFlag, customerId, sortObj);// 进行页面默认查询
    e.stopPropagation();
  });

  $("#qqTalk").multiQQInvoker({
    QQ: $.fn.multiQQInvoker.getContactQQ()
  });

  $("#checkNoDeposit").click(function () {
    if ($(this).attr("initValue") == $(this).val()) {
      $(this).val('');
      $(this).css("color", "#000000");
    }
  })
      .blur(function () {
        if (G.isEmpty($(this).val()) || $(this).attr("initValue") == $(this).val()) {
          $(this).val($(this).attr("initValue"));
          $(this).css("color", "#9a9a9a");
        }
      });
});


function clearCustomerDepositAddData() {
  $(".tabTotal :text").val(""); //输入清空
  $(".i_upBody :text").val("");
  jQuery(".productDetails :text").val("");
  jQuery("#checkNoDeposit").val($("#checkNoDeposit").attr("initValue")).css("color", "#9a9a9a");
  jQuery("[name='print']").removeAttr("checked");
  jQuery("#depositMemo").val("");
  jQuery("#actuallyPaidDeposit").text("0");
  $("#depositDiv").css("display", "none");
  $("#mask").css("display", "none");
  try {
    $(window.parent.document.body).find("input[type='button']").eq(0).focus().blur();
  } catch (e) {

  }
}

function getInOutFlag() {
  var inOutFlag;
  var inFlag = $("#inFlag").val(); // 获取到的为字符串
  var outFlag = $("#outFlag").val();
  if (inFlag && inFlag === '1' && outFlag && outFlag === '2') {
    inOutFlag = '0';
  } else {
    if (inFlag && inFlag === '1')inOutFlag = inFlag;
    if (outFlag && outFlag === '2')inOutFlag = outFlag;
  }
  return  inOutFlag;
}

function queryDepositOrders(startPageNo, inOutFlag, customerId, sort) {
  var url = "customerDeposit.do?method=queryDepositOrdersByCustomerIdOrSupplierId";
  var dataContent;
  if (sort && sort['time']) {
    dataContent = {
      startPageNo: startPageNo,
      inOutFlag: inOutFlag,
      customerId: customerId,
      sortName: 'time',
      sortFlag: sort['time']
    };
  } else if (sort && sort['money']) {
    dataContent = {
      startPageNo: startPageNo,
      inOutFlag: inOutFlag,
      customerId: customerId,
      sortName: 'money',
      sortFlag: sort['money']
    };
  } else {
    dataContent = {
      startPageNo: startPageNo,
      inOutFlag: inOutFlag,
      customerId: customerId
    }
  }
  App.Net.syncPost({
    url: url,
    dataType: "json",
    data: dataContent,
    success: function (json) {
      if (json) {
        // 和ajaxPaging 标签配合使用 初始化查询条件
        initPage(json, "dynamical1", url, '', 'initDepositOrdersTable', '', '', {
          startPageNo: startPageNo,
          inOutFlag: inOutFlag,
          customerId: customerId}, '');

        initDepositOrdersTable(json);// initPage里面的回调函数只有在点击分页组件的时候会调用 第一次初始化自己调用一次
      }
      //TODO 这里检测dialog的状态 已经open则关闭 重新弹出?
      var dialogTitle = "<div id=\"\" class=\"\">预收款充值/消费记录</div>";
      $("#depositOrders").dialog({
        resizable: true,
        title: dialogTitle,
        height: 400,
        width: 900,
        modal: true,
        closeOnEscape: false
      });
    }
  });
}

function initDepositOrdersTable(jsonStr) {
  depositOrdersTableContentInit(jsonStr);
  //depositOrdersTableStyleInit();
}

function depositOrdersTableContentInit(json) {
  var data = json.results;
  $("#deposit_orders_table tr:not(:first)").remove(); // remove掉已经存在的表格数据
  if (data && data.length > 0) {
    for (var i = 0; i < data.length; i++) {
      var tr = "<tr class='table-row-original' depositOrderId='" + data[i].id + "'>";
      tr += '<td style="padding-left:10px;">' + data[i].createdTime + '</td>';
      tr += '<td>' + dataTransition.rounding(data[i].actuallyPaid, 2) + '元</td>';
      if (data[i].inOut && data[i].inOut === 1) {
        tr += '<td>' + '收款' + '</td>';
      }
      else {
        tr += '<td>' + '取用' + '</td>';
      }

      var depositTypes = data[i].depositType.split("|");
      tr += '<td>' + depositTypes[1] + '</td>';
      if (data[i].relatedOrderNo) {
        tr += '<td><a class="blue_color" href="' + genUrlByDepositTypeAndId(depositTypes[0], data[i].relatedOrderIdStr) + '" >' + data[i].relatedOrderNo + '</a></td>'; //TODO 这边根据单据的类型生成URL
      } else {
        tr += '<td>' + '-' + '</td>';
      }
      tr += '<td title="' + (data[i].operator == null ? '' : data[i].operator) + '">' + (data[i].operator == null ? '' : data[i].operator) + '</td>';
      tr += '<td title="' + (data[i].memo == null ? "无" : data[i].memo) + '">' + (data[i].memo == null ? "无" : data[i].memo) + '</td>';
      tr += '</tr>';
      var $tr = $(tr);
      $("#deposit_orders_table").append($tr);
    }
  }
}

function genUrlByDepositTypeAndId(type, id) {
  var urlPrefix;
  if (type) {
    if (type == "SALES" || type == "SALES_REPEAL") {
      urlPrefix = 'sale.do?method=getSalesOrder&menu-uid=WEB.TXN.SALE_MANAGE.SALE&salesOrderId=';
    }
    if (type == "SALES_BACK" || type == "SALES_BACK_REPEAL") {
      urlPrefix = " salesReturn.do?method=showSalesReturnOrderBySalesReturnOrderId&salesReturnOrderId=";
    }
    if (type == "INVENTORY" || type == "INVENTORY_REPEAL") {
      urlPrefix = "storage.do?method=getPurchaseInventory&menu-uid=WEB.TXN.PURCHASE_MANAGE.STORAGE&purchaseInventoryId=";
    }
    if (type == "INVENTORY_BACK" || type == "INVENTORY_BACK_REPEAL") {
      urlPrefix = "goodsReturn.do?method=showReturnStorageByPurchaseReturnId&purchaseReturnId=";
    }
    if (type == "COMPARE") {
      urlPrefix = "statementAccount.do?method=showStatementAccountOrderById&statementOrderId=";
    }
  }
  return urlPrefix + id;
}

function depositOrdersTableStyleInit() {
  tableUtil.tableStyle('#deposit_orders_table', '.tab_title,.title'); //TODO 表格样式
}

$(function () {
  $(".tabRecord tr").not(".tabTitle").css({"border": "1px solid #bbbbbb", "border-width": "1px 0px"});
  $(".tabRecord tr:nth-child(odd)").not(".tabTitle").css("background", "#eaeaea");

  $(".tabRecord tr").not(".tabTitle").hover(
      function () {
        $(this).find("td").css({"background": "#fceba9", "border": "1px solid #ff4800", "border-width": "1px 0px"});

        $(this).css("cursor", "pointer");
      },
      function () {
        $(this).find("td").css({"background-Color": "#FFFFFF", "border": "1px solid #bbbbbb", "border-width": "1px 0px 0px 0px"});
        $(".tabRecord tr:nth-child(odd)").not(".tabTitle").find("td").css("background", "#eaeaea");
      }
  );

  $(".divContent").find("input.txt").hide();
  $(".divContent").find(".rad").hide();
  $(".divContent").find("select").hide();
  $("#button").hide();
  $(".table_inputContact").hide();

  $(".close").hide();
  $(".table_inputContact").not("tr:first").hover(
      function () {
        $(".close").show();
      },
      function () {
        $(".close").hide();
      }
  )

  $(".alert").hide();

  $(".hover").live("hover", function (event) {
    var _currentTarget = $(event.target).parent().find(".alert");
    _currentTarget.show();
    //因为有2px的空隙,所以绑定在parent上.
    _currentTarget.parent().mouseleave(function (event) {
      event.stopImmediatePropagation();

      if ($(event.relatedTarget).find(".alert")[0] != _currentTarget[0]) {
        _currentTarget.hide();
      }
    });
  }, function (event) {
    var _currentTarget = $(event.target).parent().find(".alert");

    if ($(event.relatedTarget).find(".alert")[0] != _currentTarget[0]) {
      $(event.target).parent().find(".alert").hide();
    }
  });

});


function switchContact() {
  var $currentContactBlock = $(this).parents(".J_editCustomerContact");
  var currentLevel = $currentContactBlock.find("input[id$='level']").val();
  var $mainContactBlock = $currentContactBlock.siblings().find('.icon_connacter').parents(".J_editCustomerContact");
  var $alert = $(this).siblings(".alert"); // 保存alert div

  $mainContactBlock.find('.icon_connacter').removeClass('icon_connacter').addClass('icon_grayconnacter').addClass("hover"); // 主联系人灰化
  $mainContactBlock.find("input[id$='mainContact']").val("0"); // 修改非主联系人
  $mainContactBlock.find("input[id$='level']").val(currentLevel);
  $alert.insertAfter($mainContactBlock.find('.icon_grayconnacter')).hide(); // 添加alert
  $mainContactBlock.find('.icon_grayconnacter').live("click", switchContact);

  $(this).removeClass('icon_grayconnacter').addClass('icon_connacter'); // 当前联系人转变为主联系人
  $currentContactBlock.find("input[id$='mainContact']").val("1"); // 设置为主联系人1
  $currentContactBlock.find("input[id$='level']").val("0");
  $(this).siblings(".alert").remove();
  $(this).unbind("click");
}


defaultStorage.setItem(storageKey.MenuUid, "WEB.CUSTOMER_MANAGER.CUSTOMER_DATA");

function clearDefaultAddress() {
  if ($("#input_address").val() == $("#input_address").attr("initValue") && $("#input_address").val() == "详细地址") {
    $("#input_address").val('');
  }
  if ($("#address").val() == $("#address").attr("initValue") && $("#address").val() == "详细地址") {
    $("#address").val('');
  }
  if ($("#input_address1").val() == $("#input_address1").attr("initValue") && $("#input_address1").val() == "详细地址") {
    $("#input_address1").val('');
  }
  if ($("#input_address2").val() == $("#input_address2").attr("initValue") && $("#input_address2").val() == "详细地址") {
    $("#input_address2").val('');
  }
}

</script>
</head>
<body class="bodyMain">
<input type="hidden" value="uncleUser" id="pageName">
<input type="hidden" value="clientInfo" id="orderType">
<input id="wholesalerVersion" type="hidden" value="${wholesalerVersion}"/>
<input type="hidden" id="isOnlineShop" value="${customerDTO.isOnlineShop}"/>
<input id="customerShopId" type="hidden" value="${customerDTO.customerShopId}">
<input id="supplierId" type="hidden" value="${customerDTO.supplierId}"/>

<%@ include file="/WEB-INF/views/header_html.jsp" %>
<div class="i_main clear">

<div class="mainTitles">
  <div class="titleWords">客户资料</div>
  <c:if test="${fromPage =='customerData'}">
    <div class="title-r" style="padding-top:48px;"><a href="customer.do?method=customerdata">返回列表></a></div>
  </c:if>
</div>

<div class="customer_nav">
  <ul>
    <li><a id="customerInfoTitle" href="#" class="arrer">详细信息</a></li>
    <bcgogo:hasPermission permissions="web.statement.order.redirectSearchCustomerBill" resourceType="menu">
      <li><a id="customerStatementTitle" href="#">客户对账单</a></li>
    </bcgogo:hasPermission>
  </ul>

  <div class="setting-relative">
    <div class="setting-absolute J_customerOptDetail" style="display: none">
      <ul>
        <bcgogo:hasPermission permissions="WEB.TXN.SALE_MANAGE.SALE" resourceType="menu">
          <li><a class="blue_color" style="cursor: pointer" onclick="redirectSalesOrder()">购买商品</a></li>
        </bcgogo:hasPermission>
        <bcgogo:hasPermission permissions="WEB.TXN.SALE_MANAGE.RETURN" resourceType="menu">
          <li><a class="blue_color" style="cursor: pointer" onclick="redirectSalesReturn()">销售退货</a></li>
        </bcgogo:hasPermission>
        <bcgogo:hasPermission permissions="web.statement.order.redirectSearchCustomerBill" resourceType="menu">
          <li><a class="blue_color" id="duizhan" style="cursor: pointer">财务对账</a></li>
        </bcgogo:hasPermission>

        <bcgogo:hasPermission permissions="WEB.CUSTOMER_MANAGER.CUSTOMER_DELETE">
          <li><a class="blue_color" id="deleteCustomerButton" onclick="deleteCustomer()"
                 style="cursor: pointer">删除客户</a></li>
        </bcgogo:hasPermission>
        <bcgogo:hasPermission permissions="WEB.CUSTOMER_MANAGER.CUSTOMER_UPDATE">
          <c:if test="${empty customerDTO.customerShopId && 'NONE_REGISTERED' eq shopStatus}">
            <li><a class="blue_color" id="updateToShopBtn" onclick="updateToShop()" style="cursor: pointer">升级客户</a>
            </li>
          </c:if>
        </bcgogo:hasPermission>
      </ul>
    </div>
  </div>

  <div class="setting J_customerOpt">操 作</div>
</div>


<input type="hidden" id="isUncleUser"/>
<input id="customerId" type="hidden" value="${customerId}"/>
<input id="customerName" type="hidden" value="${customerDTO.name}"/>
<input id="id" name="id" type="hidden" value="${customerDTO.id}"/>

<input type="hidden" value="" id="modifyAll">

<div class="booking-management">


<div class="titBody" id="customerDetailInfo">

<div class="lineTitle"><span>基本信息</span>

  <c:if test="${not empty customerDTO.customerShopId}">
    <a class="customer-tip"
       href="shopMsgDetail.do?method=renderShopMsgDetail&paramShopId=${customerDTO.customerShopId}">商</a>
  </c:if>
  <bcgogo:hasPermission permissions="WEB.CUSTOMER_MANAGER.CUSTOMER_MODIFY">
    <div class="editButton" id="editCustomerInfo">编 辑</div>
  </bcgogo:hasPermission>
        <span
            class="font12-normal"><a
            style="cursor: pointer"
            onclick="customerConsume('totalConsume');">累计消费: ${customerDTO.consumeTimes}次&nbsp;${customerDTO.totalAmount}元</a>
          <bcgogo:hasPermission permissions="WEB.TXN.SALE_MANAGE.RETURN">
            | <a style="cursor: pointer"
                 onclick="customerConsume('salesReturn');">累计销售退货: ${customerDTO.totalReturnAmount}元</a>
          </bcgogo:hasPermission>
          <bcgogo:permission>
            <bcgogo:if permissions="WEB.CUSTOMER_MANAGER.CUSTOMER_ARREARS">
              | <a style="cursor: pointer"
                   onclick="customerConsume('totalReceivable');">应收: ${customerDTO.totalReceivable}元</a>
              <a style="cursor: pointer"
                 onclick="customerConsume('totalReturn');">应付: ${customerDTO.totalReturnDebt}元</a>
            </bcgogo:if>
            <bcgogo:else>
              | <a style="cursor: pointer"
                   onclick="customerConsume('totalReturn');">应付: ${customerDTO.totalReturnDebt}元</a>
              <th>&nbsp;</th>
              <td>&nbsp;</td>
            </bcgogo:else>
          </bcgogo:permission></span>
  <bcgogo:hasPermission permissions="web.statement.order.redirectSearchCustomerBill" resourceType="menu">
    <div class="editButton" id="duizhang">对 账</div>
  </bcgogo:hasPermission>
</div>
<div class="clear"></div>
<div class="customer" id="customerBasicInfoShow">

  <table width="100%" border="0" class="order-table"  id="customerDetailTable">
    <colgroup>
      <col width="150"/>
      <col width="150"/>
      <col width="150"/>
      <col width="250"/>
    </colgroup>
    <tr class="J_showCustomerOtherInfo">
      <td colspan="2">客户名称：

        <c:choose>
          <c:when test="${customerDTO.isOnlineShop}">
            <span>${customerDTO.name}</span>
          </c:when>
          <c:otherwise>
            <span class="J_customerBasicSpan" data-key="name">${(customerDTO.name ==null || customerDTO.name =='')?'--':customerDTO.name}</span>
          </c:otherwise>
        </c:choose>

        <c:if test="${customerDTO.identity=='isSupplier'}">
          <a class="blue_color">【客户&amp;供应商】</a>
        </c:if>
      </td>

      <td colspan="2">座机：<span class="J_customerBasicSpan" data-key="landLineForAll">${(customerDTO.landLineForAll ==null || customerDTO.landLineForAll =='')?'--':customerDTO.landLineForAll}</span></td>

    </tr>

    <tr class="J_showCustomerOtherInfo">
      <td colspan="2">
        <input type="hidden" value="${totalCustomerDeposit}" id="hiddenDeposit">
        <span style="float:left">预收款余额：<span id="totalCustomerDepositSpan">${totalCustomerDeposit}元</span></span>&nbsp;&nbsp;<a
          class="blue_color reconciliation recharge"
          id="customer_deposit" style="cursor: pointer;font-size:12px">充值</a>&nbsp;&nbsp;
        <a id="queryDepositOrders" class="blue_color" style="cursor: pointer;font-size:12px">历史记录</a></td>
      <td colspan="2">地址：<c:choose>
        <c:when test="${customerDTO.isOnlineShop}">
          <span>${(customerDTO.areaInfo ==null || customerDTO.areaInfo =='')?'--':customerDTO.areaInfo}</span>
          <span>${(customerDTO.address ==null || customerDTO.address =='')?'--':customerDTO.address}</span>
        </c:when>
        <c:otherwise>
          <span class="J_customerBasicSpan" data-key="areaInfo">${(customerDTO.areaInfo ==null || customerDTO.areaInfo =='')?'--':customerDTO.areaInfo}</span>
          <span class="J_customerBasicSpan" data-key="address">${(customerDTO.address ==null || customerDTO.address =='')?'--':customerDTO.address}</span>
        </c:otherwise>
      </c:choose>
      </td>
    </tr>
    <tr class="J_showCustomerOtherInfo">
      <td colspan="4" class="border_dashed"></td>
    </tr>

    <tr id="customerContactContainer" class="J_showCustomerOtherInfo">

      <c:if test="${!customerDTO.hasMainContact}">

        <td>主联系人：--</td>
        <td>手机号：--</td>
        <td>QQ：--</td>
        <td>Email:--</td>
      </c:if>

      <c:if test="${customerDTO.hasMainContact}">

        <c:forEach items="${customerDTO.contacts}" var="contact" varStatus="status">
          <c:choose>
            <c:when test="${contact.mainContact == 1}">
              <td>主联系人：${(contact.name ==null || contact.name =='')?'--':contact.name}</td>
              <td>手机号：${(contact.mobile ==null || contact.mobile =='')?'--':contact.mobile}</td>
              <td>QQ：${(contact.qq ==null || contact.qq =='')?'--':contact.qq}</td>
              <td>Email:${(contact.email ==null || contact.email =='')?'--':contact.email} </td>
            </c:when>
          </c:choose>
        </c:forEach>
      </c:if>


    </tr>




    <tr class="titBottom_Bg">
      <td colspan="4">
        <div class="div_Btn"><a id="showDetailInfo" onclick="showDetailInfo();"
                                class="btnDown"></a></div>
      </td>
    </tr>

  </table>
  <div class="clear"></div>
</div>
<div class="customer" id="customerBasicInfoEdit" style="display: none;">
<form id="customerBasicForm" method="post">

<input type="hidden" id="serviceCategoryRelationIdStr" name="serviceCategoryRelationIdStr"
       value="${customerDTO.serviceCategoryRelationIdStr}">
<input type="hidden" id="vehicleModelIdStr" name="vehicleModelIdStr" value="${customerDTO.vehicleModelIdStr}">
<input type="hidden" id="thirdCategoryIdStr" name="thirdCategoryIdStr" value="${customerDTO.thirdCategoryIdStr}">
<textarea style="display:none" id="thirdCategoryNodeListJson">${customerDTO.thirdCategoryNodeListJson}</textarea>
<textarea style="display:none"
          id="shopVehicleBrandModelDTOListJson">${customerDTO.shopVehicleBrandModelDTOListJson}</textarea>

<table width="100%" border="0" class="order-table">
<colgroup>
  <col width="79"/>
  <col width="190"/>
  <col width="65"/>
  <col width="190"/>
  <col width="60"/>
  <col width="190"/>
  <col width="65"/>
  <col width="190"/>
</colgroup>

<tr>
  <td class="test1"><span class="red_color">*</span>客户名称 </td>
  <td colspan="3">：
    <c:choose>
      <c:when test="${customerDTO.isOnlineShop}">
        <span>${customerDTO.name}</span>
        <input type="hidden" id="name" name="name" value="${customerDTO.name}">
      </c:when>
      <c:otherwise>
        <input type="text" style="width:89.5%" maxlength="50" id="name" name="name"
               reset-value="${customerDTO.name}" value="${customerDTO.name}"
               class="txt J_formreset">
      </c:otherwise>
    </c:choose>
  </td>
  <td class="test1">座 机</td>
  <td colspan="3">：

    <input maxlength="14" id="landLine" name="landLine" value="${customerDTO.landLine}" type="text" class="txt" style="width:28.5%; margin-right:5px;"/><input
              maxlength="14" id="landLineSecond" name="landLineSecond" value="${customerDTO.landLineSecond}" type="text" class="txt" style="width:28.5%; margin-right:5px;"/><input
              maxlength="14" id="landLineThird" name="landLineThird" value="${customerDTO.landLineThird}" type="text" class="txt" style="width:28.5%; margin-right:5px;"/></td>

</tr>

<tr>
  <td class="test1">简 称</td>
  <td>：
    <c:choose>
      <c:when test="${customerDTO.isOnlineShop}">
        <span>${customerDTO.shortName}</span>
        <input type="hidden" id="shortName" name="shortName" value="${customerDTO.shortName}">
      </c:when>
      <c:otherwise>
        <input type="text" maxlength="50" id="shortName" name="shortName"
               reset-value="${customerDTO.shortName}" value="${customerDTO.shortName}"
               class="txt J_formreset"/>
      </c:otherwise>
    </c:choose>
  </td>
  <td class="test1">身 份</td>
  <td>：
    <label class="rad" style="font-size: 12px"><input id="alsoSupplier" name="alsoSupplier" type="checkbox"
                                                       <c:if test="${customerDTO.identity=='isSupplier'}">checked='checked'</c:if>
        <c:if test="${customerDTO.permanentDualRole}"> disabled='disabled' </c:if>/>
      也是供应商
    </label>
  </td>
  <td class="test1"><span class="red_color">* </span>地 址</td>
  <td colspan="3">：
    <c:choose>
      <c:when test="${customerDTO.isOnlineShop}">
        <span id="areaInfo">${customerDTO.areaInfo}</span>
        <input type="hidden" id="province" name="province" value="${customerDTO.province}">
        <input type="hidden" id="city" name="city" value="${customerDTO.city}">
        <input type="hidden" id="region" name="region" value="${customerDTO.region}">
      </c:when>
      <c:otherwise>
        <input type="hidden" class="J_formreset" reset-value="${customerDTO.province}"
               id="select_provinceInput" value="${customerDTO.province}"/>
        <input type="hidden" class="J_formreset" reset-value="${customerDTO.city}"
               id="select_cityInput" value="${customerDTO.city}"/>
        <input type="hidden" class="J_formreset" reset-value="${customerDTO.region}"
               id="select_regionInput" value="${customerDTO.region}"/>

        <select id="province" name="province" style="width:94px;">
          <option value="">所有省</option>
        </select>
        <select id="city" name="city" style="width:90px;">
          <option value="">所有市</option>
        </select>
        <select id="region" name="region" style="width:80px;">
          <option value="">所有区</option>
        </select>
      </c:otherwise>
    </c:choose>
    <c:choose>
      <c:when test="${customerDTO.isOnlineShop}">
        <span>${customerDTO.address}</span>
        <input type="hidden" id="address" name="address" value="${customerDTO.address}">
      </c:when>
      <c:otherwise>
        <input type="text" style="width:22%; margin-right:5px;" maxlength="50" id="address" name="address"
               reset-value="${customerDTO.address}" value="${customerDTO.address}" class="txt J_formreset"/>
      </c:otherwise>
    </c:choose>
  </td>
</tr>



<c:if test="${customerDTO.hasMainContact}">

<c:forEach items="${customerDTO.contacts}" var="contact" varStatus="status">
    <c:choose>
      <c:when test="${contact.mainContact == 1}">
        <tr>
          <input type="hidden" name="contacts[${status.index}].id" id="contacts[${status.index}].id"
                 data-key="idStr" value="${contact.idStr}"/>
          <input type="hidden" name="contacts[${status.index}].level" id="contacts[${status.index}].level"
                 value="${status.index}"/>

        <td class="test1">主联系人</td>
        <input type="hidden" name="contacts[${status.index}].mainContact" class="J_formreset"
               id="contacts[${status.index}].mainContact" data-key="mainContact" reset-value="1"
               value="1"/>
          <td>：
                <input type="text" maxlength="11"
                       name="contacts[${status.index}].name"
                       id="contacts[${status.index}].name" data-key="name"
                       value="${contact.name}" reset-value="${contact.name}"
                       class="txt J_formreset"/>
              </td>

              <td class="test1">手机号</td>
              <td>：
                <input type="text" maxlength="11"
                       name="contacts[${status.index}].mobile" id="contacts[${status.index}].mobile"
                       data-key="mobile" value="${contact.mobile}" reset-value="${contact.mobile}"
                       class="txt J_formreset"/>
              </td>
              <td align="right">QQ</td>
              <td>：
                <input type="text" maxlength="15" name="contacts[${status.index}].qq"
                       id="contacts[${status.index}].qq" data-key="qq" value="${contact.qq}"
                       reset-value="${contact.qq}" class="txt J_formreset"/>
              </td>
              <td align="right">Email</td>
              <td>：
                <input type="text" maxlength="20" name="contacts[${status.index}].email"
                       id="contacts[${status.index}].email" data-key="email" value="${contact.email}"
                       reset-value="${contact.email}" class="txt J_formreset"/>
              </td>
            </tr>

      </c:when>


    </c:choose>

</c:forEach>

<c:forEach items="${customerDTO.contacts}" var="contact" varStatus="status">
    <c:choose>
      <c:when test="${contact.mainContact != 1}">
        <tr>
          <input type="hidden" name="contacts[${status.index}].id" id="contacts[${status.index}].id"
                 data-key="idStr" value="${contact.idStr}"/>
          <input type="hidden" name="contacts[${status.index}].level" id="contacts[${status.index}].level"
                 value="${status.index}"/>

          <td class="test1">联系人</td>
          <input type="hidden" name="contacts[${status.index}].mainContact" class="J_formreset"
                 id="contacts[${status.index}].mainContact" data-key="mainContact" reset-value="0"
                 value="0"/>


          <td>：
                <input type="text" maxlength="11"
                       name="contacts[${status.index}].name"
                       id="contacts[${status.index}].name" data-key="name"
                       value="${contact.name}" reset-value="${contact.name}"
                       class="txt J_formreset"/>
              </td>


              <td class="test1">手机号</td>
              <td>：
                <input type="text" maxlength="11"
                       name="contacts[${status.index}].mobile" id="contacts[${status.index}].mobile"
                       data-key="mobile" value="${contact.mobile}" reset-value="${contact.mobile}"
                       class="txt J_formreset"/>
              </td>
              <td align="right">QQ</td>
              <td>：
                <input type="text" maxlength="15" name="contacts[${status.index}].qq"
                       id="contacts[${status.index}].qq" data-key="qq" value="${contact.qq}"
                       reset-value="${contact.qq}" class="txt J_formreset"/>
              </td>
              <td align="right">Email</td>
              <td>：
                <input type="text" maxlength="20" name="contacts[${status.index}].email"
                       id="contacts[${status.index}].email" data-key="email" value="${contact.email}"
                       reset-value="${contact.email}" class="txt J_formreset"/>
              </td>
            </tr>

      </c:when>


    </c:choose>

</c:forEach>
  </c:if>

<c:if test="${!customerDTO.hasMainContact}">
        <c:set var="contactIndex" value="0"/>
        <c:forEach begin="${contactIndex}" end="2" step="1" varStatus="status">

          <tr>
            <input type="hidden" name="contacts[${status.index}].id" id="contacts[${status.index}].id"
                   data-key="idStr" value="${contact.idStr}"/>
            <input type="hidden" name="contacts[${status.index}].level" id="contacts[${status.index}].level"
                   value="${status.index}"/>
            <td class="test1">
              <c:if test="${status.index == 0}">
                主联系人
              </c:if>
              <c:if test="${status.index != 0}">
                联系人
              </c:if>
            </td>

            <c:if test="${status.index == 0}">
              <input type="hidden" name="contacts[${status.index}].mainContact" class="J_formreset"
                     id="contacts[${status.index}].mainContact" data-key="mainContact" reset-value="1"
                     value="1"/>
            </c:if>
            <c:if test="${status.index != 0}">
              <input type="hidden" name="contacts[${status.index}].mainContact" class="J_formreset"
                     id="contacts[${status.index}].mainContact" data-key="mainContact" reset-value="0"
                     value="0"/>
            </c:if>

            <td>：
              <input type="text" maxlength="11"
                     name="contacts[${status.index}].name"
                     id="contacts[${status.index}].name" data-key="name"
                     value="${contact.name}" reset-value="${contact.name}"
                     class="txt J_formreset"/>
            </td>


            <td class="test1">手机号</td>
            <td>：
              <input type="text" maxlength="11"
                     name="contacts[${status.index}].mobile" id="contacts[${status.index}].mobile"
                     data-key="mobile" value="${contact.mobile}" reset-value="${contact.mobile}"
                     class="txt J_formreset"/>
            </td>
            <td align="right">QQ</td>
            <td>：
              <input type="text" maxlength="15" name="contacts[${status.index}].qq"
                     id="contacts[${status.index}].qq" data-key="qq" value="${contact.qq}"
                     reset-value="${contact.qq}" class="txt J_formreset"/>
            </td>
            <td align="right">Email</td>
            <td>：
              <input type="text" maxlength="20" name="contacts[${status.index}].email"
                     id="contacts[${status.index}].email" data-key="email" value="${contact.email}"
                     reset-value="${contact.email}" class="txt J_formreset"/>
            </td>
          </tr>
        </c:forEach>
      </c:if>

<tr>
  <td class="test1">传 真</td>
  <td>：
    <input type="text" maxlength="20" id="fax" name="fax" reset-value="${customerDTO.fax}"
           value="${customerDTO.fax}" class="txt J_formreset"/>
  </td>
  <td class="test1">客户类别</td>

  <td>：
    <select style="width:78%" class="txt J_formreset" name="customerKind" id="customerKind"
            reset-value="${customerDTO.customerKind}">
      <option value="">--请选择--</option>
      <c:forEach items="${customerTypeMap}" var="customerType" varStatus="status">
        <option
            value="${customerType.key}" ${customerType.key eq customerDTO.customerKind?'selected':''}>${customerType.value}</option>
      </c:forEach>
    </select>
  </td>

  <td class="test1">&nbsp;</td>
  <td>&nbsp;</td>
  <td class="test1">&nbsp;</td>
  <td>&nbsp;</td>
</tr>


<tr>
  <td class="test1">开户行</td>
  <td>：
    <input type="text" maxlength="20" id="bank" name="bank" reset-value="${customerDTO.bank}"
           value="${customerDTO.bank}" class="txt J_formreset"/></td>
  <td class="test1">开户名</td>
  <td>：
    <input type="text" maxlength="20" id="bankAccountName" name="bankAccountName"
           reset-value="${customerDTO.bankAccountName}" value="${customerDTO.bankAccountName}"
           class="txt J_formreset"/></td>
  <td class="test1">账 号</td>
  <td>：
    <input type="text" maxlength="20" id="account" name="account"
           reset-value="${customerDTO.account}" value="${customerDTO.account}"
           class="txt J_formreset"/></td>
  <td class="test1">结算类型</td>
  <td>：
    <select style="width:144px;" name="settlementType" id="settlementType"
            reset-value="${customerDTO.settlementType}" class="txt J_formreset">
      <option value="">--请选择--</option>
      <c:forEach items="${settlementTypeMap}" var="settlementType" varStatus="status">
        <option
            value="${settlementType.key}" ${settlementType.key eq customerDTO.settlementType?'selected':''}>${settlementType.value}</option>
      </c:forEach>
    </select>
  </td>
</tr>


<tr>
  <td valign="top" class="test1">发票类型</td>
  <td valign="top">：
    <select style="width:78%" name="invoiceCategory" id="invoiceCategory" reset-value="${customerDTO.invoiceCategory}"
            class="txt J_formreset">
      <option value="">--请选择--</option>
      <c:forEach items="${invoiceCatagoryMap}" var="invoiceCategory" varStatus="status">
        <option
            value="${invoiceCategory.key}" ${invoiceCategory.key eq customerDTO.invoiceCategory?'selected':''}>${invoiceCategory.value}</option>
      </c:forEach>
    </select>
  </td>
  <td valign="top" class="test1">备 注</td>
  <td colspan="5" valign="top"><span class="fl">：&nbsp;</span>
    <input type="text" maxlength="400" style="width:94%;" id="memo" name="memo" reset-value="${customerDTO.memo}" value="${customerDTO.memo}" class="txt J_formreset"/>
  </td>
</tr>
</table>
<div class="clear height"></div>
<c:choose>
  <c:when test="${customerDTO.isOnlineShop}">
    <table width="100%" border="0" class="order-table">
      <colgroup>
        <col width="100"/>
        <col>
      </colgroup>
      <tr>
        <th valign="top">服务范围：</th>
        <td><span>${customerDTO.serviceCategoryRelationContent}</span></td>
      </tr>
      <tr>
        <th>经营产品：</th>
        <td><span>${customerDTO.businessScopeStr}</span></td>
      </tr>
      <tr>
        <th>主营车型：</th>
        <td>
          <input type="hidden" id="selectBrandModel" name="selectBrandModel" value="${customerDTO.selectBrandModel}">
          <span>${customerDTO.vehicleModelContent}</span>
        </td>
      </tr>
    </table>
    <div class="clear"></div>

  </c:when>
  <c:otherwise>
    <div class="scopeBusiness">
      <div class="left select-t"  style="margin-left:-6px;" align="right"><span class="red_color">* </span>服务范围：</div>
      <div class="right">
        <div class="select-t">
          <c:if test="${not empty serviceCategoryDTOList}">
            <c:forEach items="${serviceCategoryDTOList}" var="serviceCategoryDTO">
              <label class="lbl">
                <input class="J_serviceCategoryCheckBox"
                       type="checkbox" ${fn:contains(customerDTO.serviceCategoryRelationIdStr, serviceCategoryDTO.id)?'checked':''}
                       value="${serviceCategoryDTO.id}">
                  ${serviceCategoryDTO.name}</label>
            </c:forEach>
          </c:if>
        </div>
      </div>
      <div class="clear i_height"></div>
    </div>
    <div class="clear"></div>

    <div class="scope-content">
      <div class="left select-t" align="right"  style="text-align: left;padding-left: 6px;">经营产品&nbsp;</div>
      <div class="right">
        <div class="select-t" id="" style="display: none"><a href="#" class="wrong"></a> <span class="red_color font12">选择经营产品</span>
        </div>
      </div>
      <div class="clear"></div>

      <div id="businessScopeTreeDiv"></div>
      <div class="clear height"></div>
    </div>

    <div class="scope-content">
      <div class="left select-t" align="right" style="margin-left:-20px;">主营车型 &nbsp;</div>
      <div class="right" style="line-height:25px">
        <label><input type="radio" id="allBrandModel" value="ALL_MODEL" name="selectBrandModel"/>全部车型</label>&nbsp;
        <label><input type="radio" id="partBrandModel" value="PART_MODEL" name="selectBrandModel"/>部分车型</label>&nbsp;
      </div>
      <div class="clear"></div>
      <div id="vehicleBrandModelDiv"></div>
      <div class="clear i_height"></div>
    </div>
  </c:otherwise>
</c:choose>

</form>
<div class="clear"></div>
<bcgogo:hasPermission permissions="WEB.CUSTOMER_MANAGER.CUSTOMER_MODIFY">
  <div class="padding10">
    <input type="button" class="query-btn" id="saveCustomerBasicBtn" value="确认"/>
    <input type="button" class="query-btn" id="cancelCustomerBasicBtn" value="取消"/>
  </div>
</bcgogo:hasPermission>
</div>
</div>
<div class="clear height"></div>

<%--<bcgogo:hasPermission permissions="web.statement.order.redirectSearchCustomerBill" resourceType="menu">--%>
  <div id="customerDetailStatement" style="display: none;">
    <%@include file="/customerDetail/customerDetailBill.jsp" %>
  </div>
<%--</bcgogo:hasPermission>--%>

<div class="titBody" id="customerDetailConsume">

<div class="lineTitle"><span>消费信息</span>

  <div class="title-r" style="float:left; margin-left:10px;">
    <a style="cursor: pointer;" id="moreConsumeInfo">查询条件<img src="images/rightArrow.png"
                                                              style="float:right; margin:12px 0 0 5px;"/></a>
  </div>
</div>


<div class="lineBody" style="border:#ccc 1px solid; background:none; width:968px; border-top:0;">
<div class="cuSearch">
<div id="queryCondition" style="display: none;">

<div class="gray-radius" style="margin:0; width:950px;">

<form id="inquiryCenterSearchForm" commandName="inquiryCenterInitialDTO" name="inquiryCenterSearchForm"
      action="inquiryCenter.do?method=inquiryCenterSearchOrderAction" method="post">
  <input type="hidden" name="maxRows" id="pageRows" value="5">
  <input type="hidden" name="totalRows" id="totalRows" value="0">
  <input type="hidden" id="sortStatus" name="sort" value="created_time desc"/>
  <input type="hidden" name="customerOrSupplierId" id="customerOrSupplierId" value="${customerId}">
  <input type="hidden" name="debtType" id="debtType" value="">
  <input type="hidden" name="accountMemberNo" id="accountMemberNo" value="">

    <div class="divTit" style="float:none"><span class="spanName">日期：</span>&nbsp;
      <a class="btnList" id="my_date_yesterday" name="my_date_select">昨天</a>&nbsp;
      <a class="btnList" id="my_date_today" name="my_date_select">今天</a>&nbsp;
      <a class="btnList" id="my_date_lastmonth" name="my_date_select">上月</a>&nbsp;
      <a class="btnList" id="my_date_thismonth" name="my_date_select">本月</a>&nbsp;
      <a class="btnList" id="my_date_thisyear" name="my_date_select">今年</a>&nbsp;
      <input id="startDate" type="text" value="${startDateStr}" readonly="readonly" name="startTimeStr"
             class="my_startdate txt"/>&nbsp;至&nbsp;
      <input id="endDate" type="text" value="${endDateStr}" readonly="readonly" name="endTimeStr"
             class='my_enddate txt'/>&nbsp;&nbsp;
    </div>

    <div class="clear"></div>

      <bcgogo:orderPageConfigurationParam orderGroupName="order_type_condition" orderNameAndResource="[purchase_order,WEB.TXN.PURCHASE_MANAGE.PURCHASE];
            [storage_order,WEB.TXN.PURCHASE_MANAGE.STORAGE];[sale_order,WEB.TXN.SALE_MANAGE.SALE];[vehicle_construction_order,WEB.VEHICLE_CONSTRUCTION.CONSTRUCT.BASE];
            [wash_beauty_order,WEB.VEHICLE_CONSTRUCTION.WASH_BEAUTY.BASE];[purchase_return_order,WEB.TXN.PURCHASE_MANAGE.RETURN];
            [sale_return_order,WEB.TXN.SALE_MANAGE.RETURN];[buy_card_order,WEB.CUSTOMER_MANAGER.MEMBERSHIP_PACKAGE_MANAGER];
            [return_card,WEB.CUSTOMER_MANAGER.MEMBERSHIP_PACKAGE_MANAGER]">
        <c:if test="${!order_type_condition_has_none_of_the_order_group}">
          <div style="padding: 0 0 5px;" class="divTit divWarehouse member">
            <span class="spanName">单据类型：</span>

            <div class="warehouseList" id="orderTypes" style="width: auto;">
              <label class="rad" id="orderTypeAllLabel"><input type="checkbox" id="orderTypeAll"
                                                               data-name="all"/>所有</label>&nbsp;
              <c:choose>
                <c:when test="${order_type_condition_sale_order}">
                  <label class="rad" id="saleLabel"><input type="checkbox" name="orderType" value="SALE"
                                                           data-name="sale"/>销售单</label>&nbsp;
                </c:when>
                <c:otherwise>
                  <bcgogo:hasPermission permissions="WEB.TXN.SALE_MANAGE.SALE">
                    <input type="hidden" name="orderType" origValue="SALE"/>
                  </bcgogo:hasPermission>
                </c:otherwise>
              </c:choose>
              <c:choose>
                <c:when test="${order_type_condition_sale_return_order}">
                  <label class="rad" id="saleReturnLabel"><input type="checkbox" name="orderType" value="SALE_RETURN"
                                                                 data-name="saleReturn"/>销售退货单</label>&nbsp;
                </c:when>
                <c:otherwise>
                  <bcgogo:hasPermission permissions="WEB.TXN.SALE_MANAGE.RETURN">
                    <input type="hidden" name="orderType" origValue="SALE_RETURN"/>
                  </bcgogo:hasPermission>
                </c:otherwise>
              </c:choose>

            </div>

            <bcgogo:orderPageConfigurationParam orderGroupName="order_other_condition"
                                                orderNameAndResource="order_status_repeal">
              <c:if test="${order_other_condition_order_status_repeal}">
                <label class="rad" id="orderStatusRepealLabel"><input type="checkbox" id="orderStatusRepeal"
                                                                      name="orderStatusRepeal" value="YES"/><span
                    class="red_color">包含作废</span></label>
              </c:if>
            </bcgogo:orderPageConfigurationParam>
          </div>
        </c:if>
      </bcgogo:orderPageConfigurationParam>

    <div class="clear"></div>

    <div style="padding: 0 0 5px;" class="divTit divWarehouse member">
      <span class="spanName">单据号：</span>
      <div class="warehouseList"style="width: auto;">
        <input type="text"
               value="${inquiryCenterInitialDTO.receiptNo!=null?inquiryCenterInitialDTO.receiptNo:''}"
               autocomplete="off" class="txt" name="receiptNo">




        <bcgogo:orderPageConfigurationParam orderGroupName="order_product_condition" orderNameAndResource="product_info;commodity_code">
            <c:if test="${!order_product_condition_has_none_of_the_order_group}">

              &nbsp;商品：<input type="text" class="txt J-productSuggestion" id="searchWord" name="searchWord"
                             searchField="product_info" value="品名/品牌/规格/型号/适用车辆" initValue="品名/品牌/规格/型号/适用车辆"
                             style="width:180px;"/>&nbsp;

              <c:if test="${order_product_condition_product_info}">
                    <input type="text" class="txt J-productSuggestion" id="productName" name="productName" searchField="product_name" value="品名" initValue="品名" style="width:85px;display: none;"/>
                    <input type="text" class="txt J-productSuggestion" id="productBrand" name="productBrand" searchField="product_brand" value="品牌/产地" initValue="品牌/产地" style="width:85px;display: none;"/>
                    <input type="text" class="txt J-productSuggestion" id="productSpec" name="productSpec" searchField="product_spec" value="规格" initValue="规格" style="width:85px;display: none;"/>
                    <input type="text" class="txt J-productSuggestion" id="productModel" name="productModel" searchField="product_model" value="型号" initValue="型号" style="width:85px;display: none;"/>
                    <input type="text" class="txt J-productSuggestion" id="productVehicleBrand" name="productVehicleBrand" searchField="product_vehicle_brand" value="车辆品牌" initValue="车辆品牌" style="width:85px;display: none;"/>
                    <input type="text" class="txt J-productSuggestion" id="productVehicleModel" name="productVehicleModel" searchField="product_vehicle_model" value="车型" initValue="车型" style="width:85px;display: none;"/>
                </c:if>
                <c:if test="${order_product_condition_commodity_code}">
                    <input type="text" class="txt J-productSuggestion" id="commodityCode" name="commodityCode" searchField="commodity_code" value="商品编号" initValue="商品编号" style="text-transform: uppercase;width:85px;display: none;"/>
                </c:if>
              </c:if>
        </bcgogo:orderPageConfigurationParam>
      </div>
    </div>

    <div class="clear"></div>

    <bcgogo:orderPageConfigurationParam orderGroupName="order_pay_method_condition" orderNameAndResource="cash;bankCard;cheque;deposit;[customer_deposit,WEB.VERSION.CUSTOMER.DEPOSIT.USE];
          [member_balance_pay,WEB.VERSION.VEHICLE_CONSTRUCTION];not_paid;statement_account;expense_amount;[coupon,WEB.VERSION.VEHICLE_CONSTRUCTION]">
      <c:if test="${!order_pay_method_condition_has_none_of_the_order_group}">
        <div class="divTit divWarehouse member more_condition" style="float:none;padding: 0 0 5px;" id="settlementMethod">
          <span class="spanName">结算方式：</span>

          <div class="warehouseList">
            <c:if test="${order_pay_method_condition_cash}">
              <label class="rad"><input type="checkbox" value="CASH" id="cash" name="payMethod"/>现金</label>&nbsp;
            </c:if>
            <c:if test="${order_pay_method_condition_bankCard}">
              <label class="rad"><input type="checkbox" value="BANK_CARD" id="bankCard" name="payMethod"/>银联</label>
            </c:if>
            <c:if test="${order_pay_method_condition_cheque}">
              <label class="rad"><input type="checkbox" value="CHEQUE" id="cheque" name="payMethod"/>支票</label>
            </c:if>
            <c:if test="${order_pay_method_condition_customer_deposit}">
              <label class="rad"><input type="checkbox" value="CUSTOMER_DEPOSIT" id="customerDeposit" name="payMethod"/>预收款</label>
            </c:if>
            <c:if test="${order_pay_method_condition_not_paid}">
              <label id="debtLabel" class="rad"><input type="checkbox" value="true" id="notPaid"
                                                       name="notPaid"/>挂账</label>
            </c:if>
            <c:if test="${order_pay_method_condition_statement_account}">
              <label class="rad"><input type="checkbox" value="STATEMENT_ACCOUNT" id="statement_account"
                                        name="payMethod"/>对账</label>
            </c:if>
            <c:if test="${order_pay_method_condition_coupon}">
              <label class="rad"><input type="checkbox" value="COUPON" id="coupon" name="payMethod"/>消费券</label>
              <input type="text" initValue='消费券类型' id="couponType" name="couponType" class="txt"/>
            </c:if>
            <c:if test="${order_pay_method_condition_expense_amount}">
              消费金额
              <input type="text" class="mon_search txt" name="amountLower" id="amountLower"
                     style="width:90px;"/>~<input type="text" class="mon_search txt" name="amountUpper" id="amountUpper" style="width:90px;"/> 元
            </c:if>
          </div>
        </div>
      </c:if>
    </bcgogo:orderPageConfigurationParam>

    <div class="clear height"></div>

    <div class="divTit button_conditon button_search"><a class="blue_color clean J_clean_style"
                                                         id="resetSearchCondition"
                                                         href="javascript:">清空条件</a><a class="button" id="btnSearch">查&nbsp;询</a>
    </div>
</form>

</div>

</div>

     <div class="clear i_height"></div>
     <div class="gray-radius" style="margin:0; padding:5px 10px;">

<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr id="statisticsInfo">
    <td valign="top">
      <div class="divTit">共&nbsp;<b id="totalNum">0</b>&nbsp;条记录&nbsp;&nbsp;</div>
    </td>
    <td valign="top">
      <bcgogo:hasPermission permissions="WEB.TXN.SALE_MANAGE.SALE" resourceType="menu">
        <div class="divTit" data-type="sale">销售（<b id="counts_order_total_amount_order_type_sale">0</b>笔&nbsp;
          应收<b class="red_color" id="amounts_order_total_amount_order_type_sale">0</b>元&nbsp;
          实收<b class="red_color" id="amounts_order_settled_amount_order_type_sale">0</b>元&nbsp;
          欠款<b class="red_color" id="amounts_order_debt_amount_order_type_sale">0</b>元）
        </div>
      </bcgogo:hasPermission>
      <bcgogo:hasPermission permissions="WEB.TXN.SALE_MANAGE.RETURN||WEB.TXN.PURCHASE_MANAGE.RETURN"
                            resourceType="menu">
        <div class="divTit" data-type="saleReturn">销售退货（<b id="counts_order_total_amount_order_type_sale_return">0</b>笔&nbsp;
          应付<b class="green_color" id="amounts_order_total_amount_order_type_sale_return">0</b>元&nbsp;
          实付<b class="green_color" id="amounts_order_settled_amount_order_type_sale_return">0</b>元&nbsp;
          欠付<b class="green_color" id="amounts_order_debt_amount_order_type_sale_return">0</b>元）
        </div>
      </bcgogo:hasPermission>
    </td>
  </tr>
</table>
<div style="width:949px;border-right:1px solid #C5C5C5" class="line_develop list_develop">
  <span class="fl" style="font-size:14px; color:#333; margin-left:10px;">排序方式：</span>
  <a class="J_order_sort" sortField="receipt_no" currentSortStatus="desc">单据号<span class="arrowDown J_sort_span"></span></a>
  <a class="hover J_order_sort" sortField="created_time" currentSortStatus="desc">日期<span
      class="arrowDown J_sort_span"></span></a>
  <a class="J_order_sort" sortField="order_total_amount" currentSortStatus="desc">金额<span
      class="arrowDown J_sort_span"></span></a>

</div>
<table class="tab_cuSearch J_tab_cuSearch" cellpadding="0" cellspacing="0" style="width:950px;">

  <col width="100">
  <col width="80">
  <col width="80">
  <col>
  <col width="60">
  <col width="70">
  <col width="60">
  <col width="60">
  <col width="70">

  <tr class="titleBg">
    <td style="padding-left:10px;">单据号</td>
    <td>日期</td>
    <td>单据类型</td>
    <td>内容</td>
    <td>总计</td>
    <td>实收/实付</td>
    <td>欠款</td>
    <td>优惠</td>
    <td>状态</td>
  </tr>
  <tr class="space">
    <td colspan="9"></td>
  </tr>
</table>
<bcgogo:ajaxPaging url="inquiryCenter.do?method=inquiryCenterSearchOrderAction" postFn="showResponse"
                   dynamical="inquiryCenter" display="none"/>

<div class="clear i_height"></div>
</div>
</div>
<div class="clear height"></div>

</div>

<div class="clear height"></div>

</div>


<c:if test="${fromPage =='customerData'}">
  <div class="shopping_btn">
    <div class="divImg" id="returnCustomerListBtn">
      <img src="images/return.png"/>

      <div class="sureWords" style="font-size:12px">返回客户列表</div>
    </div>
  </div>
</c:if>
</div>

<div class="clear i_height"></div>

<div id="customerOtherInfo" class="customerOtherInfo customer"
     style="position:relative; z-index:1; display:none;width: 994px; border:#ccc 1px solid; background:#eee;box-shadow:3px 3px 8px #bbb; border-top:0">

  <table width="100%" border="0" class="order-table">
    <colgroup>
      <col width="150"/>
      <col width="150"/>
      <col width="150"/>
      <col width="250"/>
    </colgroup>

    <c:if test="${!customerDTO.hasMainContact}">
      <tr class="J_otherCustomerContactContainer">
        <td>联系人：--</td>
        <td>手机号：--</td>
        <td>QQ：--</td>
        <td>Email:--</td>
      </tr>
      <tr class="J_otherCustomerContactContainer">
        <td>联系人：--</td>
        <td>手机号：--</td>
        <td>QQ：--</td>
        <td>Email:--</td>
      </tr>
    </c:if>

    <c:if test="${customerDTO.hasMainContact}">
      <c:forEach items="${customerDTO.contacts}" var="contact" varStatus="status">
        <c:choose>
          <c:when test="${contact.mainContact != 1}">
            <tr class="J_otherCustomerContactContainer">
              <td>
                联系人：${(contact.name ==null || contact.name =='')?'--':contact.name}
              </td>
              <td>手机号：${(contact.mobile ==null || contact.mobile =='')?'--':contact.mobile}</td>
              <td>QQ：${(contact.qq ==null || contact.qq =='')?'--':contact.qq}</td>
              <td>Email:${(contact.email ==null || contact.email =='')?'--':contact.email}</td>
            </tr>
          </c:when>
        </c:choose>
      </c:forEach>
    </c:if>
    <tr>
      <td>简称：
        <c:choose>
          <c:when test="${customerDTO.isOnlineShop}">
            <span>${(customerDTO.shortName ==null || customerDTO.shortName =='')?'--':customerDTO.shortName}</span>
          </c:when>
          <c:otherwise>
            <span class="J_customerBasicSpan" data-key="shortName">${(customerDTO.shortName ==null || customerDTO.shortName =='')?'--':customerDTO.shortName}</span>
          </c:otherwise>
        </c:choose>
      </td>
      <td>传真：<span class="J_customerBasicSpan" data-key="fax">${(customerDTO.fax ==null || customerDTO.fax =='')?'--':customerDTO.fax}</span></td>
      <td>客户类别：<span class="J_customerBasicSpan" data-key="customerKindStr">${(customerDTO.customerKindStr ==null || customerDTO.customerKindStr =='')?'--':customerDTO.customerKindStr}</span></td>
      <td>&nbsp;</td>
    </tr>
    <tr>
      <td colspan="4" class="border_dashed"></td>
    </tr>
    <tr>
      <td colspan="4">服务范围：<span class="J_customerBasicSpan"
                                 data-key="serviceCategoryRelationContent">${(customerDTO.serviceCategoryRelationContent ==null || customerDTO.serviceCategoryRelationContent =='')?'--':customerDTO.serviceCategoryRelationContent}</span>
      </td>
    </tr>
    <tr>
      <td colspan="4">经营产品：<span class="J_customerBasicSpan" id="businessScopeSpan"
                                 data-key="businessScopeStr">${(customerDTO.businessScopeStr ==null || customerDTO.businessScopeStr =='')?'--':customerDTO.businessScopeStr}</span></td>
    </tr>
    <tr>
      <td colspan="4">主营车型：<span class="J_customerBasicSpan" id="vehicleModelContentSpan"
                                 data-key="vehicleModelContent">${(customerDTO.vehicleModelContent ==null || customerDTO.vehicleModelContent =='')?'--':customerDTO.vehicleModelContent}</span></td>
    </tr>
    <tr>
      <td colspan="4" class="border_dashed"></td>
    </tr>
    <tr>
      <td>开户行：<span class="J_customerBasicSpan" data-key="bank">${(customerDTO.bank ==null || customerDTO.bank =='')?'--':customerDTO.bank}</span></td>
      <td>开户名：<span class="J_customerBasicSpan" data-key="bankAccountName">${(customerDTO.bankAccountName ==null || customerDTO.bankAccountName =='')?'--':customerDTO.bankAccountName}</span></td>
      <td>账号：<span class="J_customerBasicSpan" data-key="account">${(customerDTO.account ==null || customerDTO.account =='')?'--':customerDTO.account}</span></td>
      <td>结算类型：<span class="J_customerBasicSpan" data-key="settlementTypeStr">${(customerDTO.settlementTypeStr ==null || customerDTO.settlementTypeStr =='')?'--':customerDTO.settlementTypeStr}</span>
      </td>
    </tr>
    <tr>
      <td>发票类型：<span class="J_customerBasicSpan" data-key="invoiceCategoryStr">${(customerDTO.invoiceCategoryStr ==null || customerDTO.invoiceCategoryStr =='')?'--':customerDTO.invoiceCategoryStr}</span>
      </td>
      <td colspan="3">备注：<span class="J_customerBasicSpan"
                   data-key="memo">${(customerDTO.memo==''|| customerDTO.memo==null)?'--':customerDTO.shortMemo}</span></td>
    </tr>
    <tr class="titBottom_Bg">
      <td colspan="4">
        <div class="div_Btn">
          <a onclick="showDetailInfo();" class="btnUp" style="margin-bottom:-5px;"></a>
        </div>
      </td>
    </tr>

  </table>
</div>
</div>


<div id="mask" style="display:block;position: absolute;"></div>

<div id="deleteCustomer_dialog">
  <div id="deleteReceiptNo"></div>
</div>

<iframe id="iframe_PopupBox" style="position:absolute;z-index:7; left:200px; top:800px; display:none;overflow:hidden;"
        allowtransparency="true" width="840px" height="700px" scrolling="no" frameborder="0" src=""></iframe>

<iframe id="iframe_PopupBoxMakeTime" style="position:absolute;z-index:8;top:210px;left:87px;display:none; "
        allowtransparency="true" width="350px" height="500px" frameborder="0" src="" scrolling="no"></iframe>

<%@ include file="/sms/enterPhone.jsp" %>

<iframe id="iframe_PopupBox_2" style="position:absolute;z-index:5; left:200px; top:400px; display:none;"
        allowtransparency="true" width="1000px" height="1500px" frameborder="0" src="" scrolling="no"></iframe>
<input type="hidden" id="parentPageType" value="uncleUser"/>

<div id="modifyClientDiv" style="display:none;" class="alertMain newCustomers">
    <%@include file="/customer/modifyClient.jsp" %>
</div>

<div id="depositDiv" style="position: fixed; margin-left:auto; margin-right:auto; top: 37%; z-index: 8; display: none;"><!--TODO 样式是否要修改-->
    <jsp:include page="/customer/customerDepositAdd.jsp"></jsp:include>
</div>

<div class="alertMain newCustomers" id="depositOrders" style="display:none">
    <div class="height"></div>
    <div class="select_supplier">
        <input type="checkbox" name="inOutFlag" id="inFlag" checked="checked" value="1"/>
        <label class="rad" for="inFlag" >收款记录</label>
        <%--<label class="rad" for="inFlag" id="radExist">收款记录</label>--%>
        &nbsp;&nbsp;
        <input type="checkbox" name="inOutFlag" id="outFlag" checked="checked" value="2"/>
        <label class="rad" for="outFlag" >取用记录</label>
        <%--<label class="rad" for="outFlag" id="radAdd">取用记录</label>--%>
    </div>
    <div class="exist_suppliers">
        <div class="clear"></div>
        <table cellpadding="0" cellspacing="0" class="tabRecord tabSupplier" id="deposit_orders_table">
            <col width="130">
            <col width="70">
            <col width="90">
            <col width="120">
            <col width="120">
            <col width="60">
            <col>
            <tr class="tabTitle">
                <td style="padding-left:10px;">时间<a class="descending" id="depositOrdersTime"></a></td>
                <td>金额<a class="ascending" id="depositOrdersMoney"></a></td>
                <td>类型</td>
                <td>方式</td>
                <td>相关单据</td>
                <td>操作人</td>
                <td>备注</td>
            </tr>
        </table>
        <div class="hidePageAJAX">
            <bcgogo:ajaxPaging url="customerDeposit.do?method=queryDepositOrdersByCustomerIdOrSupplierId"
                               postFn="initDepositOrdersTable"
                               dynamical="dynamical1" display='none'/>
        </div>
        <div class="height"></div>
    </div>
</div>


<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>