<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
<title>入库单</title>
<%
    boolean storageBinTag = ServiceManager.getService(IShopConfigService.class).isStorageBinSwitchOn(WebUtil.getShopId(request));//选配仓位功能 默认开启这个功能false
    boolean tradePriceTag = ServiceManager.getService(IShopConfigService.class).isTradePriceSwitchOn(WebUtil.getShopId(request),WebUtil.getShopVersionId(request));//选配批发价功能
%>
<link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/up<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/moneyDetail<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/draftOrder<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/goodsStorageFinish<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/accept<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/supplierLook<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/yinshouyinfu<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" href="js/components/themes/bcgogo-ratting<%=ConfigController.getBuildVersion()%>.css">


<c:choose>
    <c:when test="<%=storageBinTag%>">
        <link rel="stylesheet" type="text/css" href="styles/storageBinOn<%=ConfigController.getBuildVersion()%>.css"/>
    </c:when>
    <c:otherwise>
        <link rel="stylesheet" type="text/css" href="styles/storageBinOff<%=ConfigController.getBuildVersion()%>.css"/>
    </c:otherwise>
</c:choose>
<c:choose>
    <c:when test="<%=tradePriceTag%>">
        <link rel="stylesheet" type="text/css" href="styles/tradePriceOn<%=ConfigController.getBuildVersion()%>.css"/>
    </c:when>
    <c:otherwise>
        <link rel="stylesheet" type="text/css" href="styles/tradePriceOff<%=ConfigController.getBuildVersion()%>.css"/>
    </c:otherwise>
</c:choose>
<style type="text/css">
    .item input {
        text-overflow: clip;
    }
</style>
<%@include file="/WEB-INF/views/header_script.jsp" %>
<script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/utils/tableUtil<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/module/invoiceCommon<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/selectOptions<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/script<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/detailsArrears<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/page/txn/goodsStorage<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/inventorySearchIndex<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/supplierSearch<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/checkMobileAndTelphone<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/barCode<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/setLimit<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/suggestion<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.js"></script>
<script type="text/javascript" src="js/page/txn/txn<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/draftOrderBox<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/detailsArrears<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript"
        src="js/statementAccount/statementAccountUtil<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/page/txn/goodsStorageFinish<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/components/ui/bcgogo-ratting<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/components/ui/rattingComments<%=ConfigController.getBuildVersion()%>.js"></script>


<script type="text/javascript">
defaultStorage.setItem(storageKey.MenuUid,"WEB.TXN.PURCHASE_MANAGE.STORAGE");
defaultStorage.setItem(storageKey.MenuCurrentItem,"详情");

<bcgogo:permissionParam permissions="WEB.AUTOACCESSORYONLINE.ON_SALE_OPERATION">
    APP_BCGOGO.Permission.AutoAccessoryOnline.OnSaleOperation = ${WEB_AUTOACCESSORYONLINE_ON_SALE_OPERATION};
</bcgogo:permissionParam>
var repairOrderId = '${param.repairOrderId}';
var productAmount = '${param.productAmount}';
var productIds = '${param.productIds}';
var returnType = '${param.returnType}';
var returnIndex = '${param.returnIndex}';
$(window).load(function () {
    if($.cookie("currentStepName") == "PRODUCT_ONLINE_GUIDE_FILL_PRODUCT_INFO"){
        userGuide.caller("PRODUCT_ONLINE_GUIDE_INVENTORY_FINISH","PRODUCT_ONLINE_GUIDE");
    }
});


$(document).ready(function() {
    //print为详细结算弹出框的打印状态,id为单据ID
    var print = "${print}";
    var id = "${purchaseInventoryDTO.id}";

    if (print != "" && id != "") {
        window.open("storage.do?method=getPurchaseInventoryToPrint&purchaseInventoryId=" + id, '',
                "dialogWidth=1024px;dialogHeight=768px");
    }

    if(APP_BCGOGO.Permission.AutoAccessoryOnline.OnSaleOperation&&GLOBAL.Util.getUrlParameter("firstOpen")){
        var notInSalesIds = '${notInSalesIds}';
        if(!G.isEmpty(notInSalesIds) && G.isEmpty($("#purchaseSupplierShopId").val())){
            $("#notInSalePrompt").dialog({ width: 520, modal: true});

            $("#onSaleConfirmBtn").bind("click", function(){
                var notInSalesIdArr = notInSalesIds.split(",");
                var url = "";
                if(notInSalesIdArr.length == 1){
                    url = "goodsInOffSales.do?method=toGoodsInSalesEditor&productId=" + notInSalesIdArr[0];
                }else if(notInSalesIdArr.length > 1){
                    url = "goodsInOffSales.do?method=toBatchGoodsInSalesEditor&productIdList=" + notInSalesIds;
                }
                window.open(url);
                $("#notInSalePrompt").dialog("close");
            });

            $("#onSaleCancelBtn").bind("click", function(){
                $("#notInSalePrompt").dialog("close");
            });
        }
    }
    $(".itemAmount,.itemPurchasePrice,.itemTotal").bind("blur", function() {
        dataTransition.roundingSpanNumber("totalSpan");
        var format = $(this).val();
        format = dataTransition.rounding(format, 2);
        $(this).val(format);
        setTotal();
    });
    $("#table_productNo").bind("mouseenter", function(event) {
        $("input[type='text']", this).each(function() {
            this.title = this.value;
            $(this).tooltip({delay:0});
        });
    });
    $(document).click(function(e) {
        var e = e || event;
        var target = e.srcElement || e.target;
        if (target.id != "div_brand") {
            $("#div_brand").css("display", "none");
        }
    });

    $(document).click(function(e) {
        var e = e || event;
        var target = e.srcElement || e.target;
        if (target.id != "div_serviceName" && target.id != "Scroller-Container_ServiceName") {
            $("#div_serviceName").hide();
        }
    });
    $(".item").each(function() {
        var productVehicleStatus = $(this).find("td:eq(1)>input").eq(2).val();
        var productId2 = $(this).find("td:eq(1)>input").eq(9).val();
        if (productId2 != null && productId2 != "") {
            if (productVehicleStatus == "0") {
                $(this).find("td:eq(6)>input:first").attr("disabled", true);
                $(this).find("td:eq(7)>input:first").attr("disabled", true);
                $(this).find("td:eq(8)>input:first").attr("disabled", true);
            } else if (productVehicleStatus == "1") {
                $(this).find("td:eq(5)>input:first").val("全部");
                $(this).find("td:eq(5)>input:last").val("全部");
                $(this).find("td:eq(6)>input:first").val("");
                $(this).find("td:eq(6)>input:last").val("");
                $(this).find("td:eq(6)>input:first").attr("disabled", true);
                $(this).find("td:eq(7)>input:first").val("");
                $(this).find("td:eq(7)>input:last").val("");
                $(this).find("td:eq(7)>input:first").attr("disabled", true);
                $(this).find("td:eq(8)>input:first").val("");
                $(this).find("td:eq(8)>input:last").val("");
                $(this).find("td:eq(8)>input:first").attr("disabled", true);
            }
        }
    });
    //update RecommendedPrice
    $("input[id$=recommendedPrice]").live("change", function() {
        var price = $(this).val();
        var prefix = $(this).attr("id");
        prefix = prefix.substr(0, prefix.indexOf("."));
        var productLocalInfoIdVal = $("#" + prefix + "\\.productId").val();
        if (isNaN(price)) {
            alert("请输入正确的价格!");
            return false;
        }
        if ($.trim(price) == '') {
            price = 0.0;
        }
        if (productLocalInfoIdVal != "" && productLocalInfoIdVal != null) {     //   product_local_info not null
            $.ajax({
                type:"POST",
                url:"txn.do?method=ajaxUpdateRecommendedPrice",
                data:{
                    id:productLocalInfoIdVal,             //product_local_info ID
                    price:price
                },
                dataType:"json",
                async:false,
                cache:false
            });
        }
    });

    $("#acceptor").live("click", function() {
        var obj = this;
        $.ajax({
            type:"POST",
            url:"member.do?method=getSaleMans",
            async:true,
            data:{
                now:new Date()
            },
            cache:false,
            dataType:"json",
            error:function(XMLHttpRequest, error, errorThrown) {
                $("#div_serviceName").css({'display':'none'});
            },
            success:function(jsonObject) {
                initSaleMan(obj, jsonObject);
            }
        });
    });

    $("#acceptor").live("blur", function() {
        $("#div_serviceName").fadeOut();
    });

    $("#deleteAcceptor").hide();

    $("#deleteAcceptor").live("click", function() {
        $("#acceptor").val("");
        $("#acceptorId").val("");
        $("#deleteAcceptor").hide();
    });

    $("#acceptorDiv").mouseenter(function() {
        if ($("#acceptor").val() && !isDisable()) {
            $("#deleteAcceptor").show();
        }
    });

    $("#acceptorDiv").mouseleave(function() {
        $("#deleteAcceptor").hide();
    });
    if ($("#purchaseInventoryMessage").val()) {
        nsDialog.jAlert($("#purchaseInventoryMessage").attr("messageDetail"));
    }
    $(".payMoney").text('收¥'+dataTransition.simpleRounding(${totalReceivable},1));
    $(".fuMoney").text('付¥'+dataTransition.simpleRounding(${totalPayable},1));
});
//页面跳转光标停留在第一个商品品名处
//         js.event.add(window, "load", function() {
//            if($("itemDTOs0.productName").value==""){
//                $("itemDTOs0.productName").focus();
//            }
//         });

function showIt() {
    bcgogo.checksession({"parentWindow":window.parent, 'iframe_PopupBox':$("#iframe_PopupBox_Limit")[0], 'src':"txn.do?method=setSale"});
}

function setRecommendedPrice(percent, value) {     //在入库单处设定销售价
    if (isNaN(percent) || isNaN(value)) {
        return false;
    }
    $("input[id$=recommendedPrice]").each(function(i) {
        var recommendedPriceDom = $("input[id$=recommendedPrice]").eq(i);
        var prefix = recommendedPriceDom.attr("id");
        prefix = prefix.substr(0, prefix.indexOf("."));
        var purchasePriceDomVal = $("#" + prefix + "\\.purchasePrice").val();
        recommendedPriceDom.val((purchasePriceDomVal * (1 + percent / 100) + value * 1).toFixed(2));
        if (recommendedPriceDom.val() * 1 <= 0.0) {
            recommendedPriceDom.val('0.0');
        }
        recommendedPriceDom.change();
    });
}

function initSaleMan(domObject, jsonObject) {
    var offset = $(domObject).offset();
    var offsetHeight = $(domObject).height();
    var offsetWidth = $(domObject).width();
    var domTitle = domObject.name;
    var position = domObject.getBoundingClientRect();
    var x = position.left;
    var y = position.top;
    var selectmore = jsonObject.length;
    if (selectmore <= 0) {
        $("#div_serviceName").css({'display':'none'});
    }
    else {
        $("#div_serviceName").css({
            'display':'block','position':'absolute',
            'left':x + 'px',
            'top':y + offsetHeight + 4 + 'px',
            'width':'80px',
            overflowY:"scroll",
            overflowX:"hidden"
        });

        $("#Scroller-Container_ServiceName").html("");

        for (var i = 0; i < jsonObject.length; i++) {
            var id = jsonObject[i].idStr;
            var a = $("<a id=" + id + "></a>");
            $(a).css("color", "#000");
            a.html(jsonObject[i].name + "<br>");
            $(a).bind("mouseover", function() {
                $("#Scroller-Container_ServiceName > a").removeAttr("class");
                $(this).attr("class", "hover");
                selectValue = jsonObject[$("#Scroller-Container_ServiceName > a").index($(this)[0])].name;// $(this).html();
                selectItemNum = parseInt(this.id.substring(10));
            });

            $(a).click(function() {
                var sty = this.id;

                $("#acceptorId").val(sty);

                $(domObject).val(selectValue = jsonObject[$("#Scroller-Container_ServiceName > a").index($(this)[0])].name); //取的第一字符串

                selectItemNum = -1;
            });

            $("#Scroller-Container_ServiceName").append(a);
        }
    }
}

function newStorageOrder() {
    window.open($("#basePath").val() + "storage.do?method=getProducts&type=txn", "_blank");
}
</script>
</head>

<body class="bodyMain">
<%@ include file="/WEB-INF/views/header_html.jsp" %>
<%@ include file="/common/messagePrompt.jsp" %>
<input type="hidden" id="basePath" name="bathPath" value="<%=basePath%>"/>

<div class="i_main">
<jsp:include page="txnNavi.jsp">
    <jsp:param name="currPage" value="purchase"/>
</jsp:include>
<jsp:include page="purchaseNavi.jsp">
    <jsp:param name="currPage" value="goodsStorage"/>
</jsp:include>

<div class="i_mainRight" id="i_mainRight">
<form:form commandName="purchaseInventoryDTO" id="purchaseInventoryForm" action="storage.do?method=saveStorage"
           method="post" name="thisform">
<jsp:include page="unit.jsp"/>
<form:hidden path="shopId" value="${sessionScope.shopId}"/>
<form:hidden path="id" value="${purchaseInventoryDTO.id}"/>
<input id="orderType" name="orderType" value="purchaseInventoryOrder" type="hidden"/>
<input id="type" name="type" type="hidden" value="${param.type}">
<input id="purchaseOrderId" name="purchaseOrderId" type="hidden" value="${param.purchaseOrderId}">
<input id="repairOrderId" name="repairOrderId" type="hidden" value="${param.repairOrderId}">
<input id="productIds" name="productIds" type="hidden" value="${param.productIds}">
<input id="productAmount" name="productAmount" type="hidden" value="${param.productAmount}">
<form:hidden path="statementAccountOrderId" value="${purchaseInventoryDTO.statementAccountOrderId}"/>
<!--js permission control zhangjuntao-->
<bcgogo:permissionParam permissions="WEB.TXN.INVENTORY_MANAGE.ALARM_SETTINGS,WEB.TXN.INVENTORY_MANAGE.SALE_PRICE_SETTING,WEB.VEHICLE_CONSTRUCTION.CONSTRUCT.BASE">
    <input type="hidden" id="permissionInventoryAlarmSettings" value="${permissionParam1}"/>
    <input type="hidden" id="permissionSalePriceSettings" value="${permissionParam2}"/>
    <input type="hidden" id="repairOrderPermission" value="${permissionParam3}"/>
</bcgogo:permissionParam>
<input type="hidden" id="purchaseInventoryMessage" value="${purchaseInventoryMessage}"
       messageDetail="${purchaseInventoryMessageInfo}">
<form:hidden path="status" value="${purchaseInventoryDTO.status}"/>
<form:hidden path="draftOrderIdStr" value="${purchaseInventoryDTO.draftOrderIdStr}"/>

<ul class="yinye_title clear">
        <%--<li id="fencount">入库单</li>--%>
    <li class="danju" style="width:250px;">
        单据号：<strong>${purchaseInventoryDTO.receiptNo} <a href="javascript:showOrderOperationLog('${purchaseInventoryDTO.id}','inventory')"  class="blue_col">操作记录</a></strong>
        <form:hidden path="supplierId" value="${purchaseInventoryDTO.supplierId}"/>
    </li>
</ul>
<div class="clear"></div>
<div class="tuihuo_tb">
<table>
    <col width="80"/>
    <tr>
        <td>供应商信息</td>
    </tr>
</table>
<table class="clear" id="tb_tui">
    <col width="6%"/>
    <col width="18%"/>
    <col width="6%"/>
    <col width="14%"/>
    <col width="6%"/>
    <col width="14%"/>
    <col width="7%"/>
    <col width="17%"/>
    <col/>
    <tr>
        <td class="td_title">供应商</td>
        <td><span style="width:160px;float:left;margin-left:5px;" class="ellipsis">${purchaseInventoryDTO.supplier}</span>
            <c:if test="${not empty supplierDTO.supplierShopId}">
            <a class="icon_online_shop" href="shopMsgDetail.do?method=renderShopMsgDetail&paramShopId=${supplierDTO.supplierShopId}"></a>
            <a class="J_QQ" data_qq="${supplierDTO.qqArray}"></a>
            </c:if>
        </td>
        <td class="td_title">座机</td>
        <td>${purchaseInventoryDTO.landline}</td>
        <td class="td_title">联系人</td>
        <td>${purchaseInventoryDTO.contact}</td>
        <td class="td_title">联系电话</td>
        <td>${purchaseInventoryDTO.mobile}</td>
    </tr>
    <tr>
        <td class="td_title">地址</td>
        <td colspan="5">${purchaseInventoryDTO.address}</td>
        <td class="td_title">当前应收应付</td>
        <td class="qian_red">
            <div class="pay" id="duizhan" style="text-align:left;margin-left: 25px">
                <c:choose>
                    <c:when test="${totalReceivable != null}">
                        <span class="red_color payMoney">收¥${totalReceivable}</span>
                    </c:when>
                    <c:otherwise>
                        <span class="gray_color fuMoney">收¥0</span>
                    </c:otherwise>
                </c:choose>
                <c:choose>
                    <c:when test="${totalPayable != null}">
                        <span class="green_color fuMoney">付¥${totalPayable}</span>
                    </c:when>
                    <c:otherwise>
                        <span class="gray_color fuMoney">付¥0</span>
                    </c:otherwise>
                </c:choose>
            </div>
        </td>
    </tr>
</table>
<table>
    <col width="80"/>
    <col width="80"/>
    <tr>
        <td>入库信息</td>
        <bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.STOREHOUSE">
            <td style="width:30%;">仓库：${purchaseInventoryDTO.storehouseName}</td>
        </bcgogo:hasPermission>
        <td style="width:30%;">验收人：${purchaseInventoryDTO.acceptor}</td>
        <td>入库日期：${purchaseInventoryDTO.vestDateStr}</td>
    </tr>
</table>
<table class="clear j_table_productNo table2" id="tb_tui">
    <col width="75"/>
    <col width="75"/>
    <col width="70"/>
    <col width="80"/>
    <col width="80"/>
    <col width="80"/>
    <col width="80"/>
    <col width="80"/>
    <col width="80"/>
    <col width="50"/>
    <col width="60"/>
    <tr class="tab_title">
        <td>商品编号</td>
        <td>品名</td>
        <td>品牌/产地</td>
        <td>规格</td>
        <td>型号</td>
        <td>车型</td>
        <td>车辆品牌</td>
        <td>单价</td>
        <td>入库量/单位</td>
        <td>小计</td>
        <td>货位</td>
    </tr>
    <c:forEach items="${purchaseInventoryDTO.itemDTOs}" var="itemDTO" varStatus="status">
        <tr class="item">
            <td>
                    ${itemDTO.commodityCode!=null?itemDTO.commodityCode:""}
                <input type="hidden" value="${itemDTO.productId}"  id="itemDTOs${status.index}.productId"/>
            </td>
            <td>
                    ${itemDTO.productName!=null?itemDTO.productName:""}
                <input type="hidden" value="${itemDTO.productName}"  id="itemDTOs${status.index}.productName"/>
            </td>
            <td>
                    ${itemDTO.brand!=null?itemDTO.brand:""}
            </td>
            <td>
                    ${itemDTO.spec!=null?itemDTO.spec:''}
            </td>
            <td>
                    ${itemDTO.model!=null?itemDTO.model:""}
            </td>
            <td>
                    ${itemDTO.vehicleModel!=null?itemDTO.vehicleModel:''}
            </td>
            <td>
                    ${itemDTO.vehicleBrand!=null?itemDTO.vehicleBrand:''}
            </td>
            <bcgogo:permission>
                <bcgogo:if permissions="WEB.TXN.PURCHASE_MANAGE.STORAGE" resourceType="menu">
                    <td style="color:#FF6700;">
                            ${itemDTO.purchasePrice!=null?itemDTO.purchasePrice:""}
                    </td>
                </bcgogo:if>
            </bcgogo:permission>
            <td style="color:#FF0000;">
                    ${itemDTO.amount!=null?itemDTO.amount:""}${itemDTO.unit!=null?itemDTO.unit:""}
                <input type="hidden" value="${itemDTO.amount}"  id="itemDTOs${status.index}.amount"/>
                <input type="hidden" value="${itemDTO.unit}"  id="itemDTOs${status.index}.unit"/>
                <input type="hidden" value="${itemDTO.storageUnit}"  id="itemDTOs${status.index}.storageUnit"/>
                <input type="hidden" value="${itemDTO.sellUnit}"  id="itemDTOs${status.index}.sellUnit"/>
                <input type="hidden" value="${itemDTO.inventoryAmount}"  id="itemDTOs${status.index}.inventoryAmount"/>
                <input type="hidden" value="${itemDTO.rate}"  id="itemDTOs${status.index}.rate"/>
            </td>
            <bcgogo:hasPermission permissions="WEB.TXN.PURCHASE_MANAGE.STORAGE" resourceType="menu">
                <td style="color:#6D8FB9;">
                        ${itemDTO.total!=null?itemDTO.total:""}
                </td>
            </bcgogo:hasPermission>
            <td class="storage_bin_td">
                    ${itemDTO.storageBin}
            </td>
        </tr>
    </c:forEach>
    <tr>
        <td colspan="10">合计：</td>
        <td colspan="2">${purchaseInventoryDTO.total!=null?purchaseInventoryDTO.total:""}</td>
    </tr>
    <tr>
        <td colspan="1" class="td_title">备注：</td>
        <td colspan="11"><span class="sale-memo">${purchaseInventoryDTO.memo}</span></td>
    </tr>
</table>
<div class="height"></div>
<strong class="jie_info clear">结算信息</strong>
<div class="jie_detail clear">
    <div class="info">
        <div>应付总额：&nbsp;<span>${purchaseInventoryDTO.total!=null?purchaseInventoryDTO.total:""}元</span></div>
        <div style="">实付总计：&nbsp;<span>${purchaseInventoryDTO.stroageActuallyPaid}元</span></div>
        <div style="">优惠总计：&nbsp;<span>${purchaseInventoryDTO.stroageSupplierDeduction}元</span></div>
        <div style="">挂账金额：&nbsp;<span class="red_color" id="guazhang">${purchaseInventoryDTO.stroageCreditAmount}元</span></div>
    </div>

    <table cellpadding="0" cellspacing="0" class="tabDan clear tabRu">
        <col width="120">
        <col width="60">
        <col width="90">
        <col width="90">
        <col width="70">
        <col width="70">
        <col>

        <tr  class="tabBg">
            <td>结算日期</td>
            <td >结算人</td>
            <td >上次结余</td>
            <td >本次实付</td>
            <td >本次优惠</td>
            <td >本次挂账</td>
            <td >附加信息</td>
        </tr>
        <c:forEach items="${payableHistoryRecordDTOs}" var="payableHistoryRecordDTO" varStatus="status">

            <c:if test="${payableHistoryRecordDTO.statementAccountFlag == true && payableHistoryRecordDTO.statementAmount>=0}" var="isStatementAccountRecord">
                <tr>
                    <td>${payableHistoryRecordDTO.paidTimeStr}</td>
                    <td>${payableHistoryRecordDTO.payer}</td>
                    <td>${payableHistoryRecordDTO.actuallyPaid + payableHistoryRecordDTO.deduction + payableHistoryRecordDTO.creditAmount}</td>
                    <td>${payableHistoryRecordDTO.statementAmount}</td>
                    <td>${payableHistoryRecordDTO.deduction}</td>
                    <td>${payableHistoryRecordDTO.creditAmount}</td>
                    <td>
                        对账结算：¥${payableHistoryRecordDTO.statementAmount}&nbsp;&nbsp;对账单号：<a class="blue_color"  onclick="openStatementOrder('${purchaseInventoryDTO.statementAccountOrderId}');" href="javascript:;">${receiveNo}</a>
                    </td>
                </tr>
            </c:if>
            <c:if test="${!isStatementAccountRecord && payableHistoryRecordDTO.actuallyPaid + payableHistoryRecordDTO.deduction + payableHistoryRecordDTO.creditAmount>0}">
                <tr>
                    <td>${payableHistoryRecordDTO.paidTimeStr}</td>
                    <td>${payableHistoryRecordDTO.payer}</td>

                    <td>
                        <c:if test="${status.index==0}" var="isFirstRecord">
                            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;--
                        </c:if>
                        <c:if test="${!isFirstRecord}">
                            ${payableHistoryRecordDTO.actuallyPaid + payableHistoryRecordDTO.deduction + payableHistoryRecordDTO.creditAmount}
                        </c:if>
                    </td>
                    <td>${payableHistoryRecordDTO.actuallyPaid}</td>
                    <td>${payableHistoryRecordDTO.deduction}</td>
                    <td>${payableHistoryRecordDTO.creditAmount}</td>
                    <td>
                        <c:if test="${payableHistoryRecordDTO.cash>0}">
                            现金：¥${payableHistoryRecordDTO.cash}<br />
                        </c:if>

                        <c:if test="${payableHistoryRecordDTO.bankCardAmount>0}">
                            银联：¥${payableHistoryRecordDTO.bankCardAmount}<br />
                        </c:if>
                        <c:if test="${payableHistoryRecordDTO.checkAmount>0}">
                            支票：¥${payableHistoryRecordDTO.checkAmount}
                            使用支票号${payableHistoryRecordDTO.checkNo}<br/>
                        </c:if>
                        <c:if test="${payableHistoryRecordDTO.depositAmount>0}">
                            预付款：¥${payableHistoryRecordDTO.depositAmount}
                        </c:if>
                    </td>
                </tr>
            </c:if>
        </c:forEach>

    </table>
</div></div>
<div class="clear"></div>
<div class="shopping_btn" style="float:right; clear:right;">
    <bcgogo:hasPermission  resourceType="menu" permissions="WEB.TXN.PURCHASE_MANAGE.STORAGE">

        <bcgogo:hasPermission permissions="WEB.SUPPLIER_MANAGER.SUPPLIER_DATA.DUE_SETTLEMENT">
            <c:if test="${purchaseInventoryDTO.status=='PURCHASE_INVENTORY_DONE' && purchaseInventoryDTO.stroageCreditAmount>0}">
                <div class="btn_div_Img">
                    <input type="button" id = "storageDebt" class="saleAccount j_btn_i_operate" onclick="toPayableSettlement('${purchaseInventoryDTO.supplierIdStr}','purchaseInventoryOrder')"
                           onfocus="this.blur();"/>
                    <div class="optWords">欠款结算</div>
                </div>
            </c:if>
        </bcgogo:hasPermission>
        <bcgogo:hasPermission permissions="WEB.TXN.SALE_MANAGE.RETURN">
            <%--<c:if test="${salesOrderDTO.status eq 'SALE_DONE' || salesOrderDTO.status eq 'SALE_DEBT_DONE'}">--%>
            <div id="returnStorageDiv" class="salesReturn_div"  style="float:left;">
                <input id="returnStorageBtn" type="button" onfocus="this.blur();"/>
                <div class="salesReturn_text_div">退货</div>
            </div>
            <%--</c:if>--%>
        </bcgogo:hasPermission>
        <div class="btn_div_Img" id="print_div" style="float:right;">
            <input id="printBtn" type="button" class="print j_btn_i_operate" onfocus="this.blur();"/>
            <input id="print" type="hidden" name="print" value="${purchaseInventoryDTO.print}"/>

            <div class="optWords">打印</div>
        </div>


    </bcgogo:hasPermission>

</div>
<bcgogo:permission>
    <bcgogo:if permissions="WEB.TXN.PURCHASE_MANAGE.STORAGE.CANCEL">
        <div class="invalidImg" id="invalid_div">
            <input id="nullifyBtn" type="button" onfocus="this.blur();"/>

            <div class="invalidWords" id="invalidWords">作废</div>
        </div>
    </bcgogo:if>
</bcgogo:permission>
<bcgogo:hasPermission permissions="WEB.TXN.PURCHASE_MANAGE.STORAGE" resourceType="menu">
    <div class="copyInput_div" id="copyInput_div">
        <input id="copyInput" type="button" onfocus="this.blur();"/>

        <div class="copyInput_text_div" id="copyInput_text">复制</div>
    </div>
</bcgogo:hasPermission>

<!--结算详细弹出框-->
    <%--<div id="payDetail" style="position: fixed; left:33%; top: 37%; z-index: 10; display: none;">--%>
    <%--<jsp:include page="payDetail.jsp"></jsp:include>--%>
    <%--</div>--%>
    <%--<input type="hidden" id="settledAmount" style="width:100px;"/>--%>
<form:hidden path="deduction" value="${purchaseInventoryDTO.deduction}"/>
<form:hidden path="paidtype" value=''/>
<form:hidden path="creditAmount" value="${purchaseInventoryDTO.creditAmount}"/>
<form:hidden path="cash" value="${purchaseInventoryDTO.cash}"/>
<form:hidden path="bankCardAmount" value="${purchaseInventoryDTO.bankCardAmount}"/>
<form:hidden path="checkAmount" value="${purchaseInventoryDTO.checkAmount}"/>
<form:hidden path="checkNo" value="${purchaseInventoryDTO.checkNo}"/>
<form:hidden path="actuallyPaid" value="${purchaseInventoryDTO.actuallyPaid}"/>
<form:hidden path="depositAmount" value="${purchaseInventoryDTO.depositAmount}"/>
<form:hidden path="returnMoneyType" value="${purchaseInventoryDTO.returnMoneyType}"/>

<input type="hidden" id="hiddenSubmitClick"/>
</div>
</form:form>
</div>
</div>

<div id="div_brand" class="i_scroll" style="display:none;height:230px">
    <div class="Scroller-Container" id="Scroller-Container_id">
    </div>
</div>
<%--<c:choose>--%>

<div class="zuofei" id="zuofei"></div>

<%--</c:choose>--%>

<div id="mask" style="display:block;position: absolute;">
</div>

<iframe id="iframe_PopupBox" style="position:absolute;z-index:5; left:200px; top:400px; display:none;"
        allowtransparency="true" width="1000px" height="1000px" frameborder="0" src="" scrolling="no"></iframe>
<iframe id="iframe_PopupBox_1" style="position:absolute;z-index:6; left:200px; top:400px; display:none;"
        allowtransparency="true" width="1000px" height="500px" frameborder="0" src="" scrolling="no"></iframe>
<iframe id="iframe_PopupBox_Limit" style="position:absolute;z-index:5; left:40%; top:35%; display:none;"
        allowtransparency="true" width="286px" height="180px" frameborder="0" src="" scrolling="no"></iframe>

<div id="draftOrder_dialog" style="display:none">
    <div class="i_draft_table">
        <table cellpadding="0" cellspacing="0" class="i_draft_table_box" id="draft_table">
            <col>
            <col width="50">
            <col width="100">
            <col width="220">
            <col width="220">
            <col width="400">
            <col>
            <tr class="tab_title">
                <td class="tab_first"></td>
                <td>No</td>
                <td>单据号</td>
                <td>保存时间</td>
                <td>供应商</td>
                <td>采购商品</td>
                <td class="tab_last"></td>
            </tr>
        </table>
        <!--分页-->
        <div class="hidePageAJAX">
            <jsp:include page="/common/pageAJAX.jsp">
                <jsp:param name="url" value="draft.do?method=getDraftOrders"></jsp:param>
                <jsp:param name="data" value="{startPageNo:1,orderTypes:'INVENTORY'}"></jsp:param>
                <jsp:param name="jsHandleJson" value="initOrderDraftTable"></jsp:param>
                <jsp:param name="hide" value="hideComp"></jsp:param>
                <jsp:param name="dynamical" value="dynamical1"></jsp:param>
            </jsp:include>
        </div>
    </div>
</div>

<div id="commodityCode_dialog" style="display:none">
    <span id="commodityCode_dialog_msg"></span>
</div>


<!-- 供应商下拉菜单 zhangchuanlong-->
<div id="div_brandvehiclelicenceNo" class="i_scroll" style="display:none;width:300px;">
    <div class="Container" style="width:300px;">
        <div id="Scroller-1licenceNo" style="width:300px;">
            <div class="Scroller-ContainerSupplier" id="Scroller-Container_idlicenceNo">
            </div>
        </div>
    </div>
</div>
<div id="div_serviceName" class="i_scroll" style="display:none;width:150px;">
    <div class="Scroller-Container" id="Scroller-Container_ServiceName">
    </div>
</div>
<iframe id="iframe_PopupBox_account" style="position:absolute;z-index:5; left:500px; top:300px; display:none;"
        allowtransparency="true" width="900px" height="450px" frameborder="0" src="" scrolling="no"></iframe>
<!-- 搜索下拉, TODO 以后移到组件里 -->
<div id="id-searchcomplete"></div>
<div id="id-searchcompleteMultiselect"></div>

<!-- 选择现金或定金 确认弹出框-->
<div id="sureCashOrDeposit" style="position: fixed; left:30%; top: 37%; z-index: 14; display: none;">
    <jsp:include page="sureCashOrDeposit.jsp"></jsp:include>
</div>
<!-- 挂账，扣款免付确认弹出框-->
<div id="creditDeductionBtn" style="position: fixed; left:30%; top: 37%; z-index:11; display: none;">
    <jsp:include page="conform.jsp"></jsp:include>
</div>
<div id="dialog-confirm" title="提醒" style="display:none">
    <p><span class="ui-icon ui-icon-alert" style="float:left; margin:0 7px 20px 0;"></span>

    <div id="dialog-confirm-text"></div>
    </p>
</div>
<style>
    .Scroller-Container a {
        /* 引入up.css导致下拉列表样式被覆盖为白色，将应付款确认框替换为JQuery UI后可删除  */
        color: #000000;
    }
</style>

<div id="newSettlePage" style="display:none"></div>

<bcgogo:hasPermission permissions="WEB.SUPPLIER_MANAGER.SUPPLIER_COMMENT.SAVE">

    <input id="purchaseSupplierShopId" name="purchaseSupplierShopId" type="hidden"
           value="${purchaseInventoryDTO.purchaseSupplierShopId}">

    <%--//供应商评价提示--%>
    <div class="i_searchBrand" id="supplierCommentNotice" title="友情提示" style="display:none; width:500px;">
        <h3>您购买的商品已成功入库！ </h3>
        <h3> 来评价你的供应商并说说您本次的购物体验吧！</h3>
        <table border="0" width="480">
            <tr>
                <td colspan="2">
                    <div class="btnClick" style="height:50px; line-height:50px">
                        <input type="button" id="supplierCommentConfirmBtn" onfocus="this.blur();" value="确&nbsp;定">
                        <input type="button" id="supplierCommentCancelBtn" onfocus="this.blur();" value="下次再说">
                    </div>
                </td>
            </tr>
        </table>
    </div>

    <%--//供应商评价打分--%>
    <div class="i_searchBrand" id="supplierCommentDiv" title="评价供应商" style="display:none; width: 500px;">

        <form id="supplierCommentForm" method="post" action="storage.do?method=saveSupplierComment">
            <table border="0" width="480">
                <tr>
                    <td>
                        货品 质量：
                        <input type="hidden" id="qualityScoreDivHidden" name="qualityScore" value=""/>
                        <input type="hidden" id="purchaseInventoryIdStr" name="purchaseInventoryIdStr"
                               value="${purchaseInventoryDTO.idStr}"/>
                    </td>
                    <td id="qualityScoreDiv"></td>
                    <td rowspan="4">
                        <div class="alertScore">
                            <a class="arrowLeft"></a>

                            <div class="alertInfo">
                                <div class="alertTop"></div>
                                <div class="alertBody">
                                    <div>小提示：点击星星就能打分了，打分完全是匿名滴。</div>
                                    <a class="yellow_Star"></a>
                                    <a class="hand"></a>
                                </div>
                                <div class="alertBottom"></div>
                            </div>
                        </div>
                    </td>
                </tr>
                <tr>
                    <td>
                        货品性价比：
                        <input type="hidden" id="performanceScoreDivHidden" name="performanceScore" value=""/>
                    </td>
                    <td id="performanceScoreDiv"></td>
                </tr>

                <tr>
                    <td>
                        发货 速度：
                        <input type="hidden" id="speedScoreDivHidden" name="speedScore" value=""/>
                    </td>
                    <td id="speedScoreDiv"></td>
                </tr>
                <tr>
                    <td>
                        服务 态度：
                        <input type="hidden" id="attitudeScoreDivHidden" name="attitudeScore" value=""/>
                    </td>
                    <td id="attitudeScoreDiv"></td>
                </tr>
                <tr>
                    <td>详细评论：</td>
                    <td colspan="2"><textarea id="supplierCommentContent" name="commentContent" style="width:320px;"
                                              maxlength="500" onkeydown="getRemainChar(this);" onkeyup="getRemainChar(this);"></textarea>
                    </td>
                </tr>
                <tr>
                    <td colspan="3">
                        <span style="margin-left: 50px;">您还能输入<span id="supplierCommentContentRemain">500</span>个字</span>
                    </td>
                </tr>
                <tr>
                    <td colspan="3">
                        <div class="btnClick" style="height:50px; line-height:50px">
                            <input type="button" id="commentConfirmBtn" onfocus="this.blur();" value="发表评论">
                            <input type="button" id="commentCancelBtn" onfocus="this.blur();" value="取消">
                        </div>
                    </td>
                </tr>
            </table>
        </form>
    </div>

    <%--//供应商评价成功--%>
    <div class="i_searchBrand" id="supplierCommentSuccess" title="友情提示" style="display:none; width:500px;">

        <h3>您的评价已发表成功！您可前往采购单</h3>

        <h3><a href="#" id="redirectPurchaseOrder" style="color:#005DB7">${purchaseInventoryDTO.purchaseReceiptNo}</a>查看您的评论
        </h3>
        <input type="hidden" id="purchaseOrderIdStr" name="purchaseOrderIdStr"
               value="${purchaseInventoryDTO.purchaseOrderIdStr}"/>
        <table border="0" width="480">
                <%--<tr>--%>
                <%--<td colspan="2">您的评价已发表成功！您可前往采购单</td>--%>
                <%--</tr>--%>
                <%--<tr>--%>
                <%--<td colspan="2"><a href="#" style="color:#005DB7">${purchaseInventoryDTO.purchaseReceiptNo}</a>查看您的评论</td>--%>
                <%--</tr>--%>
            <tr>
                <td colspan="3">
                    <div class="btnClick" style="height:50px; line-height:50px">
                        <input type="button" id="commentSuccessBtn" onfocus="this.blur();" value="确认">
                    </div>
                </td>
            </tr>
        </table>
    </div>
</bcgogo:hasPermission>


<div class="i_searchBrand" id="notInSalePrompt" title="友情提示" style="display:none; width:500px;">
    <h3>已成功入库！ </h3>
    <h3>是否上架刚刚入库的商品？上架后系统将推荐您的商品给需要的客户哦！</h3>
    <table border="0" width="480">
        <tr>
            <td colspan="2">
                <div class="btnClick" style="height:50px; line-height:50px">
                    <input type="button" id="onSaleConfirmBtn" onfocus="this.blur();" value="现在去上架">
                    <input type="button" id="onSaleCancelBtn" onfocus="this.blur();" value="下次再说">
                </div>
            </td>
        </tr>
    </table>
</div>
<jsp:include page="/txn/orderOperationLog.jsp" />
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>
