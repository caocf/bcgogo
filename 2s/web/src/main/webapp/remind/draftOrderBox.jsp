
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
  <title>单据草稿箱</title>
  <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
  <link rel="stylesheet" type="text/css" href="styles/draftOrder<%=ConfigController.getBuildVersion()%>.css"/>
  <link rel="stylesheet" type="text/css" href="styles/newTodo<%=ConfigController.getBuildVersion()%>.css"/>
  <%@include file="/WEB-INF/views/header_script.jsp" %>
  <script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.min.js"></script>
  <script type="text/javascript" src="js/utils/tableUtil.js"></script>
  <script type="text/javascript" src="js/draftOrderBox<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript">


  </script>
</head>
<body class="bodyMain">
<%@ include file="/sms/enterPhone.jsp" %>
<%@include file="/WEB-INF/views/header_html.jsp" %>

<div class="s_main i_main clear">
  <div class="i_search">
    <div class="i_searchTitle">单据草稿箱</div>
  </div>

  <div class="i_mainNewTo clear">
    <div class="i_mainRight" id="i_mainRight">
      <form:form commandName="draftOrderSearchDTO" id="draftOrderSearchForm"
                 action="draft.do?method=getDraftOrders" method="post"
                 name="thisform">
        <%--<form:hidden path="orderTypes" value=""/>--%>
        <form:hidden path="startPageNo" value="1"/>
        <div class="d_draft_seach">
          <span class="up_line"></span>
          <table class="t_search_draft">
            <col width="80"/>
            <col width="300"/>
            <col width="530"/>
            <col width="70"/>
            <tr class="titlle">
              <td style="text-align:right;">单据类型：</td>
              <td class="search_td clearfix" colspan="2">
                <input type="checkbox" id="allOrderTypesCbox" value="ALL"/><label
                  for="allOrderTypesCbox">全选</label>
                <bcgogo:hasPermission permissions="WEB.VEHICLE_CONSTRUCTION.CONSTRUCT.BASE">
                  <input type="checkbox" id="repairCbox" name="orderTypes" value="REPAIR"/><label
                    for="repairCbox">施工单</label>
                </bcgogo:hasPermission>
                <bcgogo:hasPermission permissions="WEB.TXN.PURCHASE_MANAGE.STORAGE" resourceType="menu">
                  <input type="checkbox" id="inverntoryCbox" name="orderTypes"
                         value="INVENTORY"/><label for="inverntoryCbox">入库单</label>
                </bcgogo:hasPermission>
                <bcgogo:hasPermission permissions="WEB.TXN.PURCHASE_MANAGE.PURCHASE" resourceType="menu">
                  <input type="checkbox" id="purchaseCbox" name="orderTypes" value="PURCHASE"/><label
                    for="purchaseCbox">采购单</label>
                </bcgogo:hasPermission>
                <bcgogo:hasPermission permissions="WEB.TXN.SALE_MANAGE.SALE" resourceType="menu">
                  <input type="checkbox" id="saleCbox" name="orderTypes" value="SALE"/><label
                    for="saleCbox">销售单</label>
                </bcgogo:hasPermission>
                <bcgogo:hasPermission permissions="WEB.TXN.PURCHASE_MANAGE.RETURN"  resourceType="menu">
                  <input type="checkbox" id="returnCbox" name="orderTypes" value="RETURN"/><label
                    for="returnCbox">入库退货单</label>
                </bcgogo:hasPermission>
                <bcgogo:hasPermission permissions="WEB.TXN.SALE_MANAGE.RETURN" resourceType="menu">
                  <input type="checkbox" id="salesReturnCbox" name="orderTypes"
                         value="SALE_RETURN"/><label for="salesReturnCbox">销售退货单</label>
                </bcgogo:hasPermission>
              </td>
              <td>
                <input type="button" value="查询" id="draftSearchBtn" class="buttonBig"
                       style="margin: 0 20px 0 0;"/></td>
            </tr>
            <tr>
              <td style="text-align:right;">保存日期：</td>
              <td>
                <form:input id="startTimeInput" type="text" readonly="true" cssClass="textbox selectTime" value="${draftOrderSearchDTO.startTime}" path="startTime"/> 至 <form:input id="endTimeInput" type="text" readonly="true" cssClass="textbox selectTime" value="${draftOrderSearchDTO.endTime}" path="endTime" />
              </td>
              <td></td>
              <td></td>
            </tr>
          </table>
          <span class="down_line"></span>
        </div>
      </form:form>
      <div class="clear"></div>
      <div class="d_draft_table">
        <table class="t_draft_stat">
          <col width="80"/>
          <col width="890"/>
          <tr class="orderTyperCountTitle">
            <td>共<a id="totalNum" href="#" orderType="ALL">0</a>条记录</td>
            <td>其中(施工单<a id="REPAIRNUM" href="#" orderType="REPAIR">0</a>条&nbsp;入库单<a id="INVENTORYNUM"
                                                                                      href="#"
                                                                                      orderType="INVENTORY">0</a>条&nbsp;
              采购单<a id="PURCHASENUM" href="#" orderType="PURCHASE">0</a>条&nbsp;销售单<a id="SALENUM" href="#"
                                                                                     orderType="SALE">0</a>条&nbsp;入库退货单<a
                  id="RETURNNUM" href="#" orderType="RETURN">0</a>条&nbsp;销售退货单<a id="SALE_RETURNNUM"
                                                                                 href="#"
                                                                                 orderType="SALE_RETURN">0</a>条)
            </td>
          </tr>
        </table>
        <table cellpadding="0" cellspacing="0" class="t_draft_table_box" flag="newPage" id="draft_table">
          <col width="30">
          <col width="100">
          <col width="130">
          <col width="120">
          <col width="100">
          <col width="100">
          <col width="100">
          <col width="200">
          <col width="250">
          <col width="90">
          <tr class="tab_title">
            <td class="first-padding">No</td>
            <td>单据号</td>
            <td>保存时间</td>
            <td>保存人</td>
            <td>单据类型</td>
            <td>客户/供应商</td>
            <td>车牌号</td>
            <td>施工内容</td>
            <td>材料</td>
            <td class="last-padding">操作</td>
          </tr>

        </table>
        <!--分页-->
        <div class="hidePageAJAX">
          <jsp:include page="/common/pageAJAX.jsp">
            <jsp:param name="url" value="draft.do?method=getDraftOrders"></jsp:param>
            <jsp:param name="data" value="{startPageNo:1,orderTypes:'ALL'}"></jsp:param>
            <jsp:param name="jsHandleJson" value="initALlDraftTableBox"></jsp:param>
            <jsp:param name="hide" value="hideComp"></jsp:param>
            <jsp:param name="dynamical" value="dynamical1"></jsp:param>
          </jsp:include>
        </div>
      </div>
    </div>
  </div>
</div>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>