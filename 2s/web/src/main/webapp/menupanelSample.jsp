<%@ page import="com.bcgogo.config.ConfigController" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta content="text/html;charset=utf-8;" http-equiv="content-type">
    <title>menu panel</title>

    <link rel="stylesheet" href="js/components/themes/bcgogo-menupanel.css">

    <style type="text/css">
            /*body{background: #a4bdec;}*/
    </style>

    <script type="text/javascript" src="js/extension/jquery/jquery-1.4.2.min.js"></script>
    <script type="text/javascript" src="js/base.js"></script>
    <script type="text/javascript" src="js/application.js"></script>
    <script type="text/javascript" src="js/components/ui/bcgogo-menupanel.js"></script>

    <script type="text/javascript">
        $(document).ready(function () {
            var menuPanel = new App.Module.MenuPanel();

            $("#testDiv,#testDiv2,#testDiv3,#testDiv4").bind("mouseenter", function (event) {

                menuPanel.remove();
                var param = {
                    data: {
                        "root": "xxxxx",
                        "href": "xxx",
                        "item": [
                            {
                                "label": "title01",
                                "href": "",
                                "item": [
                                    {
                                        "label": "item1YUIMyLover",
                                        "href": ""
                                    },
                                    {
                                        "label": "item2",
                                        "href": ""
                                    },
                                    {
                                        "label": "item3",
                                        "href": ""
                                    },
                                    {
                                        "label": "item4",
                                        "href": ""
                                    }
                                ]
                            },
                            {
                                "label": "title02",
                                "href": "",
                                "item": [
                                    {
                                        "label": "item1",
                                        "href": ""
                                    },
                                    {
                                        "label": "item2",
                                        "href": ""
                                    },
                                    {
                                        "label": "item3",
                                        "href": ""
                                    },
                                    {
                                        "label": "item4",
                                        "href": ""
                                    },
                                    {
                                        "label": "item4",
                                        "href": ""
                                    },
                                    {
                                        "label": "item4",
                                        "href": ""
                                    }
                                ]
                            },
                            {
                                "label": "title03",
                                "href": "",
                                "item": [
                                    {
                                        "label": "item1",
                                        "href": ""
                                    },
                                    {
                                        "label": "item2",
                                        "href": ""
                                    },
                                    {
                                        "label": "item3",
                                        "href": ""
                                    },
                                    {
                                        "label": "item4",
                                        "href": ""
                                    }
                                ]
                            },
                            {
                                "label": "title04",
                                "href": "",
                                "item": [
                                    {
                                        "label": "item1",
                                        "href": ""
                                    },
                                    {
                                        "label": "item2",
                                        "href": ""
                                    },
                                    {
                                        "label": "item3",
                                        "href": ""
                                    },
                                    {
                                        "label": "item4",
                                        "href": ""
                                    }
                                ]
                            },
                            {
                                "label": "title05",
                                "href": "",
                                "item": [
                                    {
                                        "label": "item1",
                                        "href": ""
                                    },
                                    {
                                        "label": "item2",
                                        "href": ""
                                    },
                                    {
                                        "label": "item3",
                                        "href": ""
                                    },
                                    {
                                        "label": "item4",
                                        "href": ""
                                    },
                                    {
                                        "label": "item5",
                                        "href": ""
                                    },
                                    {
                                        "label": "item6",
                                        "href": ""
                                    },
                                    {
                                        "label": "item7",
                                        "href": ""
                                    },
                                    {
                                        "label": "item8",
                                        "href": ""
                                    }
                                ]
                            }
                        ]
                    },
                    config: {
                        autoTurnning:false,
                        column: 3,
                        align: "left",
                        manualPosition: false,
                        hookSelector: $(this),
                        "z-index": 20,
                        "onSelect": function (road, event) {
                            G.info(road);
                        }
                    }
                };


                if (G.contains($(this).attr("id"), ["testDiv4"])) {
                    param.config.align = "right";
                }

                menuPanel.show(param);
            });
        });
    </script>

</head>
<body style="width: 1000px;position: relative;">

<div id="testDiv" style="width:100px;height: 100px;margin-left: 200px;border: solid 1px red;float:left;"></div>
<div id="testDiv2" style="width:100px;height: 100px;margin-left:20px;border: solid 1px red;float:left;"></div>
<div id="testDiv3" style="width:100px;height: 100px;margin-left:20px;border: solid 1px red;float:left;"></div>
<div id="testDiv4" style="width:100px;height: 100px;margin-left:20px;border: solid 1px red;float:left;"></div>

</body>
</html>