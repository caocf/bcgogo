<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<div id="promotionManagerAlert" class="alertMain" style="display: none">
     <input id="addPromotions_pageSource" type="hidden" value="goodsInSalesEditor"/>
    <div class="cuSearch" style="float:left;">
        <div class="create" style="border: 0px">
            <h3 style="color:#000000;">您可以创建打折、特价、送货上门等促销活动哦！</h3>
            <div class="createList">
                <span class="promotion_title_icon icon_minus " onclick="getMLJAlert()"></span>
                <div class="minus">
                    <b class="title">帮助您轻松提示客单价！</b>
                    <a onclick="getMLJAlert()" class="blue_color">立即创建>></a>
                </div>
            </div>
            <div class="createList">
                <span class="promotion_title_icon icon_special" onclick="getBargainAlert()"></span>
                <div class=" minus special">
                    <b class="title">推新品，清库存，快速出单！</b>
                    <a onclick="getBargainAlert()" class="blue_color">立即创建>></a>
                </div>
            </div>
            <div class="createList">
                <span class="promotion_title_icon icon_send" onclick="getMJSAlert()"></span>
                <div class="minus send">
                    <b class="title">快速刺激买家下单！</b>
                    <a onclick="getMJSAlert()" class="blue_color">立即创建>></a>
                </div>
            </div>
            <div class="createList">
                <span class="promotion_title_icon icon_avoid" onclick="getFreeShippingDivAlert()"></span>
                <div class="minus avoid">
                    <b class="title">吸引客户下单，积累客户数量！</b>
                    <a onclick="getFreeShippingDivAlert()" class="blue_color">立即创建>></a>
                </div>
            </div>
        </div>

    </div>
</div>
<div>
    <%--创建满立减--%>
    <%@ include file="manageMLJAlert.jsp" %>
    <%--创建满就送--%>
    <%@ include file="manageMJSAlert.jsp" %>
    <%--创建特价--%>
    <%@ include file="manageBargainAlert.jsp" %>
    <%--&lt;%&ndash;创建送货上门&ndash;%&gt;--%>
    <%--<%@ include file="manageFreeShippingAlert.jsp" %>--%>
</div>
<%--此处用DIV  show出来，参见BCSHOP-10229--%>
        <div id="manageFreeShippingAlertDiv" class="ui-dialog ui-widget ui-widget-content ui-corner-all ui-draggable ui-resizable" style="display: none; z-index: 1004; outline: 0px none; position: absolute; height: auto; width: 700px; top: 50%" tabindex="-1" role="dialog" aria-labelledby="ui-dialog-title-manageFreeShippingAlert">
            <%--创建送货上门--%>
            <%@ include file="manageFreeShippingAlertDiv.jsp" %>
        </div>



