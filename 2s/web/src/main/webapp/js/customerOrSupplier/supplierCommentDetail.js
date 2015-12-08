/**
 * 供应商评价详情专用js
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 13-3-19
 * Time: 上午9:52
 * To change this template use File | Settings | File Templates.
 */




function initSupplierCommentRecord(data) {

  $("#supplierCommentRecordTable tr:not(:first)").remove();

  if (data == null || data[0] == null || data[0].recordDTOList == null || data[0].recordDTOList == 0) {
    $("#noSupplierCommentRecord").css("display", "block");
    return;
  } else {
    $("#noSupplierCommentRecord").css("display", "none");
  }

  $.each(data[0].recordDTOList, function(index, order) {

    var tr = "<tr class='space'><td colspan='5'></td></tr> ";

    var commentTimeStr = (!order.commentTimeStr ? "---" : order.commentTimeStr);
    var commentContent = (!order.commentContent ? "" : order.commentContent);
    var firstCommentContent = (!order.firstCommentContent ? "" : order.firstCommentContent);
    var addCommentContent = (!order.addCommentContent ? "" : order.addCommentContent);
    var customer = (!order.customer ? "---" : order.customer);

    tr += "<tr class='offerBg'>";
    tr += "<td style='padding-left:10px;'>" + (index + 1) + "</td>";
    tr += "<td title='" + commentTimeStr + "'>" + commentTimeStr + "</td>";

     tr += '<td>' +
         '<div class="shopTit" ><label >货品质量</label><a class="bigStar" style="background-position:0px ' + '-'+ order.qualityScoreSpan  + 'px;"' +'></a>&nbsp;<span class="color_yellow">'+ order.qualityScore +'</span>分</div>' +
         '<div class="shopTit" ><label >货品性价比</label><a class="bigStar" style="background-position:0px ' + '-'+ order.performanceScoreSpan  + 'px;"' +'></a>&nbsp;<span class="color_yellow">'+ order.performanceScore +'</span>分</div>' +
         '<div class="shopTit" ><label >发货速度</label><a class="bigStar" style="background-position:0px ' + '-'+ order.speedScoreSpan  + 'px;"' +'></a>&nbsp;<span class="color_yellow">'+ order.speedScore +'</span>分</div>' +
         '<div class="shopTit" ><label >服务态度</label><a class="bigStar" style="background-position:0px ' + '-'+ order.attitudeScoreSpan  + 'px;"' +'></a>&nbsp;<span class="color_yellow">'+ order.attitudeScore +'</span>分</div>' +
         '</td>';

    if (addCommentContent != "") {
      tr += "<td style='word-break: break-all; word-wrap:break-word;' title='" + firstCommentContent +addCommentContent + "'>" + firstCommentContent + "<div class='addWord gray_color'> " + addCommentContent + "</div>" + "</td>";
    } else {
      tr += "<td style='word-break: break-all; word-wrap:break-word;' title='" + firstCommentContent + "'>" + firstCommentContent + "</td>";
    }
    tr += "<td title='" + customer + "'>" + customer + "</td>";
    tr += "</tr>";
    $("#supplierCommentRecordTable").append($(tr));
  });
}