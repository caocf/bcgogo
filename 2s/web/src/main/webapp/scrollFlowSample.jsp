<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>

<head>
    <meta content="text/html;charset=utf-8;" http-equiv="content-type"/>
    <title></title>
    <style type="text/css">
        .JScrollGroup {
            border: solid 1px #00f;
        }

        .JScrollItem {
            border: solid 1px #f00;
        }
    </style>

    <link rel="stylesheet" href="/js/components/themes/bcgogo-scrollFlow.css"/>

    <script type="text/javascript" charset="utf-8" src="/js/extension/jquery/jquery-1.4.2.min.js"></script>
    <script type="text/javascript" charset="utf-8" src="/js/base.js"></script>
    <script type="text/javascript" charset="utf-8" src="/js/application.js"></script>
    <script type="text/javascript" charset="utf-8" src="/js/components/ui/bcgogo-scrollFlow.js"></script>

    <script type="text/javascript">
        $(function () {
            var scrollFlowVertical = new App.Module.ScrollFlowVertical();
            scrollFlowVertical
                    .init({
                        "selector": "#testComponent",
                        "width": 600,
                        "height": 300,
                        "background": "#fff",
                        "scrollInterval": 3000,
                        "isShowPagination":false,
                        "onNextComplete": function () {

                        },
                        "onPrevComplete": function () {

                        }
                    })
                    .startAutoScroll();

            var scrollFlowHorizontal = new App.Module.ScrollFlowHorizontal();
            scrollFlowHorizontal
                    .init({
                        "selector": "#testHorizontal",
                        "width": 200,
                        "height": 50,
                        "background": "#fff",
                        "scrollInterval": 2000
                    })
                    .startAutoScroll();

            window.scrollFlowVertical = scrollFlowVertical;
            window.scrollFlowHorizontal = scrollFlowHorizontal;
        });

    </script>
</head>

<body style="background:#000;">

<div id="testComponent" class="JScrollFlowVertical">
    <ul class="JScrollGroup">
        <li class="JScrollItem">
            11111111
        </li>
        <li class="JScrollItem">
            22222222
        </li>
        <li class="JScrollItem">
            33333333
        </li>
        <li class="JScrollItem">
            44444444
        </li>
    </ul>
</div>

<div style="height:10px"></div>

<div id="testHorizontal" class="JScrollFlowHorizontal">
    <ul class="JScrollGroup">
        <li class="JScrollItem">
            11111111
        </li>
        <li class="JScrollItem">
            22222222
        </li>
        <li class="JScrollItem">
            33333333
        </li>
        <li class="JScrollItem">
            44444444
        </li>
    </ul>
</div>


</body>
</html>