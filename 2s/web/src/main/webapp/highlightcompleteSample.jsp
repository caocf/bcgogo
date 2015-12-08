<%@ page import="com.bcgogo.config.ConfigController" %>
<%--
  Created by IntelliJ IDEA.
  User: lenovo
  Date: 12-8-22
  Time: 上午11:31
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Simple jsp page</title>
    <link rel="stylesheet" href="css/CSSReset.css"/>
    <link rel="stylesheet" href="js/extension/jquery/plugin/jquery-ui/themes/redmond/jquery-ui-1.8.21.custom.css"/>

    <script type="text/javascript" src="js/extension/jquery/jquery-1.4.2.min.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery-ui/ui/jquery-ui-1.8.21.custom.js"></script>
    <script type="text/javascript" src="js/base<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/application<%=ConfigController.getBuildVersion()%>.js"></script>

    <%-- load highlightcomplete ui --%>
    <script type="text/javascript" src="js/components/ui/bcgogo-highlightcomplete<%=ConfigController.getBuildVersion()%>.js"></script>

    <script type="text/javascript">
        var highlightcomplete = APP_BCGOGO.Module.highlightcomplete;

        $(document).ready(function() {
            var inputtingTimerId = 0;
            $("#inputtingText").bind("keyup", function(event) {
                clearTimeout(inputtingTimerId);
                var keyName = GLOBAL.Interactive.keyNameFromEvent(event);
                if (keyName.search(/left|up|right|down|enter|backspace/g) == -1) {
                    inputtingTimerId = setTimeout(function() {
                        highlightcomplete.complete({
                            "selector":$("#inputtingText"),
                            "value":$("#targetCompareText").val()
                        });
                    }, 300);
                }
            });
        });
    </script>

</head>
<body>
Place your content here
<input type="text" id="inputtingText" value=""/>
<input type="text" id="targetCompareText" value="avlon"/>

</body>
</html>