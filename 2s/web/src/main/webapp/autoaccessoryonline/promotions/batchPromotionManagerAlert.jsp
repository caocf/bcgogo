<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<script>
    $().ready(function(){

        getPromotionDetailForInsales(function(promotionList){
            initShopPromotionInfo(promotionList);
        });

        $(".pSelector").change(function(){
            var $item=$(this).closest(".s-item");
            $item.find('[name="promotionSource"]').attr("checked",true);
            $(".s-item").not($item).find('.pSelector').val("");
        });

    });

</script>


<div id="batchPromotionManagerAlert" class="alertMain" style="display: none">
    <input id="serviceStartTime" type="hidden" />
    <input id="addPromotions_pageSource" type="hidden" />
    <div style="margin-top: 15px" class="s-item">
        <label class="rad"><input type="radio" name="promotionSource" promotionSource="exist" checked="true">选择已有促销</label>
        <select class="promotionSelector pSelector" style="width:150px;"></select>
    </div>
    <div style="margin-top: 15px" class="s-item">
        <label class="rad"><input type="radio" promotionSource="new" name="promotionSource">创建新促销&nbsp;&nbsp;</label>
        <select id="promotionTypeSelector" class="pSelector" style="width:150px;">
            <option value="">请选择促销类型</option>
            <option value="MLJ">满立减</option>
            <option value="MJS">满就送</option>
            <option value="BARGAIN">特价商品</option>
            <option value="FREE_SHIPPING">送货上门</option>
        </select>
    </div>

</div>

<div>
    <%--创建满立减--%>
    <%@ include file="manageMLJAlert.jsp" %>
    <%--创建满就送--%>
    <%@ include file="manageMJSAlert.jsp" %>
    <%--创建特价--%>
    <%@ include file="manageBargainAlert.jsp" %>
    <%--创建送货上门--%>
    <%--<%@ include file="manageFreeShippingAlert.jsp" %>--%>
</div>
<div id="manageFreeShippingAlertDiv" class="ui-dialog ui-widget ui-widget-content ui-corner-all ui-draggable ui-resizable" style="display: none; z-index: 1004; outline: 0px none; position: absolute; height: auto; width: 700px; top: 50%" tabindex="-1" role="dialog" aria-labelledby="ui-dialog-title-manageFreeShippingAlert">
    <%--创建送货上门--%>
    <%@ include file="manageFreeShippingAlertDiv.jsp" %>
</div>
