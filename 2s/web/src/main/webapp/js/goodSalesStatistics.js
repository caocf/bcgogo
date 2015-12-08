/**
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 12-10-30
 * Time: 下午8:28
 * To change this template use File | Settings | File Templates.
 */

$(function() {

  $("#printBtn").bind("click", function() {
    GLOBAL.Util.loadJsCssFile("styles/goodSaleStatPrint.css", "css");
    window.print();
  });

  window.onafterprint = function() {
    GLOBAL.Util.removeJsCssFile("styles/goodSaleStatPrint.css", "css");
  }


  jQuery("#goodSales").click(function() {
    window.location.href = "salesStat.do?method=getGoodSaleCost";
  });

  jQuery("#badSales").click(function() {
    window.location.href = "salesStat.do?method=getBadSaleCost";
  });


  $("#costStatSubmit").click(function() {
    $("#salesStatConditionForm").submit();
    $(this).attr("disabled", true);
  })

})