<jsp:useBean id="loanTransfersDTO" scope="request" type="com.bcgogo.txn.dto.LoanTransfersDTO"/>
<%--
  Created by IntelliJ IDEA.
  User: ZhangJuntao
  Date: 12-10-22
  Time: 上午11:58
  转账中
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <%@include file="/WEB-INF/views/header_script.jsp" %>
    <script type="text/javascript" src="js//page/admin/loanMoney<%=ConfigController.getBuildVersion()%>.js"></script>
    <title>系统管理——货款转账</title>
<body>
<%@include file="/WEB-INF/views/header_html.jsp" %>
<div class="i_main clear">
    <jsp:include page="../systemManagerNavi.jsp">
        <jsp:param name="currPage" value="loanTransfersNaviMenu"/>
    </jsp:include>
    <div class="i_main">
        <div class="i_mainRight">
            <div class="sms_rechange">
                <div class="sms_rechangeBody">
                    <table cellpadding="0" cellspacing="0" class="sms_rechangeTable">
                        <col width="100">
                        <col/>
                        <tr>
                            <td style="color:#000000;">充值序号</td>
                            <td>${loanTransfersDTO.transfersNumber}</td>
                        </tr>
                        <tr>
                            <td style="color:#000000;">日期</td>
                            <td>${loanTransfersDTO.transfersTimeStr}</td>
                        </tr>
                        <tr>
                            <td style="color:#000000;">货款分类</td>
                            <td><span>${loanTransfersDTO.type.value}</span></td>
                        </tr>
                        <tr>
                            <td style="color:#000000;">转账金额</td>
                            <td><span class="price">${loanTransfersDTO.amount}</span>元</td>
                        </tr>
                        <tr>
                            <td style="color:#000000;">备注</td>
                            <td><span>${loanTransfersDTO.memo}</span></td>
                        </tr>
                        <tr>
                            <td></td>
                            <td class="rechangeIn"><input type="button" value="${loanTransfersDTO.status.value}" onfocus="this.blur();"/></td>
                        </tr>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>
${chinapayForm}
<script type="text/javascript">
    $(function() {
        var form = $("#form_chinapay")[0];
        if (form != null) {
            form.submit();
        }
    });
</script>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>