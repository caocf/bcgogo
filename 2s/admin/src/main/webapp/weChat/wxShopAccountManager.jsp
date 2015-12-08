<%--
  Created by IntelliJ IDEA.
  User: ndong
  Date: 2014-12-19
  Time: 15:45
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <title>微信店铺账户管理</title>
  <link rel="stylesheet" type="text/css" href="styles/dataMaintenance.css"/>
  <link rel="stylesheet" type="text/css" href="styles/backstage.css"/>
  <link rel="stylesheet" type="text/css" href="styles/backAgent.css"/>
  <link rel="stylesheet" type="text/css" href="styles/storesMange.css"/>
  <link rel="stylesheet" type="text/css" href="styles/style.css"/>
  <link rel="stylesheet" type="text/css" href="js/extension/jquery-easyui/jquery-easyui-1.4.1/themes/bootstrap/easyui.css" />
  <link rel="stylesheet" type="text/css" href="js/extension/jquery-easyui/jquery-easyui-1.4.1/themes/icon.css" />

  <script type="text/javascript" src="js/extension/jquery-easyui/jquery-easyui-1.4.1/jquery.min.js"></script>
  <script type="text/javascript" src="js/extension/jquery-easyui/jquery-easyui-1.4.1/jquery.easyui.min.js"></script>
  <script type="text/javascript" src="js/extension/jquery-easyui/jquery-easyui-1.4.1/locale/easyui-lang-zh_CN.js"></script>
  <%--<script type="text/javascript" src="js/extension/jquery-easyui/jquery-easyui-1.4.1/easyloader.js"></script>--%>

  <%@include file="/WEB-INF/views/script-common.jsp"%>
  <script type="text/javascript" src="js/bcgogo.js"></script>
  <script type="text/javascript" src="js/fourSVehicle/fourSVehicleManager.js"></script>
  <script type="text/javascript" src="js/mask.js"></script>
  <script type="text/javascript">

    $(function(){

      $("#shopSelect").combobox({
        url:'print.do?method=getShopNameByName',
        method:'post',
        valueField:'id',
        textField:'name',
        mode:'remote',
        queryParam:'name',
        onSelect:function(record){
          $("#editShopId").val(record.id);
        }
      });

      $("#wxShopAccountTable").datagrid({
        rownumbers:true,
        singleSelect:true,
        fitColumns:true,//适应列宽
        striped:true,//条纹
        nowrap:false,//截取当数据长度超出列宽时将会自动截取
        loadMsg:"数据加载中",//载入时信息
        url:'weChat.do?method=getWXShopAccount',
        method:'get',
        pagination:true,
        pageNumber:1,
        pageSize:15,
        pageList:[15,20],
        toolbar: '#tb',
        columns:[[
          {field:'shopId',hidden:true},
          {field:'shopName',title:'店铺',width:80},
          {field:'accountName',title:'微信',width:80},
          {field:'balance',title:'余额',width:80,editor:{type:'numberbox',options:{precision:1}}},
          {field:'expireDateStr',title:'过期时间',width:80},
        ]]
      });



      $("#searchBtn").click(function () {
        var $wxShopAccountTable = $('#wxShopAccountTable');
        var queryParams = $wxShopAccountTable.datagrid('options').queryParams;
        var shopIds = $("#shopSelect").combobox("getValues");
        queryParams.shopId = shopIds[0];
        $wxShopAccountTable.datagrid('options').pageNumber = 1;
        var p = $wxShopAccountTable.datagrid('getPager');
        if(p){
          p.pagination({
            pageNumber:1
          });
        }
        //重新加载datagrid的数据
        $("#wxShopAccountTable").datagrid('reload');
      });

      $('#expireDate').datebox({
//        formatter: function(date){
//          var dateStr=date.getFullYear()+"-"+(date.getMonth()+1)+"-"+date.getDate();
//          return dateStr;
//        }
      });



      $("#editBtn").click(function(){

        var row=$("#wxShopAccountTable").datagrid("getSelected");
        if(G.isEmpty(row)) {
          alert("请选择编辑的数据")
          return;
        }
        var id=row.idStr;
        var expireDate=row.expireDateStr;
        $("#editForm [name='id']").val(id);
        $("#expireDate").datebox("setValue",expireDate);

        $('#edit_win').dialog({
          width:350,
          height:200,
          modal:true,
          title:"微信账户编辑",
          open:function(){
            $("#editForm [name='balance']").val(0);
          },
          buttons:[{
            text:'保存',
            handler:function(){
              $('#editForm').form('submit', {
                url:"weChat.do?method=saveOrUpdateWXShopAccount",
                success: function(result){
                  var data = eval('(' + result + ')');
                  if (!data.success){
                    alert(data.msg);
                    return;
                  }
                  $('#edit_win').dialog('close');
                  $("#wxShopAccountTable").datagrid('reload');
                }
              });

            }
          },{
            text:'关闭',
            handler:function(){
              $('#edit_win').dialog('close');  // close a window
            }
          }]
        });
      });


    });



  </script>
</head>
<body>


<div id="tb" style="padding:2px 5px;">
  <div style="margin-bottom:5px">
    <a id="editBtn" href="#" class="easyui-linkbutton" iconCls="icon-edit" plain="true" >账户充值</a>
  </div>
  <div>
    店铺：<input autocomplete="off" id="shopSelect" style="width: 200px">
    <a href="#" id="searchBtn" class="easyui-linkbutton" iconCls="icon-search">查询</a>
  </div>
</div>

<div id="edit_win">
  <form id="editForm" method="post" style="padding:22px 0 0 52px">
    <input type="hidden" name="id"/>
    <div>
      <label>充值金额</label>
      <input class="easyui-validatebox" type="text" name="money" style="width:110px"/>
    </div>
    <div style="padding-top: 10px">
      <label>过期时间</label>
      <input autocomplete="off"  id="expireDate" name="expireDateStr" style="width:110px">
    </div>
  </form>
</div>

<div class="main">
  <%@include file="/WEB-INF/views/header.jsp" %>
  <div class="body">
    <%@include file="/WEB-INF/views/left.jsp" %>
    <div class="bodyRight">

      <div class="rightMain clear">
        <%@include file="/weChat/wxNav.jsp"%>
        <div class="fileInfo">
          <table id="wxShopAccountTable"  title="店铺微信账户管理" style="width: 100%;height: auto">
          </table>
        </div>
      </div>
      <div class="bottom_crile clear">
        <div class="crile"></div>
        <div class="bottom_x"></div>
        <div style="clear:both;"></div>
      </div>
    </div>
  </div>
</div>
<ul class="suggestionMain" style="list-style: none;color:#000000;">
</ul>
<div id="mask" style="display:block;position: absolute;">
</div>

</body>
</html>
