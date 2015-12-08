$().ready(function() {
    /**
     * 绑定查询时间控件
     */
    $("#startTimeInput,#endTimeInput").bind("click", function() {
        $(this).blur();
    }).datetimepicker({
        "numberOfMonths": 1,
        "showButtonPanel": true,
        "changeYear": true,
        "changeMonth": true,
        "yearSuffix": "",
        "onClose": function(dateText, inst) {
            if(!$(this).val()) {
                return;
            }
            if($("#startTimeInput").val() >= $("#endTimeInput").val()) {
                nsDialog.jAlert("开始时间应小于结束时间，请重新选择！");
                $(this).val(inst.lastVal);
            }
        },
        "onSelect": function(dateText, inst) {
            if(inst.lastVal == dateText) {
                return;
            }

            $(this).val(dateText);
        }
    });

    //默认单据类型全选
    $(".search_td>input").each(function() {
        $(this).attr("checked", true);
    });

    $(".orderTyperCountTitle a").hover(function(){
        $(this).css({"color":"#fd5300","textDecoration":"underline"});
    },function(){
        $(this).css({"color":"#0067C2","textDecoration":"none"});
    });

    $(".table-row-original a").live("mouseover",function(){
        $(this).css({"color":"#fd5300","textDecoration":"underline"});
    });
    $(".table-row-original a").live("mouseout",function(){
        $(this).css({"color":"#0094FF","textDecoration":"none"});
    });
    //绑定全选按钮
    $("#allOrderTypesCbox").bind("click", function() {
        if($("#allOrderTypesCbox").attr("checked")) {
            $(".search_td>input").attr("checked", true);
        } else {
            $(".search_td>input").attr("checked", false);
        }
    });

    //各按钮全部选择则全选按钮自动勾选，否则自动去掉勾选
    $(".search_td>input").not($("#allOrderTypesCbox")).each(function() {
        $(this).bind("click", function() {
            if(!$(this).attr("checked")) {
                $("#allOrderTypesCbox").attr("checked", false);
            }
            if(isAllTypeSelected()) {
                $("#allOrderTypesCbox").attr("checked", true);
            }
        });
    });

    /**
     * 绑定查询按钮
     */
    $("#draftSearchBtn").bind("click", function() {
        var startTime = $("#startTimeInput").val();
        var endTime = $("#endTimeInput").val();
        if(startTime >= endTime) {
            nsDialog.jAlert("开始时间应小于结束时间，请重新选择！");
            return;
        }

        $("#draftOrderSearchForm").ajaxSubmit(function(data) {
            initDraftTableBox(data);
            initDraftOrderStat(data);
            var paramJson = {
                startPageNo: 1,
                orderTypes: getSelectedOrderTypes(),
                startTime: startTime,
                endTime: endTime
            };
            initPages(data, "dynamical1", "draft.do?method=getDraftOrders", '', "initDraftTableBox", '', '', paramJson, '');
        });
    });


    /**
     * 绑定草稿箱上面的链接
     */
    $(".orderTyperCountTitle a").bind("click", function() {
        if(!GLOBAL.Lang.isEmpty($(this).html()) && Number($(this).html()) >= 0) {
            var url = "draft.do?method=getDraftOrders";
            var startTime = $("#startTimeInput").val();
            var endTime = $("#endTimeInput").val();
            if(startTime >= endTime) {
                nsDialog.jAlert("开始时间应小于结束时间，请重新选择！");
                return;
            }
            var orderType = $(this).attr("orderType");
            var paramJson = {
                startPageNo: 1,
                orderTypes: orderType,
                startTime: startTime,
                endTime: endTime
            };
            if(orderType == "ALL") {
                paramJson = {
                    startPageNo: 1,
                    orderTypes: getSelectedOrderTypes(),
                    startTime: startTime,
                    endTime: endTime
                };
            }
            APP_BCGOGO.Net.syncPost({
                url: url,
                dataType: "json",
                data: paramJson,
                success: function(json) {
                    if(!json) {
                        GLOBAL.error("draftOrderBox.js --> $(\".orderTyperCountTitle a\").bind(\"click\", function () {  的回调值 json 是空值");
                        return;
                    }

                    initDraftTableBox(json);
                    initPages(json, "dynamical1", url, '', "initDraftTableBox", '', '', paramJson, '');
                }
            });
        }
    });
    //有草稿箱的单据，从草稿打开的单据有单据号
    if(!G.isEmpty($("#receiptNo").val())){
        $("#receiptNoSpan").text($("#receiptNo").val());
    }
});

/**
 * 根据单据类型的radiobutton生成单据类型的字符串
 */

function getSelectedOrderTypes() {
    var orderTypesStr = "";
    if($("#allOrderTypesCbox").attr("checked")) {
        orderTypesStr = "ALL";
        return orderTypesStr;
    }
    $(".search_td>input").not($("#allOrderTypesCbox")).each(function() {
        if($(this).attr("checked")) {
            orderTypesStr += $(this).val() + ",";
        }
    });
    return orderTypesStr;
}

/**
 * 判断单据类型的radiobutton是否选中
 */

function isAllTypeSelected() {
    var flag = true;
    $(".search_td>input").not($("#allOrderTypesCbox")).each(function() {
        if(!$(this).attr("checked")) {
            flag = false;
        }
    });
    return flag;
}

/**
 * 删除草稿
 * @param draftOrderIdStr
 */

function deleteDraftOrder(draftOrderIdStr) {
    if(!confirm("删除后该条单据草稿将消失，是否确认删除？")) {
        return;
    }
    var startTime = $("#startTimeInput").val();
    var endTime = $("#endTimeInput").val();
    if(startTime >= endTime) {
        nsDialog.jAlert("开始时间应小于结束时间，请重新选择！");
        return;
    }

    var url = "draft.do?method=deleteDraftOrder";
    var paramJson = {
        draftOrderIdStr: draftOrderIdStr,
        startPageNo: 1,
        orderTypes: getSelectedOrderTypes(),
        startTime: startTime,
        endTime: endTime
    };
    APP_BCGOGO.Net.asyncAjax({
        url: url,
        dataType: "json",
        data: paramJson,
        success: function(jsonStr) {
            initDraftTableBox(jsonStr);
            initDraftOrderStat(jsonStr);
            initPages(jsonStr, "dynamical1", "draft.do?method=getDraftOrders", '', "initDraftTableBox", '', '', paramJson, '');
        }
    });
}

function modifyDraftOrder(draftOrderIdStr) {

    if($("#newSettlePage")) {
        window.location.href = "draft.do?method=getOrderByDraftOrderId&flag=" + $("#draft_table").attr("flag") + "&draftOrderIdStr=" + draftOrderIdStr + "&newSettlePage=true";
    } else if($("#receiptNo") && $("#receiptNo").val()) {
        window.location.href = "draft.do?method=getOrderByDraftOrderId&flag=" + $("#draft_table").attr("flag") + "&draftOrderIdStr=" + draftOrderIdStr + "&receiptNo=" + $("#receiptNo").val();
    } else {
        window.location.href = "draft.do?method=getOrderByDraftOrderId&flag=" + $("#draft_table").attr("flag") + "&draftOrderIdStr=" + draftOrderIdStr;
    }
}

/**
 * 获取当前时间
 */

function getCurrentTime() {
    var now = new Date();
    var year = now.getFullYear();
    var month = now.getMonth() + 1;
    var day = now.getDate();
    var hour = now.getHours();
    var minute = now.getMinutes();
    var second = now.getSeconds();
    return year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + second;
}

/**
 * 选定草稿后，初始化施工单页面
 * @param json
 */

function initReapirOrderDraftTable(json) {
    var draftOrderJson = json[0].draftOrderData;
    if(draftOrderJson && draftOrderJson.length > 0) {
        $("#draft_table tr:not(:first)").remove();
        for(var i = 0; i < draftOrderJson.length; i++) {
            var material = draftOrderJson[i].material == null ? "" : draftOrderJson[i].material;
            var serviceContent = draftOrderJson[i].serviceContent == null ? "" : draftOrderJson[i].serviceContent;
            var tr = "<tr class='table-row-original' draftOrderId='" + draftOrderJson[i].idStr + "'><td></td>";
            tr += ("<td style='border-left:none;' class='first-padding'>" + (i + 1) + "</td>");
            tr += ("<td>" + (draftOrderJson[i].receiptNo) + "</td>");
            tr += ("<td>" + (draftOrderJson[i].saveTimeStr == null ? "" : draftOrderJson[i].saveTimeStr) + "</td>");
            tr += ("<td>" + (draftOrderJson[i].customerOrSupplierName == null ? "" : draftOrderJson[i].customerOrSupplierName) + "</td>");
            tr += ("<td>" + (draftOrderJson[i].vehicle == null ? "" : draftOrderJson[i].vehicle) + "</td>");
            tr += ('<td title= \'' + serviceContent + '\'>' + (draftOrderJson[i].serviceContentStr == null ? "" : draftOrderJson[i].serviceContentStr) + "</td>");
            tr += ('<td title= \'' + material + '\' class="last-padding">' + (draftOrderJson[i].materialStr == null ? "" : draftOrderJson[i].materialStr) + "</td>");
            tr += "<td></td></tr>";
            var $tr = $(tr);
            $("#draft_table").append($tr);
            $tr.dblclick(function(event) {
                loadDraftOrder($(this).attr("draftOrderId"));
            });
        }
    }
    draftTableStyleAdjuct();
}

/**
 * 选定草稿后，初始化除施工单以外单据页面
 * @param jsonStr
 */

function initOrderDraftTable(jsonStr) {
    var draftJsonStr = jsonStr[0].draftOrderData;
    if(draftJsonStr && draftJsonStr.length > 0) {
        $("#draft_table tr:not(:first)").remove();
        for(var i = 0; i < draftJsonStr.length; i++) {
            var customerOrSupplierName = draftJsonStr[i].customerOrSupplierName == null ? "" : draftJsonStr[i].customerOrSupplierName;
            var saveTimeStr = draftJsonStr[i].saveTimeStr == null ? "" : draftJsonStr[i].saveTimeStr;
            var material = draftJsonStr[i].material == null ? "" : draftJsonStr[i].material;
            var receiptNo = draftJsonStr[i].receiptNo == null ? "" : draftJsonStr[i].receiptNo;
            var materialStr = draftJsonStr[i].materialStr == null ? "" : draftJsonStr[i].materialStr;
            var tr = "<tr class='table-row-original' draftOrderId='" + draftJsonStr[i].idStr + "'><td></td>";
            tr += '<td style="border-left:none;" class="first-padding">' + (i + 1) + '</td>';
            tr += '<td>' + receiptNo + '</td>';
            tr += '<td>' + saveTimeStr + '</td>';
            tr += '<td>' + customerOrSupplierName + '</td>';
            tr += '<td title= \'' + material + '\' class="last-padding">' + materialStr + '</td>';
            tr += '<td></td></tr>';
            var $tr = $(tr);
            $("#draft_table").append($tr);
            $tr.dblclick(function(event) {
                loadDraftOrder($(this).attr("draftOrderId"));
            });
        }
    }

    draftTableStyleAdjuct();
}

/**
 * 初始化代办事项页面的草稿箱页面
 * @param jsonStr
 */

function initDraftTableBox(jsonStr) {
    $("#draft_table tr:not(:first)").remove();
    var draftJsonStr = jsonStr[0].draftOrderData;
    if(draftJsonStr && draftJsonStr.length > 0) {
        for(var i = 0; i < draftJsonStr.length; i++) {
            var draftOrderIdStr = draftJsonStr[i].idStr == null ? "" : draftJsonStr[i].idStr;
            var receiptNo = draftJsonStr[i].receiptNo == null ? "" : draftJsonStr[i].receiptNo;
            var customerOrSupplierName = draftJsonStr[i].customerOrSupplierName == null ? "" : draftJsonStr[i].customerOrSupplierName;
            var saveTimeStr = draftJsonStr[i].saveTimeStr == null ? "" : draftJsonStr[i].saveTimeStr;
            var userName = draftJsonStr[i].userName == null ? "" : draftJsonStr[i].userName;
            var orderTypeStr = draftJsonStr[i].orderTypeStr == null ? "" : draftJsonStr[i].orderTypeStr;
            var vehicleNo = draftJsonStr[i].vehicle == null ? "" : draftJsonStr[i].vehicle;
            var material = draftJsonStr[i].material == null ? "" : draftJsonStr[i].material;
            var materialStr = draftJsonStr[i].materialStr == null ? "" : draftJsonStr[i].materialStr;
            var serviceContent = draftJsonStr[i].serviceContent == null ? "" : draftJsonStr[i].serviceContent;
            var serviceContentStr = draftJsonStr[i].serviceContentStr == null ? "" : draftJsonStr[i].serviceContentStr;
            var tr = "<tr class='table-row-original' draftOrderId='" + draftOrderIdStr + "'>";
            tr += '<td style="border-left:none;" class="first-padding">' + (i + 1) + '</td>';
            tr += '<td>' + receiptNo + '</td>';
            tr += '<td>' + saveTimeStr + '</td>';
            tr += '<td>' + userName + '</td>';
            tr += '<td>' + orderTypeStr + '</td>';
            tr += '<td>' + customerOrSupplierName + '</td>';
            tr += '<td>' + vehicleNo + '</td>';
            tr += '<td title= \'' + serviceContent + '\'>' + serviceContentStr + '</td>';
            tr += '<td title= \'' + material + '\'>' + materialStr + '</td>';
            tr += '<td class="last-padding"><a href="#" onclick="modifyDraftOrder(\'' + draftOrderIdStr + '\')"><span>编辑</span></a>&nbsp;|&nbsp;<a href="#" onclick="deleteDraftOrder(\'' + draftOrderIdStr + '\')"><span>删除</span></a></td>';
            tr += '</tr >';
            //绑定双击事件
            var $tr = $(tr);
            $("#draft_table").append($tr);
            $tr.dblclick(function(event) {
                loadDraftOrder($(this).attr("draftOrderId"));
            });
        }
    }
    draftTableStyleAdjuct();
}

function initDraftOrderStat(jsonStr) {
    //init orderType number info
    var countOrderTypeList = jsonStr[0].countOrderTypeList;
    $(".orderTyperCountTitle").find("a").html(0);
    var orderType;
    var oderTypeNum;
    var totalNum = 0;
    for(var i = 0; i < countOrderTypeList.length; i++) {
        orderType = countOrderTypeList[i][0];
        oderTypeNum = countOrderTypeList[i][1];
        totalNum += oderTypeNum;
        $("#" + orderType + "NUM").html(oderTypeNum);
    }
    $("#totalNum").html(totalNum);
}

function initALlDraftTableBox(jsonStr) {
    initDraftTableBox(jsonStr);
    initDraftOrderStat(jsonStr)
}
/**
 * 调整draft table样式
 */

function draftTableStyleAdjuct() {
//    tableUtil.tableStyle('#draft_table', '.tab_title,.title');
}

/**
 * 初始化草稿箱弹出页面
 */

function getDraftOrderBox() {
    var url = "draft.do?method=getDraftOrders";
    var orderType = getOrderType();
    APP_BCGOGO.Net.syncPost({
        url: url,
        dataType: "json",
        data: {
            startPageNo: 1,
            orderTypes: orderType
        },
        success: function(json) {
            var dialog_title = "";
            var msg = "";
            if(orderType == "REPAIR") {
                dialog_title = "施工单草稿箱";
                msg = "暂无施工单草稿！";
            } else if(orderType == "PURCHASE") {
                dialog_title = "采购单草稿箱";
                msg = "暂无采购单草稿！";
            } else if(orderType == "INVENTORY") {
                dialog_title = "入库单草稿箱";
                msg = "暂无入库单草稿！";
            } else if(orderType == "SALE") {
                dialog_title = "销售单草稿箱";
                msg = "暂无销售单草稿！";
            } else if(orderType == "RETURN") {
                dialog_title = "入库退货单草稿箱";
                msg = "暂无入库退货单草稿！";
            } else if(orderType == "SALE_RETURN") {
                dialog_title = "销售退货单草稿箱";
                msg = "暂无销售退货单草稿！";
            }
            if(json[0].draftOrderData.length == 0) {
                nsDialog.jAlert(msg);
                return;
            }
            if(orderType == "REPAIR") {
                initReapirOrderDraftTable(json);
                initPages(json, "dynamical1", url, '', "initReapirOrderDraftTable", '', '', {
                    startPageNo: 1,
                    orderTypes: orderType
                }, '');
            } else {
                initOrderDraftTable(json);
                initPages(json, "dynamical1", url, '', "initOrderDraftTable", '', '', {
                    startPageNo: 1,
                    orderTypes: orderType
                }, '');
            }

            //下面是对jQuery dialog组件的改造
            var titleDom = "<div id=\"div_drag_DraftTitle\" class=\"i_note more_title\">" + dialog_title + "(双击打开草稿)" + "</div>";
            $("#draftOrder_dialog").dialog({
                resizable: true,
                title: titleDom,
                height: 400,
                width: 900,
                modal: true,
                closeOnEscape: false
            });
        }
    });
}

function loadDraftOrder(draftOrderIdStr) {
    if($("#draftOrderIdStr").val() == draftOrderIdStr){
        modifyDraftOrder(draftOrderIdStr);
    } else {
        if(openNewOrderPage()) {
            window.open("draft.do?method=getOrderByDraftOrderId&flag=" + $("#draft_table").attr("flag") + "&newOpen=true&draftOrderIdStr=" + draftOrderIdStr, "_blank");
        } else {
            modifyDraftOrder(draftOrderIdStr);
        }
    }

}