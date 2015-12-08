<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <title>入库退货单</title>
  <%
    boolean storageBinTag = ServiceManager.getService(IShopConfigService.class).isStorageBinSwitchOn((Long) request.getSession().getAttribute("shopId"));//选配仓位功能 默认开启这个功能false
  %>
  <%@include file="/WEB-INF/views/header_script.jsp" %>
  <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
  <link rel="stylesheet" type="text/css" href="styles/draftOrder<%=ConfigController.getBuildVersion()%>.css"/>
  <link rel="stylesheet" type="text/css" href="styles/returnStorage<%=ConfigController.getBuildVersion()%>.css"/>
  <link rel="stylesheet" type="text/css" href="styles/returnStorageFinish<%=ConfigController.getBuildVersion()%>.css"/>
  <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
  <link rel="stylesheet" type="text/css" href="styles/supplierLook<%=ConfigController.getBuildVersion()%>.css"/>
  <link rel="stylesheet" type="text/css" href="styles/yinshouyinfu<%=ConfigController.getBuildVersion()%>.css"/>
  <c:choose>
     <c:when test="<%=storageBinTag%>">
       <link rel="stylesheet" type="text/css" href="styles/storageBinOn<%=ConfigController.getBuildVersion()%>.css"/>
     </c:when>
     <c:otherwise>
       <link rel="stylesheet" type="text/css" href="styles/storageBinOff<%=ConfigController.getBuildVersion()%>.css"/>
     </c:otherwise>
  </c:choose>
  <script type="text/javascript" src="js/utils/tableUtil<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/module/invoiceCommon<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/utils/tableUtil<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/detailsArrears<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/page/txn/returnStorageFinish<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/page/txn/txn<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/suggestion<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/script<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/selectOptions<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/draftOrderBox<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.js"></script>
  <script type="text/javascript" src="js/messagePrompt<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/checkMobileAndTelphone<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/supplierSearch<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/statementAccount/statementAccountUtil<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript">
    defaultStorage.setItem(storageKey.MenuUid,"WEB.TXN.PURCHASE_MANAGE.RETURN");
    defaultStorage.setItem(storageKey.MenuCurrentItem,"详情");
    //单据操作日志
    $().ready(function(){
        var orderId = jQuery("#id").val();
        APP_BCGOGO.Net.asyncPost({
            url: "txn.do?method=showOperationLog",
            data: {
                orderId: orderId,
                orderType: "purchase_return"
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
<div class="i_main">
<jsp:include page="txnNavi.jsp">
	<jsp:param name="currPage" value="purchase"/>
</jsp:include>
<jsp:include page="purchaseNavi.jsp">
	<jsp:param name="currPage" value="purchaseReturn"/>
</jsp:include>
<div class="i_mainRight" id="i_mainRight">
<jsp:include page="unit.jsp"/>
<form:form commandName="purchaseReturnDTO" id="purchaseReturnForm" action="goodsReturn.do?method=saveReturnStorage" method="post">
<input id="orderType" name="orderType" value="purchaseReturnOrder" type="hidden"/>
<input type="hidden" id="basePath" name="bathPath" value="<%=basePath%>"/>
<form:hidden path="id" value="${purchaseReturnDTO.id == null?'': purchaseReturnDTO.id}" cssClass="checkStringEmpty"/>
<form:hidden path="supplierId" value="${purchaseReturnDTO.supplierId}" />
<form:hidden path="statementAccountOrderId" value="${purchaseReturnDTO.statementAccountOrderId}"/>
<form:hidden path="status" value="${purchaseReturnDTO.status == null?'': purchaseReturnDTO.status}"/>
<form:hidden path="supplierShopId" value="${purchaseReturnDTO.supplierShopId}"/>
    <ul class="yinye_title clear">
        <%--<li id="fencount">入库退货单</li>--%>
        <li class="danju" style="width:250px;">
            单据号：<strong>${purchaseReturnDTO.receiptNo} <a href="javascript:showOrderOperationLog('${purchaseReturnDTO.id}','purchase_return')"  class="blue_col">操作记录</a></strong>
        </li>
            <c:if test="${!empty purchaseReturnDTO.supplierShopId}">
                <li class="danju" style="text-align: left;width: 50px">
                    <a class="connect">在线</a>
                </li>
            </c:if>
        <li class="danju">
            单据状态：
            <c:choose>
                <c:when test="${purchaseReturnDTO.status=='SETTLED' && purchaseReturnDTO.accountDebtAmount>0}">
                    <strong>欠款结算</strong>
                </c:when>
                <c:otherwise>
                    <strong>${purchaseReturnDTO.status.name}</strong>
                </c:otherwise>
            </c:choose>
        </li>
            <c:if test="${purchaseReturnDTO.originReceiptNo != null}">
                <li class="danju" style="width:170px;">相关单据： <strong>${purchaseReturnDTO.originReceiptNo}</strong></li>
            </c:if>
    </ul>

    <div class="clear"></div>
     <div class="tuihuo_tb">
        <table >
            <col width="80"/>
            <tr>
                <td>供应商信息</td>
            </tr>
        </table>
        <table class="clear tb_tui" id="tb_tui">
            <col width="80"/>
            <col width="200"/>
            <col width="150"/>
            <col width="150"/>
            <col width="150"/>
            <col width="180"/>
            <col/>
            <tr>
                <td class="td_title">供应商</td>
                <td><span style="width:160px;float:left;margin-left:5px;" class="ellipsis">${purchaseReturnDTO.supplier}</span>
                    <c:if test="${not empty supplierDTO.supplierShopId}">
                    <a class="icon_online_shop" href="shopMsgDetail.do?method=renderShopMsgDetail&paramShopId=${supplierDTO.supplierShopId}"></a>
                    <a class="J_QQ" data_qq="${supplierShop.qqArray}"></a>
                    </c:if>
                </td>
                <td class="td_title">联系人</td>
                <td>${purchaseReturnDTO.contact}</td>
                <td class="td_title">联系电话</td>
                <td>${purchaseReturnDTO.mobile}</td>
            </tr>
            <tr>
                <td class="td_title">地址</td>
                <td colspan="3">${purchaseReturnDTO.address}</td>
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
        <table width="983">
            <col width="80"/>
            <col width="80"/>
            <tr>
                <td>退货信息</td>
                <bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.STOREHOUSE">
                    <td style="width:30%;">仓库：${purchaseReturnDTO.storehouseName}</td>
                </bcgogo:hasPermission>
                <td style="width:35%;">验货人：${purchaseReturnDTO.editor}</td>
                <td>退货日期：${purchaseReturnDTO.vestDateStr}</td>
                <td>
                    <c:if test="${!empty purchaseReturnDTO.supplierShopId}">
                        <a id="showOperationLog" href="javascript:showOperationLog()">查看操作记录</a>
                    </c:if>
                </td>
            </tr>
        </table>
        <table class="clear tb_tui" id="tb_tui">
            <col width="80"/>
            <col />
            <col width="90"/>
            <col width="90"/>
            <col width="80"/>
            <col width="80"/>
            <col width="80"/>
            <col width="80"/>
            <col width="75"/>
            <col width="88"/>
            <tr class="tab_title" >
                <td>商品编号</td>
                <td>品名</td>
                <td>品牌/产地</td>
                <td>规格</td>
                <td>型号</td>
                <td>车型</td>
                <td>车辆品牌</td>
                <td>退货价</td>
                <td>退货数/单位</td>
                <td>小计</td>
            </tr>
            <c:forEach items="${purchaseReturnDTO.itemDTOs}" var="itemDTO" varStatus="status">
                <tr class="item">
                    <td>
                        ${itemDTO.commodityCode!=null?itemDTO.commodityCode:""}
                    </td>
                    <td>
                        ${itemDTO.productName!=null?itemDTO.productName:""}
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
                    <td>
                    	${itemDTO.price!=null?itemDTO.price:"0"}
                    </td>
                    <td style="color:#FF6700;">
                        ${itemDTO.amount!=null?itemDTO.amount:""}${itemDTO.unit!=null?itemDTO.unit:""}
                    </td>
                    <td style="color:#FF0000;">
                        ${itemDTO.total!=null?itemDTO.total:"0"}
                    </td>
                </tr>
            </c:forEach>
            <tr>
                <td colspan="8">合计：</td>
                <td colspan="1">${purchaseReturnDTO.totalReturnAmount == null ? "0":purchaseReturnDTO.totalReturnAmount }</td>
                <td colspan="2">${purchaseReturnDTO.total!=null?purchaseReturnDTO.total:""}</td>
            </tr>
            <tr>
                <td colspan="1" class="td_title">备注：</td>
                <td colspan="10"><span class="sale-memo">${purchaseReturnDTO.memo}</span></td>
            </tr>
        </table>
         <c:if test="${purchaseReturnDTO.status =='SELLER_REFUSED' || purchaseReturnDTO.status =='REPEAL'}">
             <table class="clear" id="tb_tui" style="margin-top:10px;">
                 <tr class="memo_bg">
                     <td colspan="1" class="td_title" style="width:75px;">拒绝理由：</td>
                     <td colspan="11"><span class="sale-memo">${purchaseReturnDTO.refuseReason}</span></td>
                 </tr>
             </table>
         </c:if>
            <div class="height"></div>
<strong class="jie_info clear">结算信息</strong>
        <div class="jie_detail clear almost">
			<div>应收总额：&nbsp;<span>${purchaseReturnDTO.total!=null?purchaseReturnDTO.total:""}元</span></div>
            <input type="hidden" id="hiddenDebt" value="${purchaseReturnDTO.accountDebtAmount}" >
            <div style="">实收总计：&nbsp;<span>${purchaseReturnDTO.settledAmount>0?purchaseReturnDTO.settledAmount:0.0}元</span></div>
            <div style="">优惠总计：&nbsp;<span>${purchaseReturnDTO.accountDiscount>0?purchaseReturnDTO.accountDiscount:0.0}元</span></div>
            <div style="">挂账金额：&nbsp;<span class="red_color">${purchaseReturnDTO.accountDebtAmount>0?purchaseReturnDTO.accountDebtAmount:0.0}元</span></div>
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
            <td >本次实收</td>
        	<td >本次优惠</td>
            <td >本次挂账</td>
            <td >附加信息</td>
        </tr>
      <c:forEach items="${payableHistoryRecordDTOs}" var="payableHistoryRecordDTO" varStatus="status">
        <c:if test="${payableHistoryRecordDTO.statementAccountFlag == true}" var="isStatementAccountRecord">
         <tr>
        	<td>${payableHistoryRecordDTO.paidTimeStr}</td>
            <td>${payableHistoryRecordDTO.payer}</td>
            <td>${-payableHistoryRecordDTO.actuallyPaid - payableHistoryRecordDTO.deduction - payableHistoryRecordDTO.creditAmount>0?-payableHistoryRecordDTO.actuallyPaid - payableHistoryRecordDTO.deduction - payableHistoryRecordDTO.creditAmount:0.0}</td>
            <td>${-payableHistoryRecordDTO.statementAmount>0?-payableHistoryRecordDTO.statementAmount:0.0}</td>
            <td>${-payableHistoryRecordDTO.deduction>0?-payableHistoryRecordDTO.deduction:0.0}</td>
            <td>${-payableHistoryRecordDTO.creditAmount>0?-payableHistoryRecordDTO.creditAmount:0.0}</td>
            <td>
                对账结算：¥${-payableHistoryRecordDTO.statementAmount>0?-payableHistoryRecordDTO.statementAmount:0.0}&nbsp;&nbsp;对账单号：<a class="blue_color" onclick="openStatementOrder('${purchaseReturnDTO.statementAccountOrderId}');" href="#">${receiveNo}</a>
            </td>
           </tr>
        </c:if>
         <c:if test="${!isStatementAccountRecord && -payableHistoryRecordDTO.actuallyPaid - payableHistoryRecordDTO.deduction - payableHistoryRecordDTO.creditAmount>0}">
        <tr>
        	<td>${payableHistoryRecordDTO.paidTimeStr}</td>
            <td>${payableHistoryRecordDTO.payer}</td>

            <td>
          <c:if test="${status.index==0}" var="isFirstRecord">
                       &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;--
          </c:if>
          <c:if test="${!isFirstRecord}">
            ${-payableHistoryRecordDTO.actuallyPaid - payableHistoryRecordDTO.deduction - payableHistoryRecordDTO.creditAmount}
          </c:if>
            </td>
            <td>${-payableHistoryRecordDTO.actuallyPaid>0?-payableHistoryRecordDTO.actuallyPaid:0.0}</td>
            <td>${-payableHistoryRecordDTO.deduction>0?-payableHistoryRecordDTO.deduction:0.0}</td>
            <td>${-payableHistoryRecordDTO.creditAmount>0?-payableHistoryRecordDTO.creditAmount:0.0}</td>
            <td>
              <c:if test="${-payableHistoryRecordDTO.cash>0}">
                    现金：¥${-payableHistoryRecordDTO.cash}<br />
              </c:if>

              <c:if test="${-payableHistoryRecordDTO.bankCardAmount>0}">
                    银联：¥${-payableHistoryRecordDTO.bankCardAmount}<br />
              </c:if>

              <c:if test="${-payableHistoryRecordDTO.checkAmount>0}">
                    支票：¥${-payableHistoryRecordDTO.checkAmount}
                    使用支票号${payableHistoryRecordDTO.checkNo}<br/>
              </c:if>

              <c:if test="${-payableHistoryRecordDTO.depositAmount>0}">
                    预付款：¥${-payableHistoryRecordDTO.depositAmount}
              </c:if>
            </td>
        </tr>
        </c:if>
      </c:forEach>

        </table>
        </div></div>
         <div class="height"></div>
    </div>
    <bcgogo:permission>
    <bcgogo:if permissions="WEB.TXN.PURCHASE_MANAGE.RETURN.CANCEL">
        <%-- 在线单据结算后不可以作废 --%>
        <c:if test="${(purchaseReturnDTO.supplierShopId!=null && (purchaseReturnDTO.status== 'PENDING' || purchaseReturnDTO.status== 'SELLER_REFUSED'))
                || (purchaseReturnDTO.supplierShopId == null && purchaseReturnDTO.status == 'SETTLED')}">
            <div class="invalidImg" id="invalid_div" style="display: block;margin-top:-6px">
                <input id="nullifyBtn" type="button" onfocus="this.blur();"/>

                <div class="invalidWords" id="invalidWords">作废</div>
            </div>
        </c:if>
    </bcgogo:if>
    </bcgogo:permission>
    <bcgogo:hasPermission permissions="WEB.TXN.PURCHASE_MANAGE.RETURN"  resourceType="menu">
        <c:if test="${(purchaseReturnDTO.supplierShopId != null && purchaseReturnDTO.status== 'REPEAL')
                    || (purchaseReturnDTO.supplierShopId == null && (purchaseReturnDTO.status== 'SETTLED' || purchaseReturnDTO.status == 'REPEAL') )}">
            <div class="copyInput_div" id="copyInput_div" style="display: block;margin-top:-6px">
                <input id="copyInput" type="button" onfocus="this.blur();"/>

                <div class="copyInput_text_div" id="copyInput_text">复制</div>
            </div>
        </c:if>
    </bcgogo:hasPermission>

    <div class="zuofei" id="zuofei" style="left: 600px; top: 150px;"></div>
    <div class="shopping_btn" style="float:right; clear:right; margin-top:6px;">
        <c:if test="${purchaseReturnDTO.status== 'SELLER_ACCEPTED'}">
            <div id="account_div" class="btn_div_Img">
                <input id="accountBtn" class="saleAccount j_btn_i_operate" type="button" onfocus="this.blur();">

                <div class="optWords">结算</div>
            </div>
        </c:if>
        <div id="print_div" class="btn_div_Img">
            <input type="button" id="printBtn" value="" class="print j_btn_i_operate"/>

            <div class="optWords" id="printWords">打印</div>
            <input type="hidden" id="print" name="print" value="${purchaseReturnDTO.print}"/>
        </div>
    </div>


<input type="hidden" name="cash" id="cash" value="${purchaseReturnDTO.cash}">
<input type="hidden" name="strikeAmount" id="strikeAmount" value="${purchaseReturnDTO.strikeAmount}">
<input type="hidden" name="depositAmount" id="depositAmount" value="${purchaseReturnDTO.depositAmount}">
</form:form>
</div>

</div>

<div id="mask"  style="display:block;position: absolute;">
</div>
<div class="tuihuo" style="display:none;"></div>
<div class="acceptedImg" id="acceptedImg"></div>
<div class="refusedImg" id="refusedImg"></div>
<div class="debtSettleImg" id="debtSettleImg" ></div>
<div class="jie_suan" id="settledImg" style="left:800px;"></div>
<iframe id="iframe_PopupBox" style="position:absolute;z-index:5; left:200px; top:400px; display:none;" allowtransparency="true" width="1000px" height="1000px" frameborder="0" src="" scrolling="no"></iframe>

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
              <td>供应商</td>
              <td>退货商品</td>
              <td class="tab_last"></td>
          </tr>
      </table>
    <!--分页-->
    <div class="hidePageAJAX">
      <jsp:include page="/common/pageAJAX.jsp">
        <jsp:param name="url" value="draft.do?method=getDraftOrders"></jsp:param>
        <jsp:param name="data" value="{startPageNo:1,orderTypes:'RETURN'}"></jsp:param>
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
<div id="div_brand" class="i_scroll" style="display:none;height:230px">
      <div class="Scroller-Container" id="Scroller-Container_id">
      </div>
</div>
 <div id="dialog-confirm" title="提醒">
    <p><span class="ui-icon ui-icon-alert" style="float:left; margin:0 7px 20px 0;"></span>
        <div id="dialog-confirm-text"></div>
    </p>
</div>

<div id="div_serviceName" class="i_scroll" style="display:none;width:228px;">
    <div class="Scroller-Container" id="Scroller-Container_ServiceName">
    </div>
</div>
<div id="newSettlePage" style="display:none"></div>
<bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.STOREHOUSE">
    <jsp:include page="../txn/common/selectStoreHouse.jsp"/>
</bcgogo:hasPermission>

<iframe id="iframe_PopupBox_account" style="position:absolute;z-index:5; left:500px; top:400px; display:none;"
        allowtransparency="true"  width="900px" height="450px" frameborder="0" src="" scrolling="no"></iframe>
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