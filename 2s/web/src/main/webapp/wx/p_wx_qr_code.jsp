<%@ page import="com.bcgogo.config.ConfigController" %>
<%--
  Created by IntelliJ IDEA.
  User: ndong
  Date: 14-9-15
  Time: 下午5:58
  To change this template use File | Settings | File Templates.
--%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>打印微信二维码</title>
    <link rel="stylesheet" type="text/css" href="styles/wechat<%=ConfigController.getBuildVersion()%>.css">

    <script type="text/javascript">

    </script>
</head>
<body class="bodyMain">
<div id="wechat_scanning">
    <p><img src="${qr_code_show_url}" class="suitForImg" width="430" height="430" /> </p>
    <p>【扫一扫，关注微信公共号】 </p>
    <ol>
        <li>替代短信发送促销短信，祝福短信</li>
        <li>替代会员卡功能</li>
        <li>微信预约客户做服务</li>
        <li>实时促销</li>
        <li>发送微信电子账单</li>
        <li>一发微信对车主的好处：1 电子账单； 2 电子会员卡； 3 查询违章</li>
    </ol>
</div>
</body>
</html>