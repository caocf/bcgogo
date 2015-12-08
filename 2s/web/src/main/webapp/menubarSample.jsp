<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.bcgogo.config.ConfigController" %>
<html>
<head>
    <meta content="text/html;charset=utf-8" http-equiv="content-type" />
    <title>menu bar</title>

    <link rel="stylesheet" href="js/components/themes/bcgogo-menuRoad.css"/>
    <script type="text/javascript" src="js/extension/jquery/jquery-1.4.2.min.js"></script>
    <script type="text/javascript" src="js/base.js"></script>
    <script type="text/javascript" src="js/application.js"></script>
    <script type="text/javascript" src="js/components/ui/bcgogo-menupanel.js"></script>

    <script type="text/javascript">
        $(document).ready(function(){
            // TODO demo
            var testData = {
                "root":"首页",
                "href":"",
                "uid":"00067730-0a93-410a-c398-d3b0a25a6492",
                "item":[
                    {
                        "label":"进销存进销存进销存进销存",
                        "href":"",
                        "uid":"762a974a-5e31-f4a3-4690-5790a22f019f",
                        "item":[
                            {
                                "label":"入库单",
                                "href":"",
                                "uid":"2f644fb8-f7be-e212-0cfe-afa6884de6a5"
                            }
                        ]
                    },
                    {
                        "label":"采购单采购单采购单采购单采购单",
                        "href":"",
                        "uid":"2ff90f3e-c144-b6d3-9799-8bb04cea12cb",
                        "item":[
                            {
                                "label":"采购单",
                                "href":"",
                                "uid":"2f644fb8-f7be-e212-0cfe-afa68889dfbc"
                            }
                        ]
                    }
                ]
            };


            var menuDataProxy = new App.Module.MenuDataProxy();

            var menubar = new App.Module.MenuBar();
            menubar.show({
                data:testData,
                road:menuDataProxy.getRoad(testData, "2f644fb8-f7be-e212-0cfe-afa6884de6a5", true),
                config:{
                    selector:"#testDiv",
                    autoTurnning:false,
                    onSelect:function(road, data, uid, event) {
                        G.info(road);
                        G.info(data);
                        G.info(uid);
                    }
                }
            });

            window._$ = menubar._$;
        });
    </script>

</head>
<body>

<div id="testDiv">
</div>


</body>
</html>