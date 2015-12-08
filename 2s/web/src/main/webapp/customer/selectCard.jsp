<%@ page import="com.bcgogo.config.ConfigController" %>
<%--
  Created by IntelliJ IDEA.
  User: cfl
  Date: 12-7-9
  Time: 上午10:25
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>会员卡类型</title>
    <link rel="stylesheet" type="text/css" href="styles/up<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/moneyDetail<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/memberSet<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/selectCard<%=ConfigController.getBuildVersion()%>.css"/>
	<link rel="stylesheet" type="text/css" href="js/components/themes/bcgogo-scanning<%=ConfigController.getBuildVersion()%>.css" />

    <script type="text/javascript" src="js/extension/jquery/jquery-1.4.2.min.js"></script>
    <script type="text/javascript" src="js/base<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/application<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/module/ajaxQuery<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/module/bcgogo-scanning<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/module/bcgogo-WebStorage<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/bcgogo<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/mask<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/common<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">

        jQuery(document).ready(function() {
//      window.parent.addHandle(document.getElementById('div_selectCard'), window);
                        if($("#doMemberCard",window.parent.document))
           {
            $("#doMemberCard",window.parent.document).removeAttr("disabled");
           }
            if($("#gouka",window.parent.document))
            {
                $("#gouka",window.parent.document).removeAttr("disabled");
            }
            if (jQuery("#div_close") != null) {
                jQuery("#div_close").click(function() {
                    closeWindow();
                });
            }

            if (jQuery("#cancleBtn") != null) {
                jQuery("#cancleBtn").click(function() {
                    closeWindow();
                });
            }

            if($(".washCard").length <= 0) {
               $(".basicMember").css("height","40px");
            }
        });
        function closeWindow() {
            jQuery(window.parent.document).find("#mask").hide();
            jQuery(window.parent.document).find("#iframe_CardList").hide();

            if($("#doMemberCard",window.parent.document)[0])
            {
                $("#doMemberCard",window.parent.document).removeAttr("disabled");

            }

            if($("#gouka",window.parent.document)[0])
            {
                $("#gouka",window.parent.document).removeAttr("disabled");

            }

            try {
                $(window.parent.document.body).find("input[type='button']").eq(0).focus().blur();
            } catch(e) {
                ;
            }
        }

        function buyCard() {

            if($("#buyCard").attr("disabled")) {
                return;
            }

            var cardId = "";
            jQuery(".selRad").each(function(i) {
                if (this.checked) {
                    cardId = this.value;
                }
            });
            if (cardId == "") {
                return false;
            }

            if($("#pageName",window.parent.document) && $("#pageName",window.parent.document).val() == "uncleUser")
            {
                $("#memberCardId", parent.document).val(cardId);
                $("#memberCardId", parent.document).click();

                jQuery(window.parent.document).find("#iframe_CardList").hide();
                return;
            }
            var customerName = $("#customer", window.parent.document)[0]?$("#customer", window.parent.document).val():$("#customerName", window.parent.document).val();
            APP_BCGOGO.Net.syncPost({
                url: "customer.do?method=checkCustomerExistAndSave",
                data: {
                    customerName: customerName,
                    mobile: $("#mobile",window.parent.document).val(),
                    landLine: $("#landLine",window.parent.document).val(),
                    licenceNo: $("#licenceNo",window.parent.document).val(),
                    brand: $("#brand",window.parent.document).val(),
                    model: $("#model",window.parent.document).val(),
                    customerId: $("#customerId",window.parent.document).val(),
                    tsLog: 10000000000 * (1 + Math.random())
                },
                cache: false,
                dataType: "json",
                success: function(data) {
                    var msg = data.msg;
                    var id = "";
                    if("existGtOne" == msg) {
                        alert("存在多个客户同一个手机，请修改客户手机信息");
                        $("#buyCard").removeAttr("disabled");
                        return;
                    } else if("existOne" == msg) {
                        id = data.id;
                        $("#customerId",window.parent.document).val(id);
                        $("#memberCardId", parent.document).val(cardId);
                        $("#memberCardId", parent.document).click();

                        jQuery(window.parent.document).find("#iframe_CardList").hide();
                        return;
                    } else if("saveSuccess" == msg) {
                        id = data.id;
                        $("#customerId",window.parent.document).val(id);
                        $("#memberCardId", parent.document).val(cardId);
                        $("#memberCardId", parent.document).click();
                        jQuery(window.parent.document).find("#iframe_CardList").hide();
                        return;
                    } else if("saveError" == msg) {
                        alert("客户信息保存失败，请重新点击！");
                        $("#buyCard").removeAttr("disabled");
                        return;
                    } else {
                        alert("网络异常");
                        $("#buyCard").removeAttr("disabled");
                        return;
                    }
                },
                error: function() {
                    alert("网络异常");
                    $("#buyCard").removeAttr("disabled");
                    return;
                }
            });
        }

        function buycardNoCardId() {
            if($("#buycardNoCardId").attr("disabled")) {
                return;
            }
            if($("#pageName",window.parent.document) && $("#pageName",window.parent.document).val() == "uncleUser")
            {
                $("#memberCardId", parent.document).click();

                jQuery(window.parent.document).find("#iframe_CardList").hide();
                return;
            }
            var customerName = $("#customer", window.parent.document)[0]?$("#customer", window.parent.document).val():$("#customerName", window.parent.document).val();
            APP_BCGOGO.Net.syncPost({
                url: "customer.do?method=checkCustomerExistAndSave",
                data: {
                    customerName: customerName,
                    mobile: $("#mobile",window.parent.document).val(),
                    landLine: $("#landLine",window.parent.document).val(),
                    licenceNo: $("#licenceNo",window.parent.document).val(),
                    brand: $("#brand",window.parent.document).val(),
                    model: $("#model",window.parent.document).val(),
                    customerId: $("#customerId",window.parent.document).val(),
                    tsLog: 10000000000 * (1 + Math.random())
                },
                cache: false,
                dataType: "json",
                success: function(data) {
                    var msg = data.msg;
                    var id = "";
                    if("existGtOne" == msg) {
                        alert("存在多个客户同一个手机，请修改客户手机信息");
                        $("#buycardNoCardId").removeAttr("disabled");
                        return;
                    } else if("existOne" == msg) {
                        id = data.id;
                        $("#customerId",window.parent.document).val(id);
                        $("#memberCardId", parent.document).click();

                        jQuery(window.parent.document).find("#iframe_CardList").hide();
                        return;
                    } else if("saveSuccess" == msg) {
                        id = data.id;
                        $("#customerId",window.parent.document).val(id);
                        $("#memberCardId", parent.document).click();
                        jQuery(window.parent.document).find("#iframe_CardList").hide();
                        return;
                    } else if("saveError" == msg) {
                        alert("客户信息保存失败，请重新点击！");
                        $("#buycardNoCardId").removeAttr("disabled");
                        return;
                    } else {
                        alert("网络异常");
                        $("#buycardNoCardId").removeAttr("disabled");
                        return;
                    }
                },
                error: function() {
                    alert("网络异常");
                    $("#buycardNoCardId").removeAttr("disabled");
                    return;
                }
            });

        }
    </script>
</head>
<body style='overflow: hidden;'>
<input type="hidden" id="customerId" value="${customerId}"/>

<div class="i_searchBrand">
    <div class="i_arrow"></div>
    <div class="i_upLeft"></div>
    <div class="i_upCenter">
      <div class="i_note" id="div_selectCard">会员卡类型</div>
      <div class="i_close" id="div_close"></div>
    </div>
    <div class="i_upRight"></div>
    <div class="i_upBody">
      <div class="basicMember">

          <c:forEach items="${memberCardDTOs}" var="memberCardDTO" varStatus="status">
            <div class="washCard">
              <c:if test="${status.index ==0}">
               <input type="radio" class="selRad" name="radio" checked="checked" value="${memberCardDTO.id}"/>
              </c:if>
              <c:if test="${status.index !=0}">
               <input type="radio" class="selRad" name="radio" value="${memberCardDTO.id}"/>
              </c:if>
              <c:if test="${memberCardDTO.sort == 4}">
                        <img src="images/clearCard.png"/>
              </c:if>
              <c:if test="${memberCardDTO.sort == 3}">
                        <img src="images/yinCard.png"/>
              </c:if>
              <c:if test="${memberCardDTO.sort == 2}">
                        <img src="images/jinCard.png"/>
              </c:if>
              <c:if test="${memberCardDTO.sort == 1}">
                        <img src="images/vipCard.png"/>
              </c:if>
              <c:if test="${memberCardDTO.sort == 0}">
                        <img src="images/memberCard.png"/>
              </c:if>
              <div class="cardWords">
                <lable>${memberCardDTO.name}
                  <c:if test="${memberCardDTO.type=='timeCard'}">(计次卡)</c:if>
                  <c:if test="${memberCardDTO.type=='valueCard'}">(储值卡)</c:if>
                </lable>
                <input type="text" class="txtPrice" value="${memberCardDTO.price}" readonly="true"/>，
                <c:if test="${memberCardDTO.type=='valueCard'}">
                  储值<input type="text" class="txtPrice" value="${memberCardDTO.worth}" readonly="true"/>，
                </c:if>
                        <c:forEach items="${memberCardDTO.memberCardServiceDTOs}" var="memberCardServiceDTO"
                                   varStatus="status">

                  <c:if test="${memberCardServiceDTO.times ==-1 && memberCardServiceDTO.term !=-1}">
                                <input type="text" class="txtPrice"
                                       value="${memberCardServiceDTO.serviceName}${memberCardServiceDTO.term}月内不限次"
                                       title="${memberCardServiceDTO.serviceName}${memberCardServiceDTO.term}月内不限次"
                                       readonly="true"/>,
                  </c:if>
                  <c:if test="${memberCardServiceDTO.times !=-1 && memberCardServiceDTO.term ==-1}">
                                <input type="text" class="txtPrice"
                                       value="${memberCardServiceDTO.serviceName}${memberCardServiceDTO.times}次不限期"
                                       title="${memberCardServiceDTO.serviceName}${memberCardServiceDTO.times}次不限期"
                                       readonly="true"/>,
                  </c:if>
                  <c:if test="${memberCardServiceDTO.times ==-1 && memberCardServiceDTO.term ==-1}">
                                <input type="text" class="txtPrice" value="${memberCardServiceDTO.serviceName}不限次不限期"
                                       title="${memberCardServiceDTO.serviceName}不限次不限期" readonly="true"/>,
                  </c:if>
                  <c:if test="${memberCardServiceDTO.times !=-1 && memberCardServiceDTO.term !=-1}">
                                <input type="text" class="txtPrice"
                                       value="${memberCardServiceDTO.serviceName}${memberCardServiceDTO.term}月内${memberCardServiceDTO.times}次"
                                       title="${memberCardServiceDTO.serviceName}${memberCardServiceDTO.term}月内${memberCardServiceDTO.times}次"
                                       readonly="true"/>,
                  </c:if>
                </c:forEach>
              </div>
            </div>
            <div class="clear height"></div>
          </c:forEach>
      <div class="clear height"></div>

    </div>
      <div class="clear height"></div>
      <div class="btnInput">
            <div style="float:right;">
                <input type="button" onfocus="this.blur();" value="确认" id="buycard" onclick="buyCard()"/>
                <input type="button" onfocus="this.blur();" value="取消" id="cancleBtn"/>
      </div>
            <div style="display:block;float:left">
                <input type="button" onfocus="this.blur();" value="" id="buycardNoCardId" onclick="buycardNoCardId()" class="buycard"/>
            </div>
        </div>

  </div>
    <div class="i_upBottom">
      <div class="i_upBottomLeft"></div>
      <div class="i_upBottomCenter"></div>
      <div class="i_upBottomRight"></div>
    </div>
  <div id="mask" style="display:block;position: absolute;">
    <iframe id="iframe_PopupBox" style="position:absolute;z-index:7; left:200px; top:200px; display:none;"
            allowtransparency="true" width="850px" height="500px" frameborder="0" src=""></iframe>
    </div>
</div>
</body>
</html>