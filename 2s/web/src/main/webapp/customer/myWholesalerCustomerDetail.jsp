<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ include file="/WEB-INF/views/includes.jsp" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
<title>客户详情</title>
<link rel="stylesheet" href="js/extension/jquery/plugin/jquery-ui/themes/redmond/jquery.ui.timepicker-addon.css"/>
<link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/addCheck<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/moneyDetail<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/register<%=ConfigController.getBuildVersion()%>.css"/>

<!--add by zhuj-->
<link rel="stylesheet" type="text/css" href="styles/up<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="js/components/themes/bcgogo-multiselectTwoDialog<%=ConfigController.getBuildVersion()%>.css" />

<%@include file="/WEB-INF/views/header_script.jsp" %>
<script type="text/javascript">
    defaultStorage.setItem(storageKey.MenuUid, "WEB.CUSTOMER_MANAGER.CUSTOMER_DATA");
    defaultStorage.setItem(storageKey.MenuCurrentItem,"客户详情");

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
<script type="text/javascript" src="js/statementAccount/statementAccountUtil<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/page/customer/modifyClient<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/contact<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/customerOrSupplier/customerSupplierBusinessScope<%=ConfigController.getBuildVersion()%>.js"></script>

<script type="text/javascript" src="js/components/ui/bcgogo-multiselectTwoDialog<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/components/ui/bcgogo-multiselectTwoDialogTree<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/utils/stringUtil<%=ConfigController.getBuildVersion()%>.js"></script>

<script type="text/javascript">
// add by zhu 联系人  既是客户又是供应商的 弹出页面用到
$(document).ready(function(){
    // 绑定 联系人列表 删除事件
    $("#modifyClientDiv .close").live("click",delContact);
    // 绑定 联系人列表 点击成为主联系人事件
    $("#modifyClientDiv .icon_grayconnacter").live("click",switchTrContact);

});

function delContact(){
    var $single_contacts = $(this).closest("tr").siblings(".single_contact_gen").andSelf();
    if ($single_contacts && $single_contacts.length > 3) {
        $(this).closest("tr").remove();
        if ($single_contacts.length - 1 <= 3) {
            $(".warning").hide();
        }
    }else{
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
            url:"businessScope.do?method=getAllBusinessScope",
            success:function(data){
                if (G.isEmpty(data)) {
                    return;
                }
                var ensureDataList = [];
                if(!G.Lang.isEmpty($("#thirdCategoryNodeListJson").val())){
                    ensureDataList = JSON.parse(decodeURIComponent(G.Lang.normalize($("#thirdCategoryNodeListJson").val())));
                }
                multiSelectTwoDialogTree.init({
                    "startLevel":2,
                    "data": data,
                    "ensureDataList":ensureDataList,
                    "selector": "#businessScopeTreeDiv",
                    "onSearch":function(searchWord, event) {
                        return App.Net.syncPost({
                            url:"businessScope.do?method=getAllBusinessScope",
                            data:{"searchWord":searchWord},
                            dataType:"json"
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
            url:"businessScope.do?method=getAllStandardVehicleBrandModel",
            success:function(data){
                multiSelectTwoDialog.init({
                    "data": data,
                    "selector": "#vehicleBrandModelDiv"
                });

                if(!G.Lang.isEmpty($("#shopVehicleBrandModelDTOListJson").val())){
                    multiSelectTwoDialog.initSelectedData(JSON.parse(decodeURIComponent(G.Lang.normalize($("#shopVehicleBrandModelDTOListJson").val()))));
                    $("#partBrandModel").click();
                }else{
                    $("#allBrandModel").click();
                }
            }
        });

        $("input[name='selectBrandModel']").bind("click", function () {
            if ($(this).val() == "ALL_MODEL") {
                $("#vehicleBrandModelDiv").css("display", "none");
            } else if ($(this).val() == "PART_MODEL") {
                $("#vehicleBrandModelDiv").css("display", "block");
            }
        });
    }


    $("#alsoSupplier").click(function () {
        if (!$("#alsoSupplier").attr("checked")) {
            var permanentDualRole = false;
            APP_BCGOGO.Net.syncAjax({
                url: "customer.do?method=getCustomerById",
                dataType: "json",
                data: {customerId:$("#customerId").val()},
                success: function (data) {
                    if(data!=null && data.permanentDualRole){
                        permanentDualRole = true;
                    }
                }
            });
            if(permanentDualRole){
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
                        url:"customer.do?method=cancelCustomerBindingSupplier",
                        data:{customerId:$("#customerId").val()},
                        success:function(result){
                            if(result == 'success'){
                                nsDialog.jAlert("解绑成功！");
                                if (G.Lang.isEmpty($("#customerId").val())) {
                                    window.location.reload();
                                } else {
                                    window.location.href = "unitlink.do?method=customer&customerId=" + $("#customerId").val();
                                }
                            }else{
                                nsDialog.jAlert("解除绑定失败！")
                            }
                        },
                        error:function(){
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
                    var selectBrandModel ="";
                    if(isOnlineShop){
                        selectBrandModel = $("#selectBrandModel").val();
                    }else{
                        selectBrandModel = $("input:radio[name='selectBrandModel']:checked").val();
                    }

                    $("#modifyClientDiv #newSelectBrandModel").val(selectBrandModel);
                    $("#modifyClientDiv #updateSelectBrandModel").val(selectBrandModel);

                    $("#modifyClientDiv").dialog({
                        width:820,
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

        var $deposit = $("#deposit"),
                left = $(document).width() / 2 - $deposit.width() / 2; // TODO  找到 mask 的hide 的地方
        //TODO 改用jquery dialog
        $deposit.css("display", "block")
                .css("left", left);
        return false;
    });

    $("#div_close,#cancleBtn").click(function () {
        $(".tabTotal :text").val(""); //输入清空
        $(".i_upBody :text").val("");
        jQuery(".productDetails :text").val("");
        jQuery("#checkNoDeposit").val($("#checkNoDeposit").attr("initValue")).css("color","#9a9a9a");
        jQuery("[name='print']").removeAttr("checked");
        jQuery("#depositMemo").val("");
        jQuery("#actuallyPaidDeposit").text("0");
        $("#deposit").css("display", "none");
        $("#mask").css("display", "none");
        try {
            $(window.parent.document.body).find("input[type='button']").eq(0).focus().blur();
        } catch (e) {

        }
        return false;
    });

    /**
     * 定金弹出框  现金,银行卡，支票keyup事件
     *
     */
    $("#cashDeposit,#bankCardAmountDeposit,#checkAmountDeposit").bind("keyup", function () {
        $(this).css("color","#000000");
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
                print: $("#deposit #print").attr("checked")
            },
            cache: false,
            dataType: "json",
            success: function (jsonStr) {
                if (jsonStr.success) {
                    alert("预收金充值成功！");
                    $("#deposit").css("display", "none");
                    $("#mask").css("display", "none");
                    $("#iframe_PopupBox_1").css("display", "none");
                    if (jsonStr.operation && jsonStr.operation == 'print') {
                        window.open("customerDeposit.do?method=printDeposit&customerId=" + customerId + "&cashDeposit=" + cash + "&bankCardAmountDeposit=" + bankCardAmount + "&checkAmountDeposit=" + checkAmount + "&checkNoDeposit=" + checkNo + "&actuallyPaidDeposit=" + actuallyPaid + "&now=" + new Date(), '', "dialogWidth=1024px;dialogHeight=768px");
                    }
                    window.location.reload();
                } else {
                    alert("预收金充值失败！");
                }
            }
        });
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
        QQ:$.fn.multiQQInvoker.getContactQQ()
    });

    $("#checkNoDeposit").click(function(){
        if($(this).attr("initValue") == $(this).val()) {
            $(this).val('');
            $(this).css("color","#000000");
        }
    })
     .blur(function(){
       if(G.isEmpty($(this).val()) || $(this).attr("initValue") == $(this).val()) {
        $(this).val($(this).attr("initValue"));
        $(this).css("color","#9a9a9a");
       }
     });
});

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
            tr += '<td title="' + (data[i].operator == null ? '': data[i].operator) + '">' + (data[i].operator == null ? '': data[i].operator) + '</td>';
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

    $(".hover").live("hover", function(event){
        var _currentTarget=$(event.target).parent().find(".alert");
        _currentTarget.show();
        //因为有2px的空隙,所以绑定在parent上.
        _currentTarget.parent().mouseleave(function(event){
            event.stopImmediatePropagation();

            if($(event.relatedTarget).find(".alert")[0]!=_currentTarget[0]) {
                _currentTarget.hide();
            }
        });
    },function(event){
        var _currentTarget=$(event.target).parent().find(".alert");

        if($(event.relatedTarget).find(".alert")[0]!=_currentTarget[0]) {
            $(event.target).parent().find(".alert").hide();
        }
    });

});

// add by zhu 联系人
$(document).ready(function(){
    // 绑定 联系人列表 点击成为主联系人事件
    $("#customerBasicForm").find('.icon_grayconnacter').live("click",switchContact);

});

function switchContact() {
    var $currentContactBlock =$(this).parents(".J_editCustomerContact");
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
<input type="hidden" id="isOnlineShop" value="${customerDTO.isOnlineShop}" />
<input id="customerShopId" type="hidden" value="${customerDTO.customerShopId}">
<input id="supplierId" type="hidden" value="${customerDTO.supplierId}"/>

<%@ include file="/WEB-INF/views/header_html.jsp" %>
<div class="i_main clear">
<div class="cusTitle">客户详情</div>
<div class="titBodys">
    <a class="hover_btn" href="#" onclick="redirectUncleUser('customer')">客户详细信息</a>
    <a class="normal_btn" href="#" onclick="redirectCustomerBill('customer')">客户对账单</a>
    <div class="setting-relative">
        <div class="setting-absolute J_customerOptDetail" style="right: 94px;display: none">
            <ul>
                <bcgogo:hasPermission permissions="WEB.TXN.SALE_MANAGE.SALE" resourceType="menu">
                    <li><a class="blue_color" style="cursor: pointer" onclick="redirectSalesOrder()">购买商品</a></li>
                </bcgogo:hasPermission>
                <bcgogo:hasPermission permissions="WEB.TXN.SALE_MANAGE.RETURN" resourceType="menu">
                    <li><a class="blue_color" style="cursor: pointer" onclick="redirectSalesReturn()">销售退货</a></li>
                </bcgogo:hasPermission>
                <li><a class="blue_color" id="duizhan" style="cursor: pointer">财务对账</a></li>
                <bcgogo:hasPermission permissions="WEB.CUSTOMER_MANAGER.CUSTOMER_DELETE">
                    <li><a class="blue_color" id="deleteCustomerButton" onclick="deleteCustomer()" style="cursor: pointer">删除客户</a></li>
                </bcgogo:hasPermission>
                <bcgogo:hasPermission permissions="WEB.CUSTOMER_MANAGER.CUSTOMER_UPDATE">
                    <c:if test="${empty customerDTO.customerShopId && 'NONE_REGISTERED' eq shopStatus}">
                        <li><a class="blue_color" id="updateToShopBtn" onclick="updateToShop()" style="cursor: pointer">升级客户</a></li>
                    </c:if>
                </bcgogo:hasPermission>
            </ul>
        </div>
    </div>
    <div class="title-r" style="line-height:25px;"><a class="blue_color" href="customer.do?method=customerdata">返回客户列表></a></div>
    <div class="setting J_customerOpt"><img src="images/setting_r2_c6.jpg" />操 作 </div>
</div>
<div class="i_mainRight" id="i_mainRight">
<input type="hidden" id="isUncleUser"/>
<input id="customerId" type="hidden" value="${customerId}"/>
<input id="customerName" type="hidden" value="${customerDTO.name}"/>
<input id="id" name="id" type="hidden" value="${customerDTO.id}"/>

<input type="hidden" value="" id="modifyAll">

<div class="booking-management">
<div class="titBody">
    <div class="lineTitle" style="text-align:left;color: #444443">
        <bcgogo:hasPermission permissions="WEB.CUSTOMER_MANAGER.CUSTOMER_MODIFY">
            <div class="title-r"><a class="blue_color" style="cursor: pointer" id="editCustomerInfo"><img src="images/edit.png"/> 编辑</a></div>
        </bcgogo:hasPermission>
        基本信息
        <c:if test="${not empty customerDTO.customerShopId}">
            <a class="customer-tip" href="shopMsgDetail.do?method=renderShopMsgDetail&paramShopId=${customerDTO.customerShopId}">商</a>
        </c:if>
    </div>
    <div class="clear"></div>
    <div class="customer" id="customerBasicInfoShow">
        <div class="member-left" style="width:58%">
            <table width="100%" border="0" class="order-table">
                <colgroup>
                    <col width="20%"/>
                    <col width="30%"/>
                    <col width="20%"/>
                    <col width="30%"/>
                </colgroup>
                <tr>
                    <th>客户名称：</th>
                    <td colspan="3">
                        <c:choose>
                            <c:when test="${customerDTO.isOnlineShop}">
                                <span>${customerDTO.name}</span>
                            </c:when>
                            <c:otherwise>
                                <span class="J_customerBasicSpan" data-key="name">${customerDTO.name}</span>
                            </c:otherwise>
                        </c:choose>
                    </td>
                </tr>
                <tr>
                    <th>简称：</th>
                    <td>
                        <c:choose>
                            <c:when test="${customerDTO.isOnlineShop}">
                                <span>${customerDTO.shortName}</span>
                            </c:when>
                            <c:otherwise>
                                <span class="J_customerBasicSpan" data-key="shortName">${customerDTO.shortName}</span>
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <th>座机：</th>
                    <td>
                        <span class="J_customerBasicSpan" data-key="landLine">${customerDTO.landLine}</span>
                    </td>
                </tr>
                <tr>
                    <th>传真：</th>
                    <td>
                        <span class="J_customerBasicSpan" data-key="fax">${customerDTO.fax}</span>
                    </td>
                    <th>生日：</th>
                    <td><span class="J_customerBasicSpan" data-key="birthdayString">${customerDTO.birthdayString}</span></td>
                </tr>
                <tr>
                    <th>地址：</th>
                    <td colspan="3">
                        <c:choose>
                            <c:when test="${customerDTO.isOnlineShop}">
                                <span>${customerDTO.areaInfo}</span>
                                <span>${customerDTO.address}</span>
                            </c:when>
                            <c:otherwise>
                                <span class="J_customerBasicSpan" data-key="areaInfo">${customerDTO.areaInfo}</span>
                                <span class="J_customerBasicSpan" data-key="address">${customerDTO.address}</span>
                            </c:otherwise>
                        </c:choose>
                    </td>
                </tr>
                <tr>
                    <th>客户类别：</th>
                    <td><span class="J_customerBasicSpan" data-key="customerKindStr">${customerDTO.customerKindStr}</span></td>
                    <th><span class="J_customerIdentity" style="display: ${empty customerDTO.identityStr?'none':''}">身份：</span></th>
                    <td><span class="J_supplierBasicSpan J_customerIdentity" data-key="identityStr">${customerDTO.identityStr}</span></td>
                </tr>
            </table>
        </div>

        <div class="member-right" style="width:39%;background:none" id="customerContactContainer">
            <c:forEach items="${customerDTO.contacts}" var="contact" varStatus="status">
                <div class="contact_list"> <span title="${contact.name}" style="width: 90px;cursor: auto;white-space:nowrap; word-break:keep-all; overflow:hidden; text-overflow:ellipsis;" class="name ${contact.mainContact == 1?'icon_connacter':'icon_grayconnacter'}">${contact.name}</span>
                    <div class="mode">
                        <span class="icon_phone" title="${contact.mobile}" style="height:16px;width: 90px;white-space:nowrap; word-break:keep-all; overflow:hidden; text-overflow:ellipsis;">${contact.mobile}</span>
                        <span class="icon_QQ" title="${contact.qq}" style="height:16px;width: 90px;white-space:nowrap; word-break:keep-all; overflow:hidden; text-overflow:ellipsis;">${contact.qq}</span>
                        <span class="icon_email" title="${contact.email}" style="height:16px;width: 90px;white-space:nowrap; word-break:keep-all; overflow:hidden; text-overflow:ellipsis;">${contact.email}</span>
                    </div>
                </div>
            </c:forEach>
        </div>
        <div class="clear"></div>
    </div>
    <div class="customer" id="customerBasicInfoEdit" style="display: none;">
        <form id="customerBasicForm" method="post">
            <div class="member-left" style="width:58%">
                <table width="100%" border="0" class="order-table">
                    <colgroup>
                        <col width="20%"/>
                        <col width="30%"/>
                        <col width="20%"/>
                        <col width="30%"/>
                    </colgroup>
                    <tr>
                        <th>客户名称：</th>
                        <td colspan="3">
                            <c:choose>
                                <c:when test="${customerDTO.isOnlineShop}">
                                    <span>${customerDTO.name}</span>
                                    <input type="hidden" id="name" name="name" value="${customerDTO.name}">
                                </c:when>
                                <c:otherwise>
                                    <input type="text" style="width:434px" maxlength="50" id="name" name="name" reset-value="${customerDTO.name}" value="${customerDTO.name}" class="txt J_formreset">
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                    <tr>
                        <th>简称：</th>
                        <td>
                            <c:choose>
                                <c:when test="${customerDTO.isOnlineShop}">
                                    <span>${customerDTO.shortName}</span>
                                    <input type="hidden" id="shortName" name="shortName" value="${customerDTO.shortName}">
                                </c:when>
                                <c:otherwise>
                                    <input type="text" style="width:150px" maxlength="50" id="shortName" name="shortName" reset-value="${customerDTO.shortName}" value="${customerDTO.shortName}" class="txt J_formreset"/>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <th>座机：</th>
                        <td>
                            <input type="text" style="width:150px" maxlength="20" id="landLine" name="landLine" reset-value="${customerDTO.landLine}" value="${customerDTO.landLine}" class="txt J_formreset"/>
                        </td>
                    </tr>
                    <tr>
                        <th>传真：</th>
                        <td>
                            <input type="text" style="width:150px" maxlength="20" id="fax" name="fax" reset-value="${customerDTO.fax}" value="${customerDTO.fax}" class="txt J_formreset"/>
                        </td>
                        <th>生日：</th>
                        <td><input type="text" style="width:150px" readonly="true" id="birthdayString" name="birthdayString" reset-value="${customerDTO.birthdayString}" value="${customerDTO.birthdayString}" class="txt J_formreset"/></td>
                    </tr>
                    <tr>
                        <th>所属区域：</th>
                        <td colspan="3">
                            <c:choose>
                                <c:when test="${customerDTO.isOnlineShop}">
                                    <span id="areaInfo">${customerDTO.areaInfo}</span>
                                    <input type="hidden" id="province" name="province" value="${customerDTO.province}">
                                    <input type="hidden" id="city" name="city" value="${customerDTO.city}">
                                    <input type="hidden" id="region" name="region" value="${customerDTO.region}">
                                </c:when>
                                <c:otherwise>
                                    <input type="hidden" class="J_formreset" reset-value="${customerDTO.province}"  id="select_provinceInput" value="${customerDTO.province}"/>
                                    <input type="hidden" class="J_formreset" reset-value="${customerDTO.city}"  id="select_cityInput" value="${customerDTO.city}"/>
                                    <input type="hidden" class="J_formreset" reset-value="${customerDTO.region}"  id="select_regionInput" value="${customerDTO.region}"/>

                                    <select id="province" name="province" class="txt select" style="height:21px;width:100px;">
                                        <option value="">所有省</option>
                                    </select>
                                    <select id="city" name="city" class="txt select" style="height:21px;width:100px;">
                                        <option value="">所有市</option>
                                    </select>
                                    <select id="region" name="region" class="txt select" style="height:21px;width:100px;">
                                        <option value="">所有区</option>
                                    </select>
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                    <tr>
                        <th>详细地址：</th>
                        <td colspan="3">
                            <c:choose>
                                <c:when test="${customerDTO.isOnlineShop}">
                                    <span>${customerDTO.areaInfo}</span>
                                    <span>${customerDTO.address}</span>
                                    <input type="hidden" id="address" name="address" value="${customerDTO.address}">
                                </c:when>
                                <c:otherwise>
                                    <input type="text" style="width:434px" maxlength="50" id="address" name="address" reset-value="${customerDTO.address}" value="${customerDTO.address}" class="txt J_formreset"/>
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                    <tr>
                        <th>客户类别：</th>
                        <td>
                            <select style="height:21px;width: 150px" name="customerKind" id="customerKind" reset-value="${customerDTO.customerKind}" class="txt J_formreset">
                                <option value="">--请选择--</option>
                                <c:forEach items="${customerTypeMap}" var="customerType" varStatus="status">
                                    <option value="${customerType.key}" ${customerType.key eq customerDTO.customerKind?'selected':''}>${customerType.value}</option>
                                </c:forEach>
                            </select>
                        </td>
                        <th>身份：</th>
                        <td>
                            <label class="rad" style="font-size: 12px"><input id="alsoSupplier" name="alsoSupplier" type="checkbox"
                                   <c:if test="${customerDTO.identity=='isSupplier'}">checked='checked'</c:if>
                                    <c:if test="${customerDTO.permanentDualRole}"> disabled='disabled' </c:if>/>
                            也是供应商
                            </label>
                        </td>
                    </tr>
                </table>
            </div>
            <div class="member-right" style="width:39%;background:none">
                <c:forEach items="${customerDTO.contacts}" var="contact" varStatus="status">
                    <div class="contact_list J_editCustomerContact single_contact">
                        <input type="hidden" name="contacts[${status.index}].id" id="contacts[${status.index}].id" data-key="idStr" value="${contact.idStr}"/>
                        <input type="hidden" name="contacts[${status.index}].level" id="contacts[${status.index}].level" value="${status.index}"/>
                        <div>
                            <c:choose>
                                <c:when test="${contact.mainContact == 1}">
                                    <span  style="float: left;" class="name icon_connacter"></span>
                                    <input type="hidden" name="contacts[${status.index}].mainContact" class="J_formreset" id="contacts[${status.index}].mainContact" data-key="mainContact" reset-value="1" value="1"/>
                                </c:when>
                                <c:otherwise>
                                    <span  style="float: left;" class="name icon_grayconnacter hover"></span>
                                    <input type="hidden" name="contacts[${status.index}].mainContact" class="J_formreset" id="contacts[${status.index}].mainContact" data-key="mainContact" reset-value="0" value="0"/>
                                    <div class="alert" style="margin-top:15px">
                                        <span class="arrowTop"></span>
                                        <div class="alertAll">
                                            <div class="alertLeft"></div>
                                            <div class="alertBody">点击设为主联系人</div>
                                            <div class="alertRight"></div>
                                        </div>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                            <span  style="float: left"><input type="text" maxlength="11" style="width: 90px" name="contacts[${status.index}].name" id="contacts[${status.index}].name" data-key="name" value="${contact.name}" reset-value="${contact.name}"  class="txt"/></span>
                        </div>
                        <div class="mode">
                            <a class="icon_phone">
                                <input type="text" maxlength="11" style="width: 90px" name="contacts[${status.index}].mobile" id="contacts[${status.index}].mobile" data-key="mobile" value="${contact.mobile}" reset-value="${contact.mobile}"  class="txt J_formreset"/>
                            </a>
                            <a class="icon_QQ">
                                <input type="text" maxlength="15" style="width: 90px" name="contacts[${status.index}].qq" id="contacts[${status.index}].qq" data-key="qq" value="${contact.qq}" reset-value="${contact.qq}" class="txt J_formreset"/>
                            </a>
                            <a class="icon_email">
                                <input type="text" maxlength="20" style="width: 90px" name="contacts[${status.index}].email" id="contacts[${status.index}].email" data-key="email" value="${contact.email}" reset-value="${contact.email}" class="txt J_formreset"/>
                            </a>
                        </div>
                    </div>
                </c:forEach>
            </div>
            <div class="clear"></div>
        </form>
        <bcgogo:hasPermission permissions="WEB.CUSTOMER_MANAGER.CUSTOMER_MODIFY">
            <div class="padding10">
                <input type="button"  class="query-btn" id="saveCustomerBasicBtn" value="确认"/>
                <input type="button"  class="query-btn" id="cancelCustomerBasicBtn" value="取消"/>
            </div>
        </bcgogo:hasPermission>
        <div class="clear"></div>
    </div>
</div>
<div class="clear i_height"></div>
<div class="titBody">
    <div class="lineTitle" style="text-align:left;color: #444443">
        <bcgogo:hasPermission permissions="WEB.CUSTOMER_MANAGER.CUSTOMER_MODIFY">
            <c:if test="${!customerDTO.isOnlineShop}">
                <div class="title-r"><a  class="blue_color" style="cursor: pointer" id="editCustomerBusinessInfo"><img src="images/edit.png"/> 编辑</a></div>
            </c:if>
        </bcgogo:hasPermission>
    经营范围 </div>
    <div class="clear"></div>
    <div class="customer" id="customerBusinessInfoShow">
        <table width="100%" border="0" class="order-table">
            <colgroup>
                <col  width="100"/>
                <col>
            </colgroup>
            <tr>
                <th valign="top">服务范围：</th>
                <td><span class="J_customerBusinessSpan" data-key="serviceCategoryRelationContent">${customerDTO.serviceCategoryRelationContent}</span></td>
            </tr>
            <tr>
                <th>经营产品：</th>
                <td><span class="J_customerBusinessSpan" id="businessScopeSpan" data-key="businessScopeStr">${customerDTO.businessScopeStr}</span></td>
            </tr>
            <tr>
                <th>主营车型：</th>
                <td><span class="J_customerBusinessSpan" id="vehicleModelContentSpan" data-key="vehicleModelContent">${customerDTO.vehicleModelContent}</span></td>
            </tr>
        </table>
        <div class="clear"></div>
    </div>

    <div class="customer" id="customerBusinessInfoEdit" style="display: none">
        <form id="customerBusinessForm" method="post">
            <input type="hidden" id="serviceCategoryRelationIdStr" name="serviceCategoryRelationIdStr" value="${customerDTO.serviceCategoryRelationIdStr}">
            <input type="hidden" id="vehicleModelIdStr" name="vehicleModelIdStr" value="${customerDTO.vehicleModelIdStr}">
            <input type="hidden" id="thirdCategoryIdStr" name="thirdCategoryIdStr" value="${customerDTO.thirdCategoryIdStr}">
            <textarea style="display:none" id="thirdCategoryNodeListJson">${customerDTO.thirdCategoryNodeListJson}</textarea>
            <textarea style="display:none" id="shopVehicleBrandModelDTOListJson">${customerDTO.shopVehicleBrandModelDTOListJson}</textarea>

            <c:choose>
            <c:when test="${customerDTO.isOnlineShop}">
                <table width="100%" border="0" class="order-table">
                    <colgroup>
                        <col  width="100"/>
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
                    <div class="left select-t" align="right"><span class="red_color">* </span>服务范围&nbsp;</div>
                    <div class="right">
                        <div class="select-t">
                            <c:if test="${not empty serviceCategoryDTOList}">
                                <c:forEach items="${serviceCategoryDTOList}" var="serviceCategoryDTO">
                                    <label class="lbl">
                                        <input class="J_serviceCategoryCheckBox" type="checkbox" ${fn:contains(customerDTO.serviceCategoryRelationIdStr, serviceCategoryDTO.id)?'checked':''} value="${serviceCategoryDTO.id}">
                                            ${serviceCategoryDTO.name}</label>
                                </c:forEach>
                            </c:if>
                        </div>
                    </div>
                    <div class="clear i_height"></div>
                </div>
                <div class="clear"></div>
                <div class="scope-content">
                    <div class="left select-t" align="right">经营产品&nbsp;</div>
                    <div class="right">
                        <div class="select-t" id="" style="display: none"> <a href="#" class="wrong"></a> <span class="red_color font12">选择经营产品</span> </div>
                    </div>
                    <div class="clear"></div>
                    <div id="businessScopeTreeDiv"></div>
                    <div class="clear height"></div>
                </div>
                <div class="scope-content">
                    <div class="left select-t" align="right"><span class="red_color">*</span> 主营车型 &nbsp;</div>
                    <div class="right" style="line-height:25px">
                        <label><input type="radio" id="allBrandModel" value="ALL_MODEL" name="selectBrandModel"/>全部车型</label>&nbsp;
                        <label><input type="radio" id="partBrandModel" value="PART_MODEL" name="selectBrandModel"/>部分车型</label>&nbsp;
                    </div>
                    <div class="clear"></div>
                    <div id="vehicleBrandModelDiv" style="display:none;"></div>
                    <div class="clear i_height"></div>
                </div>
                <bcgogo:hasPermission permissions="WEB.CUSTOMER_MANAGER.CUSTOMER_MODIFY">
                    <div class="clear"></div>
                    <div class="padding10">
                        <input type="button" id="saveCustomerBusinessBtn"  class="query-btn" value="确认"/>
                        <input type="button" id="cancelCustomerBusinessBtn" class="query-btn" value="取消"/>
                    </div>
                </bcgogo:hasPermission>

            </c:otherwise>
            </c:choose>
        </form>
    </div>

</div>

<div class="clear i_height"></div>
<div class="shelvesed clear"  style="width:600px">
    <div class="topTitle" style="text-align:left;color: #444443">
        <bcgogo:hasPermission permissions="WEB.CUSTOMER_MANAGER.CUSTOMER_MODIFY">
            <div class="title-r"><a class="blue_color" style="cursor: pointer" id="editCustomerAccountInfo"><img src="images/edit.png"/> 编辑</a></div>
        </bcgogo:hasPermission>
        账户信息
    </div>
    <div class="customer" style="border:0" id="customerAccountInfoShow">
        <table width="100%" border="0" class="order-table">
            <colgroup>
                <col width="18%"/>
                <col width="30%"/>
                <col width="18%"/>
                <col width="30%"/>
            </colgroup>
            <tr>
                <th>开户行：</th>
                <td><span class="J_customerAccountSpan" data-key="bank">${customerDTO.bank}</span></td>
                <th>开户名：</th>
                <td><span class="J_customerAccountSpan" data-key="bankAccountName">${customerDTO.bankAccountName}</span></td>
            </tr>
            <tr>
                <th>账号：</th>
                <td><span class="J_customerAccountSpan" data-key="account">${customerDTO.account}</span></td>
                <th>结算类型：</th>
                <td><span class="J_customerAccountSpan" data-key="settlementTypeStr">${customerDTO.settlementTypeStr}</span></td>
            </tr>
            <tr>
                <th>发票类型：</th>
                <td><span class="J_customerAccountSpan" data-key="invoiceCategoryStr">${customerDTO.invoiceCategoryStr}</span></td>
                <th>&nbsp;</th>
                <td>&nbsp;</td>
            </tr>
            <tr>
                <th>备注：</th>
                <td colspan="3"><span class="J_customerAccountSpan" data-key="memo">${customerDTO.memo}</span></td>
            </tr>
        </table>
    </div>
    <div class="customer" style="border:0;display: none" id="customerAccountInfoEdit">
        <form id="customerAccountForm" method="post">
        <table width="100%" border="0" class="order-table">
            <colgroup>
                <col width="18%"/>
                <col width="30%"/>
                <col width="18%"/>
                <col width="30%"/>
            </colgroup>
            <tr>
                <th>开户行：</th>
                <td><input type="text" maxlength="20" id="bank" name="bank" reset-value="${customerDTO.bank}" value="${customerDTO.bank}" class="txt J_formreset"/></td>
                <th>开户名：</th>
                <td><input type="text" maxlength="20" id="bankAccountName" name="bankAccountName" reset-value="${customerDTO.bankAccountName}" value="${customerDTO.bankAccountName}" class="txt J_formreset"/></td>
            </tr>
            <tr>
                <th>账号：</th>
                <td><input type="text" maxlength="20" id="account" name="account" reset-value="${customerDTO.account}" value="${customerDTO.account}" class="txt J_formreset"/></td>
                <th>结算类型：</th>
                <td>
                    <select style="height:21px;width: 95%;" name="settlementType" id="settlementType" reset-value="${customerDTO.settlementType}" class="txt J_formreset">
                        <option value="">--请选择--</option>
                        <c:forEach items="${settlementTypeMap}" var="settlementType" varStatus="status">
                            <option value="${settlementType.key}" ${settlementType.key eq customerDTO.settlementType?'selected':''}>${settlementType.value}</option>
                        </c:forEach>
                    </select>
                </td>
            </tr>
            <tr>
                <th>发票类型：</th>
                <td>
                    <select style="height:21px;width: 95%;" name="invoiceCategory" id="invoiceCategory" reset-value="${customerDTO.invoiceCategory}" class="txt J_formreset">
                        <option value="">--请选择--</option>
                        <c:forEach items="${invoiceCatagoryMap}" var="invoiceCategory" varStatus="status">
                            <option value="${invoiceCategory.key}" ${invoiceCategory.key eq customerDTO.invoiceCategory?'selected':''}>${invoiceCategory.value}</option>
                        </c:forEach>
                    </select>
                </td>
                <th>&nbsp;</th>
                <td>&nbsp;</td>
            </tr>
            <tr>
                <th>备注：</th>
                <td colspan="3"><input type="text" maxlength="250" id="memo" name="memo" reset-value="${customerDTO.memo}" value="${customerDTO.memo}" class="txt J_formreset"/></td>
            </tr>
        </table>
        </form>
        <bcgogo:hasPermission permissions="WEB.CUSTOMER_MANAGER.CUSTOMER_MODIFY">
            <div class="padding10">
                <input type="button"  class="query-btn" id="saveCustomerAccountBtn" value="确认"/>
                <input type="button"  class="query-btn" id="cancelCustomerAccountBtn" value="取消"/>
            </div>
        </bcgogo:hasPermission>

        <div class="clear"></div>
    </div>
</div>

<div class="shelvesed shelves"  style="width:381px">
    <div class="topTitle" style="text-align:left;color: #444443">
        <div class="record-relative">
            <div class="record-absolute"><a href="#" class="J_customerConsumeHistory">点击这里可查看消费记录</a></div>
        </div>
        <div class="title-r"><a class="blue_color" style="cursor: pointer" id="customerConsumerHistoryBtn">历史消费></a></div>
        消费信息</div>
    <div class="customer" style="border:0">
        <table width="100%" border="0" class="order-table">
            <colgroup>
                <col width="28%"/>
                <col width="22%"/>
                <col width="28%"/>
                <col width="22%"/>
            </colgroup>
            <tr>
                <th>累计消费：</th>
                <td>${customerDTO.totalAmount}元</td>
                <th>累计销售退货：</th>
                <td>${customerDTO.totalReturnAmount}元 </td>
            </tr>
            <tr>
                <bcgogo:permission>
                    <bcgogo:if permissions="WEB.CUSTOMER_MANAGER.CUSTOMER_ARREARS">
                        <th>应收：</th>
                        <td>${customerDTO.totalReceivable}元</td>
                        <th>应付：</th>
                        <td>${customerDTO.totalReturnDebt}元</td>
                    </bcgogo:if>
                    <bcgogo:else>
                        <th>应付：</th>
                        <td>${customerDTO.totalReturnDebt}元</td>
                        <th>&nbsp;</th>
                        <td>&nbsp;</td>
                    </bcgogo:else>
                </bcgogo:permission>
            </tr>
            <tr>
                <td colspan="4">
                    <div class="divTit" style="float:right; margin-top:5px;">
                        <input type="hidden" value="${totalCustomerDeposit}" id="hiddenDeposit">
                        预收款余额：<span>${totalCustomerDeposit}元</span>&nbsp;
                        <a id="queryDepositOrders" style="cursor: pointer;" class="blue_color">充值/取用记录</a>&nbsp;
                        <a id="customer_deposit" style="cursor: pointer;" class="reconciliation recharge">充值</a>&nbsp;
                        <div class="title-r default_a" id="duizhang" style="cursor: pointer; margin: 2px 0px 3px;">对账 &gt;</div>
                    </div>
                </td>
            </tr>
        </table>
    </div>
</div>
<div class="clear i_height"></div>
<div class="shopping_btn">
    <div class="divImg" id="returnCustomerListBtn">
        <img src="images/return.png" />
        <div class="sureWords" style="font-size:12px">返回客户列表</div>
    </div>
</div>
</div>
<div class="clear i_height"></div>
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
    <%@include file="modifyClient.jsp" %>
</div>
<!-- 付定金 add by zhuj-->
<div id="deposit" style="position: fixed; margin-left:auto; margin-right:auto; top: 37%; z-index: 8; display: none;"><!--TODO 样式是否要修改-->
    <jsp:include page="customerDepositAdd.jsp"></jsp:include>
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