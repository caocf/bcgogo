<%@ page import="com.bcgogo.config.ConfigController" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>用户注册</title>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/register<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="js/extension/jquery/plugin/jquery-ui/themes/flick/jquery-ui-1.8.21.custom.css"/>
    <script type="text/javascript" src="js/extension/jquery/jquery-1.4.2.min.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery-ui/ui/jquery-ui-1.8.21.custom.min.js"></script>
    <script type="text/javascript" src="js/application<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/base<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/module/bcgogo-noticeDialog<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/register/registerMain<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">
        $(function(){
            if(!swfobject.hasFlashPlayerVersion("11.8.0")){
                var content='<div>检测到您尚未安装Flash插件。请按以下步骤安装：</div>'+
                        '<div>1. <a class="blue_color" href="http://mail.bcgogo.com:8088/install_flash_player.exe" style="color: #007CDA">点我下载</a>，'+
                        '并记住下载到本机的文件位置</div>'+
                        '<div>2. 下载完成后关闭一发软件</div>'+
                        '<div>3. 找到下载好的文件，双击并安装</div>'+
                        '<div>4. 重新打开一发软件</div>';
                nsDialog.jAlert(content);
            }
        });
    </script>
</head>
<body class="bodyMain" style="width:100%;height:100%;">
<div class="clear m_topMain">
  <div class="l_top">
    <div class="l_topBorder"></div>
    <c:choose>
        <c:when test="${param.registerType eq 'SUPPLIER_REGISTER'}">
            <a href="user.do?method=createmain">
                <div class="home"></div>
            </a>
        </c:when>
        <c:otherwise>
            <div class="home"></div>
        </c:otherwise>
    </c:choose>
    <div class="l_topBorder"></div>
    <div class="l_topTitle_register">欢迎您注册一发EasyPower软件</div>
    <div class="l_topBorder"></div>
   </div>
</div>

<div class="i_main clear">
    <input type="hidden" id="paramRegisterType" value="${param.registerType}">
    <input type="hidden" id="paramNeedVerify" value="${param.needVerify}">
    <input type="hidden" id="customerId" value="${param.customerId}">
    <div style="float:left;">
        <h1 class="user">用户注册</h1>
        <div class="height"></div>
        <div style="float:left;">
            <div class="regMain">
                <c:if test="${!empty commonShopVersions}">
                    <label class="titles">汽车修理管理软件</label>
                    <c:forEach items="${commonShopVersions}" var="shopVersionDTO">
                        <div class="divSelect">
                            <label class="shopSelect select" versionName="${shopVersionDTO.name}" versionId="${shopVersionDTO.id}"
                                   registerType="CUSTOMER">${shopVersionDTO.description}</label>
                        </div>
                    </c:forEach>
                </c:if>
                <c:if test="${!empty wholesalerShopVersion}">
                    <label class="titles">汽车配件管理软件</label>
                    <c:forEach items="${wholesalerShopVersion}" var="shopVersionDTO">
                        <div class="divSelect">
                            <label class="shopSelect select" versionName="${shopVersionDTO.name}" versionId="${shopVersionDTO.id}"
                                   registerType="SUPPLIER">${shopVersionDTO.description}</label>
                        </div>
                    </c:forEach>
                </c:if>

                <c:if test="${!empty fourSShopVersions}">
                  <label class="titles">汽车4S店管理软件</label>
                  <c:forEach items="${fourSShopVersions}" var="shopVersionDTO">
                    <div class="divSelect">
                      <label class="shopSelect select" versionName="${shopVersionDTO.name}"
                             versionId="${shopVersionDTO.id}"
                             registerType="FOURS">${shopVersionDTO.description}</label>
                    </div>
                  </c:forEach>
                </c:if>
                <label>软件${SHOP_PROBATIONARY_PERIOD}天内免费试用，${SHOP_PROBATIONARY_PERIOD}天内全额付款，可享受优惠！</label>
            </div>
            <a class="clear nextBtn">下一步</a>
        </div>
        <div class="productInfo" id="showProductInfo">
        </div>
    </div>
    <a class="registerBg"></a>
</div>

<div id="REPAIR_SHOP_INFO" style="display: none">
    <div class="proTitle">产品介绍<label>（软件30天内免费试用）</label></div>
       <div class="proBody">
       	<h3 class="yellow_color">初级版—适用小型汽修美容店</h3>

           <div>
               一发软件初级版高度集成了小型汽修美容店需要使用的车辆施工管理、进销存管理、客户供应商管理、财务报表分析等功能，<b>特点：操作简单，智能运用，额外提供在线寻找本地和外地商品报价和供应商，在线比价。帮助小型汽修美容店精确管理，节省采购成本快速成长----拥有一发软件等于拥有一群供应链！适合10个人以内的店面使用！</b>更多详情，请前往注册试用！
           </div>
           <%--<c:if test="${param.registerType != 'SALES_REGISTER'}"><span class="yellow_color">软件销售价：${softPrice["REPAIR_SHOP"]}元</span></c:if>--%>
       </div>
       <div class="proBottom"></div>
</div>

<div id="INTEGRATED_SHOP_INFO" style="display: none">
    <div class="proTitle">产品介绍<label>（软件30天内免费试用）</label></div>
    <div class="proBody">
        <h3 class="yellow_color">综合版—适用中小型一站式维修厂</h3>

        <div>
            一发软件综合版全面实现中小型一站式维修厂的业务财务灵活管理应用，是一套简单易用、安全可靠的专业汽车行业软件。它提供车辆施工管理、进销存管理、客户供应商管理、员工管理，财务分析等功能，<b>特点：操作简单，智能运用，额外提供在线寻找本地和外地商品报价和供应商，在线比价。帮助小型汽修美容店精确管理，节省采购成本快速成长---拥有一发软件等于拥有一群供应链！适合20个人以内的店面使用。</b>更多详情，请前往注册试用！
        </div>
        <%--<c:if test="${param.registerType != 'SALES_REGISTER'}"><span class="yellow_color">软件销售价：${softPrice["INTEGRATED_SHOP"]}元</span></c:if>--%>
    </div>
    <div class="proBottom"></div>
</div>
<div id="ADVANCED_SHOP_INFO" style="display: none">
    <div class="proTitle">产品介绍<label>（软件30天内免费试用）</label></div>
    <div class="proBody">
        <h3 class="yellow_color">高级版—适用大型汽修店及4S店 </h3>

        <div>
            一发软件高级版提供车辆施工管理、采购、销售、仓存、库存核算盘点、财务报表与分析等功能模块。前台，仓管，财务，维修施工等分工明确。<b>特点：操作简单，智能运用，额外提供在线寻找本地和外地商品报价和供应商，在线比价。帮助小型汽修美容店精确管理，节省采购成本快速成长--拥有一发软件等于拥有一群供应链！适合50个人以上的店面使用！</b>更多详情，请前往注册试用！
        </div>
        <%--<c:if test="${param.registerType != 'SALES_REGISTER'}"><span class="yellow_color">软件销售价：${softPrice["ADVANCED_SHOP"]}元</span></c:if>--%>
    </div>
    <div class="proBottom"></div>
</div>
<div id="SMALL_WHOLESALER_SHOP_INFO" style="display: none">
    <div class="proTitle">产品介绍<label>（软件30天内免费试用）</label></div>
    <div class="proBody">
        <h3 class="yellow_color">标准版—适用小型批发商</h3>
        <div>
            一发软件汽配版是针对小型批车配件批发商的行业软件，其充分结合汽配行业经营的管理特点，集进销存管理、客户与供应商管理、在线销售、在线营销、财务管理于一体，帮助企业建立简单高效的业务流程，实现企业成本、利润核算的实时管理，提高企业经营管理水平。<b>特点：操作简单，智能分析，获取客户，精确促销,追踪商品在客户那里的销售状况等超级增值功能，--拥有一份批发软件等于拥有一个经营好帮手！适合小型10人内的批发商使用！</b>更多详情，请前往注册试用！
        </div>
        <%--<c:if test="${param.registerType != 'SALES_REGISTER'}"><div class="price">软件销售价：1900</div>--%>
        <%--<span class="yellow_color promotions">促销价:900元</span>--%>
        <%--<div class="yellow_color">截止日期：2013-11-11</div></c:if>--%>
    </div>
    <div class="proBottom"></div>
</div>
<div id="WHOLESALER_SHOP_INFO" style="display: none">
    <div class="proTitle">产品介绍<label>（软件30天内免费试用）</label></div>
    <div class="proBody">
        <h3 class="yellow_color">专业版—适用中型批发商</h3>

        <div>
            一发批发版为中型汽车配件批发商量身打造集进销存管理、客户和供应商管理、财务管理于一体，仓库，财务，采购，销售等角色分工明确帮助您实时掌握配件库存，往来对账清楚明白，提升工作效率与管理水平。<b>特点：操作简单，智能分析，在线快速获取新客户，精确促销,追踪商品在客户那里的销售状况等超级增值功能，帮助精确管理库存，科学安排物流等--拥有一份批发软件等于拥有一个经营好帮手！适合中大型的本地批发商使用！</b>更多详情，请前往注册试用！
        </div>
        <%--<c:if test="${param.registerType != 'SALES_REGISTER'}"><span class="yellow_color">软件销售价：${softPrice["WHOLESALER_SHOP"]}元</span></c:if>--%>
    </div>
    <div class="proBottom"></div>
</div>
<div id="LARGE_WHOLESALER_SHOP_INFO" style="display: none">
    <div class="proTitle">产品介绍<label>（软件30天内免费试用）</label></div>
    <div class="proBody">
        <h3 class="yellow_color">豪华版—适用大型配件批发商</h3>

        <div>
            一发软件大型批发版是一套针对汽车行业大型配件批发商的专业业务管理软件，它拥有强大的配件信息管理、简洁高效的仓储管理、敏捷的在线销售/营销系统、专业完善的财务分析报表，并通过灵活的客户/供应商管理、强大的价格管理帮助企业建立起高效的业务流程与协同工作机制，实现企业业务及成本、利润的实时管理，提示工作效率与管理水平！<b>特点：操作简单，智能分析，在线快速获取新客户，精确促销,追踪商品在客户那里的销售状况等超级增值功能，帮助精确管理库存，科学安排物流等--拥有一份批发软件等于拥有一个经营好帮手！适合大型的跨区域多仓库的批发商使用！</b>更多详情，请前往注册试用！
        </div>
        <%--<c:if test="${param.registerType != 'SALES_REGISTER'}"><span class="yellow_color">软件销售价：${softPrice["LARGE_WHOLESALER_SHOP"]}元</span></c:if>--%>
    </div>
    <div class="proBottom"></div>
</div>

<div id="FOUR_S_SHOP_INFO" style="display: none">
    <div class="proTitle">产品介绍<label>（软件30天内免费试用）</label></div>
    <div class="proBody">
        <h3 class="yellow_color">4S店专业版—适用4S店</h3>

        <div>
          一发软件4S店版提供 待办事项、车辆施工、客户管理、评价中心、消息中心、财务统计、查询中心、系统管理 等功能模块。<b>特点：1. 先进的客户关系管理 引入先进的客户关系管理理念，全面协助企业管理客户资源。通过对客户资源的有效管理，达到缩短销售周期、提高服务质量、提升客户满意度与忠诚度，增强企业综合竞争能力。
          2. 全面流畅的业务管理 包括售前的客户接待跟踪、售中车辆订购或销售、财务收款开票等功能。
          3. 强大的统计查询分析功能 除提供各种业务数据、财务数据的查询统计功能外，还提供强大的分析功能。</b>
          更多详情，请前往注册试用！
        </div>
        <%--<c:if test="${param.registerType != 'SALES_REGISTER'}"><span class="yellow_color">软件销售价：${softPrice["LARGE_WHOLESALER_SHOP"]}元</span></c:if>--%>
    </div>
    <div class="proBottom"></div>
</div>

<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>