<%--
  Created by IntelliJ IDEA.
  User: ndong
  Date: 13-3-6
  Time: 上午9:07
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>外部借调</title>

    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/borrowOrder<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/addCheck<%=ConfigController.getBuildVersion()%>.css"/>
    <%@include file="/WEB-INF/views/header_script.jsp" %>
    <script type="text/javascript" src="js/script<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.js"></script>
    <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/inventorySearchIndex<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/selectOptions<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/utils/tableUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/customerSearch<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/suggestion<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/module/invoiceCommon<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/checkMobileAndTelphone<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/messagePrompt<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/page/txn/txn<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/contact<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/supplierSearch<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/stockManager/borrowOrder<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/storeHouseDialog<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">
        defaultStorage.setItem(storageKey.MenuCurrentItem,"新增");
    </script>
</head>
<body class="bodyMain">
<%@include file="/WEB-INF/views/header_html.jsp" %>

<div class="i_main clear">
    <jsp:include page="../unit.jsp"/>
    <jsp:include page="../txnNavi.jsp">
        <jsp:param name="currPage" value="inventory"/>
    </jsp:include>
    <jsp:include page="../inventroyNavi.jsp">
        <jsp:param name="currPage" value="borrowOrder"/>
        <jsp:param name="isRepairPickingSwitchOn" value="<%=isRepairPickingSwitchOn%>"/>
    </jsp:include>
    <div class="i_mainRight" id="i_mainRight">
        <div class="cartTop"></div>
        <div class="cartBody">
            <input type="hidden" id="orderType" value="borrowOrder">
            <form:form action="borrow.do?method=saveborrowOrder" id="borrowOrderForm" commandName="borrowOrderDTO" method="post" class="J_leave_page_prompt">
            <input type="hidden" id="customerOrSupplierId" name="customerOrSupplierDTO.customerOrSupplierIdStr"/>

            <input type="hidden" id="csType" name="customerOrSupplierDTO.csType" value="customer"/>
            <input type="hidden" id="contactId" name="customerOrSupplierDTO.contactId" value=""/>
             <form:hidden path="selectSupplier" value="${WEB_VERSION_PRODUCT_THROUGH_SELECT_SUPPLIER}"/>
            <table class="elivate tabMore borrowerDetail" id="tabMore">
                <col width="100">
                <col width="110">
                <col width="84">
                <col width="90">
                <col width="75">
                <col width="90">
                <col width="94">
                <col>
                <col width="80">
                <tr>
                        <%--<c:if test="${borrowOrderDTO.receiptNo}">--%>
                        <%--<td>单据号：</td>--%>
                        <%--<td><span>${borrowOrderDTO.receiptNo}</span></td>--%>
                        <%--</c:if>--%>
                    <td class="t_title">订单号</td>
                    <td><span id="receiptNoSpan" class="receiptNoSpan">系统自动生成</span></td>
                    <td class="t_title">操作人</td>
                    <td>
                        <span>${borrowOrderDTO.operator}</span>
                    </td>
                    <td class="t_title">借调日期</td>
                    <td><input type="text" style="width: 116px" class="vestDateStr" id="orderVestDate" name="vestDateStr" value="${borrowOrderDTO.vestDateStr}" ordertype="borrowOrder" readonly="readonly" /></td>
                    <c:if test="${isHaveStoreHouse}">
                        <td class="t_title">调出仓库</td>
                        <td>
                            <form:select path="storehouseId" cssClass="j_checkStoreHouse checkSelectChanged"
                                         cssStyle="width:120px;float: left;height:21px;border: 1px solid #BBBBBB;" autocomplete="off">
                                <option value="">—请选择仓库—</option>
                                <form:options items="${storeHouseDTOs}" itemValue="id" itemLabel="name"/>
                            </form:select>
                        </td>
                    </c:if>
                    <td></td>
                </tr>
                <tr>
                    <td class="t_title">借调者类型</td>
                    <td>
                    <span>
                    <label class="rad"><input type="radio" name="borrower_type_select" borrowerType="customer" checked="checked"/>客户</label><label class="rad">
                        <input type="radio" name="borrower_type_select"  borrowerType="supplier"/>供应商</label>
                    </span>
                    </td>
                    <td class="t_title">借调者</td>
                    <td>
                        <input id="customer" type="text" class="J-customerOrSupplierSuggestion checkStringEmpty  customerOrSupplierName"
                               name="customerOrSupplierDTO.name"  style="float: left;"/>
                    </td>
                    <td class="t_title">联系人</td>
                    <td>
                        <input type="text" id="contact" name="customerOrSupplierDTO.contact" style="float: left;width: 116px"/>
                    </td>
                    <td class="t_title">手机号码</td>
                    <td>
                        <input style="width: 117px" type="text" id="mobile" maxlength="11" name="customerOrSupplierDTO.mobile"/>
                        <input id="landline" type="hidden" name="customerOrSupplierDTO.landline"/>
                        <input type="hidden" id="hiddenMobile" />

                    </td>
                    <td  style="vertical-align:text-top;"><a id="borrowerDetail" class="down blue_color">详细</a></td>
                </tr>
            </table>
            <table cellpadding="0" cellspacing="0" class="table2 tabSlip tabPick" id="table_productNo">
                <col width="90">
                <col width="90">
                <col width="90">
                <col width="90">
                <col width="90">
                <col width="90">
                <col width="90">
                <col width="75">
                <col width="60">
                <col width="50">
                <col width="60">
                <col width="70">
                <col width="70">
                <tr id="totalRowTR" class="titleBg">
                    <td style="padding-left:10px;">商品编号</td>
                    <td>品名</td>
                    <td>品牌/产地</td>
                    <td>规格</td>
                    <td>型号</td>
                    <td>车型</td>
                    <td>车辆品牌</td>
                    <td>成本均价</td>
                    <td>库存量</td>
                    <td>借调数</td>
                    <td>单位</td>
                    <td>小计</td>
                    <td>操作</td>
                </tr>
                <tr class="space">
                    <td colspan="15"></td>
                </tr>
            </table>
            <div class="tableInfo">
                <div class="t_total" style="float:left;padding-left:10px;">
                    预计还调日期：
                    <input type="text" id="returnDate" name="returnDateStr" readonly="readonly" />
                </div>
                <div class="t_total">合计：<span id="borrowTotal" class="yellow_color">0</span>元</div>
            </div>
        </div>
        <div class="danju_beizhu">
            <span>备注：</span>
            <input id="memo" class="checkStringEmpty textbox memo" type="text" maxlength="400" value="" name="memo" autocomplete="off">
                <%--<textarea class="txt textarea" name="memo" id="memo"></textarea>--%>
        </div>
        <div class="height"></div>
        <div class="shopping_btn">
            <div class="divImg">
                <img src="images/sureStorage.jpg" id="saveBorrowOrder">
                <div class="sureWords sure">确认借调</div>
            </div>
            <div class="divImg">
                <img src="images/cancel.PNG" id="toBorrowOrderList">
                <div class="sureWords">返回列表</div>
            </div>
        </div>
    </div>
</div>
<div class="height"></div>
</form:form>
</div>
</div>


<div id="id-searchcomplete"></div>
<div id="id-searchcompleteMultiselect"></div>
<div id="div_brand" class="i_scroll" style="display:none;height:230px">
    <div class="Scroller-Container" id="Scroller-Container_id"></div>
</div>
<div id="dialog-confirm" title="提醒" style="display: none">
    <p><span class="ui-icon ui-icon-alert" style="float:left; margin:0 7px 20px 0;"></span>

    <div id="dialog-confirm-text"></div>
    </p>
</div>

<iframe id="iframe_PopupBox" style="position:absolute;z-index:5; left:200px; top:400px; display:none;"
        allowtransparency="true" height="1000px" frameborder="0" src="" scrolling="no"></iframe>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
<!-- add by zhuj  name duplicate tip -->
<div class="alertMain productDetails" id="nameDupTip" style ="display:none;" >
    <div class="height"></div>
    <div id="cusDupTip">
        <div>该客户存在重名客户，请选择</div>
        <div class="height"></div>
        <label class="rad"><input type="radio" id="newCustomer"/>该客户为新客户,需填写手机或修改客户名加以区分</label><br/>
        <label class="rad" id="oldCustomer"><input type="radio"/>该客户为老客户,则请选择所需客户</label>
    </div>

    <div id="oldCustomers" style = "display:none">
        <div>请选择老客户</div>
        <div class="height"></div>
    </div>
    <div class="height"></div>
    <div class="button button_tip">
        <a class="btnSure J_btnSure">确 定</a>
        <a class="btnSure J_return" style="display: none">返回上一步</a>
    </div>
</div>

<div id="commodityCode_dialog" style="display:none">
    <span id="commodityCode_dialog_msg"></span>
</div>
<div id="storeHouseDialog" style="display:none;" class="alertMain">
    <div style="margin-top: 10px;">
        <span style="font-size: 14px;">您没有选择仓库信息！请选择仓库：</span>
        <select id="storehouseDiv"
                style="width:120px;height:21px;border: 1px solid #BBBBBB;" autocomplete="off">
            <option value="">—请选择仓库—</option>
            <c:forEach items="${storeHouseDTOs}" var="storeHouseDTO">
                <option value="${storeHouseDTO.id}">${storeHouseDTO.name}</option>
            </c:forEach>
        </select>
        <input id="btnType" type="hidden" />
    </div>
    <div class="button" style="width:100%;margin-top: 10px;">
        <a id="confirmBtn1" class="btnSure" href="javascript:;">确 定</a>
        <a id="cancleBtn" class="btnSure" href="javascript:;">取消</a>
    </div>
</div>

</body>
</html>