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
<title>供应商详情</title>
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
                $("#vehicleBrandModelDiv").css("display", "none");
            } else if ($(this).val() == "PART_MODEL") {
                $("#vehicleBrandModelDiv").css("display", "block");
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
    window.location = encodeURI("sms.do?method=smswrite&supplierId="+$("#supplierId").val()+"&mobile=" + mobile);
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
defaultStorage.setItem(storageKey.MenuCurrentItem,"供应商详情");

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

    $(".tabSlip tr").css({"border":"1px solid #bbbbbb","border-width":"1px 0px"});
    $(".tabSlip tr:nth-child(odd)").css("background","#eaeaea");
    $(".tabSlip tr").not(".titleBg").hover(
            function () {
                $(this).find("td").css({"background":"#fceba9","border":"1px solid #ff4800","border-width":"1px 0px","color":"#ff4800"});

                $(this).css("cursor","pointer");
            },
            function () {
                $(this).find("td").css({"background-Color":"#FFFFFF","border":"1px solid #bbbbbb","border-width":"1px 0px 0px 0px","color":"#272727"});
                $(".tabSlip tr:nth-child(odd)").not(".titleBg" ).find("td").css("background","#eaeaea");
            }
    );

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
<div class="cusTitle">供应商详情</div>
<div class="titBodys">
    <a class="hover_btn" href="#" onclick="redirectUncleUser('supplier')">供应商详细信息</a>
    <a class="normal_btn" href="#" onclick="redirectCustomerBill('supplier')">供应商对账单</a>
    <div class="setting-relative">
        <div class="setting-absolute J_customerOptDetail" style="right: 106px;display: none">
            <ul>
                <bcgogo:hasPermission permissions="WEB.TXN.PURCHASE_MANAGE.PURCHASE" resourceType="menu">
                    <li><a class="default_a" style="cursor: pointer" onclick="forwardTo(0)">采购</a></li>
                </bcgogo:hasPermission>
                <bcgogo:hasPermission permissions="WEB.TXN.PURCHASE_MANAGE.STORAGE" resourceType="menu">
                    <li><a class="default_a" style="cursor: pointer" onclick="forwardTo(1)">入库</a></li>
                </bcgogo:hasPermission>
                <bcgogo:hasPermission permissions="WEB.TXN.PURCHASE_MANAGE.RETURN" resourceType="menu">
                    <li><a class="default_a" style="cursor: pointer" onclick="forwardTo(4)">退货</a></li>
                </bcgogo:hasPermission>

                <li><a class="default_a" id="duizhan" style="cursor: pointer">财务对账</a></li>
                <bcgogo:hasPermission permissions="WEB.SUPPLIER_MANAGER.SUPPLIER_DATA.DELETE">
                    <li><a class="default_a" style="cursor: pointer" onclick="deleteSupplier()">删除供应商</a></li>
                </bcgogo:hasPermission>

            </ul>
        </div>
    </div>
    <div class="title-r" style="line-height:25px;"><a class="default_a" href="customer.do?method=searchSuppiler&resetSearchCondition=true">返回供应商列表></a></div>
    <div class="setting J_customerOpt"><img src="images/setting_r2_c6.jpg" />操 作 </div>
</div>

<div class="i_mainRight" id="i_mainRight">

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
<div class="titBody">
<div class="lineTitle" style="text-align:left;color: #444443">
    <bcgogo:hasPermission permissions="WEB.SUPPLIER_MANAGER.SUPPLIER_DATA.UPDATE">
        <div class="title-r"><a class="default_a" style="cursor: pointer" id="editSupplierInfo"><img src="images/edit.png"/> 编辑</a></div>
    </bcgogo:hasPermission>
    基本信息
    <c:if test="${not empty supplierDTO.supplierShopId}">
        <a class="customer-tip" style="margin:9px 0 5px 5px;" href="shopMsgDetail.do?method=renderShopMsgDetail&paramShopId=${supplierDTO.supplierShopId}">商</a>
        <a id="qqTalk"></a>
    </c:if>
</div>
<div class="clear"></div>
<div class="customer" id="supplierBasicInfoShow">
    <div class="member-left" style="width:58%">
        <table width="100%" border="0" class="order-table">
            <colgroup>
                <col width="20%"/>
                <col width="30%"/>
                <col width="20%"/>
                <col width="30%"/>
            </colgroup>
            <tr>
                <th>供应商名称：</th>
                <td colspan="3">
                    <c:choose>
                        <c:when test="${supplierDTO.isOnlineShop}">
                            <span>${supplierDTO.name}</span>
                        </c:when>
                        <c:otherwise>
                            <span class="J_supplierBasicSpan" data-key="name">${supplierDTO.name}</span>
                        </c:otherwise>
                    </c:choose>
                </td>
            </tr>
            <tr>
                <th>简称：</th>
                <td>
                    <c:choose>
                        <c:when test="${supplierDTO.isOnlineShop}">
                            <span>${supplierDTO.abbr}</span>
                        </c:when>
                        <c:otherwise>
                            <span class="J_supplierBasicSpan" data-key="abbr">${supplierDTO.abbr}</span>
                        </c:otherwise>
                    </c:choose>
                </td>
                <th>座机：</th>
                <td>
                    <span class="J_supplierBasicSpan" data-key="landLine">${supplierDTO.landLine}</span>
                </td>
            </tr>
            <c:choose>
                <c:when test="${wholesalerVersion}">
                    <tr>
                        <th>传真：</th>
                        <td>
                            <span class="J_supplierBasicSpan" data-key="fax">${supplierDTO.fax}</span>
                        </td>
                        <th><span class="J_supplierIdentity" style="display: ${empty supplierDTO.identityStr?'none':''}">身份：</span></th>
                        <td><span class="J_supplierBasicSpan J_supplierIdentity" data-key="identityStr">${supplierDTO.identityStr}</span></td>
                    </tr>
                </c:when>
                <c:otherwise>
                    <tr>
                        <th>传真：</th>
                        <td>
                            <span class="J_supplierBasicSpan" data-key="fax">${supplierDTO.fax}</span>
                        </td>
                    </tr>
                    <tr>
                        <th></th>
                        <td></td>
                    </tr>
                </c:otherwise>
            </c:choose>

            <tr>
                <th>地址：</th>
                <td colspan="3">
                    <c:choose>
                        <c:when test="${supplierDTO.isOnlineShop}">
                            <span>${supplierDTO.areaInfo}</span>
                            <span>${supplierDTO.address}</span>
                        </c:when>
                        <c:otherwise>
                            <span class="J_supplierBasicSpan" data-key="areaInfo">${supplierDTO.areaInfo}</span>
                            <span class="J_supplierBasicSpan" data-key="address">${supplierDTO.address}</span>
                        </c:otherwise>
                    </c:choose>
                </td>
            </tr>
        </table>
    </div>

    <div class="member-right" style="width:39%;background:none" id="supplierContactContainer">
        <c:forEach items="${supplierDTO.contacts}" var="contact" varStatus="status">
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
<div class="customer" id="supplierBasicInfoEdit" style="display: none;">
    <form id="supplierBasicForm" method="post">
        <div class="member-left" style="width:58%">
            <table width="100%" border="0" class="order-table">
                <colgroup>
                    <col width="20%"/>
                    <col width="30%"/>
                    <col width="20%"/>
                    <col width="30%"/>
                </colgroup>
                <tr>
                    <th>供应商名称：</th>
                    <td colspan="3">
                        <c:choose>
                            <c:when test="${supplierDTO.isOnlineShop}">
                                <span>${supplierDTO.name}</span>
                                <input type="hidden" id="name" name="name" value="${supplierDTO.name}">
                            </c:when>
                            <c:otherwise>
                                <input type="text" style="width:434px" maxlength="50" id="name" name="name" reset-value="${supplierDTO.name}" value="${supplierDTO.name}" class="txt J_formreset">
                            </c:otherwise>
                        </c:choose>
                    </td>
                </tr>
                <tr>
                    <th>简称：</th>
                    <td>
                        <c:choose>
                            <c:when test="${supplierDTO.isOnlineShop}">
                                <span>${supplierDTO.abbr}</span>
                                <input type="hidden" id="abbr" name="abbr" value="${supplierDTO.abbr}">
                            </c:when>
                            <c:otherwise>
                                <input type="text" style="width:150px" maxlength="50" id="abbr" name="abbr" reset-value="${supplierDTO.abbr}" value="${supplierDTO.abbr}" class="txt J_formreset"/>
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <th>座机：</th>
                    <td>
                        <input type="text" style="width:150px" maxlength="20" id="landLine" name="landLine" reset-value="${supplierDTO.landLine}" value="${supplierDTO.landLine}" class="txt J_formreset"/>
                    </td>
                </tr>
                <c:choose>
                    <c:when test="${wholesalerVersion}">
                        <tr>
                            <th>传真：</th>
                            <td>
                                <input type="text" style="width:150px" maxlength="20" id="fax" name="fax" reset-value="${supplierDTO.fax}" value="${supplierDTO.fax}" class="txt J_formreset"/>
                            </td>
                            <th>身份：</th>
                            <td>
                                <label class="rad"><input id="identity" type="checkbox" <c:if test="${supplierDTO.identity=='isCustomer'}">checked='checked'</c:if>
                                        <c:if test="${supplierDTO.permanentDualRole}"> disabled='disabled' </c:if>/>
                                    也是客户
                                </label>
                            </td>
                        </tr>
                    </c:when>
                    <c:otherwise>
                        <tr>
                            <th>传真：</th>
                            <td>
                                <input type="text" style="width:150px" maxlength="20" id="fax" name="fax" reset-value="${supplierDTO.fax}" value="${supplierDTO.fax}" class="txt J_formreset"/>
                            </td>
                        </tr>
                        <tr>
                            <th></th>
                            <td></td>
                        </tr>
                    </c:otherwise>
                </c:choose>

                <tr>
                    <th>所属区域：</th>
                    <td colspan="3">
                        <c:choose>
                            <c:when test="${supplierDTO.isOnlineShop}">
                                <span id="areaInfo">${supplierDTO.areaInfo}</span>
                                <input type="hidden" id="province" name="province" value="${supplierDTO.province}">
                                <input type="hidden" id="city" name="city" value="${supplierDTO.city}">
                                <input type="hidden" id="region" name="region" value="${supplierDTO.region}">
                            </c:when>
                            <c:otherwise>
                                <input type="hidden" class="J_formreset" reset-value="${supplierDTO.province}"  id="select_provinceInput" value="${supplierDTO.province}"/>
                                <input type="hidden" class="J_formreset" reset-value="${supplierDTO.city}"  id="select_cityInput" value="${supplierDTO.city}"/>
                                <input type="hidden" class="J_formreset" reset-value="${supplierDTO.region}"  id="select_regionInput" value="${supplierDTO.region}"/>

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
                            <c:when test="${supplierDTO.isOnlineShop}">
                                <span>${supplierDTO.address}</span>
                                <input type="hidden" id="address" name="address" value="${supplierDTO.address}">
                            </c:when>
                            <c:otherwise>
                                <input type="text" style="width:434px" maxlength="50" id="address" name="address" reset-value="${supplierDTO.address}" value="${supplierDTO.address}" class="txt J_formreset"/>
                            </c:otherwise>
                        </c:choose>
                    </td>
                </tr>
            </table>
        </div>
        <div class="member-right" style="width:39%;background:none">
            <c:forEach items="${supplierDTO.contacts}" var="contact" varStatus="status">
                <div class="contact_list J_editSupplierContact single_contact">
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
                        <span  style="float: left"><input type="text" style="width: 90px" maxlength="11" name="contacts[${status.index}].name" id="contacts[${status.index}].name" data-key="name" value="${contact.name}" reset-value="${contact.name}"  class="txt J_formreset"/></span>
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
<div class="titBody">
    <div class="lineTitle" style="text-align:left;color: #444443">
        <bcgogo:hasPermission permissions="WEB.SUPPLIER_MANAGER.SUPPLIER_DATA.UPDATE">
            <c:if test="${!supplierDTO.isOnlineShop}">
                <div class="title-r"><a class="default_a" style="cursor: pointer" id="editSupplierBusinessInfo"><img src="images/edit.png"/> 编辑</a></div>
            </c:if>
        </bcgogo:hasPermission>
        经营范围 </div>
    <div class="clear"></div>
    <div class="customer" id="supplierBusinessInfoShow">
        <table width="100%" border="0" class="order-table">
            <colgroup>
                <col  width="100"/>
                <col>
            </colgroup>
            <tr>
                <th>经营产品：</th>
                <td><span class="J_supplierBusinessSpan" id="businessScopeContentSpan" data-key="businessScope">${supplierDTO.businessScope}</span></td>
            </tr>
            <tr>
                <th>主营车型：</th>
                <td><span class="J_supplierBusinessSpan" id="vehicleModelContentSpan" data-key="vehicleModelContent">${supplierDTO.vehicleModelContent}</span></td>
            </tr>
        </table>
        <div class="clear"></div>
    </div>

    <div class="customer" id="supplierBusinessInfoEdit" style="display: none">
        <form id="supplierBusinessForm" method="post">
            <input type="hidden" id="vehicleModelIdStr" name="vehicleModelIdStr" value="${supplierDTO.vehicleModelIdStr}">
            <input type="hidden" id="thirdCategoryIdStr" name="thirdCategoryIdStr" value="${supplierDTO.thirdCategoryIdStr}">
            <textarea style="display:none" id="thirdCategoryNodeListJson">${supplierDTO.thirdCategoryNodeListJson}</textarea>
            <textarea style="display:none" id="shopVehicleBrandModelDTOListJson">${supplierDTO.shopVehicleBrandModelDTOListJson}</textarea>

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
                    <bcgogo:hasPermission permissions="WEB.SUPPLIER_MANAGER.SUPPLIER_DATA.UPDATE">
                        <div class="clear"></div>
                        <div class="padding10">
                            <input type="button" id="saveSupplierBusinessBtn"  class="query-btn" value="确认"/>
                            <input type="button" id="cancelSupplierBusinessBtn" class="query-btn" value="取消"/>
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
        <bcgogo:hasPermission permissions="WEB.SUPPLIER_MANAGER.SUPPLIER_DATA.UPDATE">
            <div class="title-r"><a class="default_a" style="cursor: pointer" id="editSupplierAccountInfo"><img src="images/edit.png"/> 编辑</a></div>
        </bcgogo:hasPermission>
        账户信息
    </div>
    <div class="customer" style="border:0" id="supplierAccountInfoShow">
        <table width="100%" border="0" class="order-table">
            <colgroup>
                <col width="18%"/>
                <col width="30%"/>
                <col width="18%"/>
                <col width="30%"/>
            </colgroup>
            <tr>
                <th>开户行：</th>
                <td><span class="J_supplierAccountSpan" data-key="bank">${supplierDTO.bank}</span></td>
                <th>开户名：</th>
                <td><span class="J_supplierAccountSpan" data-key="accountName">${supplierDTO.accountName}</span></td>
            </tr>
            <tr>
                <th>账号：</th>
                <td><span class="J_supplierAccountSpan" data-key="account">${supplierDTO.account}</span></td>
                <th>结算类型：</th>
                <td><span class="J_supplierAccountSpan" data-key="settlementType">${supplierDTO.settlementType}</span></td>
            </tr>
            <tr>
                <th>发票类型：</th>
                <td><span class="J_supplierAccountSpan" data-key="invoiceCategory">${supplierDTO.invoiceCategory}</span></td>
                <th>&nbsp;</th>
                <td>&nbsp;</td>
            </tr>
            <tr>
                <th>备注：</th>
                <td colspan="3"><span class="J_supplierAccountSpan" data-key="memo">${supplierDTO.memo}</span></td>
            </tr>
        </table>
    </div>
    <div class="customer" style="border:0;display: none" id="supplierAccountInfoEdit">
        <form id="supplierAccountForm" method="post">
            <table width="100%" border="0" class="order-table">
                <colgroup>
                    <col width="18%"/>
                    <col width="30%"/>
                    <col width="18%"/>
                    <col width="30%"/>
                </colgroup>
                <tr>
                    <th>开户行：</th>
                    <td><input type="text" maxlength="20" id="bank" name="bank" reset-value="${supplierDTO.bank}" value="${supplierDTO.bank}" class="txt J_formreset"/></td>
                    <th>开户名：</th>
                    <td><input type="text" maxlength="20" id="accountName" name="accountName" reset-value="${supplierDTO.accountName}" value="${supplierDTO.accountName}" class="txt J_formreset"/></td>
                </tr>
                <tr>
                    <th>账号：</th>
                    <td><input type="text" maxlength="20" id="account" name="account" reset-value="${supplierDTO.account}" value="${supplierDTO.account}" class="txt J_formreset"/></td>
                    <th>结算类型：</th>
                    <td>
                        <select style="height:21px;width: 95%;" name="settlementTypeId" id="settlementTypeId" reset-value="${supplierDTO.settlementTypeId}" class="txt J_formreset">
                            <option value="">--请选择--</option>
                            <c:forEach items="${settlementTypeList}" var="settlementType" varStatus="status">
                                <option value="${settlementType.key}" ${settlementType.key eq supplierDTO.settlementTypeId?'selected':''}>${settlementType.value}</option>
                            </c:forEach>
                        </select>
                    </td>
                </tr>
                <tr>
                    <th>发票类型：</th>
                    <td>
                        <select style="height:21px;width: 95%;" name="invoiceCategoryId" id="invoiceCategoryId" reset-value="${supplierDTO.invoiceCategoryId}" class="txt J_formreset">
                            <option value="">--请选择--</option>
                            <c:forEach items="${invoiceCategoryList}" var="invoiceCategory" varStatus="status">
                                <option value="${invoiceCategory.key}" ${invoiceCategory.key eq supplierDTO.invoiceCategoryId?'selected':''}>${invoiceCategory.value}</option>
                            </c:forEach>
                        </select>
                    </td>
                    <th>&nbsp;</th>
                    <td>&nbsp;</td>
                </tr>
                <tr>
                    <th>备注：</th>
                    <td colspan="3"><input type="text" maxlength="250" id="memo" name="memo" reset-value="${supplierDTO.memo}" value="${supplierDTO.memo}" class="txt J_formreset"/></td>
                </tr>
            </table>
        </form>
        <bcgogo:hasPermission permissions="WEB.SUPPLIER_MANAGER.SUPPLIER_DATA.UPDATE">
            <div class="padding10">
                <input type="button"  class="query-btn" id="saveSupplierAccountBtn" value="确认"/>
                <input type="button"  class="query-btn" id="cancelSupplierAccountBtn" value="取消"/>
            </div>
        </bcgogo:hasPermission>

        <div class="clear"></div>
    </div>
</div>

<div class="shelvesed shelves"  style="width:381px">
    <div class="topTitle" style="text-align:left;color: #444443">
        <div class="record-relative">
            <div class="record-absolute"><a href="#" class="J_supplierConsumeHistory">点击这里可查看交易记录</a></div>
        </div>
        <div class="title-r"><a class="default_a" style="cursor: pointer" id="supplierConsumerHistoryBtn">历史交易></a></div>
        交易信息</div>
    <div class="customer" style="border:0">
        <table width="100%" border="0" class="order-table">
            <colgroup>
                <col width="28%"/>
                <col width="22%"/>
                <col width="28%"/>
                <col width="22%"/>
            </colgroup>
            <tr>
                <th>累计交易：</th>
                <td>${totalTradeAmount}元</td>
                <th>累计入库退货：</th>
                <td>${totalReturnAmount}元 </td>
            </tr>
            <tr>
                <th>应收：</th>
                <td>${totalReceivable}元</td>
                <th>应付：</th>
                <td>${totalPayable}元</td>
            </tr>
            <tr>
                <td colspan="4">
                    <div class="divTit" style="float:right; margin-top:5px;">
                        <input id="hiddenDeposit" type="hidden" value="${totalDeposit}">
                        预付款余额：<span>${totalDeposit}元</span>&nbsp;
                        <a id="queryDepositOrders" style="cursor: pointer;" class="blue_color">充值/取用记录</a>&nbsp;
                        <bcgogo:hasPermission permissions="WEB.SUPPLIER_MANAGER.SUPPLIER_DATA.PAY_EARNEST_MONEY">
                            <a id="btnPayed" style="cursor: pointer;" class="reconciliation recharge">充值</a>&nbsp;
                        </bcgogo:hasPermission>
                        <div class="title-r default_a" id="duizhang" style="cursor: pointer; margin: 2px 0px 3px;">对账 &gt;</div>
                    </div>
                </td>
            </tr>
        </table>
    </div>
</div>
<div class="clear i_height"></div>
<div class="titBody">
    <div class="lineTitle" style="text-align:left;color: #444443">付款记录</div>
    <div class="lineBody bodys">
        <div class="clear i_height"></div>
        <div class="cuSearch">
            <div class="gray-radius" style="margin:0;">
                <div style="width:949px;border-right:1px solid #C5C5C5" class="line_develop list_develop">
                    <form id="consumptionHistoryForm">
                    <span class="historyRecord">付款记录查询</span>
                    <span style="float: left">时间
                        <input type="text" id="fromTimeStr" style="width:90px; height:17px;" class="txt">&nbsp;至&nbsp;
                        <input type="text" id="toTimeStr" style="width:90px; height:17px;" class="txt">
                        <input id="payRecordSearchBtn" type="button"  class="inquiry-btn" value="查询"/>
                    </span>
                    <a class="payTime" id="arrearDown" name='created'>付款时间<span id="arrearDownArrow" class="arrowDown"></span></a>
                    </form>
                </div>
                <table class="tab_cuSearch" cellpadding="0" cellspacing="0" style="width:950px;" id="pay_history_record">
                    <colgroup>
                        <col width="30">
                        <col width="120">
                        <col width="100">
                        <col width="80">
                        <col width="80">
                        <col width="60">
                        <col width="60">
                        <col>
                        <col width="50">
                        <col width="50">
                        <col width="50">
                        <col width="80">
                        <col width="50">
                        <col width="50">
                        <col width="50">
                    </colgroup>
                    <tr class="titleBg">
                        <td style="padding-left:10px;">NO</td>
                        <td>付款时间</td>
                        <td>单据号</td>
                        <td>单据类型</td>
                        <td>金额</td>
                        <td>已付金额</td>
                        <td>现金</td>
                        <td>银联</td>
                        <td>支票</td>
                        <td>预付款</td>
                        <td>优惠</td>
                        <td>支票号码</td>
                        <td>实付</td>
                        <td>挂账</td>
                        <td>状态</td>
                    </tr>
                </table>
                <div class="clear i_height"></div>
                <!----------------------------分页----------------------------------->
                <div class="i_pageBtn">
                    <jsp:include page="/common/pageAJAX.jsp">
                        <jsp:param name="url" value="payable.do?method=payHistoryRecords"></jsp:param>
                        <jsp:param name="data" value="{startPageNo:1,supplierId:$('#supplierId').val()}"></jsp:param>
                        <jsp:param name="jsHandleJson" value="initPayableHistoryRecord"></jsp:param>
                        <jsp:param name="dynamical" value="dynamical4"></jsp:param>
                        <jsp:param name="display" value="none"></jsp:param>
                    </jsp:include>
                </div>
            </div>
            <div class="clear i_height"></div>
        </div>
    </div>
    <div class="lineBottom"></div>
    <div class="clear i_height"></div>
</div>
<div class="clear i_height"></div>
<div class="shopping_btn">
    <div class="divImg" id="returnSupplierListBtn">
        <img src="images/return.png" />
        <div class="sureWords" style="font-size:12px">返回供应商列表</div>
    </div>
</div>
</div>
</div>

<div id="mask" style="display:block;position: absolute;"></div>
<iframe id="iframe_PopupBox" style="position:absolute;z-index:5; left:200px; top:400px; display:none;"
        allowtransparency="true" width="1000px" height="1500px" frameborder="0" src="" scrolling="no"></iframe>
<iframe id="iframe_PopupBox_1" style="position:absolute;z-index:5; left:200px; top:400px; display:none;"
        allowtransparency="true" width="1000px" height="1500px" frameborder="0" src="" scrolling="no"></iframe>


<!-- 付定金-->
<div id="deposit" style="position: fixed; z-index: 8; display: none;">
    <jsp:include page="orderMoney.jsp"></jsp:include>
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
    <jsp:include page="conform.jsp"></jsp:include>
</div>
<input type="hidden" id="pageRows" value="10" />
<input type="hidden" id="pageType" value="uncleSupplier" />
<div id="modifyClientDiv" style="display:none;" class="alertMain newCustomers">
    <%--<jsp:include page="modifyClient.jsp"></jsp:include>--%>
    <%@include file="../txn/modifySupplier.jsp" %>
</div>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>