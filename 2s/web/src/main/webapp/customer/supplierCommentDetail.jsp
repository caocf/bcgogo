<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
  <title>评价详情</title>
  <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
  <link rel="stylesheet" type="text/css" href="styles/supplierCommentDetail<%=ConfigController.getBuildVersion()%>.css"/>
  <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
  <%@include file="/WEB-INF/views/header_script.jsp" %>
  <script type="text/javascript" src="js/statementAccount/statementAccountUtil<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/customerOrSupplier/supplierCommentDetail<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">
        defaultStorage.setItem(storageKey.MenuUid, "APPLY_GET_APPLY_SUPPLIERS");
    </script>
</head>
<body class="bodyMain">
<input type="hidden" id="paramShopId" name="paramShopId" value="${paramShopId}"/>
<%@include file="/WEB-INF/views/header_html.jsp" %>
<div class="i_main clear">
    <div class="height"></div>
<div class="supplierInfo">
    <div class="divTit">
        <b class="name">${shopDTO.name}</b>
    </div>
    <div class="divTit">
        <c:if test="${not empty supplierId}">
            <a class="blue_color" href="unitlink.do?method=supplier&supplierId=${supplierId}">查看本地资料</a>&nbsp;&nbsp;
        </c:if>

      <c:if test="${selfShopComment == 'selfShopComment'}">
        <a class="blue_color" href="goodsInOffSales.do?method=toInSalingGoodsList">查看上架商品</a>
      </c:if>
      <c:if test="${selfShopComment != 'selfShopComment'}">
        <a class="blue_color" href="autoAccessoryOnline.do?method=toCommodityQuotations&shopId=${shopDTO.id}">查看上架商品</a>
      </c:if>

    </div>
    <div class="divTit" style="width:100%;">
        <span class="connact_title">所&nbsp;在&nbsp;地：</span>
        <div class="connact">
            <div>${shopDTO.areaName}</div>
        </div>
    </div>
    <div class="divTit">注册时间：<span>${shopDTO.registrationDateStr}</span></div>
    <div class="divTit">成交笔数：<span>${commentStatDTO.orderAmount}</span>笔</div>
    <div class="divTit" style="width:100%;">
        <span class="connact_title">联&nbsp;系&nbsp;人：</span>
        <div class="connact">
            <c:forEach items="${shopDTO.contacts}" var="contact" varStatus="status">
                <c:if test="${contact != null}">
                    <div>
                        <span>${empty contact.name?'暂无信息':contact.name}</span>
                        <span class="icon_phone"></span>
                        <span>${empty contact.mobile?'暂无信息':contact.mobile}</span>
                    <c:if test="${not empty supplierId}">
                        <span class="icon_QQ"></span>
                        <span class="qq_width">${empty contact.qq?'暂无信息':contact.qq}</span>
                        <span class="icon_email"></span>
                        <span>${empty contact.email?'暂无信息':contact.email}</span>
                    </c:if>
                    </div>
                </c:if>
            </c:forEach>
        </div>
    </div>
    <div class="divTit">
        座&nbsp;&nbsp;&nbsp;&nbsp;机：<span>${empty shopDTO.landline?'暂无信息':shopDTO.landline}</span>
    </div>
    <div class="divTit">
        &nbsp;传&nbsp;&nbsp;&nbsp;&nbsp;真：<span>${empty shopDTO.fax?'暂无信息':shopDTO.fax}</span>
    </div>
    <div class="divTit">
        <span class="connact_title">经营产品：</span>
        <div class="connact">
            <span>${empty shopDTO.businessScopeStr?'暂无信息':shopDTO.businessScopeStr}</span>
        </div>
    </div>
</div>

<div class="score supplier_score">
    <div class="divTit" style="width:100%;"><b class="name">评分详细</b></div>
    <div class="allScore">总分：<span class="total_star" style=" background-position: 0px -${supplierCommentStatDTO.totalScoreSpan}px;"></span>
        <span class="yellow_color">
            <c:choose><c:when test="${supplierCommentStatDTO.totalScore == 0}">暂无分数</c:when><c:otherwise>${supplierCommentStatDTO.totalScore} 分</c:otherwise></c:choose>
        </span>&nbsp;
        <span class="gray_color">共<span>${supplierCommentStatDTO.recordAmount}</span>名客户参与评分</span></div>
    <div class="shop">
        <div class="shopTit">货品质量&nbsp;<a class="bigStar" style=" background-position: 0px -${commentStatDTO.qualityScoreSpan}px;"></a>&nbsp;<span class="yellow_color"><c:choose><c:when test="${commentStatDTO.qualityTotalScore == 0}">暂无分数</c:when><c:otherwise>${commentStatDTO.qualityTotalScore} 分</c:otherwise></c:choose></span></div>
        <div class="shopList"><a class="littleStar"></a>&nbsp;<span class="yellow_color">5.0</span>分&nbsp;<span style="width:70px; display:inline-block;"><span class="percent" style="width:${commentStatDTO.qualityFiveAmountPer};"></span></span>&nbsp;<span class="yellow_color">${commentStatDTO.qualityFiveAmountPer}</span></div>
        <div class="shopList"><a class="littleStar" style="background-position:0px -11px;"></a>&nbsp;<span class="yellow_color">4.0</span>分&nbsp;<span style="width:70px; display:inline-block;"><span class="percent" style="width:${commentStatDTO.qualityFourAmountPer};"></span></span>&nbsp;<span class="yellow_color">${commentStatDTO.qualityFourAmountPer}</span></div>
        <div class="shopList"><a class="littleStar" style="background-position:0px -22px;"></a>&nbsp;<span class="yellow_color">3.0</span>分&nbsp;<span style="width:70px; display:inline-block;"><span class="percent" style="width:${commentStatDTO.qualityThreeAmountPer};"></span></span>&nbsp;<span class="yellow_color">${commentStatDTO.qualityThreeAmountPer}</span></div>
        <div class="shopList"><a class="littleStar" style="background-position:0px -33px;"></a>&nbsp;<span class="yellow_color">2.0</span>分&nbsp;<span style="width:70px; display:inline-block;"><span class="percent" style="width:${commentStatDTO.qualityTwoAmountPer};"></span></span>&nbsp;<span class="yellow_color">${commentStatDTO.qualityTwoAmountPer}</span></div>
        <div class="shopList"><a class="littleStar" style="background-position:0px -44px;"></a>&nbsp;<span class="yellow_color">1.0</span>分&nbsp;<span style="width:70px; display:inline-block;"><span class="percent" style="width:${commentStatDTO.qualityOneAmountPer};"></span></span>&nbsp;<span class="yellow_color">${commentStatDTO.qualityOneAmountPer}</span></div>
    </div>

    <div class="shop">
        <div class="shopTit">货品性价比&nbsp;<a class="bigStar" style=" background-position: 0px -${commentStatDTO.performanceScoreSpan}px;"></a>&nbsp;<span class="yellow_color"><c:choose><c:when test="${commentStatDTO.performanceTotalScore == 0}">暂无分数</c:when><c:otherwise>${commentStatDTO.performanceTotalScore} 分</c:otherwise></c:choose></span></div>
        <div class="shopList"><a class="littleStar"></a>&nbsp;<span class="yellow_color">5.0</span>分&nbsp;<span style="width:70px; display:inline-block;"><span class="percent" style="width:${commentStatDTO.performanceFiveAmountPer};"></span></span>&nbsp;<span class="yellow_color">${commentStatDTO.performanceFiveAmountPer}</span></div>
        <div class="shopList"><a class="littleStar" style="background-position:0px -11px;"></a>&nbsp;<span class="yellow_color">4.0</span>分&nbsp;<span style="width:70px; display:inline-block;"><span class="percent" style="width:${commentStatDTO.performanceFourAmountPer};"></span></span>&nbsp;<span class="yellow_color">${commentStatDTO.performanceFourAmountPer}</span></div>
        <div class="shopList"><a class="littleStar" style="background-position:0px -22px;"></a>&nbsp;<span class="yellow_color">3.0</span>分&nbsp;<span style="width:70px; display:inline-block;"><span class="percent" style="width:${commentStatDTO.performanceThreeAmountPer};"></span></span>&nbsp;<span class="yellow_color">${commentStatDTO.performanceThreeAmountPer}</span></div>
        <div class="shopList"><a class="littleStar" style="background-position:0px -33px;"></a>&nbsp;<span class="yellow_color">2.0</span>分&nbsp;<span style="width:70px; display:inline-block;"><span class="percent" style="width:${commentStatDTO.performanceTwoAmountPer};"></span></span>&nbsp;<span class="yellow_color">${commentStatDTO.performanceTwoAmountPer}</span></div>
        <div class="shopList"><a class="littleStar" style="background-position:0px -44px;"></a>&nbsp;<span class="yellow_color">1.0</span>分&nbsp;<span style="width:70px; display:inline-block;"><span class="percent" style="width:${commentStatDTO.performanceOneAmountPer};"></span></span>&nbsp;<span class="yellow_color">${commentStatDTO.performanceOneAmountPer}</span></div>
    </div>

    <div class="shop">
        <div class="shopTit">发货速度&nbsp;<a class="bigStar" style=" background-position: 0px -${commentStatDTO.speedScoreSpan}px;"></a>&nbsp;<span class="yellow_color"><c:choose><c:when test="${commentStatDTO.speedTotalScore == 0}">暂无分数</c:when><c:otherwise>${commentStatDTO.speedTotalScore} 分</c:otherwise></c:choose></span></div>
        <div class="shopList"><a class="littleStar"></a>&nbsp;<span class="yellow_color">5.0</span>分&nbsp;<span style="width:70px; display:inline-block;"><span class="percent" style="width:${commentStatDTO.speedFiveAmountPer};"></span></span>&nbsp;<span class="yellow_color">${commentStatDTO.speedFiveAmountPer}</span></div>
        <div class="shopList"><a class="littleStar" style="background-position:0px -11px;"></a>&nbsp;<span class="yellow_color">4.0</span>分&nbsp;<span style="width:70px; display:inline-block;"><span class="percent" style="width:${commentStatDTO.speedFourAmountPer};"></span></span>&nbsp;<span class="yellow_color">${commentStatDTO.speedFourAmountPer}</span></div>
        <div class="shopList"><a class="littleStar" style="background-position:0px -22px;"></a>&nbsp;<span class="yellow_color">3.0</span>分&nbsp;<span style="width:70px; display:inline-block;"><span class="percent" style="width:${commentStatDTO.speedThreeAmountPer};"></span></span>&nbsp;<span class="yellow_color">${commentStatDTO.speedThreeAmountPer}</span></div>
        <div class="shopList"><a class="littleStar" style="background-position:0px -33px;"></a>&nbsp;<span class="yellow_color">2.0</span>分&nbsp;<span style="width:70px; display:inline-block;"><span class="percent" style="width:${commentStatDTO.speedTwoAmountPer};"></span></span>&nbsp;<span class="yellow_color">${commentStatDTO.speedTwoAmountPer}</span></div>
        <div class="shopList"><a class="littleStar" style="background-position:0px -44px;"></a>&nbsp;<span class="yellow_color">1.0</span>分&nbsp;<span style="width:70px; display:inline-block;"><span class="percent" style="width:${commentStatDTO.speedOneAmountPer};"></span></span>&nbsp;<span class="yellow_color">${commentStatDTO.speedOneAmountPer}</span></div>
    </div>

    <div class="shop">
            <div class="shopTit">服务态度&nbsp;<a class="bigStar" style=" background-position: 0px -${supplierCommentStatDTO.attitudeScoreSpan}px;"></a>&nbsp;<span class="yellow_color"><c:choose><c:when test="${supplierCommentStatDTO.attitudeTotalScore == 0}">暂无分数</c:when><c:otherwise>${supplierCommentStatDTO.attitudeTotalScore} 分</c:otherwise></c:choose></span></div>
        <div class="shopList"><a class="littleStar"></a>&nbsp;<span class="yellow_color">5.0</span>分&nbsp;<span style="width:70px; display:inline-block;"><span class="percent"  style="width:${supplierCommentStatDTO.attitudeFiveAmountPer};"></span></span>&nbsp;<span class="yellow_color">${supplierCommentStatDTO.attitudeFiveAmountPer}</span></div>
        <div class="shopList"><a class="littleStar" style="background-position:0px -11px;"></a>&nbsp;<span class="yellow_color">4.0</span>分&nbsp;<span style="width:70px; display:inline-block;"><span class="percent"  style="width:${supplierCommentStatDTO.attitudeFourAmountPer};"></span></span>&nbsp;<span class="yellow_color">${supplierCommentStatDTO.attitudeFourAmountPer}</span></div>
        <div class="shopList"><a class="littleStar" style="background-position:0px -22px;"></a>&nbsp;<span class="yellow_color">3.0</span>分&nbsp;<span style="width:70px; display:inline-block;"><span class="percent"  style="width:${supplierCommentStatDTO.attitudeThreeAmountPer};"></span></span>&nbsp;<span class="yellow_color">${supplierCommentStatDTO.attitudeThreeAmountPer}</span></div>
        <div class="shopList"><a class="littleStar" style="background-position:0px -33px;"></a>&nbsp;<span class="yellow_color">2.0</span>分&nbsp;<span style="width:70px; display:inline-block;"><span class="percent"  style="width:${supplierCommentStatDTO.attitudeTwoAmountPer};"></span></span>&nbsp;<span class="yellow_color">${supplierCommentStatDTO.attitudeTwoAmountPer}</span></div>
        <div class="shopList"><a class="littleStar" style="background-position:0px -44px;"></a>&nbsp;<span class="yellow_color">1.0</span>分&nbsp;<span style="width:70px; display:inline-block;"><span class="percent"  style="width:${supplierCommentStatDTO.attitudeOneAmountPer};"></span></span>&nbsp;<span class="yellow_color">${supplierCommentStatDTO.attitudeOneAmountPer}</span></div>
    </div>
</div>
<div class="clear i_height"></div>
<div class="recordList">来自客户的评价</div>
<div class="clear"></div>
<div class="shoppingCart">
  <div class="cartTop"></div>
  <div class="cartBody">
    <table cellpadding="0" cellspacing="0" class="tabCart" style="table-layout: fixed;" id="supplierCommentRecordTable">
      <col width="60">
      <col width="80">
      <col width="220">
      <col>
      <col width="180">
      <tr class="titleBg">
        <td style="padding-left:10px;">No</td>
        <td>评价时间</td>
        <td>评分</td>
        <td>详细评论</td>
        <td>客户</td>
      </tr>
    </table>

    <div id="noSupplierCommentRecord" style="width:480px;height:30px;color: black;display: none;text-align:center;">该供应商暂无评价记录！</div>

    <jsp:include page="/common/pageAJAX.jsp">
      <jsp:param name="url" value="supplier.do?method=getSupplierCommentRecord"></jsp:param>
      <jsp:param name="jsHandleJson" value="initSupplierCommentRecord"></jsp:param>
      <jsp:param name="dynamical" value="initSupplierCommentRecord"></jsp:param>
      <jsp:param name="data" value="{startPageNo:'1',maxRows:5,paramShopId:$('#paramShopId').val()}"></jsp:param>
    </jsp:include>

  </div>
  <div class="cartBottom"></div>
</div>
</div>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>