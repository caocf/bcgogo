// JavaScript Document
$(document).ready(
    function(){
      $("#tab_four tr:gt(0)").hide();
      $("#kucun tr:gt(0)").hide();
      $("#sata_tab tr:gt(0)").hide();
      $("#tab_three tr:gt(0)").hide();
      $("#qiankuan tr:gt(0)").hide();
      $(".i_leftBtn").hide();
      $(".hidePageAJAX").hide();

      $(".distance .J_more_or_less,.distance1 .J_more_or_less").click(function() {
        //下一个元素是表格，取得其全部行元素
//        var tab = $(this).parent().next().find("tr:gt(0)");
        var tab = $(this).parent().next().find("tr");
        //获取表格
        var tashow = $(this).parent().next();
        //表格内容展开或者隐藏
        if (tab.css("display") == "none" || tashow.next().is(":visible") == false) {
          if(tashow.find("tr").not(":first").size()==0){
              return;
          }
          tab.show();
          $(this).css("background", "url('images/rightTop.png') no-repeat right");
          tashow.next().show();
          $( $(".distance,.distance1").next().next() ).find(".i_pageBtn,.onlin_his,.nextPage").show();
        } else {
          tab.hide();
          $(this).css("background", "url('images/rightArrow.png') no-repeat right");
          tashow.next().hide();
        }
      });
    }
);



