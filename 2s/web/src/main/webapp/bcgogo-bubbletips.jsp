<%@ page import="com.bcgogo.config.ConfigController" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=gb2312"/>
    <title>bcgogo-bubbletips</title>
    <link rel="stylesheet" href="js/extension/jquery/plugin/jquery-ui/themes/redmond/jquery-ui-1.8.21.custom.css"/>
    <link rel="stylesheet" href="js/components/themes/bcgogo-bubbletips.css"/>
    <script type="text/javascript" src="js/extension/jquery/jquery-1.4.2.min.js"></script>
    <script type="text/javascript" src="js/base<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/application<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/components/ui/bcgogo-bubbletips.js"></script>
    <script type="text/javascript" src="js/module/bcgogo-WebStorage.js"></script>
    <script language="javascript" type="text/javascript">
        $(function () {
            $("#demo1").bubbleTips();
            $("#demo2").bubbleTips({position: "b", triangleOffsetX: 35});
//            console.log(defaultStorage.getItem("hans"));
            defaultStorage.setItem(storageKey.Menus, "test");
//            console.log(defaultStorage.getItem("hans"));
//            defaultStorage.removeItem("hans");
//            console.log(defaultStorage.getItem("hans"));
//            defaultStorage.setItem("hans", "test");
//            defaultStorage.setItem("zjt", "test");
            console.log(defaultStorage.getItem(storageKey.Menus));
            defaultStorage.clear();
            console.log(defaultStorage.getItem(storageKey.Menus));
//            console.log(defaultStorage.getItem("zjt"));
//            defaultStorage.setItem("hans", "test");
        });


    </script>
</head>
<body>
<div style=";margin-top: 100px;"></div>
<div id="btip1"></div>
<div style="color:red;width: 130px" id="demo1" detail="<a href='http://www.jq-school.com/'>111</a><br />Jquery">
    1111111111111111
</div>
<div style=";margin-top: 30px;"></div>
<input type="text" id="demo2" detail="网页制作">
</body>
</html>
