<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title></title>
    <meta content="text/html;charset=utf-8;" http-equiv="content-type" />

    <link rel="stylesheet" href="styles/CSSReset.css"/>
    <link rel="stylesheet" href="js/extension/jquery/plugin/jquery-ui/themes/redmond/jquery-ui-1.8.21.custom.css"/>
    <link rel="stylesheet" href="js/extension/jquery/plugin/jquery-ui/themes/base/jquery.ui.timepicker-addon.css"/>
    <style type="text/css">
        .ui-dialog, .ui-widget {
            font-family: "Trebuchet MS", "Helvetica", "Arial",  "Verdana", "sans-serif";
            font-size: 80%;
        }
    </style>

    <script type="text/javascript" src="js/extension/jquery/jquery-1.4.2.min.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery-ui/ui/jquery-ui-1.8.21.custom.min.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery-ui/ui/i18n/jquery.ui.datepicker-zh-CN.min.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery-ui/ui/jquery.ui.timepicker-addon.js"></script>
    <script type="text/javascript" src="js/base.js"></script>
    <script type="text/javascript" src="js/application.js"></script>

    <script type="text/javascript" charset="utf-8">
        $(document).ready(function() {
            $("#test")
                .datetimepicker({
                    "numberOfMonths" : 1,
                    "showButtonPanel": true,
                    "changeYear":true,
                    "changeMonth":true,
                    "yearSuffix":"",
                    "yearRange":"c-100:c+100"
                })
                .bind("click", function(event) {
                    $(this).blur();
                });
        });
    </script>
</head>
<body>
<input type="text" id="test" class="test" style="margin-left:300px"/>
</body>
</html>