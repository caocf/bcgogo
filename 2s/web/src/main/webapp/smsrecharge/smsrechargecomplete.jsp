<%@ page import="com.bcgogo.txn.dto.SmsRechargeDTO" %>
<%@ page import="java.util.List" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    //会员个性化
    boolean isMemberSwitchOn = WebUtil.checkMemberStatus(request);
    int pageNo = Integer.parseInt(request.getAttribute("pageNo").toString());
    int pageSize = Integer.parseInt(request.getAttribute("pageSize").toString());
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>短信充值</title>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/moreHistory<%=ConfigController.getBuildVersion()%>.css"/>
    <%@include file="/WEB-INF/views/header_script.jsp" %>
</head>

<body class="bodyMain">

<%if (request.getAttribute("info") != null) {%>
<script type="text/javascript" language="JavaScript">
    defaultStorage.setItem(storageKey.MenuUid,"WEB.CUSTOMER_MANAGER.SMS_RECHARGE");

    alert("${info}");
</script>
<%}%>

<%@include file="/WEB-INF/views/header_html.jsp" %>

<div class="i_main">
    <div class="i_search">
        <div class="i_recharge">
            <jsp:include page="../customer/customerNavi.jsp" >
              <jsp:param name="currPage" value="smsRecharge" />
            </jsp:include>
        </div>
        <div class="i_main">
            <div class="i_mainRight">

                <%
                    if (request.getAttribute("smsRechargeDTO") != null) {
                        SmsRechargeDTO smsRechargeDTO = (SmsRechargeDTO) request.getAttribute("smsRechargeDTO");
                %>

                <div class="sms_rechange">
                    <div class="sms_rechangeTop"></div>
                    <div class="sms_rechangeBody">
                        <table cellpadding="0" cellspacing="0" class="sms_rechangeTable">
                            <col width="100">
                            <col width="100">
                            <col/>
                            <tr>
                                <td style="color:#000000;">充值时间</td>
                                <td><%=smsRechargeDTO.getRechargeTimeStr()%>
                                </td>
                                <td></td>
                            </tr>
                            <tr>
                                <td style="color:#000000;">充值序号</td>
                                <td><%=smsRechargeDTO.getRechargeNumber() == null ? 0 : smsRechargeDTO.getRechargeNumber()%>
                                </td>
                                <td></td>
                            </tr>
                            <tr>
                                <td style="color:#000000;">短信余额</td>
                                <td><span>${smsBalance}</span>元</td>
                                <td></td>
                            </tr>
                            <tr>
                                <td style="color:#000000;">充值金额</td>
                                <td><span class="price"><%=smsRechargeDTO.getRechargeAmount()%></span>元</td>
                                <td></td>
                            </tr>

                            <tr>
                                <td colspan="2"></td>
                                <td class="rechangeComplete">
                                    <input type="button"
                                           value="<%=smsRechargeDTO.getStatusDesc()%>"
                                           onfocus="this.blur();"/>
                                </td>
                            </tr>
                        </table>
                    </div>
                    <div class="sms_rechangeBottom"></div>
                </div>

                <%
                    }
                %>

                <div class="height"></div>

                <%
                    if (request.getAttribute("smsRechargeDTOList") != null) {
                %>

                <div class="div_rechange">
                    <table cellpadding="0" cellspacing="0" class="table2">
                        <tr class="i_tabelBorder">
                            <td colspan="6">共有<span class="hover">${recordCount}</span>条历史记录&nbsp;&nbsp;充值总额<span
                                    class="price">${rechargeTotal}</span>元
                            </td>
                        </tr>
                        <tr class="i_tableTitle">
                            <td style="border-left:none;">No</td>
                            <td>消费时间</td>
                            <td>充值序号</td>
                            <td>短信余额</td>
                            <td>充值金额</td>
                            <td style="border-right:none;">状态</td>
                        </tr>

                        <%
                            int no = 0;
                            for (SmsRechargeDTO smsRechargeDTO : ((List<SmsRechargeDTO>) request.getAttribute("smsRechargeDTOList"))) {
                                no++;
                        %>
                        <tr>
                            <td style="border-left:none;"><%=no + (pageNo - 1) * pageSize%>
                            </td>
                            <td><%=smsRechargeDTO.getRechargeTimeStr()%>
                            </td>
                            <td><%=smsRechargeDTO.getRechargeNumber() == null ? 0 : smsRechargeDTO.getRechargeNumber()%>
                            </td>
                            <td class="table_right"><%=smsRechargeDTO.getSmsBalance() == null ? 0 : smsRechargeDTO.getSmsBalance()%>
                            </td>
                            <td class="table_right"><%=smsRechargeDTO.getRechargeAmount()%>
                            </td>
                            <td style="border-right:none;"><%=smsRechargeDTO.getStatusDesc()%>
                            </td>
                        </tr>
                        <%
                            }
                        %>
                    </table>
                    <div class="height"></div>
                    <div class="i_leftBtn  rechangeList">
                        <%if (pageNo > 1) {%>
                        <div class="lastPage">
                            <a href="smsrecharge.do?method=smsrechargejump&pageNo=${pageNo-1}&pageSize=${pageSize}&rechargenumber=${rechargeNumber}">上一页</a>
                        </div>
                        <%}%>
                        <div>${pageNo}</div>
                        <%if (no >= pageSize) {%>
                        <div class="nextPage">
                        <a href="smsrecharge.do?method=smsrechargejump&pageNo=${pageNo+1}&pageSize=${pageSize}&rechargenumber=${rechargeNumber}">下一页</a>
                        </div>
                        <%}%>
                    </div>
                </div>

                <%
                    }
                %>
            </div>
        </div>
    </div>
</div>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>