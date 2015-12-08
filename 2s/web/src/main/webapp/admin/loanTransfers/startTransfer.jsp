<%@ page import="com.bcgogo.enums.LoanType" %>
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
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.js"></script>
    <script type="text/javascript" src="js/utils/tableUtil.js"></script>
    <script type="text/javascript" src="js//page/admin/loanTransfers<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">
        <c:if test="${loanTransfersInfo!=null}">
            nsDialog.jAlert("${loanTransfersInfo}");
        </c:if>
    </script>
    <title>系统管理——货款转账</title>
<body>
<%@include file="/WEB-INF/views/header_html.jsp" %>
<div class="i_main clear">
    <jsp:include page="../systemManagerNavi.jsp">
        <jsp:param name="currPage" value="loanTransfersNaviMenu"/>
    </jsp:include>
    <div class="i_mainRight" id="i_mainRight">
        <div class="tuihuo_first loan_div">
            <span class="left_tuihuo"></span>

            <form action="loanTransfers.do?method=saveLoan" method="post" target="_blank" id="loanMoneyForm">
                <table class="jihua_tb">
                    <col width="90"/>
                    <col width="903"/>
                    <tr>
                        <td>货款分类:</td>
                        <td>
                            <div class="xiala">
                                <select name="type" style="height:22px; width:180px;margin-top: 10px;float: left;vertical-align: middle;">
                                    <option value="FIRST_PAYMENT">首次付款</option>
                                    <option value="UPGRADE_PAYMENT">升级付款</option>
                                    <option value="OTHER_PAYMENT">其他</option>
                                </select> <label>货款金额：</label>
                                <input type="text" class="daikuan textbox" id="loan_transfers_amount" value="0.0" name="amount" /><label>元</label>
                            </div>
                        </td>
                    </tr>
                    <tr>
                        <td>产品类型:</td>
                        <td class="productType ruanjian">
                            <label><input type="checkbox" id="hardProduct"/>硬件</label>
                            <label><input type="checkbox" id="softProduct"/>软件</label>
                        </td>
                    </tr>
                    <tr>
                        <td>软件产品:</td>
                        <td class="ruanjian shopVersion">
                            <c:forEach var='shopVersionDTO' items='${shopVersionDTOList}'>
                                <label>
                                    <input type="radio" name="shopVersionId" id="${shopVersionDTO.name}" value="${shopVersionDTO.id}"/>${shopVersionDTO.value}
                                </label>
                            </c:forEach>
                        </td>
                    </tr>
                    <tr>
                        <td>硬件产品:</td>
                        <td class="ruanjian products">
                            <input type="checkbox" name="products" value="读卡器" id="cardReader" />
                            <label for="cardReader">读卡器</label>
                            <input type="checkbox" name="products" value="摄像头" id="camera" />
                            <label for="camera">摄像头</label>
                            <input type="checkbox" name="products" value="会员卡" id="memberCard" />
                            <label for="memberCard">会员卡</label>
                        </td>
                    </tr>
                    <tr>
                        <td>货款备注:</td>
                        <td><input type="text" style="width:100%;" name="memo" maxlength="100" class="textbox" /></td>
                    </tr>
                    <tr>
                        <td></td>
                        <td>
                            <input type="submit" value="确定" class="buttonBig"/>
                        </td>
                    </tr>
                </table>
            </form>
            <span class="right_tuihuo"></span>
        </div>
        <div class="clear"></div>
        <div class="tuihuo_tb">
            <table class="tui_title">
                <col width="120"/>
                <col/>
                <tr>
                    <td>共有<a>${pager.totalRows}</a>条历史记录</td>
                    <td>货款总额<a href="#">${totalAmount}</a>元</td>
                </tr>
            </table>
            <table class="clear" id="tb_tui" style="table-layout:fixed;width:950px ">
                <col width="40"/>
                <col width="142"/>
                <col width="132"/>
                <col width="72"/>
                <col width="92"/>
                <col width="92"/>
                <col width="92"/>
                <col width="90"/>
                <col width="220"/>
                <tr class="tab_title">
                    <td class="first-padding">NO</td>
                    <td>转账时间</td>
                    <td>转账序号</td>
                    <td>货款金额</td>
                    <td>货款分类</td>
                    <td>软件产品</td>
                    <td>硬件产品</td>
                    <td>状态</td>
                    <td class="last-padding">货款备注</td>
                </tr>
                <c:forEach var="loan" items="${loanTransfersDTOList}" varStatus="status">
                    <tr class="table-row-original">
                        <td class="first-padding">${status.index+1}</td>
                        <td>${loan.transfersTimeStr}</td>
                        <td>${loan.transfersNumber}</td>
                        <td style="overflow: hidden;text-overflow: ellipsis;white-space: nowrap;" title="${loan.amountStr}">${loan.amountStr}</td>
                        <td><%--<a href="#">首次购买</a>--%>${loan.type.value}</td>
                        <td>${loan.shopVersionValue}</td>
                        <td style="overflow: hidden;text-overflow: ellipsis;white-space: nowrap;" title="${loan.productsStr}">${loan.productsStr}</td>
                        <td>${loan.status.value}</td>
                        <td style="overflow: hidden;text-overflow: ellipsis;white-space: nowrap;" title="${loan.memo}" class="last-padding">${loan.memo}</td>
                    </tr>
                </c:forEach>
            </table>
        </div>
        <jsp:include page="/common/paging.jsp">
            <jsp:param name="url" value="loanTransfers.do?method=showPage"></jsp:param>
        </jsp:include>
    </div>
</div>
<div id="mask" style="display:block;position: absolute;">
</div>
<%@ include file="/common/messagePrompt.jsp" %>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>