<%--<%@ page import="com.bcgogo.config.ConfigController" %>--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>historyListSample</title>

    <link rel="stylesheet" href="js/extension/jquery/plugin/jquery-ui/themes/redmond/jquery-ui-1.8.21.custom.css" />
    <link rel="stylesheet" href="js/extension/jquery/plugin/jquery-tooltip/jquery.tooltip.css" />
    <link rel="stylesheet" href="js/extension/jquery/plugin/dataTables/css/jquery.dataTables_themeroller.css" />
    <link rel="stylesheet" href="js/components/themes/bcgogo-droplist.css" />
    <link rel="stylesheet" href="js/extension/jquery/plugin/tipsy-master/src/stylesheets/tipsy.css"/>

    <script type="text/javascript" src="js/extension/json2/json2.min.js"></script>
    <script type="text/javascript" src="js/extension/jquery/jquery-1.4.2.min.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery-ui/ui/jquery-ui-1.8.21.custom.min.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery-tooltip/jquery.tooltip.min.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/tipsy-master/src/javascripts/jquery.tipsy.js"></script>
    <script type="text/javascript" src="js/base.js"></script>
    <script type="text/javascript" src="js/application.js"></script>
    <script type="text/javascript" src="js/components/ui/bcgogo-droplist.js"></script>

    <script type="text/javascript">
        $(document).ready(function () {
//            /** droplist 初始化 有如下参数要传
//             * {
//             *     // 下拉框挂钩的 Dom 对象， 一般为输入对象，比如 input[type='text']
//             *     "selector": $node
//             *
//             *     // 必传， 当选中了某行，触发此回调， 回调的传参形式按照 如下示例
//             *     // index 代表是第几行
//             *     // data 代表选中那行的数据
//             *     "onSelect":function(event, index, data){
//             *         //...
//             *     },
//             *
//             *     // 必传， 修改完毕后，点击确认按钮后，进行相关操作
//             *     // index 代表是第几行
//             *     // data 代表选中那行的 已经被修改后的数据
//             *     "onSave":function(event, index, data){
//             *         //...
//             *     },
//             *
//             *     // isEditable 用于指定是否出现修改文本的 UI
//             *     // [true|false], 默认值 false,
//             *     "isEditable:false,
//             *
//             *     // 数据格式
//             *     "data":{
//             *          uuid:uuid,
//             *          data:[
//             *              {label:"发动机"},
//             *              {label:"机油"},
//             *              {label:"啦啦啦"},
//             *              {label:"大富豪汽车"},
//             *              {label:"计算机程序设计艺术知几何？"}
//             *          ]
//             *      }
//             * }
//             */
////            droplist.show();
//
////            隐藏组件
////            droplist.hide();
////
////            需要UUID ，每次的请求和数据都是唯一的， 由于请求是异步的不知道是哪数据回来， 所以需要使用 UUID进行判断
////            droplist.setUUID();
////            获取UUID
////            droplist.getUUID();
////            获取 droplist 所跟随的 node 的 Dom 节点
////            droplist.getFollowNode();
////
////            检查对象是否是 可见状态
////            droplist.isVisible();
////            清除数据 以及 视图中的items
////            droplist.clear();
////            销毁对象
////            droplist.destory();
//
//
//            // TODO 下面的代码是实际的测试代码请见
//
//            // 创建数据集, 这里的数据集只是一个示例， 我们假设下面的数据集是通过一次 Ajax 请求返回的
//            // 在以下数据集中，有一个比较特别的 uuid ， 它是用来干什么的呢？ 我的回答是， 用来保证异步请求的唯一性
//            // 什么意思？ 是这样的， 设想两次请求分别在很短的时间内发送了， 而第1个请求消耗的服务器时间比较长
//            // 那么可能会出现这种情况， 在第二次请求发送之后， 第一次请求的结果才回来， 这个时候我们该如何处理呢？ 我们又如何区分多次的请求？
//            // uuid 大侠站了出来， uuid 顾名思义，"全局唯一不重复ID" ，在 base.js 中有已经写好的 js 生成 uuid 的方法 GLOBAL.Util.generateUUID();
////            var data = {
////                uuid:"1a9cb7b5-affc-4f90-a2ec-b870fdb64400"
////            };
//
//            // 我们如何使用这个 uuid ：
//            // （1）首先使用 js 的 GLOBAL.Util.generateUUID() 方法生成一个 uuid， 并将其设置给 droplist组件
            var droplist = APP_BCGOGO.Module.droplist;
            var uuid = GLOBAL.Util.generateUUID();
            droplist.setUUID(uuid);


            $("#text1,#text2,#text3,#text4")
                .bind("click focus", function (event) {
//                (2) 然后我们通过一次 Ajax 获得数据， 这里我们模拟这次Ajax 的过程， 其中 uuid 应当被当做参数传递
//                    为什么呢？ 什么实用 uuid 会起作用呢？ 且看代码
//                       APP_BCGOGO.Net.asyncGet({
//                           url:"xxxxxx"
//                           data:{
//                               "uuid":uuid,
//                               "xxx":xxx,
//                               "xxx":xxx
//                               //....
//                           },
//                           dataType:"json",
//                           success:function(json) {
//                               // 这里的json 的格式就应该是如下格式数据, 或者将服务器端传回的数据，包装成如下格式
//                               // json :
//                               // {
//                               //    //这里的 uuid 既是请求发送时传递的 uuid ，此时由服务端再传回来
//                               //    uuid:"xxxxxxxxxxxxxxxxxxxxx",
//                               //    data:[
//                               //        // type == "category" 表示这是一个标题, 现在的需求中，已经很少有需要 category 的需求了
//                               //        {label:"xxxxx", type:"category"},
//                               //        {label:"xxxxx"}，
//                               //        {label:"xxxxx"},
//                               //        {label:"xxxxx"},
//                               //        {label:"xxxxx"}
//                               //    ]
//                               // }
//
//                            }
//                        });
                    askForAssistDroplist(event);
                });

            function askForAssistDroplist(event) {
                var droplist = APP_BCGOGO.Module.droplist;
                clearTimeout(droplist.delayTimerId || 0);
                droplist.delayTimerId = setTimeout(function () {
                    var droplist = APP_BCGOGO.Module.droplist;
                    // 我们dummy 一个数据集， 这个数据集即符合 droplist 的需要。
                    var result = {
                        uuid:uuid,
                        data:[
                            {label:"发动机", isEditable:true},
                            {label:"发动机"},
                            {label:"机油", isEditable:false},
                            {label:"机油"},
                            {label:"啦啦啦", isEditable:false},
                            {label:"大富豪汽车", isEditable:true},
                            {label:"啦啦啦", isEditable:false},
                            {label:"大富豪汽车", isEditable:true},
                            {label:"啦啦啦", isEditable:false},
                            {label:"大富豪汽车", isEditable:true},
                            {label:"啦啦啦", isEditable:false},
                            {label:"大富豪汽车", isEditable:true},
                            {label:"啦啦啦", isEditable:false},
                            {label:"大富豪汽车", isEditable:true},
                            {label:"啦啦啦", isEditable:false},
                            {label:"大富豪汽车", isEditable:true},
                            {label:"啦啦啦", isEditable:false},
                            {label:"大富豪汽车", isEditable:true},
                            {label:"计算机程序设计艺术知几何？"}
                        ]
                    };
                    // 在实际代码中，在这个地方需要进行数据请求， 然后 下面的两句代码应该是在， 数据回来的回调中调用

                    droplist.show({
                        "selector":$(event.currentTarget),
                        "originalValue":{label:"xxx"},
                        "isEditable":true,
                        "isDeletable":false,
                        "data":result,
                        "saveWarning":"保存操作会影响全局数据",
                        "deleteWarning":"删除操作会影响全局数据",
                        "onSelect":function (event, index, data, hook) {
                            GLOBAL.debug(data);
                            var log = "选中的数据如下：\n"
                                + "index:  " + index + "\n" + "data:  " + JSON.stringify(data);
                            $("#systemNotice")
                                .html("<p>" + log + "</p>")
                                .dialog({"modal":true, "title":"提示", "stack":true});

                            GLOBAL.debug(log);
                        },

                        "onSave":function (event, index, data, hook) {
                            GLOBAL.debug(data);
                            var log = "修改后的数据如下：\n"
                                + "index:  " + index + "\n" + "data:  " + JSON.stringify(data);
//                            $("#systemNotice")
//                                .html("<p>" + log + "</p>")
//                                .dialog({"modal":true, "title":"提示", "stack":true});

                            GLOBAL.debug(log);
                            // TODO 保存数据到服务器端 AJAX 请求
                        },

                        "onDelete":function (event, index, data, hook) {
                            // TODO
                            ;
                        },

                        onKeyboardSelect:function(event, index, data, hook) {
                            // TODO;
                        }
                    });
                }, 200);
            }
        });
    </script>

</head>

<body>

<input id="text1" type="text" style="width:200px; display:block; margin-bottom: 20px;"/>
<input id="text2" type="text"/>
<input id="text3" type="text"/>
<input id="text4" type="text" style="width:130px;"/>

<div id="systemNotice"></div>

</body>
</html>
