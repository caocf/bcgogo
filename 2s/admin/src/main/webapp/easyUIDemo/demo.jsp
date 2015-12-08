<%@ page language="java" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<html>
<head>
    <title></title>
    <link rel="stylesheet" type="text/css" href="js/extension/jquery-easyui/jquery-easyui-1.4.1/themes/default/easyui.css" />
    <link rel="stylesheet" type="text/css" href="js/extension/jquery-easyui/jquery-easyui-1.4.1/themes/icon.css" />
    <style type="text/css">
        #fm{
            margin:0;
            padding:10px 30px;
        }
        .ftitle{
            font-size:14px;
            font-weight:bold;
            color:#666;
            padding:5px 0;
            margin-bottom:10px;
            border-bottom:1px solid #ccc;
        }
        .fitem{
            margin-bottom:5px;
        }
        .fitem label{
            display:inline-block;
            width:80px;
        }
    </style>
    <script type="text/javascript" src="js/extension/jquery-easyui/jquery-easyui-1.4.1/jquery.min.js"></script>
    <script type="text/javascript" src="js/extension/jquery-easyui/jquery-easyui-1.4.1/jquery.easyui.min.js"></script>
    <script type="text/javascript" src="js/extension/jquery-easyui/jquery-easyui-1.4.1/easyloader.js"></script>
    <script>
        function newUser(){
            $('#dlg').dialog('open').dialog('setTitle','New User');
            $('#fm').form('clear');
            url = 'save_user.php';
        }

        function editUser(){
        var row = $('#dg').datagrid('getSelected');
        if (row){
            $('#dlg').dialog('open').dialog('setTitle','Edit User');
            $('#fm').form('load',row);
            url = 'update_user.php?id='+row.id;
        }
        }

        function saveUser(){
            $('#fm').form('submit',{
                url: url,
                onSubmit: function(){
                    return $(this).form('validate');
                },
                success: function(result){
                    var result = eval('('+result+')');
                    if (result.errorMsg){
                        $.messager.show({
                            title: 'Error',
                            msg: result.errorMsg
                        });
                    } else {
                        $('#dlg').dialog('close');		// close the dialog
                        $('#dg').datagrid('reload');	// reload the user data
                    }
                }
            });
        }

    </script>
</head>
<body>
<%--按钮样式--%>
<div style="padding:5px;background:#fafafa;width:700px;border:1px solid #ccc">
    <span>按钮样式1:</span>
    <a href="#" class="easyui-linkbutton" iconCls="icon-add">add</a>
    <a href="#" class="easyui-linkbutton" iconCls="icon-back">back</a>
    <a href="#" class="easyui-linkbutton" iconCls="icon-blank">blank</a>
    <a href="#" class="easyui-linkbutton" iconCls="icon-cancel" >cancel</a>
    <a href="#" class="easyui-linkbutton" iconCls="icon-clear" >clear</a>
    <a href="#" class="easyui-linkbutton" iconCls="icon-cut" >cut</a>
    <a href="#" class="easyui-linkbutton" iconCls="icon-remove">remove</a>
    <a href="#" class="easyui-linkbutton" iconCls="icon-save">save</a>
    <a href="#" class="easyui-linkbutton" iconCls="icon-filter" >filter</a>
    <a href="#" class="easyui-linkbutton" iconCls="icon-help" >help</a>
    <a href="#" class="easyui-linkbutton" iconCls="icon-lock" >lock</a>
    <a href="#" class="easyui-linkbutton" iconCls="icon-man" >man</a>
    <a href="#" class="easyui-linkbutton" iconCls="icon-mini-add" >mini-add</a>
    <a href="#" class="easyui-linkbutton" iconCls="icon-mini-edit" >mini-edit</a>
    <a href="#" class="easyui-linkbutton" iconCls="icon-mini-refresh" >mini-refresh</a>
    <a href="#" class="easyui-linkbutton" iconCls="icon-no" >no</a>
    <a href="#" class="easyui-linkbutton" iconCls="icon-ok" >ok</a>
    <a href="#" class="easyui-linkbutton" iconCls="icon-pencil" >pencil</a>
    <a href="#" class="easyui-linkbutton" iconCls="icon-print" >print</a>
    <a href="#" class="easyui-linkbutton" iconCls="icon-redo" >redo</a>
    <a href="#" class="easyui-linkbutton" iconCls="icon-reload" >reload</a>
    <a href="#" class="easyui-linkbutton" iconCls="icon-search" >search</a>
    <a href="#" class="easyui-linkbutton" iconCls="icon-sum" >sum</a>
    <a href="#" class="easyui-linkbutton" iconCls="icon-tip" >tip</a>
    <a href="#" class="easyui-linkbutton" iconCls="icon-undo" >undo</a>

</div>
<br>
<%--按钮链接样式--%>
<div style="padding:5px;background:#fafafa;width:700px;border:1px solid #ccc">
    <span>按钮样式2增加plain="true"属性:</span>
    <a href="#" class="easyui-linkbutton" iconCls="icon-add" plain="true">增加</a>
    <a href="#" class="easyui-linkbutton" iconCls="icon-save" plain="true">保存</a>
    <a href="#" class="easyui-linkbutton" iconCls="icon-reload" plain="true">刷新</a>
    <a href="#" class="easyui-linkbutton" iconCls="icon-edit" plain="true">编辑</a>
    <a href="#" class="easyui-linkbutton" iconCls="icon-remove" plain="true">删除</a>
</div>
<br>

<table id="dg" title="data grid 配合toolbar 加载数据" class="easyui-datagrid" style="width:700px;height:250px"
       url="easyUIDemo.do?method=users"
       toolbar="#toolbar"
       rownumbers="true" fitColumns="true" singleSelect="true">
    <thead>
    <tr>
        <th field="firstname" width="50">First Name</th>
        <th field="lastname" width="50">Last Name</th>
        <th field="phone" width="50">Phone</th>
        <th field="email" width="50">Email</th>
    </tr>
    </thead>
</table>
<div id="toolbar">
    <a href="#" class="easyui-linkbutton" iconCls="icon-add" plain="true" onclick="newUser()">New User</a>
    <a href="#" class="easyui-linkbutton" iconCls="icon-edit" plain="true" onclick="editUser()">Edit User</a>
    <a href="#" class="easyui-linkbutton" iconCls="icon-remove" plain="true" onclick="destroyUser()">Remove User</a>
</div>

<div id="dlg" class="easyui-dialog" style="width:400px;height:280px;padding:10px 20px"
     closed="true" buttons="#dlg-buttons">
    <div class="ftitle">User Information</div>
    <form id="fm" method="post">
        <div class="fitem">
            <label>First Name:</label>
            <input name="firstname" class="easyui-validatebox" required="true">
        </div>
        <div class="fitem">
            <label>Last Name:</label>
            <input name="lastname" class="easyui-validatebox" required="true">
        </div>
        <div class="fitem">
            <label>Phone:</label>
            <input name="phone">
        </div>
        <div class="fitem">
            <label>Email:</label>
            <input name="email" class="easyui-validatebox" validType="email">
        </div>
    </form>
</div>
<div id="dlg-buttons">
    <a href="#" class="easyui-linkbutton" iconCls="icon-ok" onclick="saveUser()">Save</a>
    <a href="#" class="easyui-linkbutton" iconCls="icon-cancel" onclick="javascript:$('#dlg').dialog('close')">Cancel</a>
</div>

</body>
</html>
