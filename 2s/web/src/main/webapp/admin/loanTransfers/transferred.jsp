<%--
  Created by IntelliJ IDEA.
  User: ZhangJuntao
  Date: 12-10-22
  Time: 上午11:58
  开始转账
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/returnStorage<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/returnsTan<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/danjuCg<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/yinwaiCount<%=ConfigController.getBuildVersion()%>.css"/>
    <%@include file="/WEB-INF/views/header_script.jsp" %>
    <script type="text/javascript">
        var time = 5;
        function closeWindow() {
            window.setTimeout('closeWindow()', 1000);
            if (time >= 0) {
                $("#showMessage").html(time--);
            } else {
//                window.opener = null; //关闭窗口时不出现提示窗口
//                window.close();
                toLoanTransfersPage();
            }
        }

        function toLoanTransfersPage() {
            window.location.href = "loanTransfers.do?method=showPage";
        }
    </script>
    <title>系统管理——货款转账</title>
<body onload="closeWindow();">
<%@include file="/WEB-INF/views/header_html.jsp" %>
<div class="i_main clear">
    <jsp:include page="../systemManagerNavi.jsp">
        <jsp:param name="currPage" value="loanTransfersNaviMenu"/>
    </jsp:include>
    <div class="i_mainRight" id="i_mainRight">
        <div class="sms_rechange">
            <div class="sms_rechangeBody">
                <table cellpadding="0" cellspacing="0" class="sms_rechangeTable">
                    <col width="100">
                    <col/>
                    <c:choose>
                        <c:when test="${loanTransfersInfo!=null}">
                            <tr>
                                <td style="color:#000000;">充值失败：</td>
                                <td>${loanTransfersInfo}</td>
                            </tr>
                        </c:when>
                        <c:otherwise>
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
                                <td class="rechangeIn"><input type="button" value="${loanTransfersDTO.status.value}" onclick="toLoanTransfersPage()" onfocus="this.blur();"/></td>
                            </tr>
                        </c:otherwise>
                    </c:choose>
                    <tr>
                        <td></td>
                        <td style="color:#000;font-size: 15px">倒计时<span style="color:red;margin: 0 10px 0 10px;" id="showMessage"></span>秒后 离开当前窗口！</td>
                    </tr>
                </table>
            </div>
        </div>
    </div>
</div>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>