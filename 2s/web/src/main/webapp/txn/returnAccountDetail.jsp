<%@ page import="com.bcgogo.config.ConfigController" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
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
  <script type="text/javascript" src="js/extension/jquery/jquery-1.4.2.min.js"></script>
  <script type="text/javascript" src="js/extension/jquery/plugin/jquery-ui/ui/jquery-ui-1.8.21.custom.js"></script>
  <script type="text/javascript" src="js/extension/jquery/plugin/jquery-ui/ui/jquery.ui.timepicker-addon.js"></script>
  <script type="text/javascript"
          src="js/extension/jquery/plugin/jquery-ui/ui/i18n/jquery.ui.datepicker-zh-CN.min.js"></script>
  <script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.min.js"></script>
  <script type="text/javascript" src="js/extension/jquery/plugin/jquery.validate.min.js"></script>
  <script type="text/javascript" src="js/extension/json2/json2.js"></script>
  <script type="text/javascript" src="js/base<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/application<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/returnAccountDetail<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/settleAccounts<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/module/bcgogo-noticeDialog<%=ConfigController.getBuildVersion()%>.js"></script>

  <script type="text/javascript">
      jQuery(document).ready(function() {

          $("#div_close_pay_detail,#cancleBtnPayDetail").live("click",function(){
            closeWindow();
          });

                jQuery("#huankuanTime")
          .bind("click", function () {
            jQuery(this).blur();
          })
          .datepicker({
            "numberOfMonths":1,
            "showButtonPanel":true,
            "changeYear":true,
            "changeMonth":true,
            "yearRange":"c-100:c+100",
            "yearSuffix":"",
            "onSelect":function(dateText, inst){
                var _this = inst.input || inst.$input;
                var newValue = _this.val();
                if (G.Lang.isEmpty(newValue)) return;
                if (GLOBAL.Util.getDate(newValue).getTime() - GLOBAL.Util.getDate(GLOBAL.Date.getCurrentFormatDate()).getTime() < 0) {
                    _this.val('');
                    nsDialog.jAlert("不能选择今天之前的日期!");
                    return;
                }
            }
          });
      });

      function closeWindow()
      {
          window.parent.document.getElementById("mask").style.display = "none";
          window.parent.document.getElementById("iframe_PopupBox_account").style.display = "none";
		  self.frameElement.blur();
		  $(window.top.document.body)
			  .append($("<input type='text' id='focusBackToMe'>"))
			  .find("#focusBackToMe")
			  .focus()
			  .select()
			  .remove();
      }
  </script>

</head>
<body>
<div class="i_searchBrand i_searchBrand-account i_history">
    <div class="i_arrow"></div>
    <div class="i_upLeft"></div>
    <div class="i_upCenter">
        <div class="i_note" id="div_drag">结算详细</div>
        <div class="i_close" id="div_close_pay_detail"></div>
    </div>
    <div class="i_upRight"></div>
    <div class="i_upBody" style="border-bottom: 1px solid #DADADA;">
        <form:form modelAttribute="settlePurchaseReturnDTO" action="goodsReturn.do?method=settleForWholesaler" id="returnAccountDetailSettleForm" >
            <input type="hidden" id="purchaseReturnId" name="purchaseReturnId" value="${purchaseReturnId}" />
            <input type="hidden" id="status" name="status" value="${status}" />
            <input type="hidden" id="total" name="total" value="${total}" />
            <label id="cardsTitle">退货应收总计：<strong id="returnTotal">0</strong>元</label>
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
                            <div>现&nbsp;&nbsp;&nbsp;&nbsp;金：<input type="text" id="cash" name="cash" autocomplete="off"/>元</div>
                            <div>银&nbsp;&nbsp;&nbsp;&nbsp;联：<input type="text" id="bankAmount" name="bankAmount" autocomplete="off"/>元</div>
                            <div>
                                支&nbsp;&nbsp;&nbsp;&nbsp;票：<input type="text" id="bankCheckAmount" name="bankCheckAmount" autocomplete="off"/>元 &nbsp;&nbsp;
                                <input type="text" placeholder="支票号" maxlength="20" id="bankCheckNo" name="bankCheckNo" autocomplete="off"/>
                            </div>
                            <c:if test="${empty isRelationOrder}">
                            <div>
                                转预付款：<input type="text" id="depositAmount" name="depositAmount" autocomplete="off"/>元 &nbsp;&nbsp;
                                预付款余额：<span id="sumPayable">${sumPayable}</span>元
                            </div>
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
                <input type="checkbox" id="print" value="true" style=" background:none; width:15px; height:15px;margin:9px 0px 0px 250px; margin:7px 0px 0px 250px\9; "/>
                <label style="margin:9px 0px 0px 2px;margin-top:10px\9; display:block; float:left;">打印单据</label>

                <input type="button" class="sure_tui" name="surePayStroage" id="surePayStroage" value="结算"/>
                <input type="button" id="cancleBtnPayDetail"  value="取消"/>
                <div class="clear"></div>
            </div>
        </form:form>
    </div>
</div>
<div class="clear"></div>
</body>
</html>