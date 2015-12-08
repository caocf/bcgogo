<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<%@include file="/WEB-INF/views/header_script.jsp" %>


<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"><html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>推荐客户</title>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>

    <script type="text/javascript" src="js/components/ui/bcgogo-mapMark.js"></script>
    <script type="text/javascript" src="js/components/ui/bcgogo-scrollFlow<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/page/customer/applyCustomerIndex<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">
        defaultStorage.setItem(storageKey.MenuUid, "CUSTOMER_MANAGER_SEARCH_APPLY_CUSTOMER");
    </script>
</head>

<body class="bodyMain">
<%@include file="/WEB-INF/views/header_html.jsp" %>
<div class="i_main clear">
    <div class="mainTitles">
        <div class="titleWords">推荐客户</div>
    </div>
    <div class="titBody">
    <div class="bodyLeft look_Left">
        <div class="clear"></div>
        <div id="recommendCustomerDiv" class="JScrollFlowVertical J_customerRecommendShow" style="display: none;"></div>
        <div class="clear i_height J_customerRecommendShow"  style="display: none;"></div>

        <c:if test="${!empty productCategoryNode && fn:length(productCategoryNode.children)>0}">
            <div class="cuSearch look_supplier">
                <div class="look_title"><a class="icon_look"></a>按配件查找客户<a class="blue_color" style="float:right; font-size:12px; font-weight:normal;" href="apply.do?method=getApplyCustomersPage">查看所有</a></div>
                <table class="tab_cuSearch tab_look" cellpadding="0" cellspacing="0">
                    <col width="75">
                    <col>
                    <c:set var="secondNodeCount" value="0"/>
                    <c:forEach items="${productCategoryNode.children}" var="firstNode">
                        <c:if test="${secondNodeCount<20}">
                            <tr>
                                <td class="tab_title">${firstNode.text}</td>
                                <td>
                                    <table class="tab_lookList" cellpadding="0" cellspacing="0">
                                        <c:forEach items="${firstNode.children}" var="secondNode" varStatus="secondStatus">
                                            <c:if test="${secondNodeCount<20}">
                                                <c:set var="secondNodeCount" value="${secondNodeCount+1}"/>
                                                <c:set var="thirdTextLength" value="0"/>
                                                <c:if test="${secondStatus.index%2==0}">
                                                    <tr>
                                                </c:if>
                                                <td>
                                                    <div class="look_list">
                                                        <a class="blue_color" href="apply.do?method=getApplyCustomersPage&secondCategoryIdStr=${secondNode.id}"><b>${secondNode.text}</b></a>
                                                        <span class="look_listInfo">
                                                            <c:forEach items="${secondNode.children}" var="thirdNode">
                                                                <c:set var="thirdTextLength" value="${thirdTextLength+fn:length(thirdNode.text)+1}"/>
                                                                <a <c:if test="${thirdTextLength>29}">class="J_showProductCategoryAll" style="display: none"</c:if>  href="apply.do?method=getApplyCustomersPage&thirdCategoryIdStr=${thirdNode.id}">${thirdNode.text}</a>
                                                            </c:forEach>
                                                            <c:if test="${thirdTextLength>29}"><a class="icon_more J_showMoreProductCategory">更多</a></c:if>
                                                        </span>
                                                    </div>
                                                </td>
                                                <c:if test="${secondStatus.index%2==1}">
                                                    </tr>
                                                </c:if>
                                            </c:if>
                                        </c:forEach>
                                    </table>
                                </td>
                            </tr>
                        </c:if>
                    </c:forEach>
                </table>

            </div>
        </c:if>

    </div>
    <div class="bodyRight look_Right">
        <img src="images/adsPictures.png" />
        <div class="i_height"></div>
        <div class="map look_supplier" style="height: 330px;width: 328px">
            <div class="look_title"><a class="icon_look"></a>按地区查找客户</div>
            <div id="mapDiv" class="map_body"></div>
        </div>
    </div>
    <div class="height"></div>
    </div>
</div>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>
