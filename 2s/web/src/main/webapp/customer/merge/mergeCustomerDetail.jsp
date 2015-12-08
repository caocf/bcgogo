<%@ page import="com.bcgogo.config.ConfigController" %>
<%--
  Created by IntelliJ IDEA.
  User: ndong
  Date: 13-1-18
  Time: 上午7:26
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
  <title>合并客户详细信息</title>
  <link rel="stylesheet" type="text/css" href="style/style.css"/>
  <link rel="stylesheet" type="text/css" href="styles/mergeRecord<%=ConfigController.getBuildVersion()%>.css"/>



  <script type="text/javascript">

  </script>
</head>
<body class="bodyMain">
<div class="i_upBody">
<div class="mergeBefore" style="color:#FF0000;">友情提示：客户合并后，将只保留选中客户的信息，其他未选中客户的消费信息、欠款信息、会员服务,OBD信息和车辆信息将转移到选中客户上。</div>
<h3 class="blue_color mergeBefore">请您选择需要保留的客户：</h3>
<h3 class="blue_color mergeAfter">进行合并的客户信息如下：</h3>
<div class="i_height"></div>
<div class="unRelateMergedCustomer">
<div id="parentBorder" class="border">
  <table cellpadding="0" cellspacing="0" class="tabMerge tab1 tabMerge_selected" id="parentTable" style="border-bottom:none">
    <col width="62">
    <col width="120">
    <col width="70">
    <col width="100">
    <col width="90">
    <col width="100">
    <col width="90">
    <col width="80">
    <col width="110">
    <col width="60">
    <col width="90">
    <tr>
      <td class="tab_title">客户名</td>
      <td class="name"></td>
      <td class="tab_title">客户类别</td>
      <td class="customerKindStr"></td>
      <td class="tab_title">会员级别</td>
      <td class="type"></td>
      <td class="tab_title">状态</td>
      <td class="memberStatus"></td>
      <td class="tab_title">项目</td>
      <td class="tab_title">剩余次数</td>
      <td class="tab_title">失效日期</td>
    </tr>
    <tr>
      <td class="tab_title">联系人</td>
      <td class="contact"></td>
      <td class="tab_title">区域</td>
      <td class="areaStr"></td>
      <td class="tab_title">卡号</td>
      <td class="memberNo"></td>
      <td class="tab_title">会员会龄</td>
      <td class="dateKeep"></td>
      <td class="serviceName0"></td>
      <td class="serviceTime0"></td>
      <td class="deadLine0"></td>
    </tr>
    <tr>
      <td class="tab_title"><div class="radCheck"  isParent="true"  id="selectedCustomer1"></div>手机</td>
      <td class="mobile"></td>
      <td class="tab_title">开户行</td>
      <td class="bank"></td>
      <td class="tab_title">入会日期</td>
      <td class="joinDateStr"></td>
      <td class="tab_title">到期</td>
      <td class="serviceDeadLineStr"></td>
      <td class="serviceName1"></td>
      <td class="serviceTime1"></td>
      <td class="deadLine1"></td>
    </tr>
    <tr>
      <td class="tab_title">座机</td>
      <td class="landLine"></td>
      <td class="tab_title">开户名</td>
      <td class="bankAccountName"></td>
      <td class="tab_title">储值余额</td>
      <td class="balance"></td>
      <td class="tab_title">会员卡累计消费</td>
      <td class="memberConsumeTotal"></td>
      <td class="serviceName2"></td>
      <td class="serviceTime2"></td>
      <td class="deadLine2"></td>
    </tr>
    <tr>
      <td class="tab_title">地址</td>
      <td class="address"></td>
      <td class="tab_title">账户</td>
      <td class="bankAccountName"></td>
      <td class="tab_title">累计消费</td>
      <td class="totalAmount"></td>
      <td class="tab_title">累计销售退货</td>
      <td class="totalReturnAmount"></td>
      <td class="serviceName3"></td>
      <td class="serviceTime3"></td>
      <td class="deadLine3"></td>
    </tr>
    <tr>
      <td class="tab_title">结算方式</td>
      <td class="settlementTypeStr"></td>
      <td class="tab_title">退货次数</td>
      <td class="countCustomerReturn"></td>
      <td class="tab_title">当前应付</td>
      <td class="totalPayable"></td>
      <td class="tab_title">当前应收</td>
      <td class="totalReceivable"></td>
      <td class="serviceName4"></td>
      <td class="serviceTime4"></td>
      <td class="deadLine4"></td>
    </tr>
  </table>
  <table cellpadding="0" cellspacing="0" class="tabMerge tab1 tabMerge_selected" id="parentVehicleTable" style="border-top:none;">
    <col width="30">
    <col width="80">
    <col width="80">
    <col width="70">
    <col width="60">
    <col width="60">
    <col width="110">
    <col width="80">
    <col width="110">
    <col width="100">
    <col width="80">
    <col width="380">
    <tr class="tab_title">
      <td colspan="12">拥有车辆数：<span id="parentVehicleNum">0</span>辆</td>
    </tr>
    <tr class="tab_title">
      <td>NO</td>
      <td>车牌</td>
      <td>车辆品牌</td>
      <td>车型</td>
      <td>年代</td>
      <td>排量</td>
      <td>车架号</td>
      <td>发动机号</td>
      <td>车身颜色</td>
      <td>购车日期</td>
      <td>进厂里程</td>
      <td>预约服务</td>
    </tr>

  </table>
</div>
<div class="height"></div>
<div id="childBorder" class="border">
  <table cellpadding="0" cellspacing="0" class="tabMerge tab2" id="childTable">
    <col width="62">
    <col width="120">
    <col width="70">
    <col width="100">
    <col width="90">
    <col width="100">
    <col width="90">
    <col width="80">
    <col width="120">
    <col width="60">
    <col width="80">
    <tr>
      <td class="tab_title">客户名</td>
      <td class="name"></td>
      <td class="tab_title">客户类别</td>
      <td class="customerKindStr"></td>
      <td class="tab_title">会员级别</td>
      <td class="type"></td>
      <td class="tab_title">状态</td>
      <td class="memberStatus"></td>
      <td class="tab_title">项目</td>
      <td class="tab_title">剩余次数</td>
      <td class="tab_title">失效日期</td>
    </tr>
    <tr>
      <td class="tab_title">联系人</td>
      <td class="contact"></td>
      <td class="tab_title">区域</td>
      <td class="areaStr"></td>
      <td class="tab_title">卡号</td>
      <td class="memberNo"></td>
      <td class="tab_title">会员会龄</td>
      <td class="dateKeep"></td>
      <td class="serviceName0"></td>
      <td class="serviceTime0"></td>
      <td class="deadLine0"></td>
    </tr>
    <tr>
      <td class="tab_title"><div class="radNormal"  isParent="false"  id="selectedCustomer2"></div>手机</td>
      <td class="mobile"></td>
      <td class="tab_title">开户行</td>
      <td class="bank"></td>
      <td class="tab_title">入会日期</td>
      <td class="joinDateStr"></td>
      <td class="tab_title">到期</td>
      <td class="serviceDeadLineStr"></td>
      <td class="serviceName1"></td>
      <td class="serviceTime1"></td>
      <td class="deadLine1"></td>
    </tr>
    <tr>
      <td class="tab_title">座机</td>
      <td class="landLine"></td>
      <td class="tab_title">开户名</td>
      <td class="bankAccountName"></td>
      <td class="tab_title">储值余额</td>
      <td class="balance"></td>
      <td class="tab_title">会员卡累计消费</td>
      <td class="memberConsumeTotal"></td>
      <td class="serviceName2"></td>
      <td class="serviceTime2"></td>
      <td class="deadLine2"></td>
    </tr>
    <tr>
      <td class="tab_title">地址</td>
      <td class="address"></td>
      <td class="tab_title">账户</td>
      <td class="bankAccountName"></td>
      <td class="tab_title">累计消费</td>
      <td class="totalAmount"></td>
      <td class="tab_title">累计销售退货</td>
      <td class="totalReturnAmount"></td>
      <td class="serviceName3"></td>
      <td class="serviceTime3"></td>
      <td class="deadLine3"></td>
    </tr>
    <tr>
      <td class="tab_title">结算方式</td>
      <td class="settlementTypeStr"></td>
      <td class="tab_title">退货次数</td>
      <td class="countCustomerReturn"></td>
      <td class="tab_title">当前应付</td>
      <td class="totalDebt"></td>
      <td class="tab_title">当前应收</td>
      <td class="totalReceivable"></td>
      <td class="serviceName4"></td>
      <td class="serviceTime4"></td>
      <td class="deadLine4"></td>
    </tr>
  </table>

  <table cellpadding="0" cellspacing="0" class="tabMerge tab2" id="childVehicleTable">
    <col width="30">
    <col width="80">
    <col width="80">
    <col width="70">
    <col width="60">
    <col width="60">
    <col width="110">
    <col width="80">
    <col width="110">
    <col width="100">
    <col width="80">
    <col width="380">
    <tr class="tab_title">
      <td colspan="12">拥有车辆数：<span id="childVehicleNum">0</span>辆</td>
    </tr>
    <tr class="tab_title">
      <td>NO</td>
      <td>车牌</td>
      <td>车辆品牌</td>
      <td>车型</td>
      <td>年代</td>
      <td>排量</td>
      <td>车架号</td>
      <td>发动机号</td>
      <td>车身颜色</td>
      <td>购车日期</td>
      <td>进厂里程</td>
      <td>预约服务</td>
    </tr>
  </table>
</div>
 </div>
<div class="height"></div>
<div id="mergeCustomerDiv" class="mergeConfirm"></div>
</div>
</div>

</body>
</html>
