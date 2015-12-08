<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--预约单列表页面，取消，拒绝需要用到的dialog，需要与addCheck.css样式,appointOrderList.js配合使用  --%>
<div class="alertMain addProducts" id="refuseDialog" style="display: none;">
    <div class="height"></div>
    <table cellpadding="0" cellspacing="0" class="tab_product">
        <col width="60px">
        <col>
        <tr>
            <td valign="top">拒绝理由：</td>
            <td style="text-align:left;">
                <textarea class="txt" style=" height:100px; width:330px;" maxlength="150" id="refuseMsg"></textarea>
                <input type="hidden" id="selectedToRefuseOrderId">
            </td>
        </tr>
    </table>
    <div class="height"></div>
    <div class="button">
        <a class="btnSure" id="confirmRefuse">保&nbsp;存</a>
        <a class="btnSure" id="cancelRefuse">取&nbsp;消</a>
    </div>
</div>

<div class="alertMain addProducts" id="cancelDialog" style="display: none;">
    <div class="height"></div>
    <table cellpadding="0" cellspacing="0" class="tab_product">
        <col width="60px">
        <col>
        <tr>
            <td valign="top">取消理由：</td>
            <td style="text-align:left;">
                <textarea class="txt" style=" height:100px; width:330px;" maxlength="150" id="cancelMsg"></textarea>
                <input type="hidden" id="selectedToCancelOrderId">
            </td>
        </tr>
    </table>
    <div class="height"></div>
    <div class="button">
        <a class="btnSure" id="confirmCancel">保&nbsp;存</a>
        <a class="btnSure" id="cancelCancel">取&nbsp;消</a>
    </div>
</div>