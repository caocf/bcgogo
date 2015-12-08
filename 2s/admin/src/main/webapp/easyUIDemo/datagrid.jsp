<%@ page language="java" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Complex DataGrid - jQuery EasyUI Demo</title>
    <link rel="stylesheet" type="text/css" href="js/extension/jquery-easyui/jquery-easyui-1.4.1/themes/default/easyui.css" />
    <link rel="stylesheet" type="text/css" href="js/extension/jquery-easyui/jquery-easyui-1.4.1/themes/icon.css" />
    <script type="text/javascript" src="js/extension/jquery-easyui/jquery-easyui-1.4.1/jquery.min.js"></script>
    <script type="text/javascript" src="js/extension/jquery-easyui/jquery-easyui-1.4.1/jquery.easyui.min.js"></script>
    <%--<script type="text/javascript" src="js/extension/jquery-easyui/jquery-easyui-1.4.1/easyloader.js"></script>--%>
    <script>
        $(function(){
            $('#test').datagrid({
                title : 'datagrid实例',
                iconCls : 'icon-ok',
                width : 600,
                pageSize : 5,//默认选择的分页是每页5行数据
                pageList : [ 5, 10, 15, 20 ],//可以选择的分页集合
                nowrap : true,//设置为true，当数据长度超出列宽时将会自动截取
                striped : true,//设置为true将交替显示行背景。
                collapsible : true,//显示可折叠按钮
//                toolbar:"#tb",//在添加 增添、删除、修改操作的按钮要用到这个
                url:'easyUIDemo.do?method=users',//url调用Action方法
                loadMsg : '数据装载中......',
                singleSelect:true,//为true时只能选择单行
                fitColumns:true,//允许表格自动缩放，以适应父容器
                //sortName : 'xh',//当数据表格初始化时以哪一列来排序
                //sortOrder : 'desc',//定义排序顺序，可以是'asc'或者'desc'（正序或者倒序）。
                remoteSort : false,
                frozenColumns : [ [ {
                    field : 'ck',
                    checkbox : true
                } ] ],
                pagination : true,//分页
                rownumbers : true//行数
            });
        });
        function resize(){
            $('#test').datagrid('resize', {
                width:700,
                height:400
            });
        }
        function getSelected(){
            var selected = $('#test').datagrid('getSelected');
            if (selected){
                alert(selected.code+":"+selected.name+":"+selected.addr+":"+selected.col4);
            }
        }
        function getSelections(){
            var ids = [];
            var rows = $('#test').datagrid('getSelections');
            for(var i=0;i<rows.length;i++){
                ids.push(rows[i].code);
            }
            alert(ids.join(':'));
        }
        function clearSelections(){
            $('#test').datagrid('clearSelections');
        }
        function selectRow(){
            $('#test').datagrid('selectRow',2);
        }
        function selectRecord(){
            $('#test').datagrid('selectRecord','002');
        }
        function unselectRow(){
            $('#test').datagrid('unselectRow',2);
        }
        function mergeCells(){
            $('#test').datagrid('mergeCells',{
                index:2,
                field:'addr',
                rowspan:2,
                colspan:2
            });
        }
    </script>
</head>
<body>
<h2>Complex DataGrid</h2>
<div class="demo-info">
    <div class="demo-tip icon-tip"></div>
    <div>Click the button to do actions with datagrid.</div>
</div>

<div style="margin:10px 0;">
    <a href="#" onclick="getSelected()">GetSelected</a>
    <a href="#" onclick="getSelections()">GetSelections</a>
    <a href="#" onclick="selectRow()">SelectRow</a>
    <a href="#" onclick="selectRecord()">SelectRecord</a>
    <a href="#" onclick="unselectRow()">UnselectRow</a>
    <a href="#" onclick="clearSelections()">ClearSelections</a>
    <a href="#" onclick="resize()">Resize</a>
    <a href="#" onclick="mergeCells()">MergeCells</a>
</div>

<table id="test">
    <thead>
    <tr>
        <th data-options="field:'code',width:100,align:'center'">学生学号</th>
        <th data-options="field:'name',width:100,align:'center'">姓名</th>
        <th data-options="field:'addr',width:100,align:'center'">性别</th>
        <th data-options="field:'col4',width:100,align:'center'">年龄</th>
    </tr>
    </thead>
</table>

</body>
</html>