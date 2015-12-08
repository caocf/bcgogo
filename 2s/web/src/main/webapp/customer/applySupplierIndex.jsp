<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<%@include file="/WEB-INF/views/header_script.jsp" %>


<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"><html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>推荐供应商</title>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>

    <script type="text/javascript" src="js/components/ui/bcgogo-mapMark.js"></script>
    <script type="text/javascript" src="js/components/ui/bcgogo-scrollFlow<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/page/customer/applySupplierIndex<%=ConfigController.getBuildVersion()%>.js"></script>
     <script type="text/javascript" src="js/selectOptions<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/module/invoiceCommon<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">
        defaultStorage.setItem(storageKey.MenuUid, "APPLY_GET_APPLY_SUPPLIERS");
    </script>
    <bcgogo:hasPermission permissions="WEB.AD_SHOW">
        <script type="text/javascript" src="js/adShow<%=ConfigController.getBuildVersion()%>.js"></script>
    </bcgogo:hasPermission>
</head>

<body class="bodyMain">
<%@include file="/WEB-INF/views/header_html.jsp" %>
<input type="hidden" value="applySupplierIndex" id="pageType">
<div class="i_main clear">
    <div class="mainTitles">
        <div class="titleWords">推荐供应商</div>
    </div>
    <div class="titBody">
        <div class="bodyLeft look_Left">
            <div class="clear"></div>
            <div id="recommendSupplierDiv" class="JScrollFlowVertical J_supplierRecommendShow" style="display: none;"></div>
            <div class="clear i_height J_supplierRecommendShow"  style="display: none;"></div>

            <c:if test="${!empty productCategoryNode && fn:length(productCategoryNode.children)>0}">
                <div class="cuSearch look_supplier">
                    <div class="look_title"><a class="icon_look"></a>按配件查找供应商<a class="blue_color" style="float:right; font-size:12px; font-weight:normal;" href="apply.do?method=getApplySuppliersPage">查看所有</a></div>
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
                                                            <a class="blue_color" href="apply.do?method=getApplySuppliersPage&secondCategoryIdStr=${secondNode.id}"><b>${secondNode.text}</b></a>
                                                                    <span class="look_listInfo">
                                                                        <c:forEach items="${secondNode.children}" var="thirdNode">
                                                                            <c:set var="thirdTextLength" value="${thirdTextLength+fn:length(thirdNode.text)+1}"/>
                                                                            <a <c:if test="${thirdTextLength>29}">class="J_showProductCategoryAll" style="display: none"</c:if>  href="apply.do?method=getApplySuppliersPage&thirdCategoryIdStr=${thirdNode.id}">${thirdNode.text}</a>
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

            <div class="map look_supplier" style="height: 330px;width: 328px">
                <div class="look_title"><a class="icon_look"></a>按地区查找供应商</div>
                <div id="mapDiv" class="map_body"></div>
            </div>
            <div class="clear i_height"></div>
              <div class="map look_supplier">
            	<div class="look_title"><a class="icon_look"></a>按车型查找供应商</div>
                  <form:form id="searchApplySupplierForm" action="apply.do?method=getApplySuppliersPage" method="post">
                      <div class="map_body" style="padding:10px">
                          <input type="text" style="width:135px;" class="txt" placeholder="车辆品牌" id="vehicles0.vehicleBrand" name="standardVehicleBrand" pagetype="customerVehicle">
                          <input type="text" class="txt" placeholder="车型" id="vehicles0.vehicleModel" name="standardVehicleModel" pagetype="customerVehicle">
                          <input type="button" class="i_search_btn" id="searchSupplyBtn">
                      </div>
                 </form:form>
            </div>


            <div class="clear i_height"></div>
            <c:choose>
                <c:when test="${!empty starShopDTOList}">
                    <div class="star_list" style="height: auto">
                        <span class="cart_title star_title"></span>

                        <div class="star_shop">
                            <c:forEach items="${starShopDTOList}" var="starShopDTO">
                                <span class="star_shoplist"><a href="#" onclick="window.open('supplier.do?method=redirectSupplierComment&paramShopId=${starShopDTO.id}');" class="blue_color">${starShopDTO.name}</a>（${starShopDTO.areaName}）</span>
                            </c:forEach>
                        </div>
                    </div>
                </c:when>
                <c:otherwise>
                    <img src="images/adsPictures.png" />
                </c:otherwise>
            </c:choose>
        </div>
        <div class="height"></div>
    </div>
</div>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>
