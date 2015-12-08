$(document).ready(function () {
    $("#startDate,#endDate")
        .datepicker({
            "numberOfMonths": 1,
            "showButtonPanel": true,
            "changeYear": true,
            "showHour": false,
            "showMinute": false,
            "changeMonth": true,
            "yearRange": "c-100:c+100",
            "yearSuffix": ""
        })
        .blur(function () {
            var startDate = $("#startDate").val();
            var endDate = $("#endDate").val();
            if (startDate == "" || endDate == "") return;
            if (APP_BCGOGO.Validator.stringIsZhCn(startDate) && APP_BCGOGO.Validator.stringIsZhCn(endDate)) {
                return;
            } else {
                if (startDate > endDate) {
                    $("#endDate").val(startDate);
                    $("#startDate").val(endDate);
                }
            }
        })
        .bind("click", function () {
            $(this).blur();
        })
        .change(function () {
            var startDate = $("#startDate").val();
            var endDate = $("#endDate").val();
            $(".good_his > .today_list").removeClass("hoverList");
            if (endDate == "" || startDate == "") {
                return;
            }
            if (APP_BCGOGO.Validator.stringIsZhCn(startDate) && APP_BCGOGO.Validator.stringIsZhCn(endDate)) {
                return;
            } else {
                if (startDate > endDate) {
                    $("#endDate").val(startDate);
                    $("#startDate").val(endDate);
                }
            }
        });

    $("#editor").bind('click',function () {
        getSaleManSuggestion($(this));
    }).bind('keyup', function () {
        $("#editorId").val('');
        getSaleManSuggestion($(this));
    });

    $("#addAllocateRecordBtn").bind("click", function () {
        window.location = "allocateRecord.do?method=createAllocateRecord";
    });

    $("#allocateRecordSearchForm").submit(function (e) {
        e.preventDefault();
        var param = $("#allocateRecordSearchForm").serializeArray();
        var paramJson = {};
        $.each(param, function (index, val) {
            paramJson[val.name] = val.value;
        });

        $(".j_sort").each(function () {
            if (this.id=="sortVestDate") {
                $(this).addClass("descending").removeClass("ascending");
            }else{
                $(this).addClass("ascending").removeClass("descending");
            }
        });

        $("#allocateRecordSearchForm").ajaxSubmit({
            dataType: "json",
            success: function (data) {
                drawAllocateRecordTable(data);
                initPages(data, "dynamical1", "allocateRecord.do?method=getAllocateRecordList", '', "drawAllocateRecordTable", '', '', paramJson, '');
            },
            error: function () {
                $("#allocateRecordDataCount").text("0");
                $("#allocateRecordTable tr").not('.table-row-title').remove();
                nsDialog.jAlert("数据异常!");
            }
        });
    });
});


function getSaleManSuggestion($domObject) {
    var searchWord = $domObject.val().replace(/[\ |\\]/g, "");
    var droplist = APP_BCGOGO.Module.droplist;
    droplist.setUUID(GLOBAL.Util.generateUUID());
    var ajaxData = {
        keyword: searchWord,
        uuid: droplist.getUUID()
    };
    var ajaxUrl = "searchInventoryIndex.do?method=getSaleManSuggestion";
    APP_BCGOGO.wjl.LazySearcher.lazySearch(ajaxUrl, ajaxData, function (result) {
        droplist.show({
            "selector": $domObject,
            "data": result,
            "onSelect": function (event, index, data) {
                $domObject.val(data.label);
                $("#editorId").val(data.details.id);
                droplist.hide();
            }
        });
    });
}

function showAllocateRecord(id){
    window.open("allocateRecord.do?method=showAllocateRecordByAllocateRecordId&allocateRecordId=" +id);
}
/**
 * 封装table
 */
function drawAllocateRecordTable(data) {
    $("#allocateRecordDataCount").text("0");
    $("#allocateRecordTable tr").not('.table-row-title').remove();
    if (data == null || data[0] == null || data[0].allocateRecordData == null || data[0].allocateRecordData == 0) {
        return;
    }
    $.each(data[0].allocateRecordData, function (index, allocateRecord) {
        var id = (!allocateRecord.idStr ? "" : allocateRecord.idStr);
        var receiptNo = (!allocateRecord.receiptNo ? "--" : allocateRecord.receiptNo);
        var outStorehouseName = (!allocateRecord.outStorehouseName ? "--" : allocateRecord.outStorehouseName);
        var inStorehouseName = (!allocateRecord.inStorehouseName ? "--" : allocateRecord.inStorehouseName);
        var totalAmount = (!allocateRecord.totalAmount ? "--" : allocateRecord.totalAmount);
        var totalCostPrice = (!allocateRecord.totalCostPrice ? "--" : allocateRecord.totalCostPrice);
        var editor = (!allocateRecord.editor ? "--" : allocateRecord.editor);
        var vestDateStr = (!allocateRecord.vestDateStr ? "--" : allocateRecord.vestDateStr);
        var createType = (!allocateRecord.originOrderId?"自主生成" : "系统生成");
        var tr = '<tr class="table-row-original">';
        tr += '<td class="first-padding">' + (index + 1) + '</td>';
        tr += '<td title="' + receiptNo + '"><a class="blue_col" onclick="showAllocateRecord(\'' + id + '\')">' + receiptNo + '</a></td>';
        tr += '<td title="' + outStorehouseName + '">' + outStorehouseName + '</td>';
        tr += '<td title="' + inStorehouseName + '">' + inStorehouseName + '</td>';
        tr += '<td title="' + totalAmount + '">' + totalAmount + '</td>';
        tr += '<td title="' + totalCostPrice + '">' + totalCostPrice + '</td>';
        tr += '<td title="' + editor + '">' + editor + '</td>';
        tr += '<td title="' + vestDateStr + '">' + vestDateStr + '</td>';
        tr += '<td title="' + createType + '" class="last-padding">' + createType + '</td>';
        tr += '</tr>';
        $("#allocateRecordTable").append(tr);
        
    });
    tableUtil.tableStyle("#allocateRecordTable", '.title_tb');
    $("#allocateRecordDataCount").text(data[0].allocateRecordDataCount);
}
