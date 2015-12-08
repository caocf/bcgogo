$(document).ready(function() {
    var selects = $("#select");
    var options = $("#option");
    var state = true;
    selects.click(function() {
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
    /* selects.hover(function(){
     $(this).css("background","url(search_xia.png) right 10px no-repeat");
     },
     function(){
     $(this).css("background","url(search_xia.png) right 10px no-repeat");
     });*/
    /* $("li").hover(function(){
     $(this).css("background","#990000").css("color","#ff9900");
     },
     function(){
     $(this).css("background","#820014").css("color","#fff");
     });*/
    $("li").click(function() {
        /*   $(this).css("background","#c00").css("color","#ffffff");*/
        options.css("display", "none");
        selects.children("span").text($(this).attr("tip"));
        $(".valt").val($(this).attr("tip"));
        state = false;
    });
    options.click(function() {
        selects.click(function() {
            return false;
        });
    });
})