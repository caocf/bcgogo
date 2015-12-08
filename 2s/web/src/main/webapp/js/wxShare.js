

$(".container").css( "width" , innerWidth);
    $(".container").css( "height" , innerHeight - 45);
    $("div.progressbar").css("width",innerWidth/6);

//    jQuery.ajax({
//        type:"POST",
//        url: " ",
//        dataType:"json",
//        success: function(responseStr){
//            init4sShopInfo(responseStr);
//            init4sShopMap(responseStr);
//        }
//    });

    function init4sShopInfo(responseStr){

        $("div.wrap6 div.tel span").text(responseStr.telNo);
        $("div.wrap6 div.qq span").text(responseStr.qq);
        $("div.wrap6 div.addr").text(responseStr.addr);
    }

    function init4sShopMap(responseStr){

         var map = new BMap.Map("showMap");
    //     var point = new BMap.Point(116.331398,39.897445);
    //     map.centerAndZoom(point,15);
         var myGeo = new BMap.Geocoder();
         if(responseStr.addr){
             var detail = responseStr.addr;
             var city = detail.substring(0,2);
             myGeo.getPoint(detail, function(point){
                 if (point) {
                     map.centerAndZoom(point, 16);
                     map.addOverlay(new BMap.Marker(point));
                     marker.setAnimation(BMAP_ANIMATION_BOUNCE);
                 }
             }, city);
         }else{
             $("#showMap").hide();
         }
    }


    var curWrap = 1;
    $(".box").on("swiperight",function(e){
        e.stopPropagation();
        curWrap--;
        if( curWrap <=0){
            curWrap = 6;
        };
        if( curWrap == 6){
            $("#showMap").css("opacity","1").addClass("mapLaunch");
        }else{
            $("#showMap").css("opacity","0").removeClass("mapLaunch");
        };
        $(".box").hide();
        $("div.progressbar").css("width",innerWidth/6*curWrap);
        $("div.footer span.curPage").text(curWrap);
        $(".box").children().css("opacity","0");
        $(".wrap"+curWrap).slideDown(1000,function(){
            $(".box").children().css("opacity","1");
        });
    });
    $(".box").on("swipeleft",function(e){
        e.stopPropagation();
        curWrap++;
        if( curWrap >=7){
            curWrap = 1;
        };
        if( curWrap == 6){
            $("#showMap").css("opacity","1").addClass("mapLaunch");
        }else{
            $("#showMap").css("opacity","0").removeClass("mapLaunch");
        };
        $(".box").hide();
        $("div.progressbar").css("width",innerWidth/6*curWrap);
        $("div.footer span.curPage").text(curWrap);
        $(".box").children().css("opacity","0");
        $(".wrap"+curWrap).slideDown(1000,function(){
            $(".box").children().css("opacity","1");
        });
    });
    $(".box").on("swipeup",function(e){
        e.stopPropagation();
        curWrap++;
        if( curWrap >=7){
            curWrap = 1;
        };
        if( curWrap == 6){
            $("#showMap").css("opacity","1").addClass("mapLaunch");
        }else{
            $("#showMap").css("opacity","0").removeClass("mapLaunch");
        };
        $(".box").hide();
        $("div.progressbar").css("width",innerWidth/6*curWrap);
        $("div.footer span.curPage").text(curWrap);
        $(".box").children().css("opacity","0");
        $(".wrap"+curWrap).slideDown(1000,function(){
            $(".box").children().css("opacity","1");
        });
    });
    $(".box").on("swipedown",function(e){
        e.stopPropagation();
        curWrap--;
        if( curWrap <=0){
            curWrap = 6;
        };
        if( curWrap == 6){
            $("#showMap").css("opacity","1").addClass("mapLaunch");
        }else{
            $("#showMap").css("opacity","0").removeClass("mapLaunch");
        };
        $(".box").hide();
        $("div.progressbar").css("width",innerWidth/6*curWrap);
        $("div.footer span.curPage").text(curWrap);
        $(".box").children().css("opacity","0");
        $(".wrap"+curWrap).slideDown(1000,function(){
            $(".box").children().css("opacity","1");
        });
    });


    // �ٶȵ�ͼAPI����
    var map = new BMap.Map("showMap");
    //��ͼĬ����ʾΪ����
    var point = new BMap.Point(116.331398,39.897445);
    map.centerAndZoom(point,15);
    // ������ַ������ʵ��
    var myGeo = new BMap.Geocoder();
    // ����ַ���������ʾ�ڵ�ͼ��,��������ͼ��Ұ
    var detail = "苏州市工业园区东平街汉嘉大厦5010室";
//    var detail = "苏州市工业园区";
    myGeo.getPoint(detail, function(point){
        if (point) {
            map.centerAndZoom(point, 15);
            var marker = new BMap.Marker(point);
            map.addOverlay(marker);
//            map.addOverlay(new BMap.Marker(map.getCenter()));
            marker.setAnimation(BMAP_ANIMATION_BOUNCE);
        }
    }, "苏州");
