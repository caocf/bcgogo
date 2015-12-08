/**
 * @author ??
 * @adjuster 潘震
 * @修改时间 2012-07-03
 */
$().ready(function() {
    if ($("#huankuanTime", parent.document).length > 0) {
        if (!$("#datetime").val()) {
            $("#datetime").val( $("#huankuanTime", parent.document).val() );
        }
    }
    $("#confirmBtn").bind("click", function() {
        if ($.trim($("#datetime").val()) != '') {
            var orderVestDate = window.parent.document.getElementById("orderVestDate");
            if (orderVestDate) {
                // 归属时间 与 设置还款时间
                if (GLOBAL.Util.getDate(orderVestDate.value).getTime() - GLOBAL.Util.getDate($("#datetime").val()).getTime() > 0) {
                    alert("请选择销售日期以后的日期。");
                    return;
                }
            } else {
                // 今天时间 与 设置还款时间
                var myDate = GLOBAL.Date.getCurrentFormatDate();
                if (myDate > $("#datetime").val()) {
                    alert("请选择今天及以后的日期。");
                    return;
                }
            }
            $("#mask", parent.document).css("display", "none");
            if ($("#iframe_PopupBoxMakeTime", parent.document).length > 0) {
                $("#iframe_PopupBoxMakeTime", parent.document).css("display", "none");
            }
            //将是否设置还款时间标识为1
            $("#isMakeTime", parent.document).val("1");
            if ($("#huankuanTime", parent.document).length > 0) {
                $("#huankuanTime", parent.document).val($("#datetime").val());
            } // 商品销售单，保存还款时间到隐藏域，在提交的时候一起设置。
        }
    });
});


