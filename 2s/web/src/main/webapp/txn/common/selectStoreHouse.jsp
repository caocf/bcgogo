<%@ page import="com.bcgogo.config.ConfigController" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<link rel="stylesheet" type="text/css" href="styles/selectStorehouse<%=ConfigController.getBuildVersion()%>.css"/>
    <script type="text/javascript">
        $(document).ready(function () {
            $("#storehouseCancelBtn").bind("click", function () {
                $("#_toStorehouseId").val("");
                $("#selectStorehouseDialog").dialog("close");
            });
            $("#storehouseConfirmBtn").bind("click", function () {
                if(!G.Lang.isEmpty($("#_storehouseId").attr("data-order-id"))){
                    $("#_toStorehouseId").val($("#_storehouseId").val());
                    if (getOrderType() == "REPAIR_PICKING_INFO") {
                        nsDialog.jConfirm("友情提示：请确认是否出库/退料？","确认出库/退料提示",function(returnVal){
                           if(returnVal){
                               $("#toStorehouseId").val($("#_storehouseId").val());
                               $("#repairPickingForm").attr("action","pick.do?method=handleRepairPicking");
                               $("#repairPickingForm").submit();
                           }
                        });
                    }else if(getOrderType() == "REPAIR_PICKING_LIST"){
                        var idPrefix = $("#_storehouseId").attr("repair_Picking_idPrefix");
                        $("#"+idPrefix + "\\.form").find("input[name='toStorehouseId']").val($("#_storehouseId").val());
                        var msg  = "友情提示：退料确认后，本单中所有未退料材料将全部退料！<br><br><div align='center'>您确定要全部退料吗？</div>";
                        nsDialog.jConfirm(msg,"确认出库/退料提示", function (returnVal) {
                            if (returnVal) {
                                itemFormSubmit(idPrefix ,"RETURN_STORAGE");
                            }
                        });
                    } else {
                        repealOrder($("#_storehouseId").attr("data-order-id"));
                    }
                }
            });
        });
    </script>
<div class="i_searchBrand" id="selectStorehouseDialog" title="请选择仓库" style="width:310px;display:none">
    <div class="i_upBody" style="width:310px;border-width: medium 0;">
        <h3>当前单据仓库已经不存在，请选择其他仓库!</h3>

        <div class="request">仓库：
            <select id="_storehouseId" data-order-id="" autocomplete="off" style="width:150px;height:21px;
            border:1px solid #BBBBBB;" name="_storehouseId" repair_Picking_idPrefix="">
                <c:forEach items="${storeHouseDTOList}" var="storeHouseDTO">
                    <option value="${storeHouseDTO.id}">${storeHouseDTO.name}</option>
                </c:forEach>
            </select>
            <input id="_toStorehouseId" name="_toStorehouseId" value="" type="hidden" autocomplete="off"/>
        </div>
        <div class="height"></div>
        <div class="btnClick" style="padding-left:30px;">
            <input type="button" id="storehouseConfirmBtn" onfocus="this.blur();" value="确&nbsp;定">
            <input type="button" id="storehouseCancelBtn" onfocus="this.blur();" value="取&nbsp;消">
        </div>
    </div>
</div>