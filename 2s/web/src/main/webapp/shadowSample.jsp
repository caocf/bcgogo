<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta content="text/html;charset=utf-8;" http-equiv="Content-Type">
    <title>shadow</title>
    <link rel="stylesheet" href="js/components/themes/bcgogo-shadow.css">

    <script type="text/javascript" src="js/extension/jquery/jquery-1.4.2.min.js"></script>
    <script type="text/javascript" src="js/base.js"></script>
    <script type="text/javascript" src="js/application.js"></script>
    <script type="text/javascript" src="js/components/ui/bcgogo-shadow.js"></script>

    <script type="text/javascript">
        $(document).ready(function(){
            var shadow = App.Module.shadow;
            // 全透明
//            shadow.cover({x:100, y:100,w:100, h:100, isFullTransparent:true});
//            shadow.coverExcept({x:100, y:100,w:100, h:100, isFullTransparent:true});

            // 半透明
//            shadow.cover({x:100, y:100,w:100, h:100});
//            shadow.coverExcept({x:100, y:100,w:100, h:100});

            // 带边框的遮罩
//            shadow.cover({x:100, y:100,w:100, h:100, hasBorder:true});
            shadow.coverExcept({x:100, y:100,w:100, h:100, hasBorder:true});
        });
    </script>
</head>
<body>
<input type="button" value="bomb bomb bomb bomb bomb bomb bomb"/><br>
<input type="button" value=" bomb bomb bomb bomb bomb bomb bomb bomb"/><br>
<input type="button" value=" bomb bomb bomb bomb bomb bomb bomb bomb"/><br>
<input type="button" value=" bomb bomb bomb bomb bomb bomb bomb bomb"/><br>
<input type="button" value=" bomb bomb bomb bomb bomb bomb bomb bomb"/><br>
<input type="button" value=" bomb bomb bomb bomb bomb bomb bomb bomb"/><br>
<input type="button" value=" bomb bomb bomb bomb bomb bomb bomb bomb"/><br>
</body>
</html>