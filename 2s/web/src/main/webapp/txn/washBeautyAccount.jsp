<%--
  Created by IntelliJ IDEA.
  User: zhoudongming
  Date: 12-7-24
  Time: 上午10:29
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>结算详细</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <link rel="stylesheet" type="text/css" href="styles/up<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/moneyDetail<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="js/extension/jquery/plugin/jquery-ui/themes/redmond/jquery-ui-1.8.21.custom.css"/>
    <link rel="stylesheet" type="text/css" href="js/extension/jquery/plugin/jquery-ui/themes/redmond/jquery.ui.timepicker-addon.css"/>
    <style type="css/text">
        #ui-datepicker-div, .ui-datepicker {
            font-size: 90%;
        }
    </style>

    <script type="text/javascript" src="js/extension/jquery/jquery-1.4.2.min.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery.validate.min.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.min.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery-ui/ui/jquery-ui-1.8.21.custom.min.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery-ui/ui/jquery.ui.timepicker-addon.min.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery-ui/ui/i18n/jquery.ui.datepicker-zh-CN.min.js"></script>
    <script type="text/javascript" src="js/base<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/application<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/member<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/script<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/selectOptions<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/washBeautyAccount<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">
        $(document).ready(function() {

            $("#div_close,#cancelBtn").click(function() {
                window.parent.document.getElementById("mask").style.display = "none";
                window.parent.document.getElementById("iframe_PopupBox_account").style.display = "none";
                window.parent.document.getElementById("iframe_PopupBox_account").src = "";

                try {
                    $(window.parent.document.body).find("input[type='button']").eq(0).focus().blur();
                } catch(e) {
                    ;
                }

				self.frameElement.blur();
				$(window.top.document.body)
					.append($("<input type='text' id='focusBackToMe'>"))
					.find("#focusBackToMe")
					.focus()
					.select()
					.remove();
            });
            if ($("#accountMemberNo")) {
                $("#accountMemberNo").focus();
            }
            var orderTotal = window.parent.document.getElementById("total").value;

            $("#orderTotal").text(orderTotal);

            if (!$("#id", window.parent.document).val()) {
                var cashAmount = window.parent.document.getElementById("cashAmount").value;
                $("#cashAmount").val(orderTotal);
                $("#settledAmount").val(orderTotal);
            }

            if ($("#orderTotal").text() == 0) {
                $("#accountDiscount,#settledAmount,#accountDebtAmount,#cashAmount,#bankAmount,#bankCheckAmount,#memberAmount,#bankCheckNo,#huankuanTime").attr("disabled", "true");
            }
            // init date picker
            $("#huankuanTime")
                    .bind("click", function () {
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
<form action="" id="washBeautyAccountForm" method="post">
    <input type="hidden" value="<%=isMemberSwitchOn%>" id="isMemberSwitchOn"/>

    <div class="i_searchBrand i_searchBrand-washBeautyAccount">
        <div class="i_arrow"></div>
        <div class="i_upLeft"></div>
        <div class="i_upCenter">
            <div class="i_note" id="div_drag">结算详细</div>
            <div class="i_close" id="div_close" onclick="closeWinDiv()"></div>
        </div>
        <div class="i_upRight"></div>
        <div class="i_upBody">
            <div class="moneyTotal">
                <div class="total">总计：<span id="orderTotal" class="span">0</span>元</div>
            </div>
            <div class="clear height"></div>

            <div id="moneyTotal" class="moneyTotal">
                <div class="total">优&nbsp;&nbsp;惠：<input id="accountDiscount" type="text" style="width:60px;"/></div>
                <div class="total">欠款挂账：<input id="accountDebtAmount" type="text" style="width:60px;"/></div>
                <div class="total">还款日期：<input type="text" id="huankuanTime" class="tab_input" style="width:84px;"/>
                </div>
            </div>
            <div class="clear height"></div>


            <table cellpadding="0" cellspacing="0" class="tabTotal">
                <col width="220"/>
                <col/>
                <tr>
                    <td>现&nbsp;&nbsp;&nbsp;&nbsp;金：<input id="cashAmount" type="text" style="width:120px;"/></td>
                </tr>
                <tr>
                    <td>银&nbsp;行&nbsp;卡：<input id="bankAmount" type="text" style="width:120px;"/></td>
                </tr>
                <tr>
                    <td>
                        支&nbsp;&nbsp;&nbsp;&nbsp;票：<input id="bankCheckAmount" type="text" style="width:120px;"/>

                        <div class="divNum">
                            号&nbsp;&nbsp;&nbsp;码：<input class="tab_input" id="bankCheckNo" type="text"
                                                        style="width:101px;">
                        </div>
                    </td>
                    <td style="font-weight:bold; font-size:14px; text-align:right; padding-right:10px;">
                        实&nbsp;&nbsp;收：<input
                            id="settledAmount"
                            type="text"
                            style="width:120px;"/>
                    </td>
                </tr>
                <c:if test="<%=isMemberSwitchOn%>">
                    <tr>
                        <td>会员储值：<input id="memberAmount" type="text" style="width:120px;"/></td>
                        <td>可用储值：<span id="memberBalance"></span></td>
                    </tr>
                </c:if>

            </table>
            <div class="clear height"></div>
            <c:if test="<%=isMemberSwitchOn%>">
                <div style="padding-left:0px;">
                    <div style="float:left;padding-right:0px;">请刷卡/输入卡号：<input id="accountMemberNo" maxlength="25"
                                                                               type="text"
                                                                               style="width:100px;"/>
                    </div>
                    密&nbsp;&nbsp;码：<input id="accountMemberPassword" type="password" style="width:100px;"/>
                </div>
            </c:if>
            <div class="clear height"></div>
            <div class="btnInput">
                <span class="span">*双击更改付款方式</span>
                <label style="padding-right:10px;" class=chkPrint>
                    <input type="checkbox" value="true" id="print" name="print"/>打印</label>
                <label style="padding-right:10px;" class=chkPrint>
                    <input type="checkbox" value="true" id="sendMessage" name="sendMessage"
                           checked="checked"/>短信</label>
                <input id="confirmBtn" onclick="checkDate();" type="button" value="结算" onfocus="this.blur();"/>
                <input type="button" id="cancelBtn" value="取消" onfocus="this.blur();"/>
            </div>
        </div>
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
                <input type="text" name="mobile" id="mobile" value="${customerDTO.mobile}">
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
</form>


</body>
</html>