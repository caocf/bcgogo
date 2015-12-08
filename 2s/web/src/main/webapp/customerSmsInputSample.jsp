<%@ page import="com.bcgogo.config.ConfigController" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>customerSmsInputSample</title>

    <link rel="stylesheet" href="js/extension/jquery/plugin/jquery-ui/themes/redmond/jquery-ui-1.8.21.custom.css"/>
    <link rel="stylesheet" href="js/extension/jquery/plugin/jquery-ui/themes/redmond/jquery.ui.timepicker-addon.css"/>
    <link rel="stylesheet" href="js/extension/jquery/plugin/dataTables/css/jquery.dataTables_themeroller.css"/>
    <link rel="stylesheet" href="js/extension/jquery/plugin/jquery-tooltip/jquery.tooltip.css"/>
    <link rel="stylesheet" href="js/components/themes/bcgogo-customerSms-input<%=ConfigController.getBuildVersion()%>.css"/>

    <script type="text/javascript" src="js/extension/json2/json2.min.js"></script>
    <script type="text/javascript" src="js/extension/jquery/jquery-1.4.2.min.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery-ui/ui/jquery-ui-1.8.21.custom.min.js"></script>
    <script type="text/javascript" src="js/base<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/application<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/components/ui/bcgogo-customerSms-input<%=ConfigController.getBuildVersion()%>.js"></script>

    <script type="text/javascript">
        var customerSmsInput = APP_BCGOGO.Module.customerSmsInput;
        $(document).ready(function() {
            customerSmsInput.init({
                "selector":$("#testDiv"),
                "width":1000,
				"isEnableAddTask":false,
                "onSelectPerson":function(event){
                    // TODO
                },
                "onSave":function(event, isSendImmediately){
                    // TODO
                },
                "onClear":function(event){
                    customerSmsInput.clearData();
                }
            });

            var data = [
                {"name":"xxx", "mobile":"xxx", "userId":"xxxx-xxxx-xxxx-xxxx"}
            ];
            customerSmsInput.addData(data);


            // 常用方法
            // addData()
            // clearData()
            // getData()
//            getValuesByKey(key)   key -->   "name" | "mobile" | "userId"
            // delData(userId)

            // setState(state)     state -->   customerSmsInput.STATE.CREATE   |  customerSmsInput.STATE.CHANGE

        });


    </script>
</head>

<body>

    <div id="testDiv">
    </div>

</body>
</html>
