<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<html xmlns="http://www.w3.org/1999/xhtml" >
<head xmlns="http://www.w3.org/1999/xhtml">
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title >询价比价</title>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
    <%@include file="/WEB-INF/views/header_script.jsp" %>
    <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/utils/imageUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/page/txn/enquiry/enquiryOrderDetail<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">
        defaultStorage.setItem(storageKey.MenuUid,"WEB.SCHEDULE.ENQUIRY_LIST");
    </script>
</head>
<body style="color: #000000;position: absolute;">
<%@ include file="/WEB-INF/views/header_html.jsp" %>
<div class="i_main clear">
    <div class="mainTitles">
        <div class="titleWords">询价比价详情</div>
    </div>
    <div class="booking-management">
        <div class="document-no">单据号：<span>${shopEnquiryDTO.receiptNo}</span></div>
        <div class="document-content">
            <h1>客户信息</h1>
            <table width="100%" border="0" cellspacing="0" class="equal2">
                <colgroup>
                    <col width="120">
                    <col width="120">
                    <col width="120">
                    <col width="120">
                    <col width="120">
                    <col width="150">
                    <col width="120">
                    <col width="120">
                </colgroup>
                <tr>
                    <td class="equal2_title">车牌号</td>
                    <td>${shopEnquiryDTO.vehicleNo}</td>
                    <td class="equal2_title">客户名</td>
                    <td>${shopEnquiryDTO.appUserName}</td>
                    <td class="equal2_title">手机号</td>
                    <td>${shopEnquiryDTO.appUserMobile}</td>
                    <td class="equal2_title">状态</td>
                    <td id="responseStatusStr">${shopEnquiryDTO.responseStatusStr}</td>
                </tr>
            </table>
            <h1>询价信息</h1>
            <c:if test="${!empty shopEnquiryDTO.enquiryImages}">
                <div>
                    <c:forEach var="appUserImageDTO" items="${shopEnquiryDTO.enquiryImages}">
                        <div class="price-img">
                            <img src="${appUserImageDTO.smallImageUrl}" bigSrc="${appUserImageDTO.bigImageUrl}"/>
                        </div>
                    </c:forEach>
                </div>
            </c:if>
            <div class="clear"></div>
            <table width="100%" border="0" cellspacing="0" class="equal2">
                <colgroup>
                <col width="100">
                <col>
                </colgroup>
                <tr>
                    <td class="equal2_title">描述</td>
                    <td style="text-align:left; padding-left:10px;">${shopEnquiryDTO.description}</td>
                </tr>
                <tr>
                    <td class="equal2_title">时间</td>
                    <td style="text-align:left; padding-left:10px;">${shopEnquiryDTO.sendTimeStr}</td>
                </tr>
            </table>
            <h1>报价信息</h1>
            <div id="enquiryResponseInfo">
                <c:choose>
                    <c:when test="${!empty shopEnquiryDTO.enquiryShopResponses}">
                        <c:forEach items="${shopEnquiryDTO.enquiryShopResponses}" var="enquiryShopResponse" varStatus="status">
                            <div id="enquiryShopResponses${status.index}" class="J_response_content">
                                <input type="hidden" id="enquiryShopResponses${status.index}.id" value="${enquiryShopResponse.id}">
                                <div class="gray-line">${enquiryShopResponse.responseTimeStr}</div>
                                <div class="white-line">${enquiryShopResponse.responseMsg}</div>
                            </div>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <div class="white-line J_NO_Quotation">尚未报价！</div>
                    </c:otherwise>
                </c:choose>
            </div>
            </div>
            <table width="100%" border="0" cellspacing="5">
                <input type="hidden" id="enquiryId" value="${shopEnquiryDTO.id}"/>
                <tr>
                    <td width="10%" valign="top">报价：</td>
                    <td width="90%">
                        <textarea id="responseMsg" class="txt" style="width:915px; height:100px;" maxLength = "200"></textarea>
                    </td>
                </tr>
            </table>
            <div class="clear"></div>
        </div>

        <div class="clear i_height"></div>
        <input id="quote" type="button" class="jieCount" value="立即报价">
        <div class="shopping_btn">
            <div id="printBtn" class="divImg">
                <input class="print" type="button" onfocus="this.blur();">

                <div class="sureWords">打印</div>
            </div>
            <div id="returnBtn" class="divImg">
                <input class="return" type="button" onfocus="this.blur();">

                <div class="sureWords">返回列表</div>
            </div>
        </div>
        <div class="clear"></div>
    </div>
<div>
    <div class="img_dialog_mask" style="display: none"></div>
    <div class="img_dialog_layer" style="display: none">
        <div class="img_dialog_preview">
            <%--<a class="img_dialog_close" title="关闭"></a>--%>
            <div class="img_dialog_scroller">
                <div class="img_dialog_pic_show_box">
                    <div class="img_dialog_pic_box">
                        <img src="">
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>