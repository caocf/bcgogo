<%--
  Created by IntelliJ IDEA.
  User: jinyuan
  Date: 13-6-21
  Time: 上午11:19
  To change this template use File | Settings | File Templates.
--%>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<%@ page import="com.bcgogo.config.ConfigController" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <title>公司简介</title>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
    <%@include file="/WEB-INF/views/header_script.jsp" %>
</head>
<body>
<%@include file="/WEB-INF/views/header_html.jsp" %>
<div class="i_main clear">
    <div class="clear i_height"></div>
    <div class="titBody" style="height:300px;">
        <div class="lineTitle">
            公司简介
        </div>
        <div class="lineBody">
            <div style="line-height:28px; font-size:14px; padding-top:10px;">
                &nbsp;&nbsp;&nbsp;&nbsp;苏州统购信息科技有限公司是一家以美国硅谷海归博士为技术班底的高科技企业.公司开发的一发(EasyPower)汽车服务管理系统是国内第一款以汽修,汽车美容,汽配ERP管理为基础,整合汽车后市场服务系统的智能型云计算概念软件.公司致力于打造国内第一家整合车主,汽车服务提供商,汽车用品配件供应商三方于一体的一站式汽车服务网络平台。
            </div>
            <div style="line-height:28px; font-size:14px;">
                &nbsp;&nbsp;&nbsp;&nbsp;苏州统购信息科技有限公司与中国农业银行、中国工商银行、中国银行、华夏银行、财付通、支付宝、快钱等多家金融机构以及第三方支付建立了紧密的合作关系，在三年内打通全 国60万家店面和8万家供货厂商之间的供销通道，建立全覆盖的交易网络。实现全国范围内的汽车配件和用品从厂家到零售终端的直通交易。让销售渠道扁平化， 让采购管理更容易，让信息流通更简单，让新品上市更快捷，让融资渠道更方便。

            </div>
        </div>
        <div class="lineBottom"></div>
    </div>
</div>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>