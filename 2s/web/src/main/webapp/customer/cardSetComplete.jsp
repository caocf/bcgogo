
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
    <link rel="stylesheet" type="text/css" href="styles/cardThird<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/cardThird<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/memberSet<%=ConfigController.getBuildVersion()%>.css"/>
    <%@ include file="/WEB-INF/views/header_script.jsp"%>
    
    <%--<script type="text/javascript" src="js/script<%=ConfigController.getBuildVersion()%>.js"></script>--%>
    <script type="text/javascript" src="js/txnbase<%=ConfigController.getBuildVersion()%>.js"></script>
    <%--<script type="text/javascript" src="js/dragiframe<%=ConfigController.getBuildVersion()%>.js"></script>--%>
    <script type="text/javascript" src="js/jsScroller<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/jsScrollbar<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/cardFirst<%=ConfigController.getBuildVersion()%>.js"></script>

    <script type="text/javascript">
        defaultStorage.setItem(storageKey.MenuUid, "WEB.CUSTOMER_MANAGER.MEMBERSHIP_PACKAGE_MANAGER");

      jQuery(document).ready(function(){
        jQuery(".btnOpera1").live("click",function(){
            var id=this.id;
            nsDialog.jConfirm("确认删除会员卡？", null, function (yes) {
            if (yes) {
                if(id == null || id==""){
                    nsDialog.jAlert("数据库中无此记录");
                    return false;
                }
                window.location = "member.do?method=disableMemberCard&id="+id;
                }
            });
        });

        jQuery(".btnAdd").live("click",function(){
          window.location = "member.do?method=toCardFirst";
        });
      });
    </script>

</head>
<body class="bodyMain">
<%@ include file="/WEB-INF/views/header_html.jsp" %>

<div class="i_main clear" >
  <div class="i_search">
    <jsp:include page="customerNavi.jsp" >
      <jsp:param name="currPage" value="cardManage" />
    </jsp:include>
  </div>
  <div class="basicMember">
      <div class="height"></div>
      <div class="basicSet">
        会员卡种类
          <bcgogo:hasPermission permissions="WEB.CUSTOMER_MANAGER.MEMBERSHIP_PACKAGE_SETTING">
        <input type="button" class="btnAdd" onfocus="this.blur();" value="会员套餐设置" />
          </bcgogo:hasPermission>
      </div>
      <div class="height"></div>
        <c:forEach items="${memberCardDTOs}" var="memberCardDTO" varStatus="status">
        <div class="washCard">
        <bcgogo:hasPermission permissions="WEB.CUSTOMER_MANAGER.MEMBERSHIP_PACKAGE_DELETE">
        <input type="button" class="btnOpera1" onfocus="this.blur();" id="${memberCardDTO.id}"/>
        </bcgogo:hasPermission>
        <%--<input type="hidden" class="memberCardDTOId" value="${memberCardDTO.id}"/>--%>
        <c:if test="${memberCardDTO.sort == 4}">
          <img src="images/clearCard.png" />
        </c:if>
        <c:if test="${memberCardDTO.sort == 3}">
          <img src="images/yinCard.png" />
        </c:if>
        <c:if test="${memberCardDTO.sort == 2}">
          <img src="images/jinCard.png" />
        </c:if>
        <c:if test="${memberCardDTO.sort == 1}">
          <img src="images/vipCard.png" />
        </c:if>
          <c:if test="${memberCardDTO.sort == 0}">
            <img src="images/memberCard.png" />
          </c:if>
          <div class="cardWords">
          <lable>${memberCardDTO.name}
            <%--<c:if test="${memberCardDTO.type=='timeCard'}">(计次卡)</c:if>--%>
            <%--<c:if test="${memberCardDTO.type=='valueCard'}">(储值卡)</c:if>--%>
          </lable>
          <input type="text" class="txtPrice txtMoney" value="${memberCardDTO.price}" readonly="true"/>，
          <c:if test="${memberCardDTO.type=='valueCard'}">
          储值<input type="text" class="txtPrice txtMoney" value="${memberCardDTO.worth}" readonly="true"/>，
          </c:if>
          <c:forEach items="${memberCardDTO.memberCardServiceDTOs}" var="memberCardServiceDTO" varStatus="status">

            <c:if test="${memberCardServiceDTO.times ==-1 && memberCardServiceDTO.term !=-1}">
              <input type="text" class="txtPrice" value="${memberCardServiceDTO.serviceName}${memberCardServiceDTO.term}月内不限次" title="${memberCardServiceDTO.serviceName}${memberCardServiceDTO.term}月内不限次" readonly="true"/>,
            </c:if>
            <c:if test="${memberCardServiceDTO.times !=-1 && memberCardServiceDTO.term ==-1}">
              <input type="text" class="txtPrice" value="${memberCardServiceDTO.serviceName}${memberCardServiceDTO.times}次不限期" title="${memberCardServiceDTO.serviceName}${memberCardServiceDTO.times}次不限期" readonly="true"/>,
            </c:if>
            <c:if test="${memberCardServiceDTO.times ==-1 && memberCardServiceDTO.term ==-1}">
              <input type="text" class="txtPrice" value="${memberCardServiceDTO.serviceName}不限次不限期" title="${memberCardServiceDTO.serviceName}不限次不限期" readonly="true"/>,
            </c:if>
            <c:if test="${memberCardServiceDTO.times !=-1 && memberCardServiceDTO.term !=-1}">
              <input type="text" class="txtPrice" value="${memberCardServiceDTO.serviceName}${memberCardServiceDTO.term}月内${memberCardServiceDTO.times}次" title="${memberCardServiceDTO.serviceName}${memberCardServiceDTO.term}月内${memberCardServiceDTO.times}次" readonly="true"/>,
            </c:if>
          </c:forEach>
        </div>
        </div>
        <div class="height"></div>
        </c:forEach>


      <div class="height"></div>
      <div class="height"></div>

  </div>
  <div id="mask"  style="display:block;position: absolute;">
  </div>

  <iframe id="iframe_PopupBox" style="position:absolute;z-index:5; left:400px; top:400px; display:none;" allowtransparency="true" width="630px" height="450px" frameborder="0" src=""></iframe>

</div>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>