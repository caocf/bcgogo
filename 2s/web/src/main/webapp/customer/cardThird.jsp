
<%--
  Created by IntelliJ IDEA.
  User: cfl
  Date: 12-7-3
  Time: 下午5:41
  
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
<link rel="stylesheet" type="text/css" href="styles/cardThird<%=ConfigController.getBuildVersion()%>.css"/>
<%@ include file="/WEB-INF/views/header_script.jsp" %>


<script type="text/javascript" src="js/txnbase<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/jsScroller<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/jsScrollbar<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/cardFirst<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/vehiclenosolrheader<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>

<script type="text/javascript">
defaultStorage.setItem(storageKey.MenuUid, "WEB.CUSTOMER_MANAGER.MEMBERSHIP_PACKAGE_MANAGER");
defaultStorage.setItem(storageKey.MenuCurrentItem,"设置");

var originalServiceName;
var displayComp = GLOBAL.Display
jQuery(document).ready(function() {

        jQuery(document).click(function(e) {
          var e = e || event;
          var target = e.srcElement || e.target;
        if (target.id != "div_serviceName" && target.id != "Scroller-Container_ServiceName" && target.className != "txt_input term" && target.className != "txt_card useTimes") {
            jQuery("#div_serviceName").hide();
          }
        });

    jQuery(".term").live("focus", function(event) {
          var obj = this;
        var jsonStr = [
            {
                'id': '无期限'
            },
            {
                'id': '1'
            },
            {
                'id': '2'
            },
            {
                'id': '3'
            },
            {
                'id': '4'
            },
            {
                'id': '5'
            },
            {
                'id': '6'
            },
            {
                'id': '7'
            },
            {
                'id': '8'
            },
            {
                'id': '9'
            },
            {
                'id': '10'
            },
            {
                'id': '11'
            },
            {
                'id': '12'
            },
            {
                'id': '13'
            },
            {
                'id': '14'
            },
            {
                'id': '15'
            }
        ]
        droplistLite.show({
          event:event,
          hiddenid:"serviceId",
          id:"id",
          name:"id",
          data:jsonStr
        });
        });

    jQuery(".useTimes").live("focus", function(event) {
          var obj = this;
        var jsonStr = [
            {
                'id': '不限次'
                },
            {
                'id': '10'
                },
            {
                'id': '20'
            },
            {
                'id': '30'
            },
            {
                'id': '50'
                }
        ];
        droplistLite.show({
          event:event,
          hiddenid:"serviceId",
          id:"id",
          name:"id",
          data:jsonStr
        });
    });

    $(".serviceName").live("click focus keyup", function(event) {
        event = event || event.which;
        var keyCode = event.keyCode;

        if (keyCode == 37 || keyCode == 38 || keyCode == 39 || keyCode == 40) {
            return;
              }

        var obj = this;

        droplistLite.show({
          event:event,
          hiddenid:"serviceId",
          id:"id",
          name:"name",
          data:"txn.do?method=searchService"
        });
    });

    jQuery("#complete").live("click", function() {

           var checkFlag = false;
           var ErrorMsg = "";

           jQuery(".item").each(function(i) {
             var obj = jQuery(this).find("input")[0];
             var flag = isServiceDataNoMessage(obj);
             if (!flag && flag != null) {
               checkFlag = true;
               return;
             }
           });

        if (checkFlag) {
               return;
           }

        if (jQuery(".item").size() >= 1) if (checkTheSame()) {
                ErrorMsg += "服务有重复内容或为空，请修改或删除。";
             }

        if (ErrorMsg != null && ErrorMsg != "") {
            nsDialog.jAlert(ErrorMsg);
           }
        else {
           jQuery("#memberCardForm").submit();
        }
         });
        bindAutoClearTimes();

});

function isServiceDataNoMessage(domObj) {
          var reg = /^[1-9]\d*|0$/;
          var idPrefix = domObj.id.split(".")[0];
          if (idPrefix == "" || idPrefix == null) {
              return null;
          }
          var serviceName = document.getElementById(idPrefix + ".serviceName").value;
          var times = document.getElementById(idPrefix + ".timesStr").value.toString();
          var term = document.getElementById(idPrefix + ".termStr").value.toString();

    if ((term == null || term == "") && (serviceName == null || serviceName == "") && (times == null || times == "")) {
              return true;
          }

          if (serviceName == null || serviceName == "") {
              alert("请选择或填写服务！");
              return false;
          }

          if (times == null || times == "") {
              alert("次数填写不正确！");
              return false;
          }

           if (term == null || term == "") {
              alert("期限填写不正确！");
              return false;
          }

    //  正则表达式判定整数
    if (times != "不限次") {
              var foo = APP_BCGOGO.Validator;
              if (!(foo.stringIsIntGreaterThanNegativeOne(times))) {
            alert("次数填写不正确！");
                  return false;
              }
          }

    if ("无期限" != $.trim(term) && "无限期" != $.trim(term)) {
        var foo = APP_BCGOGO.Validator;
        if (!(foo.stringIsInt(term))) {
            alert("期限填写不正确！");
            return false;
        }
    }
}

/**
      *  清空输入框变化，自动清空
       */

function bindAutoClearTimes() {
          $(".serviceName").live('focus', function() {
        originalServiceName = $(this).val();
          });
          $(".serviceName").live('blur', function() {
        if (originalServiceName != $(this).val()) $(this).parent().next().find("input").val("");
          });
}

function searchService(domObj) {
        var idPrefix = domObj.id.split(".")[0];
        var indexNum = idPrefix.substring(21);

    bcgogo.checksession({
                            "parentWindow": window.parent,
                            'iframe_PopupBox': $("iframe_PopupBox"),
                            'src': "member.do?method=showMemberCardService&index=" + indexNum
          });
}
</script>

</head>
<body class="bodyMain">
<%@ include file="/WEB-INF/views/header_html.jsp" %>

<div class="i_main clear">
  <div class="i_search">
        <jsp:include page="customerNavi.jsp">
            <jsp:param name="currPage" value="cardAdd"/>
    </jsp:include>
  </div>
    <div class="card_set clear"><label>卡类型选择</label><input type="button" value="第一步"/><label>金额设定</label><input
            type="button" value="第二步"/><label>消费项目</label><input type="button" value="第三步"
                                                                 class="choose_btn"/><label>完成</label></div>
  <div class="card_providing clear">
    <img src="images/memberCard.png"/>
    <label class="lbl_name"> ${memberCardDTO.name}</label>

    <div class="money_show">
      <label class="clear">购买金额：${memberCardDTO.price}元</label>
      <label class="clear">储值金额：${memberCardDTO.worth}元</label>
    </div>
    <form:form commandName="memberCardDTO" id="memberCardForm" action="member.do?method=saveCardSet"
               method="post" name="thisform">

    <form:hidden path="id" value="${memberCardDTO.id}"></form:hidden>
    <form:hidden path="type" value="${memberCardDTO.type}"></form:hidden>
    <form:hidden path="status" value="${memberCardDTO.status}"></form:hidden>
    <form:hidden path="name" value="${memberCardDTO.name}"></form:hidden>
    <form:hidden path="price" value="${memberCardDTO.price}"></form:hidden>
    <form:hidden path="worth" value="${memberCardDTO.worth}"></form:hidden>
    <form:hidden path="accumulatePoints" value="${memberCardDTO.accumulatePoints}"></form:hidden>
    <form:hidden path="serviceDiscount" value="${memberCardDTO.serviceDiscount}"></form:hidden>
    <form:hidden path="materialDiscount" value="${memberCardDTO.materialDiscount}"></form:hidden>
    <form:hidden path="worthTerm" value="${memberCardDTO.worthTerm}"></form:hidden>
    <form:hidden path="percentage" value="${memberCardDTO.percentage}"></form:hidden>
    <form:hidden path="percentageAmount" value="${memberCardDTO.percentageAmount}"></form:hidden>
    <table cellpadding="0" cellspacing="0" class="table2 tabPresent" id="table_productNo">
                <col width="300"/>
                <col width="100"/>
                <col/>
                <col width="50"/>
      <tr class="tb_title">
        <td>项目</td>
        <td>次数</td>
        <td>期限</td>
        <td>操作<input class="opera2" type="button" style="display:none;"></td>
      </tr>
      <c:forEach items="${memberCardDTO.memberCardServiceDTOs}" var="memberCardServiceDTO" varStatus="status">
        <tr class="item">
          <td>
                            <form:hidden path="memberCardServiceDTOs[${status.index}].serviceId"
                                         value="${memberCardServiceDTO.serviceId}"/>
                            <form:input path="memberCardServiceDTOs[${status.index}].serviceName"
                                        cssClass="txt_card serviceName textbox" value="${memberCardServiceDTO.serviceName}"
                                        autocomplete="off"/>
          </td>
          <td class="times">
               <%--<c:choose>--%>
              <%--<c:when test="${memberCardServiceDTO.times==-1}">--%>
                <%--<form:input path="memberCardServiceDTOs[${status.index}].times" autocomplete="off" maxlength="8" class="txt_card useTimes" value="不限次"/>--%>
              <%--</c:when>--%>
              <%--<c:otherwise>--%>
                            <form:input path="memberCardServiceDTOs[${status.index}].timesStr" autocomplete="off"
                                        maxlength="8" cssClass="txt_card useTimes textbox"
                                        value="${memberCardServiceDTO.timesStr}"/>
              <%--</c:otherwise>--%>
            <%--</c:choose>--%>
          </td>
          <td class="qixian">
            <%--<c:choose>--%>
              <%--<c:when test="${memberCardServiceDTO.term==-1}">--%>
                <%--<form:input path="memberCardServiceDTOs[${status.index}].term" autocomplete="off" readOnly="true" class="txt_input term" value="无限期" />--%>
              <%--</c:when>--%>
              <%--<c:otherwise>--%>
                            <form:input path="memberCardServiceDTOs[${status.index}].termStr" autocomplete="off"
                                         cssClass="txt_card useTimes textbox" value="${memberCardServiceDTO.termStr}"/>
              <%--</c:otherwise>--%>
            <%--</c:choose>--%>
                            <label>月</label>
          </td>
          <td style="">
            <input class="opera1" type="button" id="memberCardServiceDTOs${status.index}.opera1Btn">
          </td>
        </tr>
      </c:forEach>

    </table>

    <div class="clear" style="height:1px\9;"></div>
    </form:form>
    <input type="button" value="设置完成" class="input_next" id="complete"/>
  </div>
    <iframe id="iframe_PopupBox" width="850px" scrolling="no" height="450px" frameborder="0" src=""
            allowtransparency="true" style="position:absolute;z-index:9; left:200px; top:200px; display:none;">
  </iframe>
</div>

<div id="div_serviceName" class="i_scroll" style="display:none;width:150px;">
  <div class="Scroller-Container" id="Scroller-Container_ServiceName">
  </div>
</div>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>