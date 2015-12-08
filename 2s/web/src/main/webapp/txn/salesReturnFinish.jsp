<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>销售退货单</title>
    <%
        boolean storageBinTag = ServiceManager.getService(IShopConfigService.class).isStorageBinSwitchOn((Long) request.getSession().getAttribute("shopId"));//选配仓位功能 默认开启这个功能false
    %>
    <%@include file="/WEB-INF/views/header_script.jsp" %>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/draftOrder<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/returnStorage<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/accept<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/supplierLook<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/yinshouyinfu<%=ConfigController.getBuildVersion()%>.css"/>

    <link rel="stylesheet" type="text/css"
          href="styles/returnStorageFinish<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css"
          href="styles/goodsSaleFinish<%=ConfigController.getBuildVersion()%>.css"/>
    <c:choose>
        <c:when test="<%=storageBinTag%>">
            <link rel="stylesheet" type="text/css"
                  href="styles/storageBinOn<%=ConfigController.getBuildVersion()%>.css"/>
        </c:when>
        <c:otherwise>
            <link rel="stylesheet" type="text/css"
                  href="styles/storageBinOff<%=ConfigController.getBuildVersion()%>.css"/>
        </c:otherwise>
    </c:choose>
    <style>
        .blue {
            color: blue !important;
        }
    </style>

    <script type="text/javascript" src="js/module/invoiceCommon<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/page/txn/salesReturnFinish<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/page/txn/txn<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/script<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/selectOptions<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.js"></script>
    <script type="text/javascript" src="js/messagePrompt<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/supplierSearch<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/draftOrderBox<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/utils/tableUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/detailsArrears<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/suggestion<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/page/txn/salesReturn<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/statementAccount/statementAccountUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">
        defaultStorage.setItem(storageKey.MenuUid,"WEB.TXN.SALE_MANAGE.RETURN");
        defaultStorage.setItem(storageKey.MenuCurrentItem,"详情");
        //单据操作日志
        $().ready(function(){
            var orderId = jQuery("#id").val();
            APP_BCGOGO.Net.asyncPost({
                url: "txn.do?method=showOperationLog",
                data: {
                    orderId: orderId,
                    orderType: "sale_return"
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
    </script>
</head>
<body>
<%@ include file="/WEB-INF/views/header_html.jsp" %>
<%@ include file="/common/messagePrompt.jsp" %>

<div style="display:none" id="errorMsg">${errorMsg}</div>
<div class="i_main clear">
<jsp:include page="txnNavi.jsp">
    <jsp:param name="currPage" value="goodsSale"/>
</jsp:include>
<jsp:include page="saleNavi.jsp">
    <jsp:param name="currPage" value="saleReturn"/>
</jsp:include>
<div class="i_mainRight" id="i_mainRight">
<form:form id="salesReturnForm"  commandName="salesReturnDTO" method="post">
<jsp:include page="unit.jsp"/>
<input id="orderType" name="orderType" value="salesReturnOrder" type="hidden"/>
<input type="hidden" id="basePath" name="bathPath" value="<%=basePath%>"/>
<input type="hidden" id="id" name="id" value="${salesReturnDTO.id == null?'': salesReturnDTO.id}" class="checkStringEmpty"/>
<input type="hidden" id="operateType" name="operateType"/>
<input type="hidden" id="status" name="status" value="${salesReturnDTO.status == null?'': salesReturnDTO.status}"/>
<input type="hidden" id="refuseReason" name="refuseReason"/>
<input type="hidden" id="statementAccountOrderId" name="statementAccountOrderId" value="${salesReturnDTO.statementAccountOrderId == null?'': salesReturnDTO.statementAccountOrderId}"/>
<input type="hidden" id="settledAmount" name="settledAmount" />
<input type="hidden" id="cashAmount" name="cashAmount" />
<input type="hidden" id="bankAmount" name="bankAmount" value="" />
<input type="hidden" id="bankCheckAmount" name="bankCheckAmount" value="" />
<input type="hidden" id="bankCheckNo" name="bankCheckNo" />
<input type="hidden" id="strikeAmount" name="strikeAmount" />
<input type="hidden" id="discountAmount" name="discountAmount" />
<input type="hidden" id="accountDebtAmount" name="accountDebtAmount" />
<input id="status" type="hidden" value="${salesReturnDTO.status}"/>

<span id="customerId" style="display:none;">${salesReturnDTO.customerId}</span>
<ul class="yinye_title clear">
        <%--<li id="fencount">销售退货单</li>--%>
    <li class="danju" style="width:250px;">
        单据号：<strong>${salesReturnDTO.receiptNo} <a href="javascript:showOrderOperationLog('${salesReturnDTO.id}','sale_return')"  class="blue_col">操作记录</a></strong>
    </li>
    <c:if test="${!empty salesReturnDTO.purchaseReturnOrderId}">
        <li class="danju" style="text-align: left;width: 50px">
            <a class="connect">在线</a>
        </li>
    </c:if>
    <li class="danju">
        单据状态：
                    <span id="orderStatusStr">
                    <c:choose>
                        <c:when test="${salesReturnDTO.status=='SETTLED' && salesReturnDTO.accountDebtAmount>0}">
                            <strong>欠款结算</strong>
                        </c:when>
                        <c:otherwise>
                            <strong>${salesReturnDTO.status.name}</strong>
                        </c:otherwise>
                    </c:choose>
                    </span>
    </li>
    <c:if test="${salesReturnDTO.originReceiptNo != null}">
        <li class="danju" style="width:170px;">相关单据： <strong>${salesReturnDTO.originReceiptNo}</strong></li>
    </c:if>
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
    <col width="150"/>
    <col width="180"/>
    <col/>
    <tr>
        <td class="td_title">客户信息</td>
        <td><span id="customerName" style="width:160px;float:left;margin-left:5px;" class="ellipsis" title="${salesReturnDTO.customer}">${salesReturnDTO.customer}</span>
            <c:if test="${not empty customerDTO.customerShopId}">
            <a class="icon_online_shop" href="shopMsgDetail.do?method=renderShopMsgDetail&paramShopId=${customerDTO.customerShopId}"></a>
            <a class="J_QQ" data_qq="${customerDTO.qqArray}"></a>
            </c:if>
        </td>
        <td class="td_title">联系人</td>
        <td>${salesReturnDTO.contact}</td>
        <td class="td_title">联系电话</td>
        <td>${salesReturnDTO.mobile}</td>
    </tr>
    <tr>
        <td class="td_title">地址</td>
        <td colspan="3">${salesReturnDTO.address}</td>
        <td class="td_title">当前应收应付</td>
        <td class="qian_red">
            <div class="pay" id="duizhanFinish" style="text-align:left;margin-left: 25px">
                <c:choose>
                    <c:when test="${not empty totalReceivable}">
                        <span class="red_color payMoney">收¥${totalReceivable}</span>
                    </c:when>
                    <c:otherwise>
                        <span class="gray_color fuMoney">收¥0</span>
                    </c:otherwise>
                </c:choose>
                <c:choose>
                    <c:when test="${not empty totalPayable}">
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
<table width="983">
    <col width="80"/>
    <col width="80"/>
    <tr>
        <td>退货信息</td>
        <bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.STOREHOUSE">
            <td style="width:20%;">仓库：
                <c:choose>
                    <c:when test="${salesReturnDTO.status eq 'WAITING_STORAGE'}">
                        <form:select path="storehouseId" cssClass="j_checkStoreHouse"  cssStyle="width:120px;height:21px;border: 1px solid #BBBBBB;" autocomplete="off">
                            <option value="">—请选择仓库—</option>
                            <form:options items="${storeHouseDTOList}" itemValue="id" itemLabel="name"/>
                        </form:select>
                    </c:when>
                    <c:otherwise>
                        ${salesReturnDTO.storehouseName}
                    </c:otherwise>
                </c:choose>
            </td>
        </bcgogo:hasPermission>
        <td style="width:20%;">
            <div style="float:left;width:180px;" id="salesReturnerDiv">
                退货人：
                <c:choose>
                    <c:when test="${salesReturnDTO.status eq 'WAITING_STORAGE'}">
                                <span id="salesReturnerSpan">
                              <input name="salesReturner" id="salesReturner" value="${salesReturnDTO.salesReturner}"
                                     readOnly="true"
                                     initsalesReturnervalue="${salesReturnDTO.salesReturner}"
                                     style="width: 100px;" cssClass="checkStringChanged" class="textbox"/>
                              <img src="images/list_close.png" id="deleteSalesReturner"
                                   style="width:12px;cursor:pointer">
                              <input type="hidden" id="salesReturnerId" name="salesReturnerId"
                                     value="${salesReturnDTO.salesReturnerId}"
                                     initsalesReturnerIdvalue="${salesReturnDTO.salesReturnerId}"/>
                              <input type="hidden" id="editorId" name="editorId"
                                     value="${salesReturnDTO.editorId}"
                                     initeditorIdvalue="${salesReturnDTO.editorId}"/>
                              </span>
                    </c:when>
                    <c:otherwise>
                        ${salesReturnDTO.salesReturner}
                    </c:otherwise>
                </c:choose>
            </div>
        </td>
        <td style="width:200px;">退货日期：${salesReturnDTO.vestDateStr}</td>
        <td>
            <c:if test="${!empty salesReturnDTO.purchaseReturnOrderId}">
                <a id="showOperationLog" href="javascript:showOperationLog()">查看操作记录</a>
            </c:if>
        </td>
    </tr>
</table>
<div>
    <table class="clear table2" id="tb_tui" style="margin-bottom:10px;">
        <col width="75"/>
        <col width="75"/>
        <col width="70"/>
        <col width="95"/>
        <col width="80"/>
        <col width="80"/>
        <col width="80"/>
        <col width="80"/>

        <col width="70"/>
        <c:if test="${salesReturnDTO.status=='WAITING_STORAGE'}">
            <col width="80"/>
            <col width="80"/>
        </c:if>
        <col width="80"/>
        <col width="80"/>
        <tr class="tab_title">
            <td>商品编号</td>
            <td>品名</td>
            <td>品牌/产地</td>
            <td>规格</td>
            <td>型号</td>
            <td>车型</td>
            <td>车辆品牌</td>
            <td>营业分类</td>
            <td>退货价</td>
            <td>退货数量</td>
            <c:if test="${salesReturnDTO.status=='WAITING_STORAGE'}">
                <td>入库数量</td>
                <td>入库单位</td>
            </c:if>
            <td>小计</td>
        </tr>
        <%--<tr class="space">--%>
            <%--<td style="height: 8px" colspan="15"></td>--%>
        <%--</tr>--%>
        <c:forEach items="${salesReturnDTO.itemDTOs}" var="itemDTO" varStatus="status">
            <tr class="item">
                <c:choose>
                    <c:when test="${salesReturnDTO.status!='SETTLED' && salesReturnDTO.purchaseReturnOrderId!=null}">
                        <td>
                          <input type="hidden" id="itemDTOs${status.index}.productId" value="${itemDTO.productId}"/>
                        ${itemDTO.purchaseReturnItemDTO.commodityCode}
                        </td>
                        <td><span id="productName${status.index}">${itemDTO.purchaseReturnItemDTO.productName}</span><span id="productNameSupplier${status.index}" style="display:none">${itemDTO.productName}</span></td>
                        <td><span id="brand${status.index}">${itemDTO.purchaseReturnItemDTO.brand}</span><span id="brandSupplier${status.index}" style="display:none">${itemDTO.brand}</span></td>
                        <td><span id="spec${status.index}">${itemDTO.purchaseReturnItemDTO.spec}</span><span id="specSupplier${status.index}" style="display:none">${itemDTO.spec}</span></td>
                        <td><span id="model${status.index}">${itemDTO.purchaseReturnItemDTO.model}</span><span id="modelSupplier${status.index}" style="display:none">${itemDTO.model}</span></td>
                        <td><span id="vehicleModel${status.index}">${itemDTO.purchaseReturnItemDTO.vehicleModel}</span><span id="vehicleModelSupplier${status.index}" style="display:none">${itemDTO.vehicleModel}</span></td>
                        <td><span id="vehicleBrand${status.index}">${itemDTO.purchaseReturnItemDTO.vehicleBrand}</span><span id="vehicleBrandSupplier${status.index}" style="display:none">${itemDTO.vehicleBrand}</span></td>
                    </c:when>
                    <c:otherwise>
                        <td>${itemDTO.commodityCode}</td>
                        <td>${itemDTO.productName}</td>
                        <td>${itemDTO.brand}</td>
                        <td>${itemDTO.spec}</td>
                        <td>${itemDTO.model}</td>
                        <td>${itemDTO.vehicleModel}</td>
                        <td>${itemDTO.vehicleBrand}</td>
                    </c:otherwise>
                </c:choose>
                <td>${itemDTO.businessCategoryName}</td>
                <td>${itemDTO.price!=null?itemDTO.price:"0"}</td>
                <td>${itemDTO.amount==null?"0":itemDTO.amount} ${itemDTO.unit}</td>
                <c:if test="${salesReturnDTO.status=='WAITING_STORAGE'}">
                    <td><input id="itemDTOs${status.index}.amount" type="text" value="${itemDTO.storageAmount}" name="itemDTOs[${status.index}].storageAmount" size="7"
                               class="itemAmount table_input checkNumberEmpty"/></td>
                    <td>
                        <input type="text" name="itemDTOs[${status.index}].wholesalerUnit" id="itemDTOs${status.index}.unit"
                               value='${itemDTO.wholesalerUnit!=null?itemDTO.wholesalerUnit:""}' style="width:40px;"
                               class="itemUnit table_input checkStringEmpty"/>
                        <input type="hidden" value="${itemDTO.storageUnit}" name="itemDTOs[${status.index}].storageUnit" id="itemDTOs${status.index}.storageUnit"/>
                        <input type="hidden" value="${itemDTO.sellUnit}" name="itemDTOs[${status.index}].sellUnit"  id="itemDTOs${status.index}.sellUnit"/>
                        <input type="hidden" value="${itemDTO.rate}" name="itemDTOs[${status.index}].rate" id="itemDTOs${status.index}.rate"/>
                        <input type="hidden" value="${itemDTO.productId}" name="itemDTOs[${status.index}].productId" id="itemDTOs${status.index}.productId"/>
                        <input type="hidden" value="${itemDTO.customerProductId}" name="itemDTOs[${status.index}].customerProductId" id="itemDTOs${status.index}.customerProductId"/>
                        <input type="hidden" value="${itemDTO.id}" name="itemDTOs[${status.index}].id" id="itemDTOs${status.index}.id" />
                    </td>
                </c:if>
                <td style="color:#FF0000;">${itemDTO.total!=null?itemDTO.total:"0"}</td>
            </tr>
        </c:forEach>
        <tr>
            <td colspan="9">合计：</td>
            <td colspan="1">${salesReturnDTO.totalReturnAmount == null ? "0":salesReturnDTO.totalReturnAmount}</td>
            <c:if test="${salesReturnDTO.status=='WAITING_STORAGE'}">
                <td colspan='2'></td>
            </c:if>
            <td colspan="1">${salesReturnDTO.total!=null?salesReturnDTO.total:"0"}</td>
        </tr>
        <tr>
            <td colspan="2">退货备注：</td>
            <td colspan="
                        <c:choose>
                          <c:when test="${salesReturnDTO.status=='WAITING_STORAGE'}">
                              11
                              </c:when>
                              <c:otherwise>
                              9
                              </c:otherwise>
                              </c:choose>">
                    ${salesReturnDTO.purchaseReturnOrderMemo}
            </td>
        </tr>
    </table>
</div>
<div class="clear"></div>

<table class="clear tb_tui"  style="margin-top:10px;">
    <tr class="memo_bg">
        <c:choose>
            <c:when test="${salesReturnDTO.status =='PENDING'}">
                <td class="td_title" style="width: 75px; text-align: center; height: 60px;" colspan="1">
                    备注：
                </td>
                <td style="" colspan="12">
                    <textarea id="memo" name="memo" initialValue="请输入备注..." style="color:#7F7F7F;width: 98%; height: 45px;" maxlength="500">请输入备注...</textarea>
                </td>
            </c:when>
            <c:otherwise>
                <td class="td_title" style="width: 75px; text-align: center;" colspan="1">
                    备注：
                </td>
                <td style="" colspan="12">
                        ${salesReturnDTO.memo}
                </td>
            </c:otherwise>
        </c:choose>
    </tr>
</table>
<c:if test="${salesReturnDTO.status =='REFUSED'}">
    <table class="clear" id="tb_tui" style="margin-top:10px;">
        <tr class="memo_bg">
            <td colspan="1" class="td_title" style="width:75px">拒绝理由：</td>
            <td colspan="12">${salesReturnDTO.refuseReason} </td>
        </tr>
    </table>
</c:if>

<%--<c:if test="${salesReturnDTO.status eq 'SETTLED'}">--%>
<div class="height"></div>
<strong class="jie_info clear">结算信息</strong>
<div class="jie_detail clear almost">
    <div>应付总额：&nbsp;<span>${salesReturnDTO.total}元</span></div>

    <div style="">实付总计：&nbsp;<span>${salesReturnDTO.settledAmount}元</span></div>
    <div style="">优惠总计：&nbsp;<span>
             ${salesReturnDTO.discountAmount}元
            </span></div>
    <div style="">挂账金额：&nbsp;<span class="red_color" id="returnDebt">${salesReturnDTO.accountDebtAmount}元</span></div>
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
        <c:forEach items="${receptionRecordDTOs}" var="receptionRecordDTO" varStatus="status">

            <tr>
                <td>${receptionRecordDTO.receptionDateStr}</td>
                <td>${receptionRecordDTO.payee}</td>

                <td>
                    <c:if test="${status.index==0}" var="isFirstRecord">
                        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;--
                    </c:if>
                    <c:if test="${!isFirstRecord}">
                        ${-receptionRecordDTO.originDebt>0?-receptionRecordDTO.originDebt:0.0}
                    </c:if>
                </td>
                <td>${-receptionRecordDTO.amount>0?-receptionRecordDTO.amount:0.0}</td>
                <td>${-receptionRecordDTO.discount>0?-receptionRecordDTO.discount:0.0}</td>
                <td>${-receptionRecordDTO.remainDebt>0?-receptionRecordDTO.remainDebt:0.0}</td>
                <td>
                    <c:if test="${-receptionRecordDTO.cash>0}">
                        现金：¥${-receptionRecordDTO.cash}<br />
                    </c:if>

                    <c:if test="${-receptionRecordDTO.bankCard>0}">
                        银联：¥${-receptionRecordDTO.bankCard}<br />
                    </c:if>

                    <c:if test='${"CUSTOMER_STATEMENT_DEBT" eq receptionRecordDTO.orderTypeEnum}'>

                        对账结算：¥${-receptionRecordDTO.statementAmount>0?-receptionRecordDTO.statementAmount:0.0}
                        对账单号：<a class="blue_color" onclick="openStatementOrder('${salesReturnDTO.statementAccountOrderId}');" href="#">${receiveNo}</a>
                        <br />
                    </c:if>

                    <c:if test="${-receptionRecordDTO.cheque>0}">
                        支票：¥${-receptionRecordDTO.cheque}
                        使用支票号${receptionRecordDTO.chequeNo}<br/>
                    </c:if>
                    <c:if test="${receptionRecordDTO.memberId != null}">
                        会员卡：¥${receptionRecordDTO.memberBalancePay}
                        卡号：${receptionRecordDTO.memberNo}
                        ${receptionRecordDTO.memberDiscountRatio*10}折优惠<br />
                    </c:if>
                    <c:if test="${-receptionRecordDTO.deposit > 0.001}" >
                        预收款：¥${-receptionRecordDTO.deposit}<br/>
                    </c:if>

                </td>
            </tr>
        </c:forEach>
    </table>
</div></div>
<%--</c:if>--%>
<div class="height"></div>
</div>

<div class="clear"></div>
<bcgogo:hasPermission permissions="WEB.TXN.SALE_MANAGE.RETURN.REPEAL">
    <c:if test="${salesReturnDTO.status =='SETTLED' && salesReturnDTO.purchaseReturnOrderId == null && salesReturnDTO.statementAccountOrderId == null}">
        <div class="invalidImg" id="invalid_div" style="margin:0 0 0;display: block;" >
            <input id="nullifyBtn" type="button" onfocus="this.blur();"/>

            <div class="invalidWords" id="invalidWords">作废</div>
        </div>
    </c:if>
</bcgogo:hasPermission>
<div class="shopping_btn" style="float:right; clear:right; margin-top:16px;">
    <bcgogo:hasPermission permissions="WEB.TXN.SALE_MANAGE.RETURN">
        <c:if test="${salesReturnDTO.status=='WAITING_STORAGE'}">
            <div class="btn_div_Img" id="account_div">
                <input id="accountBtn" type="button" class="saleAccount j_btn_i_operate" onfocus="this.blur();"/>
                <div class="optWords">结算</div>
            </div>
        </c:if>
        <c:if test="${salesReturnDTO.status=='PENDING'}">
            <div class="btn_div_Img" id="accept_div">
                <input id="acceptBtn" type="button" class="acceptBtn j_btn_i_operate"
                       onfocus="this.blur();"/>

                <div class="optWords">接受退货</div>
            </div>
            <div class="btn_div_Img" id="refuse_div">
                <input id="refuseBtn" type="button" class="refuseBtn j_btn_i_operate"
                       onfocus="this.blur();"/>

                <div class="optWords">拒绝退货</div>
            </div>
        </c:if>
    </bcgogo:hasPermission>
    <div class="btn_div_Img" id="print_div">
        <input id="printBtn" type="button" class="print j_btn_i_operate" onfocus="this.blur();"/>
        <input type="hidden" name="print" id="print" value="${salesReturnDTO.print}">

        <div class="optWords">打印</div>
    </div>
</div>
<div class="clear"></div>
</form:form>
</div>

</div>
<div class="zuofei" id="zuofei"></div>

<div class="acceptedImg" id="acceptedImg"></div>
<div class="pendingImg" id="pendingImg"></div>
<div class="refusedImg" id="refusedImg"></div>
<div class="stopImg" id="stopImg"></div>
<div class="waittingStorageImg" id="waittingStorageImg"></div>
<div class="debtSettleImg" id="debtSettleImg" ></div>
<div class="jie_suan" id="settledImg" style="left:800px;"></div>

<div id="mask" style="display:block;position: absolute;"></div>
<div class="tuihuo" style="display:none;"></div>

<iframe id="iframe_PopupBox" style="position:absolute;z-index:5; left:200px; top:400px; display:none;"
        allowtransparency="true" width="1000px" height="1000px" frameborder="0" src="" scrolling="no"></iframe>

<div id="id-searchcomplete" name="createGoodsReturnBill"></div>
<div id="id-searchcompleteMultiselect"></div>
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
                <td>客户</td>
                <td>退货商品</td>
                <td class="tab_last"></td>
            </tr>
        </table>
    </div>

    <!--分页-->
    <div class="hidePageAJAX">
        <jsp:include page="/common/pageAJAX.jsp">
            <jsp:param name="url" value="draft.do?method=getDraftOrders"></jsp:param>
            <jsp:param name="data" value="{startPageNo:1,orderTypes:'SALE_RETURN'}"></jsp:param>
            <jsp:param name="jsHandleJson" value="initOrderDraftTable"></jsp:param>
            <jsp:param name="hide" value="hideComp"></jsp:param>
            <jsp:param name="dynamical" value="dynamical1"></jsp:param>
        </jsp:include>
    </div>
</div>

<div id="commodityCode_dialog" style="display:none">
    <span id="commodityCode_dialog_msg"></span>
</div>
<div id="div_brand" class="i_scroll" style="display:none;height:230px">
    <div class="Scroller-Container" id="Scroller-Container_id">
    </div>
</div>
<div id="dialog-confirm" title="提醒">
    <p><span class="ui-icon ui-icon-alert" style="float:left; margin:0 7px 20px 0;"></span>

    <div id="dialog-confirm-text"></div>
    </p>
</div>
<div class="i_searchBrand" id="refuseReasonDialog" title="确认拒绝提示" style="display:none">
    <div class="i_upBody">
        <h3>友情提示：您拒绝后，该退货请求将被驳回 ！</h3>
        <h3>您确定要拒绝该销售退货吗？</h3>
        <div class="request">
            <label style="width:90px;">拒绝理由：</label>
            <textarea class="textarea" id="refuseReasonTextarea" maxlength="500"></textarea>
        </div>
        <div class="height"></div>
        <div class="btnClick">
            <input type="button" id="refuseConfirmBtn" onfocus="this.blur();" value="确&nbsp;定">
            <input type="button" id="refuseCancelBtn" onfocus="this.blur();" value="取&nbsp;消">
        </div>
    </div>
</div>
<div id="div_serviceName" class="i_scroll" style="display:none;width:228px;">
    <div class="Scroller-Container" id="Scroller-Container_ServiceName">
    </div>
</div>
<div id="acceptProductPrompt" style="display:none;" title="请确定是否接受退货">

</div>
<div id="newSettlePage" style="display:none"></div>
<iframe id="iframe_PopupBox_account" style="position:absolute;z-index:5; left:250px; top:150px; display:none;"
        allowtransparency="true" width="900px" height="450px" frameborder="0" src="" scrolling="no"></iframe>
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
<jsp:include page="/txn/orderOperationLog.jsp" />
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>