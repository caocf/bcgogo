$(document).ready(function () {
    var selects = $("#select"),
        options = $("#option"),
        state = true;

    selects.click(function () {
        if (state) {
            if (!($(this).is(":animated"))) {
                options.slideDown();
            } else {
                options.css("display", "none");
            }
            state = false;
        } else {
            if (!($(this).is(":animated"))) {
                options.slideUp();
            } else {
                $(this).stop(true, true);
                options.css("display", "");
            }
            state = true;
        }
    });

    $("li").click(function () {
        /*   $(this).css("background","#c00").css("color","#ffffff");*/
        options.css("display", "none");
        selects.children("span").text($(this).attr("tip"));
        $(".valt").val($(this).attr("tip"));
        state = false;
    });
    options.click(function () {
        selects.click(function () {
            return false;
        });
    });
})