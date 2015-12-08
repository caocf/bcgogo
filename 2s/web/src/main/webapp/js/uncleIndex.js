/**
 * Created by IntelliJ IDEA.
 * User: sl
 * Date: 12-2-13
 * Time: 上午11:04
 * To change this template use File | Settings | File Templates.
 */

var nextPageNo1 = 1;
var isTheLastPage1 = false;
var nextPageNo2 = 1;
var isTheLastPage2 = false;
function initTr1(jsonStr) {
    if(jsonStr!=null){
        $("#customerCount").text(jsonStr[jsonStr.length - 1].totalRows);
        $("#histy tr:not(:first)").remove();
        for (var i = 0; i < jsonStr.length - 1; i++) {
            var customerId = jsonStr[i].customerId == undefined ? " " : jsonStr[i].customerIdString;
            var name = jsonStr[i].name == undefined ? " " : jsonStr[i].name;
            var contact = jsonStr[i].contact == undefined ? " " : jsonStr[i].contact;
            var mobile = jsonStr[i].mobile == undefined ? " " : jsonStr[i].mobile;
            var address = jsonStr[i].address == undefined ? " " : jsonStr[i].address;
            var totalAmount = jsonStr[i].totalAmount == undefined ? " " : jsonStr[i].totalAmount;
            var totalReceivable = jsonStr[i].totalReceivable == undefined ? " " : jsonStr[i].totalReceivable;
            var vehicleCount = jsonStr[i].vehicleCount == undefined ? " " : jsonStr[i].vehicleCount;
            var licenceNo = jsonStr[i].licenceNo == undefined ? " " : jsonStr[i].licenceNo;
            var lastDateStr = jsonStr[i].lastDateStr == undefined ? " " : jsonStr[i].lastDateStr;
            var tr = '<tr class="table-row-original">';
            tr += '<td style="border-left:none;color:black;">' + (i + 1) + '</td>';
            tr += '<td class="blue">' + '<a class="blue_col" href="unitlink.do?method=customer&customerId=' + customerId + '" title="'+name+'">' + name + '</a>' + '</td>';
            tr += '<td class="blue">' + '<a class="blue_col" href="unitlink.do?method=customer&customerId=' + customerId + '">' + contact + '</a>' + '</td>';
//            tr += '<td class="blue photo">' + '<span>' + mobile + '</span><a href="javascript:smsHistory(\'' + mobile + '\')"></a>' + '</td>';
          var smsSendPermission= jQuery("#smsSendPermission").val();
          if(smsSendPermission=="true"){
             tr += '<td class="blue photo">' + '<span>' + mobile + '</span><a href="javascript:smsHistory(\'' + mobile + '\',\'' + customerId + '\',\'' + '' + '\')"></a>' + '</td>';
          } else{
             tr += '<td class="blue photo"><span>' + mobile + '</span></td>';
          }

            tr += '<td style="color:black;">' + address + '</td>';
            tr += '<td class="blue">' + '<a class="blue_col" href="unitlink.do?method=customer&customerId=' + customerId + '">' + totalAmount + '¥</a>' + '</td>';
            tr += '<td class="blue">' + '<a class="blue_col" href="unitlink.do?method=customer&customerId=' + customerId + '">' + totalReceivable + '¥</a>' + '</td>';
            if(APP_BCGOGO.Permission.Version.VehicleConstruction){
                tr += '<td class="blue">' + vehicleCount + '</td>';
                tr += '<td class="blue">' + licenceNo + '</td>';
            }
            tr += '<td class="blue">' + lastDateStr + '</td>';
            tr += '</tr>';
            $("#histy").append(tr);
        }
        tableUtil.tableStyle('#histy','.title_his');
    }
}



function initTr2(jsonStr) {
    $("#supplierCount").text(jsonStr[jsonStr.length - 1].totalRows);
    $("#history tr:not(:first)").remove();
    for (var i = 0; i < jsonStr.length - 1; i++) {
        var supplierId = jsonStr[i].idString == undefined ? " " : jsonStr[i].idString;
        var contact = jsonStr[i].contact == undefined ? " " : jsonStr[i].contact;
        var name = jsonStr[i].name == undefined ? " " : jsonStr[i].name;
        var mobile = jsonStr[i].mobile == undefined ? " " : jsonStr[i].mobile;
        var address = jsonStr[i].address == undefined ? " " : jsonStr[i].address;
        var totalInventoryAmount = jsonStr[i].totalInventoryAmount == undefined?" " : APP_BCGOGO.StringFilter.inputtingPriceFilter(jsonStr[i].totalInventoryAmount, 2);
        var lastOrderTypeDesc = jsonStr[i].lastOrderTypeDesc == undefined ? " " : jsonStr[i].lastOrderTypeDesc;
        var lastOrderProducts = jsonStr[i].lastOrderProducts == undefined ? " " : jsonStr[i].lastOrderProducts;
        var lastOrderTimeStr = jsonStr[i].lastOrderTimeStr == undefined ? " " : jsonStr[i].lastOrderTimeStr;
        var lastOrderId = jsonStr[i].lastOrderId == undefined ? " " : jsonStr[i].lastOrderId;
        var lastOrderLink = jsonStr[i].lastOrderLink == undefined?" " : jsonStr[i].lastOrderLink;

        var tr = '<tr class="table-row-original">';
        tr += '<td style="border-left:none;color:black;">' + (i + 1) + '</td>';
        tr += '<td class="blue">' + '<a class="blue_col" href="unitlink.do?method=supplier&supplierId=' + supplierId + '" title = "'+name+'">' + name + '</a>' + '</td>';
        tr += '<td class="blue">' + '<a class="blue_col" href="unitlink.do?method=supplier&supplierId=' + supplierId + '" title = "'+contact+'">' + contact + '</a>' + '</td>';
        /*tr += '<td class="blue photo">' + '<span>' + mobile + '</span><a href="javascript:smsHistory(\'' + mobile + '\')"></a>' + '</td>';*/
       var smsSendPermission= jQuery("#smsSendPermission").val();
          if(smsSendPermission=="true"){
            tr += '<td class="blue photo">' + '<span>' + mobile + '</span><a href="javascript:smsHistory(\'' + mobile + '\',\'' + '' + '\',\'' + supplierId + '\')"></a>' + '</td>';
          } else{
             tr += '<td class="blue photo"><span>' + mobile + '</span></td>';
          }

        tr += '<td style="color:black;">' + address + '</td>';
        tr += '<td class="blue">' + '<a class="blue_col" href="unitlink.do?method=supplier&supplierId=' + supplierId + '">'+totalInventoryAmount +'</a>' + '</td>';
        tr += '<td class="blue">' + lastOrderTypeDesc + '</td>';
        tr += '<td class="blue" title = "'+ lastOrderProducts +'">'  + lastOrderProducts + '</td>';
//        tr += '<td class="blue">' + '<a href="' + lastOrderLink + '">' + lastOrderProducts + '</a>' + '</td>';
        tr += '<td class="blue">' + lastOrderTimeStr + '</td>';
//        tr += '<td style="border-right:none;" class="blue">' + '<a href="' + lastOrderLink + '">' + lastOrderTimeStr + '</a>' + '</td>';
        tr += '</tr>';
        $("#history").append(tr);
    }
    tableUtil.tableStyle('#history','.title_his');
}