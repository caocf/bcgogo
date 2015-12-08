	// JavaScript Document
    
    $(document).ready(
        function(){
		$("#kucun").hide();
        if($.trim($("#debtTag").val())!="yes"){
		    $("#sata_tab").hide();        //交付欠款table
            $(".i_leftBtn").hide();
        }
		$("#tab_three").hide();
		$(".i_bottom").show();       //待交付分页控件

		$(".distance a").click(function(){
			var tab=$(this).parent().next();           //table
			var tashow=$(this).parent().next();        //table
			if(tab.css("display") == "none")
			{
				tab.slideDown();
				$(this).css("background","url('images/rightTop.png') no-repeat right");
				tashow.next().slideDown();            //分页控件
			}
			else
			{
				tab.slideUp();
				$(this).css("background","url('images/rightArrow.png') no-repeat right");
				tashow.next().slideUp();              //分页控件
			}
			
		});            
        }
    );
 