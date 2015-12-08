<%@ page contentType="text/html;charset=UTF-8" language="java" %>
 <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<%@ include file="/WEB-INF/views/includes.jsp" %>
<%
%>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>短信充值</title>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <script type="text/javascript">
        defaultStorage.setItem(storageKey.MenuUid,"WEB.CUSTOMER_MANAGER.SMS_RECHARGE");
    </script>
    <%@include file="/WEB-INF/views/header_script.jsp" %>
</head>
<body class="bodyMain">
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
                <div class="sms_rechange">
                    <div class="sms_rechangeBody">
                        <table cellpadding="0" cellspacing="0" class="sms_rechangeTable">
                            <col width="100">
                            <col/>
                            <tr>
                                <td style="color:#000000;">充值序号</td>
                                <td>${rechargeNumber}</td>
                            </tr>
                            <tr>
                                <td style="color:#000000;">日期</td>
                                <td>${rechargeTime}</td>
                            </tr>
                            <tr>
                                <td style="color:#000000;">短信余额</td>
                                <td><span>${smsBalance}</span>元</td>
                            </tr>
                            <tr>
                                <td style="color:#000000;">充值金额</td>
                                <td><span class="price">${rechargeAmount}</span>元</td>
                            </tr>
                            <tr>
                                <td></td>
                                <td class="rechangeIn"><input type="button" value="充值中" onfocus="this.blur();"/></td>
                            </tr>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
${chinapayForm}

<script type="text/javascript" language="JavaScript">
    (function() {
        $().ready( function() {
            var form = $("#form_chinapay")[0];
            if (form != null) {
                //form.target = "_blank";
                form.submit();

                document.body.removeChild(form);
            }
        });
    })();


</script>
<%@include file="/WEB-INF/views/footer_html.jsp" %>

</body>
</html>
