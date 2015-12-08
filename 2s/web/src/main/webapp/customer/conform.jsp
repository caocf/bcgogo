<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div class="i_searchBrand">
    <div class="i_arrow"></div>
    <div class="i_upLeft"></div>
    <div class="i_upCenter">
        <div class="i_note" id="div_drag">挂账/扣款免付</div>
        <div class="i_close" id="cancleCreditDeducationBtnDiv_close"></div>
    </div>
    <div class="i_upRight"></div>
    <div class="i_upBody">
         <div class="moneyTotal">
            <div class="total"><span class="span">实付为0，是否挂账或扣款免付？</span></div>
        </div>
        <div class="clear height"></div>
            <div class="clear height"></div>
            <div class="btnInput">
                 <input id="type" type="hidden" value=""  />
                <input id="creditAmountBtn" type="button" value="挂账" onfocus="this.blur();" />
                <input id="deductionBtn" type="button" value="扣款免付" onfocus="this.blur();"/>
                <input id="cancleCreditDeducationBtn" type="button" value="取消" onfocus="this.blur();"/>
            </div>
    </div>
    <div class="i_upBottom">
        <div class="i_upBottomLeft"></div>
        <div class="i_upBottomCenter"></div>
        <div class="i_upBottomRight"></div>
    </div>
</div>