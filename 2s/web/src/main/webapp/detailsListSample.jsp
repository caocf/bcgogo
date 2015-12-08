<%@ page import="com.bcgogo.config.ConfigController" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>historyListSample</title>

    <link rel="stylesheet" href="js/extension/jquery/plugin/jquery-ui/themes/redmond/jquery-ui-1.8.21.custom.css"/>
    <link rel="stylesheet" href="js/extension/jquery/plugin/jquery-tooltip/jquery.tooltip.css" />
    <!-- 		<link rel="stylesheet" href="js/extension/jquery/plugin/dataTables/css/jquery.dataTables.css"/> -->
    <link rel="stylesheet" href="js/extension/jquery/plugin/dataTables/css/jquery.dataTables_themeroller.css"/>
    <link rel="stylesheet" href="js/components/themes/bcgogo-detailsListPanel<%=ConfigController.getBuildVersion()%>.css"/>

    <script type="text/javascript" src="js/extension/jquery/jquery-1.4.2.min.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery-ui/ui/jquery-ui-1.8.21.custom.min.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/dataTables/js/jquery.dataTables.min.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery-tooltip/jquery.tooltip<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/extension/json2/json2.min.js"></script>
    <script type="text/javascript" src="js/base<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/application<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/components/ui/bcgogo-detailsList<%=ConfigController.getBuildVersion()%>.js"></script>

    <script type="text/javascript">
        var detailsList = APP_BCGOGO.Module.detailsList;

        $(document).ready(function() {
            var uuid = "sampleUUID";
            var data = '{"history":{"uuid":"sampleUUID", "totalCount":100, "data":[{"recommendedPrice":"1500.0","indexId":"01","product_model":"18寸","product_name":"轮胎","product_vehicle_brand":"别克","product_spec":"225","product_brand":"米其林","sellUnit":"条","inventoryNum":"100","product_vehicle_model":"君威","purchasePrice":"1000.0"},{"recommendedPrice":"700.0","indexId":"02","product_model":"5CM","product_name":"保险杠","product_vehicle_brand":"别克","product_spec":"","product_brand":"台湾","sellUnit":"根","inventoryNum":"10","product_vehicle_model":"君威","purchasePrice":"5000.0"},{"recommendedPrice":"700.0","indexId":"02","product_model":"5CM","product_name":"保险杠","product_vehicle_brand":"别克","product_spec":"","product_brand":"台湾","sellUnit":"根","inventoryNum":"10","product_vehicle_model":"君威","purchasePrice":"5000.0"},{"recommendedPrice":"700.0","indexId":"02","product_model":"5CM","product_name":"保险杠","product_vehicle_brand":"别克","product_spec":"","product_brand":"台湾","sellUnit":"根","inventoryNum":"10","product_vehicle_model":"君威","purchasePrice":"5000.0"},{"recommendedPrice":"700.0","indexId":"02","product_model":"5CM","product_name":"保险杠","product_vehicle_brand":"别克","product_spec":"","product_brand":"台湾","sellUnit":"根","inventoryNum":"10","product_vehicle_model":"君威","purchasePrice":"5000.0"},{"recommendedPrice":"700.0","indexId":"02","product_model":"5CM","product_name":"保险杠","product_vehicle_brand":"别克","product_spec":"","product_brand":"台湾","sellUnit":"根","inventoryNum":"10","product_vehicle_model":"君威","purchasePrice":"5000.0"},{"recommendedPrice":"700.0","indexId":"02","product_model":"5CM","product_name":"保险杠","product_vehicle_brand":"别克","product_spec":"","product_brand":"台湾","sellUnit":"根","inventoryNum":"10","product_vehicle_model":"君威","purchasePrice":"5000.0"},{"recommendedPrice":"700.0","indexId":"02","product_model":"5CM","product_name":"保险杠","product_vehicle_brand":"别克","product_spec":"","product_brand":"台湾","sellUnit":"根","inventoryNum":"10","product_vehicle_model":"君威","purchasePrice":"5000.0"},{"recommendedPrice":"700.0","indexId":"02","product_model":"5CM","product_name":"保险杠","product_vehicle_brand":"别克","product_spec":"","product_brand":"台湾","sellUnit":"根","inventoryNum":"10","product_vehicle_model":"君威","purchasePrice":"5000.0"},{"recommendedPrice":"700.0","indexId":"02","product_model":"5CM","product_name":"保险杠","product_vehicle_brand":"别克","product_spec":"","product_brand":"台湾","sellUnit":"根","inventoryNum":"10","product_vehicle_model":"君威","purchasePrice":"5000.0"},{"recommendedPrice":"700.0","indexId":"02","product_model":"5CM","product_name":"保险杠","product_vehicle_brand":"别克","product_spec":"","product_brand":"台湾","sellUnit":"根","inventoryNum":"10","product_vehicle_model":"君威","purchasePrice":"5000.0"},{"recommendedPrice":"700.0","indexId":"02","product_model":"5CM","product_name":"保险杠","product_vehicle_brand":"别克","product_spec":"","product_brand":"台湾","sellUnit":"根","inventoryNum":"10","product_vehicle_model":"君威","purchasePrice":"5000.0"}]}}';
            detailsList.init({
                "selector":$("#testTable"),
                "categoryList":[{"name":"product_model","title":"规格"}, {"name":"product_vehicle_model,product_vehicle_brand","title":"车型/车辆品牌"}],
                "onDbClick":function(event, index, data){
                    detailsList.hide();
                    // TODO ... 比如将选中的值赋到表格中
                } ,
                "pageSize":4
            });
            detailsList.setUUID(uuid);
            detailsList.show();
            detailsList.draw(JSON.parse(data)["history"]);
        });
    </script>
</head>

<body>

<div id="testTable">
</div>

</body>
</html>
