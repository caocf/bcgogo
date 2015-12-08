<%@ page import="com.bcgogo.enums.OrderStatus" %>
<%@ page import="com.bcgogo.product.dto.ProductDTO" %>
<%@ page import="com.bcgogo.txn.dto.PurchaseReturnDTO" %>
<%@ page import="com.bcgogo.txn.dto.PurchaseReturnItemDTO" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.bcgogo.utils.NumberUtil" %>
<%@ page import="com.bcgogo.utils.DateUtil" %>
<%@ page import="org.apache.commons.collections.CollectionUtils" %>
<%@ page import="java.util.List" %>
<%@ page import="com.bcgogo.common.StringUtil" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
  <title>汽配在线——订单中心——待办入库退货单</title>

  <link rel="stylesheet" type="text/css" href="styles/up<%=ConfigController.getBuildVersion()%>.css"/>
  <link rel="stylesheet" type="text/css" href="styles/addPlan<%=ConfigController.getBuildVersion()%>.css"/>
  <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
  <link rel="stylesheet" type="text/css" href="styles/moreHistory<%=ConfigController.getBuildVersion()%>.css"/>
  <link rel="stylesheet" type="text/css" href="styles/newTodo<%=ConfigController.getBuildVersion()%>.css"/>
  <link rel="stylesheet" type="text/css" href="styles/statistics<%=ConfigController.getBuildVersion()%>.css"/>
  <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
  <link rel="stylesheet" type="text/css" href="styles/orderCenter<%=ConfigController.getBuildVersion()%>.css"/>
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
  <script type="text/javascript" src="js/page/autoaccessoryonline/todoOrders<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/page/txn/txn<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript">
      defaultStorage.setItem(storageKey.MenuUid, "WEB.SCHEDULE.REMIND_ORDERS.PURCHASE_RETURN");
      //按钮操作 待办入库退货单
      //修改
      function purchaseReturnModify(orderId){
          window.open("onlineReturn.do?method=modifyReturnStorage&purchaseReturnId="+orderId);
      }
      //未被批发商接受前的作废
      function purchaseReturnCancel(orderId){
          if(APP_BCGOGO.Permission.Version.StoreHouse){//这个时候作废才会操作库存
              if (!validatorStoreHouseOrderRepeal($("#_toStorehouseId").val(),"RETURN",orderId)) {
                  return;
              }
          }
          repealOrder(orderId);
      }
      //selectStoreHouse.jsp中js  调用  名称不能修改
      function repealOrder(orderId){
          nsDialog.jConfirm("友情提示：入库退货单作废后，将不再有效，交易会被取消！您确定要作废该入库退货单吗？",null,function(returnVal){
              if(returnVal){
                  var url = "goodsReturn.do?method=repealReturnStorage&purchaseReturnId=" + orderId;
                  if(APP_BCGOGO.Permission.Version.StoreHouse){
                      url +="&toStorehouseId="+$("#_toStorehouseId").val();
                  }
                  window.open(url);
                  setTimeout(function(){
                      window.location.reload();
                  },5000);
              }
          });
      }
      //作废
      function purchaseReturnRepeal(orderId){
          nsDialog.jConfirm("友情提示：是否确认作废？",null,function(returnVal){
              if(returnVal){
                  window.open("goodsReturn.do?method=repealReturnStorage&purchaseReturnId="+orderId);
                  setTimeout(function(){
                      window.location.reload();
                  },5000);
              }
          });
      }
      //结算
      function purchaseReturnSettle(orderId){
          window.open("goodsReturn.do?method=showReturnStorageByPurchaseReturnId&purchaseReturnId="+orderId);
      }
      $(function(){
          $("#todoPurchaseReturnOrders").addClass("hover_yinye");
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
        <jsp:param name="currPage" value="purchaseReturn"/>
    </jsp:include>

<%
    String startTimeStr = (String)request.getAttribute("startTimeStr");
    String endTimeStr = (String)request.getAttribute("endTimeStr");
    String supplierName = (String)request.getAttribute("supplierName");
    String receiptNo = (String)request.getAttribute("receiptNo");
    String orderStatus = (String)request.getAttribute("orderStatus");
    //入库退货单
    Long purchase_return_today_new = (Long)request.getAttribute("purchase_return_today_new");
    Long purchase_return_early_new = (Long)request.getAttribute("purchase_return_early_new");
    Long purchase_return_new = (Long)request.getAttribute("purchase_return_new");
    Long purchase_return_seller_accept = (Long)request.getAttribute("purchase_return_seller_accept");
    Long purchase_return_seller_refused = (Long)request.getAttribute("purchase_return_seller_refused");
    Long purchase_return_in_progress = (Long)request.getAttribute("purchase_return_in_progress");
    //今天的时间段
    String todayTimeStr = DateUtil.convertDateLongToDateString("yyyy-MM-dd",System.currentTimeMillis());
    String yesterdayTimeStr = DateUtil.convertDateLongToDateString("yyyy-MM-dd", System.currentTimeMillis() - 24 * 60 * 60 * 1000);
%>

    <div class="titBody">
        <div class="todoCountDiv">
            <table class="todoCountTable" width="100%">
                <tr>
                    <td width="10%" style="color:#FF5E04">新退货单</td>
                    <td width="20%">共有&nbsp;<a class="blue_color" <c:if test="<%=purchase_return_new<=0%>">onclick="javascript:return false;"</c:if>
                                               href="orderCenter.do?method=getTodoOrders&type=TODO_PURCHASE_RETURN_ORDERS&orderStatus=<%=OrderStatus.SELLER_PENDING.toString()%>"><%=purchase_return_new%></a>&nbsp;条待卖家处理</td>
                    <td>
                        其中&nbsp;&nbsp;
                        <a class="blue_color" <c:if test="<%=purchase_return_today_new<=0%>">onclick="javascript:return false;"</c:if>
                           href="orderCenter.do?method=getTodoOrders&type=TODO_PURCHASE_RETURN_ORDERS&orderStatus=<%=OrderStatus.SELLER_PENDING.toString()%>&startTimeStr=<%=todayTimeStr%>">今日新增：<%=purchase_return_today_new%>条</a>&nbsp;&nbsp;
                        <a class="blue_color" <c:if test="<%=purchase_return_early_new<=0%>">onclick="javascript:return false;"</c:if>
                           href="orderCenter.do?method=getTodoOrders&type=TODO_PURCHASE_RETURN_ORDERS&orderStatus=<%=OrderStatus.SELLER_PENDING.toString()%>&endTimeStr=<%=yesterdayTimeStr%>">往日新增：<%=purchase_return_early_new%>条</a>
                    </td>
                </tr>
                <tr>
                    <td style="color:#FF5E04">处理中退货单</td>
                    <td>共有&nbsp;<a class="blue_color" <c:if test="<%=purchase_return_in_progress<=0%>">onclick="javascript:return false;"</c:if>
                                   href="orderCenter.do?method=getTodoOrders&type=TODO_PURCHASE_RETURN_ORDERS&orderStatus=inProgress"><%=purchase_return_in_progress%></a>&nbsp;条</td>
                    <td>
                        其中&nbsp;&nbsp;
                        <a class="blue_color" <c:if test="<%=purchase_return_seller_accept<=0%>">onclick="javascript:return false;"</c:if>
                           href="orderCenter.do?method=getTodoOrders&type=TODO_PURCHASE_RETURN_ORDERS&orderStatus=<%=OrderStatus.SELLER_ACCEPTED.toString()%>">卖家已接受：<%=purchase_return_seller_accept%>条</a>&nbsp;&nbsp;
                        <a class="blue_color" <c:if test="<%=purchase_return_seller_refused<=0%>">onclick="javascript:return false;"</c:if>
                           href="orderCenter.do?method=getTodoOrders&type=TODO_PURCHASE_RETURN_ORDERS&orderStatus=<%=OrderStatus.SELLER_REFUSED.toString()%>">卖家已拒绝：<%=purchase_return_seller_refused%>条</a>
                    </td>
                </tr>
            </table>
        </div>
        <form id="todoOrdersSearchForm" action="orderCenter.do?method=getTodoOrders">
            <input type="hidden" id="type" value="TODO_PURCHASE_RETURN_ORDERS" />
            <div class="divTit">下单时间：<input id="startTimeStr" type="text" class="txt" readonly="readonly" value="<%=startTimeStr%>" />&nbsp;至&nbsp;
                <input id="endTimeStr" type="text" class="txt" readonly="readonly" value="<%=endTimeStr%>" /></div>
            <div class="divTit">供应商：<input id="supplierName" type="text" class="txt" value="<%=supplierName%>" /></div>
            <div class="divTit">单据号：<input id="receiptNo" type="text" class="txt" value="<%=receiptNo%>" /></div>
            <div class="divTit">状态：
                <select id="select_orderStatus" class="selTit" style="width:150px;">
                    <option value="allTodo">所有待办入库退货单</option>
                    <option value="inProgress" <%if("inProgress".equals(orderStatus)){%>selected="selected"<%}%>>处理中的入库退货单</option>
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
                    <option value="all" <%if("all".equals(orderStatus)){%>selected="selected"<%}%>>所有入库退货单</option>
                </select>
            </div>
            <input type="hidden" id="orderStatus"/>
            <input type="button" id="todoOrdersSearchBtn" onfocus="this.blur();" class="btn_search" />
        </form>

        <div class="i_height"></div>
        <table class="divSlip" style="color: #ffffff; width:1003px">
            <tr class="titleBg" style="line-height: 32px;">
                <td style="width:75px; padding-left:10px;">商品编号</td>
                <td style="width:100px;">品名</td>
                <td style="width:100px;">品牌/产地</td>
                <td style="width:95px;">规格</td>
                <td style="width:97px;">型号</td>
                <td style="width:98px;">车辆品牌</td>
                <td style="width:100px;">车型</td>
                <td style="width:65px;">单价</td>
                <td style="width:100px;">销售数量</td>
                <td style="width:55px;">单位</td>
                <td>金额</td>
            </tr>
        </table>

<%
    List<PurchaseReturnDTO> orderDTOList = (List<PurchaseReturnDTO>)request.getAttribute("orderDTOList");
    List<Map<Long,ProductDTO>> productDTOMapList = (List<Map<Long,ProductDTO>>)request.getAttribute("productDTOMapList");
    if(CollectionUtils.isNotEmpty(orderDTOList)){
        for(int i=0;i<orderDTOList.size();i++){

%>
        <div class="i_height"></div>
        <table cellpadding="0" cellspacing="0" class="tabSlip" id="tab_slip_<%=i%>">
            <col width="90">
            <col width="100">
            <col width="100">
            <col width="100">
            <col width="100">
            <col width="100">
            <col width="100">
            <col width="70">
            <col width="100">
            <col width="55">
            <col>
            <tr class="titleBg">
                <td colspan="11">
                    <div class="titLbl">单号：
            <%
                if(orderDTOList.get(i).getStatus().equals(OrderStatus.SELLER_PENDING)){
            %>
                        <a target="_blank" class="reciptNo" href="onlineReturn.do?method=modifyReturnStorage&purchaseReturnId=<%=orderDTOList.get(i).getId()%>">
            <%
                }else{
            %>
                        <a target="_blank" class="reciptNo" href="goodsReturn.do?method=showReturnStorageByPurchaseReturnId&purchaseReturnId=<%=orderDTOList.get(i).getId()%>">
            <%
                }
            %>
                            <%=orderDTOList.get(i).getReceiptNo()%>
                        </a>
                    </div>
                    <div class="titLbl">下单时间：<span><%=DateUtil.convertDateLongToDateString("yyyy-MM-dd kk:mm:ss",orderDTOList.get(i).getCreationDate())%></span></div>
                    <div class="titLbl">供应商：<a class="todoSupplier" href="unitlink.do?method=supplier&supplierId=<%=orderDTOList.get(i).getSupplierId()%>"><%=orderDTOList.get(i).getSupplier()%></a></div>
                    <div class="titLbl">状态：<label class="yellow_color"><%=orderDTOList.get(i).getStatus().getName()%></label></div>
                    <a class="up" id="clickShow_<%=i%>">更多</a>
            <%
                //单据状态决定按钮状态
                if(orderDTOList.get(i).getStatus().equals(OrderStatus.SELLER_PENDING)){
            %>
                    <%--<a class="btns" target="_blank" href="RFbuy.do?method=show&id=<%=orderDTOList.get(i).getId()%>">修&nbsp;改</a>--%>
                    <%--<a class="btns" target="_blank" href="RFbuy.do?method=purchaseOrderRepeal&id=<%=orderDTOList.get(i).getId()%>">作&nbsp;废</a>--%>
                    <a class="btns" onclick="purchaseReturnCancel('<%=orderDTOList.get(i).getId()%>')">作&nbsp;废</a>
                    <a class="btns" onclick="purchaseReturnModify('<%=orderDTOList.get(i).getId()%>')">修&nbsp;改</a>
            <%
                }if(orderDTOList.get(i).getStatus().equals(OrderStatus.SELLER_ACCEPTED)){
            %>
                    <a class="btns" onclick="purchaseReturnSettle('<%=orderDTOList.get(i).getId()%>')">结&nbsp;算</a>
            <%
                }
                if(orderDTOList.get(i).getStatus().equals(OrderStatus.SELLER_REFUSED)){
            %>
                    <a class="btns" onclick="purchaseReturnRepeal('<%=orderDTOList.get(i).getId()%>')">作&nbsp;废</a>
            <%
                }
            %>
                </td>
            </tr>
<%
            Map<Long,ProductDTO> productDTOMap = productDTOMapList.get(i);
            for(int j=0;j<orderDTOList.get(i).getItemDTOs().length;j++){
                PurchaseReturnItemDTO itemDTO = orderDTOList.get(i).getItemDTOs()[j];
                ProductDTO productDTO = productDTOMap.get(itemDTO.getProductId());
%>
            <tr class="data_tr_<%=i%> table-row-original" style="display:none;">
                <td style="padding-left:10px;"><span class="limit-span"><%=productDTO.getCommodityCode()==null?"":productDTO.getCommodityCode()%></span></td>
                <td><span class="limit-span"><%=productDTO.getName()%></span></td>
                <td><span class="limit-span"><%=StringUtil.isEmpty(productDTO.getBrand())?"——":productDTO.getBrand()%></span></td>
                <td><span class="limit-span"><%=StringUtil.isEmpty(productDTO.getSpec())?"——":productDTO.getSpec()%></span></td>
                <td><span class="limit-span"><%=StringUtil.isEmpty(productDTO.getModel())?"——":productDTO.getModel()%></span></td>
                <td><span class="limit-span"><%=StringUtil.isEmpty(productDTO.getVehicleBrand())?"——":productDTO.getVehicleBrand()%></span></td>
                <td><span class="limit-span"><%=StringUtil.isEmpty(productDTO.getVehicleModel())?"——":productDTO.getVehicleModel()%></span></td>
                <td><span class="limit-span"><%=NumberUtil.round(itemDTO.getPrice(),NumberUtil.MONEY_PRECISION)%></span></td>
                <td><span class="limit-span"><%=NumberUtil.round(itemDTO.getAmount(),1)%></span></td>
                <td><span class="limit-span"><%=itemDTO.getUnit()==null?"——":itemDTO.getUnit()%></span></td>
                <td><span class="limit-span"><%=NumberUtil.round(itemDTO.getTotal(),NumberUtil.MONEY_PRECISION)%></span></td>
            </tr>
<%
            }
%>
        </table>
        <div class="slipInfo">
        	合计商品<span><%=orderDTOList.get(i).getItemDTOs().length%></span>种&nbsp;|&nbsp;应收：<span><%=NumberUtil.round(orderDTOList.get(i).getTotal(),NumberUtil.MONEY_PRECISION)%></span>元
        </div>
<%
        }
    }
%>
        <div id="todoPurchaseReturnOrdersPage">
            <jsp:include page="/common/paging.jsp">
                <jsp:param name="url" value="orderCenter.do?method=getTodoOrders&type=TODO_PURCHASE_RETURN_ORDERS&startTimeStr=${startTimeStr}&endTimeStr=${endTimeStr}&supplierName=${supplierName}&receiptNo=${receiptNo}&orderStatus=${orderStatus}"></jsp:param>
            </jsp:include>
            <div class="clear"></div>
        </div>
    </div>
</div>

<div id="mask"  style="display:block;position: absolute;"></div>
<bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.STOREHOUSE">
    <jsp:include page="../../txn/common/selectStoreHouse.jsp"/>
</bcgogo:hasPermission>
<iframe id="iframe_PopupBox" style="position:absolute;z-index:5; left:400px; top:400px; display:none;" allowtransparency="true" width="630px" height="450px" frameborder="0" src=""></iframe>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>