<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--<div class="i_searchBrand" style="width:842px">--%>
    <%--<div class="i_arrow"></div>--%>
    <%--<div class="i_upLeft"></div>--%>
    <%--<div class="i_upCenter">--%>
        <%--<div class="i_note" id="div_drag">预付款充值</div>--%>
        <%--<div class="i_close" id="div_close"></div>--%>
    <%--</div>--%>
    <%--<div class="i_upRight"></div>--%>
    <%--<div class="i_upBody">--%>
        <%--<div class="moneyTotal">--%>
            <%--<div class="total">余额：<span class="span" id="balance"></span>元</div>--%>
            <%--<div class="total"></div>--%>
        <%--</div>--%>
        <%--<div class="clear height"></div>--%>
        <%--<table cellpadding="0" cellspacing="0" class="tabTotal tabDetail">--%>
            <%--<col width="500"/>--%>
            <%--<col/>--%>
            <%--<tr>--%>
                <%--<td>--%>
                    <%--现&nbsp;&nbsp;&nbsp;&nbsp;金：--%>
                    <%--<input name="cashDeposit" id="cashDeposit" type="text"--%>
                           <%--style="width:120px;" value=""/>--%>
                <%--</td>--%>
                <%--<td></td>--%>
            <%--</tr>--%>

            <%--<tr>--%>
                <%--<td>--%>
                    <%--银 行 卡：--%>
                    <%--<input name="bankCardAmountDeposit" id="bankCardAmountDeposit" type="text"--%>
                           <%--style="width:120px;" value=""/>--%>
                <%--</td>--%>
                <%--<td></td>--%>
            <%--</tr>--%>
            <%--<tr>--%>
                <%--<td>--%>
                    <%--支&nbsp;&nbsp;&nbsp;&nbsp;票：--%>
                    <%--<input name="checkAmountDeposit" id="checkAmountDeposit" type="text"--%>
                           <%--style="width:120px;" value=""/>--%>

                    <%--&lt;%&ndash;<br/>&ndash;%&gt;--%>
                    <%--&nbsp;&nbsp;&nbsp;&nbsp;--%>
                    <%--支票号码：--%>
                    <%--&lt;%&ndash;<br/>&ndash;%&gt;--%>
                    <%--<input name="checkNoDeposit" id="checkNoDeposit" type="text"--%>
                           <%--style="width:120px;" value=""/>--%>

                <%--</td>--%>
                <%--<td style="font-weight:bold; font-size:14px; text-align:right; padding-right:10px;">--%>
                    <%--实&nbsp;&nbsp;付：--%>
                    <%--<input name="actuallyPaidDeposit" id="actuallyPaidDeposit" type="text"--%>
                           <%--style="width:120px;" value=""/>--%>
                <%--</td>--%>
            <%--</tr>--%>
        <%--</table>--%>

        <%--<div class="clear height"></div>--%>
        <%--<div class="btnDiv">--%>
            <%--<div style="display: inline;"><input type="checkbox" id="print" name="print"/><label for="print">打印</label>--%>
            <%--</div>--%>
            <%--<div class="btnInput" style="display: inline;">--%>
                <%--<input id="sureBtn" type="button" value="确认" onfocus="this.blur();"/>--%>
                <%--<input id="cancleBtn" type="button" value="取消" onfocus="this.blur();"/>--%>
            <%--</div>--%>
        <%--</div>--%>
    <%--</div>--%>

    <%--<div class="i_upBottom">--%>
        <%--<div class="i_upBottomLeft"></div>--%>
        <%--<div class="i_upBottomCenter"></div>--%>
        <%--<div class="i_upBottomRight"></div>--%>
    <%--</div>--%>
<%--</div>--%>
<div class="alertMain productDetails" style="color:#000000;">
    <div class="alert_title">
        <div class="left"></div>
        <div class="body">预付款充值</div>
        <div class="right"></div>
    </div>
    <div class="height"></div>
    <div class="divTit alert_divTit">现金&nbsp;<input type="text" class="txt" name="cashDeposit" id="cashDeposit" />&nbsp;元</div>
    <div class="divTit alert_divTit">银联&nbsp;<input type="text" class="txt" name="bankCardAmountDeposit" id="bankCardAmountDeposit" />&nbsp;元</div>
    <div class="divTit alert_divTit">支票&nbsp;<input type="text" class="txt" name="checkAmountDeposit" id="checkAmountDeposit" />&nbsp;元&nbsp;<input type="text" initValue="支票号" value="支票号" class="txt"  name="checkNoDeposit" id="checkNoDeposit" style="color:#9a9a9a;"/></div>
    <div class="height"></div>
    <div class="divTit"><span class="span_title">备注</span>&nbsp;<textarea style="width:280px;" name="memo" id="depositMemo" maxlength="250"></textarea></div>
    <div class="divTit alert_divTit"><b>当前余额：<span id="balance">0</span>元</b></div>
    <div class="divTit alert_divTit"><b>实付金额：<span id="actuallyPaidDeposit">0</span>元</b></div>
    <div class="button"><label><input type="checkbox" id="print" name="print"/>打印</label>&nbsp;&nbsp;<a class="btnSure" id="sureBtn" href="javascript:;">确&nbsp;定</a><a class="btnSure" id="cancleBtn" href="javascript:;">取&nbsp;消</a></div>

</div>