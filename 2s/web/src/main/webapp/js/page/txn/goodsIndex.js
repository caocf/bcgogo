/**
 * Created by IntelliJ IDEA.
 * User: zyj
 * Date: 12-2-3
 * Time: 上午10:46
 * To change this template use File | Settings | File Templates.
 */
var isTheLastPage = false;
var bcgogoAjaxQuery = APP_BCGOGO.Module.wjl.ajaxQuery;

$(document).ready(function() {

    jQuery("#pageNo_id>div").bind("click", function() {
        if (!search_Switch) {
            $("#table_productNo tr:not(:first)").remove();
            var selectItem = $(this).html();
            if (selectItem == "上一页") {
                if (nextPageNo > 1) {
                    nextPageNo = nextPageNo - 1;
                    $("#thisPageNo").html(nextPageNo);
                    $("#pageNo_id>div:last").css('display', 'block');
                    if (nextPageNo == 1) {
                        $(this).css('display', 'none');
                    }
                }
            } else if (selectItem == "下一页") {
                if (!isTheLastPage) {
                    nextPageNo = nextPageNo + 1;
                    $("#thisPageNo").html(nextPageNo);
                    $("#pageNo_id>div:first").css('display', 'block');
                }
            }
            var ajaxUrl = "goodsindex.do?method=inventory";
            var ajaxData = {productInfo:$("#searchProductName").val(),start:nextPageNo,rows:25,countStr:$("#count").val()};
            bcgogoAjaxQuery.setUrlData(ajaxUrl, ajaxData);
            bcgogoAjaxQuery.ajaxQuery(function(jsonStr) {
                initTr(jsonStr);
                getChecked();
                var checkboxs = document.getElementsByName("productIds");
                for (var i = 0; i < checkboxs.length; i++) {
                    if (checkboxs[i].checked == false) {
                        document.getElementById("checkAlls").checked = false;
                        break;
                    }
                    document.getElementById("checkAlls").checked = true;
                }
                if (jsonStr[jsonStr.length - 1].isTheLastPage) {
                    $("#pageNo_id>div:last").css('display', 'none');
                }
            }, function(jsonStr) {
                $("#pageNo_id>div:last").css('display', 'none');
            });
        } else {
            var selectItem = $(this).html();
            if (selectItem == "上一页") {
                if (nextPageNo > 1) {
                    nextPageNo = nextPageNo - 1;
                    $("#thisPageNo").html(nextPageNo);
                    $("#pageNo_id>div:last").css('display', 'block');
                    if (nextPageNo == 1) {
                        $(this).css('display', 'none');
                    }
                    if (search_condition == "stockSearch") {
                        $("#searchInventoryBtn").click();
                    } else if (search_condition == "lowerLimit") {
                        $("#lowerLimit_click").click();
                    } else if (search_condition == "upperLimit") {
                        $("#upperLimit_click").click();
                    }

                }
            } else if (selectItem == "下一页") {
                if (!isTheLastPage) {
                    nextPageNo = nextPageNo + 1;
                    $("#thisPageNo").html(nextPageNo);
                    $("#pageNo_id>div:first").css('display', 'block');
                    if (search_condition == "stockSearch") {
                        $("#searchInventoryBtn").click();
                    } else if (search_condition == "lowerLimit") {
                        $("#lowerLimit_click").click();
                    } else if (search_condition == "upperLimit") {
                        $("#upperLimit_click").click();
                    }
                }
            }
        }
    });
});

function initTr(jsonStr) {
	var permissionGoodsBuy = jQuery("#permissionGoodsBuy").val();
	var permissionGoodsSale = jQuery("#permissionGoodsSale").val();
	var permissionGoodsHistory = jQuery("#permissionGoodsHistory").val();
	var permissionInventoryAlarmSettings = jQuery("#permissionInventoryAlarmSettings").val();
    if (jsonStr.length >= 1) {
        $("#table_productNo tr:not(:first)").remove();
        isTheLastPage = jsonStr[jsonStr.length - 1].isTheLastPage;

        for (var i = 0; i < jsonStr.length - 1; i++) {
            if (!jsonStr[i])  continue;
            var brandName = jsonStr[i].productVehicleBrand;
            brandName = (brandName === "多款" || brandName === "全部" ? "" : brandName);

            var tr = '<tr>';
            tr += '<td style="border-left:none;">';
            tr += '<input id="' + i + '" type="checkbox" name="productIds"  class="test" value="' + jsonStr[i].productLocalInfoIdStr + '" onfocus="this.blur();"/>' +
                '<input id="productDTOs' + i + '.productLocalInfoId" name="productDTOs[' + i + '].productLocalInfoId" type="hidden" class="input_lowerLimit" title="" value="' + jsonStr[i].productLocalInfoIdStr + '" ></td>';
            tr += '<td title="' + (i + 1) + '">' + (i + 1) + '</td>';
			if (permissionGoodsHistory == "true") {
				tr += '<td title="' + jsonStr[i].name + '" class="qian_blue">' +
					'<a href="javascript:getGoodsHistory1(\'' + GLOBAL.Lang.addSlash(jsonStr[i].name) + '\',\'' + GLOBAL.Lang.addSlash(jsonStr[i].brand) + '\',\''
					+ GLOBAL.Lang.addSlash(jsonStr[i].spec) + '\',\'' + GLOBAL.Lang.addSlash(jsonStr[i].model) + '\');">' + jsonStr[i].name + '</a></td>';
			} else{
              tr += '<td>' + jsonStr[i].name + '</td>';
			}
            tr += '<td>' + jsonStr[i].brand + '</td>';
            tr += '<td>' + jsonStr[i].spec + '</td>';
            tr += '<td>' + jsonStr[i].model + '</td>';

            tr += '<td title="' + brandName + '">' + brandName + '</td>';

            tr += '<td>' + jsonStr[i].productVehicleModel + '</td>';
//            tr += '<td>' + jsonStr[i].productVehicleYear + jsonStr[i].productVehicleEngine + '</td>';
            tr += '<td style="text-align:right;" >' + jsonStr[i].inventoryNum + '<input id="inventoryNum' + i + '" type="hidden" value="' + jsonStr[i].inventoryNum + '"></td>';
            tr += '<td><span>' + jsonStr[i].sellUnit + '</span></td>';
            if (permissionGoodsBuy == "true") {  //author zhangjuntao  permission control
                tr += '<td style="text-align:right;" ><span class="span_purchase_price" style="display: none;">' + jsonStr[i].purchasePrice + '</span><input class="purchasePrice_class" type="hidden" value="' + jsonStr[i].purchasePrice + '"> </td>';
            }
            if (permissionInventoryAlarmSettings == "true") {
                tr += '<td class="product_setting product_setting_btnExchange"><input id="productDTOs' + i + '.lowerLimit" name="productDTOs[' + i + '].lowerLimit" class="input_lowerLimit" title="" value="' + dataTransition.simpleRounding(jsonStr[i].lowerLimit, 1) + '" ></td>';
                tr += '<td class="product_setting product_setting_btnExchange"><input id="productDTOs' + i + '.upperLimit" name="productDTOs[' + i + '].upperLimit" class="input_upperLimit" title="" value="' + dataTransition.simpleRounding(jsonStr[i].upperLimit, 1) + '" ></td>';
            } else {
                tr += '<td class="product_setting product_setting_btnExchange">' + dataTransition.simpleRounding(jsonStr[i].lowerLimit, 1) + '</td>';
                tr += '<td class="product_setting product_setting_btnExchange">' + dataTransition.simpleRounding(jsonStr[i].upperLimit, 1) + '</td>';
            }
            if (permissionGoodsSale == "true") {
                tr += '<td style="border-right:none;display: none" class="product_setting product_setting_btnSaleja">' +
                    '<input style="text-align:right;" type="text" class="table_input recommendedPrice_input" value="' + jsonStr[i].recommendedPrice + '"><span style="display: none;">' + jsonStr[i].productLocalInfoIdStr + '</span></td>';
            }
            tr += '<td style="border-right:none;color:#69C;cursor:pointer"><div onclick="EditGoodsILnfo.show(' + i + ')">修改</div></td>';
            tr += '</tr>';
            $("#table_productNo").append($(tr));
//            $(".input_lowerLimit,.input_upperLimit").bind("change", function() {     //todo 方法移动到stocksearch.js by qxy
//                updateSingleLimit(this);
//            });
        }
    }
    else {
        isTheLastPage = true;
    }
}


function initTr1(jsonStr) {
    $("#sata_tab tr:not(:first)").remove();
    if (jsonStr.length > 1) {
        for (var i = 0; i < jsonStr.length - 1; i++) {
            var tr = '<tr class="table-row-original">';
            tr += '<td style="border-left:none;">' + (i + 1) + '</td>';
            tr += ' <td> <a href="storage.do?method=getProducts&repairOrderId=' + jsonStr[i].id + '&productIds=' + jsonStr[i].productId + '&productAmount=' + jsonStr[i].amount + '">' + jsonStr[i].productName + '</a></td>';
            tr += ' <td> <a href="txn.do?method=getRepairOrder&menu-uid=VEHICLE_CONSTRUCTION_REPAIR&repairOrderId=' + jsonStr[i].id + '">' + jsonStr[i].vechicle + '</a></td>';
            tr += '<td>' + jsonStr[i].customer + '</td>';
            tr += '<td>' + jsonStr[i].mobile + '</td>';
            tr += '<td>' + jsonStr[i].vehicleModel + '</td>';
            tr += '<td>' + jsonStr[i].service + '</td>';
            tr += '<td style="border-right:none;" class="txt_right">' + jsonStr[i].endDateStr + '</td>';
            tr += '</tr>';
            $("#sata_tab").append(tr);
        }
    }
    tableUtil.tableStyle('#sata_tab','.table_title');
}
function initTr2(jsonStr) {
    $("#kucun tr:not(:first)").remove();
    if (jsonStr.length > 1) {
        for (var i = 0; i < jsonStr.length - 1; i++) {
            var tr = '<tr class="table-row-original">';
            tr += '<td style="border-left:none;">' + (i + 1) + '</td>';
            tr += ' <td> <a href="txn.do?method=getRepairOrder&menu-uid=VEHICLE_CONSTRUCTION_REPAIR&repairOrderId=' + jsonStr[i].id + '">' + jsonStr[i].productName + '</a></td>';
            tr += '<td>' + jsonStr[i].vechicle + '</td>';
            tr += '<td>' + jsonStr[i].customer + '</td>';
            tr += '<td>' + jsonStr[i].mobile + '</td>';
            tr += '<td>' + jsonStr[i].vehicleModel + '</td>';
            tr += '<td>' + jsonStr[i].service + '</td>';
            tr += '<td class="txt_right">' + jsonStr[i].startDateStr + '</td>';
            tr += '<td style="border-right:none;" class="txt_right">' + jsonStr[i].endDateStr + '</td>';
            tr += '</tr>';
            $("#kucun").append(tr);
        }
    }
    tableUtil.tableStyle('#kucun','.table_title');
}

function initTr3(jsonStr) {
    $("#tab_three tr:not(:first)").remove();
    if (jsonStr.length > 1) {
        for (var i = 0; i < jsonStr.length - 1; i++) {
            var tr = '<tr class="table-row-original">';
            tr += '<td style="border-left:none;">' + (i + 1) + '</td>';
            tr += '<td>' + jsonStr[i].supplier + '</td>';
            tr += '<td><a href="storage.do?method=getProducts&type=txn&supplierId=' + jsonStr[i].supplierId + '&purchaseOrderId=' + jsonStr[i].purchaseOrderId + '">' + jsonStr[i].productName + ' </a></td>';
            tr += '<td>' + jsonStr[i].productBrand + '</td>';
            tr += '<td>' + jsonStr[i].productSpec + '</td>';
            tr += '<td>' + jsonStr[i].productModel + '</td>';
            tr += '<td>' + jsonStr[i].price + '</td>';
            tr += '<td>' + jsonStr[i].amount + '</td>';
            tr += '<td style="border-right:none;" class="txt_right">' + jsonStr[i].price * jsonStr[i].amount + '</td>';
            tr += '</tr>';
            $("#tab_three").append(tr);
        }
    }
    tableUtil.tableStyle('#tab_three','.table_title');
}

function getCursorPosition(ctrl) {//获取光标位置函数
    var CaretPos = 0;
    // IE Support
    if (document.selection) {
        ctrl.focus();
        var Sel = document.selection.createRange();
        Sel.moveStart('character', -ctrl.value.length);
        CaretPos = Sel.text.length;
    }
    // Firefox support
    else if (ctrl.selectionStart != NaN || ctrl.selectionStart == '0')
        CaretPos = ctrl.selectionStart;
    return (CaretPos);
}

function setCursorPosition(ctrl, pos) {//设置光标位置函数
    if (ctrl.setSelectionRange) {
        ctrl.focus();
        ctrl.setSelectionRange(pos, pos);
    }
    else if (ctrl.createTextRange) {
        var range = ctrl.createTextRange();
        range.collapse(true);
        range.moveEnd('character', pos);
        range.moveStart('character', pos);
        range.select();
    }
}



