<%@ page import="com.bcgogo.config.ConfigController" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>来料待修</title>
    <link rel="stylesheet" type="text/css" href="styles/up3<%=ConfigController.getBuildVersion()%>.css"/>
    <script type="text/javascript" src="js/extension/jquery/jquery-1.4.2.min.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.min.js"></script>
    <script type="text/javascript" src="js/base<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/application<%=ConfigController.getBuildVersion()%>.js"></script>

    <script type="text/javascript">
        $(document).ready(function() {
            $(".repairOrderId").click(function() {
                var storageUnit = $("#storageUnit").val();
                var sellUnit = $("#sellUnit").val();
                var rate = $("#rate").val() * 1;
                var lackAmountBySellUnit;
                var lackAmount = $(this).next().val();
                var lackUnit = $(this).next().next().val();
                if (lackUnit == storageUnit && storageUnit != sellUnit && rate * 1 != 0) {           //缺料单位为库存大单位
                    lackAmountBySellUnit = lackAmount * rate * 1;
                } else {
                    lackAmountBySellUnit = lackAmount * 1;
                }
                if ($(this).attr("checked") == true) {
                    if ($("#used").text() * 1 + lackAmountBySellUnit <= $("#total").text() * 1)
                        $("#used").text($("#used").text() * 1 + lackAmountBySellUnit);
                    else {
                        $(this).attr("checked", false);
                        alert('库存不足，不能选择该单据，请调整选择');
                    }
                } else {
                    $("#used").text($("#used").text() * 1 - lackAmountBySellUnit);
                }
                $("#remainder").text($("#total").text() * 1 - $("#used").text() * 1);
                $(this).next().attr("checked", $(this).attr("checked"));
            });
            $("#confirmBtn").click(function() {
                window.parent.document.getElementById("mask").style.display = "none";
                window.parent.document.getElementById("iframe_PopupBox").style.display = "none";
                $("#incomingRepairForm").ajaxSubmit({
                  dataType: 'json',
                  success:function(data){
                    if(data.result == 'success'){
                return false;
                    }else{
                      window.parent.location.href = data.result;
                    }
                  }
            });
        });
        });

        $(document).ready(function() {

            document.getElementById("div_close").onclick = function() {
                window.parent.document.getElementById("mask").style.display = "none";
                window.parent.document.getElementById("iframe_PopupBox").style.display = "none";
                //window.parent.document.getElementById("iframe_PopupBox").src = "";
                try {
                    $(window.parent.document.body).find("input[type='button']").eq(0).focus().blur();
                } catch(e) {
                    ;
                }
                window.parent.showSupplierComment();
            };
            document.getElementById("cancelBtn").onclick = function() {
                $("#div_close").click();
            };
        });
    </script>
</head>
<body>
<div class="i_incoming" id="div_show">
    <div class="i_arrow"></div>
    <div class="i_upLeft"></div>
    <div class="i_upCenter">
        <div class="i_note" id="div_drag">来料待修提示<span>(下列车牌已来料入库，请点击进入施工单)</span></div>
        <div class="i_closeIncome" id="div_close"></div>
    </div>
    <div class="i_upRight"></div>
    <div class="i_upBody">
        <form id="incomingRepairForm" name="incomingRepairForm" action="storage.do?method=updateLackGood&returnType=1" method="post">
            <input id="productIds" name="productIds" type="hidden" value="${param.productIds}">
            <input id="storageUnit" name="storageUnit" type="hidden" value="${storageUnit}">
            <input id="sellUnit" name="sellUnit" type="hidden" value="${sellUnit}">
            <input id="rate" name="rate" type="hidden" value="${rate}">
            <table cellpadding="0" cellspacing="0" class="tableIncoming">
                <col width="40">
                <col width="80">
                <col width="55">
                <col width="100">
                <col width="70">
                <col width="50">
                <col width="100"/>
                <col width="40">
                <col width="35">
                <col width="117">
                <col width="117">
                <tr class="incomingTit">
                    <td>No</td>
                    <td>车牌号</td>
                    <td>车主</td>
                    <td>联系方式</td>
                    <td>车辆品牌</td>
                    <td>车型</td>
                    <td>品名</td>
                    <td>数量</td>
                    <td>单位</td>
                    <td>进厂时间</td>
                    <td>预计出厂时间</td>
                </tr>
                <c:forEach items="${lackMaterialDTOs}" var="lackMaterial" varStatus="varStatus">
                    <tr>
                        <td><input id="repairOrderId" class="repairOrderId" type="checkbox" name="repairOrderId" value="${lackMaterial.id}" onfocus="this.blur();"/>
                            <input id="productAmount" style="display: none;" type="checkbox" name="productAmount" value="${lackMaterial.amount}"/>
                            <input id="productUnit" style="display: none;" type="checkbox" name="productUnit" value="${lackMaterial.unit}"/>
                        </td>
                        <td>${lackMaterial.vechicle}</td>
                        <td>${lackMaterial.customer}</td>
                        <td>${lackMaterial.mobile}</td>
                        <td>${lackMaterial.vehicleBrand}</td>
                        <td>${lackMaterial.vehicleModel}</td>
                        <td>${lackMaterial.productName}</td>
                        <td class="incomingCount">${lackMaterial.amount}</td>
                        <td>${lackMaterial.unit}</td>
                        <td>${lackMaterial.startDateStr}</td>
                        <td>${lackMaterial.endDateStr}</td>
                    </tr>
                </c:forEach>
            </table>
            <br/>

            <div><br/><br/><br/>库存量：<label id="total">${inventoryAmount}</label>
                单位：<label id="unit">${sellUnit}</label>
                分配：<label id="used">0</label> 剩余：<label id="remainder">${inventoryAmount}</label></div>
            <div class="i_sure">
              <input type="button" id="cancelBtn" value="取消" onfocus="this.blur();"/>
            </div>
            <div class="i_sure">
              <input type="button" id="confirmBtn" value="确定" onfocus="this.blur();"/>
            </div>
        </form>
    </div>
    <div class="i_upBottom">
        <div class="i_upBottomLeft"></div>
        <div class="i_upBottomCenter"></div>
        <div class="i_upBottomRight"></div>
    </div>
</div>

</body>
</html>
