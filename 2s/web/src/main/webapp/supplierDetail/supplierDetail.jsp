<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>

<%--
  Created by IntelliJ IDEA.
  User: monrove
  Date: 11-12-20
  Time: 下午8:06
--%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
<title>单位联系人-供应商</title>
<link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" href="js/components/themes/bcgogo-ratting<%=ConfigController.getBuildVersion()%>.css">
<link rel="stylesheet" href="js/components/themes/bcgogo-scorePanel<%=ConfigController.getBuildVersion()%>.css">
<link rel="stylesheet" type="text/css" href="styles/moreHistory<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/uncleSupplier<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/moneyDetail<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/up<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/yinshouyinfu<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/addCheck<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/register<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="js/components/themes/bcgogo-multiselectTwoDialog<%=ConfigController.getBuildVersion()%>.css" />
<style type="text/css">
  .lineTitle span {
    float: left;
  }

  .lineTitle span a {
    margin: 0 5px;
  }
  .lineTitle{width:988px; height:32px; float:left; color:#272727; font-size:14px; font-weight:bold; line-height:32px; padding-left:10px; border:#dddddd 1px solid;}

</style>

<%@include file="/WEB-INF/views/header_script.jsp" %>
<script type="text/javascript" src="js/utils/tableUtil<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.min.js"></script>
<script type="text/javascript" src="js/detailsArrears<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/supplierDetail<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/statementAccount/statementAccountUtil<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript"
        src="js/customerOrSupplier/supplierCommentUtil<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/checkMobileAndTelphone<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript"
        src="js/components/ui/bcgogo-scorePanel<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/components/ui/bcgogo-ratting<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/contact<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/page/customer/modifySupplier<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/customerOrSupplier/customerSupplierBusinessScope<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/components/ui/bcgogo-multiselectTwoDialog<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/components/ui/bcgogo-multiselectTwoDialogTree<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/utils/stringUtil<%=ConfigController.getBuildVersion()%>.js"></script>

<script type="text/javascript" src="js/page/search/inquirySystemOrder<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/components/ui/bcgogo-droplist.js"></script>

<script type="text/javascript"
        src="js/customerDetail/customerDetailInfo<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/statementAccount/customerBill<%=ConfigController.getBuildVersion()%>.js"></script>

<script type="text/javascript">
    <bcgogo:permissionParam permissions="WEB.VERSION.HAS_CUSTOMER_CONTACTS,WEB.VERSION.HAS_SUPPLIER_CONTACTS">
        APP_BCGOGO.Permission.Web_Version_Has_Supplier_Contact =${WEB_VERSION_HAS_SUPPLIER_CONTACTS}; // 客户多联系人
        APP_BCGOGO.Permission.Web_Version_Has_Customer_Contact =${WEB_VERSION_HAS_CUSTOMER_CONTACTS}
    </bcgogo:permissionParam>
</script>
<script type="text/javascript">
$(document).ready(function(){
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

    // 绑定 联系人列表 点击成为主联系人事件
    $("#supplierBasicForm").find('.icon_grayconnacter').live("click",switchContact);

});

function switchContact() {
    var $currentContactBlock =$(this).parents(".J_editSupplierContact");
    var currentLevel = $currentContactBlock.find("input[id$='level']").val();
    var $mainContactBlock = $currentContactBlock.siblings().find('.icon_connacter').parents(".J_editSupplierContact");
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


function enterPhoneSendSms(objEnterPhoneMobile) {
    smsHistory(objEnterPhoneMobile);
}

function smsHistory(mobile) {
    if (mobile == null || jQuery.trim(mobile) == "") {
        jQuery("#enterPhoneSupplierId").val(jQuery("#supplierId").val());
        Mask.Login();
        jQuery("#enterPhoneSetLocation").fadeIn("slow");
        return;
    }
    window.location = "unitlink.do?method=smsHistory&mobile=" + mobile;
}

function forwardTo(type) {
    var url = "";
    if (type * 1 == 0) {     //采购
        url = "RFbuy.do?method=create&supplierName=" + $("#customerOrSupplierName").val() + "&supplierId=" + $("#supplierId").val();
    }
    if (type * 1 == 1) {     //入库
        url = "storage.do?method=getProducts&supplierName=" + $("#customerOrSupplierName").val() + "&type=txn&supplierId=" + $("#supplierId").val();
    }
    if (type * 1 == 4) {
            url = "goodsReturn.do?method=createReturnStorageBySupplierId&supplierId="+ $("#supplierId").val();
    }
    window.location = url;
}

defaultStorage.setItem(storageKey.MenuUid,"WEB.SUPPLIER_MANAGER.SUPPLIER_DATA");

function clearDefaultAddress(){
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
userGuide.currentPageIncludeGuideStep = "SUPPLIER_APPLY_GUIDE_SUCCESS";

// add by zhuj
$(function(){

    $("#addUp").click(function(){location.href="add_innerPicking.html";})

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

    $(".table_inputContact").hide();

    $(".close").hide();
    $(".table_inputContact").not("tr:first").hover(
        function(){
            $(".close").show();
        },
        function(){
            $(".close").hide();
        }
    );

    $(".divContent").find("input.txt").hide();
    $(".divContent").find(".rad").hide();
    $(".divContent").find("select").hide();
    $("#button").hide();
    $(".businessRange").hide();
    $(".selectList").hide();
});

// add by zhuj
$(document).ready(function(){
    // 绑定 联系人列表 删除事件
    $("#modifyClientDiv .close").live("click",delContact);
    // 绑定 联系人列表 点击成为主联系人事件
    $("#modifyClientDiv .icon_grayconnacter").live("click",switchTrContact);



    function delContact(){
        var $single_contacts = $(this).closest("tr").siblings(".single_contact_gen").andSelf();
        if ($single_contacts && $single_contacts.length > 3) {
            $(this).closest("tr").remove();
            if ($single_contacts.length - 1 <= 3) {
                $(".warning").hide();
            }
        }else{
            $(this).parent().siblings().children('input').val(" "); // 清空该联系人所有信息
        }
    }

    function switchTrContact() {

        var $mainContact = $(this).closest("tr").siblings().find('.icon_connacter');
        $mainContact.removeClass('icon_connacter').addClass('icon_grayconnacter').addClass('hover'); // 主联系人灰化

        $(this).removeClass('icon_grayconnacter').removeClass('hover').addClass('icon_connacter'); // 当前联系人转变为主联系人

        var $alert = $(this).siblings(".alert"); // 保存alert div
        $(this).siblings(".alert").remove();

        $(this).parent().find("input[id$='mainContact']").val("1"); // 设置为主联系人1

        var currentLevel = $(this).parent().find("input[id$='level']").val();
        $(this).parent().find("input[id$='level']").val("0");

        $(this).unbind("click");

        $alert.insertAfter($mainContact).hide(); // 添加alert

        $mainContact.parent().find("input[id$='mainContact']").val("0"); // 修改非主联系人
        $mainContact.parent().find("input[id$='level']").val(currentLevel);
        $mainContact.live("click", switchContact);
    }

    $("#qqTalk").multiQQInvoker({
        QQ:$.fn.multiQQInvoker.getContactQQ()
    });

});


/**
 * 校验新增的手机列表里面号码是否有重复
 * 重复返回true 否则false
 * @param mobiles
 */
function isMobileDuplicate(mobiles) {

    var mobilesTemp = new Array();
    for (var index in  mobiles) {
        if(!G.isEmpty(mobiles[index])){
            if (GLOBAL.Array.indexOf(mobilesTemp, mobiles[index]) >= 0) {
                nsDialog.jAlert("手机号【" + mobiles[index] + "】重复，请重新填写。");
                return true;
            }
            mobilesTemp.push(mobiles[index]);
        }
    }
    return false;
}

</script>
</head>
<body class="bodyMain">
<input type="hidden" id="pageType" value="uncleSupplier" />
<%@include file="/WEB-INF/views/header_html.jsp" %>
<%@include file="/sms/enterPhone.jsp" %>
<div class="title"></div>
<div class="i_main clear">

<div class="mainTitles">
  <div class="titleWords">供应商资料</div>
  <c:if test="${fromPage =='supplierData'}">
    <div class="title-r" style="padding-top:48px;"><a href="customer.do?method=searchSuppiler">返回列表></a></div>
  </c:if>
</div>


<div class="customer_nav">
  <ul>
    <li><a id="customerInfoTitle" href="#" class="arrer">详细信息</a></li>
    <bcgogo:hasPermission permissions="web.statement.order.redirectSearchCustomerBill" resourceType="menu">
      <li><a id="customerStatementTitle" href="#">供应商对账单</a></li>
    </bcgogo:hasPermission>
  </ul>
  <div class="setting-relative">
          <div class="setting-absolute J_customerOptDetail" style="display: none">
              <ul>
                  <bcgogo:hasPermission permissions="WEB.TXN.PURCHASE_MANAGE.PURCHASE" resourceType="menu">
                      <li><a class="default_a" style="cursor: pointer;width: 60px;" onclick="forwardTo(0)">采购</a></li>
                  </bcgogo:hasPermission>
                  <bcgogo:hasPermission permissions="WEB.TXN.PURCHASE_MANAGE.STORAGE" resourceType="menu">
                      <li><a class="default_a" style="cursor: pointer;width: 60px;" onclick="forwardTo(1)">入库</a></li>
                  </bcgogo:hasPermission>
                  <bcgogo:hasPermission permissions="WEB.TXN.PURCHASE_MANAGE.RETURN" resourceType="menu">
                      <li><a class="default_a" style="cursor: pointer;width: 60px;" onclick="forwardTo(4)">退货</a></li>
                  </bcgogo:hasPermission>

                  <li><a class="default_a" id="duizhan" style="cursor: pointer">财务对账</a></li>
                  <bcgogo:hasPermission permissions="WEB.SUPPLIER_MANAGER.SUPPLIER_DATA.DELETE">
                      <li><a class="default_a" style="cursor: pointer;width: 60px;" onclick="deleteSupplier()">删除供应商</a></li>
                  </bcgogo:hasPermission>

              </ul>
          </div>
      </div>
  <div class="setting J_customerOpt">操 作</div>
</div>


<input type="hidden" id="totalAverageScore" value="${supplierDTO.totalAverageScore}">
<input type="hidden" id="qualityAverageScore" value="${supplierDTO.qualityAverageScore}">
<input type="hidden" id="performanceAverageScore" value="${supplierDTO.performanceAverageScore}">
<input type="hidden" id="speedAverageScore" value="${supplierDTO.speedAverageScore}">
<input type="hidden" id="attitudeAverageScore" value="${supplierDTO.attitudeAverageScore}">
<input type="hidden" id="commentRecordCount" value="${supplierDTO.commentRecordCount}">
<input type="hidden" id="supplierShopId" value="${supplierDTO.supplierShopId}">
<input type="hidden" id="isOnlineShop" value="${supplierDTO.isOnlineShop}" />
<input type="hidden" id="parentPageType" value="uncleSupplier" />
<input type="hidden" id="customerOrSupplierName" value="${supplierDTO.name}" />
<input type="hidden" id="customerId" name="customerId" value="${supplierDTO.customerId}">
<input type="hidden" id="supplierId" value="${supplierDTO.id}" />

<div class="booking-management">
<div class="titBody" id="customerDetailInfo">

<div class="lineTitle"><span>基本信息</span>

  <c:if test="${not empty supplierDTO.supplierShopId}">
    <a class="customer-tip"
       href="shopMsgDetail.do?method=renderShopMsgDetail&paramShopId=${supplierDTO.supplierShopId}">商</a>
  </c:if>
  <bcgogo:hasPermission permissions="WEB.SUPPLIER_MANAGER.SUPPLIER_DATA.UPDATE">
      <div class="editButton" id="editSupplierInfo">编 辑</div>
    </bcgogo:hasPermission>
    <span
        class="font12-normal"><a
        style="cursor: pointer"
        onclick="customerConsume('supplierTotalConsume');">累计交易: ${consumeTimes}次&nbsp;${totalTradeAmount}元</a>
      <bcgogo:hasPermission permissions="WEB.TXN.PURCHASE_MANAGE.RETURN">
        | <a style="cursor: pointer"
             onclick="customerConsume('purchaseReturn');">累计入库退货: ${totalReturnAmount}元</a>
      </bcgogo:hasPermission>
      <bcgogo:permission>
        <bcgogo:if permissions="WEB.CUSTOMER_MANAGER.CUSTOMER_ARREARS">
          | <a style="cursor: pointer"
               onclick="customerConsume('supplierTotalReceivable');">应收: ${totalReceivable}元</a>
          <a style="cursor: pointer" onclick="customerConsume('supplierTotalReturn');">应付: ${totalPayable}元</a>
        </bcgogo:if>
        <bcgogo:else>
          | <a style="cursor: pointer" onclick="customerConsume('supplierTotalReturn');">应付: ${totalPayable}元</a>
          <th>&nbsp;</th>
          <td>&nbsp;</td>
        </bcgogo:else>
      </bcgogo:permission></span>
    <bcgogo:hasPermission permissions="web.statement.order.redirectSearchCustomerBill" resourceType="menu">
      <div class="editButton" id="duizhang">对 账</div>
    </bcgogo:hasPermission>
  </div>
  <div class="clear"></div>

<div class="customer" id="supplierBasicInfoShow">
        <table width="100%" border="0" class="order-table" id="customerDetailTable" >
          <colgroup>
            <col width="150"/>
            <col width="150"/>
            <col width="150"/>
            <col width="250"/>
          </colgroup>

          <tr class="J_showCustomerOtherInfo">
            <td colspan="2">供应商名称：

              <c:choose>
                <c:when test="${supplierDTO.isOnlineShop}">
                  <span>${supplierDTO.name}</span>
                </c:when>
                <c:otherwise>
                  <span class="J_supplierBasicSpan" data-key="name">${supplierDTO.name}</span>
                </c:otherwise>
              </c:choose>

              <c:if test="${supplierDTO.identity=='isCustomer'}">
                <a class="blue_color">【客户&amp;供应商】</a>
              </c:if>
            </td>
            <td colspan="2">座机：
                <span class="J_supplierBasicSpan" data-key="landLineForAll">${(supplierDTO.landLineForAll ==null || supplierDTO.landLineForAll =='')?'--':supplierDTO.landLineForAll}</span>
            </td>
          </tr>

          <tr class="J_showCustomerOtherInfo">
            <td colspan="2">
              <input type="hidden" value="${totalDeposit}" id="hiddenDeposit">
              <span style="float:left">预付款余额：<span id="totalDepositSpan">${totalDeposit}元</span></span>&nbsp;&nbsp;<a
                class="blue_color reconciliation recharge"
                id="btnPayed" style="cursor: pointer;font-size:12px">充值</a>&nbsp;&nbsp;
              <a id="queryDepositOrders" class="blue_color" style="cursor: pointer;font-size:12px">历史记录</a></td>
            <td colspan="2">地址：<c:choose>
              <c:when test="${supplierDTO.isOnlineShop}">
                <span>${(supplierDTO.areaInfo ==null || supplierDTO.areaInfo =='')?'--':supplierDTO.areaInfo}</span>
                <span>${(supplierDTO.address ==null || supplierDTO.address =='')?'--':supplierDTO.address}</span>
              </c:when>
              <c:otherwise>
                <c:choose>
                <c:when
                    test="${(supplierDTO.areaInfo ==null || supplierDTO.areaInfo =='') &&(supplierDTO.address ==null || supplierDTO.address =='')}">
                  <span class="J_supplierBasicSpan" data-key="areaInfo"></span>
                  <span class="J_supplierBasicSpan" data-key="address">--</span>
                </c:when>
                <c:otherwise>
                  <span class="J_supplierBasicSpan" data-key="areaInfo">${(supplierDTO.areaInfo ==null || supplierDTO.areaInfo =='')?'--':supplierDTO.areaInfo}</span>
                  <span class="J_supplierBasicSpan" data-key="address">${(supplierDTO.address ==null || supplierDTO.address =='')?'--':supplierDTO.address}</span>
                </c:otherwise>
                </c:choose>
              </c:otherwise>
            </c:choose>
            </td>
          </tr>

          <tr class="J_showCustomerOtherInfo">
            <td colspan="4" class="border_dashed"></td>
          </tr>


          <tr id="supplierContactContainer" class="J_showCustomerOtherInfo">

            <c:if test="${!supplierDTO.hasMainContact}">

              <td>主联系人：--</td>
              <td>手机号：--</td>
              <td>QQ：--</td>
              <td>Email:--</td>
            </c:if>

            <c:if test="${supplierDTO.hasMainContact}">
              <c:forEach items="${supplierDTO.contacts}" var="contact" varStatus="status">
                <c:choose>
                  <c:when test="${contact.mainContact == 1}">
                    <td>主联系人：${(contact.name ==null || contact.name =='')?'--':contact.name}</td>
                    <td>手机号：${(contact.mobile ==null || contact.mobile =='')?'--':contact.mobile}</td>
                    <td>QQ：${(contact.qq ==null || contact.qq =='')?'--':contact.qq}</td>
                    <td>Email:${(contact.email ==null || contact.email =='')?'--':contact.email}</td>
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

<div class="customer" id="supplierBasicInfoEdit" style="display: none;">

    <form id="supplierBasicForm" method="post">

      <input type="hidden" id="vehicleModelIdStr" name="vehicleModelIdStr" value="${supplierDTO.vehicleModelIdStr}">
      <input type="hidden" id="thirdCategoryIdStr" name="thirdCategoryIdStr" value="${supplierDTO.thirdCategoryIdStr}">
      <textarea style="display:none" id="thirdCategoryNodeListJson">${supplierDTO.thirdCategoryNodeListJson}</textarea>
      <textarea style="display:none" id="shopVehicleBrandModelDTOListJson">${supplierDTO.shopVehicleBrandModelDTOListJson}</textarea>

      <table width="100%" border="0" class="order-table">
      <colgroup>
        <col width="89"/>
        <col width="190"/>
        <col width="65"/>
        <col width="190"/>
        <col width="60"/>
        <col width="190"/>
        <col width="65"/>
        <col width="190"/>
      </colgroup>


        <tr>
          <td class="test1"><span class="red_color">*</span>供应商名称</td>
          <td colspan="3">：
            <c:choose>
              <c:when test="${supplierDTO.isOnlineShop}">
                <span>${supplierDTO.name}</span>
                <input type="hidden" id="name" name="name" value="${supplierDTO.name}">
              </c:when>
              <c:otherwise>
                <input type="text" style="width:89.5%" maxlength="50" id="name" name="name"
                       reset-value="${supplierDTO.name}" value="${supplierDTO.name}"
                       class="txt J_formreset">
              </c:otherwise>
            </c:choose>
          </td>
          <td class="test1">座 机</td>
          <td colspan="3">：
            <input name="landLine" maxlength="14" id="landLine" value="${supplierDTO.landLine}" type="text" class="txt" style="width:27%; margin-right:5px;"/>
              <input name="landLineSecond" maxlength="14" id="landLineSecond" value="${supplierDTO.landLineSecond}" type="text" class="txt" style="width:27%; margin-right:5px;"/>
              <input name="landLineThird" maxlength="14" id="landLineThird" value="${supplierDTO.landLineThird}" type="text" class="txt" style="width:27%; margin-right:5px;"/>
          </td>

        </tr>


        <tr>
          <td class="test1">简 称</td>
          <td>：
            <c:choose>
              <c:when test="${supplierDTO.isOnlineShop}">
                <span>${supplierDTO.abbr}</span>
                <input type="hidden" id="abbr" name="abbr" value="${supplierDTO.abbr}">
              </c:when>
              <c:otherwise>
                <input type="text" maxlength="50" id="abbr" name="abbr"
                       reset-value="${supplierDTO.abbr}" value="${supplierDTO.abbr}"
                       class="txt J_formreset"/>
              </c:otherwise>
            </c:choose>
          </td>

          <c:choose>
            <c:when test="${wholesalerVersion}">
              <td class="test1">身 份</td>
              <td>：
                <label class="rad" style="font-size: 12px"><input id="identity" type="checkbox"
                                                                  <c:if
                                                                      test="${supplierDTO.identity=='isCustomer'}">checked='checked'</c:if>
                    <c:if test="${supplierDTO.permanentDualRole}"> disabled='disabled' </c:if>/>
                  也是客户
                </label>
              </td>
            </c:when>
            <c:otherwise>
              <td></td>
              <td></td>
            </c:otherwise>
          </c:choose>

          <td class="test1"><span class="red_color">* </span>地 址</td>
          <td colspan="3">：
            <c:choose>
              <c:when test="${supplierDTO.isOnlineShop}">
                <span id="areaInfo">${supplierDTO.areaInfo}</span>
                <input type="hidden" id="province" name="province" value="${supplierDTO.province}">
                <input type="hidden" id="city" name="city" value="${supplierDTO.city}">
                <input type="hidden" id="region" name="region" value="${supplierDTO.region}">
              </c:when>
              <c:otherwise>
                <input type="hidden" class="J_formreset" reset-value="${supplierDTO.province}"
                       id="select_provinceInput" value="${supplierDTO.province}"/>
                <input type="hidden" class="J_formreset" reset-value="${supplierDTO.city}"
                       id="select_cityInput" value="${supplierDTO.city}"/>
                <input type="hidden" class="J_formreset" reset-value="${supplierDTO.region}"
                       id="select_regionInput" value="${supplierDTO.region}"/>

                <select id="province" name="province" style="width:92px;">
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
              <c:when test="${supplierDTO.isOnlineShop}">
                <span>${supplierDTO.address}</span>
                <input type="hidden" id="address" name="address" value="${supplierDTO.address}">
              </c:when>
              <c:otherwise>
                <input type="text" style="width:22%; margin-right:5px;" maxlength="50" id="address" name="address"
                       reset-value="${supplierDTO.address}" value="${supplierDTO.address}" class="txt J_formreset"/>
              </c:otherwise>
            </c:choose>
          </td>
        </tr>
      <c:if test="${supplierDTO.hasMainContact}">

      <c:forEach items="${supplierDTO.contacts}" var="contact" varStatus="status">
          <c:choose>
            <c:when test="${contact.mainContact == 1}">
              <tr>
                <input type="hidden" name="contacts[${status.index}].id" id="contacts[${status.index}].id"
                       data-key="idStr" value="${contact.idStr}"/>
                <input type="hidden" name="contacts[${status.index}].level" id="contacts[${status.index}].level"
                       value="0"/>
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

      <c:forEach items="${supplierDTO.contacts}" var="contact" varStatus="status">
          <c:choose>
            <c:when test="${contact.mainContact != 1}">

              <tr>

                 <input type="hidden" name="contacts[${status.index}].id" id="contacts[${status.index}].id"
                        data-key="idStr" value="${contact.idStr}"/>
                 <input type="hidden" name="contacts[${status.index}].level" id="contacts[${status.index}].level"
                        value="${contact.level}"/>

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

      <c:if test="${!supplierDTO.hasMainContact}">
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
          <input type="text" maxlength="20" id="fax" name="fax" reset-value="${supplierDTO.fax}"
                 value="${supplierDTO.fax}" class="txt J_formreset"/>
        </td>
        <td class="test1">&nbsp;</td>
        <td>&nbsp;</td>
        <td class="test1">&nbsp;</td>
        <td>&nbsp;</td>
        <td class="test1">&nbsp;</td>
        <td>&nbsp;</td>
      </tr>

      <tr>
        <td class="test1">开户行</td>
        <td>：
          <input type="text" maxlength="20" id="bank" name="bank" reset-value="${supplierDTO.bank}"
                 value="${supplierDTO.bank}" class="txt J_formreset"/></td>
        <td class="test1">开户名</td>
        <td>：
          <input type="text" maxlength="20" id="accountName" name="accountName"
                 reset-value="${supplierDTO.accountName}" value="${supplierDTO.accountName}"
                 class="txt J_formreset"/></td>
        <td class="test1">账 号</td>
        <td>：
          <input type="text" maxlength="20" id="account" name="account"
                 reset-value="${supplierDTO.account}" value="${supplierDTO.account}"
                 class="txt J_formreset"/></td>
        <td class="test1">结算类型</td>
        <td>：
          <select style="width:78%" name="settlementTypeId" id="settlementTypeId"
                  reset-value="${supplierDTO.settlementTypeId}" class="txt J_formreset">
            <option value="">--请选择--</option>
            <c:forEach items="${settlementTypeList}" var="settlementType" varStatus="status">
              <option
                  value="${settlementType.key}" ${settlementType.key eq supplierDTO.settlementTypeId?'selected':''}>${settlementType.value}</option>
            </c:forEach>
          </select>
        </td>
      </tr>

      <tr>
        <td valign="top" class="test1">发票类型</td>
        <td valign="top">：
          <select style="width:78%" name="invoiceCategoryId" id="invoiceCategoryId" reset-value="${supplierDTO.invoiceCategoryId}"
                  class="txt J_formreset">
            <option value="">--请选择--</option>
            <c:forEach items="${invoiceCategoryList}" var="invoiceCategory" varStatus="status">
              <option
                  value="${invoiceCategory.key}" ${invoiceCategory.key eq supplierDTO.invoiceCategoryId?'selected':''}>${invoiceCategory.value}</option>
            </c:forEach>
          </select>
        </td>

        <td valign="top" class="test1">备 注</td>
        <td colspan="5" valign="top"><span class="fl">：&nbsp;</span>
          <input type="text" style="width:94%;" maxlength="400" id="memo" name="memo" reset-value="${supplierDTO.memo}" value="${supplierDTO.memo}" class="txt J_formreset"/>
        </td>
      </tr>


            </table>
      <div class="clear height"></div>

      <c:choose>
          <c:when test="${supplierDTO.isOnlineShop}">
              <table width="100%" border="0" class="order-table">
                  <colgroup>
                      <col  width="100"/>
                      <col>
                  </colgroup>
                  <tr>
                      <th>经营产品：</th>
                      <td><span>${supplierDTO.businessScope}</span></td>
                  </tr>
                  <tr>
                      <th>主营车型：</th>
                      <td>
                          <input type="hidden" id="selectBrandModel" name="selectBrandModel" value="${supplierDTO.selectBrandModel}">
                          <span>${supplierDTO.vehicleModelContent}</span>
                      </td>
                  </tr>
              </table>
              <div class="clear"></div>

          </c:when>
          <c:otherwise>
              <div class="scope-content">
                  <div class="left select-t" align="right" style="text-align: left;padding-left: 6px;" >经营产品：</div>
                  <div class="right">
                      <div class="select-t" id="" style="display: none"> <a href="#" class="wrong"></a> <span class="red_color font12">选择经营产品</span> </div>
                  </div>
                  <div class="clear"></div>
                  <div id="businessScopeTreeDiv"></div>
                  <div class="clear height"></div>
              </div>
              <div class="scope-content">
                  <div class="left select-t" align="right" style="margin-left:-20px;">主营车型：</div>
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

    <bcgogo:hasPermission permissions="WEB.SUPPLIER_MANAGER.SUPPLIER_DATA.UPDATE">
        <div class="padding10">
            <input type="button"  class="query-btn" id="saveSupplierBasicBtn" value="确认"/>
            <input type="button"  class="query-btn" id="cancelSupplierBasicBtn" value="取消"/>
        </div>
    </bcgogo:hasPermission>
    <div class="clear"></div>
</div>
</div>

<div class="clear i_height"></div>

<div id="customerDetailStatement" style="display: none;">
  <%@include file="/customerDetail/customerDetailBill.jsp" %>
</div>

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
  <input type="hidden" name="customerOrSupplierId" id="customerOrSupplierId" value="${supplierDTO.id}">
  <input type="hidden" name="debtType" id="debtType" value="">
  <input type="hidden" name="accountMemberNo" id="accountMemberNo" value="">

    <div class="divTit" style="float:none"><span class="spanName">日期：</span>&nbsp;
      <a class="btnList" id="my_date_yesterday" name="my_date_select">昨天</a>&nbsp;
      <a class="btnList" id="my_date_today" name="my_date_select">今天</a>&nbsp;
      <a class="btnList" id="my_date_lastmonth" name="my_date_select">上月</a>&nbsp;
      <a class="btnList" id="my_date_thismonth" name="my_date_select">本月</a>&nbsp;
      <a class="btnList" id="my_date_thisyear" name="my_date_select">今年</a>&nbsp;
      <input id="startDate" type="text" value="" readonly="readonly" name="startTimeStr"
             class="my_startdate txt"/>&nbsp;至&nbsp;
      <input id="endDate" type="text" value="" readonly="readonly" name="endTimeStr" class='my_enddate txt'/>&nbsp;&nbsp;
    </div>

    <div class="clear"></div>

      <bcgogo:orderPageConfigurationParam orderGroupName="order_type_condition" orderNameAndResource="[purchase_order,WEB.TXN.PURCHASE_MANAGE.PURCHASE];
            [storage_order,WEB.TXN.PURCHASE_MANAGE.STORAGE];[sale_order,WEB.TXN.SALE_MANAGE.SALE];[vehicle_construction_order,WEB.VEHICLE_CONSTRUCTION.CONSTRUCT.BASE];
            [wash_beauty_order,WEB.VEHICLE_CONSTRUCTION.WASH_BEAUTY.BASE];[purchase_return_order,WEB.TXN.PURCHASE_MANAGE.RETURN];
            [sale_return_order,WEB.TXN.SALE_MANAGE.RETURN];[buy_card_order,WEB.CUSTOMER_MANAGER.MEMBERSHIP_PACKAGE_MANAGER];
            [return_card,WEB.CUSTOMER_MANAGER.MEMBERSHIP_PACKAGE_MANAGER]">
        <c:if test="${!order_type_condition_has_none_of_the_order_group}">
          <div class="divTit divWarehouse member" style="padding: 0 0 5px;">
            <span class="spanName">单据类型：</span>

            <div class="warehouseList" id="orderTypes" style="width: auto;">
              <label class="rad" id="orderTypeAllLabel"><input type="checkbox" id="orderTypeAll"
                                                               data-name="all"/>所有</label>&nbsp;

                <c:choose>
                  <c:when test="${order_type_condition_purchase_order}">
                    <label class="rad" id="purchaseLabel"><input type="checkbox" name="orderType" value="PURCHASE"
                                                                 data-name="purchase"/>采购单</label>&nbsp;
                  </c:when>
                  <c:otherwise>
                    <bcgogo:hasPermission permissions="WEB.TXN.PURCHASE_MANAGE.PURCHASE">
                      <input type="hidden" name="orderType" origValue="PURCHASE"/>
                    </bcgogo:hasPermission>
                  </c:otherwise>
                </c:choose>

                <c:choose>
                  <c:when test="${order_type_condition_storage_order}">
                    <label class="rad" id="inventoryLabel"><input type="checkbox" name="orderType" value="INVENTORY"
                                                                  data-name="storage"/>入库单</label>&nbsp;
                  </c:when>
                  <c:otherwise>
                    <bcgogo:hasPermission permissions="WEB.TXN.PURCHASE_MANAGE.STORAGE">
                      <input type="hidden" name="orderType" origValue="INVENTORY"/>
                    </bcgogo:hasPermission>
                  </c:otherwise>
                </c:choose>

                <c:choose>
                  <c:when test="${order_type_condition_purchase_return_order}">
                    <label class="rad" id="returnLabel"><input type="checkbox" name="orderType" value="RETURN"
                                                               data-name="return"/>入库退货单</label>&nbsp;
                  </c:when>
                  <c:otherwise>
                    <bcgogo:hasPermission permissions="WEB.TXN.PURCHASE_MANAGE.RETURN">
                      <input type="hidden" name="orderType" origValue="RETURN"/>
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

      <div class="warehouseList" style="width: auto;">
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
            <c:if test="${order_pay_method_condition_deposit}">
              <label class="rad"><input type="checkbox" value="DEPOSIT" id="deposit" name="payMethod"/>预付款</label>
            </c:if>
            <c:if test="${order_pay_method_condition_not_paid}">
              <label id="debtLabel" class="rad"><input type="checkbox" value="true" id="notPaid"
                                                       name="notPaid"/>挂账</label>
            </c:if>
            <c:if test="${order_pay_method_condition_statement_account}">
              <label class="rad"><input type="checkbox" value="STATEMENT_ACCOUNT" id="statement_account"
                                        name="payMethod"/>对账</label>
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
      <div class="divTit" style="padding: 0;">共&nbsp;<b id="totalNum">0</b>&nbsp;条记录&nbsp;&nbsp;</div>
    </td>
    <td valign="top">


      <bcgogo:hasPermission permissions="WEB.TXN.PURCHASE_MANAGE.PURCHASE" resourceType="menu">
        <div class="divTit" style="padding: 0;" data-type="purchase">采购（<b id="counts_order_total_amount_order_type_purchase">0</b>笔）</div>
      </bcgogo:hasPermission>

      <bcgogo:hasPermission permissions="WEB.TXN.PURCHASE_MANAGE.STORAGE" resourceType="menu">
        <div class="divTit" style="padding: 0;" data-type="storage">入库（<b id="counts_order_total_amount_order_type_inventory">0</b>笔&nbsp;
          应付<b class="green_color" id="amounts_order_total_amount_order_type_inventory">0</b>元&nbsp;
          实付<b class="green_color" id="amounts_order_settled_amount_order_type_inventory">0</b>元&nbsp;
          欠款<b class="green_color" id="amounts_order_debt_amount_order_type_inventory">0</b>元）
        </div>
      </bcgogo:hasPermission>
      <bcgogo:hasPermission permissions="WEB.TXN.SALE_MANAGE.RETURN||WEB.TXN.PURCHASE_MANAGE.RETURN"
                            resourceType="menu">
        <div class="divTit" style="padding: 0;" data-type="return">入库退货（<b id="counts_order_total_amount_order_type_return">0</b>笔&nbsp;
          应收<b class="red_color" id="amounts_order_total_amount_order_type_return">0</b>元&nbsp;
          实收<b class="red_color" id="amounts_order_settled_amount_order_type_return">0</b>元&nbsp;
          欠款<b class="red_color" id="amounts_order_debt_amount_order_type_return">0</b>元）
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

  <col width="110">
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

<c:if test="${fromPage =='supplierData'}">
  <div class="shopping_btn">
    <div class="divImg" id="returnSupplierListBtn">
      <img src="images/return.png"/>

      <div class="sureWords" style="font-size:12px">返回供应商列表</div>
    </div>
  </div>
</c:if>

</div>
<div class="clear i_height"></div>





<div id="mask" style="display:block;position: absolute;"></div>
<iframe id="iframe_PopupBox" style="position:absolute;z-index:5; left:200px; top:400px; display:none;"
        allowtransparency="true" width="1000px" height="1500px" frameborder="0" src="" scrolling="no"></iframe>
<iframe id="iframe_PopupBox_1" style="position:absolute;z-index:5; left:200px; top:400px; display:none;"
        allowtransparency="true" width="1000px" height="1500px" frameborder="0" src="" scrolling="no"></iframe>


<!-- 付定金 -->
<div id="depositDiv" style="position: fixed; z-index: 8; display: none;">
    <jsp:include page="/customer/orderMoney.jsp"></jsp:include>
</div>

<!-- 取用记录 -->
<div class="alertMain newCustomers" id="depositOrders" style="display:none">
	<div class="height"></div>
    <div class="select_supplier">
        <input type="checkbox" name="inOutFlag" id="inFlag" checked="checked" value="1"/>
    	<label class="rad" for="inFlag">收款记录</label>
        <%--<label class="rad" for="inFlag" id="radExist">收款记录</label>--%>
        &nbsp;&nbsp;
        <input type="checkbox" name="inOutFlag" id="outFlag" checked="checked" value="2"/>
        <label class="rad" for="outFlag">取用记录</label>
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
            <td style="padding-left:10px;" >时间<a class="descending" id="depositOrdersTime"></a></td>
            <td>金额<a class="ascending" id="depositOrdersMoney"></a></td>
            <td>类型</td>
            <td>方式</td>
            <td>相关单据</td>
            <td>操作人</td>
            <td>备注</td>
        </tr>
        </table>
        <div class="hidePageAJAX">
            <bcgogo:ajaxPaging url="payable.do?method=queryDepositOrdersShopIdAndSupplierId"
                               postFn="initDepositOrdersTable"
                               dynamical="dynamical2" display='none'/>
        </div>
        <div class="height"></div>
    </div>
</div>


<!-- 挂账，扣款免付确认弹出框-->
<div id="creditDeductionBtn" style="position: fixed; left:30%; top: 37%; z-index: 9; display: none;">
    <jsp:include page="/customer/conform.jsp"></jsp:include>
</div>

<div id="customerOtherInfo" class="customerOtherInfo customer"
     style="position:relative; z-index:1; display:none;width: 994px; border:#ccc 1px solid; background:#eee;box-shadow:3px 3px 8px #bbb; border-top:0">

  <table width="100%" border="0" class="order-table">
    <colgroup>
      <col width="150"/>
      <col width="150"/>
      <col width="150"/>
      <col width="250"/>
    </colgroup>

    <c:if test="${!supplierDTO.hasMainContact}">
      <tr class="J_otherSupplierContactContainer">
        <td>联系人：--</td>
        <td>手机号：--</td>
        <td>QQ：--</td>
        <td>Email:--</td>
      </tr>
      <tr class="J_otherSupplierContactContainer">
        <td>联系人：--</td>
        <td>手机号：--</td>
        <td>QQ：--</td>
        <td>Email:--</td>
      </tr>
    </c:if>

    <c:if test="${supplierDTO.hasMainContact}">
      <c:forEach items="${supplierDTO.contacts}" var="contact" varStatus="status">
        <c:choose>
          <c:when test="${contact.mainContact != 1}">
            <tr class="J_otherSupplierContactContainer">
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
          <c:when test="${supplierDTO.isOnlineShop}">
            <span>${supplierDTO.abbr}</span>
          </c:when>
          <c:otherwise>
            <span class="J_supplierBasicSpan" data-key="abbr">${(supplierDTO.abbr ==null || supplierDTO.abbr =='')?'--':supplierDTO.abbr}</span>
          </c:otherwise>
        </c:choose>
      </td>
      <td>传真：<span class="J_supplierBasicSpan" data-key="fax">${(supplierDTO.fax ==null || supplierDTO.fax =='')?'--':supplierDTO.fax}</span></td>
      <td>&nbsp;</td>
    </tr>
    <tr>
      <td colspan="4" class="border_dashed"></td>
    </tr>
    <tr>
      <td colspan="4">经营产品：<span class="J_supplierBasicSpan" id="businessScopeContentSpan"
                                 data-key="businessScope">${(supplierDTO.businessScope ==null || supplierDTO.businessScope =='')?'--':supplierDTO.businessScope}</span></td>
    </tr>
    <tr>
      <td colspan="4">主营车型：<span class="J_supplierBasicSpan" id="vehicleModelContentSpan"
                                 data-key="vehicleModelContent">${(supplierDTO.vehicleModelContent ==null || supplierDTO.vehicleModelContent =='')?'--':supplierDTO.vehicleModelContent}</span></td>
    </tr>
    <tr>
      <td colspan="4" class="border_dashed"></td>
    </tr>
    <tr>
      <td>开户行：<span class="J_supplierBasicSpan" data-key="bank">${(supplierDTO.bank ==null || supplierDTO.bank =='')?'--':supplierDTO.bank}</span></td>
      <td>开户名：<span class="J_supplierBasicSpan" data-key="accountName">${(supplierDTO.accountName ==null || supplierDTO.accountName =='')?'--':supplierDTO.accountName}</span></td>
      <td>账号：<span class="J_supplierBasicSpan" data-key="account">${(supplierDTO.account ==null || supplierDTO.account =='')?'--':supplierDTO.account}</span></td>
      <td>结算类型：<span class="J_supplierBasicSpan" data-key="settlementType">${(supplierDTO.settlementType ==null || supplierDTO.settlementType =='')?'--':supplierDTO.settlementType}</span>
      </td>
    </tr>
    <tr>
      <td>发票类型：<span class="J_supplierBasicSpan" data-key="invoiceCategory">${(supplierDTO.invoiceCategory ==null || supplierDTO.invoiceCategory =='')?'--':supplierDTO.invoiceCategory}</span>
      </td>
      <td colspan="3">备注：<span class="J_supplierBasicSpan"
                   data-key="memo" title="${supplierDTO.memo}">${(supplierDTO.memo==''|| supplierDTO.memo==null)?'--':supplierDTO.shortMemo}</span></td>
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

<input type="hidden" id="pageType" value="uncleSupplier" />
<div id="modifyClientDiv" style="display:none;" class="alertMain newCustomers">
    <%--<jsp:include page="modifyClient.jsp"></jsp:include>--%>
    <%@include file="../txn/modifySupplier.jsp" %>
</div>




<%@include file="/WEB-INF/views/footer_html.jsp" %>
</div>
</body>
</html>