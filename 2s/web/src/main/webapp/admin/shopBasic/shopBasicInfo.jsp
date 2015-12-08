
<%--
  店面基本资料
  Created by IntelliJ IDEA.
  User: lw
  Date: 13-3-19
  Time: 下午1:54
  To change this template use File | Settings | File Templates.
--%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
  <title>系统管理—本店基本资料</title>
  <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
  <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
  <link rel="stylesheet" type="text/css"
        href="styles/supplierCommentDetail<%=ConfigController.getBuildVersion()%>.css"/>
  <script type="text/javascript"
          src="js/customerOrSupplier/supplierCommentUtil<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript"
          src="js/statementAccount/statementAccountUtil<%=ConfigController.getBuildVersion()%>.js"></script>
  <%@include file="/WEB-INF/views/header_script.jsp" %>
</head>
<body class="bodyMain">
<input type="hidden" id="supplierId" name="supplierId" value="${shopDTO.id}"/>
<%@include file="/WEB-INF/views/header_html.jsp" %>
<div class="title"></div>
<div class="i_main clear">
<%--    <jsp:include page="../systemManagerNavi.jsp">
        <jsp:param name="currPage" value="shopBasicInfo"/>
    </jsp:include>--%>
    <div class="mainTitles">
        <div class="titleWords">本店基本资料</div>
        <%--</div>--%>
        <bcgogo:hasPermission permissions="WEB.SYSTEM_SETTINGS.SHOP_BASIC_COMMENT">
            <div class="titBodys">
                <a class="hover_btn" href="shopBasic.do?method=getShopBasicInfo" action-type="menu-click"
                                   menu-name="WEB.SYSTEM_SETTINGS.SHOP_BASIC_INFO">本店基本资料</a>
                <a class="normal_btn" action-type="menu-click"
                               menu-name="WEB.SYSTEM_SETTINGS.SHOP_BASIC_COMMENT"
                               callback="redirectShopCommentDetail('${shopDTO.id}')">本店评价</a>
            </div>
        </bcgogo:hasPermission>
    </div>
    <div class="supplierInfo basicDetails">
        <div class="divTit">
            <b class="name">基本信息</b>
        </div>
        <%--<div class="divTit">
            <a class="blue_color">修改信息</a>
        </div>--%>
        <div class="divTit" style="width:100%;">
            公 司 名 称：<span>${shopDTO.name}</span>
        </div>
        <div class="divTit" style="width:190px;">注 册 时 间：<span>${shopDTO.registrationDateStr}</span></div>
        <div class="divTit" style="width:180px;">固定电话：<span>${shopDTO.landline}</span></div>
        <div class="divTit" style="width:180px;">
            所在地车牌：<span>${shopDTO.licencePlate}</span>
        </div>
        <div class="divTit" style="width:190px;">
            负责人/店主：<span>${shopDTO.owner}</span>
        </div>
        <div class="divTit">手机号码：<span>${shopDTO.mobile}</span></div>
        <div class="divTit" style="width:100%;">详 细 地 址：<span>${shopDTO.address}</span></div>
        <div class="divTit" style="width:190px;">网&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;址：<span>${shopDTO.url}</span></div>
        <div class="divTit">经营方式：<span>有限公司</span></div>
        <div class="divTit" style="width:100%;">经营产品：<span>${shopDTO.businessScopeStr}</span></div>

        <div class="divTit" style="width:100%;">备&nbsp;&nbsp;&nbsp;&nbsp;注：<span></span></div>
        <div class="divTit" style="width:100%;">
            <span class="connact_title">联&nbsp;系&nbsp;人：</span>

            <div class="connact basic_connact" style="width:550px;">
                <c:forEach items="${shopDTO.contacts}" var="contact" varStatus="status">
                    <c:if test="${contact != null}">
                    <div>
                        <span>${empty contact.name?'暂无信息':contact.name}</span>
                        <span class="icon_phone"></span>
                        <span>${empty contact.mobile?'暂无信息':contact.mobile}</span>
                        <span class="icon_QQ"></span>
                        <span class="qq_width">${empty contact.qq?'暂无信息':contact.qq}</span>
                        <span class="icon_email"></span>
                        <span>${empty contact.email?'暂无信息':contact.email}</span>
                        <c:if test="${contact.isShopOwner==1}"><span class="icon_connacter">主联系人</span></c:if>
                    </div>
                    </c:if>
                </c:forEach>
            </div>
        </div>

    </div>

    <div class="picture">
      <c:choose>
        <c:when test="${shopDTO.businessLicenseId == null}">
          <div class="lists">
            <div class="listsTop"></div>
            <div class="listsBody">
                <span>营业执照：</span>
              暂无图片
            </div>
            <div class="listsBottom"></div>
          </div>
        </c:when>
        <c:otherwise>
          <div class="lists">
            <div class="listsTop"></div>
            <div class="listsBody">
                <span>营业执照：</span>
              <img src="shopBasic.do?method=getShopBusinessLicense&shopId=${shopDTO.id}" width="100px" height="100px"
                   id="shopBusinessLicense"/>
            </div>
            <div class="listsBottom"></div>
          </div>
        </c:otherwise>
      </c:choose>
    </div>

    <div class="picture">
      <c:choose>
        <c:when test="${shopDTO.shopPhotoId == null}">
          <div class="lists">
            <div class="listsTop"></div>
            <div class="listsBody">
                <span>店面照片：</span>
              暂无图片
            </div>
            <div class="listsBottom"></div>
          </div>
        </c:when>
        <c:otherwise>
          <div class="lists">
            <div class="listsTop"></div>
            <div class="listsBody">
                <span>店面照片：</span>
              <img src="shopBasic.do?method=getShopPhotoByShopId&shopId=${shopDTO.id}" width="100px" height="100px"
                   id="photo"/>
            </div>
            <div class="listsBottom"></div>
          </div>
        </c:otherwise>
      </c:choose>
    </div>
    <div class="clear i_height"></div>
  </div>

<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>