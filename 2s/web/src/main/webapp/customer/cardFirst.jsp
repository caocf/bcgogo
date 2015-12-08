
<%--
  Created by IntelliJ IDEA.
  User: cfl
  Date: 12-7-2
  Time: 下午4:06
  To change this template use File | Settings | File Templates.
--%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>会员套餐</title>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/moreHistory<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/cardFirst<%=ConfigController.getBuildVersion()%>.css"/>
    
    <%@ include file="/WEB-INF/views/header_script.jsp"%>
    <script type="text/javascript" src="js/invoicing<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/bcgogo<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/script<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/txnbase<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/jsScroller<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/jsScrollbar<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/cardFirst<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">
        defaultStorage.setItem(storageKey.MenuUid, "WEB.CUSTOMER_MANAGER.MEMBERSHIP_PACKAGE_MANAGER");
        defaultStorage.setItem(storageKey.MenuCurrentItem,"设置");
    </script>
</head>
<body class="bodyMain">
<%@ include file="/WEB-INF/views/header_html.jsp" %>

<div class="i_main clear" >
  <div class="i_search">
    <jsp:include page="customerNavi.jsp" >
      <jsp:param name="currPage" value="cardAdd" />
    </jsp:include>
  </div>
  <div class="card_main clear" >
    <form:form commandName="memberCardDTO" id="memberCardForm" action="member.do?method=toCardSecond" method="post" name="thisform">
    <ul class="main_show">
      <li class="setCard" ><label>卡类型选择</label><input type="button" value="第一步" class="choose_btn"/><label>金额设定</label><input type="button" value="第二步"/><label>消费项目</label><input type="button" value="第三步"/><label>完成</label></li>
      <li class="card_show"><input type="radio" name="name" value="洗车卡" checked="checked" id="washCard"/><img src="images/clearCard.png"/><label>洗车卡</label></li>
      <li class="card_show"><input type="radio" name="name" value="银卡" id="silverCard"/><img src="images/yinCard.png"/><label>银卡</label></li>
      <li class="card_show"><input type="radio" name="name" value="金卡" id="goldCard"/><img src="images/jinCard.png"/><label>金卡</label></li>
      <li class="card_show"><input type="radio" name="name" value="VIP卡" id="vipCard"/><img src="images/vipCard.png"/><label>VIP卡</label></li>
      <li class="radio_card"><input type="radio" name="name" id="customCard"/><label>自定义卡名</label><input type="text" class="type_ca" id="cardName" autocomplete="off"/>
          <%--<label>卡类型：</label>--%>
                             <input type="hidden" name="type" id="type" value="timeCard" />
      <input style="display:none"/>
        <%--<div class="stock_txtName">--%>
          <%--<div class="stock_txtLeft"></div>--%>
          <%--<div class="stock_txtBody" id="div_txtBody"><input type="text" value="计次卡" id="cardKind" class="stock_text" readonly="readonly"/></div>--%>
          <%--<div class="stock_txtClick"><input type="button" id="selectCardKindBtn" onfocus="this.blur();"/></div>--%>
          <%--<ul class="type_card">--%>
            <%--<li>计次卡</li>--%>
            <%--<li>储值卡</li>--%>
          <%--</ul>--%>
        <%--</div>--%>

      </li>
      <li class="next_show"><input type="button" id="nextStep" value="下一步"/></li>
    </ul>
    </form:form>
    <div class="clear"></div>
  </div>
</div>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>