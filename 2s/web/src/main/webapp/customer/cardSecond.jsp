
<%--
  Created by IntelliJ IDEA.
  User: cfl
  Date: 12-7-3
  Time: 下午3:26
  To change this template use File | Settings | File Templates.
--%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>会员套餐</title>

    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/moreHistory<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/cardFirst<%=ConfigController.getBuildVersion()%>.css"/>

    <%@ include file="/WEB-INF/views/header_script.jsp"%>

    <script type="text/javascript" src="js/script<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/txnbase<%=ConfigController.getBuildVersion()%>.js"></script>
    <%--<script type="text/javascript" src="js/dragiframe<%=ConfigController.getBuildVersion()%>.js"></script>--%>
    <script type="text/javascript" src="js/jsScroller<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/jsScrollbar<%=ConfigController.getBuildVersion()%>.js"></script>
    <%--<script type="text/javascript" src="js/cardFirst<%=ConfigController.getBuildVersion()%>.js"></script>--%>

    <script type="text/javascript">
        defaultStorage.setItem(storageKey.MenuUid, "WEB.CUSTOMER_MANAGER.MEMBERSHIP_PACKAGE_MANAGER");
        defaultStorage.setItem(storageKey.MenuCurrentItem,"设置");

    jQuery(document).ready(function(){
//      if(jQuery("#typeHid").val()  == 'timeCard')
//      {
//        document.getElementById("worth").readOnly=true;
//      }
      var reg = /^\d+(\.{0,1}\d+){0,1}$/ ;
      jQuery("#price").bind("blur",function(){
        if(jQuery("#price").val() != "" && jQuery("#price").val() != null)
        {
          if(!reg.test(jQuery("#price").val()))
          {
            nsDialog.jAlert("请输入正确的价格",null,function(){
            jQuery("#price").val("");
            jQuery("#price").focus();
            jQuery("#price").select();
            });
          }
        }

      });

      jQuery("#worth").bind("blur",function(){
        if(jQuery("#worth").val() != "" && jQuery("#worth").val() != null)
        {
          if(!reg.test(jQuery("#worth").val()))
          {
            nsDialog.jAlert("请输入正确的价格",null,function(){
            jQuery("#worth").val("");
            jQuery("#worth").focus();
            jQuery("#worth").select();
            });
          }
        }
      });

      jQuery("#percentageAmount").bind("blur",function(){
        if(jQuery("#percentageAmount").val() != "" && jQuery("#percentageAmount").val() != null)
        {
          if(!reg.test(jQuery("#percentageAmount").val()))
          {
            nsDialog.jAlert("请输入正确的价格",null,function(){
            jQuery("#percentageAmount").val("");
            jQuery("#percentageAmount").focus();
            jQuery("#percentageAmount").select();
            });
          }
        }
      });

      jQuery("#toThird").bind("click",function(){
        //是否要对空做验证
//        if(jQuery("#"))
        jQuery("#memberCardForm").submit();
      });

      jQuery("#price,#worth,#percentageAmount").keyup(function() {
        if(jQuery(this).val() != APP_BCGOGO.StringFilter.inputtingPriceFilter(this.value, 2)){
        jQuery(this).val( APP_BCGOGO.StringFilter.inputtingPriceFilter(this.value, 2) );
        }
      });
    });

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
    <form:form commandName="memberCardDTO" id="memberCardForm" action="member.do?method=toCardThird"
               method="post" name="thisform">
    <form:hidden path="name" value="${memberCardDTO.name}"></form:hidden>
    <input type="hidden" id = "type" name= "type" value="${memberCardDTO.type}" />
    <ul class="main_show">
        <li class="setCard" ><label>卡类型选择</label><input type="button" value="第一步" /><label>金额设定</label><input type="button" value="第二步" class="choose_btn"/><label>消费项目</label><input type="button" value="第三步"/><label>完成</label></li>
        <li class="card_show" ><img src="images/memberCard.png" width="36px" height="23px;"/><label>

          &nbsp;&nbsp;&nbsp;&nbsp;购买金额:</label><input type="text" class="txt_second" name="price" id="price" value="${memberCardDTO.price}" autocomplete="off"/><label>元
          &nbsp;&nbsp;&nbsp;&nbsp;储值金额:</label><input type="text" autocomplete="off" class="txt_second" name="worth" id="worth" value="${memberCardDTO.worth}"/><label>元</label>
         <label style="margin-left:20px;">员工售卡提成:</label><input type="text" class="txt_second" autocomplete="off" name="percentageAmount" id="percentageAmount" value="${memberCardDTO.percentageAmount}"/><label>元</label></li>
        <li class="next_show"><input type="button" value="下一步" id="toThird"/></li>
    </ul>
    </form:form>
    <div class="clear"></div>
    <input type="hidden" id="typeHid" value="${memberCardDTO.type}"/>
  </div>
</div>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>