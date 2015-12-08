<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/views/includes.jsp" %>

<div id="editGoodsInfo_id" class="editGoodsInfo_i_searchBrand" style="display: none">
  <div class="i_arrow"></div>
  <div class="i_upLeft"></div>
  <div class="i_upCenter">
    <div class="i_note" id="div_drag">修改商品信息</div>
    <div class="i_close" id="div_close"></div>
  </div>
  <div class="i_upRight"></div>
  <div class="i_upBody clear">
    <ul id="edit_content_id" class="edit_goods clear">
      <li><label>商品编码</label>
        <input type="text" class="txt_edit" id="edit_commodity_code_id" maxlength="20"></li>
      <li><label>车辆品牌</label>
        <input type="text" id="edit_vehicle_brand_id" class="txt_edit"
               onkeyup="EditGoodsILnfo.modifyText('brand',this);"
               onblur="EditGoodsILnfo.modifyTextBlur('brand',this)"></li>
      <li><label>品名</label>
        <input type="text" class="txt_edit" id="edit_product_name_id"
               onkeyup="EditGoodsILnfo.modifyText('productName',this)"
               onblur="EditGoodsILnfo.modifyTextBlur('productName',this)"></li>
      <li><label>车型</label>
        <input type="text" id="edit_vehicle_model_id" class="txt_edit"
               onkeyup="EditGoodsILnfo.modifyText('model',this);searchVehicleSuggestion(this,'notClick',jQuery('#edit_vehicle_brand_id').val())"
               onclick="searchVehicleSuggestion(this,'click',jQuery('#edit_vehicle_brand_id').val())"
               onblur="EditGoodsILnfo.modifyTextBlur('model',this)"></li>
      <li><label>品牌</label>
        <input type="text" class="txt_edit" id="edit_product_brand_id"
               onkeyup="EditGoodsILnfo.modifyText('productBrand',this)"
               onblur="EditGoodsILnfo.modifyTextBlur('productBrand',this)"></li>
      <li><label>库存上限</label>
        <input id="upperLimit_id" type="text" class="txt_edit"
               onkeyup="EditGoodsILnfo.modifyNumber('upperLimit',this)"
               onblur="EditGoodsILnfo.modifyNumber('upperLimit',this)"></li>
      <li><label>规格</label>
        <input type="text" class="txt_edit" id="edit_product_spec_id"
               onkeyup="EditGoodsILnfo.modifyText('productSpec',this)"
               onblur="EditGoodsILnfo.modifyTextBlur('productSpec',this)"></li>
      <li><label>库存下限</label>
        <input id="lowerLimit_id" type="text" class="txt_edit"
               onkeyup="EditGoodsILnfo.modifyNumber('lowerLimit',this)"
               onblur="EditGoodsILnfo.modifyNumber('lowerLimit',this)"></li>
      <li><label>型号</label>
        <input type="text" class="txt_edit" id="edit_product_model_id"
               onkeyup="EditGoodsILnfo.modifyText('productModel',this)"
               onblur="EditGoodsILnfo.modifyTextBlur('productModel',this)"></li>
      <li><label>销售价</label>
        <input id="recommendedPrice_id" type="text" class="txt_edit"
               onkeyup="EditGoodsILnfo.modifyNumber('recommendedPrice',this)"
               onblur="EditGoodsILnfo.modifyNumber('recommendedPrice',this)"></li>
      <li class="storage_bin_li"><label>货位</label>
        <input id="storage_bin_id" class="txt_edit" type="text"
               onkeyup="EditGoodsILnfo.modifyText('storageBin',this)"
               onblur="EditGoodsILnfo.modifyTextBlur('storageBin',this)"></li>
      <li class="trade_price_li"><label>批发价</label>
        <input id="tradePrice_id" type="text" class="txt_edit"
               onkeyup="EditGoodsILnfo.modifyNumber('tradePrice',this)"
               onblur="EditGoodsILnfo.modifyNumber('tradePrice',this)"></li>
      <li><label>单位</label>
        <span id="storage_unit_num" style="display: none;">1</span>
        <input type="text" class="txt_edit itemUnit" id="storage_unit_id" style="width: 38px;display: none;"
               onkeyup="EditGoodsILnfo.modifyText('storageUnit',this)">
        <span id="equal" style="display: none;">=</span>
        <span id="rate_span" style="display: none;">1</span>
        <input type="text" class="txt_edit itemUnit" id="sell_unit_id"
               onkeyup="EditGoodsILnfo.modifyText('sellUnit',this)"></li>
      <%--<li><label>提成</label><input type="text" class="txt_edit"></li>--%>
      <%--<li><label>商品类别</label><input type="text" class="txt_edit" onclick="ProductCategory.show(this,'商品')" readonly="true"></li>--%>
      <%--<li><label>营业类别</label><input type="text" class="txt_edit" onclick="ProductCategory.show(this,'营业')" readonly="true"></li>--%>
      <%--<li><label>仓库</label><span></span></li>--%>
      <li><label>数量</label><span id="amount_span"></span></li>
      <li><label>入库价</label><span id="purchase_span"></span></li>
      <li><label>商品分类</label>
        <input id="edit_productKind" class="txt_edit" maxlength="20"
               onkeyup="EditGoodsILnfo.modifyText('kindName',this)"
               onblur="EditGoodsILnfo.modifyTextBlur('kindName',this)"></li>
    </ul>
    <%--<div class="next_info clear">--%>
    <%--<input type="button" value="上一条" onfocus="this.blur();" class="btn">--%>
    <%--<input type="button" value="下一条" onfocus="this.blur();" class="btn">--%>

    <%--&lt;%&ndash;<div>共有<label>1200</label>条,第<label>1222</label>条</div>&ndash;%&gt;--%>
    <%--</div>--%>
    <bcgogo:hasPermission permissions="WEB.TXN.INVENTORY_MANAGE.INVENTORY_PRICE_ADJUSTMENT.BASE" resourceType="menu">
      <div>
        <label>库存盘点、调价</label><br/>
        <label>实际库存</label>
        <input type="text" id="actualInventoryAmount" value="">
        &nbsp;&nbsp;&nbsp;
        <label>库存均价</label>
        <input type="text" id="actualInventoryAveragePrice" value="">
      </div>
    </bcgogo:hasPermission>


    <div class="more_his clear">
      <input id="editGoodsInfo_done" type="button" value="确认" onfocus="this.blur();" class="btn">
      <input id="editGoodsInfo_cancel" type="button" value="取消" onfocus="this.blur();" class="btn">
    </div>

  </div>
  <div class="i_upBottom clear add_upBottom">
    <div class="i_upBottomLeft"></div>
    <div class="i_upBottomCenter"></div>
    <div class="i_upBottomRight"></div>
  </div>
</div>

