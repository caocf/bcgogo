<%@ page import="com.bcgogo.common.WebUtil" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="bcgogo" uri="http://www.bcgogo.com/taglibs/tags" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>结算详细</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <link rel="stylesheet" type="text/css" href="styles/up1<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/moneyDetail<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="js/extension/jquery/plugin/jquery-ui/themes/redmond/jquery-ui-1.8.21.custom.css"/>
    <link rel="stylesheet" type="text/css" href="js/extension/jquery/plugin/jquery-ui/themes/redmond/jquery.ui.timepicker-addon.css"/>
    <link rel="stylesheet" type="text/css" href="styles/setleNew<%=ConfigController.getBuildVersion()%>.css"/>

    <style>
        #ui-datepicker-div, .ui-datepicker {
            font-size: 90%;
        }
    </style>
    <script type="text/javascript" src="js/extension/json2/json2.js"></script>
    <script type="text/javascript" src="js/extension/jquery/jquery-1.4.2.min.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery-ui/ui/jquery-ui-1.8.21.custom.min.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery-ui/ui/jquery.ui.timepicker-addon.min.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery-ui/ui/i18n/jquery.ui.datepicker-zh-CN.min.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.min.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery.validate.min.js"></script>
    <script type="text/javascript" src="js/base<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/application<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/module/bcgogo-noticeDialog<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/member<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/accountDetailSalesReturn<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/settleAccounts<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">
        <bcgogo:permissionParam resourceType="render" permissions="WEB.VERSION.CUSTOMER.DEPOSIT.USE">
        APP_BCGOGO.Permission.Version.CustomerDeposit =${WEB_VERSION_CUSTOMER_DEPOSIT_USE};
        </bcgogo:permissionParam>
    </script>
    <%
        boolean isMemberSwitchOn = WebUtil.checkMemberStatus(request);
    %>
</head>
<body>
<%@ include file="/common/messagePrompt.jsp" %>
<input type="hidden" id="isMemberSwitchOn" value="<%=isMemberSwitchOn%>">

<div class="i_searchBrand i_searchBrand-account i_history">
    <div class="i_arrow"></div>
    <div class="i_upLeft"></div>
    <div class="i_upCenter">
        <div class="i_note" id="div_drag">结算详细</div>
        <div class="i_close" id="div_close"></div>
    </div>
    <div class="i_upRight"></div>
    <div class="i_upBody">
        <form:form modelAttribute="settleSalesReturnDTO" action="salesReturn.do?method=settleForWholesaler" id="salesReturnAccountDetailSettleForm" >
            <input type="hidden" value="${salesReturnDTO == null?"":salesReturnDTO.id}" name="salesReturnOrderId"/>
            <input type="hidden" id="settleType" value="${settleType == null?"":settleType}"/>
            <label id="cardsTitle">应付总计：<strong id="orderTotal">${salesReturnDTO==null?"":salesReturnDTO.total}</strong>元</label>
            <div id="orderStatus" style="display:none;">${salesReturnDTO==null?"":salesReturnDTO.status}</div>
            <div class="clear"></div>
            <input type="hidden" id="strikeAmount" name="strikeAmount" value="0"/>

            <div class="common_settlement">
                <table>
                    <col width="130px"/>
                    <col/>
                    <tr>
                        <td class="left">
                            <span class="label">实付金额：</span><span id="settledAmountLabel" class="num">0</span>元
                            <input type="hidden" id="settledAmount" autocomplete="off"/>
                        </td>
                        <td>
                            <div>现&nbsp;&nbsp;&nbsp;&nbsp;金：<input type="text" id="cashAmount" name="cashAmount" autocomplete="off"/>元</div>
                            <div>银&nbsp;&nbsp;&nbsp;&nbsp;联：<input type="text" id="bankAmount" name="bankAmount" autocomplete="off"/>元</div>
                            <div>
                                支&nbsp;&nbsp;&nbsp;&nbsp;票：<input type="text" id="bankCheckAmount" name="bankCheckAmount" autocomplete="off"/>元 &nbsp;&nbsp;
                                <input type="text" placeholder="支票号" maxlength="20" id="bankCheckNo" name="bankCheckNo" autocomplete="off"/>
                            </div>
                            <bcgogo:hasPermission permissions="WEB.VERSION.CUSTOMER.DEPOSIT.USE">
                            <div>
                                转预收款：<input type="text" id="customerDeposit" name="customerDeposit" autocomplete="off"/>元 &nbsp;&nbsp;
                                预收款：<span id="deposit_avaiable" value="${customerDepositAvailable}">${customerDepositAvailable}</span>元
                            </div>
                            </bcgogo:hasPermission>
                        </td>
                    </tr>
                    <tr>
                        <td class="left">
                            <span class="label">挂账金额：</span><span id="accountDebtAmountLabel" class="num">0</span>元
                        </td>
                        <td>
                            挂账金额：<input type="text" id="accountDebtAmount" name="accountDebtAmount" autocomplete="off"/>元 &nbsp;&nbsp;
                        </td>
                    </tr>
                    <tr>
                        <td class="left">
                            <span class="label">优惠金额：</span><span id="accountDiscountLabel" class="num">0</span>元
                        </td>
                        <td>
                            优惠金额：<input type="text" id="discountAmount" name="discountAmount" autocomplete="off"/>元&nbsp;&nbsp;
                            （按折扣算优惠价格：<input type="text" id="discount" name="discount" class="tab_input"style="width:100px;" autocomplete="off" placeholder="请输入折扣"/><span id="discountWord" style="display:none">折</span>）
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
            <input type="checkbox" value="true" id="print" name="print" style=" background:none; width:15px; height:15px;margin:9px 0px 0px 250px; margin:7px 0px 0px 250px; "/>
            <label style="margin:9px 0px 0px 2px;margin-top:10px; display:block; float:left;">打印单据</label>
            <input type="button" class="sure_tui" onclick="checkDate();" id="confirmBtn" value="结算"/>
            <input type="button" id="cancelBtn" value="取消"/>
            <div class="clear"></div>
        </div>
        </form:form>
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

</body>
</html>