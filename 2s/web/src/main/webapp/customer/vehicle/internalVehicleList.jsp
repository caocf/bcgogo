<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>内部车辆管理</title>
    <%@include file="/WEB-INF/views/header_script.jsp" %>

    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/register<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/prompt_box<%=ConfigController.getBuildVersion()%>.css"/>

    <%--<link rel="stylesheet" type="text/css" href="js/extension/jquery/jquery-easyui/jquery-easyui-1.4.1/themes/default/easyui.css" />--%>
    <%--<link rel="stylesheet" type="text/css" href="js/extension/jquery/jquery-easyui/jquery-easyui-1.4.1/themes/icon.css" />--%>
    <%--<script type="text/javascript" src="js/extension/jquery/jquery-easyui/jquery-easyui-1.4.1/jquery.min.js"></script>--%>
    <%--<script type="text/javascript" src="js/extension/jquery/jquery-easyui/jquery-easyui-1.4.1/jquery.easyui.min.js"></script>--%>

    <%--<script type="text/javascript" src="js/module/invoiceCommon<%=ConfigController.getBuildVersion()%>.js"></script>--%>
    <%--<script type="text/javascript" src="js/suggestion<%=ConfigController.getBuildVersion()%>.js"></script>--%>
    <%--<script type="text/javascript" src="js/vehicleValidator<%=ConfigController.getBuildVersion()%>.js"></script>--%>
    <%--<script type="text/javascript" src="js/checkMobileAndTelphone<%=ConfigController.getBuildVersion()%>.js"></script>--%>

    <%--<script type="text/javascript" src="js/components/ui/bcgogo-droplist.js"></script>--%>
    <%--<script type="text/javascript"--%>
            <%--src="js/page/customer/vehicle/customerVehicleBasicFunction<%=ConfigController.getBuildVersion()%>.js"></script>--%>

    <%--<script type="text/javascript"--%>
            <%--src="js/page/customer/vehicle/vehicleDetail<%=ConfigController.getBuildVersion()%>.js"></script>--%>

    <script type="text/javascript">
        defaultStorage.setItem(storageKey.MenuUid, "WEB.CUSTOMER_MANAGER.VEHICLE_MANAGE.INTERNAL_VEHICLE_MANAGER");
    </script>
</head>
<body class="bodyMain">
<%@include file="/WEB-INF/views/header_html.jsp" %>
<div class="i_main clear">
<iframe style="width: 1000px;height:750px;border: 0" src="internalVehicle.do?method=internalVehicleListContentPage"></iframe>
</div>
<%@ include file="/common/messagePrompt.jsp" %>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>
