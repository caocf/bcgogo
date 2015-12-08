<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>

<head>
    <meta content="text/html;charset=utf-8" http-equiv="content-type">

    <title>Map Mark</title>

    <script type="text/javascript" src="js/extension/json2/json2.min.js"></script>
    <script type="text/javascript" src="js/extension/jquery/jquery-1.4.2.min.js"></script>
    <script type="text/javascript" src="js/base.js"></script>
    <script type="text/javascript" src="js/application.js"></script>
    <script type="text/javascript" src="js/components/ui/bcgogo-mapMark.js"></script>

    <script type="text/javascript">
        $(document).ready(function() {
            var mapMark = new App.Module.MapMark();

            // 这是最简单的调用样例， 如果需要查看详细的调用文档， 请看 bcgogo-mapMark.js 这个文件的注释，里面有详细说明
            // 除了 initDefault() , 还有更自由的函数 init 可以选用，具体的用法也请看 bcgogo-mapMark.js 的注释
            mapMark.initDefault({
                "width":350,
                "selector":"#mapTest",
                "onSelect":function(event, itemData)  {
                    G.error(event);
                    G.error(itemData);

                    // TODO do something start here

                    // For example, 跳转页面

//                    if(itemData.label === "江苏") {
//                        window.open("http://www.baidu.com/s?wd=" + encodeURIComponent(itemData.label) + "&rsv_spt=1&issp=1&rsv_bp=0&ie=utf-8&tn=baiduhome_pg");
//                    }

                    window.open("http://www.baidu.com/s?wd=" + encodeURIComponent(itemData.label) + "&rsv_spt=1&issp=1&rsv_bp=0&ie=utf-8&tn=baiduhome_pg");
                }
            });
        });
    </script>
</head>

<body>

<div id="mapTest"></div>

</body>
</html>