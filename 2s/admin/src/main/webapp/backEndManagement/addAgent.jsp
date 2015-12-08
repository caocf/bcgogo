<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
  <title>增加代理商</title>
    <%-- styles --%>
  <link rel="stylesheet" type="text/css" href="styles/style.css"/>
  <link rel="stylesheet" type="text/css" href="styles/agent.css"/>
    <%@include file="/WEB-INF/views/style-thirdpartLibs.jsp"%>

    <%-- scripts --%>
    <%@include file="/WEB-INF/views/script-thirdpartLibs.jsp"%>
    <%@include file="/WEB-INF/views/script-common.jsp"%>
  <script type="text/javascript">
    $(document).ready(function() {
      var yearData=$("#dateYear").html().split("年");
      $("#year").val(yearData[0]);
      $(".monthTarget").blur(function() {
        var totalValue = 0;
        var monthValue = document.getElementsByName("monthTarget");
        for (var i = 0; i < monthValue.length; i++) {
          totalValue = totalValue*1 + monthValue[i].value*1;
        }
        $("#yearTarget").val(totalValue);
      });
      $(".rightIcon").click(function(){
          $("#year").val( $("#year").val()*1+1);
          $("#dateYear").html(($("#year").val()*1)+"年");
          initDate();
      });

      $(".leftIcon").click(function(){
        $("#year").val($("#year").val()-1);
          $("#dateYear").html(($("#year").val())+"年");
         initDate();
      });

      function initDate(){
        var monthValue=document.getElementsByName("monthTarget");
        for(var i=0;i<monthValue.length;i++){
           document.getElementsByName("monthTarget")[i].value="";
        }
        $("#yearTarget").val("");
      }

      $("#agentCode").blur(function(){
        var agentCode=$("#agentCode").val();
        if($.trim(agentCode)==""){
          alert("代理商编号不能为空!");
          return;
        }
        $.ajax({
            type:"POST",
            url:"agents.do?method=checkAgent",
            data:{agentCode:agentCode},
            cache:false,
            success:function(data){
                if(data=="yes"){
                  alert("代理商编号已存在！！");
                }
            }
        });
      });
    });
    function addIt() {
        $("#myform")[0].submit();
      }
  </script>
</head>
<body class="bodyMain">
<div class="i_main">
  <!--代理商基本信息-->
  <!--代理商-->
  <div class="rightTime">
    <div class="timeLeft"></div>
    <div class="timeBody">代理商基本信息</div>
    <div class="timeRight"></div>
  </div>
  <!--代理商结束-->
  <!--table-->
  <div class="clear"></div>
  <form id="myform" action="agents.do?method=saveAgent" method="POST">
    <table cellpadding="0" cellspacing="0" class="agent_tb">
      <col width="77">
      <col width="290">
      <col width="97">
      <col width="137">
      <col width="97">
      <col width="146">
      <col width="100">
      <tr>
        <td>代理商名</td>
        <td class="agent_color"><input type="text" name="name"/></td>
        <td>负责人</td>
        <td class="agent_color"><input type="text" name="personInCharge"/></td>
        <td>代理商编码</td>
        <td class="agent_color"><input type="text" name="agentCode" id="agentCode"/></td>
        <td>状态</td>
      </tr>
      <tr>
        <td>地址</td>
        <td class="agent_color"><input type="text" name="address"/></td>
        <td>联系方法</td>
        <td class="agent_color"><input type="text" name="mobile"/></td>
        <td>负责区域</td>
        <td class="agent_color"><input type="text" name="respArea"/></td>
        <td>
          <select name="state">
            <option value="1">有效</option>
            <option value="2">停止</option>
          </select>
        </td>
      </tr>
    </table>
    <!--table结束-->
    <!--代理商基本信息结束-->
    <!--目标完成情况-->
    <!--代理商-->
    <div class="height"></div>
    <div class="rightTime">
      <div class="timeLeft"></div>
      <div class="timeBody">计划目标
        <div style="float:right;margin-right:10px;">
          <div class="leftIcon">
            <input type="button" onfocus="this.blur();">
          </div>
          <span id="dateYear">2012年</span>
          <input type="hidden" value="2012" name="year" id="year"/>

          <div class="rightIcon">
            <input type="button" onfocus="this.blur();">
          </div>
        </div>
      </div>
      <div class="timeRight"></div>
    </div>
    <!--代理商结束-->
    <!--table-->
    <table cellpadding="0" cellspacing="0" id="gogal_tb" class="clear">
      <col width="98">
      <col width="65">
      <col width="65">
      <col width="65">
      <col width="65">
      <col width="65">
      <col width="65">
      <col width="65">
      <col width="65">
      <col width="65">
      <col width="75">
      <col width="75">
      <col width="75">
      <col width="95">
      <thead>
      <tr>
        <th>季度</th>
        <th colspan="3">第一季度</th>
        <th colspan="3">第二季度</th>
        <th colspan="3">第三季度</th>
        <th colspan="3">第四季度</th>
        <th></th>
      </tr>
      <tr>
        <th>月份</th>
        <th>1月</th>
        <th>2月</th>
        <th>3月</th>
        <th>4月</th>
        <th>5月</th>
        <th>6月</th>
        <th>7月</th>
        <th>8月</th>
        <th>9月</th>
        <th>10月</th>
        <th>11月</th>
        <th>12月</th>
        <th>累计</th>
      </tr>
      </thead>
      <tbody>
      <tr>
        <td>目标</td>
        <td><input type="text" name="monthTarget" class="monthTarget" value="0"/></td>
        <td><input type="text" name="monthTarget" class="monthTarget" value="0"/></td>
        <td><input type="text" name="monthTarget" class="monthTarget" value="0"/></td>
        <td><input type="text" name="monthTarget" class="monthTarget" value="0"/></td>
        <td><input type="text" name="monthTarget" class="monthTarget" value="0"/></td>
        <td><input type="text" name="monthTarget" class="monthTarget" value="0"/></td>
        <td><input type="text" name="monthTarget" class="monthTarget" value="0"/></td>
        <td><input type="text" name="monthTarget" class="monthTarget" value="0"/></td>
        <td><input type="text" name="monthTarget" class="monthTarget" value="0"/></td>
        <td><input type="text" name="monthTarget" class="monthTarget" value="0"/></td>
        <td><input type="text" name="monthTarget" class="monthTarget" value="0"/></td>
        <td><input type="text" name="monthTarget" class="monthTarget" value="0"/></td>
        <td><input type="text" name="yearTarget" id="yearTarget" value="0"/></td>
      </tr>
      </tbody>
    </table>
  </form>
  <!--table结束-->
  <div class="i_height"></div>
  <div class="registerButton">
    <input type="button" value="确认新增" onfocus="this.blur();"  id="addButton" onclick="addIt()"/>
    <input type="button" value="取消" onfocus="this.blur();"/>
  </div>
  <!--目标完成情况结束-->
</div>
</div>
</div>
</body>
</html>