<%@ page import="com.bcgogo.common.WebUtil" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="bcgogo" uri="http://www.bcgogo.com/taglibs/tags" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>

<%--
  Created by IntelliJ IDEA.
  User: lw
  Date: 12-7-10
  Time: 上午11:25
  To change this template use File | Settings | File Templates.
--%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>结算详细</title>
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
    <%@include file="/WEB-INF/views/header_script.jsp" %>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.min.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery.validate.min.js"></script>
    <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/salesAccount<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/settleAccounts<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">

         <bcgogo:permissionParam resourceType="render" permissions="WEB.VERSION.CUSTOMER.DEPOSIT.USE">
            APP_BCGOGO.Permission.Version.CustomerDeposit=${WEB_VERSION_CUSTOMER_DEPOSIT_USE}; <!-- add by zhuj render customer deposit -->
        </bcgogo:permissionParam>

        jQuery(document).ready(function() {

            jQuery("#huankuanTime")
                    .bind("click", function () {
                        jQuery(this).blur();
                    })
                    .bind("keydown", function() {
                        $(this).blur();
                    })
                    .datepicker({
                        "numberOfMonths":1,
                        "showButtonPanel":true,
                        "changeYear":true,
                        "changeMonth":true,
                        "yearRange":"c-100:c+100",
                        "yearSuffix":""
                    });
        });
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

        <input type="hidden" id="hiddenTotal">
        <label id="cardsTitle">应收总计：<strong id="orderTotal">0</strong>元</label>
        <bcgogo:permission>
            <bcgogo:if resourceType="logic" permissions="WEB.VERSION.MEMBER_STORED_VALUE">
                <label style="display: inline-block; margin: 4px 0px 0px; padding-left: 20px;margin: 6px 0 0;">
                    <input type="checkbox" id="memberDiscountCheck" style=" margin-bottom: 1px;margin-right: 5px;vertical-align: text-bottom;" />享受会员折扣
                    <span>：<span id="memberDiscountRatio">${memberDiscount}</span> &nbsp;折</span>
                </label>
            </bcgogo:if>
            <bcgogo:else>
                <label style="margin: 4px 0px 0px; padding-left: 20px;margin: 6px 0 0; display: none">
                    <input type="text" id="memberDiscountCheck" style=" margin-bottom: 1px;margin-right: 5px;vertical-align: text-bottom;" />享受会员折扣
                    <span>：<span id="memberDiscountRatio">${memberDiscount}</span>&nbsp;折</span>
                </label>
            </bcgogo:else>
        </bcgogo:permission>

        <div class="clear"></div>

        <div class="common_settlement">
            <table>
                <col width="130px"/>
                <col/>
                <tr>
                    <td class="left">
                        <span class="label">实收金额：</span><span id="settledAmountLabel" class="num">0</span>元
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
                            预&nbsp;收&nbsp;款：<input type="text" id="customerDeposit" name="customerDeposit" autocomplete="off"/>元 &nbsp;&nbsp;
                            可用预收：<span id="depositAvailable">${depositAvailable}</span>元
                        </div>
                        </bcgogo:hasPermission>

                        <c:if test="<%=isMemberSwitchOn%>">
                            <bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.MEMBER_STORED_VALUE">
                            <div>
                                会员储值：<input type="text" id="memberAmount" name="memberAmount" maxlength="10" autocomplete="off"/>元 &nbsp;&nbsp;
                                <input type="text" id="accountMemberNo" name="accountMemberNo" placeholder="请刷卡/输入卡号" value="${memberNo}" autocomplete="off"/>&nbsp;&nbsp;
                                <input type="password" id="accountMemberPassword" name="accountMemberPassword" placeholder="密码" autocomplete="off"/>
                                储值余额：<span id="memberBalance"><c:choose><c:when test="${memberBalance==null || memberBalance ==''}">0.0 </c:when><c:otherwise>${memberBalance}</c:otherwise></c:choose></span>元
                                <input type="hidden" id="hiddenSelfMemberNo" value="${memberNo}"/>
                            </div>
                            </bcgogo:hasPermission>
                        </c:if>
                    </td>
                </tr>
                <tr>
                    <td class="left">
                        <span class="label">挂账金额：</span><span id="accountDebtAmountLabel" class="num">0</span>元
                    </td>
                    <td>
                        挂账金额：<input type="text" id="accountDebtAmount" name="accountDebtAmount" autocomplete="off"/>元 &nbsp;&nbsp;
                        预计还款日期：<input type="text" id="huankuanTime" name="huankuanTime" class="tab_input" autocomplete="off" placeholder="预计还款日期"/>
                    </td>
                </tr>
                <tr>
                    <td class="left">
                        <span class="label">优惠金额：</span><span id="accountDiscountLabel" class="num">0</span>元
                    </td>
                    <td>
                        优惠金额：<input type="text" id="accountDiscount" autocomplete="off"/>元&nbsp;&nbsp;
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
        <div class="ti_btn clear" >
            <input type="checkbox" value="true" id="print" name="print" style=" background:none; width:15px; height:15px;margin:9px 0px 0px 250px; margin:7px 0px 0px 250px\9; "/>
            <label style="margin:9px 0px 0px 2px;margin-top:10px\9; display:block; float:left;">打印单据</label>
            <bcgogo:permission>
                <bcgogo:if resourceType="logic" permissions="WEB.VERSION.MEMBER_STORED_VALUE">
                    <input type="checkbox" value="true" id="sendMessage" name="sendMessage" style=" background:none; width:15px; height:15px;margin:9px 0px 0px 10px; margin:7px 0px 0px 10px\9;display: none "/><%-- 暂未实现会员短信 --%>
                    <label style="margin:9px 0px 0px 2px;margin-top:10px\9; display:none; float:left;">发送短信</label>
                </bcgogo:if>
                <bcgogo:else>
                    <input type="checkbox" value="true" id="sendMessage" name="sendMessage" style=" background:none; width:15px; height:15px;margin:9px 0px 0px 10px; margin:7px 0px 0px 10px\9;display: none "/>
                    <label style="margin:9px 0px 0px 2px;margin-top:10px\9; display:none; float:left;">发送短信</label>
                </bcgogo:else>
            </bcgogo:permission>

            <input type="button" class="sure_tui" onclick="checkDate();" id="confirmBtn" value="结算"/>
            <input type="button" id="cancelBtn"  value="取消"/>
            <div class="clear"></div>
        </div>
    </div>

</div>
<div class="clear"></div>


<%--<div class="tab_repay" id="selectBtn"
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
</div>--%>


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