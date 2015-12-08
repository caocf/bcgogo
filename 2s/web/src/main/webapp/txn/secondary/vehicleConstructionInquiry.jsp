<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>施工结算附表</title>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/secondary/vehicleConstructionInquiry<%=ConfigController.getBuildVersion()%>.css"/>
    <%@include file="/WEB-INF/views/header_script.jsp" %>
    <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/secondary/vehicleConstructionInquiry<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">
        defaultStorage.setItem(storageKey.MenuUid, "web_repair_order_secondary_inquiry");
    </script>
</head>
<body class="bodyMain">
<%@ include file="/WEB-INF/views/header_html.jsp" %>
<%@ include file="/common/messagePrompt.jsp" %>
<div class="i_main">
     <div class="body">
         <div>
             <h1 class="inlineBlock">施工结算附表</h1>
         </div>
         <form name="myForm" id="myForm">
             <input name="customerId" id="customerId" type="hidden" autocomplete="off">
             <div class="top">
                 <div class="title">结算附表查询</div>
                 <div class="content float_clear">
                     <div class="line float_left">
                         <div class="inlineBlock float_left">结算时间</div>
                         <div class="inlineBlock float_left">
                             <a class="dateSelect">昨天</a>
                             <a class="dateSelect">今天</a>
                             <a class="dateSelect">最近一周</a>
                             <a class="dateSelect">最近一月</a>
                             <a class="dateSelect">最近一年</a>
                             <input name="startDateStr" id="startDateStr" type="text" readonly="readonly" autocomplete="off">&#160;至&#160;<input name="endDateStr" id="endDateStr" autocomplete="off" value="${endDateStr}"  readonly="readonly">
                         </div>
                     </div>
                     <div class="line float_left">
                         <div class="inlineBlock float_left">单据信息</div>
                         <div class="inlineBlock float_left"><input id="customerInfo" name="customerInfo" placeholder="客户名/联系人/手机号/车牌号" autocomplete="off" style="width: 250px;" maxlength="50">&#160;&#160;<input name="receipt" placeholder="施工单号" autocomplete="off" style="width: 100px;" maxlength="20"></div>
                     </div>
                     <div class="line float_left">
                         <div class="inlineBlock float_left">单据类型</div>
                         <div class="inlineBlock float_left" >
                             <input name="statusStr" value="REPAIR_SETTLED" id="orderType1" type="checkbox" class="inlineBlock float_left" autocomplete="off"><label for="orderType1" class="inlineBlock float_left">已结算</label>
                             <input name="statusStr" value="REPAIR_DEBT" id="orderType2" type="checkbox" class="inlineBlock float_left" autocomplete="off"><label for="orderType2" class="inlineBlock float_left">欠款结算</label>
                             <input name="statusStr" value="REPAIR_REPEAL" id="orderType3" type="checkbox" class="inlineBlock float_left" autocomplete="off"><label for="orderType3" class="inlineBlock float_left">已作废</label>
                         </div>
                     </div>
                     <div class="line float_left" style="text-align: center;">
                         <a href="#" class="button" id="inquiry">查询</a>
                         <a href="#" class="a" id="reset">清空条件</a>
                     </div>
                 </div>
             </div>

             <div class="middle">
                 <div class="inlineBlock">共有<span id="count">${repairOrderSecondaryResponse.count}</span>条记录</div>
                 <div class="inlineBlock">单据总额：<span id="total">${repairOrderSecondaryResponse.total}</span>元</div>
                 <div class="inlineBlock">实收：<span id="income">${repairOrderSecondaryResponse.income}</span>元</div>
                 <div class="inlineBlock">挂账：<span id="debt">${repairOrderSecondaryResponse.debt}</span>元</div>
                 <div class="inlineBlock">优惠：<span id="discount">${repairOrderSecondaryResponse.discount}</span>元</div>
             </div>

             <div class="bottom float_clear">
                 <table cellpadding="0" cellspacing="0" width="100%">
                     <colgroup>
                         <col width="2%">
                         <col width="8%">
                         <col width="11%">
                         <col width="18%">
                         <col width="8%">
                         <col width="15%">
                         <col width="6%">
                         <col width="6%">
                         <col width="6%">
                         <col width="6%">
                         <col width="6%">
                         <col>
                     </colgroup>
                     <tr class="title">
                         <td>NO</td>
                         <td>单据号</td>
                         <td>进厂日期</td>
                         <td style="text-align: left;">客户信息</td>
                         <td>车牌号</td>
                         <td style="text-align: left;">车主信息</td>
                         <td>单据金额</td>
                         <td>实收</td>
                         <td>挂账</td>
                         <td>优惠</td>
                         <td style="text-align: left;">状态</td>
                         <td>操作</td>
                     </tr>
                 </table>
                 <div id="noSecondaryList" style="text-align:center;color: #444443;" class="hide">对不起！没有符合条件的对账单信息！</div>
                 <jsp:include page="/common/pageAJAX.jsp">
                     <jsp:param name="url" value="repairOrderSecondary.do?method=queryRepairOrderSecondary"></jsp:param>
                     <jsp:param name="dynamical" value="repairOrderSecondaryList"></jsp:param>
                     <jsp:param name="data" value="{startPageNo:'1',maxRows:15}"></jsp:param>
                     <jsp:param name="jsHandleJson" value="showList"></jsp:param>
                     <jsp:param name="display" value="none"></jsp:param>
                 </jsp:include>
             </div>
         </form>
     </div>
</div>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>