//TODO service.jsp引用该文件，但service.jsp未见调用
$().ready(function() {
    $("#confirmBtn").click(function() {
        if ($.trim($("#datetime").text()) != '') {
            $("#serviceDateStr").val($("#datetime").text());
        }
        $("form:first").submit();
        $(window.parent.document).find("#mask").hide();
        $(window.parent.document).find("#iframe_PopupBox").hide();
    });

    $("#div_close").click(function() {
        $(window.parent.document).find("#mask").hide();
        $(window.parent.document).find("#iframe_PopupBox").hide();
        try {
            $(window.parent.document.body).find("input[type='button']").eq(0).focus().blur();
        } catch(e) {
            ;
        }
    });
});