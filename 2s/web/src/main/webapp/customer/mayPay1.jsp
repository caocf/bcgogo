<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div class="i_history">
    <div class="i_arrow"></div>
    <div class="i_upLeft"></div>
    <div class="i_upCenter">
        <div class="i_note" id="div_drag" style="width:740px;">应付结算</div>
        <div class="i_close" id="div_close"></div>
    </div>
    <div class="i_upRight"></div>
    <div class="i_upBody">
        <div id="div_arrear" class="clear">
            <div class="more_his">
                <div style="float:left; line-height:25px;">共有<span class="hover" id="totalCountPayable">3</span>条历史记录
                </div>
                <div class="selectTime">
                    <input id="fromTime" type="text" readonly="true" name="fromTime" />
                </div>
                <div style="float:left; line-height:25px; padding-left:20px;">至</div>
                <div class="selectTime">
                    <input id="toTime" type="text" readonly="true" name="toTime" />
                </div>
                <input type="button" class="btnSearch" id="btnSearch" onfocus="this.blur();" value="搜索"/>
            </div>
            <table cellpadding="0" cellspacing="0" class="table2" id="payable_table">
                <col width="10"/>
                <col width="50"/>
                <col width="120"/>
                <col width="60"/>
                <col width="98"/>
                <col width="90"/>
                <col width="90"/>
                <col width="90"/>
                <col width="90"/>
                <col width="90"/>
                <tr class="title_his">
                    <td style="border-left:none;"></td>
                    <td>NO</td>
                    <td>消费时间<input type="button" class="arrearDown" onfocus="this.blur();" id="arrear" name="pay_time"/></td>
                    <td>单据号码</td>
                    <td>材料/品名</td>
                    <td class="txt_right">单据金额</td>
                    <td class="txt_right">已付金额</td>
                    <td style="border-right:none;" class="txt_right">挂账金额</td>
                    <td style="border-right:none;" class="txt_right">折扣金额</td>
                    <td style="border-right:none;" class="txt_right">状态</td>
                </tr>
            </table>
            <div class="clear"></div>
            <div id="checkAllLabel" style="margin-top:7px; margin-left:6px;">
                <input type="checkbox" name="checkAll" id="checkAll"/>
                <label for="checkAll"><font color="#000000">全选</font></label>
            </div>
            <!--分页-->
            <div class="simplePageAJAX">
                <jsp:include page="/common/pageAJAX.jsp">
                    <jsp:param name="url" value="payable.do?method=searchPayable"></jsp:param>
                    <jsp:param name="data" value="{startPageNo:1,supplierId:jQuery('#supplierId').val()}"></jsp:param>
                    <jsp:param name="jsHandleJson" value="initPayableTable"></jsp:param>
                    <jsp:param name="dynamical" value="dynamical3"></jsp:param>
                </jsp:include>
            </div>
            <!--分页结束-->
            <!--结算-->
            <div class="tableInfo3">
                <div class="total">
                    <div>总计：<span id="totalCreditAmount">0</span>元</div>
                    <div>
                        <select class="jiesuan" id="selMoney">
                            <option>现金</option>
                        </select>
                    </div>
                    <div>实付：<input id="actually_paid" type="text" style="width:40px;"/>元</div>
                    <div>挂账：<input id="credit_amount" type="text" style="width:40px;"/>元</div>
                    <div>扣款：<input id="supplier_deduction" type="text" style="width:40px;"/>元</div>
                </div>
            </div>
            <div class="clear"></div>
            <div class="tableInfo4">
                <label style="margin:30px 0px 0px 30px;float:left;color: #000000;"><input id="checkPrint" type="checkbox" checked="checked"/>打印</label>
                <div class="table_btn">
                    <input id="cancleBtn" type="button" class="i_operate" value="取消" onfocus="this.blur();"/>
                    <!--<input id="payablePrintBtn" type="button" class="i_operate" value="打印" onfocus="this.blur();"/>-->
                    <input id="settleAccounts" type="button" class="i_operate" value="结算" onfocus="this.blur();"/>
                </div>
            </div>
        </div>
    </div>
    <div class="i_upBottom">
        <div class="i_upBottomLeft"></div>
        <div class="i_upBottomCenter"></div>
        <div class="i_upBottomRight"></div>
    </div>
</div>

<div id="mask" style="display:block;position: absolute;z-index: 9;">
</div>
<!-- 结算详细-->
<div id="payDetail" style="position: fixed; left:37%; top: 37%; z-index: 10; display: none;">
    <jsp:include page="payDetail.jsp"></jsp:include>
</div>
