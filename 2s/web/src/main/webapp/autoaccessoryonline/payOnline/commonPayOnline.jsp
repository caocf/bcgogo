<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="bcgogo" uri="http://www.bcgogo.com/taglibs/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<form id="payBcgogoReceivableOrderForm" method="post" target="_blank">
    <input type="hidden" id="orderReceivableAmount" value="${not empty bcgogoReceivableOrderDTO?bcgogoReceivableOrderDTO.receivableAmount:''}" autocomplete="off"/>
    <input type="hidden" id="currentPayableAmount" value="${not empty bcgogoReceivableOrderDTO?bcgogoReceivableOrderDTO.currentPayableAmount:''}" autocomplete="off"/>
    <input type="hidden" id="bcgogoReceivableOrderRecordRelationId" name="bcgogoReceivableOrderRecordRelationId" value="${not empty bcgogoReceivableOrderDTO?bcgogoReceivableOrderDTO.bcgogoReceivableOrderToBePaidRecordRelationId:''}" autocomplete="off">
    <input type="hidden" id="bcgogoReceivableOrderId" name="bcgogoReceivableOrderId" value="${not empty bcgogoReceivableOrderDTO?bcgogoReceivableOrderDTO.id:''}" autocomplete="off">

    <input type="hidden" id="instalmentPlanAlgorithmId" name="instalmentPlanAlgorithmId" value="" autocomplete="off"/>
    <input type="hidden" id="receivableMethod" name="receivableMethod" value="" autocomplete="off">
    <input type="hidden" id="paidAmount" name="paidAmount" value="" autocomplete="off">
</form>

<form id="combinedPayBcgogoReceivableOrderForm" action="bcgogoReceivable.do?method=combinedPaymentsOnlineReceivable" method="post" target="_blank">
    <input type="hidden" id="bcgogoReceivableOrderRecordRelationIds" name="bcgogoReceivableOrderRecordRelationIds" value="" autocomplete="off">
    <input type="hidden" id="paidAmounts" name="paidAmounts" value="" autocomplete="off">
</form>


<div class="i_searchBrand" id="cancelBcgogoReceivableOrderReasonDialog" title="确认取消交易提示" style="display:none; width: 400px;">
    请输入取消交易的原因：
    <form id="cancelBcgogoReceivableOrderReasonForm" method="post" action="bcgogoReceivable.do?method=cancelBcgogoReceivableOrder">
        <table border="0" width="380">
            <tr>
                <td><textarea class="textarea" id="cancelBcgogoReceivableOrderReason" name="cancelReason" style="width:320px;" maxlength="200" autocomplete="off"></textarea></td>
            </tr>
            <tr>
                <td colspan="2">
                    <div class="btnClick" style="height:50px; line-height:50px">
                        <input type="hidden" name="bcgogoReceivableOrderId" value="${not empty bcgogoReceivableOrderDTO?bcgogoReceivableOrderDTO.id:''}" autocomplete="off"/>
                        <input type="button" id="cancelBcgogoReceivableOrderReasonConfirmBtn" onfocus="this.blur();" value="确&nbsp;定">
                        <input type="button" id="cancelBcgogoReceivableOrderReasonCancelBtn" onfocus="this.blur();" value="取&nbsp;消">
                    </div>
                </td>
            </tr>
        </table>
    </form>
</div>