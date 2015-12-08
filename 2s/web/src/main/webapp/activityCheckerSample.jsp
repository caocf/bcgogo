<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta content="text/html;charset=utf-8" http-equiv="content-type">
    <title>active checker sample</title>

    <script type="text/javascript" src="js/extension/jquery/jquery-1.4.2.min.js"></script>
    <script type="text/javascript" src="js/base.js"></script>
    <script type="text/javascript" src="js/application.js"></script>
    <script type="text/javascript" src="js/components/ui/bcgogo-activityChecker.js"></script>

    <script type="text/javascript">
        $(document).ready(function () {
            var activityChecker = new App.Module.ActivityChecker();

            activityChecker
                    .init({
                        interval:1000,
                        timeout:3500,
                        onInactivity:function() {
                            console.log("activityChecker is inactivity!");
                        }
                    })
                    .start();

            setInterval((function(checker) {
                return function() {
                    console.log(checker.status());
                };
            })(activityChecker), 1000);
        });
    </script>
</head>
<body>

</body>
</html>