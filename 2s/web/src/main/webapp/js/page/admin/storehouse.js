$(document).ready(function () {
    getStoreHouseData();

    $("#addStoreHouse").bind("click",function() {
        $("#storeHouseDialog").dialog({
            width: 310,
            modal: true,
            resizable:false,
            title:"新增仓库",
            beforeclose: function(event, ui) {
                $("#storehouseForm input").not(':button,:submit,:reset').val("");
                $("#storehouseForm textarea").val("");
                return true;
            },
            open: function() {
                return true;
            }
        });
    });

    $("#saveStoreHouseBtn").bind("click",function() {
        if(GLOBAL.Lang.isEmpty($("#name").val())){
            nsDialog.jAlert("仓库名称不能为空!");
            return;
        }
        $("#storehouseForm").ajaxSubmit({
            dataType: "json",
            success:function (data) {
                if(data.success){
                    nsDialog.jAlert("保存成功!");
                    getStoreHouseData();
                    $("#storeHouseDialog").dialog("close");
                }else{
                    nsDialog.jAlert(data.msg);
                }
            },
            error:function () {
                nsDialog.jAlert("保存失败!");
            }
        });
    });

    $("#closeBtn").bind("click", function () {
        $("#storeHouseDialog").dialog("close");
    });
});

function modifyStoreHouse(id) {
    $("#storeHouseDialog").dialog({
        width: 310,
        modal: true,
        title:"修改仓库",
        beforeclose: function(event, ui) {
            $("#storehouseForm input").not(':button,:submit,:reset').val("");
            $("#storehouseForm textarea").val("");
            return true;
        },
        open: function() {
            APP_BCGOGO.Net.syncPost({
                url:"storehouse.do?method=getStoreHouseDTOById",
                data:{id:id},
                dataType:"json",
                success:function (data) {
                    $("#id").val(data.idStr);
                    $("#name").val(data.name);
                    $("#address").val(data.address);
                    $("#memo").val(data.memo);
                },
                error:function () {
                    nsDialog.jAlert("数据异常!");
                }
            });
        }
    });
}
function deleteStoreHouse(id) {
    nsDialog.jConfirm("是否确认删除？", null, function (returnVal) {
        if (returnVal) {
            APP_BCGOGO.Net.syncPost({
                url:"storehouse.do?method=deleteStoreHouseById",
                data:{id:id},
                dataType:"json",
                success:function (data) {
                    if(data.success){
                        nsDialog.jAlert("删除成功!");
                        getStoreHouseData();
                    }else{
                        nsDialog.jAlert(data.msg);
                    }
                },
                error:function () {
                    nsDialog.jAlert("删除失败!");
                }
            });
        }
    });
}


function getStoreHouseData(){
    var paramJson = {startPageNo:1,maxRows:15};
    APP_BCGOGO.Net.syncPost({
        url: "storehouse.do?method=getStoreHouseList",
        data: paramJson,
        dataType: "json",
        success: function(data) {
            drawStoreHouseTable(data);
            initPages(data, "dynamical1", "storehouse.do?method=getStoreHouseList", '', "drawStoreHouseTable", '', '', paramJson, '');
        },
        error:function () {
            $("#storehouseTable tr:not(:first)").remove();
            $("#storeHouseDataCount").text(0);
            nsDialog.jAlert("数据异常!");
        }
    });
}
/**
 * 封装table
 */
function drawStoreHouseTable(data) {
    $("#storehouseTable tr:not(:first)").remove();
    if (data == null || data[0] == null || data[0].storeHouseData == null || data[0].storeHouseData == 0) {
        return;
    }
    $.each(data[0].storeHouseData, function(index, storeHouse) {
        var id = (!storeHouse.idStr ? "" : storeHouse.idStr);
        var name = (!storeHouse.name ? "--" : storeHouse.name);
        var address = (!storeHouse.address ? "--" : storeHouse.address);
        var memo = (!storeHouse.memo ? "--" : storeHouse.memo);
        var tr = '<tr class="table-row-original">';
        tr += '<td style="padding-left:10px;">' + (index + 1) + '</td>';
        tr += '<td title="' + name + '">' + name + '</td>';
        tr += '<td title="' + address + '">' + address + '</td>';
        tr += '<td title="' + memo + '">' + memo + '</td>';
        tr += '<td><a class="clickNum" onclick="modifyStoreHouse(\''+id+'\');">修改</a>&nbsp;' +
                  '<a class="clickNum" onclick="deleteStoreHouse(\''+id+'\');">删除</a></td>';
        tr += '</tr>';
        $("#storehouseTable").append($(tr));
    });
    tableUtil.tableStyle("#storehouseTable",'.divSlip');
    $("#storeHouseDataCount").text(data[0].storeHouseDataCount);
}