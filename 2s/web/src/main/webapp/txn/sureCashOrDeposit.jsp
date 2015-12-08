<%@ page import="com.bcgogo.config.ConfigController" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>现金或定金</title>
    <link rel="stylesheet" type="text/css" href="styles/up<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/moneyDetail<%=ConfigController.getBuildVersion()%>.css"/>
    <script type="text/javascript" src="js/mask<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">
    </script>
</head>
<body>
<div class="i_searchBrand">
    <div class="i_arrow"></div>
    <div class="i_upLeft"></div>
    <div class="i_upCenter">
        <div class="i_note" id="div_drag">退现金或定金</div>
        <div class="i_close" id="sure_div_close"></div>
    </div>
    <div class="i_upRight"></div>
    <div class="i_upBody">
         <div class="moneyTotal">
            <div class="total"><span class="span">此单据已付款！请选择退款方式</span></div>
        </div>
        <div class="clear height"></div>
            <div class="clear height"></div>
            <div class="btnInput">
                 <input id="type" type="hidden" value=""  />
                <input id="sureCash" type="button" value="退现金" onfocus="this.blur();" />
                <input id="sureDeposit" type="button" value="退定金" onfocus="this.blur();"/>
            </div>
    </div>
    <div class="i_upBottom">
        <div class="i_upBottomLeft"></div>
        <div class="i_upBottomCenter"></div>
        <div class="i_upBottomRight"></div>
    </div>
</div>
</body>
</html>