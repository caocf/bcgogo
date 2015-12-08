<%@ page import="com.bcgogo.enums.OrderStatus" %>
<%@ page import="com.bcgogo.product.dto.ProductDTO" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.bcgogo.enums.PromotionsEnum" %>
<%@ page import="com.bcgogo.txn.dto.*" %>
<%@ page import="com.bcgogo.utils.NumberUtil" %>
<%@ page import="com.bcgogo.utils.DateUtil" %>
<%@ page import="java.util.List" %>
<%@ page import="org.apache.commons.collections.CollectionUtils" %>
<%@ page import="com.bcgogo.common.StringUtil" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
  <title>订单列表</title>

  <link rel="stylesheet" type="text/css" href="styles/up<%=ConfigController.getBuildVersion()%>.css"/>
  <link rel="stylesheet" type="text/css" href="styles/addPlan<%=ConfigController.getBuildVersion()%>.css"/>
  <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
  <link rel="stylesheet" type="text/css" href="styles/moreHistory<%=ConfigController.getBuildVersion()%>.css"/>
  <link rel="stylesheet" type="text/css" href="styles/newTodo<%=ConfigController.getBuildVersion()%>.css"/>
  <link rel="stylesheet" type="text/css" href="styles/statistics<%=ConfigController.getBuildVersion()%>.css"/>
  <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
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

  <script type="text/javascript">
      defaultStorage.setItem(storageKey.MenuUid, "WEB.SCHEDULE.REMIND_ORDERS.PURCHASE");
      //按钮操作 待办采购单
      //修改
      function purchaseModify(orderId){
          window.open("RFbuy.do?method=show&id="+orderId);
      }
      //未被批发商接受前的作废
      function purchaseCancel(orderId){
          nsDialog.jConfirm("友情提示：采购单作废后，将不再有效，交易会被取消！您确定要作废该采购单吗？",null,function(returnVal){
              if(returnVal){
                  window.open("RFbuy.do?method=purchaseOrderRepeal&id="+orderId);
                  setTimeout(function(){
                      window.location.reload();
                  },5000);
              }
          });
      }
      //作废
      function purchaseRepeal(orderId){
          nsDialog.jConfirm("友情提示：作废后该采购单不再属于待办单据，只能通过查询找出！您确定要作废该采购单吗？",null,function(returnVal){
              if(returnVal){
                  window.open("RFbuy.do?method=purchaseOrderRepeal&id="+orderId);
                  setTimeout(function(){
                      window.location.reload();
                  },5000);
              }
          });
      }
       //在线采购退货
      function toOnlinePurchaseReturn(orderId){
          if (!GLOBAL.Lang.isEmpty(orderId) && GLOBAL.Lang.isNumber(orderId)) {
              window.open("onlineReturn.do?method=onlinePurchaseReturnEdit&purchaseOrderId=" + orderId,"_blank");
          }
      }
      //入库
      function purchaseStorage(supplierId,orderId){
          APP_BCGOGO.Net.syncPost({
                url: "storage.do?method=validatePurchaseOrder",
                dataType: "json",
                data: {
                    "purchaseOrderId": orderId
                },
                success: function(result) {
                    if (result.success) {
                        window.open("storage.do?method=getProducts&type=txn&supplierId="+supplierId+"&purchaseOrderId=" + orderId);
                    } else {
                        nsDialog.jAlert(result.msg, null, function(){
                            window.location.reload();
                        });
                    }
                },
                error: function() {
                    nsDialog.jAlert("验证时产生异常，请重试！");
                }
            });
      }
      $(function(){
          $("#todoPurchaseOrders").addClass("hover_yinye");
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
        <jsp:param name="currPage" value="purchase"/>
    </jsp:include>
<%
    String startTimeStr = (String)request.getAttribute("startTimeStr");
    String endTimeStr = (String)request.getAttribute("endTimeStr");
    String supplierName = (String)request.getAttribute("supplierName");
    String receiptNo = (String)request.getAttribute("receiptNo");
    String orderStatus = (String)request.getAttribute("orderStatus");
    //采购单
    Long purchase_today_new = (Long)request.getAttribute("purchase_today_new");
    Long purchase_early_new = (Long)request.getAttribute("purchase_early_new");
    Long purchase_new = (Long)request.getAttribute("purchase_new");
    Long purchase_seller_stock = (Long)request.getAttribute("purchase_seller_stock");
    Long purchase_seller_dispatch = (Long)request.getAttribute("purchase_seller_dispatch");
    Long purchase_seller_refused = (Long)request.getAttribute("purchase_seller_refused");
    Long purchase_seller_stop = (Long)request.getAttribute("purchase_seller_stop");
    Long purchase_in_progress = (Long)request.getAttribute("purchase_in_progress");
    Long purchase_today_done = (Long)request.getAttribute("purchase_today_done");
    Long purchase_early_done = (Long)request.getAttribute("purchase_early_done");
    Long purchase_done = (Long)request.getAttribute("purchase_done");
    //今天的时间段
    String todayTimeStr = DateUtil.convertDateLongToDateString("yyyy-MM-dd", System.currentTimeMillis());
    String yesterdayTimeStr = DateUtil.convertDateLongToDateString("yyyy-MM-dd", System.currentTimeMillis()-24*60*60*1000);
%>

    <div class="titBody">
        <div class="todoCountDiv">
            <table class="todoCountTable" width="100%">
                <tr>
                    <td width="10%" style="color:#FF5E04">新订单</td>
                    <td width="20%">共有&nbsp;<a class="blue_color" <c:if test="<%=purchase_new<=0%>">onclick="javascript:return false;"</c:if>
                                               href="orderCenter.do?method=getTodoOrders&type=TODO_PURCHASE_ORDERS&orderStatus=<%=OrderStatus.SELLER_PENDING.toString()%>"><%=purchase_new%></a>&nbsp;条待卖家处理</td>
                    <td>
                        其中&nbsp;&nbsp;
                        <a class="blue_color" <c:if test="<%=purchase_today_new<=0%>">onclick="javascript:return false;"</c:if>
                           href="orderCenter.do?method=getTodoOrders&type=TODO_PURCHASE_ORDERS&orderStatus=<%=OrderStatus.SELLER_PENDING.toString()%>&startTimeStr=<%=todayTimeStr%>">今日新增：<%=purchase_today_new%>条</a>&nbsp;&nbsp;
                        <a class="blue_color" <c:if test="<%=purchase_early_new<=0%>">onclick="javascript:return false;"</c:if>
                           href="orderCenter.do?method=getTodoOrders&type=TODO_PURCHASE_ORDERS&orderStatus=<%=OrderStatus.SELLER_PENDING.toString()%>&endTimeStr=<%=yesterdayTimeStr%>">往日新增：<%=purchase_early_new%>条</a>
                    </td>
                </tr>
                <tr>
                    <td style="color:#FF5E04">处理中订单</td>
                    <td>共有&nbsp;<a class="blue_color" <c:if test="<%=purchase_in_progress<=0%>">onclick="javascript:return false;"</c:if>
                                   href="orderCenter.do?method=getTodoOrders&type=TODO_PURCHASE_ORDERS&orderStatus=inProgress"><%=purchase_in_progress%></a>&nbsp;条</td>
                    <td>
                        其中&nbsp;&nbsp;
                        <a class="blue_color"<c:if test="<%=purchase_seller_stock<=0%>">onclick="javascript:return false;"</c:if>
                           href="orderCenter.do?method=getTodoOrders&type=TODO_PURCHASE_ORDERS&orderStatus=<%=OrderStatus.SELLER_STOCK.toString()%>">卖家备货中：<%=purchase_seller_stock%>条</a>&nbsp;&nbsp;
                        <a class="blue_color"<c:if test="<%=purchase_seller_dispatch<=0%>">onclick="javascript:return false;"</c:if>
                           href="orderCenter.do?method=getTodoOrders&type=TODO_PURCHASE_ORDERS&orderStatus=<%=OrderStatus.SELLER_DISPATCH.toString()%>">卖家已发货：<%=purchase_seller_dispatch%>条</a>&nbsp;&nbsp;
                        <a class="blue_color"<c:if test="<%=purchase_seller_stop<=0%>">onclick="javascript:return false;"</c:if>
                           href="orderCenter.do?method=getTodoOrders&type=TODO_PURCHASE_ORDERS&orderStatus=<%=OrderStatus.PURCHASE_SELLER_STOP.toString()%>">卖家中止交易：<%=purchase_seller_stop%>条</a>&nbsp;&nbsp;
                        <a class="blue_color"<c:if test="<%=purchase_seller_refused<=0%>">onclick="javascript:return false;"</c:if>
                           href="orderCenter.do?method=getTodoOrders&type=TODO_PURCHASE_ORDERS&orderStatus=<%=OrderStatus.SELLER_REFUSED.toString()%>">卖家拒绝销售：<%=purchase_seller_refused%>条</a>
                    </td>
                </tr>
                <tr>
                    <td style="color:#FF5E04">已入库</td>
                    <td>共有&nbsp;<a class="blue_color" <c:if test="<%=purchase_done<=0%>">onclick="javascript:return false;"</c:if>
                                   href="orderCenter.do?method=getTodoOrders&type=TODO_PURCHASE_ORDERS&orderStatus=<%=OrderStatus.PURCHASE_ORDER_DONE.toString()%>"><%=purchase_done%></a>&nbsp;条</td>
                    <td>
                        其中&nbsp;&nbsp;
                        <a class="blue_color" <c:if test="<%=purchase_today_done<=0%>">onclick="javascript:return false;"</c:if>
                           href="orderCenter.do?method=getTodoOrders&type=TODO_PURCHASE_ORDERS&orderStatus=<%=OrderStatus.PURCHASE_ORDER_DONE.toString()%>&startTimeStr=<%=todayTimeStr%>&timeField=inventoryVestDate">今日入库：<%=purchase_today_done%>条</a>&nbsp;&nbsp;
                        <a class="blue_color" <c:if test="<%=purchase_early_done<=0%>">onclick="javascript:return false;"</c:if>
                           href="orderCenter.do?method=getTodoOrders&type=TODO_PURCHASE_ORDERS&orderStatus=<%=OrderStatus.PURCHASE_ORDER_DONE.toString()%>&endTimeStr=<%=yesterdayTimeStr%>&timeField=inventoryVestDate">往日入库：<%=purchase_early_done%>条</a>
                    </td>
                </tr>
            </table>
        </div>
        <form id="todoOrdersSearchForm" action="orderCenter.do?method=getTodoOrders">
            <input type="hidden" id="type" value="TODO_PURCHASE_ORDERS" />
            <div class="divTit">下单时间：<input id="startTimeStr" type="text" class="txt" readonly="readonly" value="<%=startTimeStr%>" />&nbsp;至&nbsp;
                <input id="endTimeStr" type="text" class="txt" readonly="readonly" value="<%=endTimeStr%>" /></div>
            <div class="divTit">供应商：<input id="supplierName" type="text" class="txt" value="<%=supplierName%>" /></div>
            <div class="divTit">单据号：<input id="receiptNo" type="text" class="txt" value="<%=receiptNo%>" /></div>
            <div class="divTit">状态：
                <select id="select_orderStatus" class="selTit" style="width:150px;">
                    <option value="allTodo">所有待办采购单</option>
                    <option value="inProgress" <%if("inProgress".equals(orderStatus)){%>selected="selected"<%}%>>处理中的采购单</option>
<%
    List<OrderStatus> statusList = (List<OrderStatus>)request.getAttribute("statusList");
    if(CollectionUtils.isNotEmpty(statusList)){
        for(int i=0;i<statusList.size();i++){
%>
                    <option value="<%=statusList.get(i).toString()%>" <%if(statusList.get(i).toString().equals(orderStatus)){%>selected="selected"<%}%>><%=statusList.get(i).getName()%></option>
<%
        }
    }
%>
                    <option value="all" <%if("all".equals(orderStatus)){%>selected="selected"<%}%>>所有采购单</option>
                </select>
            </div>
            <input type="hidden" id="orderStatus"/>
            <input type="button" id="todoOrdersSearchBtn" onfocus="this.blur();" class="btn_search" />
        </form>

        <div class="i_height"></div>
        <table class="divSlip" style="color: #ffffff; width:1003px">
            <tr class="titleBg" style="line-height: 32px;">
                <td style="width:80px; padding-left:10px;">商品编号</td>
                <td style="width:95px;">品名</td>
                <td style="width:95px;">品牌/产地</td>
                <td style="width:95px;">规格</td>
                <td style="width:95px;">型号</td>
                <td style="width:95px;">车辆品牌</td>
                <td style="width:95px;">车型</td>
                <td style="width:60px;">上架价格</td>
                <td style="width:90px;">成交价</td>
                <td style="width:60px;">销售数量</td>
                <td style="width:50px;">单位</td>
                <td style="width:80px;">金额</td>
            </tr>
        </table>

<%
    List<PurchaseOrderDTO> orderDTOList = (List<PurchaseOrderDTO>)request.getAttribute("orderDTOList");
    Map<Long,PurchaseInventoryDTO> purchaseInventoryOrderMap = (Map<Long,PurchaseInventoryDTO>)request.getAttribute("purchaseInventoryOrderMap");
    if(CollectionUtils.isNotEmpty(orderDTOList)){
        int i=0;
        for(PurchaseOrderDTO purchaseOrderDTO : orderDTOList){

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
            <col width="60">
            <col width="90">
            <col width="60">
            <col width="50">
            <col width="80">
            <tr class="titleBg">
                <td colspan="12">
                    <div class="titLbl">单号：<a  class="reciptNo" target="_blank" href="RFbuy.do?method=show&id=<%=purchaseOrderDTO.getId()%>"><%=purchaseOrderDTO.getReceiptNo()%></a>
                        <input type="hidden" class="J-orderId" value="<%=purchaseOrderDTO.getId()%>" />
                    </div>
                    <div class="titLbl">下单时间：<span><%=DateUtil.convertDateLongToDateString("yyyy-MM-dd kk:mm:ss",purchaseOrderDTO.getCreationDate())%></span></div>
                    <div class="titLbl">供应商：<a class="todoSupplier" href="unitlink.do?method=supplier&supplierId=<%=purchaseOrderDTO.getSupplierId()%>"><%=purchaseOrderDTO.getSupplier()%></a></div>
                    <div class="titLbl">状态：<label class="yellow_color"><%=purchaseOrderDTO.getStatus().getName()%></label></div>
<%
                if(purchaseOrderDTO.getStatus().equals(OrderStatus.SELLER_STOCK) || purchaseOrderDTO.getStatus().equals(OrderStatus.SELLER_DISPATCH)){
%>
                    <div class="titLbl">预计送达时间：<span><%=purchaseOrderDTO.getPreDispatchDateStr()%></span></div>
<%
                }else if(purchaseOrderDTO.getStatus().equals(OrderStatus.PURCHASE_ORDER_DONE)){
                    PurchaseInventoryDTO purchaseInventoryDTO = purchaseInventoryOrderMap.get(purchaseOrderDTO.getId());
                    if(purchaseInventoryDTO!=null){
%>
                    <div class="titLbl">入库单号：<a target="_blank" href="storage.do?method=getPurchaseInventory&purchaseInventoryId=<%=purchaseInventoryDTO.getId()%>"><%=purchaseInventoryDTO.getReceiptNo()%></a></div>
<%
                    }
                }
%>
                    <a class="up" id="clickShow_<%=i%>">更多</a>
            <%
                //单据状态决定按钮状态
                if(purchaseOrderDTO.getStatus().equals(OrderStatus.SELLER_PENDING)){
            %>
                    <a class="btns" onclick="purchaseCancel('<%=purchaseOrderDTO.getId()%>')">作&nbsp;废</a>
                    <a class="btns" onclick="purchaseModify('<%=purchaseOrderDTO.getId()%>')">修&nbsp;改</a>
            <%
                }
                if(purchaseOrderDTO.getStatus().equals(OrderStatus.SELLER_STOCK)){
                    if(purchaseOrderDTO.isShortage()==false){
            %>
                    <a class="btns" onclick="purchaseStorage('<%=purchaseOrderDTO.getSupplierId()%>','<%=purchaseOrderDTO.getId()%>')">入&nbsp;库</a>
            <%
                    }
                }
                if(purchaseOrderDTO.getStatus().equals(OrderStatus.SELLER_DISPATCH) || purchaseOrderDTO.getStatus().equals(OrderStatus.PURCHASE_ORDER_WAITING)){
            %>
                    <a class="btns" onclick="purchaseStorage('<%=purchaseOrderDTO.getSupplierId()%>','<%=purchaseOrderDTO.getId()%>')">入&nbsp;库</a>
            <%
                }
                if(purchaseOrderDTO.getStatus().equals(OrderStatus.SELLER_REFUSED)){
            %>
                    <a class="btns" onclick="purchaseRepeal('<%=purchaseOrderDTO.getId()%>')">作&nbsp;废</a>
            <%
                }
                if(purchaseOrderDTO.getStatus().equals(OrderStatus.PURCHASE_SELLER_STOP)){
            %>
                    <a class="btns" onclick="purchaseRepeal('<%=purchaseOrderDTO.getId()%>')">作&nbsp;废</a>
            <%
                }
                if(purchaseOrderDTO.getStatus().equals(OrderStatus.PURCHASE_ORDER_DONE)){
                    if(purchaseOrderDTO.getSupplierShopId() != null){
            %>
                        <a class="btns" onclick="toOnlinePurchaseReturn('<%=purchaseOrderDTO.getId()%>')">退&nbsp;货</a>
            <%
                    }
                }
            %>
                </td>
            </tr>
<%
            for(PurchaseOrderItemDTO purchaseOrderItemDTO :purchaseOrderDTO.getItemDTOs()){
%>
            <tr class="data_tr_<%=i%> table-row-original item" style="display:none;">
                <td title="<%=purchaseOrderItemDTO.getCommodityCode()==null?"":purchaseOrderItemDTO.getCommodityCode()%>" style="padding-left:10px;"><span class="limit-span"><%=purchaseOrderItemDTO.getCommodityCode()==null?"":purchaseOrderItemDTO.getCommodityCode()%></span></td>
                <td title="<%=purchaseOrderItemDTO.getProductName()%>"><span class="limit-span"><%=purchaseOrderItemDTO.getProductName()%></span><span style="display:none" class="j_supplierProductId" ><%=purchaseOrderItemDTO.getSupplierProductId()%></span></td>
                <td title="<%=StringUtil.isEmpty(purchaseOrderItemDTO.getBrand())?"":purchaseOrderItemDTO.getBrand()%>"><span class="limit-span"><%=StringUtil.isEmpty(purchaseOrderItemDTO.getBrand())?"——":purchaseOrderItemDTO.getBrand()%></span></td>
                <td title="<%=StringUtil.isEmpty(purchaseOrderItemDTO.getSpec())?"":purchaseOrderItemDTO.getSpec()%>"><span class="limit-span"><%=StringUtil.isEmpty(purchaseOrderItemDTO.getSpec())?"——":purchaseOrderItemDTO.getSpec()%></span></td>
                <td title="<%=StringUtil.isEmpty(purchaseOrderItemDTO.getModel())?"":purchaseOrderItemDTO.getModel()%>"><span class="limit-span"><%=StringUtil.isEmpty(purchaseOrderItemDTO.getModel())?"——":purchaseOrderItemDTO.getModel()%></span></td>
                <td title="<%=StringUtil.isEmpty(purchaseOrderItemDTO.getVehicleBrand())?"":purchaseOrderItemDTO.getVehicleBrand()%>"><span class="limit-span"><%=StringUtil.isEmpty(purchaseOrderItemDTO.getVehicleBrand())?"——":purchaseOrderItemDTO.getVehicleBrand()%></span></td>
                <td title="<%=StringUtil.isEmpty(purchaseOrderItemDTO.getVehicleModel())?"":purchaseOrderItemDTO.getVehicleModel()%>"><span class="limit-span"><%=StringUtil.isEmpty(purchaseOrderItemDTO.getVehicleModel())?"——":purchaseOrderItemDTO.getVehicleModel()%></span></td>
                <td title="<%=purchaseOrderItemDTO.getPrice()%>"><span class='limit-span <%=(NumberUtil.compareDouble(purchaseOrderItemDTO.getPrice(),purchaseOrderItemDTO.getQuotedPrice())?"":"oldPrice")%>'><%=NumberUtil.round(purchaseOrderItemDTO.getQuotedPrice(),NumberUtil.MONEY_PRECISION)%></span></td>

                <%
                    if(purchaseOrderItemDTO.getQuotedPreBuyOrderItemId()!=null){
                %>
                    <td class="quotedPreBuyPrice">
                        <span title="<%=NumberUtil.round(purchaseOrderItemDTO.getPrice(),NumberUtil.MONEY_PRECISION)%>" class="limit-span"><%=NumberUtil.round(purchaseOrderItemDTO.getPrice(),NumberUtil.MONEY_PRECISION)%></span>
                    </td>
                <%
                    }else if (!purchaseOrderItemDTO.getCustomPriceFlag() && !StringUtil.isEmpty(purchaseOrderItemDTO.getPromotionsId()) && purchaseOrderItemDTO.getQuotedPreBuyOrderItemId()==null) {
                %>
                <td class="promotionPrice J-priceInfo">
                    <span class="clearfix">
                        <span class="promotion">
                            <em class="cuxi">
                                <%=NumberUtil.round(purchaseOrderItemDTO.getPrice(),NumberUtil.MONEY_PRECISION)%>
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
                }else if(purchaseOrderItemDTO.getCustomPriceFlag() && !NumberUtil.compareDouble(purchaseOrderItemDTO.getPrice(),purchaseOrderItemDTO.getQuotedPrice()) && purchaseOrderItemDTO.getQuotedPreBuyOrderItemId()==null){
                %>
                <td class="expectPrice">
                    <span title="<%=NumberUtil.round(purchaseOrderItemDTO.getPrice(),NumberUtil.MONEY_PRECISION)%>" class="limit-span"><%=NumberUtil.round(purchaseOrderItemDTO.getPrice(),NumberUtil.MONEY_PRECISION)%></span>
                </td>
                <%
                }else{
                %>
                <td><span title="<%=NumberUtil.round(purchaseOrderItemDTO.getPrice(),NumberUtil.MONEY_PRECISION)%>" class="limit-span"><%=NumberUtil.round(purchaseOrderItemDTO.getPrice(),NumberUtil.MONEY_PRECISION)%></span></td>
                <%
                    }
                %>

                <td title="<%=NumberUtil.round(purchaseOrderItemDTO.getAmount(),1)%>"><span class="limit-span"><%=NumberUtil.round(purchaseOrderItemDTO.getAmount(),1)%></span></td>
                <td title="<%=purchaseOrderItemDTO.getUnit()==null?"":purchaseOrderItemDTO.getUnit()%>"><span class="limit-span"><%=purchaseOrderItemDTO.getUnit()==null?"":purchaseOrderItemDTO.getUnit()%></span></td>
                <td title="<%=NumberUtil.round(purchaseOrderItemDTO.getTotal(),NumberUtil.MONEY_PRECISION)%>"><span class="limit-span"><%=NumberUtil.round(purchaseOrderItemDTO.getTotal(),NumberUtil.MONEY_PRECISION)%></span></td>
            </tr>
<%
            }
%>
        </table>
        <div class="slipInfo">
        	合计商品<span><%=purchaseOrderDTO.getItemDTOs().length%></span>种&nbsp;|&nbsp;应付：<span><%=NumberUtil.round(purchaseOrderDTO.getTotal(),NumberUtil.MONEY_PRECISION)%></span>元
  <%
    if(purchaseOrderDTO.getPromotionsInfoDTO()!=null&&NumberUtil.round(purchaseOrderDTO.getPromotionsInfoDTO().getPromotionsTotal())>0){
  %>
    (优惠总计：&nbsp <span><%=NumberUtil.round(purchaseOrderDTO.getPromotionsInfoDTO()==null?0:purchaseOrderDTO.getPromotionsInfoDTO().getPromotionsTotal(),NumberUtil.MONEY_PRECISION)%></span>元)
  <%
      }
  %>

<%
            if(purchaseOrderDTO.getExpressId()!=null){
%>
            <span style="float:right">物流：<%=purchaseOrderDTO.getCompany()%>&nbsp;&nbsp;<%=purchaseOrderDTO.getWaybills()%></span>
<%
            }
%>
        </div>
<%
            i++;
        }
    }
%>
        <div id="todoPurchaseOrdersPage">
            <jsp:include page="/common/paging.jsp">
                <jsp:param name="url" value="orderCenter.do?method=getTodoOrders&type=TODO_PURCHASE_ORDERS&startTimeStr=${startTimeStr}&endTimeStr=${endTimeStr}&supplierName=${supplierName}&receiptNo=${receiptNo}&orderStatus=${orderStatus}"></jsp:param>
            </jsp:include>
            <div class="clear"></div>
        </div>
    </div>
</div>

<div id="mask"  style="display:block;position: absolute;">
</div>

<iframe id="iframe_PopupBox" style="position:absolute;z-index:5; left:400px; top:400px; display:none;" allowtransparency="true" width="630px" height="450px" frameborder="0" src=""></iframe>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>