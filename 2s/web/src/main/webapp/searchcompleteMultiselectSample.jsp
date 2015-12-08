<%@ page import="com.bcgogo.config.ConfigController" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta content="text/html;charset=utf-8;" http-equiv="Content-Type" />
    <title>searchcompleteSample</title>

    <%--<link rel="stylesheet" href="css/CSSReset.css"/>--%>
    <link rel="stylesheet" href="js/extension/jquery/plugin/jquery-ui/themes/redmond/jquery-ui-1.8.21.custom.css"/>
    <link rel="stylesheet" href="js/extension/jquery/plugin/jquery-ui/themes/redmond/jquery.ui.timepicker-addon.css"/>
    <link rel="stylesheet" href="js/extension/jquery/plugin/dataTables/css/jquery.dataTables_themeroller.css"/>
    <link rel="stylesheet" href="js/extension/jquery/plugin/jquery-tooltip/jquery.tooltip.css" />
    <link rel="stylesheet" href="js/components/themes/bcgogo-searchcomplete<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" href="js/components/themes/bcgogo-autocomplete<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" href="js/components/themes/bcgogo-detailsListPanel<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" href="js/components/themes/bcgogo-autocomplete.css"/>
    <link rel="stylesheet" href="js/components/themes/bcgogo-detailsListPanel.css"/>

    <style type="text/css">
        body{
            background-color: #000000;
        }

        #tx1, #tx2, #tx3 {
            display: block;
            margin-left: auto;
            margin-right: auto;
        }
    </style>

    <script type="text/javascript" src="js/extension/jquery/jquery-1.4.2.min.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery-ui/ui/jquery-ui-1.8.21.custom.min.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery-ui/ui/jquery.ui.timepicker-addon.min.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/dataTables/js/jquery.dataTables.min.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery-tooltip/jquery.tooltip.min.js"></script>
    <script type="text/javascript" src="js/base<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/application<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/components/ui/bcgogo-autocomplete-multiselect<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/components/ui/bcgogo-detailsList-multiselect<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/components/ui/bcgogo-searchcomplete-multiselect<%=ConfigController.getBuildVersion()%>.js"></script>


    <script type="text/javascript">
        // get dummy data
//        var detailsListData = '{"uuidValue":[{"recommendedPrice":"1500.0","indexId":"01","product_model":"18寸","product_name":"轮胎","product_vehicle_brand":"别克","product_spec":"225","product_brand":"米其林","sellUnit":"条","inventoryNum":"100","product_vehicle_model":"君威","purchasePrice":"1000.0"},{"recommendedPrice":"700.0","indexId":"02","product_model":"5CM","product_name":"保险杠","product_vehicle_brand":"别克","product_spec":"","product_brand":"台湾","sellUnit":"根","inventoryNum":"10","product_vehicle_model":"君威","purchasePrice":"5000.0"},{"recommendedPrice":"1500.0","indexId":"01","product_model":"18寸","product_name":"轮胎","product_vehicle_brand":"别克","product_spec":"225","product_brand":"米其林","sellUnit":"条","inventoryNum":"100","product_vehicle_model":"君威","purchasePrice":"1000.0"},{"recommendedPrice":"700.0","indexId":"02","product_model":"5CM","product_name":"保险杠","product_vehicle_brand":"别克","product_spec":"","product_brand":"台湾","sellUnit":"根","inventoryNum":"10","product_vehicle_model":"君威","purchasePrice":"5000.0"},{"recommendedPrice":"1500.0","indexId":"01","product_model":"18寸","product_name":"轮胎","product_vehicle_brand":"别克","product_spec":"225","product_brand":"米其林","sellUnit":"条","inventoryNum":"100","product_vehicle_model":"君威","purchasePrice":"1000.0"},{"recommendedPrice":"700.0","indexId":"02","product_model":"5CM","product_name":"保险杠","product_vehicle_brand":"别克","product_spec":"","product_brand":"台湾","sellUnit":"根","inventoryNum":"10","product_vehicle_model":"君威","purchasePrice":"5000.0"},{"recommendedPrice":"1500.0","indexId":"01","product_model":"18寸","product_name":"轮胎","product_vehicle_brand":"别克","product_spec":"225","product_brand":"米其林","sellUnit":"条","inventoryNum":"100","product_vehicle_model":"君威","purchasePrice":"1000.0"},{"recommendedPrice":"700.0","indexId":"02","product_model":"5CM","product_name":"保险杠","product_vehicle_brand":"别克","product_spec":"","product_brand":"台湾","sellUnit":"根","inventoryNum":"10","product_vehicle_model":"君威","purchasePrice":"5000.0"},{"recommendedPrice":"1500.0","indexId":"01","product_model":"18寸","product_name":"轮胎","product_vehicle_brand":"别克","product_spec":"225","product_brand":"米其林","sellUnit":"条","inventoryNum":"100","product_vehicle_model":"君威","purchasePrice":"1000.0"},{"recommendedPrice":"700.0","indexId":"02","product_model":"5CM","product_name":"保险杠","product_vehicle_brand":"别克","product_spec":"","product_brand":"台湾","sellUnit":"根","inventoryNum":"10","product_vehicle_model":"君威","purchasePrice":"5000.0"},{"recommendedPrice":"1500.0","indexId":"01","product_model":"18寸","product_name":"轮胎","product_vehicle_brand":"别克","product_spec":"225","product_brand":"米其林","sellUnit":"条","inventoryNum":"100","product_vehicle_model":"君威","purchasePrice":"1000.0"},{"recommendedPrice":"700.0","indexId":"02","product_model":"5CM","product_name":"保险杠","product_vehicle_brand":"别克","product_spec":"","product_brand":"台湾","sellUnit":"根","inventoryNum":"10","product_vehicle_model":"君威","purchasePrice":"5000.0"},{"recommendedPrice":"1500.0","indexId":"01","product_model":"18寸","product_name":"轮胎","product_vehicle_brand":"别克","product_spec":"225","product_brand":"米其林","sellUnit":"条","inventoryNum":"100","product_vehicle_model":"君威","purchasePrice":"1000.0"},{"recommendedPrice":"700.0","indexId":"02","product_model":"5CM","product_name":"保险杠","product_vehicle_brand":"别克","product_spec":"","product_brand":"台湾","sellUnit":"根","inventoryNum":"10","product_vehicle_model":"君威","purchasePrice":"5000.0"},{"recommendedPrice":"1500.0","indexId":"01","product_model":"18寸","product_name":"轮胎","product_vehicle_brand":"别克","product_spec":"225","product_brand":"米其林","sellUnit":"条","inventoryNum":"100","product_vehicle_model":"君威","purchasePrice":"1000.0"},{"recommendedPrice":"700.0","indexId":"02","product_model":"5CM","product_name":"保险杠","product_vehicle_brand":"别克","product_spec":"","product_brand":"台湾","sellUnit":"根","inventoryNum":"10","product_vehicle_model":"君威","purchasePrice":"5000.0"}]}';
//        var detailsListJsonData = JSON.parse(detailsListData);

//        var detailsListJsonData = null;

        var totalData = '{"uuid":"uuidValue","history":{"uuid":"uuidValue","data":[{"recommendedPrice":"1500.0","indexId":"01","product_model":"18寸","product_name":"轮胎","product_vehicle_brand":"别克","product_spec":"225","product_brand":"米其林","sellUnit":"条","inventoryNum":"100","product_vehicle_model":"君威","purchasePrice":"1000.0"},{"recommendedPrice":"700.0","indexId":"02","product_model":"5CM","product_name":"保险杠","product_vehicle_brand":"别克","product_spec":"","product_brand":"台湾","sellUnit":"根","inventoryNum":"10","product_vehicle_model":"君威","purchasePrice":"5000.0"},{"recommendedPrice":"1500.0","indexId":"01","product_model":"18寸","product_name":"轮胎","product_vehicle_brand":"别克","product_spec":"225","product_brand":"米其林","sellUnit":"条","inventoryNum":"100","product_vehicle_model":"君威","purchasePrice":"1000.0"},{"recommendedPrice":"700.0","indexId":"02","product_model":"5CM","product_name":"保险杠","product_vehicle_brand":"别克","product_spec":"","product_brand":"台湾","sellUnit":"根","inventoryNum":"10","product_vehicle_model":"君威","purchasePrice":"5000.0"},{"recommendedPrice":"1500.0","indexId":"01","product_model":"18寸","product_name":"轮胎","product_vehicle_brand":"别克","product_spec":"225","product_brand":"米其林","sellUnit":"条","inventoryNum":"100","product_vehicle_model":"君威","purchasePrice":"1000.0"},{"recommendedPrice":"700.0","indexId":"02","product_model":"5CM","product_name":"保险杠","product_vehicle_brand":"别克","product_spec":"","product_brand":"台湾","sellUnit":"根","inventoryNum":"10","product_vehicle_model":"君威","purchasePrice":"5000.0"},{"recommendedPrice":"1500.0","indexId":"01","product_model":"18寸","product_name":"轮胎","product_vehicle_brand":"别克","product_spec":"225","product_brand":"米其林","sellUnit":"条","inventoryNum":"100","product_vehicle_model":"君威","purchasePrice":"1000.0"},{"recommendedPrice":"700.0","indexId":"02","product_model":"5CM","product_name":"保险杠","product_vehicle_brand":"别克","product_spec":"","product_brand":"台湾","sellUnit":"根","inventoryNum":"10","product_vehicle_model":"君威","purchasePrice":"5000.0"},{"recommendedPrice":"1500.0","indexId":"01","product_model":"18寸","product_name":"轮胎","product_vehicle_brand":"别克","product_spec":"225","product_brand":"米其林","sellUnit":"条","inventoryNum":"100","product_vehicle_model":"君威","purchasePrice":"1000.0"},{"recommendedPrice":"700.0","indexId":"02","product_model":"5CM","product_name":"保险杠","product_vehicle_brand":"别克","product_spec":"","product_brand":"台湾","sellUnit":"根","inventoryNum":"10","product_vehicle_model":"君威","purchasePrice":"5000.0"},{"recommendedPrice":"1500.0","indexId":"01","product_model":"18寸","product_name":"轮胎","product_vehicle_brand":"别克","product_spec":"225","product_brand":"米其林","sellUnit":"条","inventoryNum":"100","product_vehicle_model":"君威","purchasePrice":"1000.0"},{"recommendedPrice":"700.0","indexId":"02","product_model":"5CM","product_name":"保险杠","product_vehicle_brand":"别克","product_spec":"","product_brand":"台湾","sellUnit":"根","inventoryNum":"10","product_vehicle_model":"君威","purchasePrice":"5000.0"},{"recommendedPrice":"1500.0","indexId":"01","product_model":"18寸","product_name":"轮胎","product_vehicle_brand":"别克","product_spec":"225","product_brand":"米其林","sellUnit":"条","inventoryNum":"100","product_vehicle_model":"君威","purchasePrice":"1000.0"},{"recommendedPrice":"700.0","indexId":"02","product_model":"5CM","product_name":"保险杠","product_vehicle_brand":"别克","product_spec":"","product_brand":"台湾","sellUnit":"根","inventoryNum":"10","product_vehicle_model":"君威","purchasePrice":"5000.0"},{"recommendedPrice":"1500.0","indexId":"01","product_model":"18寸","product_name":"轮胎","product_vehicle_brand":"别克","product_spec":"225","product_brand":"米其林","sellUnit":"条","inventoryNum":"100","product_vehicle_model":"君威","purchasePrice":"1000.0"},{"recommendedPrice":"700.0","indexId":"02","product_model":"5CM","product_name":"保险杠","product_vehicle_brand":"别克","product_spec":"","product_brand":"台湾","sellUnit":"根","inventoryNum":"10","product_vehicle_model":"君威","purchasePrice":"5000.0"}]},"dropDown":{"uuid":"uuidValue","data":[{"details":{"product_name":"轮胎"},"label":"轮胎","type":"option"},{"details":{"product_name":"保险杠"},"label":"保险杠","type":"option"}]}}';
        var totalJsonData = JSON.parse(totalData);

        var searchcompleteMultiselect = APP_BCGOGO.Module.searchcompleteMultiselect;

        // init ready
        $().ready(function() {

            searchcompleteMultiselect.init({
                "selector":$("#id-searchcompleteMultiselect"),
                // 必须
                "detailsListCategoryList":[{"name":"product_model","title":"规格"}, {"name":"product_vehicle_model,product_vehicle_brand","title":"车型/车辆品牌"}]
//                ,
//                // 必须
//                "autocompleteClick":function(event){},
//                // 不是必须
//                "detailsListSelect":function(event, index, data){},
                  // necessary
//                  "onDetialsListDbClick":function(event, index, data){}
//                // 必须
//                "onMore":function(event){},
//                // 必须
//                "onFinish":function(event, indexList, dataList){}

            });
            searchcompleteMultiselect.hide();
            searchcompleteMultiselect.hide("autocomplete");
            searchcompleteMultiselect.hide("detailsList");


            $("#tx1,#tx2,#tx3,#tx4,#tx5").bind("focus", function(event) {
                if( $(this).attr("id") == "tx5" ) {
                    searchcompleteMultiselect.changeThemes(searchcompleteMultiselect.themes.SMALL);
                }else {
                    searchcompleteMultiselect.changeThemes(searchcompleteMultiselect.themes.NORMAL);
                }
                searchcompleteMultiselect.hide();
                searchcompleteMultiselect.hide("autocomplete");
                searchcompleteMultiselect.hide("detailsList");

                searchcompleteMultiselect.setUUID("uuidValue");
                searchcompleteMultiselect.moveFollow({node:event.currentTarget});

                searchcompleteMultiselect.show();
                searchcompleteMultiselect.show("autocomplete");
                searchcompleteMultiselect.show("detailsList");

                searchcompleteMultiselect.draw(totalJsonData);

//                searchcompleteMultiselect.draw(autocompleteJsonData, "autocomplete");
//                searchcompleteMultiselect.draw(detailsListJsonData, "detailsList");
            });

            $(document).bind("click", function(event) {
                var selectorArray = [$(".bcgogo-ui-searchcomplete-autocomplete,.bcgogo-ui-searchcomplete-detailsList", searchcompleteMultiselect._target), "[id^=\"tx\"]"];
                if ($(event.target).closest(selectorArray).length == 0) {
                    searchcompleteMultiselect.hide();
                    searchcompleteMultiselect.hide("autocomplete");
                    searchcompleteMultiselect.hide("detailsList");
                }
            });

        });
    </script>

</head>

<body>

<input type="text" id="tx1"/>
<input type="text" id="tx2"/>
<input type="text" id="tx3"/>
<input type="text" id="tx4"/>
<input type="text" id="tx5"/>

<div id="id-searchcompleteMultiselect" style="position:absolute;">
</div>

</body>

</html>
