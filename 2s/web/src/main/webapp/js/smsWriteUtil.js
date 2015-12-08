
$().ready(function(){
    if($("#fromPage").val()=="sendPromotionsMsg"){
        var liStr="";
        $(".pCustomer").each(function(){
            var mobile=$(this).attr("mobile");
            var customerName=$(this).attr("customerName");
            liStr+='<li class="bcgogo-customerSmsInput-option">';
            liStr+='<p>'+customerName+' : '+mobile+'</p>';
            liStr+='<input type="hidden" class="promotions_mobile" value="'+mobile+'" />';
            liStr+='<span class="close_icon">×</span></li>';
        });
        if(!G.isEmpty(liStr)){
            $(".bcgogo-customerSmsInput-optionContainer").append(liStr);
            $(".bcgogo-customerSmsInput-optionContainer").css("visibility","visible");
            $(".bcgogo-customerSmsInput-optionContainer .close_icon").bind("click",function(){
                $(this).closest("li").remove();
            });
        }


    }
});