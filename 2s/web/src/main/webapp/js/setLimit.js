//弹出添加上下限的iframe
function showSetLimit(flag) {
    var url = "txn.do?method=showSetLimtPage&task=" + flag;
    bcgogo.checksession({"parentWindow":window.parent, 'iframe_PopupBox':$("#iframe_PopupBox_Limit")[0],
        'src':url});
}

function updateLimit() {
    $("#productDTOListForm").attr("action", "txn.do?method=updateLimit");
    var options = {
        success:initLimitSpan,
        type:"post",
        dataType:"json"
    };
    $("#productDTOListForm").ajaxSubmit(options);
}

function initLimitSpan(json) {
    if (json.length > 0) {
        if ($("#upperLimitCount") != null && $("#upperLimitCount") != undefined) {
            $("#upperLimitCount").text(dataTransition.simpleRounding(json[0].currentUpperLimitAmount, 0));

//            if(Number(dataTransition.simpleRounding(json[0].currentUpperLimitAmount, 0))>0){
//                $('#upperLimit_click').css({'color':'red'});
//            }
//            else{
//                $('#upperLimit_click').css({'color':'#fd5300'});
//            }
        }
        if ($("#totalRowsUpperLimit").length > 0) {
            $("#totalRowsUpperLimit").val(dataTransition.simpleRounding(json[0].currentUpperLimitAmount, 0));
        }
        if ($("#lowerLimitCount") != null && $("#lowerLimitCount") != undefined) {
            $("#lowerLimitCount").text(dataTransition.simpleRounding(json[0].currentLowerLimitAmount, 0));

//            if(Number(dataTransition.simpleRounding(json[0].currentLowerLimitAmount, 0))>0){
//                $('#lowerLimit_click').css({'color':'red'});
//            }
//            else{
//                $('#lowerLimit_click').css({'color':'#fd5300'});
//            }
        }
        if ($("#totalRowsLowerLimit").length > 0) {
            $("#totalRowsLowerLimit").val(dataTransition.simpleRounding(json[0].currentLowerLimitAmount, 0));
        }
    }
}

function updateSingleLimit(node) {
    validateLimit(node);
    var productId, lowerLimitVal, upperLimitVal;
    var idprefix = $(node).attr("id");
    idprefix = idprefix.substring(0, idprefix.indexOf("."));
    productId = $("#" + idprefix + "\\.productLocalInfoId").val();
    lowerLimitVal = $("#" + idprefix + "\\.lowerLimit").val();
    upperLimitVal = $("#" + idprefix + "\\.upperLimit").val();
    APP_BCGOGO.Net.syncPost({
        "url":"txn.do?method=updateSingleLimit",
        "data":{
            productId:productId,
            lowerLimitVal:lowerLimitVal,
            upperLimitVal:upperLimitVal
        },
        "cache":false,
        "dataType":"json",
        "success":function (json) {
            initLimitSpan(json);
        }
    });
}
function validateLimit(node) {
    var idprefix = $(node).attr("id");
    var productId, lowerLimitVal, upperLimitVal;
    updateSingleLimitFlag = true;
    if (idprefix && idprefix.indexOf(".") != -1) {
        idprefix = idprefix.substring(0, idprefix.indexOf("."));
        $("#" + idprefix + "\\.lowerLimit").val(dataTransition.simpleRounding($("#" + idprefix + "\\.lowerLimit").val(), 1));
        $("#" + idprefix + "\\.upperLimit").val(dataTransition.simpleRounding($("#" + idprefix + "\\.upperLimit").val(), 1));
        productId = $("#" + idprefix + "\\.productLocalInfoId").val();
        lowerLimitVal = $("#" + idprefix + "\\.lowerLimit").val() * 1;
        upperLimitVal = $("#" + idprefix + "\\.upperLimit").val() * 1;
        if (upperLimitVal < lowerLimitVal && upperLimitVal != 0 && lowerLimitVal != 0) {
            var temp = lowerLimitVal;
            lowerLimitVal = upperLimitVal;
            upperLimitVal = temp;
            $("#" + idprefix + "\\.lowerLimit").val(dataTransition.simpleRounding(lowerLimitVal, 1));
            $("#" + idprefix + "\\.upperLimit").val(dataTransition.simpleRounding(upperLimitVal, 1));
            updateSingleLimitFlag = false;
        } else if (upperLimitVal == lowerLimitVal && upperLimitVal != 0 && lowerLimitVal != 0) {
            upperLimitVal++;
            $("#" + idprefix + "\\.upperLimit").val(dataTransition.simpleRounding(upperLimitVal, 1));
        }
    }
}

$(document).ready(function () {
//           $(".order_input_lowerLimit,.order_input_upperLimit").live("change", function() {
//            validateLimit(this);
//           });
    // 上限和下限值按照现在的需求逻辑， 一定是用户输入的， 如果是用户输入 ，那么就一定会有 blur 事件触发，
    // 在此事件触发是 ， 检测值是否改变 , 但是注意的一点是在重构的时候一定要， 尽量避免使用 live 来绑定事件
    // 应当使用 bind 来帮定， 这一个是出于代码释义来考虑， 一个是基于维护性考虑。
    $(".order_input_lowerLimit,.order_input_upperLimit").live("blur", function () {
        validateLimit(this);
    });

    $(".order_input_lowerLimit,.order_input_upperLimit").each(function () {
        $(this).val(dataTransition.simpleRounding($(this).val(), 1));
    });
});