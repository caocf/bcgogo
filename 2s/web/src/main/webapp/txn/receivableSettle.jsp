<%--
  Created by IntelliJ IDEA.
  User: ndong
  Date: 12-10-11
  Time: 上午3:38
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>应收结算</title>
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
    <script type="text/javascript" src="js/utils/tableUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/base<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/application<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/module/ajaxQuery<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/module/bcgogo-noticeDialog<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/module/bcgogo-scanning<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/module/bcgogo-WebStorage<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/bcgogo<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/clientInfo<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/common<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/detailsArrears<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/customerOrSupplier/receivableSettle<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/settleAccounts<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">
        <bcgogo:permissionParam resourceType="logic" permissions="WEB.VERSION.VEHICLE_CONSTRUCTION">
        APP_BCGOGO.Permission.Version.VehicleConstruction=${WEB_VERSION_VEHICLE_CONSTRUCTION};
        </bcgogo:permissionParam>
    </script>
    <%
    boolean isMemberSwitchOn = WebUtil.checkMemberStatus(request);
    %>
</head>

<body style="background:none repeat scroll 0 0 transparent">
<%@ include file="/common/messagePrompt.jsp" %>

<div id="div_show" class="i_supplierInfo_qian_kuan more_supplier_qian_kuan" style="overflow:hidden">

    <!--单据结算界面的欠款结算-->
    <input type="hidden" id="orderType" value="${orderType}" />
    <input type="hidden" id="debtMoney" value="${debtDTO.debt}" />
    <input type="hidden" id="receivableId" value="${receivableDTO.id}" />
    <input type="hidden" id="debtId" value="${debtDTO.id}" />
    <input type="hidden" id="debtVehicle" value="${debtDTO.vehicleNumber}" />
    <input type="hidden" id="debtTotalAmount" value="${debtDTO.totalAmount}" />
    <input type="hidden" id="debtOrderPayed" value="${debtDTO.settledAmount}" />
    <input type="hidden" id="debtCustomerId" value="${customerId}"/>

    <div class="i_arrow"></div>
    <div class="i_upCenter_qian_kuan" style="">
        <div class="i_note_qian_kuan" id="div_drag">应收结算</div>
        <div class="i_close" id="div_close1" style="display: block;"></div>
    </div>
    <div class="i_upBody_settle" style="height:500px;overflow-x:hidden;overflow-y:auto;">
        <div id="div_arrear" class="clear">
        <input type="hidden" id="memberNumber" name="memberNumber" value="${memberNumber}"/>
        <input type="hidden" id="memberBalance" name="memberBalance" value="${memberBalance}"/>
            <c:if test="${orderType == null || orderType == ''}">
            <div class="more_his"
                 style="color: #000;font-weight:bold;font-size:14px;margin-bottom:5px;padding-left:15px;float:left ">
                <input type="hidden" id="orderByFlagInput" orderByFlag="ASC"/>
                <input type="hidden" id="orderByFieldInput" orderByField="remindTime"/>

                <form:form commandName="recOrPayIndexDTO" id="searchReceivableForm"
                           action="arrears.do?method=getReceivablesByCustomerId" method="post" name="thisform">
                    <form:hidden path="customerOrSupplierIdStr" id="customerId"
                                 value="${recOrPayIndexDTO.customerOrSupplierIdStr}"/>

                    <table class="t_search_receivable">
                        <col width="80"/>
                        <col width="180"/>
                        <col width="100"/>
                        <col width="150"/>
                        <col width="30"/>
                        <col width="150"/>
                        <col width="60"/>
                        <tr>
                            <td>
                                <div>单据号：</div>
                            </td>
                            <td><form:input type="text" id="receiptNo" name="txnOrderId"
                                            style="margin-right: 30px;border:1px solid #7F9DB9;height:28px;"
                                            path="receiptNo"/></td>
                            <td>
                                <div>预计还款时间:</div>
                            </td>
                            <td><form:input id="startDate" name="startDate" class="txt" value="" type="text"
                                            path="startRepayDateStr"
                                            style="border:1px solid #7F9DB9;height:28px;"/>
                            </td>
                            <td>
                                <div style="float:left;padding-left:20px;">至</div>
                            </td>
                            <td><form:input id="endDate" name="endDate" value="" class="txt" type="text"
                                            style="border:1px solid #7F9DB9;height:28px;" path="endRepayDateStr"/>
                            </td>
                            <td><input type="button" id="searchReceivableBtn" class="buttonBig"
                                       onfocus="this.blur();" value="搜索" style="margin-top:2px;"/></td>
                        </tr>
                    </table>
                </form:form>
            </div>


            <table cellpadding="0" cellspacing="0" class="t_settlement clear" id="history"
                   style="margin:0px 15px; border-top:none;">
                <col width="20px"/>
                <col width="20px"/>
                <col width="60px"/>
                <col width="120px"/>
                <bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.VEHICLE_CONSTRUCTION">
                <col width="60px"/>
                </bcgogo:hasPermission>

                <col width="100px"/>
                <col width="70px"/>
                <col width="70px"/>
                <col width="70px"/>
                <col width="70px"/>
                <col width="90px"/>
                <tr class="title_settle" style="border:none;">
                    <td><input type="checkbox" id="checkAll" name="checkAll" style="margin-left:5px;"/></td>
                    <td>NO</td>
                    <td>单据号</td>
                    <td>消费时间<input id="cosumeTime" class="arrearUp" type="button" orderByFlag="ASC"
                                   orderByField="payTime" onfocus="this.blur();"></td>
                    <bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.VEHICLE_CONSTRUCTION">
                    <td>车牌号</td>
                    </bcgogo:hasPermission>
                    <td>类型</td>
                    <td>消费金额</td>
                    <td>实收金额</td>
                    <td>优惠金额</td>
                    <td>挂账金额</td>
                    <td style="border:none;">预计还款日期<input id="repayTime" class="arrearUp" orderByFlag="ASC"
                                                          orderByField="remindTime" type="button"
                                                          onfocus="this.blur();"></td>
                </tr>
            </table>


            <div class="clear"></div>
            <div class="simplePageAJAX" style="margin:10px 40px 0 0;">
                <jsp:include page="/common/pageAJAX.jsp">
                    <jsp:param name="url" value="arrears.do?method=getReceivablesByCustomerId"></jsp:param>
                    <jsp:param name="data" value="{startPageNo:1,customerOrSupplierIdStr:jQuery('#customerId').val(),orderByFlag:jQuery('#orderByFlagInput').attr('orderByFlag'),
          orderByField:jQuery('#orderByFieldInput').attr('orderByField')}"></jsp:param>
                    <jsp:param name="jsHandleJson" value="initReceivableTable"></jsp:param>
                    <jsp:param name="dynamical" value="dynamical1"></jsp:param>
                </jsp:include>
            </div>
            <span></span>

            <div class="clear"></div>
            </c:if>
            <div class="settle_detail" style="padding-left:15px;width:100%; ">
                <div class="contentTitle cont_title">
                    <a class="hover_title cont_title">应收款信息</a>
                </div>
                <div class="clear"></div>
                <label id="cardsTitle" style="display: block;float: left; line-height: 27px;">应收总计：<span
                        id="totalAmount" name="totalAmount" class="span">0</span>元</label>

                <div class="clear"></div>

                <div class="common_settlement" style="width: 98%;">
                    <table>
                        <col width="140px"/>
                        <col/>
                        <tr>
                            <td class="left">
                                <span class="label">实收金额：</span><span id="settledAmountLabel" class="num">0</span>元
                                <input type="hidden" id="payedAmount" autocomplete="off"/>
                            </td>
                            <td>
                                <div>现&nbsp;&nbsp;&nbsp;&nbsp;金：<input type="text" id="cashAmount" name="cashAmount" autocomplete="off"/>元</div>
                                <div>银&nbsp;&nbsp;&nbsp;&nbsp;联：<input type="text" id="bankAmount" name="bankAmount" autocomplete="off"/>元</div>
                                <div>
                                    支&nbsp;&nbsp;&nbsp;&nbsp;票：<input type="text" id="bankCheckAmount" name="bankCheckAmount" autocomplete="off"/>元&nbsp;&nbsp;
                                    <input type="text" placeholder="支票号" maxlength="20" id="bankCheckNo" name="bankCheckNo" autocomplete="off"/>
                                </div>
                                <c:if test="<%=isMemberSwitchOn%>">
                                    <bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.MEMBER_STORED_VALUE">
                                        <div>
                                            会员储值：<input type="text" id="memberAmount" name="memberAmount" autocomplete="off"/>元&nbsp;&nbsp;
                                            <input type="text" id="accountMemberNo" name="accountMemberNo" placeholder="请刷卡/输入卡号" data-memberNo="${memberNo}" value="${memberNo}" autocomplete="off"/>
                                            <input type="password" id="accountMemberPassword" name="accountMemberPassword" placeholder="密码" autocomplete="off"/>
                                            储值余额：<span id="memberBalanceNum">${memberBalance}</span>
                                        </div>
                                    </bcgogo:hasPermission>
                                </c:if>
                                <bcgogo:hasPermission permissions="WEB.VERSION.CUSTOMER.DEPOSIT.USE">
                                    <div>
                                        预&nbsp;收&nbsp;款：<input id="depositAmount" name="depositAmount" type="text" autocomplete="off"/>元&nbsp;&nbsp;
                                        可用预收：<span id="depositAvailable">${depositAvailable == null ? '0.0': depositAvailable}</span> 元
                                    </div>
                                </bcgogo:hasPermission>
                            </td>
                        </tr>
                        <tr>
                            <td class="left">
                                <span class="label">挂账金额：</span><span id="accountDebtAmountLabel" class="num">0</span>元
                            </td>
                            <td>
                                挂账金额：<input type="text" id="owedAmount" name="owedAmount" autocomplete="off"/>元&nbsp;&nbsp;
                                预计还款日期：<input type="text" id="huankuanTime" name="huankuanTime" class="tab_input" autocomplete="off" placeholder="预计还款日期"/>
                            </td>
                        </tr>
                        <tr>
                            <td class="left">
                                <span class="label">优惠金额：</span><span id="accountDiscountLabel" class="num">0</span>元
                            </td>
                            <td>
                                优惠金额：<input type="text" id="discountAmount" name="discountAmount" autocomplete="off"/>元&nbsp;
                                （按折扣算优惠价格：<input type="text" id="discount" name="discount" class="tab_input"style="width:100px;" autocomplete="off" placeholder="请输入折扣"/><span id="discountWord" style="display:none">折</span>）
                            </td>
                        </tr>
                        <tr>
                            <td colspan="2">
                                <div class="total" style="font-size:12px;">合&nbsp;&nbsp;&nbsp;&nbsp;计：<span id="totalLabel" class="num">0</span>元</div>
                                <div class="errorInfo" id="errorInfo"></div>
                            </td>
                        </tr>
                    </table>
                </div>

                <div class="height"></div>
                <div class="ti_btn clear">
                    <input id="receivalePrintBtn" type="checkbox"
                           style=" background:none; width:15px; height:15px;margin:9px 0px 0px 300px; margin:5px 0px 0px 300px\9; "/>
                    <label style="margin:9px 0px 0px 2px;margin-top:8px\9">打印单据</label>
                    <input id="btnSettle" type="button" class="buttonBig sure_tui" value="结算"/>
                    <input id="cancelButton" type="button" class="buttonBig" value="取消"/>

                    <div class="clear"></div>
                </div>
            </div>
        </div>
        <div class="height"></div>
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
        allowtransparency="true" width="1150px" height="800px" frameborder="0" src=""></iframe>

<div id="systemDialog"></div>


</body>
</html>

