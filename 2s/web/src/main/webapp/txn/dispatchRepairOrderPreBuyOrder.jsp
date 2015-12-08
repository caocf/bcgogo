<%@ page import="com.bcgogo.config.ConfigController" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
<script type="text/javascript">
    $(document).ready(function () {
        var $_win = $("#dispatchRepairOrderPreBuyOrderWindow");

        $_win.find('.publish-button').click(function () {
            var me = $_win;
            if (!validate($(me))) {
                return;
            }
            if ($(me).attr("disabled") !== "disabled") {
                $(me).attr("disabled", "disabled");
                $(me).find('form').ajaxSubmit({
                    dataType: "json",
                    url: "preBuyOrder.do?method=ajaxSavePreBuyOrder",
                    type: "POST",
                    success: function (data) {
//                        $("#dispatchRepairOrderPreBuyOrderTip").html(data['msg']);
                        showDispatchRepairOrderPreBuyOrderTip(
                                function () {
                                    $(me).removeAttr("disabled");
                                    $(me).dialog("close");
                                },
                                function () {
                                    $(me).removeAttr("disabled");
                                    $(me).dialog("close");
                                    window.open("preBuyOrder.do?method=preBuyOrderManage");
                                });
                    }
                });
            }
        });
        $_win.dialog({
            title: "求购提示",
            width: 500,
            height: 300,
            modal: true,
            resizable: false,
            autoOpen: false,
            closeOnEscape: false,
            close: function () {
                $(this).find('table').find('tr:not(:first)').remove();
                $(this).find('[name=title]').val("");
                $(this).find('[name=preBuyOrderValidDate]').val("");
                validateCreatePreBuyOrder.callback();
            }
        });

        //校验
        function validate($_this) {
            var amounts = $_this.find('[name$=".amount"]');
            if (amounts.length == 0) {
                nsDialog.jAlert("发布商品为空!", null, function () {
                    $_win.dialog("close");
                });
                return false;
            }
            for (var i = 0; i < amounts.length; i++) {
                if (Number($(amounts[i]).val()) <= 0) {
                    nsDialog.jAlert("请输入求购数量!");
                    return false;
                }
            }
            var domNames = $_this.find('[name$=".productName"]'),
                    name="求购：";
            $.each(domNames, function (index, dom) {
                if (name.length < 50) {
                    if (index != 0) {
                        name += "，";
                    }
                    name += $(dom).val();
                }
            });
            $_this.find('input[name=title]').val(name);
            if (!$_this.find('[name=title]').val()) {
                nsDialog.jAlert("请输入求购标题!");
                return false;
            }
            if (!$_this.find('select[name=preBuyOrderValidDate]').val()) {
                nsDialog.jAlert("请输入有效期!");
                return false;
            }
            return true;
        }

        //发布求购之后 提示
        function showDispatchRepairOrderPreBuyOrderTip(fn1, fu2) {
            $("#pre-buy-order-number-success").html($("#pre-buy-order-number").html());
            var $_tip= $("#dispatchRepairOrderPreBuyOrderTip");
            $_tip.find(".continue-button").click(function(){
                $(this).dialog("close");
                fn1();
            });
            $_tip.find(".review-button").click(function(){
                fu2();
                $(this).dialog("close");
            });
            $_tip.dialog({
                title:"求购提示",
                width:400,
                height:300,
                resizable: false,
                modal: true,
                draggable: true,
//                buttons: {
//                    "继续做单": function () {
//                        $(this).dialog("close");
//                        fn1();
//                    },
//                    "去看求购": function () {
//                        fu2();
//                        $(this).dialog("close");
//                    }
//                },
                close: function () {
                    $("#dispatchRepairOrderPreBuyOrderWindow").dialog("close");
                }
            });
        }

        //单行删除
        $_win.find('td[bcgogo-action="delete"]').live("click", function () {
            var $tr = $(this).parents('tr');
            $tr.remove();
        });

    });

    //显示求购页面
    function showCreatePreBuyOrder(products) {
        var $_win = $("#dispatchRepairOrderPreBuyOrderWindow");
        var content = '', commodityCode,productId, name, brand, model, spec, vehicleBrand, vehicleModel, unit;
        $("#pre-buy-order-number").html(products.length);
        $.each(products, function (index, product) {
            commodityCode = ( product['commodityCode'] ? product['commodityCode'] : "");
            productId = ( product['productId'] ? product['productId'] : "");
            name = ( product['name'] ? product['name'] : "");
            brand = ( product['brand'] ? product['brand'] : "");
            model = ( product['model'] ? product['model'] : "");
            spec = ( product['spec'] ? product['spec'] : "");
            vehicleBrand = ( product['vehicleBrand'] ? product['vehicleBrand'] : "");
            vehicleModel = ( product['vehicleModel'] ? product['vehicleModel'] : "");
            unit = ( product['unit'] ? product['unit'] : "");
            content += '<tr><td><div class="td-item">' +
                    '<input type="hidden" name="itemDTOs[' + index + '].commodityCode" value="' + commodityCode + '">' +
                    '<input type="hidden" name="itemDTOs[' + index + '].productName" value="' + name + '">' +
                    '<input type="hidden" name="itemDTOs[' + index + '].productId" value="' + productId + '">' +
                    '<input type="hidden" name="itemDTOs[' + index + '].brand" value="' + brand + '">' +
                    '<input type="hidden" name="itemDTOs[' + index + '].model" value="' + model + '">' +
                    '<input type="hidden" name="itemDTOs[' + index + '].spec" value="' + spec + '">' +
                    '<input type="hidden" name="itemDTOs[' + index + '].vehicleBrand" value="' + vehicleBrand + '">' +
                    '<input type="hidden" name="itemDTOs[' + index + '].vehicleModel" value="' + vehicleModel + '">' +
                    '<input type="hidden" name="itemDTOs[' + index + '].unit" value="' + unit + '">';
            content += commodityCode + '</div></td>';
            content += '<td><div class="td-item">' + name + '</div></td>';
            content += '<td><div class="td-item">' + brand + '</div></td>';
            content += '<td><div class="td-item">' + model + '</div></td>';
            content += '<td><div class="td-item">' + spec + '</div></td>';
//            content += '<td><div class="td-item">' + vehicleBrand + '</div></td>';
//            content += '<td><div class="td-item">' + vehicleModel + '</div></td>';
            content += '<td><div class="td-item w60">缺料<input type="text" name="itemDTOs[' + index + '].amount" value="' + product['amount'] + '"/>' + unit + '</div></td>';
            content += '<td bcgogo-action="delete"><div class="td-item delete-button"></div></td>';
            content += '</tr>';
        });
        $_win.find('table').append(content);
        $_win.dialog("open");
    }
</script>
<div id="dispatchRepairOrderPreBuyOrderWindow" title="发布求购" style="display:none;">
    <form id="dispatchRepairOrderPreBuyOrderForm" action="preBuyOrder.do?method=ajaxSavePreBuyOrder" method="post">
        <input type="hidden" maxlength="50" value="" name="title">
        <div class="notice-buy">
            <div class="div-static-tips"></div>
            <div class="div-content">
                <div class="content-title">
                    本单共缺料<span style="color:red;" id="pre-buy-order-number">0</span>种商品
                </div>
                <div class="data-list">
                    <table>
                        <col class="col-1">
                        <col class="col-2">
                        <col class="col-3">
                        <col class="col-4">
                        <col class="col-5">
                        <col class="col-6">
                        <col class="col-7">
                    </table>
                </div>
                <div class="hr-grey"></div>
                <div class="comment-grey">
                    友情提示：您可以直接发布求购信息，发布后汽配商将会针对求购报价，您可以对报价比价后再下单采购！
                </div>
                <div class="valid-date">
                    <label>求购有效期</label>
                    <select name="preBuyOrderValidDate">
                        <option value="">&mdash;请选择&mdash;</option>
                        <option value="ONE_DAY">1天内有效</option>
                        <option value="THREE_DAY">3天内有效</option>
                        <option value="SEVEN_DAY">7天内有效</option>
                        <option value="FIFTEEN_DAY">15天内有效</option>
                        <option value="THIRTY_DAY">30天内有效</option>
                    </select>
                </div>
                <div class="publish-button">发布求购</div>
            </div>
            <div style="clear:both;float:none;"></div>
        </div>
    </form>
</div>


<div id="dispatchRepairOrderPreBuyOrderTip" style="display:none;">
    <div class="notice-buy-success">
        <div class="div-content">
            <dl>
                <dt>友情提示:</dt>
                <dd>
                    <span>发布求购成功！</span>共求购<span style="color:red" id="pre-buy-order-number-success">0</span>种商品
                    <br />
                    此求购将推送给匹配的汽配商！
                </dd>
                <div style="clear: both;float: none;"></div>
            </dl>

            <div class="group-button">
                <div class="continue-button">继续做单</div>
                <div class="review-button">查看求购</div>
            </div>
            <div style="clear: both;float: none;"></div>
        </div>
    </div>
</div>