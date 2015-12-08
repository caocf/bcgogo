<%@ page import="com.bcgogo.config.ConfigController" %>
<%--
  Created by IntelliJ IDEA.
  User: Hans
  Date: 13-5-26
  Time: 上午10:29
--%>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta content="text/html;charset=utf-8" http-equiv="content-type" >
    <title>页面配置</title>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/head<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
    <%@include file="/WEB-INF/views/header_script.jsp" %>
    <script type="text/javascript" src="js/page/admin/pageCustomizerConfig<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">
        defaultStorage.setItem(storageKey.MenuUid,"WEB_SYSTEM_SETTINGS_CUSTOM_CONFIG_PAGE_CONFIG");
    </script>
</head>
<body class="bodyMain">
<%@include file="/WEB-INF/views/header_html.jsp" %>
<div class="i_main clear">
<div class="mainTitles">
    <jsp:include page="customConfigNav.jsp">
        <jsp:param name="currPage" value="pageCustomizerConfig"/>
    </jsp:include>
</div>
<div class="titBody cuSearch">
    <div class="cartTop"></div>
    <div class="lineBody bodys cartBody">
        <div class="divTit" style="width:100%;">
            <b>请选择所需配置的项目：</b>
            <select id="select-page-config-scene" oldvalue="order">
                <option value="order">单据查询条件</option>
              <bcgogo:hasPermission permissions="WEB.TXN.INVENTORY_MANAGE.STOCK_SEARCH">
                <option value="product">库存商品栏列</option>
              </bcgogo:hasPermission>

            </select>
        </div>
        <form id="order-page-config-form" action="pageCustomizerConfig.do?method=updateOrderPageConfig" method="post">
            <div class="div_document" area-name="order">
                <div class="divTit">
                    提示：您可在这里进行条件隐藏的操作！
                </div>
                <div class="divTit" style="float:right;"><a class="blue_color" id="restore-order-config" scene="ORDER">还原所有隐藏条件</a></div>
                <div id="order_condition_area">
                </div>
            </div>
        </form>
        <div class="div_shopping" area-name="product" style="display: none">
            <input type="hidden" id="page_customizer_of_product_config">
            <input type="hidden" id="default_page_customizer_of_product_config">
            <div class="divTit">
                下面显示了您的库存商品的信息，您可以用&nbsp;<span class="icon_left"></span>&nbsp;与&nbsp;<span
                    class="icon_right"></span>&nbsp;调整列显示顺序，用&nbsp;<span class="icon_delete"></span>&nbsp;移除一个列！
            </div>
            <div class="divTit" style="float:right;"><a class="blue_color" id="restore-product-config" scene="PRODUCT">还原默认设置</a></div>
            <form action="pageCustomizerConfig.do?method=updateProductPageConfig" id="order-product-config-form" method="post">
                <table cellpadding="0" cellspacing="0" class="tab_cuSearch" id="product_condition_area" >
                </table>
            </form>
        </div>
        <div class="height"></div>
        <div class="divTit button_conditon button_search">
            <a class="button gray_button" id="save-page-customizer-config">保存设置</a>
            <a class="button gray_button" id="cancel-page-customizer-config">取&nbsp;消</a>
        </div>
    </div>
    <div class="cartBottom"></div>
    <div class="clear height"></div>
</div>
</div>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>