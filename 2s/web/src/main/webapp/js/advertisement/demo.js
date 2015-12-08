
$(document).ready(function(){
//    var music=document.getElementById('music');
//    if(music!=null&&$("#iframe_ad",window.parent.document).css("z-index")!=-1){
//        music.play();
//    }
    //window.parent.document.getElementById('iframe_ad').contentWindow.document.getElementById('music').play();
    $(".close").on("click",function(){

//        music.pause();
        $("#iframe_ad",window.parent.document).fadeOut(500);
        $("#iframe_ad_small",window.parent.document).fadeIn(500);
    });

    $(".small").on("click",function(){
        //alert("test click");
//        alert(window.parent.document.getElementById('iframe_ad').contentWindow.document.getElementById('music').getAttribute("class"));
//        window.parent.document.getElementById('iframe_ad').contentWindow.document.getElementById('music').play();
        $("#iframe_ad",window.parent.document).css("z-index",'1000');
        $("#iframe_ad",window.parent.document).css("filter","alpha(opacity=100)");
        $("#iframe_ad",window.parent.document).css("-moz-opacity","1");
        $("#iframe_ad",window.parent.document).css("opacity","1");
        $("#iframe_ad",window.parent.document).fadeIn(500);
        //ad_show();
        $("#iframe_ad_small",window.parent.document).fadeOut(500);
    });

    //$(".wrapper").css("marginTop",($(window).height()-$(".wrapper").height())/2);
//    $(".switchLeft").css("left",($(window).width()-$(".wrapper").width())/2+20);
//    $(".switchRight").css("right",($(window).width()-$(".wrapper").width())/2+20);

    var index=0;
    //var timer=null;
    //var len=$("ul>li").length;
    var $height=$("#pic>li").height();

    $("ol li").click(function(){
        index = $("ol li").index(this);
        switchImage(index);
        switchNumber(index);
    });



    //自动播放
    //$(".wrapper").hover(function(){
    //    clearInterval(timer);
    //},function(){
    //    if(timer){
    //        clearInterval(timer);
    //    }
    //    timer=setInterval(function(){
    //        switchImage(index);
    //        switchNumber(index);
    //        index++;
    //        if(index==len){index=0;}
    //    },3000)
    //}).trigger("mouseout");


    function switchImage(index){
        $(".switchLeft").hide();
        $(".switchRight").hide();
        $("ul").stop(true,false).animate({
            'top' : -$height*index
        },1000,function(){
            $(".switchLeft").show();
            $(".switchRight").show();
        })
    }

    function switchNumber(index){
        $("ol li").removeClass("clicked")
            .eq(index).addClass("clicked");
    }


    //向右切换下一页
    var no_1;
    $(".switchRight").on("click",function(){
        no_1=$(".clicked").html();
        if(no_1==6){
            no_1=0;
        }
        switchImage(no_1);
        switchNumber(no_1);
        index=no_1;
    });
    $("img").on("click",function(){
        no_1=$(".clicked").html();
        if(no_1==6){
            no_1=0;
        }
        switchImage(no_1);
        switchNumber(no_1);
        index=no_1;
    });
    //向左切换上一页
    var no_2;
    $(".switchLeft").on("click",function(){
        no_2=$(".clicked").html()-2;
        if(no_2<0){
            no_2=5;
        }
        switchImage(no_2);
        switchNumber(no_2);
        index=no_2;
    });

    change();

    function change(){
        for(var i=0;i<4;i++){
            //购买日期
            var time=new Date();
            var oYear=time.getFullYear();
            var oMonth=time.getMonth()+1;
            var oDate=time.getDate();
            //购买店铺
            var city= ["苏州", "徐州","扬州", "昆山","无锡","上海", "南京","常州","连云港","镇江"];
            var index1=Math.round(Math.random()*9);
            var store=["汽修厂","汽配厂"];
            var index2=Math.round(Math.random());
            //购买数量
            var num=Math.ceil(Math.random()*9)+1;     //1-10台
            document.getElementsByClassName('num')[i].style.color="red";
            //购买价格
            var money= 850 * num;

            document.getElementsByClassName('time')[i].innerHTML=oYear+'.'+oMonth+'.'+oDate;
            document.getElementsByClassName('store')[i].innerHTML=city[index1]+"XXXXX"+store[index2];
            document.getElementsByClassName('num')[i].innerHTML=num;
            document.getElementsByClassName('money')[i].innerHTML=money;
        }
    }

    setInterval(change,300000);
});


