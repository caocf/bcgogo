<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>促销管理</title>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
    <%@include file="/WEB-INF/views/header_script.jsp" %>

    <script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.js"></script>
    <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/page/autoaccessoryonline/promotions<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">
        defaultStorage.setItem(storageKey.MenuUid, "PROMOTIONS_MANAGER_MENU");
        <c:choose>
        <c:when test="${not empty promotionsDTO.idStr}">
        defaultStorage.setItem(storageKey.MenuCurrentItem,"编辑");
        </c:when>
        <c:otherwise>
        defaultStorage.setItem(storageKey.MenuCurrentItem,"新增");
        </c:otherwise>
        </c:choose>
    </script>
</head>
<body class="bodyMain">
<%@ include file="/WEB-INF/views/header_html.jsp" %>
<%@ include file="/common/messagePrompt.jsp" %>
<div class="i_main clear">
    <div class="clear i_height"></div>
    <div class="titBody">
        <jsp:include page="../supplyCenterLeftNavi.jsp">
            <jsp:param name="currPage" value="promotions"/>
            <jsp:param name="biMenu" value="promotionManager"/>
        </jsp:include>

        <div class="bodyLeft">
            <h3 class="title">创建促销——特价商品</h3>
            <div class="cuSearch">
                <div class="clear chartStep blue_color">
                    <span class="yellow_color">1、促销设置</span>
                    <a class="stepImg"></a>
                    <span>2、添加上架商品</span>
                    <a class="stepImg"></a>
                    <span>3、促销中的商品</span>
                    <a class="stepImg"></a>
                    <span>4、推广您的促销</span>
                </div>
                <form:form commandName="promotionsDTO" id="promotionsForm" action="promotions.do?method=savePromotions" method="post" name="thisform">
                    <input type="hidden" name="type" value="${promotionsDTO.type}"/>
                    <input id="promotionsId" type="hidden" name="id" value="${promotionsDTO.idStr}"/>
                    <table cellpadding="0" cellspacing="0" class="table_bargain table_promotion">
                        <col width="100" >
                        <col width="150" >
                        <col >
                        <tr>
                            <td class="name_right"><a class="red_color">*</a>促销名称</td>
                            <td colspan="2"><input  id="promotionsName"  name="name" value="${promotionsDTO.name}" type="text" class="txt txt_color" maxlength="20" />&nbsp;
                                <a class="right J_promotionsName_tip" style="display: none"></a><span class="gray_color">仅限20个字</span></td>
                        </tr>
                        <tr>
                            <td class="name_right" style="vertical-align:top;">促销描述</td>
                            <td colspan="2"><textarea id="description" name="description"  class="txt txt_color" maxlength="200">${promotionsDTO.description}</textarea>&nbsp;
                                <span class="gray_color" style="vertical-align:top;">仅限200个字</span></td>
                        </tr>
                        <tr>
                            <td class="name_right"><span class="red_color" >*</span>开始时间</td>
                            <td colspan="2">
                                <input  name="startTimeStr" type="text" value="${promotionsDTO.startTimeStr}"  class="time_input startTimeStr txt"  style="width:130px;" />
                            </td>
                        </tr>
                        <tr>
                            <td class="name_right" style="vertical-align:top; padding-top:8px;"><a class="red_color">*</a>结束时间</td>
                            <td colspan="2" class="td_time">
                                <input id="serviceStartTime" type="hidden" value="${startTime}" />
                                <input id="timeFlag" type="hidden" value="${promotionsDTO.timeFlag}"/>
                                <label class="rad"><input class="date_select_week date_select" name="date_select" type="radio" />7天</label>
                                <label class="rad"><input class="date_select_month date_select" name="date_select" type="radio" />30天</label>
                                <label class="rad"><input class="date_select_three_month date_select" name="date_select" type="radio" />90天</label>
                                <label class="rad"><input class="date_select_unlimited date_select" name="date_select" type="radio" />不限时</label>
                                <span id="lastDate" class="yellow_color limited_Date">活动持续时间<span  id="cDay" class="red_color">30</span>天<span  id="cHour" class="red_color">0</span>时</span>
                                     <span  class="yellow_color un_limited" style="display: none">
                                        活动持续时间不限时
                                     </span>
                                <div>
                                    <input type="radio"  name="date_select" class="date_select_define date_select"/>
                                    自定义时间
                                    <input value="${promotionsDTO.endTimeStr}" name="endTimeStr" type="text" class="time_input txt" style="display: none;width:130px;" />
                                    <%--<input value="${promotionsDTO.endTimeStr}" name="endTimeStr" type="hidden" />--%>
                                </div>
                            </td>
                        </tr>
                    </table>
                    <div class="clear i_height"></div>
                    <div class="divTit" style="margin-left:211px;">
                        <a id="savePromotionsBtn" pageType="manageBargain" class="button">保存促销</a>
                        <a id="nextToAddPromotionsProduct" pageType="manageBargain" class="button">下一步</a>
                    </div>
                </form:form>
            </div>
        </div>
    </div>

</div>
<div id="mask" style="display:block;position: absolute;"></div>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>