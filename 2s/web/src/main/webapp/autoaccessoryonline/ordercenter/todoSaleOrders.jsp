<%@ page import="com.bcgogo.enums.OrderStatus" %>
<%@ page import="com.bcgogo.product.dto.ProductDTO" %>
<%@ page import="com.bcgogo.txn.dto.ExpressDTO" %>
<%@ page import="com.bcgogo.txn.dto.SalesOrderDTO" %>
<%@ page import="com.bcgogo.txn.dto.SalesOrderItemDTO" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.bcgogo.enums.PromotionsEnum" %>
<%@ page import="com.bcgogo.txn.dto.PromotionsRuleDTO" %>
<%@ page import="com.bcgogo.utils.NumberUtil" %>
<%@ page import="com.bcgogo.utils.DateUtil" %>
<%@ page import="com.bcgogo.utils.CollectionUtil" %>
<%@ page import="java.util.List" %>
<%@ page import="com.bcgogo.common.StringUtil" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
  <title>待办销售单</title>

  <link rel="stylesheet" type="text/css" href="styles/up<%=ConfigController.getBuildVersion()%>.css"/>
  <link rel="stylesheet" type="text/css" href="styles/addPlan<%=ConfigController.getBuildVersion()%>.css"/>
  <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
  <link rel="stylesheet" type="text/css" href="styles/moreHistory<%=ConfigController.getBuildVersion()%>.css"/>
  <link rel="stylesheet" type="text/css" href="styles/newTodo<%=ConfigController.getBuildVersion()%>.css"/>
  <link rel="stylesheet" type="text/css" href="styles/statistics<%=ConfigController.getBuildVersion()%>.css"/>
  <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
  <link rel="stylesheet" type="text/css" href="styles/accept<%=ConfigController.getBuildVersion()%>.css"/>
  <link rel="stylesheet" type="text/css" href="styles/orderCenter<%=ConfigController.getBuildVersion()%>.css"/>
  <link rel="stylesheet" type="text/css" href="styles/cuxiao<%=ConfigController.getBuildVersion()%>.css"/>
  <style type="text/css">
  .div_year{
    background: none repeat scroll 0 0 #FFFFFF;
    border: 1px solid #ADADAD;
    display: none;
    line-height: 25px;
    margin-left: 69px;
    padding-right: 3px;
    position: absolute;
    z-index: 99;
  }

  .div_year a {
    color: #515151;
    display: block;
    padding-left: 3px;
    width: 100%;
  }
  </style>
  <%@include file="/WEB-INF/views/header_script.jsp" %>
  <script type="text/javascript" src="js/extension/jquery/plugin/jquery-ui/ui/jquery.ui.timepicker-addon.min.js"></script>
  <script type="text/javascript" src="js/extension/jquery/plugin/jquery-ui/ui/i18n/jquery.ui.datepicker-zh-CN.min.js"></script>
  <script type="text/javascript" src="js/utils/tableUtil<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/page/autoaccessoryonline/promotionsUtil<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/page/autoaccessoryonline/todoOrders<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/page/txn/txn<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript">
  defaultStorage.setItem(storageKey.MenuUid, "WEB.SCHEDULE.REMIND_ORDERS.SALE");
  defaultStorage.setItem(storageKey.MenuCurrentItem,"列表");
    //按钮操作 待办销售单
    //接受
    function acceptSale(orderId,vestDate,dom){
        if($(dom).attr("disabled")){
            return;
        }
        if(APP_BCGOGO.Permission.Version.ProductThroughSelectSupplier){
            toSalesOrder(orderId);
            return;
        }
        //赋上单据ID
        $("#salesOrderId").val(orderId);
        $("#pruchaseVestDate").html(vestDate);
        $("#acceptDialog").dialog({ width: 540, modal:true});
    }
    //拒绝
    function refuseSale(orderId,dom){
       if($(dom).attr("disabled")){
         return;
       }
        $("#refuse_id").val(orderId);
        $("#refuseReasonDialog").dialog({ width: 520, modal:true});
    }
    //发货
    function dispatchSale(orderId, dom) {
        if ($(dom).attr("disabled")) {
            return;
        }
        if(APP_BCGOGO.Permission.Version.ProductThroughSelectSupplier){
            toSalesOrder(orderId);
            return;
        }
        $("#salesOrderId").val(orderId);
        App.Net.syncPost({
            url: "sale.do?method=validateDispatchSaleOrder",
            data: {salesOrderId: orderId},
            dataType: "json",
            success: function (json) {
                if(json && json.success){
                    $("#dispatch_id").val(orderId);
                    $(".shortageOperateVerfier").remove();
                    $("#dispatchDialog").dialog({ width: 520, modal: true});
                }else{
                    if(json && json.operation=="CONFIRM_ALLOCATE_RECORD"){
                        nsDialog.jConfirm(json.msg, "确认仓库调拨提示", function (returnVal) {
                            if(returnVal){
                                window.open("allocateRecord.do?method=createAllocateRecordBySaleOrderId&salesOrderId="+orderId,"_blank");
                            }
                        });
                    }else if(json && json.operation=="ALERT_SALE_LACK"){
                        nsDialog.jAlert(json.msg);
                    }else{
                        nsDialog.jAlert(json.msg);
                    }
                }
            }
        });
    }
    //终止交易
    function stopSale(orderId, purchaseOrderStatus, dom) {
        if ($(dom).attr("disabled")) {
            return;
        }
        if (purchaseOrderStatus == "<%=OrderStatus.PURCHASE_ORDER_DONE.toString()%>") {
            nsDialog.jAlert("买家已入库，不能作废！")
        } else {
            $("#repeal_id").val(orderId);
            $("#repealReasonDialog").dialog({
                width: 520,
                modal: true,
                beforeclose: function(event, ui) {
                    return true;
                }
            });
        }
    }
    //作废
    function repealSale(orderId, dom) {
        if ($(dom).attr("disabled")) {
            return;
        }
        if(APP_BCGOGO.Permission.Version.StoreHouse){//这个时候作废才会操作库存
            if (!validatorStoreHouseOrderRepeal($("#_toStorehouseId").val(),"SALE",orderId)) {
                return;
            }
        }
        repealOrder(orderId);
    }
    //selectStoreHouse.jsp中js  调用  名称不能修改
    function repealOrder(orderId){
        nsDialog.jConfirm("友情提示：作废后可能会发生库存和金额的变化，为保证账面完整，请将原单据回笼，并标记作废！您确定要作废该销售单吗？", null, function (returnVal) {
            if (returnVal) {
                var url = "sale.do?method=saleOrderRepeal&salesOrderId=" + orderId;
                if(APP_BCGOGO.Permission.Version.StoreHouse){
                    url +="&toStorehouseId="+$("#_toStorehouseId").val();
                }
                window.open(url);
                setTimeout(function () {
                    window.location.reload();
                }, 5000);
            }
        });
    }
    //结算
    function settleSale(orderId,dom){
       if($(dom).attr("disabled")){
         return;
       }
        window.open("sale.do?method=toOnlineSalesOrder&menu-uid=WEB.TXN.SALE_MANAGE.SALE&salesOrderId="+orderId);
    }
    //欠款结算
    function debtSettleSale(orderId,dom){
        if($(dom).attr("disabled")){
            return;
        }
        window.open("sale.do?method=toOnlineSalesOrder&menu-uid=WEB.TXN.SALE_MANAGE.SALE&salesOrderId="+orderId);
    }
    //缺料
    function toShotageInventory(salesOrderId,dom){
      if($(dom).attr("disabled")=="true"){
        return;
      }
      if(stringUtil.isEmpty(salesOrderId)){
        return;
      }
      var url="storage.do?method=getProducts&type=good&goodsindexflag=gi&salesOrderId=";
      url+=salesOrderId;
      window.open(url);
    }
$(document).ready(function(){
    //接受采购
    $("#acceptConfirmBtn").bind("click", function () {
        if(APP_BCGOGO.Permission.Version.StoreHouse && G.Lang.isEmpty($("#storehouseId").val())){
            nsDialog.jAlert("请选择仓库！");
            return;
        }
        //预计交货时间
        var radios = document.getElementsByName("dispatchDateRadio");
        for(var i=0;i<radios.length;i++){
            if(radios[i].checked==true){
                $("#dispatchDate").val(radios[i].value);
                if(i==3){
                    $("#dispatchDate").val($("#definedDate").val());
                }
            }
        }
        if($("#dispatchDate").val()==""){
            nsDialog.jAlert("预计交货时间必须填写！",null,function(){});
        }else{
            $("#salesAcceptForm").attr("action","sale.do?method=acceptSaleOrder&id="+$("#salesOrderId").val());
            $("#salesAcceptForm").submit();
            $("#salesAcceptForm")[0].reset();
            setTimeout(function(){
                window.location.reload();
            },5000);
            $("#acceptDialog").dialog("close");
        }
    });
    $("#acceptCancelBtn").bind("click", function () {
        $("#salesAcceptForm")[0].reset();
        $("#acceptDialog").dialog("close");
    });

    //拒绝采购
    $("#refuseConfirmBtn").bind("click", function () {
        if($("#refuseMsg").val()!=""){
            $("#salesRefuseForm").submit();
            $("#salesRefuseForm")[0].reset();
            setTimeout(function(){
                window.location.reload();
            },5000)
            $("#refuseReasonDialog").dialog("close");
        }else{
            nsDialog.jAlert("拒绝理由必须填写！");
        }
    });
    $("#refuseCancelBtn").bind("click", function () {
        $("#salesRefuseForm")[0].reset();
        $("#refuseReasonDialog").dialog("close");
    });

    //发货
    $("#dispatchConfirmBtn").bind("click", function () {
        $("#dispatch_id").val($("#salesOrderId").val());
        if($("#company").val()!=""){
            $("#dispatchForm").submit();
            $("#dispatchForm")[0].reset();
            setTimeout(function(){
                window.location.reload();
            },5000);
            $("#dispatchDialog").dialog("close");
        }else{
            nsDialog.jAlert("物流公司名称必须填写！");
        }
    });
    $("#dispatchCancelBtn").bind("click", function () {
        $("#dispatchForm")[0].reset();
        $("#dispatchDialog").dialog("close");
    });

    //终止交易
    $("#repealConfirmBtn").bind("click", function () {
        if (!$.trim($("#repealMsg").val())) {
            nsDialog.jAlert("请填写作废理由", null, function () {
            });
            return;
        }
        $("#salesRepealForm").submit();
        $("#salesRepealForm")[0].reset();
        setTimeout(function () {
            window.location.reload();
        }, 5000);
        $("#repealReasonDialog").dialog("close");
    });
    $("#repealCancelBtn").bind("click", function () {
        $("#salesRepealForm")[0].reset();
        $("#repealReasonDialog").dialog("close");
    });
    $("#todoSaleOrders").addClass("hover_yinye");
});
  </script>
</head>
<body class="bodyMain">
<%@include file="/WEB-INF/views/header_html.jsp" %>

<div class="i_main clear">
    <jsp:include page="../autoAccessoryOnlineNavi.jsp">
        <jsp:param name="currPage" value="orderCenter"/>
    </jsp:include>
    <jsp:include page="todoOrderNavi.jsp">
        <jsp:param name="currPage" value="sale"/>
    </jsp:include>
<%
    String startTimeStr = (String)request.getAttribute("startTimeStr");
    String endTimeStr = (String)request.getAttribute("endTimeStr");
    String customerName = (String)request.getAttribute("customerName");
    String receiptNo = (String)request.getAttribute("receiptNo");
    String orderStatus = (String)request.getAttribute("orderStatus");
    //今天的时间段
    String todayTimeStr = DateUtil.convertDateLongToDateString("yyyy-MM-dd", System.currentTimeMillis());
    String yesterdayTimeStr = DateUtil.convertDateLongToDateString("yyyy-MM-dd", System.currentTimeMillis()-24*60*60*1000);
    //销售单
    Long sale_today_new = (Long)request.getAttribute("sale_today_new");
    Long sale_early_new = (Long)request.getAttribute("sale_early_new");
    Long sale_new = (Long)request.getAttribute("sale_new");
    Long sale_stocking = (Long)request.getAttribute("sale_stocking");
    Long sale_dispatch = (Long)request.getAttribute("sale_dispatch");
    Long sale_sale_debt_done = (Long)request.getAttribute("sale_sale_debt_done");
    Long sale_in_progress = (Long)request.getAttribute("sale_in_progress");
%>


    <div class="titBody">
    <%--<input id="id" type="hidden" value="${salesOrderDTO.id}">--%>
        <div class="todoCountDiv">
            <table class="todoCountTable" width="100%">
                <tr>
                    <td width="10%" style="color:#FF5E04">新订单</td>
                    <td width="20%">共有&nbsp;<a class="blue_color" <c:if test="<%=sale_new<=0%>">onclick="javascript:return false;"</c:if>
                                               href="orderCenter.do?method=getTodoOrders&type=TODO_SALE_ORDERS&orderStatus=<%=OrderStatus.PENDING.toString()%>"><%=sale_new%></a>&nbsp;条待卖家处理</td>
                    <td>
                        其中&nbsp;&nbsp;
                        <a class="blue_color" <c:if test="<%=sale_today_new<=0%>">onclick="javascript:return false;"</c:if>
                           href="orderCenter.do?method=getTodoOrders&type=TODO_SALE_ORDERS&orderStatus=<%=OrderStatus.PENDING.toString()%>&startTimeStr=<%=todayTimeStr%>">今日新增：<%=sale_today_new%>条</a>&nbsp;&nbsp;
                        <a class="blue_color" <c:if test="<%=sale_early_new<=0%>">onclick="javascript:return false;"</c:if>
                           href="orderCenter.do?method=getTodoOrders&type=TODO_SALE_ORDERS&orderStatus=<%=OrderStatus.PENDING.toString()%>&endTimeStr=<%=yesterdayTimeStr%>">往日新增：<%=sale_early_new%>条</a>
                    </td>
                </tr>
                <tr>
                    <td style="color:#FF5E04">处理中订单</td>
                    <td>共有&nbsp;<a class="blue_color" <c:if test="<%=sale_in_progress<=0%>">onclick="javascript:return false;"</c:if>
                                   href="orderCenter.do?method=getTodoOrders&type=TODO_SALE_ORDERS&orderStatus=inProgress"><%=sale_in_progress%></a>&nbsp;条</td>
                    <td>
                        其中&nbsp;&nbsp;
                        <a class="blue_color"<c:if test="<%=sale_stocking<=0%>">onclick="javascript:return false;"</c:if>
                           href="orderCenter.do?method=getTodoOrders&type=TODO_SALE_ORDERS&orderStatus=<%=OrderStatus.STOCKING.toString()%>">备货中待发货：<%=sale_stocking%>条</a>&nbsp;&nbsp;
                        <a class="blue_color"<c:if test="<%=sale_dispatch<=0%>">onclick="javascript:return false;"</c:if>
                           href="orderCenter.do?method=getTodoOrders&type=TODO_SALE_ORDERS&orderStatus=<%=OrderStatus.DISPATCH.toString()%>">已发货待结算：<%=sale_dispatch%>条</a>&nbsp;&nbsp;
                        <a class="blue_color"<c:if test="<%=sale_sale_debt_done<=0%>">onclick="javascript:return false;"</c:if>
                           href="orderCenter.do?method=getTodoOrders&type=TODO_SALE_ORDERS&orderStatus=<%=OrderStatus.SALE_DEBT_DONE.toString()%>">欠款结算：<%=sale_sale_debt_done%>条</a>
                    </td>
                </tr>
            </table>
        </div>
        <form id="todoOrdersSearchForm" action="orderCenter.do?method=getTodoOrders">
            <input type="hidden" id="type" value="TODO_SALE_ORDERS" />
            <div class="divTit">下单时间：<input id="startTimeStr" type="text" class="txt" readonly="readonly" value="<%=startTimeStr%>" />&nbsp;至&nbsp;
                <input id="endTimeStr" type="text" class="txt" readonly="readonly" value="<%=endTimeStr%>" /></div>
            <div class="divTit">客户：<input id="customerName" type="text" class="txt" value="<%=customerName%>" /></div>
            <div class="divTit">单据号：<input id="receiptNo" type="text" class="txt" value="<%=receiptNo%>" /></div>
            <div class="divTit">状态：
                <select id="select_orderStatus" class="selTit" style="width:150px;">
                    <option value="allTodo">所有待办销售单</option>
                    <option value="inProgress" <%if("inProgress".equals(orderStatus)){%>selected="selected"<%}%>>处理中的销售单</option>
<%
    List<OrderStatus> statusList = (List<OrderStatus>)request.getAttribute("statusList");
    if(CollectionUtil.isNotEmpty(statusList)){
        for(int i=0;i<statusList.size();i++){
%>
                    <option value="<%=statusList.get(i).toString()%>" <%if(statusList.get(i).toString().equals(orderStatus)){%>selected="selected"<%}%>><%=statusList.get(i).getName()%></option>
<%
        }
    }
%>
                    <option value="all" <%if("all".equals(orderStatus)){%>selected="selected"<%}%>>所有销售单</option>
                </select>
            </div>
            <input type="hidden" id="orderStatus"/>
            <input type="button" id="todoOrdersSearchBtn" onfocus="this.blur();" class="btn_search" />
        </form>

        <div class="i_height"></div>
        <table class="divSlip" style="color: #ffffff; width:1003px">
            <tr class="titleBg" style="line-height: 32px;">
                <td style="width:75px; padding-left:10px;">商品编号</td>
                <td style="width:94px;">品名</td>
                <td style="width:94px;">品牌/产地</td>
                <td style="width:92px;">规格</td>
                <td style="width:92px;">型号</td>
                <td style="width:94px;">车辆品牌</td>
                <td style="width:94px;">车型</td>
                <td style="width:65px;">上架价格</td>
                <td style="width:75px;">成交价</td>
                <td style="width:70px;">销售数量</td>
                <td style="width:50px;">单位</td>
                <td>金额</td>
            </tr>
        </table>

<%
    List<SalesOrderDTO> orderDTOList = (List<SalesOrderDTO>)request.getAttribute("orderDTOList");
    List<Map<Long,ProductDTO>> productDTOMapList = (List<Map<Long,ProductDTO>>)request.getAttribute("productDTOMapList");
    Map<Long,ExpressDTO> expressDTOMap = (Map<Long,ExpressDTO>)request.getAttribute("expressDTOMap");
    Map<Long,OrderStatus> purchaseOrderStatusMap = (Map<Long,OrderStatus>)request.getAttribute("purchaseOrderStatusMap");

    if(CollectionUtil.isNotEmpty(orderDTOList)){
        for(int i=0;i<orderDTOList.size();i++){
%>
        <div class="i_height"></div>
        <table cellpadding="0" cellspacing="0" class="tabSlip J-orderDiv" id="tab_slip_<%=i%>">
            <col width="90">
            <col width="95">
            <col width="95">
            <col width="95">
            <col width="95">
            <col width="95">
            <col width="95">
            <col width="68">
            <col width="75">
            <col width="70">
            <col width="55">
            <col/>
            <tr class="titleBg">
                <td colspan="12">

                    <div class="titLbl">单号：<a class="reciptNo" target="_blank" href="sale.do?method=toOnlineSalesOrder&menu-uid=WEB.TXN.SALE_MANAGE.SALE&salesOrderId=<%=orderDTOList.get(i).getId()%>"><%=orderDTOList.get(i).getReceiptNo()%></a>&nbsp;
                        <%
                            if(orderDTOList.get(i).isShortage()){
                        %>
                        <bcgogo:permission>
                            <bcgogo:if permissions="WEB.TXN.PURCHASE_MANAGE.STORAGE"  resourceType="menu">
                                <a style="color:red;" class="shortageOperateVerfier" href="#" onclick="toShotageInventory('<%=orderDTOList.get(i).getId()%>',this)">缺料</a>
                            </bcgogo:if>
                            <bcgogo:else>
                                <a style="color:#787777;" class="shortageOperateVerfier" href="#">缺料</a>
                            </bcgogo:else>
                        </bcgogo:permission>
                        <%
                            }
                        %>
                        <input type="hidden" class="J-orderId" value="<%=orderDTOList.get(i).getPurchaseOrderId()%>" />
                    </div>
                    <div class="titLbl">下单时间：<span><%=DateUtil.convertDateLongToDateString("yyyy-MM-dd kk:mm:ss", orderDTOList.get(i).getCreationDate())%></span></div>
                    <div class="titLbl">客户：
                       <bcgogo:permission>
                            <bcgogo:if permissions="WEB.CUSTOMER_MANAGER.CUSTOMER_DATA">
                                <a class="todoSupplier" href="unitlink.do?method=customer&customerId=<%=orderDTOList.get(i).getCustomerId()%>"><%=orderDTOList.get(i).getCustomer()%></a>
                            </bcgogo:if>
                            <bcgogo:else>
                                 <a><%=orderDTOList.get(i).getCustomer()%></a>
                            </bcgogo:else>
                        </bcgogo:permission>
                    </div>
                    <div class="titLbl">状态：
                        <%
                            if(orderDTOList.get(i).getStatus().equals(OrderStatus.SALE_DONE) && (null != orderDTOList.get(i).getDebt() && orderDTOList.get(i).getDebt()>0))
                            {
                        %>
                        <label class="yellow_color"><%=OrderStatus.SALE_DEBT_DONE.getName()%></label>
                        <%
                        }
                        else
                        {
                        %>
                        <label class="yellow_color"><%=orderDTOList.get(i).getStatus().getName()%></label>
                        <%
                            }

                        %>
                    </div>
                    <a class="up" id="clickShow_<%=i%>">更多</a>
            <%
                //单据状态决定按钮状态
                if(orderDTOList.get(i).getStatus().equals(OrderStatus.PENDING)){

            %>
                    <bcgogo:permission>
                        <bcgogo:if permissions="WEB.TXN.SALE_MANAGE.SALE" resourceType="menu">
                            <a class="btns" onclick="refuseSale('<%=orderDTOList.get(i).getId()%>',this)">拒&nbsp;绝</a>
                            <a class="btns" onclick="acceptSale('<%=orderDTOList.get(i).getId()%>','<%=orderDTOList.get(i).getPurchaseVestDate()%>',this)">接&nbsp;受</a>
                        </bcgogo:if>
                        <bcgogo:else>
                            <a class="btns" style="color: #787777 ">拒&nbsp;绝</a>
                            <a class="btns" style="color: #787777 ">接&nbsp;受</a>
                        </bcgogo:else>
                    </bcgogo:permission>
            <%
                }
                if(orderDTOList.get(i).getStatus().equals(OrderStatus.STOCKING)){
            %>
                    <bcgogo:permission>
                        <bcgogo:if permissions="WEB.TXN.SALE_MANAGE.SALE.CANCEL">
                            <a class="btns stopSaleBtn" onclick="stopSale('<%=orderDTOList.get(i).getId()%>','<%=purchaseOrderStatusMap.get(orderDTOList.get(i).getId())%>',this)">终止销售</a>
                        </bcgogo:if>
                        <bcgogo:else>
                            <a class="btns stopSaleBtn" style="color: #787777">终止销售</a>
                        </bcgogo:else>
                    </bcgogo:permission>


                    <bcgogo:permission>
                        <bcgogo:if permissions="WEB.TXN.SALE_MANAGE.SEND_OUT">
                            <a class="btns dispatchSale" onclick="dispatchSale('<%=orderDTOList.get(i).getId()%>',this)">发&nbsp;货</a>
                        </bcgogo:if>
                        <bcgogo:else>
                            <a class="btns dispatchSale" style="color: #787777">发&nbsp;货</a>
                        </bcgogo:else>
                    </bcgogo:permission>
            <%
                }
                if(orderDTOList.get(i).getStatus().equals(OrderStatus.DISPATCH)){
            %>
                    <bcgogo:permission>
                        <bcgogo:if permissions="WEB.TXN.SALE_MANAGE.SALE.CANCEL">
                            <a class="btns stopSaleBtn" onclick="stopSale('<%=orderDTOList.get(i).getId()%>','<%=purchaseOrderStatusMap.get(orderDTOList.get(i).getId())%>',this)">终止销售</a>
                        </bcgogo:if>
                        <bcgogo:else>
                            <a class="btns stopSaleBtn" style="color: #787777">终止销售</a>
                        </bcgogo:else>
                    </bcgogo:permission>
                    <bcgogo:permission>
                        <bcgogo:if permissions="WEB.TXN.SALE_MANAGE.SALE" resourceType="menu">
                            <a class="btns" onclick="settleSale('<%=orderDTOList.get(i).getId()%>',this)">结&nbsp;算</a>
                        </bcgogo:if>
                        <bcgogo:else>
                            <a class="btns" style="color: #787777">结&nbsp;算</a>
                        </bcgogo:else>
                    </bcgogo:permission>

            <%
                }
                if(orderDTOList.get(i).getStatus().equals(OrderStatus.SALE_DONE) && (null != orderDTOList.get(i).getDebt() && orderDTOList.get(i).getDebt().doubleValue()>0)){
            %>
                    <bcgogo:permission>
                        <bcgogo:if permissions="WEB.TXN.SALE_MANAGE.SALE.CANCEL">
                            <a class="btns" onclick="repealSale('<%=orderDTOList.get(i).getId()%>',this)">作&nbsp;废</a>
                        </bcgogo:if>
                        <bcgogo:else>
                            <a class="btns" style="color: #787777">作&nbsp;废</a>
                        </bcgogo:else>
                    </bcgogo:permission>
                    <bcgogo:permission>
                        <bcgogo:if permissions="WEB.TXN.SALE_MANAGE.SALE">
                            <a class="btns" onclick="debtSettleSale('<%=orderDTOList.get(i).getId()%>',this)">结&nbsp;算</a>
                        </bcgogo:if>
                        <bcgogo:else>
                            <a class="btns" style="color: #787777">结&nbsp;算</a>
                        </bcgogo:else>
                    </bcgogo:permission>

            <%
                }
                if(orderDTOList.get(i).getStatus().equals(OrderStatus.SALE_DONE) && (null==orderDTOList.get(i).getDebt() || orderDTOList.get(i).getDebt().doubleValue()==0)){
            %>
                    <bcgogo:permission>
                        <bcgogo:if permissions="WEB.TXN.SALE_MANAGE.SALE.CANCEL">
                            <a class="btns" onclick="repealSale('<%=orderDTOList.get(i).getId()%>',this)">作&nbsp;废</a>
                        </bcgogo:if>
                        <bcgogo:else>
                            <a class="btns" style="color: #787777">作&nbsp;废</a>
                        </bcgogo:else>
                    </bcgogo:permission>
            <%
                }
            %>
                </td>
            </tr>
<%
            Map<Long,ProductDTO> productDTOMap = productDTOMapList.get(i);
            for(int j=0;j<orderDTOList.get(i).getItemDTOs().length;j++){
                SalesOrderItemDTO itemDTO = orderDTOList.get(i).getItemDTOs()[j];
                ProductDTO productDTO = productDTOMap.get(itemDTO.getProductId());
%>
            <tr class="data_tr_<%=i%> table-row-original item" style="display:none;">
                <td style="padding-left:10px;"><%=StringUtil.isEmpty(productDTO.getCommodityCode())?"":productDTO.getCommodityCode()%></td>
                <td><%=productDTO.getName()%><span style="display:none" class="j_supplierProductId" ><%=productDTO.getProductLocalInfoId()%></span></td>
                <td><%=StringUtil.isEmpty(productDTO.getBrand())?"——":productDTO.getBrand()%></td>
                <td><%=StringUtil.isEmpty(productDTO.getSpec())?"——":productDTO.getSpec()%></td>
                <td><%=StringUtil.isEmpty(productDTO.getModel())?"——":productDTO.getModel()%></td>
                <td><%=StringUtil.isEmpty(productDTO.getVehicleBrand())?"——":productDTO.getVehicleBrand()%></td>
                <td><%=StringUtil.isEmpty(productDTO.getVehicleModel())?"——":productDTO.getVehicleModel()%></td>
                <td title="<%=itemDTO.getPrice()%>"><span class='limit-span <%=(NumberUtil.compareDouble(itemDTO.getPrice(), itemDTO.getQuotedPrice()) ?"":"oldPrice")%>'><%=NumberUtil.round(itemDTO.getQuotedPrice(),NumberUtil.MONEY_PRECISION)%></span></td>
                <%
                    if(itemDTO.getQuotedPreBuyOrderItemId()!=null){
                %>
                <td class="quotedPreBuyPrice">
                   <span title="<%=NumberUtil.round(itemDTO.getPrice(),NumberUtil.MONEY_PRECISION)%>" class="limit-span"><%=NumberUtil.round(itemDTO.getPrice(),NumberUtil.MONEY_PRECISION)%></span>
                </td>
                <%
                    }else if (!itemDTO.getCustomPriceFlag() && !StringUtil.isEmpty(itemDTO.getPromotionsId()) && itemDTO.getQuotedPreBuyOrderItemId()==null) {
                %>
                <td class="promotionPrice J-priceInfo">
                    <span class="clearfix">
                        <span class="promotion">
                            <em class="cuxi">
                                <%=NumberUtil.round(itemDTO.getPrice(),NumberUtil.MONEY_PRECISION)%>
                            </em>
                            <div class="alert" style="overflow: visible;display: none;">
                                <div class="ti_top"></div>
                                <div class="ti_body alertBody">
                                </div>
                                <div class="ti_bottom"></div>
                            </div>
                        </span>
                    </span>
                </td>
                <%
                }else if(itemDTO.getCustomPriceFlag() && !NumberUtil.compareDouble(itemDTO.getPrice(), itemDTO.getQuotedPrice()) && itemDTO.getQuotedPreBuyOrderItemId()==null){
                %>
                <td class="expectPrice J-priceInfo">
                    <span class="clearfix">
                        <span class="promotion">
                            <em class="cuxi">
                                <%=NumberUtil.round(itemDTO.getPrice(),NumberUtil.MONEY_PRECISION)%>
                            </em>
                            <div class="alert" style="overflow: visible;display: none;">
                                对方期望价
                            </div>
                        </span>
                    </span>
                </td>
                <%
                }else{
                %>
                <td><span title='<%=NumberUtil.round(itemDTO.getPrice(),NumberUtil.MONEY_PRECISION)%>' class="limit-span"><%=NumberUtil.round(itemDTO.getPrice(),NumberUtil.MONEY_PRECISION)%></span></td>
                <%
                    }
                %>
                <td><%=NumberUtil.round(itemDTO.getAmount(),1)%>
                    <%
                        //如果有缺料，则显示缺料数量
                        if ((orderDTOList.get(i).getStatus().equals(OrderStatus.PENDING) || orderDTOList.get(i).getStatus().equals(OrderStatus.STOCKING)) && com.bcgogo.utils.NumberUtil.doubleVal(itemDTO.getShortage()) > 0) {
                    %>

                    <bcgogo:permission>
                        <bcgogo:if permissions="WEB.TXN.PURCHASE_MANAGE.STORAGE"  resourceType="menu">
                            <a class="shortageOperateVerfier" style="color:red;" href="#" onclick="toShotageInventory('<%=orderDTOList.get(i).getId()%>',this)">缺料<%=itemDTO.getShortage()%></a>
                        </bcgogo:if>
                        <bcgogo:else>
                            <a class="shortageOperateVerfier" style="color: #787777;" href="#">缺料<%=itemDTO.getShortage()%></a>
                        </bcgogo:else>
                    </bcgogo:permission>
                    <%
                        }
                    %>

                </td>
                <td><%=StringUtil.isEmpty(itemDTO.getUnit())?"":itemDTO.getUnit()%></td>
                <td><%=NumberUtil.round(itemDTO.getTotal(),NumberUtil.MONEY_PRECISION)%></td>
            </tr>
<%
            }
%>
        </table>
        <div class="slipInfo">
        	合计商品<span><%=orderDTOList.get(i).getItemDTOs().length%></span>种&nbsp;|&nbsp;应收：<span><%=NumberUtil.round(orderDTOList.get(i).getTotal(),NumberUtil.MONEY_PRECISION)%></span>元
<%
            if(orderDTOList.get(i).getPromotionsInfoDTO()!=null && orderDTOList.get(i).getPromotionsInfoDTO().getPromotionsTotal()>0){
%>
            (<span style="margin:0 5px;">优惠总计： <%=orderDTOList.get(i).getPromotionsInfoDTO().getPromotionsTotal()%>元</span>)
<%
            }
            if(expressDTOMap!=null && expressDTOMap.containsKey(orderDTOList.get(i).getId())){
                ExpressDTO expressDTO = expressDTOMap.get(orderDTOList.get(i).getId());
%>
            <span style="float:right">物流：<%=expressDTO.getCompany()%>&nbsp;&nbsp;<%=expressDTO.getWaybills()%></span>
<%
            }
%>
        </div>
<%
        }
    }
%>
        <div id="todoSaleOrdersPage">
            <jsp:include page="/common/paging.jsp">
                <jsp:param name="url" value="orderCenter.do?method=getTodoOrders&type=TODO_SALE_ORDERS&startTimeStr=${startTimeStr}&endTimeStr=${endTimeStr}&customerName=${customerName}&receiptNo=${receiptNo}&orderStatus=${orderStatus}"></jsp:param>
            </jsp:include>
            <div class="clear"></div>
        </div>
    </div>
</div>

<div class="i_searchBrand" id="acceptDialog" title="确认接受提示" style="display:none; width:500px;">
        <h3>您确定要接受该销售吗？</h3>
        <form id="salesAcceptForm" target="_blank" method="post" action="sale.do?method=acceptSaleOrder">
            <table border="0" width="550">
                <bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.STOREHOUSE">
                    <tr>
                        <td align="right">仓库：</td>
                        <td>
                            <select id="storehouseId" name="storehouseId" style="width:140px;height:21px;border: 1px solid #BBBBBB;" autocomplete="off">
                                <option value="">——请选择仓库——</option>
                                <c:forEach items="${storeHouseDTOList}" var="item">
                                    <option value="${item.id}">${item.name}</option>
                                </c:forEach>
                            </select>
                        </td>
                    </tr>
                </bcgogo:hasPermission>
                <tr>
                    <td width="120" align="right">买家要求到货日期：</td>
                    <td><span id="pruchaseVestDate"></span></td>
                </tr>
                <tr>
                    <td align="right"><img src="images/star.jpg">预计交货日期：</td>
                    <td>
                        <input type="radio" name="dispatchDateRadio" value="today" checked="checked" />&nbsp;本日&nbsp;&nbsp;
                        <input type="radio" name="dispatchDateRadio" value="tomorrow" />&nbsp;明日&nbsp;&nbsp;
                        <input type="radio" name="dispatchDateRadio" value="innerThreeDays" />&nbsp;三天内&nbsp;&nbsp;
                        <input type="radio" name="dispatchDateRadio" value="definedDate" />&nbsp;自定义日期&nbsp;
                        <input id="definedDate" type="text" class="txt" style="width:75px;" readonly="readonly" />
                        <input id="dispatchDate" name="preDispatchDateStr" type="hidden"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="2">
                        <div class="btnClick" style="height:50px; line-height:50px">
                            <input type="hidden" id="salesOrderId"/>
                            <input type="button" id="acceptConfirmBtn" onfocus="this.blur();" value="确&nbsp;定">
                            <input type="button" id="acceptCancelBtn" onfocus="this.blur();" value="取&nbsp;消">
                        </div>
                    </td>
                </tr>
            </table>
        </form>
</div>

<div class="i_searchBrand" id="refuseReasonDialog" title="确认拒绝提示" style="display:none; width: 500px;">
    <h3>友情提示：您拒绝后，该采购请求将被驳回 ！</h3>
    <h3>您确定要拒绝该销售单吗？</h3>
    <form id="salesRefuseForm" target="_blank" method="post" action="sale.do?method=refuseSaleOrder">
        <table border="0" width="480">
            <tr>
                <td width="100" align="right"><img src="images/star.jpg">拒绝理由：</td>
                <td><textarea class="textarea" id="refuseMsg" name="refuseMsg" style="width:320px;" maxlength="500"></textarea></td>
            </tr>
            <tr>
                <td colspan="2">
                    <div class="btnClick" style="height:50px; line-height:50px">
                        <input type="hidden" id="refuse_id" name="id"/>
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
    <form id="salesRepealForm" target="_blank" method="post" action="sale.do?method=saleOrderRepeal">
        <table border="0" width="480">
            <tr>
                <td width="100" align="right"><img src="images/star.jpg">终止销售理由：</td>
                <td><textarea class="textarea" id="repealMsg" name="repealMsg" style="width:320px;" maxlength="500"></textarea></td>
            </tr>
            <tr>
                <td colspan="2">
                    <div class="btnClick" style="height:50px; line-height:50px">
                        <input type="hidden" id="repeal_id" name="salesOrderId"/>
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
    <form id="dispatchForm" target="_blank" method="post" action="sale.do?method=dispatchSaleOrder">
        <table border="0" width="480">
            <tr>
                <td width="100" align="right">物流公司：</td>
                <td><input type="text" id="company" name="company" style="width:320px;height:20px;" maxlength="40" /></td>
            </tr>
            <tr>
                <td width="100" align="right">运单号：</td>
                <td><input type="text" id="waybill_id" name="waybills" style="width:320px;height:20px;"  maxlength="100"/></td>
            </tr>
            <tr>
                <td width="100" align="right">备注：</td>
                <td><textarea id="dispatch_memo" name="dispatchMemo" style="width:320px;"  maxlength="500"></textarea></td>
            </tr>
            <tr>
                <td colspan="2">
                    <div class="btnClick" style="height:50px; line-height:50px">
                        <input type="hidden" id="dispatch_id" name="id"/>
                        <input type="button" id="dispatchConfirmBtn" onfocus="this.blur();" value="确&nbsp;定">
                        <input type="button" id="dispatchCancelBtn" onfocus="this.blur();" value="取&nbsp;消">
                    </div>
                </td>
            </tr>
        </table>
    </form>
</div>

<div id="mask"  style="display:block;position: absolute;">
</div>
<bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.STOREHOUSE">
    <jsp:include page="../../txn/common/selectStoreHouse.jsp"/>
</bcgogo:hasPermission>
<iframe id="iframe_PopupBox_2" style="position:absolute;z-index:5; left:400px; top:400px; display:none;"
        allowtransparency="true" width="1000px" height="650500px" frameborder="0" src=""></iframe>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>