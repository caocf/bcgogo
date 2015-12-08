<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta content="text/html;charset=utf-8;" http-equiv="content-type" />
    <title>timepicker</title>
    <link rel="stylesheet" href="js/extension/jquery/plugin/jquery-ui/themes/redmond/jquery-ui-1.8.21.custom.css"/>
    <link rel="stylesheet" href="js/extension/jquery/plugin/jquery-ui/themes/redmond/jquery.ui.timepicker-addon.css"/>

    <style type="text/css">
        .ui-widget, .ui-dialog {
            font-size:80%;
        }
    </style>


    <script type="text/javascript" src="js/extension/jquery/jquery-1.4.2.min.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery-ui/ui/jquery-ui-1.8.21.custom.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery-ui/ui/i18n/jquery.ui.datepicker-zh-CN.min.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery-ui/ui/jquery.ui.timepicker-addon.js"></script>
    <script type="text/javascript" src="js/base.js"></script>
    <script type="text/javascript" src="js/application.js"></script>

    <script type="text/javascript">
        $(document).ready(function () {
            $(".test_timepicker")
                .datetimepicker({
                    showButtonPanel: true,
                    yearSuffix: "",
                    yearRange: "c-100:c+100",
                    changeYear: true,
                    changeMonth: true,
                    showHour: true,
                    showMinute: true
//                        ,
//                    showSlider:false
                })
                .bind("focus click", function (event) {
                    $(this).blur();
                });
        });
    </script>

</head>
<body>

<input type="text" class="test_timepicker" />

</body>
</html>