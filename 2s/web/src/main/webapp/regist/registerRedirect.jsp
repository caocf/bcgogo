<%@ page import="com.bcgogo.config.service.IConfigService" %>
<%@ page import="com.bcgogo.service.ServiceManager" %>
<%@ page import="com.bcgogo.utils.ShopConstant" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    out.clearBuffer();
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    StringBuffer requestUrl = request.getRequestURL();
    String redirectUrl = configService.getConfig("RegisterRedirect", ShopConstant.BC_SHOP_ID);
    if (requestUrl.indexOf(configService.getConfig("CustomerRegister", ShopConstant.BC_SHOP_ID)) > -1) {
        redirectUrl += "shopRegister.do?method=registerMain&registerType=CUSTOMER&needVerify=true&data=" + Math.random();
    } else if (requestUrl.indexOf(configService.getConfig("SupplierRegister", ShopConstant.BC_SHOP_ID)) > -1) {
        redirectUrl += "shopRegister.do?method=registerMain&registerType=SUPPLIER&needVerify=true&data=" + Math.random();
    } else if (requestUrl.indexOf(configService.getConfig("SystemCustomerRegister", ShopConstant.BC_SHOP_ID)) > -1){
        redirectUrl += "shopRegister.do?method=registerMain&registerType=CUSTOMER&data=" + Math.random();
    } else if (requestUrl.indexOf(configService.getConfig("SystemSupplierRegister", ShopConstant.BC_SHOP_ID)) > -1){
        redirectUrl += "shopRegister.do?method=registerMain&registerType=SUPPLIER&data=" + Math.random();
    } else {
        redirectUrl += "shopRegister.do?method=registerMain&data=" + Math.random();
    }
    response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
    response.setHeader("Location", redirectUrl);
%>
