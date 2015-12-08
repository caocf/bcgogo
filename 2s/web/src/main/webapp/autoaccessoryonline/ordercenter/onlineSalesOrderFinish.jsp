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
    <script type="text/javascript" src="js/page/autoaccessoryonline/onlineSalesOrder<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">
        defaultStorage.setItem(storageKey.MenuUid, "WEB.SCHEDULE.REMIND_ORDERS.SALE");
        defaultStorage.setItem(storageKey.MenuCurrentItem,"详情");
        //单据操作日志
        $().ready(function(){
            var orderId = jQuery("#id").val();
            APP_BCGOGO.Net.asyncPost({
                url: "txn.do?method=showOperationLog",
                data: {
                    orderId: orderId,
                    orderType: "sale"
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
    <jsp:param name="currPage" value="goodsSale"/>
</jsp:include>
<jsp:include page="../../txn/saleNavi.jsp">
    <jsp:param name="currPage" value="goodsSale"/>
</jsp:include>

<div class="i_mainRight" id="i_mainRight">
<jsp:include page="../../txn/unit.jsp"/>
<input type="hidden" id="salesOrderId" value="${salesOrderDTO.id}"/>
<form:form commandName="salesOrderDTO" id="salesOrderForm" action=""  method="post" name="thisform">
<form:hidden path="id" value="${salesOrderDTO.id}" cssClass="checkStringEmpty"/>
<form:hidden path="status" value="${salesOrderDTO.status}"/>
<c:if test="${!empty purchaseOrderDTO}">
    <input type="hidden" id="purchaseOrderDTOStatus" value="${purchaseOrderDTO.status}"/>
</c:if>
<form:hidden path="receivableId" value="${salesOrderDTO.receivableId}"/>
<form:hidden path="shopId" value="${sessionScope.shopId}"/>
<form:hidden path="draftOrderIdStr" value="${salesOrderDTO.draftOrderIdStr}"/>
<form:hidden path="statementAccountOrderId" value="${salesOrderDTO.statementAccountOrderId}"/>
<c:if test="${!empty result}">
    <input type="hidden" id="saleOrderMessage" value="${result.msg}" resultDate="${result.data}"
           resultOperation="${result.operation}">
</c:if>
<input id="type" name="type" type="hidden" value="${param.type}"> <!-- 库存带过来的参数-->
<input id="orderType" name="orderType" value="goodsSaleOrder" type="hidden"/>

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
    <li class="danju">
        单据号： <strong>${salesOrderDTO.receiptNo}</strong>
    </li>
    <li class="danju" style="text-align: left;width: 50px">
        <a class="connect">在线</a>
    </li>
    <li class="danju">
        单据状态：
        <span id="orderStatusStr">
        <c:choose>
            <c:when test="${salesOrderDTO.status=='SALE_DONE' && salesOrderDTO.debt>0}">
                <strong>欠款结算</strong>
            </c:when>
            <c:otherwise>
                <strong>${salesOrderDTO.status.name}</strong>
            </c:otherwise>
        </c:choose>
        </span>

        <input type="hidden" id="customerId" name="customerId" value="${salesOrderDTO.customerId}"/>
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
        <td><span id="customerName">${salesOrderDTO.customer}</span></td>
        <td class="td_title">联系人</td>
        <td>${salesOrderDTO.contact}</td>
        <td class="td_title">联系电话</td>
        <td><span id="customerMobile">${salesOrderDTO.mobile}</span></td>
    </tr>
    <tr>
        <td class="td_title">地址</td>
        <td colspan="3">${salesOrderDTO.address}</td>
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


<div style="width: 100%;margin: 10px 10px">
    <div class="cartTop" style="width: 984px"></div>
    <div class="cartBody" style="width: 985px">
        <table class="clear table2" style="width:98%" id="table_productNo">
            <col width="75"/>
            <col width="400"/>
            <col width="80">
            <col width="80"/>
            <col width="80"/>
            <col width="80"/>
            <col width="80"/>
            <col width="95"/>
            <col width="95"/>
            <col width="95"/>
            <tr class="titleBg">
                <td style="padding-left:5px;">商品编号</td>
                <td>商品信息</td>
                <td>上架价格</td>
                <td>成交价</td>
                <td>买家采购量</td>
                <td>本店出货量</td>
                <td>单位</td>
                <td>营业分类</td>
                <td>货位</td>
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
                        </td>
                        <td>${itemDTO.productInfo}</td>
                        <c:choose>
                            <c:when test="${salesOrderDTO.purchaseOrderId!=null}">
                                <td>
                                    <span class='${itemDTO.price!=itemDTO.quotedPrice?"oldPrice":""}'><fmt:formatNumber value="${itemDTO.quotedPrice}" pattern="#.##"/></span>
                                </td>
                                <c:choose>
                                    <c:when test="${itemDTO.quotedPreBuyOrderItemId!=null}">
                                        <td class="quotedPreBuyPrice"><span style="color:#FF6700;"><fmt:formatNumber value="${itemDTO.price}" pattern="#.##"/></span></td>
                                    </c:when>
                                    <c:when test="${itemDTO.price!=itemDTO.quotedPrice && itemDTO.promotionsDTO!=null && itemDTO.quotedPreBuyOrderItemId==null}">
                                        <td class="promotionPrice J-priceInfo">
                                            <span class="clearfix">
                                            <em class="cuxi">
                                                <span style="color:#FF6700;"><fmt:formatNumber value="${itemDTO.price}" pattern="#.##"/></span>
                                            </em>
                                            <div class="alert" style="display: none">
                                                <div class="ti_top"></div>
                                                <div class="ti_body alertBody">
                                                    <div style="font-weight:bold;">活动截止：<span style="color: #FF5113;">${itemDTO.promotionsDTO.endTimeCNStr==null?"不限期":itemDTO.promotionsDTO.endTimeCNStr}</span>
                                                    </div>
                                                    <c:forEach items="${itemDTO.promotionsDTO.promotionsRuleDTOList}" var="promotionsRuleDTO">
                                                        <div>
                                                            满
                                                            <span class="green_color">${promotionsRuleDTO.minAmountStr}</span>件，单价打
                                                            <span style="color: #CB0000;">${promotionsRuleDTO.discountAmountStr}</span>折
                                                        </div>
                                                    </c:forEach>
                                                </div>
                                                <div class="ti_bottom"></div>
                                            </div>
                                            </span>
                                        </td>
                                    </c:when>
                                    <c:when test="${itemDTO.price!=itemDTO.quotedPrice && itemDTO.promotionsDTO==null && itemDTO.quotedPreBuyOrderItemId==null}">
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
                            </c:when>
                            <c:otherwise>
                                <td style="color:#FF6700;"><fmt:formatNumber value="${itemDTO.price}" pattern="#.##"/></td>
                            </c:otherwise>
                        </c:choose>

                        <td>
                            <form:input path="itemDTOs[${status.index}].amount" value="0"
                                        class="itemAmount table_input checkNumberEmpty txtHover"/>
                        </td>
                        <td>${itemDTO.unit}</td>
                        <td>${itemDTO.businessCategoryName}</td>
                        <td class="storage_bin_td">${itemDTO.storageBin}</td>
                        <td style="color:#6D8FB9;"><fmt:formatNumber value="${itemDTO.total}" pattern="#.##"/></td>
                    </tr>
                </c:if>
            </c:forEach>

        </table>
        <div  class="t_total">合计：
            <span id="totalSpan">${salesOrderDTO.itemTotal}</span>
        </div>
            <%--<div class="clear" style="height: 10px"></div>--%>
        <div class="clear"  style="margin-top: 10px;margin-bottom: 10px;padding-left: 20px;">
            买家采购备注：<span class="sale-memo">${salesOrderDTO.purchaseMemo}</span>
        </div>
    </div>
</div>
<div class="height"></div>

<c:if test="${salesOrderDTO.status eq 'PENDING'}">

</c:if>

<div class="height"></div>
<table class="clear tb_tui">
    <col width="75"/>
    <col/>
    <c:choose>
        <c:when test="${salesOrderDTO.status eq 'PENDING'}">
            <tr style="width:300px;">
                <td class="td_title" style="text-align: center; height: 60px;">备注：</td>
                <td>
                    <textarea class="textarea" id="acceptMemo" style="width: 98%; height: 45px; color: #666666;"
                              maxlength="100" initialvalue="请输入销售备注..."
                              initialvalueColor="#666666">请输入销售备注...</textarea>
                    <input type="hidden" id="acceptMemo_hidden" name="acceptMemo">

                </td>
            </tr>
        </c:when>
        <c:when test="${salesOrderDTO.status ne 'PENDING'}">
            <tr style="width:300px;">
                <td class="td_title">备注：</td>
                <td><span class="sale-memo">${salesOrderDTO.memo}</span></td>
            </tr>
        </c:when>
    </c:choose>
</table>

<c:if test="${!empty salesOrderDTO.purchaseOrderId && !empty salesOrderDTO.refuseMsg}">
    <div class="height"></div>
    <table class="clear tb_tui">
        <col width="75"/>
        <col/>
        <tr style="width:300px;">
            <td class="td_title">拒绝理由：</td>
            <td><span class="sale-memo">${salesOrderDTO.refuseMsg}</span></td>
        </tr>
    </table>
</c:if>

<c:if test="${!empty salesOrderDTO.purchaseOrderId && !empty salesOrderDTO.repealMsg}">
    <div class="height"></div>
    <table class="clear tb_tui">
        <col width="75"/>
        <col/>
        <tr style="width:300px;">
            <td class="td_title">终止理由：</td>
            <td><span class="sale-memo">${salesOrderDTO.repealMsg}</span></td>
        </tr>
    </table>
</c:if>


<c:choose>
    <c:when test="${!empty salesOrderDTO.purchaseOrderId and (!empty salesOrderDTO.company or !empty salesOrderDTO.waybills
    or !empty salesOrderDTO.dispatchMemo)}">
        <table>
            <col width="80"/>
            <tr>
                <td>物流信息</td>
            </tr>
        </table>
        <table class="clear tb_tui">
            <col width="98"/>
            <col width="400"/>
            <col width="85"/>
            <col width="400"/>
            <tr>
                <td class="td_title">物流公司：</td>
                <td>${salesOrderDTO.company} </td>
                <td class="td_title">运单号：</td>
                <td>${salesOrderDTO.waybills} </td>
            </tr>
            <tr>
                <td class="td_title">物流备注：</td>
                <td colspan="3"><span class="sale-memo">${salesOrderDTO.dispatchMemo}</span></td>
            </tr>
        </table>
    </c:when>
</c:choose>
</form:form>

<div class="height"></div>
</div>
<div class="clear"></div>
<div class="shopping_btn" style="float:right; clear:right;">

    <bcgogo:hasPermission permissions="WEB.TXN.SALE_MANAGE.SALE"  resourceType="menu">
        <c:if test="${salesOrderDTO.status=='DISPATCH'}">
            <div class="btn_div_Img" id="saleSave_div">
                <input id="supplierSalesAccount" type="button" class="saleAccount j_btn_i_operate"
                       onfocus="this.blur();"/>

                <div class="optWords">结算</div>
            </div>
        </c:if>
        <bcgogo:hasPermission permissions="WEB.CUSTOMER_MANAGER.CUSTOMER_DATA.ARREARS">
            <c:if test="${salesOrderDTO.status=='SALE_DONE' && salesOrderDTO.debt>0}">
                <div class="btn_div_Img" id="saleDebtSave_div">
                    <input id="supplierSalesDebtAccount" type="button" class="saleAccount j_btn_i_operate"
                           onclick="toReceivableSettle('${salesOrderDTO.customerId}','goodsSaleOrder')"
                           onfocus="this.blur();"/>

                    <div class="optWords">欠款结算</div>
                </div>
            </c:if>
        </bcgogo:hasPermission>
    </bcgogo:hasPermission>

    <bcgogo:hasPermission permissions="WEB.TXN.SALES_MANAGE.PRINT">
        <div class="btn_div_Img" id="print_div">
            <input id="printBtn" type="button" class="print j_btn_i_operate" onfocus="this.blur();"/>
            <input type="hidden" name="print" id="print" value="${salesOrderDTO.print}">

            <div class="optWords">打印</div>
        </div>
    </bcgogo:hasPermission>

</div>

<bcgogo:hasPermission permissions="WEB.TXN.SALE_MANAGE.SALE.CANCEL">
    <c:if test="${ salesOrderDTO.statementAccountOrderId == null}">
        <div class="invalidImg" id="invalid_div">
            <input id="nullifyBtn" type="button" onfocus="this.blur();"/>
            <div class="invalidWords" id="invalidWords">作废</div>
        </div>
    </c:if>
    <c:if test="${(salesOrderDTO.status eq 'STOCKING' or salesOrderDTO.status eq 'DISPATCH') and !empty salesOrderDTO.purchaseOrderId}">
        <div class="stopSaleImg" id="stopSale_div" onclick="stopSale()">
            <input id="stopSaleBtn" type="button" onfocus="this.blur();"/>

            <div class="invalidWords">终止销售</div>
        </div>
    </c:if>
</bcgogo:hasPermission>
<bcgogo:hasPermission permissions="WEB.TXN.SALE_MANAGE.SALE" resourceType="menu">
    <c:if test="${salesOrderDTO.status eq 'SALE_DONE' || salesOrderDTO.status eq 'SALE_DEBT_DONE' || salesOrderDTO.status eq 'SALE_REPEAL' }">
        <div class="copyInput_div" id="copyInput_div">
            <input id="copyInput" type="button" onfocus="this.blur();"/>

            <div class="copyInput_text_div" id="copyInput_text">复制</div>
        </div>
    </c:if>
</bcgogo:hasPermission>
<bcgogo:hasPermission permissions="WEB.TXN.SALE_MANAGE.RETURN">
    <c:if test="${salesOrderDTO.status eq 'SALE_DONE' || salesOrderDTO.status eq 'SALE_DEBT_DONE'}">
        <div class="salesReturn_div" id="salesReturnDiv">
            <input id="salesReturn" type="button" onfocus="this.blur();"/>

            <div class="salesReturn_text_div" id="createSalesReturn">退货</div>
        </div>
    </c:if>
</bcgogo:hasPermission>

</div>
<%--</form:form>--%>
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

<iframe id="iframe_PopupBox_account" style="position:absolute;z-index:5; left:500px; top:300px; display:none;"
        allowtransparency="true" width="900px" height="450px" frameborder="0" src=""></iframe>
<iframe id="iframe_PopupBox" style="position:absolute;z-index:5; left:200px; top:400px; display:none;"
        allowtransparency="true" width="1000px" height="1000px" scrolling="no" frameborder="0" src=""></iframe>
<iframe id="iframe_PopupBox_2"
        style="position:absolute;z-index:6;top:210px;left:87px;display:none;  background: none repeat scroll;"
        allowtransparency="true" width="1000px" height="650px" frameborder="0" src="" scrolling="no"></iframe>
<iframe id="iframe_PopupBoxMakeTime" style="position:absolute;z-index:8;top:210px;left:87px;display:none; "
        allowtransparency="true" width="350px" height="500px" frameborder="0" src="" scrolling="no"></iframe>
<div id="isInvo"></div>

<div id="draftOrder_dialog" style="display:none">
    <div class="i_draft_table">
        <table cellpadding="0" cellspacing="0" class="i_draft_table_box" id="draft_table">
            <col>
            <col width="50">
            <col width="220">
            <col width="220">
            <col width="400">
            <col>
            <tr class="tab_title">
                <td class="tab_first"></td>
                <td>No</td>
                <td>保存时间</td>
                <td>客户</td>
                <td>销售商品</td>
                <td class="tab_last"></td>
            </tr>
        </table>
        <!--分页-->
        <div class="hidePageAJAX">
            <jsp:include page="/common/pageAJAX.jsp">
                <jsp:param name="url" value="draft.do?method=getDraftOrders"></jsp:param>
                <jsp:param name="data" value="{startPageNo:1,orderTypes:'SALE'}"></jsp:param>
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
<input type="hidden" id="goodSalePage" value="1"/>

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
                        <input type="button" id="refuseConfirmBtn" onfocus="this.blur();" value="确&nbsp;定">
                        <input type="button" id="refuseCancelBtn" onfocus="this.blur();" value="取&nbsp;消">
                    </div>
                </td>
            </tr>
        </table>
    </form>
</div>
<div class="i_searchBrand" id="repealReasonDialog" title="确认终止销售提示" style="display:none; width: 500px;">
    <h3>友情提示：销售单终止销售后，将不再有效，交易会被取消 ！</h3>

    <h3>您确定要终止该销售单吗？</h3>

    <form id="salesRepealForm" method="post" action="sale.do?method=saleOrderRepeal">
        <table border="0" width="480">
            <tr>
                <td width="100" align="right"><img src="images/star.jpg">终止销售理由：</td>
                <td><textarea class="textarea" id="repealMsg" name="repealMsg" style="width:320px;"
                              maxlength="500"></textarea></td>
            </tr>
            <tr>
                <td colspan="2">
                    <div class="btnClick" style="height:50px; line-height:50px">
                        <input type="hidden" id="repeal_id" name="salesOrderId" value="${salesOrderDTO.id}"/>
                        <input type="button" id="repealConfirmBtn" onfocus="this.blur();" value="确&nbsp;定">
                        <input type="button" id="repealCancelBtn" onfocus="this.blur();" value="取&nbsp;消">
                    </div>
                </td>
            </tr>
        </table>
    </form>
</div>
<div class="i_searchBrand" id="dispatchDialog" title="确认发货提示" style="display:none; width: 500px;">
    <h3>请填写发货的物流信息</h3>

    <form id="dispatchForm" method="post" action="sale.do?method=dispatchSaleOrder">
        <table border="0" width="480">
            <tr>
                <td width="100" align="right">物流公司：</td>
                <td><input type="text" id="company" name="company" style="width:320px;height:20px;" maxlength="40"/>
                </td>
            </tr>
            <tr>
                <td width="100" align="right">运单号：</td>
                <td><input type="text" id="waybill_id" name="waybills" style="width:320px;height:20px;"
                           maxlength="100"/></td>
            </tr>
            <tr>
                <td width="100" align="right">备注：</td>
                <td><textarea id="dispatch_memo" name="dispatchMemo" style="width:320px;" maxlength="500"></textarea>
                </td>
            </tr>
            <tr>
                <td colspan="2">
                    <div class="btnClick" style="height:50px; line-height:50px">
                        <input type="hidden" id="dispatch_id" name="salesOrderId" value="${salesOrderDTO.id}"/>
                        <input type="button" id="dispatchConfirmBtn" onfocus="this.blur();" value="确&nbsp;定">
                        <input type="button" id="dispatchCancelBtn" onfocus="this.blur();" value="取&nbsp;消">
                    </div>
                </td>
            </tr>
        </table>
    </form>
</div>
<bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.STOREHOUSE">
    <jsp:include page="../../txn/common/selectStoreHouse.jsp"/>
</bcgogo:hasPermission>
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
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>