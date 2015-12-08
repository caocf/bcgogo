/**
 * 页面初始化
 *
 * @param jsonStr
 */
function initSensitiveWord(jsonStr) {
    $("#table_smsFailedJob tr:not(:first)").remove();
    if (jsonStr.length > 0) {
        for (var i = 0; i < jsonStr.length - 1; i++) {
            var word = jsonStr[i] == null ? " " : jsonStr[i];
            var tr = '<tr>';
            tr += '<td style="border-left:none;">' + (i + 1) + '&nbsp;</td>';
            tr += '<td title= \'' + word + '\'>' + word + '</td>';
            tr += '<td><a class="config_modify" href="#"  onclick="editWord(\'' + word + '\')">编辑</a> &nbsp; &nbsp;' +
                '<a class="config_modify" href="#"  onclick="deleteWord(\'' + word + '\')">删除</a></td>';
            tr += '</tr >';
            $("#table_smsFailedJob").append($(tr));
        }
    } else {
        $("#page").css("display", "none");
    }
}

/**
 * 短信发送失败修改 重新发送
 *
 * @param type
 * @param content
 * @param shop_id
 */
function editWord(oldWord) {
    Mask.Login();
    $("#setLocation").css("display", "block");
    $("#oldWord").val(oldWord);
}
/**
 * 删除敏感词
 *
 * @param word
 */
function deleteWord(word) {
    if (confirm("确认删除！")) {
        window.location.href = "sms.do?method=deleteSensitiveWord&word=" + word;
    }
}
/**
 * 查询敏感词
 *
 */
function searchSensitiveWord() {
    var searchWord = $("#searchWord").val();
    $.ajax({
        type:"POST",
        url:"sms.do?method=getSensitiveWords",
        data:{word:searchWord,startPageNo:1},
        cache:false,
        dataType:"json",
        success:function(jsonStr) {
            initSensitiveWord(jsonStr);
            initfenye(jsonStr, "dynamical1", "sms.do?method=getSensitiveWords", '', "initSensitiveWord", '', '',
                {startPageNo:1,word:searchWord}, '');
        }
    });
}
/**
 * 弹出狂遮罩，关闭按钮操作
 */
$(function() {
    $("#div_show").draggable();
    $("#div_close,#cancleBtn").click(function() {
        document.getElementById("mask").style.display = "none";
        $("#setLocation").css("display", "none");
    });
    /*
     *敏感词重复验证
     */
    $("#add").click(function() {
        var word = $("#word").val();
        if ($.trim(word) == "") return false;
        $.ajax({
            type:"POST",
            url:"sms.do?method=validateAddSensitiveWord",
            data:{word:word},
            cache:false,
            dataType:"json",
            success:function(jsonStr) {
                if (jsonStr.result == "success") {
                    document.getElementById("addSensitiveWord").submit();
                } else {
                    alert('敏感词重复！请重新输入！');
                }
            }
        });
    });

    $("#clear").click(function() {
        if (!confirm("确定清空？")) {
            return;
        }
//        $.post("sms.do?method=clearSensitiveWord");
        window.location.href = "sms.do?method=clearSensitiveWord";
//        window.location.href = "sms.do?method=initSensitiveWordPage";
    });


    /**
     * 修改敏感词验证
     */
    $("#confirmBtn").click(function() {
        var word = $("#newWord").val();
        if ($.trim(word) == "") return false;
        $.ajax({
            type:"POST",
            url:"sms.do?method=validateAddSensitiveWord",
            data:{word:word},
            cache:false,
            dataType:"json",
            success:function(jsonStr) {
                if (jsonStr.result == "success") {
                    document.getElementById("editSensitiveWord").submit();
                } else {
                    alert('敏感词重复！请重新输入！');
                }
            }
        });
    });

});

