<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>商品分类 template</title>

    <link rel="stylesheet" href="style/salesSlip.css"/>
    <link rel="stylesheet" href="js/components/themes/bcgogo-categorySelector.css"/>

    <script type="text/javascript" src="js/extension/jquery/jquery-1.4.2.min.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery-ui/ui/jquery-ui-1.8.21.custom.min.js"></script>
    <script type="text/javascript" src="js/base.js"></script>
    <script type="text/javascript" src="js/application.js"></script>
    <script type="text/javascript" src="js/components/ui/bcgogo-categorySelector.js"></script>

    <script type="text/javascript">
        var dummyData = [{"level_1_name":"飞机","level_2_name":"","level_3_name":"","level_1_id":"0000","level_2_id":"","level_3_id":""},{"level_1_name":"飞碟","level_2_name":"","level_3_name":"","level_1_id":"0005","level_2_id":"","level_3_id":""},{"level_1_name":"飞船","level_2_name":"","level_3_name":"","level_1_id":"0006","level_2_id":"","level_3_id":""},{"level_1_name":"飞艇","level_2_name":"","level_3_name":"","level_1_id":"0007","level_2_id":"","level_3_id":""},{"level_1_name":"飞鸟","level_2_name":"","level_3_name":"","level_1_id":"0008","level_2_id":"","level_3_id":""},{"level_1_name":"鸟","level_2_name":"","level_3_name":"","level_1_id":"0009","level_2_id":"","level_3_id":""},{"level_1_name":"鸟王","level_2_name":"","level_3_name":"","level_1_id":"0010","level_2_id":"","level_3_id":""},{"level_1_name":"啦啦啦","level_2_name":"","level_3_name":"","level_1_id":"0011","level_2_id":"","level_3_id":""},{"level_1_name":"神迹","level_2_name":"","level_3_name":"","level_1_id":"0012","level_2_id":"","level_3_id":""},{"level_1_name":"奇迹","level_2_name":"","level_3_name":"","level_1_id":"0013","level_2_id":"","level_3_id":""},{"level_1_name":"圣人","level_2_name":"","level_3_name":"","level_1_id":"0014","level_2_id":"","level_3_id":""},{"level_1_name":"老学究","level_2_name":"","level_3_name":"","level_1_id":"0015","level_2_id":"","level_3_id":""},{"level_1_name":"文盲","level_2_name":"","level_3_name":"","level_1_id":"0016","level_2_id":"","level_3_id":""},{"level_1_name":"暴徒","level_2_name":"","level_3_name":"","level_1_id":"0017","level_2_id":"","level_3_id":""},{"level_1_name":"动物","level_2_name":"","level_3_name":"","level_1_id":"0018","level_2_id":"","level_3_id":""},{"level_1_name":"宇航器","level_2_name":"旋转分离器","level_3_name":"","level_1_id":"0004","level_2_id":"jkfdaskdjf","level_3_id":""},{"level_1_name":"汽车","level_2_name":"","level_3_name":"","level_1_id":"0001","level_2_id":"","level_3_id":""},{"level_1_name":"汽车","level_2_name":"配件","level_3_name":"","level_1_id":"0001","level_2_id":"aaaaaaaaa","level_3_id":""},{"level_1_name":"汽车","level_2_name":"配件","level_3_name":"火花塞","level_1_id":"0001","level_2_id":"aaaaaaaaa","level_3_id":"ususus-00dsf0df0d0d"},{"level_1_name":"坦克","level_2_name":"","level_3_name":"","level_1_id":"0002","level_2_id":"","level_3_id":""},{"level_1_name":"坦克","level_2_name":"动力系统","level_3_name":"","level_1_id":"0002","level_2_id":"bbbbbbbbbbbbb","level_3_id":""},{"level_1_name":"坦克","level_2_name":"动力系统","level_3_name":"水套","level_1_id":"0002","level_2_id":"bbbbbbbbbbbbb","level_3_id":"uiuiuiui-asdf9898df7a8sdf"},{"level_1_name":"坦克","level_2_name":"悬挂系统","level_3_name":"","level_1_id":"0002","level_2_id":"bbbbbbbbbbbbb02","level_3_id":""},{"level_1_name":"坦克","level_2_name":"悬挂系统","level_3_name":"扭力连杆","level_1_id":"0002","level_2_id":"bbbbbbbbbbbbb02","level_3_id":"jijijijij-dasf88f9d0a0sdf9"},{"level_1_name":"坦克","level_2_name":"悬挂系统","level_3_name":"半扭力连杆","level_1_id":"0002","level_2_id":"bbbbbbbbbbbbb02","level_3_id":"yuyuyuyu-88dufuufjasd89"}];

        $(document).ready(function() {
            var categorySelector = new App.Module.CategorySelector();
            window.categorySelector = categorySelector;

            categorySelector.init({
                "selector":"#testId",
                "data":dummyData,

                // onInput 功能还没完整完成
//                "onInput":function(event, index, value, viewData) {
//                    for (var i = 0; i < arguments.length; i++) {
//                        var obj = arguments[i];
//                        console.log(obj);
//                    }
//
//                    if(categorySelector.xhr) {
//                        categorySelector.xhr.abort();
//                    }
//
//                    if(index === 0) {
//                        categorySelector.xhr = App.Net.async({
//                            url:"../dummyData/",
//                            success:function(data) {
//                                categorySelector.resetCategory(index , data);
//                            }
//                        });
//                    } else if(index === 1) {
//                        categorySelector.xhr = App.Net.async({
//                            url:"../dummyData/"
//                        });
//                    } else if(index === 2) {
//                        categorySelector.xhr = App.Net.async({
//                            url:"../dummyData/"
//                        });
//                    }
//                },
                onSelect:function(event, data, index) {
                    console.log(event);
                    console.log(data);
                    console.log(index);
                }
            });


//            categorySelector.reset();
            categorySelector.reset({"level_1_id":"0001"},1);
//            categorySelector.reset({"level_2_id":"aaaaaaaaa"},2);
//            categorySelector.reset({"level_3_id":"ususus-00dsf0df0d0d"},3);




        });
    </script>

</head>

<body>

<div id="testId"></div>

</body>
</html>
