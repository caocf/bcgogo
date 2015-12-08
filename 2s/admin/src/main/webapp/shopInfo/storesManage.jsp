<%@ page import="com.bcgogo.util.Pager" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>后台管理系统——店面管理</title>

	<%-- styles --%>
    <link rel="stylesheet" type="text/css" href="styles/backstage.css"/>
    <link rel="stylesheet" type="text/css" href="styles/backAgent.css"/>
    <link rel="stylesheet" type="text/css" href="styles/storesMange.css"/>
	<%@include file="/WEB-INF/views/style-thirdpartLibs.jsp" %>

	<%-- scripts --%>
	<%@include file="/WEB-INF/views/script-thirdpartLibs.jsp" %>
	<%@include file="/WEB-INF/views/script-common.jsp" %>
    <script type="text/javascript" src="js/extension/My97DatePicker/WdatePicker.js"></script>
    <script type="text/javascript" src="js/txnbase_2.js"></script>
    <script type="text/javascript" src="js/stores_2.js"></script>
    <script type="text/javascript" src="js/searchDefault.js"></script>
    <script type="text/javascript">
        function checkMoney() {
            var foo = APP_BCGOGO.Validator;
            var money = $("#money").val();
			if (money != "") {
				if (foo.stringIsPrice(money) === false) {
                    alert("充值金额为数字！请重新输入")
                    return false;
                } else {
                    return true;
                }
            }
        }
    </script>
</head>
<body>
<div class="main">
<!--头部-->
<div class="top">
    <div class="top_left">
        <div class="top_name">统购后台管理系统</div>
        <div class="top_image"></div>
		你好，<span>张三</span>|<a href="#">退出</a>
	</div>
    <div class="top_right"><span>2011.11.23 14:01 星期三</span></div>
</div>
<!--头部结束-->
<div class="body">

<!--左侧列表-->
<%@include file="/WEB-INF/views/left.jsp" %>
<!--左侧列表结束-->

<!--右侧内容-->
<div class="bodyRight">
<!--搜索-->
<div class="rightText">
        <div class="textLeft"></div>
	<div class="textBody">
		<input type="text" value="店铺名" id="txt_shopName"/>
                <input type="hidden" value="" id="shopId"/>
                </div>
                <div class="textRight"></div>
</div>

<div class="rightText">
                <div class="textLeft"></div>
                <div class="textBody"><input type="text" value="店主" id="txt_shopOwner"/></div>
                <div class="textRight"></div>
</div>

<div class="rightText">
                <div class="textLeft"></div>
                <div class="textAddressbody"><input type="text" value="地址" id="txt_address"/></div>
                <div class="textRight"></div>
</div>

<div class="rightText">
                <div class="textLeft"></div>
                <div class="textBody"><input type="text" value="手机/电话" id="txt_phone"/></div>
                <div class="textRight"></div>
</div>

<input type="button" class="rightSearch" value="搜 索" />
<!--搜索结束-->
<!--内容-->
<div class="rightMain clear">
<!--店面基本信息-->
<!--代理商-->
<div class="rightTime">
                    <div class="timeLeft"></div>
                    <div class="timeBody">
		<div class="pen"></div>店面基本信息
                    </div>
                    <div class="timeRight"></div>
</div>
<!--代理商结束-->

<!--table-->
<table cellpadding="0" cellspacing="0" id="agent_tb" class="clear">
                    <col width="73" style="*width:53px;">
                    <col width="153">
                    <col width="68">
                    <col width="188">
                    <col width="68">
                    <col width="297">
                    <tr>
                        <td class="stroes_first">店铺名</td>
                        <td>${shopDTO.name}</td>
                        <td>地 址</td>
                        <td>${shopDTO.address}</td>
                        <td>主营项目</td>
                        <td>${shopDTO.businessScope}</td>
                    </tr>
                    <tr>
                        <td class="stroes_first">店 主</td>
                        <td> ${shopDTO.storeManager}</td>
                        <td>营业时间</td>
                        <td>${shopDTO.businessHours}</td>
                        <td>成立时间</td>
                        <td>${shopDTO.establishedStr}</td>
                    </tr>
                    <tr>
                        <td class="stroes_first">手机号</td>
                        <td>${shopDTO.mobile}</td>
                        <td>签约业务员</td>
                        <td>${shopDTO.agent}</td>
                        <td>店面人员</td>
		<td>${shopDTO.personnel}名<div class="shuaxin">刷新库存数据</div></td>
                    </tr>
</table>
<!--table结束-->
<!--店面基本信息息结束-->
<!--历史记录-->
<!--代理商-->
<div class="rightTime  salesman clear">
                    <div class="timeLeft"></div>
                    <div class="timeBody">
                        <form   action="shopInfoHistory.do?method=shopInfo" method="POST" onSubmit="return checkMoney()">
                        <label>历史记录</label>
                        <!--时间-->
                        <div class="i_searchTime">
                            <div class="textLeft"></div>
                            <div class="textBody">
					<input type="hidden" name="shopId" value="${shopDTO.id}" />
					<input type="text" id="startTime" name="startTime" onclick="WdatePicker()" readonly="true" />
					<img src="images/datePicker.jpg" onclick="WdatePicker({el:'bxId',dateFmt:'yyyy-MM-dd'})" />
                            </div>
                            <div class="textRight"></div>
                        </div>
                        <!--时间-->
                        <div class="i_searchTime">
                            <div class="textLeft"></div>
                            <div class="textBody">
					<input type="text" id="endTime" name="endTime" onclick="WdatePicker()" readonly="true" />
					<img src="images/datePicker.jpg" onclick="WdatePicker({el:'bxId',dateFmt:'yyyy-MM-dd'})" />
                            </div>
                            <div class="textRight"></div>
                        </div>
                        <!--充值-->
                     <div class="i_searchTime recharges select" id="select">
                <div class="textLeft"></div>
				<div class="textBody"><input type="text" id="money" name="money" class="valt" /></div>
                <div class="textRight"></div>
				<img src="images/search_xia.png" style="display:block;position:absolute;top:10px;right:10px;" />
                 <div class="option" id="option">
                  <ul>
                   <li tip="30">30</li>
                   <li tip="50">50</li>
                   <li tip="100">100</li>
                  </ul>
                 </div>
           </div>
                        <!--其他-->
                        <div class="other">
                            <div class="textLeft"></div>
				<div class="textBody"><input type="text" id="other" name="other" /></div>
                            <div class="textRight"></div>
                        </div>
			<input type="submit" class="rightSearch search_or" value="搜 索" />
                            </form>
                    </div>
                    <div class="timeRight"></div>
</div>
<!--代理商结束-->
<!--table-->
<table cellpadding="0" cellspacing="0" id="histroy_tb" class="clear">
                    <col width="70">
                    <col width="185">
                    <col width="155">
                    <col width="305">
                    <col width="155">
                    <col width="145">
                    <thead>
                    <tr>
                        <th>NO</th>
                        <th>时间</th>
                        <th>项目</th>
                        <th>内容</th>
                        <th>处理人</th>
                        <th>处理结果</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="smsRechargeDTOList" items="${smsRechargeDTOList}" varStatus="status">
                        <c:choose>
                            <c:when test="${(status.index + 1) % 2 != 0}">
                                <tr>
                                    <td>${status.index + 1}</td>
                                    <td>${smsRechargeDTOList.rechargeTimeStr}</td>
						<td>充值</td>
                                    <td>余额${smsRechargeDTOList.smsBalance}，充值${smsRechargeDTOList.rechargeAmount}</td>
                                    <td>${smsRechargeDTOList.userName}</td>
                                    <td>
                                    <c:choose>
								<c:when test="${smsRechargeDTOList.state==0}">新记录</c:when>
								<c:when test="${smsRechargeDTOList.state==1}">已提交银联</c:when>
								<c:otherwise>完成</c:otherwise>
                                          </c:choose>
                                    </td>
                                </tr>
                            </c:when>
                            <c:when test="${(status.index + 1) % 2 == 0}">
                                <tr class="agent_bg">
                                    <td>${status.index + 1}</td>
                                    <td>${smsRechargeDTOList.rechargeTimeStr}</td>
						<td>充值</td>
                                    <td>余额${smsRechargeDTOList.smsBalance}，充值${smsRechargeDTOList.rechargeAmount}</td>
                                    <td>${smsRechargeDTOList.userName}</td>
                                    <td>
                             <c:choose>
								<c:when test="${smsRechargeDTOList.state}==0">新记录</c:when>
								<c:when test="${smsRechargeDTOList.state}==1">已提交银联</c:when>
								<c:otherwise>完成</c:otherwise>
                              </c:choose>
                                    </td>
                                </tr>
                            </c:when>
                        </c:choose>
                    </c:forEach>
                    </tbody>
</table>
<%
                        //处理分页
                        Pager pager = (Pager) request.getAttribute("pager");
%>
<div class="i_leftBtn  rechangeList">
                        <%if (pager.getCurrentPage() > 1) {%>
	<div class="lastPage"><a href="shopInfoHistory.do?method=shopInfo&pageNo=<%=pager.getLastPage()%>&rechargeamount=${rechargeAmount}&shopId=${shopDTO.id}&startTime=${startTime}&endTime=${endTime}&money=${money}&other=${other}">上一页</a></div>
	<%
		}
		if (pager.getTotalPage() > 1) {
	%>
                        <div class="onlin_his"><%=pager.getCurrentPage()%></div>
	<%
		}
		if (pager.hasNextPage()) {
	%>
	<div class="nextPage"><a href="shopInfoHistory.do?method=shopInfo&pageNo=<%=pager.getNextPage()%>&rechargeamount=${rechargeAmount}&shopId=${shopDTO.id}&startTime=${startTime}&endTime=${endTime}&money=${money}&other=${other}">下一页</a></div>
                        <%}%>
</div>
<!--table结束-->
<!--历史记录结束-->
</div>

<!--内容结束-->
<!--圆角-->
<div class="bottom_crile clear"></div>
<div class="crile"></div>
<div class="bottom_x"></div>
<div style="clear:both;"></div>
</div>
<!--圆角结束-->
</div>
<!--右侧内容结束-->
</div>

</body>
</html>