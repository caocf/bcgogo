/**
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 12-11-3
 * Time: 上午11:38
 * To change this template use File | Settings | File Templates.
 */

$(function() {

      $("#printBtn").bind("click", function() {
        GLOBAL.Util.loadJsCssFile("styles/returnStatPrint.css", "css");
        window.print();
      });

      window.onafterprint = function() {
        GLOBAL.Util.removeJsCssFile("styles/returnStatPrint.css", "css");
      }
      $("#costStatSubmit").click(function() {
        $("#salesStatConditionForm").submit();
        $(this).attr("disabled", true);
      })

    }
)
