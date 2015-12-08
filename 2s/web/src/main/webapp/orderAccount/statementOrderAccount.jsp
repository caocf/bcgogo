<%--
  对账单结算页面
  Created by IntelliJ IDEA.
  User: liuWei
  Date: 13-1-9
  Time: 下午4:34
  To change this template use File | Settings | File Templates.
--%>
<%@ page import="com.bcgogo.common.WebUtil" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="bcgogo" uri="http://www.bcgogo.com/taglibs/tags" %>
<%
    boolean depositDisplay = (Boolean) request.getAttribute("depositDisplay");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>对账单结算</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <link rel="stylesheet" type="text/css" href="styles/up1<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/moneyDetail<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css"
          href="js/extension/jquery/plugin/jquery-ui/themes/redmond/jquery-ui-1.8.21.custom.css"/>
    <link rel="stylesheet" type="text/css"
          href="js/extension/jquery/plugin/jquery-ui/themes/redmond/jquery.ui.timepicker-addon.css"/>
    <link rel="stylesheet" type="text/css" href="styles/setleNew<%=ConfigController.getBuildVersion()%>.css"/>

    <style>
        #ui-datepicker-div, .ui-datepicker {
            font-size: 90%;
        }
    </style>
    <script type="text/javascript" src="js/extension/jquery/jquery-1.4.2.min.js"></script>
    <script type="text/javascript"
            src="js/extension/jquery/plugin/jquery-ui/ui/jquery-ui-1.8.21.custom.min.js"></script>
    <script type="text/javascript"
            src="js/extension/jquery/plugin/jquery-ui/ui/jquery.ui.timepicker-addon.min.js"></script>
    <script type="text/javascript"
            src="js/extension/jquery/plugin/jquery-ui/ui/i18n/jquery.ui.datepicker-zh-CN.min.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.min.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery.validate.min.js"></script>
    <script type="text/javascript" src="js/extension/json2/json2.js"></script>
    <script type="text/javascript" src="js/base<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/application<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript"
            src="js/module/bcgogo-noticeDialog<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript"
            src="js/statementAccount/statementOrderAccount<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/settleAccounts<%=ConfigController.getBuildVersion()%>.js"></script>
</head>
<body>
<%@ include file="/common/messagePrompt.jsp" %>
<input type="hidden" id="orderDebtType" name="orderDebtType" value="${orderDebtType}"/>

<div class="i_searchBrand i_searchBrand-account i_history">
    <div class="i_arrow"></div>
    <div class="i_upLeft"></div>
    <div class="i_upCenter">
        <div class="i_note" id="div_drag">对账单结算</div>
        <div class="i_close" id="div_close"></div>
    </div>
    <div class="i_upRight"></div>
    <div class="i_upBody">


        <label id="cardsTitle"></label>
        <label id="totalDebt" style="line-height: 27px;">0</label>元
        <input type="hidden" id="hiddenTotal">

        <div class="clear"></div>

        <div class="common_settlement">
            <table>
                <col width="130px"/>
                <col/>
                <tr>
                    <td class="left">
                        <span class="label"><span id="receivableText">实收总额</span>：</span><span id="settledAmountLabel" class="num">0</span>元
                        <input type="hidden" id="settledAmount" name="settledAmount" autocomplete="off"/>
                    </td>
                    <td>
                        <div>现&nbsp;&nbsp;&nbsp;&nbsp;金：<input type="text" id="cashAmount" name="cashAmount" autocomplete="off"/>元 </div>
                        <div>银&nbsp;&nbsp;&nbsp;&nbsp;联：<input type="text" id="bankAmount" name="bankAmount" autocomplete="off"/>元 </div>
                        <div>
                            支&nbsp;&nbsp;&nbsp;&nbsp;票：<input type="text" id="bankCheckAmount" name="bankCheckAmount" autocomplete="off"/>元&nbsp;&nbsp;
                            <input type="text" placeholder="支票号" maxlength="20" id="bankCheckNo" name="bankCheckNo" autocomplete="off"/>
                        </div>
                        <bcgogo:hasPermission permissions="WEB.CUSTOMER_MANAGER.MEMBERSHIP_PACKAGE_MANAGER">
                        <bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.MEMBER_STORED_VALUE">
                        <c:if test="${orderDebtType=='CUSTOMER_DEBT_RECEIVABLE'}">
                            <div>
                                会员储值：<input type="text" id="memberAmount" style="width:100px;" autocomplete="off"/>元&nbsp;&nbsp;
                                <input type="text" id="accountMemberNo" placeholder="请刷卡/输入卡号" autocomplete="off"/>
                                <input type="password" id="accountMemberPassword" placeholder="密码" autocomplete="off"/>
                                储值余额：<span id="memberBalance">${memberBalance}</span>元
                                <span style="display:none;"><span id="memberNo">${memberNo}</span></span>
                            </div>
                        </c:if>
                        </bcgogo:hasPermission>
                        </bcgogo:hasPermission>

                        <c:if test="${orderDebtType=='CUSTOMER_DEBT_RECEIVABLE'}">
                            <bcgogo:hasPermission permissions="WEB.VERSION.CUSTOMER.DEPOSIT.USE">
                                <c:if test="<%=depositDisplay%>">
                                    <div>预&nbsp;收&nbsp;款：<input type="text" id="depositAmount" name="depositAmount" autocomplete="off"/>元&nbsp;&nbsp;
                                        预收款余额：<span id="deposit_avaiable">${sumPayable}</span>元
                                    </div>
                                </c:if>
                            </bcgogo:hasPermission>
                        </c:if>

                        <c:if test="${orderDebtType=='SUPPLIER_DEBT_PAYABLE'}">
                            <c:if test="<%=depositDisplay%>">
                                <div>
                                    预&nbsp;付&nbsp;款：<input type="text" id="depositAmount" name="depositAmount" autocomplete="off"/>元
                                    预付款余额：<span id="deposit_avaiable">${sumPayable}</span>元
                                </div>
                            </c:if>
                        </c:if>
                    </td>
                </tr>
                <tr>
                    <td class="left">
                        <span class="label">挂账金额：</span><span id="accountDebtAmountLabel" class="num">0</span>元
                    </td>
                    <td>
                        挂账金额：<input type="text" id="accountDebtAmount" name="accountDebtAmount" autocomplete="off"/>元&nbsp;&nbsp;
                        预计还款日期：<input type="text" id="paymentTimeStr" name="paymentTimeStr" class="tab_input" autocomplete="off" placeholder="预计还款日期"/>
                    </td>
                </tr>
                <tr>
                    <td class="left">
                        <span class="label">优惠金额：</span><span id="accountDiscountLabel" class="num">0</span>元
                    </td>
                    <td>
                        优惠金额：<input type="text" id="accountDiscount" name="accountDiscount" autocomplete="off"/>元&nbsp;
                        （按折扣算优惠价格：<input type="text" id="discount" name="discount" class="tab_input" autocomplete="off" placeholder="请输入折扣"/><span id="discountWord" style="display:none">折</span>）
                    </td>
                </tr>
                <tr>
                    <td colspan="2">
                        <div class="total">合&nbsp;&nbsp;&nbsp;&nbsp;计：<span id="totalLabel" class="num">0</span>元</div>
                        <div class="errorInfo" id="errorInfo"></div>
                    </td>
                </tr>
            </table>
        </div>

        <div class="height"></div>
        <div class="ti_btn clear">
            <input type="checkbox" value="true" id="print" name="print"
                   style=" background:none; width:15px; height:15px;margin:9px 0px 0px 250px; margin:7px 0px 0px 250px\9; "/>
            <label style="margin:9px 0px 0px 2px;margin-top:10px\9; display:block; float:left;">打印单据</label>
            <input type="checkbox" value="true" id="sendMessage" name="sendMessage"
                   style=" display:none; background:none; width:15px; height:15px;margin:9px 0px 0px 10px; margin:7px 0px 0px 10px\9; "/>
            <input type="hidden" id="smsSwitch" value="${smsSwitch}"/>
            <label id="sendMessageText"
                   style="margin:9px 0px 0px 2px;margin-top:10px\9; display:none; float:left;">发送短信</label>
            <input type="button" class="sure_tui" onclick="checkDate();" id="confirmBtn" value="结算"/>
            <input type="button" id="cancelBtn" value="取消"/>

            <div class="clear"></div>
        </div>
    </div>

</div>
<div class="clear"></div>

<div class="i_upBottom">
    <div class="i_upBottomLeft"></div>
    <div class="i_upBottomCenter"></div>
    <div class="i_upBottomRight"></div>
</div>
</div>


<div class="tab_repay" id="selectBtn"
     style="display:none;position:absolute; left:250px; top:200px; width:300px;z-index:100">
    <div class="i_arrow"></div>
    <div class="i_upLeft"></div>
    <div class="i_upCenter">
        <div class="i_note" id="div_confirm"></div>
    </div>
    <div class="i_upRight"></div>
    <div class="i_upBody">
        <div class="boxContent" style="float:none; text-align:center; color:#000">
            实收为0，是否挂账或优惠赠送？
        </div>
        <div class="sure" style="float:none; text-align:center;">
            <input type="button" value="挂账" onfocus="this.blur();" onclick="selectDebt();"/>
            <input type="button" value="赠送" onfocus="this.blur();" onclick="selectDiscount();"/>
        </div>
    </div>
    <div class="i_upBottom">
        <div class="i_upBottomLeft"></div>
        <div class="i_upBottomCenter"></div>
        <div class="i_upBottomRight"></div>
    </div>
</div>


<div class="tab_repay" id="inputMobile"
     style="display:none;position:absolute; left:250px; top:200px; width:300px;z-index:100">
    <div class="i_arrow"></div>
    <div class="i_upLeft"></div>
    <div class="i_upCenter">
        <div class="i_note" id="div_mobile">有欠款请最好填写手机号码</div>
    </div>
    <div class="i_upRight"></div>
    <div class="i_upBody">
        <div class="boxContent" style="float:none; text-align:center; color:#000">
            <input type="text" name="mobile" id="mobile">
        </div>
        <div class="sure" style="float:none; text-align:center;">
            <input type="hidden" type="hidden" id="flag" isNoticeMobile="false">
            <input type="button" value="确定" onfocus="this.blur();" onclick="inputMobile();"/>
            <input type="button" value="取消" onfocus="this.blur();" onclick="cancleInputMobile();"/>
        </div>
    </div>
    <div class="i_upBottom">
        <div class="i_upBottomLeft"></div>
        <div class="i_upBottomCenter"></div>
        <div class="i_upBottomRight"></div>
    </div>
</div>
</body>
</html>