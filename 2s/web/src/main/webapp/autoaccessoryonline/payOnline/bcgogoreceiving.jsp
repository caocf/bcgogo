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
    <title>支付信息</title>
<body>
<%@include file="/WEB-INF/views/header_html.jsp" %>
<div class="i_main clear">
    <jsp:include page="../autoAccessoryOnlineNavi.jsp">
        <jsp:param name="currPage" value="bcgogoReceivableRecord"/>
    </jsp:include>
    <div class="i_main">
        <div class="i_mainRight">
            <div class="sms_rechange">
                <div class="sms_rechangeBody">
                    <table cellpadding="0" cellspacing="0" class="sms_rechangeTable">
                        <col width="100">
                        <col/>
                        <tr>
                            <td style="color:#000000;">日期</td>
                            <td>${bcgogoReceivableDTO.paymentTimeStr}</td>
                        </tr>
                        <tr>
                            <td style="color:#000000;">费用类型</td>
                            <td><span>${bcgogoReceivableDTO.paymentTypeStr}</span></td>
                        </tr>
                        <tr>
                            <td style="color:#000000;">付款金额</td>
                            <td><span class="price">${bcgogoReceivableDTO.paidAmount}</span>元</td>
                        </tr>
                        <c:if test="${not empty receivableErrorInfo}">
                            <tr>
                                <td style="color:red;" colspan="2">${receivableErrorInfo}&nbsp;&nbsp;&nbsp; <a href="javascript:window.close();" style="cursor: pointer" class="blue_color">关闭当前页</a></td>
                            </tr>
                        </c:if>

                    </table>
                </div>
            </div>
        </div>
    </div>
</div>
<c:if test="${empty receivableErrorInfo}">
${chinapayForm}
</c:if>
<script type="text/javascript">
    $(function () {
        <c:if test="${empty receivableErrorInfo}">
        var form = $("#form_chinapay")[0];
        if (form != null) {
            form.submit();
        }
        </c:if>
    });
</script>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>