<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <title></title>

    <meta content="text/html;charset=utf-8" http-equiv="content-type"/>

    <link rel="stylesheet" href="js/components/themes/bcgogo-guideTip.css">

    <script type="text/javascript" src="js/extension/jquery/jquery-1.8.3.min.js"></script>
    <script type="text/javascript" src="js/base.js"></script>
    <script type="text/javascript" src="js/application.js"></script>
    <script type="text/javascript" src="js/components/ui/bcgogo-guideTip.js"></script>
    <script type="text/javascript">
        var panel = new App.Module.GuideTipPanel();
        var okButton = new App.Module.GuideTipOkButton();
        var cancelButton = new App.Module.GuideTipCancelButton();
        var startPanel = new App.Module.GuideTipStartPanel();
        $(document).ready(function(){

            panel.show({
                backgroundImageUrl:panel.IMG.L_B_big_blue,
                left:40,
                top:40,
                content:{
                    htmlText:"<span>Hello World!</span>",
                    // default 40
                    top:40,
                    // default 35
                    left:35
                },
                "z-index":panel.Z_INDEX.TOP
            });

            okButton.show({
                left:100,
                top:100,
                label:"我是阿甘",
                click:function(event) {
                    alert("点了 ok button!");
                },
                "z-index":okButton.Z_INDEX.TOP
            });

            cancelButton.show({
                left:200,
                top:200,
                click:function(event) {
                    alert("点了 cancel button!");
                },
                "z-index":cancelButton.Z_INDEX.TOP
            });

            startPanel.show({
                left:400,
                top:100,

                associated:{
                    label:"关联",
                    "click":function(event){
                        alert("关联按钮!");
                    }
                },
                drop:{
                    label:"以后不再提示",
                    "click":function(event){
                        alert("放弃按钮!");
                    }
                },

                "z-index":startPanel.Z_INDEX.TOP
            });
        });
    </script>
</head>
<body>

</body>
</html>