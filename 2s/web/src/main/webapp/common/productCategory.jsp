<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div id="productCategory_id" class="productCategory_i_searchBrand">
    <div class="i_arrow"></div>
    <div class="i_upLeft"></div>
    <div class="i_upCenter">
        <div class="i_note" id="div_drag">设定商品类别</div>
        <div class="i_close" id="div_close"></div>
    </div>
    <div class="i_upRight"></div>
    <div class="i_upBody clear">
        <ul id="productCategory_ul" class="clear">
            <li><input type="radio" value="机修" name="radio"><label>机修</label><input type="button" class="btnPlus"
                                                                                    onfocus="this.blur();"></li>
            <li><input type="radio" value="钣金" name="radio"><label>钣金</label><input type="button" class="btnPlus"
                                                                                    onfocus="this.blur();"></li>
            <li><input type="radio" value="喷漆" name="radio"><label>喷漆</label><input type="button" class="btnPlus"
                                                                                    onfocus="this.blur();"></li>
            <li><input type="radio" value="洗车" name="radio"><label>洗车</label><input type="button" class="btnPlus"
                                                                                    onfocus="this.blur();"></li>
            <li><input type="radio" value="美容" name="radio"><label>美容</label><input type="button" class="btnPlus"
                                                                                    onfocus="this.blur();"></li>
            <li><input type="radio" value="装潢" name="radio"><label>装潢</label><input type="button" class="btnPlus"
                                                                                    onfocus="this.blur();"></li>
            <li><input type="radio" value="轮胎" name="radio"><label>轮胎</label><input type="button" class="btnPlus" onfocus="this.blur();"></li>
            <li><input type="radio" value="" name="radio">
                <input id="productCategory_value" type="text" style="width:145px;" title="输入完成,回车确认!"></li>

        </ul>
        <div class="more_his">
            <input id="productCategory_done" type="button" value="确认" onfocus="this.blur();" class="btn">
            <input id="productCategory_cancel" type="button" value="取消" onfocus="this.blur();" class="btn">
        </div>
    </div>
    <div class="i_upBottom clear add_upBottom">
        <div class="i_upBottomLeft"></div>
        <div class="i_upBottomCenter"></div>
        <div class="i_upBottomRight"></div>
    </div>
</div>
