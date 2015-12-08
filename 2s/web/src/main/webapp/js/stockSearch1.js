//待入库
var nextPageNo3 = 1;
var isTheLastPage3 = false;
//jQuery(document).ready(function() {
//    jQuery.ajax({
//        type:"POST",
//        url:jQuery("#basePath").val() + "stockSearch.do?method=waitcoming",
//        data:{startPageNo:nextPageNo3,maxRows:5},
//        cache:false,
//        dataType:"json",
//        success:function(jsonStr) {
//            initTr3(jsonStr);
//            if (isTheLastPage3 == true && nextPageNo3 == 1) {
//                jQuery("#pageNo_id3>div:eq(1)").css('display', 'none');
//            }
//            if (isTheLastPage3 == true) {
//                jQuery("#pageNo_id3>div:last").css('display', 'none');
//            }
//            if (nextPageNo3 == 1) {
//                jQuery("#pageNo_id3>div:first").css('display', 'none');
//            }
//        }
//    });
//});
//jQuery("#pageNo_id3>div").live("click", function() {
//
//    jQuery("#sata_tab tr:not(:first)").remove();
//    var selectItem = jQuery(this).html();
//    if (selectItem == "上一页") {
//        if (nextPageNo3 > 1) {
//            nextPageNo3 = nextPageNo3 - 1;
//            jQuery("#thisPageNo3").html(nextPageNo3);
//            jQuery("#pageNo_id3>div:last").css('display', 'block');
//            if (nextPageNo3 == 1) {
//                jQuery(this).css('display', 'none');
//            }
//        }
//    } else if (selectItem == "下一页") {
//        if (!isTheLastPage3) {
//            nextPageNo3 = nextPageNo3 + 1;
//            jQuery("#thisPageNo3").html(nextPageNo3);
//            jQuery("#pageNo_id3>div:first").css('display', 'block');
//        }
//    }
//    jQuery.ajax({
//        type:"POST",
//        url:jQuery("#basePath").val() + "stockSearch.do?method=waitcoming",
//        data:{startPageNo:nextPageNo3,maxRows:5},
//        cache:false,
//        dataType:"json",
//        success:function(jsonStr) {
//          initTr3(jsonStr);
//            if (jsonStr[jsonStr.length - 1].isTheLastPage3 == "true") {
//                jQuery("#pageNo_id3>div:last").css('display', 'none');
//            }
//        }
//    });
//});
function initTr3(jsonStr) {
    jQuery("#sata_tab tr:not(:first)").remove();
    for (var i = 0; i < jsonStr.length - 1; i++) {
//        if (jsonStr[jsonStr.length - 1].isTheLastPage3 == "true") {
//            isTheLastPage3 = true;
//        } else {
//            isTheLastPage3 = false;
//        }
        if (jsonStr[i] != null) {
            var tr = '<tr>';
            tr += '<td style="border-left:none;">' + (i + 1) + '</td>';
            tr += '<td>' + jsonStr[i].supplier + '</td>';
            tr += '<td><a href="storage.do?method=getProducts&type=txn&purchaseOrderId=' + jsonStr[i].purchaseOrderId + '">' + jsonStr[i].productName + ' </a></td>';
            tr += '<td>' + jsonStr[i].productBrand + '</td>';
            tr += '<td>' + jsonStr[i].productSpec+ '</td>';
            tr += '<td>' + jsonStr[i].productModel + '</td>';
            tr += '<td>' + jsonStr[i].price + '</td>';
            tr += '<td>' + jsonStr[i].amount + '</td>';
            tr += '<td>' + jsonStr[i].unit + '</td>';
            tr += '<td style="border-right:none;" class="txt_right">' + jsonStr[i].price *  jsonStr[i].amount+'</td>';
            tr += '</tr>';
            jQuery("#sata_tab").append(tr);
        }
    }
}