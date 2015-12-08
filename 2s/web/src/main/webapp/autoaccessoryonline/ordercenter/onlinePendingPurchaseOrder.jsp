<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>订单详情</title>
    <%
        boolean storageBinTag = ServiceManager.getService(IShopConfigService.class).isStorageBinSwitchOn((Long) request.getSession().getAttribute("shopId"));//选配仓位功能 默认开启这个功能false
    %>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/draftOrder<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/goodsSaleFinish<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/supplierLook<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/cuxiao<%=ConfigController.getBuildVersion()%>.css"/>
    <c:choose>
        <c:when test="<%=storageBinTag%>">
            <link rel="stylesheet" type="text/css" href="styles/storageBinOn<%=ConfigController.getBuildVersion()%>.css"/>
        </c:when>
        <c:otherwise>
            <link rel="stylesheet" type="text/css" href="styles/storageBinOff<%=ConfigController.getBuildVersion()%>.css"/>
        </c:otherwise>
    </c:choose>
    <link rel="stylesheet" type="text/css" href="styles/accept<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/yinshouyinfu<%=ConfigController.getBuildVersion()%>.css"/>

    <style type="text/css">
        .item input {
            text-overflow: clip;
        }
        .num_cont {
            color: #000000;
        }

    </style>
    <script type="text/javascript">
        var returnType = '${salesOrderDTO.returnType}';
        var returnIndex = '${salesOrderDTO.returnIndex}';
    </script>
    <%@include file="/WEB-INF/views/header_script.jsp" %>
    <script type="text/javascript" src="js/utils/tableUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/script<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/module/invoiceCommon<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/messagePrompt<%=ConfigController.getBuildVersion()%>.js"></script>
    <%--<script type="text/javascript" src="js/page/txn/goodsSale<%=ConfigController.getBuildVersion()%>.js"></script>--%>
    <script type="text/javascript" src="js/inventorySearchIndex<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/customerSearch<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/selectOptions<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/page/txn/txn<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.js"></script>
    <script type="text/javascript" src="js/checkMobileAndTelphone<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/page/txn/goodsSaleSolr<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/suggestion<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/detailsArrears<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/draftOrderBox<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/page/txn/goodsSaleFinish<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/statementAccount/statementAccountUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/page/autoaccessoryonline/promotionsUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/page/autoaccessoryonline/onlineSalesOrder<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">
        defaultStorage.setItem(storageKey.MenuUid, "WEB.SCHEDULE.REMIND_ORDERS.SALE");
        defaultStorage.setItem(storageKey.MenuCurrentItem,"详情");
        //单据操作日志
        $().ready(function(){
            var orderId = jQuery("#purchaseOrderId").val();
            APP_BCGOGO.Net.asyncPost({
                url: "txn.do?method=showOperationLog",
                data: {
                    orderId: orderId,
                    orderType: "purchase",
                    fromPage:"SALE_NEW_ORDER"
                },
                cache: false,
                dataType: "json",
                success: function(result) {
                    if(result && result.length>0){
                        for(var i = 0; i < result.length; i++) {
                            var creationDateStr = result[i].creationDateStr == null ? "" : result[i].creationDateStr;
                            var userName = result[i].userName == null ? "" : result[i].userName;
                            var content = result[i].content == null ? "" : result[i].content;
                            var tr = '<tr>';
                            tr += '<td style="border-left:none;padding-left:10px;"><span>' + (i + 1) + '&nbsp;</span></td>';
                            tr += '<td>' + creationDateStr + '</td>';
                            tr += '<td>' + userName + '</td>';
                            tr += '<td>' + content + '</td>';
                            $("#operationLog_tab").append($(tr));
                        }
                    }
                }
            });
        });

        var salesOrderId = '${salesOrderDTO.id}';
        var debt = '${salesOrderDTO.debt}';
        var customer = '${salesOrderDTO.customer}';
        var licenceNo = '${salesOrderDTO.licenceNo}';

        function detailsArrears() {
            var customerId = $("#customerId").val();
            toReceivableSettle(customerId);
        }

        //author:zhangjuntao
        var time = new Array(),timeFlag1 = true,timeFlag2 = true;
        time[0] = new Date().getTime();
        time[1] = new Date().getTime();
        time[2] = new Date().getTime();
        time[3] = new Date().getTime();
        var reg = /^\d+(\.{0,1}\d*)$/;

        $(document).ready(function() {

            $(".itemAmount,.itemTotal").bind("blur", function() {
                dataTransition.roundingSpanNumber("totalSpan");
                var format = $(this).val();
                format = dataTransition.rounding(format, 2);
                $(this).val(format);
            });
            $("#table_productNo input").bind("mouseover", function () {
                this.title = this.value;
            });

            $(document).click(function (e) {
                var e = e || event;
                var target = e.srcElement || e.target;
                if (target && typeof(target.id) == "string" && target.id.split(".")[1] != "productName" && target.id.split(".")[1] != "brand"
                        && target.id.split(".")[1] != "spec" && target.id.split(".")[1] != "model"
                        && target.id.split(".")[1] != "vehicleBrand" && target.id.split(".")[1] !=
                        "vehicleModel"
                        && target.id.split(".")[1] != "vehicleYear" && target.id.split(".")[1] !=
                        "vehicleEngine"
                        && target.id != "div_brand") {
                    $("#div_brand")[0].style.display = "none";
                }
            });

            jQuery("#goodsSaler").live("click", function(event) {
                droplistLite.show({
                    event:event,
                    hiddenId:"goodsSalerId",
                    id:"idStr",
                    name:"name",
                    data:"member.do?method=getSaleMans",
                    keyword:'name'
                });
            });

            jQuery(document).click(function(e) {
                var e = e || event;
                var target = e.srcElement || e.target;
                if (target.id != "div_serviceName" && target.id != "Scroller-Container_ServiceName") {
                    jQuery("#div_serviceName").hide();
                }
            });

            jQuery("#deleteGoodsSaler").hide();

            jQuery("#deleteGoodsSaler").live("click", function() {
                jQuery("#goodsSaler").val("");
                jQuery("#goodsSalerId").val("");
                jQuery("#deleteGoodsSaler").hide();
            });

            jQuery("#goodsSalerDiv").mouseenter(function() {
                if (jQuery("#goodsSaler").val() && !isDisable()) {
                    jQuery("#deleteGoodsSaler").show();
                }
            });

            jQuery("#goodsSalerDiv").mouseleave(function() {
                jQuery("#deleteGoodsSaler").hide();
            });

            if ($("#saleOrderMessage").val()) {
                if ($("#saleOrderMessage").attr("resultOperation") == "ALERT_SALE_LACK") {
                    nsDialog.jAlert($("#saleOrderMessage").val(), null, function () {
                    });
                } else if ($("#saleOrderMessage").attr("resultOperation") == "ALERT") {
                    nsDialog.jAlert($("#saleOrderMessage").val(), null, function () {
                    });
                }else if ($("#saleOrderMessage").attr("resultOperation") == "CONFIRM_ALLOCATE_RECORD") {
                    nsDialog.jConfirm($("#saleOrderMessage").val(), "确认仓库调拨提示", function (returnVal) {
                        if(returnVal){
                            window.open("allocateRecord.do?method=createAllocateRecordBySaleOrderId&salesOrderId=${salesOrderDTO.id}","_blank");
                        }
                    });
                }
            }

        });

        function addNewSaleOrder() {
            window.open($("#basePath").val() + "sale.do?method=getProducts&type=txn", "_blank");
        }
        defaultStorage.setItem(storageKey.MenuUid, "WEB.TXN.SALE_MANAGE.SALE");
    </script>
</head>

<body class="bodyMain">
<%@ include file="/WEB-INF/views/header_html.jsp" %>
<%@ include file="/common/messagePrompt.jsp" %>
<input type="hidden" id="basePath" name="bathPath" value="<%=basePath%>"/>

<div class="i_main clear">
<jsp:include page="../../txn/txnNavi.jsp">
    <jsp:param name="currPage" value="pendingPurchaseOrder"/>
</jsp:include>
</div>

<div class="i_mainRight" id="i_mainRight">
<jsp:include page="../../txn/unit.jsp"/>
<form:form commandName="salesOrderDTO" id="salesOrderForm" action=""  method="post" name="thisform">
<form:hidden path="purchaseOrderId" value="${purchaseOrderDTO.id}" cssClass="checkStringEmpty"/>
<form:hidden path="company" />
<form:hidden path="waybills" />
<form:hidden path="dispatchMemo" />
<form:hidden path="status" value="${salesOrderDTO.status}"/>
<c:if test="${!empty purchaseOrderDTO}">
    <input type="hidden" id="purchaseOrderDTOStatus" value="${purchaseOrderDTO.status}"/>
    <input type="hidden" class="J-orderId" value="${purchaseOrderDTO.id}"/>
</c:if>
<%--<form:hidden path="receivableId" value="${salesOrderDTO.receivableId}"/>--%>
<%--<form:hidden path="shopId" value="${sessionScope.shopId}"/>--%>
<%--<form:hidden path="draftOrderIdStr" value="${salesOrderDTO.draftOrderIdStr}"/>--%>
<%--<form:hidden path="statementAccountOrderId" value="${salesOrderDTO.statementAccountOrderId}"/>--%>
<c:if test="${!empty result}">
    <input type="hidden" id="saleOrderMessage" value="${result.msg}" resultDate="${result.data}"
           resultOperation="${result.operation}">
</c:if>
<input id="type" name="type" type="hidden" value="${param.type}"> <!-- 库存带过来的参数-->
<input id="orderType" name="orderType" value="goodsSaleOrder" type="hidden"/>
<input id="orderStatus" name="orderStatus" value="${purchaseOrderDTO.status}" type="hidden"/>

<input id="isMakeTime" type="hidden" value="0">
<input id="huankuanTime" type="hidden" value="" name="huankuanTime">
<input id="isAllMakeTime" type="hidden" value="0">
    <%--------------------欠款结算 zhouxiaochen 2011-12-14-------------------------------%>
<iframe id="iframe_qiankuan" style="position:absolute; left:0px; top:200px; display:none;"
        allowtransparency="true" width="1000px" height="600px" frameborder="0" src="">
</iframe>
    <%--------------------End 欠款结算 zhouxiaochen 2011-12-14----------------------------%>
<div class="tableInfo">

<ul class="yinye_title clear">
    <li class="danju" style="width: 200px;">
        对方采购单号： <strong>${purchaseOrderDTO.receiptNo}</strong>
    </li>
    <li class="danju" style="text-align: left;width: 50px">
        <a class="connect">在线</a>
    </li>
    <li class="danju">
        单据状态：
        <span id="orderStatusStr"> <strong>${salesOrderDTO.status.name}</strong></span>
        <input type="hidden" id="customerId" name="customerId" value="${customerDTO.id}"/>
    </li>
    <li class="danju" style="width: 220px">
        下单时间：<span style="color:#787777;font-weight: normal;">${purchaseOrderDTO.vestDateStr}</span>
    </li>
</ul>
<div class="clear"></div>
<div class="tuihuo_tb">
<table>
    <col width="80"/>
    <tr>
        <td>客户信息</td>
    </tr>
</table>
<table class="clear tb_tui">
    <col width="80"/>
    <col width="200"/>
    <col width="150"/>
    <col width="150"/>
    <col width="120"/>
    <col width="180"/>
    <col/>
    <tr>
        <td class="td_title">客户名</td>
        <td>
            <span id="customerName" style="width:160px;float:left;margin-left:5px;" class="ellipsis">${customerDTO.name}</span>
            <a target="_blank" class="icon_online_shop" href="shopMsgDetail.do?method=renderShopMsgDetail&paramShopId=${customerDTO.customerShopId}"></a>
            <a class="J_QQ" data_qq="${customerDTO.qqArray}"></a>
        </td>
        <td class="td_title">联系人</td>
        <td>${customerDTO.contact}</td>
        <td class="td_title">联系电话</td>
        <td><span id="customerMobile">${customerDTO.mobile}</span></td>
    </tr>
    <tr>
        <td class="td_title">地址</td>
        <td colspan="3">${customerDTO.address}</td>
        <td class="td_title">当前应收应付</td>
        <td class="qian_red">
            <div class="pay" id="duizhan" style="text-align:left;margin-left: 25px">
                <c:choose>
                    <c:when test="${customerRecordDTO.totalReceivable >0}">
                        <span class="red_color payMoney">收¥${customerRecordDTO.totalReceivable}</span>
                    </c:when>
                    <c:otherwise>
                        <span class="gray_color fuMoney">收¥0</span>
                    </c:otherwise>
                </c:choose>
                <c:choose>
                    <c:when test="${customerRecordDTO.totalPayable >0}">
                        <span class="green_color fuMoney">付¥${customerRecordDTO.totalPayable}</span>
                    </c:when>
                    <c:otherwise>
                        <span class="gray_color fuMoney">付¥0</span>
                    </c:otherwise>
                </c:choose>
            </div>
        </td>
    </tr>
</table>

<div style="margin-top: 10px">
    销售信息
    <span>买家期望交货:${purchaseOrderDTO.deliveryDateStr}  </span>
    <bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.STOREHOUSE">
    <bcgogo:permission>
        <bcgogo:if resourceType="logic" permissions="WEB.VERSION.PRODUCT.THROUGH_SELECT_SUPPLIER">
                <span>发货仓库：
                <form:select path="storehouseId" cssClass="j_checkStoreHouse checkSelectChanged" cssStyle="width:150px;height:21px;border: 1px solid #BBBBBB;" autocomplete="off">
                    <option value="">—请选择仓库—</option>
                    <form:options items="${storeHouseDTOList}" itemValue="id" itemLabel="name"/>
                </form:select>
                </span>
        </bcgogo:if>
        <bcgogo:else>
                <span>发货仓库：
                <form:select path="storehouseId" cssClass="j_checkStoreHouse checkSelectChanged" cssStyle="width:150px;height:21px;border: 1px solid #BBBBBB;" autocomplete="off">
                    <option value="">—请选择仓库—</option>
                    <form:options items="${storeHouseDTOList}" itemValue="id" itemLabel="name"/>
                </form:select>
                </span>
        </bcgogo:else>
    </bcgogo:permission>
    </bcgogo:hasPermission>
    <a id="showOperationLog" href="javascript:showOperationLog()">查看操作记录</a>
</div>
<div style="width: 100%;margin: 10px 10px">
    <div class="cartTop" style="width: 984px"></div>
    <div class="cartBody" style="width: 985px">
        <table class="clear table2" style="width:98%" id="table_productNo">
            <col width="75"/>
            <col width="400"/>
            <col width="80">
            <col width="80"/>
            <col width="120"/>
            <bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.PRODUCT.THROUGH_SELECT_SUPPLIER">
               <col width="80"/>
            </bcgogo:hasPermission>
            <col width="80"/>
            <col width="95"/>
            <tr class="titleBg">
                <td style="padding-left:5px;">商品编号</td>
                <td>商品信息</td>
                <td>上架价格</td>
                <td>成交价</td>
                <td>买家采购量</td>
                <bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.PRODUCT.THROUGH_SELECT_SUPPLIER">
                   <td>本店出货量</td>
                </bcgogo:hasPermission>
                <td>单位</td>
                <td>总额</td>
            </tr>
            <tr class="space">
                <td colspan="15" style="height: 8px"></td>
            </tr>
            <c:forEach items="${salesOrderDTO.itemDTOs}" var="itemDTO" varStatus="status">
                <c:if test="${itemDTO!=null}">
                    <tr class="bg item">
                        <td>
                                ${itemDTO.commodityCode!=null?itemDTO.commodityCode:""}
                            <form:hidden path="itemDTOs[${status.index}].commodityCode" value='${itemDTO.commodityCode!=null?itemDTO.commodityCode:""}'/>
                            <form:hidden path="itemDTOs[${status.index}].productId" value="${itemDTO.productId}"/>
                            <input type="hidden" id="itemDTOs${status.index}.itemId" value="${itemDTO.idStr}"/>
                        </td>
                        <td>${itemDTO.productInfo}</td>
                        <td>
                            <span class='${itemDTO.price!=itemDTO.quotedPrice?"oldPrice":""}'><fmt:formatNumber value="${itemDTO.quotedPrice}" pattern="#.##"/></span>
                        </td>

                        <c:choose>
                            <c:when test="${itemDTO.quotedPreBuyOrderItemId!=null}">
                                <td class="quotedPreBuyPrice"><span style="color:#FF6700;"><fmt:formatNumber value="${itemDTO.price}" pattern="#.##"/></span></td>
                            </c:when>
                            <c:when test="${!itemDTO.customPriceFlag && not empty itemDTO.promotionsId && itemDTO.quotedPreBuyOrderItemId==null}">
                                <td class="promotionPrice J-priceInfo">
                                    <span class="clearfix">
                                    <em class="cuxi">
                                        <span style="color:#FF6700;"><fmt:formatNumber value="${itemDTO.price}" pattern="#.##"/></span>
                                    </em>
                                    <div class="alert" style="display: none">
                                        <div class="ti_top"></div>
                                        <div class="ti_body alertBody">
                                        </div>
                                        <div class="ti_bottom"></div>
                                    </div>
                                    </span>
                                </td>
                            </c:when>
                            <c:when test="${itemDTO.customPriceFlag && itemDTO.price!=itemDTO.quotedPrice && itemDTO.quotedPreBuyOrderItemId==null}">
                                <td class="expectPrice J-priceInfo">
                                    <span class="clearfix">
                                    <em class="cuxi">
                                        <span style="color:#FF6700;"><fmt:formatNumber value="${itemDTO.price}" pattern="#.##"/></span>
                                    </em>
                                     <div class="alert" style="overflow: visible;display: none;">
                                         对方期望价
                                     </div>
                                    </span>
                                </td>
                            </c:when>
                            <c:otherwise>
                                <td style="color:#FF6700;"><fmt:formatNumber value="${itemDTO.price}" pattern="#.##"/></td>
                            </c:otherwise>
                        </c:choose>
                        <td style="color:red;">
                            <span id="itemDTOs${status.index}.pAmount">${itemDTO.purchaseAmount}</span>
                        </td>
                        <bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.PRODUCT.THROUGH_SELECT_SUPPLIER">
                                <td>
                                    <form:input path="itemDTOs[${status.index}].amount" value="0"
                                                class="itemAmount table_input checkNumberEmpty txtHover"/>
                                </td>
                        </bcgogo:hasPermission>
                        <td>${itemDTO.unit}</td>
                        <td style="color:#6D8FB9;"><fmt:formatNumber value="${itemDTO.total}" pattern="#.##"/></td>
                    </tr>
                </c:if>
            </c:forEach>

        </table>
        <div  class="t_total">
            <c:if test="${not empty purchaseOrderDTO.promotionsInfoDTO}">
            <div class="orderPromotionsDetail" style="float:right">
                <div class="infoName" style="font-size: 13">优惠明细：</div>
                <c:if test="${not empty purchaseOrderDTO.promotionsInfoDTO.BARGAIN}">
                <div class="bargainDiv">
                    <span>特价商品：</span>
                    优惠<span class="arialFont">&yen;</span> <span class="promotions_total" id="bargainSpan">${purchaseOrderDTO.promotionsInfoDTO.BARGAIN}</span>
                </div>
                </c:if>
                <c:if test="${not empty purchaseOrderDTO.promotionsInfoDTO.MLJ}">
                <div class="mljDiv">
                    <span>满立减：</span>
                    优惠<span class="arialFont">&yen;</span> <span class="promotions_total" id="mljSpan">${purchaseOrderDTO.promotionsInfoDTO.MLJ}</span>
                </div>
                </c:if>
                <c:if test="${not empty purchaseOrderDTO.promotionsInfoDTO.MJS}">
                <div class="mjsDiv">
                    <span>满就送：</span>
                    <span id="mjsSpan">${purchaseOrderDTO.promotionsInfoDTO.MJS}</span>
                </div>
                </c:if>
                <c:if test="${not empty purchaseOrderDTO.promotionsInfoDTO.FREE_SHIPPING}">
                <div class="freeShippingDiv">
                    <span id="freeShipping">送货上门</span>
                </div>
                </c:if>
                <b class="almost">
                    优惠总计:<span class="arialFont">&yen;</span>
                    <span class="green_color" id="promotionsTotalSpan">${purchaseOrderDTO.promotionsInfoDTO.promotionsTotal}</span> 元
                </b>
            </div>
            </c:if>
            <div class="clear"/>
            <div style="float:right">
            合计：
            <span id="totalSpan">${purchaseOrderDTO.total}</span>
            </div>
        </div>
            <%--<div class="clear" style="height: 10px"></div>--%>
        <div class="clear"  style="margin-top: 10px;margin-bottom: 10px;padding-left: 20px;">
            买家采购备注：<span class="sale-memo">${salesOrderDTO.purchaseMemo}</span>
        </div>
    </div>
</div>
<div class="height"></div>
<c:if test="${!(salesOrderDTO.status eq 'STOP') && !(salesOrderDTO.status eq 'REFUSED')}">
    <div style="margin-left: 10px">
   <span id="goodsSalerDiv">销售人：
                      <span id="goodsSalerSpan">
                      <input name="goodsSaler" id="goodsSaler" value="${salesOrderDTO.goodsSaler}" readOnly="true"
                             initgoodssalervalue="${salesOrderDTO.goodsSaler}"
                             style="width: 70px;" cssClass="checkStringChanged" class="textbox"/>
                      <img src="images/list_close.png" id="deleteGoodsSaler" style="width:12px;cursor:pointer">
                      <input type="hidden" id="goodsSalerId" name="goodsSalerId" value="${salesOrderDTO.goodsSalerId}"
                             initgoodssalervalue="${salesOrderDTO.goodsSalerId}"/>
                      </span>
   </span>
        <span>销售日期：${salesOrderDTO.vestDateStr}</span>
        预计交货日期：
        <input type="radio" name="dispatchDateRadio" value="today" checked="checked"/>&nbsp;本日&nbsp;&nbsp;
        <input type="radio" name="dispatchDateRadio" value="tomorrow"/>&nbsp;明日&nbsp;&nbsp;
        <input type="radio" name="dispatchDateRadio" value="innerThreeDays"/>&nbsp;三天内&nbsp;&nbsp;
        <input type="radio" name="dispatchDateRadio" value="definedDate"/>&nbsp;自定义日期&nbsp;
        <input id="definedDate" type="text" class="txt" style="width:75px;" readonly="readonly"/>
        <input id="dispatchDate" type="hidden" name="preDispatchDateStr"/>
        <input type="hidden" id="goodsSaler_hidden" name="goodsSaler">
            <%--<input type="hidden" id="goodsSalerId_hidden" name="goodsSalerId">--%>
    </div>
</c:if>
<div class="height"></div>
<table class="clear tb_tui">
    <col width="75"/>
    <col/>
            <tr style="width:300px;">
                <td class="td_title" style="text-align: center; height: 60px;">备注：</td>
                <td>
                    <textarea class="textarea" id="acceptMemo" style="width: 98%; height: 45px; color: #666666;"
                              maxlength="100" initialvalue="请输入销售备注..."
                              initialvalueColor="#666666">请输入销售备注...</textarea>
                    <input type="hidden" id="acceptMemo_hidden" name="acceptMemo">

                </td>
            </tr>
</table>
<c:if test="${'SELLER_REFUSED' eq purchaseOrderDTO.status && !empty purchaseOrderDTO.refuseMsg}">
    <div class="height"></div>
    <table class="clear tb_tui">
        <col width="75"/>
        <col/>
        <tr style="width:300px;">
            <td class="td_title">拒绝理由：</td>
            <td><span class="sale-memo">${purchaseOrderDTO.refuseMsg}</span></td>
        </tr>
    </table>
</c:if>
</form:form>

<div class="height"></div>
</div>
<div class="clear"></div>
<div class="shopping_btn" style="float:right; clear:right;margin-top: 20px;">
    <bcgogo:hasPermission permissions="WEB.TXN.SALE_MANAGE.SALE" resourceType="menu">
        <c:if test="${!(salesOrderDTO.status eq 'STOP') && !(salesOrderDTO.status eq 'REFUSED')}">
            <div class="btn_div_Img" id="accept_div">
                <input id="acceptBtn" type="button" class="acceptBtn j_btn_i_operate" onfocus="this.blur();"/>

                <div class="optWords">接受订单</div>
            </div>
            <div class="btn_div_Img" id="refuse_div" onclick="refuseSale()">
                <input id="refuseBtn" type="button" class="refuseBtn j_btn_i_operate"
                       onfocus="this.blur();"/>

                <div class="optWords">拒绝订单</div>
            </div>
        </c:if>
    </bcgogo:hasPermission>

    <bcgogo:hasPermission permissions="WEB.TXN.SALES_MANAGE.PRINT">
        <div class="btn_div_Img" id="print_pending_purchaseOrder">
            <input type="button" class="print" onfocus="this.blur();"/>
            <input type="hidden" name="print" id="print" value="${salesOrderDTO.print}">

            <div class="optWords">打印</div>
        </div>
    </bcgogo:hasPermission>

</div>
</div>
</div>

<div id="div_brand" class="i_scroll" style="display:none;">
    <div class="Scroller-Container" id="Scroller-Container_id">
    </div>
</div>
<%--缓存更多客户信息--%>
<input type="hidden" id="hidName"/>
<input type="hidden" id="hidShortName"/>
<input type="hidden" id="hidAddress"/>
<input type="hidden" id="hidContact"/>
<input type="hidden" id="hidMobile"/>
<input type="hidden" id="hidPhone"/>
<input type="hidden" id="hidFax"/>
<input type="hidden" id="hidMemberNumber"/>
<input type="hidden" id="hidBirthdayString"/>
<input type="hidden" id="hidQQ"/>
<input type="hidden" id="hidEmail"/>
<input type="hidden" id="hidBank"/>
<input type="hidden" id="hidBankAccountName"/>
<input type="hidden" id="hidAccount"/>
<%----%>
</div>
<div class="zuofei" id="zuofei"></div>
<div id="mask" style="display:block;position: absolute;">
</div>

<iframe id="iframe_PopupBox_account" style="position:absolute;z-index:5; left:300px; top:300px; display:none;"
        allowtransparency="true" width="900px" height="450px" frameborder="0" src=""></iframe>
<iframe id="iframe_PopupBox" style="position:absolute;z-index:5; left:200px; top:400px; display:none;"
        allowtransparency="true" width="1000px" height="1000px" scrolling="no" frameborder="0" src=""></iframe>
<iframe id="iframe_PopupBox_2"
        style="position:absolute;z-index:6;top:210px;left:87px;display:none;  background: none repeat scroll;"
        allowtransparency="true" width="1000px" height="650px" frameborder="0" src="" scrolling="no"></iframe>
<iframe id="iframe_PopupBoxMakeTime" style="position:absolute;z-index:8;top:210px;left:87px;display:none; "
        allowtransparency="true" width="350px" height="500px" frameborder="0" src="" scrolling="no"></iframe>
<div id="isInvo"></div>

<div id="commodityCode_dialog" style="display:none">
    <span id="commodityCode_dialog_msg"></span>
</div>

<!-- 客户商下拉菜单 zhangchuanlong-->
<div id="div_brandCustomer" class="i_scroll" style="display:none;width:300px;">
    <div class="Container" style="width:300px;">
        <div id="Scroller-1licenceNo1" style="width:300px;">
            <div class="Scroller-ContainerSupplier" id="Scroller-Container_idCustomer">
            </div>
        </div>
    </div>
</div>
<div id="div_serviceName" class="i_scroll" style="display:none;width:150px;">
    <div class="Scroller-Container" id="Scroller-Container_ServiceName">
    </div>
</div>
<!-- 搜索下拉, TODO 以后移到组件里 -->
<div id="id-searchcomplete"></div>
<div id="id-searchcompleteMultiselect"></div>
<div id="dialog-confirm" title="提醒" style="display: none">
    <p><span class="ui-icon ui-icon-alert" style="float:left; margin:0 7px 20px 0;"></span>

    <div id="dialog-confirm-text"></div>
    </p>
</div>

<div id="newSettlePage" style="display:none"></div>

<div class="i_searchBrand" id="refuseReasonDialog" title="确认拒绝提示" style="display:none; width: 500px;">
    <h3>友情提示：您拒绝后，该采购请求将被驳回 ！</h3>

    <h3>您确定要拒绝该销售单吗？</h3>

    <form id="salesRefuseForm" method="post" action="sale.do?method=refuseSaleOrder">
        <table border="0" width="480">
            <tr>
                <td width="100" align="right"><img src="images/star.jpg">拒绝理由：</td>
                <td>
                    <textarea class="textarea" id="refuseMsg" name="refuseMsg" style="width:320px;" maxlength="500"></textarea>
                    <input type="hidden" id="saleMemo_hidden" name="saleMemo">
                </td>
            </tr>
            <tr>
                <td colspan="2">
                    <div class="btnClick" style="height:50px; line-height:50px">
                        <input type="hidden" id="refuse_id" value="${salesOrderDTO.id}" name="id"/>
                        <input type="hidden" name="purchaseOrderId" value="${salesOrderDTO.purchaseOrderId}" />
                        <input type="button" id="refuseConfirmBtn" onfocus="this.blur();" value="确&nbsp;定">
                        <input type="button" id="refuseCancelBtn" onfocus="this.blur();" value="取&nbsp;消">
                    </div>
                </td>
            </tr>
        </table>
    </form>
</div>
<!-- 操作日志弹出框 -->
<div class="i_searchBrand" id="showOperationLog_div" title="查看单据操作记录" style="display:none; width: 500px;">
    <table id="operationLog_tab" border="0" width="480">
        <tr style="background-color:#E9E9E9; height:16px; ">
            <td class="tab_first">NO</td>
            <td>操作时间</td>
            <td>操作者</td>
            <td>操作内容</td>
        </tr>
    </table>
</div>
<bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.STOREHOUSE">
    <jsp:include page="../../txn/common/selectStoreHouse.jsp"/>
</bcgogo:hasPermission>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>