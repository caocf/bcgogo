<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page import="com.bcgogo.common.Pager" %>
<%@ page import="com.bcgogo.txn.dto.SmsRechargeDTO" %>
<%@ page import="java.util.List" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    //会员个性化
    boolean isMemberSwitchOn = WebUtil.checkMemberStatus(request);
%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>客户管理_短信充值</title>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css"  href="styles/moreHistory<%=ConfigController.getBuildVersion()%>.css"/>
    <%@include file="/WEB-INF/views/header_script.jsp" %>
    <script type="text/javascript" src="js/utils/tableUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <style type="text/css">
        #rechangeTable {
            border: 1px solid #bbbbbb;
    }
    </style>
    <script type="text/javaScript">
        $(function() {
            tableUtil.tableStyle('#rechangeTable', '.i_tabelBorder,.i_tableTitle');
    });


    </script>
</head>
<body class="bodyMain">

<%if (request.getAttribute("info") != null) {%>
<script type="text/javascript" language="JavaScript">
    alert("${info}");
</script>
<%}%>

<%@include file="/WEB-INF/views/header_html.jsp" %>

<div class="i_main clear">
    <div class="">
        <jsp:include page="../sms/smsNavi.jsp">
            <jsp:param name="catalogue" value="smsRecharge"/>
        </jsp:include>
        <div class="i_main">
            <div class="i_mainRight">

                <div class="operation-tip clearfix">
                    <ul class="operation-tips-content clearfix" style="float:left;">
                        <li class="left-side"></li>
                        <li>
                            <span class="tips">第一步：选择充值金额</span>
                        </li>
                        <li class="arrow"></li>
                        <li>
                            <span class="tips">第二步：进入银联支付页面</span>
                        </li>
                        <li class="arrow"></li>
                        <li>
                            <span class="tips">第三步：选择支付方式进行支付</span>
                        </li>
                        <li class="arrow"></li>
                        <li>
                            <span class="tips">完成</span>
                        </li>
                        <li class="right-side"></li>
                    </ul>
                </div>
                <div class="sysHelp" onclick="toHelper('transferHelper')">短信充值帮助</div>

                <div class="sepcial-tips">提示：初次使用银联支付时请先安装安全控件，安装完成后刷新页面再支付。</div>

                <div class="sms_rechange">
                    <div class="sms_rechangeBody">
                        <table cellpadding="0" cellspacing="0" class="sms_rechangeTable">
                            <col width="100">
                            <col width="400">
                            <tr>
                                <td>账户余额</td>
                                <td><span>${smsBalance}</span>元</td>
                            </tr>
                            <tr>
                                <td>充值金额</td>
                                <td>
                                    <form name="form_smsrecharge" id="form_smsrecharge" target="_blank"
                                          action="smsrecharge.do" method="get">
                                        <input type="hidden" name="method" value="smsrecharging"/>
                                        <label><input type="radio" name="rechargeamount" value="100" checked="checked"/>100元</label>
                                        <label><input type="radio" name="rechargeamount" value="200"/>200元</label>
                                        <label><input type="radio" name="rechargeamount" value="500"/>500元</label>
                                        <label><input type="radio" name="rechargeamount" value="1000"/>1000元</label>
                                        <label><input type="radio" name="rechargeamount" value="1500"/>1500元</label>
                                        <label><input type="radio" name="rechargeamount" value="2000"/>2000元</label>
                                        <label><input type="radio" name="rechargeamount"
                                                      id="radio_otheramount"/>其他</label>
                                    </form>
                                    <input type="text" class="multiple" id="input_otheramount"/>元
                                </td>
                            </tr>
                            <tr>
                                <td>充值方式</td>
                                <td class="union">
                                    <label>
                                        <input type="radio" name="radioType" value="1" checked="checked"/>
                                        <img src="images/unionPay.jpg"/>
                                    </label>
                                </td>
                            </tr>
                            <tr>
                                <td></td>
                                <td class="rechange">
                                    <input type="button" id="button_recharge" value="确定充值"/>

                                    <div style="color:red;display:none" id="showRemind">请使用IE浏览器进行充值</div>
                                </td>
                            </tr>
                        </table>

                    </div>
                </div>
                <div class="height"></div>
                <div class="div_rechange">
                    <table id="rechangeTable" cellpadding="0" cellspacing="0" class="table2">
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
                            if (request.getAttribute(
                                    "smsRechargeDTOList") != null && ((List<SmsRechargeDTO>) request.getAttribute(
                                    "smsRechargeDTOList")).size() > 0) {
                                int rowNum = 0;
                                for (SmsRechargeDTO smsRechargeDTO : ((List<SmsRechargeDTO>) request.getAttribute(
                                        "smsRechargeDTOList"))) {
                                    rowNum++;
                        %>
                        <tr class="table-row-original">
                            <td style="border-left:none;"><%=rowNum%>

                            </td>
                            <td><%=smsRechargeDTO.getRechargeTimeStr()%>
                            </td>
                            <td><%=smsRechargeDTO.getRechargeNumber() == null ? "" : smsRechargeDTO.getRechargeNumber().toString()%>
                            </td>
                            <td><%=smsRechargeDTO.getSmsBalance() == null ? "" : smsRechargeDTO.getSmsBalance().toString()%>
                            </td>
                            <td><%=smsRechargeDTO.getRechargeAmount()%>
                            </td>
                            <td style="border-right:none;"><%=smsRechargeDTO.getStatusDesc()%>
                            </td>
                        </tr>
                        <% }
                        }%>

                    </table>
                    <div class="height"></div>

                    <%
                        //处理分页
                        Pager pager = (Pager) request.getAttribute("pager");
                    %>
                    <div class="i_leftBtn  rechangeList">
                        <%if (pager.getCurrentPage() > 1) {%>
                        <div class="lastPage">
                        <a href="smsrecharge.do?method=smsrecharge&pageNo=<%=pager.getLastPage()%>&rechargeamount=${rechargeAmount}">上一页</a>
                        </div>
                        <%
                            }
                            if (pager.getTotalPage() > 1) {
                        %>
                        <div class="onlin_his"><%=pager.getCurrentPage()%>
                        </div>
                        <%
                            }
                            if (pager.hasNextPage()) {
                        %>
                        <div class="nextPage">
                        <a href="smsrecharge.do?method=smsrecharge&pageNo=<%=pager.getNextPage()%>&rechargeamount=${rechargeAmount}">下一页</a>
                        </div>
                        <%}%>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<script type="text/javascript">
    (function() {
        <%if (request.getAttribute("rechargeAmount") != null) {%>
        init(${rechargeAmount});
        <%}%>

        //初始化充值金额
        function init(recharge_amount) {
            var radios = document.getElementsByName("rechargeamount");
            for (var i = 0, l = radios.length; i < l; i++) {
                if (recharge_amount == radios[i].value) {
                    radios[i].checked = true;
                    return;
                }
            }
            setOtherAmount(recharge_amount);
        }

        //设置其他充值金额
        function setOtherAmount(amount) {
            if (isNaN(amount)) return;

            amount = parseInt(amount);
            if (amount < 50 || amount > 2000) return;

            $("#input_otheramount")[0].value = amount;
            $("#radio_otheramount")[0].value = amount;
            $("#radio_otheramount")[0].checked = true;
        }

        $("#input_otheramount")[0].onfocus = function() {
            $("#radio_otheramount")[0].checked = true;
        }

        $("#button_recharge")[0].onclick = function() {
//          if(!document.all){    // 支持火狐充值
//            $("#showRemind").css('display','block');
//             return ;
//          }

            if ($("#radio_otheramount")[0].checked) {
                var amount = $("#input_otheramount")[0].value;
                if (isNaN(amount)) {
                    alert("请输入正确的充值金额！");
                    return;
                }

                amount = parseInt(amount);
                if (amount < 50 || amount > 2000) {
                    alert("充值金额须在50至2000之间！");
                    return;
                }

                setOtherAmount(amount);
            }
            $("#form_smsrecharge")[0].submit();
        }

    })();


</script>
<%@include file="/WEB-INF/views/footer_html.jsp" %>

</body>
</html>
