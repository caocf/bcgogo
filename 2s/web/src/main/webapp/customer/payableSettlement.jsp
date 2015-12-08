<%--
  Created by IntelliJ IDEA.
  User: ndong
  Date: 12-10-11
  Time: 上午3:38
  To change this template use File | Settings | File Templates.
--%>
<%@ include file="/WEB-INF/views/includes.jsp" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>应付结算</title>
    <link rel="stylesheet" type="text/css" href="styles/up<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/qianKuan<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/moreUserinfo<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/moreHistory<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/moneyDetail<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="js/extension/jquery/plugin/jquery-ui/themes/redmond/jquery-ui-1.8.21.custom.css"/>
    <link rel="stylesheet" type="text/css" href="js/extension/jquery/plugin/jquery-ui/themes/redmond/jquery.ui.timepicker-addon.css"/>
	<link rel="stylesheet" type="text/css" href="js/components/themes/bcgogo-scanning<%=ConfigController.getBuildVersion()%>.css" />
    <script type="text/javascript" src="js/extension/jquery/jquery-1.4.2.min.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery-ui/ui/jquery-ui-1.8.21.custom.min.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery-ui/ui/jquery.ui.timepicker-addon.min.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery-ui/ui/i18n/jquery.ui.datepicker-zh-CN.min.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.min.js"></script>
    <script type="text/javascript" src="js/utils/tableUtil.js"></script>
    <script type="text/javascript" src="js/base<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/application<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/module/bcgogo-noticeDialog<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/module/bcgogo-scanning<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/module/bcgogo-WebStorage<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/bcgogo<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/module/ajaxQuery<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/common<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/detailsArrears<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/customerOrSupplier/payableSettlement<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/settleAccounts<%=ConfigController.getBuildVersion()%>.js"></script>

</head>

<body style="background:none repeat scroll 0 0 transparent">
<%@ include file="/common/messagePrompt.jsp" %>
<div id="div_show" class="i_supplierInfo_qian_kuan more_supplier_qian_kuan" style="overflow:hidden">
    <input type="hidden" id="supplierId" value="${recOrPayIndexDTO.customerOrSupplierIdStr}"/>
    <input type="hidden" id="orderType" value="${orderType}" />
    <input type="hidden" id="debtMoney" value="${payableDTO.creditAmount}" />
    <input type="hidden" id="payableId" value="${payableDTO.id}" />
    <div class="i_arrow"></div>
    <div class="i_upCenter_qian_kuan">
        <div class="i_note_qian_kuan" id="div_drag">应付结算</div>
        <div class="i_close" id="div_close" style="display: block;"></div>
    </div>
    <div class="height"></div>
    <div class="i_upBody_settle">
        <div id="div_arrear" class="clear div_arrear">
            <c:if test="${orderType != 'purchaseInventoryOrder'}">
            <div id="orderDebtHidden">
                <div class="more_his"
                     style="color: #000;font-weight:bold;font-size:14px;margin-bottom:5px;padding-left:15px;float:left ">
                    <!-- TODO 增加大小值的判断 -->
                    <form:form commandName="recOrPayIndexDTO" id="searchPayableForm"
                               action="arrears.do?method=getPayablesBySupplierId" method="post">
                        <form:hidden path="customerOrSupplierIdStr" id="customerOrSupplierIdStr"
                                     value="${recOrPayIndexDTO.customerOrSupplierIdStr}"/>
                        <div style="float:left;">单据号：</div>
                        <div class="selectTime" style="margin:0px 10px 0px 0px;">
                            <form:input type="text" id="receiptNoId" style="width:90%; margin:0px 10px 0px 0px;"
                                        path="receiptNo"/>
                        </div>
                        <div style="float:left;  margin-left:40px;">入库时间:</div>
                        <div class="selectTime">
                            <form:input id="startDate" name="startDate" class="txt" value="" type="text" path="startDateStr"
                                        style="border:1px solid #7F9DB9;line-height:28px;"/>
                        </div>
                        <div style="float:left; padding-left:20px;">至</div>
                        <div class="selectTime">
                            <form:input id="endDate" name="endDate" value="" class="txt" type="text"
                                        style="border:1px solid #7F9DB9;line-height:28px;" path="endDateStr"/>
                        </div>
                        <input type="button" id="searchPayableBtn" class="buttonBig" onfocus="this.blur();"
                               value="搜索" style="margin-left:6px;"/>
                    </form:form>
                </div>
                <table cellpadding="0" cellspacing="0" class="t_settlement clear" id="history"
                       style="margin:0px 15px; border-top: medium none;">
                    <col width="20px"/>
                    <col width="20px"/>
                    <col width="100px"/>
                    <col width="120px"/>
                    <col width="60px"/>
                    <col width="70px"/>
                    <col width="70px"/>
                    <col width="70px"/>
                    <tr class="title_settle" style="border:none;">
                        <td><input type="checkbox" id="checkAll" name="checkAll" style="margin-left:5px;"/></td>
                        <td>NO</td>
                        <td>单据号</td>
                        <td>入库时间</td>
                        <td>材料</td>
                        <td>单据金额</td>
                        <td>已付金额</td>
                        <td style="border:none;">挂账金额</td>
                    </tr>
                </table>
                <div class="clear"></div>
                <div class="simplePageAJAX" style="margin:10px 40px 0 0;">
                    <jsp:include page="/common/pageAJAX.jsp">
                        <jsp:param name="url" value="arrears.do?method=getPayablesBySupplierId"></jsp:param>
                        <jsp:param name="data"
                                   value="{startPageNo:1,customerOrSupplierIdStr:jQuery('#supplierId').val()}"></jsp:param>
                        <jsp:param name="jsHandleJson" value="initPayableTable"></jsp:param>
                        <jsp:param name="dynamical" value="dynamical1"></jsp:param>
                    </jsp:include>
                </div>


                <div class="clear"></div>
            </div>
            </c:if>
            <div class="settle_detail" style="padding-left:15px;width:100%; ">
                <div class="contentTitle cont_title">
                    <a class="hover_title">应付款信息</a>
                </div>
                <label id="cardsTitle">应付总计：<span id="totalCreditAmount" name="totalCreditAmount" class="span">0.0</span>元</label>

                <div class="clear"></div>

                <div class="common_settlement" style="width:98%;">
                    <table>
                        <col width="140px"/>
                        <col/>
                        <tr>
                            <td class="left">
                                <span class="label">实付金额：</span><span id="settledAmountLabel" class="num">0</span>元
                                <input type="hidden" id="actuallyPaid" autocomplete="off"/>
                            </td>
                            <td>
                                <div>现&nbsp;&nbsp;&nbsp;&nbsp;金：<input type="text" id="cash" name="cash" autocomplete="off"/>元</div>
                                <div>银&nbsp;&nbsp;&nbsp;&nbsp;联：<input type="text" id="bankCardAmount" name="bankCardAmount" autocomplete="off"/>元</div>
                                <div>
                                    支&nbsp;&nbsp;&nbsp;&nbsp;票：<input type="text" id="checkAmount" name="checkAmount" autocomplete="off"/>元&nbsp;&nbsp;
                                    <input type="text" maxlength="20" id="checkNo" name="checkNo" placeholder="支票号" autocomplete="off"/>
                                </div>
                                <div>
                                    预&nbsp;付&nbsp;款：<input id="depositAmount" name="depositAmount" type="text"/>元&nbsp;&nbsp;
                                    可用预付款：<span id="deposit_avaiable">${depositAvaiable}</span>元
                                </div>
                            </td>
                        </tr>
                        <tr>
                            <td class="left">
                                <span class="label">挂账金额：</span><span id="accountDebtAmountLabel" class="num">0</span>元
                            </td>
                            <td>
                                挂账金额：<input type="text" id="creditAmount" name="creditAmount" autocomplete="off"/>元
                            </td>
                        </tr>
                        <tr>
                            <td class="left">
                                <span class="label">优惠金额：</span><span id="accountDiscountLabel" class="num">0</span>元
                            </td>
                            <td>
                                优惠金额：<input type="text" id="deduction" name="deduction" autocomplete="off"/>元&nbsp;
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
                    <input id="checkDetailPrint" type="checkbox"
                           style=" background:none; width:15px; height:15px;margin:9px 0px 0px 300px; margin:5px 0px 0px 300px\9; "/>
                    <label style="margin:9px 0px 0px 2px;margin-top:8px\9">打印单据</label>
                    <input id="surePay" type="button" class="buttonBig sure_tui" value="结算"/>
                    <input id="cancelButton" type="button" class="buttonBig" value="取消"/>

                    <div class="clear"></div>
                </div>


            </div>


        </div>
    </div>
</div>

<div id="mask" style="display:block;position: absolute;">
</div>
<iframe id="iframe_PopupBoxMakeTime" style="position:absolute;z-index:10;display:none; " allowtransparency="true"
        width="350px" height="500px" frameborder="0" src="" scrolling="no"></iframe>
<iframe name="iframe_PopupBox" id="iframe_PopupBox"
        style="position:absolute;z-index:6;top:210px;left:87px;display:none; " allowtransparency="true" width="1000px"
        height="800px" frameborder="0" src=""></iframe>
<iframe id="iframe_PopupBox_1" style="position:absolute;z-index:6;top:210px;left:87px;display:none; "
        allowtransparency="true" width="1000px" height="800px" frameborder="0" src=""></iframe>


</body>
</html>

