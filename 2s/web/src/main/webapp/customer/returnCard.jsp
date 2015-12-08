<%@ page import="com.bcgogo.config.ConfigController" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
<title>退卡</title>

<link rel="stylesheet" type="text/css" href="styles/up<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/moreUserinfo<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/moreHistory<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/style.css"/>

<link rel="stylesheet" type="text/css" href="js/extension/jquery/plugin/jquery-ui/themes/redmond/jquery-ui-1.8.21.custom.css"/>
<link rel="stylesheet" type="text/css" href="js/extension/jquery/plugin/jquery-ui/themes/redmond/jquery.ui.timepicker-addon.css"/>
<link rel="stylesheet" type="text/css" href="js/components/themes/bcgogo-scanning<%=ConfigController.getBuildVersion()%>.css" />
<style>
  #ui-datepicker-div, .ui-datepicker {
    font-size: 90%;
  }

  .isDatepickerInited {

  }
</style>


<script type="text/javascript" src="js/extension/jquery/jquery-1.4.2.min.js"></script>
<script type="text/javascript" src="js/extension/jquery/plugin/jquery.validate.js"></script>
<script type="text/javascript" src="js/extension/jquery/plugin/jquery-ui/ui/jquery-ui-1.8.21.custom.min.js"></script>
<script type="text/javascript" src="js/extension/jquery/plugin/jquery-ui/ui/jquery.ui.timepicker-addon.js"></script>
<script type="text/javascript" src="js/extension/jquery/plugin/jquery-ui/ui/i18n/jquery.ui.datepicker-zh-CN.js"></script>
<script type="text/javascript" src="js/extension/json2/json2.js"></script>
<script type="text/javascript" src="js/base<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/application<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/basecommon<%=ConfigController.getBuildVersion()%>.js"></script>

<script type="text/javascript" src="js/module/ajaxQuery<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/module/bcgogoValidate<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/module/bcgogo-scanning<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/module/bcgogo-WebStorage<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/mask<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/common<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/vehiclenosolrheader<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/bcgogo<%=ConfigController.getBuildVersion()%>.js"></script>

<script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.js"></script>
<script type="text/javascript" src="js/uploadPreview.js"></script>
<script type="text/javascript" src="js/returnCard<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>

</head>
<body class="bodyMain" style="width:740px;">
<input type="hidden" id="pageLinkedFrom" value="${pageLinkedFrom}"/>
<form:form commandName="memberCardReturnDTO" id="memberCardReturnForm" action="member.do?method=saveReturnCard"
           method="post" name="thisform">
<form:hidden path="memberDTO.id" value="${memberDTO.id}" />
<form:hidden path="customerId" value="${customerDTO.id}" />
<form:hidden path="customerName" value="${customerDTO.name}" />
<form:hidden path="memberCardName" value="${memberDTO.type}" />
<form:hidden path="lastMemberCardOrderId" />
  
<div class="i_supplierInfo more_supplier i_buyCards">
  <div class="i_arrow"></div>
  <div class="i_upLeft"></div>
  <div class="i_upCenter i_two">
    <div class="i_note more_title">退卡</div>
    <div class="i_close" id="div_close"></div>
  </div>
  <div class="i_upRight"></div>
  <div class="i_upBody" style="height:520px;">
    <table cellpadding="0" cellspacing="0" class="table3 supplierTable">
      <col width="150" />
      <col width="200" />
      <col width="220" />
      <col />
      <tr>
        <td>客　户　名:<span>${customerDTO.name}</span></td>
        <td>
          会　员（卡）号:<span>${memberDTO.memberNo}<form:hidden path="memberNo" value="${memberDTO.memberNo}" /></span>
        </td>
        <td>会员卡类型:
          <span>${memberDTO.type}</span>
        </td>
      </tr>
      <tr>
        <td>
          上次购卡金额:<label style="color:red;">${memberCardReturnDTO.lastBuyTotal}<form:hidden path="lastBuyTotal" /></label></td>
        <td >上次购卡时间:<span style="color:red;">${memberCardReturnDTO.lastBuyDateStr}<form:hidden path="lastBuyDate" /></span></td>
        <%--<td>单据号:<span>${memberCardReturnDTO.orderNo}</span></td>--%>
      </tr>
    </table>
    <div class="clear height"></div>
    <table cellpadding="0" cellspacing="0" class="table2 tabMoney">
    <col />
    <col width="110" />
    <col width="110" />
    <tr class="trTitle">
      <td style="border-left:none;">项目</td>
      <td class="txt_right">上次储值</td>
      <td style="border-right:none;" class="txt_right">剩余</td>
    </tr>
    <tr>
      <td style="border-left:none;">储值金额</td>
      <td class="txt_right"><span>${memberCardReturnDTO.lastRecharge}
        <form:hidden path="memberCardReturnItemDTOs[0].lastRecharge" value="${memberCardReturnDTO.lastRecharge}" />
        <form:hidden path="lastRecharge" />
      </span></td>
      <td style="border-right:none;" class="txt_right"><span>${memberCardReturnDTO.memberBalance}
        <form:hidden path="memberCardReturnItemDTOs[0].memberBalance" value="${memberCardReturnDTO.memberBalance}" />
      <form:hidden path="memberBalance" /></span></td>
    </tr>
    </table>
    <div class="clear height"></div>
    <table cellpadding="0" cellspacing="0" class="table2 buy_tab">
    <col />
    <col  />
    <col  />
    <tr>
      <td style="border-left:none;">服务项目</td>
      <td class="txt_right">上次购买次数</td>
      <td class="txt_right" style="border-right:none;">剩余次数</td>
    </tr>
    <c:forEach items="${memberCardReturnDTO.memberCardReturnServiceDTOs}" var="memberService" varStatus="status">
    <tr>
      <td style="border-left:none;">
        ${memberService.serviceName}
        <form:hidden path="memberCardReturnServiceDTOs[${status.index}].serviceId" value="${memberService.serviceId}" />
        <form:hidden path="memberCardReturnServiceDTOs[${status.index}].lastBuyTimes" value="${memberService.lastBuyTimes}" />
        <form:hidden path="memberCardReturnServiceDTOs[${status.index}].usedTimes" value="${memberService.usedTimes}" />
        <form:hidden path="memberCardReturnServiceDTOs[${status.index}].remainTimes" value="${memberService.remainTimes}" />
      </td>
      <td class="txt_right">
        <c:choose>
          <c:when test="${memberService.lastBuyTimes==-1}">不限次</c:when>
          <c:otherwise>${memberService.lastBuyTimes}</c:otherwise>
        </c:choose>
      </td>
      <td class="txt_right" style="border-right:none;">
        <c:choose>
          <c:when test="${memberService.remainTimes==-1}">不限次</c:when>
          <c:otherwise>${memberService.remainTimes}</c:otherwise>
        </c:choose>
		   </td>
    </tr>
    </c:forEach>

    </table>
    <div class="clear height"></div>
    <div class="postTitle">
      <div style="float:left">付款结算</div>
      <div style="float:left;padding-left:420px;width:120px;">
        <label style="margin-right:10px;">导购员：${memberCardReturnDTO.salesMan}
          <form:hidden path="memberCardReturnItemDTOs[0].salesId" />
          <form:hidden path="memberCardReturnItemDTOs[0].salesMan" value="${memberCardReturnDTO.salesMan}" />
        </label>
      </div>
      <div style="float:left;padding-left:10px;">
        <label >操作员：${sessionScope.userName}
        <form:hidden path="executorId" value="${sessionScope.userId}" />
        </label>
      </div>
    </div>
    <table cellpadding="0" cellspacing="0" class="table3 supplierTable">
    <col width="200" />
    <col width="170" />
    <col width="170" />
    <col />
    <tr>
      <td style="font-size:14px;">退卡金额:
       <input type="text" class="tab_input" id="returnTotal" value="${memberCardReturnDTO.lastBuyTotal}" style="width:102px;" /></td>
    <!--  <td colspan="3">退入预收款:
          <input type="text" class="tab_input" value="300" style="width:100px;" />
      </td>-->
    </tr>
    <tr class="tr_bg tr_br">
      <td>现&nbsp;&nbsp;金:<input type="text" id="returnCash" name="receptionRecordDTO.cash" class="tab_input" style="width:120px;" /></td>
      <td></td>
      <td></td>
      <td></td>
    </tr>
    <tr class="tr_bg">
      <td>银行卡:<input type="text" id="returnBank" name="receptionRecordDTO.bankCard" class="tab_input" style="width:120px;" /></td>
      <td colspan="3" style="text-align:right; padding-right:40px;"><span class="words">实 付</span>:
        <input type="text" id="returnSettleAmount" name="receptionRecordDTO.amount" class="tab_input" value="${memberCardReturnDTO.lastBuyTotal}" style="width:100px;" /></td>
    </tr>
    <tr class="tr_bg">
      <td>
        支&nbsp;&nbsp;票:<input type="text" id="returnCheck" name="receptionRecordDTO.cheque" class="tab_input" style="width:120px;" />
        <div class="divNum">号&nbsp;&nbsp;码:<input type="text" id="returnCheckNo" name="receptionRecordDTO.chequeNo" class="tab_input" style="width:95px;" /></div>
      </td>
      <td></td>
      <td></td>
      <td></td>
    </tr>
    </table>
     <div class="clear"></div>
     <div class="height"></div>
     <div class="noticeWords">*双击更改付款方式</div>
     <div class="more_his">
     <!--  <div class="btnOperaer">
          导购员:<input type="text" style="width:50px;" class="tab_input" />
          操作员:<input type="text" style="width:50px;" class="tab_input" />

       </div>-->
      <label style="padding-right:10px;"><input id="print" type="checkbox" />打印</label>
       <input type="button" style="display:none" value="hiddenPrint" id="hiddenPrintBtn" />
      <input id="returnCardSubmitBtn" type="button" value="确认" onfocus="this.blur();" class="btn"/>
      <input id="cancelBtn" type="button" value="取消" onfocus="this.blur();" class="btn"/>
     </div>
     <div class="height"></div>
    </div>
    <div class="i_upBottom">
      <div class="i_upBottomLeft"></div>
      <div class="i_upBottomCenter"></div>
      <div class="i_upBottomRight"></div>
    </div>
</div>
</form:form>

</body>
</html>